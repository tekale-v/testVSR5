/**
 * PGPQRServices.java. Class with Rest Webservices to fetch data for PGPQRWidget
 * Copyright (c) Dassault Systemes.
 * All Rights Reserved.
 *
 */
package com.pg.widgets.pqr;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;

import matrix.db.JPO;

@Path("/pqrservices")
public class PGPQRServices extends RestService {
	private static Logger logger = Logger.getLogger(PGPQRServices.class.getName());
	
	static final String ERROR_PGWIDGET_GETALLOWEDSTATE = "error thrown from PGWidgets in method getAllowedStates";
	static final String ERROR_PGWIDGET_INCORRECTSECURITCONTEXT = "error thrown from PGWidgets : getSecurityContexts ::";
	static final String ERROR_PGWidgets_MEPSEPDETAILS = "Error thrown from PGWidgets : getMEPSEPDetailsJSON";
	
	static final String ALLOWED_TYPE = "AllowedTypes";
	static final String ALLOWED_STATES = "AllowedStates";
	static final String ENTERPRISE_ID = "EnterprisePartID";
	static final String BUILD_APPLICATION_JSON = "application/json";
	@GET
	@Path("/getMEPSEPDetailsJSON")
	/**
	 * REST method to MEP and SEP details
	 * 
	 * @param request
	 *            HTTPServletRequest for REST service
	 * @param strEnterprisePartID
	 *            Part for which the PQRWidget table is displayed
	 * @param strAllowedStates
	 *            The appropriate states for the functionality of PQR widget
	 * @param strAllowedTypes
	 *            The appropriate types for the functionality of PQR widget
	 * @return response in JSON format
	 * @throws Exception
	 *             when operation fails
	 */
	public Response getMEPSEPDetailsJSON(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(ENTERPRISE_ID) String strEnterprisePartID,
			@QueryParam(ALLOWED_STATES) String strAllowedStates, @QueryParam(ALLOWED_TYPE) String strAllowedTypes) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			Map<String, Object> theMepAttributesMap = new HashMap<>();
			theMepAttributesMap.put(ENTERPRISE_ID, strEnterprisePartID);
			theMepAttributesMap.put(ALLOWED_STATES, strAllowedStates);
			theMepAttributesMap.put(ALLOWED_TYPE, strAllowedTypes);
			String output = PGPQRReport.getMEPSEPDetailsJSON(context, JPO.packArgs(theMepAttributesMap));
			res = Response.ok(output).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, ERROR_PGWidgets_MEPSEPDETAILS, e);
		}
		return res;
	}

	@GET
	@Path("/getSecurityContexts")
	/**
	 * The method has the following input parameters: Request from the session Returns: Response created with the context users' Security context in
	 * 
	 * @param request
	 *            HttpServletRequest for REST service
	 * @return response in JSON format
	 */
	public Response getSecurityContexts(@javax.ws.rs.core.Context HttpServletRequest request) {
		Response res = null;
		try {
			JsonObjectBuilder output;
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			output = (JsonObjectBuilder) PGPQRReport.getSecurityContexts(context);
			res = Response.status(200).entity(output.build().toString()).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, ERROR_PGWIDGET_INCORRECTSECURITCONTEXT, e);
		}
		return res;
	}

	@GET
	@Path("/getAllowedStates")
	/**
	 * REST method to fetch the allowed states for MEPs
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param strAllowedStates
	 *            State-names that are checked against the allowed states
	 * @return Response created with the valid list of states in JSON format
	 */
	public Response getAllowedStates(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(ALLOWED_STATES) String strAllowedStates) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String output = PGPQRReport.getAllowedStates(context, strAllowedStates);
			res = Response.ok(output).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, ERROR_PGWIDGET_GETALLOWEDSTATE, e);
		}
		return res;
	}
}
