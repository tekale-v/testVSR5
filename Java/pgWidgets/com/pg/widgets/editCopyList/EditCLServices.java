package com.pg.widgets.editCopyList;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.widgets.rtautil.RTAUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

@Path("/editCLservices")
public class EditCLServices extends RestService {

	private static final Logger logger = Logger.getLogger(EditCLServices.class.getName());
	static final String EXCEPTION_MESSAGE = "Exception in EditCLServices";
	static final String RETURN_STRING = "returnString";

	/***
	 * This method is called to get Copy List Edit Data Returns: Copy List-MC-LC
	 * Details
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	@POST
	@Path("/getCopyListEditData")
	public Response getCopyListEditData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
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
			strOutput = EditCLMainTable.getCopyListEditData(context, paramString);
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
	 * This method is called to get Copy List Header Data like Countries and
	 * Languages Info
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	@POST
	@Path("/getCopyListHeaderData")
	public Response getCopyListHeaderData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
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
			strOutput = EditCLUtil.getCopyListHeaderData(context, paramString);
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
	 * This method connect MC to CopyList
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/connectMCtoCopyList")
	public Response connectMCtoCopyList(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.connectMCtoCopyList(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@POST
	@Path("/autoUpdateInstanceSequence")
	public Response autoUpdateInstanceSequence(@javax.ws.rs.core.Context HttpServletRequest request,
			String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			strOutput = EditCLUtil.autoUpdateInstanceSequence(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to fetch regions
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getCopyListRegions")
	public Response getCopyListRegions(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.getCopyListRegions(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@POST
	@Path("addlocalelements")
	public Response addLocalElements(@FormParam("addedLanguages") String paramString1,
			@FormParam("selectedMCAs_POAs") String paramString2, @Context HttpServletRequest paramHttpServletRequest,
			@Context HttpServletResponse paramHttpServletResponse) {

		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.addLocalElements(context, paramString1, paramString2);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to get Author and Approver Copy List Edit Data Returns:
	 * Copy List-MC-LC and it's Author and Approver Details Details
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	@POST
	@Path("/getAuthorAndApproverCopyListEditData")
	public Response getAuthorAndApproverCopyListEditData(
			@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
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
			strOutput = EditCLUtil.getAuthorAndApproverCopyListEditData(context, paramString);
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
	 * This method is called to Create Master Copy Returns: POA-MC-LC Details
	 * 
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
			strOutput = EditCLUtil.createMasterCopyElement(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to create Graphic Element Returns: Graphic elements
	 * Info
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	@POST
	@Path("/createGraphicElement")
	public Response createGraphicElement(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString)
			throws FrameworkException {
		Response res = null;
		String strOutput = null;
		boolean isContextPushed = false;
		boolean isSCMandatory = false;
		matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
		try {
			//RTA user do not have access to release the Graphical Element on Creation so need to push context
			ContextUtil.pushContext(context);
			isContextPushed = true;
			strOutput = EditCLUtil.createGraphicElement(context, paramString, paramHttpServletRequest);
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
	 * This method delete Authoring Task
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/deleteAuthoringTask")
	public Response deleteAuthoringTask(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.deleteAuthoringTask(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method reassign Task to Selected Person
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/reassignTaskToSelectedPerson")
	public Response reassignTaskToSelectedPerson(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.reassignTaskToSelectedPerson(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to promote CopyList Returns: message on failure
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/promoteCopyList")
	public Response promoteCopyList(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.promoteCopyList(context, paramString, paramHttpServletRequest);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to get POA Hierarchy Data Returns: POA Product
	 * Hierarchy
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getCLHierachyData")
	public Response getCLHierachyData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.getCLHierachyData(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to u[date GPS Address Attributes
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/updateGPSAddressAttributes")
	public Response updateGPSAddressAttributes(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.updateGPSAddressAttributes(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method is called to u[date GPS Address Attributes
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/getSubTypeList")
	public Response getSubTypeList(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse) {
		Response res = null;
		Object strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.getSubTypeList(context);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/***
	 * This method will set mother and child details on copy list 
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/setCopyListMotherChild")
	public Response setCopyListMotherChild(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.setCopyListMotherChild(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}	
	
	/***
	 * This method will set the Copy Text value on LC 
	 * 
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @param paramString
	 * @return
	 */
	@POST
	@Path("/updateLCContent")
	public Response updateLCContent(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.updateLCCopyText(context, paramString);
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
	@GET
	@Path("/getRegionsAsProdType")
	public Response getRegionsAsProdType(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			String strRegions = EditCLUtil.getRegionsAsProdType(context, paramString);
			res = Response.ok(strRegions).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
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
	@GET
	@Path("/getCopyManagerTableData")
	public Response getCopyManagerTableData(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, @QueryParam("regionId") String paramString) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			String strRegions = EditCLUtil.getCopyManagerTableData(context, paramString);
			res = Response.ok(strRegions).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/updatemotherchilddependency")
	public Response updateMotherChildDependency(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse, String paramString) {
		Response res = null;
		String strOutput = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(paramHttpServletRequest, isSCMandatory);
			strOutput = EditCLUtil.updateMotherChildDependency(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
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
	@Path("/updateLCEContent")
	public Response updateLCEContent(@javax.ws.rs.core.Context HttpServletRequest request, String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = EditCLUtil.updateLCEContent(context, strInputData);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in EditCLServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
		
	@GET
	@Path("/getlocalcopiesforcopylist")
	public Response getLocalCopiesForCopyList(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(EditCLConstants.COPYLIST_ID) String paramString) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = EditCLUtil.getLocalCopiesForCopyList(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in EditCLServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	@POST
	@Path("/gettasksforlocalcopies")
	public Response getTasksForLocalCopies(@javax.ws.rs.core.Context HttpServletRequest request, String paramString) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = RTAUtil.getTasksForLocalCopies(context, paramString);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception in EditCLServices", e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} 
		return res;
	}
	
	@POST
	@Path("/getCLAttributeRangeValues")
	public Response getCLAttributeRangeValues(@javax.ws.rs.core.Context HttpServletRequest request, String data) {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

			JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(data);
			String strAttributeNames = jsonInputInfo.getString("RANGE_ATTRIBUTE_NAMES");
			res = Response.ok(EditCLUtil.getCLAttributeRangeValues(context, strAttributeNames))
					.type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getartworkelementsformce")
	public Response getArtworkElementsForMce(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
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
			strOutput = EditCLUtil.getCopyListArtworkElementsForMce(context, paramString).build().toString();
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
}
