package com.pg.dsm.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

//DSM Reports 2018x6_Feb Requirements 40386,40891,40892
public class pgCertificationRollUpReport  implements DomainConstants {	
	public pgCertificationRollUpReport (Context context, String[] args) {
		
}
	private static final String HYPERLINK = "Hyperlink:|";
	private static final String HYPERLINK_PIPE = "|";
	private static final String HYPERLINK_COMPARE = "Hyperlink:";
	private static final String EMXCPN = "emxCPN";
	private static final String USERNAME = "UserName";
	private static final String REPORTFILENAME = "ReportFileName";
	private static final String REPORTOBJECTID = "ReportObjectId";
	private static final String CPNSTRINGRESOURCE = "emxCPNStringResource";
	private static final String EBP = "EBP";
	private static final String YES = "Yes";
	private static final String NO = "No";
	private static final String ACCESS_ERR = "No Access to Part";
	private static final String TYPE_ERR = "Invalid Part Name or Type for this report";
	private static final String NAME_ERR = "Part Name and Revision is required";
	private static final String EXPIRED = "Expired";
	private static final String STR_PARTNERS_PG = "Partners_PG";
	private static final String STR_MULTI_OWNERSHIP_FOR_OBJ = "Multiple Ownership For Object";
	private static final String DENIED = "#DENIED!";
	private static final String COMMA = ",";
	private static final String ERR_MESSAGE = "Creation of Object Failed";
	private static final String DATE_YMDHS = "yyyy-MM-dd HH:mm:ss";
	private static final String DIRECTORY_ERR = "Could not create directory";
	private PrintWriter outLog = null;
	private static final String HYPERLINKASINPUT = "HyperlinkAsInput";
	private static final String REALTIMEPROCESS = "RealTimeProcess";
	private static final String SHEETNAME = "Sheet1";
	private static final String SELECTEDCERTIFICATIONS = "SelectedCertifications";
	private static final String EMXCPNSTRINGRESOURCE= "emxCPNStringResource";
	private static final String CERTIFICATION_FROMPOLICY = "frommid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"].torel["+pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS+"].from.policy";
	private static final String CERTIFICATION_NAME = "frommid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"].torel["+pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS+"].to.name";
	private static final String CERTIFICATION_ID = "frommid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"].torel["+pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS+"].to.id";
	private static final String CERTIFICATION_STATUS = "frommid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"].attribute[pgCertficationStatus]";
	private static final String CERTIFICATION_EXPDATE = "frommid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"]."+pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE;
	private static final String RM_CERTIFICATION_EXPDATE = "tomid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"]."+pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE;
	private static final String RM_CERTIFICATION_STATUS = "tomid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"].attribute[pgCertficationStatus]";
	private static final String RM_CERTIFICATION_MEPID = "tomid["+pgV3Constants.RELATIONSHIP_PGMEPSEPCERTIFICATION+"].fromrel.to.id";
	private static final String RM_CERTIFICATION_NAME = "to.name";
					 
	private static final String CERTIFICATION_REGION = "tomid[pgPLIRegionTopgPLIMaterialCertifications].from.name";
	private static final String CERTIFICATION_AREA = "tomid[pgPLIAreaTopgPLIMaterialCertifications].from.name";
	private static final String CERTIFICATION_GROUP ="tomid[pgPLIGroupTopgPLIMaterialCertifications].from.name";
	private static final String CERTIFICATION_MARKET = "tomid[pgCountriesCertificationClaimed].from.name";
	private int iHyperLinkLimit;
	private static final String USER_AGENT ="person_UserAgent";
	private static final String REL_EBOMSUBSTITUTE = PropertyUtil.getSchemaProperty(null, "relationship_EBOMSubstitute");
	private static final MapList mlLogDetails = new MapList();
	private static final String SYSTEMDATEFORMAT = eMatrixDateFormat.getEMatrixDateFormat();
	private static final String STATE_IN_WORK = "In Work";
	private static final String STATE_FROZEN = "Frozen";
	private static final String STATE_APPROVED = "Approved";
	private static final String STATE_RELEASE = "Released";
	private static final String STATE_PENDINGOBSOLETE = "Pending Obsolete";	
	private static final String STATE_OBSOLETE = "Obsolete";	
	private static final String MANUFACTUREREQUIVALENT = "Manufacturer Equivalent";
	private static final String ECPART = "EC Part";
	private static final String RELEASE = "Release";
	private static final String BLANKROW = "BlankRow";
	
	//Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567 - Start
	private String strOriginatingSource = "";
	private static final String SPECREADER = "SpecReader";
	private static final String ENOVIA = "ENOVIA";
	private static final String ORIGINATINGSOURCE = "OriginatingSource";
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
	
	//April_CW Certification Roll-up Report 
	public void generateCertificationReport(Context context, String args[]) {		
		try {
		StringBuilder sbLogFolder = new StringBuilder();
		File fLogFolder = null;
		String strDirectoryNotCreated = DIRECTORY_ERR;
		String strStartTime = null;
		
		HashMap<String, String> hmArgs = (HashMap) JPO.unpackArgs(args);
		String strUserName = hmArgs.get(USERNAME);
		String strPartNames = hmArgs.get("GCAS");
		String strReportFileName = hmArgs.get(REPORTFILENAME);
		String strReportObjectId = hmArgs.get(REPORTOBJECTID);
		String strSelectedCertifications = hmArgs.get(SELECTEDCERTIFICATIONS);
		//Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567 - Start
		strOriginatingSource = hmArgs.get(ORIGINATINGSOURCE);
		//Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567 - End
		
		String strHyperlink = hmArgs.get(HYPERLINKASINPUT);
		String strRealTimeProcess = hmArgs.get(REALTIMEPROCESS);
		Map<String, Object> mPassValue = new HashMap<>();
		mPassValue.put(REALTIMEPROCESS, strRealTimeProcess);
		mPassValue.put(HYPERLINKASINPUT, strHyperlink);
		String configLOGFilePath = "";
		configLOGFilePath = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(), "emxCPN.DSMReportCTRLMJob.Log.FilePath");
		sbLogFolder.append(configLOGFilePath).append(java.io.File.separator);
		fLogFolder = new File(sbLogFolder.toString());
		if (!fLogFolder.exists() && !fLogFolder.mkdirs()) {
			throw new IOException(strDirectoryNotCreated + fLogFolder);
		}
		outLog = new PrintWriter(new FileOutputStream(fLogFolder.toString() + File.separator + "CertificationReportLog.log", true));
		strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());
		if (UIUtil.isNotNullAndNotEmpty(strUserName) && (UIUtil.isNotNullAndNotEmpty(strPartNames))) {
			DomainObject doObj = DomainObject.newInstance(context, strReportObjectId);
			outLog.print("-------Report requested by: " + strUserName + " | " + strStartTime + "--------\n");
			outLog.print("Parts: " + strPartNames + "\n");
			outLog.print("Report Object Id: " + strReportObjectId + "\n");
			outLog.flush();
			doObj.promote(context);
			getCertificationReportProcess(context, strUserName, strPartNames, strReportFileName, strReportObjectId, strSelectedCertifications, mPassValue);
		} else {
			outLog.print("-------Report requested by: " + strUserName + " : " + strStartTime + "--------\n");
			outLog.print("Report cannot be generated. Check Report Object for request details: " + strReportObjectId	+ "\n");
			outLog.flush();
		}
		}catch(Exception e) {
			outLog.print("Exception in  generateCertificationReport: "+e+"\n");
			outLog.flush();
		}
	}
	

	/**This method is used to run the Certification report 
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
	private void getCertificationReportProcess(Context context, String strUserName, String strPartNames, String strReportFileName, String strReportObjectId, String strSelectedCertifications, Map<String,Object> mPassValue)throws Exception {
				
		MapList mlCertificationReportFinal = new MapList();
		MapList mlPart = new MapList();
		StringList slIndividualPartNames = StringUtil.split(strPartNames, COMMA);
		Map<String, Object> mp = new HashMap<>();
		Map<String, Object> mpRow = new HashMap<>();
		MapList mpCertificationReportDetails = new MapList();
		MapList mlCertificationReportDetailsAll = new MapList();
		MapList mpCertificationReportSummary = new MapList();
		MapList mlCertificationReportSummaryAll = new MapList();
		String strPartObjectId = DomainConstants.EMPTY_STRING;
		String strPartObjectName = DomainConstants.EMPTY_STRING;
		int iPartMLSize = 0;
		//Added for 2022x-04 Dec CW Defect 54805 - starts
		boolean isContextPushed = false;
		//Added for 2022x-04 Dec CW Defect 54805 - Ends
		boolean bInputPartAccess = false;
		try {
		String strStartTime = null; 			
			strStartTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());	  			
			outLog.print("-------Report requested by: " +strUserName+" | "+strStartTime+"--------\n");
		 	outLog.print("Parts: "+strPartNames+"\n");
		 	outLog.print("Certifications: "+strSelectedCertifications+"\n");
		 	outLog.print("Report Object Id: "+strReportObjectId+"\n");
		 	outLog.flush(); 
		 	String strReportSheetName = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CertificationReport.Worksheet.Name.CertificationReport");
			String strLogSheetName = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CertificationReport.Worksheet.Name.CertificationReportLog");
			XSSFWorkbook workbook = new XSSFWorkbook();
		 	XSSFSheet sheetReport = createSheet(context, workbook, strReportSheetName);
		 	XSSFSheet sheetLog =createSheet(context, workbook, strLogSheetName);
		 	mlPart = getValidPart(context, slIndividualPartNames, strUserName);
		 	
		 	if (null != mlPart && !mlPart.isEmpty()) {
				outLog.print("Report started for: " + strUserName + ": " + strPartObjectId + "|" + strPartObjectName + "\n");
				outLog.flush();
				iPartMLSize = mlPart.size();
				for (int i = 0; i < iPartMLSize; i++) {
					mp = (Map) mlPart.get(i);
					strPartObjectId = (String) mp.get(DomainConstants.SELECT_ID);
					bInputPartAccess = accessCheck(context, strUserName, strPartObjectId);
					if (bInputPartAccess) {
						
						DomainObject dObjPart = DomainObject.newInstance(context, strPartObjectId);
						//Added for 2022x-04 Dec CW Defect 54805 - starts
						//Push context for providing access to EBP users
						ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
						isContextPushed = true; 
						//Added for 2022x-04 Dec CW Defect 54805 - Ends
						mpCertificationReportDetails = getCertificationReportDetail(context, dObjPart,strSelectedCertifications);
						mpCertificationReportSummary = getCertificationReportSummary(context, mp, strSelectedCertifications, mpCertificationReportDetails);
						//Added for 2022x-04 Dec CW Defect 54805 - starts
						ContextUtil.popContext(context);
						//Push context for providing access to EBP users
						isContextPushed = false;
						//Added for 2022x-04 Dec CW Defect 54805 - Ends
					}
					
					if(!mpCertificationReportSummary.isEmpty()){
						for(int iRow=0;iRow<mpCertificationReportSummary.size();iRow++){
							mpRow = (Map<String, Object>) mpCertificationReportSummary.get(iRow);
							mlCertificationReportSummaryAll.add(mpRow);	
						}
					}
					
					if(!mpCertificationReportDetails.isEmpty()){
						for(int jRow=0;jRow<mpCertificationReportDetails.size();jRow++){
							mpRow = (Map<String, Object>) mpCertificationReportDetails.get(jRow);
							mlCertificationReportDetailsAll.add(mpRow);	
						}
					}
				}
					
			}
		 	mPassValue.put("UserName", strUserName);
		 	mPassValue.put("ReportFileName", strReportFileName);
		 	mPassValue.put("ReportId", strReportObjectId);
		 	mlCertificationReportFinal = calculateRollUp(context, mlCertificationReportDetailsAll, mlCertificationReportSummaryAll);
			updateExcelWorkbook(context, mlCertificationReportFinal, workbook, sheetReport, sheetLog, mPassValue);
			mlCertificationReportDetailsAll.clear();
			mlCertificationReportSummaryAll.clear();
			mlCertificationReportFinal.clear();
			mlLogDetails.clear();
			String strEndTime = null;
			strEndTime = new SimpleDateFormat(DATE_YMDHS).format(new Date());
			outLog.print("Report completed for: " + strUserName + ": " + strPartNames + "|" + strReportFileName	+ "-------\n");
			outLog.print("-------Time completed: " + strEndTime + "-------\n");
			outLog.print("-------\n");			
			outLog.flush();
		} catch (Exception e) {
			outLog.print("Exception in  getCertificationReportProcess: " + strUserName + ": " + e + "\n");
			outLog.flush();
		} finally {
			//Added for 2022x-04 Dec CW Defect 54805 - starts
			if(isContextPushed)
				ContextUtil.popContext(context);
			//Added for 2022x-04 Dec CW Defect 54805 - Ends
			outLog.close();
		}
	}
	


	/**This method is used to build the rollup section
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
private MapList calculateRollUp(Context context,MapList mlCertificationReportDetailsAll, MapList mlCertificationReportSummaryAll) throws FrameworkException {
	MapList mpFinalReport = new MapList();
	Map<String, Object> mpRow = new HashMap<>();
	try {
		if(!mlCertificationReportSummaryAll.isEmpty()){
			for(int iRow=0;iRow<mlCertificationReportSummaryAll.size();iRow++){
				mpRow = (Map<String, Object>) mlCertificationReportSummaryAll.get(iRow);
				mpFinalReport.add(mpRow);	
			}
		}
		
		if(!mlCertificationReportDetailsAll.isEmpty()){
			for(int jRow=0;jRow<mlCertificationReportDetailsAll.size();jRow++){
				mpRow = (Map<String, Object>) mlCertificationReportDetailsAll.get(jRow);
				mpFinalReport.add(mpRow);	
			}
		}
		
	} catch (Exception e) {
		outLog.print("Exception in calculateRollUp: "+e+"\n");
		outLog.flush();
	} 
	return mpFinalReport;
}
	
	
/**This method is used to get the MEP Details
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private MapList getCertificationReportDetail(Context context,DomainObject dObjPart, String strSelectedCertifications) throws FrameworkException {
	
	MapList mpFinalReportDetail = new MapList();
	MapList mpFinalReportDetailTemp = new MapList();
	Map<String, Object> mpRow = new HashMap<>();
	Map<String, Object> mpSubsInfo = new HashMap<>();
	String strChildPartName = DomainConstants.EMPTY_STRING;
	String strChildPartRevision = DomainConstants.EMPTY_STRING;
	String strChildPartTitle = DomainConstants.EMPTY_STRING;
	String strSubPartName = DomainConstants.EMPTY_STRING;
	String strSubPartRevision = DomainConstants.EMPTY_STRING;
	String strSubPartTitle = DomainConstants.EMPTY_STRING;
	String strEBOMSubstitute = DomainConstants.EMPTY_STRING;
	String strEBOMSubstituteId = DomainConstants.EMPTY_STRING;
	String strChildPartId = DomainConstants.EMPTY_STRING;
	String strMEPExpDate = DomainConstants.EMPTY_STRING;
	String strRMExpDate = DomainConstants.EMPTY_STRING;
	String strRMFulfilled = DomainConstants.EMPTY_STRING;
	String strAnalyzedClaim = DomainConstants.EMPTY_STRING;
	MapList mpBOMData = new MapList();
	MapList mpMEPDetails = new MapList();
	Map mpChild = null;		
	Map mpMEP = null;
	boolean bHasMultipleSubs = false;
	int iEBOMSubstituteIdSize = 0;
	Date tmpDate;
	//Added code for May22_CW defect for 47362 Strats
	String strComparePart = "";
	StringBuffer sbPartsIdCompare = new StringBuffer();
	StringBuffer sbSubsIdCompare = new StringBuffer();
	//Added code for May22_CW defect for 47362 Ends
	StringList slEBOMSubstituteId = new StringList();
	StringList slObjSelects = new StringList();
	slObjSelects.add(DomainConstants.SELECT_NAME);
	slObjSelects.add(DomainConstants.SELECT_REVISION);
	slObjSelects.add(DomainConstants.SELECT_ID);
	slObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
	SimpleDateFormat formatter = new SimpleDateFormat (SYSTEMDATEFORMAT);
	SimpleDateFormat smpdateformat = new SimpleDateFormat("MMM d, yyyy");
	Map mpData = dObjPart.getInfo(context, slObjSelects);
	String strName = (String)mpData.get(DomainConstants.SELECT_NAME);
	String strRevision = (String)mpData.get(DomainConstants.SELECT_REVISION);
	String strId = (String)mpData.get(DomainConstants.SELECT_ID);
	
	boolean bChildPartIsPrimary = false;
	try {
		mpBOMData = expandEBOM(context, dObjPart);
		sbPartsIdCompare.setLength(0);
		if(!mpBOMData.isEmpty()){
			for(int iChildPart=0;iChildPart<mpBOMData.size();iChildPart++){				
				mpFinalReportDetailTemp.clear();
				mpChild = (Map<String, Object>) mpBOMData.get(iChildPart);
				strChildPartId = (String) mpChild.get(DomainConstants.SELECT_ID);
				strChildPartName = (String) mpChild.get(DomainConstants.SELECT_NAME);
				strChildPartRevision = (String) mpChild.get(DomainConstants.SELECT_REVISION);
				strChildPartTitle = (String) mpChild.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				strEBOMSubstitute = (String) mpChild.get("frommid["+REL_EBOMSUBSTITUTE+"]");
				bChildPartIsPrimary = isPrimary(strEBOMSubstitute);
				//Added code for May22_CW defect for 47362 Ends
				strComparePart = pgV3Constants.SYMBOL_TILDA+strChildPartId+pgV3Constants.SYMBOL_TILDA;
				if((UIUtil.isNotNullAndNotEmpty(strChildPartId) && !sbPartsIdCompare.toString().contains(strComparePart)) || !bChildPartIsPrimary){
					if(bChildPartIsPrimary) {
						sbPartsIdCompare.append(strComparePart).append(pgV3Constants.SYMBOL_COMMA);
					}
				//Added code for May22_CW Req 42705 End	
				mpMEPDetails = getMEPCertification(context, strChildPartId, strSelectedCertifications);
							
				if(!mpMEPDetails.isEmpty()){					
					for(int iMEPRow=0;iMEPRow<mpMEPDetails.size();iMEPRow++){
						mpMEP = (Map<String, Object>) mpMEPDetails.get(iMEPRow);
						mpRow = new HashMap();					
						strMEPExpDate = (String) mpMEP.get("MEPClaimExpiration");
						if (UIUtil.isNotNullAndNotEmpty(strMEPExpDate)) {
							tmpDate = formatter.parse(strMEPExpDate);
							strMEPExpDate = smpdateformat.format(tmpDate);
						}
					
						strRMExpDate = (String) mpMEP.get("RMClaimExpiration");
						if (UIUtil.isNotNullAndNotEmpty(strRMExpDate)) {
							tmpDate = formatter.parse(strRMExpDate);
							strRMExpDate = smpdateformat.format(tmpDate);
						}
							
						strRMFulfilled = (String) mpMEP.get("RMFulfilled");
						if (!UIUtil.isNotNullAndNotEmpty(strRMFulfilled)) {
							strRMFulfilled = NO;
						}
														
						mpRow.put("ParentRM", strChildPartId);
						mpRow.put("PartId", strId);
						mpRow.put("MEPId", (String) mpMEP.get("MEPId"));
						mpRow.put("RMId", strChildPartId);
						mpRow.put("PartName", strName);
						mpRow.put("PartRev", strRevision);
						mpRow.put("CertificationAnalyzed", mpMEP.get("CertificationAnalyzed"));
						mpRow.put("MEPFulfilled", mpMEP.get("MEPFulfilled"));
						mpRow.put("RMFulfilled", strRMFulfilled);
						mpRow.put("MaterialName", strChildPartName);
						mpRow.put("MaterialRev", strChildPartRevision);
						mpRow.put("MaterialTitle", strChildPartTitle);
						mpRow.put("Substitute", "Primary");
						mpRow.put("RMClaimStatus", mpMEP.get("RMClaimStatus"));
						mpRow.put("RMClaimExpiration", strRMExpDate);
						mpRow.put("MEPName", (String) mpMEP.get("MEPName"));
						mpRow.put("MEPRev", (String) mpMEP.get("MEPRev"));
						mpRow.put("MEPState", (String) mpMEP.get("MEPState"));
						mpRow.put("MEPClaimStatus", (String) mpMEP.get("MEPClaimStatus"));
						mpRow.put("MEPClaimExpiration", strMEPExpDate);		
						mpFinalReportDetailTemp.add(mpRow);
					}
				} 
			} 
				if(!bChildPartIsPrimary) {
				sbSubsIdCompare.setLength(0);	
				bHasMultipleSubs = false;					
				if(mpChild.get("frommid["+REL_EBOMSUBSTITUTE+"].to.id") instanceof StringList){
					slEBOMSubstituteId = (StringList) (mpChild.get("frommid["+REL_EBOMSUBSTITUTE+"].to.id"));
					iEBOMSubstituteIdSize = slEBOMSubstituteId.size();
					bHasMultipleSubs = true;				
				} else {
					strEBOMSubstituteId = (String) mpChild.get("frommid["+REL_EBOMSUBSTITUTE+"].to.id");
					iEBOMSubstituteIdSize = 1;
				}				
				for(int k=0;k<iEBOMSubstituteIdSize;k++){
					if (bHasMultipleSubs) {
						strEBOMSubstituteId = (String)slEBOMSubstituteId.get(k);
					} 
					//Added code for May22_CW defect for 47362 Starts
					strComparePart = pgV3Constants.SYMBOL_TILDA+strEBOMSubstituteId+pgV3Constants.SYMBOL_TILDA;
					//Added code for May22_CW Req 42705 End	
					if (UIUtil.isNotNullAndNotEmpty(strEBOMSubstituteId) && !sbSubsIdCompare.toString().contains(strComparePart)) {
						sbSubsIdCompare.append(strComparePart).append(pgV3Constants.SYMBOL_COMMA);
						DomainObject domSubObj = DomainObject.newInstance(context, strEBOMSubstituteId);
						mpSubsInfo = domSubObj.getInfo(context,slObjSelects);
						strSubPartName = (String)mpSubsInfo.get(DomainConstants.SELECT_NAME);
						strSubPartRevision = (String)mpSubsInfo.get(DomainConstants.SELECT_REVISION);
						strSubPartTitle = (String)mpSubsInfo.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
						
						mpMEPDetails = getMEPCertification(context, strEBOMSubstituteId, strSelectedCertifications);
						for(int iMEPRow=0;iMEPRow<mpMEPDetails.size();iMEPRow++){
							mpMEP = (Map<String, Object>) mpMEPDetails.get(iMEPRow);
							strMEPExpDate = (String) mpMEP.get("MEPClaimExpiration");
							if (UIUtil.isNotNullAndNotEmpty(strMEPExpDate)) {
								tmpDate = formatter.parse(strMEPExpDate);
								strMEPExpDate = smpdateformat.format(tmpDate);
							}
							
							strRMExpDate = (String) mpMEP.get("RMClaimExpiration");
							if (UIUtil.isNotNullAndNotEmpty(strRMExpDate)) {
								tmpDate = formatter.parse(strRMExpDate);
								strRMExpDate = smpdateformat.format(tmpDate);
							}
							
							strRMFulfilled = (String) mpMEP.get("RMFulfilled");
							if (!UIUtil.isNotNullAndNotEmpty(strRMFulfilled)) {
								strRMFulfilled = NO;
							}
							
							mpRow = new HashMap();							
							mpRow.put("ParentRM", strChildPartId);
							mpRow.put("PartId", strId);
							mpRow.put("MEPId", (String) mpMEP.get("MEPId"));
							mpRow.put("RMId", strEBOMSubstituteId);
							mpRow.put("PartName", strName);
							mpRow.put("PartRev", strRevision);
							mpRow.put("CertificationAnalyzed", (String) mpMEP.get("CertificationAnalyzed"));
							mpRow.put("MEPFulfilled", (String) mpMEP.get("MEPFulfilled"));
							mpRow.put("RMFulfilled", strRMFulfilled);
							mpRow.put("MaterialName", strSubPartName);
							mpRow.put("MaterialRev", strSubPartRevision);
							mpRow.put("MaterialTitle", strSubPartTitle);
							mpRow.put("Substitute", "Substitute");
							mpRow.put("RMClaimStatus", (String) mpMEP.get("RMClaimStatus"));
							mpRow.put("RMClaimExpiration", strRMExpDate);
							mpRow.put("MEPName", (String) mpMEP.get("MEPName"));
							mpRow.put("MEPRev", (String) mpMEP.get("MEPRev"));
							mpRow.put("MEPState", (String) mpMEP.get("MEPState"));
							mpRow.put("MEPClaimStatus", (String) mpMEP.get("MEPClaimStatus"));
							mpRow.put("MEPClaimExpiration", strMEPExpDate);	
							mpFinalReportDetailTemp.add(mpRow);							
						}
					}
				}
			}	
			
			mpFinalReportDetailTemp.sort("CertificationAnalyzed","ascending","string");
			if(!mpFinalReportDetailTemp.isEmpty()){
				for(int jRow=0;jRow<mpFinalReportDetailTemp.size();jRow++){
					mpRow = (Map<String, Object>) mpFinalReportDetailTemp.get(jRow);
					mpFinalReportDetail.add(mpRow);	
				}
				//Added code for May22_CW defect for 47362 Start
				mpRow = new HashMap();							
				mpRow.put("AddRow", BLANKROW);	
				mpFinalReportDetail.add(mpRow);
				//Added code for May22_CW defect for 47362 Ends
			}
			
			}
		} 
	} catch (Exception e) {
		outLog.print("Exception in  getCertificationReportDetail: " + e + "\n");
		outLog.flush();
	} 
	return mpFinalReportDetail;
}
	


/**This method is used to generate report summary
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private MapList getCertificationReportSummary(Context context, Map mp, String strSelectedCertifications, MapList mpCertificationReportDetails) throws FrameworkException {
	MapList mpFinalReportSummary = new MapList();
	Map<String, Object> mpSummaryRow = new HashMap<>();
	Map<String, Object> mpPartDetail = new HashMap<>();
	Map<String, Object> mpFulfilment = new HashMap<>();
	String strClaimOnPart = DomainConstants.EMPTY_STRING;
	String strClaim = DomainConstants.EMPTY_STRING;
	String strCertificationMarket = DomainConstants.EMPTY_STRING;
	String strCertificationRegion = DomainConstants.EMPTY_STRING;
	String strCertificationArea = DomainConstants.EMPTY_STRING;
	String strCertificationGroup = DomainConstants.EMPTY_STRING;
	String strRMFulfil = DomainConstants.EMPTY_STRING;
	String strMEPFulfil = DomainConstants.EMPTY_STRING;
	String strProductPartFulfil = DomainConstants.EMPTY_STRING;
	try {
		String strPartObjectName = (String) mp.get(DomainConstants.SELECT_NAME);
		String strPartObjectRevision = (String) mp.get(DomainConstants.SELECT_REVISION);
		String strPartObjectId = (String) mp.get(DomainConstants.SELECT_ID);
		StringList slRequestedClaims = StringUtil.split(strSelectedCertifications,"~");		
		for (int i = 0;i<slRequestedClaims.size();i++) {
			mpSummaryRow = new HashMap();
			strClaim = slRequestedClaims.get(i);			
			mpPartDetail = getPartDetail(context, strPartObjectId, strClaim);
			if (!mpPartDetail.isEmpty()) {
				strCertificationMarket = (String) mpPartDetail.get("CertificationMarket");
				strCertificationRegion = (String) mpPartDetail.get("CertificationRegion");
				strCertificationArea = (String) mpPartDetail.get("CertificationArea");
				strCertificationGroup = (String) mpPartDetail.get("CertificationGroup");
				strClaimOnPart = (String) mpPartDetail.get("CertificationClaimOnPart");
			} 
			mpFulfilment = getFulfillmentSummary(strPartObjectId, strClaim, mpCertificationReportDetails);
			if (!mpFulfilment.isEmpty()) {
				strRMFulfil = (String) mpFulfilment.get("RMFulfilled");
				strMEPFulfil = (String) mpFulfilment.get("MEPFulfilled");
				strProductPartFulfil = (String) mpFulfilment.get("ProductPartClaimFulfilled");				
			} 
			
			mpSummaryRow.put("ParentRM", "");
			mpSummaryRow.put("PartName", strPartObjectName);
			mpSummaryRow.put("PartRev", strPartObjectRevision);
			mpSummaryRow.put("PartId", strPartObjectId);
			mpSummaryRow.put("CertificationClaimOnPart", strClaimOnPart);
			mpSummaryRow.put("CertificationAnalyzed", strClaim);
			mpSummaryRow.put("ProductPartClaimFulfilled", strProductPartFulfil);
			mpSummaryRow.put("MEPFulfilled", strMEPFulfil);
			mpSummaryRow.put("RMFulfilled", strRMFulfil);
			mpSummaryRow.put("CertificationMarket", strCertificationMarket);
			mpSummaryRow.put("CertificationRegion", strCertificationRegion);
			mpSummaryRow.put("CertificationArea", strCertificationArea);
			mpSummaryRow.put("CertificationGroup", strCertificationGroup);	
			mpFinalReportSummary.add(mpSummaryRow);			
		}
		mpSummaryRow = new HashMap();
		mpFinalReportSummary.add(mpSummaryRow);
	} catch (Exception e) {
		outLog.print("Exception in  getCertificationReportSummary: " + e + "\n");
		outLog.flush();
	} 
		return mpFinalReportSummary;	
}
	

/**This method is used to calculate the Claim Fulfillment
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private Map getFulfillmentSummary(String strPartObjectId, String strClaim, MapList mlCertificationReportDetails) throws FrameworkException {
	Map<String, Object> mpRow = new HashMap<>();
	Map<String, Object> mpReturn = new HashMap<>();
	String strRMVal = DomainConstants.EMPTY_STRING;
	String strMEPVal= DomainConstants.EMPTY_STRING;
	String strPartId = DomainConstants.EMPTY_STRING;
	String strAnalyzedClaim = DomainConstants.EMPTY_STRING;
	String strMEPState = "";
	String strMEPReturn = YES;
	String strRMReturn= YES;
	String strProductFulfilled=NO;	
	boolean bRMComplete = false;
	boolean bMEPComplete = false;
	boolean bRMClaimFound = false;
	boolean bMEPClaimFound = false;
	boolean bMEPReleased = false;
	boolean bHasReleasedMEP = false;
	try {
		if(!mlCertificationReportDetails.isEmpty()){
			strMEPReturn = YES;
			strRMReturn= YES;
			strProductFulfilled=YES;
			for(int jRow=0;jRow<mlCertificationReportDetails.size();jRow++){
				mpRow = (Map<String, Object>) mlCertificationReportDetails.get(jRow);
				strPartId = (String) mpRow.get("PartId");
				strAnalyzedClaim = (String) mpRow.get("CertificationAnalyzed");
				//Added code for May22_CW defect for 47362 Starts
				strMEPState = (String) mpRow.get("MEPState");				
				if (UIUtil.isNotNullAndNotEmpty(strMEPState) && RELEASE.equalsIgnoreCase(strMEPState)) {					
					bMEPReleased = true;					
				} else {					
					bMEPReleased = false;
				}				
				if (bMEPReleased) {
					bHasReleasedMEP = true;					
					//Added code for May22_CW defect for 47362 End	
					if (strPartId.equalsIgnoreCase(strPartObjectId) && strAnalyzedClaim.equalsIgnoreCase(strClaim) && !bRMComplete) {
						strRMVal = (String) mpRow.get("RMFulfilled");
						bRMClaimFound = true;					
						if (UIUtil.isNotNullAndNotEmpty(strRMVal)) {
							if (strRMVal.equalsIgnoreCase(NO) || strRMVal.equalsIgnoreCase(EXPIRED)) {
								bRMComplete = true;
								strRMReturn = NO;
							}
						} else {
							bRMComplete = true;
							strRMReturn = NO;
						}
					}
					if (strPartId.equalsIgnoreCase(strPartObjectId) && strAnalyzedClaim.equalsIgnoreCase(strClaim) && !bMEPComplete) {
						strMEPVal = (String) mpRow.get("MEPFulfilled");
						bMEPClaimFound = true;
						if (UIUtil.isNotNullAndNotEmpty(strMEPVal)) {
							if (strMEPVal.equalsIgnoreCase(NO) || strMEPVal.equalsIgnoreCase(EXPIRED)) {
								bMEPComplete = true;
								strMEPReturn = NO;
							}
						} else {
							bMEPComplete = true;
							strMEPReturn = NO;
						}
					}
				}
			}
		} 
		
		///get map of part,parentRM and string of clearance 
		if (!bRMClaimFound) 
			strRMReturn = NO;
		if (!bMEPClaimFound)
			strMEPReturn = NO;
		
		//Added code for May22_CW defect for 47362 Starts
		if (!bHasReleasedMEP) {
			strRMReturn = NO;
			strMEPReturn = NO;
		}
		//Added code for May22_CW defect for 47362 End
		if (strRMReturn.equalsIgnoreCase(NO) && strMEPReturn.equalsIgnoreCase(NO))
			strProductFulfilled = NO;			
		if (strRMReturn.equalsIgnoreCase(YES) && strMEPReturn.equalsIgnoreCase(YES))
			strProductFulfilled = NO;		
		if (strRMReturn.equalsIgnoreCase(YES) && strMEPReturn.equalsIgnoreCase(NO))
			strProductFulfilled = YES;
		if (strRMReturn.equalsIgnoreCase(NO) && strMEPReturn.equalsIgnoreCase(YES))
			strProductFulfilled = YES;
				
		mpReturn.put("RMFulfilled", strRMReturn);
		mpReturn.put("MEPFulfilled", strMEPReturn);
		mpReturn.put("ProductPartClaimFulfilled", strProductFulfilled);	
		 
	} catch (Exception e) {
		outLog.print("Exception in  getFulfillmentSummary:" + e + "\n");
		outLog.flush();	
	} 
	return mpReturn;	
}



/**This method is used to get the input part details
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */

