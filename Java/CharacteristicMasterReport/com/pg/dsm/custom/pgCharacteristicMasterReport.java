/*  pgCharacteristicMasterReport.java  Report Request for Characteristic Master Report Download written for requirements 40535,40536,40554,40555,40556,40557,40558,41298
     Author:DSM Report Team
     Copyright (c) 2021
     All Rights Reserved.
 */

package com.pg.dsm.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import matrix.db.MxMessageSupport;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import com.matrixone.apps.domain.util.ContextUtil;

import com.matrixone.apps.domain.util.FrameworkException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicFactory;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristic;
import com.dassault_systemes.enovia.characteristic.interfaces.ENOICharacteristicsUtil;


public class pgCharacteristicMasterReport implements DomainConstants {

	public pgCharacteristicMasterReport(Context context, String[] args) {

	}

	
	private StringList slCharacteristicMasterInfoSelect = getCharacteristicMasterColumnSelects();
	private StringList slCharacterisiticMasterCommonInfoSelect = getObjectCommonSelects();

	private static final String EMXCPN = "emxCPN";
	private static final String USERNAME = "UserName";
	private static final String REPORTFILENAME = "ReportFileName";
	private static final String REPORTOBJECTID = "ReportObjectId";
	private static final String ATTR_IMPACTEDTYPE = "attribute[pgImpactedType]";
	
	private static final String DATE_YMDHS = "yyyy-MM-dd HH:mm:ss";
	private static final String DIRECTORY_ERR = "Could not create directory";
	private PrintWriter outLog = null;
	
	private static final String DEFAULT = "Default";
	private static final String ATTRIBUTE_CHARACTERISTICCATEGORY  = "attribute[Characteristic Category]";
	private static final String ATTRIBUTE_PGCATEGORYSPECIFICS = "attribute[pgCategorySpecifics]";
	private static final String ATTRIBUTE_PGCHARACTERISTIC = "attribute[pgCharacteristic]";
	private static final String ATTRIBUTE_PGCHARACTERISTICSPECIFICS="attribute[pgCharacteristicSpecifics]";
	private static final String ATTRIBUTE_PGDESIGNSPECIFICS="attribute[pgDesignSpecifics]";
	private static final String ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM="attribute[pgProductCategoryPlatform]";
	private static final String ATTRIBUTE_PGMETHODSPECIFICS="attribute[pgMethodSpecifics]";
	private static final String ATTRIBUTE_PGSAMPLING="attribute[pgSampling]";
	private static final String ATTRIBUTE_PGSUBGROUP="attribute[pgSubGroup]";
	private static final String ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT="attribute[pgLowerSpecificationLimit]";
	private static final String ATTRIBUTE_PGLOWERROUTINERELEASELIMIT="attribute[pgLowerRoutineReleaseLimit]";
	private static final String ATTRIBUTE_PGUPPERROUTINERELEASELIMIT="attribute[pgUpperRoutineReleaseLimit]";
	private static final String ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT="attribute[pgUpperSpecificationLimit]";
	private static final String ATTRIBUTE_PGREPORTTONEAREST="attribute[pgReportToNearest]";
	private static final String ATTRIBUTE_PGREPORTTYPE="attribute[pgReportType]";
	private static final String ATTRIBUTE_PGISDESIGNDRIVEN="attribute[pgIsDesignDriven]";
	private static final String ATTRIBUTE_PGRELEASECRITERIA="attribute[pgReleaseCriteria]";
	private static final String ATTRIBUTE_PGACTIONREQUIRED="attribute[pgActionRequired]";
	private static final String ATTRIBUTE_PGCRITICALITYFACTOR="attribute[pgCriticalityFactor]";
	private static final String ATTRIBUTE_PGCHARACTERISTICNOTES="attribute[Characteristic Notes]";
	private static final String ATTRIBUTE_PGTMLOGIC="attribute[pgTMLogic]";
	private static final String ATTRIBUTE_PGTESTMETHODORIGIN="attribute[pgTestMethodOrigin]";
	private static final String ATTRIBUTE_PGMETHODNUMBER="attribute[pgMethodNumber]";
	private static final String ATTRIBUTE_PGPLANTTESTINGRETESTING="attribute[pgPlantTestingRetesting]";
	private static final String ATTRIBUTE_PGRETESTINGUOM="attribute[pgRetestingUOM]";
	private static final String ATTRIBUTE_PGBASIS="attribute[pgBasis]";
	private static final String ATTRIBUTE_PGTESTGROUP="attribute[pgTestGroup]";
	private static final String ATTRIBUTE_PGAPPLICATION="attribute[pgApplication]";
	private static final String ATTRIBUTE_PGPLANTTESTINGLEVEL="attribute[pgPlantTesting]";
	private static final String INTERFACE = "interface";
	private static final String SYMBOL_NOT_EQUALS = "!=";
	
