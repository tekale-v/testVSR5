package com.pg.widgets.route;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import com.custom.pg.Artwork.ArtworkConstants;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.awl.enumeration.AWLType;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.common.InboxTask;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.SubscriptionManager;
import com.matrixone.apps.common.UserTask;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MailUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.mxType;
import com.matrixone.apps.framework.ui.UIUtil;
import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectAttributes;
import matrix.db.ClientTask;
import matrix.db.ClientTaskItr;
import matrix.db.ClientTaskList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.SelectList;
import matrix.util.StringList;

import com.matrixone.apps.domain.util.eMatrixDateFormat;

public class PGRoute {

	private static final String FROM_REL_ROUTETASK		=	"from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.name";
	private static final String FROM_REL_PROJECT_TASK		=	"from["+DomainConstants.RELATIONSHIP_PROJECT_TASK+"].to.name";
	private static final String ATTR_TASK_INSTRUCTION 	=	"attribute["+DomainConstants.ATTRIBUTE_ROUTE_INSTRUCTIONS+"]";
	private static final String ATTR_TASK_DUE_DATE		=	"attribute["+DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE+"]";
	private static final String ATTR_TASK_APPROVAL_STATUS	=	"attribute["+DomainConstants.ATTRIBUTE_APPROVAL_STATUS+"]";	  

	private static final String ATTR_TITLE				=	"attribute["+DomainConstants.ATTRIBUTE_TITLE+"]";
	private static final String ATTR_COMMENTS				=	"attribute["+DomainConstants.ATTRIBUTE_COMMENTS+"]";
	private static final String ATTR_ROUTE_STATUS			=	"attribute["+DomainConstants.ATTRIBUTE_ROUTE_STATUS+"]";
	private static final String ATTR_ROUTE_ACTION  =  "attribute["+DomainConstants.ATTRIBUTE_ROUTE_ACTION+"]"; 
	private static String routeDuedate="" ;
	 private static final Logger logger = Logger.getLogger(PGRoute.class.getName());
	 	static final String JSON_OUTPUT_KEY_NAME = "NAME";
		static final String JSON_OUTPUT_KEY_STATE = "STATE";
		static final String JSON_OUTPUT_KEY_OWNER = "OWNER";
		static final String JSON_OUTPUT_KEY_TYPE = "TYPE";
		static final String JSON_OUTPUT_KEY_REVISION = "REVISION";
		static final String JSON_OUTPUT_KEY_ORIGINATED = "ORIGINATED";
		static final String TASK_ID_KEY = "taskId";
		
		static final String JSON_OUTPUT_KEY_ROUTEID = "RouteId";
		static final String JSON_OUTPUT_KEY_Type = "Type";
		static final String JSON_OUTPUT_KEY_Name = "Name";
		static final String JSON_OUTPUT_KEY_STATUS = "Status/ApprovalStatus";
		static final String JSON_OUTPUT_KEY_OWNERAPPROVER = "Owner/Approver";
		static final String JSON_OUTPUT_KEY_APPROVAL = "Approval/DueDate";
		static final String JSON_OUTPUT_KEY_COMMENT = "Comment/Instruction";
		static final String JSON_OUTPUT_KEY_ROUTE_ACTION = "Route Action";
		
		static final String JSON_OUTPUT_KEY_ACTION = "action";
		static final String JSON_OUTPUT_KEY_FALSE = "false";
		static final String JSON_OUTPUT_KEY_TRUE = "true";
		static final String JSON_OUTPUT_KEY_CURRENTSTATE = "currentState";
		
		static final String ROUTE_STATUS_STARTED = "Started";
		static final String ROUTE_STATUS_STOPPED = "Stopped";
		
		static final String TASK_APPROVAL_STATUS_APPROVE = "Approve";
		static final String TASK_APPROVAL_STATUS_REJECT = "Reject";
		static final String TASK_APPROVAL_STATUS_ABSTAIN = "Abstain";
		
		static final String IS_REJECT_ACTION = "isRejectAction";
		static final String IGNORE_REJECT_COMMENT = "ignoreRejectComments";
		
		static final String ACTION_COMPLETE = "Complete";
		static final String ACTION_APPROVE = "Approve";
		static final String ACTION_REJECT = "Reject";
		static final String ACTION_ABSTAIN = "Abstain";
		
		static final String IS_TASK_COMPLETED = "isTaskCompleted";
		static final String IS_ROUTE_STOPPED = "isRouteStopped";
		static final String APPROVAL_STATUS = "approvalStatus";
		static final String NONE = "None";
		static final String GET_COMMENT_FROM_TASK_ID = "getCommentsFromTaskId";
		static final String TRUE_VALUE = "true";
		
		static final String ESKO_STATUS_READY = "Ready";
		
		static final String SHOW_FDA_WINDOW = "showFDAWindow";
		static final String NOT_SHOW_FDA_WINDOW = "notShowFDAWindow";
		
		static final String BLN_OWNER = "blnOwner";
		static final String BLN_APPROVE = "blnApprove";
		static final String BLN_MATCH = "blnMatch";
		static final String BLN_REJECT = "blnReject";
		static final String BLN_MARKUP_DATA = "blnMarkupData";  
		static final String SHOW_COMMENTS_REQ_ALERT = "showCommentsReqAlert";  
		static final String SHOWFDA = "showFDA"; 
		
		
		static final String JSON_OUTPUT_KEY_IS_REJECT_ACTION = "isRejectAction";
		static final String JSON_OUTPUT_KEY_FLAG = "flag";
		static final String JSON_OUTPUT_KEY_BIFOWNER = "bIfOwner";
		static final String JSON_OUTPUT_KEY_IS_VALID_APPROVER = "isValidApprover";
		static final String JSON_OUTPUT_KEY_CAN_TASK_COMPLETED = "canTaskCompleted";
		static final String JSON_OUTPUT_KEY_IGNORE_REJECT_COMPLETED = "ignoreRejectComments";
		static final String JSON_OUTPUT_KEY_IS_TASK_COMPLETED = "isTaskCompleted";
		static final String JSON_OUTPUT_KEY_IS_FDA_WINDOW = "isFDAWindow";
		static final String JSON_OUTPUT_KEY_COMMENT_FLAG = "commentFlag";
		static final String JSON_OUTPUT_KEY_IS_ROUTE_STOPPED = "isRouteStopped";
		static final String JSON_OUTPUT_KEY_BLN_OWNER = "blnOwner";
		static final String JSON_OUTPUT_KEY_BLN_REJECT = "blnReject";
		static final String JSON_OUTPUT_KEY_BLN_MATCH = "blnMatch";
		static final String JSON_OUTPUT_KEY_BLN_MARKUP_DATA = "blnMarkupData";
		static final String JSON_OUTPUT_KEY_BLN_ESK_MATCH = "blnEskoMatch";
		static final String JSON_OUTPUT_KEY_SHOW_REVIEWER_COMMENT_REQ_ALERT = "showReviewerCommentsReqAlert";
		static final String JSON_OUTPUT_KEY_SHOW_COMMENT_REQ_ALERT = "showCommentsReqAlert";
		static final String JSON_OUTPUT_KEY_SHOW_FDA_WINDOW = "showFDAWindow";
		static final String JSON_OUTPUT_KEY_NOT_SHOW_FDA_WINDOW = "notShowFDAWindow";
		static final String JSON_OUTPUT_KEY_NOT_ROUTE_ID = "routeId";
		static final String JSON_OUTPUT_KEY_NOT_TASK_ID = "taskId";
		static final String JSON_OUTPUT_KEY_TASK_STATUS = "taskStatus";
		
		static final String GRANTOR = "grantor";
		static final String GRANTEE = "grantee";
		static final String GRANTEEACCESS = "granteeaccess";
		
		static final String TASK_ID = "TaskId";
		static final String OBJECT_ID = "objectId";
		
		static final String ATTR_REVIEWERS_COMMENTS               = PropertyUtil.getSchemaProperty(null, "attribute_ReviewersComments");
		static final String ATTR_REVIEW_COMMENTS_NEEDED         = PropertyUtil.getSchemaProperty(null, "attribute_ReviewCommentsNeeded");
		
		static final String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION =  "relationship["+PropertyUtil.getSchemaProperty(null,"relationship_pgAAA_EskoMarkupDataTopgPLIResponsibleFunction")+"]";
		static final String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON =  "relationship["+PropertyUtil.getSchemaProperty(null,"relationship_pgAAA_EskoMarkupDataTopgPLIReason")+"]";
		static final String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE =  "relationship["+PropertyUtil.getSchemaProperty(null,"relationship_pgAAA_EskoMarkupDataTopgPLIDefectType")+"]";
		static final String OWNER = "owner";
		static final String ROLE_STARTS_WITH = "role_";
		static final String ROUTE_TASK_USER = "routeTaskUser";
		
		static final String AUTHENTICATE_USER = "authenticateUser";
		static final String APP_INBOXTASK_EDIT_FORM = "APPInboxTaskEditForm";
		static final String FORM = "form";
		static final String PG_IS_REJECT_TASK_ACTION = "pgIsRejectTaskAction";
		static final String OPEN_AUTHENTICATE_PAGE = "OpenAuthenticatePage";
		static final String COMMENTS = "comments";
		static final String FAILURE = "Failure";
		static final String OFFSET_FROM_TASK_CREATE_DATE     = "Task Create Date";
		static final String NO_VALUE = "No";
		static final String YES_VALUE = "Yes";
		static final String COMMENT = "Comment";
		static final String NOTIFY_ONLY = "Notify Only";
		static final String ALL_VALUE = "All";
		static final String MQL_ADD_HISTORY_QUERY = "Modify bus $1 add history $2 comment $3";
		static final String APPROVE = "approve";
		static final String MQL_REVOKE_GRANTOR_GRANTEE = "modify bus $1 revoke grantor $2 grantee $3";
		
		static final String FLAG_FDA = "fda";
		static final String RETURN_BACK = "returnBack";
		static final String LANGUAGE_STR = "languageStr";
		static final String LOCALE_OBJ = "localeObj";
		static final String TIME_ZONE = "timeZone";
		static final String REVIEWER_COMMENTS = "ReviewerComments";
		static final String DUE_DATE = "DueDate";
		static final String ROUTE_TIME = "routeTime";
		static final String SUCESS_VALUE = "Success";
		static final String REQUEST_MAP = "requestMap";
		static final String USER_ID = "userId";
		static final String PASSWORD = "passwd";
		static final String PASSPORT_URL = "PASSPORT_URL";
		static final String IS_VERIFIED = "is_verified";
		static final String TASK_NAME = "name";
		static final String ARTWORK_REVISION = "-";
		static final String ARTWORK_NAME = "ArtworkConfigurationDetails";

