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


@Path("/apprequestservices")
public class PGAPPRequestRestServices extends RestService {
	public static final String APPLICATION_JASON ="application/json";
	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PGAPPRequestRestServices.class);

	/**
	 * Method to create APP Request
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/createapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.createAPPRequest(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to update Request
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.updateAPPRequest(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get APP Request
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAPPRequests(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getAPPRequests(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get All requests
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getall")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAPPRequests(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getAllAPPRequests(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to get APP request based on Request Id
	 * @param request
	 * @param sRequestId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getapprequest/RequestId/{RequestId}")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAPPRequestBasedOnRequestId(@javax.ws.rs.core.Context HttpServletRequest request,  @PathParam(PGAPPRequestConstants.KEY_REQUEST_ID) String sRequestId) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getAPPRequest(context, sRequestId);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get APP Request based on request set
	 * @param request
	 * @param sRequestSetId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getapprequest/RequestSetId/{RequestSetId}")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAPPRequestBasedOnRequestDataId(@javax.ws.rs.core.Context HttpServletRequest request, @PathParam(PGAPPRequestConstants.KEY_REQUEST_SET_ID) String sRequestSetId) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getAPPRequestData(context, sRequestSetId);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to get EBOM Details for given affected items
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getEBOMDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjectDetails(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getEBOMDetails(context, sInput);		
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get Latest Affected Items Details for Given requests
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getLatestAffectedItems")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLatestAffectedItems(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getLatestAffectedItems(context, sInput);		
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get Request Process owners
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getRequestProcessOwners")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRequestProcessOwnerList(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getRequestApproverList(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to claim APP Request
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/claimapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response claimAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.claimAPPRequest(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to Reject APP Request
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/rejectapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response rejectAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.rejectAPPRequest(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to Cancel APP Request
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/cancelapprequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response cancelAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.cancelAPPRequest(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to update implemented item
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateImplementedItem")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateImplementedItem(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.updateImplementedItem(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to reset implemented item
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/resetImplementedItem")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetImplementedItem(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.resetImplementedItem(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to Delete Request Data
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deleteRequestData")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRequestData(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.deleteRequestData(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to implement APP Request
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/implementAPPRequest")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response implementAPPRequest(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.implementAPPRequest(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to mark Request to Implemented
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/markAsImplemented")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response markAsImplemented(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.markAsImplemented(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to validate affected items for Implementation
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/validateAffectedItemsForImplementation")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateAffectedItemsForImplementation(@javax.ws.rs.core.Context HttpServletRequest request, String sInput) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.validateAffectedItemsForImplementation(context, sInput);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to promote all APP requests related to Request Set
	 * @param request
	 * @param sRequestSetId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/promote/RequestSetId/{RequestSetId}")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response promoteRequests(@javax.ws.rs.core.Context HttpServletRequest request, @PathParam(PGAPPRequestConstants.KEY_REQUEST_SET_ID) String sRequestSetId) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.promoteRequests(context, sRequestSetId, false);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to demote all APP Requests related to Request Set
	 * @param request
	 * @param sRequestSetId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/demote/RequestSetId/{RequestSetId}")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response demoteRequests(@javax.ws.rs.core.Context HttpServletRequest request, @PathParam(PGAPPRequestConstants.KEY_REQUEST_SET_ID) String sRequestSetId) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.demoteRequests(context, sRequestSetId, true);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to validate Context user is part of APP Request Approval Group or not
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/validateRequestApprovalMembership")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateRequestApprovalMembership(@javax.ws.rs.core.Context HttpServletRequest request) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.validateAPPRequestApprovalMembership(context);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to load all type of changes
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/loadTypeOfChanges")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTypeOfChanges(@javax.ws.rs.core.Context HttpServletRequest request) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getTypeOfChanges(context);
		} catch (Exception e) {
			logger.error(e.getMessage() ,e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get History Data for given object
	 * @param request
	 * @param sObjectId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getHistory/{id}")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjectHistory(@javax.ws.rs.core.Context HttpServletRequest request,  @PathParam("id") String sObjectId) throws Exception {
		Response res = null;
		try {			
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return PGAPPRequestServices.getObjectHistory(context, sObjectId);
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
