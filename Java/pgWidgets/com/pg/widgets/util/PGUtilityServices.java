package com.pg.widgets.util;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;


@Path("/pgutilityservices")
public class PGUtilityServices extends RestService
{
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final Logger logger = Logger.getLogger(PGUtilityServices.class.getName());
	static final String EXCEPTION_MESSAGE  = "Exception in PGUtilityServices";

	
	/**
	 * Method to get Hierarchy structure for tree format Ag-grid
	 * @param request
	 * @param strInputData
	 * @return Response as JSON array
	 */
	@POST
	@Path("/getgridtreeformat")
	public Response getGridTreeFormat(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getGridTreeFormat(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	/**
	 * Method to update Object or Rel attributes in Tree Grid
	 * @param request
	 * @param strInputData
	 * @return Response as JSON Object with status and tree data(if requested)
	 */
	@POST
	@Path("/updategriddata")
	public Response updateGridData(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.updateGridData(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	/**
	 * Method to update Object or Rel attributes
	 * @param request
	 * @param strInputData
	 * @return Response as JSON Object with status
	 */
	@POST
	@Path("/updateobjectrelattributes")
	public Response updateObjectRelAttributes(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.updateObjectOrRel(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	/**
	 * Method to add new row in Tree Grid
	 * @param request
	 * @param strInputData
	 * @return Response as JSON Object with status and tree data(if requested)
	 */
	@POST
	@Path("/addnewrowtotree")
	public Response addNewRowToTree(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.addNewRowToTree(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	/**
	 * Method to remove rows from Tree Grid
	 * @param request
	 * @param strInputData
	 * @return Response as JSON Object with status and tree data(if requested)
	 */
	@POST
	@Path("/removerowsfromtree")
	public Response removeRowsFromTree(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.removeRowsFromTree(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	/**
	 * Method to promote or demote any object
	 * @param request
	 * @param strObjId
	 * @param strOperation
	 * @return Response as json with status
	 */
	@GET
	@Path("/setobjectstate")		
	public Response promoteDemote(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strObjId,
			@QueryParam(PGWidgetConstants.KEY_OPERATION) String strOperation,
			@QueryParam(PGWidgetConstants.KEY_STATE) String strState){
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.setObjectState(context, strObjId, strOperation, strState);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Method to get info for an object
	 * @param request
	 * @param sData
	 * @return Response as json with status
	 */

	@POST
	@Path(value = "/getDataFromDatabase")
	public Response getObjectInfoData(@javax.ws.rs.core.Context HttpServletRequest request, String sData) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

			logger.log(Level.INFO, "getObjectInfoData sData" , sData);
			
			String strOutput = PGWidgetUtil.getObjectInfoData(context, sData);
			logger.log(Level.INFO, "getObjectInfoData strOutput", strOutput);

			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {  
			logger.log(Level.SEVERE, "Exception in PGUtilityServices getObjectInfoData", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res; 
	}

	/**
	 * Method to get all IP Control classes for current user
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String classification value Restricted or 'Highly Restricted' along with object selects
	 * @return : String json with list of IP Classes
	 * @throws Exception 
	 */
	@GET
	@Path("/getIPClassesForUser")
	public Response getIPClassesForUser(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getIPClassesForUser(context, strInputData);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Method to upload file
	 * @param request : HttpServletRequest request param
	 * @return : Response string with result message
	 * @throws Exception 
	 */
	@POST
	@Path("/fileUpload")
	public Response fileUpload(@javax.ws.rs.core.Context HttpServletRequest request ) {
		Response res = null;
		String strOut = null;
		try{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String file = request.getParameter("file");
			String objectId = request.getParameter("objectID");
			boolean result = PGWidgetUtil.fileUpload(context, objectId, file);
			strOut = (result ? "File uploaded successfully" : "Error uploading file");
			res = Response.ok(strOut).type(TYPE_APPLICATION_FORMAT).build();
		}catch(Exception e){
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to upload file
	 * @param request : HttpServletRequest request param
	 * @return : Response string with result message
	 * @throws Exception 
	 */
	@POST
	@Path("/fileUploadCustom")
	public Response fileUploadCustomFlow(@javax.ws.rs.core.Context HttpServletRequest request , String strInput) {
		Response res = null;
		String strOut = null;
		try{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOut = PGWidgetUtil.fileUploadCustom(context, request, strInput);
//			strOut = (result ? "File uploaded successfully" : "Error uploading file");
			res = Response.ok(strOut).type(TYPE_APPLICATION_FORMAT).build();
		}catch(Exception e){
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to fetch details for specific tab in Object view
	 * @param request
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	@Path("/getRelatedTabInfo")
	@POST
	public Response getRelatedTabInfo(@Context HttpServletRequest request,Map<String,String> mpRequestMap) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getImageTabInfo(context,mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@GET
	@Path("/getDefaultIRMUserPreferences")
	public Response getDefaultIRMUserPreferences(@javax.ws.rs.core.Context HttpServletRequest request)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getDefaultIRMUserPreferences(context);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@POST
	@Path("/connectdisconnectobjects")
	public Response connectDisconnectObjects(@Context HttpServletRequest request, String strInput) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.connectDisconnectObjects(context, strInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getrelateddocumentobjects")
	public Response getRelatedObjectData(@Context HttpServletRequest request, String strInput) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getRelatedObjectData(context, strInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
    @POST
	@Path("/getAttributeRangeValues")
	public Response getAttributeRangeValues(@javax.ws.rs.core.Context HttpServletRequest request, String data) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

			JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(data);
			String strAttributeNames = jsonInputInfo.getString("RANGE_ATTRIBUTE_NAMES");
			JsonObjectBuilder result = Json.createObjectBuilder();
			result.add("RangeAttributeValues", PGWidgetUtil.getAttributeRangeValues(context, strAttributeNames));

			res = Response.ok(PGWidgetUtil.getAttributeRangeValues(context, strAttributeNames))
					.type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getobjectidfromuuid")
	public Response getObjectIdFromUUID(@Context HttpServletRequest request, String strInput) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getObjectIdFromUUID(context, strInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/addexistingobjects")
	public Response addExistingObjects(@Context HttpServletRequest request, String strInput) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.addExistingObjects(context, strInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/createdomainobject")
	public Response createDomainObject(@Context HttpServletRequest request, String strInput) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.createDomainObject(context, strInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	@POST
	@Path("/getobjectinfo")
	public Response getObjectInfo(@javax.ws.rs.core.Context HttpServletRequest request, String strInput)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			Map mapInput = PGWidgetUtil.getMapFromJson(context, PGWidgetUtil.getJsonFromJsonString(strInput));
			JsonObjectBuilder jsonOutput = PGWidgetUtil.getObjectInfo(context, mapInput);
			res = Response.ok(jsonOutput.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@GET
	@Path("/getuserpreferencetemplatedata")
	public Response getUserPreferenceTemplateData(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getUserPreferenceTemplateData(context, strInput);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@GET
	@Path("/getdsmuserpreferencetemplatedata")
	public Response getDSMUserPreferenceTemplateData(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getDSMUserPreferenceTemplateData(context, strInput);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to revise Object
	 * @param request
	 * @param strInputData
	 * @return Response as JSON Object with status and tree data(if requested)
	 */
	@POST
	@Path("/reviseObject")
	public Response reviseObject(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.reviseObject(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	/**
	 * The service is to get generic person roles
	 * @return
	 */
	@GET
	@Path("/getpersonroles")
	public Response getPersonRoles(@javax.ws.rs.core.Context HttpServletRequest request) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.getPersonRoles(context);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}


	@POST
	@Path("/findObjects")
	/**
	 * REST method to find object of specified type
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
	 *            The appropriate states
	 * @return response in JSON format
	 * @throws Exception
	 *             when operation fails
	 */
	public Response findObjects(@javax.ws.rs.core.Context HttpServletRequest request,String paramString
		) throws Exception	
	{
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);			
			JsonObject jsonOutputData =PGWidgetUtil.findObjectsJson(context,jsonInputData);			
			res = Response.ok(jsonOutputData).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	} 
	
	@POST
	@Path("/getAllRevision")
	public Response getAllRevision(@javax.ws.rs.core.Context HttpServletRequest request,String paramString) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject strOutput = PGWidgetUtil.getAllRevision(context,paramString);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get all the 'User Preference Templates'
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getAllUserPreferenceTemplates")
	public Response getAllUserPreferenceTemplates(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGWidgetUtil pgWidgetUtil = new PGWidgetUtil(); 
			String strOutput = pgWidgetUtil.getAllUserPreferenceTemplates(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to get pick list values for Multi-parents
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getPickListForMultiParents")
	public Response getPickListForMultiParents(@javax.ws.rs.core.Context HttpServletRequest request, String sParamString) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject strOutput = PGWidgetUtil.getPickListForMultiParents(context,sParamString);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to create and edit view format Ag-grid
	 * @param request
	 * @param strInputData
	 * @return Response as JSON array
	 */
	@POST
	@Path("/createOrUpdateDataObjects")
	public Response createOrUpdateDataObjects(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.createOrUpdateDataObjects(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices ", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	/**
	 * Method to Fetch view format Ag-grid
	 * @param request
	 * @param strInputData
	 * @return Response as JSON array
	 */
	@POST
	@Path("/fetchDataObject")
	public Response fetchDataObject(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.fetchDataObject(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices ", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	/**
	 * Method to Fetch view format Ag-grid
	 * @param request
	 * @param strInputData
	 * @return Response as JSON array
	 */
	@POST
	@Path("/setDataObjectDescription")
	public Response setDataObjectDescription(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.setDataObjectDescription(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in PGUtilityServices ", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
}
