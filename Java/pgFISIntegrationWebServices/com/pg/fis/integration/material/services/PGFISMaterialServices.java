package com.pg.fis.integration.material.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import matrix.db.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@javax.ws.rs.Path(value = "/fismaterialservices")
public class PGFISMaterialServices extends RestService {

	static final String EXCEPTION_MESSAGE = "Exception in PGFISMaterialServices";
	static final Logger logger = Logger.getLogger(PGFISMaterialServices.class.getName());

	/**
	 * REST method to Update On-Premise Material
	 * 
	 * @param request      HTTPServletRequest for REST service
	 * @param mpRequestMap
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@PUT
	@Path("/revisematerial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reviseAndupdateMaterialService(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			Map<String, Object> mpRequestMap) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				PGFISMaterialDetails pgFISMaterialDetailsOjb =  new PGFISMaterialDetails();
				return pgFISMaterialDetailsOjb.reviseAndupdateOnPremMaterial(context, paramHttpServletRequest, mpRequestMap);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

		} finally {
			if (context != null) {
				context.close();
			}
		}

		return res;

	}
	
	/**
	 * REST method to Update On-Premise Material
	 * 
	 * @param request      HTTPServletRequest for REST service
	 * @param requestBody
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */
	
	@PUT
	@Path("/updatematerial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMaterialService(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			String requestBody) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				return PGFISMaterialDetails.updateOnPremMaterial(context, paramHttpServletRequest, requestBody);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

		} finally {
			if (context != null) {
				context.close();
			}
		}   
		return res;

	}
}