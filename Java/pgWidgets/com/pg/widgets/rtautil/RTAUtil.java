package com.pg.widgets.rtautil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import com.custom.pg.Artwork.ArtworkConstants;
import com.dassault_systemes.cpd.json.JsonHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.awl.dao.ArtworkContent;
import com.matrixone.apps.awl.dao.ArtworkMaster;
import com.matrixone.apps.awl.dao.CPGProduct;
import com.matrixone.apps.awl.dao.CopyElement;
import com.matrixone.apps.awl.dao.CopyList;
import com.matrixone.apps.awl.dao.GraphicsElement;
import com.matrixone.apps.awl.dao.POA;
import com.matrixone.apps.awl.enumeration.AWLAttribute;
import com.matrixone.apps.awl.enumeration.AWLPolicy;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.awl.enumeration.AWLRole;
import com.matrixone.apps.awl.enumeration.AWLState;
import com.matrixone.apps.awl.enumeration.AWLType;
import com.matrixone.apps.awl.util.AWLConstants;
import com.matrixone.apps.awl.util.AWLExportPOAToExcel;
import com.matrixone.apps.awl.util.AWLPreferences;
import com.matrixone.apps.awl.util.AWLPropertyUtil;
import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.awl.util.Access;
import com.matrixone.apps.awl.util.ArtworkUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.awl.util.RouteUtil;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.common.Company;
import com.matrixone.apps.common.InboxTask;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.common.util.FormBean;
import com.matrixone.apps.cpd.dao.CPDCache;
import com.matrixone.apps.cpd.dao.Country;
import com.matrixone.apps.cpd.dao.Language;
import com.matrixone.apps.cpd.dao.Region;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.BackgroundProcess;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.DateUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UISearchUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.productline.ProductLineConstants;
import com.matrixone.apps.program.Task;
import com.matrixone.servlet.Framework;
import com.pg.artwork.claims.PGRTADCMDataLoad;
import com.pg.v3.custom.pgV3Constants;
import com.pg.widgets.editCopyList.EditCLConstants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.BusinessObject;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Policy;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class RTAUtil {

	static final String KEY_TITLE = "title";
	static final String KEY_LAST = "last";
	static final String KEY_REVISIONS = "revisions";
	static final String KEY_FLAG = "flag";
	static final String KEY_PARENTTYPE = "parenttype";
	static final String KEY_CONTENT = "content";
	static final String STR_TRANSLATE = "Translate";
	static final String STR_NOTRANSLATE = "NoTranslate";
	static final String STR_INLINETYPE = "InlineType";
	static final String STR_GRAPHICTYPE = "GraphicType";
	static final String STR_NONE = "None";
	static final String STR_LANG = "_language";
	static final String STR_INSSEQ = "_instancesequence";
	static final String STR_ORDER = "_order";
	static final String STR_NOTES = "_notes";
	static final String STR_NOTESSHORT = "_notesshort";
	static final String KEY_ISMANDATORY = "_IS_MANDATORY";
	static final String STR_ARTWORKUSAGE = "ArtworkUsage";
	static final String STR_ADDDESC = "AddDesc";
	static final String KEY_IDS = "Ids";
	static final String KEY_TYPES = "Types";
	static final String KEY_SEQ = "Seq";
	static final String KEY_NAME = "Name";
	static final String KEY_COUNTRYINFO = "CountryInfo";
	static final String KEY_PRODUCTINFO = "ProductInfo";
	static final String KEY_LANGUAGEINFO = "LanguageInfo";
	static final String KEY_POAINFO = "POA_Info";
	static final String KEY_INGREDIENTDEC = "IngredientDec";
	static final String KEY_PGRTAADRESSCES = "pgRTAAdressCES";
	static final String KEY_PGRTASIZEBASECES = "pgRTASizeBaseCES";
	static final String KEY_AutomaticMRKClaimResults = "AutomaticMRKClaimResults";
	static final String KEY_KINDOFHEADER = "kindOfHeader";
	static final String KEY_BASELANG = "preferenceLanguage";
	static final String KEY_BASELANGNAME = "name";
	static final String KEY_POAHIERARCHYDATA = "POAHierachyData";
	static final String KEY_BASELANGVALUE = "value";
	static final String KEY_MARKETINGNAME = "marketingName";
	static final String KEY_MASTERCOPYTEXT = "masterCopyText";
	static final String KEY_TRANSLATE = "translate";
	static final String KEY_INLINETRANSLATE = "inlineTranslation";
	static final String KEY_VALIDITYDATE = "validityDate";
	static final String KEY_INSTRUCTIONS = "instructions";
	static final String KEY_REFERENCENUMBER = "referenceNumber";
	static final String KEY_PLACEOFORIGIN = "placeOfOrigin";
	static final String KEY_LISTSEP = "listSeparator";
	static final String KEY_LISTSEQ = "listItemSequence";
	static final String KEY_LISTITEM = "listItemId";
	static final String KEY_LEAF = "leaf";
	static final String KEY_SELECTED = "selected";
	static final String KEY_EXPANDED = "expanded";
	static final String KEY_REGIONS = "regions";
	static final String KEY_DOCUMENTTYPE = "documenttype";
	static final String KEY_FILENAME = "filename";
	static final String KEY_MCSURL = "mcsUrl";
	static final String STR_ALLCOUNTRIES = "AllCountries";
	static final String STR_MARKETING_CUSTOMIZATION = "Marketing Customization";
	static final String STR_ACCESS_MODIFY = "current.access[modify]";
	static final String STR_CUSTOM_MODIFY_POA = "CustomModifyPOA";
	static final String STR_CUSTOM_REFERENCE_POA = "CustomReferencePOA";
	static final String STR_STANDARD_MODIFY_POA = "StandardModifyPOA";
	static final String STR_STANDARD_REFERENCE_POA = "StandardReferencePOA";
	static final String STR_MCIDS = "MCIds";
	static final String STR_LEADPOAID = "LeadPOAIds";
	static final String STR_COPYLIST_ID = "CopyListID";
	static final String REQUESTVALUEMAP = "RequestValuesMap";
	static final String TRIGGERFAILED = "TriggerFailed";
	static final String STR_MESSAGE = "Message";	
	static final String KEY_LANGUAGES = "Languages";
	static final String KEY_GCETYPES = "GCETypes";
	static final String KEY_MCETYPES = "MCETypes";
	static final String KEY_GCEInfo = "graphicinfo";
	static final String KEY_GCEDocument = "graphicdocument";
	static final String KEY_GCEElement = "graphicelement";
	static final String EXCEPTION_MESSAGE = "Exception in RTAUtil";
	static final String STR_FOLLOWERPOA = "FollowerPOA";
	static final String KEY_BRANDLEVEL = "isPOAAtBrandLevel";
	static final String KEY_SELECTEDPOAs = "selectedPOAs";
	static final String KEY_JOBTITLE = "jobTitle";
	static final String KEY_INTEGRATIONTYPE = "integrationType";
	static final String KEY_INTENDED_MARKET = "intendedMarket";
	static final String KEY_CLAIMOBJ = "claimObject";
	static final String KEY_CLAIMOWNER = "claimOwner";
	static final String KEY_CLAIMTYPE = "claimType";
	static final String KEY_CLAIMDESCP = "claimDescription";
	static final String KEY_RELPART = "relatedPart";
	static final String KEY_SIZE = "size";
	static final String KEY_PKGCOMPTYPE = "packageComponentType";
	static final String KEY_CLAIMPANELLOC = "claimPanelLocation";
	static final String KEY_CLAIMDISPANELLOC = "disclaimerPanelLocation";
	static final String KEY_VALCLAIM = "valueClaim";
	static final String KEY_EXECTYPE = "executionType";
	static final String KEY_BRAND = "brand";
	static final String KEY_SUBBRAND = "subBrand";
	static final String KEY_PRODUCTFORM = "productForm";
	static final String KEY_COUNT = "count";
	static final String KEY_VARIANT = "variant";
	static final String KEY_LOTIONSCENT = "lotionScent";
	static final String KEY_BUSINESSAREA = "businessArea";
	static final String KEY_PRODCATEPLATFORM = "productCategoryPlatform";
	static final String KEY_FRANCHISEPLATFORM = "franchisePlatform";
	static final String KEY_PGEXPIRATIONDATE = "expirationDate";
	static final String KEY_PGRTAMCPRODUCTIONPLANT = "pgRTAMCProductionPlant";
	static final String KEY_PGRTAMCPRIMARYPRODPLANT = "pgRTAMCPrimaryProdPlant";
	static final String KEY_SHOWBRANDTYPE = "ShowBrandType";
	static final String KEY_AUTOMATIC_MRK_CLAIM_RESULTS = "pgAutomaticMRKClaimResults";
	static final String KEY_RTA_GPS_FIXED_CES = "pgRTAGPSFixedRES";
	static final String KEY_STATE = "state";
	static final String KEY_COPYTEXT = "copyText";
	static final String KEY_ISMASTERCOPY = "isMasterCopy";
	static final String KEY_LOCALCOPYCONTENT = "localCopyContent";
	static final String KEY_LANGMARKETINGNAME = "language/marketingName";
	static final String KEY_PROJWONER = "projectOwner";
	static final String KEY_PROJNAME = "projectName";
	static final String KEY_ARTWORKPKGNAME = "artworkPackageName";
	static final String KEY_COUNTRIES = "countries";
	static final String KEY_LOCALCOPYLANG = "localCopyLanguage";
	static final String KEY_POAS = "POAs";
	static final String KEY_LOCALCOPIES = "localCopies";
	static final String KEY_IS_APPROVE_COPY_MATRIX = "IsApproveCopyMatrix";
	static final String KEY_SELECTED_WBS_IDS = "SelectedWBSIds";
	static final String KEY_ROUTE_TEMPLATE_AUTHOR = "RouteTemplateAuthor";
	static final String KEY_ROUTE_TEMPLATE_APPROVER = "RouteTemplateApprover";
	static final String KEY_PARENT_ID = "parentId";
	static final String KEY_NEW_COPY_TEXT = "newCopyText";
	static final String KEY_RESPONSIBLE_FUNCTION = "ResonsibleFunction";
	static final String KEY_APPROVE = "approve";
	static final String KEY_AUTHOR = "author";
	static final String KEY_SUBMIT = "submit";
	static final String KEY_REVISE_LCE = "reviseLCE";
	static final String KEY_REVISE_MCE = "reviseMCE";
	static final String KEY_SAVE = "save";
	static final String KEY_SELECTED_POA_IDs = "selectedPOAIds";
	static final String KEY_LANGUAGE = "Language";
	static final String KEY_CONTENT_ID = "contentId";
	static final String KEY_IS_GRAPHIC = "isGraphic";
	static final String KEY_IS_GRAPHIC_ELEMENT = "isGraphicElement";
	static final String KEY_COMPOSITE_COPY = "isCompositeCopy";
	static final String KEY_ACTION = "action";
	static final String KEY_IMAGE_DATA = "imageData";
	static final String KEY_SELECTEDMCEs_POAs = "selectedMCEs_POAs";
	static final String KEY_COPY_TEXT = "CopyText";
	static final String KEY_COPY_TEXT_RTE = "Copy Text_RTE";
	static final String KEY_SPOA_OBJ_ID = "sPOAObjId";
	static final String KEY_FCS_ENABLED = "fcsEnabled";
	static final String KEY_IS_TASK_ACTIVE = "isTaskActive";
	static final String KEY_SHOW_SUBMIT_BUTTON = "showSubmitButton";
	static final String KEY_POA_MC_INST_SEQ = "poa_mc_instSeq";
	static final String KEY_JPO_NAME = "JPOName";
	static final String KEY_JPO_METHOD_NAME = "JPOMethodName";
	static final String KEY_SELECTED_POA_LIST = "SelectedPOAList";
	static final String KEY_FILE_DEST_PATH = "FileDestPath";
	static final String KEY_APPEND = "append";
	static final String KEY_UNLOCK = "unlock";
	static final String KEY_type = "type";
	static final String KEY_policy = "policy";
	static final String KEY_PARENT_REL_NAME = "parentRelName";
	static final String KEY_NO_OF_FILES = "noOfFiles";
	static final String KEY_OBJECT_ACTION = "objectAction";
	static final String KEY_FORMAT = "format";
	static final String KEY_MASTERLANG = "masterLanguage";
	static final String STR_MCEReviseStatus = "current.access[revise]";
	static final String KEY_MCEReviseStatus = "MCEReviseStatus";
	static final String KEY_ALLOW_REEXECUTION = "No";
	static final String KEY_RESTART_POINT = "Created";
	static final String KEY_NOTIFY_OWNER = "No";
	static final String SELECT_PHYSICAL_ID = "physicalid";
	static final String SEPARATOR_NEW_LINE = "\n";
	static final String ATTRIBUTE_PROGRAM_ARGS = PropertyUtil.getSchemaProperty(null, "attribute_ProgramArguments");
	static final String KEY_JOB_STATUS = "jobStatus";
	static final String KEY_JOB_ID = "jobId";
	private static final String RANGE_TRUE = "true";
	private static final String SEL_ATTR_SCHEDULED_COMPLETION_DATE = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE);
	private static final String AUTHORING_DURATION = "AuthoringDuration";
	private static final String APPROVAL_DURATION = "ApprovalDuration";
	private static final String PHYSICALID_CONNECTION= "physicalid[connection]";
	private static final String SEL_ATTR_ACTUAL_COMPLETION_DATE = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ACTUAL_COMPLETION_DATE);
	static final String KEY_MC = "MC-";

	private static final String strBGSubmittedPref = "Submitted : ";

	static String MCSURL = DomainConstants.EMPTY_STRING;
	
	static final String COLUMN_AUTHOR_TASK_SD = "AuthoringTaskSD";
	static final String COLUMN_APPROVER_TASK_SD = "ApprovalTaskSD";
	static final String AUTHOR_TASK_DURATION = "AuthoringDuration";
	static final String APPROVER_TASK_DURATION = "ApprovalDuration";
	
	private static final Logger logger = Logger.getLogger(RTAUtil.class.getName());

	private RTAUtil() {
		throw new IllegalStateException("Utility class");
	}

	public RTAUtil(Context context, Object object) {
		// TODO Auto-generated constructor stub
	}

	/**
	 * The method returns the full name of the user
	 * 
	 * @param context the Enovia Context object
	 * @return The full name of the logged in user
	 */
	public static Object getPersonFullName(Context context) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			JsonArrayBuilder outArr = Json.createArrayBuilder();
			String name = PersonUtil.getFullName(context);
			if (UIUtil.isNotNullAndNotEmpty(name)) {
				outArr.add(name);
			}
			output.add(RTAUtilConstants.JSON_KEY_DATA, outArr);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE + ex.getMessage(), ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
		}
		return output;
	}

	/***
	 * This method returns the POA data
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String getPOAEditData(Context context, String paramString) throws Exception {
		String strOutput;
		JsonArray jsonArr = Json.createArrayBuilder().build();
		try {
			DateFormat simple = new SimpleDateFormat("HH:mm:ss");
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			MCSURL = jsonInputData.getString(KEY_MCSURL);
			paramString = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(paramString, PGWidgetConstants.KEY_OR);
			HashMap<String, Object> mapFinalInfo = new HashMap<>();
			strOutput = null;
			// logger
			long start = System.currentTimeMillis();
			Date result = new Date(start);
			logger.log(Level.INFO, AWLUtil.strcat("Before getFinalMCLCDetails method called:", simple.format(result)));
			mapFinalInfo = getFinalMCLCDetails(context, poaIdList, DomainConstants.EMPTY_STRING);
			long end = System.currentTimeMillis();
			Date resultend = new Date(end);
			logger.log(Level.INFO,
					AWLUtil.strcat("After executing getFinalMCLCDetails method; ", simple.format(resultend)));
			logger.log(Level.INFO, AWLUtil.strcat("Time diff to execute getFinalMCLCDetails takes ", (end - start), "ms"));

			// logger
			jsonArr = convertMaptoJSONFormat(context, mapFinalInfo);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, e.getMessage())
					.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e)).build().toString();
		}
		return jsonArr.toString();
	}

	/***
	 * This method returns all the MC's and LC's Data
	 * 
	 * @param context
	 * @param poaIdList
	 * @param strNewMCId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static HashMap getFinalMCLCDetails(Context context, StringList poaIdList, String strNewMCId)
			throws Exception {
		DateFormat simple = new SimpleDateFormat("HH:mm:ss");
		long start = System.currentTimeMillis();
		Date result = new Date(start);
		logger.log(Level.INFO,
				AWLUtil.strcat("Before getArtworkMasterIdSel called in getFinalMCLCDetails:", simple.format(result)));
		String strArtworkMasterSelectable = ArtworkContent.getArtworkMasterIdSel(context);
		long end = System.currentTimeMillis();
		Date resultend = new Date(end);
		logger.log(Level.INFO, AWLUtil.strcat("After executing getArtworkMasterIdSel in getFinalMCLCDetails:; ",
				simple.format(resultend)));
		logger.log(Level.INFO, AWLUtil.strcat("Time diff to execute getArtworkMasterIdSel in getFinalMCLCDetails:; ",
				(end - start), "ms"));
		StringList selectList = StringList.create(DomainConstants.SELECT_ID, KEY_LAST, KEY_REVISIONS, "revisions.id",
				DomainConstants.SELECT_NAME, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION,
				DomainConstants.SELECT_CURRENT, AWLAttribute.MARKETING_NAME.getSel(context),
				AWLAttribute.TRANSLATE.getSel(context), AWLAttribute.INLINE_TRANSLATION.getSel(context),
				RTAUtilConstants.SELECT_PGRTAGPSFIXEDCES, RTAUtilConstants.SELECT_AUTOMRKCLAIMRESULTS, RTAUtilConstants.SELECT_PGRTAGPSFIXEDRES,
				RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT, RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE,
				STR_MCEReviseStatus,RTAUtilConstants.SELECT_VALIDITY_DATE, RTAUtilConstants.SELECT_ATTR_AWL_INSTRUCTIONS);

		StringList mceRelSelects = StringList.create(AWLAttribute.INSTANCE_SEQUENCE.getSel(context),
				AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));

		StringList mceRelSelects2 = StringList.create(AWLAttribute.NOTES.getSel(context));
		mceRelSelects2.add("attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");

		//Performance IMP point CW6
		selectList.add("from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "|to.attribute["
				+ AWLAttribute.IS_BASE_COPY.get(context) + "]=='Yes'].to.attribute[Copy Text_RTE]");
		selectList.add("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");

		// selectList.add("to[" + AWLRel.ARTWORK_ASSEMBLY.get(context) + "].from[" +
		// AWLType.COPY_LIST.get(context)
		// + "].attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");
		// //---->change, remove CL
		StringList selectList3 = StringList.create(DomainConstants.SELECT_ID, KEY_LAST, KEY_REVISIONS, "revisions.id",
				DomainConstants.SELECT_NAME, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION,
				DomainConstants.SELECT_CURRENT, AWLAttribute.TRANSLATE.getSel(context),
				AWLAttribute.MARKETING_NAME.getSel(context), AWLAttribute.INLINE_TRANSLATION.getSel(context),
				AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context), RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT,
				RTAUtilConstants.SELECT_VALIDITY_DATE,RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE);
		selectList3.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type");
		selectList3.add("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]");
		
		//Performance IMP point CW6
		selectList3.add("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");

		StringList selectList2 = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
				DomainConstants.SELECT_TYPE, RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT,RTAUtilConstants.SELECT_VALIDITY_DATE,
				RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE);
		selectList2.add("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "]");
		selectList2.add(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context));
		selectList2.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id");

		String strPOAName = null;
		StringList strMCIdsList = new StringList();
		StringList strLCIdsList = new StringList();
		POA poa = null;
		MapList mlPOAMCs = null;
		MapList mlPOALCs = null;

		HashMap<String, Object> mapFinalInfo = new HashMap<>();
		String strWhere = null;
		for (String strPOAId : poaIdList) {
			if (BusinessUtil.isNotNullOrEmpty(strPOAId)) {
				poa = new POA(strPOAId);
				MapList mlPOALCList = null;
				DomainObject dmoPOAObject = DomainObject.newInstance(context, strPOAId);
				strPOAName = BusinessUtil.getInfo(context, strPOAId, DomainConstants.SELECT_NAME);
				// Prepare MAP of Unique MCs with POAs level info
				if (BusinessUtil.isNullOrEmpty(strNewMCId)) {
					// logger
					long start1 = System.currentTimeMillis();
					Date result1 = new Date(start1);
					logger.log(Level.INFO,
							AWLUtil.strcat("Before getArtworkMasters### if block ### called in getFinalMCLCDetails:",
									simple.format(result1)));
					mlPOAMCs = poa.getArtworkMasters(context, selectList, mceRelSelects, DomainConstants.EMPTY_STRING);
					mlPOALCList = dmoPOAObject.getRelatedObjects(context,
							AWLRel.ARTWORK_ASSEMBLY.get(context),
							AWLType.ARTWORK_ELEMENT.get(context),
							selectList2, // Object Select
							mceRelSelects2, // rel Select
							false, // get To
							true, // get From
							(short) 0, // recurse level
							null, // where Clause
							null,
							0);
					long end1 = System.currentTimeMillis();
					Date resultend1 = new Date(end1);
					logger.log(Level.INFO,
							AWLUtil.strcat(
									"After executing getArtworkMasters ### if block ### in getFinalMCLCDetails:; ",
									simple.format(resultend1)));
					logger.log(Level.INFO,
							AWLUtil.strcat(
									"Time diff to execute getArtworkMasters ### if block ### in getFinalMCLCDetails:; ",
									(end1 - start1), "ms"));
					// logger
				} else {

					strWhere = "id=='" + strNewMCId + "'";
					// logger
					long start1 = System.currentTimeMillis();
					Date result1 = new Date(start1);
					logger.log(Level.INFO,
							AWLUtil.strcat("Before getArtworkMasters### else block ### called in getFinalMCLCDetails:",
									simple.format(result1)));
					mlPOAMCs = poa.getArtworkMasters(context, selectList, mceRelSelects, strWhere);
					mlPOALCList = dmoPOAObject.getRelatedObjects(context,
							AWLRel.ARTWORK_ASSEMBLY.get(context),
							AWLType.ARTWORK_ELEMENT.get(context),
							selectList2, // Object Select
							mceRelSelects2, // rel Select
							false, // get To
							true, // get From
							(short) 0, // recurse level
							null, // where Clause
							null,
							0);
					long end1 = System.currentTimeMillis();
					Date resultend1 = new Date(end1);
					logger.log(Level.INFO,
							AWLUtil.strcat(
									"After executing getArtworkMasters ### else block ### in getFinalMCLCDetails:; ",
									simple.format(resultend1)));
					logger.log(Level.INFO, AWLUtil.strcat(
							"Time diff to execute getArtworkMasters ### else block ### in getFinalMCLCDetails:; ",
							(end1 - start1), "ms"));
					// logger
				}
				// logger
				long start1 = System.currentTimeMillis();
				Date result1 = new Date(start1);
				logger.log(Level.INFO,
						AWLUtil.strcat("Before getMCDetails called in getFinalMCLCDetails:", simple.format(result1)));
				validityDateCheck(context, mlPOAMCs);
				getMCDetails(context, strPOAName, mlPOAMCs, mlPOALCList, mapFinalInfo, strMCIdsList, poa);
				long end1 = System.currentTimeMillis();
				Date resultend1 = new Date(end1);
				logger.log(Level.INFO, AWLUtil.strcat("After executing getMCDetails in getFinalMCLCDetails:; ",
						simple.format(resultend1)));
				logger.log(Level.INFO, AWLUtil.strcat("Time diff to execute getMCDetails in getFinalMCLCDetails:; ",
						(end1 - start1), "ms"));
				// logger
				// Prepare MAP of Unique LCs with POAs level info
				selectList3.add(strArtworkMasterSelectable);
				if (BusinessUtil.isNullOrEmpty(strNewMCId)) {

					// logger
					long start2 = System.currentTimeMillis();
					Date result2 = new Date(start2);
					logger.log(Level.INFO,
							AWLUtil.strcat("Before getArtworkElements ###if block #### called in getFinalMCLCDetails:",
									simple.format(result2)));
					mlPOALCs = poa.getArtworkElements(context, selectList3, mceRelSelects2);
					long end2 = System.currentTimeMillis();
					Date resultend2 = new Date(end2);
					logger.log(Level.INFO,
							AWLUtil.strcat(
									"After executing getArtworkElements ###if block #### in getFinalMCLCDetails:; ",
									simple.format(resultend2)));
					logger.log(Level.INFO, AWLUtil.strcat(
							"Time diff to execute getArtworkElements ###if block ####  in getFinalMCLCDetails:; ",
							(end2 - start2), "ms"));
					// logger
				} else {
					// logger
					long start2 = System.currentTimeMillis();
					Date result2 = new Date(start2);
					logger.log(Level.INFO, AWLUtil.strcat(
							"Before getArtworkElementsForMaster ###else block #### called in getFinalMCLCDetails:",
							simple.format(result2)));
					mlPOALCs = poa.getArtworkElementsForMaster(context, selectList3, mceRelSelects2, strNewMCId, false);
					long end2 = System.currentTimeMillis();
					Date resultend2 = new Date(end2);
					logger.log(Level.INFO, AWLUtil.strcat(
							"After executing getArtworkElementsForMaster ###else block #### in getFinalMCLCDetails:; ",
							simple.format(resultend2)));
					logger.log(Level.INFO, AWLUtil.strcat(
							"Time diff to execute getArtworkElementsForMaster ###else block ####  in getFinalMCLCDetails:; ",
							(end2 - start2), "ms"));
					// logger
				}
				// logger
				long start2 = System.currentTimeMillis();
				Date result2 = new Date(start2);
				logger.log(Level.INFO,
						AWLUtil.strcat("Before getLCDetails called in getFinalMCLCDetails:", simple.format(result2)));
	            
				validityDateCheck(context, mlPOALCs);
				getLCDetails(context, strPOAName, mlPOALCs, mapFinalInfo, strLCIdsList);
				long end2 = System.currentTimeMillis();
				Date resultend2 = new Date(end2);
				logger.log(Level.INFO, AWLUtil.strcat("After executing getLCDetails in getFinalMCLCDetails:; ",
						simple.format(resultend2)));
				logger.log(Level.INFO, AWLUtil.strcat("Time diff to execute getLCDetails in getFinalMCLCDetails:; ",
						(end2 - start2) + "ms"));
				// logger
			}
		}
		return mapFinalInfo;
	}

	public static void getAuthorApproverPersonInfo(Context context, MapList mlPOAMCLCs) throws Exception {
	    String PERSON_STATE_INACTIVE = PropertyUtil.getSchemaProperty(context, "Policy", DomainConstants.POLICY_PERSON,"state_Inactive");
		StringList slMCLCIdList = new StringList();
		StringList slInactiveAssignees = new StringList();
		for(int i=0; i<mlPOAMCLCs.size(); i++) {
			Map mPOAMCLCs = (Map) mlPOAMCLCs.get(i);
			String strType = (String) mPOAMCLCs.get(DomainConstants.SELECT_TYPE);
			if (!strType.equals(RTAUtilConstants.TYP_POA)) {
				slMCLCIdList.add((String)mPOAMCLCs.get(DomainObject.SELECT_ID));
			}
		}
		StringList slSelectList = StringList.create(DomainObject.SELECT_ID, DomainObject.SELECT_NAME);
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "]");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "]");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.name");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.name");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.current");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.current");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.type");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.type");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.id");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.id");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.from[Route Node].to.current");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.from[Route Node].to.current");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.from[Route Node].to.name");
		slSelectList.add("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.from[Route Node].to.name");
		MapList mlAssignee = BusinessUtil.getInfoList(context, slMCLCIdList, slSelectList);
		for (int i = 0; i < mlAssignee.size(); i++) {
			slInactiveAssignees = new StringList();
			Map mAssignment = (Map)mlAssignee.get(i);
			StringList slAuthorType = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.type");
			StringList slLCId = (StringList)mAssignment.get(DomainObject.SELECT_ID);
			String strlCId = slLCId.get(0);
			if(BusinessUtil.isNotNullOrEmpty(slAuthorType)) {
				StringList slAuthorCurrent = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.current");
				StringList slAuthorName = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.name");
				StringList slMultiAuthorCurrent = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.from[Route Node].to.current");
				StringList slMultiAuthorName = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_AUTHOR.get(context) + "].to.from[Route Node].to.name");
				for(int j = 0; j < slAuthorType.size(); j++)
				{
					if((DomainObject.TYPE_PERSON).equals((String)slAuthorType.get(j)))
					{
						if(PERSON_STATE_INACTIVE.equals((String)slAuthorCurrent.get(j)))
						{
							String strTempPersonName = (String)slAuthorName.get(j);
							if(!slInactiveAssignees.contains(strTempPersonName))
								slInactiveAssignees.add(strTempPersonName);
						}
						
					}else {
						if(BusinessUtil.isNotNullOrEmpty(slMultiAuthorCurrent)) {
							if(slMultiAuthorCurrent.contains(PERSON_STATE_INACTIVE))
							{
								for(int k = 0; k < slMultiAuthorCurrent.size(); k++)
								{
									if(PERSON_STATE_INACTIVE.equals((String)slMultiAuthorCurrent.get(k)))
									{
										String strTempPersonName = (String)slMultiAuthorName.get(k);
										if(!slInactiveAssignees.contains(strTempPersonName))
											slInactiveAssignees.add(strTempPersonName);
									}
								}
							}
						}
					}
					
				}
			}
			StringList slApproverType = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.type");
			if(BusinessUtil.isNotNullOrEmpty(slApproverType)) {
				StringList slApproverCurrent = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.current");
				StringList slApproverName = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.name");
				StringList slMultiApproverCurrent = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.from[Route Node].to.current");
				StringList slMultiApproverName = (StringList)mAssignment.get("from[" + AWLRel.ARTWORK_CONTENT_APPROVER.get(context) + "].to.from[Route Node].to.name");
				for(int j = 0; j < slApproverType.size(); j++)
				{
					if((DomainObject.TYPE_PERSON).equals((String)slApproverType.get(j)))
					{
						if(PERSON_STATE_INACTIVE.equals((String)slApproverCurrent.get(j)))
						{
							String strTempPersonName = (String)slApproverCurrent.get(j);
							if(!slInactiveAssignees.contains(strTempPersonName))
								slInactiveAssignees.add(strTempPersonName);
						}
						
					}else {
						if(BusinessUtil.isNotNullOrEmpty(slMultiApproverCurrent)) {
							if(slMultiApproverCurrent.contains(PERSON_STATE_INACTIVE))
							{
								for(int k = 0; k < slMultiApproverCurrent.size(); k++)
								{
									if(PERSON_STATE_INACTIVE.equals((String)slMultiApproverCurrent.get(k)))
									{
										String strTempPersonName = (String)slMultiApproverName.get(k);
										if(!slInactiveAssignees.contains(strTempPersonName))
											slInactiveAssignees.add(strTempPersonName);
									}
								}
							}
						}
					}
					
				}
			}
			for(int l=0; l<mlPOAMCLCs.size(); l++) {
				Map mPOAMCLCs = (Map) mlPOAMCLCs.get(l);
				if(strlCId.equals(mPOAMCLCs.get(DomainObject.SELECT_ID))) {
					mPOAMCLCs.put(RTAUtilConstants.KEY_INACTIVE_USERS, slInactiveAssignees);
				}
			}
		}		
	}

	public static HashMap<String, Object> getPOAArtworkElementsForMce(Context context, StringList poaIdList, String strMasterId)
			throws Exception {

		StringList slMCEIds = FrameworkUtil.split(strMasterId, PGWidgetConstants.KEY_COMMA_SEPARATOR);

		HashMap<String, Object> mapFinalInfo = new HashMap<String, Object>();
		StringList strMCIdsList = new StringList();
		StringList strLCIdsList = new StringList();
		DateFormat simple = new SimpleDateFormat("HH:mm:ss");
		for (String strPOAId : poaIdList) {
			POA poa = new POA(strPOAId);
			String strPOAName = BusinessUtil.getInfo(context, strPOAId, DomainConstants.SELECT_NAME);
			StringList slWhereMCE = new StringList();
			StringList slWhereLCE = new StringList();
			for(int i=0 ; i<slMCEIds.size() ; i++){
				slWhereMCE.add("id=='" + strMasterId + "'");
				slWhereLCE.add("to["+ AWLRel.ARTWORK_ELEMENT_CONTENT.get(context)+ "].from.id=='"+ strMasterId+ "'" );
			}
			String strWhereMCE = slWhereMCE.join(" || ");
			String strWhereLCE = slWhereLCE.join(" || ");
			// Master copy details

			StringList selectList = StringList.create(DomainConstants.SELECT_ID, KEY_LAST, KEY_REVISIONS, "revisions.id",
					DomainConstants.SELECT_NAME, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION,
					DomainConstants.SELECT_CURRENT, AWLAttribute.MARKETING_NAME.getSel(context),
					AWLAttribute.TRANSLATE.getSel(context), AWLAttribute.INLINE_TRANSLATION.getSel(context),
					RTAUtilConstants.SELECT_PGRTAGPSFIXEDCES, RTAUtilConstants.SELECT_AUTOMRKCLAIMRESULTS, RTAUtilConstants.SELECT_PGRTAGPSFIXEDRES,
					RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT, RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE,
					STR_MCEReviseStatus,RTAUtilConstants.SELECT_VALIDITY_DATE);

			selectList.add("from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "|to.attribute["
					+ AWLAttribute.IS_BASE_COPY.get(context) + "]=='Yes'].to.attribute[Copy Text_RTE]");
			selectList.add("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");

			StringList mceRelSelects = StringList.create(AWLAttribute.INSTANCE_SEQUENCE.getSel(context),
					AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));

			StringList mceRelSelects2 = StringList.create(AWLAttribute.NOTES.getSel(context));
			mceRelSelects2.add("attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");

			long start = System.currentTimeMillis();
			Date result = new Date(start);
			logger.log(Level.INFO,
					AWLUtil.strcat("Before getArtworkMasterIdSel called in getFinalMCLCDetails:", simple.format(result)));

			StringList selectList3 = StringList.create(DomainConstants.SELECT_ID, KEY_LAST, KEY_REVISIONS, "revisions.id",
					DomainConstants.SELECT_NAME, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION,
					DomainConstants.SELECT_CURRENT, AWLAttribute.TRANSLATE.getSel(context),
					AWLAttribute.MARKETING_NAME.getSel(context), AWLAttribute.INLINE_TRANSLATION.getSel(context),
					AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context), RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT,
					RTAUtilConstants.SELECT_VALIDITY_DATE,RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE);
			selectList3.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type");
			selectList3.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id");
			selectList3.add("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]");
			selectList3.add("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "]");
			selectList3.add("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");
			
			MapList mlPOAMCs = poa.getArtworkMasters(context, selectList, mceRelSelects, strWhereMCE);

			MapList mlPOALCList = poa.getRelatedObjects(context,
					AWLRel.ARTWORK_ASSEMBLY.get(context),
					AWLType.ARTWORK_ELEMENT.get(context),
					selectList3, // Object Select
					mceRelSelects2, // rel Select
					false, // get To
					true, // get From
					(short) 0, // recurse level
					strWhereLCE, // where Clause
					null,
					0);

			RTAUtil.getMCDetails(context, strPOAName, mlPOAMCs, mlPOALCList, mapFinalInfo, strMCIdsList, poa);
			RTAUtil.getLCDetails(context, strPOAName, mlPOALCList, mapFinalInfo, strLCIdsList);
		}
		return mapFinalInfo;
	}

	
	/***
	 * This method returns all the MC Data
	 * 
	 * @param context
	 * @param strPOAName
	 * @param mlPOAMCs
	 * @param mapFinalInfo
	 * @param strMCIdsList
	 * @param poa
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void getMCDetails(Context context, String strPOAName, MapList mlPOAMCs, MapList mlPOALCList,
			HashMap<String, Object> mapFinalInfo, StringList strMCIdsList, POA poa) throws Exception {
		DateFormat simple = new SimpleDateFormat("HH:mm:ss");
		Map<?, ?> mLCInfo = null;
		String strMCId = null;
		String strFlag = null;
		String strImage = null;

		Map eachLCs = null;
		String strArtElemFromId = null;

		HashMap<Object, Object> mapInfo = null;

		StringList slMCLang = null;
		StringList slMCNotes = null;
		String strMCNotes = null;
		int iPOAMCsSize = mlPOAMCs.size();
		for (int i = 0; i < iPOAMCsSize; i++) {
			mapInfo = new HashMap<>();
			mLCInfo = (Map<?, ?>) mlPOAMCs.get(i);
			strMCId = (String) mLCInfo.get(DomainConstants.SELECT_ID);
			MapList mlMCLang = new MapList();

			if (mlPOALCList != null && !mlPOALCList.isEmpty()) {
				for (int j = 0; j < mlPOALCList.size(); j++) {
					eachLCs = (Map) mlPOALCList.get(j);
					strArtElemFromId = (String) eachLCs
							.get("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id");

					if (strMCId.equals(strArtElemFromId)) {
						mlMCLang.add(eachLCs);

					}
				}
			}

			slMCLang = BusinessUtil.toStringList(mlMCLang, AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context));
			slMCNotes = BusinessUtil.toStringList(mlMCLang, AWLAttribute.NOTES.getSel(context));
			// logger
			long start = System.currentTimeMillis();
			Date result = new Date(start);
			logger.log(Level.INFO,
					AWLUtil.strcat("Before getNotesForMC called in getMCDetails:", simple.format(result)));
			strMCNotes = getNotesForMC(slMCNotes);
			long end = System.currentTimeMillis();
			Date resultend = new Date(end);
			logger.log(Level.INFO, AWLUtil.strcat("After executing getNotesForMC called in getMCDetails:; ",
					simple.format(resultend)));
			logger.log(Level.INFO, AWLUtil.strcat("Time diff to execute getNotesForMC called in getMCDetails:; ",
					(end - start), "ms"));
			// logger
			if (!strMCIdsList.contains(strMCId)) {
				strMCIdsList.add(strMCId);
				// logger
				long start1 = System.currentTimeMillis();
				Date result1 = new Date(start1);
				logger.log(Level.INFO,
						AWLUtil.strcat("Before getCopyElementType called in getMCDetails:", simple.format(result1)));
				strFlag = getCopyElementType(context, mLCInfo);
				long end1 = System.currentTimeMillis();
				Date resultend1 = new Date(end1);
				logger.log(Level.INFO, AWLUtil.strcat("After executing getCopyElementType called in getMCDetails:; ",
						simple.format(resultend1)));
				logger.log(Level.INFO, AWLUtil.strcat(
						"Time diff to execute getCopyElementType called in getMCDetails:; ", (end1 - start1), "ms"));
				// logger
				mapInfo.put(DomainConstants.SELECT_ID, strMCId);
				mapInfo.put(DomainConstants.SELECT_NAME, mLCInfo.get(DomainConstants.SELECT_NAME));
				mapInfo.put(DomainConstants.SELECT_TYPE, mLCInfo.get(DomainConstants.SELECT_TYPE));
				mapInfo.put(DomainConstants.SELECT_REVISION, mLCInfo.get(DomainConstants.SELECT_REVISION));
				mapInfo.put(KEY_MCEReviseStatus, mLCInfo.get(STR_MCEReviseStatus));
				// logger
				long start2 = System.currentTimeMillis();
				Date result2 = new Date(start2);
				logger.log(Level.INFO,
						AWLUtil.strcat("Before getFormattedRevisions called in getMCDetails:", simple.format(result2)));
				JsonArrayBuilder jsonArrRevData = getFormattedRevisions(mLCInfo.get(KEY_REVISIONS), mLCInfo);
				long end2 = System.currentTimeMillis();
				Date resultend2 = new Date(end2);
				logger.log(Level.INFO, AWLUtil.strcat("After executing getFormattedRevisions called in getMCDetails:; ",
						simple.format(resultend2)));
				logger.log(Level.INFO, AWLUtil.strcat(
						"Time diff to execute getFormattedRevisions called in getMCDetails:; ", (end2 - start2), "ms"));
				// logger
				mapInfo.put(KEY_REVISIONS, jsonArrRevData.build().toString());
				mapInfo.put(KEY_LAST, mLCInfo.get(KEY_LAST));
				mapInfo.put(DomainConstants.SELECT_CURRENT, mLCInfo.get(DomainConstants.SELECT_CURRENT));
				mapInfo.put(KEY_TITLE, mLCInfo.get(AWLAttribute.MARKETING_NAME.getSel(context)));
				mapInfo.put(KEY_FLAG, strFlag);
				mapInfo.put(KEY_AUTOMATIC_MRK_CLAIM_RESULTS, mLCInfo.get(RTAUtilConstants.SELECT_AUTOMRKCLAIMRESULTS));
				mapInfo.put(KEY_RTA_GPS_FIXED_CES, mLCInfo.get(RTAUtilConstants.SELECT_PGRTAGPSFIXEDRES));
				if (strFlag.equals(STR_GRAPHICTYPE)) {
					try {
						String[] arrayOfString = new String[2];
						arrayOfString[0] = strMCId;
						arrayOfString[1] = MCSURL;
						// logger
						long start3 = System.currentTimeMillis();
						Date result3 = new Date(start3);
						logger.log(Level.INFO, AWLUtil.strcat(
								"Before JPO.invoke=AWLGraphicsElementUI  in getMCDetails:", simple.format(result3)));
						strImage = JPO.invoke(context, "AWLGraphicsElementUI", null, "getGraphicImageURLforPOAAction",
								arrayOfString, String.class);
						long end3 = System.currentTimeMillis();
						Date resultend3 = new Date(end3);
						logger.log(Level.INFO,
								AWLUtil.strcat(
										"After executing JPO.invoke=AWLGraphicsElementUI called in getMCDetails:; ",
										simple.format(resultend3)));
						logger.log(Level.INFO, AWLUtil.strcat(
								"Time diff to execute JPO.invoke=AWLGraphicsElementUI called in getMCDetails:; ",
								(end3 - start3), "ms"));
						// logger
					} catch (Exception e) {
						throw e;
					}
					mapInfo.put(KEY_CONTENT, strImage);
				} else {
					mapInfo.put(KEY_CONTENT, mLCInfo.get(
							"from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].to.attribute[Copy Text_RTE]"));
				}
				mapInfo.put(KEY_LANGUAGES, FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
				mapInfo.put(strPOAName + STR_LANG, FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
				mapInfo.put(strPOAName + STR_INSSEQ, mLCInfo.get(AWLAttribute.INSTANCE_SEQUENCE.getSel(context)));
				mapInfo.put(strPOAName + STR_ORDER,
						mLCInfo.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context)));
				mapInfo.put(strPOAName + STR_NOTES, strMCNotes);
				mapInfo.put(RTAUtilConstants.ATTR_PG_CONFIRM_ASSIGNMENT,
						mLCInfo.get(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT));
				mapInfo.put(RTAUtilConstants.ATTRIBUTE_PGSUBCOPYTYPE,
						mLCInfo.get(RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE));
				mapInfo.put(RTAUtilConstants.ATTR_VALIDITY_DATE,
						mLCInfo.get(RTAUtilConstants.SELECT_VALIDITY_DATE));
				mapInfo.put(RTAUtilConstants.ATTR_INSTRUCTIONS,
						mLCInfo.get(RTAUtilConstants.SELECT_ATTR_AWL_INSTRUCTIONS));
				mapInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, 
						mLCInfo.get(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED));
				mapFinalInfo.put(strMCId, mapInfo);
			} else {
				mapInfo = (HashMap<Object, Object>) mapFinalInfo.get(strMCId);
				mapInfo.put(strPOAName + STR_LANG, FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
				mapInfo.put(strPOAName + STR_INSSEQ, mLCInfo.get(AWLAttribute.INSTANCE_SEQUENCE.getSel(context)));
				mapInfo.put(strPOAName + STR_ORDER,
						mLCInfo.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context)));
				mapInfo.put(strPOAName + STR_NOTES, strMCNotes);
				mapFinalInfo.put(strMCId, mapInfo);
			}

		}
	}

	/***
	 * This method
	 * 
	 * @param revisions
	 * @param mLCInfo
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getFormattedRevisions(Object revisions, Map<?, ?> mLCInfo) throws Exception {
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonArrayBuilder jsonArrRevData = Json.createArrayBuilder();
		String strRevId = null;
		String strRevName = null;
		if (revisions instanceof StringList) {
			StringList slRevisions = (StringList) revisions;
			for (String strRev : slRevisions) {
				strRevId = (String) mLCInfo.get("revisions[" + strRev + "].id");
				jsonObjKeyVariant = Json.createObjectBuilder();
				jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, strRev);
				jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, strRev);
				jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strRevId);
				jsonArrRevData.add(jsonObjKeyVariant);
			}
		} else {
			strRevName = (String) revisions;
			strRevId = (String) mLCInfo.get("revisions[" + strRevName + "].id");
			jsonObjKeyVariant = Json.createObjectBuilder();
			jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, strRevName);
			jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, strRevName);
			jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strRevId);
			jsonArrRevData.add(jsonObjKeyVariant);
		}
		return jsonArrRevData;
	}

	/***
	 * 
	 * @param slMCNotes
	 * @return
	 * @throws Exception
	 */
	public static String getNotesForMC(StringList slMCNotes) throws Exception {
		StringList slMCNotesNew = new StringList();
		String strNote = null;
		if (BusinessUtil.isNotNullOrEmpty(slMCNotes)) {
			for (int i = 0; i < slMCNotes.size(); i++) {
				strNote = slMCNotes.get(i).trim();
				if (BusinessUtil.isNotNullOrEmpty(strNote)) {
					slMCNotesNew.add(strNote);
				}
			}
		}
		if (BusinessUtil.isNotNullOrEmpty(slMCNotesNew)) {
			return PGWidgetConstants.CONSTANT_STRING_STAR;
		} else {
			return DomainConstants.EMPTY_STRING;
		}
	}

	/***
	 * This method returns the type of Copy Element Selected
	 * 
	 * @param context
	 * @param mLCInfo
	 * @return
	 * @throws Exception
	 */
	public static String getCopyElementType(Context context, Map<?, ?> mLCInfo) throws Exception {
		String strFlag = null;
		if (((String) mLCInfo.get(AWLAttribute.TRANSLATE.getSel(context))).equalsIgnoreCase(AWLConstants.RANGE_YES)
				&& ((String) mLCInfo.get(AWLAttribute.INLINE_TRANSLATION.getSel(context)))
						.equalsIgnoreCase(AWLConstants.RANGE_NO)) {
			strFlag = STR_TRANSLATE;
		} else if (((String) mLCInfo.get(AWLAttribute.TRANSLATE.getSel(context)))
				.equalsIgnoreCase(AWLConstants.RANGE_NO)) {
			strFlag = STR_NOTRANSLATE;
		} else if (((String) mLCInfo.get(AWLAttribute.INLINE_TRANSLATION.getSel(context)))
				.equalsIgnoreCase(AWLConstants.RANGE_YES)) {
			strFlag = STR_INLINETYPE;
		} else if (((String) mLCInfo.get("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]"))
				.equalsIgnoreCase(AWLConstants.RANGE_TRUE)) {
			strFlag = STR_GRAPHICTYPE;
		} else {
			strFlag = STR_NONE;
		}
		return strFlag;
	}

	/**
	 * This method returns all the LC Data
	 * 
	 * @param context
	 * @param strPOAName
	 * @param mlPOALCs
	 * @param mapFinalInfo
	 * @param strLCIdsList
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void getLCDetails(Context context, String strPOAName, MapList mlPOALCs,
			HashMap<String, Object> mapFinalInfo, StringList strLCIdsList) throws Exception {
		DateFormat simple = new SimpleDateFormat("HH:mm:ss");
		// logger
		long start = System.currentTimeMillis();
		Date result = new Date(start);
		logger.log(Level.INFO,
				AWLUtil.strcat("Before getArtworkMasterIdSel method called in getLCDetails:", simple.format(result)));
		String strArtworkMasterSelectable = ArtworkContent.getArtworkMasterIdSel(context);
		long end = System.currentTimeMillis();
		Date resultend = new Date(end);
		logger.log(Level.INFO,
				AWLUtil.strcat("After executing getArtworkMasterIdSel in getLCDetails:; ", simple.format(resultend)));
		logger.log(Level.INFO,
				AWLUtil.strcat("Time diff to execute getArtworkMasterIdSel in getLCDetails:; ", (end - start), "ms"));
		// logger
		Map<?, ?> mLCInfo = null;
		String strLCId = null;
		String strMCId = null;
		String strMCLCIdsKey = null;
		String strNotes = null;
		String strFlag = null;
		HashMap<String, Object> mapInfo = new HashMap<>();
		Object tempString = null;
		int iPOALCsSize = mlPOALCs.size();
		for (int i = 0; i < iPOALCsSize; i++) {
			mapInfo = new HashMap<>();
			mLCInfo = (Map<?, ?>) mlPOALCs.get(i);
			strLCId = (String) mLCInfo.get(DomainConstants.SELECT_ID);
			strMCId = (String) mLCInfo.get(strArtworkMasterSelectable);
			strMCLCIdsKey = strMCId + PGWidgetConstants.KEY_UNDERSCORE + strLCId;
			if (!strLCIdsList.contains(strMCLCIdsKey)) {
				Map mapTemp = getAuthorApproverForCopyElement(context, strLCId);
				if (mapTemp.containsKey(RTAUtilConstants.KEY_AUTHOR)) {
					mapInfo.put(RTAUtilConstants.KEY_AUTHOR, mapTemp.get(RTAUtilConstants.KEY_AUTHOR));
				} if (mapTemp.containsKey(RTAUtilConstants.KEY_APPROVER)) {
					mapInfo.put(RTAUtilConstants.KEY_APPROVER, mapTemp.get(RTAUtilConstants.KEY_APPROVER));
				}
				strFlag = getCopyElementType(context, mLCInfo);

				strLCIdsList.add(strMCLCIdsKey);
				mapInfo.put(DomainConstants.SELECT_ID, strLCId);
				mapInfo.put(DomainConstants.SELECT_NAME, mLCInfo.get(DomainConstants.SELECT_NAME));
				mapInfo.put(DomainConstants.SELECT_TYPE, mLCInfo.get(DomainConstants.SELECT_TYPE));
				mapInfo.put(KEY_PARENTTYPE,
						mLCInfo.get("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type"));
				mapInfo.put(DomainConstants.SELECT_REVISION, mLCInfo.get(DomainConstants.SELECT_REVISION));
				JsonArrayBuilder jsonArrRevData = getFormattedRevisions(mLCInfo.get(KEY_REVISIONS), mLCInfo);
				mapInfo.put(KEY_REVISIONS, jsonArrRevData.build().toString());
				mapInfo.put(KEY_LAST, mLCInfo.get(KEY_LAST));
				mapInfo.put(DomainConstants.SELECT_CURRENT, mLCInfo.get(DomainConstants.SELECT_CURRENT));
				mapInfo.put(KEY_TITLE, mLCInfo.get(AWLAttribute.MARKETING_NAME.getSel(context)));
				mapInfo.put(KEY_FLAG, strFlag);
				mapInfo.put(KEY_CONTENT, mLCInfo.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
				mapInfo.put(strPOAName + STR_LANG, mLCInfo.get(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context)));
				mapInfo.put(strPOAName + STR_INSSEQ, mLCInfo.get(AWLAttribute.INSTANCE_SEQUENCE.getSel(context)));
				mapInfo.put(strPOAName + STR_ORDER,
						mLCInfo.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context)));
				strNotes = (String) mLCInfo.get(AWLAttribute.NOTES.getSel(context));
				if (BusinessUtil.isNotNullOrEmpty(strNotes)) {
					mapInfo.put(strPOAName + STR_NOTESSHORT, PGWidgetConstants.CONSTANT_STRING_STAR);
				} else {
					mapInfo.put(strPOAName + STR_NOTESSHORT, DomainConstants.EMPTY_STRING);
				}
				mapInfo.put(strPOAName + STR_NOTES, strNotes);

				tempString = mLCInfo.get("attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");

				String strValue;
				if (tempString instanceof StringList) {
					StringList temp = (StringList) tempString;
					strValue = PGWidgetUtil.getStringFromSL(temp, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				} else {
					strValue = (String) tempString;
				}
				if (UIUtil.isNullOrEmpty(strValue)) {
					mapInfo.put(strPOAName + KEY_ISMANDATORY, DomainConstants.EMPTY_STRING);
				} else {
					mapInfo.put(strPOAName + KEY_ISMANDATORY, tempString);
				}
				mapInfo.put(KEY_LANGUAGES, mLCInfo.get(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context)));
				mapInfo.put(RTAUtilConstants.ATTRIBUTE_PGSUBCOPYTYPE,
						mLCInfo.get(RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE));
				mapInfo.put(RTAUtilConstants.ATTR_VALIDITY_DATE,
						mLCInfo.get(RTAUtilConstants.SELECT_VALIDITY_DATE));
				mapInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, 
						mLCInfo.get(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED));
				mapFinalInfo.put(strMCLCIdsKey, mapInfo);
			} else {
				mapInfo = (HashMap<String, Object>) mapFinalInfo.get(strMCLCIdsKey);
				mapInfo.put(strPOAName + STR_LANG, mLCInfo.get(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context)));
				mapInfo.put(strPOAName + STR_INSSEQ, mLCInfo.get(AWLAttribute.INSTANCE_SEQUENCE.getSel(context)));
				mapInfo.put(strPOAName + STR_ORDER,
						mLCInfo.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context)));
				strNotes = (String) mLCInfo.get(AWLAttribute.NOTES.getSel(context));
				if (BusinessUtil.isNotNullOrEmpty(strNotes)) {
					mapInfo.put(strPOAName + STR_NOTESSHORT, PGWidgetConstants.CONSTANT_STRING_STAR);
				} else {
					mapInfo.put(strPOAName + STR_NOTESSHORT, DomainConstants.EMPTY_STRING);
				}
				mapInfo.put(strPOAName + STR_NOTES, strNotes);

				tempString = mLCInfo.get("attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");

				String strValue;
				if (tempString instanceof StringList) {
					StringList temp = (StringList) tempString;
					strValue = PGWidgetUtil.getStringFromSL(temp, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				} else {
					strValue = (String) tempString;
				}

				if (UIUtil.isNullOrEmpty(strValue)) {
					mapInfo.put(strPOAName + KEY_ISMANDATORY, DomainConstants.EMPTY_STRING);
				} else {
					mapInfo.put(strPOAName + KEY_ISMANDATORY, tempString);
				}

			}
		}
	}

	/***
	 * This method converts Map to JSON format
	 * 
	 * @param context
	 * @param mapFinalInfo
	 * @return
	 * @throws Exception
	 */
	public static JsonArray convertMaptoJSONFormat(Context context, HashMap<String, Object> mapFinalInfo)
			throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrPOAData = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonArrayBuilder jsonArrHier = null;
		String strKey = null;
		Map<?, ?> mapData = new HashMap();
		MapList mlData = new MapList();
		try {
			for (Entry<String, Object> entry : mapFinalInfo.entrySet()) {
				jsonArrHier = Json.createArrayBuilder();
				jsonObjKeyVariant = Json.createObjectBuilder();
				strKey = entry.getKey();
				if (strKey.contains(PGWidgetConstants.KEY_UNDERSCORE)) {
					StringList slMCLCHierarchy = FrameworkUtil.split(strKey, PGWidgetConstants.KEY_UNDERSCORE);
					jsonArrHier.add(slMCLCHierarchy.get(0));
					jsonArrHier.add(slMCLCHierarchy.get(1));
				} else {
					jsonArrHier.add(strKey);
				}
				Object dataFromMap = mapFinalInfo.get(strKey);
				if(dataFromMap instanceof Map) {
					mapData = (Map<?, ?>) dataFromMap;
					jsonObjKeyVariant = getJsonObjectFromMap(mapData);
					jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
					jsonArrPOAData.add(jsonObjKeyVariant.build());
				}else if(dataFromMap instanceof MapList) {
					mlData = (MapList) dataFromMap;
					for(int i=0 ; i<mlData.size() ; i++) {
						jsonObjKeyVariant = getJsonObjectFromMap((Map<?, ?>) mlData.get(i));
						jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
						jsonArrPOAData.add(jsonObjKeyVariant.build());
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return jsonArrPOAData.build();
	}

	public static JsonObjectBuilder getJsonObjectFromMap(Map<?,?> mapData) {
		JsonObjectBuilder jsonObjBldr = Json.createObjectBuilder();
		for (Entry<?, ?> entryMCLC : mapData.entrySet()) {
			String strValue = null;
			if (entryMCLC.getValue() instanceof StringList) {
				StringList temp = (StringList) entryMCLC.getValue();
				strValue = PGWidgetUtil.getStringFromSL(temp, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			} else {
				strValue = (String) entryMCLC.getValue();
			}
			if (BusinessUtil.isNotNullOrEmpty(strValue)) {
				jsonObjBldr.add((String) entryMCLC.getKey(), strValue);
			} else {
				jsonObjBldr.add((String) entryMCLC.getKey(), DomainConstants.EMPTY_STRING);
			}
		}
		return jsonObjBldr;
		
	}
	/***
	 * This method returns Header Data of POA
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 */
	public static String getPOAHeaderData(Context context, String paramString) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			HashMap<Object, Object> mapFinalPOAHeader = new HashMap<>();
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			paramString = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			String strCheckPOAIds = jsonInputData.getString(RTAUtilConstants.CHECK_POA_IDs);
			StringList checkPOAIdList = FrameworkUtil.split(strCheckPOAIds, PGWidgetConstants.KEY_OR);
			String strTypeOfPOA = POA.standardOrCustomOrMixed(context, checkPOAIdList);
			if(RTAUtilConstants.KEY_POA_MIXED.equalsIgnoreCase(strTypeOfPOA)){
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, AWLPropertyUtil.getI18NString(context, "emxAWL.Message.InvalidPOACombination"));
				output.add(PGWidgetConstants.KEY_ERROR, AWLPropertyUtil.getI18NString(context, "emxAWL.Message.InvalidPOACombination"));
				output.add(PGWidgetConstants.KEY_MESSAGE, AWLPropertyUtil.getI18NString(context, "emxAWL.Message.InvalidPOACombination"));
				output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
				return output.build().toString();
			}
			String strUser = context.getUser();
			boolean isContextUserValid = false;
			StringList poaIdList = FrameworkUtil.split(paramString, PGWidgetConstants.KEY_OR);
			String strTitle = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE);
			String strPOACountryName = AWLUtil.strcat("from[", AWLRel.POA_COUNTRY.get(context), "].to.name");
			String strPOACountryId = AWLUtil.strcat("from[", AWLRel.POA_COUNTRY.get(context), "].to.id");
			String strAssPOAIds = AWLUtil.strcat("to[", AWLRel.ASSOCIATED_POA.get(context), "].from.id");
			String strAssPOAMarketingName = AWLUtil.strcat("to[", AWLRel.ASSOCIATED_POA.get(context),
					"].from.attribute[" + AWLAttribute.MARKETING_NAME.get(context), "]");
			String strAssPOAKindOfCPG = AWLUtil.strcat("to[", AWLRel.ASSOCIATED_POA.get(context), "].from.type.kindof[",
					AWLType.CPG_PRODUCT.get(context), "]");
			String strIsPOAAtBrandLevel = AWLUtil.strcat("to[", AWLRel.ASSOCIATED_POA.get(context), "].from.type");
			String strApprMatrixID = "to[" + DomainConstants.RELATIONSHIP_TASK_DELIVERABLE + "].from["
					+ ArtworkConstants.TYPE_APPROVE_POA_COPY_MATRIX_TASK + "].id";
			String strApprMatrixState = "to[" + DomainConstants.RELATIONSHIP_TASK_DELIVERABLE + "].from["
					+ ArtworkConstants.TYPE_APPROVE_POA_COPY_MATRIX_TASK + "].current";
			String strApprMatrixTaskAssignee = "to[" + DomainConstants.RELATIONSHIP_TASK_DELIVERABLE + "].from["
					+ ArtworkConstants.TYPE_APPROVE_POA_COPY_MATRIX_TASK + "].to["
					+ DomainConstants.RELATIONSHIP_ASSIGNED_TASKS + "].from.name";
			String strAssignAuthorApproverTaskSelect = "to[" + DomainConstants.RELATIONSHIP_TASK_DELIVERABLE + "|from."
					+ DomainConstants.SELECT_CURRENT + "!='" + DomainConstants.STATE_INBOX_TASK_COMPLETE + "' && from."
					+ DomainConstants.SELECT_OWNER + "=='" + context.getUser() + "' ].from["
					+ RTAUtilConstants.TYPE_ASSIGN_AUTHOR_APPROVER_TASK + "].id";
			String strFetchAASelect = "to[" + DomainConstants.RELATIONSHIP_TASK_DELIVERABLE + "].from["
					+ RTAUtilConstants.TYPE_ASSIGN_AUTHOR_APPROVER_TASK + "].id";
			String strArtworkPackageSelectId = "to[" +PropertyUtil.getSchemaProperty(null, "relationship_pgAAA_ProjectToPOA")+ "].from.id";

			StringList slSelects = BusinessUtil.toStringList(DomainConstants.SELECT_ID,
					PGWidgetConstants.SELECT_PHYSICAL_ID, DomainConstants.SELECT_NAME,
					DomainConstants.SELECT_DESCRIPTION, DomainConstants.SELECT_CURRENT, STR_ACCESS_MODIFY,
					DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE),
					AWLAttribute.ARTWORK_USAGE.getSel(context), AWLAttribute.ARTWORK_BASIS.getSel(context),
					strPOACountryName, strPOACountryId, strAssPOAIds, strAssPOAMarketingName, strAssPOAKindOfCPG,
					RTAUtilConstants.SELECT_PGRTAADDITIONALDESCRIPTION, RTAUtilConstants.SELECT_PGRTAGPSFIXEDCES,
					RTAUtilConstants.SELECT_PGRTAADRESSCES, RTAUtilConstants.SELECT_PGRTASIZEBASECES, RTAUtilConstants.SELECT_PGRTAGPSFIXEDRES, RTAUtilConstants.SELECT_AUTOMRKCLAIMRESULTS, strIsPOAAtBrandLevel, strApprMatrixID,
					strApprMatrixState, strApprMatrixTaskAssignee, strAssignAuthorApproverTaskSelect, strArtworkPackageSelectId);
			MapList mlPOAs = BusinessUtil.getInfoList(context, poaIdList, slSelects);
			String strBRAND = AWLType.BRAND.get(context);
			String str9 = AWLPolicy.POA.get(context);
			HashMap<Object, Object> mapPOAInfo = null;
			HashMap<Object, Object> mapPOACountryLangInfo = null;
			HashMap<Object, Object> mapCountryInfo = null;
			HashMap<Object, Object> mapProductInfo = null;
			HashMap<Object, Object> mapLangInfo = null;
			Map<?, ?> mapPOA = null;
			Map<?, ?> mapPOALang = null;
			StringList slCountryNames = null;
			StringList slCountryIds = null;
			StringList slLangNames = null;
			StringList slLangIds = null;
			StringList slLangSeq = null;
			String strPOAId = null;
			String strArtBasis = null;
			String strAccessModify = null;
			String strProductType = null;
			String strApproveMatrixID = null;
			String strApproveMatrixState = null;
			Object oAssignee = null;

			int iPOAListSize = mlPOAs.size();
			int iPOALangsSize = 0;
			POA pOA = null;
			for (int iPOA = 0; iPOA < iPOAListSize; iPOA++) {
				mapPOA = (HashMap<?, ?>) mlPOAs.get(iPOA);
				mapPOAInfo = new HashMap<>();
				mapPOACountryLangInfo = new HashMap<>();
				mapCountryInfo = new HashMap<>();
				mapProductInfo = new HashMap<>();
				mapLangInfo = new HashMap<>();
				slLangNames = new StringList();
				slLangIds = new StringList();
				slLangSeq = new StringList();
				slCountryNames = new StringList();
				slCountryIds = new StringList();
				strPOAId = BusinessUtil.getFirstString(mapPOA, DomainConstants.SELECT_ID);
				pOA = new POA(strPOAId);
				mapPOAInfo.put(DomainConstants.SELECT_NAME,
						BusinessUtil.getFirstString(mapPOA, DomainConstants.SELECT_NAME));
				mapPOAInfo.put(DomainConstants.SELECT_CURRENT, AWLPropertyUtil.getStateI18NString(context, str9,
						BusinessUtil.getFirstString(mapPOA, DomainConstants.SELECT_CURRENT)));
				mapPOAInfo.put(KEY_TITLE, BusinessUtil.getFirstString(mapPOA, strTitle));
				mapPOAInfo.put(STR_ARTWORKUSAGE,
						BusinessUtil.getFirstString(mapPOA, AWLAttribute.ARTWORK_USAGE.getSel(context)));
				mapPOAInfo.put(DomainConstants.SELECT_DESCRIPTION,
						BusinessUtil.getFirstString(mapPOA, DomainConstants.SELECT_DESCRIPTION));
				mapPOAInfo.put(STR_ADDDESC,
						BusinessUtil.getFirstString(mapPOA, RTAUtilConstants.SELECT_PGRTAADDITIONALDESCRIPTION));
				mapPOAInfo.put(KEY_INGREDIENTDEC,
						BusinessUtil.getFirstString(mapPOA, RTAUtilConstants.SELECT_PGRTAGPSFIXEDCES));
				mapPOAInfo.put(KEY_PGRTAADRESSCES,
						BusinessUtil.getFirstString(mapPOA, RTAUtilConstants.SELECT_PGRTAADRESSCES));
				mapPOAInfo.put(KEY_PGRTASIZEBASECES,
								BusinessUtil.getFirstString(mapPOA, RTAUtilConstants.SELECT_PGRTASIZEBASECES));
				mapPOAInfo.put(KEY_AutomaticMRKClaimResults,
						BusinessUtil.getFirstString(mapPOA, RTAUtilConstants.SELECT_AUTOMRKCLAIMRESULTS));
				mapPOAInfo.put(PGWidgetConstants.SELECT_PHYSICAL_ID,
						BusinessUtil.getFirstString(mapPOA, PGWidgetConstants.SELECT_PHYSICAL_ID));
				mapPOAInfo.put(KEY_AUTOMATIC_MRK_CLAIM_RESULTS,
						BusinessUtil.getFirstString(mapPOA, RTAUtilConstants.SELECT_AUTOMRKCLAIMRESULTS));
				mapPOAInfo.put(KEY_RTA_GPS_FIXED_CES,
						BusinessUtil.getFirstString(mapPOA, RTAUtilConstants.SELECT_PGRTAGPSFIXEDRES));

				strProductType = BusinessUtil.getFirstString(mapPOA, strIsPOAAtBrandLevel);
				mapPOAInfo.put(KEY_BRANDLEVEL,
						(BusinessUtil.isNotNullOrEmpty(strProductType) && strProductType.equalsIgnoreCase(strBRAND)));
				strArtBasis = BusinessUtil.getFirstString(mapPOA, AWLAttribute.ARTWORK_BASIS.getSel(context));
				boolean bool = BusinessUtil.isNotNullOrEmpty(strArtBasis)
						? STR_MARKETING_CUSTOMIZATION.equalsIgnoreCase(strArtBasis)
						: false;
				strAccessModify = BusinessUtil.getFirstString(mapPOA, STR_ACCESS_MODIFY);
				strApproveMatrixID = BusinessUtil.getFirstString(mapPOA, strApprMatrixID);

				strApproveMatrixState = BusinessUtil.getFirstString(mapPOA, strApprMatrixState);
				if (BusinessUtil.isNotNullOrEmpty(strApproveMatrixID)) {
					mapPOAInfo.put(KEY_IS_APPROVE_COPY_MATRIX, PGWidgetConstants.STRING_TRUE);
				} else {
					mapPOAInfo.put(KEY_IS_APPROVE_COPY_MATRIX, PGWidgetConstants.STRING_FALSE);
				}

				String assignee = null;
				StringList taskAssignees = new StringList();
				oAssignee = BusinessUtil.getFirstString(mapPOA, strApprMatrixTaskAssignee);
				if (oAssignee != null) {
					if (oAssignee instanceof String) {
						assignee = (String) oAssignee;
						taskAssignees = FrameworkUtil.split(assignee, matrix.db.SelectConstants.cSelectDelimiter);

					} else {
						taskAssignees = (StringList) oAssignee;
					}
				}
				if (taskAssignees.contains(strUser)) {
					isContextUserValid = true;
				}
				boolean isTaskActive = PGWidgetConstants.STATE_ACTIVE.equalsIgnoreCase(strApproveMatrixState);
				mapPOAInfo.put(KEY_IS_TASK_ACTIVE, isTaskActive);

				boolean showSubmitButton = isContextUserValid && isTaskActive;
				mapPOAInfo.put(KEY_SHOW_SUBMIT_BUTTON, showSubmitButton);

				boolean bool1 = AWLConstants.RANGE_TRUE.equalsIgnoreCase(strAccessModify);
				mapPOAInfo.put(KEY_KINDOFHEADER, getKindOfValue(bool, bool1));
				mapPOAInfo.put(RTAUtilConstants.TYPE_ASSIGN_AUTHOR_APPROVER_TASK, mapPOA.get(strFetchAASelect));
				mapPOAInfo.put("isConnectedToAWP", UIUtil.isNotNullAndNotEmpty(PGWidgetUtil.extractMultiValueSelect(mapPOA, strArtworkPackageSelectId)) ? true : false);
				mapPOAInfo.put("isPromoteDisabled", isPromoteDisabled(context, strPOAId));
				mapPOAInfo.put("isDemoteDisabled", isDemoteDisabled(context, strPOAId));
				Object object1 = mapPOA.get(strPOACountryName);
				if (object1 != null) {
					slCountryNames = (StringList) mapPOA.get(strPOACountryName);
					slCountryIds = (StringList) mapPOA.get(strPOACountryId);
				}
				mapCountryInfo.put(KEY_NAME, slCountryNames);
				mapCountryInfo.put(KEY_IDS, slCountryIds);
				Object object2 = mapPOA.get(strAssPOAMarketingName);
				if (object2 != null) {
					mapProductInfo.put(KEY_NAME, object2);
					mapProductInfo.put(KEY_IDS, mapPOA.get(strAssPOAIds));
					mapProductInfo.put(KEY_TYPES, mapPOA.get(strAssPOAKindOfCPG));
				}
				MapList mlPOALangs = pOA.related(AWLType.LOCAL_LANGUAGE, AWLRel.POA_LOCAL_LANGUAGE).relid().relAttr(
						(AWLAttribute[]) AWLUtil.toArray((Object[]) new AWLAttribute[] { AWLAttribute.SEQUENCE }))
						.query(context);
				mlPOALangs.sort(AWLAttribute.SEQUENCE.getSel(context), PGWidgetConstants.STRING_CAPITAL_TRUE, "integer");
				iPOALangsSize = mlPOALangs.size();
				for (int iPOALang = 0; iPOALang < iPOALangsSize; iPOALang++) {
					mapPOALang = (Hashtable<?, ?>) mlPOALangs.get(iPOALang);
					slLangNames.add((String) mapPOALang.get(DomainConstants.SELECT_NAME));
					slLangIds.add((String) mapPOALang.get(DomainConstants.SELECT_ID));
					slLangSeq.add((String) mapPOALang.get(AWLAttribute.SEQUENCE.getSel(context)));
				}
				mapLangInfo.put(KEY_NAME, slLangNames);
				mapLangInfo.put(KEY_IDS, slLangIds);
				mapLangInfo.put(KEY_SEQ, slLangSeq);
				mapPOACountryLangInfo.put(KEY_COUNTRYINFO, mapCountryInfo);
				mapPOACountryLangInfo.put(KEY_PRODUCTINFO, mapProductInfo);
				mapPOACountryLangInfo.put(KEY_POAINFO, mapPOAInfo);
				mapPOACountryLangInfo.put(KEY_LANGUAGEINFO, mapLangInfo);
				mapFinalPOAHeader.put(strPOAId, mapPOACountryLangInfo);
			}
			return (new JsonHelper()).getJsonString(mapFinalPOAHeader);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
	}

	/***
	 * 
	 * @param paramBoolean1
	 * @param paramBoolean2
	 * @return
	 */
	public static String getKindOfValue(boolean paramBoolean1, boolean paramBoolean2) {
		if (paramBoolean1)
			return paramBoolean2 ? STR_CUSTOM_MODIFY_POA : STR_CUSTOM_REFERENCE_POA;
		return paramBoolean2 ? STR_STANDARD_MODIFY_POA : STR_STANDARD_REFERENCE_POA;
	}

	/***
	 * This method returns POA Hierarchy data
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	public static String getPOAHierachyData(Context paramContext, String paramString) throws FrameworkException {

		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrHier = Json.createArrayBuilder();
		JsonObjectBuilder languageArr = Json.createObjectBuilder();
		JsonObjectBuilder strOutput = Json.createObjectBuilder();
		JsonObjectBuilder POAHierachyData = Json.createObjectBuilder();

		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			paramString = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			boolean ShowBrandType = jsonInputData.getBoolean(KEY_SHOWBRANDTYPE);// to check for Brand Type for
																				// PlaceOfOrigin
			StringList poaIdList = FrameworkUtil.split(paramString, PGWidgetConstants.KEY_OR);
			JsonObjectBuilder jsonObjKeyVariant = null;
			JsonObjectBuilder jsonObjKeyVariantChild = null;
			StringList slSelect = new StringList(3);
			slSelect.add("to[" + AWLRel.ASSOCIATED_POA.get(paramContext) + "].from.id");
			slSelect.add("to[" + AWLRel.ASSOCIATED_POA.get(paramContext) + "].from.name");
			slSelect.add("to[" + AWLRel.ASSOCIATED_POA.get(paramContext) + "].from."
					+ AWLAttribute.MARKETING_NAME.getSel(paramContext));
			String strCPGId = null;
			String strTypeModel = null;
			String strType = null;
			String strId = null;
			String pre_lang = null;
			StringList slProducthierarchy = new StringList();
			Map<?, ?> mapProduct = null;
			Map<?, ?> mapProductHierarchy = null;
			MapList mlProductHierarchy = null;
			MapList mlProduct = BusinessUtil.getInfo(paramContext, poaIdList, slSelect);
			int iPOASize = poaIdList.size();
			int iPrdHierarchySize = 0;
			pre_lang = AWLPreferences.getPreferedBaseLanguage(paramContext);
			languageArr.add(KEY_BASELANGNAME, pre_lang);
			languageArr.add(KEY_BASELANGVALUE, pre_lang);
			for (byte b = 0; b < iPOASize; b++) {
				jsonObjKeyVariant = Json.createObjectBuilder();
				mapProduct = (Map<?, ?>) mlProduct.get(b);
				strCPGId = (String) mapProduct.get("to[" + AWLRel.ASSOCIATED_POA.get(paramContext) + "].from.id");
				if (!slProducthierarchy.contains(strCPGId)) {
					slProducthierarchy.add(strCPGId);
					String physicalId = FrameworkUtil.getPIDfromOID(paramContext,strCPGId);
					jsonObjKeyVariant.add(DomainConstants.SELECT_PHYSICAL_ID, physicalId);
					jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strCPGId);
					jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE,
							(String) mapProduct.get("to[" + AWLRel.ASSOCIATED_POA.get(paramContext) + "].from.name"));
					jsonObjKeyVariant.add(DomainConstants.SELECT_NAME,
							(String) mapProduct.get("to[" + AWLRel.ASSOCIATED_POA.get(paramContext) + "].from."
									+ AWLAttribute.MARKETING_NAME.getSel(paramContext)));
					jsonArrHier.add(jsonObjKeyVariant);
				}
				mlProductHierarchy = (new CPGProduct(strCPGId)).getProductHierarchy(paramContext);
				strTypeModel = AWLType.MODEL.get(paramContext);
				iPrdHierarchySize = mlProductHierarchy.size();
				for (int i = 0; i < iPrdHierarchySize; i++) {
					jsonObjKeyVariantChild = Json.createObjectBuilder();
					mapProductHierarchy = (Map<?, ?>) mlProductHierarchy.get(i);
					strType = (String) mapProductHierarchy.get(DomainConstants.SELECT_TYPE);
					strId = (String) mapProductHierarchy.get(DomainConstants.SELECT_ID);

					if (!strType.equalsIgnoreCase(strTypeModel) && !slProducthierarchy.contains(strId)) {
						String physicalId = FrameworkUtil.getPIDfromOID(paramContext,strId);
						logger.log(Level.INFO, AWLUtil.strcat(KEY_SHOWBRANDTYPE, ShowBrandType));
						if (!ShowBrandType && "Brand".equals(strType)) {
							continue;
						}
						slProducthierarchy.add(strId);
						jsonObjKeyVariantChild.add(DomainConstants.SELECT_PHYSICAL_ID, physicalId);
						jsonObjKeyVariantChild.add(DomainConstants.SELECT_ID, strId);
						jsonObjKeyVariantChild.add(PGWidgetConstants.KEY_VALUE,
								(String) mapProductHierarchy.get(DomainConstants.SELECT_NAME));
						jsonObjKeyVariantChild.add(DomainConstants.SELECT_NAME,
								(String) mapProductHierarchy.get(AWLAttribute.MARKETING_NAME.getSel(paramContext)));
						jsonArrHier.add(jsonObjKeyVariantChild);
					}
				}

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}

		strOutput.add(KEY_PLACEOFORIGIN, jsonArrHier);
		strOutput.add(KEY_BASELANG, languageArr);

		POAHierachyData.add(KEY_POAHIERARCHYDATA, strOutput);

		return POAHierachyData.build().toString();
	}

	/***
	 * This method is called to create Master Copy Element
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static String createMasterCopyElement(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String strOutput = null;
		JsonArray jsonArr = Json.createArrayBuilder().build();
		try {
			String strMCId = null;
			String strPOAId = null;
			HashMap<String, Object> mapFinalInfo = new HashMap<>();
			Map<Object, Object> attrMap = new HashMap<>();
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);

			String strMCType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
			String strDisplayName = jsonInputData.getString(KEY_MARKETINGNAME);
			String strCopyText = jsonInputData.getString(KEY_MASTERCOPYTEXT);
			String strTranslate = jsonInputData.getString(KEY_TRANSLATE);
			String strInline = jsonInputData.getString(KEY_INLINETRANSLATE);
			String strValidityDate = jsonInputData.getString(KEY_VALIDITYDATE);
			String strInstructions = jsonInputData.getString(KEY_INSTRUCTIONS);
			String strReferenceNo = jsonInputData.getString(KEY_REFERENCENUMBER);
			String strplaceOfOrigin = jsonInputData.getString(KEY_PLACEOFORIGIN);
			String strMasterLang = jsonInputData.getString(KEY_MASTERLANG);
			String strPGSubCopyType =
					 jsonInputData.getString(EditCLConstants.KEY_PGSUBCOPYTYPE);
			AWLPreferences.setPreferedBaseLanguage(context, strMasterLang);
			List<String> placeOfOriginIDs = FrameworkUtil.split(strplaceOfOrigin, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			Map<String, String> copyElementData = new HashMap<String, String>();
			copyElementData.put(DomainConstants.SELECT_TYPE, strMCType);
			String[] strDisplayNameLinesArr = strDisplayName.split("(\r\n|\r|\n)", -1);
			StringBuilder sbDisplayName = new StringBuilder();
			for (String strDisplayNameVal : strDisplayNameLinesArr) {
				if (BusinessUtil.isNotNullOrEmpty(strDisplayNameVal)) {
					sbDisplayName.append(strDisplayNameVal).append(" ");
				}
			}
			strDisplayName = sbDisplayName.toString();
			strDisplayName = strDisplayName.trim();
			copyElementData.put(AWLAttribute.MARKETING_NAME.get(context), strDisplayName);
			copyElementData.put(AWLAttribute.DISPLAY_TEXT.get(context), strCopyText);
			copyElementData.put(AWLAttribute.TRANSLATE.get(context), strTranslate);
			copyElementData.put(AWLAttribute.INLINE_TRANSLATION.get(context), strInline);
			copyElementData.put(AWLAttribute.BUILD_LIST.get(context), DomainConstants.EMPTY_STRING);
			attrMap.put(RTAUtilConstants.ATTR_VALIDITY_DATE, strValidityDate);
			attrMap.put(RTAUtilConstants.ATTR_INSTRUCTIONS, strInstructions);
			attrMap.put(RTAUtilConstants.ATTR_REFERENCE_NO, strReferenceNo);
			attrMap.put(EditCLConstants.ATTRIBUTE_PGSUBCOPYTYPE, strPGSubCopyType);
			copyElementData.put(KEY_LISTSEP, DomainConstants.EMPTY_STRING);
			copyElementData.put(KEY_LISTSEQ, DomainConstants.EMPTY_STRING);
			copyElementData.put(KEY_LISTITEM, DomainConstants.EMPTY_STRING);
			DomainObject placeOfOriginOBJ = BusinessUtil.isNotNullOrEmpty(placeOfOriginIDs)
					? new DomainObject(placeOfOriginIDs.get(0))
					: null;
			// Create Master Copy Object
			ArtworkMaster am = ArtworkMaster.createMasterCopyElement(context, strMCType, copyElementData,
					placeOfOriginOBJ, new ArrayList<Country>());
			if (BusinessUtil.isNotNullOrEmpty(placeOfOriginIDs)) {
				placeOfOriginIDs.remove(0);
				for (String placeOfOriginID : placeOfOriginIDs) {
					DomainRelationship rel = am.connectFrom(context, AWLRel.ARTWORK_MASTER,
							new DomainObject(placeOfOriginID));
					rel.setAttributeValue(context, AWLAttribute.PLACE_OF_ORIGIN.get(context), AWLConstants.RANGE_YES);
				}
			}
			// Connect MC to POAs and create LCs
			am.setAttributeValues(context, attrMap);
			strMCId = am.getObjectId();
			
			//RTA DS - CW 22x-04 - Added for ALM-47882 - Start
			HashMap<String, Object> hmProgramMap = new HashMap<>();
			hmProgramMap.put("MCId", strMCId);
			hmProgramMap.put("MCSubCopyType", strPGSubCopyType);
			
			String[] methodargs = JPO.packArgs(hmProgramMap);
			JPO.invoke(context, "pgRTACopyElementUtil", null, "setSubCopyTypeonMCFromWidgets", methodargs, void.class);			
			//RTA DS - CW 22x-04 - Added for ALM-47882 - End
			
			POA poa = null;
			int iPOASize = poaIdList.size();
			for (int i = 0; i < iPOASize; i++) {
				strPOAId = poaIdList.get(i);
				poa = new POA(strPOAId);
				StringList poaLanguages = poa.getLanguageNames(context);
				poa.addArtworkMaster(context, am);
				poa.addLocalCopiesToPOA(context, am, poaLanguages);
			}
			// Get the details of newly created Master Copy and connected LCs
			mapFinalInfo = getFinalMCLCDetails(context, poaIdList, strMCId);
			// Convert the Map to JSON Format
			jsonArr = convertMaptoJSONFormat(context, mapFinalInfo);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonArr.toString();
	}

	/***
	 * This method is called to fetch regions of Copy Elements and POA
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String getRegions(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjRegion = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);

			StringList slCountryIds = new StringList();
			JsonArrayBuilder jsonArrHier = Json.createArrayBuilder();

			MapList mlPOAs = BusinessUtil.getInfoList(context, poaIdList,
					AWLUtil.strcat("from[", AWLRel.POA_COUNTRY.get(context), "].to.id"));
			Map<?, ?> mapPOA = null;
			int iPOAListSize = mlPOAs.size();
			for (int iPOA = 0; iPOA < iPOAListSize; iPOA++) {
				mapPOA = (HashMap<?, ?>) mlPOAs.get(iPOA);
				slCountryIds = (StringList) mapPOA
						.get(AWLUtil.strcat("from[", AWLRel.POA_COUNTRY.get(context), "].to.id"));
			}

			String strApplicableCountryIds = STR_ALLCOUNTRIES;
			String strSelectedCountryIds = slCountryIds.join(PGWidgetConstants.KEY_COMMA_SEPARATOR);

			List<Region> regions = getTopRegions(context);
			getSubRegions(context, regions, strApplicableCountryIds, strSelectedCountryIds, jsonArrHier);
			jsonObjRegion.add(KEY_REGIONS, jsonArrHier);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonObjRegion.build().toString();
	}

	/***
	 * This method is called to fetch sub regions of Copy Elements and POA
	 * 
	 * @param context
	 * @param regions
	 * @param strApplicableCountryIds
	 * @param strSelectedCountryIds
	 * @param jsonArrHier
	 * @throws Exception
	 */
	public static void getSubRegions(Context context, List<Region> regions, String strApplicableCountryIds,
			String strSelectedCountryIds, JsonArrayBuilder jsonArrHier) throws Exception {
		String strRegionId = null;
		String strRegionCurrent = null;
		JsonObjectBuilder jsonObjKeyVariant = null;
		List<Country> listCountries = null;
		int iSubRegSize = 0;
		for (Region subregion : regions) {
			jsonObjKeyVariant = Json.createObjectBuilder();
			strRegionId = subregion.getObjectId();
			strRegionCurrent = BusinessUtil.getInfo(context, strRegionId, DomainConstants.SELECT_CURRENT);
			if (ProductLineConstants.STATE_INACTIVE.equals(strRegionCurrent)) {
				continue;
			}
			jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, subregion.getName());
			jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strRegionId);
			iSubRegSize = subregion.getSubRegions(context).size();
			if (iSubRegSize <= 0) {
				jsonObjKeyVariant.add(KEY_LEAF, AWLConstants.RANGE_TRUE);
			} else {
				List<Region> listSubRegions = subregion.getSubRegions(context);
				getSubRegions(context, listSubRegions, strApplicableCountryIds, strSelectedCountryIds, jsonArrHier);
			}
			listCountries = subregion.getCountries(context, true);
			getCountries(context, listCountries, strApplicableCountryIds, strSelectedCountryIds, jsonArrHier,
					jsonObjKeyVariant);
		}
	}

	/***
	 * This method is called to fetch countries of Copy Elements and POA
	 * 
	 * @param context
	 * @param listCountries
	 * @param strApplicableCountryIds
	 * @param strSelectedCountryIds
	 * @param jsonArrHier
	 * @param jsonObjKeyVariant
	 */
	public static void getCountries(Context context, List<Country> listCountries, String strApplicableCountryIds,
			String strSelectedCountryIds, JsonArrayBuilder jsonArrHier, JsonObjectBuilder jsonObjKeyVariant) {
		boolean isApplicable = false;
		for (Country country : listCountries) {
			String currentCountryId = country.getObjectId();
			if ((strApplicableCountryIds != null) && ((strApplicableCountryIds.equalsIgnoreCase(STR_ALLCOUNTRIES))
					|| (strApplicableCountryIds.indexOf(currentCountryId) >= 0))) {
				isApplicable = true;
				if ((strSelectedCountryIds != null) && (strSelectedCountryIds.indexOf(currentCountryId) >= 0)) {
					jsonObjKeyVariant.add(KEY_SELECTED, AWLConstants.RANGE_TRUE);
					jsonObjKeyVariant.add(KEY_EXPANDED, AWLConstants.RANGE_TRUE);
					break;
				}
			}
		}
		if (isApplicable) {
			jsonArrHier.add(jsonObjKeyVariant);
		}
	}

	/***
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Region> getTopRegions(Context context) throws Exception {
		ArrayList localArrayList = new ArrayList<>();
		List<Region> regions = null;
		try {
			String str1 = Company.getHostCompany(context);
			StringList localStringList1 = new StringList(DomainConstants.SELECT_ID);
			localStringList1.add(DomainConstants.SELECT_NAME);
			localStringList1.add(DomainConstants.SELECT_TYPE);
			localStringList1.add("to[" + DomainConstants.RELATIONSHIP_SUB_REGION + "]");
			StringList localStringList3;
			String strId = null;
			String strType = null;
			String strRelSubRegion = null;
			Iterator<?> localIterator;
			Object localObject;
			Map<?, ?> localMap = null;
			if ((str1 != null) && (str1.length() > 0)) {
				MapList localMapList = DomainObject.findObjects(context, 
						DomainConstants.TYPE_REGION, //typePattern
						PGWidgetConstants.VAULT_ESERVICE_PRODUCTION, // vaultPattern
						null, // where clause
						localStringList1); // objectSelects
				localMapList.addSortKey(DomainConstants.SELECT_NAME, PGWidgetConstants.STRING_CAPITAL_TRUE, "string");
				localMapList.sort();

				String str4 = EnoviaResourceBundle.getProperty(context,
						"emxCPD.RegionCountryUI.DisplayTopLevelRegions");
				StringList localStringList2 = FrameworkUtil.split(str4, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				localStringList3 = new StringList(localStringList2.size());

				for (localIterator = localStringList2.iterator(); localIterator.hasNext();) {
					localObject = localIterator.next();
					localStringList3.add(PropertyUtil.getSchemaProperty(context, (String) localObject));
				}
				for (localIterator = localMapList.iterator(); localIterator.hasNext();) {
					localObject = localIterator.next();
					localMap = (Map<?, ?>) localObject;
					strId = (String) localMap.get(DomainConstants.SELECT_ID);
					strType = (String) localMap.get(DomainConstants.SELECT_TYPE);
					strRelSubRegion = (String) localMap.get("to[" + DomainConstants.RELATIONSHIP_SUB_REGION + "]");
					localMap.remove("to[" + DomainConstants.RELATIONSHIP_SUB_REGION + "]");
					if (strRelSubRegion.equalsIgnoreCase(AWLConstants.RANGE_FALSE)) {
						if (localStringList3.contains(strType)) {
							Region localRegion = CPDCache.getRegion(strId);
							localRegion.setName((String) localMap.get(DomainConstants.SELECT_NAME));
							localArrayList.add(localRegion);
						}
					}
				}
			}
			regions = (List<Region>) localArrayList;
		} catch (Exception e) {
			regions = CPDCache.getTopRegions(context);
			throw e;
		}
		return regions;
	}

	/***
	 * This method is called to add Master Copy Elements to POA
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String addMasterCopy(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutput = null;
		JsonArray jsonArr = Json.createArrayBuilder().build();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);
			String strMCIds = jsonInputData.getString(STR_MCIDS);
			StringList mcIdList = FrameworkUtil.split(strMCIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			StringList poaLanguages = new StringList();
			String strPOAId = null;
			String strMCId = null;
			HashMap<String, Object> mapFinalInfo = new HashMap<>();
			HashMap<String, Object> mapMCLCInfo = new HashMap<>();
			POA poa = null;
			ArtworkMaster am = null;
			int iMCsSize = mcIdList.size();
			int iPOAsSize = poaIdList.size();
			for (int i = 0; i < iMCsSize; i++) {
				strMCId = mcIdList.get(i);
				if (BusinessUtil.isNotNullOrEmpty(strMCId)) {
					am = new ArtworkMaster(strMCId);
					for (int j = 0; j < iPOAsSize; j++) {
						strPOAId = poaIdList.get(j);
						poa = new POA(strPOAId);
						poaLanguages = poa.getLanguageNames(context);
						poa.addArtworkMaster(context, am);
						poa.addLocalCopiesToPOA(context, am, poaLanguages);
					}
					mapFinalInfo = getFinalMCLCDetails(context, poaIdList, strMCId);
					mapMCLCInfo.putAll(mapFinalInfo);
				}
			}
			jsonArr = convertMaptoJSONFormat(context, mapMCLCInfo);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonArr.toString();
	}

	/***
	 * This method returns Copy List Master Copies added to POA
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getCopyLisMasterCopies(Context context, String paramString) throws FrameworkException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutput = null;
		boolean isContextPushed = false;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strCopyListId = jsonInputData.getString(STR_COPYLIST_ID);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			String temp = strCopyListId;
			String[] copyListIdArr = temp.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
			StringList strCopyListIdSL = BusinessUtil.toStringList(copyListIdArr);
			validateCLToPOA(context, strCopyListIdSL, strPOAIds);
			StringList slObjSelects = new StringList(8);
			slObjSelects.add(DomainConstants.SELECT_ID);
			slObjSelects.add(DomainConstants.SELECT_NAME);
			slObjSelects.add(DomainConstants.SELECT_CURRENT);
			slObjSelects.add(DomainConstants.SELECT_REVISION);
			slObjSelects.add(AWLAttribute.TRANSLATE.getSel(context));
			slObjSelects.add(AWLAttribute.INLINE_TRANSLATION.getSel(context));
			slObjSelects.add(AWLAttribute.MARKETING_NAME.getSel(context));
			slObjSelects.add("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");
			slObjSelects.add(RTAUtilConstants.SELECT_PGRTAMCPRODUCTIONPLANT);
			slObjSelects.add(RTAUtilConstants.SELECT_PGRTAMCPRIMARYPRODPLANT);
			StringList slRelSelects = new StringList(2);
			slRelSelects.add(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
			String strBaseCopyTextOrImage = null;
			String strMCId = null;
			String strBaseId = null;
			String strFlag = null;
			MapList mlMasterCopyList = null;
			MapList mlReturn = new MapList();
			Boolean isFrom = false;
			Boolean isTo = true;
			DomainObject doCL = null;
			// RTA users do not have access on DCM Data so need to push context
			ContextUtil.pushContext(context);
			isContextPushed = true;
			Pattern typePattern = new Pattern(AWLType.MASTER_COPY_ELEMENT.get(context));
			typePattern.addPattern(AWLType.MASTER_ARTWORK_ELEMENT.get(context));
			if (BusinessUtil.isNotNullOrEmpty(strCopyListId)) {
				doCL = DomainObject.newInstance(context, strCopyListId);
				mlMasterCopyList = doCL.getRelatedObjects(context, // context,
						AWLRel.COPY_LIST_ARTWORK_MASTER.get(context), // relationshipPattern,
						typePattern.getPattern(), // typepattern
						slObjSelects, // objectSelects
						slRelSelects, // relationshipSelects,
						isFrom, // getFrom
						isTo, // getTo,
						(short) 1, // recurseToLevel,
						null, // objectWhere,
						null, // relWhere
						0); // limit
			}
			for (Map<Object, Object> mMasterCopy : (List<Map>) mlMasterCopyList) {
				strMCId = (String) mMasterCopy.get(DomainConstants.SELECT_ID);
				strBaseId = (new ArtworkMaster(strMCId)).getBaseArtworkElement(context).getObjectId(context);
				strFlag = (String) mMasterCopy
						.get("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");
				if (strFlag.equalsIgnoreCase(AWLConstants.RANGE_FALSE)) {
					strBaseCopyTextOrImage = BusinessUtil.getInfo(context, strBaseId,
							"attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]");
					mMasterCopy.put(KEY_MASTERCOPYTEXT, strBaseCopyTextOrImage);
				} else {
					String[] arrayOfString = new String[2];
					arrayOfString[0] = strBaseId;
					arrayOfString[1] = strMCId;
					strBaseCopyTextOrImage = JPO.invoke(context, "AWLGraphicsElementUI", null,
							"getGraphicImageURLforPOAAction", arrayOfString, String.class);
					mMasterCopy.put(KEY_CONTENT, strBaseCopyTextOrImage);
				}
				mMasterCopy.put(KEY_SEQ, mMasterCopy.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context)));
				// mMasterCopy.put(KEY_MASTERCOPYTEXT, strBaseCopyTextOrImage);
				mMasterCopy.put(KEY_MARKETINGNAME, mMasterCopy.get(AWLAttribute.MARKETING_NAME.getSel(context)));
				mMasterCopy.put(KEY_TRANSLATE, mMasterCopy.get(AWLAttribute.TRANSLATE.getSel(context)));
				mMasterCopy.put(KEY_INLINETRANSLATE, mMasterCopy.get(AWLAttribute.INLINE_TRANSLATION.getSel(context)));
				mMasterCopy.put(KEY_PGRTAMCPRODUCTIONPLANT,
						mMasterCopy.get(RTAUtilConstants.SELECT_PGRTAMCPRODUCTIONPLANT));
				mMasterCopy.put(KEY_PGRTAMCPRIMARYPRODPLANT,
						mMasterCopy.get(RTAUtilConstants.SELECT_PGRTAMCPRIMARYPRODPLANT));

				// If MCE exists, get Claims data related to it
				if (BusinessUtil.isNotNullOrEmpty(strMCId)) {
					StringList slClaimsObjSel = new StringList(15);
					slClaimsObjSel.add(DomainConstants.SELECT_NAME);
					slClaimsObjSel.add(DomainConstants.SELECT_DESCRIPTION);
					slClaimsObjSel.add(DomainConstants.SELECT_OWNER);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGSIZE);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGVALUECLAIM);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGPRODUCTFORM);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGCOUNT);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGVARIANT);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGLOTION);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGCLAIMBRAND);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_CLAIM_TYPE);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGCLAIMSUBBRAND);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGCLAIMINTENDEDMARKETS);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGPACKCOMPONENTTYPE);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGPANELLOCATION);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGDISCLAIMERPANELLOCATION);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGEXECUTIONTYPE);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGBUSINESSAREA);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGPRODCATEPLATFORM);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGFRANCHISEPLATFORM);
					DomainConstants.MULTI_VALUE_LIST.add(RTAUtilConstants.SELECT_PGEXPIRATIONDATE);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGCLAIMBRAND);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_CLAIM_TYPE);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGCLAIMSUBBRAND);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGCLAIMINTENDEDMARKETS);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGPACKCOMPONENTTYPE);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGPANELLOCATION);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGDISCLAIMERPANELLOCATION);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGEXECUTIONTYPE);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGBUSINESSAREA);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGPRODCATEPLATFORM);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGFRANCHISEPLATFORM);
					slClaimsObjSel.add(RTAUtilConstants.SELECT_PGEXPIRATIONDATE);

					DomainObject doMC = DomainObject.newInstance(context, strMCId); // Domain Obj of MCE's that are
																					// related to CL passed
					String sClaimRelPattern = RTAUtilConstants.RELATIONSHIP_PGMASTERCOPYCLAIM;
					String sTypePattern = PGWidgetConstants.CONSTANT_STRING_STAR;
					boolean sMasterIsFrom = true, sMasterIsTo = true;
					MapList mlClaimsMasterCopyList = new MapList();
					mlClaimsMasterCopyList = doMC.getRelatedObjects(context, // context,
							sClaimRelPattern, // relationshipPattern,
							sTypePattern, // typepattern
							slClaimsObjSel, // objectSelects
							null, // relationshipSelects,
							sMasterIsFrom, // getFrom
							sMasterIsTo, // getTo,
							(short) 1, // recurseToLevel,
							null, // objectWhere,
							null, // relWhere
							0); // limit
					if (mlClaimsMasterCopyList.size() > 0) {
						Map<?, ?> mClaimsMasterCopy = (Map<?, ?>) mlClaimsMasterCopyList.get(0); // Cast MapList to
																									// List<Map> and
																									// then
																									// use List method
						// Append Claims Data to the MCE Map
						mMasterCopy.put(KEY_CLAIMOBJ, mClaimsMasterCopy.get(DomainConstants.SELECT_NAME));
						mMasterCopy.put(KEY_CLAIMOWNER, mClaimsMasterCopy.get(DomainConstants.SELECT_OWNER));
						// mMasterCopy.put(KEY_CLAIMTYPE,
						// mClaimsMasterCopy.get(DomainConstants.SELECT_TYPE));
						mMasterCopy.put(KEY_CLAIMTYPE, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_CLAIM_TYPE));
						mMasterCopy.put(KEY_CLAIMDESCP, mClaimsMasterCopy.get(DomainConstants.SELECT_DESCRIPTION));
						mMasterCopy.put(KEY_RELPART, DomainConstants.EMPTY_STRING); // ---##--- MORE INFO NEEDED ---##---
						mMasterCopy.put(KEY_SIZE, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGSIZE));
						mMasterCopy.put(KEY_PKGCOMPTYPE,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGPACKCOMPONENTTYPE));
						mMasterCopy.put(KEY_CLAIMPANELLOC,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGPANELLOCATION));
						mMasterCopy.put(KEY_CLAIMDISPANELLOC,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGDISCLAIMERPANELLOCATION));
						mMasterCopy.put(KEY_VALCLAIM, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGVALUECLAIM));
						mMasterCopy.put(KEY_EXECTYPE, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGEXECUTIONTYPE));
						mMasterCopy.put(KEY_BRAND, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGCLAIMBRAND));
						mMasterCopy.put(KEY_SUBBRAND, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGCLAIMSUBBRAND));
						mMasterCopy.put(KEY_PRODUCTFORM, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGPRODUCTFORM));
						mMasterCopy.put(KEY_COUNT, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGCOUNT));
						mMasterCopy.put(KEY_VARIANT, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGVARIANT));
						mMasterCopy.put(KEY_LOTIONSCENT, mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGLOTION));
						mMasterCopy.put(KEY_INTENDED_MARKET,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGCLAIMINTENDEDMARKETS));
						mMasterCopy.put(KEY_BUSINESSAREA,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGBUSINESSAREA));
						mMasterCopy.put(KEY_PRODCATEPLATFORM,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGPRODCATEPLATFORM));
						mMasterCopy.put(KEY_FRANCHISEPLATFORM,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGFRANCHISEPLATFORM));
						mMasterCopy.put(KEY_PGEXPIRATIONDATE,
								mClaimsMasterCopy.get(RTAUtilConstants.SELECT_PGEXPIRATIONDATE));
					}
				}
				mMasterCopy.remove(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
				mMasterCopy.remove(AWLAttribute.MARKETING_NAME.getSel(context));
				mMasterCopy.remove(AWLAttribute.TRANSLATE.getSel(context));
				mMasterCopy.remove(AWLAttribute.INLINE_TRANSLATION.getSel(context));
				mMasterCopy.remove("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");
				String strAttrName = DomainConstants.EMPTY_STRING;
				String strAttrValue = DomainConstants.EMPTY_STRING;
				for (Map.Entry<?, ?> entry : mMasterCopy.entrySet()) {
					strAttrName = (String) entry.getKey();
					strAttrValue = PGWidgetUtil.extractMultiValueSelect(mMasterCopy, strAttrName);
					mMasterCopy.put(strAttrName, PGWidgetUtil.checkNullValueforString(strAttrValue));
				}
				mlReturn.add(mMasterCopy);

			}
			mlReturn.sort(KEY_SEQ, PGWidgetConstants.STRING_CAPITAL_TRUE, "integer");
			strOutput = convertMapListToJSON(context, mlReturn);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		} finally {
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGCLAIMBRAND);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGCLAIMSUBBRAND);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGCLAIMINTENDEDMARKETS);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGPACKCOMPONENTTYPE);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGPANELLOCATION);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGDISCLAIMERPANELLOCATION);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGEXECUTIONTYPE);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGBUSINESSAREA);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGPRODCATEPLATFORM);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGFRANCHISEPLATFORM);
			DomainConstants.MULTI_VALUE_LIST.remove(RTAUtilConstants.SELECT_PGEXPIRATIONDATE);
			if (isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
		return strOutput;
	}

	/***
	 * This method is called to check if the selected CL the country, language,
	 * Claim request relationship and Artwork Usage matches with the POA selected
	 * 
	 * @param context
	 * @param strCopyListId
	 * @param strPOAIds
	 * @throws Exception
	 */
	public static void validateCLToPOA(Context context, StringList strCopyListId, String strPOAIds) throws Exception {
		boolean isContextPushed = false;
		try {
			// convert slCLToBeCopied to string list

			String strCLId = (String) strCopyListId.get(0);
			StringList slCLSelect = new StringList();
			String SELECT_ARTWORKUSAGE = "attribute[" + AWLAttribute.ARTWORK_USAGE.get(context) + "]";
			String SELECT_CL_LOCALLANGUAGE = "from[" + AWLRel.COPY_LIST_LOCAL_LANGUAGE.get(context) + "].to."
					+ DomainObject.SELECT_ID;
			String SELECT_CL_COUNTRY = "from[" + AWLRel.COPY_LIST_COUNTRY.get(context) + "].to."
					+ DomainObject.SELECT_ID;
			// RTA 18x.6 Aug CW added for ALM-44172 Starts
			String REL_PGCOPYLIST = RTAUtilConstants.REL_PG_COPYLIST_CLAIM_REQUEST;
			String SELECT_CL_CLAIMREQ = "to[" + REL_PGCOPYLIST + "].from." + DomainObject.SELECT_ID;

			slCLSelect.add(SELECT_ARTWORKUSAGE);
			slCLSelect.add(SELECT_CL_LOCALLANGUAGE);
			slCLSelect.add(SELECT_CL_COUNTRY);
			slCLSelect.add(SELECT_CL_CLAIMREQ);
			slCLSelect.add(DomainObject.SELECT_ATTRIBUTE_TITLE);
			// RTA users do not have access on DCM Data so need to push context
			ContextUtil.pushContext(context);
			isContextPushed = true;
			String strCRId = null;
			Map<?, ?> mCLDetails = BusinessUtil.getInfoList(context, strCLId, slCLSelect);
			if (mCLDetails.containsKey("to[" + REL_PGCOPYLIST + "].from." + DomainObject.SELECT_ID)) {
				StringList slCRId = (StringList) mCLDetails.get(SELECT_CL_CLAIMREQ);
				strCRId = slCRId.get(0);
			}
			boolean hasClaimReq = false;
			if (BusinessUtil.isNotNullOrEmpty(strCRId)) {
				hasClaimReq = true;
			}
			// RTA 18x.6 Aug CW added for ALM-44172 Ends
			StringList slCLTitle = (StringList) mCLDetails.get(DomainObject.SELECT_ATTRIBUTE_TITLE);
			String strCLTitle = (String) slCLTitle.get(0);
			StringList slCLArtworkUsage = (StringList) mCLDetails.get(SELECT_ARTWORKUSAGE);
			StringList slCLLocalLanguage = (StringList) mCLDetails.get(SELECT_CL_LOCALLANGUAGE);
			StringList slCLCountry = (StringList) mCLDetails.get(SELECT_CL_COUNTRY);
			StringList slPOAs = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_PIPE_SEPARATOR);
			StringList slPOASelect = new StringList();
			String REL_POA_LOCAL_LANGUAGE = AWLRel.POA_LOCAL_LANGUAGE.get(context);
			String REL_POA_COPY_LIST = AWLRel.POA_COPY_LIST.get(context);
			String SELECT_POA_COPY_LIST = "from[" + REL_POA_COPY_LIST + "].to." + DomainObject.SELECT_ID;
			String SELECT_POA_LOCALLANGUAGE = "from[" + REL_POA_LOCAL_LANGUAGE + "].to." + DomainObject.SELECT_ID;
			String SELECT_POA_COUNTRY = "from[" + AWLRel.POA_COUNTRY.get(context) + "].to." + DomainObject.SELECT_ID;
			slPOASelect.add(SELECT_POA_COPY_LIST);
			slPOASelect.add(SELECT_ARTWORKUSAGE);
			slPOASelect.add(SELECT_POA_LOCALLANGUAGE);
			slPOASelect.add(SELECT_POA_COUNTRY);
			slPOASelect.add(DomainObject.SELECT_NAME);
			slPOASelect.add(DomainObject.SELECT_ID);
			String strIssuePOAName = DomainObject.EMPTY_STRING;
			MapList mlPOADetails = BusinessUtil.getInfoList(context, slPOAs, slPOASelect);
			boolean bIsMatch = true;
			boolean bIsAUMatch = true;
			boolean bIsCountryMatch = false;
			boolean bIsLangMatch = false;
			Map<?, ?> mPOA = null;
			StringList slPOAName = null;
			StringList slPOAArtworkUsage = null;
			StringList slPOALocalLanguage = null;
			StringList slPOACountry = null;
			StringList slIssuePOAName = new StringList();
			for (int i = 0; i < mlPOADetails.size(); i++) {
				mPOA = (Map<?, ?>) mlPOADetails.get(i);
				slPOAName = (StringList) mPOA.get(DomainObject.SELECT_NAME);
				strIssuePOAName = (String) slPOAName.get(0);
				slPOAArtworkUsage = (StringList) mPOA.get(SELECT_ARTWORKUSAGE);
				for (String strPOAArtworkUsage : slPOAArtworkUsage)
					if (!slCLArtworkUsage.contains(strPOAArtworkUsage)) {
						bIsAUMatch = false;
					} else {
						bIsAUMatch = true;
						slPOACountry = (StringList) mPOA.get(SELECT_POA_COUNTRY);
						for (String strPOACountry : slPOACountry) {
							if (slCLCountry.contains(strPOACountry)) {
								bIsCountryMatch = true;
								break;
							}else {
								bIsCountryMatch = false;
							}
						}
						slPOALocalLanguage = (StringList) mPOA.get(SELECT_POA_LOCALLANGUAGE);
						for (String strPOALocalLanguage : slPOALocalLanguage) {
							// RTA 18x.6 Aug CW added for ALM-44172 Starts
							if (slCLLocalLanguage.contains(strPOALocalLanguage) || hasClaimReq)
							// RTA 18x.6 Aug CW added for ALM-44172 Ends
							{
								bIsLangMatch = true;
								break;
							}else {
								bIsLangMatch = false;
							}
						}
					}
				if (!bIsAUMatch || !bIsCountryMatch || !bIsLangMatch) {
					bIsMatch = false;
					slIssuePOAName.add(strIssuePOAName);
				}
			}
			String strIssuePOANames = String.join(PGWidgetConstants.KEY_COMMA_SEPARATOR, slIssuePOAName);
			if (!bIsMatch) {
				String strMessage = (EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
						context.getLocale(), "emxAWL.Alert.SelectedCLNotApplicableForPOA")).concat(
								strIssuePOANames.concat(EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
										context.getLocale(), "emxAWL.Alert.CLNotApplicableInformation")));
				throw new Exception(strMessage);
			} else {
				StringBuffer strSubHeader = new StringBuffer();
				strSubHeader.append(strCLTitle);

				// check this code and convert it

				/*
				 * %> <script language="javascript" type="text/javaScript"> var strUrl =
				 * "<%=href%>"; <!-- 22x Upgrade Changes for URL encoding to avoid error due to
				 * special characters --> getTopWindow().location.href=encodeURI(strUrl);
				 * </script> <%
				 */
			}
		} catch (Exception e) {
			// session.putValue("error.message",e.getMessage());
			throw e;

			// RTA 18x.6 Aug CW added for ALM-44172 Starts
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
	}

	/***
	 * This method converts mapList to JSON
	 * 
	 * @param context
	 * @param mlReturn
	 * @return
	 */
	public static String convertMapListToJSON(Context context, MapList mlReturn) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrData = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		String strKey = null;
		String strValue = DomainConstants.EMPTY_STRING;
		Map<?, ?> mapData = null;
		try {
			for (int i = 0; i < mlReturn.size(); i++) {
				mapData = (Map<?, ?>) mlReturn.get(i);
				jsonObjKeyVariant = Json.createObjectBuilder();
				for (Entry<?, ?> entryMCLC : mapData.entrySet()) {
					strKey = (String) entryMCLC.getKey();
					strValue = (String) entryMCLC.getValue();
					if (BusinessUtil.isNotNullOrEmpty(strValue)) {
						jsonObjKeyVariant.add(strKey, strValue);
					} else {
						jsonObjKeyVariant.add(strKey, DomainConstants.EMPTY_STRING);
					}
				}
				jsonArrData.add(jsonObjKeyVariant.build());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonArrData.build().toString();
	}

	/***
	 * This method converts Map to JSON Array format
	 * 
	 * @param context
	 * @param mapFinalInfo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static JsonArrayBuilder convertMaptoJSONArrFormat(Context context, Map<String, HashMap> mapFinalInfo)
			throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrPOAData = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonArrayBuilder jsonArrHier = null;
		String strKey = null;
		String strValue = DomainConstants.EMPTY_STRING;
		Map<String, String> mapData = null;
		try {
			for (Map.Entry<String, HashMap> entry : mapFinalInfo.entrySet()) {
				jsonArrHier = Json.createArrayBuilder();
				jsonObjKeyVariant = Json.createObjectBuilder();
				strKey = entry.getKey();
				mapData = mapFinalInfo.get(strKey);
				if (strKey.contains(PGWidgetConstants.KEY_UNDERSCORE)) {
					StringList slMCLCHierarchy = FrameworkUtil.split(strKey, PGWidgetConstants.KEY_UNDERSCORE);
					jsonArrHier.add(slMCLCHierarchy.get(0));
					jsonArrHier.add(slMCLCHierarchy.get(1));
				} else {
					jsonArrHier.add(strKey);
				}
				jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
				for (Map.Entry<String, String> entryMCLC : mapData.entrySet()) {
					strValue = entryMCLC.getValue();
					if (BusinessUtil.isNotNullOrEmpty(strValue)) {
						jsonObjKeyVariant.add(entryMCLC.getKey(), strValue);
					} else {
						jsonObjKeyVariant.add(entryMCLC.getKey(), DomainConstants.EMPTY_STRING);
					}
				}
				jsonArrPOAData.add(jsonObjKeyVariant.build());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		return jsonArrPOAData;
	}

	/***
	 * This method is called to connect CL Master Copies to POA
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String connecttMasterCopiesFromCL(Context context, String paramString) throws Exception {

		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);

		String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
		StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);
		String strMCIds = jsonInputData.getString(STR_MCIDS);
		StringList mcIdList = FrameworkUtil.split(strMCIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		String strCopyListId = jsonInputData.getString(STR_COPYLIST_ID);

		StringList slObjSelects = new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		StringList slRelSelects = new StringList();
		slRelSelects.add(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
		POA objPOA = null;
		MapList mlMasterCopies = null;
		StringList slPOAMC = null;
		ArtworkMaster amMC = null;
		String strSequenceNumber = null;
		MapList mlCopyList = null;
		Boolean isFrom = true;
		Boolean isTo = false;
		for (String strPOAId : poaIdList) {
			objPOA = new POA(strPOAId);
			mlMasterCopies = objPOA.getArtworkMasters(context, StringList.create(DomainConstants.SELECT_ID),
					DomainConstants.EMPTY_STRING);
			slPOAMC = BusinessUtil.toStringList(mlMasterCopies, DomainConstants.SELECT_ID);
			for (String strMasterId : mcIdList) {
				if (!slPOAMC.contains(strMasterId)) {
					amMC = new ArtworkMaster(strMasterId);
					objPOA.addArtworkMasterAndElements(context, amMC);

					mlCopyList = amMC.getRelatedObjects(context, // context,
							AWLRel.COPY_LIST_ARTWORK_MASTER.get(context), // relationshipPattern,
							AWLType.COPY_LIST.get(context), // typePattern
							slObjSelects, // objectSelects
							slRelSelects, // relationshipSelects,
							isFrom, // getFrom
							isTo, // getTo,
							(short) 1, // recurseToLevel,
							(DomainConstants.SELECT_ID).concat("==".concat(strCopyListId)), // objectWhere,
							null, // relWhere
							0); // limit
					for (Map<?, ?> mCL : (List<Map>) mlCopyList) {
						strSequenceNumber = (String) mCL
								.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
						if (BusinessUtil.isNotNullOrEmpty(strSequenceNumber)) {
							objPOA.setSequenceNumber(context, strMasterId, Integer.valueOf(strSequenceNumber));
						}
					}
				}
			}
		}
		return (new JsonHelper()).getJsonString(PGWidgetConstants.KEY_SUCCESS);
	}

	/***
	 * This method is called to create Graphic Element
	 * 
	 * @param context
	 * @param paramString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	public static String createGraphicElement(Context context, String paramString, HttpServletRequest request)
			throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjKeyVariant = Json.createObjectBuilder();
		JsonArrayBuilder jsonObjTemp = Json.createArrayBuilder();
		JsonArrayBuilder jsonGCEObjArr = Json.createArrayBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);

			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);
			Map<String, HashMap> mapFinalInfo = new HashMap<>();
			Map<String, Object> attrMap = new HashMap<>();
			String strPOAId = null;
			String strMCId = null;
			String strMCType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
			String strDisplayName = jsonInputData.getString(KEY_MARKETINGNAME);
			String strDocumentType = jsonInputData.getString(KEY_DOCUMENTTYPE);
			String strFileName = jsonInputData.getString(KEY_FILENAME);
			String strMasterDesc = jsonInputData.getString(DomainConstants.SELECT_DESCRIPTION);
			String strValidityDate = jsonInputData.getString(KEY_VALIDITYDATE);
			String strplaceOfOrigin = jsonInputData.getString(KEY_PLACEOFORIGIN);
			String strInstructions = jsonInputData.getString(KEY_INSTRUCTIONS);
			List<String> placeOfOriginIDs = FrameworkUtil.split(strplaceOfOrigin, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			Map<String, String> imageElementData = new HashMap<String, String>();
			String[] strDisplayNameLinesArr = strDisplayName.split("(\r\n|\r|\n)", -1);
			StringBuilder sbDisplayName = new StringBuilder();
			for (String strDisplayNameVal : strDisplayNameLinesArr) {
				if (BusinessUtil.isNotNullOrEmpty(strDisplayNameVal)) {
					sbDisplayName.append(strDisplayNameVal).append(" ");
				}
			}
			strDisplayName = sbDisplayName.toString();
			strDisplayName = strDisplayName.trim();
			imageElementData.put(DomainConstants.SELECT_TYPE, strMCType);
			imageElementData.put(DomainConstants.DOCUMENT, strDocumentType);
			imageElementData.put(DomainConstants.SELECT_DESCRIPTION, strMasterDesc);
			imageElementData.put(AWLAttribute.MARKETING_NAME.get(context), strDisplayName);
			// imageElementData.put(CommonDocument.SELECT_FILE_NAME, strFileName);
			imageElementData.put(KEY_MCSURL, DomainConstants.EMPTY_STRING);
			attrMap.put(RTAUtilConstants.ATTR_VALIDITY_DATE, strValidityDate);
			attrMap.put(RTAUtilConstants.ATTR_INSTRUCTIONS, strInstructions);
			List<Country> countries = new ArrayList<Country>();
			DomainObject ctxObject = BusinessUtil.isNotNullOrEmpty(placeOfOriginIDs)
					? DomainObject.newInstance(context, placeOfOriginIDs.get(0))
					: null;
			ArtworkMaster dobImagekMaster = ArtworkMaster.createMasterImageElement(context, strMCType, imageElementData,
					ctxObject, countries);
			if (BusinessUtil.isNotNullOrEmpty(placeOfOriginIDs)) {
				placeOfOriginIDs.remove(0);
				for (String placeOfOriginID : placeOfOriginIDs) {
					DomainRelationship rel = dobImagekMaster.connectFrom(context, AWLRel.ARTWORK_MASTER,
							DomainObject.newInstance(context, placeOfOriginID));
					rel.setAttributeValue(context, AWLAttribute.PLACE_OF_ORIGIN.get(context), AWLConstants.RANGE_YES);
				}
			}

			GraphicsElement graphicsElement = (GraphicsElement) dobImagekMaster.getBaseArtworkElement(context);
			String strGraphicDocumentId = graphicsElement.getGraphicDocument(context).getObjectId(context);
			String strGraphicElementId = graphicsElement.getObjectId();

			dobImagekMaster.setAttributeValues(context, attrMap);

			strMCId = dobImagekMaster.getObjectId();
			POA poa = null;
			int iPOASize = poaIdList.size();
			for (int i = 0; i < iPOASize; i++) {
				strPOAId = poaIdList.get(i);
				poa = new POA(strPOAId);
				StringList poaLanguages = poa.getLanguageNames(context);
				poa.addArtworkMaster(context, dobImagekMaster);
				poa.addLocalCopiesToPOA(context, dobImagekMaster, poaLanguages);
			}
			// Get the details of newly created Master Copy and connected LCs
			mapFinalInfo = getFinalMCLCDetails(context, poaIdList, strMCId);
			// Convert the Map to JSON Format
			jsonGCEObjArr = convertMaptoJSONArrFormat(context, mapFinalInfo);
			jsonObjTemp = jsonGCEObjArr;
			JsonArray tempAarr = jsonObjTemp.build();
			String gceID = DomainConstants.EMPTY_STRING;
			for (int i = 0; i < tempAarr.size(); i++) {

				JSONObject jsonObject = new JSONObject(tempAarr.get(i).toString());
				Map<String, Object> response = new ObjectMapper().readValue(jsonObject.toString(), HashMap.class);
				List hierarchyList = new ArrayList();
				hierarchyList = (List) response.get("hierarchy");
				if (hierarchyList.size() == 1) {
					gceID = response.get(PGWidgetConstants.KEY_OBJECTID).toString();
					break;
				}
			}
			// For CheckIn File In object.
			graphicElementFileUpload(context, gceID, strFileName);

			// ============================================================================
			// This code is referred from pg_AWLArtworkElementProcess.jsp
			// For promote the GCE to release state as it gets created
			MqlUtil.mqlCommand(context, "trigger off;", true);

			GraphicsElement graphicElement = new GraphicsElement(strGraphicElementId);
			graphicElement.setState(context, AWLState.RELEASE.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT));

			DomainObject dobjGraphicDocument = DomainObject.newInstance(context, strGraphicDocumentId);
			dobjGraphicDocument.setState(context, AWLState.RELEASE.get(context, AWLPolicy.ARTWORK_GRAPHIC));

			dobImagekMaster.setState(context, AWLState.RELEASE.get(context, AWLPolicy.ARTWORK_ELEMENT));

			MqlUtil.mqlCommand(context, "trigger on;", true);

			// ============================================================================

			jsonObjKeyVariant.add(KEY_GCEInfo, jsonGCEObjArr);
			jsonObjKeyVariant.add(KEY_GCEDocument, strGraphicDocumentId);
			jsonObjKeyVariant.add(KEY_GCEElement, strGraphicElementId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		} finally {
			MqlUtil.mqlCommand(context, "trigger on;", true);
		}
		return jsonObjKeyVariant.build().toString();
	}

	/***
	 * This method is called when uploaded file while creating Graphic Element
	 * 
	 * @param context
	 * @param gceID
	 * @param fileBase64
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public static void graphicElementFileUpload(Context context, String gceID, String fileBase64) throws Exception {
		OutputStream outputStream = null;
		try {

			JSONObject json = new JSONObject(fileBase64);
			String fileName = json.getString("fileName");
			String b64 = json.getString(RTAUtilConstants.JSON_KEY_DATA);

			ArtworkMaster artworkMaster = new ArtworkMaster(gceID);
			GraphicsElement graphicsElement = (GraphicsElement) artworkMaster.getBaseArtworkElement(context);
			String strGraphicDocumentId = graphicsElement.getGraphicDocument(context).getObjectId(context);
			String strGraphicElementId = graphicsElement.getObjectId();

			String[] blob = b64.split("base64,");

			String strWorkspace = context.createWorkspace();

			File dir = new File(strWorkspace);
			File out = new File(dir, fileName);

			outputStream = new FileOutputStream(out);
			byte[] decoder = Base64.getDecoder().decode(blob[1].toString().replace("\"", ""));
			outputStream.write(decoder);
			outputStream.close();

			// DomainObject domObject = DomainObject.newInstance(context,
			// "20336.41905.60632.44183");
			// domObject.checkinFile(context, true, true, DomainConstants.EMPTY_STRING,
			// DomainConstants.FORMAT_GENERIC,
			// fileName, strWorkspace);

			// File file = new File(dirpath);
			// Files.delete(file.toPath());

			String str1 = AWLPropertyUtil.getConfigPropertyString(context, "emxAWL.ImageDocument.Policies");
			if (!BusinessUtil.isNullOrEmpty(fileName)) {
				HashMap<Object, Object> hashMap = new HashMap<>();
				hashMap.put(KEY_FCS_ENABLED, PGWidgetConstants.STRING_FALSE);
				hashMap.put(PGWidgetConstants.KEY_OBJECT_ID, strGraphicDocumentId);
				hashMap.put(KEY_PARENT_ID, strGraphicElementId);
				hashMap.put(KEY_APPEND, PGWidgetConstants.STRING_FALSE);
				hashMap.put(KEY_UNLOCK, PGWidgetConstants.STRING_FALSE);
				hashMap.put(KEY_type, RTAUtilConstants.TYP_SYMBOL);
				hashMap.put(KEY_policy, str1);
				hashMap.put(KEY_PARENT_REL_NAME, AWLRel.GRAPHIC_DOCUMENT.toString());
				hashMap.put("fileName0", fileName);
				hashMap.put(CommonDocument.SELECT_TITLE, "testing");
				hashMap.put(KEY_NO_OF_FILES, "1");
				hashMap.put(KEY_OBJECT_ACTION, "checkin");
				hashMap.put(KEY_MCSURL, DomainConstants.EMPTY_STRING);
				hashMap.put(KEY_FORMAT, DomainConstants.FORMAT_GENERIC);
				hashMap.put("attributeMap", new HashMap<>());
				String[] arrayOfString = JPO.packArgs(hashMap);

				Map<String, String> map1 = JPO.invoke(context, "emxCommonDocument", null, "commonDocumentCheckin",
						arrayOfString, Map.class);

				// File file = new File(dirpath);
				// Files.delete(file.toPath());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}

	}

	/***
	 * This method is called to promote POA's to it's next state
	 * 
	 * @param context
	 * @param paramString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String promotePOAs(Context context, String paramString, HttpServletRequest request) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		StringBuilder sbReturnMessage = new StringBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);
			String strPOAId = null;
			StringBuffer strMessage = new StringBuffer();
			boolean boolTriggerFailed = false;
			HashMap<String, Object> mpParam = null;
			MapList mlTriggersList = null;
			Iterator<?> poaItr = poaIdList.iterator();
			Map<?, ?> mapTriggers = new HashMap<>();
			while (poaItr.hasNext()) {
				strPOAId = (String) poaItr.next();
				mpParam = new HashMap<>();
				mpParam.put(AWLConstants.OBJECT_ID, strPOAId);
				mpParam.put(REQUESTVALUEMAP, new HashMap<>());
				mlTriggersList = JPO.invoke(context, "emxTriggerValidation", new String[1], "getCheckTriggers",
						JPO.packArgs(mpParam), MapList.class);
				mapTriggers = executeTriggers(context, mlTriggersList, request, strMessage,
						(Map<String, Object>) mapTriggers);
			}

			boolTriggerFailed = (boolean) mapTriggers.get(TRIGGERFAILED);
			strMessage = (StringBuffer) mapTriggers.get(STR_MESSAGE);
			JsonObjectBuilder JsonOut = Json.createObjectBuilder();
			PGWidgetUtil.createErrorMessage(context, sbReturnMessage, JsonOut);
			JsonObject jsonObj = JsonOut.build();
			if(jsonObj.containsKey(PGWidgetConstants.KEY_STATUS) && PGWidgetConstants.KEY_FAILED.equals(jsonObj.getString(PGWidgetConstants.KEY_STATUS))) {
				output.add("warning", sbReturnMessage.toString());
			}
			if (!boolTriggerFailed && BusinessUtil.isNullOrEmpty(strMessage.toString())) {
				massPromotePOAs(context, poaIdList);
			} else {
				throw new Exception(strMessage.toString());
			}
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			StringBuilder sbError = new StringBuilder(e.getMessage());
			if(sbReturnMessage.length() > 0) {
				sbError.append("\n");
			}
			sbError.append(sbReturnMessage.toString());
			output.add(PGWidgetConstants.KEY_ERROR, sbError.toString());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return output.build().toString();
	}

	/***
	 * 
	 * @param context
	 * @param mlTriggersList
	 * @param request
	 * @param strMessage
	 * @param mapTriggers
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<?, ?> executeTriggers(Context context, MapList mlTriggersList, HttpServletRequest request,
			StringBuffer strMessage, Map<String, Object> mapTriggers) throws Exception {
		String[] strMethodArgs = null;
		StringList strlResults = null;
		StringList strCommentsList = null;
		String strSelectedId = null;
		String strTriggerId = null;
		String strTriggerRev = null;
		String strJPOResults = null;
		String strTempComments = DomainConstants.EMPTY_STRING;
		DomainObject doTriggerObject = null;
		Map<?, ?> mpTrigger = null;
		boolean boolTriggerFailed = false;
		mapTriggers.put(STR_MESSAGE, strMessage);
		mapTriggers.put(TRIGGERFAILED, boolTriggerFailed);
		Iterator<Map<?, ?>> mlItr = null;
		mlItr = mlTriggersList.iterator();
		com.matrixone.apps.framework.lifecycle.CalculateSequenceNumber myBean = new com.matrixone.apps.framework.lifecycle.CalculateSequenceNumber();
		matrix.db.Context contextRequest = null;
		contextRequest = Framework.getContext(request.getSession(false));
		while (mlItr.hasNext()) {
			mpTrigger = mlItr.next();
			strSelectedId = (String) mpTrigger.get(DomainConstants.SELECT_ID);
			strTriggerId = FrameworkUtil.split(strSelectedId, PGWidgetConstants.KEY_OR).get(1);
			doTriggerObject = DomainObject.newInstance(context, strTriggerId);
			strTriggerRev = doTriggerObject.getInfo(context, DomainConstants.SELECT_REVISION);
			if (strTriggerRev != null) {
				strMethodArgs = JPO.packArgs(strSelectedId);
				strJPOResults = (String) JPO.invoke(context, "emxTriggerValidationResults", new String[1], "executeTriggers",
						strMethodArgs, String.class);
				if (strJPOResults != null) {
					strlResults = FrameworkUtil.split(strJPOResults, PGWidgetConstants.KEY_TILDE);
					if ("Fail".equalsIgnoreCase((String) strlResults.get(0))) {
						strCommentsList = myBean.getClientTasks(context);
						if (BusinessUtil.isNotNullOrEmpty(strCommentsList)) {
//							strTempComments += strCommentsList.get(0) + "  ";
							strTempComments = DomainConstants.EMPTY_STRING;
							for (int k = 0; k < strCommentsList.size(); k++) {
								strTempComments += (String) strCommentsList.get(k) + "  ";
							}
							strMessage.append("Notice :");
							strMessage.append(strTempComments);
							strMessage.append("\n");
							boolTriggerFailed = true;
							mapTriggers.put(STR_MESSAGE, strMessage);
							mapTriggers.put(TRIGGERFAILED, boolTriggerFailed);
						}
						break;
					}

				}
			}
		}
		return mapTriggers;
	}

	/***
	 * This method is called for promoting POA's to it's next state
	 * 
	 * @param context
	 * @param poaIdList
	 * @return
	 * @throws Exception
	 */
	public static Boolean massPromotePOAs(Context context, StringList poaIdList) throws Exception {
		boolean isPOAPromoted = false;
		try {
			if (BusinessUtil.isNotNullOrEmpty(poaIdList)) {
				POA poa = null;
				for (String strPOAId : (List<String>) poaIdList) {
					poa = new POA(strPOAId);
					poa.promote(context);
					isPOAPromoted = true;
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return isPOAPromoted;
	}

	/***
	 * This method is called to fetch the duplicate instance sequence of MC's with
	 * the same type
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getDuplicateInstanceSeq(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutput = null;
		try {
			MapList mlDuplicateInstanceMC = new MapList();
			MapList mlDuplicateInstanceMCExtra = new MapList();
			MapList mlDuplicateInstanceMCMatch = new MapList();

			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);

			if (BusinessUtil.isNotNullOrEmpty(poaIdList)) {
				String strPOAid = null;
				String strPOASName = null;
				String strType = null;
				String strInsSeq = null;
				String typeInstSeqStr = null;
				MapList masterCopies = new MapList();
				int iPOAIdListSize = poaIdList.size();
				POA poa = null;
				List<String> tempTypeInstSeqList = null;
				StringList slSelect = new StringList(3);
				slSelect.add(AWLAttribute.MARKETING_NAME.getSel(context));
				slSelect.add(DomainConstants.SELECT_TYPE);
				slSelect.add(DomainConstants.SELECT_ID);
				// For each POA on edit poa page
				for (int i = 0; i < iPOAIdListSize; i++) {
					strPOAid = poaIdList.get(i);
					strPOASName = BusinessUtil.getInfo(context, strPOAid, DomainObject.SELECT_NAME);
					poa = new POA(strPOAid);
					tempTypeInstSeqList = new ArrayList<String>();
					// get all master copy element for each POA
					masterCopies = poa.getArtworkMasters(context, slSelect,
							new StringList(AWLAttribute.INSTANCE_SEQUENCE.getSel(context)), AWLConstants.EMPTY_STRING);
					// check for duplicate instance ids
					for (Map<String, Object> masterInfoMap : (List<Map>) masterCopies) {
						strInsSeq = (String) masterInfoMap.get(AWLAttribute.INSTANCE_SEQUENCE.getSel(context));
						strType = (String) masterInfoMap.get(DomainConstants.SELECT_TYPE);
						masterInfoMap.put(RTAUtilConstants.STR_POANAME, strPOASName);
						masterInfoMap.put(KEY_SEQ, strInsSeq);
						masterInfoMap.put(KEY_MARKETINGNAME,
								masterInfoMap.get(AWLAttribute.MARKETING_NAME.getSel(context)));
						typeInstSeqStr = strType + strInsSeq;
						if (tempTypeInstSeqList.contains(typeInstSeqStr)) {
							mlDuplicateInstanceMC.add(masterInfoMap);
							mlDuplicateInstanceMCMatch.add(masterInfoMap);
						} else {
							mlDuplicateInstanceMCExtra.add(masterInfoMap);
						}
						masterInfoMap.remove(AWLAttribute.MARKETING_NAME.getSel(context));
						masterInfoMap.remove(AWLAttribute.INSTANCE_SEQUENCE.getSel(context));
						tempTypeInstSeqList.add(typeInstSeqStr);
					}
					mlDuplicateInstanceMC = getFinalDuplicateInstanceSeqList(context, mlDuplicateInstanceMCExtra,
							mlDuplicateInstanceMCMatch, mlDuplicateInstanceMC);
				}
			}
			strOutput = convertMapListToJSON(context, mlDuplicateInstanceMC);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return strOutput;
	}

	/***
	 * This method is called to fetch the duplicate instance sequence of MC's with
	 * the same type
	 * 
	 * @param context
	 * @param mlDuplicateInstanceMCExtra
	 * @param mlDuplicateInstanceMCMatch
	 * @param mlDuplicateInstanceMC
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static MapList getFinalDuplicateInstanceSeqList(Context context, MapList mlDuplicateInstanceMCExtra,
			MapList mlDuplicateInstanceMCMatch, MapList mlDuplicateInstanceMC) throws Exception {
		String strType = null;
		String strInsSeq = null;
		String strInsSeqTemp = null;
		String strTypeTemp = null;
		Map<?, ?> CEMap = null;
		Map<?, ?> CEMapNew = null;
		String strPOANameTemp = null;
		String strPOANameMatch = null;
		// Build final list of duplicate instance ids
		int nInstancecount = mlDuplicateInstanceMCExtra.size();
		int nInstanceMCcount = mlDuplicateInstanceMCMatch.size();
		for (int j = 0; j < nInstancecount; j++) {
			CEMap = (Map<?, ?>) mlDuplicateInstanceMCExtra.get(j);
			strInsSeq = (String) CEMap.get(KEY_SEQ);
			strType = (String) CEMap.get(DomainConstants.SELECT_TYPE);
			strPOANameMatch = (String) CEMap.get(RTAUtilConstants.STR_POANAME);
			for (int k = 0; k < nInstanceMCcount; k++) {
				CEMapNew = (Map<?, ?>) mlDuplicateInstanceMCMatch.get(k);
				strInsSeqTemp = (String) CEMapNew.get(KEY_SEQ);
				strTypeTemp = (String) CEMapNew.get(DomainConstants.SELECT_TYPE);
				strPOANameTemp = (String) CEMapNew.get(RTAUtilConstants.STR_POANAME);

				if (strInsSeq.equals(strInsSeqTemp) && strType.equals(strTypeTemp)
						&& strPOANameMatch.equals(strPOANameTemp)) {
					if (!mlDuplicateInstanceMC.contains(CEMap))
						mlDuplicateInstanceMC.add(CEMap);
				}
			}
		}
		return mlDuplicateInstanceMC;
	}

	/***
	 * This method is called to validate date for Master Copies and returns MC's for
	 * which the date is already due or is of current date
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getValidityDateForMasterCopies(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutput = null;
		MapList mlValidityDateMC = new MapList();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);

			String strPOAid = null;
			String strPOASName = null;
			String strValidityDate = null;
			MapList masterCopies = new MapList();
			int iPOAIdListSize = poaIdList.size();
			POA poa = null;

			StringList slSelect = new StringList(4);
			slSelect.add(RTAUtilConstants.SELECT_VALIDITY_DATE);
			slSelect.add(AWLAttribute.MARKETING_NAME.getSel(context));
			slSelect.add(DomainConstants.SELECT_TYPE);
			slSelect.add(DomainConstants.SELECT_ID);

			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			Calendar currentDate = Calendar.getInstance();
			String dateNow = formatter.format(currentDate.getTime());
			Date systemDate = formatter.parse(dateNow);
			// For each POA on edit poa page
			for (int i = 0; i < iPOAIdListSize; i++) {
				strPOAid = poaIdList.get(i);
				strPOASName = BusinessUtil.getInfo(context, strPOAid, DomainObject.SELECT_NAME);
				poa = new POA(strPOAid);
				masterCopies = poa.getArtworkMasters(context, slSelect, AWLConstants.EMPTY_STRING);
				for (Map<String, Object> masterInfoMap : (List<Map>) masterCopies) {
					strValidityDate = (String) masterInfoMap.get(RTAUtilConstants.SELECT_VALIDITY_DATE);
					masterInfoMap.put(KEY_VALIDITYDATE, strValidityDate);
					masterInfoMap.put(KEY_MARKETINGNAME,
							masterInfoMap.get(AWLAttribute.MARKETING_NAME.getSel(context)));
					if (BusinessUtil.isNotNullOrEmpty(strValidityDate)) {
						Date validityDate = formatter.parse(strValidityDate);
						if (validityDate.compareTo(systemDate) <= 0) {
							masterInfoMap.remove(RTAUtilConstants.SELECT_VALIDITY_DATE);
							masterInfoMap.remove(AWLAttribute.MARKETING_NAME.getSel(context));
							masterInfoMap.put(RTAUtilConstants.STR_POANAME, strPOASName);
							mlValidityDateMC.add(masterInfoMap);
						}
					}
				}
			}
			strOutput = convertMapListToJSON(context, mlValidityDateMC);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return strOutput;
	}

	/***
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String getPOAInitialData(Context context) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjKeyVariant = Json.createObjectBuilder();
		try {
			JsonArrayBuilder jsonArrMasterCopyTypes = getMasterCopyTypes(context);
			JsonArrayBuilder jsonArrMasterLanguages = getMasterLanguages(context);
			JsonArrayBuilder jsonArrGraphicTypes = getGraphicTypes(context);
			jsonObjKeyVariant.add(KEY_MCETYPES, jsonArrMasterCopyTypes);
			jsonObjKeyVariant.add(KEY_LANGUAGES, jsonArrMasterLanguages);
			jsonObjKeyVariant.add(KEY_GCETYPES, jsonArrGraphicTypes);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonObjKeyVariant.build().toString();
	}

	/***
	 * This method is called to get the type of Master Copy
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getMasterCopyTypes(Context context) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrPOAData = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		try {
			StringList parentTypes = new StringList();
			StringList childrenTypes = new StringList();
			StringList slExcludeList = new StringList();
			StringList slExcludeChildrenList = new StringList();
			StringList finalExcludeList = new StringList();
			slExcludeList.add(UISearchUtil.getActualNames(context, "type_ListItem"));
			slExcludeList.add(UISearchUtil.getActualNames(context, "type_StructuredMasterArtworkElement"));
			slExcludeList.add(UISearchUtil.getActualNames(context, "type_IconWithVariableMasterText"));
			slExcludeList.add(UISearchUtil.getActualNames(context, "type_SupplementFactsMasterCopy"));
			slExcludeList.add(UISearchUtil.getActualNames(context, "type_InternalPackageIdentifierMasterCopy"));
			String strTopParentType = AWLType.MASTER_COPY_ELEMENT.get(context);
			String strParentType = null;
			String strChildType = null;
			if (BusinessUtil.isNotNullOrEmpty(strTopParentType)) {
				parentTypes = UINavigatorUtil.getChildrenTypeFromCache(context, strTopParentType, true);
			}
			for(int i = 0; i < slExcludeList.size(); i++) {
				slExcludeChildrenList.addAll(UINavigatorUtil.getChildrenTypeFromCache(context, slExcludeList.get(i), true));
			}
			finalExcludeList.addAll(slExcludeChildrenList);
			finalExcludeList.addAll(slExcludeList);
			for (int j = 0; j < parentTypes.size(); j++) {
				strParentType = parentTypes.get(j);
				childrenTypes = UINavigatorUtil.getChildrenTypeFromCache(context, strParentType, true);
				for (int k = 0; k < childrenTypes.size(); k++) {
					jsonObjKeyVariant = Json.createObjectBuilder();
					strChildType = childrenTypes.get(k);
					if (!finalExcludeList.contains(strChildType)) {
						jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, strChildType);
						jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, strChildType);
						jsonArrPOAData.add(jsonObjKeyVariant);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		return jsonArrPOAData;
	}

	/***
	 * This method returns the language info of MC's
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getMasterLanguages(Context context) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonArrayBuilder jsonArrLangData = Json.createArrayBuilder();
		try {
			StringList objSelect = new StringList(2);
			objSelect.add(DomainConstants.SELECT_ID);
			objSelect.add(DomainConstants.SELECT_NAME);

			MapList mlLanguageList = DomainObject.findObjects(context, AWLType.LOCAL_LANGUAGE.get(context),
					DomainConstants.QUERY_WILDCARD, DomainConstants.EMPTY_STRING, objSelect);
			StringList sllangNames = BusinessUtil.toStringList(mlLanguageList, DomainConstants.SELECT_NAME);

			String strLanguagesConfig = AWLPropertyUtil.getConfigPropertyString(context,
					"emxAWL.Preferences.MasterCopyCreate.Language");
			StringList sllangNamesConfig = FrameworkUtil.split(strLanguagesConfig, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			StringList langNames = new StringList();
			String strLanguage = null;
			int nLangSize = sllangNamesConfig.size();
			for (int cnt = 0; cnt < nLangSize; cnt++) {
				jsonObjKeyVariant = Json.createObjectBuilder();
				strLanguage = sllangNamesConfig.get(cnt);
				if (sllangNames.contains(strLanguage)) {
					langNames.add(strLanguage);
					jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, strLanguage);
					jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, strLanguage);
					jsonArrLangData.add(jsonObjKeyVariant);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		return jsonArrLangData;
	}

	/***
	 * This method returns the type of Graphic Element
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getGraphicTypes(Context context) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonArrayBuilder jsonArrLangData = Json.createArrayBuilder();
		try {
			String strType = null;
			StringList imageTypesList = AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.getDerivative(context, false);
			int iImageTypeSize = 0;
			if (BusinessUtil.isNotNullOrEmpty(imageTypesList)) {
				iImageTypeSize = imageTypesList.size();
				for (int i = 0; i < iImageTypeSize; i++) {
					strType = imageTypesList.get(i);
					jsonObjKeyVariant = Json.createObjectBuilder();
					jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, strType);
					jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, strType);
					jsonArrLangData.add(jsonObjKeyVariant);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		return jsonArrLangData;
	}

	/***
	 * This method is called to export POAs Returns: message on failure
	 * 
	 * @param context
	 * @param paramString
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static Response exportPOAs(Context context, String paramString, HttpServletResponse response)
			throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		InputStream in = null;
		ServletOutputStream sout = null;
		ResponseBuilder rbResponse = null;
		Response resOutput = null;
		try {
			StringList poaIdList = FrameworkUtil.split(paramString, PGWidgetConstants.KEY_OR);
			AWLExportPOAToExcel exportProcess = new AWLExportPOAToExcel();
			String name = exportProcess.startExportProcess(context, poaIdList);
			StringBuilder sbFile = new StringBuilder();
			sbFile.append(exportProcess.getWorkingPath());
			sbFile.append(File.separator);
			sbFile.append(name);
			sbFile.append(".xlsx");
			HashMap<String, Object> hmRequest = new HashMap<>();
			hmRequest.put("FileName", sbFile.toString());
			hmRequest.put(KEY_SELECTED_POA_LIST, poaIdList);
			hmRequest.put(KEY_FILE_DEST_PATH, exportProcess.getWorkingPath());
			hmRequest.put(KEY_NAME, name);

			HashMap<String, Object> hmProgramMap = new HashMap<>();
			hmProgramMap.put("requestMap", hmRequest);

			String[] methodargs = JPO.packArgs(hmProgramMap);
			name = (String) JPO.invoke(context, "pgAWL_Util", methodargs, "readWriteExcelData", methodargs,
					String.class);

			if (BusinessUtil.isNotNullOrEmpty(name)) {
				name = name + ".zip";

				in = new FileInputStream(exportProcess.getWorkingPath() + File.separator + name);
				sout = response.getOutputStream();

				int count;
				int buffSize = 8 * 1024;
				byte[] buf = new byte[buffSize];
				while ((count = in.read(buf, 0, buffSize)) > 0) {
					sout.write(buf, 0, count);
				}
				rbResponse = Response.ok(name);
				rbResponse.header("Content-Disposition", "attachment; filename=\"" + name + "\"");
				rbResponse.header("Content-Type", "application/octet-stream");
				rbResponse.header("fileName", name);
				sout.flush();
				AWLUtil.deleteFilesInWorkspace(context, exportProcess.getWorkingPath(), false);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		} finally {
			if (in != null)
				in.close();
			if (sout != null)
				sout.close();
		}
		if (rbResponse != null) {
			resOutput = rbResponse.build();
		}
		return resOutput;
	}

	/***
	 * This method is called on Harmonize POAs Returns:Message
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String harmonizePOAs(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strMessage = DomainConstants.EMPTY_STRING;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_OR);
			String strLeadPOAId = jsonInputData.getString(STR_LEADPOAID);
			String contextUser = context.getUser();
			String sLeadPOA = DomainConstants.EMPTY_STRING;
			String sFollowerPOA = DomainConstants.EMPTY_STRING;
			if (BusinessUtil.isNotNullOrEmpty(poaIdList) && BusinessUtil.isNotNullOrEmpty(strLeadPOAId)) {
				// Lead poa will be always 1
				sLeadPOA = BusinessUtil.getInfo(context, strLeadPOAId, DomainConstants.SELECT_NAME);
				MapList mlSelectedPOAs = BusinessUtil.getInfo(context, poaIdList,
						new StringList(DomainConstants.SELECT_NAME));
				if (BusinessUtil.isNotNullOrEmpty(mlSelectedPOAs)) {
					StringList slFollowerPOA = BusinessUtil.toStringList(mlSelectedPOAs, DomainConstants.SELECT_NAME);
					sFollowerPOA = FrameworkUtil.join(slFollowerPOA, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				}
			}

			if (BusinessUtil.isNotNullOrEmpty(sLeadPOA)) {
				if (sLeadPOA.contains(PGWidgetConstants.KEY_COMMA_SEPARATOR)) {
					// when more than one lead poa exists
					// ALERT GOES HERE FOR INVALID LEAD POA
					strMessage = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource", context.getLocale(),
							"emxAWL.Harmonization.InvalidLead");
				} else {
					boolean isFileSubmited = submitFileToInbound(context,
							generateHarmonizationInput(context, sLeadPOA, contextUser, sFollowerPOA));
					if (isFileSubmited) {
						// ACTION COMPLETED SUCCESSFULLY
						strMessage = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
								context.getLocale(), "emxAWL.Harmonization.success.widget");
					} else {
						// ERROR IN OPERATION
						strMessage = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
								context.getLocale(), "emxAWL.Harmonization.ErrorSubmit");
					}
				}
			} else {
				// ALERT FOR LEAD POA INFO NULL ? EMPTY
				strMessage = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource", context.getLocale(),
						"emxAWL.Harmonization.EmptyLead");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return (new JsonHelper()).getJsonString(strMessage);
	}

	/***
	 * 
	 * @param context
	 * @param leadPOA
	 * @param strUser
	 * @param followerPOA
	 * @return
	 * @throws Exception
	 */
	public static File generateHarmonizationInput(Context context, String leadPOA, String strUser, String followerPOA)
			throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();
		FileOutputStream fileOutReport = null;
		File harmonizationInput = null;
		XSSFSheet sheet = null;
		Row row = null;
		Cell cell = null;
		String fileNameFormat = "_rev_001.xlsx";
		try {
			String workSpacePath = context.createWorkspace();
			StringBuilder strBuf = new StringBuilder();
			strBuf.append(workSpacePath);
			strBuf.append(java.io.File.separator);
			strBuf.append(leadPOA);
			strBuf.append(fileNameFormat);
			String strReportFilePath = strBuf.toString();
			harmonizationInput = new File(strReportFilePath);
			fileOutReport = new FileOutputStream(harmonizationInput);

			sheet = wb.createSheet(leadPOA + "_rev_001");

			row = sheet.createRow(0);

			// CELL FOR LEAD POA
			cell = row.createCell(0);
			cell.setCellValue(leadPOA);

			// CELL FOR USER INFO
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellValue(strUser);

			// CELL FOR FOLLOWER POA HEADING
			row = sheet.createRow(2);
			cell = row.createCell(0);
			cell.setCellValue(STR_FOLLOWERPOA);

			// CELL FOR FOLLOWER POA'S
			cell = row.createCell(1);
			cell.setCellValue(followerPOA);

			wb.write(fileOutReport);

		} catch (Exception exp) {
		} finally {
			if (fileOutReport != null) {
				fileOutReport.close();
			}
		}
		return harmonizationInput;
	}

	/***
	 * 
	 * @param context
	 * @param inputFile
	 * @return
	 * @throws Exception
	 */
	public static boolean submitFileToInbound(Context context, File inputFile) throws Exception {
		boolean isFilePlaced = false;
		BusinessObject bObjConfigurationObject = new BusinessObject(ArtworkConstants.TYPE_PG_CONFIGURATION_ADMIN,
				EnoviaResourceBundle.getProperty(context, "emxAWL.RTABusinessObjectName.ArtworkConfigurationDetails"),
				pgV3Constants.SYMBOL_HYPHEN, ArtworkConstants.VAULT_ESERVICE_PRODUCTION);
		DomainObject dobjConfigurationObject = DomainObject.newInstance(context, bObjConfigurationObject);
		String harmonizationPath = dobjConfigurationObject.getInfo(context,
				RTAUtilConstants.SELECT_PGRTAHARMONIZATIONPATH);
		if (BusinessUtil.isNotNullOrEmpty(harmonizationPath)) {
			File inbound = new File(harmonizationPath);
			if (!inbound.exists()) {
				inbound.mkdirs();
			}
			if (inbound.exists()) {
				Files.move(Paths.get(inputFile.getAbsolutePath()),
						Paths.get(harmonizationPath + File.separator + inputFile.getName()),
						java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				isFilePlaced = true;
			}
		}
		return isFilePlaced;
	}

	/***
	 * 
	 * @param context
	 * @param strInputData
	 * @throws Exception
	 */
	public static String pgRTARetrieveVariableCE(Context context, String strInputData) throws Exception {

		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String selectedPOAId = jsonInputData.getString(RTAUtilConstants.POA_IDs);
		StringList slPOAs = null;

		String sPOAId = selectedPOAId;
		String[] sArrSelectedPOAId = sPOAId.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
		slPOAs = BusinessUtil.toStringList(sArrSelectedPOAId);

		int iSize = slPOAs.size();
		BusinessObject boConfigObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
				EnoviaResourceBundle.getProperty(context, "emxAWL.RTABusinessObjectName.ArtworkConfigurationDetails"),
				pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
		DomainObject domObject = DomainObject.newInstance(context, boConfigObject);
		int iMaxPOA = Integer.parseInt(domObject.getAttributeValue(context,
				RTAUtilConstants.ATTR_PG_MAX_POA_DETAILS_TO_RULES_MANAGER));
		if (iSize > iMaxPOA) {
			String[] args = { String.valueOf(iMaxPOA) };
			String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.MaxPOAsSelectedForRulesManager", args,
					null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
			return sAlert;
			// throw (new Exception(sAlert));

		} else {
			Map<String, Object> mRequestMap = new HashMap<>();
			mRequestMap.put(KEY_SELECTEDPOAs, slPOAs);

			String sInvalidPOA = JPO.invoke(context, "pgAAA_Util", null, "identifyInvalidPOA",
					matrix.db.JPO.packArgs(mRequestMap), String.class);

			if (BusinessUtil.isNotNullOrEmpty(sInvalidPOA)) {
				String[] args = { sInvalidPOA };
				String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.InvalidPOA", args,
						null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
				return sAlert;
				// throw (new Exception(sAlert));
			}
			String sPOAhavingblankFOPPA = JPO.invoke(context, "pgAAA_Util", null, "checkForFOPPA",
					matrix.db.JPO.packArgs(mRequestMap), String.class);
			if (BusinessUtil.isNotNullOrEmpty(sPOAhavingblankFOPPA)) {
				String[] args = { sPOAhavingblankFOPPA };
				String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.POAFOPPABlank", args,
						null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
				return sAlert;
				// throw (new Exception(sAlert));
			}
			mRequestMap.put(KEY_INTEGRATIONTYPE,
					EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.PASSIntegration"));

			Map<?, ?> mPOAInfo = JPO.invoke(context, "pgAAA_Util", null, "processPOAData",
					matrix.db.JPO.packArgs(mRequestMap), Map.class);
			StringBuilder sbAlert = new StringBuilder();
			if (mPOAInfo != null && !mPOAInfo.isEmpty()) {
				StringList slPOAToBeSubmitted = (StringList) mPOAInfo.get("New");
				StringList slPOAWithKeep = (StringList) mPOAInfo.get("Keep");
				StringList slPOAWithSameAs = (StringList) mPOAInfo.get("SameAs");
				if (slPOAWithKeep != null && !slPOAWithKeep.isEmpty()) {
					StringList slPOANameWithKeep = getNameFromID(context, slPOAWithKeep, mPOAInfo);
					String[] args = { FrameworkUtil.join(slPOANameWithKeep, PGWidgetConstants.KEY_COMMA_SEPARATOR) };
					String sAlert = MessageUtil.getMessage(context, null,
							"emxAWL.Alert.Notification.UnableToRetrieveCE", args, null, context.getLocale(),
							AWLConstants.AWL_STRING_RESOURCE);
					sbAlert.append(sAlert);
				}
				if (slPOAWithSameAs != null && !slPOAWithSameAs.isEmpty()) {
					StringList slPOANameWithSameAs = getNameFromID(context, slPOAWithSameAs, mPOAInfo);
					String[] args = { FrameworkUtil.join(slPOANameWithSameAs, PGWidgetConstants.KEY_COMMA_SEPARATOR) };
					String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.SameAsPOA", args,
							null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
					sbAlert.append(sAlert);
				}
				if (slPOAToBeSubmitted != null && !slPOAToBeSubmitted.isEmpty()) {
					mRequestMap.put(KEY_SELECTEDPOAs, slPOAToBeSubmitted);

					mRequestMap.put(KEY_JOBTITLE,
							EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.PASSIntegrationTitle"));

					StringList slPOANameForSubmitted = getNameFromID(context, slPOAToBeSubmitted, mPOAInfo);
					String[] args = { FrameworkUtil.join(slPOANameForSubmitted, PGWidgetConstants.KEY_COMMA_SEPARATOR) };
					String sAlert = MessageUtil.getMessage(context, null,
							"emxAWL.Alert.Notification.SendReqToRulesManager", args, null, context.getLocale(),
							AWLConstants.AWL_STRING_RESOURCE);
					// create job object and save all the poa ids
					JPO.invoke(context, "pgAAA_Util", null, "createJobObjectForReqToRulesManager",
							matrix.db.JPO.packArgs(mRequestMap), void.class);
					sbAlert.append(sAlert);
				}
				if (sbAlert.length() > 0) {
					return sbAlert.toString();
					// throw (new Exception(sbAlert.toString()));
				}
			} else {
				String sAlert = EnoviaResourceBundle.getProperty(context, AWLConstants.AWL_STRING_RESOURCE,
						context.getLocale(), "emxAWL.Alert.Notification.POANotSubmitted");
				return sAlert;
				// throw (new Exception(sAlert));
			}

		}
		return PGWidgetConstants.KEY_SUCCESS;
	}

	/***
	 * 
	 * @param context
	 * @param slPOAID
	 * @param mPOAInfo
	 * @return
	 * @throws Exception
	 */
	public static StringList getNameFromID(Context context, StringList slPOAID, Map<?, ?> mPOAInfo) throws Exception {
		StringList slPOAName = new StringList(slPOAID.size());
		for (String sPOAID : slPOAID) {
			slPOAName.add((String) mPOAInfo.get(sPOAID));
		}
		return slPOAName;
	}

	/***
	 * This method retrieves Claim Object Data
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static String pgRTARetrieveDataForClaim(Context context, String strInputData) throws Exception {
		String StrAlertOutput = null;
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String selectedPOAId = jsonInputData.getString(RTAUtilConstants.POA_IDs);
		StringList slPOAs = null;
		String sPOAId = selectedPOAId;
		String[] sArrSelectedPOAId = sPOAId.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
		slPOAs = BusinessUtil.toStringList(sArrSelectedPOAId);
		int iSize = slPOAs.size();
		BusinessObject boConfigObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
				EnoviaResourceBundle.getProperty(context, "emxAWL.RTABusinessObjectName.ArtworkConfigurationDetails"),
				pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
		DomainObject domObject = DomainObject.newInstance(context, boConfigObject);
		int iMaxPOA = Integer.parseInt(domObject.getAttributeValue(context,
				RTAUtilConstants.ATTR_PG_MAX_POA_DETAILS_TO_RULES_MANAGER));

		if (iSize > iMaxPOA) {
			String[] args = { String.valueOf(iMaxPOA) };
			String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.MaxPOAsSelectedForRulesManager", args,
					null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
			StrAlertOutput = sAlert;
		} else {
			Map<String, Object> mRequestMap = new HashMap<>();
			mRequestMap.put(KEY_SELECTEDPOAs, slPOAs);
			mRequestMap.put(KEY_INTEGRATIONTYPE, "CLAIMS");
			String sInvalidPOA = JPO.invoke(context, "pgAAA_Util", null, "identifyInvalidPOA",
					matrix.db.JPO.packArgs(mRequestMap), String.class);
			if (BusinessUtil.isNotNullOrEmpty(sInvalidPOA)) {
				String[] args = { sInvalidPOA };
				String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.InvalidPOA", args,
						null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
				StrAlertOutput = sAlert;

			} else {
				Map<?, ?> mPOAInfo = JPO.invoke(context, "pgAAA_Util", null, "processPOAClaimsData",
						matrix.db.JPO.packArgs(mRequestMap), Map.class);
				StringBuilder sbAlert = new StringBuilder();
				if (mPOAInfo != null && !mPOAInfo.isEmpty()) {
					StringList slPOAToBeSubmitted = (StringList) mPOAInfo.get(KEY_SPOA_OBJ_ID);
					if (slPOAToBeSubmitted != null && !slPOAToBeSubmitted.isEmpty()) {
						mRequestMap.put(KEY_SELECTEDPOAs, slPOAToBeSubmitted);
						mRequestMap.put(KEY_JOBTITLE,
								EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.CLAIMSIntegrationJobTitle"));
						StringList slPOANameForSubmitted = getNameFromID(context, slPOAToBeSubmitted, mPOAInfo);
						String[] args = { FrameworkUtil.join(slPOANameForSubmitted, PGWidgetConstants.KEY_COMMA_SEPARATOR) };
						String sAlert = MessageUtil.getMessage(context, null,
								"emxAWL.Alert.Notification.SendReqToRulesManager", args, null, context.getLocale(),
								AWLConstants.AWL_STRING_RESOURCE);
						JPO.invoke(context, "pgAAA_Util", null, "createJobObjectForReqToRulesManager",
								matrix.db.JPO.packArgs(mRequestMap), void.class);
						sbAlert.append(sAlert);
					}
					if (sbAlert.length() > 0) {
						StrAlertOutput = sbAlert.toString();
						;
					}
				} else {
					String sAlert = EnoviaResourceBundle.getProperty(context, AWLConstants.AWL_STRING_RESOURCE,
							context.getLocale(), "emxAWL.Alert.Notification.POANotSubmitted");
					StrAlertOutput = sAlert;
				}
			}
		}
		return StrAlertOutput;
	}

	/***
	 * This method is called to add Local Copy Element to Master Copy Element
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static String addLceToMce(Context context, String paramString) throws Exception {
		String strOut = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String objectId = jsonInputData.getString(BusinessUtil.OBJECT_ID);
			String language = jsonInputData.getString(KEY_LANGUAGE);
			HashMap<String, Object> programMap = new HashMap<>();
			programMap.put(PGWidgetConstants.KEY_OBJECT_ID, objectId);
			programMap.put(KEY_LANGUAGE, language);
			// Check if language already added to MCE
			StringList selBusStmts = new StringList();
			selBusStmts.add("attribute[Copy Text Language]");
			String typepattern = PGWidgetConstants.CONSTANT_STRING_STAR;
			String relpattern = "Artwork Element Content";
			DomainObject mceDomainObj = DomainObject.newInstance(context, objectId);
			MapList connectedLangs = mceDomainObj.getRelatedObjects(context, relpattern, typepattern, selBusStmts,
					new StringList(), false, true, (short) 1, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			Iterator<?> langsItr = connectedLangs.iterator();
			while (langsItr.hasNext()) {
				Map<?, ?> tempMap = (Map<?, ?>) langsItr.next();
				if (language.equalsIgnoreCase(tempMap.get("attribute[Copy Text Language]").toString())) {
					throw new Exception("LCE with language " + language + " already connected to MCE.");
				}
			}
			String methodargs[] = JPO.packArgs(programMap);
			String strJPOName = jsonInputData.getString(KEY_JPO_NAME);
			String strMethodName = jsonInputData.getString(KEY_JPO_METHOD_NAME);
			HashMap<?, ?> returnMap = (HashMap<?, ?>) JPO.invoke(context, strJPOName, null, strMethodName, methodargs,
					HashMap.class); // Returns objectId of Local Copy created
			DomainObject localCopyObj = DomainObject.newInstance(context, returnMap.get(PGWidgetConstants.KEY_OBJECTID).toString());
			String strCopytext = jsonInputData.getString(KEY_COPY_TEXT);
			Map<String, String> mapAttribute = new HashMap<String, String>();
			mapAttribute.put(KEY_COPY_TEXT_RTE, strCopytext);
			localCopyObj.setAttributeValues(context, mapAttribute);
			JsonObjectBuilder jsonOut = Json.createObjectBuilder();
			jsonOut.add(PGWidgetConstants.KEY_OBJECTID, returnMap.get(PGWidgetConstants.KEY_OBJECTID).toString());
			strOut = jsonOut.build().toString();
		} catch (Exception e) {
			throw e;
		}
		return strOut;
	}

	/***
	 * This method is called to fetch countries
	 * 
	 * @param context
	 * @param paramString1
	 * @param paramString2
	 * @param paramString3
	 * @return
	 * @throws Exception
	 */
	public static String getCountriesApplicable(Context context, String paramString1, String paramString2,
			String paramString3) throws Exception {
		StringList RegionIDSL = FrameworkUtil.split(paramString1, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		HashMap<Object, Object> responseHashMap = new HashMap<>();
		try {

			for (int i = 0; i < RegionIDSL.size(); i++) {
				String RegionID = RegionIDSL.get(i);

				Region region = CPDCache.getRegion(RegionID);
				List<Country> list = region.getCountries(context);

				HashMap<Object, Object> hashMap = new HashMap<>();

				for (Country country : list) {
					String str = country.getObjectId();
					if (paramString2 != null
							&& (paramString2.equalsIgnoreCase("AllCountries") || paramString2.indexOf(str) >= 0)) {
						HashMap<Object, Object> hashMap1 = new HashMap<>();
						hashMap1.put(PGWidgetConstants.KEY_OBJECTID, str);
						hashMap1.put(PGWidgetConstants.KEY_NAME, country.getName());
						if (paramString3 != null && paramString3.indexOf(str) >= 0)
							hashMap1.put(KEY_SELECTED, Boolean.valueOf(true));
						hashMap.put(country.getObjectId(), hashMap1);
					}
				}
				responseHashMap.put(RegionID, hashMap);
			}
		} catch (Exception e) {
			throw e;
		}
		return (new JsonHelper()).getJsonString(responseHashMap);
	}

	/***
	 * This method is called to fetch Languages
	 * 
	 * @param context
	 * @param paramString1
	 * @param paramString2
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getLanguagesToCreatePOA(Context context, String paramString1, String paramString2)
			throws Exception {
		HashMap<Object, Object> responseHashMap = new HashMap<>();
		try {
			TreeMap<Object, Object> treeMap = new TreeMap<>();
			String str = DomainConstants.EMPTY_STRING;
			if (UIUtil.isNotNullAndNotEmpty(paramString2)) {
				str = AWLUtil.strcat(new Object[] { "[", paramString2, "]" });
				JsonReader jsonReader = Json.createReader(new StringReader(str));
				JsonArray jsonArray = jsonReader.readArray();
				for (byte b = 0; b < jsonArray.size(); b++) {
					JsonObject jsonObject = jsonArray.getJsonObject(b);
					String str1 = jsonObject.getString(PGWidgetConstants.KEY_OBJECTID);
					int i = 0;
					try {
						i = jsonObject.getInt("seq");
					} catch (Exception exception) {
						i = Integer.valueOf(jsonObject.getString("seq")).intValue();
					}
					treeMap.put(Integer.valueOf(i), str1);
				}
			}
			StringList countryIDSL = FrameworkUtil.split(paramString1, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			for (int i = 0; i < countryIDSL.size(); i++) {

				String countryID = countryIDSL.get(i);

				Country country = CPDCache.getCountry(countryID);
				List<Language> list = country.getLanguages(context);
				HashMap<Object, Object> hashMap1 = new HashMap<>();
				HashMap<Object, Object> hashMap2 = new HashMap<>();
				HashMap<Object, Object> hashMap3 = new HashMap<>();
				for (Language language : list) {
					HashMap<Object, Object> hashMap = new HashMap<>();
					hashMap.put(PGWidgetConstants.KEY_NAME, language.getName());
					hashMap.put(PGWidgetConstants.KEY_OBJECTID, language.getObjectId());
					if (UIUtil.isNotNullAndNotEmpty(str) && str.indexOf(language.getObjectId()) > -1) {
						hashMap.put(KEY_SELECTED, Boolean.valueOf(true));
						hashMap2.put(language.getObjectId(), hashMap);
						continue;
					}
					hashMap.put("seq", "-1");
					hashMap1.put(language.getObjectId(), hashMap);
				}

				for (Object integer : treeMap.keySet()) {
					String str1 = (String) treeMap.get(integer);
					Map<String, String> map = (Map) hashMap2.get(str1);
					if (map != null) {
						map.put("seq", integer.toString());
						hashMap3.put(str1, map);
					}
				}
				for (Object str1 : hashMap1.keySet()) {
					Map map = (Map) hashMap1.get(str1);
					hashMap3.put(str1, map);
				}
				responseHashMap.put(countryID, hashMap3);
			}

		} catch (Exception e) {
			throw e;
		}

		return (new JsonHelper()).getJsonString(responseHashMap);
	}

	@SuppressWarnings("unused")
	public static String validateAlertForIntegration(Context context, String paramString, HttpServletRequest request)
			throws Exception {
		String strOutput = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			String[] arrPOAID = strObjectIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR, -1);
			DomainObject domPOA = null;
			String SELECT_ATTR_ClaimRequestId = RTAUtilConstants.ATTR_PG_CLAIM_REQUEST_ID;
			for (int i = 0; i < arrPOAID.length; i++) {
				domPOA = DomainObject.newInstance(context, arrPOAID[i]);
				String sClaimRequestId = domPOA.getAttributeValue(context, SELECT_ATTR_ClaimRequestId);
				Map<String, StringList> mRequestMap = new HashMap<String, StringList>();
				mRequestMap.put(KEY_SELECTEDPOAs, FrameworkUtil.split(strObjectIds, PGWidgetConstants.KEY_COMMA_SEPARATOR));
				strOutput = JPO.invoke(context, "pgAAA_Util", null, "getIntegrationPOA",
						matrix.db.JPO.packArgs(mRequestMap), String.class);
			}

		} catch (Exception e) {
			throw e;
		}

		return strOutput;
	}

	public static String validateForClaimsMC(Context context, String paramString, HttpServletRequest request)
			throws Exception {
		String strOut = null;
		Map<String, String> mPOAMCID = new HashMap<String, String>();
		Map<String, String> mPOAMCTypeName = new HashMap<String, String>();
		boolean isContextPushedCheck = false;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectIds = jsonInputData.getString(KEY_SELECTEDMCEs_POAs);
			String REL_PGCLAMISID = RTAUtilConstants.RELATIONSHIP_PGMASTERCOPYCLAIM;
			String[] arrPOAID = strObjectIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR, -1);
			StringList slSplitArr = null;
			String sMaster = null;
			String strMCRCheck = null;
			DomainObject domObjMaster = null;
			// Added by RTA AugCW-22 ClaimObject Check Requirement-44157 - End
			String STR_SUPER_USER_Agent = RTAUtilConstants.PERSON_USER_AGENT;
			// RTA users do not have access on DCM Data so need to push context
			ContextUtil.pushContext(context, STR_SUPER_USER_Agent, null, context.getVault().getName());
			isContextPushedCheck = true;
			StringList slSelects = new StringList(DomainConstants.SELECT_TYPE);
			slSelects.add(DomainConstants.SELECT_NAME);
			slSelects.add("to[" + REL_PGCLAMISID + "]");
			for (String str : arrPOAID) {
				// format mc_poaMCID_POAID
				slSplitArr = FrameworkUtil.split(str, PGWidgetConstants.KEY_UNDERSCORE);
				sMaster = slSplitArr.get(slSplitArr.size() - 2);
				sMaster = sMaster.substring(3, sMaster.length());
				domObjMaster = DomainObject.newInstance(context, sMaster);
				Map mapInfo = domObjMaster.getInfo(context, slSelects);
				strMCRCheck = (String) mapInfo.get("to[" + REL_PGCLAMISID + "]");
				if (strMCRCheck != null && strMCRCheck.equalsIgnoreCase(PGWidgetConstants.STRING_CAPITAL_TRUE)) {
					mPOAMCTypeName.put(DomainConstants.SELECT_NAME, (String) mapInfo.get(DomainConstants.SELECT_NAME));
					mPOAMCTypeName.put(DomainConstants.SELECT_TYPE, (String) mapInfo.get(DomainConstants.SELECT_TYPE));
					mPOAMCID.put(sMaster,new JsonHelper().getJsonString(mPOAMCTypeName));
				}
			}
		} catch (Exception et) {
			throw et;
		} finally {

			if (isContextPushedCheck) {
				ContextUtil.popContext(context);
				isContextPushedCheck = false;
			}
			if (!mPOAMCID.isEmpty()) {
				strOut = new JsonHelper().getJsonString(mPOAMCID);
			}
		}
		return strOut;
	}

	public static String updateRemoveIntegrationPOA(Context context, String paramString) throws Exception {
		String strOut = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectIds = jsonInputData.getString(KEY_SELECTED_POA_IDs);
			String[] arrPOAID = strObjectIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR, -1);
			String SELECT_ATTR_AUTOMATICRETRIEVECE = RTAUtilConstants.ATTR_PGAUTOMATEDRETRIEVCEsCHANGES;
			StringList slSplitArr = null;
			DomainObject domPOA = null;
			String sPOA = null;
			String sAutomatedRetrieveCEChangesUpdate = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.Attribute.pgAutomatedRetrievCEsChanges.Value");
			for (String str : arrPOAID) {
				// format mc_poaMCID_POAID
				slSplitArr = FrameworkUtil.split(str, PGWidgetConstants.KEY_UNDERSCORE);
				sPOA = slSplitArr.get(slSplitArr.size() - 1);
				domPOA = DomainObject.newInstance(context, sPOA);
				if (domPOA != null) {
					if (domPOA.getAttributeValue(context, SELECT_ATTR_AUTOMATICRETRIEVECE) != null) {
						domPOA.setAttributeValue(context, SELECT_ATTR_AUTOMATICRETRIEVECE,
								sAutomatedRetrieveCEChangesUpdate);
					}
				}
			}
			strOut = PGWidgetConstants.KEY_SUCCESS;
		} catch (Exception et) {
			throw et;
		}
		return strOut;
	}

	public static String updateIntegrationPOA(Context context, String paramString) throws Exception {
		String strOut = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectIds = jsonInputData.getString(KEY_SELECTED_POA_IDs);
			String[] arrPOAID = strObjectIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR, -1);
			String SELECT_ATTR_AUTOMATICRETRIEVECE = RTAUtilConstants.ATTR_PGAUTOMATEDRETRIEVCEsCHANGES;
			DomainObject domPOA = null;
			String sAutomatedRetrieveCEChangesUpdate = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.Attribute.pgAutomatedRetrievCEsChanges.Value");
			for (int i = 0; i < arrPOAID.length; i++) {
				domPOA = DomainObject.newInstance(context, arrPOAID[i]);
				if (domPOA != null) {
					if (domPOA.getAttributeValue(context, SELECT_ATTR_AUTOMATICRETRIEVECE) != null) {
						domPOA.setAttributeValue(context, SELECT_ATTR_AUTOMATICRETRIEVECE,
								sAutomatedRetrieveCEChangesUpdate);
					}
				}
			}
			strOut = PGWidgetConstants.KEY_SUCCESS;
		} catch (Exception et) {
			throw et;
		}
		return strOut;
	}

	public static String getImageURl(Context context, String paramString) throws Exception {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		MCSURL = jsonInputData.getString(KEY_MCSURL);
		String ObjectID = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
		StringList ObjectIDSL = FrameworkUtil.split(ObjectID, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		// String ObjectID = "20336.41905.23162.20357";
		String strImage = null;
		for (int i = 0; i < ObjectIDSL.size(); i++) {
			try {
				String[] arrayOfString = new String[2];
				arrayOfString[0] = ObjectIDSL.get(i);
				arrayOfString[1] = MCSURL;

				strImage = JPO.invoke(context, "AWLGraphicsElementUI", null, "getGraphicImageURLforPOAAction",
						arrayOfString, String.class);

				jsonObjectBuilder.add(DomainConstants.SELECT_ID, ObjectIDSL.get(i));
				jsonObjectBuilder.add(KEY_IMAGE_DATA, strImage);
				jsonArr.add(jsonObjectBuilder);
			} catch (Exception e) {
				throw e;
			}
		}
		return jsonArr.build().toString();
	}

	public static String updateRemoveRoboticPOA(Context context, String paramString) throws Exception {
		String strOut = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectIds = jsonInputData.getString(KEY_SELECTED_POA_IDs);
			String[] arrPOAID = strObjectIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR, -1);
			String SELECT_ATTR_ROBOTIC2COMMENT = RTAUtilConstants.ATTR_PG_ROBOTIC_2_COMMENT;
			DomainObject domPOA = null;
			;
			String sRobotic2POAComment = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.Attribute.Robotic2Comment.Value");
			for (int i = 0; i < arrPOAID.length; i++) {
				domPOA = DomainObject.newInstance(context, arrPOAID[i]);
				if (domPOA != null) {
					if (domPOA.getAttributeValue(context, SELECT_ATTR_ROBOTIC2COMMENT) != null)
						domPOA.setAttributeValue(context, SELECT_ATTR_ROBOTIC2COMMENT, sRobotic2POAComment);
				}
			}
			strOut = PGWidgetConstants.KEY_SUCCESS;
		} catch (Exception et) {
			throw et;
		}
		return strOut;
	}

	public static String authorApproveMasterCopy(Context context, String paramString) throws Exception {
		boolean isExceptionOccurred = false;
		boolean isTransactionActive = false;
		StringBuilder sbMessage = new StringBuilder();
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strMceId = jsonInputData.getString(RTAUtilConstants.KEY_MCE_ID);
			String strContent = jsonInputData.containsKey(KEY_NEW_COPY_TEXT) ? jsonInputData.getString(KEY_NEW_COPY_TEXT) : DomainConstants.EMPTY_STRING;
			String strAction = jsonInputData.getString(KEY_ACTION);
			StringList slMCEIds = StringUtil.split(strMceId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			//RTA DS 2022x.05 added for ALM - 55631 Start
			StringList slBusSel = StringList.create(DomainConstants.SELECT_NAME, DomainConstants.SELECT_CURRENT);
			String strStateCEReview = AWLState.REVIEW.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT);
			//RTA DS 2022x.05 added for ALM - 55631 End
			ContextUtil.startTransaction(context, true);
			StringList noAccessCEList = new StringList();
			isTransactionActive = true;
			for (int i = 0; i < slMCEIds.size(); i++) {
				PGRTADCMDataLoad.setPrimaryOwnership(context, slMCEIds.get(i));
				String strBaseLCId = PGRTADCMDataLoad.getBaseLocalCopyInfoForMaster(context, slMCEIds.get(i));
				if (UIUtil.isNotNullAndNotEmpty(strBaseLCId)) {
					//RTA DS 2022x.05 added for ALM - 55631 Start
					CopyElement localCopyElem = new CopyElement(strBaseLCId);
					Map mLCInfo = localCopyElem.getInfo(context, slBusSel);
					String strBaseLCName = (String) mLCInfo.get(DomainConstants.SELECT_NAME);
					String strCurrent = (String) mLCInfo.get(DomainConstants.SELECT_CURRENT);
					Map mapAuthorApprover = getAuthorApproverForCopyElement(context, strBaseLCId);
					String strAuthor = (String) mapAuthorApprover.get(RTAUtilConstants.KEY_AUTHOR);
					String strApprover = (String) mapAuthorApprover.get(RTAUtilConstants.KEY_APPROVER);
					if (KEY_AUTHOR.equals(strAction) && UIUtil.isNotNullAndNotEmpty(strAuthor)
							&& strAuthor.indexOf(context.getUser()) == -1) {
						noAccessCEList.add(strBaseLCName);
						continue;
					}
					else if (KEY_APPROVE.equals(strAction) && UIUtil.isNotNullAndNotEmpty(strApprover)
							&& strApprover.indexOf(context.getUser()) == -1) {
						noAccessCEList.add(strBaseLCName);
						continue;
					}
					//RTA DS 2022x.05 added for ALM - 55631 End
					String strLoggedInUserId = PersonUtil.getPersonObjectID(context);
					
					if (UIUtil.isNotNullAndNotEmpty(strAction)) {
						if(KEY_SAVE.equals(strAction)) {
							localCopyElem.setCopyText(context, strContent);
							if (jsonInputData.containsKey(EditCLConstants.COPYLIST_ID)) {
								String strCLId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
								String strCLMotherChildInfo = BusinessUtil.getInfo(context, strCLId,
										EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO);
								MapList mlCopyListLCs = DomainObject.newInstance(context, strCLId).getRelatedObjects(
										context, AWLRel.ARTWORK_ASSEMBLY.get(context), // Rel Pattern
										AWLType.ARTWORK_ELEMENT.get(context), // Type Pattern
										StringList.create(DomainConstants.SELECT_ID), // Object Select
										null, // rel Select
										false, // get To
										true, // get From
										(short) 0, // recurse level
										null, // where Clause
										null, 0);
								StringList slCLLCIds = BusinessUtil.toStringList(mlCopyListLCs,
										DomainConstants.SELECT_ID);
								if (BusinessUtil.isNotNullOrEmpty(strCLMotherChildInfo)) {
									com.pg.widgets.editCopyList.EditCLUtil.syncMotherContentToChildLCs(context,
											strCLMotherChildInfo, strBaseLCId, strContent, slCLLCIds);
								}
							}
						} else {
							if (UIUtil.isNotNullAndNotEmpty(strContent)) {
							localCopyElem.setCopyText(context, strContent);
							}
							String strApproverToBe = null;
							//RTA DS 2022x.05 added for ALM - 55631 Start
							if (KEY_AUTHOR.equals(strAction) || (KEY_APPROVE.equals(strAction)
									&& DomainConstants.STATE_PART_PRELIMINARY.equals(strCurrent))) {
								strApproverToBe = UIUtil.isNullOrEmpty(strApprover) ? strLoggedInUserId : null;
								localCopyElem.updateAssignee(context, strLoggedInUserId, strApproverToBe); //(context, strAuthor, strApprover)
								promoteLCtoReviewReleaseState(context, strBaseLCId, strCurrent);
							}
							strCurrent = localCopyElem.getInfo(context, DomainConstants.SELECT_CURRENT);
							if (KEY_APPROVE.equals(strAction) && strStateCEReview.equals(strCurrent)) {								
								promoteLCtoReviewReleaseState(context, strBaseLCId,	strStateCEReview);
							}
						}
						//RTA DS 2022x.05 added for ALM - 55631 Ends
					}
				}

			}
			ContextUtil.commitTransaction(context);
			if(noAccessCEList.size() > 0) {
				throw new Exception(EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
							context.getLocale(), "emxAWL.Error.NoAccessToPerformOperation")+" for Copy Element: "+ String.join(",", noAccessCEList));
			}
		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
				isExceptionOccurred = true;
				sbMessage.append(e.getMessage()).append("\n");
			}
			
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE + PGWidgetUtil.getExceptionTrace(e));
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
			return jsonStatus.build().toString();
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build()
				.toString();
	}
	public static String checkAuthorApproverAccessForRevise(Context context, HttpServletRequest request,
			String paramString) throws Exception {
		JsonObjectBuilder jsonResponse = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strMceId = jsonInputData.getString(RTAUtilConstants.KEY_MCE_ID);
			String strAuthorMsg = null;
			String strApproverMsg = null;
			boolean bAuthorAccess = false;
			boolean bApproverAccess = false;
			JsonObjectBuilder jsonAuthor = Json.createObjectBuilder();
			JsonObjectBuilder jsonApprover = Json.createObjectBuilder();
			String strBaseLCId = PGRTADCMDataLoad.getBaseLocalCopyInfoForMaster(context, strMceId);
			if (UIUtil.isNotNullAndNotEmpty(strBaseLCId)) {
				Map mapAuthorApprover = getAuthorApproverForCopyElement(context, strBaseLCId);
				String strAuthor = (String) mapAuthorApprover.get(RTAUtilConstants.KEY_AUTHOR);
				String strApprover = (String) mapAuthorApprover.get(RTAUtilConstants.KEY_APPROVER);
				if (UIUtil.isNullOrEmpty(strAuthor)
						|| (UIUtil.isNotNullAndNotEmpty(strAuthor) && strAuthor.indexOf(context.getUser()) == -1)) {
					strAuthorMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
							context.getLocale(), "emxAWL.Error.AuthorNoAccessToPerformOperation");
					jsonAuthor.add(PGWidgetConstants.KEY_MESSAGE, strAuthorMsg);
				} else
					bAuthorAccess = true;
				if (UIUtil.isNullOrEmpty(strApprover)
						|| (UIUtil.isNotNullAndNotEmpty(strApprover) && strApprover.indexOf(context.getUser()) == -1)) {
					strApproverMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
							context.getLocale(), "emxAWL.Error.ApproverNoAccessToPerformOperation");
					jsonApprover.add(PGWidgetConstants.KEY_MESSAGE, strApproverMsg);
				} else
					bApproverAccess = true;
			}
			jsonAuthor.add(PGWidgetConstants.KEY_STATUS, bAuthorAccess);
			jsonApprover.add(PGWidgetConstants.KEY_STATUS, bApproverAccess);
			jsonResponse.add("author", jsonAuthor);
			jsonResponse.add("approve", jsonApprover);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			return jsonResponse.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_ERROR)
					.add(PGWidgetConstants.KEY_MESSAGE, e.getMessage()).build().toString();
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
				.add(PGWidgetConstants.KEY_MESSAGE, jsonResponse).build().toString();
	}
	public static String reviseCopyElement(Context context, HttpServletRequest request, String paramString)
			throws Exception {
		boolean isExceptionOccurred = false;
		boolean isTransactionActive = false;
		StringBuilder sbMessage = new StringBuilder();
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strMceId = jsonInputData.getString(RTAUtilConstants.KEY_MCE_ID);
			String strReason = jsonInputData.getString(RTAUtilConstants.KEY_REASON);
			String strComment = jsonInputData.getString(RTAUtilConstants.KEY_COMMENT);
			String strDefectType = jsonInputData.getString(RTAUtilConstants.KEY_DEFECT_TYPE);
			String strResponsibleFunction = jsonInputData.getString(KEY_RESPONSIBLE_FUNCTION);
			String strAction = jsonInputData.getString(KEY_ACTION);
			
			if(KEY_REVISE_LCE.equals(strAction)) {
            	reviseLocalCopy(context,request, paramString);
			} else {
				String strBaseLCId = PGRTADCMDataLoad.getBaseLocalCopyInfoForMaster(context, strMceId);

				if (UIUtil.isNotNullAndNotEmpty(strBaseLCId)) {
					Map mapAuthorApprover = getAuthorApproverForCopyElement(context, strBaseLCId);
					String strAuthor = (String) mapAuthorApprover.get(RTAUtilConstants.KEY_AUTHOR);
					String strApprover = (String) mapAuthorApprover.get(RTAUtilConstants.KEY_APPROVER);
					String strReturnMsg = null;
					if (KEY_AUTHOR.equals(strAction)) {
						if (UIUtil.isNullOrEmpty(strAuthor) || (UIUtil.isNotNullAndNotEmpty(strAuthor)
								&& strAuthor.indexOf(context.getUser()) == -1)) {
							throw new Exception(EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
									context.getLocale(), "emxAWL.Error.AuthorNoAccessToPerformOperation"));
						}
						if (UIUtil.isNotNullAndNotEmpty(strAuthor)
								&& strAuthor.indexOf(PGWidgetConstants.KEY_COMMA_SEPARATOR) != -1) {
							strReturnMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
									context.getLocale(), "emxAWL.Error.alertMultipleAuthorApproverPresent");
						}
					} else if (KEY_APPROVE.equals(strAction)) {
						if (UIUtil.isNullOrEmpty(strApprover) || (UIUtil.isNotNullAndNotEmpty(strApprover)
								&& strApprover.indexOf(context.getUser()) == -1)) {
							throw new Exception(EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
									context.getLocale(), "emxAWL.Error.ApproverNoAccessToPerformOperation"));
						}
						if (UIUtil.isNotNullAndNotEmpty(strApprover)
								&& strApprover.indexOf(PGWidgetConstants.KEY_COMMA_SEPARATOR) != -1) {
							strReturnMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
									context.getLocale(), "emxAWL.Error.alertMultipleAuthorApproverPresent");
						}
					}
					Map<String, String> contentInfoMap = AWLUtil.getContentInformationForReviseDialog(context,
							strMceId);
					if (jsonInputData.containsKey(KEY_NEW_COPY_TEXT)) {
						contentInfoMap.put(KEY_NEW_COPY_TEXT, jsonInputData.getString(KEY_NEW_COPY_TEXT));
					} else {
						contentInfoMap.put(KEY_NEW_COPY_TEXT, contentInfoMap.get("currentCopyContent"));
					}
					if(jsonInputData.containsKey(RTAUtilConstants.KEY_SUBCOPYTYPE)) {
						contentInfoMap.put(RTAUtilConstants.KEY_SUBCOPYTYPE, jsonInputData.getString(RTAUtilConstants.KEY_SUBCOPYTYPE));
					}
					contentInfoMap.put(RTAUtilConstants.KEY_REASON, strReason);
					contentInfoMap.put(RTAUtilConstants.KEY_COMMENT, strComment);
					contentInfoMap.put(RTAUtilConstants.KEY_DEFECT_TYPE, strDefectType);
					contentInfoMap.put(KEY_RESPONSIBLE_FUNCTION, strResponsibleFunction);
					contentInfoMap.put(KEY_CONTENT_ID, strBaseLCId);
					contentInfoMap.put(PGWidgetConstants.KEY_OBJECT_ID, strBaseLCId);
					contentInfoMap.put(KEY_IS_GRAPHIC,
							"Yes".equals(contentInfoMap.get(KEY_IS_GRAPHIC_ELEMENT)) ? PGWidgetConstants.STRING_TRUE
									: PGWidgetConstants.STRING_FALSE);
					contentInfoMap.put("isComposite",
							"Yes".equals(contentInfoMap.get(KEY_COMPOSITE_COPY)) ? PGWidgetConstants.STRING_TRUE
									: PGWidgetConstants.STRING_FALSE);

					if (KEY_REVISE_MCE.equals(strAction)) {
						ArtworkContent baseLocalCopy = ArtworkContent.getNewInstance(context, strBaseLCId);
						String strAuthorId = baseLocalCopy.getAuthoringAssigneeId(context);
						String strApproverId = baseLocalCopy.getApprovalAssigneeId(context);
						if (UIUtil.isNotNullAndNotEmpty(strAuthorId)) {
							contentInfoMap.put("hiddenAuthorOID", strAuthorId);
						}
						if (UIUtil.isNotNullAndNotEmpty(strApproverId)) {
							contentInfoMap.put("hiddenApproverOID", strApproverId);
						}
					}

					logger.log(Level.INFO, "contentInfoMap :: {}", contentInfoMap);

					FormBean formBean = new FormBean();
					formBean.processForm(request.getSession(), request);
					Set<String> keySet = contentInfoMap.keySet();
					Iterator<String> itr = keySet.iterator();
					while (itr.hasNext()) {
						String key = itr.next();
						formBean.setElementValue(key, contentInfoMap.get(key));
					}
					ContextUtil.startTransaction(context, true);
					isTransactionActive = true;
					Map<String, FormBean> argsMap = new HashMap();
					argsMap.put("formBean", formBean);
					String[] args = JPO.packArgs(argsMap);
					HashMap<String, String> hMap = (HashMap) JPO.invoke(context, "AWLArtworkElement", null,
							"reviseArtworkElementOutsideArtworkPackage", args, Map.class);

					logger.log(Level.INFO, "revised hMap :: " + hMap);

					strMceId = (String) hMap.get(PGWidgetConstants.KEY_OBJECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strMceId)) {// action is not submit
						strBaseLCId = PGRTADCMDataLoad.getBaseLocalCopyInfoForMaster(context, strMceId);
						if (KEY_AUTHOR.equals(strAction)) {
							PGRTADCMDataLoad.promoteLCtoReviewReleaseState(context, strBaseLCId,
									DomainConstants.STATE_PART_PRELIMINARY);
						} else if (KEY_APPROVE.equals(strAction)) {
							if (UIUtil.isNotNullAndNotEmpty(strReturnMsg)) {
								ContextUtil.commitTransaction(context);
								return Json.createObjectBuilder()
										.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
										.add(PGWidgetConstants.KEY_MESSAGE, strReturnMsg).build().toString();
							}
							PGRTADCMDataLoad.promoteLCtoReviewReleaseState(context, strBaseLCId,
									DomainConstants.STATE_PART_PRELIMINARY);
							PGRTADCMDataLoad.promoteLCtoReviewReleaseState(context, strBaseLCId,
									DomainConstants.STATE_PART_REVIEW);
						}
					} else {
						isExceptionOccurred = true;
						if (isTransactionActive) {
							ContextUtil.abortTransaction(context);
						}
					}
					ContextUtil.commitTransaction(context);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			isExceptionOccurred = true;			
			sbMessage.append(e.getMessage()).append("\n");
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
			}
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
			return jsonStatus.build().toString();
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build()
				.toString();
	}

	private static Map<String, String> prepareContentMap(Context context, String strAuthor, JsonObject jsonInputData, String strMceId,
			String strReason, String strComment, String strDefectType, String strResponsibleFunction,
			String strBaseLCId) throws FrameworkException {
		Map<String, String> contentInfoMap = AWLUtil.getContentInformationForReviseDialog(context,
				strMceId);
		if (jsonInputData.containsKey(KEY_NEW_COPY_TEXT) && strAuthor.equalsIgnoreCase(context.getUser())) {
			contentInfoMap.put(KEY_NEW_COPY_TEXT, jsonInputData.getString(KEY_NEW_COPY_TEXT));
		} else {
			contentInfoMap.put(KEY_NEW_COPY_TEXT, contentInfoMap.get("currentCopyContent"));
		}
		contentInfoMap.put(RTAUtilConstants.KEY_REASON, strReason);
		contentInfoMap.put(RTAUtilConstants.KEY_COMMENT, strComment);
		contentInfoMap.put(RTAUtilConstants.KEY_DEFECT_TYPE, strDefectType);
		contentInfoMap.put(KEY_RESPONSIBLE_FUNCTION, strResponsibleFunction);

		contentInfoMap.put(KEY_CONTENT_ID, strBaseLCId);
		contentInfoMap.put(PGWidgetConstants.KEY_OBJECT_ID, strBaseLCId);
		contentInfoMap.put(KEY_IS_GRAPHIC,
				"Yes".equals(contentInfoMap.get(KEY_IS_GRAPHIC_ELEMENT)) ? PGWidgetConstants.STRING_TRUE
						: PGWidgetConstants.STRING_FALSE);
		contentInfoMap.put("isComposite",
				"Yes".equals(contentInfoMap.get(KEY_COMPOSITE_COPY)) ? PGWidgetConstants.STRING_TRUE
						: PGWidgetConstants.STRING_FALSE);
		contentInfoMap.put("isFromWidget", PGWidgetConstants.STRING_TRUE);
		return contentInfoMap;
	}

	private static String authorCase(Context context, String strAuthor, String strReturnMsg) throws Exception {
		if (UIUtil.isNullOrEmpty(strAuthor) || (UIUtil.isNotNullAndNotEmpty(strAuthor)
				&& strAuthor.indexOf(context.getUser()) == -1)) {
			throw new Exception(EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.Error.AuthorNoAccessToPerformOperation"));
		}
		if (UIUtil.isNotNullAndNotEmpty(strAuthor)
				&& strAuthor.indexOf(PGWidgetConstants.KEY_COMMA_SEPARATOR) != -1) {
			strReturnMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.Error.alertMultipleAuthorApproverPresent");
		}
		return strReturnMsg;
	}

	private static String approveCase(Context context, String strApprover, String strReturnMsg) throws Exception {
		if (UIUtil.isNullOrEmpty(strApprover) || (UIUtil.isNotNullAndNotEmpty(strApprover)
				&& strApprover.indexOf(context.getUser()) == -1)) {
			throw new Exception(EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.Error.ApproverNoAccessToPerformOperation"));
		}
		if (UIUtil.isNotNullAndNotEmpty(strApprover)
				&& strApprover.indexOf(PGWidgetConstants.KEY_COMMA_SEPARATOR) != -1) {
			strReturnMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.Error.alertMultipleAuthorApproverPresent");
		}
		return strReturnMsg;
	}

	private static void reviseMCE(Context context, String strBaseLCId, Map<String, String> contentInfoMap)
			throws FrameworkException {
		ArtworkContent baseLocalCopy = ArtworkContent.getNewInstance(context, strBaseLCId);
		String strAuthorId = baseLocalCopy.getAuthoringAssigneeId(context);
		String strApproverId = baseLocalCopy.getApprovalAssigneeId(context);
		if (UIUtil.isNotNullAndNotEmpty(strAuthorId)) {
			contentInfoMap.put("hiddenAuthorOID", strAuthorId);
		}
		if (UIUtil.isNotNullAndNotEmpty(strApproverId)) {
			contentInfoMap.put("hiddenApproverOID", strApproverId);
		}
	}
	
	
	public static String reviseLocalCopy(Context context, HttpServletRequest request, String paramString)
			throws Exception {
		boolean isExceptionOccurred = false;
		boolean isTransactionActive = false;
		StringBuilder sbMessage = new StringBuilder();
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strLCId = jsonInputData.getString(RTAUtilConstants.KEY_LCE_ID);
			String strReason = jsonInputData.getString(RTAUtilConstants.KEY_REASON);
			String strComment = jsonInputData.getString(RTAUtilConstants.KEY_COMMENT);
			String strDefectType = jsonInputData.getString(RTAUtilConstants.KEY_DEFECT_TYPE);
			String strResponsibleFunction = jsonInputData.getString(KEY_RESPONSIBLE_FUNCTION);
			
			Map<String, String> contentInfoMap = AWLUtil.getContentInformationForReviseDialog(context, strLCId);
				if(jsonInputData.containsKey(KEY_NEW_COPY_TEXT)) {
					contentInfoMap.put(KEY_NEW_COPY_TEXT, jsonInputData.getString(KEY_NEW_COPY_TEXT));
				}else {
					contentInfoMap.put(KEY_NEW_COPY_TEXT, contentInfoMap.get("currentCopyContent"));
				}
				contentInfoMap.put(RTAUtilConstants.KEY_REASON, strReason);
				contentInfoMap.put(RTAUtilConstants.KEY_COMMENT, strComment);
				contentInfoMap.put(RTAUtilConstants.KEY_DEFECT_TYPE, strDefectType);
				contentInfoMap.put(KEY_RESPONSIBLE_FUNCTION, strResponsibleFunction);

				contentInfoMap.put(KEY_CONTENT_ID, strLCId);
				contentInfoMap.put(PGWidgetConstants.KEY_OBJECT_ID, strLCId);
				contentInfoMap.put(KEY_IS_GRAPHIC,
						"Yes".equals(contentInfoMap.get(KEY_IS_GRAPHIC_ELEMENT)) ? PGWidgetConstants.STRING_TRUE : PGWidgetConstants.STRING_FALSE);
				contentInfoMap.put("isComposite",
						"Yes".equals(contentInfoMap.get(KEY_COMPOSITE_COPY)) ? PGWidgetConstants.STRING_TRUE : PGWidgetConstants.STRING_FALSE);
				
				ArtworkContent localCopy = ArtworkContent.getNewInstance(context, strLCId);
				String strAuthorId = localCopy.getAuthoringAssigneeId(context);
				String strApproverId = localCopy.getApprovalAssigneeId(context);
				if (UIUtil.isNotNullAndNotEmpty(strAuthorId)) {
					contentInfoMap.put("hiddenAuthorOID", strAuthorId);
				}
				if (UIUtil.isNotNullAndNotEmpty(strApproverId)) {
					contentInfoMap.put("hiddenApproverOID", strApproverId);
				}
				FormBean formBean = new FormBean();
				formBean.processForm(request.getSession(), request);
				Set<String> keySet = contentInfoMap.keySet();
				Iterator<String> itr = keySet.iterator();
				while (itr.hasNext()) {
					String key = itr.next();
					formBean.setElementValue(key, contentInfoMap.get(key));
				}

				ContextUtil.startTransaction(context, true);
				isTransactionActive = true;
				Map<String, FormBean> argsMap = new HashMap();
				argsMap.put("formBean", formBean);
				String[] args = JPO.packArgs(argsMap);
				HashMap<String, String> hMap = (HashMap) JPO.invoke(context, "AWLArtworkElement", null,
						"reviseArtworkElementOutsideArtworkPackage", args, Map.class);
                strLCId = (String) hMap.get(PGWidgetConstants.KEY_OBJECT_ID);
				
				if (UIUtil.isNullOrEmpty(strLCId)) {
					isExceptionOccurred = true;
					if (isTransactionActive) {
						ContextUtil.abortTransaction(context);
					}
				}
				ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			isExceptionOccurred = true;			
			sbMessage.append(e.getMessage()).append("\n");
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
			}
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
			return jsonStatus.build().toString();
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build()
				.toString();
	}

	public static String getWhereUsedAndLcFromMce(Context context, String paramString) throws Exception {
		String RELATIONSHIP_POACOUNTRY = AWLRel.POA_COUNTRY.get(context);
		String SELECT_POACOUNTRY = "from[" + RELATIONSHIP_POACOUNTRY + "].to.name";
		JsonObjectBuilder jsonObjHierPOALC = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectId = jsonInputData.getString(STR_MCIDS);
			String RELATIONSHIP_ARTWORKPACKAGECONTENT = AWLRel.ARTWORK_PACKAGE_CONTENT.get(context);
			String SELECT_PROJECTNAME = RTAUtilConstants.SELECT_RELPROJECTTOPOANAMEID + DomainObject.SELECT_NAME;
			String SELECT_PROJECTID = RTAUtilConstants.SELECT_RELPROJECTTOPOANAMEID + DomainObject.SELECT_ID;
			String SELECT_ARTPKGNAME = "to[" + RELATIONSHIP_ARTWORKPACKAGECONTENT + "].from."
					+ DomainConstants.SELECT_ATTRIBUTE_TITLE;
			String SELECT_ARTPKGID = "to[" + RELATIONSHIP_ARTWORKPACKAGECONTENT + "].from." + DomainObject.SELECT_ID;
			if (BusinessUtil.isNotNullOrEmpty(strObjectId)) {
				StringList slObjSel = new StringList(15);
				slObjSel.add(DomainConstants.SELECT_ID);
				slObjSel.add(DomainConstants.SELECT_NAME);
				slObjSel.add(DomainConstants.SELECT_TYPE);
				slObjSel.add(DomainConstants.SELECT_OWNER);
				slObjSel.add(DomainConstants.SELECT_REVISION);
				slObjSel.add(DomainConstants.SELECT_CURRENT);
				slObjSel.add(RTAUtilConstants.SELECT_ISBASECOPY);
				slObjSel.add(RTAUtilConstants.SELECT_PGAWLREFNO);
				slObjSel.add(RTAUtilConstants.SELECT_TITLE);
				slObjSel.add(RTAUtilConstants.SELECT_COPYTEXT);
				slObjSel.add(RTAUtilConstants.SELECT_MARKETINGNAME);
				slObjSel.add(RTAUtilConstants.SELECT_COPYTEXTLANG);
				slObjSel.add(RTAUtilConstants.SELECT_RELPROJECTTOPOA);
				slObjSel.add(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT);
				slObjSel.add(SELECT_PROJECTNAME);
				slObjSel.add(SELECT_PROJECTID);
				slObjSel.add(SELECT_ARTPKGNAME);
				slObjSel.add(SELECT_ARTPKGID);
				DomainConstants.MULTI_VALUE_LIST.add(SELECT_POACOUNTRY);
				slObjSel.add(SELECT_POACOUNTRY);
				DomainObject domPOALC = DomainObject.newInstance(context, strObjectId);
				Pattern relPattern = new Pattern(RTAUtilConstants.RELATIONSHIP_POAARTWORKMASTER);
				relPattern.addPattern(RTAUtilConstants.REL_ARTWORKELEMENTCONTENT);
				Pattern typePattern = new Pattern(RTAUtilConstants.TYP_POA);
				typePattern.addPattern(RTAUtilConstants.TYP_COPYELEMENT);
				boolean isFrom = true, isTo = true;
				MapList mcPOALCData = new MapList();
				Map<?, ?> eachPOALCData = null;
				JsonArrayBuilder jsonArrHierPOA = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrHierLC = Json.createArrayBuilder();
				String strType = null;
				mcPOALCData = domPOALC.getRelatedObjects(context, // context,
						relPattern.getPattern(), // relationshipPattern,
						typePattern.getPattern(), // typepattern
						slObjSel, // objectSelects
						null, // relationshipSelects,
						isFrom, // getFrom
						isTo, // getTo,
						(short) 1, // recurseToLevel,
						null, // objectWhere,
						null, // relWhere
						0); // limit
                getAuthorApproverPersonInfo(context, mcPOALCData);
				if (mcPOALCData.size() > 0) {
					for (int j = 0; j < mcPOALCData.size(); j++) {
						eachPOALCData = (Map<?, ?>) mcPOALCData.get(j);
						strType = (String) eachPOALCData.get(DomainConstants.SELECT_TYPE);
						if (strType.equals(RTAUtilConstants.TYP_POA)) {
							JsonObjectBuilder mcPoaData = Json.createObjectBuilder();
							mcPoaData.add(DomainConstants.SELECT_ID,
									(String) eachPOALCData.get(DomainConstants.SELECT_ID));
							mcPoaData.add(DomainConstants.SELECT_NAME,
									(String) eachPOALCData.get(DomainConstants.SELECT_NAME));
							mcPoaData.add(KEY_TITLE, (String) eachPOALCData.get(RTAUtilConstants.SELECT_TITLE));
							mcPoaData.add(KEY_LOCALCOPYCONTENT,
									(String) eachPOALCData.get(RTAUtilConstants.SELECT_COPYTEXT));
							mcPoaData.add(KEY_LOCALCOPYLANG,
									(String) eachPOALCData.get(RTAUtilConstants.SELECT_COPYTEXTLANG));
							mcPoaData.add(KEY_STATE, (String) eachPOALCData.get(DomainConstants.SELECT_CURRENT));
							mcPoaData.add(DomainConstants.SELECT_OWNER,
									(String) eachPOALCData.get(DomainConstants.SELECT_OWNER));
							mcPoaData.add(KEY_PROJWONER, PGWidgetUtil.checkNullValueforString(
									(String) eachPOALCData.get(RTAUtilConstants.SELECT_RELPROJECTTOPOA)));
							mcPoaData.add(KEY_PROJNAME, PGWidgetUtil
									.checkNullValueforString((String) eachPOALCData.get(SELECT_PROJECTNAME)));
							mcPoaData.add(KEY_ARTWORKPKGNAME, PGWidgetUtil
									.checkNullValueforString((String) eachPOALCData.get(SELECT_ARTPKGNAME)));
							mcPoaData.add(KEY_COUNTRIES,
									PGWidgetUtil
											.checkNullValueforString(PGWidgetUtil.extractMultiValueSelect(eachPOALCData, SELECT_POACOUNTRY)));
							jsonArrHierPOA.add(mcPoaData);
						} else {
							String lcType = (String) eachPOALCData.get(DomainConstants.SELECT_TYPE);

							JsonObjectBuilder mcLcData = Json.createObjectBuilder();
							mcLcData.add(DomainConstants.SELECT_ID,
									(String) eachPOALCData.get(DomainConstants.SELECT_ID));
							mcLcData.add(DomainConstants.SELECT_NAME,
									(String) eachPOALCData.get(DomainConstants.SELECT_NAME));
							mcLcData.add(DomainConstants.SELECT_TYPE,
									(String) eachPOALCData.get(DomainConstants.SELECT_TYPE));
							mcLcData.add(KEY_COPYTEXT, (String) eachPOALCData.get(RTAUtilConstants.SELECT_COPYTEXT));
							mcLcData.add(KEY_ISMASTERCOPY,
									(String) eachPOALCData.get(RTAUtilConstants.SELECT_ISBASECOPY));
							mcLcData.add(DomainConstants.SELECT_REVISION,
									(String) eachPOALCData.get(DomainConstants.SELECT_REVISION));
							mcLcData.add(KEY_STATE, (String) eachPOALCData.get(DomainConstants.SELECT_CURRENT));
							mcLcData.add(DomainConstants.SELECT_OWNER,
									(String) eachPOALCData.get(DomainConstants.SELECT_OWNER));
							mcLcData.add(KEY_REFERENCENUMBER, (String) eachPOALCData.get(RTAUtilConstants.SELECT_PGAWLREFNO));
							if (lcType.equals(RTAUtilConstants.TYP_STRUCMASTERARTELETYPE)) {
								mcLcData.add(KEY_LANGMARKETINGNAME,
										(String) eachPOALCData.get(RTAUtilConstants.SELECT_MARKETINGNAME));
							} else {
								mcLcData.add(KEY_LANGMARKETINGNAME,
										(String) eachPOALCData.get(RTAUtilConstants.SELECT_COPYTEXTLANG));
							}
							 Object value = eachPOALCData.get(RTAUtilConstants.KEY_INACTIVE_USERS);
							 if (value instanceof StringList) {
								 List<?> listValue = (List<?>) value;
								 String strInactiveUsers = listValue.stream()
				                           .map(Object::toString)
				                           .collect(Collectors.joining(", "));
								 mcLcData.add(RTAUtilConstants.KEY_INACTIVE_USERS, (String)strInactiveUsers);
							 } else if (value instanceof String) {
								 mcLcData.add(RTAUtilConstants.KEY_INACTIVE_USERS, (String)value);
							 }
							mcLcData.add(RTAUtilConstants.ATTR_PG_CONFIRM_ASSIGNMENT, (String) eachPOALCData.get(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT));
							Map mapTemp = getAuthorApproverForCopyElement(context, (String) eachPOALCData.get(DomainConstants.SELECT_ID));
							if (mapTemp.containsKey(RTAUtilConstants.KEY_AUTHOR)) {
								mcLcData.add(RTAUtilConstants.KEY_AUTHOR, (String)mapTemp.get(RTAUtilConstants.KEY_AUTHOR));
							} 
							if (mapTemp.containsKey(RTAUtilConstants.KEY_APPROVER)) {
								mcLcData.add(RTAUtilConstants.KEY_APPROVER, (String)mapTemp.get(RTAUtilConstants.KEY_APPROVER));
							}
							jsonArrHierLC.add(mcLcData);
						}
					}
				}
				jsonObjHierPOALC.add(KEY_POAS, jsonArrHierPOA);
				jsonObjHierPOALC.add(KEY_LOCALCOPIES, jsonArrHierLC);
			}
		} catch (Exception et) {
			throw et;
		} finally {
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_POACOUNTRY);
		}
		return jsonObjHierPOALC.build().toString();
	}
	
	public static String confirmAssignmentCopyElement(Context context, String strMceId, String strLCEIds) throws Exception {
		// String strOut = DomainConstants.EMPTY_STRING;
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			if(!UIUtil.isNotNullAndNotEmpty(strMceId)) {
				return "MCE Id cannot be null/empty";
			}
			StringList slLCEIds = FrameworkUtil.split(strLCEIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			ContextUtil.startTransaction(context, true);

			String strBaseLCId = PGRTADCMDataLoad.getBaseLocalCopyInfoForMaster(context, strMceId);
			if(slLCEIds != null && !slLCEIds.contains(strBaseLCId)) {
				slLCEIds.add(strBaseLCId);
			}
			String strAttrConfirmAAAssignee = PropertyUtil.getSchemaProperty(context,
					"attribute_pgConfirmAuthorApproverAssignee");
			String strLabelReviewed = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
					context.getLocale(), "emxAWL.ConfirmAssignment.REVIEWED");
			Map<String, String> requestMap = new HashMap<>();
			requestMap.put(DomainObject.SELECT_ID, strMceId);
			String responseText = (String) JPO.invoke(context, "pgRTA_Util", null, "checkMCInactiveAssignee",
					JPO.packArgs(requestMap), String.class);
			if (!responseText.equals(DomainObject.EMPTY_STRING)) {
				responseText = (EnoviaResourceBundle.getProperty(context, "emxAWLStringResource", context.getLocale(),
						"emxAWL.Alert.LCInActiveAssignee")).concat(responseText);
				MqlUtil.mqlCommand(context, com.matrixone.apps.awl.util.AWLConstants.MQL_ERROR, responseText);
			}
			DomainObject domMC = DomainObject.newInstance(context, strMceId);
			boolean isContextPushed = false;
			try {
				// RTA users do not have access on DCM Data so need to push context
				if (Access.isArtworkProjectManager(context) || Access.isProductManager(context)) {
					ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT, null,
							ArtworkConstants.VAULT_ESERVICE_PRODUCTION);
					isContextPushed = true;
				}
				domMC.setAttributeValue(context, strAttrConfirmAAAssignee, strLabelReviewed);
				if (slLCEIds != null && !slLCEIds.isEmpty()) {
					for(int i=0 ; i<slLCEIds.size() ; i++) {
						BusinessUtil.setAttribute(context, slLCEIds.get(i), strAttrConfirmAAAssignee, strLabelReviewed);
					}
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
				}
			}
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			// strOut = PGWidgetConstants.KEY_SUCCESS;
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			output.add(PGWidgetConstants.KEY_ERROR, PGWidgetConstants.KEY_SUCCESS);
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			throw e;
		}
		return output.build().toString();
	}
	
	public static String assignAuthorApproverCopyElement(Context context, HttpServletRequest request,
			String paramString) throws Exception {
		String strOut = DomainConstants.EMPTY_STRING;
		ArtworkContent artworkCE = null;
		Map mBaseInfo = null;
		String strBaseCEId = null;
		String strBaseState = null;
		String strLCEId = null;
		StringList slBaseCEIds = new StringList();
		StringList slFinalBaseLCs = new StringList();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			/*
			 * { "AuthorPersonOID" "ApproverPersonOID" "AuthorRouteTemplateOID"
			 * "ApproverRouteTemplateOID" "AssignAuthorAsApproverHiddenValue" }
			 */
			Map mapInput = PGWidgetUtil.getMapFromJson(context, jsonInputData);
			String strMessage = null;
			String strMode = (String) mapInput.get("mode");

			String strTokenName = (String) request.getHeader("csrfTokenName");
			String strCSRFValue = (String) request.getHeader(strTokenName);

			mapInput.put("csrfTokenName", strTokenName);
			mapInput.put(strTokenName, strCSRFValue);

			if (jsonInputData.containsKey(RTAUtilConstants.KEY_MCE_ID)) {
				mapInput.put(AWLConstants.OBJECT_ID, jsonInputData.getString(RTAUtilConstants.KEY_MCE_ID));
				String strBaseLCId = PGRTADCMDataLoad.getBaseLocalCopyInfoForMaster(context,
						jsonInputData.getString(RTAUtilConstants.KEY_MCE_ID));
				mapInput.put(AWLConstants.SEL_ROW_IDs, strBaseLCId);
			} else if (jsonInputData.containsKey(RTAUtilConstants.KEY_LCE_IDS)) {
				StringList slIds = FrameworkUtil.split(jsonInputData.getString(RTAUtilConstants.KEY_LCE_IDS),
						PGWidgetConstants.KEY_COMMA_SEPARATOR);
				StringList slValidList = getValidIdsForAssignAA(context, slIds, strMode);
				if (slIds.size() > slValidList.size()) {
					strMessage = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource", context.getLocale(),
							"emxAWL.Message.alertCopyElementInvalidForAssignment");
				}
				mapInput.put(AWLConstants.SEL_ROW_IDs, slValidList.join(PGWidgetConstants.KEY_COMMA_SEPARATOR));
			}

			HashMap<String, Map<String, Object>> mapArgs = new HashMap<>();
			mapArgs.put("requestMap", mapInput);
			strMessage = doArtworkContentAssignment(context, JPO.packArgs(mapArgs));
			JSONObject jsonObject = new JSONObject(strMessage);

			if (UIUtil.isNotNullAndNotEmpty(strMessage)) {
				strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
						.add(PGWidgetConstants.KEY_MESSAGE, jsonObject.getString(PGWidgetConstants.KEY_MESSAGE))
						.add(KEY_JOB_STATUS, jsonObject.getString(KEY_JOB_STATUS))
						.add(KEY_JOB_ID, jsonObject.getString(KEY_JOB_ID)).build().toString();
			} else {
				strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
						.build().toString();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_MESSAGE, e.getMessage()).build().toString();
		}
		return strOut;
	}
	
	public static String doArtworkContentAssignment(Context context, String[] args) throws FrameworkException {
		boolean bIsContextPushed = false;
		String strOut = new String();
		try {
			//Added for New Req Claims LCE Assign A/A starts
			Map programMap = (Map) JPO.unpackArgs(args);
			Map requestMap = (Map) programMap.get("requestMap");
			String strIsFromClaimsPage = (String) requestMap.get("claimsPage");
			String strObjId = "";
			DomainObject domObj = null;
			StringList slIdsToRemoveList = new StringList();
			StringList slRouteList = new StringList();
			//Added for New Req Claims LCE Assign A/A ends
			
			Map assigneeInfo = getAssigneeDetails(context, args);
			StringList idList = BusinessUtil.getStringList(assigneeInfo,AWLConstants.SEL_ROW_IDs);
			//RTA DS - CW 22x-01 - Added for ALM-44247 - Start
			boolean isFromClaims = false;
			StringList slExcludeCEIds = new StringList(); 
			//RTA DS - CW 22x-01 - Added for ALM-44247 - End
			//Added for New Req Claims LCE Assign A/A starts
			logger.log(Level.INFO, "1 Context User Info Method :: doArtworkContentAssignment()---------------> - {0}", context.getUser());
			if(BusinessUtil.isNotNullOrEmpty(strIsFromClaimsPage) && "true".equals(strIsFromClaimsPage))
			{
				//RTA DS - CW 22x-01 - Added for ALM-44247 - Start
				isFromClaims = true;
				//RTA DS - CW 22x-01 - Added for ALM-44247 - End
				if (!ArtworkConstants.PERSON_USERAGENT.equalsIgnoreCase(context.getUser())) {
					ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT, null, context.getVault().getName());
					bIsContextPushed = true;
				}	
				for(int j=0;j<idList.size();j++)
				{
					strObjId = (String) idList.get(j);
					domObj = DomainObject.newInstance(context,strObjId);
					if(!(domObj.isKindOf(context, AWLType.COPY_ELEMENT.get(context))))
					{
						slIdsToRemoveList.add(strObjId);
					} else if(domObj.isKindOf(context, AWLType.COPY_ELEMENT.get(context))) {
						logger.log(Level.INFO, "Inside Method :: doArtworkContentAssignment() -- Before slRouteList ---------------> - {0}", slRouteList);
						slRouteList = domObj.getInfoList(context,"from["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].to.id");
						logger.log(Level.INFO, "Inside Method :: doArtworkContentAssignment() -- After slRouteList ---------------> - {0}", slRouteList);
						if(slRouteList.size()>0)
						{
							slIdsToRemoveList.add(strObjId);
						}
					}
				}
				if(bIsContextPushed){
					ContextUtil.popContext(context);
					bIsContextPushed = false;
				}				
				if(slIdsToRemoveList.size()>0)
				{
					idList.removeAll(slIdsToRemoveList);
				}
			}
			//Added for New Req Claims LCE Assign A/A ends
			String author = BusinessUtil.getString(assigneeInfo,AWLRel.ARTWORK_CONTENT_AUTHOR.get(context));
			String approver = BusinessUtil.getString(assigneeInfo,AWLRel.ARTWORK_CONTENT_APPROVER.get(context));
			//RTA 18x.1 - ADDED FOR ALM-25409 START
			author = getAbsenceDelegate(context,author);
			approver = getAbsenceDelegate(context,approver);
			//RTA 18x.1 - ADDED FOR ALM-25409 END
			
			//P&G RTA: CODE START
			author	= getRouteTemplateId(context, author, false);
			approver= getRouteTemplateId(context, approver, true);
			//P&G RTA: CODE END
			//RTA DS - CW 22x-01 - Modified for ALM-44247 - Start
			MapList mlCEInfo = null;
			//if(isFromClaims) {
				String strAttrpgRTABGJobStatus = PropertyUtil.getSchemaProperty(context, "attribute_pgRTABGJobStatus");
				String strSelAttrpgRTABGJobStatus = DomainObject.getAttributeSelect(strAttrpgRTABGJobStatus);
				String strPGRTABGJobStatusVal = null;
				StringList strBusSelList = new StringList(DomainConstants.SELECT_CURRENT);
				strBusSelList.add(DomainConstants.SELECT_ID);
				strBusSelList.add(strSelAttrpgRTABGJobStatus);
				mlCEInfo = BusinessUtil.getInfo(context, idList, strBusSelList);
				Iterator<?> itr = mlCEInfo.iterator();
				Map<?,?> mCEInfo = null;
				while(itr.hasNext()) {
					mCEInfo = (Map)itr.next();
					strPGRTABGJobStatusVal = (String)mCEInfo.get(strSelAttrpgRTABGJobStatus);
					if(BusinessUtil.isNotNullOrEmpty(strPGRTABGJobStatusVal) && strPGRTABGJobStatusVal.indexOf("Submitted : ") != -1) {
						slExcludeCEIds.add((String)mCEInfo.get(DomainConstants.SELECT_ID));
					}
				}
				idList.removeAll(slExcludeCEIds);
			//}
			for (int i = 0; i < idList.size(); i++) {
				ArtworkContent element = ArtworkContent.getNewInstance(context, (String) idList.get(i));
				element.updateAssignee(context, author, approver);
			}
			logger.log(Level.INFO, "3 Context User Info Method :: doArtworkContentAssignment()---------------> - {0}", context.getUser());
			logger.log(Level.INFO, "Inside Method :: doArtworkContentAssignment() -- idList ---------------> - {0}", idList);
			logger.log(Level.INFO, "4 Context User Info Method :: doArtworkContentAssignment()---------------> - {0}", context.getFrameContext(context.getSession().toString()).getUser());
			String strMessage = new String();
			    if(BusinessUtil.isNotNullOrEmpty(idList)) {
				Object[] objectArray = new Object[] {context , author, approver, requestMap, idList};
				Class[] objectTypeArray = new Class[] {matrix.db.Context.class, String.class, String.class, Map.class, StringList.class};
				com.matrixone.apps.domain.util.BackgroundProcess backgroundProcess = new com.matrixone.apps.domain.util.BackgroundProcess();
				
				backgroundProcess.submitJob(context,(Object)new RTAUtil(context, null),"startAuthorApprovalRoutesForCE",objectArray, objectTypeArray);		
				strMessage = EnoviaResourceBundle.getProperty(context,"emxAWLStringResource",context.getLocale(),"emxAWL.AssignAuthorApprover.BackgroundJob");
				strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
						.add(PGWidgetConstants.KEY_MESSAGE, strMessage).build().toString();
				MqlUtil.mqlCommand(context, "notice $1", strMessage);
			    }else {
			    strMessage = EnoviaResourceBundle.getProperty(context,"emxAWLStringResource",context.getLocale(),"emxAWL.AssignAuthorApprover.BackgroundJobNotCreated");
			    strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
						.add(PGWidgetConstants.KEY_MESSAGE, strMessage).build().toString();
			    }
			    return strOut;
			//RTA DS - CW 22x-01 - Modified for ALM-44247 - End
		} catch (Exception e) { throw new FrameworkException(e); }
		finally {
			if(bIsContextPushed){
				ContextUtil.popContext(context);
				bIsContextPushed = false;
			}			
		}
	}
	
	//RTA DS - CW 22x-01 - Added for ALM-44247 - Start
		/**
		 * Method to set the Author and Approver on LC. Start the authoring task.
		 * @param context
		 * @param strCEId - LC id
		 * @param strAuthor - Author name
		 * @param strApprover - Approver name
		 * @param requestMap - Details entered in form
		 * @throws Exception
		 */
		public static void startAuthorApprovalRoutesForCE(Context context, String strAuthor, String strApprover, Map<?,?> requestMap, StringList slCEIds) throws Exception{
			String strCEId = null;
			String strAttrpgRTABGJobStatus = PropertyUtil.getSchemaProperty(context, "attribute_pgRTABGJobStatus");
			try {
				
				String strAuthorDueDate = (String) requestMap.get("AuthorDueDate");
				String strApproveDueDate = (String) requestMap.get("ApproveDueDate");
				String strAuthorDuration = (String) requestMap.get("AuthorDuration");
				String strApproveDuration = (String) requestMap.get("ApproveDuration");
				String strRouteOwner = context.getUser();
				String strRouteState = null;
				
				String strCEState = null;
				String strBaseCEId = null;
				String strBaseState = null;
				
				Map mCEAuthorRouteInfo = null;
				Map mCEApproveRouteInfo = null;
				
				
				Calendar calendar = new java.util.GregorianCalendar();
				int iHour = calendar.get( Calendar.HOUR );
				int iAMPM = calendar.get( Calendar.AM_PM );
				//If the server time is greater than 5PM and before 11:59 PM then set the actual start date to next day.
				if( iAMPM > 0 && iHour > 5){
					calendar.add(Calendar.DATE, 1);					
				}
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.HOUR_OF_DAY, 17);			
				Date sysDate = calendar.getTime();
				Calendar cAuthorDueDate = null;
				
				Long lDuration = null;
				DateFormat formatter = DateFormat.getDateInstance(eMatrixDateFormat.getEMatrixDisplayDateFormat(), context.getLocale());
				SimpleDateFormat sdFormatter = new SimpleDateFormat(eMatrixDateFormat.strEMatrixDateFormat, Locale.US);
				
				Date dAuthorDueDate = null;

				if(BusinessUtil.isNullOrEmpty(strAuthorDuration) && BusinessUtil.isNullOrEmpty(strAuthorDueDate)) {
					strAuthorDuration = getDefaultAuthorApprovalDuration(context, AWLConstants.LOCAL_COPY_AUTHORING);					
				}
				
				if(BusinessUtil.isNullOrEmpty(strAuthorDueDate)) {
					lDuration = Long.valueOf(strAuthorDuration);
					strAuthorDueDate = getDateAfterAddingXDuration(sysDate,lDuration);
				} else if(BusinessUtil.isNotNullOrEmpty(strAuthorDueDate)) {			
					SimpleDateFormat sDateformatter = new SimpleDateFormat(eMatrixDateFormat.strEMatrixDateFormat, Locale.US);
					dAuthorDueDate = formatter.parse(strAuthorDueDate);
					calendar.setTime(dAuthorDueDate);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.HOUR_OF_DAY, 17);				
					strAuthorDueDate = sDateformatter.format(calendar.getTime());
					strAuthorDuration = Long.toString((com.matrixone.apps.domain.util.DateUtil.computeDuration(sysDate, calendar.getTime())));
				}
				
				
				if(BusinessUtil.isNullOrEmpty(strApproveDueDate) && BusinessUtil.isNullOrEmpty(strApproveDuration)) {
					strApproveDuration = getDefaultAuthorApprovalDuration(context, AWLConstants.LOCAL_COPY_APPROVAL);
				} else if(BusinessUtil.isNotNullOrEmpty(strApproveDueDate)) {
					if(dAuthorDueDate == null) {
						dAuthorDueDate = sdFormatter.parse(strAuthorDueDate);
					}
					cAuthorDueDate = new java.util.GregorianCalendar();
					cAuthorDueDate.setTime(dAuthorDueDate);
					cAuthorDueDate.set(Calendar.MINUTE, 0);
					cAuthorDueDate.set(Calendar.SECOND, 0);
					cAuthorDueDate.set(Calendar.HOUR_OF_DAY, 17);	
					cAuthorDueDate.set(Calendar.AM_PM, 0);
					cAuthorDueDate.set(Calendar.HOUR, 0);
					cAuthorDueDate.set(Calendar.MILLISECOND, 0);
					
					Date dApproveDueDate = formatter.parse(strApproveDueDate);
					
					Calendar cApproveDueDate = new java.util.GregorianCalendar();
					cApproveDueDate.setTime(dApproveDueDate);
					cApproveDueDate.set(Calendar.MINUTE, 0);
					cApproveDueDate.set(Calendar.SECOND, 0);
					cApproveDueDate.set(Calendar.HOUR_OF_DAY, 17);	
					cApproveDueDate.set(Calendar.AM_PM, 0);
					cApproveDueDate.set(Calendar.HOUR, 0);
					cApproveDueDate.set(Calendar.MILLISECOND, 0);
					
					strApproveDuration = Long.toString(com.matrixone.apps.domain.util.DateUtil.computeDuration(cAuthorDueDate.getTime(), cApproveDueDate.getTime()) - 1);				
				}
				if(BusinessUtil.isNotNullOrEmpty(strApproveDuration) && (Integer.parseInt(strApproveDuration) == 0)) {
					strApproveDuration = getDefaultAuthorApprovalDuration(context, AWLConstants.LOCAL_COPY_APPROVAL);
				}
				
				MapList mlCEInfo = BusinessUtil.getInfo(context, slCEIds, StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT));
				Map mCEInfo = null;
				Iterator itr = mlCEInfo.iterator();
				ArtworkContent artworkCE = null;
				Map mAuthorApproverRoutes = null;
				Map mBaseInfo = null;
				MapList mlAuthoringRoutes = null;
				MapList mlApproveRoutes = null;
				String strCEStatePreliminary = AWLState.PRELIMINARY.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT);
				String strCEStateReleased = AWLState.RELEASE.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT);
				StringList slFinalCEIds = new StringList();
				StringList slFinalCreateStartRoutesCEIds = new StringList();
				Map<String, String> mNonBaseToBase = new HashMap<>();
				SimpleDateFormat sdfFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), context.getLocale());
				String strCurrentFormatDate = sdfFormat.format(java.util.Calendar.getInstance().getTime());	
				String strBGSubmitted = strBGSubmittedPref + strCurrentFormatDate;			
				while(itr.hasNext()) {
					mCEInfo = (Map)itr.next();
					strCEId = (String)mCEInfo.get(DomainConstants.SELECT_ID);
					strCEState = (String)mCEInfo.get(DomainConstants.SELECT_CURRENT);
					if(BusinessUtil.isNotNullOrEmpty(strCEId) && BusinessUtil.isNotNullOrEmpty(strCEState) && strCEState.equals(strCEStatePreliminary)) {
						artworkCE = ArtworkContent.getNewInstance(context, strCEId);
						mBaseInfo = artworkCE.getBaseContent(context).getInfo(context, StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT));
						strBaseCEId = (String)mBaseInfo.get(DomainConstants.SELECT_ID);
						strBaseState = (String)mBaseInfo.get(DomainConstants.SELECT_CURRENT);
						if(!strCEId.equals(strBaseCEId) && strCEStateReleased.equals(strBaseState)) {
							slFinalCEIds.add(strCEId);
							slFinalCreateStartRoutesCEIds.add(strCEId);
							BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, strBGSubmitted);
						}
						if(strCEId.equals(strBaseCEId) && strCEStatePreliminary.equals(strBaseState)) {
							slFinalCEIds.add(strCEId);
							slFinalCreateStartRoutesCEIds.add(strCEId);
							BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, strBGSubmitted);
						}
						if(!strCEId.equals(strBaseCEId) && !strCEStateReleased.equals(strBaseState)) {
							slFinalCEIds.add(strCEId);
							mNonBaseToBase.put(strCEId, strBaseCEId);
							BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, strBGSubmitted);
						}
					}
				}
				slFinalCEIds = BusinessUtil.toUniqueList(slFinalCEIds);
				slFinalCreateStartRoutesCEIds = BusinessUtil.toUniqueList(slFinalCreateStartRoutesCEIds);
				logger.log(Level.INFO, "1 Context User Info Method :: startAuthorApprovalRoutesForCE()---------------> - {0}", context.getUser());
				for(Object oCEId : slFinalCEIds) {
					strCEId = (String)oCEId;
					RouteUtil.createArtworkRoutes(context, strRouteOwner, strCEId);
					artworkCE = ArtworkContent.getNewInstance(context, strCEId);
					mBaseInfo = artworkCE.getBaseContent(context).getInfo(context, StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT));
					strBaseCEId = (String)mBaseInfo.get(DomainConstants.SELECT_ID);

					Map mCERouteAndRouteNodeInfo = getCEConnectedRoutes(context, strCEId);
					mlAuthoringRoutes = (MapList) mCERouteAndRouteNodeInfo.get("AuthorRoutes");
					mlApproveRoutes = (MapList)mCERouteAndRouteNodeInfo.get("ApproverRoutes");
					
					//Update Authoring Details			
					if(BusinessUtil.isNotNullOrEmpty(mlAuthoringRoutes)) {
						mlAuthoringRoutes.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
						mCEAuthorRouteInfo = (Map)mlAuthoringRoutes.get(0);
						strRouteState = (String)mCEAuthorRouteInfo.get(DomainConstants.SELECT_CURRENT);
						if(BusinessUtil.isNotNullOrEmpty(strRouteState) && DomainConstants.STATE_ROUTE_DEFINE.equals(strRouteState)) {
							updateAuthorApproverRouteNodeDCM(context, mCEAuthorRouteInfo, strAuthorDueDate, strAuthorDuration, false);	
						}
					}
					//Update Approval Details
					if(BusinessUtil.isNotNullOrEmpty(mlApproveRoutes)) {
						mlApproveRoutes.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
						mCEApproveRouteInfo = (Map)mlApproveRoutes.get(0);
						strRouteState = (String)mCEApproveRouteInfo.get(DomainConstants.SELECT_CURRENT);				
						if(BusinessUtil.isNotNullOrEmpty(strRouteState) && DomainConstants.STATE_ROUTE_DEFINE.equals(strRouteState)) {
							updateAuthorApproverRouteNodeDCM(context, mCEApproveRouteInfo, null, strApproveDuration, true);
						}				
					}	
					if(!slFinalCreateStartRoutesCEIds.contains(strCEId)) {
						BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, DomainConstants.EMPTY_STRING);
					}
					
				}
				for(Object oCEId : slFinalCreateStartRoutesCEIds) {
					strCEId = (String)oCEId;
					RouteUtil.startArtworkAuthoringRoute(context, strCEId);
					BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, DomainConstants.EMPTY_STRING);
				}
				
				String strNonBaseId = null;
				Set<String> keySet = mNonBaseToBase.keySet();
				Iterator<String> itrNonBase = keySet.iterator();
				while (itrNonBase.hasNext()) {
					strNonBaseId = itrNonBase.next();
					strBaseCEId = mNonBaseToBase.get(strNonBaseId);
					strBaseState = BusinessUtil.getInfo(context, strBaseCEId, DomainConstants.SELECT_CURRENT);
					if(strCEStateReleased.equals(strBaseState)){
						RouteUtil.startArtworkAuthoringRoute(context, strNonBaseId);
					}
				}					
				logger.log(Level.INFO, "2 Context User Info Method :: startAuthorApprovalRoutesForCE()---------------> - {0}", context.getUser());
			}catch(Exception ex) {
				if(BusinessUtil.isNotNullOrEmpty(strCEId)) {
					BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, "Error :"+ex.getLocalizedMessage());
				}
				//IMPORTANT DO NOT THROW EXCEPTION
				logger.log(Level.INFO, "Error occured while executing method pgRTADCMUtil::startAuthorApprovalRoutesForCE: ", ex);
			}
		}
		//RTA DS - CW 22x-01 - Added for ALM-44247 - End
	
	
	public static String getDateAfterAddingXDuration(Date dStartDate, Long lDuration) {

		String strReturnDate = "";

		try	{

			SimpleDateFormat formatter = new SimpleDateFormat(eMatrixDateFormat.strEMatrixDateFormat, Locale.US);

			GregorianCalendar cal = new GregorianCalendar();

			Date dFinishDate = DateUtil.computeFinishDate(dStartDate, (lDuration*24*3600*1000));

			cal.setTime(dFinishDate);

			strReturnDate = formatter.format(cal.getTime());

		} catch (Exception e) {

			throw e;

		}

		return strReturnDate;

	}
	
	//RTA 18x.6 - ALM-36145 - Starts	
		/**	
		 * Method to get the default MC or LC Author or Approval duration.
		 * @param   context
		 * @param   strArtworkInfo
		 */		
		public static String getDefaultAuthorApprovalDuration(Context context, String strArtworkInfo) throws Exception{						
			try
			{
				String strDefaultDueDateOffset = null;
				if(BusinessUtil.isNotNullOrEmpty(strArtworkInfo)){
					if(strArtworkInfo.equals(AWLConstants.MASTER_COPY_AUTHORING)){
						strDefaultDueDateOffset = EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.DefaultMCEAuthoringDurationInDays");
					}else if(strArtworkInfo.equals(AWLConstants.MASTER_COPY_APPROVAL)){
						strDefaultDueDateOffset = EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.DefaultMCEApprovalDurationInDays");
					}else if(strArtworkInfo.equals(AWLConstants.LOCAL_COPY_AUTHORING)){
						strDefaultDueDateOffset = EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.DefaultLCEAuthoringDurationInDays");
					}else if(strArtworkInfo.equals(AWLConstants.LOCAL_COPY_APPROVAL)){
						strDefaultDueDateOffset = EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.DefaultLCEApprovalDurationInDays");
					}			
				}			
				if(BusinessUtil.isNullOrEmpty(strDefaultDueDateOffset)){
					strDefaultDueDateOffset = EnoviaResourceBundle.getProperty(context, "emxAWL.ArtworkTask.DueDateOffset");
				}
				return strDefaultDueDateOffset;
			} catch(Exception ex) {
				throw new FrameworkException(ex);
			}
		}
		//RTA 18x.6 - ALM-36145 - Ends	
	
	//RTA DS - CW 22x-01 - Added for ALM-44247 - Start
		/**
		 * Method to get CEs connected to Route
		 * @param context
		 * @param strCEId - CE Id
		 * @param strWhereParam - Where clause
		 * @return - Routes list
		 * @throws Exception
		 */
		private static Map getCEConnectedRoutes(Context context, String strCEId) throws Exception{
			Map mAARouteInfoList = new HashMap();
			if(BusinessUtil.isNotNullOrEmpty(strCEId)) {
				MapList mlAuthorRoutes = new MapList();
				MapList mlApproverRoutes = new MapList();
				String strSelAttrArtworkInfo = DomainObject.getAttributeSelect(AWLAttribute.ARTWORK_INFO.get(context));
				String strSelAttriDuteDateOffset = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
				StringList strBusSelectList = new StringList();
				strBusSelectList.add(DomainConstants.SELECT_ID);
				strBusSelectList.add(DomainConstants.SELECT_NAME);
				strBusSelectList.add(DomainConstants.SELECT_ORIGINATED);
				strBusSelectList.add(DomainConstants.SELECT_CURRENT);
				strBusSelectList.add(strSelAttrArtworkInfo);	
				strBusSelectList.add(DomainConstants.SELECT_TYPE);
				
				StringList strRelSelList = new StringList();
				strRelSelList.add(DomainRelationship.SELECT_ID);
				strRelSelList.add(DomainConstants.SELECT_FROM_ID);
				strRelSelList.add(DomainRelationship.SELECT_NAME);
				strRelSelList.add(PHYSICALID_CONNECTION);
				strRelSelList.add(SEL_ATTR_SCHEDULED_COMPLETION_DATE);
				strRelSelList.add(strSelAttriDuteDateOffset);

				Pattern typePattern = new Pattern(DomainConstants.TYPE_ROUTE);
				typePattern.addPattern(DomainConstants.TYPE_PERSON);

				Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_OBJECT_ROUTE);
				relPattern.addPattern(DomainConstants.RELATIONSHIP_ROUTE_NODE);
				
				DomainObject domCE = DomainObject.newInstance(context, strCEId);
				MapList mlRoutesInfo = domCE.getRelatedObjects(context, 
										relPattern.getPattern(), //Rel pattern
										typePattern.getPattern(), //Bus pattern
										strBusSelectList, //Bus select
										strRelSelList, //Rel select
										false,//get to
										true, //get from	
										(short)2, //level
										null,//bus where clause
										null, //rel where
										0);
				Iterator itr = mlRoutesInfo.iterator();
				Map mRouteInfo = null;
				String strArtworkInfo = null;
				String strRelType = null;
				String strRouteId = null;
				Map mRouteIdRouteNode = new HashMap();
				
				while(itr.hasNext()){
					mRouteInfo = (Map)itr.next();
					strRelType = (String)mRouteInfo.get(DomainRelationship.SELECT_NAME);
					
					if(DomainConstants.RELATIONSHIP_ROUTE_NODE.equals(strRelType)){
						MapList mlRouteNodeInfo = null;
						strRouteId = (String)mRouteInfo.get(DomainConstants.SELECT_FROM_ID);
						if(mRouteIdRouteNode.containsKey(strRouteId)){
							mlRouteNodeInfo = (MapList)mRouteIdRouteNode.get(strRouteId);
						}else{
							mlRouteNodeInfo = new MapList();
						}
						mlRouteNodeInfo.add(mRouteInfo);
						mRouteIdRouteNode.put(strRouteId, mlRouteNodeInfo);
					}else{
						strArtworkInfo = PGWidgetUtil.extractMultiValueSelect(mRouteInfo, strSelAttrArtworkInfo);
						if(BusinessUtil.isNotNullOrEmpty(strArtworkInfo) && (strArtworkInfo.indexOf(AWLConstants.LOCAL_COPY_AUTHORING) > -1 || strArtworkInfo.indexOf(AWLConstants.MASTER_COPY_AUTHORING) > -1)){
							mlAuthorRoutes.add(mRouteInfo); 
						}
						if(BusinessUtil.isNotNullOrEmpty(strArtworkInfo) && (strArtworkInfo.indexOf(AWLConstants.LOCAL_COPY_APPROVAL) > -1 || strArtworkInfo.indexOf(AWLConstants.MASTER_COPY_APPROVAL) > -1)){
							mlApproverRoutes.add(mRouteInfo); 
						}
					}
				}
				mAARouteInfoList.put("AuthorRoutes", mlAuthorRoutes);
				mAARouteInfoList.put("ApproverRoutes", mlApproverRoutes);
				mAARouteInfoList.put("RouteIdRouteNodeInfo", mRouteIdRouteNode);	
			}
			return mAARouteInfoList;
		}
		
		//RTA DS - CW 22x-01 - Added for ALM-44247 - Start
		/**
		 * Method to update the Due Date and Duration for Author and Approver columns.
		 * @param context
		 * @param mRouteInfo
		 * @param strScheduleCompletionDate
		 * @param strDuration
		 * @param isUpdateOnlyDuration
		 * @throws Exception
		 */
		private static void updateAuthorApproverRouteNodeDCM(Context context, Map mRouteInfo, String strScheduleCompletionDate, String strDuration, boolean isUpdateOnlyDuration) throws Exception{
			try{
				String strRouteId = (String)mRouteInfo.get(DomainConstants.SELECT_ID);
				if(BusinessUtil.isNullOrEmpty(strRouteId)){
					return;
				}
				String strSelRouteSeq = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
				StringList relSelectList = new StringList(2);
				relSelectList.add(DomainRelationship.SELECT_ID);				
				relSelectList.add(strSelRouteSeq);
								
				String strRouteNodeRelId = null;
				DomainRelationship domRouteNodeRel = null;
				
				Long lDuration = null;
				Map mRouteNodeAttrVal = null;
				Map mRouteNodeInfo = null;
				Iterator itrRouteNode = null;
				
				String strRouteDescription = BusinessUtil.getInfo(context, strRouteId, DomainConstants.SELECT_DESCRIPTION);
				MapList mlRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, null, relSelectList);
				mlRouteNodeInfo.sort(strSelRouteSeq, "descending", "integer");			
				int iNodeCnt = mlRouteNodeInfo.size();
				mRouteNodeInfo = (Map)mlRouteNodeInfo.get(0);
				if(iNodeCnt == 1) {						
					strRouteNodeRelId = (String)mRouteNodeInfo.get(DomainRelationship.SELECT_ID);
					mRouteNodeAttrVal = new HashMap();
					if(!isUpdateOnlyDuration) {
						mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE, strScheduleCompletionDate);
						mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM, "Task Create Date");
					}
					mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, strDuration);
					if(BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId)){	
						domRouteNodeRel = new DomainRelationship(strRouteNodeRelId);
						domRouteNodeRel.setAttributeValues(context, mRouteNodeAttrVal);
					}					
				} else {
					Map mSeqWiseDueDate = new HashMap();
					String strSeqVal = (String)mRouteNodeInfo.get(strSelRouteSeq);
					Date sysDate = null;
					mlRouteNodeInfo.sort(strSelRouteSeq, "ascending", "integer");
					itrRouteNode = mlRouteNodeInfo.iterator();
					if(Integer.parseInt(strSeqVal) > 1) {					
						double l = Double.parseDouble(strDuration) / Double.parseDouble(strSeqVal);
						lDuration = Long.valueOf(Math.round(l));							
						SimpleDateFormat formatter = new SimpleDateFormat(eMatrixDateFormat.strEMatrixDateFormat, Locale.US);
						while(itrRouteNode.hasNext()) {
							mRouteNodeInfo = (Map)itrRouteNode.next();
							strSeqVal = (String)mRouteNodeInfo.get(strSelRouteSeq);
							if(!isUpdateOnlyDuration) {
								if(!mSeqWiseDueDate.containsKey(strSeqVal)) {
									if(mSeqWiseDueDate.containsKey(Integer.toString((Integer.parseInt(strSeqVal) - 1)))){
										strScheduleCompletionDate = (String)mSeqWiseDueDate.get(Integer.toString((Integer.parseInt(strSeqVal) - 1)));
									}
									sysDate = formatter.parse(strScheduleCompletionDate);									
									strScheduleCompletionDate = getDateAfterAddingXDuration(sysDate,lDuration);
									mSeqWiseDueDate.put(strSeqVal, strScheduleCompletionDate);
								} else {
									strScheduleCompletionDate = (String)mSeqWiseDueDate.get(strSeqVal);									
								}
							}
							strRouteNodeRelId = (String)mRouteNodeInfo.get(DomainRelationship.SELECT_ID);						
							
							mRouteNodeAttrVal = new HashMap();
							if(!isUpdateOnlyDuration) {
								mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE, strScheduleCompletionDate);
								mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM, "Task Create Date");
							}
							mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, Long.toString(lDuration));
							mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_TITLE, strRouteDescription);
							if(BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId)){							
								domRouteNodeRel = new DomainRelationship(strRouteNodeRelId);									
								domRouteNodeRel.setAttributeValues(context, mRouteNodeAttrVal);
							}								
						}
					} else {
						while(itrRouteNode.hasNext()) {
							mRouteNodeInfo = (Map)itrRouteNode.next();
							strRouteNodeRelId = (String)mRouteNodeInfo.get(DomainRelationship.SELECT_ID);
							mRouteNodeAttrVal = new HashMap();
							if(!isUpdateOnlyDuration) {
								mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE, strScheduleCompletionDate);
								mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM, "Task Create Date");
							}
							mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, strDuration);
							mRouteNodeAttrVal.put(DomainConstants.ATTRIBUTE_TITLE, strRouteDescription);
							domRouteNodeRel = new DomainRelationship(strRouteNodeRelId);
							if(BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId)){	
								domRouteNodeRel.setAttributeValues(context, mRouteNodeAttrVal);
							}								
						}							
					}
				}
			}catch(Exception ex){
				logger.log(Level.INFO, "Error occured while executing method pgRTADCMUtil::updateAuthorApproverRouteNodeDCM: ", ex);
				throw ex;
			}
		}	
		//RTA DS - CW 22x-01 - Added for ALM-44247 - End
	
	protected static Map getAssigneeDetails(Context context, String[] args) throws FrameworkException {
		try {
			Map programMap          	= (Map)JPO.unpackArgs(args);
			Map requestMap	        	= BusinessUtil.getRequestMap(programMap);

			String strRowIDs 			= (String)requestMap.get(AWLConstants.SEL_ROW_IDs);
			String objectID				= (String)requestMap.get(AWLConstants.OBJECT_ID);
			String DesignerPersonOID			= (String)requestMap.get("DesignerPersonOID");
			String AuthorPersonOID			= (String)requestMap.get("AuthorPersonOID");
			String ApproverPersonOID		= (String)requestMap.get("ApproverPersonOID");
			String AuthorRouteTemplateOID 	= (String)requestMap.get("AuthorRouteTemplateOID");
			String ApproverRouteTemplateOID = (String)requestMap.get("ApproverRouteTemplateOID");
			String strAssignAuthorAsApproverHiddenValue	= (String)requestMap.get("AssignAuthorAsApproverHiddenValue");
			boolean isAuthorAsApprover = RANGE_TRUE.equalsIgnoreCase(strAssignAuthorAsApproverHiddenValue);

			StringList idList = FrameworkUtil.split(strRowIDs, ",");

			String author	= BusinessUtil.isNotNullOrEmpty(AuthorPersonOID) ? AuthorPersonOID :
							  BusinessUtil.isNotNullOrEmpty(AuthorRouteTemplateOID) ? AuthorRouteTemplateOID :
					          BusinessUtil.isNotNullOrEmpty(DesignerPersonOID) ? DesignerPersonOID : "";


			String approver = BusinessUtil.isNotNullOrEmpty(ApproverPersonOID) ? ApproverPersonOID :
							  BusinessUtil.isNotNullOrEmpty(ApproverRouteTemplateOID) ? ApproverRouteTemplateOID :
							  isAuthorAsApprover ? author : "";

			Map map = new HashMap(4);
			map.put(AWLConstants.SEL_ROW_IDs, idList);
			map.put(AWLConstants.OBJECT_ID, objectID);
			map.put(AWLRel.ARTWORK_CONTENT_AUTHOR.get(context), author);
			map.put(AWLRel.ARTWORK_CONTENT_APPROVER.get(context), approver);
			return map;
		} catch (Exception e) { throw new FrameworkException(e); }
	}
	
	
	//RTA 18x.1 - ADDED FOR ALM-25409 START
		public static String getAbsenceDelegate(Context context, String strPersonList)
		{	
		
			try {
				
				String personOID = null;
				DomainObject personObj = null;
				Map personInfoMap = null;		
				String strAbsenceEndDate = null;
				String strAbsenceStartDate = null;
				String strAbsenceDelegate = null;
				Date curDate = new Date();
				Date absenceStartDate = new Date();
				Date absenceEndDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat (eMatrixDateFormat.getInputDateFormat(), context.getLocale());
				String strAttributeAbsenceDelegate = PropertyUtil.getSchemaProperty(context,"attribute_AbsenceDelegate");
				String strAttributeAbsenceStartDate = PropertyUtil.getSchemaProperty(context,"attribute_AbsenceStartDate");
				String strAttributeAbsenceEndDate = PropertyUtil.getSchemaProperty(context,"attribute_AbsenceEndDate");
				String strDelegatedPersonObjectId = null;
				StringList selects = new StringList(3);
				selects.add("attribute["+strAttributeAbsenceDelegate+"].value");
				selects.add("attribute["+strAttributeAbsenceEndDate+"].value");
				selects.add("attribute["+strAttributeAbsenceStartDate+"].value");
								
				StringList sl = FrameworkUtil.split(strPersonList, "|");
				StringList slReturn = new StringList();
				String strSequence = "";
				String strTemp = "";
				int iIndexOfComma;
				for(int k=0; k<sl.size(); k++) {
					personOID = (String) sl.get(k);
					strSequence = "";
					iIndexOfComma = personOID.indexOf(",");
					if(iIndexOfComma!= -1){
						strTemp = new String(personOID);
						personOID = personOID.substring(0,iIndexOfComma);
						strSequence = strTemp.substring(strTemp.indexOf(",")+1,strTemp.length());
					}
					personObj = DomainObject.newInstance(context, personOID);
					personInfoMap = personObj.getInfo(context, selects);													
					strAbsenceDelegate = (String)personInfoMap.get("attribute["+strAttributeAbsenceDelegate+"].value");
					strAbsenceStartDate = (String)personInfoMap.get("attribute["+strAttributeAbsenceStartDate+"].value");
					strAbsenceEndDate = (String)personInfoMap.get("attribute["+strAttributeAbsenceEndDate+"].value");
					Boolean bDelegateExist = false;
					if(BusinessUtil.isNotNullOrEmpty(strAbsenceDelegate) && BusinessUtil.isNotNullOrEmpty(strAbsenceStartDate) && BusinessUtil.isNotNullOrEmpty(strAbsenceEndDate))
					{
							strDelegatedPersonObjectId = PersonUtil.getPersonObjectID(context, strAbsenceDelegate);
							curDate.setHours(0);curDate.setMinutes(0);curDate.setSeconds(0);
							absenceStartDate = sdf.parse(strAbsenceStartDate);
							absenceStartDate.setHours(0);absenceStartDate.setMinutes(0);absenceStartDate.setSeconds(0);
							absenceEndDate = sdf.parse(strAbsenceEndDate);
							absenceEndDate.setHours(0);absenceEndDate.setMinutes(0);absenceEndDate.setSeconds(0);
							if(BusinessUtil.isNotNullOrEmpty(strDelegatedPersonObjectId) && (absenceStartDate.equals(curDate) || absenceEndDate.equals(curDate) || (absenceStartDate.before(curDate) && absenceEndDate.after(curDate))))
							{
								bDelegateExist = true;
							}
					}
					if(bDelegateExist)
					{
						if(BusinessUtil.isNotNullOrEmpty(strSequence))
							strDelegatedPersonObjectId = strDelegatedPersonObjectId.concat(",".concat(strSequence));
						slReturn.add(strDelegatedPersonObjectId);
					}
					else
					{
						if(BusinessUtil.isNotNullOrEmpty(strSequence))
							personOID = personOID.concat(",".concat(strSequence));
						slReturn.add(personOID);
					}
				}
				String strReturn = FrameworkUtil.join(slReturn,"|");
				return strReturn;
			}catch(Exception e)
			{
				
				return strPersonList;
			}
		}
		
		public static String getRouteTemplateId(Context context, String assigneeIds, boolean approval) throws Exception {
			String routeTemplateId = assigneeIds;
			try {	
				Map<String, String> assigneeMap	= getAssigneeMapfromString(assigneeIds);
				assigneeMap			= sortByComparator(assigneeMap, true, true);
				String artworkInfo 	= joinMapToString(assigneeMap);
				if(assigneeMap.size() > 1) {
					routeTemplateId	= getRouteTemplateByArtworkInfo(context, artworkInfo, approval);
					if(BusinessUtil.isNullOrEmpty(routeTemplateId))
						routeTemplateId	= createRouteTemplate(context, assigneeMap, artworkInfo, approval);
				} else if(assigneeMap.size() > 0) {
					routeTemplateId = assigneeMap.keySet().iterator().next();
				}
				
			} catch (Exception e) {
				throw e;
			}
			return routeTemplateId;
		}
		
		
		public static Map<String, String> getAssigneeMapfromString(String assigneeIds) throws FrameworkException {
			Map<String, String> assigneeMap	= new LinkedHashMap<String, String>();
			StringList assigneeSeqList = FrameworkUtil.split(assigneeIds, "|");
			for(int itr=0; itr<assigneeSeqList.size(); itr++) {
				StringList assigneeDetails	= FrameworkUtil.split((String)assigneeSeqList.get(itr), ",");
				
				if(assigneeDetails.size() > 0) {
					String assigneeId	= (String) assigneeDetails.get(0);
					String assigneeSeq	= "1";
					
					if(assigneeDetails.size() > 1) {
						try {
							assigneeSeq = (String) assigneeDetails.get(1);
						} catch (Exception e) {
							throw e;
						}
					}
					assigneeMap.put(assigneeId, assigneeSeq);
				}
			}
			return assigneeMap;
		}
		
		public static String createRouteTemplate(Context context, Map<String, String> assigneeMap, String artworkInfo, boolean approval) throws MatrixException {
			String routeTemplateId = "";
			String strPLIApproverRoleValue = EnoviaResourceBundle.getProperty(context,"emxAWLStringResource",context.getLocale(),"emxAWL.RouteTemplate.pgPLIApproverRole");
			try{
				routeTemplateId = FrameworkUtil.autoName(context,"type_RouteTemplate", 
						new Policy(DomainObject.POLICY_ROUTE_TEMPLATE).getFirstInSequence(context), "policy_RouteTemplate");
				String routeBasePurpose = approval ? RouteUtil.ROUTE_APPROVAL : RouteUtil.ROUTE_STANDARD;
				String routeAction 		= approval ? RouteUtil.RANGE_VALUE_APPROVE : RouteUtil.RANGE_VALUE_COMMENT;
				HashMap<String, String> attributeMap = new HashMap<String, String>();
				attributeMap.put(PropertyUtil.getSchemaProperty(context,"attribute_TaskEditSetting"), "Modify/Delete Task List");
				attributeMap.put(AWLAttribute.ARTWORK_INFO.get(context), artworkInfo);
				attributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE, routeBasePurpose);
				attributeMap.put(RTAUtilConstants.ATTRIBUTE_PGPLI_APPROVER_ROLE, strPLIApproverRoleValue);
				DomainObject routeTemplateObj	= DomainObject.newInstance(context, routeTemplateId);
				routeTemplateObj.setAttributeValues(context, attributeMap);
				//modification RTA 15x.5 Route Template issue - starts
				//routeTemplateObj.setState(context, "Active");
				routeTemplateObj.setDescription(context, "Route Template for " + artworkInfo);
				Iterator<String> iter = assigneeMap.keySet().iterator();
				while(iter.hasNext()) {
					String assigneeId	= (String)iter.next();
					String taskSequence= (String)assigneeMap.get(assigneeId);
					DomainObject assgineeObj = DomainObject.newInstance(context, assigneeId);
					DomainRelationship routeNodeRel = DomainRelationship.connect(context, routeTemplateObj, 
							DomainConstants.RELATIONSHIP_ROUTE_NODE, assgineeObj);
					String duedate = "2";
					try {
						duedate = FrameworkProperties.getProperty(context, "emxAWL.ArtworkInboxTaskCompletion.DueDateOffset");
					} catch (Exception e) {}
					HashMap<String, String> relAttributeMap = new HashMap<String, String>();
					relAttributeMap.put(PropertyUtil.getSchemaProperty(context,"attribute_ParallelNodeProcessionRule"), "All");
					relAttributeMap.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, duedate);
					relAttributeMap.put(DomainConstants.ATTRIBUTE_TITLE, assgineeObj.getInfo(context, DomainConstants.SELECT_NAME));
					relAttributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_ACTION, routeAction);
					relAttributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE, taskSequence);
					relAttributeMap.put(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM, "Route Start Date");
					relAttributeMap.put(DomainConstants.ATTRIBUTE_ALLOW_DELEGATION, AWLConstants.RANGE_TRUE);
					relAttributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_INSTRUCTIONS, routeBasePurpose);
					routeNodeRel.setAttributeValues(context, relAttributeMap);
				}
				
				
				routeTemplateObj.setState(context, "Active");
				//modification RTA 15x.5 Route Template issue - ends
				//Added for the ALM 3533--START
				/*String routeTemplateOwner=(String)routeTemplateObj.getInfo(context,DomainConstants.SELECT_OWNER);
				String objPerAndOrgIds = MqlUtil.mqlCommand(context, "print bus Person \""+routeTemplateOwner+"\" - select id to["+DomainConstants.RELATIONSHIP_EMPLOYEE+"].from.id dump |");
				if(BusinessUtil.isNotNullOrEmpty(objPerAndOrgIds)){
					StringList objIdList= FrameworkUtil.split(objPerAndOrgIds, "|");
					String perId=(String)objIdList.get(0);
					String perOrg=(String)objIdList.get(1);
					if(BusinessUtil.isNotNullOrEmpty(perId) && BusinessUtil.isNotNullOrEmpty(perOrg)){
						DomainRelationship routeTempObj = DomainRelationship.connect(context,DomainObject.getObject(context, perId) , 
								DomainConstants.RELATIONSHIP_ROUTE_TEMPLATES, routeTemplateObj);
						DomainRelationship routeTempOrgObj = DomainRelationship.connect(context,DomainObject.getObject(context, perOrg) , 
								DomainConstants.RELATIONSHIP_OWNING_ORGANIZATION, routeTemplateObj);
					}
				} */
				 //Added for the ALM 3533--END
			}catch (Exception e){
				throw e;
			 }
			
			return routeTemplateId;
		}
		
		public static String getRouteTemplateByArtworkInfo(Context context, String artworkInfo, boolean approval) throws FrameworkException {
			String routePurpose = approval ? RouteUtil.ROUTE_APPROVAL : RouteUtil.ROUTE_STANDARD;
			StringList selects 	= BusinessUtil.toStringList(DomainConstants.SELECT_ID, "attribute["+AWLAttribute.ARTWORK_INFO.get(context)+"].value", 
																					"attribute["+DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE+"].value");
			
			String whereClause 	= "attribute["+AWLAttribute.ARTWORK_INFO.get(context)+"].value == '" + artworkInfo +"' && " +
													"attribute["+DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE+"].value == '" + routePurpose +"' ";
			MapList rtMapList	= DomainObject.findObjects(context, DomainConstants.TYPE_ROUTE_TEMPLATE, "*", whereClause, selects);
			
			return rtMapList.size() > 0 ? (String)BusinessUtil.getIdList(rtMapList).get(0) : null;
		}

		
		public static Map<String, String> sortByComparator(Map<String, String> unsortMap, final boolean keys, final boolean order) {
	        List<Entry<String, String>> list = new LinkedList<Entry<String, String>>(unsortMap.entrySet());
	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry<String, String>>() {
	            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
					if(keys) {
	            		return order ? o1.getKey().compareTo(o2.getKey()) :
	            							o2.getKey().compareTo(o1.getKey());
					} else {
	            		return order ? o1.getValue().compareTo(o2.getValue()) :
	            							o2.getValue().compareTo(o1.getValue());
					}
	            }
	        });
	        // Maintaining insertion order with the help of LinkedList
	        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
	        for (Entry<String, String> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	        return sortedMap;
	    }
	
		public static String joinMapToString(Map<String, String> hm) {
			StringList keyValueList = new StringList();
			 Iterator<String> iter = hm.keySet().iterator();
			 while(iter.hasNext()) {
				 String key = (String)iter.next();
				 String val = (String)hm.get(key);
				 
				 keyValueList.add(key+","+val);
			 }
			
			return FrameworkUtil.join(keyValueList, "|");
		}
		
	private static StringList getValidIdsForAssignAA(Context context, StringList idList, String strMode) throws FrameworkException {
		DomainObject domObj = null;
		String strObjId = null;
		boolean bIsContextPushed = false;
		StringList slIdsToRemoveList = new StringList();
		if (!ArtworkConstants.PERSON_USERAGENT.equalsIgnoreCase(context.getUser())) {
			ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT, null, context.getVault().getName());
			bIsContextPushed = true;
		}
		for(int j=0;j<idList.size();j++)
		{
			strObjId = (String) idList.get(j);
			domObj = DomainObject.newInstance(context,strObjId);
			String strCurrent = domObj.getInfo(context, DomainConstants.SELECT_CURRENT);
			if(!(domObj.isKindOf(context, AWLType.COPY_ELEMENT.get(context))))
			{
				slIdsToRemoveList.add(strObjId);
			} else if(domObj.isKindOf(context, AWLType.COPY_ELEMENT.get(context))) {
				StringList slRouteList = domObj.getInfoList(context,"from["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].to.id");
				if(slRouteList.size()>0 && (("revise".equals(strMode) && AWLState.PRELIMINARY.get(context, AWLPolicy.POA).equals(strCurrent)) || ("assignAuthorApprover".equals(strMode))))
				{
					slIdsToRemoveList.add(strObjId);
				}
			}
		}
		if(bIsContextPushed){
			ContextUtil.popContext(context);
			bIsContextPushed = false;
		}				
		if(slIdsToRemoveList.size()>0)
		{
			idList.removeAll(slIdsToRemoveList);
		}
		return idList;
	}
	
	public static String getBaseCopiesForMCEs(Context context, String paramString) throws Exception {
		String strOut = DomainConstants.EMPTY_STRING;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strMCEIds = jsonInputData.getString(RTAUtil.STR_MCIDS);
			StringList slMCIds = FrameworkUtil.split(strMCEIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			JsonArrayBuilder jsonArrReturn = Json.createArrayBuilder();
			StringList slObjSel = new StringList();
			slObjSel.add(DomainConstants.SELECT_ID);
			slObjSel.add(DomainConstants.SELECT_NAME);
			slObjSel.add(DomainConstants.SELECT_TYPE);
			slObjSel.add(DomainConstants.SELECT_OWNER);
			slObjSel.add(DomainConstants.SELECT_REVISION);
			slObjSel.add(DomainConstants.SELECT_CURRENT);
			slObjSel.add(RTAUtilConstants.SELECT_ISBASECOPY);
			slObjSel.add(RTAUtilConstants.SELECT_PGAWLREFNO);
			slObjSel.add(RTAUtilConstants.SELECT_TITLE);
			slObjSel.add(RTAUtilConstants.SELECT_COPYTEXT);
			slObjSel.add(RTAUtilConstants.SELECT_MARKETINGNAME);
			slObjSel.add(RTAUtilConstants.SELECT_COPYTEXTLANG);
			slObjSel.add(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT);
			slObjSel.add("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]");
			String strWhere = RTAUtilConstants.SELECT_ISBASECOPY +" == Yes";
			MapList mlFinalList = new MapList();
			for(int i=0 ; i<slMCIds.size() ; i++) {
				MapList mlLcList = new ArtworkMaster(slMCIds.get(i)).getArtworkElements(context, slObjSel, new StringList(PGWidgetConstants.SELECT_CONNECTION_ID), strWhere);
				String strBaseId = (String) ((Map) mlLcList.get(0)).get(DomainConstants.SELECT_ID);
				Map mapBaseLC = (Map) mlLcList.get(0);
				mapBaseLC.putAll(getAuthorApproverForCopyElement(context, strBaseId));
				mapBaseLC.put(KEY_PARENT_ID, slMCIds.get(i));
				mlFinalList.add(mapBaseLC);
			}
			
			for (int i = 0; i < mlFinalList.size(); i++) {
				Map mapLC = (Map) mlFinalList.get(i);

//				String KEY_MASTER_COPY_APPROVAL="";
//				String KEY_MASTER_COPY_AUTHORING="";
//				if((String)mapLC.get(RTAUtilConstants.KEY_MASTER_COPY_APPROVAL) != null && 
//						(String)mapLC.get(RTAUtilConstants.KEY_MASTER_COPY_AUTHORING) != null) {
//					KEY_MASTER_COPY_APPROVAL=(String) mapLC.get(RTAUtilConstants.KEY_MASTER_COPY_APPROVAL);
//				    KEY_MASTER_COPY_AUTHORING=(String) mapLC.get(RTAUtilConstants.KEY_MASTER_COPY_AUTHORING);
//				}
				jsonArrReturn.add(Json.createObjectBuilder()
						.add(DomainConstants.SELECT_ID, (String) mapLC.get(DomainConstants.SELECT_ID))
						.add(DomainConstants.SELECT_TYPE, (String) mapLC.get(DomainConstants.SELECT_TYPE))
						.add(DomainConstants.SELECT_NAME, (String) mapLC.get(DomainConstants.SELECT_NAME))
						.add(DomainConstants.SELECT_NAME, (String) mapLC.get(DomainConstants.SELECT_NAME))
						.add(DomainConstants.SELECT_REVISION, (String) mapLC.get(DomainConstants.SELECT_REVISION))
						.add(DomainConstants.SELECT_CURRENT, (String) mapLC.get(DomainConstants.SELECT_CURRENT))
						.add(DomainConstants.SELECT_CURRENT, (String) mapLC.get(DomainConstants.SELECT_CURRENT))
						.add("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]", (String) mapLC.get("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]"))
						.add(DomainConstants.SELECT_ATTRIBUTE_TITLE,
								(String) mapLC.get(DomainConstants.SELECT_ATTRIBUTE_TITLE))
						.add(KEY_CONTENT, (String) mapLC.get(RTAUtilConstants.SELECT_COPYTEXT))
						.add(KEY_LANGUAGES, (String) mapLC.get(RTAUtilConstants.SELECT_COPYTEXTLANG))
						.add(RTAUtilConstants.ATTR_PG_CONFIRM_ASSIGNMENT, (String) mapLC.get(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT))
						.add(RTAUtilConstants.KEY_AUTHOR, PGWidgetUtil.checkNullValueforString((String) mapLC.get(RTAUtilConstants.KEY_AUTHOR)))
						.add(RTAUtilConstants.KEY_APPROVER, PGWidgetUtil.checkNullValueforString((String) mapLC.get(RTAUtilConstants.KEY_APPROVER)))
						.add(PGWidgetConstants.KEY_HIERARCHY,
								Json.createArrayBuilder().add((String) mapLC.get(KEY_PARENT_ID))
								.add((String) mapLC.get(DomainConstants.SELECT_ID))));
//				        .add(RTAUtilConstants.KEY_APPROVER_ROUTE_STATUS, KEY_MASTER_COPY_APPROVAL)
//						.add(RTAUtilConstants.KEY_AUTHOR_ROUTE_STATUS, KEY_MASTER_COPY_AUTHORING));
			}
			strOut = jsonArrReturn.build().toString();
		}catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
			throw e;
		}		
		return strOut;
	}

	public static Map getAuthorApproverForCopyElement(Context context, String strLCEId) throws Exception {
		Map<String, String> mapReturn = new HashMap<>();
		try {
			StringList slSels = new StringList();

			slSels.add(RTAUtilConstants.SELECT_AUTHOR_PERSON);
			slSels.add(RTAUtilConstants.SELECT_APPROVER_PERSON);
			slSels.add(RTAUtilConstants.SELECT_AUTHOR_FROM_TEMPLATE);
			slSels.add(RTAUtilConstants.SELECT_APPROVER_FROM_TEMPLATE);
			slSels.add(RTAUtilConstants.SELECT_AUTHOR_ROUTE_STATUS);
			slSels.add(RTAUtilConstants.SELECT_AUTHOR_ARTWORK_INFO);

			StringList slMultiList = new StringList();
			slMultiList.add(RTAUtilConstants.SELECT_AUTHOR_FROM_TEMPLATE);
			slMultiList.add(RTAUtilConstants.SELECT_APPROVER_FROM_TEMPLATE);
			slMultiList.add(RTAUtilConstants.SELECT_AUTHOR_ROUTE_STATUS);
			slMultiList.add(RTAUtilConstants.SELECT_AUTHOR_ARTWORK_INFO);

			Map<String, Object> mapInfo = new CopyElement(strLCEId).getInfo(context, slSels, slMultiList);
			if(mapInfo.get(RTAUtilConstants.SELECT_AUTHOR_FROM_TEMPLATE) != null) {
				mapReturn.put(RTAUtilConstants.KEY_AUTHOR, PGWidgetUtil.extractMultiValueSelect(mapInfo, RTAUtilConstants.SELECT_AUTHOR_FROM_TEMPLATE));
				mapReturn.put(KEY_ROUTE_TEMPLATE_AUTHOR, mapInfo.get(RTAUtilConstants.SELECT_AUTHOR_PERSON).toString());
			}else if(mapInfo.get(RTAUtilConstants.SELECT_AUTHOR_PERSON) != null) {
				mapReturn.put(RTAUtilConstants.KEY_AUTHOR, mapInfo.get(RTAUtilConstants.SELECT_AUTHOR_PERSON).toString());
			}
			if(mapInfo.get(RTAUtilConstants.SELECT_APPROVER_FROM_TEMPLATE) != null) {
				mapReturn.put(RTAUtilConstants.KEY_APPROVER, PGWidgetUtil.extractMultiValueSelect(mapInfo, RTAUtilConstants.SELECT_APPROVER_FROM_TEMPLATE));
				mapReturn.put(KEY_ROUTE_TEMPLATE_APPROVER, mapInfo.get(RTAUtilConstants.SELECT_APPROVER_PERSON).toString());
			}else if(mapInfo.get(RTAUtilConstants.SELECT_APPROVER_PERSON) != null) {
				mapReturn.put(RTAUtilConstants.KEY_APPROVER, mapInfo.get(RTAUtilConstants.SELECT_APPROVER_PERSON).toString());
			}

			StringList routeStatusStr= (StringList) mapInfo.get(RTAUtilConstants.SELECT_AUTHOR_ROUTE_STATUS);
			StringList artworkInfoStr= (StringList) mapInfo.get(RTAUtilConstants.SELECT_AUTHOR_ARTWORK_INFO);
			if(routeStatusStr != null && artworkInfoStr != null) {
				for(int i=0;i<routeStatusStr.size();i++) {
					mapReturn.put(artworkInfoStr.get(i), routeStatusStr.get(i));
				}
			}
		}catch (Exception e) {
			throw e;
		}		
		return mapReturn;
	}

	public static String completeAssignAuthorApproveTask(Context context, String strInput) {
		Object[] objectArray = new Object[] {context , strInput};
		Class[] objectTypeArray = new Class[] {matrix.db.Context.class, String.class};
		BackgroundProcess backgroundProcess = new BackgroundProcess();
		try {
			backgroundProcess.submitJob(context, new RTAUtil(), "completeAssignAuthorApproveTaskBackground", objectArray, objectTypeArray);
		} catch (MatrixException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
			.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
				.add(PGWidgetConstants.KEY_MESSAGE, EnoviaResourceBundle.getProperty(context, "emxAWLStringResource", context.getLocale(),
						"emxAWL.AssignAuthorApprover.BackgroundJob")).build().toString();
	}
	public static String completeAssignAuthorApproveTaskBackground(Context context, String strInput) throws FrameworkException {
		String strOut = DomainConstants.EMPTY_STRING;
		boolean isTransactionActive = false;
		boolean isContextPushed = false;
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			StringList slPOAIds =  FrameworkUtil.split(jsonInput.getString(KEY_POAS),
					PGWidgetConstants.KEY_COMMA_SEPARATOR);
			String strWhere = DomainConstants.SELECT_CURRENT + "!=" + DomainConstants.STATE_INBOX_TASK_COMPLETE;
			JsonObjectBuilder inputData = Json.createObjectBuilder();
			inputData.add(PGWidgetConstants.KEY_RELPATTERN, DomainConstants.RELATIONSHIP_TASK_DELIVERABLE);
			inputData.add(PGWidgetConstants.TYPE_PATTERN, RTAUtilConstants.TYPE_ASSIGN_AUTHOR_APPROVER_TASK);
			inputData.add(PGWidgetConstants.KEY_EXPANDLEVEL, "0");
			inputData.add(PGWidgetConstants.KEY_WHERECONDITION, strWhere);
			inputData.add(PGWidgetConstants.KEY_GETTO, PGWidgetConstants.STRING_TRUE);
			inputData.add(PGWidgetConstants.KEY_GETFROM, PGWidgetConstants.STRING_FALSE);
			inputData.add(PGWidgetConstants.KEY_LIMIT, "1");
			inputData.add(PGWidgetConstants.KEY_RELWHERECONDITION, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.KEY_RELATIONSHIPSELECTS, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.KEY_OBJECT_SELECTS, DomainConstants.SELECT_ID);

			if (!(Access.isArtworkProjectManager(context) || Access.isProductManager(context))) {
				strOut = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource", context.getLocale(),
						"emxAWL.Error.NoAccessToPerformOperation");
			} else {
				ContextUtil.startTransaction(context, true);
				isTransactionActive = true;
				// RTA users do not have access on DCM Data so need to push context
				ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT, null,
						ArtworkConstants.VAULT_ESERVICE_PRODUCTION);
				isContextPushed = true;

				for (int i = 0; i < slPOAIds.size(); i++) {
					inputData.add(PGWidgetConstants.KEY_OBJECT_ID, slPOAIds.get(i));
					MapList mlList = PGWidgetUtil.getRelatedObjectsMapList(context, inputData.build());
					if (mlList != null && !mlList.isEmpty()) {
						try {
							String strAATaskId = (String) ((Map) mlList.get(0)).get(DomainConstants.SELECT_ID);
							Map requestMap = new HashMap();
							requestMap.put(KEY_SELECTED_WBS_IDS, new StringList(strAATaskId));
							String methodArgs[] = matrix.db.JPO.packArgs(requestMap);
							Map mapInfo = (Map) JPO.invoke(context, "pgRTAWBS", null,
									"authorAndApproverAssignmentCheck", methodArgs, Map.class);
							String strMissingAAMsg = (String) mapInfo.get("ErrMessage");
							Task task = new Task(strAATaskId);
							if (UIUtil.isNullOrEmpty(strMissingAAMsg)) {
								task.setState(context, DomainConstants.STATE_INBOX_TASK_COMPLETE);
							} else {
								task.setState(context, DomainConstants.STATE_ISSUE_ACTIVE);
							}
						} catch (Exception e) {
							logger.log(Level.SEVERE, EXCEPTION_MESSAGE + PGWidgetUtil.getExceptionTrace(e));
						}
					}
				}
			}
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build()
					.toString();
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
			}
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
		return strOut;
	}
	
	public static String updateInstanceSequence(Context context, String strInput) {
		String strOut = DomainConstants.EMPTY_STRING;
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			JsonArray jsonArrInput = jsonInput.getJsonArray(KEY_POA_MC_INST_SEQ);

			for (int i = 0; i < jsonArrInput.size(); i++) {
				StringList slInputValues = FrameworkUtil.split(jsonArrInput.getString(i), PGWidgetConstants.KEY_UNDERSCORE);
				String strPOAId = (String) slInputValues.get(0);
				String strMCId = (String) slInputValues.get(1);
				if (BusinessUtil.isKindOf(context, strPOAId, AWLType.POA.get(context))) {
					(new POA(strPOAId)).setInstanceSequence(context, new ArtworkMaster(strMCId),
							Integer.parseInt(slInputValues.get(2)));
				}
			}
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build()
					.toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
		return strOut;
	}
	
	public static StringList getSelectedIdsForLaunchWidget(Context context, String strObjectId, StringList slSelectedIdList)
			throws MatrixException {

		Map mapReturn = new HashMap<>();

		boolean isPOA = POA.isKindOfPOA(context, slSelectedIdList);
		boolean isCopyList = CopyList.isKindOfCopyList(context, slSelectedIdList);

		StringList selects = new StringList();
		selects.add(DomainConstants.SELECT_ID);
		selects.add(AWLConstants.SELECT_MODIFY_ACCESS);
		selects.add(DomainConstants.SELECT_CURRENT);

		boolean bSingleSourcePOAExists = false;
		boolean bCopyMatrixStatus = false;
		String strApprovalStatus = null;

		StringList combinedObjList = new StringList();
		StringList slSourcePOA = new StringList();

		HashMap returnMap = (HashMap) JPO.invoke(context, "pgRTA_Util", null, "getSourceTargetPOAForAP",
				new String[] { strObjectId }, HashMap.class);
		HashMap hmPOA = returnMap != null ? (HashMap) returnMap.get("SourceTargetPOA") : new HashMap<>();
		slSourcePOA = (StringList) returnMap.get("SourcePOAList"); 
		int nSourcePOASize = slSourcePOA.size();
		if (nSourcePOASize == 1) {
			bSingleSourcePOAExists = true;
		}
		// This logic is referred from pgRTAPOAEditIntermediateProcess.jsp for Copy Edit
		// POA command on AP Page.
		if (slSelectedIdList != null && !slSelectedIdList.isEmpty() && (isCopyList || isPOA)) {
			if (isPOA) {
				selects.add("attribute[" + ArtworkConstants.ATTRIBUTE_RIGHTCOPYAPPROVALSTATUS + "]");
			}
			MapList editableObjMapList = BusinessUtil.getInfo(context, slSelectedIdList, selects);
			StringList editObjList = new StringList();
			StringList nonEditableObjList = new StringList();
			for (Object obj : editableObjMapList) {
				Map map = (Map) obj;
				String access = (String) map.get(AWLConstants.SELECT_MODIFY_ACCESS);
				String objId = (String) map.get(DomainConstants.SELECT_ID);
				boolean hasAccessToModify = AWLConstants.RANGE_TRUE.equalsIgnoreCase(access);
				bCopyMatrixStatus = false;
				strApprovalStatus = (String) map
						.get("attribute[" + ArtworkConstants.ATTRIBUTE_RIGHTCOPYAPPROVALSTATUS + "]");
				if (BusinessUtil.isNotNullOrEmpty(strApprovalStatus)) {
					bCopyMatrixStatus = ArtworkConstants.RCM_STATUS_APPROVED.equalsIgnoreCase(strApprovalStatus);
				}
				hasAccessToModify = isPOA ? hasAccessToModify && !bCopyMatrixStatus : hasAccessToModify;
				String strSourcePOAId = (String) hmPOA.get(objId);
				if (hasAccessToModify) {
					editObjList.add(objId);
					if (!editObjList.contains(strSourcePOAId) && !bSingleSourcePOAExists)
						editObjList.add(strSourcePOAId);
				} else {
					nonEditableObjList.add(objId);
					if (!nonEditableObjList.contains(strSourcePOAId) && !bSingleSourcePOAExists)
						nonEditableObjList.add(strSourcePOAId);
				}
			}
			if (bSingleSourcePOAExists) {
				String strSourcePOAIdNew = (String) slSourcePOA.get(0);
				if (!editObjList.contains(strSourcePOAIdNew))
					editObjList.add(strSourcePOAIdNew);
			}
			String strSelectedObjIds = "";
			combinedObjList.addAll(editObjList);
			combinedObjList.addAll(nonEditableObjList);
			strSelectedObjIds = FrameworkUtil.join(combinedObjList, "|");

		}
		return combinedObjList;
	}
	public static boolean userHasAWLAccess(Context context) throws FrameworkException {
		return PersonUtil.hasAnyAssignment(context, AWLRole.ARTWORK_PROJECT_MANAGER.get(context))
				|| PersonUtil.hasAnyAssignment(context, AWLRole.PRODUCT_MANAGER.get(context));
	}
	
	public static boolean isDemoteDisabled(Context context, String strPOAId) throws FrameworkException {
		boolean isContextPushed = false;
		try {
			DomainObject domPOA = DomainObject.newInstance(context, strPOAId);
			StringList slSelects = new StringList();
			String ATTR_ORIGINATING_SOURCE = PropertyUtil.getSchemaProperty(context, "attribute_pgOriginatingSource");
			slSelects.add(DomainObject.getAttributeSelect(ATTR_ORIGINATING_SOURCE));
			slSelects.add(DomainConstants.SELECT_TYPE);
			slSelects.add(DomainConstants.SELECT_CURRENT);
			slSelects.add("to[" + PropertyUtil.getSchemaProperty(null, "relationship_pgAAA_ProjectToPOA") + "].from.id");

			//Context pushed as some of the selects are connected objects and user may not have read access to them
			ContextUtil.pushContext(context);
			isContextPushed = true;
			Map mapPOA = domPOA.getInfo(context, slSelects);
			ContextUtil.popContext(context);
			isContextPushed = false;
			String strCurrent = (String) mapPOA.get(DomainConstants.SELECT_CURRENT);
			String strAttrOriginationSource = (String) mapPOA
					.get(DomainObject.getAttributeSelect(ATTR_ORIGINATING_SOURCE));
			boolean isProjectConnected = UIUtil.isNotNullAndNotEmpty((String) mapPOA
					.get("to[" + PropertyUtil.getSchemaProperty(null, "relationship_pgAAA_ProjectToPOA") + "].from.id"));
			StringList slStatesAllowed = new StringList(AWLState.PRELIMINARY.get(context, AWLPolicy.POA.get(context)));
			slStatesAllowed.add(AWLState.ARTWORK_IN_PROCESS.get(context, AWLPolicy.POA.get(context)));
			slStatesAllowed.add(AWLState.REVIEW.get(context, AWLPolicy.POA.get(context)));
			slStatesAllowed.add(AWLState.RELEASE.get(context, AWLPolicy.POA.get(context)));
			if(((String) mapPOA.get(DomainConstants.SELECT_CURRENT))
					.equals(AWLState.DRAFT.get(context, AWLPolicy.POA.get(context)))) {
				return true;
			}
			if (!(!isProjectConnected && slStatesAllowed.contains(strCurrent) && ("DSO".equals(strAttrOriginationSource)
					|| !AWLType.POA.get(context).equals((String) mapPOA.get(DomainObject.SELECT_TYPE))
					|| (AWLType.POA.get(context).equals((String) mapPOA.get(DomainObject.SELECT_TYPE)) && userHasAWLAccess(context))))) {
				return true;
			}
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		finally {
			if(isContextPushed)
				ContextUtil.popContext(context);
		}
		return false;
	}
	
	public static boolean isPromoteDisabled(Context context, String strPOAId) throws FrameworkException {
		try {
			DomainObject domPOA = DomainObject.newInstance(context, strPOAId);
			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_TYPE);
			slSelects.add(DomainConstants.SELECT_CURRENT);

			//Context pushed as some of the selects are connected objects and user may not have read access to them
			Map mapPOA = domPOA.getInfo(context, slSelects);
			boolean isDraft = ((String) mapPOA.get(DomainConstants.SELECT_CURRENT))
					.equals(AWLState.DRAFT.get(context, AWLPolicy.POA.get(context)));

			if(isDraft) {
				return false;
			}else {
				return true;
			}
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, e.getMessage());
			throw e;
		}
	}
	public static String connectMCtoPOA(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutput = null;
		JsonArray jsonArrRowData = Json.createArrayBuilder().build();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			String strCheckPOAIds = jsonInputData.getString(RTAUtilConstants.CHECK_POA_IDs);
			StringList allPOAIdList = FrameworkUtil.split(strCheckPOAIds, PGWidgetConstants.KEY_PIPE_SEPARATOR);
			StringList poaIdList = FrameworkUtil.split(strPOAIds, PGWidgetConstants.KEY_PIPE_SEPARATOR);
			String strMCIds = jsonInputData.getString("MCIds");
			String strMode = jsonInputData.getString("mode");
			JsonObject jsonObjInstanceSequence = null;
			if(jsonInputData.containsKey("instancesequence")) {
				jsonObjInstanceSequence = jsonInputData.getJsonObject("instancesequence");
			}
			StringList mcIdList = FrameworkUtil.split(strMCIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			StringList selectList = StringList.create(DomainConstants.SELECT_ID, "last", "revisions", "revisions.id",
					DomainConstants.SELECT_NAME, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION,
					DomainConstants.SELECT_CURRENT, AWLAttribute.MARKETING_NAME.getSel(context));
			Map<?, ?> mapMCData = null;
			String MCName = null;

			Map excludeMCEError = new HashMap(); // Same Revision already connected
			Map excludeMCEWarning = new HashMap(); // Different Revision of same MCE already connected
			JsonArrayBuilder jArrIncludeList = Json.createArrayBuilder(); // Valid for new connection
			StringList slIncludeNames = new StringList(); // Valid for new connection
			Map MCEMap = new HashMap();
			MapList editableObjMapList = BusinessUtil.getInfo(context, mcIdList, selectList);
			for (int i = 0; i < editableObjMapList.size(); i++) {
				mapMCData = (Map<?, ?>) editableObjMapList.get(i);
				MCName = (String) mapMCData.get(DomainConstants.SELECT_NAME);
				MCEMap.put(MCName, mapMCData);
			}
			logger.log(Level.INFO, "MCEMap::" + MCEMap);
			for (String strPOAId : poaIdList) {
				if (BusinessUtil.isNotNullOrEmpty(strPOAId)) {
					POA poa = new POA(strPOAId);
					String POAName = poa.getInfo(context, DomainConstants.SELECT_NAME);
					MapList mlPOAMCs = poa.getArtworkMasters(context, selectList, AWLConstants.EMPTY_STRING);
					Map MCPOAMap = new HashMap();
					for (int i = 0; i < mlPOAMCs.size(); i++) {
						mapMCData = (Map<?, ?>) mlPOAMCs.get(i);
						MCName = (String) mapMCData.get(DomainConstants.SELECT_NAME);
						MCPOAMap.put(MCName, mapMCData);
					}
					Set setKeys = MCEMap.keySet();
					Iterator itr = setKeys.iterator();
					while (itr.hasNext()) {
						String keyMCEName = (String) itr.next();
						if (MCPOAMap.containsKey(keyMCEName)) {
							checkExistingMCEs(POAName, excludeMCEError, excludeMCEWarning, MCEMap, MCPOAMap,
									keyMCEName);
						} else {
							logger.log(Level.INFO, "POAName :: " + POAName);
							String strMasterId = (String) ((Map) MCEMap.get(keyMCEName))
									.get(DomainConstants.SELECT_ID);
							String strMasterName = (String) ((Map) MCEMap.get(keyMCEName)).get(DomainConstants.SELECT_NAME);
							logger.log(Level.INFO, "strMasterName :: " + strMasterName);
							logger.log(Level.INFO, "strMasterId :: " + strMasterId);
							if (strMode.equals("addExistingElement")) {
								strOutput = addMasterCopyNew(context, strPOAId, strMasterId);
							} else if (strMode.equals("addExistingCL")) {
								strOutput = connectMasterCopiesFromCLNew(context, strPOAId, strMasterId,
										jsonInputData.getString("copyListId"));
							} else if (strMode.equals("addArtworkElement")) {
								strOutput = addArtworkElementToPOA(context, strPOAId, strMasterId);
							}
							else if (strMode.equals("changeRevision")) {
								strOutput = addArtworkElementToPOA(context, strPOAId, strMasterId);
								if (PGWidgetConstants.KEY_SUCCESS.equalsIgnoreCase(strOutput)
										&& jsonObjInstanceSequence != null) {
									for (Map.Entry entry : jsonObjInstanceSequence.entrySet()) {
										String strPOAIdTemp = (String) entry.getKey();
										logger.log(Level.INFO, "strPOAIdTemp :: " + strPOAIdTemp);
										logger.log(Level.INFO, "instanceSeq :: "
												+ jsonObjInstanceSequence.getString(strPOAIdTemp));
										updateInstanceSequenceIdOnRel(context, strPOAIdTemp, strMasterId, jsonObjInstanceSequence.getString(strPOAIdTemp));
									}
								}
							}
							// Get the details of newly connected Master Copy and connected LCs
							HashMap mapFinalInfo = getPOAArtworkElementsForMce(context, allPOAIdList, strMasterId);
							// Convert the Map to JSON Format
							JsonArray jsonArrOut = convertMaptoJSONFormat(context, mapFinalInfo);
							jsonArrRowData = mergeArrays(jsonArrOut, jsonArrRowData);
							logger.log(Level.INFO, "strOutput :: " + strOutput);
							if (!slIncludeNames.contains(strMasterName)) {
								jArrIncludeList.add(strMasterId);
								slIncludeNames.add(strMasterName);
							}
						}
						logger.log(Level.INFO, "Valid for Connection :: " + jArrIncludeList.build());
					}
				}
			}
			output.add("rowData", jsonArrRowData);
			buildAddMCEReturnMessage(context, output, excludeMCEError, excludeMCEWarning, slIncludeNames, jArrIncludeList.build());
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		logger.log(Level.INFO, "output :: " + output.build());
		return output.build().toString();
	}

	public static void updateInstanceSequenceIdOnRel(Context context, String strPOAId, String strMasterId, String attribValue)
			throws Exception {
		POA poa = new POA(strPOAId);
		String strWhere = DomainConstants.SELECT_ID + "==" + strMasterId;
		MapList mlMCE = poa.getArtworkMasters(context, StringList.create(DomainConstants.SELECT_ID),
				StringList.create(PGWidgetConstants.SELECT_CONNECTION_ID), strWhere);
		if(mlMCE != null && !mlMCE.isEmpty()) {
			String strRelId = (String) ((Map) mlMCE.get(0)).get(PGWidgetConstants.SELECT_CONNECTION_ID);
			DomainRelationship.setAttributeValue(context, strRelId, AWLAttribute.INSTANCE_SEQUENCE.get(context), attribValue);
		}
	}
	
	private static void checkExistingMCEs(String POAName, Map excludeMCEError, Map excludeMCEWarning, Map MCEMap,
			Map MCPOAMap, String keyMCEName) {
		String typeConnected = (String) ((Map) MCPOAMap.get(keyMCEName))
				.get(DomainConstants.SELECT_TYPE);
		String typeToConnect = (String) ((Map) MCEMap.get(keyMCEName))
				.get(DomainConstants.SELECT_TYPE);
		if (typeToConnect.equals(typeConnected)) {
			String revConnected = (String) ((Map) MCPOAMap.get(keyMCEName))
					.get(DomainConstants.SELECT_REVISION);
			String revToConnect = (String) ((Map) MCEMap.get(keyMCEName))
					.get(DomainConstants.SELECT_REVISION);
			if (revConnected.equals(revToConnect)) {
				logger.log(Level.INFO, "Same MCE already connected :: " + keyMCEName);
				if (excludeMCEError.containsKey(POAName)) {
					((StringList) excludeMCEError.get(POAName)).add(keyMCEName);
				} else {
					excludeMCEError.put(POAName, new StringList(keyMCEName));
				}
			} else if (!revConnected.equals(revToConnect)) {
				logger.log(Level.INFO, "Different revisons of same MCE found :; " + keyMCEName);
				if (excludeMCEWarning.containsKey(POAName)) {
					((StringList) excludeMCEWarning.get(POAName)).add(keyMCEName);
				} else {
					excludeMCEWarning.put(POAName, new StringList(keyMCEName));
				}
			}
		}
	}
	public static void buildAddMCEReturnMessage(Context context, JsonObjectBuilder output, Map excludeMCEError, Map excludeMCEWarning,
			StringList slIncludeNames, JsonArray jArrIncludeList) {
			logger.log(Level.INFO, "excludeMCEWarning :: " + excludeMCEWarning);
			logger.log(Level.INFO, "excludeMCEError :: " + excludeMCEError);
		StringBuilder sbError = new StringBuilder();
		final String strAlreadyConnectedMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
				context.getLocale(), "emxAWL.Message.addCopyElement.AlreadyConnected");
		final String strDiffRevMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
				context.getLocale(), "emxAWL.Message.addCopyElement.DifferentRevisionConnected");
		final String strSuccessfulConnectionMsg = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
				context.getLocale(), "emxAWL.Message.addCopyElement.SuccessfulConnection");
			if (excludeMCEError.size() > 0) {
				if (slIncludeNames.size() > 0) {
				sbError.append(strSuccessfulConnectionMsg);
					sbError.append(slIncludeNames.join(", ")).append("\n");
				}
			sbError.append(strAlreadyConnectedMsg);
				Set setKeys = excludeMCEError.keySet();
				Iterator itr = setKeys.iterator();
				while (itr.hasNext()) {
					String itrKey = (String) itr.next();
					sbError.append(itrKey).append(": ").append(
							((StringList) (excludeMCEError.get(itrKey))).join(PGWidgetConstants.KEY_COMMA_SEPARATOR))
							.append("\n");
				}
			}
			if (excludeMCEWarning.size() > 0) {
				if (slIncludeNames.size() > 0 && excludeMCEError.size() == 0) {
				sbError.append(strSuccessfulConnectionMsg);
					sbError.append(slIncludeNames.join(", ")).append("\n");
				}
			sbError.append(strDiffRevMsg);
				Set setKeys = excludeMCEWarning.keySet();
				Iterator itr = setKeys.iterator();
				while (itr.hasNext()) {
					String itrKey = (String) itr.next();
					sbError.append(itrKey).append(": ").append(
							((StringList) (excludeMCEWarning.get(itrKey))).join(PGWidgetConstants.KEY_COMMA_SEPARATOR))
							.append("\n");
				}
			}
			if (sbError.length() > 0) {
				output.add("errorMsg", sbError.toString());
			}
			if (slIncludeNames.size() > 0) {
				if (excludeMCEError.size() == 0 && excludeMCEWarning.size() == 0) {
				output.add(PGWidgetConstants.KEY_SUCCESS,
						strSuccessfulConnectionMsg + slIncludeNames.join(PGWidgetConstants.KEY_COMMA_SEPARATOR));
				}
			output.add(PGWidgetConstants.KEY_DATA, jArrIncludeList);
			}
		}
	
	public static String addMasterCopyNew(Context context, String strPOAId, String strMasterId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutput = null;
		try {
			ArtworkMaster am = new ArtworkMaster(strMasterId);
			POA poa = new POA(strPOAId);
			StringList poaLanguages = poa.getLanguageNames(context);
			poa.addArtworkMaster(context, am);
			poa.addLocalCopiesToPOA(context, am, poaLanguages);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			strOutput = output.build().toString();
		}
		return strOutput;
	}
	public static String addArtworkElementToPOA(Context context, String strPOAId, String strMasterId) throws FrameworkException {
		StringList stringList = FrameworkUtil.split(strPOAId, ",");
	    String str = ArtworkUtil.addMasterCopiesToPOA(context, BusinessUtil.toStringList(new String[] { strMasterId } ), stringList);
		return str;
	}
	public static String connectMasterCopiesFromCLNew(Context context, String strPOAId, String strMasterId,
			String strCopyListId) throws Exception {
		try {
			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_ID);
			StringList slRelSelects = new StringList();
			slRelSelects.add(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
			POA objPOA = null;
			ArtworkMaster amMC = null;
			String strSequenceNumber = null;
			MapList mlCopyList = null;
			Boolean isFrom = true;
			Boolean isTo = false;
			objPOA = new POA(strPOAId);
			amMC = new ArtworkMaster(strMasterId);
			objPOA.addArtworkMasterAndElements(context, amMC);
			mlCopyList = amMC.getRelatedObjects(context, // context,
					AWLRel.COPY_LIST_ARTWORK_MASTER.get(context), // relationshipPattern,
					AWLType.COPY_LIST.get(context), // typePattern
					slObjSelects, // objectSelects
					slRelSelects, // relationshipSelects,
					isFrom, // getFrom
					isTo, // getTo,
					(short) 1, // recurseToLevel,
					(DomainConstants.SELECT_ID).concat("==".concat(strCopyListId)), // objectWhere,
					null, // relWhere
					0); // limit
			for (Map<?, ?> mCL : (List<Map>) mlCopyList) {
				strSequenceNumber = (String) mCL.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
				if (BusinessUtil.isNotNullOrEmpty(strSequenceNumber)) {
					objPOA.setSequenceNumber(context, strMasterId, Integer.valueOf(strSequenceNumber));
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, e.getMessage())
					.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e)).build().toString();
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build().toString();
	}
	public static String checkMCEOriginator(Context context, String paramString) throws Exception {

		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String strMCId = jsonInputData.getString("MCIds");
		String strLCId = jsonInputData.getString(RTAUtilConstants.KEY_LCE_ID);
		boolean isValidOriginator = true;
		boolean isContextPushed = false;
		boolean isRevClaims = false;
		Person loggedInPerson = Person.getPerson(context);
		String loggedInUser = loggedInPerson.getName();
		StringList slObjSelects = new StringList();
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATOR);
		slObjSelects.add("to["+RTAUtilConstants.RELATIONSHIP_PGMASTERCOPYCLAIM+"].from.id");
		String strOriginatorName = EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.IntegrationUsers");
		StringList slOriginatorNames = FrameworkUtil.split(strOriginatorName, ",");
		String strLoggedInUserName = EnoviaResourceBundle.getProperty(context, "emxAWL.Message.ItegrationLoggedInUserNames");
		try {
			ContextUtil.pushContext(context);
			isContextPushed = true;
			DomainObject domMCObj = DomainObject.newInstance(context, strMCId);
			Map mapMCInfo = domMCObj.getInfo(context, slObjSelects);
			String strOriginator = (String) mapMCInfo.get(DomainConstants.SELECT_ORIGINATOR);
			String strClaimId = (String) mapMCInfo.get("to["+RTAUtilConstants.RELATIONSHIP_PGMASTERCOPYCLAIM+"].from.id");
			if (BusinessUtil.isNotNullOrEmpty(strOriginator) && slOriginatorNames.contains(strOriginator)) {
				if (!slOriginatorNames.contains(loggedInUser)) {
					isValidOriginator = false;
				}
			} else if (BusinessUtil.isNotNullOrEmpty(strClaimId)) {
				String strBaseLCId = PGRTADCMDataLoad.getBaseLocalCopyInfoForMaster(context, strMCId);
				if(BusinessUtil.isNotNullOrEmpty(strLCId) && !RTAUtilConstants.KEY_ROOT_NODE_ID.equals(strLCId)) {
					if (UIUtil.isNotNullAndNotEmpty(strBaseLCId)) {
						if(!strLCId.equals(strBaseLCId)) {
							isRevClaims = true;
						}
					}	
				}
				if (!loggedInUser.equals(strLoggedInUserName))
					isValidOriginator = false;
			}
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage());
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(RTAUtilConstants.KEY_IS_VALID_ORIGINATOR, isValidOriginator)
					.add(RTAUtilConstants.KEY_REV_CLAIMS_LC, isRevClaims).build().toString();
		} finally {
			if (isContextPushed)
				ContextUtil.popContext(context);
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
				.add(RTAUtilConstants.KEY_IS_VALID_ORIGINATOR, isValidOriginator)
				.add(RTAUtilConstants.KEY_REV_CLAIMS_LC, isRevClaims).build().toString();
	}
	
	//assignauthor copy Task Status WS Start
	//RTA DS - CW 22x-01 - Modified for ALM-44247 - Start
	public static MapList getTasksConnectedToLC(Context context, String sObjId) throws Exception
	{
		boolean bIsContextPushed = false;	
		try{
		//Pushing the context to fetch claim details	
		if (!ArtworkConstants.PERSON_USERAGENT.equalsIgnoreCase(context.getUser())) {
			ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT, null, context.getVault().getName());
			bIsContextPushed = true;
		}	
		MapList mlTaskList = new MapList();	
		MapList mlPendingTaskList = new MapList();	
		String strSelRoute = "from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]";
		String strSelIsBaseCopy = DomainObject.getAttributeSelect(AWLAttribute.IS_BASE_COPY.get(context));
		
		StringList selectsList = new StringList();
		selectsList.add(DomainConstants.SELECT_TYPE);
		selectsList.add(DomainConstants.SELECT_NAME);
		selectsList.add(DomainConstants.SELECT_ID);	
		selectsList.add("attribute["+ RTAUtilConstants.ATTRIBUTE_MARKETINGNAME + "]");	
		selectsList.add("attribute[" + RTAUtilConstants.ATTRIBUTE_CELANGUAGE + "]");
		selectsList.add(RTAUtilConstants.SELECT_APPROVER_ID);	
		selectsList.add(RTAUtilConstants.SELECT_AUTHOR_ID);
		selectsList.add(DomainConstants.SELECT_CURRENT);
		selectsList.add(strSelRoute);
		selectsList.add(strSelIsBaseCopy);
		selectsList.add(RTAUtilConstants.SELECT_RTA_BG_JOB_STATUS);
		
		Map map = null;
		String strObjId = null;
		String strIsRouteConnected = null;
		
		DomainObject domObj = null;
		DomainObject doBusObj = DomainObject.newInstance(context, sObjId);
		String relName = RTAUtilConstants.REL_CLARTWORKMASTER + "," + RTAUtilConstants.REL_ARTWORKELEMENTCONTENT ;

		MapList mlTaskListTemp = doBusObj.getRelatedObjects(context,
									relName,	//Rel name
									DomainConstants.QUERY_WILDCARD, //Type name
									selectsList,//Object Select
									DomainConstants.EMPTY_STRINGLIST,//rel Select
									false,//get To
									true,//get From
									(short)2,//recurse level
									null,//bus where Clause
									null,//Rel where
									0);		
		
		long start = System.currentTimeMillis();	
		String strApproverId = null;
		String strApproverName = null;
		String strAuthorId = null;
		String strAuthorName = null;
		Map mAandAPersonInfo = new HashMap();
		for(int i = 0 ; i < mlTaskListTemp.size(); i++) {
			map = (Map) mlTaskListTemp.get(i);
			strObjId = (String) map.get(DomainConstants.SELECT_ID);
			if(BusinessUtil.isNotNullOrEmpty(strObjId)) {
				domObj = DomainObject.newInstance(context,strObjId);
				if(domObj.isKindOf(context, AWLType.COPY_ELEMENT.get(context))) {					
					map.put(DomainConstants.SELECT_LEVEL, "1");
					strIsRouteConnected = (String) map.get(strSelRoute);
					if("true".equalsIgnoreCase(strIsRouteConnected)) {			
						mlPendingTaskList.add(map);		
						getAllTasksConnectedToRoute(context, strObjId, map, mlTaskList, mAandAPersonInfo);					
					}else {
						strApproverId = (String)map.get(RTAUtilConstants.SELECT_APPROVER_ID);
						strApproverName = getAssigneeDisplayName(context, strApproverId, mAandAPersonInfo);
						strAuthorId = (String)map.get(RTAUtilConstants.SELECT_AUTHOR_ID);
						strAuthorName = getAssigneeDisplayName(context, strAuthorId, mAandAPersonInfo);
						map.put(RTAUtilConstants.SELECT_APPROVER_NAME, strApproverName);
						map.put(RTAUtilConstants.SELECT_AUTHOR_NAME, strAuthorName);							
						mlTaskList.add(map);
					}				
				}
			}
		}
		logger.log(Level.INFO, "Time taken for loading ALL LCs ---------------> - {0}", (System.currentTimeMillis()-start));	
		return mlTaskList;

	} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
			logger.log(Level.SEVERE, e.getMessage());
			return new MapList();
		}finally {
			if (bIsContextPushed) {
				ContextUtil.popContext(context);
			}			
		}
	}
		//RTA DS - CW 22x-01 - Modified for ALM-44247 - End
		
	//RTA DS - CW 22x-01 - Modified for ALM-44247 - Start
	public static void getAllTasksConnectedToRoute(Context context, String strCEId, Map mCEInfo, MapList mlTaskList,
			Map mAandAPersonInfo) throws Exception {
		String strAuthoringRouteId = null;
		String strApproveRouteId = null;
		String strRouteNodeId = null;
		String strAuthorRouteState = null;
		String strApproveRouteState = null;
		String strSelAttriDuteDateOffset = DomainObject
				.getAttributeSelect(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);

		StringList slRouteNodeSelects = new StringList(3);
		slRouteNodeSelects.add(DomainConstants.SELECT_NAME);
		slRouteNodeSelects.add(DomainConstants.SELECT_TYPE);
		slRouteNodeSelects.add(DomainConstants.SELECT_ID);

		StringList slRouteNodeRelSelects = new StringList(4);
		slRouteNodeRelSelects.add(DomainRelationship.SELECT_ID);
		slRouteNodeRelSelects.add(DomainRelationship.SELECT_NAME);
		slRouteNodeRelSelects.add(PHYSICALID_CONNECTION);
		slRouteNodeRelSelects.add(SEL_ATTR_SCHEDULED_COMPLETION_DATE);
		slRouteNodeRelSelects.add(strSelAttriDuteDateOffset);

		boolean isMultiAuthors = false;
		boolean isMultiApprovers = false;

		Iterator<?> itrRouteNodes = null;

		Map mCEAuthorRouteInfo = new HashMap();
		Map mCEApproveRouteInfo = new HashMap();
		Map mFinalInfo = null;
		Map mRouteNodeInfo = null;
		MapList mlAuthoringRoutes = null;
		MapList mlApproveRoutes = null;

		Map mCERouteAndRouteNodeInfo = getCEConnectedRoutes(context, strCEId);

		mlAuthoringRoutes = (MapList) mCERouteAndRouteNodeInfo.get("AuthorRoutes");
		mlApproveRoutes = (MapList) mCERouteAndRouteNodeInfo.get("ApproverRoutes");
		Map mCERouteNodeInfo = (Map) mCERouteAndRouteNodeInfo.get("RouteIdRouteNodeInfo");

		mlAuthoringRoutes.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
		mlApproveRoutes.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");

		if (mlAuthoringRoutes != null && BusinessUtil.isNotNullOrEmpty(mlAuthoringRoutes)) {
			strAuthoringRouteId = (String) ((Map) mlAuthoringRoutes.get(0)).get(DomainConstants.SELECT_ID);
			if (BusinessUtil.isNotNullOrEmpty(strAuthoringRouteId)) {
				// Fetch the Route Task Info
				mCEAuthorRouteInfo = getArtworkRouteTasksInfo(context, strAuthoringRouteId, "Authoring");
			}
		}
		if (mlApproveRoutes != null && BusinessUtil.isNotNullOrEmpty(mlApproveRoutes)) {
			strApproveRouteId = (String) ((Map) mlApproveRoutes.get(0)).get(DomainConstants.SELECT_ID);
			if (BusinessUtil.isNotNullOrEmpty(strApproveRouteId)) {
				mCEApproveRouteInfo = getArtworkRouteTasksInfo(context, strApproveRouteId, "Approval");
			}
		}
		// Fetch the Route Node Info
		MapList mlAuthorRouteNodeInfo = new MapList();
		MapList mlApproverRouteNodeInfo = new MapList();

		if (BusinessUtil.isNotNullOrEmpty(strAuthoringRouteId)) {
			mlAuthorRouteNodeInfo = (MapList) mCERouteNodeInfo.get(strAuthoringRouteId);
		}
		if (BusinessUtil.isNotNullOrEmpty(strApproveRouteId)) {
			mlApproverRouteNodeInfo = (MapList) mCERouteNodeInfo.get(strApproveRouteId);
		}

		isMultiAuthors = (null != mlAuthorRouteNodeInfo && mlAuthorRouteNodeInfo.size() > 1) ? Boolean.TRUE
				: Boolean.FALSE;
		isMultiApprovers = (null != mlApproverRouteNodeInfo && mlApproverRouteNodeInfo.size() > 1) ? Boolean.TRUE
				: Boolean.FALSE;
		String strApproverId = (String) mCEInfo.get(RTAUtilConstants.SELECT_APPROVER_ID);
		String strApproverName = getAssigneeDisplayName(context, strApproverId, mAandAPersonInfo);
		String strAuthorId = (String) mCEInfo.get(RTAUtilConstants.SELECT_AUTHOR_ID);
		String strAuthorName = getAssigneeDisplayName(context, strAuthorId, mAandAPersonInfo);
		if (!isMultiAuthors && !isMultiApprovers) {
			mFinalInfo = new HashMap();

			// Add CE Info
			mFinalInfo.putAll(mCEInfo);
			mFinalInfo.put(RTAUtilConstants.SELECT_APPROVER_NAME, strApproverName);
			mFinalInfo.put(RTAUtilConstants.SELECT_AUTHOR_NAME, strAuthorName);
			// Author Info
			if (mlAuthorRouteNodeInfo != null && BusinessUtil.isNotNullOrEmpty(mlAuthorRouteNodeInfo)) {
				mRouteNodeInfo = (Map) mlAuthorRouteNodeInfo.get(0);
				strRouteNodeId = (String) mRouteNodeInfo.get(PHYSICALID_CONNECTION);
				if (mCEAuthorRouteInfo.containsKey(strRouteNodeId)) {
					mFinalInfo.putAll((Map) mCEAuthorRouteInfo.get(strRouteNodeId));
				} else {
					mFinalInfo.put(DomainRelationship.SELECT_ID, mRouteNodeInfo.get(DomainRelationship.SELECT_ID));
				}
				mFinalInfo.put(AUTHORING_DURATION, mRouteNodeInfo.get(strSelAttriDuteDateOffset));
				strAuthorRouteState = (String) ((Map) mlAuthoringRoutes.get(0)).get(DomainConstants.SELECT_CURRENT);
				hasEditAccessForAuthorApproverDueDate(strAuthorRouteState, mFinalInfo, true);
			}
			// Approver Info
			if (mlApproverRouteNodeInfo != null && BusinessUtil.isNotNullOrEmpty(mlApproverRouteNodeInfo)) {
				mRouteNodeInfo = (Map) mlApproverRouteNodeInfo.get(0);
				strRouteNodeId = (String) mRouteNodeInfo.get(PHYSICALID_CONNECTION);
				if (mCEApproveRouteInfo.containsKey(strRouteNodeId)) {
					mFinalInfo.putAll((Map) mCEApproveRouteInfo.get(strRouteNodeId));
				} else {
					mFinalInfo.put(DomainRelationship.SELECT_ID, mRouteNodeInfo.get(DomainRelationship.SELECT_ID));
				}
				mFinalInfo.put(APPROVAL_DURATION, mRouteNodeInfo.get(strSelAttriDuteDateOffset));
				strApproveRouteState = (String) ((Map) mlApproveRoutes.get(0)).get(DomainConstants.SELECT_CURRENT);
				hasEditAccessForAuthorApproverDueDate(strApproveRouteState, mFinalInfo, false);
			}
			mlTaskList.add(mFinalInfo);
		} else {
			if (mlAuthorRouteNodeInfo != null && mlAuthorRouteNodeInfo.size() > 0) {
				itrRouteNodes = mlAuthorRouteNodeInfo.iterator();
				while (itrRouteNodes.hasNext()) {
					mRouteNodeInfo = (Map) itrRouteNodes.next();
					strRouteNodeId = (String) mRouteNodeInfo.get(PHYSICALID_CONNECTION);
					mFinalInfo = new HashMap();
					mFinalInfo.putAll(mCEInfo);
					if (BusinessUtil.isNotNullOrEmpty(strRouteNodeId)
							&& mCEAuthorRouteInfo.containsKey(strRouteNodeId)) {
						mFinalInfo.putAll((Map) mCEAuthorRouteInfo.get(strRouteNodeId));
						checkTaskDueDateAndDurationEditAccess((Map) mCEAuthorRouteInfo.get(strRouteNodeId), mFinalInfo,
								true);
					} else {
						mFinalInfo.put(DomainRelationship.SELECT_ID, mRouteNodeInfo.get(DomainRelationship.SELECT_ID));
						mFinalInfo.put("IsAuthorDuration", "true");
					}

					mFinalInfo.put(RTAUtilConstants.SELECT_AUTHOR_NAME, strAuthorName);
					mFinalInfo.put(RTAUtilConstants.SELECT_APPROVER_NAME, strApproverName);

					mFinalInfo.put(AUTHORING_DURATION, mRouteNodeInfo.get(strSelAttriDuteDateOffset));
					mlTaskList.add(mFinalInfo);
				}
			}
			if (mlApproverRouteNodeInfo != null && mlApproverRouteNodeInfo.size() > 0) {
				itrRouteNodes = mlApproverRouteNodeInfo.iterator();
				while (itrRouteNodes.hasNext()) {
					mRouteNodeInfo = (Map) itrRouteNodes.next();
					strRouteNodeId = (String) mRouteNodeInfo.get(PHYSICALID_CONNECTION);
					mFinalInfo = new HashMap();
					mFinalInfo.putAll(mCEInfo);
					if (BusinessUtil.isNotNullOrEmpty(strRouteNodeId)
							&& mCEApproveRouteInfo.containsKey(strRouteNodeId)) {
						mFinalInfo.putAll((Map) mCEApproveRouteInfo.get(strRouteNodeId));
						checkTaskDueDateAndDurationEditAccess((Map) mCEApproveRouteInfo.get(strRouteNodeId),
								mFinalInfo, false);
					} else {
						mFinalInfo.put(DomainRelationship.SELECT_ID,
								mRouteNodeInfo.get(DomainRelationship.SELECT_ID));
						mFinalInfo.put("IsApproveDuration", "true");
					}
					mFinalInfo.put(RTAUtilConstants.SELECT_AUTHOR_NAME, strAuthorName);
					mFinalInfo.put(RTAUtilConstants.SELECT_APPROVER_NAME, strApproverName);
					mFinalInfo.put(APPROVAL_DURATION, mRouteNodeInfo.get(strSelAttriDuteDateOffset));
					mlTaskList.add(mFinalInfo);
				}
			}
		}
	}
	//RTA DS - CW 22x-01 - Modified for ALM-44247 - End
	/**
	 * Method to check edit access on DueDate and Duration fields. 
	 * @param mTaskInfo - Inbox Task Info
	 * @param mFinalInfo - Final Info Map
	 * @param isAuthor - Is Author
	 */
	private static void checkTaskDueDateAndDurationEditAccess(Map mTaskInfo, Map mFinalInfo, boolean isAuthor) {
		String strTaskState = (String)mTaskInfo.get("TaskState");
		if(BusinessUtil.isNotNullOrEmpty(strTaskState) && strTaskState.equals(DomainConstants.STATE_INBOX_TASK_ASSIGNED)) {
			if(isAuthor) {
				mFinalInfo.put("IsAuthorDuration", "true");
				mFinalInfo.put("IsAuthorDueDate", "true");				
			} else {
				mFinalInfo.put("IsApproveDuration", "true");
				mFinalInfo.put("IsApproveDueDate", "true");			
			}
		}		
	}
	
	/**
	 * Method to check edit access on DueDate and Duration fields. 
	 * @param strRouteState - Route state
	 * @param mRouteInfo - Route Info
	 * @param isAuthor - Is Author
	 * @throws Exception
	 */
	private static void hasEditAccessForAuthorApproverDueDate(String strRouteState, Map mRouteInfo, boolean isAuthor) throws Exception{
		if(BusinessUtil.isNullOrEmpty(strRouteState)) {
			return;
		}
		if(strRouteState.equals(DomainConstants.STATE_ROUTE_DEFINE)) {
			if(isAuthor) {
				mRouteInfo.put("IsAuthorDuration", "true");
			} else {
				mRouteInfo.put("IsApproveDuration", "true");
			}
		} else if(strRouteState.equals(DomainConstants.STATE_ROUTE_IN_PROCESS)) {
			if(isAuthor) {
				mRouteInfo.put("IsAuthorDuration", "true");
				mRouteInfo.put("IsAuthorDueDate", "true");
			} else {
				mRouteInfo.put("IsApproveDuration", "true");
				mRouteInfo.put("IsApproveDueDate", "true");
			}			
		}
	}
	
	//RTA DS - CW 22x-01 - Added for ALM-44247 - Start
	/**
	 * Method to get the LC information used in table column.
	 * @param context
	 * @param strRouteId - Route Id
	 * @param strAuthorOrApprover - Prefix for author or approver
	 * @return Map containing task info
	 * @throws Exception
	 */
	private static Map getArtworkRouteTasksInfo(Context context, String strRouteId, String strAuthorOrApprover) throws Exception{

		StringBuilder sbTaskName = new StringBuilder();
		StringBuilder sbTaskSD = new StringBuilder();
		StringBuilder sbTaskAD = new StringBuilder();
		String attrRouteNodeId = AWLAttribute.ROUTE_NODE_ID.getSel(context);
		String strTaskName = null;
		String strTaskSD = null;
		String strTaskAD = null;
		String strTaskStatus = null;
		String strRouteNodeId = null;
		
		StringList objSelectsList = new StringList();
		objSelectsList.add(DomainConstants.SELECT_ID);
		objSelectsList.add(DomainConstants.SELECT_NAME);
		objSelectsList.add(DomainConstants.SELECT_CURRENT);
		objSelectsList.add(SEL_ATTR_SCHEDULED_COMPLETION_DATE);
		objSelectsList.add(SEL_ATTR_ACTUAL_COMPLETION_DATE);
		objSelectsList.add(attrRouteNodeId);
		
		StringList relSelectsList = new StringList();
		relSelectsList.add(DomainRelationship.SELECT_ID);
		relSelectsList.add("to.attribute["+RTAUtilConstants.ATTRIBUTE_ARTWORKINFO+"]");

		Route route = new Route(strRouteId);
		
		MapList mlRouteTaskList = route.getRouteTasks(context, objSelectsList, relSelectsList, null, false);
		Iterator itrTasks = mlRouteTaskList.iterator();
		
		Map mapRouteTask = null;
		Map mapRouteTaskInfo = null;
		Map mRouteNodeTaskInfo = new HashMap();
		
		while(itrTasks.hasNext())
		{
			mapRouteTask = (Map) itrTasks.next();
			mapRouteTaskInfo = new HashMap(); 
			strRouteNodeId = (String) mapRouteTask.get(attrRouteNodeId);
			strTaskName = (String) mapRouteTask.get(DomainConstants.SELECT_NAME);
			strTaskSD = (String) mapRouteTask.get(SEL_ATTR_SCHEDULED_COMPLETION_DATE);
			strTaskAD = (String) mapRouteTask.get(SEL_ATTR_ACTUAL_COMPLETION_DATE);
			
			strTaskStatus = checkForTaskStatus(strTaskSD, strTaskAD, true);
			
			
			strTaskSD = getParsedDate(context,strTaskSD);
			strTaskAD = getParsedDate(context,strTaskAD);
						
			if(sbTaskName.length()!=0)
			{
				sbTaskName.append("<br></br>");
				sbTaskSD.append("<br></br>");
			}
			if(sbTaskAD.length()!=0) {
				sbTaskAD.append("<br></br>");
			}					
			sbTaskName.append(strTaskName);
			sbTaskSD.append(strTaskSD);
			sbTaskAD.append(strTaskAD);

			mapRouteTaskInfo.put(strAuthorOrApprover+"TaskName",strTaskName);
			mapRouteTaskInfo.put(strAuthorOrApprover+"TaskSD",strTaskSD);
			mapRouteTaskInfo.put(strAuthorOrApprover+"TaskAD",strTaskAD);	
			mapRouteTaskInfo.put(strAuthorOrApprover+"TaskStatus", strTaskStatus);
			mapRouteTaskInfo.put("TaskState", (String) mapRouteTask.get(DomainConstants.SELECT_CURRENT));
			mapRouteTaskInfo.put(DomainRelationship.SELECT_ID, mapRouteTask.get(DomainRelationship.SELECT_ID));
			mapRouteTaskInfo.put("TaskId", mapRouteTask.get(DomainConstants.SELECT_ID));
			mRouteNodeTaskInfo.put(strRouteNodeId, mapRouteTaskInfo);
		}
		
		mRouteNodeTaskInfo.put("TaskName", sbTaskName.toString());
		mRouteNodeTaskInfo.put("TaskSD", sbTaskSD.toString());
		mRouteNodeTaskInfo.put("TaskAD", sbTaskAD.toString());
		mRouteNodeTaskInfo.put("RouteId", strRouteId);
		
		return mRouteNodeTaskInfo;
	}
	//RTA DS - CW 22x-01 - Added for ALM-44247 - End
	
	//RTA DS - CW 22x-01 - Added for ALM-44247 - Start
	/**
	 * Method to return the task status icon
	 * @param strDueDate - Task due date
	 * @param strActualCompletionDate - Actual Completion date
	 * @param isTaskActive - Is task active
	 * @param isRouteNode - Is Route Node
	 * @return - Icon
	 * @throws Exception
	 */
	private static String checkForTaskStatus(String strDueDate, String strActualCompletionDate, boolean isTaskActive) throws Exception{
		
		String strTaskStatus = DomainConstants.EMPTY_STRING;		
		try {
			Date dueDate = null;
			if(BusinessUtil.isNotNullOrEmpty(strDueDate)) {
				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
				dueDate = formatter.parse(strDueDate);
				Calendar calDueDate = Calendar.getInstance();
				calDueDate.setTime(dueDate);
				calDueDate.set(Calendar.MINUTE, 0);
				calDueDate.set(Calendar.SECOND, 0);
				calDueDate.set(Calendar.HOUR_OF_DAY, 17);
				calDueDate.set(Calendar.AM_PM, 0);
				calDueDate.set(Calendar.HOUR, 0);
				calDueDate.set(Calendar.MILLISECOND, 0);
				Date completionDate = null;
				Calendar cal = Calendar.getInstance();
				
				if(BusinessUtil.isNotNullOrEmpty(strActualCompletionDate)) {
					completionDate = formatter.parse(strActualCompletionDate);
					cal.setTime(completionDate);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.HOUR_OF_DAY, 17);					
					cal.set(Calendar.AM_PM, 0);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MILLISECOND, 0);										
					if(cal.equals(calDueDate) || cal.before(calDueDate)) {
						strTaskStatus = "<center><img src='../awl/images/AWLApprovedForRelease.gif' title='On Time Completed' alt='On Time Completed' /></center>";												
					}else if(cal.after(calDueDate)) {
						strTaskStatus = "<center><img src='../awl/images/pgAWLRedTick.gif' title='Not Completed On Time' alt='Not Completed On Time' /></center>";
					}					
				}else {
					completionDate = new Date();
					cal.setTime(completionDate);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.HOUR_OF_DAY, 17);
					cal.set(Calendar.AM_PM, 0);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MILLISECOND, 0);					
					if((cal.before(calDueDate) || cal.equals(calDueDate)) && isTaskActive) {
						strTaskStatus = "<center><img src='../common/images/pgCPDTaskStatusBlue.gif' title='In Process' alt='In Process' /></center>";
					}else if(cal.after(calDueDate)) {	
						strTaskStatus = "<center><img src='../common/images/CPDTaskStatusRed.gif' title='Delayed' alt='Delayed' /></center>";
					}					
				}				
			}
		}catch(Exception ex) {
			logger.log(Level.SEVERE, ex.toString());
			logger.log(Level.SEVERE, ex.getMessage());			
		}
		return strTaskStatus;
	}
	//RTA DS - CW 22x-01 - Added for ALM-44247 - End
	
	public static String getParsedDate(Context context, String strData) throws Exception
	{
		String strDate = "";
		try{
			if(BusinessUtil.isNotNullOrEmpty(strData)){
				strDate = (String) (getConvertedDate(context,(strData)));
			}
		} catch(Exception ex){
			throw ex;
		}
		return strDate;
	}
	public static String getConvertedDate (Context context,String strDateToConvert) throws Exception
	{
		String strDate = "";
		try {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(strDateToConvert);  
		strDate = formatter.format(date);  
		} catch(Exception ex)
		{
			throw ex;
		}
		return strDate;
	}
	
	public static String getAssigneeDisplayName(Context context, String objectId, Map mAandAPersonInfo)
			throws FrameworkException {
		String displayName = "";
		try {
			if (BusinessUtil.isNullOrEmpty(objectId))
				return "";

			if (mAandAPersonInfo != null && mAandAPersonInfo.containsKey(objectId))
				return (String) mAandAPersonInfo.get(objectId);

			StringList selects = BusinessUtil.toStringList(DomainConstants.SELECT_TYPE,
					DomainConstants.SELECT_NAME);

			Map selectMap = BusinessUtil.getInfo(context, objectId, selects);
			String objectType = (String) selectMap.get(DomainConstants.SELECT_TYPE);
			String objectName = (String) selectMap.get(DomainConstants.SELECT_NAME);
			if (DomainConstants.TYPE_PERSON.equalsIgnoreCase(objectType)) {
				displayName = PersonUtil.getFullName(context, objectName);
			} else {
				StringList selectList = BusinessUtil.toStringList(DomainConstants.SELECT_DESCRIPTION,
						AWLAttribute.ARTWORK_INFO.getSel(context));
				Map newInfoMap = BusinessUtil.getInfo(context, objectId, selectList);
				String artworkInfo = (String) newInfoMap.get(AWLAttribute.ARTWORK_INFO.getSel(context));
				if (BusinessUtil.isNullOrEmpty(artworkInfo)) {
					displayName = objectName;
				} else {
					if (artworkInfo.indexOf("=") != -1) {
						displayName = (String) newInfoMap.get(DomainConstants.SELECT_DESCRIPTION);
					} else {
						displayName = getAssigneeDisplayNameByArtowrkInfo(context, artworkInfo);
					}
				}
			}
			if (mAandAPersonInfo != null && BusinessUtil.isNotNullOrEmpty(displayName)) {
				mAandAPersonInfo.put(objectId, displayName);
			}
		} catch (Exception ex) {
			throw new FrameworkException(ex);
		}
		return displayName;
	}

	public static String getAssigneeDisplayNameByArtowrkInfo(Context context, String artworkInfo) throws FrameworkException {
		
		SortedMap<String, SortedSet> assigneesBySeqMap = getAssigneesBySeqMap(context, artworkInfo);


		StringList assigneeBySeqList	= new StringList();
		for (String sequence : assigneesBySeqMap.keySet()) {
			SortedSet assignees = assigneesBySeqMap.get(sequence);
			List<String> alist	= new ArrayList<String>(assignees);
			for(String assigneeId: alist) {
				String assigneeFullName	= PersonUtil.getFullName(context, 
												BusinessUtil.getInfo(context, assigneeId, DomainConstants.SELECT_NAME));
				assigneeBySeqList.add( assigneeFullName + " (" + sequence + ")");
			}
		}


		return FrameworkUtil.join(assigneeBySeqList, ",");
	}
	
	public static SortedMap<String, SortedSet> getAssigneesBySeqMap(Context context, String artworkInfo) {
		SortedMap assigneesBySeqMap = artworkInfo.indexOf("=") != -1 ? 
											getAssigneesBySeqMapBasedOnNewImpl(context, artworkInfo) :
												getAssigneesBySeqMapBasedOnOldImpl(context, artworkInfo);
		return assigneesBySeqMap;
	}
	
	public static SortedMap<String, SortedSet> getAssigneesBySeqMapBasedOnNewImpl(Context context, String artworkInfo) {
		SortedMap assigneesBySeqMap = new TreeMap<String, SortedSet>();


		StringList assigneesBySeqList = FrameworkUtil.split(artworkInfo, "|");
		
		for(int itr=0; itr<assigneesBySeqList.size(); itr++) {
			StringList assigneeBySeqList = FrameworkUtil.split(
												(String)assigneesBySeqList.get(itr), "=");
			
			if(assigneeBySeqList.size() == 2) {
				String sequence = (String) assigneeBySeqList.get(0);
				String assignees= (String) assigneeBySeqList.get(1);
				
				assigneesBySeqMap.put(sequence, 
						new TreeSet<String>(FrameworkUtil.split(assignees, ",")));
			}
		}
		
		return assigneesBySeqMap;
	}
	public static SortedMap<String, SortedSet> getAssigneesBySeqMapBasedOnOldImpl(Context context, String artworkInfo) {
		SortedMap assigneesBySeqMap = new TreeMap<String, SortedSet>();


		StringList assigneesBySeqList = FrameworkUtil.split(artworkInfo, "|");
		
		for(int itr=0; itr<assigneesBySeqList.size(); itr++) {
			StringList assigneeBySeqList = FrameworkUtil.split(
												(String)assigneesBySeqList.get(itr), ",");
			
			if(assigneeBySeqList.size() == 2) {
				String assignee = (String) assigneeBySeqList.get(0);
				String sequence = (String) assigneeBySeqList.get(1);
				
				if(!assigneesBySeqMap.containsKey(sequence)) {
					assigneesBySeqMap.put(sequence, new TreeSet<String>());
				}
				SortedSet assignees	= (SortedSet) assigneesBySeqMap.get(sequence);
				assignees.add(assignee);
			}
		}


		SortedMap assigneesBySeqOrderedMap = new TreeMap<String, SortedSet>();
		int itr = 1;
		Iterator iterator = assigneesBySeqMap.keySet().iterator();
		while (iterator.hasNext()) {
			String sequence = (String) iterator.next();
			assigneesBySeqOrderedMap.put(Integer.toString(itr),
									assigneesBySeqMap.get(sequence));
			itr++;
		}


		return assigneesBySeqOrderedMap;
	}

	//Assign Author COpy Task Status WS End
	
	public static String getAuthorApproverInfo(Context context, String paramString) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		MCSURL = jsonInputData.getString(KEY_MCSURL);
		String strMCEIds = jsonInputData.getString("MCEIds");
		String strLanguages = jsonInputData.getString("Languages");
		String strMode = jsonInputData.getString("mode");
		JsonObject mceIDLang = jsonInputData.getJsonObject("mceIdLanguageMap");
		StringList mceIdList = FrameworkUtil.split(strMCEIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		String strObjIds = jsonInputData.getString("objIds");
		StringList objIdList =  FrameworkUtil.split(strObjIds, PGWidgetConstants.KEY_PIPE_SEPARATOR);
		JsonArray jsonOut = getMCandLCForPOAs(context, mceIdList, FrameworkUtil.split(strLanguages, PGWidgetConstants.KEY_COMMA_SEPARATOR), mceIDLang, strMode, objIdList);
		return jsonOut.toString();
	}

	public static JsonArray getMCandLCForPOAs(Context context, StringList mceIdList, StringList slLanguages, JsonObject mceIDLang, String strMode, StringList objIdList)
			throws Exception {

		JsonArrayBuilder jsonArrCopyElems = Json.createArrayBuilder();
		try {
			StringList selectList = StringList.create(DomainConstants.SELECT_ID, KEY_LAST, KEY_REVISIONS, "revisions.id",
					DomainConstants.SELECT_NAME, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION,
					DomainConstants.SELECT_CURRENT, AWLAttribute.MARKETING_NAME.getSel(context),
					AWLAttribute.TRANSLATE.getSel(context), AWLAttribute.INLINE_TRANSLATION.getSel(context),
					RTAUtilConstants.SELECT_PGRTAGPSFIXEDCES, RTAUtilConstants.SELECT_AUTOMRKCLAIMRESULTS, RTAUtilConstants.SELECT_PGRTAGPSFIXEDRES,
					RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT, STR_MCEReviseStatus);

			selectList.add("from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "|to.attribute["
					+ AWLAttribute.IS_BASE_COPY.get(context) + "]=='Yes'].to.attribute[Copy Text_RTE]");
			selectList.add("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");

			MapList mlMCEInfoList = DomainObject.getInfo(context, mceIdList.toStringArray(), selectList);

			jsonArrCopyElems = buildJsonForAA(context, mlMCEInfoList, slLanguages, mceIDLang, strMode, objIdList);
		} catch (Exception e) {
			throw e;
		}

		return jsonArrCopyElems.build();

	}

	public static JsonArrayBuilder buildJsonForAA(Context context, MapList mlMCEInfoList, StringList slLanguages1, JsonObject mceIDLang, String strMode, StringList objIdList)
			throws Exception {
		JsonArrayBuilder jsonArrOut = Json.createArrayBuilder();
		for (int i = 0; i < mlMCEInfoList.size(); i++) {
			JsonObjectBuilder jsonObjMCE = Json.createObjectBuilder();
			Map mapMCEInfo = (Map) mlMCEInfoList.get(i);
			String strMCId = (String) mapMCEInfo.get(DomainConstants.SELECT_ID);
			StringList slLanguages = new StringList();
			for (String key : mceIDLang.keySet()) {
	            JsonValue value = mceIDLang.get(key);
	            if (value instanceof JsonString) {
	                String languages = ((JsonString) value).getString();
	                StringList slMCLang = FrameworkUtil.split(languages, PGWidgetConstants.KEY_COMMA_SEPARATOR);
	                if (key.equals(strMCId)) {
	                    slLanguages = slMCLang;
	                }
	            }
	        }
			MapList mlBaseCopy = getBaseCopiesForMCEsMapList(context, strMCId);
			String strBaseLCId = new String();
			String strBaselang = new String();
			if (mlBaseCopy != null && !mlBaseCopy.isEmpty()) {
				Map mapBase = (Map) mlBaseCopy.get(0);
				strBaseLCId = (String) mapBase.get(DomainConstants.SELECT_ID);
				strBaselang = (String) mapBase.get("attribute[Copy Text Language]");
				String strAuthorName = (String) mapBase.get(RTAUtilConstants.KEY_AUTHOR);
				String strApproverName = (String) mapBase.get(RTAUtilConstants.KEY_APPROVER);
				if (UIUtil.isNotNullAndNotEmpty(strAuthorName)) {
					jsonObjMCE.add(RTAUtilConstants.KEY_AUTHOR, strAuthorName);
				}
				if (UIUtil.isNotNullAndNotEmpty(strApproverName)) {
					jsonObjMCE.add(RTAUtilConstants.KEY_APPROVER, strApproverName);
				}
				jsonObjMCE.add(RTAUtilConstants.KEY_LOCAL_BASE_COPY, strBaseLCId);
				jsonObjMCE.add("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]",
						(String) mapBase.get("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]"));
				if(mapBase.get(RTAUtilConstants.KEY_MASTER_COPY_AUTHORING) != null) {
					jsonObjMCE.add(RTAUtilConstants.KEY_AUTHOR_ROUTE_STATUS,
							(String)mapBase.get(RTAUtilConstants.KEY_MASTER_COPY_AUTHORING));
				}
				if (mapBase.get(RTAUtilConstants.SELECT_AUTHOR_ARTWORK_INFO) != null) {
					jsonObjMCE.add("artworkInfo",
							PGWidgetUtil.extractMultiValueSelect(mapBase, RTAUtilConstants.SELECT_AUTHOR_ARTWORK_INFO));
				}
				if(mapBase.get(RTAUtilConstants.KEY_MASTER_COPY_APPROVAL) != null) {
					jsonObjMCE.add(RTAUtilConstants.KEY_APPROVER_ROUTE_STATUS,
							(String)mapBase.get(RTAUtilConstants.KEY_MASTER_COPY_APPROVAL));
				}
				if(mapBase.get(RTAUtilConstants.SELECT_RTA_BG_JOB_STATUS) != null) {
					jsonObjMCE.add(RTAUtilConstants.SELECT_RTA_BG_JOB_STATUS,
							(String)mapBase.get(RTAUtilConstants.SELECT_RTA_BG_JOB_STATUS));
				}
			}
			List<Map<String, Object>> listLocalCopies = getRelatedLCObjectList(context, strMCId, slLanguages, objIdList);
			for(int j=0; j<mlBaseCopy.size(); j++) {
				Map map = (Map) mlBaseCopy.get(j);
			    Object mlBaseCopyIdObj = map.get(DomainObject.SELECT_ID);
			    if (mlBaseCopyIdObj != null && mlBaseCopyIdObj instanceof String) {
			        String mlBaseCopyId = (String) mlBaseCopyIdObj;
			        if (listLocalCopies.stream().noneMatch(copy -> mlBaseCopyId.equals(copy.get(DomainObject.SELECT_ID)))) {
			            listLocalCopies.addAll(mlBaseCopy);
			        }
			        break;
			    }
			}
			String arrLCIds[] = getArrayFromList(context, listLocalCopies);
			
			StringList slLCEIds = StringList.asList(arrLCIds);
			StringList slSelectsLocalCopies = new StringList();
			if ("MasterCopyAssignee".equals(strMode)) {
				slSelectsLocalCopies = StringList.create(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context));

				MapList mlLCELanguageInfo = DomainObject.getInfo(context, arrLCIds, slSelectsLocalCopies);
				StringList slMCLang = BusinessUtil.toStringList(mlLCELanguageInfo,
						AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context));
				String strMCLang = FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				slMCLang = FrameworkUtil.split(strMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				HashSet<String> uniqueLangSet = new HashSet<>();
		        for (String lang : slMCLang) {
		        	uniqueLangSet.add(lang);
		        }
		        slMCLang.clear();
		        slMCLang.addAll(uniqueLangSet);
				String strFlag = RTAUtil.getCopyElementType(context, mapMCEInfo);

				jsonObjMCE.add(DomainConstants.SELECT_ID, strMCId);
				jsonObjMCE.add(DomainConstants.SELECT_NAME, (String) mapMCEInfo.get(DomainConstants.SELECT_NAME));
				jsonObjMCE.add(DomainConstants.SELECT_TYPE, (String) mapMCEInfo.get(DomainConstants.SELECT_TYPE));
				jsonObjMCE.add(DomainConstants.SELECT_REVISION,
						(String) mapMCEInfo.get(DomainConstants.SELECT_REVISION));
				jsonObjMCE.add(DomainConstants.SELECT_CURRENT, (String) mapMCEInfo.get(DomainConstants.SELECT_CURRENT));
				jsonObjMCE.add(KEY_TITLE, (String) mapMCEInfo.get(AWLAttribute.MARKETING_NAME.getSel(context)));
				jsonObjMCE.add(KEY_FLAG, strFlag);
				jsonObjMCE.add(KEY_LANGUAGES, FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
				jsonObjMCE.add(RTAUtilConstants.ATTR_PG_CONFIRM_ASSIGNMENT,
						(String) mapMCEInfo.get(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT));
				if (strFlag.equals(STR_GRAPHICTYPE)) {
					String strImage = null;
					try {
						String[] arrayOfString = new String[2];
						arrayOfString[0] = strMCId;
						arrayOfString[1] = MCSURL;
						strImage = JPO.invoke(context, "AWLGraphicsElementUI", null, "getGraphicImageURLforPOAAction",
								arrayOfString, String.class);
					} catch (Exception e) {
						throw e;
					}
					jsonObjMCE.add(KEY_CONTENT, strImage);
				} else {
					jsonObjMCE.add(KEY_CONTENT, (String) mapMCEInfo.get(
							"from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].to.attribute[Copy Text_RTE]"));
				}
				jsonArrOut.add(jsonObjMCE.build());
			} else {
				slSelectsLocalCopies = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
						DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION, DomainConstants.SELECT_CURRENT,
						AWLAttribute.TRANSLATE.getSel(context), AWLAttribute.MARKETING_NAME.getSel(context),
						AWLAttribute.INLINE_TRANSLATION.getSel(context),
						AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context),
						RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT, RTAUtilConstants.SELECT_RTA_BG_JOB_STATUS);
				slSelectsLocalCopies.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type");
				slSelectsLocalCopies.add("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]");
				slSelectsLocalCopies.add("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");
				slSelectsLocalCopies.add("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]");
				slSelectsLocalCopies.add(RTAUtilConstants.SELECT_COPYTEXT);
				slSelectsLocalCopies.add(RTAUtilConstants.SELECT_ISBASECOPY);
				slSelectsLocalCopies.add("from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
				
				MapList mlLCELanguageInfo = DomainObject.getInfo(context, arrLCIds, slSelectsLocalCopies);

				if ("LocalCopyAssignee".equals(strMode)) {
					buildLCEJsonForAA(context, mlLCELanguageInfo, jsonArrOut, objIdList);
				}
//					else {
//						buildCopyTaskStatusJsonForAA(context, mlLCELanguageInfo, slLCEIds, strMCId, jsonArrOut);
//					}
			}
		}
		return jsonArrOut;
	}

	private static String[] getArrayFromList(Context context, List<Map<String, Object>> listLocalCopies)
			throws FrameworkException {
		String arrReturn[] = new String[listLocalCopies.size()];
		for (int j = 0; j < listLocalCopies.size(); j++) {
			arrReturn[j] = (String) listLocalCopies.get(j).get(DomainConstants.SELECT_ID);
		}
		return arrReturn;
	}

	public static void buildLCEJsonForAA(Context context, MapList mlLCEInfoList, JsonArrayBuilder jsonArrOut, StringList objIdList)
			throws Exception {
		DomainObject popDom = DomainObject.newInstance(context, objIdList.get(0));
		boolean isTypeCopyList = popDom.isKindOf(context,AWLType.COPY_LIST.get(context));
		StringList slCLCountryList = new StringList();
		if(isTypeCopyList) {
			Pattern relPattern = new Pattern(EditCLConstants.RELATIONSHIP_COPYLISTCOUNTRY);
			Pattern typePattern = new Pattern(RTAUtilConstants.TYPE_COUNTRY);
			DomainObject domCLObj = DomainObject.newInstance(context, objIdList.get(0));
			StringList objSelects = new StringList(DomainObject.SELECT_NAME);
			MapList mlCopyListCountries = domCLObj.getRelatedObjects(context, // context,
					relPattern.getPattern(), // relationshipPattern,
					typePattern.getPattern(), // typepattern
					objSelects, // objectSelects
					null, // relationshipSelects,
					true, // getFrom
					true, // getTo,
					(short) 1, // recurseToLevel,
					null, // objectWhere,
					null, // relWhere
					0); // limit
			for (int i = 0; i < mlCopyListCountries.size(); i++) {
	            Map map = (Map) mlCopyListCountries.get(i);
	            String countryName = (String) map.get(DomainConstants.SELECT_NAME);
	            slCLCountryList.add(countryName);
	        }
            			
		}
		for (int i = 0; i < mlLCEInfoList.size(); i++) {
			Map mapLCE = (Map) mlLCEInfoList.get(i);
			String strLCEId = (String) mapLCE.get(DomainConstants.SELECT_ID);
			String strFlag = RTAUtil.getCopyElementType(context, mapLCE);

			mapLCE.put(KEY_TITLE, (String) mapLCE.get(AWLAttribute.MARKETING_NAME.getSel(context)));
			mapLCE.put(KEY_FLAG, strFlag);
			mapLCE.put(KEY_LANGUAGES, (String)mapLCE.get(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context)));
			mapLCE.put(RTAUtilConstants.ATTR_PG_CONFIRM_ASSIGNMENT,
					(String) mapLCE.get(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT));
			mapLCE.put(KEY_CONTENT, mapLCE.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
			mapLCE.put(EditCLConstants.STR_LANGUAGE_ID, mapLCE.get("from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id"));
			String strLangId = (String) mapLCE.get("from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
			if(isTypeCopyList) {
				StringList countryList = getCountryFromLang(context, strLangId, mapLCE, slCLCountryList);
				mapLCE.put(RTAUtilConstants.KEY_COUNTRIES, countryList);
			}
			Map mapAA = getAuthorApproverForCopyElement(context, strLCEId);
			
			if ((String) mapAA.get(RTAUtilConstants.KEY_AUTHOR) != null) {
				mapLCE.put(RTAUtilConstants.KEY_AUTHOR, (String) mapAA.get(RTAUtilConstants.KEY_AUTHOR));
			}
			if ((String) mapAA.get(RTAUtilConstants.KEY_APPROVER) != null) {
				mapLCE.put(RTAUtilConstants.KEY_APPROVER, (String) mapAA.get(RTAUtilConstants.KEY_APPROVER));
			}
			if("Yes".equalsIgnoreCase((String) mapLCE.get(RTAUtilConstants.SELECT_ISBASECOPY))) {
				if(mapAA.get(RTAUtilConstants.KEY_MASTER_COPY_AUTHORING) != null) {
					mapLCE.put(RTAUtilConstants.KEY_AUTHOR_ROUTE_STATUS,
							(String)mapAA.get(RTAUtilConstants.KEY_MASTER_COPY_AUTHORING));
				}
				if(mapAA.get(RTAUtilConstants.KEY_MASTER_COPY_APPROVAL) != null) {
					mapLCE.put(RTAUtilConstants.KEY_APPROVER_ROUTE_STATUS,
							(String)mapAA.get(RTAUtilConstants.KEY_MASTER_COPY_APPROVAL));
				}
			}else {
				if(mapAA.get(RTAUtilConstants.KEY_LOCAL_COPY_AUTHORING) != null) {
					mapLCE.put(RTAUtilConstants.KEY_AUTHOR_ROUTE_STATUS,
							(String)mapAA.get(RTAUtilConstants.KEY_LOCAL_COPY_AUTHORING));
				}
				if(mapAA.get(RTAUtilConstants.KEY_LOCAL_COPY_APPROVAL) != null) {
					mapLCE.put(RTAUtilConstants.KEY_APPROVER_ROUTE_STATUS,
							(String)mapAA.get(RTAUtilConstants.KEY_LOCAL_COPY_APPROVAL));
				}
			}
			jsonArrOut.add(PGWidgetUtil.getJSONFromMap(context, mapLCE));
		}
	}
	
	public static StringList getCountryFromLang(Context context, String strLangId, Map mapLCE, StringList slCLCountryList) throws Exception {
		Pattern relPattern = new Pattern(RTAUtilConstants.RELATIONSHIP_LANGUAGEUSED);
		Pattern typePattern = new Pattern(RTAUtilConstants.TYPE_COUNTRY);
		boolean isFrom = true, isTo = true;
		StringList countryList = new StringList();
		StringList slObjSelects = new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		DomainObject langObject = DomainObject.newInstance(context, strLangId);
		MapList mlcountryList = new MapList();
		StringList slCLLangCountryList = new StringList();
		mlcountryList = langObject.getRelatedObjects(context, // context,
				relPattern.getPattern(), // relationshipPattern,
				typePattern.getPattern(), // typepattern
				slObjSelects, // objectSelects
				null, // relationshipSelects,
				isFrom, // getFrom
				isTo, // getTo,
				(short) 1, // recurseToLevel,
				null, // objectWhere,
				null, // relWhere
				0); // limit
		for (int i = 0; i < mlcountryList.size(); i++) {
            Map map = (Map) mlcountryList.get(i);
            String countryName = (String) map.get(DomainConstants.SELECT_NAME);
            countryList.add(countryName);
        }
		for (String country : countryList) {
            if (slCLCountryList.contains(country)) {
            	slCLLangCountryList.add(country);
            }
        }
		return slCLLangCountryList;
		
	}
	
	public static List<Map<String, Object>> getRelatedLCObjectList(Context context, String strMCId, StringList slLanguages, StringList objIdList) throws Exception {
		Pattern relPattern = new Pattern(RTAUtilConstants.REL_ARTWORKELEMENTCONTENT);
		Pattern typePattern = new Pattern(RTAUtilConstants.TYP_COPYELEMENT);
		boolean isFrom = true, isTo = true;
		MapList mcLCData = new MapList();
		Map<?, ?> eachPOALCData = null;
		StringList slObjSelects = new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(RTAUtilConstants.SELECT_COPYTEXT);
		slObjSelects.add(RTAUtilConstants.SELECT_COPYTEXTLANG);
		DomainObject domMCId = DomainObject.newInstance(context, strMCId);
		String isInlineTranslate = domMCId.getInfo(context, AWLAttribute.INLINE_TRANSLATION.getSel(context));
		JsonArrayBuilder jsonArrHierPOA = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrHierLC = Json.createArrayBuilder();
		String strType = null;
		mcLCData = domMCId.getRelatedObjects(context, // context,
				relPattern.getPattern(), // relationshipPattern,
				typePattern.getPattern(), // typepattern
				slObjSelects, // objectSelects
				null, // relationshipSelects,
				isFrom, // getFrom
				isTo, // getTo,
				(short) 1, // recurseToLevel,
				null, // objectWhere,
				null, // relWhere
				0); // limit
		MapList filteredList = new MapList();
		if(isInlineTranslate.equalsIgnoreCase(RTAUtilConstants.KEY_CONST_YES)) {
			MapList inlineMCMapList = processInlineMC(context, strMCId, objIdList, mcLCData);
			filteredList.addAll(inlineMCMapList);
		}else {
	        for (int i = 0; i < mcLCData.size(); i++) {
	            Map<String, Object> map = (Map<String, Object>) mcLCData.get(i);
	            String formattedList = new String();
	            StringBuilder resultBuilder = new StringBuilder();
	            String copyTextLanguage = (String) map.get("attribute[Copy Text Language]");
	            for (String str : slLanguages) {
	                if(str.equalsIgnoreCase(copyTextLanguage)){
	            	   if(!filteredList.contains(map)) {
	                	filteredList.add(map);
	            	   }
	                }
	            }
	        }
		}
		return filteredList;
	}
	
	public static MapList processInlineMC(Context context, String strMCId, StringList objIdList, MapList mcLCData) throws Exception {
		MapList finalMapList = new MapList();
		StringList mceRelSelects = StringList.create(AWLAttribute.NOTES.getSel(context));
		mceRelSelects.add("attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");
		StringList selectList = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
				DomainConstants.SELECT_TYPE, RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT,
				RTAUtilConstants.SELECT_VALIDITY_DATE, RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE);
		selectList.add("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "]");
		selectList.add(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context));
		selectList.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id");
		for (String objId : objIdList) {
			DomainObject dmoObject = DomainObject.newInstance(context, objId);
			MapList mlObjLCList = dmoObject.getRelatedObjects(context, AWLRel.ARTWORK_ASSEMBLY.get(context),
					AWLType.ARTWORK_ELEMENT.get(context), selectList, // Object Select
					mceRelSelects, // rel Select
					false, // get To
					true, // get From
					(short) 0, // recurse level
					null, // where Clause
					null, 0);
			for (int i = 0; i < mlObjLCList.size(); i++) {
				Map objLCMap = (Map) mlObjLCList.get(i);
				String objLCId = (String) objLCMap.get(DomainObject.SELECT_ID);
				for (int j = 0; j < mcLCData.size(); j++) {
					Map mcLCMap = (Map) mcLCData.get(j);
					String mcLCId = (String) mcLCMap.get(DomainObject.SELECT_ID);
					if (objLCId.equals(mcLCId)) {
						finalMapList.add(mcLCMap);
					}
				}
			}
		}
		return finalMapList;
	}
	
	public static MapList getBaseCopiesForMCEsMapList(Context context, String strMCEIds)
			throws FrameworkException, Exception {
		MapList mlFinalList = new MapList();
		StringList slMCIds = FrameworkUtil.split(strMCEIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		StringList slObjSel = new StringList();
		slObjSel.add(DomainConstants.SELECT_ID);
		slObjSel.add(DomainConstants.SELECT_NAME);
		slObjSel.add(DomainConstants.SELECT_TYPE);
		slObjSel.add(DomainConstants.SELECT_OWNER);
		slObjSel.add(DomainConstants.SELECT_REVISION);
		slObjSel.add(DomainConstants.SELECT_CURRENT);
		slObjSel.add(RTAUtilConstants.SELECT_ISBASECOPY);
		slObjSel.add(RTAUtilConstants.SELECT_PGAWLREFNO);
		slObjSel.add(RTAUtilConstants.SELECT_TITLE);
		slObjSel.add(RTAUtilConstants.SELECT_COPYTEXT);
		slObjSel.add(RTAUtilConstants.SELECT_MARKETINGNAME);
		slObjSel.add(RTAUtilConstants.SELECT_COPYTEXTLANG);
		slObjSel.add(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT);
		slObjSel.add("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]");
		slObjSel.add(RTAUtilConstants.SELECT_AUTHOR_ROUTE_STATUS);
		slObjSel.add(RTAUtilConstants.SELECT_AUTHOR_ARTWORK_INFO);
		slObjSel.add(RTAUtilConstants.SELECT_ISBASECOPY);
		slObjSel.add(RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE);
		slObjSel.add(RTAUtilConstants.SELECT_VALIDITY_DATE);
		slObjSel.add(RTAUtilConstants.SELECT_APPROVER_ID);	
		slObjSel.add(RTAUtilConstants.SELECT_AUTHOR_ID);
		slObjSel.add(RTAUtilConstants.SELECT_RTA_BG_JOB_STATUS);
		
//			String strWhere = RTAUtilConstants.SELECT_ISBASECOPY + " == Yes";

		for (int i = 0; i < slMCIds.size(); i++) {
			MapList mlLcList = new ArtworkMaster(slMCIds.get(i)).getArtworkElements(context, slObjSel,
					new StringList(PGWidgetConstants.SELECT_CONNECTION_ID), DomainConstants.EMPTY_STRING);
			
			for (int j = 0; j < mlLcList.size(); j++) {
				Map mapLC = (Map) mlLcList.get(j);
//					mapLCEInfo.put(mapLC.get(DomainConstants.SELECT_ID), mapLC);
				if("Yes".equalsIgnoreCase((String) mapLC.get(RTAUtilConstants.SELECT_ISBASECOPY))) {				
					String strBaseId = (String)(mapLC.get(DomainConstants.SELECT_ID));
					mapLC.putAll(RTAUtil.getAuthorApproverForCopyElement(context, strBaseId));
					mapLC.put(KEY_PARENT_ID, slMCIds.get(i));
					mlFinalList.add(mapLC);
					break;
				}
			}
		}
		return mlFinalList;
	}

	public static String deleteLocalCopyTasks(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String strCEIds = jsonInputData.getString(RTAUtilConstants.KEY_LCE_IDS);
		StringList slCEId = FrameworkUtil.split(strCEIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		String strReturnMsg = "";
		HashMap hArgs = new HashMap();
		hArgs.put("ceIds",slCEId);
		String[] strArgs = JPO.packArgs(hArgs);	
		try {
			strReturnMsg=(String)JPO.invoke(context, "pgRTACopyElementUtil", null, "deleteAuthoringTaskInBackground", strArgs, String.class);
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			output.add(PGWidgetConstants.KEY_MESSAGE, strReturnMsg.toString());
		} catch (Exception e) {
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			throw e;
		} 
		return output.build().toString();
	}

	public static String reassignTaskToSelectedPerson(Context context, String paramString) {
		String strOut = new String();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String strAthorAssigneeId = new String();
		if (jsonInputData.containsKey("NewAuthorAssigneeOID")) {
			strAthorAssigneeId = jsonInputData.getString("NewAuthorAssigneeOID");
		} else {
			strAthorAssigneeId = "";
		}
		String strApproverAssigneeId = new String();
		if (jsonInputData.containsKey("NewApproverAssigneeOID")) {
			strApproverAssigneeId = jsonInputData.getString("NewApproverAssigneeOID");
		} else {
			strApproverAssigneeId = "";
		}
		String strAssignAuthorAsApproverHiddenValue	= jsonInputData.getString("AssignAuthorAsApproverHiddenValue");
		boolean isAuthorAsApprover = RANGE_TRUE.equalsIgnoreCase(strAssignAuthorAsApproverHiddenValue);
		String strNewAuthorAssigneeId	= BusinessUtil.isNotNullOrEmpty(strAthorAssigneeId) ? strAthorAssigneeId :"";
        String strNewApproverAssigneeId = BusinessUtil.isNotNullOrEmpty(strApproverAssigneeId) ? strApproverAssigneeId :(isAuthorAsApprover ? strNewAuthorAssigneeId : "");
		String strCEId = null;
		try{
			JsonObject testJson = (JsonObject) jsonInputData.get("CEIdsForReAssignAuthorApprover");
			Map mOIdRelId = parseJsonBasedOnValueType(testJson);
			if(mOIdRelId != null) {	
				Object[] objectArray = new Object[] {context , strNewAuthorAssigneeId, strNewApproverAssigneeId, mOIdRelId};
				Class[] objectTypeArray = new Class[] {matrix.db.Context.class, String.class, String.class, Map.class};
				com.matrixone.apps.domain.util.BackgroundProcess backgroundProcess = new com.matrixone.apps.domain.util.BackgroundProcess();
				backgroundProcess.submitJob(context,(Object)new RTAUtil(context, null),"reassignTaskToSelectedPersonBackground",objectArray, objectTypeArray);
				String strMessage = EnoviaResourceBundle.getProperty(context,"emxAWLStringResource",context.getLocale(),"emxAWL.ReAssignAuthorApprover.BackgroundJob");
				MqlUtil.mqlCommand(context, "notice $1", strMessage);	
				strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
						.add(PGWidgetConstants.KEY_MESSAGE, strMessage).build().toString();
			}
		} catch(Exception ex) {			
			logger.log(Level.INFO, "Exception in reassignTaskToSelectedPerson method - {0}", ex);
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_MESSAGE, ex.getMessage()).build().toString();

		}
		return strOut;
	}
	
	/**
	 * Method to reassign the Author and Approvers to CE
	 * @param context
	 * @param strNewAuthorAssigneeId - New Author Assignee
	 * @param strNewApproverAssigneeId - New Approver Assignee
	 * @param mOIdRelId - Map contains CE and rel id of route node or route task
	 * @throws Exception
	 */
	public void reassignTaskToSelectedPersonBackground(Context context, String strNewAuthorAssigneeId, String strNewApproverAssigneeId, Map mOIdRelId) throws Exception 
	{
		String strCEId = null;
		StringList strITRelsList = new StringList();
		String strRouteState = null;
		String strRouteId = null;
		Map mCEAuthorRouteInfo = null;
		Map mCEApproveRouteInfo = null;
		MapList mlRoutes = null;
		MapList mlRouteNodeInfo = null;
		String strSelRouteSeq = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
		String strSelAttriDuteDateOffset = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
		String strAttrpgRTABGJobStatus = PropertyUtil.getSchemaProperty(context, "attribute_pgRTABGJobStatus");
		boolean bIsContextPushed = false;
		try{
			SimpleDateFormat sdfFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), context.getLocale());
			String strCurrentFormatDate = sdfFormat.format(java.util.Calendar.getInstance().getTime());	
			String strBGSubmitted = strBGSubmittedPref + strCurrentFormatDate;			
			String strPGRTABGJobStatus = null;
			StringList slRouteNodeSelects = new StringList(3);
			slRouteNodeSelects.add(DomainConstants.SELECT_NAME);
			slRouteNodeSelects.add(DomainConstants.SELECT_TYPE);
			slRouteNodeSelects.add(DomainConstants.SELECT_ID);
			
			StringList slRouteNodeRelSelects = new StringList(5);
			slRouteNodeRelSelects.add(DomainRelationship.SELECT_ID);
			slRouteNodeRelSelects.add(DomainRelationship.SELECT_NAME);
			slRouteNodeRelSelects.add(PHYSICALID_CONNECTION);
			slRouteNodeRelSelects.add(SEL_ATTR_SCHEDULED_COMPLETION_DATE);
			slRouteNodeRelSelects.add(strSelAttriDuteDateOffset);	
			slRouteNodeRelSelects.add(strSelRouteSeq);
			
			if(mOIdRelId == null) {
				return;
			}
			ArtworkContent element = null;
			
			//Pushing the context to fetch claim details	
			if (!ArtworkConstants.PERSON_USERAGENT.equalsIgnoreCase(context.getUser())) {
				ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT, null, context.getVault().getName());
				bIsContextPushed = true;
			}			
			for (Iterator ceKeyIter = mOIdRelId.keySet().iterator(); ceKeyIter.hasNext();) {
				strCEId = (String)ceKeyIter.next();					
				if(BusinessUtil.isNotNullOrEmpty(strCEId)) {
					strPGRTABGJobStatus = BusinessUtil.getAttribute(context, strCEId, strAttrpgRTABGJobStatus);
					if(BusinessUtil.isNotNullOrEmpty(strPGRTABGJobStatus) && strPGRTABGJobStatus.indexOf(strBGSubmittedPref) != -1) {
						ceKeyIter.remove();
					} else {
						BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, strBGSubmitted);
					}
				}					
			}
			strNewAuthorAssigneeId = checkForAbsenceDelegate(context, strNewAuthorAssigneeId);
			strNewApproverAssigneeId = checkForAbsenceDelegate(context, strNewApproverAssigneeId);
			logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method strNewAuthorAssigneeId ::: {0}", strNewAuthorAssigneeId);
			logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method strNewApproverAssigneeId ::: {0}", strNewApproverAssigneeId);
			logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method mOIdRelId ::: {0}", mOIdRelId);
			
			for (Iterator ceKeyIter = mOIdRelId.keySet().iterator(); ceKeyIter.hasNext();) {
				strCEId = (String)ceKeyIter.next();
				if(BusinessUtil.isNotNullOrEmpty(strCEId)) {
					strITRelsList = (StringList)mOIdRelId.get(strCEId);
					logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method isAuthor ::: {0}", strITRelsList);
					element = ArtworkContent.getNewInstance(context, strCEId);
                    ArtworkContent artworkCE = ArtworkContent.getNewInstance(context, strCEId);
					Map mBaseInfo = artworkCE.getBaseContent(context).getInfo(context, StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT));
					String strBaseCEId = (String)mBaseInfo.get(DomainConstants.SELECT_ID);
					
					Map mCERouteAndRouteNodeInfo = getCEConnectedRoutes(context, strCEId);
					if(BusinessUtil.isNotNullOrEmpty(strNewAuthorAssigneeId)) {
						//Update Authoring Details
						mlRoutes = (MapList) mCERouteAndRouteNodeInfo.get("AuthorRoutes");
						if(BusinessUtil.isNotNullOrEmpty(mlRoutes)) {
							mlRoutes.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
							mCEAuthorRouteInfo = (Map)mlRoutes.get(0);
							logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method mCEAuthorRouteInfo ::: {0}", mCEAuthorRouteInfo);
							strRouteState = (String)mCEAuthorRouteInfo.get(DomainConstants.SELECT_CURRENT);
							if(BusinessUtil.isNotNullOrEmpty(strRouteState) && (DomainConstants.STATE_ROUTE_DEFINE.equals(strRouteState) || DomainConstants.STATE_ROUTE_IN_PROCESS.equals(strRouteState))) {
								strRouteId = (String)mCEAuthorRouteInfo.get(DomainConstants.SELECT_ID);
								mlRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, slRouteNodeSelects, slRouteNodeRelSelects);
								updateRouteNodeOrTaskWithNewAssignee(context, element, strRouteId, mlRouteNodeInfo, strITRelsList, strNewAuthorAssigneeId, true);
							}
						}
					}
					if(BusinessUtil.isNotNullOrEmpty(strNewApproverAssigneeId)) {
						//Update Approval Details
						mlRoutes = (MapList)mCERouteAndRouteNodeInfo.get("ApproverRoutes");
						if(BusinessUtil.isNotNullOrEmpty(mlRoutes)) {
							mlRoutes.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
							mCEApproveRouteInfo = (Map)mlRoutes.get(0);
							strRouteState = (String) mCEApproveRouteInfo.get(DomainConstants.SELECT_CURRENT);				
							if(BusinessUtil.isNotNullOrEmpty(strRouteState) && (DomainConstants.STATE_ROUTE_DEFINE.equals(strRouteState) || DomainConstants.STATE_ROUTE_IN_PROCESS.equals(strRouteState))) {
								strRouteId = (String) mCEApproveRouteInfo.get(DomainConstants.SELECT_ID);
								mlRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, slRouteNodeSelects, slRouteNodeRelSelects);
								if(DomainConstants.STATE_ROUTE_IN_PROCESS.equals(strRouteState)) {
									mCEApproveRouteInfo = getArtworkRouteTasksInfo(context, strRouteId, "Approval");
								}
								updateRouteNodeOrTaskWithNewAssignee(context, element, strRouteId, mlRouteNodeInfo, strITRelsList, strNewApproverAssigneeId, false);								
							}				
						}	
					}
					BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, DomainConstants.EMPTY_STRING);
				}
			}
		} catch(Exception ex) {
			if(BusinessUtil.isNotNullOrEmpty(strCEId)) {
				BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, "Error :"+ex.getLocalizedMessage());
			}
			logger.log(Level.INFO, "Exception in reassignTaskToSelectedPersonBackground method ::: {0}", ex);
		} finally {
			if(bIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}
	
	/**
	 * Method to get the Absence delegate if any the assignee passed in the argument.
	 * @param context
	 * @param strAssigneeId - Assignee Id
	 * @return Return delegate id if set
	 * @throws Exception
	 */
	private String checkForAbsenceDelegate(Context context, String strAssigneeId) throws Exception{		
		try {
			if(BusinessUtil.isNullOrEmpty(strAssigneeId)) {
				return DomainConstants.EMPTY_STRING;
			}
			Map personInfoMap = null;		
			String strAbsenceEndDate = null;
			String strAbsenceStartDate = null;
			String strAbsenceDelegate = null;
			String strDelegatedPersonObjectId = null;
			Calendar cal = Calendar.getInstance();
			int iHour = cal.get( Calendar.HOUR );
			int iAMPM = cal.get( Calendar.AM_PM );
			//If the server time is greater than 5PM and before 11:59 PM then set the actual start date to next day.
			if( iAMPM > 0 && iHour > 5){
				cal.add(Calendar.DATE, 1);					
			}
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.HOUR_OF_DAY, 17);
			cal.set(Calendar.AM_PM, 0);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MILLISECOND, 0);	
			SimpleDateFormat sdf = new SimpleDateFormat (eMatrixDateFormat.getInputDateFormat(), context.getLocale());
			String strAttributeAbsenceDelegate = PropertyUtil.getSchemaProperty(context,"attribute_AbsenceDelegate");
			String strAttributeAbsenceStartDate = PropertyUtil.getSchemaProperty(context,"attribute_AbsenceStartDate");
			String strAttributeAbsenceEndDate = PropertyUtil.getSchemaProperty(context,"attribute_AbsenceEndDate");			
			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_NAME);
			slSelects.add("attribute["+strAttributeAbsenceDelegate+"]");
			slSelects.add("attribute["+strAttributeAbsenceEndDate+"]");
			slSelects.add("attribute["+strAttributeAbsenceStartDate+"]");
			personInfoMap = BusinessUtil.getInfo(context, strAssigneeId, slSelects);
			strAbsenceDelegate = (String)personInfoMap.get("attribute["+strAttributeAbsenceDelegate+"]");
			strAbsenceStartDate = (String)personInfoMap.get("attribute["+strAttributeAbsenceStartDate+"]");
			strAbsenceEndDate = (String)personInfoMap.get("attribute["+strAttributeAbsenceEndDate+"]");
			if(BusinessUtil.isNotNullOrEmpty(strAbsenceDelegate) && BusinessUtil.isNotNullOrEmpty(strAbsenceStartDate) && BusinessUtil.isNotNullOrEmpty(strAbsenceEndDate))
			{
				Date curDate = cal.getTime();
				strDelegatedPersonObjectId = PersonUtil.getPersonObjectID(context, strAbsenceDelegate);
				Date absenceStartDate = sdf.parse(strAbsenceStartDate);
				cal.setTime(absenceStartDate);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.HOUR_OF_DAY, 17);
				cal.set(Calendar.AM_PM, 0);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MILLISECOND, 0);
				absenceStartDate = cal.getTime();
				Date absenceEndDate = sdf.parse(strAbsenceEndDate);
				cal.setTime(absenceEndDate);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.HOUR_OF_DAY, 17);
				cal.set(Calendar.AM_PM, 0);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MILLISECOND, 0);
				absenceEndDate = cal.getTime();
				if(BusinessUtil.isNotNullOrEmpty(strDelegatedPersonObjectId) && (absenceStartDate.equals(curDate) || absenceEndDate.equals(curDate) || (absenceStartDate.before(curDate) && absenceEndDate.after(curDate))))
				{
					strAssigneeId = strDelegatedPersonObjectId;
				}
			}		
		}catch(Exception ex) {
			//Important Do not throw Exception
			logger.log(Level.INFO, "Exception in resetCEOwnerAndOriginator method ::: {0}", ex);
		}
		return strAssigneeId;
	}
	
	/**
	 * Update the new assignee to route node and inbox task.
	 * @param context
	 * @param element Artwork Element
	 * @param strRouteId - Route Id
	 * @param mlRouteNodeInfo - Route Node Info
	 * @param strITRelsList - Selected Route Node or IT rel
	 * @param strNewAssigneeId - New Assignee Id
	 * @throws Exception
	 */
	private void updateRouteNodeOrTaskWithNewAssignee(Context context, ArtworkContent element, String strRouteId, MapList mlRouteNodeInfo, StringList strITRelsList, String strNewAssigneeId, boolean isAuthor) throws Exception{
		Map mRouteNodeInfo = null;
		String strRouteNodeRelId = null;
		String strRouteNodePhyId = null;
		String strProjectTaskRelId = null;
		String strInboxTaskId = null;
		String attrRouteNodeId = AWLAttribute.ROUTE_NODE_ID.getSel(context);
		String strSelRouteSeq = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
		String strSeq = null;
		String strTaskState = null;
		String strOldAssignee = null;
		String strNewAssignee = null;
		
		StringList objSelectsList = new StringList();
		objSelectsList.add(DomainConstants.SELECT_ID);
		objSelectsList.add(DomainConstants.SELECT_NAME);
		objSelectsList.add(DomainConstants.SELECT_CURRENT);
		objSelectsList.add(SEL_ATTR_SCHEDULED_COMPLETION_DATE);
		objSelectsList.add(SEL_ATTR_ACTUAL_COMPLETION_DATE);
		objSelectsList.add(attrRouteNodeId);
		
		StringList relSelectsList = new StringList();
		relSelectsList.add(DomainRelationship.SELECT_ID);
		relSelectsList.add("to.attribute["+RTAUtilConstants.ATTRIBUTE_ARTWORKINFO+"]");
		
		Route route = new Route(strRouteId);
		
		MapList mlRouteTaskList = route.getRouteTasks(context, objSelectsList, relSelectsList, null, false);
		Map<?,?> mTaskInfo = null;
		InboxTask taskBean = null;
		DomainObject domPerson = null;
		logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strNewAssigneeId ::: {0}", strNewAssigneeId);
		logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method mlRouteTaskList ::: {0}", mlRouteTaskList);
		//If the new Assignee already present return without updating		
		StringList strNodeList = BusinessUtil.toStringList(mlRouteNodeInfo, DomainConstants.SELECT_ID);
		if(BusinessUtil.isNotNullOrEmpty(strNodeList) && strNodeList.contains(strNewAssigneeId)) {
			return;
		}
		logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method mlRouteNodeInfo ::: {0}", mlRouteNodeInfo);
		
		if(mlRouteNodeInfo.size() == 1) {
			mRouteNodeInfo = (Map)mlRouteNodeInfo.get(0);
			strRouteNodeRelId = (String)mRouteNodeInfo.get(DomainRelationship.SELECT_ID);
			strOldAssignee = (String)mRouteNodeInfo.get(DomainConstants.SELECT_ID);
			if(BusinessUtil.isNotNullOrEmpty(strOldAssignee) && !strOldAssignee.equals(strNewAssigneeId)) {
				if(BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId)) {
					DomainRelationship.setToObject(context, strRouteNodeRelId, DomainObject.newInstance(context, strNewAssigneeId));
				}
				logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strRouteNodeRelId ::: {0}", strRouteNodeRelId);
				if(BusinessUtil.isNotNullOrEmpty(mlRouteTaskList)) {
					strInboxTaskId = (String)((Map)mlRouteTaskList.get(0)).get(DomainConstants.SELECT_ID);
					strTaskState = (String)((Map)mlRouteTaskList.get(0)).get(DomainConstants.SELECT_CURRENT);
					logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strInboxTaskId ::: {0}", strInboxTaskId);
					logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strTaskState ::: {0}", strTaskState);
					if(BusinessUtil.isNotNullOrEmpty(strInboxTaskId) && BusinessUtil.isNotNullOrEmpty(strTaskState) && !DomainConstants.STATE_INBOX_TASK_COMPLETE.equals(strTaskState)) {
						taskBean = (InboxTask)DomainObject.newInstance(context, strInboxTaskId);
						domPerson = DomainObject.newInstance(context, strNewAssigneeId);
						taskBean.reAssignTask(context, domPerson, "Person", taskBean.getOwner(context).getName(), "Person", domPerson.getInfo(context, DomainConstants.SELECT_NAME));						
					}
				}
				logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method isAuthor ::: {0}", isAuthor);
				if(isAuthor) {										
					element.updateAssignee(context, strNewAssigneeId, element.getApprovalAssigneeId(context));
				}else {
					element.updateAssignee(context, element.getAuthoringAssigneeId(context), strNewAssigneeId);
				}				
			}
		}else {
			mlRouteNodeInfo.sort(strSelRouteSeq, "ascending", "integer");
						
			Iterator<?> itr = mlRouteNodeInfo.iterator();
			StringList strCopiedRelIds = new StringList();
			strCopiedRelIds.addAll(strITRelsList);
			Map<String, String> mNodePersonInfo = new HashMap<>();
			while(itr.hasNext()) {
				mRouteNodeInfo = (Map)itr.next();
				strRouteNodeRelId = (String)mRouteNodeInfo.get(DomainRelationship.SELECT_ID);
				strOldAssignee = (String)mRouteNodeInfo.get(DomainConstants.SELECT_ID);
				mNodePersonInfo.put((String)mRouteNodeInfo.get(PHYSICALID_CONNECTION), strOldAssignee);
				if(BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId) && BusinessUtil.isNotNullOrEmpty(strOldAssignee) && !strOldAssignee.equals(strNewAssigneeId) && strCopiedRelIds.contains(strRouteNodeRelId)) {
					DomainRelationship.setToObject(context, strRouteNodeRelId, DomainObject.newInstance(context, strNewAssigneeId));
					strCopiedRelIds.remove(strRouteNodeRelId);					
				}
			}
			
			itr = mlRouteTaskList.iterator();
			while(itr.hasNext()) {
				mTaskInfo = (Map)itr.next();
				strTaskState = (String)mTaskInfo.get(DomainConstants.SELECT_CURRENT);
				strProjectTaskRelId = (String)mTaskInfo.get(DomainRelationship.SELECT_ID);
				strRouteNodePhyId = (String)mTaskInfo.get(attrRouteNodeId);
				strOldAssignee = mNodePersonInfo.get(strRouteNodePhyId);
				strInboxTaskId = (String)mTaskInfo.get(DomainConstants.SELECT_ID);
				if(BusinessUtil.isNotNullOrEmpty(strProjectTaskRelId) && BusinessUtil.isNotNullOrEmpty(strInboxTaskId) && BusinessUtil.isNotNullOrEmpty(strTaskState) && BusinessUtil.isNotNullOrEmpty(strOldAssignee) && !strOldAssignee.equals(strNewAssigneeId) && strCopiedRelIds.contains(strProjectTaskRelId) && !DomainConstants.STATE_INBOX_TASK_COMPLETE.equals(strTaskState)) {
					taskBean = (InboxTask)DomainObject.newInstance(context, strInboxTaskId);
					domPerson = DomainObject.newInstance(context, strNewAssigneeId);
					taskBean.reAssignTask(context, domPerson, "Person", taskBean.getOwner(context).getName(), "Person", domPerson.getInfo(context, DomainConstants.SELECT_NAME));
					DomainRelationship.setToObject(context, strRouteNodePhyId, DomainObject.newInstance(context, strNewAssigneeId));	
				}
			}
			
			//Finally update the CE with new Assignee
			StringBuilder sbNewSeq = new StringBuilder();
			StringList slRouteNodeSelects = new StringList(1);
			slRouteNodeSelects.add(DomainConstants.SELECT_ID);
			StringList slRouteNodeRelSelects = new StringList(1);
			slRouteNodeRelSelects.add(strSelRouteSeq);
			
			MapList mlNewRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, slRouteNodeSelects, slRouteNodeRelSelects);
			itr = mlNewRouteNodeInfo.iterator();
			while(itr.hasNext()) {
				mRouteNodeInfo = (Map)itr.next();
				strOldAssignee = (String)mRouteNodeInfo.get(DomainConstants.SELECT_ID);
				strSeq = (String)mRouteNodeInfo.get(strSelRouteSeq);
				if(BusinessUtil.isNotNullOrEmpty(sbNewSeq.toString())) {
					sbNewSeq.append("|"); 
				}
				sbNewSeq.append(strOldAssignee).append(",").append(strSeq);
			}
			if(BusinessUtil.isNotNullOrEmpty(sbNewSeq.toString())) {
				if(isAuthor) {
					strNewAssignee	= getRouteTemplateId(context, sbNewSeq.toString(), false);					
					element.updateAssignee(context, strNewAssignee, element.getApprovalAssigneeId(context));
				}else {
					strNewAssignee	= getRouteTemplateId(context, sbNewSeq.toString(), true);
					element.updateAssignee(context, element.getAuthoringAssigneeId(context), strNewAssignee);
				}
			}
		}
	}
	
	/**
	 * Method to get Value for Each type
	 * @param jsonObject
	 * @return
	 */
	public static Map parseJsonBasedOnValueType(JsonObject jsonObject) {
		Map<String, Object> mapOutput = new HashMap();
		String sKey;
		JsonValue jsonValue;
		Map mapLocal;
		for (Map.Entry<String,JsonValue> entry : jsonObject.entrySet())
		{
			sKey = entry.getKey();				
			jsonValue = entry.getValue();
			
			switch(jsonValue.getValueType()){
		       case ARRAY:
		    	   processJsonObject(mapOutput, sKey, (JsonArray)jsonValue);	
		           break;
		       case OBJECT:
		    	   mapLocal = parseJsonBasedOnValueType((JsonObject)jsonValue);
		    	   mapOutput.put(sKey, mapLocal);
		           break;
		       case NUMBER:
		    	   mapOutput.put(sKey, Integer.toString(((JsonNumber)jsonValue).intValue()));
		           break;
		       case STRING:
		    	   mapOutput.put(sKey, ((JsonString)jsonValue).getString());
		           break;
		       case TRUE:
		    	   mapOutput.put(sKey, true);
		           break;
		       case FALSE:
		    	   mapOutput.put(sKey, false);
		           break;
		       default:
		    	   mapOutput.put(sKey, jsonValue.toString());
		   }
		}
		
		return mapOutput;
	}
	
	/**
	 * Method to process Object if it is type of Array
	 * @param mapOutput
	 * @param sKey
	 * @param jsonValue
	 * @return
	 */
	private static Map processJsonObject(Map mapOutput, String sKey, JsonArray jsonValue) 
	{
		Object objectValue = jsonValue.get(0);
		if(objectValue instanceof JsonString)
		{
			StringList slValueList = new StringList();			
			int len = jsonValue.size();
			for (int i=0;i<len;i++){ 
				slValueList.add(((JsonString)jsonValue.get(i)).getString());
			} 
			mapOutput.put(sKey, slValueList);

		}
		else if(objectValue instanceof JsonNumber)
		{
			List<Integer> integerValueList = new ArrayList();			
			int len = jsonValue.size();
			for (int i=0;i<len;i++){ 
				integerValueList.add(((JsonNumber)jsonValue).intValue());
			} 
			mapOutput.put(sKey, integerValueList);
		}		
		else
		{
			Map mapLocal;
			MapList mlLocalList = new MapList();
			List<JsonObject> elements = jsonValue.getValuesAs(JsonObject.class);
	        for(JsonObject element: elements) {
	        	mapLocal = parseJsonBasedOnValueType(element);
	        	mlLocalList.add(mapLocal);
	        }	        
	        mapOutput.put(sKey, mlLocalList);
		}		
		return mapOutput;			
	}
		/**
	 * Update the task duration and due date.
	 * @param context
	 * @param paramString JSON string containing input parameters
	 * @return String JSON response
	 * @throws Exception 
	 */
		public static String updateRouteNodeOrTaskDueDate(Context context, String paramString) throws Exception {

			String strOutput = null;
			try {
				JsonReader jsonReader = Json.createReader(new StringReader(paramString));
				JsonArray jsonArray = jsonReader.readArray();
				for (byte b = 0; b < jsonArray.size(); b++) {
					JsonObject jsonInputData = jsonArray.getJsonObject(b);
					// JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
					String strCEId = jsonInputData.getString("objId");
					String strRelId = jsonInputData.getString("relId");
					String strNewValue = jsonInputData.getString("newValue");
					String strColumnName = jsonInputData.getString("columnName");
					// =======================================@@@@==========================================
					String strTaskId = null;
					String strScheduleCompletionDate = null;
					String strRouteNodeId = null;
					String strRouteNodeRelId = null;

					MapList mlRoutes = null;
					boolean isAuthoring = false;
					String strAttrpgRTABGJobStatus = PropertyUtil.getSchemaProperty(context,
							"attribute_pgRTABGJobStatus");
					String strPGRTABGJobStatus = BusinessUtil.getAttribute(context, strCEId,
							strAttrpgRTABGJobStatus);

					if (BusinessUtil.isNullOrEmpty(strColumnName)
							|| (BusinessUtil.isNotNullOrEmpty(strPGRTABGJobStatus)
									&& strPGRTABGJobStatus.indexOf(strBGSubmittedPref) != -1)) {
						strOutput = Json.createObjectBuilder()
								.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
								.add(PGWidgetConstants.KEY_MESSAGE, "column name is null/empty or PGRTA BG status null/empty/not contain BG submitted pref").build().toString();
						return strOutput;
					}
					ArtworkContent artworkCE = ArtworkContent.getNewInstance(context, strCEId);
					Map mBaseInfo = artworkCE.getBaseContent(context).getInfo(context, StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT));
					String strBaseCEId = (String)mBaseInfo.get(DomainConstants.SELECT_ID);
					// getting local copy authoring/approval routes
					Map mCERouteAndRouteNodeInfo = getCEConnectedRoutes(context, strCEId);
					if (strColumnName.equals(AUTHOR_TASK_DURATION) || strColumnName.equals(COLUMN_AUTHOR_TASK_SD)) {
						mlRoutes = (MapList) mCERouteAndRouteNodeInfo.get("AuthorRoutes");
						isAuthoring = true;
					} else {
						mlRoutes = (MapList)mCERouteAndRouteNodeInfo.get("ApproverRoutes");
					}
					mlRoutes.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
					Map mCERouteInfo = null;
					Map mRouteNodeInfo = null;
					Map mTaskInfo = null;

					String strRouteId = (BusinessUtil.isNotNullOrEmpty(mlRoutes))
							? (String) ((Map) mlRoutes.get(0)).get(DomainConstants.SELECT_ID)
							: null;

					if (BusinessUtil.isNullOrEmpty(strRouteId)) {
						throw new Exception("Route Id is null/empty");
					}
					// Fetch the Route Task Info
					String strSelAttriDuteDateOffset = DomainObject
							.getAttributeSelect(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
					String strDuration = null;

					Date dScheduleCompletionDate = null;

					Long lDuration = null;
					DomainObject domTask = null;
					Map mAttrInfo = new HashMap();
					DomainRelationship domRel = null;

					StringList slRouteNodeSelects = new StringList(3);
					slRouteNodeSelects.add(DomainConstants.SELECT_NAME);
					slRouteNodeSelects.add(DomainConstants.SELECT_TYPE);
					slRouteNodeSelects.add(DomainConstants.SELECT_ID);

					StringList slRouteNodeRelSelects = new StringList(4);
					slRouteNodeRelSelects.add(DomainRelationship.SELECT_ID);
					slRouteNodeRelSelects.add(DomainRelationship.SELECT_NAME);
					slRouteNodeRelSelects.add(PHYSICALID_CONNECTION);
					slRouteNodeRelSelects.add(SEL_ATTR_SCHEDULED_COMPLETION_DATE);
					slRouteNodeRelSelects.add(strSelAttriDuteDateOffset);

					Calendar cal = Calendar.getInstance();
					int iHour = cal.get(Calendar.HOUR);
					int iAMPM = cal.get(Calendar.AM_PM);
					// If the server time is greater than 5PM and before 11:59 PM then set the
					// actual start date to next day.
					if (iAMPM > 0 && iHour > 5) {
						cal.add(Calendar.DATE, 1);
					}
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.HOUR_OF_DAY, 17);
					Date sysDate = cal.getTime();

					if (isAuthoring) {
						mCERouteInfo = getArtworkRouteTasksInfo(context, strRouteId, "Authoring");
					} else {
						mCERouteInfo = getArtworkRouteTasksInfo(context, strRouteId, "Approval");
					}
					MapList mlRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, slRouteNodeSelects,
							slRouteNodeRelSelects);

					boolean isMultiAssignees = (mlRouteNodeInfo.size() > 1) ? Boolean.TRUE : Boolean.FALSE;

					mRouteNodeInfo = (Map) mlRouteNodeInfo.get(0);
					strRouteNodeId = (String) mRouteNodeInfo.get(PHYSICALID_CONNECTION);
					strRouteNodeRelId = (String) mRouteNodeInfo.get(DomainRelationship.SELECT_ID);

					if (strColumnName.equals(COLUMN_AUTHOR_TASK_SD)
							|| strColumnName.equals(COLUMN_APPROVER_TASK_SD)) {
						DateFormat formatter = DateFormat.getDateInstance(
								eMatrixDateFormat.getEMatrixDisplayDateFormat(), context.getLocale());
						SimpleDateFormat sDateformatter = new SimpleDateFormat(
								eMatrixDateFormat.strEMatrixDateFormat, Locale.US);

						dScheduleCompletionDate = formatter.parse(strNewValue);
						cal.setTime(dScheduleCompletionDate);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						cal.set(Calendar.HOUR_OF_DAY, 17);
						strScheduleCompletionDate = sDateformatter.format(cal.getTime());
						strDuration = Long.toString(
								(com.matrixone.apps.domain.util.DateUtil.computeDuration(sysDate, cal.getTime())));
						mAttrInfo.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, strDuration);
					}

					if (!isMultiAssignees) {
						if (BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId)) {
							if (mCERouteInfo.containsKey(strRouteNodeId)) {
								mTaskInfo = (Map) mCERouteInfo.get(strRouteNodeId);
								strTaskId = (String) mTaskInfo.get("TaskId");
							}
							domRel = DomainRelationship.newInstance(context, strRouteNodeRelId);

							if (strColumnName.equals(AUTHOR_TASK_DURATION)
									|| strColumnName.equals(APPROVER_TASK_DURATION)) {
								mAttrInfo.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, strNewValue);
								if (BusinessUtil.isNotNullOrEmpty(strTaskId)) {
									lDuration = Long.valueOf(strNewValue);
									// strScheduleCompletionDate =
									// emxCommonInitiateRoute_mxJPO.getDateAfterAddingXDuration(sysDate,lDuration);
									strScheduleCompletionDate = getDateAfterAddingXDuration(sysDate, lDuration);

								}
							} else {
								mAttrInfo.put(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE,
										strScheduleCompletionDate);
							}
							domRel.setAttributeValues(context, mAttrInfo);
						}

					} else {

						domRel = DomainRelationship.newInstance(context, strRelId);
						Map mRelInfo = (Map) DomainRelationship.getInfo(context, new String[] { strRelId },
								StringList.create(DomainRelationship.SELECT_TYPE,
										DomainConstants.ATTRIBUTE_ROUTE_NODE_ID, DomainConstants.SELECT_FROM_ID))
								.get(0);
						String strRelType = (String) mRelInfo.get(DomainRelationship.SELECT_TYPE);
						if (DomainConstants.RELATIONSHIP_ROUTE_NODE.equals(strRelType)) {
							if(strColumnName.equals(COLUMN_AUTHOR_TASK_SD)
							|| strColumnName.equals(COLUMN_APPROVER_TASK_SD)) {
								domRel.setAttributeValues(context, mAttrInfo);
							}else {
								mAttrInfo.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, strNewValue);
								domRel.setAttributeValues(context, mAttrInfo);
							}
						} else {
							strTaskId = (String) mRelInfo.get(DomainConstants.SELECT_FROM_ID);
							if (BusinessUtil.isNotNullOrEmpty(strTaskId)) {
								strRouteNodeId = BusinessUtil.getInfo(context, strTaskId,
										DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_NODE_ID));
								if (strColumnName.equals(AUTHOR_TASK_DURATION)
										|| strColumnName.equals(APPROVER_TASK_DURATION)) {
									lDuration = Long.valueOf(strNewValue);
									// strScheduleCompletionDate =
									// emxCommonInitiateRoute_mxJPO.getDateAfterAddingXDuration(sysDate,lDuration);
									strScheduleCompletionDate = getDateAfterAddingXDuration(sysDate, lDuration);
									mAttrInfo.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, strNewValue);
									mAttrInfo.put(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE,
											strScheduleCompletionDate);
								}
								domRel = DomainRelationship.newInstance(context, strRouteNodeId);
								domRel.setAttributeValues(context, mAttrInfo);
							}
						}
					}

					if (BusinessUtil.isNotNullOrEmpty(strTaskId)) {
						mAttrInfo.put(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE,
								strScheduleCompletionDate);
						domTask = DomainObject.newInstance(context, strTaskId);
						domTask.setAttributeValues(context, mAttrInfo);
					}

					strOutput = Json.createObjectBuilder()
							.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build().toString();
				}
			} catch (Exception ex) {
				logger.log(Level.SEVERE,
						"Error occured while executing method RTAUtil::updateRouteNodeOrTaskDueDate: ", ex);
				strOutput = Json.createObjectBuilder()
						.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
						.add(PGWidgetConstants.KEY_ERROR, ex.getMessage()).build().toString();

			}
			return strOutput;
		}
	
	/**
	 * get next date after adding duration
	 * @param String this is current date
	 * @param long this is the duration
	 * @return String final date 
	 * @throws Exception
	 */
	public static String getDateAfterAddingXDuration(Date currDate, long duration)
	{
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		cal.add(Calendar.DATE, (int)duration);
		Date dateAfterXDays = cal.getTime();
		SimpleDateFormat sDateformatter = new SimpleDateFormat(eMatrixDateFormat.strEMatrixDateFormat, Locale.US);
		String formatteDateAfterXDays	= sDateformatter.format(dateAfterXDays);
		return formatteDateAfterXDays;
		
	}
		/**
	   	 * This method gets the TimeZone details 
	   	 * @param context : context
	   	 * @return Double (Timezone)
	   	 */

		public static double getTimeZone(Context context) {
			try {
				String timeZone = context.getCustomData("timeZone");
				if(BusinessUtil.isNullOrEmpty(timeZone)) {
					timeZone = context.getTimezone();
					TimeZone tz = TimeZone.getTimeZone(timeZone);
				    Calendar cal = GregorianCalendar.getInstance(tz);
				    int offsetInMillis = tz.getOffset(cal.getTimeInMillis());
				    String offset = String.format("%02d:%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
				    timeZone = (offsetInMillis >= 0 ? "+" : "-") + offset;
				}
				return Double.parseDouble(timeZone);
			} catch (Exception e) {
				return 0.0;
			}
		}
	//RTA DS 2022x.05 added for ALM - 55631 Start
	/**
	 * This method is used to promote LC to release state.
	 * @param context
	 * @param strLocalId
	 * @param strCurrent
	 * @throws Exception
	 */
	public static void promoteLCtoReviewReleaseState(Context context, String strLocalId,String strCurrent) throws Exception {	
		StringList slLCSelects=new StringList(4);
		slLCSelects.add(DomainConstants.SELECT_CURRENT);
		slLCSelects.add(DomainConstants.SELECT_OWNER);
		slLCSelects.add(DomainConstants.SELECT_ID);
		boolean isContextPushed = false;
		StringList strRouteList = null;
		ArtworkContent artworkContent = null;
		String strRouteState = null;
		Route route = null;
		MapList mlRouteTasks = null;
		MapList mlAllRouteTasks = new MapList();
		String strInboxTaskWhere = AWLUtil.strcat("current", "!=", DomainConstants.STATE_INBOX_TASK_COMPLETE);
		try{
			if(BusinessUtil.isNotNullOrEmpty(strLocalId)){
				DomainObject domLocalObj = DomainObject.newInstance(context,strLocalId);
				artworkContent = ArtworkContent.getNewInstance(context, strLocalId);
				if("Preliminary".equalsIgnoreCase(strCurrent)) {
					if(artworkContent.isBaseCopy(context)){				
						strRouteList = RouteUtil.getConnectedRouteIDs(context, strLocalId, AWLConstants.MASTER_COPY_AUTHORING);
					}else{							
						strRouteList = RouteUtil.getConnectedRouteIDs(context, strLocalId, AWLConstants.LOCAL_COPY_AUTHORING);
					}						
				}else if("Review".equalsIgnoreCase(strCurrent)){
					if(artworkContent.isBaseCopy(context)){
						strRouteList = RouteUtil.getConnectedRouteIDs(context, strLocalId, AWLConstants.MASTER_COPY_APPROVAL);
					}else{							
						strRouteList = RouteUtil.getConnectedRouteIDs(context, strLocalId, AWLConstants.LOCAL_COPY_APPROVAL);
					}
				}
				if(BusinessUtil.isNotNullOrEmpty(strRouteList)){
					for(String strRouteId : (List<String>)strRouteList){
						if(BusinessUtil.isNotNullOrEmpty(strRouteId)){
							strRouteState = BusinessUtil.getInfo(context, strRouteId, DomainConstants.SELECT_CURRENT);
							if(BusinessUtil.isNotNullOrEmpty(strRouteState)) {
								if(strRouteState.equals(DomainConstants.STATE_ROUTE_IN_PROCESS) || strRouteState.equals(DomainConstants.STATE_ROUTE_DEFINE)){
									route = new Route(strRouteId);
									mlRouteTasks = route.getRouteTasks(context, slLCSelects, null, strInboxTaskWhere, false);
									mlAllRouteTasks.addAll(mlRouteTasks);
								}								
							}
						}
					}
					//Complete Task
					completeInboxTask(context,mlAllRouteTasks,isContextPushed);
				} else {
					DomainObject DOMlcObj = DomainObject.newInstance(context, strLocalId);
					if(AWLState.PRELIMINARY.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT).equals(strCurrent)) {
						DOMlcObj.promote(context);
					}else if(AWLState.REVIEW.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT).equals(strCurrent)){
						DOMlcObj.setState(context, DomainConstants.STATE_PART_RELEASE);
					}
				}
			}
		}catch(Exception ex) {				
			throw ex;
		}
	} 
	//RTA DS 2022x.05 added for ALM - 55631 End
	
	//RTA DS 2022x.05 added for ALM - 55631 Start
	/**
	 * This method is used to complete task.
	 * @param context
	 * @param mlAllRouteTasks
	 * @param isContextPushed
	 * @throws FrameworkException
	 */
	private static void completeInboxTask(Context context, MapList mlAllRouteTasks,boolean isContextPushed) throws FrameworkException {
		try {
			if(BusinessUtil.isNotNullOrEmpty(mlAllRouteTasks)){
				Iterator itr = mlAllRouteTasks.iterator();
				while(itr.hasNext())
				{
					Map map = (Map)itr.next();
					String strTaskId = (String)map.get(DomainConstants.SELECT_ID);
					String sTaskOwner = (String)map.get(DomainConstants.SELECT_OWNER);						
					if(BusinessUtil.isNotNullOrEmpty(strTaskId))
					{
						if(!sTaskOwner.equalsIgnoreCase(context.getUser())) {	
							//in order to complete the task, need to push context to task owner and then complete
							ContextUtil.pushContext(context, sTaskOwner, DomainConstants.EMPTY_STRING,context.getVault().getName());
							isContextPushed = true;
						}
						RouteUtil.CompleteInboxTask(context, strTaskId, AWLConstants.TASK_STATUS_NONE, AWLConstants.TASK_STATUS_NONE,  context.getSession().getLanguage(), getTimeZone(context));		
						if(isContextPushed) {	
							ContextUtil.popContext(context);
							isContextPushed = false;
						}
					}
				}				
			}
		}
		catch(Exception ex) {
			throw ex;
		}
		finally {
			if(isContextPushed) {	
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
	}
	//RTA DS 2022x.05 added for ALM - 55631 End
	
	public static String getRoboticPOA(Context context, String paramString) throws Exception {
		String sOutput = DomainConstants.EMPTY_STRING;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			paramString = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList sLPOAID = FrameworkUtil.split(paramString, PGWidgetConstants.KEY_OR);
			String sObjectId = sLPOAID.get(0);
			POA poa = new POA(sObjectId);
			StringList slCheckinReason = null;
			String sLatestRefDocCheckinReason = DomainConstants.EMPTY_STRING;
			String sPOAName = DomainConstants.EMPTY_STRING;

			BusinessObjectWithSelectList listRefDocInfo = new BusinessObjectWithSelectList();
			BusinessObjectWithSelect selectRefDocDetails = null;

			StringList slBusSelect = new StringList("to[" + RTAUtilConstants.RELATIONSHIP_ARTWORK_PACKAGE_CONTENT
					+ "].from.from[" + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT
					+ "|to.name ~~ 'GPSFixedInputOutputReport*'].to.attribute["
					+ DomainConstants.ATTRIBUTE_CHECKIN_REASON + "]");

			Map<String, String> mPOAMCID = new HashMap<String, String>();
			int iSize = sLPOAID.size();

			for (int i = 0; i < iSize; i++) {
				sObjectId = sLPOAID.get(i);
				poa = new POA(sObjectId);
				StringList sArrOfID = new StringList(sObjectId);
				DomainObject dmoPOAObject = DomainObject.newInstance(context, sObjectId);
				listRefDocInfo = DomainObject.getSelectBusinessObjectData(context, sArrOfID, slBusSelect, false);
				logger.log(Level.INFO, "listRefDocInfo----->(8044)", listRefDocInfo);
				selectRefDocDetails = listRefDocInfo.getElement(0);
				slCheckinReason = (StringList) selectRefDocDetails
						.getSelectDataList("to[" + RTAUtilConstants.RELATIONSHIP_ARTWORK_PACKAGE_CONTENT
								+ "].from.from[" + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT
								+ "].to.attribute[" + DomainConstants.ATTRIBUTE_CHECKIN_REASON + "]");
				logger.log(Level.INFO, "slCheckinReason----->(8049)", slCheckinReason);
				if (slCheckinReason != null && !slCheckinReason.isEmpty()) {
					sLatestRefDocCheckinReason = slCheckinReason.get(slCheckinReason.size() - 1);
					logger.log(Level.INFO, "sLatestRefDocCheckinReason----->(8053)", sLatestRefDocCheckinReason);
					sPOAName = poa.getName(context);
					if (sLatestRefDocCheckinReason.contains(sPOAName)) {
						mPOAMCID.put(sObjectId, sPOAName);
					}
				}
			}
			logger.log(Level.INFO, "mPOAMCID----->(8057)", mPOAMCID);
			if (!mPOAMCID.isEmpty()) {
				sOutput = new JsonHelper().getJsonString(mPOAMCID);
			}

		} catch (Exception ex) {
			logger.log(Level.INFO, "Exception in getRoboticPOA method - {0}", ex);
			sOutput = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_MESSAGE, ex.getMessage()).build().toString();
		}
		logger.log(Level.INFO, "sOutput----->(8066)", sOutput);
		return sOutput;
	}
	
	public static StringList getSelectsForCopyTaskStatus(Context context) {
		
		String strSelRoute = "from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]";
		String strSelIsBaseCopy = DomainObject.getAttributeSelect(AWLAttribute.IS_BASE_COPY.get(context));
		String strAttrInlineTranslation = AWLAttribute.INLINE_TRANSLATION.getSel(context);
		
		StringList selectsList = new StringList();
		selectsList.add(DomainConstants.SELECT_TYPE);
		selectsList.add(DomainConstants.SELECT_NAME);
		selectsList.add(DomainConstants.SELECT_ID);
		selectsList.add("attribute[" + RTAUtilConstants.ATTRIBUTE_MARKETINGNAME + "]");
		selectsList.add("attribute[" + RTAUtilConstants.ATTRIBUTE_CELANGUAGE + "]");
		selectsList.add(RTAUtilConstants.SELECT_APPROVER_ID);
		selectsList.add(RTAUtilConstants.SELECT_AUTHOR_ID);
		selectsList.add(strSelRoute);
		selectsList.add(strSelIsBaseCopy);
		selectsList.add(strAttrInlineTranslation);

		selectsList.add(DomainConstants.SELECT_OWNER);
		selectsList.add(DomainConstants.SELECT_REVISION);
		selectsList.add(DomainConstants.SELECT_CURRENT);
		selectsList.add(RTAUtilConstants.SELECT_PGAWLREFNO);
		selectsList.add(RTAUtilConstants.SELECT_TITLE);
		selectsList.add(RTAUtilConstants.SELECT_COPYTEXT);
		selectsList.add(RTAUtilConstants.SELECT_MARKETINGNAME);
		selectsList.add(RTAUtilConstants.SELECT_COPYTEXTLANG);
		selectsList.add(RTAUtilConstants.SELECT_ATTR_PG_CONFIRM_ASSIGNMENT);
		selectsList.add(RTAUtilConstants.SELECT_AUTHOR_ROUTE_STATUS);
		selectsList.add(RTAUtilConstants.SELECT_AUTHOR_ARTWORK_INFO);
		selectsList.add(RTAUtilConstants.SELECT_ATTRIBUTE_PGSUBCOPYTYPE);
		selectsList.add(RTAUtilConstants.SELECT_VALIDITY_DATE);
		selectsList.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id");
		selectsList.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "]."+AWLAttribute.NOTES.getSel(context));
		selectsList.add("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");
		selectsList.add(RTAUtilConstants.SELECT_APPROVER_ID);
		selectsList.add(RTAUtilConstants.SELECT_AUTHOR_ID);
		selectsList.add(RTAUtilConstants.SELECT_RTA_BG_JOB_STATUS);
		selectsList.add("from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
		
		return selectsList;
		
	}
	
	public static String getBaseCopyIdsForMCEs(Context context, String paramString) throws Exception {
		JsonObjectBuilder jsonObjOut = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			JsonArray strMCEIds = jsonInputData.getJsonArray(RTAUtil.STR_MCIDS);
			StringList slObjSel = new StringList();
			slObjSel.add(DomainConstants.SELECT_ID);
			slObjSel.add(RTAUtilConstants.SELECT_ISBASECOPY);
			String strWhere = RTAUtilConstants.SELECT_ISBASECOPY +" == Yes";
			StringList slBaseCopyIds = new StringList();
			for(int i=0 ; i<strMCEIds.size() ; i++) {
				MapList mlLcList = new ArtworkMaster(strMCEIds.getString(i)).getArtworkElements(context, slObjSel, null, strWhere);
				slBaseCopyIds.add((String) ((Map) mlLcList.get(0)).get(DomainConstants.SELECT_ID));
			}
			jsonObjOut.add(RTAUtilConstants.KEY_LCE_IDS, slBaseCopyIds.join(PGWidgetConstants.KEY_COMMA_SEPARATOR));

			return jsonObjOut.build().toString();
		}catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonObjOut.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
			return jsonObjOut.build().toString();
		}		
	}
	
	public static String getTasksForLocalCopies(Context context, String paramString) throws Exception
	{
		JsonArrayBuilder jsonArrOut = Json.createArrayBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			JsonArray jsonArrLCEIds = jsonInputData.getJsonArray(RTAUtilConstants.KEY_LCE_IDS);
			boolean isTypeCopyList = false;
			StringList slCLCountryList = new StringList();
			if (jsonInputData.containsKey("objIds")) {
				String strObjIds = jsonInputData.getString("objIds");
				StringList objIdList =  FrameworkUtil.split(strObjIds, PGWidgetConstants.KEY_PIPE_SEPARATOR);
				DomainObject popDom = DomainObject.newInstance(context, objIdList.get(0));
			    isTypeCopyList = popDom.isKindOf(context,AWLType.COPY_LIST.get(context));
				if(isTypeCopyList) {
					Pattern relPattern = new Pattern(EditCLConstants.RELATIONSHIP_COPYLISTCOUNTRY);
					Pattern typePattern = new Pattern(RTAUtilConstants.TYPE_COUNTRY);
					DomainObject domCLObj = DomainObject.newInstance(context, objIdList.get(0));
					StringList objSelects = new StringList(DomainObject.SELECT_NAME);
					MapList mlCopyListCountries = domCLObj.getRelatedObjects(context, // context,
							relPattern.getPattern(), // relationshipPattern,
							typePattern.getPattern(), // typepattern
							objSelects, // objectSelects
							null, // relationshipSelects,
							true, // getFrom
							true, // getTo,
							(short) 1, // recurseToLevel,
							null, // objectWhere,
							null, // relWhere
							0); // limit
					for (int i = 0; i < mlCopyListCountries.size(); i++) {
			            Map map = (Map) mlCopyListCountries.get(i);
			            String countryName = (String) map.get(DomainConstants.SELECT_NAME);
			            slCLCountryList.add(countryName);
			        }
		            			
				}
			}
			MapList mlTaskList = new MapList();

			StringList selectsList = getSelectsForCopyTaskStatus(context);

			String strSelRoute = "from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]";

			long start = System.currentTimeMillis();
			Map mAandAPersonInfo = new HashMap();
			StringList slLCIds = new StringList(jsonArrLCEIds.size());
			for (int i = 0; i < jsonArrLCEIds.size(); i++) {
				slLCIds.add(jsonArrLCEIds.getString(i));
			}
			
			MapList mlLCInfo = BusinessUtil.getInfo(context, slLCIds, selectsList);
			Iterator itr = mlLCInfo.iterator();
			while (itr.hasNext()) {
				Map mapLCE = (Map)itr.next();
				String strCEId = (String) mapLCE.get(DomainConstants.SELECT_ID);
				String strIsRouteConnected = (String) mapLCE.get(strSelRoute);
				String strLangId = (String) mapLCE.get("from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
				if(isTypeCopyList) {
					StringList countryList = getCountryFromLang(context, strLangId, mapLCE, slCLCountryList);
					mapLCE.put(RTAUtilConstants.KEY_COUNTRIES, countryList);
				}
				if ("true".equalsIgnoreCase(strIsRouteConnected)) {
					getAllTasksConnectedToRoute(context, strCEId, mapLCE, mlTaskList, mAandAPersonInfo);
				} else {
					String strApproverId = (String) mapLCE.get(RTAUtilConstants.SELECT_APPROVER_ID);
					String strApproverName = getAssigneeDisplayName(context, strApproverId, mAandAPersonInfo);
					String strAuthorId = (String) mapLCE.get(RTAUtilConstants.SELECT_AUTHOR_ID);
					String strAuthorName = getAssigneeDisplayName(context, strAuthorId, mAandAPersonInfo);
					mapLCE.put(RTAUtilConstants.SELECT_APPROVER_NAME, strApproverName);
					mapLCE.put(RTAUtilConstants.SELECT_AUTHOR_NAME, strAuthorName);
					mlTaskList.add(mapLCE);
				}
			}
			logger.log(Level.INFO,"mlTaskList size :: "+mlTaskList.size());
			
			Iterator itrMlTaskList = (BusinessUtil.isNotNullOrEmpty(mlTaskList)) ? mlTaskList.iterator()
					: new MapList().iterator();

			while (itrMlTaskList.hasNext()) {
				jsonArrOut.add(PGWidgetUtil.getJSONFromMap(context, (Map) itrMlTaskList.next()));
			}
			logger.log(Level.INFO, "Time taken for loading ALL LCs -> {0}",
					(System.currentTimeMillis() - start));
			return jsonArrOut.build().toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGWidgetUtil.getExceptionTrace(e));
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, PGWidgetUtil.getExceptionTrace(e)).build().toString();
		}
	}
	
	public static String getLocalCopiesForPOAs(Context context, String paramString) throws Exception {
		JsonObjectBuilder jsonObjOut = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			JsonArray jsonArrPOAIds = jsonInputData.getJsonArray(RTAUtilConstants.POA_IDs);
			String strSelLCEId = "from[" + AWLRel.ARTWORK_ASSEMBLY.get(context) + "].to."+DomainConstants.SELECT_ID;

			StringList slFinal = new StringList();
			long start = System.currentTimeMillis();
			for (int i = 0; i < jsonArrPOAIds.size(); i++) {
				POA poa = new POA(jsonArrPOAIds.getString(i));
				StringList slLCEids = poa.getInfoList(context, strSelLCEId);
				slFinal.addAll(slLCEids);
			}
			logger.log(Level.INFO, "Time taken for loading ALL LCs ---------------> - {0}",
					(System.currentTimeMillis() - start));
			jsonObjOut.add(RTAUtilConstants.KEY_LCE_IDS, slFinal.join(PGWidgetConstants.KEY_COMMA_SEPARATOR));
			return jsonObjOut.build().toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
			logger.log(Level.SEVERE, e.getMessage());
			jsonObjOut.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			return jsonObjOut.build().toString();
		}
	}
	
	/**
	 * Method to get the Job status
	 * @param context : Context eMatrix context object
	 * @param strReportId : String report object is
	 * @param strJobId : String Job object id
	 * @return : String json with Job details else report doc info
	 * @throws Exception
	 */
	public static String getJobStatus(Context context, String strJobId) throws Exception {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		try {
			if(UIUtil.isNotNullAndNotEmpty(strJobId))
			{
				DomainObject jobObject = DomainObject.newInstance(context,strJobId);
				
				StringList slJobSelect = new StringList(3);
				slJobSelect.add(DomainConstants.SELECT_CURRENT);
				slJobSelect.add(RTAUtilConstants.SELECT_ATTRIBUTE_COMPLETION_STATUS);
				slJobSelect.add(RTAUtilConstants.SELECT_ATTRIBUTE_ERROR_MESSAGE);
				Map<?, ?> jobMap = jobObject.getInfo(context,slJobSelect);
				/*if(!RTAUtilConstants.JOB_SUCCEEDED.equals((String)jobMap.get(RTAUtilConstants.SELECT_ATTRIBUTE_COMPLETION_STATUS)))
				{*/
					jsonObjOutput.add(RTAUtilConstants.KEY_JOB_ID, strJobId);
					jsonObjOutput.add(RTAUtilConstants.KEY_JOB_STATUS,(String)jobMap.get(DomainConstants.SELECT_CURRENT));
					jsonObjOutput.add(RTAUtilConstants.KEY_JOB_COMPLETION_STATUS,(String)jobMap.get(RTAUtilConstants.SELECT_ATTRIBUTE_COMPLETION_STATUS));
					jsonObjOutput.add(RTAUtilConstants.KEY_JOB_ERROR_MESSAGE,(String)jobMap.get(RTAUtilConstants.SELECT_ATTRIBUTE_ERROR_MESSAGE));
					return jsonObjOutput.build().toString();
				//}
			}
			/*if (UIUtil.isNotNullAndNotEmpty(strReportId)) {
				DomainObject dobReportObject = DomainObject.newInstance(context, strReportId);
				Map<?,?> objectInfo = dobReportObject.getInfo(context, new StringList(RTAUtilConstants.SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION));
				String strAttInputInfo = (String) objectInfo.get(RTAUtilConstants.SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);
				return getDocumentDetails(context, strAttInputInfo);
			} else {
				jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, RTAUtilConstants.VALUE_FAILED);
				//jsonObjOutput.add(PGWidgetConstants.KEY_ERROR, VALUE_ERROR_MESSAGE);
			}*/
		} catch (Exception e) {
			jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, RTAUtilConstants.VALUE_FAILED);
			if(UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				jsonObjOutput.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			}
			throw e;
		}
		return jsonObjOutput.build().toString();
	}
	
	private static List<JsonObject> jsonArrayToList(JsonArray jsonArray) {
        List<JsonObject> list = new ArrayList<>();
        jsonArray.forEach(listItem -> list.add((JsonObject) listItem));
        return list;
    }

    private static JsonArray listToJsonArray(List<JsonObject> list) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        list.forEach(jsonValue -> jsonArrayBuilder.add(jsonValue));
        return jsonArrayBuilder.build();
    }
    
    public static JsonArray mergeArrays(JsonArray array1, JsonArray array2) {
    	// Convert JSON arrays to lists for manipulation
    	List<JsonObject> list1 = jsonArrayToList(array1);
    	List<JsonObject> list2 = jsonArrayToList(array2);

    	// Create a new list to hold merged elements
    	List<JsonObject> mergedList = new ArrayList<>();

    	// Add all elements from the first array
    	mergedList.addAll(list1);

    	// Add all elements from the second array
    	mergedList.addAll(list2);

    	// Build a new JsonArray from the merged list
    	JsonArray mergedJsonArray = listToJsonArray(mergedList);
    	return mergedJsonArray;
    }

    public static String getArtworkElementInfo(Context context, String paramString) throws Exception {
    	JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
    	String strMCEIds = jsonInputData.getString(STR_MCIDS);
    	if (BusinessUtil.isNotNullOrEmpty(strMCEIds)) {
    		StringList slMCEIds = FrameworkUtil.split(strMCEIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
    		StringList slSelectsLocalCopies = StringList.create(DomainObject.SELECT_ID, RTAUtilConstants.SELECT_ATTR_AWL_INSTRUCTIONS);

				MapList mlMCEInfo = DomainObject.getInfo(context, slMCEIds.toStringArray(), slSelectsLocalCopies);
				MapList finalMCEInfo = new MapList();
				for(int i=0; i<mlMCEInfo.size(); i++) {
					Map mMCEInfo = (Map) mlMCEInfo.get(i);
					Map map = new HashMap<>();
		            map.put(mMCEInfo.get(DomainObject.SELECT_ID), mMCEInfo.get(RTAUtilConstants.SELECT_ATTR_AWL_INSTRUCTIONS));
		            finalMCEInfo.add(map);
				}
				JsonArray outputArray = PGWidgetUtil.converMaplistToJsonArray(context, finalMCEInfo);
				return outputArray.toString();
			}
			return "";
		}
		
		private static void validityDateCheck(Context context, MapList mlPOAData) throws Exception {
			String strValidateDate = null;
			Date date =null;
			Date sytemTodaysDate = null;
			Date sCEValidityDate =null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			for (int i = 0; i < mlPOAData.size(); i++) {
				Map mCEInfo = (Map<?, ?>) mlPOAData.get(i);
				strValidateDate = (String)mCEInfo.get(RTAUtilConstants.SELECT_VALIDITY_DATE); 
				if(BusinessUtil.isNotNullOrEmpty(strValidateDate)){
					date = new Date();
					sytemTodaysDate = dateFormat.parse(dateFormat.format(date));
					sCEValidityDate = dateFormat.parse(strValidateDate);
					if(sytemTodaysDate.after(sCEValidityDate))
					{
						mCEInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, PGWidgetConstants.STRING_TRUE);
					}else {
						mCEInfo.put(EditCLConstants.KEY_IS_VALIDITY_DATE_EXPIRED, PGWidgetConstants.STRING_FALSE);
					}
				}
			}
		}
		
		 private static void processPOAMCLCDetails(Context context, MapList mlPOAMCLCList, MapList mlPOAMCList, MapList mlPOALCList, MapList mlPOALCs) throws Exception{
    	try {
    		
    		Iterator itr = mlPOALCList.iterator();
    		Map mCEInfo = null;
    		Map mMasterCE = null;
    		String strCEId = null;
    		String strIsBaseCopy = null;
    		String strAttrCopyTextRTESel = new StringBuilder("attribute[").append(AWLAttribute.COPY_TEXT.get(context)).append("_RTE]").toString();
    		String strSelFromCopyTextRTE = new StringBuilder("from[").append(AWLRel.ARTWORK_ELEMENT_CONTENT.get(context)).append("].to.").append(strAttrCopyTextRTESel).toString();
    		Map mMCInfo = new HashMap();
    		Map mLCInfo = new HashMap();
    		Map mBaseCopyInfo = new HashMap();
    		while(itr.hasNext()) {
    			mCEInfo = (Map)itr.next();
    			strCEId = (String) mCEInfo.get(DomainConstants.SELECT_ID); 
    			mLCInfo.put(strCEId, mCEInfo);
    		}
    		
    		mlPOAMCLCList.sort(DomainConstants.SELECT_LEVEL, "ascending", "integer");
    		itr = mlPOAMCLCList.iterator();
    		
    		String strLevel = null;
    		while(itr.hasNext()) {
    			mCEInfo = (Map)itr.next();
    			strCEId = (String) mCEInfo.get(DomainConstants.SELECT_ID);
    			strLevel = (String)mCEInfo.get(DomainConstants.SELECT_LEVEL);
    			if("1".equals(strLevel)) {
    				mMCInfo.put(strCEId, mCEInfo);
    				mlPOAMCList.add(mCEInfo);
    			} else {
    				strIsBaseCopy = (String) mCEInfo.get(DomainConstants.SELECT_ID);
    				if(mLCInfo.containsKey(strCEId)) {
	    				mCEInfo.putAll((Map)mLCInfo.get(strCEId));
	    				mCEInfo.put("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id", (String)mCEInfo.get(DomainRelationship.SELECT_FROM_ID));
	    				mCEInfo.put("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type", (String)mCEInfo.get(DomainRelationship.SELECT_FROM_TYPE));
	    				mlPOALCs.add(mCEInfo);
    				}
    				if(AWLConstants.RANGE_YES.equalsIgnoreCase(strIsBaseCopy)) {
    					mMasterCE = (Map) mMCInfo.get((String)mCEInfo.get(DomainRelationship.SELECT_FROM_ID));
    					mMasterCE.put(strSelFromCopyTextRTE, (String) mCEInfo.get(strAttrCopyTextRTESel)); 
    				}
    			}
    		}
    	}catch(Exception ex) {
    		throw ex;
    	}
    }
	   
}