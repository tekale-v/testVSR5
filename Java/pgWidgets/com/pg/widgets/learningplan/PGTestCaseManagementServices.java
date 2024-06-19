/*
 * PGTestCaseManagementServices.java
 * 
 * Added by Dashboard Team
 * For Test Case Management Widget related Webservice
 * 
 */

package com.pg.widgets.learningplan;

import com.dassault_systemes.platform.restServices.RestService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Class PGTestCaseManagementServices has all the web service methods defined for the 'Test Case Management' widget activities.
 * 
 * @since 2018x.5
 * @author 
 *
 */
@Path("/pgtestcasemanagementservices")
public class PGTestCaseManagementServices extends RestService
{
	/**
	 * Creates the Test Case and connects the related objects with the created Test Case
	 * @param request param
	 * @param strTitle : String Title of the new Test Case going to be created
	 * @param strDescription : String Description of the new Test Case going to be created
	 * @param strObjSelects : String object selectables to get current and related object info
	 * @param strRelatedIds : String Info of the object going to be connected with test case
	 * @return : String JSON status and created test case info
	 * @throws Exception
	 */
	@GET
	@Path("/createTestCase")		
	public Response createTestCase(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("Title") String strTitle,
			@QueryParam("Description") String strDescription,
			@QueryParam("ObjSelectables") String strObjSelects,
			@QueryParam("RelatedIds") String strRelatedIds) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			
			PGTestCaseManagementUtil pgTestCaseManagementObj = new PGTestCaseManagementUtil();
			String strOutput = pgTestCaseManagementObj.createAndConnectTestCase(context, strTitle, strDescription, strObjSelects, strRelatedIds);

