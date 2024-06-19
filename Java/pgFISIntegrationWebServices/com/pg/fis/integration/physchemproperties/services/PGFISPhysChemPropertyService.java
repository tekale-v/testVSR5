package com.pg.fis.integration.physchemproperties.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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

@javax.ws.rs.Path(value = "/fisphyschemservices")
public class PGFISPhysChemPropertyService extends RestService {

	static final String EXCEPTION_MESSAGE = "Exception in PGFISPhysChemPropertyService";
	static final Logger logger = Logger.getLogger(PGFISPhysChemPropertyService.class.getName());
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	
	/**
	 * REST method to Update PhysChem Attributes
	 * 
	 * @param request    HTTPServletRequest for REST service
	 * @param JSON MAP with attributes to be updated
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */
	@PUT
	@Path("/updatephyschamproperties")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePhysChemProperties(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			Map<String, Object> mpRequestMap) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				return PGFISPhysChemPropertyDetails.updatePhysChemPropertiesToEnovia(context, mpRequestMap);
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
	 * REST method to get PhysChem Attributes based on passed Selectables.
	 * 
	 * @param request -   HTTPServletRequest for REST service
	 * @param sData - Json String with UUID and selectables
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */
	@POST
	@Path(value = "/getphyschemdata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjectInfoData(@javax.ws.rs.core.Context HttpServletRequest request, String sData) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

			logger.log(Level.INFO, "getObjectInfoData sData" , sData);
			
			res = PGFISPhysChemPropertyDetails.getObjectInfoData(context, sData);

		} catch (Exception e) {  
			logger.log(Level.SEVERE, "Exception in PGFISPhysChemPropertyService getObjectInfoData", e);
			res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} 
		return res; 
	}
}