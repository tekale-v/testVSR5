package com.pg.dsm.support_tools.support_actions.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.support_tools.support_actions.util.RollupType;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportConstants;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;

import matrix.db.Context;
import matrix.util.StringList;

public class MarkRollup extends SupportAction {

	Context _context;
	String _supportAction;
	Map<String, String> _infoMap;

	public MarkRollup(Context _context, String _supportAction, Map<String, String> _infoMap) {
		this._context = _context;
		this._supportAction = _supportAction;
		this._infoMap = _infoMap;
	}
	@Override
	public boolean checkState() throws Exception {
		return SupportUtil.isStateRelease(_infoMap.get(SELECT_CURRENT));
	}
	@Override
	public boolean hasAccess() throws Exception {
		return hasReadAccess();
	}
	public boolean hasReadAccess() throws Exception {
		return SupportUtil.hasAccess(_context, _infoMap.get(SELECT_ID), ACCESS_READ, ACCESS_SHOW);
	}
	@Override
	public boolean isQualified() throws Exception {
		return isTypeQualified();
	}
	@Override
	public String execute() throws Exception {
		String ret = UNSUPPORTED_ACTION;
		try {
			System.out.println("Inside Mark Rollup - execute:"+_supportAction);
			String args[] = { (String)_infoMap.get(SELECT_ID), (String)_infoMap.get(SELECT_TYPE), (String)_infoMap.get(SUPPORT_ACTION_SUB_OPTIONS) ,_supportAction};
			int result = markRollup(_context, args);
			if(result == 0) {
				ret = SUPPORT_ACTION_SUCCESS;
			}
		} catch(Exception e) {
			ret = SupportType.MARK_ROLLUP.getException();
			e.printStackTrace();
		}
		return ret;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(MARK_ROLLUP);
		builder.append(SYMB_SQUARE_BRACKET_OPEN);
		builder.append(_context);
		builder.append(SYMBOL_COMMA);
		builder.append(_supportAction);
		builder.append(SYMBOL_COMMA);
		builder.append(_infoMap);
		builder.append(SYMB_SQUARE_BRACKET_CLOSE);
		return builder.toString();
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to record the history when the flagging is performed.  
	 * @return void 
	 * @throws Exception
	 */
	@Override
	public void recordHistory() throws Exception {
		try {
			MqlUtil.mqlCommand(_context,"modify bus $1 add history $2 comment $3", _infoMap.get(SELECT_ID), PARAM_CUSTOM, SupportType.MARK_ROLLUP.getComment().concat(_infoMap.get(SUPPORT_CONTEXT_USER))); 
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to check if given object is supported for mark Roll-up
	 * @return boolean - true/false
	 * @throws Exception
	 * Modified for Defect #40400
	 */
	public boolean isTypeQualified() throws Exception {
		return SupportUtil.getTypes(SupportUtil.getValue(_infoMap, SUPPORT_ACTION_TYPE)).contains(SupportUtil.getTypeSymbolicName(_context, SupportUtil.getValue(_infoMap, SELECT_TYPE)));
	}

	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Method to perform mark Roll-up 
	 * @return int - 
	 * @throws Exception
	 */
	public int markRollup(Context context,String[] args) throws Exception {
		int ret = 1;
		boolean isContextPushed = false;
		String sEvents = EMPTY_STRING;
		StringBuilder eventBuf = new StringBuilder();
		try {
			
			if(null != args && args.length>0){				
				String objectId = args[0];
				String objectType = args[1];
				String rollTypes = args[2];
				String supportAction = args[3];
				
				if(SupportUtil.isNotNullEmpty(rollTypes)) {
					rollTypes = rollTypes.substring(1);
					rollTypes = rollTypes.substring(0,rollTypes.length()-1);
					rollTypes = rollTypes.replaceAll(BLANK_SPACE, NO_SPACE);
				}
				StringList listRollups = FrameworkUtil.split(rollTypes, SYMBOL_COMMA);
				listRollups.removeAll(Arrays.asList(null, ""));

				if(SupportUtil.isNotNullEmpty(objectId) && SupportUtil.isNotNullEmpty(rollTypes) && TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(objectType)) {
					Map<String, String> tempMap = new HashMap<String, String>();
					DomainObject busObj = DomainObject.newInstance(context);
					busObj.setId(objectId);
					Map infoMap = (Map)busObj.getInfo(context, ROLLUP_BUS_SELECTS);
					if(infoMap != null) {

						String currentEvent = (String)infoMap.get(SELECT_ATTRIBUTE_EVENT_FOR_ROLLUP);
						String assemblyType = (String)infoMap.get(SELECT_ATTRIBUTE_ASSEMBLY_TYPE);
						String eachEventName = EMPTY_STRING;
						String eachRollUp = EMPTY_STRING;
						Iterator itr = listRollups.iterator();
						while(itr.hasNext()) {
							eachRollUp = (String)itr.next();
							eachEventName = RollupType.valueOf(eachRollUp.trim()).getEventName();
							if((SupportUtil.isNotNullEmpty(assemblyType) && SHIPPABLE_HALB.equalsIgnoreCase(assemblyType)) && (ROLLUP_EXLCUDE_TYPES.contains(eachRollUp.trim())))
								continue;
							if(SupportUtil.isNotNullEmpty(currentEvent) && currentEvent.contains(eachEventName))
								continue;
							else {
								if(eventBuf.length()>0 || SupportUtil.isNotNullEmpty(currentEvent)) {
									eventBuf.append(SYMBOL_COMMA);
								}
								eventBuf.append(eachEventName);
							}
						}
						sEvents = currentEvent+eventBuf.toString();
						
						if(!CONST_TRUE.equalsIgnoreCase((String)infoMap.get(SELECT_ATTRIBUTE_MARK_FOR_ROLLUP)) && (SupportUtil.isNotNullEmpty(sEvents))){
							tempMap.put(ATTRIBUTE_PGMARKFORROLLUP, CONST_TRUE);	
						}
						if(SupportUtil.isNotNullEmpty(sEvents))
							tempMap.put(ATTRIBUTE_PGEVENTFORROLLUP, sEvents);

						ContextUtil.pushContext(context, PERSON_USER_AGENT, null, context.getVault().getName());
						isContextPushed = true;
						busObj.setAttributeValues(context, tempMap);
						ret = 0;
					}
				}else{
					DomainObject busObj = DomainObject.newInstance(context);
					busObj.setId(objectId);
					//Push context is used as the user not have access to update attribute value
					ContextUtil.pushContext(context, PERSON_USER_AGENT, null, context.getVault().getName());
					isContextPushed = true;
					busObj.setAttributeValue(context,ATTRIBUTE_PGREGISTRATIONROLLUPFLAG,"True");
					ret = 0;
				}
			}
		} catch(Exception e){
			ret = 1;
			e.printStackTrace();
			throw e;
		} finally {
			if(isContextPushed){
				ContextUtil.popContext(context);
				isContextPushed= false;
			}
		}
		return ret;
	}
}
