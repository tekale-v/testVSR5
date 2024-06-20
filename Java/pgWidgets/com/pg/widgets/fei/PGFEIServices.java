package com.pg.widgets.fei;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;


@Path("/pgfeiservices")
public class PGFEIServices extends RestService
{
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final Logger logger = Logger.getLogger(PGFEIServices.class.getName());
	static final String EXCEPTION_MESSAGE  = "Exception in PGFEIServices";
	
	/**
	 * Create copy of APP
	 * @param request
	 * @param strInputData
	 * @return Response as json cloned object info
	 * @throws Exception
	 */
	@POST
	@Path("/createVariant")
	public Response createVariant(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) throws Exception
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);				
			String strOutput = PGFEIUtil.createClone(context,strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get the Job status
	 * @param request : HttpServletRequest request param
	 * @param strJobId : String Job object id
	 * @return : Response json with Job details
	 * @throws Exception
	 */
	@GET
	@Path("/getJobStatus")
	public Response getJobStatus(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(PGFEIUtil.KEY_JOB_ID) String strJobId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonOutput = PGFEIUtil.getJobStatus(context, strJobId);
			String strOutput = jsonOutput.toString();
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to create a new Experiment(Workspace Vault) under a workspace
	 * @param request
	 * @param strInputData
	 * @return Response as json with created Experiment Id
	 * @throws Exception
	 */
	@POST
	@Path("/createExperiment")
	public Response createExperiment(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) throws Exception
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.createExperiment(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to connect an existing content object to Experiment
	 * @param request
	 * @param strInputData
	 * @return Response as json with connection status
	 * @throws Exception
	 */
	@POST
	@Path("/addexistingcontent")
	public Response addExistingContent(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) throws Exception
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.addNewVariant(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get experiment info
	 * @param request
	 * @param strInputData
	 * @return Response as json with requested Experiment Info in array
	 * @throws Exception
	 */
	@GET
	@Path("/getExperimentInfo")
	public Response getExperimentInfo(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strObjectId, @QueryParam(PGWidgetConstants.OBJ_SELECT) String strObjSelectables) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getExperimentsDetail(context, strObjectId, strObjSelectables);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	
	/**
	 * Method to get Experiments(Workspace Vaults) under a workspace
	 * @param request
	 * @param strInputData
	 * @return Response as json with all Experiments in array
	 * @throws Exception
	 */
	@POST
	@Path("/getAllExperiments")
	public Response getAllExperiments(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getAllExperiments(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get Content(any type supported by Rel 'Vaulted Documents') under a Experiment(Workspace Vaults)
	 * @param request
	 * @param strInputData
	 * @return Response as json with all content objects in array
	 * @throws Exception
	 */
	@GET
	@Path("/getExperimentContents")
	public Response getExperimentContents(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strObjectId, @QueryParam(PGWidgetConstants.OBJ_SELECT) String strObjSelectables,
			@QueryParam(PGWidgetConstants.KEY_RELATIONSHIPSELECTS) String strRelSelectables) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getExperimentContents(context, strObjectId, strObjSelectables, strRelSelectables);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get BOM structure for contents of an Experiment
	 * @param request
	 * @param strObjectId
	 * @param strObjSelectables
	 * @param strRelSelectables
	 * @return Response with BOM structure
	 */
	@GET
	@Path("/getbomstructure")
	public Response getBOMStructure(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(DomainConstants.SELECT_ID) String strObjectId) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getBOMWithVariants(context, strObjectId);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
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
	@Path("/promotedemote")		
	public Response promoteDemote(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strObjId,
			@QueryParam(PGWidgetConstants.KEY_OPERATION) String strOperation){
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGWidgetUtil.promoteDemoteObject(context, strObjId, strOperation);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to delete any object
	 * @param request
	 * @param strObjId
	 * @param strOperation
	 * @return Response as json with status
	 */
	@POST
	@Path("/deleteExperiments")
	public Response deleteExperiments(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strOutput = PGFEIUtil.deleteExperiments(context, jsonInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
		
	}
	
	/**
	 * Method to disconnect any Relationship
	 * @param request
	 * @param strObjId
	 * @param strOperation
	 * @return Response as json with status
	 */
	@POST
	@Path("/disconnectobject")
	public Response disconnectObject(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strOutput = PGFEIUtil.disconnectObject(context, jsonInputData);			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
		
	}
	@POST
	@Path("/removebom")
	public Response removeBOM(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strOutput = PGFEIUtil.removeBOM(context, jsonInputData);					
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
		
	}
	@POST
	@Path("/removecharorcenterline")
	public Response removeCharOrCenterline(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strOutput = PGFEIUtil.removeCharOrCenterline(context, jsonInputData);		
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
		
	}
	
	@POST
	@Path("/updateobject")
	public Response updateObject(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData)
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.updateObject(context, strInputData);			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/updateexperiment")
	public Response updateExperiment(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData)
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.updateExperiment(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/updateData")
	public Response updateData(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData)
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.updateData(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
		
	/**
	 * Get Performance Characteristics of cloned APP
	 * @param request
	 * @param strInputData
	 * @return Response as json cloned object info
	 * @throws Exception
	 */
	@GET
	@Path("/getperformancecharsofAPP")
	public Response getPerformancecharsofAPP(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(DomainConstants.SELECT_ID) String strObjectId) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getPerformanceChars(context, strObjectId);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Get Centerlines of cloned APP
	 * @param request
	 * @param strInputData
	 * @return Response as json cloned object info
	 * @throws Exception
	 */
	@GET
	@Path("/getcenterlineofAPP")
	public Response getCenterlineofAPP(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(DomainConstants.SELECT_ID) String strObjectId) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getCenterline(context, strObjectId);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
		/**
	 * Copy and create new Experiment and create clone of all varients
	 * @param request
	 * @param strInputData
	 * @return Response as json cloned object info
	 * @throws Exception
	 */
	@POST
	@Path("/copyexperiment")
	public Response copyExperiment(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.copyExperiment(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get all Performance Characteristics 
	 * @param request
	 * @return Response as json with all Performance Characteristics in array
	 * @throws Exception
	 */
	@GET
	@Path("/getAllPerformanceCharacteristics")
	public Response getAllPerformanceCharacteristics(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(PGWidgetConstants.KEY_VALUE) String strInputValue) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getAllExtendedData(context, PGFEIUtil.TYPE_PG_PLICHARACTERISTIC,strInputValue);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get all Centerlines 
	 * @param request
	 * @return Response as json with all Centerlines in array
	 * @throws Exception
	 */
	@GET
	@Path("/getAllCenterlines")
	public Response getAllCenterlines(@javax.ws.rs.core.Context HttpServletRequest request,  @QueryParam(PGWidgetConstants.KEY_VALUE) String strInputValue) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getAllExtendedData(context, PGFEIUtil.CENTERLINE_PICKLIST,strInputValue);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get Picklist values 
	 * @param request
	 * @return Response as json with Picklists in array
	 * @throws Exception
	 */
	@POST
	@Path("/getpicklistvalues")
	public Response getPickListValues(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getPickListValues(context, strInputData);	
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * Revise varient and replicate relationship with Experiment
	 * @param request
	 * @param objectId
	 * @return Response as json revised object info
	 * @throws Exception
	 */
	@POST
	@Path("/reviseVariant")
	public Response reviseVariant(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) 
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);				
			String strOutput = PGFEIUtil.reviseVariant(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Create or update Performance Characteristics of Variants
	 * @param request
	 * @param strInputData
	 * @return Response as json cloned object info
	 * @throws Exception
	 */
	@POST
	@Path("/createOrUpdateCharOfVariant")
	public Response createOrUpdateCharOfVariant(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) 
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);				
			String strOutput = PGFEIUtil.createOrUpdateCharOrCenterlineOfVariant(context, strInputData,PGFEIUtil.TYPE_CHARACTERISTIC);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Create or update Centerlines of Varients
	 * @param request
	 * @param strInputData
	 * @return Response as json cloned object info
	 * @throws Exception
	 */
	@POST
	@Path("/createOrUpdateCenterlinesOfVariant")
	public Response createOrUpdateCenterlinesOfVariant(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) 
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);				
			String strOutput = PGFEIUtil.createOrUpdateCharOrCenterlineOfVariant(context, strInputData,PGFEIUtil.TYPE_CENTERLINE);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method to get all Characteristic specifics of Performance Char 
	 * @param request
	 * @return Response as json with all Characteristic specifics in array
	 * @throws Exception
	 */
	@GET
	@Path("/getCharacteristicSpecificsOfChar")
	public Response getCharacteristicSpecificsOfChar(@javax.ws.rs.core.Context HttpServletRequest request) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getCharSpecifics(context);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get all test method specifics of Performance Char 
	 * @param request
	 * @return Response as json with all test method specifics in array
	 * @throws Exception
	 */
	@GET
	@Path("/getTestMethodSpecs")
	public Response getTestMethodSpecs(@javax.ws.rs.core.Context HttpServletRequest request) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.getTestMethodSpecs(context);				
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method to get all Plants 
	 * @param request
	 * @return Response as json with all test method specifics in array
	 * @throws Exception
	 */
	@POST
	@Path("/setCoOwnersOnExperiment")
	public Response setCoOwnersOnExperiment(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strExperimentId, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.setCoOwnersOnExperiment(context,strExperimentId, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to check valid source product data 
	 * @param request
	 * @return Response
	 * @throws Exception
	 */
	@GET
	@Path("/isValidPart")
	public Response isValidPart(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strSourceId, @QueryParam(PGFEIUtil.KEY_SECURITY_CLASSIFICATION) String strSecurityClass) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.isValidPart(context,strSourceId, strSecurityClass);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to check valid BOM item 
	 * @param request
	 * @return Response
	 * @throws Exception
	 */
	@GET
	@Path("/isValidBOMItem")
	public Response isValidBOMItem(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strSourceId, @QueryParam(PGFEIUtil.KEY_SECURITY_CLASSIFICATION) String strSecurityClass) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGFEIUtil.isValidBOMItem(context,strSourceId, strSecurityClass);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
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
			boolean isSCMandatory = false;
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
	 * Method to check if given user is EBP or not
	 * @param request : HttpServletRequest request param
	 * @param strInputData : User names and ids
	 * @return : String json boolean
	 * @throws Exception 
	 */
	@POST
	@Path("/isEBPUser")
	public Response isEBPUser(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData)
					throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);				
			String strOutput = PGFEIUtil.isEBPUser(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

}
