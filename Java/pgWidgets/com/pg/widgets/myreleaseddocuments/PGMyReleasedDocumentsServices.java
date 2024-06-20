package com.pg.widgets.myreleaseddocuments;

import com.dassault_systemes.platform.restServices.RestService;



import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;


@Path("/pgreleaseddocumentsservices")
public class PGMyReleasedDocumentsServices extends RestService
{

	private static final Logger logger = Logger.getLogger(PGMyReleasedDocumentsServices.class.getName());
	static final String ERROR_IN_GETPROJECTDOCUMENT_IN_MYRELEASEDDOCUMENT = "Exception in PGMyReleasedDocuments : getProjectDocuments::";
	
	static final String TYPE_PATTERN = "TypePattern";
	static final String PROJECT_ATTRIBUTES = "ProjectAttributes";
	static final String RELEASED_IN_DURATION = "ReleasedInDuration";
	static final String OBJECT_LIMIT = "ObjectLimit";
	static final String PROJECT_ID = "ProjectId";
	
	static final String OBJ_SELECT = "ObjectSelects";
	static final String BUILD_APPLICATION_JSON = "application/json";

	@GET
	@Path("/myDocuments")
	/**
	 * REST method to get my released document details
	 * 
	 * @param request
	 *            HTTPServletRequest for REST service
	 * @param ProjectId
	 *            Project for which the document is displayed
	 * @param ProjectAttributes
	 *            The appropriate attributes for the functionality the widget
	 * @param TypePattern
	 *            The appropriate types for the functionality of the widget
	 * @return response in JSON format
	 * @throws Exception
	 *             when operation fails
	 */
	public Response getProjectDocuments(@javax.ws.rs.core.Context HttpServletRequest request, 
			@QueryParam(PROJECT_ID) String ProjectId,
			@QueryParam(PROJECT_ATTRIBUTES) String ProjectAttributes,
			@QueryParam(RELEASED_IN_DURATION) String ReleasedInDuration,
			@QueryParam(OBJECT_LIMIT) String ObjectLimit,			
			@QueryParam(OBJ_SELECT) String ObjectSelects,
			@QueryParam(TYPE_PATTERN) String typePattern) throws Exception		
	{
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(PROJECT_ID, ProjectId);
			mpParamMAP.put(PROJECT_ATTRIBUTES, ProjectAttributes);
			mpParamMAP.put(RELEASED_IN_DURATION, ReleasedInDuration);
			mpParamMAP.put(OBJECT_LIMIT, ObjectLimit);
			mpParamMAP.put(OBJ_SELECT, ObjectSelects);
			mpParamMAP.put(TYPE_PATTERN, typePattern);
			String strOutput = PGMyReleasedDocuments.getProjectDocuments(context,mpParamMAP);
			//String output =JPO.invoke(context, "PGMyReleasedDocuments", null, "getProjectDocuments", JPO.packArgs(mpParamMAP), String.class);
			
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, ERROR_IN_GETPROJECTDOCUMENT_IN_MYRELEASEDDOCUMENT + e.getMessage(), e);
			throw e;
		}
		return res;
	}  



}
