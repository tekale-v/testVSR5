package com.pg.dsm.custom;
/**================================================================================================================
 * Name: MassArtworkDownload
 * AUTHOR : DSM L4 (SOGETI)
 * Date : 21-Sep-2017
 * Description: This is a script to download the Artwork PDFs for Released/Obsolete Finished Product Code (FP and FPP) in specified Path.
 * The FPC names will be added in the Input file.
 * Username should have security access to the ART (pgArtwork/POA).   
 * 02/28/209 : Updated: for 2018x.0
 * =================================================================================================================*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.TreeMap;
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
import matrix.db.MxMessageSupport;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.matrixone.apps.domain.util.MqlUtil;
import java.io.OutputStream;
//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.awl.util.AWLExportPOAToExcel;
import org.apache.commons.io.FileUtils;
//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Starts
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkProperties;
import java.util.Calendar;
import java.util.TimeZone;
import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Ends
//Added code for defect id 42293 iText process for PDF stamping starts
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
//Added code for defect id 42293 iText process for PDF stamping ends

public class pgMassArtworkDownloadReport {
	//Code fix for Prod Issue Mass Artwork Download in Production where files requested by one user is being added to the export of another user if request is submitted simultaneously : Starts
	private  final String VAULT_ESERVICEPRODUCTION = pgV3Constants.VAULT_ESERVICEPRODUCTION;
	private  final String DSM_ORIGIN = "DSO";
	private  final String EMPLOYEE = "Employee";
	private  final String NON_EMP = "Non-Emp";
	private  final String EBP = "EBP";
	private  final String RESTRICTED = "Restricted";
	private  final String HIGHLY_RESTRICTED = "Highly Restricted";
	private PrintWriter outLog = null;
	PrintWriter outSuccessLog = null;
	PrintWriter outFailureLog = null;
	//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
	PrintWriter outExportPOAFailureLog = null;
	//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
	private static final String DATE_YMDHS = "yyyy.MM.dd.HH.mm.ss";
	private static final String DIRECTORY_ERR = "Could not create directory";	
	private static final String CATALINA_BASE="catalina.base";
	private static final String PATH_SEPARATOR="/";			
		
	private  long start_time=0; 
	private  final String RequestorIdEmpty= "is not present in DataBase";
	private  final String RequestorIdInactive= "is Inactive.";
	private  final String PDFFolder="File";
	private  final String TYPE_PGPOADOCUMENT = PropertyUtil.getSchemaProperty("type_pgPOADocument");
	private  StringList slPartId=new StringList();
	private  final String strRenameFailureSubstituteMsg = "File rename failed or User does not have checkout access to Object (For EBOM Substitute)";
	private  final String strRenameFailureMsg = "File rename failed or User does not have checkout access to Object";
	private  final String strNoArtworkAccess = "No access to Artwork ";
	private  final String strArtworkNotConnected = "Artwork not connected ";
	private  final String strArtworkNotConnectedOrNoAccess = "Final Artwork File not connected or user does not have access to it";
	private  final String strArtworkPDFNotConnectedOrNoAccess = "Artwork PDF not found or user does not have access to it";
	private  final String strNoArtworkState = "PDF not downloaded: Artwork is not in Release or Obsolete State";
	private  final String strNoConnectedFPP = "FPP not Connected";
	private  String strGlobalState=pgV3Constants.EMPTY_STRING;
	private  StringList slFileRenamedMainPart = new StringList();
	private  final StringList SL_REL_EBOM_SUBSTITUTION_SELECT = getRelSelectEBOMSubstitution();
	private  final StringList SL_OBJECT_SELECT = getObjectSelect();
	private  StringList slBOMChildObjectIds = new StringList();
	private  Map mpArtPack=new HashMap();
    private  MapList mlAllART =new MapList();
    private static final String STRINGRESOURCEFILE ="emxCPN";
    private final String TYPE_ARTWORKFILE = "Artwork File";
    private final String TYPE_PGDSOAFFECTEDFPPLIST = PropertyUtil.getSchemaProperty("type_pgDSOAffectedFPPList");
    private final String REL_PGDSOAFFECTEDFPPLIST = PropertyUtil.getSchemaProperty("relationship_pgDSOAffectedFPPList");
	//Code fix for Prod Issue Mass Artwork Download in Production where files requested by one user is being added to the export of another user if request is submitted simultaneously : Ends
	private final String REVISION_RENDITION = "Rendition";
		//Added the code for 22x.02 May CW Defect 49901 - Starts
	private String strRequestOriginatingSource = "";
	private static final String SPECREADER = "SpecReader";
	//Added the code for 22x.02 May CW Defect 49901 - Ends
    private  StringList getObjectSelect() {
		StringList slSelect = new StringList(20);		
		slSelect.add(DomainConstants.SELECT_ID);	
		slSelect.add(DomainConstants.SELECT_CURRENT);
		slSelect.add(DomainConstants.SELECT_REVISION);
		slSelect.add(DomainConstants.SELECT_NAME);
		slSelect.add("relationship");
		slSelect.add(DomainConstants.SELECT_TYPE);
		slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		slSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PARTSPECIFICATION+"].to.type");
		return slSelect;
	}
	//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
	private  StringList slPOACheck=new StringList();
	private static final String EXPORT_POA = "ExportPOA";
	//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
	//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
	private static final String PDFARTWORK = "PDFArtwork";
	//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- Ends
	//Added code for 2018x.6 Requirement 36716,36717 Generate Artwork Report by scheduled CTRLM Job Starts
	private static final String REALTIMEPROCESS = "RealTimeProcess";
	//Added code for 2018x.6 Requirement 36716,36717 Generate Artwork Report by scheduled CTRLM Job Ends
	
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
	
	
	/**
     * This method is to generate the PDF File for the manual entry from the user side
     * @param args
     * @return void
     * @throws Exception
     */
	public void generateMassArtworkDownloadReportEntry(Context context, String args[]) throws Exception {
		HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
	    String strUserName = (String) hmArgs.get("UserName");
	    String strPartNames	= (String) hmArgs.get("GCAS");
	    String strReportFileName = (String) hmArgs.get("ReportFileName");
	    String strReportObjectId = (String) hmArgs.get("ReportObjectId");
	    //Added code for Requirement: 31972-The report shall give the user the option to download Artwork files rather than default to Artwork Technical Specification- Starts
	    String strFinalArtwork = (String) hmArgs.get("FinalArtworkPDF");
	    //Added code for Requirement: 31972-The report shall give the user the option to download Artwork files rather than default to Artwork Technical Specification- Ends
	    //Added for POA Extract Requirement Id : 33653,34053,34054 --starts
	    String strExportPOA = (String) hmArgs.get(EXPORT_POA);
	    //Added for POA Extract Requirement Id : 33653,34053,34054 --ends
	    //Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
	    String strPDFArtwork = (String) hmArgs.get(PDFARTWORK);
	    //Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- Ends
	    //Added code for 2018x.6 Requirement 36716 Generate Artwork Report directly Starts
	    String strRealTimeProcess = (String) hmArgs.get(REALTIMEPROCESS);
	  //Added code for 2018x.6 Requirement 36716 Generate Artwork Report directly Ends
	  //Added the code for 22x.02 May CW Defect 49901 - Starts
	    strRequestOriginatingSource = (String) hmArgs.get("OriginatingSource");
	  //Added the code for 22x.02 May CW Defect 49901 - Ends
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames))){
	    	DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
			doObj.promote(context);
	    	//Modified for POA Extract Requirement Id : 33653,34053,34054 --starts
			//Modified for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
			generateMassArtworkDownloadReport(context,strUserName,strPartNames,strReportFileName,strReportObjectId,strFinalArtwork,strExportPOA,strPDFArtwork,strRealTimeProcess);
			//Modified for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
	       	//Modified for POA Extract Requirement Id : 33653,34053,34054 --ends
	       }
	   }	
	
	/**
     * This method is to generate the PDF File for the manual entry from the user side
     * @param args
     * @return void
     * @throws Exception
     */
		
	private void generateMassArtworkDownloadReport(Context context, String strUserName, String strPartNames,String strReportFileName, String strReportObjectId,String strFinalArtwork,String strExportPOA,String strPDFArtwork,String strRealTimeProcess) throws Exception{
		File fFolderPath = null;
		StringBuilder sbLogFolder = new StringBuilder();
		File fLogFolder = null;
		String strDirectoryNotCreated = "Could not create directory ";
		String strStartTime = null; 
		String strJVM = getJVMInstance();
		try {
			String sStringResourceFile="emxCPNStringResource"; 
			String sLanguage = context.getSession().getLanguage();
			FileReader fileReaderMQLGeneratedInputFile = null;
			BufferedReader buffReaderForMQLGeneratedInputFile = null;
			String line = "";
			StringList slCompleteRow = new StringList();
			String strInputFileLimit = i18nNow.getI18nString("emxCPN.MassArtworkDownload.InputFileName.Limit",sStringResourceFile, sLanguage);
			int intInputFileLimit = Integer.parseInt(strInputFileLimit);
			String strUserSecurityType = DomainConstants.EMPTY_STRING;
			String strUserOid = DomainConstants.EMPTY_STRING;
			String strFCFPPNames= DomainConstants.EMPTY_STRING;
			String strFCFPPName= DomainConstants.EMPTY_STRING;
			String strFCFPPNamesFromInputFile=null;
			String strFolder=DomainConstants.EMPTY_STRING;
			String strPdfFolder = DomainConstants.EMPTY_STRING;
			String strPdffileFolder = DomainConstants.EMPTY_STRING;
			String strWorkspace = DomainConstants.EMPTY_STRING;
			String strCurrent = DomainConstants.EMPTY_STRING;	
			String strFPPPartNames = DomainConstants.EMPTY_STRING;
			StringList slIndividualFCFPPName=new StringList();
			StringList slSelect = new StringList(10);			
			slSelect.add("id");	
			slSelect.add("current");
			slSelect.add("name");
			slSelect.add("revision");
			slSelect.add("type");
			slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			String strFCFPPObjectId = DomainConstants.EMPTY_STRING;
			String strFCFPPObjectType = DomainConstants.EMPTY_STRING;
			String strFCFPPObjectName = DomainConstants.EMPTY_STRING;
			String strFCFPPObjectRev = DomainConstants.EMPTY_STRING;
			String strFCFPPObjectstate = DomainConstants.EMPTY_STRING;
			String strFCFPPSecondLastObjectstate = "";			
			// Mass ArtWork PDF download Folder creation-- Starts 	
			BufferedWriter bwZipinFiles = null;
			File fMassPDFParentFolder = null;
			StringBuffer sbZipFileName = new StringBuffer();
			StringBuffer sbZipFolderName = new StringBuffer();
			StringBuffer sbMassArtWorkPDFFolder	= new StringBuffer();
			StringBuffer strCurrentDirectoryPath = new StringBuffer();
			long lupdateStartTime = System.currentTimeMillis();
			
			Date now = new Date();
			SimpleDateFormat smpdf = new SimpleDateFormat("MM-dd-yyyy");
			String strReportExtractedDate = smpdf.format(now);	
			String strUserName_temp = strUserName.replace(".","-");
			String strFileBaseNameFolder = "";
			if(UIUtil.isNotNullAndNotEmpty(strReportFileName)) {
				strFileBaseNameFolder = strReportFileName+"_"+strReportExtractedDate+"_"+strUserName_temp+"_"+lupdateStartTime;
			} else {
				strFileBaseNameFolder = "MassArtWorkDownloadRequest_"+strReportExtractedDate+"_"+strUserName_temp+"_"+lupdateStartTime;
			}
			sbZipFileName.append(strFileBaseNameFolder).append(".zip");
			//Added code for 2018x.6 Requirement 36716 Generate Mass Artwork Report directly Starts
			String configFilePath = DomainConstants.EMPTY_STRING;
			String configLOGFilePath = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess)){
				configFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.MassArtworkDownloadReport.Worksheet.FilePath");
				configLOGFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Log.FilePath");
			} else{
				configFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.MassArtworkDownloadReportCTRLMJob.Worksheet.FilePath");
				configLOGFilePath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.DSMReportCTRLMJob.Log.FilePath");
			}
			//Added code for 2018x.6 Requirement 36716 Generate Mass Artwork Report directly Starts
			sbMassArtWorkPDFFolder.append(configFilePath).append(java.io.File.separator);			
			fMassPDFParentFolder = new File(sbMassArtWorkPDFFolder.toString());
			
			if (!fMassPDFParentFolder.exists() && !fMassPDFParentFolder.mkdirs())  {
				throw new IOException(strDirectoryNotCreated + fMassPDFParentFolder);
			}
			strCurrentDirectoryPath.append(sbMassArtWorkPDFFolder.toString()).append(strFileBaseNameFolder).append(java.io.File.separator);
			fFolderPath = new File((new StringBuffer(strCurrentDirectoryPath.toString()).append(java.io.File.separator)).toString());
			if (!fFolderPath.exists() && !fFolderPath.mkdirs()) 
			{
				throw new IOException(strDirectoryNotCreated + fFolderPath);
			}
			// Mass ArtWork PDF download Folder creation-- Ends 
			sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
			fLogFolder = new File(sbLogFolder.toString());
			if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
				throw new IOException(strDirectoryNotCreated + fLogFolder);
			}
				//Log file for Mass Artwork Report
			strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());	 
			outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"MassArtworkDownloadLog.log",true));
			outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+" | JVM: "+strJVM+"--------\n");
			outLog.print("Parts: "+strPartNames+"\n");
			outLog.print("Report Object Id: "+strReportObjectId+"\n");
			outLog.flush();					
			
			// User Log File Creation -- Starts
			outSuccessLog = new PrintWriter(new FileOutputStream(fFolderPath.toString()+ File.separator  + strUserName + "_DownloadSuccessLog_"+String.valueOf(System.currentTimeMillis())+".log",true));
			outFailureLog = new PrintWriter(new FileOutputStream(fFolderPath.toString()+ File.separator  + strUserName + "_DownloadFailureLog_"+String.valueOf(System.currentTimeMillis())+".log",true));
			outSuccessLog.print("TYPE|NAME|REVISION:ArtworkType|ArtworkName|ArtworkRev|ArtworkPDFNewName"+ "\n" );
			outSuccessLog.flush();
			//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
			if(strExportPOA.equalsIgnoreCase("true")){
				outExportPOAFailureLog = new PrintWriter(new FileOutputStream(fFolderPath.toString()+ File.separator  +"ExportPOAFailureLog_"+System.currentTimeMillis()+".log",true));
			}
			//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
			//Added code for Requirement Id: 32058: Mass Artwork Download tool shall allow user to input PMP and AFPP to download POA--Starts 
			//Modified for POA Extract Requirement Id : 33653,34053,34054
			//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
			strFPPPartNames = getConnectedFPP(context,strPartNames,strUserName,fFolderPath.toString(),strFinalArtwork,strExportPOA,strPDFArtwork);
			//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
			strPartNames = strFPPPartNames;	
			//Added code for Requirement Id: 32058: Mass Artwork Download tool shall allow user to input PMP and AFPP to download POA--Ends
			slIndividualFCFPPName = FrameworkUtil.split(strPartNames, ",");
			int slIndividualFCFPPNameSize =slIndividualFCFPPName.size();
			if(slIndividualFCFPPNameSize>0){
				if(slIndividualFCFPPNameSize<=intInputFileLimit){
					processInputParts(context,slIndividualFCFPPName,strUserName,strFinalArtwork,strExportPOA,fFolderPath.toString(),strPDFArtwork);
					
				} else {
					outFailureLog.append("Input file has exceeded the limit of " +intInputFileLimit + " Part Codes. Please correct and re-execute.");
				}
			}
			//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
			if(!slPOACheck.isEmpty() && strExportPOA.equalsIgnoreCase("true")) {
				getPOAStructure(context,slPOACheck,fFolderPath.toString());
			} else {
				if(strExportPOA.equalsIgnoreCase("true")) {
					outExportPOAFailureLog.append("NO POA connected for Export or No Access").append("\n");
					outExportPOAFailureLog.flush();
				}
			}
			//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
			//Zip file creation : Start
			if (fFolderPath.exists()) {
				sbZipFolderName.append(sbMassArtWorkPDFFolder.toString()).append(java.io.File.separator).append(sbZipFileName);
				zipFiles(sbZipFolderName.toString(), fFolderPath.toString());
			} 
			//Zip file creation : End
			// code to create the object and checking the .zip file in that object	
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
			createDSMReportObject(context,sbMassArtWorkPDFFolder.toString(),sbZipFileName.toString(),strReportObjectId,strUserName);				
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
			long TotalExecutionTime= System.currentTimeMillis() - start_time;
			long TotalTimeInMinutes = (TotalExecutionTime / 1000) / 60;
			long TotalTimeInSeconds = (TotalExecutionTime / 1000);
										
			outSuccessLog.close();
			outFailureLog.close();
			//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
			if(strExportPOA.equalsIgnoreCase("true")){
				outExportPOAFailureLog.close();
			}
			//Added for POA Extract Requirement Id : 33653,34053,34054 --ends				
			//COMMENT for local testing
			deleteFiles(context,fFolderPath.toString());				
			//Added code to fix - Mount point issues in prod -Prod issue -Ends
			String strEndTime = null; 
			strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
			outLog.print("Report completed for: "+strUserName+": " +sbZipFileName+ "-------\n");
			outLog.print("-------Time completed: "+strEndTime+"-------\n");	
			outLog.print("-------\n");
			outLog.flush();
			outLog.close();
		} catch (Exception e) {			
			outLog.print("Exception in generateMassArtworkDownloadReport: "+e+"\n");
			outLog.flush();	
		} 
	}

	/**
     * This method creates createDSMReportObject
     * @param args
     * @return void
     * @throws Exception
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
			outLog.print("Exception in createDSMReportObject: "+e+"\n");
			outLog.flush();	
		 }
		
	}
	
	/**
	 * This method will get the files in the srcDir and zip them in name full file name provided in zipFile.
	 * @param zipFile holds the name for the zip file to be created.
	 * @param srcDir holds the folder where the files to be zipped are available
	 * @return Nothing 
	 */
	public  void zipFiles(String zipFile, String srcDir) throws Exception 
	{
		FileInputStream fis = null;
		ZipOutputStream zos = null;
		try                 
		{                   
			File srcFile = new File(srcDir);
			File[] files = srcFile.listFiles(); 
			zos = new ZipOutputStream(new FileOutputStream(zipFile));
			int ilength = 0 ;
			
			byte[] buffer;
			for (int iCount = 0; iCount < files.length; iCount++) 
			{
				// create byte buffer
				buffer = new byte[1024];
				fis = new FileInputStream(files[iCount]);
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
			outLog.print("Exception in zipFiles: "+e+"\n");
			outLog.flush();
		}
		finally
		{
			zos.close();			
		}
	}
	
	 /**
     * This method is to generate the Mass ArtWork Download PDF for the input .txt file which will be browsed by the user 
     * @param args
     * @return void
     * @throws Exception
     */
public void generateMassArtworkDownloadReportForInputFile(Context context, String args[]) throws Exception {
	FileReader fileReaderMQLGeneratedInputFile = null;
	BufferedReader buffReaderForMQLGeneratedInputFile = null;
	StringList slCompleteRow = new StringList();
	String strPartOrCALabelFromInputFile= DomainConstants.EMPTY_STRING;
	String strChangeActions = DomainConstants.EMPTY_STRING;
	String strCAName = DomainConstants.EMPTY_STRING;
	String strPartNames = DomainConstants.EMPTY_STRING;
	String line = "";
	HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
	//Code-Fix for Defect 24821(DSM Report- Input file exceed the limit error) --Start 
	File file  = (File) hmArgs.get("DSMReportFile");
	String strReportFileName = (String) hmArgs.get("ReportFileName");
	String strReportObjectId = (String) hmArgs.get("ReportObjectId");
	String strUserName = (String) hmArgs.get("UserName");
	//Added code for Requirement: 31972-The report shall give the user the option to download Artwork files rather than default to Artwork Technical Specification- Starts
	String strFinalArtwork = (String) hmArgs.get("FinalArtworkPDF");
	//Added code for Requirement: 31972-The report shall give the user the option to download Artwork files rather than default to Artwork Technical Specification- Ends
	//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
	String strExportPOA = (String) hmArgs.get(EXPORT_POA);
	//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
	fileReaderMQLGeneratedInputFile = new FileReader(file);
	buffReaderForMQLGeneratedInputFile = new BufferedReader(fileReaderMQLGeneratedInputFile);
	String finalVal = DomainConstants.EMPTY_STRING;
	while ((line = buffReaderForMQLGeneratedInputFile.readLine()) != null) {
		if(line != null && line.length()>0){
			if(!line.contains(","))
				finalVal += line + ",";
			else 
				finalVal += line;
		}	
	}
	StringList strFinalList = FrameworkUtil.split(finalVal,",");
	finalVal = strFinalList.toString();
	finalVal = finalVal.replace("[", "").replace("]", ""); 
	strPartNames=finalVal;
	if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames))){
		StringList slGCAS = FrameworkUtil.split(strPartNames, ",");
		StringList slChangeAction = FrameworkUtil.split(strChangeActions, ",");
		try { 
			HashMap hmArgs1 = new HashMap();
			hmArgs1.put("UserName",strUserName);
			hmArgs1.put("GCAS",strPartNames);
			hmArgs1.put("ReportFileName",strReportFileName);
			hmArgs1.put("ReportObjectId",strReportObjectId);
			hmArgs1.put("FinalArtworkPDF",strFinalArtwork);
			//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
			hmArgs1.put(EXPORT_POA,strExportPOA);
			//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
			BackgroundProcess backgroundProcess = new BackgroundProcess();
			backgroundProcess.submitJob(context, "com.pg.dsm.custom.pgMassArtworkDownloadReport", "generateMassArtworkDownloadReportEntry", JPO.packArgsRemote(hmArgs1) , (String)null);
		} catch(Exception ex) {
			ContextUtil.abortTransaction(context);
			outLog.print("Exception in generateMassArtworkDownloadReportForInputFile: "+ex+"\n");
			outLog.flush();			
			throw ex;
		}
	}
}

