package com.pg.dsm.custom;
/*   pgWhereUsedReport.java - Report Request For Where Used Report Download written for requirement 33642
     Author:DSM L4 
     Copyright (c) 2019
     All Rights Reserved.
 */
import java.io.File;
import java.io.FileOutputStream;
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

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import matrix.util.Pattern;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.matrixone.apps.cpn.enumeration.CPNRelationship;
import com.matrixone.apps.cpn.enumeration.CPNType;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.hssf.util.HSSFColor;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import org.apache.poi.ss.usermodel.Font;
import java.nio.file.Files;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.poi.ss.util.WorkbookUtil;

public class pgWhereUsedReport implements DomainConstants {
	public pgWhereUsedReport(Context context, String[] args) {
	}
	private StringList slObjectCommonInfoSelect = getObjectCommonSelects();
	private StringList slObjectColumnInfoSelect = getObjectColumnSelects();
	private static final String STATE_RELEASE = "Release,Complete,Locked"; 
	private static final String STATE_OBSOLETE = "Obsolete";
	private static final String HYPERLINK = "Hyperlink:|";
	private static final String HYPERLINK_PIPE = "|";
	private static final String DELIMITER = "|";
	private static final String COMMA = ",";
	private static final String HYPERLINK_COMPARE = "Hyperlink:";
	private static final String ALTERNATE_PREFIX = "Alternate";
	private static final String FBOM_PREFIX = "FBOM_Substitute";
	private static final String EBOM_PREFIX = "EBOM_Substitute";
	private static final String MAKETOPRODUCE_PREFIX = "Make To Produce";
	private static final String DESCENDING = "descending";
	private static final String DENIED = "#DENIED!";
	private static final String STRING = "string";
	private static final String SHEET = "This is an invalid part";
	private static final String SHEETNAME = "Sheet1";
	private static final String EMXCPNSTRINGRESOURCE = "emxCPNStringResource";
	private static final String EMXCPN = "emxCPN";
	private static final String USER_AGENT ="person_UserAgent";
	private static final String NOACCESS ="No Access";
	private static final String FROMREL ="].fromrel.to.id";
	private static final String SUBFROMRELID = "].fromrel.from.id";
	private static final String SUBFROMRELCURRENT = "].fromrel.from.current";
	private static final String SUBFROMRELTYPE = "].fromrel.from.type";
	private static final String DATE_YMDHS = "yyyy-MM-dd HH:mm:ss";
	private static final String SUBSTITUTE_PREFIX = "_Substitute";
	private static final String TYPEFORMULATIONPHASE = "Formulation Phase";
	private static final String TYPEPGPHASE = "pgPhase";
	private StringList slCircularReference = new StringList();
	private PrintWriter outLog = null;
	private int iHyperLinkLimit;
	private int iRowCountAll;
	private int rowCount;
	private static final String REPORT_STATE = "Submitted";
	//Added code for 2018x.6 Requirement id 36711 Ability to generate Part Library report with-without hyperlink Starts
	private static final String HYPERLINKASINPUT = "HyperlinkAsInput";
	//Added code for 2018x.6 Requirement id 36711 Ability to generate Part Library report with-without hyperlink Ends

	//Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567 - Start
	private String strOriginatingSource = "";
	private static final String SPECREADER = "SpecReader";
	private static final String ENOVIA = "ENOVIA";
	private static final String ORIGINATING_SOURCE = "OriginatingSource";
	private static final String TYPE_GLOBALSUBSCRIPTIONCOFIGURATION = "Global Subscription Configuration";
	//Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567 - End
	
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
	private static final String STR_VAULT_ADMINISTRATION=PropertyUtil.getSchemaProperty(null,"vault_eServiceAdministration");
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends

