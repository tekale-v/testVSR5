
package com.pg.designtools.integrations.ebom;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.engineering.PartFamily;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.pg.designtools.datamanagement.CommonProductData;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.EngineeringItem;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.util.ChangeManagement;
import com.pg.designtools.util.IPManagement;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.StringList;

/**
	 * @author GQS
	 * This class is an implementation of a Transient Job Handler 
	 * A Transient Job can be designed to handler one or more events for which specific updates can be 
	 * made to the Job Configuration attribute that maintains information that may span different client sessions.
	 * The only rule is that only one Transient Job can be managed at a given time.
	 *
	 */
	public class CollaborateEBOMJobFacility  {
	
		
		public CollaborateEBOMJobFacility() {
			super();
		}
		
		String strFreezeDesign = "";
    	String strTransferControl ="";
    	String strChangeManagement = "";
    	String strBackgroundJob = "";
    	String strReUseCACO = "";
    	String strExecSrv = "";
    	boolean bIsShapePart=false;
    	MapList objectList=new MapList();
    	StringBuilder strReturnMessageBuf = new StringBuilder();
    	StringList slECPartIdsForReUseCOCA= new StringList();
    	StringBuilder sbOptions= new StringBuilder();
    	StringBuilder sbNotificationMessage;
    	StringBuilder sbReturnMsgWithinCollaborateRevise;
		
    	/**
    	 * Main method invoked to perform the enhanced sync functionality.
    	 * @param context
    	 * @param strXMLDetails
    	 * @return StringBuilder message
    	 * @throws Exception
    	 */
		public StringBuilder collaborateWithProductData(Context context,String strXMLDetails) throws Exception{
			
			VPLMIntegTraceUtil.trace(context, "\n >>>> START of collaborateWithProductData method");
			strExecSrv="EBOM";
			String strReturn = parseInputXML(context,strXMLDetails);
			if(!DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strReturn)){
				processObjectsFromXML(context);
			}else {
				Map mpVPMRefInfo;
				for(int i=0;i<objectList.size();i++) {
    				
					mpVPMRefInfo=(Map)objectList.get(i);
					strReturnMessageBuf.append(DataConstants.STR_PART_SEPARATOR);
					strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    			strReturnMessageBuf.append(mpVPMRefInfo.get(DomainConstants.SELECT_NAME));
	    			strReturnMessageBuf.append(DataConstants.CONSTANT_STRING_SPACE);
	    			strReturnMessageBuf.append(mpVPMRefInfo.get(DomainConstants.SELECT_REVISION));
	    			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				}
				strReturnMessageBuf.append(DataConstants.STR_LINE_SEPARATOR);
				strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				strReturnMessageBuf.append(DataConstants.NO_ACTION_PERFORMED);
			}
			VPLMIntegTraceUtil.trace(context, "\n >>>> strReturnMessageBuf::"+strReturnMessageBuf);
			return strReturnMessageBuf;	
		}
		
		/**
		 * Method to parse the xml, received as input from CATIA
		 * @param context
		 * @param strXMLDetails
		 * @return String
		 * @throws IOException
		 * @throws ParserConfigurationException
		 * @throws SAXException
		 */
		private String parseInputXML(Context context,String strXMLDetails) throws  IOException, ParserConfigurationException, SAXException{ 				
			
			String strReturn=DataConstants.CONSTANT_TRUE;
			VPLMIntegTraceUtil.trace(context, "\n >>>> START of parseInputXML method");
			 DocumentBuilderFactory dbf =DocumentBuilderFactory.newInstance();
						 
			 DocumentBuilder db = dbf.newDocumentBuilder();
			
			 InputSource is = new InputSource();
			 is.setCharacterStream(new StringReader(strXMLDetails));
			  Document doc = db.parse(is);
	        
			  Element element ;
		  
			   if ("EBOM".equalsIgnoreCase(strExecSrv)) {
				   
				   NodeList optionNode = doc.getElementsByTagName("Options");
					
				   element =  (Element) optionNode.item(0);
				   NamedNodeMap options = element.getAttributes();
					   
				   for (int i = 0; i < options.getLength(); i++) {
					   if("FreezeDesign".equals(options.item(i).getNodeName()))
							   strFreezeDesign=options.item(i).getNodeValue();
					   else if("TransferControl".equals(options.item(i).getNodeName()))
						   strTransferControl=options.item(i).getNodeValue();
					   else if("ChangeManagement".equals(options.item(i).getNodeName()))
						   strChangeManagement=options.item(i).getNodeValue();
					   else if("BackgroundJob".equals(options.item(i).getNodeName()))
						   strBackgroundJob=options.item(i).getNodeValue();
					else if("ReUseCACO".equals(options.item(i).getNodeName()))
						strReUseCACO=options.item(i).getNodeValue();
				   }
	    	
	    	
			    	VPLMIntegTraceUtil.trace(context, "\n >>>> strFreezeDesign::"+strFreezeDesign);
			    	VPLMIntegTraceUtil.trace(context, "\n >>>> strTransferControl::"+strTransferControl);
			    	VPLMIntegTraceUtil.trace(context, "\n >>>> strChangeManagement::"+strChangeManagement);
			    	VPLMIntegTraceUtil.trace(context, "\n >>>> strBackgroundJob::"+strBackgroundJob);
				VPLMIntegTraceUtil.trace(context, "\n >>>> strReUseCACO::"+strReUseCACO);
	    	
			    	strReturnMessageBuf.append(DataConstants.STR_OPTIONS_SEPARATOR);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_FREEZE_DESIGN+DataConstants.SEPARATOR_COLON+strFreezeDesign);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_TRANSFER_CONTROL+DataConstants.SEPARATOR_COLON+strTransferControl);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_CHANGE_MANAGEMENT+DataConstants.SEPARATOR_COLON+strChangeManagement);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_BACKGROUND_JOB+DataConstants.SEPARATOR_COLON+strBackgroundJob);
			    	strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			    strReturnMessageBuf.append(DataConstants.CONSTANT_REUSE_COCA+DataConstants.SEPARATOR_COLON+strReUseCACO);
			    strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			    
			    sbOptions.append(strReturnMessageBuf);
					
			    	if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strFreezeDesign) && DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strTransferControl) && DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strChangeManagement)){
						strReturn=DataConstants.CONSTANT_FALSE;
					}
			    	//DTCLD-411 Start When Create CO/CA , ReUse CO/CA , Process in Background is selected then CO/CA does not get created .
			    	if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strFreezeDesign) && DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strTransferControl)
			    			&& DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strChangeManagement) &&
			    			DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strReUseCACO) && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strBackgroundJob)){
			    		strBackgroundJob=DataConstants.CONSTANT_FALSE;
					}	
			    	//DTCLD-411 End
			   }
					//object node processing
					NodeList objectsNode = doc.getElementsByTagName("Objects");
					element =  (Element) objectsNode.item(0);
					NodeList objects = element.getChildNodes();
					NamedNodeMap object;
					Map mpObjectInfo;
					   
					for (int i = 0; i < objects.getLength(); i++) {
						   object =objects.item(i).getAttributes();
						   mpObjectInfo=new HashMap();
						   
						   for (int j = 0;j < object.getLength(); j++) {
							   mpObjectInfo.put(object.item(j).getNodeName(),object.item(j).getNodeValue());
						   }
						   objectList.add(mpObjectInfo);
					   }
		
			  if(!("EBOM".equalsIgnoreCase(strExecSrv) || "ReviseUpstage".equalsIgnoreCase(strExecSrv))) {
				  strReturnMessageBuf.append(DataConstants.UNSUPPORTED_SERVICE);
			   }
		         VPLMIntegTraceUtil.trace(context, "\n >>>> objectList::"+objectList);
		         
			return strReturn;
	    }
		
		/**
		 * Method to do the processing on the VPMReference objects
		 * @param context
		 */
		private void processObjectsFromXML(Context context) {
			
			 VPLMIntegTraceUtil.trace(context, "\n >>>> START of processObjectsFromXML method");
			
			String strECPartId="";
			String strECPartName="";
			String strECPartRev="";
			String strECPartType="";
			String strECPartOwner="";
			
			Map mpECPartInfo ;
			Map mpVPMRefInfo;
			Map mpVPMRef;
			String strVPMReferenceName;
			String strVPMReferenceRev;
			String strVPMReferenceId ;
			String strVPMReferenceCurrent;
			String strEnterpriseType;
			String strCADDesignOrigination;
			IPManagement ipMgmt= new IPManagement(context);
			MapList mlVPMRefObject;
			
			for(int i=0;i<objectList.size();i++) {
	    				
				mpVPMRefInfo=(Map)objectList.get(i);
				strVPMReferenceName=(String)mpVPMRefInfo.get(DomainConstants.SELECT_NAME);
				strVPMReferenceRev=(String)mpVPMRefInfo.get(DomainConstants.SELECT_REVISION);
				
				strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				strReturnMessageBuf.append(DataConstants.STR_PART_SEPARATOR);
	    		strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
    			strReturnMessageBuf.append(strVPMReferenceName).append(DataConstants.CONSTANT_STRING_SPACE).append(strVPMReferenceRev);
    			
    			sbNotificationMessage = new StringBuilder();
    			sbNotificationMessage.append(DataConstants.CONSTANT_NEW_LINE);
    			sbNotificationMessage.append(DataConstants.STR_PART_SEPARATOR);
    			sbNotificationMessage.append(DataConstants.CONSTANT_NEW_LINE);
    			sbNotificationMessage.append(strVPMReferenceName).append(DataConstants.CONSTANT_STRING_SPACE).append(strVPMReferenceRev);
    					
	    		try {
		    		
		    		mlVPMRefObject = ipMgmt.findObject(context, DataConstants.TYPE_VPMREFERENCE,strVPMReferenceName,strVPMReferenceRev, getVPMReferenceSelectables());
		    	
		    		VPLMIntegTraceUtil.trace(context, "\n >>>> mlVPMRefObject::"+mlVPMRefObject);
		    		
		    		if(null!=mlVPMRefObject && !mlVPMRefObject.isEmpty()) {
		    			mpVPMRef = (Map)mlVPMRefObject.get(0);
		    			strVPMReferenceId = (String) mpVPMRef.get(DomainConstants.SELECT_ID);
		    			strVPMReferenceCurrent = (String) mpVPMRef.get(DomainConstants.SELECT_CURRENT);
		    			strCADDesignOrigination = (String)mpVPMRef.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
		    			strEnterpriseType=(String)mpVPMRef.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
		    	
		    			if(DataConstants.RANGE_VALUE_AUTOMATION.equalsIgnoreCase(strCADDesignOrigination)) {
		    				continue;
		    			}
		    			mpECPartInfo=getECPartInfoFromVPMReference(context, strVPMReferenceId);
		    			VPLMIntegTraceUtil.trace(context, "\n >>>> mpECPartInfo::"+mpECPartInfo);
		    			
		    			strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
		    			
		    			VPLMIntegTraceUtil.trace(context, "\n >>>> strECPartId::"+strECPartId);
		    			
      				    strECPartName=(String)mpECPartInfo.get(DomainConstants.SELECT_NAME);
		    			strECPartRev=(String)mpECPartInfo.get(DomainConstants.SELECT_REVISION);
		    			strECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
	    				 strECPartOwner=(String)mpECPartInfo.get(DomainConstants.SELECT_OWNER);
		    				 
		    			VPLMIntegTraceUtil.trace(context, "\n >>>>  strECPartName::"+strECPartName);
		    				
		    			if(DataConstants.TYPE_ASSEMBLED_PRODUCT_PART.equalsIgnoreCase(strECPartType) || 
		    				 DataConstants.STR_ASSEMBLED_PRODUCT_PART.equals(strEnterpriseType)) {
		    				continue;
		    			}
	    				
		 	    		String[] sArgCollaboration = new String[] {strVPMReferenceId,strECPartId,strFreezeDesign,strTransferControl,strChangeManagement,strECPartName,strECPartRev,strReUseCACO,strECPartType,strECPartOwner,strBackgroundJob,strVPMReferenceName,strVPMReferenceRev,sbOptions.toString(),sbNotificationMessage.toString(),strVPMReferenceCurrent}; 
		    			
	    				if("True".equalsIgnoreCase(strBackgroundJob)) {
		 		    		processBackgroundJob(context,strECPartId,strECPartName,strECPartRev,sArgCollaboration);
		 		    	}else {
		 		    		collaborateWithEnterprise (context,sArgCollaboration);
		 		    	}
    					
    					if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strReUseCACO) && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strChangeManagement) && UIUtil.isNotNullAndNotEmpty(strECPartId)) {	
    						slECPartIdsForReUseCOCA.add(strECPartId);	
    					}
		    		}else {
						strReturnMessageBuf.append(DataConstants.INCORRECT_VPMREFERENCE_PROVIDED_BY_CATIA);
		    		}
	    		}catch(Exception e) {
    			VPLMIntegTraceUtil.trace(context, "\n >>>> Inside Catch Block of processObjectsFromXML::");	
	    			//VPLMIntegTraceUtil.printStackTrace(context, e);
    			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
    			strReturnMessageBuf.append(DataConstants.STR_ERROR).append(DataConstants.CONSTANT_STRING_SPACE).append(e.getMessage());
	    		}
			}
		VPLMIntegTraceUtil.trace(context, "\n >>>> slECPartIdsForReUseCOCA::"+slECPartIdsForReUseCOCA);	
		try {
			if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strReUseCACO)) {	
				String strJobTitle = new StringBuilder("Collaborate with Enterprise ReUse ChangeManagement: ").toString();
				String[] sArgCMProcess = new String[] {slECPartIdsForReUseCOCA.toString()};
				initiateBackgroundJobExecutionForCollaborate(context,"",strJobTitle,sArgCMProcess,"backgroundJobExecutionForChangeManagementReUseCACO");
			}
		}
		catch(Exception e) {
			VPLMIntegTraceUtil.trace(context, "\n >>>> Inside Catch Block of processObjectsFromXML for ChangeManagement Reuse CO CA::");	
			//VPLMIntegTraceUtil.printStackTrace(context, e);
			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			strReturnMessageBuf.append(DataConstants.STR_ERROR).append(DataConstants.CONSTANT_STRING_SPACE).append(e.getMessage());
		}
		
		}
	    
		/**
		 * Method for background job processing
		 * @param context
		 * @param strECPartId
		 * @param strECPartName
		 * @param strECPartRev
		 * @param strVPMReferenceId
		 * @throws Exception
		 */
		private void processBackgroundJob(Context context,String strECPartId,String strECPartName,String strECPartRev,String [] sArgCollaboration) throws Exception  {
			
			VPLMIntegTraceUtil.trace(context, ">>>> START of processBackgroundJob method strECPartId "+strECPartId);
			
			if(UIUtil.isNullOrEmpty(strECPartId)) {
				String strVPMReferenceId = sArgCollaboration[0];
				String strVPMReferenceName = sArgCollaboration[11];
				String strVPMReferenceRev = sArgCollaboration[12];
				String strVPMReferenceCurrent = sArgCollaboration[15];
						
				EngineeringItem engItem = new EngineeringItem();
				String strResult= engItem.syncToEnterprise(context, strVPMReferenceId,strTransferControl);
				strResult=formatSyncResult(strResult);
				strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				strReturnMessageBuf.append(strResult);
				
				sbNotificationMessage.append(DataConstants.CONSTANT_NEW_LINE);
				sbNotificationMessage.append(strResult);
				
				 //get the details from VPMReference
				 Map mpECPartInfo=getECPartInfoFromVPMReference(context, strVPMReferenceId);
				 
				 VPLMIntegTraceUtil.trace(context,">>> Cloned data ECPartInfo::"+mpECPartInfo);
				 
				 strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
				 strECPartName=(String)mpECPartInfo.get(DomainConstants.SELECT_NAME);
				 strECPartRev=(String)mpECPartInfo.get(DomainConstants.SELECT_REVISION);
				 String strECPartType = (String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
				 String strECPartOwner = (String)mpECPartInfo.get(DomainConstants.SELECT_OWNER);
				 
				 //added for DTCLD-373
				 addAutomationInterface(context, DataConstants.CONSTANT_CATIA_APPLICATION, DataConstants.CONSTANT_COLLABORATE_EBOM_PROCESS, strECPartId);
				 
				 sArgCollaboration = new String[] {strVPMReferenceId,strECPartId,strFreezeDesign,strTransferControl,strChangeManagement,strECPartName,strECPartRev,strReUseCACO,strECPartType,strECPartOwner,strBackgroundJob,strVPMReferenceName,strVPMReferenceRev,sbOptions.toString(),sbNotificationMessage.toString(),strVPMReferenceCurrent}; 
				 if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strReUseCACO) && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strChangeManagement) && UIUtil.isNotNullAndNotEmpty(strECPartId)) {	
						slECPartIdsForReUseCOCA.add(strECPartId);	
				 }
			}
			
			CommonUtility commonUtility = new CommonUtility();
			//Check if previous Background Job operation is still in progress
			String strJobTitle = new StringBuilder("Collaborate with Enterprise : ").append(strECPartName).append(DataConstants.CONSTANT_STRING_SPACE).append(strECPartRev).toString();							
			String strIsBackgroundJobRunning = commonUtility.checkRunningBackGroundJobConnected(context, strECPartId, strJobTitle);
			
			VPLMIntegTraceUtil.trace(context, ">>>> strJobTitle::"+strJobTitle+" strIsBackgroundJobRunning::"+strIsBackgroundJobRunning);
			
			if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strIsBackgroundJobRunning))
			{
				initiateBackgroundJobExecutionForCollaborate(context,strECPartId,strJobTitle,sArgCollaboration,"backgroundJobExecutionForCollaborate");
			}else {
				 strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    		 strReturnMessageBuf.append(strIsBackgroundJobRunning);
			}
		}
		
		/**
		 * Method to initiate background processing
		 * @param context
		 * @param strECPartId
		 * @param strJobTitle
		 * @param strVPMReferenceId
		 * @throws Exception
		 */
		private void initiateBackgroundJobExecutionForCollaborate(Context context , String strECPartId,String strJobTitle,String []sArgCollaboration,String strMethodName) throws Exception {

			VPLMIntegTraceUtil.trace(context, ">>>> START of initiateBackgroundJobExecutionForCollaborate method");
			
			Job bgJob = new Job("com.pg.designtools.integrations.ebom.CollaborateEBOMJobFacility", strMethodName, sArgCollaboration, false);
			bgJob.setTitle(strJobTitle);
			bgJob.setActionOnCompletion("Delete");
			bgJob.setNotifyOwner("Yes");
			bgJob.createAndSubmit(context);
			bgJob.setStartDate(context);
			
			StringList slObjectSelect = new StringList(2);
			slObjectSelect.add(DomainConstants.SELECT_ID);
			slObjectSelect.add(DomainConstants.SELECT_NAME);
			
			Map mapJobInfo = bgJob.getInfo(context, slObjectSelect);
			String strJobId = (String)mapJobInfo.get(DomainConstants.SELECT_ID);
			String strJobName = (String)mapJobInfo.get(DomainConstants.SELECT_NAME);
			VPLMIntegTraceUtil.trace(context, ">>>> initiateBackgroundJobExecutionForCollaborate strJobName "+strJobName);
			VPLMIntegTraceUtil.trace(context, ">>>> initiateBackgroundJobExecutionForCollaborate strJobId "+strJobId);
			
			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			strReturnMessageBuf.append("Job Name : "+strJobName);
			
			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			if("backgroundJobExecutionForChangeManagementReUseCACO".equalsIgnoreCase(strMethodName)) {
				
				strReturnMessageBuf.append(DataConstants.JOB_SUBMITTED_SUCCESS_FOR_REUSE_COCA);
			}else {
				strReturnMessageBuf.append(DataConstants.JOB_SUBMITTED_SUCCESS);
			}
					
			//Connect Background Job to EC Part by Pending Job connection
			if(UIUtil.isNotNullAndNotEmpty(strECPartId)) {
				bgJob.addFromObject(context, new RelationshipType(DataConstants.RELATIONSHIP_PENDING_JOB), strECPartId);
			}
			
		}
		
		/**
		 * Background Job method for collaborate with Enterprise
		 * @param context
		 * @param args
		 * @return
		 * @throws Exception 
		 */
		public void backgroundJobExecutionForCollaborate(Context context, String[] args) throws Exception
		{						
			VPLMIntegTraceUtil.trace(context, ">>>> START of backgroundJobExecutionForCollaborate method");
			collaborateWithEnterprise (context,args);					
		}
		
		/**
		 * Method to actual perform the actions as per operations selected
		 * @param context
		 * @param sArgCollaborateEP
		 * @throws Exception 
		 */
		private void collaborateWithEnterprise(Context context,String [] sArgCollaborateEP) throws Exception {
			VPLMIntegTraceUtil.trace(context, ">>>> START of collaborateWithEnterprise method");
			
			String transferControl = "no";
	    	EngineeringItem engItem = new EngineeringItem();
	    	Boolean bError = false;
	    	String strExceptionMessage = "";
	    	String sBackGroundJob = "";
	    	StringBuilder sbNotificationMsgWithinJobExecution = new StringBuilder();
			try{
				String sVPMReferenceId = sArgCollaborateEP[0];
				String sECPartId = sArgCollaborateEP[1];
				String sFreezeDesign =  sArgCollaborateEP[2];
				String sTransferControl =  sArgCollaborateEP[3];
				String sChangeManagement = sArgCollaborateEP[4];
				String sECPartName=sArgCollaborateEP[5];
				String sECPartRev=sArgCollaborateEP[6];
				String sReUseCACO=sArgCollaborateEP[7];
				sBackGroundJob=sArgCollaborateEP[10];
				String sVPMReferenceCurrent = sArgCollaborateEP[15];
				String  sECPartType="";
				
				String strResult="";
				boolean bClonedData=false;
				
				VPLMIntegTraceUtil.trace(context, ">>sVPMReferenceId::"+sVPMReferenceId+" sECPartId::"+sECPartId);
				VPLMIntegTraceUtil.trace(context, ">>sFreezeDesign::"+sFreezeDesign+" sTransferControl::"+sTransferControl+" sChangeManagement::"+sChangeManagement);
				VPLMIntegTraceUtil.trace(context, ">>sECPartName::"+sECPartName+" sECPartRev::"+sECPartRev);
				
				if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sTransferControl)) {
					transferControl = "give";
				}
						 
				 //for the cloned/duplicated object
				 if(UIUtil.isNullOrEmpty(sECPartId)) {
					 bClonedData=true;
					 strResult=transferControlOperation(context,sVPMReferenceId,transferControl,engItem);
					 
					strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
					strReturnMessageBuf.append(strResult);
						
					sbNotificationMsgWithinJobExecution.append(DataConstants.CONSTANT_NEW_LINE);
					sbNotificationMsgWithinJobExecution.append(strResult);
						
					  //get the details from VPMReference
					 Map mpECPartInfo=getECPartInfoFromVPMReference(context, sVPMReferenceId);
					 
					 VPLMIntegTraceUtil.trace(context,">>> Cloned data ECPartInfo::"+mpECPartInfo);
					 
					 sECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
					 sECPartName=(String)mpECPartInfo.get(DomainConstants.SELECT_NAME);
					 sECPartRev=(String)mpECPartInfo.get(DomainConstants.SELECT_REVISION);
					 sECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
					 
					 //added for DTCLD-373
					 addAutomationInterface(context, DataConstants.CONSTANT_CATIA_APPLICATION, DataConstants.CONSTANT_COLLABORATE_EBOM_PROCESS, sECPartId);
					//get the current state of VPMReference
					 DomainObject doVPMReference=DomainObject.newInstance(context,sVPMReferenceId);
					 sVPMReferenceCurrent=doVPMReference.getInfo(context, DomainConstants.SELECT_CURRENT);
					 
					 VPLMIntegTraceUtil.trace(context,">>> Cloned data sVPMReferenceCurrent::"+sVPMReferenceCurrent);
					 
					 slECPartIdsForReUseCOCA.add(sECPartId);	
					 
				 }
				 
				VPLMIntegTraceUtil.trace(context,">>bClonedData::"+bClonedData);
					
				 strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	 	    	 strReturnMessageBuf.append(sECPartName).append(DataConstants.CONSTANT_STRING_SPACE).append(sECPartRev);
				 
	 	    	 sbNotificationMsgWithinJobExecution.append(DataConstants.CONSTANT_NEW_LINE);
	 	    	 sbNotificationMsgWithinJobExecution.append(sECPartName).append(DataConstants.CONSTANT_STRING_SPACE).append(sECPartRev);
				 	
				//logic to update the mandatory attributes on EC Part
				DomainObject doECPart=DomainObject.newInstance(context,sECPartId);
				BusinessObject busPreviousRevObj = doECPart.getPreviousRevision(context);
				if(UIUtil.isNullOrEmpty(sECPartType))
					sECPartType=doECPart.getInfo(context, DomainConstants.SELECT_TYPE);
				
				VPLMIntegTraceUtil.trace(context,">>sECPartType::"+sECPartType);
				VPLMIntegTraceUtil.trace(context,">>previous revision exists::"+busPreviousRevObj.exists(context));
				
				//DTWPI-123: update the mandatory attributes only for first revision
				if(!DataConstants.TYPE_SHAPE_PART.equals(sECPartType) && !busPreviousRevObj.exists(context) && 
						(DataConstants.STATE_IN_WORK.equalsIgnoreCase(sVPMReferenceCurrent) || DataConstants.STATE_PRIVATE.equalsIgnoreCase(sVPMReferenceCurrent))) {
					updateMandatoryAttributesOnECPart(context,sVPMReferenceId,sECPartId);
				}
				
				if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sFreezeDesign)) {
					
					if(DataConstants.STATE_IN_WORK.equalsIgnoreCase(sVPMReferenceCurrent)) {
					strResult=engItem.freezeVPMReference(context, sVPMReferenceId);
					}else if(DataConstants.STATE_PRIVATE.equalsIgnoreCase(sVPMReferenceCurrent)) {
						strResult=engItem.promoteVPMReferenceFromPrivateToFrozen(context, sVPMReferenceId);
					}
					
					if(DataConstants.STATE_IN_WORK.equalsIgnoreCase(sVPMReferenceCurrent) || DataConstants.STATE_PRIVATE.equalsIgnoreCase(sVPMReferenceCurrent)) {
						VPLMIntegTraceUtil.trace(context,">>strResult "+strResult);
					if(UIUtil.isNotNullAndNotEmpty(strResult)) {
						strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
						strReturnMessageBuf.append(DataConstants.FREEZE_DESIGN_SUCCESS);
					
					sbNotificationMsgWithinJobExecution.append(DataConstants.CONSTANT_NEW_LINE);
					sbNotificationMsgWithinJobExecution.append(DataConstants.FREEZE_DESIGN_SUCCESS);
					}
					}
					else if(!bClonedData){
						strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    				strReturnMessageBuf.append(DataConstants.OBJECT_IN_FROZEN_OR_BEYOND);
	    				
	    				
	    				sbNotificationMsgWithinJobExecution.append(DataConstants.CONSTANT_NEW_LINE);
	    				sbNotificationMsgWithinJobExecution.append(DataConstants.OBJECT_IN_FROZEN_OR_BEYOND);
					}
				}
				
				if(!bClonedData && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sTransferControl)){
					 strResult=transferControlOperation(context,sVPMReferenceId,transferControl,engItem);
					 
					strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
					strReturnMessageBuf.append(strResult);
							
					sbNotificationMsgWithinJobExecution.append(DataConstants.CONSTANT_NEW_LINE);
					sbNotificationMsgWithinJobExecution.append(strResult);
				}
					
				if((bClonedData && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sTransferControl))) {
					strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
					strReturnMessageBuf.append(DataConstants.FREEZE_DESIGN_SUCCESS);
					
					sbNotificationMsgWithinJobExecution.append(DataConstants.CONSTANT_NEW_LINE);
					sbNotificationMsgWithinJobExecution.append(DataConstants.FREEZE_DESIGN_SUCCESS);
				}
				
				if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sChangeManagement) && !DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sReUseCACO)) {
					
					ChangeManagement changemanagement = new ChangeManagement();
					StringList partIdList = new StringList();
					partIdList.add(sECPartId);
					
					strResult=changemanagement.createChangeObjects(context, partIdList);

					if(DataConstants.STR_SUCCESS.equals(strResult)) {
						strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
						strReturnMessageBuf.append(DataConstants.CHANGE_MANAGEMENT_SUCCESS);
						
						sbNotificationMsgWithinJobExecution.append(DataConstants.CONSTANT_NEW_LINE);
						sbNotificationMsgWithinJobExecution.append(DataConstants.CHANGE_MANAGEMENT_SUCCESS);
					}
				}
			}catch(Exception e){
				VPLMIntegTraceUtil.trace(context, "\n >>>> Inside Catch Block of collaborateWithEnterprise::");	
				//VPLMIntegTraceUtil.printStackTrace(context, e);
				strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				strReturnMessageBuf.append(DataConstants.STR_ERROR).append(DataConstants.CONSTANT_STRING_SPACE).append(e.getMessage());
				bError = true;
				strExceptionMessage = e.getMessage();
			}finally {
				if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sBackGroundJob)) {
					sendBackgroundJobNotificationForCollaboarteWithEnterprise(context, sArgCollaborateEP, sbNotificationMsgWithinJobExecution,bError,strExceptionMessage);	
				}
			}
		}
		
		/**
		 * Method to perform the operations for Transfer Control 
		 * @param context
		 * @param sVPMReferenceId
		 * @param transferControl
		 * @param engItem
		 * @return strResult
		 * @throws MatrixException
		 */
		private String transferControlOperation(Context context, String sVPMReferenceId, String transferControl, EngineeringItem engItem) throws MatrixException {
			String strResult="";
			DomainObject doObj=DomainObject.newInstance(context,sVPMReferenceId);
			StringList slSelects=new StringList(2);
			slSelects.add("to["+DataConstants.REL_VPM_INSTANCE+"]");
			slSelects.add("from["+DataConstants.REL_VPM_INSTANCE+"]");
			Map mpAssemblyInfo=doObj.getInfo(context,slSelects);
			
			String sChildPresent=(String) mpAssemblyInfo.get("to["+DataConstants.REL_VPM_INSTANCE+"]");
			String sParentPresent=(String) mpAssemblyInfo.get("from["+DataConstants.REL_VPM_INSTANCE+"]");
			VPLMIntegTraceUtil.trace(context, ">>sChildPresent::"+sChildPresent+" sParentPresent::"+sParentPresent);
			
			if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sChildPresent) || 
								DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sParentPresent)) {
				 strResult= engItem.syncToEnterprise(context, sVPMReferenceId,transferControl,"0");
			}else {
				strResult= engItem.syncToEnterprise(context, sVPMReferenceId,transferControl);
			}
			 strResult=formatSyncResult(strResult);
			 return strResult;
		}

		/**
		 * This method is used for sending Notifications when BackgroundJob is set as True while Collaborate with EP
		 * @param context
		 * @param sArgCollaborateEP
		 * @param sbNotificationMsgWithinJobExecution
		 * @param bError
		 * @param strExceptionMessage
		 * @throws Exception
		 */
		public void sendBackgroundJobNotificationForCollaboarteWithEnterprise(Context context, String [] sArgCollaborateEP, StringBuilder sbNotificationMsgWithinJobExecution,Boolean bError,String strExceptionMessage) throws Exception 
		{
			VPLMIntegTraceUtil.trace(context, ">>>> sendBackgroundJobNotificationForCollaboarteWithEnterprise Start ");
			String sECPartId = sArgCollaborateEP[1];
			String sECPartName=sArgCollaborateEP[5];
			String sECPartRev=sArgCollaborateEP[6];
			String sECPartType=sArgCollaborateEP[8];
			String sECPartOwner=sArgCollaborateEP[9];
			String sVPMReferenceName=sArgCollaborateEP[11];
			String sVPMReferenceRev=sArgCollaborateEP[12];
			String sOptions=sArgCollaborateEP[13];
			String sNotificationMessageBeforeJObCreation=sArgCollaborateEP[14];
			
	        Locale localeMessage = MessageUtil.getLocale(context);

			String sEmailSubject;
			//Email Message
			String sEnerprisePart = new StringBuilder(sECPartName).append(DataConstants.CONSTANT_DOT).append(sECPartRev).toString();
			//Get Base URL
			String sBaseURL = JPO.invoke(context, "emxMailUtil", null, "getBaseURL", new String[0], String.class);
			sEnerprisePart = new StringBuilder("<a href=").append(sBaseURL).append("?objectId=").append(sECPartId).append(">").append(sEnerprisePart).append("</a>").toString();
			
			StringBuilder sbEmailMessage = new StringBuilder();
			sbEmailMessage.append("</br>");
			
			String strMessageSubject ="";
			String strMessage="";
			if(Boolean.TRUE.equals(bError)) {
				strMessageSubject = "emxCPN.CollaborateWithEP.BackgroundJob.FailureMail.Subject";
				strMessage = "emxCPN.CollaborateWithEP.BackgroundJob.FailureMail.Message";
			}else {
				strMessageSubject = "emxCPN.CollaborateWithEP.BackgroundJob.SuccessMail.Subject";
				strMessage = "emxCPN.CollaborateWithEP.BackgroundJob.SuccessMail.Message";
			}
			
			sEmailSubject = MessageUtil.getMessage(context, null, strMessageSubject ,new String[] {sVPMReferenceName,sVPMReferenceRev,sECPartType, sECPartName, sECPartRev}, null, localeMessage, DataConstants.CONSTANT_EMX_CPN_STRING_RESOURCE);
			
			sbEmailMessage.append(MessageUtil.getMessage(context, null, strMessage ,new String[] {sVPMReferenceName,sVPMReferenceRev,sECPartType, sECPartName, sECPartRev}, null, localeMessage, DataConstants.CONSTANT_EMX_CPN_STRING_RESOURCE));
			
			sbEmailMessage.append("</br></br>");
			sbEmailMessage.append("<style>table#t01 {width:100%; border: 1px solid black; border-collapse: collapse;}table#t01 tr:nth-child(even) { background-color: #eee;}table#t01 tr:nth-child(odd) {background-color:#fff;}table#t01 th {background-color: #006699; color: white; border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;}table#t01 td {border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;}</style>");
			sbEmailMessage.append("<table id='t01'><tr><th>");
			sbEmailMessage.append(DataConstants.CONSTANT_PART_INFORMATION).append("</th><th>");
			sbEmailMessage.append(DataConstants.CONSTANT_COLLABORATION_STATUS).append("</th></tr><tr><td>");
			sbEmailMessage.append(sEnerprisePart).append("</td><td>");
			sbEmailMessage.append(sOptions);
			sbEmailMessage.append(sNotificationMessageBeforeJObCreation);
			sbEmailMessage.append("</br>");
			sbEmailMessage.append(sbNotificationMsgWithinJobExecution);
			if(UIUtil.isNotNullAndNotEmpty(strExceptionMessage)) {
				sbEmailMessage.append("</br>");
				sbEmailMessage.append(strExceptionMessage);
		}
			sbEmailMessage.append("</td></tr></table>");	
			
			StringList slToList = new StringList();
			slToList.add(sECPartOwner);
			// Define message details
			Map mapArgs = new HashMap();
			mapArgs.put("subject", sEmailSubject);
			mapArgs.put("message", sbEmailMessage.toString());
			mapArgs.put("objectId", sECPartId);
			mapArgs.put("toList", slToList);

			try 
			{
				JPO.invoke(context, "pgDSOCATIAIntegration", null, "sendEmailNotification", JPO.packArgs(mapArgs));
			} 
			catch (Exception e) 
			{
				VPLMIntegTraceUtil.trace(context, "\n >>>> Inside Catch Block of sendBackgroundJobNotificationForCollaboarteWithEnterprise::");	
				//VPLMIntegTraceUtil.printStackTrace(context, e);
				strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				strReturnMessageBuf.append(DataConstants.STR_ERROR).append(DataConstants.CONSTANT_STRING_SPACE).append(e.getMessage());
			}
		}
		
		/**
		 * This method creates Change Management Objects for all the Parts when ReUse CO/CA is True while Collaborate with EP
		 * @param context
		 * @param sArgCMProcess
		 * @throws FrameworkException
		 */
        public void backgroundJobExecutionForChangeManagementReUseCACO (Context context,String [] sArgCMProcess) throws FrameworkException {
			ChangeManagement changemanagement = new ChangeManagement();
			String  strPartIdsForReUseCOCA = sArgCMProcess[0];
			strPartIdsForReUseCOCA=strPartIdsForReUseCOCA.replace("[","");
			strPartIdsForReUseCOCA=strPartIdsForReUseCOCA.replace("]","");
			StringList slPartIdsForReUseCOCA = StringUtil.split(strPartIdsForReUseCOCA, ",");
			String strResult=changemanagement.createChangeObjects(context, slPartIdsForReUseCOCA);
			VPLMIntegTraceUtil.trace(context,">>backgroundJobExecutionForChangeManagementReUseCACO strResult:: "+strResult);		
		}

		/**
		 * Method to update the mandatory attributes on EC Part.
		 * This method would also connect the picklist items to EC Part
		 * @param context
		 * @param strVPMReferenceId
		 * @param strECPartId
		 * @throws MatrixException  
		 */
		public void updateMandatoryAttributesOnECPart(Context context, String strVPMReferenceId, String strECPartId) throws MatrixException {
			
			
			VPLMIntegTraceUtil.trace(context, ">>>> START of updateMandatoryAttributesOnECPart method");
			
			if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceId) && UIUtil.isNotNullAndNotEmpty(strECPartId)) {
				DomainObject doVPMRef=DomainObject.newInstance(context,strVPMReferenceId);
				
				boolean bPackaging=false;
				boolean bProduct=false;
				StringList slSelects=new StringList();
				// get all the interfaces of the object
				BusinessInterfaceList busInterfaces = doVPMRef.getBusinessInterfaces(context);
				
				if(busInterfaces.toString().contains(DataConstants.INTERFACE_PNG_PACKAGING)) {
					bPackaging=true;
					slSelects=getObjectSelectsForInterface(DataConstants.INTERFACE_PNG_PACKAGING);
				}else if(busInterfaces.toString().contains(DataConstants.INTERFACE_PNG_PRODUCT)) {
					bProduct=true;
					slSelects=getObjectSelectsForInterface(DataConstants.INTERFACE_PNG_PRODUCT);
				}
				
				slSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.from["+DataConstants.REL_PRIMARY_ORGANIZATION+"].to.id");
				
				VPLMIntegTraceUtil.trace(context, ">>>> bPackaging::"+bPackaging+" bProduct::"+bProduct);
				
				if(bPackaging || bProduct) {
					Map mpVPMRefInfo=doVPMRef.getInfo(context,slSelects);
				
					VPLMIntegTraceUtil.trace(context, ">>>> mpVPMRefInfo::"+mpVPMRefInfo);
					
					setMandatoryAttributes(context,mpVPMRefInfo,strECPartId,bPackaging,bProduct);
					
					connectPickListObjectsToECPart(context,mpVPMRefInfo,strECPartId,bPackaging,bProduct);
				}
			}
		}

		/**
		 * Method to set the mandatory attributes on EC Part
		 * @param context
		 * @param mpVPMRefInfo
		 * @param strECPartId
		 * @param bPackaging
		 * @param bProduct
		 * @throws FrameworkException 
		 */
		private void setMandatoryAttributes(Context context, Map mpVPMRefInfo, String strECPartId,boolean bPackaging,boolean bProduct) throws FrameworkException {
		
			VPLMIntegTraceUtil.trace(context, ">>>>START of setMandatoryAttributes method");
			
			Map mpAttrMap=new HashMap();
			DomainObject doECPart=DomainObject.newInstance(context,strECPartId);
			
			if(bPackaging) {
				mpAttrMap.put(DataConstants.ATTRIBUTE_CLASS, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_CLASS));
				mpAttrMap.put(DataConstants.ATTRIBUTE_SEGMENT, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_SEGMENT));
				mpAttrMap.put(DataConstants.ATTRIBUTE_RELEASE_CRITERIA, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_RELEASE_CRITERIA));
				mpAttrMap.put(DataConstants.ATTRIBUTE_MATERIAL_TYPE, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_MATERIAL_TYPE));
				mpAttrMap.put(DataConstants.ATTRIBUTE_LIFECYCLE_STATUS, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_MFG_STATUS));
			}
			else if(bProduct) {
				mpAttrMap.put(DataConstants.ATTRIBUTE_SEGMENT, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PRODUCT_SEGMENT));
				mpAttrMap.put(DataConstants.ATTRIBUTE_RELEASE_CRITERIA, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PRODUCT_RELEASE_CRITERIA));
				mpAttrMap.put(DataConstants.ATTRIBUTE_LIFECYCLE_STATUS, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PRODUCT_MFG_STATUS));
			}
		
			VPLMIntegTraceUtil.trace(context, ">>>>mpAttrMap::"+mpAttrMap);
			
			try {
			doECPart.setAttributeValues(context, mpAttrMap);
			}catch(Exception e) {
				StringList slOwnerModificationAccessList = new StringList();
				slOwnerModificationAccessList.add(DataConstants.CONSTANT_ACCESS_MODIFY);
				boolean hasAccess = FrameworkUtil.hasAccess(context, doECPart,slOwnerModificationAccessList);
				VPLMIntegTraceUtil.trace(context, ">>>>setMandatoryAttributes hasAccess::"+hasAccess);
				if (!hasAccess) {
					throw new FrameworkException(DataConstants.CONSTANT_NO_MODIFY_ACCESS_ON_EC_PART_CHECK_VPMCONTROL);
				}
			}
		
			VPLMIntegTraceUtil.trace(context, ">>>>Mandatory Attributes set on EC Part");
		}

		/**
		 * Method to get the selectables for VPMReference as per the interface added on the object
		 * @param strInterfaceName
		 * @return StringList
		 */
		private StringList getObjectSelectsForInterface(String strInterfaceName) {
			StringList slSelects=new StringList();
			
			if(DataConstants.INTERFACE_PNG_PACKAGING.equals(strInterfaceName)) {
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PACKAGING_SEGMENT);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PACKAGING_REPORTED_FUNCTION);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PACKAGING_CLASS);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PACKAGING_MATERIAL_TYPE);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PACKAGING_MFG_STATUS);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PACKAGING_RELEASE_CRITERIA);
			}
			else if(DataConstants.INTERFACE_PNG_PRODUCT.equals(strInterfaceName)) {
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PRODUCT_MFG_STATUS);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PRODUCT_RELEASE_CRITERIA);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PRODUCT_SEGMENT);
			}
			return slSelects;
		}
		
		/**
		 * Method to connect the pick list objects of type Segment, Reported Function and Primary Organization to EC Part
		 * @param context
		 * @param mpVPMRefInfo
		 * @param strECPartId
		 * @param bPackaging
		 * @param bProduct
		 * @throws MatrixException
		 */
		private void connectPickListObjectsToECPart(Context context, Map mpVPMRefInfo, String strECPartId,boolean bPackaging,boolean bProduct) throws MatrixException{

			VPLMIntegTraceUtil.trace(context,">>> START of connectPickListObjectsToECPart method");
			VPLMIntegTraceUtil.trace(context,">>> mpVPMRefInfo::"+mpVPMRefInfo);
		
			IPManagement ipMgmt=new IPManagement(context);
			
			String strSegment="";
			String strPackagingReportedFunction="";
			String strPrimaryOrg="";
			boolean bSegmentToBeUpdated=false;
			boolean bReportedFunctionToBeUpdated=false;
			
			Map mpConnectedPicklistObjects=getConnectedPicklistObjectsFromECPart(context,strECPartId);
			VPLMIntegTraceUtil.trace(context,">>> mpConnectedPicklistObjects::"+mpConnectedPicklistObjects);
			
			if(bPackaging) {
				strSegment=(String)mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_SEGMENT);
				strPackagingReportedFunction=(String)mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_REPORTED_FUNCTION);
				strPrimaryOrg=(String)mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION);
				
				bSegmentToBeUpdated=verifyPicklistValueIsUpdated(strSegment,(String)mpConnectedPicklistObjects.get(DataConstants.CONST_PICKLIST_SEGMENT));
				bReportedFunctionToBeUpdated=verifyPicklistValueIsUpdated(strPackagingReportedFunction,(String)mpConnectedPicklistObjects.get(DataConstants.CONST_PICKLIST_REPORTEDFUNCTION));
			}else if(bProduct) {
				strSegment=(String)mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PRODUCT_SEGMENT);
				strPrimaryOrg=(String)mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION);
				
				bSegmentToBeUpdated=verifyPicklistValueIsUpdated(strSegment,(String)mpConnectedPicklistObjects.get(DataConstants.CONST_PICKLIST_SEGMENT));
			}
			
			VPLMIntegTraceUtil.trace(context,"bSegmentToBeUpdated::"+bSegmentToBeUpdated);
			VPLMIntegTraceUtil.trace(context,"bReportedFunctionToBeUpdated::"+bReportedFunctionToBeUpdated);
			
	
			String strType="";
			String strName="";
			String strRelationship="";
			String strAttributeName="";
			MapList mlObject;
			Map mpObject=new HashMap();
			HashMap programMap=new HashMap();
			HashMap paramMap=new HashMap();
			
			VPLMIntegTraceUtil.trace(context,"\n strSegment::"+strSegment);
			VPLMIntegTraceUtil.trace(context,"\n strPrimaryOrg::"+strPrimaryOrg);
			VPLMIntegTraceUtil.trace(context,"\n strPackagingReportedFunction::"+strPackagingReportedFunction);
			
		
			
			for(int i=0;i<3;i++) {
				if(i==0) {
					if(UIUtil.isNotNullAndNotEmpty(strSegment) && bSegmentToBeUpdated) {
						strType=DataConstants.CONST_PICKLIST_SEGMENT;
						strName=strSegment;
						strRelationship=DataConstants.CONST_REL_PDTEMPLATES_TO_PGPLISEGMENT;
					}
				}else if(i==1) {
					if(UIUtil.isNotNullAndNotEmpty(strPackagingReportedFunction) && bReportedFunctionToBeUpdated) {
						strType=DataConstants.CONST_PICKLIST_REPORTEDFUNCTION;
						strName=strPackagingReportedFunction;
						strRelationship=DataConstants.CONST_REL_PDTEMPLATES_TO_PGPLIREPORTEDFUNCTION;
						strAttributeName="ReportedFunction";
					}
				}else {
					if(UIUtil.isNotNullAndNotEmpty(strPrimaryOrg)) {
						strType=DataConstants.CONST_PICKLIST_ORGCHANGEMGMT;
						strName=strPrimaryOrg;
					}
				}
				
				VPLMIntegTraceUtil.trace(context,"\n strType::"+strType+" strName::"+strName+" strRelationship::"+strRelationship+" strAttributeName::"+strAttributeName);
				if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strName)) {
			
					mlObject=ipMgmt.findObject(context, strType, strName, DataConstants.SEPARATOR_STAR, 	new StringList(DomainConstants.SELECT_ID));
					if(null!=mlObject && !mlObject.isEmpty())
						mpObject=(Map) mlObject.get(0);
					VPLMIntegTraceUtil.trace(context,"\n mpObject::"+mpObject);
					
					if(DataConstants.CONST_PICKLIST_ORGCHANGEMGMT.equals(strType)) {
						
						paramMap.put("New OID", mpObject.get(DomainConstants.SELECT_ID));
						paramMap.put("objectId", strECPartId);
						paramMap.put("Old OID", mpVPMRefInfo.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.from["+DataConstants.REL_PRIMARY_ORGANIZATION+"].to.id"));
			    		programMap.put("paramMap",paramMap);
			    		
			    		VPLMIntegTraceUtil.trace(context,"\n programMap::"+programMap);
			    		
			    		JPO.invoke(context,"pgDSOCPNProductData", null, "disconnectAndConnectPrimaryOrganizationToProductData", JPO.packArgs(programMap));
					}else {
						String[] arrData=new String[1];
						arrData[0]=(String) mpObject.get(DomainConstants.SELECT_ID);
						
						paramMap.put("New Value", mpObject.get(DomainConstants.SELECT_ID));
						paramMap.put("New Values",arrData);
						paramMap.put("objectId", strECPartId);
						
						HashMap settingMap=new HashMap();
						settingMap.put("pgPicklistType", strType);
						settingMap.put("RelationshipName", strRelationship);
						settingMap.put("AttributeName", strAttributeName);
						HashMap fieldMap=new HashMap();
						fieldMap.put("settings", settingMap);
						programMap.put("paramMap",paramMap);
						programMap.put("fieldMap",fieldMap);
						
						VPLMIntegTraceUtil.trace(context,"\n programMap::"+programMap);
						
						JPO.invoke(context,"emxCPNProductData", null, "connectPickListObject", JPO.packArgs(programMap));
					}
				}
			}
		}
		
		/**
		 * Method to verify whether the picklist value is modified 
		 * @param strVPMRefValue
		 * @param strECPartValue
		 * @return boolean
		 */
    	private boolean verifyPicklistValueIsUpdated(String strVPMRefValue, String strECPartValue) {
			boolean bIsUpdated=false;
			//Added null or empty check for strECPartValue for ALM 51601 DTCLD-444
			if(UIUtil.isNullOrEmpty(strECPartValue) || (UIUtil.isNotNullAndNotEmpty(strVPMRefValue) && UIUtil.isNotNullAndNotEmpty(strECPartValue)
					&& !strVPMRefValue.equalsIgnoreCase(strECPartValue)))
				bIsUpdated=true;
			return bIsUpdated;
		}

    	/**
    	 * Method to get the connected picklist object info from EC Part
    	 * @param context
    	 * @param strECPartId
    	 * @return Map
    	 * @throws FrameworkException
    	 */
		private Map getConnectedPicklistObjectsFromECPart(Context context, String strECPartId) throws FrameworkException {
			
			VPLMIntegTraceUtil.trace(context, "START of getConnectedPicklistObjectsFromECPart method");
    		Map mpConnectedPicklistObjects=new HashMap();
    		DomainObject doECPart=DomainObject.newInstance(context,strECPartId);
    		
    		StringList slSelects=new StringList(2);
    		slSelects.add("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT+"].to.name");
    		slSelects.add("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION+"].to.name");
    		Map mpResult=doECPart.getInfo(context, slSelects);
    		
    		mpConnectedPicklistObjects.put(DataConstants.CONST_PICKLIST_SEGMENT, mpResult.get("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT+"].to.name"));
    		mpConnectedPicklistObjects.put(DataConstants.CONST_PICKLIST_REPORTEDFUNCTION, mpResult.get("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION+"].to.name"));
    		
    		VPLMIntegTraceUtil.trace(context, "mpConnectedPicklistObjects::"+mpConnectedPicklistObjects);
			return mpConnectedPicklistObjects;
		}

		/**
    	 * Main method invoked to perform the enhanced revise upstage functionality.
    	 * @param context
    	 * @param strXMLDetails
    	 * @return StringBuilder message
    	 * @throws Exception
    	 */
		public StringBuilder executeRevGoToStage(Context context,String strXMLDetails) throws Exception{
			
			VPLMIntegTraceUtil.trace(context, "\n >>>> START of executeRevGoToStage method");
			
			strExecSrv="ReviseUpstage";
			
			//see DTWPI-10 for XML format that leverages our existing Enhanced Sync format
			String strReturn = parseInputXML(context,strXMLDetails);
			if(!DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strReturn)){
				processObjectsFromXMLForReviseUpstage(context);
			}
			VPLMIntegTraceUtil.trace(context, "\n >>>> strReturnMessageBuf::"+strReturnMessageBuf);
			return strReturnMessageBuf;	
		}		
		
		/**
		 * Method to do the processing on the VPMReference objects for revise and upstage
		 * @param context
		 */
		public Map processObjectsFromXMLForReviseUpstage(Context context) {
			
			 VPLMIntegTraceUtil.trace(context, "\n >>>> START of processObjectsFromXMLForReviseUpstage method");
			
			
			String strECPartId="";
			String strECPartType="";
			String strECPartName="";
			String strECPartRev="";
			String strRevisedVPMReference="";
			String strECPartState="";
			
			Map mpECPartInfo ;
			Map mpVPMRefXMLInfo;
			Map mpVPMRef;
			String strVPMReferenceName;
			String strVPMReferenceRev;
			String strVPMReferenceId ;
			String strCADDesignOrigination;
			String strVPMReferenceReleasePhase;
			String strNewReleasePhase;
			String strVPMReferenceCurrent ;
			IPManagement ipMgmt= new IPManagement(context);
			MapList mlVPMRefObject;
			boolean bNotValidCondition=false;
			
			for(int i=0;i<objectList.size();i++) {
	    				
				bNotValidCondition=false;
				bIsShapePart=false;
				mpVPMRefXMLInfo=(Map)objectList.get(i);
				strVPMReferenceName=(String)mpVPMRefXMLInfo.get(DomainConstants.SELECT_NAME);
				strVPMReferenceRev=(String)mpVPMRefXMLInfo.get(DomainConstants.SELECT_REVISION);
				strVPMReferenceReleasePhase=(String)mpVPMRefXMLInfo.get("currentPhase");
				strNewReleasePhase=(String)mpVPMRefXMLInfo.get("newPhase");
				
				strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				strReturnMessageBuf.append(DataConstants.STR_PART_SEPARATOR);
	    		strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
    			strReturnMessageBuf.append(strVPMReferenceName).append(DataConstants.CONSTANT_STRING_SPACE).append(strVPMReferenceRev);
    			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
    			strReturnMessageBuf.append("Release Phase").append(DataConstants.CONSTANT_STRING_SPACE).append(strVPMReferenceReleasePhase);
    			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
    			strReturnMessageBuf.append("New Release Phase").append(DataConstants.CONSTANT_STRING_SPACE).append(strNewReleasePhase);
    			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    		try {
		    		
		    		mlVPMRefObject = ipMgmt.findObject(context, DataConstants.TYPE_VPMREFERENCE,strVPMReferenceName,strVPMReferenceRev, getVPMReferenceSelectables());	
		    		VPLMIntegTraceUtil.trace(context, "\n >>>> mlVPMRefObject::"+mlVPMRefObject);
		    		
		    		if(null!=mlVPMRefObject && !mlVPMRefObject.isEmpty()) {
		    			mpVPMRef = (Map)mlVPMRefObject.get(0);
		    			strVPMReferenceId = (String) mpVPMRef.get(DomainConstants.SELECT_ID);
		    			strVPMReferenceCurrent = (String) mpVPMRef.get(DomainConstants.SELECT_CURRENT);
		    			
		    			strCADDesignOrigination=(String)mpVPMRef.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
		    			if(DataConstants.RANGE_VALUE_AUTOMATION.equalsIgnoreCase(strCADDesignOrigination)) {
		    				bNotValidCondition=true;
		    			}
		    			
		    			//Upstage for Shape Part is not allowed
		    		
	    				String strEnterpriseType=(String)mpVPMRef.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
	    				VPLMIntegTraceUtil.trace(context, "\n >>>>  strEnterpriseType::"+strEnterpriseType);
		    			mpECPartInfo=getECPartInfoFromVPMReference(context, strVPMReferenceId);
		    			VPLMIntegTraceUtil.trace(context, "\n >>>> mpECPartInfo::"+mpECPartInfo);
		    			
		    			strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
		    			VPLMIntegTraceUtil.trace(context, "\n >>>> strECPartId::"+strECPartId);
		    			
		    			 strECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
		    			 VPLMIntegTraceUtil.trace(context, "\n >>>>  strECPartType::"+strECPartType);
		    			 
		    			 strECPartState=(String)mpECPartInfo.get(DomainConstants.SELECT_CURRENT);
		    			 VPLMIntegTraceUtil.trace(context, "\n >>>>  strECPartState::"+strECPartState);
		    				 
		    			 if(DataConstants.TYPE_ASSEMBLED_PRODUCT_PART.equalsIgnoreCase(strECPartType) || 
		    					 DataConstants.STR_ASSEMBLED_PRODUCT_PART.equals(strEnterpriseType)) {
	    					 bNotValidCondition=true;
	    				 }
		    			
		    			 if(DataConstants.TYPE_SHAPE_PART.equalsIgnoreCase(strECPartType) || 
	    						 DataConstants.VALID_ENT_TYPE_FOR_EXPLORATION.equalsIgnoreCase(strEnterpriseType)) {
	    					 bIsShapePart=true;
	    				 }
		    			 VPLMIntegTraceUtil.trace(context, "\n >>>>  bIsShapePart::"+bIsShapePart);
		    			 
		    			 if(bIsShapePart && !strNewReleasePhase.equals(strVPMReferenceReleasePhase)) {
	    					 strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    					 strReturnMessageBuf.append(DataConstants.SHAPE_PART_UPSTAGE_NOT_ALLOWED);
	    					 bNotValidCondition=true;
	    				 }
		    			 
		     			 if((DataConstants.RELEASE_PHASE_PRODUCTION.equalsIgnoreCase(strVPMReferenceReleasePhase) &&  
		     					 !strNewReleasePhase.equals(strVPMReferenceReleasePhase)) || 
		     					 (DataConstants.RELEASE_PHASE_PILOT.equalsIgnoreCase(strVPMReferenceReleasePhase) && 
		     							 DataConstants.RELEASE_PHASE_DEVELOPMENT.equalsIgnoreCase(strNewReleasePhase) ) ) {
	    					 strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    					 strReturnMessageBuf.append(DataConstants.LOWER_TARGET_PHASE_NOT_ALLOWED);
	    					 bNotValidCondition=true;
	    				 }
		    			 
		     			 if(DataConstants.STATE_OBSOLETE.equalsIgnoreCase(strECPartState)) {
		     				 strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    					 strReturnMessageBuf.append(DataConstants.CANNOT_REVISE_EC_PART_IN_OBSOLETE);
	    					 bNotValidCondition=true;
		     			 }
		     				    			
		     			 if(!DataConstants.STATE_RELEASED.equalsIgnoreCase(strVPMReferenceCurrent) && (DataConstants.RELEASE_PHASE_PILOT.equalsIgnoreCase(strVPMReferenceReleasePhase) || DataConstants.RELEASE_PHASE_PRODUCTION.equalsIgnoreCase(strVPMReferenceReleasePhase))) {
		     				 
		     				 strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
	    					 strReturnMessageBuf.append(DataConstants.CONSTANT_REVISE_NOT_POSSIBLE_WHEN_CHANGE_CONTROL);
	    					 bNotValidCondition=true;
		     			 }
		     				 		
		     			 if(bNotValidCondition) {
	    					 continue;
		     			 }
		     			 
		    			if(UIUtil.isNotNullAndNotEmpty(strECPartId)) {
		    				 strECPartName=(String)mpECPartInfo.get(DomainConstants.SELECT_NAME);
		    				 strECPartRev=(String)mpECPartInfo.get(DomainConstants.SELECT_REVISION);
		    				
		    				 strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
		 	    			 strReturnMessageBuf.append(strECPartName).append(DataConstants.CONSTANT_STRING_SPACE).append(strECPartRev);
		    			}	    			
		 	    		 strRevisedVPMReference=collaborateWithEnterpriseRevGoTo(context, mpVPMRef,mpVPMRefXMLInfo,mpECPartInfo);
		       		}else {
						strReturnMessageBuf.append(DataConstants.INCORRECT_VPMREFERENCE_PROVIDED_BY_CATIA);
		    		}
	    		}catch(Exception e) {
	    			 VPLMIntegTraceUtil.trace(context, "\n >>>>  processObjectsFromXMLForReviseUpstage Catch Block::");
	    		}
			}
			Map mpFinalReturn=new HashMap();
			mpFinalReturn.put("RevisedVPMReferenceId", strRevisedVPMReference);
			mpFinalReturn.put("message", strReturnMessageBuf.toString());
			return mpFinalReturn;
		}		
		
		/**
		 * Method to get the ECPart selectables
		 * @return StringList
		 */
		public StringList getECPartSelectables() {
			StringList slECPartObjSelects=new StringList();
			
			slECPartObjSelects.add(DomainConstants.SELECT_ID);
			slECPartObjSelects.add(DomainConstants.SELECT_NAME);
			slECPartObjSelects.add(DomainConstants.SELECT_REVISION);
			slECPartObjSelects.add(DomainConstants.SELECT_TYPE);
			slECPartObjSelects.add(DomainConstants.SELECT_POLICY);
			slECPartObjSelects.add(DomainConstants.SELECT_CURRENT);
			slECPartObjSelects.add(DomainConstants.SELECT_VAULT);
			slECPartObjSelects.add(DomainConstants.SELECT_OWNER);
			slECPartObjSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			slECPartObjSelects.add(DomainConstants.SELECT_DESCRIPTION);
			slECPartObjSelects.add("from["+DataConstants.REL_PRIMARY_ORGANIZATION+"].to.id");
			slECPartObjSelects.add("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT+"].to.id");

			return slECPartObjSelects;
		}
		
		/**
		 * Method to get the VPMReference selectables
		 * @return StringList
		 */
		private StringList getVPMReferenceSelectables()
		{
			StringList slVPMReferenceObjSelects=new StringList();
			slVPMReferenceObjSelects.add(DomainConstants.SELECT_ID);
			slVPMReferenceObjSelects.add(DomainConstants.SELECT_CURRENT);
			slVPMReferenceObjSelects.add(DomainConstants.SELECT_NAME);
			slVPMReferenceObjSelects.add(DomainConstants.SELECT_REVISION);
			slVPMReferenceObjSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
			slVPMReferenceObjSelects.add(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
			slVPMReferenceObjSelects.add(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);	
			
			return slVPMReferenceObjSelects;
		}
		
		/**
		 * Method to perform revise/upstage
		 * @param context
		 * @param Map VPMRefInfo
		 * @param Map XMLInfo
		 * @param Map ECPartInfo
		 * @throws MatrixException
		 */
		private String collaborateWithEnterpriseRevGoTo(Context context,Map mpVPMRef, Map mpVPMRefXMLInfo,Map mpECPartInfo) throws Exception {
			VPLMIntegTraceUtil.trace(context, ">>>> START of collaborateWithEnterpriseRevGoTo method");
			
	    	EngineeringItem engItem = new EngineeringItem();
	    	boolean bCtxPushed = false;
	    	String strRevisedVPMReference=DomainConstants.EMPTY_STRING;
			sbReturnMsgWithinCollaborateRevise = new StringBuilder();
			DomainObject doVPMReference;
			try{
				ContextUtil.startTransaction(context, true);
				String strNewRevisionObjId = null;
				String strECPartId;
				String strOperation = "";
			  	String strVPMReferenceId=(String)mpVPMRef.get(DomainConstants.SELECT_ID);
			  	String strCloneDerivedFrom=(String)mpVPMRef.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
			  	
				String strVPMReferenceReleasePhase=(String)mpVPMRefXMLInfo.get("currentPhase");
				String strNewReleasePhase=(String)mpVPMRefXMLInfo.get("newPhase");
				
				//transfer the control to Enovia
				boolean bClonedData=false;
				boolean bNotLatestRevision=false;
				String transferControl="give";
				if(UIUtil.isNotNullAndNotEmpty(strCloneDerivedFrom)) {
					bClonedData=true;
				}
			
				VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo bClonedData::"+bClonedData);
				VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo transferControl::"+transferControl);
				
				if(bClonedData) {
					syncToEnterprise(context,engItem,strVPMReferenceId,transferControl);
					//get the info from ECPart and add it to mpECPartInfo
			
					mpECPartInfo=getECPartInfoFromVPMReference(context, strVPMReferenceId);
	    		
	    			VPLMIntegTraceUtil.trace(context, "\n >>>> mpECPartInfo  for cloned data::"+mpECPartInfo);
	    			
	    			//update the mandatory attributes on the new EC Part
	    			strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
	    			 //added for DTCLD-373
	    			addAutomationInterface(context,DataConstants.CONSTANT_CATIA_APPLICATION, DataConstants.CONSTANT_COLLABORATE_EBOM_PROCESS, strECPartId);
	    			try{
	    				updateMandatoryAttributesOnECPart(context, strVPMReferenceId, strECPartId);
	    			}catch(Exception e) {
	    				VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch updateMandatoryAttributesOnECPart:::"+e.getMessage());
	    				throw e;
	    			}
				}

				strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
				String strECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
				String strECPartPolicy=(String)mpECPartInfo.get(DomainConstants.SELECT_POLICY);
				String strECPartState=(String)mpECPartInfo.get(DomainConstants.SELECT_CURRENT);
				String strECPartName=(String)mpECPartInfo.get(DomainConstants.SELECT_NAME);
				String strECPartRev=(String)mpECPartInfo.get(DomainConstants.SELECT_REVISION);
				DomainObject doObject=DomainObject.newInstance(context,strECPartId);
				
				boolean bLastRevision=doObject.isLastRevision(context);
				VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo bLastRevision:::"+bLastRevision);
				
				if(bLastRevision || (!bLastRevision && DataConstants.RELEASE_PHASE_DEVELOPMENT.equalsIgnoreCase(strVPMReferenceReleasePhase))) {
			
					//to enable leap frog for Development phase, need to get the details of the latest revision
					
					if(!bLastRevision) {
						//get the latest revision details
						BusinessObject boLastRevision=doObject.getLastRevision(context);
						VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo boLastRevision:::"+boLastRevision);
						
						if(boLastRevision.exists(context)){
							doObject=DomainObject.newInstance(context,boLastRevision);
							strVPMReferenceId=doObject.getInfo(context, "from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].to["+
							DataConstants.TYPE_VPMREFERENCE+"].id");
							VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo strVPMReferenceId of latest revision:::"+strVPMReferenceId);
							
							mpECPartInfo=getECPartInfoFromVPMReference(context, strVPMReferenceId);
							VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo mpECPartInfo for latest revision:::"+mpECPartInfo);
							
							strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
							strECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
							strECPartPolicy=(String)mpECPartInfo.get(DomainConstants.SELECT_POLICY);
							strECPartState=(String)mpECPartInfo.get(DomainConstants.SELECT_CURRENT);
							strECPartName=(String)mpECPartInfo.get(DomainConstants.SELECT_NAME);
							strECPartRev=(String)mpECPartInfo.get(DomainConstants.SELECT_REVISION);
							try {
							VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo before calling syncToEnterprise*** ");
							syncToEnterprise(context,engItem,strVPMReferenceId,transferControl);
							VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo after calling syncToEnterprise*** ");
							}catch(Exception e) {
			    				VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch syncToEnterprise11:::"+e.getMessage());
			    				throw e;
			    			}
						}
					}else if(!bClonedData) {
						syncToEnterprise(context,engItem,strVPMReferenceId,transferControl);
					}
					
					try {
						VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo before calling releasePreviousRevision*** ");
						releasePreviousRevision(context, strECPartId, strECPartType, strVPMReferenceReleasePhase);
						VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo after calling releasePreviousRevision*** ");
					}catch(Exception e) {
						VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo inside calling catch releasePreviousRevision "+e.getMessage());
						addExceptionMessage(e.getMessage(),"Release of previous revision");
						throw e;
					}
					
					//move the current EC Part to release state
					if(!bIsShapePart && !DataConstants.STATE_OBSOLETE.equals(strECPartState) && !DataConstants.STATE_RELEASE.equals(strECPartState)) {
							//If upstage operation is executed, then release the selected object.
						// If Development phase and revise operation, then we do not want to release the selected object
						if(!DataConstants.RELEASE_PHASE_DEVELOPMENT.equalsIgnoreCase(strVPMReferenceReleasePhase) || !strVPMReferenceReleasePhase.equals(strNewReleasePhase)) {
						
						try {
							VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo before calling setState");
								doObject.setState(context, DataConstants.STATE_RELEASE);
								VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo after calling setState");
								sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
								sbReturnMsgWithinCollaborateRevise.append("Released ").append(strECPartName);
								sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_STRING_SPACE);
								sbReturnMsgWithinCollaborateRevise.append(strECPartRev);
								VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo Released current object");
						}catch(Exception e) {
							VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo inside catch setState to Release "+e.getMessage());
								addExceptionMessage(e.getMessage(),"Release of "+strECPartName+" "+strECPartRev);
								throw e;
						}
					}
					}
					
					doVPMReference=DomainObject.newInstance(context,strVPMReferenceId);
					String strCurrentControl=doVPMReference.getInfo(context, DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
					VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo strCurrentControl:::"+strCurrentControl);
					
					if(strCurrentControl.equalsIgnoreCase(DataConstants.CONSTANT_TRUE)) {
						try {
						CommonProductData cpd=new CommonProductData();
						cpd.transferControlToEnterprise(context, doVPMReference);
						VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo Control is given back to Enovia");
						}catch(Exception e) {
		    				VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch transferControlToEnterprise:::"+e.getMessage());
		    				throw e;
		    			}
					}
					
					if(strVPMReferenceReleasePhase.equals(strNewReleasePhase)) {
					
						mpECPartInfo=getECPartInfoFromVPMReference(context, strVPMReferenceId);
						
						strNewRevisionObjId=reviseProductData(context,doObject,mpECPartInfo);
						//added for DTCLD-373
						strOperation="Revision";
						
						VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo strNewRevisionObjId "+strNewRevisionObjId);
						if(bIsShapePart) {
							String[] hmArgs=new String[3];
							hmArgs[0]=strECPartId;
							hmArgs[1]=strECPartType;
							hmArgs[2]=strNewRevisionObjId;
							JPO.invoke(context,"pgDSOCATIAIntegration",null,"autoSyncEnterprisePartToNewCATIAVersion",hmArgs,String.class);
							
						}
						if(UIUtil.isNotNullAndNotEmpty(strNewRevisionObjId)) {
							sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
							sbReturnMsgWithinCollaborateRevise.append(DataConstants.REVISION_SUCCESS);
							 //added for DTCLD-373
							addAutomationInterface(context,DataConstants.CONSTANT_CATIA_APPLICATION,DataConstants.CONSTANT_REVISE_PROCESS,strNewRevisionObjId);
						}
					}else {
						// exec upstage DTWPI-29
						strNewRevisionObjId=upstageProductData(context,mpECPartInfo,strNewReleasePhase);
						//added for DTCLD-373
						strOperation="Upstage";
					}
				}else {
					sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
					sbReturnMsgWithinCollaborateRevise.append(DataConstants.STR_ERROR_EC_PART_REV_NOT_LATEST);
					bNotLatestRevision=true;
				}
				VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo new revision id ::"+strNewRevisionObjId);
				
				//transfer control back to CATIA
				
				if(!bNotLatestRevision) {
					try {
					VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo before calling syncToDesignForCurrentECPart ");
					syncToDesignForCurrentECPart(context,engItem,strECPartId,transferControl,bNotLatestRevision);
					VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo after calling syncToDesignForCurrentECPart ");
				}
					catch(Exception e) {
						VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch calling syncToDesignForCurrentECPart :::"+e.getMessage());
						throw e;
					}
				}
				
				strRevisedVPMReference=postProcessOnRevisedObject(context,strNewRevisionObjId,transferControl,engItem);
				VPLMIntegTraceUtil.trace(context,"\n >>> collaborateWithEnterpriseRevGoTo strRevisedVPMReference:::"+strRevisedVPMReference +" strOperation:::"+strOperation);
				
				 //START: added for DTCLD-373
				if(UIUtil.isNotNullAndNotEmpty(strRevisedVPMReference)) {
	 	    		if("Revision".equals(strOperation)) {
						addAutomationInterface(context,DataConstants.CONSTANT_CATIA_APPLICATION,DataConstants.CONSTANT_REVISE_PROCESS,strRevisedVPMReference);
	 	    		}else {
						addAutomationInterface(context,DataConstants.CONSTANT_CATIA_APPLICATION,DataConstants.CONSTANT_UPSTAGE_PROCESS,strRevisedVPMReference);
	 	    		}
						//END: added for DTCLD-373
				}
				//set CADDesignOrigination as Manual
				doVPMReference=DomainObject.newInstance(context,strVPMReferenceId);
				String strVPMReferenceState=doVPMReference.getInfo(context, DomainConstants.SELECT_CURRENT);
				VPLMIntegTraceUtil.trace(context,"\n >>> collaborateWithEnterpriseRevGoTo state of selected VPMReference:::"+strVPMReferenceState);
			
				//current user does not have access to modify attribute, since object would be in Released state. Hence pushContext is used
				ContextUtil.pushContext(context);
				bCtxPushed = true;
				VPLMIntegTraceUtil.trace(context,"\n >>> collaborateWithEnterpriseRevGoTo context is pushed to User Agent");
				try {
				doVPMReference.setAttributeValue(context, DataConstants.ATTRIBUTE_CAD_DESIGN_ORIGINATION, DataConstants.RANGE_VALUE_MANUAL);
				VPLMIntegTraceUtil.trace(context,"\n >>> collaborateWithEnterpriseRevGoTo Set CADDesignOrigination as Manual for existing VPMReference");
				}catch(Exception e) {
					VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch doVPMReference.setAttributeValue :::"+e.getMessage());
					throw e;
				}
				
			}catch(Exception e){
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo Inside CATCH Block "+e.getMessage());
				if(ContextUtil.isTransactionActive(context)) {
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo Transaction active in catch block");
					ContextUtil.abortTransaction(context);	
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo Transaction aborted");
				}
				
			}finally {
				VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo Inside finally ");
				strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
				strReturnMessageBuf.append(sbReturnMsgWithinCollaborateRevise);	
				
				if(ContextUtil.isTransactionActive(context)) {
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo Transaction active in finally block");
					try {
					ContextUtil.commitTransaction(context);
					}catch(Exception r) {
						VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo commitTransaction block "+r.getMessage());
					}
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>collaborateWithEnterpriseRevGoTo Transaction committed");
				}
				if(bCtxPushed) {
					ContextUtil.popContext(context);
				}
			}
			return strRevisedVPMReference;
		}

		/**
		 * Method to upstage EC Part
		 * @param context
		 * @param mpECPartInfo
		 * @param strNewReleasePhase
		 * @return objectid of upstaged object
		 * @throws Exception
		 */
		public String upstageProductData(Context context, Map mpECPartInfo, String strNewReleasePhase) throws Exception {
			TimeZone tz=TimeZone.getTimeZone(context.getSession().getTimezone());
			int tzoff = tz.getRawOffset();
			Double timezone = ((double)tzoff /3600000);
			
			String strNewRevisionObjId="";
			String strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
			String strECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
			String strECPartPolicy=(String)mpECPartInfo.get(DomainConstants.SELECT_POLICY);
			
			HashMap programMap = new HashMap();
			programMap.put("parentObjectId",strECPartId);
			programMap.put("newPolicy",DataConstants.SCHEMA_POLICY_EC_PART);
			programMap.put("newStage",strNewReleasePhase);
			programMap.put("newTemplateId",CommonUtility.getTemplateObjectId(context, strECPartType, strECPartPolicy));
			programMap.put("timeZone", timezone);
			
			VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo upstage programMap::"+programMap);
		try {
			strNewRevisionObjId = JPO.invoke(context,"emxCPNProductDataPartStage",null,"postProcessGoToStage",JPO.packArgs(programMap),String.class);
		}catch(Exception e) {
			VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch calling emxCPNProductDataPartStage postProcessGoToStage:::"+e.getMessage());
			throw e;
		}
			if(UIUtil.isNotNullAndNotEmpty(strNewRevisionObjId)) {
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.UPSTAGE_SUCCESS);
				
				//added for DTCLD-373
				addAutomationInterface(context,DataConstants.CONSTANT_CATIA_APPLICATION,DataConstants.CONSTANT_UPSTAGE_PROCESS,strNewRevisionObjId);
				
				try {
				updateConnectionForPartFamily(context,strNewRevisionObjId,strNewReleasePhase);
				}catch(Exception e) {
					VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch calling updateConnectionForPartFamily :::"+e.getMessage());
					throw e;
				}
			}
			return strNewRevisionObjId;
		}

		/**
		 * Method for post process activities on the revised VPMReference object
		 * @param context
		 * @param strNewRevisionObjId
		 * @param transferControl
		 * @param engItem
		 * @return object id of revised VPMReference
		 * @throws Exception
		 */
		private String postProcessOnRevisedObject(Context context, String strNewRevisionObjId, String transferControl, EngineeringItem engItem) throws Exception {
			VPLMIntegTraceUtil.trace(context, ">> START of postProcessOnRevisedObject method");
			String strRevisedVPMReference="";
			if(UIUtil.isNotNullAndNotEmpty(strNewRevisionObjId)) {
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append("New Revision is:: ");
				
				DomainObject doNewRevision=DomainObject.newInstance(context,strNewRevisionObjId);
				StringList slSelects=getECPartSelectables();
				slSelects.add("from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");
				
				Map mpNewRevision=doNewRevision.getInfo(context, slSelects);
				VPLMIntegTraceUtil.trace(context, ">>>> collaborateWithEnterpriseRevGoTo mpNewRevision ::"+mpNewRevision);
				
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append("ECPart_Name::");
				sbReturnMsgWithinCollaborateRevise.append(mpNewRevision.get(DomainConstants.SELECT_NAME));
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append("ECPart_Revision::");
				sbReturnMsgWithinCollaborateRevise.append(mpNewRevision.get(DomainConstants.SELECT_REVISION));
				
				//set CADDesignOrigination as Manual
				strRevisedVPMReference=(String) mpNewRevision.get("from["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");
				VPLMIntegTraceUtil.trace(context,"\n >>> collaborateWithEnterpriseRevGoTo strRevisedVPMReference:::"+strRevisedVPMReference);
				if(UIUtil.isNotNullAndNotEmpty(strRevisedVPMReference)) {
					DomainObject doRevisedVPMReference=DomainObject.newInstance(context,strRevisedVPMReference);
					
					Map mpRevisedVPMReferenceInfo=doRevisedVPMReference.getInfo(context, getVPMReferenceSelectables());
					sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
					sbReturnMsgWithinCollaborateRevise.append("VPMReference_Name::");
					sbReturnMsgWithinCollaborateRevise.append(mpRevisedVPMReferenceInfo.get(DomainConstants.SELECT_NAME));
					sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
					sbReturnMsgWithinCollaborateRevise.append("VPMReference_Revision::");
					sbReturnMsgWithinCollaborateRevise.append(mpRevisedVPMReferenceInfo.get(DomainConstants.SELECT_REVISION));
					try {
					doRevisedVPMReference.setAttributeValue(context, DataConstants.ATTRIBUTE_CAD_DESIGN_ORIGINATION, DataConstants.RANGE_VALUE_MANUAL);
					VPLMIntegTraceUtil.trace(context,"\n >>> collaborateWithEnterpriseRevGoTo Set CADDesignOrigination as Manual for revised VPMReference");
					}catch(Exception e) {
						VPLMIntegTraceUtil.trace(context, ">>>collaborateWithEnterpriseRevGoTo calling inside catch doRevisedVPMReference.setAttributeValue :::"+e.getMessage());
						throw e;
					}
				}
				syncToDesign(context,engItem,strNewRevisionObjId,transferControl);
			}
			return strRevisedVPMReference;
		}

		/**
		 * Method to revise the EC Part
		 * @param context
		 * @param doObject
		 * @param mpECPartInfo
		 * @return strRevisedObjectId;
		 * @throws MatrixException
		 */
		public String reviseProductData(Context context,DomainObject doObject,Map mpECPartInfo) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">> START of reviseProductData method");
			HashMap programMap = new HashMap();
			String strECPartVault=(String)mpECPartInfo.get(DomainConstants.SELECT_VAULT);
			String strPrimaryOrgId=(String)mpECPartInfo.get("from["+DataConstants.REL_PRIMARY_ORGANIZATION+"].to.id");
			String strSegmentId=(String)mpECPartInfo.get("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT+"].to.id");
			String strECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
			String strECPartPolicy=(String)mpECPartInfo.get(DomainConstants.SELECT_POLICY);
			String strECPartId=(String)mpECPartInfo.get(DomainConstants.SELECT_ID);
			
			programMap.put("specType", FrameworkUtil.getAliasForAdmin(context, "type",strECPartType, true));
			programMap.put(DomainConstants.SELECT_POLICY, strECPartPolicy);
			programMap.put("strSourceProdDataId", strECPartId);
			programMap.put(DomainConstants.SELECT_REVISION,doObject.getNextSequence(context));
			programMap.put("vaultName",strECPartVault);
			programMap.put("rdoId", strPrimaryOrgId);
				
			//create Map for relatedObjList
			Map mpRelatedObjList=new HashMap();
			mpRelatedObjList.put("BU", strPrimaryOrgId);
			mpRelatedObjList.put("Segment", strSegmentId);
			
			programMap.put("relatedObjList", mpRelatedObjList);
			
			//create Map for attrList
			Map mpAttrList=new HashMap();
			mpAttrList.put(DomainConstants.SELECT_OWNER, mpECPartInfo.get(DomainConstants.SELECT_OWNER));
			mpAttrList.put(DomainConstants.SELECT_DESCRIPTION, mpECPartInfo.get(DomainConstants.SELECT_DESCRIPTION));
			
			Map mpAttributesMap=new HashMap();
			mpAttributesMap.put("Title", mpECPartInfo.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				
			mpAttrList.put("AttributesMap", mpAttributesMap);
			
			programMap.put("attributeList", mpAttrList);
				
			VPLMIntegTraceUtil.trace(context, ">>>> revise programMap::"+programMap);
			
			PropertyUtil.setGlobalRPEValue(context,"REVISE_IPM_EBOM", "true");
			PropertyUtil.setGlobalRPEValue(context,"REVISE_IPM_DSM", "true");
			
			return JPO.invoke(context, "enoGLSFormulationProductData", null, "reviseProductData", JPO.packArgs(programMap), String.class);
			
		}

		/**
		 * Method to sync to Enterprise
		 * @param context
		 * @param engItem
		 * @param strVPMReferenceId
		 * @param transferControl
		 */
		private void syncToEnterprise(Context context,EngineeringItem engItem, String strVPMReferenceId, String transferControl) throws Exception{
			try {
				String strResult= engItem.syncToEnterprise(context, strVPMReferenceId,transferControl);
				strResult=formatSyncResult(strResult);
				VPLMIntegTraceUtil.trace(context, ">>>> syncToEnterprise strResult::"+strResult);
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append(strResult);
			}catch(Exception e) {
				VPLMIntegTraceUtil.trace(context, ">>>> Inside catch block of syncToEnterprise "+e.getMessage());
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append("Exception while Sync To Enterprise ");
				throw e;
			}
		}

		/**
		 * Method to sync to Design for current EC Part
		 * @param context
		 * @param engItem
		 * @param strECPartId
		 * @param transferControl
		 * @param bNotLatestRevision
		 */
		private void syncToDesignForCurrentECPart(Context context,EngineeringItem engItem, String strECPartId, String transferControl,boolean bNotLatestRevision) throws Exception{
			try {
				VPLMIntegTraceUtil.trace(context, ">>>>START of syncToDesignForCurrentECPart method");
				VPLMIntegTraceUtil.trace(context, ">>>>user::"+context.getUser());
				String strResult=engItem.syncToDesign(context, strECPartId,transferControl);
				strResult=formatSyncResult(strResult);
				if(bNotLatestRevision) {
					sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
					sbReturnMsgWithinCollaborateRevise.append(strResult);
				}
				VPLMIntegTraceUtil.trace(context, ">>>> Control given back to Design for current EC Part");
			}catch(Exception e) {
				VPLMIntegTraceUtil.trace(context, ">>>> Inside catch block of syncToDesign for current EC Part "+e.getMessage());
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append("Exception while Sync To Design for current EC Part ");
				throw e;
			}
			VPLMIntegTraceUtil.trace(context, "<<<< END of syncToDesignForCurrentECPart method");
		}
		
		/**
		 * Method to sync to revised EC Part
		 * @param context
		 * @param engItem
		 * @param strECPartId
		 * @param transferControl
		 */
		private void syncToDesign(Context context,EngineeringItem engItem, String strECPartId, String transferControl) throws Exception{
			try {
				VPLMIntegTraceUtil.trace(context, ">>>>START of syncToDesign method");
				VPLMIntegTraceUtil.trace(context, ">>>>user::"+context.getUser());
				String strResult=engItem.syncToDesign(context, strECPartId,transferControl);
				strResult=formatSyncResult(strResult);
				VPLMIntegTraceUtil.trace(context, ">>>> Control given back to Design for revised EC Part");
				VPLMIntegTraceUtil.trace(context, ">>> Sync to Design result:::"+strResult);
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append(strResult);
			}catch(Exception e) {
				VPLMIntegTraceUtil.trace(context, ">>>> Inside catch block of syncToDesign for revised EC Part "+e.getMessage());
				sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
				sbReturnMsgWithinCollaborateRevise.append("Exception while Sync To Design for revised EC Part ");	
				throw e;
			}
			VPLMIntegTraceUtil.trace(context, "<<<< END of syncToDesign method");
		}
		
		/**
		 * Method to remove the brackets from sync result
		 * @param strResult
		 * @return String
		 */
		private String formatSyncResult(String strResult) {
			strResult=strResult.replace("[","");
			strResult=strResult.replace("]","");
			return strResult;
		}

		/**
		 * This method would handle connection with Part Family as part of upstage process
		 * @param context
		 * @param strNewRevisionObjId
		 * @param strNewReleasePhase
		 * @throws Exception
		 */
		private void updateConnectionForPartFamily(Context context, String strNewRevisionObjId,String strNewReleasePhase) throws Exception {
			VPLMIntegTraceUtil.trace(context, ">>> START of updateConnectionForPartFamily method");
			if(UIUtil.isNotNullAndNotEmpty(strNewRevisionObjId))
			{ 					
				DomainObject doRevisedECPart = DomainObject.newInstance(context,strNewRevisionObjId);
				
				StringList slSelects=new StringList();
				slSelects.add("interface["+DataConstants.INTERFACE_PART_FAMILY_REFERENCE+"]");
				slSelects.add("attribute["+DataConstants.ATTRIBUTE_REFERENCE_TYPE+"]");
				slSelects.add("to["+ DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM +"].frommid["+ 
				DataConstants.REL_PART_FAMILY_REFERENCE+"].torel.to.attribute["+DataConstants.ATTR_RELEASE_PHASE+"]");
				slSelects.add("to["+ DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM +"].from[" + DomainConstants.TYPE_PART_FAMILY +"].id");
				
				Map mpInfo=doRevisedECPart.getInfo(context, slSelects);
				VPLMIntegTraceUtil.trace(context, ">>> mpInfo:::"+mpInfo);
				String partSeriesON=(String)mpInfo.get("interface["+ DataConstants.INTERFACE_PART_FAMILY_REFERENCE+"]");
				
				if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(partSeriesON))
				{
					String sRefType = (String)mpInfo.get("attribute["+DataConstants.ATTRIBUTE_REFERENCE_TYPE+"]");
					if("R".equals(sRefType))
					{
                       String strReleasePhase=(String)mpInfo.get("to["+ DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM +"].frommid["+ 
               				DataConstants.REL_PART_FAMILY_REFERENCE+"].torel.to.attribute["+DataConstants.ATTR_RELEASE_PHASE+"]");
                       
                       String strPartFamilyId =(String)mpInfo.get("to["+ DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM +"].from[" + 
                       DomainConstants.TYPE_PART_FAMILY +"].id");
                       
                       if(UIUtil.isNotNullAndNotEmpty(strReleasePhase) && !strReleasePhase.equals(strNewReleasePhase))
                        {
                                if((strReleasePhase.equals(DataConstants.RELEASE_PHASE_DEVELOPMENT)) || 
                                		(strReleasePhase.equals(DataConstants.RELEASE_PHASE_PILOT) && 
                                				strNewReleasePhase.equals(DataConstants.RELEASE_PHASE_PRODUCTION)))
                                {
                                	String[] args = new String[1];
                                	args[0] = strNewRevisionObjId;
                                    PartFamily partlist = new PartFamily();
                               		if(UIUtil.isNotNullAndNotEmpty(strPartFamilyId))
                                 	{
                				 		partlist.removeReferenceFromMaster(context,args,strPartFamilyId,"emxPartFamily","removeReferenceFromMaster");
                				 		VPLMIntegTraceUtil.trace(context, ">>> Removed reference from Master");
                				 		sbReturnMsgWithinCollaborateRevise.append(DataConstants.CONSTANT_NEW_LINE);
                				 		sbReturnMsgWithinCollaborateRevise.append("Removed reference from Master");
                                	}
                                }
                        }
					}
				}
			}	
		}
		
		/**
		 * This method would get all previous revisions and release them
		 * @param context
		 * @param strECPartId
		 * @param strECPartType
		 * @param strReleasePhase
		 * @return int
		 * @throws MatrixException
		 */
		private int releasePreviousRevision(Context context, String strECPartId,String strECPartType,String strReleasePhase) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>> START of releasePreviousRevision method");
			boolean bCtxPushed = false;
			int iResult=0;
			try
			{
				if(!DataConstants.RELEASE_PHASE_DEVELOPMENT.equalsIgnoreCase(strReleasePhase) && !DataConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strECPartType) && !bIsShapePart) {
				
								CommonProductData cpd=new  CommonProductData(context);
								DomainObject doObj=DomainObject.newInstance(context,strECPartId);
								
								//current user would not have access to all the previous revisions. Hence pushContext is used
								ContextUtil.pushContext(context);
								bCtxPushed = true;
								ArrayList<String> arrOldRevisions=(ArrayList<String>) cpd.getPreviousRevisions(context, doObj);
							    Collections.reverse(arrOldRevisions);
								VPLMIntegTraceUtil.trace(context, ">>> arrOldRevisions::"+arrOldRevisions);
								iResult=releasePreviousRevisions(context,arrOldRevisions);
				}
			}catch(MatrixException e) {
				VPLMIntegTraceUtil.trace(context, ">>> releasePreviousRevision:: Inside catch of releasePreviousRevision method "+e.getMessage());
				throw e;
			}finally {
				if(bCtxPushed)
					ContextUtil.popContext(context);
			}
			VPLMIntegTraceUtil.trace(context, ">>> End of releasePreviousRevision method");
			return iResult;
		}
		
		/**
		 * This method would release all previous revisions
		 * @param context
		 * @param arrOldRevisions
		 * @return int
		 * @throws FrameworkException
		 */
		private int releasePreviousRevisions(Context context, ArrayList<String> arrOldRevisions) throws FrameworkException {
			VPLMIntegTraceUtil.trace(context, ">>> START of releasePreviousRevisions method");
			int iResult=0;
			if(null!=arrOldRevisions && !arrOldRevisions.isEmpty()){
					Iterator itr = arrOldRevisions.iterator();
	
					String prevObjState="";
					String prevObjId="";
					String prevObjName;
					String prevObjRev;
					DomainObject prevRevObj;
	
					StringList slSelects=new StringList(3);
					slSelects.add(DomainConstants.SELECT_CURRENT);
					slSelects.add(DomainConstants.SELECT_NAME);
					slSelects.add(DomainConstants.SELECT_REVISION);
					Map<String,String> mpInfo;
					
					while(itr.hasNext())	{
						mpInfo=new HashMap();
						prevObjId = (String)itr.next();
						prevRevObj	= DomainObject.newInstance(context, prevObjId);
						mpInfo=prevRevObj.getInfo(context,slSelects);
						prevObjState=mpInfo.get(DomainConstants.SELECT_CURRENT);
						prevObjName=mpInfo.get(DomainConstants.SELECT_NAME);
						prevObjRev=mpInfo.get(DomainConstants.SELECT_REVISION);
						
						VPLMIntegTraceUtil.trace(context, ">>>prevObjState "+prevObjState);
						
						if(!DataConstants.STATE_OBSOLETE.equals(prevObjState) && !DataConstants.STATE_RELEASE.equals(prevObjState)) {
							
							try {
								prevRevObj.setState(context, DataConstants.STATE_RELEASE);
								VPLMIntegTraceUtil.trace(context, ">>>Released "+prevRevObj);
								strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
								strReturnMessageBuf.append("Released ").append(prevObjName);
								strReturnMessageBuf.append(DataConstants.CONSTANT_STRING_SPACE);
								strReturnMessageBuf.append(prevObjRev);
							}catch(Exception e) {
								addExceptionMessage(e.getMessage(),"Release of "+prevObjName+" "+prevObjRev);
							}
						}
					}
				}
			return iResult;
		}
		
		/**
		 * Method to add exception message in StringBuilder
		 * @param strMessage
		 */
		private void addExceptionMessage(String strMessage,String strEvent) {
			DesignToolsIntegrationException designToolsException=new DesignToolsIntegrationException();
			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			strReturnMessageBuf.append("Exception during ").append(strEvent).append(" :: ");
			strReturnMessageBuf.append(DataConstants.CONSTANT_NEW_LINE);
			strReturnMessageBuf.append(designToolsException.formatExceptionMessage(strMessage));
		}
		
		/**
		 * Method to get the ECPart info from VPMReference
		 * @param context
		 * @param strVPMReferenceId
		 * @return Map
		 * @throws MatrixException
		 */
		private Map getECPartInfoFromVPMReference(Context context,String strVPMReferenceId) throws MatrixException {
			HashMap hmParam=new HashMap();
			hmParam.put("objectId", strVPMReferenceId);
			hmParam.put("objSelects", getECPartSelectables());
			
			return JPO.invoke(context, "pgDSOCATIAIntegration", null, "getECPartInfoFromCAD", JPO.packArgs(hmParam), Map.class);
		}
		
		/**
		 * Method to update the mandatory attributes on CAD Object
		 * @param context
		 * @param strVPMReferenceId
		 * @param strECPartId
		 * @throws MatrixException 
		 */
		public void updateMandatoryAttributesOnCAD(Context context, String strVPMReferenceId, String strECPartId) throws MatrixException  {
			
			if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceId) && UIUtil.isNotNullAndNotEmpty(strECPartId)) {
				DomainObject doECPart=DomainObject.newInstance(context,strECPartId);
				VPLMIntegTraceUtil.trace(context, "<< updateMandatoryAttributesOnCAD: Start ");
				StringList slSelects=new StringList();
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_RELEASE_CRITERIA);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_MATERIAL_TYPE);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_SEGMENT);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_CLASS);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_LIFECYCLE_STATUS);
				slSelects.add("from["+DataConstants.REL_PRIMARY_ORGANIZATION+"].to.name");
				slSelects.add("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION+"].to.name");
				
				
				Map mpECPartInfo=doECPart.getInfo(context,slSelects);
				VPLMIntegTraceUtil.trace(context, "<< updateMandatoryAttributesOnCAD: mpECPartInfo "+mpECPartInfo);
				setMandatoryAttributesOnCAD(context,mpECPartInfo,strVPMReferenceId);	
			}
		}
		
		/**
		 * Method to set the mandatory attributes on CAD Object
		 * @param context
		 * @param mpECPartInfo
		 * @param strVPMReferenceId
		 * @throws MatrixException 
		 */
		private void setMandatoryAttributesOnCAD(Context context, Map mpECPartInfo, String strVPMReferenceId) throws MatrixException {
		
			Map mpAttrMap=new HashMap();
			DomainObject doVPMRef=DomainObject.newInstance(context,strVPMReferenceId);
			VPLMIntegTraceUtil.trace(context, "<< setMandatoryAttributesOnCAD: Start ");
			boolean bPackaging=false;
			boolean bProduct=false;
			// get all the interfaces of the object
			BusinessInterfaceList busInterfaces = doVPMRef.getBusinessInterfaces(context);
			
			if(busInterfaces.toString().contains(DataConstants.INTERFACE_PNG_PACKAGING)) {
				bPackaging=true;
			}else if(busInterfaces.toString().contains(DataConstants.INTERFACE_PNG_PRODUCT)) {
				bProduct=true;
			}
			
			if(bPackaging) {
				mpAttrMap.put(DataConstants.ATTRIBUTE_PACKAGING_CLASS, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_CLASS));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PACKAGING_SEGMENT, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_SEGMENT));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PACKAGING_RELEASE_CRITERIA, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_RELEASE_CRITERIA));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PACKAGING_MATERIAL_TYPE, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_MATERIAL_TYPE));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PACKAGING_MFG_STATUS, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_LIFECYCLE_STATUS));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PACKAGING_REPORTED_FUNCTION, mpECPartInfo.get("from["+DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION+"].to.name"));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION, mpECPartInfo.get("from["+DataConstants.REL_PRIMARY_ORGANIZATION+"].to.name"));
			}
			else if(bProduct) {
				mpAttrMap.put(DataConstants.ATTRIBUTE_PRODUCT_SEGMENT, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_SEGMENT));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PRODUCT_RELEASE_CRITERIA, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_RELEASE_CRITERIA));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PRODUCT_MFG_STATUS, mpECPartInfo.get(DataConstants.SELECT_ATTRIBUTE_LIFECYCLE_STATUS));
				mpAttrMap.put(DataConstants.ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION, mpECPartInfo.get("from["+DataConstants.REL_PRIMARY_ORGANIZATION+"].to.name"));
			}
			
			VPLMIntegTraceUtil.trace(context, "<< setMandatoryAttributesOnCAD: mpAttrMap "+mpAttrMap);
			try {
				ContextUtil.pushContext(context, DataConstants.PERSON_USER_AGENT, null, context.getVault().getName());
			doVPMRef.setAttributeValues(context, mpAttrMap);		
			}finally {
				ContextUtil.popContext(context);
			}
				
		}
		
		/**
		 * DTCLD-142 22x changes
		 * Method to update the outer dimension attributes on EC Part.
		 * @param context
		 * @param strVPMReferenceId
		 * @param strECPartId
		 * @throws MatrixException  
		 */
		public void updateOuterDimensionAttributesOnECPart(Context context, String strVPMReferenceId, String strECPartId) throws MatrixException {
			
			VPLMIntegTraceUtil.trace(context, ">>>> START of updateOuterDimensionAttributesOnECPart method");
			
			if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceId) && UIUtil.isNotNullAndNotEmpty(strECPartId)) {
				DomainObject doVPMRef=DomainObject.newInstance(context,strVPMReferenceId);
				
				StringList slSelects=new StringList();
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_HEIGHT);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_WIDTH);
				slSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_LENGTH);
				slSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
				
			    Map mpVPMRefInfo=doVPMRef.getInfo(context,slSelects);
				VPLMIntegTraceUtil.trace(context, ">>>> mpVPMRefInfo::"+mpVPMRefInfo);

				String strECPartType=(String)mpVPMRefInfo.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
				VPLMIntegTraceUtil.trace(context, ">>>> strECPartType::"+strECPartType);
				
				Map mpAttrMap=new HashMap();
				DomainObject doECPart=DomainObject.newInstance(context,strECPartId);
				
				if(DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART.equalsIgnoreCase(strECPartType) ||
						DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART.equalsIgnoreCase(strECPartType)){
					mpAttrMap.put(DataConstants.ATTRIBUTE_OUTER_DIMENSION_HEIGHT_ORIGINAL, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_HEIGHT));
					mpAttrMap.put(DataConstants.ATTRIBUTE_OUTER_DIMENSION_WIDTH_ORIGINAL, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_WIDTH));
					mpAttrMap.put(DataConstants.ATTRIBUTE_OUTER_DIMENSION_LENGTH_ORIGINAL, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_LENGTH));
				}
				else if(DataConstants.TYPE_SHAPE_PART.equalsIgnoreCase(strECPartType)) {
					mpAttrMap.put(DataConstants.ATTRIBUTE_OUTER_DIMENSION_HEIGHT, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_HEIGHT));
					mpAttrMap.put(DataConstants.ATTRIBUTE_OUTER_DIMENSION_WIDTH, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_WIDTH));
					mpAttrMap.put(DataConstants.ATTRIBUTE_OUTER_DIMENSION_LENGTH, mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_LENGTH));
				}
			
				VPLMIntegTraceUtil.trace(context, ">>>>mpAttrMap::"+mpAttrMap);
				
				try {
					doECPart.setAttributeValues(context, mpAttrMap);
					VPLMIntegTraceUtil.trace(context, ">>>>Outer dimension attributes set on EC Part");
				}catch(Exception e) {
					StringList slOwnerModificationAccessList = new StringList();
					slOwnerModificationAccessList.add(DataConstants.CONSTANT_ACCESS_MODIFY);
					boolean hasAccess = FrameworkUtil.hasAccess(context, doECPart,slOwnerModificationAccessList);
					VPLMIntegTraceUtil.trace(context, ">>>>updateOuterDimensionAttributesOnECPart hasAccess::"+hasAccess);
					if (!hasAccess) {
						throw new FrameworkException(DataConstants.CONSTANT_NO_MODIFY_ACCESS_ON_EC_PART_CHECK_VPMCONTROL);
					}
				}
			}
		}
		
		/**
		 * Method added for DTCLD-373: add automation interface on object
		 * @param context
		 * @param strApplicationName
		 * @param strProcessName
		 * @param strObjectId
		 * @throws MatrixException
		 */
		public void addAutomationInterface(Context context, String strApplicationName, String strProcessName, String strObjectId) throws MatrixException {
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				String[] newArgs = { strApplicationName, strProcessName, strObjectId};
				VPLMIntegTraceUtil.trace(context, ">>> Arguments for automation metric tracking are:");
				for(int i=0;i<newArgs.length;i++) {
					VPLMIntegTraceUtil.trace(context, newArgs[i]);
				}
				JPO.invoke(context, DataConstants.CONSTANT_JPO_PGDTAUTOMATION_METRIC_TRACKING, null, DataConstants.CONSTANT_METHOD_ADD_USAGE_TRACKING_TODATA, newArgs);
			}
		}
}