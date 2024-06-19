package com.pg.dsm.custom;
/*   pgPartLibraryReport.java - DSM Report Request For Part Library Report Download
     Author:DSM L4 
     Copyright (c) 2019
     All Rights Reserved.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import java.util.Set;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dassault_systemes.enovia.changeaction.factory.ChangeActionFactory;
import com.dassault_systemes.enovia.changeaction.interfaces.IChangeAction;
import com.dassault_systemes.enovia.changeaction.interfaces.IChangeActionServices;
import com.dassault_systemes.enovia.changeaction.interfaces.IOperation;
import com.dassault_systemes.enovia.changeaction.interfaces.IRealizedChange;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

//Added for Hyperlink Requirement : 33630 - starts 
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.hssf.util.HSSFColor;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
//Added for defect id 41121 Starts
import com.matrixone.apps.domain.util.FrameworkException;
//Added for defect id 41121 Ends
//Added for Hyperlink Requirement : 33630 - ends 
import java.io.PrintWriter;
import java.nio.file.Files;

import com.matrixone.apps.domain.util.StringUtil;

public class pgPartLibraryReport implements DomainConstants {
	public pgPartLibraryReport(Context context, String[] args) {
	}
	//MapList mlAllPartLibDetails = new MapList();
	//Added the code for 29575	The Part Family Extract Shall not populate data for Inherit Status column if isLeaf=FALSE : Starts
	private static final String KEY_NO = "NO";
	private static final String KEY_ISLEAF = "isLeaf";
	public static final String ATTRIBUTE_PG_PFCHANGE = PropertyUtil.getSchemaProperty("attribute_pgPFChange");
	//Added the code for 29575	The Part Family Extract Shall not populate data for Inherit Status column if isLeaf=FALSE : Ends
	private static final String SYMBOL_PIPE = "|";
	private static final String ATTRIBUTE_ORIGINATOR = PropertyUtil.getSchemaProperty("attribute_Originator");
	//Added code for the Defect- 29927 : Include Part Family description and Part Family Title to the report -Starts
	public static final String ATTRIBUTE_TITLE = PropertyUtil.getSchemaProperty("attribute_Title");
	//Added code for the Defect- 29927 : Include Part Family description and Part Family Title to the report -Ends
	
	//Added the code for Defect Id : 29944 - Value of state in part library report doesn't match the value in UI - Starts
	public static final String STATE_PRELIMINARY = "Preliminary";
	public static final String STATE_REVIEW = "Review";
	public static final String STATE_IN_WORK = "In Work";
	public static final String STATE_APPROVED = "Approved";
	public static final String STATE_FROZEN = "Frozen";
	public static final String STATE_RELEASE = "Release";
	//Added for Hyperlink Requirement : 33630 - starts 
	private static final String HYPERLINK = "Hyperlink:|";
	private static final String HYPERLINK_PIPE = "|";
	private static final String HYPERLINK_COMPARE = "Hyperlink:";
	private static final String FROMMID = "].frommid[";
	private static final String DATE_YMDHS = "yyyy-MM-dd HH:mm:ss";
	private PrintWriter outLog = null;
	private static final String CATALINA_BASE="catalina.base";
	private static final String PATH_SEPARATOR="/";	
	//Added for Hyperlink Requirement : 33630 - ends
	//Added for Part Library Requirement 36714 -starts
	private static final String STRINGRESOURCEFILE = "emxCPNStringResource"; 
	private static final String CPNFILE = "emxCPN"; 
	//Added for Part Library Requirement 36714 -ends
	//Added code for 2018x.6 Requirement id 36711 Ability to generate Part Library report with-without hyperlink Starts
	private static final String HYPERLINKASINPUT = "HyperlinkAsInput";
	//Added code for 2018x.6 Requirement id 36711 Ability to generate Part Library report with-without hyperlink Ends
	private static final String REALTIMEPROCESS = "RealTimeProcess"; 
	private int rowCount;
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	private static final String STRFROMEMAILID = "strFromEmailId";
	private static final String STRTOEMAILID = "strToEmailId";
	private static final String STRSUBJECT = "strSubject";
	private static final String STRMESSAGEBODY = "strMessageBody";
	private static final String STR_PERSON_PLM_ADMIN = "PLM Admin";
	private static final String STR_COMMON="common";
	private static final String STR_SYMBOL_BRACKETS="{0}";
	private static final String STR_TEXT_HTML="text/html";
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	
	/**
     * this method is to generate the Excel Sheet for the manual entry from the user side
     * @param context is the matrix context
     * @param args
     * @return void
     * @throws Exception
     */
	public void generatePartLibrartyReportEntry(Context context, String[] args) throws Exception {
		HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
		String strPartLibNames = (String) hmArgs.get("GCAS");
		String strReportFileName = (String) hmArgs.get("ReportFileName");
		String strReportObjectId = (String) hmArgs.get("ReportObjectId");
		String strUserName = (String) hmArgs.get("UserName"); 
		String strHyperlink = (String) hmArgs.get(HYPERLINKASINPUT);
		String strRealTimeProcess = (String) hmArgs.get(REALTIMEPROCESS);
		if(UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartLibNames))){
	    	StringList slPartLibNames = StringUtil.split(strPartLibNames, pgV3Constants.SYMBOL_COMMA);
	    	DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
			doObj.promote(context);
	    	generatePartLibrartyReport(context,strUserName,slPartLibNames,strReportFileName,strReportObjectId,strHyperlink,strRealTimeProcess);
	    	}
	   }
	/**
     * this method is to generate the Excel Sheet for the manual entry from the user side 
     * @param context is the matrix context
     * @param args
     * @return void
     * @throws Exception
     */
	//Modified for Part Library Requirement 36714 -starts
	public void generatePartLibrartyReport(Context context, String strUserName,StringList slPartLibNames,String strReportFileName,String strReportObjectId,String strHyperlink, String strRealTimeProcess)throws Exception{
		// Part Library Report Folder creation :Starts
		StringBuilder sbPartLibraryReportFolder = new StringBuilder();
		String strDirectoryNotCreated = "Could not create directory ";
		StringBuilder sbLogFolder = new StringBuilder();
		//Added code for 2018x.6 Requirement id 36712 Generate Part Library report directly Starts
		String configLOGFilePath = DomainConstants.EMPTY_STRING;
		String configFilePath = DomainConstants.EMPTY_STRING;
		if(UIUtil.isNotNullAndNotEmpty(strRealTimeProcess) && pgV3Constants.TRUE.equalsIgnoreCase(strRealTimeProcess)){
			configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNFILE, context.getLocale(),"emxCPN.DSMReport.Log.FilePath");
			configFilePath = EnoviaResourceBundle.getProperty(context, CPNFILE, context.getLocale(),"emxCPN.PartLibraryReport.Worksheet.FilePath");
		}
		else{
			configLOGFilePath = EnoviaResourceBundle.getProperty(context, CPNFILE, context.getLocale(),"emxCPN.DSMReportCTRLMJob.Log.FilePath");
			configFilePath = EnoviaResourceBundle.getProperty(context, CPNFILE, context.getLocale(),"emxCPN.PartLibraryReportCTRLMJob.Worksheet.FilePath");
		}
		//Added code for 2018x.6 Requirement id 36712 Generate Part Library report directly Ends
	    sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);	   
		File fLogFolder = new File(sbLogFolder.toString());
		if (!fLogFolder.exists() && !fLogFolder.mkdirs())  {
			throw new IOException(strDirectoryNotCreated + fLogFolder);
		}
		//Log file for Report
		String strJVM = getJVMInstance(); 			
		String strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());	  			
		outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString()+ File.separator  +"PartLibraryLog.log",true));
		outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+" | JVM:"+strJVM+"--------\n");
		outLog.flush(); 
		sbPartLibraryReportFolder.append(configFilePath).append(java.io.File.separator);
		File fPartLibraryReportFolder = new File(sbPartLibraryReportFolder.toString());
		if (!fPartLibraryReportFolder.exists() && !fPartLibraryReportFolder.mkdirs())  {
			throw new IOException(strDirectoryNotCreated + fPartLibraryReportFolder);
		}
		String strReportPath = fPartLibraryReportFolder.toString(); 
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheetPartLib = createSheet(context, workbook); 
		Map<String,Object> mExcelDetail = new HashMap<>();
		mExcelDetail.put("Workbook", workbook);
		mExcelDetail.put("SheetName", sheetPartLib);
		mExcelDetail.put("HyperLink", strHyperlink);
		//Added for defect id 41121 Starts
		mExcelDetail.put("ReportId", strReportObjectId);
		//Added for defect id 41121 Ends
		String strParentPath = DomainConstants.EMPTY_STRING;
		String strObjectType = DomainConstants.EMPTY_STRING;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectId = DomainConstants.EMPTY_STRING;
		String strPartLibrary = DomainConstants.EMPTY_STRING;
		StringList slParentPath = new StringList();
		MapList mlPartLibDetails = new MapList();
		Map<String,String> mPartLibDetails  = new HashMap<>();
		String strPartLibObjId = DomainConstants.EMPTY_STRING;
		String strPartLibName = DomainConstants.EMPTY_STRING;
		String strPartLibRevision = DomainConstants.EMPTY_STRING;
		String strPartLibOriginator = DomainConstants.EMPTY_STRING;
		boolean bFirstCall = true;
		if(null != slPartLibNames && !slPartLibNames.isEmpty()){
			for(Object IndividualPartLib : slPartLibNames){
			 	outLog.print("Library: "+IndividualPartLib.toString()+"\n");
			 	outLog.print("Report Object Id: "+strReportObjectId+"\n");
			 	outLog.flush(); 
				if(UIUtil.isNotNullAndNotEmpty(IndividualPartLib.toString()) && IndividualPartLib.toString().contains(pgV3Constants.SYMBOL_PIPE)){
					String[] strSplittedValue = IndividualPartLib.toString().split("\\|",-1); 
					strObjectName  = strSplittedValue[(strSplittedValue.length)-1];
					strObjectName=String.valueOf(strObjectName).trim();
					strPartLibrary= strSplittedValue[(strSplittedValue.length)-2];
					strPartLibrary=String.valueOf(strPartLibrary).trim();
					strObjectType = pgV3Constants.TYPE_PARTFAMILY;
				}else{
					strObjectType = PropertyUtil.getSchemaProperty(context,"type_PartLibrary");
					strObjectName = IndividualPartLib.toString();
					strObjectName=String.valueOf(strObjectName).trim();
				}
				mlPartLibDetails = getObject(context,strObjectType,strObjectName);
				if(null !=mlPartLibDetails && !mlPartLibDetails.isEmpty()){
					for(int i =0;i<mlPartLibDetails.size();i++){
						mPartLibDetails = (Map) mlPartLibDetails.get(i);
						strObjectId = mPartLibDetails.get(DomainConstants.SELECT_ID);
						slParentPath.clear();
						strParentPath = DomainConstants.EMPTY_STRING;
						if(pgV3Constants.TYPE_PARTFAMILY.equalsIgnoreCase(strObjectType)){
							slParentPath = getParentPath(context,strPartLibrary,strObjectId,slParentPath,false);
							if(null != slParentPath && !slParentPath.isEmpty()){
								strParentPath = slParentPath.toString().replace("[", "").replace("]", "").replace(pgV3Constants.SYMBOL_COMMA, pgV3Constants.SYMBOL_PIPE);
							}
						}
						strPartLibObjId = mPartLibDetails.get(DomainConstants.SELECT_ID)+pgV3Constants.SYMBOL_PIPE+strObjectType;		
						//Start modified for 22x Changes for Material Library/Family Auto Name Change
						strPartLibName = mPartLibDetails.get(SELECT_ATTRIBUTE_TITLE);
						//End modified for 22x Changes for Material Library/Family Auto Name Change
						strPartLibRevision = mPartLibDetails.get(DomainConstants.SELECT_REVISION);
					    strPartLibOriginator = mPartLibDetails.get(pgV3Constants.SELECT_ATTRIBUTE_ORIGINATOR);
					   if(UIUtil.isNotNullAndNotEmpty(strParentPath)){
						   strPartLibName=strParentPath;
					   }
					   if(UIUtil.isNotNullAndNotEmpty(strParentPath) || (UIUtil.isNullOrEmpty(strParentPath) && PropertyUtil.getSchemaProperty(context,"type_PartLibrary").equals(strObjectType))){
						   expandPartFamily(context,strPartLibObjId,strPartLibName,strPartLibRevision,mExcelDetail,bFirstCall,rowCount);
							outLog.print("Expand completed for: "+strUserName+": "+IndividualPartLib.toString()+"|" +strReportFileName+ "------\n");
							outLog.print("---------\n");
							outLog.flush();
					   }
					}
				}
			}
		}	
		Map<String,String> mpReportPathDetails = getReportName(context, strReportFileName, strUserName,strReportPath);
		String strFullPath=mpReportPathDetails.get("FullPath");
		String strReportName=mpReportPathDetails.get("ReportName");
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		checkinExcelWorkbook(context,workbook,strFullPath,strReportPath,strReportName,strReportObjectId,strUserName);
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
		String strEndTime = null; 
		strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());		
		outLog.print("Report completed for: "+strUserName+": -------Time completed: "+strEndTime+"-------\n");	
		outLog.print("-------\n");
		outLog.flush();
		outLog.close();
	}
	
	/**
     * this method is to get the Part Families which are connected to the Part Library
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     * @throws Exception
     */
	public void expandPartFamily(Context context,String strPartObjId, String strinPath,String strPartRevision,Map<String,Object>mExcelDetail,boolean bFirstCall, int iRowCount) throws Exception{
		String strObjectType = DomainConstants.EMPTY_STRING;
		//Added for defect id 41121 Starts
		String strReportObjectId = (String)mExcelDetail.get("ReportId");
		boolean isReport = true;
		//Added for defect id 41121 Ends
		if(strPartObjId.contains(pgV3Constants.SYMBOL_PIPE)){
			StringList slSplittedValue=StringUtil.split(strPartObjId, pgV3Constants.SYMBOL_PIPE);
			strPartObjId=slSplittedValue.get(0);
			strPartObjId=String.valueOf(strPartObjId).trim();
			strObjectType=slSplittedValue.get(1);
			strObjectType=String.valueOf(strObjectType).trim();
		}
		DomainObject domObj = DomainObject.newInstance(context, strPartObjId);
		StringList slSelectObj = new StringList();				
		slSelectObj.add(DomainConstants.SELECT_NAME);
		slSelectObj.add(DomainConstants.SELECT_ID);
		slSelectObj.add("attribute["+PropertyUtil.getSchemaProperty(context,"attribute_pgIsLeafLevel")+"]");
		slSelectObj.add(DomainConstants.SELECT_OWNER);
		slSelectObj.add(DomainConstants.SELECT_REVISION);
		//Start modified for 22x Changes for Material Library/Family Auto Name Change
		slSelectObj.add(SELECT_ATTRIBUTE_TITLE);
		//End modified for 22x Changes for Material Library/Family Auto Name Change
		Map<String,String> mPartFamDetails = new HashMap<>();
		if(pgV3Constants.TYPE_PARTFAMILY.equalsIgnoreCase(strObjectType) && bFirstCall){
			Map<String,String> mapCAInfo = domObj.getInfo(context, slSelectObj);
			bFirstCall = false;
			//Added for defect id 41121 Starts
			isReport = isReportExist(context,strReportObjectId);
			if(isReport) {
				//Added for defect id 41121 Ends
				expandPartFamilyProcess(context,mapCAInfo,strinPath,strPartRevision,mExcelDetail,bFirstCall,iRowCount);
			}	
		}else{
			MapList mlPartFamDetails = domObj.getRelatedObjects(context,//context
					pgV3Constants.RELATIONSHIP_SUBCLASS ,//rel pattern
					pgV3Constants.TYPE_PARTFAMILY,//type pattern
					slSelectObj, //obj select
					null,//rel select
					false,//get To
					true,//get From
					(short)1,//recurse level
					null,//obj where clause
					null,//rel where clause
					0);//limit
			if((null != mlPartFamDetails) && (!mlPartFamDetails.isEmpty())){
				for (int i = 0;i<mlPartFamDetails.size();i++) {
					mPartFamDetails	= (Map<String,String>)mlPartFamDetails.get(i);
					//Added for defect id 41121 Starts
					isReport = isReportExist(context,strReportObjectId);
					if(isReport) {
						expandPartFamilyProcess(context,mPartFamDetails,strinPath,strPartRevision,mExcelDetail,bFirstCall,iRowCount);
					} else {
						break;
					}
					//Added for defect id 41121 Ends
				}
			}
		}
	}
	
	/**
     * this method is to get the Part Families which are connected to the Part Library
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     * @throws Exception
     */
	private void expandPartFamilyProcess(Context context,Map<String,String> mPartFamDetails, String strinPath,String strPartRevision,Map<String,Object>mExcelDetail,boolean bFirstCall, int iRowCount) throws Exception{
		StringBuilder sbPartFamPath	= new StringBuilder();
		StringBuilder sbPartRevisionPath = new StringBuilder();
		sbPartFamPath.append(pgV3Constants.SYMBOL_PIPE).append(strinPath);
		sbPartRevisionPath.append(pgV3Constants.SYMBOL_PIPE).append(strPartRevision);
		String strPartFamObjId = mPartFamDetails.get(DomainConstants.SELECT_ID);
		String strPartFamName = mPartFamDetails.get(DomainConstants.SELECT_NAME);
		//Start modified for 22x Changes for Material Library/Family Auto Name Change
		String strPartFamTitle = mPartFamDetails.get(SELECT_ATTRIBUTE_TITLE);
		//End modified for 22x Changes for Material Library/Family Auto Name Change
	    String strPartFamRevision = mPartFamDetails.get(DomainConstants.SELECT_REVISION);
		String strIsLeaf = mPartFamDetails.get("attribute["+PropertyUtil.getSchemaProperty(context,"attribute_pgIsLeafLevel")+"]");
		//Start modified for 22x Changes for Material Library/Family Auto Name Change
		sbPartFamPath.append(pgV3Constants.SYMBOL_PIPE).append(strPartFamTitle);
		//End modified for 22x Changes for Material Library/Family Auto Name Change
		sbPartRevisionPath.append(pgV3Constants.SYMBOL_PIPE).append(strPartFamRevision);
		String strPartFamPath = sbPartFamPath.toString();
		String strPartFamRevisions = sbPartRevisionPath.toString();
		String strPartFamCorrPath = strPartFamPath.substring(pgV3Constants.SYMBOL_PIPE.length());
		String strPartFamCorrRevisionsPath = strPartFamRevisions.substring(pgV3Constants.SYMBOL_PIPE.length());
		if(UIUtil.isNotNullAndNotEmpty(strIsLeaf)){
			if(pgV3Constants.TRUE.equalsIgnoreCase(strIsLeaf)){
				strIsLeaf = "YES";
			}
			else if(pgV3Constants.FALSE.equalsIgnoreCase(strIsLeaf)){
				strIsLeaf = "NO";
			}
		}		
		getPartsConnected(context,strPartFamObjId,strIsLeaf,strPartFamCorrPath,strPartFamCorrRevisionsPath,mExcelDetail);
		outLog.print("expandPartFamilyProcess...: "+strPartFamCorrPath+" "+strPartFamCorrPath+"-------\n");	
		outLog.flush();
		expandPartFamily(context,strPartFamObjId, strPartFamCorrPath,strPartFamCorrRevisionsPath,mExcelDetail,bFirstCall,iRowCount);
	}
	//Added for Part Library Requirement 36714 -ends
	
	/**
     * this method is to get the Part's which are connected to the Part Family
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     * @throws Exception
     */
	public void getPartsConnected(Context context,String strPartFamObjId, String strIsLeaf, String strPartFamCorrPath,String strPartFamCorrRevisionsPath,Map<String,Object>mExcelDetail) throws Exception{
		DomainObject domObj = null;
		Map<String,String> mpColumnDetails;
		domObj = DomainObject.newInstance(context, strPartFamObjId);
		StringList slSelectObj = new StringList();	
		String strObsoleteState = "Obsolete";
		String strNo = "NO";
		//The Part Library Report Shall include the Attribute "Part Type" of the parts assigned to the Part Family : Starts
		slSelectObj.add(DomainConstants.SELECT_TYPE);
		//The Part Library Report Shall include the Attribute "Part Type" of the parts assigned to the Part Family : Ends
		slSelectObj.add(DomainConstants.SELECT_NAME);
		slSelectObj.add(DomainConstants.SELECT_REVISION);
		slSelectObj.add(DomainConstants.SELECT_CURRENT);
		//slSelectObj.add(DomainConstants.SELECT_OWNER);
		slSelectObj.add(DomainConstants.SELECT_ID);
		slSelectObj.add("attribute[Title]");
		slSelectObj.add(DomainConstants.SELECT_DESCRIPTION);		 
		slSelectObj.add("to[Classified Item].frommid[Part Family Reference].torel.to.name");
		//slSelectObj.add("attribute[Change Action]");
		slSelectObj.add("attribute[pgPFChange]");
		slSelectObj.add("attribute[Reference Type]");
		//Added for DefectId : 29944- Starts
		slSelectObj.add(DomainConstants.SELECT_POLICY);
		String strTranslatedState = DomainConstants.EMPTY_STRING;
		String strWhereClause = "current!=Obsolete";
		//Added for DefectId : 29944- Ends
		Short sRecursionLevel = 1;
		MapList mlPartDetails = (MapList)domObj.getRelatedObjects(context,//context
				pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM ,//rel pattern
				DomainConstants.QUERY_WILDCARD , //type pattern
				slSelectObj,//obj sel
				null, //rel sel
				false,//get to
				true,//get from
				sRecursionLevel,//recurse level
				strWhereClause, //obj where clause
				null, //rel where clause
				0);//limit
		for (int i=0;i<mlPartDetails.size();i++) {
			Map mPartDetails = (Map)mlPartDetails.get(i);
			//Fetching non Obsolete Parts : Starts
			String strPartState = (String) mPartDetails.get("current");
			if(!strPartState.equalsIgnoreCase(strObsoleteState)){
			String strPartId = (String) mPartDetails.get("id");
			//Added for DefectId : 29944- Starts
			String strPartPolicy = (String) mPartDetails.get(DomainConstants.SELECT_POLICY);
			String strLanguage = context.getLocale().getLanguage();
			strTranslatedState = i18nNow.getStateI18NString(strPartPolicy, strPartState, strLanguage);
			//Added for DefectId : 29944- Ends
			if (UIUtil.isNotNullAndNotEmpty(strPartId)) {
				String [] args = new String [3];
				args[0] = strPartId;
				MapList mlChanges = new MapList();
				mlChanges = getConnectedChanges(context,args);
				if(mlChanges != null){
					for(Object caObject : mlChanges){
						Map caMap = (Map)caObject;
					    String sCAProposedName = (String) caMap.get("CAProposedName"); 
						String strCAProposedCurrent = (String) caMap.get("CAProposedCurrent");
						String strCAId = (String) caMap.get(DomainConstants.SELECT_ID);
						if(UIUtil.isNotNullAndNotEmpty(sCAProposedName)) {
							mPartDetails.put("CAProposedName", sCAProposedName);
							mPartDetails.put("CAProposedCurrent", strCAProposedCurrent);
							mPartDetails.put("strCAId", strCAId);							
						} else {
							mPartDetails.put("CAProposedName", "");
							mPartDetails.put("CAProposedCurrent", "");
							mPartDetails.put("strCAId", "");
						}	
					}
				}
			} 
			
			String strFinalOwnership = DomainConstants.EMPTY_STRING;
			String strFinalOwner = DomainConstants.EMPTY_STRING;
			String strPartFamilyFinalState = DomainConstants.EMPTY_STRING;
			String strPartFamilyFinalDescription = DomainConstants.EMPTY_STRING;
			String strPartFamilyFinalTitle = DomainConstants.EMPTY_STRING;
			mpColumnDetails = new HashMap<>();
			
			if(UIUtil.isNotNullAndNotEmpty(strPartFamCorrPath) && UIUtil.isNotNullAndNotEmpty(strPartFamCorrRevisionsPath)) {
				
				//strFinalOwnership = getCollaboratorsColumnInfo(context,strPartFamCorrPath,strPartFamCorrRevisionsPath);
				//strFinalOwner = getOwnerColumnInfo(context,strPartFamCorrPath,strPartFamCorrRevisionsPath); 
				//strPartFamilyFinalState = getPartFamilyStateColumnInfo(context,strPartFamCorrPath,strPartFamCorrRevisionsPath); 
				//strPartFamilyFinalDescription = getPartFamilyDescriptionColumnInfo(context,strPartFamCorrPath,strPartFamCorrRevisionsPath);
				//strPartFamilyFinalTitle = getPartFamilyTitleColumnInfo(context,strPartFamCorrPath,strPartFamCorrRevisionsPath);	
				//2018x.6 Updated code to inprove performance
				mpColumnDetails = getPartFamilyColumnInfo(context,strPartFamCorrPath,strPartFamCorrRevisionsPath);
				if(mpColumnDetails.get("Collaborator") != null)
					strFinalOwnership=mpColumnDetails.get("Collaborator");
				if(mpColumnDetails.get("Owner") != null)
					strFinalOwner=mpColumnDetails.get("Owner");
				if(mpColumnDetails.get("State") != null)
					strPartFamilyFinalState=mpColumnDetails.get("State");
				if(mpColumnDetails.get("Description") != null)
					strPartFamilyFinalDescription=mpColumnDetails.get("Description");
				if(mpColumnDetails.get("Title") != null)
					strPartFamilyFinalTitle=mpColumnDetails.get("Title");
			}
				mPartDetails.put("Path", strPartFamCorrPath);
				mPartDetails.put("Owner", strFinalOwner);
                mPartDetails.put("PartFamilyState", strPartFamilyFinalState);
                mPartDetails.put("Description", strPartFamilyFinalDescription);
				mPartDetails.put("Title", strPartFamilyFinalTitle);
			    mPartDetails.put("Collaborators", strFinalOwnership);
				mPartDetails.put("isLeaf", strIsLeaf);
				mPartDetails.put("current", strTranslatedState);
				//Update Worksheet
				rowCount = rowCount + 1;
				updateWorksheetPartLib(context,rowCount,mPartDetails,mExcelDetail);
			}
		}
	 }
	
	//Added for Part Library Requirement 36714 -starts
	 /** this method is to create Excel Sheet
	 * @returns void
	 * @throws IOException
	 */
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	private void checkinExcelWorkbook(Context context,XSSFWorkbook workbook,String strFullPath,String strReportPath,String strReportName,String strReportObjectId,String strUserName) throws IOException {
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
		try(FileOutputStream outputStream = new FileOutputStream(strFullPath)) {
			workbook.write(outputStream);
			// code to create the object and checking the .excel file in that object
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
			createDSMReportObject(context,strReportPath,strReportName,strReportObjectId,strUserName);
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
		}catch (Exception e) {
			outLog.print("Exception in  checkinExcelWorkbook: "+e+"\n");
			outLog.flush();
		}
	}
	//Added for Part Library Requirement 36714 -ends
	
	/**
     * this method is to update Excel Sheet with Part Library details
     * @param context is the matrix context
     * @param args has the required information
     * @return void
     */
 private void updateWorksheetPartLib(Context context, int rowCount, Map<String,String> mp,Map<String,Object>mExcelDetail) {
	 try {
			//Added code for 2018x.6 Requirement id 36711 Ability to generate Part Library report with-without hyperlink Starts
		 String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.PartLibraryReport.HyperlinkLimit");
		 int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll=0;
			//Added code for 2018x.6 Requirement id 36711 Ability to generate Part Library report with-without hyperlink Ends
			XSSFWorkbook workbook=(XSSFWorkbook)mExcelDetail.get("Workbook");
			XSSFSheet sheetPartLib=(XSSFSheet)mExcelDetail.get("SheetName");
			String strHyperlink= (String)mExcelDetail.get("HyperLink");
			XSSFCellStyle style = workbook.createCellStyle();			
			String strIsLeafValue =DomainConstants.EMPTY_STRING;			
			StringList slMasterPart=new StringList();
			XSSFRow row = sheetPartLib.createRow(rowCount);
			HashMap<Integer, String> hm = new HashMap<>();
			//Added for 2018x6.1 Dec CW defect 42332 Starts
			String sLanguage = context.getSession().getLanguage();
			//Added for 2018x6.1 Dec CW defect 42332 Ends
			iRowCountAll = iRowCountAll + 1;
			int columnCount = 0;
		    strIsLeafValue = (String) mp.get(KEY_ISLEAF);
			hm.put(0, mp.get("Path"));
			hm.put(1, mp.get("PartFamilyState"));
			hm.put(2, mp.get("Description"));
			hm.put(3, mp.get("Title"));
			hm.put(4, mp.get("Owner"));
			hm.put(5, mp.get("Collaborators"));
			hm.put(6, strIsLeafValue);
			if(strIsLeafValue.equals(KEY_NO)) {
				hm.put(7, DomainConstants.EMPTY_STRING);
			}else {
				hm.put(7, mp.get(AWLUtil.strcat("attribute[", ATTRIBUTE_PG_PFCHANGE, "]")));
			}
			//Added for 2018x6.1 Dec CW defect 42332 Starts
			String strDisplayType = mp.get(DomainConstants.SELECT_TYPE);
			strDisplayType = i18nNow.getTypeI18NString(strDisplayType, sLanguage);
			hm.put(8, strDisplayType);
			//Added for 2018x6.1 Dec CW defect 42332 Starts
			hm.put(9, HYPERLINK+mp.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+mp.get(DomainConstants.SELECT_ID));
			hm.put(10, mp.get("revision"));
			hm.put(11, mp.get("current"));
			hm.put(12, mp.get("attribute[Reference Type]"));
			hm.put(13, mp.get("attribute[Title]"));
			hm.put(14, mp.get("description"));
			hm.put(15, mp.get("to["+pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM+FROMMID+pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE+"].torel.to.name"));
			hm.put(16, HYPERLINK+mp.get("CAProposedName")+HYPERLINK_PIPE+mp.get("strCAId"));
				
			String strCellValue = DomainConstants.EMPTY_STRING;
			for(int j=0;j<hm.size();j++){	
				//Added for Defect - 40571 starts
				Cell cell = row.createCell(columnCount);
				columnCount++;
				//Added for Defect - 40571 ends
				//Added for Hyperlink Requirement : 33630 - starts
				strCellValue = hm.get(j);
				cell.setCellStyle(style);
				if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
					String[] strSplittedValue = strCellValue.split("\\|", -1); 
					String strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
					String strValue = strSplittedValue[(strSplittedValue.length)-2];
					if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
						getHyperlink(context,cell,workbook,strValue,strHyperlinkId);
					} else if(UIUtil.isNotNullAndNotEmpty(strValue)){
						cell.setCellStyle(style);
						cell.setCellValue(strValue);
					}
				}else {
					cell.setCellValue(strCellValue);	
				}
			}	
	} catch (Exception e) {
		outLog.print("Exception in  updateWorksheetPartLib: "+e+"\n");
		outLog.flush();
	}
}
	
	
	//Modified for Part Library Requirement 36714 -starts
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
			String strObjectName= EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.PartLibrary.ReportRequestName");
			String strNewObjectName = strObjectName+pgV3Constants.SYMBOL_UNDERSCORE+strContextUserName+pgV3Constants.SYMBOL_UNDERSCORE+System.currentTimeMillis();
			String strType = PropertyUtil.getSchemaProperty(context,"type_pgDSMReport");
			if(UIUtil.isNullOrEmpty(strReportObjectId))
			{
				String appName = FrameworkUtil.getTypeApplicationName(context, strType);
				DomainObject createBO = DomainObject.newInstance(context, strType, appName);
				BusinessObject bo = new BusinessObject(strType, strNewObjectName, DomainConstants.EMPTY_STRING, pgV3Constants.VAULT_ESERVICEPRODUCTION);
				if(!bo.exists(context))
				{
					createBO.createObject(context, strType, strNewObjectName,DomainConstants.EMPTY_STRING, PropertyUtil.getSchemaProperty(context,"policy_pgDSMReport"), pgV3Constants.VAULT_ESERVICEPRODUCTION);
					strReportObjectId = createBO.getObjectId(context);

				}
			}
			
			if(UIUtil.isNotNullAndNotEmpty(strReportObjectId)){
				DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
				outLog.print("Checking in report Start: "+strReportName+"\n");
				outLog.flush();
				doObj.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, PropertyUtil.getSchemaProperty(context,"format_generic"), strReportName, strReportPath);
				outLog.print("Checking in report Complete: "+strReportName+"\n");
				outLog.flush();
				doObj.promote(context);
				String sFullPath = strReportPath.concat(pgV3Constants.SYMBOL_FORWARD_SLASH).concat(strReportName); 
				File file = new File(sFullPath);				
				Files.delete(file.toPath());
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
				sendEmail(context, strReportName, strUserName);
				//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
			} else {
				throw new MatrixException("Creation of Object Failed");
			}
		} catch (Exception e) {
			outLog.print("Exception in  createDSMReportObject: "+e+"\n");
			outLog.flush();
		}
	}
	//Modified for Part Library Requirement 36714 -ends
	
	/**
     * this method is to get Change Actions connected to Parts
     * @param context is the matrix context
     * @param args has the required information
     * @return MapList
     * @throws Exception
     */
	public MapList getConnectedChanges(Context context, String []args) throws Exception { 
		MapList totalRelatedListCAs = new MapList();
		StringList stlObjectSelects = new StringList();
		stlObjectSelects.addElement(DomainConstants.SELECT_ID);
		stlObjectSelects.addElement(DomainConstants.SELECT_NAME);
		stlObjectSelects.addElement(DomainConstants.SELECT_CURRENT);
		Set changeActions = new HashSet();
		String strObjectId = args[0];
		BusinessObject busObjePart = new BusinessObject(strObjectId);
		List lBusoBject = new ArrayList(); 
		lBusoBject.add(busObjePart);
		String physicalId = DomainObject.newInstance(context, strObjectId).getInfo(context, "physicalid");
		List<String> lObjectId = new ArrayList();
		lObjectId.add(physicalId);
		MapList realizedCAMapList = new MapList();
		MapList proposedCAMapList = new MapList();
		ChangeActionFactory factory = new ChangeActionFactory();
		IChangeActionServices changeAction = factory.CreateChangeActionFactory();

		Map<String,Map<IChangeAction,List<IOperation>>> mapPrposedOperationAndCa = changeAction.getProposedOperationAndChangeActionFromIdList(context,lObjectId);
		Map<String,Map<IChangeAction, List<IRealizedChange>>> mapRealizedAndCaLinked = changeAction.getRealizedAndCaFromListObjects(context, lBusoBject, false, true, true);

		for(Entry<String, Map<IChangeAction, List<IOperation>>> mapOutput : mapPrposedOperationAndCa.entrySet()){
			for(Entry<IChangeAction,List<IOperation>> mapOutput2: mapOutput.getValue().entrySet()){
				List<IOperation> proposedList = mapOutput2.getValue();
				IChangeAction iChangeAction = mapOutput2.getKey();
				if(iChangeAction!= null){
					for(int index = 0; index<proposedList.size(); index++) {
						Map<String, String> proposedCAMap = new HashMap<String, String>();
						BusinessObject busChangeAction = iChangeAction.getCaBusinessObject();
						String strChangeActionPhysID = busChangeAction.getObjectId();
						DomainObject domChangeAction = DomainObject.newInstance(context, strChangeActionPhysID);
						Map mapCAInfo = domChangeAction.getInfo(context, stlObjectSelects);
						String strCurrent = (String) mapCAInfo.get(DomainConstants.SELECT_CURRENT);
						String strCAId = (String) mapCAInfo.get(DomainConstants.SELECT_ID);
						String strCAName = (String) mapCAInfo.get(DomainConstants.SELECT_NAME);
						proposedCAMap.put(DomainConstants.SELECT_ID, strCAId);
						proposedCAMap.put("CAProposedName", strCAName);
						proposedCAMap.put("CAProposedCurrent", strCurrent);
						
						if(!proposedCAMap.isEmpty()){
							proposedCAMapList.add(proposedCAMap);
						}

					}
				}
			}
		}

		for(Entry<String, Map<IChangeAction,List<IRealizedChange>>> mapOutput : mapRealizedAndCaLinked.entrySet()){
			for(Entry<IChangeAction,List<IRealizedChange>> mapOutput2: mapOutput.getValue().entrySet()){
				List<IRealizedChange> realizedList = mapOutput2.getValue();
				IChangeAction iChangeAction = mapOutput2.getKey();
				Map<String, String> realizedCAMap = new HashMap<String, String>();

				if(iChangeAction!= null){
					BusinessObject busChangeAction = iChangeAction.getCaBusinessObject();
					String strChangeActionPhysID = busChangeAction.getObjectId();
					DomainObject domChangeAction = DomainObject.newInstance(context, strChangeActionPhysID);
					Map mapCAInfo = domChangeAction.getInfo(context, stlObjectSelects);
					String strCurrent = (String) mapCAInfo.get(DomainConstants.SELECT_CURRENT);
					String strCAId = (String) mapCAInfo.get(DomainConstants.SELECT_ID);
					String strCAName = (String) mapCAInfo.get(DomainConstants.SELECT_NAME);
					realizedCAMap.put(DomainConstants.SELECT_ID, strCAId);
					realizedCAMap.put("CARealizedName", strCAName);
					realizedCAMap.put("CARealizedCurrent", strCurrent);
				}
				if(!realizedCAMap.isEmpty()){
					realizedCAMapList.add(realizedCAMap);
				}
			}
		}

		String sCAId = DomainConstants.EMPTY_STRING;
		String sCurrent = DomainConstants.EMPTY_STRING; 
		if(proposedCAMapList != null){
			for(Object caObject:proposedCAMapList){
				Map caProposedMap = (Map)caObject;
				sCAId = (String) caProposedMap.get(DomainConstants.SELECT_ID);
				if(!changeActions.contains(sCAId)){
					totalRelatedListCAs.add(caProposedMap);
					changeActions.add(sCAId);
				}
			}
		}
		if(realizedCAMapList != null){
			for(Object caObject:realizedCAMapList){
				Map caRealizedMap = (Map)caObject;
				sCAId = (String) caRealizedMap.get(DomainConstants.SELECT_ID);
				if(!changeActions.contains(sCAId)){
					totalRelatedListCAs.add(caRealizedMap);
					changeActions.add(sCAId);
				}
			}
		}
		return totalRelatedListCAs;
	}
	/**
	 * this method is to generate the Excel Sheet for the input .txt file which will be browsed by the user
	 * @param context is the matrix context
	 * @param args
	 * @return void
	 * @throws Exception
	 */
	/*
	public void generateFCRForInputFile(Context context, String args[]) throws Exception {
		FileReader fileReaderMQLGeneratedInputFile = null;
		BufferedReader buffReaderForMQLGeneratedInputFile = null;
	    String strPartLibName = DomainConstants.EMPTY_STRING;
		String line = "";
		HashMap hmArgs = (HashMap) JPO.unpackArgs(args);
		File file = (File) hmArgs.get("DSMReportFile");
		String strReportFileName = (String) hmArgs.get("ReportFileName");
		String strReportObjectId = (String) hmArgs.get("ReportObjectId");
		String strUserName = (String) hmArgs.get("UserName");
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
		buffReaderForMQLGeneratedInputFile.close();
		fileReaderMQLGeneratedInputFile.close();
		strPartLibName = finalVal;		 
		if(UIUtil.isNotNullAndNotEmpty(strUserName) && UIUtil.isNotNullAndNotEmpty(strPartLibName)){
			try { 
				HashMap hmArgs1 = new HashMap();
				hmArgs1.put("UserName",strUserName);
				hmArgs1.put("GCAS",strPartLibName);
				hmArgs1.put("ReportFileName",strReportFileName);
				hmArgs1.put("ReportObjectId",strReportObjectId);
				BackgroundProcess backgroundProcess = new BackgroundProcess();
				backgroundProcess.submitJob(context, "com.pg.dsm.custom.pgPartLibraryReport", "generatePartLibrartyReportEntry", JPO.packArgsRemote(hmArgs1) , (String)null);
			} catch(Exception ex) {
				ContextUtil.abortTransaction(context);
				ex.printStackTrace();
				throw ex;
			}
		}
	}
	*/
	/**
	 * this method is Used to get the Part Library Column Data in a single method - 2018x6 Enhancement
	 * @param context is the matrix context
	 * @param args has the required information
	 * @return String
	 */	
	private Map<String,String> getPartFamilyColumnInfo(Context context, String strPartFamCorrPath,String strPartFamCorrRevisionsPath) {
		String strProject = DomainConstants.EMPTY_STRING;
		String strOwnerShip = DomainConstants.EMPTY_STRING;
		StringList slFinalOwnership = new StringList();
		String strFinalOwnerShip = DomainConstants.EMPTY_STRING;
		StringBuffer sbOwnership = new StringBuffer();
		String strFinalOwnership = DomainConstants.EMPTY_STRING;
	
		String strOwner = DomainConstants.EMPTY_STRING;
		String strPartFamilyState = DomainConstants.EMPTY_STRING;
		String strPartFamilyDescription = DomainConstants.EMPTY_STRING;
		String strPartFamilyTitle = DomainConstants.EMPTY_STRING;
	    StringList slFinalList = StringUtil.split(strPartFamCorrPath, "|");
	    StringList slFinalListRev = StringUtil.split(strPartFamCorrRevisionsPath, "|");
	    Map mpPartColumn = new HashMap<>();
	    int slFinalListSize = slFinalList.size();
		int slFinalListRevisionSize = slFinalListRev.size();
		StringList slSelect = new StringList(1);
		Map mpPartSelects = null;
		DomainObject doObjLastLeaf = null;
		Map mpLastLeaf = null;
		try {
		StringList slBusSelectable = new StringList();	
		slBusSelectable.add(DomainConstants.SELECT_OWNER);
	    slBusSelectable.add(DomainConstants.SELECT_PROJECT);
		slBusSelectable.add("ownership");	
		DomainConstants.MULTI_VALUE_LIST.add("ownership");
		slBusSelectable.add(DomainConstants.SELECT_CURRENT);
	    slBusSelectable.add(DomainConstants.SELECT_DESCRIPTION);
	    slBusSelectable.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slSelect.add(DomainConstants.SELECT_ID);
		StringList slMulOwnerDetails = new StringList();
		String strLastLeaf = slFinalList.get(slFinalListSize-1);
		String strLastLeafRevison = slFinalListRev.get(slFinalListRevisionSize-1);
		//Start modified for 22x Changes for Material Library/Family Auto Name Change
		MapList mlLastLeafInfo = DomainObject.findObjects(context,DomainConstants.TYPE_PART_FAMILY,"*",strLastLeafRevison,"*",pgV3Constants.VAULT_ESERVICEPRODUCTION,"attribute[Title]=='"+strLastLeaf+"'",false,slSelect);
		//End modified for 22x Changes for Material Library/Family Auto Name Change
		if(!mlLastLeafInfo.isEmpty()) {
		mpLastLeaf = (Map)mlLastLeafInfo.get(0);
		String strLastLeafId = (String)mpLastLeaf.get(DomainConstants.SELECT_ID);
		if(UIUtil.isNotNullAndNotEmpty(strLastLeafId)) {
			doObjLastLeaf = DomainObject.newInstance(context, strLastLeafId);
			mpPartSelects = doObjLastLeaf.getInfo(context, slBusSelectable);	
			if(mpPartSelects !=null && !mpPartSelects.isEmpty()) {
				
			//start Collab	
			strProject = (String) mpPartSelects.get(DomainConstants.SELECT_PROJECT);
			if(mpPartSelects.get("ownership") instanceof StringList)
				slMulOwnerDetails.addAll((StringList)mpPartSelects.get("ownership"));
			else
				slMulOwnerDetails.add((String)mpPartSelects.get("ownership"));
			
			for(int iIndex = 0; iIndex < slMulOwnerDetails.size(); iIndex++) {
				strOwnerShip = slMulOwnerDetails.get(iIndex);
				slFinalOwnership = StringUtil.split(strOwnerShip, "|");
				if(slFinalOwnership != null && !slFinalOwnership.isEmpty()) {
					strFinalOwnerShip = slFinalOwnership.get(1);
					
					if(strFinalOwnerShip.endsWith("_PRJ")) {
						//There is no API available for it
						strFinalOwnerShip = MqlUtil.mqlCommand(context, "print role $1 select person dump", new String[] { strFinalOwnerShip });
						strFinalOwnerShip = PersonUtil.getFullName(context, strFinalOwnerShip);
					}
					sbOwnership.append(",").append(strFinalOwnerShip);
				}
			}
			sbOwnership.append(",").append(strProject);
			strFinalOwnership = sbOwnership.substring(",".length());
			mpPartColumn.put("Collaborator", strFinalOwnership);
			//End Collab
			
			//Start Owner
			strOwner = PersonUtil.getFullName(context, (String)mpPartSelects.get(DomainConstants.SELECT_OWNER));
			mpPartColumn.put("Owner", strOwner);
			
			//Start State
			strPartFamilyState = (String)mpPartSelects.get(DomainConstants.SELECT_CURRENT);
			mpPartColumn.put("State", strPartFamilyState);
		
			//Description
			strPartFamilyDescription = (String)mpPartSelects.get(DomainConstants.SELECT_DESCRIPTION);
			mpPartColumn.put("Description", strPartFamilyDescription);
			
			//Title
			strPartFamilyTitle = (String)mpPartSelects.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			mpPartColumn.put("Title", strPartFamilyTitle);
			
		}
	  }	
	 }
	}catch(Exception e) {
		 outLog.print("Exception in  getPartFamilyColumnInfo: "+e+"\n");
		 outLog.flush();
	 }
	finally {
			  DomainConstants.MULTI_VALUE_LIST.remove("ownership");	
	}
		return mpPartColumn;
	}
	
