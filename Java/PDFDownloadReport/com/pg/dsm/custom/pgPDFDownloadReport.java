/*   pgPDFDownloadReport.java - DSM Report Request For PDF Download
     Author:DSM L4 
     Copyright (c) 2018
     All Rights Reserved.
 */
package com.pg.dsm.custom;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import matrix.db.MxMessageSupport;

import org.apache.commons.io.FileUtils;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;
//Added the code for Defect 47035,47370 - Starts
import com.matrixone.apps.domain.util.PersonUtil;
import org.apache.commons.lang3.StringUtils;
//Added the code for Defect 47035,47370 - Starts

public class pgPDFDownloadReport implements DomainConstants {
	
	
	/*
	 * This method is used to generate PDF Views or GenDoc for PDF Download Report 
	 * 
	 */
	private static final String STRINGRESOURCEFILE ="emxCPN";
	private static final String DATE_YMDHS = "yyyy-MM-dd HH:mm:ss";
	private static final String DIRECTORY_ERR = "Could not create directory";		
	private static final String CATALINA_BASE="catalina.base";
	private static final String PATH_SEPARATOR="/";	
	private PrintWriter outLog = null;
	//added hardcoded values to constants 
	private static final String STRFOLDERPATH = "folderPath";
	private static final String STRERROR = "Error";
	private static final String STRDOWNLOAD="Download";
	//added hardcoded values to constants
	//Added code for 2018x.6 Requirement 36719 Generate PDF Report directly Starts
	private static final String REALTIMEPROCESS = "RealTimeProcess";
	//Added code for 2018x.6 Requirement 36719 Generate PDF Report directly Ends
	//Added the code for Defect 47035,47370 - Starts
	private static final String STR_CONSOLIDATEDPACKAGING ="consolidatedpackaging";
	private static final String STR_CONTRACTPACKAGING ="contractpackaging";
	private static final String STR_SUPPLIER ="supplier";
	private static final String STR_WAREHOUSE ="warehouse";
	private static final String STR_PQR ="PQR";
	private static final String STR_DSO ="DSO";
	private static final String STR_GENDOC ="GenDoc";
	private static final String STR_ALLINFO ="allinfo";
	private static final String STR_COMBINEDWITHMASTER ="combinedwithmaster";
	private static final String STR_EMPLOYEE = "Employee";
	private static final String STR_PGIPMWAREHOUSEREADER="pgIPMWarehouseReader";
	private static final String SPECREADER = "SpecReader";
	
	private String strRequestOriginatingSource = "";
	//Added the code for Defect 47035,47370 - Ends
	
