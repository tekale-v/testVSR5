package com.pg.widgets.taskmanagement;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;

@Path("/taskmanagementservices")
public class PGTaskManagementServices extends RestService
{
	
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final Logger logger = Logger.getLogger(PGTaskManagementServices.class.getName());
	static final String EXCEPTION_MESSAGE  = "Exception in PGTaskManagementServices";
	
	@POST
	@Path("/getconnectedobjects")
	public Response getRelatedObjectData(@Context HttpServletRequest request, String strInput) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGTaskManagement.getRelatedObjectData(context, strInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}