/**
 * This method copies user input file
 * @param file
 * @return void
 * @throws Exception
 */
	public void copy(File src, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(dest); 
			byte[] buf = new byte[1024]; 
			int bytesRead; 
			while ((bytesRead = is.read(buf)) > 0) 
			{ 
				os.write(buf, 0, bytesRead);
			} 
		}	
		catch(Exception e)
		{
			outLog.print("Exception in copy: "+e+"\n");
			outLog.flush();			
		
		} finally {
			is.close();
			os.close();
		}
	}
	
/**
 * Description: This method is used for getting Latest Release revision or Latest Obsolete revision of FPP/FP.
 * @param context
 * @return Map containing Latest Release or Obsolete FPC object
 * Returns: Map containing Latest Release or Obsolete FPC object
 */
	public Map getCheckAllRevision(Context context, String  strFCFPP ,String strcurrentGlobalState) throws Exception {
		Map mpLatestFPFPP=null;
		String sWhereExp = "(attribute[pgOriginatingSource]!=Enginuity) && current=="+strcurrentGlobalState;
		short sQueryLimit=0;
		String strCurrent=pgV3Constants.EMPTY_STRING;
		String strRevision=pgV3Constants.EMPTY_STRING;
		String strid=pgV3Constants.EMPTY_STRING;
		Map maRevInfo=null;
		StringList slCheckFPFPP=new StringList();
		slCheckFPFPP.add(DomainConstants.SELECT_NAME);	
		slCheckFPFPP.add(DomainConstants.SELECT_TYPE);
		slCheckFPFPP.addElement(DomainConstants.SELECT_ID);
		slCheckFPFPP.addElement(DomainConstants.SELECT_CURRENT);
		slCheckFPFPP.addElement(DomainConstants.SELECT_REVISION);
		slCheckFPFPP.addElement("last.name");
		slCheckFPFPP.addElement("last.previous.current");
		slCheckFPFPP.addElement("last.current");
		StringBuffer sbTypePattern = new StringBuffer();		    
		sbTypePattern.append(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
		sbTypePattern.append(",");
		sbTypePattern.append(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
		NavigableMap mapLatest=new TreeMap();
		MapList maRevisionsInfol = DomainObject.findObjects(context,
				sbTypePattern.toString(),
				strFCFPP,
				"*",
				"*",
				pgV3Constants.VAULT_ESERVICEPRODUCTION,
				sWhereExp.toString(),
				"",
				true,
				(StringList) slCheckFPFPP,
				sQueryLimit);
		if( maRevisionsInfol!=null && !maRevisionsInfol.isEmpty() ) {
			for(int i=0;i<maRevisionsInfol.size();i++) {
				maRevInfo=(Map) maRevisionsInfol.get(i);
				strCurrent=(String) maRevInfo.get(DomainConstants.SELECT_CURRENT);
				strRevision=(String) maRevInfo.get(DomainConstants.SELECT_REVISION);
				mapLatest.put(strRevision, maRevInfo);
			}
				mpLatestFPFPP=(Map)mapLatest.get(mapLatest.lastKey());
		} 
		return mpLatestFPFPP;
	}


	/**
	 * Description: This method is used for getting EBOM Substitutes Part Ids, the Part Ids are passed in CheckOutEBOMSubstitute method
	 * This method also calls getConnectedEBOMChild where Checkout happens for Regular Part. 
	 * @return Void
	 * @param args
	 */
	public void getArtWorkPDF(Context context, DomainObject dobjFCFPP, String strFCFPPObjectId, String strFCFPPObjectType, String strFCFPPName, String strFCFPPObjectRev, String strUserName,String strFolder,String strFinalArtwork, String strExportPOA,String strPDFArtwork) {
		try{
			String strTypeSubstitutePOAArt=DomainConstants.EMPTY_STRING;
			String strNameSubstitutePOAArt=DomainConstants.EMPTY_STRING;
			String strDocumentOrPartType=DomainConstants.EMPTY_STRING;		
			StringList slPartIds = new StringList();
			StringList slPartTempId = new StringList();
			Map mpBOMSpec=null;
			boolean bArtworkPresent;
			boolean bfileName = false;
			StringBuffer sbRelPattern = new StringBuffer();		    
			sbRelPattern.append(pgV3Constants.RELATIONSHIP_EBOM);
			sbRelPattern.append(",");
			sbRelPattern.append(pgV3Constants.RELATIONSHIP_PARTSPECIFICATION);														
			StringBuffer sbTypePattern = new StringBuffer();		    
			sbTypePattern.append(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
			sbTypePattern.append(",");
			sbTypePattern.append(pgV3Constants.TYPE_FINISHEDPRODUCTPART);			
			
			//Added logic to remove Circular References --Begin		
			slPartId=new StringList();
			slBOMChildObjectIds=new StringList();
			//Modified for POA Extract Requirement Id : 33653,34053,34054 --starts
			//Modified for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
			slPartIds =	getConnectedEBOMChild(context,strFCFPPObjectId,strUserName,strFolder,dobjFCFPP,strFinalArtwork,strExportPOA,strPDFArtwork);
			//Modified for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork-ends 
			//Modified for POA Extract Requirement Id : 33653,34053,34054 --ends
			//Added logic to remove Circular References --End
			//Modified for POA Extract Requirement Id : 33653,34053,34054 --starts
			//Modified for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
			checkOutEBOMSubstitute(context,slPartIds,strFCFPPObjectType,strFCFPPName,strFCFPPObjectRev,strUserName,strFolder,strFinalArtwork,strExportPOA,strPDFArtwork);
			//Modified for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork-ends
			//Modified for POA Extract Requirement Id : 33653,34053,34054 --ends
		}catch(Exception e){
			outLog.print("Exception in getArtworkPDF: "+e+"\n");
			outLog.flush();					
		}
	}


	/**
	 * Description: This Recursive Method is used to get EBOM Child one level at a time.In order to prevent Circular References.
	 * @return EBOM Substitute Parts Object Ids
	 * @param context
	 */
	private StringList getConnectedEBOMChild(Context context, String strObjectID,String strUserName, String strFolder,DomainObject dobjFPCOrPart,String strFinalArtwork, String strExportPOA,String strPDFArtwork) {
				
		MapList mlRelatedEBOMPart=null;		
		try{	
			String sStringResourceFile="emxCPNStringResource"; 
			String sLanguage = context.getSession().getLanguage();
			StringBuffer sbRelPattern = new StringBuffer();
			sbRelPattern.append("EBOM");		
			StringBuffer sbTypePattern = new StringBuffer();
			sbTypePattern.append(pgV3Constants.TYPE_PGARTWORK);
			sbTypePattern.append(",");
			sbTypePattern.append(pgV3Constants.TYPE_POA);			
			String strBOMIntermediateTypes=i18nNow.getI18nString("emxCPN.MassArtworkDownload.MassArtWork.BOM.IntermediateTypes",sStringResourceFile, sLanguage);
			String strBOMMaterialTypes=i18nNow.getI18nString("emxCPN.MassArtworkDownload.MassArtWork.BOM.MaterialTypes",sStringResourceFile, sLanguage);
			Map mpFPCObject= (Map)dobjFPCOrPart.getInfo(context, SL_OBJECT_SELECT);
			String strFPCObjType= (String)mpFPCObject.get("type");
			String strFPCObjName= (String)mpFPCObject.get("name");
			String strFPCObjRevision= (String)mpFPCObject.get("revision");
			
			String strMaterialType= DomainConstants.EMPTY_STRING;
			String strMaterialName= DomainConstants.EMPTY_STRING;
			String strMaterialRevision= DomainConstants.EMPTY_STRING;
			Short sRecursionLevel =1;
			//Added code for defect id: 31958--Starts
			//ContextUtil.pushContext(context, "User Agent", null,context.getVault().getName());
			//Added code for defect id: 31958--Ends
			DomainObject dobjDymanicEBOMOrPart= DomainObject.newInstance(context,strObjectID);
			boolean isAcess = accessCheck(context, strUserName, dobjDymanicEBOMOrPart);
			if(isAcess){
				mlRelatedEBOMPart = (MapList)dobjDymanicEBOMOrPart.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM, pgV3Constants.TYPE_PART, SL_OBJECT_SELECT, SL_REL_EBOM_SUBSTITUTION_SELECT, false,true,sRecursionLevel,null, null, 0);
				//Added code for defect id: 31958--Starts
				//ContextUtil.popContext(context);
				//Added code for defect id: 31958--Ends
				if(mlRelatedEBOMPart.size()>0){
					Iterator itr = mlRelatedEBOMPart.iterator();
					while (itr.hasNext()){
						Map mpBOMChild= (Map)itr.next();				
						String strBOMChildType= (String)mpBOMChild.get("type");
						String strBOMChildId=(String)mpBOMChild.get("id");
						String strBOMChildName=(String)mpBOMChild.get("name");
						String strBOMChildRevision=(String)mpBOMChild.get("revision");
						//Added code for defect id: 31958--Starts
						//Storing EBOM Substitute Parts Ids in a List --Begin
						if(mpBOMChild.get("frommid[EBOM Substitute].to.id") instanceof StringList){
							StringList  slPartTempId = (StringList)mpBOMChild.get("frommid[EBOM Substitute].to.id");	
							if(slPartTempId!=null){						
								for(int j=0;j<slPartTempId.size();j++){
									String strPartId=(String)slPartTempId.get(j);
									slPartId.add(strPartId);
								}
							}
						}
						else if((mpBOMChild.get("frommid[EBOM Substitute].to.id"))!=null){
							String 	strPartId = (String)mpBOMChild.get("frommid[EBOM Substitute].to.id");
							if(!slPartId.contains(strPartId));
							slPartId.add(strPartId);					
						}
						//Storing EBOM Substitute Parts Ids in a List --End
	
						if(!slBOMChildObjectIds.contains(strBOMChildId)){
							slBOMChildObjectIds.add(strBOMChildId);					
							if(strBOMMaterialTypes.indexOf("~"+strBOMChildType+"~")>-1)
							{
								DomainObject doObjMaterial = DomainObject.newInstance(context,strBOMChildId);
								Map mpMaterial = (Map)doObjMaterial.getInfo(context, SL_OBJECT_SELECT);
								strMaterialType= (String)mpMaterial.get("type");
								strMaterialName=(String)mpMaterial.get("name");
								strMaterialRevision=(String)mpMaterial.get("revision");
								//Added code for defect id- 31958--Starts
								boolean isAccess = accessCheck(context, strUserName, doObjMaterial);
								if(isAccess){
									//Added code for defect id- 31958--Ends
									MapList mlRelatedART = (MapList)doObjMaterial.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PARTSPECIFICATION, sbTypePattern.toString(), SL_OBJECT_SELECT, SL_REL_EBOM_SUBSTITUTION_SELECT, false,true,sRecursionLevel,null, null, 0);
		                            if(mlRelatedART.size()>0){
										Iterator itrART = mlRelatedART.iterator();
										while (itrART.hasNext()){
											Map mpART = (Map)itrART.next();
											String strARTObjectId= (String)mpART.get("id");							
											mpArtPack.put(strARTObjectId, doObjMaterial);
											if(!mlAllART.contains(mpART))
												mlAllART.add(mpART);	
											}
									} else {
										//Artwork not connected to Material Part
										outFailureLog.append(strFPCObjType).append("|").append(strFPCObjName).append("|").append(strFPCObjRevision).append("|").append(strMaterialType).append("|").append(strMaterialName).append("|").append(strMaterialRevision).append("|").append(strArtworkNotConnected).append("\n");
										outFailureLog.flush();							
									}	
								}
							} else if(strBOMIntermediateTypes.indexOf("~"+strBOMChildType+"~")>-1){
								//Modified for POA Extract Requirement Id : 33653,34053,34054 --starts
								//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
								getConnectedEBOMChild(context,strBOMChildId,strUserName,strFolder,dobjFPCOrPart,strFinalArtwork,strExportPOA,strPDFArtwork);
								////modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
								//Modified for POA Extract Requirement Id : 33653,34053,34054 --ends
							}
						} else {
							//For Circular reference, skipping the Object and logging in Program file
							/*outProgram.append(strFPCObjType+"|"+strFPCObjName+"|"+strFPCObjRevision+" : "+strBOMChildType +"|"+ strBOMChildName +"|"+strBOMChildRevision+" - repeated BOM item").append("\n"); 
							outProgram.flush();*/
							continue;
						}
					}
				}
			}
			if(!mlAllART.isEmpty() && mlAllART!=null )
			{	
				//Modified for POA Extract Requirement Id : 33653,34053,34054 --starts
				//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
				checkOutEBOM(context,mlAllART,mpArtPack,strUserName,strFolder,dobjFPCOrPart,strFinalArtwork,strExportPOA,strPDFArtwork);
				//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
				//Modified for POA Extract Requirement Id : 33653,34053,34054 --ends
				mlAllART =new MapList();
				mpArtPack=new HashMap();
			}
		}
		catch (Exception e){
			outLog.print("Exception in getConnectedEBOMChild: "+e+"\n");
			outLog.flush();			
		}
		return slPartId;
	}

	/**
	 * Description: This Method will call Checkout method for Objects.
	 * Access calculated on basis of DSM Security Model on pgArtwork/POA Object.
	 * @return Void
	 * @param Context
	 */
	private void checkOutEBOM(Context context,MapList mlAllART, Map mpArtPack,String strUserName,String strFolder,DomainObject dobjFPCOrPart,String strFinalArtwork, String strExportPOA,String strPDFArtwork) throws Exception{
	try {
		boolean bfileName=false;
		StringBuffer strRenameFile = new StringBuffer();
		String strArtState = DomainConstants.EMPTY_STRING; 
		String strWhereExpr = "revision=="+REVISION_RENDITION;
		MapList mlRelatedDocumentOrPart = new MapList();
		//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
		MapList mlRelatedPDFArtworkDocument = new MapList();
		//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- Ends
		StringList slArtWorkName= new StringList();
		String strFCFPPObjectType = DomainConstants.EMPTY_STRING;
		String strFCFPPObjectRev = DomainConstants.EMPTY_STRING;
		String strFCFPPObjectName = DomainConstants.EMPTY_STRING;
		//Added code for defect id 38957 Starts
		boolean bHasArtwork = false;
		//Added code for defect id 38957 Ends
		StringList slSelectObj = new StringList(DomainConstants.SELECT_ID);
		slSelectObj.add(DomainConstants.SELECT_TYPE);
		slSelectObj.add(DomainConstants.SELECT_NAME);
		slSelectObj.add(DomainConstants.SELECT_REVISION);
		for(int i=0;i<mlAllART.size();i++)
		{
			Map mpArt= (Map)mlAllART.get(i);
			Map mpFPPInfo=dobjFPCOrPart.getInfo(context ,SL_OBJECT_SELECT );
			strFCFPPObjectType=(String)mpFPPInfo.get(DomainConstants.SELECT_TYPE);
			strFCFPPObjectRev=(String)mpFPPInfo.get(DomainConstants.SELECT_REVISION);
			strFCFPPObjectName=(String)mpFPPInfo.get(DomainConstants.SELECT_NAME);
			String strArtId = (String)mpArt.get(DomainConstants.SELECT_ID);
			String strArtName=(String)mpArt.get(DomainConstants.SELECT_NAME);
			String strArtRev =(String)mpArt.get(DomainConstants.SELECT_REVISION);
			String strArtType =(String)mpArt.get(DomainConstants.SELECT_TYPE);	
			strArtState = (String)mpArt.get(DomainConstants.SELECT_CURRENT);	
			DomainObject doObjArtWork=DomainObject.newInstance(context,strArtId);
			boolean bAccessToArtWorkRead =accessCheck(context,strUserName,doObjArtWork);
			//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
			if(bAccessToArtWorkRead && strExportPOA.equalsIgnoreCase("true") && !slPOACheck.contains(strArtId)){
				slPOACheck.add(strArtId);
			}
			//Added for POA Extract Requirement Id : 33653,34053,34054 --ends	
			DomainObject doObjMaterial =(DomainObject) mpArtPack.get(strArtId);
			//Added for Defect : 33756--starts
			String strArtWorkParentName = doObjMaterial.getInfo(context, pgV3Constants.SELECT_NAME);
			String 	strArtWorkParentRev=doObjMaterial.getInfo(context, pgV3Constants.SELECT_REVISION);
			String 	srtArtWorkParentTitle = doObjMaterial.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			//Added for Defect : 33576--ends
			//Added code for defect id 38957 Starts
			bHasArtwork=false;
			//Added code for defect id 38957 Ends
			//Added code for Defect Id - 30148 : Mass Artwork download tool should not download PDF from pre-Release Artwork--Starts
			if(strArtState.equalsIgnoreCase("Obsolete") || strArtState.equalsIgnoreCase("Release")){
			//Added code for Defect Id - 30148 : Mass Artwork download tool should not download PDF from pre-Release Artwork--Ends
			//Added code for Requirement: 31972-The report shall give the user the option to download Artwork files rather than default to Artwork Technical Specification-Start
			
			if(UIUtil.isNotNullAndNotEmpty(strFinalArtwork) && strFinalArtwork.equalsIgnoreCase("true")){
				mlRelatedDocumentOrPart=(MapList)doObjArtWork.getRelatedObjects(context, //Context
						pgV3Constants.RELATIONSHIP_PARTSPECIFICATION, //Relationship
						TYPE_ARTWORKFILE, //Type
						SL_OBJECT_SELECT, //Object Select
						null, //Rel Select
						false, //get To
						true, //get From
						(short)1, //recurse level
						"", //object where clause
						null, //relationship where clause
						0); //limit 
			}//Added code for Requirement: 31972-The report shall give the user the option to download Artwork files rather than default to Artwork Technical Specification-Ends
			//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
			//Added for 2018x.6 - Download the PDF if Part does not have Final Artwort
			//Added code for defect id 38957 Starts
			if (!mlRelatedDocumentOrPart.isEmpty()) {
				bHasArtwork = true;
			}
			//Added code for defect id 38957 Ends
			if(UIUtil.isNotNullAndNotEmpty(strPDFArtwork) && pgV3Constants.TRUE.equalsIgnoreCase(strPDFArtwork) || (!bHasArtwork && pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork))) {
				mlRelatedPDFArtworkDocument=doObjArtWork.getRelatedObjects(context, //Context
						pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT, //Relationship
						pgV3Constants.TYPE_PGIPMDOCUMENT+pgV3Constants.SYMBOL_COMMA+TYPE_PGPOADOCUMENT, //Type
						SL_OBJECT_SELECT, //Object Select
						null, //Rel Select
						false, //get To
						true, //get From
						(short)1, //recurse level
						strWhereExpr, //object where clause
						null, //relationship where clause
						0); //limit
			}
			if(mlRelatedPDFArtworkDocument!=null && !mlRelatedPDFArtworkDocument.isEmpty())
			{
				mlRelatedDocumentOrPart.addAll(mlRelatedPDFArtworkDocument);
			}
			//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- Ends
			if(mlRelatedDocumentOrPart.size()>0 && mlRelatedDocumentOrPart!=null){
				Iterator itrDocumentOrPart = mlRelatedDocumentOrPart.iterator();
				for(int iIndex=mlRelatedDocumentOrPart.size()-1;iIndex>=0;iIndex--){
					Map mpDocumentOrPart = (Map)mlRelatedDocumentOrPart.get(iIndex);
					String strRelationship =(String)mpDocumentOrPart.get("relationship");
					String strDocumentOrPartType=(String)mpDocumentOrPart.get("type");
					if(strRelationship.equalsIgnoreCase(pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT) && (strDocumentOrPartType.equals(pgV3Constants.TYPE_PGIPMDOCUMENT) || strDocumentOrPartType.equals(TYPE_PGPOADOCUMENT)) || (strRelationship.equalsIgnoreCase(pgV3Constants.RELATIONSHIP_PARTSPECIFICATION)&& strDocumentOrPartType.equals(TYPE_ARTWORKFILE))){
						if (bAccessToArtWorkRead) {
							//Clear string buffer for next pdf name. 
							strRenameFile.setLength(0);		
							//Added to prevent PDF and FinalArtwork file from overwriting each other						
							if (strRelationship.equalsIgnoreCase(pgV3Constants.RELATIONSHIP_PARTSPECIFICATION) && strDocumentOrPartType.equals(TYPE_ARTWORKFILE)) 
							{								
								srtArtWorkParentTitle = srtArtWorkParentTitle+"_FinalArtwork";
							} 
																		
							String strDocumentId=(String)mpDocumentOrPart.get("id");
							DomainObject doObjDocument=DomainObject.newInstance(context,strDocumentId);
							boolean bAccessToFinalArtWorkRead =accessCheck(context,strUserName, doObjDocument);
							String 	selectFormatFileNames = DomainConstants.EMPTY_STRING;
							String strFileName = DomainConstants.EMPTY_STRING;
							//Added code for defect id:33319--Starts
							String strFileNameArtwork  = DomainConstants.EMPTY_STRING;
							String 	artworkFormat = DomainConstants.EMPTY_STRING;
							//Added code for defect id:33319--Ends
							//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
							//
							
							if ((bAccessToFinalArtWorkRead && pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) || pgV3Constants.TRUE.equalsIgnoreCase(strPDFArtwork)) || (pgV3Constants.FALSE.equalsIgnoreCase(strFinalArtwork) && bAccessToArtWorkRead)) {
								//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
								String  genericFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
								selectFormatFileNames = "format["+ genericFormat +"].file.name";	
								strFileName = (String)doObjDocument.getInfo(context, selectFormatFileNames);
								File oldfile = new File(strFolder+"/"+strFileName);
								//Added code for defect id:33319--Starts
								if(UIUtil.isNullOrEmpty(strFileName) && pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) && bHasArtwork) {
									artworkFormat = PropertyUtil.getSchemaProperty(context,"format_Artwork");
									selectFormatFileNames = "format["+ artworkFormat +"].file.name";
									strFileNameArtwork = (String)doObjDocument.getInfo(context, selectFormatFileNames);
									oldfile = new File(strFolder+"/"+strFileNameArtwork);
								}
								//Added code for defect id:33319--Ends
								int[] intspl = new int[] {47,92,58,42,63,34,60,62,124};
									for(int j=0;j<intspl.length;j++){
									srtArtWorkParentTitle=srtArtWorkParentTitle.replace((char)intspl[j], (char)32);
								}
								strRenameFile = renameFile(strFCFPPObjectName,strGlobalState,strFCFPPObjectType,strArtWorkParentName,strArtWorkParentRev,srtArtWorkParentTitle);
								
								if(!slFileRenamedMainPart.contains(strRenameFile.toString())){
									slFileRenamedMainPart.addElement(strRenameFile.toString());
									outLog.print("Before checkout file -: " + strUserName +": FileName:" + strFileName+"\n");
									outLog.print("Before checkout file -: " + strFCFPPObjectType +" " + strFCFPPObjectName+"\n");
									outLog.print("Before checkout file -: strArtName: " + strArtName+"\n");
									outLog.flush();	
									//Added code for defect id:33319--Starts
									int iCheckout = 1;
									if(pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) && bHasArtwork){
											if(UIUtil.isNullOrEmpty(strFileName)) {
												//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Starts
												strFileName = strFileNameArtwork;
												//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Ends
												iCheckout =	checkOutFile(context,doObjDocument,strFileNameArtwork,artworkFormat,strFolder,strFCFPPObjectType,strFCFPPObjectName,strFCFPPObjectRev,strArtType,strArtName,strArtRev);
										} else{
											iCheckout =	checkOutFile(context,doObjDocument,strFileName,genericFormat,strFolder,strFCFPPObjectType,strFCFPPObjectName,strFCFPPObjectRev,strArtType,strArtName,strArtRev);
										}
									}
									//added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
									if(pgV3Constants.TRUE.equalsIgnoreCase(strPDFArtwork) || (pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) && !bHasArtwork)){
										iCheckout =	checkOutFile(context,doObjDocument,strFileName,genericFormat,strFolder,strFCFPPObjectType,strFCFPPObjectName,strFCFPPObjectRev,strArtType,strArtName,strArtRev);
									}
									//added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
									//Added code for defect id:33319--Ends
									if (iCheckout==0){
										outLog.print("Checkout - Success: " + iCheckout+"\n");
										outLog.flush();
										//Renaming Checkout Code Begin
										//code to sleep for 1 sec
										outLog.print("Before Rename file.");
										outLog.flush();
										Thread.sleep(1000);
										File newfile = new File(strFolder+"/"+strRenameFile);
										outLog.print("newfile-------->"+newfile);
										outLog.print("After Rename file.");
										outLog.print("Before Stamping file.");
										outLog.flush();
										addStamping(context,doObjDocument,strFolder,strFileName,strFinalArtwork,bHasArtwork,strRelationship);
										//code to sleep for 1 sec
										Thread.sleep(1000);
										outLog.print("After Stamping file.");
										outLog.flush();
										bfileName = oldfile.renameTo(newfile);
										outLog.print("oldfile-------->"+oldfile);
										outLog.print("bfileName-------->"+bfileName);
										outLog.flush();
										//Renaming Checkout Code End
										outLog.print("Checkout complete.");
										outLog.flush();
									} else {
										outLog.print("Checkout - Fail: " + iCheckout+"\n");
										outLog.flush();
									}
									if(bfileName==false) {
										outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPObjectName).append("|").append(strFCFPPObjectRev).append("|").append(strRenameFailureMsg).append("|").append(strArtType).append("|").append(strArtName).append("|").append(strArtRev).append("\n");
										outFailureLog.flush();
										outLog.print("File rename - Fail\n");
										outLog.flush();
									} else {
										outSuccessLog.append(strFCFPPObjectType).append("|").append(strFCFPPObjectName).append("|").append(strFCFPPObjectRev).append(":").append(strArtType).append("|").append(strArtName).append("|").append(strArtRev).append("|").append(strRenameFile).append("\n");
										outSuccessLog.flush();
									}
									slFileRenamedMainPart = new StringList();
								}
							} else {								
								outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPObjectName).append("|").append(strFCFPPObjectRev).append("|").append(strNoArtworkAccess).append("|").append(strArtType).append("|").append(strArtName).append("|").append(strArtRev).append("\n");
								outFailureLog.flush();
							}
						} else {
							if(!slArtWorkName.contains(strArtName)){
								slArtWorkName.add(strArtName);
								outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPObjectName).append("|").append(strFCFPPObjectRev).append("|").append(strNoArtworkAccess).append("|").append(strArtType).append("|").append(strArtName).append("|").append(strArtRev).append("\n");
								outFailureLog.flush();
							}
						}
					}
				} 
			} else {
				if(strFinalArtwork.equalsIgnoreCase("true")){
					outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPObjectName).append("|").append(strFCFPPObjectRev).append("|").append(strArtType).append("|").append(strArtName).append("|").append(strArtRev).append("|").append(strArtworkNotConnectedOrNoAccess).append("\n");
					outFailureLog.flush();
				}else{
					outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPObjectName).append("|").append(strFCFPPObjectRev).append("|").append(strArtType).append("|").append(strArtName).append("|").append(strArtRev).append("|").append(strArtworkPDFNotConnectedOrNoAccess).append("\n");
					outFailureLog.flush();
				}
			}
		} else {
			outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPObjectName).append("|").append(strFCFPPObjectRev).append("|").append("|").append(strArtType).append("|").append(strArtName).append("|").append(strArtRev).append("|").append(strArtState).append("|").append(strNoArtworkState).append("\n");
			outFailureLog.flush();
			}
		}
	} catch(Exception ex){	
		outLog.print("Exception in checkOutEBOM: "+ex+"\n");
		outLog.flush();
	}
	}