	private static final String ATTRIBUTE_PGBUSINESSAREA = "attribute[pgBusinessArea]";
	private static final String ATTRIBUTE_UNITOFMEASURE = "attribute[PlmParamDisplayUnit]";
	private static final String REALTIMEPROCESS = "RealTimeProcess";
	private static final String AllRELEASEDCMS = "AllLatestCMS";
	private static final String LATESTRELEASEPART = "LatestCM";
	private static final String SELECTEDBUSINESSAREAS = "SelectedBusinessAreas";
	private static final String SHEETNAME_CM = "Characteristic_Master";
	private static final String SHEETNAME_CRITERIA = "CM-CRIT_Relationship";
	private static final String STRFORMAT = PropertyUtil.getSchemaProperty(null,"format_generic");
	private static final String TYPECHARACTERISTICMASTER = PropertyUtil.getSchemaProperty(null,"type_CharacteristicMaster");
	private int rowCount;
	//Added for defect id 46172 Starts
	private static final String ATTRIBUTE_PGMETHODORIGIN = "attribute["+PropertyUtil.getSchemaProperty(null,"attribute_pgMethodOrigin")+"]";
	private static final String ATTRIBUTE_OBSCUREUOM = "attribute["+PropertyUtil.getSchemaProperty(null,"attribute_ObscureUnitofMeasure")+"]";
	//Added for defect id 46172 Ends
	//Requirement 42696
	private static final String RELEASED_DATE = "state[Released].actual";
	private static final String RELATIONSHIP_CRITERIA_OUTPUT = "Criteria Output";
	private static final String TYPECRITEIRA = "Criteria";
	
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
	private static final String PATH_SEPARATOR="/";
	private static final String CPNSTRINGRESOURCE = "emxCPNStringResource";
	private String strOriginatingSource = "";
	private static final String TYPE_GLOBALSUBSCRIPTIONCOFIGURATION = "Global Subscription Configuration";
	private static final String ORIGINATINGSOURCE = "OriginatingSource";
	private static final String STR_VAULT_ADMINISTRATION=PropertyUtil.getSchemaProperty(null,"vault_eServiceAdministration");
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	
	/*
     * this method is to generate the Characteristic Master Report for the input parts submitted by user
     * @param context is the matrix context
     * @param args
     * @return void
     */	
	public void generateCharacteristicMasterReport(Context context, String args[])  {
		
		try {
		StringBuilder sbLogFolder = new StringBuilder();
		File fLogFolder = null;
		String strDirectoryNotCreated = DIRECTORY_ERR;
		String strStartTime = null;
		
		HashMap<String, String> hmArgs = (HashMap) JPO.unpackArgs(args);
		String strUserName = hmArgs.get(USERNAME);
		String strReportFileName = hmArgs.get(REPORTFILENAME);
		String strReportObjectId = hmArgs.get(REPORTOBJECTID);
		String strLatestVersionAsInput = hmArgs.get(LATESTRELEASEPART);
		String strAllReleasedCMs = hmArgs.get(AllRELEASEDCMS);
		String strSelectedBusinessAreas = hmArgs.get(SELECTEDBUSINESSAREAS);
		String strRealTimeProcess = hmArgs.get(REALTIMEPROCESS);
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
		strOriginatingSource = hmArgs.get(ORIGINATINGSOURCE);
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
		String strRequestedState = "";
		String configLOGFilePath = "";
		
		if (UIUtil.isNotNullAndNotEmpty(strRealTimeProcess))
			configLOGFilePath = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),
					"emxCPN.DSMReportCTRLMJob.Log.FilePath");
		

		sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
		fLogFolder = new File(sbLogFolder.toString());
		if (!fLogFolder.exists() && !fLogFolder.mkdirs()) {
			throw new IOException(strDirectoryNotCreated + fLogFolder);
		}
		