/**
 * this method is Used to get the Collaborators Information
 * @param context is the matrix context
 * @param args has the required information
 * @return String
 */

private String getCollaboratorsColumnInfo(Context context, String strPartFamCorrPath,String strPartFamCorrRevisionsPath) {
	String strProject = DomainConstants.EMPTY_STRING;
	String strOwnerShip = DomainConstants.EMPTY_STRING;
	StringList slFinalOwnership = new StringList();
	String strFinalOwnerShip = DomainConstants.EMPTY_STRING;
	String strFinalOwnership = DomainConstants.EMPTY_STRING;
	StringBuffer sbOwnership = new StringBuffer();
    StringList slFinalList = FrameworkUtil.split(strPartFamCorrPath, "|");
    StringList slFinalListRev = FrameworkUtil.split(strPartFamCorrRevisionsPath, "|");
    int slFinalListSize = slFinalList.size();
	int slFinalListRevisionSize = slFinalListRev.size();
	StringList slSelect = new StringList(1);
	Map mpPartSelects = null;
	DomainObject doObjLastLeaf = null;
	Map mpLastLeaf = null;
	try {
	StringList slBusSelectable = new StringList(10);			
	slBusSelectable.add(DomainConstants.SELECT_PROJECT);
	slBusSelectable.add(DomainConstants.SELECT_REVISION);
	slBusSelectable.add("ownership");	
	//Start Modified for 22x Upgrade to handle new API changes for multi value select
	StringList slMultiSelect = new StringList();
	slMultiSelect.add("ownership");
	//End Modified for 22x Upgrade to handle new API changes for multi value select
	slSelect.add(DomainConstants.SELECT_ID);
	StringList slMulOwnerDetails = new StringList();
	String strLastLeaf = slFinalList.get(slFinalListSize-1);
	String strLastLeafRevison = slFinalListRev.get(slFinalListRevisionSize-1);
	MapList mlLastLeafInfo = DomainObject.findObjects(context,DomainConstants.TYPE_PART_FAMILY,strLastLeaf,strLastLeafRevison,"*",pgV3Constants.VAULT_ESERVICEPRODUCTION,"",false,slSelect);
	if(mlLastLeafInfo.size()>0) {
	mpLastLeaf = (Map)mlLastLeafInfo.get(0);
	String strLastLeafId = (String)mpLastLeaf.get(DomainConstants.SELECT_ID);
	if(UIUtil.isNotNullAndNotEmpty(strLastLeafId)) {
		doObjLastLeaf = DomainObject.newInstance(context, strLastLeafId);
		//Start Modified for 22x Upgrade to handle new API changes for multi value select
		mpPartSelects = doObjLastLeaf.getInfo(context, slBusSelectable, slMultiSelect);
		//End Modified for 22x Upgrade to handle new API changes for multi value select
		if(mpPartSelects !=null && !mpPartSelects.isEmpty()) {
		strProject = (String) mpPartSelects.get(DomainConstants.SELECT_PROJECT);
		if(mpPartSelects.get("ownership") instanceof StringList)
			slMulOwnerDetails.addAll((StringList)mpPartSelects.get("ownership"));
		else
			slMulOwnerDetails.add((String)mpPartSelects.get("ownership"));
		
		for(int iIndex = 0; iIndex < slMulOwnerDetails.size(); iIndex++) {
			strOwnerShip = slMulOwnerDetails.get(iIndex);
			slFinalOwnership = FrameworkUtil.split(strOwnerShip, "|");
			if(slFinalOwnership != null && !slFinalOwnership.isEmpty()) {
				strFinalOwnerShip = slFinalOwnership.get(1);
				
				if(strFinalOwnerShip.endsWith("_PRJ")) {
					strFinalOwnerShip = MqlUtil.mqlCommand(context, "print role $1 select person dump", new String[] { strFinalOwnerShip });
					strFinalOwnerShip = PersonUtil.getFullName(context, (String)strFinalOwnerShip);
				}
				sbOwnership.append(",").append(strFinalOwnerShip);
		}
	  }
		sbOwnership.append(",").append(strProject);
		strFinalOwnership = sbOwnership.substring(",".length());
	}
  }	
 }
}catch(Exception e) {
	 e.printStackTrace();
	 outLog.print("Exception in  getCollaboratorsColumnInfo: "+e+"\n");
	 outLog.flush();
 }
	return strFinalOwnership;
}