private Map getPartDetail(Context context, String strPartId, String strClaim) throws FrameworkException {
	Map<String, Object> mpReturn = new HashMap<>();
	Map<String, Object> mpCertification = new HashMap<>();
	String strCertificationName = DomainConstants.EMPTY_STRING;
	boolean bOnPart = false;
	Short sRecursionLevel = 1;
	try {
		StringList slObjSelect = new StringList();
		slObjSelect.add(DomainConstants.SELECT_NAME);	
		StringList slRelSelect = new StringList();
		slRelSelect.add(RM_CERTIFICATION_NAME);
		slRelSelect.add(CERTIFICATION_REGION);
		slRelSelect.add(CERTIFICATION_AREA);
		slRelSelect.add(CERTIFICATION_GROUP);
		slRelSelect.add(CERTIFICATION_MARKET);
		slRelSelect.add(DomainConstants.SELECT_NAME);
		DomainObject domPartObj = DomainObject.newInstance(context, strPartId);
		MapList mlConnectedCertifications = domPartObj.getRelatedObjects(context, // context
				pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS, // relationshipPattern
				pgV3Constants.TYPE_PGPLIMATERIALCERTIFICATIONS, // typePattern
				slObjSelect, // objectSelects
				slRelSelect, // relationshipSelects
				false, //get To
				true, //get From
				(short)0, //recurse level
				null, // objectWhere
				null, // relationshipWhere
				0);// limit
		if (null != mlConnectedCertifications && !mlConnectedCertifications.isEmpty()) {
			Iterator<Object> itrCertification = mlConnectedCertifications.iterator();
			while (itrCertification.hasNext()) {
				bOnPart=false;
				mpCertification = (Map) itrCertification.next();
				strCertificationName = (String) mpCertification.get(RM_CERTIFICATION_NAME);
				if (strCertificationName.equalsIgnoreCase(strClaim)) {
					bOnPart=true;
					mpReturn = processMarketMap(mpCertification, strClaim);	
					mpReturn.put("CertificationClaimOnPart", strClaim);
					break;
				}
			}
			if (!bOnPart) {
				mpReturn.put("CertificationClaimOnPart", "");
			}
		}
	
	} catch (Exception e) {
		outLog.print("Exception in  getPartDetail:" + e + "\n");
		outLog.flush();
	} 
	return mpReturn;	
}

