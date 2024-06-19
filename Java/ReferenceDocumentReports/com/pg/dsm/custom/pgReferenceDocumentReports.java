/*   pgReferenceDocumentReports.java - Report Request For Reference Document Report Download written for requirement 34322
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
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

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
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
//added for REq Id:33634-starts
import org.apache.poi.ss.usermodel.Font;

import com.matrixone.apps.common.CommonDocument;
//added for REq Id:33634-Ends
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

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

public class pgReferenceDocumentReports implements DomainConstants {
	

	public pgReferenceDocumentReports(Context context, String[] args) {		
	}
	    
		//Hyperlink--Starts
	    private static final String HYPERLINK = "Hyperlink:|";
		private static final String HYPERLINK_PIPE = "|";
		private static final String EMXCPN = "emxCPN";
		private static final String HYPERLINK_COMPARE = "Hyperlink:";
		//Hyperlink--Ends
		private StringList SLOBJECTCOMMONINFOSELECT = getObjectCommonSelects();
		private static final String USERNAME="UserName";
		private static final String REPORTFILENAME="ReportFileName";
		private static final String REPORTOBJECTID="ReportObjectId";
		private static final String LATESTRELEASEPART="LatestVersionAsInputRefDoc";
		private static final String STRFINALCOMMONCOLUMNS = "CommonColumns";
		private static final String CPNSTRINGRESOURCE = "emxCPNStringResource";
		private static final String DESCENDING = "descending";
		private static final String STRING = "string";
		private static final String EBP = "EBP";
		private static final String STR_PARTNERS_PG="Partners_PG";
		private static final String STR_MULTI_OWNERSHIP_FOR_OBJ="Multiple Ownership For Object";
		private static final String DENIED = "#DENIED!";
		private static final String COMMA = ",";
		//Removed attribute pgSubType as part of Defect 34024
		//added for Defect 34024 Subtypes are not being pick up by Reference Document report Starts
		private static final String SUBTYPE = "SubType";
		private static final String STRFROMSTART ="from[";
		private static final String STRTONAME ="].to.name";
		//added for Defect 34024 Subtypes are not being pick up by Reference Document report Ends
		private static final String ERR_MESSAGE="Creation of Object Failed";
		private static final String REVISION_RENDITION = "Rendition";
		private static final String INPUTPARTCURRENT="InputPartCurrent";
		private static final String REFERENCEDOCCURRENT="ReferenceDocCurrent";
		private static final String RELEASED_STATE="RELEASED";
		private static final String CPNRESOURCEFILE ="emxCPN";
		private static final String DATE_YMDHS = "yyyy.MM.dd.HH.mm.ss";
		private static final String DIRECTORY_ERR = "Could not create directory";	
		private static final String CATALINA_BASE="catalina.base";
		private static final String PATH_SEPARATOR="/";			
		private PrintWriter outLog = null;
		//Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
		private static final String HYPERLINKASINPUT = "HyperlinkAsInput";
		//Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
		//Added code for 2018x.6 requirement id 36708 Generate Reference Document report directly Starts
		private static final String REALTIMEPROCESS = "RealTimeProcess"; 
		//Added code for 2018x.6 requirement id 36708 Generate Reference Document report directly Ends
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
		private static final String STR_COMMON="common";
		private static final String STR_SYMBOL_BRACKETS="{0}";
		private static final String STR_TEXT_HTML="text/html";
		private static final String SPECREADER = "SpecReader";
		private static final String ENOVIA = "ENOVIA";
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
		public void generateReferenceDocumentReport(Context context, String[] args) throws Exception {
		File fFolderPath = null;
		StringBuilder sbLogFolder = new StringBuilder();
		File fLogFolder = null;
		String strDirectoryNotCreated = DIRECTORY_ERR;
		String strStartTime = null; 
		String sLanguage = context.getSession().getLanguage();
		String strJVM = getJVMInstance();
	    HashMap<String,String> hmArgs =(HashMap) JPO.unpackArgs(args);
	    String strUserName = hmArgs.get(USERNAME);
	    String strPartNames = hmArgs.get("GCAS");	    
	    String strReportFileName =hmArgs.get(REPORTFILENAME);
	    String strReportObjectId = hmArgs.get(REPORTOBJECTID);	  
	    String strLatestVersionAsInput = hmArgs.get(LATESTRELEASEPART);
	    //Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
	    String strHyperlink = hmArgs.get(HYPERLINKASINPUT);
	    //Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
	    //Added code for 2018x.6 requirement id 36708 Generate Reference Document report directly Starts
	    String strRealTimeProcess = hmArgs.get(REALTIMEPROCESS);
	    //Added for Apr22 CW requirement 41502 Starts
	  	strOriginatingSource = hmArgs.get("OriginatingSource");
	  	if(UIUtil.isNullOrEmpty(strOriginatingSource)) {
	  		strHyperlink = "false";
	  	}
	  	//Added for Apr22 CW requirement 41502 Ends
	    String configLOGFilePath = DomainConstants.EMPTY_STRING;
	    if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
	    	configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Log.FilePath");
	    else
	    	configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReportCTRLMJob.Log.FilePath");
	    //Added code for 2018x.6 requirement id 36708 Generate Reference Document report directly Ends
	    sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
	    fLogFolder = new File(sbLogFolder.toString());
	    if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
	    	throw new IOException(strDirectoryNotCreated + fLogFolder);
	    }
	    outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"ReferenceDocumentReportLog.log",true));
	    strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());
		Map<String,String> mHyperLinkDetails = new HashMap<>();		
		mHyperLinkDetails.put("HyperlinkAsInput",strHyperlink);
		mHyperLinkDetails.put("RealTimeProcess",strRealTimeProcess);
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames))){
	    	DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
	    	outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+" | JVM: "+strJVM+"--------\n");
	 	    outLog.print("Parts: "+strPartNames+"\n");
	 	    outLog.print("Report Object Id: "+strReportObjectId+"\n");
	 	    outLog.flush();
	    	doObj.promote(context);
	    	//Updated code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
	    	generateReferenceDocumentReportProcess(context,strUserName,strPartNames,strReportFileName,strReportObjectId,strLatestVersionAsInput,mHyperLinkDetails);
	    	//Updated code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
	   } else {
		    outLog.print("-------Report requested by: " +strUserName+" : "+strStartTime+"--------\n");
		    outLog.print("Report cannot be generated. Check Report Object Id for request details: "+strReportObjectId+"\n");
		    outLog.flush(); 
	   }
	}

	/** this method is to get valid part from input
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
		String strDocWhereClauseTrue=DomainConstants.EMPTY_STRING;
		String strWhereClauseFalse=DomainConstants.EMPTY_STRING;
		String strPartWhereClauseTrue=DomainConstants.EMPTY_STRING;
		int mlValidPartSize=0;
		int mlValidPartDetailsSize=0;
		try {
			
			for (Object inputPartName : slIndividualPartNames) {				
				strPartName = String.valueOf(inputPartName).trim();	
				strPartWhereClauseTrue = "current=="+pgV3Constants.STATE_RELEASE+pgV3Constants.SYMBOL_OR+"current=="+pgV3Constants.STATE_COMPLETE+pgV3Constants.SYMBOL_OR+"current=="+pgV3Constants.STATE_RELEASED+pgV3Constants.SYMBOL_OR+"current=="+RELEASED_STATE+"";
				strDocWhereClauseTrue = "current=="+pgV3Constants.STATE_RELEASE+pgV3Constants.SYMBOL_OR+"current=="+pgV3Constants.STATE_RELEASED+pgV3Constants.SYMBOL_OR+"current=="+RELEASED_STATE+"";
				strWhereClauseFalse = "revision=="+DomainConstants.SELECT_LATEST_REVISION+"";
				if(UIUtil.isNotNullAndNotEmpty(strPartName)){
					if(UIUtil.isNotNullAndNotEmpty(strLatestVersionAsInput) && pgV3Constants.TRUE.equals(strLatestVersionAsInput)) {
						mlPart=getHighestRevisionPart(context,strPartName,strPartWhereClauseTrue,strDocWhereClauseTrue);
						mlValidPart.add(mlPart);
					}
					else
					{
						//Modified for defect 34433 Cannot extract Reference Document Report from Obsolete Parts Starts					
						mlPart=getHighestRevisionPart(context,strPartName,strWhereClauseFalse,strWhereClauseFalse);
						//Modified for defect 34433 Cannot extract Reference Document Report from Obsolete Parts Ends
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
		}catch(Exception e) {			
			outLog.print("Exception in  getPartList : "+e+"\n");
			outLog.flush();
		}
		return mlreturnPart;
	}
	
	
	/*
     * this method is to get highest revision input part 
     * @param context is the matrix context
     * @return MapList
     */
	private MapList getHighestRevisionPart(Context context,String strPartName,String strWhereExpPart,String strWhereExpDoc)throws FrameworkException
	{
 
		String strType = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ReferenceDocumentReport.Types");
		StringList slSelect = new StringList();			
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
			MapList mlPartObj = DomainObject.findObjects(context,strType,strPartName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,strWhereExpPart,false,slSelect);
			//fetching  Document  parts
			MapList mlPartDoc = DomainObject.findObjects(context,DomainConstants.TYPE_DOCUMENT,strPartName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,strWhereExpDoc,true,slSelect);
			if(!mlPartDoc.isEmpty())
			{
				mlPartObj.addAll(mlPartDoc);
			}
			//Fetching Highest Release revision --Begin	
			if(!mlPartObj.isEmpty())
			{
				mlPartObjSize=mlPartObj.size();
				if(mlPartObjSize>1)
				{
					//Added for requirement : 37382 - starts
					mlPartObj.sort(DomainConstants.SELECT_TYPE, DESCENDING, STRING);
					mlPart=getHighestRevForAllTypes(mlPartObj);
					//Added for requirement : 37382 - ends
				}
				else
				{
					mReleaseHighestRevision = (Map)mlPartObj.get(0);	
					mlPart.add(mReleaseHighestRevision);
				}
			}
		}catch(Exception e) {
			outLog.print("Exception in  getHighestRevisionPart: "+e+"\n");
			outLog.flush();	
		}
		return mlPart;
	}
 
   	
	/** this method is to generate the Exel Sheet
	 * @param context
	 * @param strUserName
	 * @param strPartNames
	 * @param strReportFileName
	 * @param strReportObjectId
	 * @param strLatestVersionAsInput
	 */
	public void generateReferenceDocumentReportProcess(Context context,String strUserName,String strPartNames,String strReportFileName, String strReportObjectId,String strLatestVersionAsInput,Map<String,String> mHyperLinkDetails) {
		 
		try {
			StringList slSelect = new StringList();			
			slSelect.add(DomainConstants.SELECT_ID);	
			slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);	
			slSelect.add(DomainConstants.SELECT_CURRENT);
			slSelect.add(DomainConstants.SELECT_NAME);	
			slSelect.add(DomainConstants.SELECT_TYPE);
			slSelect.add(DomainConstants.SELECT_REVISION);
			slSelect.add(DomainConstants.SELECT_LAST_ID);  
			
			MapList	mlRefDocDetails=new  MapList();
			MapList mlRefDocDetailsAll=new  MapList();
			MapList mlPart = new MapList();
			Map<String,Object> mp= new HashMap<>();
			Map<String,Object> mpTemp=new HashMap<>();
			Map<String,Object> mPartColumn= new HashMap<>();
			String strPartObjectId=DomainConstants.EMPTY_STRING;
			String strPartObjectName=DomainConstants.EMPTY_STRING;
			
			StringList   slIndividualPartNames = StringUtil.split(strPartNames, COMMA);
			mlPart=getPartList(context,slIndividualPartNames,strLatestVersionAsInput);
			int iPartMLSize = 0;
			int imlRefDocDetailsSize = 0;
			boolean bInputPartAccess = false; 
			if(null !=mlPart && !mlPart.isEmpty()){
				iPartMLSize=mlPart.size();
				for(int i=0;i<iPartMLSize;i++)
				{
					mp = (Map)mlPart.get(i);
					strPartObjectId= (String)mp.get(DomainConstants.SELECT_ID);
					bInputPartAccess=accessCheck(context, strUserName, strPartObjectId);
					if(bInputPartAccess)
					{
						strPartObjectName=(String) mp.get(DomainConstants.SELECT_NAME);
						outLog.print("Expand start for: "+strUserName+": "+strPartObjectId+"|" +strPartObjectName+ "\n");
						outLog.flush();	
						//Req Id:34558-Report shall have the following fields displayed for the corresponding inputted parts/docs:Name,Revision,Maturity State,Release Date,Title,Primary Organization-Starts
						DomainObject dobjPart = DomainObject.newInstance(context, strPartObjectId);
						mPartColumn=getCommonColumnsDetail(context,dobjPart);
							
						//Req Id:34558-Report shall have the following fields displayed for the corresponding inputted parts/docs:Name,Revision,Maturity State,Release Date,Title,Primary Organization-Ends
						mlRefDocDetails=getReferenceDocDetails(context,dobjPart,mPartColumn,strUserName);
						
						if(null !=mlRefDocDetails && !mlRefDocDetails.isEmpty()){
							imlRefDocDetailsSize=mlRefDocDetails.size();
							for(int iIndex = 0; iIndex <imlRefDocDetailsSize; iIndex++){
								mpTemp = (Map)mlRefDocDetails.get(iIndex);
								mlRefDocDetailsAll.add(mpTemp);
							}
							mlRefDocDetails.clear();
						}
						outLog.print("Expand end for: "+strUserName+": "+strPartObjectId+"|" +strPartObjectName+ "\n");
						outLog.flush();
					} else {
						outLog.print("Could not Expand: "+strUserName+": "+strPartObjectId+"|" +pgV3Constants.NO_ACCESS+ "\n");
						outLog.flush();	
					}
				}
			}
		//Updated code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
		createExcelWorkbook(context,mlRefDocDetailsAll,strReportFileName,strReportObjectId,strUserName,mHyperLinkDetails);	
		//Updated code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
		mlRefDocDetailsAll.clear();
		String strEndTime = null; 
		strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
		outLog.print("------Report completed for: "+strUserName+": "+strPartNames+"|" +strReportFileName+ "-------\n");
		outLog.print("------Time completed: "+strEndTime+"-------\n");	
		outLog.print("------\n");
		outLog.flush();
	} catch (Exception e) {
		outLog.print("Exception in  generateReferenceDocumentReportProcess: "+strUserName+": "+e+"\n");
		outLog.flush();
	}
	finally {
		outLog.close();
	}		
}

	/** getObjectCommonSelects- Stringlist for Common Columns 
	 * @return StringList
	 */
	public StringList getObjectCommonSelects(){
		
		StringList slObjInfoSelect = new StringList(10);
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
			slObjInfoSelect.add(STRFROMSTART.concat(pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION).concat(STRTONAME));
			slObjInfoSelect.add(DomainConstants.SELECT_POLICY);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return slObjInfoSelect;
	}

	/*
     * this method is to write column Names in excel sheet
     * @param context is the matrix context
     * @return void
     */
	private void getColumnNames(Context context,XSSFRow rowHeaderPart,XSSFCellStyle cellStyleReferenceDocument,XSSFCellStyle cellStyleConnectedPartCol) {
		try {
			String columnName=DomainConstants.EMPTY_STRING;
			String strColumnName=DomainConstants.EMPTY_STRING;
			String strColumnValue=DomainConstants.EMPTY_STRING;
			String strColumnNames =EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.ReferenceDocumentReport.Worksheet.ColumnTypes");
			String strColDocumentName="DocumentName";
			boolean bStyleConnectedPartCol=false;
			StringList slIndividualColumnNames = StringUtil.split(strColumnNames, COMMA);
			for (int i = 0;i<slIndividualColumnNames.size();i++) {
				 columnName = slIndividualColumnNames.get(i);
				 strColumnName = String.valueOf(columnName).trim();
				 strColumnValue = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.ReferenceDocumentReport.Column."+strColumnName);
				 if(UIUtil.isNotNullAndNotEmpty(strColumnName) && strColDocumentName.equals(strColumnName))
				{
					bStyleConnectedPartCol=true;
				}
				 Cell cell = rowHeaderPart.createCell(i);
				
				if(bStyleConnectedPartCol)
				{
					cell.setCellStyle(cellStyleConnectedPartCol);
				}else
				{
					cell.setCellStyle(cellStyleReferenceDocument);
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
	 * @param rowHeaderTablesRefDocs
	 * @param mlRefDocDetails
	 * @param sheetReferenceDocuments
	 * @param cellStyleReferenceDocument
	 * @param cellStyleConnectedPartCol
	 */
	private void updateWorksheetReferenceDocuments(Context context,XSSFWorkbook workbook,XSSFRow rowHeaderTablesRefDocs,MapList mlRefDocDetails,XSSFSheet sheetReferenceDocuments,Map<String,Object> mCellStyle,String strHyperlink)
	{	
		try
		{
			//Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
			XSSFCellStyle cellStyleConnectedPartCol=(XSSFCellStyle)mCellStyle.get("CellStyle");
			XSSFCellStyle cellStyleReferenceDocument=(XSSFCellStyle)mCellStyle.get("CellStyleRefDoc");
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ReferenceDocumentReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll = 0;
			//Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
			String strHyperlinkId = DomainConstants.EMPTY_STRING;
			String strValue = DomainConstants.EMPTY_STRING;
			String strTypeName = DomainConstants.EMPTY_STRING;
			Map<String,Object> mpRefDoc=new HashMap<>();
			HashMap<Integer,Object> hmFinal=new HashMap<>();
			String strTypeDisplayName=DomainConstants.EMPTY_STRING;
			Map<String,Object> htCommonCol=new HashMap<>();
			String strCellValue = DomainConstants.EMPTY_STRING;
			//Added for 2018x.6 Jun CW Requirement 42873 - Starts
			boolean isIRMDocumentType = false;
			//Added for 2018x.6 Jun CW Requirement 42873 - Ends
			String strPrimaryOrganizationSelect = STRFROMSTART+pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION+STRTONAME;
			
			getColumnNames(context, rowHeaderTablesRefDocs, cellStyleReferenceDocument,cellStyleConnectedPartCol);
			String sLanguage = context.getSession().getLanguage();			
			int rowCount1 = 0;
			int rowCount = (sheetReferenceDocuments.getLastRowNum()) - (sheetReferenceDocuments.getFirstRowNum());	
			if(rowCount != 0)
				rowCount1 = rowCount;
			int columnCount1 = 0;
			int mlRefDocDetailsSize=mlRefDocDetails.size();
			int hmFinalSize=0;
			String[] strSplittedValue;
			for (int i=0;i<mlRefDocDetailsSize;i++){
				//Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
				iRowCountAll = iRowCountAll + 1;
				//Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
				XSSFRow row = sheetReferenceDocuments.createRow(++rowCount1);
				columnCount1 = 0;
				mpRefDoc = (Map)mlRefDocDetails.get(i);
				//Added for 2018x.6 JUN CW Requirement 42873 - Starts
				DomainObject domReferenceDocument = DomainObject.newInstance(context, (String)mpRefDoc.get(DomainConstants.SELECT_ID));
				isIRMDocumentType = domReferenceDocument.isKindOf(context, pgV3Constants.TYPE_PG_IRM_DOCUMENT);
				//Added for 2018x.6 JUN CW Requirement 42873 - Ends
				
				hmFinal= new HashMap<>();				
				 htCommonCol= (Map)mpRefDoc.get(STRFINALCOMMONCOLUMNS);	
				//Added code for Req Id : 34324 Hyperlinks--Starts
				hmFinal.put(0, HYPERLINK+htCommonCol.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+htCommonCol.get(DomainConstants.SELECT_ID));
				//Added code for Req Id : 34324 Hyperlinks--Ends
				strTypeName = (String)htCommonCol.get(DomainConstants.SELECT_TYPE);
				strTypeDisplayName=i18nNow.getTypeI18NString(strTypeName, sLanguage);
				hmFinal.put(1,strTypeDisplayName);				
				hmFinal.put(2, htCommonCol.get(DomainConstants.SELECT_REVISION));
				hmFinal.put(3, htCommonCol.get(INPUTPARTCURRENT));
				hmFinal.put(4, htCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
				hmFinal.put(5, htCommonCol.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));				
				hmFinal.put(6, htCommonCol.get(strPrimaryOrganizationSelect));
				//updated for Req Id 36710 The report shall include Description attribute for input and related Part--starts
				hmFinal.put(7, htCommonCol.get(DomainConstants.SELECT_DESCRIPTION));
				//updated for Req Id 36710 The report shall include Description attribute for input and related Part--ends
				//Added code for Req Id : 34324 Hyperlinks--Starts
				//Added for Req 47051 : 18x6 Apr CW -- Starts
				if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && "SpecReader".equalsIgnoreCase(strOriginatingSource)){
					//Added for 2018x.6 JUN CW Requirement 42873 - starts
					
					if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase((String)mpRefDoc.get(REFERENCEDOCCURRENT)) && isIRMDocumentType) {
						hmFinal.put(8, HYPERLINK+mpRefDoc.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+mpRefDoc.get(DomainConstants.SELECT_ID));
					} else {
						hmFinal.put(8, mpRefDoc.get(DomainConstants.SELECT_NAME));
					}
					//Added for 2018x.6 JUN CW Requirement 42873 - Ends
					
					
				} else {
					hmFinal.put(8, HYPERLINK+mpRefDoc.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+mpRefDoc.get(DomainConstants.SELECT_ID));
				}
				//Added for Req 47051 : 18x6 Apr CW -- Ends
				//Added code for Req Id : 34324 Hyperlinks--Ends
				hmFinal.put(9, mpRefDoc.get(DomainConstants.SELECT_REVISION));
				hmFinal.put(10, mpRefDoc.get(REFERENCEDOCCURRENT));
				hmFinal.put(11, mpRefDoc.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));			
				hmFinal.put(12, mpRefDoc.get(SUBTYPE));
				strTypeName = (String)mpRefDoc.get(DomainConstants.SELECT_TYPE);
				strTypeDisplayName=i18nNow.getTypeI18NString(strTypeName, sLanguage);
				hmFinal.put(13,strTypeDisplayName);
				hmFinal.put(14, mpRefDoc.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
				//updated for Req Id 36710 The report shall include Description attribute for input and related Part--starts
				hmFinal.put(15, mpRefDoc.get(DomainConstants.SELECT_DESCRIPTION));
				//updated for Req Id 36710 The report shall include Description attribute for input and related Part--ends
				//For Cells Creation in each Row		
				hmFinalSize=hmFinal.size();
				for (int j = 0; j < hmFinalSize; j++) {
					Cell cell = row.createCell(columnCount1++);
					if(hmFinal.get(j) instanceof StringList)
					{
						strCellValue = (hmFinal.get(j).toString().replace("[", "").replace("]", ""));
					}
					else
					{
						strCellValue = (String) hmFinal.get(j);
					}
			
					//Added code for Req Id : 34324 Hyperlinks--Starts
					if (UIUtil.isNotNullAndNotEmpty(strCellValue)
							&& strCellValue.startsWith(HYPERLINK_COMPARE)) {
						 strSplittedValue = strCellValue.split("\\|",-1);
						 strHyperlinkId = strSplittedValue[(strSplittedValue.length) - 1];
						 strValue = strSplittedValue[(strSplittedValue.length) - 2];
						 //Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
						 if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
						 //Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
							 getHyperlink(context, cell, workbook, strValue,
								strHyperlinkId);
						 //Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
						 } else if(UIUtil.isNotNullAndNotEmpty(strValue)) {
							 cell.setCellValue(strValue);
						 }
						//Added code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
					}	//Added code for Req Id : 34324 Hyperlinks-ends
					else {
						
						cell.setCellValue(strCellValue);
					}
				}
			}
		} catch (Exception e) {			
			outLog.print("Exception in  updateWorksheetReferenceDocuments: "+e+"\n");
			outLog.flush();	
		}
	}

	/**
     * this method is to create Excel Sheet
     * @param context is the matrix context
     * @param args has the required information
     * @return void
	 * @throws IOException 
     */
   private void createExcelWorkbook(Context context,MapList mlRefDocDetailsAll,String strReportFileName, String strReportObjectId,String strUserName,Map<String,String> mHyperLinkDetails) throws IOException {
	   FileOutputStream outputStream=null;
	   File sbReferenceDocumentFolder = null;
	   try {
		   //Added code for 2018x.6 requirement id 36708 Generate Reference Document report directly Ends
			String strHyperlink=mHyperLinkDetails.get(HYPERLINKASINPUT);
			String strRealTimeProcess=mHyperLinkDetails.get(REALTIMEPROCESS);
		    String strReportPath = DomainConstants.EMPTY_STRING;
		    if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
		    	strReportPath = EnoviaResourceBundle.getProperty(context, EMXCPN,context.getLocale(),"emxCPN.RefDocReport.Worksheet.FilePath");
		    else
		    	strReportPath = EnoviaResourceBundle.getProperty(context, EMXCPN,context.getLocale(),"emxCPN.RefDocReportCTRLMJob.Worksheet.FilePath");
		    //Added code for 2018x.6 requirement id 36708 Generate Reference Document report directly Ends
		    String strReportName = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE,context.getLocale(),"emxCPN.ReferenceDocumentReport.ReportName");
			
			String strReportExtension = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE,context.getLocale(),"emxCPN.ReferenceDocumentReport.ReportExtension");
			String strSheetRefDoc = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE,context.getLocale(),"emxCPN.ReferenceDocumentReport.Worksheet.Name");
			StringBuffer sbReportName = new StringBuffer();
			XSSFWorkbook workbook = new XSSFWorkbook();	
			
			XSSFSheet sheetTablesRefDoc = workbook.createSheet(strSheetRefDoc);	
			XSSFRow rowHeaderTablesATS = sheetTablesRefDoc.createRow(0);
			XSSFCellStyle cellStyleRefDoc = sheetTablesRefDoc.getWorkbook().createCellStyle();		
			XSSFFont fontRelatedATS = sheetTablesRefDoc.getWorkbook().createFont();
			XSSFCellStyle cellStyle = sheetTablesRefDoc.getWorkbook().createCellStyle();
			XSSFFont xsfont = sheetTablesRefDoc.getWorkbook().createFont();
			xsfont.setBold(true);
			xsfont.setFontHeightInPoints((short) 12);
			cellStyle.setFont(fontRelatedATS);
			cellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());			
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
			fontRelatedATS.setBold(true);
			fontRelatedATS.setFontHeightInPoints((short) 12);
			cellStyleRefDoc.setFont(fontRelatedATS);
			cellStyleRefDoc.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());			
			cellStyleRefDoc.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			//Updated code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Starts
			Map<String,Object> mCellStyle = new HashMap<>();
			mCellStyle.put("CellStyle", cellStyle);
			mCellStyle.put("CellStyleRefDoc", cellStyleRefDoc);
			updateWorksheetReferenceDocuments(context,workbook,rowHeaderTablesATS,mlRefDocDetailsAll,sheetTablesRefDoc,mCellStyle,strHyperlink);
			//Updated code for 2018x.6 Requirement id 36707 Ability to generate Reference Document report with-without hyperlink Ends
	
			Date currentDate = new Date();
		    SimpleDateFormat smpdf = new SimpleDateFormat("MM-dd-yyyy");
		    String strReportExtractedDate = smpdf.format(currentDate);	
		    String strUserNameTemp = strUserName.replace(".","-");
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format :Start
			if(UIUtil.isNotNullAndNotEmpty(strReportFileName)) {
				strReportName =sbReportName.append(strReportFileName).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strReportExtractedDate).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strUserNameTemp).append(pgV3Constants.SYMBOL_UNDERSCORE).append(System.currentTimeMillis()).append(pgV3Constants.SYMBOL_DOT).append(strReportExtension).toString();
			} else {
				strReportName =sbReportName.append(strReportName).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strReportExtractedDate).append(pgV3Constants.SYMBOL_UNDERSCORE).append(strUserNameTemp).append(pgV3Constants.SYMBOL_UNDERSCORE).append(System.currentTimeMillis()).append(pgV3Constants.SYMBOL_DOT).append(strReportExtension).toString();
			}
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format : End
			sbReferenceDocumentFolder = new File(strReportPath);
			if (!sbReferenceDocumentFolder.exists() )  {
				sbReferenceDocumentFolder.mkdirs();
			}
			outputStream = new FileOutputStream(sbReferenceDocumentFolder.toString()+ File.separator + strReportName);
			workbook.write(outputStream);
			// code to create the object and checking the .excel file in that object
			createReferenceDocumentReportObject(context,strUserName,sbReferenceDocumentFolder.toString(),strReportName,strReportObjectId);

	
			mlRefDocDetailsAll.clear();
		} 
		catch (Exception e) {
			e.printStackTrace();
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
	
	
	/**
     * this method is to Checkin Excel Sheet in to a DSM Report Object
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     * @throws Exception
     */
	private void createReferenceDocumentReportObject(Context context,String strUserName,String strReportPath,String strReportName,String strReportObjectId) throws MatrixException {
		boolean isContextPushed = false;
		try {
			
			String typePgDSMReport = PropertyUtil.getSchemaProperty(context,"type_pgDSMReport");
			String policyPgDSMReport = PropertyUtil.getSchemaProperty(context,"policy_pgDSMReport");
			String  strFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
			String strObjectName= EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ReferenceDocumentReport.ObjectName");
			String strContextUserName = context.getUser();
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
					ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					isContextPushed = true;
					outLog.print("Before Checkin Report: "+strUserName+"| "+strReportName+"| " +smpdateformat.format(currentDate)+ "\n");
					outLog.flush();		
					DomainObject doObj = DomainObject.newInstance(context, strObjectId);
				    doObj.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, strFormat, strReportName, strReportPath);
			        doObj.promote(context);			      
			        String sFullPath = strReportPath.concat("/").concat(strReportName);			       
			        File file = new File(sFullPath);
			        Files.delete(file.toPath());
			        outLog.print("After Checkin Report: "+strUserName+"| "+strReportName+"| " +smpdateformat.format(currentDate)+ "\n");
			        outLog.flush();	
			      //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
					sendEmail(context, strReportName, strUserName);
					//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
			      	
			} else {
			 	throw new MatrixException(ERR_MESSAGE);
			}
		
		} catch (Exception e) {
			outLog.print("Exception in  createReferenceDocumentReportObject and Checkin File: "+e+"\n");
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
	private boolean accessCheck(Context context, String strUserName,
		String strPartId) throws FrameworkException {
		boolean isContextPushed = false;
		boolean bAccess = false;
		String strValue = DomainConstants.EMPTY_STRING;
		boolean bDataExists = false;
		boolean bIsEBPUser = false;
		bDataExists = isdataExists(context, strUserName, strPartId);
		if (bDataExists) {
			bIsEBPUser = isEBP(context, strUserName);
			if (bIsEBPUser) {
				bAccess = accessCheckEBP(context, strUserName, strPartId);
			} else {
				try {
					DomainObject domainObject = DomainObject.newInstance(context);
					domainObject.setId(strPartId);
					ContextUtil.pushContext(context, strUserName, null, context.getVault().getName());
					isContextPushed = true;
					strValue = domainObject.getInfo(context,DomainConstants.SELECT_CURRENT);
					if (UIUtil.isNotNullAndNotEmpty(strValue)&& !(DENIED).equals(strValue)) {
						bAccess = true;
					} else {
						bAccess = false;
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (isContextPushed)
						ContextUtil.popContext(context);
				}
			}
		} else {
			bAccess = false;
		}
		return bAccess;
	}
	
/**
 * @param context
 * @param strUserName
 * @param strPartId
 * @return boolean
 * @throws FrameworkException
 */
private boolean isdataExists(Context context, String strUserName, String strPartId) throws FrameworkException
{
	boolean bDataExists = false; 
	
    StringList slBusSelect = new StringList(DomainConstants.SELECT_TYPE);
    slBusSelect.add(DomainConstants.SELECT_NAME);
    slBusSelect.add(DomainConstants.SELECT_REVISION);
    ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
    DomainObject objPart = DomainObject.newInstance(context, strPartId);
    Map<String,Object> mpPartDetails = objPart.getInfo(context, slBusSelect);
    ContextUtil.popContext(context);
    String strPartType = (String)mpPartDetails.get(DomainConstants.SELECT_TYPE);
    String strPartName = (String)mpPartDetails.get(DomainConstants.SELECT_NAME);
    String strPartRev = (String)mpPartDetails.get(DomainConstants.SELECT_REVISION);
    ContextUtil.pushContext(context,strUserName,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
    MapList    mlPartAccess = objPart.findObjects(context, //context
            strPartType, //type
            strPartName, //name
            strPartRev, //rev
            null, //owner
            pgV3Constants.VAULT_ESERVICEPRODUCTION, //vault
            null, //where clause
            false, //expand type
            null); //obj select
   
    ContextUtil.popContext(context);
    
    int mlPartAccessSize=mlPartAccess.size();
    if(mlPartAccess.size()>0)
    {
    	bDataExists=true;
    }
    else
    {
    	bDataExists=false;
    }
    return bDataExists;
   
	}
	
	/**
	 * Description: This method is used to check ebp access as per DSM Security Model.
	 * Fetching ART attribute[pgIPClassification] and Person attribute[pgSecurityEmployeeType].
	 * Performing access checks on basis of ART Special Project Security Group.
	 * Returns: True/False
	 * @throws FrameworkException 
	 */
	private boolean accessCheckEBP(Context context, String strUserName, String strPartId) throws FrameworkException {
		boolean bHasaccess = false;
		boolean bHasaccessWithOutComment = false;
		boolean bHasaccessForCoProject = false;
		boolean bHasaccessForCoProjectWithoutComment = false;
		boolean bHasaccessUserProject = false;
		boolean bHasaccessUserProjectWithoutComment = false;
		boolean bAccessToPart = false;
		StringList slObjSelect = new StringList(1);
		slObjSelect.add(DomainConstants.SELECT_NAME);
		StringList slRelSelect = new StringList();
		String strValue = DomainConstants.EMPTY_STRING;
		String strUserOid = DomainConstants.EMPTY_STRING;
		short sQueryLimit = 5;
		Short sRecursionLevel = 1;
		StringList slSelect = new StringList();
		slSelect.add(DomainConstants.SELECT_ID);
		Map<String,String> mpPersonData=new HashMap<>();
		Map<String,String> mpUserVendor=new HashMap<>();
		String strUserVendor=DomainConstants.EMPTY_STRING;
		try {
			ContextUtil.pushContext(context,//context
					PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),//user name
					DomainConstants.EMPTY_STRING,//password
					DomainConstants.EMPTY_STRING);//vault
			
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
			ContextUtil.popContext(context);
			if (!bAccessToPart) {
				// For parts where access is inherited
				DomainObject domainObject = DomainObject.newInstance(context);
				domainObject.setId(strPartId);
				ContextUtil.pushContext(context, strUserName, null, context
						.getVault().getName());
				
					strValue = domainObject.getInfo(context, DomainConstants.SELECT_CURRENT);
					if(UIUtil.isNotNullAndNotEmpty(strValue) && !(DENIED).equals(strValue)){
						bAccessToPart = true;
					} else {
						bAccessToPart = false;
					}
					ContextUtil.popContext(context);
			}
		} catch (Exception e) {
			bAccessToPart = false;
			e.printStackTrace();
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
		 private void getHyperlink(Context context, Cell cell, XSSFWorkbook workbook, String strValue, String strId) throws FrameworkException {
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
			XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(HyperlinkType.URL);
			if(UIUtil.isNotNullAndNotEmpty(strId) && UIUtil.isNotNullAndNotEmpty(strValue)){
				if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && "ENOVIA".equalsIgnoreCase(strOriginatingSource)) {
					strURL = EnoviaResourceBundle.getProperty(context,EMXCPN, context.getLocale(), "emxCPN.BaseURL");
					strNewURL = String.valueOf(strURL).trim();
					link.setAddress(strNewURL + "?objectId=" + strId);
				} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && "SpecReader".equalsIgnoreCase(strOriginatingSource)) {
					strURL = getSpecReaderURL(context);
					strNewURL = String.valueOf(strURL).trim();
					link.setAddress(strNewURL + strValue);
				}
				//Removing the extra setAddress for 2018x.6 APR CW Defect 46915
				
				cell.setCellValue(strValue);
				cell.setHyperlink( link);
				cell.setCellStyle(style);
			} else {
				cell.setCellValue(DomainConstants.EMPTY_STRING);
			}
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
			
	
	/**  This method is used to get the connected Reference Document details of an object
	 * @param context
	 * @param dobjPart
	 * @param mPartColumn
	 * @param strUserName
	 * @return MapList
	 * @throws FrameworkException
	 */

	private MapList getReferenceDocDetails(Context context,DomainObject dobjPart,Map<String,Object> mPartColumn,String strUserName)throws FrameworkException
	{	
		String strSubTypeValue=DomainConstants.EMPTY_STRING;
		String strRefDocType=DomainConstants.EMPTY_STRING; 
		String strIsDocumentType=DomainConstants.EMPTY_STRING;
		String strRefDocId=DomainConstants.EMPTY_STRING;
		String strRelatedPartPolicy = DomainConstants.EMPTY_STRING;
		String strInputPolicy = DomainConstants.EMPTY_STRING;
		String strInputTranslatedState = DomainConstants.EMPTY_STRING;
		String strRelatedPartTranslatedState = DomainConstants.EMPTY_STRING;
		String strRelatedPartState = DomainConstants.EMPTY_STRING;
		String strInputState = DomainConstants.EMPTY_STRING;
		MapList documentList = new MapList();
		MapList mlFilteredList = new MapList();
		Map<String,Object> mRefDocDetails = new HashMap<>();
		int idocumentListSize = 0;
		boolean bAccess = false;
		String strLanguage = DomainConstants.EMPTY_STRING;
		try
		{
			String sRelpgDocumentToSubType=PropertyUtil.getSchemaProperty(context, "relationship_pgDocumentToSubType");
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
			objectSelects.add(DomainConstants.SELECT_REVISION);
			objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			objectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			objectSelects.add(STRFROMSTART+sRelpgDocumentToSubType+STRTONAME);
			objectSelects.add("type.kindof["+CommonDocument.TYPE_DOCUMENTS+"]");
			objectSelects.add(DomainConstants.SELECT_POLICY);
			//added for Req Id 36710 The report shall include Description attribute for input and related Part--starts
			objectSelects.add(DomainConstants.SELECT_DESCRIPTION);
			//Added for Req Id 36710 The report shall include Description attribute for input and related Part--ends
			String objectWhere = "revision!="+REVISION_RENDITION+"";
		String relPattern = DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT;
		
	//	ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
		documentList = dobjPart.getRelatedObjects(context, 
				relPattern, //relationship pattern
				DomainConstants.QUERY_WILDCARD, // object pattern
				objectSelects, // object selects
				null, // relationship selects
				false,   // to direction
				true,  // from direction
				(short)1,  // recursion level
				objectWhere,// object where clause
				null,//relationshipWhere
				0);//limit
		
		Map<String,Object> mpRefDoc = null;
		//Req Id: 34323:Report shall have the following fields displayed for the corresponding Reference Documents that are attached to the objects that were inputted into the Report--starts
			if(!documentList.isEmpty()){
				idocumentListSize=documentList.size();
    			for(int i=0; i<idocumentListSize;i++){
    				
    				 mpRefDoc = (Map)documentList.get(i);
    				 
    				 strRefDocType=(String)(mpRefDoc.get(DomainConstants.SELECT_TYPE));
    				 strIsDocumentType=(String)mpRefDoc.get("type.kindof["+CommonDocument.TYPE_DOCUMENTS+"]");
    				 if (pgV3Constants.TRUE.equalsIgnoreCase(strIsDocumentType)) {
    					
    					
    					 strRefDocId=(String)(mpRefDoc.get(DomainConstants.SELECT_ID));
    					 strLanguage=context.getLocale().getLanguage();
         				
         				strInputState=(String)mPartColumn.get(DomainConstants.SELECT_CURRENT);
         				strInputPolicy = (String) mPartColumn.get(DomainConstants.SELECT_POLICY);
         				strInputTranslatedState = i18nNow.getStateI18NString(strInputPolicy, strInputState, strLanguage);
         				mPartColumn.put(INPUTPARTCURRENT, strInputTranslatedState);
         				
         				mRefDocDetails = new HashMap<>();
         				mRefDocDetails.put(DomainConstants.SELECT_ID,strRefDocId);
	     				mRefDocDetails.put(DomainConstants.SELECT_NAME, mpRefDoc.get(DomainConstants.SELECT_NAME));
	     				mRefDocDetails.put(DomainConstants.SELECT_TYPE, strRefDocType);
	     				mRefDocDetails.put(DomainConstants.SELECT_REVISION, mpRefDoc.get(DomainConstants.SELECT_REVISION));
	     				mRefDocDetails.put(STRFINALCOMMONCOLUMNS, mPartColumn);
   					
    					
    				//Req Id: 34560: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-Starts
    					 bAccess=accessCheck(context, strUserName, strRefDocId);
    				//Req Id: 34560: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-ends
    				if (bAccess){
    					
    					//updated for Defect 34024 Subtypes are not being pick up by Reference Document report Starts
    					if(pgV3Constants.TYPE_TESTMETHOD.equals(strRefDocType))
    					{
    						strSubTypeValue=getSpecificationSubtype(mpRefDoc);	
    					}
    					else
    					{
    						strSubTypeValue=(String)mpRefDoc.get(STRFROMSTART+sRelpgDocumentToSubType+STRTONAME);
    						
    					}
    					//updated for Defect 34024 Subtypes are not being pick up by Reference Document report Ends
    					
    					strRelatedPartState=(String)mpRefDoc.get(DomainConstants.SELECT_CURRENT);
    					strRelatedPartPolicy = (String) mpRefDoc.get(DomainConstants.SELECT_POLICY);
    					strRelatedPartTranslatedState = i18nNow.getStateI18NString(strRelatedPartPolicy, strRelatedPartState, strLanguage);
    					
    					mRefDocDetails.put(DomainConstants.SELECT_ATTRIBUTE_TITLE, mpRefDoc.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
        				mRefDocDetails.put(REFERENCEDOCCURRENT, strRelatedPartTranslatedState);
        				mRefDocDetails.put(SUBTYPE,strSubTypeValue);
        				mRefDocDetails.put(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE, mpRefDoc.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
        				//added for Req Id 36710 The report shall include Description attribute for input and related Part--starts
        				mRefDocDetails.put(DomainConstants.SELECT_DESCRIPTION, mpRefDoc.get(DomainConstants.SELECT_DESCRIPTION));
        				//Added for Req Id 36710 The report shall include Description attribute for input and related Part--ends
    				
    				}//Req Id: 34560: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-Starts
    				else
    				{
    					mRefDocDetails.put(DomainConstants.SELECT_ATTRIBUTE_TITLE,pgV3Constants.NO_ACCESS);
        				mRefDocDetails.put(REFERENCEDOCCURRENT, pgV3Constants.NO_ACCESS);
        				mRefDocDetails.put(SUBTYPE,pgV3Constants.NO_ACCESS);
        				mRefDocDetails.put(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE,pgV3Constants.NO_ACCESS);
        				//added for Req Id 36710 The report shall include Description attribute for input and related Part--starts
        				mRefDocDetails.put(DomainConstants.SELECT_DESCRIPTION, pgV3Constants.NO_ACCESS);
        				//Added for Req Id 36710 The report shall include Description attribute for input and related Part--ends
    				}
    				//Req Id: 34560: if a user doesnot have access to the Objects displayed, the report shall display "No Access" in the table-ends
    				
    				mlFilteredList.add(mRefDocDetails);
    				
    				 }
    				
    			}
			}
		
			} catch (Exception e){
				outLog.print("Exception in  getReferenceDocDetails: "+e+"\n");
				outLog.flush();
			}
			return mlFilteredList;
			//Req Id: 34323:Report shall have the following fields displayed for the corresponding Reference Documents that are attached to the objects that were inputted into the Report--starts	
			}
	
	/** getSpecificationSubtype This method is to get Specification Subtype
	 * @param mpRefDoc
	 * @return String
	 */
	private String getSpecificationSubtype(Map<String, Object> mpRefDoc) {
		String strSubType = DomainConstants.EMPTY_STRING;
		try {
				String sSpecSubType = (String) mpRefDoc.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
				if (UIUtil.isNotNullAndNotEmpty(sSpecSubType)) {
					strSubType = sSpecSubType;
				}
		} catch (Exception e) {
			e.printStackTrace();
			outLog.print("Exception in  getSpecificationSubtype: "+e+"\n");
			outLog.flush();	
		}
		return strSubType;
	}

	/** getCommonColumnsDetail Map for Common  Columns 
	 * @param context
	 * @param dobjRelatedPart
	 * @return Map
	 */
	private Map<String,Object> getCommonColumnsDetail(Context context, DomainObject dobjRelatedPart) {
		Map<String, Object> mTSDetail = null;
		try {
			mTSDetail = dobjRelatedPart.getInfo(context, SLOBJECTCOMMONINFOSELECT);
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
	 * @throws Exception
	 */
	public void generateReferenceDocumentReportForInputFile(Context context,  String[] args) throws Exception {
				
		String strReportFileName=DomainConstants.EMPTY_STRING;
		String strReportObjectId=DomainConstants.EMPTY_STRING;
		String strUserName=DomainConstants.EMPTY_STRING;
		String strLatestVersionAsInput=DomainConstants.EMPTY_STRING;
		String line = DomainConstants.EMPTY_STRING;
		
		HashMap<String,Object> hmArgs = (HashMap) JPO.unpackArgs(args);
		File file = (File) hmArgs.get("DSMReportFile");
		StringBuilder finalVal =new StringBuilder();
		
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
			}//code modified for defect 34021 and 34023
			String strPartLibName= finalVal.toString();
			if(UIUtil.isNotNullAndNotEmpty(strUserName) && UIUtil.isNotNullAndNotEmpty(strPartLibName)){
				
				HashMap<String,String> hmArgs1 = new HashMap<>();
				hmArgs1.put(USERNAME,strUserName);
				hmArgs1.put("GCAS",strPartLibName);
				hmArgs1.put(REPORTFILENAME,strReportFileName);
				hmArgs1.put(REPORTOBJECTID,strReportObjectId);
				hmArgs1.put(LATESTRELEASEPART,strLatestVersionAsInput);
				BackgroundProcess backgroundProcess = new BackgroundProcess();
				backgroundProcess.submitJob(context, "com.pg.dsm.custom.pgReferenceDocumentReports", "generateReferenceDocumentReport", JPO.packArgsRemote(hmArgs1) , (String)null);
			}
		}
		catch (Exception e) {
			ContextUtil.abortTransaction(context);
			outLog.print("Exception in  generateReferenceDocumentReportForInputFile: "+e+"\n");
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
	
	
	//Added for requirement : 37382 - starts
	/*
	 * getHighestRevForAllTypes- This method is to get process the Parts to get Highest Revision of all types
	 * @returns MapList
	 */
	private MapList getHighestRevForAllTypes(MapList mlAllParts){
		MapList mlReturnParts = new MapList();
		MapList mlMoreThanOneType = new MapList();
		StringList slTypes = new StringList();
		try {
			for(int i=0; i<mlAllParts.size();i++){
				Map<String, String> mEachPart = (Map<String, String>)mlAllParts.get(i);
				String strPartType = mEachPart.get(DomainConstants.SELECT_TYPE);
				if(!slTypes.contains(strPartType)){
					slTypes.add(strPartType);
					Map<String,String> mHighestRev = getLatestRev(mlMoreThanOneType);
					if(null != mHighestRev && !mHighestRev.isEmpty()){
						mlReturnParts.add(mHighestRev);
						mlMoreThanOneType.clear();
					} 
					mlMoreThanOneType.add(mEachPart);
				} else{
					mlMoreThanOneType.add(mEachPart);
				}
			}
			Map<String,String> mHighestRev = getLatestRev(mlMoreThanOneType);
			if(null != mHighestRev && !mHighestRev.isEmpty()){
				mlReturnParts.add(mHighestRev);
			}
		}catch(Exception e){
		outLog.print("Exception in  getHighestRevForAllTypes "+e+"\n");
		outLog.flush();
		}
		return mlReturnParts;
	}
	
	
	/*
	 * getLatestRev- This method is to get Latest Rev of  Parts
	 * @returns Map
	 */
	private Map<String, String> getLatestRev(MapList mlMoreThanOneType){
		Map<String, String> mHighestRev = new HashMap<>();
		if(null != mlMoreThanOneType && !mlMoreThanOneType.isEmpty()){
			mlMoreThanOneType.sort(DomainConstants.SELECT_REVISION, DESCENDING, STRING);
			for(int j=0; j<mlMoreThanOneType.size();j++){
				if(j==0){
					mHighestRev = (Map) mlMoreThanOneType.get(j);
				}
			}
		} 
		return mHighestRev;	
	}
	//Added for requirement : 37382 - ends
	
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
				if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && ENOVIA.equalsIgnoreCase(strOriginatingSource)) {
				strUrl = EnoviaResourceBundle.getProperty(context,EMXCPN, context.getLocale(), "emxCPN.BaseURL");
				strUrl = strUrl.substring(0, strUrl.indexOf(PATH_SEPARATOR+STR_COMMON));
				strUrl = String.valueOf(strUrl).trim();
				strUrlPath = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.Enovia.DSMReportLink");
				sbUrl.append(strUrl);
				sbUrl.append(strUrlPath);
			} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
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



