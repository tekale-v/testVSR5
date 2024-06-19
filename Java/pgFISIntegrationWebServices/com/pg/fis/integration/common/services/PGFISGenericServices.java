package com.pg.fis.integration.common.services;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.JsonWebApplicationException;
import com.dassault_systemes.platform.restServices.RestService;
import matrix.db.Context;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@javax.ws.rs.Path(value = "/fisgenericservices")
public class PGFISGenericServices extends RestService {

	static final String EXCEPTION_MESSAGE = "Exception in PGFISGenericServices";
	static final Logger logger = Logger.getLogger(PGFISGenericServices.class.getName());

	/**
	 * REST method to update Reference attribute on given physical Id
	 * @param HTTPServletRequest - Request for REST service
	 * @param pyisicalId - Business Object physicalId
	 * @body updateReferenceJson - Json with values to be updated
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */

	@PUT
	@Path("/{physicalid}/updatereference")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateObjectReference(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@PathParam("physicalid") String physicalId, Map<String, Object> updateReferenceMap) {
		Context context = null;
		boolean isSCMandatory = false;
		Response res = null;
		try {
			context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			if (context != null) {
				res = PGFISGenericServicesDetails.updateReference(context, physicalId, updateReferenceMap);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			String returnErrorJSON = JsonWebApplicationException.buildJsonObject("BIO500", e.getMessage()).toString();
			res = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(returnErrorJSON).build();
		} finally {
			if (context != null) {
				context.close();
			}
		}
		return res;
	}
}