package com.pg.sizebasedataresponse.ws.rulesmanager;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.platform.ven.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;

import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.servlet.FrameworkServlet;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;

import matrix.db.Context;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import com.custom.pg.Artwork.ArtworkConstants;
import com.matrixone.servlet.Framework;


@Path("/rulesManager")
public class PGRTASizeBaseService  extends RestService {
	private static final Logger logger = Logger.getLogger("com.pg.response.rulesmanager.PGRTAPassService");
	private static final String MSG_200 = "CE Creation request submitted successfully and is in progress. Once processed, RTA will send the response back";
	private static final String MSG_201 = "Request is empty or does not contain information for atleast one POA and hence request cannot be processed";
	private static final String MSG_500 = "Request cannot be processed as an exception occurred in RTA";
	private static final String MSG_209 = "Application user does not exist or Inactive in RTA";
	
	/**
	 * 
	 * @param paramHttpServletRequest
	 * @return
	 * @throws MatrixException 
	 * @throws Exception
	 */
private Context getContext(HttpServletRequest paramHttpServletRequest){
		logger.info("Enter method getContext()");
		Context context = null;
		if (Framework.isLoggedIn(paramHttpServletRequest)) {
			context = Framework.getContext(paramHttpServletRequest.getSession(false));
		}
		logger.info("Exit method getContext()");
		return context;
	}
	
	
	/**
	 * 
	 * @param context
	 * @param sJSON
	 * @param sOwner
	 * @return void
	 * @throws Exception
	 */
	private void createJobObject(Context context,String sJSON,String sOwner) throws FrameworkException{
		logger.info("Enter method createJobObject()");
		Job job = new Job();
		job.create(context);
		job.setOwner(context, sOwner);
		job.setVault(context, ArtworkConstants.VAULT_ESERVICE_PRODUCTION);
		job.setAttributeValue(context,DomainConstants.ATTRIBUTE_TITLE,"SIZEBased_RulesManager_ConnectDataRequest");
		job.setDescription(context,sJSON);
	}
	
	  @POST
	  @Path("/createConnectCE")
	  @Consumes({"application/json"})
	  @Produces({"application/json"})
	  public Response createConnectCE(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, RequestDataFromRulesManager reqDataFromRM)throws Exception {
	    logger.info("\n Enter createCopyElements() starts");
	    Context context = null;
	    boolean isContextPushed = false;
	    PGResponseData responseData = null;
	    try {
	    	context = getContext(paramHttpServletRequest);
				String sWbServiceUser = EnoviaResourceBundle.getProperty(context, "emxCPN.AAL.applicationUser");
	    	  	String strPersonId = PersonUtil.getPersonObjectID(context, sWbServiceUser);
	    	  	if(BusinessUtil.isNotNullOrEmpty(strPersonId)) {
	    	  		DomainObject domObj = DomainObject.newInstance(context,strPersonId);
	    	  		if(DomainConstants.STATE_PERSON_ACTIVE.equals(domObj.getInfo(context, DomainConstants.SELECT_CURRENT))) {
	    	  			//push context is required as the context user does not have access to create job objects.
						ContextUtil.pushContext(context);
	    				isContextPushed = true;
	    				List<POA> poaDataFromRM = reqDataFromRM.getPoa();
	    				if(poaDataFromRM != null && !poaDataFromRM.isEmpty() )  {
	    					ObjectMapper mapper = new ObjectMapper();
	    					createJobObject(context,mapper.writeValueAsString(reqDataFromRM),sWbServiceUser);
	    					responseData = new PGResponseData("200",MSG_200);
	    				}else {
	    					responseData = new PGResponseData("201",MSG_201);
	    				}
	    	  		}
	    	  		else
	    	  			responseData = new PGResponseData("209",MSG_209);
	    	  	}else {
	    	  		responseData = new PGResponseData("209",MSG_209);
	    	  	}
	    }catch(Exception ex) {
	    	logger.log(Level.INFO, "Exception occurred while processing the request",ex);
	    	responseData = new PGResponseData("500",MSG_500);
	    	
	    }finally {
			if(context != null && isContextPushed){
				ContextUtil.popContext(context);
			}
			if(context != null && context.isConnected()){
				context.close();
				logger.info("Context SuccessFully Disconnected.");
			}
		}
	    logger.info("\n Enter createCopyElements() Ends.");
	    return Response.ok(responseData).build();
	  }
	
	
	/**
     * This method will be used to logout the session
     * @param request - is the request session context
	 * @return Response
     */
	@javax.ws.rs.Path("/logout")
    @Produces({"application/ds-json", "application/xml"})
    @GET
    public Response logout(@javax.ws.rs.core.Context HttpServletRequest request) {
        try {
            FrameworkServlet.doLogout(request.getSession());
            return Response.status(200).build();
        } catch (Exception e) {
        	logger.log(Level.INFO, "Exception occurred while trying to logout the session",e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}