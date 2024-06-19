/*
 * PGClaimServices.java
 * 
 * Added by DSM Claim Manager Team
 * For Claim Management Widget related Web Service
 * 
 */

package com.pg.widgets.claimManager;

import static com.matrixone.apps.domain.DomainConstants.EMPTY_STRING;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.util.StringList;

/**
 * Class PGClaimServices has all the web service methods defined for the 'Claim Management' widget activities.
 * 
 * @since 2018x.5
 * @author 
 *
 */
@Path("/pgClaimManagementservices")
public class PGClaimServices extends RestService
{
	PGClaimManagementUtil pgClaimObj = new PGClaimManagementUtil();
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final Logger logger = Logger.getLogger(PGClaimServices.class.getName());
	static final String EXCEPTION_MESSAGE  = "Exception in PGClaimServices";
	//Added for Route Service - START
	static final String TASK_ID = "taskId";
	static final String ROUTE_ID = "routeId";
	static final String COMMENTS = "comments";
	static final String GET_COMMENT_FROM_TASK_ID = "getCommentsFromTaskId";
	static final String TASK_STATUS = "taskStatus";
	static final String FLAG = "flag";
	static final String SHOW_FDA = "showFDA";
	//Added for Route Service - END
	/**
	 * Creates the Claim and connects the related objects with the created Claim
	 * @param request param
	 * @param strClaimType : String Title of the new Claim going to be created
	 * @param strDescription : String Description of the new Claim going to be created
	 * @param strObjSelects : String object selectable to get current and related object info
	 * @param strRelatedIds : String Info of the object going to be connected with Claim
	 * @return : String JSON status and created Claim info
	 * @throws Exception
	 */
	@POST
	@Path("/create")	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createDocument(@javax.ws.rs.core.Context HttpServletRequest request,
			Map<String,Object> mpRequestMap
			) throws Exception {
		Response res = null;
		try {
			
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return pgClaimObj.createClaim(context, mpRequestMap);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@Path("/find")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFind(@Context HttpServletRequest request,Map<String, String> mpRequestData ) throws Exception {
		boolean isSCMandatory = false;
		matrix.db.Context context = getContext(request, isSCMandatory);
		return pgClaimObj.findObjects(context,mpRequestData);
	}

	@POST
	@Path("/performOperation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response performOperation(@Context HttpServletRequest request,Map<String, Object> mpRequestData ) throws Exception {
		Response res = null;
		try
		{
			String strOjectId = (String) mpRequestData.get("objectId");
			String strParentId = (String) mpRequestData.get("parentId");
			String strEvent = (String) mpRequestData.get("event");
			Map<String, Object> mapAttribute = (Map<String, Object>) mpRequestData.get("attributeMap");
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = null;
			if("promote".equalsIgnoreCase(strEvent)||"demote".equalsIgnoreCase(strEvent))
			{
				strOutput = PGWidgetUtil.promoteDemoteObject(context, strOjectId, strEvent);
			}else if("Approve".equalsIgnoreCase(strEvent)||"Reject".equalsIgnoreCase(strEvent))
			{
				strOutput =pgClaimObj.approveRejectObject(context,strOjectId,strEvent);
			}else if("delete".equalsIgnoreCase(strEvent))
			{
				strOutput = pgClaimObj.deleteObject(context, strOjectId);
			}else if("clone".equalsIgnoreCase(strEvent)){
				strOutput =pgClaimObj.cloneObject(context, strOjectId, strParentId, null);
			}else if("revise".equalsIgnoreCase(strEvent)){
				strOutput = pgClaimObj.reviseObject(context, strOjectId, strParentId, mapAttribute);
			}else if("DemoteOnReject".equalsIgnoreCase(strEvent)){
				strOutput = pgClaimObj.demoteOnTaskRejection(context, strOjectId);
			}else if("replaceRevision".equalsIgnoreCase(strEvent)){
				strOutput = pgClaimObj.replaceWithLatestRev(context, strOjectId);
			}
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	@POST
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateObject(@Context HttpServletRequest request, Map<String, Object> mpUpdateValue) throws Exception {
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput =pgClaimObj.updateObject(context, mpUpdateValue);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@Path("/deleteReleatedData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteConnection(@Context HttpServletRequest request, Map<String,String> mpRequestMap) {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.deleteConnection(context,mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@Path("/getRelatedData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReleatedDataInfo(@Context HttpServletRequest request,Map<String,String> mpRequestMap 
			) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.getRelatedDataInfo(context,mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}



	@SuppressWarnings("unchecked")
	@POST
	@Path("/getObjectInfo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"application/ds-json", TYPE_APPLICATION_FORMAT})
	public Response AttributesReadAccess(@Context HttpServletRequest request, Map<String,Object> mpRequestMap) throws Exception {
		matrix.db.Context context = null;
		Response resp = null;
		try {
			List busID = (List) mpRequestMap.get("busIDs");		
			context = this.getAuthenticatedContext(request, false);
			//To Clear Mql Error Notice if any
			context.clearClientTasks();
			if(BusinessUtil.isNotNullOrEmpty(busID))
			{
				resp = pgClaimObj.getObjectInfo(context,busID);
			}

		} 
		catch(Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			resp=Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
		}
		finally {

			if (context != null) {
				context.shutdown();
			}
		}

		return resp;
	}

	@Path("/count")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCount(@Context HttpServletRequest request,Map<String,Object> mpRequestMap) throws Exception {
		boolean isSCMandatory = false;
		matrix.db.Context context = getContext(request, isSCMandatory);
		String strMode = (String) mpRequestMap.get("mode");
		if("count".equals(strMode))
		{
			return pgClaimObj.getCount(context);
		}
		else
		{
			List slObjectSelect = (List) mpRequestMap.get("objectSelectble");	
			return pgClaimObj.getExpirationData(context,slObjectSelect);
		}
		
	}

	/**
	 * DSM(DS) 2018x.5 - This is a utility method to get all the elements in a StringList as a comma separated String
	 * @param slValues : Input StringList
	 * @return <code>String</code> as described above
	 * @throws Exception
	 */
	public String getStringListAsString(StringList slValues) {
		StringBuilder sbOut = new StringBuilder();
		if(null != slValues){
			int iListSize = slValues.size();
			for (int i = 0; i < iListSize; i++) {
				sbOut.append(slValues.get(i));
				if (i < (iListSize-1)){
					sbOut.append(",");
				}
			}
		}else{
			return EMPTY_STRING;
		}
		return sbOut.toString();
	}



	/**
	 * Method to connect an existing Claim object to Claim Request
	 * @param request
	 * @param strInputData
	 * @return Response as json with connection status
	 * @throws Exception
	 */
	@POST
	@Path("/connect")
	public Response addExistingContent(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) throws Exception
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.addObject(context, strInputData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Method to create Claim Media and connect with Claim
	 * @param request
	 * @param strInputData
	 * @return Response as json with object id of Claim Media
	 * @throws Exception
	 */
	@POST
	@Path("/createMedia")
	public Response createMedia(@javax.ws.rs.core.Context HttpServletRequest request, Map<String,Object> mpRequestMap) throws Exception
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return pgClaimObj.createMedia(context, mpRequestMap);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@Path("/getRelatedMediaData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getReleatedMediaDataInfo(@Context HttpServletRequest request, 
			@QueryParam("id") String strObjectId,
			@QueryParam("types") String strTypePatten, 
			@QueryParam("rels") String strRelPatten,
			@QueryParam("ObjSelectables") String strSelect) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.getRelatedMediaDataInfo(context,strObjectId,strTypePatten,strRelPatten,strSelect);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();			
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}	

	/**
	 * Method to add Secondary ownership to object
	 * @param request
	 * @param strInputData
	 * @return Response as json with ownership add status
	 * @throws Exception
	 */
	@POST
	@Path("/addOwnership")
	public Response addOwnership(@javax.ws.rs.core.Context HttpServletRequest request, Map<String,Object> mpRequestMap) throws Exception
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.addMember(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Method to remove Secondary ownership on object
	 * @param request
	 * @param strRelId
	 * @return Response as json with ownership removal status
	 * @throws Exception
	 */
	@Path("/removeOwnership")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeOwnership(@Context HttpServletRequest request, Map<String,Object> mpRequestMap) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.deleteAccess(context,mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@Path("/getPickListData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPickListData(@Context HttpServletRequest request, Map<String,Object> mpRequestMap) {
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return pgClaimObj.getPickListData(context, mpRequestMap);
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	@Path("/setState")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setState(@Context HttpServletRequest request, Map<String,Object> mpRequestMap) {
		Response res = null;
		try
		{
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return pgClaimObj.setState(context, mpRequestMap);
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRelatedTabInfo(@Context HttpServletRequest request,Map<String,String> mpRequestMap) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.getRelatedTabInfo(context,mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**This Method return context
	 * @param request
	 * @param isSCMandatory
	 * @return
	 */
	private matrix.db.Context getContext(HttpServletRequest request, boolean isSCMandatory) {
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
		//To Clear Mql Error Notice if any
		context.clearClientTasks();
		return context;
	}
	/**
	 * Method to connect an existing Master copy Element to newly created claim if Master copy Element don't have any claim connected.
	 * @param request
	 * @param strInputData
	 * @return Response as json with connection status
	 * @throws Exception
	 */
	@Path("/connectMCE")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response connectMCE(@Context HttpServletRequest request,Map<String,Object> mpRequestMap) throws Exception
	{
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.createClaimAndconnectMCE(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method taken from com.pg.widgets.route.PGRouteServices.java
	 * Web service to fetch completed tasks
	 * 
	 * @param request
	 *            HttpServletRequest object for web service
	 * @param taskId
	 *            object Id of Task
	 * @param routeId
	 *            object Id of Route
	 * @param comments
	 *            Text to add on Tasks as comments
	 * @param getCommentsFromTaskId
	 *            var to decide on certain action
	 * @param taskStatus
	 *            flag to get Task Status
	 * @param flag
	 *            flas to other task details
	 * @param shownFDAWindow
	 *            flag to display Approval Signature dialog box
	 * @return Response response code
	 * @throws Exception
	 *             when operation fails
	 */
	@GET
	@Path("/getCompleteTask")
	public Response getCompleteTask(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("taskId") String taskId, @QueryParam("routeId") String routeId,
			@QueryParam("comments") String comments, @QueryParam("getCommentsFromTaskId") String getCommentsFromTaskId, @QueryParam("taskStatus") String taskStatus,
			@QueryParam("flag") String flag, @QueryParam("shownFDAWindow") String shownFDAWindow) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			Map<String, Object> theMepAttributesMap = new HashMap<>();
			theMepAttributesMap.put(TASK_ID, taskId);
			theMepAttributesMap.put(ROUTE_ID, routeId);
			theMepAttributesMap.put(COMMENTS, comments);
			theMepAttributesMap.put(GET_COMMENT_FROM_TASK_ID, getCommentsFromTaskId);
			theMepAttributesMap.put(TASK_STATUS, taskStatus);
			theMepAttributesMap.put(FLAG, flag);
			theMepAttributesMap.put(SHOW_FDA, shownFDAWindow);
			String output = pgClaimObj.getCompleteTask(context, theMepAttributesMap);
			res = Response.ok(output).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
		return res;
	}
	/**
	 * This method calls the Validate method which checks if there are any duplicate entries
	 * with Claim and Brand combination
	 * @param request
	 * @param mpRequestMap
	 * @return
	 */
	@Path("/validateClaimDuplicate")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateClaimDuplicate(@Context HttpServletRequest request, Map mpRequestMap) {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.validateClaims(context,(Map) mpRequestMap.get("requestMap"), (String) mpRequestMap.get("tabName"));
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * This method calls the Validate method which checks if there are any duplicate entries
	 * with Claim and Brand combination
	 * @param request
	 * @param mpRequestMap
	 * @return
	 */
	@Path("/updateAttributesOnObject")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAttributesOnObject(@Context HttpServletRequest request, Map mpRequestMap) {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.updateAttributesOnObject(context,(Map) mpRequestMap.get("attributeMap"));
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * DCM (DS) 2022x-01 CW - REQ 45296 - The system shall allow to mass create the claims
	 * This method creates multiple Claims and Disclaimers
	 * @param request
	 * @param mpRequestMap
	 * @return
	 */
	@Path("/MassCreateClaim")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response massCreateClaim(@Context HttpServletRequest request, Map mpRequestMap) {
		Response res = null;
		try {
			
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return pgClaimObj.massCreateClaim(context, mpRequestMap);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**REQ 46020 - Validate Indented market on CLR promotion
	 * This method returns CLR info for related claims
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception 
	 */
	@Path("/getConnectedObjectsData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConnectedObjectsData(@Context HttpServletRequest request,Map<String,Object> mpRequestMap 
			) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			res = pgClaimObj.getConnectedObjectsData(context,mpRequestMap);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * DCM (DS) User Story 457 - Claim User/Originator - VIEW files instead of downloading them - START
	 * Method  to generate PDF files of existing checked in files
	 * @param request
	 * @param mpRequestMap
	 * @return
	 */
	@Path("/MassGeneratePDF")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response massGeneratePDF(@Context HttpServletRequest request, Map mpRequestMap) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			return pgClaimObj.massGeneratePDF(context, mpRequestMap);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - START
	/**
	 * This method calls the Validate method which checks if there are any duplicate entries
	 * with Claim and Brand combination
	 * @param request
	 * @param mpRequestMap
	 * @return
	 */
	@Path("/validateCLMSData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateCLMSData(@Context HttpServletRequest request, Map mpRequestMap) {
		Response res;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgClaimObj.validateCLMSDataOnClaims(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - END
}