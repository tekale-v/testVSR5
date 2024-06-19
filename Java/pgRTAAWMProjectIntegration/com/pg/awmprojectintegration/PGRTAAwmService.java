package com.pg.awmprojectintegration;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.custom.pg.Artwork.ArtworkConstants;
import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.platform.ven.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.servlet.Framework;
import com.matrixone.servlet.FrameworkServlet;

import matrix.db.Context;
import matrix.util.MatrixException;
import javax.servlet.http.HttpSession;
//Added by RTA Capgemini Offshore for 22x.4 Dec_23_CW Req 46176,46177,46178 Starts
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import java.util.List;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
//Added by RTA Capgemini Offshore for 22x.4 Dec_23_CW Req 46176,46177,46178 Ends
@Path("/awm")
public class PGRTAAwmService  extends RestService {
	private static final Logger logger = Logger.getLogger("=========PGRTAAwmService");
	private static final String RESPONSE_TWO_ZERO_ZERO = "200 OK";
	//modified by RTA Capgemini Offshore for 22x.4 Dec_23_CW Req 55550 Starts
	private static final String MESSAGE_POAUPDATE_IN_PROGRESS = "POA Update request will be processed in sometime...";
	//modified by RTA Capgemini Offshore for 22x.4 Dec_23_CW Req 55550 Ends
	//Added by RTA Capgemini Offshore for 22x.4 Dec_23_CW Req 46176,46177,46178 Starts
	private static final String RESPONSE_FIVE_ZERO_ZERO = "500";
	private static final String RESPONSE_TWO_ZERO_ONE = "201 OK";
	private static final String MSG_201 = "Request is empty or does not contain information for atleast one POA and hence request cannot be processed";
	private static final String MSG_209 = "Application user does not exist or Inactive in RTA";
	public static String AWM_USER = null;
	private static final String MSG_500 = "Request cannot be processed as an exception occurred in RTA";
	//Added by RTA Capgemini Offshore for 22x.4 Dec_23_CW Req 46176,46177,46178 Ends
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
	
private void createJobObject(Context context,String sJSON,String sOwner) throws FrameworkException{
	logger.info("Enter method createJobObject()");
	Job job = new Job();
	job.create(context);
	job.setOwner(context, sOwner);
	job.setVault(context, ArtworkConstants.VAULT_ESERVICE_PRODUCTION);
	job.setAttributeValue(context,DomainConstants.ATTRIBUTE_TITLE,"AWM_Project_Update");
	job.setDescription(context,sJSON);
	logger.info("Exit method createJobObject()");
}
	/**
	 * 
	 * @param context
	 * @param sJSON
	 * @param sOwner
	 * @return void
	 * @throws Exception
	 */ 
//Added by RTA Capgemini Offshore for 22x.4 Dec_23_CW Req 46176,46177,46178 Starts
@POST
	  @Path("/updateTasks")
	  @Consumes({"application/json"})
	  @Produces({"application/json"})
	public Response updateTasks(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, PgAwmRequestData awmReqData)throws Exception {
	logger.log(Level.INFO, "Enter method updateTasks()");
	String errorMsg = DomainConstants.EMPTY_STRING; 
	Context context = null;
	boolean isContextPushed = false;
	PgPoaAwmResponse responseData = null;
	try {
		context = getContext(paramHttpServletRequest);
		if(context != null) {
		String sWbServiceUser = context.getUser();
		logger.log(Level.INFO, "<<<<<<<sWbServiceUser>>>>>>>>>"+sWbServiceUser);
	  	String strPersonId = PersonUtil.getPersonObjectID(context, sWbServiceUser);
	  	if(BusinessUtil.isNotNullOrEmpty(strPersonId)) {
	  		DomainObject domObj = DomainObject.newInstance(context,strPersonId);
	  		if(DomainConstants.STATE_PERSON_ACTIVE.equals(domObj.getInfo(context, DomainConstants.SELECT_CURRENT))) {
	  			//push context is required as the context user does not have access to create job objects.
				ContextUtil.pushContext(context);
				isContextPushed = true;
				List<PgPoaAwmRequestData> poaDataFromAWM = awmReqData.getPgAAA();
				if(poaDataFromAWM != null && !poaDataFromAWM.isEmpty() )  {
					ObjectMapper mapper = new ObjectMapper();
					createJobObject(context,mapper.writeValueAsString(awmReqData),sWbServiceUser);
					responseData = new PgPoaAwmResponse(RESPONSE_TWO_ZERO_ZERO,MESSAGE_POAUPDATE_IN_PROGRESS);
				}else {
					responseData = new PgPoaAwmResponse("201",MSG_201);
				}
	  		}
	  		else
	  			responseData = new PgPoaAwmResponse("209",MSG_209);
	  	}else {
	  		responseData = new PgPoaAwmResponse("209",MSG_209);
	  	}
		}		
		} catch (Exception e) {
					logger.log(Level.INFO, "Exception occurred while processing the request",e);
					responseData = new PgPoaAwmResponse(RESPONSE_FIVE_ZERO_ZERO, MSG_500);
			}
			
			finally {
				if(context != null && isContextPushed){
					ContextUtil.popContext(context);
				}
				if(context != null && context.isConnected()){
					context.close();
					logger.info("Context SuccessFully Disconnected.");
				}
			}
			
	JsonObjectBuilder job = Json.createObjectBuilder(); 
	if(responseData!=null) {
		job.add("code",responseData.code);
		job.add("message",responseData.message);
	}
	logger.log(Level.INFO, "Exit method updateTasks()");
	return Response.ok(job.build()).build();
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