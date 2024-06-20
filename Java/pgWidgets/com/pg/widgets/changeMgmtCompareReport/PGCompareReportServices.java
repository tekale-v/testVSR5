/*
 * PGCompareReportServices.java
 * 
 * 
 */

package com.pg.widgets.changeMgmtCompareReport;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;


@Path("/pgChangeMgmtCompareReportservices")
public class PGCompareReportServices extends RestService
{
	PGCompareReportUtil pgCompReportObj = new PGCompareReportUtil();
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final Logger logger = Logger.getLogger(PGCompareReportServices.class.getName());
	static final String EXCEPTION_MESSAGE  = "Exception in PGCompareReportServices";

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
	/*
	 * This method returns object Info with the selectables passed
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception 
	 */
	@Path("/getObjectDetails")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConnectedObjectsData(@Context HttpServletRequest request,Map<String,Object> mpRequestMap 
			) throws Exception {
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			res = pgCompReportObj.getObjectDetails(context,mpRequestMap);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/*
	 * This method returns Table data for each of the Compare Type values
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception 
	 */
	@Path("/getTableData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTableData(@Context HttpServletRequest request,Map<String,Object> mpRequestMap) throws Exception 
	{
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			res = pgCompReportObj.getTableSchemaData(context, mpRequestMap);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/*
	 * This method returns Attribute comparison report for 2 objects
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception 
	 */
	@Path("/getAttributeCompareReport")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAttributeCompareReportData(@Context HttpServletRequest request,Map<String,Object> mpRequestMap) throws Exception 
	{
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			res = pgCompReportObj.getAttributeCompareReport(context,mpRequestMap);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	/*
	 * This method returns comparison report for an object based on the input Compare Type
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception 
	 */
	@Path("/getCompareReport")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCompareReportData(@Context HttpServletRequest request,Map<String,Object> mpRequestMap) throws Exception 
	{
		Response res;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			
			res = pgCompReportObj.getCompareReportData(context,mpRequestMap);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}