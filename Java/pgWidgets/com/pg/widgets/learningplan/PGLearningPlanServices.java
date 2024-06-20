package com.pg.widgets.learningplan;

import com.dassault_systemes.platform.restServices.RestService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @since 2018x.5
 * @author
 *
 */
@Path("/pglearningplanservices")
public class PGLearningPlanServices extends RestService
{
	/**
	 * Method calls the another method getRSPStructure which expand the structure of Requirement Specification to get all the related objects info in a JSON
	 * @param request
	 * @param strObjectId : String object id of Requirement Specification
	 * @param strHeaderSelects : String object selectables for RSP
	 * @return : String JSON response with all related data information for RSP
	 * @throws Exception
	 */
	@GET
	@Path("/getData")		
	public Response getData(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("objectId") String strObjectId,
			@QueryParam("headers") String strHeaderSelects) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

			PGLearningPlanUtil pgLearningPlanUtilObj = new PGLearningPlanUtil();
			String strOutput = pgLearningPlanUtilObj.getRSPStructure(context, strObjectId, strHeaderSelects);
			
			res = Response.ok(strOutput).type("application/json").build();
		} catch (Exception e) {
			e.printStackTrace();
			res=Response.ok(e.getMessage()).type("application/json").build();
		}
		return res;
	}
}