/**This method is used to process the Part Market data  
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private Map processMarketMap(Map mpCertification, String strClaim) throws FrameworkException {
	Map<String, Object> mpReturn = new HashMap<>();
	String strCertificationName = DomainConstants.EMPTY_STRING;
	String strRegion = DomainConstants.EMPTY_STRING;
	String strArea = DomainConstants.EMPTY_STRING;
	String strMarket = DomainConstants.EMPTY_STRING;
	String strGroup = DomainConstants.EMPTY_STRING;
	StringList slRegion = new StringList();
	StringList slArea = new StringList();
	StringList slMarket = new StringList();
	StringList slGroup = new StringList();	
	try {
		strCertificationName = (String) mpCertification.get(RM_CERTIFICATION_NAME);
		if (strCertificationName.equalsIgnoreCase(strClaim)) {
			if (mpCertification.get(CERTIFICATION_REGION) != null && (mpCertification.get(CERTIFICATION_REGION) instanceof StringList)) {
				slRegion = (StringList) mpCertification.get(CERTIFICATION_REGION);
				strRegion = convertToString(slRegion);		
					
			} else if (mpCertification.get(CERTIFICATION_REGION) != null) {
				strRegion  = (String) mpCertification.get(CERTIFICATION_REGION);
			}
			if (mpCertification.get(CERTIFICATION_AREA) !=null && (mpCertification.get(CERTIFICATION_AREA) instanceof StringList)) {
				slArea = (StringList) mpCertification.get(CERTIFICATION_AREA);
				strArea = convertToString(slArea);		
					
			} else if (mpCertification.get(CERTIFICATION_AREA) != null) {
				strArea  = (String) mpCertification.get(CERTIFICATION_AREA);
			}
			if (mpCertification.get(CERTIFICATION_GROUP) !=null && (mpCertification.get(CERTIFICATION_GROUP) instanceof StringList)) {
				slGroup = (StringList) mpCertification.get(CERTIFICATION_GROUP);
				strGroup = convertToString(slGroup);		
					
			} else if (mpCertification.get(CERTIFICATION_GROUP) != null) {
				strGroup  = (String) mpCertification.get(CERTIFICATION_GROUP);
			}
			if (mpCertification.get(CERTIFICATION_MARKET) !=null && (mpCertification.get(CERTIFICATION_MARKET) instanceof StringList)) {
				slMarket = (StringList) mpCertification.get(CERTIFICATION_MARKET);
				strMarket = convertToString(slMarket);					
			} else if (mpCertification.get(CERTIFICATION_MARKET) != null) {
				strMarket  = (String) mpCertification.get(CERTIFICATION_MARKET);
			}
			mpReturn.put("Certification", strCertificationName);
			mpReturn.put("CertificationRegion", strRegion);
			mpReturn.put("CertificationArea",strArea);
			mpReturn.put("CertificationGroup", strGroup);
			mpReturn.put("CertificationMarket", strMarket);		
		}
		
	} catch (Exception e) {
		outLog.print("Exception in  processMarketMap:" + e + "\n");
		outLog.flush();
	} 
	return mpReturn;	
}



/**This method is used to determine if RM is Primary or Substitute
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */

