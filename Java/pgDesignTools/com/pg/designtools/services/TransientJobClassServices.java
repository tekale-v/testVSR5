package com.pg.designtools.services;

import javax.ws.rs.Path;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.util.AutomationDemotePromoteFacility;

import matrix.util.MatrixException;


@Path("/TransientJobClassServices")
public class TransientJobClassServices extends RestService {

	protected matrix.db.Context context; 
	
	@GET
	@Path("/demoteAutomationData")
	public Response demoteAutomationData(@Context HttpServletRequest req) throws MatrixException{

		String strResult="";
		try {
			context = getAuthenticatedContext(req,false);

		    String strJobName="";
		    String strJobEvent="";
	        String strObjType="";
	        String strObjName="";
	        String strObjRev="";
	        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
				
					strObjType=urlParam[0];	
					strObjName=urlParam[1];
					strObjRev=urlParam[2];
					strJobEvent=urlParam[3];
					strJobName=urlParam[4];
					
					//GQS - JobName will lead to a Handler being selected; hardcoded for this iteration
					AutomationDemotePromoteFacility jobHandler= new AutomationDemotePromoteFacility(context);
					strResult=jobHandler.init(context,strJobEvent, strObjType,strObjName,strObjRev);
				}
			}
		} catch (DesignToolsIntegrationException ex) {
			return Response.status(ex.getnErrorCode()).entity(ex.getStrErrorMessage()).build();
		}catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strResult).build();
	}
	
	@GET
	@Path("/promoteAutomationData")
	public Response promoteAutomationData(@Context HttpServletRequest req) throws MatrixException{

		String strResult="";
		try {
			context = getAuthenticatedContext(req,false);

		    String strJobName="";
		    String strJobEvent="";
		    String strObjType="";
	        String strObjName="";
	        String strObjRev="";
	        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					strObjType=urlParam[0];	
					strObjName=urlParam[1];
					strObjRev=urlParam[2];
					strJobEvent=urlParam[3];	
					strJobName=urlParam[4];
					
					//GQS - JobName will lead to a Handler being selected; hardcoded for this iteration
					AutomationDemotePromoteFacility jobHandler=new AutomationDemotePromoteFacility(context);
					strResult=jobHandler.init(context,strJobEvent, strObjType,strObjName,strObjRev);
				}
			}
		} catch (DesignToolsIntegrationException ex) {
			return Response.status(ex.getnErrorCode()).entity(ex.getStrErrorMessage()).build();
		}catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strResult).build();
	}
}