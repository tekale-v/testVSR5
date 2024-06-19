/*
Project Name: P&G
Class Name: PGSignupFormTasks
Clone From/Reference: N/A
Purpose: This is a business class containing the logic to build response based on parameters passed
from widget service call.
Change History : Added for new functionalities under 2018x.5 release 
for Requirement 33490,34528,33491,34529,34530,34531,34532,34533,34535
 */
package com;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import matrix.db.Context;
import matrix.util.StringList;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainSymbolicConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpSession;
import com.matrixone.apps.domain.util.PersonUtil;

//Code added for UR 22x release Defect Id - 38918 : Start
import java.text.SimpleDateFormat;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import java.util.Date;
import java.util.Hashtable;
//Code added for UR 22x release Defect Id - 38918 : Ends




public class PGSignupFormTasks {

	private static final String VAULT_ESERVICE_PRODUCTION = PropertyUtil.getSchemaProperty(null, DomainSymbolicConstants.SYMBOLIC_vault_eServiceProduction);
	private static final String TYPE_PGSIGNUPFORM = PropertyUtil.getSchemaProperty(null,"type_pgSignupForm");
	private static final String ATTRIBUTE_TASKCOMMENTSNEEDED  = PropertyUtil.getSchemaProperty(null,"attribute_TaskCommentsNeeded");
	private static final String ATTRIBUTE_REVIEWCOMMENTSNEEDED = PropertyUtil.getSchemaProperty(null, "attribute_ReviewCommentsNeeded");
	private static final String ATTRIBUTE_APPROVALSTATUS            = PropertyUtil.getSchemaProperty(null, "attribute_ApprovalStatus" );
	private static final String JSON_OUTPUT_KEY_ID = "Id";
	private static final String STR_YES = "Yes";
	private static final String STR_NO = "No";
	private static final String JSON_INBOX_TASK_OUTPUT_KEY_ID ="IbId";
	private static final String SYMBOL_HYPHEN ="-";
	private static final String JSON_OUTPUT_KEY_NAME= "name";
	private static final String STATE_PGSIGNUPFORM_ACTIVE = PropertyUtil.getSchemaProperty(null,"Policy", PropertyUtil.getSchemaProperty(null,"policy_pgSignupForm"),"state_Active");
	private static final String STATE_PGSIGNUPFORM_REVIEW =PropertyUtil.getSchemaProperty(null,"Policy", PropertyUtil.getSchemaProperty(null,"policy_pgSignupForm"),"state_Review");;
	private static final String REL_SELECT= "relationship["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.relationship["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from.";
	private static final String WHERE_EXP = "WhereExpression";
	private static final String OBJ_SELECT = "ObjectSelects";
	private static final String MULIT_VAL_CHAR = "\u0007";
	private static final String STATUS = "STATUS";
	private static final String INVALID_USER_MESSAGE = "Invalid Username and/or Password.";
	//Code added for UR 22x release Defect Id - 38918 : Start
	private static final String STR_RELATIONSHIP_ROUTETASK =  PropertyUtil.getSchemaProperty(null,"relationship_RouteTask");
	//Code added for UR 22x release Defect Id - 38918 : End
	/**
	 * @param context	logged in user's context 
	 * @param mpParamMap holds the value passed from service for required data filtering 
	 * @return	String in JSON format, containing data extracted from enovia .
	 * @throws Exception when operation fails
	 */
	protected static String getAssignedSignupFormTask(Context context , Map<?,?> mpParamMap)  throws Exception {

		JsonObjectBuilder output = Json.createObjectBuilder();
		//Code added for UR 22x release Defect Id - 38918 : Start
		Map mpSignupFormMap = null;
		String strReviewDate = DomainConstants.EMPTY_STRING;
		//Code added for UR 22x release Defect Id - 38918 : Ends
		try {
			String strWhereExpression = (String) mpParamMap.get(WHERE_EXP);
			String strSelectables = (String) mpParamMap.get(OBJ_SELECT);
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(REL_SELECT+DomainConstants.SELECT_ID);
			objectSelects.add(REL_SELECT+DomainConstants.SELECT_TYPE);
			objectSelects.add(REL_SELECT+DomainConstants.SELECT_CURRENT);
			objectSelects.add(REL_SELECT+DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_NAME);
			//Code added for UR 22x release Defect Id - 38918 : Start
			String formatedDate = DomainConstants.EMPTY_STRING;
			//Code added for UR 22x release Defect Id - 38918 : Ends
			if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
				objectSelects.addAll(strSelectables.split(","));
			}
			DomainObject contextPersonObj = getContextPersonObject (context);

			MapList mlInboxTask = contextPersonObj.getRelatedObjects(context,//Logged in User context
					DomainConstants.RELATIONSHIP_PROJECT_TASK,//Relationship pattern
					DomainConstants.TYPE_INBOX_TASK,//Type pattern
					objectSelects,//object selects
					null,//rel where
					true,//object from
					false,//object to
					(short) 1,//exapand level
					strWhereExpression,//bus where
					DomainConstants.EMPTY_STRING);//rel select
			JsonArrayBuilder jsonArr = Json.createArrayBuilder();
			if (!mlInboxTask.isEmpty()) {
				
				//Code added for UR 22x release Defect Id - 38918 : Start
				for (int iInboxTaskList = 0; iInboxTaskList < mlInboxTask.size(); iInboxTaskList++) {
					mpSignupFormMap = (Hashtable) mlInboxTask.get(iInboxTaskList);
					if (mpSignupFormMap != null && !mpSignupFormMap.isEmpty()) {
						strReviewDate = (String)mpSignupFormMap.get("from["+STR_RELATIONSHIP_ROUTETASK+"].to.originated");				
						if (UIUtil.isNotNullAndNotEmpty(strReviewDate)) {				
							SimpleDateFormat formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
							Date tmpDate = formatter.parse(strReviewDate);
							formatter = new SimpleDateFormat("yyyy-MM-dd");	
							formatedDate = formatter.format(tmpDate);					
							mpSignupFormMap.put("from["+STR_RELATIONSHIP_ROUTETASK+"].to.originated", formatedDate);
						}
					}
				}//Code added for UR 22x release Defect Id - 38918 : End
				
				mlInboxTask.addSortKey(DomainConstants.SELECT_NAME,"ascending", "String");
				mlInboxTask.sort();
				jsonArr = getJsonArrayFromMapList (mlInboxTask);
			}		
			output.add("data",jsonArr.build());
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
		return output.build().toString();
	}
	/**
	 * @param context	logged in user's context 
	 * @return	DomainObject of context user's person person object
	 * @throws Exception when operation fails
	 */
	private static DomainObject getContextPersonObject (Context context) throws FrameworkException  {
		StringList slSelectable = new StringList();
		slSelectable.add(DomainConstants.SELECT_ID);
		String strContextUser = context.getUser();
		MapList mlPersonObj = DomainObject.findObjects(context,//Logged in User context
				DomainConstants.TYPE_PERSON,//Type pattern
				strContextUser,//namePattern
				SYMBOL_HYPHEN,//revPattern
				DomainConstants.QUERY_WILDCARD,//ownerPattern
				VAULT_ESERVICE_PRODUCTION,//vaultPattern
				null,//whereExpression
				false,//expandType
				slSelectable);//objectSelects
		String contextPersonId = (String) (((Map <?,?>) (mlPersonObj.get(0))).get(DomainConstants.SELECT_ID));
		return DomainObject.newInstance(context, contextPersonId);
	}	
	/**
	 * @param mlInboxTask	MapList of data containing data from enovia.
	 * @return JsonArrayBuilder having data from enovia in JSON array format.
	 * @throws Exception when operation fails
	 */
	private static JsonArrayBuilder getJsonArrayFromMapList ( MapList mlInboxTask ) {
		JsonObjectBuilder jsonObject = null;
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		Map<?, ?> objMap = null;
		String strKey ;
		String strValue;
		String strId ;
		//Code modified for 2018x.5 March Down Time Issue Defect # 39586 -Start
		String strRelatedObjectType = DomainConstants.EMPTY_STRING;
		String strRelatedObjectState = DomainConstants.EMPTY_STRING;
		String strRelatedObjectName = DomainConstants.EMPTY_STRING;
		String strRelatedObjectId = DomainConstants.EMPTY_STRING;
		//Code modified for 2018x.5 March Down Time Issue Defect # 39586 -End
		try {
		for (int i = 0; i < mlInboxTask.size(); i++) {
			jsonObject = Json.createObjectBuilder(); 
			objMap = (Map<?,?>)mlInboxTask.get(i);
			strId = checkNullValueforString((String)objMap.get(DomainConstants.SELECT_ID));
			//Code modified for 2018x.5 March Down Time Issue Defect # 39586 -Start
			if(!(objMap.get(REL_SELECT+DomainConstants.SELECT_TYPE) instanceof StringList)) {
				strRelatedObjectType = (String)objMap.get(REL_SELECT+DomainConstants.SELECT_TYPE);
			}
			if(!(objMap.get(REL_SELECT+DomainConstants.SELECT_CURRENT) instanceof StringList)) {
				strRelatedObjectState = (String)objMap.get(REL_SELECT+DomainConstants.SELECT_CURRENT);
			}
			if(!(objMap.get(REL_SELECT+DomainConstants.SELECT_NAME) instanceof StringList)) {
				strRelatedObjectName = (String)objMap.get(REL_SELECT+DomainConstants.SELECT_NAME);
			}
			if(!(objMap.get(REL_SELECT+DomainConstants.SELECT_ID) instanceof StringList)) {
				strRelatedObjectId = (String)objMap.get(REL_SELECT+DomainConstants.SELECT_ID);
			}
			if(UIUtil.isNotNullAndNotEmpty(strRelatedObjectType) && UIUtil.isNotNullAndNotEmpty(strRelatedObjectState)) {
				if(strRelatedObjectType.equals(TYPE_PGSIGNUPFORM) &&
							(strRelatedObjectState.equals(STATE_PGSIGNUPFORM_ACTIVE) || 
									strRelatedObjectState.equals(STATE_PGSIGNUPFORM_REVIEW))) {
			//Code modified for 2018x.5 March Down Time Issue Defect # 39586 -End
					jsonObject.add(JSON_OUTPUT_KEY_ID,strRelatedObjectId);	
					jsonObject.add(JSON_INBOX_TASK_OUTPUT_KEY_ID,strId);
					jsonObject.add(JSON_OUTPUT_KEY_NAME,checkNullValueforString(strRelatedObjectName));

					objMap.remove(DomainConstants.SELECT_NAME);
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
					//Code modified for 2018x.5 March Down Time Issue Defect # 39586 -Start
					strRelatedObjectType = DomainConstants.EMPTY_STRING;
					strRelatedObjectState = DomainConstants.EMPTY_STRING;
					strRelatedObjectId = DomainConstants.EMPTY_STRING;
					strRelatedObjectName = DomainConstants.EMPTY_STRING;
					//Code modified for 2018x.5 March Down Time Issue Defect # 39586 -End
				}
				
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArr;
	}
	/**
	 * @param strString parameter for json object.
	 * @return string if not null blank otherwise.
	 */
	private static String checkNullValueforString(  String strString )
	{
		if(strString!=null && strString.contains(MULIT_VAL_CHAR)) {
			strString=strString.replaceAll(MULIT_VAL_CHAR, " , ");
		}
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}
	/**
	 * @param context	logged in user's context 
	 *  @param strProcessCheck holds the value for task to be approved/rejected.
	 * @param strInboxTaskId holds the value Inbox Task Ids to be processed.
	 * @param session holds the value Http session for user password validation.
	 * @param strComment holds the value to update comment on Inbox Task.
	 * @param strUserName holds the value of logged in user's name.
	 * @param strPassword holds the value of logged in user's password.
	 * @return	String in JSON format, containing data extracted from enovia .
	 * @throws Exception when operation fails
	 */
	protected static String validateUserAndProcessTask(Context context,String strProcessCheck, String strInboxTaskId, HttpSession session,
			String strComment, String strUserName, String strPassword)  {

		JsonObjectBuilder jsonReturnObject = Json.createObjectBuilder(); 
		JsonObjectBuilder jsonObject = Json.createObjectBuilder(); 
		try {
			boolean bIsUserCredentialsValid = validateUserCredentials(context,session, strUserName, strPassword );
			if ( bIsUserCredentialsValid ) {
				String strInboxTaskCompletedIds = processInboxTask (context,strProcessCheck,strInboxTaskId, strComment );
				jsonObject.add(STATUS, strInboxTaskCompletedIds);
			} else {
				jsonObject.add(STATUS, INVALID_USER_MESSAGE);
			}
			jsonReturnObject.add("data",jsonObject.build());
		} catch ( Exception e) {
			e.printStackTrace();
		}
		return jsonReturnObject.build().toString();
	}

	/**
	 * @param context	logged in user's context 
	 *  @param strProcessCheck holds the value for task to be approved/rejected.
	 * @param strInboxTaskId holds the value Inbox Task Ids to be processed.
	 * @throws Exception when operation fails
	 */
	private static String processInboxTask(Context context, String strProcessCheck, String strInboxTaskIds, String strComment) {
		DomainObject doObjItask;
		Map<String, String> mpAttributeMap = new HashMap<>();
		StringBuilder sbStatus = new StringBuilder();
		try {
			if ( null != strInboxTaskIds && !strInboxTaskIds.equals(DomainConstants.EMPTY_STRING) ) {
				
				StringList slInboxTaskIds = StringUtil.split(strInboxTaskIds,",");
				for (int iCount = 0; iCount<slInboxTaskIds.size() ;iCount++) {
					try {
						doObjItask = DomainObject.newInstance (context, slInboxTaskIds.get(iCount));
						
							mpAttributeMap.put(ATTRIBUTE_TASKCOMMENTSNEEDED, STR_YES);
							mpAttributeMap.put(DomainConstants.ATTRIBUTE_COMMENTS, strComment);
							mpAttributeMap.put(ATTRIBUTE_APPROVALSTATUS, strProcessCheck);
							mpAttributeMap.put(ATTRIBUTE_REVIEWCOMMENTSNEEDED, STR_NO);

						doObjItask.setAttributeValues(context, mpAttributeMap);
						doObjItask.promote (context);
					} catch (Exception e) {
						sbStatus.append(e.getMessage());
						}
					}	
			}
		}  catch ( Exception e) {
			e.printStackTrace();
		}
		return sbStatus.toString(); 
	}

	/**
	 * @param context	logged in user's context 
	 * @param session holds the value Http session for user password validation.
	 * @param session holds the value Http session for user password validation.
	 * @param strUserName holds the value of logged in user's name.
	 * @return	boolean if user's password is valid or not  .
	 * @throws  Exception when operation fails
	 * @throws Exception when operation fails
	 */
	private static boolean validateUserCredentials(Context context, HttpSession session, String strUserName,
			String strPassword) throws  Exception {
		boolean bIsUserCredentialsValid = true;

		String passportURL = PropertyUtil.getEnvironmentProperty(context, "PASSPORT_URL");
		boolean is3DPassportServerInUse = (passportURL != null && passportURL.length() > 0);
		try {
			if ( null !=strUserName && strUserName.equals (context.getUser() )) {
				PersonUtil.checkFDAAuthentication(context, strUserName, strPassword, passportURL, is3DPassportServerInUse, session, false);
			} else {
				bIsUserCredentialsValid = false;
			}
		} catch (Exception er) {
			bIsUserCredentialsValid = false;
		}
		return bIsUserCredentialsValid;
	}
}