	/*
     * this method is to generate the Where Used Report for the input parts submitted by user
     * @param context is the matrix context
     * @param args
     * @return void
     */	
public void generateWhereUsedReport(Context context, String[] args) throws Exception {
		HashMap<String, String> hmArgs = JPO.unpackArgs(args);
		String strPartNames = hmArgs.get("GCAS");
		String strReportFileName = hmArgs.get("ReportFileName");
		String strReportObjectId =  hmArgs.get("ReportObjectId");
		String strUserName =  hmArgs.get("UserName"); 
		String strLatestReleasePartOnly =  hmArgs.get("WhereUsedLatestReleasePartOnly");
		String strLevelSelected =  hmArgs.get("LevelSelected");
		//Added the code for 22x.03 August 2023 CW Requirement 41567 - Start
		strOriginatingSource = hmArgs.get(ORIGINATING_SOURCE);
		//Added the code for 22x.03 August 2023 CW Requirement 41567 - End
		int inputlevel = Integer.parseInt(strLevelSelected);
		//Added code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
		String strHyperlink = hmArgs.get(HYPERLINKASINPUT);
		//Added code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
	    if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames))){
	    	DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
	    	String objState = doObj.getInfo(context, DomainConstants.SELECT_CURRENT);
	    	if(UIUtil.isNotNullAndNotEmpty(objState) && REPORT_STATE.equalsIgnoreCase(objState)){
	    		doObj.promote(context);
	    		//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
	    		generateWhereUsedReportProcess(context,strUserName,strPartNames,strReportFileName,strReportObjectId,strLatestReleasePartOnly,inputlevel,strHyperlink);
	    		//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
	    		}
	    	}
	  }
	
		
	/*
     * this method fetches all the valid Parts from input and processes them.
     * @param context is the matrix context
     * @param args
     * @return void
     */	
	//NEW
	public void generateWhereUsedReportProcess(Context context, String strUserName,String strPartNames,String strReportFileName,String strReportObjectId, String strLatestReleasePartOnly, int inputlevel, String strHyperlink)throws MatrixException{
		MapList mlPart = new MapList();
		String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsedReport.HyperlinkLimit");
		iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
		
		StringList slIndividualPartNames = StringUtil.split(strPartNames, ",");			
		StringBuilder sbLogFolder = new StringBuilder();
		String strRelPrefix = DomainConstants.EMPTY_STRING;
		File fLogFolder = null;
		String strDirectoryNotCreated = "Could not create directory ";
		StringList slObjSelect = new StringList();
		slObjSelect.add(DomainConstants.SELECT_NAME);	               
		slObjSelect.add(DomainConstants.SELECT_TYPE);
		slObjSelect.add(DomainConstants.SELECT_CURRENT);
		
		String configFilePath = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.LogFolder");
		sbLogFolder.append(configFilePath).append(java.io.File.separator).append("WhereUsedLogFolder").append(java.io.File.separator);
		try{
			fLogFolder = new File(sbLogFolder.toString());
			if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
				throw new IOException(strDirectoryNotCreated + fLogFolder);
			}
			//Log file for Where Used Report
			String strStartTime = null; 			
			strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());	  			
			outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"WhereUsedLog.log",true));
			outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+"--------\n");
		 	outLog.print("Parts: "+strPartNames+"\n");
		 	outLog.print("Report Object Id: "+strReportObjectId+"\n");
		 	outLog.flush(); 
		 	
			mlPart=getValidPart(context, slIndividualPartNames, strLatestReleasePartOnly);
			//create Excel workbook	
			XSSFWorkbook workbook = new XSSFWorkbook();
			//Added code for defect id 41121 Starts
			boolean isReport = true;
			//Added code for defect id 41121 Ends
			if(null !=mlPart && !mlPart.isEmpty()){
				for(int i=0; i<mlPart.size();i++){					
					Map<String, String> mPartDetails = (Map<String, String>)mlPart.get(i);
					String strObjId = mPartDetails.get(DomainConstants.SELECT_ID);							
					String strInputCurrent = mPartDetails.get(DomainConstants.SELECT_CURRENT);
					String strInputPartName = mPartDetails.get(DomainConstants.SELECT_NAME);
					//create Excel worksheet for each valid part
					XSSFSheet sheetPart = createSheet(context, workbook, strInputPartName, rowCount); 
					rowCount = 0;
					String strPrevPath = strInputPartName;
					StringList slAllIdsList = new StringList();
					slAllIdsList.add(strObjId);						
					StringList slEBOMSubs = getEBOMSubstitute(context,strObjId,strInputCurrent);
					StringList slAlternate = getAlternate(context,strObjId,strInputCurrent);
					StringList slFBOMSubs = getFBOMSubstitute(context,strObjId,strInputCurrent);
					StringList slMakeToProduce = getMakeToProduce(context,strObjId,strInputCurrent);
					slAllIdsList = appendRelIds(slEBOMSubs, slAllIdsList);
					slAllIdsList = appendRelIds(slAlternate, slAllIdsList);
					slAllIdsList = appendRelIds(slFBOMSubs, slAllIdsList);
					slAllIdsList = appendRelIds(slMakeToProduce, slAllIdsList);	
					slAllIdsList = removeDuplicates(slAllIdsList);						
					outLog.print("Part to be exanded: "+strUserName+": "+slAllIdsList+"\n");
					outLog.flush();	
					DomainObject domAltPart = new DomainObject();
					String strObjState = DomainConstants.EMPTY_STRING;
					String strObjType = DomainConstants.EMPTY_STRING;
					String strObjName = DomainConstants.EMPTY_STRING;
					//Requirement 33916 updated to allow reports to be generated for parts which user does not have access
					boolean isContextPushed = false;
					boolean bAccess = accessCheck(context,strUserName,strObjId);
					if(!bAccess) {
						ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
						isContextPushed = true;
					}
					DomainObject domPartObj= DomainObject.newInstance(context,strObjId);
					Map<String, String> mpCommonColumnsDetail = getCommonColumnsDetail(context,domPartObj);
					for(int j=0; j<slAllIdsList.size();j++){							
						String sId = slAllIdsList.get(j);
						if(sId.contains("|")){
							strRelPrefix = sId.substring(0,sId.indexOf("|"));
							sId = sId.substring(sId.indexOf("|")+1,sId.length());
							domAltPart= DomainObject.newInstance(context,sId);							
							strObjState = domAltPart.getInfo(context, DomainConstants.SELECT_CURRENT);
							strObjType = domAltPart.getInfo(context, DomainConstants.SELECT_TYPE);
							strObjName = domAltPart.getInfo(context, DomainConstants.SELECT_NAME);								
						} else {
							strRelPrefix = DomainConstants.EMPTY_STRING;
							strObjState = mpCommonColumnsDetail.get(DomainConstants.SELECT_CURRENT);
							strObjType = mpCommonColumnsDetail.get(DomainConstants.SELECT_TYPE);
							strObjName = mpCommonColumnsDetail.get(DomainConstants.SELECT_NAME);								
						}						
						slCircularReference.clear();
						int currentlevel = 0;
						outLog.print("Expand start for: "+strUserName+": "+sId+"|" +strObjName+ "\n");
						outLog.flush();														
						//Process each Part
						slCircularReference.add(sId);
						//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
						//Added code for defect id 41121 Starts
						isReport = isReportExist(context,strReportObjectId);
						if(isReport) {
							getParentDetailProcess(context, strUserName,sId,strObjState,strObjType,strObjName,strInputPartName,inputlevel,currentlevel,strPrevPath,strInputCurrent,strRelPrefix,mpCommonColumnsDetail,workbook,sheetPart,strHyperlink,strReportObjectId);
						} else {
							break;
						}
						//Added code for defect id 41121 Ends
						//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
						outLog.print("Expand end for: "+strUserName+": "+sId+"|" +strObjName+ "\n");
						outLog.flush();
					}
					//Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54222 - Starts
					if(isContextPushed){
						ContextUtil.popContext(context);
						isContextPushed = false;
					}
					//Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54222 - Ends
					slCircularReference.clear();
				//Requirement 33916 updated to allow reports to be generated for parts which user does not have access
				}
			} else{
				XSSFSheet sheetPart = workbook.createSheet(SHEETNAME);
				int rowCountInvalid = 0;
				XSSFRow row = sheetPart.createRow(rowCountInvalid);
				Cell cell = row.createCell(rowCountInvalid++);
				cell.setCellValue(SHEET);
				outLog.print("Report completed for: "+strUserName+": "+strPartNames+"| No Valid Parts \n");
				outLog.flush();
			}
			//CheckIn Excel workbook to DSM Report Object
			checkInExcelFile(context,workbook,strReportFileName,strReportObjectId);
			String strEndTime = null; 
			strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
			outLog.print("Report completed for: "+strUserName+": "+strPartNames+"|" +strReportFileName+ "-------\n");
			outLog.print("-------Time completed: "+strEndTime+"-------\n");	
			outLog.print("-------\n");
			outLog.flush();
		}catch (Exception e) {
			e.printStackTrace();
			outLog.print("Exception in  generateWhereUsedReportProcess: "+strUserName+": "+e+"\n");
			outLog.flush();
		} finally {
			outLog.close();
		}
	}
	
	
	
/*
 * getParentDetailProcess- This method is to call getParentDetail method recursively
 * @return--- void
 * @param context is the matrix context
*/
private void getParentDetailProcess(Context context, String strUserName, String strParentId, String strParentState, String strParentType, String strParentName, String strInputPartName, int inputLevel, int currentlevel, String strPrevPath, String strInputCurrent, String strRelPrefix, Map<String,String> mpCommonColumnsDetail, XSSFWorkbook workbook, XSSFSheet sheetPart, String strHyperlink, String strReportObjectId) throws FrameworkException{
	String strWhereClause  = DomainConstants.EMPTY_STRING;
	String strPath  = DomainConstants.EMPTY_STRING;
	String strRelName = DomainConstants.EMPTY_STRING;
	String strCurPath = strPrevPath;
	StringList slRelSelect = new StringList();
	//Added code for defect id 41121 Starts
	boolean isReport = true;
	//Added code for defect id 41121 Ends
	slRelSelect.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
	String relPgDSOAffectedFPPList = PropertyUtil.getSchemaProperty(context,"relationship_pgDSOAffectedFPPList");
	Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_EBOM);
	relPattern.addPattern(CPNRelationship.FBOM.getRelationship(context));
	relPattern.addPattern(relPgDSOAffectedFPPList);
	relPattern.addPattern(pgV3Constants.RELATIONSHIP_PGTRANSPORTUNIT);
	relPattern.addPattern(pgV3Constants.RELATIONSHIP_PLANNEDFOR);
	relPattern.addPattern(pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE);
	String typeParentSub = CPNType.PARENTSUB.getType(context);
	boolean isContextPushed = false;
	try {		
		if (currentlevel == 0) {
			strPath = strInputPartName;
		} else {
			strPath = strPrevPath;
		}		
		
		if(!(pgV3Constants.TYPE_PGPHASE).equals(strParentType) || UIUtil.isNotNullAndNotEmpty(strRelPrefix)) {
			currentlevel = currentlevel + 1;			
		} 
		//2018x.6 Defect 40427 Requirement update Substitute, Alternate, Maket to Produce must be reported as Level 0
		//2018x6 41984 UAT - Remove object being substituted and list the parent instead
		if (strRelPrefix.equals(ALTERNATE_PREFIX) || strRelPrefix.equals(MAKETOPRODUCE_PREFIX)) {
			currentlevel = currentlevel - 1;
		}
		if (UIUtil.isNotNullAndNotEmpty(strRelPrefix)) {					
			strPath = strInputPartName + "-->" + strParentName;
			strCurPath = strPath;
			if (!(pgV3Constants.TYPE_PGPHASE).equals(strParentType)) {
				rowCount = rowCount + 1;
				//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
				updateWorksheetPart(context,workbook,sheetPart,strUserName,rowCount,mpCommonColumnsDetail,strParentId,currentlevel,strPath,strRelPrefix,strHyperlink);
				//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
				currentlevel = currentlevel + 1;
			}
		}
		if (inputLevel>=currentlevel) {
				//Requirement 33916 updated to allow reports to be generated for parts which user does not have access
				boolean bAccess = accessCheck(context,strUserName,strParentId);
				if (!bAccess) {
					ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);	
					isContextPushed = true;
				}
				DomainObject domPartObj = DomainObject.newInstance(context, strParentId);
				MapList mlRelatedParentsNext = domPartObj.getRelatedObjects(context, //context
						relPattern.getPattern(), //rel
						DomainConstants.QUERY_WILDCARD, //type
						slObjectColumnInfoSelect, //obj select
						slRelSelect, //rel select
						true, //get To
						false, //get From
						(short) 1, //recurse level
						strWhereClause, //object where clause
						null, //rel where clause
						0); //limit
				if(isContextPushed){
					ContextUtil.popContext(context);
				}
				//Requirement 33916 updated to allow reports to be generated for parts which user does not have access
				StringList slPartsToExpand = getPartsToExpand(mlRelatedParentsNext,strInputCurrent);

				for(int i=0; i<mlRelatedParentsNext.size();i++) {						
					Map<String, Object> mRelatedParentsNext = (Map<String, Object>)mlRelatedParentsNext.get(i);
					strParentType = (String) mRelatedParentsNext.get(DomainConstants.SELECT_TYPE);
					strParentId = (String) mRelatedParentsNext.get(DomainConstants.SELECT_ID);
					strParentState = (String) mRelatedParentsNext.get(DomainConstants.SELECT_CURRENT);
					strParentName = (String) mRelatedParentsNext.get(DomainConstants.SELECT_NAME);
					strRelName = (String) mRelatedParentsNext.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
					if(!slCircularReference.contains(strParentId)){
						slCircularReference.add(strParentId);
					
					if (slPartsToExpand.contains(strParentId) && !(pgV3Constants.TYPE_PGPHASE).equals(strParentType) && !(typeParentSub).equals(strParentType)){
						String strUpdatePath = strCurPath + "-->" + strParentName;						
						rowCount = rowCount + 1;
						//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
						updateWorksheetPart(context,workbook,sheetPart,strUserName,rowCount,mpCommonColumnsDetail,strParentId,currentlevel,strUpdatePath,strRelName,strHyperlink);
						//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
					}
						
					if (((slPartsToExpand.contains(strParentId) && !STATE_OBSOLETE.contains(strParentState)) || (pgV3Constants.TYPE_PGPHASE).equals(strParentType))) {	
						strPath = strCurPath + "-->" + strParentName;
						strRelPrefix = "";
						//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
						//Added code for defect id 41121 Starts
						isReport = isReportExist(context,strReportObjectId);
						if(isReport) {
							getParentDetailProcess(context,strUserName, strParentId, strParentState, strParentType, strParentName, strInputPartName, inputLevel,currentlevel, strPath,strInputCurrent,strRelPrefix,mpCommonColumnsDetail,workbook,sheetPart,strHyperlink,strReportObjectId);
						} else {
							break;
						}
						//Added code for defect id 41121 Ends
						//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
					}
					slCircularReference.remove(strParentId);
				}
			}
			
		}
		} catch(Exception e){
			e.printStackTrace();
			outLog.print("Exception in  getParentDetailProcess "+e+"\n");
			outLog.flush();
		}	
	}

		
