package com.pg.widgets.myfavorites;

import com.dassault_systemes.platform.restServices.RestService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;


@Path("/pgfavoritesservices")
public class PGFavoritesServices extends RestService
{

	@POST
	@Path("/addToCollection")	
	public Response addToCollection(@javax.ws.rs.core.Context HttpServletRequest request, 
			String data) throws Exception			
	{
		Response res = null;
		try
		{
			matrix.db.Context context = getAuthenticatedContext(request, false);
			String strOutput = PGFavoritesUtil.addToCollection(context, data);
			res = Response.ok(strOutput).type("application/json").build();
		}
		catch (Exception e)
		{
			throw e;
		}
		return res;
	}
	@POST
	@Path("/removeFromCollection")	
	public Response removeFromCollection(@javax.ws.rs.core.Context HttpServletRequest request, 
			String data) throws Exception			
	{
		Response res = null;
		try
		{
			matrix.db.Context context = getAuthenticatedContext(request, false);								
			String strOutput = PGFavoritesUtil.removeFromCollection(context, data);
			res = Response.ok(strOutput).type("application/json").build();
		}
		catch (Exception e)
		{
			throw e;
		}
		return res;
	}

	@GET
	@Path("/getCollectionData")		
	public Response getCollectionData(@javax.ws.rs.core.Context HttpServletRequest request,
			@QueryParam(PGFavoritesUtil.KEY_RELID) String RelId) throws Exception {
		Response res = null;
		try {
			boolean isSCMandatory = false;
			matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);				
			String strOutput = PGFavoritesUtil.getCollectionItems(context, RelId);
			res = Response.ok(strOutput).type("application/json").build();
		} catch (Exception e) {
			throw e;
		}
		return res;
	}
}
