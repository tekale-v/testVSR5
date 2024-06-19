package com.pg.widgets.apprequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.png.apollo.apprequest.PGAPPRequestConstants;
import com.png.apollo.apprequest.PGAPPRequestServices;
import com.png.apollo.apprequest.PGAPPRequestUtil;

@Path("/api")
public class PGAPPRequestAPIServices extends RestService {
	
	public static final String APPLICATION_JASON ="application/json";
	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PGAPPRequestAPIServices.class);

	@POST
	@Path("/createapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			
			boolean isAuthorized = PGAPPRequestUtil.validateAuthentication(context);
			
			if(!isAuthorized)
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity(PGAPPRequestConstants.STR_ERROR_UNAUTHORIZED_ACCESS).build();
			}
			
			return PGAPPRequestServices.createAPPRequest(context, sInput, true);
			
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	
	@POST
	@Path("/updateapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			
			boolean isAuthorized = PGAPPRequestUtil.validateAuthentication(context);
			
			if(!isAuthorized)
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity(PGAPPRequestConstants.STR_ERROR_UNAUTHORIZED_ACCESS).build();
			}
			
			return PGAPPRequestServices.updateAPPRequest(context, sInput, true);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAPPRequests(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			
			boolean isAuthorized = PGAPPRequestUtil.validateAuthentication(context);
			
			if(!isAuthorized)
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity(PGAPPRequestConstants.STR_ERROR_UNAUTHORIZED_ACCESS).build();
			}
			
			return PGAPPRequestServices.getAPPRequests(context, sInput, true);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	@GET
	@Path("/getapprequest/id/{RequestId}")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAPPRequestBasedOnRequestId(@javax.ws.rs.core.Context HttpServletRequest request,  @PathParam(PGAPPRequestConstants.KEY_REQUEST_ID) String sRequestId) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			
			boolean isAuthorized = PGAPPRequestUtil.validateAuthentication(context);
			
			if(!isAuthorized)
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity(PGAPPRequestConstants.STR_ERROR_UNAUTHORIZED_ACCESS).build();
			}
			
			return PGAPPRequestServices.getAPPRequest(context, sRequestId, true);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	@GET
	@Path("/getapprequest/RequestSetId/{RequestSetId}")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAPPRequestBasedOnRequestDataId(@javax.ws.rs.core.Context HttpServletRequest request, @PathParam(PGAPPRequestConstants.KEY_REQUEST_SET_ID) String sRequestSetId) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			
			boolean isAuthorized = PGAPPRequestUtil.validateAuthentication(context);
			
			if(!isAuthorized)
			{
				return Response.status(Response.Status.UNAUTHORIZED).entity(PGAPPRequestConstants.STR_ERROR_UNAUTHORIZED_ACCESS).build();
			}
			
			return PGAPPRequestServices.getAPPRequestData(context, sRequestSetId, true);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to return Authenticated Context
	 * @param request
	 * @param isSCMandatory
	 * @return
	 */
	private matrix.db.Context getContext(HttpServletRequest request, boolean isSCMandatory) {
		matrix.db.Context context = null;
		try {			
			context = getAuthenticatedContext(request, isSCMandatory);
		} catch (Exception e) {			
			logger.error(e.getMessage() ,e);
			throw e;
		}
		return context;
	}

}