			res = Response.ok(strOutput).type("application/json").build();
		} catch (Exception e) {
			e.printStackTrace();
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * 
	 * Creates the Test Execution and connects the Test Case to newly created Test Execution
	 * @param request
	 * @param strTitle : String Title of the new Test Execution going to be created
	 * @param strDescription : String strDescription of the new Test Execution going to be created
	 * @param strRelatedIds : String Test Case Ids to be connected
	 * @param strEstimatedStartDate : String Estimated Start Date for 'Test Execution'
	 * @param strEstimatedEndDate : String Estimated End Date for 'Test Execution'
	 * @param strCopyParameter : String which has values true or false, indicates whether to copy params from Test Case to Test Execution or not.
	 * @param strObjSelects : String object selectables
	 * @return : String JSON status and created Test Execution info
	 * @throws Exception
	 */
	@GET
	@Path("/createTestExecution")		
	public Response createTestExecution(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("Title") String strTitle,
			@QueryParam("Description") String strDescription,
			@QueryParam("TestCaseId") String strRelatedIds,
			@QueryParam("EstimatedStartDate") String strEstimatedStartDate,
			@QueryParam("EstimatedEndDate") String strEstimatedEndDate,
			@QueryParam("CopyParameter") String strCopyParameter,
			@QueryParam("ObjSelectables") String strObjSelects) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			
			PGTestCaseManagementUtil pgTestCaseManagementObj = new PGTestCaseManagementUtil();
			String strOutput = pgTestCaseManagementObj.createAndConnectTestExecution(context, strTitle, strDescription, strRelatedIds, strEstimatedStartDate, strEstimatedEndDate, strCopyParameter, strObjSelects);

			res = Response.ok(strOutput).type("application/json").build();
		} catch (Exception e) {
			e.printStackTrace();
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}

	/**
	 * Generic method which returns related objects info of Test case or Test Execution 
	 * @param request
	 * @param strTypeName : String type pattern
	 * @param strRelName : String relationship pattern
	 * @param strOjectId : String OID of Test case or Test Execution 
	 * @param strObjSelectables : String object selectables
	 * @return : String JSON of related object info
	 * @throws Exception
	 */
	@GET
	@Path("/getRelatedObjectsForTCorTE")		
	public Response getRelatedObjectsForTCorTE(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("TypeName") String strTypeName,
			@QueryParam("RelName") String strRelName,
			@QueryParam("id") String strOjectId,
			@QueryParam("ObjSelectables") String strObjSelectables) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
			
			PGTestCaseManagementUtil pgTestCaseManagementObj = new PGTestCaseManagementUtil();
			String strOutput = pgTestCaseManagementObj.getRelatedObjectsForTCorTE(context, strTypeName, strRelName, strOjectId, strObjSelectables, "");

			res = Response.ok(strOutput).type("application/json").build();
		} catch (Exception e) {
			e.printStackTrace();
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Connects the objects to Test Case
	 * @param request
	 * @param strRelIdsTypes : String Ids of objects to be connected with Test Case
	 * @param strObjectId : String OID of Test case
	 * @param strTypePattern : String type pattern
	 * @param strRelPattern : String relationship pattern
	 * @param strObjSelectables : String object selectables
	 * @return : String JSON of connected object info
	 * @throws Exception
	 */
	@GET
	@Path("/connectToTestCase")		
	public Response connectToTestCase(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("RelatedIds") String strRelIdsTypes,
			@QueryParam("id") String strObjectId,
			@QueryParam("types") String strTypePattern,
			@QueryParam("rels") String strRelPattern,
			@QueryParam("ObjSelectables") String strObjSelectables) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
						
			PGTestCaseManagementUtil pgTestCaseManagementObj = new PGTestCaseManagementUtil();
			String strOutput = pgTestCaseManagementObj.connectToTestCase(context, strRelIdsTypes, strObjectId, strTypePattern, strRelPattern, strObjSelectables);
			
			res = Response.ok(strOutput).type("application/json").build();
		} catch (Exception e) {
			e.printStackTrace();
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
    /**
     * Generic method to disconnect objects from Test case or Test Execution 
     * @param request
     * @param strConnectionIds : String Ids of the objects to be disconnected
     * @param strObjectId : String Test case or Test Execution  Id
     * @param strTypePattern : String type pattern
     * @param strRelPattern : String relationship pattern
     * @param strObjSelectables : String object selectables
     * @return : String JSON status
     * @throws Exception
     */
	@GET
	@Path("/disconnectObject")		
	public Response disconnectObject(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("ConnectionIds") String strConnectionIds,
			@QueryParam("id") String strObjectId,
			@QueryParam("types") String strTypePattern,
			@QueryParam("rels") String strRelPattern,
			@QueryParam("ObjSelectables") String strObjSelectables) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
						
			PGTestCaseManagementUtil pgTestCaseManagementObj = new PGTestCaseManagementUtil();
			String strOutput = pgTestCaseManagementObj.disconnectFromObject(context, strConnectionIds, strObjectId, strTypePattern, strRelPattern, strObjSelectables);
			
			res = Response.ok(strOutput).type("application/json").build();
		} catch (Exception e) {
			e.printStackTrace();
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
	/**
	 * Connects the objects to Test Execution
	 * @param request
	 * @param strRelIdsTypes : String Ids of objects to be connected with Test Execution
	 * @param strObjectId : String OID of Test Execution
	 * @param strTypePattern : String type pattern
	 * @param strRelPattern : String relationship pattern
	 * @param strObjSelectables : String object selectables
	 * @return : String JSON of connected object info
	 * @throws Exception
	 */
	@GET
	@Path("/connectToTestExecution")		
	public Response connectToTestExecution(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam("RelatedIds") String strRelIdsTypes,
			@QueryParam("id") String strObjectId,
			@QueryParam("types") String strTypePattern,
			@QueryParam("rels") String strRelPattern,
			@QueryParam("ObjSelectables") String strObjSelectables) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = true;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
						
			PGTestCaseManagementUtil pgTestCaseManagementObj = new PGTestCaseManagementUtil();
			String strOutput = pgTestCaseManagementObj.connectToTestExecution(context, strRelIdsTypes, strObjectId, strTypePattern, strRelPattern, strObjSelectables);
			
			res = Response.ok(strOutput).type("application/json").build();
			
		} catch (Exception e) {
			e.printStackTrace();
			res=Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
		return res;
	}
	
}