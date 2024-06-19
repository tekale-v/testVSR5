package com.pg.fis.integration.picklist.services;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

@javax.ws.rs.Path(value = "/fispicklistservices")
public class PGFISPickListServices extends RestService {

	static final String EXCEPTION_MESSAGE = "Exception in PGFISPickListServices";
	static final Logger logger = Logger.getLogger(PGFISPickListServices.class.getName());
	public static final String MANDETORY_DETAILS_MISSING = "Mandatory information(query Param 'id') is empty or null";

	/**
	 * REST method to get PickList Details
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@GET
	@javax.ws.rs.Path("/getpicklistdetails/{physicalId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPickListDetails(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalId") String physicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					return PGFISPickListDetails.getPickListDetailsResponse(context, physicalId);
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
	 * REST method to get PickList Product Classification Combinations
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @QueryParam id - Child picklist Business Object physicalId
	 * @QueryParam parentid - parent picklist Business Object physicalId
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@GET
	@javax.ws.rs.Path("/getpicklistcombinations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPickListCombinations(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@QueryParam("id") String childPhysicalId, @QueryParam("parentid") String parentPhysicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(childPhysicalId)) {
					return PGFISPickListDetails.getPickListCombinationsResponse(context, childPhysicalId, parentPhysicalId);
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