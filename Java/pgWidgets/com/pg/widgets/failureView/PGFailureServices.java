/*
 * PGClaimServices.java
 * 
 * Added by Dashboard Team
 * For Claim Management Widget related Webservice
 * 
 */

package com.pg.widgets.failureView;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;

/**
 * Class PGFailureServices has all the web service methods defined for the 'Failure View' widget activities.
 * 
 * @since 2018x.7
 * @author 
 *
 */
@Path("/pgFailureServices")
public class PGFailureServices extends RestService
{
	@Path("/OP")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response performOP(@javax.ws.rs.core.Context HttpServletRequest request,
			Map<String,Object> mpRequestMap) throws Exception {
		boolean isSCMandatory = true;
		matrix.db.Context context = getAuthenticatedContext(request, isSCMandatory);
		String mode = (String)mpRequestMap.get("opType");
		Response res = null;
		PGFailureViewUtil objUtil=new PGFailureViewUtil(context);
		if("access".equals(mode))
		{
			res =  objUtil.checkAccess(context);
		}
		else if("getData".equals(mode))
		{
			res =  objUtil.getDataByFind(context,mpRequestMap);
		}
		else if("getFailureViewData".equals(mode))
		{
			res =  objUtil.getFailureViewData(context,mpRequestMap);
		}
		else if("removeErrorMessage".equals(mode))
		{
			res =  objUtil.removeErrorMessageFromObject(context,mpRequestMap);
		}
		else if("resend".equals(mode))
		{
			res =  objUtil.resendToSAPOrGenDoc(context,mpRequestMap);
		}
		return res;
	}
	
	
	
}