/*
 * this method is to create Excel Workbool
 * @param context is the matrix context
 * @return void
 */

private void checkInExcelFile(Context context,XSSFWorkbook workbook, String strReportFileName, String strReportObjectId) {
	try {
		File sbWhereUsedFolder = null;
		String strReportPath= EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.WhereUsedReport.Worksheet.FilePath");
		String strReportName=EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.ReportName");
		String strReportExtension = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.ReportExtension");
		String strUserName = context.getUser();
		
		Date now = new Date();
		SimpleDateFormat smpdf = new SimpleDateFormat("MM-dd-yyyy");
		String strReportExtractedDate = smpdf.format(now);	
		String strUserNameTemp = strUserName.replace(".","-");
		// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format :Start
		if(UIUtil.isNotNullAndNotEmpty(strReportFileName)) {
			strReportName = strReportFileName+"_"+strReportExtractedDate+"_"+strUserNameTemp+"_"+System.currentTimeMillis()+"." + strReportExtension;
		} else {
			strReportName = strReportName+"_"+strReportExtractedDate+"_"+strUserNameTemp+"_"+System.currentTimeMillis()+"." + strReportExtension;
		}
		// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format : End
		sbWhereUsedFolder = new File(strReportPath);
		if (!sbWhereUsedFolder.exists())  {
			sbWhereUsedFolder.mkdirs();
			}
		FileOutputStream outputStream = new FileOutputStream(sbWhereUsedFolder.toString()+ File.separator + strReportName);
		workbook.write(outputStream);
		// code to create the object and checking the .excel file in that object
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		createDSMReportObject(context,strReportPath,strReportName,strReportObjectId,strUserName);	
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
		outputStream.flush();
		outputStream.close();

	} 
	catch (Exception e) {
		e.printStackTrace();
		outLog.print("Exception in  checkInExcelFile: "+e+"\n");
		outLog.flush();
	} 
}
			

/*
 * this method is to create separate sheets for input parts
 * @param context is the matrix context
 * @return void
 */
private XSSFSheet createSheet(Context context, XSSFWorkbook workbook, String strPartName,int rowCount) {
	String strSheetName = DomainConstants.EMPTY_STRING;
	XSSFSheet sheetPart = null;
	if(UIUtil.isNotNullAndNotEmpty(strPartName)){	
		strSheetName = strPartName;
	}	else {
		strSheetName = SHEETNAME;
	}
	XSSFCellStyle cellStylePart = workbook.createCellStyle();
	XSSFFont xsfontPart = workbook.createFont();
	xsfontPart.setBold(true);
	xsfontPart.setFontHeightInPoints((short) 12);
	cellStylePart.setFont(xsfontPart);
	cellStylePart.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());		
	cellStylePart.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	if(workbook.getSheet(strSheetName) == null){
		String sheetName = WorkbookUtil.createSafeSheetName(strSheetName);	
		sheetPart = workbook.createSheet(sheetName);
		XSSFRow rowHeaderPart = sheetPart.createRow(0);					
		getColumnNames(context,rowHeaderPart,cellStylePart);
		rowCount = rowCount + 1;
	} 
	return sheetPart;
}

/*
 * this method is to get column names for excel sheet
 * @param context is the matrix context
 * @return void 
 */
private void getColumnNames(Context context,XSSFRow rowHeaderPart,XSSFCellStyle cellStylePart) {
	try {
		String strColumnNames = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.Worksheet.ColumnTypes");
		StringList slIndividualColumnNames = StringUtil.split(strColumnNames, COMMA);
		for (int i = 0;i<slIndividualColumnNames.size();i++) {
			String columnName = slIndividualColumnNames.get(i);
			String strColumnName = String.valueOf(columnName).trim();
			String strColumnValue = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.Worksheet.Column."+strColumnName);
			Cell cell = rowHeaderPart.createCell(i);
			cell.setCellStyle(cellStylePart);
			cell.setCellValue(strColumnValue);	
		}
	} catch(Exception e) {
		e.printStackTrace();
		outLog.print("Exception in  getColumnNames: "+e+"\n");
		outLog.flush();
	}
}

/*
 * this method is to get column names for excel sheet
 * @param context is the matrix context
 * @return void 
 */
																	
