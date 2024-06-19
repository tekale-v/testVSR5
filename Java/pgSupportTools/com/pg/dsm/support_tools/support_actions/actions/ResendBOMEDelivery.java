package com.pg.dsm.support_tools.support_actions.actions;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;
import com.pg.dsm.sapview.utils.MarkUtils;

import matrix.db.Context;
import matrix.db.JPO;

public class ResendBOMEDelivery extends SupportAction {

	Context _context;
	String _supportAction;
	Map<String, String> _infoMap;
	private transient final Logger logger = Logger.getLogger(this.getClass().getName());
	
	public ResendBOMEDelivery(Context _context, String _supportAction, Map<String, String> _infoMap) {
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
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to Resend bom to SAP. 
	 * @return String
	 * @throws Exception
	 * Modified by DSM in 2018x.6 Jan CW for Defect 45659
	 */
	@Override
	public String execute() throws Exception {
		String ret = UNSUPPORTED_ACTION;
		try {
			System.out.println("Inside Resend BOM eDelivery - execute:"+_supportAction);
			String[] args = new String[] { (String)_infoMap.get(SELECT_ID)};
			//String[] args = new String[] { (String)_infoMap.get(SELECT_ID), (String)_infoMap.get(SELECT_TYPE), (String)_infoMap.get(SELECT_NAME), (String)_infoMap.get(SELECT_REVISION), EMPTY_STRING, EMPTY_STRING};
			//JPO.invoke(_context, SupportType.RESEND_BOM_EDELIVERY.getJPO(), null, SupportType.RESEND_BOM_EDELIVERY.getMethod(), args, String.class);
			int result = markForBOMeDelivery(_context, args);
			if(result == 0) {
				ret = SUPPORT_ACTION_SUCCESS;
			}
			
		} catch(Exception e) {
			ret = SupportType.RESEND_BOM_EDELIVERY.getException();
			e.printStackTrace();
		}
		return ret;
	}
	
	/**DSM 2018x.6 Jan CW for Defect 45659 - On Demand Support Tools 
	 * @about - Helper method to Resend bom to SAP. 
	 * @return String
	 * @throws FrameworkException 
	 */
	public int markForBOMeDelivery(Context context, String[] args) throws FrameworkException {
		int ret = 1;
		boolean isContextPushed = false;
		try {		
			if(null != args && args.length>0){
				String objectId = args[0];
				if(SupportUtil.isNotNullEmpty(objectId)){
					DomainObject busObj = DomainObject.newInstance(context,objectId);
					//Pushing Context to 'User Agent' as the logged in user may no have access to update attribute value
					ContextUtil.pushContext(context, PERSON_USER_AGENT, null, context.getVault().getName());
					isContextPushed = true;
					MarkUtils.markBOMeDeliveryFlag(context,busObj);
					ret = 0;
				}
				
			}
		} catch(FrameworkException e){
			ret = 1;
			logger.log(Level.WARNING, null, e);
		} finally {
			if(isContextPushed){
				ContextUtil.popContext(context);
				isContextPushed= false;
			}
		}
		return ret;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(RESEND_BOM_EDELIVERY);
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
	 * @about - Helper method to check if given object is supported for Resend BOM data
	 * @return boolean - true/false
	 * @throws Exception
	 */
	public boolean isTypeQualified() throws Exception {
		return SupportUtil.getTypes(SupportUtil.getValue(_infoMap, SUPPORT_ACTION_TYPE)).contains(SupportUtil.getTypeSymbolicName(_context, SupportUtil.getValue(_infoMap, SELECT_TYPE)));
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to record the history when the flagging is performed.  
	 * @return void 
	 * @throws Exception
	 */
	@Override
	public void recordHistory() throws Exception {
		try {
			MqlUtil.mqlCommand(_context,"modify bus $1 add history $2 comment $3", _infoMap.get(SELECT_ID), PARAM_CUSTOM, SupportType.RESEND_BOM_EDELIVERY.getComment().concat(_infoMap.get(SUPPORT_CONTEXT_USER))); 
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}

}