/**
 * this method is Used to get the Owner Information For the last Part Family
 * @param context is the matrix context
 * @param args has the required information
 * @return String
 */

private String getOwnerColumnInfo(Context context, String strPartFamCorrPath,String strPartFamCorrRevisionsPath) {
	String strOwner = DomainConstants.EMPTY_STRING;;
    StringList slFinalList = FrameworkUtil.split(strPartFamCorrPath, SYMBOL_PIPE);
    StringList slFinalListRev = FrameworkUtil.split(strPartFamCorrRevisionsPath, SYMBOL_PIPE);
    int slFinalListSize = slFinalList.size();
	int slFinalListRevisionSize = slFinalListRev.size();
	Map mpPartSelects = null;
	DomainObject doObjLastLeaf = null;
	Map mpLastLeaf = null;
	try {
	StringList slBusSelectable = new StringList(10);			
	slBusSelectable.add(DomainConstants.SELECT_ID);
    slBusSelectable.add(DomainConstants.SELECT_OWNER);
    String strLastLeaf = slFinalList.get(slFinalListSize-1);
	String strLastLeafRevison = slFinalListRev.get(slFinalListRevisionSize-1);
	MapList mlLastLeafInfo = DomainObject.findObjects(context,DomainConstants.TYPE_PART_FAMILY,strLastLeaf,strLastLeafRevison,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,DomainConstants.EMPTY_STRING,false,slBusSelectable);
	if(mlLastLeafInfo.size()>0) {
	mpLastLeaf = (Map)mlLastLeafInfo.get(0);
	String strLastLeafId = (String)mpLastLeaf.get(DomainConstants.SELECT_ID);
	if(UIUtil.isNotNullAndNotEmpty(strLastLeafId)) {
		doObjLastLeaf = DomainObject.newInstance(context, strLastLeafId);
		mpPartSelects = doObjLastLeaf.getInfo(context, slBusSelectable);	
		if(mpPartSelects !=null && !mpPartSelects.isEmpty()) {
			strOwner = PersonUtil.getFullName(context, (String)mpPartSelects.get(DomainConstants.SELECT_OWNER));
		}
  }	
 }
}catch(Exception e) {
	 outLog.print("Exception in getOwnerColumnInfo: "+e+"\n");
	 outLog.flush();
 }

	return strOwner;
}


