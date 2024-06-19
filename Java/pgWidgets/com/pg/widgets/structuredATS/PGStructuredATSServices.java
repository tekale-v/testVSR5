package com.pg.widgets.structuredats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import com.dassault_systemes.platform.restServices.RestService;
@Path("/structuredATSServices")
public class PGStructuredATSServices extends RestService {
	
	private static final Logger logger= LoggerFactory.getLogger(PGStructuredATSServices.class.getName()); 
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final String EXCEPTION_MESSAGE  = "Exception in PGStructuredATSServices";
	static final String STRING_STATUS = "status";
	
	@POST
	@Path("/createEditStructuredATS")
	public Response createStructuredATS(@javax.ws.rs.core.Context HttpServletRequest request, Map<String,Object> mpRequestMap) throws Exception {
		Response res = null;
		String strMode = null;
		String strOutput = null;
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			strMode = (String)mpRequestMap.get("mode");
			PGStructuredATSCreateEditUtil pgStructuredATSObj = new PGStructuredATSCreateEditUtil();
			if (UIUtil.isNotNullAndNotEmpty(strMode) && strMode.equalsIgnoreCase("Create"))
				return pgStructuredATSObj.createStructuredAuthorizedTemporaryStandard(context,mpRequestMap);
			else if (UIUtil.isNotNullAndNotEmpty(strMode) && strMode.equalsIgnoreCase("Edit"))
				return pgStructuredATSObj.editStructuredAuthorizedTemporaryStandard(context,mpRequestMap);
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	@Path("/copyStructuredATS")
	@POST
	public Response copyStructuredATS(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		Response res = null;
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = null;
			PGStructuredATSUtil pgStructuredATSObj = new PGStructuredATSUtil();
			strOutput = pgStructuredATSObj.copyStructuredATSWithBOM(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	@Path("/addSATSPerformanceCharacteristics")
	@POST
	public Response addPerformanceCharacteristics(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		String strOutput = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		PGStructuredATSPerformanceUtil pgStructuredATSPerformanceUtil = new PGStructuredATSPerformanceUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			strOutput = pgStructuredATSPerformanceUtil.addPerfCharCopyToSATS(context, mpRequestMap).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));			
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();
		}
		return res;
	}
	@Path("/removeSATSPerformanceCharacteristics")
	@POST
	public Response removePerformanceCharacteristics(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		String strOutput = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		PGStructuredATSPerformanceUtil pgStructuredATSPerformanceUtil = new PGStructuredATSPerformanceUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			strOutput = pgStructuredATSPerformanceUtil.removePerfCharCopyToSATS(context, strJsonInput).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));			
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();
		}
		return res;
	}
	@Path("/modifySATSPerformanceCharacteristics")
	@POST
	public Response modifyPerformanceCharacteristics(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		String strOutput = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		PGStructuredATSPerformanceUtil pgStructuredATSPerformanceUtil = new PGStructuredATSPerformanceUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			strOutput = pgStructuredATSPerformanceUtil.modifyPerfCharCopyToSATS(context, mpRequestMap).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));			
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();		
		}
		return res;
	}
	/** Method to fetch Performance Characterstics
	 * 
	 * @param request
	 * @param strOjectId
	 * @return performance characterstics with parent ID
	 * @throws Exception
	 */
	@POST
	@Path("/fetchSATSPerformanceCharacteristics")
	public Response getPerformanceObjects(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSPerformanceUtil pgStructuredATSPerformanceUtil = new PGStructuredATSPerformanceUtil();
			String  strOutput = pgStructuredATSPerformanceUtil.fetchPerformanceCharacterstics(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	@Path("/addATSToBOM")
	@POST
	public Response addExistingATS(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		Response res = null;
		try
		{
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
			PGStructuredATSBOMUtil pgStructuredATSBOMObj = new PGStructuredATSBOMUtil();
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = null;
			strOutput = pgStructuredATSBOMObj.addExistingPartsToBOM(context, mpRequestMap).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	@Path("/addalternates")
	@POST
	public Response addalternateStructuredATS(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		Response res = null;
		try
		{
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
			PGStructuredATSAlternateUtil pgStructuredATSAlternateUtil = new PGStructuredATSAlternateUtil();
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = null;
			
			strOutput = pgStructuredATSAlternateUtil.connectAlternateToSATS(context, mpRequestMap).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
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
	 * Method to get where used objects for selected FOP, RMP based on Plant filter
	 * @param request
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getWhereUsedObjects")
	public Response getWhereUsedObjects(@javax.ws.rs.core.Context HttpServletRequest request, Map<String,String> mpRequestMap) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSWhereUsedUtil pgSATSWhereUsedUtil = new PGStructuredATSWhereUsedUtil();
			String strOutput = pgSATSWhereUsedUtil.getWhereUsedObjects(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to connect the where used objects with SATS object on Save and update the attribute
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/updateWhereusedSelectedItem")
	public Response updateWhereusedSelectedItem(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSWhereUsedUtil pgSATSWhereUsedUtil = new PGStructuredATSWhereUsedUtil();
			String strOutput = pgSATSWhereUsedUtil.updateWhereusedSelectedItem(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method for replace operations on BOM page for SATS
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/replaceOperationsSATS")
	public Response replaceOperationsSATS(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSReplaceOperationsUtil pgSATSWhereUsedBOMOperationsUtil = new PGStructuredATSReplaceOperationsUtil();
			String strOutput = pgSATSWhereUsedBOMOperationsUtil.replaceOperationsSATS(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method for many to many replace operation of FOP on BOM page for SATS
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/replaceOperationsSATSForMultipleSources")
	public Response replaceOperationsSATSForMultipleSources(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSReplaceOperationsUtil pgSATSWhereUsedBOMOperationsUtil = new PGStructuredATSReplaceOperationsUtil();
			String strOutput = pgSATSWhereUsedBOMOperationsUtil.replaceOperationsSATSForMultipleSources(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to add the Balancing Material to SATS
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/addBalancingMaterialToSATS")
	public Response addBalancingMaterialToSATS(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSReplaceOperationsUtil pgSATSWhereUsedBOMOperationsUtil = new PGStructuredATSReplaceOperationsUtil();
			String strOutput = pgSATSWhereUsedBOMOperationsUtil.addBalancingMaterialToSATS(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get the data to be shown on drop zone for where used tab.
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/fetchSATSWhereUsedDropzoneData")
	public Response fetchSATSWhereUsedDropzoneData(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSWhereUsedUtil pgSATSWhereUsedUtil = new PGStructuredATSWhereUsedUtil();
			String strOutput = pgSATSWhereUsedUtil.fetchSATSWhereUsedDropzoneData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get the BOM data along with related SATS data
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/fetchSATSBOMData")
	public Response fetchSATSBOMData(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSBOMDataUtil pgSATSBOMDataUtil = new PGStructuredATSBOMDataUtil();
			String strOutput = pgSATSBOMDataUtil.fetchSATSBOMData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	/**
	 * This method is used to modify connected BOM & ALTERNATE Operations to SATS
	 * 
	 * @param context
	 * @param 
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/modifyOperationsSATS")
	public Response modifyOperationsSATS(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSModifyOpsUtil pgStructuredATSModifyOpsUtil = new PGStructuredATSModifyOpsUtil(); 
			String  strOutput = pgStructuredATSModifyOpsUtil.modifyBOMSATS(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method to revise the SATS object and replicate the connections from previous revision
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/reviseStructuredATS")
	public Response reviseStructuredATS(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSReviseUtil pgStructuredATSReviseUtilObj = new PGStructuredATSReviseUtil();
			String strOutput = pgStructuredATSReviseUtilObj.reviseStructuredATSObject(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	@Path("/getATSInformation")
	@POST
	public Response getATSInformation(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		Response res = null;
		try
		{
			PGStructuredATSUtil pgStructuredATSObj = new PGStructuredATSUtil();
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String  strOutput = pgStructuredATSObj.getATSInformation(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	
	@Path("/updateATSInformation")
	@POST
	public Response updateATSInformation(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		Response res = null;
		try
		{
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
			PGStructuredATSUtil pgStructuredATSObj = new PGStructuredATSUtil();
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = null;
			strOutput = pgStructuredATSObj.updateATSInformation(context, mpRequestMap).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	@Path("/getATSPlants")
	@POST
	public Response getATSPlants(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		PGStructuredATSUtil pgStructuredATSUtil = new PGStructuredATSUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);	
			String  strOutput = pgStructuredATSUtil.getATSRelatedobjects(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to disconnected Plants connected to ATS
	 * 
	 * @param request
	 * @param strOjectId
	 * @param strObjSelectables
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/removeATSPlants")
	public Response removeRelatedATSObjects(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		PGStructuredATSUtil pgStructuredATSUtil = new PGStructuredATSUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);	
			String  strOutput = pgStructuredATSUtil.removePlantsFromSATS(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method to refresh Plants connected to ATS
	 * 
	 * @param request
	 * @param strOjectId
	 * @param
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/refreshSATSPlants")
	public Response refreshConnectedSATSPlants(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		PGStructuredATSRefreshPlants pgStructuredATSRefreshPlants = new PGStructuredATSRefreshPlants(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);	
			String  strOutput = pgStructuredATSRefreshPlants.connectRefreshPlants(context, mpRequestMap);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	/**
	 * This method is used to delete connected BOM  Operations to SATS
	 * 
	 * @param context
	 * @param strSATSId
	 * @param strRelId
	 * @param strATSCtxId
	 * @param strRelType
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deleteBOMSATS")
	public Response deleteBOMSATS(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGStructuredATSDeleteUtil pgStructuredATSDeleteUtil = new PGStructuredATSDeleteUtil(); 
			String  strOutput = pgStructuredATSDeleteUtil.deleteSATSDataForBOM(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	/**
	 * This method is used to delete connected Performance Characteristics Operations to SATS
	 * 
	 * @param context
	 * @param strOjectId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deletePCSATS")
	public Response deletePCSATS(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		PGStructuredATSDeleteUtil pgStructuredATSDeleteUtil = new PGStructuredATSDeleteUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);	
			String  strOutput = pgStructuredATSDeleteUtil.deletePCSATS(context, mpRequestMap);
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	@Path("/getHistory")
	@POST
	public Response getStucturedATSSHistory(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		String strOutput = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		PGStructuredATSHistoryUtil  pgStructuredATSHistory = new PGStructuredATSHistoryUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			strOutput = pgStructuredATSHistory.getSATSHistory(context, strJsonInput).toString();
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));			
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();
		}
		return res;
	}
    /**
	 * This method is used to delete connected WhereUsed Operations to SATS
	 * 
	 * @param context
	 * @param strAPPorFOPId,strSATSId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/whereUsedSATSDelete")
	public Response whereUsedSATSDelete(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		PGStructuredATSWhereUsedDeleteUtil pgStructuredATSWhereUsedDeleteUtil = new PGStructuredATSWhereUsedDeleteUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);	
			String  strOutput = pgStructuredATSWhereUsedDeleteUtil.deleteWhereUsedBOMOps(context, strJsonInput);
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	
	//Services for new SATS widget : Start :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

	/**
	 * Method to update ATS data from excel sheet
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/importBOMData")
	public Response importBOMData(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGNWStructuredATSImportFromExcelUtil objSATSImportFromExcelUtil = new PGNWStructuredATSImportFromExcelUtil(); 
			String  strOutput = objSATSImportFromExcelUtil.importBOMData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to update ATS data from excel sheet
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/importPCData")
	public Response importPCData(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGNWStructuredATSImportFromExcelPCUtil objSATSImportFromExcelPCUtil = new PGNWStructuredATSImportFromExcelPCUtil(); 
			String  strOutput = objSATSImportFromExcelPCUtil.importPerfCharData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get the Header and Row BOM data for current ATS
	 * @param request
	 * @param strJsonInput
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/fetchBOMData")
	public Response fetchBOMData(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGNWStructuredATSBOMDataUtil objStructuredATSBOMDataUtil = new PGNWStructuredATSBOMDataUtil(); 
			String  strOutput = objStructuredATSBOMDataUtil.fetchSATSBOMData(context, strJsonInput);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception excep) {
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res = Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}

	@POST
	@Path("/saveBOMData")
	public Response nwsaveBOMDataStructureATS(@javax.ws.rs.core.Context HttpServletRequest request, String strJsonInput) throws Exception {
		Response res = null;
		String strMode = null;
		String strOutput = null;
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			PGNWStructuredATSSaveBOMUtil pgStructuredATSobj = new PGNWStructuredATSSaveBOMUtil();
			System.out.println("strJsonInput----"+strJsonInput);
			//String  strOutput = pgStructuredATSobj.deleteWhereUsedBOMOps(context, strJsonInput);
			String  strReturn = pgStructuredATSobj.saveStructureATSBOM(context, strJsonInput);
			jsonReturnObj.add(STRING_STATUS, strReturn);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}
	
	@Path("/savePCData")
	@POST
	public Response savePCData(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		String strOutput = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		PGStructuredATSPerformanceUtil pgStructuredATSPerformanceUtil = new PGStructuredATSPerformanceUtil(); 
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			strOutput = pgStructuredATSPerformanceUtil.modifyPerfCharCopyToSATS(context, mpRequestMap).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));			
			return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(output.build().toString()).build();		
		}
		return res;
	}
	
	@POST
	@Path("/removeDeletePCSATS")
	public Response removeDeletePCSATS(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		
		PGStructuredATSPerformanceUtil pgStructuredATSPerformanceUtil = new PGStructuredATSPerformanceUtil(); 
		try
		{
			System.out.println("REM/DEL-------Service-----CALLING");
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);	
			String strOutput = pgStructuredATSPerformanceUtil.removeDeletePerfCharCopyToSATS(context, mpRequestMap).toString();
			jsonReturnObj.add(STRING_STATUS, strOutput);
			res = Response.ok(jsonReturnObj.build().toString()).type(TYPE_APPLICATION_FORMAT).build();
		}
		catch(Exception excep)
		{
			logger.error(EXCEPTION_MESSAGE, excep.getMessage());
			res=Response.status(Response.Status.BAD_REQUEST).entity(excep.getMessage()).build();
		}
		return res;
	}

}
