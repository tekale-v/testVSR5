package com.pg.cache;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.util.FrameworkException;

import matrix.util.MatrixException;

@Path("/cacheservice")
public class PGCacheService extends RestService {
	
	static final String RESPONSE_TYPE_APPLICATION_JSON = "application/json";
	
	/**
	 * This method returns server cache timestamp for list of types
	 * @param request
	 * @param typeList
	 * @return
	 * @throws FrameworkException
	 */
	@GET
	@Path("/getCachedTS")
	public Response getCachedTS(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("type") String typeList) throws Exception {
		Response res = null;
		boolean isSCMandatory = false;
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
		String strOutput = PGCacheUtil.getCacheTimestamp(context, typeList);
		res = Response.ok(strOutput).type(RESPONSE_TYPE_APPLICATION_JSON).build();
		return res;
	}
	
	/**
	 * This method returns server cache timestamp for a type
	 * @param request
	 * @param strTypeName
	 * @return
	 * @throws FrameworkException
	 */
	@GET
	@Path("/getCachedTypeTS")
	public Response getCachedTypeTS(@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("type") String strTypeName) throws Exception {
		Response res = null;
		boolean isSCMandatory = false;
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
		String strOutput = PGCacheUtil.getCacheTypeTimestamp(context, strTypeName);
		res = Response.ok(strOutput).type(RESPONSE_TYPE_APPLICATION_JSON).build();
		return res;
	}
	
	/**
	 * This method returns data from server cache for a type
	 * @param request
	 * @param strTypeName
	 * @return
	 * @throws MatrixException 
	 */
	@GET
	@Path("/getCachedData")
	public Response getCachedData(@javax.ws.rs.core.Context HttpServletRequest request , @QueryParam("type") String strTypeName) throws Exception {
		Response res = null;
		boolean isSCMandatory = false;
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
		String storeData = PGCacheUtil.getCacheForType(context, strTypeName);		
		res = Response.ok(storeData).build();
		return res;
	}	
}