/**
 * this method is Used to get the Part Family Information For the last Part Family
 * @param context is the matrix context
 * @param args has the required information
 * @return String
 */

private String getPartFamilyStateColumnInfo(Context context, String strPartFamCorrPath,String strPartFamCorrRevisionsPath) {
	String strPartFamilyState = DomainConstants.EMPTY_STRING;;
    StringList slFinalList = FrameworkUtil.split(strPartFamCorrPath, SYMBOL_PIPE);
    StringList slFinalListRev = FrameworkUtil.split(strPartFamCorrRevisionsPath, SYMBOL_PIPE);
    int slFinalListSize = slFinalList.size();
	int slFinalListRevisionSize = slFinalListRev.size();
	Map mpPartSelects = null;
	DomainObject doObjLastLeaf = null;
	Map mpLastLeaf = null;
	try {
	StringList slBusSelectable = new StringList(10);			
	slBusSelectable.add(DomainConstants.SELECT_ID);
    slBusSelectable.add(DomainConstants.SELECT_CURRENT);
    
	
    String strLastLeaf = slFinalList.get(slFinalListSize-1);
	String strLastLeafRevison = slFinalListRev.get(slFinalListRevisionSize-1);
	MapList mlLastLeafInfo = DomainObject.findObjects(context,DomainConstants.TYPE_PART_FAMILY,strLastLeaf,strLastLeafRevison,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,DomainConstants.EMPTY_STRING,false,slBusSelectable);
	if(mlLastLeafInfo.size()>0) {
	mpLastLeaf = (Map)mlLastLeafInfo.get(0);
	String strLastLeafId = (String)mpLastLeaf.get(DomainConstants.SELECT_ID);
	if(UIUtil.isNotNullAndNotEmpty(strLastLeafId)) {
		doObjLastLeaf = DomainObject.newInstance(context, strLastLeafId);
		mpPartSelects = doObjLastLeaf.getInfo(context, slBusSelectable);	
		if(mpPartSelects !=null && !mpPartSelects.isEmpty()) {
			strPartFamilyState = (String)mpPartSelects.get(DomainConstants.SELECT_CURRENT);
		}
  }	
 }
}catch(Exception e) {
	 outLog.print("Exception in getPartFamilyStateColumnInfo: "+e+"\n");
	 outLog.flush();
 }
    return strPartFamilyState;
}