private void updateWorksheetPart(Context context,XSSFWorkbook workbook, XSSFSheet sheetPart, String strUserName, int rowCount, Map<String,String> mpCommon,String strPartId, int strPartLevel,String strPath, String strRelationshipName, String strHyperlink) {
	try{
		if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
			boolean bAccess = accessCheck(context,strUserName,strPartId);		
			String strLanguage = context.getLocale().getLanguage();
			String strPrimaryOrganization = DomainConstants.EMPTY_STRING;
			String strSecondaryOrganization = DomainConstants.EMPTY_STRING;
			String strSegment = DomainConstants.EMPTY_STRING; 
			String strParentTranslatedType = DomainConstants.EMPTY_STRING;
			String strInputObjectName = mpCommon.get(DomainConstants.SELECT_NAME);
			String strInputObjectId = mpCommon.get(DomainConstants.SELECT_ID);
			String strInputObjectType = mpCommon.get(DomainConstants.SELECT_TYPE);
			String strInputObjectRevision = mpCommon.get(DomainConstants.SELECT_REVISION);
			String strInputObjectState = mpCommon.get(DomainConstants.SELECT_CURRENT);
			String strInputPolicy = mpCommon.get(DomainConstants.SELECT_POLICY);
	
			String strInputTranslatedState = i18nNow.getStateI18NString(strInputPolicy, strInputObjectState, strLanguage);
			String strInputTranslatedType = i18nNow.getTypeI18NString(strInputObjectType, strLanguage);
								
			int iLevel = strPartLevel;
			String strLevel = Integer.toString(iLevel);
			String strAsterik = getAsterisk(iLevel);
			DomainObject domPartObj= DomainObject.newInstance(context,strPartId);
			Map<String, String> mpOrg = new HashMap<>();
			Map<String, String>mp = new HashMap<>();
			//Create Excel Row
			XSSFRow row = sheetPart.createRow(rowCount);
			int columnCount1 = 0;
			HashMap<Integer, String> hm = new HashMap<>();
			if (bAccess) {
				mp = getColumnsDetail(context,domPartObj);
				String strConnectedType = mp.get(DomainConstants.SELECT_TYPE);
				String strConnectedCurrent = mp.get(DomainConstants.SELECT_CURRENT);
				String strPolicy = mp.get(DomainConstants.SELECT_POLICY);
				String strParentTranslatedState = i18nNow.getStateI18NString(strPolicy, strConnectedCurrent, strLanguage);				
				String strTranslatedRelationship = i18nNow.getTypeI18NString(strRelationshipName, strLanguage);			
				strParentTranslatedType = i18nNow.getTypeI18NString(strConnectedType, strLanguage);		
			//Get multi-value Organization and Segment
			
			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_NAME); 			
			StringBuilder sbPrimaryOrgName = new StringBuilder();
			StringBuilder sbSecOrgName = new StringBuilder();
			StringBuilder sbSegmentName = new StringBuilder();
			String strRelName = DomainConstants.EMPTY_STRING;
			MapList mlOrg = domPartObj.getRelatedObjects(context, // context
					pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + ","
					+ pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION + ","
					+ pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT, // rel pattern
					pgV3Constants.TYPE_PGPLIORGANIZATIONCHANGEMANAGEMENT + ","
					+ pgV3Constants.TYPE_PGPLISEGMENT, // type pattern
					slObjSelects, // objectSelects
					null, // relationshipSelects
					false, // getTo - Get Parent Data
					true, // getFrom - Get Child Data
					(short) 1, // recurseToLevel
					null, // objectWhere
					null,// relationshipWhere
					0);// limit
			if (!mlOrg.isEmpty()) {
				Iterator<Object> itrOrg = mlOrg.iterator();
				while (itrOrg.hasNext()) {
					mpOrg = (Map) itrOrg.next();
					strRelName = (mpOrg.get("relationship"));
					if (strRelName != null && pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION.equals(strRelName)) {
						strPrimaryOrganization = (mpOrg.get(DomainConstants.SELECT_NAME));
						if (sbPrimaryOrgName.length() > 0)
							sbPrimaryOrgName.append(", ").append(strPrimaryOrganization);
						else
							sbPrimaryOrgName.append(strPrimaryOrganization);
					}
					if (strRelName != null && pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION.equals(strRelName)) {
						strSecondaryOrganization = (mpOrg.get(DomainConstants.SELECT_NAME));
						if (sbSecOrgName.length() > 0) {
							sbSecOrgName.append(", ").append(strSecondaryOrganization);
						} else {
							sbSecOrgName.append(strSecondaryOrganization);
						}
					}
					if (strRelName != null && pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT.equals(strRelName)) {
						strSegment = (mpOrg.get(DomainConstants.SELECT_NAME));
						if (sbSegmentName.length() > 0) {
							sbSegmentName.append(", ").append(strSegment);
						} else {
							sbSegmentName.append(strSegment);
						}
					}
				}
			}
			
			hm.put(0, strInputTranslatedType);
			hm.put(1, HYPERLINK+strInputObjectName+HYPERLINK_PIPE+strInputObjectId);
			hm.put(2, strInputObjectRevision);
			hm.put(3, strInputTranslatedState);
			hm.put(4, strParentTranslatedType);
			hm.put(5, HYPERLINK+mp.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+mp.get(DomainConstants.SELECT_ID));
			hm.put(6, mp.get(DomainConstants.SELECT_REVISION));
			hm.put(7, strParentTranslatedState);
			hm.put(8, mp.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
			hm.put(9, mp.get(DomainConstants.SELECT_DESCRIPTION));
			hm.put(10, strTranslatedRelationship);								
			hm.put(11, strAsterik);
			hm.put(12, strLevel);
			hm.put(13, strPath);
			hm.put(14, mp.get(pgV3Constants.SELECT_ATTRIBUTE_ORIGINATOR));
			hm.put(15, mp.get(pgV3Constants.SELECT_ATTRIBUTE_PGLASTUPDATEUSER));
			hm.put(16, sbSegmentName.toString());
			hm.put(17, mp.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE));
			hm.put(18, mp.get(DomainConstants.SELECT_OWNER));
			hm.put(19, mp.get(DomainConstants.SELECT_POLICY));
			hm.put(20, sbPrimaryOrgName.toString());
			hm.put(21, sbSecOrgName.toString());
			hm.put(22, mp.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
			hm.put(23, mp.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE));
			hm.put(24, mp.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE));	
			} else {
				StringList slSelectObj = new StringList(DomainConstants.SELECT_ID);
				slSelectObj.add(DomainConstants.SELECT_TYPE);
				slSelectObj.add(DomainConstants.SELECT_NAME);
				slSelectObj.add(DomainConstants.SELECT_REVISION);
				slSelectObj.add(DomainConstants.SELECT_ID);
				domPartObj= DomainObject.newInstance(context,strPartId);				
				mp=domPartObj.getInfo(context,slSelectObj );
				String strConnectedType = mp.get(DomainConstants.SELECT_TYPE);
				strParentTranslatedType = i18nNow.getTypeI18NString(strConnectedType, strLanguage);
				
				hm.put(0, strInputTranslatedType);
				hm.put(1, HYPERLINK+strInputObjectName+HYPERLINK_PIPE+strInputObjectId);
				hm.put(2, strInputObjectRevision);
				hm.put(3, strInputTranslatedState);
				hm.put(4, strParentTranslatedType);
				hm.put(5, HYPERLINK+mp.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+mp.get(DomainConstants.SELECT_ID));
				hm.put(6, mp.get(DomainConstants.SELECT_REVISION));
				hm.put(7, NOACCESS);
				hm.put(8, NOACCESS);
				hm.put(9, NOACCESS);
				hm.put(10, NOACCESS);								
				hm.put(11, NOACCESS);
				hm.put(12, NOACCESS);
				hm.put(13, strPath);
				hm.put(14, NOACCESS);
				hm.put(15, NOACCESS);
				hm.put(16, NOACCESS);
				hm.put(17, NOACCESS);
				hm.put(18, NOACCESS);
				hm.put(19, NOACCESS);
				hm.put(20, NOACCESS);
				hm.put(21, NOACCESS);
				hm.put(22, NOACCESS);
				hm.put(23, NOACCESS);
				hm.put(24, NOACCESS);				
			}
			//For Cells Creation in each Row
			//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
			updateRow(context,workbook,row,hm,columnCount1,strHyperlink);
			//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
		}			
	}catch (Exception e) {		
		outLog.print("Exception in  updateWorksheetPart "+e+"\n");
		outLog.flush();	
	}
}
								


/*
 * this method is to update rows with data in excel sheet
 * @param context is the matrix context
 * @return void
 */