private boolean isPrimary(String strEBOMSubstitute) throws FrameworkException {
	boolean bChildPartIsPrimary = false;
	try {
		if(UIUtil.isNotNullAndNotEmpty(strEBOMSubstitute)){
			if("TRUE".equalsIgnoreCase(strEBOMSubstitute)) {						
				bChildPartIsPrimary = false;
			} else if("FALSE".equalsIgnoreCase(strEBOMSubstitute)){	
				bChildPartIsPrimary = true;
			}
		} 
	} catch (Exception e) {
		outLog.print("Exception in  isPrimary:" + e + "\n");
		outLog.flush();
	} 
		return bChildPartIsPrimary;	
}



/**This method is used to retreive the BOM data
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */

private MapList expandEBOM(Context context, DomainObject dObjPart) { 	
MapList mlBOMChild = new MapList();		
try {		
	StringList slObjSelect = new StringList();	
	slObjSelect.add(DomainConstants.SELECT_NAME);
	slObjSelect.add(DomainConstants.SELECT_TYPE);
	slObjSelect.add(DomainConstants.SELECT_REVISION);
	slObjSelect.add(DomainConstants.SELECT_ID);
	slObjSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);			
	
	StringList slObjRelSelect = new StringList();		
	slObjRelSelect.add("frommid["+REL_EBOMSUBSTITUTE+"]");			
	slObjRelSelect.add("frommid["+REL_EBOMSUBSTITUTE+"].to.name");
	slObjRelSelect.add("frommid["+REL_EBOMSUBSTITUTE+"].to.id");
				
	mlBOMChild = (MapList)dObjPart.getRelatedObjects(
			context,									
			pgV3Constants.RELATIONSHIP_EBOM,		//rel pattern
			pgV3Constants.TYPE_RAWMATERIALPART, 	//type pattern
			slObjSelect, 							//object select
			slObjRelSelect,							//relationship select
			false,									// to side
			true,									// from side
			(short)0,                               //expansion level
			null,									//post type pattern
			null, 									// post relationship pattern
			0);										//limit
	
	}catch(Exception e) {
		outLog.print("Exception in  expandEBOM: "+e+"\n");
		outLog.flush();
	}
	return mlBOMChild;
}


	
/**This method is used to get the MEP SEP Certification data of a part
 * @param context
 * @param dobjPart
 * @param mpObjectCommon
 * @return
 * @throws FrameworkException 
 */
private MapList getMEPCertification(Context context, String strChildPartId, String strSelectedCertifications) throws FrameworkException {
	MapList mlMEPSEPCertiAll=new MapList();		
	String strWhereExp="";
		StringBuilder sbRelPattern = new StringBuilder();
		sbRelPattern.append(pgV3Constants.RELATIONSHIP_MANUFACTUREREQUIVALENT);
		sbRelPattern.append(pgV3Constants.SYMBOL_COMMA);
		
		StringList slBusSelect=new StringList();
		slBusSelect.addElement(DomainConstants.SELECT_ID);
		slBusSelect.addElement(DomainConstants.SELECT_TYPE);
		slBusSelect.addElement(DomainConstants.SELECT_NAME);
		slBusSelect.addElement(DomainConstants.SELECT_REVISION);
		slBusSelect.addElement(DomainConstants.SELECT_CURRENT);
		
		StringList slRelSelect = new StringList();
		slRelSelect.addElement(CERTIFICATION_NAME);
		slRelSelect.addElement(CERTIFICATION_ID);
		slRelSelect.addElement(CERTIFICATION_STATUS);
		slRelSelect.addElement(CERTIFICATION_EXPDATE);	
		slRelSelect.addElement(CERTIFICATION_FROMPOLICY);	
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47292 - Starts
		if(SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
			 strWhereExp = "(current=="+pgV3Constants.STATE_RELEASE+")";
		}else {
			 strWhereExp = "(current!="+pgV3Constants.STATE_OBSOLETE+")";
		}
		//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47292 - Ends
		
		boolean bToDir = false;
		boolean bFromDir = true;
		try{
		DomainObject dobChildjPart = DomainObject.newInstance(context, strChildPartId);
		MapList mpReturnList = dobChildjPart.getRelatedObjects(
				context, //Context
				sbRelPattern.toString(), //Relationship
				DomainConstants.TYPE_PART, //Type
				slBusSelect, //Object Select
				slRelSelect, //Rel Select
				bToDir, //get To
				bFromDir, //get From
				(short)1, //recurse level
				strWhereExp, //object where clause
				null, //relationship where clause
				0); //limit
		int mpMEPSEPCertiSize = mpReturnList.size();
		if(mpMEPSEPCertiSize>0) {
			//process Child Part.
			mlMEPSEPCertiAll = processConnectedMEP(context, strChildPartId, mpReturnList, strSelectedCertifications);			
		} else {
			mlMEPSEPCertiAll = processNoMEP(context, strSelectedCertifications);
		}
		mlMEPSEPCertiAll.sort("CertificationAnalyzed","ascending","string");
	}	catch (Exception e){					
		outLog.print("Exception in  getMEPCertification: "+e+"\n");
		outLog.flush();	
	}		
	return mlMEPSEPCertiAll;
}