/**
 * this method is Used to get the Part Family Description Information For the last Part Family
 * @param context is the matrix context
 * @param args has the required information
 * @return String
 */

private String getPartFamilyDescriptionColumnInfo(Context context, String strPartFamCorrPath,String strPartFamCorrRevisionsPath) {
	String strPartFamilyDescription = DomainConstants.EMPTY_STRING;;
    StringList slFinalList = FrameworkUtil.split(strPartFamCorrPath, SYMBOL_PIPE);
    StringList slFinalListRev = FrameworkUtil.split(strPartFamCorrRevisionsPath, SYMBOL_PIPE);
    int slFinalListSize = slFinalList.size();
	int slFinalListRevisionSize = slFinalListRev.size();
	Map mpPartSelects = null;
	DomainObject doObjLastLeaf = null;
	Map mpLastLeaf = null;
	try {
	StringList slBusSelectable = new StringList(10);			
	slBusSelectable.add(DomainConstants.SELECT_ID);
    slBusSelectable.add(DomainConstants.SELECT_DESCRIPTION);
    slBusSelectable.add(DomainConstants.SELECT_ID);
    slBusSelectable.add(DomainConstants.SELECT_CURRENT);
    String strLastLeaf = slFinalList.get(slFinalListSize-1);
	String strLastLeafRevison = slFinalListRev.get(slFinalListRevisionSize-1);
	MapList mlLastLeafInfo = DomainObject.findObjects(context,DomainConstants.TYPE_PART_FAMILY,strLastLeaf,strLastLeafRevison,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,DomainConstants.EMPTY_STRING,false,slBusSelectable);
	if(mlLastLeafInfo.size()>0) {
	mpLastLeaf = (Map)mlLastLeafInfo.get(0);
	String strLastLeafId = (String)mpLastLeaf.get(DomainConstants.SELECT_ID);
	if(UIUtil.isNotNullAndNotEmpty(strLastLeafId)) {
		doObjLastLeaf = DomainObject.newInstance(context, strLastLeafId);
		mpPartSelects = doObjLastLeaf.getInfo(context, slBusSelectable);	
		if(mpPartSelects !=null && !mpPartSelects.isEmpty()) {
			strPartFamilyDescription = (String)mpPartSelects.get(DomainConstants.SELECT_DESCRIPTION);
		}
  }	
 }
}catch(Exception e) {	
	 outLog.print("Exception in getPartFamilyDescriptionColumnInfo: "+e+"\n");
	 outLog.flush();
 }
    return strPartFamilyDescription;
}