/**
 * Description: This Method will Checkout file from EBOM Substitutes Parts.Navigating from Part to pgArtwork/POA Objects.
 * Access calculated on basis of DSM Security Model on pgArtwork/POA Object.
 * @returv Void
 * @param context
 */
	private void checkOutEBOMSubstitute(Context context, StringList slPartId, String strFCFPPObjectType, String strFCFPPName, String strFCFPPObjectRev, String strUserName, String strFolder, String strFinalArtwork, String strExportPOA,String strPDFArtwork) {
	try{
		String strWhereExpr = "revision=="+REVISION_RENDITION;
		String sStringResourceFile="emxCPNStringResource"; 
		String sLanguage = context.getSession().getLanguage();
		String strIDPOAArt = DomainConstants.EMPTY_STRING;
		String strNameSubstitutePOAArt= DomainConstants.EMPTY_STRING;
		String strTypeSubstitutePOAArt= DomainConstants.EMPTY_STRING;
		String strRevisionSubstitutePOAArt= DomainConstants.EMPTY_STRING;
		String strPartType= DomainConstants.EMPTY_STRING;
		String strPartRev= DomainConstants.EMPTY_STRING;
		String strPartName= DomainConstants.EMPTY_STRING;
		String strPartTitle = DomainConstants.EMPTY_STRING;
		String strDocumenParttId= DomainConstants.EMPTY_STRING;
		String genericPartFormat = DomainConstants.EMPTY_STRING;
		String selectFormatFileNames = DomainConstants.EMPTY_STRING;
		String strFileRename= DomainConstants.EMPTY_STRING;
		String fileNamesPart= DomainConstants.EMPTY_STRING;
		String strBOMMaterialTypes=i18nNow.getI18nString("emxCPN.MassArtworkDownload.MassArtWork.BOM.MaterialTypes",sStringResourceFile, sLanguage);
		MapList mlRelatedDocument = new MapList();
		//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
		MapList mlRelatedPDFArtworkDocument = new MapList();
		//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
        boolean bfileName = false;		
        boolean isAccess = false;
        StringList slEBOMSubArtWorkName = new StringList();
		//Added code for defect id:33319--Starts 
		String artworkFormat = DomainConstants.EMPTY_STRING;
		String strFileNameArtwork = DomainConstants.EMPTY_STRING;
		//Added code for defect id:33319--Ends
		//Added defect id 38957 Starts
		String strRelationship = DomainConstants.EMPTY_STRING;
		String strDocumentOrPartType = DomainConstants.EMPTY_STRING;
		boolean bHasArtwork = false;
		//Added defect id 38957 Ends
		for(int j=0;j<slPartId.size();j++){
			String strPartId=(String)slPartId.get(j);
			if(UIUtil.isNotNullAndNotEmpty(strPartId)){
				DomainObject doObjPartDetail=DomainObject.newInstance(context,strPartId);
				Map mpFPPdetails=(Map)doObjPartDetail.getInfo(context ,SL_OBJECT_SELECT);				
				strPartType=(String)mpFPPdetails.get(DomainConstants.SELECT_TYPE);
				strPartRev=(String)mpFPPdetails.get(DomainConstants.SELECT_REVISION);
				strPartName=(String)mpFPPdetails.get(DomainConstants.SELECT_NAME);
				strPartTitle=(String)mpFPPdetails.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				if(strBOMMaterialTypes.indexOf("~"+strPartType+"~")>-1)
				{
					//Added code for defect id- 31958--Starts
					isAccess = accessCheck(context, strUserName, doObjPartDetail);
					if(isAccess){
					//Added code for defect id- 31958--Ends
					MapList mlRelatedPartDetailsArt = (MapList)doObjPartDetail.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PARTSPECIFICATION, pgV3Constants.TYPE_PGARTWORK+","+pgV3Constants.TYPE_POA, SL_OBJECT_SELECT, null, false, true, (short)1, null, null,0);
						if(mlRelatedPartDetailsArt.size()>0){					
						Iterator itrART =mlRelatedPartDetailsArt.iterator();
						while(itrART.hasNext()){
							Map mpART = (Map)itrART.next();												
							strIDPOAArt =(String)mpART.get("id");
							strNameSubstitutePOAArt=(String)mpART.get("name");
							strTypeSubstitutePOAArt=(String)mpART.get("type");
							strRevisionSubstitutePOAArt=(String)mpART.get("revision");
							DomainObject doObjPOAArt=DomainObject.newInstance(context,strIDPOAArt);	
							boolean bAccessArtWorkRead = accessCheck(context,strUserName,doObjPOAArt);
							//Added defect id 38957 Starts
							bHasArtwork = false;
							//Added defect id 38957 Ends
							if (bAccessArtWorkRead){
								//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
								if(strExportPOA.equalsIgnoreCase("true") && !slPOACheck.contains(strIDPOAArt)){
									slPOACheck.add(strIDPOAArt);
								}
								//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
								if(strFinalArtwork.equalsIgnoreCase("true")){
									mlRelatedDocument=(MapList)doObjPOAArt.getRelatedObjects(context, //Context
											pgV3Constants.RELATIONSHIP_PARTSPECIFICATION, //Relationship
											TYPE_ARTWORKFILE, //Type
											SL_OBJECT_SELECT, //Object Select
											null, //Rel Select
											false, //get To
											true, //get From
											(short)1, //recurse level
											"", //object where clause
											null, //relationship where clause
											0); //limit
									}
								if (!mlRelatedDocument.isEmpty()) {
									bHasArtwork = true;
								}
								//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
								if(UIUtil.isNotNullAndNotEmpty(strPDFArtwork) && strPDFArtwork.equalsIgnoreCase(pgV3Constants.TRUE)|| (!bHasArtwork && pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork))) {
									mlRelatedPDFArtworkDocument=doObjPOAArt.getRelatedObjects(context, //Context
												pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT, //Relationship
												pgV3Constants.TYPE_PGIPMDOCUMENT+pgV3Constants.SYMBOL_COMMA+TYPE_PGPOADOCUMENT, //Type
												SL_OBJECT_SELECT, //Object Select
												null, //Rel Select
												false, //get To
												true, //get From
												(short)1, //recurse level
												strWhereExpr, //object where clause
												null, //relationship where clause
												0); //limit
										
									}
								if(mlRelatedPDFArtworkDocument!=null && !mlRelatedPDFArtworkDocument.isEmpty())
								{
									mlRelatedDocument.addAll(mlRelatedPDFArtworkDocument);
								}
								//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
									if(mlRelatedDocument.size()>0 && mlRelatedDocument!=null){
										Iterator itrDocument =mlRelatedDocument.iterator();
										while(itrDocument.hasNext()){
											Map mpDocument = (Map)itrDocument.next();											
											strDocumenParttId=(String)mpDocument.get("id");
											//Added code for defect id 38957 Starts
											strRelationship =(String)mpDocument.get(pgV3Constants.RELATIONSHIP);
											strDocumentOrPartType=(String)mpDocument.get(DomainConstants.SELECT_TYPE);
											//Added code for defect id 38957 Ends
											DomainObject doObjIPMDocument=DomainObject.newInstance(context,strDocumenParttId);
											boolean bAccessToFinalArtWorkRead =accessCheck(context,strUserName, doObjIPMDocument);
											if ((bAccessToFinalArtWorkRead && pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) || pgV3Constants.TRUE.equalsIgnoreCase(strPDFArtwork)) || (pgV3Constants.FALSE.equalsIgnoreCase(strFinalArtwork) && bAccessArtWorkRead)) {
												//Added to prevent PDF and FinalArtwork file from overwriting each other						
												if (strRelationship.equalsIgnoreCase(pgV3Constants.RELATIONSHIP_PARTSPECIFICATION) && strDocumentOrPartType.equals(TYPE_ARTWORKFILE)) 
												{	
													strPartTitle = (new StringBuilder()).append(strPartTitle).append("_FinalArtwork").toString();
												} 
												genericPartFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
												selectFormatFileNames = "format["+ genericPartFormat +"].file.name";	
												fileNamesPart = (String)doObjIPMDocument.getInfo(context, selectFormatFileNames);
												File oldPartFile=new File(strFolder+"/"+fileNamesPart);
												//Added code for defect id:33319--Starts
												if(UIUtil.isNullOrEmpty(fileNamesPart) && pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) && bHasArtwork){
													artworkFormat = PropertyUtil.getSchemaProperty(context,"format_Artwork");
													selectFormatFileNames = "format["+ artworkFormat +"].file.name";
													strFileNameArtwork = (String)doObjIPMDocument.getInfo(context, selectFormatFileNames);
													oldPartFile = new File(strFolder+"/"+strFileNameArtwork);
												}
												//Added code for defect id:33319--Ends
												int[] intspl = new int[] {47,92,58,42,63,34,60,62,124};
												for(int i=0;i<intspl.length;i++){
													strPartTitle=strPartTitle.replace((char)intspl[i], (char)32);
												}
												if (strGlobalState.equalsIgnoreCase("Obsolete")){
													strFileRename=strFCFPPName+"_"+strPartName+"_"+strPartRev+"_"+strPartTitle+"_OBSOLETE.pdf";
												}
												else{
													strFileRename=strFCFPPName+"_"+strPartName+"_"+strPartRev+"_"+strPartTitle+".pdf";
												}
												if(!slFileRenamedMainPart.contains(strFileRename)){
													slFileRenamedMainPart.addElement(strFileRename);
													outLog.print("Before checkout file -: " +strUserName +": FileName:" +fileNamesPart+"\n");
													outLog.print("Before checkout file -: " + strFCFPPObjectType +" " + strFCFPPName+"\n");
													outLog.flush();	
												
													//Added code for defect id:33319--Starts
													int iCheckout = 1;
													if(pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) && bHasArtwork){
														if(UIUtil.isNullOrEmpty(fileNamesPart)) {
														iCheckout =	checkOutFile(context,doObjIPMDocument,strFileNameArtwork,artworkFormat,strFolder,strFCFPPObjectType,strFCFPPName,strFCFPPObjectRev,strTypeSubstitutePOAArt,strNameSubstitutePOAArt,strRevisionSubstitutePOAArt);
														} else{
															iCheckout=checkOutFile(context,doObjIPMDocument,fileNamesPart,genericPartFormat,strFolder,strFCFPPObjectType,strFCFPPName,strFCFPPObjectRev,strTypeSubstitutePOAArt,strNameSubstitutePOAArt,strRevisionSubstitutePOAArt);
														}
													}//Added code for defect id:33319--Ends
													//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
													if(pgV3Constants.TRUE.equalsIgnoreCase(strPDFArtwork) || (pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) && !bHasArtwork)){
														iCheckout=checkOutFile(context,doObjIPMDocument,fileNamesPart,genericPartFormat,strFolder,strFCFPPObjectType,strFCFPPName,strFCFPPObjectRev,strTypeSubstitutePOAArt,strNameSubstitutePOAArt,strRevisionSubstitutePOAArt);
													}
													//Added code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
													if(iCheckout==0){
														outLog.print("iCheckout success: " + iCheckout+"\n");
														outLog.flush();
														addStamping(context,doObjIPMDocument,strFolder,fileNamesPart,strFinalArtwork,bHasArtwork,strRelationship);
														//code to sleep for 1 sec
														Thread.sleep(1000);
														File newPartFile = new File(strFolder+"/"+strFileRename);
														//code to sleep for 1 sec
														Thread.sleep(1000);
														bfileName = oldPartFile.renameTo(newPartFile);
													} else {
														outLog.print("Checkout - Fail: " + iCheckout+"\n");
														outLog.flush();
													}
													if(bfileName==false) {
														outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append("|").append(strRenameFailureSubstituteMsg).append("|").append(strTypeSubstitutePOAArt).append("|").append(strNameSubstitutePOAArt).append("|").append(strRevisionSubstitutePOAArt).append("\n");
														outFailureLog.flush();
														outLog.print("File rename - Fail\n");
														outLog.flush();
													}
													else {
														outSuccessLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append(":").append(strTypeSubstitutePOAArt).append("|").append(strNameSubstitutePOAArt).append("|").append(strRevisionSubstitutePOAArt).append("|").append(strFileRename).append("\n");
														outSuccessLog.flush();
													}
													slFileRenamedMainPart = new StringList();
												}
											}
										}
									} else {
										if(strFinalArtwork.equalsIgnoreCase("true")){
											outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append("|").append(strTypeSubstitutePOAArt).append("|").append(strNameSubstitutePOAArt).append("|").append(strRevisionSubstitutePOAArt).append("|").append(strArtworkNotConnectedOrNoAccess).append("\n");
											outFailureLog.flush();
										}else{
											outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append("|").append(strTypeSubstitutePOAArt).append("|").append(strNameSubstitutePOAArt).append("|").append(strRevisionSubstitutePOAArt).append("|").append(strArtworkPDFNotConnectedOrNoAccess).append("\n");
											outFailureLog.flush();
										}
									}
								}
								else{
									if(!slEBOMSubArtWorkName.contains(strNameSubstitutePOAArt)){
										slEBOMSubArtWorkName.add(strNameSubstitutePOAArt);
										outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append("|").append(strNoArtworkAccess).append("|").append(strTypeSubstitutePOAArt).append("|").append(strNameSubstitutePOAArt).append("|").append(strRevisionSubstitutePOAArt).append("\n");
										outFailureLog.flush();
									}
								}
							}
						}	
						}
						else {		
							//Artwork not connected to Material Part so logging in Failure logs						 
							outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append("|").append(strPartType).append("|").append(strPartRev).append("|").append(strPartName).append("|").append(strArtworkNotConnected).append("\n");
							outFailureLog.flush();
						}
					}
				}
			}
		} catch(Exception ex){			
			outLog.print("Exception in checkOutEBOMSubstitute: "+ex+"\n");
			outLog.flush();
		}
	}


	/**
	 * Description: This method is used to Checkout files from pgIPMDocument/pgPOADocument Object.
	 * Checkout operation happening with 'User Agent' context and Trigger Off mode. After checkout Trigger On and Pop Context.
	 * @return On Successful Checkout returns 0. On Failure returns 1.
	 * @param context
	 */
	public int checkOutFile(Context context, DomainObject doObjDocument, String fileNamesPart, String genericPartFormat, String strFolder,String strFCFPPObjectType, String strFCFPPName, String strFCFPPObjectRev, String strBOMSpecType, String strArtPOAName, String strArtPOARev) throws FrameworkException {
		try{
			ContextUtil.pushContext(context, null, null, null);
			MqlUtil.mqlCommand(context, "trigger off");
			String fileName ="";
			FileList files = new FileList();
			StringList fileNameList = FrameworkUtil.split(fileNamesPart, ",");	
			for(int j=0; j<fileNameList.size(); ++j){
				fileName = (String)fileNameList.get(j);
				matrix.db.File file = new matrix.db.File(fileName, genericPartFormat);
				files.addElement(file);
			}
			if(files.size()>0){
				doObjDocument.checkoutFiles(context, false, genericPartFormat, files, strFolder);
			}
		}catch(Exception e){				
				outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append("|").append(e.getMessage()).append(":").append(strBOMSpecType).append("|").append(strArtPOAName).append("|").append(strArtPOARev).append("\n");
				outFailureLog.flush();
				outLog.print("Exception in checkOutFile: "+e+"\n");
				outLog.flush();
				return 1;
			}
			finally {
				MqlUtil.mqlCommand(context, "trigger on");
				ContextUtil.popContext(context);
			}
		return 0;
	}

