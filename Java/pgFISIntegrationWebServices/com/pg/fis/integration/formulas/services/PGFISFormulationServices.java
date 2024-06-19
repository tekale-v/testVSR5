package com.pg.fis.integration.formulas.services;

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

@javax.ws.rs.Path(value = "/fisformservices")
public class PGFISFormulationServices extends RestService {

	static final String EXCEPTION_MESSAGE = "Exception in PGFISFormulationServices";
	static final Logger logger = Logger.getLogger(PGFISFormulationServices.class.getName());

	/**
	 * REST method to get GCAS Number For FIS Integration
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@GET
	@javax.ws.rs.Path("/getgcascode/{cloudphysicalid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGCAScode(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @PathParam("cloudphysicalid") String physicalId, @QueryParam("enterpriseId") String enterpriseId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					res =  PGFISFormulationDetails.getGCASCodeFromOnPrem(context, physicalId, enterpriseId);
				} else {
					JsonObjectBuilder output = Json.createObjectBuilder();
					output.add(PGFISWSConstants.STRING_MESSAGE, PGFISWSConstants.MANDETORY_CLOUD_DETAILS_MISSING);
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
	 * REST method to get cloud status for FBOM for given Formulation Process
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@GET
	@javax.ws.rs.Path("/getformulationcloudflag/{physicalId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormulationCloudFlag(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalId") String physicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					return PGFISFormulationDetails.getFormulationCloudFlagResponse(context, physicalId);
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
	 * method to get crossreference details For FIS Integration
	 * 
	 * @param request    HTTPServletRequest for REST service	 
	 * @param physicalId - physicalId of context object
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	
	@GET
	@javax.ws.rs.Path("/getformulacrossreference/{physicalId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCrossReference(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalId") String physicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					return PGFISFormulationDetails.getCrossReferenceResponse(context, physicalId);
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
	 * method to getFormulated Material details For FIS Integration
	 * 
	 * @param request    HTTPServletRequest for REST service	 
	 * @param physicalId - physicalId of context object
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	
	@GET
	@javax.ws.rs.Path("/getformulatedmaterialdetails/{physicalId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormulatedMaterialDetails(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalId") String physicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					return PGFISFormulationDetails.getFormulatedMaterialDetailsResponse(context, physicalId);
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
	
	@GET
	@javax.ws.rs.Path("/getformulatedmaterialreference/{physicalId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormulatedMaterialReference(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalId") String physicalId) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				if (UIUtil.isNotNullAndNotEmpty(physicalId)) {
					return PGFISFormulationDetails.getFormulatedMaterialReferenceResponse(context, physicalId);
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
