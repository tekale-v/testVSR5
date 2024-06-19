package com.pg.dsm.ws.restapp.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.pg.dsm.ws.enumeration.DSMConstants;
import com.pg.dsm.ws.restapp.utils.DSMAppUtils;

import matrix.util.MatrixException;

/**
 * @author DSM(Sogeti)
 *
 */
@Path("/webservice")
public class DSMRestService extends DSMRestServiceBase {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * @param paramHttpServletRequest
	 * @param paramString
	 * @param paramParentId
	 * @return
	 * @throws FrameworkException
	 */
	@Path("/call")
	@GET
	@Produces({ "application/json", "application/ds-json" })
	public Response callService(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@QueryParam("objectId") String objectId,@QueryParam("program") String sProgram, @QueryParam("tableconfig") String tableConfig){
		Response localResponse;
		try {
			matrix.db.Context localContext = getAuthenticatedContext(paramHttpServletRequest, false);
			checkCSRFToken(paramHttpServletRequest);
			 
			String jsonData=new DSMAppUtils(localContext, objectId, sProgram).setTableConfig(tableConfig).execute().getJsonOutput();
			localResponse = Response
					.ok(jsonData, DSMConstants.RESPONSE_JSON.getValue())
					.cacheControl(this.cacheControl).build();
		} catch (Exception localException) {
			localResponse = setErrorResponse(localException);
			logger.log(Level.WARNING,null,localException);
		} 
		return localResponse;
	}

	/**
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @return
	 */
	@Path("/config")
	@GET
	@Produces({ "application/json", "application/ds-json" })
	public Response getConfiguration(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse) {
		Response localResponse = null;
		disableResponseCache(paramHttpServletResponse);

		matrix.db.Context localContext = null;
		try {

			localContext = getAuthenticatedContext(paramHttpServletRequest, false);
			String strToken = getCSRFToken(paramHttpServletRequest);

			JsonObjectBuilder localJsonObjectBuilder = Json.createObjectBuilder();

			localJsonObjectBuilder.add("csrf", XSSUtil.encodeForHTML(localContext, strToken));

			int i = getTimeoutFromSettings();
			localJsonObjectBuilder.add("timeout", i);
			localResponse = Response
					.ok(localJsonObjectBuilder.build().toString(), DSMConstants.RESPONSE_JSON.getValue())
					.cacheControl(this.cacheControl).build();
		} catch (Exception localException) {
			localResponse = Response.status(500).build();
			logger.log(Level.WARNING,null,localException);
		}
		return localResponse;
	}

	/**
	 * @param paramException
	 * @return
	 */
	private Response setErrorResponse(Exception paramException) {
		JsonObjectBuilder localJsonObjectBuilder = Json.createObjectBuilder();
		logger.log(Level.WARNING,null,paramException);
		try {
			localJsonObjectBuilder.add("message", paramException.getMessage());
		} catch (Exception localException) {
			logger.log(Level.WARNING,null,localException);
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(localJsonObjectBuilder.build().toString())
				.cacheControl(this.cacheControl).build();
	}

	/**
	 * @param paramHttpServletRequest
	 * @return
	 * @throws Exception
	 */
	private String getCSRFToken(HttpServletRequest paramHttpServletRequest) {
		String str = "";
		try {
			HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
			Object localObject = localHttpSession.getAttribute(DSMConstants.ENO_CSRF_TOKEN.getValue());
			if (localObject == null) {
				str = generateCSRFToken();
				localHttpSession.setAttribute(DSMConstants.ENO_CSRF_TOKEN.getValue(), str);
			} else {
				str = localObject.toString();
			}
		} catch (Exception localException) {
			logger.log(Level.WARNING,null,localException);
		}
		return str;
	}

	/**
	 * @param paramContext
	 * @return
	 * @throws FrameworkException
	 */
	private int getTimeoutFromSettings() {
		int i = Integer.parseInt("100");
		return i * 1000;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private static String generateCSRFToken() throws Exception {
		return UINavigatorUtil.generateRandomId("SHA1PRNG", 32);
	}

	/**
	 * @param paramHttpServletResponse
	 */
	private static void disableResponseCache(HttpServletResponse paramHttpServletResponse) {
		paramHttpServletResponse.setHeader("CacheControl", "no-cache, no-store");
		paramHttpServletResponse.setHeader("Pragma", "no-cache");
		paramHttpServletResponse.setHeader("Expires", "-1");
	}

	/**
	 * @param paramHttpServletRequest
	 */
	private void checkCSRFToken(HttpServletRequest paramHttpServletRequest) {
		try {
			String str1 = paramHttpServletRequest.getHeader("X-Request");
			HttpSession localHttpSession = paramHttpServletRequest.getSession(true);

			String str2 = localHttpSession.getAttribute("ENO_CSRF_TOKEN").toString();

			if ((str1 == null) || (str1.isEmpty())) {
				throw new MatrixException("");
			}
			if ((str2 == null) || (str2.isEmpty())) {
				throw new MatrixException("");
			}

			if (!str2.equals(str1))
				throw new MatrixException("");
		} catch (Exception e) {
			logger.log(Level.WARNING,null,e);
		}
	}
}
