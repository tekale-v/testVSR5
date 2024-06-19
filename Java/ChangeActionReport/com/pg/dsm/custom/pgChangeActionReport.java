/*  pgChangeActionReport.java  Report Request For Change Action Report Download written for requirement 37250,37724,37726 
     Author:DSM Report Team
     Copyright (c) 2019
     All Rights Reserved.
 */
package com.pg.dsm.custom;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.Properties;
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
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;

public class pgChangeActionReport implements DomainConstants {
	
	
	public pgChangeActionReport(Context context, String[] args) {
		
	}
    private static final String HYPERLINK = "Hyperlink:|";
	private static final String HYPERLINK_COMPARE = "Hyperlink:";
	private static final String USERNAME="UserName";
	private static final String REPORTFILENAME="ReportFileName";
	private static final String REPORTOBJECTID="ReportObjectId";
	private static final String STRFINALCOMMONCOLUMNS = "CommonColumns";
	private static final String CPNSTRINGRESOURCE = "emxCPNStringResource";
	private static final String ERR_MESSAGE="Creation of Object Failed";
	private static final String CPNRESOURCEFILE ="emxCPN";
	private static final String DATE_YMDHS = "yyyy-MM-dd HH:mm:ss";
	private static final String DIRECTORY_ERR = "Could not create directory";
	private static final String CATALINA_BASE="catalina.base";
	private static final String ATTRIBUTE_SELECT="attribute[";
	private static final String BRACKET_START="(";
	private static final String BRACKET_END=")";
	private PrintWriter outLog = null;
	//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Starts
	private static final String HYPERLINKASINPUT = "HyperlinkAsInput";
	//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Ends
	//Added code for 2018x.6 Requirement id 37976 Generate Change Action Report directly Starts
	private static final String REALTIMEPROCESS = "RealTimeProcess";
	//Added code for 2018x.6 Requirement id 37976 Generate Change Action Report directly Ends
	//Added for Defect -38739 - starts
	private static final String ATTRIBUTE_SYNOPSIS = PropertyUtil.getSchemaProperty(null,"attribute_Synopsis");
	//Added for Defect -38739 - ends
	//Added code for Requirement: 46219--Starts
	private static final String ATTRIBUTE_ROUTEACTION = PropertyUtil.getSchemaProperty(null,"attribute_RouteAction");
	private static final String SELECT_ATTRIBUTE_ROUTEACTION = "attribute[" + ATTRIBUTE_ROUTEACTION  + "]";
	
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	private static final String STRFROMEMAILID = "strFromEmailId";
	private static final String STRTOEMAILID = "strToEmailId";
	private static final String STRSUBJECT = "strSubject";
	private static final String STRMESSAGEBODY = "strMessageBody";
	private static final String STR_PERSON_PLM_ADMIN = "PLM Admin";
	private static final String STR_COMMON="common";
	private static final String STR_SYMBOL_BRACKETS="{0}";
	private static final String STR_TEXT_HTML="text/html";
	private static final String PATH_SEPARATOR="/";
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	
	//Added code for Requirement: 46219--Ends
	/** 
     * This method is to generate CA Report for input submitted by user
     * @param args
     * @return void
     * @throws Exception
     */  
	public void generateChangeActionReport(Context context, String[] args) throws Exception {  
		StringBuilder sbLogFolder = new StringBuilder();
		File fLogFolder = null;
		String strStartTime = null; 
		String strJVM = getJVMInstance();
	 	HashMap<String, String> hmArgs = (HashMap) JPO.unpackArgs(args);
	    String strUserName =hmArgs.get(USERNAME);
	    String strChangeAction = hmArgs.get("ChangeAction");	    
	    String strOriginator = hmArgs.get("Originator");	    
	    String strReportFileName =hmArgs.get(REPORTFILENAME);
	    String strReportObjectId =hmArgs.get(REPORTOBJECTID);	  
	    String strSelectedState =hmArgs.get("SelectedState");
	    //Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Starts
	    String strHyperlink = hmArgs.get(HYPERLINKASINPUT);
	    //Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Ends
	    //Added code for 2018x.6 Requirement id 37976 Generate Change Action Report directly Starts
	    String strRealTimeProcess = hmArgs.get(REALTIMEPROCESS);
	    //Added code for 2018x.6 Requirement id 37976 Generate Change Action Report directly Ends
	    Map<String,String> mPassValue = new HashMap<>();
	    mPassValue.put(REALTIMEPROCESS,strRealTimeProcess);
	    mPassValue.put(HYPERLINKASINPUT,strHyperlink);
	    mPassValue.put("SelectedState",strSelectedState);
	    String configLOGFilePath = DomainConstants.EMPTY_STRING;
	    if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
	    	configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Log.FilePath");
	    else
	    	configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.DSMReportCTRLMJob.Log.FilePath");
	    sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
	    fLogFolder = new File(sbLogFolder.toString());
	    if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
	    	throw new IOException(DIRECTORY_ERR + fLogFolder);
	    }
	    outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"CAReportLog.log",true));
	    strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());	    
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strChangeAction) || UIUtil.isNotNullAndNotEmpty(strOriginator))){
	    	DomainObject doObj  =DomainObject.newInstance(context, strReportObjectId);
	 	    outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+" | JVM: "+strJVM+"-------\n");
	 	    outLog.print("ChangeAction: "+strChangeAction+"\n");
	 	    outLog.print("Report Object Id: "+strReportObjectId+"\n");
	 	    outLog.flush();
	    	doObj.promote(context);
	    	getChangeActionReportProcess(context,strUserName,strChangeAction,strReportFileName,strReportObjectId,strOriginator,mPassValue);
	    } else {
	    	outLog.print("-------Report requested by: " +strUserName+" : "+strStartTime+"--------\n");
			outLog.print("Report cannot be generated. Check Report Object Id for request details: "+strReportObjectId+"\n");
			outLog.flush(); 
	    }
	}

	/** This method fetches all the valid CA and originator from input and processes them.
	 * @returns void
	 */
	private void getChangeActionReportProcess(Context context,String strUserName,String strChangeAction,String strReportFileName, String strReportObjectId,String strOriginator,Map<String,String> mPassValue){
		MapList mlTaskList = new MapList();
		StringList slConnectedChangeFromOriginator = new StringList();
		StringList slInputChangeActionId = new StringList();
		StringList slAppendedChangeActionId = new StringList();
		Map<String,Object> mCommonDetail = new HashMap<>();
		Map<String,Object> mCADetail = new HashMap<>();
		String sId = DomainConstants.EMPTY_STRING;
		String sProposedRealizedCount = DomainConstants.EMPTY_STRING;
		int iProposedRealizedCount = 0; 
		boolean isContextPushed = false;
		try {
			//create Excel workbook	
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheetCA = createSheet(context, workbook); 
			String strSelectedState = mPassValue.get("SelectedState");
			String strHyperlink = mPassValue.get(HYPERLINKASINPUT);
			String strRealTimeProcess = mPassValue.get(REALTIMEPROCESS);
			if(UIUtil.isNotNullAndNotEmpty(strOriginator)){
				slConnectedChangeFromOriginator = getConnectedChangeAction(context,strOriginator,strSelectedState);
			} 
			if(UIUtil.isNotNullAndNotEmpty(strChangeAction)){
				slInputChangeActionId = getChangeActionList(context,strChangeAction);
			}
			if(null != slConnectedChangeFromOriginator && !slConnectedChangeFromOriginator.isEmpty()){
				slAppendedChangeActionId = appendIds(slConnectedChangeFromOriginator,slAppendedChangeActionId);
			}
			if(null != slInputChangeActionId && !slInputChangeActionId.isEmpty()){
				slAppendedChangeActionId = appendIds(slInputChangeActionId,slAppendedChangeActionId);
			}
			for(int i = 0;i<slAppendedChangeActionId.size();i++){
				sId = slAppendedChangeActionId.get(i);
				outLog.print("Expand start for: "+strUserName+": "+sId+ "\n");
				outLog.flush();	
				if(UIUtil.isNotNullAndNotEmpty(sId)){
					//Added for Defect - 38773- starts
					//As per the requirement, report should also contain Change Action for which user doesn't have access.Hence, push context is used.
					ContextUtil.pushContext(context,pgV3Constants.PERSON_USER_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					isContextPushed = true;
					//Added for Defect - 38773- ends
					DomainObject dobjCA = DomainObject.newInstance(context, sId);
					mCommonDetail = getCommonColumnsDetail(context,dobjCA);
					iProposedRealizedCount = getProposedRealizedActivitiesFromCA(context,sId);
					sProposedRealizedCount = String.valueOf(iProposedRealizedCount);
					mCommonDetail.put("ProposedRealizedCount",sProposedRealizedCount);	
					mlTaskList = getConnectedTaskDetail(context,dobjCA,mCommonDetail);
					if(mlTaskList.isEmpty()){
						mCADetail.put(STRFINALCOMMONCOLUMNS, mCommonDetail);
						mlTaskList.add(mCADetail);
					}
					//Added for Defect - 38773- starts
					if(isContextPushed) {
						ContextUtil.popContext(context);
					}
					//Added for Defect - 38773- ends
					updateWorksheetCAReport(context,workbook,mlTaskList,sheetCA,strHyperlink);
				}
				outLog.print("Expand end for: "+strUserName+": "+sId+"\n");
				outLog.flush();
			}
			Map<String,String> mpReportPathDetails = getReportName(context, strReportFileName, strUserName,strRealTimeProcess);
			String strFullPath=mpReportPathDetails.get("FullPath");
			String strReportPath=mpReportPathDetails.get("ReportPath");
			String strReportName=mpReportPathDetails.get("ReportName");
			checkinExcelWorkbook(context,workbook,strFullPath,strReportPath,strReportName,strReportObjectId,strUserName);	
			String strEndTime = null; 
			strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
			outLog.print("Report completed for: "+strUserName+": "+strChangeAction+"|" +strReportFileName+ "-------\n");
			outLog.print("-------Time completed: "+strEndTime+"------\n");	
			outLog.print("--------\n");
			outLog.flush();
		}catch (Exception e) {
			outLog.print("Exception in  getChangeActionReportProcess: "+strUserName+": "+e+"\n");
			outLog.flush();
		}finally {
			outLog.close();
		}
	}
	
	/*
	 * this method is to create sheet for excel
	 * @param context is the matrix context
	 * @return void
	 */
	private XSSFSheet createSheet(Context context, XSSFWorkbook workbook) {
		String strCAReport = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CAReport.Worksheet.Name.CAReport");
		XSSFSheet sheetTablesCA = workbook.createSheet(strCAReport);	
		XSSFRow rowHeaderTables = sheetTablesCA.createRow(0);
		XSSFCellStyle cellStyleRelatedCA = sheetTablesCA.getWorkbook().createCellStyle();		
		XSSFFont fontCA = sheetTablesCA.getWorkbook().createFont();
		XSSFFont xsfont = sheetTablesCA.getWorkbook().createFont();
		xsfont.setBold(true);
		xsfont.setFontHeightInPoints((short) 12);
		fontCA.setBold(true);
		fontCA.setFontHeightInPoints((short) 12);
		cellStyleRelatedCA.setFont(fontCA);
		cellStyleRelatedCA.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());			
		cellStyleRelatedCA.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		getColumnNames(context, rowHeaderTables, cellStyleRelatedCA);
		return sheetTablesCA;
	}
	
	/*
	 * this method is to get Report name and path
	 * @param context is the matrix context
	 * @return Map
	 */
	private Map<String,String> getReportName(Context context, String strReportFileName, String strUserName, String strRealTimeProcess) {
		String strReportPath = DomainConstants.EMPTY_STRING;
		if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess))
			strReportPath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.CAReport.Worksheet.FilePath");
		else
			strReportPath = EnoviaResourceBundle.getProperty(context, CPNRESOURCEFILE, context.getLocale(),"emxCPN.CAReportCTRLMJob.Worksheet.FilePath");
		String strReportName = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CAReport.ReportName");
		String strReportExtension = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CAReport.ReportExtension");
		StringBuilder sbReportName = new StringBuilder();	
		Map<String,String> mpReturn = new HashMap<>();
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
		File sbCAFolder = new File(strReportPath);
		if (!sbCAFolder.exists())  {
			sbCAFolder.mkdirs();
		}
		mpReturn.put("ReportName",strReportName);
		mpReturn.put("ReportPath",sbCAFolder.toString());
		mpReturn.put("FullPath",sbCAFolder.toString()+ File.separator + strReportName);
		return mpReturn;
	}
	
	/**
	 * this method is to get valid CA names 
	 * @throws FrameworkException
	 */
	private StringList getChangeActionList(Context context,String strChangeAction) throws FrameworkException {	
		String strTrimmedCAName = DomainConstants.EMPTY_STRING;
		String strId = DomainConstants.EMPTY_STRING;
		StringList slId = new StringList();	
		Map<String, Object> mCADetails=new HashMap<>();
		MapList	mlCAObj = new MapList();
		StringBuilder strGCASBuilder = new StringBuilder();
		boolean isContextPushed = false;
		try {
			StringList slSelect = new StringList(1);											
			slSelect.add(DomainConstants.SELECT_ID);	
			StringList slIndividualCANames = StringUtil.split(strChangeAction, ",");
			for (Object CAName : slIndividualCANames) {
				strTrimmedCAName = String.valueOf(CAName).trim();	
				if(UIUtil.isNotNullAndNotEmpty(strTrimmedCAName)){
					strGCASBuilder.append(strTrimmedCAName);
					strGCASBuilder.append(pgV3Constants.SYMBOL_COMMA);
				}
			}
			//As per the requirement, report should also contain Change Action for which user doesn't have access.Hence, push context is used.
			ContextUtil.pushContext(context,pgV3Constants.PERSON_USER_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;
			if(UIUtil.isNotNullAndNotEmpty(strGCASBuilder.toString())){
				mlCAObj =DomainObject.findObjects(context,//context
				pgV3Constants.TYPE_CHANGEACTION,//type
				strGCASBuilder.toString(),//name
				DomainConstants.QUERY_WILDCARD,//revision
				DomainConstants.QUERY_WILDCARD,//owner
				pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
				DomainConstants.EMPTY_STRING,//where clause
				false,//expand type
				slSelect);//object select 
			}
			if(null != mlCAObj && !mlCAObj.isEmpty()){
				for(int i=0;i<mlCAObj.size();i++){
					mCADetails = (Map<String,Object>)mlCAObj.get(i);
					strId = (String) mCADetails.get(DomainConstants.SELECT_ID);
					if(UIUtil.isNotNullAndNotEmpty(strId)){
					slId.add(strId);
					}	
				}
			}
		}catch(Exception e) {
			outLog.print("Exception in  getChangeActionList : "+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return slId;
	}
	
		
	 /** this method is to create Excel Sheet
	 * @returns void
	 * @throws IOException
	 */
	private void checkinExcelWorkbook(Context context,XSSFWorkbook workbook,String strFullPath,String strReportPath,String strReportName,String strReportObjectId,String strUserName) throws IOException {
	   try(FileOutputStream outputStream = new FileOutputStream(strFullPath)) {
			workbook.write(outputStream);
			// code to create the object and checking the .excel file in that object
			createCAReportObject(context,strUserName,strReportPath,strReportName,strReportObjectId);
		}catch (Exception e) {
			outLog.print("Exception in  checkinExcelWorkbook: "+e+"\n");
			outLog.flush();
		}
	}

	/**
    * this method is to create Report Object and checkin excel sheet to it
    * @return void
    */
	private void createCAReportObject(Context context,String strUserName,String strReportPath,String strReportName,String strReportObjectId) throws MatrixException{
		boolean isContextPushed = false;
		try {
			String typePgDSMReport = PropertyUtil.getSchemaProperty(context,"type_pgDSMReport");
			String policyPgDSMReport = PropertyUtil.getSchemaProperty(context,"policy_pgDSMReport");
			String  strFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
			String strContextUserName = context.getUser();
			String strObjectName= EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CAReport.ObjectName");
			String strNewObjectName = strObjectName+pgV3Constants.SYMBOL_UNDERSCORE+strContextUserName+pgV3Constants.SYMBOL_UNDERSCORE+System.currentTimeMillis();
			if(UIUtil.isNullOrEmpty(strReportObjectId)){
				String appName = FrameworkUtil.getTypeApplicationName(context, typePgDSMReport);
				DomainObject createBO = DomainObject.newInstance(context, typePgDSMReport, appName);
				BusinessObject bo = new BusinessObject(typePgDSMReport, strNewObjectName,DomainConstants.EMPTY_STRING, pgV3Constants.VAULT_ESERVICEPRODUCTION);
				if(!bo.exists(context)){
					createBO.createObject(context, typePgDSMReport, strNewObjectName,DomainConstants.EMPTY_STRING, policyPgDSMReport, pgV3Constants.VAULT_ESERVICEPRODUCTION);
					strReportObjectId = createBO.getObjectId(context);
				}
			}
			if(UIUtil.isNotNullAndNotEmpty(strReportObjectId)){
				//User might not have access to checkin the file.Hence, push context is used.
				ContextUtil.pushContext(context,pgV3Constants.PERSON_USER_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
				DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
				outLog.print("Before Checkin Report: "+strUserName+"| "+strReportName+"\n");
				outLog.flush();		
				doObj.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, strFormat, strReportName, strReportPath);
				doObj.promote(context);
				String sFullPath = strReportPath.concat(pgV3Constants.SYMBOL_FORWARD_SLASH).concat(strReportName);	
				File file = new File(sFullPath);
				Files.delete(file.toPath());
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
				sendEmail(context, strReportName, strUserName);
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
				outLog.print("After Checkin Report: "+strUserName+"| "+strReportName+"\n");
				outLog.flush();	
			} else {
			 	throw new MatrixException(ERR_MESSAGE);
			}		
		} catch (Exception e) {
			outLog.print("Exception in  createCAReportObject and Checkin File: "+e+"\n");
			outLog.flush();	
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
	}
	

	/** This method is used to get the Hyper link for Name columns in report
	 * @returns void
	 */
	private void getHyperlink(Context context, Cell cell, XSSFWorkbook workbook,
		String strValue, String strId) {
		String strURL = EnoviaResourceBundle.getProperty(context,CPNRESOURCEFILE, context.getLocale(), "emxCPN.BaseURL");
		String strNewURL = String.valueOf(strURL).trim();
		CreationHelper createHelper = workbook.getCreationHelper();
		XSSFCellStyle style = workbook.createCellStyle();
		Font hlinkfont = workbook.createFont();
		hlinkfont.setUnderline(Font.U_SINGLE);
		hlinkfont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
		style.setFont(hlinkfont);
		XSSFHyperlink link = (XSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
		if (UIUtil.isNotNullAndNotEmpty(strId) && UIUtil.isNotNullAndNotEmpty(strValue)) {
			link.setAddress(strNewURL + "?objectId=" + strId);
			cell.setCellValue(strValue);
			cell.setHyperlink(link);
			cell.setCellStyle(style);
		} else {
			cell.setCellValue(DomainConstants.EMPTY_STRING);
		}
	}
		 
		
	/** this method is to write column Names in excel sheet
	 * @returns void
	 */
	private void getColumnNames(Context context,XSSFRow rowHeaderPart,XSSFCellStyle cellStyle) {
		try {
			String strColumnNames = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CAReport.Worksheet.ColumnTypes");
			StringList slIndividualColumnNames = StringUtil.split(strColumnNames, pgV3Constants.SYMBOL_COMMA);
			String strIndividualcolumnName = DomainConstants.EMPTY_STRING;
			String strColumnName = DomainConstants.EMPTY_STRING;
			String strColumnValue = DomainConstants.EMPTY_STRING;
			for (int i = 0;i<slIndividualColumnNames.size();i++) {
				strIndividualcolumnName = slIndividualColumnNames.get(i);
				strColumnName = String.valueOf(strIndividualcolumnName).trim();
				strColumnValue = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CAReport.Worksheet.Column."+strColumnName);
				Cell cell = rowHeaderPart.createCell(i);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(strColumnValue);	
			}
		}catch(Exception e) {
			outLog.print("Exception in  getColumnNames: "+e+"\n");
			outLog.flush();
		}
	}
	
	/** this method is to update Excel Sheet with fetched data
	 * @returns void
	 */
	private void updateWorksheetCAReport(Context context,XSSFWorkbook workbook,MapList mlCADetailsAll, XSSFSheet sheetCA,String strHyperlink) {
		try {
			//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Starts
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.ChangeActionReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll=0;
			//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Ends
			String strCellValue = DomainConstants.EMPTY_STRING;
			String strHyperlinkId = DomainConstants.EMPTY_STRING;
			String strValue = DomainConstants.EMPTY_STRING;
			Map<String,Object> mpCA=new HashMap<>();
			Map<String,Object> htCommonCol=new HashMap<>();
			HashMap<Integer,Object> hmFinal=new HashMap<>();
			int rowCountCurrent = 0;
			int rowCount = (sheetCA.getLastRowNum())- (sheetCA.getFirstRowNum());
			if (rowCount != 0)
				rowCountCurrent = rowCount;
			int columnCount = 0;
			String[] strSplittedValue;
			for (int k = 0; k <mlCADetailsAll.size(); k++) {
				//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Starts
				iRowCountAll = iRowCountAll + 1;
				//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Ends
				++rowCountCurrent;
				XSSFRow row = sheetCA.createRow(rowCountCurrent);
				columnCount = 0;
				mpCA = (Map) mlCADetailsAll.get(k);
				htCommonCol= (Map)mpCA.get(STRFINALCOMMONCOLUMNS);
				hmFinal.put(0,HYPERLINK+ htCommonCol.get(DomainConstants.SELECT_NAME)+ pgV3Constants.SYMBOL_PIPE+ htCommonCol.get(DomainConstants.SELECT_ID));
				//Added for Defect -38739 - starts
				hmFinal.put(1, htCommonCol.get(ATTRIBUTE_SELECT+ATTRIBUTE_SYNOPSIS+"]"));
				//Added for Defect -38739 - ends
				hmFinal.put(2, htCommonCol.get(DomainConstants.SELECT_CURRENT));			
				hmFinal.put(3, htCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_ORIGINATOR));
				hmFinal.put(4, htCommonCol.get("ProposedRealizedCount"));
				hmFinal.put(5, mpCA.get(DomainConstants.SELECT_NAME));
				hmFinal.put(6, mpCA.get(DomainConstants.SELECT_OWNER));
				hmFinal.put(7, mpCA.get(DomainConstants.SELECT_CURRENT));
				hmFinal.put(8, mpCA.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENTS));
				hmFinal.put(9, mpCA.get(DomainConstants.SELECT_ORIGINATED));
				hmFinal.put(10,mpCA.get(ATTRIBUTE_SELECT+PropertyUtil.getSchemaProperty(context,"attribute_ActualCompletionDate")+"]"));
				//Added code for Requirement: 46219--Starts
				hmFinal.put(11, htCommonCol.get(DomainConstants.SELECT_DESCRIPTION));
				hmFinal.put(12, mpCA.get(SELECT_ATTRIBUTE_ROUTEACTION));
				//Added code for Requirement: 46219--Ends
				// For Cells Creation in each Row
				for (int j = 0; j <hmFinal.size();j++) {
					Cell cell = row.createCell(columnCount);
					columnCount++;
					if (hmFinal.get(j) instanceof StringList) {
						strCellValue =  (hmFinal.get(j).toString().replace("[", "").replace("]", ""));
					} else {
						strCellValue = (String) hmFinal.get(j);
					}
					if (UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)) {
						strSplittedValue = strCellValue.split("\\|",-1);
						strHyperlinkId = strSplittedValue[(strSplittedValue.length) - 1];
						strValue = strSplittedValue[(strSplittedValue.length) - 2];
						//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Starts
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
							getHyperlink(context, cell, workbook, strValue,strHyperlinkId);
						
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)){
							cell.setCellValue(strValue);
						}
						//Added code for 2018x.6 Requirement id 37979 Ability to generate Change Action report with-without hyperlink Ends
					}else {
						cell.setCellValue(strCellValue);
					}
				}
			} 
		}catch (Exception e) {
			outLog.print("Exception in  updateWorksheetCAReport: "+e+"\n");
			outLog.flush();	
		}
	}
	
	/** getCommonColumnsDetail Map for Common  Columns
	 * @return Map
	 */
	private Map<String,Object> getCommonColumnsDetail(Context context, DomainObject dobjRelatedPart) throws FrameworkException {
		Map<String, Object> mTSDetail = new HashMap<>();
		try {
			StringList slObjInfoSelect = new StringList();
			slObjInfoSelect.add(DomainConstants.SELECT_ID);			
		    slObjInfoSelect.add(DomainConstants.SELECT_NAME);
			//Added for Defect -38739 - starts
			slObjInfoSelect.add(ATTRIBUTE_SELECT+ATTRIBUTE_SYNOPSIS+"]");  
			//Added for Defect -38739 - ends
			slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_ORIGINATOR);  
			slObjInfoSelect.add(DomainConstants.SELECT_CURRENT);
			//Added code for Requirement: 46219--Starts
			slObjInfoSelect.add(DomainConstants.SELECT_DESCRIPTION);
			//Added code for Requirement: 46219--Ends
			//As per the requirement, report should also contain information of Change Action for which user doesn't have access.Hence, push context is used.
			mTSDetail = dobjRelatedPart.getInfo(context, slObjInfoSelect);			
		} catch (FrameworkException e) {
			outLog.print("Exception in  getCommonColumnsDetail: "+e+"\n");
			outLog.flush();	
		}
		return mTSDetail;
	}
			
	
	/**
	 * this method is used to retrieve the JVM Instance for logging
	 * @returns String
	 */
	private String getJVMInstance(){				
		String strCatalinaPath=System.getProperty(CATALINA_BASE);
		String strInstanceName=DomainConstants.EMPTY_STRING;
		try {
			if(UIUtil.isNotNullAndNotEmpty(strCatalinaPath) && strCatalinaPath.contains(pgV3Constants.SYMBOL_FORWARD_SLASH)){			
				StringList slInfo=StringUtil.split(strCatalinaPath, pgV3Constants.SYMBOL_FORWARD_SLASH);
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
	 * Description: This method is used to get connected CA from Originator
	 * Returns: StringList of CA List
	 */
	private StringList getConnectedChangeAction(Context context,String strOriginatorList,String strSelectedState) throws FrameworkException {
		boolean isContextPushed = false; 
		String strState = DomainConstants.EMPTY_STRING;
		String strOriginator = DomainConstants.EMPTY_STRING;
		StringBuilder sbWhereClause = new StringBuilder();
		StringList slId = new StringList();
		StringList slDuplicateCheck = new StringList();
		boolean bSetState = false;
		MapList mlPerson = new MapList();
		try{
			StringList slStateList = StringUtil.split(strSelectedState, pgV3Constants.SYMBOL_TILDA);
			StringList slOriginatorList = StringUtil.split(strOriginatorList, pgV3Constants.SYMBOL_COMMA);
			for(int i=0;i<slOriginatorList.size();i++){
				strOriginator = slOriginatorList.get(i);
				strOriginator=String.valueOf(strOriginator).trim();
				if(UIUtil.isNotNullAndNotEmpty(strOriginator) && !slDuplicateCheck.contains(strOriginator)){
					slDuplicateCheck.add(strOriginator);
					mlPerson = DomainObject.findObjects(context,//context
							DomainConstants.TYPE_PERSON,//type
							strOriginator,//name
							pgV3Constants.SYMBOL_HYPHEN,//revision
							DomainConstants.QUERY_WILDCARD,//owner
							pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
							DomainConstants.EMPTY_STRING,//where clause
							false,//expand type
							null);//object select
					if(null != mlPerson && !mlPerson.isEmpty()){
						if(UIUtil.isNotNullAndNotEmpty(sbWhereClause.toString())){
							sbWhereClause.append(pgV3Constants.SYMBOL_SPACE).append(pgV3Constants.SYMBOL_OR).append(" attribute[").append(pgV3Constants.ATTRIBUTE_ORIGINATOR).append("] == \"").append(strOriginator).append("\"");
						} else {
							sbWhereClause.append(BRACKET_START).append(ATTRIBUTE_SELECT).append(pgV3Constants.ATTRIBUTE_ORIGINATOR).append("] == \"").append(strOriginator).append("\"");
							bSetState = true;
						}
					}
				}
			}
			if(null != sbWhereClause.toString() && !(sbWhereClause.toString()).isEmpty()) {
				for(int j=0;j<slStateList.size();j++){
					strState = slStateList.get(j);
					if(bSetState){
						sbWhereClause.append(BRACKET_END).append(pgV3Constants.SYMBOL_SPACE).append(pgV3Constants.SYMBOL_AND).append(" (current == \"").append(strState).append("\"");
						bSetState = false;
					} else {
						sbWhereClause.append(pgV3Constants.SYMBOL_SPACE).append(pgV3Constants.SYMBOL_OR).append(" current == \"").append(strState).append("\"");
					}
				}
				sbWhereClause.append(BRACKET_END);
				StringList slSelect = new StringList(1);											
				slSelect.add(DomainConstants.SELECT_ID);
				//As per the requirement, report should also contain Change Action for which user doesn't have access.Hence, push context is used.
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);				 
				isContextPushed=true;
				MapList mlCA = DomainObject.findObjects(context,//context
						pgV3Constants.TYPE_CHANGEACTION,//type
						DomainConstants.QUERY_WILDCARD,//name
						DomainConstants.QUERY_WILDCARD,//revision
						DomainConstants.QUERY_WILDCARD,//owner
						pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
						sbWhereClause.toString(),//Where Expression
						false,//expandType
						slSelect);//objectSelects	
				if(null !=mlCA && !mlCA.isEmpty()){
					slId = getCANameList(mlCA);
				}
			}
		}catch(Exception e){
			outLog.print("Exception in  getConnectedChangeAction: "+e+"\n");
			outLog.flush();	
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return slId;
	}
	
	/**
	 * Description: This method is used to get connected CA Id/name from CA filed or input file
	 * Returns: CA Name List
	 */
	private StringList getCANameList(MapList mlCA){
		String sId = DomainConstants.EMPTY_STRING;
		Map<String,String> mCADetails = new HashMap<>();
		StringList slId = new StringList();
		try{
			for(int i=0; i<mlCA.size();i++){
				mCADetails = (Map<String,String>)mlCA.get(i);
				sId = mCADetails.get(DomainConstants.SELECT_ID);
				if(UIUtil.isNotNullAndNotEmpty(sId)){
					slId.add(sId);
				}
			}
		}
		catch(Exception e){
			outLog.print("Exception in  getCANameList: "+e+"\n");
			outLog.flush();	
		} 
		return slId;
	}
	
	 /**
	 * this method is to append CA Id list
	 * @param context is the matrix context
	 * @return Stringlist
	 */
	private StringList appendIds(StringList slChangeActionId, StringList slAppendedChangeActionId){
		String sId = DomainConstants.EMPTY_STRING;
		for(int i=0;i<slChangeActionId.size();i++){		
			sId = slChangeActionId.get(i);
			if(!slAppendedChangeActionId.contains(sId)){	
				slAppendedChangeActionId.add(sId);
			}
		}
		return slAppendedChangeActionId;
	}

	/**
 	* Method to get connected Parts from ChangeAction
 	* -	If Change Action has both Proposed Activity and Realized Activity Items
	*		Count Realized Items as per requirement 
	* -	If Change Action has only Affected Items 
	*		Count for Proposed Items as per requirement
	*
 	* @return integer
 	*/
	private int getProposedRealizedActivitiesFromCA(Context context, String strCAObjectId) throws Exception{
		int iCountItems = 0;
		try{
			MapList mlPAParts = new ChangeAction(strCAObjectId).getAffectedItems(context);
			MapList mlRAParts = new ChangeAction(strCAObjectId).getAllRealizedChanges(context);
			String strPartId = DomainConstants.EMPTY_STRING;
			Map<String,String> mpPart=new HashMap<>();
			// If Change Action has only Proposed Activities and No Realized Activities, Count for Proposed Activities
			if (!mlPAParts.isEmpty() && mlRAParts.isEmpty()) {
				Iterator<Object> itrPAParts = mlPAParts.iterator();						
				while (itrPAParts.hasNext()){
					mpPart = (Map)itrPAParts.next();	
					strPartId  = mpPart.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
						iCountItems++;
					}
				}
			}
			//	If Change Action has both Proposed Activities and Realized Activities, count for Realized Activities
			if (!mlRAParts.isEmpty()) {
				Iterator<Object> itrRAParts = mlRAParts.iterator();						
				while (itrRAParts.hasNext()){
					mpPart = (Map)itrRAParts.next();	
					strPartId  = mpPart.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
						iCountItems++;
					}
				}
			}
		} catch(Exception e){
			outLog.print("Exception in  getProposedRealizedActivitiesFromCA: "+e+"\n");
			outLog.flush();	
		} 
		return iCountItems;
	}
	
	/**
	* this method is to get connected Task details from CA
	* @param context is the matrix context
	* @return MapList
	*/
	private MapList getConnectedTaskDetail(Context context, DomainObject dobjCA, Map<String,Object> mCommonDetail) throws FrameworkException{
		String strRouteID = DomainConstants.EMPTY_STRING;
		MapList mlTaskDetailList = new MapList();
		StringList slSelect = new StringList();		
		slSelect.add(DomainConstants.SELECT_ID);	
		slSelect.add(DomainConstants.SELECT_CURRENT);	
		slSelect.add(DomainConstants.SELECT_NAME);
		slSelect.add(DomainConstants.SELECT_OWNER);
		slSelect.add(DomainConstants.SELECT_ORIGINATED);
		slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENTS);
		slSelect.add(ATTRIBUTE_SELECT+PropertyUtil.getSchemaProperty(context,"attribute_ActualCompletionDate")+"]");
		//Added code for Requirement: 46219--Starts
		slSelect.add(SELECT_ATTRIBUTE_ROUTEACTION);
		//Added code for Requirement: 46219--Ends
		Map<String,String> mpRouteTask = new HashMap<>();
		Map<String,Object> mpRoute = new HashMap<>();
		try{
			MapList mlRoute= dobjCA.getRelatedObjects(context, //context
									pgV3Constants.RELATIONSHIP_OBJECTROUTE, //rel pattern
									TYPE_ROUTE, //type pattern
									slSelect,// object select
									null, // rel select
									false,//get To
									true, //get From
									(short)1, //recurse level
									null, // obj where clause
									null, //rel where clause
									0);//limit
			if(null != mlRoute && !mlRoute.isEmpty()){
				for(int i=0;i<mlRoute.size();i++){
					mpRouteTask=(Map<String,String>)mlRoute.get(i);
					strRouteID=mpRouteTask.get(DomainConstants.SELECT_ID);
					DomainObject dobjRoute = DomainObject.newInstance(context, strRouteID);
					MapList mlRouteTask = dobjRoute.getRelatedObjects(context,//context
													pgV3Constants.RELATIONSHIP_ROUTETASK, //rel pattern
													TYPE_INBOX_TASK, //type pattern
													slSelect, //obj select
													null,// rel select 
													true,//get To
													false, //get From
													(short)1, //recurse level
													null, //obj where clause
													null, //rel where clause
													0);//limit
					if(null != mlRouteTask && !mlRouteTask.isEmpty()){
						for(int j=0;j<mlRouteTask.size();j++){
							mpRoute = (Map<String,Object>) mlRouteTask.get(j);
							mpRoute.put(STRFINALCOMMONCOLUMNS,mCommonDetail);
							mlTaskDetailList.add(mpRoute);
						}
					}
				}
			}
		} catch(Exception e){
			outLog.print("Exception in  getConnectedTaskDetail: "+e+"\n");
			outLog.flush();	
		} 
		return mlTaskDetailList;
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