		outLog = new PrintWriter(new FileOutputStream(
				fLogFolder.toString() + File.separator + "CharacteristicMasterReportLog.log", true));

		
		strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());
		if (UIUtil.isNotNullAndNotEmpty(strUserName)) {
			DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
			outLog.print("-------Report requested by: " + strUserName + " : " + strStartTime + "--------\n");
			outLog.print("Report Object Id: " + strReportObjectId + "\n");
			outLog.print("Selected Business Areas :" + strSelectedBusinessAreas + "\n");
			outLog.flush();
			doObj.promote(context);
			if("False".equalsIgnoreCase(strAllReleasedCMs) && "False".equalsIgnoreCase(strLatestVersionAsInput)) {
				strRequestedState = DEFAULT;
			} else if("True".equalsIgnoreCase(strAllReleasedCMs) ){
				strRequestedState = AllRELEASEDCMS;
			} else {
				strRequestedState = LATESTRELEASEPART;
			}
			getCharacteristicMasterReportProcess(context, strUserName, strReportFileName, strReportObjectId,
					strRequestedState, strSelectedBusinessAreas);
			
			
		} else {
			outLog.print("Report cannot be generated. Check Report Object Id for request details: " + strReportObjectId
					+ "\n");
			outLog.flush();
		}
		
		}catch(Exception e) {
			outLog.print("Exception in  generateCharacteristicMasterReport: "+e+"\n");
			outLog.flush();
		}

	}
	
	/** this method fetches all the selected Business areas from input and processes them.
	 * @param context
	 * @param strUserName
	 * @param strReportFileName
	 * @param strReportObjectId
	 * @param strRequestedState
	 * @param strSelectedBusinessAreas
	 * @param strLatestVersionAsInput
	 * @return void
	 */
	private void getCharacteristicMasterReportProcess(Context context, String strUserName, String strReportFileName,
			String strReportObjectId, String strRequestedState, String strSelectedBusinessAreas) {
		

	try {
		Map<String, String> mpCharacteristicMaster;
		rowCount = 0;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet characteristicMasterReportsheet = createSheet(context, workbook, SHEETNAME_CM);
		XSSFSheet characteristicMasterCriteriaSheet = createSheet(context, workbook, SHEETNAME_CRITERIA);
		//Requirement 42696
		rowCount = rowCount + 1;
		MapList mlCharaceristicMasterData = getCharacteristicMasterObjects(context, strSelectedBusinessAreas, strRequestedState);
		if (mlCharaceristicMasterData != null && !mlCharaceristicMasterData.isEmpty()) {
			
			for (int i = 0; i < mlCharaceristicMasterData.size(); i++) {
				mpCharacteristicMaster = (Map) mlCharaceristicMasterData.get(i);
				getCharacteristicMasterData(context, mpCharacteristicMaster, workbook, characteristicMasterReportsheet, rowCount, strRequestedState);
			}
			getCharacteristicMasterCriteriaData(context, mlCharaceristicMasterData, workbook, characteristicMasterCriteriaSheet, strRequestedState);			

		} else{
			createSheet(context, workbook, SHEETNAME_CM);
			createSheet(context, workbook, SHEETNAME_CRITERIA);
			outLog.print("Report completed for: "+strUserName+": "+strReportObjectId+": "+strReportFileName+"| No Characteristic Master Objects found for Selected Business Areas"+ "\n");
			outLog.flush();
		}
		checkInExcelFile(context,workbook,strReportFileName,strReportObjectId);
		String strEndTime = null; 
		strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());			
		outLog.print("-------Time completed: "+strEndTime+"-------\n");	
		outLog.print("-------\n");
		outLog.flush();
	}catch(Exception e) {
		outLog.println("Exception in  getCharacteristicMasterReportProcess: "+strUserName+": "+e+"\n");
		outLog.flush();
	} 
		
		
	}
	
	/** this method fetches all the valid Characteristic Master objects in a map and processes them.
	 * @param context
	 * @param mpCharacteristicMaster
	 * @param workbook
	 * @param characteristicMasterReportsheet
	 * @param strRequestedState
	 * @param rowCount
	 * @return void
	 */
	public void getCharacteristicMasterData(Context context, Map mpCharacteristicMaster, XSSFWorkbook workbook, XSSFSheet characteristicMasterReportsheet,  int rowCount, String strRequestedState) {
		
		String strCharacteristicMasterId = (String)mpCharacteristicMaster.get(DomainConstants.SELECT_ID);
		String strCharacteristicMasterName = (String)mpCharacteristicMaster.get(DomainConstants.SELECT_NAME);
		
		boolean isValidCM = true;
		
		HashMap<Integer, Object> hm = new HashMap<>();
		XSSFRow row = characteristicMasterReportsheet.createRow(rowCount);
		int columnCount1 = 0;
		//Added for defect id 46172 Starts
		String strNotes = "";
		//Added for defect id 46172 Ends
		try {
		if(strRequestedState.equalsIgnoreCase(LATESTRELEASEPART)) {
			isValidCM = isLatestReleasedCharacteristicMasterPart(context, strCharacteristicMasterName, strCharacteristicMasterId);
		}
			if(isValidCM) {
			ENOICharacteristicsUtil charUtil = ENOCharacteristicFactory.getCharacteristicUtil(context);
			HashMap<String, StringList> mpDimensionDisplayVals =  charUtil.getDimensions(context);
			DomainObject sCMObject= DomainObject.newInstance(context,strCharacteristicMasterId);
			Pattern relPattern = new Pattern(pgV3Constants.RELATIONSHIP_PARAMETER_AGGREGATION);
			MapList getRelatedCharacteristicMasterData = sCMObject.getRelatedObjects(context,
					relPattern.getPattern(),  //relationship pattern
					pgV3Constants.SYMBOL_STAR,                         // object pattern
					slCharacteristicMasterInfoSelect,                 // object selects
					null,              // relationship selects
					false,                        // to direction
					true,                       // from direction
					(short)1,                    // recursion level
					DomainConstants.EMPTY_STRING,                        // object where clause
					null,//relationshipWhere
					0);//limit
			
			//Added for defect id 46172 -starts
			StringList slMulValueAttr = new StringList();
			slMulValueAttr.add(ATTRIBUTE_PGBUSINESSAREA);
			slMulValueAttr.add(ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM);
			StringList slSelValueAttr = new StringList();
			slSelValueAttr.add(ATTRIBUTE_PGBUSINESSAREA);
			slSelValueAttr.add(ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM);
			DomainObject doCharacteristicMaster = DomainObject.newInstance(context, (String)mpCharacteristicMaster.get(DomainConstants.SELECT_ID));
			Map mapMultiValueAttributes = doCharacteristicMaster.getInfo(context, slSelValueAttr,slMulValueAttr);
			
			//Added for defect id 46172 - ends
			
			int getRelatedCharacteristicMasterDataSize = getRelatedCharacteristicMasterData.size();
			for(int i=0;i<getRelatedCharacteristicMasterDataSize;i++) {
				Map mpGetRelatedCMData = (Map) getRelatedCharacteristicMasterData.get(i);
				ENOICharacteristic characteristic = ENOCharacteristicFactory.getCharacteristicById(context, (String)mpGetRelatedCMData.get(pgV3Constants.PHYSICALID));
				hm.put(0, mpCharacteristicMaster.get(DomainConstants.SELECT_NAME));
				hm.put(1,mpCharacteristicMaster.get(DomainConstants.SELECT_REVISION));
				hm.put(2,mpCharacteristicMaster.get(DomainConstants.SELECT_OWNER));
				hm.put(3, mpCharacteristicMaster.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				hm.put(4, mpCharacteristicMaster.get(DomainConstants.SELECT_DESCRIPTION));
				hm.put(5, mpCharacteristicMaster.get(DomainConstants.SELECT_CURRENT));
				hm.put(6, mpGetRelatedCMData.get(ATTRIBUTE_CHARACTERISTICCATEGORY));
				hm.put(7, mpGetRelatedCMData.get(ATTRIBUTE_PGCATEGORYSPECIFICS));
				//Added for defect id 46172 Starts
				hm.put(8, mpGetRelatedCMData.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				//Added for defect id 46172 Ends
				hm.put(9, mpGetRelatedCMData.get(ATTRIBUTE_PGCHARACTERISTICSPECIFICS));
				//Added for defect id 46177 Starts
				hm.put(10, mpGetRelatedCMData.get(DomainConstants.SELECT_DESCRIPTION));
				//Added for defect id 46177 Ends
				hm.put(11, mpGetRelatedCMData.get(ATTRIBUTE_PGDESIGNSPECIFICS));
				//Added for defect id 46172 Starts
				hm.put(12, getAttributeValue(mapMultiValueAttributes, ATTRIBUTE_PGBUSINESSAREA));//Business Areas
				hm.put(13, getAttributeValue(mapMultiValueAttributes,ATTRIBUTE_PGPRODUCTCATEGORYPLATFORM));
				//Added for defect id 46172 Ends
				hm.put(14, getDimensionData((StringList)mpGetRelatedCMData.get(INTERFACE), mpDimensionDisplayVals));
				//Added for defect id 46172 Starts
				hm.put(15, getUnitOfMeasure(mpGetRelatedCMData));
				//Added for defect id 46172 Ends
				hm.put(16, getTestMethods(context, (String)mpGetRelatedCMData.get(DomainConstants.SELECT_ID)));
				//Updated for defect id 46241 Starts
				hm.put(17, (mpGetRelatedCMData.get(ATTRIBUTE_PGMETHODSPECIFICS)).toString().replace("\n"," "));
				//Updated for defect id 46241 Ends
				hm.put(18, getTestMethodReferenceDocument(context, (String)mpGetRelatedCMData.get(DomainConstants.SELECT_ID)));//here
				hm.put(19, mpGetRelatedCMData.get(ATTRIBUTE_PGSAMPLING));
				hm.put(20, mpGetRelatedCMData.get(ATTRIBUTE_PGSUBGROUP));
				//Added for defect id 46172 Starts
				hm.put(21, characteristic.getLowerSpecificationLimit(context));
				hm.put(22, characteristic.getLowerRoutineReleaseLimit(context));
				//Added for defect id 46172 Ends
				hm.put(23, characteristic.getMinimalValue(context));
				hm.put(24, characteristic.getNominalValue(context));
				hm.put(25, characteristic.getMaximalValue(context));
				//Added for defect id 46172 Starts
				hm.put(26, characteristic.getUpperRoutineReleaseLimit(context));
				hm.put(27, characteristic.getUpperSpecificationLimit(context));
				//Added for defect id 46172 Ends
				hm.put(28, mpGetRelatedCMData.get(ATTRIBUTE_PGREPORTTONEAREST));
				hm.put(29, mpGetRelatedCMData.get(ATTRIBUTE_PGREPORTTYPE));
				hm.put(30, mpGetRelatedCMData.get(ATTRIBUTE_PGISDESIGNDRIVEN));
				hm.put(31, mpGetRelatedCMData.get(ATTRIBUTE_PGRELEASECRITERIA));
				hm.put(32, mpGetRelatedCMData.get(ATTRIBUTE_PGACTIONREQUIRED));
				hm.put(33, mpGetRelatedCMData.get(ATTRIBUTE_PGCRITICALITYFACTOR));
				//Added for defect id 46172 Starts
				strNotes = mpGetRelatedCMData.get(ATTRIBUTE_PGCHARACTERISTICNOTES).toString().replace("\n"," ");
				hm.put(34, strNotes);
				//Added for defect id 46172 Ends
				//Updated for defect id 46241 Starts
				hm.put(35, (mpGetRelatedCMData.get(ATTRIBUTE_PGTMLOGIC)).toString().replace("\n"," "));
				//Added for defect id 46172 Starts
				hm.put(36, (mpGetRelatedCMData.get(ATTRIBUTE_PGMETHODORIGIN)).toString().replace("\n"," "));
				//Added for defect id 46172 Ends
				hm.put(37, (mpGetRelatedCMData.get(ATTRIBUTE_PGMETHODNUMBER)).toString().replace("\n"," "));
				//Updated for defect id 46241 Ends
				hm.put(38, mpGetRelatedCMData.get(ATTRIBUTE_PGPLANTTESTINGLEVEL));
				hm.put(39, mpGetRelatedCMData.get(ATTRIBUTE_PGPLANTTESTINGRETESTING));
				hm.put(40, mpGetRelatedCMData.get(ATTRIBUTE_PGRETESTINGUOM));
				hm.put(41, mpGetRelatedCMData.get(ATTRIBUTE_PGBASIS));
				hm.put(42, mpGetRelatedCMData.get(ATTRIBUTE_PGTESTGROUP));
				hm.put(43, mpGetRelatedCMData.get(ATTRIBUTE_PGAPPLICATION));
				
			}
			//Updated for defect id 46241-Starts
			if(getRelatedCharacteristicMasterDataSize>0) {
				//Updated for defect id 46241-ends
				updateRow(workbook, row, hm, columnCount1);
			}
			
			
			}
		
		}catch(Exception e) {
			outLog.print("Exception in  getCharacteristicMasterData: "+e+"\n");
			outLog.flush();
		} 
			
		}

	
	/** this method fetches all the valid Characteristic Master Crtieriaobjects in a map and processes them.
	 * @param context
	 * @param mpCharacteristicMaster
	 * @param workbook
	 * @param characteristicMasterReportsheet
	 * @param strRequestedState
	 * @param rowCount
	 * @return void
	 */
	//Added for Requirement 42696
	public void getCharacteristicMasterCriteriaData(Context context, MapList mlCharaceristicMasterData, XSSFWorkbook workbook, XSSFSheet characteristicMasterCriteriaSheet, String strRequestedState) {
		String strCharacteristicMasterId = "";
		String strCharacteristicMasterName = "";
		Map<String, String> mpCharacteristicMaster;
		boolean isValidCM = true;		
		String strWhereExp = "(current!="+pgV3Constants.STATE_OBSOLETE+")";
		HashMap<Integer, Object> hm = new HashMap<>();		
		int columnCount1 = 0;
		int critRowCount = 0;
		String strName = "";
		String strRev = "";
		String strState = "";
		String strReleasedDate = "";
		StringList slObjSelect = new StringList(1);							
		slObjSelect.add(DomainConstants.SELECT_NAME);
		slObjSelect.add(DomainConstants.SELECT_REVISION);
		slObjSelect.add(DomainConstants.SELECT_CURRENT);
		slObjSelect.add(RELEASED_DATE);
		
		try {
			for (int n = 0; n < mlCharaceristicMasterData.size(); n++) {
				mpCharacteristicMaster = (Map) mlCharaceristicMasterData.get(n);
				strCharacteristicMasterId = (String)mpCharacteristicMaster.get(DomainConstants.SELECT_ID);
				strCharacteristicMasterName = (String)mpCharacteristicMaster.get(DomainConstants.SELECT_NAME);
			if(strRequestedState.equalsIgnoreCase(LATESTRELEASEPART)) {
				isValidCM = isLatestReleasedCharacteristicMasterPart(context, strCharacteristicMasterName, strCharacteristicMasterId);
			}
			if(isValidCM) {
			DomainObject sCMObject= DomainObject.newInstance(context,strCharacteristicMasterId);					
			strName = (String)sCMObject.getInfo(context, "name");	
			strRev = (String)sCMObject.getInfo(context, "revision");	
			strState = (String)sCMObject.getInfo(context, "current");	
			strReleasedDate = (String)sCMObject.getInfo(context, "state[Released].actual");	
		
			Pattern relPattern = new Pattern(RELATIONSHIP_CRITERIA_OUTPUT);
			MapList mlCMCriteriaData = sCMObject.getRelatedObjects(context,
					relPattern.getPattern(),  //relationship pattern
					TYPECRITEIRA,             // object pattern
					slObjSelect,              // object selects
					null,              // relationship selects
					true,             // to direction
					false,              // from direction
					(short)1,          // recursion level
					strWhereExp,       // object where clause
					null,//relationshipWhere
					0);//limit
					
			int iCMCriteriaDataSize = mlCMCriteriaData.size();
			for(int i=0;i<iCMCriteriaDataSize;i++) {
				critRowCount = critRowCount + 1;
				XSSFRow row = characteristicMasterCriteriaSheet.createRow(critRowCount);
				Map mpGetRelatedCrit = (Map) mlCMCriteriaData.get(i);
				hm.put(0, strName);
				hm.put(1, strRev);
				hm.put(2, strState);
				hm.put(3, strReleasedDate);
				hm.put(4, mpGetRelatedCrit.get(DomainConstants.SELECT_NAME));
				hm.put(5, mpGetRelatedCrit.get(DomainConstants.SELECT_REVISION));
				hm.put(6, mpGetRelatedCrit.get(DomainConstants.SELECT_CURRENT));
				hm.put(7, mpGetRelatedCrit.get(RELEASED_DATE));
				updateRow(workbook, row, hm, columnCount1);
			}
		}
		}
		}catch(Exception e) {
			outLog.print("Exception in  getCharacteristicMasterCriteriaData: "+e+"\n");
			outLog.flush();
		} 
			
		}
	
	
	
	
	
	/** this method fetches Latest Released Characteristic Master Object
	 * @param context
	 * @param strCharacteristicMasterName
	 * @param strCharacteristicMasterId
	 * @return boolean
	 */
	
		private boolean isLatestReleasedCharacteristicMasterPart(Context context, String strCharacteristicMasterName, String strCharacteristicMasterId) {
			
			boolean isLatestCM = false;
			StringList slSelect = new StringList(2);
			slSelect.add(DomainConstants.SELECT_ID);
			slSelect.add(DomainConstants.SELECT_REVISION);
			
			String strStateWhere = "Current=="+pgV3Constants.STATE_RELEASED;
			
			Map<String, String> mReleaseHighestRevision = new HashMap<>();
			int mlPartObjSize = 0;
			int mlPartObjSortSize = 0;

			try {
				
				MapList mlPartObj = DomainObject.findObjects(context, // context
						TYPECHARACTERISTICMASTER, // type
						strCharacteristicMasterName, // name
						DomainConstants.QUERY_WILDCARD, // revision
						DomainConstants.QUERY_WILDCARD, // owner
						pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault
						strStateWhere, // where clause
						false, // expand type
						slSelect);// object select
				

				if (!mlPartObj.isEmpty()) {
					mlPartObjSize = mlPartObj.size();
					
						if (mlPartObjSize > 1) {
							// Fetching Highest Release revision --Begin
							mlPartObj.sort(DomainConstants.SELECT_REVISION, "descending", "string");
							mlPartObjSortSize = mlPartObj.size();
							for (int iRelRevNum = 0; iRelRevNum < mlPartObjSortSize; iRelRevNum++) {
								mReleaseHighestRevision = (Map) mlPartObj.get(iRelRevNum);
								isLatestCM = strCharacteristicMasterId.equalsIgnoreCase(mReleaseHighestRevision.get(DomainConstants.SELECT_ID));
								//getting the highest revision and breaking it.
								if(UIUtil.isNotNullAndNotEmpty(String.valueOf(isLatestCM))) {
								break;
								}
							}
							return isLatestCM; 
							
						} else {
							isLatestCM = true;
							return isLatestCM;
						}
						}
				
				return isLatestCM;

				
			} catch (Exception e) {
				outLog.print("Exception in  isLatestReleasedCharacteristicMasterPart:"+e+"\n");
				outLog.flush();
			} 
			
			return isLatestCM;
			}
		
	

		/** this method fetches all the characteristic Master Objects matching the business areas and the requested state 
		 * @param context
		 * @param strSelectedBusinessAreas
		 * @param strRequestedState
		 * @return MapList
		 */
	public MapList getCharacteristicMasterObjects(Context context, String strSelectedBusinessAreas, String strRequestedState) {
		
		StringBuilder sbFinalWhere = new StringBuilder();
		StringBuilder sbStateWhere = new StringBuilder();
		sbStateWhere.append(DomainConstants.SELECT_CURRENT);
		
		
		MapList mlData = new MapList();

		String sBusinessAreas = strSelectedBusinessAreas;
		String[] strArrBusinessArea = sBusinessAreas.split("\\~");
		StringBuilder strWhere = new StringBuilder();
		for (int i = 0; i < strArrBusinessArea.length; i++) {
			
			if (strWhere.length()==0) {
				if(strArrBusinessArea[i].trim().length()>0) {
					strWhere.append("attribute[pgBusinessArea]~~'*"+strArrBusinessArea[i]+"*'");
				}
				
			} else {
				if(strArrBusinessArea[i].trim().length()>0) {
					strWhere.append(pgV3Constants.SYMBOL_OR);
					strWhere.append("attribute[pgBusinessArea]~~'*"+strArrBusinessArea[i]+"*'");
				}
			}

		}
		if(strRequestedState.equalsIgnoreCase(DEFAULT)) {
			sbStateWhere.append(SYMBOL_NOT_EQUALS);
			sbStateWhere.append(pgV3Constants.STATE_OBSOLETE);
			
			
		} 
		else {
			sbStateWhere.append(pgV3Constants.CONST_SYMBOL_EQUAL);
			sbStateWhere.append(pgV3Constants.STATE_RELEASED);
			
		}
		
		
		sbFinalWhere.append(pgV3Constants.CONST_OPEN_BRACKET);
		sbFinalWhere.append(strWhere);
		sbFinalWhere.append(pgV3Constants.CONST_CLOSED_BRACKET);
		sbFinalWhere.append(pgV3Constants.SYMBOL_AND);
		sbFinalWhere.append(sbStateWhere);
		String strfinalWhere = sbFinalWhere.toString();
		
		

		try {

			mlData = DomainObject.findObjects(context, // context
					TYPECHARACTERISTICMASTER, // type
					DomainConstants.QUERY_WILDCARD, // name
					DomainConstants.QUERY_WILDCARD, // revision
					DomainConstants.QUERY_WILDCARD, // owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault
					strfinalWhere, // whereExpression
					false, // expandType
					slCharacterisiticMasterCommonInfoSelect);
					
					
			
		} catch (Exception e) {
			outLog.print("Exception in  getCharacteristicMasterObjects: "+e+"\n");
			outLog.flush();
		}

		return mlData;
	}
	
		
	/** this method contains StringList of selectable
	 * @return StringList
	 */
		private StringList getObjectCommonSelects() {

		StringList slObjInfoSelect = new StringList(12);
		try {
			slObjInfoSelect.add(DomainConstants.SELECT_ID);
			slObjInfoSelect.add(DomainConstants.SELECT_NAME);
			slObjInfoSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			slObjInfoSelect.add(DomainConstants.SELECT_TYPE);
			slObjInfoSelect.add(DomainConstants.SELECT_REVISION);
			slObjInfoSelect.add(DomainConstants.SELECT_DESCRIPTION);
			slObjInfoSelect.add(DomainConstants.SELECT_CURRENT);
			slObjInfoSelect.add(DomainConstants.SELECT_OWNER);
			slObjInfoSelect.add(DomainConstants.SELECT_POLICY);
			slObjInfoSelect.add(ATTR_IMPACTEDTYPE);
			
			
			} catch (Exception e) {
			outLog.print("Exception in  getObjectCommonSelects: "+e+"\n");
			outLog.flush();
		}
		return slObjInfoSelect;
	}

		/** this method creates the sheet in the work book
		 * @param context
		 * @param workbook
		 * @return Sheet
		 */
		
		private XSSFSheet createSheet(Context context, XSSFWorkbook workbook, String strSheetName) {
		XSSFSheet cmReportsheet = null;
		
		XSSFCellStyle cellStylePart = workbook.createCellStyle();
		XSSFFont xsfontPart = workbook.createFont();
		xsfontPart.setBold(true);
		xsfontPart.setFontHeightInPoints((short) 12);
		cellStylePart.setFont(xsfontPart);
		cellStylePart.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
		cellStylePart.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		if (workbook.getSheet(strSheetName) == null) {
			String sheetName = WorkbookUtil.createSafeSheetName(strSheetName);
			cmReportsheet = workbook.createSheet(sheetName);
			XSSFRow rowHeaderPart = cmReportsheet.createRow(0);
			getColumnNames(context, rowHeaderPart, cellStylePart,strSheetName);
			
		}
		return cmReportsheet;
	}
		
		/** this method creates the header by pulling all the column names
		 * @param context
		 * @param rowHeaderPart
		 * @param cellStylePart
		 * @return void
		 */
	private void getColumnNames(Context context, XSSFRow rowHeaderPart, XSSFCellStyle cellStylePart, String strSheetName) {
		try {			
			String strColumnNames = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),
					"emxCPN.CharacteristicMasterReport.Worksheet.ColumnTypes."+strSheetName);
			StringList slIndividualColumnNames = StringUtil.split(strColumnNames, pgV3Constants.SYMBOL_COMMA);
			for (int i = 0; i < slIndividualColumnNames.size(); i++) {
				String columnName = slIndividualColumnNames.get(i);
				String strColumnName = String.valueOf(columnName).trim();
				String strColumnValue = EnoviaResourceBundle.getProperty(context, EMXCPN,
						context.getLocale(), "emxCPN.CharacteristicMasterReport.Worksheet.Column." + strColumnName);
				Cell cell = rowHeaderPart.createCell(i);
				cell.setCellStyle(cellStylePart);
				cell.setCellValue(strColumnValue.trim());
			}
		} catch (Exception e) {
			
			outLog.print("Exception in  getColumnNames: " + e + "\n");
			outLog.flush();
		}
	}
	
	/** this method contains selectables for columns in the report 
	 * @return StringList
	 */
	private StringList getCharacteristicMasterColumnSelects() {
		
		StringList slCMSelects = new StringList();
		slCMSelects.add(INTERFACE);
		slCMSelects.add(DomainConstants.SELECT_ID);
		slCMSelects.add(ATTRIBUTE_CHARACTERISTICCATEGORY);
		slCMSelects.add(ATTRIBUTE_PGCATEGORYSPECIFICS);
		slCMSelects.add(ATTRIBUTE_PGCHARACTERISTIC);
		slCMSelects.add(ATTRIBUTE_PGCHARACTERISTICSPECIFICS);
		slCMSelects.add(ATTRIBUTE_PGDESIGNSPECIFICS);
		slCMSelects.add(ATTRIBUTE_PGMETHODSPECIFICS);
		slCMSelects.add(ATTRIBUTE_PGSAMPLING);
		slCMSelects.add(ATTRIBUTE_PGSUBGROUP);
		slCMSelects.add(ATTRIBUTE_PGLOWERSPECIFICATIONLIMIT);
		slCMSelects.add(ATTRIBUTE_PGLOWERROUTINERELEASELIMIT);
		slCMSelects.add(ATTRIBUTE_PGUPPERROUTINERELEASELIMIT);
		slCMSelects.add(ATTRIBUTE_PGUPPERSPECIFICATIONLIMIT);
		slCMSelects.add(ATTRIBUTE_PGREPORTTONEAREST);
		slCMSelects.add(ATTRIBUTE_PGREPORTTYPE);
		slCMSelects.add(ATTRIBUTE_PGISDESIGNDRIVEN);
		slCMSelects.add(ATTRIBUTE_PGRELEASECRITERIA);
		slCMSelects.add(ATTRIBUTE_PGACTIONREQUIRED);
		slCMSelects.add(ATTRIBUTE_PGCRITICALITYFACTOR);
		slCMSelects.add(ATTRIBUTE_PGCHARACTERISTICNOTES);
		slCMSelects.add(ATTRIBUTE_PGTMLOGIC);
		slCMSelects.add(ATTRIBUTE_PGTESTMETHODORIGIN);
		slCMSelects.add(ATTRIBUTE_PGMETHODNUMBER);
		slCMSelects.add(ATTRIBUTE_PGPLANTTESTINGRETESTING);
		slCMSelects.add(ATTRIBUTE_PGRETESTINGUOM);
		slCMSelects.add(ATTRIBUTE_PGBASIS);
		slCMSelects.add(ATTRIBUTE_PGTESTGROUP);
		slCMSelects.add(ATTRIBUTE_PGAPPLICATION);
		slCMSelects.add(ATTRIBUTE_PGPLANTTESTINGLEVEL);
		slCMSelects.add(ATTRIBUTE_UNITOFMEASURE);
		slCMSelects.add(pgV3Constants.PHYSICALID);
		//Added for defect id 46172 Starts
		slCMSelects.add(ATTRIBUTE_PGMETHODORIGIN);
		slCMSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slCMSelects.add(ATTRIBUTE_OBSCUREUOM);
		slCMSelects.add(DomainConstants.SELECT_DESCRIPTION);
		//Added for defect id 46172 Ends
		
		return slCMSelects;
	}
	
	/** this method updates the row in the report 
	 * @param workbook
	 * @param row
	 * @param hm
	 * @param columnCount1
	 * @return void
	 */
	private void updateRow(XSSFWorkbook workbook,XSSFRow row,Map<Integer,Object> hm, int columnCount1) {	
		
		
		String strCellValue = "";
		
		XSSFCellStyle style = workbook.createCellStyle();		
		try {
			for(int k=0;k<hm.size();k++){			
				
				Cell cell = row.createCell(columnCount1);
				//Added for defect id 46172 Starts 
				if(hm.get(k) instanceof StringList){
					strCellValue = (hm.get(k).toString().replace("[", "").replace("]", ""));
					
				} else{
					strCellValue = (String)hm.get(k);
				}
				//Added for defect id 46172 Ends
					if(UIUtil.isNotNullAndNotEmpty(strCellValue)){	
						cell.setCellValue(strCellValue);
						cell.setCellStyle(style);
					} else {
						cell.setCellValue(DomainConstants.EMPTY_STRING);
						cell.setCellStyle(style);
					}	
					columnCount1++;
				
			}
			rowCount = rowCount+1;
			
		}catch(Exception e) {
			outLog.print("Exception in  updateRow "+e+"\n");
			outLog.flush();	
		}
	}
	
	/** this method check-in the generated report in the report object
	 * @param workbook
	 * @param row
	 * @param hm
	 * @param columnCount1
	 * @return void
	 */
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	private void createCharacteristicMasterReportObject(Context context,String strReportPath,String strReportName,String strReportObjectId,String strUserName) throws MatrixException {
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends	 
		try {
			String strObjectId = strReportObjectId;
			if(UIUtil.isNullOrEmpty(strObjectId))
			{
				outLog.print("Report request object does not exist or not found");
				outLog.flush();
			}
			
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				DomainObject doObj = DomainObject.newInstance(context, strObjectId);
				doObj.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, STRFORMAT, strReportName, strReportPath);
				doObj.promote(context);
				String sFullPath = strReportPath.concat("/").concat(strReportName); 
				File file = new File(sFullPath);
				//below line commented for Local testing only - has to be uncommented for deployment
				Files.delete(file.toPath());
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
	
	/** this method fetches all the required information to check in the report
	 * @param context
	 * @param workbook
	 * @param strReportFileName
	 * @param strReportObjectId
	 * @return void
	 */
	private void checkInExcelFile(Context context,XSSFWorkbook workbook, String strReportFileName, String strReportObjectId) {
		try {
			
			File sbCharacteristicMasterReportFolder = null;
			String strReportPath= EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.CharacteristicMasterReportCTRLMJob.Worksheet.FilePath");
			String strReportName=EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.CharacteristicMaster.ReportName");
			String strReportExtension = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.CharacteristicMaster.ReportExtension");
			String strUserName = context.getUser();
			
			Date now = new Date();
			SimpleDateFormat smpdf = new SimpleDateFormat("MM-dd-yyyy");
			String strReportExtractedDate = smpdf.format(now);	
			String strUserNameTemp = strUserName.replace(".","-");
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format :Start
			StringBuilder sbFilename = new StringBuilder();
			if(UIUtil.isNotNullAndNotEmpty(strReportFileName)) {
				
				sbFilename.append(strReportFileName);
			} else {
				
			sbFilename.append(strReportName);
			}
			sbFilename.append(pgV3Constants.SYMBOL_UNDERSCORE);
			sbFilename.append(strReportExtractedDate);
			sbFilename.append(pgV3Constants.SYMBOL_UNDERSCORE);
			sbFilename.append(strUserNameTemp);
			sbFilename.append(pgV3Constants.SYMBOL_UNDERSCORE);
			sbFilename.append(System.currentTimeMillis());
			sbFilename.append(pgV3Constants.SYMBOL_DOT);
			sbFilename.append(strReportExtension);
			strReportName = sbFilename.toString();
			// To replace the first word of the existing file name format by use entered Input File Name.if this field is empty follow the default format : End
			
			sbCharacteristicMasterReportFolder = new File(strReportPath);
			if (!sbCharacteristicMasterReportFolder.exists())  {
				sbCharacteristicMasterReportFolder.mkdirs();
				}
			FileOutputStream outputStream = new FileOutputStream(sbCharacteristicMasterReportFolder.toString()+ File.separator + strReportName);
			workbook.write(outputStream);
			// code to create the object and checking the .excel file in that object
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
			createCharacteristicMasterReportObject(context,strReportPath,strReportName,strReportObjectId,strUserName);				
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
			outputStream.flush();
			outputStream.close();

		} 
		catch (Exception e) {
			
			outLog.print("Exception in  checkInExcelFile: "+e+"\n");
			outLog.flush();
		} 
	}
	
	/** this method returns the connected Test Methods seperated by comma
	 * @param context
	 * @param strPlmParameterId
	 * @return String
	 */
	
	public String getTestMethods(Context context, String strPlmParameterId)  throws FrameworkException {
		Map mpTestMethodData;
		DomainObject sPlmParameterObject= DomainObject.newInstance(context,strPlmParameterId);
		StringBuilder sb = new StringBuilder();
		StringList slTestMethod = new StringList();
		slTestMethod.add(DomainConstants.SELECT_NAME);
		//Added for defect id 46172 Starts
		String strValidTypes = (new StringBuffer()).append(pgV3Constants.TYPE_PGTESTMETHOD).append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_TESTMETHOD).toString();
		Pattern relPattern = new Pattern(pgV3Constants.REL_CHARACTERISTICTESTMETHOD);
		//Added for defect id 46172 Ends
		MapList getRelatedTestMethodsData = sPlmParameterObject.getRelatedObjects(context,
				relPattern.getPattern(),  //relationship pattern
				strValidTypes,                         // object pattern
				slTestMethod,                 // object selects
				null,              // relationship selects
				false,                        // to direction
				true,                       // from direction
				(short)1,                    // recursion level
				null,                        // object where clause
				null,//relationshipWhere
				0);//limit
		for(int i=0;i<getRelatedTestMethodsData.size();i++) {
			mpTestMethodData = (Map) getRelatedTestMethodsData.get(i);
			if(sb.length()==0) {
				sb.append(mpTestMethodData.get(DomainConstants.SELECT_NAME));
			}else {
			sb.append(pgV3Constants.SYMBOL_COMMA);
			sb.append(mpTestMethodData.get(DomainConstants.SELECT_NAME));
			}
			
		}
		//Updated for defect 46241 Starts
		return sb.toString().replace("\n"," ");
		//Updated for defect 46241 Ends
	}
	
	/** this method returns the Reference Documents separated by comma
	 * @param context
	 * @param strPlmParameterId
	 * @return String
	 */
	
	private String getTestMethodReferenceDocument(Context context, String strPlmParameterId) throws FrameworkException{
		
		Map mpTestMethodData;
			
		DomainObject sPlmParameterObject= DomainObject.newInstance(context,strPlmParameterId);
		//Added for defect id 46172 Starts
		String strValidTypes = (new StringBuffer()).append(pgV3Constants.TYPE_PGSTANDARDOPERATINGPROCEDURE).append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_PGILLUSTRATION).append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_PGQUALITYSPECIFICATION).append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_TESTMETHOD).append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_PGTESTMETHOD).toString();
		//Added for defect id 46172 Starts
		StringBuilder sb = new StringBuilder();
		StringList slTestMethod = new StringList();
		slTestMethod.add(DomainConstants.SELECT_NAME);
		Pattern relPattern = new Pattern(pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT);
		MapList getRelatedTestMethodsData = sPlmParameterObject.getRelatedObjects(context,
				relPattern.getPattern(),  //relationship pattern
				strValidTypes,                         // object pattern
				slTestMethod,                 // object selects
				null,              // relationship selects
				false,                        // to direction
				true,                       // from direction
				(short)1,                    // recursion level
				null,                        // object where clause
				null,//relationshipWhere
				0);//limit
		for(int i=0;i<getRelatedTestMethodsData.size();i++) {
			 mpTestMethodData = (Map) getRelatedTestMethodsData.get(i);
			if(sb.length()==0) {
				sb.append(mpTestMethodData.get(DomainConstants.SELECT_NAME));
			}else {
			sb.append(pgV3Constants.SYMBOL_COMMA);
			sb.append(mpTestMethodData.get(DomainConstants.SELECT_NAME));
			}
			
		}
		//Updated for defect id 46241 Starts
		return sb.toString().replace("\n"," ");
		//Updated for defect id 46241 Ends
	}
	
	/** this method returns the dimension data of plmParameter object
	 * @param connectedInterfaces
	 * @param mpDimensionDisplayVals
	 * @return String
	 */
	private String getDimensionData(StringList connectedInterfaces, HashMap<String, StringList> mpDimensionDisplayVals) {
		String sDimension = "";
		StringList slCharacteristicInterfaces = connectedInterfaces;
		sDimension = identifyDimensionUsingInterface(slCharacteristicInterfaces,mpDimensionDisplayVals);
		
		return sDimension;
	}
	
	/** this method process the dimensions depending on connected interface
	 * @param slInterfaceList
	 * @param mpDimensionDisplayVals
	 * @return String
	 */
	private String identifyDimensionUsingInterface(StringList slInterfaceList, Map<String, StringList> mpDimensionDisplayVals) {
		String sReturnDimension = DomainConstants.EMPTY_STRING;
	  	Map<String,Object> mpDimensionNames;
	  	if(mpDimensionDisplayVals.size() > 0 ) {
	  		mpDimensionNames = prepareMapForName(mpDimensionDisplayVals);
	  		
	  		if(!mpDimensionNames.isEmpty()) {
		  		String sValue;
			  	for(int i = 0 ; i < slInterfaceList.size(); i++) {
			  		sValue = slInterfaceList.get(i);
			  		if(UIUtil.isNotNullAndNotEmpty(sValue) && mpDimensionNames.containsKey(sValue)) {
			  			sReturnDimension = (String) mpDimensionNames.get(sValue);
			  			break;
			  		}
			  	}
		  	}
	  	}
		return sReturnDimension;
	}
	
	
	/** this method returns the display names of dimensions
	 * @param mpReturnMap
	 * @return Map
	 */
	public Map<String,Object> prepareMapForName(Map<String, StringList> mpReturnMap) {
		HashMap<String,Object> mpDisplayName = new HashMap<>();
		StringList slActual = mpReturnMap.get("field_choices");
		StringList slDisplay = mpReturnMap.get("field_display_choices"); 
		for(int i = 0 ; i < slActual.size(); i++ ) {
			mpDisplayName.put(slActual.get(i),slDisplay.get(i) );
		}
		return mpDisplayName;
	}

	/**Added for defect id 46172
	 * @param mpGetRelatedCMData
	 * @return
	 */
	private String getUnitOfMeasure(Map<String,Object> mpGetRelatedCMData) {
		String strUoM = "";
		if(UIUtil.isNotNullAndNotEmpty((String)mpGetRelatedCMData.get(ATTRIBUTE_UNITOFMEASURE))) {
			strUoM = (String)mpGetRelatedCMData.get(ATTRIBUTE_UNITOFMEASURE);
		} else if(UIUtil.isNotNullAndNotEmpty((String)mpGetRelatedCMData.get(ATTRIBUTE_OBSCUREUOM))) {
			strUoM = (String)mpGetRelatedCMData.get(ATTRIBUTE_OBSCUREUOM);
		} 
		return strUoM;
	}
	
	/**Added for defect id 46172
	 * @param strValue
	 * @return
	 */
	private String getAttributeValue(Map mpMultiValueCMData, String attribute) {
		String sFinalValue = "";
		if(mpMultiValueCMData.size()>0 && mpMultiValueCMData.containsKey(attribute)) {
			StringList slValue = (StringList)mpMultiValueCMData.get(attribute);
			if(!slValue.isEmpty()) {
				sFinalValue = (slValue.toString().replace("[", "").replace("]", "").replace(",","|"));
			}
		}
		
		return sFinalValue;
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
			}
			finally {
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
		
		/**Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567
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