private void updateRow(Context context,XSSFWorkbook workbook,XSSFRow row,Map<Integer,String> hm, int columnCount1, String strHyperlink) {	
	iRowCountAll = iRowCountAll + 1;
	String strHyperlinkId = DomainConstants.EMPTY_STRING;
	String strCellValue = DomainConstants.EMPTY_STRING;
	String strValue = DomainConstants.EMPTY_STRING;
	XSSFCellStyle style = workbook.createCellStyle();		
	try {
		for(int k=0;k<hm.size();k++){								
			Cell cell = row.createCell(columnCount1++);
			strCellValue = hm.get(k); 
			//Hyperlink is applied to limited number of rows because of Excel limitaion
			if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)) {
				String[] strSplittedValue = strCellValue.split("\\|",-1); 
				strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
				strValue = strSplittedValue[(strSplittedValue.length)-2];
				//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Starts
				if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){					
				//Updated code for 2018x.6 Requirement id 36700 Ability to generate Where Used report with-without hyperlink--Ends
					getHyperlink(context,cell,workbook,strValue,strHyperlinkId);
				} else {					
					cell.setCellValue(strValue);
					cell.setCellStyle(style);
				}
			} else {
				if(UIUtil.isNotNullAndNotEmpty(strCellValue)){	
					cell.setCellValue(strCellValue);
					cell.setCellStyle(style);
				} else {
					cell.setCellValue(DomainConstants.EMPTY_STRING);
					cell.setCellStyle(style);
				}	
			}
			
		}
	}catch(Exception e) {
		e.printStackTrace();
		outLog.print("Exception in  updateRow "+e+"\n");
		outLog.flush();	
	}
}
		
		
/*
 * this method is to Checkin Excel Sheet in to a Where Used Report Object
 * @param context is the matrix context
 * @param args has the required information
 * @return void
	     */

//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
private void createDSMReportObject(Context context,String strReportPath,String strReportName,String strReportObjectId,String strUserName) throws MatrixException {
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	boolean isContextPushed = false; 
	try {
		String typePgDSMReport = PropertyUtil.getSchemaProperty(context,"type_pgDSMReport");
		String policyPgDSMReport = PropertyUtil.getSchemaProperty(context,"policy_pgDSMReport");
		String strObjectName= EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.ObjectName");
		String  strFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
		String strContextUserName = context.getUser();
		String strNewObjectName = strObjectName+"_"+strContextUserName+"_"+System.currentTimeMillis();
		String strObjectId = strReportObjectId;
		if(UIUtil.isNullOrEmpty(strObjectId))
		{
			String appName = FrameworkUtil.getTypeApplicationName(context, typePgDSMReport);
			DomainObject createBO = DomainObject.newInstance(context, typePgDSMReport, appName);
			BusinessObject bo = new BusinessObject(typePgDSMReport, strNewObjectName, DomainConstants.EMPTY_STRING, pgV3Constants.VAULT_ESERVICEPRODUCTION);
			if(!bo.exists(context))
			{
				createBO.createObject(context, typePgDSMReport, strNewObjectName,DomainConstants.EMPTY_STRING, policyPgDSMReport, pgV3Constants.VAULT_ESERVICEPRODUCTION);
				strObjectId = createBO.getObjectId(context);
			}
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);	
			isContextPushed = true; 
			DomainObject doObj = DomainObject.newInstance(context, strObjectId);
			doObj.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, strFormat, strReportName, strReportPath);
			doObj.promote(context);
			String sFullPath = strReportPath.concat("/").concat(strReportName); 
			File file = new File(sFullPath);
			//below line commented for Local testing only - has to be uncommented for deployment
			Files.delete(file.toPath());
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
			sendEmail(context, strReportName, strUserName);
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		} else {
			throw new MatrixException("Creation of Object Failed");
			
		}
	} catch (Exception e) {
		e.printStackTrace();
		outLog.print("Exception in  createDSMReportObject and Checkin File: "+e+"\n");
		outLog.flush();	
	}finally {
		if(isContextPushed)
			ContextUtil.popContext(context);
	}
}
		
		
/*
 * this method is to get valid part from input 
 * @param context is the matrix context
 * @return MapList
 */
private MapList getValidPart(Context context, StringList slIndividualPartNames, String strLatestReleasePartOnly) throws FrameworkException{
	MapList mlValidPart = new MapList();
	MapList mlPart = new MapList();
	MapList mlreturnPart = new MapList();
	String strPartName = DomainConstants.EMPTY_STRING;
	try {
		for (Object PartName : slIndividualPartNames) {
			strPartName = String.valueOf(PartName).trim();
			String strWhereClauseTrue = "current =="+pgV3Constants.STATE_RELEASE+pgV3Constants.SYMBOL_SPACE+pgV3Constants.SYMBOL_OR+pgV3Constants.SYMBOL_SPACE+"current=="+pgV3Constants.STATE_COMPLETE;
			String strWhereClauseFalse = "revision==last";
			if(UIUtil.isNotNullAndNotEmpty(strPartName)){
				if(UIUtil.isNotNullAndNotEmpty(strLatestReleasePartOnly) && strLatestReleasePartOnly.equalsIgnoreCase("true")) {
					//Code to fetch the Latest Release Parts only
					mlPart=getHighestRevisionForPart(context,strPartName,strWhereClauseTrue);
					mlValidPart.add(mlPart);
					//Fetching Highest Release revision --End
				} else {
					mlPart=getHighestRevisionForPart(context,strPartName,strWhereClauseFalse);
					mlValidPart.add(mlPart);
					//Fetching Highest Release revision --End
				}
			}
		} 
		for(int i=0;i<mlValidPart.size();i++){
			MapList mlValidPartDetails = (MapList) mlValidPart.get(i);
			for(int j=0;j<mlValidPartDetails.size();j++){
				Map<String, String> mPartDetails = (Map<String, String>)mlValidPartDetails.get(j);
				mlreturnPart.add(mPartDetails);
			}	
		}
			
	}catch(Exception e) {
		e.printStackTrace();	
		outLog.print("Exception in  getValidPart : "+e+"\n");
		outLog.flush();
	}
	return mlreturnPart;
}	


/*
 * this method determines which part to expand based on input Part state 
 * @param context is the matrix context
 * @return MapList
 */
private boolean isValidForProcessing(String strInputPartState, String strRelPartState) {
	boolean bIsValid = false;
	if(STATE_RELEASE.contains(strInputPartState) && !STATE_OBSOLETE.contains(strRelPartState)) {
		//Return only Release and Preliminary
		bIsValid = true;	
	} else if ((STATE_OBSOLETE.contains(strInputPartState)) && (STATE_RELEASE.contains(strRelPartState) || STATE_OBSOLETE.contains(strRelPartState))) {
		//Return  Release or latest version Obsolete
		bIsValid = true;
	} else if ((!STATE_OBSOLETE.contains(strInputPartState) && !STATE_RELEASE.contains(strInputPartState)) && (!STATE_OBSOLETE.contains(strRelPartState) && !STATE_RELEASE.contains(strRelPartState))) {
		bIsValid = true;
	} else {
		bIsValid = false;
	}
	return bIsValid;
}



/*
 * this method determines which part to expand based on input Part state 
 * @return MapList
 */