	/**
	 * 
	 * Get Route info and connected tasks in a route
	 * @param context Enovia context object
	 * @param sObjectId objectId of route.
	 * @return response in JSON format
	 * @throws Exception
	 * 				when operation fails
	 */
	public static String getRouteInfo(Context context, String sObjectId) throws Exception
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		try
		{	
			DomainObject domObj = DomainObject.newInstance(context,sObjectId);

			StringList slParentSelects = new StringList(6); 
			slParentSelects.add(DomainConstants.SELECT_NAME);
			slParentSelects.add(DomainConstants.SELECT_OWNER);
			slParentSelects.add(DomainConstants.SELECT_CURRENT); 
			slParentSelects.add(DomainConstants.SELECT_TYPE);
			slParentSelects.add(DomainConstants.SELECT_REVISION);
			slParentSelects.add(DomainConstants.SELECT_ORIGINATED);

			Map<?,?> mpData  = (Map<?, ?>)domObj.getInfo(context,slParentSelects);			

			JsonObjectBuilder jsonParentInfo = Json.createObjectBuilder(); 
			jsonParentInfo.add(JSON_OUTPUT_KEY_NAME,(String)mpData.get(DomainConstants.SELECT_NAME));
			jsonParentInfo.add(JSON_OUTPUT_KEY_STATE,(String)mpData.get(DomainConstants.SELECT_CURRENT));
			jsonParentInfo.add(JSON_OUTPUT_KEY_OWNER,(String)mpData.get(DomainConstants.SELECT_OWNER));
			jsonParentInfo.add(JSON_OUTPUT_KEY_TYPE,(String)mpData.get(DomainConstants.SELECT_TYPE));
			jsonParentInfo.add(JSON_OUTPUT_KEY_REVISION,(String)mpData.get(DomainConstants.SELECT_REVISION));
			jsonParentInfo.add(JSON_OUTPUT_KEY_ORIGINATED,(String)mpData.get(DomainConstants.SELECT_ORIGINATED));	
		
			JsonArrayBuilder jsonArr = getConnectedRoutesAndTasks(context, sObjectId);			
			jsonParentInfo.add("data",jsonArr.build());
			output.add(sObjectId,jsonParentInfo);			
		}
		catch (Exception ex)
		{
			logger.log(Level.SEVERE,ex.getMessage(),ex);
			output.add("error",ex.getMessage());
		}
		return output.build().toString();
	}

	/**
	 * Fetch all the routes connected to object 
	 * @param context
	 * @param sObjectId
	 * @return response in JSON format
	 * @throws MatrixException
	 */
	private static JsonArrayBuilder getConnectedRoutesAndTasks(Context context, String sObjectId) throws MatrixException
	{
		JsonArrayBuilder jsonArr =  Json.createArrayBuilder();
		SelectList selectStmts = new SelectList();
		selectStmts.addName();
		selectStmts.addCurrentState();
		selectStmts.addId();
		selectStmts.addOwner();
		selectStmts.addType();
		selectStmts.addDescription();	
		
		selectStmts.add("attribute["+DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE+"]");
		selectStmts.add("attribute["+DomainConstants.ATTRIBUTE_ROUTE_STATUS+"]");
		
		MapList mlRouteList = Route.getRoutes(context, sObjectId, selectStmts, null, null, false);
		

		if(mlRouteList != null && !mlRouteList.isEmpty())
		{
			jsonArr = addRouteTaskDetails(context, mlRouteList);	
		}
		return jsonArr;
	}


	/**
	 * Fetch Inbox tasks of routes
	 * @param context enovia context object
	 * @param mlRouteList contains maps with details of route objects.Map contains details like TNR etc.
	 * @return  response in JSON format

	 * @throws MatrixException when operation fails
	 */
	private static JsonArrayBuilder addRouteTaskDetails(Context context, MapList mlRouteList) throws MatrixException
	{
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();	
				
		String routeId ;
		String routeType ;
		String routeOwner ;
		String routeStatus ;
		String routeName ;
		String routeduedate ;
		String routedescrition ;
		String routeCurrentState;
		String sTaskApprover  ;
		String sTaskDueDate  ;
		String sTaskType  ;
		String sTaskName  ;
		String sTaskApprovalStatus  ;
		String sTaskInstruction  ;
		String strTaskState  ;
		String contextUser  = context.getUser();
        String strRouteAction;
        
		String policyInboxTask =	PropertyUtil.getSchemaProperty(context, "policy_InboxTask");
		String stateTaskComplete =	FrameworkUtil.lookupStateName(context, policyInboxTask, "state_Complete");
        
		SelectList strTaskList = new SelectList();
		strTaskList.addName();
		strTaskList.addId();
		strTaskList.addCurrentState();
		strTaskList.add(FROM_REL_ROUTETASK);
		strTaskList.add(ATTR_TITLE);
		strTaskList.add(ATTR_COMMENTS);
		strTaskList.add(FROM_REL_PROJECT_TASK);
		strTaskList.add(ATTR_TASK_INSTRUCTION);
		strTaskList.add(ATTR_TASK_DUE_DATE);
		strTaskList.add(ATTR_TASK_APPROVAL_STATUS);

		strTaskList.add(ATTR_ROUTE_ACTION);
		

		MapList mlTasklist = null;
		JsonObjectBuilder jsonObject = null;
		Map<?, ?> routeMap = null;
		mlRouteList.addSortKey(DomainConstants.SELECT_NAME,"ascending", "String");
		mlRouteList.sort();

		Iterator<?> itr = mlRouteList.iterator();
		while(itr.hasNext()) 
		{
			
			jsonObject = Json.createObjectBuilder(); 
			routeMap = (Map<?, ?>)itr.next();
			routeId = (String)routeMap.get(DomainConstants.SELECT_ID);
			routeType = (String)routeMap.get(DomainConstants.SELECT_TYPE);
			routeOwner = (String)routeMap.get(DomainConstants.SELECT_OWNER);
			routeName = (String)routeMap.get(DomainConstants.SELECT_NAME);
			routeStatus = (String)routeMap.get(ATTR_ROUTE_STATUS);
			routeduedate=(String)routeMap.get(routeDuedate);
			routedescrition=(String)routeMap.get(DomainConstants.SELECT_DESCRIPTION);
			routeCurrentState = (String)routeMap.get(DomainConstants.SELECT_CURRENT);



			jsonObject.add(JSON_OUTPUT_KEY_ROUTEID,checkNullValueforString( context,  routeId));
			jsonObject.add(JSON_OUTPUT_KEY_Type, checkNullValueforString( context,routeType));
			jsonObject.add(JSON_OUTPUT_KEY_Name, checkNullValueforString( context,routeName));
			jsonObject.add(JSON_OUTPUT_KEY_STATUS, checkNullValueforString( context,routeStatus));
			jsonObject.add(JSON_OUTPUT_KEY_OWNERAPPROVER, checkNullValueforString( context,routeOwner));
			jsonObject.add(JSON_OUTPUT_KEY_APPROVAL, checkNullValueforString( context,routeduedate));
			jsonObject.add(JSON_OUTPUT_KEY_COMMENT, checkNullValueforString( context,routedescrition));
			jsonObject.add(JSON_OUTPUT_KEY_ACTION, JSON_OUTPUT_KEY_FALSE);
			jsonObject.add(JSON_OUTPUT_KEY_CURRENTSTATE, routeCurrentState);

			// if the Route Status is Stopped or Route is Completed then not required to fetch the Task info
			if(routeStatus!=null && routeStatus.equalsIgnoreCase(ROUTE_STATUS_STARTED) ) {
				Route routeObj = (Route)DomainObject.newInstance(context,DomainConstants.TYPE_ROUTE);
				routeObj.setId(routeId);
				mlTasklist = routeObj.getRouteTasks(context, strTaskList, null, null, false);	
	            
				// check for the status of the task.
				Map<?, ?> taskMap = null;
				if(mlTasklist!=null){
					JsonObjectBuilder jsonTaskObject = null;
					JsonArrayBuilder jsontaskarr=Json.createArrayBuilder(); 
					
					for(int j = 0, iTaskCount = mlTasklist.size(); j < iTaskCount; j++) 
					{
						jsonTaskObject = Json.createObjectBuilder();
						taskMap = (Map<?, ?>) mlTasklist.get(j);
						sTaskInstruction	=	(String)taskMap.get(ATTR_TASK_INSTRUCTION);
						sTaskApprover	=	(String)taskMap.get(FROM_REL_PROJECT_TASK);
						sTaskDueDate	=	(String)taskMap.get(ATTR_TASK_DUE_DATE);
						sTaskType	=	(String)taskMap.get(DomainConstants.SELECT_TYPE);
						sTaskName	=	(String) taskMap.get(DomainConstants.SELECT_NAME);
						strRouteAction =(String)taskMap.get("attribute["+DomainConstants.ATTRIBUTE_ROUTE_ACTION+"]"); 
						
						strTaskState  = (String)taskMap.get(DomainConstants.SELECT_CURRENT);					
						sTaskApprovalStatus = getTaskApprovalStatus(context, routeOwner, routeStatus, strTaskState, contextUser,taskMap);
						jsonTaskObject.add(TASK_ID, (String) taskMap.get(DomainConstants.SELECT_ID));
						jsonTaskObject.add(JSON_OUTPUT_KEY_Type, checkNullValueforString(context,sTaskType));
						jsonTaskObject.add(TASK_NAME, checkNullValueforString(context,sTaskName));
						jsonTaskObject.add(JSON_OUTPUT_KEY_STATUS, checkNullValueforString(context,sTaskApprovalStatus));
						jsonTaskObject.add(JSON_OUTPUT_KEY_OWNERAPPROVER, checkNullValueforString(context,sTaskApprover));
						jsonTaskObject.add(JSON_OUTPUT_KEY_APPROVAL, checkNullValueforString( context,sTaskDueDate));
						jsonTaskObject.add(JSON_OUTPUT_KEY_COMMENT, checkNullValueforString( context, sTaskInstruction));
						jsonTaskObject.add(JSON_OUTPUT_KEY_ROUTE_ACTION, checkNullValueforString(context,strRouteAction));
												
						if(contextUser.equalsIgnoreCase(sTaskApprover) && !strTaskState.equalsIgnoreCase(stateTaskComplete)) {
							jsonTaskObject.add(JSON_OUTPUT_KEY_ACTION, JSON_OUTPUT_KEY_TRUE);
							
						} else {
							jsonTaskObject.add(JSON_OUTPUT_KEY_ACTION, JSON_OUTPUT_KEY_FALSE);
						}						
						jsontaskarr.add(jsonTaskObject);
					}
					jsonObject.add("TASKInfo",jsontaskarr);
				}
			}else {
				jsonObject.add("TASKInfo",Json.createArrayBuilder());
			}
			jsonArr.add(jsonObject);	
		}
		return jsonArr;
	}
	
	/**
	 * Get task approval status
	 * @param context enovia context object
	 * @param routeOwner owner of the route object
	 * @param routeStatus status of the route
	 * @param strTaskState state of the task
	 * @param contextUser name of the logged in user
	 * @param taskMap map with details of the task
	 * @return task approval status approval status of task object
	 * @throws FrameworkException when operation fails
	 */
	private static String getTaskApprovalStatus(Context context, String routeOwner,
		String routeStatus, String strTaskState, String contextUser, Map<?, ?> taskMap) throws FrameworkException {
		final String TASK_COMPLETED = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.LifecycleTasks.Completed", context.getLocale());
		final String TASK_APPROVED =  EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Lifecycle.Approved", context.getLocale());
		final String TASK_REJECTED =  EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Lifecycle.Rejected", context.getLocale());
		final String TASK_ABSTAINED =  EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Lifecycle.Abstained", context.getLocale());
		final String TASK_NEEDS_REVIEW =  EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.Lifecycle.NeedsReview", context.getLocale());
		final String TASK_AWAITING_APPROVAL =  EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.LifecycleTasks.AwaitingApproval",context.getLocale());   
		final String TASK_PENDING =  EnoviaResourceBundle.getFrameworkStringResourceProperty(context,"emxFramework.LifecycleTasks.Pending",context.getLocale());
		final String ROUTE_STOPPED = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.LifecycleTasks.RouteStopped", context.getLocale());

		String policyInboxTask =	PropertyUtil.getSchemaProperty(context, "policy_InboxTask");
		String stateTaskComplete =	FrameworkUtil.lookupStateName(context, policyInboxTask, "state_Complete");
		String stateTaskReview =	FrameworkUtil.lookupStateName(context, policyInboxTask, "state_Review");
		String sTaskApprovalStatus="";
		sTaskApprovalStatus	=	(String) taskMap.get(ATTR_TASK_APPROVAL_STATUS);
		if (stateTaskComplete.equalsIgnoreCase(strTaskState)) 
		{
			if (TASK_APPROVAL_STATUS_APPROVE.equals(sTaskApprovalStatus))
			{
				sTaskApprovalStatus = TASK_APPROVED;
			}
			else if (TASK_APPROVAL_STATUS_REJECT.equals(sTaskApprovalStatus)) 
			{
				sTaskApprovalStatus = TASK_REJECTED;
			}
			else if (TASK_APPROVAL_STATUS_ABSTAIN.equals(sTaskApprovalStatus)) 
			{ 
				sTaskApprovalStatus=TASK_ABSTAINED;
			}
			else
			{
				sTaskApprovalStatus=TASK_COMPLETED;
			}

		}
		else if(stateTaskReview.equals(strTaskState))
		{
			sTaskApprovalStatus=TASK_NEEDS_REVIEW;
		} 
		else 
		{

			if (ROUTE_STATUS_STOPPED.equals(routeStatus)) 
			{
				sTaskApprovalStatus=ROUTE_STOPPED;
			}
			else 
			{
				if(UIUtil.isNotNullAndNotEmpty(routeOwner) && routeOwner.equals(contextUser))
				{
					sTaskApprovalStatus=TASK_AWAITING_APPROVAL;
				}
				else
				{
					sTaskApprovalStatus=TASK_PENDING;
				}
			}
		}
		return sTaskApprovalStatus;
	}

	/**
	 * Check string if null 
	 * @param context
	 * @param strValue
	 * @return empty string
	 * 
	 */
	private static String checkNullValueforString( Context context , String strValue ) 
	{
		return null != strValue ? strValue : DomainConstants.EMPTY_STRING;
	}
	
	/**
	 * Get the latest connected tasks with its details
	 * @param context enovia context object
	 * @param theMepAttributesMap Map with details of MEPs
	 * @return JSON object string consisting of the information to be displayed
	 * @throws Exception when operation fails
	 */
		public static  String getApproveRejecttask(Context context,Map<?, ?> theMepAttributesMap) throws Exception {
		HashMap<?,?> programMap = (HashMap <?,?>)theMepAttributesMap;
		String objectId = (String) programMap.get(TASK_ID_KEY);	
		String action	=	(String) programMap.get(JSON_OUTPUT_KEY_ACTION);
		Json.createArrayBuilder();
	
		HashMap<Object, Object> hmParam = new HashMap<>();
		boolean isRejectAction = false;
		boolean ignoreRejectComments = false;
	
		InboxTask taskObject	=	(InboxTask)DomainObject.newInstance(context, objectId);
		BusinessObjectAttributes boAttrGeneric	=	taskObject.getAttributes(context);
		new AttributeItr(boAttrGeneric.getAttributes());
		
		boolean canTaskCompleted  				= false;
		boolean isTaskCompleted  				= false;
		boolean showCommentsReqAlert			= false;
		boolean showReviewerCommentsReqAlert	= false;
		boolean showFDAWindow					= false;
		boolean notShowFDAWindow				= false;
		boolean isFDAWindow					    = false;
		boolean isRouteStopped					    = false;
		//Code modification for PG AAA customization Starts //
		boolean blnOwner = false;
		boolean blnApprove = false;
		boolean blnReject = false;
		boolean blnMatch = false;
		boolean blnMarkupData = false;
		boolean blnEskoMatch = false;
		boolean hasRole							= true;
		String strVersionId = DomainConstants.EMPTY_STRING;
		
		String strCurrentUser = context.getUser();
		boolean commentFlag = true;
		HashMap<String, String> taskMapObj = new HashMap<String, String>();
		taskMapObj.put(OBJECT_ID,objectId);
		
	
		Boolean isValidApprover = JPO.invoke(context, "pgDSOUtil", null, "isTaskApproverSameAsCAOriginator", JPO.packArgs(taskMapObj),Boolean.class);
		// IVU Modified to override the OOTB behavior
	
		hmParam.put(JSON_OUTPUT_KEY_IS_VALID_APPROVER, isValidApprover);
		boolean flag=false;
		try{
			ContextUtil.pushContext(context);
			flag = true;
			canTaskCompleted = taskObject.canCompleteTask(context);
		} finally {
			if(flag)
				ContextUtil.popContext(context);
		}
		hmParam.put(JSON_OUTPUT_KEY_FLAG, flag);  
		hmParam.put(JSON_OUTPUT_KEY_CAN_TASK_COMPLETED,canTaskCompleted);
	
		if(canTaskCompleted) {
	
	
			final   String SELECT_ROUTE_ID              	= "from[" + DomainConstants.RELATIONSHIP_ROUTE_TASK + "].to.id";
			final	String SELECT_TASK_ASSIGNEE_NAME    	= "from[" + DomainConstants.RELATIONSHIP_PROJECT_TASK + "].to.name";
			final	String SELECT_ROUTE_ACTION          	= "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_ACTION + "]";
			final	String SELECT_ROUTE_APPROVAL_STATUS 	= "attribute[" + DomainConstants.ATTRIBUTE_APPROVAL_STATUS + "]";
			final	String SELECT_TASK_COMMENTS         	= "attribute[" + DomainConstants.ATTRIBUTE_COMMENTS + "]";
			final	String SELECT_REVIEW_COMMENTS       	= "attribute["+ATTR_REVIEWERS_COMMENTS+"]";
			final   String SELECT_REVIEW_COMMENTS_NEEDED	= "attribute["+ATTR_REVIEW_COMMENTS_NEEDED+"]";
			final   String SELECT_REVIEW_TASK	= "attribute[" + DomainConstants.ATTRIBUTE_REVIEW_TASK + "]";
			final	String SELECT_ROUTE_STATUS              	= "from[" + DomainConstants.RELATIONSHIP_ROUTE_TASK + "].to.attribute[" + DomainConstants.ATTRIBUTE_ROUTE_STATUS + "]";
			
			StringList taskSelectList = new StringList(11);
			taskSelectList.add(SELECT_ROUTE_ID);
			taskSelectList.add(SELECT_TASK_ASSIGNEE_NAME);
			taskSelectList.add(InboxTask.SELECT_ROUTE_TASK_USER);
			taskSelectList.add(SELECT_ROUTE_ACTION);
			taskSelectList.add(SELECT_ROUTE_APPROVAL_STATUS);
			taskSelectList.add(SELECT_TASK_COMMENTS);
			taskSelectList.add(DomainConstants.SELECT_CURRENT);
			taskSelectList.add(DomainConstants.SELECT_OWNER);
			taskSelectList.add(SELECT_REVIEW_COMMENTS);
			taskSelectList.add(SELECT_REVIEW_COMMENTS_NEEDED);
			taskSelectList.add(SELECT_ROUTE_STATUS);
	
			ContextUtil.pushContext(context);
			boolean bIsContextPushed = true;
			Map<?, ?> taskMap;
			try {
				taskMap = taskObject.getInfo(context,taskSelectList);
			}finally {
				if(bIsContextPushed) {
					ContextUtil.popContext(context);
					bIsContextPushed = false;
				}
			}
			
			String strRouteId        = (String)taskMap.get(SELECT_ROUTE_ID );
		String strCurrentState   = (String)taskMap.get(DomainConstants.SELECT_CURRENT );
		taskMap.get(SELECT_ROUTE_APPROVAL_STATUS );
		String comments          = (String)taskMap.get(SELECT_TASK_COMMENTS );
		taskMap.get(SELECT_TASK_ASSIGNEE_NAME );
		String sTaskOwner        = (String)taskMap.get(DomainConstants.SELECT_OWNER );
	    taskMap.get(SELECT_REVIEW_COMMENTS_NEEDED);
		String strReviewComments = (String)taskMap.get(SELECT_REVIEW_COMMENTS);
		String strReviewTask = (String)taskMap.get(SELECT_REVIEW_TASK);
			String strRouteStatus = (String)taskMap.get(SELECT_ROUTE_STATUS);
			String strRouteTaskUser = (String)taskMap.get(InboxTask.SELECT_ROUTE_TASK_USER);
			String strRouteAction = (String)taskMap.get(SELECT_ROUTE_ACTION);
			if(COMMENT.equals(strRouteAction)) {
				showReviewerCommentsReqAlert = true;
			}
	
		//Due to Resume Process implementation there can be tasks which are not connected to route and hence we cannot find
		//the route id from these tasks. Then the route id can be found by first finding the latest revision of the task
		//and then querying for the route object.
			if (strRouteId == null) {
				DomainObject domObjRouteLastRevision = DomainObject.newInstance(context, taskObject.getLastRevision(context));
				strRouteId = domObjRouteLastRevision.getInfo(context, SELECT_ROUTE_ID);
			}
	
			String showComments          = EnoviaResourceBundle.getProperty(context,"emxComponents.Routes.ShowCommentsForTaskApproval");
			String ignoreComments        = EnoviaResourceBundle.getProperty(context,"emxComponentsRoutes.InboxTask.IgnoreComments");
			String isFDAEnabled 		 = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.EnableFDA");
			String stateComplete 		 = PropertyUtil.getSchemaProperty(context, "policy", DomainConstants.POLICY_INBOX_TASK, "state_Complete");
	
			showComments = UIUtil.isNullOrEmpty(showComments) ? JSON_OUTPUT_KEY_TRUE : showComments;
			
			ignoreComments = UIUtil.isNullOrEmpty(ignoreComments) ? JSON_OUTPUT_KEY_FALSE : ignoreComments;
	
			boolean isCommentsRequired = JSON_OUTPUT_KEY_TRUE.equalsIgnoreCase(showComments);
			// V2 and V3 Team Modified (Declared outside if) for V62014x.4 for Defect 3478 -- START
			ignoreRejectComments = JSON_OUTPUT_KEY_TRUE.equalsIgnoreCase(ignoreComments);
			// V2 and V3 Team Modified (Declared outside if) for V62014x.4 for Defect 3478 -- END
			boolean hasComments = UIUtil.isNotNullAndNotEmpty(comments);
	  
			isTaskCompleted = (stateComplete.equals(strCurrentState));
	
			boolean isCompleteAction = ACTION_COMPLETE.equals(action);
			boolean isApproveAction = ACTION_APPROVE.equals(action);
			isRejectAction = ACTION_REJECT.equals(action);
			boolean isAbstainAction = ACTION_ABSTAIN.equals(action);
		
			isRouteStopped = ROUTE_STATUS_STOPPED.equals(strRouteStatus);
	
	
			hmParam.put(IS_REJECT_ACTION, isRejectAction);
			hmParam.put(IGNORE_REJECT_COMMENT, ignoreRejectComments); 
	
	
			blnApprove = ACTION_APPROVE.equals(action);
			blnReject = ACTION_REJECT.equals(action);
	
			try {
				EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource",context.getLocale(),"emxComponents.Route.ShowAlertMessage");	
				HashMap<String, String> argMap = new HashMap<>();
				argMap.put(OBJECT_ID, strRouteId);
				argMap.put(OWNER, sTaskOwner);				
				Boolean bIfOwner = JPO.invoke(context,"pgVT_Util",null,"checkIsOwnerOnlyApproverForSignatureReferenceDocs",JPO.packArgs(argMap),Boolean.class);				
				hmParam.put(JSON_OUTPUT_KEY_BIFOWNER, bIfOwner);
	
			}
			catch(Exception e) {
				logger.log(Level.SEVERE,e.getMessage(),e);				
			}
	
			if(isRouteStopped) {
				showCommentsReqAlert = false;
			}
			else if(isCompleteAction) {
				showCommentsReqAlert = !hasComments;
				hmParam.put(JSON_OUTPUT_KEY_NOT_ROUTE_ID, strRouteId);
				hmParam.put(JSON_OUTPUT_KEY_NOT_TASK_ID, objectId);
				hmParam.put(APPROVAL_STATUS,NONE);
				hmParam.put(GET_COMMENT_FROM_TASK_ID,TRUE_VALUE);
	
			} else if(isApproveAction || isRejectAction || isAbstainAction) {
	
				DomainObject domObjRouteContent =  DomainObject.newInstance(context,objectId);				
				String strObjectId = domObjRouteContent.getInfo(context,"from["+AWLRel.ROUTE_TASK.get(context)+"].to.to["+AWLRel.OBJECT_ROUTE.get(context)+"].from."+DomainObject.SELECT_ID);
				domObjRouteContent.setId(strObjectId);
				String strObjectType = domObjRouteContent.getTypeName();
				String strObjectCurrent = domObjRouteContent.getInfo(context,DomainObject.SELECT_CURRENT);
				StringList strSelectable=new StringList(DomainObject.SELECT_ID);
				strSelectable.add(DomainObject.SELECT_TYPE);
				strSelectable.add(DomainObject.SELECT_NAME);				
				if(BusinessUtil.isNotNullOrEmpty(strObjectType) && (strObjectType.equalsIgnoreCase(AWLType.POA.get(context)) || strObjectType.equalsIgnoreCase(ArtworkConstants.TYPE_CIC))){
					hasComments=true;
					commentFlag=false;
				}
				showCommentsReqAlert = ((isApproveAction || isAbstainAction) && isCommentsRequired && !hasComments)  || (isRejectAction && !ignoreRejectComments && !hasComments);
	
				String STR_ATTRIBUTE_PGAAA_ESKOTYPES = PropertyUtil.getSchemaProperty(context,"attribute_pgAAA_EskoTypes");
				String strEskoType = "attribute["+STR_ATTRIBUTE_PGAAA_ESKOTYPES+"]";
	
				BusinessObject bObjConfigurationObject = new BusinessObject(ArtworkConstants.TYPE_PG_CONFIGURATION_ADMIN,ARTWORK_NAME,ARTWORK_REVISION,ArtworkConstants.VAULT_ESERVICE_PRODUCTION);
				DomainObject dobjConfigurationObject = DomainObject.newInstance(context, bObjConfigurationObject); 
				if(dobjConfigurationObject.exists(context)) {	
					String strConfigAttributeValue = dobjConfigurationObject.getInfo(context,strEskoType);
					StringList slConfigEskoType = FrameworkUtil.split(strConfigAttributeValue, ",");
					StringList slActualNameEskoType = new StringList();
					StringList slActualNameEskoTypeStates = new StringList();
					for(int i=0; i<slConfigEskoType.size(); i++) {
						String strAttributeEskoType =  slConfigEskoType.get(i);
						StringList strAttributeValueEsko = FrameworkUtil.split(strAttributeEskoType, ":");
						String strActualEskoType = "";
						String strActualEskoPolicy = "";
						String strActualEskoState = "";
						if(strAttributeValueEsko.size()==3) {
							for (int j=0;j<strAttributeValueEsko.size(); j++) {
								String strValueEskoType = strAttributeValueEsko.get(0);
								String strValueEskoState =  strAttributeValueEsko.get(1);
								String strValueEskoPolicy =  strAttributeValueEsko.get(2);
								strActualEskoType = PropertyUtil.getSchemaProperty(context,strValueEskoType);
								strActualEskoPolicy = PropertyUtil.getSchemaProperty(context,strValueEskoPolicy);
								strActualEskoState = PropertyUtil.getSchemaProperty(context, "Policy", strActualEskoPolicy, strValueEskoState);
							}
						}
						slActualNameEskoType.add(strActualEskoType);
						slActualNameEskoTypeStates.add(strActualEskoState);
					}
					boolean blnEskoTypeState = getStateType(slActualNameEskoType, slActualNameEskoTypeStates, strObjectType, strObjectCurrent);
	
					if(blnEskoTypeState) {
						blnMatch = true;
	
						if (UIUtil.isNotNullAndNotEmpty(strObjectType) && AWLType.POA.get(context).equals(strObjectType)) {
							domObjRouteContent.setId(strObjectId);
							MapList mlArtworkFile=domObjRouteContent.getRelatedObjects(context, //Context context
									DomainConstants.RELATIONSHIP_PART_SPECIFICATION, //String relationshipPattern
									AWLType.ARTWORK_FILE.get(context), //String typePattern
									strSelectable, // StringList objectSelects
									null,  // StringList relationshipSelects
									false, //boolean getTo
									true, // boolean getFrom
									(short)1,  //short recurseToLevel
									"",   //String objectWhere
									"", //String relationshipWhere
									0);// int limit
							if(!mlArtworkFile.isEmpty()) { 
								strObjectId=(String) ((Map<?, ?>) mlArtworkFile.get(0)).get(DomainConstants.SELECT_ID);
							}
						}
						DomainObject dobjDocument=DomainObject.newInstance(context, strObjectId);
						MapList mlVersion= dobjDocument.getRelatedObjects(context,      //Context context
								PropertyUtil.getSchemaProperty(context,"relationship_ActiveVersion"), //String relationshipPattern
								"*", //String typePattern
								new StringList(DomainConstants.SELECT_ID),  // StringList objectSelects
								null,   // StringList relationshipSelects
								false,  //boolean getTo
								true,      // boolean getFrom
								(short)1, //short recurseToLevel
								"",    //String objectWhere
								"",   //String relationshipWhere
								0 ); // int limit
						if(!mlVersion.isEmpty()) { 
							for(int m=0;m<mlVersion.size();m++) {
								strVersionId=(String) ((Map<?, ?>) mlVersion.get(m)).get(DomainConstants.SELECT_ID);
								StringList strlEksoSelect = new StringList(DomainConstants.SELECT_ID);
								strlEksoSelect.add("attribute["+PropertyUtil.getSchemaProperty(context,"attribute_EskoViewableStatus")+"]");
								DomainObject dobEsko=DomainObject.newInstance(context, strVersionId);
								MapList  mlEsko = dobEsko.getRelatedObjects(context,  //Context context
										PropertyUtil.getSchemaProperty(context,"relationship_EskoViewableOf"), //String relationshipPattern
										PropertyUtil.getSchemaProperty(context,"type_EskoViewable"), //String typePattern
										strlEksoSelect, // StringList objectSelects
										null,   // StringList relationshipSelects
										false,   //boolean getTo
										true,     // boolean getFrom
										(short)1, //short recurseToLevel
										"",    //String objectWhere
										"",   //String relationshipWhere
										0 ); // int limit
								if(!mlEsko.isEmpty()) {
									String strEskoViewableId = (String) ((Map<?, ?>) mlEsko.get(0)).get(DomainConstants.SELECT_ID);
									StringList strSelectObject=new StringList();
									String strEskoStatus=(String) ((Map<?, ?>) mlEsko.get(0)).get("attribute["+PropertyUtil.getSchemaProperty(context,"attribute_EskoViewableStatus")+"]");
									if(ESKO_STATUS_READY.equals(strEskoStatus)) {
										blnEskoMatch = true;
										strSelectObject.add(DomainConstants.SELECT_ID);
										strSelectObject.add(DomainConstants.SELECT_OWNER);
										strSelectObject.add(RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION);
										strSelectObject.add(RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON);
										strSelectObject.add(RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE);
										DomainObject dobEskoMarkup = DomainObject.newInstance(context,strEskoViewableId);
										MapList mlEskoMarkupData = dobEskoMarkup.getRelatedObjects(context,  //Context context
												PropertyUtil.getSchemaProperty(context,"relationship_pgAAAEskoViewableToMarkup"),//String relationshipPattern
												PropertyUtil.getSchemaProperty(context,"type_pgAAA_EskoMarkupData"),//String typePattern
												strSelectObject , // StringList objectSelects
												null,   // StringList relationshipSelects
												false,  //boolean getTo
												true,     // boolean getFrom
												(short)1,  //short recurseToLevel
												"", //String objectWhere
												"",  //String relationshipWhere
												0 );// int limit
										if (!mlEskoMarkupData.isEmpty()) {
											for(int k=0; k<mlEskoMarkupData.size() ;k++) {
												String strMarkUpDataOwner = (String) ((Map<?, ?>) mlEskoMarkupData.get(k)).get(DomainConstants.SELECT_OWNER);
												String strRelEskoToResponsibleFunction = (String) ((Map<?, ?>) mlEskoMarkupData.get(k)).get(RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION);
												String strRelEskoToReason = (String) ((Map<?, ?>) mlEskoMarkupData.get(k)).get(RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON);
												String strRelEskoToDefectType = (String) ((Map<?, ?>) mlEskoMarkupData.get(k)).get(RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE);
												if(strMarkUpDataOwner.equals(strCurrentUser)) {
													blnOwner = true;
													if ((strRelEskoToResponsibleFunction!=null && JSON_OUTPUT_KEY_FALSE.equalsIgnoreCase(strRelEskoToResponsibleFunction)) || (strRelEskoToReason!=null && "false".equalsIgnoreCase(strRelEskoToReason)) || (strRelEskoToDefectType!=null && "false".equalsIgnoreCase(strRelEskoToDefectType))) {
														blnMarkupData = true;
														break;
													}
												}
											}
										}
									}
								}
								if(blnEskoMatch) {
									break;
								}
							}		
						}
						showFDAWindow = !showCommentsReqAlert && JSON_OUTPUT_KEY_TRUE.equalsIgnoreCase(isFDAEnabled) && ((!blnOwner && blnApprove) || (blnOwner && blnReject));
						isFDAWindow   = JSON_OUTPUT_KEY_TRUE.equalsIgnoreCase(isFDAEnabled) && showCommentsReqAlert;
					}
				}
	
				if(!blnMatch || !blnEskoMatch) {
	
					showFDAWindow = !showCommentsReqAlert && JSON_OUTPUT_KEY_TRUE.equalsIgnoreCase(isFDAEnabled);
	
				}
	
				new StringBuffer();
	
	
				hasRole  = PersonUtil.hasAssignment(context,
						PropertyUtil.getSchemaProperty(context, strRouteTaskUser));
				String isResponsibleRoleEnabled = DomainConstants.EMPTY_STRING;
				try{
					isResponsibleRoleEnabled = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.ResponsibleRoleForSignatureMeaning.Preserve");
					if(UIUtil.isNotNullAndNotEmpty(isFDAEnabled) && isFDAEnabled.equalsIgnoreCase(JSON_OUTPUT_KEY_TRUE) && UIUtil.isNotNullAndNotEmpty(isResponsibleRoleEnabled) && isResponsibleRoleEnabled.equalsIgnoreCase(JSON_OUTPUT_KEY_TRUE)
							&& UIUtil.isNotNullAndNotEmpty(strRouteTaskUser) && strRouteTaskUser.startsWith(ROLE_STARTS_WITH))
					{
						if(hasRole)
							hmParam.put(ROUTE_TASK_USER,strRouteTaskUser);
						else
						{
							showFDAWindow = false;
							notShowFDAWindow = true;
						}
					}
				}
				catch(Exception e)
				{
					isResponsibleRoleEnabled = JSON_OUTPUT_KEY_FALSE;
					logger.log(Level.SEVERE,e.getMessage(),e);	
				}
				hmParam.put(JSON_OUTPUT_KEY_NOT_ROUTE_ID, strRouteId);
				hmParam.put(JSON_OUTPUT_KEY_NOT_TASK_ID, objectId);
				hmParam.put(GET_COMMENT_FROM_TASK_ID,TRUE_VALUE);
			} 
		}
		hmParam.put(JSON_OUTPUT_KEY_ACTION, action);
	
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		if(hmParam!=null) 
		{					
			jsonObject.add(JSON_OUTPUT_KEY_IS_REJECT_ACTION,(Boolean) hmParam.get(JSON_OUTPUT_KEY_IS_REJECT_ACTION));
			jsonObject.add(JSON_OUTPUT_KEY_FLAG,(Boolean) hmParam.get(JSON_OUTPUT_KEY_FLAG));
			jsonObject.add(JSON_OUTPUT_KEY_ACTION,(String) hmParam.get(JSON_OUTPUT_KEY_ACTION));
			jsonObject.add(JSON_OUTPUT_KEY_IS_VALID_APPROVER,(Boolean) hmParam.get(JSON_OUTPUT_KEY_IS_VALID_APPROVER));
			jsonObject.add(JSON_OUTPUT_KEY_CAN_TASK_COMPLETED,(Boolean) hmParam.get(JSON_OUTPUT_KEY_CAN_TASK_COMPLETED));
			jsonObject.add(JSON_OUTPUT_KEY_IGNORE_REJECT_COMPLETED,(Boolean) hmParam.get(JSON_OUTPUT_KEY_IGNORE_REJECT_COMPLETED));
			jsonObject.add(JSON_OUTPUT_KEY_IS_TASK_COMPLETED, isTaskCompleted);
			jsonObject.add(JSON_OUTPUT_KEY_IS_FDA_WINDOW, isFDAWindow);
			jsonObject.add(JSON_OUTPUT_KEY_COMMENT_FLAG, commentFlag);
			jsonObject.add(JSON_OUTPUT_KEY_IS_ROUTE_STOPPED, isRouteStopped);
			jsonObject.add(JSON_OUTPUT_KEY_BLN_OWNER, blnOwner);
			jsonObject.add(JSON_OUTPUT_KEY_BLN_REJECT, blnReject);
			jsonObject.add(JSON_OUTPUT_KEY_BLN_MATCH, blnMatch);
			jsonObject.add(JSON_OUTPUT_KEY_BLN_MARKUP_DATA, blnMarkupData);
			jsonObject.add(JSON_OUTPUT_KEY_BLN_ESK_MATCH, blnEskoMatch);
			jsonObject.add(JSON_OUTPUT_KEY_SHOW_REVIEWER_COMMENT_REQ_ALERT, showReviewerCommentsReqAlert);
			jsonObject.add(JSON_OUTPUT_KEY_SHOW_COMMENT_REQ_ALERT, showCommentsReqAlert);
			jsonObject.add(JSON_OUTPUT_KEY_SHOW_FDA_WINDOW, showFDAWindow);
			jsonObject.add(JSON_OUTPUT_KEY_NOT_SHOW_FDA_WINDOW, showCommentsReqAlert);
			jsonObject.add(JSON_OUTPUT_KEY_NOT_ROUTE_ID,(String) hmParam.get(JSON_OUTPUT_KEY_NOT_ROUTE_ID));
			jsonObject.add(JSON_OUTPUT_KEY_NOT_TASK_ID,(String) hmParam.get(TASK_ID_KEY));
			jsonObject.add(JSON_OUTPUT_KEY_TASK_STATUS,(String) hmParam.get(JSON_OUTPUT_KEY_ACTION));			
		}
		return jsonObject.build().toString();
	}

	/**
	 * Get state and type 
	 * @param slActualNameEskoType name of the object
	 * @param slActualNameEskoTypeStates all states of the object
	 * @param strObjectType Type of the enovia object
	 * @param strObjectCurrent current state of the enovia object
	 * @return
	 */
	private static boolean getStateType(StringList slActualNameEskoType, StringList slActualNameEskoTypeStates, String strObjectType, String strObjectCurrent)  {
		boolean blnReturn = false;
		String strEskoTypeValue ="";
		String strActualEskoTypeState = "";
		for (int l=0; l<slActualNameEskoType.size(); l++) {
			strEskoTypeValue = slActualNameEskoType.get(l);
			if(strEskoTypeValue.equals(strObjectType)) {
				strActualEskoTypeState =  slActualNameEskoTypeStates.get(l);
				if(strActualEskoTypeState.equals(strObjectCurrent)) {
					blnReturn = true;
					break;
				}
			}
		}
		return blnReturn;
	}

	/**
	 * Get all complete tasks and update task details
	 * @param context enovia context object
	 * @param theMepAttributesMap map with details of the MEP
	 * @return name of the completed task
	 * @throws Exception when operation fails
	 */
	public static String getCompleteTask(Context context,Map<?, ?> theMepAttributesMap) throws Exception{
		HashMap<?,?> programMap = (HashMap <?,?>)theMepAttributesMap;
	
		String sgetCommentsFromTaskId=(String) programMap.get(GET_COMMENT_FROM_TASK_ID);
		boolean getCommentsFromTaskId=false;
		if(TRUE_VALUE.equals(sgetCommentsFromTaskId)) {
			getCommentsFromTaskId=true;
		}
	
		String taskId = (String) programMap.get(TASK_ID_KEY);	
		String routeId=(String) programMap.get(JSON_OUTPUT_KEY_NOT_ROUTE_ID);
		String taskStatus=(String) programMap.get(JSON_OUTPUT_KEY_TASK_STATUS);
		String flag = (String) programMap.get(JSON_OUTPUT_KEY_FLAG);
		String showFDA = (String) programMap.get(SHOWFDA);
		String comments=(String) programMap.get(COMMENTS);		
		String reviewcomments="";
		String sReturn = FAILURE;
	
		try{
	
			HashMap<Object , Object> hmParam = new HashMap<>();
			String i18NReadAndUnderstand =EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", context.getLocale(),
					"emxFramework.UserAuthentication.ReadAndUnderstand");
			String strApprovalStatusAttr = PropertyUtil.getSchemaProperty(context, "attribute_ApprovalStatus" );			
			String strLanguage = context.getSession().getLanguage();
			Locale strLocale = context.getLocale();
	
			Route route = (Route)DomainObject.newInstance(context,  routeId);
			Person person = new Person();
			DomainObject task = DomainObject.newInstance(context,  taskId);
	
			if(getCommentsFromTaskId && (comments == null || "".equals(comments))) {
				comments = task.getInfo(context, "attribute[" + DomainConstants.ATTRIBUTE_COMMENTS + "]");
			}
	
			String attributeBracket = "attribute[";
			String closeBracket = "]";
			DomainRelationship domainRel            = null;
			String attrReviewCommentsNeeded         = PropertyUtil.getSchemaProperty(context, "attribute_ReviewCommentsNeeded");
			String sAttParallelNodeProcessonRule    = PropertyUtil.getSchemaProperty(context, "attribute_ParallelNodeProcessionRule");
			String attrTaskCommentsNeeded   = PropertyUtil.getSchemaProperty(context, "attribute_TaskCommentsNeeded");
			String strRouteActionAttr         = PropertyUtil.getSchemaProperty(context, "attribute_RouteAction" );
			String SELECT_ROUTE_ACTION          = "attribute[" + strRouteActionAttr + "]";
			boolean bTaskCommentGiven               = false;
			boolean returnBack                      = false;
			boolean isProjectSpace                  = false;
	
			String routeTime                        = "";
			String taskScheduledDate  = "";
	
			StringBuilder sAttrComments = new StringBuilder(attributeBracket);
			sAttrComments.append(DomainConstants.ATTRIBUTE_COMMENTS);
			sAttrComments.append(closeBracket);
			StringBuilder sAttrReviewTask =new StringBuilder(attributeBracket);
			sAttrReviewTask.append(DomainConstants.ATTRIBUTE_REVIEW_TASK);
			sAttrReviewTask.append(closeBracket);
			StringBuilder sAttrRouteNodeId =new StringBuilder(attributeBracket);
			sAttrRouteNodeId.append(DomainConstants.ATTRIBUTE_ROUTE_NODE_ID);
			sAttrRouteNodeId.append(closeBracket);
			StringBuilder sAttrApprovalStatus =new StringBuilder(attributeBracket);
			sAttrApprovalStatus.append(DomainConstants.ATTRIBUTE_APPROVAL_STATUS);
			sAttrApprovalStatus.append(closeBracket);
			StringBuilder sAttrScheduledCompletionDate =new StringBuilder(attributeBracket);
			sAttrScheduledCompletionDate.append(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE );
			sAttrScheduledCompletionDate.append(closeBracket);
	
			StringList selectStmt = new StringList(7);
			selectStmt.add(sAttrComments.toString());
			selectStmt.add(sAttrReviewTask.toString());
			selectStmt.add(sAttrRouteNodeId.toString());
			selectStmt.add(sAttrApprovalStatus.toString());
			selectStmt.add(sAttrScheduledCompletionDate.toString());
			selectStmt.add("from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_ROUTE_SCOPE+"].from.id");
			selectStmt.add(SELECT_ROUTE_ACTION);
	
			person=Person.getPerson(context);
			
			String personName = person.getInfo(context,DomainConstants.SELECT_NAME);
	
			AttributeList attrList                  = new AttributeList();
			Map<?, ?> taskInfoMap           = task.getInfo(context, selectStmt);
			String taskComments       = (String)taskInfoMap.get(sAttrComments.toString());
			taskScheduledDate                = (String)taskInfoMap.get(sAttrScheduledCompletionDate.toString());
			taskInfoMap.get(SELECT_ROUTE_ACTION);
			EnoviaResourceBundle.getProperty(context,"emxComponents.Routes.EnforceAssigneeApprovalComments");
			
			if(comments !=null && (!"".equals(comments.trim()))){
				String oldComment=task.getInfo(context,"attribute["+DomainConstants.ATTRIBUTE_COMMENTS+"]");
				if(!oldComment.equals(comments)){
					bTaskCommentGiven =true;
				}
			}else{
				bTaskCommentGiven =false;
				comments="";
			}
			
			if(!returnBack)
			{
				String treeMenu = "";
				boolean bIsTransActive = ContextUtil.isTransactionActive(context);
				try{
					if(!bIsTransActive) {
						ContextUtil.startTransaction(context, true);
						bIsTransActive = true;
					}
					
					String strDateTime              = "";
	
					if(UIUtil.isNotNullAndNotEmpty(taskScheduledDate)){
						strDateTime  = taskScheduledDate;
					}
					String routeNodeId        = (String)taskInfoMap.get(sAttrRouteNodeId.toString());
					//Get the correct relId for the RouteNodeRel given the attr routeNodeId from the InboxTask.
					routeNodeId = route.getRouteNodeRelId(context, routeNodeId);
					String reviewTask         = (String)taskInfoMap.get(sAttrReviewTask.toString());
					
					if(bTaskCommentGiven){
						task.setAttributeValue(context,attrTaskCommentsNeeded, NO_VALUE);
					} else {
						task.setAttributeValue(context,attrTaskCommentsNeeded, YES_VALUE);
					}
	
					// Setting the attributes to the task object
					if (!"".equals(taskStatus)){
						attrList.add(new Attribute(new AttributeType(strApprovalStatusAttr), taskStatus));
					}
					if(!"".equals(strDateTime)){
						attrList.add(new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE), strDateTime));
					}
					attrList.add(new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_COMMENTS), comments));
					task.setAttributes(context,attrList);
	
					treeMenu = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"eServiceComponents.treeMenu.InboxTask");	
					if(  treeMenu  != null && !"null".equals( treeMenu  ) && !"".equals( treeMenu )) {
						MailUtil.setTreeMenuName(context, treeMenu );
					}
	
					// promote task object
					if (YES_VALUE.equalsIgnoreCase(reviewTask)){
						task.promote(context);
						AttributeList attrList1 = new AttributeList();
						attrList1.add(new Attribute(new AttributeType(attrReviewCommentsNeeded), "Yes"));
						task.setAttributes(context,attrList1);
	
						try{
							domainRel             = DomainRelationship.newInstance(context ,routeNodeId);
							Map<String, String> attrMap           = new HashMap<>();
							attrMap.put(attrReviewCommentsNeeded,YES_VALUE);
							Route.modifyRouteNodeAttributes(context, routeNodeId, attrMap);
						}catch(Exception ex){
							ex.printStackTrace();
							throw ex;
						}
	
					}else{
						// do all operations for delta due-date offsets in this loop
						try {
							StringBuilder sAttrDueDateOffset =new StringBuilder(attributeBracket);
							sAttrDueDateOffset.append(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
							sAttrDueDateOffset.append(closeBracket);
							StringBuilder sAttrDueDateOffsetFrom =new StringBuilder(attributeBracket);
							sAttrDueDateOffsetFrom.append(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM);
							sAttrDueDateOffsetFrom.append(closeBracket);
							StringBuilder sAttrSequence =new StringBuilder(attributeBracket);
							sAttrSequence.append(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
							sAttrSequence.append(closeBracket);
							String selState               = DomainConstants.SELECT_CURRENT;  // get state
	
							StringList relSelects            = new StringList();
	
							StringBuilder sWhereExp           = new StringBuilder();
							int nextTaskSeq                  = 0;
							int currTaskSeq                  = 0;
							domainRel                     = DomainRelationship.newInstance(context ,routeNodeId);
							String currTaskSeqStr         = domainRel.getAttributeValue(context, DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
							String parallelType                  = domainRel.getAttributeValue(context, sAttParallelNodeProcessonRule);
	
							// get current / next task sequence
							if(UIUtil.isNotNullAndNotEmpty(currTaskSeqStr)){
								currTaskSeq = Integer.parseInt(currTaskSeqStr);
								nextTaskSeq = currTaskSeq+1;
							}
							relSelects.add(sAttrDueDateOffset.toString());
							relSelects.add(sAttrDueDateOffsetFrom.toString());
							relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
							sWhereExp.append("(");
							sWhereExp.append(selState);
							sWhereExp.append(" != \"");
							sWhereExp.append(DomainConstants.STATE_INBOX_TASK_COMPLETE);
							sWhereExp.append("\")");
							sWhereExp.append(" && (");
							sWhereExp.append(sAttrSequence.toString());
							sWhereExp.append(" == \"");
							sWhereExp.append(String.valueOf(currTaskSeq));
							sWhereExp.append("\")");
							// finds all same order tasks which are not yet complete; if any such exists, next offset task due-date is not set till all complete
							boolean shouldSetNextTaskDueDate = shouldSetNextTaskDueDate(context, route,currTaskSeq+"");
							boolean completeAny              = true;
							if(ALL_VALUE.equals(parallelType)){
								completeAny = false;
							}
							// can proceed to set offset due-date for next task
							// only if Parallel node rule is 'Any' or corresponding flag got is true
							if(completeAny || shouldSetNextTaskDueDate){
								// where clause filters to next order tasks offset from this task complete
								sWhereExp = new StringBuilder();
								sWhereExp.append("(");
								sWhereExp.append(sAttrDueDateOffset.toString());
								sWhereExp.append(" !~~ \"\")");
								sWhereExp.append(" && (");
								sWhereExp.append(sAttrDueDateOffsetFrom.toString());
								sWhereExp.append(" ~~ \"");
								sWhereExp.append(OFFSET_FROM_TASK_CREATE_DATE);
								sWhereExp.append("\")");
								sWhereExp.append(" && (");
								sWhereExp.append(sAttrSequence.toString());
								sWhereExp.append(" == \"");
								sWhereExp.append(String.valueOf(nextTaskSeq));
								sWhereExp.append("\")");
								
								MapList nextOrderOffsetList = getNextOrderOffsetTasks(context, route, relSelects, sWhereExp.toString());
								
	
								Route.setDueDatesFromOffset(context,nextOrderOffsetList);
							}
						}catch(Exception e) {
							logger.log(Level.SEVERE,e.getMessage(),e);	
						}
	
						MapList subRouteList=route.getAllSubRoutes(context);
	
						try {
	
							task.promote(context);
							{
								String isFDAEnabled = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.EnableFDA");
								if(UIUtil.isNotNullAndNotEmpty(isFDAEnabled) && isFDAEnabled.equals(JSON_OUTPUT_KEY_TRUE)) {
									String strRouteTaskUser = task.getAttributeValue(context,DomainConstants.ATTRIBUTE_ROUTE_TASK_USER);
									String isResponsibleRoleEnabled = DomainConstants.EMPTY_STRING;
									try {
										isResponsibleRoleEnabled = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.ResponsibleRoleForSignatureMeaning.Preserve");
									} catch(Exception e) {
										isResponsibleRoleEnabled = JSON_OUTPUT_KEY_FALSE;
										logger.log(Level.SEVERE,e.getMessage(),e);	
									}
									if(UIUtil.isNotNullAndNotEmpty(isResponsibleRoleEnabled) && isResponsibleRoleEnabled.equalsIgnoreCase("true") && UIUtil.isNotNullAndNotEmpty(strRouteTaskUser) && strRouteTaskUser.startsWith("role_")) {
										i18NReadAndUnderstand = MessageUtil.getMessage(context, null, "emxFramework.UserAuthentication.ReadAndUnderstandRole", new String[] {
												PropertyUtil.getSchemaProperty(context, strRouteTaskUser)}, null, context.getLocale(),
												"emxFrameworkStringResource");
										if (taskStatus.equalsIgnoreCase(TASK_APPROVAL_STATUS_APPROVE)){
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,APPROVE,i18NReadAndUnderstand);
										} else {
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,taskStatus,i18NReadAndUnderstand);
										}
									} else {
										if (taskStatus.equalsIgnoreCase(TASK_APPROVAL_STATUS_APPROVE)) {
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,APPROVE,i18NReadAndUnderstand);
										} else {
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,taskStatus,i18NReadAndUnderstand);
										}
									}
								}
							}
	
	
							// Added to notify to the subscribed person, if Task completion event is subscribed -start
							String taskState = task.getInfo(context, DomainConstants.SELECT_CURRENT);
	
							if(Route.STATE_ROUTE_COMPLETE.equals(taskState)) {
								try {
									SubscriptionManager subscriptionMgr = route.getSubscriptionManager();
									subscriptionMgr.publishEvent(context, Route.EVENT_TASK_COMPLETED, task.getId(context));
								} catch(Exception e) {
									logger.log(Level.SEVERE,e.getMessage(),e);	
								}
							}
							String sSubject =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.common.TaskDeletionNotice");		
							String sMessage1 =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.common.TaskDeletionMessage3");		
							String sMessage2 =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.common.TaskDeletionMessage2");		
	
							String sMessage=sMessage1+" "+task.getName()+" "+sMessage2;
							Route.deleteOrphanSubRoutes(context,subRouteList,sMessage,sSubject);
							// set Inbox Task title for auto-namer tasks
							boolean bContextPushed = false;
							try{
	
								ContextUtil.pushContext(context);
								bContextPushed = true;
								InboxTask.setTaskTitle(context, routeId);
							}catch(Exception e){
								logger.log(Level.SEVERE,e.getMessage(),e);	
							}
							finally {
								if(bContextPushed)
									ContextUtil.popContext(context);
							}
							
							if (!"".equals(routeId)) {
								treeMenu = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"eServiceComponents.treeMenu.Route");	
								if(  treeMenu  != null && !"null".equals( treeMenu  ) && !"".equals( treeMenu )) {
									MailUtil.setTreeMenuName(context, treeMenu );
								}
								StringList stringlist = new StringList(10);
								stringlist.clear();
								stringlist.add(DomainConstants.SELECT_ID);
								stringlist.add(DomainConstants.SELECT_TYPE);
								stringlist.add(GRANTOR);
								stringlist.add(GRANTEE);
								stringlist.add(GRANTEEACCESS);
	
								DomainObject domainobject1 = DomainObject.newInstance(context, routeId);
	
								MapList maplist = domainobject1.getRelatedObjects(context,  //Context context
										DomainConstants.RELATIONSHIP_OBJECT_ROUTE, // String relationshipPattern
										"*",  // String typePattern
										stringlist, //StringList objectSelects
										DomainConstants.EMPTY_STRINGLIST, // StringList relationshipSelects
										true, // boolean getTo
										false, // boolean getFrom
										(short)1, // short recurseToLevel
										DomainConstants.EMPTY_STRING, //String objectWhere
										DomainConstants.EMPTY_STRING,// String relationshipWhere
										0);//  int limit
								Map<?, ?> map1 = domainobject1.getInfo(context, stringlist);
								maplist.add(0, map1);
								Iterator<Map<?, ?>> iterator = maplist.iterator();
								while(iterator.hasNext()){
									Map<?, ?> map2 = iterator.next();
									Object obj1 = null;
									Object obj2 = null;
									Object obj3 = null;
									Object obj4 = map2.get(GRANTOR);
									if((obj4 instanceof String) || obj4 == null){
										if(obj4 == null || "".equals(obj4))
											continue;
										obj1 = new ArrayList<Object>(1);
										obj2 = new ArrayList<Object>(1);
										obj3 = new ArrayList<Object>(1);
										((java.util.List) (obj1)).add(obj4);
										((java.util.List) (obj2)).add(map2.get(GRANTEE));
										((java.util.List) (obj3)).add(map2.get(GRANTEEACCESS));
									}else{
										obj1 = (java.util.List)map2.get(GRANTOR);
										obj2 = (java.util.List)map2.get(GRANTEE);
										obj3 = (java.util.List)map2.get(GRANTEEACCESS);
									}
	
									String objId = (String)map2.get("id");
									BusinessObject doc=new BusinessObject(objId);
									doc.open(context);
									if (doc.getTypeName().equals(DomainConstants.TYPE_DOCUMENT)){
	
										for(int i = 0; i < ((java.util.List) (obj1)).size(); i++) {
											String strGrantee = (String)((java.util.List) (obj2)).get(i);
	
											if(strGrantee.equals(personName)) {
												String strGrantor = (String)((java.util.List) (obj1)).get(i);
	
												if(strGrantor.equals(DomainConstants.PERSON_ROUTE_DELEGATION_GRANTOR)) {
													boolean isCntxPushed=false;
													try {
														
														ContextUtil.pushContext(context, DomainConstants.PERSON_ROUTE_DELEGATION_GRANTOR, null, context.getVault().getName());
														isCntxPushed = true;
														MqlUtil.mqlCommand(context, MQL_REVOKE_GRANTOR_GRANTEE,false, objId,strGrantor,personName);
	
													} catch(Exception exception4){
														throw new FrameworkException(exception4);
													}
													finally{
														if(isCntxPushed) {
															ContextUtil.popContext(context);
														}
													}
												}
											}
										}
									}
									doc.close(context);
								}
							}
						} catch(Exception ex)
						{
							logger.log(Level.SEVERE,ex.getMessage(),ex);
						}
	
					}  
					if(bIsTransActive) {
						ContextUtil.commitTransaction(context); 
						bIsTransActive = false;
					}
				}catch(Exception e){
					String strPromoteConnectedError =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.Common.PromoteConnectObjectFailed");
					ClientTaskList listNotices 	= context.getClientTasks();	
					ClientTaskItr itrNotices 	= new ClientTaskItr(listNotices);
					String message = "";
					boolean isPromoteConnectObject = false;
					while (itrNotices.next()) {
						ClientTask clientTaskMessage =  itrNotices.obj();
						String emxMessage = clientTaskMessage.getTaskData();
						if(UIUtil.isNotNullAndNotEmpty(emxMessage) && strPromoteConnectedError.equalsIgnoreCase(emxMessage.trim())){
							isPromoteConnectObject = true;
							message =emxMessage +":" + message;
						}else {
							message = message + emxMessage;  					
						}
					}
					if(isPromoteConnectObject){
						context.clearClientTasks();
						MqlUtil.mqlCommand(context, "notice $1",message);
					}
					if(bIsTransActive) {
						ContextUtil.abortTransaction(context);
						bIsTransActive = false;
					}
				}
				
				String routeState = route.getInfo(context,DomainObject.SELECT_CURRENT);
	
				if(routeState != null && ACTION_COMPLETE.equals(routeState)){
				
					treeMenu = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"eServiceComponents.treeMenu.Route");
	
					boolean validTreeMenu=false;
					if(  treeMenu  != null && !"null".equals( treeMenu  ) && !"".equals( treeMenu )){
						validTreeMenu=true;
					}
					// PMC Bug 313582:java.lang.ClassCastException - While completing the Route task
					// Added to get the "type" of the "from" end object(Workspace Vault) connected through "Route Scope" relationship as "Super User" since the context user does not have access
					boolean isContextPushed = false ;
					String sScopeObjType;
					String sWorkspaceId = "";
					try {
						ContextUtil.pushContext(context);
						isContextPushed = true;
						sScopeObjType = route.getInfo(context,"to["+route.RELATIONSHIP_ROUTE_SCOPE+"].from.type");
					}finally {
						if(isContextPushed) {
							ContextUtil.popContext(context);
							isContextPushed = false;
						}	
					}
					// PMC Bug 313582 Ends here
	
					//To get the Workspace object to publish the corresponding subscribed events
					if(DomainConstants.TYPE_PROJECT_VAULT.equals(sScopeObjType)) {
						try {
							if(!isContextPushed) {
								ContextUtil.pushContext(context);
								isContextPushed = true;
							}
							String sWorkspaceVaultId = route.getInfo(context,"to["+DomainConstants.RELATIONSHIP_ROUTE_SCOPE+"].from.id");
							if(validTreeMenu) {
								MailUtil.setTreeMenuName(context, treeMenu );
							}
		
							sWorkspaceId = UserTask.getProjectId(context, sWorkspaceVaultId);
							if (sWorkspaceId!=null)
							{
								DomainObject domainObject = DomainObject.newInstance(context);
								domainObject.setId(sWorkspaceId);
								String sType=domainObject.getInfo(context,DomainConstants.SELECT_TYPE);
								if(sType.equals(DomainConstants.TYPE_PROJECT_SPACE) || mxType.isOfParentType(context,sType,DomainConstants.TYPE_PROJECT_SPACE)) //Modified for Sub type
									isProjectSpace=true;
							}
						}finally {
							if(isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
						}
					}else{
						try {
							if(!isContextPushed) {
								ContextUtil.pushContext(context);
								isContextPushed = true;
							}
							sWorkspaceId  = route.getInfo(context,"to["+DomainConstants.RELATIONSHIP_ROUTE_SCOPE+"].from.id");	
						}finally {
							if(isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
						}
					}
					if(( UIUtil.isNotNullAndNotEmpty(sWorkspaceId)) && !(isProjectSpace)) {
						try {
							ContextUtil.pushContext(context);
							isContextPushed = true;
							if(validTreeMenu){
								MailUtil.setTreeMenuName(context, treeMenu);
							}
						}finally {
							if(isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
						}
					}
					StringList listContentIds = new StringList();
					try {
						ContextUtil.pushContext(context);
						isContextPushed = true;
						//To get list of documents attached to Route and publish Route Completed Event for the document
						listContentIds = route.getInfoList(context,"to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from.id");
					}finally {
						if(isContextPushed) {
							ContextUtil.popContext(context);
							isContextPushed = false;
						}
						
					}
	
					DomainObject document = DomainObject.newInstance(context,DomainConstants.TYPE_DOCUMENT);
					if(listContentIds == null){
						listContentIds = new StringList();
					}
	
					Iterator<?> contentItr = listContentIds.iterator();
					
					while(contentItr.hasNext()){
	
						String sDocId = (String)contentItr.next();
	
						document.setId(sDocId);
	
						if(validTreeMenu) {
							MailUtil.setTreeMenuName(context, treeMenu );
						}
					}
					if(validTreeMenu) {
						MailUtil.setTreeMenuName(context, treeMenu );
					}
				}
			}
			Boolean canComplete = false;
			if(flag != null && FLAG_FDA.equalsIgnoreCase(flag))
			{
				if(returnBack){
					hmParam.put(RETURN_BACK,returnBack);                   //give alert emxComponents.RejectComments.
				}else {
					canComplete =  true;
				}
			}else {
				if(returnBack){
					hmParam.put(RETURN_BACK,returnBack);                   //give alert emxComponents.RejectComments.Comments
				}else {
					canComplete =  true;
				}
	
			}
			if(canComplete) {
				HashMap<String, Serializable> requestMap = new HashMap<String, Serializable>();
				requestMap.put(LANGUAGE_STR,strLanguage);
				requestMap.put(LOCALE_OBJ,strLocale);
				TimeZone tz = TimeZone.getTimeZone(context.getSession().getTimezone());
				Calendar cal = Calendar.getInstance(tz);
				requestMap.put(TIME_ZONE,String.valueOf(cal.DST_OFFSET));
				requestMap.put(OBJECT_ID,taskId);
				requestMap.put(COMMENTS,comments);
	
				requestMap.put(REVIEWER_COMMENTS,reviewcomments);
				java.util.Date date = new java.util.Date(taskScheduledDate);
				int intDateFormat = eMatrixDateFormat.getEMatrixDisplayDateFormat();
				java.text.DateFormat formatter = java.text.DateFormat.getDateInstance(intDateFormat, context.getLocale());
				taskScheduledDate =  formatter.format(date);
				requestMap.put(DUE_DATE,taskScheduledDate);
				routeTime =  "00:00:00 AM";
				requestMap.put(ROUTE_TIME,routeTime); 
				InboxTask inboxTaskObj  					= (InboxTask)DomainObject.newInstance(context, taskId);
				BusinessObjectAttributes boAttrGeneric = inboxTaskObj.getAttributes(context);
				AttributeItr attrItrGeneric   = new AttributeItr(boAttrGeneric.getAttributes());
	
				while (attrItrGeneric.next()) {
					Attribute attrGeneric = attrItrGeneric.obj();
					String strAttrName=attrGeneric.getName() ;					
					if (strAttrName.equals(DomainConstants.ATTRIBUTE_APPROVAL_STATUS) ) {
						requestMap.put(strAttrName,taskStatus); 
					}
					else if(strAttrName.equals(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE)){
						requestMap.put(strAttrName,taskScheduledDate); 
					}
					else if(strAttrName.equals(DomainConstants.ATTRIBUTE_COMMENTS)) {
					    //requestMap.put(strAttrName,taskComments);
						requestMap.put(strAttrName,comments);
					}					 
					else if(strAttrName.equals(reviewcomments)) {
						requestMap.put(strAttrName,reviewcomments); 
					}
					else
					{
						String sAttrValue = inboxTaskObj.getAttributeValue(context, strAttrName);
						requestMap.put(strAttrName,sAttrValue); 
					}
				}
	
		     HashMap<String, HashMap<String, Serializable>> hmArguments = new HashMap<String, HashMap<String, Serializable>>();
			 hmArguments.put(REQUEST_MAP,requestMap);
			 try {
				 if( !showFDA.equalsIgnoreCase(JSON_OUTPUT_KEY_TRUE))
					{
					 JPO.invoke(context, "emxInboxTask", null, "updateTaskDetails", JPO.packArgs(hmArguments), HashMap.class);
					}
				
			 }catch(Exception e){
				 e.printStackTrace();
			 }
			 sReturn= SUCESS_VALUE;
			}
		}
		catch (Exception e) {
			e.printStackTrace();      
			return sReturn;
		}
	
	
		return sReturn;
		}

	/**
	 * Get the task of the next order in the route
	 * @param context enovia context object
	 * @param route domain object of route 
	 * @param relSelects list of relationship select
	 * @param sWhereExp  where expression to get Related object 
	 * @return Maplist with details of tasks of the next order
	 * @throws Exception when operation fails
	 */
	public static MapList getNextOrderOffsetTasks(Context context, DomainObject route, StringList relSelects, String sWhereExp) throws Exception{

	
		return route.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_ROUTE_NODE, //String relPattern
				DomainConstants.QUERY_WILDCARD,  //String typePattern
				null,                         //StringList objectSelects,
				relSelects,                     //StringList relationshipSelects,
				false,                     //boolean getTo,
				true,                     //boolean getFrom,
				(short)1,                 //short recurseToLevel,
				"",                       //String objectWhere,
				sWhereExp,           //String relationshipWhere,
				0);                // int limit
	}

	/**
	 * metod to set due date in the next task
	 * @param context enovia context object
	 * @param route Route object
	 * @param currTaskSeq seq number of the next task
	 * @return true when due date must be set; false, otherwise
	 * @throws Exception when operation fails
	 */
	public static boolean shouldSetNextTaskDueDate(Context context, Route route, String currTaskSeq) throws Exception{

		boolean retVal =false;
		StringList objSelect=new StringList();
		objSelect.add(DomainConstants.SELECT_ID);
		objSelect.add("attribute["+DomainConstants.ATTRIBUTE_ROUTE_NODE_ID+"]");
		objSelect.add(DomainConstants.SELECT_CURRENT);
		
		
		MapList taskMapList = route.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_ROUTE_TASK, //String relPattern
				DomainConstants.TYPE_INBOX_TASK,         //String typePattern
				objSelect,                     //StringList objectSelects,
				null,                          //StringList relationshipSelects,
				true,                          //boolean getTo,
				false,                         //boolean getFrom,
				(short)1,                      //short recurseToLevel,
				"",                           //String objectWhere,
				"",                           //String relationshipWhere,
				0);                       //Map includeMap

		// for getting all Same sequence tasks which are incomplete
		MapList incompleteSameSeqMapList=new MapList();
		Iterator<?> it=taskMapList.iterator();
		String state = "";
		String relId = "";
		String currTaskSeqStr = "";
		DomainRelationship domRel = null;
		HashMap<?,?> hashTable ;
		while(it.hasNext()){
			hashTable=(HashMap<?,?>)it.next();
			state=(String)hashTable.get(DomainConstants.SELECT_CURRENT);
			relId=(String)hashTable.get("attribute["+DomainConstants.ATTRIBUTE_ROUTE_NODE_ID+"]");

			//Get the correct relId for the RouteNodeRel given the attr routeNodeId from the InboxTask.
			relId = route.getRouteNodeRelId(context, relId);
			
			domRel=DomainRelationship.newInstance(context,relId);
			currTaskSeqStr    = domRel.getAttributeValue(context, DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
			if(currTaskSeq.equals(currTaskSeqStr) && !state.equalsIgnoreCase(DomainConstants.STATE_INBOX_TASK_COMPLETE)){
				incompleteSameSeqMapList.add(hashTable);
			}
		}
		if(incompleteSameSeqMapList.size()==1 ){
			retVal = true;
		}
		return retVal;
	}
	
  /**
   * 
   * @param context
   * @param theMepAttributesMap map containing username and password
   * @return return true/false depend on verification 
   * @throws Exception
   */
	public static String getcheckverified(Context context, Map<?, ?> theMepAttributesMap) throws Exception{
		HashMap<?,?> programMap =  (HashMap <?,?>)theMepAttributesMap;

		String userId=(String) programMap.get(USER_ID);
		String passwd=(String) programMap.get(PASSWORD);
		HttpSession session = getSession();		
		
		boolean isVerified=false;
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();

		try{
			String passportURL = PropertyUtil.getEnvironmentProperty(context, PASSPORT_URL);
			boolean is3DPassportServerInUse = (passportURL != null && passportURL.length() > 0); 
			boolean bXMLResponseTag = true; 
			try 
			{				
				PersonUtil.checkFDAAuthentication(context, userId, passwd, passportURL, is3DPassportServerInUse,session, bXMLResponseTag);	
				isVerified = true;
			} 
			catch (Exception e) {
				logger.log(Level.SEVERE,e.getMessage(),e);
			}
			jsonObject.add(IS_VERIFIED, (boolean)isVerified);
		}
		catch (Exception ex) {
			logger.log(Level.SEVERE,ex.getMessage(),ex);
		}	
		return jsonObject.build().toString();
	}
    
	/**
	 * Method to get the Session object
	 * @return HttpSession object
	 */
	private static HttpSession getSession() {
		HttpSession session = new HttpSession() {

			@Override
			public void setMaxInactiveInterval(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeValue(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void putValue(String arg0, Object arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isNew() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void invalidate() {
				// TODO Auto-generated method stub

			}

			@Override
			public String[] getValueNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getValue(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public HttpSessionContext getSessionContext() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServletContext getServletContext() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMaxInactiveInterval() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long getLastAccessedTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getId() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getCreationTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Enumeration<String> getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		return session;
	}

}