/**
 * this method is Used to get the Part Family Title Information For the last Part Family
 * @param context is the matrix context
 * @param args has the required information
 * @return String
 */

private String getPartFamilyTitleColumnInfo(Context context, String strPartFamCorrPath,String strPartFamCorrRevisionsPath) {
	String strPartFamilyTitle = DomainConstants.EMPTY_STRING;;
    StringList slFinalList = FrameworkUtil.split(strPartFamCorrPath, SYMBOL_PIPE);
    StringList slFinalListRev = FrameworkUtil.split(strPartFamCorrRevisionsPath, SYMBOL_PIPE);
    int slFinalListSize = slFinalList.size();
	int slFinalListRevisionSize = slFinalListRev.size();
	Map mpPartSelects = null;
	DomainObject doObjLastLeaf = null;
	Map mpLastLeaf = null;
	try {
	StringList slBusSelectable = new StringList(10);			
	slBusSelectable.add(DomainConstants.SELECT_ID);
    slBusSelectable.add(AWLUtil.strcat("attribute[", ATTRIBUTE_TITLE, "]"));
    String strLastLeaf = slFinalList.get(slFinalListSize-1);
	String strLastLeafRevison = slFinalListRev.get(slFinalListRevisionSize-1);
	MapList mlLastLeafInfo = DomainObject.findObjects(context,DomainConstants.TYPE_PART_FAMILY,strLastLeaf,strLastLeafRevison,DomainConstants.QUERY_WILDCARD,pgV3Constants.VAULT_ESERVICEPRODUCTION,DomainConstants.EMPTY_STRING,false,slBusSelectable);
	if(mlLastLeafInfo.size()>0) {
	mpLastLeaf = (Map)mlLastLeafInfo.get(0);
	String strLastLeafId = (String)mpLastLeaf.get(DomainConstants.SELECT_ID);
	if(UIUtil.isNotNullAndNotEmpty(strLastLeafId)) {
		doObjLastLeaf = DomainObject.newInstance(context, strLastLeafId);
		mpPartSelects = doObjLastLeaf.getInfo(context, slBusSelectable);	
		if(mpPartSelects !=null && !mpPartSelects.isEmpty()) {
			strPartFamilyTitle = (String)mpPartSelects.get(AWLUtil.strcat("attribute[", ATTRIBUTE_TITLE, "]"));
		}
  }	
 }
}catch(Exception e) {
	outLog.print("Exception in getPartFamilyTitleColumnInfo: "+e+"\n");
	outLog.flush();
 }
    return strPartFamilyTitle;
}


