package com.pg.designtools.services;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.servlet.Framework;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonProductData;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.integrations.ebom.ImportIntoReference;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.integrations.ebom.CollaborateEBOMJobFacility;
import com.pg.designtools.util.IPManagement;
import com.pg.designtools.util.SyncManagement;
import com.matrixone.apps.domain.util.StringUtil;

import matrix.util.StringList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;

@Path("/SyncClassServices")
public class SyncClassServices extends RestService {

	protected matrix.db.Context context; 
	
	@GET
	@Path("/getECPartName")
	public Response getECPartName(@Context HttpServletRequest req) {

		String strECPartName="";
		try {
			context = getAuthenticatedContext(req,false);

		    String strName="";
	        String strRev="";
	        String strType="";
	        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					strType=urlParam[0];
					strName=urlParam[1];
					strRev=urlParam[2];
									
					SyncManagement syncMgmt=new SyncManagement(context);
					strECPartName=syncMgmt.getConnectedECPartName(context, strType, strName, strRev);
				}
			}
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strECPartName).build();
	}
	
	@GET
	@Path("/checkMandatoryAttributes")
	public Response checkMandatoryAttributes(@Context HttpServletRequest req) {

		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of checkMandatoryAttributes method");
	    String strMessage="";
		try {
			context = getAuthenticatedContext(req,false);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> context user::"+context.getUser());
		    String strECPartObjectId="";
		    String strECPartType="";
	        String strType="";
	        String strName="";
	        String strRev="";
	        
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			String[] urlParam;
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> queryString::"+queryString);

			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				
				if (urlParam.length > 0) {
					strType=urlParam[0];
					strName=urlParam[1];
					strRev=urlParam[2];
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> strType::"+strType+" strName::"+strName+" strRev::"+strRev);
					
					StringList slSelects=new StringList(3);
					slSelects.addElement("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
					slSelects.addElement("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.id");
					slSelects.addElement(DomainConstants.SELECT_ID);
					
					IPManagement ipMgmt=new IPManagement(context);
					MapList mlObject=ipMgmt.findObject(context,strType,strName,strRev,slSelects);
					Map mpECPart=new HashMap();
					if(!mlObject.isEmpty())
						mpECPart=(Map)mlObject.get(0);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> mlObject::"+mlObject);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> mpECPart::"+mpECPart);
					
					if(DataConstants.TYPE_VPMREFERENCE.equals(strType)) {
						//get the connected EC Part type and object Id
						strECPartObjectId=(String)mpECPart.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.id");
						strECPartType=(String)mpECPart.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
					}else {
						// get the objectId of the EC Part
						strECPartObjectId=(String)mpECPart.get(DomainConstants.SELECT_ID);
						strECPartType=strType;
					}
					
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> strECPartObjectId::"+strECPartObjectId+" strECPartType::"+strECPartType);
					
					if(UIUtil.isNotNullAndNotEmpty(strECPartObjectId) && UIUtil.isNotNullAndNotEmpty(strECPartType)) {
						Map<String,String> mpECPartInfo=new HashMap();
						mpECPartInfo.put(DomainConstants.SELECT_ID, strECPartObjectId);
						mpECPartInfo.put(DomainConstants.SELECT_TYPE, strECPartType);
						CommonProductData cpd=new CommonProductData(context);
						strMessage=cpd.checkMandatoryAttributes(context, mpECPartInfo);
					}else {
						DesignToolsIntegrationException dte=new DesignToolsIntegrationException();
						int iCode=dte.getD2SExceptionMessageCode(DataConstants.NO_EC_PART_CONNECTED);
						strMessage=dte.getD2SExceptionMessage(iCode);
						VPLMIntegTraceUtil.trace(context, ">>  checkMandatoryAttributes error code:" + iCode+" message::"+strMessage);
						return Response.status(iCode).entity(strMessage).build();
					}
				}
			}
		}catch (DesignToolsIntegrationException ex) {
			VPLMIntegTraceUtil.trace(context, ">>  checkMandatoryAttributes error code:" + ex.getnErrorCode()+" message::"+ex.getStrErrorMessage());
			return Response.status(ex.getnErrorCode()).entity(ex.getStrErrorMessage()).build();
		} 
		catch (Exception ex) {
			VPLMIntegTraceUtil.trace(context, ">>  checkMandatoryAttributes error message::"+ex.getMessage());
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strMessage).build();
	}
	
	@GET
	@Path("/generateXMLForMandatoryAttributes")
	@Produces({"application/xml", "application/json"})
	public Response generateXMLForMandatoryAttributes(@Context HttpServletRequest req) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of generateXMLForMandatoryAttributes method");
		String strXML="";
		try {
			context=getAuthenticatedContext(req, false);
			
			SyncManagement syncMgmt=new SyncManagement(context);
			strXML=syncMgmt.generateXMLForMandatoryAttributes(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Final output::"+strXML);
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strXML).build();
	}
	

	@POST
	@Path("/genEBOM")
	/**
	 * This Web Service is called for EBOM Sync Operation. 
	 * @param request
	 * @param incomingData
	 * @return
	 * @throws Exception
	 */
	public Response generatebom(@Context HttpServletRequest request) throws Exception
	{				

		context = Framework.getContext(request.getSession(false)); 
		VPLMIntegTraceUtil.trace(context, "\n START of genEBOM webservice");
		StringBuilder strReturnMessageBuf;
		String line = null;
		StringBuilder sbCatiaDetails = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
		while((line = in.readLine())!=null) {
			sbCatiaDetails.append(line);
		}
		String strXMLDetails = sbCatiaDetails.toString();
	
		VPLMIntegTraceUtil.trace(context, "Input XML::"+strXMLDetails);
		try 
		{
			//call function to execute the sync
			CollaborateEBOMJobFacility collaborateebomJobFacility = new CollaborateEBOMJobFacility();
			
			strReturnMessageBuf = collaborateebomJobFacility.collaborateWithProductData(context,strXMLDetails);
		}
		catch(Exception ex)
		{
			return Response.serverError().entity(DataConstants.STR_ERROR + DataConstants.SEPARATOR_COLON+ex.toString()).build();			
		}
		return Response.status(200).entity(strReturnMessageBuf.toString()).build();
	}
	
	@POST
	@Path("/reviseUpstage")
	/**
	 * This Web Service is called for EBOM Sync Operation. 
	 * @param request
	 * @param incomingData
	 * @return
	 * @throws Exception
	 */
	public Response reviseUpstage(@Context HttpServletRequest request) throws Exception
	{				
		context = Framework.getContext(request.getSession(false)); 
		VPLMIntegTraceUtil.trace(context, "\n START of reviseUpstage webservice");
		VPLMIntegTraceUtil.trace(context, "\n context::"+context.getUser());
		StringBuilder strReturnMessageBuf;
		String line = null;
		StringBuilder sbCatiaDetails = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
		while((line = in.readLine())!=null) {
			sbCatiaDetails.append(line);
		}
		String strXMLDetails = sbCatiaDetails.toString();
		VPLMIntegTraceUtil.trace(context, "Input XML::"+strXMLDetails);
		try 
		{
			//call function to execute the sync
			CollaborateEBOMJobFacility collaborateebomJobFacility = new CollaborateEBOMJobFacility();
			
			strReturnMessageBuf = collaborateebomJobFacility.executeRevGoToStage(context, strXMLDetails);
		}
		catch(Exception ex)
		{
			return Response.serverError().entity(DataConstants.STR_ERROR + DataConstants.SEPARATOR_COLON+ex.toString()).build();			
		}
		return Response.status(200).entity(strReturnMessageBuf.toString()).build();
	}
	
	@GET
	@Path("/getObjectTNRFromPhysicalId")
	public Response getObjectTNRFromPhysicalId(@Context HttpServletRequest req) {

		VPLMIntegTraceUtil.trace(context, ">>>> START of getObjectTNRFromPhysicalId method");
		String strObjectDetails="";
		try {
			context = getAuthenticatedContext(req,false);

			String strPhysicalId = req.getQueryString();
			strPhysicalId =  java.net.URLDecoder.decode(strPhysicalId,"UTF-8");
			
			VPLMIntegTraceUtil.trace(context, ">>>> strPhysicalId::"+strPhysicalId);

			if (UIUtil.isNotNullAndNotEmpty(strPhysicalId)) {
				
					SyncManagement syncMgmt=new SyncManagement(context);
					strObjectDetails=syncMgmt.getObjectTNRFromPhysicalId(context,strPhysicalId);
					VPLMIntegTraceUtil.trace(context, ">>>> strObjectDetails::"+strObjectDetails);
				}
		} catch (Exception ex) {
			return Response.status(500).entity(ex.getMessage()).build();
		}
		return Response.status(200).entity(strObjectDetails).build();
	}
	
	@GET
	@Path("/importInto")
	/**
	 * This Web Service is called for Import Into Operation. 
	 * @param request
	 * @param incomingData that will include the Model Source and Import Source
	 * @return Response
	 * @throws Exception
	 */

	public Response importIntoReference(@Context HttpServletRequest req) {
		
		StringBuilder strReturnMessageBuf=new StringBuilder();
		try {

			// Get the user context
			if (Framework.isLoggedIn(req)) {
				context = Framework.getContext(req.getSession(false));
			}
			
			VPLMIntegTraceUtil.trace(context, ">>>> START of importIntoReference method");
			VPLMIntegTraceUtil.trace(context, ">>>> context::"+context.getUser());
			
			
			//evaluate the incoming arguments
			String strBaseProdNameParameter = DomainConstants.EMPTY_STRING;
			String strBaseProdRevParameter = DomainConstants.EMPTY_STRING;
			String strImportedProdNameParameter =  DomainConstants.EMPTY_STRING;
			String strImportedProdRevParameter = DomainConstants.EMPTY_STRING;
			String strImportedProdOIDParameter=DomainConstants.EMPTY_STRING;
			String strBaseProdName;
			String strBaseProdRev;
			String strImportedProdName= DomainConstants.EMPTY_STRING;
			String strImportedProdRev= DomainConstants.EMPTY_STRING;
			String strReviseBase= DomainConstants.EMPTY_STRING;
			boolean bRevise=true;
			
			String[] urlParam;
			String queryString = req.getQueryString();
			queryString =  java.net.URLDecoder.decode(queryString,"UTF-8");
			
			if(queryString.contains("%26")){
				queryString = queryString.replace("%26", "&");
			}
			
			VPLMIntegTraceUtil.trace(context, ">>>> queryString::"+queryString);
			
			if (UIUtil.isNotNullAndNotEmpty(queryString)) {
				urlParam = queryString.split("&");
				// Put the two prods in a map.Get the Query Type and Root Product Name from URL/Query String
				if (queryString.contains("NewProductOID")) {
					strBaseProdNameParameter = urlParam[0];
					strBaseProdRevParameter= urlParam[1];
					strImportedProdOIDParameter = urlParam[2];
					strReviseBase = urlParam[3];
				}else {
					strBaseProdNameParameter = urlParam[0];
					strBaseProdRevParameter= urlParam[1];
					strImportedProdNameParameter = urlParam[2];
					strImportedProdRevParameter = urlParam[3];
					strReviseBase = urlParam[4];
				}
			}
			VPLMIntegTraceUtil.trace(context, ">>>>strBaseProdNameParameter::"+strBaseProdNameParameter+" strBaseProdRevParameter::"+strBaseProdRevParameter);
			VPLMIntegTraceUtil.trace(context, ">>>>strImportedProdOIDParameter::"+strImportedProdOIDParameter);
			VPLMIntegTraceUtil.trace(context, ">>>>strReviseBase::"+strReviseBase);
			VPLMIntegTraceUtil.trace(context, ">>>>strImportedProdNameParameter::"+strImportedProdNameParameter+" strImportedProdRevParameter::"+strImportedProdRevParameter);
			
			
			strBaseProdName = StringUtil.split(strBaseProdNameParameter, "=").get(1);
			strBaseProdRev= StringUtil.split(strBaseProdRevParameter, "=").get(1);
			
			VPLMIntegTraceUtil.trace(context, ">>>> strBaseProdName::"+strBaseProdName+" strBaseProdRev::"+strBaseProdRev);
			
			if(UIUtil.isNotNullAndNotEmpty(strImportedProdOIDParameter)) {
				
				String strPhysicalId= StringUtil.split(strImportedProdOIDParameter, "=").get(1);
				VPLMIntegTraceUtil.trace(context, ">>>> strPhysicalId::"+strPhysicalId);
				
				
				if(UIUtil.isNotNullAndNotEmpty(strPhysicalId)) {
					SyncManagement syncMgmt=new SyncManagement(context);
					String strObjectDetails=syncMgmt.getObjectTNRFromPhysicalId(context,strPhysicalId);
					VPLMIntegTraceUtil.trace(context, ">>>> strObjectDetails::"+strObjectDetails);
				
					if(strObjectDetails.contains(DataConstants.SEPARATOR_PIPE)) {
						StringList slObjDetails=StringUtil.split(strObjectDetails,DataConstants.SEPARATOR_PIPE);
						strImportedProdName=slObjDetails.get(1);
						strImportedProdRev=slObjDetails.get(2);
					}else {
						//display message that object does not exist
						strReturnMessageBuf.append(strObjectDetails);
					}
				}else {
					strReturnMessageBuf.append(DataConstants.STR_PHYSICALID_NOT_PASSED);
				}
			}else {
				strImportedProdName=StringUtil.split(strImportedProdNameParameter, "=").get(1);
				strImportedProdRev=StringUtil.split(strImportedProdRevParameter, "=").get(1);
			}
			
			if (UIUtil.isNotNullAndNotEmpty(strReviseBase)) {
				bRevise=Boolean.parseBoolean(StringUtil.split(strReviseBase, "=").get(1));
			}
			
			VPLMIntegTraceUtil.trace(context, ">>>>strImportedProdName::"+strImportedProdName+" strImportedProdRev::"+strImportedProdRev+" bRevise::"+bRevise);
			
			//call processing logic
			if(UIUtil.isNotNullAndNotEmpty(strImportedProdName) && UIUtil.isNotNullAndNotEmpty(strImportedProdRev) && 
					UIUtil.isNotNullAndNotEmpty(strBaseProdName) && UIUtil.isNotNullAndNotEmpty(strBaseProdRev)) {
				ImportIntoReference importintoRef=new ImportIntoReference();
				strReturnMessageBuf=importintoRef.importIntoVPMReference(context,strBaseProdName,strBaseProdRev,strImportedProdName,strImportedProdRev,bRevise);
				VPLMIntegTraceUtil.trace(context, ">>>> strReturnMessageBuf::"+strReturnMessageBuf.toString());
			}
		}catch(Exception ex) {
			return Response.serverError().entity(DataConstants.STR_ERROR + DataConstants.SEPARATOR_COLON+ex.toString()).build();	
		}
		return Response.status(200).entity(strReturnMessageBuf.toString()).build();
	}
}