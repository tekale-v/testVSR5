/*  pgATSReports.java  Report Request For ATS Reports Download written for requirement 34294
     Author:DSM Report Team
     Copyright (c) 2019
     All Rights Reserved.
 */
package com.pg.dsm.custom;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
//added for REq Id:33634-starts
import org.apache.poi.ss.usermodel.Font;
//added for REq Id:33634-Ends
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
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
import java.util.Properties;
//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends

public class pgATSReports implements DomainConstants {
	
	
	public pgATSReports(Context context, String[] args) {
		
}
	private StringList SL_OBJECT_COMMONINFO_SELECT = getObjectCommonSelects();
    private static final String HYPERLINK = "Hyperlink:|";
	private static final String HYPERLINK_PIPE = "|";
	private static final String HYPERLINK_COMPARE = "Hyperlink:";
	private static final String EMXCPN = "emxCPN";
	private static final String USERNAME="UserName";
	private static final String REPORTFILENAME="ReportFileName";
	private static final String REPORTOBJECTID="ReportObjectId";
	private static final String LATESTRELEASEPART="LatestVersionAsInput";
	private static final String STRFINALCOMMONCOLUMNS = "CommonColumns";
	private static final String CPNSTRINGRESOURCE = "emxCPNStringResource";
	private static final String ATTR_IMPACTEDTYPE="attribute[pgImpactedType]";
	private static final String DESCENDING = "descending";
	private static final String STRING = "string";
	private static final String TYPE = "Type";
	private static final String EBP = "EBP";
	private static final String STR_PARTNERS_PG="Partners_PG";
	private static final String STR_MULTI_OWNERSHIP_FOR_OBJ="Multiple Ownership For Object";
	private static final String DENIED = "#DENIED!";
	private static final String COMMA = ",";
	private static final String NAME_SELECT = "].to.name";
	private static final String FROM = "from[";
	private static final String ERR_MESSAGE="Creation of Object Failed";
	private static final String CPNRESOURCEFILE ="emxCPN";
	private static final String DATE_YMDHS = "yyyy-MM-dd HH:mm:ss";
	private static final String DIRECTORY_ERR = "Could not create directory";
	private static final String CATALINA_BASE="catalina.base";
	private static final String PATH_SEPARATOR="/";
	private PrintWriter outLog = null;
	//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
	private static final String HYPERLINKASINPUT = "HyperlinkAsInput";
	//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
	//Added code for 2018x.6 Requirement id 36704 Generate ATS report directly Starts
	private static final String REALTIMEPROCESS = "RealTimeProcess";
	//Added code for 2018x.6 Requirement id 36704 Generate ATS report directly Ends
	//Added for Apr22 CW requirement 41502 Starts
	private String strOriginatingSource = "";
	private static final String TYPE_GLOBALSUBSCRIPTIONCOFIGURATION = "Global Subscription Configuration";
	//Added for Apr22 CW requirement 41502 Ends
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	private static final String STRFROMEMAILID = "strFromEmailId";
	private static final String STRTOEMAILID = "strToEmailId";
	private static final String STRSUBJECT = "strSubject";
	private static final String STRMESSAGEBODY = "strMessageBody";
	private static final String STR_PERSON_PLM_ADMIN = "PLM Admin";
	private static final String STR_ENOVIA = "Enovia";
	private static final String STR_SPECREADER ="SpecReader";
	private static final String STR_COMMON="common";
	private static final String STR_SYMBOL_BRACKETS="{0}";
	private static final String STR_TEXT_HTML="text/html";
	private static final String STR_VAULT_ADMINISTRATION=PropertyUtil.getSchemaProperty(null,"vault_eServiceAdministration");
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	
	/**
	 * 
     * this method is to generate the Exel Sheet for the manual entry from the user side
     * 
     * @param args
     * @return void
     * @throws Exception
     */  
	public void generateATSReport(Context context, String[] args) throws Exception {  
		StringBuilder sbLogFolder = new StringBuilder();
		File fLogFolder = null;
		String strDirectoryNotCreated = DIRECTORY_ERR;
		String strStartTime = null; 
		String sLanguage = context.getSession().getLanguage();
		String strJVM = getJVMInstance();
	 	HashMap<String, String> hmArgs = (HashMap) JPO.unpackArgs(args);
	    String strUserName =hmArgs.get(USERNAME);
	    String strPartNames = hmArgs.get("GCAS");	    
	    String strReportFileName =hmArgs.get(REPORTFILENAME);
	    String strReportObjectId =hmArgs.get(REPORTOBJECTID);	  
	    String strLatestVersionAsInput =hmArgs.get(LATESTRELEASEPART);
	    //Added code for 2018x.6 Requirement id 36703,36704 Ability to generate ATS report with-without hyperlink, Generate ATS report directly Starts Starts
		String strHyperlink = hmArgs.get(HYPERLINKASINPUT);
		String strRealTimeProcess = hmArgs.get(REALTIMEPROCESS);
		//Added for Apr22 CW requirement 41502 Starts
		strOriginatingSource = hmArgs.get("OriginatingSource");
		if(UIUtil.isNullOrEmpty(strOriginatingSource)) {
			strHyperlink = "false";
		}
		//Added for Apr22 CW requirement 41502 Ends
		Map<String,Object> mPassValue = new HashMap<>();
	    mPassValue.put(REALTIMEPROCESS,strRealTimeProcess);
	    mPassValue.put(HYPERLINKASINPUT,strHyperlink);
		String configLOGFilePath = DomainConstants.EMPTY_STRING;
	    if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
	    	configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Log.FilePath");
	    else
	    	configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReportCTRLMJob.Log.FilePath");
	    //Added code for 2018x.6 Requirement id 36704 Generate ATS report directly Ends
	    sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
	    fLogFolder = new File(sbLogFolder.toString());
	    if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
	    	throw new IOException(strDirectoryNotCreated + fLogFolder);
	    }
	    outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"ATSReportLog.log",true));
	    strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());	    
	    
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames))){
	    	DomainObject doObj  =DomainObject.newInstance(context, strReportObjectId);
	 	    outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+" | JVM: "+strJVM+"--------\n");
	 	    outLog.print("Parts: "+strPartNames+"\n");
	 	    outLog.print("Report Object Id: "+strReportObjectId+"\n");
	 	    outLog.flush();
	    	doObj.promote(context);
	    	//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
	    	getATSReportProcess(context,strUserName,strPartNames,strReportFileName,strReportObjectId,strLatestVersionAsInput,mPassValue);
	    	//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
	    } else {
	    	outLog.print("-------Report requested by: " +strUserName+" : "+strStartTime+"--------\n");
			outLog.print("Report cannot be generated. Check Report Object Id for request details: "+strReportObjectId+"\n");
			outLog.flush(); 
	    }
	}

	/** this method fetches all the valid Parts from input and processes them.
	 * @param context
	 * @param strUserName
	 * @param strPartNames
	 * @param strReportFileName
	 * @param strReportObjectId
	 * @param strLatestVersionAsInput
	 */
	private void getATSReportProcess(Context context,String strUserName,String strPartNames,String strReportFileName, String strReportObjectId,String strLatestVersionAsInput,Map<String,Object> mPassValue) 
	{
			MapList mlATSDetails = new MapList();
			MapList mlATSDetailsAll=new  MapList();
			MapList mlPart = new MapList();	
			StringList slIndividualPartNames = StringUtil.split(strPartNames, COMMA);
			Map<String, Object> mp = new HashMap<>();
			Map<String,Object> mPartColumn= new HashMap<>();
			Map<String,Object> mpTemp=new HashMap<>();
			String	strImpactedType=DomainConstants.EMPTY_STRING;
			String strPartObjectId=DomainConstants.EMPTY_STRING;
			String strPartObjectName=DomainConstants.EMPTY_STRING;
			int iPartMLSize = 0;
			int iATSDetailsSize = 0;
			boolean bInputPartAccess = false; 
			try {
				mlPart=getPartList(context,slIndividualPartNames,strLatestVersionAsInput);
				if(null !=mlPart && !mlPart.isEmpty()){
					iPartMLSize=mlPart.size();
						for(int i=0;i<iPartMLSize;i++){
						mp =  (Map)mlPart.get(i);
						strPartObjectId= (String) mp.get(DomainConstants.SELECT_ID);
						bInputPartAccess=accessCheck(context, strUserName, strPartObjectId);
						if(bInputPartAccess)
						{
							strPartObjectName=(String) mp.get(DomainConstants.SELECT_NAME);
							outLog.print("Expand start for: "+strUserName+": "+strPartObjectId+"|" +strPartObjectName+ "\n");
							outLog.flush();			
						//Req Id: 34297  Report shall have the following fields displayed for the corresponding ATS(s) that were inputted into the Report:Name,Revision,Maturity State,Impacted Type,Release Date,Expiration Date,Title,Primary Organization-Starts
						DomainObject dobjPart = DomainObject.newInstance(context, strPartObjectId);						
						mPartColumn=getCommonColumnsDetail(context,dobjPart);
						//Req Id: 34298  The ATS Related Parts Report once inputted with ATS(s), shall return a list of all related Parts that the various ATS(s) inputted impact-starts
						strImpactedType = UINavigatorUtil.getAdminI18NString(TYPE,(String)mPartColumn.get(ATTR_IMPACTEDTYPE),context.getSession().getLanguage());
						mPartColumn.put(ATTR_IMPACTEDTYPE,strImpactedType);
						//Req Id: 34298  The ATS Related Parts Report once inputted with ATS(s), shall return a list of all related Parts that the various ATS(s) inputted impact-ends
						// Req Id: 34297  Report shall have the following fields displayed for the corresponding ATS(s) that were inputted into the Report:Name,Revision,Maturity State,Impacted Type,Release Date,Expiration Date,Title,Primary Organization-Ends
						
						//Req Id: 34294 The system shall have an ATS Related Parts Report-Starts
						mlATSDetails=getRelatedAtsDetails(context,dobjPart,mPartColumn,strUserName);

						if(null !=mlATSDetails && !mlATSDetails.isEmpty()){
							iATSDetailsSize=mlATSDetails.size();
							for(int iIndex = 0; iIndex <iATSDetailsSize; iIndex++){
								mpTemp= (Map) mlATSDetails.get(iIndex);
								mlATSDetailsAll.add(mpTemp);
							}
							mlATSDetails.clear();
						}
					
						//Req Id: 34294 The system shall have an ATS Related Parts Report-Ends
						outLog.print("Expand end for: "+strUserName+": "+strPartObjectId+"|" +strPartObjectName+ "\n");
						outLog.flush();
					}
					else
					{
						outLog.print("Could not Expand for: "+strUserName+": "+strPartObjectId+"|" +pgV3Constants.NO_ACCESS+ "\n");
						outLog.flush();	
					}
				}
			}
			//Updated code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
			createExcelWorkbook(context,mlATSDetailsAll,strReportFileName,strReportObjectId,strUserName,mPassValue);
			//Updated code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
			mlATSDetailsAll.clear();
			String strEndTime = null; 
			strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
			outLog.print("Report completed for: "+strUserName+": "+strPartNames+"|" +strReportFileName+ "-------\n");
			outLog.print("-------Time completed: "+strEndTime+"-------\n");	
			outLog.print("-------\n");
			outLog.flush();
		} catch (Exception e) {
			outLog.print("Exception in  getATSReportProcess: "+strUserName+": "+e+"\n");
			outLog.flush();
		} finally {
			outLog.close();
		}
	}
	
	/**
	 * this method is to get valid part from input 
	 * @param context
	 * @param slIndividualPartNames
	 * @param strLatestVersionAsInput
	 * @return MapList
	 * @throws FrameworkException
	 */
	private MapList getPartList(Context context,StringList slIndividualPartNames,String strLatestVersionAsInput) throws FrameworkException {	
		String strPartName = null;
		MapList mlPart = new MapList();	
		MapList mlValidPart = new MapList();
		MapList mlreturnPart = new MapList();
		MapList mlValidPartDetails=new MapList();
		Map<String, Object> mPartDetails=new HashMap<>();
		String strWhereClauseTrue=DomainConstants.EMPTY_STRING;
		String strWhereClauseFalse=DomainConstants.EMPTY_STRING;
		int mlValidPartSize=0;
		int mlValidPartDetailsSize=0;
		
		try {
			
			for (Object inputPartName : slIndividualPartNames) {				
				strPartName = String.valueOf(inputPartName).trim();	
				strWhereClauseTrue = "current=="+pgV3Constants.STATE_RELEASE+"";
				 strWhereClauseFalse = "revision=="+DomainConstants.SELECT_LATEST_REVISION+"";
				if(UIUtil.isNotNullAndNotEmpty(strPartName)){
					if(UIUtil.isNotNullAndNotEmpty(strLatestVersionAsInput) && pgV3Constants.TRUE.equals(strLatestVersionAsInput)) {
						mlPart=getHighestRevisionPart(context,strPartName,strWhereClauseTrue);
						mlValidPart.add(mlPart);
					}
					else
					{
						mlPart=getHighestRevisionPart(context,strPartName,strWhereClauseFalse);
						mlValidPart.add(mlPart);
					}
				}
			}
			mlValidPartSize=mlValidPart.size();
			for(int i=0;i<mlValidPartSize;i++){
				mlValidPartDetails = (MapList) mlValidPart.get(i);
				mlValidPartDetailsSize=mlValidPartDetails.size();
				for(int j=0;j<mlValidPartDetailsSize;j++){
					mPartDetails = (Map)mlValidPartDetails.get(j);
					mlreturnPart.add(mPartDetails);
				}	
			}
		} catch(Exception e) {
			outLog.print("Exception in  getPartList : "+e+"\n");
			outLog.flush();
		}
		return mlreturnPart;
	}
	
	/**this method is to get highest revision input part 
	 * @param context
	 * @param strPartName
	 * @param strWhereExp
	 * @return MapList
	 * @throws FrameworkException
	 */
	private MapList getHighestRevisionPart(Context context,String strPartName,String strWhereExp)throws FrameworkException
	{
		StringList slSelect = new StringList(7);			
		slSelect.add(DomainConstants.SELECT_ID);	
		slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);	
		slSelect.add(DomainConstants.SELECT_CURRENT);
		slSelect.add(DomainConstants.SELECT_NAME);	
		slSelect.add(DomainConstants.SELECT_TYPE);
		slSelect.add(DomainConstants.SELECT_REVISION);
		slSelect.add(DomainConstants.SELECT_LAST_ID); 
		MapList mlPart = new MapList();
		Map<String, String> mReleaseHighestRevision=new HashMap<>();
		int mlPartObjSize=0;
		int mlPartObjSortSize=0;
		
		try {
			MapList	mlPartObj =DomainObject.findObjects(context,//context
					pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD,//type
					strPartName,//name
					DomainConstants.QUERY_WILDCARD,//revision
					DomainConstants.QUERY_WILDCARD,//owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
					strWhereExp,//where clause
					false,//expand type
					slSelect);//object select 
			
			if(!mlPartObj.isEmpty())
			{
				mlPartObjSize=mlPartObj.size();
				if(mlPartObjSize>1)
				{
					//Fetching Highest Release revision --Begin	
					mlPartObj.sort(DomainConstants.SELECT_REVISION, DESCENDING, STRING);
					mlPartObjSortSize=mlPartObj.size();
					for (int iRelRevNum = 0; iRelRevNum <mlPartObjSortSize; iRelRevNum++) {
						mReleaseHighestRevision= (Map)mlPartObj.get(iRelRevNum);								
						mlPart.add(mReleaseHighestRevision);
						break;
					}
				}
				else
				{
					mReleaseHighestRevision= (Map)mlPartObj.get(0);
					mlPart.add(mReleaseHighestRevision);
				}
			}
		
		//Fetching Highest Release revision --End			
		}catch(Exception e) {
			outLog.print("Exception in  getHighestRevisionPart: "+e+"\n");
			outLog.flush();	
		}
		return mlPart;
	}
	
	/*
	 * getObjectCommonSelects- Stringlist for Common Columns 
	 * @return StringList
	 */
	private StringList getObjectCommonSelects(){
		
		StringList slObjInfoSelect = new StringList(12);
		try{                 
			slObjInfoSelect.add(DomainConstants.SELECT_ID);			
		    slObjInfoSelect.add(DomainConstants.SELECT_NAME);
			slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);                 
			slObjInfoSelect.add(DomainConstants.SELECT_TYPE);
			slObjInfoSelect.add(DomainConstants.SELECT_REVISION);
			slObjInfoSelect.add(DomainConstants.SELECT_DESCRIPTION);
			slObjInfoSelect.add(DomainConstants.SELECT_CURRENT);
			slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
			slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
			slObjInfoSelect.add(DomainConstants.SELECT_POLICY);
			slObjInfoSelect.add(FROM.concat(pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION).concat(NAME_SELECT));	
			slObjInfoSelect.add(ATTR_IMPACTEDTYPE);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return slObjInfoSelect;
	}
	
		
   /** this method is to create Excel Sheet
 * @param context
 * @param mlATSDetailsAll
 * @param strReportFileName
 * @param strReportObjectId
 * @param strUserName
 * @throws IOException
 */
