/* pgIPMPDFViewUtil_mxJPO
 ** This JPO contains necessary methods for PDF generation
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.openejb.loader.Files;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
//DSM (DS) 2018x.0 ALM 24281 UAT issue: Failure View : CA is not present to Render GenDOc -STARTS
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
//DSM (DS) 2018x.0 ALM 24281 UAT issue: Failure View : CA is not present to Render GenDOc -ENDS
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.cpn.CPNCommon;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.cpn.ProductData;
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.cpn.util.CPNUIUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.StringUtil;
//import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.mxFtp;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.util.EncryptCrypto;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v3.custom.pgV3Util;
import com.png.apollo.pgApolloConstants;

import matrix.db.AccessConstants;
import matrix.db.Attribute;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectItr;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import matrix.util.StringList;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

// Cloud-GenDoc - starts
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.pg.dsm.gendoc.GenDocClient;
import com.pg.dsm.gendoc.cloud.GenDoc;
import com.pg.dsm.gendoc.cloud.GenDocWorkDir;
import com.pg.dsm.gendoc.cloud.GenDocUpload;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.itext.services.PDFConvert;
import com.pg.dsm.gendoc.itext.services.PDFMerge;
import com.pg.dsm.gendoc.models.DigitalSpecCloudConfig;
import com.pg.dsm.gendoc.models.DigitalSpec;
import com.pg.dsm.gendoc.models.Document;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.time.Duration;
import java.time.Instant;
import org.apache.commons.io.FilenameUtils;
//Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
import com.microsoft.azure.storage.StorageException;
import java.net.URISyntaxException;
import com.pg.dsm.gendoc.util.GenDocProcessResponse;
import com.pg.dsm.ui.UIProgramUtil; //Added by DSM (Sogeti) for 22x.1 - Defect 51450
import com.pg.dsm.gendoc.util.FileRenameUtil;
//Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
// Cloud-GenDoc - ends

/**
 * The <code>emxCPNProductDataBase</code> class represents the Product Data
 * Functionality of the CPN
 *
 * @version CPN V6R2010
 */
public class pgIPMPDFViewUtil_mxJPO extends CPNCommon {
	/** select for owner.name */
	protected static String FTP_PROTOCOL = "";
	protected static int FTP_PORT = 21;
	protected static String FTP_HOST_NAME = "";
	protected static String FTP_USER_NAME = "";
	protected static String FTP_PASSWORD = "";
	protected static String FTP_INPUT_FOLDER = "";
	protected static String FTP_OUTPUT_FOLDER = "";
	protected static String FTP_ROOT_FOLDER = "";
	// Added by DSM(Sogeti)-2015x.1 for PDF Views (Defect ID-3787) - Starts
	//protected static String FTP_DTD_PATH = "";
	// Added by DSM(Sogeti)-2015x.1 for PDF Views (Defect ID-3787) - Ends
	//protected static String CLEANUP_ON_RESTART = "";
	protected static long SLEEP_INTERVAL = 20;
	//protected static int SLEEP_COUNTER = 0;
	//protected static String SUPPLIER_PDF_CONTENT_PREFIX = "";
	//protected static String START_SERVER = "true";
	public static String DATE_FORMAT = "";
	public static final int FIRST_LEVEL = 1;
	//public static final int SECOND_LEVEL = 2;
	//public static final String SELECT_OWNER_NAME = "owner.name";
	//protected static String CPN_PROPERTIES = "emxCPN";
	private static final String CONST_EMXCPN = "emxCPNStringResource";
	//protected static String CPN_RESOURCE_PROPERTIES = CONST_EMXCPN;
	//public static String RenderingFormat = PropertyUtil.getSchemaProperty("format_JView");
	//	/private static final String DYNAMIC_ROW_SEPERATOR = "~#~";
	private pgIPMPDFViewDataFormatter_mxJPO htmlFormatter;
	String strLanguage = "";
	private String strTempPath = "";
	private String strFileNameCheck = "";
	private String strParentObjectID = "";
	private transient Logger logger = Logger.getLogger(this.getClass().getName());
	private boolean isAllPopContext = false;
	private boolean isAllPushContext = false;
	
	//DSM(DS) 2022x CW4 REQ 48079,48164,48081,48082,48083,48084,48085,48086,48136,48137,48139,48141,48142,48145,48146,48147,48149,48150,48153,48161,48163,48080,48228,48229,48254,48230- Auto CRAD masters revise and release - START
	private static final String  STR_CONST_CA_FOR_AUTO_CRAD = "Yes";
	//DSM(DS) 2022x CW4 REQ 48079,48164,48081,48082,48083,48084,48085,48086,48136,48137,48139,48141,48142,48145,48146,48147,48149,48150,48153,48161,48163,48080,48228,48229,48254,48230- Auto CRAD masters revise and release - START
	/*Refactoring-Commented start
	private static final String CONST_TYPE_INCLUSION_LIST = "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.TypeInclusionList";
	private static final String CONST_NON_STRUCT_TYPE_INCLUSION_LIST = "emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList";
	private static final String CONST_GENDOC = "GenDoc";
	private static final String CONST_USER_AGENT = "User Agent";
	private static final String CONST_FORMAT_FILE_NAME = "format.file.name";
	private static final String CONST_ALLINFO = "allinfo";
	private static final String CONST_HEADER1 = "header1";
	private static final String CONST_HEADER2 = "header2";
	private static final String CONST_HEADER3 = "header3";
	private static final String CONST_STRHTMLWITHDATA = "strHTMLWithData";
	private static final String CONST_STRXMLTICKETCONTENT = "strXMLTicketContent";
	private static final String CONST_SUPPLIER = "supplier";
	private static final String CONST_CONSOLIDATEDPACKAGING = "consolidatedpackaging";
	private static final String CONST_WAREHOUSE = "warehouse";
	private static final String CONST_CONTRACTPACKAGING = "contractpackaging";
	private static final String CONST_STRING = "String";
	private static final String CONST_ASCENDING = "ascending";
	private static final String CONST_NO_ACCESS = "No Access";
	private static final String CONST_FALSE = "FALSE";
	private static final String CONST_TRUE = "TRUE";           pgV3Constants.CONST_TRUE
	private static final String CONST_TEXTCENTER = "TEXTCENTER";
	private static final String CONST_COMBINEDWITHMASTER = "combinedwithmaster";
	Refactoring-Commented end*/
	// Added by DSM(Sogeti)-2018x.1 for PDF Defect 25898 Starts
	//String ATTRIBUTE_V_NAME = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_Name");
	//String SELECT_ATTRIBUTE_V_NAME = "attribute[" + ATTRIBUTE_V_NAME + "]";
	// Added by DSM(Sogeti)-2018x.1 for PDF Defect 25898 Ends

	/**
	 * constructor of the emxProductDataBase.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param args    holds the following input arguments: 0 - objectList MapList
	 * @return Object of type MapList
	 * @throws Exception if the operation fails
	 * @since CPN V6R2010
	 */
	public pgIPMPDFViewUtil_mxJPO(Context context, String args[]) throws Exception {
		super();
		if (args != null && args.length > 0) {
			setId(args[0]);
		}

		SLEEP_INTERVAL = Integer.parseInt(EnoviaResourceBundle.getProperty(context, "emxCPN.PDF.SleepInterval"));
		DATE_FORMAT = EnoviaResourceBundle.getProperty(context, "eServiceSuiteCPN.defaultDateFormat");

		htmlFormatter = new pgIPMPDFViewDataFormatter_mxJPO(context,args);
		strLanguage = context.getSession().getLanguage();
		strTempPath = "";
		strFileNameCheck = "";
	}

	/**
	 * Main method of the emxCPNProductDataBase.
	 *
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param args    holds the following input arguments: 0 - objectList MapList
	 * @return int
	 * @throws Exception if the operation fails
	 * @since CPN V6R2010
	 */
	public int mxMain(Context context, String args[]) throws Exception {
		return 1;
	}

	/**
	 * Method is to cleanup the input and output adlib folders
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param         this method take string as argument. @ return void
	 *
	 */
	public void cleanAdlibFolders(Context context, String strTempFolder) throws Exception {

		FTPClient ftp = null;
		try {
			String path = DomainConstants.EMPTY_STRING;
			ftp = new FTPClient();
			ftp.connect(FTP_HOST_NAME);
			if (!ftp.login(FTP_USER_NAME, EncryptCrypto.decryptString(FTP_PASSWORD))) {
				ftp.logout();
			}
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
			}
			if (FTPReply.isPositiveCompletion(reply)) {
				System.out.println("connnecting2 in Clean......");
			}

			// Delete Output folder contain file
			String outputPath = FTP_OUTPUT_FOLDER + java.io.File.separator + strTempFolder;
			ftp.enterLocalPassiveMode();
			boolean bWrkDir = ftp.changeWorkingDirectory(outputPath);

			if (!bWrkDir) {
				ftp.makeDirectory(outputPath);
				bWrkDir = ftp.changeWorkingDirectory(outputPath);
			}
			String[] files = ftp.listNames();
			if (null != files && files.length > 0) {
				int size = files.length;

				for (int i = 0; i < size; i++) {
					path = outputPath + java.io.File.separator + files[i];
					ftp.deleteFile(path);
				}
			}
			ftp.removeDirectory(outputPath);

			// Delete Input folder contain file
			String inputPath = FTP_INPUT_FOLDER + java.io.File.separator + strTempFolder;
			ftp.enterLocalPassiveMode();
			bWrkDir = ftp.changeWorkingDirectory(inputPath);
			if (!bWrkDir) {
				ftp.makeDirectory(inputPath);
				bWrkDir = ftp.changeWorkingDirectory(inputPath);
			}

			files = ftp.listNames();
			if (null != files && files.length > 0) {
				int size = files.length;
				for (int i = 0; i < size; i++) {
					path = inputPath + java.io.File.separator + files[i];
					ftp.deleteFile(path);
				}
			}
			ftp.removeDirectory(inputPath);
		} catch (Exception ex) {
			throw new Exception(ex);
		} finally {
			if (null != ftp) {
				ftp.logout();
				ftp.disconnect();
			}
		}
	}

	/**
	 *
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static int startPDFGenerationJob(Context context, String[] args) throws Exception {
		String loggedinUser = PropertyUtil.getGlobalRPEValue(context, ContextUtil.MX_LOGGED_IN_USER_NAME);
		if (loggedinUser == null || "".equals(loggedinUser) || "null".equals(loggedinUser)
				|| ContextUtil.MX_LOGGED_IN_USER_NAME.equals(loggedinUser)) {
			if (args.length == 3) {
				loggedinUser = args[2];
			}
		} else {
			args[2] = loggedinUser;
		}

		Job job = new Job("pgCPNPDFUtil", "renderPDF", args, false);
		job.setTitle("PDF Generation Job");
		job.create(context);
		job.setOwner(context, loggedinUser);
		String sAttrPP = PropertyUtil.getSchemaProperty(context,"attribute_ProgressPercent");
		job.setAttributeValue(context, sAttrPP, "0");
		job.submit(context);
		String relPattern = PropertyUtil.getSchemaProperty(context, "relationship_PendingJob");
		job.addFromObject(context, new RelationshipType(relPattern), args[0]);
		return 0;
	}

	/**
	 * This public method will be executed to replace white spaces in a string
	 *
	 * @param String
	 * @return String without white spaces
	 * @since CPN V6R2010
	 */
	public static String replaceSpaces(String s) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c != ' ') {
				stringbuffer.append(c);
			}
		}
		return stringbuffer.toString();
	}

	/**
	 * @Desc: This method updates Error attributes if object is not connected to Gen
	 *        Doc prior release
	 * @param Context
	 * @param args    returns 1: if Error 0: if Success
	 **/
	public int pgCheckGenDoc(Context context, String[] args) throws Exception {
		int iReturn = 0;
		try {
			System.out.println("::::::::::pgCheckGendoc::Method::From jar::::::");
			//Added by DSM (Sogeti) for 2018x.5 Req #35245 - Starts 
			String strAttrValue;
			Attribute attribute;
			//Domain Object Internally make a call to Business Object, so we are directly using BusinessObject
			BusinessObject boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, pgV3Constants.GENDOC_ENABLEDISABLE_CONFIGOBJECT, pgV3Constants.SYMBOL_HYPHEN,pgV3Constants.VAULT_ESERVICEPRODUCTION);
			if (boConfig.exists(context)){
				attribute = boConfig.getAttributeValues(context, pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONATTR);
				strAttrValue = attribute.getValue().trim();				
				if(UIUtil.isNotNullAndNotEmpty(strAttrValue) && (pgV3Constants.ON).equalsIgnoreCase(strAttrValue)) {
				//Added by DSM (Sogeti) for 2018x.5 Req #35245 - Ends 
					String objectId = args[0];
					DomainObject domainObject = DomainObject.newInstance(context, objectId);
					DomainObject doAffectedItem = null;
					// Added by DSM(Sogeti) - Fix for 2018x.0 April Defect 25866
					String strReleasePhase = DomainConstants.EMPTY_STRING;
					StringList objectSelects = new StringList(5);
					objectSelects.add(DomainConstants.SELECT_TYPE);
					objectSelects.add(DomainConstants.SELECT_NAME);
					objectSelects.add(DomainConstants.SELECT_POLICY);
					objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
					objectSelects.add(DomainConstants.SELECT_FILE_FORMAT);
					
					//DSM(DS) 2022x CW4 REQ 48079,48164,48081,48082,48083,48084,48085,48086,48136,48137,48139,48141,48142,48145,48146,48147,48149,48150,48153,48161,48163,48080,48228,48229,48254,48230- Auto CRAD masters revise and release - START
					// To bypass CA when promote CA from backend
					objectSelects.add(DomainObject.getAttributeSelect(PropertyUtil.getSchemaProperty("attribute_pgAutoCRAD")));
					//DSM(DS) 2022x CW4 REQ 48079,48164,48081,48082,48083,48084,48085,48086,48136,48137,48139,48141,48142,48145,48146,48147,48149,48150,48153,48161,48163,48080,48228,48229,48254,48230- Auto CRAD masters revise and release - END

					Map mObjectDetails = domainObject.getInfo(context, objectSelects);
					
					//DSM(DS) 2022x CW4 REQ 48079,48164,48081,48082,48083,48084,48085,48086,48136,48137,48139,48141,48142,48145,48146,48147,48149,48150,48153,48161,48163,48080,48228,48229,48254,48230- Auto CRAD masters revise and release - START
					String strPgAutoCRAD = (String)mObjectDetails.get(DomainObject.getAttributeSelect(PropertyUtil.getSchemaProperty("attribute_pgAutoCRAD")));
					if(STR_CONST_CA_FOR_AUTO_CRAD.equalsIgnoreCase(strPgAutoCRAD)) {
						return iReturn;
					}
					//DSM(DS) 2022x CW4 REQ 48079,48164,48081,48082,48083,48084,48085,48086,48136,48137,48139,48141,48142,48145,48146,48147,48149,48150,48153,48161,48163,48080,48228,48229,48254,48230- Auto CRAD masters revise and release - END

					String strProcessGenDoc = (String) mObjectDetails.get(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
					String strType = (String) mObjectDetails.get(DomainConstants.SELECT_TYPE);
					String strPolicy = (String) mObjectDetails.get(DomainConstants.SELECT_POLICY);
					String strName = (String) mObjectDetails.get(DomainConstants.SELECT_NAME);
					String languageStr = context.getSession().getLanguage();
					String sCurrentDate = null;
					String strMessage = DomainConstants.EMPTY_STRING;
					StringList slFileFormat = (StringList) mObjectDetails.get(DomainConstants.SELECT_FILE_FORMAT);

					// Map attributes = null;

					Date today = null;

					SimpleDateFormat dateFormat = new SimpleDateFormat(eMatrixDateFormat.getInputDateFormat());
					//Modify Code Refactoring
					String cntlanguage=context.getLocale().getLanguage();
					if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strPolicy)
							|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
						if (null != strProcessGenDoc && pgV3Constants.CONST_TRUE.equals(strProcessGenDoc)
								&& pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strPolicy)) {
							strMessage = UINavigatorUtil.getI18nString("emxCPN.Alerts.RenderGenDocCA.MEPMarked", CONST_EMXCPN,
									cntlanguage);
							throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strMessage));
						} else if (null != strProcessGenDoc && pgV3Constants.CONST_TRUE.equals(strProcessGenDoc)
								&& pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equals(strPolicy)) {
							strMessage = UINavigatorUtil.getI18nString("emxCPN.Alerts.RenderGenDocCA.SEPMarked", CONST_EMXCPN,
									cntlanguage);
							throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strMessage));
						} else {
							String strFileFormat = null;
							String strGenDocAllowedFile = null;
							int nfileNames = slFileFormat.size();
							for (int i = 0; i < nfileNames; i++) {
								strFileFormat = (String) slFileFormat.get(i);
								if ("generic".equalsIgnoreCase(strFileFormat)) {
									strGenDocAllowedFile = strFileFormat;
									break;
								}
							}
							if ((UIUtil.isNotNullAndNotEmpty(strGenDocAllowedFile))
									&& (!isGenDocPresent(context, domainObject))) {
								// attributes = new HashMap();
								today = new Date();
								sCurrentDate = dateFormat.format(today);
								//						String strErrorMessage = i18nNow.getI18nString("emxCPN.PDF.RnditionErrorFailedMessage",
								//								"emxCPN", languageStr) + strName;
								String strErrorMessage =EnoviaResourceBundle.getProperty(context,CPN_PROPERTIES,context.getLocale(),"emxCPN.PDF.RnditionErrorFailedMessage") + strName;
								// attributes.put(pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION,
								// pgV3Constants.PDFVIEW_RENDITION);
								// attributes.put(pgV3Constants.ATTRIBUTE_PGERRORDATE, sCurrentDate);
								// domainObject.setAttributeValues(context, attributes);
								setErrorClassificationAttributes(context, doAffectedItem, pgV3Constants.PDFVIEW_RENDITION,
										sCurrentDate);
								throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strErrorMessage));
							}
						}
					} else if (pgV3Constants.TYPE_CHANGEACTION.equals(strType) && null != strProcessGenDoc
							&& pgV3Constants.CONST_TRUE.equals(strProcessGenDoc)) {
						strMessage = UINavigatorUtil.getI18nString("emxCPN.Alerts.RenderGenDocCA.CAMarked", CONST_EMXCPN,
								cntlanguage);

						// Added by DSM-2015x.5.1 for PDF Views (Defect id #17951) - Starts
						String sRelatedRelationshipPattern = pgV3Constants.RELATIONSHIP_AFFECTEDITEM + ","
								+ pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM + ","
								+ pgV3Constants.RELATIONSHIP_IMPLEMENTEDITEM;
						StringList slBusSelects = new StringList(4);
						slBusSelects.add(DomainConstants.SELECT_POLICY);
						slBusSelects.add(DomainConstants.SELECT_ID);
						slBusSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
						slBusSelects.add(DomainConstants.SELECT_FILE_FORMAT);
						// Added by DSM(Sogeti) - Fix for 2018x.0 April Defect 25866
						slBusSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
						String strRelatedProcessGenDoc = DomainConstants.EMPTY_STRING;
						String strRelatedId = DomainConstants.EMPTY_STRING;
						String strRelatedPolicy = DomainConstants.EMPTY_STRING;
						String strTempFileName = DomainConstants.EMPTY_STRING;
						StringList slRelatedFileFormat = new StringList();
						DomainObject doRelatedItem = null;
						// DSM(DS) 2018x.0 ALM # 21048 - The CA for revised part cannot be promoted. -
						// Start
						HashMap programMap=new HashMap();
						programMap.put("CAId", objectId);
						programMap.put("slSelect", slBusSelects);
						programMap.put("reqChang", pgV3Constants.For_Release);
						programMap.put("affectedRel", true);
						programMap.put("impRel", true);
						String[] strArgs =JPO.packArgs(programMap);
						//				MapList mlRelatedObjects = pgDSMChangeUtil_mxJPO.getAffectedItemForCA(context, objectId, slBusSelects,
						//						pg.For_Release, true, true);
						MapList mlRelatedObjects =(MapList)pgPDFViewHelper.executeMainClassMethod(context, "pgDSMChangeUtil", "getAffectedItemForCA", strArgs);
						/*
						 * MapList mlRelatedObjects = domainObject.getRelatedObjects(context,
						 * sRelatedRelationshipPattern, "*", slBusSelects, null, false, true, (short)1,
						 * null, null );
						 */
						// DSM(DS) 2018x.0 ALM # 21048 - The CA for revised part cannot be promoted. -
						// End
						if (mlRelatedObjects != null && (mlRelatedObjects.size() > 0)) {
							for (int j = 0; j < mlRelatedObjects.size(); j++) {
								Map mRelatedData = (Map) mlRelatedObjects.get(j);
								strRelatedId = (String) mRelatedData.get(DomainConstants.SELECT_ID);
								doRelatedItem = DomainObject.newInstance(context, strRelatedId);
								strRelatedPolicy = (String) mRelatedData.get(DomainConstants.SELECT_POLICY);
								// Added by DSM(Sogeti) - Fix for 2018x.0 April Defect 25866
								strReleasePhase = (String) mRelatedData.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
								if (UIUtil.isNotNullAndNotEmpty(strRelatedPolicy)
										&& (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strRelatedPolicy)
												|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT
												.equalsIgnoreCase(strRelatedPolicy))) {
									strRelatedProcessGenDoc = (String) mRelatedData
											.get(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
									Object objDataValue = (Object) mRelatedData.get(DomainConstants.SELECT_FILE_FORMAT);
									if (objDataValue instanceof String) {
										strTempFileName = (String) objDataValue;
									} else if (objDataValue instanceof StringList) {
										slRelatedFileFormat = (StringList) objDataValue;
									}

									String strRelatedFileFormat = null;
									String strRelatedGenDocAllowedFile = null;
									int ifileNames = slRelatedFileFormat.size();
									if (null != slRelatedFileFormat && ifileNames > 0) {
										for (int q = 0; q < ifileNames; q++) {
											strRelatedFileFormat = (String) slRelatedFileFormat.get(q);
											if ("generic".equalsIgnoreCase(strRelatedFileFormat)) {
												strRelatedGenDocAllowedFile = strRelatedFileFormat;
												break;
											}
										}
									} else if (UIUtil.isNotNullAndNotEmpty(strTempFileName)) {
										strRelatedGenDocAllowedFile = strTempFileName;
									}

									if ((UIUtil.isNotNullAndNotEmpty(strRelatedGenDocAllowedFile))
											&& (!isGenDocPresent(context, doRelatedItem))) {
										System.out.println("Inside If ....exception...........");
										throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strMessage));
									}
								} else {
									// Modified by DSM(Sogeti) - Fix for 2018x.0 April Defect 25866
									// if(!isGenDocPresent(context, doRelatedItem)){
									if (!pgV3Constants.DEVELOPMENT.equals(strReleasePhase) && !pgV3Constants.PILOT.equals(strReleasePhase)
											&& !isGenDocPresent(context, doRelatedItem)) {
										throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strMessage));
									}
								}
							}
						}
						// Added by DSM-2015x.5.1 for PDF Views (Defect id #17951) - Ends

					} else {
						String strId = "";
						String strEnginuityAuthored = DomainConstants.EMPTY_STRING;
						String strRequestedChange = DomainConstants.EMPTY_STRING;
						// Modified by DSM-2015x.5.1 for PDF Views (Defect id #17951) - Starts
						// Commented  by DSM-2018x.3  for PDF Views (Defect id #29918) - Starts
						//StringList slRelFileFormat = new StringList();
						// Commented  by DSM-2018x.3  for PDF Views (Defect id #29918) - Ends

						// Added by DSM-2018x.0 for Gendoc Issue (Defect id #21236) - Starts
						String strSingleFileName = DomainConstants.EMPTY_STRING;
						// Added by DSM-2018x.0 for Gendoc Issue (Defect id #21236) - Ends

						StringList busSelects = new StringList(6);
						busSelects.add(DomainConstants.SELECT_TYPE);
						busSelects.add(DomainConstants.SELECT_NAME);
						busSelects.add(DomainConstants.SELECT_POLICY);
						busSelects.add(DomainConstants.SELECT_ID);
						busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
						busSelects.add(DomainConstants.SELECT_FILE_FORMAT);
						// Modified by DSM-2015x.5.1 for PDF Views (Defect id #17951) - Ends
						// DSM (DS) 2018x.1 - ALM - 24673 - GenDoc check should be excluded for the
						// Development Parts - Starts
						busSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
						String strObjReleasePhase = DomainConstants.EMPTY_STRING;
						// DSM (DS) 2018x.1 - ALM - 24673 - GenDoc check should be excluded for the
						// Development Parts - Ends
						String strObjPolicy = DomainConstants.EMPTY_STRING;
						String strValidPolicy = EnoviaResourceBundle.getProperty(context,"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.PolicyInclusionListGenDoc");
						StringList slList = FrameworkUtil.split(strValidPolicy, ",");

						String strGendocExcludeTypes = EnoviaResourceBundle.getProperty(context,
								"emxCPN.PDFViews.ContentFileCheck.TypesExcludeList");
						String strObjType = DomainConstants.EMPTY_STRING;

						StringList relSelects = new StringList(1);
						relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_REQUESTEDCHANGE);

						String relationshipPattern = pgV3Constants.RELATIONSHIP_AFFECTEDITEM + ","
								+ pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM + ","
								+ pgV3Constants.RELATIONSHIP_IMPLEMENTEDITEM;
						StringBuffer sbTypePattern = new StringBuffer(250);
						sbTypePattern.append(EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST).trim());
						sbTypePattern.append(",");
						sbTypePattern
						.append(EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_NON_STRUCT_TYPE_INCLUSION_LIST).trim());
						// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
						// START
						ChangeAction caInstance = new ChangeAction(objectId);

						HashMap programMap=new HashMap();
						programMap.put("CAId", objectId);
						programMap.put("slSelect", busSelects);
						programMap.put("reqChang", pgV3Constants.For_Release);
						programMap.put("affectedRel", true);
						programMap.put("impRel", true);
						String[] strArgs =JPO.packArgs(programMap); 
						//				MapList maplistObjects = pgDSMChangeUtil_mxJPO.getAffectedItemForCA(context, objectId, busSelects,
						//						pg.For_Release, true, true);
						MapList maplistObjects = (MapList)pgPDFViewHelper.executeMainClassMethod(context,"pgDSMChangeUtil", "getAffectedItemForCA", strArgs);
						/*
						 * MapList maplistObjects = domainObject.getRelatedObjects(context,
						 * relationshipPattern, sbTypePattern.toString(), busSelects, // object Select
						 * relSelects, // rel Select false, // to true, // from (short)1, null, // ob
						 * where null // rel where );
						 */
						// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
						// END
						if (maplistObjects != null && (maplistObjects.size() > 0)) {
							for (int p = 0; p < maplistObjects.size(); p++) {
								Map mData = (Map) maplistObjects.get(p);
								// Added  by DSM-2018x.3  for PDF Views (Defect id #29918) - Starts
								StringList slRelFileFormat = new StringList();
								// Added  by DSM-2018x.3  for PDF Views (Defect id #29918) - Ends
								// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
								// START
								strId = (String) mData.get(DomainConstants.SELECT_ID);
								// DSM(DS) 2018x.0 ALM # 21048 - The CA for revised part cannot be promoted. -
								// Start
								strRequestedChange = (String) mData.get(DomainConstants.ATTRIBUTE_REQUESTED_CHANGE);
								if (UIUtil.isNullOrEmpty(strRequestedChange)) {
									strRequestedChange = caInstance.getRequestedChangeFromChangeAction(context, strId,
											objectId);
								}
								// DSM(DS) 2018x.0 ALM # 21048 - The CA for revised part cannot be promoted. -
								// End
								// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
								// END
								if (UIUtil.isNotNullAndNotEmpty(strRequestedChange)
										&& pgV3Constants.For_Release.equals(strRequestedChange)) {
									strEnginuityAuthored = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
									if (UIUtil.isNotNullAndNotEmpty(strEnginuityAuthored)
											&& !("True".equalsIgnoreCase(strEnginuityAuthored))) {
										strName = (String) mData.get(DomainConstants.SELECT_NAME);
										strId = (String) mData.get(DomainConstants.SELECT_ID);

										strObjType = (String) mData.get(DomainConstants.SELECT_TYPE);
										strObjPolicy = (String) mData.get(DomainConstants.SELECT_POLICY);
										doAffectedItem = DomainObject.newInstance(context, strId);
										// DSM (DS) 2018x.1 - ALM - 24673 - GenDoc check should be excluded for the
										// Development Parts - Starts
										strObjReleasePhase = (String) mData.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
										if (UIUtil.isNotNullAndNotEmpty(strObjPolicy)
												&& pgV3Constants.POLICY_EC_PART.equalsIgnoreCase(strObjPolicy)
												&& UIUtil.isNotNullAndNotEmpty(strObjReleasePhase)
												&& pgV3Constants.DEVELOPMENT.equalsIgnoreCase(strObjReleasePhase)) {
											continue;
										}
										// DSM (DS) 2018x.1 - ALM - 24673 - GenDoc check should be excluded for the
										// Development Parts - Ends
										if (strGendocExcludeTypes.contains(strObjType)) {
											String strFileFormat = null;
											String strGenDocAllowedFile = null;
											int nfileNames = slFileFormat.size();
											for (int i = 0; i < nfileNames; i++) {
												strFileFormat = (String) slFileFormat.get(i);
												if ("generic".equalsIgnoreCase(strFileFormat)) {
													strGenDocAllowedFile = strFileFormat;
													break;
												}
											}
											if (UIUtil.isNullOrEmpty(strGenDocAllowedFile)) {
												break;
											}
										}
										// Added by DSM-2015x.5.1 for PDF Views (Defect id #17951) - Starts
										if (UIUtil.isNotNullAndNotEmpty(strObjPolicy)
												&& (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strObjPolicy)
														|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT
														.equalsIgnoreCase(strObjPolicy))) {

											// Modified by DSM-2018x.0 for Gendoc Issue (Defect id #21236) - Starts
											Object objDataValue = (Object) mData.get(DomainConstants.SELECT_FILE_FORMAT);
											if (objDataValue instanceof String) {
												strSingleFileName = (String) objDataValue;
											} else if (objDataValue instanceof StringList) {
												slRelFileFormat = (StringList) objDataValue;
											}

											String strRelatedFileFormat = null;
											String strRelGenDocAllowedFile = null;
											int ifileNames = slRelFileFormat.size();
											if (null != slRelFileFormat && ifileNames > 0) {
												for (int q = 0; q < ifileNames; q++) {
													strRelatedFileFormat = (String) slRelFileFormat.get(q);
													if ("generic".equalsIgnoreCase(strRelatedFileFormat)) {
														strRelGenDocAllowedFile = strRelatedFileFormat;
														break;
													}
												}
											} else if (UIUtil.isNotNullAndNotEmpty(strSingleFileName)) {
												strRelGenDocAllowedFile = strSingleFileName;
											}
											if ((UIUtil.isNotNullAndNotEmpty(strRelGenDocAllowedFile))
													&& (!isGenDocPresent(context, doAffectedItem))) {
												System.out.println("Inside If ....no gendoc....exception...........");
												throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strMessage));
											}
										}
										// Added by DSM-2015x.5.1 for PDF Views (Defect id #17951) - Ends
										// Modified by DSM(Sogeti) - Fix for 2018x.0 April Defect 25866
										// if(!isGenDocPresent(context, doAffectedItem) &&
										// (UIUtil.isNotNullAndNotEmpty(strObjPolicy) && slList.contains(strObjPolicy)
										// && !pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strObjPolicy) &&
										// !pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjPolicy))){
										if (!pgV3Constants.DEVELOPMENT.equals(strObjReleasePhase) && !pgV3Constants.PILOT.equals(strObjReleasePhase)
												&& !isGenDocPresent(context, doAffectedItem)
												&& (UIUtil.isNotNullAndNotEmpty(strObjPolicy) && slList.contains(strObjPolicy)
														&& !pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strObjPolicy)
														&& !pgV3Constants.POLICY_SUPPLIEREQUIVALENT
														.equalsIgnoreCase(strObjPolicy))) {
											// Modified by DSM-2018x.0 for Gendoc Issue (Defect id #21236) - Ends

											// attributes = new HashMap();
											today = new Date();
											sCurrentDate = dateFormat.format(today);
											//									String strErrorMessage = i18nNow.getI18nString(
											//											"emxCPN.PDF.RnditionErrorFailedMessage", "emxCPN", languageStr) + strName;
											String strErrorMessage = EnoviaResourceBundle.getProperty(context,CPN_PROPERTIES,context.getLocale(),"emxCPN.PDF.RnditionErrorFailedMessage") + strName;
											setErrorClassificationAttributes(context, doAffectedItem,
													pgV3Constants.PDFVIEW_RENDITION, sCurrentDate);
											/*
											 * attributes.put(pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION,
											 * pgV3Constants.PDFVIEW_RENDITION);
											 * attributes.put(pgV3Constants.ATTRIBUTE_PGERRORDATE, sCurrentDate);
											 * doAffectedItem.setAttributeValues(context, attributes);
											 */

											throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strErrorMessage));
										}
									}
								}
							}
						}

					}
		//Added by DSM (Sogeti) for 2018x.5 Req #35245 - Starts 
				}
			}
		//Added by DSM (Sogeti) for 2018x.5 Req #35245 - Ends 
		} catch (Exception ex) {
			ex.printStackTrace();

			throw new Exception(ex.getMessage());

		}
		return iReturn;
	}

	/**
	 * This method will update the Error classification attribues via PushContext.
	 */
	private static void setErrorClassificationAttributes(Context context, DomainObject doAffectedItem,
			String PGERRORCLASSIFICATION, String PGERRORDATE) throws Exception {
		boolean isContextPushed = false;

		try {
			if (UIUtil.isNotNullAndNotEmpty(PGERRORCLASSIFICATION) && UIUtil.isNotNullAndNotEmpty(PGERRORDATE)
					&& doAffectedItem != null) {
				Map attributes = new HashMap();
				ContextUtil.pushContext(context);
				isContextPushed = true;
				attributes.put(pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION, PGERRORCLASSIFICATION);
				attributes.put(pgV3Constants.ATTRIBUTE_PGERRORDATE, PGERRORDATE);
				doAffectedItem.setAttributeValues(context, attributes);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}

	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public Map startGenDocGenerationJob(Context context, String[] args) throws Exception {		

		Map<String, String> mRenderPDFDetails = new HashMap();
		HashMap hmProgramMap = (HashMap) JPO.unpackArgs(args);
		String strObjectID = (String) hmProgramMap.get("objectID");
		Map<String, String> mAdlibDetails = (Map) hmProgramMap.get("AdlibDetails");
		//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47391 - Starts
	   /* StringList slObjSelect = new StringList();		
		slObjSelect.add(pgV3Constants.SELECT_ID);
				
		StringList slObject = new StringList();
		slObject.add(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
		slObject.add(pgV3Constants.SELECT_POLICY);
		slObject.add(pgV3Constants.SELECT_TYPE);		 
		
		MapList mlConnectedGenDoc = null;
		Map mpConnectedGenDoc = null;
		String strIPMDocumentId=DomainConstants.EMPTY_STRING;
		String strMarkedforGenDoc=DomainConstants.EMPTY_STRING;;
		String strMarkedObjectPolicy = DomainConstants.EMPTY_STRING;;
		String strMarkedObjectType=DomainConstants.EMPTY_STRING;
		//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47391 - Ends
		*/
		try {
			java.util.Date today = new Date();
			DomainObject domGCAS = DomainObject.newInstance(context, strObjectID);
			boolean isGenDocPresent = isGenDocPresent(context, domGCAS);
			//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47391 - Starts
			/*
			if(isGenDocPresent) {
			Map mpDomainObject = domGCAS.getInfo(context, slObject);
			strMarkedforGenDoc = (String) mpDomainObject.get(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
			strMarkedObjectPolicy = (String) mpDomainObject.get(pgV3Constants.SELECT_POLICY);
			strMarkedObjectType = (String) mpDomainObject.get(pgV3Constants.SELECT_TYPE);
			if(isAllowedTypeforManualGenDoc(context,strMarkedObjectType,strMarkedObjectPolicy) && pgV3Constants.TRUE.equalsIgnoreCase(strMarkedforGenDoc)) {
				mlConnectedGenDoc = domGCAS.getRelatedObjects(context, //context
				pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT,//relationshipPattern 
				pgV3Constants.TYPE_PGIPMDOCUMENT,//typePattern
				slObjSelect, //objectSelects
				null,//relationshipSelects 
				false, //getTo
				true,//getFrom
				(short)1,//recurseToLevel 
				"revision==Rendition", //objectWhere
				null,//relationshipWhere
				0);//limit
			if(null != mlConnectedGenDoc && !mlConnectedGenDoc.isEmpty()) {
				mpConnectedGenDoc = (Map) mlConnectedGenDoc.get(0);
				strIPMDocumentId = (String) mpConnectedGenDoc.get(pgV3Constants.SELECT_ID);
				
			}
				//Deleting the IPM Document
				CommonDocument comDoc = new CommonDocument(strIPMDocumentId);					
				try {
				comDoc.deleteDocuments(context, new String [] {strIPMDocumentId});
				domGCAS.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgV3Constants.FALSE);
				}
				catch (Exception e) {
					System.out.print(e.getMessage());
		        }
			}
		}
		*/  
			//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47391 - Ends	

			if (!isGenDocPresent) {
				Map attributes = new HashMap();

				String strATTFailedReason = pgV3Constants.ATTRIBUTE_PGFAILEDREASON;
				String strAttErrClass = pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION;
				String strAttErrDate = pgV3Constants.ATTRIBUTE_PGERRORDATE;

				SimpleDateFormat dateFormat = new SimpleDateFormat(eMatrixDateFormat.getInputDateFormat());

				String sCurrentDate = "";
				sCurrentDate = dateFormat.format(today);

                // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
                String rootFolderPrefix;
                if (mAdlibDetails.containsKey(CloudConstants.Basic.GEN_DOC_BLOB_ROOT_FOLDER_NAME.getValue())) {
                    rootFolderPrefix = mAdlibDetails.get(CloudConstants.Basic.GEN_DOC_BLOB_ROOT_FOLDER_NAME.getValue());
                } else {
                    rootFolderPrefix = getGenDocBlobFolderName(context, domGCAS);
                }
                // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

                // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
                // Add 4th argument in the array.
                String[] argumentDetails = new String[]{strObjectID, pgV3Constants.PDFVIEW_GENDOC, pgV3Constants.PERSON_USER_AGENT, rootFolderPrefix};
                // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts

				// Cloud-GenDoc start
                ICloudConfig cloudConfig = new DigitalSpecCloudConfig(context);
                if (CloudGenDocUtil.useCloud(cloudConfig)) {
                    mRenderPDFDetails = beginCloudGenDocProcess(context, argumentDetails, cloudConfig);
                 // Cloud-GenDoc end
                } else {
                    // else use - adlib.
				mRenderPDFDetails = renderPDF(context, argumentDetails, mAdlibDetails);
                }
                

				String strErrorMsg = mRenderPDFDetails.get("strErrorMsg");
				String strReturnType = mRenderPDFDetails.get("strReturnType");

                if (UIUtil.isNotNullAndNotEmpty(strErrorMsg) || "1".equals(strReturnType)) {
                    sCurrentDate = dateFormat.format(today);
                    attributes.put(strATTFailedReason, strErrorMsg);
                    attributes.put(strAttErrClass, "Rendition");
                    attributes.put(strAttErrDate, sCurrentDate);
                    domGCAS.setAttributeValues(context, attributes);
                    throw (new Exception(pgV3Constants.PDFVIEW_RENDITION + " : " + strErrorMsg));
                } else {
                    attributes.put(strATTFailedReason, "");
                    attributes.put(strAttErrClass, "");
                    attributes.put(strAttErrDate, "");
                    domGCAS.setAttributeValues(context, attributes);
                }
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        } catch (Throwable throwableError) {
            throwableError.printStackTrace();
            System.out.println(throwableError.getSuppressed());
            throw new Exception(throwableError.getMessage());
        }
        return mRenderPDFDetails;
    }

	/**
	 * This override method of renderPDF method.This method will call from
	 * All/Supplier/Consolidate/Warehouse/Combined PDF view
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String  args[] holds request parameters
	 * @return String for renderPDF path
	 * @throws Exception if the operation fails
	 */
	public String renderPDF(Context context, String args[]) throws Exception {

		String objectId = args[0];
		//Modify by DSM(Sogeti) 2018x.6.1 Defect #43129 - START
		args[0] = DomainObject.newInstance(context,objectId).getInfo(context, DomainConstants.SELECT_ID);
		logger.log(Level.INFO, "I am inside the jar files");
		logger.log(Level.INFO," Method has started renderPDF--->  : {0}",(args[0]));
		//Modify by DSM(Sogeti) 2018x.6.1 Defect #43129 - END

		long startTime = new Date().getTime();
		Map<String, String> mRenderDetails = renderPDF(context, args, null);
		long endTime = new Date().getTime();

		System.out.println("Total Time has taken by the Render PDF Method is-->" + (endTime - startTime));

		return mRenderDetails.get("returnString");
	}
	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process GenDoc. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param String strGenDocFileName - holds GenDoc file name
	 * @param String strGenDocFilePath - holds GenDoc file path
	 * @param Map mapAdlibTicketData - holds data of GenDoc
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processGendoc(Context context, 
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName,
			StringBuffer sbGenDocFileName, 
			StringBuffer sbGenDocFilePath,
			String strTempFileName,
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent) throws Exception {
		
		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		String strNonStructuredTypes = (String) mapAdlibTicketData.get(pgPDFViewConstants.CONT_UNSTRUCTUREDTYPES);
		//Added by DSM 2018x.5 Defect req : 33190 Start
		String strAuthoringApplication = (String) mapAdlibTicketData.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		//Added by DSM 2018x.5 Defect req : 33190 End


		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;

		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind);
		String strHeader1 = DomainConstants.EMPTY_STRING;

		if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)
				|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
			if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader1 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.GenDoc.MEP_SIS.header",CONST_EMXCPN, strLanguage);
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.MEP.header",CONST_EMXCPN, strLanguage);
				strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_MEP";
			} else if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader1 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.GenDoc.SEP_SIS.header",CONST_EMXCPN, strLanguage);
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.SEP.header",CONST_EMXCPN, strLanguage);
				strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_SEP";
			}
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strHeader1);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);

			Map mAdlibTicketDetails = (Map) getAdlibTicketXMLContent(context, objectId, ftpInputFolder,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData,
					strPDFViewKind);

            // Cloud Gen-Doc start -
            if (mAdlibTicketDetails.containsKey("checkOutFileDetailsInJSONFormat")) {
                String checkOutFileDetailsInJSONFormat = (String) mAdlibTicketDetails.get("checkOutFileDetailsInJSONFormat");
                mapAdlibTicketData.put("checkOutFileDetailsInJSONFormat", checkOutFileDetailsInJSONFormat);
            }
            // Cloud Gen-Doc end

			sbXMLTicketContent.append((String) mAdlibTicketDetails.get("AdlibTicketDetails"));
			sbGenDocFileName.append((String) mAdlibTicketDetails.get("strfileName"));
			sbGenDocFilePath.append((String) mAdlibTicketDetails.get("strTempPath"));
		} else {

			String strTechnicalStdHeader = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.GenDoc.TechicalStandard", CONST_EMXCPN, strLanguage);
			//Modify Code Refactoring
			strTechSpecType=strTechSpecType.trim();			
			if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART
					.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGCUSTOMERUNITPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGINNERPACKUNITPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGMASTERCUSTOMERUNITPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGMASTERINNERPACKUNITPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGTRANSPORTUNITPART.equals(strTechSpecType.trim())
					|| pgV3Constants.TYPE_PGMASTERCONSUMERUNITPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGPROMOTIONALITEMPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGMASTERPACKAGINGASSEMBLYPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGMASTERPACKAGINGMATERIALPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PACKAGINGMATERIALPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_PGONLINEPRINTINGPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_FABRICATEDPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_SOFTWAREPART.equals(strTechSpecType)) {
				//Modify Code Refactoring
				//							pgV3ConstantsForTypesStringList=FrameworkUtil.split(pgPDFViewConstants.pgV3ConstantsForTypes, ",");
				//							if(pgV3ConstantsForTypesStringList.contains(strTechSpecType.trim()))
				//								  {	
				strPDFViewKindExtension = DomainConstants.EMPTY_STRING;

				if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTechSpecType)) {
					strHeader2 = UINavigatorUtil.getI18nString(
							"emxCPN.PDFViews.ALLView.FinishedProductPart.header", CONST_EMXCPN,
							strLanguage);
					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirement  - Starts
					strHeader2 = getFPPShippableHALBHeader(strTechSpecType, loadShortType(context, strTechSpecType), strAssemblyType, strHeader2);
					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements  - Ends

				} else if (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strTechSpecType)) {
					strHeader2 = UINavigatorUtil.getI18nString(
							"emxCPN.PDFViews.ALLView.PackagingAssemblyPart.header", CONST_EMXCPN,
							strLanguage);
				} else if (pgV3Constants.TYPE_PACKAGINGMATERIALPART.equals(strTechSpecType)) {
					strHeader2 = UINavigatorUtil.getI18nString(
							"emxCPN.PDFViews.ALLView.PackagingMaterialPart.header", CONST_EMXCPN,
							strLanguage);

				} else if (pgV3Constants.TYPE_SOFTWAREPART.equals(strTechSpecType)) {
					strHeader2 = "SOFTWARE PART (SWP) -";
				}

				else {
					strHeader2 = UINavigatorUtil.getI18nString(
							"emxCPN.PDFViews.ALLView." + strTechSpecType + ".header",
							CONST_EMXCPN, strLanguage);
				}
				//Modify Code Refactoring
				//								String strServerPath = i18nObject.GetString("emxCPN", strLocale, "emxCPN.ServerPath");
				String strServerPath = EnoviaResourceBundle.getProperty(context,"emxCPN", context.getLocale(), "emxCPN.ServerPath");
				String strPDFXMLBase = strServerPath + java.io.File.separator + "pdfHtmlBase";
				mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strTechnicalStdHeader);
				mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);

				BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
						.getFinishedProductPartSelectableMap(context);

				Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
						.getpgFinishedProductRelatedSelectableMap(context, false);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);

				if (pgV3Constants.TYPE_PGTRANSPORTUNITPART.equals(strTechSpecType)) {
					strPDFViewKindExtension = "_TU";
				}

				else if (pgV3Constants.TYPE_SOFTWAREPART.equals(strTechSpecType)) {
					strPDFViewKindExtension = "_SWP";
				} else {
					strAbbr = loadShortType(context, strTechSpecType);
					strPDFViewKindExtension = "_" + strAbbr;
				}
				//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 32001- Starts
				strPDFViewKindExtension = getFPPShippableHALBExtension(strTechSpecType, strAbbr, strAssemblyType, strPDFViewKindExtension);
				//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 32001- Ends

				consolidatedMap = getHTMLAndXMLContent(context, objectId,
						CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
						BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
						!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
						strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
						vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
						!ifTogetPrimaryAndSecondaryOrganization);
				sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
				sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
				//Modified by DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start
			} else if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_DEVICEPRODUCTPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_RAWMATERIALPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_MASTERRAWMATERIALPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_MASTERPRODUCTPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART.equals(strTechSpecType)
					|| pgV3Constants.TYPE_FORMULATIONPART.equals(strTechSpecType)
					|| (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)
							&& "DSO".equals(strOriginatingSource))
					|| pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equals(strTechSpecType) || pgV3Constants.TYPE_STRUCTURED_ATS.equals(strTechSpecType)) {
				//Modified by DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End
				if (pgV3Constants.TYPE_RAWMATERIALPART.equals(strTechSpecType)) {
					strHeader2 = UINavigatorUtil.getI18nString(
							"emxCPN.PDFViews.ALLView.RawMaterialPart.header", CONST_EMXCPN,
							strLanguage);
				} else if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)) {
					strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.IRMS.header",
							CONST_EMXCPN, strLanguage);
				} else if (pgV3Constants.TYPE_FORMULATIONPART.equals(strTechSpecType)) {
					strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.FOP.header",
							CONST_EMXCPN, strLanguage);
					//Added by DSM 2018x.5 Defect req : 33190 Start
				}else if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType) && "LPD".equalsIgnoreCase(strAuthoringApplication)) {
					strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView." + strTechSpecType + pgPDFViewConstants.CATIA_HEADER, CONST_EMXCPN,strLanguage);
					//Added by DSM 2018x.5 Defect req : 33190 End
				//Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start
				}else if(pgV3Constants.TYPE_STRUCTURED_ATS.equals(strTechSpecType)) {
					strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.Gendoc.pgAuthorizedTemporarySpecification.header",CONST_EMXCPN, strLanguage);
			   //Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End
				}else {
					strHeader2 = UINavigatorUtil.getI18nString(
							"emxCPN.PDFViews.ALLView." + strTechSpecType + ".header",
							CONST_EMXCPN, strLanguage);
				}
				//Modify Code Refactoring
				//								String strServerPath = i18nObject.GetString("emxCPN", strLocale, "emxCPN.ServerPath");
				String strServerPath = EnoviaResourceBundle.getProperty(context,"emxCPN", context.getLocale(), "emxCPN.ServerPath");
				String strPDFXMLBase = strServerPath + java.io.File.separator + "pdfHtmlBase";
				mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strTechnicalStdHeader);
				mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
				BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
						.getFinishedProductPartSelectableMap(context);
				Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
						.getpgFinishedProductRelatedSelectableMap(context, false);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);

				if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)) {
					strPDFViewKindExtension = "_IRMS";
					//Added by DSM 2018x.5 Defect req : 33190 Start
				}else if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType) && "LPD".equalsIgnoreCase(strAuthoringApplication)) {
					strAbbr = loadShortType(context, strTechSpecType);
					strPDFViewKindExtension = "_" + strAbbr + "_" + pgPDFViewConstants.CONS_CATIA;
					//Added by DSM 2018x.5 Defect req : 33190 End
				} else {
					strAbbr = loadShortType(context, strTechSpecType);
					strPDFViewKindExtension = "_" + strAbbr;
				}
				consolidatedMap = getHTMLAndXMLContent(context, objectId,
						CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
						BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
						!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
						strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
						vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
						ifTogetPrimaryAndSecondaryOrganization);
				sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
				sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));

			} else if (strNonStructuredTypes.contains(strTechSpecType)) {
				if (!"".equalsIgnoreCase(strTempFileName) && null != strTempFileName) {
					if (pgV3Constants.TYPE_PGPACKINGINSTRUCTIONS.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_PI";
					} else if (pgV3Constants.TYPE_PGSTACKINGPATTERN.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_SP";

					}//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts 
					else if (pgV3Constants.TYPE_PGARTWORK.equals(strTechSpecType) || pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD
							.equals(strTechSpecType)) {
						String strServerPath = EnoviaResourceBundle.getProperty(context,"emxCPN", context.getLocale(), "emxCPN.ServerPath");
						String strPDFXMLBase = strServerPath + java.io.File.separator + "pdfHtmlBase";
						strTechnicalStdHeader = UINavigatorUtil.getI18nString("emxCPN.PDFViews.GenDoc.TechicalStandard", CONST_EMXCPN, strLanguage);
						strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.Gendoc." + strTechSpecType + ".header",CONST_EMXCPN, strLanguage);
						mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strTechnicalStdHeader);
						mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
						BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getNonStructuredPartSelectableMap(context);
						strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
						strAbbr = loadShortType(context, strTechSpecType);
						strPDFViewKindExtension = "_" + strAbbr;
						consolidatedMap = getHTMLAndXMLContent(context, objectId,CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),BusinessObjectSelectableMap, 
								null, FIRST_LEVEL, ftpInputFolder,!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, 
								strPDFViewKind, strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
								vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
								ifTogetPrimaryAndSecondaryOrganization);
								sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
								Map mAdlibTicketDetails = (Map) consolidatedMap.get(pgPDFViewConstants.CONST_STRADLIB);
								sbXMLTicketContent.append((String) mAdlibTicketDetails.get("AdlibTicketDetails"));
								sbGenDocFileName.append((String) mAdlibTicketDetails.get("strfileName"));
								sbGenDocFilePath.append((String) mAdlibTicketDetails.get("strTempPath"));
					} 
					//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends
					else if (pgV3Constants.TYPE_PGILLUSTRATION.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_ILST";
					} else if (pgV3Constants.TYPE_RAW_MATERIAL_PLANT_INSTRUCTION
							.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_RMPI";
					} else if (pgV3Constants.TYPE_PGMAKINGINSTRUCTIONS.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind
								+ UINavigatorUtil.getI18nString(
										"emxCPN.PDFViews.strPDFViewKind.TYPE_PGMAKINGINSTRUCTIONS",
										CONST_EMXCPN, strLanguage);
					} else if (pgV3Constants.TYPE_PGPROCESSSTANDARD.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind
								+ UINavigatorUtil.getI18nString(
										"emxCPN.PDFViews.strPDFViewKind.TYPE_PGPROCESSSTANDARD",
										CONST_EMXCPN, strLanguage);
					} else if (pgV3Constants.TYPE_AUTHORIZEDCONFIGSTAND
							.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind
								+ UINavigatorUtil.getI18nString(
										"emxCPN.PDFViews.strPDFViewKind.TYPE_AUTHORIZEDCONFIGSTAND",
										CONST_EMXCPN, strLanguage);
					} else if (pgV3Constants.TYPE_PGCOMPETITIVEPRODUCTPART
							.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind
								+ UINavigatorUtil.getI18nString(
										"emxCPN.PDFViews.strPDFViewKind.TYPE_PGCOMPETITIVEPRODUCTPART",
										CONST_EMXCPN, strLanguage);
					} else if (pgV3Constants.TYPE_PGLABORATORYINDEXSPECIFICATION
							.equals(strTechSpecType)) {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind
								+ UINavigatorUtil.getI18nString(
										"emxCPN.PDFViews.strPDFViewKind.TYPE_PGLABORATORYINDEXSPECIFICATION",
										CONST_EMXCPN, strLanguage);
					} else {
						strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind
								+ UINavigatorUtil.getI18nString("emxCPN.PDFViews.strPDFViewKind.ForAll",
										CONST_EMXCPN, strLanguage);
					}
					if(!(pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equals(strTechSpecType) || pgV3Constants.TYPE_PGARTWORK.equals(strTechSpecType))) {
						Map mAdlibTicketDetails = (Map) getAdlibTicketXMLContent(context, objectId,
								ftpInputFolder, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
								mapAdlibTicketData, strPDFViewKind);

                        // Cloud Gen-Doc start -
                        if (mAdlibTicketDetails.containsKey("checkOutFileDetailsInJSONFormat")) {
                            String checkOutFileDetailsInJSONFormat = (String) mAdlibTicketDetails.get("checkOutFileDetailsInJSONFormat");
                            mapAdlibTicketData.put("checkOutFileDetailsInJSONFormat", checkOutFileDetailsInJSONFormat);
                        }
                        // Cloud Gen-Doc end

						sbXMLTicketContent.append((String) mAdlibTicketDetails.get("AdlibTicketDetails"));
						sbGenDocFileName.append((String) mAdlibTicketDetails.get("strfileName"));
						sbGenDocFilePath.append((String) mAdlibTicketDetails.get("strTempPath"));
					}
				}
			}
		}
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements
	/**
	 * helper method to get type specific extension (header) for report. 
	 * @param String type - holds object type value
	 * @param String shortType - holds object short type name
	 * @param String assemblySubType - holds attribute value
	 * @param String extension - holds header-extension value
	 * @return String for report header extension.
	 * @throws Exception if the operation fails
	 */
	public String getFPPShippableHALBExtension(String strType, String strShortType, String strAssemblySubType, String strExtension) throws Exception {
		if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strType)) {
			if(UIUtil.isNotNullAndNotEmpty(strAssemblySubType) && pgV3Constants.SHIPPABLE_HALB.equals(strAssemblySubType)) {
				strExtension = "_" + strShortType + "_" + pgPDFViewConstants.CONS_HALB;
			}
		}
		return strExtension;
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements
	/**
	 * helper method to get type specific extension (sub-header) for report. 
	 * @param String type - holds object type value
	 * @param String shortType - holds object short type name
	 * @param String assemblySubType - holds attribute value
	 * @param String header - holds sub-header-extension value
	 * @return String for report sub-header extension.
	 * @throws Exception if the operation fails
	 */
	public String getFPPShippableHALBHeader(String strType, String strShortType, String strAssemblySubType, String strHeader) {
		if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strType)) {
			if(UIUtil.isNotNullAndNotEmpty(strAssemblySubType) && pgV3Constants.SHIPPABLE_HALB.equals(strAssemblySubType)) {
				strHeader = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.FinishedProductPartHALB.header", CONST_EMXCPN, strLanguage);
				strHeader = FrameworkUtil.findAndReplace(strHeader, strShortType, strShortType+" - "+pgPDFViewConstants.CONS_HALB);
			}
		}
		return strHeader;
	}
	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process all info view. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param Map mapAdlibTicketData - holds data of all info view
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processAllInfoView(Context context, 
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName, 
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent) throws Exception {

		//Start Code Refactoring
		//						StringList slAllInfoTypes = new StringList();
		//
		//						slAllInfoTypes.add(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGINNERPACKUNITPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGMASTERINNERPACKUNITPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGONLINEPRINTINGPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGMASTERPACKAGINGMATERIALPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGMASTERPACKAGINGASSEMBLYPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGMASTERCUSTOMERUNITPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGTRANSPORTUNITPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGCONSUMERUNITPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGPROMOTIONALITEMPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_PGMASTERCONSUMERUNITPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_FABRICATEDPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_DEVICEPRODUCTPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_RAWMATERIALPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_MASTERRAWMATERIALPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_MASTERPRODUCTPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_FORMULATIONPART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART);
		//						slAllInfoTypes.add(pgV3Constants.TYPE_SOFTWAREPART);
		//End Code Refactoring

		//variable declaration for type.
		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		//Added by DSM 2018x.5 Defect req : 33190 Start
		String strAuthoringApplication = (String) mapAdlibTicketData.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		//Added by DSM 2018x.5 Defect req : 33190 End

		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;

		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind); // Added by DSM Sogeti for
		// 2015x.5 iText modifications
		// for all views.
		String strAllInfoHeader = UINavigatorUtil.getI18nString("emxCPN.PDFViews.AllView.header",CONST_EMXCPN, strLanguage);
		mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strAllInfoHeader);
		if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)|| pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getMEPPartSelectableMap(context);
			Map selectRelatedMap = null;
			if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.SEP.header",CONST_EMXCPN, strLanguage);
				strPDFViewKindExtension = "_SEP";
				selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
						.getpgFinishedProductRelatedSelectableMap(context, false);
			} else if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.MEP.header",CONST_EMXCPN, strLanguage);
				strPDFViewKindExtension = "_MEP";
				selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getMEPRelatedSelectableMap(context, false);
			}
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);

			consolidatedMap = getHTMLAndXMLContent(context, objectId, strRelPattern.getPattern(),
					strTypePattern.getPattern(), BusinessObjectSelectableMap, selectRelatedMap,
					FIRST_LEVEL, ftpInputFolder, !ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales,
					strPDFViewKind, strContextUser, !ifType, strTechSpecType, ftpOutputFolder,
					strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)&& !("DSO".equals(strOriginatingSource))) {

			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgRawMaterialView.NotDSOheader", CONST_EMXCPN,strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getRawMaterialSelectableMap(context);

			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getRawMaterialRelatedSelectableMap(context, false);
			strPDFViewKindExtension = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.strPDFViewKind.TYPE_PGRAWMATERIAL", CONST_EMXCPN, strLanguage);
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, ifHavingBOM, !ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgMasterRawMaterial.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterRawMaterialSelectableMap(context);

			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterRawMaterialRelatedSelectableMap(context);
			strPDFViewKindExtension = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.strPDFViewKindExtension.TYPE_PGMASTERRAWMATERIAL", CONST_EMXCPN,strLanguage);
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, ifHavingBOM, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGPKGSTUDYPROTOCOL.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgPKGStudyProtocol.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgStudyProtocolSelectableMap(context);
			strPDFViewKindExtension = UINavigatorUtil.getI18nString("emxCPN.PDFViews.strPDFViewKindExtension.TYPE_PGPKGSTUDYPROTOCOL", CONST_EMXCPN,strLanguage);
			Map selectRelatedMap = null;

			consolidatedMap = getHTMLAndXMLContent(context, objectId, strRelPattern.getPattern(),
					strTypePattern.getPattern(), BusinessObjectSelectableMap, selectRelatedMap,
					FIRST_LEVEL, ftpInputFolder, false, false, false, strPDFViewKind, strContextUser,
					false, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension, false);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));

		} else if (pgV3Constants.TYPE_PGPACKINGMATERIAL.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgPackingMaterial.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGENVIRONMENTALCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMATERIALCONSTRUCTIONCHARACTERISTIC);
			strPDFViewKindExtension = UINavigatorUtil.getI18nString("emxCPN.PDFViews.strPDFViewKindExtension.TYPE_PGPACKINGMATERIAL", CONST_EMXCPN,strLanguage);
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL.equals(strTechSpecType)) {

			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgMasterPackingMaterial.header", CONST_EMXCPN,strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);

			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getPackingMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getPackingMaterialRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGENVIRONMENTALCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMATERIALCONSTRUCTIONCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind, strContextUser,
					ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgFormulatedProduct.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getFormulatedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getFormulatedProductRelatedSelectableMap(context);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, !ifHavingBOM, !ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGBASEFORMULA.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgBaseFormula.header",CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getBaseFormulaSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getBaseFormulaRelatedSelectableMap(context);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, !ifHavingBOM, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGCONSUMERDESIGNBASIS.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgConsumerDesignBasis.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getCDBSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getCDBRelatedSelectableMap(context);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKAGEQUALITYCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMATERIALCONSTRUCTIONCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGAPPROVEDSUPPLIERLIST.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgApprovedSupplierList.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getApprovedSupplierListSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getApprovedSupplierListRelatedSelectableMap(context);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					pgV3Constants.RELATIONSHIP_PGAPPROVEDSUPPLIERLISTROW,
					pgV3Constants.TYPE_PGMANUFACTURERMATERIAL, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, ifHavingBOM, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_SHAREDTABLE.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.SharedTable.header",CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getCommonPerformanceSpecificationSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getCommonPerformanceSpecificationRelatedSelectableMap(context);
			strPDFViewKindExtension = UINavigatorUtil.getI18nString("emxCPN.PDFViews.strPDFViewKindExtension.TYPE_SHAREDTABLE", CONST_EMXCPN,strLanguage);
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, ifHavingBOM, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgFinishedProduct.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
			//Start Code Refactoring
			//						} else if (slAllInfoTypes.contains(strTechSpecType)) {
			//End Code Refactoring
		} else if (pgIPMPDFViewDataSelectable_mxJPO.getAllInfoTypes().contains(strTechSpecType)) {
			if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.FinishedProductPart.header", CONST_EMXCPN, strLanguage);
				//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements - Starts
				strHeader2 = getFPPShippableHALBHeader(strTechSpecType, loadShortType(context, strTechSpecType), strAssemblyType, strHeader2);
				//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements - Ends
			} else if (pgV3Constants.TYPE_PACKAGINGMATERIALPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.PackagingMaterialPart.header", CONST_EMXCPN,strLanguage);
			} else if (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.PackagingAssemblyPart.header", CONST_EMXCPN,strLanguage);
			} else if (pgV3Constants.TYPE_RAWMATERIALPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.RawMaterialPart.header", CONST_EMXCPN, strLanguage);
			} else if (pgV3Constants.TYPE_SOFTWAREPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgSoftwarePart.header", CONST_EMXCPN, strLanguage);
			} else if (pgV3Constants.TYPE_FORMULATIONPART.equals(strTechSpecType)) {
				if (!pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(strPolicy)) {
					strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.FormulationPart.header", CONST_EMXCPN,strLanguage);
				} else {
					strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.FormulationPart.header_clone", CONST_EMXCPN,strLanguage);
				}
			} else if ((pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType)|| pgV3Constants.TYPE_DEVICEPRODUCTPART.equals(strTechSpecType))&& pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(strPolicy)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView." + strTechSpecType + ".header_clone", CONST_EMXCPN,strLanguage);
				//Added by DSM 2018x.5 Defect req : 33190 Start
			}else if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType) && "LPD".equalsIgnoreCase(strAuthoringApplication)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView." + strTechSpecType + pgPDFViewConstants.CATIA_HEADER, CONST_EMXCPN,strLanguage);
				//Added by DSM 2018x.5 Defect req : 33190 End
			} else {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView." + strTechSpecType + ".header", CONST_EMXCPN,strLanguage);
			}
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getFinishedProductPartSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			if (pgV3Constants.TYPE_PGTRANSPORTUNITPART.equals(strTechSpecType)) {
				strPDFViewKindExtension = "_TU";
			} else {
				strAbbr = loadShortType(context, strTechSpecType);
				if ((pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType)
						|| pgV3Constants.TYPE_DEVICEPRODUCTPART.equals(strTechSpecType)
						|| pgV3Constants.TYPE_FORMULATIONPART.equals(strTechSpecType))
						&& pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART
						.equalsIgnoreCase(strPolicy)) {
					strPDFViewKindExtension = "_" + strAbbr + "_Clone";
				//Added by DSM 2018x.5 Defect req : 33190 Start
				}else if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType) && "LPD".equalsIgnoreCase(strAuthoringApplication)) {
					
					strPDFViewKindExtension = "_" + strAbbr + "_" + pgPDFViewConstants.CONS_CATIA;
				//Added by DSM 2018x.5 Defect req : 33190 End
				}
				else {
					strPDFViewKindExtension = "_" + strAbbr;
					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 30957 - Starts
					strPDFViewKindExtension = getFPPShippableHALBExtension(
							strTechSpecType,       //object type 
							strAbbr,               //type short name
							strAssemblyType,       //attribute (pgAssemblyType)
							strPDFViewKindExtension//extension
							);
					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 30957 - Ends
				}
			}
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgMasterFinishedProduct.header", CONST_EMXCPN,strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getpgMasterFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgMasterFinishedProductRelatedSelectableMap(context);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strPDFViewKindExtension = "_MPS";
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.pgPackingSubassembly.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getpgPackingSubassemblySelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgPackingSubassemblyRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);

			strPDFViewKindExtension = "_PSUB";
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)
				&& "DSO".equals(strOriginatingSource)) {

			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2,
					UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.IRMS.header", CONST_EMXCPN,context.getLocale().getLanguage()));
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getFinishedProductPartSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strPDFViewKindExtension = "_IRMS";
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
					/* Modified by DSM (Req id #47506) - Start */
		else if(pgV3Constants.TYPE_STRUCTURED_ATS.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2,
					UINavigatorUtil.getI18nString("emxCPN.PDFViews.Gendoc.pgAuthorizedTemporarySpecification.header", CONST_EMXCPN,context.getLocale().getLanguage()));
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = pgV3Constants.SYMBOL_UNDERSCORE + strAbbr;

			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);

			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductRelatedSelectableMap(context, false);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getFinishedProductPartSelectableMap(context);

			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
		/* Modified by DSM (Req id #47506) - End */
		consolidatedMap = null;

	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process supplier view. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param Map mapAdlibTicketData - holds data of supplier view
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processSupplierView(Context context,
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName, 
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent) throws Exception {

		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);


		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;

		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind);
		String strSupplierHeader = UINavigatorUtil.getI18nString("emxCPN.PDFViews.SupplierView.header",
				CONST_EMXCPN, strLanguage);
		if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)
				|| pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.SEP.header",
						CONST_EMXCPN, strLanguage);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			} else if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.MEP.header",
						CONST_EMXCPN, strLanguage);
				strTypePattern.addPattern(pgV3Constants.TYPE_PGMATERIAL);
			}
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			//removed unused-variable
			consolidatedMap = getAllDataMap(context, 
					objectId, //object id
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, //relationship 
					strTypePattern.getPattern(), // type pattern
					pgIPMPDFViewDataSelectable_mxJPO.getFinishedProductPartSelectableMap(context), //basic selectables 
					pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductRelatedSelectableMap(context, false), // rel selectables
					 //level
//					ftpInputFolder,  // folder
//					true, 
					true, 
					true, 
					strPDFViewKind// view
//					strContextUser // context user
					);
			if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_SEP";
			} else if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_MEP";
			}
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap,strPDFViewKind, strContextUser)));
		}

		else if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)
				&& !(pgV3Constants.DSM_ORIGIN.equals(strOriginatingSource))) {

			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.pgRawMaterialView.NotDSOheader", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getRawMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getRawMaterialRelatedSelectableMap(context, false);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_RAW";

			//optimised
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap,strPDFViewKind, strContextUser)));

		} else if (pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.pgMasterRawMaterial.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterRawMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterRawMaterialRelatedSelectableMap(context);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_MRMS";
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap,strPDFViewKind, strContextUser)));

		} else if (pgV3Constants.TYPE_PGPACKINGMATERIAL.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.pgPackingMaterial.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGENVIRONMENTALCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMATERIALCONSTRUCTIONCHARACTERISTIC);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_PKG";
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap,strPDFViewKind, strContextUser)));
		} else if (pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.pgMasterPackingMaterial.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGENVIRONMENTALCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMATERIALCONSTRUCTIONCHARACTERISTIC);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap,
					 ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind
					);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_MPMS";

			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap,strPDFViewKind, strContextUser)));

		} else if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.pgFinishedProduct.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PART);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap,
					 ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind
					);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_FP";
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap,strPDFViewKind, strContextUser)));

		} else if (pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.pgMasterFinishedProduct.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterFinishedProductRelatedSelectableMap(context);
			strTypePattern.addPattern(pgV3Constants.TYPE_PART);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap,
					 ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind
					);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_MPS";
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap,strPDFViewKind, strContextUser)));

		} else if (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.pgPackingSubassembly.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgPackingSubassemblySelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgPackingSubassemblyRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PART);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap,
					 ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind
					);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_PSUB";
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap, strPDFViewKind, strContextUser)));

		} else if (pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART.equals(strTechSpecType)
				|| pgV3Constants.TYPE_PGPROMOTIONALITEMPART.equals(strTechSpecType)
				|| pgV3Constants.TYPE_PACKAGINGMATERIALPART.equals(strTechSpecType)
				|| pgV3Constants.TYPE_PGONLINEPRINTINGPART.equals(strTechSpecType)
				|| pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strTechSpecType)
				|| pgV3Constants.TYPE_FABRICATEDPART.equals(strTechSpecType)
				|| pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART.equals(strTechSpecType)
				|| pgV3Constants.TYPE_RAWMATERIALPART.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			strPDFViewKindExtension = DomainConstants.EMPTY_STRING;
			if (pgV3Constants.TYPE_PACKAGINGMATERIALPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView.PackagingMaterialPart.header", CONST_EMXCPN,
						strLanguage);
			} else if (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView.PackagingAssemblyPart.header", CONST_EMXCPN,
						strLanguage);
			} else if (pgV3Constants.TYPE_RAWMATERIALPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView.RawMaterialPart.header", CONST_EMXCPN, strLanguage);
			} else {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView." + strTechSpecType + ".header", CONST_EMXCPN,
						strLanguage);
			}
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFinishedProductPartSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			if (pgV3Constants.TYPE_FABRICATEDPART.equals(strTechSpecType)) {
				consolidatedMap = getHTMLAndXMLContent(context, objectId, DomainConstants.EMPTY_STRING,
						DomainConstants.EMPTY_STRING, BusinessObjectSelectableMap, selectRelatedMap,
						FIRST_LEVEL, ftpInputFolder, ifHavingBOM, ifHavingPlants,
						!ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType,
						strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
						mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
						!ifTogetPrimaryAndSecondaryOrganization);
			} else {
				consolidatedMap = getHTMLAndXMLContent(context, objectId,
						CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
						BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
						!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
						strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
						vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
						!ifTogetPrimaryAndSecondaryOrganization);
			}
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}

		else if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)
				&& (pgV3Constants.DSM_ORIGIN.equals(strOriginatingSource))) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strSupplierHeader);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2,
					UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.IRMS.header", CONST_EMXCPN,
							context.getLocale().getLanguage()));
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFinishedProductPartSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			consolidatedMap = getAllDataMap(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap,
					true, true, strPDFViewKind);
			Map mPrimaryAndSecondaryOrg = getPrimaryAndSecondaryOrganization(context, objectId,
					strTechSpecType, strPDFViewKind);
			consolidatedMap.putAll(mPrimaryAndSecondaryOrg);
			strPDFViewKind = strPDFViewKind + File.separator + strPDFViewKind + "_IRMS";
			sbHTMLWithData.append(htmlFormatter.formatTables(htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap, strPDFViewKind, strContextUser)));
		}
		consolidatedMap = null;
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process combined-master view. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param Map mapAdlibTicketData - holds data of combined-master view
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processCombinedWithMasterView(Context context,
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName, 
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent) throws Exception {

		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);


		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;

		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind);
		String strCombinedHeader = UINavigatorUtil
				.getI18nString("emxCPN.PDFViews.combinedwithmaster.header", CONST_EMXCPN, strLanguage);
		if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strCombinedHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.combinedwithmaster.pgRawMaterial.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getRawMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getRawMaterialRelatedSelectableMap(context, true);
			strRelPattern.addPattern(CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC);
			strRelPattern.addPattern(pgV3Constants.RELATIONSHIP_PGMASTER);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMASTERRAWMATERIAL);
			strPDFViewKindExtension = "_RAW";
			consolidatedMap = getHTMLAndXMLContent(context, objectId, strRelPattern.getPattern(),
					strTypePattern.getPattern(), BusinessObjectSelectableMap, selectRelatedMap,
					FIRST_LEVEL, ftpInputFolder, ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales,
					strPDFViewKind, strContextUser, ifType, strTechSpecType, ftpOutputFolder,
					strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strCombinedHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.combinedwithmaster.pgFinishedProduct.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, true);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGPACKINGMATERIAL.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strCombinedHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.combinedwithmaster.pgPackingMaterial.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getPackingMaterialRelatedSelectableMap(context, true);
			strRelPattern.addPattern(CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC);
			strRelPattern.addPattern(pgV3Constants.RELATIONSHIP_PGMASTER);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMATERIALCONSTRUCTIONCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGENVIRONMENTALCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL);
			strPDFViewKindExtension = "_PKG";
			consolidatedMap = getHTMLAndXMLContent(context, objectId, strRelPattern.getPattern(),
					strTypePattern.getPattern(), BusinessObjectSelectableMap, selectRelatedMap,
					FIRST_LEVEL, ftpInputFolder, ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales,
					strPDFViewKind, strContextUser, ifType, strTechSpecType, ftpOutputFolder,
					strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strTechSpecType)) {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strCombinedHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.combinedwithmaster.pgPackingSubassembly.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgPackingSubassemblySelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgPackingSubassemblyRelatedSelectableMap(context, true);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
		consolidatedMap = null;
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process consolidated-packaging view. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param Map mapAdlibTicketData - holds data of consolidated-packaging view
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processConsolidatedPackagingView(Context context,
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName, 
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent) throws Exception {

		Map<String, String> returnMap = new HashMap<String, String>();
		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);


		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;
		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind);
		String strConsolidatedPackagingHeader = UINavigatorUtil.getI18nString(
				"emxCPN.PDFViews.consolidatedpackaging.header", CONST_EMXCPN, strLanguage);
		mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strConsolidatedPackagingHeader);
		if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.consolidatedpackaging.pgFinishedProduct.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind, strContextUser,
					ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.consolidatedpackaging.pgMasterFinishedProduct.header",
					CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterFinishedProductRelatedSelectableMap(context);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind, strContextUser,
					ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.consolidatedpackaging.FinishedProductPart.header", CONST_EMXCPN,
					strLanguage);

			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements - Starts
			strHeader2 = getFPPShippableHALBHeader(strTechSpecType, loadShortType(context, strTechSpecType), strAssemblyType, strHeader2);
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements - Ends

			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFinishedProductPartSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;

			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 31220 - Starts
			strPDFViewKindExtension = getFPPShippableHALBExtension(strTechSpecType, strAbbr, strAssemblyType, strPDFViewKindExtension);
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 31220 - Ends

			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
		if (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.consolidatedpackaging.pgPackingSubassembly.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgPackingSubassemblySelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgPackingSubassemblyRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind, strContextUser,
					ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
		consolidatedMap = null;
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process warehouse view. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param Map mapAdlibTicketData - holds data of warehouse view
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processWarehouseView(Context context,
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName, 
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent) throws Exception {

		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);


		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;

		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind);
		String strWareHouseHeader = UINavigatorUtil.getI18nString("emxCPN.PDFViews.warehouse.header",
				CONST_EMXCPN, strLanguage);
		mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strWareHouseHeader);
		if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.consolidatedpackaging.pgFinishedProduct.header", CONST_EMXCPN,
					strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind, strContextUser,
					ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.consolidatedpackaging.pgMasterFinishedProduct.header",
					CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterFinishedProductRelatedSelectableMap(context);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind, strContextUser,
					ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.consolidatedpackaging.FinishedProductPart.header", CONST_EMXCPN,
					strLanguage);
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements - Starts
			strHeader2 = getFPPShippableHALBHeader(strTechSpecType, loadShortType(context, strTechSpecType), strAssemblyType, strHeader2);
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements - Ends

			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFinishedProductPartSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgMasterFinishedProductRelatedSelectableMap(context);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 32002 - Starts
			strPDFViewKindExtension = getFPPShippableHALBExtension(strTechSpecType, strAbbr, strAssemblyType, strPDFViewKindExtension);
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements 32002 - Ends
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
					vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
		consolidatedMap = null;
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process contract-packaging view. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param Map mapAdlibTicketData - holds data of contract-packaging view
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processContractPackagingView(Context context,
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName, 
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent,
			String[] args) throws Exception {


		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		//Added by DSM 2018x.5 Defect req : 33190 Start
		String strAuthoringApplication = (String) mapAdlibTicketData.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		//Added by DSM 2018x.5 Defect req : 33190 Start

		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;

		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind);
		String strContractpackagingHeader = UINavigatorUtil.getI18nString(
				"emxCPN.PDFViews.contractmanufacturing.header", CONST_EMXCPN, strLanguage);
		mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strContractpackagingHeader);
		if (pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equals(strTechSpecType)) {
			strContractpackagingHeader = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.contractpackaging.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strContractpackagingHeader);
			strHeader2 = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.ALLView.contractpackaging.pgFormulatedProduct.header",
					CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFormulatedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFormulatedProductRelatedSelectableMap(context);
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, ifHavingBOM, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, !ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		//Added by DSM 2018x.5 Defect  : 33190 Start
		}else if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType) && "LPD".equalsIgnoreCase(strAuthoringApplication)) {
			strContractpackagingHeader = UINavigatorUtil.getI18nString(
					"emxCPN.PDFViews.contractpackaging.header", CONST_EMXCPN, strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strContractpackagingHeader);
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView." + strTechSpecType + pgPDFViewConstants.CATIA_HEADER, CONST_EMXCPN,strLanguage);
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			//Modified/Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements - Starts
			/*BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFormulatedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFormulatedProductRelatedSelectableMap(context);*/
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getFinishedProductPartSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductRelatedSelectableMap(context, false);
			//Modified/Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements - Ends
			strAbbr = loadShortType(context, strTechSpecType);
			strPDFViewKindExtension = "_" + strAbbr + "_" + pgPDFViewConstants.CONS_CATIA;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC, BusinessObjectSelectableMap,
					selectRelatedMap, FIRST_LEVEL, ftpInputFolder, ifHavingBOM, ifHavingPlants,
					ifHavingCountryOfSales, strPDFViewKind, strContextUser, ifType, strTechSpecType,
					ftpOutputFolder, strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, !ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
		//Added by DSM 2018x.5 Defect  : 33190 End

		else if (pgV3Constants.TYPE_FORMULATIONPART.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.FOP.header",
					CONST_EMXCPN, strLanguage);
			//Start Code Refactoring
			//							mapAdlibTicketData.put(CONST_HEADER2, strHeader2);
			//							BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getFinishedProductPartSelectableMap(context);
			//							Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getpgFinishedProductRelatedSelectableMap(context, false);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			//							String strContext = context.getUser();
			//							String strUserAgent = CONST_USER_AGENT;
			//							if (strContext.equals(strUserAgent)) {
			//								ContextUtil.popContext(context);
			//								isPopContext = true;
			//							}
			//							strAbbr = loadShortType(context, strTechSpecType);
			//							strPDFViewKindExtension = "_" + strAbbr;
			//							consolidatedMap = getHTMLAndXMLContent(context, objectId,
			//									CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
			//									BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
			//									!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
			//									strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
			//									vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
			//									!ifTogetPrimaryAndSecondaryOrganization);

			consolidatedMap =getProductPartsHTMLAndXMLData(context, strHeader2, mapAdlibTicketData, strTypePattern, strPDFfileName, args);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));

			//							if (isPopContext) {
			//								ContextUtil.pushContext(context, strContext, "", "");
			//								isPopContext = false;
			//							}
			//End Code Refactoring
		} else if (pgV3Constants.TYPE_DEVICEPRODUCTPART.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.DPP.header",
					CONST_EMXCPN, strLanguage);
			//Start code Refactoring
			//							mapAdlibTicketData.put(CONST_HEADER2, strHeader2);
			//							BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getFinishedProductPartSelectableMap(context);
			//							Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getpgFinishedProductRelatedSelectableMap(context, false);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			//							String strContext = context.getUser();
			//							String strUserAgent = CONST_USER_AGENT;
			//							if (strContext.equals(strUserAgent)) {
			//								ContextUtil.popContext(context);
			//								isPopContext = true;
			//							}
			//							strAbbr = loadShortType(context, strTechSpecType);
			//							strPDFViewKindExtension = "_" + strAbbr;
			//							consolidatedMap = getHTMLAndXMLContent(context, objectId,
			//									CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
			//									BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
			//									!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
			//									strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
			//									vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
			//									!ifTogetPrimaryAndSecondaryOrganization);
			consolidatedMap =getProductPartsHTMLAndXMLData(context, strHeader2, mapAdlibTicketData, strTypePattern, strPDFfileName, args);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
			//							if (isPopContext) {
			//								ContextUtil.pushContext(context, strContext, "", "");
			//								isPopContext = false;
			//							}
			//End code Refactoring
		} else if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.APP.header",
					CONST_EMXCPN, strLanguage);
			//							mapAdlibTicketData.put(CONST_HEADER2, strHeader2);
			//							BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getFinishedProductPartSelectableMap(context);
			//							Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getpgFinishedProductRelatedSelectableMap(context, false);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			//							String strContext = context.getUser();
			//							String strUserAgent = CONST_USER_AGENT;
			//							if (strContext.equals(strUserAgent)) {
			//								ContextUtil.popContext(context);
			//								isPopContext = true;
			//							}
			//							strAbbr = loadShortType(context, strTechSpecType);
			//							strPDFViewKindExtension = "_" + strAbbr;
			//							consolidatedMap = getHTMLAndXMLContent(context, objectId,
			//									CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
			//									BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
			//									!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
			//									strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
			//									vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
			//									!ifTogetPrimaryAndSecondaryOrganization);
			consolidatedMap =getProductPartsHTMLAndXMLData(context, strHeader2, mapAdlibTicketData, strTypePattern, strPDFfileName, args);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
			//							if (isPopContext) {
			//								ContextUtil.pushContext(context, strContext, "", "");
			//								isPopContext = false;
			//							}
			//End code Refactoring
		} else if (pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equals(strTechSpecType)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.IPP.header",
					CONST_EMXCPN, strLanguage);
			//Start code Refactoring
			//							mapAdlibTicketData.put(CONST_HEADER2, strHeader2);
			//							BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getFinishedProductPartSelectableMap(context);
			//							Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
			//									.getpgFinishedProductRelatedSelectableMap(context, false);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			//							strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			//							String strContext = context.getUser();
			//							String strUserAgent = pgV3Constants.PERSON_USER_AGENT;
			//							if (strContext.equals(strUserAgent)) {
			//								ContextUtil.popContext(context);
			//								isPopContext = true;
			//							}
			//							strAbbr = loadShortType(context, strTechSpecType);
			//							strPDFViewKindExtension = "_" + strAbbr;
			//							consolidatedMap = getHTMLAndXMLContent(context, objectId,
			//									CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
			//									BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
			//									!ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
			//									strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName,
			//									vHTMLInputFiles, mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
			//									!ifTogetPrimaryAndSecondaryOrganization);
			consolidatedMap =getProductPartsHTMLAndXMLData(context, strHeader2, mapAdlibTicketData, strTypePattern, strPDFfileName, args);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
			//							if (isPopContext) {
			//								ContextUtil.pushContext(context, strContext, "", "");
			//								isPopContext = false;
			//							}
			//End code Refactoring
		}//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements - RM CM View Starts 
		else if(pgV3Constants.TYPE_RAWMATERIALPART.equals(strTechSpecType) && pgV3Constants.POLICY_EC_PART.equalsIgnoreCase(strPolicy)) {
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.RMP.header",
					CONST_EMXCPN, strLanguage);
			consolidatedMap =getProductPartsHTMLAndXMLData(context, strHeader2, mapAdlibTicketData, strTypePattern, strPDFfileName, args);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
       //Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - Start
			else if(pgV3Constants.TYPE_STRUCTURED_ATS.equals(strTechSpecType))
		{
			strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.Gendoc.pgAuthorizedTemporarySpecification.header",CONST_EMXCPN, strLanguage);
			consolidatedMap =getProductPartsHTMLAndXMLData(context, strHeader2, mapAdlibTicketData, strTypePattern, strPDFfileName, args);
			if(consolidatedMap != null) {
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
			}
		}
		//Added DSM for Requirement 47656,47667 2022x.04 Dec CW 2023 - End
		//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements - RM CM View Ends
		consolidatedMap = null;
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method to process PQR view. 
	 * @param Context context - holds Matrix context
	 * @param String strContextUser - holds context user
	 * @param String strPDFViewKind - holds view name
	 * @param String strPDFfileName - holds view pdf name
	 * @param Map mapAdlibTicketData - holds data of PQR view
	 * @param StringBuffer strHTMLWithData - holds data for html
	 * @param StringBuffer strXMLTicketContent - holds data for renderer.
	 * @return void 
	 * @throws Exception if the operation fails
	 */
	public void processPQRView(Context context,
			String strContextUser, 
			String strPDFViewKind, 
			String strPDFfileName, 
			Map<String, String> mapAdlibTicketData,
			StringBuffer sbHTMLWithData,
			StringBuffer sbXMLTicketContent) throws Exception {

		String objectId = (String)mapAdlibTicketData.get(SELECT_ID);
		String strPolicy = (String)mapAdlibTicketData.get(SELECT_POLICY);
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strOriginatingSource = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);


		Map BusinessObjectSelectableMap = null;
		Map consolidatedMap = new HashMap();
		String strHeader2 = EMPTY_STRING;
		String strPDFViewKindExtension = EMPTY_STRING;
		String strAbbr = DomainConstants.EMPTY_STRING;

		Pattern strRelPattern = new Pattern("");
		Pattern strTypePattern = new Pattern("");

		boolean ifType = true;
		boolean ifHavingBOM = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifHavingPlants = false;
		boolean ifTogetPrimaryAndSecondaryOrganization = true;

		mapAdlibTicketData.put(pgV3Constants.PDF_VIEW, strPDFViewKind);
		String strPQRHeader = UINavigatorUtil.getI18nString("emxCPN.PDFViews.PQR.header", CONST_EMXCPN,
				strLanguage);
		mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER1, strPQRHeader);
		if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)
				|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO.getMEPPartSelectableMap(context);
			Map selectRelatedMap = null;
			if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.MEP.header",
						CONST_EMXCPN, strLanguage);
				strPDFViewKindExtension = "_MEP";
				selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getMEPRelatedSelectableMap(context,
						false);
			} else if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.SEP.header",
						CONST_EMXCPN, strLanguage);
				strPDFViewKindExtension = "_SEP";
				selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getMEPRelatedSelectableMap(context,
						false);
			}
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			consolidatedMap = getHTMLAndXMLContent(context, objectId, strRelPattern.getPattern(),
					strTypePattern.getPattern(), BusinessObjectSelectableMap, selectRelatedMap,
					FIRST_LEVEL, ftpInputFolder, !ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales,
					strPDFViewKind, strContextUser, !ifType, strTechSpecType, ftpOutputFolder,
					strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPolicy,
					strPDFViewKindExtension, ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		} else {
			strAbbr = loadShortType(context, strTechSpecType);
			if (pgV3Constants.TYPE_RAWMATERIALPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView.RawMaterialPart.header", CONST_EMXCPN, strLanguage);
			} else if (pgV3Constants.TYPE_PACKAGINGMATERIALPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView.PackagingMaterialPart.header", CONST_EMXCPN,
						strLanguage);
			} else if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strTechSpecType)
					&& "DSO".equals(strOriginatingSource)) {
				strHeader2 = UINavigatorUtil.getI18nString("emxCPN.PDFViews.ALLView.IRMS.header",
						CONST_EMXCPN, strLanguage);
				strAbbr = "IRMS";
			} else if (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strTechSpecType)) {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView.PackagingAssemblyPart.header", CONST_EMXCPN,
						strLanguage);
			} else {
				strHeader2 = UINavigatorUtil.getI18nString(
						"emxCPN.PDFViews.ALLView." + strTechSpecType + ".header", CONST_EMXCPN,
						strLanguage);
			}
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);
			BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductSelectableMap(context);
			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO
					.getpgFinishedProductRelatedSelectableMap(context, false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId,
					CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, strTypePattern.getPattern(),
					BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL, ftpInputFolder,
					ifHavingBOM, ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind, strContextUser,
					ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension,
					!ifTogetPrimaryAndSecondaryOrganization);
			sbHTMLWithData.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRHTMLWITHDATA));
			sbXMLTicketContent.append((String) consolidatedMap.get(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT));
		}
		consolidatedMap = null;
	}

	//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code-refactoring)
	/**
	 * helper method get required selects for renderer. 
	 * @return StringList - list of selectables. 
	 * @throws Exception if the operation fails
	 */
	public StringList getRequiredSelectablesForRenderer() throws Exception {

		StringList slSelectables = new StringList(28);
		slSelectables.add(DomainConstants.SELECT_TYPE);
		slSelectables.add(DomainConstants.SELECT_NAME);
		slSelectables.add(DomainConstants.SELECT_REVISION);
		slSelectables.add(DomainConstants.SELECT_CURRENT);
		slSelectables.add(DomainConstants.SELECT_POLICY);
		slSelectables.add(DomainConstants.SELECT_ORIGINATOR);
		//Added by DSM for PDF views 2018x.3 Defect - 24078 : Starts
		slSelectables.add(DomainConstants.SELECT_PROJECT);
		slSelectables.add(DomainConstants.SELECT_ORGANIZATION);
		//Added by DSM for PDF views 2018x.3 Defect - 24078 : Ends
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		//Added by DSM 2018x.5 Defect req : 33190 Start
		slSelectables.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		//Added by DSM 2018x.5 Defect req : 33190 End


		slSelectables.add("attribute[" + CPNCommonConstants.ATTRIBUTE_PRODUCT_DATA_VIEW_FORMNAME + "]");
		slSelectables.add("attribute[" + CPNCommonConstants.ATTRIBUTE_PRODUCT_DATA_RENDER_LANGUAGE + "]");
		//			slSelectables.add("attribute[" + PropertyUtil.getSchemaProperty("attribute_pgIPClassification") + "]");
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);

		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGSECURITYSTATUS);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		//			slSelectables.add("attribute[" + PropertyUtil.getSchemaProperty("attribute_ApprovedDate") + "]");
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_APPROVED_DATE);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		slSelectables.add("to[" + pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION + "]");

		slSelectables.add("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.name");
		slSelectables.add("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "]");
		slSelectables.add(pgV3Constants.SELECT_FILE_NAME);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		slSelectables.add("from[" + pgV3Constants.RELATIONSHIP_DERIVED + "]");
		//Added by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Starts
		slSelectables.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGISATSSPECIFICTOIMPACTEDPARTORSPECREV);
		//Added by DSM(Sogeti)-2018x.6 for PDF Views Requirement #40493 - Ends
		return slSelectables;

	}

	/**
	 * This public method will be executed to render attributes of a tech object
	 * into a pdf file. This is a trigger method.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String  args[] holds request parameters
	 * @Param String This parameter has added to make this method overloaded
	 * @return 0 for true and 1 for false
	 * @throws Exception if the operation fails
	 * @since CPN V6R2010
	 */
	String ftpInputFolder = null;
	String ftpOutputFolder = null;
	String CPN_PROPERTIES = "emxCPN";
	Vector vHTMLInputFiles = null;
	public Map<String, String> renderPDF(Context context, String args[], Map<String, String> mAdlibDetails)
			throws Exception {

		long startTimeOfMainMethod = new Date().getTime();
		String strErrorMsg = null;

		Map<String, String> mDetailsRenderPDF = new HashMap<String, String>();
		boolean isPopContext = false;
		boolean isPushContext = false;

		String returnString = null;
		String strProductDataTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST);
		String strNonStructuredTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_NON_STRUCT_TYPE_INCLUSION_LIST);
		strNonStructuredTypes = strNonStructuredTypes.replace(",pgDSOAffectedFPPList", "");
		//String strATTFailedReason = pgV3Constants.ATTRIBUTE_PGFAILEDREASON;
		String strKind = args[1];
		String strTempFolderName = null;

		try {
			if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)
					|| context.isAssigned(pgV3Constants.ROLE_PGCONTRACTPACKER)
					|| context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER)
					|| context.isAssigned(pgV3Constants.ROLE_IPMWAREHOUSEREADER)) {
				isPushContext = true;
				//Modify Code Refactoring
				//				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, "", "");
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			}
			String strContextUser = args[2];

			if (args == null || args.length < 1 || args[0] == null || "".equals(args[0]) || "null".equals(args[0])) {
				throw new MatrixException("Not valid arguments!");
			}
			String objectId = args[0];
			strParentObjectID = objectId;
			String strPDFViewKind = args[1];
			//Modify Code Refactoring
			//			String mode = "";
			//			if (args.length > 1) {
			//				mode = args[1];
			//			}
			long startTime = System.currentTimeMillis();
			String sFileName = "";
			//Modify Code Refacoring
			//			int iResult = 1;
			String strLocale = context.getLocale().toString();
			//			i18nNow i18nObject = new i18nNow();
			//Modify Code Refactoring
			//			String renderSoftwareInstalled = i18nObject.GetString(CPN_PROPERTIES, strLocale, "emxCPN.RenderPDF");
			//			String strFTPNeeded = i18nObject.GetString(CPN_PROPERTIES, strLocale, "emxCPN.RenderPDF.iText.NoFTP");
			String renderSoftwareInstalled =EnoviaResourceBundle.getProperty(context,CPN_PROPERTIES,context.getLocale(),"emxCPN.RenderPDF");
			String strFTPNeeded = EnoviaResourceBundle.getProperty(context,CPN_PROPERTIES, context.getLocale(), "emxCPN.RenderPDF.iText.NoFTP");
			long lBeforeDataFetch = new Date().getTime();
			DomainObject domainObject = DomainObject.newInstance(context, objectId);

			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Starts
			Map<String, String> mapAdlibTicketData = domainObject.getInfo(context, getRequiredSelectablesForRenderer());
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Ends

			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Starts
			/*
			 * to-do:
			 * add the property UnStructuredTypeProperty in constants file.
			 */
			mapAdlibTicketData.put(pgPDFViewConstants.CONT_UNSTRUCTUREDTYPES, strNonStructuredTypes);
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - End

			//Modify Code Refactoring
			domainObject.close(context);
			String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
			String strTechSpecName = (String) mapAdlibTicketData.get(DomainConstants.SELECT_NAME);
			String strTechSpecRev = (String) mapAdlibTicketData.get(DomainConstants.SELECT_REVISION);
			//Modify Code Refacoring
			//			String strAssemblyType = (String) mapAdlibTicketData.get("attribute[pgAssemblyType]");
			String strAssemblyType = (String) mapAdlibTicketData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			String strOriginatingSource = (String) mapAdlibTicketData
					.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
			String sPolicy = (String) mapAdlibTicketData.get(DomainConstants.SELECT_POLICY);
			String strTempFileName = "";
			//Modify Code Refactoring
			//			StringList pgV3ConstantsForTypesStringList=new StringList();
			//			Object objDataValue = (Object) mapAdlibTicketData.get(pgV3Constants.SELECT_FILE_NAME);
			Object objDataValue = (Object) mapAdlibTicketData.get(pgV3Constants.SELECT_FILE_NAME);
			if (objDataValue instanceof String) {
				strTempFileName = (String) objDataValue;
			} else if (objDataValue instanceof StringList) {
				StringList slTempFileName = (StringList) objDataValue;

				if (null != slTempFileName && slTempFileName.size() > 0) {
					strTempFileName = (String) slTempFileName.get(0);
				}
			}
			long lAfterDataFetch = new Date().getTime();
			System.out.println("Total Time to Fetch Object Basic Details: " + (lAfterDataFetch - lBeforeDataFetch));

			if (pgV3Constants.PDFVIEW_GENDOC.equals(strKind) && ((strNonStructuredTypes.contains(strTechSpecType.trim())
					&& !strTechSpecType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL))
					|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(sPolicy)
					|| pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(sPolicy))) {
				//Modify Code Refactoring
				//				renderSoftwareInstalled = mAdlibDetails.get("pgRenderPDFGenDoc");
				//				FTP_PROTOCOL = mAdlibDetails.get("pgProtocolGenDoc");
				//				FTP_PORT = Integer.parseInt(mAdlibDetails.get("pgPortGenDoc"));
				//				FTP_HOST_NAME = mAdlibDetails.get("pgHostNameGenDoc");
				//				FTP_USER_NAME = mAdlibDetails.get("pgAdlibUserNameGenDoc");
				//				FTP_PASSWORD = mAdlibDetails.get("pgAdlibPasswordGenDoc");
				//				FTP_ROOT_FOLDER = mAdlibDetails.get("pgFTPRootFolderPathGenDoc");
				//				FTP_INPUT_FOLDER = mAdlibDetails.get("pgFTPInputFolderPathGenDoc");
				//				FTP_OUTPUT_FOLDER = mAdlibDetails.get("pgFTPOutputFolderPathGenDoc");
				renderSoftwareInstalled = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGRENDERPDFGENDOC);
				FTP_PROTOCOL = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGPROTOCOLGENDOC);
				FTP_PORT = Integer.parseInt(mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGPORTGENDOC));
				FTP_HOST_NAME = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGHOSTNAMEGENDOC);
				FTP_USER_NAME = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGADLIBUSERNAMEGENDOC);
				FTP_PASSWORD = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGADLIBPASSWORDGENDOC);
				FTP_ROOT_FOLDER = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGFTPROOTFOLDERPATHGENDOC);
				FTP_INPUT_FOLDER = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGFTPINPUTFOLDERPATHGENDOC);
				FTP_OUTPUT_FOLDER = mAdlibDetails.get(pgV3Constants.ATTRIBUTE_PGFTPOUTPUTFOLDERPATHGENDOC);

				String strSessionId = context.getSession().getSessionId();
				if (strSessionId.indexOf(":") != -1) {
					strSessionId = strSessionId.substring(0, strSessionId.indexOf(":"));
				}
				String timeStamp = Long.toString(System.currentTimeMillis());
				strTempFolderName = strSessionId + timeStamp + objectId + "_" + strKind;
				strTempFolderName = strTempFolderName.replace(':', '_');
				ftpInputFolder = FTP_INPUT_FOLDER + java.io.File.separator + strTempFolderName;
				ftpOutputFolder = FTP_OUTPUT_FOLDER + java.io.File.separator + strTempFolderName;
				strFTPNeeded = pgV3Constants.CONST_TRUE;
			}
			//Modify Code Refactoring
			//			strTechSpecName = replaceSpaces(strTechSpecName);
			strTechSpecName=strTechSpecName.replaceAll(pgV3Constants.SYMBOL_SPACE, DomainConstants.EMPTY_STRING);
			String xmlFileName = strTechSpecName + "-Rev" + strTechSpecRev + ".xml";
			String strPDFfileName = strTechSpecName + "-Rev" + strTechSpecRev + ".pdf";
			String strHTMLFileName = strTechSpecName + "-Rev" + strTechSpecRev + ".htm";
			String destFileName = strTechSpecName + "-Rev" + strTechSpecRev + ".pdf";
			StringList objectIdList = new StringList(1);
			objectIdList.addElement(objectId);
			vHTMLInputFiles = new Vector();
			vHTMLInputFiles.add(strHTMLFileName);
			sFileName = strTechSpecName + "-Rev" + strTechSpecRev + ".pdf";

			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Starts
			StringBuffer sbHTMLWithData = new StringBuffer();
			StringBuffer sbXMLTicketContent = new StringBuffer();
			StringBuffer sbGenDocFileName = new StringBuffer();
			StringBuffer sbGenDocFilePath = new StringBuffer();
			//String strGenDocFileName = "";
			//String strGenDocFilePath = "";
			//String strHTMLWithData = null;
			//String strXMLTicketContent = null;
			//Map BusinessObjectSelectableMap = null;
			//Map consolidatedMap = new HashMap();
			//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Ends

			if (renderSoftwareInstalled != null && renderSoftwareInstalled.equalsIgnoreCase(pgV3Constants.CONST_TRUE)) {
				boolean isContextPushed = false;
				boolean isPDFGenerated = true;

				FTPClient ftp = null;

				try {
					if (strFTPNeeded != null && strFTPNeeded.equalsIgnoreCase(pgV3Constants.CONST_TRUE)) {
						long lBeforeFTP1 = new Date().getTime();
						// create folders on ftp server
						ftp = new FTPClient();
						ftp.connect(FTP_HOST_NAME);
						if (!ftp.login(FTP_USER_NAME, EncryptCrypto.decryptString(FTP_PASSWORD))) {
							ftp.logout();
						}
						int reply = ftp.getReplyCode();
						if (FTPReply.isPositiveCompletion(reply)) {
							System.out.println("connnecting1......" + objectId); // Added for confirming FTP connection
							ftp.enterLocalPassiveMode();
							if (ftp.changeWorkingDirectory(FTP_INPUT_FOLDER)) {
								ftp.makeDirectory(strTempFolderName);
							}

							if (ftp.changeWorkingDirectory(FTP_OUTPUT_FOLDER)) {
								ftp.makeDirectory(strTempFolderName);
							}
							ftp.logout();
							ftp.disconnect();
							long lAfterFTP1 = new Date().getTime();
							System.out.println(" Total Time to Create Temp/Input Directory on Adlib FTP1: "
									+ (lAfterFTP1 - lBeforeFTP1));
						} else {
							ftp.disconnect();
						}
					}

					//Commented by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Starts
					/*boolean ifHavingBOM = false;
					boolean ifHavingCountryOfSales = true;
					boolean ifHavingPlants = false;
					boolean ifType = true;
					boolean ifTogetPrimaryAndSecondaryOrganization = true;
					Pattern strRelPattern = new Pattern("");
					Pattern strTypePattern = new Pattern("");
					String strHeader2 = DomainConstants.EMPTY_STRING;
					String strPDFViewKindExtension = DomainConstants.EMPTY_STRING;
					String strAbbr = DomainConstants.EMPTY_STRING;
					 */
					//Commented by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Ends

					String strLanguage = context.getLocale().getLanguage();

					String strPolicy = mapAdlibTicketData.get(DomainConstants.SELECT_POLICY);
					long lBeforeHTML = new Date().getTime();

					System.out.println("Switch case :"+strPDFViewKind);

					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Starts
					switch(strPDFViewKind.trim()) {

					case pgPDFViewConstants.CONST_ALLINFO:
						System.out.println("Load case :"+pgPDFViewConstants.CONST_ALLINFO);

						processAllInfoView(context, 
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName, 
								mapAdlibTicketData, 
								sbHTMLWithData, 
								sbXMLTicketContent);
						break;

					case pgV3Constants.PDFVIEW_GENDOC:
						System.out.println("Load case :"+pgV3Constants.PDFVIEW_GENDOC);
						processGendoc(context, 
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName,
								sbGenDocFileName, 
								sbGenDocFilePath,
								strTempFileName,
								mapAdlibTicketData,
								sbHTMLWithData,
								sbXMLTicketContent);
						break;


					case pgPDFViewConstants.CONST_SUPPLIER:
						System.out.println("Load case :"+pgPDFViewConstants.CONST_SUPPLIER);
						processSupplierView(
								context,
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName, 
								mapAdlibTicketData,
								sbHTMLWithData,
								sbXMLTicketContent);

						break;

					case pgPDFViewConstants.CONST_COMBINEDWITHMASTER:
						System.out.println("Load case :"+pgPDFViewConstants.CONST_COMBINEDWITHMASTER);
						processCombinedWithMasterView(
								context,
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName, 
								mapAdlibTicketData,
								sbHTMLWithData,
								sbXMLTicketContent);
						break;

					case pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING:
						System.out.println("Load case :"+pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING);
						processConsolidatedPackagingView(
								context,
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName, 
								mapAdlibTicketData,
								sbHTMLWithData,
								sbXMLTicketContent);

						break;

					case pgPDFViewConstants.CONST_WAREHOUSE:
						System.out.println("Load case :"+pgPDFViewConstants.CONST_WAREHOUSE);
						processWarehouseView(
								context,
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName, 
								mapAdlibTicketData,
								sbHTMLWithData,
								sbXMLTicketContent); 

						break;

					case pgPDFViewConstants.CONST_CONTRACTPACKAGING:
						System.out.println("Load case :"+pgPDFViewConstants.CONST_CONTRACTPACKAGING);
						processContractPackagingView(
								context,
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName, 
								mapAdlibTicketData,
								sbHTMLWithData,
								sbXMLTicketContent,
								args);
						break;

					case pgV3Constants.PQR_VIEW:
						System.out.println("Load case :"+pgV3Constants.PQR_VIEW);
						processPQRView(
								context,
								strContextUser, 
								strPDFViewKind, 
								strPDFfileName, 
								mapAdlibTicketData,
								sbHTMLWithData,
								sbXMLTicketContent);
						break;
					default:
						break;
						
					}
					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (code refactoring) - Ends

					//Modify Code Refactoring
					//consolidatedMap.clear();
					long lAfterHTML = new Date().getTime();
					System.out.println("Total Time to Generate HTML File: " + (lAfterHTML - lBeforeHTML));
					ftp = null;

					if (strFTPNeeded != null && strFTPNeeded.equalsIgnoreCase(pgV3Constants.CONST_TRUE)) {

						// Structured GenDoc
						if (pgV3Constants.PDFVIEW_GENDOC.equals(strKind) && !(strNonStructuredTypes.contains(strTechSpecType)
								&& !(strTechSpecType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL)))) {

							if (!((pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)
									|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy))
									&& (pgV3Constants.PDFVIEW_GENDOC.equals(strKind)))) {
								long lBeforeFTP2 = new Date().getTime();

								ftp = new FTPClient();
								ftp.connect(FTP_HOST_NAME);
								if (!ftp.login(FTP_USER_NAME, EncryptCrypto.decryptString(FTP_PASSWORD))) {
									ftp.logout();
								}
								int reply = ftp.getReplyCode();
								if (!FTPReply.isPositiveCompletion(reply)) {
									ftp.disconnect();
								}
								if (FTPReply.isPositiveCompletion(reply)) {
									System.out.println("connnecting2......" + objectId);
								}
								ftp.enterLocalPassiveMode();
								boolean bWrkDir = ftp.changeWorkingDirectory(ftpInputFolder);
								if (!bWrkDir) {
									boolean isMakeDir = ftp.makeDirectory(ftpInputFolder);
									bWrkDir = ftp.changeWorkingDirectory(ftpInputFolder);
								}
								InputStream inStreamHtmlData = new java.io.ByteArrayInputStream(sbHTMLWithData.toString().getBytes());

								ftp.setFileType(ftp.BINARY_FILE_TYPE);
								boolean Store = ftp.storeFile(strHTMLFileName, inStreamHtmlData);
								inStreamHtmlData.close();
								if (!Store) {
									String Html_gen_Error = i18nNow.getI18nString("emxCPN.PDF.HTMLGenError",
											CONST_EMXCPN, strLanguage);
									MqlUtil.mqlCommand(context, "notice " + Html_gen_Error);
								}
								long lAfterFTP2 = new Date().getTime();
								System.out.println(
										"Total Time to Copy HTML File to Adlib FTP2: " + (lAfterFTP2 - lBeforeFTP2));
							}
						}

						// Unstructured GenDoc and MEP\SEP Gendoc if content file is there
						if (pgV3Constants.PDFVIEW_GENDOC.equals(strKind)
								&& ((strNonStructuredTypes.contains(strTechSpecType)
										&& !strTechSpecType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL))
										|| (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy))
										|| (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)))
								&& !"".equalsIgnoreCase(strTempFileName) && null != strTempFileName) {
							if (sbGenDocFileName.toString() != null && "" != sbGenDocFileName.toString()) {

								ftp = new FTPClient();
								ftp.connect(FTP_HOST_NAME);
								if (!ftp.login(FTP_USER_NAME, EncryptCrypto.decryptString(FTP_PASSWORD))) {
									ftp.logout();
								}
								int reply = ftp.getReplyCode();
								if (!FTPReply.isPositiveCompletion(reply)) {
									ftp.disconnect();
								}

								if (FTPReply.isPositiveCompletion(reply)) {
									System.out.println("connnecting22......" + objectId);
								}
								ftp.enterLocalPassiveMode();
								boolean bWrkDir = ftp.changeWorkingDirectory(ftpInputFolder);

								if (!bWrkDir) {
									boolean isMakeDir = ftp.makeDirectory(ftpInputFolder);
									bWrkDir = ftp.changeWorkingDirectory(ftpInputFolder);
								}
								if (!bWrkDir) {
									strErrorMsg = "Failed to Change Working Directory of Adlib to " + ftpInputFolder;
								}
								//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
								if(pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equalsIgnoreCase(strTechSpecType) || pgV3Constants.TYPE_PGARTWORK.equals(strTechSpecType)) {
									String destPath = sbGenDocFilePath.toString()+File.separator+objectId;
									String fileName = strTechSpecName+"TempContentFile.PDF";
									String sFilePath = generateiTextPDF(context, sbHTMLWithData.toString(), objectId, strPDFfileName,
											mapAdlibTicketData);								
									fileCopy(sFilePath, destPath+File.separator+fileName);
									sbGenDocFileName.append(":"+fileName);
								}
								//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends
								StringTokenizer stateTok = new StringTokenizer(sbGenDocFileName.toString(), ":");

								while (stateTok.hasMoreTokens()) {
									String tok = (String) stateTok.nextToken();
									if (StringUtils.isBlank(strTempPath)) {
										strTempPath = sbGenDocFilePath.toString();
									}
									InputStream in = new FileInputStream(strTempPath + java.io.File.separator + objectId
											+ java.io.File.separator + tok.trim());
									ftp.setFileType(ftp.BINARY_FILE_TYPE);
									boolean Store = ftp.storeFile(tok.trim(), in);
									in.close();

								}

							}
						}

						ftp.enterLocalPassiveMode();
						long lBeforeFTP3 = new Date().getTime();
						boolean bWrkDir = ftp.changeWorkingDirectory(ftpInputFolder);
						if (!bWrkDir) {
							boolean isMakeDir = ftp.makeDirectory(ftpInputFolder);
							bWrkDir = ftp.changeWorkingDirectory(ftpInputFolder);
						}
						InputStream inStreamTicket = new java.io.ByteArrayInputStream(
								(sbXMLTicketContent.toString()).getBytes());
						ftp.setFileType(ftp.BINARY_FILE_TYPE);
						boolean Store = ftp.storeFile(xmlFileName, inStreamTicket);
						inStreamTicket.close();
						if (!Store) {
							String Xml_gen_Error = i18nNow.getI18nString("emxCPN.PDF.XMLGenError", CONST_EMXCPN,
									strLanguage);
							MqlUtil.mqlCommand(context, "notice " + Xml_gen_Error);
						}
						long startAdlibMonitorTiming = new Date().getTime();
						System.out.println("Adlib Ticket has copied in: "
								+ (startAdlibMonitorTiming - startTimeOfMainMethod) + " ---objectId---" + objectId);
						boolean isCwd = ftp.changeWorkingDirectory(ftpOutputFolder);
						if (!isCwd) {
							boolean isMakeDir = ftp.makeDirectory(ftpOutputFolder);
							bWrkDir = ftp.changeWorkingDirectory(ftpOutputFolder);
						}
						long lAfterFTP3 = new Date().getTime();
						System.out
						.println("Total Time to Copy XML ticket on Adlib FTP3 : " + (lAfterFTP3 - lBeforeFTP3));
						Date date = new Date();
						int Time_Counter = Integer.parseInt(
								i18nNow.getI18nString("emxCPN.PDF.TimeLimitCounter", CONST_EMXCPN, strLanguage));
						String PdfGeneration_ERROR = i18nNow.getI18nString("emxCPN.PDF.PDFGenError", CONST_EMXCPN,
								strLanguage);
						ftp.enterLocalPassiveMode();
						int c = 0;
						long lBeforewhile = new Date().getTime();
						while (!isFileExists(ftp, destFileName, objectId)) {
							c++;
							if (pgV3Constants.PDFVIEW_GENDOC.equals(strKind)) {
								Thread.sleep(SLEEP_INTERVAL * 100);
							}
							if (new Date().getTime() - date.getTime() >= Time_Counter) {
								MqlUtil.mqlCommand(context, "notice " + PdfGeneration_ERROR);

								strErrorMsg = PdfGeneration_ERROR;
								isPDFGenerated = false;
								break;
							}
						}
						long lAfterWhile = new Date().getTime();
						System.out.println("While Loop Ran for==========" + c + "==========Times");
						System.out.println("Total time for While Loop (Sleep Time)-->" + (lAfterWhile - lBeforewhile));
						//Modify Code Refactoring
						//						iResult = 0;
						long endAdlibMonitorTiming = new Date().getTime();
						System.out.println("Total time has taken by the Adlib-->"
								+ (endAdlibMonitorTiming - startAdlibMonitorTiming) + "----objectId---" + objectId);

						String strPDFpath = null, strTempIODirectory = null;
						System.out.println("Debug statement for defect 14112" + isPDFGenerated);
						
						try {
							if (isPDFGenerated) {
								long lBeforeCopyFile = new Date().getTime();
								String property = "java.io.tmpdir";
								strTempIODirectory = System.getProperty(property);
								strPDFpath = strTempIODirectory + java.io.File.separator + destFileName;
								ftp.enterLocalPassiveMode();
								InputStream inputStream = ftp.retrieveFileStream(destFileName);
								OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(strPDFpath));
								long cnt = com.matrixone.fcs.common.TransportUtil.transport(inputStream, outputStream,
										8 * 1024);
								File fDestination = new File(
										strTempIODirectory + java.io.File.separator + destFileName);
								if (fDestination.exists()) {
									fDestination.setReadable(true, false);
									fDestination.setExecutable(true, false);
									fDestination.setWritable(true, false);
								}
								outputStream.flush();
								outputStream.close();
								long endTime = System.currentTimeMillis();
								long lAfterCopyFile = new Date().getTime();
								;
								System.out.println("Total time to Copy PDF File from Adlib to PLM FTP4"
										+ (lAfterCopyFile - lBeforeCopyFile));
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							strErrorMsg = exception.getMessage();
						}
						String instId = "";
						if ((pgV3Constants.PDFVIEW_GENDOC.equals(strKind) && (strProductDataTypes.contains(strTechSpecType)
								|| strNonStructuredTypes.contains(strTechSpecType))) && isPDFGenerated) {
							try {
								instId = connectFPPToRenditionobj(context, objectId);
							} catch (Exception ex) {
								strErrorMsg = ex.getMessage();
								ex.printStackTrace();

							} finally {
								try {
									if (null != instId && !"".equals(instId)) {
										//Added by DSM for PDF views 2018x.3 Defect - 24078 : Starts
										String strProject = (String) mapAdlibTicketData.get(DomainConstants.SELECT_PROJECT);
										String strOrganization = (String) mapAdlibTicketData.get(DomainConstants.SELECT_ORGANIZATION);
										//Added by DSM for PDF views 2018x.3 Defect - 24078 : Ends
										DomainObject dobject = DomainObject.newInstance(context, instId);
										//Commented by DSM for PDF views 2018x.3 Defect - 24078 : Starts
										/*String strProject = EnoviaResourceBundle.getProperty(context,
												"emxCPN.RenderPDF.projectName");
										String strOrganization = EnoviaResourceBundle.getProperty(context,
												"emxCPN.RenderPDF.organizationName");*/
										//Commented by DSM for PDF views 2018x.3 Defect - 24078 : Ends
										dobject.setPrimaryOwnership(context, strProject, strOrganization);
										System.out.println("--Trying to checkin file for GenDoc--" + destFileName + "  "
												+ objectId);
										dobject.checkinFile(context, false, false, "", FORMAT_GENERIC, destFileName,
												strTempIODirectory);
									}

								} catch (Exception e) {
									strErrorMsg = e.getMessage();
									e.printStackTrace();
								} finally {
									if (null != ftp) {
										ftp.logout();
										ftp.disconnect();
										ftp = null;
									}
								}
							}

						}
						// clean the adlib input & output foldres
						// cleanAdlibFolders(context,strTempFolderName);
						returnString = strPDFpath;
					} else {
						long lBeforeiText = new Date().getTime();
						returnString = generateiTextPDF(context, sbHTMLWithData.toString(), objectId, strPDFfileName,
								mapAdlibTicketData);
						long lAfteriText = new Date().getTime();
						// Added sysout for capturing time to generate pdf via iText
						System.out.println("Total Time to iText Generator: " + (lAfteriText - lBeforeiText));
					}
				} catch (Exception ex) {
					if (pgV3Constants.PDFVIEW_GENDOC.equals(strKind) && (strProductDataTypes.contains(strTechSpecType)
							|| strNonStructuredTypes.contains(strTechSpecType))) {
						strErrorMsg = ex.getMessage();
						returnString = "1";
					} else {
						ex.printStackTrace();
						throw ex;
					}
				} catch (Throwable th) {
					th.printStackTrace();
				} finally {
					if (null != ftp) {
						ftp.logout();
						ftp.disconnect();
					}
					if (isPushContext && pgV3Constants.PERSON_USER_AGENT.equals(context.getUser())) {
						ContextUtil.popContext(context);
						isPushContext = false;
					}
				}
			}
			long endTimeOfMainMethod1 = new Date().getTime();
			System.out.println(
					"\n\n  Name :: " + strTechSpecName + "     Revision::   " + strTechSpecRev + " objectId ------->"
							+ objectId + "---Total time--->" + (endTimeOfMainMethod1 - startTimeOfMainMethod));

		} catch (Exception exception) {
			throw exception;
		} catch (Throwable throwableError) {
			throwableError.printStackTrace();
			System.out.println(throwableError.getSuppressed());
		} finally {
			if (isPushContext && pgV3Constants.PERSON_USER_AGENT.equals(context.getUser())) {
				ContextUtil.popContext(context);
				isPushContext = false;
			}
		}
		mDetailsRenderPDF.put("returnString", returnString);
		mDetailsRenderPDF.put("strErrorMsg", strErrorMsg);

		long endTimeOfMainMethod = new Date().getTime();
		System.out.println("Total time has taken-->" + (endTimeOfMainMethod - startTimeOfMainMethod));
		return mDetailsRenderPDF;

	}// End of renderPDF method

	/**
	 * 
	 * 
	 * @param context
	 * @param objectId
	 * @param strRelPattern
	 * @return
	 * @throws Exception
	 */
	private Map getManufacturingLocation(Context context, String objectId, String strRelPattern) throws Exception {
		MapList mlLocationAttribute = null;
		Map mManufacturingLocation = new HashMap();
		StringList busSelect = new StringList(6);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGHOUSENUMBER);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_ADDRESS);
		busSelect.add("attribute[" + pgV3Constants.ATTRIBUTE_CITY + "]");
		busSelect.add("attribute[" + DomainConstants.ATTRIBUTE_STATE_REGION + "]");
		busSelect.add("attribute[" + pgV3Constants.ATTRIBUTE_COUNTRY + "]");
		//Added by DSM(Sogeti)-2018x.2 for PDF Views Defect #20091 - Starts
		busSelect.add("attribute[" + pgPDFViewConstants.ATTRIBUTE_POSTALCODE + "]");
		//Added by DSM(Sogeti)-2018x.2 for PDF Views Defect #20091 - Ends
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_ORGANIZATIONID);
		try {
			DomainObject doObjectid = DomainObject.newInstance(context, objectId);
			mlLocationAttribute = doObjectid.getRelatedObjects(context, strRelPattern, pgV3Constants.TYPE_COMPANY,
					busSelect, null, true, false, (short) 1, null, null);
			if (null != mlLocationAttribute && mlLocationAttribute.size() > 0) {
				mManufacturingLocation = (Map) mlLocationAttribute.get(0);
			}
		} catch (Exception exception) {
			throw exception;
		}
		return mManufacturingLocation;
	}

	/**
	 *
	 * @param context
	 * @param objectId
	 * @return
	 */
	
	private Map getAllDataMap(Context context, String objectId, String relPattern, String typePattern,
			Map masterDataSelectable, Map relatedSelectables,					
			boolean ifHavingPlants, boolean ifHavingCountryOfSales, String strPDFViewType)
					throws Exception {
		System.out.println("Total memory (bytes): getAllDataMap " + Runtime.getRuntime().totalMemory());
		long startTime = new Date().getTime();
		Object objDataValue = DomainConstants.EMPTY_STRING;
		String strContext = DomainConstants.EMPTY_STRING;		
		String strType = DomainConstants.EMPTY_STRING;		
		String strTypes = DomainConstants.EMPTY_STRING;
		String[] argsSubs = null;
		Map mapMasterDomainObject = new HashMap<>();
		String strProductDataTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST);
		String strProducedByFormula = pgV3Constants.KEY_YES;
		try {
			DomainObject masterObj = DomainObject.newInstance(context, objectId);
			StringList slMasterSelect = (StringList) masterDataSelectable.get("busSelect");
			int iSize = slMasterSelect.size();
			StringList slObjectSelects = new StringList(7);
			slObjectSelects.addElement(DomainConstants.SELECT_TYPE);
			slObjectSelects.addElement(DomainConstants.SELECT_CURRENT);
			slObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
			slObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
			slObjectSelects.addElement(pgPDFViewConstants.CONST_PREVIOUS_ID);
			slObjectSelects.addElement( DomainConstants.SELECT_ORIGINATOR);
			slObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			slObjectSelects.addElement("from["+pgV3Constants.PGMASTER+pgPDFViewConstants.CONS_TO_ID);
			Map<String, String> mObjectInfo = masterObj.getInfo(context, slObjectSelects);
			if (pgPDFViewConstants.CONST_COMBINEDWITHMASTER.equals(strPDFViewType) && iSize > 0) {
				String strRemove;
				for (int i = 0; i < iSize; i++) {
					strRemove = slMasterSelect.get(i);
					strRemove = strRemove.replaceAll(pgPDFViewConstants.CONS_LAST, DomainConstants.EMPTY_STRING);
					slMasterSelect.remove(i);
					slMasterSelect.add(i, strRemove);
				}
			}

			BusinessObjectWithSelectList objWithSelectList = BusinessObject.getSelectBusinessObjectData(context,
					new String[] { objectId }, slMasterSelect);
			slMasterSelect.clear();
			MapList mlBusinessObjectDataList = new MapList();

			StringList slToRemoveMasterKey = new StringList();
			StringList slDataList = new StringList(2);

			//Refactor 2018x.6 start
			if (null != objWithSelectList && !objWithSelectList.isEmpty()) {	
				mlBusinessObjectDataList = getSelectableObjectDataList(context, objectId,objWithSelectList,strPDFViewType,mObjectInfo,masterObj,slToRemoveMasterKey);
			}
			mapMasterDomainObject = getAllPlatformAndChassisData(context, objectId ,mlBusinessObjectDataList, mapMasterDomainObject);
			StringList busSelect = new StringList();
			StringList relSelect = new StringList();
			if (null != relatedSelectables) {
				busSelect = (StringList) relatedSelectables.get("busSelect");
				relSelect = (StringList) relatedSelectables.get("relSelect");
			}

			if ((null != relPattern && !DomainConstants.EMPTY_STRING.equals(relPattern)) && (null != typePattern && !DomainConstants.EMPTY_STRING.equals(typePattern))) {
				MapList mlMasterRelatedObjects = masterObj.getRelatedObjects(context, relPattern, typePattern,
						busSelect, relSelect, false, true, (short) 1, "current.access[read]=='TRUE'", null, 0);
				if (null != mlMasterRelatedObjects && !mlMasterRelatedObjects.isEmpty()) {	
					//Refactor 2018x.6 Ends
					String strKey = DomainConstants.EMPTY_STRING;
					MapList mlMasterRelatedData = new MapList();
					Map<String,StringList> mpMasterData;
					Map<String,Object> mapObject;
					for (Iterator<Map<String,Object>> iterator = mlMasterRelatedObjects.iterator(); iterator.hasNext();) {
						mapObject =  iterator.next();
						for (Iterator<String> itrMapKeys = mapObject.keySet().iterator(); itrMapKeys.hasNext();) {
							strKey =  itrMapKeys.next();
							objDataValue = mapObject.get(strKey);
							slDataList = new StringList(2);
							mpMasterData = new HashMap<>();
							if (objDataValue instanceof String) {		// need Code Refactro
								slDataList.addElement((String) objDataValue);
								Collections.sort(slDataList);
								mpMasterData.put(strKey, slDataList);
							} else if (objDataValue instanceof StringList) {
								slDataList.addAll((StringList) objDataValue);
								mpMasterData.put(strKey, slDataList);
							}
							mlMasterRelatedData.add(mpMasterData);
						}
					}
					mapMasterDomainObject.putAll(consolidateRows(context, mlMasterRelatedData));
					mlMasterRelatedData.clear();
				}
				mlMasterRelatedObjects.clear();
			}
			if (pgPDFViewConstants.CONST_COMBINEDWITHMASTER.equals(strPDFViewType)) {			
					MapList mlBusinessObjectDataListMaster = new MapList();
					mlBusinessObjectDataListMaster =  getCSSMasterAttributeDetails(context, mObjectInfo.get(DomainConstants.SELECT_TYPE), slToRemoveMasterKey,masterObj);
					mapMasterDomainObject.putAll(consolidateRows(context, mlBusinessObjectDataListMaster));
					mlBusinessObjectDataListMaster.clear();
			}
			
			if (ifHavingPlants) {
				MapList mlModifiedPlants = getModifiedPlants(context, masterObj);
				if (null != mlModifiedPlants && !mlModifiedPlants.isEmpty()) {			
					mapMasterDomainObject.putAll(consolidateRows(context, mlModifiedPlants));
					mlModifiedPlants.clear();
				}
			}
			
			if (validateString(strPDFViewType) && pgPDFViewConstants.CONST_COMBINEDWITHMASTER.equals(strPDFViewType)) {
				Map mMasterDomainObject = getCombinedMasterDetails(context,masterObj,mlBusinessObjectDataList,strPDFViewType);
				mapMasterDomainObject.putAll(mMasterDomainObject);
			}
			// mlBusinessObjectDataList.clear();
			String strMasterType = mObjectInfo.get(DomainConstants.SELECT_TYPE);
			String[] args;
			if (!strProductDataTypes.contains(strMasterType) || strMasterType.equalsIgnoreCase(pgV3Constants.TYPE_PGMASTERRAWMATERIAL)) {
				Map mapMasterSpecList = getMasterSpecList(context,objectId,strPDFViewType);		
				mapMasterDomainObject.putAll(mapMasterSpecList);
			}
			
			Map<String,String> argMap ;
			if (strProductDataTypes.contains(strMasterType) && !pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equalsIgnoreCase(strMasterType)) {
				StringBuilder sRel = new StringBuilder();
				sRel.append(pgV3Constants.RELATIONSHIP_PARTSPECIFICATION );
				sRel.append(pgV3Constants.SYMBOL_COMMA);
				sRel.append(pgV3Constants.RELATIONSHIP_PGAPPROVEDSUPPLIERLIST);
				sRel.append(pgV3Constants.SYMBOL_COMMA);
				sRel.append(pgV3Constants.RELATIONSHIP_PGINHERITEDCADSPECIFICATION);
				// Modified by DSM-2015x.5.1 for PDF content issue Defect #20349 - Ends
				argMap = new HashMap<>();
				argMap.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
				argMap.put(pgPDFViewConstants.CONS_PARENTRELNAME, sRel.toString());
				argMap.put("Mode", "PDF");								
				String[] arg = JPO.packArgs(argMap);
				strContext = context.getUser();
				if (pgV3Constants.PERSON_USER_AGENT.equals(strContext) && validateString(strPDFViewType)
						&& (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)
								|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType))) {
					ContextUtil.popContext(context);
					isAllPopContext = true;
				}

				if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)
						|| context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER)) {
				//The sonar lint comment Remove this forbidden call not applicable here, as the below code is having existing requirement logic to get access.	
					ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					isAllPushContext = true;
				}

				MapList mlSpecificatins =(MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "getDocumentsForFPP", arg);
				//Refactor 2018x.6 
				//The sonar lint comment Remove this forbidden call not applicable here, as the below code is having existing requirement logic to get access.
				if (isAllPopContext && validateString(strPDFViewType) && (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)
						|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType))) {
					ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					isAllPopContext = false;
				}
				
				StringList objectSelects = new StringList(1);										
				objectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_REFERENCETYPE);
				Map<String,String> mObjectDetails = masterObj.getInfo(context, objectSelects);
				strTypes = mObjectInfo.get(DomainConstants.SELECT_TYPE);
				String strReferenceTypeAttribute = mObjectDetails.get(pgV3Constants.SELECT_ATTRIBUTE_REFERENCETYPE);
				MapList mlSubClass = new MapList();
				Map<String,String> argMapMaster = new HashMap<>();
				argMapMaster.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
				argMapMaster.put(pgPDFViewConstants.CONS_PARENTRELNAME, pgV3Constants.RELATIONSHIP_PARTSPECIFICATION);
				argMapMaster.put("Mode", "PDF");
				String[] argMaster = JPO.packArgs(argMapMaster);
				if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTypes)) {
					argMapMaster.remove("Mode");
					argMapMaster.put("Mode", DomainConstants.EMPTY_STRING);
					mlSubClass =(MapList)pgPDFViewHelper.executeMainClassMethod(context, "emxPartFamily", "getMasterSpecifications", argMaster);
				} else {
						//Modified by DSM-2018x.6 for May CW PDF Views (Defect id #47950) - Starts  
						mlSubClass =(MapList)pgPDFViewHelper.executeMainClassMethod(context, "emxPartFamily", "getConnectedMasterSpecifications", argMaster);
						//Modified by DSM-2018x.6 for May CW PDF Views (Defect id #47950) - Ends  
				}

				if (null != mlSpecificatins && !mlSpecificatins.isEmpty()) {
					MapList  mlRelatedSpecsList = getRelatedSpecData(context, mlSpecificatins, masterObj,mObjectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE));
					mapMasterDomainObject.putAll(consolidateRows(context, mlRelatedSpecsList));
					mlRelatedSpecsList.clear();
				}
				if (null != mlSubClass && !mlSubClass.isEmpty()) {
					MapList mlMasterSpecsList = getMasterSpecData(context, mlSubClass,strTypes, strReferenceTypeAttribute);
					mapMasterDomainObject.putAll(consolidateRows(context, mlMasterSpecsList));
					mlMasterSpecsList.clear();
				}	
				//Refactor 2018x.6
				if ("ROH".equals(mObjectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE))) {
					strProducedByFormula = pgV3Constants.KEY_NO;
				}
				Map<String,String> mpProducedByFormula = new HashMap<>();
				mpProducedByFormula.put("strProducedByFormula", strProducedByFormula);
				mapMasterDomainObject.putAll(mpProducedByFormula);

			}
			StringList relselectStmts = new StringList(2);
			StringList slselectStmts = new StringList(2);
			slselectStmts.addElement(DomainConstants.SELECT_ID);
			slselectStmts.addElement(DomainConstants.SELECT_NAME);
			relselectStmts.addElement(pgPDFViewConstants.CONS_FROMMID + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.id");
			relselectStmts.addElement(pgPDFViewConstants.CONS_FROMMID + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.name");
			MapList mlMasterMapList = new MapList();
			Map<String,String> mMasterMap = new HashMap<>();
			String strMasterid = null;
			String strMasterName = null;


			//Refactor 2018x.6
			if (pgV3Constants.DSM_ORIGIN.equals(mObjectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE))) {
				mlMasterMapList = masterObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM,
						pgV3Constants.TYPE_PARTFAMILY, slselectStmts, relselectStmts, true, false, (short) 1, null,
						null);
				if (!mlMasterMapList.isEmpty()) {

					mMasterMap = (Map) mlMasterMapList.get(0);
					strMasterid =  mMasterMap
							.get(pgPDFViewConstants.CONS_FROMMID + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.id");
					strMasterName =  mMasterMap
							.get(pgPDFViewConstants.CONS_FROMMID + pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE + "].torel.to.name");
				}
			} else {

				if (!strMasterType.contains("Master")) {
					mlMasterMapList = masterObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGMASTER,
							pgV3Constants.SYMBOL_STAR, slselectStmts, null, true, false, (short) 1, null, null);
				}
				if (!mlMasterMapList.isEmpty()) {
					mMasterMap = (Map) mlMasterMapList.get(0);
					strMasterid =  mMasterMap.get(DomainConstants.SELECT_ID);
					strMasterName = mMasterMap.get(DomainConstants.SELECT_NAME);
				}
			}
			
			MapList mlMasterObjectDetails = getMasterObjDetails(context, strMasterid, strMasterName, strType, strPDFViewType, argsSubs);
			if (null != mlMasterObjectDetails && !mlMasterObjectDetails.isEmpty()) {			
				mapMasterDomainObject.putAll(consolidateRows(context, mlMasterObjectDetails));
				mlMasterObjectDetails.clear();
			}
			String strPrevious = mObjectInfo.get(pgPDFViewConstants.CONST_PREVIOUS_ID);			// check move to method 

			String strObs = DomainConstants.EMPTY_STRING;
			if (UIUtil.isNotNullAndNotEmpty(strPrevious)) {	
				DomainObject prevObj = DomainObject.newInstance(context, strPrevious);
				strObs = prevObj.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);

			}
			Map<String,String> prevObject = new HashMap<>();
			prevObject.put("previousRev", strObs);
			mapMasterDomainObject.putAll(prevObject);
			Map<String,String> unPacked = new HashMap<>();		
			unPacked.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
			unPacked.put(pgPDFViewConstants.CONS_PARENTRELNAME, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT);
			args = JPO.packArgs(unPacked);
			String strOriginatingSourcecheck = mObjectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
			Map<String,String> object = new HashMap<>();
			if ((!(strProductDataTypes.contains(strMasterType)))
					|| (pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equalsIgnoreCase(strMasterType))
					|| (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(strMasterType)
							&& strOriginatingSourcecheck.contains(pgV3Constants.CSS_SOURCE))) {	
				MapList mlRefDocuments = (MapList) pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "getDocumentWithFiles", args);
				
				if (null != mlRefDocuments && !mlRefDocuments.isEmpty()) {
					object = new HashMap<>();

					for (Iterator<Map<String,String>> iterator = mlRefDocuments.iterator(); iterator.hasNext();) {
						object =  iterator.next();
						object.put("IndividualDocumentName", object.get(pgPDFViewConstants.CONS_FILENAMES));
						object.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONS_REFERENCE_DOCUMENTS);
						object.put("DocumentType", ( object.get("fileType")));
						object.put("Language",
								((String) object.get(DomainConstants.SELECT_NAME)).startsWith("LR_")
								?  object.get(DomainConstants.SELECT_REVISION)
										: DomainConstants.EMPTY_STRING);
					}
					mapMasterDomainObject.putAll(consolidateRows(context, mlRefDocuments));
					mlRefDocuments.clear();
				}
			}
			//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts 
			//Refactor 2018x.6
			if (strProductDataTypes.contains(strMasterType)
					|| pgV3Constants.TYPE_PGPKGSTUDYPROTOCOL.equals(strMasterType) || pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equals(strMasterType)) {
			//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends
				DomainObject dmObj = null;
				MapList mlRefDocuments = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "emxCPNCommonDocumentUI", pgPDFViewConstants.CONS_GETDOCUMENTS, args);
				
				if (mlRefDocuments != null && !mlRefDocuments.isEmpty()) {
					mapMasterDomainObject.putAll(consolidateRows(context, getRefDocuments(context, mlRefDocuments)));
					//Modify Code Refactoring
					mlRefDocuments.clear();
				}
			}
			argMap = new HashMap<>();
			argMap.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
			argMap.put("table", "pgIPMMaterialProducedByFormulation");
			String[] args1 = JPO.packArgs(argMap);
			MapList mlMaterialList = (MapList)pgPDFViewHelper.executeMainClassMethod(context, "pgIPMV3Reports", "getDataForATS", args1);
			if (null != mlMaterialList && !mlMaterialList.isEmpty()) {
				object = new HashMap<>();
				for (Iterator<Map<String,String>> iterator = mlMaterialList.iterator(); iterator.hasNext();) {
					object =  iterator.next();
					object.put("pgDefinesMaterialName", object.get(DomainConstants.SELECT_NAME));
					object.put(DomainConstants.SELECT_TYPE, "pgDefinesMaterial");
					object.put("pgDefinesMaterialTitle", object.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
				}
				mapMasterDomainObject.putAll(consolidateRows(context, mlMaterialList));
			}
			//Modify Code Refactoring
			mlMaterialList.clear();
			args = JPO.packArgs(unPacked);

			MapList mlListOfEnvironmentalData = (MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "pgGetEnvironmentalCharData", args);
			Map<String,String> map = null;
			if (null != mlListOfEnvironmentalData && !mlListOfEnvironmentalData.isEmpty()) {
				map = new HashMap<>();
				for (Iterator<Map<String,String>> iterator = mlListOfEnvironmentalData.iterator(); iterator.hasNext();) {
					map = iterator.next();
					strType = map.get(DomainConstants.SELECT_TYPE);

					map.put(DomainConstants.SELECT_TYPE, "pgEnvironmentalCharacteristic");
				}
				mapMasterDomainObject.putAll(consolidateRows(context, mlListOfEnvironmentalData));
			}
			//Modify Code Refactoring
			mlListOfEnvironmentalData.clear();
			MapList mlListOfMOCData = (MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "pgGetMaterialConstructionCharData", args);
			if (null != mlListOfMOCData && !mlListOfMOCData.isEmpty()) {
				map = new HashMap<>();
				for (Iterator<Map<String,String>> iterator = mlListOfMOCData.iterator(); iterator.hasNext();) {
					map =  iterator.next();
					strType = map.get(DomainConstants.SELECT_TYPE);
					map.put(DomainConstants.SELECT_TYPE, "pgMaterialConstructionCharacteristic");
				}
				mapMasterDomainObject.putAll(consolidateRows(context, mlListOfMOCData));
			}
			//Modify Code Refactoring
			mlListOfMOCData.clear();
			MapList mlPerformanceCharList = new MapList();
			MapList mlSharedTableList = new MapList();
			MapList mlPerformancespecifications = (MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "pgGetPerformanceCharData", args);
			if (null != mlPerformancespecifications && !mlPerformancespecifications.isEmpty()) {
				setSharedTableAndPerformanceCharData(context,mlPerformancespecifications,mlSharedTableList ,mlPerformanceCharList);
			}
			mapMasterDomainObject.putAll(consolidateRows(context, mlSharedTableList));
			//Modify Code Refactoring
			mlSharedTableList.clear();
			mapMasterDomainObject.putAll(consolidateRows(context, mlPerformanceCharList));
			//Modify Code Refactoring
			mlPerformanceCharList.clear();
			if (ifHavingCountryOfSales) {
				Map mapCountryOfSale = getCountryOfSale(context,masterObj,mObjectInfo);
				mapMasterDomainObject.putAll(mapCountryOfSale);
			}
			Map<String,String> unPackedOw = new HashMap<>();		
			unPackedOw.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
			unPackedOw.put(pgPDFViewConstants.CONS_PARENTRELNAME, pgPDFViewConstants.RELATIONSHIP_REFERENCEDOCUMENT);
			args = JPO.packArgs(unPackedOw);
			MapList mlGetOwnershipTable = (MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CONS_PGIPMTABLESJPO, "getOwnershipTableData", args);
			if (null != mlGetOwnershipTable && !mlGetOwnershipTable.isEmpty()) {
				setOwnershipTableData(mlGetOwnershipTable);
				mapMasterDomainObject.putAll(consolidateRows(context, mlGetOwnershipTable));
			}
			//Modify Code Refactoring
			mlGetOwnershipTable.clear();
			MapList mlSharinglist = getSharingAttributeTable(context, args);
			if (null != mlSharinglist && !mlSharinglist.isEmpty()) {			
				mapMasterDomainObject.putAll(consolidateRows(context, mlSharinglist));
				mlSharinglist.clear();
			}
			Map<String,String> unPackedSe = new HashMap<>();		// clear
			unPackedSe.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
			unPackedSe.put(pgPDFViewConstants.CONS_PARENTRELNAME, pgPDFViewConstants.RELATIONSHIP_REFERENCEDOCUMENT);
			Map<String,Map<String,String>> paramMap = new HashMap<>();
			paramMap = (Map) JPO.unpackArgs(args);
			paramMap.put("paramMap", unPackedSe);
			paramMap.put(pgPDFViewConstants.CONS_REQUESTMAP, unPackedSe);
			args = JPO.packArgs(paramMap);
			String strSecurityCategory = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "getSecurityCategory", args);
			mapMasterDomainObject.putAll(getMapfor("strSecurityCategory", strSecurityCategory));

			String strProjectSecurityGroup = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "getpgProjectSecurityGroupValue", args);
			mapMasterDomainObject.putAll(getMapfor("strProjectSecurityGroup", strProjectSecurityGroup));
			
			String strEnvironmentalCharacteristic = getEnvPackingLevel(context, objectId);
			Map<String,String> packingLevelMap = new HashMap<>();
			packingLevelMap.put("strEnvironmentalCharacteristic", strEnvironmentalCharacteristic);
			mapMasterDomainObject.putAll(packingLevelMap);

			strContext = context.getUser();

			boolean isPushContextt = false;
			if (!(pgV3Constants.PERSON_USER_AGENT.equals(strContext))) {
			//The sonar lint comment Remove this forbidden call not applicable here, as the below code is having existing requirement logic to get access.	
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isPushContextt = true;
			}

			String strApproverForOwnership = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "getApproversOnOwnershipPage", args);
			mapMasterDomainObject.putAll(getMapfor("strApproverForCBD", strApproverForOwnership));
			if (isPushContextt) {
				ContextUtil.popContext(context);
				isPushContextt = false;
			}

			getWND(context, args, mapMasterDomainObject) ;
			
			strContext = context.getUser();
			if (!(pgV3Constants.PERSON_USER_AGENT.equals(strContext))) {
			//The sonar lint comment Remove this forbidden call not applicable here, as the below code is having existing requirement logic to get access.	
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isPushContextt = true;
			}

			String strApprovers = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_EMXCPNPRODUCTDATA, "getApprovers", args);
			strApprovers = StringEscapeUtils.escapeJava(strApprovers);
			mapMasterDomainObject.putAll(getMapfor("strApprover", strApprovers));

			if (isPushContextt) {
				ContextUtil.popContext(context);
				isPushContextt = false;
			}

			String strSharingRegions = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_EMXCPNPRODUCTDATA, "getSharingRegions", args);

			mapMasterDomainObject.putAll(getMapfor("strSharingRegion", strSharingRegions));
			
			//Modify Code Refactoring
			String strMasterId = mObjectInfo.get("from["+pgV3Constants.PGMASTER+pgPDFViewConstants.CONS_TO_ID);
			Map<String,String> paramMapMaster = new HashMap<>();
			paramMapMaster.put(pgPDFViewConstants.CONS_OBJECTID, strMasterId);
			Map<String,Map<String,String>> mpReqMaster = new HashMap<>();
			mpReqMaster.put(pgPDFViewConstants.CONS_REQUESTMAP, paramMapMaster);
			String[] argsMaster = JPO.packArgs(mpReqMaster);
			String strMasterSharingRegions = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_EMXCPNPRODUCTDATA, "getSharingRegions", argsMaster);
			mapMasterDomainObject.putAll(getMapfor("strMasterSharingRegion", strMasterSharingRegions));
			
			String strClass = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "getSecurityClassification", args);
			mapMasterDomainObject.putAll(getMapfor("strClassification", strClass));

			Map<String,String> reqMap = new HashMap<>();
			reqMap.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
			Map<String,Map<String,String>> paramMapLast = new HashMap<>();
			paramMapLast.put(pgPDFViewConstants.CONS_REQUESTMAP, reqMap);
			String[] argsLast = JPO.packArgs(paramMapLast);
			String strLastUpdateUser = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_EMXCPNPRODUCTDATA, "getLastUpdatedUserForOwnership", argsLast);
			mapMasterDomainObject.putAll(getMapfor("strLastUpdateUser", strLastUpdateUser));

			HashMap<String,String> hm=new HashMap<>();
			hm.put("strOriginator", mObjectInfo.get(DomainConstants.SELECT_ORIGINATOR));
			String[] args2= JPO.packArgs(hm);


			String strOriginator = (String)pgPDFViewHelper.executeIntermediatorClassMethod(context, "getUserName", args2);
			mapMasterDomainObject.putAll(getMapfor("strOriginatorName", strOriginator));
			
			String strAssemblyType = mObjectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			String masterType = mObjectInfo.get(DomainConstants.SELECT_TYPE);
			if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(masterType)
					|| pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equalsIgnoreCase(masterType)
					|| pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(masterType)) {
				if (strAssemblyType.equals(DomainConstants.EMPTY_STRING)) {
					if (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(masterType)) {
						mapMasterDomainObject.put(pgPDFViewConstants.CONS_IPSASSEMBLYTYPE, "PSUB");
					} else {
						mapMasterDomainObject.put(pgPDFViewConstants.CONS_IPSASSEMBLYTYPE, "Finished Product");
					}
				} else {
					mapMasterDomainObject.put(pgPDFViewConstants.CONS_IPSASSEMBLYTYPE, strAssemblyType);
				}
			}

			MapList mlsupplierTraderInfoList = getSupplierTraderInfoList(context, args);
			
			if (null != mlsupplierTraderInfoList && !mlsupplierTraderInfoList.isEmpty()) { 
				mapMasterDomainObject.putAll(consolidateRows(context, mlsupplierTraderInfoList));
				mlsupplierTraderInfoList.clear();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Adding as per new requirement to show all information to external users -
			// Starts
			if (isAllPopContext) {
			//The sonar lint comment Remove this forbidden call not applicable here, as the below code is having existing requirement logic to get access.	
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isAllPopContext = false;
			}
			// Adding as per new requirement to show all information to external users -
			// Ends
			if (isAllPushContext) {
				ContextUtil.popContext(context);
				isAllPushContext = false;
			}
			
		}
		long endTime;
		if (mapMasterDomainObject.containsValue(pgV3Constants.DENIED)) {
			Map<String,String> mpFilteredMaster = new HashMap<>();
			Iterator<Map.Entry<String,String>> it = mapMasterDomainObject.entrySet().iterator();
			Map.Entry<String,String> pair;
			String strValue;
			while (it.hasNext()) {
				pair =  it.next();
				strValue =  pair.getValue();
				if (pgV3Constants.DENIED.equalsIgnoreCase(strValue)) {
					mpFilteredMaster.put(pair.getKey(), pgPDFViewConstants.CONST_NO_ACCESS);
				} else {
					mpFilteredMaster.put(pair.getKey(), strValue);
				}
				it.remove(); // avoids a ConcurrentModificationException
			}
			endTime = new Date().getTime();
			System.out.println("Total Time has taken by the getAllDataMap Method is-->" + (endTime - startTime));
			System.out.println("Free memory (bytes): getAllDataMap " + Runtime.getRuntime().freeMemory());
			return mpFilteredMaster;
		} else {
			endTime = new Date().getTime();
			System.out.println("Total Time has taken by the getAllDataMap Method is-->" + (endTime - startTime));
			System.out.println("Free memory (bytes): getAllDataMap " + Runtime.getRuntime().freeMemory());
			return mapMasterDomainObject;
		}
	}

	

	/**
	 * Method to get  Platform And Chassis Value
	 * @param context - matrix context
	 * @param String  -  Object Id
	 * @param HashMap - Settings
	 * @return String  - Platform And Chassis Value 
	 * @since DSM 2018x.5 Requirement
	 */
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements
	private String getDisplayAllPlatformAndChassisValue(Context context, String objectId, HashMap settings) throws Exception {
		HashMap requestMap  = new HashMap();
		HashMap fieldMap  = new HashMap();
		
		
		fieldMap.put("settings",settings);
		
		requestMap.put("objectId",objectId);
		requestMap.put("mode","view");
		
		HashMap programMap =new HashMap();
		programMap.put("requestMap", requestMap);
		programMap.put("fieldMap", fieldMap);
		String[] args=JPO.packArgs(programMap);
		
		String strPlatformChasisValue =(String)pgPDFViewHelper.executeMainClassMethod(context, "emxCommonDocument", "displayAllPlatformAndChassisValue",args );
		strPlatformChasisValue = strPlatformChasisValue.replaceAll("\\|", ",");
		return strPlatformChasisValue;
	}

	/**
	 *
	 * @param context
	 * @param mapMasterDomainObject
	 * @return
	 */
	private Map getpackingUnitTablevalues(Context context, Map mapMasterDomainObject) {
		int i = 0;

		StringList slkeys = new StringList(10);
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGUNITOFMEASURESYSTEM + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGGTIN + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGDEPTH + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGWIDTH + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGHEIGHT + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGDIMENSIONUNITOFMEASURE + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGGROSSWEIGHT + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGGROSSWEIGHTUNITOFMEASURE + "]");
		slkeys.add(
				"pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGCONSUMERUNITSPERPACKINGUNIT + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGNETWEIGHTOFPRODUCTINCONSUMERUNIT
				+ "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGNETWEIGHTUNITOFMEASURE + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGCUBEEFFICIENCY + "]");
		slkeys.add("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGCSSTYPE + "]");

		String strKey = null;
		String strValue = null;

		for (Iterator iterator = slkeys.iterator(); iterator.hasNext();) {
			strKey = (String) iterator.next();
			strValue = (String) mapMasterDomainObject.get(strKey);
			if ("~#~~#~".equals(strValue)) {
				i++;
			}
		}
		if (i == 12) {
			mapMasterDomainObject
			.put("pgPackingUnitCharacteristic_attribute[" + pgV3Constants.ATTRIBUTE_PGCSSTYPE + "]", "~#~~#~");
		}
		return mapMasterDomainObject;
	}

	/**
	 *
	 * @param context
	 * @param objType
	 * @return String
	 */
	public StringList getType(Context context, Object objType) {
		String strReturnType = "";
		StringList slList = new StringList(1);
		if (null != objType) {
			if (objType instanceof String) {
				strReturnType = (String) objType;
				slList.add(strReturnType);
			} else if (objType instanceof StringList) {
				StringList listObject = (StringList) objType;
				slList.addAll(listObject);
			}
		}
		return slList;
	}

	/**
	 *
	 * @param context
	 * @param obj
	 * @return String
	 */
	public String getString(Context context, Object obj) {
		String strReturn = "";
		if (null != obj) {
			if (obj instanceof String) {
				strReturn = (String) obj;
			} else if (obj instanceof StringList) {
				StringList listObject = (StringList) obj;
				listObject.sort();
				int listSize = listObject.size();

				String strTestMethod = DomainConstants.EMPTY_STRING;

				for (int j = 0; j < listSize; j++) {
					strTestMethod = (String) listObject.get(j);
					if (j == (listSize - 1)) {
						strReturn += strTestMethod;
					} else {
						strReturn += strTestMethod + "<br/>";
					}
				}
			}
		}
		return strReturn;
	}

	/**
	 *
	 * @param context
	 * @param objectId
	 * @param ftpInputFolder
	 * @return
	 * @throws Exception
	 */
	private String processImage(Context context, String objectId, String ftpInputFolder) throws Exception {
		String imageSrc = "";
		DomainObject domObj = DomainObject.newInstance(context, objectId);
		Pattern relPattern = new Pattern("");
		relPattern.addPattern(
				PropertyUtil.getSchemaProperty(context, CommonDocument.SYMBOLIC_relationship_ReferenceDocument));
		StringList typeSelects = new StringList(1);
		typeSelects.add(CommonDocument.SELECT_ID);
		StringList relSelects = new StringList(1);
		relSelects.add(CommonDocument.SELECT_RELATIONSHIP_ID);

		StringList slSelect = new StringList(2);
		slSelect.add(DomainConstants.SELECT_NAME);
		slSelect.add(DomainConstants.SELECT_REVISION);
		Map mapData = domObj.getInfo(context, slSelect);
		String rev = (String) mapData.get(DomainConstants.SELECT_REVISION);
		String name = (String) mapData.get(DomainConstants.SELECT_NAME);

		String docName = "IMG_" + name + "." + rev;
		String objectWhere = "name==" + docName;
		MapList docList = domObj.getRelatedObjects(context, relPattern.getPattern(), "*", typeSelects, relSelects,
				false, true, (short) 1, objectWhere, null, null, null, null);
		if (docList.isEmpty()) {
			return "";
		}
		String id = (String) ((Hashtable) docList.get(0)).get("id");

		// Labels through property files
		matrix.db.File imageFile = null;

		String fileName = "";
		String folderpath = ftpInputFolder + java.io.File.separator + "images";

		DomainObject imgObj = DomainObject.newInstance(context, id);
		FileList list = imgObj.getFiles(context);
		Iterator<matrix.db.File> imageFilesItr = list.iterator();

		if (imageFilesItr.hasNext()) {
			imageFile = imageFilesItr.next();
			fileName = imageFile.getName();
			try {
				imgObj.checkoutFile(context, false, "", fileName, folderpath);
			} catch (Exception ex) {
				System.out.println("Error in checking out image file::");
				ex.printStackTrace();
			}
		}
		if (!list.isEmpty() && (!fileName.isEmpty())) {
			imageSrc = folderpath + java.io.File.separator + fileName;
		}
		return imageSrc;
	}

	/**
	 *
	 * @param context
	 * @param mlListToConsolidate
	 * @return
	 */
	private Map consolidateRows(Context context, List mlListToConsolidate) throws Exception {
		/* Total memory currently in use by the JVM */
		System.out.println("Total memory (bytes): consolidateRows " + Runtime.getRuntime().totalMemory());

		Map processedMap = new HashMap();
		int rows = 0;
		StringList slValueList = null;
		String strValue = null;
		Map tempMap = null;
		Object typeValue = null;
		String strPolicyValue = null;
		String key = null;
		Object objValue = null;
		String strListValue = null;
		StringList slDistributionNames = null;
		String strNewValue = null;
		StringList slValues = null;

		String prcValue = DomainConstants.EMPTY_STRING;
		DomainObject doObj = null;
		StringBuilder sbProcessChar = null;
		Object policyValue = null;
		StringList dateStringList=new StringList();
		final String strSpecTypesKey = "SpecificationName,SpecificationSAPDescription,SpecificationDisplayType,SpecificationSubType,SpecificationState";
		StringList slSpecKey = FrameworkUtil.split(strSpecTypesKey, ",");

		Map Argsmap = new HashMap();

		String[] args = JPO.packArgs(Argsmap);

		//		pgIPMProductData_mxJPO ipmProduct = new pgIPMProductData_mxJPO(context, args);
		try {
			if (null != mlListToConsolidate && mlListToConsolidate.size() > 0) {
				for (Iterator iterator = mlListToConsolidate.iterator(); iterator.hasNext(); rows++) {
					tempMap = (Map) iterator.next();
					if (!tempMap.isEmpty()) {
						typeValue = tempMap.get(DomainConstants.SELECT_TYPE);
						policyValue = tempMap.get(DomainConstants.SELECT_POLICY);

						if (null != policyValue) {
							strPolicyValue = getStringValue(policyValue);
							if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicyValue)) {
								typeValue = pgV3Constants.STRING_MANUFACTUREREQUIVALENTPART;
							} else if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicyValue)) {
								typeValue = pgV3Constants.STRING_SUPPLIEREQUIVALENTPART;
							}
						}

						// iterating temp Map
						for (Iterator itr = tempMap.keySet().iterator(); itr.hasNext();) {
							key = (String) itr.next();
							objValue = tempMap.get(key);
							if (objValue instanceof StringList) {
								slValueList = (StringList) objValue;
								strListValue = convertToString(slValueList);
								if (pgV3Constants.CONST_TRUE.equalsIgnoreCase(strListValue)
										|| pgPDFViewConstants.CONST_FALSE.equalsIgnoreCase(strListValue)) {
									strListValue = convertToYesNo(strListValue);
								}

								if (key.equals("from[" + pgV3Constants.RELATIONSHIP_EBOM + "].frommid["
										+ pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]")) {
									strListValue = convertToYesNo(strListValue);
								}
								if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE)
										|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE)
										|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED)) {
									strListValue = strPlantUsage(strListValue);
								}
								if ((null != strListValue) && (!"".equals(strListValue))
										&& (!"null".equals(strListValue))) {

									if (key.equals("modified")
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGUNARCHIVEDATE)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE)
											|| key.equals("to[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_RELEASE_DATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_EFFECTIVITYDATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_EXPIRATION_DATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_PGARCHIVEDATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_PGUNARCHIVEDATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.to[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.from[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.modified")
											|| key.equals("originated")
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGPREVIOUSREVISIONOBSOLETEDATE)
											|| key.equals(
													"attribute[" + pgV3Constants.ATTRIBUTE_PGSTUDYLAUNCHDATE + "]")) {

										strListValue = getFormattedDate(strListValue);
									} else if (key.equals(
											"attribute[" + pgV3Constants.ATTRIBUTE_PGELECTRONICDISTRIBUTION + "]")
											|| key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGCOCREATORS + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_PGCOCREATORS
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute["
													+ pgV3Constants.ATTRIBUTE_PGELECTRONICDISTRIBUTION + "]")) {
										slDistributionNames = new StringList(1);
										if (strListValue.contains("|")) {
											slValues = FrameworkUtil.split(strListValue, "|");
											if (null != slValues && slValues.size() > 0) {
												int slLength = slValues.size();
												String str = null;
												for (int i = 0; i < slLength; i++) {
													str = (String) slValues.get(i);
													//Modified by DSM-2018x.6 for PDF Views (Defect id #37870) - Starts
													slDistributionNames.add(str.trim());
													//Modified by DSM-2018x.6 for PDF Views (Defect id #37870) - Ends
												}
											}
											slDistributionNames.sort();
											strListValue = FrameworkUtil.join(slDistributionNames, "|");
										} else {
											//Modified by DSM-2018x.6 for PDF Views (Defect id #37870) - Starts
											strListValue = strListValue.trim();
											//Modified by DSM-2018x.6 for PDF Views (Defect id #37870) - Ends
										}
									} else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.from[" + pgV3Constants.RELATIONSHIP_CHARACTERISTIC
													+ "].to.attribute[" + pgV3Constants.ATTRIBUTE_PGCSSTYPE + "]")) {
										strListValue = getFormatedValue(context, strListValue);
									}

									else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRAND)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRANDINFORMATION)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute["
													+ pgV3Constants.ATTRIBUTE_PGBRANDINFORMATION + "]")) {
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Starts
										String arBrandInfo[] = new String[] { strParentObjectID, "|" };
										if (key.equals(
												"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.attribute["
														+ pgV3Constants.ATTRIBUTE_PGBRANDINFORMATION + "]")) {
											String sMasterId=null;
											Object objValueTemp = tempMap.get(
													"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.id");
											if (objValueTemp instanceof StringList) {
												sMasterId = (String) ((StringList) objValueTemp).get(0);
											} else {
												sMasterId = (String) objValueTemp;
											}
											arBrandInfo[0]=sMasterId;
											//strListValue = ipmProduct.getBrandInformationValue(context, arBrandInfo);
										}
										strListValue = (String)pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getBrandInformationValue", arBrandInfo);
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Ends
										// [DRJ] : Made below change as per code review process
										strListValue = strListValue.replace("\\\\|", "<br/>");

									} else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGCLASSIFICATION)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_PGCLASSIFICATION
													+ "]")) {
										strListValue = strListValue.format(Locale.ENGLISH, "%s", strListValue);
										if ("".equals(strListValue) || "null".equals(strListValue)
												|| null == strListValue) {
											strListValue = getRestrictionType(context, (String) typeValue, CONST_EMXCPN,
													strLanguage);
										}
									} else if (key.equals("Sharing_PLMCategory")
											|| key.equals("MasterSharing_PLMCategory")) {
										strListValue = getPLMSecurityCategory(context, strListValue);
									} else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALFUNCTION)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute["
													+ pgV3Constants.ATTRIBUTE_PGMATERIALFUNCTION + "]")) {
										strListValue = getSortedValue(context, strListValue);
									} else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute["
													+ pgV3Constants.ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE + "]")) {
										strListValue = getSortedValue(context, strListValue);
									} else if (key.equals("type") || key.equals(pgPDFViewConstants.SELECT_ATTRIBUTE_IMPACTED_TYPE)) {

										strListValue = UINavigatorUtil.getAdminI18NString("Type", strListValue,
												context.getSession().getLanguage());
									}
								}
								String strTypeValue = null;
								if (typeValue instanceof StringList) {
									strTypeValue = convertToString((StringList) typeValue);
								} else {
									strTypeValue = (String) typeValue;
								}

								if (processedMap.containsKey(strTypeValue + "_" + key)) {

									prcValue = (String) processedMap.get(strTypeValue + "_" + key);
									sbProcessChar = new StringBuilder();
									if (UIUtil.isNotNullAndNotEmpty(strListValue)) {
										sbProcessChar.append(prcValue).append(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
										prcValue = replaceSpecialCharacters(strListValue);
										sbProcessChar.append(prcValue);

									} else {
										sbProcessChar.append(prcValue);
									}
									processedMap.put(strTypeValue + "_" + key, sbProcessChar.toString());
								} else {
									strListValue = replaceSpecialCharacters(strListValue);

									if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strTypeValue)
											&& (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRANDINFORMATION)
													|| key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]"))
											&& UIUtil.isNotNullAndNotEmpty(strListValue)) {
										strListValue = strListValue.replaceAll("~#~", "");
										if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRANDINFORMATION)) {
											processedMap.put(
													strTypeValue + "_attribute["
															+ pgV3Constants.ATTRIBUTE_PGBRANDINFORMATION + "]",
															strListValue);
										}
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Starts
										if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRAND)) {
											processedMap.put(strTypeValue + "_"+key,strListValue);
										}
										/*if (key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]")) {
											doObj = DomainObject.newInstance(context, strParentObjectID);
											String strBrandAttr = doObj.getAttributeValue(context,
													pgV3Constants.ATTRIBUTE_PGBRAND);
											processedMap.put(strTypeValue + "_attribute["
													+ pgV3Constants.ATTRIBUTE_PGBRAND + "]", strBrandAttr);
										}*/
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Ends
									} else if (key
											.equals("from[" + pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE + "].to.name")
											&& UIUtil.isNotNullAndNotEmpty(strListValue)) {
										strListValue = strListValue.replaceAll("~#~", ",");
										processedMap.put(strTypeValue + "_from["
												+ pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE + "].to.name",
												strListValue);
										//Added by DSM-2018x.5 for PDF Views (Defect id #33162) - Start
									} else if(key.equals(("from["+pgV3Constants.RELATIONSHIP_PGMASTER+"].to.last."+pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVECOMMENT))) {
										
										strListValue = strListValue.replaceAll("~#~", "");
										processedMap.put(strTypeValue + "_" + key, strListValue);
										
										//Added by DSM-2018x.5 for PDF Views (Defect id #33162) - End
									}//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #34470 : Starts
									else if(pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART.equals(strTypeValue)
											&& (key.equals(pgPDFViewConstants.SELECT_ATTRIBUTE_PGMATERIALRESTRICTIONCOMMENT))) {
										
										strListValue = strListValue.replaceAll("~#~", "");
										processedMap.put(strTypeValue + "_" + key, strListValue);
										//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #34470 : Ends
									}//Added by DSM(IRM)-2018x.5 for Study Protocol PDF Views (Defect #35543) - Starts
									else if(pgV3Constants.TYPE_PGPKGSTUDYPROTOCOL.equalsIgnoreCase(strTypeValue) && 
										(key.equals("from["+pgPDFViewConstants.RELATIONSHIP_PGSTUDYPROTOCOLTOPANELISTTYPE+"].to.name") || 
										key.equals("from["+pgPDFViewConstants.RELATIONSHIP_PGSTUDYPROTOCOLTORACE+"].to.name") || 
										key.equals("from[" + pgPDFViewConstants.RELATIONSHIP_PGSTUDYPROTOCOLTOPLIPANELISTSUPERVISION + "].to.name") || key.equals("from["+pgPDFViewConstants.RELATIONSHIP_PG_SPTODATAMERGE+"].to.name") || key.equals("from[" + pgPDFViewConstants.RELATIONSHIP_PG_SPTOATTACHMENTFORMAT + "].to.name") || key.equals("from[" + pgPDFViewConstants.RELATIONSHIP_PG_SPTOSTUDYLOCATIONS + "].to.name") || key.equals("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTODATACOLLECTIONMETHODS+"].to.name"))
										&& UIUtil.isNotNullAndNotEmpty(strListValue)){
										strListValue = strListValue.replaceAll("~#~", "|");
										strListValue = strListValue.substring(0, strListValue.length()-1); 
										processedMap.put(strTypeValue + "_" + key, strListValue);
									}//Added by DSM(IRM)-2018x.5 for Study Protocol PDF Views (Defect #35543) - Ends
									else {
										// Modified for PNGUPGRADE FD04 Obsolete Comment field showing wrong value in
										// PDF START
										if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVECOMMENT)) {
											strListValue = strListValue.replaceAll("~#~", "");
										}
										// Modified for PNGUPGRADE FD04 Obsolete Comment field showing wrong value in
										// PDF END
										processedMap.put(strTypeValue + "_" + key, strListValue);
									}
								}
							} else if (objValue instanceof String) {
								strValue = (String) objValue;
								if (pgV3Constants.CONST_TRUE.equalsIgnoreCase(strValue) || pgPDFViewConstants.CONST_FALSE.equalsIgnoreCase(strValue)) {
									strValue = convertToYesNo(strValue);
								}
								if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE)
										|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE)
										|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED)) {
									strValue = strPlantUsage(strValue);
								}
								if (key.equals("from[" + pgV3Constants.RELATIONSHIP_EBOM + "].frommid["
										+ pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]")) {
									strValue = convertToYesNo(strValue);
								}
								if (null != strValue && !"".equals(strValue) && !"null".equals(strValue)) {
									if (key.equals("modified")
											|| key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGUNARCHIVEDATE + "]")
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE)
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE)
											|| key.equals("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE + "]")
											|| key.equals("to[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_RELEASE_DATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_EFFECTIVITYDATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_EXPIRATION_DATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_PGARCHIVEDATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_PGUNARCHIVEDATE
													+ "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.to[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.from[" + pgV3Constants.RELATIONSHIP_PGSUPERSEDES
													+ "].attribute[" + pgV3Constants.ATTRIBUTE_PGSUPERSEDESONDATE + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.modified")
											|| key.equals("originated") || key.equals("attribute["
													+ pgV3Constants.ATTRIBUTE_PGPREVIOUSREVISIONOBSOLETEDATE + "]")){
										strValue = getFormattedDate(strValue);
									}
									else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.from[" + pgV3Constants.RELATIONSHIP_CHARACTERISTIC
													+ "].to.attribute[" + pgV3Constants.ATTRIBUTE_PGCSSTYPE + "]")) {
										strValue = getFormatedValue(context, strValue);
									} else if (key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]")
											|| key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRANDINFORMATION)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute["
													+ pgV3Constants.ATTRIBUTE_PGBRANDINFORMATION + "]")) {
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Starts
										String arBrandInfo[] = new String[] { strParentObjectID, "|" };
										if (key.equals(
												"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.attribute["
														+ pgV3Constants.ATTRIBUTE_PGBRANDINFORMATION + "]")) {
											String sMasterId=null;
											Object objValueTemp = tempMap.get(
													"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.id");
											if (objValueTemp instanceof StringList) {
												sMasterId = (String) ((StringList) objValueTemp).get(0);
											} else {
												sMasterId = (String) objValueTemp;
											}										
											arBrandInfo[0]=sMasterId;
											//strValue = ipmProduct.getBrandInformationValue(context, arBrandInfo);
										}
										strValue = (String)pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getBrandInformationValue", arBrandInfo);
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Ends
										strValue = strValue.replaceAll("\\\\|", "<br/>");
										// Do not assign strValue to other variable. Else replaceAll does not work
										// correctly
									} else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGCLASSIFICATION)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute[" + pgV3Constants.ATTRIBUTE_PGCLASSIFICATION
													+ "]")) {
										strValue = strValue.format(Locale.ENGLISH, "%s", strValue);
										if ("".equals(strValue) || "null".equals(strValue) || null == strValue) {
											strValue = getRestrictionType(context, (String) typeValue, CONST_EMXCPN,
													strLanguage);
										}
									} else if (key.equals("Sharing_PLMCategory")
											|| key.equals("MasterSharing_PLMCategory")) {
										strValue = getPLMSecurityCategory(context, strValue);
									} else if (key
											.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGMATERIALFUNCTION + "]")
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute["
													+ pgV3Constants.ATTRIBUTE_PGMATERIALFUNCTION + "]")) {
										strValue = getSortedValue(context, strValue);
									} else if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE)
											|| key.equals("from[" + pgV3Constants.RELATIONSHIP_PGMASTER
													+ "].to.last.attribute["
													+ pgV3Constants.ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE + "]")) {
										strValue = getSortedValue(context, strValue);
									} else if (key.equals("type")) {

										strValue = UINavigatorUtil.getAdminI18NString("Type", strValue,
												context.getSession().getLanguage());
									}
								}
								if (processedMap.containsKey(typeValue + "_" + key)
										&& !((pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(typeValue)
												&& (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRANDINFORMATION)
														&& UIUtil.isNotNullAndNotEmpty(strValue)))
												|| key.equals("type"))) {
									prcValue = (String) processedMap.get(typeValue + "_" + key);
									sbProcessChar = new StringBuilder();

									sbProcessChar.append(prcValue).append(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
									prcValue = replaceSpecialCharacters(strValue);
									sbProcessChar.append(prcValue);

									processedMap.put(typeValue + "_" + key, sbProcessChar.toString());

								} else {
									strValue = replaceSpecialCharacters(strValue);
									if (slSpecKey.contains(key)) {
										processedMap.put("Product Data" + "_" + key, strValue);
									} else if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(typeValue)
											&& (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRANDINFORMATION)
													|| key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]"))
											&& UIUtil.isNotNullAndNotEmpty(strValue)) {
										if (key.equals(pgV3Constants.SELECT_ATTRIBUTE_PGBRANDINFORMATION)) {
											processedMap.put(
													typeValue + "_attribute["
															+ pgV3Constants.ATTRIBUTE_PGBRANDINFORMATION + "]",
															strValue);
										}
										if (key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]")) {
											processedMap.put(
													typeValue + "_attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]",
													strValue);
										}

									} else {
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Starts
										if (key.equals("attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]")) {
											if(UIUtil.isNotNullAndNotEmpty(strValue)) {
												processedMap.put(typeValue + "_attribute[" + pgV3Constants.ATTRIBUTE_PGBRAND + "]",strValue);
											}
										} else{
											processedMap.put(typeValue + "_" + key, strValue);
										}
										//Modified by DSM-2018x.3 for PDF Views (Defect id #29842) - Ends
									}

								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/* Total amount of free memory available to the JVM */
		System.out.println("Free memory (bytes): consolidateRows " + Runtime.getRuntime().freeMemory());

		return processedMap;
	}

	private String getStringValue(Object objectValue) {
		String strValue = "";
		if (null != objectValue) {
			if (objectValue instanceof StringList) {
				strValue = convertToString((StringList) objectValue);
			} else if (objectValue instanceof String) {
				strValue = (String) objectValue;
			}
		}
		return strValue;
	}

	/**
	 *
	 * @param slValueList
	 * @return
	 */
	private String convertToString(StringList slValueList) {
		StringBuffer strBuffer = new StringBuffer();
		for (int i = 0; i < slValueList.size(); i++) {
			strBuffer.append((String) slValueList.get(i));
			if (slValueList.size() > 1) {
				strBuffer.append(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
			}
		}
		return strBuffer.toString();
	}

	/**
	 *
	 * @param context
	 * @param strCategory
	 * @return
	 */
	public String getPLMSecurityCategory(Context context, String strCategory) {
		if (validateString(strCategory)) {

			StringTokenizer stCategory = new StringTokenizer(strCategory, ",");
			StringList slTempList = new StringList(1);
			String strFinalCategory = "";

			while (stCategory.hasMoreTokens()) {
				String strTempCategory = (String) stCategory.nextToken();
				if (!slTempList.contains(strTempCategory)) {
					slTempList.addElement(strTempCategory);

					if (strFinalCategory.length() <= 0) {
						strFinalCategory = strTempCategory;
					} else {
						strFinalCategory = strFinalCategory + "," + strTempCategory;
					}
				}
			}
			return strFinalCategory;
		} else {
			return "";
		}
	}

	/**
	 *
	 * @param context             - User Context
	 * @param strObjectId         - Object Id
	 * @param sInPath             - Adlib Input folder path
	 * @param sOutPath            - Adlib Output folder path
	 * @param strOutFileName      - Output file name
	 * @param vStrInputFiles      - Input files to be appended to Adlib ticket
	 * @param mapObjectHeaderData - Object details
	 * @param strPDFViewType      - PDF View
	 * @return Adlib ticket
	 * @throws Exception
	 */
	//Modified Method by DSM(Sogeti)-2018x.5 for PDF Views Requirements    
	public Map<String, String> getAdlibTicketXMLContent(Context context, String objectId, String sInPath,
			String sOutPath, String strOutFileName, Vector vStrInputFiles, Map mapObjectHeaderData,
			String strPDFViewType) throws Exception {
		String FTP_DTD_PATH = "";
		Map<String, String> adlibeTicketDetails = new HashMap<String, String>();
		try {
		boolean isTextRightUpdate = true;
		StringBuffer sbXMLContent = new StringBuffer();

		final String BLANK_STRING = " ";
		sInPath = sInPath.replace(java.io.File.separator, "/");
		sOutPath = sOutPath.replace(java.io.File.separator, "/");
		strPDFViewType = strPDFViewType.substring(strPDFViewType.indexOf(java.io.File.separator) + 1);
		strPDFViewType = strPDFViewType.substring(0, strPDFViewType.indexOf("_"));
		String strFullInputFolderPath = FTP_ROOT_FOLDER + sInPath;
		String strFullOutputFolderPath = FTP_ROOT_FOLDER + sOutPath;
		String strObjectName = (String) mapObjectHeaderData.get(DomainConstants.SELECT_NAME);
		String strObjectOriginator = (String) mapObjectHeaderData.get(DomainConstants.SELECT_ORIGINATOR);
		String strObjectRevision = (String) mapObjectHeaderData.get(DomainConstants.SELECT_REVISION);
		String strObjectCurrentState = (String) mapObjectHeaderData.get(DomainConstants.SELECT_CURRENT);
		String strObjectType = (String) mapObjectHeaderData.get(DomainConstants.SELECT_TYPE);
		String strOriginatingSource = (String) mapObjectHeaderData
				.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		String strObjectPolicy = (String) mapObjectHeaderData.get(DomainConstants.SELECT_POLICY);
		String pgIPClassification = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		String strObjectStatus = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		String strObjectSecurityStatus = pgIPClassification.format(Locale.ENGLISH, "%s", pgIPClassification);
		String strHeader1 = (String) mapObjectHeaderData.get(pgPDFViewConstants.CONST_HEADER1);
		String strHeader2 = (String) mapObjectHeaderData.get(pgPDFViewConstants.CONST_HEADER2);
		String strHeader3 = (String) mapObjectHeaderData.get(pgPDFViewConstants.CONST_HEADER3);
		String strHavingMaster = (String) mapObjectHeaderData.get("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "]");
		String strMaster = (String) mapObjectHeaderData
				.get("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.name");
		String strAssemblyType = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		String strProductDataTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST);
		String strNonStructuredTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_NON_STRUCT_TYPE_INCLUSION_LIST);
		strNonStructuredTypes = strNonStructuredTypes.replace(",pgDSOAffectedFPPList", "");

		// Added for Upgrade 2018.0 start
		String strObjectCurrentDisplay = EnoviaResourceBundle.getStateI18NString(context, strObjectPolicy,
				strObjectCurrentState, context.getLocale().getLanguage());
		if (BusinessUtil.isNotNullOrEmpty(strObjectCurrentDisplay))
			strObjectCurrentDisplay = strObjectCurrentState;
		// Added for Upgrade 2018.0 End

		if (UIUtil.isNullOrEmpty(strHeader3)) {
			strHeader3 = "";
		}

		String strArchiveDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		if (validateString(strArchiveDate)) {
			strArchiveDate = getFormattedDate(strArchiveDate);
		}
		String strEffectiveDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		strEffectiveDate = getFormattedDate(strEffectiveDate);
		String strExpiryDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		strExpiryDate = getFormattedDate(strExpiryDate);
		String strReleaseDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		strReleaseDate = getFormattedDate(strReleaseDate);
		String strApprovedDate = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_APPROVED_DATE);
		strApprovedDate = getFormattedDate(strApprovedDate);

		String strObjectID = (String) mapObjectHeaderData.get(DomainConstants.SELECT_ID);
		Map hmRequestMap = new HashMap();
		Map hmArgMap = new HashMap();
		hmArgMap.put("objectId", strObjectID);
		hmRequestMap.put("requestMap", hmArgMap);

		String strHasATS = (String) JPO.invoke(context, "pgDSOCPNProductData", null, "displayhasATSHeaderAttribute",
				JPO.packArgs(hmRequestMap), String.class);
		String strIsATS = (String) JPO.invoke(context, "pgIPMProductData", null, "displayIsATSHeaderAttribute",
				JPO.packArgs(hmRequestMap), String.class);

		String strSAPTitle = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		strSAPTitle = strSAPTitle.replaceAll("[&]", "&amp;");
		strSAPTitle = strSAPTitle.replaceAll("[<]", "&lt;");
		strSAPTitle = strSAPTitle.replaceAll("[>]", "&gt;");
		strSAPTitle = strSAPTitle.replaceAll("[\"]", "&quot;");
		strSAPTitle = strSAPTitle.replaceAll("[\']", "&#39;");

		strSAPTitle = strSAPTitle.replaceAll("\u00D7", "&#215;");
		String strSAPType = (String) mapObjectHeaderData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		strSAPType = getSAPTypeForPDF(strPDFViewType, strSAPType, strObjectType, strProductDataTypes,
				strNonStructuredTypes, strOriginatingSource);

		MapList genDocList = new MapList();
		StringBuffer sbXMLSubContent = new StringBuffer();
		int nSize = 0;
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc - Starts
		if(pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGARTWORK.equals(strObjectType)) {
			String sFile = strObjectName+"TempContentFile.PDF";
			sbXMLSubContent.append(pgPDFViewConstants.CONST_INPUT_TAG_START +  sFile + pgPDFViewConstants.CONST_FOLDER
					+ strFullInputFolderPath + pgPDFViewConstants.CONST_INPUT_TAG_END);
		}
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc - Ends
		if (pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType) && ((strNonStructuredTypes.contains(strObjectType)
				&& !(strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL)))
				|| (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strObjectPolicy))
				|| (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equals(strObjectPolicy)))) {
			genDocList = appendedGenDocListForNonStructuredType(context, objectId);
		}

		if (genDocList.size() > 0) {
			nSize = genDocList.size();

                // Cloud-GenDoc start - applied generic String
                Map<String, String> genDocMap = new HashMap();
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;
                // Cloud-GenDoc end-

			String strfileName = "";
			String strSourceFolder = "";
			StringBuffer sbFileName = new StringBuffer();
			String strFileTempPath = "";
			for (Iterator iterator = genDocList.iterator(); iterator.hasNext();) {
                    // Cloud-GenDoc start - applied generic String
                    genDocMap = (Map<String, String>) iterator.next();
                    jsonObject = new JSONObject(genDocMap);
                    // below keys are not required for cloud
                    jsonObject.remove("Footer");
                    jsonObject.remove("strTempPath");
                    jsonArray.put(jsonObject);
                    // Cloud-GenDoc end

				strfileName = (String) genDocMap.get("fileName");
				strFileTempPath = (String) genDocMap.get("strTempPath");
				strSourceFolder = (String) genDocMap.get("path");
				sbXMLSubContent.append("<JOB:DOCINPUT FILENAME=\"" + strfileName + "\" FOLDER=\""
						+ strFullInputFolderPath + "\" />\n");
				if (sbFileName.length() > 0) {
					sbFileName.append(":");
				}
				sbFileName.append(strfileName);
				adlibeTicketDetails.put("strTempPath", strFileTempPath);
			}
			adlibeTicketDetails.put("strfileName", sbFileName.toString());
                adlibeTicketDetails.put("checkOutFileDetailsInJSONFormat", jsonArray.toString());

		}
		Map<String, String> mapFooterStamp = null;

		mapFooterStamp = getFooterStampings(context, strObjectType, strObjectCurrentState, strObjectStatus,
				strPDFViewType, strEffectiveDate, strArchiveDate, strObjectRevision, strObjectSecurityStatus);

		String strProductDataObjectName = padRight(strObjectName, 12);
		String strProductDataObjectRevision = padRight(strObjectRevision, 5);
		strObjectName = padRight(strObjectName, 25);
		strObjectOriginator = padRight(strObjectOriginator, 25);
		strObjectRevision = padRight(strObjectRevision, 25);
		strObjectCurrentState = padRight(strObjectCurrentState, 34);
		if ("".equals(strEffectiveDate) || "".equals(strExpiryDate) || "".equals(strReleaseDate)
				|| "".equals(strApprovedDate)) {
			strEffectiveDate = padRight(strEffectiveDate, 19);
			strExpiryDate = padRight(strExpiryDate, 19);
			strApprovedDate = padRight(strApprovedDate, 29);
			strReleaseDate = padRight(strReleaseDate, 18) + ".";
		}
		// ... And so forth for all the header requirements

		sbXMLContent.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n");
		if (!pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType)) {
			sbXMLContent.append("<?AdlibExpress applanguage=\"USA\" appversion=\"")
			.append(EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.OtherViews.appversion"))
			.append("\" dtdversion=\"")
			.append(EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.OtherViews.dtdversion"))
			.append("\" ?>\n");
			sbXMLContent.append("<!DOCTYPE JOBS SYSTEM \"" + FTP_DTD_PATH + "\" >\n");
		} else {
			sbXMLContent.append("<?AdlibExpress applanguage=\"USA\" appversion=\"")
			.append(EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.GenDoc.appversion"))
			.append("\" dtdversion=\"")
			.append(EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.GenDoc.dtdversion"))
			.append("\" ?>\n");
			sbXMLContent.append("<!DOCTYPE JOBS SYSTEM \"" + FTP_DTD_PATH + "\" >\n");
		}

		sbXMLContent.append("<!-- The First 3 Lines are the header of an XML file, all XML files must have one\n");
		sbXMLContent.append("there should be no text before it -->\n");
		sbXMLContent.append(
				"<JOBS xmlns:JOBS=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:JOB=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		sbXMLContent.append("<JOB>\n");
		sbXMLContent.append(
				"<!-- DOCINPUTS, this is where you specify the file you wish to convert and its location -->\n");
		sbXMLContent.append("<JOB:DOCINPUTS>\n");

		if (!(pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType) && ((strNonStructuredTypes.contains(strObjectType)
				&& !(strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL)))
				|| pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy)
				|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy)))) {

			int iFileSize = vStrInputFiles.size();
			for (int i = 0; i < iFileSize; i++) {
				sbXMLContent.append("<JOB:DOCINPUT FILENAME=\"").append(vStrInputFiles.get(i)).append("\" FOLDER=\"")
				.append(strFullInputFolderPath).append("\" />\n");
			}
		}

		if (sbXMLSubContent.length() > 1) {
			sbXMLContent.append(sbXMLSubContent.toString());
		}

		sbXMLContent.append("</JOB:DOCINPUTS>\n");
		sbXMLContent.append(
				"<!-- DOCOUTPUTS, this is where you specify the file name and location of the converted file -->\n");
		sbXMLContent.append("<JOB:DOCOUTPUTS>\n");
		sbXMLContent.append("<JOB:DOCOUTPUT FILENAME=\"" + strOutFileName + "\" FOLDER=\"" + strFullOutputFolderPath
				+ "\" DOCTYPE=\"PDF\" />\n");
		sbXMLContent.append("</JOB:DOCOUTPUTS>\n");

		sbXMLContent.append("<JOB:SCRIPTS ENABLED=\"No\">\n");
		sbXMLContent.append("</JOB:SCRIPTS>\n");

		sbXMLContent.append("<JOB:SETTINGS>\n");
		sbXMLContent.append("<JOB:FOOTERS ENABLED=\"Yes\">\n");

		if (!(pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType))) {

			sbXMLContent.append("<JOB:FOOTER ENABLED=\"Yes\"\n");
			sbXMLContent.append("FONTCOLOR=\"").append(mapFooterStamp.get("FONTCOLOR")).append("\"\n");
			sbXMLContent.append("MARGINLEFT=\"").append(mapFooterStamp.get("MARGINLEFT")).append("\"\n");
			sbXMLContent.append("MARGINRIGHT=\"").append(mapFooterStamp.get("MARGINRIGHT")).append("\"\n");
			sbXMLContent.append("MARGINVERTICAL=\"").append(mapFooterStamp.get("MARGINVERTICAL")).append("\"\n");
			sbXMLContent.append("LAYER=\"").append(mapFooterStamp.get("LAYER")).append("\"\n");
			sbXMLContent.append("PAGES=\"1\"\n");
			sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"\n");
			if (strProductDataTypes.contains(strObjectType)) {
				sbXMLContent.append("FONTNAME=\"Times-Italic\"\n");
				sbXMLContent.append("FONTSIZE=\"10\"\n");
				if (pgV3Constants.STATE_OBSOLETE.equals(strObjectCurrentState.trim())) {
					sbXMLContent.append("TEXTCENTER=\"").append(mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER))
					.append("  Page &amp;[Page] of &amp;[Pages]\"/>\n");
				} else if (pgV3Constants.STATE_RELEASE.equals(strObjectCurrentState.trim())) {
					sbXMLContent.append("TEXTCENTER=\"").append(mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER))
					.append("  Page &amp;[Page] of &amp;[Pages]\"/>\n");
				} else {
					if (pgV3Constants.STATE_PRELIMINARY.equals(strObjectCurrentState.trim())) {
						String strStamp = mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER);
						strStamp = strStamp.replaceAll(" P&amp;G PRELIMINARY", "P&amp;G PRE-RELEASED");
						sbXMLContent.append("TEXTCENTER=\"").append(strStamp)
						.append("  Page &amp;[Page] of &amp;[Pages]\"/>\n");
					} else {
						String strStamp = mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER);
						strStamp = strStamp.replaceAll("PLM View Created:", "P&amp;G PRE-RELEASED Printed");
						sbXMLContent.append("TEXTCENTER=\"").append(strStamp)
						.append("  Page &amp;[Page] of &amp;[Pages]\"/>\n");
					}
				}
			} else {

				sbXMLContent.append("FONTNAME=\"").append(mapFooterStamp.get("FONTNAME")).append("\"\n");
				sbXMLContent.append("FONTSIZE=\"").append(mapFooterStamp.get("FONTSIZE")).append("\"\n");
				sbXMLContent.append("TEXTCENTER=\"").append(mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER))
				.append("  Page &amp;[Page] of &amp;[Pages]\"/>\n");
			}

			// Non-GenDoc items footer setting for page 2 and so on
			sbXMLContent.append("<JOB:FOOTER ENABLED=\"Yes\"\n");
			if (strProductDataTypes.contains(strObjectType)) {
				sbXMLContent.append("FONTNAME=\"Times-Italic\"\n");
				sbXMLContent.append("FONTSIZE=\"10\"\n");
				sbXMLContent.append("FONTCOLOR=\"" + mapFooterStamp.get("FONTCOLOR") + "\"\n");

				sbXMLContent.append("LAYER=\"" + mapFooterStamp.get("LAYER") + "\"\n");
				sbXMLContent.append("MARGINLEFT=\"" + mapFooterStamp.get("MARGINLEFT") + "\"\n");
				sbXMLContent.append("MARGINRIGHT=\"" + mapFooterStamp.get("MARGINRIGHT") + "\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"" + mapFooterStamp.get("MARGINVERTICAL") + "\"\n");
				sbXMLContent.append("PAGES=\"DOC1[2-]\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"\n");

				if (pgV3Constants.STATE_OBSOLETE.equals(strObjectCurrentState.trim())) {
					sbXMLContent.append("TEXTCENTER=\"" + mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER)
					+ "  Page &amp;[Page] of &amp;[Pages]\"/>\n");
				} else if (pgV3Constants.STATE_RELEASE.equals(strObjectCurrentState.trim())) {

					sbXMLContent.append("TEXTCENTER=\"" + mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER)
					+ "  Page &amp;[Page] of &amp;[Pages]\"/>\n");
				} else {
					if (pgV3Constants.STATE_PRELIMINARY.equals(strObjectCurrentState.trim())) {
						String strStamp = mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER);
						strStamp = strStamp.replaceAll(" P&amp;G PRELIMINARY", "P&amp;G PRE-RELEASED");
						sbXMLContent.append("TEXTCENTER=\"" + strStamp + "  Page &amp;[Page] of &amp;[Pages]\"/>\n");
					} else {
						String strStamp = mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER);
						strStamp = strStamp.replaceAll("PLM View Created:", "P&amp;G PRE-RELEASED Printed");
						sbXMLContent.append("TEXTCENTER=\"" + strStamp + "  Page &amp;[Page] of &amp;[Pages]\"/>\n");
					}
				}
			} else {
				sbXMLContent.append("FONTNAME=\"" + mapFooterStamp.get("FONTNAME") + "\"\n");
				sbXMLContent.append("FONTSIZE=\"" + mapFooterStamp.get("FONTSIZE") + "\"\n");
				sbXMLContent.append("FONTCOLOR=\"" + mapFooterStamp.get("FONTCOLOR") + "\"\n");
				sbXMLContent.append("LAYER=\"" + mapFooterStamp.get("LAYER") + "\"\n");
				sbXMLContent.append("MARGINLEFT=\"" + mapFooterStamp.get("MARGINLEFT") + "\"\n");
				sbXMLContent.append("MARGINRIGHT=\"" + mapFooterStamp.get("MARGINRIGHT") + "\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"" + mapFooterStamp.get("MARGINVERTICAL") + "\"\n");
				sbXMLContent.append("PAGES=\"DOC1[2-]\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"\n");
				sbXMLContent.append("TEXTCENTER=\"" + mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER)
				+ "  Page &amp;[Page] of &amp;[Pages]\"/>\n");
			}

			// Append footer stamping for connected documents if any
			for (int nCount = 2; nCount < nSize + 2; nCount++) {
				Map mapFooter = (Map) genDocList.get(nCount - 2);
				String strFooter = (String) mapFooter.get("Footer");
				sbXMLContent.append("<JOB:FOOTER ENABLED=\"Yes\"\n");
				if (strProductDataTypes.contains(strObjectType)) {
					sbXMLContent.append("FONTNAME=\"Times-Italic\"\n");
					sbXMLContent.append("FONTSIZE=\"10\"\n");
					sbXMLContent.append("FONTCOLOR=\"0\"\n");
					sbXMLContent.append("LAYER=\"Foreground\"\n");
					sbXMLContent.append("MARGINLEFT=\"0.25\"\n");
					sbXMLContent.append("MARGINRIGHT=\"0.25\"\n");
					sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
					sbXMLContent.append("OPACITY=\"80\"\n");
					sbXMLContent.append("ORIENTATION=\"Page\"\n");
					sbXMLContent.append("PAGES=\"DOC" + nCount + "\"\n");
					sbXMLContent.append("PAGENUMBERINGMODE=\"OriginalDocuments\"\n");

					if (pgV3Constants.STATE_OBSOLETE.equals(strObjectCurrentState.trim())) {
						sbXMLContent.append("TEXTCENTER=\"" + mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER)
						+ "  Page &amp;[Page] of &amp;[Pages]\"/>\n");
					} else if (pgV3Constants.STATE_RELEASE.equals(strObjectCurrentState.trim())) {
						sbXMLContent.append("TEXTCENTER=\"" + mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER) + "\"/>\n");
					} else {
						if (pgV3Constants.STATE_PRELIMINARY.equals(strObjectCurrentState.trim())) {
							String strStamp = mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER);
							strStamp = strStamp.replaceAll(" P&amp;G PRELIMINARY", "P&amp;G PRE-RELEASED");
							sbXMLContent.append("TEXTCENTER=\"" + strStamp + "\"/>\n");
						} else {
							String strStamp = mapFooterStamp.get(pgPDFViewConstants.CONST_TEXTCENTER);
							strStamp = strStamp.replaceAll("PLM View Created:", "P&amp;G PRE-RELEASED Printed");
							sbXMLContent.append("TEXTCENTER=\"" + strStamp + "\"/>\n");
						}
					}
				} else if (!(pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType))) {
					sbXMLContent.append("FONTNAME=\"Helvetica-Bold\"\n");
					sbXMLContent.append("FONTSIZE=\"16\"\n");
					sbXMLContent.append("FONTCOLOR=\"0\"\n");
					sbXMLContent.append("LAYER=\"Foreground\"\n");
					sbXMLContent.append("MARGINLEFT=\"0.25\"\n");
					sbXMLContent.append("MARGINRIGHT=\"0.25\"\n");
					sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
					sbXMLContent.append("OPACITY=\"80\"\n");
					sbXMLContent.append("ORIENTATION=\"Page\"\n");
					sbXMLContent.append("PAGES=\"DOC" + nCount + "\"\n");
					sbXMLContent.append("PAGENUMBERINGMODE=\"OriginalDocuments\"\n");
					sbXMLContent.append("TEXTCENTER=\"" + strFooter + "Page &amp;[Page] of &amp;[Pages]" + "\"/>\n");
				}
			}
		}
		sbXMLContent.append("</JOB:FOOTERS>\n");

		if (!(pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType) && (strNonStructuredTypes.contains(strObjectType)
				&& !(strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL))))) {

			sbXMLContent.append("<JOB:HEADERS ENABLED=\"Yes\">\n");
			if (!(pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType))) {
				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Roman\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
				sbXMLContent.append("TEXTCENTER=\"The Procter &amp; Gamble Company - " + strHeader1 + "\"\n");
				if ((Boolean.parseBoolean(strHavingMaster))
						&& ((pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strObjectType))
								|| (pgV3Constants.TYPE_PGPACKINGMATERIAL.equals(strObjectType))
								|| (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strObjectType))
								|| ((pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType))
										&& strOriginatingSource.contains(pgV3Constants.CSS_SOURCE)))) {
					sbXMLContent.append("TEXTRIGHT=\"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;Is ATS: "
							+ padRight(strIsATS, 55) + "&lt;CR&gt;Has ATS: " + padRight(strHasATS, 15)
							+ "           Master: " + strMaster + "\"\n");
					isTextRightUpdate = false;
				}

				if (((pgPDFViewConstants.CONST_ALLINFO.equals(strPDFViewType)) || (pgPDFViewConstants.CONST_WAREHOUSE.equals(strPDFViewType))
						|| (pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType))
						|| (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)) || (pgPDFViewConstants.CONST_CONTRACTPACKAGING.equals(strPDFViewType))
						|| (pgV3Constants.PQR_VIEW.equals(strPDFViewType)))
						&& (strProductDataTypes.contains(strObjectType))
						&& !(pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strObjectType))
						&& !(pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType)
								&& strOriginatingSource.contains(pgV3Constants.CSS_SOURCE))) {

					sbXMLContent.append("TEXTLEFT=\"" + strObjectSecurityStatus + "&lt;CR&gt;&lt;CR&gt;Name:"
							+ BLANK_STRING + strObjectName + "          Revision:" + BLANK_STRING + strObjectRevision
							+ "           State:" + BLANK_STRING + strObjectCurrentDisplay + "\"\n");

					if (!((pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy))
							|| (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy))
							|| (pgV3Constants.PQR_VIEW.equals(strPDFViewType)))
							|| (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType))) {

						String strHATS = "Has ATS: " + strHasATS;
						sbXMLContent.append("TEXTRIGHT= \"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;"
								+ padRight(strHATS, 66) + "&lt;CR&gt;\"\n");
					} else {
						sbXMLContent.append("TEXTRIGHT= \"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;" + ""
								+ "&lt;CR&gt;\"\n");
					}
				} else {
					sbXMLContent.append("TEXTLEFT=\"" + strObjectSecurityStatus + "&lt;CR&gt;&lt;CR&gt;Name:"
							+ BLANK_STRING + strObjectName + "          Revision:" + BLANK_STRING + strObjectRevision
							+ "           State:" + BLANK_STRING + strObjectCurrentDisplay
							+ "&lt;CR&gt;SAP Description:" + BLANK_STRING + strSAPTitle
							+ "&lt;CR&gt;___________________________________________________________________________________________________________________________________________\"\n");

					if (isTextRightUpdate) {
						sbXMLContent.append("TEXTRIGHT=\"Page &amp;[Page] of &amp;[Pages]&lt;CR&gt;&lt;CR&gt;Is ATS: "
								+ padRight(strIsATS, 5) + "&lt;CR&gt;Has ATS: " + strHasATS + "\"\n");
					}
				}

				sbXMLContent.append("PAGES=\"1\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");

				// First page Header 1 ends
				// First page header for Bold overlay starts:
				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Bold\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");

				if (((pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)) && (strProductDataTypes.contains(strObjectType)))
						|| (pgV3Constants.PQR_VIEW.equals(strPDFViewType)
								&& (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy)
										|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy)))
						|| ((pgPDFViewConstants.CONST_CONTRACTPACKAGING.equals(strPDFViewType))
								&& !(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equals(strObjectType)
										|| pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equals(strObjectType)))
						|| ((pgPDFViewConstants.CONST_WAREHOUSE.equals(strPDFViewType)
								&& (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType))))) {

					String strHeader22 = "";
					strHeader22 = strHeader2.substring(0, strHeader2.length() - 2);
					sbXMLContent.append("TEXTCENTER=\"&lt;CR&gt;" + strHeader22 + "\"\n");
				} else {
					sbXMLContent.append(
							"TEXTCENTER=\"&lt;CR&gt;" + strHeader2 + " " + strSAPType + " " + strHeader3 + "\"\n");
				}
				sbXMLContent.append("PAGES=\"1\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");
				// First page header for BOLD overlay ends:
			}
			if ((pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType)) && (strProductDataTypes.contains(strObjectType))
					&& !(pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy)
							|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy))) {
				// For GenDoc Second Page starts
				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Roman\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
				sbXMLContent.append("TEXTCENTER=\"The Procter &amp; Gamble Company - " + strHeader1 + "\"\n");
				sbXMLContent.append("TEXTRIGHT= \"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;\"\n");
				sbXMLContent.append("TEXTLEFT=\"" + strObjectSecurityStatus + "&lt;CR&gt;&lt;CR&gt;Name:" + BLANK_STRING
						+ strObjectName + "          Revision:" + BLANK_STRING + strObjectRevision + "\"\n");
				sbXMLContent.append("PAGES=\"1\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");

				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Bold\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
				// For GenDoc Second Page Ends
				// For GenDoc Bold Overlay starts for Second Page
				sbXMLContent
				.append("TEXTCENTER=\"&lt;CR&gt;" + strHeader2 + " " + strSAPType + " " + strHeader3 + "\"\n");
				sbXMLContent.append("PAGES=\"1\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");

				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Roman\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
				sbXMLContent.append("TEXTCENTER=\"The Procter &amp; Gamble Company - " + strHeader1 + "\"\n");
				if (strObjectType.equals(pgV3Constants.TYPE_PGPROMOTIONALITEMPART)) {

					sbXMLContent.append("TEXTLEFT=\"" + strObjectSecurityStatus + "&lt;CR&gt;&lt;CR&gt;Name:"
							+ BLANK_STRING + strProductDataObjectName + "     Revision:" + BLANK_STRING
							+ strProductDataObjectRevision + "Title:" + BLANK_STRING + strSAPTitle
							+ "&lt;CR&gt;___________________________________________________________________________________________________________________________________________\"\n");
					String strED = "Effective Date:" + BLANK_STRING + strEffectiveDate;
					sbXMLContent.append("TEXTRIGHT= \"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;"
							+ padRight(strED, 26) + "&lt;CR&gt;\"\n");

				} else {
					sbXMLContent.append("TEXTLEFT=\"" + strObjectSecurityStatus + "&lt;CR&gt;&lt;CR&gt;Name:"
							+ BLANK_STRING + strObjectName + "     Revision:" + BLANK_STRING + strObjectRevision
							+ "Title:" + BLANK_STRING + strSAPTitle
							+ "&lt;CR&gt;___________________________________________________________________________________________________________________________________________\"\n");
					sbXMLContent.append("TEXTRIGHT= \"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;\"\n");
				}

				sbXMLContent.append("PAGES=\"DOC1[2-]\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");
				// For GenDoc Bold Overlay Ends for Second Page
				// For GenDoc remaining Pages starts

				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Bold\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
				String strHeader22 = "";
				strHeader22 = strHeader2.substring(0, strHeader2.length() - 2);
				sbXMLContent.append("TEXTCENTER=\"&lt;CR&gt;" + strHeader22 + "\"\n");
				sbXMLContent.append("PAGES=\"DOC1[2-]\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");
				// For GenDoc remaining Pages Ends
			} else if (!(pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType)
					&& (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strObjectPolicy)
							|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strObjectPolicy)))) {
				// Remaining page headers
				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Roman\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
				sbXMLContent.append("TEXTCENTER=\"The Procter &amp; Gamble Company - " + strHeader1 + "\"\n");
				if (strProductDataTypes.contains(strObjectType)
						&& !(pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strObjectType))
						&& !(pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType)
								&& strOriginatingSource.contains(pgV3Constants.CSS_SOURCE))) {
					sbXMLContent.append("TEXTLEFT=\"" + strObjectSecurityStatus + "&lt;CR&gt;&lt;CR&gt;Name:"
							+ BLANK_STRING + strProductDataObjectName + "     Revision:" + BLANK_STRING
							+ strProductDataObjectRevision + "Title:" + BLANK_STRING + strSAPTitle
							+ "&lt;CR&gt;___________________________________________________________________________________________________________________________________________\"\n");
					String strED = "Effective Date:" + BLANK_STRING + strEffectiveDate;
					sbXMLContent.append("TEXTRIGHT= \"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;"
							+ padRight(strED, 26) + "&lt;CR&gt;\"\n");
				} else {
					sbXMLContent.append("TEXTLEFT=\"" + strObjectSecurityStatus + "&lt;CR&gt;&lt;CR&gt;Name:"
							+ BLANK_STRING + strProductDataObjectName + "     Revision:" + BLANK_STRING
							+ strProductDataObjectRevision + "SAP Description:" + BLANK_STRING + strSAPTitle
							+ "&lt;CR&gt;___________________________________________________________________________________________________________________________________________\"\n");
					sbXMLContent
					.append("TEXTRIGHT=\"Page &amp;[Page] of &amp;[Pages] &lt;CR&gt;&lt;CR&gt;Effective Date:"
							+ BLANK_STRING + strEffectiveDate + "\"\n");
				}

				sbXMLContent.append("PAGES=\"DOC1[2-]\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");
				// remaining page overlay for BOLD starts:HM
				sbXMLContent.append("<JOB:HEADER ENABLED=\"Yes\"\n");
				sbXMLContent.append("FONTNAME=\"Times-Bold\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
				sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
				sbXMLContent.append("LAYER=\"Foreground\"\n");
				sbXMLContent.append("MARGINLEFT=\"0.5\"\n");
				sbXMLContent.append("MARGINRIGHT=\"0.5\"\n");
				sbXMLContent.append("MARGINVERTICAL=\"0.5\"\n");
				if (strProductDataTypes.contains(strObjectType)) {
					if (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)) {
						String strHeader22 = "";
						strHeader22 = strHeader2.substring(0, strHeader2.length() - 2);
						sbXMLContent.append("TEXTCENTER=\"&lt;CR&gt;" + strHeader22 + "\"\n");
					} else if (strHeader2.contains("MRMS") || strHeader2.contains("MATL")) {
						sbXMLContent.append("TEXTCENTER=\"&lt;CR&gt;" + strHeader2 + " " + strSAPType + "\"\n");
					} else {
						String strHeader22 = "";
						strHeader22 = strHeader2.substring(0, strHeader2.length() - 2);
						sbXMLContent.append("TEXTCENTER=\"&lt;CR&gt;" + strHeader22 + "\"\n");
					}
				} else {
					sbXMLContent.append(
							"TEXTCENTER=\"&lt;CR&gt;" + strHeader2 + " " + strSAPType + " " + strHeader3 + "\"\n");
				}

				sbXMLContent.append("PAGES=\"DOC1[2-]\"\n");
				sbXMLContent.append("PAGENUMBERINGMODE=\"MergedDocument\"/>\n");
				// remaining page overlay for BOLD ends:HM
				// Remaining page header ends
			}
			sbXMLContent.append("</JOB:HEADERS>\n");
			sbXMLContent.append(
					"<JOB:HTMLCONVERSION MARGINBOTTOM=\"0.5\" MARGINLEFT=\"0.5\" MARGINRIGHT=\"0.5\" MARGINTOP=\"1.2\" ORIENTATION=\"Landscape\"  PAGESIZE=\"A4\" PAGEHEIGHT=\"8\" PAGEWIDTH=\"11\"/>\n");
			sbXMLContent.append("<JOB:WATERMARKS ENABLED=\"Yes\">\n");
			sbXMLContent.append("<JOB:WATERMARK ENABLED=\"Yes\"\n");
			if (strProductDataTypes.contains(strObjectType)) {
				sbXMLContent.append("FONTNAME=\"Times-Italic\"\n");
				sbXMLContent.append("FONTSIZE=\"10\"\n");
			} else {
				sbXMLContent.append("FONTNAME=\"Times-Roman\"\n");
				sbXMLContent.append("FONTSIZE=\"11\"\n");
			}
			sbXMLContent.append("FONTCOLOR=\"4144959\"\n");
			sbXMLContent.append("LAYER=\"Foreground\"\n");
			sbXMLContent.append("VERTICAL=\"0.1\"\n");
			sbXMLContent.append("OPACITY=\"80\"\n");
			sbXMLContent.append("ORIENTATION=\"Page\"\n");
			sbXMLContent.append("PAGES=\"All\"/>\n");
			sbXMLContent.append("</JOB:WATERMARKS>\n");
		}

		sbXMLContent.append("</JOB:SETTINGS>\n");
		sbXMLContent.append("</JOB>\n");
		sbXMLContent.append("</JOBS>");
		adlibeTicketDetails.put("AdlibTicketDetails", sbXMLContent.toString().trim());
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return adlibeTicketDetails;
		
	}

	/**
	 *
	 * @param strDate
	 * @return
	 */
	public static String getFormattedDate(String strDate) throws MatrixException {
		SimpleDateFormat formatter = null;
		Date tmpDate = null;
		String formatedDate = null;
		StringBuffer sbformatedDate = new StringBuffer();
		try {
			if ((null != strDate) && (!"".equals(strDate)) && (!"null".equals(strDate))
					&& (!strDate.contains("DENIED"))) {
				if (strDate.contains(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR)) {
					String[] strDates = strDate.split(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
					for (int i = 0; i < strDates.length; i++) {
						strDate = strDates[i];
						formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
						tmpDate = formatter.parse(strDate);
						formatter = new SimpleDateFormat(DATE_FORMAT);
						formatedDate = formatter.format(tmpDate);
						sbformatedDate.append(formatedDate).append(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
					}
				} else {
					formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
					tmpDate = formatter.parse(strDate);
					formatter = new SimpleDateFormat(DATE_FORMAT);
					formatedDate = formatter.format(tmpDate);
					sbformatedDate.append(formatedDate);
				}

			} else {
				return "";
			}
		} catch (ParseException ex) {
			throw new MatrixException(ex);
		}
		return sbformatedDate.toString();
	}

	/**
	 *
	 * @param strTrueFalse
	 * @return
	 */
	public static String convertToYesNo(String strTrueFalse) {
		String strReturnString = "";
		String strCondition = null;
		StringBuffer sbformatedCondition = new StringBuffer();

		if (null != strTrueFalse && !"".equals(strTrueFalse) && !"null".equals(strTrueFalse)) {
			if (strTrueFalse.contains(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR)) {
				String[] strElement = strTrueFalse.split(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
				for (int i = 0; i < strElement.length; i++) {
					strCondition = strElement[i];

					if (strCondition.equalsIgnoreCase("True")) {
						strReturnString = "Yes";
					} else if (strCondition.equalsIgnoreCase("False")) {
						strReturnString = "No";
					}

					sbformatedCondition.append(strReturnString).append(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
				}

			} else {
				if (strTrueFalse.equalsIgnoreCase("True")) {
					strReturnString = "Yes";
				} else if (strTrueFalse.equalsIgnoreCase("False")) {
					strReturnString = "No";
				}
				sbformatedCondition.append(strReturnString);
			}
		}
		return sbformatedCondition.toString();
	}

	/**
	 *
	 * @param strTrueFalse
	 * @return
	 */
	public static String convertTomultipleStrings(String strTrueFalse) {
		String strReturnString = "";
		String strCondition = null;
		StringBuffer sbformatedCondition = new StringBuffer();

		if (null != strTrueFalse && !"".equals(strTrueFalse) && !"null".equals(strTrueFalse)) {
			if (strTrueFalse.contains(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR)) {
				String[] strElement = strTrueFalse.split(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
				for (int i = 0; i < strElement.length; i++) {
					strCondition = strElement[i];

					if (strCondition.equalsIgnoreCase("True")) {
						strReturnString = "Yes";
					} else if (strCondition.equalsIgnoreCase("False")) {
						strReturnString = "No";
					}

					sbformatedCondition.append(strReturnString).append(pgPDFViewConstants.DYNAMIC_ROW_SEPERATOR);
				}

			} else {
				if (strTrueFalse.equalsIgnoreCase("True")) {
					strReturnString = "Yes";
				} else if (strTrueFalse.equalsIgnoreCase("False")) {
					strReturnString = "No";
				}
				sbformatedCondition.append(strReturnString);
			}
		}
		return sbformatedCondition.toString();
	}

	/**
	 *
	 * @param s
	 * @param n
	 * @return
	 */
	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	/**
	 *
	 * @param s
	 * @param n
	 * @return
	 */
	public static String padLeft(String s, int n) {
		return String.format("%1$#" + n + "s", s);
	}

	/**
	 *
	 * @param sBoolean
	 * @return
	 */
	private String changeTrueToYes(String sBoolean) {
		if (sBoolean != null && sBoolean.equalsIgnoreCase("True")) {
			return "Yes";
		} else {
			return "No";
		}
	}

	/**
	 *
	 * @param strYesNo
	 * @return
	 */
	private String strPlantUsage(String strYesNo) {
		String strYESNO = null;
		if ("Yes".equalsIgnoreCase(strYesNo)) {
			strYESNO = "YES";
		} else {
			strYESNO = "";
		}
		return strYESNO;
	}

	/**
	 *
	 * @param strYesNo
	 * @return
	 */
	private String strImageSource(String strYesNo) {
		String strImageSource = null;
		if ("Yes".equalsIgnoreCase(strYesNo)) {
			strImageSource = "Yes";
			// strImageSource="<img border=\"0\" alt=\"Yes\"
			// src=\"../Common/images/iconStatusComplete.gif\"></img>";
		} else {
			//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #34669  - Starts
			strImageSource = "No";
			//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #34669  - Ends
			// strImageSource="<img border=\"0\" alt=\"Yes\"
			// src=\"../Common/images/iconStatusError.gif\"></img>";
		}
		return strImageSource;
	}

	/**
	 *
	 * @return
	 */
	public static Map<String, String> getFooterStampings(Context context, String strType,
			String strObjectLifeCycleState, String strObjectStatus, String strPDFViewType, String strStampingDate,
			String strArchiveDate, String strObjectRevision, String strObjectSecurityStatus) throws Exception {
		Map mapFooter = new HashMap();
		TimeZone tz = Calendar.getInstance().getTimeZone();
		String strPrintedDate = new SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance(tz).getTime());
		String strContractSupplier = " P&amp;G Authorized View Created: ";
		String strPlanningMessage = " THIS STANDARD IS NOT TO BE USED FOR PRODUCTION\n";
		String strPreliminary = " P&amp;G PRELIMINARY Printed ";
		String strObsolate = " P&amp;G OBSOLETE " + strArchiveDate;
		String strPnGAuthorize = " P&amp;G AUTHORIZED Release Date ";
		String strPnGAuthorizePlann = " P&amp;G AUTHORIZED Rev ";
		String strProductDataTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST);
		String strPrereleaseState = EnoviaResourceBundle.getProperty(context,
				"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.PRERELEASED_STATE");
		String strPreReleaseStamp = "P&amp;G PRE_RELEASED Rev";
		String strPLMViewStamp = "PLM View Created ";
		String strBusinessUseStamp = DomainConstants.EMPTY_STRING;

		if (pgV3Constants.RESTRICTED.equalsIgnoreCase(strObjectSecurityStatus)
				|| pgV3Constants.INTERNAL_USE.equalsIgnoreCase(strObjectSecurityStatus)) {
			strBusinessUseStamp = pgV3Constants.BUSINESS_USE_HYPHEN;
		}

		mapFooter.put("FONTNAME", "Helvetica");
		mapFooter.put("FONTSIZE", "13");
		mapFooter.put("FONTCOLOR", "4144959");
		mapFooter.put("FONTEXTENDEDBOLD", "Yes");
		mapFooter.put("LAYER", "Foreground");
		mapFooter.put("MARGINLEFT", "0.5");
		mapFooter.put("MARGINRIGHT", "0.5");
		mapFooter.put("MARGINVERTICAL", "0.2");
		if (strProductDataTypes.contains(strType) && (pgPDFViewConstants.CONST_ALLINFO.equalsIgnoreCase(strPDFViewType))) {

			if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPLMViewStamp + strPrintedDate);

			} else if (strPrereleaseState.contains(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPreReleaseStamp + strObjectRevision
						+ " Effective Date " + strStampingDate + " Printed  " + strPrintedDate);

			} else if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strObsolate + " PRINTED " + strPrintedDate);

			} else {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strContractSupplier + strPrintedDate);
			}
		} else if (strProductDataTypes.contains(strType) && (pgPDFViewConstants.CONST_WAREHOUSE.equalsIgnoreCase(strPDFViewType)
				|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType)
				|| pgPDFViewConstants.CONST_SUPPLIER.equalsIgnoreCase(strPDFViewType)
				|| pgPDFViewConstants.CONST_CONTRACTPACKAGING.equalsIgnoreCase(strPDFViewType))) {
			if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPnGAuthorizePlann + strObjectRevision
						+ " Effective Date " + strStampingDate + " Printed  " + strPrintedDate);
			} else if (strPrereleaseState.contains(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPreReleaseStamp + strObjectRevision
						+ " Effective Date " + strStampingDate + " Printed  " + strPrintedDate);
			} else if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strObsolate + " PRINTED " + strPrintedDate);
			} else {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strContractSupplier + strPrintedDate);
			}
		} else if ((pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType)) && (strProductDataTypes.contains(strType))) {
			mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPnGAuthorizePlann + strObjectRevision
					+ " Effective Date " + strStampingDate + " Printed  " + strPrintedDate);
		} else if (pgPDFViewConstants.CONST_SUPPLIER.equalsIgnoreCase(strPDFViewType)
				|| pgPDFViewConstants.CONST_CONTRACTPACKAGING.equalsIgnoreCase(strPDFViewType)) {
			if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPnGAuthorizePlann + strObjectRevision
						+ " Effective Date " + strStampingDate + " Printed  " + strPrintedDate);
			} else if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strObsolate + " PRINTED " + strPrintedDate);
			} else {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + "P&amp;G PRE_RELEASED Printed " + strPrintedDate);
			}
		} else {
			if (pgV3Constants.STATE_PRELIMINARY.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPreliminary + strPrintedDate);
			} else if (pgV3Constants.STATE_RELEASED.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER,
						strBusinessUseStamp + strPnGAuthorize + strStampingDate + " Printed  " + strPrintedDate);
			} else if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjectLifeCycleState)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strObsolate + " Printed " + strPrintedDate);
			} else if ("PLANNING".equalsIgnoreCase(strObjectStatus)) {
				strPnGAuthorize = " P&amp;G AUTHORIZED Effective Date ";
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strPlanningMessage + "&lt;CR&gt;"
						+ strPnGAuthorize + strStampingDate + " Printed " + strPrintedDate);
			} else if (pgPDFViewConstants.CONST_WAREHOUSE.equalsIgnoreCase(strPDFViewType)
					|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equalsIgnoreCase(strPDFViewType)) {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + strContractSupplier + strPrintedDate);
			} else {
				mapFooter.put(pgPDFViewConstants.CONST_TEXTCENTER, strBusinessUseStamp + "PLM View Created: " + strPrintedDate);
			}
		}
		return mapFooter;
	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean checkViewAvailability(Context context, String[] args) throws Exception {
		boolean viewAvailable = false;
		boolean isSubAssembly = false;
		try {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			String strObjectId = (String) programMap.get("objectId");
			Map mapSettings = (Map) programMap.get("SETTINGS");
			String strView = (String) mapSettings.get("view");

			DomainObject dmo = DomainObject.newInstance(context, strObjectId);
			StringList slRelSelect = new StringList(1);
			slRelSelect.add(DomainConstants.SELECT_ID);
			slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);

			StringList slMaster = new StringList(4);
			slMaster.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
			slMaster.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			slMaster.add(DomainConstants.SELECT_TYPE);
			slMaster.add(DomainConstants.SELECT_POLICY);
			slMaster.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);

			Map<String, String> mapMaster = dmo.getInfo(context, slMaster);
			String strType = mapMaster.get(DomainConstants.SELECT_TYPE);// dmo.getType(context);
			String strCSSType = mapMaster.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);// dmo.getAttributeValue(context,"pgCSSType");

			String strOriginatingSource = mapMaster.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);

			String strAssemblyType = mapMaster.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);

			String strPolicy = mapMaster.get(DomainConstants.SELECT_POLICY);

			boolean isPSUB = validateString(strAssemblyType);

			String strContractPackagingTypes = EnoviaResourceBundle.getProperty(context,
					"emxCPN.PDFViews.ContractPackagingTypes");
			String strSupplierViewTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.SupplierViewTypes");

			String strPQRViewTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.PQRTypes");

			if (isPSUB && (strAssemblyType.equals("Purchased Subassembly")
					|| strAssemblyType.equals("Purchased and/or Produced Subassembly"))) {
				isSubAssembly = true;
			}

			boolean havingMaster = false;
			if (strType.equals(pgV3Constants.TYPE_PGRAWMATERIAL)
					|| (strType.equals(pgV3Constants.TYPE_PGFINISHEDPRODUCT))
					|| (strType.equals(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY))
					|| (strType.equals(pgV3Constants.TYPE_PGPACKINGMATERIAL))) {
				havingMaster = dmo.hasRelatedObjects(context, "" + pgV3Constants.RELATIONSHIP_PGMASTER + "", true);
			}

			if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER)) {
				// for CM following commands needs to be displayed
				// 1) "Consolidated Packing View" for Structured IPS, MPS
				// 2) "Gen Doc" & "Contract Packaging View" for Structured FC
				// 3) "Gen Doc" for All Other Structured Document Types
				// 4) "Actual Document" for All Other Non-Structured Document Types

				if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING)
						|| strView.equalsIgnoreCase(pgPDFViewConstants.CONST_CONTRACTPACKAGING)) {
					// need to check if 'Plants Authorized to Use' only for Structured FC for
					// contractpackaging
					if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_CONTRACTPACKAGING)) {
						if ((strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFORMULATEDPRODUCT)
								&& strCSSType.contains("_")) || (strContractPackagingTypes.contains(strType))) {
							BusinessObject boPerson = new BusinessObject(DomainConstants.TYPE_PERSON, context.getUser(),
									"-", pgV3Constants.VAULT_ESERVICEPRODUCTION);
							DomainObject doPersonObject = DomainObject.newInstance(context,
									boPerson.getObjectId(context));

							StringList slPlants = doPersonObject.getInfoList(context,
									"to[" + pgV3Constants.RELATIONSHIP_MEMBER + "].from.name");

							MapList mlPlantsToPD = dmo.getRelatedObjects(context,
									DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, pgV3Constants.TYPE_PLANT,
									new StringList(DomainObject.SELECT_NAME), slRelSelect, true, false, (short) 1, null,
									"");

							boolean bHasPlant = false;
							String strName = "";
							String strIsAutToUse = "";
							if (null != mlPlantsToPD && mlPlantsToPD.size() > 0) {
								for (int i = 0; i < mlPlantsToPD.size(); i++) {
									Map mPlantData = (Map) mlPlantsToPD.get(i);
									String strPlant = (String) mPlantData.get(DomainObject.SELECT_NAME);
									if (slPlants.contains(strPlant)) {
										strIsAutToUse = (String) mPlantData
												.get("attribute[" + pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE + "]");
										if (StringUtils.isNotBlank(strIsAutToUse)
												&& strIsAutToUse.equalsIgnoreCase(pgV3Constants.CONST_TRUE)) {
											viewAvailable = true;
											break;
										}
									}
								}
							}
							//Modify Code Refactoring
							mlPlantsToPD.clear();
						}
					} else if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING)) {
						if (strCSSType.contains("_") && (strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFINISHEDPRODUCT)
								|| strType.equalsIgnoreCase(pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT)
								|| strType.equalsIgnoreCase(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY))) {
							viewAvailable = true;
						}
						if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strType)) {

							viewAvailable = true;
						}

					}
				} else if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_SUPPLIER)) {
					if (strSupplierViewTypes.contains(strType)) {
						viewAvailable = true;
					}
				}
			}
			if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)) {
				// for CS & CP following commands needs to be displayed
				// 1) "Supplier View" for Structured IRMS, MRMS, IPMS, MPMS, IPS, MPS
				// 2) "Actual Document" for All Other Non-Structured Document Types

				if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_SUPPLIER)) {
					if (strCSSType.contains("_") && (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strType)
							|| pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equals(strType)
							|| pgV3Constants.TYPE_PGRAWMATERIAL.equals(strType)
							|| pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strType)
							|| pgV3Constants.TYPE_PGPACKINGMATERIAL.equals(strType)
							|| pgV3Constants.TYPE_PGMASTERPACKINGMATERIAL.equals(strType)
							|| pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strType))) {
						viewAvailable = true;
					}

					if (pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART.equals(strType)
							|| pgV3Constants.TYPE_PGPROMOTIONALITEMPART.equals(strType)
							|| pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strType)
							|| pgV3Constants.TYPE_PACKAGINGMATERIALPART.equals(strType)
							|| pgV3Constants.TYPE_PGONLINEPRINTINGPART.equals(strType)
							|| pgV3Constants.TYPE_FABRICATEDPART.equals(strType)
							|| pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART.equals(strType)
							|| pgV3Constants.TYPE_RAWMATERIALPART.equals(strType)
							|| (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strType)
									&& pgV3Constants.DSM_ORIGIN.equals(strOriginatingSource))) {
						viewAvailable = true;
					}
				}
			} else {
				boolean isStructured = checkIfStructured(context, strType, strCSSType, strOriginatingSource);
				if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_WAREHOUSE)) {
					isStructured = true;
				}
				boolean viewOK = checkViewType(context, strView, strType, havingMaster, isSubAssembly,
						strOriginatingSource);

				if (isStructured && viewOK) {
					if ((pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strType))
							&& (pgPDFViewConstants.CONST_SUPPLIER.equals(strView))) {
						viewAvailable = false;
					} else {
						viewAvailable = true;
					}
				}

				if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_CONTRACTPACKAGING)) {
					if ((strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFORMULATEDPRODUCT) && strCSSType.contains("_"))
							|| (strContractPackagingTypes.contains(strType))) {
						BusinessObject boPerson = new BusinessObject(DomainConstants.TYPE_PERSON, context.getUser(),
								"-", pgV3Constants.VAULT_ESERVICEPRODUCTION);
						DomainObject doPersonObject = DomainObject.newInstance(context, boPerson.getObjectId(context));
						StringList slPlants = doPersonObject.getInfoList(context,
								"to[" + pgV3Constants.RELATIONSHIP_MEMBER + "].from.name");

						// get the plants info to which the standard is connected
						MapList mlPlantsToPD = dmo.getRelatedObjects(context,
								DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, pgV3Constants.TYPE_PLANT,
								new StringList(DomainObject.SELECT_NAME), slRelSelect, true, false, (short) 1, null,
								"");

						boolean bHasPlant = false;
						String strName = "";
						String strIsAutToUse = "";
						Map mPlantData = null;
						String strPlant = null;
						if (null != mlPlantsToPD && mlPlantsToPD.size() > 0) {
							for (int i = 0; i < mlPlantsToPD.size(); i++) {
								mPlantData = (Map) mlPlantsToPD.get(i);
								strPlant = (String) mPlantData.get(DomainObject.SELECT_NAME);
								if (slPlants.contains(strPlant)) {
									strIsAutToUse = (String) mPlantData
											.get("attribute[" + pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE + "]");
									if (StringUtils.isNotBlank(strIsAutToUse)
											&& strIsAutToUse.equalsIgnoreCase(pgV3Constants.CONST_TRUE)) {
										viewAvailable = true;
										break;
									}
								}
							}
						}
					}
					if (strType.equalsIgnoreCase(pgV3Constants.TYPE_PGFORMULATEDPRODUCT) && !strCSSType.contains("_")) {
						viewAvailable = false;
					}
				}
				// Added by DSM(Sogeti)-2015x.5.1 for PDF Views (Defect Id-19060) - Starts
				if (strView.equalsIgnoreCase(pgPDFViewConstants.CONST_SUPPLIER)
						&& (pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equals(strType)
								|| pgV3Constants.TYPE_DEVICEPRODUCTPART.equals(strType))) {
					viewAvailable = false;
				}
				// Added by DSM(Sogeti)-2015x.5.1 for PDF Views (Defect Id-19060) - Ends
			}
			if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy) || pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy) ) {
				if ((pgPDFViewConstants.CONST_SUPPLIER.equalsIgnoreCase(strView) || pgPDFViewConstants.CONST_ALLINFO.equalsIgnoreCase(strView)
						|| pgV3Constants.PQR_VIEW.equalsIgnoreCase(strView))) {
					viewAvailable = true;
				} else {
					viewAvailable = false;
				}

			}
			/* Modified by DSM (Req id #47506) - Start */
			if (pgV3Constants.TYPE_STRUCTURED_ATS.equalsIgnoreCase(strType) && pgPDFViewConstants.CONST_ALLINFO.equalsIgnoreCase(strView)) {
			
					viewAvailable = true;

			}
			/* Modified by DSM (Req id #47506) - End */
		} catch (Exception e) {
			throw new MatrixException(e);
		}
		return viewAvailable;
	}

	/**
	 * This method checks whether the given standard is structured or not
	 * 
	 * @param context
	 * @param strObjId
	 * @return
	 * @throws MatrixException
	 */

	private boolean checkIfStructured(Context context, String strType, String strCSSType, String strOriginatingSource)
			throws MatrixException

	{
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

			if (slDSOTypes.contains(strType) || (slCSSTypes.contains(strType)
					&& (strCSSType.contains("_") || pgV3Constants.DSM_ORIGIN.equals(strOriginatingSource)))) {
				isStructured = true;
			}

		} catch (Exception e) {
			throw new MatrixException(e);
		}
		return isStructured;
	}

	/**
	 *
	 * @param context
	 * @param strView
	 * @param strType
	 * @param strOriginatingSource
	 * @return
	 * @throws MatrixException
	 */
	private boolean checkViewType(Context context, String strView, String strType, boolean havingMaster,
			boolean isSubAssembly, String strOriginatingSource) throws MatrixException {

		boolean isViewOK = false;
		boolean isWareHouseView = false;

		String strProductDataTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST);
		String strAllInfoTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.AllInfoTypes");
		String strSupplierViewTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.SupplierViewTypes");
		String strContractPackagingTypes = EnoviaResourceBundle.getProperty(context,
				"emxCPN.PDFViews.ContractPackagingTypes");
		String strCombinedWithMasterTypes = EnoviaResourceBundle.getProperty(context,
				"emxCPN.PDFViews.CombinedWithMasterTypes");
		String strWareHouseTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.WareHouseTypes");
		String strConsolidatedPackagingTypes = EnoviaResourceBundle.getProperty(context,
				"emxCPN.PDFViews.ConsolidatedPackagingTypes");
		String strPQRTypes = EnoviaResourceBundle.getProperty(context, "emxCPN.PDFViews.PQRTypes");

		String strUserName = context.getUser();

		String contextUserType = MqlUtil.mqlCommand(context,
				"print bus Person '" + strUserName + "' - select attribute[pgSecurityEmployeeType].value dump");

		if ("Employee".equals(contextUserType) || context.isAssigned("pgIPMWarehouseReader")) {
			isWareHouseView = true;
		}
		if (validateString(strView) && validateString(strType)) {
			if ((pgPDFViewConstants.CONST_ALLINFO.equals(strView)) && (strAllInfoTypes.contains(strType))) {
				isViewOK = true;
			} else if ((pgPDFViewConstants.CONST_COMBINEDWITHMASTER.equals(strView)) && havingMaster
					&& (strCombinedWithMasterTypes.contains(strType))) {
				isViewOK = true;
			} else if ((pgPDFViewConstants.CONST_CONTRACTPACKAGING.equals(strView)) && (strContractPackagingTypes.contains(strType))) {
				isViewOK = true;
			} else if ((pgPDFViewConstants.CONST_SUPPLIER.equals(strView)) && (strSupplierViewTypes.contains(strType))) {
				isViewOK = true;
			} else if ((pgPDFViewConstants.CONST_WAREHOUSE.equals(strView)) && isWareHouseView && (strWareHouseTypes.contains(strType))) {
				isViewOK = true;
			} else if ((pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strView))
					&& (strConsolidatedPackagingTypes.contains(strType))) {
				isViewOK = true;
			} else if ((pgV3Constants.PQR_VIEW.equals(strView)) && (strPQRTypes.contains(strType))) {
				if (pgV3Constants.TYPE_PGRAWMATERIAL.equals(strType) && !"DSO".equals(strOriginatingSource)) {
					isViewOK = false;
				} else {
					isViewOK = true;
				}
			} else if ((pgV3Constants.PDFVIEW_GENDOC.equals(strView)) && (strProductDataTypes.contains(strType))) {
				isViewOK = true;
			}
			if (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER)
					|| context.isAssigned(pgV3Constants.ROLE_PGCONTRACTPACKER)) {
				StringList slViews = new StringList();
				slViews.add(pgPDFViewConstants.CONST_ALLINFO);
				slViews.add(pgPDFViewConstants.CONST_COMBINEDWITHMASTER);

				if (slViews.contains(strView)
						|| (pgPDFViewConstants.CONST_SUPPLIER.equals(strView) && (strSupplierViewTypes.contains(strType)))) {
					isViewOK = false;
				}
			}
		}
		return isViewOK;
	}

	/**
	 *
	 * @param context
	 * @param mapMaster
	 * @return
	 */
	private boolean checkIfHavingMaster(Context context, Map<String, String> mapMaster) {
		boolean isHavingMaster = false;
		if (null != mapMaster && !mapMaster.isEmpty()) {
			boolean toSideMaster = (Boolean
					.parseBoolean(mapMaster.get("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "]")));
			if (toSideMaster) {
				isHavingMaster = true;
			}
		}
		return isHavingMaster;
	}

	/**
	 *
	 * @param strVerifyString
	 * @return
	 */
	public static boolean validateString(String strVerifyString) {
		boolean isStringDesired = false;
		if (null != strVerifyString && !"".equals(strVerifyString) && !"null".equalsIgnoreCase(strVerifyString)) {
			isStringDesired = true;
		}
		return isStringDesired;
	}

	/**
	 *
	 * @param context
	 * @param strOriginator
	 * @return
	 * @throws Exception
	 */
	public String getUserName(Context context, String strOriginator) throws Exception {
		String strUserName = "";
		try {
			StringList slObjSelects = new StringList(5);
			slObjSelects.addElement(DomainConstants.SELECT_ID);
			slObjSelects.addElement(DomainConstants.SELECT_NAME);
			slObjSelects.addElement("attribute[First Name]");
			slObjSelects.addElement("attribute[Last Name]");
			slObjSelects.addElement("attribute[pgTNumber]");
			String strNamePattern = "*";
			String strRevisionPattern = "*";
			String strWhere = "attribute[pgTNumber]==" + strOriginator + " && revision==last";
			MapList mlDataList = DomainObject.findObjects(context, "Person", strNamePattern, strRevisionPattern, "*",
					"eService Production", strWhere, false, slObjSelects);
			if (mlDataList.size() > 0) {
				Map dataMap = (Map) mlDataList.get(0);
				strUserName = (String) dataMap.get("attribute[First Name]") + " "
						+ (String) dataMap.get("attribute[Last Name]");
			} else {
				strUserName = strOriginator;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			return strUserName;
		}
	}

	public String getFormatedValue(Context context, String strListValue) {
		if (validateString(strListValue)) {
			if ("Customer_Unit".equalsIgnoreCase(strListValue)) {
				strListValue = "Customer Unit";
			} else if ("Inner_Pack".equalsIgnoreCase(strListValue)) {
				strListValue = "Inner Pack";
			} else if ("Consumer_Unit".equalsIgnoreCase(strListValue)) {
				strListValue = "Consumer Unit";
			}
		}
		return strListValue;
	}

	public StringList getTraderDistributorInformation(Context context, String strListValue) {
		// G STREHLE KG~AM PFERDEMARKT~LANGENHAGEN~*~GERMANY~10005197
		// Manufacturer ~Street~ City ~State~Country~Manufacturer Vendor Number
		StringList slValues = new StringList(1);
		if (validateString(strListValue)) {
			slValues = FrameworkUtil.split(strListValue, "~");
		}
		return slValues;
	}

	public String getBrandInformation(Context context, String strListValue) {
		// KADUS~*~DEMI PCOL CRM~0/00
		StringBuffer stBuff = new StringBuffer();
		if (validateString(strListValue)) {
			StringList strBrandValueList = new StringList(1);
			StringTokenizer st = new StringTokenizer(strListValue, "|");
			while (st.hasMoreTokens()) {
				String strTemp = st.nextToken();
				int i = strTemp.indexOf("~");
				String strValue = strTemp.substring(0, i);
				strBrandValueList.addElement(strValue);
			}
			strBrandValueList.sort();

			String strNewValue = DomainConstants.EMPTY_STRING;
			for (int nCount = 0; nCount < strBrandValueList.size(); nCount++) {
				strNewValue = (String) strBrandValueList.get(nCount);
				if (stBuff.length() < 1) {
					stBuff.append(strNewValue);
				} else {
					if (stBuff.indexOf(strNewValue) == -1) {
						stBuff.append("<br/>").append(strNewValue);
					}
				}

			}
		}
		return stBuff.toString();
	}

	/**
	 * 
	 * @param context
	 * @param strType
	 * @param strCSSType
	 * @return
	 * @throws MatrixException
	 */
	public boolean checkIfStructuredForEBPUser(Context context, String strType, String strCSSType)
			throws MatrixException {

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

				if ((context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) && slCMTypes.contains(strType))
						|| (context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER) && slCSTypes.contains(strType))) {
					isStructured = true;
				}
			}
		} catch (Exception e) {
			throw new MatrixException(e);
		}
		return isStructured;
	}

	public boolean checkForStructuredType(Context context, String strType, String strCSSType) throws MatrixException {
		boolean isStructured = false;
		try {
			isStructured = checkIfStructured(context, strType, strCSSType, null);

		} catch (Exception e) {
			throw new MatrixException(e);
		}
		return isStructured;
	}

	/**
	 * To Get filter Objects when user is Supplier or Manufacturer
	 * 
	 * @param context
	 * @param objectId
	 * @param mlBusinessObjectDataList
	 * @param strUser
	 * @return
	 * @throws MatrixException
	 */
	private Map filterObjects(Context context, String objectId, MapList mlBusinessObjectDataList, String strUser)
			throws MatrixException {

		Map mpFilterObject = null;
		HashMap AllFilterObjectsMap = new HashMap();

		String strNewKey = "";
		String strOldKey = "";
		String sKey = "";
		String strFormKey = "";

		String strKeyBeforeDot = "";

		StringList slFileterObjects = new StringList();
		StringList slObjects = new StringList();
		//		pgV3SecurityUtil_mxJPO objV3 = new pgV3SecurityUtil_mxJPO(context, null);
		Map mapFileterMap = new HashMap();
		try {
			if (null != mlBusinessObjectDataList && mlBusinessObjectDataList.size() > 0) {

				mpFilterObject = (Map) mlBusinessObjectDataList.get(0);
				Iterator itrMG = mpFilterObject.keySet().iterator();
				StringList slIds = new StringList(1);
				StringList slCount = new StringList(1);

				while (itrMG.hasNext()) {
					String strKey = (String) itrMG.next();
					if (strKey.endsWith(".id")) {
						strNewKey = strKey.substring(0, strKey.length() - 2);
						slIds = (StringList) mpFilterObject.get(strKey);
						ContextUtil.pushContext(context, strUser, "", "");
						HashMap programMap = new HashMap();
						programMap.put("parentId", objectId);
						programMap.put("documentList", slIds);
						String[] arg= JPO.packArgs(programMap);
						//						slCount = (StringList) objV3.checkEBPAccessForSl(context, objectId, slIds);


						slCount = (StringList)pgPDFViewHelper.executeIntermediatorClassMethod(context, "checkEBPAccessForSl", arg);

						ContextUtil.popContext(context);
						mapFileterMap.put(strNewKey, slCount);
					}
				}
				StringList strArrayKey = new StringList(1);
				itrMG = mapFileterMap.keySet().iterator();
				// NEW MAP WHILE
				while (itrMG.hasNext()) {
					strNewKey = (String) itrMG.next();
					strArrayKey = FrameworkUtil.split(strNewKey, ".");
					if (strArrayKey.size() > 0) {
						strKeyBeforeDot = (String) strArrayKey.get(0);
						strKeyBeforeDot = strKeyBeforeDot + ".";
					}
					slFileterObjects = (StringList) mapFileterMap.get(strNewKey);

					if (slFileterObjects.size() <= 0) {
						continue;
					} else {
						// OLD MAP WHILE
						// Add by DSM(Sogeti) for Code Optmization
						int index = 0;
						for (int i = slFileterObjects.size(); i > 0; i--) {
							// Modified by DSM(Sogeti) for Code Optmization
							// Modified for 2018x Upgrade STARTS
							index = Integer.parseInt(slFileterObjects.get(i - 1));
							// Modified for 2018x Upgrade ENDS

							Iterator itrObject = mpFilterObject.keySet().iterator();
							while (itrObject.hasNext()) {
								strOldKey = (String) itrObject.next();
								StringList slOldValue = (StringList) mpFilterObject.get(strOldKey);
								if ((strOldKey.indexOf(strNewKey) != -1 || strOldKey.indexOf(strKeyBeforeDot) != -1)
										&& slOldValue.size() > 0) {
									slOldValue.removeElementAt(index);
								}
								AllFilterObjectsMap.put(strOldKey, slOldValue);
							}
						}

					}
				}
			}
		} catch (Exception e) {
			throw new MatrixException(e);
		}
		return AllFilterObjectsMap;
	}

	/**
	 *
	 * @param context
	 * @param specType
	 * @param propertyFile
	 * @param languageStr
	 * @return
	 * @throws Exception
	 */
	private String getRestrictionType(Context context, String specType, String propertyFile, String languageStr)
			throws Exception {
		String sRestrictionType = DomainConstants.EMPTY_STRING;
		if (specType.equals(pgV3Constants.TYPE_PGFORMULATEDMATERIAL)
				|| specType.equals(pgV3Constants.TYPE_PGMAKINGINSTRUCTIONS)
				|| specType.equals(pgV3Constants.TYPE_PGBASEFORMULA)
				|| specType.equals(pgV3Constants.TYPE_PGFORMULATEDPRODUCT)
				|| specType.equals(pgV3Constants.TYPE_PGCONSUMERDESIGNBASIS)) {
			sRestrictionType = i18nNow.getI18nString("emxCPN.Type.HighRestrctionMsg", propertyFile, languageStr);
		} // Modified by DSM(Sogeti) - Fix for 2018x.2  Defect 29136 - Starts
		else if (specType.equals(pgV3Constants.TYPE_PGTESTMETHOD)
				|| specType.equals(pgV3Constants.TYPE_PGQUALITYSPECIFICATION)
				|| specType.equals(pgV3Constants.TYPE_PGSTACKINGPATTERN)
				|| specType.equals(pgV3Constants.TYPE_TESTMETHOD))
		{
			// Modified by DSM(Sogeti) - Fix for 2018x.2  Defect 29136 - Ends
			sRestrictionType = i18nNow.getI18nString("emxCPN.Type.UIMsg", propertyFile, languageStr);
		} else {
			sRestrictionType = i18nNow.getI18nString("emxCPN.Type.GenericRestrctionMsg", propertyFile, languageStr);
		}
		return sRestrictionType;
	}

	/**
	 *
	 * @param context
	 * @param sPackingLevel
	 * @return
	 */
	public String getEnvPackingLevel(Context context, String objId) throws Exception {
		StringList objectSelects = new StringList(2);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGLEVEL);
		String sPackingLevel = "";
		StringList relSelects = new StringList(1);
		String typePattern = pgV3Constants.TYPE_PGENVIRONMENTALCHARACTERISTIC;
		DomainObject doObj = DomainObject.newInstance(context, objId);
		MapList objList = doObj.getRelatedObjects(context, CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, typePattern,
				objectSelects, relSelects, false, true, (short) 1, null, null, 0);
		if (objList != null && objList.size() > 0) {
			for (int i = 0; i < objList.size(); i++) {
				Map mMap = (Map) objList.get(0);
				sPackingLevel = (String) mMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGLEVEL);
				if (sPackingLevel == null) {
					sPackingLevel = "";
				}
			}
		}
		return sPackingLevel;
	}

	/**
	 *
	 * @param context
	 * @param strListValue
	 * @return
	 * @throws Exception
	 */
	public String getSortedValue(Context context, String strListValue) throws Exception {

		try {
			StringList slValues = FrameworkUtil.split(strListValue, "|");
			slValues.sort();
			strListValue = FrameworkUtil.join(slValues, "|");
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			return strListValue;
		}
	}

	/**
	 *
	 * @param context
	 * @param         objectId,strPDFViewType, sInPath
	 * @return MapList
	 * @throws Exception
	 */
	public MapList appendedGenDocList(Context context, String objectId, String strPDFViewType, String sInPath)
			throws MatrixException {
		MapList returnList = new MapList();

		try {

			String strTempDirectory = sInPath;
			String strRelReferenceDoc = PropertyUtil.getSchemaProperty(context,"relationship_ReferenceDocument");
			DomainObject domainobject = DomainObject.newInstance(context, objectId);
			String strTechSpecType = (String) domainobject.getInfo(context, DomainConstants.SELECT_TYPE);
			MapList newPIList = new MapList();
			MapList newPROSList = new MapList();
			MapList pgSPSList = new MapList();

			if (strPDFViewType.equals(pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING)
					&& (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strTechSpecType)
							|| pgV3Constants.TYPE_PGMASTERFINISHEDPRODUCT.equals(strTechSpecType)
							|| pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strTechSpecType))) {
				newPIList = pgPIorPROSList(context, objectId, pgV3Constants.TYPE_PGPACKINGINSTRUCTIONS);
				newPROSList = pgPIorPROSList(context, objectId, pgV3Constants.TYPE_PGPROCESSSTANDARD);
				logger.log(Level.INFO,"the value newPIList",newPIList);
				logger.log(Level.INFO,"the value newPROSList",newPROSList);

			}
			pgSPSList = pgSPSListFromTransportUnitTable(context, objectId);
			BusinessObject bo = null;
			int i = 1;
			if (newPIList.size() > 0) {
				String format = "";
				Map sec1Map = new HashMap();
				for (Iterator iterator = newPIList.iterator(); iterator.hasNext();) {
					sec1Map = (Map) iterator.next();
					Map newMap = mapForGenDoc(context, sec1Map, strTempDirectory);
					if (!newMap.isEmpty()) {
						newMap.put("sortColumn", "" + i);
						returnList.add(newMap);
						i++;
					}
				}
			}
			if (pgSPSList.size() > 0) {
				String format = "";
				Map sec1Map = new HashMap();
				for (Iterator iterator = pgSPSList.iterator(); iterator.hasNext();) {
					sec1Map = (Map) iterator.next();
					Map newMap = mapForGenDoc(context, sec1Map, strTempDirectory);
					if (!newMap.isEmpty()) {
						newMap.put("sortColumn", "" + i);
						returnList.add(newMap);
						i++;
					}
				}
			}

			if (newPROSList.size() > 0) {
				Map sec1Map = new HashMap();
				for (Iterator iterator = newPROSList.iterator(); iterator.hasNext();) {
					sec1Map = (Map) iterator.next();
					Map newMap = mapForGenDoc(context, sec1Map, strTempDirectory);
					if (!newMap.isEmpty()) {
						newMap.put("sortColumn", "" + i);
						returnList.add(newMap);
						i++;
					}
				}
			}
			logger.log(Level.INFO,"the value returnList",returnList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnList.sort("sortColumn", pgPDFViewConstants.CONST_ASCENDING, "integer");
		return returnList;
	}
	
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Starts
	/**
	 *
	 * @param context
	 * @param         objectId,strPDFViewType, sInPath
	 * @return MapList
	 * @throws Exception
	 */
	public MapList appendedGenDocListForATSType(Context context, String objectId, String strPDFViewType, String sInPath)
			throws MatrixException {
		MapList returnList = new MapList();

		try {

			String strTempDirectory = sInPath;
			DomainObject domainobject = DomainObject.newInstance(context, objectId);
			String strTechSpecType = (String) domainobject.getInfo(context, DomainConstants.SELECT_TYPE);
			String strTechSpecRev = (String) domainobject.getInfo(context, DomainConstants.SELECT_REVISION);
			MapList newPIList = new MapList();
			MapList newPROSList = new MapList();
			MapList pgSPSList = new MapList();
			
			Map sec1Map = new HashMap();
			sec1Map.put(DomainConstants.SELECT_ID, objectId);
			sec1Map.put(DomainConstants.SELECT_TYPE, strTechSpecType);
			sec1Map.put(DomainConstants.SELECT_REVISION, strTechSpecRev);
			
			MapList newMap = mapForGenDocATS(context, sec1Map, strTempDirectory);
			if (!newMap.isEmpty()) {
				returnList.addAll(newMap);
			}
			logger.log(Level.INFO,"appendedGenDocListForATSType ::::newMap::::::",newMap);
		} catch (Exception e) {

		}
		return returnList;
	}
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Ends
	/**
	 *
	 * @param context
	 * @param         objectId,strType
	 * @return MapList
	 * @throws Exception
	 */
	public static MapList pgPIorPROSList(Context context, String objectId, String strType) throws MatrixException {
		MapList returnList = new MapList();
		Map Argsmap = new HashMap();
		try {
			Argsmap.put("objectId", objectId);
			Argsmap.put("parentRelName", "relationship_PartSpecification,relationship_pgApprovedSupplierList");
			String[] arg = JPO.packArgs(Argsmap);
			BusinessObject bo = null;
			//			pgIPMProductData_mxJPO productSpecs = new pgIPMProductData_mxJPO(context, arg);
			//			MapList mlSpecificatins = (MapList) productSpecs.getDocuments(context, arg);
			MapList mlSpecificatins = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getDocuments", arg);
			if (mlSpecificatins.size() > 0) {
				Map secMap = new HashMap();
				for (Iterator iterator = mlSpecificatins.iterator(); iterator.hasNext();) {
					secMap = (Map) iterator.next();
					String strObjType = (String) secMap.get(DomainConstants.SELECT_TYPE);
					if (strObjType.equals(strType)) {
						returnList.add(secMap);
					}
				}
				returnList.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
				returnList.addSortKey(DomainConstants.SELECT_REVISION, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
				returnList.sort();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnList;
	}

	/**
	 *
	 * @param context
	 * @param objectId
	 * @return MapList
	 * @throws Exception
	 */
	public static MapList pgSPSListFromTransportUnitTable(Context context, String objectId) throws MatrixException {
		MapList returnList = new MapList();
		try {
			DomainObject masterObj = DomainObject.newInstance(context, objectId);
			MapList objList = null;
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			StringList relSelects = new StringList();
			String relPattern = CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC;

			String typePattern = pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC;
			objList = masterObj.getRelatedObjects(context, relPattern, typePattern, objectSelects, relSelects, true,
					true, (short) 2, null, null, 0);

			if (BusinessUtil.isNotNullOrEmpty(objList)) {
				Map objMap = new HashMap();
				Map objSPSMap = new HashMap();
				String strTUCharacteristic = "";
				String strConfType = pgV3Constants.TYPE_PGSTACKINGPATTERN;

				DomainObject tucDomainObject = null;
				StringList objSelects = new StringList();
				MapList mlStackingPatterns = null;

				for (int i = 0; i < objList.size(); i++) {

					objMap = (Map) objList.get(i);
					strTUCharacteristic = (String) objMap.get(DomainConstants.SELECT_ID);
					tucDomainObject = DomainObject.newInstance(context, strTUCharacteristic);
					mlStackingPatterns = new MapList();
					objSelects.add(DomainObject.SELECT_ID);
					objSelects.add(DomainObject.SELECT_NAME);
					objSelects.add(DomainObject.SELECT_REVISION);
					objSelects.add(DomainObject.SELECT_TYPE);
					mlStackingPatterns = tucDomainObject.getRelatedObjects(context,
							DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, strConfType, objSelects, null, true, true,
							(short) 1, null, null);

					if (mlStackingPatterns != null && !mlStackingPatterns.isEmpty()) {
						for (int index = 0; index < mlStackingPatterns.size(); index++) {
							objSPSMap = (Map) mlStackingPatterns.get(index);
							if (!returnList.contains(objSPSMap)) {
								returnList.add(objSPSMap);
							}
						}

						returnList.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
						returnList.addSortKey(DomainConstants.SELECT_REVISION, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
						returnList.sort();

					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnList;
	}

	/**
	 *
	 * @param context
	 * @param         objMap,strTempDirectory,sInPath
	 * @return Map
	 * @throws Exception
	 */
	public static Map mapForGenDoc(Context context, Map objMap, String strTempDirectory) throws MatrixException {
		String format = "";
		Map newMap = new HashMap();
		DomainObject newObjDO = null;
		Map sec1Map = new HashMap();
		Pattern relPattern = new Pattern("");
		relPattern.addPattern(
				PropertyUtil.getSchemaProperty(context, CommonDocument.SYMBOLIC_relationship_ReferenceDocument));
		StringList typeSelects = new StringList(1);
		typeSelects.add(CommonDocument.SELECT_ID);
		StringList relSelects = new StringList(1);
		relSelects.add(CommonDocument.SELECT_RELATIONSHIP_ID);
		String strFooter = "";
		String strObjName = (String) objMap.get(DomainConstants.SELECT_NAME);
		String strObjid = (String) objMap.get(DomainConstants.SELECT_ID);

		String strObjRev = (String) objMap.get(DomainConstants.SELECT_REVISION);
		String objfileName = "LR_" + strObjName + "." + strObjRev;
		String objectWhere = "name==" + objfileName + " && revision==Rendition";
		try {
			strFooter = getGenDocFooter(context, strObjid);

			newObjDO = DomainObject.newInstance(context, strObjid);

			MapList docList = newObjDO.getRelatedObjects(context, relPattern.getPattern(), "*", typeSelects, relSelects,
					false, true, (short) 1, objectWhere, null, null, null, null);
			String id = "";
			BusinessObject bo = null;
			if (docList != null && docList.size() > 0) {
				id = (String) ((Hashtable) docList.get(0)).get("id");
			}
			String fileName = "";
			if (!id.equals("")) {
				Map checkoutMap = CommonDocument.checkout(context, new StringList(id), "download");
				String[] fileNames = (String[]) checkoutMap.get("fileNames");
				String[] formats = (String[]) checkoutMap.get("formats");
				String[] locks = (String[]) checkoutMap.get("locks");
				String[] paths = (String[]) checkoutMap.get("paths");
				format = formats[0];
				fileName = fileNames[0];
				bo = new BusinessObject(id);
				bo.checkoutFile(context, false, format, fileName, strTempDirectory);

				newMap.put("name", strObjName);
				newMap.put("fileName", fileName);
				newMap.put("Footer", strFooter);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newMap;
	}
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Starts
	/**
	 *
	 * @param context
	 * @param         objMap,strTempDirectory,sInPath
	 * @return Map
	 * @throws Exception
	 */
	public static MapList mapForGenDocATS(Context context, Map objMap, String strTempDirectory) throws MatrixException {
		String format = "";

		MapList newMapList = new MapList();
		DomainObject newObjDO = null;
		String strFooter = "";
		String strObjName = (String) objMap.get(DomainConstants.SELECT_NAME);
		String strObjid = (String) objMap.get(DomainConstants.SELECT_ID);
		String strObjRev = (String) objMap.get(DomainConstants.SELECT_REVISION);

		try {
			strFooter = getGenDocFooter(context, strObjid);

			newObjDO = DomainObject.newInstance(context, strObjid);
			String id = strObjid;
			BusinessObject bo = null;

			String fileName = "";
			if (!id.equals("")) {
				Map checkoutMap = CommonDocument.checkout(context, new StringList(id), "download");
				String[] fileNames = (String[]) checkoutMap.get("fileNames");
				String[] formats = (String[]) checkoutMap.get("formats");
				String[] locks = (String[]) checkoutMap.get("locks");
				String[] paths = (String[]) checkoutMap.get("paths");
				format = formats[0];
				fileName = fileNames[0];
				StringList fileNamessl = new StringList();
				bo = new BusinessObject(id);
				for(String files : fileNames) {
					if(files.endsWith(".pdf")) {
						fileName = files;
						bo.checkoutFile(context, false, format, fileName, strTempDirectory);						
						Map newMap = new HashMap();
						newMap.put("name", strObjName);
						newMap.put("fileName", fileName);
						newMap.put("Footer", strFooter);
						newMapList.add(newMap);
					}
				}
			}
		} catch (Exception e) {

		}
		return newMapList;
	}
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Ends
	/**
	 *
	 * @param context
	 * @param strTempDirectory
	 * @return MapList
	 * @throws Exception
	 */

	public static void clearTempFolder(Context context, String strTempDirectory) throws Exception {
		java.io.File folderDelete = new java.io.File(strTempDirectory);
		if (folderDelete.list().length == 0) {
			folderDelete.delete();
		} else {
			// list all the directory contents
			String files[] = folderDelete.list();
			for (String temp : files) {
				// construct the file structure
				java.io.File fileDelete = new java.io.File(folderDelete, temp);
				fileDelete.delete();
			}
			// check the directory again, if empty then delete it
			if (folderDelete.list().length == 0) {
				folderDelete.delete();
			}
		}
	}

	/**
	 *
	 * @param context
	 * @param         strPath,fileName,sInPath
	 * @return
	 * @throws Exception
	 */
	public static boolean transferToFTP(Context context, String strPath, String fileName, String sInPath)
			throws Exception {

		boolean bReturn = false;
		FTPClient ftp = new FTPClient();
		ftp.connect(FTP_HOST_NAME);
		if (!ftp.login(FTP_USER_NAME, EncryptCrypto.decryptString(FTP_PASSWORD))) {
			ftp.logout();
		}
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
		}

		if (FTPReply.isPositiveCompletion(reply)) {
			System.out.println("connnecting......");
		}
		String strTemp = sInPath;
		String strTempFolderName = strTemp.substring(13, strTemp.length());
		strTemp = strTemp.substring(1, strTemp.length());
		strTemp = "/" + strTemp;
		sInPath = strTemp;
		mxFtp ftpClient = new mxFtp();
		// create folders on ftp server
		ftpClient.connect(FTP_PROTOCOL, FTP_HOST_NAME, null, FTP_USER_NAME, EncryptCrypto.decryptString(FTP_PASSWORD),
				FTP_INPUT_FOLDER, false);
		if (!ftpClient.isDir(strTempFolderName)) {
			ftpClient.mkdir(strTempFolderName);
		}
		ftpClient.close();
		ftpClient.disconnect();
		boolean bWrkDir = ftp.changeWorkingDirectory(sInPath);
		InputStream in = new FileInputStream(strPath + java.io.File.separator + fileName);
		ftp.setFileType(ftp.BINARY_FILE_TYPE);
		boolean Store = ftp.storeFile(fileName, in);
		bReturn = Store;
		in.close();
		ftp.logout();
		ftp.disconnect();
		return bReturn;
	}

	/**
	 *
	 * @param context
	 * @param objectId
	 * @return String
	 * @throws Exception
	 */
	public static String getGenDocFooter(Context context, String strObjId) throws Exception {
		String strFooter = "P&amp;G AUTHORIZED Effective Date #STAMP_VARIABLE# Printed #CURRENT_DATE# ";
		try {
			TimeZone tz = Calendar.getInstance().getTimeZone();
			String strPrintedDate = new SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance(tz).getTime());
			String systemDateFormat = eMatrixDateFormat.getEMatrixDateFormat();
			String languageStr = context.getSession().getLanguage();
			String strComment = i18nNow.getI18nString("emxCPN.PDF.COMMENTNOTFORUSE", "emxCPN", languageStr);

			DomainObject newObjDO = DomainObject.newInstance(context, strObjId);
			String strObjState = (String) newObjDO.getInfo(context, DomainConstants.SELECT_CURRENT);
			String obsoleteDate = "";

			String effectivityDate = newObjDO.getAttributeValue(context, ProductData.ATTRIBUTE_EFFECTIVITY_DATE);
			String StrObjAttributeValue = newObjDO.getAttributeValue(context, "Status");
			String StrObjState = newObjDO.getInfo(context, "current");
			String returnDate = "";
			Date newtmpDate = null;
			SimpleDateFormat formatter = new SimpleDateFormat(systemDateFormat);
			DateFormat formatter1 = new SimpleDateFormat(systemDateFormat);
			if (effectivityDate != null && effectivityDate.trim().length() > 0) {
				Date tmpDate = formatter.parse(effectivityDate);
				formatter = new SimpleDateFormat("yyyy-MM-dd");
				returnDate = formatter.format(tmpDate);
			}

			// If object state is obsolete replace the stamp with that state
			if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strObjState)) {
				obsoleteDate = (String) newObjDO.getInfo(context, "state[pgV3Constants.STATE_OBSOLETE].actual");
				if (obsoleteDate != null && obsoleteDate.trim().length() > 0) {
					newtmpDate = (Date) formatter1.parse(obsoleteDate);
					returnDate = formatter.format(newtmpDate);
				}
				strFooter = strFooter.replace("AUTHORIZED Effective Date", "OBSOLETE");
				strFooter = strFooter.replace("#STAMP_VARIABLE#", returnDate);

				if (StrObjAttributeValue.equalsIgnoreCase("PLANNING")) {
					// When the object status is PLANNING, add an extraline
					strFooter = strFooter.replace("P&amp;G", strComment + "\n" + "P&amp;G");
				}
			} else if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(StrObjState)) {
				// Object is in Release state, replace the *tag* with Effectivity Date
				strFooter = strFooter.replace("#STAMP_VARIABLE#", returnDate);
				if (StrObjAttributeValue.equalsIgnoreCase("PLANNING")) {
					// When the object status is PLANNING, add an extraline
					strFooter = strFooter.replace("P&amp;G", strComment + "\n" + "P&amp;G");

				}
			}

			strFooter = strFooter.replace("#CURRENT_DATE#", strPrintedDate);
		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			return strFooter;
		}
	}

	/**
	 * This method returns the maplist of Country clearance data which will be
	 * displayed in pdf generated
	 * 
	 * @param context
	 * @param strObjectId
	 * @return MapList
	 */
	public MapList getCountryOfSalesDataListForFCPDF(Context context, DomainObject dmoObject) throws Exception {
		long startTime = new Date().getTime();
		Map countryOfSalesmap = new HashMap();
		MapList mlCountryOfSales = new MapList();
		MapList mlCountryOfSalesTemp = new MapList();
		try {
			Map programMap = new HashMap();
			programMap.put("parentOID", dmoObject.getId(context));
			String[] methodargs = JPO.packArgs(programMap);
			long startUITime = new Date().getTime();
			//			pgCountriesOfSale_mxJPO pgCOS = new pgCountriesOfSale_mxJPO(context, methodargs);

			// get the country clearance data
			//			mlCountryOfSalesTemp = pgCOS.getPgCountryClearanceData(context, methodargs);
			mlCountryOfSalesTemp  = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgCountriesOfSale", "getPgCountryClearanceData", methodargs);
			long UIMethod = new Date().getTime();
			if (mlCountryOfSalesTemp != null && !mlCountryOfSalesTemp.isEmpty() && mlCountryOfSalesTemp.size() > 0) {
				Iterator itrCountryCler = mlCountryOfSalesTemp.iterator();
				long startIteratingTime = new Date().getTime();
				while (itrCountryCler.hasNext()) {
					Map mpCC = (Map) itrCountryCler.next();
					countryOfSalesmap.put("Country", mpCC.get(DomainConstants.SELECT_NAME));
					countryOfSalesmap.put("OverallClearance",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGOVERALLCLEARANCESTATUS + "]"));
					countryOfSalesmap.put("PSRAApproveStatus",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGPSRAAPPROVALSTATUS + "]"));
					countryOfSalesmap.put("ClearanceNumber",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCTNUMBER + "]"));
					countryOfSalesmap.put("RegStatus",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGREGISTRATIONSTATUS + "]"));
					countryOfSalesmap.put("RegExpirDate",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGREGISTRATIONENDDATE + "]"));
					countryOfSalesmap.put("PlantRestriction",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGPLANTRESTRICTION + "]"));
					countryOfSalesmap.put("ClearanceComments",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCLEARANCECOMMENT + "]"));
					//Added by DSM(Sogeti)-2018x.2 for PDF Views defect - 29254 - Starts
					countryOfSalesmap.put("CountryProductRegistrationNumber",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOUNTRYPRODUCTREGISTRATIONNUMBER + "]"));
					//Added by DSM(Sogeti)-2018x.2 for PDF Views defect - 29254 - Ends
					countryOfSalesmap.put("ProductRegClassification",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_PGPRODUCTREGULATORYCLASSIFICATION + "]"));
					//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements - Starts
					countryOfSalesmap.put("PackingSite",
							mpCC.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_PGPACKINGSITE + "]"));
					countryOfSalesmap.put("ManufacturingSite",
							mpCC.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_PGMANUFACTURINGSITE + "]"));
					//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements - Ends

					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (19144,11470,11471,11472,19145,19146) - Starts
					countryOfSalesmap.put("RegistrationRenewalLeadTime",
							mpCC.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_PGREGISTRATIONRENEWALLEADTIME + "]"));
					countryOfSalesmap.put("RegistrationRenewalStatus",
							mpCC.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_PGREGISTRATIONRENEWALSTATUS + "]"));
					//Added by DSM(Sogeti)-2018x.3 for PDF Views Requirements (19144,11470,11471,11472,19145,19146) - Ends
					//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirement #34364 : Starts
					countryOfSalesmap.put("RegisteredProductName",
							mpCC.get("attribute[" + pgV3Constants.ATTRIBUTE_REGISTEREDPRODUCTNAME + "]"));
					//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirement #34364 : Ends
					mlCountryOfSales.add(countryOfSalesmap);
					countryOfSalesmap = new HashMap();
				}
				long EndIteratingTime = new Date().getTime();
				System.out.println(
						"Total Time has taken for Iterating the data is-->" + (EndIteratingTime - startIteratingTime));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return mlCountryOfSales;
		}
		long endTime = new Date().getTime();
		System.out.println(
				"Total Time has taken by the  getCountryOfSalesDataListForFCPDF Method is-->" + (endTime - startTime));
		return mlCountryOfSales;
	}

	public String connectFPPToRenditionobj(Context context, String objectId) throws MatrixException {
		String instId = "";
		long startTime = new Date().getTime();
		try {

			DomainObject domFPPObject = DomainObject.newInstance(context, objectId);
			//Modified by DSM for PDF views 2018x.3 Defect - 24078 : Starts 
			StringList slbusSelect = new StringList(4);
			slbusSelect.add(DomainConstants.SELECT_NAME);
			slbusSelect.add(DomainConstants.SELECT_REVISION);
			slbusSelect.add(DomainConstants.SELECT_PROJECT);
			slbusSelect.add(DomainConstants.SELECT_ORGANIZATION);
			//Modified by DSM for PDF views 2018x.3 Defect - 24078 : Ends 
			String cmd = "";
			String strTypePattern = pgV3Constants.TYPE_PGIPMDOCUMENT;
			Map<String, String> attrInfoMap = domFPPObject.getInfo(context, slbusSelect);
			String instType = strTypePattern;
			String instName = (String) attrInfoMap.get(DomainConstants.SELECT_NAME);
			String strFPPRev = (String) attrInfoMap.get(DomainConstants.SELECT_REVISION);
			//Added by DSM for PDF views 2018x.3 Defect - 24078 : Starts 
			String strProject = (String) attrInfoMap.get(DomainConstants.SELECT_PROJECT);
			String strOrganization = (String) attrInfoMap.get(DomainConstants.SELECT_ORGANIZATION);
			//Added by DSM for PDF views 2018x.3 Defect - 24078 : Ends 
			String strFPPName = "LR_" + instName + "." + strFPPRev;
			String strWhere = "revision==Rendition && name=='" + strFPPName + "'";

			MapList fppDoclist = domFPPObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT,
					strTypePattern, new StringList(DomainConstants.SELECT_ID), new StringList(), false, true, (short) 1,
					strWhere, null, 0);
			if (null != fppDoclist && fppDoclist.size() > 0) {

				Map objectMap = (Map) fppDoclist.get(0);
				instId = (String) objectMap.get(DomainConstants.SELECT_ID);

			} else {

				String instRev = "Rendition";
				String policy = pgV3Constants.POLICY_PGIPMDOCUMENT;
				String vault = pgV3Constants.VAULT_ESERVICEPRODUCTION;
				String tnrStr = "'" + instType + "' '" + strFPPName + "' '" + instRev + "'";
				cmd = "add bus " + tnrStr + " policy '" + policy + "' vault '" + vault + "'";
				cmd = cmd.toString();
				SelectList selects = new SelectList(1);
				selects.add(DomainConstants.SELECT_ID);
				MapList renditionExist = DomainObject.findObjects(context, instType, strFPPName, instRev,
						DomainConstants.QUERY_WILDCARD, vault, null, false, selects);
				if (renditionExist.size() == 0) {
					MqlUtil.mqlCommand(context, cmd);
				}
				MapList doclist = DomainObject.findObjects(context, strTypePattern, strFPPName, instRev, "*", vault,
						null, false, selects);
				if (doclist.size() != 0) {
					Map objectMap = (Map) doclist.get(0);
					instId = (String) objectMap.get(DomainConstants.SELECT_ID);
					DomainRelationship.connect(context, objectId, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT, instId,
							true);
				}
			}
			//Added by DSM(Sogeti)-2018x.3 Defect #24078 - Starts
			if(UIUtil.isNotNullAndNotEmpty(instId)) {
				DomainObject domIPMdoc = DomainObject.newInstance(context, instId);
				domIPMdoc.setPrimaryOwnership(context, strProject, strOrganization);
			}
			//Added by DSM(Sogeti)-2018x.3 Defect #24078 - Ends			
		} catch (Exception e) {
			throw e;
		}
		long endTime = new Date().getTime();
		System.out.println("Total Time has taken by the connectFPPToRenditionobj Method is-->" + (endTime - startTime));
		return instId;
	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @throws Exception
	 */

	public void deleteFile(Context context, String args[]) throws Exception {
		try {

			if (args == null || args.length < 1 || args[0] == null || "".equals(args[0]) || "null".equals(args[0])) {
				throw new MatrixException("Not valid arguments!");
			}

			ContextUtil.pushContext(context);

			String strObjId = args[0];
			String strObjType = args[1];
			String strProductDataTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST);
			String strNonStructuredTypes = EnoviaResourceBundle.getProperty(context,
					pgPDFViewConstants.CONST_NON_STRUCT_TYPE_INCLUSION_LIST);
			strNonStructuredTypes = strNonStructuredTypes.replace(",pgDSOAffectedFPPList", "");
			String strTypePattern = pgV3Constants.TYPE_PGIPMDOCUMENT;
			StringList slbusSelect = new StringList(2);
			slbusSelect.add(DomainConstants.SELECT_NAME);
			slbusSelect.add(DomainConstants.SELECT_REVISION);

			if (strProductDataTypes.contains(strObjType) || strNonStructuredTypes.contains(strObjType)) {
				StringList typeSelects = new StringList(2);
				typeSelects.add(CommonDocument.SELECT_ID);
				typeSelects.add(pgV3Constants.SELECT_FILE_NAME);

				DomainObject domFPPObject = DomainObject.newInstance(context, strObjId);

				Map mapAttributeInfo = domFPPObject.getInfo(context, slbusSelect);
				String strRev = "";
				String strName = "";

				if (mapAttributeInfo != null) {
					strRev = (String) mapAttributeInfo.get(DomainConstants.SELECT_REVISION);
					strName = (String) mapAttributeInfo.get(DomainConstants.SELECT_NAME);

				}

				String fileName = "LR_" + strName.trim() + "." + strRev.trim();
				String objectWhere = "name=='" + fileName.trim() + "' && revision==Rendition";
				;

				MapList fppDoclist = domFPPObject.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT, strTypePattern, typeSelects, new StringList(),
						false, true, (short) 1, objectWhere, null, 0);
				if (null != fppDoclist && fppDoclist.size() > 0) {

					Map objectMap = (Map) fppDoclist.get(0);
					String strRefDocId = (String) objectMap.get(DomainConstants.SELECT_ID);

					CommonDocument domRefDocObject = new CommonDocument(strRefDocId);

					DomainObject doFPPObject = DomainObject.newInstance(context, strRefDocId);

					Map mpAttributeInfo = doFPPObject.getInfo(context, slbusSelect);
					String strRev1 = "";
					String strName1 = "";
					if (mpAttributeInfo != null) {
						strRev1 = (String) mpAttributeInfo.get(DomainConstants.SELECT_REVISION);
						strName1 = (String) mpAttributeInfo.get(DomainConstants.SELECT_NAME);

					}

					String fileName1 = strName1 + "." + strRev1;
					String strTempFileName = (String) objectMap.get(pgV3Constants.SELECT_FILE_NAME);

					if (!"".equalsIgnoreCase(strTempFileName) && null != strTempFileName) {
						domRefDocObject.unlock(context);
						domRefDocObject.deleteFile(context, strTempFileName, "generic");

					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}

	}

	/**
	 *
	 * @param context
	 * @param         objectId,strPDFViewType, sInPath
	 * @return MapList
	 * @throws Exception
	 */

	public MapList appendedGenDocListForDSOType(Context context, String objectId, String strPDFViewType,
			String strTempDirectory) throws MatrixException {

		MapList returnList = new MapList();
		try {
			String strRelReferenceDoc = pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT;
			DomainObject domainobject = DomainObject.newInstance(context, objectId);
			MapList pgSPSList = new MapList();
			pgSPSList = pgSPSListFromTransportUnitTableForDSOType(context, objectId);
			int i = 1;
			if (pgSPSList.size() > 0) {
				String format = "";
				Map sec1Map = new HashMap();
				for (Iterator iterator = pgSPSList.iterator(); iterator.hasNext();) {
					sec1Map = (Map) iterator.next();
					Map newMap = mapForGenDocForDSO(context, sec1Map, strTempDirectory);
					if (!newMap.isEmpty()) {
						newMap.put("sortColumn", "" + i);
						returnList.add(newMap);
						i++;
					}
				}
			}

			if (pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType)) {
				MapList newPIList = new MapList();
				newPIList = pgPIorPROSList(context, objectId, "pgPackingInstructions");
				BusinessObject bo = null;
				if (newPIList.size() > 0) {
					String format = "";
					Map sec1Map = new HashMap();
					for (Iterator iterator = newPIList.iterator(); iterator.hasNext();) {
						sec1Map = (Map) iterator.next();
						Map newMap = mapForGenDocForDSO(context, sec1Map, strTempDirectory);
						if (!newMap.isEmpty()) {
							newMap.put("sortColumn", "" + i);
							returnList.add(newMap);
							i++;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		returnList.sort("sortColumn", pgPDFViewConstants.CONST_ASCENDING, "integer");

		return returnList;
	}

	/**
	 *
	 * @param context
	 * @param objectId
	 * @return MapList
	 * @throws Exception
	 */
	public static MapList pgSPSListFromTransportUnitTableForDSOType(Context context, String objectId)
			throws MatrixException {
		MapList returnList = new MapList();
		try {
			DomainObject masterObj = DomainObject.newInstance(context, objectId);
			MapList objList = null;
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			StringList relSelects = new StringList();
			String relPattern = pgV3Constants.RELATIONSHIP_PGTRANSPORTUNIT;
			String typePattern = pgV3Constants.TYPE_PGTRANSPORTUNITPART;
			objList = masterObj.getRelatedObjects(context, relPattern, typePattern, objectSelects, relSelects, false,
					true, (short) 1, null, null, 0);
			if (BusinessUtil.isNotNullOrEmpty(objList)) {
				Map objMap = new HashMap();
				Map objSPSMap = new HashMap();
				String strTUCharacteristic = "";
				DomainObject tucDomainObject = null;
				StringList objSelects = new StringList();
				MapList mlStackingPatterns = null;
				String strRel = pgV3Constants.RELATIONSHIP_PARTSPECIFICATION;
				String strType = pgV3Constants.TYPE_PGSTACKINGPATTERN;

				for (int i = 0; i < objList.size(); i++) {
					objMap = (Map) objList.get(i);
					strTUCharacteristic = (String) objMap.get(DomainConstants.SELECT_ID);
					tucDomainObject = DomainObject.newInstance(context, strTUCharacteristic);
					mlStackingPatterns = new MapList();
					objSelects.add(DomainObject.SELECT_ID);
					objSelects.add(DomainObject.SELECT_NAME);
					objSelects.add(DomainObject.SELECT_REVISION);
					objSelects.add(DomainObject.SELECT_TYPE);
					mlStackingPatterns = tucDomainObject.getRelatedObjects(context, strRel, strType, objSelects, null,
							false, true, (short) 1, null, null);
					if (mlStackingPatterns != null && !mlStackingPatterns.isEmpty()) {
						for (int index = 0; index < mlStackingPatterns.size(); index++) {
							objSPSMap = (Map) mlStackingPatterns.get(index);
							if (!returnList.contains(objSPSMap)) {
								returnList.add(objSPSMap);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnList;
	}

	/**
	 *
	 * @param context
	 * @param         objMap,strTempDirectory,sInPath
	 * @return Map
	 * @throws Exception
	 */
	public Map mapForGenDocForDSO(Context context, Map objMap, String strTempDirectory) throws MatrixException {
		String format = "";
		Map newMap = new HashMap();
		DomainObject newObjDO = null;
		String strFooter = "";
		String relPattern = pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT;
		StringList typeSelects = new StringList(1);
		typeSelects.add(CommonDocument.SELECT_ID);
		StringList relSelects = new StringList(1);
		relSelects.add(CommonDocument.SELECT_RELATIONSHIP_ID);
		String strObjName = (String) objMap.get(DomainConstants.SELECT_NAME);
		String strObjid = (String) objMap.get(DomainConstants.SELECT_ID);

		String strObjRev = (String) objMap.get(DomainConstants.SELECT_REVISION);
		String objfileName = "LR_" + strObjName.trim() + "." + strObjRev.trim();
		String objectWhere = "name=='" + objfileName.trim() + "' && revision==Rendition";
		try {

			newObjDO = DomainObject.newInstance(context, strObjid);
			MapList docList = newObjDO.getRelatedObjects(context, relPattern, "*", typeSelects, relSelects, false, true,
					(short) 1, objectWhere, null, null, null, null);
			String id = "";
			BusinessObject bo = null;
			if (docList != null && docList.size() > 0) {
				id = (String) ((Hashtable) docList.get(0)).get("id");
			}
			
			String fileName = "";
			String sResult = "";
			if (!id.equals("")) {
				Map checkoutMap = CommonDocument.checkout(context, new StringList(id), "download");
				String[] fileNames = (String[]) checkoutMap.get("fileNames");
				String[] formats = (String[]) checkoutMap.get("formats");
				String[] locks = (String[]) checkoutMap.get("locks");
				String[] paths = (String[]) checkoutMap.get("paths");
				format = formats[0].trim();

				fileName = fileNames[0].trim();

				bo = new BusinessObject(id);
				bo.checkoutFile(context, false, format, fileName, strTempDirectory.trim());
				fileName = renameTempContentFile(fileName, strTempDirectory.trim(), strObjName);

				strTempPath = strTempDirectory;
				if (UIUtil.isNotNullAndNotEmpty(strFileNameCheck)) {
					strFileNameCheck = strFileNameCheck + ":" + fileName;
				} else {
					strFileNameCheck = fileName;
				}
				
				String fileName1 = strTempDirectory.trim();
				java.io.File pdfFileName = new java.io.File(fileName1);
				sResult = pdfFileName.getPath().trim();
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Starts
				String effDate = newObjDO.getInfo(context,pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
				String expDate = newObjDO.getInfo(context,pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE); 
				strFooter = getGenDocFooterForDSO(strObjName,strObjRev,effDate,expDate);
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Ends
				newMap.put("name", strObjName);
				newMap.put("fileName", fileName);
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Starts
				newMap.put("Footer", strFooter);
				//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Ends

     		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newMap;
	}
	
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Starts
	public static String getGenDocFooterForDSO(String strObjName,String strObjRev,String effDate,String expDate) throws Exception {
		StringBuffer sbFooterBuffer = new StringBuffer();
		Date today = null;
		String sCurrentDate = null;
		today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		sCurrentDate = dateFormat.format(today);
		final String CONST_PGAUTH =" P&G AUTHORIZED ";
		final String CONST_EFF = " Eff ";
        final String CONST_EXP = " Exp ";
        final String CONST_NA = "N/A";
        final String CONST_PRINT = "(Printed ";
        final String CONST_VALID = " valid for 24hrs) ";
		String ExpiryDate= getFormattedDate(expDate);
		String EffectiveDate = getFormattedDate(effDate);
		String strBusinessUseStamp = pgV3Constants.BUSINESS_USE_HYPHEN;
		sbFooterBuffer.append(strBusinessUseStamp).append(CONST_PGAUTH);
		sbFooterBuffer.append(strObjName).append(pgV3Constants.SYMBOL_DOT).append(strObjRev).append(CONST_EFF);
		if(UIUtil.isNotNullAndNotEmpty(EffectiveDate)) {
			sbFooterBuffer.append(EffectiveDate);
		} else {
			sbFooterBuffer.append(CONST_NA);
		}
		sbFooterBuffer.append(CONST_EXP);
		if(UIUtil.isNotNullAndNotEmpty(ExpiryDate)) {
			sbFooterBuffer.append(ExpiryDate);
		} else {
			sbFooterBuffer.append(CONST_NA);
		}
		sbFooterBuffer.append(CONST_PRINT).append(sCurrentDate).append(CONST_VALID);
		return sbFooterBuffer.toString();
	}
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views defects 57530  - Ends
	/**
	 *
	 * @param context
	 * @param         objectId,strPDFViewType, sInPath
	 * @return MapList
	 * @throws Exception
	 */
	public MapList appendedGenDocListForNonStructuredType(Context context, String objectId) throws MatrixException {
		MapList returnList = new MapList();
		try {
			String format = "";
			String strTempDirectory = context.createWorkspace();
			logger.info("_________________Workspace Dir______________:"+strTempDirectory);
			DomainObject domainobject = DomainObject.newInstance(context, objectId);

            // Cloud-GenDoc start
            StringList busSelects = new StringList();
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_REVISION);

            Map<Object, Object> objectMap = domainobject.getInfo(context, busSelects);
            String strObjType = (String) objectMap.get(DomainConstants.SELECT_TYPE);
            String strObjRev = (String) objectMap.get(DomainConstants.SELECT_REVISION);
            // Cloud-GenDoc end.

            String strObjName = (String) objectMap.get(DomainConstants.SELECT_NAME);
			String fileName = "";
			String strFooter = "";
			BusinessObject bo = null;
            // Cloud-GenDoc start
            Map<String, String> newMap = new HashMap<String, String>();
            // Cloud-GenDoc end.
			String sFormatGen = FORMAT_GENERIC;
			if (!objectId.equals("")) {

				Map checkoutMap = CommonDocument.checkout(context, new StringList(objectId), "download");

				String[] fileNames = (String[]) checkoutMap.get("fileNames");
				String[] formats = (String[]) checkoutMap.get("formats");
				String[] locks = (String[]) checkoutMap.get("locks");
				String[] paths = (String[]) checkoutMap.get("paths");

				strTempPath = strTempDirectory;
				bo = new BusinessObject(objectId);

				int nfileNames = fileNames.length;
				logger.info("----------------------------------------------------------------");
                logger.info("Number of CheckOut Files count:"+nfileNames);
                logger.info("----------------------------------------------------------------");

                // Cloud-GenDoc start
                String fileExtension;
                // Cloud-GenDoc end

				for (int i = 0; i < nfileNames; i++) {

                    newMap = new HashMap<String, String>();
					fileName = fileNames[i];
					format = formats[i];

                    // Cloud-GenDoc start
                    fileExtension = FilenameUtils.getExtension(fileName);
                    // Cloud-GenDoc end

					if (UIUtil.isNotNullAndNotEmpty(sFormatGen) && format.equalsIgnoreCase(sFormatGen)) {
						String strFilePath = strTempDirectory.trim() + java.io.File.separator + objectId;
						File fCheckoutFile = new File(strFilePath);

						if (!fCheckoutFile.exists()) {
							fCheckoutFile.mkdir();
						}

						bo.checkoutFile(context, false, format, fileName, strFilePath.trim());
						fileName = renameTempContentFile(fileName, strFilePath.trim(), strObjName);
						if (strFileNameCheck != null && !"".equals(strFileNameCheck)) {
							strFileNameCheck = strFileNameCheck + ":" + fileName;
						} else {
							strFileNameCheck = fileName;
						}

						newMap.put("name", strObjName);
						newMap.put("fileName", fileName);
						newMap.put("Footer", strFooter);
						newMap.put("strTempPath", strTempPath);

                        // Cloud-GenDoc start
                        newMap.put("filesCount", String.valueOf(nfileNames));
                        newMap.put("checkOutPath", strTempPath);

                        newMap.put("relativeCheckOutDir", fCheckoutFile.getParent());
                        newMap.put("relativeCheckOutDirName", fCheckoutFile.getParentFile().getName());

                        newMap.put("absoluteCheckOutDir", fCheckoutFile.getPath());
                        newMap.put("absoluteCheckOutDirName", fCheckoutFile.getName());
                        newMap.put("absoluteCheckOutFilePath", fCheckoutFile.getPath().concat(File.separator).concat(fileName));

                        newMap.put("fileExtension", fileExtension);
                        newMap.put(DomainConstants.SELECT_ID, objectId);
                        newMap.put(DomainConstants.SELECT_TYPE, strObjType);
                        newMap.put(DomainConstants.SELECT_REVISION, strObjRev);

                        // Cloud-GenDoc end

						returnList.add(newMap);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnList.sort("sortColumn", pgPDFViewConstants.CONST_ASCENDING, "integer");
		return returnList;
	}

	/**
	 * Method is to cleanup the temp files from server
	 *
	 * @param this method take string as argument. @ return void
	 *
	 */
	public void cleanTempFolder(String strPDFpath) throws Exception {
		try {
			File tempFile = new File(strPDFpath);
			if (tempFile.exists()) {
				if (tempFile.canWrite()) {
					tempFile.delete();
				}
			}
		} catch (Exception ex) {
			throw new MatrixException(ex);
		}
	}

	/**
	 * Method is to get connected primary and secondary Organization
	 * 
	 * @param Context
	 * @param strObjectId
	 * @param strType
	 * @param strPdfViewType
	 * @return Map containing info of primary and secondary organization
	 */
	private Map getPrimaryAndSecondaryOrganization(Context context, String strObjectId, String strType,
			String strPdfViewType) throws Exception {
		Map mPrimaryAndSecondaryOrganization = new HashMap();
		Map mMasterOrganization = new HashMap();
		Map mpPartOrg = new HashMap();
		String strMasterID = null;
		String strMasterLastId = null;

		StringList slSelect = new StringList(1);
		slSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.id");

		DomainObject doObjectid = null;
		DomainObject doMaster = null;
		try {
			if (validateString(strObjectId)) {
				doObjectid = DomainObject.newInstance(context, strObjectId);
				if ((pgPDFViewConstants.CONST_COMBINEDWITHMASTER).equals(strPdfViewType)
						&& (((pgV3Constants.TYPE_PGRAWMATERIAL).equals(strType))
								|| ((pgV3Constants.TYPE_PGFINISHEDPRODUCT).equals(strType))
								|| ((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY).equals(strType))
								|| ((pgV3Constants.TYPE_PGPACKINGMATERIAL).equals(strType)))) {
					Map mapObject = doObjectid.getInfo(context, slSelect);
					strMasterID = (String) mapObject
							.get("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.id");
					String[] arry = getLatestRelease(context, strMasterID);
					if (null != arry && arry.length > 1) {
						strMasterID = arry[0];
					}
					if (validateString(strMasterID)) {
						doMaster = DomainObject.newInstance(context, strMasterID);
						boolean isAccess = doMaster.checkAccess(context, (short) 0);
						if (isAccess) {
							mMasterOrganization = getOrganizationName(context, doMaster);
							mPrimaryAndSecondaryOrganization.put(
									"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.from["
											+ pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.name",
											mMasterOrganization.get("strPrimaryOrgName"));
							mPrimaryAndSecondaryOrganization.put(
									"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.from["
											+ pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION + "].to.name",
											mMasterOrganization.get("strSecOrgName"));
						} else {
							mPrimaryAndSecondaryOrganization.put(
									"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.from["
											+ pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.name",
											pgPDFViewConstants.CONST_NO_ACCESS);
							mPrimaryAndSecondaryOrganization.put(
									"from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.last.from["
											+ pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION + "].to.name",
											pgPDFViewConstants.CONST_NO_ACCESS);
						}
					}
				}
				mpPartOrg = getOrganizationName(context, doObjectid);
				mPrimaryAndSecondaryOrganization.put(DomainConstants.SELECT_ID, strObjectId);
				mPrimaryAndSecondaryOrganization.put(
						"from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.name",
						mpPartOrg.get("strPrimaryOrgName"));
				mPrimaryAndSecondaryOrganization.put(
						"from[" + pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION + "].to.name",
						mpPartOrg.get("strSecOrgName"));
				mPrimaryAndSecondaryOrganization.put(DomainConstants.SELECT_TYPE, strType);
			}
		} catch (Exception exception) {
			throw exception;
		}

		return mPrimaryAndSecondaryOrganization;
	}

	/**
	 * Method is to get connected mutipule primary and secondary Organization
	 * 
	 * @param Context
	 * @param DomainObject domPartObj
	 * @return Map containing info of mutipule primary and secondary organization
	 */
	private Map getOrganizationName(Context context, DomainObject domPartObj) throws Exception {
		StringList slObjSelects = new StringList(1);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		MapList mlOrg = new MapList();
		Map mpOrg = new HashMap();
		Map mpFinalOrg = new HashMap();
		String strRelName = null;
		String strPrimaryOrgName = null;
		String strSecOrgName = null;
		StringBuffer sbPrimaryOrgName = new StringBuffer();
		StringBuffer sbSecOrgName = new StringBuffer();
		try {
			mlOrg = domPartObj.getRelatedObjects(context, // context
					pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + ","
					+ pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION, // rel pattern
					pgV3Constants.TYPE_PGPLIORGANIZATIONCHANGEMANAGEMENT, // type pattern
					slObjSelects, // objectSelects
					null, // relationshipSelects
					false, // getTo - Get Parent Data
					true, // getFrom - Get Child Data
					(short) 1, // recurseToLevel
					null, // objectWhere
					null);// relationshipWhere
			if (mlOrg != null && mlOrg.size() > 0) {
				// Added by DSM(Sogeti)-2015x.4 for PDF Views (Defect ID-13545) -Starts
				mlOrg.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
				mlOrg.sort();
				// Added by DSM(Sogeti)-2015x.4 for PDF Views (Defect ID-13545) -Ends
				Iterator itrOrg = mlOrg.iterator();
				while (itrOrg.hasNext()) {
					mpOrg = (Map) itrOrg.next();
					strRelName = (String) (mpOrg.get("relationship"));
					if (strRelName != null && pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION.equals(strRelName)) {
						strPrimaryOrgName = (String) (mpOrg.get(DomainConstants.SELECT_NAME));
						if (sbPrimaryOrgName.length() > 0)
							sbPrimaryOrgName.append(", ").append(strPrimaryOrgName);
						else
							sbPrimaryOrgName.append(strPrimaryOrgName);
					}
					if (strRelName != null && pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION.equals(strRelName)) {
						strSecOrgName = (String) (mpOrg.get(DomainConstants.SELECT_NAME));
						if (sbSecOrgName.length() > 0) {
							sbSecOrgName.append("<BR/>").append(strSecOrgName);
						} else {
							sbSecOrgName.append(strSecOrgName);
						}
					}
				}
			}
			mpFinalOrg.put("strPrimaryOrgName", sbPrimaryOrgName.toString());
			mpFinalOrg.put("strSecOrgName", sbSecOrgName.toString());
		} catch (Exception exception) {
			throw exception;
		}
		return mpFinalOrg;
	}

	/**
	 * Method is check if the file is present on FTP server
	 * 
	 * @param FTPClient ftpGenDoc
	 * @param String    detination file name which needs to check
	 * @param String    objectId
	 * @return boolean true or false
	 */
	private boolean isFileExists(FTPClient ftpGenDoc, String destFileName, String objectId) throws Exception {
		boolean isFileExist = false;
		String[] files = ftpGenDoc.listNames();
		int size = files.length;
		for (int i = 0; i < size; i++) {
			if (destFileName.equals(files[i])) {
				isFileExist = true;
				break;
			}
		}
		return isFileExist;
	}

	/**
	 * @Description Method is to mark the CA for gendoc generation through SO user
	 * @param Context
	 * @param String  args[]
	 * @return String alert message to get displayed if the CA did not get marked
	 *         for gendoc generation
	 */
	public String markForGenDocGeneration(Context context, String args[]) throws Exception {
		ContextUtil.pushContext(context);
		StringBuffer sbMessage = new StringBuffer();
		try {
			if (args != null) {
				boolean isCAMarkedToProcessGenDoc = false;
				String selectedID = null;
				DomainObject domObjectID = null;
				Map mObjectDetails = null;
				String strType = null;
				String strName = null;
				String strCurrent = null;
				String strPGFailedReason = null;
				String strPGErrorClassification = null;
				String strEnginuityAuthored = null;
				String strStage = DomainConstants.EMPTY_STRING;
				String strErrorRenderGenDocCA = UINavigatorUtil.getI18nString("emxCPN.Alerts.ErrorRenderGenDocCA",
						CONST_EMXCPN, context.getLocale().getLanguage());
				String strRenderGenDocCA = UINavigatorUtil.getI18nString("emxCPN.Alerts.RenderGenDocCA", CONST_EMXCPN,
						context.getLocale().getLanguage());
				String strCANotPresentForObject = UINavigatorUtil.getI18nString("emxCPN.Alerts.CANotPresentForObject",
						CONST_EMXCPN, context.getLocale().getLanguage());
				String strErrorRenderGenDocObject = UINavigatorUtil.getI18nString("emxCPN.Alerts.ErrorRenderGenDocPart",
						CONST_EMXCPN, context.getLocale().getLanguage());
				//APOLLO 2018x.6 A10-751, A10-758, A10-776 Criteria CUSTOMIZATIONS - added to enable Change Management for Criteria - Starts
				String strGenDocForCriteriaError = EnoviaResourceBundle.getProperty(context, CONST_EMXCPN, context.getSession().getLocale(), "emxCPN.Alerts.RenderGenDocCA.InValidTypes");
				//APOLLO 2018x.6 A10-751, A10-758, A10-776 Criteria CUSTOMIZATIONS - added to enable Change Management for Criteria - Ends
				// DSM (DS) 2018x.0 ALM 24369 Invalid(Key) Error message if tried to Resend
				// GenDoc who has already GenDoc connected - STARTS
				// String strGenDocAlreadyPresent =
				// UINavigatorUtil.getI18nString("emxCPN.Alerts.AlreadyGenDocPrsent",
				// CONST_EMXCPN, context.getLocale().getLanguage());
				String strGenDocAlreadyPresent = UINavigatorUtil.getI18nString("emxCPN.Alerts.AlreadyGenDocPresent",
						CONST_EMXCPN, context.getLocale().getLanguage());
				// DSM (DS) 2018x.0 ALM 24369 Invalid(Key) Error message if tried to Resend
				// GenDoc who has already GenDoc connected - ENDS

				StringList slSelect = new StringList(4);
				slSelect.addElement(DomainConstants.SELECT_TYPE);
				slSelect.addElement(DomainConstants.SELECT_CURRENT);
				slSelect.addElement(DomainConstants.SELECT_NAME);
				slSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
				// Added by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Starts
				slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
				slSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
				// Added by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Ends
				StringList slAttribute = new StringList(4);
				slAttribute.addElement("attribute[" + pgV3Constants.ATTRIBUTE_PGFAILEDREASON + "]");
				slAttribute.addElement("attribute[" + pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION + "]");
				slAttribute.addElement(pgV3Constants.SELECT_POLICY);
				slAttribute.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
				String strMEPSEPPolicy = null;
				String strPolicy = null;
				String strOrgSorce = null;

				int iArguments = args.length;
				for (int i = 0; i < iArguments; i++) {
					selectedID = args[i];
					StringList splitSelectId = FrameworkUtil.split(selectedID, "|");
					selectedID = (String) splitSelectId.get(0);
					domObjectID = DomainObject.newInstance(context, selectedID);
					mObjectDetails = domObjectID.getInfo(context, slSelect);
					if (mObjectDetails != null && !mObjectDetails.isEmpty()) {
						strType = (String) mObjectDetails.get(DomainConstants.SELECT_TYPE);
						strName = (String) mObjectDetails.get(DomainConstants.SELECT_NAME);
						strCurrent = (String) mObjectDetails.get(DomainConstants.SELECT_CURRENT);

						// Added for Check if object is Enginuity Authored will not mark CA - Starts
						strEnginuityAuthored = (String) mObjectDetails
								.get(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
						// Added by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Starts
						strStage = (String) mObjectDetails.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
						strOrgSorce = (String) mObjectDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
						// Added by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Ends
						// Modified by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Starts
						// if(UIUtil.isNotNullAndNotEmpty(strEnginuityAuthored) &&
						// CONST_FALSE.equalsIgnoreCase(strEnginuityAuthored)){
						if (UIUtil.isNotNullAndNotEmpty(strOrgSorce)
								&& pgV3Constants.DSM_ORIGIN.equalsIgnoreCase(strOrgSorce)) {
							if ((UIUtil.isNotNullAndNotEmpty(strEnginuityAuthored)
									&& pgPDFViewConstants.CONST_FALSE.equalsIgnoreCase(strEnginuityAuthored))
									|| (UIUtil.isNotNullAndNotEmpty(strEnginuityAuthored)
											&& pgV3Constants.CONST_TRUE.equalsIgnoreCase(strEnginuityAuthored)
											&& ("Production".equalsIgnoreCase(strStage)))) {

								// Modified by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Ends
								// Added for Check if object is Enginuity Authored will not mark CA - Ends

								if (strType.equalsIgnoreCase(pgV3Constants.TYPE_CHANGEACTION)) {

									if (pgV3Constants.STATE_PENDING.equalsIgnoreCase(strCurrent)
											|| pgV3Constants.STATE_INWORK.equalsIgnoreCase(strCurrent)) {
										// alert gendoc cannot be generated since state is pending or inwork
										sbMessage.append(strName).append(" :").append(strErrorRenderGenDocCA)
										.append(strCurrent).append("<br>");
									} else {
										// give alert CA is marked for gendoc
										domObjectID.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC,
												pgV3Constants.CONST_TRUE);
										sbMessage.append(strName).append(strRenderGenDocCA).append("\n");
									}
								} else if (pgV3Constants.STATE_PRELIMINARY.equalsIgnoreCase(strCurrent)) {
									// alert GCAS is in preliminary state
									sbMessage.append(strName).append(strErrorRenderGenDocObject).append(strCurrent)
									.append("<br>");

								} else {
									Map mAttribute = domObjectID.getInfo(context, slAttribute);

									if (mAttribute != null && !mAttribute.isEmpty()) {
										strPGFailedReason = (String) mAttribute
												.get("attribute[" + pgV3Constants.ATTRIBUTE_PGFAILEDREASON + "]");
										strPGErrorClassification = (String) mAttribute.get(
												"attribute[" + pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION + "]");
										strMEPSEPPolicy = pgV3Constants.POLICY_MANUFACTUREREQUIVALENT + ","
												+ pgV3Constants.POLICY_SUPPLIEREQUIVALENT;
										strPolicy = (String) mAttribute.get(pgV3Constants.SELECT_POLICY);
										strOrgSorce = (String) mAttribute
												.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
										// Added by DSM (Sogeti) for defect-11372 - Ends
										boolean isGenDocPresent = isGenDocPresent(context, domObjectID);

										if (isGenDocPresent) {
											// alert gendoc is already generated
											sbMessage.append(strName).append(strGenDocAlreadyPresent).append("<br>");
											if ("Rendition".equalsIgnoreCase(strPGErrorClassification)
													&& !strPGFailedReason.isEmpty()) {
												updateAttributeListOfObject(context, domObjectID);
											}
										} else {
											if (UIUtil.isNotNullAndNotEmpty(strMEPSEPPolicy)
													&& UIUtil.isNotNullAndNotEmpty(strPolicy)
													&& strMEPSEPPolicy.contains(strPolicy)
													&& UIUtil.isNotNullAndNotEmpty(strOrgSorce)
													&& pgV3Constants.DSM_ORIGIN.equals(strOrgSorce)) {
												domObjectID.setAttributeValue(context,
														pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgV3Constants.CONST_TRUE);
												updateAttributeListOfObject(context, domObjectID);
												sbMessage.append(strName).append(strRenderGenDocCA).append("<br>");
												continue;
											}
											isCAMarkedToProcessGenDoc = markCAtoProcessGenDoc(context, domObjectID);
											// alert CA is marked for gendoc
											if (isCAMarkedToProcessGenDoc) {
												sbMessage.append(strName).append(strRenderGenDocCA).append("<br>");
												if ("Rendition".equalsIgnoreCase(strPGErrorClassification)
														&& !strPGFailedReason.isEmpty()) {
													updateAttributeListOfObject(context, domObjectID);
												}
											} else {
												sbMessage.append(strName).append(strCANotPresentForObject)
												.append("<br>");
											}
										}
									}
								}
							}
						}
						//APOLLO 2018x.6 A10-751, A10-758, A10-776 Criteria CUSTOMIZATIONS - added to enable Change Management for Criteria - Starts
						else if (UIUtil.isNotNullAndNotEmpty(strType) && strType.equalsIgnoreCase(pgApolloConstants.TYPE_CRITERIA))
						{
							sbMessage.append(strName).append(strGenDocForCriteriaError).append("<br>");
						}
						//APOLLO 2018x.6 A10-751, A10-758, A10-776 Criteria CUSTOMIZATIONS - added to enable Change Management for Criteria - Ends
					}
				}
			} else {
				System.out.println("No object being selected");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}
		return sbMessage.toString();
	}

	/**
	 * @Description utility method to update attribute of a particular object
	 * @param Context      context
	 * @param DomainObject domObjectID
	 * @return void
	 */
	private void updateAttributeListOfObject(Context context, DomainObject domObjectID) throws Exception {
		Map hmUpdateAttribute = new HashMap();
		hmUpdateAttribute.put(pgV3Constants.ATTRIBUTE_PGFAILEDREASON, "");
		hmUpdateAttribute.put(pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION, "");
		hmUpdateAttribute.put(pgV3Constants.ATTRIBUTE_PGERRORDATE, "");
		try {
			domObjectID.setAttributeValues(context, hmUpdateAttribute);
		} catch (FrameworkException exception) {
			exception.printStackTrace();
		}

	}

	/**
	 * @Description This method is to get the CA which is connected to particular
	 *              object and mark it for gendoc generation
	 * @param Context      context
	 * @param DomainObject domObjectID
	 * @return boolean If CA gets marked then returns true or else false
	 */
	private boolean markCAtoProcessGenDoc(Context context, DomainObject domObjectID) throws Exception {
		boolean isCAPresent = false;
		StringList busSelects = new StringList(1);
		busSelects.add(DomainConstants.SELECT_ID);
		Map mapCA = null;
		String strID = null;
		// DSM (DS) 2018x.0 ALM 24281 UAT issue: Failure View : CA is not present to
		// Render GenDOc -STARTS
		// String strRelnWhereClause = "attribute[Requested Change] == \"For Release\"";

		String strObjectId = (String) domObjectID.getInfo(context, DomainConstants.SELECT_ID);
		Map proposedCAData = com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil
				.getChangeObjectsInProposed(context, busSelects, new String[] { strObjectId }, 1);

		/*
		 * MapList mlConnectedCAList = domObjectID.getRelatedObjects(context,
		 * pgV3Constants.RELATIONSHIP_AFFECTEDITEM+","+pgV3Constants.
		 * RELATIONSHIP_CHANGEAFFECTEDITEM, pgV3Constants.TYPE_CHANGEACTION, busSelects,
		 * // object Select null, // rel Select true, // to false, // from (short)1,
		 * null, // ob where strRelnWhereClause // rel where );
		 * 
		 * if(mlConnectedCAList != null && !mlConnectedCAList.isEmpty()){ int
		 * imlConnectedCAList = mlConnectedCAList.size(); for(int i=0 ;
		 * i<imlConnectedCAList ; i++){ mapCA = (Map) mlConnectedCAList.get(i); strID =
		 * (String) mapCA.get(DomainConstants.SELECT_ID); DomainObject domCAId =
		 * DomainObject.newInstance(context, strID); domCAId.setAttributeValue(context,
		 * pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, CONST_TRUE); isCAPresent = true; } }
		 */

		String strRequestedChange = EMPTY_STRING;
		ChangeAction caInstance = new ChangeAction();
		DomainObject domCAId = DomainObject.newInstance(context);
		MapList proposedchangeActionList = (MapList) proposedCAData.get(strObjectId);

		if (proposedchangeActionList.size() > 0) {
			Iterator itrCA = proposedchangeActionList.iterator();
			Map caMap = null;
			while (itrCA.hasNext()) {
				caMap = (Map) itrCA.next();
				if (ChangeConstants.TYPE_CHANGE_ACTION.equals((String) caMap.get(DomainConstants.SELECT_TYPE))) {
					strID = (String) caMap.get(DomainConstants.SELECT_ID);
					caInstance.setId(strID);
					strRequestedChange = caInstance.getRequestedChangeFromChangeAction(context, strObjectId, strID);
					if (BusinessUtil.isNotNullOrEmpty(strRequestedChange)
							&& pgV3Constants.For_Release.equals(strRequestedChange)) {
						domCAId.setId(strID);
						domCAId.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgV3Constants.CONST_TRUE);
						isCAPresent = true;
					}
				}
			}
		}

		// DSM (DS) 2018x.0 ALM 24281 UAT issue: Failure View : CA is not present to
		// Render GenDOc - ENDS
		return isCAPresent;
	}

	/**
	 * @Description This method is used to check wheather the gendoc is present for
	 *              a particular object
	 * @param Context      context
	 * @param DomainObject domObjectID
	 * @return boolean If Gendoc is present returns TRUE or else FALSE
	 */
	private boolean isGenDocPresent(Context context, DomainObject domObj) throws Exception {
		boolean isGenDocPresent = false;
		try {
			StringList typeSelects = new StringList(1);
			typeSelects.add(CommonDocument.SELECT_ID);
			typeSelects.add(pgV3Constants.SELECT_FILE_NAME);
			StringList relSelects = new StringList(1);
			relSelects.add(CommonDocument.SELECT_RELATIONSHIP_ID);
			StringList slbusSelect = new StringList(2);
			slbusSelect.add(DomainConstants.SELECT_NAME);
			slbusSelect.add(DomainConstants.SELECT_REVISION);

			Map mapAttributeInfo = domObj.getInfo(context, slbusSelect);
			String rev = (String) mapAttributeInfo.get(DomainConstants.SELECT_REVISION);
			String name = (String) mapAttributeInfo.get(DomainConstants.SELECT_NAME);
			String fileName = "LR_" + name.trim() + "." + rev.trim();
			String objectWhere = "name=='" + fileName.trim() + "' && revision==Rendition";
			MapList docList = domObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT,
					pgV3Constants.TYPE_PGIPMDOCUMENT, typeSelects, relSelects, false, true, (short) 1, objectWhere,
					null, null, null, null);
			if (docList != null && docList.size() > 0) {
				Map mapData = (Map) docList.get(0);
				String strTempFileName = (String) mapData.get(pgV3Constants.SELECT_FILE_NAME);
				if (UIUtil.isNotNullAndNotEmpty(strTempFileName)) {
					isGenDocPresent = true;
				}
			}
		} catch (Exception exception) {
			throw exception;
		}
		return isGenDocPresent;
	}

	/**
	 * @Description This method is used to mark CA true while CA get promoted from
	 *              In Work to In Approval state for gendoc generation
	 * @param Context context
	 * @param         String[] String args[]
	 * @return void
	 */
	public void markCAForGenDocGeneration(Context context, String args[]) throws Exception {

		String strObjectID = args[0];
		String strType = args[1];
		// Added by DSM 2015x.2 (Sogeti) for defect-8061 - Starts
		String strEnginuityAuthored = DomainConstants.EMPTY_STRING;
		String strStage = DomainConstants.EMPTY_STRING;
		String strRequestedChange = DomainConstants.EMPTY_STRING;
		// Added by DSM 2015x.2 (Sogeti) for defect-8061 - Ends
		if (pgV3Constants.TYPE_CHANGEACTION.equalsIgnoreCase(strType)) {
			ContextUtil.pushContext(context);
			try {
				System.out.println("markCAForGenDocGeneration:" + strObjectID);
				System.out.println("markCAForGenDocGeneration:" + strType);

				DomainObject domObjectCA = DomainObject.newInstance(context, strObjectID);
				System.out.println("markCAForGenDocGeneration:" + domObjectCA);
				String relationshipPattern = pgV3Constants.RELATIONSHIP_AFFECTEDITEM + ","
						+ pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM + ","
						+ pgV3Constants.RELATIONSHIP_IMPLEMENTEDITEM;

				StringBuffer sbTypePattern = new StringBuffer(250);
				sbTypePattern.append(EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST).trim());
				sbTypePattern.append(",");
				sbTypePattern
				.append(EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_NON_STRUCT_TYPE_INCLUSION_LIST).trim());

				StringList objSelects = new StringList(3);
				objSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
				objSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
				objSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);

				StringList relSelects = new StringList(1);
				relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_REQUESTEDCHANGE);

				// DSM [Sogeti] 2015x.4 : Updated code to only mark valid CA's, for GenDoc
				// process - Starts.
				// MapList mlGCASObject = domObjectCA.getRelatedObjects(context,
				// relationshipPattern, "*", false, true, (short)1, objSelects, null,
				// null,strRelnWhereClause, 0, null, sbPostTypePattern.toString(), null);
				// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
				// START
				ChangeAction caInstance = new ChangeAction(strObjectID);
				HashMap programMap=new HashMap();
				programMap.put("CAId", strObjectID);
				programMap.put("slSelect", objSelects);
				programMap.put("reqChang", pgV3Constants.For_Release);
				programMap.put("affectedRel", true);
				programMap.put("impRel", true);
				String[] strArgs =JPO.packArgs(programMap); 
				//				MapList mlGCASObject = pgDSMChangeUtil_mxJPO.getAffectedItemForCA(context, strObjectID, objSelects,
				//						pg.For_Release, true, true);
				MapList mlGCASObject =(MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgDSMChangeUtil", "getAffectedItemForCA", strArgs);
				/*
				 * MapList mlGCASObject = domObjectCA.getRelatedObjects(context,
				 * relationshipPattern, sbTypePattern.toString(), objSelects, // object Select
				 * relSelects, // rel Select false, // to true, // from (short)1, null, // ob
				 * where null // rel where );
				 */
				// DSM [Sogeti] 2015x.4 : Updated code to only mark valid CA's, for GenDoc
				// process - Ends.

				System.out.println("markCAForGenDocGeneration:" + mlGCASObject);
				String strAffectedItemId = EMPTY_STRING;
				// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
				// END

				if (null != mlGCASObject && !mlGCASObject.isEmpty()) {
					boolean isDSMTypeObject = false;

					for (Object object : mlGCASObject) {
						Map map = (Map) object;
						// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
						// START
						strAffectedItemId = map.get(SELECT_ID).toString();
						// DSM(DS) 2018x.0 ALM # 21048 - The CA for revised part cannot be promoted. -
						// Start
						strRequestedChange = (String) map.get(DomainConstants.ATTRIBUTE_REQUESTED_CHANGE);
						if (UIUtil.isNullOrEmpty(strRequestedChange)) {
							strRequestedChange = caInstance.getRequestedChangeFromChangeAction(context,
									strAffectedItemId, strObjectID);
						}
						// DSM(DS) 2018x.0 ALM # 21048 - The CA for revised part cannot be promoted. -
						// End
						// Modification to handle Change Management Data Model for P&G 2018x Upgrade -
						// END
						String strOriginatingSource = (String) map
								.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
						strEnginuityAuthored = (String) map.get(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
						strStage = (String) map.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
						// Modified by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Starts
						// if(UIUtil.isNotNullAndNotEmpty(strEnginuityAuthored) &&
						// !("True".equalsIgnoreCase(strEnginuityAuthored))){
						if (UIUtil.isNotNullAndNotEmpty(strRequestedChange)
								&& pgV3Constants.For_Release.equals(strRequestedChange)
								&& UIUtil.isNotNullAndNotEmpty(strOriginatingSource)
								&& pgV3Constants.DSM_ORIGIN.equals(strOriginatingSource)) {
							isDSMTypeObject = true;
							if (UIUtil.isNotNullAndNotEmpty(strEnginuityAuthored)
									&& "True".equalsIgnoreCase(strEnginuityAuthored)
									&& !"Production".equalsIgnoreCase(strStage)) {
								isDSMTypeObject = false;
							}
							break;
						}
						// }
						// Modified by DSM(Sogeti) 2015x.5.1 December Fix for(Defect Id #23001) : Ends
					}
					if (isDSMTypeObject) {
						domObjectCA.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgV3Constants.CONST_TRUE);
					}
				}
			} finally {
				ContextUtil.popContext(context);
			}
		}
	}

	/**
	 * @Description This method is used to unmark CA while CA get demoted from In
	 *              Approval to In work state to delete gendoc
	 * @param Context context
	 * @param         String[] String args[]
	 * @return void
	 */
	public void unmarkCAForGenDocGeneration(Context context, String args[]) throws Exception {
		String strObjectID = args[0];
		String strType = args[1];
		if (pgV3Constants.TYPE_CHANGEACTION.equalsIgnoreCase(strType)) {
			ContextUtil.pushContext(context);
			try {
				DomainObject domObjectCA = DomainObject.newInstance(context, strObjectID);
				domObjectCA.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgPDFViewConstants.CONST_FALSE);
			} finally {
				ContextUtil.popContext(context);
			}
		}
	}

	/**
	 * @Description This method is used to mark MEP true while MEP get promoted from
	 *              preliminary to Review state for gendoc generation
	 * @param Context context
	 * @param         String[] String args[]
	 * @return void
	 */
	public void markMEPForGenDocGeneration(Context context, String args[]) throws Exception {
		String strObjectID = args[0];
		ContextUtil.pushContext(context);
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectID)) {
				DomainObject domObject = DomainObject.newInstance(context, strObjectID);
				String fileNames = MqlUtil.mqlCommand(context,
						"print bus " + strObjectID + " select format.file.format dump |");
				if (UIUtil.isNotNullAndNotEmpty(fileNames) && fileNames.contains(FORMAT_GENERIC)) {
					domObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgV3Constants.CONST_TRUE);
				}
			}
		} finally {
			ContextUtil.popContext(context);
		}
	}

	/**
	 * @Description This method is used to mark SEP true while SEP get promoted from
	 *              preliminary to Review state for gendoc generation
	 * @param Context context
	 * @param         String[] String args[]
	 * @return void
	 */
	public void markSEPForGenDocGeneration(Context context, String args[]) throws Exception {
		String strObjectID = args[0];
		ContextUtil.pushContext(context);
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectID)) {
				DomainObject domObject = DomainObject.newInstance(context, strObjectID);
				String fileNames = MqlUtil.mqlCommand(context,
						"print bus " + strObjectID + " select format.file.format dump |");
				if (UIUtil.isNotNullAndNotEmpty(fileNames) && fileNames.contains(FORMAT_GENERIC)) {
					domObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgV3Constants.CONST_TRUE);
				}
			}
		} finally {
			ContextUtil.popContext(context);
		}
	}

	/**
	 * @Description This method is used to unmark MEP while MEP get demoted from
	 *              Review to Preliminary state to delete gendoc
	 * @param Context context
	 * @param         String[] String args[]
	 * @return void
	 */
	public void unmarkMEPForGenDocGeneration(Context context, String args[]) throws Exception {
		String strObjectID = args[0];
		ContextUtil.pushContext(context);
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectID)) {
				DomainObject domObject = DomainObject.newInstance(context, strObjectID);
				domObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgPDFViewConstants.CONST_FALSE);
			}
		} finally {
			ContextUtil.popContext(context);
		}
	}

	/**
	 * @Description This method is used to unmark SEP while SEP get demoted from
	 *              Review to Preliminary state to delete gendoc
	 * @param Context context
	 * @param         String[] String args[]
	 * @return void
	 */
	public void unmarkSEPForGenDocGeneration(Context context, String args[]) throws Exception {
		String strObjectID = args[0];
		ContextUtil.pushContext(context);
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectID)) {
				DomainObject domObject = DomainObject.newInstance(context, strObjectID);
				domObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, pgPDFViewConstants.CONST_FALSE);
			}
		} finally {
			ContextUtil.popContext(context);
		}
	}

	/**
	 * Added by DSM to reduce the repetitive code
	 * 
	 * @Description This method is getting called from Render PDF method
	 * @param Context context
	 * @return Map contains strHTMLWithData and strXMLTicketContent
	 */
	public Map getHTMLAndXMLContent(Context context, String objectId, String strRelPattern, String strTypePattern,
			Map BusinessObjectSelectableMap, Map selectRelatedMap, int FIRST_LEVEL, String ftpInputFolder,
			boolean ifHavingBOM, boolean ifHavingPlants, boolean ifHavingCountryOfSales, String strPDFViewKind,
			String strContextUser, boolean ifType, String strTechSpecType, String ftpOutputFolder,
			String strPDFfileName, Vector vHTMLInputFiles, Map mapAdlibTicketData, String strPolicy,
			String strPDFViewKindExtension, boolean ifTogetPrimaryAndSecondaryOrganization) throws Exception {
		//Added by DSM-2018x.6 for PDF Views Req#36434: Starts
		addTypeSelectable(context,BusinessObjectSelectableMap,strTechSpecType);
		//Added by DSM-2018x.6 for PDF Views Req#36434: End
		Map mpReturnMap = null;
		Map consolidatedMap = new HashMap();
		//Modify Code Refactoring
		Map mPrimaryAndSecondaryOrg = new HashMap();
		Map selectMapManufacturingLocation = new HashMap();
		StringBuffer sbPDFViewKind = null;
		consolidatedMap = getAllDataMap(context, objectId, strRelPattern, strTypePattern, BusinessObjectSelectableMap,
				selectRelatedMap, ifHavingPlants, ifHavingCountryOfSales,
				strPDFViewKind);
		if (ifTogetPrimaryAndSecondaryOrganization) {
			//Modify Code Refactoring
			//			mPrimaryAndSecondaryOrg = new HashMap();
			//Modify Code Refactoring
			//			selectMapManufacturingLocation = new HashMap();
			if (ifType) {
				mPrimaryAndSecondaryOrg = getPrimaryAndSecondaryOrganization(context, objectId, strTechSpecType,
						strPDFViewKind);
			} else {
				mPrimaryAndSecondaryOrg = getPrimaryAndSecondaryOrganization(context, objectId, "", strPDFViewKind);
				if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy)) {
					strRelPattern = pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY;
				} else if (pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(strPolicy)) {
					strRelPattern = pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY;
				}
				selectMapManufacturingLocation = getManufacturingLocation(context, objectId, strRelPattern);
				consolidatedMap.putAll(selectMapManufacturingLocation);
			}
			consolidatedMap.putAll(mPrimaryAndSecondaryOrg);
		}
		sbPDFViewKind = new StringBuffer();
		sbPDFViewKind.append(strPDFViewKind);
		sbPDFViewKind.append(File.separator);
		sbPDFViewKind.append(strPDFViewKind);
		sbPDFViewKind.append(strPDFViewKindExtension);
		strPDFViewKind = sbPDFViewKind.toString();

		String strHTMLWithData = htmlFormatter.renderFormattedHTML(context, objectId, consolidatedMap, strPDFViewKind,
				strContextUser);
		strHTMLWithData = htmlFormatter.formatTables(strHTMLWithData);

		Map mAdlibTicketDetails = new HashMap();
		String strXMLTicketContent = null;
		if (UIUtil.isNotNullAndNotEmpty(ftpInputFolder)) {
			mAdlibTicketDetails = (Map) getAdlibTicketXMLContent(context, objectId, ftpInputFolder, ftpOutputFolder,
					strPDFfileName, vHTMLInputFiles, mapAdlibTicketData, strPDFViewKind);
			strXMLTicketContent = (String) mAdlibTicketDetails.get("AdlibTicketDetails");
            // Cloud Gen-Doc start -
            if (mAdlibTicketDetails.containsKey("checkOutFileDetailsInJSONFormat")) {
                String checkOutFileDetailsInJSONFormat = (String) mAdlibTicketDetails.get("checkOutFileDetailsInJSONFormat");
                mapAdlibTicketData.put("checkOutFileDetailsInJSONFormat", checkOutFileDetailsInJSONFormat);
            }
            // Cloud Gen-Doc end
		}

		mpReturnMap = new HashMap();
		mpReturnMap.put(pgPDFViewConstants.CONST_STRHTMLWITHDATA, strHTMLWithData);
		mpReturnMap.put(pgPDFViewConstants.CONST_STRXMLTICKETCONTENT, strXMLTicketContent);
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc - Starts
		mpReturnMap.put(pgPDFViewConstants.CONST_STRADLIB, mAdlibTicketDetails);
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc - Ends
		//Modify Code Refactoring
		consolidatedMap.clear();
		selectMapManufacturingLocation.clear();
		mPrimaryAndSecondaryOrg.clear();
		//mAdlibTicketDetails.clear();
		return mpReturnMap;
	}

	/**
	 * 
	 * @param context
	 * @param businessObjectSelectableMap
	 * @param strTechSpecType
	 */
	//Added by DSM-2018x.6 for PDF Views Req#36434: Starts
	private void addTypeSelectable(Context context, Map businessObjectSelectableMap, String strTechSpecType) {
		
			StringList typeBusSelectable = pgIPMPDFViewDataSelectable_mxJPO.getTypeDataBusSelectable(context, strTechSpecType);
			StringList busSelect = (StringList) businessObjectSelectableMap.get("busSelect");
			busSelect.addAll(typeBusSelectable);
		
		
	}
	//Added by DSM-2018x.6 for PDF Views Req#36434: End

	/**
	 * This method loads all abbreviated types in map used for DS email notification
	 * and table column
	 * 
	 * @return HashMap
	 * @throws Exception
	 */
	public String loadShortType(Context context, String strTypeName) throws Exception {
		HashMap<String, String> hmShortMap = new HashMap<String, String>();
		String strAttrValue = null;
		String strType = null;
		String strLongTypeName = null;
		String strShortTypeName = null;
		Attribute attribute = null;
		StringList slShortName = new StringList();
		BusinessObject boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
				pgV3Constants.PGTYPEMAPPING, "-", pgV3Constants.VAULT_ESERVICEPRODUCTION);
		if (boConfig.exists(context)) {
			attribute = boConfig.getAttributeValues(context, pgV3Constants.ATTRIBUTE_PGTYPEMAPPING);
			strAttrValue = attribute.getValue().trim();
			StringList slTypeName = FrameworkUtil.split(strAttrValue, ",");
			int iTypeNameCount = slTypeName.size();
			for (int iCount = 0; iCount < iTypeNameCount; iCount++) {
				strType = slTypeName.get(iCount).toString();
				if (UIUtil.isNotNullAndNotEmpty(strType)) {
					slShortName = FrameworkUtil.split(strType, ":");
					if (!slShortName.isEmpty()) {
						strLongTypeName = slShortName.get(0).toString();
						strShortTypeName = slShortName.get(1).toString();
						if (strLongTypeName != null && strShortTypeName != null) {
							hmShortMap.put(strLongTypeName, strShortTypeName);
						}
					}
				}
			}
		}
		return hmShortMap.get(strTypeName);
	}

	/**
	 * This method gets Source field value of Reference Document Table.It is created
	 * to avoid StringIndexOutOfBound Exception given by "showConnectionOfRefDoc"
	 * method of emxCPNCommonDocumentUIBase JPO.
	 * 
	 * @return StringList
	 * @throws Exception
	 */
	public StringList showConnectionOfRefDoc(Context context, String[] args) throws Exception {
		DomainObject dmObj = null;
		Map refDocMap = null;
		Map charMap = null;
		MapList charList = new MapList();
		StringList strSelectList = new StringList();
		StringList strCharList = new StringList();
		StringList strFinalList = new StringList();
		String strRefDocId = "";
		String strType = "";
		String strFinalType = "";
		String strSource = "";
		try {
			String strRelReferenceDocument = PropertyUtil.getSchemaProperty(context, "relationship_ReferenceDocument");
			String strTypeCharacteristics = PropertyUtil.getSchemaProperty(context, "type_Characteristic");
			strSource = CPNUIUtil.getProperty(context, "emxCPN.Table.Label.Direct");
			HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
			strSelectList.addElement(DomainConstants.SELECT_ID);
			strSelectList.addElement(DomainConstants.SELECT_TYPE);
			MapList objectList = (MapList) argumemtMap.get("objectList");
			if (objectList != null && !objectList.isEmpty()) {
				Iterator objectListIterator = objectList.iterator();
				while (objectListIterator.hasNext()) {
					refDocMap = (Map) objectListIterator.next();
					strRefDocId = (String) refDocMap.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strRefDocId)) {
						dmObj = DomainObject.newInstance(context, strRefDocId);
						boolean isAccess = dmObj.checkAccess(context, (short) AccessConstants.cRead);
						if (isAccess) {
							charList = dmObj.getRelatedObjects(context, strRelReferenceDocument, strTypeCharacteristics,
									strSelectList, null, false, true, (short) 1, null, null, 0);
							if (charList != null && !charList.isEmpty()) {
								Iterator charListIterator = charList.iterator();
								while (charListIterator.hasNext()) {
									charMap = (Map) charListIterator.next();
									strType = (String) charMap.get(DomainConstants.SELECT_TYPE);
									strType = strType.trim();
									if (!strCharList.contains(strType)) {
										strCharList.addElement(strType);
									}
								}
								if (strCharList.size() > 1) {
									for (int i = 0; i < strCharList.size(); i++) {
										strFinalType = strFinalType + "," + strCharList.get(i);
									}
									strFinalType = strFinalType.substring(1, strFinalType.length());
								} else {
									strFinalType = (String) strCharList.get(0);
								}
								strFinalList.addElement(strFinalType);
								strCharList.clear();
								strFinalType = "";
							} else {
								strFinalList.addElement(strSource);
							}
						} else {
							strFinalList.addElement(pgPDFViewConstants.CONST_NO_ACCESS);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FrameworkException();
		}
		return strFinalList;
	}

	/**
	 * This method returns valid SAP type for PDF Views
	 * 
	 * @param strPDFViewType        - Represent which PDF View.
	 * @param strSAPType            - Actual SAP Type
	 * @param strObjectType         - Actual object Type
	 * @param strObjectType         - Structured type list
	 * @param strNonStructuredTypes - Non Structured type list
	 * @param strOriginatingSource  - Object originating source
	 * @return SAP Type to be used for PDF Views
	 * @throws Exception
	 */
	private static String getSAPTypeForPDF(String strPDFViewType, String strSAPType, String strObjectType,
			String strProductDataTypes, String strNonStructuredTypes, String strOriginatingSource) throws Exception {
		if (strProductDataTypes.contains(strObjectType) || (strNonStructuredTypes.contains(strObjectType)
				&& !(strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL)))) {
			if (pgPDFViewConstants.CONST_ALLINFO.equals(strPDFViewType) || pgV3Constants.PDFVIEW_GENDOC.equals(strPDFViewType)
					|| (pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType)
							&& pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType))
					|| pgV3Constants.PQR_VIEW.equals(strPDFViewType)) {
				if (validateString(strSAPType)) {
					strSAPType = "(" + strSAPType + ")";
				} else {
					strSAPType = "N/A";
					strSAPType = "(" + strSAPType + ")";
				}
			} else if ((pgPDFViewConstants.CONST_WAREHOUSE.equals(strPDFViewType))
					&& pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strObjectType)) {
				strSAPType = DomainConstants.EMPTY_STRING;
			} else if ((pgPDFViewConstants.CONST_COMBINEDWITHMASTER.equals(strPDFViewType))
					&& (((pgV3Constants.TYPE_PGRAWMATERIAL.equals(strObjectType))
							&& strOriginatingSource.contains(pgV3Constants.CSS_SOURCE))
							|| pgV3Constants.TYPE_PGMASTERRAWMATERIAL.equals(strObjectType))) {
				strSAPType = DomainConstants.EMPTY_STRING;
			}
		} else {
			if (pgPDFViewConstants.CONST_ALLINFO.equals(strPDFViewType) && validateString(strSAPType)) {
				strSAPType = "(" + strSAPType + ")";
			} else {
				strSAPType = DomainConstants.EMPTY_STRING;
			}
		}
		return strSAPType;
	}

	/**
	 * This method returns object ID of latest released Master
	 * 
	 * @returns String
	 * @throws Exception
	 */
	public static String[] getLatestRelease(Context context, String strIPS) throws Exception {
		StringList obselect = new StringList(3);
		obselect.add(pgV3Constants.SELECT_ID);
		obselect.add(pgV3Constants.SELECT_REVISION);
		obselect.add(pgV3Constants.SELECT_CURRENT);
		String strid = "";
		String revision = "";
		String current = "";
		String[] latestrelase = new String[2];
		if (UIUtil.isNotNullAndNotEmpty(strIPS)) {
			DomainObject dom = DomainObject.newInstance(context, strIPS);
			MapList mlRevsioninfo = dom.getRevisionsInfo(context, obselect, new StringList());
			mlRevsioninfo.addSortKey(DomainObject.SELECT_REVISION, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
			mlRevsioninfo.sort();
			int size = mlRevsioninfo.size();
			for (int i = size - 1; i >= 0; i--) {
				Map mapRevsioninfo = (Map) mlRevsioninfo.get(i);
				strid = (String) mapRevsioninfo.get(pgV3Constants.SELECT_ID);
				revision = (String) mapRevsioninfo.get(pgV3Constants.SELECT_REVISION);
				current = (String) mapRevsioninfo.get(pgV3Constants.SELECT_CURRENT);
				if (pgV3Constants.STATE_PART_RELEASE.equalsIgnoreCase(current)) {
					latestrelase[0] = strid;
					latestrelase[1] = revision;
					return latestrelase;
				}
			}
		}
		return null;
	}

	/**
	 * @Desc Method to call the iText PDF code.
	 * @Author Added by DSM(Sogeti)-2015x.5 to use iText for PDF Views
	 * @param Context
	 * @param strHTMLWithData  - HTML file
	 * @param strObjectId      - Object Id
	 * @param strPDFfileName   - PDF file name
	 * @param mapPDFTicketData - object and pdf header/footer details
	 * @throws Exception
	 */
	private String generateiTextPDF(Context context, String strHTMLWithData, String strObjectId, String strPDFfileName,
			Map mapPDFTicketData) throws Exception {
		Map Argsmap = new HashMap();
		Argsmap.put(pgPDFViewConstants.CONST_STRHTMLWITHDATA, strHTMLWithData);
		Argsmap.put("objectId", strObjectId);
		Argsmap.put("strPDFfileName", strPDFfileName);
		Argsmap.put("mapPDFTicketData", mapPDFTicketData);
		String[] strArgs = JPO.packArgs(Argsmap);
		//		String strFileInfo = (String) JPO.invoke(context, "pgIPMGeneratePDFView", null, "createPdf", strArgs,
		//				String.class);
		pgIPMGeneratePDFView_mxJPO pig=new pgIPMGeneratePDFView_mxJPO(context, strArgs);
		String strFileInfo = (String) pig.createPdf(context, strArgs);
		//Modify Code Refactoring
		//mapPDFTicketData.clear();
		return strFileInfo;
	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public String deleteTempFolder(Context context, String args[]) throws Exception {
		String strPathToDel = args[0];
		boolean bDel = deleteDir(new File(strPathToDel));
		if (bDel) {
			return pgV3Constants.CONST_TRUE;
		} else {
			return pgPDFViewConstants.CONST_FALSE;
		}
	}

	/**
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		boolean success = false;
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public MapList appendedGenDocList(Context context, String[] args) throws Exception {
		HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
		String strObjectId = (String) argumemtMap.get("objectId");
		String strPDFViewType = (String) argumemtMap.get("strPDFViewType");
		String strTempDirectory = (String) argumemtMap.get("strTempDirectory");
		return (appendedGenDocList(context, strObjectId, strPDFViewType, strTempDirectory));
	}

	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */

	public MapList appendedGenDocListForDSOType(Context context, String[] args) throws Exception {
		HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
		String strObjectId = (String) argumemtMap.get("objectId");
		String strPDFViewType = (String) argumemtMap.get("strPDFViewType");
		String strTempDirectory = (String) argumemtMap.get("strTempDirectory");
		return (appendedGenDocListForDSOType(context, strObjectId, strPDFViewType, strTempDirectory));
	}
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Starts
	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */

	public MapList appendedGenDocListForATSType(Context context, String[] args) throws Exception {
		HashMap argumemtMap = (HashMap) JPO.unpackArgs(args);
		String strObjectId = (String) argumemtMap.get("objectId");
		String strPDFViewType = (String) argumemtMap.get("strPDFViewType");
		String strTempDirectory = (String) argumemtMap.get("strTempDirectory");
		return (appendedGenDocListForATSType(context, strObjectId, strPDFViewType, strTempDirectory));
	}
	//Modified by DSM(Sogeti) - 2022x.6 fix for PDF Views Req 47667  - Ends
	/**
	 * Method to delete the Reference Document object connected on Demoting a CA
	 * 
	 * @param Context context
	 * @param String  args[]
	 */
	public void deleteGenDocRefDocObj(Context context, String args[]) throws Exception {
		String strRev = DomainConstants.EMPTY_STRING;
		String strName = DomainConstants.EMPTY_STRING;
		String strRefDocId = DomainConstants.EMPTY_STRING;
		String fileName = DomainConstants.EMPTY_STRING;
		String objectWhere = DomainConstants.EMPTY_STRING;
		Map mapAttributeInfo = null;
		Map objectMap = null;
		StringList refDocSelects = null;
		DomainObject doRefDocObject = null;
		DomainObject doParentObject = null;
		MapList mlRefDocs = null;
		try {
			if (args == null || args.length < 1 || args[0] == null || "".equals(args[0]) || "null".equals(args[0])) {
				throw new MatrixException("Not valid arguments!");
			}
			ContextUtil.pushContext(context);
			String strObjId = args[0];
			String strObjType = args[1];
			String strProductDataTypes = EnoviaResourceBundle.getProperty(context, pgPDFViewConstants.CONST_TYPE_INCLUSION_LIST);
			String strNonStructuredTypes = EnoviaResourceBundle.getProperty(context,
					pgPDFViewConstants.CONST_NON_STRUCT_TYPE_INCLUSION_LIST);
			strNonStructuredTypes = strNonStructuredTypes.replace(",pgDSOAffectedFPPList", "");
			StringList slbusSelect = new StringList(2);
			slbusSelect.add(DomainConstants.SELECT_NAME);
			slbusSelect.add(DomainConstants.SELECT_REVISION);
			if (strProductDataTypes.contains(strObjType) || strNonStructuredTypes.contains(strObjType)) {
				refDocSelects = new StringList(2);
				refDocSelects.add(CommonDocument.SELECT_ID);
				refDocSelects.add(pgV3Constants.SELECT_FILE_NAME);
				doParentObject = DomainObject.newInstance(context, strObjId);
				mapAttributeInfo = doParentObject.getInfo(context, slbusSelect);
				if (mapAttributeInfo != null) {
					strRev = (String) mapAttributeInfo.get(DomainConstants.SELECT_REVISION);
					strName = (String) mapAttributeInfo.get(DomainConstants.SELECT_NAME);

				}

				fileName = "LR_" + strName.trim() + "." + strRev.trim();
				objectWhere = "name=='" + fileName.trim() + "' && revision==Rendition";
				;
				mlRefDocs = doParentObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT,
						pgV3Constants.TYPE_PGIPMDOCUMENT, refDocSelects, new StringList(), false, true, (short) 1,
						objectWhere, null, 0);
				if (null != mlRefDocs && mlRefDocs.size() > 0) {
					objectMap = (Map) mlRefDocs.get(0);
					strRefDocId = (String) objectMap.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strRefDocId)) {
						doRefDocObject = new DomainObject(strRefDocId);
						doRefDocObject.deleteObject(context);

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ContextUtil.popContext(context);
		}
	}

	/**
	 * 
	 * @param strValue
	 * @return
	 * @throws Exception
	 */
	public String replaceSpecialCharacters(String strValue) throws Exception {

		strValue = strValue.replaceAll("\n", "#LINE_BREAK");
		strValue = strValue.replaceAll("~`~", "#LINE_BREAK");
		strValue = strValue.replaceAll("<BR/>", "#LINE_BREAK");
		strValue = strValue.replaceAll("<br/>", "#LINE_BREAK");
		strValue = strValue.replaceAll("<B>", "#BOLDTAG_START");
		strValue = strValue.replaceAll("</B>", "#BOLDTAG_END");
		strValue = strValue.replaceAll("[<]", "#LESS_THAN");
		strValue = strValue.replaceAll("[>]", "#GREATER_THAN");

		strValue = strValue.replaceAll("[\"]", "&quot;");
		strValue = StringEscapeUtils.escapeJava(strValue);
		return strValue;
	}

	/**
	 * Method To Rename checked out temporary content if it is having special
	 * character
	 * 
	 * @param fileName    - Checked out temp content file name
	 * @param strFilePath - Checked out temp file path
	 * @param strObjName  - Object Name
	 */

	public String renameTempContentFile(String fileName, String strFilePath, String strObjName) throws Exception {

		String strFileName = "";

		try {

			StringBuilder oldFileName = new StringBuilder(strFilePath);
			oldFileName.append(java.io.File.separator);
			oldFileName.append(fileName.replaceAll("\u00E2", "\u003F"));

			File oldFile = new File(oldFileName.toString());
			String extension = getFileExtension(oldFile);

			StringBuilder newFileName = new StringBuilder(strFilePath);
			newFileName.append(java.io.File.separator);
			newFileName.append(strObjName);
			newFileName.append("TempContentFile");
			// Added/Modified by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
			newFileName.append(CloudGenDocUtil.getRandomUUIDForFileNamePostFix(oldFile.getName()));
			// Added/Modified by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends

			File newFile = new File(newFileName.toString());

			if (newFile.exists()) {
				throw new java.io.IOException("MESSAGE:  file already exists");
			} else {
				if (oldFile.renameTo(newFile)) {
					System.out.println("File rename Success" + fileName);
					strFileName = newFile.getName();

				} else {

					System.out.println("MESSAGE: File rename failed");
					strFileName = fileName;

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return strFileName;
	}

	/**
	 * Method to get file extension
	 * 
	 * @param file - To get file extension
	 */

	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	// Added by DSM-2015x.5.1 for POA Content File Size issue Defect 19834 - Starts
	public int checkUploadedFileSize(Context context, String[] args) throws Exception {

		int iReturn = 0;
		String strCAObjectId = args[0];
		String strObjectId = DomainConstants.EMPTY_STRING;
		String strObjectType = DomainConstants.EMPTY_STRING;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRevision = DomainConstants.EMPTY_STRING;
		StringList slFileList = new StringList();
		StringList slFileSizeList = new StringList();
		String sMessageKey = i18nNow.getI18nString("emxCPN.ContentFile.SizeCheck", "emxCPNStringResource",
				context.getSession().getLanguage());
		// String sMessageKey = "Please upload a Content file with size >0 for below
		// Parts - ";
		StringBuffer sbParts = new StringBuffer();
		StringBuffer sbMessage = new StringBuffer();
		if (UIUtil.isNotNullAndNotEmpty(strCAObjectId)) {
			String sRelatedRelationshipPattern = pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM;
			String sRelatedTypePattern = i18nNow.getI18nString(
					"emxCPN.ProductDataTemplate.type_ProductDataIPMPDF.NonStructuredTypeInclusionList", "emxCPN",
					context.getSession().getLanguage());
			StringList slBusSelects = new StringList(6);
			slBusSelects.add(DomainConstants.SELECT_ID);
			slBusSelects.add(DomainConstants.SELECT_TYPE);
			slBusSelects.add(DomainConstants.SELECT_NAME);
			slBusSelects.add(DomainConstants.SELECT_REVISION);
			slBusSelects.add(DomainConstants.SELECT_FILE_FORMAT);
			slBusSelects.add(DomainConstants.SELECT_FILE_SIZE);
			DomainObject doRelatedItem = DomainObject.newInstance(context, strCAObjectId);
			// DSM (DS) 2018x.0-post upgrade : Defect ID : 22879: The CA is promoted
			// successfully although the ART contain a 0 KB file. : Start
			/*
			 * MapList mlRelatedObjects = doRelatedItem.getRelatedObjects(context,
			 * sRelatedRelationshipPattern, sRelatedTypePattern, slBusSelects, null, false,
			 * true, (short)1, null, null );
			 */
			HashMap programMap=new HashMap();
			programMap.put("CAId", strCAObjectId);
			programMap.put("slSelect", slBusSelects);
			programMap.put("reqChang", pgV3Constants.For_Release);
			programMap.put("affectedRel", true);
			programMap.put("impRel", true);
			String[] strArgs =JPO.packArgs(programMap); 
			//			MapList mlRelatedObjects = pgDSMChangeUtil_mxJPO.getAffectedItemForCA(context, strCAObjectId, slBusSelects,
			//					pg.For_Release, true, true);
			MapList mlRelatedObjects =(MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgDSMChangeUtil", "getAffectedItemForCA", strArgs);

			// DSM (DS) 2018x.0-post upgrade : Defect ID : 22879: The CA is promoted
			// successfully although the ART contain a 0 KB file. : end

			if (mlRelatedObjects != null && (mlRelatedObjects.size() > 0)) {
				for (int j = 0; j < mlRelatedObjects.size(); j++) {
					Map mRelatedData = (Map) mlRelatedObjects.get(j);

					Object objFile = (Object) mRelatedData.get(DomainConstants.SELECT_FILE_FORMAT);
					if (objFile instanceof String) {
						String sFile = (String) objFile;
						slFileList.add(sFile);
					} else if (objFile instanceof StringList) {
						slFileList = (StringList) objFile;
					}

					Object objFileSize = (Object) mRelatedData.get(DomainConstants.SELECT_FILE_SIZE);
					if (objFileSize instanceof String) {
						String sFileSize = (String) objFileSize;
						slFileSizeList.add(sFileSize);
					} else if (objFileSize instanceof StringList) {
						slFileSizeList = (StringList) objFileSize;
					}
					if (slFileList.contains(pgV3Constants.FORMAT_GENERIC) && slFileSizeList.contains("0")) {
						strObjectType = (String) mRelatedData.get(DomainConstants.SELECT_TYPE);
						strObjectName = (String) mRelatedData.get(DomainConstants.SELECT_NAME);
						strObjectRevision = (String) mRelatedData.get(DomainConstants.SELECT_REVISION);
						sbParts.append(strObjectType);
						sbParts.append(" ");
						sbParts.append(strObjectName);
						sbParts.append(" ");
						sbParts.append(strObjectRevision);
						sbParts.append("\n");
					}
				}
			}
			if (sbParts.length() > 0) {
				iReturn = 1;
				sbMessage.append(sMessageKey);
				sbMessage.append("\n");
				sbMessage.append(sbParts.toString());
				//				emxContextUtil_mxJPO.mqlNotice(context, sbMessage.toString());
				HashMap map = new HashMap();
				map.put("notice", sbMessage.toString());
				String arg[]=JPO.packArgs(map);
				pgPDFViewHelper.executeIntermediatorClassMethod(context, "mqlNotice", arg);
			}
		}
		return iReturn;
	}

	// Added by DSM-2015x.5.1 for POA Content File Size issue Defect 19834 - Ends
	// Added by DSM(Sogeti)-2018x.1.1 for PDF Views Defect 26209 - Starts
	private void getCoAndCAData(Context context, String objectId, StringList slCOList, StringList slCAList) {
		try {
			StringList selectStmts = new StringList(2);
			selectStmts.add(DomainConstants.SELECT_ID);
			selectStmts.add(DomainConstants.SELECT_NAME);
			Map proposedCAData = com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil
					.getChangeObjectsInProposed(context, selectStmts, new String[] { objectId }, 2);
			System.out.println("------proposedCAData------");
			if (null != proposedCAData && !proposedCAData.isEmpty()) {
				MapList proposedchangeActionList = (MapList) proposedCAData.get(objectId);
				Iterator itr = proposedchangeActionList.iterator();
				Map mapChangeObjects = null;
				String strChangeObjectType = null;
				String strCOCAName = null;
				while (itr.hasNext()) {
					mapChangeObjects = (Hashtable) itr.next();
					strChangeObjectType = (String) mapChangeObjects.get(DomainConstants.SELECT_TYPE);
					strCOCAName = (String) mapChangeObjects.get(DomainConstants.SELECT_NAME);
					if ("Change Order".equals(strChangeObjectType)) {
						slCOList.add(strCOCAName);
					} else if ("Change Action".equals(strChangeObjectType)) {
						slCAList.add(strCOCAName);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	// Added by DSM(Sogeti)-2018x.1.1 for PDF Views Defect 26209 - End

	//Start Code Refactoring
	private Map getProductPartsHTMLAndXMLData(Context context, String strHeader2,
			Map<String, String> mapAdlibTicketData, Pattern strTypePattern, String strPDFfileName, String args[]) {
		// TODO Auto-generated method stub
		boolean isPopContext = false;
		boolean ifHavingBOM = false;
		boolean ifHavingPlants = false;
		boolean ifHavingCountryOfSales = true;
		boolean ifType = true;
		String objectId = args[0];
		String strPDFViewKind = args[1];
		boolean ifTogetPrimaryAndSecondaryOrganization = true;
		String strTechSpecType = (String) mapAdlibTicketData.get(DomainConstants.SELECT_TYPE);
		String strContextUser = args[2];
		Map consolidatedMap = null;
		String strPolicy = mapAdlibTicketData.get(DomainConstants.SELECT_POLICY);
		try {
			mapAdlibTicketData.put(pgPDFViewConstants.CONST_HEADER2, strHeader2);

			Map BusinessObjectSelectableMap = pgIPMPDFViewDataSelectable_mxJPO
					.getFinishedProductPartSelectableMap(context);

			Map selectRelatedMap = pgIPMPDFViewDataSelectable_mxJPO.getpgFinishedProductRelatedSelectableMap(context,
					false);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPERFORMANCECHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGUNITCHARACTERISTIC);
			strTypePattern.addPattern(pgV3Constants.TYPE_PGTRANSPORTUNITCHARACTERISTIC);
			String strContext = context.getUser();
			String strUserAgent = pgV3Constants.PERSON_USER_AGENT;
			if (strContext.equals(strUserAgent)) {
				ContextUtil.popContext(context);
				isPopContext = true;
			}
			String strAbbr = loadShortType(context, strTechSpecType);
			String strPDFViewKindExtension = "_" + strAbbr;
			consolidatedMap = getHTMLAndXMLContent(context, objectId, CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
					strTypePattern.getPattern(), BusinessObjectSelectableMap, selectRelatedMap, FIRST_LEVEL,
					ftpInputFolder, !ifHavingBOM, !ifHavingPlants, ifHavingCountryOfSales, strPDFViewKind,
					strContextUser, ifType, strTechSpecType, ftpOutputFolder, strPDFfileName, vHTMLInputFiles,
					mapAdlibTicketData, strPolicy, strPDFViewKindExtension, !ifTogetPrimaryAndSecondaryOrganization);
			if (isPopContext) {
				ContextUtil.pushContext(context, strContext, "", "");
				isPopContext = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return consolidatedMap;
	}
	//End Code Refactoring
	
	//Added by DSM (Sogeti) for 2018x.5 Support Tool Req #33939 - Starts 
	/**
	 * This method is to check if GenDoc is already present/not for Selected Object
	 * @param args
	 * @return boolean 
	 */
	public boolean isGenDocPresent(Context context, String[] args) throws Exception {
		boolean isGenDocPresent = false;
		try{
			HashMap<?,?> hmProgramMap = (HashMap) JPO.unpackArgs(args);
			String strPartId = (String) hmProgramMap.get(pgV3Constants.OBJECT_ID);

			if(UIUtil.isNotNullAndNotEmpty(strPartId)){			
				DomainObject domGCAS = DomainObject.newInstance(context, strPartId);
				isGenDocPresent = isGenDocPresent(context, domGCAS);
			}
		} catch (Exception exception) {
				throw exception;
		}
		return isGenDocPresent;
	}
	
	/**
	 * This method is to make a call for GenDoc Generation and to send notification on Failure
	 * @param args
	 */
	public void performGenDocRegenerationForReleasedParts(Context context, String args[]) throws Exception{
		Boolean isContextPushed = false;
		try{			
			HashMap<?,?> hmArgs = (HashMap) JPO.unpackArgs(args);
			MapList mlObjectList = (MapList) hmArgs.get("objectList");
			String strSupportAction = (String) hmArgs.get("SupportAction");
			Map<String, String> mAdlibDetails = (Map) hmArgs.get("AdlibDetails");
			Map<String, String> mMailDetails = (Map) hmArgs.get("MailDetails");
			int iSize = mlObjectList.size();
			ContextUtil.pushContext(context,pgV3Constants.PERSON_USER_AGENT,"","");
			isContextPushed = true;
			Map<?,?> mObjectMap;
			Map<?,?> mRenderPDFDetails;
			Map<String, Object> argMap;
			String strObjectId;
			String strObjectName;
			String strObjectRev;

			for(int i=0;i<iSize;i++){
				mObjectMap =(Map)mlObjectList.get(i);
				strObjectId=(String)mObjectMap.get(SELECT_ID);
				strObjectName=(String)mObjectMap.get(SELECT_NAME);
				strObjectRev =(String)mObjectMap.get(SELECT_REVISION);
				argMap = new HashMap<>();
				argMap.put("objectID", strObjectId);
				argMap.put("AdlibDetails", mAdlibDetails);
				mRenderPDFDetails =  startGenDocGenerationJob(context, JPO.packArgs(argMap));

				String strErrorMsg = (String) mRenderPDFDetails.get("strErrorMsg");
				String strReturnType = (String) mRenderPDFDetails.get("strReturnType");

				if (UIUtil.isNotNullAndNotEmpty(strErrorMsg) || "1".equals(strReturnType)) {
					String fromPersonalName ="GenDocRegeneration";
					String host = PropertyUtil.getEnvironmentProperty(context,"MX_SMTP_HOST");
					String strMsgSubject = mMailDetails.get("strMailSubject");			
					strMsgSubject = strMsgSubject.replace("EVENTNAME",strSupportAction);
					strMsgSubject = strMsgSubject.replace("OBJECTNAME", strObjectName);
					strMsgSubject = strMsgSubject.replace("OBJECTREVISION", strObjectRev);
					
					String strMsgBody = mMailDetails.get("strMailContent");
					strMsgBody = strMsgBody.replace("EVENTNAME", strSupportAction);
					strMsgBody = strMsgBody.replace("OBJECTNAME",strObjectName);
					strMsgBody = strMsgBody.replace("OBJECTREVISION", strObjectRev);

					strMsgBody = "Hi All,\n\n" 	+ strMsgBody + "\n" + strErrorMsg;
					
					String[] arguments = new String[7];
					arguments[0] = host;
					arguments[1] = mMailDetails.get("strFromId");
					arguments[2] = mMailDetails.get("strToId");
					arguments[3] = null;
					arguments[4] = strMsgSubject;
					arguments[5] = "\n" + strMsgBody;
					arguments[6] = fromPersonalName;
					pgV3Util.sendEMailToUser(arguments);				
				}
				
			}
		} catch (Exception exception) {
			throw exception;
		}finally {
			if(isContextPushed){
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
	}
	//Added by DSM (Sogeti) for 2018x.5 Support Tool Req #33939 - Ends 
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
	/**
	 * This method is use to move iText PDF to Gendoc file folder 
	 * to generate gendoc for non-structured type ATS and Artwork 
	 * @param context
	 * @param sourcePath 
	 * @param destPath
	 */
	public void fileCopy(String sourcePath, String destPath) {
		try {
			Path source = Paths.get(sourcePath);
	        Path target = Paths.get(destPath);
	        java.nio.file.Files.move(source, target);
			System.out.println("File moved successfully");
		}catch(Exception e) {
			System.out.println("Failed to move the file");
			e.printStackTrace();
		}
	}
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends
	
	/** Added By Sogeti(DSM) 2018x.5 PDF - This method is used to Stamp PDF File Footer
	/**
	 * @Description This method is used to Stamp PDF File
	 * @param Context context
	 * @param  String args[]
	 * @return boolean
	 */
	public boolean processGenDocStamping(Context context, String args[]) throws Exception{
		boolean bResult = false;
		
		try{
			boolean isFileRenamed = false;
			StringBuilder sbFilePathForStamp = new StringBuilder();
			StringBuilder sbFilePathToStamp = new StringBuilder();
			String strNewLineFooter = "";
			String strFooterText = "";
			Map<?,?> mProgramMap = JPO.unpackArgs(args);
			String strWorkingDir = (String)mProgramMap.get("workDir");
			String strFileName = (String)mProgramMap.get("filenameForStamp");
			String strGenDocFooter = (String)mProgramMap.get("Footer");
			float verticalLength = Float.parseFloat((String)mProgramMap.get("VerticalLength"));
			float rotationLength = Float.parseFloat((String)mProgramMap.get("RotationLength"));
			String strFileNameForStamping = (String)mProgramMap.get("FileNameForStamping");
			//Added by DSM Requirement 47082,47059  2022x.04 Dec CW 2023 - Start
			String strStampFile = (String)mProgramMap.get("StampFile");
			logger.log(Level.INFO,"Total Time has taken by the setOwnershipTableData Method is-->{0}",strStampFile);
			//Added by DSM Requirement 47082,47059  2022x.04 Dec CW 2023 - End
			float horizontalLength = 0;
			Phrase phrase;
			if(UIUtil.isNotNullAndNotEmpty(strFileName) && UIUtil.isNotNullAndNotEmpty(strFileNameForStamping)){
			
			sbFilePathForStamp.append(strWorkingDir).append(java.io.File.separator).append(strFileName);
			sbFilePathToStamp.append(strWorkingDir).append(java.io.File.separator).append(strFileNameForStamping);
			File pdfFilePathForStamp = new File(sbFilePathForStamp.toString());
			File pdfFilePathToStamp = new File(sbFilePathToStamp.toString());
			
			 PdfReader pdfReader = new PdfReader(sbFilePathForStamp.toString());
			 PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(sbFilePathToStamp.toString()));
			 int iPages = pdfReader.getNumberOfPages();
			 PdfContentByte pdfContentByte = null;
			 //Modified font size for DSM Requirement 47346  2022x.04 Dec CW 2023 - Start
			 Font pdfFont = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.BOLDITALIC, BaseColor.BLACK);
			 //Modified font size for DSM Requirement 47346  2022x.04 Dec CW 2023 - End
			 Phrase phraseNewLine = null;
			 Rectangle rect = null;
			 if(UIUtil.isNotNullAndNotEmpty(strGenDocFooter) && strGenDocFooter.contains(pgV3Constants.STAMP_NEW_LINE)){
				 String [] sSplit = strGenDocFooter.split(pgV3Constants.STAMP_NEW_LINE);
                 strNewLineFooter = sSplit[0];
				 strGenDocFooter = sSplit[1];
				 Chunk boldChunk1 = new Chunk(strNewLineFooter, pdfFont);
			     boldChunk1.setGenericTag(strNewLineFooter);
			     boldChunk1.setTextRenderMode(
			     PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 0.5f,
			              GrayColor.GRAYBLACK);
				 phraseNewLine = new Phrase(boldChunk1);
				 verticalLength = verticalLength-13;
				}
			    	
			    for (int i = 1; i <= iPages; i++) {
			      rect = pdfReader.getPageSize(i);
			      horizontalLength = rect.getWidth()/2;
			      strFooterText = strGenDocFooter +" "+ i + " of " + iPages;
			      Chunk boldChunk = new Chunk(strFooterText, pdfFont);
			      boldChunk.setGenericTag(strFooterText);
			      boldChunk.setTextRenderMode(
			      PdfContentByte.TEXT_RENDER_MODE_FILL_STROKE, 0.5f,
			              GrayColor.GRAYBLACK);
				 
			      phrase = new Phrase(boldChunk);
			      
			      pdfContentByte = pdfStamper.getOverContent(i);
			      pdfContentByte.saveState();
				
				//Added by DSM (Infosys) 2022x.6 for 57681 -   Enovia approved watermark going through the art file - START
			    /* ColumnText.showTextAligned(pdfStamper.getOverContent(i), Element.ALIGN_CENTER,
			              phrase, horizontalLength,verticalLength,rotationLength); */
						  
				float y = pdfReader.getPageSize(i).getBottom(20);
				ColumnText.showTextAligned(pdfStamper.getOverContent(i), Element.ALIGN_CENTER,
			              phrase, horizontalLength,y,rotationLength); 
				//Added by DSM (Infosys) 2022x.6 for 57681 -   Enovia approved watermark going through the art file - END
		
				 if (null!=phraseNewLine){
				 ColumnText.showTextAligned(pdfStamper.getOverContent(i), Element.ALIGN_CENTER,
			              phraseNewLine,horizontalLength,verticalLength+13,rotationLength);
			      		  pdfContentByte.restoreState();
				 	}
			    }
			    pdfStamper.close();
			    pdfReader.close();
			    Files.delete(pdfFilePathForStamp);
			    isFileRenamed =pdfFilePathToStamp.renameTo(pdfFilePathForStamp);
				if(isFileRenamed){
				   bResult = true;
				 }	
			}  
			   
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 
	 * @param context
	 * @param args
	 * @param mapMasterDomainObject
	 */
	public void getWND(Context context, String[] args, Map mapMasterDomainObject) {
		long startTime = new Date().getTime();
		try {
			MapList mlWND = (MapList)pgPDFViewHelper.executeMainClassMethod(context, "pgDSOWeightsAndDimensions", "getWeightCharacteristics", args);
			StringList slGw = new StringList(1);
			StringList slGwUom = new StringList(1);
			if (mlWND != null && !mlWND.isEmpty()) {
				Iterator<Map<String,String>> mpWNDItr = mlWND.iterator();
				String strGw ;
				String strGwUom ;
				String strid ;
				Map<String,String> mapAttributeInfoWND ;
				StringList slbusSelectWND = new StringList(2);
				slbusSelectWND.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTREAL);
				slbusSelectWND.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTUNITOFMEASURE);
				Map<String,String> wNDMap;
				while (mpWNDItr.hasNext()) {
					wNDMap =  mpWNDItr.next();
					strid =  wNDMap.get("id");
					if (UIUtil.isNotNullAndNotEmpty(strid)) {
						DomainObject domObjWND = DomainObject.newInstance(context, strid);
						mapAttributeInfoWND = domObjWND.getInfo(context, slbusSelectWND);
	
						if (mapAttributeInfoWND != null) {
							strGw =  mapAttributeInfoWND.get(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTREAL);
							strGwUom =  mapAttributeInfoWND
									.get(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTUNITOFMEASURE);
							slGw.addElement(strGw);
							slGwUom.addElement(strGwUom);
						}
					}
				}
			}
			
			String strGWDisp = getString(context, slGw);
			String strGWUomDisp = getString(context, slGwUom);
			mapMasterDomainObject.putAll(getMapfor("strGW", strGWDisp));
			mapMasterDomainObject.putAll(getMapfor("strGWUom", strGWUomDisp));
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the getWND Method is-->{0}",(endTime-startTime));
	}
	
	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 */
	public MapList getSupplierTraderInfoList(Context context, String[] args) {
		long startTime = new Date().getTime();
		MapList mlsupplierTraderInfoList = new MapList();
		try {
			MapList mlgetSupplierTraderInfo = (MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, "pgASLgetObjectLIst", args);
			String strValue = null;
			Map<String, String> manufacturerInfoMap;
			Map<String, String> mapObject2;
			Map<String, String> mTraderDistributorInfoMap = new HashMap<>();
			StringList slTraderDistributorInfoList;
			for (Iterator<Map<String, String>> iterator = mlgetSupplierTraderInfo.iterator(); iterator.hasNext();) {
				mapObject2 =  iterator.next();
				strValue = mapObject2.get("attribute[pgCSSTraderDistributor]");
				slTraderDistributorInfoList = getTraderDistributorInformation(context, strValue);
				manufacturerInfoMap = new HashMap<>();
				updateTraderDistributorInfoMap(slTraderDistributorInfoList,mTraderDistributorInfoMap);
		
				mlsupplierTraderInfoList.add(mTraderDistributorInfoMap);
				StringBuilder strAddress = new StringBuilder();
				if (validateString(mapObject2.get(pgV3Constants.SELECT_ATTRIBUTE_ADDRESS))) {
					if (strAddress.length() > 1) {
						strAddress.append(pgPDFViewConstants.STR_COMMA);
					}
					strAddress.append(mapObject2.get(pgV3Constants.SELECT_ATTRIBUTE_ADDRESS));
				}
				if (validateString(mapObject2.get("attribute[" + pgV3Constants.ATTRIBUTE_CITY + "]"))) {
					if (strAddress.length() > 1) {
						strAddress.append(pgPDFViewConstants.STR_COMMA);
					}
					strAddress.append(mapObject2.get("attribute[" + pgV3Constants.ATTRIBUTE_CITY + "]"));
				}
				if (validateString(mapObject2.get("attribute[" + pgV3Constants.ATTRIBUTE_STATEREGION + "]"))) {
					if (strAddress.length() > 1) {
						strAddress.append(pgPDFViewConstants.STR_COMMA);
					}
					strAddress.append(mapObject2.get("attribute[" + pgV3Constants.ATTRIBUTE_STATEREGION + "]"));
				}
				manufacturerInfoMap.put("UsingPlantLocation", mapObject2.get("UsingPlantLocation"));
				manufacturerInfoMap.put("ManufacturerInfo",
						validateString(mapObject2.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_ORGNIZATION_NAME + "]"))
						? mapObject2.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_ORGNIZATION_NAME + "]")
								: DomainConstants.EMPTY_STRING);
				manufacturerInfoMap.put("ManufacturerPlantLocation", strAddress.toString());
				manufacturerInfoMap.put("ManufacturerCountry",
						validateString(mapObject2.get("attribute[" + pgV3Constants.ATTRIBUTE_COUNTRY + "]"))
						? mapObject2.get("attribute[" + pgV3Constants.ATTRIBUTE_COUNTRY + "]")
								: DomainConstants.EMPTY_STRING);
				manufacturerInfoMap.put(pgPDFViewConstants.CONS_MANUFACTURERCOUNTRY,
						validateString(mapObject2.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_POSTALCODE + "]"))
						? mapObject2.get("attribute[" + pgPDFViewConstants.ATTRIBUTE_POSTALCODE + "]")
								: DomainConstants.EMPTY_STRING);
				manufacturerInfoMap.put("ManufacturerMVN",
						validateString(mapObject2.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHORTCODE))
						? mapObject2.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHORTCODE)
								: DomainConstants.EMPTY_STRING);
				mlsupplierTraderInfoList.add(manufacturerInfoMap);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the getSupplierTraderInfoList Method is-->{0}",(endTime-startTime));
		return mlsupplierTraderInfoList;
	}
	public void updateTraderDistributorInfoMap(StringList slTraderDistributorInfoList,Map mTraderDistributorInfoMap) {
		long startTime = new Date().getTime();
		try {
			int supplierSize = slTraderDistributorInfoList.size();				
			if (null != slTraderDistributorInfoList && supplierSize > 0) {	
				for (int i = 0; i < supplierSize; i++) {
					mTraderDistributorInfoMap.put("TraderDistributor",
							(supplierSize >= 1) ? ( slTraderDistributorInfoList.get(0)) : DomainConstants.EMPTY_STRING);
					mTraderDistributorInfoMap.put("TraderDistributorStreet",
							(supplierSize >= 2) ? ( slTraderDistributorInfoList.get(1)) : DomainConstants.EMPTY_STRING);
					mTraderDistributorInfoMap.put("TraderDistributorCity",
							(supplierSize >= 3) ? ( slTraderDistributorInfoList.get(2)) : DomainConstants.EMPTY_STRING);
					mTraderDistributorInfoMap.put("TraderDistributorState",
							(supplierSize >= 4) ? ( slTraderDistributorInfoList.get(3)) : DomainConstants.EMPTY_STRING);
					mTraderDistributorInfoMap.put("TraderDistributorCountry",
							(supplierSize >= 5) ? ( slTraderDistributorInfoList.get(4)) : DomainConstants.EMPTY_STRING);
					mTraderDistributorInfoMap.put("TraderDistributorMVN",
							(supplierSize >= 6) ? ( slTraderDistributorInfoList.get(5)) : DomainConstants.EMPTY_STRING);
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the updateTraderDistributorInfoMap Method is-->{0}",(endTime-startTime));
	}
	/**
	 * 
	 * @param context
	 * @param args
	 * @return
	 */
	public MapList getSharingAttributeTable(Context context,String[] args) {
		long startTime = new Date().getTime();
		MapList mlSharinglist = new MapList();
		try {
			MapList mlSharingAttributesTable = (MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CONS_PGIPMTABLESJPO, "getSharingAttributesTableData", args);
			if (null != mlSharingAttributesTable && !mlSharingAttributesTable.isEmpty()) {
				Map<String,String> mapReturn ;
				Map mSharingMap;
				StringList slSharing = new StringList(1);
				StringList slFinalListSharing = new StringList(1);
				for (Iterator<Map<String,String>> iterator2 = mlSharingAttributesTable.iterator(); iterator2.hasNext();) {
					mapReturn =  iterator2.next();
					mSharingMap = new HashMap<>();
					String strKey ;
					for (Iterator<String> itrKeys = mapReturn.keySet().iterator(); itrKeys.hasNext();) {
						strKey = itrKeys.next();
						mSharingMap.put("Sharing_" + strKey, mapReturn.get(strKey));
						if (("PLMRegion".equals(strKey)) && (!slSharing.contains(mapReturn.get(strKey)))) {
								slSharing.addElement( mapReturn.get(strKey));
						}
					}
					StringBuilder sbValue = new StringBuilder();
					String strSharingRegion;
					int slSharingsize = slSharing.size();
					for (int i = 0; i < slSharingsize; i++) {
						strSharingRegion = slSharing.get(i);
						if (sbValue.length() > 0) {
							sbValue.append(pgV3Constants.SYMBOL_PIPE);
						}
						sbValue.append(strSharingRegion);
					}
					slFinalListSharing.addElement(sbValue.toString());
					slFinalListSharing.sort();
					mSharingMap.put("Sharing_FPPSharingRegion", slFinalListSharing);
					mlSharinglist.add(mSharingMap);
				}
			}
			mlSharingAttributesTable.clear();
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the getSharingAttributeTable Method is-->{0}",(endTime-startTime));
		return mlSharinglist;
	}
	/**
	 * 
	 * @param context
	 * @param strMasterid
	 * @param strMasterName
	 * @param strType
	 * @param strPDFViewType
	 * @param argsSubs
	 * @return
	 */
	public MapList getMasterObjDetails(Context context, String strMasterid, String strMasterName, String strType ,String strPDFViewType, String[] argsSubs) {
		long startTime = new Date().getTime();
		MapList mlMasterObjectDetails = new MapList(); 
		try {
			MapList mlInfo = new MapList();
			if (UIUtil.isNotNullAndNotEmpty(strMasterid)) {
				Map<?,?> mpInfo = pgIPMPDFViewDataSelectable_mxJPO.getMasterSelectableMap(context, strMasterid);
				mlInfo.add(mpInfo);
			}
	
			if (!mlInfo.isEmpty()) {
				Map<String,String> mpMasterDetails = new HashMap<>();
				Map<String,String> reqMapp = new HashMap<>();
				reqMapp.put(pgPDFViewConstants.CONS_OBJECTID, strMasterid);
	
				Map<String,Map<String,String>> paramMapLastt = new HashMap<>();
				paramMapLastt.put(pgPDFViewConstants.CONS_REQUESTMAP, reqMapp);
	
				String[] argsLastt = JPO.packArgs(paramMapLastt);
				String strLastUpdateUsers = (String) pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_EMXCPNPRODUCTDATA, "getLastUpdatedUserForOwnership", argsLastt);
				if (validateString(strLastUpdateUsers)) {
					mpMasterDetails.put("strLastUpdateUsers", strLastUpdateUsers);
				}
				mpMasterDetails.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONS_MASTERSPECIFICATION);
				mpMasterDetails.put("MasterName", strMasterName);
				Map<String,String> mPrimaryAndSecondaryOrganization = getPrimaryAndSecondaryOrganization(context, strMasterid,strType,strPDFViewType);
				String primaryOrganization =  mPrimaryAndSecondaryOrganization.get("from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.name");
				String secondaryOrganization = mPrimaryAndSecondaryOrganization.get("from[" + pgV3Constants.RELATIONSHIP_PGSECONDARYORGANIZATION + "].to.name");						
				
				mpMasterDetails.put("MasterPrimaryOrganization",primaryOrganization);
				mpMasterDetails.put("MasterSecondaryOrganization",secondaryOrganization);
				mpMasterDetails.putAll(getMasterObjectDetails(context ,mlInfo, argsSubs,strMasterid ));
				mlMasterObjectDetails.add(mpMasterDetails);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the mlMasterObjectDetails Method is-->{0}",(endTime-startTime));
		return mlMasterObjectDetails;
		
	}

	/**
	 * 
	 * @param context
	 * @param objectId
	 * @param objWithSelectList
	 * @param strPDFViewType
	 * @param mObjectInfo
	 * @param masterObj
	 * @param slToRemoveMasterKey
	 * @return
	 */

	public MapList getSelectableObjectDataList(Context context, String objectId,BusinessObjectWithSelectList objWithSelectList, String strPDFViewType, Map mObjectInfo, DomainObject masterObj,StringList slToRemoveMasterKey) {

		MapList mlBusinessObjectDataList = new MapList();

		try {
			String sType =  (String) mObjectInfo.get(DomainConstants.SELECT_TYPE);
			String strCurrent =  (String) mObjectInfo.get(DomainConstants.SELECT_CURRENT);

			Map<String,String> mFormulationData = getFormulationData(context,sType,masterObj,strCurrent);
			
			String strFormulationType =DomainConstants.EMPTY_STRING;
			String strFormulationName =DomainConstants.EMPTY_STRING;
				if(mFormulationData != null && !mFormulationData.isEmpty()) {
					strFormulationType =mFormulationData.get(pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);
					strFormulationName = mFormulationData.get(DomainConstants.SELECT_NAME);
				}

		StringList slCOList ;
		StringList slCAList ;
		int iCOSize;
		int iCASize;
		StringList slList;
		StringList sl1List ;
		StringBuilder sbValue ;	
		int listSize;
		int sl1ListSize;
		String strOwnerValue;
		String strOwner;

		for (BusinessObjectWithSelectItr withSelectItr = new BusinessObjectWithSelectItr(
				objWithSelectList); withSelectItr.next();) {
			BusinessObjectWithSelect businessObjectWithSelect = withSelectItr.obj();
			Vector vectorKeys = businessObjectWithSelect.getSelectKeys();
			HashMap<String,StringList> mapData = new HashMap<>();

			slCOList = new StringList(2);
			slCAList = new StringList(2);

			StringBuilder sbCOFinalValue = null;
			if ((null != vectorKeys && !vectorKeys.isEmpty()) && 
					(!(vectorKeys.contains(pgPDFViewConstants.CHANGEORDER) || vectorKeys.contains(pgPDFViewConstants.AFFECTEDITEM)))) {
				
					getCoAndCAData(context, objectId, slCOList, slCAList);
					if (!(slCAList.isEmpty() && slCOList.isEmpty())) {
						sbCOFinalValue = new StringBuilder();
						iCASize = slCAList.size();
						iCOSize = slCOList.size();
						for (int i = 0; i < iCASize; i++) {
							sbCOFinalValue.append(slCAList.get(i)).append("~`~");
						}
						for (int i = 0; i < iCOSize; i++) {
							sbCOFinalValue.append(slCOList.get(i)).append("~`~");
						}
						mapData.put(pgPDFViewConstants.CONS_KEYAFFECTEDIMPLEMENTED, new StringList(sbCOFinalValue.toString()));
					}
			}
			for (Iterator<String> itrKeys = vectorKeys.iterator(); itrKeys.hasNext();) {
				String strKey = itrKeys.next();
				slList = businessObjectWithSelect.getSelectDataList(strKey);
				if (pgPDFViewConstants.CONST_COMBINEDWITHMASTER.equals(strPDFViewType) && strKey.contains(pgPDFViewConstants.CONN_PGMASTER)) {
					slToRemoveMasterKey.addElement(strKey);
					continue;
				}
				if ((pgV3Constants.TYPE_PGCONSUMERDESIGNBASIS.equals(sType) 
						&& (pgPDFViewConstants.SELECT_ATTRIBUTE_PGPHBRAND.equalsIgnoreCase(strKey)
								|| pgPDFViewConstants.SELECT_ATTRIBUTE_PGPRODUCTFORM.equalsIgnoreCase(strKey)
								|| pgPDFViewConstants.SELECT_ATTRIBUTE_PGSUBBRAND.equalsIgnoreCase(strKey)
								|| pgPDFViewConstants.SELECT_ATTRIBUTE_PGNODEID.equalsIgnoreCase(strKey)))) {
					String strslList = slList.get(0);
					if (strslList.endsWith(pgV3Constants.SYMBOL_PIPE)) {
						strslList = strslList.substring(0, strslList.length() - 1);
						slList.remove(0);
						slList.addElement(strslList);
					}
				}

				if (pgPDFViewConstants.CONS_OWNER.equalsIgnoreCase(strKey)) {
					strOwnerValue = slList.get(0);
					strOwner = PersonUtil.getFullName(context, strOwnerValue);
					slList.remove(0);
					slList.addElement(strOwner);
				}
				StringList slFinalList = new StringList();
				StringList  slCommonList = pgIPMPDFViewDataSelectable_mxJPO.getKeyList();
				StringList  slFirstList = pgIPMPDFViewDataSelectable_mxJPO.getKeySelectList();

				if (slCommonList.contains(strKey) || slFirstList.contains(strKey) ) {
					sbValue = new StringBuilder();	


					String strPGPackagingTechnology ;
					if (slCommonList.contains(strKey)) {
						listSize = slList.size();			
						for (int i = 0; i < listSize; i++) {
							strPGPackagingTechnology = slList.get(i);

							if (pgPDFViewConstants.SHIPPINGHC.equals(strKey) || pgPDFViewConstants.MATERIALCERTIFICATION.equals(strKey)
									|| pgPDFViewConstants.INTENDEDMARKET.equals(strKey) || pgPDFViewConstants.SEGMENT.equals(strKey)) {
								if (sbValue.length() > 0) {
									//Modified for 2018x.6 Defect 40679 - Starts
									sbValue.append(pgPDFViewConstants.STR_COMMA+pgV3Constants.SYMBOL_SPACE);
									//Modified for 2018x.6 Defect 40679 - Ends
								}
							} else {
								if (sbValue.length() > 0) {
									sbValue.append("~`~");
								}
							}
							sbValue.append(strPGPackagingTechnology);
						}
						slFinalList.addElement(sbValue.toString());
					} else {
						strPGPackagingTechnology = slList.get(0);
						sl1List = StringUtil.split(strPGPackagingTechnology, pgV3Constants.SYMBOL_PIPE);	
						slList.remove(0);
						sl1List.sort();
						StringBuilder strValue = new StringBuilder();
						sl1ListSize = sl1List.size();		
						for (int i = 0; i < sl1ListSize; i++) {
							if (pgPDFViewConstants.SHIPPINGHC.equals(strKey)) {
								if (sbValue.length() > 0) {
									sbValue.append("~~~");
								}
							} else {
								if (strValue.length() > 0) {
									strValue.append("~`~");
								}
							}
							strValue.append(sl1List.get(i));
						}
						slList.addElement(strValue.toString());
					}
				}

					if (slCommonList.contains(strKey)) {
					mapData.put(strKey, slFinalList);
				} else {
					mapData.put(strKey, slList);
				}
					

				
				StringList slAffectedImplementedKeyValue = getAffectedItemData(strKey,mapData,slFinalList);
				if (null != slAffectedImplementedKeyValue && !slAffectedImplementedKeyValue.isEmpty()) {
					mapData.put(pgPDFViewConstants.CONS_KEYAFFECTEDIMPLEMENTED, slAffectedImplementedKeyValue);
				}
				

				
				StringList slDataList = getIPClassificationData(strKey,mapData);
				if(!slDataList.isEmpty())
					mapData.put(strKey, slDataList);

				if ((UIUtil.isNotNullAndNotEmpty(sType)
						&& pgV3Constants.TYPE_FORMULATIONPART.equals(sType)
						&& pgV3Constants.STATE_RELEASE.equals(strCurrent))
						&& (pgPDFViewConstants.KEYFORMULATIONTYPE.equalsIgnoreCase(strKey)
								|| pgPDFViewConstants.KEYFORMULATION.equalsIgnoreCase(strKey))) {
					
					StringList slFormulationList = new StringList(1);
					if (pgPDFViewConstants.KEYFORMULATIONTYPE.equalsIgnoreCase(strKey))
						slFormulationList.addElement(strFormulationType);
					else
						slFormulationList.addElement(strFormulationName);

					mapData.put(strKey, slFormulationList);
				}
			}
			mlBusinessObjectDataList.add(mapData);
		}
		Map mpParam = new HashMap<>(); 
		Map<String,Map<String,String>> fieldMap = new HashMap<>();
		mpParam.put(pgV3Constants.OBJECT_ID, objectId);
		Map<String,String> settings = new HashMap<>();
		//Refactor 2018x.6
		if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sType)
				|| pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sType)
				|| pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sType)) {
			settings.put(pgPDFViewConstants.CONS_ATTRIBUTENAME, "ProductFormForProduct");
		} else {
			settings.put(pgPDFViewConstants.CONS_ATTRIBUTENAME, "ProductForm");
		}
		
		fieldMap.put("settings", settings);
		mpParam.put("fieldMap", fieldMap);

		String[] argsSubs = JPO.packArgs(mpParam);
//		mpParam.clear();
		String strProductForm = (String)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGDSOCPNPRODUCTDATA, "getConnectedProductForm", argsSubs);
		Map<String,String> mpProductForm = new HashMap<>();
		mpProductForm.put("ProductForm", strProductForm);
		mlBusinessObjectDataList.add(mpProductForm);
		}catch( Exception e) {
			logger.log(Level.WARNING, null, e);
		}
	
		return mlBusinessObjectDataList;
	}
	
	
	/**
	 * 
	 * @param context
	 * @param sType
	 * @param masterObj
	 * @param strCurrent
	 * @param strFormulationName 
	 * @param strFormulationType 
	 * @return
	 */

	public Map<String,String> getFormulationData(Context context, String sType, DomainObject masterObj, String strCurrent) {

		Map<String,String> hmFormulation = null;
		Map<String,String> hmFormulationValue = new HashMap<>();
		try {
		if (UIUtil.isNotNullAndNotEmpty(sType) && pgV3Constants.TYPE_FORMULATIONPART.equals(sType)
				&& pgV3Constants.STATE_RELEASE.equals(strCurrent)) {
			StringList slFormulationselect = new StringList(3);
			slFormulationselect.addElement(DomainConstants.SELECT_NAME);
			slFormulationselect.addElement(DomainConstants.SELECT_CURRENT);
			slFormulationselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);

			MapList mlFormulationList = masterObj.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE, pgV3Constants.TYPE_COSMETICFORMULATION,
						slFormulationselect, null, true, false, (short) 1, null, null, 0);
			
			
			String strFormulationState ;
	
			if (!mlFormulationList.isEmpty()) {		
				int mlFormulationSize = mlFormulationList.size();
				for (int z = 0; z < mlFormulationSize; z++) {

					hmFormulation = (Map) mlFormulationList.get(z);
					strFormulationState =  hmFormulation.get(DomainConstants.SELECT_CURRENT);
					if (UIUtil.isNotNullAndNotEmpty(strFormulationState)
							&& pgV3Constants.STATE_RELEASE.equals(strFormulationState)) {

						hmFormulationValue.put(pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE, hmFormulation.get(pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE));
						hmFormulationValue.put(DomainConstants.SELECT_NAME, hmFormulation.get(DomainConstants.SELECT_NAME));
					}
				}
			}
		}
		} catch (FrameworkException e) {
			logger.log(Level.WARNING, null, e);
		}
		return hmFormulationValue;

	}

	public StringList getIPClassificationData(String strKey, Map<String, StringList> mapData) {

		StringList slDataList = new StringList(1);
		if (pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION.equals(strKey)) {
			String strClassification = DomainConstants.EMPTY_STRING;
			StringList slClassification ;
			Object objClassification = mapData.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
			if (mapData.containsKey(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION)) {
				if (objClassification instanceof StringList) {
					slClassification = (StringList) objClassification;
					strClassification = convertToString(slClassification);	// need coderefactor
				} else if (objClassification instanceof String) {
					strClassification = (String) objClassification;
				}

				if (pgV3Constants.RESTRICTED.equals(strClassification)) {	// check with vinaya CodeRefactor
					slDataList.addElement(pgV3Constants.BUSINESS_USE);
				} else {
					slDataList.addElement(strClassification);
				}
				mapData.put(strKey, slDataList);
			}
		}
		
		return slDataList;
	}

	public StringList getAffectedItemData(String strKey, Map<String, StringList> mapData, StringList slFinalList) {
		StringList slAffectedImplementedKeyValue = null;
		if (pgPDFViewConstants.AFFECTEDITEM.equals(strKey) || pgPDFViewConstants.CHANGEAFFECTEDITEM.equals(strKey)
				|| pgPDFViewConstants.CHANGEORDER.equals(strKey)) {
			
			if (mapData.containsKey(pgPDFViewConstants.CONS_KEYAFFECTEDIMPLEMENTED)) {
				slAffectedImplementedKeyValue =  mapData.get(pgPDFViewConstants.CONS_KEYAFFECTEDIMPLEMENTED);
				String strAffectedImplementedKeyValue = slAffectedImplementedKeyValue.get(0);
				StringBuilder sbItemValue = new StringBuilder();		// need coderefactor
				sbItemValue.append(strAffectedImplementedKeyValue);
				if (!slFinalList.isEmpty()) {
					sbItemValue.append("~`~");
					sbItemValue.append( slFinalList.get(0));
				}
				slAffectedImplementedKeyValue = new StringList();
				slAffectedImplementedKeyValue.addElement(sbItemValue.toString());
			} else {
				if (null != slFinalList && !slFinalList.isEmpty()) {
					slAffectedImplementedKeyValue = slFinalList;
				}
			}
			
		}
		return slAffectedImplementedKeyValue;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	private Map<String,String> getMapfor(String key, String value) {
		Map<String,String> mMasterSharingRegionMap = new HashMap<>();
		if (validateString(value))	
			mMasterSharingRegionMap.put(key, value);
		return mMasterSharingRegionMap;
	}

	/**
	 * @Description Method To Get Country of Sale
	 * @param context
	 * @param masterObj
	 * @param mapData
	 * @return Map	 
	 */
	public Map<String,String> getCountryOfSale(Context context, DomainObject masterObj, Map<String, String> mapData) {
		long startTime = new Date().getTime();
		Map<String,String> mapMasterDomainObject = new HashMap<>();
		try {		
			MapList mlCountryOfSales = getCountryOfSalesDataListForFCPDF(context, masterObj);	
			MapList mlCountryOfSalesDetails = new MapList();
			Map<String,String> mpCountryOfSale;
			if (null != mlCountryOfSales && !mlCountryOfSales.isEmpty()) {
				for (int iCounter = 0; iCounter < mlCountryOfSales.size(); iCounter++) {
					mpCountryOfSale = (Map) mlCountryOfSales.get(iCounter);
					mpCountryOfSale.put(DomainConstants.SELECT_TYPE, mapData.get(DomainConstants.SELECT_TYPE));
					mlCountryOfSalesDetails.add(mpCountryOfSale);
				}
				mapMasterDomainObject.putAll(consolidateRows(context, mlCountryOfSalesDetails));
				mlCountryOfSalesDetails.clear();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the getCountryOfSale Method is--> {0}",(endTime-startTime));
		return mapMasterDomainObject;
	}
	
	/**
	 * @Description Method To Get Master Specification
	 * @param context
	 * @param objectId
	 * @param strPDFViewType
	 * @return Map
	 * @throws Exception
	 */
	public Map<String,String> getMasterSpecList(Context context, String objectId, String strPDFViewType) {
		long startTime = new Date().getTime();
		Map<String,String> mapMasterDomainObject = new HashMap<>();
		try {		
			Map<String,String> mArgsmap = new HashMap<>();
			mArgsmap.put(pgPDFViewConstants.CONS_OBJECTID, objectId);
			mArgsmap.put(pgPDFViewConstants.CONS_PARENTRELNAME, pgPDFViewConstants.REL_PARTSPECIFICATION_PGAPPROVEDSUPPLIERLIST);	

			String[] args = JPO.packArgs(mArgsmap);
			String strContext = context.getUser();
		if (pgV3Constants.PERSON_USER_AGENT.equals(strContext) && validateString(strPDFViewType)
				&& (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)
						|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType))) {
			ContextUtil.popContext(context);
			isAllPopContext = true;
		}
			MapList mlSpecificatins =(MapList)pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_PGIPMPRODUCTDATA, pgPDFViewConstants.CONS_GETDOCUMENTS, args);
		if (isAllPopContext && validateString(strPDFViewType) && (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)
				|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType))) {
			//The sonar lint comment Remove this forbidden call not applicable here, as the below code is having existing requirement logic to get access.
			ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT,DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			isAllPopContext = false;
		}

			if (null != mlSpecificatins && !mlSpecificatins.isEmpty()) {
				Map<String,String> mpSpecsItr = null;
				Map<String,String> mpSpecs ;
				MapList mlSpecsList = new MapList();
				String strSpecType ;
				String pgAssemblyType ;
				String pgCSSType ;
				Map<String,String> specMap ;
				for (Iterator<Map<String,String>> iterator = mlSpecificatins.iterator(); iterator.hasNext();) {
					mpSpecsItr = iterator.next();
					mpSpecs = new HashMap<>();
					strSpecType =  mpSpecsItr.get(DomainConstants.SELECT_TYPE);	
					pgAssemblyType =  mpSpecsItr.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
					pgCSSType =  mpSpecsItr.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
					specMap = new HashMap<>();
					specMap.put(DomainConstants.SELECT_TYPE, strSpecType);
					specMap.put(pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE, pgAssemblyType);
					specMap.put(pgV3Constants.ATTRIBUTE_PGCSSTYPE, pgCSSType);
					mpSpecs.put(pgPDFViewConstants.CONST_SPECIFICATIONDISPLAYTYPE, (String) htmlFormatter.getTypeDisplayName(context, strSpecType));
					mpSpecs.put(pgPDFViewConstants.CONST_SPECIFICATIONSUBTYPE,	 htmlFormatter.getSpecificationSubType(context, specMap));
					mpSpecs.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PRODUCTDATA); //this need to check again
					mpSpecs.put(pgPDFViewConstants.CONST_SEPCIFICATIONNAME, mpSpecsItr.get(DomainConstants.SELECT_NAME));
					mpSpecs.put(pgPDFViewConstants.CONST_SPECIFICATIONSAPDESC, mpSpecsItr.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
					mpSpecs.put(pgPDFViewConstants.CONST_SPECIFICATIONSTATE,  mpSpecsItr.get(DomainConstants.SELECT_CURRENT));
					mlSpecsList.add(mpSpecs);
				}
				mlSpecsList.addSortKey(pgPDFViewConstants.CONST_SPECIFICATIONDISPLAYTYPE, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
				mlSpecsList.sort();				
				mapMasterDomainObject.putAll(consolidateRows(context, mlSpecsList));				
				mlSpecsList.clear();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the getMasterSpecList Method is-->{0}",(endTime-startTime));
		return mapMasterDomainObject;
	}

	/**
	 * @Description Method to get Combined Master Details
	 * @param context
	 * @param masterObj
	 * @param mlBusinessObjectDataList
	 * @param strPDFViewType
	 * @return Map
	 * @throws Exception
	 */
	
	private Map getCombinedMasterDetails(Context context, DomainObject masterObj, MapList mlBusinessObjectDataList,
			String strPDFViewType) throws Exception {
		Map mapMasterDomainObject = new HashMap<>();
		StringList slSelect = new StringList(2);
		slSelect.add("from["+pgV3Constants.PGMASTER+"]");
		slSelect.add("from["+pgV3Constants.PGMASTER+"].to.id");
		Map<String,String> mapObject1 = masterObj.getInfo(context, slSelect);
		String isHavingMaster = mapObject1.get("from["+pgV3Constants.PGMASTER+"]");	
		Map<String,String> object ;
		StringList slBusselect = new StringList(5);
		slBusselect.addElement(DomainConstants.SELECT_NAME);
		slBusselect.addElement(DomainConstants.SELECT_TYPE);
		slBusselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
		slBusselect.addElement(DomainConstants.SELECT_ID);
		slBusselect.addElement(DomainConstants.SELECT_CURRENT);

		StringList slRelselect = new StringList(4);
		slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
		slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
		slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);
		slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
		
		if (validateString(isHavingMaster) && Boolean.parseBoolean(isHavingMaster)) {
			String sMasterId = mapObject1.get("from["+pgV3Constants.PGMASTER+"].to.id");
			String[] arry = getLatestRelease(context, sMasterId);

			if (null != arry && arry.length > 1) {
				sMasterId = arry[0];
			}

			if (UIUtil.isNotNullAndNotEmpty(sMasterId)) {
				DomainObject dom = DomainObject.newInstance(context, sMasterId);
				boolean isAccess = dom.checkAccess(context, (short) 0);
				if (isAccess) {
					String strMasterId = sMasterId;
					Map<String,String> mpMasterATS = new HashMap<>();
					Map<String,Map<String,String>> mpRequestMap = new HashMap<>();
					mpMasterATS.put(pgPDFViewConstants.CONS_OBJECTID, strMasterId);
					mpRequestMap.put(pgPDFViewConstants.CONS_REQUESTMAP, mpMasterATS);
					String[] argsSubs1 = JPO.packArgs(mpRequestMap);
					String strIsAts = (String) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "displayIsATSHeaderAttribute", argsSubs1);
					String strHasAts = (String) pgPDFViewHelper.executeMainClassMethod(context, "pgDSOCPNProductData", "displayhasATSHeaderAttribute", argsSubs1);
					HashMap mpIsHasATS = new HashMap();
					mpIsHasATS.put("strIsATS", strIsAts);
					mpIsHasATS.put("strHasATS", strHasAts);
					mlBusinessObjectDataList.add(mpIsHasATS);
					mapMasterDomainObject.putAll(consolidateRows(context, mlBusinessObjectDataList));

					if (validateString(strMasterId)) {
						Map<String,String> argsMap = new HashMap<>();
						argsMap.put("objectId", strMasterId);
						argsMap.put("parentRelName",
								"relationship_PartSpecification,relationship_pgApprovedSupplierList");
						String[] args = JPO.packArgs(argsMap);
						String strContext = context.getUser();
						if (pgV3Constants.PERSON_USER_AGENT.equals(strContext) && validateString(strPDFViewType)
								&& (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)
										|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType))) {
							ContextUtil.popContext(context);
							isAllPopContext = true;
						}
						MapList mlMasterSpecificatins = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getDocuments", args);
						if (isAllPopContext && validateString(strPDFViewType)
								&& (pgPDFViewConstants.CONST_SUPPLIER.equals(strPDFViewType)
										|| pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING.equals(strPDFViewType))) {
							//The sonar lint comment Remove this forbidden call not applicable here, as the below code is having existing requirement logic to get access.
							ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
							isAllPopContext = false;
						}

						String strSpecType  ;
						String pgAssemblyType  ;
						String pgCSSType ;
						Map<String, String> specMap;
						if (null != mlMasterSpecificatins && !mlMasterSpecificatins.isEmpty()) {
							for (Iterator<Map<String,String>> iterator = mlMasterSpecificatins.iterator(); iterator.hasNext();) {
								object = iterator.next();
								strSpecType =  object.get(DomainConstants.SELECT_TYPE);
								pgAssemblyType =  object
										.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
								pgCSSType =  object.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
								specMap = new HashMap<>();
								specMap.put(DomainConstants.SELECT_TYPE, strSpecType);
								specMap.put(pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE, pgAssemblyType);
								specMap.put(pgV3Constants.ATTRIBUTE_PGCSSTYPE, pgCSSType);
								object.put("MasterSpecificationDisplayType",
										(String) htmlFormatter.getTypeDisplayName(context, strSpecType));
								object.put("MasterSpecificationSubType",
										 htmlFormatter.getSpecificationSubType(context, specMap));
								object.put(DomainConstants.SELECT_TYPE, "Product Data");
								object.put("MasterSpecificationName",
										object.get(DomainConstants.SELECT_NAME));
								object.put("MasterSpecificationSAPDescription",
										 object.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
								object.put("MasterSpecificationState",
										 object.get(DomainConstants.SELECT_CURRENT));
							}
							mlMasterSpecificatins.addSortKey("MasterSpecificationDisplayType", pgPDFViewConstants.CONST_ASCENDING,
									pgPDFViewConstants.CONST_STRING);
							mlMasterSpecificatins.sort();
							mapMasterDomainObject.putAll(consolidateRows(context, mlMasterSpecificatins));
						}
						mlMasterSpecificatins.clear();
						Map<String,String> unPacked = (Map) JPO.unpackArgs(args);
						unPacked.remove("parentRelName");
						unPacked.put("parentRelName", "relationship_ReferenceDocument");
						args = JPO.packArgs(unPacked);
						MapList mlRefDocuments = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getDocumentWithFiles", args);
						if (null != mlRefDocuments && !mlRefDocuments.isEmpty()) {
							for (Iterator<Map<String,String>> iterator = mlRefDocuments.iterator(); iterator.hasNext();) {
								object = iterator.next();
								object.put("MasterDocumentName", object.get("filenames"));
								object.put(DomainConstants.SELECT_TYPE, "Reference_Documents");
								object.put("MasterDocumentType",  object.get("fileType"));
								object.put("MasterLanguage",
										((String) object.get(DomainConstants.SELECT_NAME)).startsWith("LR_")
										?  object.get(DomainConstants.SELECT_REVISION)
												: DomainConstants.EMPTY_STRING);
								object.remove("filenames");
							}
							mapMasterDomainObject.putAll(consolidateRows(context, mlRefDocuments));
						}
						mlRefDocuments.clear();
						MapList mlGetMasterOwnershipTable = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMTablesJPO", "getOwnershipTableData", args);
						String strKey;
						if (null != mlGetMasterOwnershipTable && !mlGetMasterOwnershipTable.isEmpty()) {
							Map owningMap = new HashMap();
							for (Iterator<Map<String,String>> iterator2 = mlGetMasterOwnershipTable.iterator(); iterator2
									.hasNext();) {
								object = iterator2.next();
								for (Iterator<String> itrKeys = object.keySet().iterator(); itrKeys.hasNext();) {
									strKey = itrKeys.next();
									owningMap.put("MasterOwning_" + strKey, object.get(strKey));
								}
							}
							mlGetMasterOwnershipTable.add(owningMap);
							mapMasterDomainObject.putAll(consolidateRows(context, mlGetMasterOwnershipTable));
						}
						mlGetMasterOwnershipTable.clear();
						MapList mlMasterSharingList = new MapList();
						MapList mlMasterSharingAttributesTable = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMTablesJPO", "getSharingAttributesTableData", args);
						if (null != mlMasterSharingAttributesTable
								&& !mlMasterSharingAttributesTable.isEmpty()) {
							object = new HashMap<>();
							Map<String,String> sharingMap ;
							for (Iterator<Map<String,String>> iterator2 = mlMasterSharingAttributesTable.iterator(); iterator2
									.hasNext();) {
								object = iterator2.next();
								sharingMap = new HashMap<>();	
								for (Iterator<String> itrKeys = object.keySet().iterator(); itrKeys.hasNext();) {
									strKey = itrKeys.next();
									sharingMap.put("MasterSharing_" + strKey, object.get(strKey));
								}
								mlMasterSharingList.add(sharingMap);
							}
							object.clear();
							mapMasterDomainObject.putAll(consolidateRows(context, mlMasterSharingList));
						}
						mlMasterSharingList.clear();
						MapList mlTempList = new MapList(1);
						MapList mlShareTableList = new MapList(1);
						MapList mlMasterPerformancespecifications = (MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "pgGetPerformanceCharData", args);
						if (null != mlMasterPerformancespecifications
								&& !mlMasterPerformancespecifications.isEmpty()) {
							Map map = new HashMap();
							HashMap masterKeyMap;
							for (Iterator iterator3 = mlMasterPerformancespecifications.iterator(); iterator3
									.hasNext();) {
								map = (Map) iterator3.next();
								masterKeyMap = new HashMap();
								for (Iterator itrKeys = map.keySet().iterator(); itrKeys.hasNext();) {
									strKey = (String) itrKeys.next();
									if (!DomainConstants.SELECT_TYPE.equals(strKey)) {
										masterKeyMap.put("Master_" + strKey, map.get(strKey));
									} else {
										masterKeyMap.put(strKey, map.get(strKey));
									}
								}
								mlTempList.add(masterKeyMap);
							}
							map.clear();
						}
						mlMasterPerformancespecifications.clear();
						String strType;
						if (null != mlTempList && !mlTempList.isEmpty()) {
							Object strCPS = null;
							Object strCPS1 = null;
							String strObjectId  ;
							String strWhere  ;
							StringList slRefDocs ;
							MapList mlTestMethods ;
							StringBuilder slTestMethodBuilder;
							Map<String,String> mapShareTable ;
							Map<String,String> performanceMap  ;
							String strTMLogic ;
							String strRefDocument ;
							String strRefDocumentType;
							String strCssType;
							String objTestMethod ;
							String strpgPlantTesting ;
							String strpgPlantTestingText;
							String strpgRetestingUOM ;
							String strLevel ;
							DomainObject doObj;
							MapList mlRefDocumentList ;
							int mlTestMethodsSize ;
							for (Iterator iterator = mlTempList.iterator(); iterator.hasNext();) {
								mapShareTable = new HashMap();
								performanceMap = (Map) iterator.next();
								 strObjectId =  performanceMap
										.get("Master_" + DomainConstants.SELECT_ID);
								doObj = DomainObject.newInstance(context, strObjectId);
								strTMLogic = doObj.getAttributeValue(context,
										pgV3Constants.ATTRIBUTE_PGTMLOGIC);
								 slTestMethodBuilder = new StringBuilder(); 
								 slRefDocs = new StringList(1);
								 strWhere = "attribute[pgCSSType]!=TAMU";
								
								mlRefDocumentList = doObj.getRelatedObjects(context,
										pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT + pgPDFViewConstants.STR_COMMA + pgV3Constants.RELATIONSHIP_PROPERTIESTESTINGREQUIREMENTS, // relationshipPattern
										CPNCommonConstants.TYPE_TECHNICAL_SPECIFICATION + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_TESTMETHOD, // typePattern
										slBusselect, 	// bus Selectables
										null,			// relationshipWhere
										true, 			// getTo
										true, 			// getFrom
										(short) 1, 		// recurseToLevel
										null, 			// relationshipWhere
										null,			// PostRelPattern
										0);
								mlRefDocumentList.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING,
										pgPDFViewConstants.CONST_STRING);
								mlRefDocumentList.sort();
								mlTestMethods = doObj.getRelatedObjects(context, "Reference Document"+ "," + "Properties Testing Requirements",
										"pgTestMethod," + pgV3Constants.TYPE_TESTMETHOD, slBusselect, slRelselect, true, true, (short) 1,
										strWhere, null, 0);
								mlTestMethods.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING,
										pgPDFViewConstants.CONST_STRING);
								mlTestMethods.sort();
								if (null != mlRefDocumentList && !mlRefDocumentList.isEmpty()) {
									for (Iterator iterator2 = mlRefDocumentList.iterator(); iterator2
											.hasNext();) {
										object = (Map) iterator2.next();
										 strRefDocument =object.get(DomainConstants.SELECT_NAME);
										 strRefDocumentType = object.get(DomainConstants.SELECT_TYPE);
										 strCssType =  object
												.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
										if (("pgTestMethod".equals(strRefDocumentType) || strRefDocumentType.equals(pgV3Constants.TYPE_TESTMETHOD))&& (!"TAMU".equals(strCssType))) 
										{
											continue;
										} else {
											slRefDocs.add(strRefDocument);
										}
									}
									performanceMap.put("Master_RefDocuments", getString(context, slRefDocs));
								} else {
									performanceMap.put("Master_RefDocuments", getString(context, null));
								}
								mlRefDocumentList.clear();
								mlTestMethodsSize = mlTestMethods.size();
								if (null != mlTestMethods && mlTestMethodsSize > 0) {
									for (int z = 0; z < mlTestMethodsSize; z++) {
										object = (Map) mlTestMethods.get(z);
										 objTestMethod = object.get(DomainConstants.SELECT_NAME);
										if (z == mlTestMethodsSize - 1) {
											slTestMethodBuilder.append(objTestMethod); 
										} else {
											slTestMethodBuilder.append(objTestMethod + "<BR/><B>" + strTMLogic + "</B><br/>"); 
										}
									}
								}
								//Added by DSM(Sogeti)-2018x.3 for PDF Views Defect #29866  - Starts
								performanceMap.put("Master_TestMethod", slTestMethodBuilder.toString());
								//Added by DSM(Sogeti)-2018x.3 for PDF Views Defect #29866  - Ends
								 strpgPlantTesting =  performanceMap
										.get("Master_attribute[pgPlantTesting]");
								 strpgPlantTestingText = performanceMap
										.get("Master_attribute[pgPlantTestingText]");
								 strpgRetestingUOM =  performanceMap
										.get("Master_attribute[pgRetestingUOM]");
								strpgRetestingUOM = getUOM(strpgRetestingUOM);
								if (!DomainConstants.EMPTY_STRING.equals(strpgPlantTesting) || !DomainConstants.EMPTY_STRING.equals(strpgPlantTestingText)
										|| !DomainConstants.EMPTY_STRING.equals(strpgRetestingUOM)) {
									performanceMap.put("Master_attribute[pgPlantTesting]", strpgPlantTesting
											+ " " + strpgPlantTestingText + " " + strpgRetestingUOM);
								}
								strLevel =  performanceMap
										.get("Master_" + DomainConstants.SELECT_LEVEL);
								strType =  performanceMap.get("Master_" + DomainConstants.SELECT_TYPE);
								if ("2".equals(strLevel)) {
									strCPS = performanceMap.get("Master_from.name");
									mapShareTable.put("Master_CPS", getString(context, strCPS));
								} else if ("1".equals(strLevel) && "Shared Table".equals(strType)) {
									strCPS1 = performanceMap.get("Master_to.name");
									mapShareTable.put("Master_CPS", getString(context, strCPS1));
								} else {
									mapShareTable.put("Master_CPS", getString(context, null));
								}
								mlShareTableList.add(mapShareTable);
							}
						}

						mapMasterDomainObject.putAll(consolidateRows(context, mlShareTableList));
						mapMasterDomainObject.putAll(consolidateRows(context, mlTempList));
						mlShareTableList.clear();
						mlTempList.clear();
						Map<String,Map> paramMap = JPO.unpackArgs(args);
						paramMap.put("paramMap", paramMap);
						args = JPO.packArgs(paramMap);
						String strSecurityCategory = (String) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getSecurityCategory", args);
						if (validateString(strSecurityCategory)) {
							HashMap securityCategoryMap = new HashMap();
							securityCategoryMap.put("strMasterSecurityCategory", strSecurityCategory);
							mapMasterDomainObject.putAll(securityCategoryMap);
						}
						String strMasterProjectSecurityGroup = (String) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getpgProjectSecurityGroupValue", args);
						if (validateString(strMasterProjectSecurityGroup)) {
							HashMap hmProjectMasterSecurityGroupMap = new HashMap();
							hmProjectMasterSecurityGroupMap.put("strMasterProjectSecurityGroup",
									strMasterProjectSecurityGroup);
							mapMasterDomainObject.putAll(hmProjectMasterSecurityGroupMap);
							//Modify Code Refactoring
							hmProjectMasterSecurityGroupMap.clear();
						}
						String strEnvironmentalCharacteristic = getEnvPackingLevel(context, strMasterId);
						HashMap packingLevelMap = new HashMap();
						packingLevelMap.put("strMasterEnvironmentalCharacteristic",
								strEnvironmentalCharacteristic);
						mapMasterDomainObject.putAll(packingLevelMap);
						String strApprovers = (String) pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "getApproversOnOwnershipPage", args);
						if (validateString(strApprovers)) {
							HashMap approverMap = new HashMap();
							approverMap.put("strMasterApprover", strApprovers);
							mapMasterDomainObject.putAll(approverMap);
						}
						DomainObject dmoObj = DomainObject.newInstance(context, strMasterId);
						HashMap hm=new HashMap();
						hm.put("strOriginator", dmoObj.getInfo(context, DomainConstants.SELECT_ORIGINATOR));
						String[] args1= JPO.packArgs(hm);

						String strOriginator = (String) pgPDFViewHelper.executeIntermediatorClassMethod(context, "getUserName", args1);
						if (validateString(strOriginator)) {
							HashMap originatorMap = new HashMap();
							originatorMap.put("strMasterOriginatorName", strOriginator);
							mapMasterDomainObject.putAll(originatorMap);
						}
						MapList mlList1 = new MapList();
						MapList mlpgGetEnvironmentalCharData =(MapList)pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "pgGetEnvironmentalCharData", args);

						Map map;
						if (null != mlpgGetEnvironmentalCharData && !mlpgGetEnvironmentalCharData.isEmpty()) {
							HashMap masterKeyValPair;
							for (Iterator iterator = mlpgGetEnvironmentalCharData.iterator(); iterator
									.hasNext();) {
								masterKeyValPair = new HashMap();
								map = (Map) iterator.next();
								strType = (String) map.get(DomainConstants.SELECT_TYPE);
								for (Iterator itrKeys = map.keySet().iterator(); itrKeys.hasNext();) {
									strKey = (String) itrKeys.next();
									masterKeyValPair.put(strType + "_Master_" + strKey, map.get(strKey));
								}
								mlList1.add(masterKeyValPair);
							}
							mapMasterDomainObject.putAll(consolidateRows(context, mlList1));
							mlpgGetEnvironmentalCharData.clear();
						}
						
						mlList1.clear();
						MapList mlList2 = new MapList();
						MapList pgGetMaterialConstructionCharData =(MapList)pgPDFViewHelper.executeMainClassMethod(context, "pgIPMProductData", "pgGetMaterialConstructionCharData", args);
						if (null != pgGetMaterialConstructionCharData
								&& !pgGetMaterialConstructionCharData.isEmpty()) {
							HashMap masterKeyValPair;
							for (Iterator iterator = pgGetMaterialConstructionCharData.iterator(); iterator
									.hasNext();) {
								masterKeyValPair = new HashMap();
								map = (Map) iterator.next();
								strType = (String) map.get(DomainConstants.SELECT_TYPE);
								for (Iterator itrKeys = map.keySet().iterator(); itrKeys.hasNext();) {
									strKey = (String) itrKeys.next();
									masterKeyValPair.put(strType + "_Master_" + strKey, map.get(strKey));
								}
								mlList2.add(masterKeyValPair);
							}
							mapMasterDomainObject.putAll(consolidateRows(context, mlList2));
							pgGetMaterialConstructionCharData.clear();
						}
						
						mlList2.clear();
					}
				}
			}

		}
		
		return mapMasterDomainObject;
	}

	
	

	/**
	 * @Description Method to get Master Attribute Details for CSS Types
	 * @param context
	 * @param objWithMasterSelectList
	 * @param strTType
	 * @param masterObj 
	 * @return MapList
	 */
	
	public MapList getCSSMasterAttributeDetails(Context context,String strTType,StringList slToRemoveMasterKey, DomainObject masterObj) {
		long startTime = new Date().getTime();
		MapList mlBusinessObjectDataListMaster = new MapList();
		try {
			StringList slMasterSelectable = new StringList();

			int slToRemoveMasterKeySize = slToRemoveMasterKey.size();
			if (null != slToRemoveMasterKey && slToRemoveMasterKeySize > 0) {
				String strRemoveMaster;
				for (int iSl = 0; iSl < slToRemoveMasterKeySize; iSl++) {
					strRemoveMaster =  slToRemoveMasterKey.get(iSl);
					strRemoveMaster = strRemoveMaster.substring(18, strRemoveMaster.length());
					slMasterSelectable.addElement(strRemoveMaster);
				}
			}
			String strMasterLastId = masterObj.getInfo(context, "from["+pgV3Constants.PGMASTER+pgPDFViewConstants.CONS_TO_LAST_ID);
			String[] arry = getLatestRelease(context, strMasterLastId);
			if (null != arry && arry.length > 1) {
				strMasterLastId = arry[0];
			}

			BusinessObjectWithSelectList objWithMasterSelectList = BusinessObject
					.getSelectBusinessObjectData(context, new String[] { strMasterLastId }, slMasterSelectable);
		
			if (null != objWithMasterSelectList && !objWithMasterSelectList.isEmpty()) {	
			HashMap mapData ;			
			StringList slList;		
			StringList sl1List ;		
			String strPGPackagingTechnology;
			StringBuilder sbValue ;	
			StringBuilder strValue ; 
			StringBuilder strKey = new StringBuilder() ;
			for (BusinessObjectWithSelectItr withSelectMasterItr = new BusinessObjectWithSelectItr(
					objWithMasterSelectList); withSelectMasterItr.next();) {
				BusinessObjectWithSelect businessObjectWithSelect = withSelectMasterItr.obj();
				Vector vectorKeys = businessObjectWithSelect.getSelectKeys();
				mapData = new HashMap<>();
				for (Iterator<String> itrKeys = vectorKeys.iterator(); itrKeys.hasNext();) {
					
					String key = itrKeys.next();
					slList = businessObjectWithSelect.getSelectDataList(key);
					strKey.append("from["+pgV3Constants.PGMASTER+"].to.last.");
					strKey.append(key);
					if (pgPDFViewConstants.STRDANGERCATEGORY.equals(strKey.toString()) || pgPDFViewConstants.STRSAFETYSYMBOL.equals(strKey.toString())) {
						sbValue = new StringBuilder();	
						strPGPackagingTechnology = slList.get(0); 
						sl1List = StringUtil.split(strPGPackagingTechnology, "|");
						slList.remove(0);
						sl1List.sort();
						 strValue = new StringBuilder();	
						strValue.setLength(0);		
						
						slList.addElement(getCSSMasterAttributeString(strKey.toString(),pgPDFViewConstants.STRSHIPPINGHC,sl1List,strValue,sbValue));
					}
					mapData.put(strKey.toString(), slList);
					mapData.put("type", strTType);
					strKey.setLength(0);
	
				}
				mlBusinessObjectDataListMaster.add(mapData);
			}
		}
		}catch(Exception e)
		{
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the getCSSMasterAttributeDetails Method is-->{0}",(endTime-startTime));
		return mlBusinessObjectDataListMaster;
	}

	/**
	 * 
	 * @param strKey
	 * @param strShippingHC
	 * @param sl1List
	 * @param strValue
	 * @param sbValue
	 * @return
	 */
	public String getCSSMasterAttributeString(String strKey,String strShippingHC,StringList sl1List,StringBuilder strValue,StringBuilder sbValue)
	{
		String strReturnString;
		int sl1Listsize = sl1List.size(); 
		for (int i = 0; i < sl1Listsize; i++) {
			if (strShippingHC.equals(strKey)) {
				if (sbValue.length() > 0) {
					sbValue.append("~~~");
				}
			} else {
				if (strValue.length() > 0) {
					strValue.append("~`~");
				}
			}
			strValue.append(sl1List.get(i));
		}
		strReturnString=strValue.toString();
		return strReturnString;
	}

	/**
	 * @Description Method to Get Plant Details
	 * @param context
	 * @param mlPlants
	 * @return MapList
	 */
	public MapList getModifiedPlants(Context context, DomainObject masterObj) {
		long startTime = new Date().getTime();
		MapList mlModifiedPlants = new MapList();
		try {
			StringList slBusselect = new StringList(4);
			slBusselect.addElement(DomainConstants.SELECT_NAME);
			slBusselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
			slBusselect.addElement(DomainConstants.SELECT_ID);
			slBusselect.addElement(DomainConstants.SELECT_CURRENT);
			StringList slRelselect = new StringList(4);
			slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
			slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
			slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);
			slRelselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
			MapList mlPlants = masterObj.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, DomainConstants.TYPE_ORGANIZATION,
					slBusselect, slRelselect, true, false, (short) 1, null, null, 0);
			mlPlants.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
			mlPlants.sort();
			if (!mlPlants.isEmpty()) {	
			String strAuthorizeToUse;
			String strAuthorizeToProduce ;
			String strIsActivated ;
			String strIsAuthorizedtoView ;
			StringList slAuthorizetoView = new StringList(1);
			StringList slPlants = new StringList(1);
			StringList slAuthorizeToUse = new StringList(1);
			StringList slAuthorizetoProduce = new StringList(1);
			StringList slActivePlants = new StringList(1);			
			Map<String,String> mapPlant;	
			for (Iterator<Map<String,String>> iterator = mlPlants.iterator(); iterator.hasNext();) {
				mapPlant = iterator.next();	
				strAuthorizeToUse = changeTrueToYes(mapPlant.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE));
				// image
				strAuthorizeToUse = strImageSource(strAuthorizeToUse);
				strAuthorizeToProduce = changeTrueToYes(mapPlant.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE));
				// image
				strAuthorizeToProduce = strImageSource(strAuthorizeToProduce);
				strIsActivated = changeTrueToYes(mapPlant.get(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED));
				// image
				strIsActivated = strImageSource(strIsActivated);
				strIsAuthorizedtoView = changeTrueToYes(mapPlant.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW));
				// image
				strIsAuthorizedtoView = strImageSource(strIsAuthorizedtoView);
					slAuthorizetoView.add(strIsAuthorizedtoView);
					slPlants.add(mapPlant.get(DomainConstants.SELECT_NAME));
					slAuthorizeToUse.add(strAuthorizeToUse);
					slAuthorizetoProduce.add(strAuthorizeToProduce);
					slActivePlants.add(strIsActivated);
			}
			mlPlants.clear();
			//The sonar lint comment to generalize the map is not applicable here, as the below mentioned map including both string and stringList.
			Map mapPlants = new HashMap<>();
			Map mapAuthorizeToUsePlants = new HashMap();
			Map mapAuthorizeToProduce = new HashMap();
			mapPlants.put("pgPlants", slPlants);
			mapAuthorizeToUsePlants.put("pgAuthorizedtoUse", slAuthorizeToUse);
			mapAuthorizeToProduce.put("pgAuthorizedtoProduce", slAuthorizetoProduce);
			Map mapActivePlants = new HashMap();
			mapActivePlants.put("pgActivePlants", slActivePlants);
			mapAuthorizeToUsePlants.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PLNAT);
			mapAuthorizeToProduce.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PLNAT);
			mapActivePlants.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PLNAT);
			mapPlants.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PLNAT);
			Map mapAuthorizeToView = new HashMap();
			mapAuthorizeToView.put("pgAuthorizedtoView", slAuthorizetoView);
			mapAuthorizeToView.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PLNAT);
			mlModifiedPlants.add(mapAuthorizeToView);
			mlModifiedPlants.add(mapPlants);
			mlModifiedPlants.add(mapAuthorizeToProduce);
			mlModifiedPlants.add(mapAuthorizeToUsePlants);
			mlModifiedPlants.add(mapActivePlants);
			(mlModifiedPlants).addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
			mlModifiedPlants.sort();
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the geModifiedPlants Method is--> {0}",(endTime-startTime));
		return mlModifiedPlants;
	}

	/**
	 * @Description Method to Get Ownership Details
	 * @param context
	 * @param mlGetOwnershipTable
	 */
	public void setOwnershipTableData( MapList mlGetOwnershipTable) {
		long startTime = new Date().getTime();
		try {
			//The sonar lint comment to generalize the map is not applicable here, as the below mentioned map including both string and stringList.
			Map mOwningMap = new HashMap<>();
			StringList slOwning = new StringList();
			String strKey;
			for (Iterator<Map<String,String>> iterator2 = mlGetOwnershipTable.iterator(); iterator2.hasNext();) {
				Map<String,String> object = iterator2.next();
				for (Iterator<String> itrKeys = object.keySet().iterator(); itrKeys.hasNext();) {
					strKey = itrKeys.next();
					mOwningMap.put("Owning_" + strKey, object.get(strKey));
					if ("PLMRegion".equals(strKey) && !slOwning.contains(object.get(strKey))) { 
						slOwning.add(object.get(strKey));	
				    }
				}
			}
			StringBuilder sbValue = new StringBuilder();
			String strOwningRegion;
			int iOwningSize = slOwning.size();
			for (int i = 0; i < iOwningSize; i++) {
				strOwningRegion = slOwning.get(i);
				if (sbValue.length() > 0) {
					sbValue.append(pgV3Constants.SYMBOL_PIPE);
				}
				sbValue.append(strOwningRegion);
			}
			StringList slFinalList = new StringList(1);
			slFinalList.addElement(sbValue.toString());
			slFinalList.sort();
			mOwningMap.put("Owning_FPPOwningRegion", slFinalList);
			mlGetOwnershipTable.add(mOwningMap);		
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the setOwnershipTableData Method is-->{0}",(endTime-startTime));
	}

	/**
	 * @Description Method to Get Performance Characteristics Details
	 * @param context
	 * @param mlPerformancespecifications
	 * @param mlSharedTableList
	 * @param mlPerformanceCharList
	 * @throws Exception
	 */
	public void setSharedTableAndPerformanceCharData(Context context, MapList mlPerformancespecifications,
			MapList mlSharedTableList, MapList mlPerformanceCharList) {
		long startTime = new Date().getTime();
		try{
			Map<String,String> performanceCharMap ;
			Map<String,String> hmSharedTable ;
			StringList slBusselect = new StringList(5);
			slBusselect.addElement(DomainConstants.SELECT_NAME);
			slBusselect.addElement(DomainConstants.SELECT_TYPE);
			slBusselect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
			slBusselect.addElement(DomainConstants.SELECT_ID);
			slBusselect.addElement(DomainConstants.SELECT_CURRENT);
	
			DomainObject doObj;							
			String strTMLogic;	
			String strObjectId;	
			StringBuilder slTestMethodBuilder = new StringBuilder(); 
			StringList slRefDocs = new StringList(1);			
			StringList slRelselect = new StringList(1);			
			String strWhere = pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE+"!="+pgPDFViewConstants.CONST_TAMU;		
			MapList mlPerfomCharRefDocs;					
			MapList mlPerformTestMethods; 				
			String strpgPlantTesting; 	
			String strpgPlantTestingText ;
			String strpgRetestingUOM;	

			StringBuilder sbTypeList = new StringBuilder();
			sbTypeList.append(pgV3Constants.TYPE_PG_TMRD_TYPES);
			sbTypeList.append(pgPDFViewConstants.STR_COMMA);
			sbTypeList.append(pgV3Constants.TYPE_PGTESTMETHOD );
			sbTypeList.append(pgPDFViewConstants.STR_COMMA);
			sbTypeList.append(pgV3Constants.TYPE_TESTMETHOD);
			sbTypeList.append(pgPDFViewConstants.STR_COMMA);
			sbTypeList.append(pgV3Constants.TYPE_PGSTACKINGPATTERN);
			for (Iterator<Map<String,String>> iterator = mlPerformancespecifications.iterator(); iterator.hasNext();) {
				performanceCharMap =iterator.next();
				strObjectId = performanceCharMap.get(DomainConstants.SELECT_ID);	
				doObj = DomainObject.newInstance(context, strObjectId);						
				strTMLogic = performanceCharMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGTMLOGIC);	
				slTestMethodBuilder.setLength(0);
				slRefDocs.clear();									
				slRelselect.clear();	
				mlPerfomCharRefDocs = doObj.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT + pgPDFViewConstants.STR_COMMA + pgPDFViewConstants.RELATIONSHIP_PROP_TESTING_REQ,
						sbTypeList.toString(),	// typePattern
						slBusselect, 
						null, 
						true, 
						true, 
						(short) 1, 
						null, 
						null, 
						0); 
				mlPerfomCharRefDocs.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
				mlPerfomCharRefDocs.sort();
				StringBuilder relationshipPattern = new StringBuilder();
				relationshipPattern	.append(pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT)
									.append(pgPDFViewConstants.STR_COMMA)
									.append(pgPDFViewConstants.RELATIONSHIP_PROP_TESTING_REQ);
				StringBuilder typePattern = new StringBuilder();
						typePattern.append(pgV3Constants.TYPE_PGTESTMETHOD)
									.append(pgPDFViewConstants.STR_COMMA)
									.append(pgV3Constants.TYPE_TESTMETHOD);
				mlPerformTestMethods = doObj.getRelatedObjects(context,
						relationshipPattern.toString(), // relationshipPattern
						typePattern.toString(), // typePattern
						true, // getTo
						true, // getFrom
						(short) 1, // recurseToLevel
						slBusselect, // objectSelects
						slRelselect, // relationshipSelects
						strWhere, // objectWhere
						null, // relationshipWhere
						null, // PostRelPattern
						pgV3Constants.TYPE_PGTESTMETHOD + pgPDFViewConstants.STR_COMMA + pgV3Constants.TYPE_TESTMETHOD + pgPDFViewConstants.STR_COMMA + pgV3Constants.TEST_METHOD_SPECIFICATION, // PostTypePattern
						null);	
				processPerformanceRefDoc(context, mlPerfomCharRefDocs, performanceCharMap);
				mlPerformTestMethods.addSortKey(DomainConstants.SELECT_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
				mlPerformTestMethods.sort();
				processTestMethod(mlPerformTestMethods,strTMLogic,slTestMethodBuilder);
				performanceCharMap.put(pgPDFViewConstants.CONST_TEST_METHOD, slTestMethodBuilder.toString());
				//Added by DSM(Sogeti)-2018x.3 for PDF Views Defect #29866  - Ends
				strpgPlantTesting = performanceCharMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTING); 
				strpgPlantTestingText = performanceCharMap.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPLANTTESTINGTEXT);			
				strpgRetestingUOM = performanceCharMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGRETESTINGUOM);	
				strpgRetestingUOM = getUOM(strpgRetestingUOM);
				if (!DomainConstants.EMPTY_STRING.equals(strpgPlantTesting) || !DomainConstants.EMPTY_STRING.equals(strpgPlantTestingText)
						|| !DomainConstants.EMPTY_STRING.equals(strpgRetestingUOM)) {
					performanceCharMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTTESTING,
							strpgPlantTesting + " " + strpgPlantTestingText + " " + strpgRetestingUOM);
				}
				hmSharedTable = getShareTableDetails(context,performanceCharMap);
				
				mlSharedTableList.add(hmSharedTable);
	
				mlPerformanceCharList.add(performanceCharMap);
			}
		}catch(Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by setSharedTableAndPerformanceCharData method {0} ",(endTime-startTime));		
	}
	
	/**
	 * @Description Method to update processTestMethod
	 * @param mapList
	 * 
	 * */
	public void processTestMethod(MapList mlPerformTestMethods, String strTMLogic, StringBuilder slTestMethodBuilder) {
		long startTime = new Date().getTime();
		try {
			int mlPerformTestMethodsSize = mlPerformTestMethods.size();
			if (mlPerformTestMethodsSize > 0) {
				Map<String,String> hmTestMethod = null;
				String objTestMethod ;
				for (int z = 0; z < mlPerformTestMethodsSize; z++) {
					hmTestMethod = (Map) mlPerformTestMethods.get(z);
					objTestMethod = hmTestMethod.get(DomainConstants.SELECT_NAME);	
					if (z == mlPerformTestMethodsSize - 1) {
						slTestMethodBuilder.append(objTestMethod); 
					} else {
						slTestMethodBuilder.append(objTestMethod + "<BR/><B>" + strTMLogic + "</B><br/>"); 
					}
				}
			}			
		} catch(Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by processTestMethod method {0} ",(endTime-startTime));		
	}

	/**
	 * @Description Method to process PerformanceCharRefDocs
	 * @param mapList
	 * 
	 * */
	public void processPerformanceRefDoc(Context context, MapList mlPerfomCharRefDocs, Map<String,String> performanceCharMap) {
		long startTime = new Date().getTime();
			if (null != mlPerfomCharRefDocs && !mlPerfomCharRefDocs.isEmpty()) { 
				Map<String,String> hmPerfoCharRefDoc = null;
				String strRefDocument;		
				String strRefDocumentType ;	
				String strCssType ;		
				StringList slRefDocs = new StringList(1);	
				for (Iterator<Map<String,String>> iterator2 = mlPerfomCharRefDocs.iterator(); iterator2.hasNext();) {
					hmPerfoCharRefDoc = iterator2.next();
					strRefDocument = hmPerfoCharRefDoc.get(DomainConstants.SELECT_NAME);			
					strRefDocumentType = hmPerfoCharRefDoc.get(DomainConstants.SELECT_TYPE);		
					strCssType =  hmPerfoCharRefDoc.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);	
					if (!(("pgTestMethod".equals(strRefDocumentType) || (strRefDocumentType.equals(pgV3Constants.TYPE_TESTMETHOD))) && (!"TAMU".equals(strCssType)))) {
						// Modified by DSM(Sogeti) - Fix for 2018x.2  Defect 29136 - Ends

						slRefDocs.addElement(strRefDocument);
					}
				}
				performanceCharMap.put(pgPDFViewConstants.CONST_REF_DOC, getString(context, slRefDocs));
			} else {
				performanceCharMap.put(pgPDFViewConstants.CONST_REF_DOC, getString(context, null));
			}			

		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by processPerformanceRefDoc method {0} ",(endTime-startTime));
	}

	/**
	 * @Description Method to Get RetestinUOMs Details
	 * @param string
	 * @return string
	 */
	public String getUOM(String strpgRetestingUOM ) {
		long startTime = new Date().getTime();
			if ((null != strpgRetestingUOM) && ("DAY".equalsIgnoreCase(strpgRetestingUOM) || "DAYS".equalsIgnoreCase(strpgRetestingUOM))) {
				strpgRetestingUOM = "D";
			} else if ((null != strpgRetestingUOM) && ("WEEK".equalsIgnoreCase(strpgRetestingUOM) || "WEEKS".equalsIgnoreCase(strpgRetestingUOM))) {
				strpgRetestingUOM = "W";
			} else if ((null != strpgRetestingUOM) && ("MONTH".equalsIgnoreCase(strpgRetestingUOM) || "MONTHS".equalsIgnoreCase(strpgRetestingUOM))) {
				strpgRetestingUOM = "M";
			} else if ((null != strpgRetestingUOM) && ("YEAR".equalsIgnoreCase(strpgRetestingUOM) || "YEARS".equalsIgnoreCase(strpgRetestingUOM))) {
				strpgRetestingUOM = "Y";
			}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by getUOM method {0}",(endTime-startTime));	
		return strpgRetestingUOM;
	}
	
	/**
	 * @Description Method to Get ShareTable Details
	 * @param context
	 * @param map
	 * @return map
	 */
	public Map<String,String> getShareTableDetails(Context context, Map<String,String> performanceCharMap) {
		long startTime = new Date().getTime();
		Map<String,String> hmSharedTable = new HashMap<>();
		try {
			String strLevel = performanceCharMap.get(DomainConstants.SELECT_LEVEL);
			String strType = performanceCharMap.get(DomainConstants.SELECT_TYPE);				
			if ("2".equals(strLevel)) {
				Object strCPS = performanceCharMap.get("from.name");
				hmSharedTable.put("CPS", getString(context, strCPS));
			} else if ("1".equals(strLevel) && "Shared Table".equals(strType)) {
				Object strCPS1 = performanceCharMap.get("to.name");
				hmSharedTable.put("CPS", getString(context, strCPS1));
			} else {
				hmSharedTable.put("CPS", getString(context, null));
			}
		} catch(Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by getShareTableDetails method {0}",(endTime-startTime));	
		return hmSharedTable;		
	}
	

	/**
	 * @Description Method to Get Reference Documents Details
	 * @param context
	 * @param mlRefDocuments
	 * @return MapList
	 * @throws Exception
	 */
	public MapList getRefDocuments(Context context, MapList mlRefDocuments) {
		long startTime = new Date().getTime();
		try {
			StringList strSelectList = new StringList(8);
			strSelectList.addElement(DomainConstants.SELECT_NAME);
			strSelectList.addElement(DomainConstants.SELECT_TYPE);
			strSelectList.addElement(DomainConstants.SELECT_REVISION);
			strSelectList.addElement(DomainConstants.SELECT_CURRENT);
			strSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			strSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLANGUAGE);
			strSelectList.addElement(DomainConstants.SELECT_DESCRIPTION);
			strSelectList.addElement("attribute[" + DomainConstants.ATTRIBUTE_LANGUAGE + "]");
			Iterator<Map<String,String>> objectListIterator = mlRefDocuments.iterator();
			//The sonar lint comment to generalize the map is not applicable here, as the below mentioned map including both string and MapList.
			Map<String,String> mapData;
			Map object ;
			MapList mlRefDocumentsFPP ;			
			String strRefDocId ;	
			DomainObject dmObj;							
			String strTypeRD;	
			String strObjType;	
			HashMap mArgmap;						
			String[] argsFPP;							
			StringList mlRefDocumentsSource;	
			HashMap<String,String> mapParamList ;				
			HashMap mArgmapVersion ;				
			String[] argsFPPVersion ;						
			Vector mlRefDocumentsVersion;				
			String strVersion ;	
			
			while (objectListIterator.hasNext()) {
				object = objectListIterator.next();
				strRefDocId = (String) object.get(DomainConstants.SELECT_ID);	
				dmObj = DomainObject.newInstance(context, strRefDocId);			
				mapData = dmObj.getInfo(context, strSelectList);
				object.put(pgPDFViewConstants.CONST_NAME, mapData.get(DomainConstants.SELECT_NAME));
				object.put(DomainConstants.SELECT_TYPE, "Reference_Documents");
				strTypeRD = mapData.get(DomainConstants.SELECT_TYPE);		
				strObjType = UINavigatorUtil.getAdminI18NString(pgV3Constants.ELEMENT_TYPE, strTypeRD, context.getSession().getLanguage());
				object.put(pgV3Constants.OBJ_TYPE, strObjType);
				object.put(DomainConstants.SELECT_REVISION, mapData.get(DomainConstants.SELECT_REVISION));
				object.put(DomainConstants.SELECT_CURRENT, mapData.get(DomainConstants.SELECT_CURRENT));
				object.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, mapData.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
				if (pgV3Constants.TYPE_PGPKGTRANSLATIONFILE.equals(strTypeRD)) {
					object.put("LanguageFPP", mapData.get(pgV3Constants.SELECT_ATTRIBUTE_PGLANGUAGE));
				} else {
					object.put("LanguageFPP", mapData.get("attribute[" + DomainConstants.ATTRIBUTE_LANGUAGE + "]"));
				}
				object.put(DomainConstants.SELECT_DESCRIPTION, mapData.get(DomainConstants.SELECT_DESCRIPTION));
				mlRefDocumentsFPP= new MapList();							
				mlRefDocumentsFPP.add(mapData);
				mArgmap= new HashMap<>();				
				mArgmap.put(pgPDFViewConstants.CONST_OBJECTLIST, mlRefDocumentsFPP);
				argsFPP = JPO.packArgs(mArgmap);			
				mlRefDocumentsSource = showConnectionOfRefDoc(context, argsFPP);
				object.put("Source", mlRefDocumentsSource);
				mapParamList = new HashMap<>();								
				mapParamList.put("reportFormat", "CSV");
				mArgmapVersion= new HashMap<>();								
				mArgmapVersion.put(pgPDFViewConstants.CONST_OBJECTLIST, mlRefDocumentsFPP);
				mArgmapVersion.put("paramList", mapParamList);
				argsFPPVersion = JPO.packArgs(mArgmapVersion);		
				mlRefDocumentsVersion = (Vector)pgPDFViewHelper.executeMainClassMethod(context, "emxCommonDocumentUI", "getVersionStatus", argsFPPVersion);
				strVersion = DomainConstants.EMPTY_STRING;			
				int iSize = mlRefDocumentsVersion.size();
				for (int i = 0; i < iSize; i++) {
					strVersion = (String) mlRefDocumentsVersion.get(i);
				}
				object.put("Version", strVersion);
			}

			mlRefDocuments.addSortKey(pgPDFViewConstants.CONST_NAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
			mlRefDocuments.sort();
			
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total Time has taken by the getRefDocuments Method is-->{0}",(endTime-startTime));
		return mlRefDocuments;
	}

	/**
	 * @Description Method to get Master Object Details
	 * @param context
	 * @param mlInfo
	 * @param argsSubs
	 * @param strMasterid
	 * @return Map
	 */
	public Map<String,String> getMasterObjectDetails(Context context, MapList mlInfo, String[] argsSubs, String strMasterid ) {
		long startTime = new Date().getTime();
		Map<String,String> mpMasterDetails = new HashMap<>();
		try {				
				Map<String,String> mObject = (Map) mlInfo.get(0);
				String strMasterClassification = getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION));
				if (UIUtil.isNotNullAndNotEmpty(strMasterClassification) && pgV3Constants.RESTRICTED.equalsIgnoreCase(strMasterClassification)) {
					strMasterClassification = pgV3Constants.BUSINESS_USE;
				}
				String strMasterPrintingProcess = getStringValue(mObject.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS + "].to.name"));
					
				String strMasterDecorationDetails = getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGDECORATIONDETAILS));
		
				String masterObjectId = getStringValue(mObject.get(DomainConstants.SELECT_ID));
				StringList slMasterCOList = new StringList();
				StringList slMasterCAList = new StringList();
				
				getCoAndCAData(context, masterObjectId, slMasterCOList, slMasterCAList);
		
				String strMasterCO = StringUtils.join(slMasterCOList, pgPDFViewConstants.STR_COMMA);
				String strMasterCA = StringUtils.join(slMasterCAList, pgPDFViewConstants.STR_COMMA);
				Map<String,String> mpParamArg = new HashMap<>();
				mpParamArg.put(pgPDFViewConstants.CONS_OBJECTID, strMasterid);
	
				String strMasterProductForm = (String) pgPDFViewHelper.executeMainClassMethod(context, "pgDSOCPNProductData", "getConnectedProductForm", argsSubs);
				StringList slCOList = new StringList(2);		
				StringBuilder sbCOValue = new StringBuilder(2);
				sbCOValue.append(strMasterCA);
				sbCOValue.append(pgV3Constants.SYMBOL_NEXT_LINE);
				sbCOValue.append(strMasterCO);
				slCOList.addElement(sbCOValue.toString());
				//Start Code Refactoring
				String strOwnerMaster = mObject.get(DomainConstants.SELECT_OWNER);
				strOwnerMaster = PersonUtil.getFullName(context, strOwnerMaster);
				mpMasterDetails.put(DomainConstants.SELECT_TYPE, "Master Specification");
				mpMasterDetails.put("MasterTitle", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE)));
				mpMasterDetails.put("MasterDescription", getStringValue(mObject.get(DomainConstants.SELECT_DESCRIPTION)));
				mpMasterDetails.put("MasterType", UINavigatorUtil.getAdminI18NString("Type", getStringValue(mObject.get(DomainConstants.SELECT_TYPE)),context.getSession().getLanguage()));
				mpMasterDetails.put("MasterOrginator", getStringValue(mObject.get(DomainConstants.SELECT_ORIGINATOR)));
				mpMasterDetails.put("MasterRev", getStringValue(mObject.get(DomainConstants.SELECT_REVISION)));
				mpMasterDetails.put("MasterOriginated", getFormattedDate(getStringValue(mObject.get(DomainConstants.SELECT_ORIGINATED))));
				mpMasterDetails.put("MasterSegment", getStringValue(mObject.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT + "].to.name")));
				mpMasterDetails.put("MasterStage", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE)));
				mpMasterDetails.put("MasterOwner", getStringValue(strOwnerMaster));
				mpMasterDetails.put("MasterEffectiveDate", getFormattedDate(getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE))));
				mpMasterDetails.put("MasterExpirationDate", getFormattedDate(getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE))));
				mpMasterDetails.put("MasterReleaseDate", getFormattedDate(getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE))));
				mpMasterDetails.put("MasterPreviousRevDate", getFormattedDate(getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGPREVIOUSREVISIONOBSOLETEDATE))));
				mpMasterDetails.put("MasterManufacturingStatus", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGLIFECYCLESTATUS)));
				mpMasterDetails.put("MasterReasonForChange", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_REASONFORCHANGE)));
				mpMasterDetails.put("MasterLocalDescription", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGLOCALDESCRIPTION)));
				mpMasterDetails.put("MasterOtherNames", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERNAMES)));
				mpMasterDetails.put("MasterComments", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT)));
				mpMasterDetails.put("MasterObsoleteDate", getFormattedDate(getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE))));
				mpMasterDetails.put("MasterObsoleteComment", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVECOMMENT)));
				mpMasterDetails.put("MasterBrand", getStringValue(mObject.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGBRAND + "].to.name")));
				mpMasterDetails.put("MasterIsBattery", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGISBATTERY)));
				mpMasterDetails.put("MasterContainsBattery", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONTAINSBATTERY)));
				mpMasterDetails.put("MasterBatteryType", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE)));
				mpMasterDetails.put("MasterStoragelimits", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS)));
				mpMasterDetails.put("MasterBaseUnitOfMeasure", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE)));
				mpMasterDetails.put("MasterStorageInfo", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEINFORMATION)));
				mpMasterDetails.put("MasterStorageTemp", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS)));
				mpMasterDetails.put("MasterShippingInfo", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS)));
				mpMasterDetails.put("MasterShippingHazard", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGHAZARDCLASSIFICATION)));
				mpMasterDetails.put("MasterTechnology", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY)));
				mpMasterDetails.put("MasterDensityUOM", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGDENSITYUOM)));
				mpMasterDetails.put("MasterOnShelf",getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGONSHELFPRODUCTDENSITY)));
				mpMasterDetails.put("MasterSAPBOMQuantity", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY)));
				mpMasterDetails.put("MasterBaseQuantity", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY)));
				mpMasterDetails.put("MasterIntendedMarkets", getStringValue(mObject.get("from[" + pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE + "].to.name")));
				mpMasterDetails.put("MasterProductExtraVarient", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXTRAVARIANT)));
				mpMasterDetails.put("MasterTemplate", getStringValue(mObject.get("from[" + pgV3Constants.RELATIONSHIP_TEMPLATE + "].to.name")));
				mpMasterDetails.put("MasterPartFamily", getStringValue(mObject.get("to[" + pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM+ "].from[" + pgV3Constants.TYPE_PARTFAMILY + "].name")));
				mpMasterDetails.put("MasterReportedFunction", getStringValue(mObject.get("from[" + pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION + "].to.name")));
				mpMasterDetails.put("MasterClass", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGCLASS)));
				mpMasterDetails.put("MasterSubClass", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSUBCLASS)));
				mpMasterDetails.put("MasterODH", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONHEIGHT)));
				mpMasterDetails.put("MasterODL", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONLENGTH)));
				mpMasterDetails.put("MasterODW", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONWIDTH)));
				mpMasterDetails.put("MasterIDW", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONWIDTH)));
				mpMasterDetails.put("MasterIDL", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONLENGTH)));
				mpMasterDetails.put("MasterIDH", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONHEIGHT)));
				mpMasterDetails.put("MasterDIMENSIONUOM", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGDIMENSIONUOM)));
				mpMasterDetails.put("MasterPackagingMaterialType", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGMATERIALTYPE)));
				mpMasterDetails.put("MasterPackComponentType", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE)));
				mpMasterDetails.put("MasterPackagingSize", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZE)));
				mpMasterDetails.put("MasterPackagingSizeUOM", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM)));
				mpMasterDetails.put("MasterPackagingTechnology", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGTECHNOLOGY)));
				mpMasterDetails.put("MasterLabelingInfo", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION)));
				mpMasterDetails.put("MasterClassification", strMasterClassification);
				mpMasterDetails.put("MasterPrintingProcess", strMasterPrintingProcess);
				mpMasterDetails.put("MasterDecorationDetails", strMasterDecorationDetails);
				mpMasterDetails.put("MasterCO", convertToString(slCOList));
				mpMasterDetails.put("MasterProductForm", strMasterProductForm);
				mpMasterDetails.put("MasterStorageConditions", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS)));
				mpMasterDetails.put("MasterStructuredReleaseCriteria", getStringValue(mObject.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED)));
				//Added by DSM for PDF Views (Req id #49326) - Start 
				mpMasterDetails.put("MasterStructuredPerformanceCharacteristics", getStringValue(mObject.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSTRUCTUREDPERFORMANCECHARACTERISTICSREQUIRED)));
				//Added by DSM for PDF Views (Req id #49326) - End 
				mpMasterDetails.put("MasterProjectInitiativeMilestone", getStringValue(mObject.get(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPROJECTMILESTONE)));
				mpMasterDetails.put("MasterTemperatureLimits", getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS)));
				mpMasterDetails.put("MasterSAPType",getStringValue(mObject.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE)));
			} catch (Exception e) {
				logger.log(Level.WARNING, null, e);
			}
			long endTime = new Date().getTime();
			logger.log(Level.INFO,"Total Time has taken by the getMasterObjectDetails Method is-->{0}",(endTime-startTime));
			return mpMasterDetails;
	}

	/**
	 * @Description Method to Get Master Specification Data
	 * @param context
	 * @param mlSubClass
	 * @param strTypes
	 * @param strReferenceTypeAttribute
	 * @return MapList
	 * @throws Exception
	 */
	public MapList getMasterSpecData(Context context, MapList mlSubClass,String strTypes, String strReferenceTypeAttribute) {
		long startTime = new Date().getTime();
		Map<String,String> mpMasterSpecItr = null;
		Map<String,String> mpMasterSpec = new HashMap<>();				
		MapList mlMasterSpecsList = new MapList();
		StringList slSelects = new StringList(6);
		slSelects.addElement(DomainConstants.SELECT_NAME);
		slSelects.addElement(DomainConstants.SELECT_TYPE);
		slSelects.addElement(DomainConstants.SELECT_CURRENT);
		slSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		slSelects.addElement("to["+ pgV3Constants.RELATIONSHIP_PARTSPECIFICATION +"].from.name");
		
		//Added by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - Starts
		StringList slRelSelects = new StringList(2);
		slRelSelects.addElement("attribute["+ pgPDFViewConstants.ATTR_PRIMARY_ARTWORK +"]");
		slRelSelects.addElement(DomainConstants.SELECT_FROM_NAME);
		//Added by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - Ends
		Map<String,MapList> argsMapMasterSpec = new HashMap<>();
		try {
		argsMapMasterSpec.put(pgPDFViewConstants.CONST_OBJECTLIST, mlSubClass); 
		String[] argsForMasterSpec = JPO.packArgs(argsMapMasterSpec);
		StringList slMasterSpecSubtype = (StringList) pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CLASS_EMXCPNPRODUCTDATA, "getSpecificationSubtype", argsForMasterSpec);
		int i = 0;
		String strMasterArtworkPrimary;
		String strMasterPartName;
		String sConnectedObjType;
		String strIpConnection;
		String[] strArrIds = new String[1];
		MapList mlConnectedObjTypes =null;
		String strId ; 	
		Map<String,String> mapInfo ;
		DomainObject partSpecObject = null;				
		String strType ;	
		String strDSOType ;
		
		for (Iterator<Map<String,String>> iterator = mlSubClass.iterator(); iterator.hasNext();) {
			mpMasterSpecItr =  iterator.next();
			strId =mpMasterSpecItr.get(DomainConstants.SELECT_ID);
			mpMasterSpec = new HashMap<>();	
			if (!(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strTypes)) && "R".equals(strReferenceTypeAttribute)) {
				strIpConnection= mpMasterSpecItr.get("id[connection]");
				strArrIds[0]=strIpConnection;
				//Modified by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - Start
				mlConnectedObjTypes = DomainRelationship.getInfo(context, strArrIds, slRelSelects);
				if(null!=mlConnectedObjTypes && !mlConnectedObjTypes.isEmpty())
				{
					mapInfo=(Map)mlConnectedObjTypes.get(0);
					sConnectedObjType = mapInfo.get(DomainConstants.SELECT_FROM_NAME);
					mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSEPCIFICATIONMASTERPARTNAME, sConnectedObjType);
					strMasterArtworkPrimary = mapInfo.get("attribute["+ pgPDFViewConstants.ATTR_PRIMARY_ARTWORK +"]");
					if(UIUtil.isNotNullAndNotEmpty(strMasterArtworkPrimary) && pgV3Constants.TRUE.equalsIgnoreCase(strMasterArtworkPrimary)) {
						strMasterArtworkPrimary = pgV3Constants.KEY_YES_VALUE;
					} else {
						strMasterArtworkPrimary = pgV3Constants.KEY_NO_VALUE;
					}
					mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSEPCIFICATIONAP,strMasterArtworkPrimary);
				}
				//Modified by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - Ends
			}
			partSpecObject = DomainObject.newInstance(context, strId);				
				mapInfo = partSpecObject.getInfo(context, slSelects);					// Check 
			mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSEPCIFICATIONNAME,  mapInfo.get(DomainConstants.SELECT_NAME)); 
			mpMasterSpec.put(DomainConstants.SELECT_TYPE,  pgPDFViewConstants.CONST_MASTERSEPCIFICATION); 
			strType =  mapInfo.get(DomainConstants.SELECT_TYPE);		
			strDSOType = UINavigatorUtil.getAdminI18NString(pgPDFViewConstants.CONST_TYPE, strType,	
					context.getSession().getLanguage());
			mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSEPCIFICATIONTYPE, strDSOType); 
			mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSPECIFICATIONSTATE,
					mapInfo.get(DomainConstants.SELECT_CURRENT)); 
			mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSEPCIFICATIONTITLE, 
					 mapInfo.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE)); 
			mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSPECIFICATIONSUBTYPE, slMasterSpecSubtype.get(i));
			strMasterPartName =  mapInfo.get("to["+ pgV3Constants.RELATIONSHIP_PARTSPECIFICATION +"].from.name");
			mpMasterSpec.put(pgPDFViewConstants.CONST_MASTERSEPCIFICATIONMSPN,strMasterPartName);
			i++;
			mlMasterSpecsList.add(mpMasterSpec);
		}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by getMasterSpecData method {0}",(endTime-startTime));
		return mlMasterSpecsList;
	}

	/**
	 * @Description Method to Get Related Specification Details
	 * @param context
	 * @param mlSpecificatins
	 * @param masterObj
	 * @param strOriginatingSource
	 * @return Map
	 * @throws Exception
	 */
	public MapList getRelatedSpecData(Context context, MapList mlSpecificatins, DomainObject masterObj,String strOriginatingSource){
		long startTime = new Date().getTime();					
		//The sonar lint comment to generalize the map is not applicable here, as the below mentioned map including both string and vector.
		MapList mlRelatedSpecsList = new MapList();
		try {
			Map mpRelatedSpec ; 		
			Map<String,String> mpRelatedSpecItr = null;
			String strRelatedSpecState ;
			String strSpecDisplayType;
			Map<String,MapList> argsMapRelSpec = new HashMap<>();
			argsMapRelSpec.put(pgPDFViewConstants.CONST_OBJECTLIST, mlSpecificatins); 
			String[] argsForRelSpec = JPO.packArgs(argsMapRelSpec);
			Vector vSpecSubtype = (Vector) pgPDFViewHelper.executeMainClassMethod(context, pgPDFViewConstants.CONS_PGIPMTABLESJPO, "getSpecificationSubtype", argsForRelSpec);
			int i = 0;
	
			String strDescription;
			String strState;
			//Added by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - Start
			StringList slRelSelectable = new StringList(2);
			slRelSelectable.addElement("attribute["+ pgPDFViewConstants.ATTR_PRIMARY_ARTWORK +"]");
			slRelSelectable.addElement("attribute["+ pgPDFViewConstants.ATTR_INHERITANCE_TYPE +"]");
			//Added by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - Ends
			
			Map<String, String> mapObjDataDetails = null;
			String strArtPrimary = DomainConstants.EMPTY_STRING ;
			String strInheritanceType = DomainConstants.EMPTY_STRING;
			
			String strSpecType ;			
			String strSpecId;			
			MapList commomML ;							
			HashMap<String,String> commonMap ;							
			StringList mlRefDocumentsSource;			
			DomainObject dmObj = null;									
			boolean havingReadAccess = false;
			for (Iterator<Map<String,String>> iterator = mlSpecificatins.iterator(); iterator.hasNext();) {
		    	mpRelatedSpecItr =  iterator.next();
				strRelatedSpecState =  mpRelatedSpecItr.get(DomainConstants.SELECT_CURRENT);
				if (pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strRelatedSpecState)) {
					i++;
					continue;
				}
				strSpecType =  mpRelatedSpecItr.get(DomainConstants.SELECT_TYPE);	
				strSpecDisplayType = UINavigatorUtil.getAdminI18NString(pgPDFViewConstants.CONST_TYPE, strSpecType,
						context.getSession().getLanguage());
				mpRelatedSpec = new HashMap<>();	
				strSpecId = mpRelatedSpecItr.get(DomainConstants.SELECT_ID);	
	
				if (!pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strSpecType)) {
					commomML = new MapList();	
					commonMap = new HashMap<>();	
					commonMap.put(DomainConstants.SELECT_ID, strSpecId);
					commomML.add(commonMap);
					argsMapRelSpec = new HashMap<>();		
					argsMapRelSpec.put(pgPDFViewConstants.CONST_OBJECTLIST, commomML); 
					argsForRelSpec = JPO.packArgs(argsMapRelSpec);
					mlRefDocumentsSource = showConnectionOfRefDoc(context, argsForRelSpec); 
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONSOURCE, mlRefDocumentsSource); 
				}
				dmObj = DomainObject.newInstance(context, strSpecId);	
				//Added by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - Start
				String[] sRelId = new String[1];
				String sRelConnection =  mpRelatedSpecItr.get(DomainConstants.SELECT_RELATIONSHIP_ID); 
				sRelId[0] = sRelConnection;
		    	MapList mlRelSelectList = DomainRelationship.getInfo(context, sRelId, slRelSelectable);
		    	if(mlRelSelectList!=null && !mlRelSelectList.isEmpty()) {
		    		Map mRelAtr = (Map)mlRelSelectList.get(0);
		    		strArtPrimary = (String) mRelAtr.get("attribute["+ pgPDFViewConstants.ATTR_PRIMARY_ARTWORK +"]");
		    		strInheritanceType = (String) mRelAtr.get("attribute["+ pgPDFViewConstants.ATTR_INHERITANCE_TYPE +"]");
		    	}
		    	//Added by DSM(Sogeti)-2018x.6 for PDF Views (Defect ID-42184) - End
				if(UIUtil.isNotNullAndNotEmpty(strArtPrimary) && pgV3Constants.TRUE.equalsIgnoreCase(strArtPrimary)) {
					strArtPrimary = pgV3Constants.KEY_YES_VALUE;
				} else {
					strArtPrimary = pgV3Constants.KEY_NO_VALUE;
				}
				havingReadAccess = dmObj.checkAccess(context, (short) AccessConstants.cRead);		
				mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONDISPLAYTYPE , strSpecDisplayType); 
	
				if (havingReadAccess) {
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONSUBTYPE, vSpecSubtype.get(i));
					i++;
					mpRelatedSpec.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PRODUCTDATA); 
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SEPCIFICATIONNAME,
							 mpRelatedSpecItr.get(DomainConstants.SELECT_NAME));
					strDescription = mpRelatedSpecItr.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE); 
					strDescription = strDescription.replace(pgV3Constants.DENIED, pgPDFViewConstants.CONST_NO_ACCESS);
	
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONSAPDESC,getRelatedSpecValue(context,mpRelatedSpecItr,masterObj,strDescription));
	
					strState = mpRelatedSpecItr.get(DomainConstants.SELECT_CURRENT);
					strState = strState.replace(pgV3Constants.DENIED, pgPDFViewConstants.CONST_NO_ACCESS);//code refactor
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONSTATE, strState);
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONORIGINATOR,
							 mpRelatedSpecItr.get(pgV3Constants.SELECT_ATTRIBUTE_ORIGINATOR));
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONAP, strArtPrimary); 
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONIHT, strInheritanceType);
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONDESCRIPTION, dmObj.getDescription(context));
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONREVISION, mpRelatedSpecItr.get(pgPDFViewConstants.CONST_REVISION));
					
					mlRelatedSpecsList.add(mpRelatedSpec);
					
				} else {
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONSUBTYPE, pgPDFViewConstants.CONST_NO_ACCESS);
					i++;
					mpRelatedSpec.put(DomainConstants.SELECT_TYPE, pgPDFViewConstants.CONST_PRODUCTDATA); 
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SEPCIFICATIONNAME,
							 mpRelatedSpecItr.get(DomainConstants.SELECT_NAME)); 
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONSAPDESC, pgPDFViewConstants.CONST_NO_ACCESS);
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONSTATE, pgPDFViewConstants.CONST_NO_ACCESS);
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONORIGINATOR, pgPDFViewConstants.CONST_NO_ACCESS);
					
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONDESCRIPTION,  pgPDFViewConstants.CONST_NO_ACCESS);
					mpRelatedSpec.put(pgPDFViewConstants.CONST_SPECIFICATIONREVISION,  pgPDFViewConstants.CONST_NO_ACCESS);
					mlRelatedSpecsList.add(mpRelatedSpec);
				}
	
			}
			
	
			if (pgV3Constants.DSM_ORIGIN.equals(strOriginatingSource)) {
				mlRelatedSpecsList.addSortKey(pgPDFViewConstants.CONST_SEPCIFICATIONNAME, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
			} else {
				mlRelatedSpecsList.addSortKey(pgPDFViewConstants.CONST_SPECIFICATIONDISPLAYTYPE, pgPDFViewConstants.CONST_ASCENDING, pgPDFViewConstants.CONST_STRING);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		mlRelatedSpecsList.sort();
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by getRelatedSpecData method {0}",(endTime-startTime));
		return mlRelatedSpecsList;
	}
	
	public String getRelatedSpecValue(Context context, Map<String,String> mpRelatedSpecItr,DomainObject masterObj,String strDescription){
		
		String strAuthoringApplication = DomainConstants.EMPTY_STRING;
		String vName = DomainConstants.EMPTY_STRING;
		String strRelatedSpec= DomainConstants.EMPTY_STRING;
		try {
		strAuthoringApplication =  masterObj.getInfo(context,			
				pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		vName =  mpRelatedSpecItr.get(pgPDFViewConstants.SELECT_ATTRIBUTE_V_NAME);
		if (UIUtil.isNotNullAndNotEmpty(strAuthoringApplication)
				&& pgPDFViewConstants.CATIA_LPD.equals(strAuthoringApplication)) { 
			if (UIUtil.isNotNullAndNotEmpty(vName)) {
				strRelatedSpec=vName;
			} else {
				strRelatedSpec=strDescription;
			}
		} else {
			strRelatedSpec=strDescription;
		}
		}catch(Exception e)
		{
			logger.log(Level.WARNING, null, e);
		}
		return strRelatedSpec;
	}
	/**
	 * @Description Method to get Platform and Chassis Details
	 * @param context
	 * @param objectId
	 * @param mlBusinessObjectDataList
	 * @param mapMasterDomainObject
	 * @return Map
	 * @throws Exception
	 */
	public Map<String,String> getAllPlatformAndChassisData(Context context, String objectId, MapList mlBusinessObjectDataList,
			Map<String,String> mapMasterDomainObject) {
		long startTime = new Date().getTime();
		HashMap<String,String> settings  = new HashMap<>();
		Map<String,String> mpProductFormDetails = null;
		try {
		if(UIUtil.isNotNullAndNotEmpty(objectId)) {
			
			// Added by DSM Sogeti for 2022x.03 Aug CW for PDF Views (Req -47318) - Start
			// setting for Attribute - FIS Reference (which is retrieved via relationship expansion)
            mpProductFormDetails = new HashMap<>();
            mpProductFormDetails = getFISReference(context, objectId);
            if (!mpProductFormDetails.isEmpty()) {
				mlBusinessObjectDataList.add(mpProductFormDetails);
            }
			// Added by DSM Sogeti for 2022x.03 Aug CW for PDF Views (Req -47318) - End
			
            // settings for Attribute Franchise
			settings.put(pgPDFViewConstants.CONST_RELATIONSHIPATTR,pgPDFViewConstants.CONST_FRANCHISEPLATFORM); 
			settings.put(pgPDFViewConstants.CONST_FIELDNAME,pgPDFViewConstants.CONST_PGFRANCHISEPLATFORM); 
			
			mpProductFormDetails = new HashMap<>();
			mpProductFormDetails.put(pgPDFViewConstants.CONST_FRANCHISE,getDisplayAllPlatformAndChassisValue(context,objectId,settings));
			mlBusinessObjectDataList.add(mpProductFormDetails);
			settings.clear();
			

			
			// settings for Attribute Product Category Platform
			settings.put(pgPDFViewConstants.CONST_RELATIONSHIPATTR,pgPDFViewConstants.CONST_PRODUCT_CATEGORY_PLATFORM); 
			settings.put(pgPDFViewConstants.CONST_FIELDNAME,pgPDFViewConstants.CONST_PGPRODUCTCATEGORYPLATFORM); 
			
			mpProductFormDetails = new HashMap<>();
			mpProductFormDetails.put(pgPDFViewConstants.CONST_PRODUCTCATEGORYPLATFORM,getDisplayAllPlatformAndChassisValue(context,objectId,settings));
			mlBusinessObjectDataList.add(mpProductFormDetails);
			settings.clear();
			
			// settings for Attribute Product Technology Platform
			settings.put(pgPDFViewConstants.CONST_RELATIONSHIPATTR,pgPDFViewConstants.CONST_PRODUCT_TECHNOLOGY_PLATFORM); 
			settings.put(pgPDFViewConstants.CONST_FIELDNAME,pgPDFViewConstants.CONST_PGPRODUCTCATEGORYPLATFORM); 
			
			mpProductFormDetails = new HashMap<>();
			mpProductFormDetails.put("ProductTechnologyPlatform",getDisplayAllPlatformAndChassisValue(context,objectId,settings));
			mlBusinessObjectDataList.add(mpProductFormDetails);
			settings.clear();
			
		
			// settings for Attribute Product Technology Chassis
			settings.put(pgPDFViewConstants.CONST_RELATIONSHIPATTR,pgPDFViewConstants.CONST_PRODUCT_TECHNOLOGY_CHASSIS); 
			settings.put(pgPDFViewConstants.CONST_FIELDNAME,pgPDFViewConstants.CONST_PGPRODUCTTECHNOLOGYCHASSIS); 
			
			mpProductFormDetails = new HashMap<>();
			mpProductFormDetails.put(pgPDFViewConstants.CONST_PRODUCTTECHNOLOGYCHASSIS,getDisplayAllPlatformAndChassisValue(context,objectId,settings));
			mlBusinessObjectDataList.add(mpProductFormDetails);
			
			// Added by DSM (Sogeti) for 22x.1 - Defect 51450 - Start
			// Get FPP related Supporting Doc	
			if (isQualifiedForWnDSupportDoc(context, objectId)) {
				mpProductFormDetails = new HashMap<>();
				UIProgramUtil util = new UIProgramUtil(context);
				String supportingDocx = util.getFinishedProductRelatedSupportingDoc(objectId);
				mpProductFormDetails.put("SupportingDocx", supportingDocx);
				mlBusinessObjectDataList.add(mpProductFormDetails);
			}
			// Added by DSM (Sogeti) for 22x.1 - Defect 51450 - End
			
			mapMasterDomainObject = consolidateRows(context, mlBusinessObjectDataList);
			settings.clear();
		}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
		long endTime = new Date().getTime();
		logger.log(Level.INFO,"Total time taken by getAllPlatformAndChassisData method {0}",(endTime-startTime));
		return mapMasterDomainObject;
	}
	
	/**
	 * Added by DSM (Sogeti) for 22x.1 - Defect 51450
     * @param context 
     * @param objectId
     * @return
     * @throws FrameworkException
     */
    public boolean isQualifiedForWnDSupportDoc(Context context, String objectId) throws FrameworkException {
        boolean qualified = false;
        StringList selectList = new StringList();
        selectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        selectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
        selectList.add(DomainConstants.SELECT_TYPE);

        DomainObject domainObject = DomainObject.newInstance(context, objectId);
        Map objectInfo = domainObject.getInfo(context, selectList);
        if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase((String)objectInfo.get(DomainConstants.SELECT_TYPE))) {
            String assemblyType = (String) objectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
            String sapType = (String) objectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
            if(!pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(assemblyType) && !"HALB".equalsIgnoreCase(sapType)) {
                qualified = true;
            }
        }
        return qualified;
    }
	
	/**
	 * Added by DSM Sogeti for 2022x.03 Aug CW for PDF Views (Req -47318)
     * @param context
     * @param objectId
     * @return
     * @throws FrameworkException
     */
    public Map<String, String> getFISReference(Context context, String objectId) throws FrameworkException {
        Map<String, String> resultMap = new HashMap<>();
        try {
            if (UIUtil.isNotNullAndNotEmpty(objectId)) {
                DomainObject domObject = DomainObject.newInstance(context, objectId);
                StringList formulationProcessList = new StringList();
                if (domObject.isKindOf(context, pgV3Constants.TYPE_FORMULATIONPART)) {
                    formulationProcessList = domObject.getInfoList(context, "from[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].to.id");
                }
                if (null != formulationProcessList && !formulationProcessList.isEmpty()) {
                    MapList infoList = DomainObject.getInfo(context, formulationProcessList.toArray(new String[formulationProcessList.size()]), StringList.create(pgPDFViewConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER), new StringList());
                    StringBuffer resultBuffer = new StringBuffer();
                    Map<Object, Object> infoMap;
                    String fisReference;
                    for (Object objectMap : infoList) {
                        infoMap = (Map<Object, Object>) objectMap;
                        fisReference = (String) infoMap.get(pgPDFViewConstants.SELECT_ATTRIBUTE_REFERENCE_IDENTIFIER);
                        if (UIUtil.isNotNullAndNotEmpty(fisReference)) {
                            if (!resultBuffer.toString().isEmpty()) {
                                resultBuffer.append(pgV3Constants.SYMBOL_COMMA);
                                resultBuffer.append(pgV3Constants.SYMBOL_SPACE);
                            }
                            resultBuffer.append(fisReference);
                        }
                    }
                    String retString = resultBuffer.toString();
                    if (UIUtil.isNotNullAndNotEmpty(retString)) {
                        resultMap.put(pgPDFViewConstants.CONST_FIS_REFERENCE, retString);
                    }
                }
            }
        } catch (FrameworkException e) {
            throw e;
        }
        return resultMap;
    }
	

    /**************************************************************************************************************************************\
     *    Cloud GenDoc Code starts
     ***************************************************************************************************************************************/
     /**
	 * @param context
	 * @param args
	 * @param cloudConfig
	 * @return
	 * @throws matrix.util.MatrixException
	 */
	public Map<String, String> beginCloudGenDocProcess(Context context, String args[], ICloudConfig cloudConfig) throws MatrixException {
		// Continuous improvement 2018x.6 Sept-CW (Defect 44025)
		// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
		Instant processStartTime = Instant.now();
		if (args == null || args.length < 1 || args[0] == null || "".equals(args[0]) || "null".equals(args[0])) {
			throw new MatrixException("Not valid arguments!");
		}
		Map<String, String> returnMap = new HashMap<String, String>();
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		int returnInteger = 1;
		String returnMessage = DomainConstants.EMPTY_STRING;
		boolean isTimedOut = Boolean.FALSE;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends-

		boolean isPushContext = false;
		try {
			if (isExternalRoleAssigned(context)) {
				isPushContext = true;
				// user does not have access, so push context required.
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			}
			String inputOid = args[0].trim();
			String inputViewIdentifier = args[1].trim();
			String inputContextUser = args[2].trim();
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
			String rootFolderPrefix = args[3].trim();
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
			DomainObject inputObj = DomainObject.newInstance(context);
			if (UIUtil.isNotNullAndNotEmpty(inputOid)) {
				inputObj.setId(inputOid);
			}
			Map<String, String> inputObjInfo = inputObj.getInfo(context, getRequiredSelectablesForRenderer());
			inputObj.close(context);
			inputObjInfo.put(CloudConstants.Basic.INPUT_OBJECT_ID.getValue(), inputOid);
			inputObjInfo.put(CloudConstants.Basic.INPUT_VIEW_IDENTIFIER.getValue(), inputViewIdentifier);
			inputObjInfo.put(CloudConstants.Basic.INPUT_CONTEXT_USER.getValue(), inputContextUser);

			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
			inputObjInfo.put(CloudConstants.Basic.GEN_DOC_BLOB_ROOT_FOLDER_NAME.getValue(), rootFolderPrefix);
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

			String loggerFile = cloudConfig.getCloudGenDocCustomLoggerConfigFilePath();
			logger.log(Level.INFO, "Custom Logger File {0}", loggerFile);
			logger.log(Level.INFO, "Initialize logger Start");
			GenDocClient genDocClient = new GenDocClient();
			genDocClient.initializeLogger(loggerFile);
			logger.log(Level.INFO, "Initialize logger End");
			logger.log(Level.INFO, "Create Instance of ICloudDocument Start");
			ICloudDocument cloudDocument = new DigitalSpec(context, inputObjInfo, cloudConfig);
			logger.log(Level.INFO, "Create Instance of ICloudDocument End");
			CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
			cloudGenDocUtil.writeGenDocWorkTextFile(cloudDocument);
			if (cloudDocument.isLoaded()) {
				logger.log(Level.INFO, "ICloudDocument instance created");

				final boolean bIsNonStructuredTypes = isNonStructuredTypes(cloudDocument);
				logger.log(Level.INFO, "Is Non-Structured Type {0}", bIsNonStructuredTypes);

						String downloadDir = cloudDocument.getAbsoluteDownloadDir();
						String absoluteCheckOutDir = cloudDocument.getAbsoluteCheckOutDir();
						String inWorkDir = cloudDocument.getInWorkDir();

						logger.log(Level.INFO, "Digital Specification - Work Directories");
						logger.log(Level.INFO, "Work Dir {0}", cloudDocument.getWorkDir());
						logger.log(Level.INFO, "Work Dir File {0}", cloudDocument.getWorkDirFile());
						logger.log(Level.INFO, "Work Dir File Path {0}", cloudDocument.getWorkDirFilePath());
						logger.log(Level.INFO, "Work Download Dir {0}", downloadDir);
						logger.log(Level.INFO, "Work Merge Dir {0}", cloudDocument.getAbsoluteMergeDir());
						logger.log(Level.INFO, "Work Blob Dir {0}", cloudDocument.getWorkDirBlobPath());
						logger.log(Level.INFO, "Absolute Checkout Directory {0}", absoluteCheckOutDir);
						logger.log(Level.INFO, "In Work Directory {0}", inWorkDir);

				inputObjInfo.put(pgPDFViewConstants.CONT_UNSTRUCTUREDTYPES, cloudDocument.getUnStructuredTypes());
				StringBuffer stringBufferHTML = new StringBuffer();
				StringBuffer stringBufferXMLContent = new StringBuffer();
				StringBuffer stringBufferGenDocFileName = new StringBuffer();
				StringBuffer stringBufferGenDocFilePath = new StringBuffer();

				// below variable are dummy assignment.
				ftpInputFolder = cloudDocument.getAbsoluteCheckOutDir();
				ftpOutputFolder = cloudDocument.getAbsoluteDownloadDir();
				vHTMLInputFiles = new Vector();
				vHTMLInputFiles.add(cloudDocument.getHtmlFileName());

				logger.log(Level.INFO, "Call Process View start");
				processView(context, cloudDocument, inputObjInfo,
						stringBufferHTML,
						stringBufferXMLContent,
						stringBufferGenDocFileName,
						stringBufferGenDocFilePath);
				logger.log(Level.INFO, "Call Process View end");

				logger.log(Level.INFO, "Html length {0}", stringBufferHTML.length());
				logger.log(Level.INFO, "File Name {0}", cloudDocument.getFileName());
				logger.log(Level.INFO, "GenDoc File Name {0}", stringBufferGenDocFileName);
				logger.log(Level.INFO, "GenDoc File Path {0}", stringBufferGenDocFilePath);

				if (bIsNonStructuredTypes) {
					logger.log(Level.INFO, "Non-Structured Types");
					final GenDocProcessResponse genDocPdf = getGenDocPdf(context, cloudDocument, inputObjInfo, stringBufferHTML, stringBufferGenDocFileName, stringBufferGenDocFilePath);
					returnInteger = genDocPdf.getReturnInteger();
					returnMessage = genDocPdf.getReturnMessage();
					isTimedOut = genDocPdf.isTimedOut();
					logger.log(Level.INFO, "Non-Structured Pdf Process|Is Timed Out -> {0}|Returned Integer -> {1}|Returned Message -> {2}", new Object[]{isTimedOut, returnInteger, returnMessage});
				} else {
					logger.log(Level.INFO, "Structured Types - Generate Html to Pdf using iText");
					returnMessage = iTextGenerateStructuredPDF(context, cloudDocument, inputObjInfo, stringBufferHTML);
					if(UIUtil.isNotNullAndNotEmpty(returnMessage)) {
						logger.log(Level.INFO, "Structured Types - Generated Html to Pdf using iText Path {0}", returnMessage);
						File outPdf = new File(returnMessage);
						if (outPdf.exists()) {
							logger.log(Level.INFO, "Structured Types - Generated Html to Pdf Exist.");
							returnInteger = 0;
							returnMessage = DomainConstants.EMPTY_STRING;
						} else {
							logger.log(Level.WARNING, "Structured Types - Failed to generat Html to Pdf.");
						}
					} else {
						logger.log(Level.WARNING, "Structured Types - Failed to generat Html to Pdf.");
					}
				}
			} else {
				returnMessage = CloudConstants.Basic.CLOUD_OBJECT_INSTANTIATION_FAILED.getValue();
				isTimedOut = Boolean.TRUE;
				logger.log(Level.WARNING, CloudConstants.Basic.CLOUD_OBJECT_INSTANTIATION_FAILED.getValue());
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
			returnMessage = e.getMessage();
			isTimedOut = Boolean.TRUE;
			// Added/Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		} finally {
			if (isPushContext && pgV3Constants.PERSON_USER_AGENT.equals(context.getUser())) {
				ContextUtil.popContext(context);
			}
		}
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		returnMap.put(CloudConstants.Basic.GEN_DOC_RETURN_TYPE.getValue(), String.valueOf(returnInteger)); // convert integer to String.
		returnMap.put(CloudConstants.Basic.GEN_DOC_RETURN_ERROR_MESSAGE.getValue(), returnMessage);
		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

		logger.log(Level.INFO, "DSM Azure GenDoc Final Map {0}", returnMap);
		Instant processEndTime = Instant.now();
		Duration duration = Duration.between(processStartTime, processEndTime);
		logger.log(Level.INFO, "DSM Azure Gendoc Process - took|{0} ms|{1} sec|{2} min", new Object[]{duration.toMillis(), duration.getSeconds(), duration.toMinutes()});
		return returnMap;
	}


    /**
     * @param context
     * @param cloudDocument
     * @param cloudGenDocUtil
     * @param totalCountForMerge
     * @return
     */
	public GenDocProcessResponse iTextProcessInvoke(Context context, ICloudDocument cloudDocument, CloudGenDocUtil cloudGenDocUtil, int totalCountForMerge) {
		// Continuous improvement 2018x.6 Sept-CW (Defect 44025)
		// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
		// Added/Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		int returnInteger = 1;
		String returnMessage = DomainConstants.EMPTY_STRING;
		boolean isTimedOut = Boolean.FALSE;
		Map<Object, Object> returnMap = new HashMap<>();
		// Added/Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

        String downloadDir = cloudDocument.getAbsoluteDownloadDir();
        String absoluteCheckOutDir = cloudDocument.getAbsoluteCheckOutDir();
        String inWorkDir = cloudDocument.getInWorkDir();
        logger.log(Level.INFO, "Total PDF Count for Merge {0}", totalCountForMerge);

        // move checkout pdf + itext generated pdf files to download folder.
		logger.log(Level.INFO, "Move iText Generated PDF to download folder start");
        cloudGenDocUtil.moveCheckOutPDFFromInWorkToDownloadFolder(inWorkDir, downloadDir);
		logger.log(Level.INFO, "Move iText Generated PDF to download folder end");

        // generate pdf using iText.
		logger.log(Level.INFO, "Convert checkout documents to PDF using iText start");
        PDFConvert pdfConvert = new PDFConvert.Builder(cloudDocument).convert();
		logger.log(Level.INFO, "Convert checkout documents to PDF using iText end");

        // check if pdf conversion was successful
        if (pdfConvert.isSuccessful()) {
			logger.log(Level.INFO, "Convert checkout documents to PDF using iText was successful");
            int inputDocumentsCount = pdfConvert.getInputDocumentsCount();
            int convertedPDFDocumentsCount = pdfConvert.getConvertedPDFDocumentsCount();

			logger.log(Level.INFO, "Number of input documents: {0}", inputDocumentsCount);
			logger.log(Level.INFO, "Number of converted documents: {0}", convertedPDFDocumentsCount);

            if (inputDocumentsCount == convertedPDFDocumentsCount) {
                logger.info("Number of files for iText conversion matches with Number of converted PDF documents");

                File downloadFile = new File(downloadDir);
                int downloadFileCount = downloadFile.list().length;
                boolean bIsCountMatch = false;

				logger.log(Level.INFO, "Number of PDF in Download Folder: {0}", downloadFileCount);
				logger.log(Level.INFO, "Number of PDF To be Present in Download Folder: {0}", totalCountForMerge);

                if (downloadFileCount == totalCountForMerge) {
                    bIsCountMatch = true;
                }
                if (bIsCountMatch) {
					logger.log(Level.INFO, "PDF count in Download Folder matches with Total required PDF count");
                    // merge all pdf into one.
					logger.log(Level.INFO, "Merge PDF start");
                    PDFMerge pdfMerge = new PDFMerge.Builder(cloudDocument).build(Boolean.TRUE);
					logger.log(Level.INFO, "Merge PDF end");
                    if (pdfMerge.isSuccessful()) {
						logger.log(Level.INFO, "Merge PDF was successful");
                        // check-in the final pdf.
						logger.log(Level.INFO, "Checkin PDF start");
                        checkInMergedPDF(context, cloudDocument, downloadDir);
						// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
						returnInteger = 0;
						logger.log(Level.INFO, "Checkin PDF end");
                    } else {
						logger.log(Level.WARNING, "Merge PDF failed");
						returnInteger = 1;
						returnMessage = pdfMerge.getErrorMessage();
						isTimedOut = Boolean.TRUE;
                    }
                } else {
					logger.log(Level.WARNING, "PDF count in Download Folder does not match with Total required PDF count");
					returnInteger = 1;
					returnMessage = CloudConstants.Basic.UPLOAD_DOWNLOAD_COUNT_MISMATCH.getValue();
					isTimedOut = Boolean.TRUE;
				}
			} else {
				logger.log(Level.WARNING, "Number of files for iText conversion does not matches with Number of converted PDF documents");
				returnInteger = 1;
				returnMessage = pdfConvert.getErrorMessage();
				isTimedOut = Boolean.TRUE;
            }
        } else {
			logger.log(Level.WARNING, "Convert checkout documents to PDF using iText failed");
			// pdf conversion failed for one or more documents.
			returnInteger = 1;
			returnMessage = pdfConvert.getErrorMessage();
			isTimedOut = Boolean.TRUE;
        }
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_INTEGER.getValue(), returnInteger);
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_STRING.getValue(), returnMessage);
		returnMap.put(CloudConstants.Basic.KEY_WORD_TIMED_OUT.getValue(), isTimedOut);
        return new GenDocProcessResponse(returnMap); // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
    }


    /**
     * @param resultMap
     * @return
     */
    public Map<Object, Object> isInvokeCloudGenDocProcessSuccess(Map<Object, Object> resultMap) {
        boolean isCloudProcessSuccess = true;
        String returnType = CloudConstants.Basic.GEN_DOC_RETURN_TYPE.getValue();
        String returnMessage = CloudConstants.Basic.GEN_DOC_RETURN_ERROR_MESSAGE.getValue();
        String returnNumerical = CloudConstants.Basic.GEN_DOC_RETURN_NUMERICAL_STRING_ONE.getValue();
        Map<Object, Object> returnMap = new HashMap<>();

        String returnTypeValue = DomainConstants.EMPTY_STRING;
        String returnErrorMessage = DomainConstants.EMPTY_STRING;

        if (resultMap.containsKey(returnType)) {
            if (resultMap.get((String) resultMap.get(returnType)).equals("1")) {
                returnTypeValue = (String) resultMap.get(returnType);
                isCloudProcessSuccess = false;
            }
        }
        if (resultMap.containsKey(returnMessage)) {
            if (UIUtil.isNotNullAndNotEmpty((String) resultMap.get(returnMessage))) {
                returnErrorMessage = (String) resultMap.get(returnMessage);
                isCloudProcessSuccess = false;
            }
        }
        if (resultMap.containsKey(CloudConstants.Basic.KEYWORD_ERROR.getValue())) {
            if (UIUtil.isNotNullAndNotEmpty((String) resultMap.get(CloudConstants.Basic.KEYWORD_ERROR.getValue()))) {
                isCloudProcessSuccess = false;
                returnErrorMessage = (String) resultMap.get(CloudConstants.Basic.KEYWORD_ERROR.getValue());
            }
        }
        if (resultMap.containsKey(CloudConstants.Basic.KEYWORD_TIMEOUT.getValue())) {
            if (UIUtil.isNotNullAndNotEmpty((String) resultMap.get(CloudConstants.Basic.KEYWORD_TIMEOUT.getValue()))) {
                isCloudProcessSuccess = false;
                returnErrorMessage = CloudConstants.Basic.CLOUD_GENDOC_TIMEOUT.getValue();
            }
        }
        returnMap.put(returnType, returnTypeValue);
        returnMap.put(returnMessage, returnErrorMessage);
        returnMap.put("IsCloudProcessSuccess", new Boolean(isCloudProcessSuccess));
        return returnMap;
    }

	/**
	 * @param context
	 * @param cloudDocument
	 * @param cloudDocuments
	 * @return
	 */
	public GenDocProcessResponse cloudGenDocProcessInvoke(Context context, ICloudDocument cloudDocument, List<Document> cloudDocuments) throws InterruptedException, URISyntaxException, StorageException {
		// Continuous improvement 2018x.6 Sept-CW (Defect 44025)
		// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
		ICloudConfig cloudConfig = cloudDocument.getCloudConfig();
		Map<Object, Object> returnMap = new HashMap<>();

		// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669.
		int returnInteger = 1;
		String returnMessage = DomainConstants.EMPTY_STRING;
		boolean isTimedOut = Boolean.FALSE;

        Object[] objectInfoArray = new Object[]{cloudDocument.getObjectType(), cloudDocument.getObjectName(), cloudDocument.getObjectRevision(), cloudDocument.getObjectOid()};
        // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

		GenDoc genDoc = new GenDoc.Builder(cloudConfig).build();
		if (genDoc.isLoaded()) {
			logger.log(Level.INFO, "Get instance of Cloud Container was successful | {0}|{1}|{2}|{3}|", objectInfoArray);
			CloudBlobContainer cloudBlobContainer = genDoc.getCloudBlobContainer();
			List<Document> uploadResponseList = new ArrayList<>();
			int uploadSuccessCount = 0;
			logger.log(Level.INFO, "GenDoc files upload start | {0}|{1}|{2}|{3}|", objectInfoArray);
			GenDocUpload genDocUpload = new GenDocUpload.Builder(cloudBlobContainer, cloudDocument, cloudDocuments).build();
			logger.log(Level.INFO, "GenDoc files upload end | {0}|{1}|{2}|{3}|", objectInfoArray);
			boolean isGenDocFileUploaded = genDocUpload.isUploadSuccessful();
			logger.log(Level.INFO, "GenDoc files upload status? {0}", isGenDocFileUploaded);
			boolean isWorkFileUploaded = Boolean.FALSE;
			if (isGenDocFileUploaded) {
				logger.log(Level.INFO, "GenDoc files upload was successful | {0}|{1}|{2}|{3}|", objectInfoArray);
				uploadSuccessCount = genDocUpload.getUploadSuccessCount();
				uploadResponseList = genDocUpload.getUploadResponseList();
				logger.log(Level.INFO, "GenDoc files upload count {0}", uploadSuccessCount);
				GenDocWorkDir genDocWorkDir = new GenDocWorkDir.Builder(cloudBlobContainer, cloudDocument).upload();
				isWorkFileUploaded = genDocWorkDir.isUploadSuccessful();
				logger.log(Level.INFO, "GenDoc Work Text File upload was successful? {0}", isWorkFileUploaded);
				if (!isWorkFileUploaded) {
					logger.log(Level.WARNING, "GenDoc Work Text File upload was not successful | {0}|{1}|{2}|{3}|", objectInfoArray);
					// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
					returnInteger = 1;
					returnMessage = genDocWorkDir.getErrorMessage();
					isTimedOut = Boolean.TRUE;
				} else {
					logger.log(Level.INFO, "GenDoc Work Text File upload was successful | {0}|{1}|{2}|{3}|", objectInfoArray);
					CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
					final GenDocProcessResponse genDocProcessResponse = cloudGenDocUtil.getCloudDownloadInfo(cloudDocument, cloudBlobContainer, cloudDocuments.size(), uploadSuccessCount, uploadResponseList.size());
					returnInteger = genDocProcessResponse.getReturnInteger();
					returnMessage = genDocProcessResponse.getReturnMessage();
					isTimedOut = genDocProcessResponse.isTimedOut();
					logger.log(Level.INFO, "Is Timed-Out {0}|Return Integer {1}|Return Message {2}|", new Object[]{isTimedOut, returnInteger, returnMessage});
				}
			} else {
				returnInteger = 1;
				returnMessage = genDocUpload.getErrorMessage();
				isTimedOut = Boolean.TRUE;
				logger.log(Level.WARNING, "GenDoc Work-File upload failed {0}", returnMessage);
			}
		} else {
			logger.log(Level.WARNING, "Get instance of Cloud Container failed | {0}|{1}|{2}|{3}|", objectInfoArray);
			returnMessage = genDoc.getErrorMessage();
			returnInteger = 1;
			isTimedOut = Boolean.TRUE;
		}
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_INTEGER.getValue(), returnInteger);
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_STRING.getValue(), returnMessage);
		returnMap.put(CloudConstants.Basic.KEY_WORD_TIMED_OUT.getValue(), isTimedOut);
		logger.log(Level.INFO, "cloudGenDocProcessInvoke - Return Map {0}", returnMap);
		return new GenDocProcessResponse(returnMap); // Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
	}

    /**
     * @param downloadedDocuments
     * @return
     */
    public Map<String, String> isCloudDownloadSuccess(List<Document> downloadedDocuments) {
        Map<String, String> returnMap = new HashMap<>();
        for (Document document : downloadedDocuments) {
            if (!Boolean.valueOf(document.getIsPdfGenerated())) {
                returnMap.put(CloudConstants.Basic.KEYWORD_ERROR.getValue(), document.getPdfGenErrorMsg());
                logger.info("Error in generating PDF -");
            }
            if (Boolean.valueOf(document.getTimeOut())) {
                returnMap.put("timeout", String.valueOf(Boolean.TRUE));
                logger.info("Timeout in generating PDF -");
            }
        }
        return returnMap;
    }

    /**
     * @param cloudDocument
     * @param fileName
     * @param filePath
     * @return
     * @throws org.json.JSONException
     */
    Document iTextGeneratedPDFCopyToCheckoutFolder(ICloudDocument cloudDocument, String fileName, String filePath) throws JSONException {
        String checkOutPath = cloudDocument.getAbsoluteCheckOutDir();
        logger.info("Absolute Checkout Dir:" + checkOutPath);
        File checkOutDir = new File(checkOutPath);
        if (!checkOutDir.exists()) {
            checkOutDir.mkdir();
            fileCopy(filePath, checkOutDir.getPath() + File.separator + fileName);
            logger.info("Structured Types - Copied iText Generated PDF to download folder after make dir");
        } else {
            fileCopy(filePath, checkOutDir.getPath() + File.separator + fileName);
            logger.info("Structured Types - Copied iText Generated PDF to download folder.");
        }
        String absoluteCheckOutFilePath = checkOutPath.concat(File.separator).concat(fileName);
        
        File absoluteCheckOutFile = new File(absoluteCheckOutFilePath);
        CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
        return cloudGenDocUtil.getDocument(absoluteCheckOutFile, cloudDocument, fileName);
    }

    /**
     * @param cloudDocument
     * @param fileName
     * @param filePath
     * @return
     * @throws org.json.JSONException
     */
    Document iTextGeneratedPDFCopyToDownloadFolder(ICloudDocument cloudDocument, String fileName, String filePath) throws JSONException {
        String downloadDir = cloudDocument.getAbsoluteDownloadDir();
        
        File downloadFolder = new File(downloadDir);
        if (!downloadFolder.exists()) {
            downloadFolder.mkdir();
            fileCopy(filePath, downloadFolder.getPath() + File.separator + fileName);
            logger.info("Structured Types - Copied iText Generated PDF to download folder after make dir");
        } else {
            fileCopy(filePath, downloadFolder.getPath() + File.separator + fileName);
            logger.info("Structured Types - Copied iText Generated PDF to download folder.");
        }
        String absoluteDownloadFilePath = downloadDir.concat(File.separator).concat(fileName);
        
        File downloadFolderFile = new File(downloadDir);
        CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
        return cloudGenDocUtil.getDocument(downloadFolderFile, cloudDocument, fileName);
    }

    /**
     * @param context
     * @return
     * @throws matrix.util.MatrixException
     */
    public boolean isExternalRoleAssigned(Context context) throws MatrixException {
        return context.isAssigned(pgV3Constants.ROLE_PGCONTRACTSUPPLIER)
                || context.isAssigned(pgV3Constants.ROLE_PGCONTRACTPACKER)
                || context.isAssigned(pgV3Constants.ROLE_PGCONTRACTMANUFACTURER)
                || context.isAssigned(pgV3Constants.ROLE_IPMWAREHOUSEREADER);
    }

    /**
     * @param context
     * @param cloudDocument
     * @param inputObjInfo
     * @param stringBufferHTML
     * @param stringBufferXMLContent
     * @param stringBufferGenDocFileName
     * @param stringBufferGenDocFilePath
     * @throws Exception
     */
    public void processView(Context context, ICloudDocument cloudDocument, Map<String, String> inputObjInfo,
                            StringBuffer stringBufferHTML,
                            StringBuffer stringBufferXMLContent,
                            StringBuffer stringBufferGenDocFileName,
                            StringBuffer stringBufferGenDocFilePath) throws Exception {

        String viewIdentifier = cloudDocument.getViewIdentifier();
        String contextUser = cloudDocument.getContextUser();
        String pdfFileName = cloudDocument.getPdfFileName();
        String fileName = cloudDocument.getFileName();

        String[] inputArgs = new String[]{cloudDocument.getObjectOid(), cloudDocument.getViewIdentifier(), cloudDocument.getContextUser()};

        switch (viewIdentifier) {

            case pgPDFViewConstants.CONST_ALLINFO:
                System.out.println("Load case :" + pgPDFViewConstants.CONST_ALLINFO);

                processAllInfoView(context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent);
                break;

            case pgV3Constants.PDFVIEW_GENDOC:
                System.out.println("Load case :" + pgV3Constants.PDFVIEW_GENDOC);
                processGendoc(context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        stringBufferGenDocFileName,
                        stringBufferGenDocFilePath,
                        fileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent);
                break;


            case pgPDFViewConstants.CONST_SUPPLIER:
                System.out.println("Load case :" + pgPDFViewConstants.CONST_SUPPLIER);
                processSupplierView(
                        context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent);

                break;

            case pgPDFViewConstants.CONST_COMBINEDWITHMASTER:
                System.out.println("Load case :" + pgPDFViewConstants.CONST_COMBINEDWITHMASTER);
                processCombinedWithMasterView(
                        context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent);
                break;

            case pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING:
                System.out.println("Load case :" + pgPDFViewConstants.CONST_CONSOLIDATEDPACKAGING);
                processConsolidatedPackagingView(
                        context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent);

                break;

            case pgPDFViewConstants.CONST_WAREHOUSE:
                System.out.println("Load case :" + pgPDFViewConstants.CONST_WAREHOUSE);
                processWarehouseView(
                        context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent);

                break;

            case pgPDFViewConstants.CONST_CONTRACTPACKAGING:
                System.out.println("Load case :" + pgPDFViewConstants.CONST_CONTRACTPACKAGING);
                processContractPackagingView(
                        context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent,
                        inputArgs);
                break;

            case pgV3Constants.PQR_VIEW:
                System.out.println("Load case :" + pgV3Constants.PQR_VIEW);
                processPQRView(
                        context,
                        contextUser,
                        viewIdentifier,
                        pdfFileName,
                        inputObjInfo,
                        stringBufferHTML,
                        stringBufferXMLContent);
                break;
            default:
                break;

        }

    }

    /**
     * @param cloudDocument
     * @param cloudGenDocUtil
     * @param jsonArray
     * @return
     */
    public List<Document> getDocuments(ICloudDocument cloudDocument, CloudGenDocUtil cloudGenDocUtil, JSONArray jsonArray) {
        List<Document> documentList = new ArrayList<>();
        try {
            ICloudConfig cloudConfig = cloudDocument.getCloudConfig();
            JSONObject jsonObject;
            String downloadFileName;
            String uploadFileName;

            StringBuilder pathBuilder;
            String objectId;

            Document document;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                uploadFileName = (String) jsonObject.get("fileName");
                downloadFileName = uploadFileName.substring(0, uploadFileName.lastIndexOf(".") + 1).concat(CloudConstants.Basic.FILE_EXTENSION_PDF.getValue());

                objectId = (String) jsonObject.get(DomainConstants.SELECT_ID);

                // build blob upload path
                pathBuilder = new StringBuilder();
                pathBuilder.append(cloudConfig.getBlobUploadPath());
                pathBuilder.append(cloudDocument.getWorkspaceName());
                pathBuilder.append(CloudConstants.Basic.SYMBOL_UNDERSCORE.getValue());
                pathBuilder.append(cloudDocument.getTimeStamp());
                pathBuilder.append(File.separator);
                pathBuilder.append(objectId);
                pathBuilder.append(File.separator);
                pathBuilder.append(uploadFileName);

                jsonObject.put("blobFileUploadPath", pathBuilder.toString());


                // build blob download path
                pathBuilder = new StringBuilder();
                pathBuilder.append(cloudConfig.getBlobDownloadPath());
                pathBuilder.append(cloudDocument.getWorkspaceName());
                pathBuilder.append(CloudConstants.Basic.SYMBOL_UNDERSCORE.getValue());
                pathBuilder.append(cloudDocument.getTimeStamp());
                pathBuilder.append(File.separator);
                pathBuilder.append(objectId);
                pathBuilder.append(File.separator);
                pathBuilder.append(downloadFileName);
                jsonObject.put("blobFileDownloadPath", pathBuilder.toString());


                // build local download path
                pathBuilder = new StringBuilder();
                pathBuilder.append(cloudDocument.getAbsoluteDownloadDir());
                pathBuilder.append(File.separator);
                pathBuilder.append(downloadFileName);
                jsonObject.put("absoluteDownloadFilePath", pathBuilder.toString());


                jsonObject.put("blobDownloadFileName", downloadFileName);

                jsonObject.put("inWorkDir", cloudDocument.getInWorkDir());
                jsonObject.put("inWorkDirName", CloudConstants.Basic.IN_WORK_DIR_NAME.getValue());
                jsonObject.put("inWorkDirFilePath", cloudDocument.getInWorkDir().concat(File.separator).concat(uploadFileName));

                document = cloudGenDocUtil.getDocument(jsonObject);
                if (null != document && Boolean.valueOf(document.getLoaded())) {
                    System.out.println("***********************************************:" + document.getInWorkDirFilePath());
                    documentList.add(document);
                }
            }
        } catch (JSONException e) {
            logger.log(Level.WARNING, null, e);
        }
        return documentList;
    }

    /**
     * @param inputObjInfo
     * @param cloudGenDocUtil
     * @return
     * @throws org.json.JSONException
     */
    List<Document> getCheckOutDocuments(ICloudDocument cloudDocument, Map<String, String> inputObjInfo, CloudGenDocUtil cloudGenDocUtil) throws JSONException {
        // Segregate the files according to file extension which needs to be sent to Cloud or iText.
        JSONArray jsonArray = new JSONArray();
        String jsonString;
        if (inputObjInfo.containsKey("checkOutFileDetailsInJSONFormat")) {
            jsonString = (String) inputObjInfo.get("checkOutFileDetailsInJSONFormat");
            if (UIUtil.isNotNullAndNotEmpty(jsonString)) {
                jsonArray = new JSONArray(jsonString);
            }
        }
        return getDocuments(cloudDocument, cloudGenDocUtil, jsonArray);
    }

    /**
     * @param context
     * @param cloudDocument
     * @param inputObjInfo
     * @param stringBufferHTML
     * @param itextDocuments
     * @throws Exception
     */
    void iTextGenerateStructuredPDF(Context context, ICloudDocument cloudDocument, Map<String, String> inputObjInfo, StringBuffer stringBufferHTML, List<Document> itextDocuments) throws Exception {
        if (cloudDocument.isGenDocStructured()) {
            logger.info("It is GenDoc Structured !");
            if (!cloudDocument.isGenDocMEPorSEP()) {
                logger.info("Structured Types not MEP/SEP");
                String fileName = cloudDocument.getTempContentPdfFileName();
                logger.info("Structured Types - Generate PDF using iText - start");
                String sFilePath = generateiTextPDF(context, stringBufferHTML.toString(), cloudDocument.getObjectOid(), cloudDocument.getPdfFileName(),
                        inputObjInfo);
                logger.info("Structured Types - Generate PDF using iText - end");
                // copy the itext generated pdf to the same checkout folder followed by sub folder with name object id.
                itextDocuments.add(iTextGeneratedPDFCopyToCheckoutFolder(cloudDocument, fileName, sFilePath));
            }
        }
    }

    /**
     * @param context
     * @param cloudDocument
     * @param inputObjInfo
     * @param stringBufferHTML
     * @return
     * @throws Exception
     */
    String iTextGenerateStructuredPDF(Context context, ICloudDocument cloudDocument, Map<String, String> inputObjInfo, StringBuffer stringBufferHTML) throws Exception {
        String outputPath = DomainConstants.EMPTY_STRING;
        if (cloudDocument.isGenDocStructured()) {
            logger.info("It is GenDoc Structured !");
            if (!cloudDocument.isGenDocMEPorSEP()) {
                logger.info("Structured Types not MEP/SEP");
                String fileName = cloudDocument.getTempContentPdfFileName();
                logger.info("Structured Types - Generate PDF using iText - start");
                outputPath = generateiTextPDF(context, stringBufferHTML.toString(), cloudDocument.getObjectOid(), cloudDocument.getPdfFileName(),
                        inputObjInfo);

                logger.info(String.format("iText Generated PDF File Path: %s", outputPath));
                logger.info("Structured Types - Generate PDF using iText - end");
            }
        }
        return outputPath;
    }

    /**
     * @param context
     * @param cloudDocument
     * @param inputObjInfo
     * @param stringBufferHTML
     * @param itextDocuments
     * @param sbGenDocFileName
     * @param sbGenDocFilePath
     * @throws Exception
     */
    void iTextGenerateUnStructuredPDF(Context context, ICloudDocument cloudDocument, Map<String, String> inputObjInfo, StringBuffer stringBufferHTML, List<Document> itextDocuments, StringBuffer sbGenDocFileName, StringBuffer sbGenDocFilePath) throws Exception {
        if (cloudDocument.isGenDocUnStructured()) {
            String inputOid = cloudDocument.getObjectOid();
            String genDocFileName = sbGenDocFileName.toString();
            logger.info("UnStructured Types");
            if (UIUtil.isNotNullAndNotEmpty(genDocFileName)) {
                logger.info("UnStructured Types - GenDoc file names are valid");
                if (cloudDocument.isATSorPOA()) {
                    logger.info("UnStructured Types - POA or ATS");
                    String fileName = cloudDocument.getObjectNameForFile().concat(pgV3Constants.SYMBOL_UNDERSCORE).concat(cloudDocument.getTimeStamp()) + "TempContentFile.PDF";
                    logger.info("UnStructured Types - Generate PDF using iText - start");
                    String sFilePath = generateiTextPDF(context, stringBufferHTML.toString(), inputOid, cloudDocument.getPdfFileName(),
                            inputObjInfo);
                    logger.info("UnStructured Types - Generate PDF using iText - end");
                    itextDocuments.add(iTextGeneratedPDFCopyToCheckoutFolder(cloudDocument, fileName, sFilePath));
                }
            }
        }
    }

    /**
     * @param cloudDocument
     * @return
     */
    private String getTempFileNameWithTimestamp(ICloudDocument cloudDocument) {
        return cloudDocument.getObjectNameForFile().concat(pgV3Constants.SYMBOL_UNDERSCORE).concat(cloudDocument.getTimeStamp()) + "TempContentFile.PDF";
    }

    /**
     * @param context
     * @param cloudDocument
     * @param inputObjInfo
     * @param stringBufferHTML
     * @param sbGenDocFileName
     * @param sbGenDocFilePath
     * @return
     * @throws Exception
     */
    String iTextGenerateUnStructuredPDF(Context context, ICloudDocument cloudDocument, Map<String, String> inputObjInfo, StringBuffer stringBufferHTML, StringBuffer sbGenDocFileName, StringBuffer sbGenDocFilePath) throws Exception {
        String outputPath = DomainConstants.EMPTY_STRING;
        if (cloudDocument.isGenDocUnStructured()) {
            String inputOid = cloudDocument.getObjectOid();
            String genDocFileName = sbGenDocFileName.toString();
            logger.info("UnStructured Types");
            if (UIUtil.isNotNullAndNotEmpty(genDocFileName)) {
                logger.info("UnStructured Types - GenDoc file names are valid");
                if (cloudDocument.isATSorPOA()) {
                    logger.info("UnStructured Types - POA or ATS");
                    String fileName = cloudDocument.getObjectNameForFile().concat(pgV3Constants.SYMBOL_UNDERSCORE).concat(cloudDocument.getTimeStamp()) + "TempContentFile.PDF";
                    logger.info("UnStructured Types - Generate PDF using iText - start");
                    outputPath = generateiTextPDF(context, stringBufferHTML.toString(), inputOid, cloudDocument.getPdfFileName(),
                            inputObjInfo);
                    logger.info("UnStructured Types - Generate PDF using iText - end");
                }
            }
        }
        return outputPath;
    }

    /**
     * @param context
     * @param cloudDocument
     * @param downloadDir
     */
    void checkInMergedPDF(Context context, ICloudDocument cloudDocument, String downloadDir) {
        try {
            String mergeDir = cloudDocument.getAbsoluteMergeDir();
            logger.info("Merge Dir:" + mergeDir);
            String mergeFilePath = mergeDir + File.separator + cloudDocument.getObjectName() + "-Rev" + cloudDocument.getObjectRevision() + ".pdf";
            logger.info("Merge File Path:" + mergeFilePath);
            String checkinFile = cloudDocument.getObjectName() + "-Rev" + cloudDocument.getObjectRevision() + ".pdf";
            logger.info("Checkin File:" + checkinFile);
            logger.info("Connect Rendition start");
            String sRenditionId = connectFPPToRenditionobj(context, cloudDocument.getObjectOid());
            logger.info("Connect Rendition end");
            File mergedFile = new File(mergeFilePath);
            if (UIUtil.isNotNullAndNotEmpty(sRenditionId) && (mergedFile.exists())) {
                logger.info("Merge File exist:" + mergedFile.getPath());
                String strProject = (String) cloudDocument.getProject();
                String strOrganization = (String) cloudDocument.getOrganization();
                DomainObject dobject = DomainObject.newInstance(context, sRenditionId);

                logger.info("Connect Primary Organiztion start");
                dobject.setPrimaryOwnership(context, strProject, strOrganization);
                logger.info("Connect Primary Organiztion end");
                logger.info("Checkin Rendition file start");
                dobject.checkinFile(context, false, false, "", FORMAT_GENERIC, checkinFile,
                        mergeDir);
                logger.info("Checkin Rendition file end");
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
     *
     * @param context
     * @param dObj
     * @return
     * @throws com.matrixone.apps.domain.util.FrameworkException
     */
    public String getGenDocBlobFolderName(Context context, DomainObject dObj) throws FrameworkException {
        StringList busSelects = new StringList();
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_REVISION);
        Map gcasInfo = dObj.getInfo(context, busSelects);

        // remove spaces from gcas name & gcas revision.
        String gcasName = (String) gcasInfo.get(DomainConstants.SELECT_NAME);
        String gcasRev = (String) gcasInfo.get(DomainConstants.SELECT_REVISION);
        gcasName = gcasName.replaceAll("\\s", DomainConstants.EMPTY_STRING).toUpperCase();
        gcasRev = gcasRev.replaceAll("\\s", DomainConstants.EMPTY_STRING).toUpperCase();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CloudConstants.Basic.DATE_FORMAT_FOR_TIMESTAMP.getValue());

        StringBuilder uniqueNameBuilder = new StringBuilder();
        uniqueNameBuilder.append(CloudConstants.Basic.KEYWORD_PART.getValue());
        uniqueNameBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        uniqueNameBuilder.append(gcasName);
        uniqueNameBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        uniqueNameBuilder.append(gcasRev);
        uniqueNameBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        uniqueNameBuilder.append(java.util.UUID.randomUUID().toString().replace(pgV3Constants.SYMBOL_HYPHEN, DomainConstants.EMPTY_STRING).toUpperCase());
        uniqueNameBuilder.append(pgV3Constants.SYMBOL_HYPHEN);
        uniqueNameBuilder.append(simpleDateFormat.format(new Date()));
        logger.log(Level.INFO, "Azure Blob Folder To Be Created with Name -> {0}", uniqueNameBuilder.toString());
        return uniqueNameBuilder.toString();
    } // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

    /**
     * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
     *
     * @param context
     * @param args
     * @param cloudConfig
     * @return
     * @throws matrix.util.MatrixException
     */
    List<File> getListOfCheckOutFiles(Map<String, String> inputObjInfo) throws JSONException {
        List<File> checkOutFiles = new ArrayList<>();
        // Segregate the files according to file extension which needs to be sent to Cloud or iText.
        JSONArray jsonArray = new JSONArray();
        String jsonString;
        if (inputObjInfo.containsKey("checkOutFileDetailsInJSONFormat")) {
            jsonString = (String) inputObjInfo.get("checkOutFileDetailsInJSONFormat");
            if (UIUtil.isNotNullAndNotEmpty(jsonString)) {
                jsonArray = new JSONArray(jsonString);
            }
        }
        JSONObject jsonObject;
        String checkOutFilePath;
        File checkOutFile;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = (JSONObject) jsonArray.get(i);
            checkOutFilePath = (String) jsonObject.get("absoluteCheckOutFilePath");
            checkOutFile = new File(checkOutFilePath);
            if (checkOutFile.exists()) {
                checkOutFiles.add(checkOutFile);
            }
        }
        return checkOutFiles;
    }// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

	/**
	 * Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	 * @param cloudDocument
	 * @return
	 */
	private boolean isNonStructuredTypes(ICloudDocument cloudDocument) {
		final String viewIdentifier = cloudDocument.getViewIdentifier();
		final String objectType = cloudDocument.getObjectType();
		final String objectPolicy = cloudDocument.getObjectPolicy();
		final String unStructuredTypes = cloudDocument.getUnStructuredTypes();
		return (pgV3Constants.PDFVIEW_GENDOC.equals(viewIdentifier)
				&& ((unStructuredTypes.contains(objectType.trim()) && !objectType.equalsIgnoreCase(pgV3Constants.TYPE_PGRAWMATERIAL))
				|| pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(objectPolicy)
				|| pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equalsIgnoreCase(objectPolicy)));
	} // Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends

	/**
	 * Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	 * @param context
	 * @param cloudDocument
	 * @param inputObjInfo
	 * @param stringBufferHTML
	 * @param stringBufferGenDocFileName
	 * @param stringBufferGenDocFilePath
	 * @param totalCountForMerge
	 * @return
	 * @throws Exception
	 */
	private GenDocProcessResponse useiTextPdf(Context context, ICloudDocument cloudDocument, Map<String, String> inputObjInfo, StringBuffer stringBufferHTML, StringBuffer stringBufferGenDocFileName, StringBuffer stringBufferGenDocFilePath, int totalCountForMerge) throws Exception {
		int returnInteger = 1;
		String returnMessage = DomainConstants.EMPTY_STRING;
		boolean isTimedOut = Boolean.FALSE;
		Map<Object, Object> returnMap = new HashMap<>();
		Object[] objectInfoArray = new Object[]{cloudDocument.getObjectType(), cloudDocument.getObjectName(), cloudDocument.getObjectRevision(), cloudDocument.getObjectOid()};
		boolean useiTextMerge = true;
		if (cloudDocument.isATSorPOA()) {
			logger.log(Level.INFO, "For POA/ATS - Generate UnStructured - Html to Pdf using iText - start | {0}|{1}|{2}|{3}|", objectInfoArray);
			logger.log(Level.INFO, "ATS or POA - Generate Html Pdf using iText - STARTS");
			String outPdfPath = iTextGenerateUnStructuredPDF(context, cloudDocument, inputObjInfo, stringBufferHTML, stringBufferGenDocFileName, stringBufferGenDocFilePath);
			logger.log(Level.INFO, "ATS or POA - Generate Html Pdf using iText - ENDS");
			if(UIUtil.isNotNullAndNotEmpty(outPdfPath)) {
				File filePdf = new File(outPdfPath);
				if (filePdf.exists()) {
					logger.log(Level.INFO, "iText Generated UnStructured Html to Pdf Output Path {0}", outPdfPath);
					Document iTextPdfDocument = iTextGeneratedPDFCopyToDownloadFolder(cloudDocument, getTempFileNameWithTimestamp(cloudDocument), outPdfPath);
					if (null != iTextPdfDocument) {
						logger.log(Level.INFO, "Generate UnStructured - Html to Pdf using iText - end | {0}|{1}|{2}|{3}|", objectInfoArray);
						totalCountForMerge = totalCountForMerge + 1; // include the html - generated pdf.
					} else {
						returnMessage = CloudConstants.Basic.FAILED_TO_COPY_HTML_PDF_TO_IN_WORK_FOLDER.getValue();
						isTimedOut = Boolean.TRUE;
						useiTextMerge = false;
					}
				} else {
					returnMessage = CloudConstants.Basic.FAILED_TO_GENERATE_HTML_TO_PDF.getValue();
					isTimedOut = Boolean.TRUE;
					useiTextMerge = false;
				}
			} else {
				returnMessage = CloudConstants.Basic.FAILED_TO_GENERATE_HTML_TO_PDF.getValue();
				isTimedOut = Boolean.TRUE;
				useiTextMerge = false;
			}
		}
		if (useiTextMerge) { // use iText for checked-in supported files
			CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
			logger.log(Level.INFO, "Start - Convert Checked-in files to Pdf using iText|{0}|{1}|{2}|{3}|", objectInfoArray);
			final GenDocProcessResponse genDocProcessResponse = iTextProcessInvoke(context, cloudDocument, cloudGenDocUtil, totalCountForMerge);
			returnInteger = genDocProcessResponse.getReturnInteger();
			returnMessage = genDocProcessResponse.getReturnMessage();
			isTimedOut = genDocProcessResponse.isTimedOut();
			logger.log(Level.INFO, "End - Convert Checked-in files to Pdf using iText|{0}|{1}|{2}|{3}|", objectInfoArray);
			logger.log(Level.INFO, "iText Process|Is Timed Out -> {0}|Returned Integer -> {1}|Returned Message -> {2}", new Object[]{isTimedOut, returnInteger, returnMessage});
		}
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_INTEGER.getValue(), returnInteger);
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_STRING.getValue(), returnMessage);
		returnMap.put(CloudConstants.Basic.KEY_WORD_TIMED_OUT.getValue(), isTimedOut);
		return new GenDocProcessResponse(returnMap);
	} // Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends

	/**
	 * Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
	 * @param context
	 * @param cloudDocument
	 * @param inputObjInfo
	 * @param stringBufferHTML
	 * @param stringBufferGenDocFileName
	 * @param stringBufferGenDocFilePath
	 * @return
	 * @throws Exception
	 */
	private GenDocProcessResponse getGenDocPdf(Context context, ICloudDocument cloudDocument, Map<String, String> inputObjInfo, StringBuffer stringBufferHTML, StringBuffer stringBufferGenDocFileName, StringBuffer stringBufferGenDocFilePath) throws Exception {

		int returnInteger = 1;
		String returnMessage = DomainConstants.EMPTY_STRING;
		boolean isTimedOut = Boolean.FALSE;
		Map<Object, Object> returnMap = new HashMap<>();

		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		List<File> listOfCheckOutFiles = getListOfCheckOutFiles(inputObjInfo);
		ICloudConfig cloudConfig = cloudDocument.getCloudConfig();
		final FileRenameUtil fileRenameUtil = new FileRenameUtil.ProcessBuilder(listOfCheckOutFiles, cloudConfig.getCharactersNotAllowedForFileName(), Boolean.FALSE).build();
		if (fileRenameUtil.isOperationSuccessful()) {
			logger.log(Level.INFO, "Rename/Remove Special Characters from CheckOut Files Passed");
			// re-use variable.
			listOfCheckOutFiles = fileRenameUtil.getRenamedFileList();
			// Check if any un-supported file extension is checked-in
			CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
			if (cloudGenDocUtil.isSupportedFileExtension(cloudConfig, listOfCheckOutFiles)) {
				// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
				// Segregate the files according to file extension which needs to be sent to Cloud or iText.
				// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
				List<Document> documentList = cloudGenDocUtil.getDocuments(cloudDocument, listOfCheckOutFiles);
				logger.log(Level.INFO, "Number of Checkout file to Document: {0}", documentList.size());

				// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
				List<Document> itextDocuments = cloudGenDocUtil.filterDocumentsByExtension(documentList, cloudConfig.getFileExtensionsForIText());
				List<Document> cloudDocuments = cloudGenDocUtil.filterDocumentsByExtension(documentList, cloudConfig.getFileExtensionsForCloud());
				List<Document> pdfDocuments = cloudGenDocUtil.filterDocumentsByExtension(documentList, CloudConstants.Basic.FILE_EXTENSION_PDF.getValue());

				Object[] objectInfoArray = new Object[]{cloudDocument.getObjectType(), cloudDocument.getObjectName(), cloudDocument.getObjectRevision(), cloudDocument.getObjectOid()};
				logger.log(Level.INFO, "Get instance of Cloud Container was successful | {0}|{1}|{2}|{3}|", objectInfoArray);

				// copy files from checkout folder to in-work directory.
				boolean copyCheckOutFiles = cloudGenDocUtil.copyCheckOutFiles(documentList, cloudDocument.getInWorkDir());
				if (copyCheckOutFiles) {
					if (cloudDocument.isGenDoc()) {
						int checkOutFileCount = documentList.size();
						int cloudDocumentCount = cloudDocuments.size();
						int pdfDocumentCount = pdfDocuments.size();
						int itextDocumentCount = itextDocuments.size();

						logger.log(Level.INFO, "Total Checkout files Count {0}", checkOutFileCount);
						logger.log(Level.INFO, "Number of files for Cloud {0}", cloudDocumentCount);
						logger.log(Level.INFO, "Number of Pdf Count {0}", pdfDocumentCount);
						logger.log(Level.INFO, "Number of files for iText {0}", itextDocumentCount);

						if (!cloudDocuments.isEmpty()) {
							logger.log(Level.INFO, "Invoke Cloud Process start | {0}|{1}|{2}|{3}|", objectInfoArray);
							final GenDocProcessResponse genDocProcessResponse = cloudGenDocProcessInvoke(context, cloudDocument, cloudDocuments);
							// Added/Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
							returnInteger = genDocProcessResponse.getReturnInteger();
							returnMessage = genDocProcessResponse.getReturnMessage();
							isTimedOut = genDocProcessResponse.isTimedOut();
							logger.log(Level.INFO, "Azure Process|Is Timed Out -> {0}|Returned Integer -> {1}|Returned Message -> {2}", new Object[]{isTimedOut, returnInteger, returnMessage});
						} else {
							logger.log(Level.WARNING, "There are no documents for Azure");
						}
						if (!isTimedOut) {
							int totalCountForMerge = cloudDocumentCount + itextDocumentCount + pdfDocumentCount;
							final GenDocProcessResponse genDocProcessResponse = useiTextPdf(context, cloudDocument, inputObjInfo, stringBufferHTML, stringBufferGenDocFileName, stringBufferGenDocFilePath, totalCountForMerge);
							returnInteger = genDocProcessResponse.getReturnInteger();
							returnMessage = genDocProcessResponse.getReturnMessage();
							isTimedOut = genDocProcessResponse.isTimedOut();
							logger.log(Level.INFO, "iText Process|Is Timed Out -> {0}|Returned Integer -> {1}|Returned Message -> {2}", new Object[]{isTimedOut, returnInteger, returnMessage});
						}
					} else {
						returnMessage = CloudConstants.Basic.NOT_GEN_DOC_REQUEST.getValue();
						isTimedOut = Boolean.TRUE;
						logger.log(Level.WARNING, CloudConstants.Basic.NOT_GEN_DOC_REQUEST.getValue());
					}
				} else {
					returnMessage = CloudConstants.Basic.COPYING_CHECKOUT_FILES_FAILED.getValue();
					isTimedOut = Boolean.TRUE;
					logger.log(Level.WARNING, CloudConstants.Basic.COPYING_CHECKOUT_FILES_FAILED.getValue());
				}
			} else {
				returnMessage = CloudConstants.Basic.CONTAINS_UNSUPPORTED_EXTENSION_FILE.getValue();
				isTimedOut = Boolean.TRUE;
				logger.log(Level.WARNING, CloudConstants.Basic.CONTAINS_UNSUPPORTED_EXTENSION_FILE.getValue());
			}
		} else {
			returnMessage = CloudConstants.Basic.ERROR_REMOVE_SPECIAL_CHARACTERS_FROM_CHECKOUT_FILES_FAILED.getValue();
			isTimedOut = Boolean.TRUE;
			logger.log(Level.WARNING, CloudConstants.Basic.ERROR_REMOVE_SPECIAL_CHARACTERS_FROM_CHECKOUT_FILES_FAILED.getValue());
		}
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_INTEGER.getValue(), returnInteger);
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_STRING.getValue(), returnMessage);
		returnMap.put(CloudConstants.Basic.KEY_WORD_TIMED_OUT.getValue(), isTimedOut);
		return new GenDocProcessResponse(returnMap);
	} // Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends


//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47391 - Starts
		/**
		 * @param context
		 * @param strType
		 * @param strPolicy
		 * @return
		 */
		public boolean isAllowedTypeforManualGenDoc(Context context, String strType, String strPolicy) {
			boolean isTypeforManualGenDoc = false;
			
			List lTypeforManualGenDoc = new ArrayList();
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGAUTHORIZEDTEMPORARYSTANDARD);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_AUTHORIZEDCONFIGSTAND);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_FORMULATECHNICALSPEC);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGLABORATORYINDEXSPECIFICATION);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_RAW_MATERIAL_PLANT_INSTRUCTION);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGILLUSTRATION);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGARTWORK);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGQUALITYSPECIFICATION);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGPROCESSSTANDARD);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGPACKINGINSTRUCTIONS);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGSTANDARDOPERATINGPROCEDURE);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGSTACKINGPATTERN);
			//Commented by DSM for 22x CW-04 for Defect 55325 Starts
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_PGMAKINGINSTRUCTIONS);
			lTypeforManualGenDoc.add(pgV3Constants.TYPE_TESTMETHOD);
			//Added by DSM for 22x CW-04 for Defect 55325 Ends
			if(lTypeforManualGenDoc.contains(strType) || pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase(strPolicy) || pgV3Constants.POLICY_SUPPLIER_EQUIVALENT.equalsIgnoreCase(strPolicy)) {
				isTypeforManualGenDoc = true;
			}
		
			return isTypeforManualGenDoc;			
		}
		//Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Requirement 47391 - Ends	
		
		
		
/**
* @param context 
* @param args 
* @return void
* @throws Exception if the operation fails
*Added by DSM (Sogeti) for 22x.04 (Dec CW 2023) Defect 55325 
*/		
	public void deleteOnDemandGenDoc(Context context, String args[]) throws Exception {

		String strObjectID = args[0];
		String strType = args[1];
				
		StringList slObjSelect = new StringList(1);		
		slObjSelect.add(pgV3Constants.SELECT_ID);
				
		StringList slObject = new StringList(3);
		slObject.add(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
		slObject.add(pgV3Constants.SELECT_POLICY);
		slObject.add(pgV3Constants.SELECT_TYPE);
				
		MapList mlConnectedGenDoc = null;
		Map mpConnectedGenDoc = null;
		String strIPMDocumentId=DomainConstants.EMPTY_STRING;
		String strMarkedforGenDoc=DomainConstants.EMPTY_STRING;;
		String strMarkedObjectPolicy = DomainConstants.EMPTY_STRING;;
		String strMarkedObjectType=DomainConstants.EMPTY_STRING;
	    boolean isContextPushed = false;
		
		if (pgV3Constants.TYPE_CHANGEACTION.equalsIgnoreCase(strType)) {
						
			try {
				ChangeAction caInstance = new ChangeAction(strObjectID);
				HashMap programMap=new HashMap();
				programMap.put("CAId", strObjectID);
				programMap.put("slSelect", slObjSelect);
				programMap.put("reqChang", pgV3Constants.For_Release);
				programMap.put("affectedRel", true);
				programMap.put("impRel", true);
				String[] strArgs =JPO.packArgs(programMap); 				
				MapList mlGCASObject =(MapList) pgPDFViewHelper.executeMainClassMethod(context, "pgDSMChangeUtil", "getAffectedItemForCA", strArgs);		
				String strAffectedItemId = DomainConstants.EMPTY_STRING;		
				if (null != mlGCASObject && !mlGCASObject.isEmpty()) {
					//push context to provide delete access to user for deleting IPM Document
					ContextUtil.pushContext(context);
					isContextPushed = true;
					for (Object object : mlGCASObject) {
						Map map = (Map) object;			
						strAffectedItemId = map.get(SELECT_ID).toString();		
						if(UIUtil.isNotNullAndNotEmpty(strAffectedItemId))		
						{
							DomainObject domGCAS = DomainObject.newInstance(context, strAffectedItemId);		
							boolean isGenDocPresent = isGenDocPresent(context, domGCAS);			
							if(isGenDocPresent) {
								Map mpDomainObject = domGCAS.getInfo(context, slObject);
								strMarkedforGenDoc = (String) mpDomainObject.get(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
								strMarkedObjectPolicy = (String) mpDomainObject.get(pgV3Constants.SELECT_POLICY);
								strMarkedObjectType = (String) mpDomainObject.get(pgV3Constants.SELECT_TYPE);
								if(isAllowedTypeforManualGenDoc(context,strMarkedObjectType,strMarkedObjectPolicy) && pgV3Constants.FALSE.equalsIgnoreCase(strMarkedforGenDoc)) {
									mlConnectedGenDoc = domGCAS.getRelatedObjects(context, //context
																				  pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT,//relationshipPattern 
																				  pgV3Constants.TYPE_PGIPMDOCUMENT,//typePattern
																				  slObjSelect, //objectSelects
																				  null,//relationshipSelects 
																				  false, //getTo
																				  true,//getFrom
																				  (short)1,//recurseToLevel 
																				  "revision==Rendition", //objectWhere
																				  null,//relationshipWhere
																				  0);//limit
					
									if(null != mlConnectedGenDoc && !mlConnectedGenDoc.isEmpty()) {
										mpConnectedGenDoc = (Map) mlConnectedGenDoc.get(0);
										strIPMDocumentId = (String) mpConnectedGenDoc.get(pgV3Constants.SELECT_ID);					
									
										//Deleting the IPM Document
										CommonDocument comDoc = new CommonDocument(strIPMDocumentId);					
										try{
											comDoc.deleteDocuments(context, new String [] {strIPMDocumentId});
					
										}
										catch (Exception e) {
											logger.log(Level.WARNING, e.getMessage());
										}
									}	
								}
							}  
						}
					}	
				}
			}catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage());
			} finally {

				if(isContextPushed)
				{
					isContextPushed = false;
					ContextUtil.popContext(context);

				}

			}
		}	
	}	
	
	/**
	 * This method is invoked from pgDSMGenDoc.jsp and used for deletion of gendoc from pre-approved state parts
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public void removeOnDemandGenDocPreApproved(Context context, String args[]) throws Exception{

		StringList slObjSelect = new StringList(1);		
		slObjSelect.add(pgV3Constants.SELECT_ID);

		StringList slObject = new StringList(3);
		slObject.add(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
		slObject.add(pgV3Constants.SELECT_POLICY);
		slObject.add(pgV3Constants.SELECT_TYPE);

		MapList mlConnectedGenDoc = null;
		Map mpConnectedGenDoc = null;
		String strIPMDocumentId=DomainConstants.EMPTY_STRING;
		String strMarkedforGenDoc=DomainConstants.EMPTY_STRING;;
		String strMarkedObjectPolicy = DomainConstants.EMPTY_STRING;;
		String strMarkedObjectType=DomainConstants.EMPTY_STRING;
		boolean isContextPushed = false;

		try{
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			String strObjId = (String)programMap.get("objectId");
			
			if(UIUtil.isNotNullAndNotEmpty(strObjId)) {
				DomainObject domGCAS = DomainObject.newInstance(context, strObjId);
				boolean isGenDocPresent = isGenDocPresent(context, domGCAS);

				if(isGenDocPresent) {
					//push context to provide delete access to user for deleting IPM Document
					ContextUtil.pushContext(context);
					isContextPushed = true;
					Map mpDomainObject = domGCAS.getInfo(context, slObject);
					strMarkedforGenDoc = (String) mpDomainObject.get(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
					strMarkedObjectPolicy = (String) mpDomainObject.get(pgV3Constants.SELECT_POLICY);
					strMarkedObjectType = (String) mpDomainObject.get(pgV3Constants.SELECT_TYPE);
					if(isAllowedTypeforManualGenDoc(context,strMarkedObjectType,strMarkedObjectPolicy) && pgV3Constants.FALSE.equalsIgnoreCase(strMarkedforGenDoc)) {
						mlConnectedGenDoc = domGCAS.getRelatedObjects(context, //context
								pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT,//relationshipPattern 
								pgV3Constants.TYPE_PGIPMDOCUMENT,//typePattern
								slObjSelect, //objectSelects
								null,//relationshipSelects 
								false, //getTo
								true,//getFrom
								(short)1,//recurseToLevel 
								"revision==Rendition", //objectWhere
								null,//relationshipWhere
								0);//limit

						if(null != mlConnectedGenDoc && !mlConnectedGenDoc.isEmpty()) {
							mpConnectedGenDoc = (Map) mlConnectedGenDoc.get(0);
							strIPMDocumentId = (String) mpConnectedGenDoc.get(pgV3Constants.SELECT_ID);	

							//Deleting the IPM Document
							CommonDocument comDoc = new CommonDocument(strIPMDocumentId);					
							try{
								comDoc.deleteDocuments(context, new String [] {strIPMDocumentId});

							}
							catch (Exception e) {
								logger.log(Level.WARNING, e.getMessage());
							} 
						}	
					}
				}  
			}            
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
		} finally {

			if(isContextPushed)
			{
				isContextPushed = false;
				ContextUtil.popContext(context);

			}

		}
	}
    /**************************************************************************************************************************************\
     *    Cloud GenDoc Code Ends
     ***************************************************************************************************************************************/


}
