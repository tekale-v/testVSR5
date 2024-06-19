/*
 * Added by APOLLO Team
 * For Collaborate with Assembled Product Part - Custom Sync
 */

package com.png.apollo.sync.ebom;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dassault_systemes.enovia.characteristic.impl.CharacteristicServices;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicFactory;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristic;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristicsUtil;
import com.dassault_systemes.enovia.characteristic.util.CharacteristicMasterConstants;
import com.dassault_systemes.enovia.characteristic.util.CharacteristicMasterUtil;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeOrder;
import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationAttribute;
import com.dassault_systemes.parameter_interfaces.ParameterInterfacesServices;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.MultiValueSelects;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.jdom.Attribute;
import com.matrixone.jdom.Document;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.input.SAXBuilder;
import com.matrixone.servlet.Framework;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.pgApolloWeightConversionUtility;
import com.png.apollo.designtool.getData.ReadWriteXMLForPLMDTDocument;

import matrix.db.BusinessInterface;
import matrix.db.BusinessObject;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.db.Signature;
import matrix.db.SignatureList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

@Path("/ebom")
public class GenerateEBOMService extends RestService {
	
	 private static final Logger loggerSync = LoggerFactory.getLogger("APOLLOSYNC");    
	 private static final Logger loggerWS = LoggerFactory.getLogger("APOLLOWS");
	 private static final org.apache.log4j.Logger loggerApolloTrace = org.apache.log4j.Logger.getLogger(GenerateEBOMService.class);
	 
	 private Map mapExecution = new HashMap();
	 long lStartTime = System.currentTimeMillis();
	 String sSyncStartTimeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
	 int iTimeStampCount = 0;
	 String sVPMRefName = DomainConstants.EMPTY_STRING;
	 String sVPMRefRevision = DomainConstants.EMPTY_STRING;
	 String sAPPName = DomainConstants.EMPTY_STRING;
	 String sAPPRevision = DomainConstants.EMPTY_STRING;
	 
	 private static final String  START_OF_COLLABORATION = "Collaborate started";
	 private static final String  REFINEMENT_OF_QUERY = "Basic validation of parameters and Refinement Of Query completed";
	 private static final String  PRODUCT_VALIDATION = "Physical Product and associated APP is retrieved";
	 private static final String  EXISTING_BACKGROUND_JOB = "Search for any existing background Job completed";
	 private static final String  PUBLISH_DESIGN = "Publish Design Parameters operation completed";
	 private static final String  BASIC_RMP_PARAMETER_VALIDATIONS = "Basic Validations of RMP and Core Material data completed";
	 private static final String  ERROR_CONSOLIDATION_UPDATE_HISTORY = "All errors are consolidated and Part History is updated";
	 private static final String  GET_CHILDREN_BEFORE_FREEZE_DESIGN_START = "Retrival of Physical Product children started";
	 private static final String  GET_CHILDREN_BEFORE_FREEZE_DESIGN_END = "Retrival of Physical Product children completed";
	 private static final String  FREEZE_DESIGN = "Freeze Design of Physical Product, 3DShape and Drawing completed";	 
	 private static final String  BG_INTIIATION = "Background Job initiated and about to submit";
	 private static final String  BG_PROCESSING_STARTED = "Background Job processing started in JVM";
	 private static final String  ENTERPRISE_ATTRIBUTE_UPDATE = "Enterprise Part Attributes Updates completed";
	 private static final String  GET_PREV_REV_DETAILS = "Get Previous Revision details completed";
	 private static final String  GET_EBOM_RMP_END = "Retrieved existing EBOM RMP";
	 private static final String  GET_PREVIOUS_REV_EBOM_SUB_END = "Retrieved Previous Revision EBOM and EBOM Substitute Details";	 
	 private static final String  EBOM_GENERATION = "EBOM Generation and EBOM attribute modification completed";
	 private static final String  ABORT_EXISTING_EVALUATE_CRITERIA_JOB = "Existing Background Jobs for Evaluate Criteria aborted";
	 private static final String  EVALUATE_CRITERIA_START = "Evaluate Criteria Started";
	 private static final String  EVALUATE_CRITERIA_END = "Evaluate Criteria completed";
	 private static final String  UPDATE_CHARACTERISTICS = "Update Characteristics completed";
	 private static final String  ADD_BOOKMARK = "APP added to Bookmark";
	 private static final String  TRANSFER_CONTROL = "Transfer Control completed";
	 private static final String  DEMOTE_VPMREF_START = "Background Operation - Demotion of VPMReference, 3DShape and drawing started";
	 private static final String  DEMOTE_VPMREF_END = "Background Operation - Demotion of VPMReference, 3DShape and drawing completed";
	 private static final String  EMAIL_NOTIFICATION_START = "Email Notification Content is prepared and about to send email";
	 private static final String  EMAIL_NOTIFICATION_END = "Email Notification is sent";
	 private static final String  END_OF_COLLABORATION = "End of Collaboration";

	@POST
	@Path("/genebom")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	/**
	 * This Web Service is called for EBOM Sync Operation. 
	 * @param request
	 * @param incomingData
	 * @return
	 * @throws Exception
	 */

	public Response generatebom(@Context HttpServletRequest request,InputStream incomingData) throws Exception
	{				
		matrix.db.Context context = null;
		StringBuffer strReturnMessageBuf = new StringBuffer();
		String strHistoryCollatrateWithAPP = pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP;
		String strPartObjID = DomainConstants.EMPTY_STRING;
		String strHistory = DomainConstants.EMPTY_STRING;		
		boolean isBackgroundJobRunning = false;
		
		mapExecution = new HashMap();	
		mapExecution.put("count", 0);
		iTimeStampCount = 0;
		try 
		{
			String line = null;
			StringBuffer sbCatiaDetails = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));		
			while((line = in.readLine())!=null) {
				sbCatiaDetails.append(line);
			}
			
			updateTimeStampDetails(START_OF_COLLABORATION);
			
			loggerSync.debug( " ===========================================================================" );			
			loggerSync.debug( " Collaborate with Assembled Product Part STARTS AT : {}" , sSyncStartTimeStamp);						
			

			String strCatiaDetails = sbCatiaDetails.toString();
			
			VPLMIntegTraceUtil.trace(context, "MassCollab: Sync Process Started at " + sSyncStartTimeStamp);
			VPLMIntegTraceUtil.trace(context, "MassCollab: strCatiaDetails = \n" + strCatiaDetails);
			
			if(UIUtil.isNotNullAndNotEmpty(strCatiaDetails)) {

				//Get the user context
				if (Framework.isLoggedIn((HttpServletRequest) request)) {
					context = Framework.getContext((HttpSession) request.getSession(false));
				}

				loggerSync.debug( " Username = {}" ,context.getUser());
				VPLMIntegTraceUtil.trace(context, "MassCollab: Username = "+context.getUser() );

				StringList objectSelects = new StringList(4);
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainConstants.SELECT_NAME);
				objectSelects.add(DomainConstants.SELECT_REVISION);
				objectSelects.add(DomainConstants.SELECT_CURRENT);
				objectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				objectSelects.add(DomainConstants.SELECT_DESCRIPTION);

				StringList relSelects = new StringList(5);
				relSelects.add(DomainRelationship.SELECT_ID);
				relSelects.add(DomainConstants.SELECT_FIND_NUMBER);
				relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
				relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
				relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLAYEREDPRODUCTAREA);
				relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_WEBWIDTH);
				
				String strReturnMsg = DomainConstants.EMPTY_STRING;
				strPartObjID = DomainConstants.EMPTY_STRING;
				String strVPMRefName = DomainConstants.EMPTY_STRING;
				String strVPMRefRev = DomainConstants.EMPTY_STRING;
				String sVPMRefDetails;
				String strAPPName = DomainConstants.EMPTY_STRING;
				String strAPPRevision = DomainConstants.EMPTY_STRING;
				String strIsBackgroundJobRunning = DomainConstants.EMPTY_STRING;
				StringList slSuccessMessages = new StringList();
				String strSuccessMessage = DomainConstants.EMPTY_STRING;
				int iErrorListSize = 0;

				String strFreezeDesign = DomainConstants.EMPTY_STRING;
				String strTransferControl = DomainConstants.EMPTY_STRING;
				String strBackgroundExecution = DomainConstants.EMPTY_STRING;
										
				//Refine Query and get details
				MapList mapListCatiaDetails  =  readAndRefineQueryString (context,strCatiaDetails);	
				
				updateTimeStampDetails(REFINEMENT_OF_QUERY);
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: mapListCatiaDetails = \n" + mapListCatiaDetails);
				loggerSync.debug( " mapListCatiaDetails = {}" , mapListCatiaDetails );
				
				int iMapListCatiaDetails = 0;
				if(null!=mapListCatiaDetails && !mapListCatiaDetails.isEmpty())
				{
					iMapListCatiaDetails = mapListCatiaDetails.size();
				}	
				Map mapProduct = new HashMap();
				MapList mlStacking = new MapList();
				MapList mlApplicators = new MapList();
				StringList slApplicatorRMPList = new StringList();
				StringList slError = new StringList();
				String strFirstRefineErrorMessage = DomainConstants.EMPTY_STRING;
				String strErrorMessage = DomainConstants.EMPTY_STRING;
				StringBuilder sbErrorHistory = new StringBuilder();
								
				Set<String> setUniqueErrors = new LinkedHashSet<>(); 
				boolean bFreezeDesign = false;
				boolean bTransferControl = false;
				boolean bBackgroundExecution = false;
				boolean bBackgroundJobInitiated = false;
				String strPublishDesignParams = DomainConstants.EMPTY_STRING;
				boolean bPublishDesignParams = true;
				String strPublishDesignParamError = DomainConstants.EMPTY_STRING;
				
				//Loop through each VPMReference Product
				for(int iCount=0; iCount < iMapListCatiaDetails; iCount++)
				{
					mapProduct = new HashMap();
					strVPMRefName = DomainConstants.EMPTY_STRING;
					strVPMRefRev = DomainConstants.EMPTY_STRING;
					strFreezeDesign = DomainConstants.EMPTY_STRING;
					strTransferControl = DomainConstants.EMPTY_STRING;
					mlStacking = new MapList();
					bFreezeDesign = false;
					bTransferControl = false;
					bBackgroundExecution = false;
					bBackgroundJobInitiated = false;
					strFirstRefineErrorMessage = DomainConstants.EMPTY_STRING;
					strErrorMessage = DomainConstants.EMPTY_STRING;
					slError = new StringList();
					sbErrorHistory = new StringBuilder();
					strAPPName = DomainConstants.EMPTY_STRING;
					strAPPRevision = DomainConstants.EMPTY_STRING;
					strIsBackgroundJobRunning = DomainConstants.EMPTY_STRING;
					isBackgroundJobRunning = false;
					iErrorListSize = 0;
					slSuccessMessages = new StringList();
					strSuccessMessage = DomainConstants.EMPTY_STRING;
					strPublishDesignParams = DomainConstants.EMPTY_STRING;
					bPublishDesignParams = true;
					strPublishDesignParamError = DomainConstants.EMPTY_STRING;
					sVPMRefDetails = DomainConstants.EMPTY_STRING;
					mlApplicators = new MapList();
					slApplicatorRMPList = new StringList();
					
					if(strReturnMessageBuf.length() > 0){
						strReturnMessageBuf.append(" #####");
					}
					
					try
					{
						mapProduct = (Map)mapListCatiaDetails.get(iCount);
						
						if(null!=mapProduct && !mapProduct.isEmpty() && mapProduct.containsKey(pgApolloConstants.KEY_NAME)) {							

							//1. Get Physical Product Information - Name, rev, bFreezeDesign and bRetainSubstitutes
							strVPMRefName = (String)mapProduct.get(pgApolloConstants.KEY_NAME);
							if(mapProduct.containsKey(pgApolloConstants.KEY_REV))
							{
								strVPMRefRev = (String)mapProduct.get(pgApolloConstants.KEY_REV);
							}
							if(mapProduct.containsKey(pgApolloConstants.KEY_FREEZEDESIGN))
							{
								strFreezeDesign = (String)mapProduct.get(pgApolloConstants.KEY_FREEZEDESIGN);
							}
							if(mapProduct.containsKey(pgApolloConstants.KEY_TRANSFERCONTROL))
							{
								strTransferControl = (String)mapProduct.get(pgApolloConstants.KEY_TRANSFERCONTROL);
							}
							if(mapProduct.containsKey(pgApolloConstants.KEY_BACKGROUNDEXECUTION))
							{
								strBackgroundExecution = (String)mapProduct.get(pgApolloConstants.KEY_BACKGROUNDEXECUTION);
							}							
							if(mapProduct.containsKey(pgApolloConstants.KEY_STACKING))
							{
								mlStacking = (MapList)mapProduct.get(pgApolloConstants.KEY_STACKING);
							}	
							if(mapProduct.containsKey(pgApolloConstants.KEY_APPLICATORS))
							{
								mlApplicators = (MapList)mapProduct.get(pgApolloConstants.KEY_APPLICATORS);
							}	
							if(mapProduct.containsKey(pgApolloConstants.KEY_APPLICATOR_RMP))
							{
								slApplicatorRMPList = (StringList)mapProduct.get(pgApolloConstants.KEY_APPLICATOR_RMP);
							}	
							if(mapProduct.containsKey(pgApolloConstants.KEY_PUBLISHDESIGNPARAM))
							{
								strPublishDesignParams = (String) mapProduct.get(pgApolloConstants.KEY_PUBLISHDESIGNPARAM);
							}							
							if(UIUtil.isNotNullAndNotEmpty(strPublishDesignParams) && (pgApolloConstants.STR_NO_FLAG.equalsIgnoreCase(strPublishDesignParams) || pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strPublishDesignParams)))
							{
								bPublishDesignParams = false;
							}							
							if(UIUtil.isNotNullAndNotEmpty(strTransferControl) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strTransferControl) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strTransferControl)))
							{
								bTransferControl = true;
							}
							
							mapProduct = updateDrawingUpdateStatusFlag(mapProduct);
							
							loggerSync.debug( " VPMRef Name: {}" , strVPMRefName);
							loggerSync.debug( " VPMRef Revision: {}" , strVPMRefRev);
							loggerSync.debug( " Freeze Design: {}" , strFreezeDesign);	
							loggerSync.debug( " Background Execution: {}" , strBackgroundExecution);		
							loggerSync.debug( " Publish Design: {}" , strPublishDesignParams);
							
							VPLMIntegTraceUtil.trace(context, "MassCollab: VPMRef Name: " + strVPMRefName );
							VPLMIntegTraceUtil.trace(context, "MassCollab: VPMRef Revision: " + strVPMRefRev );							
							VPLMIntegTraceUtil.trace(context, "MassCollab: Freeze Design: " + strFreezeDesign );
							VPLMIntegTraceUtil.trace(context, "MassCollab: mlStacking: " + mlStacking );	
							
							sVPMRefDetails = new StringBuilder(strVPMRefName).append(pgApolloConstants.CONSTANT_STRING_DOT).append(strVPMRefRev).toString();
							
							strReturnMessageBuf.append(strVPMRefName);
							strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_SPACE);							

							if(UIUtil.isNotNullAndNotEmpty(strVPMRefRev)) {
								strReturnMessageBuf.append(strVPMRefRev);
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
							}
							else 
							{
								VPLMIntegTraceUtil.trace(context, "MassCollab: No Revision Information Received from Catia for Product : " + strVPMRefName );
								loggerSync.debug( " No Revision Information Received from Catia for Product : {}" , strVPMRefName );
								strReturnMessageBuf.append(strVPMRefRev);
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_VPMREFERENCEREVISIONNOTFOUND.replace("<VPMREF_NAME>", strVPMRefName));
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
								continue;
							}
							
							String strVPMReferenceObjId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE,strVPMRefName, strVPMRefRev);
							if(UIUtil.isNullOrEmpty(strVPMReferenceObjId))
							{
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_VPMREFERENCENOTEXIST.replace("<VPMREF_NAME>", strVPMRefName).replace("<VPMREF_REV>", strVPMRefRev));
								continue;
							}
							
							VPLMIntegTraceUtil.trace(context, "MassCollab: BEFORE validateProductAndUpdateAPP >>> ");
							
							//----------------------------------------------------------------------------------------
							//2. Validate Physical Product state and associated APP. Return APP object id or Error message.
							strReturnMsg = validateProductAndUpdateAPP(context, strVPMReferenceObjId, strVPMRefName, strVPMRefRev,objectSelects,bTransferControl);
							//----------------------------------------------------------------------------------------

							VPLMIntegTraceUtil.trace(context, "MassCollab: AFTER validateProductAndUpdateAPP >>> ");
							
							if(strReturnMsg.contains(pgApolloConstants.STR_ERROR)){
								strReturnMessageBuf.append(strReturnMsg);
								continue;
							}
							//If Success it returns Part ObjectId
							strPartObjID = strReturnMsg;
							
							VPLMIntegTraceUtil.trace(context, "MassCollab: strPartObjID >>> " + strPartObjID);

							DomainObject domAPPObject  = DomainObject.newInstance(context, strPartObjID);	
							Map mapAPPInfo = domAPPObject.getInfo(context, objectSelects);
							
							strAPPName = (String)mapAPPInfo.get(DomainConstants.SELECT_NAME);
							strAPPRevision = (String)mapAPPInfo.get(DomainConstants.SELECT_REVISION);
							
							sVPMRefName = strVPMRefName;
							sVPMRefRevision = strVPMRefRev;
							sAPPName = strAPPName;
							sAPPRevision = strAPPRevision;							
							
							updateTimeStampDetails(PRODUCT_VALIDATION);
							
							//Check if previous Background Job operation is still in progress
							String strJobTitle = new StringBuilder(pgApolloConstants.STRING_COLLABORATE_WITH_APP_JOB_TITLE).append(strAPPName).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(strAPPRevision).toString();							
							strIsBackgroundJobRunning = checkRunningBackGroundJobConnected(context, strPartObjID, strJobTitle, pgApolloConstants.STR_MODE_SYNC);
							loggerSync.debug( " Background Job Validation Message : {} : {}" , strIsBackgroundJobRunning, sVPMRefDetails );

							if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strIsBackgroundJobRunning))
							{
								strHistory = strHistoryCollatrateWithAPP + pgApolloConstants.EBOM_SYNC_HISTORY_STARTS;
								addCustomHistoryOnSync(context,strPartObjID, strHistory);	
							}
							else
							{
								isBackgroundJobRunning = true;
							}
							
							loggerSync.debug( " Background Running Flag : {} : {}" , isBackgroundJobRunning, sVPMRefDetails );
							VPLMIntegTraceUtil.trace(context, "MassCollab: Previous Background Job running  >>> " + isBackgroundJobRunning);
							
							updateTimeStampDetails(EXISTING_BACKGROUND_JOB);							
							
							if(!isBackgroundJobRunning && bPublishDesignParams)
							{
								strReturnMsg =  publishDesignParametersInCollaborateWithAPP(context, mapProduct);
								if(strReturnMsg.contains(pgApolloConstants.STR_ERROR))
								{
									loggerSync.debug( "Error while Publish Design Parameter : {} {}" , strReturnMsg, sVPMRefDetails );
									VPLMIntegTraceUtil.trace(context, "MassCollab: " + strReturnMsg );
									strPublishDesignParamError = strReturnMsg;									
								}
								else
								{
									strHistory = new StringBuilder().append(strHistoryCollatrateWithAPP).append(pgApolloConstants.STR_SUCCESS_PUBLISH_DESIGN_PARAMETERS).toString();
									addCustomHistoryOnSync(context, strPartObjID , strHistory);					
									loggerSync.debug( "Design Parameters successfully published.. {} ", sVPMRefDetails);
								}		
								
								updateTimeStampDetails(PUBLISH_DESIGN);
							}													
							
							Map outPutMap = new HashMap();					
							
							//Fix for Report Type Issue and Business object does not found issue Starts
							if(mapProduct.containsKey(pgApolloConstants.STR_ERROR))
							{
								slError = (StringList)mapProduct.get(pgApolloConstants.STR_ERROR);
							}							
							if(UIUtil.isNotNullAndNotEmpty(strFreezeDesign) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strFreezeDesign) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strFreezeDesign)))
							{
								bFreezeDesign = true;
							}
							if(UIUtil.isNotNullAndNotEmpty(strBackgroundExecution) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strBackgroundExecution) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strBackgroundExecution)))
							{
								bBackgroundExecution = true;
							}
							
							//4. Validate all Raw Materials, Core Materials and Material Functions							
							mlStacking = pgApolloCommonUtil.validateRMPDataForSync (context, strPartObjID, mlStacking, true, false, true, bFreezeDesign);
							VPLMIntegTraceUtil.trace(context, "MassCollab: mlStacking validateRMPDataForSync>>> " + mlStacking);
							mlStacking = validateGrossVolumeAreaLengthParameters (mlStacking);
							VPLMIntegTraceUtil.trace(context, "MassCollab: After validateGrossVolumeAreaLengthParameters >>> " + mlStacking);
							
							//Validate All Applicators							
							strErrorMessage = pgApolloCommonUtil.validateApplicatorParts(context, mlApplicators, slApplicatorRMPList, false);
							
							VPLMIntegTraceUtil.trace(context, "MassCollab: Validate Applicator Data >>> " + mlStacking);
							
							updateTimeStampDetails(BASIC_RMP_PARAMETER_VALIDATIONS);
							
							slError.addAll(consolidateAllErrorMessages(mlStacking));
							
							if(UIUtil.isNotNullAndNotEmpty(strPublishDesignParamError))
							{
								slError.addElement(strPublishDesignParamError);
							}
							
							if(UIUtil.isNotNullAndNotEmpty(strErrorMessage))
							{
								slError.add(strErrorMessage);
							}
							
							VPLMIntegTraceUtil.trace(context, "MassCollab: slError >>> " + slError);
							
							setUniqueErrors = new LinkedHashSet<>(); 
							setUniqueErrors.addAll(slError);
							
							slError.clear(); 
							if(isBackgroundJobRunning)
							{
								slError.add(strIsBackgroundJobRunning); 						
							}							
							slError.addAll(setUniqueErrors); 														

							if(null!=slError && !slError.isEmpty())
							{
								loggerSync.debug( " All Validation Errors : {} : {}" , slError, sVPMRefDetails );
								VPLMIntegTraceUtil.trace(context, "MassCollab: All Validation Errors : " + slError);
								iErrorListSize = slError.size();
								for(int n=0 ; n < iErrorListSize ; n++)
								{
									strErrorMessage = slError.get(n);									
									if(UIUtil.isNullOrEmpty(strFirstRefineErrorMessage))
									{								
										strFirstRefineErrorMessage = strErrorMessage;
									}	
									if(!isBackgroundJobRunning)
									{
										sbErrorHistory = new StringBuilder();
										sbErrorHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
										sbErrorHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);
										sbErrorHistory.append(pgApolloConstants.CONSTANT_STRING_SPACE);
										sbErrorHistory.append(strErrorMessage);
										VPLMIntegTraceUtil.trace(context, "MassCollab: \n"+sbErrorHistory.toString() );																	
										addCustomHistoryOnSync(context, strPartObjID, sbErrorHistory.toString());
									}
								}		
								sbErrorHistory = new StringBuilder();
								sbErrorHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
								sbErrorHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);
								sbErrorHistory.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbErrorHistory.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
								if(!isBackgroundJobRunning)
								{
									addCustomHistoryOnSync(context, strPartObjID, sbErrorHistory.toString());	
								}
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(strFirstRefineErrorMessage).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
								strReturnMessageBuf.append(StringUtil.join(slError, pgApolloConstants.CONSTANT_STRING_CARET));	
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
								strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
								
								strReturnMessageBuf = updateDrawingUpdateStatus(context,strReturnMessageBuf, mapProduct, strPartObjID);
								
								if(UIUtil.isNullOrEmpty(strPublishDesignParamError))
								{
									strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
									strReturnMessageBuf.append(pgApolloConstants.STR_SUCCESS_PUBLISH_DESIGN_PARAMETERS);
								}			
								updateTimeStampDetails(ERROR_CONSOLIDATION_UPDATE_HISTORY);
								continue;		
							}
														
							//All Initial Validations are completed. So executing Actual processing.
							loggerSync.debug( "All initial data Validation is completed. : {} ", sVPMRefDetails);
							VPLMIntegTraceUtil.trace(context, "MassCollab: All initial data Validation is completed. ");
							
							if(bBackgroundExecution)
							{

								outPutMap = initiateBackgroundJobExecutionForCollaborate(context, mapProduct, strPartObjID, strVPMReferenceObjId, strVPMRefName, strVPMRefRev, mapAPPInfo, bFreezeDesign);
								if(outPutMap.containsKey(pgApolloConstants.STR_ERROR))
								{																		
									sbErrorHistory = new StringBuilder();
									sbErrorHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
									sbErrorHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);
									sbErrorHistory.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbErrorHistory.append(pgApolloConstants.STR_ERROR_INITIATE_BACKGROUND_JOB);
									sbErrorHistory.append((String)outPutMap.get(pgApolloConstants.STR_ERROR));
									addCustomHistoryOnSync(context, strPartObjID, sbErrorHistory.toString());
									
									strReturnMessageBuf.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON);
									strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_INITIATE_BACKGROUND_JOB);
									strReturnMessageBuf.append((String)outPutMap.get(pgApolloConstants.STR_ERROR));	
									
									if(UIUtil.isNullOrEmpty(strPublishDesignParamError))
									{
										strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
										strReturnMessageBuf.append(pgApolloConstants.STR_SUCCESS_PUBLISH_DESIGN_PARAMETERS);
									}
									
									continue;									
								}
								else if(outPutMap.containsKey(pgApolloConstants.STR_SUCCESS))
								{								
									sbErrorHistory = new StringBuilder();
									sbErrorHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
									sbErrorHistory.append(pgApolloConstants.STR_STATUS_JOB_INITIATED);
									sbErrorHistory.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);									
									sbErrorHistory.append((String)outPutMap.get(pgApolloConstants.STR_SUCCESS));
									addCustomHistoryOnSync(context, strPartObjID, sbErrorHistory.toString());
									
									strReturnMessageBuf.append(pgApolloConstants.STR_STATUS_JOB_INITIATED);
									strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
									strReturnMessageBuf.append((String)outPutMap.get(pgApolloConstants.STR_SUCCESS));
									
									if(UIUtil.isNullOrEmpty(strPublishDesignParamError))
									{
										strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
										strReturnMessageBuf.append(pgApolloConstants.STR_SUCCESS_PUBLISH_DESIGN_PARAMETERS);
									}									
									bBackgroundJobInitiated = true;
								}
							}
							else
							{
								outPutMap = collaborateWithAPP(context, mapProduct, strPartObjID, strVPMReferenceObjId);
								
								if(outPutMap.containsKey(pgApolloConstants.STR_SUCCESS))
								{
									slSuccessMessages = (StringList) outPutMap.get(pgApolloConstants.STR_SUCCESS);
								}	
								if(UIUtil.isNullOrEmpty(strPublishDesignParamError))
								{										
									slSuccessMessages.addElement(pgApolloConstants.STR_SUCCESS_PUBLISH_DESIGN_PARAMETERS);
								}
								if(null != slSuccessMessages && !slSuccessMessages.isEmpty())
								{					
									strSuccessMessage = StringUtil.join(slSuccessMessages, pgApolloConstants.CONSTANT_STRING_CARET);
								}
								if(outPutMap.containsKey(pgApolloConstants.STR_ERROR))
								{
									strErrorMessage = (String)outPutMap.get(pgApolloConstants.STR_ERROR);
									if(strErrorMessage.contains(pgApolloConstants.CONSTANT_STRING_CARET))
									{
										strFirstRefineErrorMessage = (StringUtil.split(strErrorMessage, pgApolloConstants.CONSTANT_STRING_CARET)).get(0);
										strReturnMessageBuf.append(strFirstRefineErrorMessage);
									}
									else
									{
										strReturnMessageBuf.append(strErrorMessage);
									}									
									strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
									strReturnMessageBuf.append(strSuccessMessage);
									strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
									strReturnMessageBuf.append(strErrorMessage);
									continue;
								}
	
							}						
						}
						else 
						{ //Give a error response if Query string length is 0
							strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
							strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
							strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_DATANOTRECEIVED);
							continue;
						}
						if(!bBackgroundJobInitiated)
						{
							strReturnMessageBuf.append(pgApolloConstants.STR_SUCCESS);
							strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
							strReturnMessageBuf.append(pgApolloConstants.STR_EBOM_SUCCESSMESSAGE);
							if(UIUtil.isNotNullAndNotEmpty(strSuccessMessage))
							{
								strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET).append(strSuccessMessage);
							}
						}
					}catch (Exception ex){
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
						strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_DATAPARSEERROR.replace("<VPMREF_NAME>", strVPMRefName).replace("<VPMREF_REV>", strVPMRefRev));
						loggerApolloTrace.error(ex.getMessage() ,ex);
						
						if(UIUtil.isNotNullAndNotEmpty(strPartObjID) && !isBackgroundJobRunning)
						{
							strHistory = strHistoryCollatrateWithAPP + pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR + ex.getLocalizedMessage();
							addCustomHistoryOnSync(context,strPartObjID, strHistory);
						}
						loggerSync.debug( " Error :{}" , ex.getLocalizedMessage() );
					}
					finally
					{						
						if(UIUtil.isNotNullAndNotEmpty(strPartObjID) && !bBackgroundJobInitiated && !isBackgroundJobRunning)
						{
							strHistory = strHistoryCollatrateWithAPP + pgApolloConstants.EBOM_SYNC_HISTORY_END ;
							addCustomHistoryOnSync(context,strPartObjID, strHistory);							
							updateTimeStampDetails(END_OF_COLLABORATION);
						}
					}
				}				
			} else {
				strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
				strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
				strReturnMessageBuf.append(pgApolloConstants.STR_NO_DATA_FOR_PROCESSING);
				VPLMIntegTraceUtil.trace(context, "MassCollab: No valid Data Sent for Processing.");
				loggerSync.debug( " No valid data sent for processing.");
			}			
			VPLMIntegTraceUtil.trace(context, "MassCollab: END OF COLLABORATION >>> ");			
		}
		catch(Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
			if(UIUtil.isNotNullAndNotEmpty(strPartObjID) && !isBackgroundJobRunning)
			{
				strHistory =strHistoryCollatrateWithAPP +pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR+ ex.getLocalizedMessage();
				addCustomHistoryOnSync(context,strPartObjID, strHistory);
			}			
			loggerSync.error( " Error : {}" , ex.getLocalizedMessage());
			return Response.serverError().entity(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON+ex.toString()).build();			
		}
		finally 
		{
			loggerSync.debug( " Final Response --> {}", strReturnMessageBuf);
			String strEndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerSync.debug( "****SYNC COMPLETED**** {}" , strEndTime );
		}		
		return Response.status(200).entity(strReturnMessageBuf.toString()).build();
	}
	
	/**
	 * Method to update Drawing Update Status Flag
	 * @param mapProduct
	 * @return
	 */
	private Map updateDrawingUpdateStatusFlag(Map mapProduct)
	{
		String sDrawingUpdateStatus = DomainConstants.EMPTY_STRING;
		if(mapProduct.containsKey(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS))
		{
			sDrawingUpdateStatus = (String)mapProduct.get(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS);
		}	

		boolean bDrawingUpdateStatus = true;

		if(UIUtil.isNotNullAndNotEmpty(sDrawingUpdateStatus))
		{
			if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sDrawingUpdateStatus))
			{
				bDrawingUpdateStatus = true;
			}
			else
			{
				bDrawingUpdateStatus = false;				
			}
		}
		
		mapProduct.put(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS_BOOLEAN_FLAG, bDrawingUpdateStatus);
		
		return mapProduct;
	}


	/**
	 * Method to update Drawing Update Status
	 * @param context 
	 * @param sReturnMessageBuff
	 * @param mapProduct
	 * @param sPartObjID 
	 * @return
	 * @throws Exception 
	 */
	public StringBuffer updateDrawingUpdateStatus(matrix.db.Context context, StringBuffer sReturnMessageBuff, Map mapProduct, String sPartObjID) throws Exception 
	{	
		String sDrawingUpdateStatus = DomainConstants.EMPTY_STRING;
		if(mapProduct.containsKey(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS))
		{
			sDrawingUpdateStatus = (String)mapProduct.get(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS);
		}	
		String sMessage;
		if(UIUtil.isNotNullAndNotEmpty(sDrawingUpdateStatus))
		{
			if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sDrawingUpdateStatus))
			{
				sMessage = pgApolloConstants.STR_SUCCESS_DRAWING_UPDATE;
			}
			else
			{
				sMessage = pgApolloConstants.STR_ERROR_DRAWING_UPDATE;				
			}
			
			if(UIUtil.isNotNullAndNotEmpty(sMessage))
			{
				sReturnMessageBuff.append(pgApolloConstants.CONSTANT_STRING_CARET);
				sReturnMessageBuff.append(sMessage);
				
				if(UIUtil.isNotNullAndNotEmpty(sPartObjID))
				{
					String strHistory = new StringBuilder(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP).append(sMessage).toString(); 
					addCustomHistoryOnSync(context,sPartObjID, strHistory);
				}	
			}
			
		}	
		
		return sReturnMessageBuff;		
	}
	
	/**
	 * Method to update message list for Drawing Update Status
	 * @param context 
	 * @param slMessageList
	 * @param sDrawingUpdateStatus
	 * @param strPartObjID 
	 * @return
	 * @throws Exception 
	 */
	public StringList updateMessageListForDrawingUpdateStatus(matrix.db.Context context, StringList slMessageList, String sDrawingUpdateStatus, String sPartObjID) throws Exception 
	{	
		String sMessage = DomainConstants.EMPTY_STRING;
		
		if(UIUtil.isNotNullAndNotEmpty(sDrawingUpdateStatus))
		{
			if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sDrawingUpdateStatus))
			{
				sMessage = pgApolloConstants.STR_SUCCESS_DRAWING_UPDATE;
			}
			else
			{
				sMessage = pgApolloConstants.STR_ERROR_DRAWING_UPDATE;
			}
			
		}	
		
		if(UIUtil.isNotNullAndNotEmpty(sMessage))
		{
			slMessageList.add(sMessage);
			
			if(UIUtil.isNotNullAndNotEmpty(sPartObjID))
			{
				String strHistory = new StringBuilder(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP).append(sMessage).toString(); 
				addCustomHistoryOnSync(context,sPartObjID, strHistory);
			}		
		}
		
		return slMessageList;		
	}

	/**
	 * Method to update time stamp details
	 * @param sTimeStampIdentifier 
	 */
	public void updateTimeStampDetails(String sTimeStampIdentifier) 
	{		
		if(null != mapExecution && mapExecution.containsKey("count"))
		{
			lStartTime = System.currentTimeMillis();
			sSyncStartTimeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			
			int iCount = (int)mapExecution.get("count");			
			iTimeStampCount = iCount+1;
		}
		else
		{
			return;
		}		
		mapExecution.put("t"+iTimeStampCount+"_timestamp", sSyncStartTimeStamp);
		mapExecution.put("t"+iTimeStampCount+"_time", lStartTime);
		mapExecution.put("Identifier_"+iTimeStampCount, sTimeStampIdentifier);
		mapExecution.put("count", iTimeStampCount);

		if(END_OF_COLLABORATION.equalsIgnoreCase(sTimeStampIdentifier))
		{
			writeFinalLogs();		
		}
	}
	
	/**
	 * Method to write Final Logs
	 */
	private void writeFinalLogs() 
	{
		int iCount = 0;
		try {
			iCount = (int)mapExecution.get("count");
		} catch (Exception e) {
			iCount = 0;
		}	
		
		if(iCount > 0)
		{
			String sTimeStamp;
			String sTime;
			String sIdentifier;
			
			Long lTime;
			Long lPreviousTime = 0L;
			Long lTimeTaken;		

			loggerSync.debug("-------------------- {} - {} {} - {} {}----------------------", pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP, sVPMRefName, sVPMRefRevision, sAPPName, sAPPRevision);

			try {
				for(int i=1; i<=iCount; i++)
				{
					sIdentifier = (String)mapExecution.get("Identifier_"+i);
					sTimeStamp = (String)mapExecution.get("t"+i+"_timestamp");
					lTime = (long)mapExecution.get("t"+i+"_time");
					
					if(lPreviousTime > 0)
					{
						lTimeTaken = lTime - lPreviousTime;
					}
					else
					{
						lTimeTaken = 0L;
					}
					
					loggerSync.debug(" {} at : {} Time Taken : {} ms ", sIdentifier, sTimeStamp, lTimeTaken);
					
					lPreviousTime = lTime;
				}
			} catch (Exception ex) {
				
				loggerApolloTrace.error(ex.getMessage() ,ex);
			}
			
			loggerSync.debug("-------------------- {} - {} {} - {} {}----------------------", END_OF_COLLABORATION, sVPMRefName, sVPMRefRevision, sAPPName, sAPPRevision);

		}
		
	}

	/**
	 * Common method to collaborate with APP for Normal Sync operation and Background Job
	 * @param context
	 * @param mapProduct
	 * @param strPartObjID
	 * @param strVPMReferenceObjId
	 * @return
	 */
	public Map collaborateWithAPP(matrix.db.Context context, Map mapProduct, String strPartObjID, String strVPMReferenceObjId ) throws Exception
	{
		Map outputMap = new HashMap();
		String strErrorMessage;
		StringBuilder sbErrorMessage = new StringBuilder();
		StringList slSuccessMsg = new StringList();
		try 
		{
			StringList objectSelects = new StringList(3);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			String strHistoryCollatrateWithAPP = pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP;

			StringList relSelects = new StringList(5);
			relSelects.add(DomainRelationship.SELECT_ID);
			relSelects.add(DomainConstants.SELECT_FIND_NUMBER);
			relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
			relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
			relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLAYEREDPRODUCTAREA);

			MapList mlConnectedMaterialData;
			DomainObject dPartObj = null;
			String strReturnMsg;
			String strVPMRefName;
			String strVPMRefRev = DomainConstants.EMPTY_STRING;

			String strFreezeDesign = DomainConstants.EMPTY_STRING;
			String strTransferControl = DomainConstants.EMPTY_STRING;
			String strRetainSubstitutes = DomainConstants.EMPTY_STRING;
			String strAddToBookmark = DomainConstants.EMPTY_STRING;		

			MapList mlStacking = new MapList();
			MapList mlPerformanceCharacteristics = new MapList();
			StringList slPlyGroupsOfModel = new StringList();
			Map mpCatiaEnoviaUOMMap = new HashMap();
			StringList strEBOMAttributeList = new StringList();
			String strHistory;
			StringList slReturnMsgList;
						
			boolean bFreezeDesign = false;
			boolean bTransferControl = false;
			boolean bRetainSubstitutes = false;
			boolean bAddToBookmark = false;
			
			strVPMRefName = (String)mapProduct.get(pgApolloConstants.KEY_NAME);
			if(mapProduct.containsKey(pgApolloConstants.KEY_REV))
			{
				strVPMRefRev = (String)mapProduct.get(pgApolloConstants.KEY_REV);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_FREEZEDESIGN))
			{
				strFreezeDesign = (String)mapProduct.get(pgApolloConstants.KEY_FREEZEDESIGN);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_TRANSFERCONTROL))
			{
				strTransferControl = (String)mapProduct.get(pgApolloConstants.KEY_TRANSFERCONTROL);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_RETAINSUBSTITUTE))
			{
				strRetainSubstitutes = (String)mapProduct.get(pgApolloConstants.KEY_RETAINSUBSTITUTE);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_UOM))
			{
				mpCatiaEnoviaUOMMap = (Map)mapProduct.get(pgApolloConstants.KEY_UOM);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_UNIQUEPLYGROUPLIST))
			{
				slPlyGroupsOfModel = (StringList)mapProduct.get(pgApolloConstants.KEY_UNIQUEPLYGROUPLIST);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_STACKING))
			{
				mlStacking = (MapList)mapProduct.get(pgApolloConstants.KEY_STACKING);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_PERFCHAR))
			{
				mlPerformanceCharacteristics = (MapList)mapProduct.get(pgApolloConstants.KEY_PERFCHAR);
			}
			if(mapProduct.containsKey(pgApolloConstants.KEY_EBOMATTRIBUTELIST))
			{
				strEBOMAttributeList = (StringList)mapProduct.get(pgApolloConstants.KEY_EBOMATTRIBUTELIST);
			}
						
			if(mapProduct.containsKey(pgApolloConstants.KEY_ADDTOBOOKMARK))
			{
				strAddToBookmark = (String) mapProduct.get(pgApolloConstants.KEY_ADDTOBOOKMARK);
			}
			
			String sDrawingUpdateStatus = DomainConstants.EMPTY_STRING;	
			if(mapProduct.containsKey(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS))
			{
				sDrawingUpdateStatus = (String)mapProduct.get(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS);
			}		
			
			boolean bDrawingUpdateStatusFlag = (boolean)mapProduct.get(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS_BOOLEAN_FLAG);

			loggerSync.debug( " Update Drawing Status: {}" , sDrawingUpdateStatus);
			VPLMIntegTraceUtil.trace(context, "MassCollab: Update Drawing Status: " + sDrawingUpdateStatus );
			updateMessageListForDrawingUpdateStatus(context, slSuccessMsg, sDrawingUpdateStatus, strPartObjID);
			
			loggerSync.debug( " VPMRef Name: {}" , strVPMRefName);
			loggerSync.debug( " VPMRef Revision: {}" , strVPMRefRev);
			loggerSync.debug( " Freeze Design: {}" , strFreezeDesign);
			loggerSync.debug( " Transfer Control: {}" , strTransferControl);							
			loggerSync.debug( " Retain Substitutes: {}" , strRetainSubstitutes);
			loggerSync.debug( " CATIA UOM: {}" , mpCatiaEnoviaUOMMap);
			loggerSync.debug( " Unique Plygroups: {}" , slPlyGroupsOfModel);			
			loggerSync.debug( " Add To Bookmark: {}" , strAddToBookmark);
			loggerSync.debug( " Drawing Update Status : {}" , bDrawingUpdateStatusFlag);

			
			VPLMIntegTraceUtil.trace(context, "MassCollab: VPMRef Name: " + strVPMRefName );
			VPLMIntegTraceUtil.trace(context, "MassCollab: VPMRef Revision: " + strVPMRefRev );
			VPLMIntegTraceUtil.trace(context, "MassCollab: CATIA UOM: " + mpCatiaEnoviaUOMMap );			
			VPLMIntegTraceUtil.trace(context, "MassCollab: Freeze Design: " + strFreezeDesign );
			VPLMIntegTraceUtil.trace(context, "MassCollab: Transfer Control: " + strTransferControl );
			VPLMIntegTraceUtil.trace(context, "MassCollab: Retain Substitutes: " + strRetainSubstitutes );
			VPLMIntegTraceUtil.trace(context, "MassCollab: mlStacking: " + mlStacking );
			VPLMIntegTraceUtil.trace(context, "MassCollab: mlPerformanceCharacteristics: " + mlPerformanceCharacteristics );
			VPLMIntegTraceUtil.trace(context, "MassCollab: Unique Plygroups: " + slPlyGroupsOfModel );
			VPLMIntegTraceUtil.trace(context, "MassCollab: strEBOMAttributeList: " + strEBOMAttributeList );			
			VPLMIntegTraceUtil.trace(context, "MassCollab: bDrawingUpdateStatusFlag: " + bDrawingUpdateStatusFlag );			
			
			DomainObject domVPMRefObj = DomainObject.newInstance(context, strVPMReferenceObjId);	
			
			//3. Update Enterprise Part
			updateEnterprisePart(context,strPartObjID,domVPMRefObj);
			
			updateTimeStampDetails(ENTERPRISE_ATTRIBUTE_UPDATE);
			
			loggerSync.debug( " Update APP Attributes completed successfully. ");
			VPLMIntegTraceUtil.trace(context, "MassCollab: Update APP Attributes completed successfully. ");	
			
			if(UIUtil.isNotNullAndNotEmpty(strFreezeDesign) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strFreezeDesign) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strFreezeDesign)))
			{
				bFreezeDesign = true;
			}								
			if(UIUtil.isNotNullAndNotEmpty(strTransferControl) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strTransferControl) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strTransferControl)))
			{
				bTransferControl = true;
			}			
			if(UIUtil.isNotNullAndNotEmpty(strRetainSubstitutes) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strRetainSubstitutes) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strRetainSubstitutes)))
			{
				bRetainSubstitutes = true;
			}	
			if(UIUtil.isNotNullAndNotEmpty(strAddToBookmark) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strAddToBookmark) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strAddToBookmark)))
			{
				bAddToBookmark = true;
			}
						
			dPartObj = DomainObject.newInstance(context,strPartObjID);							
			
			Map mapExtendedBOMInput = new HashMap();
			//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Starts
			String sPreviousAPPId = DomainConstants.EMPTY_STRING;
			boolean bPreviousAPPRevExists = false;
			BusinessObject boPreviousRevision= dPartObj.getPreviousRevision(context);
			if(null!=boPreviousRevision && boPreviousRevision.exists(context))
			{
				bPreviousAPPRevExists = true;
				sPreviousAPPId = boPreviousRevision.getObjectId(context);
			}
			mapProduct.put(pgApolloConstants.KEY_PREVIOUSOBJECTID, sPreviousAPPId);
			mapExtendedBOMInput.put(pgApolloConstants.KEY_PREVIOUSOBJECTID, sPreviousAPPId);
			//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Ends				
			
			VPLMIntegTraceUtil.trace(context, "MassCollab: BEFORE getRelatedRawMaterials >>> ");
			updateTimeStampDetails(GET_PREV_REV_DETAILS);
			//----------------------------------------------------------------------------------------
			//5. Get Existing EBOM Children connected to APP
			mlConnectedMaterialData = getRelatedRawMaterials(context,dPartObj,objectSelects, relSelects);							
			VPLMIntegTraceUtil.trace(context, "MassCollab: mlConnectedMaterialData " + mlConnectedMaterialData );
			//----------------------------------------------------------------------------------------
			updateTimeStampDetails(GET_EBOM_RMP_END);
			//----------------------------------------------------------------------------------------
			//6. Create and Update EBOM connections
			strReturnMsg = connectDisconnectEBOMAndUpdateAttributes(context,mlStacking,mlConnectedMaterialData,dPartObj,mpCatiaEnoviaUOMMap,bRetainSubstitutes,strEBOMAttributeList, mapExtendedBOMInput);
			VPLMIntegTraceUtil.trace(context, "MassCollab: connectDisconnectEBOMAndUpdateAttributes strReturnMsg = " + strReturnMsg );
			//----------------------------------------------------------------------------------------
							
			updateTimeStampDetails(EBOM_GENERATION);

			if(strReturnMsg.startsWith(pgApolloConstants.STR_SUCCESS))
			{
				String sSubstituteUpdate = DomainConstants.EMPTY_STRING;
				
				slReturnMsgList = StringUtil.split(strReturnMsg, pgApolloConstants.CONSTANT_STRING_PIPE);
				
				if(slReturnMsgList.size() > 1)
				{
					sSubstituteUpdate  = slReturnMsgList.get(1);
				}

				loggerSync.debug( " EBOM Updated Successfully. {}", strReturnMsg);
				VPLMIntegTraceUtil.trace(context, "MassCollab: EBOM Updated Successfully. "+strReturnMsg );
				
				strHistory = pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP + pgApolloConstants.STR_SUCCESS_EBOM; 
				addCustomHistoryOnSync(context,dPartObj.getObjectId(), strHistory);
				
				slSuccessMsg.add(pgApolloConstants.STR_SUCCESS_EBOM);
				
				if(pgApolloConstants.STR_NO_FLAG.equalsIgnoreCase(sSubstituteUpdate))
				{
					slSuccessMsg.add(pgApolloConstants.STR_WARNING_SUBSTITUTE_CALCULATION);
				}
				
			}							
			if(strReturnMsg.contains(pgApolloConstants.STR_ERROR))
			{
				loggerSync.debug( " Error during connectDisconnectEBOMAndUpdateAttributes >> {}" , strReturnMsg );
				VPLMIntegTraceUtil.trace(context, "MassCollab: " + strReturnMsg );								
				outputMap.put(pgApolloConstants.STR_ERROR, strReturnMsg);
				outputMap.put(pgApolloConstants.STR_SUCCESS, slSuccessMsg);
				return outputMap;				
			}		
			
			loggerSync.debug( "EBOM Update completed successfully... ");
			
			//----------------------------------------------------------------------------------------							
			boolean isUpdateStatusInProgress = false;
			try 
			{
				//7-1. Evaluate Criteria for APP
				String strCriteriaDuringSync = EnoviaResourceBundle.getProperty(context,"emxEngineeringCentral.pgLayeredProduct.evaluateCriteriaDuringMassCollaboration");
				
				String strJobTitle = new StringBuilder(pgApolloConstants.STRING_EVALUATE_CRITERIA_JOB_TITLE).append(strPartObjID).toString();
				abortAndDeletePreviousBackgroundJob(context, strJobTitle);

				dPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS);
				isUpdateStatusInProgress = true;
				
				if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strCriteriaDuringSync))
				{	
					// EVALUATE CRITERIA STARTS		
					String[] strArgs = new String[]{strPartObjID};
					updateTimeStampDetails(EVALUATE_CRITERIA_START);
					JPO.invoke(context, "pgDSMLayeredProductSyncUtil", strArgs, "evaluateCriteria", strArgs);								
					// EVALUATE CRITERIA ENDS
					updateTimeStampDetails(EVALUATE_CRITERIA_END);
				}
				//----------------------------------------------------------------------------------------
				VPLMIntegTraceUtil.trace(context, "MassCollab: BEFORE processAndUpdateCharacteristics >>> ");
				//----------------------------------------------------------------------------------------
				//7-2. Create and Update Characteristics
				Map returnMapChar = processAndUpdateCharacteristics(context,mapProduct,strPartObjID,dPartObj,bPreviousAPPRevExists, DomainConstants.EMPTY_STRING);
				
				updateTimeStampDetails(UPDATE_CHARACTERISTICS);
				
				strReturnMsg = (String)returnMapChar.get(pgApolloConstants.KEY_MESSAGE);
				//----------------------------------------------------------------------------------------
				VPLMIntegTraceUtil.trace(context, "MassCollab: AFTER processAndUpdateCharacteristics >>> ");
								
				if(strReturnMsg.contains(pgApolloConstants.STR_SUCCESS))
				{	
					validateAndSetPartCharacteristicsStatusCompleted(context, dPartObj);
					isUpdateStatusInProgress = false;
					strHistory = strHistoryCollatrateWithAPP + pgApolloConstants.STR_SUCCESS_CHARACTERISTIC;
					addCustomHistoryOnSync(context, strPartObjID , strHistory);	

					slSuccessMsg.addElement(pgApolloConstants.STR_SUCCESS_CHARACTERISTIC);

				}						
				else if(strReturnMsg.contains(pgApolloConstants.STR_ERROR))
				{
					dPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);			
					isUpdateStatusInProgress = false;
					
					sbErrorMessage.append(strReturnMsg);
					sbErrorMessage.append(". ");
					sbErrorMessage.append(pgApolloConstants.STR_ERROR_CHARACTERISTICSERROR);								
					VPLMIntegTraceUtil.trace(context, "MassCollab: Error during processAndUpdateCharacteristics >> " + sbErrorMessage.toString());
					loggerSync.debug( " Error during processAndUpdateCharacteristics >> {}" , sbErrorMessage);
				
					outputMap.put(pgApolloConstants.STR_ERROR, sbErrorMessage.toString());
					outputMap.put(pgApolloConstants.STR_SUCCESS, slSuccessMsg);
					return outputMap;
				}							
				VPLMIntegTraceUtil.trace(context, "MassCollab: After processAndUpdateCharacteristics >> "  );
				loggerSync.debug( " Characteristics are successfully processed. ");
				
			} 
			catch (Exception e) 
			{
				updateTimeStampDetails(UPDATE_CHARACTERISTICS);
				loggerSync.debug( "Error in Characteristics Evaluate Criteria and Update :{}", e.getLocalizedMessage());
				dPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);			
				isUpdateStatusInProgress = false;
				loggerApolloTrace.error(e.getMessage() ,e);
				sbErrorMessage = new StringBuilder();
				sbErrorMessage.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
				sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_DOT);
				sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_SPACE);								
				sbErrorMessage.append(pgApolloConstants.STR_ERROR_CHARACTERISTICSERROR);								
				outputMap.put(pgApolloConstants.STR_ERROR, sbErrorMessage.toString());
				outputMap.put(pgApolloConstants.STR_SUCCESS, slSuccessMsg);
				return outputMap;				
			}
			finally {
				if(isUpdateStatusInProgress) {
					dPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
				}					

			}
						
			//7-3. Add to Bookmark
			if(bAddToBookmark)
			{
				strReturnMsg = addToBookMarkInCollaborateWithAPP(context, mapProduct, strPartObjID);
				updateTimeStampDetails(ADD_BOOKMARK);

				if(strReturnMsg.contains(pgApolloConstants.STR_ERROR))
				{
					loggerSync.debug( "Error while Adding Part to Bookmark: {}" , strReturnMsg );
					VPLMIntegTraceUtil.trace(context, "MassCollab: " + strReturnMsg );								
													
					strHistory = strHistoryCollatrateWithAPP + strReturnMsg;
					addCustomHistoryOnSync(context,strPartObjID, strHistory);	
					
					outputMap.put(pgApolloConstants.STR_ERROR, strReturnMsg);
					outputMap.put(pgApolloConstants.STR_SUCCESS, slSuccessMsg);
					return outputMap;
				}
				else
				{
					strHistory = new StringBuilder().append(strHistoryCollatrateWithAPP).append(pgApolloConstants.STR_SUCCESS_ADD_TO_BOOKMARK).toString();
					addCustomHistoryOnSync(context, strPartObjID , strHistory);					
					slSuccessMsg.addElement(pgApolloConstants.STR_SUCCESS_ADD_TO_BOOKMARK);					
				}
			}
			
			VPLMIntegTraceUtil.trace(context, "MassCollab: bFreezeDesign >> " + bFreezeDesign );
			VPLMIntegTraceUtil.trace(context, "MassCollab: bTransferControl >> " + bTransferControl );
			//For Freeze Design starts
			
			if(bFreezeDesign || bTransferControl)
			{		
				if(bDrawingUpdateStatusFlag)
				{
					strReturnMsg = freezeDesignAndTransferControl (context, strVPMReferenceObjId,strPartObjID, bTransferControl, bFreezeDesign);
				}
				else
				{
					strReturnMsg = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_DRAWING_UPDATE).toString();
					strHistory = new StringBuilder(strHistoryCollatrateWithAPP).append(pgApolloConstants.EBOM_SYNC_HISTORY_ERROR_TRANSFER_CONTROL).append(pgApolloConstants.STR_ERROR_DRAWING_UPDATE).toString();
					addCustomHistoryOnSync(context,strPartObjID, strHistory);
				}
				
				updateTimeStampDetails(TRANSFER_CONTROL);

				if(strReturnMsg.contains(pgApolloConstants.STR_ERROR))
				{
					loggerSync.debug( "Error while Freeze Design and Transfer Control : {}" , strReturnMsg );
					outputMap.put(pgApolloConstants.STR_ERROR, strReturnMsg);
					outputMap.put(pgApolloConstants.STR_SUCCESS, slSuccessMsg);
					return outputMap;
				}
				else
				{
					StringBuffer sbHistory = new StringBuffer();
					sbHistory.append(strHistoryCollatrateWithAPP);
					if(bTransferControl)
					{
						sbHistory.append(pgApolloConstants.STR_SUCCESS_DESIGN_FREEZE_TRANSFER_CONTROL);
						slSuccessMsg.addElement(pgApolloConstants.STR_SUCCESS_DESIGN_FREEZE_TRANSFER_CONTROL);
					}
					else
					{
						sbHistory.append(pgApolloConstants.STR_SUCCESS_DESIGN_FREEZE);
						slSuccessMsg.addElement(pgApolloConstants.STR_SUCCESS_DESIGN_FREEZE);
					}										
					addCustomHistoryOnSync(context,strPartObjID, sbHistory.toString());
				}
			}
		}			
		catch (Exception e)
		{
			loggerApolloTrace.error(e.getMessage() ,e);
			sbErrorMessage.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
		}		
		strErrorMessage = sbErrorMessage.toString();
		if(!strErrorMessage.isEmpty())
		{
			outputMap.put(pgApolloConstants.STR_ERROR, strErrorMessage);
		}
		outputMap.put(pgApolloConstants.STR_SUCCESS, slSuccessMsg);
		
		return outputMap;
	}

	
	/**
	 * Method to Freeze Design and Transfer Control
	 * @param context
	 * @param strVPMReferenceObjId
	 * @param strPartObjID
	 * @param bTransferControl
	 * @param bFreezeDesign
	 * @return
	 * @throws Exception
	 */
	private String freezeDesignAndTransferControl (matrix.db.Context context, String strVPMReferenceObjId, String strPartObjID, boolean bTransferControl, boolean bFreezeDesign) throws Exception
	{
		boolean bError = false;
		boolean isContextPushed = false;
		boolean isSetEnv = false;
		StringBuffer sbErrorMessage = new StringBuffer();
		String strHistory = DomainConstants.EMPTY_STRING;
		String strVPMRefName = DomainConstants.EMPTY_STRING;
		String strVPMRefRev = DomainConstants.EMPTY_STRING;
		String strHistoryCollatrateWithAPP = pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP;
		String strEnv = pgApolloConstants.EBOM_SYNC_ENV_TRANSFER_CONTROL + strVPMReferenceObjId;
		String sVPMRefModelType;
		try
		{				
			//APOLLO 2018x.3 ALM 32659 - User should not able to transfer control to CATIA APP using Collaborate with EBOM command - start
			if(bTransferControl && !bFreezeDesign)
			{
				bError = true;
				sbErrorMessage.append(pgApolloConstants.STR_ERROR);
				sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
				sbErrorMessage.append(pgApolloConstants.STR_ERROR_TRANSFER_CONTROL_WITHOUT_FREEZE_DESIGN);
				loggerSync.debug( "{}" , sbErrorMessage);
				VPLMIntegTraceUtil.trace(context, "MassCollab: " + sbErrorMessage.toString());
				
				strHistory = strHistoryCollatrateWithAPP + sbErrorMessage.toString() ;
				addCustomHistoryOnSync(context,strPartObjID, strHistory);
			}
			else
			{
			//APOLLO 2018x.3 ALM 32659 - User should not able to transfer control to CATIA APP using Collaborate with EBOM command - end
				DomainObject domVPMRefObj = DomainObject.newInstance(context, strVPMReferenceObjId);
				StringList slObjectSelect = new StringList(3);
				slObjectSelect.addElement(DomainConstants.SELECT_NAME);
				slObjectSelect.addElement(DomainConstants.SELECT_REVISION);
				slObjectSelect.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);

				Map mapVPMRefInfo = domVPMRefObj.getInfo(context, slObjectSelect);
				strVPMRefName = (String)mapVPMRefInfo.get(DomainConstants.SELECT_NAME);
				strVPMRefRev = (String)mapVPMRefInfo.get(DomainConstants.SELECT_REVISION);
				sVPMRefModelType = (String)mapVPMRefInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);

				/*
				if(bFreezeDesign)
				{								
					freezeVPMReference (context, strVPMReferenceObjId);								
					loggerSync.debug( " VPM reference Successfully promoted to Frozen State.");
					VPLMIntegTraceUtil.trace(context, "MassCollab: VPM reference Successfully promoted to Frozen State." );
				}*/
				if(bTransferControl)
				{
					VPLMIntegTraceUtil.trace(context, "MassCollab: Before Transfer Control >> " );				
					
					//Context user won't always have access to transfer control. So push context is needed here.
					ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, null, context.getVault().getName());
					isContextPushed = true;
					
					//APOLLO 2018x.3 ALM 32659 - User should not able to transfer control to CATIA APP using Collaborate with EBOM command - start
					MqlUtil.mqlCommand(context, "set env global $1  $2 ;", new String[]{strEnv , pgApolloConstants.STR_TRUE_FLAG});
					isSetEnv = true;
					//APOLLO 2018x.3 ALM 32659 - User should not able to transfer control to CATIA APP using Collaborate with EBOM command - end
					
					if(pgApolloConstants.RANGE_VALUE_GENERIC.equalsIgnoreCase(sVPMRefModelType))
					{
						domVPMRefObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PG_CADDESIGN_ORIGINATION, pgApolloConstants.RANGE_CADDESIGN_ORIGINATION_AUTOMATION);
						loggerSync.debug( "Generic Model CAD Design Origination set to Automation");
					}
					
					domVPMRefObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_ISVPLMCONTROLLED, "FALSE");				
					
					loggerSync.debug( " Control is transferred to ENOVIA successfully. ");
					VPLMIntegTraceUtil.trace(context, "MassCollab: After Transfer Control >> " );
				}
			}
		}
		catch(Exception ex)
		{
			bError = true;
			sbErrorMessage.append(pgApolloConstants.STR_ERROR);
			sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
			sbErrorMessage.append(pgApolloConstants.STR_ERROR_FREEZE_DESIGN_TRANSFER_CONTROL.replace("<VPMREF_NAME>", strVPMRefName).replace("<VPMREF_REV>", strVPMRefRev));						
			loggerApolloTrace.error(ex.getMessage() ,ex);
			if(UIUtil.isNotNullAndNotEmpty(strPartObjID))
			{
				strHistory = strHistoryCollatrateWithAPP + pgApolloConstants.EBOM_SYNC_HISTORY_ERROR_TRANSFER_CONTROL + ex.getLocalizedMessage();
				addCustomHistoryOnSync(context,strPartObjID, strHistory);
			}										
			loggerSync.error( "Error in freezeDesignAndTransferControl {}" , pgApolloConstants.EBOM_SYNC_HISTORY_ERROR_TRANSFER_CONTROL + ex.getLocalizedMessage() );
		}
		finally
		{
			//APOLLO 2018x.3 ALM 32659 - User should not able to transfer control to CATIA APP using Collaborate with EBOM command - start
			if(isSetEnv)
			{
				MqlUtil.mqlCommand(context, "unset env global $1 ;", new String[]{strEnv});
				isSetEnv = false;
			}
			//APOLLO 2018x.3 ALM 32659 - User should not able to transfer control to CATIA APP using Collaborate with EBOM command - end
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
		
		return sbErrorMessage.toString();
	}
	

	
	/**
	 * Method to add Bookmark to APP
	 * @param context
	 * @param mapProduct
	 * @param strPartObjID
	 * @return
	 * @throws IOException
	 */
	private String addToBookMarkInCollaborateWithAPP(matrix.db.Context context,  Map mapProduct, String strPartObjID)
	{		
		StringBuilder sbReturnMessage = new StringBuilder();
		String strFolderId = DomainConstants.EMPTY_STRING;
		StringList slSelectable = new StringList(2);
		slSelectable.addElement(DomainConstants.SELECT_ID);
		try
		{
			loggerSync.debug( " Add To BookMark Started {}", strPartObjID);

			if(mapProduct.containsKey(pgApolloConstants.KEY_BOOKMARKID))
			{
				strFolderId = (String)mapProduct.get(pgApolloConstants.KEY_BOOKMARKID);
			}
			if(UIUtil.isNotNullAndNotEmpty(strFolderId))
			{
				StringBuilder sbWhereClause = new StringBuilder();
				sbWhereClause.append(DomainConstants.SELECT_ID).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append("'").append(strFolderId).append("'");			
				DomainObject domAPPObject = DomainObject.newInstance(context, strPartObjID);	
				Pattern typePattern = new Pattern(DomainConstants.TYPE_WORKSPACE_VAULT);
				MapList mlConnectedFolders = domAPPObject.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_VAULTED_OBJECTS_REV2,
						typePattern.getPattern(),
						slSelectable,//Object Select
						null,//rel Select
						true,//get To
						false,//get From
						(short)1,//recurse level
						sbWhereClause.toString(),//object where Clause
						null,//rel where clause
						0); //object limit
				loggerSync.debug( " mlConnectedFolders : {}" , mlConnectedFolders);
				if(null == mlConnectedFolders || mlConnectedFolders.isEmpty())
				{
					StringList slPartIDList = new StringList();
					slPartIDList.add(strPartObjID);	
					connectProductDataToFolder(context, slPartIDList, strFolderId);
				}				
			}
		}
		catch (Exception e) 
		{
			loggerApolloTrace.error(e.getMessage() ,e);
			loggerSync.error( " ERROR in addToBookMarkInCollaborateWithAPP: {}" , e.getLocalizedMessage());
			sbReturnMessage.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_ADDToBOOKMARK).append(e.getLocalizedMessage());
		}

		return sbReturnMessage.toString();
	}

	/**
	 * Method to publish Design
	 * @param context
	 * @param mapProduct
	 * @return
	 * @throws IOException
	 * @throws FrameworkException
	 */
	private String publishDesignParametersInCollaborateWithAPP(matrix.db.Context context, Map mapProduct) throws Exception 
	{
		StringBuilder sbReturnMessage = new StringBuilder();
		Map mapDesignParam = new HashMap();
		boolean isContextPushed = false;
		String strOutput;	
		if(mapProduct.containsKey(pgApolloConstants.KEY_DESIGNPARAM))
		{
			mapDesignParam = (Map)mapProduct.get(pgApolloConstants.KEY_DESIGNPARAM);
		}	
		try 
		{
			String strStartTime =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerSync.debug( " Checkout and Update XML Started : {}" ,strStartTime );
			//Push context is needed to check in Design Parameter file. Context user will not always get access to checkin into Part. 
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;
			strOutput  = ReadWriteXMLForPLMDTDocument.updateConfigFile(context, mapDesignParam);
			sbReturnMessage.append(strOutput);
			loggerSync.debug( "Checkout and Update XML Ended-----------------");
			loggerSync.debug( " Final Response   {}", sbReturnMessage);			
		} 
		catch (Exception e) 
		{
			loggerSync.error( " ERROR in publishDesignParametersInCollaborateWithAPP : {}" , e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage() ,e);
			sbReturnMessage.append(pgApolloConstants.STR_ERROR_PUBLISH_DESIGN_PARAM).append(e.getLocalizedMessage());
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		
		return sbReturnMessage.toString();
	}	
	
	/**
	 * Method to connect Part To bookmark	
	 * @param context
	 * @param slPartIDList
	 * @param strFolderId
	 * @throws FrameworkException
	 */
	public void connectProductDataToFolder(matrix.db.Context context,StringList slPartIDList, String strFolderId) throws MatrixException {
		DomainObject doFolder = DomainObject.newInstance(context,strFolderId);
		DomainRelationship.connect(context, doFolder, DomainConstants.RELATIONSHIP_VAULTED_OBJECTS_REV2,true,slPartIDList.toArray(new String[slPartIDList.size()]));
	}

	//Validate VPMReference Product and corresponding Enterprise Part
	
	/**
	 * This Method will Validate VPM reference Product and its corresponding Enterprise Part
	 * @param context
	 * @param strVPMRefId
	 * @param productName
	 * @param productRev
	 * @param selectables
	 * @return
	 * @throws MatrixException
	 */
	public String validateProductAndUpdateAPP(matrix.db.Context context, String strVPMRefId, String productName, String productRev, StringList selectables, boolean validateModelUpdateStatus) throws MatrixException{
		String strPartObjID = null;
		try{
			StringBuffer strReturnMessageBuf = new StringBuffer();
			String strPartCurrentState = null;
			String strPartName = null;
				DomainObject domVPMRefObj = DomainObject.newInstance(context,strVPMRefId);
				
				String strVPMRefCurrent = domVPMRefObj.getInfo(context, DomainConstants.SELECT_CURRENT);
				
				if(strVPMRefCurrent.equals(pgApolloConstants.STATE_SHARED) || strVPMRefCurrent.equals(pgApolloConstants.STATE_OBSOLETE_CATIA)) {
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
					strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_VPMREFERENCESTATE.replace("<VPMREF_NAME>", productName));
					strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
					return strReturnMessageBuf.toString();	
				}

				//Fetch the connected Enterprise Part
				MapList mlPartData = domVPMRefObj.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_PART_SPECIFICATION,
						pgApolloConstants.TYPE_PGASSEMBLEDPRODUCTPART,
						selectables,	//Object Select
						null,			//rel Select
						true,			//get To
						false,			//get From
						(short)1,		//recurse level
						null,			//where Clause
						null,
						0);
				VPLMIntegTraceUtil.trace(context, "MassCollab: Enterprise Part details connected to VPMReference =  mlPartData = " + mlPartData );
				
				if(null == mlPartData || mlPartData.isEmpty())
				{
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
					strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_APPNOTEXIST.replace("<VPMREF_NAME>", productName).replace("<VPMREF_REV>", productRev));
					strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
					return strReturnMessageBuf.toString();
				}
				else if (mlPartData.size()>1)
				{
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
					strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_MULTIAPPEXIST.replace("<VPMREF_NAME>", productName).replace("<VPMREF_REV>", productRev));
					return strReturnMessageBuf.toString();
				} 
				else 
				{					
					Map mpPartInfo = (Map) mlPartData.get(0);	
					strPartCurrentState = (String) mpPartInfo.get(DomainConstants.SELECT_CURRENT); 
					strPartObjID = (String) mpPartInfo.get(DomainConstants.SELECT_ID);
	
					loggerSync.debug( " APP Part Details : {} | {}" , mpPartInfo.get(DomainConstants.SELECT_NAME) , strPartObjID );
					
					if(!strPartCurrentState.equals(DomainConstants.STATE_PART_PRELIMINARY)) {
						strPartName = (String) mpPartInfo.get(DomainConstants.SELECT_NAME);
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
						strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_APPSTATECHECK.replace("<APP_NAME>", strPartName));
						strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
						return strReturnMessageBuf.toString();
					}

					//Check is previous revision of APP is not released.					
					DomainObject domPart = DomainObject.newInstance(context, strPartObjID);
					
					String sLPDModelUpdateStatus = domPart.getInfo(context, pgApolloConstants.SELECT_ATTRIBUTE_PG_LPD_MODEL_UPDATE_STATUS);
					if(validateModelUpdateStatus && pgApolloConstants.RANGE_PG_LPD_MODEL_UPDATE_STATUS_MANDATORY.equals(sLPDModelUpdateStatus)) {						
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
						strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_MANDATORYMODELUPDATESTATUS.replace("<VPMREF_NAME>", productName).replace("<VPMREF_REV>", productRev));
						strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);
						strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
						return strReturnMessageBuf.toString();
					}					
					
					String strPreviousRevId = domPart.getPreviousRevision(context).getObjectId(context);
					if(UIUtil.isNotNullAndNotEmpty(strPreviousRevId)){
						domPart = DomainObject.newInstance(context, strPreviousRevId);
						Map previousRevInfoMap =  domPart.getInfo(context, selectables);
						String strPrevRevCurrentState = (String)previousRevInfoMap.get(DomainConstants.SELECT_CURRENT);
						
						if(!strPrevRevCurrentState.equals(DomainConstants.STATE_PART_RELEASE)) {
							String strPreviousRevPartName = (String) previousRevInfoMap.get(DomainConstants.SELECT_NAME);
							strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
							strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
							strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_PREVIOUSREVAPPSTATECHECK.replace("<APP_NAME>", strPreviousRevPartName));
							strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SPACE);
							strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
							return strReturnMessageBuf.toString();
						}
					}					
				}				
		} catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return strPartObjID;
	}
		
	/**
	 * Method to Validate Gross Length, Gross Volumn, Gross Area parameters
	 * @param mlStacking
	 * @return
	 * @throws Exception
	 */
	private MapList validateGrossVolumeAreaLengthParameters (MapList mlStacking)
	{
		MapList mlReturn = new MapList();
		if(null != mlStacking && !mlStacking.isEmpty())
		{
			Map mpMaterial;
			StringList slError;
			String strBaseUOM;
			String strUniqueKey;
			Iterator  materialListItr = mlStacking.iterator();
			while(materialListItr.hasNext())
			{				
				mpMaterial = (Map)materialListItr.next();
				slError = new StringList();
				if(mpMaterial.containsKey(pgApolloConstants.STR_ERROR_LIST))
				{
					slError = (StringList) mpMaterial.get(pgApolloConstants.STR_ERROR_LIST);
				}
				strUniqueKey = (String) mpMaterial.get(pgApolloConstants.KEY_UNIQUEKEY);
				if(mpMaterial.containsKey(pgApolloConstants.ATTRIBUTE_PGBASEUOM))
				{
					strBaseUOM = (String) mpMaterial.get(pgApolloConstants.ATTRIBUTE_PGBASEUOM);
					if(pgApolloConstants.STR_UOM_SQUARE_METER.equalsIgnoreCase(strBaseUOM))
					{
						if(!mpMaterial.containsKey(pgApolloConstants.KEY_GROSS_AREA) && !mpMaterial.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))
						{
							slError.addElement(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", pgApolloConstants.KEY_GROSS_AREA));
						}
						if(!mpMaterial.containsKey(pgApolloConstants.KEY_NET_AREA) && !mpMaterial.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY))
						{
							slError.addElement(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", pgApolloConstants.KEY_NET_AREA));
						}
					}
					else if (pgApolloConstants.STR_UOM_LITER.equalsIgnoreCase(strBaseUOM) || pgApolloConstants.STR_UOM_CUBIC_METER.equalsIgnoreCase(strBaseUOM))
					{
						
						if(!mpMaterial.containsKey(pgApolloConstants.KEY_GROSS_VOLUME) && !mpMaterial.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))
						{
							slError.addElement(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", pgApolloConstants.KEY_GROSS_VOLUME));
						}
						if(!mpMaterial.containsKey(pgApolloConstants.KEY_NET_VOLUME) && !mpMaterial.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY))
						{
							slError.addElement(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", pgApolloConstants.KEY_NET_VOLUME));
						}						
					}
					else if (pgApolloConstants.STR_UOM_METER.equalsIgnoreCase(strBaseUOM))
					{
						
						if(!mpMaterial.containsKey(pgApolloConstants.KEY_GROSS_LENGTH) && !mpMaterial.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))
						{
							slError.addElement(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", pgApolloConstants.KEY_GROSS_LENGTH));
						}
						if(!mpMaterial.containsKey(pgApolloConstants.KEY_NET_LENGTH) && !mpMaterial.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY))
						{
							slError.addElement(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", pgApolloConstants.KEY_NET_LENGTH));
						}					
					}
				}
				if(!slError.isEmpty())
				{
					mpMaterial.put(pgApolloConstants.STR_ERROR_LIST,slError);
				}
				mlReturn.add(mpMaterial);
			}			
		}		
		return mlReturn;
	}
	
	/**
	 * Method to consolidate all Error Messages
	 * @param mlStacking
	 * @return
	 * @throws Exception
	 */
	private StringList consolidateAllErrorMessages (MapList mlStacking)
	{
		StringList slAllErrors = new StringList();
		if(null != mlStacking && !mlStacking.isEmpty())
		{
			Map mpMaterial;
			Iterator  materialListItr = mlStacking.iterator();
			while(materialListItr.hasNext())
			{				
				mpMaterial = (Map)materialListItr.next();
				if(mpMaterial.containsKey(pgApolloConstants.STR_ERROR_LIST))
				{
					slAllErrors.addAll((StringList) mpMaterial.get(pgApolloConstants.STR_ERROR_LIST));
				}
			}
		}		
		return slAllErrors;		
	}

	/**
	 * This method will Get Raw Materials connected to Part
	 * @param context
	 * @param partObj
	 * @param objectSelects
	 * @param relSelects
	 * @return
	 * @throws Exception
	 */

	public MapList getRelatedRawMaterials(matrix.db.Context context, DomainObject partObj, StringList objectSelects, StringList relSelects) throws MatrixException{

		MapList mlRelatedMaterial = new MapList();
		objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGBASEUOM);
		
		relSelects.add("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id");
		relSelects.add("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].to.id");
		relSelects.add("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].to." + pgApolloConstants.SELECT_ATTRIBUTE_PGBASEUOM);
		relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_WEBWIDTH);

		//Check whether the APP is  connected to any Raw Material
		mlRelatedMaterial = partObj.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_EBOM,
				DomainConstants.QUERY_WILDCARD,
				objectSelects,//Object Select
				relSelects,//rel Select
				false,//get To
				true,//get From
				(short)1,//recurse level
				null,//where Clause
				null,
				0);	

		return mlRelatedMaterial;
	}

	
	
	/**
	 * Method to connect and disconnect EBOM, update attributes
	 * @param context
	 * @param mlStacking
	 * @param mlConnectedRawMaterialData
	 * @param dPartObj
	 * @param catiaEnoviaUOMMap
	 * @param bRetainSubstitutes
	 * @param strEBOMRelSchemaAttrList
	 * @param mapExtendedEBOMInput
	 * @return
	 * @throws Exception
	 */
	public String connectDisconnectEBOMAndUpdateAttributes(matrix.db.Context context, MapList mlStacking,MapList mlConnectedRawMaterialData,DomainObject dPartObj,Map catiaEnoviaUOMMap, boolean bRetainSubstitutes, StringList strEBOMRelSchemaAttrList, Map mapExtendedEBOMInput) throws Exception {

		VPLMIntegTraceUtil.trace(context, "MassCollab: INSIDE connectDisconnectEBOMAndUpdateAttributes >> ");
		VPLMIntegTraceUtil.trace(context, "MassCollab: mlConnectedRawMaterialData = " + mlConnectedRawMaterialData );

		String strStatus = pgApolloConstants.STR_SUCCESS;
		String sSubstituteUpdate = DomainConstants.EMPTY_STRING;
		String sSubstituteUpdateStatus = DomainConstants.EMPTY_STRING;
		StringList slReturnMsgList;
		
		StringList objectSelects = new StringList(4);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_CURRENT);
		objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGBASEUOM);
		
		boolean bPreviousAPPRevExists = false;
		String sPreviousAPPId = (String)mapExtendedEBOMInput.get(pgApolloConstants.KEY_PREVIOUSOBJECTID);
		if(UIUtil.isNotNullAndNotEmpty(sPreviousAPPId))
		{
			bPreviousAPPRevExists = true;			
		}
		
		boolean isContextPushed = false;		
		
		Map mapExtendedSubstituteInput = new HashMap();		
		Map mapPreviousAPPEBOM = new HashMap();
		Map mapPreviousAPPEBOMSubstitute = new HashMap();

		try 
		{			
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;		
			VPLMIntegTraceUtil.trace(context, "MassCollab:RMP Data sent from CATIA materialList = " + mlStacking );			
			
			//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Starts
			StringList slEBOMChgAttribute = new StringList();
			StringList slEBOMSubstituteChgAttribute = new StringList();
			String sChgAttributeList = DomainConstants.EMPTY_STRING;
			if(bPreviousAPPRevExists)
			{
				sChgAttributeList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloServices.CollaborateWithAPP.EBOMChgAttributeList");
				if(UIUtil.isNotNullAndNotEmpty(sChgAttributeList))
				{
					slEBOMChgAttribute	= StringUtil.split(sChgAttributeList, pgApolloConstants.CONSTANT_STRING_COMMA);
				}
				sChgAttributeList = DomainConstants.EMPTY_STRING;
				sChgAttributeList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloServices.CollaborateWithAPP.EBOMSubstituteChgAttributeList");
				if(UIUtil.isNotNullAndNotEmpty(sChgAttributeList))
				{
					slEBOMSubstituteChgAttribute= StringUtil.split(sChgAttributeList, pgApolloConstants.CONSTANT_STRING_COMMA);
				}
				
				Map mapPreviousAPPInfo = getAPPEBOMAndEBOMSubstituteInfo(context, sPreviousAPPId, slEBOMChgAttribute, slEBOMSubstituteChgAttribute, pgApolloConstants.STR_ALL);
				
				updateTimeStampDetails(GET_PREVIOUS_REV_EBOM_SUB_END);
				
				mapPreviousAPPEBOM = (Map)mapPreviousAPPInfo.get(DomainConstants.RELATIONSHIP_EBOM);
				mapPreviousAPPEBOMSubstitute = (Map)mapPreviousAPPInfo.get(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE);
				
				mapExtendedEBOMInput.put(DomainConstants.RELATIONSHIP_EBOM, mapPreviousAPPEBOM);
				mapExtendedSubstituteInput.put(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE, mapPreviousAPPEBOMSubstitute);
				
			}
			mapExtendedEBOMInput.put(pgApolloConstants.KEY_EBOMATTRIBUTELIST, strEBOMRelSchemaAttrList);
			mapExtendedEBOMInput.put(pgApolloConstants.KEY_EBOMCHGATTRIBUTELIST, slEBOMChgAttribute);
			mapExtendedSubstituteInput.put(pgApolloConstants.KEY_EBOMSUBSTITUTE_CHGATTRIBUTELIST, slEBOMSubstituteChgAttribute);
			//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Ends		
			

			HashMap<String, String> tempMaterialInfoMap = null;
			Map materialInfoMap = new HashMap();
			String strRawMaterialName = null;
			String strMaterialBaseUOM = null;

			StringList slObjectSelects = new StringList(2);
			slObjectSelects.add(DomainConstants.SELECT_ID);
			slObjectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGBASEUOM);			
			slObjectSelects.add(DomainConstants.SELECT_NAME);
			slObjectSelects.add(DomainConstants.SELECT_REVISION);
			slObjectSelects.add(DomainConstants.SELECT_CURRENT);
			
			Pattern typePattern = new Pattern(pgApolloConstants.TYPE_RAW_MATERIAL);
			typePattern.addPattern(pgApolloConstants.TYPE_PG_RAW_MATERIAL);			
		
			HashMap materialsMap = new HashMap();
			DomainObject domMaterialObj = null;
			DomainRelationship domEBOMRelObj = null;
			String strMaterialId = null;

			BusinessInterface biLayer = new BusinessInterface(pgApolloConstants.INTERFACE_LAYERPRODUCTINTERFACE, context.getVault());
			BusinessInterface biEBOMLayer = new BusinessInterface(pgApolloConstants.INTERFACE_DSMLAYERPRODUCTEBOMINTERFACE, context.getVault());

			Map mpEBOMRelMap = new HashMap();
			Map mpEBOMToRMPMap = new HashMap();
			Map mpEBOMToSubIdsMap = new HashMap();
			Map mpEBOMToSubRelIdsMap = new HashMap();
			Map mpEBOMWebWidthMap = new HashMap();
			StringList slSubIDs = new StringList();
			StringList slSubRelIDs = new StringList();
			StringList slSubRelToDelete = new StringList();
			Map connectedRMMap = null;
			String strRMId = null;
			String strEBOMRelId = null;
			String strPlyName = null;
			String strPlyGroupName = null;
			String strRMKey = null;
			StringBuffer sbRMKeyBuff = null;
			String sExistingWebWidth;
			
			StringList slUniqueGroupLayer = new StringList();
			StringList slRelToDelete = new StringList();
			
			Iterator itr = mlConnectedRawMaterialData.iterator();
			while(itr.hasNext()){
				sbRMKeyBuff = new StringBuffer();
				connectedRMMap = (Map)itr.next();
				strRMId = (String)connectedRMMap.get(DomainConstants.SELECT_ID);
				strEBOMRelId = (String)connectedRMMap.get(DomainRelationship.SELECT_ID);
				strPlyGroupName = (String)connectedRMMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
				strPlyName = (String)connectedRMMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
				sExistingWebWidth = (String)connectedRMMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_WEBWIDTH);
				sbRMKeyBuff.append(strPlyGroupName).append("|").append(strPlyName);
				strRMKey = sbRMKeyBuff.toString();				
				if(!pgApolloCommonUtil.containsInListCaseInsensitive(strRMKey,slUniqueGroupLayer))
				{
					slUniqueGroupLayer.addElement(strRMKey);
					mpEBOMRelMap.put(strRMKey,strEBOMRelId);
					mpEBOMToRMPMap.put(strRMKey, strRMId);				
					mpEBOMWebWidthMap.put(strRMKey, sExistingWebWidth);				
					if(connectedRMMap.containsKey("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id"))
					{
						slSubRelIDs = new StringList();
						slSubIDs = new StringList();
						slSubRelIDs = getStringList((Object)connectedRMMap.get("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id"));
						slSubIDs = getStringList((Object)connectedRMMap.get("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].to.id"));
						mpEBOMToSubIdsMap.put(strRMKey,slSubIDs);
						mpEBOMToSubRelIdsMap.put(strRMKey,slSubRelIDs);
					}
				}
				else
				{
					slRelToDelete.addElement(strEBOMRelId);
				}
			}
			
			VPLMIntegTraceUtil.trace(context, "MassCollab: Existing EBOM Map mpEBOMRelMap = " + mpEBOMRelMap );
			strPlyGroupName = null;
			strPlyName = null;
			strRMKey = null;
			strEBOMRelId = null;
			int iFindNumber = 0;
			
			//Iterate through MaterialList sent from CATIA
			Iterator<HashMap<String, String>> materialItr = mlStacking.iterator();
			while(materialItr.hasNext()) {
				iFindNumber++;
				sbRMKeyBuff = new StringBuffer();
				materialsMap = materialItr.next();
				strMaterialId = (String)materialsMap.get(DomainConstants.SELECT_ID);
				DomainObject domObj_RawMat = new DomainObject(strMaterialId);			
				strMaterialBaseUOM = (String)materialsMap.get(pgApolloConstants.ATTRIBUTE_PGBASEUOM);	
				strRMKey = (String)materialsMap.get(pgApolloConstants.KEY_UNIQUEKEY);
				VPLMIntegTraceUtil.trace(context, "MassCollab: strRMKey = " + strRMKey );	
				
				materialsMap.put(DomainConstants.ATTRIBUTE_FIND_NUMBER, Integer.toString(iFindNumber));				
				
				domEBOMRelObj = new DomainRelationship();
				if(mpEBOMRelMap.containsKey(strRMKey))
				{						
					VPLMIntegTraceUtil.trace(context, "MassCollab: mpEBOMRelMap conains strRMKey LOOP >> ");						
					//Update existing EBOM connection
					strEBOMRelId = (String)mpEBOMRelMap.get(strRMKey);
					domEBOMRelObj = DomainRelationship.newInstance(context, strEBOMRelId);
					
					if(!strMaterialId.equalsIgnoreCase((String)mpEBOMToRMPMap.get(strRMKey)))
					{
						DomainRelationship.setToObject(context, strEBOMRelId, domObj_RawMat);
					}
					slSubIDs = new StringList();
					slSubRelIDs = new StringList();
					slSubRelToDelete = new StringList();
					
					sExistingWebWidth = (String)mpEBOMWebWidthMap.get(strRMKey);					
					
					materialsMap.put(pgApolloConstants.KEY_OLD_WEBWIDTH, sExistingWebWidth);

					if(bRetainSubstitutes)
					{
						if(mpEBOMToSubRelIdsMap.containsKey(strRMKey))
						{
							slSubRelIDs = (StringList) mpEBOMToSubRelIdsMap.get(strRMKey);
							slSubIDs = (StringList) mpEBOMToSubIdsMap.get(strRMKey);
							//If Primary Material and Substitute is same then Disconnect Substitute Relationship
							if(null != slSubIDs && !slSubIDs.isEmpty())
							{
								for(int x=slSubIDs.size()-1 ; x >= 0 ; x--)
								{
									if(strMaterialId.equalsIgnoreCase(slSubIDs.get(x).toString()))
									{
										slSubRelToDelete.addElement(slSubRelIDs.get(x).toString());
										slSubIDs.remove(x);
										slSubRelIDs.remove(x);										
									}
								}
							}
						}
					}
					else
					{
						if(mpEBOMToSubRelIdsMap.containsKey(strRMKey))
						{
							slSubRelToDelete.addAll((StringList) mpEBOMToSubRelIdsMap.get(strRMKey));
						}
					}					
					if(null != slSubRelToDelete && !slSubRelToDelete.isEmpty())
					{						
						slRelToDelete.addAll(slSubRelToDelete);
					}					
					//Remove the RM from the Parent EBOMRel Map
					mpEBOMRelMap.remove(strRMKey);
				} 
				else 
				{						
					VPLMIntegTraceUtil.trace(context, "MassCollab: mpEBOMRelMap NOT conains strRMKey LOOP >> ");						
					domMaterialObj = DomainObject.newInstance(context,strMaterialId);
					//Create new EBOM connection
					domEBOMRelObj = DomainRelationship.connect(context, dPartObj, DomainConstants.RELATIONSHIP_EBOM, domMaterialObj);
					VPLMIntegTraceUtil.trace(context, "MassCollab: New EBOM Connected " );
					//Add interface
					domEBOMRelObj.addBusinessInterface(context, biLayer);
					domEBOMRelObj.addBusinessInterface(context, biEBOMLayer);				
									
				}				
				
				materialsMap = updateMaterialMapWithWebWidth(context, strMaterialId, materialsMap);
				materialsMap = calculateQuantity(context,materialsMap,catiaEnoviaUOMMap,strMaterialBaseUOM);
				//Query with UOM Changes ends
				VPLMIntegTraceUtil.trace(context, "MassCollab: calculateQuantity completed. " );
				
				mapExtendedSubstituteInput.put(pgApolloConstants.KEY_EBOMSUBSTITUTE_OBJECTLIST, slSubIDs);
				mapExtendedSubstituteInput.put(pgApolloConstants.KEY_EBOMSUBSTITUTE_RELIDLIST, slSubRelIDs);

				//Update Attributes
				strStatus = updateEBOMAttributes(context,materialsMap,domEBOMRelObj,strMaterialId, dPartObj.getObjectId(), bPreviousAPPRevExists, mapExtendedEBOMInput, mapExtendedSubstituteInput);
				if(strStatus.startsWith(pgApolloConstants.STR_SUCCESS))
				{					
					slReturnMsgList = StringUtil.split(strStatus, pgApolloConstants.CONSTANT_STRING_PIPE);
					
					if(slReturnMsgList.size() > 1)
					{
						sSubstituteUpdate  = slReturnMsgList.get(1);
						if(pgApolloConstants.STR_NO_FLAG.equalsIgnoreCase(sSubstituteUpdate))
						{
							sSubstituteUpdateStatus = sSubstituteUpdate;
						}
					}
				}
				if(strStatus.contains(pgApolloConstants.STR_ERROR)){																	
					return strStatus;
				}
				VPLMIntegTraceUtil.trace(context, "MassCollab: updateEBOMAttributes completed. "+strStatus );
			}

			//get the remaining EBOM connection which are not sent from CATIA
			strEBOMRelId = null;
			String strEBOMMapKey = null;
			Set<String> ebomMapSet = mpEBOMRelMap.keySet();
			Iterator<String> itrEBOMSet = ebomMapSet.iterator();
			while (itrEBOMSet.hasNext()) {
				strEBOMMapKey = itrEBOMSet.next();
				strEBOMRelId = (String)mpEBOMRelMap.get(strEBOMMapKey);
				slRelToDelete.addElement(strEBOMRelId);
			}
			DomainRelationship.disconnect(context, slRelToDelete.toArray(new String []{}));			
			VPLMIntegTraceUtil.trace(context, "MassCollab: Disconnected EBOM rel Ids  > " + slRelToDelete );
		}
		catch(Exception ex)
		{
			strStatus = pgApolloConstants.STR_ERROR;
			loggerApolloTrace.error(ex.getMessage() ,ex);
			loggerSync.error( " Exception While updating EBOM - From connectDisconnectEBOMAndUpdateAttributes {}" , ex.getLocalizedMessage());
			VPLMIntegTraceUtil.trace(context, "MassCollab: Exception While updating EBOM - rom connectDisconnectEBOMAndUpdateAttributes > " + ex.getLocalizedMessage() );
			
			StringBuffer strHistory = new StringBuffer();
			strHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
			strHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_BOM);
			strHistory.append(ex.getLocalizedMessage());
			addCustomHistoryOnSync(context,dPartObj.getObjectId(), strHistory.toString());
			
			StringBuffer strNoRMErrorBuff = new StringBuffer();
			strNoRMErrorBuff.append(pgApolloConstants.STR_ERROR);
			strNoRMErrorBuff.append(pgApolloConstants.CONSTANT_STRING_COLON);
			strNoRMErrorBuff.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_BOM);
			strNoRMErrorBuff.append(pgApolloConstants.CONSTANT_STRING_COLON);
			strNoRMErrorBuff.append(pgApolloConstants.CONSTANT_STRING_SPACE);
			strNoRMErrorBuff.append(ex.getLocalizedMessage());
			strNoRMErrorBuff.append(pgApolloConstants.CONSTANT_STRING_SPACE);
			strNoRMErrorBuff.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
			return strNoRMErrorBuff.toString();				
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}		
		return new StringBuilder(pgApolloConstants.STR_SUCCESS).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sSubstituteUpdateStatus).toString();
	}
	
	/**
	 * This method will Update the EBOM Attributes.
	 * @param context
	 * @param materialsMap
	 * @param domRelObj
	 * @param strRMId
	 * @param strAPPId
	 * @param bPreviousAPPRevExists
	 * @param mapExtendedEBOMInput
	 * @param mapExtendedSubstituteInput
	 * @return
	 * @throws Exception
	 */
	public String updateEBOMAttributes(matrix.db.Context context, Map materialsMap, DomainRelationship domRelObj, String strRMId, String strAPPId, boolean bPreviousAPPRevExists, Map mapExtendedEBOMInput, Map mapExtendedSubstituteInput) throws Exception
	{		
		String sCurrentAPPEBOMChgCurrentValue;
		StringList ebomRelSchemaAttrList = (StringList)mapExtendedEBOMInput.get(pgApolloConstants.KEY_EBOMATTRIBUTELIST);
		StringList slEBOMChgAttribute = (StringList)mapExtendedEBOMInput.get(pgApolloConstants.KEY_EBOMCHGATTRIBUTELIST);
		Map mapPreviousAPPEBOM = (Map)mapExtendedEBOMInput.get(DomainConstants.RELATIONSHIP_EBOM);		
		String sUniqueKey = DomainConstants.EMPTY_STRING;
		
		Map materialsRefMap = new HashMap();
		materialsRefMap.putAll(materialsMap);
		
		if(materialsMap.containsKey(pgApolloConstants.KEY_UNIQUEKEY))
		{
			sUniqueKey = (String)materialsMap.get(pgApolloConstants.KEY_UNIQUEKEY);
		}
		String sExistingWebWidth = (String)materialsMap.get(pgApolloConstants.KEY_OLD_WEBWIDTH);
	
		StringBuffer strReturnMessageBuf = new StringBuffer();
		String attrKey;		
		//Prepare attribute Map
		materialsMap.remove(pgApolloConstants.KEY_PLYMATERIAL);
		Set<String> keys_Attr = materialsMap.keySet();
		Iterator<String> iter_checkInMap = keys_Attr.iterator();
		while (iter_checkInMap.hasNext()) 
		{
			attrKey = iter_checkInMap.next();
			//2018x.3 Remove Sequence - start
			if(attrKey.equals(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA) || attrKey.equals(pgApolloConstants.ATTRIBUTE_PLY_GROUP_NAME) || attrKey.equals(pgApolloConstants.ATTRIBUTE_PLY_NAME) || attrKey.equals("ConversionFactor") || attrKey.equals(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL) || attrKey.equals(pgApolloConstants.ATTRIBUTE_PGNETWEIGHTUOM) || attrKey.equals(pgApolloConstants.ATTRIBUTE_NET_WEIGHT) || ebomRelSchemaAttrList.contains(attrKey))
			//2018x.3 Remove Sequence - end
			{
				continue;
			}
			else 
			{
				iter_checkInMap.remove();  //Remove the attributes/keys from Map which are not present in EBOM relationship
			}
		}		

		//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Start
		Map mpPreviousRevisionValues;			
		sCurrentAPPEBOMChgCurrentValue = domRelObj.getAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGCHANGE);
		if(bPreviousAPPRevExists)
		{				
			if(!mapPreviousAPPEBOM.containsKey(sUniqueKey))
			{
				materialsMap.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_CPLUS);				
			}
			else
			{
				mpPreviousRevisionValues = (Map)mapPreviousAPPEBOM.get(sUniqueKey);
				mpPreviousRevisionValues.remove(DomainRelationship.SELECT_ID);
				
				materialsMap = compareAndUpdateIfRMPModified(materialsMap, materialsRefMap, mpPreviousRevisionValues);	
				
				if(!((materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_PGCHANGE) && pgApolloConstants.RANGE_VALUE_CHG_C.equalsIgnoreCase((String)materialsMap.get(pgApolloConstants.ATTRIBUTE_PGCHANGE)))))
				{
					materialsMap = compareCurrentAndPreviousAttributeValues(materialsMap, sCurrentAPPEBOMChgCurrentValue, slEBOMChgAttribute, mpPreviousRevisionValues);					
				}	
								
			}		
		}
		//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Ends
		
		//Set attribute values
		try
		{
			materialsMap.put(FormulationAttribute.IS_VPM_VISIBLE.getAttribute(context), "FALSE");
			if(!materialsMap.isEmpty())
			{
				domRelObj.setAttributeValues(context, materialsMap);
			}
		}
		catch (Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
			
			StringBuffer strHistory = new StringBuffer();
			strHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
			strHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_BOM);
			strHistory.append(ex.getLocalizedMessage());
			addCustomHistoryOnSync(context, strAPPId, strHistory.toString());			

			strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
			strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
			strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOM_ATTRIBUTE_Update);
			strReturnMessageBuf.append(". ");
			strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED);
			return strReturnMessageBuf.toString();			
		}
		
		StringList slSubstituteObjectIds = (StringList)mapExtendedSubstituteInput.get(pgApolloConstants.KEY_EBOMSUBSTITUTE_OBJECTLIST);
		String sSubstituteUpdate = DomainConstants.EMPTY_STRING;
		//Update Attributes on EBOM Substitute Connections in case of Retain Substitutes
		if(null != slSubstituteObjectIds && !slSubstituteObjectIds.isEmpty())
		{
			StringList slSubstituteRelIds = (StringList)mapExtendedSubstituteInput.get(pgApolloConstants.KEY_EBOMSUBSTITUTE_RELIDLIST);
			materialsMap.put(pgApolloConstants.KEY_OLD_WEBWIDTH, sExistingWebWidth);
			sSubstituteUpdate = setAttributesOnExistingSubstitutes(context, materialsMap, strRMId, slSubstituteObjectIds, slSubstituteRelIds,  bPreviousAPPRevExists, mapExtendedSubstituteInput);
		}
					
		return new StringBuilder(pgApolloConstants.STR_SUCCESS).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sSubstituteUpdate).toString();
	}
	
	/**
	 * Method to compare RMP modified
	 * @param materialsMap
	 * @param mpPreviousRevisionValues
	 * @param mpPreviousRevisionValues2 
	 * @return
	 */
	public Map compareAndUpdateIfRMPModified(Map materialsMap, Map materialsRefMap, Map mpPreviousRevisionValues)
	{

		String sNewRMPName = DomainConstants.EMPTY_STRING;
		String sOldRMPName = DomainConstants.EMPTY_STRING;


		if(materialsRefMap.containsKey(pgApolloConstants.KEY_PLYMATERIAL))
		{
			sNewRMPName = (String)materialsRefMap.get(pgApolloConstants.KEY_PLYMATERIAL);			
		}
		else if(materialsRefMap.containsKey(DomainConstants.SELECT_NAME))
		{
			sNewRMPName = (String)materialsRefMap.get(DomainConstants.SELECT_NAME);			
		}

		if(mpPreviousRevisionValues.containsKey(DomainConstants.SELECT_NAME))
		{
			sOldRMPName = (String)mpPreviousRevisionValues.get(DomainConstants.SELECT_NAME);			
		}

		if(UIUtil.isNotNullAndNotEmpty(sOldRMPName) && UIUtil.isNotNullAndNotEmpty(sNewRMPName) && !sOldRMPName.equals(sNewRMPName))
		{
			materialsMap.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_C);
		}

		return materialsMap;
	}

	/**
	 * This method will set attribute for EBOM Substitutes 
	 * @param context
	 * @param materialsMap
	 * @param strPrimaryId
	 * @param slSubstituteObjectIds
	 * @param slSubstituteRelIds
	 * @param bPreviousAPPRevExists
	 * @param mapExtendedSubstiuteInput
	 * @throws Exception
	 */
	private String setAttributesOnExistingSubstitutes(matrix.db.Context context, Map materialsMap, String strPrimaryId, StringList slSubstituteObjectIds, StringList slSubstituteRelIds, boolean bPreviousAPPRevExists, Map mapExtendedSubstiuteInput) throws Exception
	{		
			String sReturn = DomainConstants.EMPTY_STRING;
		    StringList slEBOMSubstituteChgAttribute = (StringList)mapExtendedSubstiuteInput.get(pgApolloConstants.KEY_EBOMSUBSTITUTE_CHGATTRIBUTELIST);
		    Map mapPreviousAPPSubstituteUniqueData = (Map)mapExtendedSubstiuteInput.get(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE);
			VPLMIntegTraceUtil.trace(context, "MassCollab: INSIDE setAttributesOnExistingSubstitutes method.. ");
			VPLMIntegTraceUtil.trace(context, "MassCollab: materialsMap = " + materialsMap );
			VPLMIntegTraceUtil.trace(context, "MassCollab: strPrimaryId = " + strPrimaryId );
			VPLMIntegTraceUtil.trace(context, "MassCollab: slSubstituteObjectIds = " + slSubstituteObjectIds );
			VPLMIntegTraceUtil.trace(context, "MassCollab: slSubstituteRelIds = " + slSubstituteRelIds );				
			
			String sCurrentAPPEBOMSubstituteChgCurrentValue;
			String strCalculatedGrossWeight = DomainConstants.EMPTY_STRING;
			String strCalculatedNetWeight = DomainConstants.EMPTY_STRING;
			String strCalculateQuantity = DomainConstants.EMPTY_STRING;
			
			String sExistingGrossWeight = DomainConstants.EMPTY_STRING;
			String sExistingNetWeight = DomainConstants.EMPTY_STRING;
			String sExistingQuantity = DomainConstants.EMPTY_STRING;
			
			String strSubstituteId = DomainConstants.EMPTY_STRING;
			String strSubstituteRelId = DomainConstants.EMPTY_STRING;
			Map mpEBOMResultDetail = new HashMap();
			DomainRelationship domSubRel = null;			
						
			//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Starts
			Map mpPreviousAPPRevisionValues;
			Set<String> keys = new HashSet();
			//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Ends

			Map attrMap = new HashMap();
			
			StringBuilder sbKeyBuilder;
			String sUniqueKey;
			String sSubstituteUniqueKey;
			
			String sToObjectId;
			String sLayerName;
			String sGroupName;
			String sRelId;
			String sExistingSubstituteWebWidth;			
			Map mapSubstituteRelInfo;

			Map mapSubstituteUniqueData = new HashMap();
			
			StringList slEBOMSubstituteSelectable = new StringList();
			slEBOMSubstituteSelectable.add(DomainRelationship.SELECT_ID);
			slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
			slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
			slEBOMSubstituteSelectable.add(DomainConstants.SELECT_TO_ID);
			slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE);
			slEBOMSubstituteSelectable.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY+".inputvalue");
			slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_GROSSWEIGHTREAL+".inputvalue");
			slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PGNETWEIGHT+".inputvalue");
			slEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_WEBWIDTH_INPUT);

			MapList mlSubstituteRelInfoList = DomainRelationship.getInfo(context, slSubstituteRelIds.toArray(new String[slSubstituteRelIds.size()]), slEBOMSubstituteSelectable);
			
			if(null != mlSubstituteRelInfoList && !mlSubstituteRelInfoList.isEmpty())
			{				
				for(int i=0 ; i < mlSubstituteRelInfoList.size() ; i++)
				{
					mapSubstituteRelInfo = (Map)mlSubstituteRelInfoList.get(i);
					
					sRelId = (String)mapSubstituteRelInfo.get(DomainRelationship.SELECT_ID);
					sLayerName = (String)mapSubstituteRelInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
					sGroupName = (String)mapSubstituteRelInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
					sToObjectId = (String)mapSubstituteRelInfo.get(DomainConstants.SELECT_TO_ID);

					sbKeyBuilder = new StringBuilder();
					sbKeyBuilder.append(sGroupName);
					sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sLayerName);	
					sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sToObjectId);						
					sUniqueKey = sbKeyBuilder.toString();
					
					mapSubstituteRelInfo.put(pgApolloConstants.KEY_UNIQUEKEY, sUniqueKey);					
					mapSubstituteUniqueData.put(sRelId, mapSubstituteRelInfo);
				}
			}
			
			Map mpEBOMDetails = new HashMap<>();
			mpEBOMDetails.put(pgApolloConstants.KEY_GROSSWEIGHT, (String)materialsMap.get(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL));
			mpEBOMDetails.put(pgApolloConstants.KEY_NETWEIGHT, (String)materialsMap.get(pgApolloConstants.ATTRIBUTE_NET_WEIGHT));
			mpEBOMDetails.put(DomainConstants.ATTRIBUTE_QUANTITY, (String)materialsMap.get(DomainConstants.ATTRIBUTE_QUANTITY));
			mpEBOMDetails.put(pgApolloConstants.KEY_AREA, (String)materialsMap.get(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA));
			
			VPLMIntegTraceUtil.trace(context, "MassCollab: mpEBOMDetails = " + mpEBOMDetails );	
			String strComponentCount=null;
			if(materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_PG_COMPONENTQUANTITY)) {
				strComponentCount =(String) materialsMap.get(pgApolloConstants.ATTRIBUTE_PG_COMPONENTQUANTITY);
			}
			
			String sExistingEBOMWebWidth = (String)materialsMap.get(pgApolloConstants.KEY_OLD_WEBWIDTH);
			String sNewEBOMWebWidth = (String)materialsMap.get(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH);
			
			boolean bSetAllNewWebWidth = false;
			if(pgApolloCommonUtil.isNullOrEmptyOrZero(sExistingEBOMWebWidth) || pgApolloCommonUtil.isNullOrEmptyOrZero(sNewEBOMWebWidth))
			{
				bSetAllNewWebWidth = true;
			}

			boolean bSetNewWebWidth = false;
			
			String sSubstituteWidthForCalculation;
			String sWidthFactor;
			
			for(int x=0 ; x < slSubstituteObjectIds.size() ; x++)
			{
				strSubstituteId = slSubstituteObjectIds.get(x);
				strSubstituteRelId = slSubstituteRelIds.get(x);
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: strSubstituteId = " + strSubstituteId );
				VPLMIntegTraceUtil.trace(context, "MassCollab: strSubstituteRelId = " + strSubstituteRelId );
				strCalculatedGrossWeight = DomainConstants.EMPTY_STRING;
				strCalculatedNetWeight = DomainConstants.EMPTY_STRING;
				strCalculateQuantity = DomainConstants.EMPTY_STRING;
				bSetNewWebWidth = false;
				
				mapSubstituteRelInfo = (Map)mapSubstituteUniqueData.get(strSubstituteRelId);
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: mapSubstituteRelInfo = " + mapSubstituteRelInfo+" sReturn "+sReturn );
				
				sExistingGrossWeight = (String) mapSubstituteRelInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_GROSSWEIGHTREAL+".inputvalue");
				
				sExistingNetWeight = (String) mapSubstituteRelInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PGNETWEIGHT+".inputvalue");

				sExistingQuantity = (String) mapSubstituteRelInfo.get(DomainConstants.SELECT_ATTRIBUTE_QUANTITY+".inputvalue");
				
				sExistingSubstituteWebWidth = (String) mapSubstituteRelInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_WEBWIDTH_INPUT);
				
				if(bSetAllNewWebWidth)
				{
					bSetNewWebWidth = true;
				}
				else
				{
					if(sExistingSubstituteWebWidth.equals(sExistingEBOMWebWidth))
					{
						bSetNewWebWidth = true;
					}
				}
				
				sSubstituteWidthForCalculation = sExistingSubstituteWebWidth;
				
				if(bSetNewWebWidth)
				{
					sSubstituteWidthForCalculation = sNewEBOMWebWidth;
				}
				

				sWidthFactor = pgApolloCommonUtil.divideValues(sSubstituteWidthForCalculation, sNewEBOMWebWidth);
				
				mpEBOMDetails.put(pgApolloConstants.KEY_WIDTHRATIO, sWidthFactor);
			
				mpEBOMResultDetail = new HashMap();
				mpEBOMResultDetail=pgApolloWeightConversionUtility.getConvertedWeightsAndQuantity (context, strPrimaryId,  strSubstituteId, mpEBOMDetails);
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: mpEBOMResultDetail = " + mpEBOMResultDetail );
				
				if(mpEBOMResultDetail.containsKey(pgApolloConstants.KEY_GROSSWEIGHT))
				{
					strCalculatedGrossWeight=(String) mpEBOMResultDetail.get(pgApolloConstants.KEY_GROSSWEIGHT);
				}
				if(mpEBOMResultDetail.containsKey(pgApolloConstants.KEY_NETWEIGHT))
				{
					strCalculatedNetWeight=(String) mpEBOMResultDetail.get(pgApolloConstants.KEY_NETWEIGHT);
				}
				if(mpEBOMResultDetail.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))
				{
					strCalculateQuantity=(String) mpEBOMResultDetail.get(DomainConstants.ATTRIBUTE_QUANTITY);
				}
				
				if(UIUtil.isNullOrEmpty(strCalculatedGrossWeight))
				{
					strCalculatedGrossWeight = DomainConstants.EMPTY_STRING;
					sReturn = pgApolloConstants.STR_NO_FLAG;
				}
				if(UIUtil.isNullOrEmpty(strCalculatedNetWeight))
				{
					strCalculatedNetWeight = DomainConstants.EMPTY_STRING;
					sReturn = pgApolloConstants.STR_NO_FLAG;
				}
				if(UIUtil.isNullOrEmpty(strCalculateQuantity))
				{
					strCalculateQuantity = DomainConstants.EMPTY_STRING;
					sReturn = pgApolloConstants.STR_NO_FLAG;
				}			


				attrMap = new HashMap();			
				attrMap.put(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, (String) materialsMap.get(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA));
				attrMap.put(pgApolloConstants.ATTRIBUTE_PGNETWEIGHTUOM, (String) materialsMap.get(pgApolloConstants.ATTRIBUTE_PGNETWEIGHTUOM));
				attrMap = updateAttributeMap(context, attrMap, sExistingGrossWeight, strCalculatedGrossWeight, pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL);
				attrMap = updateAttributeMap(context, attrMap, sExistingNetWeight, strCalculatedNetWeight, pgApolloConstants.ATTRIBUTE_NET_WEIGHT);
				attrMap = updateAttributeMap(context, attrMap, sExistingQuantity, strCalculateQuantity, DomainConstants.ATTRIBUTE_QUANTITY);	
				if(bSetNewWebWidth)	{
					attrMap.put(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH, sSubstituteWidthForCalculation);
				}

				if(UIUtil.isNotNullAndNotEmpty(strComponentCount)) {
					attrMap.put(pgApolloConstants.ATTRIBUTE_PG_COMPONENTQUANTITY, strComponentCount);
				}
				
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: attrMap = " + attrMap );
				
				domSubRel = DomainRelationship.newInstance(context, strSubstituteRelId);					
				
				//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Starts				
				sSubstituteUniqueKey = (String)mapSubstituteRelInfo.get(pgApolloConstants.KEY_UNIQUEKEY);	
				
				if(bPreviousAPPRevExists)
				{
					sCurrentAPPEBOMSubstituteChgCurrentValue= DomainConstants.EMPTY_STRING;
					if(mapSubstituteRelInfo.containsKey(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE))
					{
						sCurrentAPPEBOMSubstituteChgCurrentValue = (String) mapSubstituteRelInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE);
					}
					
					if(!mapPreviousAPPSubstituteUniqueData.containsKey(sSubstituteUniqueKey))
					{
						if(UIUtil.isNullOrEmpty(sCurrentAPPEBOMSubstituteChgCurrentValue))
						{
							attrMap.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_CPLUS);
						}
					}
					else
					{
						mpPreviousAPPRevisionValues = (Map)mapPreviousAPPSubstituteUniqueData.get(sSubstituteUniqueKey);				
						mpPreviousAPPRevisionValues.remove(DomainRelationship.SELECT_ID);

						attrMap = compareCurrentAndPreviousAttributeValues(attrMap, sCurrentAPPEBOMSubstituteChgCurrentValue, slEBOMSubstituteChgAttribute, mpPreviousAPPRevisionValues);					
					}
				}				
				//APOLLO 2018x.5 A10-598 - Populate Chg. column for CATIA APP during sync Ends
				
				if(!attrMap.isEmpty()){
					domSubRel.setAttributeValues(context, attrMap);
				}
				VPLMIntegTraceUtil.trace(context, "MassCollab: Setting attributes on Substitutes completed for " + strSubstituteRelId );
			}		
			VPLMIntegTraceUtil.trace(context, "MassCollab: Setting attributes on Substitutes sReturn " + sReturn );
	
		return sReturn;
	}

	
	

	
	/**
	 * Method to update Attribute Map
	 * @param context 
	 * @param attributeMap
	 * @param sExistingValue
	 * @param sCalculatedValue
	 * @param sKey
	 * @return
	 * @throws MatrixException 
	 */
	public static Map updateAttributeMap(matrix.db.Context context, Map attributeMap, String sExistingValue, String sCalculatedValue, String sKey) throws MatrixException
	{
		context.printTrace(pgApolloConstants.TRACE_LPD,  "MassCollab: updateAttributeMap sExistingValue = " + sExistingValue+"  sCalculatedValue = "+ sCalculatedValue +" sKey = "+sKey );

		boolean bSkipUpdateValue = pgApolloCommonUtil.preUpdateValidate(sCalculatedValue, sExistingValue);
		
		context.printTrace(pgApolloConstants.TRACE_LPD,  "MassCollab: updateAttributeMap bUpdateValue = " + bSkipUpdateValue );

		if(!bSkipUpdateValue)
		{
			attributeMap.put(sKey, sCalculatedValue);
		}	
		
		context.printTrace(pgApolloConstants.TRACE_LPD,  "MassCollab: updateAttributeMap attributeMap = " + attributeMap );

		return attributeMap;
	}
	//Calculate EBOM Quantity
	
	/**
	 * Method to update Material Map from CATIA with Web width
	 * @param context 
	 * @param strMaterialId
	 * @param materialsMap
	 * @return
	 * @throws Exception 
	 */
	public HashMap updateMaterialMapWithWebWidth(matrix.db.Context context, String strMaterialId, HashMap materialsMap) throws Exception 
	{

		context.printTrace(pgApolloConstants.TRACE_LPD,  "MassCollab: updateMaterialMapWithWebWidth strMaterialId = "+strMaterialId+"  | materialsMap = " + materialsMap );

		String sNewWebWidth;

		
		if(materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH)) {				
			String sWebWidthWithUnit = (String)materialsMap.get(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH);
			sNewWebWidth = getParameterValue(sWebWidthWithUnit);				
		} else {				
			sNewWebWidth = pgApolloWeightConversionUtility.getTargetValue(context, strMaterialId, pgApolloConstants.STR_CHARACTERISTIC_DIMENSION_WIDTH, DomainConstants.EMPTY_STRING, false);
			if(UIUtil.isNullOrEmpty(sNewWebWidth))
			{
				sNewWebWidth = "0.0";
			}

			sNewWebWidth = getParameterValue(sNewWebWidth);				
		}
		materialsMap.put(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH, sNewWebWidth);
		
		context.printTrace(pgApolloConstants.TRACE_LPD,  "MassCollab: After update updateMaterialMapWithWebWidth materialsMap = " + materialsMap );

		return materialsMap;
	}
	/**
	 * This method calculates EBOM Quantity
	 * Method modified for Query with UOM 
	 * @param context
	 * @param materialsMap
	 * @param catiaEnoviaUOMMap
	 * @param strMaterialUOM
	 * @return
	 * @throws Exception
	 */
	public HashMap calculateQuantity(matrix.db.Context context, HashMap materialsMap, Map catiaEnoviaUOMMap,String strMaterialBaseUOM) throws Exception
	{
		BigDecimal bigDecimalGrossWeight = BigDecimal.valueOf(0.0);
		BigDecimal bigDecimalNetWeight = BigDecimal.valueOf(0.0);
		String strGrossWtFromCatia;
		
		String strArea;
		String strAreaValue;
		String strAreaUnit;		
		
		boolean bIncludeQuantity = true;
		boolean bIncludeNetQuantity = true;

		String strUniqueKey = DomainConstants.EMPTY_STRING;
		if(materialsMap.containsKey(pgApolloConstants.KEY_UNIQUEKEY))
		{
			strUniqueKey = (String)materialsMap.get(pgApolloConstants.KEY_UNIQUEKEY);
		}
		
		String strNetWeightValue = DomainConstants.EMPTY_STRING;
		VPLMIntegTraceUtil.trace(context, "MassCollab: Before calculateQuantity materialsMap " + materialsMap );
		try{
			//taking the Quantity
			//Take gross weight
			
			if(materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA)){
				strArea = (String)materialsMap.get(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA);
				strAreaValue = getParameterValue(strArea);
				strAreaUnit = getParameterUnit(strArea);
				if(UIUtil.isNullOrEmpty(strAreaUnit) && catiaEnoviaUOMMap.containsKey(pgApolloConstants.KEY_AREA)) {
					
					strAreaUnit = (String)catiaEnoviaUOMMap.get(pgApolloConstants.KEY_AREA); //Catia Preference UoM					
				}
			} else{			
				strAreaValue = "0.00";
				strAreaUnit = pgApolloConstants.STR_UOM_SQUARE_CENTIMETER;
			}

			materialsMap.put(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, strAreaValue + pgApolloConstants.CONSTANT_STRING_PIPE + strAreaUnit);	
			
			String strMassUoMFromCATIA = DomainConstants.EMPTY_STRING;
			if(catiaEnoviaUOMMap.containsKey(pgApolloConstants.KEY_MASS))  
			{
				strMassUoMFromCATIA = (String)catiaEnoviaUOMMap.get(pgApolloConstants.KEY_MASS); //Catia Preference UoM
			}
			
			if(materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_NET_WEIGHT)) {				
				strNetWeightValue = getConvertedValueBasedOnUoM (context, materialsMap, pgApolloConstants.ATTRIBUTE_NET_WEIGHT, pgApolloConstants.STR_UOM_GRAM, strMassUoMFromCATIA);
				materialsMap.put(pgApolloConstants.ATTRIBUTE_NET_WEIGHT, strNetWeightValue);	
				bigDecimalNetWeight = validateAndGetNonZeroValue(strNetWeightValue);				
			}
			materialsMap.put(pgApolloConstants.ATTRIBUTE_PGNETWEIGHTUOM, pgApolloConstants.STR_MASS_UOM_GRAM);			
			
			if(materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL)) {
				
				strGrossWtFromCatia = getConvertedValueBasedOnUoM (context, materialsMap, pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL, pgApolloConstants.STR_UOM_GRAM, strMassUoMFromCATIA);
				bigDecimalGrossWeight = validateAndGetNonZeroValue(strGrossWtFromCatia);				
			}			
			if((materialsMap.containsKey(DomainConstants.ATTRIBUTE_QUANTITY)) || pgApolloConstants.STR_UOM_EACH.equalsIgnoreCase(strMaterialBaseUOM))
			{
				bIncludeQuantity = false;				
				materialsMap = processQuantity(context, materialsMap, strMaterialBaseUOM, strUniqueKey, DomainConstants.ATTRIBUTE_QUANTITY);	
			}
			if((materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY)) || pgApolloConstants.STR_UOM_EACH.equalsIgnoreCase(strMaterialBaseUOM))
			{
				bIncludeNetQuantity = false;				
				materialsMap = processQuantity(context, materialsMap, strMaterialBaseUOM, strUniqueKey, pgApolloConstants.ATTRIBUTE_NET_QUANTITY);	
			}
			if(bIncludeQuantity)
			{
				materialsMap = processArea(context, materialsMap, strMaterialBaseUOM, strAreaUnit, pgApolloConstants.KEY_GROSS_AREA, DomainConstants.ATTRIBUTE_QUANTITY);	
				materialsMap = processWeight(materialsMap, strMaterialBaseUOM, bigDecimalGrossWeight, strUniqueKey, pgApolloConstants.KEY_GROSSWEIGHT, DomainConstants.ATTRIBUTE_QUANTITY);					
				materialsMap = processVolume(context, materialsMap, strMaterialBaseUOM, strUniqueKey, pgApolloConstants.KEY_GROSS_VOLUME, DomainConstants.ATTRIBUTE_QUANTITY);					
				materialsMap = processLength(context, materialsMap, strMaterialBaseUOM, strUniqueKey, pgApolloConstants.KEY_GROSS_LENGTH, DomainConstants.ATTRIBUTE_QUANTITY);
			}			
			if(bIncludeNetQuantity)
			{
				materialsMap = processArea(context, materialsMap, strMaterialBaseUOM, strAreaUnit, pgApolloConstants.KEY_NET_AREA, pgApolloConstants.ATTRIBUTE_NET_QUANTITY);	
				materialsMap = processWeight(materialsMap, strMaterialBaseUOM, bigDecimalNetWeight, strUniqueKey, pgApolloConstants.KEY_GROSSWEIGHT, pgApolloConstants.ATTRIBUTE_NET_QUANTITY);					
				materialsMap = processVolume(context, materialsMap, strMaterialBaseUOM, strUniqueKey, pgApolloConstants.KEY_NET_VOLUME, pgApolloConstants.ATTRIBUTE_NET_QUANTITY);					
				materialsMap = processLength(context, materialsMap, strMaterialBaseUOM, strUniqueKey, pgApolloConstants.KEY_NET_LENGTH, pgApolloConstants.ATTRIBUTE_NET_QUANTITY);
			}
			
			if(!materialsMap.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))				
			{
				throw new MatrixException(pgApolloConstants.STR_ERROR_QUANTITY_CALCULATION.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", DomainConstants.ATTRIBUTE_QUANTITY));
			}	
			
			if(!materialsMap.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY))				
			{
				throw new MatrixException(pgApolloConstants.STR_ERROR_QUANTITY_CALCULATION.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", pgApolloConstants.STR_NET_QUANTITY));
			}			
			String dGrossWeightReal = bigDecimalGrossWeight.toString(); 
			materialsMap.put(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL, dGrossWeightReal);			
			VPLMIntegTraceUtil.trace(context, "MassCollab: After calculateQuantity materialsMap " + materialsMap );

		} catch(Exception ex){
			loggerApolloTrace.error(ex.getMessage() ,ex);
			throw ex;
		} 
		return materialsMap;
	}
	
	/**
	 * Method to process Quantity
	 * @param context
	 * @param materialsMap
	 * @param strMaterialBaseUOM
	 * @param strUniqueKey
	 * @param strParameterName
	 * @return
	 * @throws Exception
	 */
	private HashMap processQuantity(matrix.db.Context context, HashMap materialsMap, String strMaterialBaseUOM,  String strUniqueKey, String strParameterName) throws Exception {
		String strQuantity = DomainConstants.EMPTY_STRING;
		String strQuantityValue;
		String strQuantityUnit;
		boolean isNetQtyParameter = false;
		isNetQtyParameter = isNetParameter(strParameterName);
		String strParameterInMessage = strParameterName;
		if(isNetQtyParameter)
		{
			strParameterInMessage = pgApolloConstants.STR_NET_QUANTITY;
		}
		if(materialsMap.containsKey(strParameterName))
		{
			strQuantity = (String)materialsMap.get(strParameterName);
		}
		if(pgApolloConstants.STR_UOM_EACH.equalsIgnoreCase(strMaterialBaseUOM))
		{
			if(UIUtil.isNotNullAndNotEmpty(strQuantity))
			{
				strQuantityValue = getParameterValue(strQuantity);
			}
			else
			{
				strQuantityValue = "1.0";
			}
		}
		else
		{
			strQuantityUnit = getParameterUnit(strQuantity);
			if(UIUtil.isNullOrEmpty(strQuantityUnit)) 
			{
				throw new MatrixException(pgApolloConstants.STR_ERROR_MISSING_UNIT.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", strParameterInMessage));
			}
			else
			{
				strQuantityValue = getConvertedValueBasedOnUoM (context, materialsMap, strParameterName, strMaterialBaseUOM.toUpperCase(), pgApolloConstants.STR_UOM_LITER);
			}
		}		
		if(UIUtil.isNotNullAndNotEmpty(strQuantityValue))
		{
			materialsMap.put(strParameterName, strQuantityValue);	
		}
		else
		{
			throw new MatrixException(pgApolloConstants.STR_CONVERSION_FAILED.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", strParameterInMessage));
		}
		
		return materialsMap;
	}

	/**
	 * Method to check Quantity Parameter is Net or not
	 * @param strParameterName
	 * @return
	 */
	private boolean isNetParameter(String strParameterName) {		
		boolean isNetParameter = false;
		if(UIUtil.isNotNullAndNotEmpty(strParameterName) && pgApolloConstants.ATTRIBUTE_NET_QUANTITY.equalsIgnoreCase(strParameterName))
		{
			isNetParameter =  true;
		}
		return isNetParameter;
	}
	
	/**
	 * Method to check Quantity Parameter is Gross or not
	 * @param strParameterName
	 * @return
	 */
	private boolean isGrossParameter(String strParameterName) {		
		boolean isGrossParameter = false;
		if(UIUtil.isNotNullAndNotEmpty(strParameterName) && DomainConstants.ATTRIBUTE_QUANTITY.equalsIgnoreCase(strParameterName))
		{
			isGrossParameter = true;
		}
		return isGrossParameter;
	}


	/**
	 * Method to process length
	 * @param context
	 * @param materialsMap
	 * @param strMaterialBaseUOM
	 * @param strUniqueKey
	 * @param bIncludeQuantity
	 * @param strParameterName
	 * @param strQuantityParameter
	 * @return
	 * @throws Exception
	 */
	private HashMap processLength(matrix.db.Context context, HashMap materialsMap, String strMaterialBaseUOM,	String strUniqueKey, String strParameterName, String strQuantityParameter) throws Exception {
		String strQuantityValue;
		String strLength;
		String strLengthValue;
		boolean isGrossParameter = false;
		isGrossParameter = isGrossParameter(strQuantityParameter);
		if(pgApolloConstants.STR_UOM_METER.equalsIgnoreCase(strMaterialBaseUOM))
		{
			if(materialsMap.containsKey(strParameterName))
			{
				strLength = (String)materialsMap.get(strParameterName);
				if(isGrossParameter)
				{
					materialsMap.put(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, strLength);
				}
				
					strLengthValue = getConvertedValueBasedOnUoM (context, materialsMap, strParameterName, strMaterialBaseUOM.toUpperCase(), pgApolloConstants.STR_UOM_METER);
					if(UIUtil.isNotNullAndNotEmpty(strLengthValue))
					{
						strQuantityValue = strLengthValue;
						materialsMap.put(strQuantityParameter, strQuantityValue);
					}
					else
					{
						throw new MatrixException(pgApolloConstants.STR_CONVERSION_FAILED.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", strParameterName));
					}
								
			}
			else
			{
				throw new MatrixException(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", strParameterName));
			}			
		}
		return materialsMap;
	}
	
	/**
	 * Method to process volume
	 * @param context
	 * @param materialsMap
	 * @param strMaterialBaseUOM
	 * @param strUniqueKey
	 * @param bIncludeQuantity
	 * @param strParameterName
	 * @param strQuantityParameter
	 * @return
	 * @throws Exception
	 */
	private HashMap processVolume(matrix.db.Context context, HashMap materialsMap, String strMaterialBaseUOM,	String strUniqueKey, String strParameterName, String strQuantityParameter) throws Exception {
		String strQuantityValue;
		String strVolume;
		String strVolumeValue;
		boolean isGrossParameter = false;
		isGrossParameter = isGrossParameter(strQuantityParameter);
		if((pgApolloConstants.STR_UOM_LITER.equalsIgnoreCase(strMaterialBaseUOM) || pgApolloConstants.STR_UOM_CUBIC_METER.equalsIgnoreCase(strMaterialBaseUOM)))
		{
			if(materialsMap.containsKey(strParameterName))
			{
				strVolume = (String)materialsMap.get(strParameterName);
				if(isGrossParameter)
				{
					materialsMap.put(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, strVolume);
				}
				
					strVolumeValue = getConvertedValueBasedOnUoM (context, materialsMap, strParameterName, strMaterialBaseUOM.toUpperCase(), pgApolloConstants.STR_UOM_LITER);
					if(UIUtil.isNotNullAndNotEmpty(strVolumeValue))
					{
						strQuantityValue = strVolumeValue;
						materialsMap.put(strQuantityParameter, strQuantityValue);
					}
					else
					{
						throw new MatrixException(pgApolloConstants.STR_CONVERSION_FAILED.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", strParameterName));
					}
				
			}
			else 
			{
				throw new MatrixException(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", strParameterName));
			}

		}		
		return materialsMap;
	}
	
	/**
	 * Method to process Area
	 * @param context
	 * @param materialsMap
	 * @param strMaterialBaseUOM
	 * @param strAreaUnit
	 * @param bIncludeQuantity
	 * @param strParameterName
	 * @param strQuantityParameter
	 * @return
	 * @throws Exception
	 */
	private HashMap processArea(matrix.db.Context context, HashMap materialsMap, String strMaterialBaseUOM, String strAreaUnit, String strParameterName, String strQuantityParameter) throws Exception {
		String strUniqueKey = DomainConstants.EMPTY_STRING;
		if(materialsMap.containsKey(pgApolloConstants.KEY_UNIQUEKEY))
		{
			strUniqueKey = (String)materialsMap.get(pgApolloConstants.KEY_UNIQUEKEY);
		}
		String strQuantityValue;
		String strArea;
		boolean isGrossParameter = false;
		isGrossParameter = isGrossParameter(strQuantityParameter);
		if((pgApolloConstants.STR_UOM_SQUARE_METER.equalsIgnoreCase(strMaterialBaseUOM)))
		{
			if(materialsMap.containsKey(strParameterName))
			{
				strArea = (String)materialsMap.get(strParameterName);
				if(isGrossParameter)
				{
					materialsMap.put(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, strArea);
				}
				
					strQuantityValue = getQuantityForSquareMeter(context, materialsMap, strMaterialBaseUOM, strAreaUnit,strUniqueKey, strParameterName);									
					if(UIUtil.isNotNullAndNotEmpty(strQuantityValue))
					{
						materialsMap.put(strQuantityParameter, strQuantityValue);
					}
					else
					{
						throw new MatrixException(pgApolloConstants.STR_CONVERSION_FAILED.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", strParameterName));
					}
						
			}
			else
			{
				throw new MatrixException(pgApolloConstants.STR_ERROR_MISSING_PARAMETER.replaceFirst("<LAYERNAME>", strUniqueKey).replaceFirst("<PARAMETER>", strParameterName));
			}
			
		
		}
		return materialsMap;
	}

	
	/**
	 * Method to process Weight
	 * @param materialsMap
	 * @param strMaterialBaseUOM
	 * @param bigDecimalWeight
	 * @param strUniqueKey
	 * @param bIncludeQuantity
	 * @param strParameterName
	 * @param strQuantityParameter
	 * @return
	 * @throws MatrixException
	 */
	private HashMap processWeight(HashMap materialsMap, String strMaterialBaseUOM, BigDecimal bigDecimalWeight,String strUniqueKey, String strParameterName, String strQuantityParameter) throws MatrixException {
		String strQuantityValue = DomainConstants.EMPTY_STRING;
		if((pgApolloConstants.STR_UOM_KILOGRAM.equalsIgnoreCase(strMaterialBaseUOM) || pgApolloConstants.STR_UOM_GRAM.equalsIgnoreCase(strMaterialBaseUOM)))
		{
				strQuantityValue = getQuantityForKilogram(strMaterialBaseUOM, bigDecimalWeight, strQuantityValue);
				strQuantityValue = getQuantityForGram(strMaterialBaseUOM, bigDecimalWeight, strQuantityValue);
				if(UIUtil.isNotNullAndNotEmpty(strQuantityValue))
				{
					materialsMap.put(strQuantityParameter, strQuantityValue);
				}
				else
				{
					throw new MatrixException(pgApolloConstants.STR_CONVERSION_FAILED.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", strParameterName));
				}	
		}
		
		return materialsMap;
	}

	/**
	 * Method to get Quantity for UOM Gram
	 * @param strMaterialBaseUOM
	 * @param bigDecimalWeight
	 * @param strQuantityValue
	 * @return
	 */
	private String getQuantityForGram(String strMaterialBaseUOM, BigDecimal bigDecimalWeight, String strQuantityValue) {
		if(pgApolloConstants.STR_UOM_GRAM.equalsIgnoreCase(strMaterialBaseUOM))
		{
			strQuantityValue = bigDecimalWeight.toString();
		}
		return strQuantityValue;
	}
	
	/**
	 * Method to get Quantity for UOM Kilogram
	 * @param strMaterialBaseUOM
	 * @param bigDecimalWeight
	 * @param strQuantityValue
	 * @return
	 */
	private String getQuantityForKilogram(String strMaterialBaseUOM, BigDecimal bigDecimalWeight, String strQuantityValue) {
		BigDecimal bigDecimalQty;
		if(pgApolloConstants.STR_UOM_KILOGRAM.equalsIgnoreCase(strMaterialBaseUOM))
		{
			bigDecimalQty = bigDecimalWeight.divide(BigDecimal.valueOf(1000.0));
			strQuantityValue = bigDecimalQty.toString();
		}
		return strQuantityValue;
	}

	/**
	 * Method to get Quantity value for SQUARE METER UOM
	 * @param context
	 * @param materialsMap
	 * @param strMaterialBaseUOM
	 * @param strAreaUnit
	 * @param strUniqueKey
	 * @param strParameterName
	 * @return
	 * @throws Exception
	 */
	private String getQuantityForSquareMeter(matrix.db.Context context, HashMap materialsMap, String strMaterialBaseUOM, String strAreaUnit, String strUniqueKey, String strParameterName) throws Exception {
		String strAreaValue;
		String strQuantityValue = DomainConstants.EMPTY_STRING;
		if(pgApolloConstants.STR_UOM_SQUARE_METER.equalsIgnoreCase(strMaterialBaseUOM))
		{
			strAreaValue = getConvertedValueBasedOnUoM (context, materialsMap, strParameterName, strMaterialBaseUOM.toUpperCase(), strAreaUnit);
			if(UIUtil.isNotNullAndNotEmpty(strAreaValue))
			{
				strQuantityValue = strAreaValue;
			}
			else
			{
				throw new MatrixException(pgApolloConstants.STR_CONVERSION_FAILED.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", strParameterName));
			}
		}
		return strQuantityValue;
	}
	/**
	 * Method to get Valid unit Map- Added for Query with UOM
	 * @param context
	 * @param strUnit
	 * @return
	 * @throws Exception
	 */
	public Map getShortNameCATIAUoMMap(matrix.db.Context context) throws Exception {		
		Map validUnitMap = new HashMap();
		try
		{
			String strUnitList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloConfiguration.UOM.CATIAShortNameMapping");		
			if(null!=strUnitList && !strUnitList.isEmpty())
			{
				StringList slUnit = FrameworkUtil.split(strUnitList, pgApolloConstants.CONSTANT_STRING_COMMA);
				String strLocalUnit = DomainConstants.EMPTY_STRING;
				StringList slUoM = new StringList();
	
				if(slUnit!=null && !slUnit.isEmpty())
				{
					for(int i=0;i<slUnit.size();i++)
					{
						strLocalUnit = ((String)slUnit.get(i)).trim();
						if(UIUtil.isNotNullAndNotEmpty(strLocalUnit) && strLocalUnit.contains(pgApolloConstants.CONSTANT_STRING_PIPE))
						{
							slUoM = FrameworkUtil.split(strLocalUnit, pgApolloConstants.CONSTANT_STRING_PIPE);
							validUnitMap.put(slUoM.get(0).toString().trim(),slUoM.get(1).toString().trim());
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return validUnitMap;
	}
	
	
	/**
	 * Method to convert Value of Scientific value to plain string
	 * @param strNumber1
	 * @return
	 */
	public String getValueWithoutScientificNotations(String strNumber) {
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strNumber) && strNumber.contains("E") || strNumber.contains("e"))
			{
				BigDecimal bd = new BigDecimal(strNumber);
				strNumber = bd.toPlainString();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		return strNumber;
	}
	
	/**
	 * Method to fetch Value of Parameter - Added for Query with UOM
	 * @param strQuantity
	 * @return
	 * @throws Exception
	 */
	public String getParameterValue(String strParameterValue) throws Exception {
		
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strParameterValue))
			{
				StringList strList = new StringList();
				if(strParameterValue.contains(pgApolloConstants.CONSTANT_STRING_PIPE))
				{
					strList = FrameworkUtil.split(strParameterValue, pgApolloConstants.CONSTANT_STRING_PIPE);
					if(null!=strList && !strList.isEmpty())
					{
						strParameterValue = (String)strList.get(0);
					}
				}				
				strParameterValue = getValueWithoutScientificNotations(strParameterValue);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		return strParameterValue;
	}
	
	/**
	 * Method to fetch Unit of parameter - Added for Query with UOM
	 * @param strQuantity
	 * @return Unit
	 */
	public String getParameterUnit(String strParameter) throws Exception {
		String strUnit = DomainConstants.EMPTY_STRING;
		if(UIUtil.isNotNullAndNotEmpty(strParameter))
		{
			StringList strList = new StringList();
			if(strParameter.contains(pgApolloConstants.CONSTANT_STRING_PIPE))
			{
				strList = FrameworkUtil.split(strParameter, pgApolloConstants.CONSTANT_STRING_PIPE);
				if(null!=strList && !strList.isEmpty() && strList.size()>1)
				{
					strUnit = (String)strList.get(1);
				}
			}
		}
		return strUnit;
	}
	
	/**
	 * This method will Update Characteristic on the APP 
	 * @param context
	 * @param mapProduct
	 * @param partId
	 * @param dPartObj
	 * @param bPreviousAPPRevExists
	 * @return
	 * @throws Exception
	 */
	public Map processAndUpdateCharacteristics(matrix.db.Context context, Map mapProduct,String partId, DomainObject dPartObj, boolean bPreviousAPPRevExists, String strMode) throws Exception {
	
		boolean bPreviewMode = false;
		boolean bMassUpdate = false;
		String strProcessingMode = pgApolloConstants.STR_COLLABORATION_WITHAPP;
		if(pgApolloConstants.STR_MODE_PREVIEW.equalsIgnoreCase(strMode))
		{
			bPreviewMode = true;
			strProcessingMode = pgApolloConstants.STR_CHARARACTERISTICS_PREVIEW;
		}
		else if(pgApolloConstants.STR_MODE_UPDATE_CHAR.equalsIgnoreCase(strMode))
		{
			strProcessingMode = pgApolloConstants.STR_MANUAL_EVALUATE_WITH_SYNC;
		}
		else if(pgApolloConstants.STR_MODE_MASSUPDATE_UPDATE.equalsIgnoreCase(strMode))
		{
			bMassUpdate = true;
			strProcessingMode = pgApolloConstants.STR_TITLE_MASSUPDATECHARACTERISTICS;
		}
		VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Inside processAndUpdateCharacteristics... " );
		Map returnMapChar = new HashMap();
		String strpgCategory = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
		String strpgCategorySpecifics = DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
		String strCharTitle =  DomainConstants.EMPTY_STRING;
		String strSpecifics = DomainConstants.EMPTY_STRING;
		String strUniqueKey =  DomainConstants.EMPTY_STRING;
		//APOLLO 2018x.5 ALM 35919,35920,32151,33633 Start
		String strPerfCharReportType;
		String strIsDesignDriven;
		//APOLLO 2018x.5 ALM 35919,35920,32151,33633 End
		
		MapList mlPerformanceCharacteristics = new MapList();
		StringList slPlyGroupsOfModel = new StringList();
		
		if(mapProduct.containsKey(pgApolloConstants.KEY_PERFCHAR))
		{
			mlPerformanceCharacteristics = (MapList)mapProduct.get(pgApolloConstants.KEY_PERFCHAR);
		}		
		if(mapProduct.containsKey(pgApolloConstants.KEY_UNIQUEPLYGROUPLIST))
		{
			slPlyGroupsOfModel = (StringList)mapProduct.get(pgApolloConstants.KEY_UNIQUEPLYGROUPLIST);
		}
		MapList mlMissedParameterSets = new MapList();
		MapList mlMissingChars = new MapList();
		MapList mlApplicableUpdatedChars = new MapList();
		Map mapDeletedChar = new HashMap();
		StringList slErrorList;
		StringList slAllErrorList = new StringList();
		String strError;
		Map mapLocalCharMap;
		String strIsActive;
		
		try{
	
			String strTarget = DomainConstants.EMPTY_STRING;
			String strLowerTarget = DomainConstants.EMPTY_STRING;
			String strUpperTarget = DomainConstants.EMPTY_STRING;
			String strLowerSL = DomainConstants.EMPTY_STRING;
			String strUpperSL = DomainConstants.EMPTY_STRING;
			String strLowerRoutineLimit = DomainConstants.EMPTY_STRING;
			String strUpperRoutineLimit = DomainConstants.EMPTY_STRING;
			
			String strTitleChar = DomainConstants.EMPTY_STRING;
			String strSpecificsChar = DomainConstants.EMPTY_STRING;
			String strCharObjID = DomainConstants.EMPTY_STRING;
			String strOverrideAllowedOnChild = DomainConstants.EMPTY_STRING;
			//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
			String strpgCategorySpecificsChar;
			//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
			String strReportType = DomainConstants.EMPTY_STRING;			
			String strpgCharCategory = DomainConstants.EMPTY_STRING;
			
			Map charMap_fromList = new HashMap();
			Map mpConnectedChar = new HashMap();
			HashMap<String, String> charMap = new HashMap<>();	

			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : charListFinal sent By CATIA charList = " + mlPerformanceCharacteristics );
			//loggerSync.debug( "{} : charListFinal sent By CATIA charList = {}" ,strProcessingMode, mlPerformanceCharacteristics );
			
			String strCharCreation = EnoviaResourceBundle.getProperty(context,"emxEngineeringCentral.pgLayeredProduct.NewCharacteristicCreationDuringMassCollaboration");
			
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : strCharCreation = " + strCharCreation );
			
			//Get Related Characteristics objects from APP.
			StringList slSelects = new StringList(6);
			slSelects.addElement(DomainConstants.SELECT_ID);
			slSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE); 
			slSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);
			//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
			slSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
			//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
			slSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TYPE);
			slSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
			slSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN);
			
			StringList slUpdatedCharList = new StringList();
			StringList slNotDesignDrivenList = new StringList();
			StringList slAllNotDesignDrivenCharIdList = new StringList();
			
			String strObjectId_Char = DomainConstants.SELECT_ID;

			StringList relSelects = new StringList(2);
			relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_OVERRIDEALLOWEDONCHILD);
			relSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_MANDATORY_CHARACTERISTIC);

			Pattern relPattern = new Pattern(pgApolloConstants.RELATIONSHIP_PARAMETERAGGREGATION);
			Pattern typePattern = new Pattern(pgApolloConstants.TYPE_PLMPARAMETER);
			MapList mlConnectedCharacteristics =  new MapList();
			if(bPreviewMode)
			{
				if(mapProduct.containsKey(pgApolloConstants.KEY_APPLICABLE_CHARS))
				{
					mlConnectedCharacteristics = (MapList)mapProduct.get(pgApolloConstants.KEY_APPLICABLE_CHARS);
				}
				
				VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Applicable Characteristics = " + mlConnectedCharacteristics );
				//loggerSync.debug( "{} : Applicable Characteristics = {}" , strProcessingMode, mlConnectedCharacteristics);	
			}
			else
			{
				mlConnectedCharacteristics = dPartObj.getRelatedObjects(context,
						relPattern.getPattern(),                //relationship pattern
						typePattern.getPattern(),               //type pattern
						slSelects,                              // object selects
						relSelects,                             // relationship selects
						false,                                   // to direction
						true,                                    // from direction
						(short)1,                             // recursion level
						DomainConstants.EMPTY_STRING,          // object where clause
						null,
						0);
				
				VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Already Connected Characteristicc smlConnectedCharacteristics = " + mlConnectedCharacteristics );
				//loggerSync.debug( "{} : Characteristics connected to APP : {}" , strProcessingMode, mlConnectedCharacteristics);	
			}				
			
			updateTimeStampDetails("Get existing Characteristics (" + mlConnectedCharacteristics.size() + ") completed");
			
			if(!bPreviewMode && pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strCharCreation) && (null == mlConnectedCharacteristics || mlConnectedCharacteristics.isEmpty()))
			{
				VPLMIntegTraceUtil.trace(context, strProcessingMode+" : ERROR : No Characteristic present under APP. ");
				loggerSync.debug( "{} : ERROR : No Characteristic present under APP. No Valid Criteria present.", strProcessingMode);		
				
				StringBuffer sbHistory = new StringBuffer();
				sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
				sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);	
				sbHistory.append(pgApolloConstants.STR_ERROR_NO_CRITERIA);			
				addCustomHistoryOnSync(context, dPartObj.getObjectId(), sbHistory.toString());
				
				StringBuffer strReturnMessageBuf = new StringBuffer();
				strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
				strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
				strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_NO_CRITERIA);
				returnMapChar.put(pgApolloConstants.KEY_MESSAGE, strReturnMessageBuf.toString());
				return returnMapChar;
			}
			updateTimeStampDetails("Update History (if Criteria is not available) completed");
			
			//Iterating the Final Characteristics List
			MapList updateCharMapList = new MapList();
			MapList createNewCharMapList = new MapList();
			boolean bCharUpdate = false;
			
			//APOLLO 2018x.3 ALM 32587 starts
			StringList strCreateNewCharList = new StringList();
			//APOLLO 2018x.3 ALM 32587 ends			
			
			//Part -1 - Getting List of Characteristics to Update
			//Iterate through CharList sent by CATIA
			//APOLLO 2018x.5 A10-562 - Variable and Attribute characteristics are not getting deleted if there is no characteristic extracted from Model during sync Starts
			if(null!=mlPerformanceCharacteristics && !mlPerformanceCharacteristics.isEmpty()) 
			{
			//APOLLO 2018x.5 A10-562 - Variable and Attribute characteristics are not getting deleted if there is no characteristic extracted from Model during sync Ends
			
			
			Map mapCharUnique;

			//Iterating through Characteristics sent by CATIA
			for(int i4=0;i4<mlPerformanceCharacteristics.size();i4++) 
			{
				bCharUpdate = false;
				charMap_fromList = new HashMap();
				charMap_fromList = (Map) mlPerformanceCharacteristics.get(i4);
				
				strpgCategory = DomainConstants.EMPTY_STRING;
				strCharTitle =  DomainConstants.EMPTY_STRING;
				strSpecifics = DomainConstants.EMPTY_STRING;				
				
				strCharTitle = (String)charMap_fromList.get(DomainConstants.ATTRIBUTE_TITLE);
				strSpecifics =  (String)charMap_fromList.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSPECIFIC);
				//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
				strpgCategorySpecifics = (String)charMap_fromList.get(pgApolloConstants.ATTRIBUTE_PG_CATEGORYSPECIFICS);
				//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends

				strpgCategory = (String)charMap_fromList.get(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
				
				strPerfCharReportType =(String)charMap_fromList.get(pgApolloConstants.ATTRIBUTE_REPORT_TYPE);
				
				//APOLLO 2018x.3 ALM 32587 starts
				//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
				strUniqueKey =  DomainConstants.EMPTY_STRING;
				strUniqueKey = (String)charMap_fromList.get(pgApolloConstants.KEY_UNIQUEKEY);
				//APOLLO 2018x.3 ALM 32587 ends
				
				if (UIUtil.isNullOrEmpty(strpgCategory) || UIUtil.isNullOrEmpty(strCharTitle) || UIUtil.isNullOrEmpty(strpgCategorySpecifics))
				{
				//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
					loggerSync.debug( "{} : Following Performance Characteristic will not be processed due to missing mandatory information : ", strProcessingMode);
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
					loggerSync.debug( " PlyGroupName = {}| CategorySpecific = {}| Characteristic = {}| Specific = {}" , strpgCategory , strpgCategorySpecifics , strCharTitle  ,  strSpecifics);
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
					VPLMIntegTraceUtil.trace(context, strProcessingMode+" :  Following Performance Characteristic will not be processed due to missing mandatory information : " );
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
					VPLMIntegTraceUtil.trace(context, strProcessingMode+" : PlyGroupName = " + strpgCategory + " | CategorySpecific = " + strpgCategorySpecifics + " | Characteristic = " + strCharTitle + " | Specific = " + strSpecifics );
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
					continue;
				}
				
				//Iterating through existing Characteristics on APP
				for(int iConnectedChar=0;iConnectedChar<mlConnectedCharacteristics.size();iConnectedChar++) 
				{	
					mpConnectedChar = new HashMap();
					mpConnectedChar = (Map)mlConnectedCharacteristics.get(iConnectedChar);
					strTitleChar = (String)mpConnectedChar.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
					strSpecificsChar = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);

					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
					strpgCategorySpecificsChar = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends

					strpgCharCategory = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);					
					strReportType = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TYPE);
					//Adding for JIRA-115
					strObjectId_Char = (String)mpConnectedChar.get(DomainConstants.SELECT_ID);
					strOverrideAllowedOnChild = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_OVERRIDEALLOWEDONCHILD);
					
					strIsDesignDriven =(String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN);
					
					//Replacing the spaces to match CATIA parameter values
					strSpecificsChar = strSpecificsChar.trim();
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts

					if(strCharTitle.equalsIgnoreCase(strTitleChar) && strSpecifics.equalsIgnoreCase(strSpecificsChar) && strpgCategorySpecifics.equalsIgnoreCase(strpgCategorySpecificsChar) && strpgCategory.equals(strpgCharCategory) && strReportType.equalsIgnoreCase(strPerfCharReportType)) {
						if(strIsDesignDriven.equalsIgnoreCase(pgApolloConstants.RANGE_VALUE_PG_ISDESIGNDRIVEN_YES)) {
							//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends

							if(strOverrideAllowedOnChild.equalsIgnoreCase(pgApolloConstants.STR_TRUE_FLAG)) {
								mapCharUnique = new HashMap();
								mapCharUnique.putAll(charMap_fromList);								
								mapCharUnique.put(DomainConstants.SELECT_ID, strObjectId_Char);							
								//Add it to Update List
								updateCharMapList.add(mapCharUnique);
								//secondList
								slUpdatedCharList.add(strObjectId_Char);
							}
							//TODO - what if overrideallowed is set to FALSE
							bCharUpdate = true;							
						}
						else {
							slNotDesignDrivenList.add(strUniqueKey);
						}				
						//break;
					}
				}
				//If Characteristic does not exist then add it
				if(!bCharUpdate)
				{
					//APOLLO 2018x.3 ALM 32587 starts
					strCreateNewCharList.add(strUniqueKey);
					//APOLLO 2018x.3 ALM 32587 ends
					if(pgApolloCommonUtil.containsInListCaseInsensitive(strUniqueKey, slNotDesignDrivenList))
					{
						strError = new StringBuilder(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_NOT_DESIGNDRIVEN.replaceFirst("<PERFCHAR>", strUniqueKey)).toString();
					}
					else
					{
						strError = new StringBuilder(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_MISSING_CHARACTERISTIC.replaceFirst("<PERFCHAR>", strUniqueKey)).toString();
					}
					slErrorList = new StringList(); 
					if(charMap_fromList.containsKey(pgApolloConstants.STR_ERROR))
					{
						slErrorList = (StringList)charMap_fromList.get(pgApolloConstants.STR_ERROR);
					}
					else if(UIUtil.isNotNullAndNotEmpty(strError))
					{
						slErrorList.add(strError);
					}
					slAllErrorList.addAll(slErrorList);
					charMap_fromList.put(pgApolloConstants.STR_ERROR, slErrorList);									
					createNewCharMapList.add(charMap_fromList);
				}
			}
			//APOLLO 2018x.5 A10-562 - Variable and Attribute characteristics are not getting deleted if there is no characteristic extracted from Model during sync Starts
			}
			
			updateTimeStampDetails("Compared " + mlPerformanceCharacteristics.size() + " Characteristics from Model with existing " + mlConnectedCharacteristics.size() + " Characteristics to separate out Characteristics to update completed");
			
			//APOLLO 2018x.5 A10-562 - Variable and Attribute characteristics are not getting deleted if there is no characteristic extracted from Model during sync Ends
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Set of Characteristics to update smlConnectedCharacteristics = " + slUpdatedCharList );
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Set of Characteristics to newly create smlConnectedCharacteristics = " + createNewCharMapList );
			//InActiveCategorySpec Query changes starts
			String strCategorySpecifics; 
			//InActiveCategorySpec Query changes ends
			//Adding for JIRA-223
			String strMandateChar = DomainConstants.EMPTY_STRING; 
			//APOLLO 2018x.5 A10-500 Starts
			StringList slExcludedCategory = new StringList();
			String strExcludedCategory = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloConfiguration.pgLayeredProduct.excludedCategory");
			if(UIUtil.isNotNullAndNotEmpty(strExcludedCategory))
			{
				slExcludedCategory	= FrameworkUtil.split(strExcludedCategory, pgApolloConstants.CONSTANT_STRING_PIPE);
			}			
			//APOLLO 2018x.5 A10-500 Ends
			
			updateTimeStampDetails("Read Page file to get Excluded Category completed");
			
			boolean bContextpushed = false;
			
			// Push Context is needed, As Context user won't always have access to delete characteristics
			ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			
			bContextpushed = true;
			
			updateTimeStampDetails("Push Context completed");
			
			try
			{

			//Part 2 - Delete Unnecessary Characteristics
			int noCharDeleted = 0;
			for(int iConnectedChar=0;iConnectedChar<mlConnectedCharacteristics.size();iConnectedChar++) 
			{				
				VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Entered loop to check Delete Condition for ATTRIBUTE Characteristics");	
				mpConnectedChar = new HashMap();
				mpConnectedChar = (Map)mlConnectedCharacteristics.get(iConnectedChar);
				strpgCharCategory = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
				strReportType = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TYPE);
				strMandateChar = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_MANDATORY_CHARACTERISTIC);
				strIsDesignDriven =(String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_ISDESIGNDRIVEN);
				strUniqueKey = EvaluateCriteriaAndUpdateCharacteristics.getUniqueKeyForCharacteristics(mpConnectedChar, false);
				//Adding for JIRA-115
				strObjectId_Char = (String)mpConnectedChar.get(DomainConstants.SELECT_ID);
				if(pgApolloConstants.RANGE_VALUE_PG_ISDESIGNDRIVEN_NO.equalsIgnoreCase(strIsDesignDriven))
				{
					slAllNotDesignDrivenCharIdList.add(strObjectId_Char);
				}
				//InActiveCategorySpec Query changes starts
				//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
				strCategorySpecifics = (String)mpConnectedChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
				//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
				//InActiveCategorySpec Query changes ends
				if(null == slUpdatedCharList || slUpdatedCharList.isEmpty() || !(slUpdatedCharList.contains(strObjectId_Char)))
				{
					//InActiveCategorySpec Query changes starts
					//APOLLO 2018x.5 A10-500 Starts
					if(strIsDesignDriven.equalsIgnoreCase(pgApolloConstants.RANGE_VALUE_PG_ISDESIGNDRIVEN_YES) || (!slExcludedCategory.contains(strpgCharCategory) && !slPlyGroupsOfModel.contains(strpgCharCategory)))
					//APOLLO 2018x.5 A10-500 Ends
					//InActiveCategorySpec Query changes ends
					{
						//loggerSync.debug( "{} : To be Deleted Char Category : {} : strUniqueKey = {}" , strProcessingMode, strpgCharCategory, strUniqueKey);
						BusinessObject BO_CharObject  = new BusinessObject(strObjectId_Char);
						boolean bcharExists = BO_CharObject.exists(context);						
						if(bcharExists)
						{						
							if(!pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strMandateChar))
							{
								
								if(slAllNotDesignDrivenCharIdList.contains(strObjectId_Char))
								{
									slAllNotDesignDrivenCharIdList.remove(strObjectId_Char);
								}
								if(bPreviewMode)
								{
									if(pgApolloConstants.RANGE_VALUE_PG_ISDESIGNDRIVEN_NO.equalsIgnoreCase(strIsDesignDriven))
									{
										strUniqueKey = (String)mpConnectedChar.get(pgApolloConstants.KEY_UNIQUEKEY);										
										mapDeletedChar = new HashMap();
										mapDeletedChar.put(pgApolloConstants.STR_ERROR, new StringList(pgApolloConstants.STR_ERROR_PERFCHARACTERISTIC_NOTPRESENT));
										mapDeletedChar.putAll(mpConnectedChar);
										mlMissingChars.add(mapDeletedChar);
									}
									else
									{
										mlMissedParameterSets.add(mpConnectedChar);
									}
									
								}
								else
								{
									CharacteristicServices.deleteCharacteristic(context,strObjectId_Char,partId);								
									VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Deleting the char of objectID = " + strObjectId_Char + " and report Type =" + strReportType + " and Category = " + strpgCharCategory +" and Category Specifics = "+strCategorySpecifics+" and strUniqueKey = "+strUniqueKey);		
									//loggerSync.debug("{} : Deleting the char of objectID = {} and report Type ={} and Category = {} and Category Specifics ={} and strUniqueKey = {}" , strProcessingMode, strObjectId_Char , strReportType , strpgCharCategory,strCategorySpecifics, strUniqueKey); // Log to be removed
									noCharDeleted++;
								}

							}
							else
							{
								VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Existing Mandatory Characteristic Not updated and not deleted = " + strObjectId_Char );
								//loggerSync.debug( "{} : Characteristic Not updated and not deleted : {}" , strProcessingMode, mpConnectedChar); // Trace
								mlApplicableUpdatedChars.add(mpConnectedChar);
							}
						}
					}
					else
					{
						VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Existing Characteristic Not updated and not deleted = " + strObjectId_Char );
						if(bPreviewMode)
						{
							strUniqueKey = (String)mpConnectedChar.get(pgApolloConstants.KEY_UNIQUEKEY);	
							if(!pgApolloCommonUtil.containsInListCaseInsensitive(strUniqueKey, strCreateNewCharList))
							{
								mlApplicableUpdatedChars.add(mpConnectedChar);
							}
						}
						//loggerSync.debug( "{} :Characteristic Not updated and not deleted : {}" , strProcessingMode, mpConnectedChar); // Trace
					}			
				}				
			}	
			
			updateTimeStampDetails("Compare and deletion of unnecessary characteristics (" + noCharDeleted + ") completed");
			
			}
			
			catch(Exception ex2)
			{
				 loggerApolloTrace.error(ex2.getMessage() ,ex2);
				 throw ex2;
			}
			finally
			{
				if(bContextpushed)
				{
					ContextUtil.popContext(context);
				}
				
			}
					
			updateTimeStampDetails("Pop Context completed");
			
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : All Non Design Driven Chars for Chg update : "+slAllNotDesignDrivenCharIdList);	
			//loggerSync.debug( "{} : All Non Design Driven Chars for Chg update : {}" , strProcessingMode, slAllNotDesignDrivenCharIdList);
			
			
			if(bPreviewMode)
			{
				//loggerSync.debug( " mlDeletedChars : {}" , mlDeletedChars);//To be removed
				mlMissingChars.addAll(createNewCharMapList);
			}
			
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Starting Update Characteristics.. ");	
			//Iterate through MapList to Update		
			int iUpdateCharListSize = updateCharMapList.size();
			//Update existing Characteristics
			int noVariableCharToUpdate = 0;
			if(iUpdateCharListSize > 0)
			{
				Map updateCharateristicMap = null;
				Map mpCharAttrUpdate = new HashMap();
				strCharObjID = DomainConstants.EMPTY_STRING;
				
				String strTargetOldValue = DomainConstants.EMPTY_STRING;
				String strLowerTargetOldValue = DomainConstants.EMPTY_STRING;
				String strUpperTargetOldValue = DomainConstants.EMPTY_STRING;
				String strLowerSLOldValue = DomainConstants.EMPTY_STRING;
				String strUpperSLOldValue = DomainConstants.EMPTY_STRING;
				String strLRRLOldValue = DomainConstants.EMPTY_STRING;
				String strURRLOldValue = DomainConstants.EMPTY_STRING;							
								
				boolean isAttributeCharacteristic = false;
				StringList slExcludeAttributeList = new StringList();
				slExcludeAttributeList.add(pgApolloConstants.ATTRIBUTE_REPORT_TYPE);
				slExcludeAttributeList.add(DomainConstants.ATTRIBUTE_TITLE);
				slExcludeAttributeList.add(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSPECIFIC);
				slExcludeAttributeList.add(pgApolloConstants.ATTRIBUTE_PG_CATEGORYSPECIFICS);
				slExcludeAttributeList.add(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
				if(bPreviewMode)
				{
					slExcludeAttributeList.add(pgApolloConstants.STR_ERROR);
				}

				for(int iUpdate=0; iUpdate<iUpdateCharListSize; iUpdate++) 
				{		
					mpCharAttrUpdate = new HashMap();
					updateCharateristicMap = new HashMap();
					updateCharateristicMap = (Map) updateCharMapList.get(iUpdate);
					
					isAttributeCharacteristic = false;					
					if(updateCharateristicMap.containsKey(pgApolloConstants.ATTRIBUTE_REPORT_TYPE) && pgApolloConstants.RANGE_VALUE_ATTRIBUTE.equalsIgnoreCase((String)updateCharateristicMap.get(pgApolloConstants.ATTRIBUTE_REPORT_TYPE) )){
						isAttributeCharacteristic = true;
					}
					
					VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Updating Characteristics = " + iUpdate + " = " + updateCharateristicMap );
					
					strTarget = DomainConstants.EMPTY_STRING;
					strLowerTarget = DomainConstants.EMPTY_STRING;
					strUpperTarget = DomainConstants.EMPTY_STRING;
					strLowerSL = DomainConstants.EMPTY_STRING;
					strUpperSL = DomainConstants.EMPTY_STRING;
					strLowerRoutineLimit = DomainConstants.EMPTY_STRING;
					strUpperRoutineLimit = DomainConstants.EMPTY_STRING;					
					
					strTargetOldValue = DomainConstants.EMPTY_STRING;
					strLowerTargetOldValue = DomainConstants.EMPTY_STRING;
					strUpperTargetOldValue = DomainConstants.EMPTY_STRING;
					strLowerSLOldValue = DomainConstants.EMPTY_STRING;
					strUpperSLOldValue = DomainConstants.EMPTY_STRING;
					strLRRLOldValue = DomainConstants.EMPTY_STRING;
					strURRLOldValue = DomainConstants.EMPTY_STRING;					
									
					Set<String> keysFromMap = updateCharateristicMap.keySet();					
					//To get all key: value
					for(String key: keysFromMap){
						if(key.equals(DomainConstants.SELECT_ID) || key.equalsIgnoreCase(pgApolloConstants.KEY_ISACTIVE) || key.equalsIgnoreCase(pgApolloConstants.KEY_UNIQUEKEY)){
							continue;
						}
						//To read from Char prop entry and put it to a map for attribute setting										
						else 
						{							
							if(key.equals(pgApolloConstants.STRING_PARAMETERVALUE)) {
								strTarget = (String)updateCharateristicMap.get(pgApolloConstants.STRING_PARAMETERVALUE);
								continue;
								}
							
							 if(key.equals(pgApolloConstants.STRING_MINVALUE)) {
								strLowerTarget = (String)updateCharateristicMap.get(pgApolloConstants.STRING_MINVALUE);
								continue;
								}
							
							 if(key.equals(pgApolloConstants.STRING_MAXVALUE)) {
								strUpperTarget = (String)updateCharateristicMap.get(pgApolloConstants.STRING_MAXVALUE);
								continue;
								}
							 
							 if(key.equals(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT)) {
								 strLowerSL = (String)updateCharateristicMap.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT);								
								 continue;
								}
							 if(key.equals(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT)) {
								 strUpperSL = (String)updateCharateristicMap.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT);								
								 continue;
								}
							 //If URRL and LRRL parameters are present, use it instead of USL and LSL
							 if(key.equals(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT)) {
								 strLowerRoutineLimit = (String)updateCharateristicMap.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT);
								 continue;
								}
							 if(key.equals(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT)) {
								 strUpperRoutineLimit = (String)updateCharateristicMap.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT);
								 continue;
								}
							 if(slExcludeAttributeList.contains(key)) {
								 continue;
							 }							 
							 mpCharAttrUpdate.put(key, (String)updateCharateristicMap.get(key));						 
							
						}
					}
					
					strpgCategory = DomainConstants.EMPTY_STRING;
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific starts
					//APOLLO 2018x.6 Jul CW ALM Requirement - A10-929 - New attribute on Characteristic for Category Specific ends
					strCharTitle =  DomainConstants.EMPTY_STRING;
					strSpecifics = DomainConstants.EMPTY_STRING;					
					strCharObjID =  (String)updateCharateristicMap.get(DomainConstants.SELECT_ID);					
					strCharTitle = (String)updateCharateristicMap.get(DomainConstants.ATTRIBUTE_TITLE);
					strSpecifics =  (String)updateCharateristicMap.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSPECIFIC);
					strpgCategory = (String)updateCharateristicMap.get(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
					strUniqueKey =  DomainConstants.EMPTY_STRING;
					strUniqueKey = (String)updateCharateristicMap.get(pgApolloConstants.KEY_UNIQUEKEY);	

					if(bPreviewMode)
					{
						mapLocalCharMap = new HashMap();
						mapLocalCharMap.putAll(mpCharAttrUpdate);
						mapLocalCharMap.put(pgApolloConstants.KEY_UNIQUEKEY, strUniqueKey);	
						slErrorList = (StringList)updateCharateristicMap.get(pgApolloConstants.STR_ERROR);	
						if(null != slErrorList && !slErrorList.isEmpty())
						{
							mapLocalCharMap.put(pgApolloConstants.STR_ERROR, slErrorList);			
						}
						if(!isAttributeCharacteristic)
						{
							mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT, strLowerSL);
							mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT, strLowerRoutineLimit);
							mapLocalCharMap.put(pgApolloConstants.STRING_MINVALUE, strLowerTarget);
							mapLocalCharMap.put(pgApolloConstants.STRING_PARAMETERVALUE, strTarget);
							mapLocalCharMap.put(pgApolloConstants.STRING_MAXVALUE, strUpperTarget);
							mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT, strUpperRoutineLimit);
							mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT, strUpperSL);
						}			
						strIsActive =  (String)updateCharateristicMap.get(pgApolloConstants.KEY_ISACTIVE);						
						if(updateCharateristicMap.containsKey(pgApolloConstants.STR_ERROR))
						{
							mlMissingChars.add(mapLocalCharMap);
						}
						else if(UIUtil.isNotNullAndNotEmpty(strIsActive) && pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strIsActive))
						{
							mlMissedParameterSets.add(mapLocalCharMap);
						}
						else
						{
							mlApplicableUpdatedChars.add(mapLocalCharMap);
						}						
					}
					else 
					{
						try
						{					
							VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Attribute Map to set on Characteristic = " + mpCharAttrUpdate );

							if(!isAttributeCharacteristic)
							{							
								noVariableCharToUpdate++;
								//When UoM is passed from CATIA, get only value from parameter - starts
								// TODO: Need to check if we need to get Units as well for conversion. Not in scope for 18x.3
								strLowerSL = getParameterValue(strLowerSL);
								strLowerRoutineLimit = getParameterValue(strLowerRoutineLimit);
								strLowerTarget = getParameterValue(strLowerTarget);
								strTarget = getParameterValue(strTarget);
								strUpperTarget = getParameterValue(strUpperTarget);
								strUpperRoutineLimit = getParameterValue(strUpperRoutineLimit);
								strUpperSL = getParameterValue(strUpperSL);
								
								ENOICharacteristic charObjPLMParam = ENOCharacteristicFactory.getCharacteristicById(context, strCharObjID);																		

								strTargetOldValue = charObjPLMParam.getNominalValue(context);
								strLowerTargetOldValue = charObjPLMParam.getMinimalValue(context);
								strUpperTargetOldValue = charObjPLMParam.getMaximalValue(context);
								strLowerSLOldValue = charObjPLMParam.getLowerSpecificationLimit(context);
								strUpperSLOldValue = charObjPLMParam.getUpperSpecificationLimit(context);
								strLRRLOldValue = charObjPLMParam.getLowerRoutineReleaseLimit(context);
								strURRLOldValue = charObjPLMParam.getUpperRoutineReleaseLimit(context);		

								if((!isMatch(strLowerSL, strLowerSLOldValue)) ||
										(!isMatch(strLowerRoutineLimit, strLRRLOldValue)) ||
										(!isMatch(strLowerTarget, strLowerTargetOldValue)) ||
										(!isMatch(strTarget, strTargetOldValue)) ||
										(!isMatch(strUpperTarget, strUpperTargetOldValue)) ||
										(!isMatch(strUpperRoutineLimit, strURRLOldValue)) ||
										(!isMatch(strUpperSL, strUpperSLOldValue)))
								{
									charObjPLMParam.setEditMode(true);							
									charObjPLMParam.setRoutineReleaseLimits(strLowerRoutineLimit, strUpperRoutineLimit);
									charObjPLMParam.setSpecificationLimits(strLowerSL, strUpperSL);
									charObjPLMParam.setCharacteristicParamValues(strTarget, strLowerTarget, strUpperTarget, false, false, null);
									charObjPLMParam.commit(context);	

								}
							}				
							DomainObject domCharObject = DomainObject.newInstance(context, strCharObjID);					
							domCharObject.setAttributeValues(context, mpCharAttrUpdate);
						
							VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Setting Limits and Targets on Characteristic completed. ");						

							VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Updating Characteristics = " + iUpdate + " Completed. ");

						}
						catch(Exception ex)
						{
							StringBuffer sbHistory = new StringBuffer(); 
							sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
							sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);
							sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_CHARACTERISTIC);
					//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific starts
							sbHistory.append(strUniqueKey);
					//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific ends
							sbHistory.append(pgApolloConstants.CONSTANT_STRING_COLON);
							sbHistory.append(ex.getLocalizedMessage());					
							addCustomHistoryOnSync(context, partId , sbHistory.toString());					

							StringBuffer strReturnMessageBuf = new StringBuffer();
							strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
							strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
							strReturnMessageBuf.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_CHARACTERISTIC);	
					//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific starts				
							strReturnMessageBuf.append(strUniqueKey);				
							loggerSync.error( " Error while updating Characteristic - {} : {} " , strUniqueKey , ex.getLocalizedMessage() );
							slAllErrorList.add(strReturnMessageBuf.toString());
							returnMapChar.put(pgApolloConstants.STR_ERROR, slAllErrorList);
					//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific ends
							returnMapChar.put(pgApolloConstants.KEY_MESSAGE, strReturnMessageBuf.toString());
							return returnMapChar;

						}
					}
				}
			}
			
			updateTimeStampDetails("Update limits and targets on "+ iUpdateCharListSize +" characteristics (Variable Characteristics = " + noVariableCharToUpdate + " ) completed");
			
			if(bPreviewMode)
			{
				returnMapChar.put(pgApolloConstants.KEY_MISSING_PARAMETER_SET, mlMissedParameterSets);
				returnMapChar.put(pgApolloConstants.KEY_APPLICABLE_CHARS, mlApplicableUpdatedChars);
				returnMapChar.put(pgApolloConstants.KEY_MISSING_CHARS, mlMissingChars);
				return returnMapChar;
			}
			else if(bMassUpdate)
			{
				if(!slAllErrorList.isEmpty())
				{
					returnMapChar.put(pgApolloConstants.KEY_MESSAGE, new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_NO_CHARACTERISTICS));
				}
				else
				{
					returnMapChar.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
					//This is for LPD Global Actions - Mass Update
					updateChgOnCharacteristics(context, partId, mapProduct, strProcessingMode);
				}
				returnMapChar.put(pgApolloConstants.STR_ERROR, slAllErrorList);
				return returnMapChar;
			}
			else
			{
				updateChgOnCharacteristics(context, partId, mapProduct, strProcessingMode);
			}	
			
			updateTimeStampDetails("Updating Chg value on all Characteristics completed");
			
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Update Characteristic completed... " );

			int iCreateNewCharListSize = 0;
			if(null != createNewCharMapList && !createNewCharMapList.isEmpty())
			{
				iCreateNewCharListSize = createNewCharMapList.size();
			}
			//Create new Characteristics
			if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strCharCreation) && iCreateNewCharListSize > 0) {
				
					StringList slError = new StringList(iCreateNewCharListSize);
					VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Error during processAndUpdateCharacteristics. Following characteristics not present to update : " + createNewCharMapList);
					loggerSync.debug( "{} : Error during processAndUpdateCharacteristics. Following characteristics not present to update :  {}" , strProcessingMode, createNewCharMapList);				
					//APOLLO 2018x.3 ALM 32587 starts
					if(null != strCreateNewCharList && !strCreateNewCharList.isEmpty())
					{	
						StringBuffer sbHistory = new StringBuffer();
						String strNewChar = DomainConstants.EMPTY_STRING;
						
						sbHistory = new StringBuffer();
						sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
						sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);	
						sbHistory.append(pgApolloConstants.STR_ERROR_NO_CHARACTERISTICS);			
						addCustomHistoryOnSync(context, dPartObj.getObjectId(), sbHistory.toString());
						
						for(int i=0;i<strCreateNewCharList.size();i++)
						{
							strNewChar = strCreateNewCharList.get(i);
							
							sbHistory = new StringBuffer();
							sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
							sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);							
							if(slNotDesignDrivenList.contains(strNewChar))	{
								sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_NOT_DESIGNDRIVEN.replaceFirst("<PERFCHAR>", strNewChar));								
								slError.addElement(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_NOT_DESIGNDRIVEN.replaceFirst("<PERFCHAR>", strNewChar));
							}
							else {
								sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_MISSING_CHARACTERISTIC.replaceFirst("<PERFCHAR>", strNewChar));
								slError.addElement(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_MISSING_CHARACTERISTIC.replaceFirst("<PERFCHAR>", strNewChar));
							}
							addCustomHistoryOnSync(context, dPartObj.getObjectId(), sbHistory.toString());
						}						
					}
					//APOLLO 2018x.3 ALM 32587 ends
					StringBuffer strReturnMessageBuf = new StringBuffer();
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
					strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
					strReturnMessageBuf.append(pgApolloConstants.STR_ERROR_NO_CHARACTERISTICS);
					if(null != slError && !slError.isEmpty())
					{						
						strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_CARET);
						strReturnMessageBuf.append(StringUtil.join(slError, pgApolloConstants.CONSTANT_STRING_CARET));		
					}
					returnMapChar.put(pgApolloConstants.KEY_MESSAGE, strReturnMessageBuf.toString());
					return returnMapChar;
			}
			
			loggerSync.debug( "{} : Characteristics Successfully Updated...", strProcessingMode);
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Characteristics Successfully Updated >> ");
			
			returnMapChar.put(pgApolloConstants.KEY_MESSAGE, pgApolloConstants.STR_SUCCESS);
			return returnMapChar;
			
		} catch (Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
			
			StringBuffer sbHistory = new StringBuffer(); 
			sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP);
			sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR);
			sbHistory.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_CHARACTERISTIC);
			//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific starts
			if(UIUtil.isNotNullAndNotEmpty(strUniqueKey))
			{
				sbHistory.append(strUniqueKey);
				sbHistory.append(pgApolloConstants.CONSTANT_STRING_COLON);
			}
			sbHistory.append(ex.getLocalizedMessage());
			
			addCustomHistoryOnSync(context, partId , sbHistory.toString());	
			//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific starts
			loggerSync.error( "{} : Error while updating Characteristic - {} : {}" ,strProcessingMode, strUniqueKey  , ex.getLocalizedMessage() );			
			VPLMIntegTraceUtil.trace(context, strProcessingMode+" : Error while updating Characteristic - " + strUniqueKey + " : " + ex.getLocalizedMessage());
			//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific ends
			StringBuffer strReturnMessageBuf = new StringBuffer();
			strReturnMessageBuf.append(pgApolloConstants.STR_ERROR);
			strReturnMessageBuf.append(pgApolloConstants.CONSTANT_STRING_COLON);
			strReturnMessageBuf.append(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP_ERROR_UPDATE_CHARACTERISTIC);		
			//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific starts
			if(UIUtil.isNotNullAndNotEmpty(strUniqueKey))
			{
				strReturnMessageBuf.append(strUniqueKey);
			}
			//APOLLO 2018x.5 ALM Requirement 33633 - A10-419 - New attribute on Characteristic for Design Specific ends
			slAllErrorList.add(strReturnMessageBuf.toString());
			returnMapChar.put(pgApolloConstants.STR_ERROR, slAllErrorList);
			returnMapChar.put(pgApolloConstants.KEY_MESSAGE, strReturnMessageBuf.toString());
			return returnMapChar;
		}	
	}
	
	
	/**
	 * This method validates the Range for the Limits and Targets
	 * @param context
	 * @param strLowerSL
	 * @param strLowerRoutineLimit
	 * @param strLowerTarget
	 * @param strTarget
	 * @param strUpperTarget
	 * @param strUpperRoutineLimit
	 * @param strUpperSL
	 * @return
	 * @throws Exception
	 */
	public static boolean validateRangeForLimitAndTargets(matrix.db.Context context, String strLowerSL, String strLowerRoutineLimit, String strLowerTarget, String strTarget, String strUpperTarget, String strUpperRoutineLimit, String strUpperSL) throws Exception
	{
			boolean bReturnError = false;
			double dValue, dValueNext;
			
			StringList slListOfTargets = new StringList();
			
			if(UIUtil.isNotNullAndNotEmpty(strLowerSL) && UIUtil.isNotNullAndNotEmpty(strLowerRoutineLimit))
			{
				dValue = Double.parseDouble(strLowerSL);
				dValueNext = Double.parseDouble(strLowerRoutineLimit);
				if(Double.compare(dValue,dValueNext) > 0)
				{
					bReturnError = true;
					return bReturnError;
				}
			}
			if(UIUtil.isNotNullAndNotEmpty(strUpperRoutineLimit) && UIUtil.isNotNullAndNotEmpty(strUpperSL))
			{
			
				dValue = Double.parseDouble(strUpperRoutineLimit);
				dValueNext = Double.parseDouble(strUpperSL);
			
				if(Double.compare(dValue,dValueNext) > 0)
				{
					bReturnError = true;
					return bReturnError;
				}
			}
			if(UIUtil.isNotNullAndNotEmpty(strLowerRoutineLimit))
			{
				slListOfTargets.addElement(strLowerRoutineLimit);
			}
			if(UIUtil.isNotNullAndNotEmpty(strLowerTarget))
			{
				slListOfTargets.addElement(strLowerTarget);
			}
			if(UIUtil.isNotNullAndNotEmpty(strTarget))
			{
				slListOfTargets.addElement(strTarget);
			}
			if(UIUtil.isNotNullAndNotEmpty(strUpperTarget))
			{
				slListOfTargets.addElement(strUpperTarget);
			}
			if(UIUtil.isNotNullAndNotEmpty(strUpperRoutineLimit))
			{
				slListOfTargets.addElement(strUpperRoutineLimit);
			}	
		
			String strValue = DomainConstants.EMPTY_STRING;
			String strValueNext = DomainConstants.EMPTY_STRING;

			for(int i=0 ; i < slListOfTargets.size()-1 ; i++)
			{
				strValue = slListOfTargets.get(i).toString();
				strValueNext = slListOfTargets.get(i+1).toString();
				
				dValue = Double.parseDouble(strValue);
				dValueNext = Double.parseDouble(strValueNext);
										
				if(Double.compare(dValue,dValueNext) >= 0)
				{
					bReturnError = true;
					break;
				}
			}
		
		return bReturnError;
	}
	
	//Update Enterprise Part details
	/*
	 * This will copy attributes from VPMReference to APP
	 * Please note that this method is also called from pgDSMLayeredProductSyncUtil.
	 * @param context
	 * @param partId
	 * @param domVPMRefObj
	 * @throws Exception
	 */
	public static void updateEnterprisePart(matrix.db.Context context, String partId, DomainObject domVPMRefObj) throws Exception{
		
		StringList slSelectables = new StringList();		
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_VERSION_COMMENT);		
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PNG_GROSS_WEIGHT+"]");
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PGPRIMARYORGANIZATION+"]");
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PGBUSINESSAREA+"]");
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM+"]");
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM+"]");
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS+"]");
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PGFRANCHISEPLATFORM+"]");
		slSelectables.addElement("attribute["+pgApolloConstants.ATTRIBUTE_PGSIZE+"]");
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION);
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION);
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION);

		//APOLLO 2018x.5 A10-568 - Net Weight Related Changes for CATIA APP Starts
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_CATIA_PGNETWEIGHT);
		//APOLLO 2018x.5 A10-568 - Net Weight Related Changes for CATIA APP Ends
		
		//A10-992 2018x.6 Collaborate with APP - Copy three attribute values from Generic Model APP during sync Starts
		slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL);
		//A10-992 2018x.6 Collaborate with APP - Copy three attribute values from Generic Model APP during sync Ends
		Map hmNewAttributeMapAPP = new HashMap();
		Map hmLPDGenericModelAttrs = new HashMap();
					
		try{
			Map productInfoMap = (Map)domVPMRefObj.getInfo(context, slSelectables);	
			VPLMIntegTraceUtil.trace(context, "MassCollab: updateEnterprisePart = productInfoMap = " + productInfoMap  );
			
			if(!productInfoMap.isEmpty())
			{
				String strTitle = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
				String strDescription = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
				String strVComment = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_VERSION_COMMENT);
				String strGrossWeight = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PNG_GROSS_WEIGHT);
				String strPrimaryOrganization = (String)productInfoMap.get("attribute["+pgApolloConstants.ATTRIBUTE_PGPRIMARYORGANIZATION+"]");
				String strBusinessArea = (String)productInfoMap.get("attribute["+pgApolloConstants.ATTRIBUTE_PGBUSINESSAREA+"]");
				String strProductCategoryPlatform = (String)productInfoMap.get("attribute["+pgApolloConstants.ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM+"]");
				String strProductTechnologyPlatform = (String)productInfoMap.get("attribute["+pgApolloConstants.ATTRIBUTE_PGPRODUCTTECHNOLOGYPLATFORM+"]");
				String strProductTechnologyChassis = (String)productInfoMap.get("attribute["+pgApolloConstants.ATTRIBUTE_PGPRODUCTTECHNOLOGYCHASSIS+"]");
				String strFranchisePlatform = (String)productInfoMap.get("attribute["+pgApolloConstants.ATTRIBUTE_PGFRANCHISEPLATFORM+"]");				
				String strSize = (String)productInfoMap.get("attribute["+pgApolloConstants.ATTRIBUTE_PGSIZE+"]");	
				//APOLLO 2018x.5 A10-568 - Net Weight Related Changes for CATIA APP Starts
				String strNetWeight = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_CATIA_PGNETWEIGHT);
				//APOLLO 2018x.5 A10-568 - Net Weight Related Changes for CATIA APP Ends
				
				String sModelType = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
				String sRegion = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGREGION);
				String sSubRegion = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGSUBREGION);
				String sDefinition = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGDEFINITION);
				
				hmNewAttributeMapAPP = validateAndUpdatePartAttributeDetails(hmNewAttributeMapAPP, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDMODELTYPE, sModelType, false);
				hmNewAttributeMapAPP = validateAndUpdatePartAttributeDetails(hmNewAttributeMapAPP, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDREGION, sRegion, false);
				hmNewAttributeMapAPP = validateAndUpdatePartAttributeDetails(hmNewAttributeMapAPP, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION, sSubRegion, false);
				hmNewAttributeMapAPP = validateAndUpdatePartAttributeDetails(hmNewAttributeMapAPP, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDDEFINITION, sDefinition, false);

				
				//APOLLO 2018x.6 A10-992 Copy three attribute values from Generic Model APP during sync Starts
				String strLPDGenericModelNameRev = (String)productInfoMap.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDORIGINATEDFROMGENERICMODEL);
				if(UIUtil.isNotNullAndNotEmpty(strLPDGenericModelNameRev)) {
					hmLPDGenericModelAttrs = fetchGenericModelAPPAttributes(context,strLPDGenericModelNameRev);
					if(!hmLPDGenericModelAttrs.isEmpty()) {
						hmNewAttributeMapAPP.putAll(hmLPDGenericModelAttrs);
					}
				}
				//APOLLO 2018x.6 A10-992 Copy three attribute values from Generic Model APP during sync Starts
				
				DomainObject partObj = DomainObject.newInstance(context, partId);
				hmNewAttributeMapAPP.put(DomainConstants.ATTRIBUTE_TITLE, strTitle);
				hmNewAttributeMapAPP.put(pgApolloConstants.ATTRIBUTE_V_NAME_ENTERPRISE_PART, strTitle);
				hmNewAttributeMapAPP.put(pgApolloConstants.ATTRIBUTE_REASON_FOR_CHANGE, strVComment);
				
				
				
				if(UIUtil.isNotNullAndNotEmpty(strGrossWeight))
				{
					hmNewAttributeMapAPP.put(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL,strGrossWeight);
				}
				//APOLLO 2018x.5 A10-568 - Net Weight Related Changes for CATIA APP Starts
				if(UIUtil.isNotNullAndNotEmpty(strNetWeight))
				{
					hmNewAttributeMapAPP.put(pgApolloConstants.ATTRIBUTE_NET_WEIGHT,strNetWeight);
				}
				//APOLLO 2018x.5 A10-568 - Net Weight Related Changes for CATIA APP Ends
				
				if(UIUtil.isNotNullAndNotEmpty(strSize))
				{
					hmNewAttributeMapAPP.put(pgApolloConstants.ATTRIBUTE_PG_DSMPRODUCTSIZE,strSize);
				}
				
				partObj.setAttributeValues(context, hmNewAttributeMapAPP);
				partObj.setDescription(context, strDescription);	
								
				
				
				String strRelPattern = pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM + "," + pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS + "," + pgApolloConstants.RELATIONSHIP_PRIMARYORGANIZATION + "," + pgApolloConstants.RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA;
				
				StringList slObjectSelect = new StringList();
				slObjectSelect.addElement(DomainConstants.SELECT_ID);
				slObjectSelect.addElement(DomainConstants.SELECT_NAME);

				StringList slRelSelect = new StringList();
				slRelSelect.addElement("attribute[" + pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE + "]");
				slRelSelect.addElement("attribute[" + pgApolloConstants.ATTRIBUTE_PG_CHASSIS_TYPE + "]");
				slRelSelect.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);
				slRelSelect.addElement(DomainRelationship.SELECT_ID);
				
				StringList slObjectRelsToBeDisconnected = new StringList();
				StringList slObjectNamesToBeDisconnected = new StringList();

				MapList mlConnectedObjects = partObj.getRelatedObjects(context,
						strRelPattern,
						DomainConstants.QUERY_WILDCARD,
						slObjectSelect,//Object Select
						slRelSelect,//rel Select
						false,//get To
						true,//get From
						(short)1,//recurse level
						null,//where Clause
						null,
						0);
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: mlConnectedObjects = " + mlConnectedObjects );
				
				boolean hasConnectedCategoryPlatform = false;
				boolean hasConnectedTechnologyPlatform = false;
				boolean hasConnectedTechnologyChassis = false;
				boolean hasConnectedFranchise = false;
				boolean hasConnectedPrimaryOrganization = false;
				boolean hasConnectedBusinessArea = false;
				
				String strObjectName;
				String strRelId;

				if(null != mlConnectedObjects && !mlConnectedObjects.isEmpty())
				{
					Map mpT = new HashMap();
					String strRelName = DomainConstants.EMPTY_STRING;
					String strAttributeValue = DomainConstants.EMPTY_STRING;
					for(int i=0 ; i< mlConnectedObjects.size() ; i++ )
					{
						mpT = new HashMap();
						mpT = (Map) mlConnectedObjects.get(i);
						strRelName = (String) mpT.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
						strRelId = (String) mpT.get(DomainRelationship.SELECT_ID);
						strObjectName = (String) mpT.get(DomainConstants.SELECT_NAME);
						if(UIUtil.isNotNullAndNotEmpty(strObjectName)) 
						{
							if(pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS.equalsIgnoreCase(strRelName))
							{
								if(strObjectName.equals(strProductTechnologyChassis))
								{
									hasConnectedTechnologyChassis = true;
								}
								else
								{
									slObjectRelsToBeDisconnected.add(strRelId);
									slObjectNamesToBeDisconnected.add(strObjectName);
								}

							}
							else if(pgApolloConstants.RELATIONSHIP_PRIMARYORGANIZATION.equalsIgnoreCase(strRelName))
							{
								if(strObjectName.equals(strPrimaryOrganization))
								{
									hasConnectedPrimaryOrganization = true;
								}
								else
								{
									slObjectRelsToBeDisconnected.add(strRelId);
									slObjectNamesToBeDisconnected.add(strObjectName);
								}

							}
							else if(pgApolloConstants.RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA.equalsIgnoreCase(strRelName))
							{
								if(strObjectName.equals(strBusinessArea))
								{
									hasConnectedBusinessArea = true;
								}
								else
								{
									slObjectRelsToBeDisconnected.add(strRelId);
									slObjectNamesToBeDisconnected.add(strObjectName);
								}

							}
							else if(pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM.equalsIgnoreCase(strRelName))
							{
								strAttributeValue = (String) mpT.get("attribute[" + pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE + "]");
								if(pgApolloConstants.STR_PRODUCT_CATEGORY_PLATFORM.equalsIgnoreCase(strAttributeValue))
								{
									if(strObjectName.equals(strProductCategoryPlatform))
									{
										hasConnectedCategoryPlatform = true;
									}
									else
									{
										slObjectRelsToBeDisconnected.add(strRelId);
										slObjectNamesToBeDisconnected.add(strObjectName);
									}

								}
								if(pgApolloConstants.STR_PRODUCT_TECHNOLOGY_PLATFORM.equalsIgnoreCase(strAttributeValue))
								{
									if(strObjectName.equals(strProductTechnologyPlatform))
									{
										hasConnectedTechnologyPlatform = true;
									}
									else
									{
										slObjectRelsToBeDisconnected.add(strRelId);
										slObjectNamesToBeDisconnected.add(strObjectName);
									}
								}
								if(pgApolloConstants.STR_FRANCHISE_PLATFORM.equalsIgnoreCase(strAttributeValue))
								{
									if(strObjectName.equals(strFranchisePlatform))
									{
										hasConnectedFranchise = true;
									}
									else
									{
										slObjectRelsToBeDisconnected.add(strRelId);
										slObjectNamesToBeDisconnected.add(strObjectName);
									}
								}
							}	
						}
					}
				}
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: hasConnectedCategoryPlatform = " + hasConnectedCategoryPlatform  );
				VPLMIntegTraceUtil.trace(context, "MassCollab: hasConnectedTechnologyPlatform = " + hasConnectedTechnologyPlatform  );
				VPLMIntegTraceUtil.trace(context, "MassCollab: hasConnectedTechnologyChassis = " + hasConnectedTechnologyChassis  );
				VPLMIntegTraceUtil.trace(context, "MassCollab: hasConnectedPrimaryOrganization = " + hasConnectedPrimaryOrganization  );
				VPLMIntegTraceUtil.trace(context, "MassCollab: hasConnectedBusinessArea = " + hasConnectedBusinessArea  );
				VPLMIntegTraceUtil.trace(context, "MassCollab: hasConnectedFranchise = " + hasConnectedFranchise  );
				VPLMIntegTraceUtil.trace(context, "MassCollab: slObjectRelsToBeDisconnected = " + slObjectRelsToBeDisconnected  );
				VPLMIntegTraceUtil.trace(context, "MassCollab: slObjectNamesToBeDisconnected = " + slObjectNamesToBeDisconnected  );

				if(!slObjectRelsToBeDisconnected.isEmpty())
				{
					DomainRelationship.disconnect(context, slObjectRelsToBeDisconnected.toArray(new String []{}));			
				}
				DomainObject doNewObject = null;
				DomainRelationship domRelObject = null;
				StringBuffer sWhereExp = new StringBuffer();
				StringList addSelectList=new StringList();
				addSelectList.add("attribute["+pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE+"]");
				if(UIUtil.isNotNullAndNotEmpty(strBusinessArea))
				{
					if(!(hasConnectedBusinessArea))
					{
						doNewObject = null;
						doNewObject = getDomainObject (context, pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA, strBusinessArea, DomainConstants.QUERY_WILDCARD, null, null);
						if(null != doNewObject)
						{
							DomainRelationship.connect(context, partObj, pgApolloConstants.RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA, doNewObject);
							VPLMIntegTraceUtil.trace(context, "MassCollab: Business Area connection successful. ");
						}
					}
				}
				if(UIUtil.isNotNullAndNotEmpty(strProductCategoryPlatform))
				{
					if(!(hasConnectedCategoryPlatform))
					{
						sWhereExp = new StringBuffer();
						sWhereExp.append("attribute[");
						sWhereExp.append(pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE);
						sWhereExp.append("]=='");
						sWhereExp.append(pgApolloConstants.STR_PRODUCT_CATEGORY_PLATFORM);
						sWhereExp.append("'");
						doNewObject = null;
						doNewObject = getDomainObject (context, pgApolloConstants.TYPE_PG_PLATFORM, strProductCategoryPlatform, DomainConstants.QUERY_WILDCARD, sWhereExp.toString(), addSelectList);
						if(null != doNewObject)
						{
							domRelObject = DomainRelationship.connect(context, partObj, pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM, doNewObject);
							domRelObject.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE, pgApolloConstants.STR_PRODUCT_CATEGORY_PLATFORM);	
							VPLMIntegTraceUtil.trace(context, "MassCollab: Product Category Platform connection successful. ");
						}
					}			
				}
				if(UIUtil.isNotNullAndNotEmpty(strProductTechnologyPlatform))
				{	
					if(!(hasConnectedTechnologyPlatform))
					{
						sWhereExp = new StringBuffer();
						sWhereExp.append("attribute[");
						sWhereExp.append(pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE);
						sWhereExp.append("]=='");
						sWhereExp.append(pgApolloConstants.STR_PRODUCT_TECHNOLOGY_PLATFORM);
						sWhereExp.append("'");
						doNewObject = null;
						doNewObject = getDomainObject (context, pgApolloConstants.TYPE_PG_PLATFORM, strProductTechnologyPlatform, DomainConstants.QUERY_WILDCARD, sWhereExp.toString(), addSelectList);
						if(null != doNewObject)
						{						
							domRelObject = DomainRelationship.connect(context, partObj, pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM, doNewObject);
							domRelObject.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE, pgApolloConstants.STR_PRODUCT_TECHNOLOGY_PLATFORM);	
							VPLMIntegTraceUtil.trace(context, "MassCollab: Product Technology Platform connection successful. ");
						}
					}
				}				
				if(UIUtil.isNotNullAndNotEmpty(strFranchisePlatform))
				{	
					if(!(hasConnectedFranchise))
					{						
						sWhereExp = new StringBuffer();
						sWhereExp.append("attribute[");
						sWhereExp.append(pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE);
						sWhereExp.append("]=='");
						sWhereExp.append(pgApolloConstants.STR_FRANCHISE_PLATFORM);
						sWhereExp.append("'");
						doNewObject = null;
						doNewObject = getDomainObject (context, pgApolloConstants.TYPE_PG_PLATFORM, strFranchisePlatform, DomainConstants.QUERY_WILDCARD, sWhereExp.toString(), addSelectList);
						if(null != doNewObject)
						{						
							domRelObject = DomainRelationship.connect(context, partObj, pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM, doNewObject);
							domRelObject.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE, pgApolloConstants.STR_FRANCHISE_PLATFORM);	
							VPLMIntegTraceUtil.trace(context, "MassCollab: Franchise Platform connection successful. ");
						}
					}
				}
				if(UIUtil.isNotNullAndNotEmpty(strProductTechnologyChassis))
				{				
					if(!(hasConnectedTechnologyChassis))
					{
						addSelectList=new StringList();
						addSelectList.add("attribute["+pgApolloConstants.ATTRIBUTE_PG_CHASSIS_TYPE+"]");
						sWhereExp = new StringBuffer();
						sWhereExp.append("attribute[");
						sWhereExp.append(pgApolloConstants.ATTRIBUTE_PG_CHASSIS_TYPE);
						sWhereExp.append("]=='");
						sWhereExp.append(pgApolloConstants.STR_PRODUCT_TECHNOLOGY);
						sWhereExp.append("'");
						doNewObject = null;
						doNewObject = getDomainObject (context, pgApolloConstants.TYPE_PG_CHASSIS, strProductTechnologyChassis, DomainConstants.QUERY_WILDCARD, sWhereExp.toString(), addSelectList);
						if(null != doNewObject)
						{	
							domRelObject = DomainRelationship.connect(context, partObj, pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS, doNewObject);
							domRelObject.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PG_CHASSIS_TYPE, pgApolloConstants.STR_PRODUCT_TECHNOLOGY);
							VPLMIntegTraceUtil.trace(context, "MassCollab: Product Technology Chassis connection successful. ");
						}
					}
				}
				if(UIUtil.isNotNullAndNotEmpty(strPrimaryOrganization))
				{		
					if(!(hasConnectedPrimaryOrganization))				
					{
						doNewObject = null;
						doNewObject = getDomainObject (context, pgApolloConstants.TYPE_PG_PLIORGANIZATION, strPrimaryOrganization, DomainConstants.QUERY_WILDCARD, null, null);
						if(null != doNewObject)
						{
							DomainRelationship.connect(context, partObj, pgApolloConstants.RELATIONSHIP_PRIMARYORGANIZATION, doNewObject);
							VPLMIntegTraceUtil.trace(context, "MassCollab: Primary Organization connection successful. ");
						}
					}
				}				
			}			
		}catch (Exception exp)
		{
			VPLMIntegTraceUtil.trace(context, "MassCollab: Exception in updateEnterprisePart : "  + exp.getLocalizedMessage() );
			exp.printStackTrace();
			throw exp;	
		}
	}

	
	/**
	 * Method to validate and Update attribute Map for APP
	 * @param hmNewAttributeMapAPP
	 * @param sAttributeName
	 * @param sAttributeValue
	 * @param bAllowEmptyValue 
	 * @return
	 */
	private static Map validateAndUpdatePartAttributeDetails(Map hmNewAttributeMapAPP, String sAttributeName, String sAttributeValue, boolean bAllowEmptyValue) 
	{
		if(UIUtil.isNotNullAndNotEmpty(sAttributeValue))
		{
			hmNewAttributeMapAPP.put(sAttributeName,sAttributeValue);
		}
		else if(bAllowEmptyValue)
		{
			hmNewAttributeMapAPP.put(sAttributeName,DomainConstants.EMPTY_STRING);
		}
		return hmNewAttributeMapAPP;
	}


	/**
	 * @param context
	 * @param strLPDGenericModelNameRev in Name|Rev format
	 * @return Map with attributes fetched from Generic Model APP 
	 * @throws Exception
	 */
	private static Map fetchGenericModelAPPAttributes(matrix.db.Context context, String strLPDGenericModelNameRev) throws Exception {
		Map mpGenericModelAttrs = new HashMap<>();
		StringList slLPDGenericModelNameRev = StringUtil.split(strLPDGenericModelNameRev, pgApolloConstants.CONSTANT_STRING_PIPE);
		String strGenericModelVPMRefId;
		String strGenericModelName;
		String strGenericModelRev;
		String sProductExposedToChildren;
		String sProductMarketedAsChildrenProduct;
		String sDoestheProductRequireChildSafeDesign;

		StringList slGenericModelAPPSelects = new StringList();
		slGenericModelAPPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN);
		slGenericModelAPPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT);
		slGenericModelAPPSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN);
		
		if(slLPDGenericModelNameRev.size() == 2) {
			strGenericModelName = slLPDGenericModelNameRev.get(0);
			strGenericModelRev = slLPDGenericModelNameRev.get(1);
			strGenericModelVPMRefId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE,strGenericModelName , strGenericModelRev);
			
			String strGenericModelAPP = pgApolloCommonUtil.fetchAPPObjectId(context, strGenericModelVPMRefId);
			if(UIUtil.isNotNullAndNotEmpty(strGenericModelAPP)) {
				DomainObject doGenericModelAPP = DomainObject.newInstance(context, strGenericModelAPP);
				mpGenericModelAttrs = doGenericModelAPP.getInfo(context, slGenericModelAPPSelects);
				if(!mpGenericModelAttrs.isEmpty())
				{
					sProductExposedToChildren = (String)mpGenericModelAttrs.get(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN);
					sProductMarketedAsChildrenProduct = (String)mpGenericModelAttrs.get(pgApolloConstants.SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT);
					sDoestheProductRequireChildSafeDesign = (String)mpGenericModelAttrs.get(pgApolloConstants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN);
					
					mpGenericModelAttrs = new HashMap();
					mpGenericModelAttrs.put(pgApolloConstants.ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN, sProductExposedToChildren);
					mpGenericModelAttrs.put(pgApolloConstants.ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT, sProductMarketedAsChildrenProduct);
					mpGenericModelAttrs.put(pgApolloConstants.ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN, sDoestheProductRequireChildSafeDesign);

				}
			}
			
		}
		
		return mpGenericModelAttrs;
	}


	/**
	 * This method returns DomainObject for a Given Type name Revision and where clause. 
	 * @param context
	 * @param strTYPE
	 * @param strNAME
	 * @param strREV
	 * @param strWhrClause
	 * @param addSelectList
	 * @return
	 * @throws Exception
	 */
	private static DomainObject getDomainObject (matrix.db.Context context, String strTYPE, String strNAME, String strREV, String strWhrClause, StringList addSelectList) throws Exception
	{
		DomainObject doObject = null;
		String strObjectId = "";
		try
		{	
				StringList slSelect = new StringList();
				slSelect.addElement(DomainConstants.SELECT_ID);
				slSelect.addElement(DomainConstants.SELECT_REVISION);
				if(null!=addSelectList && !addSelectList.isEmpty())
				{
					slSelect.addAll(addSelectList);
				}	
				MapList mapListObjects = DomainObject.findObjects(context,
						  strTYPE,
						  strNAME,
						  strREV,
						  null,
						  pgApolloConstants.VAULT_ESERVICE_PRODUCTION,
						  strWhrClause,
						  true,
						  slSelect);
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: mapListPartst : "  + mapListObjects );				
				
				if(null != mapListObjects && !mapListObjects.isEmpty())
				{
					mapListObjects.sort(DomainConstants.SELECT_REVISION, "descending", "string");
					strObjectId = (String) ((Map)mapListObjects.get(0)).get(DomainConstants.SELECT_ID);
					doObject = DomainObject.newInstance(context, strObjectId);
				}
		}
		catch (Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage(), ex);
			throw ex;
		}
		
		return doObject;
	}

	/**
	 * This method returns the Category of a characteristic 
	 * @param context
	 * @param title
	 * @param EBOMUpdateLog
	 * @return
	 * @throws Exception
	 */
	public String getCharacteristicCategory(matrix.db.Context context,String title, BufferedWriter EBOMUpdateLog) throws Exception {
		String category = DomainConstants.EMPTY_STRING;
		try{
			BusinessObject plCharacteristicBusObj = new BusinessObject(pgApolloConstants.TYPE_PG_PLICHARACTERISTIC,title,"-",pgApolloConstants.VAULT_ESERVICE_PRODUCTION);
			DomainObject domPLICharacteristic = DomainObject.newInstance(context,plCharacteristicBusObj);
			String strWhereClause = pgApolloConstants.SELECT_ATTRIBUTE_PG_PLRELVALUE+"==\'"+pgApolloConstants.RANGE_CHARACTERISTICTYPE+"\'";
			MapList connectedCategory = domPLICharacteristic.getRelatedObjects(context,
					pgApolloConstants.RELATIONSHIP_PG_PLRELATEDDATA,
					pgApolloConstants.TYPE_PG_PLICHARACTERISTICTYPE,
					StringList.create(DomainObject.SELECT_NAME),
					StringList.create(pgApolloConstants.SELECT_ATTRIBUTE_PG_PLRELVALUE),
					false,
					true,
					(short)1,
					"",
					strWhereClause,
					0);

			if(!connectedCategory.isEmpty()){
				Map connectedCategoryMap = (Map)connectedCategory.get(0);
				category = (String)connectedCategoryMap.get(DomainConstants.SELECT_NAME);
			}

		}catch (Exception exp){
			exp.printStackTrace();
			VPLMIntegTraceUtil.trace(context, "MassCollab: Exception in getCharacteristicCategory >> " + exp.getLocalizedMessage());
		}
		return category;
	}

	/**
	 * This method created Change Order and Change Action for List of Parts
	 * @param context
	 * @param partIdList
	 * @return
	 * @throws Exception
	 */
	public String createChangeObjects(matrix.db.Context context, StringList partIdList) throws Exception 
	{
		String strReturnMessage = pgApolloConstants.STR_SUCCESS;
		boolean isTransactionStarted = false;
		try
		{
			if(null!=partIdList && !partIdList.isEmpty())
			{
				StringList strNewPartIdList = new StringList();
				int iPartListSize = partIdList.size();
				String[] partIdArr = new String[iPartListSize];
				for(int k=0 ; k < iPartListSize; k++){
					partIdArr[k] = partIdList.get(k);
				}

				Map mpProposedCAInfo = ChangeUtil.getChangeObjectsInProposed(context, new StringList(DomainConstants.SELECT_ID), partIdArr, 1);
				Map mpRealizedCAInfo = ChangeUtil.getChangeObjectsInRealized(context, new StringList(DomainConstants.SELECT_ID), partIdArr, 1);

				boolean bChangeExist = false;
				String strPartId = null;
				Map caMap = null;
				MapList proposedOrRealizedchangeActionList = null;
				Iterator proposedChangeItr = null;
				for(int iPart=0; iPart<iPartListSize; iPart++){
					bChangeExist = false;
					strPartId = partIdList.get(iPart);
					proposedOrRealizedchangeActionList = (MapList)mpProposedCAInfo.get(strPartId);
					if(null != proposedOrRealizedchangeActionList && proposedOrRealizedchangeActionList.isEmpty() && mpRealizedCAInfo.containsKey(strPartId))
					{
						proposedOrRealizedchangeActionList = (MapList)mpRealizedCAInfo.get(strPartId);	
					}
					if(null != proposedOrRealizedchangeActionList && !proposedOrRealizedchangeActionList.isEmpty())
					{
						proposedChangeItr = proposedOrRealizedchangeActionList.iterator();
						while(proposedChangeItr.hasNext()){
							caMap = (Map)proposedChangeItr.next();	
							if(ChangeConstants.TYPE_CHANGE_ACTION.equals((String)caMap.get(DomainConstants.SELECT_TYPE))){	
								bChangeExist = true;
								break;
							}
						}
						if(!bChangeExist){
							strNewPartIdList.add(strPartId);
						}
					} else {
						strNewPartIdList.add(strPartId);
					}
				}

				//Create new CO and CA if it does not exist
				if(!strNewPartIdList.isEmpty()){
					StringList objectSelects = new StringList(1);
					objectSelects.add(DomainConstants.SELECT_ID);

					String strDefaultChangeTemplateName = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataStructureCopy.DefaultChangeTemplateName");
					String strDefaultChangeTemplateRev = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataStructureCopy.DefaultChangeTemplateRev");
					MapList mlChangeTemplate = DomainObject.findObjects(context, ChangeConstants.TYPE_CHANGETEMPLATE, strDefaultChangeTemplateName, strDefaultChangeTemplateRev, null, pgApolloConstants.VAULT_ESERVICE_PRODUCTION, null, true, objectSelects);
					Map mpChangeTemplate = (Map)mlChangeTemplate.get(0);
					String strChangeTemplateId=(String)mpChangeTemplate.get(DomainConstants.SELECT_ID);

					ContextUtil.startTransaction(context,true);
					isTransactionStarted = true;
					//Create new CO
					String strCOId  = (new ChangeOrder()).create(context, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, context.getUser(), strChangeTemplateId, null); 

					ChangeOrder changeOrderObj = new ChangeOrder(strCOId);				
					changeOrderObj.connectAffectedItems(context,  strNewPartIdList); 
					ContextUtil.commitTransaction(context);
					isTransactionStarted = false;
				}
			}
		}
		catch(Exception exp)
		{
			if(isTransactionStarted)
			{
				ContextUtil.abortTransaction(context);
				isTransactionStarted = false;
			}
			loggerApolloTrace.error(exp.getMessage() ,exp);
			loggerSync.error( " Exception in createChangeObjects----{}", exp.getLocalizedMessage());
			VPLMIntegTraceUtil.trace(context, "MassCollab: Exception in createChangeObjects >> " + exp.getLocalizedMessage());
			strReturnMessage = pgApolloConstants.STR_ERROR;
		}
		return strReturnMessage;
	}
	
	/**
	 * This method updates custom Hisory on an Object
	 * @param context
	 * @param strPartObjID
	 * @param strHistory
	 * @throws Exception
	 */
	public void addCustomHistoryOnSync(matrix.db.Context context,String strPartObjID, String strHistory) throws Exception{

		if(UIUtil.isNotNullAndNotEmpty(strPartObjID))
		{
			String strMQLstmtUpdateHistory = "modify bus $1 add history Sync comment '$2'";
			MqlUtil.mqlCommand(context, strMQLstmtUpdateHistory, strPartObjID, strHistory);	
		}		
	}
	
	/**
	 * This method returns String or StringList based on the Object Instance type.
	 * @param value
	 * @return
	 */
    private StringList getStringList(Object value) 
    {
        StringList stringList=new StringList();        
        if(value instanceof String)
        {
        	stringList.add((String)value);
        }
        else
        {
        	stringList=(StringList)value;
        }        
        return stringList;
    }

	//2018x.2.1 Method added to process Laminate Material - start
	
	
    /**
     * This method Process the Laminated Material 
     * Method modified for Query with UOM 
     * @param context
     * @param materialList
     * @param strAreaUOM
     * @param validUnitMap
     * @return
     * @throws Exception
     */
	public MapList processLaminationAndUpdateList(matrix.db.Context context, MapList materialList, Map catiaEnoviaUOMMap) throws Exception 
	{		
		try 
		{
			VPLMIntegTraceUtil.trace(context, "MassCollab: Inside processLaminationAndUpdateList >>> ");
			VPLMIntegTraceUtil.trace(context, "MassCollab: materialList: = " + materialList );			
			
			List<Map> materialListToProcess = new ArrayList<>();
			refactorMaterialListForLamination(materialList, materialListToProcess);						
			VPLMIntegTraceUtil.trace(context, "MassCollab:List of Laminate Materials To Process: = " + materialListToProcess );			
						
			Map mpRMPLaminateName = new HashMap();
			StringList slMaterialFunction;
			
			String strCurrentMapArea;
			String strCurrentMapGrossWeight;
			String strCurrentMapNetWeight;
			String strCurrentMapFindNumber;	
			String strCurrentMapMaterialFunction;

			String strCurrentMapQuantity;
			String strCurrentMapGrossArea;
			String strCurrentMapGrossVolume;
			String strCurrentMapGrossLength;
			String strCurrentMapNetQuantity;
			String strCurrentMapNetArea;
			String strCurrentMapNetVolume;
			String strCurrentMapNetLength;	

			String strCurrentMapQuantityUnit;
			String strCurrentMapNetQuantityUnit;

			String strEntryKey;	
			String strEntryValue;	

			String strMatName;
			String stPlyGrpName;
			String strLamiName;
			String strkeyComb;

			String sFinalMaterialFunction;
			StringBuilder sbUniqueKey;			
			
			String strQuantityUnit;
			String strNetQuantityUnit;
			StringBuilder sbParameter;

			BigDecimal bigDecimalArea;
			BigDecimal bigDecimalGrossWeight;
			BigDecimal bigDecimalNetWeight;
			BigDecimal bigDecimalCurrentArea;
			BigDecimal bigDecimalCurrentGrossWeight;
			BigDecimal bigDecimalCurrentNetWeight;

			BigDecimal bigDecimalQuantity;
			BigDecimal bigDecimalGrossArea;
			BigDecimal bigDecimalGrossVolume;
			BigDecimal bigDecimalGrossLength;

			BigDecimal bigDecimalCurrentQuantity;
			BigDecimal bigDecimalCurrentGrossArea;
			BigDecimal bigDecimalCurrentGrossVolume;
			BigDecimal bigDecimalCurrentGrossLength;

			BigDecimal bigDecimalNetQuantity;
			BigDecimal bigDecimalNetArea;
			BigDecimal bigDecimalNetVolume;
			BigDecimal bigDecimalNetLength;

			BigDecimal bigDecimalCurrentNetQuantity;
			BigDecimal bigDecimalCurrentNetArea;
			BigDecimal bigDecimalCurrentNetVolume;
			BigDecimal bigDecimalCurrentNetLength;

			
			int iFindNumber = 0;
			int iCurrentFindNumber = 0;			
					
			//Get UoM Preferences from CATIA
			String strAreaCATIAUnit = DomainConstants.EMPTY_STRING;
			String strNetWeightCATIAUnit = DomainConstants.EMPTY_STRING;
			String strGrossWeightCATIAUnit = DomainConstants.EMPTY_STRING;
			
			if(null!=catiaEnoviaUOMMap)
			{
				if(catiaEnoviaUOMMap.containsKey(pgApolloConstants.KEY_AREA))
				{
					strAreaCATIAUnit = (String) catiaEnoviaUOMMap.get(pgApolloConstants.KEY_AREA);
				}
				if(catiaEnoviaUOMMap.containsKey(pgApolloConstants.KEY_MASS))
				{
					strNetWeightCATIAUnit = (String) catiaEnoviaUOMMap.get(pgApolloConstants.KEY_MASS);
					strGrossWeightCATIAUnit = (String) catiaEnoviaUOMMap.get(pgApolloConstants.KEY_MASS);
				}
			}
			
			//Query with UOM Changes ends
			if(null != materialListToProcess && !materialListToProcess.isEmpty())
			{
				Iterator  materialListProcessItr = materialListToProcess.iterator();
				Map materialLaminationMap = null;
				Map mpExitingKeyVal = null;
				while(materialListProcessItr.hasNext())
				{				
					materialLaminationMap = new HashMap();
					materialLaminationMap = (Map)materialListProcessItr.next();					
					strMatName = (String)materialLaminationMap.get(pgApolloConstants.KEY_PLYMATERIAL); 
					stPlyGrpName = (String)materialLaminationMap.get(pgApolloConstants.ATTRIBUTE_PLY_GROUP_NAME);
					strLamiName = (String)materialLaminationMap.get(pgApolloConstants.KEY_LAMINATENAME);
					//Query with UOM Changes starts
					strCurrentMapArea = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, pgApolloConstants.STR_UOM_SQUARE_CENTIMETER, strAreaCATIAUnit);
					strCurrentMapGrossWeight = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL, pgApolloConstants.STR_UOM_GRAM, strGrossWeightCATIAUnit);
					strCurrentMapNetWeight = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.ATTRIBUTE_NET_WEIGHT, pgApolloConstants.STR_UOM_GRAM, strNetWeightCATIAUnit);					
					
					strCurrentMapGrossArea = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.KEY_GROSS_AREA, pgApolloConstants.STR_UOM_SQUARE_CENTIMETER, pgApolloConstants.STR_UOM_SQUARE_CENTIMETER);
					strCurrentMapGrossVolume = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.KEY_GROSS_VOLUME, pgApolloConstants.STR_UOM_LITER, pgApolloConstants.STR_UOM_LITER);
					strCurrentMapGrossLength = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.KEY_GROSS_LENGTH, pgApolloConstants.STR_UOM_METER, pgApolloConstants.STR_UOM_METER);
					
					//2018x.6 Change - Start
					strCurrentMapNetArea = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.KEY_NET_AREA, pgApolloConstants.STR_UOM_SQUARE_CENTIMETER, pgApolloConstants.STR_UOM_SQUARE_CENTIMETER);
					strCurrentMapNetVolume = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.KEY_NET_VOLUME, pgApolloConstants.STR_UOM_LITER, pgApolloConstants.STR_UOM_LITER);
					strCurrentMapNetLength = getConvertedValueBasedOnUoM (context, materialLaminationMap, pgApolloConstants.KEY_NET_LENGTH, pgApolloConstants.STR_UOM_METER, pgApolloConstants.STR_UOM_METER);
					//2018x.6 Change - End
					
					//Query with UOM Changes starts
					strkeyComb = stPlyGrpName +"|" + strMatName + "|" +strLamiName;  //Group,MatName,LaminateName use "|"
					strCurrentMapQuantity  = DomainConstants.EMPTY_STRING;
					
					//2018x.6 Change - Start
					strCurrentMapNetQuantity  = DomainConstants.EMPTY_STRING;
					//2018x.6 Change - End	
					
					if(materialLaminationMap.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))
					{
						strCurrentMapQuantity = (String)materialLaminationMap.get(DomainConstants.ATTRIBUTE_QUANTITY);						
					}
					//2018x.6 Changes - start
					if(materialLaminationMap.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY))
					{
						strCurrentMapNetQuantity = (String)materialLaminationMap.get(pgApolloConstants.ATTRIBUTE_NET_QUANTITY);						
					}
					//2018x.6 Changes - end
					if(!mpRMPLaminateName.containsKey(strkeyComb) ) 
					{
						sbUniqueKey = new StringBuilder();
						sbUniqueKey.append(stPlyGrpName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strLamiName);
						materialLaminationMap.put(pgApolloConstants.KEY_UNIQUEKEY, sbUniqueKey.toString());						
						materialLaminationMap.put(pgApolloConstants.ATTRIBUTE_PLY_NAME, strLamiName);						
						//Query with UOM Changes starts
						materialLaminationMap = getUpdatedNetQuantityParameters(strCurrentMapNetQuantity, strCurrentMapNetArea, strCurrentMapNetVolume, strCurrentMapNetLength, materialLaminationMap, null, null);
						materialLaminationMap = getUpdatedAreaQuantityParameters(strCurrentMapArea, strCurrentMapQuantity, materialLaminationMap, null, null);
						materialLaminationMap = getUpdatedGrossParameters(strCurrentMapGrossArea, strCurrentMapGrossVolume, strCurrentMapGrossLength, materialLaminationMap, null);
						materialLaminationMap = getUpdatedWithWeightParameters(strCurrentMapGrossWeight, strCurrentMapNetWeight,materialLaminationMap, null);
						//Query with UOM Changes ends
						mpRMPLaminateName.put(strkeyComb, materialLaminationMap);		
						VPLMIntegTraceUtil.trace(context, "MassCollab:mpRMPLaminateName: = " + mpRMPLaminateName );						
					}
					else 
					{	
					
						strQuantityUnit = DomainConstants.EMPTY_STRING; 
						strNetQuantityUnit = DomainConstants.EMPTY_STRING; 

						bigDecimalArea = BigDecimal.valueOf(0.0);
						bigDecimalGrossWeight = BigDecimal.valueOf(0.0);
						bigDecimalNetWeight = BigDecimal.valueOf(0.0);
						iFindNumber = 0;						
						
						bigDecimalQuantity = BigDecimal.valueOf(0.0);
						bigDecimalGrossArea = BigDecimal.valueOf(0.0);
						bigDecimalGrossVolume = BigDecimal.valueOf(0.0);
						bigDecimalGrossLength = BigDecimal.valueOf(0.0);					
						
						bigDecimalNetQuantity = BigDecimal.valueOf(0.0);
						bigDecimalNetArea = BigDecimal.valueOf(0.0);
						bigDecimalNetVolume = BigDecimal.valueOf(0.0);
						bigDecimalNetLength = BigDecimal.valueOf(0.0);					
						
						sFinalMaterialFunction = DomainConstants.EMPTY_STRING;
						slMaterialFunction = new StringList();					

						mpExitingKeyVal = (Map)mpRMPLaminateName.get(strkeyComb);
						Iterator iteratorEntry = mpExitingKeyVal.entrySet().iterator();
						while (iteratorEntry.hasNext()) 
						{					
							Map.Entry mapEntry = (Map.Entry) iteratorEntry.next();
							strEntryKey = (String)mapEntry.getKey();	
							strEntryValue = (String)mapEntry.getValue();								

							bigDecimalNetWeight = getBigDecimalParamValueFromMap (bigDecimalNetWeight, pgApolloConstants.ATTRIBUTE_NET_WEIGHT, strEntryValue, strEntryKey , true);
							
							bigDecimalGrossWeight = getBigDecimalParamValueFromMap (bigDecimalGrossWeight, pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL, strEntryValue, strEntryKey , true);
							
							bigDecimalArea = getBigDecimalParamValueFromMap (bigDecimalArea, pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, strEntryValue, strEntryKey , false);
							
							iFindNumber = getFindNumber(iFindNumber, strEntryValue, strEntryKey);
							
							slMaterialFunction = getApplication(slMaterialFunction, strEntryValue, strEntryKey);
							
							bigDecimalQuantity = getBigDecimalParamValueFromMap (bigDecimalQuantity, pgApolloConstants.ATTRIBUTE_QUANTITY, strEntryValue, strEntryKey , true);
							
							bigDecimalGrossArea = getBigDecimalParamValueFromMap (bigDecimalGrossArea, pgApolloConstants.KEY_GROSS_AREA, strEntryValue, strEntryKey , false);
														
							bigDecimalGrossVolume = getBigDecimalParamValueFromMap (bigDecimalGrossVolume, pgApolloConstants.KEY_GROSS_VOLUME, strEntryValue, strEntryKey , false);
							
							bigDecimalGrossLength = getBigDecimalParamValueFromMap (bigDecimalGrossLength, pgApolloConstants.KEY_GROSS_LENGTH, strEntryValue, strEntryKey , false);							
							
							bigDecimalNetQuantity = getBigDecimalParamValueFromMap (bigDecimalNetQuantity, pgApolloConstants.ATTRIBUTE_NET_QUANTITY, strEntryValue, strEntryKey , true);

							bigDecimalNetArea = getBigDecimalParamValueFromMap (bigDecimalNetArea, pgApolloConstants.KEY_NET_AREA, strEntryValue, strEntryKey , false);

							bigDecimalNetVolume = getBigDecimalParamValueFromMap (bigDecimalNetVolume, pgApolloConstants.KEY_NET_VOLUME, strEntryValue, strEntryKey , false);
							
							bigDecimalNetLength = getBigDecimalParamValueFromMap (bigDecimalNetLength, pgApolloConstants.KEY_NET_LENGTH, strEntryValue, strEntryKey , false);
							
							if(DomainConstants.ATTRIBUTE_QUANTITY.equals(strEntryKey)) {
								strQuantityUnit = getParameterUnit(strEntryValue);
							}		
						if(pgApolloConstants.ATTRIBUTE_NET_QUANTITY.equals(strEntryKey)) {
								strNetQuantityUnit = getParameterUnit(strEntryValue);
							}
						}					
					
						strCurrentMapQuantityUnit = getParameterUnit(strCurrentMapQuantity);
						strCurrentMapNetQuantityUnit= getParameterUnit(strCurrentMapNetQuantity);
					
						bigDecimalCurrentArea = validateAndGetNonZeroValue(strCurrentMapArea);
						
						strCurrentMapQuantity = getParameterValue(strCurrentMapQuantity);						
						bigDecimalCurrentQuantity = validateAndGetNonZeroValue(strCurrentMapQuantity);
						bigDecimalCurrentGrossArea = validateAndGetNonZeroValue(strCurrentMapGrossArea);
						bigDecimalCurrentGrossVolume = validateAndGetNonZeroValue(strCurrentMapGrossVolume);
						bigDecimalCurrentGrossLength = validateAndGetNonZeroValue(strCurrentMapGrossLength);
						
						strCurrentMapNetQuantity = getParameterValue(strCurrentMapNetQuantity);
						bigDecimalCurrentNetQuantity = validateAndGetNonZeroValue(strCurrentMapNetQuantity);
						bigDecimalCurrentNetArea = validateAndGetNonZeroValue(strCurrentMapNetArea);
						bigDecimalCurrentNetVolume = validateAndGetNonZeroValue(strCurrentMapNetVolume);
						bigDecimalCurrentNetLength = validateAndGetNonZeroValue(strCurrentMapNetLength);
						
						bigDecimalCurrentGrossWeight = validateAndGetNonZeroValue(strCurrentMapGrossWeight);
						bigDecimalCurrentNetWeight = validateAndGetNonZeroValue(strCurrentMapNetWeight);
						
						strCurrentMapFindNumber = (String)materialLaminationMap.get(DomainConstants.ATTRIBUTE_FIND_NUMBER);
						iCurrentFindNumber = Integer.parseInt(strCurrentMapFindNumber);					
						
						strCurrentMapMaterialFunction = (String)materialLaminationMap.get(pgApolloConstants.KEY_APPLICATION);
						if(!slMaterialFunction.contains(strCurrentMapMaterialFunction))
						{
							slMaterialFunction.addElement(strCurrentMapMaterialFunction);
						}
						
						bigDecimalCurrentArea = bigDecimalArea.max(bigDecimalCurrentArea);
						strCurrentMapArea = bigDecimalCurrentArea.toString();
						
						bigDecimalCurrentQuantity = bigDecimalQuantity.max(bigDecimalCurrentQuantity);
						strCurrentMapQuantity = bigDecimalCurrentQuantity.toString();
						
						bigDecimalCurrentGrossArea = bigDecimalGrossArea.max(bigDecimalCurrentGrossArea);
						strCurrentMapGrossArea = bigDecimalCurrentGrossArea.toString();
						
						bigDecimalCurrentGrossVolume = bigDecimalGrossVolume.max(bigDecimalCurrentGrossVolume);
						strCurrentMapGrossVolume = bigDecimalCurrentGrossVolume.toString();
						
						bigDecimalCurrentGrossLength = bigDecimalGrossLength.max(bigDecimalCurrentGrossLength);
						strCurrentMapGrossLength = bigDecimalCurrentGrossLength.toString();
						
						bigDecimalCurrentNetQuantity = bigDecimalNetQuantity.max(bigDecimalCurrentNetQuantity);
						strCurrentMapNetQuantity = bigDecimalCurrentNetQuantity.toString();

						bigDecimalCurrentNetArea = bigDecimalNetArea.max(bigDecimalCurrentNetArea);
						strCurrentMapNetArea = bigDecimalCurrentNetArea.toString();

						bigDecimalCurrentNetVolume = bigDecimalNetVolume.max(bigDecimalCurrentNetVolume);
						strCurrentMapNetVolume = bigDecimalCurrentNetVolume.toString();

						bigDecimalCurrentNetLength = bigDecimalNetLength.max(bigDecimalCurrentNetLength);
						strCurrentMapNetLength = bigDecimalCurrentNetLength.toString();
						
						bigDecimalCurrentGrossWeight = bigDecimalGrossWeight.add(bigDecimalCurrentGrossWeight);
						strCurrentMapGrossWeight = bigDecimalCurrentGrossWeight.toString();
						
						bigDecimalCurrentNetWeight = bigDecimalNetWeight.add(bigDecimalCurrentNetWeight);
						strCurrentMapNetWeight = bigDecimalCurrentNetWeight.toString(); 
						
						iCurrentFindNumber = Math.min(iFindNumber, iCurrentFindNumber);
						strCurrentMapFindNumber = Integer.toString(iCurrentFindNumber);
												
						sFinalMaterialFunction = getFinalMaterialFunction(slMaterialFunction, sFinalMaterialFunction);
						
						mpExitingKeyVal = getUpdatedWithWeightParameters(strCurrentMapGrossWeight, strCurrentMapNetWeight,mpExitingKeyVal, materialLaminationMap);						
						
						mpExitingKeyVal.put(DomainConstants.ATTRIBUTE_FIND_NUMBER, strCurrentMapFindNumber);						
						if(UIUtil.isNotNullAndNotEmpty(sFinalMaterialFunction)) 
						{
							mpExitingKeyVal.put(pgApolloConstants.KEY_APPLICATION, sFinalMaterialFunction);
						}
						if(UIUtil.isNotNullAndNotEmpty(strCurrentMapQuantityUnit) && UIUtil.isNotNullAndNotEmpty(strCurrentMapQuantity))
						{
							sbParameter = new StringBuilder();
							strCurrentMapQuantity = sbParameter.append(strCurrentMapQuantity).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCurrentMapQuantityUnit).toString();
						}
						if(UIUtil.isNotNullAndNotEmpty(strCurrentMapNetQuantityUnit) && UIUtil.isNotNullAndNotEmpty(strCurrentMapNetQuantity))
						{
							sbParameter = new StringBuilder();
							strCurrentMapNetQuantity = sbParameter.append(strCurrentMapNetQuantity).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCurrentMapNetQuantityUnit).toString();
						}

						mpExitingKeyVal = getUpdatedNetQuantityParameters(strCurrentMapNetQuantity, strCurrentMapNetArea, strCurrentMapNetVolume, strCurrentMapNetLength, mpExitingKeyVal, materialLaminationMap, strNetQuantityUnit);
						mpExitingKeyVal = getUpdatedAreaQuantityParameters(strCurrentMapArea, strCurrentMapQuantity, mpExitingKeyVal, materialLaminationMap, strQuantityUnit);
						mpExitingKeyVal = getUpdatedGrossParameters(strCurrentMapGrossArea, strCurrentMapGrossVolume, strCurrentMapGrossLength, mpExitingKeyVal, materialLaminationMap);

						mpRMPLaminateName.put(strkeyComb,mpExitingKeyVal);						
						
						VPLMIntegTraceUtil.trace(context, "MassCollab:mpRMPLaminateName = " + mpRMPLaminateName );		
					}				
				}
			}			
			
			finalizeMaterialListForLamination(context, materialList, mpRMPLaminateName);			
			VPLMIntegTraceUtil.trace(context, "MassCollab: materialList = " + materialList );
			
		}
		catch(Exception ex) 
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
			throw ex;
		}		
		return materialList;
	
	}

	
	/**
	 * Method to update Weight parameters in laminate map
	 * @param strCurrentMapGrossWeight
	 * @param strCurrentMapNetWeight
	 * @param materialLaminationMap 
	 * @param mpExitingKeyVal
	 * @return
	 */
	private Map getUpdatedWithWeightParameters(String strCurrentMapGrossWeight, String strCurrentMapNetWeight, Map mpExitingKeyVal, Map materialLaminationMap) {
		if(null == materialLaminationMap)
		{
			materialLaminationMap = mpExitingKeyVal;
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL) && UIUtil.isNotNullAndNotEmpty(strCurrentMapGrossWeight))
		{
			mpExitingKeyVal.put(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL, strCurrentMapGrossWeight + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_GRAM );			
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.ATTRIBUTE_NET_WEIGHT) && UIUtil.isNotNullAndNotEmpty(strCurrentMapNetWeight))
		{
			mpExitingKeyVal.put(pgApolloConstants.ATTRIBUTE_NET_WEIGHT, strCurrentMapNetWeight + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_GRAM );
		}		
		return mpExitingKeyVal;
	}


	/**
	 * Method to validate and get non zero value
	 * @param strParameterValue
	 * @return
	 */
	private static BigDecimal validateAndGetNonZeroValue(String strParameterValue) {
		BigDecimal bigDecimalParameter = BigDecimal.valueOf(0.0);
		if(UIUtil.isNotNullAndNotEmpty(strParameterValue))
		{
			bigDecimalParameter = new BigDecimal(strParameterValue);
		}
		return bigDecimalParameter;
	}
	

	/**
	 * Method to get MF for Lamination
	 * @param slMaterialFunction
	 * @param strEntryValue
	 * @param strEntryKey
	 * @return
	 */
	private StringList getApplication(StringList slMaterialFunction, String strEntryValue, String strEntryKey) {
		String sMaterialFunction;
		if(pgApolloConstants.KEY_APPLICATION.equals(strEntryKey)) {				
		sMaterialFunction = strEntryValue;
		slMaterialFunction = StringUtil.split(sMaterialFunction, ",");
		}
		return slMaterialFunction;
	}

	/**
	 * Method to get Find Number for Lamination
	 * @param iFindNumber
	 * @param strEntryValue
	 * @param strEntryKey
	 * @return
	 * @throws Exception
	 */
	private int getFindNumber(int iFindNumber, String strEntryValue, String strEntryKey) {
		String sFindNumber;
		if(DomainConstants.ATTRIBUTE_FIND_NUMBER.equals(strEntryKey)) {
		sFindNumber = strEntryValue;
		iFindNumber = Integer.parseInt(sFindNumber);					
		}
		return iFindNumber;
	}

	/**
	 * Method get parameter value during lamination processing
	 * @param bigDecimaleExistingValue
	 * @param strParam
	 * @param strEntryValue
	 * @param strEntryKey
	 * @param getParamValueWithoutUoM
	 * @return
	 * @throws Exception
	 */
	private BigDecimal getBigDecimalParamValueFromMap (BigDecimal bigDecimaleExistingValue, String strParam, String strEntryValue, String strEntryKey , boolean getParamValueWithoutUoM) throws Exception 
	{		
		if(strParam.equalsIgnoreCase(strEntryKey))  
		{
			if(getParamValueWithoutUoM)
			{
				strEntryValue = getParameterValue(strEntryValue);
			}
			bigDecimaleExistingValue = validateAndGetNonZeroValue(strEntryValue);
		}
		return bigDecimaleExistingValue;
	}
	
	/**
	 * Method to get Final Material Function
	 * @param slMaterialFunction
	 * @param sFinalMaterialFunction
	 * @return
	 * @throws Exception
	 */
	private String getFinalMaterialFunction(StringList slMaterialFunction, String sFinalMaterialFunction) {
		if(!slMaterialFunction.isEmpty())
		{
			sFinalMaterialFunction = StringUtil.join(slMaterialFunction, ",");
		}
		return sFinalMaterialFunction;
	}

	/**
	 * Method to finalize material list for lamination after processing
	 * @param context
	 * @param materialList
	 * @param mpRMPLaminateName
	 * @throws IOException
	 */
	private void finalizeMaterialListForLamination(matrix.db.Context context, MapList materialList,	Map mpRMPLaminateName) {
		
		if(null != mpRMPLaminateName && !mpRMPLaminateName.isEmpty())
		{
			Map mpFinalKeyVal;
			String strFindNumber;
			int iFindNumber;
			int iLaminateMapIndexInList;
			for (Object str : mpRMPLaminateName.keySet())
			{
				mpFinalKeyVal = (Map) mpRMPLaminateName.get(str);
				getUpdatedMapWithFinalizedUOM(mpFinalKeyVal);	
				strFindNumber =  (String)mpFinalKeyVal.get(DomainConstants.ATTRIBUTE_FIND_NUMBER);
				if(UIUtil.isNotNullAndNotEmpty(strFindNumber))
				{
					iFindNumber = Integer.parseInt(strFindNumber);
					if(iFindNumber > 0)
					{
						iLaminateMapIndexInList = iFindNumber - 1;
					}
					else
					{
						iLaminateMapIndexInList = 0;
					}
					materialList.add(iLaminateMapIndexInList, mpFinalKeyVal);
				}
				else
				{
					materialList.add(mpFinalKeyVal);
				}
			}
		}
	}

	/**
	 * Method to refine Material list for Laminate Processing
	 * @param materialList
	 * @param materialListToProcess
	 */
	private void refactorMaterialListForLamination(MapList materialList, List<Map> materialListToProcess) {
		String strisLaminate;
		Map materialMap;
		if(null != materialList && !materialList.isEmpty())
		{
			Iterator  materialListItr = materialList.iterator();
			while(materialListItr.hasNext())
			{				
				materialMap = (Map)materialListItr.next();
				if(materialMap.containsKey(pgApolloConstants.KEY_ISLAMINATE))
				{
					strisLaminate = (String)materialMap.get(pgApolloConstants.KEY_ISLAMINATE);
					if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strisLaminate)) 
					{						
						materialListToProcess.add((Map) materialMap);	
						materialListItr.remove();
					}
				}
			}
		}
	}
	
	/**
	 * Method to get updated Map based on UOM
	 * @param strBaseUOM
	 * @param mpFinalKeyVal
	 * @return
	 */
	private Map getUpdatedMapWithFinalizedUOM(Map mpFinalKeyVal) {
		String strCurrentMapArea;
		String strCurrentMapGrossArea;
		String strCurrentMapGrossVolume;
		String strCurrentMapGrossLength;
		String strCurrentMapNetArea;
		String strCurrentMapNetVolume;
		String strCurrentMapNetLength;
		if(mpFinalKeyVal.containsKey(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA))
		{				
			strCurrentMapArea = (String) mpFinalKeyVal.get(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA);
			mpFinalKeyVal.put(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, strCurrentMapArea + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_SQUARE_CENTIMETER);		
		}		
		if(mpFinalKeyVal.containsKey(pgApolloConstants.KEY_GROSS_AREA))
		{				
			strCurrentMapGrossArea = (String) mpFinalKeyVal.get(pgApolloConstants.KEY_GROSS_AREA);
			mpFinalKeyVal.put(pgApolloConstants.KEY_GROSS_AREA, strCurrentMapGrossArea + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_SQUARE_CENTIMETER);		
		}
		if(mpFinalKeyVal.containsKey(pgApolloConstants.KEY_GROSS_VOLUME))
		{				
			strCurrentMapGrossVolume = (String) mpFinalKeyVal.get(pgApolloConstants.KEY_GROSS_VOLUME);
			mpFinalKeyVal.put(pgApolloConstants.KEY_GROSS_VOLUME, strCurrentMapGrossVolume + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_LITER);		
		}
		if(mpFinalKeyVal.containsKey(pgApolloConstants.KEY_GROSS_LENGTH))
		{				
			strCurrentMapGrossLength = (String) mpFinalKeyVal.get(pgApolloConstants.KEY_GROSS_LENGTH);
			mpFinalKeyVal.put(pgApolloConstants.KEY_GROSS_LENGTH, strCurrentMapGrossLength + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_METER);		
		}
		if(mpFinalKeyVal.containsKey(pgApolloConstants.KEY_NET_AREA))
		{				
			strCurrentMapNetArea = (String) mpFinalKeyVal.get(pgApolloConstants.KEY_NET_AREA);
			mpFinalKeyVal.put(pgApolloConstants.KEY_NET_AREA, strCurrentMapNetArea + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_SQUARE_CENTIMETER);		
		}
		if(mpFinalKeyVal.containsKey(pgApolloConstants.KEY_NET_VOLUME))
		{				
			strCurrentMapNetVolume = (String) mpFinalKeyVal.get(pgApolloConstants.KEY_NET_VOLUME);
			mpFinalKeyVal.put(pgApolloConstants.KEY_NET_VOLUME, strCurrentMapNetVolume + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_LITER);		
		}
		if(mpFinalKeyVal.containsKey(pgApolloConstants.KEY_NET_LENGTH))
		{				
			strCurrentMapNetLength = (String) mpFinalKeyVal.get(pgApolloConstants.KEY_NET_LENGTH);
			mpFinalKeyVal.put(pgApolloConstants.KEY_NET_LENGTH, strCurrentMapNetLength + pgApolloConstants.CONSTANT_STRING_PIPE + pgApolloConstants.STR_UOM_METER);		
		}
		return mpFinalKeyVal;
	}

	/**
	 * Method to get Map with updated quantity parameters
	 * @param strCurrentMapArea
	 * @param strCurrentMapQuantity
	 * @param strCurrentMapGrossArea
	 * @param strCurrentMapGrossVolume
	 * @param strCurrentMapGrossLength
	 * @param materialLaminationMap
	 * @param strPreviousQuantityUnit 
	 * @return
	 * @throws Exception
	 */
	private Map getUpdatedAreaQuantityParameters(String strCurrentMapArea, String strCurrentMapQuantity, Map mpExitingKeyVal, Map materialLaminationMap, String strPreviousQuantityUnit) throws Exception {
		String strQtyUnit;
		String strUniqueKey;
		if(null == materialLaminationMap)
		{
			materialLaminationMap = mpExitingKeyVal;
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA) && UIUtil.isNotNullAndNotEmpty(strCurrentMapArea))
		{
			mpExitingKeyVal.put(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA, strCurrentMapArea );
		}
		if(materialLaminationMap.containsKey(DomainConstants.ATTRIBUTE_QUANTITY) && UIUtil.isNotNullAndNotEmpty(strCurrentMapQuantity))
		{
			if(mpExitingKeyVal.containsKey(DomainConstants.ATTRIBUTE_QUANTITY))
			{
				strQtyUnit = getParameterUnit(strCurrentMapQuantity);	
				if(null == strPreviousQuantityUnit)
				{
					strPreviousQuantityUnit = strQtyUnit;
				}
				if(strQtyUnit.equalsIgnoreCase(strPreviousQuantityUnit))
				{
					mpExitingKeyVal.put(pgApolloConstants.ATTRIBUTE_QUANTITY, strCurrentMapQuantity );			
				}
				else
				{
					loggerSync.debug("Current Quantity Value with Unit : {} , Previous Quantity Unit : {} ", strCurrentMapQuantity, strPreviousQuantityUnit);
					strUniqueKey = (String)mpExitingKeyVal.get(pgApolloConstants.KEY_UNIQUEKEY);
					throw new MatrixException(pgApolloConstants.STR_ERROR_LAMINATE_MISMATCH_UNITS.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", DomainConstants.ATTRIBUTE_QUANTITY));
				}		
			}
			else
			{
				mpExitingKeyVal.put(pgApolloConstants.ATTRIBUTE_QUANTITY, strCurrentMapQuantity );			
			}
				
		}		
		return mpExitingKeyVal;
	}
	
	
	/**
	 * Method to update Gross Parameters
	 * @param strCurrentMapGrossArea
	 * @param strCurrentMapGrossVolume
	 * @param strCurrentMapGrossLength
	 * @param mpExitingKeyVal
	 * @param materialLaminationMap
	 * @return
	 * @throws Exception
	 */
	private Map getUpdatedGrossParameters(String strCurrentMapGrossArea, String strCurrentMapGrossVolume, String strCurrentMapGrossLength, Map mpExitingKeyVal, Map materialLaminationMap)  {
		if(null == materialLaminationMap)
		{
			materialLaminationMap = mpExitingKeyVal;
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.KEY_GROSS_AREA) && UIUtil.isNotNullAndNotEmpty(strCurrentMapGrossArea))
		{
			mpExitingKeyVal.put(pgApolloConstants.KEY_GROSS_AREA, strCurrentMapGrossArea );
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.KEY_GROSS_VOLUME) && UIUtil.isNotNullAndNotEmpty(strCurrentMapGrossVolume))
		{
			mpExitingKeyVal.put(pgApolloConstants.KEY_GROSS_VOLUME, strCurrentMapGrossVolume );
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.KEY_GROSS_LENGTH) && UIUtil.isNotNullAndNotEmpty(strCurrentMapGrossLength))
		{
			mpExitingKeyVal.put(pgApolloConstants.KEY_GROSS_LENGTH, strCurrentMapGrossLength );
		}
		return mpExitingKeyVal;
	}
	
	/**
	 * Method to update Net Qty Parameters
	 * @param strCurrentMapNetQuantity
	 * @param strCurrentMapNetArea
	 * @param strCurrentMapNetVolume
	 * @param strCurrentMapNetLength
	 * @param mpExitingKeyVal
	 * @param materialLaminationMap
	 * @param strNetQuantityUnit 
	 * @return
	 * @throws Exception
	 */
	private Map getUpdatedNetQuantityParameters(String strCurrentMapNetQuantity, String strCurrentMapNetArea, String strCurrentMapNetVolume, String strCurrentMapNetLength, Map mpExitingKeyVal, Map materialLaminationMap, String strPreviousNetQuantityUnit) throws Exception {
		String strQtyUnit;
		String strUniqueKey;

		if(null == materialLaminationMap)
		{
			materialLaminationMap = mpExitingKeyVal;
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY) && UIUtil.isNotNullAndNotEmpty(strCurrentMapNetQuantity))
		{
			if(mpExitingKeyVal.containsKey(pgApolloConstants.ATTRIBUTE_NET_QUANTITY))
			{
				strQtyUnit = getParameterUnit(strCurrentMapNetQuantity);
				if(null == strPreviousNetQuantityUnit)
				{
					strPreviousNetQuantityUnit = strQtyUnit;
				}
				if(strQtyUnit.equalsIgnoreCase(strPreviousNetQuantityUnit))
				{
					mpExitingKeyVal.put(pgApolloConstants.ATTRIBUTE_NET_QUANTITY, strCurrentMapNetQuantity );			
				}
				else
				{
					loggerSync.debug("Current Net Qty Value with Unit : {} , Previous Net Qty Unit : {} ", strCurrentMapNetQuantity, strPreviousNetQuantityUnit);
					strUniqueKey = (String)mpExitingKeyVal.get(pgApolloConstants.KEY_UNIQUEKEY);
					throw new MatrixException(pgApolloConstants.STR_ERROR_LAMINATE_MISMATCH_UNITS.replaceFirst("<LAYERNAME>", strUniqueKey).replace("<PARAMETER>", pgApolloConstants.STR_NET_QUANTITY));
				}
			}
			else
			{
				mpExitingKeyVal.put(pgApolloConstants.ATTRIBUTE_NET_QUANTITY, strCurrentMapNetQuantity );			
			}
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.KEY_NET_AREA) && UIUtil.isNotNullAndNotEmpty(strCurrentMapNetArea))
		{
			mpExitingKeyVal.put(pgApolloConstants.KEY_NET_AREA, strCurrentMapNetArea );
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.KEY_NET_VOLUME) && UIUtil.isNotNullAndNotEmpty(strCurrentMapNetVolume))
		{
			mpExitingKeyVal.put(pgApolloConstants.KEY_NET_VOLUME, strCurrentMapNetVolume );
		}
		if(materialLaminationMap.containsKey(pgApolloConstants.KEY_NET_LENGTH) && UIUtil.isNotNullAndNotEmpty(strCurrentMapNetLength))
		{
			mpExitingKeyVal.put(pgApolloConstants.KEY_NET_LENGTH, strCurrentMapNetLength );
		}
		return mpExitingKeyVal;
	}

	//2018x.2.1 Method added to process Laminate Material - End

	/**
	 * Method to get converted value based on UOM
	 * @param context
	 * @param mpMaterialMap
	 * @param strKey
	 * @param strFinalUoM
	 * @param strCATIAPreferenceUoM
	 * @return
	 * @throws Exception
	 */
	private String getConvertedValueBasedOnUoM (matrix.db.Context context, Map mpMaterialMap, String strKey, String strFinalUoM, String strCATIAPreferenceUoM) throws Exception
	{
		String strValue = DomainConstants.EMPTY_STRING;
		if(mpMaterialMap.containsKey(strKey))
		{
			String strValueWithUoM = (String)mpMaterialMap.get(strKey);
			if(UIUtil.isNotNullAndNotEmpty(strValueWithUoM))
			{
				strValue = getParameterValue(strValueWithUoM);			
				String strUoM = getParameterUnit(strValueWithUoM);			
				if(UIUtil.isNotNullAndNotEmpty(strUoM))
				{
					strValue = pgApolloWeightConversionUtility.convertValues(context , strValue , strUoM.toUpperCase(), strFinalUoM);
				}
				else
				{
					strValue = pgApolloWeightConversionUtility.convertValues(context , strValue , strCATIAPreferenceUoM.toUpperCase(), strFinalUoM);
				}				
			}		
		}
		return strValue;		
	}	

	/**
	 * This webservice will be called to create single CA for multiple physical products
	 * @param req
	 * @return
	 * @throws Exception
	 */    
	@POST
	@Path("/changeactioncreation")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	/**
	 * This webservice is called from Sync functionality to create single CA for multiple physical products
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public Response createCA(@Context HttpServletRequest req) throws Exception {
		
		String strDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		matrix.db.Context context = null;
		String line = DomainConstants.EMPTY_STRING;
		
		//Get the user context
		if (Framework.isLoggedIn(req)) {
			context = Framework.getContext(req.getSession(false));
		}
		
		loggerSync.debug( " =======================================================================" );
		loggerSync.debug(" NEW COLLABORATION STARTS WITH CA CREATION ");
		loggerSync.debug("Path : /ebom/changeactioncreation");
		loggerSync.debug("Method: GenerateEBOMService : createCA");
		
		String strReturnMsg = DomainConstants.EMPTY_STRING;		
		String strPartObjID = DomainConstants.EMPTY_STRING;
		StringList slURLParam = new StringList(); 		
		String strVPMRefName = DomainConstants.EMPTY_STRING;		
		String strVPMRefRev = DomainConstants.EMPTY_STRING;		
		StringList objectSelects = new StringList(3);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_CURRENT);
		objectSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		
		StringList slPartList = new StringList();
		StringList slParamList = new StringList();
		String strReleasePhase = DomainConstants.EMPTY_STRING;
		int iURLParamSize = 0;
		String strCreateCAForDevAndPilot = DomainConstants.EMPTY_STRING;
		StringList slCreateCAForDevAndPilot = new StringList();
		String strFlagCreateCAForDevAndPilot = DomainConstants.EMPTY_STRING;
		boolean bCreateCAForDevAndPilot = false;

		StringBuilder sbReturnMessage = new StringBuilder();

		try
		{
			StringBuilder sbCatiaDetails = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(req.getInputStream()));		

			while((line = in.readLine())!=null) {
				sbCatiaDetails.append(line);
			}
			String strCatiaDetails = sbCatiaDetails.toString();
			loggerSync.debug( "strCatiaDetails:: {}" , strCatiaDetails);

			if (UIUtil.isNotNullAndNotEmpty(strCatiaDetails))
			{
				slURLParam = StringUtil.split(strCatiaDetails, pgApolloConstants.CONSTANT_STRING_AMPERSAND);			
				if(!slURLParam.isEmpty()) 
				{			
					iURLParamSize = slURLParam.size();
					if(iURLParamSize > 1)
					{
						strCreateCAForDevAndPilot = slURLParam.get(0);
						slCreateCAForDevAndPilot = StringUtil.split(strCreateCAForDevAndPilot, pgApolloConstants.CONSTANT_STRING_EQUAL_SIGN);
						if(!slCreateCAForDevAndPilot.isEmpty() && slCreateCAForDevAndPilot.size()>1)
						{
							strFlagCreateCAForDevAndPilot = slCreateCAForDevAndPilot.get(1);
						}
						if(UIUtil.isNotNullAndNotEmpty(strFlagCreateCAForDevAndPilot) && (pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strFlagCreateCAForDevAndPilot) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strFlagCreateCAForDevAndPilot)))
						{
							bCreateCAForDevAndPilot = true;
						}
						String strVPMRefObjectId = DomainConstants.EMPTY_STRING;
						DomainObject doVPMRef = null;
						for(int i=1; i< iURLParamSize ; i++) 
						{
							slParamList = StringUtil.split(slURLParam.get(i), pgApolloConstants.CONSTANT_STRING_PIPE);
							loggerSync.debug( "slURLParam  : {}" , slParamList);

							if (!slParamList.isEmpty()) {

								strVPMRefName = slParamList.get(0);
								strVPMRefName = strVPMRefName.trim();
								loggerSync.debug( "strVPMRefName : {}" , strVPMRefName);
								strVPMRefRev = slParamList.get(1);
								strVPMRefRev = strVPMRefRev.trim();
								loggerSync.debug( "strVPMRefRevision : {}" , strVPMRefRev);
								strVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strVPMRefName, strVPMRefRev);
								if(UIUtil.isNotNullAndNotEmpty(strVPMRefObjectId))
								{
									doVPMRef = DomainObject.newInstance(context, strVPMRefObjectId);
									MapList mlRelatedAPP = doVPMRef.getRelatedObjects(context,
											DomainConstants.RELATIONSHIP_PART_SPECIFICATION,
											pgApolloConstants.TYPE_PGASSEMBLEDPRODUCTPART,
											objectSelects,//Object Select
											null,//rel Select
											true,//get To
											false,//get From
											(short)1,//recurse level
											null,//where Clause
											null,
											0);	
									if(null != mlRelatedAPP && !mlRelatedAPP.isEmpty()) 
									{
										Map mpRelatedAPP = (Map)mlRelatedAPP.get(0);
										strReleasePhase = (String)mpRelatedAPP.get(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
										if(UIUtil.isNotNullAndNotEmpty(strReleasePhase) && (pgApolloConstants.STR_MATURITYSTATUS_PRODUCTION.equalsIgnoreCase(strReleasePhase) || (bCreateCAForDevAndPilot && (pgApolloConstants.STR_MATURITYSTATUS_PILOT.equalsIgnoreCase(strReleasePhase) || pgApolloConstants.STR_MATURITYSTATUS_DEVELOPMENT.equalsIgnoreCase(strReleasePhase)))))
										{
											strPartObjID = (String)mpRelatedAPP.get(DomainConstants.SELECT_ID);
											slPartList.add(strPartObjID);																				
										}
									}
								}																									
							}	
						}				
					}
					strReturnMsg = createChangeObjects(context,slPartList);
					loggerSync.debug( "strReturnMsg After Creating CA/CO  : {}" , strReturnMsg);
					if(strReturnMsg.contains(pgApolloConstants.STR_ERROR))
					{
						sbReturnMessage.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON);
						sbReturnMessage.append(pgApolloConstants.STR_ERROR_CHANGEOBJECTERROR);
					}
					else
					{
						sbReturnMessage.append(strReturnMsg);
					}
				}
			}
			loggerSync.debug( "Final Response -->{}", sbReturnMessage);
			String strEndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerSync.debug( "****CA Creation COMPLETED**** {}" , strEndTime );
		} 
		catch (Exception e) 
		{
			loggerApolloTrace.error(e.getMessage() ,e);
			loggerSync.error( " Error -->{}", e.getLocalizedMessage());
			sbReturnMessage.append(pgApolloConstants.STR_ERROR + pgApolloConstants.CONSTANT_STRING_COLON+ e.getLocalizedMessage());
			return Response.serverError().entity(sbReturnMessage.toString()).build();
		}
		return Response.status(200).entity(sbReturnMessage.toString()).build();
	}
	
	/**
	 * This method matches 2 string values and returns true if the values are same. 
	 * @param strVal1
	 * @param strVal2
	 * @return
	 * @throws Exception
	 */
	private boolean isMatch(String strVal1, String strVal2) throws Exception
	{
		boolean isMatch = false;		
		if((UIUtil.isNullOrEmpty(strVal1) && UIUtil.isNullOrEmpty(strVal2)) || (strVal1.equals(strVal2)))
		{
			isMatch = true;
		}
		else if(UIUtil.isNotNullAndNotEmpty(strVal1) && UIUtil.isNotNullAndNotEmpty(strVal2))
		{
				if((Double.valueOf(strVal1)).equals(Double.valueOf(strVal2)))
				{
					isMatch = true;
				}
		}			
		return isMatch;		
	}
	
	/**
	 * APOLLO 2018x.5 A10-499 - Sync Changes for Query in XML Format Changes
	 * Method to read and refine query string and convert it Map form
	 * @param context
	 * @param strQueryString
	 * @return
	 * @throws Exception
	 */
    public MapList readAndRefineQueryString(matrix.db.Context context, String strQueryString) throws Exception{		
		MapList mlOutput = new MapList();
		String strError = DomainConstants.EMPTY_STRING;		
		try
		{			
			Map returnMap = new HashMap();
			SAXBuilder saxBuilder = new SAXBuilder();
			if(UIUtil.isNotNullAndNotEmpty(strQueryString))
			{	
				strQueryString = strQueryString.replaceAll("(?i)"+pgApolloConstants.STR_AUTOMATION_UNSET, pgApolloConstants.STR_AUTOMATION_BLANK);										
				if(strQueryString.contains("%20"))
				{
					strQueryString = strQueryString.replaceAll("%20", pgApolloConstants.CONSTANT_STRING_SPACE);										
				}
			}
			Document bmXMLDoc = (Document) saxBuilder.build(new StringReader(strQueryString));
			Element rootElement = bmXMLDoc.getRootElement();			
			List UOMList = (List)rootElement.getChildren(pgApolloConstants.KEY_UNITOFMEASURE);
			List prodList =(List)rootElement.getChildren(pgApolloConstants.KEY_PRODUCT);
			Element prodElement = null;
			StringList slUniquePlyGroup = new StringList();
			Set<String> setUniquePlyGroup;
			StringList slPlyGroup;
			MapList mapListStacking;
			MapList mapListApplicator;
			MapList mapListPerfChar;
			Map mapStacking;
			Map mapPerfChar;
			Map mapDesignParam;
			Map productDetailsMap;
			Map mpCatiaEnoviaUOMMap;
			String strPublishDesignParams;
			String strAddToBookmark;
			String strBookmarkId;
			//Fix for Report Type Issue and Business object does not found issue Starts
			StringList slAllErrors;
			StringList slError;
			//Fix for Report Type Issue and Business object does not found issue Ends
			boolean bPublishDesignParams = true;

			if(null!=prodList && !prodList.isEmpty())
			{
				for(int i=0;i<prodList.size();i++)
				{
					returnMap = new HashMap();
					slPlyGroup = new StringList();
					mapListStacking = new MapList();
					mapListPerfChar = new MapList();
					mapStacking = new HashMap();
					mapPerfChar = new HashMap();
					mapDesignParam = new HashMap();
					productDetailsMap = new HashMap();
					mpCatiaEnoviaUOMMap = new HashMap();
					setUniquePlyGroup = new LinkedHashSet<>();
					
					strPublishDesignParams = DomainConstants.EMPTY_STRING;
					strAddToBookmark = DomainConstants.EMPTY_STRING;
					strBookmarkId = DomainConstants.EMPTY_STRING;
					bPublishDesignParams = true;
					
					//Fix for Report Type Issue and Business object does not found issue Starts
					slAllErrors =  new StringList();
					//Fix for Report Type Issue and Business object does not found issue Ends
					mpCatiaEnoviaUOMMap = convertElementToMap((Element)UOMList.get(0));
					returnMap.put(pgApolloConstants.KEY_UOM, mpCatiaEnoviaUOMMap);
					
					prodElement = (Element)prodList.get(i);
					
					returnMap = updateApplicatorElementData(prodElement, returnMap);
					
					productDetailsMap = convertElementToMap(prodElement);										
					returnMap.putAll(productDetailsMap);
					
					mapListStacking = readChildrenElements(prodElement, pgApolloConstants.KEY_STACKING);						
					mapStacking = refineEBOMElements(context,mapListStacking, mpCatiaEnoviaUOMMap);
					
					VPLMIntegTraceUtil.trace(context, "MassCollab: After EBOM Refinement = " + mapStacking );
					
					slPlyGroup = (StringList)mapStacking.get(pgApolloConstants.KEY_EBOMPLYGROUPLIST);
					slUniquePlyGroup.addAll(slPlyGroup);
					returnMap.putAll(mapStacking);	
					
					//Fix for Report Type Issue and Business object does not found issue Starts
					slError =  new StringList();
					if(mapStacking.containsKey(pgApolloConstants.STR_ERROR))
					{
						slError = (StringList)mapStacking.get(pgApolloConstants.STR_ERROR);
						slAllErrors.addAll(slError);
					}
					//Fix for Report Type Issue and Business object does not found issue Ends
					
					mapListPerfChar = readChildrenElements(prodElement, pgApolloConstants.KEY_PERFCHAR);
					mapPerfChar = refinePerformanceCharacteristics(context,mapListPerfChar);
					
					VPLMIntegTraceUtil.trace(context, "MassCollab: After Characteristic Refinement = " + mapPerfChar );
					
					slPlyGroup = (StringList)mapPerfChar.get(pgApolloConstants.KEY_CHARPLYGROUPLIST);
					slUniquePlyGroup.addAll(slPlyGroup);
					setUniquePlyGroup.addAll(slUniquePlyGroup);
					slUniquePlyGroup.clear();
					slUniquePlyGroup.addAll(setUniquePlyGroup);

					//Fix for Report Type Issue and Business object does not found issue Starts
					slError =  new StringList();
					if(mapPerfChar.containsKey(pgApolloConstants.STR_ERROR))
					{
						slError = (StringList)mapPerfChar.get(pgApolloConstants.STR_ERROR);
						slAllErrors.addAll(slError);
					}
					//Fix for Report Type Issue and Business object does not found issue Ends					
					returnMap.putAll(mapPerfChar);					
					returnMap.put(pgApolloConstants.KEY_UNIQUEPLYGROUPLIST, slUniquePlyGroup);					
					
					if(productDetailsMap.containsKey(pgApolloConstants.KEY_PUBLISHDESIGNPARAM))
					{
						strPublishDesignParams = (String) productDetailsMap.get(pgApolloConstants.KEY_PUBLISHDESIGNPARAM);
					}
					if(UIUtil.isNotNullAndNotEmpty(strPublishDesignParams) && (pgApolloConstants.STR_NO_FLAG.equalsIgnoreCase(strPublishDesignParams) || pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strPublishDesignParams)))
					{
						bPublishDesignParams = false;
					}	
					if(productDetailsMap.containsKey(pgApolloConstants.KEY_ADDTOBOOKMARK))
					{
						strAddToBookmark = (String) productDetailsMap.get(pgApolloConstants.KEY_ADDTOBOOKMARK);
					}
					if(productDetailsMap.containsKey(pgApolloConstants.KEY_BOOKMARKID))
					{
						strBookmarkId = (String) productDetailsMap.get(pgApolloConstants.KEY_BOOKMARKID);
					}					
					if(bPublishDesignParams)
					{
						mapDesignParam = refineDesignParamElements(prodElement, pgApolloConstants.KEY_DESIGNPARAM, productDetailsMap);
						if(mapDesignParam.containsKey(pgApolloConstants.STR_ERROR))
						{
							slAllErrors.addElement((String)mapDesignParam.get(pgApolloConstants.STR_ERROR));
						}
						returnMap.putAll(mapDesignParam);
					}					
					if((pgApolloConstants.STR_YES_FLAG.equalsIgnoreCase(strAddToBookmark) || pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strAddToBookmark)) && UIUtil.isNullOrEmpty(strBookmarkId))
					{
						slAllErrors.addElement(pgApolloConstants.STR_ERROR_MISSING_BOOKMARK);						
					}
					//Fix for Report Type Issue and Business object does not found issue Starts
					if(null!= slAllErrors && !slAllErrors.isEmpty())
					{
						returnMap.put(pgApolloConstants.STR_ERROR, slAllErrors);
					}
					//Fix for Report Type Issue and Business object does not found issue Ends					
					mlOutput.add(returnMap);
				}
			}
		}
		catch (Exception ex) {
			loggerApolloTrace.error(ex.getMessage() ,ex);
			String strErrorMessage = ex.getMessage();
			if(null == strErrorMessage)
			{
				strErrorMessage = pgApolloConstants.STR_NULL_STRING;
			}
			StringBuilder sbError = new StringBuilder();
			sbError.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_READING_READREFINEQUERY.replaceFirst("<ERROR>", strErrorMessage));
			StringList slAllErrors = new StringList();
			if(!mlOutput.isEmpty())
			{		
				Iterator  mapListItr = mlOutput.iterator();
				Map mapReturn;
				while(mapListItr.hasNext())
				{				
					mapReturn = (Map)mapListItr.next();		
					if(mapReturn.containsKey(pgApolloConstants.STR_ERROR))
					{
						slAllErrors = (StringList)mapReturn.get(pgApolloConstants.STR_ERROR);
					}
					slAllErrors.add(0, sbError.toString());
					mapReturn.put(pgApolloConstants.STR_ERROR, slAllErrors);
				}				
			}
			else
			{				
				throw new MatrixException(sbError.toString());
			}
		}
		return mlOutput;
	}

    
    /**
     * Method to Update Applicator Data
     * @param prodElement
     * @param returnMap 
     * @return
     * @throws Exception 
     */
    private Map updateApplicatorElementData(Element prodElement, Map returnMap) throws Exception
    {
    	MapList mlApplicators = new MapList();
    	List applicatorList = (List)prodElement.getChildren(pgApolloConstants.KEY_APPLICATORS);
    	
    	if(null != applicatorList && !applicatorList.isEmpty())
    	{
    		Element applicatorElement = (Element)applicatorList.get(0);
    		mlApplicators  =  getChildElementMapList(applicatorElement, pgApolloConstants.KEY_APPLICATOR);		
    	}    	
		returnMap.put(pgApolloConstants.KEY_APPLICATORS, mlApplicators);
		return returnMap;
	}

	/**
     * Method to read Child MapList
     * @param mlChildList
     * @param elementKey
     * @return
     * @throws Exception 
     */
    private MapList getChildElementMapList(Element parentElement, String elementKey) throws Exception
    {
    	MapList mlChildMapList = new MapList();

    	MapList returnList =new MapList ();
    	try 
    	{
    		if(null != parentElement)
    		{
    			List listNodes = parentElement.getChildren(elementKey);
    			if(null != listNodes && !listNodes.isEmpty() )
    			{
    				Iterator childListItr = listNodes.iterator();
    				Element childElement;
    				Map childMap =null;
    				while(childListItr.hasNext()) {
    					childElement =(Element)childListItr.next();
    					childMap = convertElementToMap(childElement);
    					mlChildMapList.add(childMap);    							
    				}	

    			}
    		}
    	}
    	catch(Exception ex) 
    	{
    		loggerApolloTrace.error(ex.getMessage() ,ex);
    		throw ex;
    	}    	
    	return mlChildMapList;
    }

	/**
     * Method to fetch Design Parameter Map
     * @param prodElement
     * @param strNodeKey
     * @return
     * @throws Exception
     */
	private Map refineDesignParamElements(Element prodElement, String strNodeKey, Map productDetailsMap) throws Exception
	{
		Map mapDesignParam = new HashMap();
		Map mapFinalDesignParam = new HashMap();
		try 
		{
			String strVPMRefName;
			String strVPMRefRev = DomainConstants.EMPTY_STRING;
			strVPMRefName = (String)productDetailsMap.get(pgApolloConstants.KEY_NAME);
			if(productDetailsMap.containsKey(pgApolloConstants.KEY_REV))
			{
				strVPMRefRev = (String)productDetailsMap.get(pgApolloConstants.KEY_REV);
			}
			StringBuilder sbProductDefinition = new StringBuilder();
			sbProductDefinition.append(strVPMRefName);
			sbProductDefinition.append(pgApolloConstants.CONSTANT_STRING_COLON);
			sbProductDefinition.append(strVPMRefRev);
			String strProductDefinition = sbProductDefinition.toString();
			Map localMapDesignParam = new HashMap();
			StringList paramList;
			String strParamValue;
			mapDesignParam.put(pgApolloConstants.STR_PRODUCT_DETAILS, strProductDefinition);
			if(null != prodElement)
			{
				List listNodes = prodElement.getChildren(strNodeKey);				
				if(null != listNodes && !listNodes.isEmpty() )
				{
					Element nodeElement = (Element) listNodes.get(0);
					if(null!=nodeElement)
					{
						localMapDesignParam = convertElementToMap(nodeElement);
						if(!localMapDesignParam.isEmpty())
						{
							if(localMapDesignParam.containsKey(pgApolloConstants.KEY_STACKING))
							{
								strParamValue = (String)localMapDesignParam.get(pgApolloConstants.KEY_STACKING);
								strParamValue = handleSpecialCharacterForDesignParameter(strParamValue);
								paramList = StringUtil.splitString(strParamValue, pgApolloConstants.CONSTANT_STRING_THREE_HASH);
								localMapDesignParam.put(pgApolloConstants.STR_PRODUCT_DEFINITION, paramList);
								localMapDesignParam.remove(pgApolloConstants.KEY_STACKING);
							}
							if(localMapDesignParam.containsKey(pgApolloConstants.KEY_PERFCHAR))
							{
								strParamValue = (String)localMapDesignParam.get(pgApolloConstants.KEY_PERFCHAR);
								strParamValue = handleSpecialCharacterForDesignParameter(strParamValue);
								paramList = StringUtil.splitString(strParamValue, pgApolloConstants.CONSTANT_STRING_THREE_HASH);
								localMapDesignParam.put(pgApolloConstants.STR_PERFORMANCE_CHAR, paramList);
								localMapDesignParam.remove(pgApolloConstants.KEY_PERFCHAR);
							}
						}
					}
				}
			}
			if(!localMapDesignParam.isEmpty())
			{
				mapDesignParam.putAll(localMapDesignParam);
			}
			mapFinalDesignParam.put(strNodeKey, mapDesignParam);			
		}
		catch(Exception ex) 
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
			mapFinalDesignParam.put(pgApolloConstants.STR_ERROR, pgApolloConstants.STR_ERROR_READING_DESIGNPARAMS.replaceFirst("<ERROR>", ex.getMessage()));
		}
		return mapFinalDesignParam;
	}
	
	/**
	 * Method to handle special character for Design Param
	 * @param strParamValue
	 * @return
	 */
	private String handleSpecialCharacterForDesignParameter(String strParamValue) 
	{
		strParamValue = strParamValue.replace(pgApolloConstants.CONSTANT_STRING_TILDA, pgApolloConstants.STR_TILDA_CHAR);
		return strParamValue;
	}

	/**
	 * APOLLO 2018x.5 A10-499 - Sync Changes for Query in XML Format Changes
	 * Method to refine EBOM Map and return desired values
	 * @param context
	 * @param mapListStacking
	 * @return
	 * @throws Exception
	 */
	public Map refineEBOMElements(matrix.db.Context context, MapList mapListStacking, Map mpCatiaEnoviaUOMMap) throws Exception {
		Map outputMap = new HashMap();		
		try {
			Set setGroup = new HashSet();
			Map mapEBOMAttr = new HashMap();
			Map mapStackInfo = new HashMap();
			MapList mapList = new MapList();
			StringList slPlyGroup = new StringList();
			String strUniqueKey = DomainConstants.EMPTY_STRING;
			StringList slIgnoredParameters = new StringList();
			StringList slGroupLayer = new StringList();
			Iterator<String> itrStackingInfo = mapStackInfo.keySet().iterator();
			String strKey = DomainConstants.EMPTY_STRING;
			String strModifiedKey = DomainConstants.EMPTY_STRING;
			String strValue = DomainConstants.EMPTY_STRING;
			String strAttributeName = DomainConstants.EMPTY_STRING;
			String strPlyGroupName = DomainConstants.EMPTY_STRING;
			String strPlyLayerName = DomainConstants.EMPTY_STRING;
			String strRMPName = DomainConstants.EMPTY_STRING;
			StringBuffer uniqueKeyBuffer = new StringBuffer();
			String strIsActive = DomainConstants.EMPTY_STRING;
			StringList slUniqueRMP = new StringList();
			StringList slMandatoryParameter = new StringList();
			String strIsLaminate = DomainConstants.EMPTY_STRING;
			String strLaminateName = DomainConstants.EMPTY_STRING;
			String sIsApplicator = DomainConstants.EMPTY_STRING;
			String sApplicatorRMPName = DomainConstants.EMPTY_STRING;
			StringList slApplicatorRMPList = new StringList();
			Set setDuplicateApplicatorRMPList = new HashSet();
			String sWebWidth;
			String sWebWidthValue;
			//Fix for Report Type Issue and Business object does not found issue Starts
			StringList slError = new StringList();
			//Fix for Report Type Issue and Business object does not found issue Ends

			String strAttibuteMapping = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloServices.CollaborateWithAPP.EBOMAttributeMapping");
			VPLMIntegTraceUtil.trace(context, "MassCollab: EBOM Attribute Mapping = " + strAttibuteMapping );
			StringList slAttributeMapping = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strAttibuteMapping))
			{
				slAttributeMapping	= FrameworkUtil.split(strAttibuteMapping, pgApolloConstants.CONSTANT_STRING_PIPE);
			}
			String [] mappings = null;
			Map mapAttributeMapping = new HashMap();
			//Map prepared from reading page entry of catia-enovia attribute mapping
			for(String strMapping : slAttributeMapping) {
				mappings = strMapping.split(pgApolloConstants.CONSTANT_STRING_COLON);
				mapAttributeMapping.put(mappings[0], mappings[1]);
			}
			StringList slRelationshipAttributeList = getSchemaAttributes(context, "relationship", DomainConstants.RELATIONSHIP_EBOM);
			StringList slEBOMInterfaceAttributeList1 = getSchemaAttributes(context, "interface", pgApolloConstants.INTERFACE_LAYERPRODUCTINTERFACE);
			StringList slEBOMInterfaceAttributeList2 = getSchemaAttributes(context, "interface", pgApolloConstants.INTERFACE_DSMLAYERPRODUCTEBOMINTERFACE);
			
			StringList slFinalAttributeList = new StringList();
			slFinalAttributeList.addAll(slRelationshipAttributeList);
			slFinalAttributeList.addAll(slEBOMInterfaceAttributeList1);
			slFinalAttributeList.addAll(slEBOMInterfaceAttributeList2);

			if(null!=slFinalAttributeList && !slFinalAttributeList.isEmpty())
			{
				for(int i=0;i<slFinalAttributeList.size();i++)
				{
					strAttributeName = (String)slFinalAttributeList.get(i);
					if(!mapAttributeMapping.containsKey(strAttributeName)) {
						mapAttributeMapping.put(strAttributeName, strAttributeName);
					}
				}
			}						
			
			if(null!=mapListStacking && !mapListStacking.isEmpty())
			{
				for(int i=0;i<mapListStacking.size();i++)
				{
					mapStackInfo = (Map)mapListStacking.get(i);
					if(mapStackInfo!=null && !mapStackInfo.isEmpty())
					{
						strIsActive = DomainConstants.EMPTY_STRING;
						if(mapStackInfo.containsKey(pgApolloConstants.KEY_ISACTIVE))
						{
							strIsActive = (String)mapStackInfo.get(pgApolloConstants.KEY_ISACTIVE);
							if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strIsActive))
							{
								continue;
							}
						}						
						if(mapStackInfo.containsKey(pgApolloConstants.KEY_GROSS_AREA))
						{
							mapStackInfo.put(pgApolloConstants.KEY_AREA, (String)mapStackInfo.get(pgApolloConstants.KEY_GROSS_AREA));
						}						
						
						mapEBOMAttr = new HashMap();
						itrStackingInfo = mapStackInfo.keySet().iterator();
						while (itrStackingInfo.hasNext())
						{
							strKey = itrStackingInfo.next();
							strValue = (String)mapStackInfo.get(strKey);	
							if(mapAttributeMapping.containsKey(strKey)) {
								strModifiedKey = (String)mapAttributeMapping.get(strKey);
								mapEBOMAttr.put(strModifiedKey, strValue);
								if(!strModifiedKey.equalsIgnoreCase(strKey))
								{
									mapEBOMAttr.remove(strKey);
								}
							}
							else
							{		
								slIgnoredParameters = new StringList();
								// We are ignoring all attributes passed from CATIA if not present in Mapping properties or Correct Attribute
								if(!slIgnoredParameters.contains(strKey))
								{
									slIgnoredParameters.addElement(strKey);
								}
							}
						}
						if(mapEBOMAttr!=null && !mapEBOMAttr.isEmpty())
						{
							if(mapEBOMAttr.containsKey(pgApolloConstants.ATTRIBUTE_PLY_GROUP_NAME) && mapEBOMAttr.containsKey(pgApolloConstants.ATTRIBUTE_PLY_NAME) && mapEBOMAttr.containsKey(pgApolloConstants.KEY_PLYMATERIAL))
							{			
								strPlyGroupName = (String)mapEBOMAttr.get(pgApolloConstants.ATTRIBUTE_PLY_GROUP_NAME);
								strPlyLayerName = (String)mapEBOMAttr.get(pgApolloConstants.ATTRIBUTE_PLY_NAME);								
								mapList.add(mapEBOMAttr);
								setGroup.add(strPlyGroupName);								
								//if IsLaminate is true, then LaminateFunction and LaminateName should be non-empty		
								strIsLaminate = DomainConstants.EMPTY_STRING;
								if(mapEBOMAttr.containsKey(pgApolloConstants.KEY_ISLAMINATE))									
								{							
									strIsLaminate = (String)mapEBOMAttr.get(pgApolloConstants.KEY_ISLAMINATE);
									if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(strIsLaminate) && !mapEBOMAttr.containsKey(pgApolloConstants.KEY_LAMINATENAME))
									{
										uniqueKeyBuffer = new StringBuffer();
										uniqueKeyBuffer.append(strPlyGroupName);
										uniqueKeyBuffer.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strPlyLayerName);
										//Fix for Report Type Issue and Business object does not found issue Starts
										slError.add(pgApolloConstants.STR_ERROR_MISSING_LAMINATE_NAME.replaceFirst("<LAYERNAME>", uniqueKeyBuffer.toString()));
										//Fix for Report Type Issue and Business object does not found issue Ends
									}									
								}		
								if(mapEBOMAttr.containsKey(pgApolloConstants.KEY_ISAPPLICATOR))									
								{							
									sIsApplicator = (String)mapEBOMAttr.get(pgApolloConstants.KEY_ISAPPLICATOR);
									if(pgApolloConstants.STR_TRUE_FLAG.equalsIgnoreCase(sIsApplicator) && mapEBOMAttr.containsKey(pgApolloConstants.KEY_RMPAPPLICATOR))
									{
										sApplicatorRMPName = (String)mapEBOMAttr.get(pgApolloConstants.KEY_RMPAPPLICATOR);
										if(!slApplicatorRMPList.contains(sApplicatorRMPName))
										{
											slApplicatorRMPList.add(sApplicatorRMPName);
										}
										else
										{
											setDuplicateApplicatorRMPList.add(sApplicatorRMPName);
										}
									}									
								}		
							}
						}						
					}
				}
			}
			
			if(!setDuplicateApplicatorRMPList.isEmpty())
			{
				StringList slDuplicateApplicatorRMPList = new StringList();
				slDuplicateApplicatorRMPList.addAll(setDuplicateApplicatorRMPList);
				
				String sDuplicateApplicatorRMPs = slDuplicateApplicatorRMPList.join(pgApolloConstants.CONSTANT_STRING_COMMA);
				String sDuplicateApplicatorRMPErrorMessage = new StringBuilder(pgApolloConstants.STR_ERROR_DUPLICATE_APPLICATOR_RMPS).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(sDuplicateApplicatorRMPs).toString();
				
				slError.add(sDuplicateApplicatorRMPErrorMessage);
			}
			
			mapListStacking = new MapList();
			
			if(null != mapList && !mapList.isEmpty())
			{
				try
				{
					mapList  = processLaminationAndUpdateList(context, mapList, mpCatiaEnoviaUOMMap);
				}
				catch(Exception e)
				{
					loggerSync.error("Error in  processLaminationAndUpdateList : {}",e.getMessage());
					loggerApolloTrace.error(e.getMessage() ,e);
					String strErrorMessage = e.getMessage();
					if(null == strErrorMessage)
					{
						strErrorMessage =  pgApolloConstants.STR_NULL_STRING;
					}
					StringBuilder sbError = new StringBuilder();
					sbError.append(pgApolloConstants.STR_ERROR_LAMINATE_PROCESSING).append(strErrorMessage);
					slError.add(0, sbError.toString());
				}
				for(int x=0 ; x <mapList.size() ; x++)
				{
					mapStackInfo = (Map)mapList.get(x);					
					strPlyGroupName = (String)mapStackInfo.get(pgApolloConstants.ATTRIBUTE_PLY_GROUP_NAME);
					strPlyLayerName = (String)mapStackInfo.get(pgApolloConstants.ATTRIBUTE_PLY_NAME);
					uniqueKeyBuffer = new StringBuffer();
					uniqueKeyBuffer.append(strPlyGroupName);
					uniqueKeyBuffer.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strPlyLayerName);
					strUniqueKey = uniqueKeyBuffer.toString();
					mapStackInfo.put(pgApolloConstants.KEY_UNIQUEKEY, strUniqueKey);
					if(pgApolloCommonUtil.containsInListCaseInsensitive(strUniqueKey, slGroupLayer))
					{
						slError.add(pgApolloConstants.STR_ERROR_DUPLICATE_LAYERS.replaceFirst("<GROUP_LAYER>", strUniqueKey));
					}
					else
					{
						slGroupLayer.addElement(strUniqueKey);
					}
					if(!mapStackInfo.containsKey(pgApolloConstants.ATTRIBUTE_NET_WEIGHT))
					{
						//Fix for Report Type Issue and Business object does not found issue Starts
						slError.add(pgApolloConstants.STR_ERROR_MISSING_NET_WEIGHT.replaceFirst("<LAYERNAME>", strUniqueKey ));
						//Fix for Report Type Issue and Business object does not found issue Ends
					}
					if(!mapStackInfo.containsKey(pgApolloConstants.ATTRIBUTE_PG_GROSSWEIGHTREAL)) 
					{
						//Fix for Report Type Issue and Business object does not found issue Starts
						slError.add(pgApolloConstants.STR_ERROR_MISSING_GROSS_WEIGHT.replaceFirst("<LAYERNAME>", strUniqueKey));
						//Fix for Report Type Issue and Business object does not found issue Ends
					}
					if(mapStackInfo.containsKey(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH)) 
					{
						sWebWidth = (String)mapStackInfo.get(pgApolloConstants.ATTRIBUTE_PG_WEBWIDTH);		
						sWebWidthValue = getParameterValue(sWebWidth);
						if(pgApolloCommonUtil.isNullOrEmptyOrZero(sWebWidthValue) || !NumberUtils.isCreatable(sWebWidthValue))
						{
							slError.add(pgApolloConstants.STR_ERROR_MISSING_INVALID_WEB_WIDTH.replaceFirst("<LAYERNAME>", strUniqueKey));
						}
					}
					mapListStacking.add(mapStackInfo);
				}
			}			
			if(null!=mapList && !mapList.isEmpty())
			{
				outputMap.put(pgApolloConstants.KEY_STACKING, mapListStacking);				
			}
			else
			{
				outputMap.put(pgApolloConstants.KEY_STACKING, new MapList());	
			}
			if(null!=setGroup && !setGroup.isEmpty())
			{
				slPlyGroup.addAll(setGroup);
				outputMap.put(pgApolloConstants.KEY_EBOMPLYGROUPLIST, slPlyGroup);				
			}
			else
			{
				outputMap.put(pgApolloConstants.KEY_EBOMPLYGROUPLIST, new StringList());	
			}
			if(null!=slFinalAttributeList && !slFinalAttributeList.isEmpty())
			{
				outputMap.put(pgApolloConstants.KEY_EBOMATTRIBUTELIST, slFinalAttributeList);				
			}
			else
			{
				outputMap.put(pgApolloConstants.KEY_EBOMATTRIBUTELIST, new StringList());	
			}
			outputMap.put(pgApolloConstants.KEY_APPLICATOR_RMP, slApplicatorRMPList);	
			//Fix for Report Type Issue and Business object does not found issue Starts
			if(null!=slError && !slError.isEmpty())
			{
				outputMap.put(pgApolloConstants.STR_ERROR, slError);				
			}
			//Fix for Report Type Issue and Business object does not found issue Ends

		} catch (Exception e)
		{
			loggerApolloTrace.error(e.getMessage() ,e);
			String strErrorMessage = e.getMessage();
			if(null == strErrorMessage)
			{
				strErrorMessage =  pgApolloConstants.STR_NULL_STRING;
			}
			strErrorMessage = pgApolloConstants.STR_ERROR_REFINE_EBOM.replace("<ERROR>", strErrorMessage);
			StringBuilder sbError = new StringBuilder();
			sbError.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(strErrorMessage);
			StringList slAllErrors = new StringList();
			if(outputMap.containsKey(pgApolloConstants.STR_ERROR))
			{
				slAllErrors = (StringList)outputMap.get(pgApolloConstants.STR_ERROR);
			}
			slAllErrors.add(0, sbError.toString());
			outputMap.put(pgApolloConstants.STR_ERROR, slAllErrors);
		}
		return outputMap;
	}


	/**
	 * APOLLO 2018x.5 A10-499 - Sync Changes for Query in XML Format Changes
	 * Method to refine Characteristics Map and return desired values
	 * @param context
	 * @param mapListPerfChar
	 * @return
	 * @throws Exception
	 */
	public Map refinePerformanceCharacteristics(matrix.db.Context context, MapList mapListPerfChar) throws Exception {
		Map outputMap = new HashMap();		
		try {
			Set setGroup = new HashSet();
			Map mapCharInfo = new HashMap();
			Map mapPerformanceChar = new HashMap();
			MapList mapList = new MapList();
			StringList slPlyGroup = new StringList();
			String strUniqueKey = DomainConstants.EMPTY_STRING;
			String strPlyGroupCategorySpecific;
			StringList slIgnoredParameters = new StringList();
			Iterator<String> itrPerformanceChars = mapPerformanceChar.keySet().iterator();
			String strKey = DomainConstants.EMPTY_STRING;
			String strModifiedKey = DomainConstants.EMPTY_STRING;
			String strValue = DomainConstants.EMPTY_STRING;
			String strAttributeName = DomainConstants.EMPTY_STRING;
			String strPlyGroupName = DomainConstants.EMPTY_STRING;
			String strCategorySpecifics;
			String strCharacteristic = DomainConstants.EMPTY_STRING;
			String strCharacteristicSpecifics = DomainConstants.EMPTY_STRING;
			String strReportType = DomainConstants.EMPTY_STRING;
			String strIsActive = DomainConstants.EMPTY_STRING;
			boolean isActive = true;
			StringBuffer uniqueKeyBuffer = new StringBuffer();
			StringBuffer uniquePlyGroupCategorySpecificBuffer;
			Map uniquePlyGroupCategorySpecificMap = new HashMap();
			MapList mlPlyList = new MapList();
			//Fix for Report Type Issue and Business object does not found issue Starts
			StringList slError = new StringList();
			//Fix for Report Type Issue and Business object does not found issue Ends
			StringBuilder sbErrorString;
			String strErrorString;

			String strAttibuteMapping = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloServices.CollaborateWithAPP.CharacteristicsMapping");
			VPLMIntegTraceUtil.trace(context, "MassCollab: Characteristic Attribute Mapping = " + strAttibuteMapping );
			StringList slAttributeMapping = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strAttibuteMapping))
			{
				slAttributeMapping	= FrameworkUtil.split(strAttibuteMapping, pgApolloConstants.CONSTANT_STRING_PIPE);
			}
			String [] mappings = null;
			Map mapAttributeMapping = new HashMap();
			//Map prepared from reading page entry of catia-enovia attribute mapping
			for(String strMapping : slAttributeMapping) {
				mappings = strMapping.split(pgApolloConstants.CONSTANT_STRING_COLON);
				mapAttributeMapping.put(mappings[0], mappings[1]);
			}
			StringList slTypeAttributeList = getSchemaAttributes(context,"type",pgApolloConstants.TYPE_PLMPARAMETER);
			StringList slInterfaceAttributeList = getSchemaAttributes(context,"interface",pgApolloConstants.INTERFACE_CHARACTERISTICS);
			StringList slFinalAttributeList = new StringList();
			slFinalAttributeList.addAll(slTypeAttributeList);
			slFinalAttributeList.addAll(slInterfaceAttributeList);

			if(null!=slFinalAttributeList && !slFinalAttributeList.isEmpty())
			{
				for(int i=0;i<slFinalAttributeList.size();i++)
				{
					strAttributeName = (String)slFinalAttributeList.get(i);
					if(!mapAttributeMapping.containsKey(strAttributeName)) {
						mapAttributeMapping.put(strAttributeName, strAttributeName);
					}
				}
			}			

			if(null!=mapListPerfChar && !mapListPerfChar.isEmpty())
			{
				for(int i=0;i<mapListPerfChar.size();i++)
				{
					mapPerformanceChar = (Map)mapListPerfChar.get(i);
					if(mapPerformanceChar!=null && !mapPerformanceChar.isEmpty())
					{
						mapCharInfo = new HashMap();
						itrPerformanceChars = mapPerformanceChar.keySet().iterator();
						while (itrPerformanceChars.hasNext())
						{
							strKey = itrPerformanceChars.next();
							strValue = (String)mapPerformanceChar.get(strKey);
							if(mapAttributeMapping.containsKey(strKey)) {
								strModifiedKey = (String)mapAttributeMapping.get(strKey);
								mapCharInfo.put(strModifiedKey, strValue);
							}
							else
							{		
								slIgnoredParameters = new StringList();
								// We are ignoring all attributes passed from CATIA if not present in Mapping properties or Correct Attribute
								if(!slIgnoredParameters.contains(strKey))
								{
									slIgnoredParameters.addElement(strKey);
								}
							}
						}

						if(mapCharInfo!=null && !mapCharInfo.isEmpty())
						{
							strPlyGroupName = DomainConstants.EMPTY_STRING;

							strCharacteristic = DomainConstants.EMPTY_STRING;
							strCharacteristicSpecifics = DomainConstants.EMPTY_STRING;
							strReportType = DomainConstants.EMPTY_STRING;
							
							if(mapCharInfo.containsKey(pgApolloConstants.ATTRIBUTE_CHARACTERISTIC_CATEGORY) && 
									mapCharInfo.containsKey(pgApolloConstants.ATTRIBUTE_PG_CATEGORYSPECIFICS) && 
									mapCharInfo.containsKey(DomainConstants.ATTRIBUTE_TITLE)
									)
							{
								strIsActive = DomainConstants.EMPTY_STRING;
								isActive = true;
								if(mapCharInfo.containsKey(pgApolloConstants.KEY_ISACTIVE))
								{
									strIsActive = (String)mapCharInfo.get(pgApolloConstants.KEY_ISACTIVE);
									if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(strIsActive))
									{
										isActive = false;
									}
								}
								strPlyGroupName = (String)mapCharInfo.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTIC_CATEGORY);
								strCategorySpecifics = (String)mapCharInfo.get(pgApolloConstants.ATTRIBUTE_PG_CATEGORYSPECIFICS);
								strCharacteristic = (String)mapCharInfo.get(DomainConstants.ATTRIBUTE_TITLE);
								if(mapCharInfo.containsKey(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS)) {
									strCharacteristicSpecifics = (String)mapCharInfo.get(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS);
									if(pgApolloConstants.STR_BLANKSPECIFIC.equalsIgnoreCase(strCharacteristicSpecifics))
									{
										strCharacteristicSpecifics = DomainConstants.EMPTY_STRING;
										mapCharInfo.put(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS, DomainConstants.EMPTY_STRING);
									}
								}
								else
								{
									mapCharInfo.put(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS, DomainConstants.EMPTY_STRING);
								}
								uniqueKeyBuffer = new StringBuffer();
								uniqueKeyBuffer.append(strPlyGroupName);
								uniqueKeyBuffer.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCategorySpecifics);
								uniqueKeyBuffer.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCharacteristic);
								uniqueKeyBuffer.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCharacteristicSpecifics);
								strUniqueKey = uniqueKeyBuffer.toString();
								
								//If Report Type is Empty
								if(!mapCharInfo.containsKey(pgApolloConstants.ATTRIBUTE_REPORT_TYPE) || UIUtil.isNullOrEmpty((String)mapCharInfo.get(pgApolloConstants.ATTRIBUTE_REPORT_TYPE) )){
									sbErrorString = new StringBuilder(pgApolloConstants.STR_ERROR_MISSING_REPORT_TYPE.replaceFirst("<PERFCHAR>", strUniqueKey));
									strErrorString = sbErrorString.toString();
									//Fix for Report Type Issue and Business object does not found issue Starts
									slError.add(strErrorString);
									//Fix for Report Type Issue and Business object does not found issue Ends
									mapCharInfo = updateMapWithError(mapCharInfo, strErrorString);	
								}
								else
								{
									strReportType = (String)mapCharInfo.get(pgApolloConstants.ATTRIBUTE_REPORT_TYPE);
									uniqueKeyBuffer.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strReportType);
									strUniqueKey = uniqueKeyBuffer.toString();
								 }
								
								mapCharInfo.put(pgApolloConstants.KEY_UNIQUEKEY, strUniqueKey);
								if(isActive)
								{
									mapList.add(mapCharInfo);
									setGroup.add(strPlyGroupName);
								}								
								uniquePlyGroupCategorySpecificBuffer = new StringBuffer();
								uniquePlyGroupCategorySpecificBuffer.append(strPlyGroupName);
								uniquePlyGroupCategorySpecificBuffer.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCategorySpecifics);
								strPlyGroupCategorySpecific = uniquePlyGroupCategorySpecificBuffer.toString();
								if(uniquePlyGroupCategorySpecificMap.containsKey(strPlyGroupCategorySpecific))
								{
									mlPlyList = (MapList)uniquePlyGroupCategorySpecificMap.get(strPlyGroupCategorySpecific);
									mlPlyList.add(mapCharInfo);
									uniquePlyGroupCategorySpecificMap.put(strPlyGroupCategorySpecific, mlPlyList);
								}
								else
								{
									mlPlyList = new MapList();
									mlPlyList.add(mapCharInfo);
									uniquePlyGroupCategorySpecificMap.put(strPlyGroupCategorySpecific, mlPlyList);
								}
							}
						}						
					}
				}

				int localCounter = 0;
				Map mapPlyInfo = new HashMap();
				strIsActive = DomainConstants.EMPTY_STRING;
				if(null!=uniquePlyGroupCategorySpecificMap && !uniquePlyGroupCategorySpecificMap.isEmpty())
				{
					itrPerformanceChars = uniquePlyGroupCategorySpecificMap.keySet().iterator();
					while (itrPerformanceChars.hasNext())
					{
						mlPlyList = new MapList();
						strKey = itrPerformanceChars.next();
						mlPlyList = (MapList)uniquePlyGroupCategorySpecificMap.get(strKey);	
						if(null!=mlPlyList && !mlPlyList.isEmpty())
						{
							isActive = false;
							for(int i=0;i< mlPlyList.size();i++)
							{
								mapPlyInfo = (Map)mlPlyList.get(i);								
								if(null != mapPlyInfo && (!mapPlyInfo.containsKey(pgApolloConstants.KEY_ISACTIVE) || 
										(mapPlyInfo.containsKey(pgApolloConstants.KEY_ISACTIVE) && !pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase((String)mapPlyInfo.get(pgApolloConstants.KEY_ISACTIVE)))))
								{
									isActive = true;
									break;									
								}
							}
							
						}
					}					
				}
			}
			
			if(null!=mapList && !mapList.isEmpty())
			{
				outputMap.put(pgApolloConstants.KEY_PERFCHAR, mapList);				
			}
			else
			{
				outputMap.put(pgApolloConstants.KEY_PERFCHAR, new MapList());	
			}
			if(null!=setGroup && !setGroup.isEmpty())
			{
				slPlyGroup.addAll(setGroup);
				outputMap.put(pgApolloConstants.KEY_CHARPLYGROUPLIST, slPlyGroup);				
			}
			else
			{
				outputMap.put(pgApolloConstants.KEY_CHARPLYGROUPLIST, new StringList());	
			}			
			if(null!=mapAttributeMapping && !mapAttributeMapping.isEmpty())
			{
				outputMap.put(pgApolloConstants.KEY_CHARATTRIBUTEMAPPING, mapAttributeMapping);				
			}
			else
			{
				outputMap.put(pgApolloConstants.KEY_CHARATTRIBUTEMAPPING, new HashMap());	
			}
			
			outputMap = validateLimitRangeValuesForCharacteristics(context, outputMap);
			if(outputMap.containsKey(pgApolloConstants.STR_ERROR))
			{
				slError.addAll((StringList)outputMap.get(pgApolloConstants.STR_ERROR));
			}			
			//Fix for Report Type Issue and Business object does not found issue Starts
			if(null!=slError && !slError.isEmpty())
			{
				outputMap.put(pgApolloConstants.STR_ERROR, slError);				
			}
			//Fix for Report Type Issue and Business object does not found issue Ends			

		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage() ,e);
			String strErrorMessage = e.getMessage();
			if(null == strErrorMessage)
			{
				strErrorMessage =  pgApolloConstants.STR_NULL_STRING;
			}
			strErrorMessage = pgApolloConstants.STR_ERROR_REFINE_CHARACTERISTICS.replace("<ERROR>", strErrorMessage);
			StringBuilder sbError = new StringBuilder();
			sbError.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(strErrorMessage);
			StringList slAllErrors = new StringList();
			if(outputMap.containsKey(pgApolloConstants.STR_ERROR))
			{
				slAllErrors = (StringList)outputMap.get(pgApolloConstants.STR_ERROR);
			}
			slAllErrors.add(0, sbError.toString());
			outputMap.put(pgApolloConstants.STR_ERROR, slAllErrors);
		}
		return outputMap;
	}


	/**
	 * Method to update Map with Error
	 * @param mapCharInfo
	 * @param strErrorString
	 */
	public Map updateMapWithError(Map mapInfo, String strErrorString) 
	{
		mapInfo.computeIfAbsent(pgApolloConstants.STR_ERROR, key -> new StringList());
		mapInfo.computeIfPresent(pgApolloConstants.STR_ERROR, (key, value) -> {
			((StringList)value).add(strErrorString);
			return value;
		}
		);
		return mapInfo;
	}
	
	/**
	 * Method to validate Range Values for Characteristics
	 * @param context 
	 * @param mapList
	 * @return
	 * @throws Exception 
	 */
	private Map validateLimitRangeValuesForCharacteristics(matrix.db.Context context, Map outputMap) throws Exception 
	{
		StringList slRangeValueErrorList = new StringList();
		StringBuilder sbError;
		Map mapPerfChar;
		String strUniqueKey;
		String strTarget;
		String strLowerTarget;
		String strUpperTarget;
		String strLowerSL;
		String strUpperSL;
		String strLowerRoutineLimit;
		String strUpperRoutineLimit;	
		String strReportToNearest;
		Set<String> keysFromMap;
		MapList mapList = (MapList)outputMap.get(pgApolloConstants.KEY_PERFCHAR);
		boolean bRangeValidationError;
		if(null!=mapList && !mapList.isEmpty())
		{
			Iterator itrMapPerformanceChar = mapList.iterator();
			while(itrMapPerformanceChar.hasNext())
			{				
				mapPerfChar = (Map) itrMapPerformanceChar.next();
				strUniqueKey = (String)mapPerfChar.get(pgApolloConstants.KEY_UNIQUEKEY);
				strTarget = DomainConstants.EMPTY_STRING;
				strLowerTarget = DomainConstants.EMPTY_STRING;
				strUpperTarget = DomainConstants.EMPTY_STRING;
				strLowerSL = DomainConstants.EMPTY_STRING;
				strUpperSL = DomainConstants.EMPTY_STRING;
				strLowerRoutineLimit = DomainConstants.EMPTY_STRING;
				strUpperRoutineLimit = DomainConstants.EMPTY_STRING;
				strReportToNearest = DomainConstants.EMPTY_STRING;
				
				
				keysFromMap = mapPerfChar.keySet();
				
				if(mapPerfChar.containsKey(pgApolloConstants.STRING_PARAMETERVALUE))
				{
					strTarget = (String)mapPerfChar.get(pgApolloConstants.STRING_PARAMETERVALUE);
				}
				if(mapPerfChar.containsKey(pgApolloConstants.STRING_MINVALUE))
				{
					strLowerTarget = (String)mapPerfChar.get(pgApolloConstants.STRING_MINVALUE);
				}
				if(mapPerfChar.containsKey(pgApolloConstants.STRING_MAXVALUE)) 
				{
					strUpperTarget = (String)mapPerfChar.get(pgApolloConstants.STRING_MAXVALUE);
				}
				if(mapPerfChar.containsKey(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT)) 
				{
					strLowerSL = (String)mapPerfChar.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT);
					 if(!keysFromMap.contains(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT)) 
					 {
						 strLowerRoutineLimit = strLowerSL;
						 mapPerfChar.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT, strLowerRoutineLimit);
					 }
				}
				if(mapPerfChar.containsKey(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT))
				{
					strUpperSL = (String)mapPerfChar.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT);
					if(!keysFromMap.contains(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT)) 
					 {
						strUpperRoutineLimit = strUpperSL;
						mapPerfChar.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT, strUpperRoutineLimit);
					 }
				}
				if(mapPerfChar.containsKey(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT)) 
				{
					strLowerRoutineLimit = (String)mapPerfChar.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT);
				}
				if(mapPerfChar.containsKey(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT))
				{
					strUpperRoutineLimit = (String)mapPerfChar.get(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT);
				}	
				if(mapPerfChar.containsKey(pgApolloConstants.ATTRIBUTE_PGREPORTTONEAREST)) 
				{
					strReportToNearest = (String)mapPerfChar.get(pgApolloConstants.ATTRIBUTE_PGREPORTTONEAREST);
				}
				if(UIUtil.isNotNullAndNotEmpty(strReportToNearest) && !(NumberUtils.isNumber(strReportToNearest) && Double.parseDouble(strReportToNearest) >= 0.0))
				{
					sbError = new StringBuilder();
					sbError.append(pgApolloConstants.STR_ERROR_REPORT_TONEAREST);
					sbError.append(strUniqueKey);
					slRangeValueErrorList.add(sbError.toString());
					mapPerfChar = updateMapWithError(mapPerfChar, sbError.toString());	
				}
				strLowerSL = getParameterValue(strLowerSL);
				strLowerRoutineLimit = getParameterValue(strLowerRoutineLimit);
				strLowerTarget = getParameterValue(strLowerTarget);
				strTarget = getParameterValue(strTarget);
				strUpperTarget = getParameterValue(strUpperTarget);
				strUpperRoutineLimit = getParameterValue(strUpperRoutineLimit);
				strUpperSL = getParameterValue(strUpperSL);	
				bRangeValidationError = validateRangeForLimitAndTargets(context, strLowerSL, strLowerRoutineLimit, strLowerTarget, strTarget, strUpperTarget, strUpperRoutineLimit, strUpperSL);
				if(bRangeValidationError)
				{
					sbError = new StringBuilder();
					sbError.append(pgApolloConstants.STR_ERROR_EXPRESSION_NOT_SATISFIED);
					sbError.append(strUniqueKey);
					slRangeValueErrorList.add(sbError.toString());
					mapPerfChar = updateMapWithError(mapPerfChar, sbError.toString());	
				}				
			}

			outputMap.put(pgApolloConstants.KEY_PERFCHAR, mapList);
			if(!slRangeValueErrorList.isEmpty())
			{
				StringList slAllErrorlist = new StringList();
				if(outputMap.containsKey(pgApolloConstants.STR_ERROR))
				{
					slAllErrorlist = (StringList)outputMap.get(pgApolloConstants.STR_ERROR);
				}
				slAllErrorlist.addAll(slRangeValueErrorList);
				outputMap.put(pgApolloConstants.STR_ERROR, slAllErrorlist);
			}
		}		
		return outputMap;
	}


	/**
	 * APOLLO 2018x.5 A10-499 - Sync Changes for Query in XML Format Changes
	 * Method to read child elements for particular node key
	 * @param prodElement
	 * @param strNodeKey
	 * @return
	 * @throws Exception
	 */
	private MapList readChildrenElements (Element prodElement, String strNodeKey) throws Exception {
		MapList returnList =new MapList ();
		try 
		{
			if(null != prodElement)
			{
				List listNodes = prodElement.getChildren(strNodeKey);
				if(null != listNodes && listNodes.size() >=0 )
				{
					Element nodeElement = (Element) listNodes.get(0);
					List catagoryList =nodeElement.getChildren();
					if(null != catagoryList)
					{
						Iterator catagoryListItr = catagoryList.iterator();
						String strCatagory = DomainConstants.EMPTY_STRING;
						List charList = null;
						Iterator charListItr = null;
						Element charElement = null;
						Element catagoryElement;
						Map charMap =null;
						while(catagoryListItr.hasNext()) {
							catagoryElement =(Element)catagoryListItr.next();
							strCatagory = catagoryElement.getName();
							charList = catagoryElement.getChildren();
							charListItr =charList.iterator();
							while(charListItr.hasNext()) {
								charElement=(Element)charListItr.next();
								charMap = convertElementToMap(charElement);
								charMap.put(pgApolloConstants.KEY_PLYGROUPNAME, strCatagory.trim());
								returnList.add(charMap);
							}				
						}	
					}
				}
			}
		}
		catch(Exception ex) 
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
			throw ex;
		}
		return returnList;
	}
	
	/**
	 * APOLLO 2018x.5 A10-499 - Sync Changes for Query in XML Format Changes
	 * Method to convert XML element to its attribute Map
	 * @param element
	 * @return
	 * @throws Exception
	 */
	private Map convertElementToMap(Element element)throws Exception
	{
		Map returnMap =new HashMap();
		try 
		{
			if(null != element)
			{
				List attributeList =element.getAttributes();
				if(null !=attributeList)
				{
					Iterator attributeItr =attributeList.iterator();
					Attribute attribute =null;
					String strAttributeName=DomainConstants.EMPTY_STRING;
					String strAttributeValue=DomainConstants.EMPTY_STRING;
					while(attributeItr.hasNext()) {						
						attribute =(Attribute)attributeItr.next();
						strAttributeName =attribute.getName();
						strAttributeValue =attribute.getValue();
						strAttributeName = StringEscapeUtils.unescapeXml(strAttributeName);
						strAttributeValue = StringEscapeUtils.unescapeXml(strAttributeValue);
						returnMap.put(strAttributeName, strAttributeValue);
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return returnMap;
	}    
    
    
	//Get Given Schema attribute
	/**
	 * APOLLO 2018x.5 A10-499 - Sync Changes for Query in XML Format Changes
	 * Fetch attribute list for given schema
	 * @param context
	 * @param schemaType
	 * @param schemaName
	 * @return
	 * @throws Exception
	 */
	public StringList getSchemaAttributes(matrix.db.Context context, String schemaType, String schemaName) throws Exception{
		StringList strAttributeList = new StringList();
		try
		{
			String strMQLCommand = "list '"+schemaType+"' $1 select $2 dump $3";
			String strMQLResult = MqlUtil.mqlCommand(context, strMQLCommand, schemaName, "attribute", pgApolloConstants.CONSTANT_STRING_PIPE );
			StringTokenizer strResultTokenizer = new StringTokenizer(strMQLResult.trim(), pgApolloConstants.CONSTANT_STRING_PIPE);
			String strAttribute = null;
			while (strResultTokenizer.hasMoreTokens())
			{
				strAttribute = strResultTokenizer.nextToken().trim();
				strAttributeList.add(strAttribute);
			}
		}catch (Exception ex){
			ex.printStackTrace();
			throw ex;
		}
		return strAttributeList;
	}

	/**
	 * Method used to create Background Job for collaborate with APP
	 * @param context
	 * @param mapProduct
	 * @param strPartObjID
	 * @param strVPMReferenceObjId
	 * @param mapAPPInfo
	 * @return Map
	 */
	private Map initiateBackgroundJobExecutionForCollaborate(matrix.db.Context context, Map mapProduct, String strPartObjID, String strVPMReferenceObjId, String strVPMRefName, String strVPMRefRev, Map mapAPPInfo, boolean bFreezeDesign) throws Exception 
	{
		Map mapOutMap = new HashMap();
		try 
		{		
			
			boolean bDrawingUpdateStatusFlag = (boolean)mapProduct.get(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS_BOOLEAN_FLAG);
			
			String strAPPName = (String)mapAPPInfo.get(DomainConstants.SELECT_NAME);
			String strAPPRevision = (String)mapAPPInfo.get(DomainConstants.SELECT_REVISION); 
			String strJobTitle = new StringBuilder(pgApolloConstants.STRING_COLLABORATE_WITH_APP_JOB_TITLE).append(strAPPName).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(strAPPRevision).toString();
			
			VPLMIntegTraceUtil.trace(context, "MassCollab: initiateBackgroundJobExecutionForCollaborate strJobTitle : " + strJobTitle);

			//If 'Freeze Design' option is selected, Physical Product is promoted to 'Frozen' state.
			DomainObject domVPMRefObj  = DomainObject.newInstance(context, strVPMReferenceObjId);
			StringList slSelectObject = new StringList(2);
			slSelectObject.addElement(DomainConstants.SELECT_CURRENT);
			slSelectObject.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
			
			Map mapVPMRefInfo = domVPMRefObj.getInfo(context, slSelectObject);
			String strVPMRefCurrent = (String)mapVPMRefInfo.get(DomainConstants.SELECT_CURRENT);
			String strLPDModelType = (String)mapVPMRefInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
			
			MapList mlConnected3DShapeDrawings = new MapList();
			
			if(bFreezeDesign && bDrawingUpdateStatusFlag)
			{
				updateTimeStampDetails(GET_CHILDREN_BEFORE_FREEZE_DESIGN_START);
				
				mlConnected3DShapeDrawings = getConnectedInWork3DShapeDrawings(context, strVPMReferenceObjId);
				
				updateTimeStampDetails(GET_CHILDREN_BEFORE_FREEZE_DESIGN_END);
								 
				freezeVPMReference(context, strVPMReferenceObjId);				
				
				updateTimeStampDetails(FREEZE_DESIGN);
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: initiateBackgroundJobExecutionForCollaborate freezeVPMReference Successful : ");
			}
			else if(!bDrawingUpdateStatusFlag)
			{
				VPLMIntegTraceUtil.trace(context, "MassCollab: initiateBackgroundJobExecutionForCollaborate  freezeVPMReference failed : Drawing Update is failed");
			}
			
			Job bgJob = new Job();
			bgJob.setTitle(strJobTitle);
			bgJob.setActionOnCompletion("Delete");
			bgJob.setNotifyOwner("No");
			bgJob.create(context);		
			
			StringList slObjectSelect = new StringList(2);
			slObjectSelect.addElement(DomainConstants.SELECT_ID);
			slObjectSelect.addElement(DomainConstants.SELECT_NAME);
			Map mapJobInfo = bgJob.getInfo(context, slObjectSelect);
			String strJobId = (String)mapJobInfo.get(DomainConstants.SELECT_ID);
			String strJobName = (String)mapJobInfo.get(DomainConstants.SELECT_NAME);		
			
			updateTimeStampDetails(BG_INTIIATION);
			mapProduct.put("APPInfo", mapAPPInfo);
//			mapProduct.put("BackgroundExecution", true);
			mapProduct.put("executionMap", mapExecution);
			
			//Initiated background job
			Map mapParamArgs = new HashMap();			
			mapParamArgs.put("ProductMap", mapProduct);
			mapParamArgs.put("APPInfo", mapAPPInfo);
			mapParamArgs.put("PartObjectId", strPartObjID);
			mapParamArgs.put("VPMReferenceId", strVPMReferenceObjId);
			mapParamArgs.put("VPMReferenceName", strVPMRefName);
			mapParamArgs.put("VPMReferenceRevision", strVPMRefRev);
			mapParamArgs.put("VPMReferenceCurrentState", strVPMRefCurrent);
			mapParamArgs.put(pgApolloConstants.KEY_FREEZEDESIGN, String.valueOf(bFreezeDesign));
			mapParamArgs.put(pgApolloConstants.STR_MODEL_TYPE, strLPDModelType);
			mapParamArgs.put("JobId", strJobId);
			mapParamArgs.put("JobName", strJobName);
			mapParamArgs.put("Associated3DShapeDrawings", mlConnected3DShapeDrawings);
			
			VPLMIntegTraceUtil.trace(context, "MassCollab: mapParamArgs to BackgroundJob : " + mapParamArgs );
						
			BackgroundProcess backgroundProcess = new BackgroundProcess();
			backgroundProcess.submitJob(context, "com.png.apollo.sync.ebom.GenerateEBOMService", "backgroundJobExecutionForCollaborate", JPO.packArgsRemote(mapParamArgs), strJobId );
						
			mapOutMap.put(pgApolloConstants.STR_BACKGROUND_JOB_NAME, strJobName);
			
			//Connect Background Job to APP by Pending Job connection
			bgJob.addFromObject(context, new RelationshipType(pgApolloConstants.RELATIONSHIP_PENDING_JOB), strPartObjID);				
			
			mapOutMap.put(pgApolloConstants.STR_SUCCESS, pgApolloConstants.STR_SUCCESS_BACKGROUND_JOB_INITIATED.replaceFirst("<JOB_NAME>", strJobName))	;									
			
		} 
		catch (Exception e) 
		{
			mapOutMap.put(pgApolloConstants.STR_ERROR, e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage() ,e);
		}
		return mapOutMap;
	}
	
	/**
	 * Background Job method for collaborate with APP
	 * @param context
	 * @param args
	 * @return
	 */
	public void backgroundJobExecutionForCollaborate(matrix.db.Context context, String[] args) throws Exception
	{
		String strVPMReferenceObjId = DomainConstants.EMPTY_STRING;
		String strVPMRefName = DomainConstants.EMPTY_STRING;
		String strVPMRefRev = DomainConstants.EMPTY_STRING;
		String strVPMRefOldState = DomainConstants.EMPTY_STRING;
		String strSuccessMessage = DomainConstants.EMPTY_STRING;
		String strPartObjID = DomainConstants.EMPTY_STRING;
		String strPartObjName = DomainConstants.EMPTY_STRING;
		String strPartObjRevision = DomainConstants.EMPTY_STRING;
		String strPartObjTitle = DomainConstants.EMPTY_STRING;
		String strPartObjDescription = DomainConstants.EMPTY_STRING;
		String strLPDModelType = DomainConstants.EMPTY_STRING;
		String strJobId = DomainConstants.EMPTY_STRING;
		String strJobName = DomainConstants.EMPTY_STRING;
		boolean bFreezeDesign = false;
		StringBuilder sbErrorMessage = new StringBuilder();
		boolean bError = false;
		boolean isJobAbortedOrDeleted = false;
		StringList slSuccessMessages = new StringList();
		MapList mlConnected3DShapeDrawings = new MapList();
		boolean bDrawingUpdateStatusFlag = true;
		try 
		{			
			Map mapParamArgs = JPO.unpackArgs(args);
			Map mapProduct = (Map)mapParamArgs.get("ProductMap");
			Map mapAPPInfo = (Map)mapParamArgs.get("APPInfo");
			strPartObjID = (String)mapParamArgs.get("PartObjectId");
			strVPMReferenceObjId = (String)mapParamArgs.get("VPMReferenceId");
			strVPMRefName = (String)mapParamArgs.get("VPMReferenceName");
			strVPMRefRev = (String)mapParamArgs.get("VPMReferenceRevision");
			strVPMRefOldState = (String)mapParamArgs.get("VPMReferenceCurrentState");
			strPartObjName = (String)mapAPPInfo.get(DomainConstants.SELECT_NAME); 
			strPartObjRevision = (String)mapAPPInfo.get(DomainConstants.SELECT_REVISION); 
			strPartObjTitle = (String)mapAPPInfo.get(DomainConstants.SELECT_ATTRIBUTE_TITLE); 
			strPartObjDescription = (String)mapAPPInfo.get(DomainConstants.SELECT_DESCRIPTION); 
			bFreezeDesign = Boolean.parseBoolean((String)mapParamArgs.get(pgApolloConstants.KEY_FREEZEDESIGN));
			strLPDModelType = (String)mapParamArgs.get(pgApolloConstants.STR_MODEL_TYPE); 
			strJobId = (String)mapParamArgs.get("JobId");
			strJobName = (String)mapParamArgs.get("JobName");
			mlConnected3DShapeDrawings = (MapList)mapParamArgs.get("Associated3DShapeDrawings");
			bDrawingUpdateStatusFlag = (boolean)mapProduct.get(pgApolloConstants.KEY_UPDATEDRAWINGSTATUS_BOOLEAN_FLAG);

			mapExecution = (Map)mapProduct.get("executionMap");	
			sVPMRefName = strVPMRefName;
			sVPMRefRevision = strVPMRefRev;
			sAPPName = strPartObjName;
			sAPPRevision = strPartObjRevision;
			
			updateTimeStampDetails(BG_PROCESSING_STARTED);					
			
			loggerSync.debug( "=======================================================================" );	
			String strTime =  new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerSync.debug( " Collaborate with Assembled Product Part - Background Job: {} STARTS AT : {}" , strJobName , strTime);
			loggerSync.debug( " Background Job: Physical Product Name : {}" , strVPMRefName);
			loggerSync.debug( " Background Job: Physical Product Rev : {}" , strVPMRefRev);
			
			//Collaborate With APP 
			Map outPutMap = collaborateWithAPP(context, mapProduct, strPartObjID, strVPMReferenceObjId);
			if(outPutMap.containsKey(pgApolloConstants.STR_ERROR))
			{
				bError = true;
				sbErrorMessage.append((String)outPutMap.get(pgApolloConstants.STR_ERROR));
			}
			if(outPutMap.containsKey(pgApolloConstants.STR_SUCCESS))
			{
				slSuccessMessages = (StringList) outPutMap.get(pgApolloConstants.STR_SUCCESS);
				if(null != slSuccessMessages && !slSuccessMessages.isEmpty())
				{					
					strSuccessMessage = StringUtil.join(slSuccessMessages, "</br>");
				}
			}
	
		} 
		catch (Exception e) 
		{
			bError = true;
			if(UIUtil.isNotNullAndNotEmpty(sbErrorMessage.toString()))
			{
				sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_CARET);
			}
			sbErrorMessage.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
			String strEndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
			loggerSync.debug( " Error in catch Job : {} {} ", strEndTime, e.getLocalizedMessage());
		}
		finally
		{			
			//Demote VPMReference back to IN_WORK state
			if(bError && bFreezeDesign && bDrawingUpdateStatusFlag && UIUtil.isNotNullAndNotEmpty(strVPMReferenceObjId) && pgApolloConstants.STATE_IN_WORK.equalsIgnoreCase(strVPMRefOldState))
			{
				updateTimeStampDetails(DEMOTE_VPMREF_START);				
				demoteVPMReferenceOrAssociated3DShapeDrawing (context, strVPMReferenceObjId);
				loggerSync.debug("VPMReference demoted back to original state {}", strVPMReferenceObjId);
				demoteAssociated3DShapeDrawings(context, mlConnected3DShapeDrawings);
				updateTimeStampDetails(DEMOTE_VPMREF_END);
			}		
			
			isJobAbortedOrDeleted = isJobAbortedOrDeleted (context, strJobId);
			
			String strEmailSubject = DomainConstants.EMPTY_STRING;
			//Email Message
			String strEnerprisePart = new StringBuilder(strPartObjName).append(pgApolloConstants.CONSTANT_STRING_DOT).append(strPartObjRevision).toString();
			//Get Base URL
			String strBaseURL = JPO.invoke(context, "emxMailUtil", null, "getBaseURL", new String[0], String.class);
			strEnerprisePart = new StringBuilder("<a href=").append(strBaseURL).append("?objectId=").append(strPartObjID).append(">").append(strEnerprisePart).append("</a>").toString();
			
			String strCollaborationStatus = DomainConstants.EMPTY_STRING;
			String strCommentOrError = DomainConstants.EMPTY_STRING;
			String strCommentOrErrorMessage = DomainConstants.EMPTY_STRING;
			StringBuilder sbEmailMessage = new StringBuilder();
			if(bError)
			{
				if(UIUtil.isNotNullAndNotEmpty(strSuccessMessage))
				{
					strCommentOrErrorMessage = new StringBuilder(strSuccessMessage).append("</br></br>").toString();
				}
				if(isJobAbortedOrDeleted)
				{
					sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_CARET);
					sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_CARET);
					sbErrorMessage.append(EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.FailureMail.AbortComment"));
				}
				strEmailSubject = MessageUtil.getMessage(context, null, "emxCPN.CollaborateWithAPP.BackgroundJob.FailureMail.Subject",new String[] {strVPMRefName, strVPMRefRev, pgApolloConstants.STR_ASSEMBLED_PRODUCT_PART, strPartObjName, strPartObjRevision}, null, MessageUtil.getLocale(context), pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME);
				sbEmailMessage.append(MessageUtil.getMessage(context, null, "emxCPN.CollaborateWithAPP.BackgroundJob.FailureMail.Message",new String[] {strVPMRefName, strVPMRefRev, strPartObjTitle, strPartObjDescription}, null, MessageUtil.getLocale(context), pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME));		
				strCollaborationStatus = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Failure");
				strCommentOrError = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.ErrorMessage");
				strCommentOrErrorMessage = new StringBuilder(strCommentOrErrorMessage).append(sbErrorMessage.toString().replace(pgApolloConstants.CONSTANT_STRING_CARET, "</br>")).toString();
			}
			else
			{
				strEmailSubject = MessageUtil.getMessage(context, null, "emxCPN.CollaborateWithAPP.BackgroundJob.SuccessMail.Subject",new String[] {strVPMRefName, strVPMRefRev, pgApolloConstants.STR_ASSEMBLED_PRODUCT_PART, strPartObjName, strPartObjRevision}, null, MessageUtil.getLocale(context), pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME);
				sbEmailMessage.append(MessageUtil.getMessage(context, null, "emxCPN.CollaborateWithAPP.BackgroundJob.SuccessMail.Message",new String[] {strVPMRefName, strVPMRefRev, strPartObjTitle, strPartObjDescription}, null, MessageUtil.getLocale(context), pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME));
				strCollaborationStatus = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Success");
				strCommentOrError = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Comment");
				strCommentOrErrorMessage = strSuccessMessage;
			}	
			sbEmailMessage.append("</br></br>");
			sbEmailMessage.append("<style>table#t01 {width:100%; border: 1px solid black; border-collapse: collapse;}table#t01 tr:nth-child(even) { background-color: #eee;}table#t01 tr:nth-child(odd) {background-color:#fff;}table#t01 th {background-color: #006699; color: white; border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;}table#t01 td {border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;}</style>");
			sbEmailMessage.append("<table id='t01'><tr><th>");
			sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.EnterprisePart")).append("</th><th>");
			sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Title")).append("</th><th>");
			sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Description")).append("</th><th>");
			sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.ModelType")).append("</th><th>");
			sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.CollaborateWithAPP.BackgroundJob.Message.CollaborationStatus")).append("</th><th>");
			sbEmailMessage.append(strCommentOrError).append("</th></tr>");
			sbEmailMessage.append("<tr><td>").append(strEnerprisePart);
			sbEmailMessage.append("</td><td>").append(strPartObjTitle);
			sbEmailMessage.append("</td><td>").append(strPartObjDescription);
			sbEmailMessage.append("</td><td>").append(strLPDModelType);
			sbEmailMessage.append("</td><td>").append(strCollaborationStatus);
			sbEmailMessage.append("</td><td>").append(strCommentOrErrorMessage);
			sbEmailMessage.append("</td></tr></table>");	

			// Define message details
			Map mapArgs = new HashMap();
			mapArgs.put("subject", strEmailSubject);
			mapArgs.put("message", sbEmailMessage.toString());
			mapArgs.put("objectId", strPartObjID);

			try 
			{
				loggerSync.debug("Collaborate With APP Before Sending Email - Context User :{}", context.getUser());
				updateTimeStampDetails(EMAIL_NOTIFICATION_START);
				// Send notification to the context user
				JPO.invoke(context, "pgDSMLayeredProductSyncUtil", null, "sendEmailNotification", JPO.packArgs(mapArgs));
				loggerSync.debug("Collaborate With APP  After Sending Email ");
				updateTimeStampDetails(EMAIL_NOTIFICATION_END);
			} 
			catch (Exception e) 
			{
				loggerSync.error("Error in sending email ");
				loggerSync.error(e.getMessage(), e);
			}

			String strHistory = new StringBuilder(pgApolloConstants.EBOM_SYNC_HISTORY_COLLABRATE_APP).append(pgApolloConstants.EBOM_SYNC_HISTORY_END).toString() ;
			addCustomHistoryOnSync(context,strPartObjID, strHistory);
			updateTimeStampDetails(END_OF_COLLABORATION);			
		}
	}
	
	/**
	 * Method to check Job aborted or not
	 * @param context
	 * @param strJobId
	 * @return
	 * @throws Exception
	 */
	private boolean isJobAbortedOrDeleted (matrix.db.Context context, String strJobId) throws MatrixException
	{
		boolean isJobAbortedOrDeleted = false;
		DomainObject bgJob  = DomainObject.newInstance(context, strJobId);
		if(!bgJob.exists(context))
		{
			isJobAbortedOrDeleted = true;
		}		
		return isJobAbortedOrDeleted;
	}
	
	/**
	 * Method to Promote VPMRef to Frozen state
	 * @param context
	 * @param strVPMReferenceObjId
	 * @throws Exception
	 */
	public static void freezeVPMReference (matrix.db.Context context, String strVPMReferenceObjId) throws MatrixException
	{
		if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceObjId))
		{
			DomainObject domVPMRefObj  = DomainObject.newInstance(context, strVPMReferenceObjId);
			StringList slObjectList = new StringList();
			slObjectList.add(DomainConstants.SELECT_CURRENT);
			slObjectList.add(DomainConstants.SELECT_TYPE);

			Map mapObject = domVPMRefObj.getInfo(context, slObjectList);
			String strVPMRefCurrent = (String)mapObject.get(DomainConstants.SELECT_CURRENT);
			String strVPMRefType = (String)mapObject.get(DomainConstants.SELECT_TYPE);

			if(pgApolloConstants.STATE_IN_WORK.equalsIgnoreCase(strVPMRefCurrent)) 
			{
				SignatureList slSignature = domVPMRefObj.getSignatures(context,strVPMRefCurrent,pgApolloConstants.STATE_WAITAPP);													
				for(Signature objSignature : slSignature)
				{
					domVPMRefObj.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);    	    								
				}
				SignatureList slSignatureLocal = domVPMRefObj.getSignatures(context,pgApolloConstants.STATE_IN_WORK,pgApolloConstants.STATE_PRIVATE);													
				for(Signature objSignature : slSignatureLocal)
				{
					if(objSignature.isSigned())
					{
						domVPMRefObj.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
					}
				}
				domVPMRefObj.setState(context, pgApolloConstants.STATE_WAITAPP);
			}
			else if(pgApolloConstants.STATE_WAITAPP.equalsIgnoreCase(strVPMRefCurrent) && pgApolloConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strVPMRefType))
			{
				MapList mlConnected3DShapeDrawings = getConnectedInWork3DShapeDrawings(context, strVPMReferenceObjId);					
				promoteAssociated3DShapeDrawings(context, mlConnected3DShapeDrawings);				
			}
		}
	}
	
	/**
	 * Method to Demote VPMRef Associated 3DShape and Drawings
	 * @param context
	 * @param mlConnected3DShapeDrawings
	 * @throws MatrixException
	 */
	public static void promoteAssociated3DShapeDrawings(matrix.db.Context context, MapList mlConnected3DShapeDrawings) throws MatrixException
	{
		if(null!=mlConnected3DShapeDrawings && !mlConnected3DShapeDrawings.isEmpty())
		{
			Map map3DShapeDrawingInfo;
			String str3DShapeDrawingId;
			for(Object obj : mlConnected3DShapeDrawings){
				map3DShapeDrawingInfo = (Map)obj;
				str3DShapeDrawingId = (String)map3DShapeDrawingInfo.get(DomainConstants.SELECT_ID);
				if(UIUtil.isNotNullAndNotEmpty(str3DShapeDrawingId))
				{
					freezeVPMReference(context, str3DShapeDrawingId);
					loggerSync.debug("Associated 3DShape or Drawing promoted to Frozen state {}", str3DShapeDrawingId);
				}									
			}

		}	
	}
	
	/**
	 * Method to get Associated 3DShape and Drawings
	 * @param context
	 * @param strVPMReferenceObjId
	 * @return
	 * @throws FrameworkException
	 */
	private static MapList getConnectedInWork3DShapeDrawings(matrix.db.Context context, String strVPMReferenceObjId) throws FrameworkException 
	{
		MapList mlConnected3DShapeDrawings = new MapList();
		if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceObjId))
		{
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(DomainConstants.SELECT_CURRENT);
			StringBuilder sbObjWhere = new StringBuilder();
			sbObjWhere.append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append("'").append(pgApolloConstants.STATE_IN_WORK).append("'");
			DomainObject doCADObj = DomainObject.newInstance(context, strVPMReferenceObjId);
			mlConnected3DShapeDrawings = doCADObj.getRelatedObjects(context,	//context
															pgApolloConstants.RELATIONSHIP_VPMRepInstance,// relationship pattern
															DomainConstants.QUERY_WILDCARD,	// type pattern
															busSelects,	// object selects
															null,	// relationship selects
															false,// to direction
															true,// from direction
															(short) 1,// recursion level
															sbObjWhere.toString(),// object where clause
															null,	// relationship where clause
															0);// objects Limit			
			loggerSync.debug("Associated In Work 3DShape or Drawings  {}", mlConnected3DShapeDrawings);
		}
		return mlConnected3DShapeDrawings;
	}

	
	/**
	 * Method to Demote VPMRef Associated 3DShape and Drawings
	 * @param context
	 * @param mlConnected3DShapeDrawings
	 * @throws MatrixException
	 */
	public void demoteAssociated3DShapeDrawings(matrix.db.Context context, MapList mlConnected3DShapeDrawings) throws MatrixException
	{
		if(null!=mlConnected3DShapeDrawings && !mlConnected3DShapeDrawings.isEmpty())
		{
			Map map3DShapeDrawingInfo;
			String str3DShapeDrawingId;
			for(Object obj : mlConnected3DShapeDrawings)
			{
				map3DShapeDrawingInfo = (Map)obj;
				str3DShapeDrawingId = (String)map3DShapeDrawingInfo.get(DomainConstants.SELECT_ID);
				if(UIUtil.isNotNullAndNotEmpty(str3DShapeDrawingId))
				{
					demoteVPMReferenceOrAssociated3DShapeDrawing(context, str3DShapeDrawingId);
					loggerSync.debug("Associated 3DShape or Drawing demoted back to original state {}", str3DShapeDrawingId);
				}									
			}

		}	
	}
	
	/**
	 * Method to demote VPMRef to In Work state
	 * @param context
	 * @param strVPMReferenceObjId
	 * @throws Exception
	 */
	private void demoteVPMReferenceOrAssociated3DShapeDrawing (matrix.db.Context context, String strVPMReferenceObjId) throws MatrixException
	{
		boolean isContextPushed = false;
		try
		{
			//Push context is needed to demote VPMReference from Frozen state to IN_WORK state. Context user will not always get access to demote it. 
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;		
			
			if(UIUtil.isNotNullAndNotEmpty(strVPMReferenceObjId))
			{
				DomainObject domVPMRefObj  = DomainObject.newInstance(context, strVPMReferenceObjId);
				String strVPMRefCurrent = domVPMRefObj.getInfo(context, DomainConstants.SELECT_CURRENT);
				
				if(pgApolloConstants.STATE_WAITAPP.equalsIgnoreCase(strVPMRefCurrent)) 
				{
					SignatureList slSignature = domVPMRefObj.getSignatures(context,strVPMRefCurrent,pgApolloConstants.STATE_IN_WORK);													
					for(Signature objSignature : slSignature)
					{
						domVPMRefObj.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);    	    								
					}
					SignatureList slSignatureLocal = domVPMRefObj.getSignatures(context,pgApolloConstants.STATE_WAITAPP,pgApolloConstants.STATE_SHARED);													
					for(Signature objSignature : slSignatureLocal)
					{
						if(objSignature.isSigned())
						{
							domVPMRefObj.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
						}
					}
					domVPMRefObj.setState(context, pgApolloConstants.STATE_IN_WORK);
				}
			}
		}
		catch(Exception ex)
		{
			loggerApolloTrace.error(ex.getMessage() ,ex);
		}
		finally
		{
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
	}
	
	/**
	 * Method to abort and delete previous background job
	 * @param context
	 * @param strJobTitle
	 * @throws Exception
	 */
	private void abortAndDeletePreviousBackgroundJob(matrix.db.Context context, String strJobTitle) throws Exception
	{
		
		boolean bContextPushed = false;
		try
		{
			//This is very rare usecase when Background Job is stuck and we need to abort it
			//Context user won't always get access to find and abort Background Job. So we are adding Push context in the method.
			ContextUtil.pushContext(context);
			bContextPushed = true;
			
			StringList objectSelects = new StringList();
			objectSelects.addElement(DomainConstants.SELECT_ID);
			
			StringBuffer sbWhere = new StringBuffer(pgApolloConstants.SELECT_ATTRIBUTE_TITLE);
			sbWhere.append(" == '").append(strJobTitle).append("'");
			//To check whether APP is connected with any background job or not
			
			MapList mlPendingJOB = DomainObject.findObjects(context, pgApolloConstants.TYPE_JOB , DomainConstants.QUERY_WILDCARD , sbWhere.toString(), objectSelects);
		
			VPLMIntegTraceUtil.trace(context, "MassCollab: mlPendingJOB = " + mlPendingJOB );								
		
			if(null != mlPendingJOB && !mlPendingJOB.isEmpty()) 
			{
				Map tempMap = null;
				String strJobId = DomainConstants.EMPTY_STRING;
				Job job = null;

				for(int i=0, iSize = mlPendingJOB.size(); i<iSize; i++)
				{
					tempMap = (Map)mlPendingJOB.get(i);	
					strJobId = (String)tempMap.get(DomainConstants.SELECT_ID);
					job = Job.getInstance(context, strJobId);
					if(job.isRunnable(context))
					{											
						VPLMIntegTraceUtil.trace(context, "MassCollab: Job is Runnable. ");
						job.abort(context);
					}
					job.deleteObject(context);
					VPLMIntegTraceUtil.trace(context, "MassCollab: Job is deleted.. ");
				}
				
				updateTimeStampDetails(ABORT_EXISTING_EVALUATE_CRITERIA_JOB);
				
			}									
		} catch (Exception e) {
			loggerApolloTrace.error(e.getMessage() ,e);
			VPLMIntegTraceUtil.trace(context, "MassCollab: Error while deleting background job : "+strJobTitle+" " + e.getLocalizedMessage() );									
		}		
		finally 
		{
			if(bContextPushed){
				ContextUtil.popContext(context);
				bContextPushed = false;
			}
		}
			
	}
	
	/**
	 * Method to check Running Background Job connected
	 * @param context
	 * @param strPartObjectId
	 * @param strJobTitle
	 * @param sMode
	 * @return
	 * @throws FrameworkException
	 */
	public String checkRunningBackGroundJobConnected(matrix.db.Context context , String strPartObjectId, String strJobTitle, String sMode) throws FrameworkException
	{
		String strIsRunningBackgroundJobConnected = pgApolloConstants.STR_FALSE_FLAG;
		StringBuilder sbReturnMessage = new StringBuilder();
		try
		{		
			if(UIUtil.isNotNullAndNotEmpty(strPartObjectId))
			{
				DomainObject domAPP = DomainObject.newInstance(context, strPartObjectId);

				StringList slBusSelects = new StringList(3);
				slBusSelects.addElement(DomainConstants.SELECT_NAME);
				slBusSelects.addElement(DomainConstants.SELECT_CURRENT);
				slBusSelects.addElement(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				
				
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: strJobTitle >>> " + strJobTitle);

				StringBuilder	whereExp	= new StringBuilder();
				
				String sEvaluateCriteriaJobTitle = new StringBuilder(pgApolloConstants.STRING_EVALUATE_CRITERIA_JOB_TITLE).append(strPartObjectId).toString();
				
				if(UIUtil.isNotNullAndNotEmpty(strJobTitle))
				{
					whereExp.append(pgApolloConstants.CONSTANT_STRING_OPEN_ROUND_BRACE);
					whereExp.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append("'").append(strJobTitle).append("'");
					whereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
					whereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_PIPE);
					whereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
					whereExp.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append("'").append(sEvaluateCriteriaJobTitle).append("'");
					whereExp.append(pgApolloConstants.CONSTANT_STRING_CLOSE_ROUND_BRACE);
					whereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
					whereExp.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
					whereExp.append(pgApolloConstants.CONSTANT_STRING_SPACE);
					whereExp.append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(Job.STATE_JOB_RUNNING);
				}
				
				String strJobName;
				String strJobList;				
				StringList slExistingRunningJobs = new StringList();
				Map mpJobDetails = null;
				String sJobTitle;
				boolean bEvaluateCriteriaJobPresent = false;
				
				MapList mlAPPJobDetails = domAPP.getRelatedObjects(context,//context
						pgApolloConstants.RELATIONSHIP_PENDING_JOB, // relationship pattern
						pgApolloConstants.TYPE_JOB, // type pattern
						slBusSelects, // object selects
						null, // relationship selects
						false, // to direction
						true, // from direction
						(short)1,// recursion level
						whereExp.toString(), // object where clause
						null, // relationship where clause
						0);// objects Limit
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: mlAPPJobDetails >>> " + mlAPPJobDetails);
				
				if(null !=mlAPPJobDetails && !mlAPPJobDetails.isEmpty())
				{
					for(int count =0 ; count < mlAPPJobDetails.size() ; count++)
					{
						mpJobDetails = (Map)mlAPPJobDetails.get(count);
						strJobName = (String)mpJobDetails.get(DomainConstants.SELECT_NAME);
						sJobTitle = (String)mpJobDetails.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
						if(sEvaluateCriteriaJobTitle.equals(sJobTitle))
						{
							bEvaluateCriteriaJobPresent = true;
						}
						slExistingRunningJobs.addElement(strJobName);						
					}	
					
					loggerSync.debug( " Previous Background Job still Running : {}" , slExistingRunningJobs);
					VPLMIntegTraceUtil.trace(context, "MassCollab: Previous Background Job still Running  >>> " + slExistingRunningJobs);
					
					if(!slExistingRunningJobs.isEmpty())
					{
						strJobList = StringUtil.join(slExistingRunningJobs, pgApolloConstants.CONSTANT_STRING_COMMA);
						if(bEvaluateCriteriaJobPresent)
						{
							sbReturnMessage.append(pgApolloConstants.STR_ERROR_EVALUATECRITERIABACKGROUND_JOB_ISRUNNING).append(strJobList).append(pgApolloConstants.CONSTANT_STRING_DOT);
						}
						else
						{
							sbReturnMessage.append(pgApolloConstants.STR_ERROR_BACKGROUND_JOB_ISRUNNING).append(strJobList).append(pgApolloConstants.CONSTANT_STRING_DOT);
						}
					}
				}
			}
			if(sbReturnMessage.toString().isEmpty())
			{
				sbReturnMessage.append(strIsRunningBackgroundJobConnected);
			}
		}
		catch (FrameworkException e)
		{
			sbReturnMessage = new StringBuilder();
			sbReturnMessage.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage() ,e);			
		}		
		loggerSync.debug( " Final Response in checkRunningBackGroundJobConnected : {}" , sbReturnMessage);
		return sbReturnMessage.toString();
	}

	
	
	
	/**
     * This web service is called from automation to validate VPMReference and APP for Sync
     * Format : /3dspace/resources/custosync/ebom/validateVPMReferenceAndAPPForSync?<VPMRefName>|<VPMRefRevision>
     * @param req
     * @return
     * @throws Exception
     */
     @GET
     @Path("/validateVPMReferenceAndAPPForSync")
     public Response validateVPMReferenceAndAPPForSync (@Context HttpServletRequest req) throws Exception 
     {

    	 matrix.db.Context context = null;  
    	 //Get the user context
    	 if (Framework.isLoggedIn(req)) {
    		 context = Framework.getContext(req.getSession(false));
    	 }

    	 loggerWS.debug( " =======================================================================" );
    	 loggerWS.debug("Path : /ebom/validateVPMReferenceAndAPPForSync");
    	 loggerWS.debug("Method: GenerateEBOMService : validateVPMReferenceAndAPPForSync");

    	 String strReturnMsg = DomainConstants.EMPTY_STRING;                 
    	 StringList slURLParam = new StringList();                    
    	 String strVPMRefName = DomainConstants.EMPTY_STRING;             
    	 String strVPMRefRev = DomainConstants.EMPTY_STRING;      
    	 String strVPMReferenceObjId = DomainConstants.EMPTY_STRING;

    	 int iURLParamSize = 0;
    	 StringBuilder sbReturnMessage = new StringBuilder();

    	 StringList objectSelects = new StringList(6);
    	 objectSelects.add(DomainConstants.SELECT_ID);
    	 objectSelects.add(DomainConstants.SELECT_NAME);
    	 objectSelects.add(DomainConstants.SELECT_REVISION);
    	 objectSelects.add(DomainConstants.SELECT_CURRENT);
    	 objectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
    	 objectSelects.add(DomainConstants.SELECT_DESCRIPTION);

    	 try
    	 {
    		 String strCatiaDetails = req.getQueryString();
    		 loggerWS.debug( "strCatiaDetails:: {}" , strCatiaDetails);

    		 if(UIUtil.isNotNullAndNotEmpty(strCatiaDetails))
    		 {
    			 strCatiaDetails =  java.net.URLDecoder.decode(strCatiaDetails,"UTF-8");
    			 slURLParam = StringUtil.split(strCatiaDetails, pgApolloConstants.CONSTANT_STRING_AMPERSAND);
 				 String strValidURLParam = slURLParam.get(0);
    			 slURLParam = StringUtil.split(strValidURLParam, pgApolloConstants.CONSTANT_STRING_PIPE);                              
    			 if(!slURLParam.isEmpty()) 
    			 {                           
    				 iURLParamSize = slURLParam.size();
    				 if(iURLParamSize > 1)
    				 {
    					 strVPMRefName = slURLParam.get(0);
    					 strVPMRefName = strVPMRefName.trim();
    					 strVPMRefRev = slURLParam.get(1);
    					 strVPMRefRev = strVPMRefRev.trim();
    					 strVPMReferenceObjId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, strVPMRefName, strVPMRefRev);
    					 if(UIUtil.isNullOrEmpty(strVPMReferenceObjId))
    					 {
    						 sbReturnMessage.append(pgApolloConstants.STR_ERROR);
    						 sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
    						 sbReturnMessage.append(pgApolloConstants.STR_ERROR_VPMREFERENCENOTEXIST.replace("<VPMREF_NAME>", strVPMRefName).replace("<VPMREF_REV>", strVPMRefRev));                                                             
    					 }
    					 else
    					 {
    						 strReturnMsg = validateProductAndUpdateAPP(context, strVPMReferenceObjId, strVPMRefName, strVPMRefRev,objectSelects, true);        
    						 loggerWS.debug( "strReturnMsg:: {}" , strReturnMsg);
    						 if(strReturnMsg.contains(pgApolloConstants.STR_ERROR))
    						 {
    							 sbReturnMessage.append(strReturnMsg.replaceAll(pgApolloConstants.STR_ERROR_EBOMNOTPROCESSED, DomainConstants.EMPTY_STRING));
    						 }
    						 else
    						 {
    							 sbReturnMessage.append(pgApolloConstants.STR_SUCCESS);
    						 }
    					 }
    				 }
    				 else
    				 {
    					 sbReturnMessage.append(pgApolloConstants.STR_ERROR);
						 sbReturnMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
						 sbReturnMessage.append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
    				 }
    			 }
    		 }                           
    		 loggerWS.debug( "Final Response -->{}", sbReturnMessage);
    	 } 
    	 catch (Exception e) 
    	 {
    		 loggerApolloTrace.error(e.getMessage() ,e);
    		 loggerWS.error( " Error -->{}", e.getLocalizedMessage());
    		 sbReturnMessage.append(pgApolloConstants.STR_ERROR ).append( pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
    	 }
    	 return Response.status(200).entity(sbReturnMessage.toString()).build();
     }
	 

    /**
     * Method to get Characteristics from Design Parameter file
     * @param context
     * @param strObjectId
     * @return
     * @throws Exception
     */
    public Map readPerformanceCharsFromDesignParameterFile (matrix.db.Context context, String strObjectId) throws Exception
	{
		Map mapRefinedOutputMap;
		Map mapXMLParameter = ReadWriteXMLForPLMDTDocument.getConfigXMLParameters(context, strObjectId, false, true);
		boolean bIsDesignParamPresent = false;

		StringBuilder sbParameter = new StringBuilder();
		String strIsActiveParameterKey;
		sbParameter.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.KEY_ISACTIVE);
		strIsActiveParameterKey = sbParameter.toString();		
		StringList slParamDetails;
		
		Map<String, String> mapPerformanceCharParameter = new HashMap();

		String strPlyGroupName;
		String strCategorySpecifics;
		String strCharacteristic;
		String strCharacteristicSpecifics;
		StringList slInvalidParameters = new StringList();
				
		StringBuilder sbUniqueParameterKey;
		String strUniqueParameterKey;
		Set<String> setUniqueParameterKey = new HashSet();		
    	 Set<String> setInActiveKeys = new HashSet();
    	 String sInActiveUniqueParameter;
    	 String sInActiveValue;
    	 Set<String> setInActiveUniqueParameterKey = new HashSet();

    	 List<String> slInactiveGroupStacking = getInActiveGroupsFromStacking(mapXMLParameter);

    	 context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : readPerformanceCharsFromDesignParameterFile slInactiveGroupStacking = "+slInactiveGroupStacking);
		Map mapXMLParameterOnlyActive = ReadWriteXMLForPLMDTDocument.getConfigXMLParameters(context, strObjectId, true, true);


		if(null != mapXMLParameter && mapXMLParameter.containsKey(pgApolloConstants.STR_PERFORMANCE_CHAR))
		{
			bIsDesignParamPresent = true;
			mapPerformanceCharParameter = (Map)mapXMLParameter.get(pgApolloConstants.STR_PERFORMANCE_CHAR);	
			
			if(null !=mapPerformanceCharParameter && !mapPerformanceCharParameter.isEmpty())
			{
				Set<String> setParameterKeys = mapPerformanceCharParameter.keySet();
				
				for(String sParameterKey : setParameterKeys)
				{
					if(StringUtils.endsWithIgnoreCase(sParameterKey, strIsActiveParameterKey))
					{

    					 sInActiveValue = mapPerformanceCharParameter.get(sParameterKey);						

    					 if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(sInActiveValue) || pgApolloCommonUtil.containsInListCaseInsensitive(sParameterKey, slInactiveGroupStacking))
    					 {
    						 sInActiveUniqueParameter = sParameterKey.substring(0, sParameterKey.length() - strIsActiveParameterKey.length());
    						 setInActiveKeys.add(sInActiveUniqueParameter);
    					 }
					}
					else
					{
						slParamDetails = StringUtil.split(sParameterKey, pgApolloConstants.CONSTANT_STRING_DOT);
						
						if(!slParamDetails.isEmpty())
						{
							int iParamListSize = slParamDetails.size();						
							if(iParamListSize < 6 && iParamListSize >= 4)
							{
								strPlyGroupName = slParamDetails.get(0);
								strCategorySpecifics = slParamDetails.get(1);
								strCharacteristic = slParamDetails.get(2);
								if(iParamListSize >= 5)
								{
									strCharacteristicSpecifics = slParamDetails.get(3);
								}
								else
								{
									strCharacteristicSpecifics = DomainConstants.EMPTY_STRING;
								}					
								sbUniqueParameterKey = new StringBuilder();
								sbUniqueParameterKey.append(strPlyGroupName);
								sbUniqueParameterKey.append(pgApolloConstants.CONSTANT_STRING_DOT).append(strCategorySpecifics);
								sbUniqueParameterKey.append(pgApolloConstants.CONSTANT_STRING_DOT).append(strCharacteristic);
								if(UIUtil.isNotNullAndNotEmpty(strCharacteristicSpecifics))
								{
									sbUniqueParameterKey.append(pgApolloConstants.CONSTANT_STRING_DOT).append(strCharacteristicSpecifics);
								}
								
								strUniqueParameterKey = sbUniqueParameterKey.toString();								
								
								setUniqueParameterKey.add(strUniqueParameterKey);
							}
							else
							{
								slInvalidParameters.add(sParameterKey);
							}
						}
					}
				}
			}
		}	

    	 context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : readPerformanceCharsFromDesignParameterFile setInActiveKeys = "+setInActiveKeys);

    	 String sInActiveParameterKeyWithDot;

    	 if(!setUniqueParameterKey.isEmpty() && !setInActiveKeys.isEmpty())
    	 {
    		 for(String sParameterKey : setUniqueParameterKey)
    		 {
    			 for(String sInActiveParameterKey : setInActiveKeys)
    			 {
    				 sInActiveParameterKeyWithDot = new StringBuilder(sInActiveParameterKey).append(pgApolloConstants.CONSTANT_STRING_DOT).toString();

    				 if(sInActiveParameterKey.equalsIgnoreCase(sParameterKey)  || StringUtils.startsWithIgnoreCase(sParameterKey, sInActiveParameterKeyWithDot))
    				 {
    					 setInActiveUniqueParameterKey.add(sParameterKey);
    				 }
    			 }
    		 }
    	 }		

    	 context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : readPerformanceCharsFromDesignParameterFile setInActiveUniqueParameterKey = "+setInActiveUniqueParameterKey);

    	 Map mapOutput = getPerformanceCharWithAttributes(context, mapPerformanceCharParameter, setUniqueParameterKey, setInActiveUniqueParameterKey);

    	 context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : readPerformanceCharsFromDesignParameterFile mapOutput = "+mapOutput);

		MapList mlActiveCharacteristics = (MapList)mapOutput.get(pgApolloConstants.KEY_ACTIVE_CHARS);
		MapList mlInActiveCharacteristics = (MapList)mapOutput.get(pgApolloConstants.KEY_INACTIVE_CHARS);
		
		mapRefinedOutputMap = refinePerformanceCharacteristics(context, mlActiveCharacteristics);	
		
		StringList slUniquePlyGroup = getUniqueGroupsForStackingLayers(mapXMLParameterOnlyActive, mapRefinedOutputMap);		
		
		mapRefinedOutputMap.put(pgApolloConstants.KEY_CHARPLYGROUPLIST, slUniquePlyGroup);

		mapRefinedOutputMap.put(pgApolloConstants.KEY_INACTIVE_CHARS, mlInActiveCharacteristics);		
		
		mapRefinedOutputMap.put(pgApolloConstants.STR_ISDESIGNPARAMPRESENT, bIsDesignParamPresent);		
		
		if(!slInvalidParameters.isEmpty())
		{
			String sInvalidParametersList = StringUtil.join(slInvalidParameters, pgApolloConstants.CONSTANT_STRING_NEWLINE);					
			String sInvalidParameterMessage = new StringBuilder(pgApolloConstants.STR_ERROR_INVALID_CHARACTERS_PARAMETER).append(pgApolloConstants.CONSTANT_STRING_NEWLINE).append(sInvalidParametersList).toString();
			mapRefinedOutputMap = updateMapWithError(mapRefinedOutputMap, sInvalidParameterMessage);	
		}
				
		return mapRefinedOutputMap;
	}
    
    
    /**
     * Method to get Inactive Groups from Stacking
     * @param mapXMLParameter
     * @return
     */
    private List<String> getInActiveGroupsFromStacking(Map mapXMLParameter) 
    {
    	List<String> listInactiveGroupList = new ArrayList();
    	
    	String sProductDefintionKey = pgApolloConstants.STR_PRODUCT_DEFINITION;
    	
    	StringBuilder sbParameter = new StringBuilder();
		String sIsActiveParameterKey;
		sbParameter.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.KEY_ISACTIVE);
		sIsActiveParameterKey = sbParameter.toString();
		StringList slParamDetails;
		int iParamListSize = 0;
		String sParameterValue;
    	
		if(null != mapXMLParameter && mapXMLParameter.containsKey(sProductDefintionKey))
		{
			Map mapDesignParamParameter = (Map)mapXMLParameter.get(sProductDefintionKey);	
						
			if(null !=mapDesignParamParameter && !mapDesignParamParameter.isEmpty())
			{
				Set<String> setParameterKeys = mapDesignParamParameter.keySet();
				
				for(String sParameterKey : setParameterKeys)
				{
					if(StringUtils.endsWithIgnoreCase(sParameterKey, sIsActiveParameterKey))
					{
						slParamDetails = StringUtil.split(sParameterKey, pgApolloConstants.CONSTANT_STRING_DOT);						
						if(!slParamDetails.isEmpty())
						{
							iParamListSize = slParamDetails.size();						
							if(iParamListSize == 2)
							{
								sParameterValue = (String)mapDesignParamParameter.get(sParameterKey);
								if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(sParameterValue))
								{
									listInactiveGroupList.add(sParameterKey);
								}
							}
						}			
					}					
				}
			}
			
		}
		return listInactiveGroupList;
	}

	/**
     * Method to get Unique Groups for Stacking Layers
     * @param mapXMLParameterOnlyActive
     * @param mapRefinedOutputMap 
     * @return
     */
	public StringList getUniqueGroupsForStackingLayers(Map mapXMLParameterOnlyActive, Map mapRefinedOutputMap) 
	{
		StringList slUniquePlyGroup = new StringList();
		StringBuilder sbParameter = new StringBuilder();
		String strIsActiveParameterKey;
		sbParameter.append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.KEY_ISACTIVE);
		strIsActiveParameterKey = sbParameter.toString();	
		Set<String> setUniquePlyGroup = new HashSet();	
		StringList slParamDetails;
		String sPlyGroupName;

		if(null != mapXMLParameterOnlyActive && mapXMLParameterOnlyActive.containsKey(pgApolloConstants.STR_PRODUCT_DEFINITION))
		{
			Map mapDesignParamParameter = (Map)mapXMLParameterOnlyActive.get(pgApolloConstants.STR_PRODUCT_DEFINITION);	
			
			if(null !=mapDesignParamParameter && !mapDesignParamParameter.isEmpty())
			{
				Set<String> setParameterKeys = mapDesignParamParameter.keySet();
				
				for(String sParameterKey : setParameterKeys)
				{
					if(!StringUtils.endsWithIgnoreCase(sParameterKey, strIsActiveParameterKey))
					{
						slParamDetails = StringUtil.split(sParameterKey, pgApolloConstants.CONSTANT_STRING_DOT);						
						if(!slParamDetails.isEmpty())
						{
							int iParamListSize = slParamDetails.size();						
							if(iParamListSize > 3)
							{
								sPlyGroupName = slParamDetails.get(0);
								setUniquePlyGroup.add(sPlyGroupName);
							}
						}			
					}					
				}
			}
		}		
		
		StringList slPlyGroup = new StringList();
		if(mapRefinedOutputMap.containsKey(pgApolloConstants.KEY_CHARPLYGROUPLIST))
		{
			slPlyGroup = (StringList)mapRefinedOutputMap.get(pgApolloConstants.KEY_CHARPLYGROUPLIST);
		}
		setUniquePlyGroup.addAll(slPlyGroup);		
		slUniquePlyGroup.addAll(setUniquePlyGroup);		
		
		return slUniquePlyGroup;
	}


	/**
	 * Method to get MapList for Applicable Chars/IsActive Chars
	 * @param context 
	 * @param mapParameter
	 * @param setUniqueParameterKey
	 * @param setInActiveUniqueParameterKey
	 * @return
	 * @throws Exception 
	 */
	private Map getPerformanceCharWithAttributes(matrix.db.Context context, Map<String, String> mapParameter, Set<String> setUniqueParameterKey, Set<String> setInActiveUniqueParameterKey) throws Exception 
	{
		Map mapOutput = new HashMap();
		MapList mlPerformanceChar = new MapList();
		MapList mlInActivePerformanceChar = new MapList();

		StringList slParamDetails;
		String strPlyGroupName;
		String strCategorySpecifics;
		String strCharacteristic;
		String strCharacteristicSpecifics;
		StringBuilder sbKeyBuilder;
		String sUniqueKey;
		String sReportType;
		Map mapCharInfo;
		Set<String> keysFromMap;
		DecimalFormat dfnumberFormat = new DecimalFormat(pgApolloConstants.FORMAT_DECIMAL_NINE);		
		String strAttributeCharMapping = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloServices.EvaluateCriteriaWithSync.ValueExtractionAttributeMapping");
		StringList slAttributeMapping = new StringList();
		if(UIUtil.isNotNullAndNotEmpty(strAttributeCharMapping))
		{
			slAttributeMapping	= StringUtil.split(strAttributeCharMapping, pgApolloConstants.CONSTANT_STRING_PIPE);
		}	
		
		if(null!=setUniqueParameterKey && !setUniqueParameterKey.isEmpty())
		{
			StringList slUniqueParamterKeyList = new StringList();
			slUniqueParamterKeyList.addAll(setUniqueParameterKey);			
			for(String strUniqueParameterKey : slUniqueParamterKeyList)
			{
				if(null!=strUniqueParameterKey)
				{				
					slParamDetails = StringUtil.split(strUniqueParameterKey, pgApolloConstants.CONSTANT_STRING_DOT);
					
					if(!slParamDetails.isEmpty())
					{
						int iParamListSize = slParamDetails.size();			
						
						if(iParamListSize >= 3)
						{
							strPlyGroupName = slParamDetails.get(0).trim();
							strCategorySpecifics = slParamDetails.get(1).trim();
							strCharacteristic = slParamDetails.get(2).trim();
							if(iParamListSize == 4)
							{
								strCharacteristicSpecifics = slParamDetails.get(3).trim();
							}
							else
							{
								strCharacteristicSpecifics = DomainConstants.EMPTY_STRING;
							}												
							
							Map mapAttribute = mapParameter.entrySet()
								      .stream()
								      .filter(map -> map.getKey().startsWith(new StringBuilder(strUniqueParameterKey).append(pgApolloConstants.CONSTANT_STRING_DOT).toString()))
								      .collect(Collectors.toMap(map -> map.getKey().replace(new StringBuilder(strUniqueParameterKey).append(pgApolloConstants.CONSTANT_STRING_DOT).toString(), ""), map -> map.getValue()));
							mapCharInfo = new HashMap();
							mapCharInfo.put(pgApolloConstants.ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY, strPlyGroupName);
							mapCharInfo.put(pgApolloConstants.ATTRIBUTE_PG_CATEGORYSPECIFICS, strCategorySpecifics);
							mapCharInfo.put(DomainConstants.ATTRIBUTE_TITLE, strCharacteristic);
							mapCharInfo.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSPECIFIC, strCharacteristicSpecifics);
							
							keysFromMap = mapAttribute.keySet();

							mapAttribute = updatePerformanceCharWithActualValues(keysFromMap, dfnumberFormat, slAttributeMapping,mapAttribute);
							
							mapCharInfo.putAll(mapAttribute);							
							
							sbKeyBuilder = new StringBuilder();
							sbKeyBuilder.append(strPlyGroupName);
							sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCategorySpecifics);
							sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCharacteristic);
							sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(strCharacteristicSpecifics);
							
							if(mapCharInfo.containsKey(pgApolloConstants.KEY_REPORT_TYPE))
							{
								sReportType = (String)mapCharInfo.get(pgApolloConstants.KEY_REPORT_TYPE);
								sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sReportType);
							}
							
							sUniqueKey = sbKeyBuilder.toString();				

							mapCharInfo.put(pgApolloConstants.KEY_UNIQUEKEY, sUniqueKey);

							if(setInActiveUniqueParameterKey.contains(strUniqueParameterKey))
							{
								mapCharInfo.put(pgApolloConstants.KEY_ISACTIVE, pgApolloConstants.STR_FALSE_FLAG_CAPS);
								mlInActivePerformanceChar.add(mapCharInfo);
							}
							else
							{
								mlPerformanceChar.add(mapCharInfo);
							}
						}
						
					}
					
				}
				
			}
		}
		
		mapOutput.put(pgApolloConstants.KEY_INACTIVE_CHARS, mlInActivePerformanceChar);		
		mapOutput.put(pgApolloConstants.KEY_ACTIVE_CHARS, mlPerformanceChar);		

		return mapOutput;
	}


	/**
	 * Method to update Performance Char With actual Valus excluding UOM unit
	 * @param keysFromMap
	 * @param dfnumberFormat
	 * @param slAttributeMapping
	 * @param mapAttribute
	 * @return
	 */
	public Map updatePerformanceCharWithActualValues(Set<String> keysFromMap, DecimalFormat dfnumberFormat, StringList slAttributeMapping, Map mapAttribute) 
	{
		String sParameterValue;
		Double dNumber;
		for(String strKey : keysFromMap) 
		{
			sParameterValue = (String)mapAttribute.get(strKey);
			if(slAttributeMapping.contains(strKey))
			{
				sParameterValue = pgApolloCommonUtil.extractValueFromParameter(sParameterValue);
				if(UIUtil.isNotNullAndNotEmpty(sParameterValue) && NumberUtils.isNumber(sParameterValue))
				{
					dNumber = Double.parseDouble(sParameterValue);
					sParameterValue = dfnumberFormat.format(dNumber);							
				}	
				mapAttribute.put(strKey, sParameterValue);
			}						  
		}
		
		return mapAttribute;
	}
     
	
	/**
	 * Method for Manual Evaluate Criteria
	 * @param request
	 * @param strObjectId
	 * @return
	 * @throws Exception
	 */
	@Path("/evaluatecriteriaWithSync")
	@POST
	@Produces({"application/json", "application/ds-json"})
	public Response evaluatecriteriaWithSync(@Context HttpServletRequest request, @FormParam("objectId") String strObjectId)
			throws Exception {
		HashMap mapOutput = new HashMap();
		String sMessage = pgApolloConstants.STR_ERROR_CHARACTERISTICSERROR;
		new StringBuilder();
		matrix.db.Context context = null;
		//Get the user context
		if (Framework.isLoggedIn(request)) {
			context = Framework.getContext(request.getSession(false));
		}
		if(null != context )
		{
			loggerWS.debug( "Context User -->{}", context.getUser());
		}
		loggerWS.debug( " =======================================================================" );
		loggerWS.debug("Path : custosync/ebom/evaluatecriteriaWithSync");
		loggerWS.debug("Method: GenerateEBOMService : evaluatecriteriaWithSync");

		try 
		{
			String sMode = pgApolloConstants.STR_MODE_UPDATE_CHAR;

			String[] args = null;
			Map programMap = new HashMap();
			programMap.put("objectId", strObjectId);
			programMap.put(pgApolloConstants.STR_MODE,sMode);
			programMap.put(pgApolloConstants.KEY_MODIFYCONTROLCHECK, true);
			args = JPO.packArgs(programMap);

			Map outputMap = initiateBackgroundJobExecutionForUpdateCharacteristics(context, args);

			boolean isBackGroundJobIntiated = (boolean)outputMap.get(pgApolloConstants.STR_STATUS_JOB_INITIATED);	 			
			StringList slSuccessMessage = (StringList)outputMap.get(pgApolloConstants.STR_SUCCESS);
			StringList slErrorMessage = (StringList)outputMap.get(pgApolloConstants.STR_ERROR);

			loggerWS.debug( "isBackGroundJobIntiated : {}", isBackGroundJobIntiated);
			loggerWS.debug( "slSuccessMessage : {}", slSuccessMessage);
			loggerWS.debug( "slErrorMessage : {}", slErrorMessage);

			
			if(!slErrorMessage.isEmpty())
			{
				sMessage = StringUtil.join(slErrorMessage, pgApolloConstants.CONSTANT_STRING_NEWLINE);
			}
			else if(!slSuccessMessage.isEmpty())
			{
				sMessage = StringUtil.join(slSuccessMessage, pgApolloConstants.CONSTANT_STRING_NEWLINE);
			}
			mapOutput.put(pgApolloConstants.KEY_MESSAGE, sMessage);		
			loggerWS.debug( "sMessage : {}", sMessage);

		} 
		catch (Exception exception) 
		{	 			
			sMessage = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(exception.getLocalizedMessage()).toString();
			mapOutput.put(pgApolloConstants.KEY_MESSAGE, sMessage);
			loggerWS.debug( "Error in evaluatecriteriaWithSync Job : {} ",  exception.getLocalizedMessage());
			loggerWS.error(exception.getMessage(), exception);
		}
		loggerWS.debug( " ======================================================================= {}", mapOutput );
		loggerWS.debug( "Characteristics are successfully processed. ");
		return Response.ok(CharacteristicMasterUtil.transformToJSON(mapOutput)).build();
	}

	/**
	 * Method to initiate background job for Performance Characteristics
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Map initiateBackgroundJobExecutionForUpdateCharacteristics(matrix.db.Context context, String[] args) throws Exception 
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics >> args = "+args);
		Map mapOutMap = new HashMap();
		String sHistory;
		boolean isBackgroundJobRunning = false;
		mapOutMap.put(pgApolloConstants.STR_STATUS_JOB_INITIATED, false);
		String sError;
		boolean bError = false;
		String sMessageControlInCATIA;
		String sMessageNoModifyAccess;
		String sIsVPLMControlled = DomainConstants.EMPTY_STRING;
		StringList slErrorMessage = new StringList();
		StringList slSuccessMessage = new StringList();
		String sMessage;
		String sUpdateInProgressErrorMessage;

		try 
		{
			HashMap programMap = (HashMap)JPO.unpackArgs(args);
			String sPartObjID= (String) programMap.get("objectId");
			String sMode= (String) programMap.get(pgApolloConstants.STR_MODE);
			boolean bIsModifyControlCheckRequired= (boolean) programMap.get(pgApolloConstants.KEY_MODIFYCONTROLCHECK);
			DomainObject domPartObj = DomainObject.newInstance(context, sPartObjID);							
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - programMap = "+programMap);

			String sModelIdSelectable = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_PART_SPECIFICATION).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.TYPE_VPMREFERENCE).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED).toString();

			StringList slSelectables = new StringList();
			slSelectables.add(DomainConstants.SELECT_ID);
			slSelectables.add(DomainConstants.SELECT_NAME);
			slSelectables.add(DomainConstants.SELECT_REVISION);
			slSelectables.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			slSelectables.add(DomainConstants.SELECT_DESCRIPTION);
			slSelectables.add(DomainConstants.SELECT_TYPE);
			slSelectables.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);		

			if(bIsModifyControlCheckRequired)
			{
				slSelectables.add(sModelIdSelectable);
			}	    		

			Map mapAPPInfo = domPartObj.getInfo(context, slSelectables);
			String sAPPName = (String)mapAPPInfo.get(DomainConstants.SELECT_NAME);
			String sAPPRevision = (String)mapAPPInfo.get(DomainConstants.SELECT_REVISION);		
			String sCharactersticsUpdateStatus = (String)mapAPPInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);

			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - mapAPPInfo = "+mapAPPInfo);			
			
			if(pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS.equals(sCharactersticsUpdateStatus))
			{					
				sUpdateInProgressErrorMessage  = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_CHARACTERISTICSUPDATESTATUS_ISRUNNING).toString();
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - sUpdateInProgressErrorMessage = "+sUpdateInProgressErrorMessage);

				slErrorMessage.add(sUpdateInProgressErrorMessage);
				bError = true;
			}

			if(bIsModifyControlCheckRequired)
			{
				sIsVPLMControlled = (String)mapAPPInfo.get(sModelIdSelectable);
				boolean bModifyAccess = FrameworkUtil.hasAccess(context, domPartObj , "modify");	
				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - bModifyAccess = "+bModifyAccess);

				if(!bModifyAccess)
				{
					sMessageNoModifyAccess = MessageUtil.getMessage(context, null, "emxCPN.UpdateCharacteristics.BackgroundJob.Message.ModifyCheck",new String[] {sAPPName, sAPPRevision}, null, MessageUtil.getLocale(context), pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME);
					slErrorMessage.add(sMessageNoModifyAccess);
					bError = true;
				}
				if(!pgApolloConstants.STR_FALSE_FLAG_CAPS.equalsIgnoreCase(sIsVPLMControlled))
				{
					sMessageControlInCATIA = MessageUtil.getMessage(context, null, "emxCPN.UpdateCharacteristics.BackgroundJob.Message.ControlInCATIA",new String[] {sAPPName, sAPPRevision}, null, MessageUtil.getLocale(context), pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME);
					slErrorMessage.add(sMessageControlInCATIA);
					bError = true;
				}	
			}   					

			if(!bError)
			{
				//Check if previous Background Job operation is still in progress
				String sJobTitle = new StringBuilder(sMode).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(sAPPName).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(sAPPRevision).toString();
				String sIsBackgroundJobRunning = checkRunningBackGroundJobConnected(context, sPartObjID, sJobTitle, sMode);
				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - "+sMode+": Previous Background Job running = " + isBackgroundJobRunning);

				if(pgApolloConstants.STR_FALSE_FLAG.equalsIgnoreCase(sIsBackgroundJobRunning))
				{
					sHistory = new StringBuilder(sMode).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.EBOM_SYNC_HISTORY_STARTS).toString() ;
					addCustomHistoryOnSync(context,sPartObjID, sHistory);
				
					context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - "+sMode+": sJobTitle  = " + sJobTitle);

					Job bgJob = new Job();
					bgJob.setTitle(sJobTitle);
					bgJob.setNotifyOwner("No");
					bgJob.create(context);		

					StringList slObjectSelect = new StringList(2);
					slObjectSelect.addElement(DomainConstants.SELECT_ID);
					slObjectSelect.addElement(DomainConstants.SELECT_NAME);

					Map mapJobInfo = bgJob.getInfo(context, slObjectSelect);
					String sJobId = (String)mapJobInfo.get(DomainConstants.SELECT_ID);
					String sJobName = (String)mapJobInfo.get(DomainConstants.SELECT_NAME);		

					//Initiated background job
					Map mapParamArgs = new HashMap();
					mapParamArgs.put("JobInfo", mapJobInfo);
					mapParamArgs.put("APPInfo", mapAPPInfo);
					mapParamArgs.put("mode", sMode);

					context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - "+sMode+" mapParamArgs to BackgroundJob : " + mapParamArgs);
					BackgroundProcess backgroundProcess = new BackgroundProcess();
					backgroundProcess.submitJob(context, "com.png.apollo.sync.ebom.GenerateEBOMService", "backgroundJobExecutionForUpdateCharacteristics", JPO.packArgsRemote(mapParamArgs), sJobId );

					mapOutMap.put(pgApolloConstants.STR_BACKGROUND_JOB_NAME, sJobName);

					//Connect Background Job to APP by Pending Job connection
					bgJob.addFromObject(context, new RelationshipType(pgApolloConstants.RELATIONSHIP_PENDING_JOB), sPartObjID);				

					sMessage = pgApolloConstants.STR_SUCCESS_BACKGROUND_JOB_INITIATED.replaceFirst("<JOB_NAME>", sJobName);
					slSuccessMessage.add(sMessage);
					mapOutMap.put(pgApolloConstants.STR_STATUS_JOB_INITIATED, true);

				}
				else
				{
					slErrorMessage.add(sIsBackgroundJobRunning);
				}
			}					

		} 
		catch (Exception e) 
		{
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			slErrorMessage.add(sError);
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics - Error in initiateBackgroundJobExecutionForUpdateCharacteristics : "+ e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage(), e);
		}	 
		mapOutMap.put(pgApolloConstants.STR_SUCCESS, slSuccessMessage);
		mapOutMap.put(pgApolloConstants.STR_ERROR, slErrorMessage);
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : initiateBackgroundJobExecutionForUpdateCharacteristics <<"+mapOutMap);
		return mapOutMap;
	}


	/**
	 * Background Job method for Update Characteristics
	 * @param context
	 * @param args
	 * @return
	 */
	public void backgroundJobExecutionForUpdateCharacteristics(matrix.db.Context context, String[] args) throws Exception
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics >> args = "+args);
		String sPartObjName= DomainConstants.EMPTY_STRING; 
		String sPartObjRevision= DomainConstants.EMPTY_STRING; 
		String sPartObjID = DomainConstants.EMPTY_STRING; 
		StringList slResponseMessage = new StringList();
		StringList slError;
		String sHistory;
		boolean bError = false;
		String sError;
		Map mapAPPInputInfo = new HashMap();
		Map mapJobInputInfo = new HashMap();
		String sMode = DomainConstants.EMPTY_STRING;
		Map mapParamArgs = JPO.unpackArgs(args);

		mapAPPInputInfo = (Map)mapParamArgs.get("APPInfo");
		mapJobInputInfo = (Map)mapParamArgs.get("JobInfo");
		sMode = (String)mapParamArgs.get("mode");

		sPartObjID = (String)mapAPPInputInfo.get(DomainConstants.SELECT_ID);
		sPartObjName = (String)mapAPPInputInfo.get(DomainConstants.SELECT_NAME); 
		sPartObjRevision = (String)mapAPPInputInfo.get(DomainConstants.SELECT_REVISION);		
		String sMessageDesignParamNotPublished;
		DomainObject domPartObj = DomainObject.newInstance(context, sPartObjID);	
		boolean isUpdateStatusInProgress = false;

		try 
		{			
								

			Map mapProduct = readPerformanceCharsFromDesignParameterFile(context, sPartObjID);				
			boolean bIsDesignParamPresent = (boolean)mapProduct.get(pgApolloConstants.STR_ISDESIGNPARAMPRESENT);
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics - bIsDesignParamPresent = "+bIsDesignParamPresent);

			if(!bIsDesignParamPresent)
			{
				sMessageDesignParamNotPublished = MessageUtil.getMessage(context, null, "emxCPN.UpdateCharacteristics.BackgroundJob.Message.DesignParamNotPublished",new String[] {sPartObjName, sPartObjRevision}, null, MessageUtil.getLocale(context), pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME);
				slResponseMessage.add(sMessageDesignParamNotPublished);
				bError = true;
			}
			else
			{

				if(mapProduct.containsKey(pgApolloConstants.STR_ERROR))
				{
					slError = (StringList)mapProduct.get(pgApolloConstants.STR_ERROR);
					slResponseMessage.addAll(slError);
					bError = true;
					context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics - slError = "+slError);
				}
				else
				{
					StringList slPlyGroup = new StringList();
					if(mapProduct.containsKey(pgApolloConstants.KEY_CHARPLYGROUPLIST))
					{
						slPlyGroup = (StringList)mapProduct.get(pgApolloConstants.KEY_CHARPLYGROUPLIST);
					}
					mapProduct.put(pgApolloConstants.KEY_UNIQUEPLYGROUPLIST, slPlyGroup);
					
					domPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS);
					isUpdateStatusInProgress = true;

					// EVALUATE CRITERIA STARTS		
					String[] sArgs = new String[]{sPartObjID};
					JPO.invoke(context, "pgDSMLayeredProductSyncUtil", sArgs, "evaluateCriteria", sArgs);								
					// EVALUATE CRITERIA ENDS 	

					String sPreviousAPPId = DomainConstants.EMPTY_STRING;
					boolean bPreviousAPPRevExists = false;
					BusinessObject boPreviousRevision= domPartObj.getPreviousRevision(context);
					if(null!=boPreviousRevision && boPreviousRevision.exists(context))
					{
						bPreviousAPPRevExists = true;
						sPreviousAPPId = boPreviousRevision.getObjectId(context);
					}
					mapProduct.put(pgApolloConstants.KEY_PREVIOUSOBJECTID, sPreviousAPPId);
					
					Map returnMapChar = processAndUpdateCharacteristics(context,mapProduct,sPartObjID, domPartObj, bPreviousAPPRevExists, sMode);
					String sReturnMsg = (String)returnMapChar.get(pgApolloConstants.KEY_MESSAGE);
					context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics - sReturnMsg = "+sReturnMsg);

					if(pgApolloConstants.STR_SUCCESS.equalsIgnoreCase(sReturnMsg))
					{
						validateAndSetPartCharacteristicsStatusCompleted(context, domPartObj);
						isUpdateStatusInProgress = false;
						sHistory = new StringBuilder(sMode).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.STR_SUCCESS_CHARACTERISTIC).toString(); 
						addCustomHistoryOnSync(context,domPartObj.getObjectId(), sHistory);								
						slResponseMessage.addElement(pgApolloConstants.STR_SUCCESS_CHARACTERISTIC);
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics - Characteristics Updated Successfully >>"+sReturnMsg);
					}							
					if(sReturnMsg.contains(pgApolloConstants.STR_ERROR))
					{
						domPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
						isUpdateStatusInProgress = false;
						slResponseMessage.addElement(sReturnMsg);
						bError = true;
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics - Error during Characteristics Update >> "+sReturnMsg);
					}		
				}						
			}


		}
		catch (Exception e) 
		{
			bError = true;
			domPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
			isUpdateStatusInProgress = false;
			sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
			slResponseMessage.add(sError);				
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics - Error in backgroundJobExecutionForUpdateCharacteristics catch Job : "+ e.getLocalizedMessage());
			loggerApolloTrace.error(e.getMessage(), e);
			
		}
		finally
		{					
			if(isUpdateStatusInProgress) {
				domPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
			}
			sendBackgroundJobNotification(context, mapAPPInputInfo, mapJobInputInfo, slResponseMessage, bError, sMode);
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics  After Notification");
		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : backgroundJobExecutionForUpdateCharacteristics <<");
	}


	/**
	 * Method to send Background Job notification
	 * @param context
	 * @param mapAPPInputInfo
	 * @param mapJobInputInfo
	 * @param slResponseMessage
	 * @param bError
	 * @param sMode 
	 * @throws Exception
	 */
	public void sendBackgroundJobNotification(matrix.db.Context context, Map mapAPPInputInfo, Map mapJobInputInfo, StringList slResponseMessage, boolean bError, String sMode) throws Exception 
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : sendBackgroundJobNotification >> mapAPPInputInfo = "+mapAPPInputInfo+", mapJobInputInfo = "+mapJobInputInfo+", slResponseMessage = "+slResponseMessage+" , bError = "+bError+", sMode = "+sMode);

		String sPartObjID = (String)mapAPPInputInfo.get(DomainConstants.SELECT_ID);
		String sJobId = (String)mapJobInputInfo.get(DomainConstants.SELECT_ID);

		String sPartObjName = (String)mapAPPInputInfo.get(DomainConstants.SELECT_NAME); 
		String sPartObjRevision = (String)mapAPPInputInfo.get(DomainConstants.SELECT_REVISION); 
		String sPartObjTitle = (String)mapAPPInputInfo.get(DomainConstants.SELECT_ATTRIBUTE_TITLE); 
		String sPartObjDescription = (String)mapAPPInputInfo.get(DomainConstants.SELECT_DESCRIPTION); 
		String sPartObjectType = (String)mapAPPInputInfo.get(DomainConstants.SELECT_TYPE); 
		sPartObjectType = EnoviaResourceBundle.getAdminI18NString(context, "Type", sPartObjectType, "en");
		
		Locale locObj = context.getLocale();
        Locale localeMessage = MessageUtil.getLocale(context);

		boolean isJobAbortedOrDeleted = false;
		if(UIUtil.isNotNullAndNotEmpty(sJobId))
		{
			isJobAbortedOrDeleted = isJobAbortedOrDeleted (context, sJobId);				
		}
		String sEmailSubject;
		//Email Message
		String sEnerprisePart = new StringBuilder(sPartObjName).append(pgApolloConstants.CONSTANT_STRING_DOT).append(sPartObjRevision).toString();
		//Get Base URL
		String sBaseURL = JPO.invoke(context, "emxMailUtil", null, "getBaseURL", new String[0], String.class);
		sEnerprisePart = new StringBuilder("<a href=").append(sBaseURL).append("?objectId=").append(sPartObjID).append(">").append(sEnerprisePart).append("</a>").toString();

		String sMessage;

		String sProcessingStatus;
		String sCommentOrError;
		String sCommentOrErrorMessage = DomainConstants.EMPTY_STRING;
		StringBuilder sbEmailMessage = new StringBuilder();
		if(bError)
		{
			if(isJobAbortedOrDeleted)
			{
				slResponseMessage.add(EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, locObj, "emxCPN."+sMode+".BackgroundJob.FailureMail.AbortComment"));
			}
			sEmailSubject = MessageUtil.getMessage(context, null, "emxCPN."+sMode+".BackgroundJob.FailureMail.Subject",new String[] {sPartObjectType, sPartObjName, sPartObjRevision}, null, localeMessage, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME);
			sbEmailMessage.append(MessageUtil.getMessage(context, null, "emxCPN."+sMode+".BackgroundJob.FailureMail.Message",new String[] {sPartObjectType, sPartObjName, sPartObjRevision, sPartObjTitle, sPartObjDescription}, null, localeMessage, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME));		
			sProcessingStatus = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Failure");
			sCommentOrError = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.ErrorMessage");
		}
		else
		{
			sEmailSubject = MessageUtil.getMessage(context, null, "emxCPN."+sMode+".BackgroundJob.SuccessMail.Subject",new String[] {sPartObjectType, sPartObjName, sPartObjRevision}, null, localeMessage, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME);
			sbEmailMessage.append(MessageUtil.getMessage(context, null, "emxCPN."+sMode+".BackgroundJob.SuccessMail.Message",new String[] {sPartObjectType, sPartObjName, sPartObjRevision, sPartObjTitle, sPartObjDescription}, null, localeMessage, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME));
			sProcessingStatus = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Success");
			sCommentOrError = EnoviaResourceBundle.getProperty(context, pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME, locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Comment");
		}	
		if(!slResponseMessage.isEmpty())
		{
			sMessage = StringUtil.join(slResponseMessage, "</br>");
			sMessage = sMessage.replace(pgApolloConstants.CONSTANT_STRING_CARET, "</br>");
			sCommentOrErrorMessage = new StringBuilder(sMessage).append("</br></br>").toString();
		}
		sbEmailMessage.append("</br></br>");
		sbEmailMessage.append("<style>table#t01 {width:100%; border: 1px solid black; border-collapse: collapse;}table#t01 tr:nth-child(even) { background-color: #eee;}table#t01 tr:nth-child(odd) {background-color:#fff;}table#t01 th {background-color: #006699; color: white; border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;}table#t01 td {border: 1px solid black; border-collapse: collapse; padding: 5px; text-align: left;}</style>");
		sbEmailMessage.append("<table id='t01'><tr><th>");
		sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.EnterprisePart")).append("</th><th>");
		sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Title")).append("</th><th>");
		sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.Description")).append("</th><th>");
		sbEmailMessage.append(EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", locObj, "emxCPN.CollaborateWithAPP.BackgroundJob.Message.CollaborationStatus")).append("</th><th>");
		sbEmailMessage.append(sCommentOrError).append("</th></tr>");
		sbEmailMessage.append("<tr><td>").append(sEnerprisePart);
		sbEmailMessage.append("</td><td>").append(sPartObjTitle);
		sbEmailMessage.append("</td><td>").append(sPartObjDescription);
		sbEmailMessage.append("</td><td>").append(sProcessingStatus);
		sbEmailMessage.append("</td><td>").append(sCommentOrErrorMessage);
		sbEmailMessage.append("</td></tr></table>");	

		// Define message details
		Map mapArgs = new HashMap();
		mapArgs.put("subject", sEmailSubject);
		mapArgs.put("message", sbEmailMessage.toString());
		mapArgs.put("objectId", sPartObjID);

		try 
		{
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : sendBackgroundJobNotification - Before Sending Email - sMode = "+sMode);
			// Send notification to the context user
			JPO.invoke(context, "pgDSMLayeredProductSyncUtil", null, "sendEmailNotification", JPO.packArgs(mapArgs));
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : sendBackgroundJobNotification - After Sending Email - sMode = "+sMode);
		} 
		catch (Exception e) 
		{
			loggerApolloTrace.error(e.getMessage(), e);
		}

		String sHistory = new StringBuilder(sMode).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.EBOM_SYNC_HISTORY_END).toString() ;
		addCustomHistoryOnSync(context,sPartObjID, sHistory);
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : sendBackgroundJobNotification <<");

	}


	
	/**
	 * Method to update Chg on Characteristics
	 * @param context
	 * @param sPartId
	 * @param mapProduct
	 * @param sProcessingMode
	 * @throws Exception
	 */
	public void updateChgOnCharacteristics(matrix.db.Context context, String sPartId, Map mapProduct, String sProcessingMode) throws Exception
	{	
		try
		{
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgOnCharacteristics 1 >>  sPartId - "+sPartId+" mapProduct - "+mapProduct+" sProcessingMode - "+sProcessingMode);
			String sPreviousAPPId = DomainConstants.EMPTY_STRING;		
			if(mapProduct.containsKey(pgApolloConstants.KEY_PREVIOUSOBJECTID))
			{
				sPreviousAPPId = (String)mapProduct.get(pgApolloConstants.KEY_PREVIOUSOBJECTID);		
			}		
			if(UIUtil.isNotNullAndNotEmpty(sPreviousAPPId))
			{
				updateChgOnCharacteristics(context, sPartId, sPreviousAPPId, sProcessingMode);
			}		
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgOnCharacteristics 1 << ");
		} 
		catch (Exception e) 
		{
			String sHistory = new StringBuilder(sProcessingMode).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(" Update Chg on Characteristics : ").append(e.getMessage()).toString() ;
			addCustomHistoryOnSync(context,sPartId, sHistory);
			loggerApolloTrace.error(e.getMessage(), e);
		}
	}	
	
	
	/**
	 * Method to update Chg. on Characteristics
	 * @param context
	 * @param sCurrentAPPId
	 * @param sPreviousAPPId
	 * @param sProcessingMode
	 * @throws Exception
	 */
	public static void updateChgOnCharacteristics(matrix.db.Context context, String sCurrentAPPId, String sPreviousAPPId, String sProcessingMode) throws Exception
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgOnCharacteristics 2 >>  sCurrentAPPId - "+sCurrentAPPId+" sPreviousAPPId - "+sPreviousAPPId+" sProcessingMode - "+sProcessingMode);

		String sChgCharSelectList = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloServices.CollaborateWithAPP.CharacteristicsChgAttributeList");
		StringList slChgCharSelectList = new StringList();
		
		if(UIUtil.isNotNullAndNotEmpty(sChgCharSelectList))
		{
			slChgCharSelectList	= StringUtil.split(sChgCharSelectList, pgApolloConstants.CONSTANT_STRING_PIPE);
		}	
		
		if(UIUtil.isNotNullAndNotEmpty(sCurrentAPPId) && UIUtil.isNotNullAndNotEmpty(sPreviousAPPId))
		{
			loggerSync.debug("{} : Update Chg on Characteristics Started ", sProcessingMode);
			Map mapCurrentAPPCharacteristics = getAPPCharacteristics(context, sCurrentAPPId, slChgCharSelectList, sProcessingMode);			
			Map mapPreviousAPPCharacteristics = getAPPCharacteristics(context, sPreviousAPPId, slChgCharSelectList, sProcessingMode);
		
			updateChgValueOnCharacteristics(context, mapCurrentAPPCharacteristics, mapPreviousAPPCharacteristics);			
			loggerSync.debug("{} : Update Chg on Characteristics Ended ", sProcessingMode);


		}	
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgOnCharacteristics 2 << ");

	}
	
	/**
	 * Method to update Chg. value on Characteristics
	 * @param context
	 * @param mapCurrentAPPCharacteristics
	 * @param mapPreviousAPPCharacteristics
	 * @throws Exception
	 */
	public static void updateChgValueOnCharacteristics(matrix.db.Context context, Map mapCurrentAPPCharacteristics, Map mapPreviousAPPCharacteristics) throws Exception 
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics >> mapCurrentAPPCharacteristics - "+mapCurrentAPPCharacteristics+" mapPreviousAPPCharacteristics - "+mapPreviousAPPCharacteristics);

		if(null!=mapCurrentAPPCharacteristics && !mapCurrentAPPCharacteristics.isEmpty())
		{
			ArrayList<Boolean> listAttributeMatching;
			Iterator<String> itr = mapCurrentAPPCharacteristics.keySet().iterator();
			String skey;

			String sCurrentAPPCharObjectId;
			String sCurrentAPPCharChgExistingValue;
			String sCurrentAPPCharChgNewValue;

			Map mapCurrentAPPCharAttributes;
			Map mapPreviousAPPCharAttributes;
			Iterator<String> itrPreviousAPP;
			String sPreviousAPPCharAttributeValue;
			String sCurrentAPPCharAttributeValue;
			String sAttributeKey;
			Map mpCharAttrUpdate;

			while (itr.hasNext())
			{
				skey = itr.next();				
				mapCurrentAPPCharAttributes = (Map)mapCurrentAPPCharacteristics.get(skey);
				sCurrentAPPCharObjectId = (String)mapCurrentAPPCharAttributes.get(DomainConstants.SELECT_ID);	
				sCurrentAPPCharChgExistingValue = (String)mapCurrentAPPCharAttributes.get(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE);	

				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics - sCurrentAPPCharObjectId = "+sCurrentAPPCharObjectId+" sCurrentAPPCharChgExistingValue = "+sCurrentAPPCharChgExistingValue);
				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics - mapCurrentAPPCharAttributes = "+mapCurrentAPPCharAttributes);

				mpCharAttrUpdate = new HashMap();
				listAttributeMatching = new ArrayList();

				if(UIUtil.isNotNullAndNotEmpty(sCurrentAPPCharObjectId))
				{
					mapCurrentAPPCharAttributes.remove(DomainConstants.SELECT_ID);
					mapCurrentAPPCharAttributes.remove(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE);

					if(!mapPreviousAPPCharacteristics.isEmpty() && mapPreviousAPPCharacteristics.containsKey(skey))
					{
						mapPreviousAPPCharAttributes = (Map)mapPreviousAPPCharacteristics.get(skey);
						
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics - mapPreviousAPPCharAttributes = "+mapPreviousAPPCharAttributes);

						mapPreviousAPPCharAttributes.remove(DomainConstants.SELECT_ID);
						mapPreviousAPPCharAttributes.remove(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE);
						
						itrPreviousAPP = mapPreviousAPPCharAttributes.keySet().iterator();

						while (itrPreviousAPP.hasNext())
						{
							sPreviousAPPCharAttributeValue = DomainConstants.EMPTY_STRING;
							sCurrentAPPCharAttributeValue = DomainConstants.EMPTY_STRING;
							sAttributeKey = itrPreviousAPP.next();
							
							if(mapCurrentAPPCharAttributes.containsKey(sAttributeKey))
							{
								sCurrentAPPCharAttributeValue = (String) mapCurrentAPPCharAttributes.get(sAttributeKey);
							}

							if(mapPreviousAPPCharAttributes.containsKey(sAttributeKey))
							{
								sPreviousAPPCharAttributeValue = (String) mapPreviousAPPCharAttributes.get(sAttributeKey);
							}							
							
							if(NumberUtils.isNumber(sCurrentAPPCharAttributeValue) && NumberUtils.isNumber(sPreviousAPPCharAttributeValue))
							{
								if(pgApolloCommonUtil.isMatch(sCurrentAPPCharAttributeValue, sPreviousAPPCharAttributeValue, false))
								{
									listAttributeMatching.add(true);
								}
								else
								{
									mpCharAttrUpdate.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_C);
									listAttributeMatching.add(false);
									break;
								}
							}
							else if(!sPreviousAPPCharAttributeValue.equals(sCurrentAPPCharAttributeValue))
							{
								mpCharAttrUpdate.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_C);
								listAttributeMatching.add(false);
								break;
							}	
							else if(sPreviousAPPCharAttributeValue.equals(sCurrentAPPCharAttributeValue))
							{
								listAttributeMatching.add(true);
							}	
						}

						if(!listAttributeMatching.isEmpty() && listAttributeMatching.contains(true) && !listAttributeMatching.contains(false))
						{
							mpCharAttrUpdate.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, DomainConstants.EMPTY_STRING);							
						}
					}
					else
					{
						mpCharAttrUpdate.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_CPLUS);
					}
					context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics - mpCharAttrUpdate = "+mpCharAttrUpdate);

					if(!mpCharAttrUpdate.isEmpty())
					{
						sCurrentAPPCharChgNewValue = (String)mpCharAttrUpdate.get(pgApolloConstants.ATTRIBUTE_PGCHANGE);	
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics - sCurrentAPPCharChgNewValue = "+sCurrentAPPCharChgNewValue);

						if((UIUtil.isNullOrEmpty(sCurrentAPPCharChgExistingValue) && UIUtil.isNotNullAndNotEmpty(sCurrentAPPCharChgNewValue)) || (UIUtil.isNullOrEmpty(sCurrentAPPCharChgNewValue) && UIUtil.isNotNullAndNotEmpty(sCurrentAPPCharChgExistingValue)))
						{
							DomainObject domCharObject = DomainObject.newInstance(context, sCurrentAPPCharObjectId);						
							domCharObject.setAttributeValues(context, mpCharAttrUpdate);
							
							context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics - Updated Chg on Char object = "+sCurrentAPPCharObjectId);

						}						
					}

				}

			}

		}
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateChgValueOnCharacteristics << ");

	}


	/**
	 * Method to get APP Characteristics
	 * @param context
	 * @param sAPPId
	 * @param slChgCharSelectList
	 * @param sProcessingMode
	 * @return
	 * @throws Exception
	 */
	public static Map getAPPCharacteristics(matrix.db.Context context, String sAPPId, StringList slChgCharSelectList, String sProcessingMode) throws Exception
	{
		context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPCharacteristics >> sAPPId - "+sAPPId+" slChgCharSelectList - "+slChgCharSelectList+" sProcessingMode - "+sProcessingMode);

		Map mapUniqueCharacteristics = new HashMap();

		String strRelatedTestMethodId = "from["+ pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD +"].to.id";
		String strRelatedTMRDId = "from["+ DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT +"].to.id";

		StringList slCharSelectList = new StringList();
		slCharSelectList.addAll(slChgCharSelectList);
		slCharSelectList.add(DomainConstants.SELECT_ID);
		slCharSelectList.add(DomainConstants.SELECT_ATTRIBUTE_TITLE); 
		slCharSelectList.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);
		slCharSelectList.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
		slCharSelectList.add(pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TYPE);
		slCharSelectList.add(pgApolloConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
		slCharSelectList.add(strRelatedTestMethodId);
		slCharSelectList.add(strRelatedTMRDId);		
		slCharSelectList.add(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE);	
		
		slChgCharSelectList.add(pgApolloConstants.SELECT_ATTRIBUTE_PGCHANGE);
		
		Pattern relPattern = new Pattern(pgApolloConstants.RELATIONSHIP_PARAMETERAGGREGATION);
		Pattern typePattern = new Pattern(pgApolloConstants.TYPE_PLMPARAMETER);

		String sCharCategory;
		String sCharCategorySpecifics;
		String sCharTitle;
		String sCharSpecifics;
		String sCharReportType;
		DomainObject domAPPObject = DomainObject.newInstance(context,sAPPId);

		MapList mlAPPCharacteristics  =  domAPPObject.getRelatedObjects(context,	// Context
				relPattern.getPattern(),                // relationship pattern
				typePattern.getPattern(),               // type pattern
				slCharSelectList,                       // object selects
				new StringList(),                        // relationship selects
				false,                                   // to direction
				true,                                    // from direction
				(short)1,                                // recursion level
				DomainConstants.EMPTY_STRING,            // object where clause
				null,									 // Relationship Where Clause
				0);										 // Limit

		VPLMIntegTraceUtil.trace(context, sProcessingMode+" : Previous APP Connected Characteristics = " + mlAPPCharacteristics);
		context.printTrace(pgApolloConstants.TRACE_LPD, sProcessingMode+" : GenerateEBOMService : getAPPCharacteristics - mlAPPCharacteristics = "+mlAPPCharacteristics);

		Map mapTempCharacteristics = new HashMap();

		Map mapChar;
		String sCharId;
		StringBuilder sbKeyBuilder;
		String sUniqueKey;
		StringList slCharacteristicIds = new StringList();

		if(!mlAPPCharacteristics.isEmpty())
		{
			for(int i=0; i<mlAPPCharacteristics.size(); i++)
			{
				mapChar = (Map)mlAPPCharacteristics.get(i);
				sCharId = (String)mapChar.get(DomainConstants.SELECT_ID);

				slCharacteristicIds.add(sCharId);

				sCharTitle = (String)mapChar.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				sCharCategory = (String)mapChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CHARACTERISTIC_CATEGORY);
				sCharCategorySpecifics = (String)mapChar.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_CATEGORYSPECIFICS);
				sCharSpecifics = (String)mapChar.get(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSPECIFIC);
				sCharReportType = (String)mapChar.get(pgApolloConstants.SELECT_ATTRIBUTE_REPORT_TYPE);

				if(UIUtil.isNullOrEmpty(sCharSpecifics)) {
					sCharSpecifics = DomainConstants.EMPTY_STRING;
				}
				if(UIUtil.isNullOrEmpty(sCharReportType)) {
					sCharReportType = DomainConstants.EMPTY_STRING;
				}

				sbKeyBuilder = new StringBuilder();
				sbKeyBuilder.append(sCharCategory);
				sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharCategorySpecifics);
				sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharTitle);
				sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharSpecifics);
				sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sCharReportType);
				sUniqueKey = sbKeyBuilder.toString();				
				mapChar.put(pgApolloConstants.KEY_UNIQUEKEY, sUniqueKey);	
				
				context.printTrace(pgApolloConstants.TRACE_LPD, sProcessingMode+" : GenerateEBOMService : getAPPCharacteristics - sCharId = "+sCharId+" sUniqueKey "+sUniqueKey);

				mapTempCharacteristics.put(sCharId, mapChar);

			}				

			StringList slENOICharSelects = new StringList();
			slENOICharSelects.add(DomainConstants.SELECT_ID);
			slENOICharSelects.add(DomainConstants.SELECT_TYPE);
			slENOICharSelects.add(DomainConstants.SELECT_NAME);
			slENOICharSelects.add(DomainConstants.SELECT_REVISION);
			slENOICharSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT);
			slENOICharSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT);
			slENOICharSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT);
			slENOICharSelects.add(pgApolloConstants.SELECT_ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT);
			List<ENOICharacteristic> listChar = com.dassault_systemes.enovia.characteristic.impl.CharacteristicServices.getCharacteristicDetails(context, slCharacteristicIds, slENOICharSelects);

			context.printTrace(pgApolloConstants.TRACE_LPD, sProcessingMode+" : GenerateEBOMService : getAPPCharacteristics - listChar = "+listChar);

			Map mapLocalCharMap;
			String sLowerSL;
			String sLowerRoutineLimit;
			String sLowerTarget;
			String sTarget;
			String sUpperTarget;
			String sUpperRoutineLimit;
			String sUpperSL;
			Map mapTemp;
			String sAttributeValue;
			String sAttributeNameSelect;
			StringList slTestMethodId;
			StringList slTMRDId;
			

			
			String sDimension;
			String sDisplayUnit;
			String sDimensionName;

			for(ENOICharacteristic enoChar: listChar)	
			{	
				sCharId =  enoChar.getId(context);
				sLowerSL  = enoChar.getLowerSpecificationLimit(context);
				sLowerRoutineLimit = enoChar.getLowerRoutineReleaseLimit(context);
				sLowerTarget = enoChar.getMinimalValue(context);
				sTarget = enoChar.getNominalValue(context);
				sUpperTarget = enoChar.getMaximalValue(context);
				sUpperRoutineLimit = enoChar.getUpperRoutineReleaseLimit(context);
				sUpperSL = enoChar.getUpperSpecificationLimit(context);		

				mapLocalCharMap = new HashMap();			
				mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERSPECIFICATIONLIMIT, sLowerSL);
				mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSLOWERROUTINERELEASELIMIT, sLowerRoutineLimit);
				mapLocalCharMap.put(pgApolloConstants.STRING_MINVALUE, sLowerTarget);
				mapLocalCharMap.put(pgApolloConstants.STRING_PARAMETERVALUE, sTarget);
				mapLocalCharMap.put(pgApolloConstants.STRING_MAXVALUE, sUpperTarget);
				mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERROUTINERELEASELIMIT, sUpperRoutineLimit);
				mapLocalCharMap.put(pgApolloConstants.ATTRIBUTE_CHARACTERISTICSUPPERSPECIFICATIONLIMIT, sUpperSL);

				mapTemp = (Map)mapTempCharacteristics.get(sCharId);
				sUniqueKey = (String)mapTemp.get(pgApolloConstants.KEY_UNIQUEKEY);
				
				slTestMethodId = new StringList();
				slTMRDId = new StringList();

				if(mapTemp.containsKey(strRelatedTestMethodId))
				{					
					slTestMethodId = pgApolloCommonUtil.getStringListFromObject(mapTemp.get(strRelatedTestMethodId));
					slTestMethodId.sort();
					mapTemp.remove(strRelatedTestMethodId);
				}	
				
				mapLocalCharMap.put(pgApolloConstants.KEY_TEST_METHOD_ID, slTestMethodId.join(pgApolloConstants.CONSTANT_STRING_PIPE));			

				if(mapTemp.containsKey(strRelatedTMRDId))
				{
					slTMRDId = pgApolloCommonUtil.getStringListFromObject(mapTemp.get(strRelatedTMRDId));
					slTMRDId.sort();
					mapTemp.remove(strRelatedTMRDId);
				}
				
				mapLocalCharMap.put(pgApolloConstants.KEY_TMRD_ID, slTMRDId.join(pgApolloConstants.CONSTANT_STRING_PIPE));
				
				for(int j=0; j<slChgCharSelectList.size(); j++)
				{
					sAttributeNameSelect = slChgCharSelectList.get(j);

					if(mapTemp.containsKey(sAttributeNameSelect))
					{
						sAttributeValue = (String)mapTemp.get(sAttributeNameSelect);
						mapLocalCharMap.put(sAttributeNameSelect, sAttributeValue);
					}				
				}	
				
				sDimension = (UIUtil.isNotNullAndNotEmpty(enoChar.getDimension()))?enoChar.getDimension():DomainConstants.EMPTY_STRING ;
				sDisplayUnit = enoChar.getDisplayUnit();
				
				sDimensionName = DomainConstants.EMPTY_STRING;
				
				if(UIUtil.isNotNullAndNotEmpty(sDimension)){
					sDimensionName = ParameterInterfacesServices.getDimensionNLS(context, sDimension);					
					if(UIUtil.isNullOrEmpty(sDimensionName))
					{
						sDimensionName = DomainConstants.EMPTY_STRING;
					}
				}
				
				mapLocalCharMap.put(pgApolloConstants.STR_CHAR_DIMENSION, sDimensionName);
				
				mapLocalCharMap.put(CharacteristicMasterConstants.PARAM_DISPLAY_UNIT, sDisplayUnit);
							
				mapLocalCharMap.put(DomainConstants.SELECT_ID, sCharId);
				context.printTrace(pgApolloConstants.TRACE_LPD, sProcessingMode+" : GenerateEBOMService : getAPPCharacteristics - Final sUniqueKey = "+mapLocalCharMap);

				mapUniqueCharacteristics.put(sUniqueKey, mapLocalCharMap);
			}

		}
		
		context.printTrace(pgApolloConstants.TRACE_LPD, sProcessingMode+" : GenerateEBOMService : getAPPCharacteristics << "+mapUniqueCharacteristics);

		return mapUniqueCharacteristics;	
		
	}
	


	/**
	 * Method to compare Current and Previous APP EBOM and EBOM Substitute Attribute Values
	 * @param mapEBOMOrEBOMSubstitute
	 * @param sCurrentAPPChgCurrentValue
	 * @param slChgAttribute
	 * @param mpPreviousRevisionValues
	 * @return
	 * @throws Exception
	 */
	public Map compareCurrentAndPreviousAttributeValues(Map mapEBOMOrEBOMSubstitute, String sCurrentAPPChgCurrentValue, StringList slChgAttribute, Map mpPreviousRevisionValues) throws Exception {
		
		ArrayList<Boolean> listAttributeMatching = new ArrayList();
		String sCurrentAPPEBOMChgNewValue;
		String attrKey;
		String strPreviousRevisionValue;
		String strCurrentValue;
		Set<String> keys = mapEBOMOrEBOMSubstitute.keySet();
		Iterator<String> iter = keys.iterator();
		while (iter.hasNext()) 
		{
			attrKey = iter.next();
			strPreviousRevisionValue = DomainConstants.EMPTY_STRING;					
			if(slChgAttribute.contains(attrKey))
			{
				strCurrentValue = (String) mapEBOMOrEBOMSubstitute.get(attrKey);
				if(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA.equals(attrKey))
				{
					strCurrentValue = getParameterValue(strCurrentValue);
				}
				if(mpPreviousRevisionValues.containsKey(attrKey))
				{
					strPreviousRevisionValue = (String) mpPreviousRevisionValues.get(attrKey);
					if(pgApolloConstants.ATTRIBUTE_PGLAYEREDPRODUCTAREA.equals(attrKey))
					{
						strPreviousRevisionValue = getParameterValue(strPreviousRevisionValue);
					}
				}
				if(UIUtil.isNullOrEmpty(strCurrentValue) && NumberUtils.isNumber(strPreviousRevisionValue))
				{
					strCurrentValue = "0.0";
				}
				if(NumberUtils.isNumber(strCurrentValue) && NumberUtils.isNumber(strPreviousRevisionValue))
				{
					if(pgApolloCommonUtil.isMatch(strCurrentValue, strPreviousRevisionValue, true))
					{			
						listAttributeMatching.add(true);
					}
					else
					{
						mapEBOMOrEBOMSubstitute.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_C);
						listAttributeMatching.add(false);
						break;
					}
				}						
				else if(!strPreviousRevisionValue.equals(strCurrentValue))
				{
					mapEBOMOrEBOMSubstitute.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, pgApolloConstants.RANGE_VALUE_CHG_C);
					listAttributeMatching.add(false);
					break;
				}
				else if(strPreviousRevisionValue.equals(strCurrentValue))
				{
					listAttributeMatching.add(true);
				}
			}
		}
		
		if(!listAttributeMatching.isEmpty() && listAttributeMatching.contains(true) && !listAttributeMatching.contains(false))
		{
			mapEBOMOrEBOMSubstitute.put(pgApolloConstants.ATTRIBUTE_PGCHANGE, DomainConstants.EMPTY_STRING);
		}
		
		if(mapEBOMOrEBOMSubstitute.containsKey(pgApolloConstants.ATTRIBUTE_PGCHANGE))
		{
			sCurrentAPPEBOMChgNewValue = (String)mapEBOMOrEBOMSubstitute.get(pgApolloConstants.ATTRIBUTE_PGCHANGE);
			if(!((UIUtil.isNullOrEmpty(sCurrentAPPChgCurrentValue) && UIUtil.isNotNullAndNotEmpty(sCurrentAPPEBOMChgNewValue)) || (UIUtil.isNullOrEmpty(sCurrentAPPEBOMChgNewValue) && UIUtil.isNotNullAndNotEmpty(sCurrentAPPChgCurrentValue))))
			{
				mapEBOMOrEBOMSubstitute.remove(pgApolloConstants.ATTRIBUTE_PGCHANGE);
				loggerSync.debug("Setting Change EBOM or EBOM Substitute {} ", sCurrentAPPEBOMChgNewValue);
			}			
		}
		return mapEBOMOrEBOMSubstitute;
	}


		/**
		 * Method to get APP EBOM and EBOM Substitute Info based on Unique Key
		 * @param context
		 * @param sAPPId
		 * @param slEBOMAttribute
		 * @param slEBOMSubstituteAttribute
		 * @param sMode
		 * @return
		 * @throws MatrixException 
		 */
		public static Map getAPPEBOMAndEBOMSubstituteInfo(matrix.db.Context context, String sAPPId, StringList slEBOMAttribute, StringList slEBOMSubstituteAttribute, String sMode) throws MatrixException 
		{
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo >> sAPPId = "+sAPPId+" slEBOMAttribute -"+slEBOMAttribute+" slEBOMSubstituteAttribute -"+slEBOMSubstituteAttribute+" sMode -"+sMode);

			Map mapAPPInfo = new HashMap();
			DomainObject domPreviousObject = DomainObject.newInstance(context,sAPPId);
		
			StringBuilder sbAttributeSelectable;
			String sAttributeSelectable;
		
			
			StringList slPreviousAPPChildSelects = new StringList();
			slPreviousAPPChildSelects.add(DomainConstants.SELECT_ID);
			slPreviousAPPChildSelects.add(DomainConstants.SELECT_NAME);
			slPreviousAPPChildSelects.add(DomainConstants.SELECT_REVISION);
		
			StringList slPreviousEBOMSelectable = new StringList();
			slPreviousEBOMSelectable.add(DomainRelationship.SELECT_ID);
		
			
			boolean bFetchEBOM = false;
			boolean bFetchEBOMSubstitute = false;
			if(DomainConstants.RELATIONSHIP_EBOM.equals(sMode) || pgApolloConstants.STR_ALL.equalsIgnoreCase(sMode))
			{
				bFetchEBOM = true;
				slPreviousEBOMSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
				slPreviousEBOMSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
			}
		
			if(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE.equals(sMode) || pgApolloConstants.STR_ALL.equalsIgnoreCase(sMode))
			{
				bFetchEBOMSubstitute = true;
				slPreviousEBOMSelectable.add("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id");
			}	
		
			StringList slEBOMChgAttributeSelectList = new StringList();
		
			if(!slEBOMAttribute.isEmpty())
			{
				for(String sAttribute : slEBOMAttribute)
				{
					sbAttributeSelectable = new StringBuilder();
					sAttributeSelectable = sbAttributeSelectable.append("attribute[").append(sAttribute).append("]").toString();						
					slPreviousEBOMSelectable.add(sAttributeSelectable);
					slEBOMChgAttributeSelectList.add(sAttributeSelectable);
				}
			}
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - slPreviousEBOMSelectable = "+slPreviousEBOMSelectable);
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - slEBOMChgAttributeSelectList = "+slEBOMChgAttributeSelectList);

			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - bFetchEBOM = "+bFetchEBOM);
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - bFetchEBOMSubstitute = "+bFetchEBOMSubstitute);

			MapList mlPreviousAPPRelatedMaterial = domPreviousObject.getRelatedObjects(context,						         // Context
					DomainConstants.RELATIONSHIP_EBOM,       // relationship pattern
					DomainConstants.QUERY_WILDCARD,          // type pattern
					slPreviousAPPChildSelects,               // object selects
					slPreviousEBOMSelectable,                // relationship selects
					false,                                   // to direction
					true,                                    // from direction
					(short)1,                                // recursion level
					DomainConstants.EMPTY_STRING,            // object where clause
					null,									 // Relationship Where Clause
					0);										 // Limit
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - mlPreviousAPPRelatedMaterial = "+mlPreviousAPPRelatedMaterial);
			
			Map mapEBOM;
			Map mapLocalEBOM;
			StringList slEBOMSubstituteRelIds;
			StringList slAllEBOMSubstituteRelIds = new StringList();
			StringBuilder sbKeyBuilder;
			String sPlyGroupName;
			String sPlyName;
			String sUniqueKey;
			String sToObjectId;
			String sAttributeValue;
			String sAttributeName;
			String sAttributeNameSelect;
			String sEBOMRelId;
			String sEBOMSubstituteRelId;
			Map mapPreviousAPPEBOM = new HashMap();
			Map mapPreviousAPPEBOMSubstitute = new HashMap();
			String sEBOMObjectName;
		
			if(!mlPreviousAPPRelatedMaterial.isEmpty())
			{
				for(int i=0; i<mlPreviousAPPRelatedMaterial.size(); i++)
				{
					mapEBOM = (Map)mlPreviousAPPRelatedMaterial.get(i);
		
					if(bFetchEBOM)
					{
						sEBOMRelId = (String)mapEBOM.get(DomainRelationship.SELECT_ID);				
						sPlyGroupName = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
						sPlyName = (String)mapEBOM.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);				
						sEBOMObjectName =  (String)mapEBOM.get(DomainConstants.SELECT_NAME);		
		
						sbKeyBuilder = new StringBuilder();
						sbKeyBuilder.append(sPlyGroupName);
						sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sPlyName);						
						sUniqueKey = sbKeyBuilder.toString();	
						mapLocalEBOM = new HashMap();
						mapLocalEBOM.put(DomainRelationship.SELECT_ID, sEBOMRelId);
						mapLocalEBOM.put(DomainConstants.SELECT_NAME, sEBOMObjectName);

						for(int j=0; j<slEBOMAttribute.size(); j++)
						{
							sAttributeNameSelect = slEBOMChgAttributeSelectList.get(j);
							if(mapEBOM.containsKey(sAttributeNameSelect))
							{
								sAttributeValue = (String)mapEBOM.get(sAttributeNameSelect);
								sAttributeName = slEBOMAttribute.get(j);
								mapLocalEBOM.put(sAttributeName, sAttributeValue);
							}
						}
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - sUniqueKey = "+mapLocalEBOM);
						mapPreviousAPPEBOM.put(sUniqueKey, mapLocalEBOM);	
					}	
		
					if(bFetchEBOMSubstitute)
					{
						slEBOMSubstituteRelIds =  pgApolloCommonUtil.getStringListFromObject(mapEBOM.get("frommid["+EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE+"].id"));
						if(!slEBOMSubstituteRelIds.isEmpty())
						{
							slAllEBOMSubstituteRelIds.addAll(slEBOMSubstituteRelIds);
						}
					}
		
				}
			}		
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - slAllEBOMSubstituteRelIds = "+slAllEBOMSubstituteRelIds);

			if(null != slAllEBOMSubstituteRelIds && !slAllEBOMSubstituteRelIds.isEmpty())
			{
				StringList slPreviousEBOMSubstituteSelectable = new StringList();
				slPreviousEBOMSubstituteSelectable.add(DomainRelationship.SELECT_ID);
				slPreviousEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);
				slPreviousEBOMSubstituteSelectable.add(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
				slPreviousEBOMSubstituteSelectable.add(DomainConstants.SELECT_TO_ID);
		
				StringList slEBOMSubstituteChgAttributeSelectList = new StringList();
		
				if(!slEBOMSubstituteAttribute.isEmpty())
				{
					for(String sAttribute : slEBOMSubstituteAttribute)
					{
						sbAttributeSelectable = new StringBuilder();
						sAttributeSelectable = sbAttributeSelectable.append("attribute[").append(sAttribute).append("]").toString();						
						slPreviousEBOMSubstituteSelectable.add(sAttributeSelectable);
						slEBOMSubstituteChgAttributeSelectList.add(sAttributeSelectable);
					}
				}			
				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - slPreviousEBOMSubstituteSelectable = "+slPreviousEBOMSubstituteSelectable);
				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - slEBOMSubstituteChgAttributeSelectList = "+slEBOMSubstituteChgAttributeSelectList);

				String[] substituteRelArray = slAllEBOMSubstituteRelIds.toArray(new String[slAllEBOMSubstituteRelIds.size()]);
		
				MapList mlSubstituteInfoList = DomainRelationship.getInfo(context, substituteRelArray, slPreviousEBOMSubstituteSelectable);	
								
				context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - mlSubstituteInfoList = "+mlSubstituteInfoList);

				Map mapEBOMSubstitute;
				Map mapLocalEBOMSubstitute;
		
				if(!mlSubstituteInfoList.isEmpty())
				{
					for(int i=0; i<mlSubstituteInfoList.size(); i++)
					{
						mapEBOMSubstitute = (Map)mlSubstituteInfoList.get(i);
		
						sPlyGroupName = (String)mapEBOMSubstitute.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYGROUPNAME);
						sPlyName = (String)mapEBOMSubstitute.get(pgApolloConstants.SELECT_ATTRIBUTE_PLYNAME);				
						sToObjectId = (String)mapEBOMSubstitute.get(DomainConstants.SELECT_TO_ID);				
						sEBOMSubstituteRelId = (String)mapEBOMSubstitute.get(DomainRelationship.SELECT_ID);				
		
						sbKeyBuilder = new StringBuilder();
						sbKeyBuilder.append(sPlyGroupName);
						sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sPlyName);	
						sbKeyBuilder.append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sToObjectId);						
						sUniqueKey = sbKeyBuilder.toString();	
						mapLocalEBOMSubstitute = new HashMap();
						mapLocalEBOMSubstitute.put(DomainRelationship.SELECT_ID, sEBOMSubstituteRelId);
		
						for(int j=0; j<slEBOMSubstituteAttribute.size(); j++)
						{
							sAttributeNameSelect = slEBOMSubstituteChgAttributeSelectList.get(j);
							if(mapEBOMSubstitute.containsKey(sAttributeNameSelect))
							{
								sAttributeValue = (String)mapEBOMSubstitute.get(sAttributeNameSelect);
								sAttributeName = slEBOMSubstituteAttribute.get(j);
								mapLocalEBOMSubstitute.put(sAttributeName, sAttributeValue);
							}
						}
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo - Substitute sUniqueKey = "+mapLocalEBOMSubstitute);

						mapPreviousAPPEBOMSubstitute.put(sUniqueKey, mapLocalEBOMSubstitute);											
					}
				}	
		
			}
			mapAPPInfo.put(DomainConstants.RELATIONSHIP_EBOM, mapPreviousAPPEBOM);
			mapAPPInfo.put(EngineeringConstants.RELATIONSHIP_EBOM_SUBSTITUTE, mapPreviousAPPEBOMSubstitute);
			
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : getAPPEBOMAndEBOMSubstituteInfo << "+mapAPPInfo);

			return mapAPPInfo;
		}
		
		
		/**
		/**
		 * Method to update Characteristics for Parts
		 * @param context
		 * @param sPartObjID
		 * @param mapProduct
		 * @param sMode
		 * @return
		 * @throws FrameworkException
		 */
		public Map updateCharacteristics(matrix.db.Context context, String sPartObjID, Map mapProduct, String sMode) throws FrameworkException 
		{
			Map mapReturn = new HashMap();			
			StringList slErrorMessage = new StringList();
			StringList slError = new StringList();
			String sHistory;
			String sError;		
			DomainObject domPartObj = null;
			
			try
			{
				if(UIUtil.isNotNullAndNotEmpty(sPartObjID))
				{
					domPartObj = DomainObject.newInstance( context, sPartObjID);
					if(mapProduct.containsKey(pgApolloConstants.STR_ERROR))
					{
						slError = (StringList)mapProduct.get(pgApolloConstants.STR_ERROR);
						slErrorMessage.addAll(slError);
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateCharacteristics - slError = "+slError);
					}
					else
					{
						StringList slPlyGroup = new StringList();
						if(mapProduct.containsKey(pgApolloConstants.KEY_CHARPLYGROUPLIST))
						{
							slPlyGroup = (StringList)mapProduct.get(pgApolloConstants.KEY_CHARPLYGROUPLIST);
						}
						mapProduct.put(pgApolloConstants.KEY_UNIQUEPLYGROUPLIST, slPlyGroup);
						
						domPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS);

						// EVALUATE CRITERIA STARTS		
						String[] sArgs = new String[]{sPartObjID};
						JPO.invoke(context, "pgDSMLayeredProductSyncUtil", sArgs, "evaluateCriteria", sArgs);								
						// EVALUATE CRITERIA ENDS 	

						String sPreviousAPPId = DomainConstants.EMPTY_STRING;
						boolean bPreviousAPPRevExists = false;
						BusinessObject boPreviousRevision= domPartObj.getPreviousRevision(context);
						if(null!=boPreviousRevision && boPreviousRevision.exists(context))
						{
							bPreviousAPPRevExists = true;
							sPreviousAPPId = boPreviousRevision.getObjectId(context);
						}
						
						mapProduct.put(pgApolloConstants.KEY_PREVIOUSOBJECTID, sPreviousAPPId);
						
						Map returnMapChar = processAndUpdateCharacteristics(context,mapProduct,sPartObjID, domPartObj, bPreviousAPPRevExists, sMode);
						String sReturnMsg = (String)returnMapChar.get(pgApolloConstants.KEY_MESSAGE);
						StringList slErrorList = (StringList)returnMapChar.get(pgApolloConstants.STR_ERROR);
						context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateCharacteristics - sReturnMsg = "+sReturnMsg);

						if(pgApolloConstants.STR_SUCCESS.equalsIgnoreCase(sReturnMsg))
						{
							validateAndSetPartCharacteristicsStatusCompleted(context, domPartObj);
							sHistory = new StringBuilder(sMode).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.STR_SUCCESS_CHARACTERISTIC).toString(); 
							addCustomHistoryOnSync(context,domPartObj.getObjectId(), sHistory);								
							mapReturn.put(pgApolloConstants.STR_SUCCESS, pgApolloConstants.STR_SUCCESS_CHARACTERISTIC);
							context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateCharacteristics - Characteristics Updated Successfully >>"+sReturnMsg);
						}							
						if(sReturnMsg.contains(pgApolloConstants.STR_ERROR))
						{
							domPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
							if(null != slErrorList)
							{
								slErrorMessage.addAll(slErrorList);
							}
							context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : updateCharacteristics - Error during Characteristics Update >> "+sReturnMsg);
						}		
					}	
					
				}
				else
				{
					slErrorMessage.addElement(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
				}
				
			}
			catch (Exception e) 
			{
				sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
				try 
				{
					if(null != domPartObj)
					{
						domPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
					}
				} 
				catch (Exception e1)
				{
					sError = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e1.getLocalizedMessage()).toString();
				}
				slErrorMessage.addElement(sError);
				loggerApolloTrace.error(e.getMessage(), e);
			}
			
			if(!slErrorMessage.isEmpty())
			{
				mapReturn.put(pgApolloConstants.STR_ERROR, slErrorMessage);
			}						
			return mapReturn;
		}

		
		
		/**
		 * Method to validate status and Characteristics Update 
		 * @param context
		 * @param dPartObj
		 * @throws MatrixException 
		 */
		public void validateAndSetPartCharacteristicsStatusCompleted(matrix.db.Context context, DomainObject dPartObj)throws MatrixException 
		{
			String sCharUpdateStatus = dPartObj.getInfo(context, pgApolloConstants.SELECT_ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS);	
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : validateAndSetPartCharacteristicsStatusCompleted - sCharUpdateStatus >> "+sCharUpdateStatus);
			if(pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_INPROGRESS.equals(sCharUpdateStatus))
			{
				dPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_COMPLETED);
			}
			else if(pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING.equals(sCharUpdateStatus))
			{
				dPartObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_PGLPDPERFCHARACTERISTICSUPDATESTATUS, pgApolloConstants.RANGE_PGLPDPERFCHARACTERISTICSUPDATESTATUS_PENDING);
			}
			context.printTrace(pgApolloConstants.TRACE_LPD, "GenerateEBOMService : validateAndSetPartCharacteristicsStatusCompleted - sCharUpdateStatus << ");
		}


		/**
		 * Method to collaborate with Physical - Transfer control back to CATIA and demote all objects to In Work
		 * @param context
		 * @param sPhysicalProductId
		 * @return 
		 * @throws Exception 
		 */
		public Map collaborateWithPhysicalAndDemoteObjects(matrix.db.Context context, String sPhysicalProductId) throws Exception 
		{
			Map mapPhysicalProduct = new HashMap();
			String sError = DomainConstants.EMPTY_STRING;
			StringBuilder sbErrorMessage;
			
			boolean bUseUserAgent = false;
			boolean isContextPushed = false;
			
			try 
			{
				if(UIUtil.isNotNullAndNotEmpty(sPhysicalProductId))
				{
					DomainObject domVPMRefObj = DomainObject.newInstance(context, sPhysicalProductId);				
					String sCurrent = domVPMRefObj.getInfo(context, DomainConstants.SELECT_CURRENT);
					
					if(UIUtil.isNotNullAndNotEmpty(sCurrent) && (pgApolloConstants.STATE_IN_WORK.equalsIgnoreCase(sCurrent) || pgApolloConstants.STATE_WAITAPP.equalsIgnoreCase(sCurrent)))
					{
						if(pgApolloConstants.STATE_WAITAPP.equalsIgnoreCase(sCurrent))
						{
							bUseUserAgent = true;
						}
						
						if(bUseUserAgent)
						{
							//Context user won't always have access to transfer control. So push context is needed here.
							ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, null, context.getVault().getName());
							isContextPushed = true;
						}						
						
						domVPMRefObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_ISVPLMCONTROLLED, "TRUE");
						
						if(bUseUserAgent)
						{
							ContextUtil.popContext(context);
							isContextPushed = false;
						}
						
						StringList busSelects = new StringList();
						busSelects.add(DomainConstants.SELECT_ID);
						busSelects.add(DomainConstants.SELECT_CURRENT);
						
						StringBuilder sbObjWhere = new StringBuilder();
						sbObjWhere.append(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append("'").append(pgApolloConstants.STATE_WAITAPP).append("'");
						
						MapList mlConnected3DShapeDrawings = domVPMRefObj.getRelatedObjects(context,	//context
																		pgApolloConstants.RELATIONSHIP_VPMRepInstance,// relationship pattern
																		DomainConstants.QUERY_WILDCARD,	// type pattern
																		busSelects,	// object selects
																		null,	// relationship selects
																		false,// to direction
																		true,// from direction
																		(short) 1,// recursion level
																		sbObjWhere.toString(),// object where clause
																		null,	// relationship where clause
																		0);// objects Limit			
						
						demoteVPMReferenceAndAssociated3DShapeDrawing(context, sPhysicalProductId, mlConnected3DShapeDrawings);
					}					
				}
					
			}
			catch (Exception e)
			{
				loggerApolloTrace.error(e.getMessage(), e);
				sbErrorMessage = new StringBuilder();
				sbErrorMessage.append(pgApolloConstants.STR_ERROR);
				sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_COLON);
				sbErrorMessage.append(pgApolloConstants.STR_ERROR_COLLABORATE_WITH_PHYSICAL);
				sbErrorMessage.append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage());
				sError = sbErrorMessage.toString();
			}
			finally
			{
				if(bUseUserAgent && isContextPushed)
				{
					ContextUtil.popContext(context);
				}
			}
			mapPhysicalProduct.put(pgApolloConstants.STR_ERROR, sError);
			return mapPhysicalProduct;
		}
		
		
		/**
		 * Method to demote VPMRef and associated 3DShape and Drawings to In Work state
		 * @param context
		 * @param strVPMReferenceObjId
		 * @throws Exception
		 */
		public void demoteVPMReferenceAndAssociated3DShapeDrawing (matrix.db.Context context, String sPhysicalProductId, MapList mlConnected3DShapeDrawings) throws MatrixException
		{
			try
			{				
				if(UIUtil.isNotNullAndNotEmpty(sPhysicalProductId))
				{
					DomainObject domPhysicalProductAndInstance  = DomainObject.newInstance(context, sPhysicalProductId);
					String strVPMRefCurrent = domPhysicalProductAndInstance.getInfo(context, DomainConstants.SELECT_CURRENT);
					
					if(pgApolloConstants.STATE_WAITAPP.equalsIgnoreCase(strVPMRefCurrent)) 
					{
						SignatureList slSignature = domPhysicalProductAndInstance.getSignatures(context,strVPMRefCurrent,pgApolloConstants.STATE_IN_WORK);													
						for(Signature objSignature : slSignature)
						{
							domPhysicalProductAndInstance.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);    	    								
						}
						SignatureList slSignatureLocal = domPhysicalProductAndInstance.getSignatures(context,pgApolloConstants.STATE_WAITAPP,pgApolloConstants.STATE_SHARED);													
						for(Signature objSignature : slSignatureLocal)
						{
							if(objSignature.isSigned())
							{
								domPhysicalProductAndInstance.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
							}
						}
						
						domPhysicalProductAndInstance.setState(context, pgApolloConstants.STATE_IN_WORK);
					}
					
					if(null!=mlConnected3DShapeDrawings && !mlConnected3DShapeDrawings.isEmpty())
					{
						Map map3DShapeDrawingInfo;
						String s3DShapeDrawingId;
						for(Object obj : mlConnected3DShapeDrawings)
						{
							map3DShapeDrawingInfo = (Map)obj;
							s3DShapeDrawingId = (String)map3DShapeDrawingInfo.get(DomainConstants.SELECT_ID);
							if(UIUtil.isNotNullAndNotEmpty(s3DShapeDrawingId))
							{
								demoteVPMReferenceAndAssociated3DShapeDrawing(context, s3DShapeDrawingId, new MapList());
							}									
						}

					}	
					
				}
			}
			catch(Exception ex)
			{
				loggerApolloTrace.error(ex.getMessage() ,ex);
				throw ex;
			}			
		}
		/**
		 * Web service to do mini collaboration between Physical Product and Enterprise Part
		 * Format : /resources/custosync/ebom/collaborateAttributes?objectInfo=VPMRefName|VPMRefRevision
		 * @param request
		 * @param sInput
		 * @return
		 * @throws Exception
		 */
		@GET
		@Path("/collaborateAttributes")
		@Produces({MediaType.APPLICATION_JSON})
		
		public Response collaborateAttributes (@javax.ws.rs.core.Context HttpServletRequest request, @QueryParam("objectInfo") String sInput) throws Exception	{
			loggerWS.debug("******************************************************************************");
			loggerWS.debug("Path : custosync/ebom/collaborateAttributes");
			loggerWS.debug("Method: GenerateEBOMService : collaborateAttributes");
			
			matrix.db.Context context = getAuthenticatedContext(request, false);
			
			String sVPMRefObjectId;
			String sAPPObjectId;
			StringBuilder sbReturnMessages = new StringBuilder();

			try {
				if(context == null && Framework.isLoggedIn(request)) {
					context = Framework.getContext(request.getSession(false));
				}
				
				if(null != context)
				{
					loggerWS.debug( " Username = {}" ,context.getUser());
				}
				
				loggerWS.debug("objectInfo : {}", sInput);

				if(UIUtil.isNotNullAndNotEmpty(sInput))
				{
					StringList slObjectInfoList = StringUtil.split(sInput, pgApolloConstants.CONSTANT_STRING_PIPE);
					
					loggerWS.debug("slObjectInfoList : {}", slObjectInfoList);


					if(null != slObjectInfoList && !slObjectInfoList.isEmpty() && slObjectInfoList.size() > 1)
					{
						String sObjectName = slObjectInfoList.get(0);

						String sObjectRevision = slObjectInfoList.get(1);
						
						sVPMRefObjectId = pgApolloCommonUtil.getObjectId(context, pgApolloConstants.TYPE_VPMREFERENCE, sObjectName, sObjectRevision);
						
						loggerWS.debug("sVPMRefObjectId : {}", sVPMRefObjectId);

						if(UIUtil.isNotNullAndNotEmpty(sVPMRefObjectId))
						{
							sAPPObjectId = pgApolloCommonUtil.fetchAPPObjectId(context, sVPMRefObjectId);	
							
							loggerWS.debug("sAPPObjectId  : {}" , sAPPObjectId);
							
							if(UIUtil.isNotNullAndNotEmpty(sAPPObjectId))
							{
								updateEnterprisePartAttributes(context, sAPPObjectId, sVPMRefObjectId);
								sbReturnMessages.append(pgApolloConstants.STR_SUCCESS);
							} 
							else
							{
								sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_APP_FOUND);
							}						
						}
						else
						{
							sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_NO_VPMREF_FOUND);
						}
					}	
					else
					{
						sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
					}
				}
				else
				{
					sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_AUTOMATION_ERROR_INVALID_PARAMETERS);
				}
				
			}
			catch(Exception ex) 
			{
				sbReturnMessages.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage());
				loggerApolloTrace.error(ex.getMessage(), ex);

			}
			
			loggerWS.debug("Final Response : {}" ,sbReturnMessages);
			loggerWS.debug("GenerateEBOMService collaborateAttributes ended-----------------");
			loggerWS.debug("******************************************************************************");
			
			return Response.status(200).entity(sbReturnMessages.toString()).build();
		}
		
		
		
		/**
		 * Method to update enterprise part attributes
		 * @param context
		 * @param sPartId
		 * @param domVPMRefObj
		 * @throws Exception
		 */
		public static void updateEnterprisePartAttributes (matrix.db.Context context, String sPartId, String sVPMRefId) throws Exception{

			StringList slSelectables = new StringList();		
			slSelectables.add(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
			slSelectables.add(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);			
			slSelectables.add(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);

			Map mapAPPNewAttributes = new HashMap();

			try {
				
				if(UIUtil.isNotNullAndNotEmpty(sVPMRefId) && UIUtil.isNotNullAndNotEmpty(sPartId))
				{
					DomainObject domVPMRefObj = DomainObject.newInstance(context, sVPMRefId);

					Map mapProductInfo = domVPMRefObj.getInfo(context, slSelectables);	
					
					loggerWS.debug("mapProductInfo : {}", mapProductInfo);
					
					VPLMIntegTraceUtil.trace(context, "MassCollab: updateEnterprisePartAttributes = mapProductInfo = " + mapProductInfo  );

					if(!mapProductInfo.isEmpty())
					{
						String strTitle = (String)mapProductInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_V_NAME);
						String sDescription = (String)mapProductInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
						
						String sModelType = (String)mapProductInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
						
						mapAPPNewAttributes.put(MultiValueSelects.ATTRIBUTE_TITLE, strTitle);
						mapAPPNewAttributes.put(pgApolloConstants.ATTRIBUTE_V_NAME_ENTERPRISE_PART, strTitle);
					
						mapAPPNewAttributes = validateAndUpdatePartAttributeDetails(mapAPPNewAttributes, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDMODELTYPE, sModelType, false);
					
						DomainObject domPartObject = DomainObject.newInstance(context, sPartId);						

						loggerWS.debug("mapAPPNewAttributes : {} , sDescription : {}", mapAPPNewAttributes, sDescription);

						domPartObject.setAttributeValues(context, mapAPPNewAttributes);
						domPartObject.setDescription(context, sDescription);	
						
						VPLMIntegTraceUtil.trace(context, "MassCollab: updateEnterprisePartAttributes - Update Successful " );

						loggerWS.debug("mapAPPNewAttributes : {}", mapAPPNewAttributes);

					}
				}
				
			}
			catch (Exception ex)
			{
				VPLMIntegTraceUtil.trace(context, "MassCollab: Exception in updateEnterprisePartAttributes : "  + ex.getLocalizedMessage() );
				loggerApolloTrace.error(ex.getMessage(), ex);
				throw ex;	
			}
		}
}