//Added for Hyperlink Requirement : 33630 - starts
/*
* This method is used to get the Hyperlink for Name columns in report
* @return void
*/
public void getHyperlink(Context context,Cell cell, XSSFWorkbook workbook, String strValue, String strId) {
	String sStringResourceFile="emxCPN";  
	String strURL = EnoviaResourceBundle.getProperty(context, sStringResourceFile, context.getLocale(),"emxCPN.BaseURL");
	String strNewURL = String.valueOf(strURL).trim();
	CreationHelper createHelper = workbook.getCreationHelper();
	XSSFCellStyle style = workbook.createCellStyle();	
	 Font hlinkfont = workbook.createFont();
	 hlinkfont.setUnderline(Font.U_SINGLE);
	 hlinkfont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
	 style.setFont(hlinkfont);
	 XSSFHyperlink link = (XSSFHyperlink)createHelper.createHyperlink(HyperlinkType.URL);
	 if(UIUtil.isNotNullAndNotEmpty(strId) && UIUtil.isNotNullAndNotEmpty(strValue)){
		 link.setAddress(strNewURL+"?objectId="+strId);
		 cell.setCellValue(strValue);
		 cell.setHyperlink(link);
	 } else {
		 cell.setCellValue(DomainConstants.EMPTY_STRING);
	 }
	 cell.setCellStyle(style);
		
}
//Added for Hyperlink Requirement : 33630 - ends
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

