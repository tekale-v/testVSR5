package com.pg.widgets.helloworldtrusted;

import com.dassault_systemes.platform.restServices.RestService;


import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;


@Path("/helloworldservices")
public class PGHelloWorldServices extends RestService{

	private static final Logger logger = Logger.getLogger(PGHelloWorldServices.class.getName());
	
	/**
	 * Web Service to get full name of logged-in user
	 * Returns: Logged-in user full name
	 */
	@GET
	@Path("/getPersonFullName")	
	public Response getPersonFullName(@javax.ws.rs.core.Context HttpServletRequest request) {
		Response res = null;
		try {
			JsonObjectBuilder output;
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			output = (JsonObjectBuilder) PGHelloWorldUtil.getPersonFullName(context);
			
			res = Response.status(200).entity(output.build().toString()).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "error thrown from PGWidgets.getPersonFullName", e);
		}
		return res;
	}
	
	
}
