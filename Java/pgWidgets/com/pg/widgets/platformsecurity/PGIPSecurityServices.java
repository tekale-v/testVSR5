/*
 * PGIPSecurityServices.java
 * 
 * Added by Platform and Dashboard Team
 * For Platform Security Widget related Web services
 * 
 */

package com.pg.widgets.platformsecurity;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.widgets.util.PGWidgetConstants;
@Path("/platformsecurityservices")
public class PGIPSecurityServices extends RestService {
	static final Logger logger = Logger.getLogger(PGIPSecurityServices.class.getName());
	static final String EXCEPTION_MESSAGE  = "Exception in PGIPSecurityServices";
	
	/**
	 * Method to get all IP and Security control classes related to an object
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String input data
	 * @return : String json with connected class info
	 * @throws FrameworkException 
	 */
	@GET
	@Path("/getAllClassesForObject")
	public Response getAllClassesForObject(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData)
			throws FrameworkException {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.getAllClassesForObject(context, strInputData);

			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
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
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.getIPClassesForUser(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to get all Security Control classes for current user
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String object selects
	 * @return : String json with list of Security Classes
	 * @throws Exception 
	 */
	@GET
	@Path("/getSecurityClassesForUser")
	public Response getSecurityClassesForUser(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.getSecurityClassesForUser(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to change the classification of the object from Restricted to 'Highly Restricted' and vice versa
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String object selects
	 * @return : String json with list of Security Classes
	 * @throws Exception 
	 */
	@GET
	@Path("/changeClassification")
	public Response changeClassification(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.changeClassification(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method adds the selected IP or Security Control Classes to the object.
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String object selects
	 * @return : String json with list of Security Classes
	 * @throws Exception 
	 */
	@GET
	@Path("/addClassToObject")
	public Response addClassToObject(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData){
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.addClassToObject(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to reclassify object for a new Class
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String object selects
	 * @return : String json with list of Security Classes
	 * @throws Exception 
	 */
	@GET
	@Path("/reclassifyObject")
	public Response reclassifyObject(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData) {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.reclassifyObject(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Method to remove the selected class. This method is based on EXCSecurityRuleRemove.jsp which will be invoked on remove operation as part of OOTB Remove command.
	 * @param request : HttpServletRequest request param
	 * @param strInputData : String object selects
	 * @return : String json with list of Security Classes
	 * @throws Exception 
	 */
	@GET
	@Path("/removeClass")
	public Response removeClass(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.removeClass(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Set User Preferences for IP Classes
	 */
	
	
	@GET
	@Path("/setIPPreferencesBothClasses")
	public Response setIPPreferencesBothClasses(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.setIPPreferencesBothClasses(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Set User Preferences for SC Classes
	 */
	@GET
	@Path("/setPreferencesSCClasses")
	public Response setIPPreferencesSCClasses(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("data") String strInputData)
			throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.setIPPreferencesSCClasses(context, strInputData);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Set User Preferences for IP Classes
	 */
	@GET
	@Path("/getIPPreferences")
	public Response getIPPreferences(@javax.ws.rs.core.Context HttpServletRequest request){
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			PGIPSecurityUtil pgPlatformSecurityUtil = new PGIPSecurityUtil();
			String strOutput = pgPlatformSecurityUtil.getUserPreferenceIPClass(context);
			
			res = Response.ok(strOutput).type(PGWidgetConstants.TYPE_APPLICATION_FORMAT).build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
}
