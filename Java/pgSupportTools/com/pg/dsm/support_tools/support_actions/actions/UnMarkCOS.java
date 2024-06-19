package com.pg.dsm.support_tools.support_actions.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.matrixone.apps.awl.util.Access;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.pg.dsm.support_tools.support_actions.util.SupportAction;
import com.pg.dsm.support_tools.support_actions.util.SupportType;
import com.pg.dsm.support_tools.support_actions.util.SupportUtil;

import matrix.db.Context;
import matrix.util.StringList;

public class UnMarkCOS extends SupportAction {

	Context _context;
	String _supportAction;
	Map<String, String> _infoMap;

	public UnMarkCOS(Context _context, String _supportAction, Map<String, String> _infoMap) {
		this._context = _context;
		this._supportAction = _supportAction;
		this._infoMap = _infoMap;
	}
	@Override
	public boolean checkState() throws Exception {
		return SupportUtil.isStateRelease((String)_infoMap.get(SELECT_CURRENT));
	}
	@Override
	public boolean hasAccess() throws Exception {
		return hasReadAccess();
	}
	public boolean hasReadAccess() throws Exception {
		return SupportUtil.hasAccess(_context, (String)_infoMap.get(SELECT_ID), ACCESS_READ, ACCESS_SHOW);
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to call the marking un-mark COS program 
	 * @param context
	 * @param temp - required key, value for processing. 
	 * @return void -
	 * @throws Exception
	 */
	@Override
	public String execute() throws Exception {
		String ret = UNSUPPORTED_ACTION;
		try {
			System.out.println("Inside Mark Dynamic Subscription - execute:"+_supportAction);
			String args[] = { (String)_infoMap.get(SELECT_ID), (String)_infoMap.get(SELECT_TYPE) };
			unMarkCOS(_context, args);
			ret = SUPPORT_ACTION_SUCCESS;
		} catch(Exception e) {
			ret = SupportType.UNMARK_COS.getException();
			e.printStackTrace();
		}
		return ret;
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to record the history when the flagging is performed.  
	 * @return void 
	 * @throws Exception
	 */
	@Override
	public void recordHistory() throws Exception {
		try {
			MqlUtil.mqlCommand(_context,"modify bus $1 add history $2 comment $3", _infoMap.get(SELECT_ID), PARAM_CUSTOM, SupportType.UNMARK_COS.getComment().concat(_infoMap.get(SUPPORT_CONTEXT_USER))); 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(UNMARK_COS);
		builder.append(SYMB_SQUARE_BRACKET_OPEN);
		builder.append(_context);
		builder.append(SYMBOL_COMMA);
		builder.append(_supportAction);
		builder.append(SYMBOL_COMMA);
		builder.append(_infoMap);
		builder.append(SYMB_SQUARE_BRACKET_CLOSE);
		return builder.toString();
	}
	@Override
	public boolean isQualified() throws Exception {
		return isTypeQualified();
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to check if given object is supported for Un-mark COS 
	 * @return boolean - true/false
	 * @throws Exception
	 */
	public boolean isTypeQualified() throws Exception {
		return SupportUtil.getTypes(SupportUtil.getValue(_infoMap, SUPPORT_ACTION_TYPE)).contains(SupportUtil.getTypeSymbolicName(_context, SupportUtil.getValue(_infoMap, SELECT_TYPE)));
	}

	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to get connect APP.
	 * @param - Context
	 * @param - DomainObject
	 * @param - String
	 * @return MapList
	 * @throws Exception
	 */
	MapList getAPPAlternate(Context context, DomainObject busObj, String busWhere) throws Exception {
		return busObj.getRelatedObjects(context, 			// context
				RELATIONSHIP_ALTERNATE, 	// Relationship Pattern
				TYPE_ASSEMBLEDPRODUCTPART, // Type Pattern
				new StringList(SELECT_ID),// Object Selects 
				EMPTY_STRINGLIST,		  // Rel Selects
				true, 									// To Side
				false, 									// From Side
				(short)1, 								// Recursion level
				busWhere, 							// Object where clause
				EMPTY_STRING,			// rel where clause
				0);										// Limit
	}
	
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to unmark.
	 * @param - Context
	 * @param - DomainObject
	 * @return void
	 * @throws Exception
	 */
	public void unMarkAPPAlternate(Context context, DomainObject busObj) throws Exception {
		try {
			String busWhere = SELECT_CURRENT + " == "+STATE_RELEASE;
			MapList objectList = getAPPAlternate(context, busObj, busWhere);
			if(objectList != null && !objectList.isEmpty()) {
				Map<String, String> tempMap = new HashMap<String, String>();
				Iterator itr = objectList.iterator();
				while(itr.hasNext()) {
					tempMap = (Map)itr.next();
					setParentIPSCosCalculateForUnMark(context, DomainObject.newInstance(context, (String)tempMap.get(SELECT_ID)));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to unmark.
	 * @param - Context
	 * @param - String[] - array
	 * @return void
	 * @throws Exception
	 */
	public void unMarkCOS (Context context, String args[]) throws Exception {
		boolean isCtxPushed = false;
		try {
			ContextUtil.pushContext(context);
			isCtxPushed = true;
			if (null!=args && args.length>0) {
				String objectId = args[0];
				String strType = args[1];
				String strEvent = null;
				if(args.length > 2) {
					strEvent = args[2];
				}
				DomainObject busObj = DomainObject.newInstance(context, objectId);
				String strReleasePhase = (String)busObj.getInfo(context,"attribute["+STR_RELEASE_PHASE+"]");
				StringList allowedTypes = SupportUtil.getTypes(SupportUtil.getValue(_infoMap, SUPPORT_ACTION_TYPE));
				String symbolicType = SupportUtil.getTypeSymbolicName(context, strType);

				/*****************************************************************\
				 * variable for parent ips un-marking
				\*****************************************************************/

				if(SupportUtil.isNotNullEmpty(strReleasePhase) && (!ATTRIBUTE_STAGE_PRODUCTION_VALUE.equalsIgnoreCase(strReleasePhase))) {
					return ;
				}
				if (SupportUtil.isNotNullEmpty(objectId)) {

					if(UNMARK_COS_MULTI_FLAG_ALLOWED_TYPES.contains(strType)) {

						//set multiple flag certain types - exclude for type CUP
						if(allowedTypes.contains(symbolicType) && !TYPE_PGCONSUMERUNITPART.equals(strType)) {
							setMultipleAttributeValuesForUnMark(context, busObj);
						}
						String prevRevId = (String)busObj.getInfo(context, SELECT_PREVIOUS_REVISION_ID);
						if(SupportUtil.isNotNullEmpty(prevRevId)) {
							setParentIPSCosCalculateForUnMark(context, DomainObject.newInstance(context, prevRevId));
						}
						setParentIPSCosCalculateForUnMark(context, busObj);
					}
					else if(UNMARK_COS_PARENT_FLAG_ALLOWED_TYPES.contains(symbolicType)) {
						setParentIPSCosCalculateForUnMark(context,busObj);
						if(TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType)) {
							unMarkAPPAlternate(context, busObj);
						}
					}
					// need confirmation
					//to-do: 
					/*	if(SupportUtil.isNotNullEmpty(strType) &&(TYPE_PGARTWORK.equals(strType)||TYPE_POA.equals(strType)))
						{
							setIPSAttributeForArtworkPOA(context,strObjectID);
						}*/
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(isCtxPushed)
				ContextUtil.popContext(context);
		}
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to unmark.
	 * @param - Context
	 * @param - DomainObject
	 * @return void
	 * @throws Exception
	 */
	private void setParentIPSCosCalculateForUnMark(Context context, DomainObject busObj) throws Exception {
		try {
			MapList objectList = busObj.getRelatedObjects(context,
					IPS_REL_EBOM_ALTERNATE_PATTERN.toString(),  // String Relationship pattern
					TYPE_PART,       	// String type pattern
					true,          					// boolean getTo
					false,       	 				// boolean getFrom
					(short)0,       			    // short recurseToLevel
					UNMARK_COS_BUS_SELECTS,      				// StringList objectSelects
					EMPTY_STRINGLIST,   // StringList relationshipSelects
					EMPTY_STRING,    	// String objectWhere
					EMPTY_STRING, 		// String relationshipWhere
					(short)0,          				// short limit
					EMPTY_STRING,       // String postRelPattern
					IPS_POST_TYPES.toString(),      // String postTypePattern
					null);       					// Map postPatterns

			String parentId = EMPTY_STRING;
			String parentType = EMPTY_STRING;
			String parentState = EMPTY_STRING;
			String asmType = EMPTY_STRING;

			String infoArray[] = new String[3];
			StringList processedList = new StringList();

			Map<String, String> infoMap = new HashMap<String, String>();
			DomainObject parentObj = DomainObject.newInstance(context);

			if(objectList != null) {
				for(Iterator itr=objectList.iterator();itr.hasNext();) {
					infoMap = (Map)itr.next();
					parentId = (String) infoMap.get(SELECT_ID);
					parentType = (String) infoMap.get(SELECT_TYPE);
					parentState = (String) infoMap.get(SELECT_CURRENT);
					infoArray = getLastReleaseRevisionInfo(context, parentId);
					if(null != infoArray && infoArray.length > 2) {
						parentId = infoArray[0];
						parentState = infoArray[2];
					} 
					if(!STATE_PART_OBSOLETE.equalsIgnoreCase(parentState) && null != processedList && !processedList.contains(parentId)) {
						parentObj.setId(parentId);
						if(!TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(parentType) && !TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(parentType)
								&& STATE_PART_RELEASE.equalsIgnoreCase(parentState)) {
							setMultipleAttributeValuesForUnMark(context, parentObj);
						} else if(TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(parentType)) {
							asmType = (String) infoMap.get("attribute["+ATTRIBUTE_PGASSEMBLYTYPE+"]");
							if(RESHIPPER_ASS_TYPE_VAL.equalsIgnoreCase(asmType))
								getEBOMSubstituteIPSForUnMark(context, parentObj, processedList);
						} else if(TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(parentType)) {
							getEBOMSubstituteIPSForUnMark(context, parentObj, processedList);
						}
						processedList.add(parentId);
					}
				}
				//check if present IPS is Substitute of any other IPS if yes than set its pgCOSCalculate value to True.
				getEBOMSubstituteIPSForUnMark(context, busObj, processedList);

			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to substitute.
	 * @param - Context
	 * @param - DomainObject
	 * @param - StringList - objectId
	 * @return void
	 * @throws Exception
	 */
	private void getEBOMSubstituteIPSForUnMark(Context context, DomainObject busObj, StringList processedList) throws Exception {
		try {

			Map substituteMap = com.matrixone.apps.awl.util.BusinessUtil.getInfoList(context, busObj.getObjectId(), UNMARK_COS_REL_SUBSTITUTE_SELECTS);

			StringList oidList = null;
			StringList typeList = null;
			StringList stateList = null;

			if(null != substituteMap) {
				oidList = (StringList) substituteMap.get(REL_EXPAND_SUBSTITUTE_IDS);
				typeList = (StringList) substituteMap.get(REL_EXPAND_SUBSTITUTE_TYPES);
				stateList = (StringList) substituteMap.get(REL_EXPAND_SUBSTITUTE_STATES);
			}
			if(null != oidList) {

				String infoArray[] = new String[3];

				//temporary variables for iteration
				String sPhaseState = EMPTY_STRING;
				String sPhaseType = EMPTY_STRING;
				String sPhaseId = EMPTY_STRING;
				String tempId = EMPTY_STRING;
				String tempType = EMPTY_STRING;
				String tempState = EMPTY_STRING;
				DomainObject tempObj = DomainObject.newInstance(context);
				MapList tempMapList = null;
				Iterator itr = null;
				Map tempMap = null;

				int subSize = oidList.size();

				for(int i=0; i<subSize; i++) {

					sPhaseId = (String)oidList.get(i);
					sPhaseState = EMPTY_STRING;
					sPhaseType = (String) typeList.get(i);

					if(TYPE_FINISHEDPRODUCTPART.equals(sPhaseType) || TYPE_PACKAGINGASSEMBLYPART.equals(sPhaseType) || TYPE_FABRICATEDPART.equals(sPhaseType) || TYPE_PGPACKINGSUBASSEMBLY.equals(sPhaseType) || TYPE_PGFINISHEDPRODUCT.equals(sPhaseType)) {
						infoArray = getLastReleaseRevisionInfo(context, sPhaseId);
						if(null != infoArray && infoArray.length > 2) {
							sPhaseId = infoArray[0];
							sPhaseState = infoArray[2];
						} else {
							sPhaseState = (String)stateList.get(i);
						}
						if(STATE_PART_RELEASE.equalsIgnoreCase(sPhaseState) && null != processedList && !processedList.contains(sPhaseId)) {
							setMultipleAttributeValuesForUnMark(context, DomainObject.newInstance(context,sPhaseId));
							processedList.add(sPhaseId);
						}
					}

					tempObj.setId(sPhaseId);
					tempMapList = tempObj.getRelatedObjects(context, 
							RELATIONSHIP_EBOM ,
							TYPE_PART, 
							true, 
							false, 
							(short)0, 
							UNMARK_COS_BUS_SELECTS,
							EMPTY_STRINGLIST, 
							EMPTY_STRING, 
							EMPTY_STRING,
							(short)0,
							EMPTY_STRING, 
							EBOM_POST_TPYES.toString(), 
							null);

					if(null != tempMapList && tempMapList.size() > 0) {

						itr = tempMapList.iterator();
						while(itr.hasNext()) {
							tempMap = (Map)itr.next();

							tempId = (String)tempMap.get(SELECT_ID);
							tempType = (String)tempMap.get(SELECT_TYPE);
							tempState = (String)tempMap.get(SELECT_CURRENT);

							infoArray = getLastReleaseRevisionInfo(context, tempId);
							if(null != infoArray && infoArray.length > 2) {
								tempId = infoArray[0];
								tempState = infoArray[2];
							} 

							if(!STATE_PART_OBSOLETE.equalsIgnoreCase(tempState) && null != processedList && !processedList.contains(tempId)) {
								tempObj.setId(tempId);
								if(!TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(tempType) && STATE_PART_RELEASE.equalsIgnoreCase(tempState)) {
									setMultipleAttributeValuesForUnMark(context, tempObj);
								} else if(TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(tempType)) {
									if(RESHIPPER_ASS_TYPE_VAL.equalsIgnoreCase((String) tempMap.get("attribute["+ATTRIBUTE_PGASSEMBLYTYPE+"]"))) {
										getEBOMSubstituteIPSForUnMark(context, tempObj, processedList);
									}
								}
								processedList.add(tempId);
							}
						} //
					}
					//clean-up
					tempMapList = null;
					itr = null;
				}
			}
		}
		catch (FrameworkException e) {	
			e.printStackTrace();
		}
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to set flag.
	 * @param - Context
	 * @param - DomainObject
	 * @return void
	 * @throws Exception
	 */
	public void setMultipleAttributeValuesForUnMark(Context context, DomainObject busObj) throws Exception {
		try {
			Map infoMap = busObj.getInfo(context, UNMARK_COS_BUS_SELECTS);
			String strObjectId = (String)infoMap.get(SELECT_ID);
			if (SupportUtil.isNotNullEmpty(strObjectId)) {
				//check access
				boolean hasModifyAccess = Access.hasAccess(context, strObjectId, new String[] { "modify" });
				if (!hasModifyAccess) {
					return;
				}
				String strPolicy = (String)infoMap.get(SELECT_POLICY);
				if ((POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase(strPolicy)) || (POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)))
					return;

				Map<String, String> tempMap = new HashMap<String, String>();
				tempMap.put(ATTRIBUTE_PGCOSCALCULATE, ATTRIBUTE_PGCOSCALCULATE_FALSE);
				tempMap.put(ATTRIBUTE_PGCOSFLAGDATE, "");
				busObj.setAttributeValues(context, tempMap);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**DSM 2018x.3 - On Demand Support Tools 
	 * @about - Helper method to get latest revision info
	 * @param - Context
	 * @param - String - objectid
	 * @return String[] - array of string
	 * @throws Exception
	 */
	public String[] getLastReleaseRevisionInfo(Context context, String objectId) throws Exception{
		String [] infoArray= new String[3];
		if(SupportUtil.isNotNullEmpty(objectId)) {
			DomainObject busObj = DomainObject.newInstance(context, objectId);
			MapList objectList = busObj.getRevisionsInfo(context, UNMARK_COS_BUS_SELECTS, new StringList());
			Map<String, String> infoMap = new HashMap<String, String>();
			Iterator itr = objectList.iterator();
			while(itr.hasNext()) {
				infoMap = (Map)itr.next();
				if(STATE_PART_RELEASE.equalsIgnoreCase((String)infoMap.get(SELECT_CURRENT))){
					infoArray[0] = (String)infoMap.get(SELECT_ID);
					infoArray[1] = (String)infoMap.get(SELECT_REVISION);
					infoArray[2] = (String)infoMap.get(SELECT_CURRENT);
					return infoArray;
				}
			}
		}
		return null;
	}
}
