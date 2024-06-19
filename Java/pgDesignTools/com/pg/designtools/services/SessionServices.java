package com.pg.designtools.services;

import javax.ws.rs.Path;
import com.dassault_systemes.platform.restServices.RestService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

@Path("/SessionServices")
public class SessionServices extends RestService {

	protected matrix.db.Context context; 
	
	@GET
	@Path("/getCATIASessionID")
	public Response getCATIASessionID(@Context HttpServletRequest req) {

		String sCATIASessionID="";
		try {
			sCATIASessionID=req.getRequestedSessionId();
			
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(sCATIASessionID).build();
	}
}