private StringList getPartsToExpand(MapList mlRelatedParentsNext, String strInputPartState){
	String strParentId = DomainConstants.EMPTY_STRING;
	String strParentType = DomainConstants.EMPTY_STRING;
	String strParentState = DomainConstants.EMPTY_STRING;
	StringList slPartsToExpand = new StringList();
	try {
		for(int i=0; i<mlRelatedParentsNext.size();i++){						
			Map<String, Object> mRelatedParentsNext = (Map<String, Object>) mlRelatedParentsNext.get(i);
			strParentType = (String) mRelatedParentsNext.get(DomainConstants.SELECT_TYPE);
			strParentId = (String) mRelatedParentsNext.get(DomainConstants.SELECT_ID);
			strParentState = (String) mRelatedParentsNext.get(DomainConstants.SELECT_CURRENT);
		if (pgV3Constants.TYPE_PGPHASE.equals(strParentType)) {
			slPartsToExpand.add(strParentId);
			} else {
			if(STATE_RELEASE.contains(strInputPartState)) {
				//Return only Release and Preliminary
				if (!STATE_OBSOLETE.contains(strParentState)) {
					slPartsToExpand.add(strParentId);
				}
			} else if (STATE_OBSOLETE.contains(strInputPartState)) {
				//Return  Release or latest version Obsolete
				slPartsToExpand = getHighestObsoleteRev(mlRelatedParentsNext,slPartsToExpand);
				
			} else {
				//Return non-release & Non Obsolete parts
				if (!STATE_OBSOLETE.contains(strParentState) && !STATE_RELEASE.contains(strParentState)) {
					slPartsToExpand.add(strParentId);
				}
			}
			}
		}
			
	} catch(Exception e) {
			e.printStackTrace();
	} 
	slPartsToExpand = removeDuplicates(slPartsToExpand);
	return slPartsToExpand;
}
	/*
     * this method is to get highest revision input part 
     * @param context is the matrix context
     * @return MapList
     */
	private MapList getHighestRevisionForPart(Context context,String strPartName, String strWhereClause) throws FrameworkException{
		String strType = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.Types");
		String sPolicyExclusion = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.WhereUsed.PolicyExclusion");
		StringList slSelect = new StringList();
		slSelect.add(DomainConstants.SELECT_ID);
		slSelect.add(DomainConstants.SELECT_NAME);
		slSelect.add(DomainConstants.SELECT_REVISION);
		slSelect.add(DomainConstants.SELECT_CURRENT);			
		slSelect.add(DomainConstants.SELECT_TYPE);	
		slSelect.add(DomainConstants.SELECT_POLICY);	
		slSelect.add("last.id");
		MapList mlPart = new MapList();
		//Added for Release checkbox issue : Ritika--Starts
		boolean isContextPushed = false; 
		//Added for Release checkbox issue : Ritika--Ends
		try {
			//Added for Release checkbox issue : Ritika--Starts
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true; 
			//Added for Release checkbox issue : Ritika--Ends
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
			if(!mlPartValid.isEmpty()){
				mlPartValid.sort(DomainConstants.SELECT_REVISION, DESCENDING, STRING);
				Map<String, String> mReleaseHighestRevision =(Map<String, String>)mlPartValid.get(0);			
				String strInputPolicy = mReleaseHighestRevision.get(DomainConstants.SELECT_POLICY);
				if(!sPolicyExclusion.contains(strInputPolicy)){
					mlPart.add(mReleaseHighestRevision);
					}
			}
		}catch(Exception e) {
				e.printStackTrace();
			//Added for Release checkbox issue : Ritika--Starts
		} finally{
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
			//Added for Release checkbox issue : Ritika--Ends
		return mlPart;
	}
	

/*
 * this method is to create Excel Sheet
 * @param context is the matrix context
 * @return void
 */
private StringList appendRelIds(StringList slRelIds, StringList slAllIdsList){
	String sId = DomainConstants.EMPTY_STRING;
	if(null != slRelIds && !slRelIds.isEmpty()) {
		for(int i=0;i<slRelIds.size();i++){		
			sId = slRelIds.get(i);
			if(!slAllIdsList.contains(sId)){	
				slAllIdsList.add(sId);
			}
		}
	}
	return slAllIdsList;
}

	
	
	
/*
 * getCommonColumnsDetail- Map for Common  Columns 
 * @return Map
 */
private Map<String, String> getCommonColumnsDetail(Context context, DomainObject dobjRelatedPart) throws FrameworkException {
	return dobjRelatedPart.getInfo(context, slObjectCommonInfoSelect);
}
	
/*
 * getColumnsDetail- Map for Columns in excel
 * @return Map
 */
private Map<String, String> getColumnsDetail(Context context, DomainObject dobjRelatedPart) throws FrameworkException {
	return dobjRelatedPart.getInfo(context, slObjectColumnInfoSelect);
}
	
/*
 * getObjectCommonSelects- Stringlist for Common Five Columns 
 * @return StringList
 */
private StringList getObjectCommonSelects(){
	StringList slObjInfoSelect = new StringList(10);
	try{
		slObjInfoSelect.add(DomainConstants.SELECT_ID);
		slObjInfoSelect.add(DomainConstants.SELECT_NAME);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);                 
		slObjInfoSelect.add(DomainConstants.SELECT_TYPE);
		slObjInfoSelect.add(DomainConstants.SELECT_REVISION);
		slObjInfoSelect.add(DomainConstants.SELECT_DESCRIPTION);
		slObjInfoSelect.add(DomainConstants.SELECT_CURRENT);
		slObjInfoSelect.add(DomainConstants.SELECT_POLICY);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
	}catch(Exception e){
		e.printStackTrace();
	}
	return slObjInfoSelect;
}
	
	
/*
 * getObjectColumnSelects- Stringlist for Columns 
 * @return StringList
 */
private StringList getObjectColumnSelects(){
	StringList slObjInfoSelect = new StringList(10);
	try{
		slObjInfoSelect.add(DomainConstants.SELECT_ID);
		slObjInfoSelect.add(DomainConstants.SELECT_NAME);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);                 
		slObjInfoSelect.add(DomainConstants.SELECT_TYPE);
		slObjInfoSelect.add(DomainConstants.SELECT_REVISION);
		slObjInfoSelect.add(DomainConstants.SELECT_DESCRIPTION);
		slObjInfoSelect.add(DomainConstants.SELECT_CURRENT);
		slObjInfoSelect.add(DomainConstants.SELECT_OWNER);
		slObjInfoSelect.add(DomainConstants.SELECT_POLICY);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGLASTUPDATEUSER);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_ORIGINATOR);
		slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
	}catch(Exception e){
		e.printStackTrace();
	}
	return slObjInfoSelect;
}
	
/*
 * accessCheck- This method is to check access to input part for the user
 * @return boolean
 */
	private boolean accessCheck(Context context, String strUserName, String strPartId) throws FrameworkException {
		boolean bAccess = false;
		boolean isContextPushed = false;
		String strValue = DomainConstants.EMPTY_STRING;
		try {
			//Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54222 - Starts
			if(UIUtil.isNotNullAndNotEmpty(strPartId)) {
				DomainObject domainObject = DomainObject.newInstance(context, strPartId);
				ContextUtil.pushContext(context, strUserName, null, context.getVault().getName());
				isContextPushed = true;
				strValue = domainObject.getInfo(context, DomainConstants.SELECT_CURRENT);
				if (UIUtil.isNotNullAndNotEmpty(strValue) && !(DENIED).equals(strValue)) {
					bAccess = true;
				} else {
					bAccess = false;
				}
			}
		} catch (Exception e) {
			bAccess = false;
			//Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54222 - Ends
		} finally {
			if (isContextPushed)
				ContextUtil.popContext(context);
		}
		return bAccess;
	}
	
	
	
/*
 * getEBOMSubstitute- This method is to get EBOM Substitute Ids
 * @return MapList
 */