private void createExcelWorkbook(Context context,MapList mlATSDetailsAll,String strReportFileName, String strReportObjectId,String strUserName,Map<String,Object> mPassValue) throws IOException {
	   FileOutputStream outputStream=null;
	   File sbATSFolder = null;
	   try {
		   	//Added code for 2018x.6 Requirement 36704 Generate ATS report directly Starts
		   	String strRealTimeProcess = (String)mPassValue.get(REALTIMEPROCESS);
		   	String strReportPath = DomainConstants.EMPTY_STRING;
			String strReportName = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ATSReport.ReportName");
			if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
				strReportPath = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.ATSReport.Worksheet.FilePath");
			else
				strReportPath = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.ATSReportCTRLMJob.Worksheet.FilePath");
		   	//Added code for 2018x.6 Requirement 36704 Generate ATS report directly Ends
			String strReportExtension = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ATSReport.ReportExtension");
			String strRelatedATS = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ATSReport.Worksheet.Name.AtsReport");
			StringBuffer sbReportName = new StringBuffer();	
			XSSFWorkbook workbook = new XSSFWorkbook();	
		
			XSSFSheet sheetTablesRelatedATS = workbook.createSheet(strRelatedATS);	
			XSSFRow rowHeaderTablesATS = sheetTablesRelatedATS.createRow(0);
			XSSFCellStyle cellStyleRelatedATS = sheetTablesRelatedATS.getWorkbook().createCellStyle();		
			XSSFFont fontRelatedATS = sheetTablesRelatedATS.getWorkbook().createFont();
			XSSFCellStyle cellStyle = sheetTablesRelatedATS.getWorkbook().createCellStyle();
			XSSFFont xsfont = sheetTablesRelatedATS.getWorkbook().createFont();
			xsfont.setBold(true);
			xsfont.setFontHeightInPoints((short) 12);
			cellStyle.setFont(fontRelatedATS);
			cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());			
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			fontRelatedATS.setBold(true);
			fontRelatedATS.setFontHeightInPoints((short) 12);
			cellStyleRelatedATS.setFont(fontRelatedATS);
			cellStyleRelatedATS.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());			
			cellStyleRelatedATS.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			//Updated code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
			mPassValue.put("CellStyle", cellStyle);
			updateWorksheetRelatedATS(context,workbook,rowHeaderTablesATS,mlATSDetailsAll,sheetTablesRelatedATS,cellStyleRelatedATS,mPassValue);
			//Updated code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
			Date currentDate = new Date();
		    SimpleDateFormat smpdateformat = new SimpleDateFormat("MM-dd-yyyy");
		    String strReportExtractedDate = smpdateformat.format(currentDate);	
		    String strUserNameTemp = strUserName.replace(".","-");
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format :Start
			if(UIUtil.isNotNullAndNotEmpty(strReportFileName)) {
				strReportName =sbReportName.append(strReportFileName).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strReportExtractedDate).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strUserNameTemp).append(pgV3Constants.SYMBOL_UNDERSCORE).append(System.currentTimeMillis()).append(pgV3Constants.SYMBOL_DOT).append(strReportExtension).toString(); 
			} else {
				strReportName =sbReportName.append(strReportName).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strReportExtractedDate).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strUserNameTemp).append(pgV3Constants.SYMBOL_UNDERSCORE).append(System.currentTimeMillis()).append(pgV3Constants.SYMBOL_DOT).append(strReportExtension).toString();
			}
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format : End
			sbATSFolder = new File(strReportPath);
			if (!sbATSFolder.exists())  {
				sbATSFolder.mkdirs();
			}
		
			outputStream = new FileOutputStream(sbATSFolder.toString()+ File.separator + strReportName);
			workbook.write(outputStream);
			// code to create the object and checking the .excel file in that object
			createATSReportObject(context,strUserName,sbATSFolder.toString(),strReportName,strReportObjectId);
					
			//to delete the .xls file once its checked in to the newly created object
			//deleteFiles code removed as part of defect 34027
			mlATSDetailsAll.clear();
		} 
		catch (Exception e) {
			outLog.print("Exception in  createExcelWorkbook: "+e+"\n");
			outLog.flush();
		}
		finally{
			if (outputStream != null)
			{				
				outputStream.flush();
				outputStream.close();		
			}
		}
	}

	/*
    * this method is to Checkin Excel Sheet in to a ATS Used Report Object
    * @param context is the matrix context
    * @param args has the required information
    * @return void
    */
	private void createATSReportObject(Context context,String strUserName,String strReportPath,String strReportName,String strReportObjectId) throws MatrixException {
		boolean isContextPushed = false;
		try {
			String typePgDSMReport = PropertyUtil.getSchemaProperty(context,"type_pgDSMReport");
			String policyPgDSMReport = PropertyUtil.getSchemaProperty(context,"policy_pgDSMReport");
			String  strFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
			String strContextUserName = context.getUser();
			String strObjectName= EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ATSReport.ObjectName");
			String strNewObjectName = strObjectName+"_"+strContextUserName+"_"+System.currentTimeMillis();
			String strObjectId = strReportObjectId;
			Date currentDate = new Date();
			SimpleDateFormat smpdateformat = new SimpleDateFormat("MM-dd-yyyy");
			
			if(UIUtil.isNullOrEmpty(strObjectId))
			{
				String appName = FrameworkUtil.getTypeApplicationName(context, typePgDSMReport);
				DomainObject createBO = DomainObject.newInstance(context, typePgDSMReport, appName);
				BusinessObject bo = new BusinessObject(typePgDSMReport, strNewObjectName,DomainConstants.EMPTY_STRING, pgV3Constants.VAULT_ESERVICEPRODUCTION);
				if(!bo.exists(context))
				{
					createBO.createObject(context, typePgDSMReport, strNewObjectName,DomainConstants.EMPTY_STRING, policyPgDSMReport, pgV3Constants.VAULT_ESERVICEPRODUCTION);
					strObjectId = createBO.getObjectId(context);
				}
			}
				
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				ContextUtil.pushContext(context,pgV3Constants.PERSON_USER_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				
				isContextPushed = true;
				DomainObject doObj = DomainObject.newInstance(context, strObjectId);
				outLog.print("Before Checkin Report: "+strUserName+"| "+strReportName+"\n");
				outLog.flush();		
				doObj.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, strFormat, strReportName, strReportPath);
				doObj.promote(context);
				//Updated for Defect  34027 Starts
				String sFullPath = strReportPath.concat("/").concat(strReportName);	
				//Updated for Defect  34027 Ends
				File file = new File(sFullPath);
				Files.delete(file.toPath());
				outLog.print("After Checkin Report: "+strUserName+"| "+strReportName+"\n");
				outLog.flush();	
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
				sendEmail(context, strReportName, strUserName);
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
			} else {
			 	throw new MatrixException(ERR_MESSAGE);
			}		
		} catch (Exception e) {
			outLog.print("Exception in  createATSReportObject and Checkin File: "+e+"\n");
			outLog.flush();	
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
	}
	
	
	
	/*
	 * accessCheck- This method is to check access to input part for the user
	 * @return boolean
	 */

