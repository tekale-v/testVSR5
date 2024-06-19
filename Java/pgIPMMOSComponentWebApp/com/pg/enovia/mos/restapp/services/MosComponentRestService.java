package com.pg.enovia.mos.restapp.services;

import java.util.List;
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

import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.pg.enovia.mos.config.MosConfig;
import com.pg.enovia.mos.enumeration.MOSConstants;
import com.pg.enovia.mos.restapp.bean.ObjectBean;
import com.pg.enovia.mos.restapp.bean.Preferences;
import com.pg.enovia.mos.restapp.utils.MosComponentAppUtils;

import matrix.db.Context;
import matrix.util.MatrixException;

/**
 * @author DSM(Sogeti) 18x.6
 *
 */
@Path("/moscomponent")
public class MosComponentRestService extends MosComponentRestServiceBase {

	/**
	 * @param paramHttpServletRequest
	 * @param paramString
	 * @param paramParentId
	 * @return
	 * @throws FrameworkException 
	 */
	@Path("/getall")
	@GET
	@Produces({ "application/json", "application/ds-json" })
	public Response getAllMOSComponent(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
            @QueryParam("objectId") String paramString, @QueryParam("parentId") String paramParentId) throws FrameworkException{
        Response localResponse;
        boolean bIsCTXPushed = false;
        matrix.db.Context localContext =null;
        try {
            localContext = getAuthenticatedContext(paramHttpServletRequest, false);
            checkCSRFToken(paramHttpServletRequest);
       
            //Modified by DSM(Sogeti)-2015x.1.2 for COS (Defect#7597) on 24-Oct-2016 - Starts
            //Modified by DSM(Sogeti)-2018x.6.1 for COS (Defect#43170) - Starts
            //Need to display No Accessed MOS component details to MOS user's.
            ContextUtil.pushContext(localContext);
            bIsCTXPushed = true;
            List<ObjectBean> list=MosComponentAppUtils.getMosDetails(localContext, paramParentId);
            //Modified by DSM(Sogeti)-2018x.6.1 for COS (Defect#43170) - Ends
            //Modified by DSM(Sogeti)-2015x.1.2 for COS (Defect#7597) on 24-Oct-2016 - Ends
           
            localResponse = Response.ok(MosComponentAppUtils.jaxbObjectToJSON(list),
                            MOSConstants.RESPONSE_JSON.getValue())
                            .cacheControl(this.cacheControl)
                            .build();
        } catch (Exception localException) {
            localResponse = setErrorResponse(localException);
            Logger.getLogger(MosComponentRestService.class.getName()).log(Level.WARNING, "Got exception: {0}", localException.toString());             
        } finally {
            if(bIsCTXPushed) {
                ContextUtil.popContext(localContext);
                bIsCTXPushed = false;
            }
        }
        return localResponse;
    }

	
	/**
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @return
	 */
	@Path("/configuration")
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
			localResponse = Response.ok(localJsonObjectBuilder.build().toString(),
								MOSConstants.RESPONSE_JSON.getValue())
								.cacheControl(this.cacheControl)
								.build();
		} catch (Exception localException2) {
			localResponse = Response.status(500).build();
			Logger.getLogger(MosComponentRestService.class.getName()).log(Level.WARNING, "Got exception: {0}", localException2.toString());
		}
		return localResponse;
	}

	/**
	 * @param paramHttpServletRequest
	 * @param paramHttpServletResponse
	 * @return
	 */
	@Path("/getColumnPrefernces")
	@GET
	@Produces({ "application/json", "application/ds-json" })
	public Response getPrefernces(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest,
			@javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse) {
		Response localResponse = null;
		disableResponseCache(paramHttpServletResponse);

		try {
			Context localContext = getAuthenticatedContext(paramHttpServletRequest, false);
			checkCSRFToken(paramHttpServletRequest);
			
			MosConfig conf=new MosConfig();
			Preferences rootObject =conf.parseColumnPreferences(localContext, MOSConstants.CONFIG_PAGE.getValue()).getRootObject();
			
			localResponse = Response
							.ok(MosComponentAppUtils.jaxbObjectToJSON(rootObject), 
							MOSConstants.RESPONSE_JSON.getValue())
							.cacheControl(this.cacheControl)
							.build();
		} catch (Exception localException2) {
			localResponse = Response.status(500).build();
			Logger.getLogger(MosComponentRestService.class.getName()).log(Level.WARNING, "Got exception: {0}", localException2.toString());
		}
		return localResponse;
	}
	
	/**
	 * @param paramException
	 * @return
	 */
	private Response setErrorResponse(Exception paramException) {
		JsonObjectBuilder localJsonObjectBuilder = Json.createObjectBuilder();
		Logger.getLogger(MosComponentRestService.class.getName()).log(Level.WARNING, "Got exception: {0}", paramException.toString());
		try {
			localJsonObjectBuilder.add("message", paramException.getMessage());
		} catch (Exception localException) {
			Logger.getLogger(MosComponentRestService.class.getName()).log(Level.WARNING, "Got exception: {0}", localException.toString());
		}
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(localJsonObjectBuilder.build().toString())
						.cacheControl(this.cacheControl)
						.build();
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
			Object localObject = localHttpSession.getAttribute(MOSConstants.ENO_CSRF_TOKEN.getValue());
			if (localObject == null) {
				str = generateCSRFToken();
				localHttpSession.setAttribute(MOSConstants.ENO_CSRF_TOKEN.getValue(), str);
			} else {
				str = localObject.toString();
			}
		} catch (Exception localException) {
			Logger.getLogger(MosComponentRestService.class.getName()).log(Level.WARNING, "Got exception: {0}", localException.toString());
		}
		return str;
	}

	/**
	 * @param paramContext
	 * @return
	 * @throws FrameworkException
	 */
	private int getTimeoutFromSettings(){
		int i = Integer.parseInt("100");
		return i * 1000;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static String generateCSRFToken() throws Exception {
		return UINavigatorUtil.generateRandomId("SHA1PRNG", 32);
	}
	/**
	 * @param paramHttpServletResponse
	 */
	private static void disableResponseCache(HttpServletResponse paramHttpServletResponse)
	{
	    paramHttpServletResponse.setHeader("CacheControl", "no-cache, no-store");
	    paramHttpServletResponse.setHeader("Pragma", "no-cache");
	    paramHttpServletResponse.setHeader("Expires", "-1");
	}

	  /**
	 * @param paramHttpServletRequest
	 */
	private void checkCSRFToken(HttpServletRequest paramHttpServletRequest)
	  {
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
		 }catch(Exception e) {
			 Logger.getLogger(MosComponentRestService.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		 }
	
	  }
}
