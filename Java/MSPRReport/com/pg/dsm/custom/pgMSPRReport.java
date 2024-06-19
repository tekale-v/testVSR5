/*   ${CLASSNAME}.java -
     Author:DSM Report Team
     Copyright (c) 2019
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
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
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

import org.apache.commons.fileupload.FileItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;  
import org.apache.poi.ss.util.CellRangeAddress;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.ContextUtil;
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
// Added for Hyperlink Requirement : 33631 -Starts
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.ss.usermodel.CreationHelper;
// Added for Hyperlink Requirement : 33631 -Ends
public class pgMSPRReport implements DomainConstants {

	public pgMSPRReport(Context context, String[] args) {
		
}

    private final StringList SL_OBJECT_COMMONINFO_SELECT  = getObjectCommonSelects();
    private  Properties props = new Properties();
    private  PrintWriter outSuccessLog = null;
    private  PrintWriter outFailureLog = null;
    private static final String REGION_NORTHAMERICA = "North America";
	private static final String REGION_EUROPE = "Europe";
	private static final String REGION_ASIA_PACIFIC = "Asia-Pacific";
	private static final String REGION_LATINAMERICA = "Latin America";
	private static final String ATTR_ORIGINATOR  = PropertyUtil.getSchemaProperty("attribute_Originator");
	private static final String ATTR_SEGMENT  = PropertyUtil.getSchemaProperty("attribute_pgSegment");
	private static final String TYPE_ASSEMBLEDPRODUCTPART = PropertyUtil.getSchemaProperty("type_pgAssembledProductPart");
	private static final String TYPE_PGDSMREPORT = PropertyUtil.getSchemaProperty("type_pgDSMReport");
    private static final String POLICY_PG_DSMREPORT = PropertyUtil.getSchemaProperty("policy_pgDSMReport");
	private static final String DEFAULT_GPS_VIEW = "Default GPS View";
	private static final String SYMBOL_COMMA  = ",";
	private static final String SYMBOL_COLON  = ":";
	private static final String SYMBOL_DOT  = ".";
	private static final String SYMBOL_TILDE  = "~";
	private static final String SYMBOL_PIPE = "|";
	private static final String SYMBOL_UNDERSCORE = "_";
	private static final String SYMBOL_HYPEN  = "-";
	private static final String SYMBOL_NEWLINE = "\n";
	private static final String FILE_EXTENSION_LOG  = "log";
	private static final String FILE_EXTENSION_ZIP  = "zip";
	private static final String MSPR_REPORT_REQUEST = "MSPRReportRequest";
	private static final String MSPRREPORTDOWNLOADREQUEST = "MSPRReportDownloadRequest";
    private static final String KEY_RM_GPS_AP = "RM GPS AP";
	private static final String KEY_RM_GPS_NA = "RM GPS NA";
	private static final String KEY_RM_GPS_EU = "RM GPS EU";
	private static final String KEY_RM_GPS_LA = "RM GPS LA";
	private static final String KEY_DIFFERENCE = "iDifference";
	private static final String KEY_DIFFERENCE_PERCENTAGE = "iDifferencePercentage";
	private static final String KEY_SUP_MTRL_TRADE_NAME = "supplierMaterialTradeName";
	private static final String KEY_MAX_CLEARED_AMOUNT  = "maxClearedAmount";
	private static final String KEY_MAX_CLEARED_AMOUNT_UNIT = "maxClearedAmountUnit";
	private static final String KEY_REQUEST_KEY  = "requestKey";
	private static final String KEY_COMMENTS  = "comments";
	private static final String KEY_FUNCTION_MATCH  = "FunctionMatch";
	private static final String KEY_SEGMENT_MATCH = "SegmentMatch";
	private static final String KEY_MATERIAL_FUNCTION = "MaterialFunction";
	private static final String KEY_APP_SEGMENT  = "APPSegment";
	private static final String KEY_COLUMN_TYPE  = "ColumnType";
	private static final String KEY_SUBSTITUTE_FOR  = "SubstituteFor";
	private static final String KEY_ALTERNATE_FOR = "AlternateFor";
	private static final String STR_ALTERNATE = "Alternate";
	private static final String STR_SUBSTITUTE = "Substitute";
	private static final String STR_EBOM  = "EBOM";
	private static final String KEY_SINGLE_FUNCTION = "singleFunction";
	private static final String KEY_REGIONS = "regions";
	private static final String KEY_FUNCTION  = "function";
	private static final String KEY_SEGMENT = "segment";
	private static final String KEY_CLEARANCE_LEVEL = "clearanceLevel";
	private static final String KEY_MAX_CLEARANCE_WEIGHT_MG = "mxWeightInMG";
	private static final String KEY_READINESS_COMMENT = "ReadinessComment";
	private static final String ATTR_PG_NETWEIGHT = PropertyUtil.getSchemaProperty("attribute_pgNetWeight");
	private static final String ATTR_PG_NETWEIGHT_UOM = PropertyUtil.getSchemaProperty("attribute_pgNetWeightUnitOfMeasure");
	private static final String STATE_OBSOLETE = "Obsolete";
	private static final String STATE_RELEASE = "Release";
	private static final String STRINGRESOURCEFILE  ="emxCPNStringResource"; 
	private static final String REPORT_MAIN_FOLDER  ="MSPRReportFolder"; 
	private static final String ERROR_DIRECTORY  ="Could not create directory"; 
	private static final String NON_OBSOLETE_PART ="Non-Obsolete version not found.";
	private static final String NO_ACCESS ="No Access to Part.";
	private static final String RELEASE_VERSION_ERROR ="Release version not found";
	private static final String ATTRIBUTE_PG_PLATFORM_TYPE  = PropertyUtil.getSchemaProperty("attribute_pgPlatformType");
	private static final String TYPE_PG_PLICHASSIS  = PropertyUtil.getSchemaProperty("type_pgPLIChassis");
	private static final String ATTRIBUTE_PG_CHASSIS_TYPE =PropertyUtil.getSchemaProperty("attribute_pgChassisType");
	private static final String RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS =PropertyUtil.getSchemaProperty("relationship_pgDocumentToChassis");
	private static final String RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM  = PropertyUtil.getSchemaProperty("relationship_pgDocumentToPlatform");
	private static final String TYPE_PG_PLIPLATFORM = PropertyUtil.getSchemaProperty("type_pgPLIPlatform");
	private static final String RELATIONSHIP_PG_PROJECT_TO_PLATFORM = PropertyUtil.getSchemaProperty("relationship_pgProjectToPlatform");
	private static final String RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA  = PropertyUtil.getSchemaProperty("relationship_pgDocumentToBusinessArea");
	private static final String TYPE_PG_PLI_BUSINESSAREA  =PropertyUtil.getSchemaProperty("type_pgPLIBusinessArea");
	private static final String RELATIONSHIP_PGPROJECT_TO_BUSINESSAREA = PropertyUtil.getSchemaProperty("relationship_pgProjectToBusinessArea");
	private static final String PG_BUSINESS_AREA = "pgBusinessArea";
	private static final String ORDER_ASCENDING  = "ascending";
	private static final String STRING = "String";
	private static final String PRODUCT_CATEGORY_PLATFORM = "Product Category Platform";
	private static final String FIELDNAME_PGBUSINESSAREA  = "pgBusinessArea";
	private static final String FIELDNAME_PG_PRODUCTCATEGORYPLATFORM  = "pgProductCategoryPlatform";
	//Added for Req Id : 32090- Starts
	private static final String KEY_CLASS  = "Class";
	private static final String KEY_SUB_CLASS  = "Sub Class";
	//Added for Req Id : 32090- Ends
	//Added the code for the defect -MSPR Report is not pulling all data in SIT environment -Starts
	private static final String DEFAULT_DATA_RETRIVAL_OPTION= "By Segment";
	//Added the code for the defect -MSPR Report is not pulling all data in SIT environment -Ends
	// Added for Hyperlink Requirement : 33631 -Starts
	private static final String HYPERLINK = "Hyperlink:|";
	private static final String HYPERLINK_PIPE = "|";
	private static final String HYPERLINK_COMPARE = "Hyperlink:";
	private static final String CPNRESOURCEFILE ="emxCPN";
	private static final String DATE_YMDHS = "yyyy.MM.dd.HH.mm.ss";
	private static final String DIRECTORY_ERR = "Could not create directory";	
	private static final String CATALINA_BASE="catalina.base";
	private static final String PATH_SEPARATOR="/";
	private PrintWriter outLog = null;
	//Added code for 2018x.6 Requirement id Ability to generate MSPR report with-without hyperlink Starts
	private static final String HYPERLINKASINPUT = "HyperlinkAsInput";
	//Added code for 2018x.6 Requirement id Ability to generate MSPR report with-without hyperlink Ends
	private static final String REALTIMEPROCESS = "RealTimeProcess";
	//Added for 2018x6.1 Sept requirement Strats
	private static final String KEY_READINESS_REASON = "ReadinessReason";
	//Added for 2018x6.1 Sept requirement Ends
	//Added for defect id 46245 starts
	private static final String TYPEPGPLIPRODUCTCATEGORYPLATFORM = PropertyUtil.getSchemaProperty(null,"type_pgPLIProductCategoryPlatform");
	//Added for defect id 46245 ends
	
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		private static final String STRFROMEMAILID = "strFromEmailId";
		private static final String STRTOEMAILID = "strToEmailId";
		private static final String STRSUBJECT = "strSubject";
		private static final String STRMESSAGEBODY = "strMessageBody";
		private static final String STR_PERSON_PLM_ADMIN = "PLM Admin";
		private static final String STR_COMMON="common";
		private static final String STR_SYMBOL_BRACKETS="{0}";
		private static final String STR_TEXT_HTML="text/html";
		private static final String CPNSTRINGRESOURCE = "emxCPNStringResource";
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	/**
	 * 
     * this method is to generate the Exel Sheet for the manual entry from the user side
     * 
     * @param args
     * @return void
     * @throws Exception
     */
 public void generateMSPRReportForManualEntry(Context context, String args[]) throws Exception {
	    HashMap hmArgs =(HashMap) JPO.unpackArgs(args);
	    String strUserName =(String) hmArgs.get("UserName");
	    String strPartNames =(String) hmArgs.get("GCAS");
	    String strChangeActions =(String) hmArgs.get("ChangeAction");
	    String strReportFileName =(String) hmArgs.get("ReportFileName");
	    String strReportObjectId =(String) hmArgs.get("ReportObjectId");
	    String strRegionSelected =(String) hmArgs.get("RegionSelected");
	    //System.out.println("strRegionSelected--------161----------->"+strRegionSelected);
	    String strLatestReleasePartOnly =(String) hmArgs.get("MSPRLatestReleasePartOnly");
	    String strSeparator =(String) hmArgs.get("Separator");
	    //Added code for 2018x.6 Requirement 37981 Ability to generate MSPR report with-without hyperlink Starts
	    String strHyperlink = (String) hmArgs.get(HYPERLINKASINPUT);
	    //Added code for 2018x.6 Requirement 37981 Ability to generate MSPR report with-without hyperlink Ends
	    //Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Starts
	    String strRealTimeProcess = (String) hmArgs.get(REALTIMEPROCESS);
	    //Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Starts
	    //Reduce Parameter Starts
	    Map<String,String> mPassValue = new HashMap<>();
	    mPassValue.put(REALTIMEPROCESS,strRealTimeProcess);
	    mPassValue.put(HYPERLINKASINPUT,strHyperlink);
	    mPassValue.put("MSPRLatestReleasePartOnly",strLatestReleasePartOnly);
	    mPassValue.put("RegionSelected",strRegionSelected);
	    mPassValue.put("Separator",strSeparator);
	    //Reduce Parameter Ends
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames) || UIUtil.isNotNullAndNotEmpty(strChangeActions))){
	    	DomainObject doObj  =DomainObject.newInstance(context, strReportObjectId);
	    	doObj.promote(context);
	    	//code to write the input entered by the user in the text file or by manual entry into the input file : Starts
	    	//logUserInputData(context,strUserName,strPartNames,strChangeActions);
	    	//code to write the input entered by the user in the text file or by manual entry into the input file : Ends
	    	//Updated code for Hyperlink Starts
	    	generateMSPRReport(context,strUserName,strPartNames,strChangeActions,strReportFileName,strReportObjectId,mPassValue);
	    	//Updated code for Hyperlink Ends
	       }
	   }

    /**
     * this method is to generate the Exel Sheet
     * 
     * @param args
     * @return void
     * @throws Exception
     */
	public void generateMSPRReport(Context context,String strUserName,String strPartNames,String strChangeActions,String strReportFileName, String strReportObjectId,Map<String,String> mPassValue) throws Exception {
		File fFolderPath = null; 		
		File fLogFolder = null;
		StringBuilder sbLogFolder = new StringBuilder();
		String strDirectoryNotCreated = DIRECTORY_ERR;
		String strStartTime = null; 
		String sLanguage = context.getSession().getLanguage();
		String strJVM = getJVMInstance();
		
		try {
			//Create Log file and folder
			//Added code to reduce number of parameter starts
			String strRealTimeProcess = mPassValue.get(REALTIMEPROCESS);
			String strSeparator = mPassValue.get("Separator");
			String strRegionSelected = mPassValue.get("RegionSelected");
			String strLatestReleasePartOnly = mPassValue.get("MSPRLatestReleasePartOnly");
			//Added code to reduce number of parameter Ends
			//Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Starts
			String configLOGFilePath = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
				configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Log.FilePath");
			else
				configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReportCTRLMJob.Log.FilePath");
			//Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Starts
			sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
		    fLogFolder = new File(sbLogFolder.toString());
		    if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
		    	throw new IOException(strDirectoryNotCreated + fLogFolder);
		    }
		    strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());	
		    outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"MSPRReportLog.log",true));
		    outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+" | JVM: "+strJVM+"--------\n");
		 	outLog.print("Parts: "+strPartNames+"\n");
	 	    outLog.print("Change Actions: "+strChangeActions+"\n"); 
	 	    outLog.print("Regions: "+strRegionSelected+"\n");
	 	    outLog.print("Report Object Id: "+strReportObjectId+"\n");
	 	    outLog.flush();
		    //End Create Log file and folder
			String sSTRINGRESOURCEFILE = "emxCPN";
			String strType  =TYPE_ASSEMBLEDPRODUCTPART;
			StringList slIndividualCAName = new StringList();
			StringList slIndividualPartNames = new StringList();
			StringList slSelect = new StringList(10);			
			slSelect.add(DomainConstants.SELECT_ID);	
		    slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			slSelect.add(DomainConstants.SELECT_CURRENT);
			slSelect.add(DomainConstants.SELECT_NAME);	
			slSelect.add(DomainConstants.SELECT_TYPE);
			slSelect.add(DomainConstants.SELECT_REVISION);
			slSelect.add(DomainConstants.SELECT_LAST_ID);
			slSelect.add(AWLUtil.strcat("attribute[", ATTR_ORIGINATOR, "]"));
			slSelect.add(AWLUtil.strcat("attribute[", ATTR_SEGMENT, "]"));
			String strPartName =null;
			String strCAName  = DomainConstants.EMPTY_STRING;
			String strPartType = DomainConstants.EMPTY_STRING;
		    StringList slDuplicates = new StringList();
		    StringList slRegionsList  =new StringList();
		    StringList slRegionsListFinal  =new StringList();
		    
			String strNorthAmerica  = DomainConstants.EMPTY_STRING;	
			String strEurope  = DomainConstants.EMPTY_STRING;	
			String strAsiaPacific = DomainConstants.EMPTY_STRING;
			String strLatinAmerica  = DomainConstants.EMPTY_STRING;
			String strRegions = DomainConstants.EMPTY_STRING;
			Map mp = null;
			MapList mlChangeAction  = new MapList();
			String strCAObjectId = null;
			String strPartObjectId  = null;
			HashMap hmArgs  = new HashMap();
			MapList mlRelatedParts  = new MapList();
			DomainObject dobjRelatedPart = null;
			Map mpTSInfo  = null;
			Map mTSDetail = null;
			String strConnectedPartId = null;
			String strConnectedPartName = DomainConstants.EMPTY_STRING;
			String strConnectedPartRevsion = DomainConstants.EMPTY_STRING;
			String strConnectedPartState = DomainConstants.EMPTY_STRING;
			String strPart  = null;
			MapList mlPartRelease = new MapList();
			MapList mlPart  = new MapList();	
			Map mp2 = null;
			Iterator itr  = null;
			Iterator itr2 = null;
			String strRelatedPartObjectId  = null;
			MapList mlMSPRReportLiset = null;
			//Added for Req Id : 32090- Starts
			MapList mlMSPRReportList = new MapList();
			//Added for Req Id : 32090- Ends
			String strInputPartRevision =DomainConstants.EMPTY_STRING;
			String strInputPartName =DomainConstants.EMPTY_STRING;
			String strInputPartState  =DomainConstants.EMPTY_STRING;
			Map mpCommonColumns  =new HashMap();
			String strfinalRegionVal  =DomainConstants.EMPTY_STRING;   
			StringBuffer sbStateCheck =new StringBuffer();
			StringBuffer sbStateAndRevCheck=new StringBuffer();
			boolean bAccess =false;
			String strStateCheck =sbStateCheck.append("(").append("current==").append(STATE_RELEASE).append(")").toString();
			String strStateAndRevsionCheck =sbStateAndRevCheck.append("(").append("current!=").append(STATE_RELEASE).append(" && ").append("current!=").append(STATE_OBSOLETE).append(")").append(" && ").append("revision==").append("last").toString();
			if(strSeparator ==null)
			    	strSeparator = DomainConstants.EMPTY_STRING;
			if(strSeparator.equals(SYMBOL_COLON)) {
				slRegionsList = FrameworkUtil.split(strRegionSelected, SYMBOL_COLON);
			}
			else {
				 slRegionsList = FrameworkUtil.split(strRegionSelected, SYMBOL_TILDE);
			}
			   
			if(!strSeparator.equals(SYMBOL_COLON)) {
				for(int i=0;i<slRegionsList.size()-1;i++) {
					strRegions =(String)slRegionsList.elementAt(i);
					if(strRegions.equals(REGION_NORTHAMERICA)) {
						strNorthAmerica =strRegions;
						slRegionsListFinal.add(strNorthAmerica);	
					}
					if(strRegions.equals(REGION_EUROPE)) {
						strEurope =strRegions;
						slRegionsListFinal.add(strEurope);	
					}
	                if(strRegions.equals(REGION_ASIA_PACIFIC)) {
	                	strAsiaPacific  =strRegions;
	                	slRegionsListFinal.add(strAsiaPacific);	
	                }
	                if(strRegions.equals(REGION_LATINAMERICA)) {
	                	strLatinAmerica =strRegions;
	                	slRegionsListFinal.add(strLatinAmerica);	
	                }
	              }
					if(slRegionsListFinal.size()>0) {
					strfinalRegionVal = slRegionsListFinal.toString();
					strfinalRegionVal = strfinalRegionVal.replace("[", "").replace("]", "").trim();
				} 
		      }	
			// Mass MSPRReport Folder creation-- Starts 	
	        BufferedWriter bwZipinFiles = null;
			File fMSPRParentFolder = null;
			StringBuffer sbZipFileName  = new StringBuffer();
			StringBuffer sbZipFolderName = new StringBuffer();
			StringBuffer sbMSPRFolder = new StringBuffer();
			StringBuffer strCurrentDirPath = new StringBuffer();
			long lupdateStartTime  = System.currentTimeMillis();
			Date now  = new Date();
		    SimpleDateFormat smpdf  = new SimpleDateFormat("MM-dd-yyyy");
		    String strReportExtractedDate  = smpdf.format(now);	
		    String strUserName_temp = strUserName.replace(".","-");
			String strFileBaseNameFolder = DomainConstants.EMPTY_STRING;
			StringBuffer sbFileBaseFolder  =new StringBuffer();
			if(UIUtil.isNotNullAndNotEmpty(strReportFileName)) {
				strFileBaseNameFolder = sbFileBaseFolder.append(strReportFileName).append(SYMBOL_UNDERSCORE).append(strReportExtractedDate).append(SYMBOL_UNDERSCORE).append(strUserName_temp).append(SYMBOL_UNDERSCORE).append(lupdateStartTime).toString();
			} else {
				strFileBaseNameFolder = sbFileBaseFolder.append(MSPRREPORTDOWNLOADREQUEST).append(SYMBOL_UNDERSCORE).append(strReportExtractedDate).append(SYMBOL_UNDERSCORE).append(strUserName_temp).append(SYMBOL_UNDERSCORE).append(lupdateStartTime).toString();
				
			}
			sbZipFileName.append(strFileBaseNameFolder).append(SYMBOL_DOT).append(FILE_EXTENSION_ZIP);
			String configFilePath = DomainConstants.EMPTY_STRING;
			//Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Starts
			if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
				configFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.MSPRReport.Worksheet.FilePath");
			else
				configFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.MSPRReportCTRLMJob.Worksheet.FilePath");
			//Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Ends
			sbMSPRFolder.append(configFilePath).append(java.io.File.separator);			
			fMSPRParentFolder = new File(sbMSPRFolder.toString());			
			if (!fMSPRParentFolder.exists() && !fMSPRParentFolder.mkdirs())  {
				throw new IOException(ERROR_DIRECTORY + fMSPRParentFolder);
			}
			
			strCurrentDirPath.append(sbMSPRFolder.toString()).append(strFileBaseNameFolder).append(java.io.File.separator);
			fFolderPath = new File((new StringBuffer(strCurrentDirPath.toString()).append(java.io.File.separator)).toString());
			
			if (!fFolderPath.exists() && !fFolderPath.mkdirs()) 
			{
				throw new IOException(ERROR_DIRECTORY + fFolderPath);
			}
			// Mass MSPRReport Folder creation-- Ends 	
			
			// Log File Creation -- Starts
			outSuccessLog = new PrintWriter(new FileOutputStream(fFolderPath.toString()+ File.separator  + strUserName + "_DownloadSuccessLog_"+String.valueOf(System.currentTimeMillis())+SYMBOL_DOT+FILE_EXTENSION_LOG,true));
			outFailureLog = new PrintWriter(new FileOutputStream(fFolderPath.toString()+ File.separator  + strUserName + "_DownloadFailureLog_"+String.valueOf(System.currentTimeMillis())+SYMBOL_DOT+FILE_EXTENSION_LOG,true));
			outSuccessLog.print("Part Name|Region Requested"+ "\n" );
			outSuccessLog.flush();
			
		   // Log File Creation -- Ends	
			
			//Code for Change Action ---Begin	
				//Added for Feb22_CW requirement 41852 Starts
				Map<String,Object> mpStatusPCP = new HashMap<>();
				Map<String,String> mpFinalValue = new HashMap<>();
				//Added for Feb22_CW requirement 41852 Ends
				slIndividualCAName = FrameworkUtil.split(strChangeActions, SYMBOL_COMMA);
				for (int j = 0; j < slIndividualCAName.size(); j++) {
				//for (Object CAName : slIndividualCAName) {
					strCAName = (String)slIndividualCAName.get(j);	
					strCAName = String.valueOf(strCAName).trim();	
					mlChangeAction =DomainObject.findObjects(context,pgV3Constants.TYPE_CHANGEACTION,strCAName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,DomainConstants.EMPTY_STRING,false,slSelect);
						if(mlChangeAction.size()>0 && mlChangeAction!=null){
							itr  = mlChangeAction.iterator();
							while(itr.hasNext()){
								mp = (Map)itr.next();
								strCAObjectId = (String)mp.get(DomainConstants.SELECT_ID);	
								//Code Upgrade for 2018x Data Model Starts
								ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
								mlRelatedParts = getProposedRealizedActivitiesFromCA(context,strCAObjectId,slSelect);
								//Code Upgrade for 2018x Data Model Ends
									ContextUtil.popContext(context);
									itr2 = mlRelatedParts.iterator();
									while(itr2.hasNext()){
										mp2 = (Map)itr2.next();
										strRelatedPartObjectId = (String)mp2.get(DomainConstants.SELECT_ID);
										slDuplicates.addElement(strRelatedPartObjectId);
										dobjRelatedPart  = DomainObject.newInstance(context, strRelatedPartObjectId);
									    mpTSInfo = dobjRelatedPart.getInfo(context, slSelect);	
									    strPartName  = (String)mpTSInfo.get(DomainConstants.SELECT_NAME);
									    strPartType  = (String)mpTSInfo.get(DomainConstants.SELECT_TYPE);
										strConnectedPartId = (String)mpTSInfo.get(DomainConstants.SELECT_ID);
										strConnectedPartName = (String)mpTSInfo.get(DomainConstants.SELECT_NAME); 
										strConnectedPartRevsion  = (String)mpTSInfo.get(DomainConstants.SELECT_REVISION);
										strConnectedPartState  = (String)mpTSInfo.get(DomainConstants.SELECT_CURRENT);
										bAccess  =accessCheck(context,strUserName,strRelatedPartObjectId);
										if(strPartType.equals(TYPE_ASSEMBLEDPRODUCTPART)) {
											if(bAccess) {
												if(UIUtil.isNotNullAndNotEmpty(strConnectedPartId) && !strConnectedPartState.equals(STATE_OBSOLETE)) {
													hmArgs.put("objectId", strConnectedPartId);
													hmArgs.put("rollupOption", DEFAULT_GPS_VIEW);
													//Added the code for the defect -MSPR Report is not pulling all data in SIT environment -Starts
													hmArgs.put("queryOption", DEFAULT_DATA_RETRIVAL_OPTION);
													//Added the code for the defect -MSPR Report is not pulling all data in SIT environment -Ends
													if(strSeparator.equals(":")) {
													 	String strRegionsData = slRegionsList.get(j);
														StringList slRegionsListData = FrameworkUtil.split(strRegionsData, SYMBOL_COMMA);														
														
													    for(int i=0;i<slRegionsListData.size();i++) {
													    	strRegions  =(String)slRegionsListData.elementAt(i);
															if(strRegions.equals(REGION_NORTHAMERICA)) {
																strNorthAmerica =strRegions;
															} else {
																strNorthAmerica="";
															}
															if(strRegions.equals(REGION_EUROPE)) {
																strEurope =strRegions;
															} else {
																strEurope="";
															}																		
														    if(strRegions.equals(REGION_ASIA_PACIFIC)) {
														        strAsiaPacific =strRegions;
														  }  else {
														     strAsiaPacific="";
														   }
														  if(strRegions.equals(REGION_LATINAMERICA)) {
															  strLatinAmerica=strRegions;
														  } else {
														       strLatinAmerica="";
														  }
														  hmArgs.put("NorthAmerica", strNorthAmerica);
														  hmArgs.put("Europe", strEurope);
														  hmArgs.put("AsiaPacific", strAsiaPacific);
														  hmArgs.put("LatinAmerica", strLatinAmerica);
														}
															strfinalRegionVal = slRegionsListData.toString();
															strfinalRegionVal = strfinalRegionVal.replace("[", "").replace("]", "").trim();
														}else {
														hmArgs.put("NorthAmerica", strNorthAmerica);
														hmArgs.put("Europe", strEurope);
														hmArgs.put("AsiaPacific", strAsiaPacific);
														hmArgs.put("LatinAmerica", strLatinAmerica);
														}
														//Added for Feb22_CW requirement 46245 Starts
														mpStatusPCP = getBusinessAreaPCP(context,strConnectedPartId);
														mpFinalValue = getFinalValue(mpStatusPCP);
														hmArgs.put("ProductCategoryPlatform",mpFinalValue.get("PCP"));
														hmArgs.put("BusinessArea",mpFinalValue.get("BusinessArea"));
														//Added for Feb22_CW requirement 46245 Ends
														mlMSPRReportLiset=(MapList)JPO.invoke(context,"pgDSMLayeredProductComparisonReport",null,"getDataForMSPRReport",JPO.packArgs(hmArgs),MapList.class);
														//Added for Req Id : 32090- Starts
														for(int iIndex=0;iIndex < mlMSPRReportLiset.size();iIndex++) {
															Map mp1 = (Map)mlMSPRReportLiset.get(iIndex);
															String strId=(String)mp1.get(DomainConstants.SELECT_ID);
															DomainObject doObject = DomainObject.newInstance(context, strId);
															String strClass = doObject.getInfo(context,"attribute[pgClass]");
															String strSubClass = doObject.getInfo(context,"attribute[pgSubClass]");
															mlMSPRReportList.add(mp1);
														}
													    mpTSInfo.put("RegionSelected",strfinalRegionVal);
													    //Added for Feb22_CW requirement 46245 Starts
													    mpTSInfo.put("ProductCategoryPlatform",mpStatusPCP.get("PCP").toString().replace("[","").replace("]",""));
													    mpTSInfo.put("BusinessArea",mpStatusPCP.get("BusinessArea").toString().replace("[","").replace("]",""));
														//Added for Feb22_CW requirement 46245 Ends
														mpCommonColumns.put("CommonColumns", mpTSInfo);
														mlMSPRReportList.add(mpCommonColumns);
														outSuccessLog.append(strCAName).append(":").append(strPartName).append(SYMBOL_PIPE).append(strfinalRegionVal).append(SYMBOL_NEWLINE);
														outSuccessLog.flush();
														outLog.print("CA to be processed: "+strUserName+"|"+strCAName+"| "+strPartName+"|"+strConnectedPartId+"|Region Requested:" +strfinalRegionVal+ "\n");
														outLog.flush();
														//Create the .xlsx file for the each part
														createExcelWorkbook(context,mlMSPRReportList,strConnectedPartName,strConnectedPartRevsion,strConnectedPartState,fFolderPath.toString(),mPassValue); 
														//Added for Req Id : 32090- Ends
														outLog.print("Report Completed for: "+strUserName+": "+strCAName+"|"+strConnectedPartName+ "\n");
														outLog.flush();
													
												} else {
														if(UIUtil.isNotNullAndNotEmpty(strConnectedPartRevsion)) {
															outFailureLog.append(strConnectedPartRevsion).append(SYMBOL_PIPE).append(NON_OBSOLETE_PART).append(SYMBOL_NEWLINE);
															outFailureLog.flush();
															outLog.print(strConnectedPartRevsion+"| "+NON_OBSOLETE_PART+ "\n");
															outLog.flush();
														}
													}
											    }else {
										    	outFailureLog.append(strConnectedPartRevsion).append(SYMBOL_PIPE).append(NO_ACCESS).append(SYMBOL_NEWLINE);
												outFailureLog.flush();
												outLog.print(strConnectedPartRevsion+"| "+NO_ACCESS+ "\n");
												outLog.flush();
										    }
									    }	
									 }
								  }
							}
						}
				
					//Code for Change Action ---End
				
					//Code for Part ---Begin
						slIndividualPartNames = FrameworkUtil.split(strPartNames, SYMBOL_COMMA);
						Map mReleaseHighestRevision  =null;
						for (int i = 0; i < slIndividualPartNames.size(); i++) {
							strPartName = (String)slIndividualPartNames.get(i);
							strPartName= String.valueOf(strPartName).trim();
							mlPart.clear();
						    if(UIUtil.isNotNullAndNotEmpty(strLatestReleasePartOnly) && strLatestReleasePartOnly.equals("true")) {
								//Code to feth the Latest Release Parts only
								mlPartRelease = DomainObject.findObjects(context,strType,strPartName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,strStateCheck,false,slSelect);
								//Fetching Highest Release revision --Begin	
								mlPartRelease.sort(DomainConstants.SELECT_REVISION, "descending", "string");
								if (mlPartRelease.size()>0){
									mReleaseHighestRevision  =(Map)mlPartRelease.get(0);
									mlPart.add(mReleaseHighestRevision);
								}
								//Fetching Highest Release revision --End
								//Added the code for Defect ID : 29370	[18x.2 DSM Reports: wrong log message for GCAS that has no released part - Starts
								if(mlPartRelease.size() <=0 ){
									if(UIUtil.isNotNullAndNotEmpty(strPartName)) {
									outFailureLog.append(strPartName).append(SYMBOL_PIPE).append(RELEASE_VERSION_ERROR).append(SYMBOL_NEWLINE);
									outFailureLog.flush();
								 }
								} 
								//Added the code for Defect ID : 29370	[18x.2 DSM Reports: wrong log message for GCAS that has no released part - Ends
								} else {
							    //Code to feth Latest Release Parts and any existing preliminary/pre-Release 
								mlPartRelease = DomainObject.findObjects(context,strType,strPartName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,strStateCheck,false,slSelect);
								mlPart = DomainObject.findObjects(context,strType,strPartName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,strStateAndRevsionCheck,false,slSelect);
								
								//Fetching Highest Release revision --Begin	
								mlPartRelease.sort(DomainConstants.SELECT_REVISION, "descending", "string");
								if (mlPartRelease.size()>0) {
								mReleaseHighestRevision  = (Map)mlPartRelease.get(0);								
									mlPart.add(mReleaseHighestRevision);
								}
								//Fetching Highest Release revision --End								
							}
							 if(mlPart.size()>0 && mlPart!=null){
								itr = mlPart.iterator();
								while(itr.hasNext()){
									mp = (Map)itr.next();
									strPartObjectId  = (String)mp.get(DomainConstants.SELECT_ID);
									strInputPartName = (String)mp.get(DomainConstants.SELECT_NAME); 
									strInputPartRevision = (String)mp.get(DomainConstants.SELECT_REVISION);
									strInputPartState  = (String)mp.get(DomainConstants.SELECT_CURRENT);
									bAccess = accessCheck(context,strUserName,strPartObjectId);
									if(bAccess) {
										hmArgs.put("objectId", strPartObjectId);
										hmArgs.put("rollupOption", DEFAULT_GPS_VIEW);
										//Added the code for the defect -MSPR Report is not pulling all data in SIT environment -Starts
										hmArgs.put("queryOption", DEFAULT_DATA_RETRIVAL_OPTION);
										//Added the code for the defect -MSPR Report is not pulling all data in SIT environment -Ends
										if(strSeparator.equals(":")) {
									    	String strRegionsData =slRegionsList.get(i);											
											StringList slRegionsListData = FrameworkUtil.split(strRegionsData, SYMBOL_COMMA);
											for(int j=0;j<slRegionsListData.size();j++) {
												strRegions  =(String)slRegionsListData.elementAt(j);
												if(strRegions.equals(REGION_NORTHAMERICA)) {
													strNorthAmerica =strRegions;
												} else {
													strNorthAmerica="";
												}
												if(strRegions.equals(REGION_EUROPE)) {
													strEurope =strRegions;
												} else {
													strEurope="";
												}
														
										        if(strRegions.equals(REGION_ASIA_PACIFIC)) {
										           	strAsiaPacific =strRegions;
										       }  else {
										           	strAsiaPacific="";
												}
										        if(strRegions.equals(REGION_LATINAMERICA)) {
										          	strLatinAmerica=strRegions;
										        } else {
										         	strLatinAmerica="";
												}
										            hmArgs.put("NorthAmerica", strNorthAmerica);
													hmArgs.put("Europe", strEurope);
													hmArgs.put("AsiaPacific", strAsiaPacific);
													hmArgs.put("LatinAmerica", strLatinAmerica);
										         }
												strfinalRegionVal = slRegionsListData.toString();
												strfinalRegionVal = strfinalRegionVal.replace("[", "").replace("]", "").trim();
										  } else {
											hmArgs.put("NorthAmerica", strNorthAmerica);
											hmArgs.put("Europe", strEurope);
											hmArgs.put("AsiaPacific", strAsiaPacific);
											hmArgs.put("LatinAmerica", strLatinAmerica);
										}
									//Added for Feb22_CW requirement 46245 Starts
									mpStatusPCP = getBusinessAreaPCP(context,strPartObjectId);
									mpFinalValue = getFinalValue(mpStatusPCP);
									hmArgs.put("ProductCategoryPlatform",mpFinalValue.get("PCP"));
									hmArgs.put("BusinessArea",mpFinalValue.get("BusinessArea"));
									//Added for Feb22_CW requirement 46245 Ends
									mlMSPRReportLiset=(MapList)JPO.invoke(context,"pgDSMLayeredProductComparisonReport",null,"getDataForMSPRReport",JPO.packArgs(hmArgs),MapList.class);
									//Added for Req Id : 32090- Starts
									for(int iIndex=0;iIndex<mlMSPRReportLiset.size();iIndex++) {
									Map mp1 = (Map)mlMSPRReportLiset.get(iIndex);
									String strId=(String)mp1.get(DomainConstants.SELECT_ID);
									DomainObject doObject = DomainObject.newInstance(context, strId);
									String strClass = doObject.getInfo(context,"attribute[pgClass]");
									String strSubClass = doObject.getInfo(context,"attribute[pgSubClass]");
									mp1.put("Class",strClass);
									mp1.put("Sub Class",strSubClass);
									mlMSPRReportList.add(mp1);
									}
									//Added for Feb22_CW requirement 46245 Starts
									mp.put("ProductCategoryPlatform",mpStatusPCP.get("PCP").toString().replace("[","").replace("]",""));
									mp.put("BusinessArea",mpStatusPCP.get("BusinessArea").toString().replace("[","").replace("]",""));
									//Added for Feb22_CW requirement 46245 Ends
									mp.put("RegionSelected",strfinalRegionVal);
									mpCommonColumns.put("CommonColumns", mp);
									mlMSPRReportList.add(mpCommonColumns);
									outLog.print("Part to be processed: "+strUserName+"|"+strInputPartName+"|"+strPartObjectId+"|Region Requested:" +strfinalRegionVal+ "\n");
									outLog.flush();
									//Create the .xlsx file for the each part
									createExcelWorkbook(context,mlMSPRReportList,strInputPartName,strInputPartRevision,strInputPartState,fFolderPath.toString(),mPassValue); 
									//Added for Req Id : 32090- Ends									
									String strEndTime = null; 
									strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
									outLog.print("------Report completed for: "+strUserName+": "+strInputPartName+"|" +strReportFileName+ "-------\n");
									outLog.print("------Time completed: "+strEndTime+"-------\n");	
									outLog.print("------\n");
									outLog.flush();
									outSuccessLog.append(strPartName).append(SYMBOL_PIPE).append(strfinalRegionVal).append(SYMBOL_NEWLINE);
									outSuccessLog.flush();
								  }	else {
								    	outFailureLog.append(strConnectedPartRevsion).append(SYMBOL_PIPE).append(NO_ACCESS).append(SYMBOL_NEWLINE);
										outFailureLog.flush();
										outLog.print(strConnectedPartRevsion+"| "+NO_ACCESS+ "\n");
										outLog.flush();
								    }
							  }
							//Added the code for Defect ID : 29370	[18x.2 DSM Reports: wrong log message for GCAS that has no released part - Starts
							 } else {
								if(UIUtil.isNotNullAndNotEmpty(strPartName) && strLatestReleasePartOnly.equals("false")) {
									outFailureLog.append(strPartName).append(SYMBOL_PIPE).append(NON_OBSOLETE_PART).append(SYMBOL_NEWLINE);
									outFailureLog.flush();
									outLog.print(strPartName+"| "+NON_OBSOLETE_PART+ "\n");
									outLog.flush();
								}
								
							}//Added the code for Defect ID : 29370	[18x.2 DSM Reports: wrong log message for GCAS that has no released part - Ends
						}
						
			//Zip file creation
			if (fFolderPath.exists()) {
				sbZipFolderName.append(sbMSPRFolder.toString()).append(java.io.File.separator).append(sbZipFileName);
				zipFiles(sbZipFolderName.toString(), fFolderPath.toString());
			} 	
			
		  // code to create the object and checking the .zip file in that object
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		  createDSMReportObject(context,sbMSPRFolder.toString(),sbZipFileName.toString(),strReportObjectId,strUserName);
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
		  outSuccessLog.close();
		  outFailureLog.close();
		  deleteFiles(context,fFolderPath.toString());
		  String strEndTime = null; 
			strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
			outLog.print("------Report completed for: "+strUserName+": "+strPartNames+"|" +strReportFileName+ "-------\n");
			outLog.print("------Time completed: "+strEndTime+"-------\n");	
			outLog.print("------\n");
			outLog.flush();
		 }catch (Exception e) {			
			outLog.print("Exception in  generateMSPRReport: "+strUserName+": "+e+"\n");
			outLog.flush();
		} finally {
			//Added code to delete the folder in which the files got checked out. -Starts
			outLog.close();
			//deleteFiles(context,fFolderPath.toString());
			//Added code to delete the folder in which the files got checked out. -Ends
		}
	}


	/**
     * this method is to generate the Exel Sheet for the input .txt file which will be browsed by the user
     * 
     * @param args
     * @return void
     * @throws Exception
     */
 public void generateMSPRReportForInputFile(Context context, String args[]) throws Exception {
	    //CHANGES FOR 22X UPGRADE START
	    FileReader fileReaderMQLGeneratedInputFile  = null;
	    //CHANGES FOR 22X UPGRADE END
	    BufferedReader buffReaderForMQLGeneratedInputFile  = null;
	    StringList slCompleteRow = new StringList();
	    String strPartOrCAFromInputFile = DomainConstants.EMPTY_STRING;
	    String strRegionsFromInputFile  = DomainConstants.EMPTY_STRING;
	    String strChangeActions = DomainConstants.EMPTY_STRING;
		String strPartNames = DomainConstants.EMPTY_STRING;
	    String line  = DomainConstants.EMPTY_STRING;
	    HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
	    String strReportFileName = (String) hmArgs.get("ReportFileName");
	    String strLatestReleasePartOnly = (String) hmArgs.get("MSPRLatestReleasePartOnly");
		String strReportObjectId = (String) hmArgs.get("ReportObjectId");
		//Added code for 2018x.6 Requirement 37981 Ability to generate MSPR report with-without hyperlink Starts
		String strHyperlink = (String) hmArgs.get(HYPERLINKASINPUT);
		//Added code for 2018x.6 Requirement 37981 Ability to generate MSPR report with-without hyperlink Ends
		//Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Starts
		String strRealTimeProcess = (String) hmArgs.get(REALTIMEPROCESS);
		//Added code for 2018x.6 Requirement 36722 Generate MSPR Report directly Starts
		//Updated for ALM 40940 Pass all form data in http body body  
		//fileReaderMQLGeneratedInputFile = new FileReader(file);
	    //buffReaderForMQLGeneratedInputFile  = new BufferedReader(fileReaderMQLGeneratedInputFile);
	    String finalVal  = DomainConstants.EMPTY_STRING;
	    String finalRegionValue =DomainConstants.EMPTY_STRING;
	    StringList slPartOrCAList  = new StringList();
	    StringList slRegionList = new StringList();
	    String strUserName  = (String) hmArgs.get("UserName");
	    //CHANGES FOR 22X UPGRADE START
	    try {
			File file  = (File) hmArgs.get("DSMReportFile");
			fileReaderMQLGeneratedInputFile = new FileReader(file);
			buffReaderForMQLGeneratedInputFile  = new BufferedReader(fileReaderMQLGeneratedInputFile);
		//CHANGES FOR 22X UPGRADE END
	   while ((line = buffReaderForMQLGeneratedInputFile.readLine()) != null) {
			 if(line != null && line.length()>0) {
				 //Added code for Defect ID : 29330	[18x.2 DSM Report]: System does not accept .txt file containing say 45 or 50 GCAS - 2018x.2--Starts
				 if (line.contains("|")){
				//Added code for Defect ID : 29330	[18x.2 DSM Report]: System does not accept .txt file containing say 45 or 50 GCAS - 2018x.2--Ends
					slCompleteRow = FrameworkUtil.split(line, SYMBOL_PIPE);	
					strPartOrCAFromInputFile = (String)slCompleteRow.get(0);
					strRegionsFromInputFile = (String)slCompleteRow.get(1);
					
					//System.out.println("strRegionsFromInputFile for Each Part----------------------->"+strRegionsFromInputFile);
					
					finalVal +=strPartOrCAFromInputFile +SYMBOL_COMMA;
					finalRegionValue  += strRegionsFromInputFile+SYMBOL_COLON;	
				    //System.out.println("finalRegionValue ALL--------603---------------->"+finalRegionValue);					
				 } 
				 //Added code for Defect ID : 29330	[18x.2 DSM Report]: System does not accept .txt file containing say 45 or 50 GCAS - 2018x.2--Starts
				 else {
					finalVal +=line+SYMBOL_COMMA;
				}
				//Added code for Defect ID : 29330	[18x.2 DSM Report]: System does not accept .txt file containing say 45 or 50 GCAS - 2018x.2--Ends
			}
		StringList strFinalList = FrameworkUtil.split(finalVal,SYMBOL_COMMA);
		finalVal = strFinalList.toString();
		finalVal = finalVal.replace("[", "").replace("]", ""); 
		strPartNames =finalVal;
		strChangeActions = finalVal;
	   }
		 //CHANGES FOR 22X UPGRADE START
	    }
	    catch(Exception e)
	    {
	    	outLog.print("Exception in  generateMSPRReportForInputFile: "+e.getMessage()+"\n");
	    }
	    finally
	    {
	    	if(fileReaderMQLGeneratedInputFile != null)
	    		fileReaderMQLGeneratedInputFile.close();
	    	if(buffReaderForMQLGeneratedInputFile != null)
	    		buffReaderForMQLGeneratedInputFile.close();
	    }
	    //CHANGES FOR 22X UPGRADE END
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames) || UIUtil.isNotNullAndNotEmpty(strChangeActions))){
	   try { 
				    HashMap hmArgs1 = new HashMap();
					hmArgs1.put("UserName",strUserName);
					hmArgs1.put("GCAS",strPartNames);
					hmArgs1.put("ChangeAction",strChangeActions);
					hmArgs1.put("ReportFileName",strReportFileName);
					hmArgs1.put("ReportObjectId",strReportObjectId);
					hmArgs1.put("MSPRLatestReleasePartOnly",strLatestReleasePartOnly);
					hmArgs1.put("RegionSelected",finalRegionValue);
					hmArgs1.put("Separator",":");
					//Added code for 2018x.6 Requirement id 37981 Ability to generate MSPR report with-without hyperlink Starts
					hmArgs1.put(HYPERLINKASINPUT, strHyperlink);
					//Added code for 2018x.6 Requirement id 37981 Ability to generate MSPR report with-without hyperlink Ends
					hmArgs1.put(REALTIMEPROCESS, strRealTimeProcess);
					BackgroundProcess backgroundProcess = new BackgroundProcess();
	                backgroundProcess.submitJob(context, "com.pg.dsm.custom.pgMSPRReport", "generateMSPRReportForManualEntry", JPO.packArgsRemote(hmArgs1) , (String)null);
	               } catch(Exception ex) {
	                ContextUtil.abortTransaction(context);
	                ex.printStackTrace();
	                outLog.print("Exception in  generateMSPRReportForInputFile: "+strUserName+": "+ex+"\n");
	    			outLog.flush();
	                throw ex;
	            }
             } 
	     //}
 }
	/**
 	* Method to get connected Parts from ChangeManagement
 	* @param context
 	* @param strCAObjectId - CA object id & its selectable
 	* @return MapList
 	*/
	public MapList getProposedRealizedActivitiesFromCA(Context context, String strCAObjectId, StringList slObject){
		MapList mlProposedRealizedParts = new MapList();
		try
		{
		MapList mlPAParts = new ChangeAction(strCAObjectId).getAffectedItems(context);
		MapList mlRAParts = new ChangeAction(strCAObjectId).getAllRealizedChanges(context);
		String strPartId = DomainConstants.EMPTY_STRING;
		String strPartLastId = DomainConstants.EMPTY_STRING;
		String strPartState = DomainConstants.EMPTY_STRING;
		Map mPartSelects = null;
		DomainObject domPAPart = null;
		DomainObject domRAPart = null;
		Map mpPAPart = null;
		Map mpRAPart = null;
		if (mlPAParts.size()>0) {
			Iterator itrPAParts = mlPAParts.iterator();						
			while (itrPAParts.hasNext()){
				mpPAPart = (Map)itrPAParts.next();	
				strPartId  = (String)mpPAPart.get(DomainConstants.SELECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
					domPAPart = DomainObject.newInstance(context,strPartId);
					mPartSelects = domPAPart.getInfo(context, slObject);
					strPartLastId	= (String) mPartSelects.get(DomainConstants.SELECT_LAST_ID);
					strPartState	= (String) mPartSelects.get(DomainConstants.SELECT_CURRENT);
					if (!STATE_OBSOLETE.equalsIgnoreCase(strPartState)) {
						mlProposedRealizedParts.add(mPartSelects);
					}
				}
			}
		}
		
		if (mlRAParts.size()>0) {
			Iterator itrRAParts = mlRAParts.iterator();						
			while (itrRAParts.hasNext()){
				mpRAPart = (Map)itrRAParts.next();	
				strPartId  = (String)mpRAPart.get(DomainConstants.SELECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
					domRAPart = DomainObject.newInstance(context,strPartId);
					mPartSelects = domRAPart.getInfo(context, slObject);
					strPartLastId	= (String) mPartSelects.get(DomainConstants.SELECT_LAST_ID);
					strPartState	= (String) mPartSelects.get(DomainConstants.SELECT_CURRENT);
					if (!STATE_OBSOLETE.equalsIgnoreCase(strPartState)) {
						mlProposedRealizedParts.add(mPartSelects);
					}
				 }
			}
		}
		
		
		}catch(Exception ex) {
			outLog.print("Exception in  getProposedRealizedActivitiesFromCA: "+ex+"\n");
			outLog.flush();
		}
		return mlProposedRealizedParts;
		
	}
			
	/**
	 * getObjectCommonSelects- Stringlist for Common Five Columns 
	 * @return
	 */
	public static StringList getObjectCommonSelects(){
		
		StringList slObjInfoSelect = new StringList(10);
		try{                 

			//Code Upgrade for 2018x Data Model -- Starts
			slObjInfoSelect.add(DomainConstants.SELECT_ID);
			//Code Upgrade for 2018x Data Model -- Ends
		    slObjInfoSelect.add(DomainConstants.SELECT_NAME);
			slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);                 
			slObjInfoSelect.add(DomainConstants.SELECT_TYPE);
			slObjInfoSelect.add(DomainConstants.SELECT_REVISION);
			slObjInfoSelect.add(DomainConstants.SELECT_DESCRIPTION);
			slObjInfoSelect.add(DomainConstants.SELECT_CURRENT);
			slObjInfoSelect.add(AWLUtil.strcat("attribute[", ATTR_ORIGINATOR, "]"));
			slObjInfoSelect.add(AWLUtil.strcat("attribute[", ATTR_SEGMENT, "]"));
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return slObjInfoSelect;
	}
	
	/**This method is used to write the input entered by manual entry or by browing the input file into the log file.
	 * @param context is the matrix context
	 * @param args has the required information
	 * @return void
	 * @throws Exception
	 */ /*
	public void logUserInputData(Context context, String strUserName, String strPartNames,String strChangeActions) throws Exception{
		String strWorkSpaceFolder = context.createWorkspace();
		StringBuffer sbFileName  = new StringBuffer(); 
		StringBuffer sbFinalData = new StringBuffer(); 
		StringBuffer sbWorkSpacePath= new StringBuffer(); 
		String strFileName = sbFileName.append(MSPR_REPORT_REQUEST).append(SYMBOL_UNDERSCORE).append(strUserName).append(SYMBOL_UNDERSCORE).append(String.valueOf(System.currentTimeMillis())).append(SYMBOL_DOT).append(FILE_EXTENSION_LOG).toString();
		String strWorkSpacePath  = sbWorkSpacePath.append(strWorkSpaceFolder).append(java.io.File.separator).append(strFileName).toString();
		File file  = new File(strWorkSpacePath);
	    FileWriter fr  = null;
	    String strFinalData  = DomainConstants.EMPTY_STRING;
	    if(UIUtil.isNotNullAndNotEmpty(strPartNames) && UIUtil.isNotNullAndNotEmpty(strChangeActions)) {
	    	if(strPartNames.equalsIgnoreCase(strChangeActions)) {
	    		strFinalData = strPartNames;
	    	}
	    	else {
	    		strFinalData = sbFinalData.append(strPartNames).append(SYMBOL_COMMA).append(strChangeActions).toString();
	    	}
	    }
	    else if(UIUtil.isNotNullAndNotEmpty(strPartNames)) {
	    	strFinalData  = strPartNames;
	    }
	    else if(UIUtil.isNotNullAndNotEmpty(strChangeActions)) {
	    	strFinalData  = strChangeActions;
	    }
	    else {
	    	strFinalData  =  DomainConstants.EMPTY_STRING;
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
	/**
     * this method is to create Excel Sheet
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     */
   public void createExcelWorkbook(Context context,MapList mlMSPRReportLiset,String strInputPartName, String strInputPartRevision, String strInputPartState,String strReportPath, Map<String,String> mPassValue) {
		try {
			//Added to reduce Parameter Starts
			String strHyperlink = mPassValue.get(HYPERLINKASINPUT);
			//Added to reduce Parameter Starts
			String sLanguage = context.getSession().getLanguage();
			String strReportName =i18nNow.getI18nString("emxCPN.MSPRReport.MSPRReport.Name",STRINGRESOURCEFILE, sLanguage);
			String strReportExtension  =i18nNow.getI18nString("emxCPN.MSPRReport.ReportExtension",STRINGRESOURCEFILE, sLanguage);
			XSSFWorkbook workbook = new XSSFWorkbook();	
			String strSheetName  = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Name.MSPRReport",STRINGRESOURCEFILE, sLanguage);
			String strUserName = context.getUser();
			XSSFSheet sheetMSPRReport  = workbook.createSheet(strSheetName);
			StringBuffer sbReportFileName  =new StringBuffer(); 
			updateWorksheetMSPRReport(context,workbook,mlMSPRReportLiset,sheetMSPRReport,strHyperlink);
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format :Start
			if(UIUtil.isNotNullAndNotEmpty(strInputPartName)) {
				strReportName  = sbReportFileName.append(strInputPartName).append(SYMBOL_UNDERSCORE).append(strInputPartRevision).append(SYMBOL_UNDERSCORE).append(strInputPartState).append(SYMBOL_UNDERSCORE).append(String.valueOf(System.currentTimeMillis())).append(SYMBOL_DOT).append(strReportExtension).toString();
				
			}
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format : End
			FileOutputStream outputStream  = new FileOutputStream(strReportPath+ File.separator + strReportName);
			workbook.write(outputStream);
			if (outputStream != null)
			{				
				outputStream.flush();
				outputStream.close();		
			}
			mlMSPRReportLiset.clear();
		} 
		catch (Exception e) {			
			outLog.print("Exception in  createExcelWorkbook: "+e+"\n");
			outLog.flush();
		} 

	}
	
	
	/**
     * this method is to Checkin Excel Sheet in to a DSM Report Object
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     * @throws Exception
     */
 //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	public void createDSMReportObject(Context context,String strReportPath,String strReportName,String strReportObjectId,String strUserName) throws MatrixException {
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
		try {
			String strContextUserName = context.getUser();
			StringBuffer sbNewObjectName=new StringBuffer();
			String strNewObjectName = sbNewObjectName.append(MSPR_REPORT_REQUEST).append(SYMBOL_UNDERSCORE).append(strContextUserName).append(SYMBOL_UNDERSCORE).append(String.valueOf(System.currentTimeMillis())).toString();
			String strObjectId = strReportObjectId;
			String strpgDSMReport  =FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE,TYPE_PGDSMREPORT, true);
			Date currentDate = new Date();
			SimpleDateFormat smpdateformat = new SimpleDateFormat("MM-dd-yyyy");
			if(UIUtil.isNullOrEmpty(strObjectId))
			{
				String appName  = FrameworkUtil.getTypeApplicationName(context, TYPE_PGDSMREPORT);
				DomainObject createBO = DomainObject.newInstance(context, TYPE_PGDSMREPORT, appName);
				BusinessObject bo = new BusinessObject(strpgDSMReport, strNewObjectName, DomainConstants.EMPTY_STRING, pgV3Constants.VAULT_ESERVICEPRODUCTION);
				if(!bo.exists(context))
				{   createBO.createObject(context, TYPE_PGDSMREPORT, strNewObjectName,DomainConstants.EMPTY_STRING, POLICY_PG_DSMREPORT, pgV3Constants.VAULT_ESERVICEPRODUCTION);
					strObjectId = createBO.getObjectId(context);

				}
			}

			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				outLog.print("Before Checkin File: "+strReportName+"| " +smpdateformat.format(currentDate)+ "\n");
				outLog.flush();	
				DomainObject doObj = DomainObject.newInstance(context, strObjectId);
				doObj.checkinFile(context, true, true, "", "generic", strReportName, strReportPath);
				doObj.promote(context);
				String sFullPath = strReportPath.concat("\\").concat(strReportName); 
				File file = new File(sFullPath);
				file.delete();
				outLog.print("After Checkin File: "+strReportName+"| " +smpdateformat.format(currentDate)+ "\n");
				outLog.flush();	
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
				sendEmail(context, strReportName, strUserName);
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
			} else {
				throw new MatrixException("Creation of Object Failed");
			}
		} catch (Exception e) {
			outLog.print("Exception in  createDSMReportObject and Checkin File: "+e+"\n");
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
			zos 				= new ZipOutputStream(new FileOutputStream(zipFile));
			int ilength = 0;
			
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
			outLog.print("Exception in  ZipFiles: "+e+"\n");
			outLog.flush();
		}
		finally
		{
			zos.close();
			
		}
	}
	

	/**
     * this method is to update Excel Sheet with MSPR Report details
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     */
	
	public void updateWorksheetMSPRReport(Context context,XSSFWorkbook workbook, MapList mlMSPRReportLiset,XSSFSheet sheetMSPRReport,String strHyperlink) {
		try {
			//Added code for 2018x.6 Requirement id Ability to generate MSPR report with-without hyperlink Starts
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.MSPRReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll = 0;
			//Added code for 2018x.6 Requirement id Ability to generate MSPR report with-without hyperlink Ends
			String sLanguage = context.getSession().getLanguage();
			String strTitle = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.Title",STRINGRESOURCEFILE, sLanguage);
			//Added for Req Id : 32090- Starts
			String strSector = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.BusinessArea",STRINGRESOURCEFILE, sLanguage);
			String strFranchise = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.ProductCategoryPlatform",STRINGRESOURCEFILE, sLanguage);
			//String strSegment = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.Segment",STRINGRESOURCEFILE, sLanguage);
			//Added for Req Id : 32090- Ends
			String strSelectedRegions = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.SelectedRegions",STRINGRESOURCEFILE, sLanguage);
			String strOriginator = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.Originator",STRINGRESOURCEFILE, sLanguage);
			String strReportGenerationTime = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.ReportGenerationTime",STRINGRESOURCEFILE, sLanguage);
			String strGCAS = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.GCAS",STRINGRESOURCEFILE, sLanguage);
			String strRevision = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.Revision",STRINGRESOURCEFILE, sLanguage);
			String strColumnType = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.ColumnType",STRINGRESOURCEFILE, sLanguage);
			String strSubstituteFor = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.SubstituteFor",STRINGRESOURCEFILE, sLanguage);
			String strAlternateFor = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.AlternateFor",STRINGRESOURCEFILE, sLanguage);
			String strTradeName = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.TradeName",STRINGRESOURCEFILE, sLanguage);
			String strReadinessComments = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.ReadinessComments",STRINGRESOURCEFILE, sLanguage);
			String strMaterialFunction = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.MaterialFunction",STRINGRESOURCEFILE, sLanguage);
			String strNetWeightFCTarget = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.NetWeightFCTarget",STRINGRESOURCEFILE, sLanguage);
			String strUoMfromFC = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.UoMfromFC",STRINGRESOURCEFILE, sLanguage);
			String strMaxAmountfromGPS = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.MaxAmountfromGPS",STRINGRESOURCEFILE, sLanguage);
			String strUoMfromGPS = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.UoMfromGPS",STRINGRESOURCEFILE, sLanguage);
			String strDifferenceMaxAmtGPSNetWtFC = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.DifferenceMaxAmtGPSNetWtFC",STRINGRESOURCEFILE, sLanguage);
			String strConvertDifferencetoPer = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.ConvertDifferencetoPer",STRINGRESOURCEFILE, sLanguage);
			String strRMGPSEUStatus = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.RMGPSEUStatus",STRINGRESOURCEFILE, sLanguage);
			String strRMGPSAPStatus = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.RMGPSAPStatus",STRINGRESOURCEFILE, sLanguage);
			String strRMGPSLAStatus = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.RMGPSLAStatus",STRINGRESOURCEFILE, sLanguage);
			String strCommentsfromGPS = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.CommentsfromGPS",STRINGRESOURCEFILE, sLanguage);
			String strMaterialFunctionMatchesbasedonmapping	= i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.MaterialFunctionMatchesbasedonmapping",STRINGRESOURCEFILE, sLanguage);
			String strSegmentMatchesbasedonmapping = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.SegmentMatchesbasedonmapping",STRINGRESOURCEFILE, sLanguage);
			String strGPSNumber = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.GPSNumber",STRINGRESOURCEFILE, sLanguage);
			//Added for Req Id : 32090- Starts
			String strClass = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.Class",STRINGRESOURCEFILE, sLanguage);
			String strSubClass = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.SubClass",STRINGRESOURCEFILE, sLanguage);
			String strRMGPSNAStatus = i18nNow.getI18nString("emxCPN.MSPRReport.Worksheet.Column.RMGPSNAStatus",STRINGRESOURCEFILE, sLanguage);
			//Added for Req Id : 32090- Ends
			//Added for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Starts
			String strReadinessStatus = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(),"emxCPN.MSPRReport.Worksheet.Column.ReadinessStatus");
			//Added for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Ends
			XSSFRow rowHeaderMSPRReportTitle = sheetMSPRReport.createRow(0);
			XSSFRow rowHeaderMSPRReportSector  = sheetMSPRReport.createRow(1);
			XSSFRow rowHeaderMSPRReportFranchise  = sheetMSPRReport.createRow(2);
			//Added for Req Id : 32090- Starts
			//XSSFRow rowHeaderMSPRReportSegement = sheetMSPRReport.createRow(3);
			XSSFRow rowHeaderMSPRReportRegions = sheetMSPRReport.createRow(3);
			XSSFRow rowHeaderMSPRReportOriginator = sheetMSPRReport.createRow(4);
			XSSFRow rowHeaderMSPRReportGenerationTime  = sheetMSPRReport.createRow(5);
			XSSFRow rowHeaderMSPRCommonColumns = sheetMSPRReport.createRow(6);
			//Added for Req Id : 32090- Ends
			XSSFCellStyle cellStyleMSPRReport  = sheetMSPRReport.getWorkbook().createCellStyle();
			XSSFCellStyle cellStyleMSPRReportCommonColumn = sheetMSPRReport.getWorkbook().createCellStyle();
			HashMap hm  = new HashMap<>();
			Map mp1 =null;
			sheetMSPRReport.addMergedRegion(new CellRangeAddress(0,0,1,21));  
			sheetMSPRReport.addMergedRegion(new CellRangeAddress(1,1,1,21));  
			sheetMSPRReport.addMergedRegion(new CellRangeAddress(2,2,1,21));  
			sheetMSPRReport.addMergedRegion(new CellRangeAddress(3,3,1,21));  
			sheetMSPRReport.addMergedRegion(new CellRangeAddress(4,4,1,21));  
			sheetMSPRReport.addMergedRegion(new CellRangeAddress(5,5,1,21)); 
			//Added for Req Id : 32090- Starts 
			//sheetMSPRReport.addMergedRegion(new CellRangeAddress(6,6,1,21));  
			//Added for Req Id : 32090- Ends
			rowHeaderMSPRCommonColumns.setHeight((short) 1575);
			XSSFFont xsfontMSPRReportCommonColumn = sheetMSPRReport.getWorkbook().createFont();
			xsfontMSPRReportCommonColumn.setBold(false);
			xsfontMSPRReportCommonColumn.setFontHeightInPoints((short) 11);
			cellStyleMSPRReportCommonColumn.setFont(xsfontMSPRReportCommonColumn);
			cellStyleMSPRReportCommonColumn.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());			
			cellStyleMSPRReportCommonColumn.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cellStyleMSPRReportCommonColumn.setBorderTop(BorderStyle.THIN);
			cellStyleMSPRReportCommonColumn.setBorderBottom(BorderStyle.THIN);
			cellStyleMSPRReportCommonColumn.setBorderLeft(BorderStyle.THIN);
			cellStyleMSPRReportCommonColumn.setBorderRight(BorderStyle.THIN);
			//Title:
			Cell cellTitle = rowHeaderMSPRReportTitle.createCell(0);
			cellTitle.setCellStyle(cellStyleMSPRReport);
			cellTitle.setCellValue(strTitle);
			
			//Sector:
			Cell cellSector  = rowHeaderMSPRReportSector.createCell(0);
			cellSector.setCellStyle(cellStyleMSPRReport);
			cellSector.setCellValue(strSector);
			
			//Franchise:
			Cell cellFranchise = rowHeaderMSPRReportFranchise.createCell(0);
			cellFranchise.setCellStyle(cellStyleMSPRReport);
			cellFranchise.setCellValue(strFranchise);
			
			//Added for Req Id : 32090- Starts
			/*
			//Segment:
			Cell cellSegment = rowHeaderMSPRReportSegement.createCell(0);
			cellSegment.setCellStyle(cellStyleMSPRReport);
			cellSegment.setCellValue(strSegment);
			*/
			//Added for Req Id : 32090- Ends
			
			//Selected Regions:
			Cell cellRegions = rowHeaderMSPRReportRegions.createCell(0);
			cellRegions.setCellStyle(cellStyleMSPRReport);
			cellRegions.setCellValue(strSelectedRegions);
			
			//Originator:
			Cell cellOriginator  = rowHeaderMSPRReportOriginator.createCell(0);
			cellOriginator.setCellStyle(cellStyleMSPRReport);
			cellOriginator.setCellValue(strOriginator);
			
			//Report Generation Time:
			Cell cellReportGenTime = rowHeaderMSPRReportGenerationTime.createCell(0);
			cellReportGenTime.setCellStyle(cellStyleMSPRReport);
			cellReportGenTime.setCellValue(strReportGenerationTime);
			
			//GCAS
			Cell cellGCAS  = rowHeaderMSPRCommonColumns.createCell(0);
			cellGCAS.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellGCAS.setCellValue(strGCAS);
			
			//Revision
			Cell cellRevision = rowHeaderMSPRCommonColumns.createCell(1);
			cellRevision.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellRevision.setCellValue(strRevision);
			
			//Title
			Cell cellColumnTitle = rowHeaderMSPRCommonColumns.createCell(2);
			cellColumnTitle.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellColumnTitle.setCellValue(strTitle);
			
			//commented the code for Defect Id :29914 - Column Type column should be deleted in the MSPR Report done thru Mass Download -Starts
			//Column Type
			//Cell cellColumnType  = rowHeaderMSPRCommonColumns.createCell(3);
			//cellColumnType.setCellStyle(cellStyleMSPRReportCommonColumn);
			//cellColumnType.setCellValue(strColumnType);
			//commented the code for Defect Id :29914 - Column Type column should be deleted in the MSPR Report done thru Mass Download -Ends
			//Trade Name
			Cell cellTradeName = rowHeaderMSPRCommonColumns.createCell(3);
			cellTradeName.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellTradeName.setCellValue(strTradeName);
			
			//Added for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Starts
			//Readiness status
			Cell cellReadinessStatus = rowHeaderMSPRCommonColumns.createCell(4);
			cellReadinessStatus.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellReadinessStatus.setCellValue(strReadinessStatus);
			
			//Readiness Comments
			Cell cellReadinessComments = rowHeaderMSPRCommonColumns.createCell(5);
			cellReadinessComments.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellReadinessComments.setCellValue(strReadinessComments);
			//Updated for 2018x6.1 Sept requirement Ends
			
			//Material Function
			Cell cellMaterialFunction = rowHeaderMSPRCommonColumns.createCell(6);
			cellMaterialFunction.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellMaterialFunction.setCellValue(strMaterialFunction);
			
			//Added for Req Id : 32090- Starts
			//Class Function
			Cell cellClass = rowHeaderMSPRCommonColumns.createCell(7);
			cellClass.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellClass.setCellValue(strClass);
			
			//Sub Class Function
			Cell cellSubClass = rowHeaderMSPRCommonColumns.createCell(8);
			cellSubClass.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellSubClass.setCellValue(strSubClass);
			
			//Net Weight(FC Target)
			Cell cellNetWeightFCTarget = rowHeaderMSPRCommonColumns.createCell(9);
			cellNetWeightFCTarget.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellNetWeightFCTarget.setCellValue(strNetWeightFCTarget);
			
			//UoM from FC
			Cell cellUoMfromFC = rowHeaderMSPRCommonColumns.createCell(10);
			cellUoMfromFC.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellUoMfromFC.setCellValue(strUoMfromFC);
			
			//Max Amount from GPS
			Cell celMaxAmountfromGPS  = rowHeaderMSPRCommonColumns.createCell(11);
			celMaxAmountfromGPS.setCellStyle(cellStyleMSPRReportCommonColumn);
			celMaxAmountfromGPS.setCellValue(strMaxAmountfromGPS);
			
			//UoM from GPS
			Cell cellUoMfromGPS  = rowHeaderMSPRCommonColumns.createCell(12);
			cellUoMfromGPS.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellUoMfromGPS.setCellValue(strUoMfromGPS);
			
			//Difference Max Amt GPS - Net Wt FC
			Cell cellDifferenceMaxAmtGPSNetWtFC  = rowHeaderMSPRCommonColumns.createCell(13);
			cellDifferenceMaxAmtGPSNetWtFC.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellDifferenceMaxAmtGPSNetWtFC.setCellValue(strDifferenceMaxAmtGPSNetWtFC);
			
			//Convert Difference to %
			Cell cellConvertDifferencetoPer  = rowHeaderMSPRCommonColumns.createCell(14);
			cellConvertDifferencetoPer.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellConvertDifferencetoPer.setCellValue(strConvertDifferencetoPer);
			
			//RM GPS NA Status
			Cell cellRMGPSNAStatus = rowHeaderMSPRCommonColumns.createCell(15);
			cellRMGPSNAStatus.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellRMGPSNAStatus.setCellValue(strRMGPSNAStatus);
			
			//RM GPS EU Status
			Cell cellRMGPSEUStatus = rowHeaderMSPRCommonColumns.createCell(16);
			cellRMGPSEUStatus.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellRMGPSEUStatus.setCellValue(strRMGPSEUStatus);
			
			//RM GPS AP Status
			Cell cellRMGPSAPStatus = rowHeaderMSPRCommonColumns.createCell(17);
			cellRMGPSAPStatus.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellRMGPSAPStatus.setCellValue(strRMGPSAPStatus);
			
			//RM GPS LA Status
			Cell cellRMGPSLAStatus = rowHeaderMSPRCommonColumns.createCell(18);
			cellRMGPSLAStatus.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellRMGPSLAStatus.setCellValue(strRMGPSLAStatus);
			
			//Comments from GPS
			Cell cellCommentsfromGPS  = rowHeaderMSPRCommonColumns.createCell(19);
			cellCommentsfromGPS.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellCommentsfromGPS.setCellValue(strCommentsfromGPS);
			
			//Commented for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Starts
			/*Material Function Matches based on mapping
			Cell cellMaterialFunctionMatchesbasedonmapping  = rowHeaderMSPRCommonColumns.createCell(20);
			cellMaterialFunctionMatchesbasedonmapping.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellMaterialFunctionMatchesbasedonmapping.setCellValue(strMaterialFunctionMatchesbasedonmapping);
			
			//Segment Matches based on mapping
			Cell cellSegmentMatchesbasedonmapping = rowHeaderMSPRCommonColumns.createCell(21);
			cellSegmentMatchesbasedonmapping.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellSegmentMatchesbasedonmapping.setCellValue(strSegmentMatchesbasedonmapping);
			*/
			//Commented for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Ends
			//Added for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Ends
			//GPS Number
			Cell cellGPSNumber  = rowHeaderMSPRCommonColumns.createCell(20);
			//Added for Req Id : 32090- Ends
			cellGPSNumber.setCellStyle(cellStyleMSPRReportCommonColumn);
			cellGPSNumber.setCellValue(strGPSNumber);
			
			int mlMSPRReportLisetSize  = mlMSPRReportLiset.size();
			if(mlMSPRReportLisetSize>0) {
				Map mpCommonColumns = (Map)mlMSPRReportLiset.get(mlMSPRReportLisetSize-1);
				Map mpCommonColumnsData = (Map) mpCommonColumns.get("CommonColumns");
				//Title 
				Cell cellTitleValue = rowHeaderMSPRReportTitle.createCell(1);
				cellTitleValue.setCellValue((String) mpCommonColumnsData.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));	
				cellTitleValue.setCellStyle(cellStyleMSPRReport);
				
				//Sector
				Cell cellSectorValue = rowHeaderMSPRReportSector.createCell(1);
				//Commented and updated for Feb22 CW Requirement 41852 Starts 
				//cellSectorValue.setCellValue(displaySectorAndFranchiseColumnValue(context,(String)mpCommonColumnsData.get(DomainConstants.SELECT_ID),DomainConstants.EMPTY_STRING,FIELDNAME_PGBUSINESSAREA));
				cellSectorValue.setCellValue((String)mpCommonColumnsData.get("BusinessArea"));	
				//Commented and updated for Feb22 CW Requirement 41852 Ends
				cellSectorValue.setCellStyle(cellStyleMSPRReport);
				
				//Franchise
				Cell cellFranchiseValue = rowHeaderMSPRReportFranchise.createCell(1);
				//Commented and updated for Feb22 CW Requirement 41852 Starts
				//cellFranchiseValue.setCellValue(displaySectorAndFranchiseColumnValue(context,(String)mpCommonColumnsData.get(DomainConstants.SELECT_ID),PRODUCT_CATEGORY_PLATFORM,FIELDNAME_PG_PRODUCTCATEGORYPLATFORM));
				cellFranchiseValue.setCellValue((String)mpCommonColumnsData.get("ProductCategoryPlatform"));
				//Commented and updated for Feb22 CW Requirement 41852 Ends
				cellFranchiseValue.setCellStyle(cellStyleMSPRReport);
				
				//Added for Req Id : 32090- Starts
				/*
				//Segment
				Cell cellSegmentValue = rowHeaderMSPRReportSegement.createCell(1);
				cellSegmentValue.setCellValue((String) mpCommonColumnsData.get(AWLUtil.strcat("attribute[", ATTR_SEGMENT, "]")));	
				cellSegmentValue.setCellStyle(cellStyleMSPRReport);
				*/
				//Added for Req Id : 32090- Ends
				
				//Selected Region
				Cell cellRegionValue = rowHeaderMSPRReportRegions.createCell(1);
				cellRegionValue.setCellValue((String) mpCommonColumnsData.get("RegionSelected"));	
				cellRegionValue.setCellStyle(cellStyleMSPRReport);
				
				//Originator
				Cell cellOriginatorValue = rowHeaderMSPRReportOriginator.createCell(1);
				cellOriginatorValue.setCellValue((String) mpCommonColumnsData.get(AWLUtil.strcat("attribute[", ATTR_ORIGINATOR, "]")));	
				cellOriginatorValue.setCellStyle(cellStyleMSPRReport);
				
				//Report Generation Time
				Cell cellReportGenTimeValue = rowHeaderMSPRReportGenerationTime.createCell(1);
				cellReportGenTimeValue.setCellValue(getMSPRLastExecutionDate(context));	
				cellReportGenTimeValue.setCellStyle(cellStyleMSPRReport);
				int colIndex =0;
				int rowCount1  =7;
				Cell cell  =null;
				int columnCount1  = 0;
				XSSFRow row  =null;
				// Added for Hyperlink Requirement : 33631 -Starts
				CreationHelper createHelper = workbook.getCreationHelper();
				String strValue = DomainConstants.EMPTY_STRING;
				String strParentId = DomainConstants.EMPTY_STRING;
				String strChildId = DomainConstants.EMPTY_STRING;
				String strHyperlinkId = DomainConstants.EMPTY_STRING;
				String strCellValue = DomainConstants.EMPTY_STRING;
				// Added for Hyperlink Requirement : 33631 -Starts
				String[] strSplittedValue;
				for(int iIndex=0;iIndex<mlMSPRReportLisetSize;iIndex++) {
					//Added code for 2018x.6 Requirement id Ability to generate report with-without hyperlink Starts
					iRowCountAll = iRowCountAll + 1;
					//Added code for 2018x.6 Requirement id Ability to generate report with-without hyperlink Etarts
					mp1 = (Map)mlMSPRReportLiset.get(iIndex);
					row = sheetMSPRReport.createRow(rowCount1++);
					// Added for Hyperlink Requirement : 33631 -Starts
					String strId = (String)mp1.get("id");
					hm.put(0, HYPERLINK+mp1.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+strId);//GCAS
					// Added for Hyperlink Requirement : 33631 -Ends
					hm.put(1, mp1.get(DomainConstants.SELECT_REVISION));//Revision
					hm.put(2, mp1.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));//Title
					columnCount1  = 0;
					//commented the code for Defect Id :29914 - Column Type column should be deleted in the MSPR Report done thru Mass Download -Starts
					/*if(mp1.containsKey(KEY_COLUMN_TYPE)) {
						hm.put(3, mp1.get(KEY_COLUMN_TYPE));//Column Type
					}else {
						hm.put(3, DomainConstants.EMPTY_STRING);
					}*/
					//commented the code for Defect Id :29914 - Column Type column should be deleted in the MSPR Report done thru Mass Download -Ends
					if(mp1.containsKey(KEY_SUP_MTRL_TRADE_NAME)) {
						hm.put(3, mp1.get(KEY_SUP_MTRL_TRADE_NAME));//Trade Name
					}else {
						hm.put(3, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_READINESS_COMMENT)) {
						hm.put(4, mp1.get(KEY_READINESS_COMMENT));//Readiness Comments
					}else {
						hm.put(4, DomainConstants.EMPTY_STRING);
					}
					//Added for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Starts
					if(mp1.containsKey(KEY_READINESS_REASON)) {
						hm.put(5, mp1.get(KEY_READINESS_REASON));//Readiness Comments
					}else {
						hm.put(5, DomainConstants.EMPTY_STRING);
					}
					//Added for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Ends
					//Updated for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Starts
					if(mp1.containsKey(KEY_MATERIAL_FUNCTION)) {
						hm.put(6, mp1.get(KEY_MATERIAL_FUNCTION));//Material Function
					}else {
						hm.put(6, DomainConstants.EMPTY_STRING);
					}
					
					//Added for Req Id : 32090- Starts
					if(mp1.containsKey(KEY_CLASS)) {
						hm.put(7, mp1.get(KEY_CLASS));//Class
					}else {
						hm.put(7, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_SUB_CLASS)) {
						hm.put(8, mp1.get(KEY_SUB_CLASS));//Subclass
					}else {
						hm.put(8, DomainConstants.EMPTY_STRING);
					}
					
					
					if(mp1.containsKey("attribute["+ATTR_PG_NETWEIGHT+"]")) {
						hm.put(9, mp1.get("attribute["+ATTR_PG_NETWEIGHT+"]"));//Net Weight(FC Target)
					}else {
						hm.put(9, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey("attribute["+ATTR_PG_NETWEIGHT_UOM+"]")) {
						hm.put(10, mp1.get("attribute["+ATTR_PG_NETWEIGHT_UOM+"]"));//UoM from FC
					}else {
						hm.put(10, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_MAX_CLEARED_AMOUNT)) {
						hm.put(11, mp1.get(KEY_MAX_CLEARED_AMOUNT));//Max Amount from GPS
					}else {
						hm.put(11, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_MAX_CLEARED_AMOUNT_UNIT)) {
						hm.put(12, mp1.get(KEY_MAX_CLEARED_AMOUNT_UNIT));//UoM from GPS
					}else {
						hm.put(12, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_DIFFERENCE)) {
						hm.put(13, mp1.get(KEY_DIFFERENCE));//Difference Max Amt GPS - Net Wt FC
					}else {
						hm.put(13, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_DIFFERENCE_PERCENTAGE)) {
						hm.put(14, mp1.get(KEY_DIFFERENCE_PERCENTAGE));//Convert Difference to %
					}else {
						hm.put(14, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_RM_GPS_NA)) {
						hm.put(15, mp1.get(KEY_RM_GPS_NA));//RM GPS NA Status
					}else {
						hm.put(15, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_RM_GPS_EU)) {
						hm.put(16, mp1.get(KEY_RM_GPS_EU));//RM GPS EU Status
					}else {
						hm.put(16, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_RM_GPS_AP)) {
						hm.put(17, mp1.get(KEY_RM_GPS_AP));//RM GPS AP Status
					}else {
						hm.put(17, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_RM_GPS_LA)) {
						hm.put(18, mp1.get(KEY_RM_GPS_LA));//RM GPS LA Status
					}else {
						hm.put(18, DomainConstants.EMPTY_STRING);
					}
					
					if(mp1.containsKey(KEY_COMMENTS)) {
						hm.put(19, mp1.get(KEY_COMMENTS));//Comments from GPS
					}else {
						hm.put(19, DomainConstants.EMPTY_STRING);
					}
					//Commented for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Starts
					/*if(mp1.containsKey(KEY_FUNCTION_MATCH)) {
						hm.put(20, mp1.get(KEY_FUNCTION_MATCH));//Material Function Matches based on mapping
					}else {
						hm.put(20, DomainConstants.EMPTY_STRING);
					}
					
					
					if(mp1.containsKey(KEY_SEGMENT_MATCH)) {
						hm.put(21, mp1.get(KEY_SEGMENT_MATCH));//Segment Matches based on mapping
					}else {
						hm.put(21, DomainConstants.EMPTY_STRING);
					}*/
					//Commented for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Ends
					if(mp1.containsKey(KEY_REQUEST_KEY)) {
						hm.put(20, mp1.get(KEY_REQUEST_KEY));//GPS Number
					}else {
						hm.put(20, DomainConstants.EMPTY_STRING);
					}
					//Updated for 2018x6.1 Sept requirement 40260 updated to reflect changes made by Apollo team Ends
					//Added for Req Id : 32090- Ends
									
					for(int j=0;j<hm.size();j++){								
						cell = row.createCell(columnCount1++);
						// Added for Hyperlink Requirement : 33631 -Starts
						strCellValue = (String)hm.get(j);
						if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
							strSplittedValue = strCellValue.split("\\|",-1); 
							strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
							strValue = strSplittedValue[(strSplittedValue.length)-2];
							//Added code for 2018x.6 Requirement id 36711 Ability to generate Part Library report with-without hyperlink Starts
							if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
							//Added code for 2018x.6 Requirement id Ability to generate report with-without hyperlink Ends
								getHyperlink(context,cell,workbook,strValue,strHyperlinkId);
								
							//Added code for 2018x.6 Requirement id Ability to generate report with-without hyperlink Starts
							} else if(UIUtil.isNotNullAndNotEmpty(strValue)){
								cell.setCellStyle(cellStyleMSPRReport);
								cell.setCellValue(strValue);
							}
							//Added code for 2018x.6 Requirement id Ability to generate report with-without hyperlink Ends
						} else {
							cell.setCellValue(strCellValue);
							cell.setCellStyle(cellStyleMSPRReport);
						}
						// Added for Hyperlink Requirement : 33631 -Starts
					}
				}
			 } 	
			
		    }catch (Exception e) {
			outLog.print("Exception in  updateWorksheetMSPRReport "+e+"\n");
			outLog.flush();	
		}
	}
	/**
     * this method is used to check the access
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     */
	 private boolean accessCheck(Context context, String strUserName, String strPartId) throws Exception {
			boolean bAccess = false; 
			String strValue = DomainConstants.EMPTY_STRING;
			try{
				DomainObject domainObject = DomainObject.newInstance(context);
				domainObject.setId(strPartId);
				ContextUtil.pushContext(context, strUserName, null,context.getVault().getName());
				strValue = (String)domainObject.getInfo(context, "current");
				ContextUtil.popContext(context);			
				if(!"#DENIED!".equals(strValue) && !strValue.equals(DomainConstants.EMPTY_STRING))
				{				
					bAccess  = true;					
				}
			}
			catch(Exception e){
				outLog.print("Exception in accessCheck: "+e+"\n");
				outLog.flush();
			}
			return bAccess;
	  }
	 
	 /**
	     * this method used to show Last Execution date in MSPR report
	     * @param context is the matrix context
	     * @param args has the required information
	     * @return String
	     */
		public String getMSPRLastExecutionDate(Context context) throws Exception
		{
			SimpleDateFormat formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
			Calendar currentDate  = Calendar.getInstance();	
			String dateNow = formatter.format(currentDate.getTime());
			return dateNow;
		}
		
		 /**
		* This method display all the Platform and Chassis values for create document by VT for Requirement 2899, 2861
		* @param context the eMatrix Context object
		* @param args holds the Document object id
		* @returns String
		* @throws Exception if the operation fails
		*/
		public String displaySectorAndFranchiseColumnValue(Context context,String strObjectId,String strRelAttribute,String strFieldName) throws Exception{   
			StringBuffer sbHtmloutput =new StringBuffer();
			StringBuffer sbReturnValue  =new StringBuffer();
			String sReturnValue  =DomainConstants.EMPTY_STRING;
			String sListTemp =DomainConstants.EMPTY_STRING;
			Pattern sRelPattern  = new Pattern(RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM);
			sRelPattern.addPattern(RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS);
			sRelPattern.addPattern(RELATIONSHIP_PG_PROJECT_TO_PLATFORM);
			sRelPattern.addPattern(RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA);
			sRelPattern.addPattern(RELATIONSHIP_PGPROJECT_TO_BUSINESSAREA);
			Pattern strPickListPattern = new Pattern(TYPE_PG_PLIPLATFORM);
			strPickListPattern.addPattern(TYPE_PG_PLICHASSIS);
			strPickListPattern.addPattern(TYPE_PG_PLI_BUSINESSAREA);
			StringList sRangevalueList =new StringList();
			int sRangevalueListSize =0;
			StringList relSelects =new StringList(3);
			relSelects.addElement(AWLUtil.strcat("attribute[", ATTRIBUTE_PG_PLATFORM_TYPE, "]"));
			relSelects.addElement(AWLUtil.strcat("attribute[", ATTRIBUTE_PG_CHASSIS_TYPE, "]"));
			
			
			StringList objectSelects =new StringList(5);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(AWLUtil.strcat("attribute[", ATTRIBUTE_PG_PLATFORM_TYPE, "]"));
			objectSelects.add(AWLUtil.strcat("attribute[", ATTRIBUTE_PG_CHASSIS_TYPE, "]"));
			objectSelects.add(DomainConstants.SELECT_TYPE);
			Map mdconnectedObjectsMap =null;
			String connectedObjects =DomainConstants.EMPTY_STRING;
			String connectedObjectsID =DomainConstants.EMPTY_STRING;
			String connectedObjectsType =DomainConstants.EMPTY_STRING;
			String relSelectPlatformAttribute =DomainConstants.EMPTY_STRING;
			String relSelectChassisAttribute  =DomainConstants.EMPTY_STRING;
			Map<String, String> connectedObjectIds = new LinkedHashMap<String, String>();
			MapList  mapConnectedlist =new MapList();
			int mapConnectedlistSize=0;
			try{
				DomainObject docObject  =DomainObject.newInstance(context,strObjectId);
				mapConnectedlist  =docObject.getRelatedObjects(context, sRelPattern.getPattern(), strPickListPattern.getPattern(), objectSelects, relSelects, false, true, (short)1,DomainConstants.EMPTY_STRING,null,0);
				if(!mapConnectedlist.isEmpty()){
				mapConnectedlist.sort(DomainConstants.SELECT_NAME, ORDER_ASCENDING, STRING);
				mapConnectedlistSize  =mapConnectedlist.size();
				for(int mapConnectedlistValues=0;mapConnectedlistValues<mapConnectedlistSize;mapConnectedlistValues++)
				{
				mdconnectedObjectsMap = (Map)mapConnectedlist.get(mapConnectedlistValues);
				connectedObjects  =(String)mdconnectedObjectsMap.get(DomainObject.SELECT_NAME);
				connectedObjectsID  =(String)mdconnectedObjectsMap.get(DomainObject.SELECT_ID);
				connectedObjectsType  =(String)mdconnectedObjectsMap.get(DomainObject.SELECT_TYPE);
				relSelectPlatformAttribute =(String)mdconnectedObjectsMap.get("attribute["+ATTRIBUTE_PG_PLATFORM_TYPE+"]");
				relSelectChassisAttribute  =(String)mdconnectedObjectsMap.get("attribute["+ATTRIBUTE_PG_CHASSIS_TYPE+"]");
				if(relSelectPlatformAttribute.equalsIgnoreCase(strRelAttribute)){
				    sRangevalueList.add(connectedObjects);		
				}
				else if(relSelectChassisAttribute.equalsIgnoreCase(strRelAttribute)){
					sRangevalueList.add(connectedObjects);	
				}
				else if(TYPE_PG_PLI_BUSINESSAREA.equalsIgnoreCase(connectedObjectsType) && PG_BUSINESS_AREA.equalsIgnoreCase(strFieldName))
				{
					sRangevalueList.add(connectedObjects);	
				}
				connectedObjectIds.put(connectedObjects,connectedObjectsID);
				}			
				}
				if(!sRangevalueList.isEmpty()){
				sRangevalueListSize =sRangevalueList.size();
				for(int sRangevalueListValues=0;sRangevalueListValues<sRangevalueListSize;sRangevalueListValues++){
				sListTemp = (String)sRangevalueList.get(sRangevalueListValues);
				if(sReturnValue.length()>0){
				sbReturnValue.append(sReturnValue).append(SYMBOL_UNDERSCORE).append(sListTemp).toString();
				}
				else {
				sReturnValue = sListTemp;
				}
				}
				}
				sbHtmloutput.append(sReturnValue);
			}catch(Exception ex){
				ex.printStackTrace();
				throw ex;		
			}
			return sbHtmloutput.toString();
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
				 java.io.File fileDelete = null;
				 if(folderDelete.list().length==0){
		 		   folderDelete.delete();
		 		   
				 }else{ 
		 		   //list all the directory contents
		     	   String files[] = folderDelete.list();
		
		     	   for (String temp : files) {
		     	      //construct the file structure
		     	      fileDelete = new java.io.File(folderDelete, temp);
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
		 
		// Added for Hyperlink Requirement : 33631 -Starts
		/*
		* This method is used to get the Hyperlink for Name columns in report
		* @return void
		*/
		 public void getHyperlink(Context context, Cell cell, XSSFWorkbook workbook, String strValue, String strId) throws Exception{
			 String strURL = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(),"emxCPN.BaseURL");
			 String strNewURL = String.valueOf(strURL).trim();
			 CreationHelper createHelper = workbook.getCreationHelper();
			 XSSFCellStyle style = workbook.createCellStyle();
			 XSSFFont hlinkfont = workbook.createFont();
			 hlinkfont.setUnderline(XSSFFont.U_SINGLE);
			 hlinkfont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
			 style.setFont(hlinkfont);
			 XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(HyperlinkType.URL);
			 if(UIUtil.isNotNullAndNotEmpty(strId) && UIUtil.isNotNullAndNotEmpty(strValue)){
				 link.setAddress(strNewURL+"?objectId="+strId);
				 cell.setCellValue(strValue);
				 cell.setHyperlink((XSSFHyperlink) link);
				 cell.setCellStyle(style);
			 } else {
				 cell.setCellValue(DomainConstants.EMPTY_STRING);
			 }
		}
		 // Added for Hyperlink Requirement : 33631 -Ends
		 
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
	 
	 /**Added for Feb22_CW requirement 46245 
		 * @param context
		 * @param strPartObjectId
		 * @return
		 * @throws FrameworkException
		 */
		private Map<String,Object> getBusinessAreaPCP(Context context, String strPartObjectId) throws FrameworkException {
			StringList slSelect = new StringList();
			slSelect.add(DomainConstants.SELECT_NAME);
			slSelect.add(DomainConstants.SELECT_TYPE);
			Pattern relPattern = new Pattern(pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA);
			relPattern.addPattern(pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM);
			DomainObject dobj = DomainObject.newInstance(context, strPartObjectId);
			MapList mlPCPBusinessArea = dobj.getRelatedObjects(context, 
					relPattern.getPattern(), //relationship pattern
					DomainConstants.QUERY_WILDCARD, // object pattern
					slSelect, // object selects
					null, // relationship selects
					false,   // to direction
					true,  // from direction
					(short)1,  // recursion level
					null,// object where clause
					null,//relationshipWhere
					0);//limit
			Map<String,Object> mpFinal = new HashMap<>();
			String strPCP = "";
			String strBusinessArea = "";
			//Added for defect id 46245 starts
			String strPartType = "";
			//Added for defect id 46245 ends
			Map<String,Object> mpInfo = new HashMap<>();
			StringList slPCP = new StringList();
			StringList slBusinessArea = new StringList();
			if(!mlPCPBusinessArea.isEmpty()) {
				for(int i=0;i<mlPCPBusinessArea.size();i++) {
					mpInfo = (Map<String, Object>) mlPCPBusinessArea.get(i);
					strPartType = (String) mpInfo.get(DomainConstants.SELECT_TYPE);
					if(UIUtil.isNotNullAndNotEmpty(strPartType) && TYPEPGPLIPRODUCTCATEGORYPLATFORM.equalsIgnoreCase(strPartType))	{
						strPCP = (String) mpInfo.get(DomainConstants.SELECT_NAME);
						if(UIUtil.isNotNullAndNotEmpty(strPCP) && !slPCP.toString().contains(strPCP)) {
							slPCP.add(strPCP);
						}
					}
					if(UIUtil.isNotNullAndNotEmpty(strPartType) && TYPE_PG_PLI_BUSINESSAREA.equalsIgnoreCase(strPartType))	{
						strBusinessArea = (String) mpInfo.get(DomainConstants.SELECT_NAME);
						if(UIUtil.isNotNullAndNotEmpty(strBusinessArea) && !slBusinessArea.toString().contains(strBusinessArea)) {
							slBusinessArea.add(strBusinessArea);
						}
					}
				}
			}
			mpFinal.put("PCP",slPCP);
			mpFinal.put("BusinessArea",slBusinessArea);
			return mpFinal;
		}
		
		/**Added for Feb22_CW requirement 46245
		 * @param mpStatusPCP
		 * @return
		 */
		private Map<String, String> getFinalValue(Map<String, Object> mpStatusPCP) {
			StringList slPCP = (StringList) mpStatusPCP.get("PCP");
			StringList slBusinessArea = (StringList) mpStatusPCP.get("BusinessArea");
			String strPCP = "";
			String strBussinessArea = "";
			Map<String,String> mpValue = new HashMap<>(); 
			if(!slPCP.isEmpty()) {
				strPCP = slPCP.get(0);
			}
			if(!slBusinessArea.isEmpty()) {
				strBussinessArea = slBusinessArea.get(0);
			}
			mpValue.put("PCP",strPCP);
			mpValue.put("BusinessArea",strBussinessArea);
			return mpValue;
		}
		
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
					strUrl = EnoviaResourceBundle.getProperty(context,CPNRESOURCEFILE, context.getLocale(), "emxCPN.BaseURL");
					strUrl = strUrl.substring(0, strUrl.indexOf(PATH_SEPARATOR+STR_COMMON));
					strUrl = String.valueOf(strUrl).trim();
					strUrlPath = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.Enovia.DSMReportLink");
					sbUrl.append(strUrl);
					sbUrl.append(strUrlPath);
				
			}catch(Exception e) {
				outLog.println("Exception in getUrl method"+e+"\n");
				outLog.flush();
				
			}
			
			return sbUrl.toString();
			}
			
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends


}	