/**This method is used to add entries from RM with no MEP
 * @param context
 * @param strSelectedCertifications
 * @return mlMEPSEPCertiAll
 * @throws FrameworkException 
 */
private MapList processNoMEP(Context context, String strSelectedCertifications) throws FrameworkException  {
MapList mlMEPSEPCertiAll=new MapList();	
Map<String,Object> mpMEPSEPCertiNew = null;
String strClaim = "";
try {
					
		StringList slRequestedClaims = StringUtil.split((String)strSelectedCertifications,"~");
		for (int i = 0;i<slRequestedClaims.size();i++) {
			strClaim = (String)slRequestedClaims.get(i);
			mpMEPSEPCertiNew = new HashMap();
			mpMEPSEPCertiNew.put("MEPId", "");
			mpMEPSEPCertiNew.put("CertificationAnalyzed", strClaim);	
			mpMEPSEPCertiNew.put("MEPClaimStatus", "");
			mpMEPSEPCertiNew.put("MEPClaimExpiration", "");;
			mpMEPSEPCertiNew.put("MEPFulfilled", "No");
			mpMEPSEPCertiNew.put("RMClaimStatus", "");
			mpMEPSEPCertiNew.put("RMClaimExpiration", "");;
			mpMEPSEPCertiNew.put("RMFulfilled", "No");
			mpMEPSEPCertiNew.put("Policy", "");	
			mpMEPSEPCertiNew.put("MEPId", "");
			mpMEPSEPCertiNew.put("MEPName", "");
			mpMEPSEPCertiNew.put("MEPRev", "");
			mpMEPSEPCertiNew.put("MEPState", "");
			mlMEPSEPCertiAll.add(mpMEPSEPCertiNew);
		
	}
}	catch (Exception e){					
	outLog.print("Exception in processNoMEP: "+e+"\n");
	outLog.flush();	
}		
	return mlMEPSEPCertiAll;
}		

 /**This method is used to processConnectedMEP 
	 * @param strRMId
	 * @param mpReturnList
	 * @param strSelectedCertifications
	 * @return mlMEPSEPCertiAll
	 * @throws FrameworkException 
	 */			 
private MapList processConnectedMEP(Context context, String strRMId, MapList mpReturnList, String strSelectedCertifications) throws FrameworkException {
	MapList mlMEPSEPCertiAll=new MapList();		
	Map<String,Object> mpMEPSEPCertiNew = null;
	Map<String,Object> mpDateStatus = new HashMap();
	String strExpirationDate = DomainConstants.EMPTY_STRING;
	String strCertificationName = DomainConstants.EMPTY_STRING;		
	String strExpired = DomainConstants.EMPTY_STRING;	
	String strFulfilled = DomainConstants.EMPTY_STRING;	
	String strMEPId = DomainConstants.EMPTY_STRING;	
	String strCertiStatus = DomainConstants.EMPTY_STRING;
	String strCertiPolicy = "";
	StringBuffer strProcessedClaim = new StringBuffer();
	Map<String,Object> mpMEPSEPCertiRow = null;
	boolean bIsRequestedClaim = false;
	boolean bHasMultipleCertifications = false;
	int slMEPSEPCertiNameSize=0;
	StringList slMEPSEPCertiName = new StringList();
	StringList slMEPSEPCertiStatus = new StringList();
	StringList slMEPSEPCertiDate = new StringList();
	StringList slMEPSEPCertiPolicy = new StringList();
	StringList slRequestedClaims = new StringList();
	int mpReturnListSize = mpReturnList.size();
	try {
		DomainObject dobChildjPart = DomainObject.newInstance(context, strRMId);
		for(int i=0;i<mpReturnListSize;i++) {
			slRequestedClaims = StringUtil.split((String)strSelectedCertifications,"~");
			mpMEPSEPCertiRow = (Map)mpReturnList.get(i);
			strProcessedClaim = strProcessedClaim.delete(0, strProcessedClaim.length());
			strMEPId = (String) mpMEPSEPCertiRow.get(DomainConstants.SELECT_ID);
			if(mpMEPSEPCertiRow.get(CERTIFICATION_NAME) instanceof StringList){
				slMEPSEPCertiName = (StringList) mpMEPSEPCertiRow.get(CERTIFICATION_NAME);				
				slMEPSEPCertiStatus = (StringList)mpMEPSEPCertiRow.get(CERTIFICATION_STATUS);
				slMEPSEPCertiDate = (StringList)mpMEPSEPCertiRow.get(CERTIFICATION_EXPDATE);
				slMEPSEPCertiPolicy = (StringList)mpMEPSEPCertiRow.get(CERTIFICATION_FROMPOLICY);
				slMEPSEPCertiNameSize = slMEPSEPCertiName.size();
				bHasMultipleCertifications = true;
			} else {
				strCertificationName = (String) mpMEPSEPCertiRow.get(CERTIFICATION_NAME);
				strCertiPolicy = (String) mpMEPSEPCertiRow.get(CERTIFICATION_FROMPOLICY);
				strCertiStatus = (String) mpMEPSEPCertiRow.get(CERTIFICATION_STATUS);
				strExpirationDate = (String) mpMEPSEPCertiRow.get(CERTIFICATION_EXPDATE);
				bHasMultipleCertifications = false;
				slMEPSEPCertiNameSize = 1;	 
			}
			
			for(int j=0;j<slMEPSEPCertiNameSize;j++){				
				if (bHasMultipleCertifications) {
					strCertificationName = (String)slMEPSEPCertiName.get(j);
					strExpirationDate = (String) slMEPSEPCertiDate.get(j);
					strCertiStatus = (String) slMEPSEPCertiStatus.get(j);
					strCertiPolicy = (String)slMEPSEPCertiPolicy.get(j);
				}
				
				bIsRequestedClaim = isRequestedClaim(strCertificationName,strSelectedCertifications);
				//Modified to fix defect 46232 April_CW
				if (bIsRequestedClaim && !strProcessedClaim.toString().contains("~"+strCertificationName+"~")) {
					strProcessedClaim = strProcessedClaim.append("~").append(strCertificationName).append("~");		

					mpMEPSEPCertiNew = new HashMap<>();
					slRequestedClaims = removeValue(slRequestedClaims, strCertificationName);
					mpMEPSEPCertiNew.put("MEPId", strMEPId);
					mpMEPSEPCertiNew.put("CertificationAnalyzed", strCertificationName);	
					mpDateStatus = getExpDateStatus(mpMEPSEPCertiRow,strCertificationName);
					if (null != mpDateStatus && !mpDateStatus.isEmpty()) {
						mpMEPSEPCertiNew.put("MEPClaimStatus", mpDateStatus.get("MEPClaimStatus"));
						mpMEPSEPCertiNew.put("MEPClaimExpiration", mpDateStatus.get("MEPClaimExpiration"));
						mpMEPSEPCertiNew.put("MEPFulfilled", mpDateStatus.get("MEPFulfilled"));						  
						mpMEPSEPCertiNew.put("RMClaimStatus", mpDateStatus.get("RMClaimStatus"));
						mpMEPSEPCertiNew.put("RMClaimExpiration", mpDateStatus.get("RMClaimExpiration"));
						mpMEPSEPCertiNew.put("RMFulfilled", mpDateStatus.get("RMFulfilled"));						
					} else {
						mpMEPSEPCertiNew.put("MEPClaimStatus","");
						mpMEPSEPCertiNew.put("MEPClaimExpiration", "");
						mpMEPSEPCertiNew.put("MEPFulfilled", NO);						  
						mpMEPSEPCertiNew.put("RMClaimStatus", "");
						mpMEPSEPCertiNew.put("RMClaimExpiration", "");
						mpMEPSEPCertiNew.put("RMFulfilled", "");
					}
					mpMEPSEPCertiNew.put("MEPId", mpMEPSEPCertiRow.get(DomainConstants.SELECT_ID));
					mpMEPSEPCertiNew.put("MEPName", mpMEPSEPCertiRow.get(DomainConstants.SELECT_NAME));
					//Modified for 2018x.6 APR CW Defect 46693 - starts
					mpMEPSEPCertiNew.put("MEPRev", mpMEPSEPCertiRow.get(DomainConstants.SELECT_REVISION));
					mpMEPSEPCertiNew.put("MEPState", mpMEPSEPCertiRow.get(DomainConstants.SELECT_CURRENT));
					//Modified for 2018x.6 APR CW Defect 46693 - Ends
					mlMEPSEPCertiAll.add(mpMEPSEPCertiNew);
				}
			} 
			if (slRequestedClaims.size() > 0) {
				for (int k=0;k<slRequestedClaims.size();k++) {
					mpMEPSEPCertiNew = new HashMap<>();
					strCertificationName = (String) slRequestedClaims.get(k);
					mpMEPSEPCertiNew.put("MEPId", strMEPId);
					mpMEPSEPCertiNew.put("CertificationAnalyzed", strCertificationName);				
					mpMEPSEPCertiNew.put("MEPClaimStatus", "");
					mpMEPSEPCertiNew.put("MEPClaimExpiration", "");
					mpMEPSEPCertiNew.put("MEPFulfilled", "No");
					mpMEPSEPCertiNew.put("RMClaimStatus", "");
					mpMEPSEPCertiNew.put("RMClaimExpiration", "");
					mpMEPSEPCertiNew.put("RMFulfilled", "No");
					mpMEPSEPCertiNew.put("Policy", "");	
					mpMEPSEPCertiNew.put("MEPName", mpMEPSEPCertiRow.get(DomainConstants.SELECT_NAME));
					//Modified for 2018x.6 APR CW Defect 46693 - starts
					mpMEPSEPCertiNew.put("MEPRev", mpMEPSEPCertiRow.get(DomainConstants.SELECT_REVISION));
					mpMEPSEPCertiNew.put("MEPState", mpMEPSEPCertiRow.get(DomainConstants.SELECT_CURRENT));
					//Modified for 2018x.6 APR CW Defect 46693 - Ends
					mlMEPSEPCertiAll.add(mpMEPSEPCertiNew);
				}
			}
			
		}		
		mlMEPSEPCertiAll.sort("CertificationAnalyzed","ascending","string");
	} catch (Exception e) {
		outLog.print("Exception in  processConnectedMEP: "+e+"\n");
		outLog.flush();	
	} 	
	return mlMEPSEPCertiAll;	
}	 


