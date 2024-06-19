package com.pg.widgets.rtautil;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.dassault_systemes.cpd.rest.exception.RestException;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.JPO;
import matrix.util.StringList;

@Path("/rtautilservices")
public class RTAUtilServices extends RestService {

	private static final Logger logger = Logger.getLogger(RTAUtilServices.class.getName());
	static final String EXCEPTION_MESSAGE = "Exception in RTAUtilServices";
	static final String RETURN_STRING = "returnString";
	/***
	 * This method is called to get full name of logged-in user Returns: Logged-in user full name
	 * @param request
	 * @return
	 */
	@GET
	@Path("/getPersonFullName")
	public Response getPersonFullName(@javax.ws.rs.core.Context HttpServletRequest request) {
		Response res = null;
		try {
			JsonObjectBuilder output;
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			output = (JsonObjectBuilder) RTAUtil.getPersonFullName(context);

			res = Response.status(200).entity(output.build().toString()).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return res;
	}

	/****
	 * This method is called to get POA Header Data Returns: POA Details
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getPOAHeaderData")
	public Response getPOAHeaderData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getPOAHeaderData(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to get POA Edit Data Returns: POA-MC-LC Details
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	@POST
	@Path("/getPOAEditData")
	public Response getPOAEditData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString)
			throws FrameworkException {
		Response res = null;
		String strOutput = null;
		boolean isSCMandatory = false;
		boolean isContextPushed = false;
		matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
		try {
			// Due to performance issue trying with push context
			ContextUtil.pushContext(context);
			isContextPushed = true;			
			strOutput = RTAUtil.getPOAEditData(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/getPOAMCData")
	public Response getPOAMCData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString)
			throws FrameworkException {
		Response res = null;
		HashMap<String, Object> strOutput = new HashMap<>();
		boolean isSCMandatory = false;
		boolean isContextPushed = false;
		matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
		try {
			// Due to performance issue trying with push context
			ContextUtil.pushContext(context);
			isContextPushed = true;
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			paramString = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(paramString, PGWidgetConstants.KEY_PIPE_SEPARATOR);
			strOutput = RTAUtil.getFinalMCLCDetails(context,poaIdList, DomainConstants.EMPTY_STRING);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return res;
	}
	

	/***
	 * This method is called to get POA Hierarchy Data Returns: POA Product Hierarchy
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getPOAHierachyData")
	public Response getPOAHierachyData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getPOAHierachyData(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to Create Master Copy Returns: POA-MC-LC Details
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/createMasterCopyElement")
	public Response createMasterCopyElement(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.createMasterCopyElement(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to fetch regions
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getRegions")
	public Response getRegions(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getRegions(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to connect Master Copy Element to POA 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/addMasterCopy")
	public Response addMasterCopy(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.addMasterCopy(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to get Copy List Master Copies Returns: MC Details
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getCopyLisMasterCopies")
	public Response getCopyLisMasterCopies(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getCopyLisMasterCopies(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to connect CL Master Copies to POA Returns: Success
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/connecttMasterCopiesFromCL")
	public Response connecttMasterCopiesFromCL(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.connecttMasterCopiesFromCL(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to create Graphic Element Returns: Graphic elements Info
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/createGraphicElement")
	public Response createGraphicElement(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.createGraphicElement(context, paramString, paramHttpServletRequest);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to mass promote POAs Returns: message on failure
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/promotePOAs")
	public Response promotePOAs(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.promotePOAs(context, paramString, paramHttpServletRequest);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to mass demote POAs
	 * @param request
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/demotePOAs")
	public Response demotePOA(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			String strOutput = PGWidgetUtil.promoteDemoteObject(context, strObjectIds,
					PGWidgetConstants.OPERATION_DEMOTE);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}


	/***
	 * This method is called to validate a case when country and Language is modified
	 * @param request
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/validateAlertForIntegration")
	public Response validateAlertForIntegration(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.validateAlertForIntegration(context, paramString, request);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
			if (BusinessUtil.isNullOrEmpty(strOutput)) {
				JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
				jsonObjSuccess.add(RETURN_STRING, PGWidgetConstants.KEY_SUCCESS);
				res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	

	/***
	 * This method is called to Validate Claim MC
	 * @param request
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/validateForClaimsMC")
	public Response validateForClaimsMC(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.validateForClaimsMC(context, paramString, request);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
			if (BusinessUtil.isNullOrEmpty(strOutput)) {
				JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
				jsonObjSuccess.add(RETURN_STRING, PGWidgetConstants.KEY_SUCCESS);
				res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;

	}

	/***
	 * This method is called to get Duplicate Instance Seq Info Returns: Duplicate Instance MC
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getDuplicateInstanceSeq")
	public Response getDuplicateInstanceSeq(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getDuplicateInstanceSeq(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to get Validity Date for Master copies Returns: Master Copies whose validity date is passed 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getValidityDateForMasterCopies")
	public Response getValidityDateForMasterCopies(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getValidityDateForMasterCopies(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to get Master Copy Types,Languages and GCE Types Returns: Types,Languages
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @return
	 */
	@POST
	@Path("/getPOAInitialData")
	public Response getPOAInitialData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getPOAInitialData(context);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to export POAs Returns: message on failure
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@GET
	@Produces("application/octet-stream")
	@Path("/exportPOAs")
	public Response exportPOAs(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse,
			@QueryParam("POAIds") String paramString) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			res = RTAUtil.exportPOAs(context, paramString, paramHttpServletResponse);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called on Harmonize POAs Returns:Message
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/harmonizePOAs")
	public Response harmonizePOAs(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.harmonizePOAs(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * 
	 * @param request
	 * @param response
	 * @param strInputData
	 * @return
	 */
	@POST
	@Path("/getMasterData")
	public Response getMasterData(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse response, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put("strInputData", strInputData);
			mpParamMAP.put("request", request);
			mpParamMAP.put("response", response);
			String strOutput = JPO.invoke(context, "pgRTAWidgetJPO", null, "getMasterData", JPO.packArgs(mpParamMAP),
					String.class);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/***
	 * 
	 * @param request
	 * @param response
	 * @param strInputData
	 * @return
	 */

	@POST
	@Path("/pgRTARetrieveVariableCE")
	public Response pgRTARetrieveVariableCE(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse response, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String responseString=RTAUtil.pgRTARetrieveVariableCE(context, strInputData);
			JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
			jsonObjSuccess.add(RETURN_STRING, responseString);
			res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * 
	 * @param request
	 * @param response
	 * @param strInputData
	 * @return
	 */
	@POST
	@Path("/pgRTARetrieveDataForClaim")
	public Response pgRTARetrieveDataForClaim(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse response, String strInputData) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.pgRTARetrieveDataForClaim(context, strInputData);
			JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
			jsonObjSuccess.add(PGWidgetConstants.KEY_MESSAGE, strOutput);
			res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to add Local Copy Elements to Master Copy Elements
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/addLceToMce")
	public Response addLceToMce(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.addLceToMce(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/***
	 * This method is called to get Markup Data
	 * @param paramHttpServletRequest
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getMarkupData")
	public Response getMarkupData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAApproveCopyMatrix.getMarkupData(context, paramString, paramHttpServletRequest);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		}catch(Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * Web service to get Rework Data
	 * @param paramHttpServletRequest
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getReworkDetails")
	public Response getReworkDetails(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAApproveCopyMatrix.getReworkDetails(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		}catch(Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * Web service to get Rework Data
	 * @param paramHttpServletRequest
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/updateCauseComment")
	public Response updateCauseComment(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAApproveCopyMatrix.submitPOA(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		}catch(Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to fetch countries
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString1
	 * @param paramString2
	 * @param paramString3
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("getcountriesapplicable")
	public String getCountriesApplicable(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse,
			@FormParam("RegionID") String paramString1, @FormParam("ApplicableCountryIds") String paramString2,
			@FormParam("SelectedCountryIds") String paramString3) throws Exception {
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getCountriesApplicable(context, paramString1, paramString2, paramString3);
			return strOutput;
		} catch (Exception exception) {
			throw new RestException(exception);
		}
	}

	/***
	 * This method is called to fetch languages
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString1
	 * @param paramString2
	 * @return
	 * @throws FrameworkException
	 * @throws RestException
	 */
	@POST
	@Path("getLanguagesToCreatePOA")
	public String getLanguagesToCreatePOA(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse,
			@FormParam("countryID") String paramString1, @FormParam("SelectedLanguageIds") String paramString2)
			throws FrameworkException, RestException {
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getLanguagesToCreatePOA(context, paramString1, paramString2);
			return strOutput;
		} catch (Exception exception) {
			throw new RestException(exception.getMessage());
		}
	}
	
	
	@POST
	@Path("/updateRemoveIntegrationPOA")
	public Response updateRemoveIntegrationPOA(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.updateRemoveIntegrationPOA(context, paramString);
			JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
			jsonObjSuccess.add(RETURN_STRING, strOutput);
			res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		}catch(Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
		
	}
	
	@POST
	@Path("/updateIntegrationPOA")
	public Response updateIntegrationPOA(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.updateIntegrationPOA(context, paramString);
			JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
			jsonObjSuccess.add(RETURN_STRING, strOutput);
			res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		}catch(Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
		
	}

	/**
	 * Method to upload file
	 * 
	 * @param request : HttpServletRequest request param
	 * @return : Response string with result message
	 * @throws Exception
	 */
	@POST
	@Path("/getImageURl")
	public Response getImageURl(@javax.ws.rs.core.Context HttpServletRequest request, String data) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String result = RTAUtil.getImageURl(context, data);
			res = Response.ok(result).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

   	
	/***
	 * This method is called to update "Robotic2 Comment" attribute when robotic MC is removed from POA
	 * @param request
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/updateRemoveRoboticPOA")
	public Response updateRemoveRoboticPOA(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.updateRemoveRoboticPOA(context, paramString);
			JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
			jsonObjSuccess.add(RETURN_STRING, strOutput);
			res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/***
	 * This method is called to approve a Master copy (promote from Preliminary to Release)
	 * @param request
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/authorapprovemastercopy")
	public Response releaseMasterCopy(@javax.ws.rs.core.Context HttpServletRequest request, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.authorApproveMasterCopy(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	@POST
	@Path("/checkAuthorApproverAccessForRevise")
	public Response checkAuthorApproverAccessForRevise(@javax.ws.rs.core.Context HttpServletRequest request, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.checkAuthorApproverAccessForRevise(context, request, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	@POST
	@Path("/reviseCopyElement")
	public Response reviseCopyElement(@javax.ws.rs.core.Context HttpServletRequest request, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.reviseCopyElement (context, request, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getWhereUsedAndLcFromMce")
	public Response getWhereUsedAndLcFromMce(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.getWhereUsedAndLcFromMce(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@GET
	@Path("/confirmassignmentcopyelement")
	public Response confirmAssignmentCopyElement(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, @QueryParam("mceId") String strMceId, @QueryParam("lceIds") String strLceIds) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.confirmAssignmentCopyElement(context, strMceId, strLceIds);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/assignauthorapprovercopyelement")
	public Response assignAuthorApproverCopyElement(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.assignAuthorApproverCopyElement(context, request, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	@POST
	@Path("/getbasecopiesformces")
	public Response getBaseCopiesForMCE(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.getBaseCopiesForMCEs(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	@POST
	@Path("/completeassignauthorapprovetask")
	public Response completeAssignAuthorApproveTask(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.completeAssignAuthorApproveTask(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@POST
	@Path("/updateinstancesequence")
	public Response updateInstanceSequence(@javax.ws.rs.core.Context HttpServletRequest request, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.updateInstanceSequence(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/submitpoaexessrequest")
	public Response submitPOAExessRequest(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAExessUtil.submitExessRequest(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/retrieveDataForGPS")
	public Response retrieveDataForGPS(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAExessUtil.retrieveDataForGPS(context, paramString);			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/***
	 * calls to validate the connect of customization POA: alert shown when we tried to connect POA more than once to the customization POA
	 * @param request
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/connectCusomizationPOA")
	public Response connectCusomizationPOA(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTACustomizationPOA.connectCusomizationPOA(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/connectMCtoPOA")
	public Response connectMCtoPOA(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.connectMCtoPOA(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	@POST
	@Path("/checkmceoriginator")
	public Response checkMCEOriginator(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.checkMCEOriginator(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getauthorapproverinfo")
	public Response getAuthorApproverInfo(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString)
			throws FrameworkException {
		Response res = null;
		String strOutput = null;
		boolean isSCMandatory = false;
		boolean isContextPushed = false;
		matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
		try {
			// Due to performance issue trying with push context
			ContextUtil.pushContext(context);
			isContextPushed = true;			
			strOutput = RTAUtil.getAuthorApproverInfo(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return res;
	}
	
	@POST
	@Path("/retrieveAddress")
	public Response retrieveAddress(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAExessUtil.retrieveAddress(context, paramString);			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/retrieveDataForSizeBase")
	public Response retrieveDataForSizeBase(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAExessUtil.retrieveDataForSizeBase(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/deleteLocalCopyTasks")
	public Response deleteLocalCopyTasks(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.deleteLocalCopyTasks(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/reassignTaskToSelectedPerson")
	public Response reassignTaskToSelectedPerson(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.reassignTaskToSelectedPerson(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/****
	 * This method is called to update route task due date
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/updateRouteNodeOrTaskDueDate")
	public Response updateCopyTaskDuration(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.updateRouteNodeOrTaskDueDate(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@POST
	@Path("/getRoboticPOA")
	public Response getRoboticPOA(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = RTAUtil.getRoboticPOA(context, paramString);
			JsonObjectBuilder jsonObjSuccess = Json.createObjectBuilder();
			jsonObjSuccess.add(RETURN_STRING, strOutput);
			res = Response.ok(jsonObjSuccess.build().toString()).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}		
	
	@POST
	@Path("/getbasecopyidsformces")
	public Response getBaseCopyIdsForMCE(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.getBaseCopyIdsForMCEs(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getlocalcopiesforpoas")
	public Response getLocalCopiesForPOAs(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.getLocalCopiesForPOAs(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@GET
	@Path("/getJobStatus")
	public Response getJobStatus(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("jobId") String strJobId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = RTAUtil.getJobStatus(context,strJobId);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getArtworkElementInfo")
	public Response getArtworkElementInfo(@javax.ws.rs.core.Context HttpServletRequest request,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = RTAUtil.getArtworkElementInfo(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}
