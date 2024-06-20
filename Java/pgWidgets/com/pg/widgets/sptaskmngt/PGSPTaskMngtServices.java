package com.pg.widgets.sptaskmngt;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;


@Path("/sptaskmngtservices")
public class PGSPTaskMngtServices extends RestService
{
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final String EXCEPTION_MESSAGE  = "Exception in PGSPTaskMngtServices";
	static final Logger logger = Logger.getLogger(PGSPTaskMngtServices.class.getName());

	/**
	 * REST method to get Tasks assigned to context user with content as Study Protocol
	 * 
	 @param request
	 *            HTTPServletRequest for REST service
	 @param TypePattern
	 *            The appropriate types for the functionality of the widget
	 * @return response in JSON format
	 * @throws Exception
	 *             when operation fails
	 */
	@POST
	@Path("/mySPTaskMngt")
	public Response getMySPTasks(@javax.ws.rs.core.Context HttpServletRequest request, String inputData)
	{
		String strOutput = null;
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);			
			strOutput = PGSPTaskMngt.getSPConnectedRouteTasks(context, inputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getroutetasks")
	public Response getRouteTasks(@javax.ws.rs.core.Context HttpServletRequest request, String inputData)
	{
		String strOutput = null;
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);			
			strOutput = PGSPTaskMngt.getRouteTasks(context, inputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/gettaskinfo")
	public Response getTaskInfo(@javax.ws.rs.core.Context HttpServletRequest request, String inputData)
	{
		String strOutput = null;
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);			
			strOutput = PGSPTaskMngt.getTaskInfo(context, inputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}
