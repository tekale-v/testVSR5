package com.pg.widgets.myprojects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;

@Path("/myprojectsservices")
public class PGMyProjectsServices extends RestService {

	static final String TYPE_APPLICATION_FORMAT = "application/json";

	@POST
	@Path("/getProjectsData")
	public Response getProjectsData(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = PGMyProjectsUtil.getProjectsData(context, paramString);
			System.out.println("In getProjectsData" + strOutput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/createprojectfromtemplate")
	public Response createProjectFromTemplate(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = PGMyProjectsUtil.createProject(context, paramString);
			System.out.println("In getProjectsData" + strOutput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}