//Added for Part Library  Report Requirement 36714 -starts
/**
 * this method is to create sheet for excel
 * @param context is the matrix context
 * @return void
 */
private XSSFSheet createSheet(Context context, XSSFWorkbook workbook) {
	String strSheetName = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.PartLibrary.SheetName");
	XSSFSheet sheetPartLib = workbook.createSheet(strSheetName);
	XSSFRow rowHeaderPartLib = sheetPartLib.createRow(0);
	XSSFCellStyle cellStylePartLib = sheetPartLib.getWorkbook().createCellStyle();
	XSSFFont xsfontPartLib = sheetPartLib.getWorkbook().createFont();
	xsfontPartLib.setBold(true);
	xsfontPartLib.setFontHeightInPoints((short) 12);
	cellStylePartLib.setFont(xsfontPartLib);
	cellStylePartLib.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());			
	cellStylePartLib.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	getColumnNames(context, rowHeaderPartLib, cellStylePartLib);
	return sheetPartLib;
}

/** this method is to write column Names in excel sheet
 * @returns void
 */
private void getColumnNames(Context context,XSSFRow rowHeaderPartLib,XSSFCellStyle cellStylePartLib) {
	try {
		String strColumnNames = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.PartLibrary.EachColumn");
		StringList slIndividualColumnNames = StringUtil.split(strColumnNames, pgV3Constants.SYMBOL_COMMA);
		String strIndividualcolumnName = DomainConstants.EMPTY_STRING;
		String strColumnName = DomainConstants.EMPTY_STRING;
		String strColumnValue = DomainConstants.EMPTY_STRING;
		for (int i = 0;i<slIndividualColumnNames.size();i++) {
			strIndividualcolumnName = slIndividualColumnNames.get(i);
			strColumnName = String.valueOf(strIndividualcolumnName).trim();
			strColumnValue = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.PartLibrary.Column."+strColumnName);
			
			
			Cell cell = rowHeaderPartLib.createCell(i);
			cell.setCellStyle(cellStylePartLib);
			
			// Added for Defect 45466:Removing Redundant Space - Starts
			cell.setCellValue(strColumnValue.trim());	
			// Added for Defect 45466:Removing Redundant Space - Ends
		}
	}catch(Exception e) {
		outLog.print("Exception in  getColumnNames: "+e+"\n");
		outLog.flush();
	}
}

/**
 * this method is to get Parent Path String when input is Library folder
 * @param context is the matrix context
 * @return StringList
 */
private StringList getParentPath(Context context,String strPartLibrary,String strLibraryFolderId,StringList slParentPath,boolean bFlag){
	try{
		if(UIUtil.isNotNullAndNotEmpty(strLibraryFolderId)){
			DomainObject doObj = DomainObject.newInstance(context, strLibraryFolderId);
			Map<String,String> mObjectDetail = new HashMap<>();
			String strObjectName = DomainConstants.EMPTY_STRING;
			String strObjectId = DomainConstants.EMPTY_STRING;
			//Start modified for 22x Changes for Material Library/Family Auto Name Change
			String strObjectTitle = DomainConstants.EMPTY_STRING;
			//End modified for 22x Changes for Material Library/Family Auto Name Change
			StringList slSelectObj = new StringList();
			slSelectObj.add(DomainConstants.SELECT_ID);
			slSelectObj.add(DomainConstants.SELECT_NAME);
			//Start modified for 22x Changes for Material Library/Family Auto Name Change
			slSelectObj.add(SELECT_ATTRIBUTE_TITLE);
			//End modified for 22x Changes for Material Library/Family Auto Name Change
			MapList mlPartFamDetails = doObj.getRelatedObjects(context,//context
					pgV3Constants.RELATIONSHIP_SUBCLASS ,//rel pattern
					DomainConstants.QUERY_WILDCARD,//type pattern
					slSelectObj, //obj sel
					null,//rel select
					true,//get To
					false,//get From
					(short)1,//recurse level
					null,//obj where clause
					null,//rel where clause
					0);//limit
			if(null != mlPartFamDetails && !mlPartFamDetails.isEmpty()){
				for(int i=0;i<mlPartFamDetails.size();i++){
					mObjectDetail = (Map)mlPartFamDetails.get(i);
					strObjectName = mObjectDetail.get(DomainConstants.SELECT_NAME);
					strObjectId = mObjectDetail.get(DomainConstants.SELECT_ID);
					//Start modified for 22x Changes for Material Library/Family Auto Name Change
					strObjectTitle = mObjectDetail.get(SELECT_ATTRIBUTE_TITLE);
					if(strPartLibrary.equalsIgnoreCase(strObjectTitle)){
						bFlag = true;
						slParentPath.add(strObjectTitle); 
					}else{
						getParentPath(context,strPartLibrary,strObjectId,slParentPath,bFlag);
					}
					if(!bFlag && !slParentPath.isEmpty()){
						slParentPath.add(strObjectTitle); 
					}
					//End modified for 22x Changes for Material Library/Family Auto Name Change
				}
			}
		}
	}catch(Exception e){
		outLog.print("Exception in  getParentPath: "+e+"\n");
		outLog.flush();
	}
	return slParentPath;
}

/**
 * this method is to get Object details
 * @param context is the matrix context
 * @return MapList
 */
private MapList getObject(Context context,String strType,String strName) throws Exception{
	MapList mlObjectDetails = new MapList();
	StringList slSelectList = new StringList();
	slSelectList.add(DomainConstants.SELECT_ID);
	slSelectList.add(DomainConstants.SELECT_NAME);
	slSelectList.add(DomainConstants.SELECT_REVISION);
	slSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_ORIGINATOR);
	slSelectList.add(DomainConstants.SELECT_CURRENT);
	//Start modified for 22x Changes for Material Library/Family Auto Name Change
	slSelectList.add(SELECT_ATTRIBUTE_TITLE);
	String strWhere = DomainConstants.EMPTY_STRING;
	try{
		strWhere = "attribute[Title]=='"+strName+"'";
		mlObjectDetails = DomainObject.findObjects(context,//context
				strType, //type
				DomainConstants.QUERY_WILDCARD,//strName,//name
				DomainConstants.QUERY_WILDCARD,//revision
				DomainConstants.QUERY_WILDCARD,//owner
				pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
				strWhere,//where clause
				false,//expand type
				slSelectList);//obj sel
	//End modified for 22x Changes for Material Library/Family Auto Name Change
	}catch(Exception e){
		outLog.print("Exception in  getObject: "+e+"\n");
		outLog.flush();
	}
	return mlObjectDetails;
}

/**
 * this method is to get Report name and path
 * @param context is the matrix context
 * @return Map
 */
private Map<String,String> getReportName(Context context, String strReportFileName, String strUserName,String strReportPath) {
	String strReportName = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.FamilyCareReport.PartLibrary.ReportName");
	String strReportExtension = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.FamilyCareReport.ReportExtension");
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
	mpReturn.put("ReportName",strReportName);
	mpReturn.put("FullPath",strReportPath+ File.separator + strReportName);
	return mpReturn;
}
//Added for Part Library  Report Requirement 36714 -ends

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
		String strSubLine = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Mail.SubjectLine");
		
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
	
	String strMessageBody = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Mail.BodyMessage");
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
			strUrl = EnoviaResourceBundle.getProperty(context,CPNFILE, context.getLocale(), "emxCPN.BaseURL");
			strUrl = strUrl.substring(0, strUrl.indexOf(PATH_SEPARATOR+STR_COMMON));
			strUrl = String.valueOf(strUrl).trim();
			strUrlPath = EnoviaResourceBundle.getProperty(context, STRINGRESOURCEFILE, context.getLocale(),"emxCPN.DSMReport.Mail.Enovia.DSMReportLink");
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