/**
 * Description: This Method is used to create new Folder for PDF file Checkout
 * @return The Folder Path
 * @param workspace folder
 * 
*/
	private String createFolder(String workspace, String folderName) throws Exception {	
		String strFolderPath = workspace + File.separator + folderName;
		File fId = new File(strFolderPath);
		if(fId.exists()){	
			deleteDir(fId);
		}
		if(!fId.mkdir()) {
			outLog.print("Directory creation failed:" + strFolderPath + "\n");
			outLog.flush();			
		}		
		return strFolderPath;
	}

/**
 * Description: This Method is used to delete user Folder 
 * @param file
 * @Returns: The Folder Path
 * 
*/
	private void deleteDir(File dir) throws Exception {
		if (dir.isDirectory()) {
				String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				deleteDir(new File(dir, children[i]));				
			}
		}
		dir.delete();
	}

	/**
	 * Description: This Method adds stamping to the PDF file 
	 * @param context
	 * @returns void
	 * 
	*/
	private void addStamping(Context context,DomainObject dbIPM,String strTempDirectory, String fileNamesPart, String strFinalArtwork, boolean bHasArtwork, String strRelationship) throws Exception {
		String sStringResourceFile="emxCPNStringResource";
		boolean isGenDocStamped = true;
		i18nNow i18nObject = new i18nNow();
		try {
			//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Starts
			String strStampFile = DomainConstants.EMPTY_STRING;
			String strRestrcitedFooter = DomainConstants.EMPTY_STRING;
			String strNonRestrictedFooter = DomainConstants.EMPTY_STRING;
			String strVerticalLength = DomainConstants.EMPTY_STRING;
			String strRotationLength = DomainConstants.EMPTY_STRING;
			boolean isFooterVisible = false;
			String sPart = DomainConstants.EMPTY_STRING;
			String strConfigObjectName= EnoviaResourceBundle.getProperty(context, sStringResourceFile, context.getLocale(),"emxCPN.PDFViews.GenDocStamping.ConfigObjName");
			String strFileNameForStamping= EnoviaResourceBundle.getProperty(context, sStringResourceFile, context.getLocale(),"emxCPN.PDFViews.GenDocStamping.FileName");
			String sServerPath= EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.ServerPath");
			String sImages = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.MassArtWorkReport.images");
			String sDir = sServerPath + java.io.File.separator + sImages;
			//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Ends
			
			String strProductDataTypes = FrameworkProperties.getProperty("emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList");
			//Updated method for defect 42293 for PDF stamping starts
			String strProductDataPOATypes=(new StringBuilder()).append(pgV3Constants.TYPE_POA).append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_PGARTWORK).toString();
			//Updated method for defect 42293 for PDF stamping ends
			String strNonStructuredDataTypes= FrameworkProperties.getProperty("emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList");
			String strObjectId = DomainConstants.EMPTY_STRING;
			boolean isReachStatement = false;
			String  genericFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
			//Updated code for defect id 38957 Starts
			if(pgV3Constants.RELATIONSHIP_PARTSPECIFICATION.equalsIgnoreCase(strRelationship) && pgV3Constants.TRUE.equalsIgnoreCase(strFinalArtwork) && bHasArtwork){
			//Updated code for defect id 38957 Ends
				strObjectId=(String)(dbIPM.getInfo(context,"to[Part Specification].from.id"));
			}else{
				strObjectId=(String)(dbIPM.getInfo(context,"to[Reference Document].from.id"));
			}
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
			//Updated method for defect 42293 for PDF stamping starts
			if(strProductDataPOATypes.contains(strType)){
			//Updated method for defect 42293 for PDF stamping starts
				//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Starts
				BusinessObject boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
							strConfigObjectName, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
					if (boConfig.exists(context)) {
						StringList slattribute = new StringList(4);
						slattribute.addElement(pgV3Constants.ATTR_STAMPING_VERTICAL_LENGTH);
						slattribute.addElement(pgV3Constants.ATTR_STAMPING_ROTATION_LENGTH);
						slattribute.addElement(pgV3Constants.ATTR_RESTRICTED_FOOTER);
						slattribute.addElement(pgV3Constants.ATTR_NON_RESTRICTED_FOOTER);

						AttributeList attlist = boConfig.getAttributeValues(context, slattribute);
						AttributeItr attrItr = new AttributeItr(attlist);
						while (attrItr.next()) {
							Attribute attribute = attrItr.obj();
							String attrName = attribute.getName().trim();
							if (pgV3Constants.ATTR_RESTRICTED_FOOTER.equalsIgnoreCase(attrName)) {
								strRestrcitedFooter = attribute.getValue().trim();
							} else if (pgV3Constants.ATTR_NON_RESTRICTED_FOOTER.equalsIgnoreCase(attrName)) {
								strNonRestrictedFooter = attribute.getValue().trim();
							} else if (pgV3Constants.ATTR_STAMPING_VERTICAL_LENGTH.equalsIgnoreCase(attrName)) {
								strVerticalLength = attribute.getValue().trim();
							} else if (pgV3Constants.ATTR_STAMPING_ROTATION_LENGTH.equalsIgnoreCase(attrName)) {
								strRotationLength = attribute.getValue().trim();
							}
						}
					}
					if ((pgV3Constants.RESTRICTED.equalsIgnoreCase(strObjectSecurityStatus)
							|| pgV3Constants.INTERNAL_USE.equalsIgnoreCase(strObjectSecurityStatus))) {
						strStampFile = strRestrcitedFooter;

					} else {

						strStampFile = strNonRestrictedFooter;
					}
					//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Ends
					
				DateFormat formatter1 = new SimpleDateFormat (systemDateFormat);
				Date newtmpDate = null;
				if (effectivityDate != null && effectivityDate.trim().length() > 0) {
					Date tmpDate = formatter.parse(effectivityDate);
					formatter = new SimpleDateFormat("yyyy-MM-dd");	
					returnDate = formatter.format(tmpDate);
				}
				formatter=new SimpleDateFormat("yyyy-MM-dd");
				if("Obsolete".equalsIgnoreCase(StrObjState)){
					String obsoleteDate = (String)newObjDO.getInfo(context, "state[Obsolete].actual");
					if (obsoleteDate != null && obsoleteDate.trim().length() > 0) {
						newtmpDate =(Date)formatter1.parse(obsoleteDate);
						returnDate = formatter.format(newtmpDate);
					}
					sPart = strStampFile.replace("AUTHORIZED #NAME_VARIABLE# Rev #REVISION_VARIABLE# Effective Date","OBSOLETE");
					sPart = sPart.replace("#STAMP_VARIABLE#",returnDate);
					if(StrObjAttributeValue.equalsIgnoreCase("PLANNING")){
						sPart = sPart.replace("P&G",strComment + "%n" + "P&G");
					}
					//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Starts
					isFooterVisible = true;
					//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Ends
					} 
					else if(("Release".equalsIgnoreCase(StrObjState))){
						sPart = strStampFile.replace("#STAMP_VARIABLE#",returnDate);
						if(StrObjAttributeValue.equalsIgnoreCase("PLANNING")){
							sPart = sPart.replace("P&G",strComment + "%n" + "P&G");	
						}
							isFooterVisible = true;
						}
						sPart = sPart.replace("#NAME_VARIABLE#",strName);
						sPart = sPart.replace("#REVISION_VARIABLE#",strRevision);
					}
				
			if(isReachStatement) {
				PdfReader reader1 = new PdfReader(strTempDirectory+java.io.File.separator+fileName);
				PdfReader reader2 = new PdfReader(sDir+java.io.File.separator+"ReachStatement.pdf");
				PdfCopyFields copy = new PdfCopyFields(new FileOutputStream(strTempDirectory +java.io.File.separator+"FullFile.pdf"));
				copy.addDocument(reader1);
				copy.addDocument(reader2);
				copy.close();
				File fOrig = new File(strTempDirectory+java.io.File.separator+fileName);
				fOrig.delete();
				File fNew = new File(strTempDirectory +java.io.File.separator+"FullFile.pdf");
				fNew.renameTo(new File(strTempDirectory+java.io.File.separator+fileName));
			}
			//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Starts
			fileName = strTempDirectory+java.io.File.separator+fileName;
			java.io.File pdfFileName= new java.io.File(fileName);	
			String fileName1 = pdfFileName.getName();
			if (isFooterVisible) {
				TimeZone tz = Calendar.getInstance().getTimeZone();
				String strPrintedDate = formatter.format(Calendar.getInstance(tz).getTime());
				String strFooter = sPart + " " + strPrintedDate + " Page";
				Map<String,String> parameterMap = new HashMap<>();
				parameterMap.put("workDir", strTempDirectory);
				parameterMap.put("filenameForStamp", fileName1);
				parameterMap.put("Footer", strFooter);
				parameterMap.put("VerticalLength", strVerticalLength);
				parameterMap.put("RotationLength", strRotationLength);
				parameterMap.put("FileNameForStamping", strFileNameForStamping);
				String[] args = JPO.packArgs(parameterMap);
				//Updated method for defect 42293 for PDF stamping starts
				isGenDocStamped = processGenDocStamping(args);
				//Updated method for defect 42293 for PDF stamping ends
			}
			//Added code for Requirement id 37689 Mass Artwork Download shall use iText process for PDF stamping Ends
			
		} catch (Exception e) {
			if (!isGenDocStamped) {
				throw e;
			}
		}
	}

	/**Added method for defect 42293 for PDF stamping
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean processGenDocStamping(String[] args) throws Exception {
	    try {
	      boolean bResult = false;
	      boolean isFileRenamed = false;
	      StringBuilder sbFilePathForStamp = new StringBuilder();
	      StringBuilder sbFilePathToStamp = new StringBuilder();
	      String strNewLineFooter = "";
	      Map<?, ?> mProgramMap = (Map<?, ?>)JPO.unpackArgs(args);
	      String strWorkingDir = (String)mProgramMap.get("workDir");
	      String strFileName = (String)mProgramMap.get("filenameForStamp");
	      String strGenDocFooter = (String)mProgramMap.get("Footer");
	      float verticalLength = Float.parseFloat((String)mProgramMap.get("VerticalLength"));
	      float rotationLength = Float.parseFloat((String)mProgramMap.get("RotationLength"));
	      String strFileNameForStamping = (String)mProgramMap.get("FileNameForStamping");
	      float horizontalLength = 0.0F;
	      if (UIUtil.isNotNullAndNotEmpty(strFileName) && UIUtil.isNotNullAndNotEmpty(strFileNameForStamping)) {
	        sbFilePathForStamp.append(strWorkingDir).append(File.separator).append(strFileName);
	        sbFilePathToStamp.append(strWorkingDir).append(File.separator).append(strFileNameForStamping);
	        File pdfFilePathForStamp = new File(sbFilePathForStamp.toString());
	        File pdfFilePathToStamp = new File(sbFilePathToStamp.toString());
	        PdfReader pdfReader = new PdfReader(sbFilePathForStamp.toString());
	        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(sbFilePathToStamp.toString()));
	        int iPages = pdfReader.getNumberOfPages();
	        PdfContentByte pdfContentByte = null;
	        Font pdfFont = new Font(Font.FontFamily.TIMES_ROMAN, 10.0F, 3, BaseColor.BLACK);
	        Phrase phraseNewLine = null;
	        Rectangle rect = null;
	        StringBuilder strFooterText = null;
	        if (UIUtil.isNotNullAndNotEmpty(strGenDocFooter) && strGenDocFooter.contains("NewLine")) {
	          String[] sSplit = strGenDocFooter.split("NewLine");
	          strNewLineFooter = sSplit[0];
	          strGenDocFooter = sSplit[1];
	          Chunk boldChunk1 = new Chunk(strNewLineFooter, pdfFont);
	          boldChunk1.setGenericTag(strNewLineFooter);
	          boldChunk1.setTextRenderMode(2, 0.5F, (BaseColor)GrayColor.GRAYBLACK);
	          phraseNewLine = new Phrase(boldChunk1);
	          verticalLength -= 13.0F;
	        } 
	        for (int i = 1; i <= iPages; i++) {
	          strFooterText = new StringBuilder();
	          rect = pdfReader.getPageSize(i);
	          horizontalLength = rect.getWidth() / 2.0F;
	          strFooterText.append(strGenDocFooter).append(" ").append(i).append(" of ").append(iPages);
	          Chunk boldChunk = new Chunk(strFooterText.toString(), pdfFont);
	          boldChunk.setGenericTag(strFooterText.toString());
	          boldChunk.setTextRenderMode(2, 0.5F, (BaseColor)GrayColor.GRAYBLACK);
	          Phrase phrase = new Phrase(boldChunk);
	          pdfContentByte = pdfStamper.getOverContent(i);
	          pdfContentByte.saveState();
	          ColumnText.showTextAligned(pdfStamper.getOverContent(i), 1, phrase, horizontalLength, verticalLength, rotationLength);
	          if (null != phraseNewLine) {
	            ColumnText.showTextAligned(pdfStamper.getOverContent(i), 1, phraseNewLine, horizontalLength, verticalLength + 13.0F, rotationLength);
	            pdfContentByte.restoreState();
	          } 
	        } 
	        pdfStamper.close();
	        pdfReader.close();
	        Files.delete(pdfFilePathForStamp.toPath());
	        isFileRenamed = pdfFilePathToStamp.renameTo(pdfFilePathForStamp);
	        if (isFileRenamed)
	          bResult = true; 
	      } 
	     
	    } catch (Exception e) {
	    	outLog.print("Exception in processGenDocStamping: "+e+"\n");
			outLog.flush();
	    } 
	    return true;
	  }
	
	private StringList getRelSelectEBOMSubstitution(){
		StringList slRelSelect = new StringList(5);
		try{
			slRelSelect.add("frommid[EBOM Substitute].to.id");
			DomainConstants.MULTI_VALUE_LIST.add("frommid[EBOM Substitute].to.id");
			slRelSelect.add("frommid[EBOM Substitute].to.type");
			DomainConstants.MULTI_VALUE_LIST.add("frommid[EBOM Substitute].to.type");
			slRelSelect.add("frommid[EBOM Substitute].to.name");
			DomainConstants.MULTI_VALUE_LIST.add("frommid[EBOM Substitute].to.name");
			slRelSelect.add("frommid[EBOM Substitute].to.revision");
			DomainConstants.MULTI_VALUE_LIST.add("frommid[EBOM Substitute].to.revision");

		}catch(Exception e){			
			outLog.print("Exception in addStamping: "+e+"\n");
			outLog.flush();
		}
		return slRelSelect;
	}
	
	
	/**This method is used to check the object Access and then return the boolean results.
	 * @param context is the matrix context
	 * @param args has the required information
	 * @return boolean
	 * @throws Exception
	 */ 
	public boolean accessCheck(Context context, String strUserName,DomainObject doObjPart) throws Exception {
		boolean bAccessToArtWorkRead = false; 
		ContextUtil.pushContext(context, strUserName, null,context.getVault().getName());
		String strValue = (String)doObjPart.getInfo(context, "current");
	    ContextUtil.popContext(context);
		if("#DENIED!".equals(strValue))
			 bAccessToArtWorkRead = false; 
		else
			bAccessToArtWorkRead = true;
		return bAccessToArtWorkRead;
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
			outLog.print("Exception in deleteFiles: "+e+"\n");
			outLog.flush();
		}
	 }
	 
	 /**
	  * This method is used get the FPPs which are connected to AFPP. 
	  * Requirement Id: 32058 :Mass Artwork Download tool shall allow user to input PMP and AFPP to download POA
	  * @param context
	  * @return String
	  * @throws Exception
	  */
	 private String getConnectedFPP(Context context, String strPartNames,String strUserName,String strFolder,String strFinalArtwork, String strExportPOA,String strPDFArtwork)throws Exception {
		 String strFPPPartNames = DomainConstants.EMPTY_STRING;
		 try{
			 StringList slIndividualFCFPPName = new StringList();
			 String strcurrentGlobalState_RELEASE = "Release";
			 String strcurrentGlobalState_OBSOLETE = "Obsolete";
			 boolean bHasRelease = false;
			 boolean bIsHighestRelease = false;
			 boolean bIsHighestObsolete = false;
			 Map mpAFPPInfo = new HashMap();
			 MapList mlAFPPInfo = new MapList();
			 MapList mlConnectedFPP = new MapList();
			 Map mpConnectedFPP = new HashMap();
			 MapList mlConnectedPOA = new MapList();
			 Map mpConnectedPOA = new HashMap();
			 Map mpArtworkTemp = new HashMap();
			 Map mpArtwork = new HashMap();
			 String strType = DomainConstants.EMPTY_STRING;
			 String strId = DomainConstants.EMPTY_STRING;
			 String strCurrent = DomainConstants.EMPTY_STRING;
			 String strName = DomainConstants.EMPTY_STRING;
			
			 String strPOAId = DomainConstants.EMPTY_STRING;
			 String strObjMaterial = DomainConstants.EMPTY_STRING;
			 DomainObject ObjMaterial = null;
			 String strRevision = DomainConstants.EMPTY_STRING;
			 String FCFPPName = DomainConstants.EMPTY_STRING;
			 DomainObject dobj = null;
			 Pattern relPatternPOA = new Pattern(pgV3Constants.RELATIONSHIP_PARTSPECIFICATION);
			 Pattern objTypePatternPOA = new Pattern(pgV3Constants.TYPE_POA);
			 objTypePatternPOA.addPattern(pgV3Constants.TYPE_PGARTWORK);
			 StringList objectSelectsPOA = new StringList(DomainConstants.SELECT_ID);
			 objectSelectsPOA.add(DomainConstants.SELECT_NAME);
			 objectSelectsPOA.add(DomainConstants.SELECT_TYPE);
			 objectSelectsPOA.add(DomainConstants.SELECT_CURRENT);
			 objectSelectsPOA.add(DomainConstants.SELECT_REVISION);
			
			 Pattern relPattern = new Pattern(REL_PGDSOAFFECTEDFPPLIST);
			 Pattern objTypePattern = new Pattern(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
			 objTypePattern.addPattern(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
			 StringList objectSelects = new StringList(DomainConstants.SELECT_ID);
			 objectSelects.add(DomainConstants.SELECT_NAME);
			 objectSelects.add(DomainConstants.SELECT_CURRENT);
			 StringList slSelect = new StringList(5);			
			 slSelect.add(DomainConstants.SELECT_ID);		
			 slSelect.add(DomainConstants.SELECT_CURRENT);
			 slSelect.add(DomainConstants.SELECT_NAME);	
			 slSelect.add(DomainConstants.SELECT_TYPE);
			 slSelect.add(DomainConstants.SELECT_REVISION);
			 StringBuffer sbSelectType = new StringBuffer(TYPE_PGDSOAFFECTEDFPPLIST);
			 sbSelectType.append(",");
			 sbSelectType.append(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
			 sbSelectType.append(",");
			 sbSelectType.append(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
			 sbSelectType.append(",");
			 sbSelectType.append(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
			 sbSelectType.append(",");
			 sbSelectType.append(pgV3Constants.TYPE_PGPACKINGMATERIAL);
			 sbSelectType.append(",");
			//Added code for defect id 34566 Starts
			 sbSelectType.append(pgV3Constants.TYPE_POA);
			 sbSelectType.append(",");
			 sbSelectType.append(pgV3Constants.TYPE_PGARTWORK);
			//Added code for defect id 34566 Ends
			 slIndividualFCFPPName = FrameworkUtil.split(strPartNames, ",");
				int slIndividualFCFPPNameSize =slIndividualFCFPPName.size();
				if(slIndividualFCFPPNameSize>0){
					for (String strFCFPPName : slIndividualFCFPPName) {
						FCFPPName = strFCFPPName.trim();						
						if (UIUtil.isNotNullAndNotEmpty(FCFPPName)) {
							//Modified for POA Extract Requirement Id : 33653,34053,34054 --starts
							mlAFPPInfo = DomainObject.findObjects(context,sbSelectType.toString(),FCFPPName, null, null, pgV3Constants.VAULT_ESERVICEPRODUCTION, null, false, slSelect);
							//Modified for POA Extract Requirement Id : 33653,34053,34054 --ends							
							if(mlAFPPInfo.size()>0){
								for(int i=0; i<mlAFPPInfo.size();i++){
									mpAFPPInfo = (Map)mlAFPPInfo.get(i);
									strType = (String)mpAFPPInfo.get(DomainConstants.SELECT_TYPE);
									strId = (String)mpAFPPInfo.get(DomainConstants.SELECT_ID);
									strCurrent = (String)mpAFPPInfo.get(DomainConstants.SELECT_CURRENT);
									strRevision = (String)mpAFPPInfo.get(DomainConstants.SELECT_REVISION);
									bHasRelease = hasRelease(context,strId,strType,FCFPPName);
									bIsHighestRelease = isHighestRevision(context,strId,strType,FCFPPName, "current==Release");
									bIsHighestObsolete = isHighestRevision(context,strId,strType,FCFPPName, "current==Obsolete");
									
									if(strType.equalsIgnoreCase(TYPE_PGDSOAFFECTEDFPPLIST)){
										if(strCurrent.equalsIgnoreCase(strcurrentGlobalState_RELEASE) || strCurrent.equalsIgnoreCase(strcurrentGlobalState_OBSOLETE)){
										//Check for highest Release or Obsolete
											if ((strCurrent.equalsIgnoreCase("Release") && bIsHighestRelease) || ((strCurrent.equalsIgnoreCase("Obsolete") && !bHasRelease && bIsHighestObsolete))) {
											
												dobj = DomainObject.newInstance(context, strId);
												mlConnectedFPP = dobj.getRelatedObjects(context, 
													relPattern.getPattern(),
													objTypePattern.getPattern(), 
													objectSelects, 
													null, 
													false,
													true,
													(short) 0,
													null, 
													null);
												if(mlConnectedFPP.size()>0){
												 for(int iIndex=0;iIndex<mlConnectedFPP.size();iIndex++){
													 mpConnectedFPP = (Map)mlConnectedFPP.get(iIndex);
													 strName = (String)mpConnectedFPP.get(DomainConstants.SELECT_NAME);
													 if(!strFPPPartNames.contains(strName)){
													 strFPPPartNames+= strName.concat(",");
													 }
												}
											} else {
												outFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append(strNoConnectedFPP).append("\n");
												outFailureLog.flush();	
											}
										}
									} else{
										outFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append("Part is not in Release or Obsolete state").append("\n");
										outFailureLog.flush();
									}
									
									} else if(strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFINISHEDPRODUCT) || strType.equalsIgnoreCase(pgV3Constants.TYPE_FINISHEDPRODUCTPART)){
										if(!strFPPPartNames.contains(FCFPPName)){
											strFPPPartNames+= FCFPPName.concat(",");
										}
										
									} else if(strType.equalsIgnoreCase(pgV3Constants.TYPE_PACKAGINGMATERIALPART) || strType.equalsIgnoreCase(pgV3Constants.TYPE_PGPACKINGMATERIAL)){
										boolean bValidPMP = checkPMP(strCurrent,bIsHighestRelease,bHasRelease,bIsHighestObsolete);
										if (bValidPMP) 
										{
											strGlobalState = strCurrent;
											dobj = DomainObject.newInstance(context, strId);
											mlConnectedPOA = dobj.getRelatedObjects(context, 
													relPatternPOA.getPattern(),
													objTypePatternPOA.getPattern(), 
													objectSelectsPOA, 
													null, 
													false,
													true,
													(short) 0,
													null, 
													null);
											if(mlConnectedPOA.size()>0){												
												DomainObject doObjMaterial = DomainObject.newInstance(context,strId);
												Map mpMaterial = (Map)doObjMaterial.getInfo(context, SL_OBJECT_SELECT);
												for(int iArtIndex=0;iArtIndex<mlConnectedPOA.size();iArtIndex++){
													
													mpArtworkTemp = (Map)mlConnectedPOA.get(iArtIndex);
													strPOAId = (String)mpArtworkTemp.get(pgV3Constants.SELECT_ID);
													mpArtwork.put(strPOAId, doObjMaterial);													
												}												
												//Added for Defect : 33576--starts
												//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
												checkOutEBOM(context, mlConnectedPOA, mpArtwork, strUserName, strFolder, doObjMaterial, strFinalArtwork,strExportPOA,strPDFArtwork);
												//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
												//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
												} else {
												outFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append(strNoConnectedFPP).append("\n");
												outFailureLog.flush();	
											}
									} else {
										outFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append("Part is not in Release or Obsolete state").append("\n");
										outFailureLog.flush();
									}
									//Added code for defect id 34566 Ends
									} else if(strType.equalsIgnoreCase(pgV3Constants.TYPE_POA) || strType.equalsIgnoreCase(pgV3Constants.TYPE_PGARTWORK)) {
										//Added for POA Extract Requirement Id : 33653,34053,34054 --starts
										DomainObject doObjMaterial=DomainObject.newInstance(context,strId);
										boolean bAccessToArtWorkRead =accessCheck(context,strUserName,doObjMaterial); 
										if (bAccessToArtWorkRead) {
											if (strExportPOA.equalsIgnoreCase("false")){
												if(bAccessToArtWorkRead){												
													mpArtwork.put(strId, doObjMaterial);
													//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- starts
													checkOutEBOM(context, mlAFPPInfo, mpArtwork, strUserName, strFolder, doObjMaterial, strFinalArtwork,strExportPOA,strPDFArtwork);
													//Modified code for Requirement: 36718-The report shall return Final artwork by default, if user select PDF artwrok checkbox, then report will return PDF artwork- ends
												} 
											//Added code for defect id 34566 Ends
											} else if(strType.equalsIgnoreCase(pgV3Constants.TYPE_POA) && strExportPOA.equalsIgnoreCase("true")){
												if(!slPOACheck.contains(strId) && bAccessToArtWorkRead){
													slPOACheck.add(strId);
												}
											} else if(!strType.equalsIgnoreCase(pgV3Constants.TYPE_POA) && strExportPOA.equalsIgnoreCase("true")){
												outExportPOAFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append("Not a vaild type for POA Export").append("\n");
												outExportPOAFailureLog.flush();
											}
										} else {
											outFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append("No Access").append("\n");
											outFailureLog.flush();
										}
										//Added for POA Extract Requirement Id : 33653,34053,34054 --ends
									} else {
										outFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append(" is not a valid type ").append("\n");
										outFailureLog.flush();
									}
								}
							} else {
								outFailureLog.append(FCFPPName).append("|").append("Not Found In DB").append("\n");
								outFailureLog.flush();
							}
						} else {
							outFailureLog.append(strType).append("|").append(FCFPPName).append("|").append(strRevision).append("|").append(" is not a valid type ").append("\n");
							outFailureLog.flush();
						}
					}
				}
	 } catch (Exception e) {
			outLog.print("Exception in getConnectedFPP: "+e+"\n");
			outLog.flush();
		}
		return strFPPPartNames;
	 }
		
	//Added for POA Extract Requirement Id : 33653,34053,34054 --starts			
	 /**
	 * this method is used export the POA Structure for MassArtwork Report.
	 * @param context
	 * @return Void
	 * @throws Exception
	 */	
		
	private void getPOAStructure(Context context ,StringList slPOA, String fPOAFolder) throws FrameworkException {
		String filePath = fPOAFolder;
		  //Added the code for 22x.02 May CW Defect 49901 - Starts
		boolean isContextPushed = false;
		  //Added the code for 22x.02 May CW Defect 49901 - Ends
		try{
			  //Added the code for 22x.02 May CW Defect 49901 - Starts
			//Pushing the Context for SPec Reader User Request
			if(SPECREADER.equalsIgnoreCase(strRequestOriginatingSource)) {
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
			}
			  //Added the code for 22x.02 May CW Defect 49901 - Starts
			AWLExportPOAToExcel exportProcess = new AWLExportPOAToExcel();
			String name = exportProcess.startExportProcess(context, slPOA);
			StringBuilder sbFile = new StringBuilder();
			sbFile.append(exportProcess.getWorkingPath());
			sbFile.append(File.separator);
			sbFile.append(name);
			sbFile.append(".xlsx");
			HashMap<String,Object> hmRequest = new HashMap<>();
			hmRequest.put("FileName",sbFile.toString());
			hmRequest.put("SelectedPOAList",slPOA);
			hmRequest.put("FileDestPath",exportProcess.getWorkingPath());
			hmRequest.put("Name",name);
			HashMap<String,Object> hmProgramMap = new HashMap<>();
			hmProgramMap.put("requestMap",hmRequest);
			String[] methodargs = JPO.packArgs(hmProgramMap);
			name = JPO.invoke(context, "pgAWL_Util", methodargs, "readWriteExcelData", methodargs, String.class);
			if(BusinessUtil.isNotNullOrEmpty(name)){
				name = name+".zip";
				FileUtils.copyFile(new File(exportProcess.getWorkingPath()+File.separator+name),new File(filePath+File.separator+name));
				AWLUtil.deleteFilesInWorkspace(context, exportProcess.getWorkingPath(), false); 
			}
		} catch(Exception ex) {			
			outExportPOAFailureLog.append("POA Export Failed").append("\n");
			outExportPOAFailureLog.flush();
			outLog.print("Exception in getPOAStructure: "+ex+"\n");
			outLog.flush();
			  //Added the code for 22x.02 May CW Defect 49901 - Starts	
		}
		finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		  //Added the code for 22x.02 May CW Defect 49901 - Ends
	}
/**
 * Description: This Method is used to create new Folder for PDF file Checkout
 * @param context
 * @returns: boolean
 * 
*/	
	private boolean hasRelease(Context context,String strPartId, String strType, String strPartName) throws FrameworkException {
		boolean hasRelease = false;
		StringList slSelect = new StringList();
		String strWhereClause = "current==Release";
		slSelect.add(DomainConstants.SELECT_ID);
		String strId;
		MapList mlPart = new MapList();
		//Added code for defect id 40556 starts
		strType = getValidTypes(strType);
		//Added code for defect id 40556 Ends
		try {
			MapList mlPartValid = DomainObject.findObjects(context, //context
					strType, //type
					strPartName,//name
					DomainConstants.QUERY_WILDCARD,//revision
					DomainConstants.QUERY_WILDCARD,//owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
					strWhereClause,//where clause
					false,//expand type
					slSelect);//object select
					//Fetching Highest Release revision --Begin	
			if (mlPartValid.size()>0) {
				hasRelease = true;
			}
		} catch(Exception e) {			
			outLog.print("Exception in hasRelease: "+e+"\n");
			outLog.flush();
		}
		return hasRelease;
	}
		
/**
 * Description: This Method is used to create new Folder for PDF file Checkout
 * @param context
 * @returns: boolean
 * 
*/	
	private boolean isHighestRevision(Context context, String strPartId, String strType, String strPartName, String strWhereClause) throws FrameworkException {
		boolean isHighestVersion = false;
		StringList slSelect = new StringList();			
		slSelect.add(DomainConstants.SELECT_ID);
		slSelect.add(DomainConstants.SELECT_REVISION);
		String strId;
		MapList mlPart = new MapList();
		//Added for defect id 40556 Starts
		strType = getValidTypes(strType);
		//Added for defect id 40556 Ends
		try {
			MapList mlPartValid = DomainObject.findObjects(context, //context
					strType, //type
					strPartName,//name
					DomainConstants.QUERY_WILDCARD,//revision
					DomainConstants.QUERY_WILDCARD,//owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
					strWhereClause,//where clause
					false,//expand type
					slSelect);//object select
						//Fetching Highest Release revision --Begin	
			if (mlPartValid.size()>0) {
				mlPartValid.sort(DomainConstants.SELECT_REVISION, "descending", "String");
				Map<String, String> mReleaseHighestRevision =(Map<String, String>)mlPartValid.get(0);			
				strId = mReleaseHighestRevision.get(DomainConstants.SELECT_ID);
				if (strId.equals(strPartId)) {
					isHighestVersion = true;
				}					
			}
		} catch(Exception e) {
			outLog.print("Exception in isHighestRevision: "+e+"\n");
			outLog.flush();
		}
			return isHighestVersion;
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
	
	private void processInputParts(Context context, StringList slIndividualFCFPPName,String strUserName,String strFinalArtwork,String strExportPOA, String strFolder,String strPDFArtwork) throws Exception{
	String strFCFPPName = "";
	for (Object FCFPPName : slIndividualFCFPPName) {
		strFCFPPName = String.valueOf(FCFPPName).trim();
		if(UIUtil.isNotNullAndNotEmpty(strFCFPPName)){
			String strGlobalStateValue="Release";
			Map mpLatestFPFPP=getCheckAllRevision(context,strFCFPPName,strGlobalStateValue);
			outLog.print("Report requested by: "+strUserName+": Processing: "+FCFPPName+"\n");
			outLog.flush();		
			if( mpLatestFPFPP==null)
			{
				strGlobalStateValue="Obsolete";
				mpLatestFPFPP=getCheckAllRevision(context,strFCFPPName,strGlobalStateValue);
			}
			if(mpLatestFPFPP!=null && !mpLatestFPFPP.isEmpty())
			{
				String strFCFPPObjectId=(String)mpLatestFPFPP.get(DomainConstants.SELECT_ID);
				String strFCFPPObjectRev=(String)mpLatestFPFPP.get(DomainConstants.SELECT_REVISION);
				String strFCFPPObjectType=(String)mpLatestFPFPP.get(DomainConstants.SELECT_TYPE);
				DomainObject dobjFCFPP = DomainObject.newInstance(context, strFCFPPObjectId);					
				
				if(strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGFINISHEDPRODUCT) || strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_FINISHEDPRODUCTPART)){
					//Modified for POA Extract Requirement Id : 33653,34053,34054 --starts
					getArtWorkPDF(context,dobjFCFPP,strFCFPPObjectId,strFCFPPObjectType,strFCFPPName,strFCFPPObjectRev,strUserName,strFolder,strFinalArtwork,strExportPOA,strPDFArtwork);
					//Modified for POA Extract Requirement Id : 33653,34053,34054 --ends
				} else {
					outFailureLog.append(strFCFPPObjectType).append("|").append(strFCFPPName).append("|").append(strFCFPPObjectRev).append("|").append("Type is not valid ").append(":").append("NA").append("|").append("NA").append("|").append("NA").append("\n");
					outFailureLog.flush();
				}
			} else{
				//Added the code for Defect ID : 27594	[Reports] Mass Artwork Download - there should be an error message -Starts
				outFailureLog.append(strFCFPPName +" part is not in Release or Obsolete state").append("\n");
				outFailureLog.flush();
				//Added the code for th eDefect ID : 27594	[Reports] Mass Artwork Download - there should be an error message -Ends
			}
		}
	}
	}
	
private StringBuffer renameFile(String strFCFPPObjectName, String strGlobalState,String strFCFPPObjectType,String strArtWorkParentName,String strArtWorkParentRev,String srtArtWorkParentTitle)throws Exception {	
	
	StringBuffer strRenameFile = new StringBuffer();
	if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strGlobalState)){
		//Updated code for defect id 34566 starts		
			if(strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PACKAGINGMATERIALPART) || strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGPACKINGMATERIAL) || strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_POA) || strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGARTWORK)){
			//Updated code for defect id 34566 Ends	
				strRenameFile.append(strArtWorkParentName).append("_").append(strArtWorkParentRev).append("_").append(srtArtWorkParentTitle).append("_OBSOLETE.pdf");
			} else {
				strRenameFile.append(strFCFPPObjectName).append("_").append(strArtWorkParentName).append("_").append(strArtWorkParentRev).append("_").append(srtArtWorkParentTitle).append("_OBSOLETE.pdf");
			}
		} else {
			//Updated code for defect id 34566 starts
			if(strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PACKAGINGMATERIALPART) || strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGPACKINGMATERIAL) || strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_POA)|| strFCFPPObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGARTWORK)){
				//Updated code for defect id 34566 Ends
				strRenameFile.append(strArtWorkParentName).append("_").append(strArtWorkParentRev).append("_").append(srtArtWorkParentTitle).append("_RELEASE.pdf");
			} else{
				strRenameFile.append(strFCFPPObjectName).append("_").append(strArtWorkParentName).append("_").append(strArtWorkParentRev).append("_").append(srtArtWorkParentTitle).append("_RELEASE.pdf");
			}
		}
	return strRenameFile;
	}

