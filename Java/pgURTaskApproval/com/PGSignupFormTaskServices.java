/*
Project Name: P&G
Class Name: PGSignupFormTaskServices
Clone From/Reference: N/A
Purpose: This is a service class and routing point to business logic to get data and return it to service call
from dashboard.
Change History : Added for new functionalities under 2018x.5 release
for Requirement 33490,34528,33491,34529,34530,34531,34532,34533,34535
 */
package com;
import com.dassault_systemes.platform.restServices.RestService;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("/pgsignupformtasksservices")
public class PGSignupFormTaskServices extends RestService
{
	private static final String WHERE_EXP = "WhereExpression";
	private static final String OBJ_SELECT = "ObjectSelects";
	private static final String BUILD_APPLICATION_JSON = "application/json";
	@GET
	@Path("/pgSignupFormTasks")
	/**
	 * REST method to get the SignupForm details
	 * @param request HTTPServletRequest for REST service
	 * @param strWhereExpressio where clause, if needed, for the functionality of the widget
	 * @param strObjectSelects selectables for the data needed in return response
	 * @return response in JSON format
	 * @throws Exception  when operation fails
	 */
	public Response getSignupFormActiveTasks(@javax.ws.rs.core.Context HttpServletRequest request, 
			@QueryParam(WHERE_EXP) String strWhereExpression,
			@QueryParam(OBJ_SELECT) String strObjectSelects)	
	{
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HashMap<String, Object> mpParamMAP = new HashMap<>();

			mpParamMAP.put(WHERE_EXP, strWhereExpression);
			mpParamMAP.put(OBJ_SELECT, strObjectSelects);
			String strOutput =PGSignupFormTasks.getAssignedSignupFormTask(context,mpParamMAP);
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	} 

	@PUT
	@Path("/validateUserAndProcessTask")
	@Consumes(MediaType.APPLICATION_JSON)
	/**
	 * REST method to  validate user and approve/reject the Inbox Taks associated with SignupForm
	 * @param request HttpServletRequest
	 * @param strInboxTaskId Inbox Task Ids that needs to be approved or rejected
	 * @param strComment Comments that needs to be updated on task
	 * @param  strProcessCheck for task to be approved/rejected.
	 * @param strUserName Username of logged in user
	 * @param strPassword Password of logged in user
	 *  @return Response created with the valid status of Operation.
	 */
	public Response startTaskProcessing(@javax.ws.rs.core.Context HttpServletRequest request,
			Map<String,String> mpRequestMap ) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HttpSession session = request.getSession(true);
			String strProcessCheck =  mpRequestMap.get("ProcessCheck");
			String strInboxTaskId =  mpRequestMap.get("TaskIdToApprove");
			String strComment =  mpRequestMap.get("Comments");
			String strUserName =  mpRequestMap.get("UserName");
			String strPassword =  mpRequestMap.get("Password");
			String strOutput = PGSignupFormTasks.validateUserAndProcessTask(context,strProcessCheck, strInboxTaskId,session,strComment,strUserName,strPassword);
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}