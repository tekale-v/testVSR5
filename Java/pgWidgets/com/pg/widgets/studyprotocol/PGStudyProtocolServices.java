package com.pg.widgets.studyprotocol;

import com.dassault_systemes.platform.restServices.RestService;

import java.util.HashMap;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.util.StringList;
@Path("/studyprotocolservices")
public class PGStudyProtocolServices extends RestService {
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final String EXCEPTION_MESSAGE  = "Exception in PGStudyProtocolServices";
	static final Logger logger = Logger.getLogger(PGStudyProtocolServices.class.getName());

	/**
	 * REST method to get Study Protocol details
	 * 
	 * @param request          HTTPServletRequest for REST service
	 * @param TypePattern      The appropriate types for the functionality of the
	 *                         widget
	 * @param NamePattern      The appropriate Name for the functionality of the
	 *                         widget
	 * @param RevisionPattern  The appropriate revision for the functionality of the
	 *                         widget
	 * @param WhereExpression  The where clause, if needed, for the functionality of
	 *                         the widget
	 * @param strAllowedStates The appropriate states for the functionality of PQR
	 *                         widget
	 * @return response in JSON format
	 * @throws Exception when operation fails
	 */
	@GET
	@Path("/myStudyProtocol")
	public Response getMyStudyProtocol(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(PGWidgetConstants.TYPE_PATTERN) String typePattern, @QueryParam(PGWidgetConstants.NAME_PATTERN) String namePattern,
			@QueryParam(PGWidgetConstants.REVISION_PATTERN) String revisionPattern, @QueryParam(PGWidgetConstants.WHERE_EXP) String whereExpression,
			@QueryParam(PGWidgetConstants.EXPAND_TYPE) String expandType, @QueryParam(PGWidgetConstants.OBJECT_LIMIT) String objectLimit,
			@QueryParam(PGWidgetConstants.OBJ_SELECT) String objectSelects, @QueryParam(PGWidgetConstants.DURATION) String duration,
			@QueryParam(PGWidgetConstants.ALLOWED_STATE) String allowedStates, @QueryParam(PGWidgetConstants.SHOW_OWNED) String showOwned) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(PGWidgetConstants.TYPE_PATTERN, typePattern);
			mpParamMAP.put(PGWidgetConstants.NAME_PATTERN, namePattern);
			mpParamMAP.put(PGWidgetConstants.REVISION_PATTERN, revisionPattern);
			mpParamMAP.put(PGWidgetConstants.WHERE_EXP, whereExpression);
			mpParamMAP.put(PGWidgetConstants.EXPAND_TYPE, expandType);
			mpParamMAP.put(PGWidgetConstants.OBJECT_LIMIT, objectLimit);
			mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, objectSelects);
			mpParamMAP.put(PGWidgetConstants.DURATION, duration);
			mpParamMAP.put(PGWidgetConstants.ALLOWED_STATE, allowedStates);
			mpParamMAP.put(PGWidgetConstants.SHOW_OWNED, showOwned);

			String strOutput = PGStudyProtocol.getMyStudyProtocol(context, mpParamMAP);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Get Study Protocol object properties
	 * @param request
	 * 
	 * @param strOjectId The object id
	 * 
	 * @param strObjSelectables The expressions for fields to be fetched
	 * 
	 * @return
	 */
	@GET
	@Path("/getObjectInfo")
	public Response getStudyProtocolProperties(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(DomainConstants.SELECT_ID) String strOjectId, @QueryParam(PGWidgetConstants.OBJ_SELECT) String strObjSelectables) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			StringList slObjectSelectables = new StringList();
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			if(UIUtil.isNotNullAndNotEmpty(strObjSelectables)){
				slObjectSelectables = StringUtil.split(strObjSelectables, ",");
			}
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(DomainConstants.SELECT_ID, strOjectId);
			mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, slObjectSelectables);

			String strOutput = PGStudyProtocol.getStudyProtocolProperties(context, mpParamMAP);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Get initial data for create screen
	 * @param request
	 * @return
	 */
	@GET
	@Path("/getInitialDataOnCreate")
	public Response getInitialDataOnCreate(@javax.ws.rs.core.Context HttpServletRequest request) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.getInitialDataOnCreate(context);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Get Study Leg information of context object
	 * @param request
	 * 
	 * @param strOjectId The object id 
	 * 
	 * @param objectSelects The expression for fields to be return
	 * 
	 * @return
	 */
	@POST
	@Path("/getStudyLegDetails")
	public Response getStudyLegDetails(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);		
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(PGWidgetConstants.KEY_OBJECT_ID, jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID));
			mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, jsonInputData.getString(PGWidgetConstants.OBJ_SELECT));		
			String strOutput = PGStudyProtocol.getStudyLegDetails(context, mpParamMAP);			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * This method is to create study leg object and connects to study protocol
	 * @param request
	 * @param strObjectId Study Protocol object id
	 * @param strType Study Leg Tyoe
	 * @param strTitle Title for Study Leg object
	 * @param strDescription Description value for Study Leg screen
	 * @param strObjectSelects The field expression to display data after create
	 * @return
	 */
	@GET
	@Path("/createStudyLeg")
	public Response createStudyLeg(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(PGWidgetConstants.KEY_OBJECT_ID) String strObjectId, @QueryParam(DomainConstants.SELECT_TYPE) String strType,
			@QueryParam(PGWidgetConstants.KEY_TITLE) String strTitle, @QueryParam(DomainConstants.SELECT_DESCRIPTION) String strDescription,
			@QueryParam(PGWidgetConstants.OBJ_SELECT) String strObjectSelects) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(DomainConstants.SELECT_TYPE, strType);
			mpParamMAP.put(DomainConstants.ATTRIBUTE_TITLE, strTitle);
			mpParamMAP.put(DomainConstants.SELECT_DESCRIPTION, strDescription);
			mpParamMAP.put(PGWidgetConstants.KEY_OBJECT_ID, strObjectId);
			mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, strObjectSelects);
			String strOutput = PGStudyProtocol.createStudyLeg(context, mpParamMAP);		
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * Connects Product Parts to Study Leg objects
	 * @param request
	 * @param strInputData The input string of query string param
	 * @return
	 */
	@POST
	@Path("/addExistingProductPart")
	public Response addExistingProductPart(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);		
			String strOutput = PGStudyProtocol.addExistingProductPart(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * This method is to remove connected Product Part Data and Deletes Study Leg object
	 * @param request
	 * @param strInputData The input string of query string param
	 * @return
	 */
	@POST
	@Path("/removeStudyLegOrProductPart")
	public Response removeStudyLegOrProductPart(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);			
			String strOutput = PGStudyProtocol.removeStudyLegOrProductPart(context,strInputData);			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * This method Edit the values for Study Leg page
	 * @param request
	 * @param strInputData The input string of query string param
	 * @return
	 */
	@POST
	@Path("/editStudyLegDetails")
	public Response editStudyLegDetails(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);			
			String strOutput = PGStudyProtocol.editStudyLegDetails(context,strInputData);	
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * This method is update the values for Study Protcol attributes
	 * @param request
	 * @param strInputData The input string of query string param
	 * @return
	 */
	@POST
	@Path("/editProperties")
	public Response editProperties(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);		
			String strOutput = PGStudyProtocol.setUpdatedValues(context, strInputData);		
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Get the initial data before launching Edit page
	 * @param request
	 * @param strInputData The input string of query string param
	 * @return
	 */
	@POST
	@Path("/getInitialDataOnEdit")
	public Response getInitialDataOnEdit(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.getInitialDataOnEdit(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * The method is to create Study Protocol object
	 * @param request
	 * @param title The title for study protocol
	 * @param description The description value for Study Protocol
	 * @param policy The policy for Study Protocol
	 * @param businessArea The selected Business Area value
	 * @param productCategoryPlatform The selected Product Category Platform f
	 * @param strObjSelectables The field expresssion to return the data
	 * @return
	 */
	@GET
	@Path("/createstudyprotocol")
	public Response createStudyProtocol(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(PGWidgetConstants.KEY_TITLE) String title, @QueryParam(PGWidgetConstants.KEY_DESCRIPTION) String description,
			@QueryParam(PGWidgetConstants.KEY_POLICY) String policy, @QueryParam(PGStudyProtocol.KEY_BUSINESSAREA) String businessArea,
			@QueryParam(PGStudyProtocol.PRODUCTCATEGORYPLATFORM) String productCategoryPlatform,
			@QueryParam(PGStudyProtocol.IPCLASSIFICATION) String strIPClassification,
			@QueryParam(PGWidgetConstants.OBJ_SELECT) String strObjSelectables,
			@QueryParam("pgUPTPhyID") String pgUPIPhysicalId) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(PGWidgetConstants.KEY_TITLE, title);
			mpParamMAP.put(PGWidgetConstants.KEY_DESCRIPTION, description);
			mpParamMAP.put(PGWidgetConstants.KEY_POLICY, policy);
			mpParamMAP.put(PGStudyProtocol.KEY_BUSINESSAREA, businessArea);
			mpParamMAP.put(PGStudyProtocol.PRODUCTCATEGORYPLATFORM, productCategoryPlatform);
			mpParamMAP.put(PGStudyProtocol.IPCLASSIFICATION, strIPClassification);
			mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, strObjSelectables);
			mpParamMAP.put(PGWidgetConstants.ATTRIBUTE_PGUPTPHYSICALID, pgUPIPhysicalId);
			String strOutput = PGStudyProtocol.createtStudyProtocol(context, mpParamMAP);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * The ChangeHandler method for Chassis and Platform section
	 * @param request
	 * @param inputData 
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/onChangeHandlerChassisAndPlatform")
	public Response getConnectedChassisAndPlatformOnChange(@javax.ws.rs.core.Context HttpServletRequest request,String inputData)	throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);	
			String strOutput = PGStudyProtocol.getConnectedChassisAndPlatformValues(context, inputData);		
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * The method is to connect existing object to Study Protocol
	 * @param request
	 * @param inputData
	 * @return
	 */
	@POST
	@Path("/addExisting")
	public Response addExisting(@javax.ws.rs.core.Context HttpServletRequest request, String inputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);			
			String strOutput = PGStudyProtocol.addExisting(context, inputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Disconnect the objects from Study Protocol
	 * @param request
	 * @param inputData
	 * @return
	 */
	@POST
	@Path("/removeSelected")
	public Response removeSelected(@javax.ws.rs.core.Context HttpServletRequest request, String inputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);			
			String strOutput = PGStudyProtocol.removeSelected(context,inputData);		
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * get connected Reference Documents objects
	 * @param request
	 * @param strInputData
	 * @return
	 */
	@POST
	@Path("/relatedObjects")
	public Response getRelatedObjects(@javax.ws.rs.core.Context HttpServletRequest request,
			String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.getRelatedObjects(context, strInputData);			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * This method is to return initial data required to launch Chassis and Platform data
	 * @param request
	 * @param strInputData
	 * @return
	 */
	@POST
	@Path("/getInitialDataOnChassisPlatform")
	public Response getInitialDataOnChassisPlatform(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.getInitialDataOnChassisPlatform(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Method to perform promote or demote operations on Study Protocol
	 * @param request
	 * @param strRelId
	 * @param strSelectedCountries
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/promotedemoteStudyProtocol")		
	public Response promoteDemoteStudyProtocol(@javax.ws.rs.core.Context HttpServletRequest request,
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

	@GET
	@Produces("application/pdf")
	@Path("/getAgencyPDFView")
	public Response downloadAgencyPDFView(@javax.ws.rs.core.Context HttpServletRequest request, @javax.ws.rs.core.Context HttpServletResponse response, @QueryParam(PGWidgetConstants.KEY_OBJECT_ID) String strOjectId) throws Exception
	{	
		boolean isSCMandatory = false;
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

		return PGStudyProtocol.getAgencyPDFViewFile(context, response, strOjectId);
	}
	@GET
	@Path("/getAllowedStates")
	/**
	 * REST method to fetch the allowed states for StudyProtocol
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param strAllowedStates
	 *            State-names that are checked against the allowed states
	 * @return Response created with the valid list of states in JSON format
	 */
	public Response getAllowedStates(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("AllowedStates") String strAllowedStates)  {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String output = PGStudyProtocol.getAllowedStates(context, strAllowedStates);
			res = Response.ok(output).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * This method is to create GPS Task for Study Protocol 
	 * @param request
	 * @param strInputData
	 * @return
	 */
	@POST
	@Path("/createGPSTaskForStudyProtocol")
	public Response createGPSTaskForStudyProtocol(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.createGPSTaskForStudyProtocol(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * The method is to Copy or Revise Study Protocol document
	 * @param request
	 * @param strInputData parameters in JSON format 
	 * @return response
	 */
	@POST
	@Path("/copyorrevisestudyprotocol")
	public Response copyOrReviseStudyProtocol(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.copyOrReviseStudyProtocol(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * The method is to link Project Space to Study Protocol document
	 * @param request
	 * @param strInputData parameters in JSON format
	 * @return 
	 */
	@POST
	@Path("/getprojectbookmarkfolders")
	public Response getProjectBookmarkFolders(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.getProjectBookmarkFolders(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}	
	
	/**
	 * The method is to copy Study Leg
	 * @param request
	 * @param strInputData - study leg id, no of copies, selectable
	 * @return
	 */
	@POST
	@Path("/copyleg")
	public Response copyleg(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGStudyProtocol.copyStudyLeg(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
}