private boolean checkPMP(String strCurrent,boolean bIsHighestRelease,boolean bHasRelease,boolean bIsHighestObsolete)throws Exception {	
boolean bValidPMP = false;
	if((pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strCurrent) && bIsHighestRelease) || (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strCurrent) && !bHasRelease && bIsHighestObsolete)){
			bValidPMP = true;
	}
	return bValidPMP;
}
/**Added for defect id 40556
 * @param strType
 * @return
 */
private String getValidTypes(String strType) {
	//Updated code for defect 41103 Starts
	String strValidTypes = strType;
	//Updated code for defect 41103 Ends
	if(UIUtil.isNotNullAndNotEmpty(strType)) {
		if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strType) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strType)) {
			strValidTypes = pgV3Constants.TYPE_PGFINISHEDPRODUCT+pgV3Constants.SYMBOL_COMMA+pgV3Constants.TYPE_FINISHEDPRODUCTPART;
		}
		if(pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strType)) {
			strValidTypes = pgV3Constants.TYPE_PGPACKINGMATERIAL+pgV3Constants.SYMBOL_COMMA+pgV3Constants.TYPE_PACKAGINGMATERIALPART;
		}
		if(pgV3Constants.TYPE_POA.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PGARTWORK.equalsIgnoreCase(strType)) {
			strValidTypes = pgV3Constants.TYPE_POA+pgV3Constants.SYMBOL_COMMA+pgV3Constants.TYPE_PGARTWORK;
		}
	}
	return strValidTypes;
}

//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts


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
		} finally {
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
