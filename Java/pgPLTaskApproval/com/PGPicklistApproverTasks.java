/*
Project Name: P&G
Class Name: PGPicklistApproverTasks
Clone From/Reference: N/A
Purpose: This is a business class containing the logic to build response based on parameters passed
from widget service call.
Change History : Added for new functionalities under 2018x.6 release 
for Requirement 36581,36583,36584,36585,36586,36587,36588,36589
 */
package com;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import matrix.db.Context;
import matrix.util.StringList;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import com.pg.pl.custom.pgPLConstants;
import com.pg.pl.custom.pgPLUtil;
import com.matrixone.apps.domain.util.ContextUtil;

public class PGPicklistApproverTasks {

	private static final String ATTRIBUTE_APPROVALSTATUS            = PropertyUtil.getSchemaProperty(null, "attribute_ApprovalStatus" );
	private static final String JSON_OUTPUT_KEY_ID = "Id";
	private static final String JSON_INBOX_TASK_OUTPUT_KEY_ID ="IbId";
	private static final String JSON_OUTPUT_KEY_NAME= "name";
	private static final String REL_SELECT= "from["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].to.to["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].from.";
	private static final String CHANGE_REQUEST_SELECT= "from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from.";
	private static final String WHERE_EXP = "WhereExpression";
	private static final String OBJ_SELECT = "ObjectSelects";
	private static final String MULIT_VAL_CHAR = "\u0007";
	private static final String STATUS = "STATUS";	
	private static final String ZERO = "0";	
	private static final String SYMB_COMMA = ",";
	private static final String YES = "Yes";
	private static final String NO = "No";
	private static final String ALTERNATE_APPROVER = "Alternate Approver";
	private static final String SELECT_OWNER_APPROVER = "to["+pgPLConstants.RELATIONSHIP_PGPLOWNER+"].from.name";
	private static final String REL_PGPLOWNER_ATTRIBUTE_PGPLPRIMARY= "to["+pgPLConstants.RELATIONSHIP_PGPLOWNER+"].attribute["+pgPLConstants.ATTRIBUTE_PGPLPRIMARY+"]";
	private static final String ROLE_PL_APPROVER = PropertyUtil.getSchemaProperty(null,"role_pgPLApprover");
	private static final String STR_ROLE_PLADMIN = PropertyUtil.getSchemaProperty(null,"role_pgPLAdministrator");
	private static final String STR_ROLE_PGPLCENTRALADMIN = PropertyUtil.getSchemaProperty(null,"role_pgPLCentralPLUser");
	private static final String TYPE_PGPLCHANGEREQUEST = PropertyUtil.getSchemaProperty(null,"type_pgPLChangeRequest");
	private static final String REL_SELECT_TITLE= "to["+TYPE_PGPLCHANGEREQUEST+"].from."+DomainConstants.SELECT_ATTRIBUTE_TITLE;
	private static final String REL_SELECT_TYPE= "to["+TYPE_PGPLCHANGEREQUEST+"].from.type";
	private static final String REL_SELECT_NAME= "to["+TYPE_PGPLCHANGEREQUEST+"].from.name";
	private static final String REL_SELECT_ID= "to["+TYPE_PGPLCHANGEREQUEST+"].from.id";
	private static final String ATTRIBUTE_PG_PL_NODEID = PropertyUtil.getSchemaProperty(null,"attribute_pgPLNodeId");
	private static final String SELECT_ATTR_NODE_ID = "attribute["+ATTRIBUTE_PG_PL_NODEID+"]";
	private static final String ATTR_PG_PL_REL_VALUES = PropertyUtil.getSchemaProperty(null,"attribute_pgPLRelValues");
	private static final String SELECT_ATTR_PG_PL_REL_VALUES = "attribute["+ATTR_PG_PL_REL_VALUES+"]";
	private static final String SYM_PIPE_SEPERATOR = "|";
	private static final String STR_RELATED_VALUE1 = "Related Value1";
	private static final String STR_RELATED_VALUE2 = "Related Value2";
	private static final String STR_RELATED_VALUE3 = "Related Value3";
	private static final String STR_RELATED_VALUE4 = "Related Value4";
	private static final String STR_RELATED_VALUE5 = "Related Value5";
	private static final String STR_RELATED_VALUE6 = "Related Value6";
	private static final String STR_RELATED_VALUE7 = "Related Value7";
	private static final String STR_RELATED_VALUE8 = "Related Value8";
	private static final String STR_RELATED_VALUE9 = "Related Value9";
	private static final String STR_RELATED_VALUE10 = "Related Value10";
	private static final String STR_RELATED_VALUEN = "Related ValueN";
	private static final String STR_ATTRIBUTE = "attribute";
	private static final String STR_RELATIONSHIP = "relationship";
	private static final String STD_REV = "-";
	private static final String AUTO_APPROVE_TASK_COMMENT = "Task Approved on promote to Approved from Review State by User :";
	private static final String AUTO_REJECT_TASK_COMMENT = "Task Rejected on promote to Rejected from Review State by User :";
	private static final String STATE_COMPLETE = PropertyUtil.getSchemaProperty(null,"Policy",PropertyUtil.getSchemaProperty(null,"policy_InboxTask"),"state_Complete");
	private static final String STATE_REVIEW = PropertyUtil.getSchemaProperty(null,"Policy",  PropertyUtil.getSchemaProperty(null,"policy_pgPLChangeRequest"), "state_Review");
	
	private static final String STATE_REJECTED = PropertyUtil.getSchemaProperty(null,"Policy",  PropertyUtil.getSchemaProperty(null,"policy_pgPLChangeRequest"),"state_Rejected");
	private static final String STR_ATTR_PGPLCORPORATE_COMMENT = PropertyUtil.getSchemaProperty(null,"attribute_pgPLCorporateComment");
	private static final String BRANCH_TO = PropertyUtil.getSchemaProperty(null,"attribute_BranchTo");
	
	/**
	 * @param context	logged in user's context 
	 * @param mpParamMap holds the value passed from service for required data filtering 
	 * @return	String in JSON format, containing data extracted from enovia .
	 * @throws Exception when operation fails
	 */
	protected static String getAssignedPicklistApproverTask(Context context , Map<?,?> mpParamMap)  throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strWhereExpression = (String) mpParamMap.get(WHERE_EXP);
		String strSelectables = (String) mpParamMap.get(OBJ_SELECT);
		
		Map<?,?> reviewCRMap = null;
		String strCRObjectId = "";			
		MapList allReviewCRList =  new MapList();
		String loginUserName = null;
		String strTaksAssigneeName = null;
		StringList slTaksAssigneeName = null;
		MapList mlChangeRequest = null;
		
		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_MODIFIED);
		objectSelects.add(DomainConstants.SELECT_CURRENT);
		objectSelects.add(REL_SELECT_TITLE);
		objectSelects.add(REL_SELECT_TYPE);
		objectSelects.add(REL_SELECT_NAME);
		objectSelects.add(REL_SELECT_ID);
		objectSelects.add(SELECT_OWNER_APPROVER);
		objectSelects.add(REL_SELECT+DomainConstants.SELECT_ID);
		objectSelects.add(REL_SELECT+DomainConstants.SELECT_TYPE);
		objectSelects.add(REL_SELECT+DomainConstants.SELECT_CURRENT);
		objectSelects.add(REL_SELECT+DomainConstants.SELECT_NAME);
		if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
			objectSelects.addAll(strSelectables.split(","));
		}
		mlChangeRequest = DomainObject.findObjects(context, 								  	//Logged in User context
														pgPLConstants.TYPE_PGPLCHANGEREQUEST, 	//Type pattern
														pgPLConstants.SYMB_WILD, 			   	//name Pattern
														pgPLConstants.SYMB_WILD, 				//revision Pattern
														pgPLConstants.SYMB_WILD,				//owner Pattern
														pgPLConstants.VAULT_ESERVICEPRODUCTION, //vault Pattern
														strWhereExpression, 					//where Expression
														false,  								//expandType
														objectSelects); 						//object selects
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		if (!mlChangeRequest.isEmpty()) {
			mlChangeRequest.addSortKey(DomainConstants.SELECT_MODIFIED,"descending", "date");
			mlChangeRequest.sort();
			
			if(context.isAssigned(ROLE_PL_APPROVER) && !(context.isAssigned(STR_ROLE_PLADMIN) || context.isAssigned(STR_ROLE_PGPLCENTRALADMIN))) {
				loginUserName = context.getUser();
				Iterator<?> reviewCRIterator = mlChangeRequest.iterator();
				while(reviewCRIterator.hasNext()) {
					reviewCRMap =(Map<?,?>)reviewCRIterator.next();
					if ( reviewCRMap.get(SELECT_OWNER_APPROVER) instanceof StringList ) {
						slTaksAssigneeName = (StringList) reviewCRMap.get(SELECT_OWNER_APPROVER);
						strTaksAssigneeName = StringUtil.join(slTaksAssigneeName,",");
					} else {
						strTaksAssigneeName = (String) reviewCRMap.get(SELECT_OWNER_APPROVER);
					}		
					strCRObjectId = (String) reviewCRMap.get(DomainConstants.SELECT_ID);
					
					
					updateAttributeDetailsToMap(context, strCRObjectId, reviewCRMap);
					
					if(UIUtil.isNotNullAndNotEmpty(strTaksAssigneeName) && strTaksAssigneeName.contains(loginUserName))
						allReviewCRList.add(reviewCRMap);
				}		
			} else {
				Iterator<?> reviewCRIterator1 = mlChangeRequest.iterator();
				while(reviewCRIterator1.hasNext()) {
					reviewCRMap =(Map)reviewCRIterator1.next();
					strCRObjectId = (String) reviewCRMap.get(DomainConstants.SELECT_ID);
					updateAttributeDetailsToMap(context, strCRObjectId, reviewCRMap);
					allReviewCRList.add(reviewCRMap);
				}
			}
			jsonArr = getJsonArrayFromMapList (allReviewCRList);
		}
		output.add("data",jsonArr.build());
		return output.build().toString();
	}
	
	/**
	 * @param context	logged in user's context 
	 * @return	DomainObject of context user's person person object
	 * @throws Exception when operation fails
	 */
	private static Map updateAttributeDetailsToMap (Context context, String strCRObjectId, Map reviewCRMap) throws Exception  {
		StringList slPLOwnerList = getPicklistOwner(context, strCRObjectId, YES);
		String strPLOwnerValue = (String) reviewCRMap.get(REL_PGPLOWNER_ATTRIBUTE_PGPLPRIMARY);
		reviewCRMap.replace(REL_PGPLOWNER_ATTRIBUTE_PGPLPRIMARY, strPLOwnerValue, slPLOwnerList);
		
		StringList slPLAlternateOwnerList = getPicklistOwner(context, strCRObjectId, NO);
		reviewCRMap.put(ALTERNATE_APPROVER, slPLAlternateOwnerList);
		
		getPicklistValueTitleName(context,reviewCRMap);
		
		StringList slRelatedVaue1 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE1);
		reviewCRMap.put(STR_RELATED_VALUE1, slRelatedVaue1); 
		
		StringList slRelatedVaue2 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE2);
		reviewCRMap.put(STR_RELATED_VALUE2, slRelatedVaue2);
		
		StringList slRelatedVaue3 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE3);
		reviewCRMap.put(STR_RELATED_VALUE3, slRelatedVaue3); 
		
		StringList slRelatedVaue4 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE4);
		reviewCRMap.put(STR_RELATED_VALUE4, slRelatedVaue4);
		
		StringList slRelatedVaue5 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE5);
		reviewCRMap.put(STR_RELATED_VALUE5, slRelatedVaue5); 
		
		StringList slRelatedVaue6 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE6);
		reviewCRMap.put(STR_RELATED_VALUE6, slRelatedVaue6);
		
		StringList slRelatedVaue7 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE7);
		reviewCRMap.put(STR_RELATED_VALUE7, slRelatedVaue7); 
		
		StringList slRelatedVaue8 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE8);
		reviewCRMap.put(STR_RELATED_VALUE8, slRelatedVaue8);
		
		StringList slRelatedVaue9 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE9);
		reviewCRMap.put(STR_RELATED_VALUE9, slRelatedVaue9);
		
		StringList slRelatedVaue10 = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUE10);
		reviewCRMap.put(STR_RELATED_VALUE10, slRelatedVaue10); 
		
		StringList slRelatedVaueN = populateRelatedPicklistValues(context, strCRObjectId, STR_RELATED_VALUEN);
		reviewCRMap.put(STR_RELATED_VALUEN, slRelatedVaueN);
		
		return reviewCRMap;
	}
	
	/**
	 * @param context	logged in user's context 
	 * @return	Picklist Config Title and Object Title of a given Change Request
	 * @throws Exception when operation fails
	 */
	private static Map getPicklistValueTitleName (Context context, Map reviewCRMap) throws FrameworkException  {
		String strPLValueType = "";
		String strPLValueName = "";
		String strPLTitle = "";
		String strPLValueId = "";
		String strPLConfigTitle = "";
		StringList objectSelects = new StringList();
		DomainObject domPLObject = null;
		Map<?,?> configurationMap = null;
		
		strPLTitle = (String) reviewCRMap.get(REL_SELECT_TITLE);
		strPLValueType = (String) reviewCRMap.get(REL_SELECT_TYPE);
		strPLValueName = (String) reviewCRMap.get(REL_SELECT_NAME);
		strPLValueId = (String) reviewCRMap.get(REL_SELECT_ID);
		if(strPLValueType != null && strPLValueType.equals(pgPLConstants.TYPE_PGPICKLISTITEM)) {
			reviewCRMap.replace(REL_SELECT_NAME, strPLValueName, strPLTitle);
			domPLObject =DomainObject.newInstance(context,strPLValueId);
			strPLValueType = domPLObject.getInfo(context,pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE);
		}
		objectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		MapList mlPLConfiguration = DomainObject.findObjects(context, 	//Context of logged in user
															pgPLConstants.TYPE_PGPLCONFIGURATION, 					//Type pattern
															strPLValueType, 										//Name pattern
															ZERO, 													//revision pattern
															pgPLConstants.SYMB_WILD, 								//owner pattern 
															pgPLConstants.VAULT_ESERVICEPRODUCTION,  				//value pattern
															"", 													//object where clause
															false, 													//do not find subtypes
															objectSelects); 										//object selects
		if(null != mlPLConfiguration &&  !mlPLConfiguration.isEmpty()) {
			configurationMap=(Map)mlPLConfiguration.get(0);
			strPLConfigTitle = (String)configurationMap.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			reviewCRMap.put(REL_SELECT_TYPE, strPLConfigTitle);
		}
		
		return reviewCRMap;
	}
	/**
	 * @param context	logged in user's context 
	 * @return	Picklist Owner list of a given Change Request
	 * @throws Exception when operation fails
	 */
	private static StringList getPicklistOwner (Context context, String strCRObjectId, String strPLOwnerValue) throws FrameworkException  {
		StringList listData = new StringList();
		StringList busSelectList = null;
		StringList relSelectsList = null;
		String strRelOwner = "";
		Map<String,String> map = null;
		DomainObject doObjCR = DomainObject.newInstance (context, strCRObjectId);

		String strWhere= "attribute["+pgPLConstants.ATTRIBUTE_PGPLPRIMARY+"] == "+strPLOwnerValue;
		busSelectList = new StringList();
		busSelectList.add(DomainConstants.SELECT_NAME);
		MapList lst = doObjCR.getRelatedObjects(context, 								//Logged in User context
												pgPLConstants.RELATIONSHIP_PGPLOWNER, 	//Relationship pattern
												pgPLConstants.SYMB_WILD, 				//Type pattern
												busSelectList, 							//Business object selects
												relSelectsList, 						//relationship selects
												true, 									//to
												false, 									//from
												(short) 1, 								//level
												DomainConstants.EMPTY_STRING,			//buss where clause
												strWhere, 								//rel where clause
												0); 									//limit
		if(lst != null && !lst.isEmpty()) {
			Iterator<?> itr = lst.iterator();
			StringBuilder sb = new StringBuilder();
			while(itr.hasNext()) {
				map =  (Map) itr.next();
				sb.append( map.get(DomainConstants.SELECT_NAME)).append(",");
			}
			strRelOwner = sb.deleteCharAt(sb.lastIndexOf(",")).toString();
			listData.add(strRelOwner);
		}else{
			listData.add("");
		}
		return listData;
	}
	
	/**
	* Populate Related Picklist Values in Picklist Approver Table Columns
		 *
		 * @param context
		 * @param args
		 * @return StringList
		 * @throws Exception
	*/
	public static StringList getRelatedPicklistValues(Context context , StringList pgPLRelatedValuesAttrList, DomainObject tableObjDO, int iRelatedValueNumber ) throws Exception {
		StringList listData = new StringList();
		StringList pgPLRelatedAttrValList = null;
		String strRelatedType = "";
		String strRelTypeVal = "";
		String strRelAttribute = "";
		String strIsattributeOrRelationType = "";
		String strRelValActual = "";
		StringList busSelectList = null;
		StringList relSelectsList = new StringList();
		Map<String,String> map = null;
		String strNodeIdNumber = "";
		String strTypePattern = "";
		String strFilterTypePattern = "";
		int iNodNum = 0;
		String strNodeId = "";
		MapList mapRelatedData = null;

		if(pgPLRelatedValuesAttrList != null && !pgPLRelatedValuesAttrList.isEmpty() ) {
			pgPLRelatedAttrValList = StringUtil.split(pgPLRelatedValuesAttrList.get(iRelatedValueNumber),SYM_PIPE_SEPERATOR);
			strRelAttribute = pgPLRelatedAttrValList.get(pgPLRelatedAttrValList.size()-1);
			strRelatedType = pgPLRelatedAttrValList.get(pgPLRelatedAttrValList.size()-1);									
			strIsattributeOrRelationType = pgPLRelatedAttrValList.get(pgPLRelatedAttrValList.size()-2);
			if(strIsattributeOrRelationType.equalsIgnoreCase(STR_ATTRIBUTE)){
				if (strRelAttribute.equalsIgnoreCase(DomainConstants.SELECT_DESCRIPTION)){
					strRelValActual = tableObjDO.getDescription(context);
				} else {
					strRelValActual = tableObjDO.getInfo(context,"attribute["+strRelAttribute+"]");
				}										
				if("".equalsIgnoreCase(strRelValActual)){		
					listData.add("");	
				} else {		
					listData.add(strRelValActual);
				}
						
			} else if(strIsattributeOrRelationType.equalsIgnoreCase(STR_RELATIONSHIP)){								
				busSelectList = new StringList();
				busSelectList.add(DomainConstants.SELECT_NAME);
				relSelectsList.add(SELECT_ATTR_NODE_ID);
				if(pgPLUtil.isPicklistItemType(context,strRelatedType)){
					busSelectList.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
					strTypePattern = pgPLConstants.TYPE_PGPICKLISTITEM;
					strFilterTypePattern = strRelatedType;
				 } else {
					strTypePattern = strRelatedType;
					strFilterTypePattern = null;
				 }
				
				mapRelatedData = pgPLUtil.getRelatedPicklistObjects(context, //Context of logged in user
															tableObjDO, 								//DomainObject of from/to type
															pgPLConstants.RELATIONSHIP_PGPLRELATEDDATA, //relationship pattern
															strTypePattern, 							//Type pattern
															busSelectList, 								//bus selects
															relSelectsList, 							//rel selects
															false, 										//to
															true, 										//from
															null, 										//bus where clause
															null,										//rel where clause 
															strFilterTypePattern); 						//Old Picklist or Non-Picklist Type
				if(mapRelatedData != null && !mapRelatedData.isEmpty()){			
					Iterator<?> itr = mapRelatedData.iterator();
						StringBuilder sb = new StringBuilder();			
						while(itr.hasNext()){
							map =  (Map) itr.next();
							strNodeId = map.get(SELECT_ATTR_NODE_ID);
							iNodNum = iRelatedValueNumber + 1;
							strNodeIdNumber = Integer.toString(iNodNum);
							if(strNodeId != null && strNodeIdNumber.equals(strNodeId.trim())) {
								if(pgPLUtil.isPicklistItemType(context,strRelatedType)){
									sb.append( map.get(DomainConstants.SELECT_ATTRIBUTE_TITLE)).append(SYMB_COMMA);
								} else {
									sb.append( map.get(DomainConstants.SELECT_NAME)).append(SYMB_COMMA);
								}
							}
						}
						if(!"".equals(sb.toString().trim())) {
							strRelTypeVal = sb.deleteCharAt(sb.lastIndexOf(SYMB_COMMA)).toString();
						} else {
							strRelTypeVal = "";
						}
						listData.add(strRelTypeVal);		
				} else {
					listData.add("");
				}
			} else {
				listData.add("");
			}
		}
		return listData;
	}
	/**
	 * Populate Related Picklist Values in Picklist Approver Table for Column N
	 *
	 * @param context
	 * @param args
	 * @return StringList
	 * @throws Exception
	 */
	public static StringList populateRelatedPicklistValuesforColumnN(Context context , StringList pgPLRelatedValuesAttrList, DomainObject tableObjDO) throws Exception {			
		StringList listData = new StringList();
		String strRelValActual = "";
		String strRelatedType = "";
		String strRelTypeVal = "";
		String strRelAttribute = "";		
		String strIsattributeOrRelationType = "";
		StringList pgPLRelatedAttrValList = null;
		StringList busSelectList = null;
		Map<String,String> map = null;
		StringList relSelectsList = new StringList();
		//Modified By Picklist Team for 2018x.6 Requirement Review Comments Starts
		StringBuilder sbRelatedValue = new StringBuilder();
		//Modified By Picklist Team for 2018x.6 Requirement Review Comments Starts
		String strTypePattern = "";
		String strFilterTypePattern = "";
		MapList mapRelatedData = null;
		int iNodeId = 0;
		//Added By Picklist Team for 2018x.6 Requirement Review Comments Starts
		Iterator<?> itrRelatedData = null;
		StringBuilder sbRelatedData = new StringBuilder();
		String strNodeId = "";
		//Added By Picklist Team for 2018x.6 Requirement Review Comments Ends
		
		for(int i=10;i<pgPLRelatedValuesAttrList.size();i++){
			pgPLRelatedAttrValList = StringUtil.split(pgPLRelatedValuesAttrList.get(i),SYM_PIPE_SEPERATOR);
			strRelAttribute = pgPLRelatedAttrValList.get(pgPLRelatedAttrValList.size()-1);
			strRelatedType = pgPLRelatedAttrValList.get(pgPLRelatedAttrValList.size()-1);
			strIsattributeOrRelationType = pgPLRelatedAttrValList.get(pgPLRelatedAttrValList.size()-2);
			if(strIsattributeOrRelationType.equalsIgnoreCase(STR_ATTRIBUTE)){
				if (strRelAttribute.equalsIgnoreCase(DomainConstants.SELECT_DESCRIPTION)){
					strRelValActual=  tableObjDO.getDescription(context);
				} else {
					strRelValActual=  tableObjDO.getInfo(context,"attribute["+strRelAttribute+"]");
				}
				if("".equalsIgnoreCase(strRelValActual)){ 
					//Modified By Picklist Team for 2018x.6 Requirement Review Comments Starts
					sbRelatedValue.append(STD_REV).append(SYMB_COMMA);
					//Modified By Picklist Team for 2018x.6 Requirement Review Comments Ends
				} else {
					//Modified By Picklist Team for 2018x.6 Requirement Review Comments Starts
					sbRelatedValue.append(strRelValActual).append(SYMB_COMMA);
					//Modified By Picklist Team for 2018x.6 Requirement Review Comments Ends
				}
			}else if(strIsattributeOrRelationType.equalsIgnoreCase(STR_RELATIONSHIP)){
				busSelectList = new StringList();
				busSelectList.add(DomainConstants.SELECT_NAME);
				relSelectsList.add(SELECT_ATTR_NODE_ID);
				if(pgPLUtil.isPicklistItemType(context,strRelatedType)){
					busSelectList.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
					strTypePattern = pgPLConstants.TYPE_PGPICKLISTITEM;
					strFilterTypePattern = strRelatedType;
				 } else {
					strTypePattern = strRelatedType;
					strFilterTypePattern = null;
				 }
				mapRelatedData = pgPLUtil.getRelatedPicklistObjects(context, 							//Context of logged in user
															tableObjDO, 								//DomainObject of from/to type
															pgPLConstants.RELATIONSHIP_PGPLRELATEDDATA, //relationship pattern
															strTypePattern,  							//Type pattern
															busSelectList,								//bus selects
															relSelectsList, 							//rel selects
															false, 										//to
															true,										//from
															null, 										//bus where clause
															null, 										//rel where clause 
															strFilterTypePattern); 						//Old Picklist or Non-Picklist Type
				//Modified By Picklist Team for 2018x.6 Requirement Review Comments Starts
				sbRelatedData = new StringBuilder();
				if(mapRelatedData != null && !mapRelatedData.isEmpty()){
					itrRelatedData = mapRelatedData.iterator();
					while(itrRelatedData.hasNext()) {
						map =  (Map) itrRelatedData.next();
						strNodeId =map.get(SELECT_ATTR_NODE_ID);
						if(strNodeId!=null && !"".equals(strNodeId.trim())) {
							iNodeId = Integer.parseInt(strNodeId);
							if(iNodeId>10) {
								if(pgPLUtil.isPicklistItemType(context,strRelatedType)){
									sbRelatedData.append( map.get(DomainConstants.SELECT_ATTRIBUTE_TITLE)).append(SYMB_COMMA);
								} else {
									sbRelatedData.append( map.get(DomainConstants.SELECT_NAME)).append(SYMB_COMMA);
								}
							}
						}
					}
				}
				sbRelatedValue.append(sbRelatedData);
			} else {
				sbRelatedValue.append(STD_REV).append(SYMB_COMMA);
			}
		}
		int len = sbRelatedValue.length();
		if(len != 0){
			strRelTypeVal = sbRelatedValue.deleteCharAt(sbRelatedValue.lastIndexOf(SYMB_COMMA)).toString();
			//Modified By Picklist Team for 2018x.6 Requirement Review Comments Ends
			listData.add(strRelTypeVal);
		}else{
			listData.add("");
		}
						
		return listData;
	}
	/**
	 * Populate Related Picklist Values in Change Request Table Columns
	 *
	 * @param context
	 * @param args
	 * @return StringList
	 * @throws Exception
	 */
	public static StringList populateRelatedPicklistValues(Context context , String strObjectId, String strColumnName) throws Exception {			
		StringList listData = new StringList();
		String strCRId = "";
		String strTableObjId = "";
		String strTableObjType = "";		
		String strRelVal = "";					
		StringList pgPLRelatedValuesAttrList = null;
		Map<?,?> configurationMap = null;
		//Added By Picklist Team for 2018x.6 Requirement Review Comments Starts
		Map<?,?> objectInfoMap = null;
		StringList objectSelects = null;
		DomainObject tableObjDO = null;
		MapList mlPLConfiguration = null;
		//Added By Picklist Team for 2018x.6 Requirement Review Comments Ends

		if(null != strObjectId ) {
			strCRId=strObjectId;
			DomainObject crDO =DomainObject.newInstance(context,strCRId);
			strTableObjId = crDO.getInfo(context, "to["+pgPLConstants.RELATIONSHIP_PGPLCHANGEREQUEST+"].from.id");
			if(null != strTableObjId && !"".equals(strTableObjId)) {
				//Added/Modified By Picklist Team for 2018x.6 Requirement Review Comments Starts
				objectSelects=new StringList(2);
				objectSelects.add(DomainConstants.SELECT_TYPE);
				objectSelects.add(pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE);
				tableObjDO = DomainObject.newInstance(context,strTableObjId);
				objectInfoMap= tableObjDO.getInfo(context,objectSelects);
				if(objectInfoMap != null && !objectInfoMap.isEmpty()) {
					strTableObjType = (String) objectInfoMap.get(DomainConstants.SELECT_TYPE);
					if(strTableObjType != null && strTableObjType.equals(pgPLConstants.TYPE_PGPICKLISTITEM)) {
						strTableObjType = (String) objectInfoMap.get(pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE);
					}
					//Added/Modified By Picklist Team for 2018x.6 Requirement Review Comments Ends
				}
				objectSelects=new StringList(2);
				objectSelects.add(DomainConstants.SELECT_ID);				
				objectSelects.add(SELECT_ATTR_PG_PL_REL_VALUES);
				mlPLConfiguration = DomainObject.findObjects(context, 								  	//Context of logged in user
															pgPLConstants.TYPE_PGPLCONFIGURATION, 	//Type pattern
															strTableObjType, 					 	//Name pattern
															ZERO, 									//revision pattern
															pgPLConstants.SYMB_WILD, 				//owner pattern 
															pgPLConstants.VAULT_ESERVICEPRODUCTION, //value pattern
															null, 									//object where clause
															false, 									//do not find subtypes
															objectSelects); 						//object selects
				
				if(null != mlPLConfiguration &&  !mlPLConfiguration.isEmpty()) {
					configurationMap=(Map<?, ?>)mlPLConfiguration.get(0);
					strRelVal = (String)configurationMap.get(SELECT_ATTR_PG_PL_REL_VALUES);
					pgPLRelatedValuesAttrList = StringUtil.split(strRelVal,"\n");
					if(!pgPLRelatedValuesAttrList.isEmpty() && null != strRelVal &&  !("".equals(strRelVal))){
						if(STR_RELATED_VALUE1.equals(strColumnName) && !pgPLRelatedValuesAttrList.isEmpty()){
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 0);							
						} else if(STR_RELATED_VALUE2.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>1) {
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 1);					
						} else if(STR_RELATED_VALUE3.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>2) {
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 2);
						} else if(STR_RELATED_VALUE4.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>3) {
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 3);
						} else if(STR_RELATED_VALUE5.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>4) {
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 4);
						} else if(STR_RELATED_VALUE6.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>5) {
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 5);
						} else if(STR_RELATED_VALUE7.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>6) {	
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 6);
						} else if(STR_RELATED_VALUE8.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>7) {	
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 7);
						} else if(STR_RELATED_VALUE9.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>8) {	
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 8);
						} else if(STR_RELATED_VALUE10.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>9) {
							listData = getRelatedPicklistValues(context, pgPLRelatedValuesAttrList, tableObjDO, 9);
						} else if(STR_RELATED_VALUEN.equals(strColumnName) && pgPLRelatedValuesAttrList.size()>10) {
							listData = populateRelatedPicklistValuesforColumnN(context, pgPLRelatedValuesAttrList, tableObjDO);
						}
					} else {
						listData.add("");
					}
				}
			}
		}
		return listData;
	}	
	/**
	 * @param mlChangeRequest	MapList of data containing data from enovia.
	 * @return JsonArrayBuilder having data from enovia in JSON array format.
	 * @throws Exception when operation fails
	 */
	private static JsonArrayBuilder getJsonArrayFromMapList ( MapList mlChangeRequest ) throws Exception {
		JsonObjectBuilder jsonObject = null;
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		Map<?, ?> objMap = null;
		String strKey ;
		String strValue;
		String strId ;
		StringList slInboxTaskIds = null ;
		String strRelatedObjectType;
		String strRelatedObjectState;
		String strRelatedObjectName;
		String strRelatedObjectId;
		for (int i = 0; i < mlChangeRequest.size(); i++) {
			jsonObject = Json.createObjectBuilder(); 
			objMap = (Map<?,?>)mlChangeRequest.get(i);
			if(objMap.get(REL_SELECT+DomainConstants.SELECT_ID) instanceof StringList) {
				slInboxTaskIds = (StringList)objMap.get(REL_SELECT+DomainConstants.SELECT_ID);
				strId = checkNullValueforString(slInboxTaskIds.get(0));
			} else {
				strId = checkNullValueforString((String)objMap.get(REL_SELECT+DomainConstants.SELECT_ID));
				slInboxTaskIds = StringUtil.split(strId,SYMB_COMMA);
				strId = checkNullValueforString(slInboxTaskIds.get(0));				
			}
			strRelatedObjectType = (String)objMap.get(DomainConstants.SELECT_TYPE);
			strRelatedObjectState = (String)objMap.get(DomainConstants.SELECT_CURRENT);
			strRelatedObjectName = (String)objMap.get(DomainConstants.SELECT_NAME);
			strRelatedObjectId = (String)objMap.get(DomainConstants.SELECT_ID);

			if (TYPE_PGPLCHANGEREQUEST.equals(strRelatedObjectType) &&
					STATE_REVIEW.equals(strRelatedObjectState)  ) {

				jsonObject.add(JSON_OUTPUT_KEY_ID,strRelatedObjectId);	
				jsonObject.add(JSON_INBOX_TASK_OUTPUT_KEY_ID,strId);
				jsonObject.add(JSON_OUTPUT_KEY_NAME,checkNullValueforString(strRelatedObjectName));
				objMap.remove(REL_SELECT+DomainConstants.SELECT_NAME);
				objMap.remove(REL_SELECT+DomainConstants.SELECT_TYPE);
				objMap.remove(REL_SELECT+DomainConstants.SELECT_CURRENT);
				objMap.remove(DomainConstants.SELECT_ID);
				objMap.remove(REL_SELECT+DomainConstants.SELECT_ID);

				for (Entry<?, ?> entry : objMap.entrySet()) {
					strKey = (String) entry.getKey();		
					if ( entry.getValue() instanceof StringList) {
						strValue =StringUtil.join((StringList)entry.getValue(), ",");	
					} else {
						strValue = checkNullValueforString((String)entry.getValue());	
					}
					jsonObject.add(strKey, strValue);
				}	
				jsonArr.add(jsonObject);
			}
		}
		return jsonArr;
	}
	/**
	 * @param strString parameter for json object.
	 * @return string if not null blank otherwise.
	 * @throws Exception when operation fails
	 */
	private static String checkNullValueforString( String strString) {
		if(strString!=null && strString.contains(MULIT_VAL_CHAR)) {
			strString=strString.replace(MULIT_VAL_CHAR, ",");
		}
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}
	/**
	 * @param context	logged in user's context 
	 *  @param strProcessCheck holds the value for task to be approved/rejected.
	 * @param strInboxTaskId holds the value Inbox Task Ids to be processed.
	 * @param strComment holds the value to update comment on Inbox Task.
	 * @param strChangeRequestId holds the value of change Request Id to be processed.
	 * @return	String in JSON format, containing data extracted from enovia .
	 * @throws Exception when operation fails
	 */
	protected static String validateUserAndProcessTask(Context context,String strProcessCheck, String strInboxTaskId, String strChangeRequestId, String strComment) throws Exception {
		JsonObjectBuilder jsonReturnObject = Json.createObjectBuilder(); 
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		String strITTaskComments = "";
		String strUser = context.getUser();
		if(strProcessCheck != null && "Approve".equals(strProcessCheck)) {
			strITTaskComments = AUTO_APPROVE_TASK_COMMENT + strUser;
		} else {
			strITTaskComments = AUTO_REJECT_TASK_COMMENT + strUser;
		}
		String strInboxTaskCompletedIds = processInboxTask (context,strProcessCheck,strInboxTaskId,strChangeRequestId,strITTaskComments, strComment);
		
		jsonObject.add(STATUS, strInboxTaskCompletedIds);
		jsonReturnObject.add("data",jsonObject.build());
		
		return jsonReturnObject.build().toString();
	}
	/**
	 * @param context	logged in user's context 
	 *  @param strProcessCheck holds the value for task to be approved/rejected.
	 * @param strInboxTaskId holds the value Inbox Task Ids to be processed.
	 * @param strChangeRequestIds holds the value of change Request Id to be processed.
	 * @param strComment holds the value to update comment on Inbox Task.
	 * @param strUserComments holds the value to update comment on Change Request.
	 * @throws Exception when operation fails
	 */
	private static String processInboxTask(Context context, String strProcessCheck, String strInboxTaskIds, String strChangeRequestIds, String strComment, String strUserComments)throws Exception {
		DomainObject doObjItask;
		DomainObject doObjCR;
		String strInboxTaskId = "";
		String strChangeRequestId = "";
		String strChangeRequestIdToMatch = "";
		boolean isTaskProcessed = false;
		boolean isContextPushed = false;
		Map<String, String> mpAttributeMap = new HashMap<>();
		StringBuilder sbStatus = new StringBuilder();
		
		if ( null != strInboxTaskIds && !strInboxTaskIds.equals(DomainConstants.EMPTY_STRING) ) {
			StringList slInboxTaskIds = StringUtil.split(strInboxTaskIds,SYMB_COMMA);
			StringList slChangeRequestIds = StringUtil.split(strChangeRequestIds,SYMB_COMMA);
			for (int iCount = 0; iCount<slInboxTaskIds.size() ;iCount++) {
				try {
					isTaskProcessed = false;
					strInboxTaskId = slInboxTaskIds.get(iCount);
					strChangeRequestId = slChangeRequestIds.get(iCount);
					if(strInboxTaskId != null && !strInboxTaskId.equals(DomainConstants.EMPTY_STRING)) {
						doObjItask = DomainObject.newInstance (context,strInboxTaskId);
						doObjCR = DomainObject.newInstance (context, strChangeRequestId);
						if (doObjItask.exists(context)) {
							mpAttributeMap = new HashMap<>();
							mpAttributeMap.put(DomainConstants.ATTRIBUTE_COMMENTS, strComment);
							mpAttributeMap.put(ATTRIBUTE_APPROVALSTATUS, strProcessCheck);						
							doObjItask.setAttributeValues(context, mpAttributeMap);
	
							//The context is pushed for the USER AGENT for promoting the Inbox Task to complete state as context user may not have promote access
							 ContextUtil.pushContext(context,pgPLConstants.PERSON_USER_AGENT, null, null);
							 isContextPushed = true;
							 doObjItask.setState(context, STATE_COMPLETE);
							 isTaskProcessed = true;
							 if(isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
							//Popping the context back to logged-in user
							//Update ChangeRequest object
							if(strProcessCheck != null && "Reject".equals(strProcessCheck)) {
								mpAttributeMap = new HashMap<>();
								mpAttributeMap.put(BRANCH_TO, STATE_REJECTED);
								mpAttributeMap.put(STR_ATTR_PGPLCORPORATE_COMMENT, strUserComments);
								strChangeRequestIdToMatch = doObjItask.getInfo(context,CHANGE_REQUEST_SELECT+DomainConstants.SELECT_ID);
								if(strChangeRequestId != null && strChangeRequestId.equals(strChangeRequestIdToMatch) 
										&& TYPE_PGPLCHANGEREQUEST.equals(doObjCR.getInfo(context, DomainConstants.SELECT_TYPE))&& isTaskProcessed) {
									doObjCR.setAttributeValues(context, mpAttributeMap);
									doObjCR.setState(context, STATE_REJECTED);
								}
							}
						}
					}
				} catch (Exception e) {
					sbStatus.append(e.getMessage());
				}
				finally{
					//Popping the context back to logged-in user
					if(isContextPushed) {
						ContextUtil.popContext(context);
						isContextPushed = false;
					}
				}
			}	
		}		
		return sbStatus.toString(); 
	}
}