/**This method is used get Date and Status 
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private Map getExpDateStatus(Map mpMEPSEPCertiRow,String strCertification) throws FrameworkException {
Map<String, Object> mpReturn = new HashMap<>();
String strExpirationDate = "";
String strCertificationName = "";		
String strFulfilled = "";	
String strCertiStatus = "";
String strCertiPolicy = "";
boolean bHasMultipleCertifications = false;
boolean bOnMEP = false;
boolean bOnRM = false;
StringList slMEPSEPCertiName = new StringList();
StringList slMEPSEPCertiStatus = new StringList();
StringList slMEPSEPCertiDate = new StringList();
StringList slMEPSEPCertiPolicy = new StringList();
int slMEPSEPCertiNameSize=0;
try {
	if(!mpMEPSEPCertiRow.isEmpty() && !UIUtil.isNotNullAndNotEmpty(strCertificationName)){

		if(mpMEPSEPCertiRow.get(CERTIFICATION_NAME) instanceof StringList){
			slMEPSEPCertiName = (StringList) mpMEPSEPCertiRow.get(CERTIFICATION_NAME);				
			slMEPSEPCertiStatus = (StringList)mpMEPSEPCertiRow.get(CERTIFICATION_STATUS);
			slMEPSEPCertiDate = (StringList)mpMEPSEPCertiRow.get(CERTIFICATION_EXPDATE);
			slMEPSEPCertiPolicy = (StringList)mpMEPSEPCertiRow.get(CERTIFICATION_FROMPOLICY);
			slMEPSEPCertiNameSize = slMEPSEPCertiName.size();
			bHasMultipleCertifications = true;
		} else {
			strCertificationName = (String) mpMEPSEPCertiRow.get(CERTIFICATION_NAME);
			strCertiPolicy = (String) mpMEPSEPCertiRow.get(CERTIFICATION_FROMPOLICY);
			strCertiStatus = (String) mpMEPSEPCertiRow.get(CERTIFICATION_STATUS);
			strExpirationDate = (String) mpMEPSEPCertiRow.get(CERTIFICATION_EXPDATE);
			bHasMultipleCertifications = false;
			slMEPSEPCertiNameSize = 1;	 
		}
		
		for(int j=0;j<slMEPSEPCertiNameSize;j++){				
			if (bHasMultipleCertifications) {
				strCertificationName = (String)slMEPSEPCertiName.get(j);
				strExpirationDate = (String) slMEPSEPCertiDate.get(j);
				strCertiStatus = (String) slMEPSEPCertiStatus.get(j);
				strCertiPolicy = (String)slMEPSEPCertiPolicy.get(j);				
			}
			//May_CW Defect 47211 Start
			strFulfilled = getFulfilled(strExpirationDate,strCertiStatus);
			//May_CW Defect 47211 End
			if (strCertificationName.equalsIgnoreCase(strCertification) && strCertiPolicy.equals(MANUFACTUREREQUIVALENT)) {
				mpReturn.put("MEPClaimStatus", strCertiStatus);
				mpReturn.put("MEPClaimExpiration", strExpirationDate);
				mpReturn.put("MEPFulfilled", strFulfilled);		
				bOnMEP = true;			
				
			}			
			if (strCertificationName.equalsIgnoreCase(strCertification) && strCertiPolicy.equals(ECPART)) {
				mpReturn.put("RMClaimStatus", strCertiStatus);
				mpReturn.put("RMClaimExpiration", strExpirationDate);
				mpReturn.put("RMFulfilled", strFulfilled);			
				bOnRM = true;
			}
		}
		if (!bOnMEP) {
			mpReturn.put("MEPClaimStatus", "");
			mpReturn.put("MEPClaimExpiration", "");
			mpReturn.put("MEPFulfilled", NO);	
		}
		if (!bOnRM) {
			mpReturn.put("RMClaimStatus", "");
			mpReturn.put("RMClaimExpiration", "");
			mpReturn.put("RMFulfilled", "");	
		}
	}	 
 }  catch (Exception e) {
		outLog.print("Exception in  getExpired: "+e+"\n");
		outLog.flush();	
	} 
return mpReturn;	
}


/**This method is used get Expired Status 
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private String getExpired (String strMEPExpirationDate) throws FrameworkException {
String strExpired = DomainConstants.EMPTY_STRING;
try {
	if (isExpired(strMEPExpirationDate)) {
		strExpired = EXPIRED;
	}	 
 }  catch (Exception e) {
		outLog.print("Exception in  getExpired: "+e+"\n");
		outLog.flush();	
	} 
return strExpired;	
}
 
/**This method is used get Fulfilled status
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private String getFulfilled (String strMEPExpirationDate, String strMEPCertiStatus) throws FrameworkException {
 String strFulfilled = NO;
 try {
	 if (!UIUtil.isNotNullAndNotEmpty(strMEPCertiStatus)) {
		strFulfilled = NO;
	} else {	 
		if (!isExpired(strMEPExpirationDate)) {
			if(UIUtil.isNotNullAndNotEmpty(strMEPCertiStatus)) {
				if (YES.equalsIgnoreCase(strMEPCertiStatus)) {						
					strFulfilled = YES;
				} 
				if (NO.equalsIgnoreCase(strMEPCertiStatus)) {							
					strFulfilled = NO;
				} 
			}
		} else {
			strFulfilled = EXPIRED;
		}
	}
 } catch (Exception e) {
	outLog.print("Exception in  getFulfilled: "+e+"\n");
	outLog.flush();	
} 
return strFulfilled;	
}


/**This method is used to determine if Claim Date has expired
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */

private boolean isExpired(String strExpirationDate) throws FrameworkException {
	boolean isExpired = false; 	
	try {
		if (UIUtil.isNotNullAndNotEmpty(strExpirationDate)) {			
	        Date now = new Date();
	        String response="";
	        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
	        String strCurrDate = sdfDate.format(now);
	        Date currentDate =  new SimpleDateFormat("MM/dd/yyyy").parse(strCurrDate);
	        Date expDate =  new SimpleDateFormat("MM/dd/yyyy").parse(strExpirationDate);
	        if (expDate!=null && (currentDate.equals(expDate) || currentDate.after(expDate))){
		         isExpired=true;
		         
	         }
		} else {
			isExpired=false;				
		}		
}  catch (Exception e) {
	outLog.print("Exception in  isExpired: "+e+"\n");
	outLog.flush();	
} 
	return isExpired;	
}

/**This method is used to check if claim is requested
 * @param StringList
 * @param String
 * @return StringList
 * @throws FrameworkException 
 */

private StringList removeValue(StringList slRequestedClaims, String strClaim) {
String strMEPClaim = "";
	for (int i = 0;i<slRequestedClaims.size();i++) {
		strMEPClaim = (String)slRequestedClaims.get(i);
		if (strClaim.equalsIgnoreCase(strMEPClaim)) {
			slRequestedClaims.remove(i);		
		}
	}
	return slRequestedClaims;
}

/**This method is used to cehck if claim is requested
 * @param String
 * @param String
 * @param 
 * @return boolean
 * @throws FrameworkException 
 */
//ALM Defect 46401 46232
private boolean isRequestedClaim(String strClaim, String strRequestedClaims) throws FrameworkException { 
	boolean bIsRequestedClaim = false; 
	if(UIUtil.isNotNullAndNotEmpty(strRequestedClaims) && UIUtil.isNotNullAndNotEmpty(strClaim))
	{	
		if (strRequestedClaims.indexOf("~")>-1) {				
			StringList slRequestedClaims = StringUtil.split((String)strRequestedClaims,"~");
			for (int i = 0;i<slRequestedClaims.size();i++) {
				String strMEPClaim = (String)slRequestedClaims.get(i);
				if (strClaim.equalsIgnoreCase(strMEPClaim)) {
					bIsRequestedClaim = true;					
					return bIsRequestedClaim;
				}
			}
			
		} else {
			if (strClaim.equalsIgnoreCase(strRequestedClaims)) {				
				bIsRequestedClaim = true;
			}
		}	
	}
	return bIsRequestedClaim;
}
	
/**This method is used to check if input part is valid
 * @param context
 * @param StringList
 * @param String
 * @return MapList
 * @throws FrameworkException 
 */

