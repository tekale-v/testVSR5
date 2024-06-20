package com.pg.widgets.route;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.common.InboxTask;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;

@Path("/routeservices")
public class PGRouteServices extends RestService {
	static final String ACTION_APPROVE = "Approve";
	static final String ACTION_REJECT = "Reject";
	static final String OUTPUT_SUCCESS = "Success ";
	static final String TASK_ID = "taskId";
	static final String ACTION = "action";
	static final String FROM_SUMMARY_PAGE = "fromSummaryPage";
	static final String ROUTE_ID = "routeId";
	static final String COMMENTS = "comments";
	static final String GET_COMMENT_FROM_TASK_ID = "getCommentsFromTaskId";
	static final String TASK_STATUS = "taskStatus";
	static final String FLAG = "flag";
	static final String USER_ID = "userId";
	static final String PASSWORD = "passwd";
	static final String SESSION = "session";
	static final String SHOW_FDA = "showFDA";
	static final String BUILD_APPLICATION_JSON = "application/json";
	private static final Logger logger = Logger.getLogger(PGRouteServices.class.getName());

	/**
	 * Web Service to get Route object details
	 * 
	 * @param request
	 *            HttpServletRequest object for web service
	 * @param EnterprisePartID
	 *            objectId of Part object
	 * @return Response with details of Route related to the Part object
	 * @throws Exception
	 *             when operation fails
	 */
	@GET
	@Path("/getRouteInfo")
	public Response getRouteInfo(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("EnterprisePartID") String EnterprisePartID) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGRoute.getRouteInfo(context, EnterprisePartID);
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		return res;
	}

	/**
	 * Web service to approve or reject a task
	 * 
	 * @param request
	 *            HttpServletRequest object for web service
	 * @param pid
	 *            object Id of Inbox Task object
	 * @param action
	 *            "Approve" or "Reject" to decide on the task action
	 * @param Comment
	 *            Text for comment on Task
	 * @return Response response code
	 * @throws Exception
	 *             when operation fails
	 */
	@GET
	@Path("/updateTask")
	public Response updateTaskInfo(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("pid") String pid, @QueryParam("action") String action,
			@QueryParam("comments") String Comment) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			InboxTask localInboxTask = new InboxTask(pid);
			if ((action.equals(ACTION_APPROVE)) || (action.equals(ACTION_REJECT))) {
				HashMap<String, String> localHashMap = new HashMap<>();
				localHashMap.put(PropertyUtil.getSchemaProperty(context, "attribute_ApprovalStatus"), action);
				localHashMap.put(DomainConstants.ATTRIBUTE_APPROVAL_STATUS, action);
				localHashMap.put(DomainConstants.ATTRIBUTE_COMMENTS, Comment);
				localInboxTask.setAttributeValues(context, localHashMap);
				localInboxTask.setState(context, PropertyUtil.getSchemaProperty(context, "policy", PropertyUtil.getSchemaProperty(context, "policy_InboxTask"), "state_Complete"));
			}
			res = Response.ok(OUTPUT_SUCCESS).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		return res;
	}

	/**
	 * Web service to fetch tasks for approval or rejection
	 * 
	 * @param request
	 *            HttpServletRequest object for web service
	 * @param taskId
	 *            object Id of Task
	 * @param action
	 *            Approve or Reject
	 * @param fromSummaryPage
	 *            name of caller
	 * @return Response response code
	 * @throws Exception
	 *             when operation fails
	 */
	@GET
	@Path("/ApproveRejecttask")
	public Response getApproveRejecttask(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("taskId") String taskId, @QueryParam("action") String action,
			@QueryParam("fromSummaryPage") String fromSummaryPage) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			Map<String, Object> theMepAttributesMap = new HashMap<>();
			theMepAttributesMap.put(TASK_ID, taskId);
			theMepAttributesMap.put(ACTION, action);
			theMepAttributesMap.put(FROM_SUMMARY_PAGE, fromSummaryPage);
			String strOutput = PGRoute.getApproveRejecttask(context, theMepAttributesMap);
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		return res;
	}

	/**
	 * Web service to fetch completed tasks
	 * 
	 * @param request
	 *            HttpServletRequest object for web service
	 * @param taskId
	 *            object Id of Task
	 * @param routeId
	 *            object Id of Route
	 * @param comments
	 *            Text to add on Tasks as comments
	 * @param getCommentsFromTaskId
	 *            var to decide on certain action
	 * @param taskStatus
	 *            flag to get Task Status
	 * @param flag
	 *            flas to other task details
	 * @param shownFDAWindow
	 *            flag to display Approval Signature dialog box
	 * @return Response response code
	 * @throws Exception
	 *             when operation fails
	 */
	@GET
	@Path("/getCompleteTask")
	public Response getCompleteTask(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("taskId") String taskId, @QueryParam("routeId") String routeId,
			@QueryParam("comments") String comments, @QueryParam("getCommentsFromTaskId") String getCommentsFromTaskId, @QueryParam("taskStatus") String taskStatus,
			@QueryParam("flag") String flag, @QueryParam("shownFDAWindow") String shownFDAWindow) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			Map<String, Object> theMepAttributesMap = new HashMap<>();
			theMepAttributesMap.put(TASK_ID, taskId);
			theMepAttributesMap.put(ROUTE_ID, routeId);
			theMepAttributesMap.put(COMMENTS, comments);
			theMepAttributesMap.put(GET_COMMENT_FROM_TASK_ID, getCommentsFromTaskId);
			theMepAttributesMap.put(TASK_STATUS, taskStatus);
			theMepAttributesMap.put(FLAG, flag);
			theMepAttributesMap.put(SHOW_FDA, shownFDAWindow);
			String output = PGRoute.getCompleteTask(context, theMepAttributesMap);
			res = Response.ok(output).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		return res;
	}

	/**
	 * @param request
	 *            HttpServletRequest object for web service
	 * @param userId
	 *            object Id of logged in user
	 * @param passwd
	 *            password of the user
	 * @return Response response code
	 * @throws Exception
	 *             when operation fails
	 */
	@GET
	@Path("/checkverified")
	public Response getcheckverified(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("userId") String userId, @QueryParam("passwd") String passwd)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			Map<String, Object> theMepAttributesMap = new HashMap<>();
			theMepAttributesMap.put(USER_ID, userId);
			theMepAttributesMap.put(PASSWORD, passwd);
			theMepAttributesMap.put(SESSION, request.getSession());
			String output = PGRoute.getcheckverified(context, theMepAttributesMap);
			res = Response.ok(output).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		return res;
	}
}
