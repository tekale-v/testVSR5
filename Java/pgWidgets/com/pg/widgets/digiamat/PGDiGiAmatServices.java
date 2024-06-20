package com.pg.widgets.digiamat;

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
@Path("/PGDiGiAmatServices")
public class PGDiGiAmatServices extends RestService {
	
	private static final Logger logger= LoggerFactory.getLogger(PGDiGiAmatServices.class.getName()); 
	static final String TYPE_APPLICATION_FORMAT = "application/json";
	static final String EXCEPTION_MESSAGE  = "Exception in PGStructuredATSServices";
	static final String STRING_STATUS = "status";
	
	@POST
	@Path("/createEditDigiAmat")
	public Response createEditDiGiAmat(@javax.ws.rs.core.Context HttpServletRequest request, Map<String,Object> mpRequestMap) throws Exception {
		Response res = null;
		String strMode = null;
		String strOutput = null;
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);
			strMode = (String)mpRequestMap.get("mode");
			PGDiGiAmatCreateEditUtil pgDigiAmatCreateEdit = new PGDiGiAmatCreateEditUtil();
			if (UIUtil.isNotNullAndNotEmpty(strMode) && strMode.equalsIgnoreCase("Create"))
				return pgDigiAmatCreateEdit.createDigiAmat(context,mpRequestMap);
			else if (UIUtil.isNotNullAndNotEmpty(strMode) && strMode.equalsIgnoreCase("Edit"))
				return pgDigiAmatCreateEdit.editDiGiAmat(context,mpRequestMap);
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

	/**
	 * This method is used to delete connected Performance Characteristics Operations to SATS
	 * 
	 * @param context
	 * @param strOjectId
	 * @return
	 * @throws Exception
	 */
	@POST
	@Path("/deleteSoAmat")
	public Response deleteSoAmat(@javax.ws.rs.core.Context HttpServletRequest request, Map mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Response res = null;
		PGDiGiAmatCreateEditUtil pgSoAmatCreateEdit = new PGDiGiAmatCreateEditUtil();		
		try
		{
			boolean isSCMandatory = true;
			matrix.db.Context context = getContext(request, isSCMandatory);	
			String  strOutput = pgSoAmatCreateEdit.deleteSoAmat(context, mpRequestMap);
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
	
}