private StringList getEBOMSubstitute(Context context, String strobjectId, String strInputState) throws FrameworkException{

	String sCurrent = DomainConstants.EMPTY_STRING;
	String sType = DomainConstants.EMPTY_STRING;
	StringList slId = new StringList();
	boolean isContextPushed = false; 
		try{  	
			
			//Defect 41984 Only list parents of substituted part
			String strSelect = "to["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].fromrel.from.id";
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true; 
			String strReferenceParts = MqlUtil.mqlCommand(context, "print bus $1 select $2 dump", strobjectId, strSelect);
			StringList slReferenceParts = StringUtil.split(strReferenceParts, COMMA);
			for (int j = 0;j<slReferenceParts.size();j++) {
				String strId = slReferenceParts.get(j);
					if(UIUtil.isNotNullAndNotEmpty(strId) && !slId.contains(strId)){
						DomainObject domPartObj= DomainObject.newInstance(context,strId);
						sCurrent = domPartObj.getInfo(context, DomainConstants.SELECT_CURRENT);
						sType = domPartObj.getInfo(context, DomainConstants.SELECT_TYPE);
						// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
						if(SPECREADER.equalsIgnoreCase(strOriginatingSource)){
							if(sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE) || sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASED)) {
								if (TYPEPGPHASE.equals(sType) || TYPEFORMULATIONPHASE.equals(sType) || isValidForProcessing(strInputState,sCurrent)) {
									slId.add(EBOM_PREFIX+DELIMITER+strId);						
								}
							}
						}else {
							if (TYPEPGPHASE.equals(sType) || TYPEFORMULATIONPHASE.equals(sType) || isValidForProcessing(strInputState,sCurrent)) {
								slId.add(EBOM_PREFIX+DELIMITER+strId);						
							}
						}
						// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
					}
				}					
			
		}catch(Exception e){
			e.printStackTrace();
			outLog.print("Exception in  getEBOMSubstitute "+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return slId;
	}	


/*
 * getAlternate- This method is to get Alternate Ids
 * @return MapList
 */
private StringList getAlternate(Context context, String strobjectId, String strInputState) throws FrameworkException{

	StringList slId = new StringList();
	boolean isContextPushed = false; 
		try{  			
			StringList slAlternateInfoSelect = new StringList();			
			String sId = DomainConstants.EMPTY_STRING;
			String sCurrent = DomainConstants.EMPTY_STRING;
			slAlternateInfoSelect.add(DomainConstants.SELECT_ID);
			slAlternateInfoSelect.add(DomainConstants.SELECT_CURRENT);
			// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true; 
			// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
			DomainObject domPartObj= DomainObject.newInstance(context,strobjectId);
			MapList mlAlternateList= domPartObj.getRelatedObjects(context, //context
										pgV3Constants.RELATIONSHIP_ALTERNATE, //relationship
										DomainConstants.QUERY_WILDCARD, //type
										slAlternateInfoSelect, //object select
										null, //rel Select
										true, //get To
										false, //get From
										(short)1, //recurse level
										null, //object where clause
										null, //rel where clause
										0); //limit
			if(null !=mlAlternateList && !mlAlternateList.isEmpty()){
				for(int i=0; i<mlAlternateList.size();i++){
					Map<String,Object> mAlternateDetails = (Map<String,Object>)mlAlternateList.get(i);
					sId = (String) mAlternateDetails.get(DomainConstants.SELECT_ID);
					sCurrent = (String) mAlternateDetails.get(DomainConstants.SELECT_CURRENT);
					 					
					// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
					if(SPECREADER.equalsIgnoreCase(strOriginatingSource)){ 					
						if(sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE) || sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASED)) {
							if (isValidForProcessing(strInputState,sCurrent)) {
								slId.add(ALTERNATE_PREFIX+DELIMITER+sId);
							}
						}
					}else {
						if (isValidForProcessing(strInputState,sCurrent)) {
							slId.add(ALTERNATE_PREFIX+DELIMITER+sId);
						}
					}
					// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			outLog.print("Exception in  getAlternate "+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return slId;
	}


/*
 * getMakeToProduce- This method is to get MakeToProduce Ids
 * @return MapList
	 */
private StringList getMakeToProduce(Context context, String strobjectId, String strInputPartState) throws FrameworkException{
		
	StringList slId = new StringList();
	String sId = DomainConstants.EMPTY_STRING;
	String sCurrent = DomainConstants.EMPTY_STRING;
	boolean isContextPushed = false; 
		try{  	
			StringList slInfoSelect = new StringList();	
			slInfoSelect.add(DomainConstants.SELECT_ID);
			slInfoSelect.add(DomainConstants.SELECT_CURRENT);
			// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true; 
			// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
			DomainObject domPartObj= DomainObject.newInstance(context,strobjectId);
			MapList mlList= domPartObj.getRelatedObjects(context, //context
										pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL, //relationship
										DomainConstants.QUERY_WILDCARD, //type
										slInfoSelect, //object select
										null, //rel Select
										true, //get To
										false, //get From
										(short)1, //recurse level
										null, //object where clause
										null, //rel where clause
										0); //limit
			if(null !=mlList && !mlList.isEmpty()){
				for(int i=0; i<mlList.size();i++){
					Map<String,Object> mMaketoProduce = (Map<String,Object>)mlList.get(i);
					sId = (String) mMaketoProduce.get(DomainConstants.SELECT_ID);
					sCurrent = (String) mMaketoProduce.get(DomainConstants.SELECT_CURRENT);
					// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
					if(SPECREADER.equalsIgnoreCase(strOriginatingSource)){
						if(sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE) || sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASED)) {
							if (isValidForProcessing(strInputPartState,sCurrent)) {
								slId.add(MAKETOPRODUCE_PREFIX+DELIMITER+sId);
							}
						}
					}else {
						if (isValidForProcessing(strInputPartState,sCurrent)) {
							slId.add(MAKETOPRODUCE_PREFIX+DELIMITER+sId);
						}
					}
					// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			outLog.print("Exception in  getMakeToProduce "+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return slId;
	}


/*
 * getFBOMSubstitute- This method is to get FBOM Substitute Ids
 * @return MapList
 */
private StringList getFBOMSubstitute(Context context, String strobjectId,String strInputState) throws FrameworkException{

	String sId = DomainConstants.EMPTY_STRING;
	String sCurrent = DomainConstants.EMPTY_STRING;
	String sType = DomainConstants.EMPTY_STRING;
	StringList slId = new StringList();
	boolean isContextPushed = false; 
	try{  	
		StringList slObjInfoSelect = new StringList();
		StringList slPartTempId = new StringList();
		StringList slPartTempState = new StringList();
		StringList slPartTempType = new StringList();
		//Defect 41984 Only list parents of substituted part
		slObjInfoSelect.add("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELID));
		slObjInfoSelect.add("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELCURRENT));
		slObjInfoSelect.add("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELTYPE));
				
		Pattern relPattern = new Pattern(CPNRelationship.FBOM.getRelationship(context));
		Pattern typePattern = new Pattern(CPNType.PARENTSUB.getType(context));
		// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
		ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
		isContextPushed = true; 
		// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
		DomainObject domPartObj= DomainObject.newInstance(context,strobjectId); 
		MapList mlSubstituteList = domPartObj.getRelatedObjects(context, //context
				relPattern.getPattern(), //type
				typePattern.getPattern(), //rel
				slObjInfoSelect, //object select
				null, //rel select
				true, //get To
				false, //get From
				(short)1, //recurse level
				null, //object where clause
				null, //rel where clause
				0); //limit
			if(null !=mlSubstituteList && !mlSubstituteList.isEmpty()){
				for(int i=0; i<mlSubstituteList.size();i++){
					Map<String,Object> mFBOMSub = (Map<String,Object>)mlSubstituteList.get(i);
					if(mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELID)) instanceof StringList){
						slPartTempId = (StringList)mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELID));
						slPartTempState = (StringList)mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELCURRENT));
						slPartTempType = (StringList)mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELTYPE));
						
						if(slPartTempId!=null){						
							for(int j=0;j<slPartTempId.size();j++){
								sId=slPartTempId.get(j);
								sCurrent=slPartTempState.get(j);
								sType=slPartTempType.get(j);
								// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
								if(SPECREADER.equalsIgnoreCase(strOriginatingSource)){
									if(sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE) || sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASED)) {
										if (TYPEPGPHASE.equals(sType) || TYPEFORMULATIONPHASE.equals(sType)|| isValidForProcessing(strInputState,sCurrent)) {
											slId.add(FBOM_PREFIX+DELIMITER+sId);
										}
									}
								}else {
									if (TYPEPGPHASE.equals(sType) || TYPEFORMULATIONPHASE.equals(sType)|| isValidForProcessing(strInputState,sCurrent)) {
										slId.add(FBOM_PREFIX+DELIMITER+sId);
									}
								}
								// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
							}
						}
					}
					else if(mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELID))!=null){
						sId = (String) mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELID));
						sCurrent = (String) mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELCURRENT));
						sType = (String) mFBOMSub.get("to[".concat(pgV3Constants.RELATIONSHIP_PLBOMSUBSTITUTE).concat(SUBFROMRELTYPE));
						
						// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - Start
						if(SPECREADER.equalsIgnoreCase(strOriginatingSource)){
							if(sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE) || sCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASED)) {
								if (TYPEPGPHASE.equals(sType) || TYPEFORMULATIONPHASE.equals(sType) || isValidForProcessing(strInputState,sCurrent)) {
									slId.add(FBOM_PREFIX+DELIMITER+sId);
								}
							}
						}else {
							if (TYPEPGPHASE.equals(sType) || TYPEFORMULATIONPHASE.equals(sType) || isValidForProcessing(strInputState,sCurrent)) {
								slId.add(FBOM_PREFIX+DELIMITER+sId);
							}
						}
						// Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 54767 - End
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			outLog.print("Exception in  getFBOMSubstitute "+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return slId;
	}	
	
	/*
	 * getHighestObsoleteRev- This method is to get Highest Rev of Obsolete Parts
	 * @returns MapList
	 */
	private StringList getHighestObsoleteRev(MapList mlRelatedParentsAll, StringList slPartsToExpand){
		try{
			if(null != mlRelatedParentsAll && !mlRelatedParentsAll.isEmpty()){
				mlRelatedParentsAll.sort(DomainConstants.SELECT_NAME, DESCENDING, STRING);
				slPartsToExpand = getHighestObsoleteRevProcess(mlRelatedParentsAll,slPartsToExpand);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return slPartsToExpand;
	}	
	
	/*
	 * getHighestObsoleteRevProcess- This method is to get process the Obsolete Parts Id to get Highest Revision
	 * @returns MapList
	 */
	private StringList getHighestObsoleteRevProcess(MapList mlRelatedParentsAll, StringList slPartsToExpand){
		MapList mlRelatedParents = new MapList();
		MapList mlMoreThanOneRevision = new MapList();
		StringList slNames = new StringList();
		StringList slRelease = new StringList();
		try {
			for(int i=0; i<mlRelatedParentsAll.size();i++){
				Map<String, String> mRelatedParents = (Map<String, String>)mlRelatedParentsAll.get(i);
				String strParentName = mRelatedParents.get(DomainConstants.SELECT_NAME);
				String strParentState = mRelatedParents.get(DomainConstants.SELECT_CURRENT);
				String strParentId = mRelatedParents.get(DomainConstants.SELECT_ID);
				if(STATE_OBSOLETE.contains(strParentState)){
					if(!slNames.contains(strParentName)){
						slNames.add(strParentName);
						Map<String,String> mHighestObsoleteRev = getLatestRev(mlMoreThanOneRevision,slRelease);	
						if(null != mHighestObsoleteRev && !mHighestObsoleteRev.isEmpty()){
							String sHighestRev = mHighestObsoleteRev.get(DomainConstants.SELECT_ID);
							slPartsToExpand.add(sHighestRev);
							mlRelatedParents.add(mHighestObsoleteRev);
							mlMoreThanOneRevision.clear();
						} 
						mlMoreThanOneRevision.add(mRelatedParents);
					} else{
					mlMoreThanOneRevision.add(mRelatedParents);
					}
				}else if(STATE_RELEASE.contains(strParentState)){
					slRelease.add(strParentName);	
					slPartsToExpand.add(strParentId);
					mlRelatedParents.add(mRelatedParents);
				}
			}
			Map<String,String> mHighestObsoleteRev = getLatestRev(mlMoreThanOneRevision,slRelease);
			if(null != mHighestObsoleteRev){
				String sHighestRev = mHighestObsoleteRev.get(DomainConstants.SELECT_ID);
				slPartsToExpand.add(sHighestRev);
				mlRelatedParents.add(mHighestObsoleteRev);
			}
		}catch(Exception e){
		e.printStackTrace();
		outLog.print("Exception in  getHighestObsoleteRevProcess "+e+"\n");
		outLog.flush();
		}
		return slPartsToExpand;
	}
	
	
	/*
	 * getLatestRev- This method is to get Latest Rev of Obsolete Parts
	 * @returns Map
	 */
	private Map<String, String> getLatestRev(MapList mlMoreThanOneRevision,StringList slRelease){
		Map<String, String> mHighestObsoleteRev = new HashMap<>();
		boolean bFlag = false;
		if(null != mlMoreThanOneRevision && !mlMoreThanOneRevision.isEmpty()){
			mlMoreThanOneRevision.sort(DomainConstants.SELECT_REVISION, DESCENDING, STRING);
			for(int j=0; j<mlMoreThanOneRevision.size();j++){
				if(j==0) {
					mHighestObsoleteRev = (Map) mlMoreThanOneRevision.get(j);
					String strName = mHighestObsoleteRev.get(DomainConstants.SELECT_NAME);
					if(!slRelease.contains(strName)){
						bFlag = true;
					}
				}
			}
		} 
		if(bFlag) {
		return mHighestObsoleteRev;
		} else {
			return null;
		}
	}


	/*
	* This method is used to get the Hyperlink for Name columns in report
	* @return void
	*/
private void getHyperlink(Context context,Cell cell, XSSFWorkbook workbook, String strValue, String strId) throws Exception{ 
		//Modified the code for 22x.03 August 2023 CW Requirement 41567 - Start
		String strURL = "";
		String strNewURL = "";
		//Modified the code for 22x.03 August 2023 CW Requirement 41567 - End
		CreationHelper createHelper = workbook.getCreationHelper();
		XSSFCellStyle style = workbook.createCellStyle();
		Font hlinkfont = workbook.createFont();
		hlinkfont.setUnderline(Font.U_SINGLE);
		hlinkfont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
		XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(HyperlinkType.URL);
		 if(UIUtil.isNotNullAndNotEmpty(strId) && UIUtil.isNotNullAndNotEmpty(strValue)){
			//Modified the code for 22x.03 August 2023 CW Requirement 41567 - Start
			if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && ENOVIA.equalsIgnoreCase(strOriginatingSource)) {
				strURL = EnoviaResourceBundle.getProperty(context,EMXCPN, context.getLocale(), "emxCPN.BaseURL");
				strNewURL = String.valueOf(strURL).trim();
				link.setAddress(strNewURL + "?objectId=" + strId);
				System.out.println(strNewURL + "?objectId=" + strId);
			} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
				strURL = getSpecReaderURL(context);
				strNewURL = String.valueOf(strURL).trim();
				link.setAddress(strNewURL + strValue);
				System.out.println(strNewURL + strValue);
			}
		 	//Modified the code for 22x.03 August 2023 CW Requirement 41567 - End
			 cell.setCellValue(strValue);
			 cell.setHyperlink(link);
			 style.setFont(hlinkfont);
			 cell.setCellStyle(style);
		 } else {
			 cell.setCellValue(DomainConstants.EMPTY_STRING);
		 }
	}
	

	/*
	* This method is used to get the the Asterisk string for level
	* @return String
	*/
	private String getAsterisk(int iLevel) {
	StringBuilder strAsteriskBuilder = new StringBuilder();
		for(int l=0; l<iLevel; l++){
			strAsteriskBuilder.append("* ");
		}
		return strAsteriskBuilder.toString();
	}
	
	/**
	* This method removed duplicates from a stringlist
	* @param StringList
	* @return StringList
	*/
	private StringList removeDuplicates(StringList slList) 
	{ 
	    StringList slNewList = new StringList(); 
	    String tempVal = DomainConstants.EMPTY_STRING;
		Iterator<String> slListItr = slList.iterator();
		while(slListItr.hasNext()) 
		{
			tempVal = slListItr.next();
			if(!slNewList.contains(tempVal))
			{
				slNewList.add(tempVal);
	        } 
	    } 
	    // return the new list 
	    return slNewList; 
	}
	
	/**This method is used to check if report object exist or not Defect id 41121 
	 * @param context
	 * @return boolean
	 * @throws FrameworkException
	 */
	private boolean isReportExist(Context context, String strReportObjectId) throws FrameworkException{
		boolean bReturn = true;
		try{
			DomainObject dObjPart = DomainObject.newInstance(context, strReportObjectId); 
		}catch (Exception e){
			bReturn = false;
			outLog.print("Report Object does not exist: "+e+"\n");
			outLog.flush(); 
		}
		return bReturn;
	}	
	
	/**
	 * Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567
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
		String strSubLine = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.SubjectLine");
		
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
	
	String strMessageBody = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.BodyMessage");
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
			strUrlPath = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.Enovia.DSMReportLink");
			sbUrl.append(strUrl);
			sbUrl.append(strUrlPath);
		} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
			strUrl = getSpecReaderURL(context);
			strUrl = String.valueOf(strUrl).trim();
			 strUrlPath = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.SpecReader.DSMReportLink");
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