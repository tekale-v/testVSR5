package com.pg.designtools.services;

import javax.ws.rs.Path;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.IPManagement;
import com.pg.designtools.util.InterfaceManagement;

import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;

@Path("/InterfaceServices")
public class InterfaceServices extends RestService {

	protected matrix.db.Context context; 
	private static final String CONST_OBJECT="The object ";
	private static final String CONST_NOT_FOUND=" was not found";
	
	@GET
	@Path("/setAutomationUsageInterface")
	public Response setAutomationUsageInterface(@Context HttpServletRequest req) throws MatrixException,IOException{

		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Start of setAutomationUsageInterface method");
		String strMessage="";
		try {
			context = getAuthenticatedContext(req,false);

	        String strType="";
	        String strName="";
	        String strRev="";
	        String strApplicationName="";
	        String strProcessName="";
	        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>queryString::"+queryString);
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					strType=urlParam[0];
					strName=urlParam[1];
					strRev=urlParam[2];
					strApplicationName=urlParam[3];
					strProcessName=urlParam[4];
					
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strType::"+strType+" strName::"+strName+" strRev::"+strRev);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strApplicationName::"+strApplicationName+" strProcessName::"+strProcessName);
					IPManagement ipMgmt=new IPManagement(context);
					MapList mlObject=ipMgmt.findObject(context, strType, strName, strRev, new StringList(DomainConstants.SELECT_ID));
										
					if(!mlObject.isEmpty()) {
						Map<String,String> mpObject=(Map<String, String>) mlObject.get(0);
						String[] args=new String[3];
						args[0]=strApplicationName;
						args[1]=strProcessName;
						args[2]=mpObject.get(DomainConstants.SELECT_ID);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>args[0]::"+args[0]+" args[1]::"+args[1]+" args[2]::"+args[2]);
						
						int iResult=JPO.invoke(context,"pgDTAutomationMetricTracking", null, "addUsageTrackingToData", args);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>iResult::"+iResult);
						if(iResult==0)
							strMessage=DataConstants.INTERFACE_SUCCESS_MESSAGE;
					}else {
						StringBuilder sbMessage=new StringBuilder();
						sbMessage.append(CONST_OBJECT);
						sbMessage.append(strType).append(" ");
						sbMessage.append(strName).append(" ");
						sbMessage.append(strRev);
						sbMessage.append(CONST_NOT_FOUND);
						strMessage=sbMessage.toString();
					}
				}
			}
		}catch (Exception ex) {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Exception::"+ex.getMessage());
			return Response.serverError().entity(ex.toString()).build(); 
		}
		return Response.status(200).entity(strMessage).build();
	}
	
	@GET
	@Path("/addExtension")
	public Response addExtension(@Context HttpServletRequest req) {

		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Start of addExtension method");
		String strMessage="";
		try {
			context = getAuthenticatedContext(req,false);

	        String strType="";
	        String strName="";
	        String strRev="";
	        String strInterfaceName="";
		        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>queryString::"+queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					strType=urlParam[0];
					strName=urlParam[1];
					strRev=urlParam[2];
					strInterfaceName=urlParam[3];
								
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strType::"+strType+" strName::"+strName+" strRev::"+strRev);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strInterfaceName::"+strInterfaceName);
					
					IPManagement ipMgmt=new IPManagement(context);
					MapList mlObject=ipMgmt.findObject(context, strType, strName, strRev, new StringList(DomainConstants.SELECT_ID));
					
					
					if(!mlObject.isEmpty()) {
						Map<String,String> mpObject=(Map<String, String>) mlObject.get(0);
						int iResult=1;
						String strObjectId=mpObject.get(DomainConstants.SELECT_ID);
						
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strObjectId::"+strObjectId);
						
						InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
						boolean bHasInterface=interfaceMgmt.checkInterfaceOnObject(context, strObjectId, strInterfaceName);
						if(!bHasInterface)
							 iResult=interfaceMgmt.addInterface(context, strObjectId, strInterfaceName);
						
						if(iResult==0)
								strMessage=DataConstants.INTERFACE_SUCCESS_MESSAGE;
						else if(iResult==3)
							strMessage=DataConstants.INTERFACE_DOES_NOT_EXIST;
						else if(bHasInterface)
							strMessage=DataConstants.INTERFACE_ALREADY_ADDED_MESSAGE;
						}else {
						StringBuilder sbMessage=new StringBuilder();
						sbMessage.append(CONST_OBJECT);
						sbMessage.append(strType).append(" ");
						sbMessage.append(strName).append(" ");
						sbMessage.append(strRev);
						sbMessage.append(CONST_NOT_FOUND);
						strMessage=sbMessage.toString();
					}
				}
			}
		}catch (Exception ex) {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Exception::"+ex.getMessage());
			return Response.status(500).entity(ex.getMessage()).build(); 
		}
		return Response.status(200).entity(strMessage).build();
	}
	
	
	@GET
	@Path("/removeExtension")
	public Response removeExtension(@Context HttpServletRequest req) {

		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Start of removeExtension method");
		String strMessage="";
		try {
			context = getAuthenticatedContext(req,false);

	        String strType="";
	        String strName="";
	        String strRev="";
	        String strInterfaceName="";
		        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>queryString::"+queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					strType=urlParam[0];
					strName=urlParam[1];
					strRev=urlParam[2];
					strInterfaceName=urlParam[3];
								
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strType::"+strType+" strName::"+strName+" strRev::"+strRev);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strInterfaceName::"+strInterfaceName);
					
					IPManagement ipMgmt=new IPManagement(context);
					MapList mlObject=ipMgmt.findObject(context, strType, strName, strRev, new StringList(DomainConstants.SELECT_ID));
					
					if(!mlObject.isEmpty()) {
						Map<String,String> mpObject=(Map<String, String>) mlObject.get(0);
						int iResult=1;
						String strObjectId=mpObject.get(DomainConstants.SELECT_ID);
						
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strObjectId::"+strObjectId);
						
						InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
						boolean bHasInterface=interfaceMgmt.checkInterfaceOnObject(context, strObjectId, strInterfaceName);
						if(bHasInterface)
							 iResult=interfaceMgmt.removeInterface(context, strObjectId, strInterfaceName);
						if(iResult==0)
								strMessage=DataConstants.INTERFACE_REMOVE_SUCCESS_MESSAGE;
						else if(iResult==3)
							strMessage=DataConstants.INTERFACE_DOES_NOT_EXIST;
						else if(!bHasInterface)
							strMessage=DataConstants.INTERFACE_NOT_PRESNT_ON_OBJECT;
						}else {
							StringBuilder sbMessage=new StringBuilder();
							sbMessage.append(CONST_OBJECT);
							sbMessage.append(strType).append(" ");
							sbMessage.append(strName).append(" ");
							sbMessage.append(strRev);
							sbMessage.append(CONST_NOT_FOUND);
							strMessage=sbMessage.toString();
						}
				}
			}
		}catch (Exception ex) {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Exception::"+ex.getMessage());
			return Response.status(500).entity(ex.getMessage()).build(); 
		}
		return Response.status(200).entity(strMessage).build();
	}
	
	@GET
	@Path("/addProcessAppTracking")
	public Response addProcessAppTracking(@Context HttpServletRequest req) {

		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Start of addProcessAppTracking method");
		String strMessage="";
		try {
			context = getAuthenticatedContext(req,false);

	        String strType="";
	        String strName="";
	        String strRev="";
	        String strProcessApplicationName="";
	        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>queryString::"+queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				
				StringList slParams=StringUtil.split(queryString, DataConstants.SEPARATOR_AT_THE_RATE);
				
				if (!slParams.isEmpty()) {

					strType=slParams.getElement(0);
					strName=slParams.getElement(1);
					strRev=slParams.getElement(2);
					strProcessApplicationName=slParams.getElement(3);
					
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strType::"+strType+" strName::"+strName+" strRev::"+strRev);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>strApplicationName::"+strProcessApplicationName);
					
					IPManagement ipMgmt=new IPManagement(context);
					MapList mlObject=ipMgmt.findObject(context, strType, strName, strRev, new StringList(DomainConstants.SELECT_ID));
					
					if(!mlObject.isEmpty()) {
						Map<String,String> mpObject=(Map<String, String>) mlObject.get(0);
						String[] args=new String[2];
						args[0]=strProcessApplicationName;
						args[1]=mpObject.get(DomainConstants.SELECT_ID);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>args[0]::"+args[0]+" args[1]::"+args[1]);
						
						int iResult=JPO.invoke(context,"pgDTAutomationMetricTracking", null, "addProcessAppTracking", args);
						//Interface added successfully --return value 0
						//Interface already exists--return value 2
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>iResult::"+iResult);
						if(iResult==0)
							strMessage=DataConstants.INTERFACE_SUCCESS_MESSAGE;
						else if(iResult==1)
							strMessage=DataConstants.ENTER_CORRECT_APPLICATION_NAME;
						else if(iResult==2)
							strMessage=DataConstants.INTERFACE_ALREADY_ADDED_MESSAGE;
						else if(iResult==3)
							strMessage=DataConstants.INTERFACE_DOES_NOT_EXIST;
					}else {
						StringBuilder sbMessage=new StringBuilder();
						sbMessage.append(CONST_OBJECT);
						sbMessage.append(strType).append(" ");
						sbMessage.append(strName).append(" ");
						sbMessage.append(strRev);
						sbMessage.append(CONST_NOT_FOUND);
						strMessage=sbMessage.toString();
					}
				}
			}
		}catch (Exception ex) {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Exception::"+ex.getMessage());
			return Response.status(500).entity(ex.getMessage()).build(); 
		}
		return Response.status(200).entity(strMessage).build();
	}
}