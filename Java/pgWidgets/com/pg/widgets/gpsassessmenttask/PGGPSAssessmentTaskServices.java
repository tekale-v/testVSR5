/*
 * PGGPSAssessmentTaskServices.java
 * 
 * Added by Dashboard Team
 * For GPS Assessment Task Widget related Webservice
 * 
 */

package com.pg.widgets.gpsassessmenttask;

import com.dassault_systemes.platform.restServices.RestService;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class PGGPSAssessmentTaskServices has all the web service methods defined for the 'GPSA ssessment Task' widget activities.
 * 
 * @since 2018x.5
 * @author 
 *
 */
@Path("/pggpsassessmenttaskservices")
public class PGGPSAssessmentTaskServices extends RestService
{
	
	static final String TYPE_APPLICATION_FORMAT  = "application/json";
	static final String EXCEPTION_MESSAGE  = "Exception in PGGPSAssessmentTaskServices";
	static final Logger logger = Logger.getLogger(PGGPSAssessmentTaskServices.class.getName());
	static final String OBJ_SELECT = "ObjSelectables";
	static final String OBJ_ID = "id";
	
	/**
	 * Method to create GPS Assessment Task
	 * @param request
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/createGPSTask")
	public Response createGPSTask(@javax.ws.rs.core.Context HttpServletRequest request,
			String strData) throws Exception {
		Response res = null;
		
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.createGPSTask(context, strData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to check GPS Task for valid attribute for GPSAssessmentCategory
	 * @param request
	 * @param strOjectId
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/validateGPSAssessmentCategory")		
	public Response validateGPSAssessmentCategory(@javax.ws.rs.core.Context HttpServletRequest request, 
			@QueryParam("id") String strOjectId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.validateGPSAssessmentCategory(context, strOjectId);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get related product parts for GPS Task
	 * @param request
	 * @param strOjectId
	 * @param strObjSelectables
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getRelatedProductsForTask")		
	public Response getRelatedProductsForTask(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			JsonObject jsonInputData = pgGPSTaskUtil.getJsonFromJsonString(strInputData);
			String strObjSelectables = jsonInputData.getString(OBJ_SELECT);
			String strOjectId = jsonInputData.getString(OBJ_ID);
			String strOutput = pgGPSTaskUtil.getRelatedProductsForTask(context, strOjectId, strObjSelectables, "");
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * Method to remove Markets from Task products
	 * @param request
	 * @param strConnectionIds
	 * @param strObjectId
	 * @param strObjSelectables
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/removeMarkets")		
	public Response removeMarkets(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("ConnectionIds") String strConnectionIds,
			@QueryParam("id") String strObjectId,
			@QueryParam("ObjSelectables") String strObjSelectables) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.removeMarketsFromTaskProducts(context, strConnectionIds, strObjectId, strObjSelectables);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get the range for the rel attribute pgExpectedRegulatoryProductClassification
	 * @param request
	 * @param strPickListName
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getAttributeRangeValues")
	public Response getAttributeRangeValues(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			JsonObject jsonInputData = pgGPSTaskUtil.getJsonFromJsonString(strInputData);		
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put(pgGPSTaskUtil.RANGE_ATTRIBUTE_NAMES, jsonInputData.getString(pgGPSTaskUtil.RANGE_ATTRIBUTE_NAMES));
			String strOutput = pgGPSTaskUtil.getAttributeRangeValues(context, mpParamMAP);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to set the value for rel attribute pgExpectedRegulatoryProductClassification 
	 * @param request
	 * @param strPickListName
	 * @param strNewAttrValue
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/setAttributeValue")		
	public Response setAttributeValue(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("ConnectionId") String strRelId,
			@QueryParam("NewValue") String strNewValue,
			@QueryParam("type") String strType,
			@QueryParam("attributeName") String strAttributeName) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.setAttributeValue(context, strRelId, strNewValue, strType,strAttributeName);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get all the counties list
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllCountries")		
	public Response getAllCountries(@javax.ws.rs.core.Context HttpServletRequest request) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.getAllCountriesList(context);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to connect Countries to GPS Task Products
	 * @param request
	 * @param strRelId
	 * @param strSelectedCountries
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/connectCountriesToProductTask")		
	public Response connectCountriesToProductTask(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("ConnectionId") String strRelId,
			@QueryParam("CountriesIds") String strSelectedCountries,
			@QueryParam("id") String strGPSTaskId,
			@QueryParam("ObjSelectables") String strObjSelects) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.connectCountriesToProductTask(context, strRelId, strSelectedCountries, strGPSTaskId, strObjSelects);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to edit GPS Task Properties page
	 * @param request
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/editGPSTaskProperties")
	public Response editGPSTaskProperties(@javax.ws.rs.core.Context HttpServletRequest request,
			String strData) throws Exception {
		Response res = null;
		
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.editGPSTaskProperties(context, strData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get properties page details for GPS Task
	 * @param request
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getGPSTaskProperties")
	public Response getGPSTaskProperties(@javax.ws.rs.core.Context HttpServletRequest request,
			String strData) throws Exception {
		Response res = null;
		
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.getGPSTaskProperties(context, strData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to perform promote or demote operations on GPS Task
	 * @param request
	 * @param strRelId
	 * @param strSelectedCountries
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/promotedemoteGPSTask")		
	public Response promotedemoteGPSTask(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("id") String strGPSTaskId,
			@QueryParam("Operation") String strOperation) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGGPSAssessmentTaskUtil pgGPSTaskUtil = new PGGPSAssessmentTaskUtil();
			String strOutput = pgGPSTaskUtil.promotedemoteGPSTask(context, strGPSTaskId, strOperation);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}