private MapList getValidPart(Context context, StringList slIndividualPartNames, String strUserName) throws FrameworkException{
	MapList mlValidPart = new MapList();		
	MapList mlPart = new MapList();	
	Map<String, Object> mpLogRow = new HashMap<>();
	String strPartName = DomainConstants.EMPTY_STRING;
	String strPartRev = DomainConstants.EMPTY_STRING;
	String strPartNameRev = DomainConstants.EMPTY_STRING;
	String strType = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CertificationReport.Types");
	StringList slSelect = new StringList();
	slSelect.add(DomainConstants.SELECT_ID);
	slSelect.add(DomainConstants.SELECT_NAME);
	slSelect.add(DomainConstants.SELECT_REVISION);
	boolean isContextPushed = false; 
	try {
		for (Object PartName : slIndividualPartNames) {
			mpLogRow = new HashMap<>();
			strPartNameRev = String.valueOf(PartName).trim();
			if(strPartNameRev.contains(".")){
				strPartName = strPartNameRev.substring(0,strPartNameRev.indexOf("."));
				strPartRev = strPartNameRev.substring(strPartNameRev.indexOf(".")+1,strPartNameRev.length());
			} else {
				outLog.print("Revision is missing: " + strPartNameRev + "\n");
				outLog.flush();
			}
			
			if(UIUtil.isNotNullAndNotEmpty(strPartName) && UIUtil.isNotNullAndNotEmpty(strPartRev)){
				isContextPushed = false; 
				//we need to push the context of User Agent to fetch the objects
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true; 
				//User inputs multiple parts and we need to find objects individually
				mlPart = DomainObject.findObjects(context, //context
							strType, //type
							strPartName,//name
							strPartRev,//revision
							DomainConstants.QUERY_WILDCARD,//owner
							pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
							null, //where clause
							false,//expand type
							slSelect);//object select
				if(isContextPushed) {
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
				if(!mlPart.isEmpty()){
					Map<String, String> mPart = (Map<String, String>)mlPart.get(0);			
					String strObjId = mPart.get(DomainConstants.SELECT_ID);		
					boolean bAccess = accessCheck(context,strUserName,strObjId);
					if(bAccess){
						mlValidPart.add(mPart);
					} else {
						mpLogRow.put("PartName", strPartNameRev);
						mpLogRow.put("Msg", ACCESS_ERR);
						outLog.print(ACCESS_ERR + ":" + strPartNameRev + "\n");
						outLog.flush();
					} 
				} else {
					mpLogRow.put("PartName", strPartNameRev);
					mpLogRow.put("Msg", TYPE_ERR);
					outLog.print(TYPE_ERR + ":" + strPartNameRev + "\n");
					outLog.flush();
				}
			} else {
				mpLogRow.put("PartName", strPartNameRev);
				mpLogRow.put("Msg", TYPE_ERR);
				outLog.print(NAME_ERR + ":"  + strPartNameRev + "\n");
				outLog.flush();
			}
			mlLogDetails.add(mpLogRow);
		}
			
	} catch(Exception e) {
		outLog.print("Exception in  getValidPart : "+e+"\n");
		outLog.flush();
	} finally {
		if (isContextPushed)
			ContextUtil.popContext(context);
	}
	return mlValidPart;
}

/**This method is used to concert StringLit to string
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */
private String convertToString(StringList slList) throws FrameworkException {
	String strReturn = DomainConstants.EMPTY_STRING;
	String strTemp= DomainConstants.EMPTY_STRING;
	try {
		for(int j=0;j<slList.size();j++){		
			StringBuilder sbReturn = new StringBuilder();
			strTemp = (String)slList.get(j);
			sbReturn.append(strReturn);
			sbReturn.append(COMMA);
			sbReturn.append(strTemp);
			strReturn = sbReturn.toString();
		}
		//remove the first comman
		strReturn=strReturn.substring(1, strReturn.length());
	}catch (Exception e) {
		outLog.print("Exception in  processMarketMap:" + e + "\n");
		outLog.flush();
	} 
	return strReturn;	
}

/**This method is used to check if user has access to the Part
 * @param 
 * @param 
 * @param 
 * @return 
 * @throws FrameworkException 
 */

	private boolean accessCheck(Context context, String strUserName, String strPartId) throws FrameworkException {
		boolean isContextPushed = false;
		boolean bAccess = false;
		String strValue = DomainConstants.EMPTY_STRING;
		boolean bIsEBPUser = isEBP(context, strUserName);
		if (bIsEBPUser) {
			bAccess = accessCheckEBP(context, strUserName, strPartId);
		} else {
			try {
				DomainObject domainObject = DomainObject.newInstance(context);
				domainObject.setId(strPartId);
				//we need to push the context of requestor to check access for the objects
				ContextUtil.pushContext(context, strUserName, null, context.getVault().getName());
				isContextPushed = true;
				strValue = domainObject.getInfo(context, DomainConstants.SELECT_CURRENT);
				if (UIUtil.isNotNullAndNotEmpty(strValue) && !(DENIED).equals(strValue)) {
					bAccess = true;
				} else {
					bAccess = false;
				}

			} catch (Exception e) {
				outLog.print("Exception in  accessCheck: " + strUserName + ": " + e + "\n");
				outLog.flush();
			} finally {
				if (isContextPushed)
					ContextUtil.popContext(context);
			}
		}
		return bAccess;
	}

	/**This method is used to check user security type
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
	private boolean isEBP(Context context, String strUserName) throws FrameworkException {
		boolean bIsEBP = false;
		Map<String, String> mpPersonData = new HashMap<>();
		try {
			String pgSecurityEmployee = PropertyUtil.getSchemaProperty(context, "attribute_pgSecurityEmployeeType");
			String strUserSecurityType = DomainConstants.EMPTY_STRING;
			StringList slSelect = new StringList(3);
			slSelect.add(DomainConstants.SELECT_CURRENT);
			slSelect.add(DomainConstants.SELECT_ID);
			slSelect.add("attribute[" + pgSecurityEmployee + "]");

			MapList mlPerson = DomainObject.findObjects(context, // context
					DomainConstants.TYPE_PERSON, // type
					strUserName, // name
					pgV3Constants.SYMBOL_HYPHEN, // revision
					DomainConstants.QUERY_WILDCARD, // owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault
					DomainConstants.EMPTY_STRING, // whereExpression
					false, // expandType
					slSelect);// objectSelects

			int maplistSize = mlPerson.size();
			for (int i = 0; i < maplistSize; i++) {
				mpPersonData = (Map) mlPerson.get(i);
				strUserSecurityType = mpPersonData.get("attribute[" + pgSecurityEmployee + "]");
			}
			if (strUserSecurityType.equalsIgnoreCase(EBP)) {
				bIsEBP = true;
			}
		} catch (Exception e) {
			outLog.print("Exception in  isEBP: " + strUserName + ": " + e + "\n");
			outLog.flush();
		}
		return bIsEBP;
	}
	
	/**This method is used to check if EBP user has access to the Part
	 * @param 
	 * @param 
	 * @param 
	 * @return 
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
		boolean isContextPushed = false;
		Map<String, String> mpPersonData = new HashMap<>();
		Map<String, String> mpUserVendor = new HashMap<>();
		String strUserVendor = DomainConstants.EMPTY_STRING;
		try {
			
			//we need to push the context of User Agent to fetch the objects
			ContextUtil.pushContext(context, // context
					PropertyUtil.getSchemaProperty(context, pgV3Constants.PERSON_USER_AGENT), // user name
					DomainConstants.EMPTY_STRING, // password
					DomainConstants.EMPTY_STRING);// vault

			isContextPushed = true;
			MapList mlPerson = DomainObject.findObjects(context, // context
					DomainConstants.TYPE_PERSON, // type
					strUserName, // name
					pgV3Constants.SYMBOL_HYPHEN, // revision
					DomainConstants.QUERY_WILDCARD, // owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault
					null, // whereExpression
					DomainConstants.EMPTY_STRING, // queryName
					true, // expandType
					slSelect, // objectSelects
					sQueryLimit);// objectLimit

			int maplistSize = mlPerson.size();
			for (int i = 0; i < maplistSize; i++) {
				mpPersonData = (Map) mlPerson.get(i);
				strUserOid = mpPersonData.get(DomainConstants.SELECT_ID);
			}
			DomainObject domPersonObj = DomainObject.newInstance(context, strUserOid);
			MapList mlRelatedUserVendors = domPersonObj.getRelatedObjects(context, // context
					pgV3Constants.RELATIONSHIP_MEMBER, // relationshipPattern
					pgV3Constants.TYPE_PLANT + COMMA + pgV3Constants.TYPE_COMPANY, // typePattern
					slObjSelect, // objectSelects
					slRelSelect, // relationshipSelects
					true, // getTo
					false, // getFrom
					sRecursionLevel, // recurseToLevel
					null, // objectWhere
					null, // relationshipWhere
					0);// limit

			if (null != mlRelatedUserVendors && !mlRelatedUserVendors.isEmpty()) {
				Iterator<Object> itrVendor = mlRelatedUserVendors.iterator();
				while (itrVendor.hasNext()) {
					mpUserVendor = (Map) itrVendor.next();
					strUserVendor = mpUserVendor.get(DomainConstants.SELECT_NAME);
					bHasaccess = DomainAccess.hasObjectOwnership(context, strPartId, strUserVendor, STR_PARTNERS_PG,
							STR_MULTI_OWNERSHIP_FOR_OBJ);
					bHasaccessWithOutComment = DomainAccess.hasObjectOwnership(context, strPartId, strUserVendor,
							STR_PARTNERS_PG, DomainConstants.EMPTY_STRING);
					bHasaccessForCoProject = DomainAccess.hasObjectOwnership(context, strPartId,
							strUserVendor + " Plant", STR_PARTNERS_PG, STR_MULTI_OWNERSHIP_FOR_OBJ);
					bHasaccessForCoProjectWithoutComment = DomainAccess.hasObjectOwnership(context, strPartId,
							strUserVendor + " Plant", STR_PARTNERS_PG, DomainConstants.EMPTY_STRING);
					bHasaccessUserProject = DomainAccess.hasObjectOwnership(context, strPartId, null,
							strUserName + "_PRJ", STR_MULTI_OWNERSHIP_FOR_OBJ);
					bHasaccessUserProjectWithoutComment = DomainAccess.hasObjectOwnership(context, strPartId, null,
							strUserName + "_PRJ", DomainConstants.EMPTY_STRING);

					if (bHasaccess || bHasaccessWithOutComment || bHasaccessForCoProject
							|| bHasaccessForCoProjectWithoutComment || bHasaccessUserProject
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
				//we need to push the context of requestor to check access for the objects
				ContextUtil.pushContext(context, strUserName, null, context.getVault().getName());
				isContextPushed = true;
				strValue = domainObject.getInfo(context, DomainConstants.SELECT_CURRENT);
				if (UIUtil.isNotNullAndNotEmpty(strValue) && !(DENIED).equals(strValue)) {
					bAccessToPart = true;
				} else {
					bAccessToPart = false;
				}

			}
		} catch (Exception e) {
			bAccessToPart = false;
			outLog.print("Exception in  accessCheckEBP: " + strUserName + ": " + e + "\n");
			outLog.flush();
		} finally {
			if (isContextPushed)
				ContextUtil.popContext(context);
		}

		return bAccessToPart;
	}

	/**This method is used to update the Excel workbook
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
	
	private void updateExcelWorkbook(Context context, MapList mlCertificationReportDetailsAll, XSSFWorkbook workbook, XSSFSheet strSheetName, XSSFSheet strLogSheet, Map<String,Object> mPassValue) throws IOException {
		FileOutputStream outputStream=null;
		   File sbCertificationReportFolder = null;
		   //cannot implement try with resource as the file path is built dynamically in the code.
		   try {
			   	String strReportPath = DomainConstants.EMPTY_STRING;
				String strReportName = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CertificationReport.ReportName");
				strReportPath = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.CertificationReport.Worksheet.FilePath");
				String strReportExtension = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CertificationReport.ReportExtension");
				StringBuffer sbReportName = new StringBuffer();	
				String strUserName = (String) mPassValue.get("UserName");
				String strReportFileName = (String) mPassValue.get("ReportFileName");
				String strReportObjectId = (String) mPassValue.get("ReportId");
			 	
				updateWorksheetCertificationReport(context,workbook,mlCertificationReportDetailsAll,strSheetName,strLogSheet, mPassValue);
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
				sbCertificationReportFolder = new File(strReportPath);
				if (!sbCertificationReportFolder.exists())  {
					sbCertificationReportFolder.mkdirs();
				}
			
				outputStream = new FileOutputStream(sbCertificationReportFolder.toString()+ File.separator + strReportName);
				workbook.write(outputStream);
				// code to create the object and checking the .excel file in that object
				createCertificationReportObject(context,strUserName,sbCertificationReportFolder.toString(),strReportName,strReportObjectId);
						
				//to delete the .xls file once its checked in to the newly created object
				mlCertificationReportDetailsAll.clear();
				
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

	/**This method is used to update the Excel worksheet
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
private void updateWorksheetCertificationReport(Context context,XSSFWorkbook workbook,MapList mlCertificationReportDetailsAll, XSSFSheet wksheet, XSSFSheet wkSheetLog,Map<String,Object> mPassValue) {
		try {
			//Added code for 2018x.6 Requirement id 36703 Ability to generate CertificationReport with-without hyperlink Starts
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CertificationReport.HyperlinkLimit");
			iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll=1;
			String strHyperlink = (String)mPassValue.get(HYPERLINKASINPUT);
			String strCellValue = "";
			String strPartId = "";
			String strMEPId = "";
			String strRMId = "";
			String strHyperlinkId = "";
			String strValue = "";
			String strPreviousParentRM = "";
			String strCurrentParentRM = "";
			String strGroup = "";
			String srtCurrent = "";
			String strAddRow = "";
			int iGroup = 0;
			
			Map<String,Object> mpReportRow;
			Map<String,Object> mpLogRow;
			HashMap<Integer, String> hm;		
			
			String[] strSplittedValue;
			int columnCount1 = 0;			
			int mlCertificationReportDetailsAllSize=mlCertificationReportDetailsAll.size();
			
			for (int i = 0; i<mlCertificationReportDetailsAllSize; i++) {
				
				XSSFRow row = wksheet.createRow(iRowCountAll);
				iRowCountAll++;
				columnCount1 = 0;
				mpReportRow = (Map) mlCertificationReportDetailsAll.get(i);
				if (mpReportRow.size() > 0) {
				strPartId = (String) mpReportRow.get("PartId");
				strMEPId = (String) mpReportRow.get("MEPId");
				strRMId = (String) mpReportRow.get("RMId");
				strCurrentParentRM = (String) mpReportRow.get("ParentRM");
				strAddRow = (String) mpReportRow.get("AddRow");
				if (UIUtil.isNotNullAndNotEmpty(strCurrentParentRM) && (!strCurrentParentRM.equals(strPreviousParentRM))) {
					iGroup++;					
				}
				strPreviousParentRM = strCurrentParentRM;
				if (iGroup > 0) { 
					strGroup = String.valueOf(iGroup);
				}
				//Added code for May22_CW defect for 47362 Start
				if (BLANKROW.equalsIgnoreCase(strAddRow)) {
					strGroup = "";
				}
				//Added code for May22_CW defect for 47362 End
				hm = new HashMap<>();				
				hm.put(0, HYPERLINK+(String) mpReportRow.get("PartName")+HYPERLINK_PIPE+strPartId);
				hm.put(1, (String) mpReportRow.get("PartRev"));
				hm.put(2, (String) mpReportRow.get("CertificationClaimOnPart"));
				hm.put(3, (String) mpReportRow.get("CertificationAnalyzed"));
				hm.put(4, (String) mpReportRow.get("ProductPartClaimFulfilled"));
				hm.put(5, (String) mpReportRow.get("RMFulfilled"));
				hm.put(6, (String) mpReportRow.get("MEPFulfilled"));
				hm.put(7, strGroup);
				hm.put(8, HYPERLINK+(String) mpReportRow.get("MaterialName")+HYPERLINK_PIPE+strRMId);
				hm.put(9, (String) mpReportRow.get("MaterialRev"));
				hm.put(10, (String) mpReportRow.get("MaterialTitle"));				
				hm.put(11, HYPERLINK+(String) mpReportRow.get("MEPName")+HYPERLINK_PIPE+strMEPId);
				hm.put(12, (String) mpReportRow.get("MEPRev"));				
				hm.put(13, (String) mpReportRow.get("Substitute"));
				if((mpReportRow.get("MEPState")!=null)){
					srtCurrent = getMappedState((String)mpReportRow.get("MEPState"));
					if(UIUtil.isNotNullAndNotEmpty(srtCurrent)){
						hm.put(14, srtCurrent);
					} else {
						hm.put(14, (String) mpReportRow.get("MEPState"));
					}
				} else {
					hm.put(14,"");
				}
				hm.put(15, (String) mpReportRow.get("MEPClaimStatus"));
				hm.put(16, (String) mpReportRow.get("MEPClaimExpiration"));
				hm.put(17, (String) mpReportRow.get("RMClaimStatus"));
				hm.put(18, (String) mpReportRow.get("RMClaimExpiration"));
				hm.put(19, (String) mpReportRow.get("CertificationMarket"));
				hm.put(20, (String) mpReportRow.get("CertificationRegion"));
				hm.put(21, (String) mpReportRow.get("CertificationArea"));
				hm.put(22, (String) mpReportRow.get("CertificationGroup"));
				
				for (int j = 0; j <hm.size();j++) {
					Cell cell = row.createCell(columnCount1);	
					columnCount1++;	
						strCellValue = (String) hm.get(j);
						if (UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)) {
						strSplittedValue = strCellValue.split("\\|",-1);
						strHyperlinkId = strSplittedValue[(strSplittedValue.length) - 1];
						strValue = strSplittedValue[(strSplittedValue.length) - 2];
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){					
							getHyperlink(context, cell, workbook, strValue, strHyperlinkId);
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)) {					
							cell.setCellValue(strValue);
						}
					} 
					else 
					{
						cell.setCellValue(strCellValue);
					}
				}				
			}
			}
			//Update Log Sheet
			int iLogRow = 0;
			int iLogColumn = 0;
			int iLogSize = mlLogDetails.size();
			for (int i = 0; i<iLogSize; i++) {
				
				XSSFRow row = wkSheetLog.createRow(iLogRow);
				iLogRow=iLogRow+1;
				iLogColumn = 0;
				mpLogRow = (Map) mlLogDetails.get(i);
				hm = new HashMap<>();				
				hm.put(0, (String) mpLogRow.get("PartName"));
				hm.put(1, (String) mpLogRow.get("Msg"));
				for (int j = 0; j <hm.size();j++) {
					Cell cell = row.createCell(iLogColumn);	
					iLogColumn=iLogColumn+1;
					strCellValue = (String) hm.get(j);
					 if(UIUtil.isNotNullAndNotEmpty(strCellValue)) {					
						 cell.setCellValue(strCellValue);
					}
				} 					
			}
			
		} catch (Exception e) {
			
			outLog.print("Exception in  updateWorksheetRelatedCertificationReport: "+e+"\n");
			outLog.flush();	
		}
	}
	/**Added by IRM(Sogeti) 2022x.03 August 2023 CW for Requirement 41567
	 * 
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
	
	private void getHyperlink(Context context, Cell cell, XSSFWorkbook workbook,
			String strValue, String strId) throws Exception {
			String strURL = "";
			String strNewURL = "";
			CreationHelper createHelper = workbook.getCreationHelper();
			XSSFCellStyle style = workbook.createCellStyle();
			Font hlinkfont = workbook.createFont();
			hlinkfont.setUnderline(Font.U_SINGLE);
			hlinkfont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
			style.setFont(hlinkfont);
			XSSFHyperlink link = (XSSFHyperlink) createHelper.createHyperlink(HyperlinkType.URL);
			if (UIUtil.isNotNullAndNotEmpty(strId) && UIUtil.isNotNullAndNotEmpty(strValue)) {
				if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && ENOVIA.equalsIgnoreCase(strOriginatingSource)) {
					strURL = EnoviaResourceBundle.getProperty(context,EMXCPN, context.getLocale(), "emxCPN.BaseURL");
					strNewURL = String.valueOf(strURL).trim();
					link.setAddress(strNewURL + "?objectId=" + strId);
				} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
					strURL = getSpecReaderURL(context);
					strNewURL = String.valueOf(strURL).trim();
					link.setAddress(strNewURL + strValue);
				}
				cell.setCellValue(strValue);
				cell.setHyperlink(link);
				cell.setCellStyle(style);
			} else {
				cell.setCellValue(DomainConstants.EMPTY_STRING);
			}
		}
	
	/**This method is used to get columns names from the property file
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
	private void getColumnNames(Context context,XSSFRow rowHeaderPart,XSSFCellStyle cellStyleCommonCol) {
		try {
			String strColumnNames = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.CertificationReport.Worksheet.ColumnTypes");
			StringList slIndividualColumnNames = StringUtil.split(strColumnNames, COMMA);
			int slIndividualColumnNamesSize=slIndividualColumnNames.size();
			
			for (int i = 0;i<slIndividualColumnNamesSize;i++) {
				String columnName = slIndividualColumnNames.get(i);
				String strColumnName = String.valueOf(columnName).trim();
				String strColumnValue = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.CertificationReport.Worksheet.Column."+strColumnName);
				Cell cell = rowHeaderPart.createCell(i);
				cell.setCellStyle(cellStyleCommonCol);
				cell.setCellValue(strColumnValue);	
			}
		}catch(Exception e) {
			outLog.print("Exception in  getColumnNames: "+e+"\n");
			outLog.flush();
		}
	}
	
	/**This method is used to check in the report 
	 * @param 
	 * @param 
	 * @param 
	 * @return 
	 * @throws FrameworkException 
	 */
	private void createCertificationReportObject(Context context,String strUserName,String strReportPath,String strReportName,String strReportObjectId) throws MatrixException {
		boolean isContextPushed = false;
		try {
			String typePgDSMReport = PropertyUtil.getSchemaProperty(context,"type_pgDSMReport");
			String policyPgDSMReport = PropertyUtil.getSchemaProperty(context,"policy_pgDSMReport");
			String  strFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
			String strContextUserName = context.getUser();
			String strObjectName= EnoviaResourceBundle.getProperty(context, CPNSTRINGRESOURCE, context.getLocale(),"emxCPN.CertificationReport.ObjectName");
			String strNewObjectName = strObjectName+"_"+strContextUserName+"_"+System.currentTimeMillis();
			String strObjectId = strReportObjectId;
			Date currentDate = new Date();
			SimpleDateFormat smpdateformat = new SimpleDateFormat("MM-dd-yyyy");
			
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){

				//need to push the User Agent context to pull the data
				ContextUtil.pushContext(context,pgV3Constants.PERSON_USER_AGENT,DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
				DomainObject doObj = DomainObject.newInstance(context, strObjectId);
				outLog.print("Before Checkin Report: "+strUserName+"| "+strReportName+"\n");
				outLog.flush();		
				doObj.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, strFormat, strReportName, strReportPath);
				doObj.promote(context);
				String sFullPath = strReportPath.concat("/").concat(strReportName);	
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
			outLog.print("Exception in  createCertificationReportObject and Checkin File: "+e+"\n");
			outLog.flush();	
		} finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
	}

	/**
	* This method is used to get Back-End values of Current for MEP 
	* @param String
	* @return String
	*/
	public String getMappedState(String current) {
		String mpCurrent = DomainConstants.EMPTY_STRING;
		if("Preliminary".equalsIgnoreCase(current)){
			mpCurrent = STATE_IN_WORK;
		}
		if("Approved".equalsIgnoreCase(current)){
			mpCurrent = STATE_APPROVED;
		}
		if("Review".equalsIgnoreCase(current)){
			mpCurrent = STATE_FROZEN;
		}
		if("Pending Obsolete".equalsIgnoreCase(current)){
			mpCurrent = STATE_PENDINGOBSOLETE;
		}
		if("Obsolete".equalsIgnoreCase(current)){
			mpCurrent = STATE_OBSOLETE;
		}
		if("Release".equalsIgnoreCase(current)){
			mpCurrent = STATE_RELEASE;
		}		
		return mpCurrent;
	}
	
	/**This method is used to create Excel Worksheets
	 * @param context
	 * @param workbook
	 * @param strWorksheetName
	 * @return newSheet
	 * @throws FrameworkException 
	 */
	private XSSFSheet createSheet(Context context, XSSFWorkbook workbook, String strWorksheetName) {
		String strSheetName = "";
		XSSFSheet newSheet = null;
		if(UIUtil.isNotNullAndNotEmpty(strWorksheetName)){	
			strSheetName = strWorksheetName;
		}	else {
			strSheetName = SHEETNAME;
		}
		XSSFCellStyle cellStylePart = workbook.createCellStyle();
		XSSFFont xsfontPart = workbook.createFont();
		xsfontPart.setBold(true);
		xsfontPart.setFontHeightInPoints((short) 10);
		cellStylePart.setFont(xsfontPart);
		cellStylePart.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());		
		cellStylePart.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		if(workbook.getSheet(strSheetName) == null){
			String sheetName = WorkbookUtil.createSafeSheetName(strSheetName);	
			newSheet = workbook.createSheet(sheetName);
			XSSFRow rowHeaderPart = newSheet.createRow(0);					
			getColumnNames(context,rowHeaderPart,cellStylePart);		
		} 
		return newSheet;
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