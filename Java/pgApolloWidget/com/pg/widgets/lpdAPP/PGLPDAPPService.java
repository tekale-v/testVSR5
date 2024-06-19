package com.pg.widgets.lpdAPP;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;

import com.dassault_systemes.enovia.gls.common.util.PRSPUtil;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.servlet.Framework;
import com.png.apollo.pgApolloConstants;


@Path("/lpdappservice")
public class PGLPDAPPService extends RestService {
	public static final String APP_ID= "id";
	public static final String APPLICATION_JASON ="application/json";
	
	@GET
	@Path("/getEBOMDetails")
	@Produces({MediaType.APPLICATION_JSON})
	public Response pgAPPEBOMDetails (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(APP_ID) String sInput) throws Exception	{
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		try {
			if(context == null && Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));
			}
			JsonObject outPutJson=PGLPDAPPMaterialUsage.getAPPEBOMDetails(context, sInput);
			res = Response.status(200).entity(outPutJson).build();
		}
		catch(Exception ex) {
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON + ex.toString()).build();
		}
		
		return res;	

	}
	
	@GET
	@Path("/getCharacteristicDetails")
	@Produces({MediaType.APPLICATION_JSON})
	
	public Response pgAPPCharacteristicDetails (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(APP_ID) String sInput) throws Exception	{
		
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		try {
			if(context == null && Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));
			}
			
				sInput =PRSPUtil.convertToObjectId(context, sInput);
				JsonObject outPutJson = PGLPDAPPCharacteristic.getAPPCharacteristic(context, sInput);
				res = Response.ok(outPutJson).type(APPLICATION_JASON).build();

		}
		catch(Exception ex) {
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON + ex.toString()).build();
		}
		
		return res;	
	}
	
	@GET
	@Path("/getDesignParameters")
	@Produces({MediaType.APPLICATION_JSON})
	
	public Response getDesignParameters (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(APP_ID) String sInput, @QueryParam(pgApolloConstants.STR_MODE) String sMode,  @QueryParam("showPreviousCollab") boolean showPreviousCollab) throws Exception	{
		
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		try {
			if(context == null && Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));
			}
			
				JsonObject outPutJson = PGLPDAPPDesignParameters.getDesignParameters(context, sInput, sMode, showPreviousCollab);
				res = Response.ok(outPutJson).type(APPLICATION_JASON).build();
		}
		catch(Exception ex) {
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON + ex.toString()).build();
		}
		
		return res;	
		
	}
	
	@GET
	@Path("/getplants")
	@Produces({MediaType.APPLICATION_JSON})
	
	public Response pgAPPPlantsDetails (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(APP_ID) String sInput){
		
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		try {
			if(context == null && Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));
			}
			
				JsonObject outPutJson = PGLPDAPPPlants.getAPPPlants(context, sInput);
				res = Response.ok(outPutJson).type(APPLICATION_JASON).build();
		}
		catch(Exception ex) {
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON + ex.toString()).build();
		}
		
		return res;	
	}
	
	@GET
	@Path("/getproductdesign")
	@Produces({MediaType.APPLICATION_JSON})
	
	public Response pgAPPProductDesign (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(APP_ID) String sInput) throws Exception	{
		
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		try {
			if(context == null && Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));
			}
				JsonObject outPutJson =PGLPDAPPProductDesign.getAPPProductDesign(context, sInput);
				res = Response.ok(outPutJson).type(APPLICATION_JASON).build();
		}
		catch(Exception ex) {
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON + ex.toString()).build();
		}
		
		return res;	
	}

	@GET
	@Path("/getproperties")
	@Produces({MediaType.APPLICATION_JSON})
	
	public Response pgAPPProperties (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(APP_ID) String sInput) throws Exception	{
		
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		try {
			if(context == null && Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));
			}
			
				JsonObject outPutJson =PGLPDAPPMaterialUsage.getPropertyForAPP(context, sInput);
				res = Response.ok(outPutJson).type(APPLICATION_JASON).build();
		}
		catch(Exception ex) {
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON + ex.toString()).build();
		}
		
		return res;	
	}	
	
	
	/**
	 * Web service to fetch basic info for object
	 * @param request
	 * @param sInput
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/getBasicInfo")
	@Produces({MediaType.APPLICATION_JSON})
	
	public Response getBasicInfo (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam(APP_ID) String sInput) throws Exception	{
		
		matrix.db.Context context = getAuthenticatedContext(request, false);
		Response res = null;
		try {
			if(context == null && Framework.isLoggedIn(request)) {
				context = Framework.getContext(request.getSession(false));
			}
			
				JsonObject outPutJson = PGLPDAPPMaterialUsage.getObjectBasicInfo(context, sInput);
				
				res = Response.ok(outPutJson).type(APPLICATION_JASON).build();
		}
		catch(Exception ex) {
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON + ex.toString()).build();
		}
		
		return res;	
	}
}