private boolean accessCheck(Context context, String strUserName, String strPartId)throws FrameworkException {
	boolean isContextPushed = false;
	boolean bAccess = false; 
	String strValue = DomainConstants.EMPTY_STRING;	
	boolean bIsEBPUser = isEBP(context, strUserName);
	if (bIsEBPUser) {
		bAccess = accessCheckEBP(context, strUserName, strPartId);	
	} else {
		try{
			DomainObject domainObject = DomainObject.newInstance(context);
			domainObject.setId(strPartId);				
			ContextUtil.pushContext(context, strUserName, null,context.getVault().getName());
			isContextPushed = true;
			strValue = domainObject.getInfo(context,DomainConstants.SELECT_CURRENT);	
				if(UIUtil.isNotNullAndNotEmpty(strValue) && !(DENIED).equals(strValue)){
				bAccess = true;					
			} else {				
				bAccess = false;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
	}	
	return bAccess;
}

		
/**
 * Description: This method is used to check ebp access as per DSM Security Model.
 * Fetching ART attribute[pgIPClassification] and Person attribute[pgSecurityEmployeeType].
 * Performing access checks on basis of ART Special Project Security Group.
 * Returns: True/False
 */
	private boolean accessCheckEBP(Context context, String strUserName,String strPartId) throws FrameworkException {
		boolean bHasaccess = false;
		boolean bHasaccessWithOutComment = false;
		boolean bHasaccessForCoProject = false;
		boolean bHasaccessForCoProjectWithoutComment = false;
		boolean bHasaccessUserProject = false;
		boolean bHasaccessUserProjectWithoutComment = false;
		boolean bAccessToPart = false;
		boolean bVendorHasNext = false;
		StringList slObjSelect = new StringList(1);
		slObjSelect.add(DomainConstants.SELECT_NAME);
		StringList slRelSelect = new StringList();
		String strValue = DomainConstants.EMPTY_STRING;
		String strUserOid = DomainConstants.EMPTY_STRING;
		short sQueryLimit = 5;
		Short sRecursionLevel = 1;
		StringList slSelect = new StringList();
		slSelect.add(DomainConstants.SELECT_ID);
		boolean isContextPushed = false; 
		Map<String,String> mpPersonData=new HashMap<>();
		Map<String,String> mpUserVendor=new HashMap<>();
		String strUserVendor=DomainConstants.EMPTY_STRING;
		try {
			ContextUtil.pushContext(context,//context
					PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),//user name
					DomainConstants.EMPTY_STRING,//password
					DomainConstants.EMPTY_STRING);//vault
			
			isContextPushed=true;			
			MapList mlPerson = DomainObject.findObjects(context,//context
					DomainConstants.TYPE_PERSON, //type
					strUserName, //name
					pgV3Constants.SYMBOL_HYPHEN,//revision
					DomainConstants.QUERY_WILDCARD,//owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION, //vault
					null, //whereExpression
					DomainConstants.EMPTY_STRING,//queryName
					true,//expandType
					slSelect, //objectSelects
					sQueryLimit);//objectLimit
			
			int maplistSize = mlPerson.size();
			for (int i = 0; i < maplistSize; i++) {
				mpPersonData = (Map) mlPerson.get(i);
				strUserOid = mpPersonData.get(DomainConstants.SELECT_ID);
			}
			DomainObject domPersonObj = DomainObject.newInstance(context,
					strUserOid);
			MapList mlRelatedUserVendors = domPersonObj.getRelatedObjects(context, //context
					pgV3Constants.RELATIONSHIP_MEMBER,//relationshipPattern 
					pgV3Constants.TYPE_PLANT+COMMA+pgV3Constants.TYPE_COMPANY,//typePattern
					slObjSelect, //objectSelects
					slRelSelect,//relationshipSelects 
					true, //getTo
					false,//getFrom
					sRecursionLevel,//recurseToLevel 
					null, //objectWhere
					null,//relationshipWhere
					0);//limit
			
			if (null !=mlRelatedUserVendors && !mlRelatedUserVendors.isEmpty()) {
				Iterator<Object> itrVendor = mlRelatedUserVendors.iterator();
				while (itrVendor.hasNext()) {
					mpUserVendor = (Map) itrVendor.next();
					strUserVendor= mpUserVendor.get(DomainConstants.SELECT_NAME);
					bHasaccess = DomainAccess.hasObjectOwnership(context,strPartId, strUserVendor,STR_PARTNERS_PG,STR_MULTI_OWNERSHIP_FOR_OBJ);
					bHasaccessWithOutComment = DomainAccess.hasObjectOwnership(context, strPartId, strUserVendor, STR_PARTNERS_PG,DomainConstants.EMPTY_STRING);
					bHasaccessForCoProject = DomainAccess.hasObjectOwnership(context, strPartId, strUserVendor + " Plant",STR_PARTNERS_PG, STR_MULTI_OWNERSHIP_FOR_OBJ);
					bHasaccessForCoProjectWithoutComment = DomainAccess.hasObjectOwnership(context, strPartId,strUserVendor + " Plant", STR_PARTNERS_PG, DomainConstants.EMPTY_STRING);
					bHasaccessUserProject = DomainAccess.hasObjectOwnership(context, strPartId, null, strUserName + "_PRJ",STR_MULTI_OWNERSHIP_FOR_OBJ);
					bHasaccessUserProjectWithoutComment = DomainAccess.hasObjectOwnership(context, strPartId, null,strUserName + "_PRJ", DomainConstants.EMPTY_STRING);

					if (bHasaccess || bHasaccessWithOutComment
							|| bHasaccessForCoProject
							|| bHasaccessForCoProjectWithoutComment
							|| bHasaccessUserProject
							|| bHasaccessUserProjectWithoutComment) {
						bAccessToPart = true;
						break;
					}
				}
			}
			
			if (!bAccessToPart) {
				// For parts where access is inherited
				DomainObject domainObject = DomainObject.newInstance(context);
				domainObject.setId(strPartId);
				ContextUtil.pushContext(context, strUserName, null, context
						.getVault().getName());
				isContextPushed=true;
				strValue = domainObject.getInfo(context, DomainConstants.SELECT_CURRENT);
				if(UIUtil.isNotNullAndNotEmpty(strValue) && !(DENIED).equals(strValue)){
					bAccessToPart = true;
				} else {
					bAccessToPart = false;
				}

			}
		} catch (Exception e) {
			bAccessToPart = false;
			e.printStackTrace();
		}
		finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		} 

		return bAccessToPart;
	}	

	/** This method is used to get the Hyperlink for Name columns in report
	 * @param context
	 * @param cell
	 * @param workbook
	 * @param strValue
	 * @param strId
	 * @throws FrameworkException 
	 */
	private void getHyperlink(Context context, Cell cell, XSSFWorkbook workbook,
		String strValue, String strId) throws FrameworkException {
		//Added for Apr22 CW requirement 41502 Starts
		String strURL = "";
		String strNewURL = "";
		//Added for Apr22 CW requirement 41502 Ends
		CreationHelper createHelper = workbook.getCreationHelper();
		XSSFCellStyle style = workbook.createCellStyle();
		Font hlinkfont = workbook.createFont();
		hlinkfont.setUnderline(Font.U_SINGLE);
		hlinkfont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
		style.setFont(hlinkfont);
		XSSFHyperlink link = (XSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
		if (UIUtil.isNotNullAndNotEmpty(strId) && UIUtil.isNotNullAndNotEmpty(strValue)) {
			//Updated for Apr22 CW requirement 41502 Starts
			if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && "ENOVIA".equalsIgnoreCase(strOriginatingSource)) {
				strURL = EnoviaResourceBundle.getProperty(context,EMXCPN, context.getLocale(), "emxCPN.BaseURL");
				strNewURL = String.valueOf(strURL).trim();
				link.setAddress(strNewURL + "?objectId=" + strId);
			} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && "SpecReader".equalsIgnoreCase(strOriginatingSource)) {
				strURL = getSpecReaderURL(context);
				strNewURL = String.valueOf(strURL).trim();
				link.setAddress(strNewURL + strValue);
			}
			//Updated for Apr22 CW requirement 41502 Ends
			cell.setCellValue(strValue);
			cell.setHyperlink(link);
			cell.setCellStyle(style);
		} else {
			cell.setCellValue(DomainConstants.EMPTY_STRING);
		}
	}
		 
		
	/** this method is to write column Names in excel sheet
	 * @param context
	 * @param rowHeaderPart
	 * @param cellStyleRelatedATS
	 * @param cellStyleCommonCol
	 */
	private void getColumnNames(Context context,XSSFRow rowHeaderPart,XSSFCellStyle cellStyleRelatedATS,XSSFCellStyle cellStyleCommonCol) {
		try {
			
			String strColumnNames = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.ATSReport.Worksheet.ColumnTypes");
			String strColConnectedPartName="ConnectedPartName";
			boolean bStyleConnectedPartCol=false;
			StringList slIndividualColumnNames = StringUtil.split(strColumnNames, COMMA);
			int slIndividualColumnNamesSize=slIndividualColumnNames.size();
			
			for (int i = 0;i<slIndividualColumnNamesSize;i++) {
				String columnName = slIndividualColumnNames.get(i);
				String strColumnName = String.valueOf(columnName).trim();
				String strColumnValue = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.ATSReport.Column."+strColumnName);
				 if(UIUtil.isNotNullAndNotEmpty(strColumnName) && strColConnectedPartName.equals(strColumnName))
				 {
					 bStyleConnectedPartCol=true;
					 
				 }
				Cell cell = rowHeaderPart.createCell(i);
				if(bStyleConnectedPartCol)
				{
					cell.setCellStyle(cellStyleCommonCol);
				}else
				{
					cell.setCellStyle(cellStyleRelatedATS);
				}
				cell.setCellValue(strColumnValue);	
			}
		}catch(Exception e) {
			outLog.print("Exception in  getColumnNames: "+e+"\n");
			outLog.flush();
		}
	}
	
	
	
	/** this method is to update Excel Sheet with fetched data
	 * @param context
	 * @param workbook
	 * @param rowHeaderPartLib
	 * @param mlATSDetailsAll
	 * @param sheetPartLib
	 * @param cellStyleRelatedATS
	 * @param cellStyleCommonCol
	 */
	private void updateWorksheetRelatedATS(Context context,XSSFWorkbook workbook, XSSFRow rowHeaderPartLib,MapList mlATSDetailsAll, XSSFSheet sheetPartLib,XSSFCellStyle cellStyleRelatedATS, Map<String,Object> mPassValue) {
		try {
			//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ATSReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll=0;
			String strHyperlink = (String)mPassValue.get(HYPERLINKASINPUT);
			XSSFCellStyle cellStyleCommonCol = (XSSFCellStyle)mPassValue.get("CellStyle");
			//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
			String strCellValue = DomainConstants.EMPTY_STRING;
			String strHyperlinkId = DomainConstants.EMPTY_STRING;
			String strValue = DomainConstants.EMPTY_STRING;
			Map<String,Object> mpATS=new HashMap<>();
			Map<String,Object> htCommonCol=new HashMap<>();
			HashMap<Integer,Object> hmFinal=new HashMap<>();
			String strTypeDisplayName=DomainConstants.EMPTY_STRING;
			String strPrimaryOrganizationSelect = FROM+pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION+NAME_SELECT;
			getColumnNames(context, rowHeaderPartLib, cellStyleRelatedATS,cellStyleCommonCol);

			String sLanguage = context.getSession().getLanguage();
			int rowCount1 = 0;
			int rowCount = (sheetPartLib.getLastRowNum())- (sheetPartLib.getFirstRowNum());
			if (rowCount != 0)
				rowCount1 = rowCount;
			int columnCount1 = 0;
			int mlATSDetailsAllSize=mlATSDetailsAll.size();
			int hmFinalSize=0;
			String strTypeName = DomainConstants.EMPTY_STRING;
			String[] strSplittedValue;
			for (int i = 0; i <mlATSDetailsAllSize; i++) {
				//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
				iRowCountAll = iRowCountAll + 1;
				//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
				XSSFRow row = sheetPartLib.createRow(++rowCount1);
				columnCount1 = 0;
				mpATS = (Map) mlATSDetailsAll.get(i);

				hmFinal= new HashMap<>();
				htCommonCol= (Map)mpATS.get(STRFINALCOMMONCOLUMNS);	
				// Added code for Req Id : 34301 - Hyperlinks--Starts
				hmFinal.put(0,HYPERLINK+ htCommonCol.get(DomainConstants.SELECT_NAME)+ HYPERLINK_PIPE+ htCommonCol.get(DomainConstants.SELECT_ID));
				// Added code for Req Id : 34301 - Hyperlinks--Ends
				hmFinal.put(1, htCommonCol.get(DomainConstants.SELECT_REVISION));
				hmFinal.put(2, htCommonCol.get(DomainConstants.SELECT_CURRENT));			
				strTypeName = (String)htCommonCol.get(ATTR_IMPACTEDTYPE);
				//modified the code for Defect 34528 Incorrect Type for Impacted Type of ATS Starts
				hmFinal.put(3, strTypeName);
				//modified the code for Defect 34528 Incorrect Type for Impacted Type of ATS Ends
				
				hmFinal.put(4, htCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
				hmFinal.put(5, htCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE));
				hmFinal.put(6,htCommonCol.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				//updated code for Req Id 36706 The report shall include Description attribute for input and related Part-Starts
				hmFinal.put(7, htCommonCol.get(DomainConstants.SELECT_DESCRIPTION));
				//updated code for Req Id 36706 The report shall include Description attribute for input and related Part-Ends
				hmFinal.put(8, htCommonCol.get(strPrimaryOrganizationSelect));
				// Added code for Req Id : 34301 - Hyperlinks--Starts
				hmFinal.put(9, HYPERLINK + mpATS.get(DomainConstants.SELECT_NAME)+ HYPERLINK_PIPE + mpATS.get(DomainConstants.SELECT_ID));
				// Added code for Req Id : 34301 - Hyperlinks--Ends
				hmFinal.put(10, mpATS.get(DomainConstants.SELECT_REVISION));
				hmFinal.put(11, mpATS.get(DomainConstants.SELECT_CURRENT));
				strTypeName = (String) mpATS.get(DomainConstants.SELECT_TYPE);
				strTypeDisplayName = i18nNow.getTypeI18NString(strTypeName,sLanguage);
				hmFinal.put(12, strTypeDisplayName);
				hmFinal.put(13, mpATS.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
				hmFinal.put(14, mpATS.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				//updated code for Req Id 36706 The report shall include Description attribute for input and related Part-Starts
				hmFinal.put(15, mpATS.get(DomainConstants.SELECT_DESCRIPTION));
				//updated code for Req Id 36706 The report shall include Description attribute for input and related Part-Ends
				hmFinal.put(16, mpATS.get(strPrimaryOrganizationSelect));

				// For Cells Creation in each Row
				hmFinalSize=hmFinal.size();
				for (int j = 0; j <hmFinalSize;j++) {
					Cell cell = row.createCell(columnCount1++);
					if (hmFinal.get(j) instanceof StringList) {
						strCellValue =  (hmFinal.get(j).toString().replace("[", "").replace("]", ""));
					} else {
						strCellValue = (String) hmFinal.get(j);
					}
					// Added code for Req Id : 34301 - Hyperlinks--Starts
					if (UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)) {
						strSplittedValue = strCellValue.split("\\|",-1);
						strHyperlinkId = strSplittedValue[(strSplittedValue.length) - 1];
						strValue = strSplittedValue[(strSplittedValue.length) - 2];
						//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){					
						//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
							getHyperlink(context, cell, workbook, strValue,
									strHyperlinkId);
						//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Starts
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)) {					
							cell.setCellValue(strValue);
						//Added code for 2018x.6 Requirement id 36703 Ability to generate ATS report with-without hyperlink Ends
						}
					} // Added code for Req Id : 34301 - Hyperlinks--Ends
					else 
					{
						cell.setCellValue(strCellValue);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			outLog.print("Exception in  updateWorksheetRelatedATS: "+e+"\n");
			outLog.flush();	
		}
	}
			
	/** This method is used to Fetch all ATS related data of an object
	 * @param context
	 * @param dobjPart
	 * @param mPartColumn
	 * @param strUserName
	 * @return MapList
	 * @throws FrameworkException
	 */
	private MapList getRelatedAtsDetails(Context context,DomainObject dobjPart,Map<String,Object> mPartColumn,String strUserName)throws FrameworkException
	{
		MapList mlFinalList = new MapList();
		Map<String,Object> mpConnectedPart=new HashMap<>();
		Map<String,Object> mATSPartDetails = new HashMap<>();
		String strPartId =DomainConstants.EMPTY_STRING;
		String strRelatedPartPolicy = DomainConstants.EMPTY_STRING;
		String strInputPolicy = DomainConstants.EMPTY_STRING;
		String strInputTranslatedState = DomainConstants.EMPTY_STRING;
		String strRelatedPartTranslatedState = DomainConstants.EMPTY_STRING;
		String strRelatedPartState = DomainConstants.EMPTY_STRING;
		String strInputState = DomainConstants.EMPTY_STRING;
		String strPrimaryOrganizationSelect = FROM+pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION+NAME_SELECT;
		StringList selectStmts = new StringList();          // putting elements to StringList
		
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		selectStmts.addElement(DomainConstants.SELECT_REVISION);
		selectStmts.addElement(DomainConstants.SELECT_CURRENT);
		selectStmts.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		selectStmts.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		selectStmts.add(DomainConstants.SELECT_POLICY);		
		selectStmts.add(strPrimaryOrganizationSelect);
		//Req Id 36706 The report shall include Description attribute for input and related Part-Starts
		selectStmts.add(DomainConstants.SELECT_DESCRIPTION);	
		//Req Id 36706 The report shall include Description attribute for input and related Part-Ends
		boolean isContextPushed = false; 
		boolean bAccess = false;
		int iRelatedPartListSize = 0;
		String strLanguage = DomainConstants.EMPTY_STRING;
		
		try {
			Pattern relPattern = new Pattern(pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION);
			//Req Id: 34299 The ATS Related Parts Report shall not return Obsoleted Related Parts-Starts
			String strObjWhere = "current!="+pgV3Constants.STATE_OBSOLETE+"";
			//Req Id: 34299 The ATS Related Parts Report shall not return Obsoleted Related Parts-Ends
			
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);				 
			isContextPushed=true;
			MapList relatedPartList = dobjPart.getRelatedObjects(context,
					relPattern.getPattern(),  //relationship pattern
					pgV3Constants.SYMBOL_STAR,                         // object pattern
					selectStmts,                 // object selects
					null,              // relationship selects
					false,                        // to direction
					true,                       // from direction
					(short)1,                    // recursion level
					strObjWhere,                        // object where clause
					null,//relationshipWhere
					0);//limit
        		
			//Req Id: 34300:The Report shall have the following fields displayed for the corresponding Related Part(s) Name,Revision,Maturity State,Release Date,Title,Primary Organization-Starts
			if(!relatedPartList.isEmpty()){
				iRelatedPartListSize=relatedPartList.size();
				for(int i=0; i<iRelatedPartListSize;i++){
					mpConnectedPart = (Map)relatedPartList.get(i);					
					strPartId= (String)mpConnectedPart.get(DomainConstants.SELECT_ID);            				
					mATSPartDetails = new HashMap<>();
					strLanguage=context.getLocale().getLanguage();
            				
					strInputState=(String)mPartColumn.get(DomainConstants.SELECT_CURRENT);
					strInputPolicy = (String) mPartColumn.get(DomainConstants.SELECT_POLICY);
					strInputTranslatedState = i18nNow.getStateI18NString(strInputPolicy, strInputState, strLanguage);
					mPartColumn.put(DomainConstants.SELECT_CURRENT, strInputTranslatedState);
					
					mATSPartDetails.put(STRFINALCOMMONCOLUMNS,mPartColumn);
					
					mATSPartDetails.put(DomainConstants.SELECT_TYPE, mpConnectedPart.get(DomainConstants.SELECT_TYPE));
					mATSPartDetails.put(DomainConstants.SELECT_ID, mpConnectedPart.get(DomainConstants.SELECT_ID));
					mATSPartDetails.put(DomainConstants.SELECT_NAME, mpConnectedPart.get(DomainConstants.SELECT_NAME));
					mATSPartDetails.put(DomainConstants.SELECT_REVISION, mpConnectedPart.get(DomainConstants.SELECT_REVISION));
					
					//Req Id: 34559: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-Starts
					bAccess = accessCheck(context,strUserName,strPartId);
					//Req Id: 34559: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-Ends
					if (bAccess){
						
						strRelatedPartState=(String)mpConnectedPart.get(DomainConstants.SELECT_CURRENT);
						strRelatedPartPolicy = (String) mpConnectedPart.get(DomainConstants.SELECT_POLICY);
						strRelatedPartTranslatedState = i18nNow.getStateI18NString(strRelatedPartPolicy, strRelatedPartState, strLanguage);
						
						mATSPartDetails.put(DomainConstants.SELECT_CURRENT, strRelatedPartTranslatedState);
						mATSPartDetails.put(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE, mpConnectedPart.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
						mATSPartDetails.put(SELECT_ATTRIBUTE_TITLE, mpConnectedPart.get(SELECT_ATTRIBUTE_TITLE));
						mATSPartDetails.put(FROM+pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION+NAME_SELECT, mpConnectedPart.get(strPrimaryOrganizationSelect));
						//Req Id 36706 The report shall include Description attribute for input and related Part-Starts
						mATSPartDetails.put(DomainConstants.SELECT_DESCRIPTION, mpConnectedPart.get(DomainConstants.SELECT_DESCRIPTION));
						//Req Id 36706 The report shall include Description attribute for input and related Part-Ends
					}
					else
					{	
						//Req Id: 34559: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-Starts
						mATSPartDetails.put(DomainConstants.SELECT_CURRENT, pgV3Constants.NO_ACCESS);
						mATSPartDetails.put(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE,pgV3Constants.NO_ACCESS);
						mATSPartDetails.put(SELECT_ATTRIBUTE_TITLE,pgV3Constants.NO_ACCESS);
						mATSPartDetails.put(FROM+pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION+NAME_SELECT, pgV3Constants.NO_ACCESS);
						//Req Id 36706 The report shall include Description attribute for input and related Part-Starts
						mATSPartDetails.put(DomainConstants.SELECT_DESCRIPTION, pgV3Constants.NO_ACCESS);
						//Req Id 36706 The report shall include Description attribute for input and related Part-Ends
					}
					//Req Id: 34559: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-Ends
					mlFinalList.add(mATSPartDetails);            			
            	}
			}	
		} catch (Exception e){	
			outLog.print("Exception in  getRelatedAtsDetails: "+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return mlFinalList;
	}
			
	/**
	 * Description: This method is used to check if user is EBP CM.
	 * Returns: True/False
	 */
	private boolean isEBP(Context context, String strUserName) throws FrameworkException {
		boolean bIsEBP = false; 
		Map<String,String> mpPersonData=new HashMap<>();
		try{
			String pgSecurityEmployee=PropertyUtil.getSchemaProperty(context,"attribute_pgSecurityEmployeeType");
			String strUserSecurityType=DomainConstants.EMPTY_STRING;
			StringList slSelect = new StringList(3);											
			slSelect.add(DomainConstants.SELECT_CURRENT);
			slSelect.add(DomainConstants.SELECT_ID);
			slSelect.add("attribute["+pgSecurityEmployee+"]");
			
			MapList mlPerson = DomainObject.findObjects(context,//context
					DomainConstants.TYPE_PERSON,//type
					strUserName,//name
					pgV3Constants.SYMBOL_HYPHEN,//revision
					DomainConstants.QUERY_WILDCARD,//owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
					DomainConstants.EMPTY_STRING,//whereExpression
					false,//expandType
					slSelect);//objectSelects
			
			int maplistSize = mlPerson.size();
			for(int i=0;i<maplistSize;i++){
				mpPersonData = (Map)mlPerson.get(i);					
				strUserSecurityType = mpPersonData.get("attribute["+pgSecurityEmployee+"]");					
			}
			if (strUserSecurityType.equalsIgnoreCase(EBP)) {
				bIsEBP = true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return bIsEBP;
	}
	
	/** getCommonColumnsDetail Map for Common  Columns 
	 * @param context
	 * @param dobjRelatedPart
	 * @return Map
	 */
	private Map<String,Object> getCommonColumnsDetail(Context context, DomainObject dobjRelatedPart) {
		Map<String, Object> mTSDetail = null;
		try {
			mTSDetail = dobjRelatedPart.getInfo(context, SL_OBJECT_COMMONINFO_SELECT);			
		} catch (FrameworkException e) {
			e.printStackTrace();
		}	
		return mTSDetail;
	}
			
	/**
	 * this method is to generate the Excel Sheet for the input .txt file which will be browsed by the user
	 * @param context is the matrix context
	 * @param args
	 * @return void
	 * @throws IOException 
	 * @throws Exception
	 */
	public void generateATSReportForInputFile(Context context, String[] args) throws Exception
	{
		String strReportFileName=DomainConstants.EMPTY_STRING;
		String strReportObjectId=DomainConstants.EMPTY_STRING;
		String strUserName=DomainConstants.EMPTY_STRING;
		String strLatestVersionAsInput=DomainConstants.EMPTY_STRING;
		String line = DomainConstants.EMPTY_STRING;
		HashMap<String, Object> hmArgs=(HashMap) JPO.unpackArgs(args);
		StringBuilder finalVal =new StringBuilder();
		
		File file = (File) hmArgs.get("DSMReportFile");
		
		try(BufferedReader buffReaderForMQLGeneratedInputFile = new BufferedReader(new FileReader(file))) {
			
			strReportFileName = (String) hmArgs.get(REPORTFILENAME);
			strReportObjectId = (String) hmArgs.get(REPORTOBJECTID);
			strUserName = (String) hmArgs.get(USERNAME);
			strLatestVersionAsInput =(String) hmArgs.get(LATESTRELEASEPART);
			
				while ((line = buffReaderForMQLGeneratedInputFile.readLine()) != null) {
					if(line.length()>0){
						if(!line.contains(COMMA))
						{
							finalVal.append(line);
							finalVal.append(COMMA);	
						}
						else 
							finalVal.append(line);
					}
				}
				String strPartLibName= finalVal.toString();		 
				if(UIUtil.isNotNullAndNotEmpty(strUserName) && UIUtil.isNotNullAndNotEmpty(strPartLibName)){			 
					HashMap<String,String> hmArgs1 = new HashMap<>();
					hmArgs1.put(USERNAME,strUserName);
					hmArgs1.put("GCAS",strPartLibName);
					hmArgs1.put(REPORTFILENAME,strReportFileName);
					hmArgs1.put(REPORTOBJECTID,strReportObjectId);
					hmArgs1.put(LATESTRELEASEPART,strLatestVersionAsInput);
					BackgroundProcess backgroundProcess = new BackgroundProcess();
					backgroundProcess.submitJob(context, "com.pg.dsm.custom.pgATSReports", "generateATSReport", JPO.packArgsRemote(hmArgs1) , (String)null);
			}
		}
		catch (Exception e) {
			ContextUtil.abortTransaction(context);
			outLog.print("Exception in  generateATSReportForInputFile: "+e+"\n");
			outLog.flush();	
			throw e;
		}
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
		try {

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
			if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && STR_ENOVIA.equalsIgnoreCase(strOriginatingSource)) {
			strUrl = EnoviaResourceBundle.getProperty(context,EMXCPN, context.getLocale(), "emxCPN.BaseURL");
			strUrl = strUrl.substring(0, strUrl.indexOf(PATH_SEPARATOR+STR_COMMON));
			strUrl = String.valueOf(strUrl).trim();
			strUrlPath = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.Enovia.DSMReportLink");
			sbUrl.append(strUrl);
			sbUrl.append(strUrlPath);
		} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && STR_SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
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
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
}

