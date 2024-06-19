package com.pg.fis.integration.securitymngt.services;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;

import matrix.db.Context;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;
import java.util.logging.Level;
import java.util.logging.Logger;

@javax.ws.rs.Path(value = "/fissecmngtservices")
public class PGFISSecurityMngtServices extends RestService {

	static final String EXCEPTION_MESSAGE = "Exception in PGFISSecurityMngtServices";
	static final Logger logger = Logger.getLogger(PGFISSecurityMngtServices.class.getName());

	/**
	 * REST method to get IP Control Class details by passing physical Id.
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @param pyisicalId Business Object physical Id
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@GET
	@javax.ws.rs.Path("/getfisipsecdetails/{physicalId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIPSecurityDetails(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalId") String physicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					return PGFISSecurityMngtDetails.getIPSecurityDetailsResponse(context, physicalId);
				} else {
					JsonObjectBuilder output = Json.createObjectBuilder();
					output.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.MANDETORY_DETAILS_MISSING);
					res = Response.status(Status.BAD_REQUEST).entity(output.build().toString()).build();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} finally {
			if (context != null) {
				context.close();
			}
		}
		return res;
	}
	
	
	/**
	 * REST method to get Security/IP Control Class List connected to Person by passing trigram/emailId of Person
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @param pyisicalId Business Object physical Id
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */
	
	@GET
	@javax.ws.rs.Path("/getsecuritygroupsdetails/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIPSecurityDetailsFromUserId(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("userId") String userId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(userId)) {
					return PGFISSecurityMngtDetails.getIPSecurityDetailsFromUserIdResponse(context, userId);
				} else {
					JsonObjectBuilder output = Json.createObjectBuilder();
					output.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.MANDETORY_DETAILS_MISSING);
					res = Response.status(Status.BAD_REQUEST).entity(output.build().toString()).build();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} finally {
			if (context != null) {
				context.close();
			}
		}
		return res;
	}
	
	/**
	 * REST method to get Security/IP Control Class List connected to Person by passing trigram/emailId of Person
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @param pyisicalId Business Object physical Id
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */
	
	@GET
	@javax.ws.rs.Path("/getallsecuritygroupsdetails/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllIPSecurityDetailsFromUserId(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("userId") String userId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(userId)) {
					return PGFISSecurityMngtDetails.getAllIPSecurityDetailsFromUserIdResponse(context, userId);
				} else {
					JsonObjectBuilder output = Json.createObjectBuilder();
					output.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.MANDETORY_DETAILS_MISSING);
					res = Response.status(Status.BAD_REQUEST).entity(output.build().toString()).build();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} finally {
			if (context != null) {
				context.close();
			}
		}
		return res;
	}
	
	/**
	 * REST method to get IP Control Class details by passing physical Id For Migration.
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @param pyisicalId Business Object physical Id
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@GET
	@javax.ws.rs.Path("/getfisipsecdetailsformigration/{physicalId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIPSecurityDetailsForMigration(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalId") String physicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					return PGFISSecurityMngtDetails.getIPSecurityDetailsResponseForMigration(context, physicalId);
				} else {
					JsonObjectBuilder output = Json.createObjectBuilder();
					output.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.MANDETORY_DETAILS_MISSING);
					res = Response.status(Status.BAD_REQUEST).entity(output.build().toString()).build();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} finally {
			if (context != null) {
				context.close();
			}
		}
		return res;
	}

}