	//Code Review Comment Added for 2022 May CW-02 Defect 52194 - Starts
	private static final String ATTRIBUTE = "attribute[";
	private static final String CLOSINGBRACKET = "]";
	private static final String ATTRIBUTE_PGSECURITYEMPLOYEETYPE = PropertyUtil.getSchemaProperty(null,"attribute_pgSecurityEmployeeType");
	private static final String SELECT_ATTRIBUTE_PGSECURITYEMPLOYEETYPE = ATTRIBUTE + ATTRIBUTE_PGSECURITYEMPLOYEETYPE  + CLOSINGBRACKET;
	//Code Review Comment Added for 2022 May CW-02 Defect 52194 - Ends

	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	private static final String STRFROMEMAILID = "strFromEmailId";
	private static final String STRTOEMAILID = "strToEmailId";
	private static final String STRSUBJECT = "strSubject";
	private static final String STRMESSAGEBODY = "strMessageBody";
	private static final String STR_PERSON_PLM_ADMIN = "PLM Admin";
	private static final String STR_ENOVIA = "Enovia";
	private static final String STR_COMMON="common";
	private static final String STR_SYMBOL_BRACKETS="{0}";
	private static final String STR_TEXT_HTML="text/html";
	private static final String TYPE_GLOBALSUBSCRIPTIONCOFIGURATION = "Global Subscription Configuration";
	private static final String CPNSTRINGRESOURCE = "emxCPNStringResource";
	private static final String STR_VAULT_ADMINISTRATION=PropertyUtil.getSchemaProperty(null,"vault_eServiceAdministration");
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	
	public void generatePDFViewsGenDocReport(Context context,String strUserName,String strPartNames,String strChangeActions,String strReportFileName, String strReportObjectId, Map<String,String> mPassValue) throws Exception {
		//System.out.println("Execution in JAR :: pgPDFDownloadReport");
		StringBuilder sbLogFolder = new StringBuilder();
		File fLogFolder = null;
		String strDirectoryNotCreated = DIRECTORY_ERR;
		String strStartTime = null;		
		String sLanguage = context.getSession().getLanguage();
		String strJVM = getJVMInstance();
		//Log file creation
		//Added code for Requirement 2018x.6 36719 Generate PDF Report directly Starts
		String strRealTimeProcess = mPassValue.get(REALTIMEPROCESS);
		String strInternalUserDownloadView = mPassValue.get("InternalUserDownloadView");
		String strPDFDownloadDailyLimit = mPassValue.get("PDFDownloadDailyLimit");
		String configLOGFilePath = DomainConstants.EMPTY_STRING;
		String configFilePath = DomainConstants.EMPTY_STRING;
		if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess)){
			configLOGFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Log.FilePath");
			configFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.PDFDownloadReport.Worksheet.FilePath");
		}
		else{
			configLOGFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.DSMReportCTRLMJob.Log.FilePath");
			configFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.PDFDownloadReportCTRLMJob.Worksheet.FilePath");
		}
		//Added code for Requirement 2018x.6 36719Generate PDF Report directly Ends
		 sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
		 fLogFolder = new File(sbLogFolder.toString());
		 if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
			 throw new IOException(strDirectoryNotCreated + fLogFolder);
		 }	
		 outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"PDFDownloadLog.log",true));
		 outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+" | JVM: "+strJVM+"--------\n");
		 outLog.print("Parts: "+strPartNames+"\n");
		 outLog.print("Change Actions: "+strChangeActions+"\n"); 
		 outLog.print("Report Object Id: "+strReportObjectId+"\n");
		 outLog.flush();  
		String strType = "pgPackingSubassembly,Software Part,pgIntermediateProductPart,pgCustomerUnitPart,pgAssembledProductPart,pgMasterInnerPackUnitPart,pgDeviceProductPart,pgTransportUnitPart,pgAncillaryPackagingMaterialPart,pgMasterFinishedProduct,pgFormulatedProduct,pgInnerPackUnitPart,pgArtwork,pgOnlinePrintingPart,pgRawMaterial,pgMasterRawMaterialPart,pgMasterProductPart,pgMasterPackingMaterial,pgAncillaryRawMaterialPart,pgFabricatedPart,pgMasterPackagingMaterialPart,pgMasterPackagingAssemblyPart,pgMasterRawMaterial,POA,pgConsumerUnitPart,Finished Product Part,Formulation Part,Packaging Material Part,Raw Material,pgSoftwarePart,pgFinishedProduct,pgPackingMaterial,Packaging Assembly Part,pgMasterCustomerUnitPart,pgMasterConsumerUnitPart,pgPromotionalItemPart,pgFormulatedMaterial,pgSupplierInformationSheet,pgTestMethod,pgIllustration,pgMakingInstructions,pgProcessStandard,pgApprovedSupplierList,pgQualitySpecification,pgCommonPerformanceSpecification,pgRawMaterialPlantInstruction,pgConsumerDesignBasis,pgStackingPattern,Test Method Specification,pgPackingInstructions,pgStandardOperatingProcedure,ArtiosCAD Component,pgAuthorizedConfigurationStandard,pgAuthorizedTemporarySpecification,Formula Technical Specification,pgLaboratoryIndexSpecification,pgCompetitiveProductPart";
		StringList slIndividualCAName = new StringList();
		StringList slIndividualPartNames = new StringList();
		StringList slSelect = new StringList();	
		StringList slDuplicates = new StringList();
		slSelect.add(DomainConstants.SELECT_ID);	
		slSelect.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);	
		slSelect.add(DomainConstants.SELECT_CURRENT);
		slSelect.add(DomainConstants.SELECT_NAME);	
		slSelect.add(DomainConstants.SELECT_TYPE);
		slSelect.add(DomainConstants.SELECT_REVISION);
		slSelect.add("last.id");
		String strCAObjectId = DomainConstants.EMPTY_STRING;
		String strPartObjectId = DomainConstants.EMPTY_STRING;
		String strPartType = DomainConstants.EMPTY_STRING;
		String strPartName = DomainConstants.EMPTY_STRING;
		String strPartRev = DomainConstants.EMPTY_STRING;		
		String strCAName = DomainConstants.EMPTY_STRING;
		String strPDFDownloadLimit = DomainConstants.EMPTY_STRING;
		String strNoAccessLogMsg = DomainConstants.EMPTY_STRING;
		int iPDFLimit = 0;
        DomainObject dobjChangeAction = null;
        File fFolderPath = null;
    	BufferedWriter bwZipinFiles = null;
    	try {     		
    		// Error Messages -- Starts
    		String strInvalidPart = "Part is not in Release State or Part not exists";
    		String strMaxLimtExceed = "Maximimum PDF Download limit exceed for today, remaining PDF request(s) will not be proccessed";
    		// Error Messages -- Ends
    		
        	// Mass PDF views download Folder creation-- Starts 	
			File fMassPDFParentFolder = null;
			StringBuffer sbZipFileName = new StringBuffer();
			StringBuffer sbZipFolderName = new StringBuffer();
			StringBuffer sbMassPDFFolder = new StringBuffer();
			StringBuffer strCurrentDirectoryPath = new StringBuffer();
			long lupdateStartTime = System.currentTimeMillis();
			
			Date now = new Date();
		    SimpleDateFormat smpdf = new SimpleDateFormat("MM-dd-yyyy");
		    String strReportExtractedDate = smpdf.format(now);	
		    String strUserName_temp = strUserName.replace(".","-");
			String strFileBaseNameFolder = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(strReportFileName)) {
				strFileBaseNameFolder = strReportFileName+"_"+strReportExtractedDate+"_"+strUserName_temp+"_"+lupdateStartTime;
			} else {
				strFileBaseNameFolder = "PDFDownloadRequest_"+strReportExtractedDate+"_"+strUserName_temp+"_"+lupdateStartTime;
			}
			sbZipFileName.append(strFileBaseNameFolder).append(".zip");
			sbMassPDFFolder.append(configFilePath).append(java.io.File.separator);
			fMassPDFParentFolder = new File(sbMassPDFFolder.toString());
			
			if (!fMassPDFParentFolder.exists() && !fMassPDFParentFolder.mkdirs())  {
				throw new IOException(strDirectoryNotCreated + fMassPDFParentFolder);
			}
			
			strCurrentDirectoryPath.append(sbMassPDFFolder.toString()).append(strFileBaseNameFolder).append(java.io.File.separator);
			fFolderPath = new File((new StringBuffer(strCurrentDirectoryPath.toString()).append(java.io.File.separator)).toString());
			
			if (!fFolderPath.exists() && !fFolderPath.mkdirs()) 
			{
				throw new IOException(strDirectoryNotCreated + fFolderPath);
			}
			// Mass PDF views download Folder creation-- Ends
			
			// Log File Creation -- Starts
			String strLogFileName = new StringBuffer("PDFDownloadRequestLog_").append(lupdateStartTime).append(".txt").toString();
			File fZipInFiles = new File((new StringBuffer(fFolderPath.toString()).append(java.io.File.separator).append(strLogFileName)).toString());
			bwZipinFiles = new BufferedWriter(new FileWriter(fZipInFiles));
			bwZipinFiles.write("Type|Name|Revision|Comment");
			bwZipinFiles.newLine();
			// Log File Creation -- Ends
			
			// Check for PDF Download limit -- Starts			
			if (UIUtil.isNotNullAndNotEmpty(strPDFDownloadDailyLimit)) {
				strPDFDownloadLimit = strPDFDownloadDailyLimit;
			} else {
				strPDFDownloadLimit = "0";
			}
			
			String strPDFUserId = DomainConstants.EMPTY_STRING;
			String strPDFDownloadDailyCount = DomainConstants.EMPTY_STRING;
			Map mPDFFiles = null;
			DomainObject domPDFUser = null;
			String strNewObjectName = "PDFReportRequest_"+strUserName;
			String appName  = FrameworkUtil.getTypeApplicationName(context, "pgDSMReportCount");
			DomainObject createBO = DomainObject.newInstance(context, "pgDSMReportCount", appName);
			BusinessObject bo = new BusinessObject("pgDSMReportCount", strNewObjectName, "", "eService Production");
	    	if(!bo.exists(context)) {	    		
				createBO.createObject(context, "pgDSMReportCount", strNewObjectName,"", "pgDSMReportCount", "eService Production");
				strPDFUserId = createBO.getObjectId(context);
				createBO.setAttributeValue(context, "pgPDFDownloadDailyCount", "0");
			} else {
				strPDFUserId = (String)bo.getObjectId(context);
			}
		   
			if (UIUtil.isNotNullAndNotEmpty(strPDFUserId)) {
				domPDFUser = DomainObject.newInstance(context, strPDFUserId);
				strPDFDownloadDailyCount = (String) domPDFUser.getAttributeValue(context, "pgPDFDownloadDailyCount");
				iPDFLimit = Integer.parseInt(strPDFDownloadDailyCount);
			}
			// Check for PDF Download limit -- Ends
			
		if(UIUtil.isNotNullAndNotEmpty(strPDFDownloadDailyCount) && Integer.parseInt(strPDFDownloadDailyCount) < Integer.parseInt(strPDFDownloadLimit) ) {
		
			//Change Action Logic -- Starts
			slIndividualPartNames = FrameworkUtil.split(strPartNames, ",");
			slIndividualCAName = FrameworkUtil.split(strChangeActions, ",");
			for (Object CAName : slIndividualCAName) {
				strCAName = String.valueOf(CAName).trim();	
				outLog.print("Processing CA: "+strUserName+"|"+strCAName+"\n");
				outLog.flush();
				MapList mlChangeAction = DomainObject.findObjects(context,pgV3Constants.TYPE_CHANGEACTION,strCAName,"*","*",pgV3Constants.VAULT_ESERVICEPRODUCTION,"",false,slSelect);
				if(mlChangeAction.size()>0 && mlChangeAction!=null){
					Iterator itr = mlChangeAction.iterator();
					while(itr.hasNext()){
						Map mp = (Map)itr.next();
						strCAObjectId = (String)mp.get("id");	
						dobjChangeAction = DomainObject.newInstance(context, strCAObjectId);
						
						ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
						MapList mlRelatedParts = getProposedRealizedActivitiesFromCA(context,strCAObjectId);
						ContextUtil.popContext(context);
						Iterator itr2 = mlRelatedParts.iterator();
						while(itr2.hasNext()){
							Map mp2 = (Map)itr2.next();
							strPartObjectId = (String)mp2.get("id");
							
							if(!slDuplicates.contains(strPartObjectId) && UIUtil.isNotNullAndNotEmpty(strPartObjectId)){
				
								if (iPDFLimit < Integer.parseInt(strPDFDownloadLimit)) {
									Map mArgs = new HashMap();
									mArgs.put("PartObjectId", strPartObjectId);
									mArgs.put("PDFDownloadLimit", strPDFDownloadLimit);
									mArgs.put("PDFLimit", iPDFLimit);
									mArgs.put("FolderPath", fFolderPath);
									mArgs.put("bwZipinFiles", bwZipinFiles);
									mArgs.put("InternalUserDownloadView", strInternalUserDownloadView);
									
									String[] args = JPO.packArgs(mArgs);
									mPDFFiles = downloadPDFFile(context, args);
									strPartType = (String) mPDFFiles.get("type");
									strPartName = (String) mPDFFiles.get("name");
									strPartRev = (String) mPDFFiles.get("revision");
									iPDFLimit = (Integer) mPDFFiles.get("PDFLimit");
									strNoAccessLogMsg = (String) mPDFFiles.get("NoAccessLogMsg");
									
									if (UIUtil.isNotNullAndNotEmpty(strNoAccessLogMsg)) {
										bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev).append("|").append(strNoAccessLogMsg).toString());
										bwZipinFiles.newLine();
										bwZipinFiles.flush();
									}
									
								} else {
									bwZipinFiles.write(strMaxLimtExceed);
									bwZipinFiles.newLine();
									bwZipinFiles.flush();// To get data update in log file
								} 
							}
						} 
						slDuplicates.addElement(strPartObjectId);
						bwZipinFiles.flush();
					}
				}
			}
			//Change Action Logic -- Ends
				
			//Part Logic -- Starts
			for (Object PartName : slIndividualPartNames) {
				MapList mlPart = new MapList();
				strPartName = String.valueOf(PartName).trim();	
				outLog.print("Processing Part: "+strUserName+"|"+strPartName+"\n");
				outLog.flush();
				MapList mlPartRelease = DomainObject.findObjects(context,strType,strPartName,"*","*",pgV3Constants.VAULT_ESERVICEPRODUCTION,"(current==Release)",false,slSelect);
				
				//Fetching Highest Release revision --Begin	
				mlPartRelease.sort(DomainConstants.SELECT_REVISION, "descending", "string");
				for (int iRelRevNum = 0; iRelRevNum < mlPartRelease.size(); iRelRevNum++) {
					Map mReleaseHighestRevision =(Map)mlPartRelease.get(iRelRevNum);								
					mlPart.add(mReleaseHighestRevision);
					break;
				}
				//Fetching Highest Release revision --End
				
				if(mlPart.size()>0 && mlPart!=null){
					Iterator itr = mlPart.iterator();
					while(itr.hasNext()){
					Map mp = (Map)itr.next();
					strPartObjectId = (String)mp.get("id");
					
					if(!slDuplicates.contains(strPartObjectId) && UIUtil.isNotNullAndNotEmpty(strPartObjectId)){
						if (iPDFLimit < Integer.parseInt(strPDFDownloadLimit)) {
							Map mArgs = new HashMap();
							mArgs.put("PartObjectId", strPartObjectId);
							mArgs.put("PDFDownloadLimit", strPDFDownloadLimit);
							mArgs.put("PDFLimit", iPDFLimit);
							mArgs.put("FolderPath", fFolderPath);
							mArgs.put("bwZipinFiles", bwZipinFiles);
							mArgs.put("InternalUserDownloadView", strInternalUserDownloadView);
								
							String[] args = JPO.packArgs(mArgs);
							mPDFFiles = downloadPDFFile(context, args);
							strPartType = (String) mPDFFiles.get("type");
							strPartName = (String) mPDFFiles.get("name");
							strPartRev = (String) mPDFFiles.get("revision");
							iPDFLimit = (Integer)mPDFFiles.get("PDFLimit");
							strNoAccessLogMsg = (String) mPDFFiles.get("NoAccessLogMsg");
							
							if (UIUtil.isNotNullAndNotEmpty(strNoAccessLogMsg)) {
								bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev).append("|").append(strNoAccessLogMsg).toString());
								bwZipinFiles.newLine();
								bwZipinFiles.flush();
							}
								
						} else {
							bwZipinFiles.write(strMaxLimtExceed);
							bwZipinFiles.newLine();
							bwZipinFiles.flush();// To get data update in log file
						}
					} 
					slDuplicates.addElement(strPartObjectId);
				}
			} else {
				//Added the code to fix Defect ID : 27588 - "|Part is not in Release State or Part not exists" should NOT be included in the last lines of the log file" :Starts
				if(UIUtil.isNotNullAndNotEmpty(strPartName)) {
					bwZipinFiles.write(new StringBuffer(strPartName).append("|").append(strInvalidPart).toString());
					bwZipinFiles.newLine();
					bwZipinFiles.flush(); // To get data update in log file
				 }
				   //Added the code to fix Defect ID : 27588 - "|Part is not in Release State or Part not exists" should NOT be included in the last lines of the log file" :Ends
				}
			}
			//Part Logic -- Ends
			
			//Updating User PDF Download count
			if (iPDFLimit != Integer.parseInt(strPDFDownloadDailyCount)) {
				domPDFUser.setAttributeValue(context, "pgPDFDownloadDailyCount", String.valueOf(iPDFLimit));
			}
			
    	} else {
    		bwZipinFiles.write(strMaxLimtExceed);
			bwZipinFiles.newLine();
			bwZipinFiles.flush(); // To get data update in log file
    	}
			
		//Zip file creation
		if (fFolderPath.exists()) {
			sbZipFolderName.append(sbMassPDFFolder.toString()).append(java.io.File.separator).append(sbZipFileName);
			zipFiles(sbZipFolderName.toString(), fFolderPath.toString());
		} 
		
		// code to create the object and checking the .zip file in that object
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		createDSMReportObject(context,sbMassPDFFolder.toString(),sbZipFileName.toString(),strReportObjectId,strUserName);
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends

		String strEndTime = null; 
		strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
		outLog.print("Report completed for: "+strUserName+":"+sbZipFileName+ "\n");
		outLog.print("------Time completed: "+strEndTime+"-------\n");	
		outLog.print("------\n");
		outLog.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			bwZipinFiles.close();
			//To delete the folder in which the files got checked out.
			deleteFiles(context,fFolderPath.toString());
		}
			
	}

	/*
	 * This method is used to download PDF file 
	 * 
	 */
	public Map downloadPDFFile(Context context, String args[]) throws Exception {
		
		String TYPE_ARTIOSCADCOMPONENT = PropertyUtil.getSchemaProperty("type_ArtiosCADComponent");
		String TYPE_POA = PropertyUtil.getSchemaProperty("type_POA");
		String strTypepgPOADocument = PropertyUtil.getSchemaProperty("type_pgPOADocument");
		String strTypepgTestMethod = PropertyUtil.getSchemaProperty("type_pgTestMethod");
		String strTypepgFinishedProduct = PropertyUtil.getSchemaProperty("type_pgFinishedProduct");
		String strTypeFinishedProductPart = PropertyUtil.getSchemaProperty("type_FinishedProductPart");
		//added for Req Id 36721 The report shall download technical drawing (.ARD file) when user input MPMP-Starts
		String strTypepgMasterPackagingMaterialPart =PropertyUtil.getSchemaProperty(context,"type_pgMasterPackagingMaterialPart");
		//added for Req Id 36721 The report shall download technical drawing (.ARD file) when user input MPMP-Starts
		HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
		
		String strUserName = context.getUser();
		String strPartObjectId = (String) hmArgs.get("PartObjectId");
		File fFolderPath = (File) hmArgs.get("FolderPath");
		BufferedWriter bwZipinFiles = (BufferedWriter) hmArgs.get("bwZipinFiles");
		int iPDFLimit = (int) hmArgs.get("PDFLimit");
		String strInternalUserDownloadView = "default";
		strInternalUserDownloadView = (String) hmArgs.get("InternalUserDownloadView");

		String strPartType = DomainConstants.EMPTY_STRING;;
		String strPartName = DomainConstants.EMPTY_STRING;;
		String strPartRev = DomainConstants.EMPTY_STRING;;
		String strPDfFileName = DomainConstants.EMPTY_STRING;;
		String mode = DomainConstants.EMPTY_STRING;;
		
		StringList slSelect = new StringList();	
		slSelect.add(DomainConstants.SELECT_NAME);	
		slSelect.add(DomainConstants.SELECT_TYPE);
		slSelect.add(DomainConstants.SELECT_REVISION);
		
		boolean bGenerateGenDoc = false;
		boolean bGeneratePDFView = false;
		boolean bIsNonStructuredType = false;
		boolean bError = false;
    	boolean bIsPOAType = false;
    	boolean bIspgTestMethodType = false;
    	boolean bConsolidatePackagingView = false;
    	boolean bAllInfoView = false;
    	//ALM Defect #28897 Added to check avaibability of each PDF view
    	boolean bSupplierView = false;
    	boolean bContractPackagingView = false;
    	boolean bWarehouseView = false;
    	boolean bGenDocView = false;
    	//END ALM Defect #28897 
		
		Map mGenDoc = null;
		Map mArtiosCAD = null;
		Map mPDFViews = null;
		Map mPDFReturn = new HashMap();
		StringBuffer sbNoAccessLogMsg = new StringBuffer();
		
		//Error Messages -- Starts 
		String strArtiosSuccessMsg = "ArtiosCAD Component File Download Success";
		String strArtiosErrorMsg = "ArtiosCAD Component File Download Error";
		//Modified the code for 2018x.6 MAYCW Defect 42296 - starts
		String strGenDocSuccessMsg = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.PDFDownloadReport.GenDocSuccessMsg");
		//Modified the code for 2018x.6 MAYCW Defect 42296 - Ends
		//Modified the code for 2018x.6 MAYCW Defect 42296 - starts
		String strGenDocErrorMsg = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.PDFDownloadReport.GenDocErrorMsg");
		//Modified the code for 2018x.6 MAYCW Defect 42296 - Ends
		String strAllInfoSuccessMsg = "PDF View Download Success";
		String strAllInfoErrorMsg = "PDF View Download Error";
		String strDSMSecurityErrMsg = "DSM Security check : No Access";
		//Modified the code for 2018x.6 MAYCW Defect 42296 - starts
		String strGenDocErrMsg = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.PDFDownloadReport.GenDocErrMsg");
		//Modified the code for 2018x.6 MAYCW Defect 42296 - Ends
		String strAllInfoErrMsg = "All Info View cannot be downloaded";
		String strConsolidatedPackagingSuccessMsg = "PDF View Download Success - ConsolidatedPackaging";
		String strConsolidatedPackagingErrorMsg = "PDF View Download Error";
		//ALM Defect #28897 Added error message
		String strGeneratePDFErrMsg = "PDF View not available for download";
		String strGenericErrMsg = "View not available for download";
		//END ALM Defect #28897 ---
		//Error Messages -- Ends
		//Added the code for Defect 47035,47370 - Starts
		Map mpPersonData=null;
		//Added the code for Defect 47035,47370 - Ends
			
		//If bAccess is true,  DSM Security check passed
		boolean bAccess = accessCheck(context,strUserName,strPartObjectId);
		boolean bIsEBPUser = isEBP(context, strUserName);	
		boolean bIsSupplierEBPUser = isSupplierEBP(context, strUserName);
		//Common Checks
		//String strProductDataTypes = FrameworkProperties.getProperty("emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
		String strNonStructuredDataTypes= FrameworkProperties.getProperty("emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
		
		DomainObject domPart = DomainObject.newInstance(context, strPartObjectId);
		Map mPartDetails = (Map) domPart.getInfo(context, slSelect);
		strPartType = (String) mPartDetails.get(DomainConstants.SELECT_TYPE);
		strPartName = (String) mPartDetails.get(DomainConstants.SELECT_NAME);
		strPartRev = (String) mPartDetails.get(DomainConstants.SELECT_REVISION);
		
		if(strNonStructuredDataTypes.contains(strPartType) || strTypepgTestMethod.equalsIgnoreCase(strPartType)) {
			bIsNonStructuredType = true;
		}
		
		// If type id ArtiosCAD Component, getting its file directly
		if (TYPE_ARTIOSCADCOMPONENT.equalsIgnoreCase(strPartType)) {
			//Access Checks for whether user has file download access on ArtiosCAD Componet -- Starts
			Map<String,Object> mDownloadArgs = new HashMap<>();
			Map<String,Object> requestMap = new HashMap<>();
			requestMap.put(pgV3Constants.OBJECT_ID, strPartObjectId);
			requestMap.put(pgV3Constants.LANGUAGESTR, "en");
			mDownloadArgs.put("requestMap", requestMap);
			String[] saDownloadArgs = JPO.packArgs(mDownloadArgs);
			
			// Invoking the properties page of ArtiosCAD Componets where download link is displayed, 
			// If Link contains Download, User has Access else no file Download Access
			String sDownloadLink = (String) JPO.invoke(context,"DSCWebFormActionsLink",null,"getHtmlString",saDownloadArgs,String.class);
			
			if (sDownloadLink.contains(STRDOWNLOAD)){
			//Access Checks for whether user has file download access on ArtiosCAD Componet -- Ends
			
				bGenerateGenDoc = false;
				bGeneratePDFView = false;
				Map mArgs = new HashMap();
				mArgs.put(pgV3Constants.OBJECT_ID, strPartObjectId);
				mArgs.put(STRFOLDERPATH, fFolderPath);
				
				String[] saArtiosArgs = JPO.packArgs(mArgs);
				mArtiosCAD = getArtiosCADComponentFile(context,saArtiosArgs);
				bError = (Boolean)mArtiosCAD.get(STRERROR);
				if(!bError) {
					iPDFLimit++;
					bwZipinFiles.write(new StringBuffer(strPartType).append(pgV3Constants.SYMBOL_PIPE).append(strPartName).append(pgV3Constants.SYMBOL_PIPE).append(strPartRev)
							.append(pgV3Constants.SYMBOL_PIPE).append(strArtiosSuccessMsg).toString());
					bwZipinFiles.newLine();
				} else {
				
					bwZipinFiles.write(new StringBuffer(strPartType).append(pgV3Constants.SYMBOL_PIPE).append(strPartName).append(pgV3Constants.SYMBOL_PIPE).append(strPartRev)
							.append(pgV3Constants.SYMBOL_PIPE).append(strArtiosErrorMsg).toString());
					bwZipinFiles.newLine();
				}
				
				bwZipinFiles.flush();
			} else {
				sbNoAccessLogMsg.append(strDSMSecurityErrMsg);
			}
			
		}  else if (bAccess) {
			//Access Check for GenDoc -- Starts
			//POA Access Check added for Defect 26119 -- Starts
			//If POA is connected to type pgPOADocument with rel reference document then bypass the command level access checks
			if (TYPE_POA.equalsIgnoreCase(strPartType)) {
				DomainObject domObj = DomainObject.newInstance(context, strPartObjectId);
				StringList slSelectList = new StringList(8);
				slSelectList.add(DomainConstants.SELECT_ID);
				slSelectList.add(DomainConstants.SELECT_FILE_NAME);
				slSelectList.add(DomainConstants.SELECT_FILE_FORMAT);
				slSelectList.add(DomainConstants.SELECT_FILE_SIZE);
				//Added the Code for Defect Id- 26992,27592,27587 - Starts
			    String sBusWhereExpression = "revision == Rendition";
				MapList mlFiles = domObj.getRelatedObjects(context,
													DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // Rel pattern
													strTypepgPOADocument, 	// Type Pattern
													slSelectList,         	// Bus select 
													null,                	// Rel select
													true,               	// to side
													true,              		// from side
													(short)1,         		// level of extraction
													sBusWhereExpression,    // Object where clause
													null,           		// Rel where clause
													0);            			// Limit
				
				//Added the Code for Defect Id- 26992,27592,27587 - Ends
				if(mlFiles.size() > 0) {
					bIsPOAType = true;
				}
			}
			//POA Access Check added for Defect 26119 -- Ends
			
			
			//Req Id 36721 The report shall download technical drawing (.ARD file) when user input MPMP-Starts
			if(strTypepgMasterPackagingMaterialPart.equalsIgnoreCase(strPartType))
			{
				StringList slObjSelectList = new StringList();
				slObjSelectList.add(DomainConstants.SELECT_ID);
				slObjSelectList.add(DomainConstants.SELECT_FILE_NAME);
				slObjSelectList.add(DomainConstants.SELECT_NAME);
				slObjSelectList.add(DomainConstants.SELECT_FILE_FORMAT);
				slObjSelectList.add(DomainConstants.SELECT_FILE_SIZE);
				slObjSelectList.add(DomainConstants.SELECT_TYPE);
				slObjSelectList.add(DomainConstants.SELECT_REVISION);
								
				Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_PART_SPECIFICATION);
				relPattern.addPattern(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT);
				relPattern.addPattern(pgV3Constants.RELATIONSHIP_PGINHERITEDCADSPECIFICATION);
				String strWhereExp ="("+DomainConstants.SELECT_FILE_FORMAT+"=='ard')" ;
				String strArtiosCADId=DomainConstants.EMPTY_STRING;
				Map<String,String> mArtiosCADDetails = new HashMap<>();
				Map<String,Object> mDownloadArgs = new HashMap<>();
				Map<String,String> requestMap = new HashMap<>();
				Map<String,Object> mArgs=new HashMap<>();
				String[] saArtiosArgs;
				String[] saDownloadArgs ;
				String strArtiosCADType=DomainConstants.EMPTY_STRING;
				String strArtiosCADName=DomainConstants.EMPTY_STRING;
				String strArtiosCADRevision=DomainConstants.EMPTY_STRING;
				String sDownloadLink=DomainConstants.EMPTY_STRING;
				
				MapList mlMPMPFiles = domPart.getRelatedObjects(context,
						relPattern.getPattern(),// Rel pattern
						TYPE_ARTIOSCADCOMPONENT,// Type Pattern
						slObjSelectList,        // Bus select 
						null,                	// Rel select
						false,               	// to side
						true,              		// from side
						(short)1,         		// level of extraction
						strWhereExp,    		// Object where clause
						null,           		// Rel where clause
						0);            			// Limit
						
				if(null != mlMPMPFiles && !mlMPMPFiles.isEmpty()){
					for (int i = 0;i<mlMPMPFiles.size();i++) {
					mArtiosCADDetails=(Map<String,String>) mlMPMPFiles.get(i);
					strArtiosCADId= mArtiosCADDetails.get(DomainConstants.SELECT_ID);
					strArtiosCADType= mArtiosCADDetails.get(DomainConstants.SELECT_TYPE);
					strArtiosCADName= mArtiosCADDetails.get(DomainConstants.SELECT_NAME);
					strArtiosCADRevision= mArtiosCADDetails.get(DomainConstants.SELECT_REVISION);
					//Access Checks for whether user has file download access on ArtiosCAD Componet -- Starts
					requestMap.put(pgV3Constants.OBJECT_ID, strArtiosCADId);
					requestMap.put(pgV3Constants.LANGUAGESTR, "en");
					mDownloadArgs.put("requestMap", requestMap);
					saDownloadArgs= JPO.packArgs(mDownloadArgs);
					
					// Invoking the properties page of ArtiosCAD Componets where download link is displayed, 
					// If Link contains Download, User has Access else no file Download Access
					sDownloadLink =  JPO.invoke(context,"DSCWebFormActionsLink",null,"getHtmlString",saDownloadArgs,String.class);
					
					if (sDownloadLink.contains(STRDOWNLOAD)){
					//Access Checks for whether user has file download access on ArtiosCAD Componet -- Ends
					
						bGenerateGenDoc = false;
						bGeneratePDFView = false;
						mArgs.put(pgV3Constants.OBJECT_ID, strArtiosCADId);
						mArgs.put(STRFOLDERPATH, fFolderPath);
						
						saArtiosArgs= JPO.packArgs(mArgs);
						mArtiosCAD = getArtiosCADComponentFile(context,saArtiosArgs);
						bError = (Boolean)mArtiosCAD.get(STRERROR);
						if(!bError) {
							
							iPDFLimit++;
							bwZipinFiles.write(new StringBuffer(strArtiosCADType).append(pgV3Constants.SYMBOL_PIPE).append(strArtiosCADName).append(pgV3Constants.SYMBOL_PIPE).append(strArtiosCADRevision)
									.append(pgV3Constants.SYMBOL_PIPE).append(strArtiosSuccessMsg).toString());
							bwZipinFiles.newLine();
						} else {
						
							bwZipinFiles.write(new StringBuffer(strArtiosCADType).append(pgV3Constants.SYMBOL_PIPE).append(strArtiosCADName).append(pgV3Constants.SYMBOL_PIPE).append(strArtiosCADRevision)
									.append(pgV3Constants.SYMBOL_PIPE).append(strArtiosErrorMsg).toString());
							bwZipinFiles.newLine();
						}
						
						bwZipinFiles.flush();
					} else {
						sbNoAccessLogMsg.append(strDSMSecurityErrMsg);
					}
					
					}
				}
			}
			//Req Id 36721 The report shall download technical drawing (.ARD file) when user input MPMP-Ends
			
			 if (bIsNonStructuredType) {
					//Added the code for Defect 47035,47370 - Starts
				String strView = "GenDoc";
				String objectId = strPartObjectId;
				boolean bViewAvai = isPDFGenDocPresent(context, objectId,strView);
				//Added the code for Defect 47035,47370 - Ends
				if(bViewAvai && (!context.isAssigned("pgIPMWarehouseReader"))) {				
					bGenerateGenDoc = true;
				} else {
					sbNoAccessLogMsg.append(strGenDocErrMsg);
				}
			 }
			//Access Check for GenDoc -- Ends
			
			//All Info Access Check -- Starts
			bIsEBPUser = isEBP(context, strUserName);
			boolean bIsCMEBPUser = isCMEBP(context, strUserName);
			bIsSupplierEBPUser = isSupplierEBP(context, strUserName);
			boolean bIsAuthorizedToProduce = isAuthorizedToProduce(context, strUserName,strPartObjectId);
			
			//Find which views are available for the Part before determining which must be generated..
			//Supplier View
			Boolean bViewAvaiValue= false;
			//Added the code for Defect 47035,47370 - Starts
			String strView = "supplier";
			StringList slobjUserSelect = new StringList();
			//Code Review Comment Added for 2022 May CW-02 Defect 52194 - Starts
			slobjUserSelect.add(SELECT_ATTRIBUTE_PGSECURITYEMPLOYEETYPE);
			//Code Review Comment Added for 2022 May CW-02 Defect 52194 - Ends
			slobjUserSelect.add(DomainConstants.SELECT_ID);
			//Code Review Comment Added for 2022 May CW-02 Defect 52194 - Starts
			MapList mlPerson = DomainObject.findObjects(context, //context
					DomainConstants.TYPE_PERSON, //type
					strUserName, //name
					"-", //revision
					DomainConstants.QUERY_WILDCARD, //owner 
					pgV3Constants.VAULT_ESERVICEPRODUCTION, //vault
					DomainConstants.EMPTY_STRING, //where clause
					false, //expand type
					slobjUserSelect); //object select
			//Code Review Comment Added for 2022 May CW-02 Defect 52194 - Ends
			int maplistSize = mlPerson.size();
			for(int i=0;i<maplistSize;i++){
				mpPersonData = (Map)mlPerson.get(i);					
			}
			bViewAvaiValue = checkViewAvailability(context, strPartObjectId, strView, mpPersonData);
			//Added the code for Defect 47035,47370 - Ends
			if(bViewAvaiValue && (!context.isAssigned("pgIPMWarehouseReader"))) {
				bSupplierView = true;
			}
			 
			//contractpackaging view
			//Added the code for Defect 47035,47370 - Starts
			strView="contractpackaging";
			bViewAvaiValue = checkViewAvailability(context, strPartObjectId, strView, mpPersonData);
			//Added the code for Defect 47035,47370 - Ends
			if(bViewAvaiValue && (!context.isAssigned("pgIPMWarehouseReader"))) {
				bContractPackagingView = true;
			}
			
			//warehouse view
			//Added the code for Defect 47035,47370 - Starts
			strView="warehouse";
			bViewAvaiValue = checkViewAvailability(context, strPartObjectId, strView, mpPersonData);
			//Added the code for Defect 47035,47370 - Ends
			if(bViewAvaiValue && context.isAssigned("pgIPMWarehouseReader")) {
				bWarehouseView = true;
			}
			
			//GenDoc
			//Added the code for Defect 47035,47370 - Starts
			strView="GenDoc";
			bViewAvaiValue = checkViewAvailability(context, strPartObjectId, strView, mpPersonData);
			//Added the code for Defect 47035,47370 - Ends
			if(bViewAvaiValue && (!context.isAssigned("pgIPMWarehouseReader"))) {
				bGenDocView = true;					
			} 
			
			//END ALM Defect #28897 
						
			//Allinfo View
			//Added the code for Defect 47035,47370 - Starts
			strView="allinfo";
			bViewAvaiValue = checkViewAvailability(context, strPartObjectId, strView, mpPersonData);
			
			if(bViewAvaiValue && (!context.isAssigned("pgIPMWarehouseReader"))) {	
				//Added the code for Defect 47035,47370 - Ends
				bGeneratePDFView = true;
				bAllInfoView = true;
			} 
			//else if (("default").equals(strInternalUserDownloadView) && !bConsolidatePackagingView && !bAllInfoView && !bIsNonStructuredType && !bIsPOAType && !bIspgTestMethodType && !bIsEBPUser && !context.isAssigned("pgIPMWarehouseReader")) {
			//	sbNoAccessLogMsg.append(strAllInfoErrMsg);
			//} 
			
			//Added for Requirement of 2018x.1.1: FPP,FP download Consolidated Packing view if user had access, otherwise download AllInfo View (all users)-Begin
			//Consolidated Packaging
			//Added the code for Defect 47035,47370 - Starts
		if(strPartType.equals(strTypepgFinishedProduct) || strPartType.equals(strTypeFinishedProductPart))
			{
				strView = "consolidatedpackaging";
				bViewAvaiValue = checkViewAvailability(context, strPartObjectId, strView, mpPersonData);
				//Added the code for Defect 47035,47370 - Ends
				if(bViewAvaiValue && (!context.isAssigned("pgIPMWarehouseReader"))) {
					bConsolidatePackagingView = true;
					bGeneratePDFView = false;
				} 
			}
			
		
			
			//ALM Defect #28897 ---
			//Assign mode only if PDF view is available.
			if (bIsEBPUser) {
				if (bConsolidatePackagingView) {
					mode = "consolidatedpackaging";
					bGeneratePDFView = false;
					//System.out.println("consolidatedpackaging view to be generated...");
				} else if (bIsCMEBPUser && bContractPackagingView) {
					mode = "contractpackaging";
					bConsolidatePackagingView = false;
					bGeneratePDFView = true;
					//System.out.println("contractpackaging view to be generated...");
				} else if ((bIsCMEBPUser || bIsSupplierEBPUser) && bSupplierView) {
					mode = "supplier";
					bConsolidatePackagingView = false;
					bGeneratePDFView = true;
					//System.out.println("supplier view to be generated...");
				} else if (bWarehouseView) {
					mode = "warehouse";
					bConsolidatePackagingView = false;
					bGeneratePDFView = true;
					//System.out.println("warehouse view to be generated...");
				} else if (bGenDocView) {
					mode = "GenDoc";
					bConsolidatePackagingView = false;
					bGeneratePDFView = false;
					bIsNonStructuredType = true;
					bGenerateGenDoc = true;
					//System.out.println("GenDoc view to be generated...");					
				}
				outLog.print("EBP User - mode: "+mode+"\n");
				outLog.flush();
			} else {				
				//Req #31276,Allow Internal User to choose which view to download
				if (("default").equalsIgnoreCase(strInternalUserDownloadView)) {
					mode = "allinfo";
				} else {				
					if (("contractpackaging").equalsIgnoreCase(strInternalUserDownloadView)) {
						 if(bContractPackagingView) {
							mode = "contractpackaging";
							bConsolidatePackagingView = false;
							bGeneratePDFView = true;
							bGenerateGenDoc = false;
							//System.out.println("contractpackaging view to be generated...Requested view: " + strInternalUserDownloadView);
						} else {
							 strAllInfoErrorMsg =  strAllInfoErrorMsg +" : Contract Packing view not available.\n";
						}
					}
					if (("supplier").equalsIgnoreCase(strInternalUserDownloadView)) {
						if(bSupplierView) {
							mode = "supplier";
							bConsolidatePackagingView = false;
							bGeneratePDFView = true;
							bGenerateGenDoc = false;
							//System.out.println("supplier view to be generated...Requested view: " + strInternalUserDownloadView);
						} else {
							 strAllInfoErrorMsg = strAllInfoErrorMsg+" : Supplier view not available.\n";
						}				 
					}
					if (("consolidatedpackaging").equalsIgnoreCase(strInternalUserDownloadView)) {
						if(bConsolidatePackagingView) {
							mode = "consolidatedpackaging";
							bConsolidatePackagingView = true;
							bGeneratePDFView = false;
							bGenerateGenDoc = false;
							//System.out.println("consolidatedpackaging view to be generated...Requested view: " + strInternalUserDownloadView);
						} else {
							strAllInfoErrorMsg = strAllInfoErrorMsg+" : Consolidated Packaging view not available.\n";
						}
					}	
				} 
				outLog.print("Internal User - mode: "+mode+"\n");
				outLog.flush();
			}
			if (mode.equals("")) {
				strAllInfoErrorMsg = "Requested: " + strAllInfoErrorMsg;
			}
		} else {
			sbNoAccessLogMsg.append(strDSMSecurityErrMsg);
		}

		//If both GenDoc and all Info View available on part, download only the Generated view view
		if ((bGenerateGenDoc && bGeneratePDFView) || (bGenerateGenDoc && bConsolidatePackagingView)) {
			bGenerateGenDoc = false;
		}
		
		outLog.print("bGenerateGenDoc: " + bGenerateGenDoc );
		outLog.print("bConsolidatePackagingView: " + bConsolidatePackagingView );
		outLog.print("bGeneratePDFView: " + bGeneratePDFView );
		outLog.print("mode: " + mode );
		outLog.print("strAllInfoErrorMsg: " + strAllInfoErrorMsg );
					
		// GenDoc
		if(bIsNonStructuredType && bGenerateGenDoc && !TYPE_ARTIOSCADCOMPONENT.equalsIgnoreCase(strPartType) || bIsPOAType || bIspgTestMethodType) {
			Map mArgs = new HashMap();
			mArgs.put("objectId", strPartObjectId);
			mArgs.put("folderPath", fFolderPath);
			mArgs.put("mode", "GenDoc");
			String[] saGenDoc = JPO.packArgs(mArgs);
			mGenDoc = getGenDocFromPart(context,saGenDoc); // Checkout from pgIPMDocument
			
			bError = (Boolean) mGenDoc.get("Error");
			if(!bError) {
				iPDFLimit++;
				bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
						.append("|").append(strGenDocSuccessMsg).toString());
				bwZipinFiles.newLine();
			} else {
				bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
						.append("|").append(strGenDocErrorMsg).toString());
				bwZipinFiles.newLine();
			}
			// To get data update in log file
			bwZipinFiles.flush();
		}
		
		//Added for Requirement of 2018x.1.1: FPP,FP download Consolidated Packing view if user had access, otherwise download AllInfo View (all users)-Begin
		if(bConsolidatePackagingView && !TYPE_ARTIOSCADCOMPONENT.equalsIgnoreCase(strPartType)) {
			Map mArgs = new HashMap();
			mArgs.put("objectId", strPartObjectId);
			mArgs.put("folderPath", fFolderPath);
			mArgs.put("mode", "consolidatedpackaging");
			String[] saConsolidatedPackaging = JPO.packArgs(mArgs);
			mPDFViews = generatePDFViewsFromPart(context,saConsolidatedPackaging);
			bError = (Boolean) mPDFViews.get("Error");
			if(!bError) {
				iPDFLimit++;
				bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
						.append("|").append(strConsolidatedPackagingSuccessMsg).toString());
				bwZipinFiles.newLine();
			} else {
				
				bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
						.append("|").append(strConsolidatedPackagingErrorMsg).toString());
				bwZipinFiles.newLine();
			}
				
			// To get data update in log file
			bwZipinFiles.flush();
		}
				
		//Generate All Info/Supplier/CM/ PDF View
		if(bGeneratePDFView && !TYPE_ARTIOSCADCOMPONENT.equalsIgnoreCase(strPartType)) {
			if (mode.equals("")) {
				bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
						.append("|").append(strAllInfoErrorMsg).toString());
				bwZipinFiles.newLine();
			} else {
				Map mArgs = new HashMap();
				mArgs.put("objectId", strPartObjectId);
				mArgs.put("folderPath", fFolderPath);
				mArgs.put("mode", mode);
				//System.out.println("mode = " + mode);
				String[] saAllInfo = JPO.packArgs(mArgs);
				mPDFViews = generatePDFViewsFromPart(context,saAllInfo);
				bError = (Boolean) mPDFViews.get("Error");
				String strModeDisplay = "";
				
				if(!bError) {
					if (mode.equals("contractpackaging"))
					{
						strModeDisplay = "Contract Manufacturing View";
					} else if (mode.equals("supplier")) {
						strModeDisplay = "Supplier View";
					} else if (mode.equals("warehouse")) {
						strModeDisplay = "Warehouse View";
					} else if (mode.equals("GenDoc")) {
						strModeDisplay = "GenDoc View";
					} else if (mode.equals("allinfo")) {
						strModeDisplay = "All Information View";
					} else if (mode.equals("consolidatedpackaging")) {
						strModeDisplay = "Consolidated Packaging View";
					} else {
						strModeDisplay = "PDF View";
					}
					iPDFLimit++;
					bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
					.append("|").append(strAllInfoSuccessMsg).toString()+" - "+strModeDisplay);
					bwZipinFiles.newLine();
				} else { 
					bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
							.append("|").append(strAllInfoErrorMsg).toString());
					bwZipinFiles.newLine();
				}
			}
			// To get data update in log file
			bwZipinFiles.flush();
		}
		
		if (!bGeneratePDFView && !bConsolidatePackagingView && !bGenerateGenDoc) {
			//No view is available for download
			//System.out.println("No view is available for download - write to log: " + strAllInfoErrorMsg);
			bwZipinFiles.write(new StringBuffer(strPartType).append("|").append(strPartName).append("|").append(strPartRev)
					.append("|").append(strAllInfoErrorMsg).toString());
			bwZipinFiles.newLine();
			bwZipinFiles.flush();
		}
		
		mPDFReturn.put("type", strPartType);
		mPDFReturn.put("name", strPartName);
		mPDFReturn.put("revision", strPartRev);
		mPDFReturn.put("PDFLimit", iPDFLimit);
		mPDFReturn.put("NoAccessLogMsg", sbNoAccessLogMsg.toString());
		
		return mPDFReturn;
	}
		
	/**
 	* Method to get connected Parts from ChangeManagement
 	* -	If Change Action has both Proposed Activity and Realized Activity Items
	*		Download PDF for Realized Items as per requirement 
	* -	If Change Action has only Affected Items 
	*		Download PDF for Proposed Items as per requirement
	*
 	* @param context
 	* @param strCAObjectId - CA object id & its selectable
 	* @return MapList
 	*/
	public MapList getProposedRealizedActivitiesFromCA(Context context, String strCAObjectId) throws Exception{
		
		MapList mlProposedRealizedParts = new MapList();
		MapList mlPAParts = new ChangeAction(strCAObjectId).getAffectedItems(context);
		MapList mlRAParts = new ChangeAction(strCAObjectId).getAllRealizedChanges(context);
		StringList slObject = new StringList(5);
		slObject.add(DomainConstants.SELECT_CURRENT);
		
		String strPartId = "";
		String strPartState = "";
		Map mPartSelects = null;
		DomainObject domPart = null;
		
		// If Change Action has only Proposed Activities and No Realized Activities, Download PDF for Proposed Activities
		if (mlPAParts.size()>0 && mlRAParts.size()==0) {
			Iterator itrPAParts = mlPAParts.iterator();						
			while (itrPAParts.hasNext()){
				Map mpPart = (Map)itrPAParts.next();	
				strPartId  = (String)mpPart.get("id");
				if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
					domPart = DomainObject.newInstance(context,strPartId);
					mPartSelects = domPart.getInfo(context, slObject);
					strPartState = (String) mPartSelects.get(DomainConstants.SELECT_CURRENT);
					//if ("Release".equalsIgnoreCase(strPartState)) { - //Commented for Requirement of 2018x.1.1: If Change Action Name is input, download the PDF of the exact part which is connected to CA
						mlProposedRealizedParts.add(mPartSelects);
					//}
				}
			}
		}
		
		//	If Change Action has both Proposed Activities and Realized Activities, Download PDF for Realized Activities
		if (mlRAParts.size()>0) {
			Iterator itrRAParts = mlRAParts.iterator();						
			while (itrRAParts.hasNext()){
				Map mpPart = (Map)itrRAParts.next();	
				strPartId  = (String)mpPart.get("id");
				if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
					domPart = DomainObject.newInstance(context,strPartId);
					mPartSelects = domPart.getInfo(context, slObject);
					strPartState = (String) mPartSelects.get(DomainConstants.SELECT_CURRENT);
					//if ("Release".equalsIgnoreCase(strPartState)) { - //Commented for Requirement of 2018x.1.1: If Change Action Name is input, download the PDF of the exact part which is connected to CA
						mlProposedRealizedParts.add(mPartSelects);
					//}
				}
			}
		}
		return mlProposedRealizedParts;
	}

	/**
     * this method is to generate the PDF File for the manual entry from the user side
     * 
     * @param args
     * @return void
     * @throws Exception
     */
	public void generatePDFViewsGenDocReportEntry(Context context, String args[]) throws Exception {
	    HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
	    String strUserName = (String) hmArgs.get("UserName");
	    String strPartNames = (String) hmArgs.get("GCAS");
	    String strChangeActions = (String) hmArgs.get("ChangeAction");
	    String strReportFileName = (String) hmArgs.get("ReportFileName");
	    String strReportObjectId = (String) hmArgs.get("ReportObjectId");
	    String strPDFDownloadDailyLimit= (String) hmArgs.get("PDFDownloadDailyLimit");
	    String strInternalUserDownloadView = (String) hmArgs.get("InternalUserDownloadView");
	    //Added code for Requirement 2018x.6 36719,36720 Generate PDF Report by scheduled CTRLM Job Starts
	    String strRealTimeProcess = (String) hmArgs.get(REALTIMEPROCESS);
	  //Added the code for Defect 47035,47370 - Starts
	    strRequestOriginatingSource = (String) hmArgs.get("OriginatingSource");
	  //Added the code for Defect 47035,47370 - Ends
	    Map<String,String> mPassValue = new HashMap<>();
	    mPassValue.put(REALTIMEPROCESS,strRealTimeProcess);
	    mPassValue.put("InternalUserDownloadView",strInternalUserDownloadView);
	    mPassValue.put("PDFDownloadDailyLimit",strPDFDownloadDailyLimit);
	    //Added code for Requirement 2018x.6 36719,36720 Generate PDF Report by scheduled CTRLM Job Ends
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames) || UIUtil.isNotNullAndNotEmpty(strChangeActions))){
	    	DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
			doObj.promote(context);
	    	//code to write the input entered by the user in the text file or by manual entry into the input file : Starts
	    	//logUserInputData(context,strUserName,strPartNames,strChangeActions);
	    	//code to write the input entered by the user in the text file or by manual entry into the input file : Ends
	    	//Added strRealTimeProcess Parameter for 2018x.6 Requirement 36719,36720 Generate PDF Report by scheduled CTRLM Job 
			generatePDFViewsGenDocReport(context,strUserName,strPartNames,strChangeActions,strReportFileName,strReportObjectId,mPassValue);
	       }
	   }
 
	 /**
	 * this method is to generate the Exel Sheet for the input .txt file which will be browsed by the user
	 * 
	 * @param args
	 * @return void
	 * @throws Exception
	 */
	/*public void generateFCRForInputFile(Context context, String args[]) throws Exception {
	    FileReader fileReaderMQLGeneratedInputFile = null;
	    BufferedReader buffReaderForMQLGeneratedInputFile = null;
	    String strPartOrCALabelFromInputFile = DomainConstants.EMPTY_STRING;
	    StringList slCompleteRow = new StringList();
	    String strChangeActions = DomainConstants.EMPTY_STRING;
		String strUserName = DomainConstants.EMPTY_STRING;
		String strPartNames = DomainConstants.EMPTY_STRING;
	    String line = DomainConstants.EMPTY_STRING;
		HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
	    java.io.File strFilePath = (java.io.File)hmArgs.get("DSMReportFile");
	    String strReportFileName = (String) hmArgs.get("ReportFileName");
	    String strReportObjectId = (String) hmArgs.get("ReportObjectId");
	    String strPDFDownloadDailyLimit= (String) hmArgs.get("PDFDownloadDailyLimit");
	    String strInternalUserDownloadView = (String) hmArgs.get("InternalUserDownloadView");
	    System.out.println("generateFCRForInputFile - strInternalUserDownloadView: " + strInternalUserDownloadView);
	    fileReaderMQLGeneratedInputFile = new FileReader(strFilePath);
		buffReaderForMQLGeneratedInputFile = new BufferedReader(fileReaderMQLGeneratedInputFile);		
		try {
			
		    strUserName = context.getUser();
		    DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
			String finalVal = "";
			while ((line = buffReaderForMQLGeneratedInputFile.readLine()) != null) {
				//Code modified for InputFile for PDF Download - Start
				/*
				 * if(line != null && line.length()>0){ slCompleteRow =
				 * FrameworkUtil.split(line, "="); strPartOrCALabelFromInputFile =
				 * (String)slCompleteRow.get(0); //Code for Change Action -- Starts
				 * if(strPartOrCALabelFromInputFile.equalsIgnoreCase("ChangeActionName")){
				 * strChangeActions = (String)slCompleteRow.get(1); } //Code for Change Action
				 * -- Ends
				 * 
				 * //Code for Part -- Starts else
				 * if(strPartOrCALabelFromInputFile.equalsIgnoreCase("PartName")){ strPartNames
				 * = (String)slCompleteRow.get(1);
				 * 
				 * } //Code for Part -- Ends }
				 */
				
				/* if ((line != null) && (line.length() > 0)) {
				        if (!line.contains(",")) {
				          finalVal = finalVal + line + ",";
				        } else {
				          finalVal = finalVal + line;
				        }
				      }
			}
		    StringList strFinalList = FrameworkUtil.split(finalVal, ",");
		    finalVal = strFinalList.toString();
		    finalVal = finalVal.replace("[", "").replace("]", "");
		    strPartNames = finalVal;
		    strChangeActions=finalVal;
		  //Code modified for InputFile for PDF Download - End
			if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames) || UIUtil.isNotNullAndNotEmpty(strChangeActions))){
				StringList slGCAS = FrameworkUtil.split(strPartNames, ",");
				StringList slChangeAction = FrameworkUtil.split(strChangeActions, ",");
				//if((slGCAS.size()+slChangeAction.size())<=100)
				//{
				//	generatePDFViewsGenDocReport(context,strUserName,strPartNames,strChangeActions,strReportFileName,strReportObjectId,strPDFDownloadDailyLimit);
				//}
				//else {
				  try { 
					    HashMap hmArgs1 = new HashMap();
						hmArgs1.put("UserName",strUserName);
						hmArgs1.put("GCAS",strPartNames);
						hmArgs1.put("ChangeAction",strChangeActions);
						hmArgs1.put("ReportFileName",strReportFileName);
						hmArgs1.put("ReportObjectId",strReportObjectId);
						hmArgs1.put("PDFDownloadDailyLimit",strPDFDownloadDailyLimit);
						hmArgs1.put("InternalUserDownloadView",strInternalUserDownloadView);
				        BackgroundProcess backgroundProcess = new BackgroundProcess();
				        backgroundProcess.submitJob(context, "com.pg.dsm.custom.pgPDFDownloadReport", "generatePDFViewsGenDocReportEntry", JPO.packArgsRemote(hmArgs1) , (String)null);
				    } catch(Exception ex) {
				        ContextUtil.abortTransaction(context);
				        ex.printStackTrace();
				        throw ex;
				    }
				 //}
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			fileReaderMQLGeneratedInputFile.close();
			buffReaderForMQLGeneratedInputFile.close();
		}
	}
	*/
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
private void createDSMReportObject(Context context,String strReportPath,String strReportName,String strReportObjectId,String strUserName) throws MatrixException {
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	
	try {
			String strContextUserName = context.getUser();
			String strNewObjectName = "PDFDownloadRequest"+"_"+strContextUserName+"_"+String.valueOf(System.currentTimeMillis());
			String strObjectId = strReportObjectId;
			String TYPE_PGDSMREPORT = PropertyUtil.getSchemaProperty("type_pgDSMReport");
			if(strObjectId.equals(""))
			{
				String appName = FrameworkUtil.getTypeApplicationName(context, TYPE_PGDSMREPORT);
				DomainObject createBO = DomainObject.newInstance(context, TYPE_PGDSMREPORT, appName);
				BusinessObject bo = new BusinessObject("type_pgDSMReport", strNewObjectName, "", "eService Production");
				if(!bo.exists(context))
				{
					createBO.createObject(context, "type_pgDSMReport", strNewObjectName,"", "pgDSMReport", "eService Production");
					strObjectId = createBO.getObjectId(context);
				}
			}
				
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				DomainObject doObj = DomainObject.newInstance(context, strObjectId);
		        String sFullPath = strReportPath.concat(strReportName);			      
		         doObj.checkinFile(context, true, true, "", "generic", strReportName, strReportPath);
		         doObj.promote(context);
		         
		         File file = new File(sFullPath);
		         file.delete();
		       //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		         sendEmail(context, strReportName, strUserName);
		       //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
			} else {
			 	throw new MatrixException("Creation of Object Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		 }
	}
	
	/**
	 * This method used to checkout the Gen Doc From Part in to folder
	 */
	public Map getGenDocFromPart(Context context, String[] args) throws Exception {
		 //Added the code for Defect 47035,47370 - Starts
		boolean isContextPushed = false;
		 //Added the code for Defect 47035,47370 - Ends
		HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
		String strPartId = (String) argumemtMap.get("objectId");
		File folder = (File) argumemtMap.get("folderPath");
		
		String strTypepgIPMDocument = PropertyUtil.getSchemaProperty("type_pgIPMDocument");
		//Code-Fix for Defect 26119 (POA PDF download issue) --Start
		String strTypepgPOADocument = PropertyUtil.getSchemaProperty("type_pgPOADocument");
		String TYPE_POA = PropertyUtil.getSchemaProperty("type_POA");
		StringBuffer sbTypePattern = new StringBuffer(strTypepgIPMDocument);
		sbTypePattern.append(",");
		sbTypePattern.append(strTypepgPOADocument);
		//Code-Fix for Defect 26119 (POA PDF download issue) --End
		
		StringList slSelectList = new StringList(8);
		slSelectList.add(DomainConstants.SELECT_ID);
		slSelectList.add(DomainConstants.SELECT_FILE_NAME);
		slSelectList.add(DomainConstants.SELECT_FILE_FORMAT);
		slSelectList.add(DomainConstants.SELECT_FILE_SIZE);
		
		String sDocObjectID = DomainConstants.EMPTY_STRING;
		String sDocObjectFileName = DomainConstants.EMPTY_STRING;
		String sDocObjectFileFormat = DomainConstants.EMPTY_STRING;
		
		Map objFileMap = null;
		Map mReturn = new HashMap();
		Map mGenDocStamp = null;
		DomainObject domObj = null;
		DomainObject domDocObj = null;
		
		boolean bIPMDocFilePresent = false;
		boolean bError = false;
		boolean bStampPDFError = false;
		StringList slObjectSelects = new StringList();
		slObjectSelects.addElement(DomainConstants.SELECT_TYPE);
		slObjectSelects.addElement(DomainConstants.SELECT_NAME);
		slObjectSelects.addElement(DomainConstants.SELECT_REVISION);
		slObjectSelects.addElement(DomainConstants.SELECT_ID);
		//Added the Code for Defect Id- 26992 - Starts
		StringList slFileNameList = new StringList();
		StringList slFileFormatList = new StringList();
		//Added the Code for Defect Id- 26992 - Ends
		try {
			 //Added the code for Defect 47035,47370 - Starts
			if(SPECREADER.equalsIgnoreCase(strRequestOriginatingSource)) {
				/*Code Review Comment  for Defect 52194 Needs Exception for below push context in case of SPECREADER*/
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
			}
			 //Added the code for Defect 47035,47370 - Ends
			domObj = DomainObject.newInstance(context, strPartId);
			mReturn = domObj.getInfo(context,slObjectSelects);
			//Code-Fix for Defect 26119 (POA PDF download issue) --Start
			//Added the Code for Defect Id- 26992,27592,27587 - Starts
			//String strPartType = (String)mReturn.get(DomainConstants.SELECT_TYPE);
			String sBusWhereExpression = "revision == Rendition";
			MapList mlFiles = domObj.getRelatedObjects(context,
												DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // Rel pattern
												sbTypePattern.toString(), 	// Type Pattern
												slSelectList,         	// Bus select 
												null,                	// Rel select
												true,               	// to side
												true,              		// from side
												(short)1,         		// level of extraction
												sBusWhereExpression,    // Object where clause
												null,           		// Rel where clause
												0);            			// Limit
			//Code-Fix for Defect 26119 (POA PDF download issue) --End
			//Added the Code for Defect Id- 26992,27592,27587 - Ends
			if(mlFiles.size() > 0) {
				
				Iterator itrFileList = mlFiles.iterator();
				while(itrFileList.hasNext()) {
					//Added the Code for Defect Id- 26992 - Starts
					objFileMap = (Map) itrFileList.next();
					if(!objFileMap.isEmpty()) {
						sDocObjectID = (String)objFileMap.get(DomainConstants.SELECT_ID);
						domDocObj = DomainObject.newInstance(context, sDocObjectID);
						if (objFileMap.get(DomainConstants.SELECT_FILE_NAME) instanceof StringList) {
							slFileNameList = (StringList) objFileMap.get(DomainConstants.SELECT_FILE_NAME);
							slFileFormatList = (StringList)objFileMap.get(DomainConstants.SELECT_FILE_FORMAT);
							for (int i = 0; i < slFileNameList.size();i++) {
								
								sDocObjectFileName = (String) slFileNameList.get(i);
								sDocObjectFileFormat = (String) slFileFormatList.get(i);
					
								if (!sDocObjectFileName.equals("")) {	
									bIPMDocFilePresent= true;
									bError = checkoutFileInThePath(context, domDocObj, sDocObjectFileFormat, sDocObjectFileName, folder.toString());
								if(!bError) {
									mGenDocStamp = addStaming(context,domDocObj,folder.toString(),sDocObjectFileName);
									bError = (Boolean) mGenDocStamp.get("Error");
								 }
								
								} 
								if(bError) {	
									break;
								}
							}
							
						} else {
							sDocObjectFileName = (String)objFileMap.get(DomainConstants.SELECT_FILE_NAME);
							sDocObjectFileFormat= (String)objFileMap.get(DomainConstants.SELECT_FILE_FORMAT);
				
							if (!sDocObjectFileName.equals("")) {	
								bIPMDocFilePresent = true;
								bError = checkoutFileInThePath(context, domDocObj, sDocObjectFileFormat, sDocObjectFileName, folder.toString());
								if(!bError) {
									mGenDocStamp = addStaming(context,domDocObj,folder.toString(),sDocObjectFileName);
									bError = (Boolean) mGenDocStamp.get("Error");
							  }
							}
							if(bError) {	
								break;
							}
						}
					}
				}
			}
			//Added the Code for Defect Id- 26992 - Ends
			mReturn.put("Error", bError);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		 //Added the code for Defect 47035,47370 - Starts
		finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		 //Added the code for Defect 47035,47370 - Ends
		return mReturn;
	} 
	
	
	/**
	 * This method used to generate PDF Views From Part in to folder
	 */
	public Map generatePDFViewsFromPart(Context context, String[] args) throws Exception {
		
		HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
		String strPartId = (String) argumemtMap.get("objectId");
		File folder = (File) argumemtMap.get("folderPath");
		String strMode = (String) argumemtMap.get("mode");
		String strModeDisplay = "PDF View";
		Map mReturn = new HashMap();

		StringList slSelectList = new StringList(8);
		slSelectList.add(DomainConstants.SELECT_ID);
		slSelectList.add(DomainConstants.SELECT_TYPE);
		slSelectList.add(DomainConstants.SELECT_NAME);
		slSelectList.add(DomainConstants.SELECT_REVISION);
		
		DomainObject domObj = null;
		String strPartType = "";
		String strPartName = "";
		String strPartRev = "";
		String strPDfFileName = "";
		String strFileInfo = "";
		boolean bError = true;
		
		try {
			domObj = DomainObject.newInstance(context, strPartId);
			Map mPart = domObj.getInfo(context, slSelectList);
			strPartType = (String) mPart.get(DomainConstants.SELECT_TYPE);
			strPartName = (String) mPart.get(DomainConstants.SELECT_NAME);
			strPartRev = (String) mPart.get(DomainConstants.SELECT_REVISION);
			
			if (UIUtil.isNotNullAndNotEmpty(strMode)) {
				
				String[] methodArgs = new String[3];
				methodArgs[0] = strPartId;
				methodArgs[1] = strMode;
				methodArgs[2] = context.getUser();
	
				String strLocale = context.getLocale().toString();
				i18nNow i18nObject = new i18nNow();
				String strServerPath = i18nObject.GetString("emxCPN", strLocale, "emxCPN.ServerPath");
				String strPDFXMLBase = strServerPath+java.io.File.separator+"pdfHtmlBase"; 
				
				strFileInfo = (String)JPO.invoke(context,"pgIPMPDFViewUtil",null,"renderPDF",methodArgs,String.class);
				
				if (UIUtil.isNotNullAndNotEmpty(strFileInfo)) {
					strPDfFileName = new StringBuffer(strPartName).append("-Rev").append(strPartRev).append(".pdf").toString();
					File tempFile = new File(strFileInfo);
					FileUtils.moveFile(tempFile, new File(new StringBuffer(folder.toString()).append(java.io.File.separator).append(strPDfFileName).toString()));
					String tempDir = tempFile.getParent();
					FileUtils.deleteDirectory(new File(tempDir));
					bError = false;
				}
			}
		
			mReturn.put("Error", bError);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mReturn;
	}
	
	/**
	 * This method used to checkout the ArtiosCADComponent From Part in to folder
	 */
	public Map getArtiosCADComponentFile(Context context, String[] args) throws Exception {
		
		HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
		String strPartId = (String) argumemtMap.get("objectId");
		File folder = (File) argumemtMap.get("folderPath");
		String TYPE_ARTIOSCADCOMPONENT = PropertyUtil.getSchemaProperty("type_ArtiosCADComponent");
	
		String sDocObjectID = DomainConstants.EMPTY_STRING;
		String sDocObjectFileName = DomainConstants.EMPTY_STRING;
		String sDocObjectFileFormat = DomainConstants.EMPTY_STRING;
		
		Map objFileMap = null;
		Map mReturn = new HashMap();
		DomainObject domDocObj = null;
		
		boolean bArtosCADFilePresent = false;
		boolean bError = false;
		StringList slFileNameList = new StringList();
		StringList slFileFormatList = new StringList();
		StringList slObjectSelects = new StringList();
		slObjectSelects.addElement(DomainConstants.SELECT_TYPE);
		slObjectSelects.addElement(DomainConstants.SELECT_NAME);
		slObjectSelects.addElement(DomainConstants.SELECT_REVISION);
		slObjectSelects.addElement(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_FILE_NAME);
		slObjectSelects.add(DomainConstants.SELECT_FILE_FORMAT);
		slObjectSelects.add(DomainConstants.SELECT_FILE_SIZE);
		
		try {
			domDocObj = DomainObject.newInstance(context, strPartId);
			objFileMap = domDocObj.getInfo(context,slObjectSelects);
			
			if(!objFileMap.isEmpty()) {
			
				if (objFileMap.get(DomainConstants.SELECT_FILE_NAME) instanceof StringList) {
					slFileNameList = (StringList) objFileMap.get(DomainConstants.SELECT_FILE_NAME);
					slFileFormatList = (StringList)objFileMap.get(DomainConstants.SELECT_FILE_FORMAT);
					for (int i = 0; i < slFileNameList.size();i++) {
						
						sDocObjectFileName = (String) slFileNameList.get(i);
						sDocObjectFileFormat = (String) slFileFormatList.get(i);
			
						if (!sDocObjectFileName.equals("")) {	
							bArtosCADFilePresent= true;
							bError = checkoutFileInThePath(context, domDocObj, sDocObjectFileFormat, sDocObjectFileName, folder.toString());
						} 
						if(bError) {	
							break;
						}
					}
					
				} else {
					sDocObjectFileName = (String)objFileMap.get(DomainConstants.SELECT_FILE_NAME);
					sDocObjectFileFormat= (String)objFileMap.get(DomainConstants.SELECT_FILE_FORMAT);
		
					if (!sDocObjectFileName.equals("")) {	
						bArtosCADFilePresent = true;
						bError = checkoutFileInThePath(context, domDocObj, sDocObjectFileFormat, sDocObjectFileName, folder.toString());
					} 
				}
			}
			
			mReturn.put("Error", bError);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mReturn;
	} 

	/**
	 * This method will checkout the file in folder provided
	 * @param context the eMatrix <code>Context</code> object.
	 * @param domObj holds the domainObject on which the checkout is to be performed.
	 * @param sObjectFileFormat
	 * @param sObjectFileName
	 * @param folder
	 * @return boolean indicating if there was any error in the checkout or not.
	 */
	public static boolean checkoutFileInThePath(Context context, DomainObject domObj, String sObjectFileFormat, String sObjectFileName, String folder) throws MatrixException 
	{
		boolean bError = false;
		
		try  {
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			
			domObj.checkoutFile(context, false, sObjectFileFormat, sObjectFileName ,folder);
			
		} catch(MatrixException e) {
			bError = true;
		}
		finally {
			
			ContextUtil.popContext(context);
		}
		return bError;
	}
	
	/**
	 * This method will get the files in the srcDir and zip them in name full file name provided in zipFile.
	 * @param zipFile holds the name for the zip file to be created.
	 * @param srcDir holds the folder where the files to be zipped are available
	 * @return Nothing 
	 */
	public  void zipFiles(String zipFile, String srcDir) throws Exception 
	{
		FileInputStream fis 	= null;
		ZipOutputStream zos 	= null;
		try 
		{
			File srcFile 		= new File(srcDir);
			File[] files 		= srcFile.listFiles();
			zos 				= new ZipOutputStream(new FileOutputStream(zipFile));
			int ilength 		= 0;
			
			byte[] buffer;
			for (int iCount = 0; iCount < files.length; iCount++) 
			{
				// create byte buffer
				buffer 			= new byte[1024];
				fis 			= new FileInputStream(files[iCount]);
				zos.putNextEntry(new ZipEntry(files[iCount].getName()));

				while ((ilength = fis.read(buffer)) > 0) 
				{
					zos.write(buffer, 0, ilength);
				}
				zos.closeEntry();
				fis.close();
			}
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			zos.close();
			
		}
	}
	
	
	 /**
     * this method is used to delete the files once the file is checked into the object
     * 
     * @param args
     * @return void
     * @throws Exception
     */
	 public void deleteFiles(Context context, String strReportPath) throws Exception {
		try { 
			 java.io.File folderDelete = new java.io.File(strReportPath);
			 if(folderDelete.list().length==0){
	 		   folderDelete.delete();
	 		   
			 }else{ 
	 		   //list all the directory contents
	     	   String files[] = folderDelete.list();
	
	     	   for (String temp : files) {
	     	      //construct the file structure
	     	      java.io.File fileDelete = new java.io.File(folderDelete, temp);
	     	      fileDelete.delete();
	     	   }
	
	     	   //check the directory again, if empty then delete it
	     	   if(folderDelete.list().length==0){
	        	     folderDelete.delete();
	     	   }
	 		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 // Code to check if User has access to the Part
	 private boolean accessCheck(Context context, String strUserName, String strPartId) throws Exception {
			boolean bAccess = false; 
			String strValue = "";
			try{
				
				DomainObject domainObject = DomainObject.newInstance(context);
				domainObject.setId(strPartId);
				//System.out.println("Before push user context: ");
				ContextUtil.pushContext(context, strUserName, null,context.getVault().getName());
				strValue = (String)domainObject.getInfo(context, "current");
				//System.out.println("After push user context: ");
				ContextUtil.popContext(context);			
				if(!"#DENIED!".equals(strValue) && !strValue.equals(""))
				{				
					bAccess = true;					
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return bAccess;
		}
		 
		 private String getPersonId(Context context, String strUserName) throws Exception {
				String strPersonId = ""; 
				try{
					//short sQueryLimit=1;
					String strUserSecurityType = "";
					StringList slSelect = new StringList(1);
					slSelect.add(DomainConstants.SELECT_ID);
					//MapList mlPerson = DomainObject.findObjects(context,DomainConstants.TYPE_PERSON,strUserName,"-","*",pgV3Constants.VAULT_ESERVICEPRODUCTION,null,"",true,(StringList) slSelect,sQueryLimit);
					MapList mlPerson = DomainObject.findObjects(context,DomainConstants.TYPE_PERSON,strUserName,"-","*",pgV3Constants.VAULT_ESERVICEPRODUCTION,"",false,slSelect);
					int maplistSize = mlPerson.size();
					for(int i=0;i<maplistSize;i++){
						Map mpPersonData = (Map)mlPerson.get(i);					
						strPersonId = (String)mpPersonData.get(DomainConstants.SELECT_ID);					
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				 return strPersonId;
			}
		 /**
			 * Description: This method is used to check if user is EBP CM.
			 * Returns: True/False
			 */
		 	private boolean isEBP(Context context, String strUserName) throws Exception {
				boolean bIsEBP = false; 
				String strUserSecurityType = "";
				try{
					//short sQueryLimit=1;
					StringList slSelect = new StringList(3);											
					slSelect.add(DomainConstants.SELECT_CURRENT);
					slSelect.add(DomainConstants.SELECT_ID);
					slSelect.add("attribute[pgSecurityEmployeeType]");
					//MapList mlPerson = DomainObject.findObjects(context,DomainConstants.TYPE_PERSON,strUserName,"-","*",pgV3Constants.VAULT_ESERVICEPRODUCTION,null,"",true,(StringList) slSelect,sQueryLimit);
					MapList mlPerson = DomainObject.findObjects(context,DomainConstants.TYPE_PERSON,strUserName,"-","*",pgV3Constants.VAULT_ESERVICEPRODUCTION,"",false,slSelect);
					int maplistSize = mlPerson.size();
					for(int i=0;i<maplistSize;i++){
						Map mpPersonData = (Map)mlPerson.get(i);					
						strUserSecurityType = (String)mpPersonData.get("attribute[pgSecurityEmployeeType]");					
					}
					if (strUserSecurityType.equalsIgnoreCase("EBP")) {
						bIsEBP = true;
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//System.out.println("strUserSecurityType: " + strUserSecurityType);
				//System.out.println("bIsEBP: " + bIsEBP);
				 return bIsEBP;
			}
		 	 /**
			 * Description: This method is used to check if user is EBP CM.
			 * Returns: True/False
			 */
		 private boolean isAuthorizedToProduce(Context context, String strUserName, String strPartId) throws Exception {			
				boolean bAuthorizedToProduce = false;
				boolean bIsEBP = isEBP(context, strUserName);
				boolean bAccess = accessCheck(context,strUserName,strPartId);
				Short sRecursionLevel = 1;
				StringBuffer sbMemberCompany = new StringBuffer();
				StringList slObjSelect = new StringList(1);
				slObjSelect.add("name");
				StringList slRelSelect = new StringList(1);
				slRelSelect.add("attribute[pgIsAuthorizedtoProduce]");
				
				if (bIsEBP && bAccess)
				try {			
					String strUserOid = getPersonId(context,strUserName);
					DomainObject domPersonObj= DomainObject.newInstance(context,strUserOid);
					DomainObject domPartObj= DomainObject.newInstance(context,strPartId);
					ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					MapList mlRelatedPlant = domPartObj.getRelatedObjects(context, DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, pgV3Constants.TYPE_PLANT, slObjSelect,slRelSelect, true, false, (short) 1, "",""); 
					MapList mlRelatedUserVendors = (MapList)domPersonObj.getRelatedObjects(context, "Member", "Plant",slObjSelect,null,true,false,sRecursionLevel,null, null, 0);	
					ContextUtil.popContext(context);
					if (mlRelatedUserVendors.size()>0) {
						Iterator itrVendor = mlRelatedUserVendors.iterator();						
						while (itrVendor.hasNext()) {
							Map mpUserVendor = (Map)itrVendor.next();	
							String strUserVendor =(String)mpUserVendor.get("name");	
							//System.out.println("strUserVendor: " + strUserVendor);
							sbMemberCompany.append(strUserVendor);
							sbMemberCompany.append("|");
						}					
					} 
					//System.out.println("sbMemberCompany: " + sbMemberCompany.toString());
					if (mlRelatedPlant.size()>0) {
						Iterator itrPlant = mlRelatedPlant.iterator();						
						while (itrPlant.hasNext()){
							Map mpRelatedPlant = (Map)itrPlant.next();	
							String strPlantName =(String)mpRelatedPlant.get("name");						
							String strAuthorizedToProduce =(String)mpRelatedPlant.get("attribute[pgIsAuthorizedtoProduce]");
							//System.out.println("bAuthorizedToProduce related Plant strAuthorizedToProduce: " + strAuthorizedToProduce);
							if (sbMemberCompany.indexOf(strPlantName)>-1 && strAuthorizedToProduce.equalsIgnoreCase("TRUE")){
								bAuthorizedToProduce = true;
								break;
							}
						}
					}
				} catch(Exception e){
					e.printStackTrace();
				}
				//System.out.println("bAuthorizedToProduce: " + bAuthorizedToProduce);
				 return bAuthorizedToProduce;
			}
		 /**
			 * Description: This method is used to check if user is EBP CM.
			 * Returns: True/False
			 */
		 private boolean isCMEBP(Context context, String strUserName) throws Exception {			
				boolean bisCMEBP = false;					
				boolean bIsEBP = isEBP(context, strUserName);
				
				Short sRecursionLevel = 1;
				StringBuffer sbMemberCompany = new StringBuffer();
				StringList slObjSelect = new StringList(1);							
				slObjSelect.add(DomainConstants.SELECT_NAME);
				StringList slRelSelect = new StringList();	
				
				if (bIsEBP) 
					try {				
						ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
						String strUserOid = getPersonId(context,strUserName);					
						DomainObject domPersonObj= DomainObject.newInstance(context,strUserOid);
						MapList mlRelatedUserVendors = (MapList)domPersonObj.getRelatedObjects(context, "Member", "Plant", slObjSelect, slRelSelect,true,false,sRecursionLevel,null, null, 0);	
						ContextUtil.popContext(context);
						Iterator itrVendor = mlRelatedUserVendors.iterator();	
						while (itrVendor.hasNext()) {
							Map mpUserVendor = (Map)itrVendor.next();
							String strUserVendor = (String) mpUserVendor.get("name");
							String[] slVendorDetail = strUserVendor.split("~");
							String strVendorCode = slVendorDetail[slVendorDetail.length-1];
							int iLength = strVendorCode.length();
							if(iLength == 4){				
								bisCMEBP = true;
								break;
							}
						}
					} catch(Exception e){
						e.printStackTrace();
					}
				//System.out.println("bisCMEBP: "+bisCMEBP);
					return bisCMEBP;
				}
		 
		 private boolean isSupplierEBP(Context context, String strUserName) throws Exception {			
				boolean isSupplierEBP = false;					
				boolean bIsEBP = isEBP(context, strUserName);
				
				Short sRecursionLevel = 1;
				StringBuffer sbMemberCompany = new StringBuffer();
				StringList slObjSelect = new StringList(1);							
				slObjSelect.add(DomainConstants.SELECT_NAME);
				StringList slRelSelect = new StringList();	
				
				if (bIsEBP) 
					try {				
						ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
						String strUserOid = getPersonId(context,strUserName);					
						DomainObject domPersonObj= DomainObject.newInstance(context,strUserOid);
						MapList mlRelatedUserVendors = (MapList)domPersonObj.getRelatedObjects(context, "Member", "Company", slObjSelect, slRelSelect,true,false,sRecursionLevel,null, null, 0);	
						ContextUtil.popContext(context);
						Iterator itrVendor = mlRelatedUserVendors.iterator();	
						while (itrVendor.hasNext()) {
							Map mpUserVendor = (Map)itrVendor.next();
							String strUserVendor = (String) mpUserVendor.get("name");
							String[] slVendorDetail = strUserVendor.split("~");
							String strVendorCode = slVendorDetail[slVendorDetail.length-1];
							int iLength = strVendorCode.length();
							if(iLength == 8){				
								isSupplierEBP = true;
								break;
							}
						}	
					} catch(Exception e){
						e.printStackTrace();
					}
				//System.out.println("isSupplierEBP: "+isSupplierEBP);
					return isSupplierEBP;
				}
		 
		private boolean hasSupplierAccess(Context context, String strUserName, String strPartId) throws Exception {			
			boolean hasSupplierAccess = false;
			boolean bHasaccess = false;
			boolean bHasaccessWithOutComment = false;
			
			boolean bIsEBP = isEBP(context, strUserName);
			boolean bAccess = accessCheck(context,strUserName,strPartId);
			Short sRecursionLevel = 1;
			StringBuffer sbMemberCompany = new StringBuffer();
			StringList slObjSelect = new StringList(1);							
			slObjSelect.add(DomainConstants.SELECT_NAME);
			StringList slRelSelect = new StringList();	
			
			if (bIsEBP && bAccess)
				try {				
					ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					String strUserOid = getPersonId(context,strUserName);
					DomainObject domPersonObj= DomainObject.newInstance(context,strUserOid);
					MapList mlRelatedUserVendors = (MapList)domPersonObj.getRelatedObjects(context, "Member", "Company", slObjSelect, slRelSelect,true,false,sRecursionLevel,null, null, 0);	
					ContextUtil.popContext(context);
					if (mlRelatedUserVendors.size()>0) {
						Iterator itrVendor = mlRelatedUserVendors.iterator();						
						while (itrVendor.hasNext()){
							Map mpUserVendor = (Map)itrVendor.next();	
							String strUserVendor =(String)mpUserVendor.get("name");
							bHasaccess = DomainAccess.hasObjectOwnership(context, strPartId, strUserVendor, "Partners_PG", "Multiple Ownership For Object");
							bHasaccessWithOutComment = DomainAccess.hasObjectOwnership(context, strPartId, strUserVendor, "Partners_PG", "");
							if(bHasaccess || bHasaccessWithOutComment) 
							{
								hasSupplierAccess = true;
								break;
							}						
						}
					}				
				} catch(Exception e){
					e.printStackTrace();
				}
				 return hasSupplierAccess;
			}
	/**
	 * this method is used to process the Background Job for the Report
	 * 
	 * @param args
	 * @return void
	 * @throws Exception
	 */
	/*
	public void processBackgroundJobForReport(Context context, String args[]) throws Exception {
		HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
		String strUserName = (String) hmArgs.get("UserName");
		String strPartNames = (String) hmArgs.get("GCAS");
		String strChangeActions = (String) hmArgs.get("ChangeAction");
		String strReportFileName = (String) hmArgs.get("ReportFileName");
		String strReportObjectId = (String) hmArgs.get("ReportObjectId");
		String strPDFDownloadDailyLimit= (String) hmArgs.get("PDFDownloadDailyLimit");
		
		try { 
			HashMap hmArgs1 = new HashMap();
			hmArgs1.put("UserName",strUserName);
			hmArgs1.put("GCAS",strPartNames);
			hmArgs1.put("ChangeAction",strChangeActions);
			hmArgs1.put("ReportFileName",strReportFileName);
			hmArgs1.put("ReportObjectId",strReportObjectId);
			hmArgs1.put("PDFDownloadDailyLimit",strPDFDownloadDailyLimit);
			BackgroundProcess backgroundProcess = new BackgroundProcess();
			backgroundProcess.submitJob(context, "com.pg.dsm.custom.pgPDFDownloadReport", "generatePDFViewsGenDocReportEntry", JPO.packArgsRemote(hmArgs1) , (String)null);
	     } catch(Exception ex) {
	    	 ContextUtil.abortTransaction(context);
	    	 ex.printStackTrace();
	    	 throw ex;
	     }
	}
	*/
	public boolean IsEBPUser(Context context, String args[]) {
		
		boolean bIsEBPUser = false;
		String strUserName = context.getUser();
		String strUserSecurityType = "";
		String strCurrent = "";
		String strUserOid = "";
		short sQueryLimit = 5;
		StringList slSelect = new StringList(3);											
		slSelect.add(DomainConstants.SELECT_CURRENT);
		slSelect.add(DomainConstants.SELECT_ID);
		slSelect.add("attribute[pgSecurityEmployeeType]");
		MapList mlPerson = new MapList();
		try {
			mlPerson = DomainObject.findObjects(context,
											DomainConstants.TYPE_PERSON,
											strUserName,
											"-",
											"*",
											pgV3Constants.VAULT_ESERVICEPRODUCTION,
											null,
											"",
											true,
											(StringList) slSelect,
											sQueryLimit);
		} catch (FrameworkException e) {
			e.printStackTrace();
		}
		
		int maplistSize = mlPerson.size();
		for(int i=0;i<maplistSize;i++){
			Map mpPersonData= (Map)mlPerson.get(i);
			strCurrent = (String)mpPersonData.get(DomainConstants.SELECT_CURRENT);
			strUserOid = (String)mpPersonData.get(DomainConstants.SELECT_ID);
			strUserSecurityType = (String)mpPersonData.get("attribute[pgSecurityEmployeeType]");					
		}
		
		if (strUserSecurityType.equalsIgnoreCase("EBP")) {
			bIsEBPUser = true;
		}
		
		return bIsEBPUser;
	}
	
	/**
	 * Method used to display all the DSM Report Log objects
	 * @param context
	 * @param args
	 */
	public  MapList displayDSMReportLogData(Context context,String args[]) throws Exception {
		MapList mlCheckinObjectFileList = null;
		String TYPE_PGDSMREPORTLOG = PropertyUtil.getSchemaProperty("type_pgDSMReportLog");
		StringList slObjectsSelect = new StringList();           
		slObjectsSelect.add(DomainConstants.SELECT_TYPE);
		slObjectsSelect.add(DomainConstants.SELECT_NAME);
		slObjectsSelect.add(DomainConstants.SELECT_REVISION);
		slObjectsSelect.add(DomainConstants.SELECT_ID);
		slObjectsSelect.add(DomainConstants.SELECT_MODIFIED);
		slObjectsSelect.add("format.file.name");
		slObjectsSelect.add("format.file.modified");
		
		try {
			
			mlCheckinObjectFileList = DomainObject.findObjects(context, TYPE_PGDSMREPORTLOG, "*", "", slObjectsSelect);
			mlCheckinObjectFileList.sort(DomainConstants.SELECT_MODIFIED, "descending", "date");
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return mlCheckinObjectFileList;
	 }
	
	/*
	 * This Method is used to stamp the Footer in Gen Doc PDF
	 */
	public Map addStaming(Context context,DomainObject dbIPM,String strTempDirectory, String fileNamesPart) throws Exception {
		Map mReturn = new HashMap();
		boolean bStampPDFError = false;
		try {
			i18nNow i18nObject = new i18nNow();
			String strProductDataTypes = FrameworkProperties.getProperty("emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
			String strProductDataPOATypes = FrameworkProperties.getProperty("emxCPN.ProductData.NoStampingOnCheckout.Types");
			String strNonStructuredDataTypes= FrameworkProperties.getProperty("emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
			boolean isReachStatement = false;
			String  genericFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
			String strObjectId = (String)(dbIPM.getInfo(context,"to[Reference Document].from.id"));
			String strLocale = context.getLocale().toString();
			String returnDate = "";
			StringList objectSelects = new StringList(8);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_REVISION);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add("attribute[pgCSSType]");
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
			objectSelects.add("attribute[pgOriginatingSource]");
			objectSelects.add("attribute[Status]");
			objectSelects.add("state[Obsolete].actual");
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
			DomainObject newObjDO = DomainObject.newInstance(context, strObjectId);
			Map mObjectDetails = newObjDO.getInfo(context,objectSelects);
			String strName = (String)(mObjectDetails.get(DomainConstants.SELECT_NAME));
			String strRevision = (String)(mObjectDetails.get(DomainConstants.SELECT_REVISION));
			String strAttributeValue = (String)(mObjectDetails.get("attribute[pgCSSType]"));
			String strOrigSource = (String)(newObjDO.getInfo(context,"attribute[pgOriginatingSource]"));
			String StrObjState = newObjDO.getInfo(context, "current");
			String StrObjOriginator = newObjDO.getInfo(context, "current");
			String strType = (String)(mObjectDetails.get(DomainConstants.SELECT_TYPE));
			String effectivityDate = (String)mObjectDetails.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
			String systemDateFormat = eMatrixDateFormat.getEMatrixDateFormat();
			SimpleDateFormat formatter = new SimpleDateFormat (systemDateFormat);
			String StrObjAttributeValue = newObjDO.getAttributeValue(context, "Status");
			String strComment = i18nObject.GetString("emxCPN", strLocale, "emxCPN.PDF.COMMENTNOTFORUSE");
			String format = (String)dbIPM.getInfo(context, DomainConstants.SELECT_FILE_FORMAT);
			String fileName = fileNamesPart;
			String pgIPClassification = (String) mObjectDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
			String strObjectSecurityStatus = pgIPClassification.format(Locale.ENGLISH, "%s", pgIPClassification);
			
			if(("pgPackingMaterial".equalsIgnoreCase(strType) ||
					"pgMasterPackingMaterial".equalsIgnoreCase(strType) ||
					"pgMasterRawMaterial".equalsIgnoreCase(strType) ||
					"pgRawMaterial".equalsIgnoreCase(strType))&& (strAttributeValue.indexOf("_")==-1))
			{
				isReachStatement=true;
	
			}
			String strPDFexe = i18nObject.GetString("emxCPN",strLocale,"emxCPN.PDF.StampPDFPath");
			String strLicense = i18nObject.GetString("emxCPN", strLocale, "emxCPN.PDF.StampPDFLicense");
			String strPDFStamp = i18nObject.GetString("emxCPN", strLocale, "emxCPN.PDF.Stamping");
			String sResult = "";
			String strStampFile = "";
			if((!strProductDataPOATypes.contains(strType)) || (strProductDataPOATypes.contains(strType) && (strOrigSource.equalsIgnoreCase("DSO")))) {
				
				if((pgV3Constants.RESTRICTED.equalsIgnoreCase(strObjectSecurityStatus) || pgV3Constants.INTERNAL_USE.equalsIgnoreCase(strObjectSecurityStatus))) {	
					
					if((strProductDataTypes.contains(strType) || strNonStructuredDataTypes.contains(strType))) {
						strStampFile=strPDFStamp+java.io.File.separator+"stampDSOBusinessUse.txt";
					} else {
						strStampFile=strPDFStamp+java.io.File.separator+"stampBusinessUse.txt";
					}
				} else if(strProductDataTypes.contains(strType) || strNonStructuredDataTypes.contains(strType))  {	
	
					strStampFile=strPDFStamp+java.io.File.separator+"stampDSO.txt";
				} else {
					
					strStampFile=strPDFStamp+java.io.File.separator+"stamp.txt";
				}
	
	
				StringBuffer sbFileContent = new StringBuffer();
				BufferedReader bf = new BufferedReader(new FileReader(strStampFile));
	
				String sLine = bf.readLine();
				DateFormat formatter1 = new SimpleDateFormat (systemDateFormat);
				String sPart1 = "";
				Date newtmpDate = null;
				
				if (effectivityDate != null && effectivityDate.trim().length() > 0) {
					Date tmpDate = formatter.parse(effectivityDate);
	
					formatter = new SimpleDateFormat("yyyy-MM-dd");	
					returnDate = formatter.format(tmpDate);
				}
				while (sLine != null) {
					if (sLine.indexOf("#STAMP_VARIABLE#") < 0) {
						sbFileContent.append(sLine).append("\n");
					} else { 
						if("Obsolete".equalsIgnoreCase(StrObjState)){
							String obsoleteDate = (String)newObjDO.getInfo(context, "state[Obsolete].actual");
							if (obsoleteDate != null && obsoleteDate.trim().length() > 0) {
								newtmpDate =(Date)formatter1.parse(obsoleteDate);
								returnDate = formatter.format(newtmpDate);
							}
							sPart1 = sLine.replace("AUTHORIZED #NAME_VARIABLE# Rev #REVISION_VARIABLE# Effective Date","OBSOLETE");
							sPart1 = sPart1.replace("#STAMP_VARIABLE#",returnDate);
							if(StrObjAttributeValue.equalsIgnoreCase("PLANNING")){
	
								sPart1 = sPart1.replace("P&G",strComment + "%n" + "P&G");
							}
						} 
						else if(("Release".equalsIgnoreCase(StrObjState))){
	
							sPart1 = sLine.replace("#STAMP_VARIABLE#",returnDate);
							if(StrObjAttributeValue.equalsIgnoreCase("PLANNING")){
	
								sPart1 = sPart1.replace("P&G",strComment + "%n" + "P&G");	
							}
						}
						sPart1 = sPart1.replace("#NAME_VARIABLE#",strName);
						sPart1 = sPart1.replace("#REVISION_VARIABLE#",strRevision);
						sbFileContent.append(sPart1).append("\n");
					}
					sLine = bf.readLine();
				}
				File f = new File(strTempDirectory+java.io.File.separator+"stampFile.txt");
				if (f.exists())
					f.delete();
				f.createNewFile();
				f.setExecutable(true);
				f.setReadable(true);
				f.setWritable(true);
				BufferedWriter bwForImage = new BufferedWriter(new FileWriter(f));
				bwForImage.write(sbFileContent.toString());
				bwForImage.flush();
				bwForImage.close();
				sResult = f.getPath();
			}
			if(isReachStatement) {
				
				PdfReader reader1 = new PdfReader(strTempDirectory+java.io.File.separator+fileName);
				PdfReader reader2 = new PdfReader(strPDFStamp+java.io.File.separator+"ReachStatement.pdf");
				PdfCopyFields copy = new PdfCopyFields(new FileOutputStream(strTempDirectory +java.io.File.separator+"FullFile.pdf"));
				copy.addDocument(reader1);
				copy.addDocument(reader2);
				copy.close();
				File fOrig = new File(strTempDirectory+java.io.File.separator+fileName);
				fOrig.delete();
				File fNew = new File(strTempDirectory +java.io.File.separator+"FullFile.pdf");
				fNew.renameTo(new File(strTempDirectory+java.io.File.separator+fileName));
			}
			
			fileName = strTempDirectory+java.io.File.separator+fileName;
			java.io.File pdfFileName= new java.io.File(fileName);	
			String fileName1 = pdfFileName.getName();
			String strStampPDFFile = strTempDirectory+java.io.File.separator+fileName1;
			java.io.File tempTranslationFile = new java.io.File(strTempDirectory+java.io.File.separator+"ExampleTranslation.pdf");
			pdfFileName.renameTo(tempTranslationFile);
			String myFile = tempTranslationFile.getName();
			Process pStampPdf = Runtime.getRuntime().exec(strPDFexe+" "+strLicense+" "+sResult+" "+tempTranslationFile); 
			pStampPdf.waitFor();
			tempTranslationFile.renameTo(pdfFileName);
			fileName1 = pdfFileName.getName();
			File f = new File(strTempDirectory+java.io.File.separator+"stampFile.txt");
			if (f.exists())
				f.delete();
		} catch (Exception e) {
			bStampPDFError = true;
			File f = new File(strTempDirectory+java.io.File.separator+"stampFile.txt");
			if (f.exists())
				f.delete();
			
			File f2 = new File(strTempDirectory+java.io.File.separator+"ExampleTranslation.pdf");
			if (f2.exists())
				f2.delete();
			
			e.printStackTrace();
			
		} finally {
			mReturn.put("Error", bStampPDFError);
		}
		
		return mReturn;
	}
	
	/**
	 * this method is used to retrieve the JVM Instance for logging
	 * @param context is the matrix context
	 * @param none
	 * @return string
	 * @throws IOException 
	 * @throws Exception
	 */
	private String getJVMInstance() throws Exception {				
		String strCatalinaPath=System.getProperty(CATALINA_BASE);
		String strInstanceName=DomainConstants.EMPTY_STRING;
		try {
			if(UIUtil.isNotNullAndNotEmpty(strCatalinaPath) && strCatalinaPath.contains(PATH_SEPARATOR)){			
				StringList slInfo=FrameworkUtil.split(strCatalinaPath, PATH_SEPARATOR);
				strInstanceName=slInfo.get(slInfo.size()-2);
			}
		} catch (Exception e) {
			outLog.print("Exception in  getJVMInstance: "+e+"\n");
			outLog.flush();	
		}
		strInstanceName = (null != strInstanceName) ? strInstanceName : "";		
		return strInstanceName;
	}
	
	/**This method is used to write the input entered by manual entry or by browing the input file into the log file.
	 * @param context is the matrix context
	 * @param args has the required information
	 * @return void
	 * @throws Exception
	 */ 
	/*
	public void logUserInputData(Context context, String strUserName, String strPartNames,String strChangeActions) throws Exception{
		String strWorkSpaceFolder = context.createWorkspace();
		String strFileName = "PDFDownloadRequest"+"_"+strUserName+"_"+String.valueOf(System.currentTimeMillis())+".log";
		String strWorkSpacePath = strWorkSpaceFolder + java.io.File.separator + strFileName;
		File file = new File(strWorkSpacePath);
	    FileWriter fr = null;
	    String strFinalData = DomainConstants.EMPTY_STRING;
	    if(UIUtil.isNotNullAndNotEmpty(strPartNames) && UIUtil.isNotNullAndNotEmpty(strChangeActions)) {
	    	if(strPartNames.equalsIgnoreCase(strChangeActions)) {
	    		strFinalData = strPartNames;
	    	}
	    	else {
	    		strFinalData = strPartNames+","+strChangeActions;
	    	}
	    }
	    else if(UIUtil.isNotNullAndNotEmpty(strPartNames)) {
	    	strFinalData = strPartNames;
	    }
	    else if(UIUtil.isNotNullAndNotEmpty(strChangeActions)) {
	    	strFinalData = strChangeActions;
	    }
	    else {
	    	strFinalData = DomainConstants.EMPTY_STRING;
	    }
	    try {
	        fr = new FileWriter(file);
	        fr.write(strFinalData);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally{
	        try {
	            fr.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	*/
	
	//Added the code for Defect 47035,47370 - Starts
	/**
	 * @param context
	 * @param strObjectId
	 * @param strView
	 * @param mpPersonData
	 * @return
	 */
	public boolean checkViewAvailability(Context context, String strObjectId, String strView, Map mpPersonData) {
		boolean viewAvailable = false;
	    boolean havingMaster = false;
	    try {
	      DomainObject dmo = DomainObject.newInstance(context, strObjectId);
	      StringList slRelSelect = new StringList(2);
	      slRelSelect.add(pgV3Constants.SELECT_ID);
	      slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
	      StringList slMaster = new StringList(5);
	      slMaster.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
	      slMaster.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
	      slMaster.add(pgV3Constants.SELECT_TYPE);
	      slMaster.add(pgV3Constants.SELECT_POLICY);
	      slMaster.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
	      Map<String, String> mapMaster = dmo.getInfo(context, slMaster);
	      String strType = mapMaster.get(pgV3Constants.SELECT_TYPE);
	      String strCSSType = mapMaster.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
	      String strOriginatingSource = mapMaster.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
	      String strPolicy = mapMaster.get(pgV3Constants.SELECT_POLICY);
	      String strContractPackagingTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.ContractPackagingTypes");
	      String strSupplierViewTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.SupplierViewTypes");
	      havingMaster = hasMaster(context,strType,dmo);
	         
	   if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_SPECREADERCONTRACTMANUFACTURER)) {
		   viewAvailable =  checkViewAvailableforManufacturer(context,strType,dmo,strView,strCSSType,strContractPackagingTypes,mpPersonData);
	   } else if (strView.equalsIgnoreCase(STR_SUPPLIER) && strSupplierViewTypes.contains(strType)) {
	          viewAvailable = true;
	        }  
	   //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 53767 - Starts
	   // Added by DSM Report(Sogeti) for 22x.04 (Dec CW 2023) Defect 55128 - Start
	   if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)|| context.isAssigned(pgV3Constants.ROLE_SPECREADERSUPPLIER)) {
		// Added by DSM Report(Sogeti) for 22x.04 (Dec CW 2023) Defect 55128 - End
	   //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 53767 - Ends
	    	  viewAvailable = checkViewAvailableforSupplier(strType,strView,strCSSType,strOriginatingSource);
	    	  
	      } else {
	        boolean isStructured = checkIfStructured(strType, strCSSType, strOriginatingSource);
	        if (strView.equalsIgnoreCase(STR_WAREHOUSE))
	          isStructured = true; 
	        boolean viewOK = checkViewType(context, strView, strType, havingMaster, strOriginatingSource, mpPersonData);
	        if (isStructured && viewOK) {
	        	if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strType) && STR_SUPPLIER.equals(strView)) {
	  	          viewAvailable = false;
	  	          } else {
	  	            viewAvailable = true;
	  	          }  
	        }
	          
	        if (strView.equalsIgnoreCase(STR_CONTRACTPACKAGING)) {
	        	viewAvailable = checkViewAvailableforContractPackaging(context,strType,strCSSType,strContractPackagingTypes,dmo,mpPersonData);
	          
	          if (strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFORMULATEDPRODUCT) && !strCSSType.contains("_"))
	            viewAvailable = false; 
	        } 
	        if (strView.equalsIgnoreCase(STR_SUPPLIER) && (pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART
	          .equals(strType) || pgV3Constants.TYPE_DEVICEPRODUCTPART
	          .equals(strType)))
	          viewAvailable = false; 
	      } 
	      if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy) || pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
	        if (STR_SUPPLIER.equalsIgnoreCase(strView) || STR_ALLINFO.equalsIgnoreCase(strView) || STR_PQR
	          .equalsIgnoreCase(strView)) {
	          viewAvailable = true;
	        } else {
	          viewAvailable = false;
	        }  
	    }
	    } catch (Exception e) {
	    	outLog.print("Exception in checkViewAvailability Method "+e+"\n");
			 outLog.flush(); 
	    } 
	    return viewAvailable;
	  }
	
	/**
	 * @param context
	 * @param strView
	 * @param strType
	 * @param havingMaster
	 * @param strOriginatingSource
	 * @param mpPersonData
	 * @return
	 * @throws MatrixException
	 */
	private boolean checkViewType(Context context, String strView, String strType, boolean havingMaster, String strOriginatingSource,Map mpPersonData) throws MatrixException {
		boolean isViewOK = false;
	    boolean isWareHouseView = false;
	    String strProductDataTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
	    String strAllInfoTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.AllInfoTypes");
	    String strSupplierViewTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.SupplierViewTypes");
	    String strContractPackagingTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.ContractPackagingTypes");
	    String strCombinedWithMasterTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.CombinedWithMasterTypes");
	    String strWareHouseTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.WareHouseTypes");
	    String strConsolidatedPackagingTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.ConsolidatedPackagingTypes");
	    String strPQRTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.PQRTypes");
	    
	    String contextUserType = (String)mpPersonData.get("attribute[pgSecurityEmployeeType]");
	    if (STR_EMPLOYEE.equals(contextUserType) || context.isAssigned(STR_PGIPMWAREHOUSEREADER)) {
	    	 isWareHouseView = true; 
	    }
	     
	    if (validateString(strView) && validateString(strType)) {
	      if (STR_ALLINFO.equals(strView) && strAllInfoTypes.contains(strType)) {
	        isViewOK = true;
	      } else if (STR_COMBINEDWITHMASTER.equals(strView) && havingMaster && strCombinedWithMasterTypes
	        .contains(strType)) {
	        isViewOK = true;
	      } else if (STR_CONTRACTPACKAGING.equals(strView) && strContractPackagingTypes.contains(strType)) {
	        isViewOK = true;
	      } else if (STR_SUPPLIER.equals(strView) && strSupplierViewTypes.contains(strType)) {
	        isViewOK = true;
	      } else if (STR_WAREHOUSE.equals(strView) && isWareHouseView && strWareHouseTypes.contains(strType)) {
	        isViewOK = true;
	      } else if (STR_CONSOLIDATEDPACKAGING.equals(strView) && strConsolidatedPackagingTypes
	        .contains(strType)) {
	        isViewOK = true;
	      } else if (STR_PQR.equals(strView) && strPQRTypes.contains(strType)) {
	        if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strType) && !STR_DSO.equals(strOriginatingSource)) {
	          isViewOK = false;
	        } else {
	          isViewOK = true;
	        } 
	      } else if (STR_GENDOC.equals(strView) && strProductDataTypes.contains(strType)) {
	        isViewOK = true;
	      } 
	      if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) || context
	        .isAssigned(pgV3Constants.ROLE_PGCONTRACTPACKER) || context.isAssigned(pgV3Constants.ROLE_SPECREADERCONTRACTMANUFACTURER)) {
	        StringList slViews = new StringList();
	        slViews.add(STR_ALLINFO);
	        slViews.add(STR_COMBINEDWITHMASTER);
	        if (slViews.contains(strView) || (STR_SUPPLIER
	          .equals(strView) && strSupplierViewTypes.contains(strType)))
	          isViewOK = false; 
	      } 
	    } 
	    return isViewOK;
	  }
	/**
	 * @param context
	 * @param strType
	 * @param strCSSType
	 * @return
	 * @throws MatrixException
	 */
	public boolean checkIfStructuredForEBPUser(Context context, String strType, String strCSSType) throws MatrixException {
		boolean isStructured = false;
	    try {
	      if (UIUtil.isNotNullAndNotEmpty(strCSSType) && strCSSType.contains("_")) {
	        StringList slCMTypes = new StringList();
	        slCMTypes.add(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
	        slCMTypes.add(pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT);
	        slCMTypes.add(pgV3Constants.TYPE_PGFORMULATEDPRODUCT);
	        slCMTypes.add(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY);
	        StringList slCSTypes = new StringList();
	        slCSTypes.add(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
	        slCSTypes.add(pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT);
	        slCSTypes.add(pgV3Constants.TYPE_PGRAWMATERIAL);
	        slCSTypes.add(pgV3Constants.TYPE_PGMASTERRAWMATERIAL);
	        slCSTypes.add(pgV3Constants.TYPE_PGPACKINGMATERIAL);
	        slCSTypes.add(pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL);
	        slCSTypes.add(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY);
	        if (((context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_SPECREADERCONTRACTMANUFACTURER)) && slCMTypes.contains(strType)) || ((context
	          .isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER) || context.isAssigned(pgV3Constants.ROLE_SPECREADERSUPPLIER)) && slCSTypes.contains(strType)))
	          isStructured = true; 
	      } 
	    } catch (Exception e) {
	      throw new MatrixException(e);
	    } 
	    return isStructured;
	  }
	  
	  /** This Method checks whether the given input part is Structured or not
	 * @param strType
	 * @param strCSSType
	 * @return
	 * @throws MatrixException
	 */
	public boolean checkForStructuredType(String strType, String strCSSType) throws MatrixException {
	    boolean isStructured = false;
	    try {
	      isStructured = checkIfStructured(strType, strCSSType, (String)null);
	    } catch (Exception e) {
	      throw new MatrixException(e);
	    } 
	    return isStructured;
	  }
	  
	  /**
	 * @param strType
	 * @param strCSSType
	 * @param strOriginatingSource
	 * @return
	 * @throws MatrixException
	 */
	private boolean checkIfStructured(String strType, String strCSSType, String strOriginatingSource) throws MatrixException {
	    boolean isStructured = false;
	    try {
	      StringList slDSOTypes = new StringList();
	      slDSOTypes.add(pgV3Constants.TYPE_PGCONSUMERDESIGNBASIS);
	      slDSOTypes.add(pgV3Constants.TYPE_PGBASEFORMULA);
	      slDSOTypes.add(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGINNERPACKUNITPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGMASTERINNERPACKUNITPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGMASTERCUSTOMERUNITPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGTRANSPORTUNITPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGMASTERCONSUMERUNITPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGCONSUMERUNITPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGPROMOTIONALITEMPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGMASTERPACKAGINGASSEMBLYPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGMASTERPACKAGINGMATERIALPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
	      slDSOTypes.add(pgV3Constants.TYPE_PGONLINEPRINTINGPART);
	      slDSOTypes.add(pgV3Constants.TYPE_SHAREDTABLE);
	      slDSOTypes.add(pgV3Constants.TYPE_FABRICATEDPART);
	      slDSOTypes.add(pgV3Constants.TYPE_FORMULATIONPART);
	      slDSOTypes.add(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
	      slDSOTypes.add(pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART);
	      slDSOTypes.add(pgV3Constants.TYPE_DEVICEPRODUCTPART);
	      slDSOTypes.add(pgV3Constants.TYPE_MASTERPRODUCTPART);
	      slDSOTypes.add(pgV3Constants.TYPE_MASTERRAWMATERIALPART);
	      slDSOTypes.add(pgV3Constants.TYPE_RAWMATERIALPART);
	      slDSOTypes.add(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART);
	      slDSOTypes.add(pgV3Constants.TYPE_SOFTWAREPART);
	      StringList slCSSTypes = new StringList();
	      slCSSTypes.add(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
	      slCSSTypes.add(pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT);
	      slCSSTypes.add(pgV3Constants.TYPE_PGFORMULATEDPRODUCT);
	      slCSSTypes.add(pgV3Constants.TYPE_PGAPPROVEDSUPPLIERLIST);
	      slCSSTypes.add(pgV3Constants.TYPE_PGRAWMATERIAL);
	      slCSSTypes.add(pgV3Constants.TYPE_PGMASTERRAWMATERIAL);
	      slCSSTypes.add(pgV3Constants.TYPE_PGPACKINGMATERIAL);
	      slCSSTypes.add(pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL);
	      slCSSTypes.add(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY);
	      slCSSTypes.add(pgV3Constants.TYPE_PGMASTERRAWMATERIAL);
	      if (slDSOTypes.contains(strType) || (slCSSTypes.contains(strType) && (strCSSType
	        .contains("_") || STR_DSO.equals(strOriginatingSource))))
	        isStructured = true; 
	    } catch (Exception e) {
	      throw new MatrixException(e);
	    } 
	    return isStructured;
	  }
	  
	  /**
	 * @param strVerifyString
	 * @return
	 */
	public static boolean validateString(String strVerifyString) {
		    boolean isStringDesired = false;
		    if (null != strVerifyString && !"".equals(strVerifyString) && !"null".equalsIgnoreCase(strVerifyString))
		      isStringDesired = true; 
		    return isStringDesired;
		  }
	  
	  /**This method checks whether the input Part has Master Connected or not
	 * @param context
	 * @param strType
	 * @param dmo
	 * @return
	 */
	public boolean hasMaster(Context context, String strType, DomainObject dmo) {
		  
		 boolean havingMaster = false;
		 try {
			 if (strType.equals(pgV3Constants.TYPE_PGRAWMATERIAL) || strType
				        .equals(pgV3Constants.TYPE_PGFINISHEDPRODUCT) || strType
				        .equals(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY) || strType
				        .equals(pgV3Constants.TYPE_PGPACKINGMATERIAL)) {
				    	  havingMaster = dmo.hasRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGMASTER, true);
				      }
		 }catch(Exception e) {
			 outLog.print("Exception in hasMaster method"+e+"\n");
			 outLog.flush();
		 }
	      
	      return havingMaster;
		  
	  }
	  
	  /**This method checks whether the View is available for Manufacturer or not
	 * @param context
	 * @param strType
	 * @param dmo
	 * @param strView
	 * @param strCSSType
	 * @param strContractPackagingTypes
	 * @param mpPersonData
	 * @return
	 */
	public boolean checkViewAvailableforManufacturer(Context context, String strType, DomainObject dmo, String strView, String strCSSType, String strContractPackagingTypes, Map mpPersonData) {
		  boolean viewAvailable = false;
		  MapList mlPlantsToPD=null;
		  StringList slRelSelect = new StringList(2);
	      slRelSelect.add(pgV3Constants.SELECT_ID);
	      slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
		  try {
		   if (strView.equalsIgnoreCase(STR_CONSOLIDATEDPACKAGING) || strView
			          .equalsIgnoreCase(STR_CONTRACTPACKAGING)) {
			          if (strView.equalsIgnoreCase(STR_CONTRACTPACKAGING)) {
			            if ((strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFORMULATEDPRODUCT) && strCSSType
			              .contains(pgV3Constants.SYMBOL_HYPHEN)) || strContractPackagingTypes.contains(strType)) {
			              DomainObject doPersonObject = DomainObject.newInstance(context, (String)mpPersonData.get(DomainConstants.SELECT_ID));
			              StringList slPlants = doPersonObject.getInfoList(context, "to[" + pgV3Constants.RELATIONSHIP_MEMBER + "].from.name");
			              mlPlantsToPD = dmo.getRelatedObjects(context, DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, pgV3Constants.TYPE_PLANT, new StringList("name"), slRelSelect, true, false, (short)1, null, "");
			             String strIsAutToUse = "";
			              if (null != mlPlantsToPD && mlPlantsToPD.size() > 0) {
			                for (int i = 0; i < mlPlantsToPD.size(); i++) {
			                  Map mPlantData = (Map)mlPlantsToPD.get(i);
			                  String strPlant = (String)mPlantData.get("name");
			                  if (slPlants.contains(strPlant)) {
			                    strIsAutToUse = (String)mPlantData.get("attribute[" + pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE + "]");
			                    if (UIUtil.isNotNullAndNotEmpty(strIsAutToUse) && strIsAutToUse
			                      .equalsIgnoreCase(pgV3Constants.CAPS_TRUE)) {
			                      viewAvailable = true;
			                      break;
			                    } 
			                  } 
			                }  
			                mlPlantsToPD.clear();
			              }
			              
			            } 
			          } else if (strView.equalsIgnoreCase(STR_CONSOLIDATEDPACKAGING)) {
			            if (strCSSType.contains("_") && (strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFINISHEDPRODUCT) || strType
			              .equalsIgnoreCase(pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT) || strType
			              .equalsIgnoreCase(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY))) {
			            	viewAvailable = true; 
			            }
			              
			            if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strType)) {
			            	 viewAvailable = true;
			            }
			              
			          } 
			        }
		  }catch(Exception e) {
			  outLog.print("Exception in method checkViewAvailableforManufacturer"+e+"\n");
			  outLog.flush();
		  }
		   return viewAvailable;
	  }
	  
	  /** This method checks whether the View is available for Supplier or not
	 * @param strType
	 * @param strView
	 * @param strCSSType
	 * @param strOriginatingSource
	 * @return
	 */
	public boolean checkViewAvailableforSupplier(String strType, String strView, String strCSSType, String strOriginatingSource) {
		  boolean viewAvailable = false;
		  if (strView.equalsIgnoreCase(STR_SUPPLIER)) {
	          if (strCSSType.contains("_") && (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strType) || pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT
	            .equals(strType) || pgV3Constants.TYPE_PGRAWMATERIAL
	            .equals(strType) || pgV3Constants.TYPE_PGMASTERRAWMATERIAL
	            .equals(strType) || pgV3Constants.TYPE_PGPACKINGMATERIAL
	            .equals(strType) || pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL
	            .equals(strType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY
	            .equals(strType))) {
	        	  viewAvailable = true;
	          }
	            
	          if (pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART.equals(strType) || pgV3Constants.TYPE_PGPROMOTIONALITEMPART
	            .equals(strType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART
	            .equals(strType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART
	            .equals(strType) || pgV3Constants.TYPE_PGONLINEPRINTINGPART
	            .equals(strType) || pgV3Constants.TYPE_FABRICATEDPART
	            .equals(strType) || pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART
	            .equals(strType) || pgV3Constants.TYPE_RAWMATERIALPART
	            .equals(strType) || (pgV3Constants.TYPE_PGRAWMATERIAL
	            .equals(strType) && STR_DSO
	            .equals(strOriginatingSource)))
	          {
	        	  viewAvailable = true; 
	          }
	            
	        } 
		  return viewAvailable;
	  }
	  
	  /**This method checks the View available for Contract Packaging
	 * @param context
	 * @param strType
	 * @param strCSSType
	 * @param strContractPackagingTypes
	 * @param dmo
	 * @param mpPersonData
	 * @return
	 */
	public boolean checkViewAvailableforContractPackaging(Context context, String strType, String strCSSType, String strContractPackagingTypes, DomainObject dmo, Map mpPersonData) {
		  boolean viewAvailable = false;
		  StringList slRelSelect = new StringList(2);
	      slRelSelect.add(pgV3Constants.SELECT_ID);
	      slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
	     try {
		  
		  if ((strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFORMULATEDPRODUCT) && strCSSType.contains("_")) || strContractPackagingTypes
		            .contains(strType)) {
	           
		            DomainObject doPersonObject = DomainObject.newInstance(context, (String)mpPersonData.get(DomainConstants.SELECT_ID));
		            StringList slPlants = doPersonObject.getInfoList(context, "to[" + pgV3Constants.RELATIONSHIP_MEMBER + "].from.name");
		            MapList mlPlantsToPD = dmo.getRelatedObjects(context, DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, pgV3Constants.TYPE_PLANT, new StringList("name"), slRelSelect, true, false, (short)1, null, "");
		            
		            String strIsAutToUse = "";
		            Map mPlantData = null;
		            String strPlant = null;
		            if (null != mlPlantsToPD && !mlPlantsToPD.isEmpty())
		              for (int i = 0; i < mlPlantsToPD.size(); i++) {
		                mPlantData = (Map)mlPlantsToPD.get(i);
		                strPlant = (String)mPlantData.get("name");
		                if (slPlants.contains(strPlant)) {
		                  strIsAutToUse = (String)mPlantData.get("attribute[" + pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE + "]");
		                  if (UIUtil.isNotNullAndNotEmpty(strIsAutToUse) && strIsAutToUse
		                    .equalsIgnoreCase(pgV3Constants.CAPS_TRUE)) {
		                    viewAvailable = true;
		                    break;
		                  } 
		                } 
		              }  
		          } 
	     }catch(Exception e){
	    	 outLog.print("Exception in checkViewAvailableforContractPackaging Method"+e+"\n");
	    	 outLog.flush();
	    	 
	     }
		  return viewAvailable;
	  }
	
	  
	  /**This Method checks whether the GenDoc is available for input part
	 * @param context
	 * @param objectId
	 * @param strView
	 * @return
	 * @throws Exception
	 */
	public boolean isPDFGenDocPresent(Context context, String objectId, String strView) throws Exception {
		  boolean bReturn = false;
		  	try {
		    
		    DomainObject dmo = DomainObject.newInstance(context, objectId);
		    String strType = dmo.getType(context);
		    String strPersonObjectId = PersonUtil.getPersonObjectID(context, context.getUser());
		    if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)|| context.isAssigned(pgV3Constants.ROLE_SPECREADERCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_SPECREADERSUPPLIER)) {
		    	String strCSSType = dmo.getAttributeValue(context, "pgCSSType");
		      boolean bStructured = strCSSType.contains("_");
		      boolean bCheckMaterial = checkMaterialOrNot(context, objectId);
		      boolean bCheckIfMaterial = checkProductIsMaterialOrNot(context, objectId);
		      String strOriginatingSource = dmo.getAttributeValue(context, pgV3Constants.ATTRIBUTE_PGORIGINATINGSOURCE);
		      if ("GenDoc".equalsIgnoreCase(strView) && (strType.equalsIgnoreCase(pgV3Constants.TYPE_DEVICEPRODUCTPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART))) {
		    	  bReturn = isCompanyHiRCompliant(context, strPersonObjectId, dmo, strView);
		      }
		        //Modified the code for 2022x.02 May CW Defect 47370 - Starts 
		      if (bCheckIfMaterial  && "GenDoc".equalsIgnoreCase(strView) && !strType.equalsIgnoreCase(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART) && !strType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPART) && !strType.equalsIgnoreCase(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART) && !strType.equalsIgnoreCase(pgV3Constants.TYPE_DEVICEPRODUCTPART)) {
				   //Modified the code for 2022x.02 May CW Defect 47370 - Ends
		    	  return true; 	  
		      }
		       
		      if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER)|| context.isAssigned(pgV3Constants.ROLE_SPECREADERCONTRACTMANUFACTURER)) {
		        if (strType.equalsIgnoreCase(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_DEVICEPRODUCTPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART)) {
		        	bStructured = true; 
		        }
		        if (bStructured && bCheckMaterial && "GenDoc".equalsIgnoreCase(strView) && !"pgFinishedProduct".equalsIgnoreCase(strType) && !"pgMasterFinishedProduct".equalsIgnoreCase(strType) && !strType.equalsIgnoreCase(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY)) {
		        	bReturn = checkForAuthToProduce(context, strType, strPersonObjectId, dmo);
		        } else if (!bStructured && bCheckMaterial && "ActualDoc".equalsIgnoreCase(strView)) {
		          bReturn = true;
		        } 
		      } else if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)|| context.isAssigned(pgV3Constants.ROLE_SPECREADERSUPPLIER) && (!bStructured && bCheckMaterial && "ActualDoc".equalsIgnoreCase(strView))) {
		       bReturn = true; 
		      } 
		    } else if ("GenDoc".equalsIgnoreCase(strView)) {
		      String strProductDataTypes = FrameworkProperties.getProperty("emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
		      String strNonStructuredTypes = FrameworkProperties.getProperty(context, "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
		      if (strProductDataTypes.contains(strType) || strNonStructuredTypes.contains(strType)) {
		        bReturn = checkProductIsMaterialOrNot(context, objectId);
		      } else {
		        bReturn = checkMaterialOrNot(context, objectId);
		      } 
		    } 
		    if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)|| context.isAssigned(pgV3Constants.ROLE_SPECREADERCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_SPECREADERSUPPLIER)) {
		      String strDSOType = dmo.getAttributeValue(context, "pgOriginatingSource");
		      if ("ActualDoc".equalsIgnoreCase(strView) && "DSO".equals(strDSOType)) {
		    	  bReturn = false; 
		      }
		         
		    } 
		  	}catch(Exception ex) {
		  		outLog.print("Exception in isPDFGenDocPresent method"+ex+"\n");
		  		outLog.flush();
		  	}
		    return bReturn;
		  }
	  
	  /**
	 * @param context
	 * @param objectId
	 * @return
	 */
	private boolean checkMaterialOrNot(Context context, String objectId) {
		    boolean isContexttPushed = false;
		    try {
		      Pattern relPattern = new Pattern("");
		      relPattern.addPattern(PropertyUtil.getSchemaProperty(context, "relationship_ReferenceDocument"));
		      StringList typeSelects = new StringList(1);
		      typeSelects.add("id");
		      typeSelects.add("format.file.name");
		      StringList relSelects = new StringList(1);
		      relSelects.add("id[connection]");
		      DomainObject domObj = DomainObject.newInstance(context, objectId);
		      String rev = domObj.getInfo(context, "revision");
		      String name = domObj.getInfo(context, "name");
		      String fileName = "LR_" + name + "." + rev;
		      String objectWhere = "name=='" + fileName + "' && revision=='Rendition'";
		      if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)|| context.isAssigned(pgV3Constants.ROLE_SPECREADERCONTRACTMANUFACTURER) || context.isAssigned(pgV3Constants.ROLE_SPECREADERSUPPLIER)) {
		       //Pushing the context to fetch the data
		    	  ContextUtil.pushContext(context);
		    	  isContexttPushed = true;
		      } 
		      MapList docList = domObj.getRelatedObjects(context, relPattern.getPattern(), pgV3Constants.SYMBOL_STAR, typeSelects, relSelects, false, true, (short)1, objectWhere, null, null, null, null);
		      if (isContexttPushed) {
		        ContextUtil.popContext(context);
		        isContexttPushed = false;
		      } 
		      if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(domObj.getInfo(context, "type")) && (docList == null || docList.size() == 0)) {
		        String strLegacyGCAS = domObj.getInfo(context, "attribute[pgLegacyGCAS]");
		        if (null != strLegacyGCAS && !"".equalsIgnoreCase(strLegacyGCAS)) {
		          fileName = "LR_" + strLegacyGCAS + "." + rev;
		          objectWhere = "name=='" + fileName + "' && revision=='Rendition'";
		          docList = domObj.getRelatedObjects(context, relPattern.getPattern(), pgV3Constants.SYMBOL_STAR, typeSelects, relSelects, false, true, (short)1, objectWhere, null, null, null, null);
		        } 
		      } 
		      if (docList != null && docList.size() > 0) {
		        Map mapData = (Map)docList.get(0);
		        String strTempFileName = (String)mapData.get("format.file.name");
		        if (!"".equalsIgnoreCase(strTempFileName) && null != strTempFileName) {
		        	 return true; 
		        }
		         
		        return false;
		      } 
		    } catch(Exception e) {
		    	outLog.print("Exception in checkMaterialOrNot method"+e+"\n");
		    	outLog.flush();
		    }
		    
		    
		    finally {
		      if (isContexttPushed) {
		    	  try {
		    		  ContextUtil.popContext(context);
		    		  isContexttPushed = false;  
		    	  }catch(Exception ex){
		    		 outLog.print("Exception while popping the context"+ex+"\n");
		    		 outLog.flush();
		    		  }
		        
		      } 
		    } 
		    return false;
		  }
	
	  /**
	 * @param context
	 * @param objectId
	 * @return
	 */
	private boolean checkProductIsMaterialOrNot(Context context, String objectId) {
		boolean isContextPushed = false;
		    try {
		    	
		    	//Pushing the context to fetch the data when the request comes from Spec Reader Application
			if(SPECREADER.equalsIgnoreCase(strRequestOriginatingSource)) {
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
			}
		      StringList typeSelects = new StringList(1);
		      typeSelects.add("id");
		      typeSelects.add("format.file.name");
		      StringList relSelects = new StringList(1);
		      relSelects.add("id[connection]");
		      StringList slbusSelect = new StringList(2);
		      slbusSelect.add("name");
		      slbusSelect.add("revision");
		      DomainObject domObj = DomainObject.newInstance(context, objectId);
		      Map mapAttributeInfo = domObj.getInfo(context, slbusSelect);
		      String rev = (String)mapAttributeInfo.get("revision");
		      String name = (String)mapAttributeInfo.get("name");
		      String fileName = "LR_" + name.trim() + "." + rev.trim();
		      String objectWhere = "name=='" + fileName.trim() + "' && revision==Rendition";
		      MapList docList = domObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT, pgV3Constants.TYPE_PGIPMDOCUMENT, typeSelects, relSelects, false, true, (short)1, objectWhere, null, null, null, null);
		      if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(domObj.getInfo(context, "type")) && (docList == null || docList.size() == 0)) {
		    	  String strLegacyGCAS = domObj.getInfo(context, "attribute[pgLegacyGCAS]");
		        if (null != strLegacyGCAS && !"".equalsIgnoreCase(strLegacyGCAS)) {
		          fileName = "LR_" + strLegacyGCAS + "." + rev;
		          objectWhere = "name=='" + fileName + "' && revision=='Rendition'";
		          docList = domObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT, pgV3Constants.SYMBOL_STAR, typeSelects, relSelects, false, true, (short)1, objectWhere, null, null, null, null);
		        } 
		      } 
		      if (docList != null && docList.size() > 0) {
		        Map mapData = (Map)docList.get(0);
		        String strTempFileName = (String)mapData.get("format.file.name");
		        if (UIUtil.isNotNullAndNotEmpty(strTempFileName)) {
		        	 return true; 
		        }
		        return false;
		      } 
		    } catch(Exception ex) {
		     outLog.print("Exception in method checkProductIsMaterialOrNot"+ex+"\n");
		     outLog.flush();
		    } finally {
				if(isContextPushed) {
					try {
						ContextUtil.popContext(context);
					}catch(Exception e) {
						outLog.print("Execption while popping context"+e+"\n");
						outLog.flush();
					}
					
				}
			}
		    return false;
		  }
	
	  /** This Method checks for HiRCompliant access
	 * @param context
	 * @param strPersonObjectId
	 * @param domObj
	 * @param strView
	 * @return
	 */
	public boolean isCompanyHiRCompliant(Context context, String strPersonObjectId, DomainObject domObj, String strView) {
		  try { 
		  DomainObject doPersonObject = DomainObject.newInstance(context, strPersonObjectId);
		    StringList slObjSelects = new StringList(2);
		    slObjSelects.add("name");
		    slObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHIRCOMPLAINT);
		    MapList mlCompanies = doPersonObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_MEMBER, DomainObject.TYPE_COMPANY, slObjSelects, null, true, false, (short)1, null, null, 0);
		    StringList slCompanyNames = domObj.getInfoList(context, "from[" + pgV3Constants.RELATIONSHIP_PGEXTERNALBUSINESSPARTNERS + "].to.name");
		    String strCompany = "";
		    String strHiRCompany = "";
		    Map<Object, Object> mpPersonDetails = new HashMap<>();
		    Map<Object, Object> mpCompany = new HashMap<>();
		    if (mlCompanies != null && mlCompanies.size() > 0)
		      for (int i = 0; i < mlCompanies.size(); i++) {
		        mpCompany = (Map<Object, Object>)mlCompanies.get(i);
		        strCompany = (String)mpCompany.get("name");
		        strHiRCompany = (String)mpCompany.get(pgV3Constants.SELECT_ATTRIBUTE_PGHIRCOMPLAINT);
		        if (slCompanyNames != null && slCompanyNames.size() > 0 && 
		          slCompanyNames.contains(strCompany) && "TRUE".equalsIgnoreCase(strHiRCompany))
		          return true; 
		        if ("supplier".equalsIgnoreCase(strView) && 
		          "TRUE".equalsIgnoreCase(strHiRCompany))
		          return true; 
		      }  
		  }catch(Exception e) {
			  outLog.print("Exception in isCompanyHiRCompliant method"+e+"\n");
			  outLog.flush();
		  }
		    return false;
		  }
	  
	  /**This Method checks for Authorize to Produce access for input Part
	 * @param context
	 * @param strType
	 * @param strPersonObjectId
	 * @param dmo
	 * @return
	 */
	private boolean checkForAuthToProduce(Context context, String strType, String strPersonObjectId, DomainObject dmo) {
		  boolean bReturn = false;
		  try {
		  StringList slRelSelect = new StringList(2);
		    slRelSelect.add("id");
		    slRelSelect.add("attribute[pgIsAuthorizedtoProduce]");
	         if ("pgFormulatedProduct".equalsIgnoreCase(strType) || strType.equalsIgnoreCase(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_DEVICEPRODUCTPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART)) {
		            if (strPersonObjectId != null && strPersonObjectId.length() > 0) {
		              DomainObject doPersonObject = DomainObject.newInstance(context, strPersonObjectId);
		              StringList slPlants = doPersonObject.getInfoList(context, "to[" + pgV3Constants.RELATIONSHIP_MEMBER + "].from.name");
		              StringList slPlantsToPD = dmo.getInfoList(context, "to[Manufacturing Responsibility].from.name");
		              boolean bHasPlant = false;
		              String strName = "";
		              if (slPlantsToPD != null && slPlantsToPD.size() > 0)
		                for (int i = 0; i < slPlantsToPD.size(); i++) {
		                  String strPlant = (String)slPlantsToPD.get(i);
		                  if (slPlants.contains(strPlant)) {
		                    bHasPlant = true;
		                    strName = strPlant;
		                    String where = "name =='" + strName + "'";
		                    MapList mpPlantLst = dmo.getRelatedObjects(context, DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, pgV3Constants.TYPE_PLANT, new StringList("name"), slRelSelect, true, false, (short)1, where, "");
		                    if (mpPlantLst != null && !mpPlantLst.isEmpty()) {
		                      Map mpTemp = (Map)mpPlantLst.get(0);
		                      String strIsAutToProduce = (String)mpTemp.get("attribute[pgIsAuthorizedtoProduce]");
		                      if (StringUtils.isNotBlank(strIsAutToProduce) && pgV3Constants.CAPS_TRUE.equalsIgnoreCase(strIsAutToProduce)) {
		                        bReturn = true;
		                        break;
		                      } 
		                    } 
		                  } 
		                }  
		            } 
		          } else {
		            bReturn = true;
		          } 
		  }catch(Exception e) {
			  outLog.print("Exception in checkForAuthToProduce method"+e+"\n");
			  outLog.flush();
		  }
	         return bReturn;
	  }
	//Added the code for Defect 47035,47370 - Ends
	
	/**
	 * @param context
	 * @param strReportName
	 * @param strUserName
	 */
	public void sendEmail(Context context, String strReportName, String strUserName) {
			
			Map mpMailData = null;
			String strFromEmailId="";
			String strToEmailIds ="";
			String strMessageBody="";
			String strSubject ="";
			
			try {
					mpMailData = getMailData(context, strReportName, strUserName);
					MxMessageSupport support = new MxMessageSupport();
					support.getSendMailInfo(context);
					Properties props = new Properties();
					props.put("mail.smtp.host", support.getSmtpHost());
					Session session = Session.getDefaultInstance(props, null);
					Message msg = new MimeMessage(session);
					 strFromEmailId = (String)mpMailData.get(STRFROMEMAILID);
					InternetAddress addressFrom = new InternetAddress(strFromEmailId);
					msg.setFrom(addressFrom);
					strToEmailIds = (String)mpMailData.get(STRTOEMAILID);
					msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(strToEmailIds));
					strSubject = (String)mpMailData.get(STRSUBJECT);
					msg.setSubject(strSubject);
					BodyPart messageBodyPart = new MimeBodyPart();
					strMessageBody =  (String)mpMailData.get(STRMESSAGEBODY);
					messageBodyPart.setContent(strMessageBody, STR_TEXT_HTML);
					if(UIUtil.isNotNullAndNotEmpty(strFromEmailId) && UIUtil.isNotNullAndNotEmpty(strToEmailIds)) {
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);
					msg.setContent(multipart);
					Transport.send(msg);
				}else {
					outLog.print("please check the emailIds Configured for the User \n");
					outLog.flush();
				}
				
			} catch (Exception e) {
				outLog.print("Exception in sendEmail Method"+e+"\n");
				outLog.flush();
				
			}

		}
		
	/**
	 * @param context
	 * @param strReportName
	 * @param strUserName
	 * @return
	 */
	private Map getMailData(Context context, String strReportName, String strUserName) {
		
		Map<String,String>mpMailData = new HashMap<String, String>();
		Map mpContextUserData = null;
		Map mpPLMAdminData = null;
		String strSubjectData[] = null;
		
		try {
			mpPLMAdminData = getPersonData(context, STR_PERSON_PLM_ADMIN);
			if(null != mpPLMAdminData && !mpPLMAdminData.isEmpty()) {
				mpMailData.put(STRFROMEMAILID, (String)mpPLMAdminData.get(pgV3Constants.SELECT_ATTRIBUTE_EMAILADDRESS));
			}
			 mpContextUserData = getPersonData(context, strUserName);
			if(null != mpContextUserData && !mpContextUserData.isEmpty()) {
				mpMailData.put(STRTOEMAILID, (String)mpContextUserData.get(pgV3Constants.SELECT_ATTRIBUTE_EMAILADDRESS));
			}
			String strSubLine = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.SubjectLine");
			strSubLine = strSubLine.replace(STR_SYMBOL_BRACKETS, strReportName);
			
			mpMailData.put(STRSUBJECT,strSubLine.trim());
			mpMailData.put(STRMESSAGEBODY,getMessageBody(context, strReportName));
		
			}catch(Exception ex) {
			outLog.print("Exception in getMailData method"+ex+"\n");
			outLog.flush();
				
		}
		return mpMailData;
		
	}


	/**
	 * @param context
	 * @param strUserName
	 * @return
	 */
	private Map getPersonData(Context context, String strUserName) {
			
			Map mpUserData = null;
			MapList mlUserInfo = null;
			StringList slSelect =new StringList();
			slSelect.add(DomainConstants.SELECT_ID);
			slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EMAILADDRESS);
			boolean isContextPushed = false;
			try {
				//Pushing the User Agent Context to find person data
				if( !pgV3Constants.PERSON_USER_AGENT.equals(context.getUser())) {
					ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					isContextPushed = true;
				}
				 mlUserInfo = DomainObject.findObjects(context,//context
						DomainConstants.TYPE_PERSON,//type
						strUserName,//name
						pgV3Constants.SYMBOL_HYPHEN,//revision
						DomainConstants.QUERY_WILDCARD,//owner
						pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
						DomainConstants.EMPTY_STRING,//whereExpression
						false,//expandType
						slSelect);//objectSelects
				
				int mlUserInfosize = mlUserInfo.size();
				for(int i=0;i<mlUserInfosize;i++) {
					mpUserData = (Map)mlUserInfo.get(i);
				}
				
			}catch(Exception ex){
			outLog.print("Exception in getPersonData method"+ex+"\n");
			outLog.flush();
			}finally {
				if(isContextPushed) {
					try {
						ContextUtil.popContext(context);
					}catch(Exception ex) {
						outLog.print("Exception while popping context in getPersonData Method \n");
						outLog.flush();
					}
					}
				}

			return mpUserData;
		}
		
	/**
	 * @param context
	 * @param strReportName
	 * @return
	 */
	private String getMessageBody(Context context, String strReportName) {
		
		String strMessageBody = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.BodyMessage");
		strMessageBody = strMessageBody.replace(STR_SYMBOL_BRACKETS, strReportName);
		StringBuilder sb = new StringBuilder();
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53492 - Starts
		sb.append(" <!DOCTYPE><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">" +
				"<style TYPE='text/css'>" +
				"body,table,tr,td {font-family: Verdana, Arial, Helvetica;font-size: 9pt;line-height: 12pt;text-align: left;}" +
				"td.display{font-weight: bold;color: white;background-color: #088CD8;}" +
				"td.hidden{background-color: transparent;border-width: 0em; line-height: 12pt;}" +
				"tr.hidden{background-color: transparent;border-width: 0em; line-height: 16px;height: 16px;}" +
				"</style></head><body><p style=\"text-align:center\"></p><table style=\"width:100%\" >" +
				"<tr text-align=\"center\">" +
				"<th  colspan=\"6\">"+strMessageBody+"</th>"
				+"</tr><tr><td><p>URL:");
				sb.append("<a href="+getUrl(context)+">DSM Report Page URL</a>"
				+"</p></td></tr></table><br></body></html>");
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53492 - Ends
		return sb.toString();
	}
		
		/**
		 * @param context
		 * @return
		 */
		private String getUrl(Context context) {
		String strUrl="";
		StringBuilder sbUrl = new StringBuilder();
		String strUrlPath = "";
		try {
				if(UIUtil.isNotNullAndNotEmpty(strRequestOriginatingSource) && STR_ENOVIA.equalsIgnoreCase(strRequestOriginatingSource)) {
				strUrl = EnoviaResourceBundle.getProperty(context,STRINGRESOURCEFILE, context.getLocale(), "emxCPN.BaseURL");
				strUrl = strUrl.substring(0, strUrl.indexOf(PATH_SEPARATOR+STR_COMMON));
				strUrl = String.valueOf(strUrl).trim();
				strUrlPath = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.Enovia.DSMReportLink");
				sbUrl.append(strUrl);
				sbUrl.append(strUrlPath);
			} else if(UIUtil.isNotNullAndNotEmpty(strRequestOriginatingSource) && SPECREADER.equalsIgnoreCase(strRequestOriginatingSource)) {
				strUrl = getSpecReaderURL(context);
				strUrl = String.valueOf(strUrl).trim();
				 strUrlPath = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.SpecReader.DSMReportLink");
				 sbUrl.append(strUrl);
				 sbUrl.append(strUrlPath);
			}
		}catch(Exception e) {
			outLog.println("Exception in getUrl method"+e+"\n");
			outLog.flush();
			
		}
		
		return sbUrl.toString();
		}
		
		
		/**
		 * @param context
		 * @return
		 * @throws FrameworkException
		 */
		private String getSpecReaderURL(Context context) throws FrameworkException {
			String objName = pgV3Constants.GLOBAL_SUB_NAME;
			StringList slSelect = new StringList();
			slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSPECREADERURL);
			String strURL = "";
			MapList mlLatestReleasePart = DomainObject.findObjects(context,//context
					TYPE_GLOBALSUBSCRIPTIONCOFIGURATION,//Type
					   objName,//name
					   DomainConstants.EMPTY_STRING,//revision
					   DomainConstants.QUERY_WILDCARD,//owner
					   //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
					   STR_VAULT_ADMINISTRATION,//vault
					 //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
					   null,//where expression
					   false,//expand type
					   slSelect);//object select
			if(!mlLatestReleasePart.isEmpty()) {
				Map<String,Object> mpConfigInfo = (Map<String, Object>) mlLatestReleasePart.get(0);
				strURL = (String) mpConfigInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSPECREADERURL);
			}
			return strURL;
		}
		
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
}	
