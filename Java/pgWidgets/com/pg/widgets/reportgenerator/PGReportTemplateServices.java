/*
 * PGReportTemplateServices.java
 * 
 * Added by Dashboard Team
 * For Report Template Widget related Web services
 * 
 */

package com.pg.widgets.reportgenerator;

import com.dassault_systemes.platform.restServices.RestService;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Class PGReportTemplateServices is used to invoke the web service methods
 * required for report template widget
 * 
 * @since 2018x.5
 * @author
 *
 */
@Path("/pgreporttemplateservices")
public class PGReportTemplateServices extends RestService {
	
	static final String TYPE_APPLICATION_FORMAT  = "application/json";
	static final String EXCEPTION_MESSAGE  = "Exception in PGReportTemplateServices";
	static final Logger logger = Logger.getLogger(PGReportTemplateServices.class.getName());
		
	/**
	 * Method to create report template object
	 * @param request : HttpServletRequest request param
	 * @param strInputInfo : String input details
	 * @param strObjSelects : String pipe separated object selects
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/createReportTemplate")
	public Response createReportTemplate(@javax.ws.rs.core.Context HttpServletRequest request, String sData) throws Exception {
		Response res = null;
		
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.createReportTemplate(context, sData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get list of all report template objects to which user has access
	 * 
	 * @param request       : HttpServletRequest request param
	 * @param strObjSelects : String pipe separated object selects
	 * @param strSelectObsoleteData : boolean true or false
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getReportTemplates")
	public Response getReportTemplates(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(PGReportTemplateUtil.TABLE_COLUMNS) String strTableColumns,
			@QueryParam(PGReportTemplateUtil.SHOW_OBSOLETE) boolean showObsolete) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.getReportTemplates(context, strTableColumns, showObsolete);

			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getAllTypesAndRels")
	public Response getAllTypesAndRels(@javax.ws.rs.core.Context HttpServletRequest request) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			
			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.getAllTypesAndRels(context);
			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * 
	 * @param request
	 * @param strTypes
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getTypeAttributeMapping")
	public Response getTypeAttributeMapping(@javax.ws.rs.core.Context HttpServletRequest request,
			String strData) throws Exception {
		Response res = null;
		
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.getTypeAttributeMapping(context, strData);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getTypeAttributeMappingBasic")
	public Response getTypeAttributeMappingBasic(@javax.ws.rs.core.Context HttpServletRequest request
			) throws Exception {
		Response res = null;
		
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.getTypeAttributeMappingBasic(context);
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	
	/**
	 * Method to set the state of the Report Template to Obsolete on delete operation
	 * @param request
	 * @param strReportTemplateId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deleteReportTemplate")
	public Response deleteReportTemplate(@javax.ws.rs.core.Context HttpServletRequest request,
			String strReportTemplateId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			
			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.deleteReportTemplate(context, strReportTemplateId);
			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * 
	 * @param request
	 * @param strReportTemplateId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getReportTemplateDetails")
	public Response getReportTemplateDetails(@javax.ws.rs.core.Context HttpServletRequest request,
			String strReportTemplateId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			
			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.getReportTemplateDetails(context, strReportTemplateId, false);
			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * 
	 * @param request
	 * @param strReportTemplateId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/getPropertiesPageDetails")
	public Response getPropertiesPageDetails(@javax.ws.rs.core.Context HttpServletRequest request,
			String strReportTemplateId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			
			PGReportTemplateUtil pgReportTempUtil = new PGReportTemplateUtil();
			String strOutput = pgReportTempUtil.getReportTemplateDetails(context, strReportTemplateId, true);
			
			res = Response.ok(strOutput).type(TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}