package com.pg.widgets.multipartcompare;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.pg.widgets.util.PGWidgetConstants;

import matrix.db.JPO;

@Path("/pgmultipartcompareservices")
public class PGMultipartCompareServices extends RestService {
	PGMultipartCompareUtil pgCompUtilObj = new PGMultipartCompareUtil();
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final Logger logger = Logger.getLogger(PGMultipartCompareServices.class.getName());
	static final String EXCEPTION_MESSAGE = "Exception in PGCompareReportServices";

	/**
	 * This Method return context
	 * 
	 * @param request
	 * @param isSCMandatory
	 * @return
	 */
	private matrix.db.Context getContext(HttpServletRequest request, boolean isSCMandatory) {
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
		// To Clear Mql Error Notice if any
		context.clearClientTasks();
		return context;
	}

	/*
	 * This method returns object Info with the selectables passed
	 * 
	 * @param context
	 * 
	 * @param mpRequestMap
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	@Path("/getobjectdetails")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjectDetails2(@Context HttpServletRequest request, @Context HttpServletResponse response,
			String paramString) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			res = pgCompUtilObj.getObjectDetails(context, paramString);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@Path("isvalidtype")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response isValidType(@Context HttpServletRequest request, @Context HttpServletResponse response,
			String paramString) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			String strOutput = pgCompUtilObj.isValidType(context, paramString);			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	@POST
	@Path("/getMultiAttributeCompareReport")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMultiAttributeCompareReport(@javax.ws.rs.core.Context HttpServletRequest request, String paramString)
			throws Exception {
		Response res = null;
		try {
			System.out.println("In getCompareObject" + paramString);
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);

			String strOutput = pgCompUtilObj.getMultiAttributeCompareReport(context, paramString);

			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getbomcomparedata")
	public Response getBOMCompareData(@javax.ws.rs.core.Context HttpServletRequest request, String strInput)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGMultipartCompareUtil.getBOMData(context, strInput);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	@POST
	@Path("/getcomparereport")
	public Response getCompareReport(@javax.ws.rs.core.Context HttpServletRequest request, String strInput)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			String strOutput = PGMultipartCompareUtil.getCompareReport(context, strInput);
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

}
