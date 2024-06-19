package com.pg.widgets.mydocuments;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.util.MatrixException;


@Path("/pgmydocumentsservices")
public class PGMyDocumentsServices extends RestService
{

	static final String TYPE_PATTERN = "TypePattern";
	static final String NAME_PATTERN = "NamePattern";
	static final String REVISION_PATTERN = "RevisionPattern";
	static final String OBJECT_LIMIT = "ObjectLimit";
	static final String WHERE_EXP = "WhereExpression";
	static final String EXPAND_TYPE = "ExpandType";
	static final String OBJ_SELECT = "ObjectSelects";
	static final String DURATION = "Duration";
	static final String ALLOWED_STATE = "AllowedStates";
	private static final Logger logger = Logger.getLogger(PGMyDocumentsServices.class.getName());
	static final String BUILD_APPLICATION_JSON = "application/json";

	@GET
	@Path("/myDocuments")
	/**
	 * REST method to get the my document details
	 * 
	 * @param request
	 *            HTTPServletRequest for REST service
	 @param TypePattern
	 *            The appropriate types for the functionality of the widget
	 @param NamePattern
	 *            The appropriate Name for the functionality of the widget
	 @param RevisionPattern
	 *            The appropriate revision for the functionality of the widget
	 @param WhereExpression
	 *            The where clause, if needed, for the functionality of the widget
	 * @param strAllowedStates
	 *            The appropriate states for the functionality of PQR widget
	 * @return response in JSON format
	 * @throws Exception
	 *             when operation fails
	 */
	public Response getMyDocuments(@javax.ws.rs.core.Context HttpServletRequest request, 
			@QueryParam(TYPE_PATTERN) String typePattern,
			@QueryParam(NAME_PATTERN) String namePattern,
			@QueryParam(REVISION_PATTERN) String revisionPattern,
			@QueryParam(WHERE_EXP) String whereExpression,
			@QueryParam(EXPAND_TYPE) String expandType,
			@QueryParam(OBJECT_LIMIT) String objectLimit,			
			@QueryParam(OBJ_SELECT) String objectSelects,
			@QueryParam(DURATION) String Duration,
			@QueryParam(ALLOWED_STATE) String AllowedStates) throws Exception	
	{
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(TYPE_PATTERN, typePattern);
			mpParamMAP.put(NAME_PATTERN, namePattern);
			mpParamMAP.put(REVISION_PATTERN, revisionPattern);
			mpParamMAP.put(WHERE_EXP, whereExpression);
			mpParamMAP.put(EXPAND_TYPE, expandType);
			mpParamMAP.put(OBJECT_LIMIT, objectLimit);
			mpParamMAP.put(OBJ_SELECT, objectSelects);
			mpParamMAP.put(DURATION, Duration);
			mpParamMAP.put(ALLOWED_STATE, AllowedStates);
			
			  String strOutput =PGMyDocuments.getMyDocuments(context,mpParamMAP);

			
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	} 
	
		@GET
		@Path("/getAllowedStates")
		/**
		 * REST method to fetch the allowed states for Documents
		 * 
		 * @param request
		 *            HttpServletRequest
		 * @param strAllowedStates
		 *            State-names that are checked against the allowed states
		 * @return Response created with the valid list of states in JSON format
		 */
		public Response getAllowedStates(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("AllowedStates") String strAllowedStates) throws Exception {
			Response res = null;
			try {
				boolean isSCMandatory = false;
				matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
				String output = PGMyDocuments.getAllowedStates(context, strAllowedStates);
				res = Response.ok(output).type(BUILD_APPLICATION_JSON).build();
			} catch (Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
			}
			return res;
		}

	
	
	/***
	 * This method is called to create Document with files checkin
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @returns Document info
	 */
	@POST
	@Path("/createdocument")
	public Response createDocument(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = PGMyDocuments.createDocumentAndCheckinFile(context, paramString, paramHttpServletRequest);
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE,  e.getMessage(), e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/editdocument")
	public Response editDocument(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = PGMyDocuments.editDocumentWithFile(context, paramString, paramHttpServletRequest);
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE,  e.getMessage(), e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@GET
	@Path("/deletedocument")
	public Response deleteDocument(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectIds") String paramString) {
		Response res = null;
		String strOutput = DomainConstants.EMPTY_STRING;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);

			strOutput = PGMyDocuments.deleteObjects(context, paramString);
			res = Response.ok(strOutput).type(BUILD_APPLICATION_JSON).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE,  e.getMessage(), e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}
