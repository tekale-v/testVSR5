/*
Project Name: P&G Artwork Apadtion Plan
Service Name: pgArtworkAdaptionPlanService
Purpose: This class contains logic to send the xml's to third party client.
 */
package com.pg.rulesengine;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.custom.pg.Artwork.ArtworkConstants;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.awl.enumeration.AWLType;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.matrixone.servlet.Framework;
import com.matrixone.servlet.FrameworkServlet;
import javax.ws.rs.GET;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.cpd.json.JSONObject;
import com.matrixone.apps.cpd.json.JSONObject.Order;
import com.matrixone.apps.awl.enumeration.AWLAttribute;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.cpd.json.JSONArray;
import java.util.Arrays;
import matrix.util.MatrixException;
import com.matrixone.apps.awl.util.AWLConstants;
import com.matrixone.apps.awl.util.AWLUtil;
import matrix.db.FileList;
import org.apache.commons.text.WordUtils;
import com.matrixone.apps.productline.ProductLineConstants;
import java.util.List;
//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
//Added by Sogeti Offshore for 2018x.3 Defect #33451 STARTS
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
//Added by Sogeti Offshore for 2018x.3 Defect #33451 ENDS
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.program.ProgramCentralConstants;
import com.pg.util.EncryptCrypto;
import com.pg.v3.custom.pgV3Constants;
//Modified By RTA Sogeti for Defect #31866 STARTS
import java.util.Base64;
//Modified By RTA Sogeti for Defect #31866 ENDS
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.Pattern;
import matrix.util.StringList;
//Added By RTA Sogeti for Defect #33235 STARTS
import com.matrixone.apps.domain.util.i18nNow;
//Added By RTA Sogeti for Defect #33235 ENDS
@javax.ws.rs.Path(value = "/artWorkAdaptionPlan")
public class pgArtworkAdaptionPlanService extends RestService {

	private static final Logger logger = Logger.getLogger("com.pg.rulesengine.pgArtworkAdaptionPlanService");

	private static final String POA_ID = "poaId";
	private static final String POA_NAME = "poaName";
	private static final String SEPARATOR = java.io.File.separator;
	private static final String FROM_ID = "].from.id";
	private static final String FROM_NAME = "].from.name";
	private static final String RELATIONSHIP = "relationship[";
	private static final String ATTRIBUTE = "attribute[";
	private static final String ONE = "001";
	private static final String AUTHORIZED = "authorized";
	private static final String DOES_NOT_EXIST = "does not exist";
	private static final String NO_CONTEXT_USER = "No context user";
	private static final String INVALID_PASSWORD = "Invalid password";

	private static String sAttrOldIPMS;
	private static String sAttrCustSpec;
	private static String sAttrFPC;
	//Added by Sogeti Offshore for 18x.5 Req #35381 STARTS
	private static String sAttrFPCDescription;
	//Added by Sogeti Offshore for 18x.5 Req #35381 ENDS
	private static String sAttrPackLevel;
	private static String sAttrGTIN;
	private static String sAttrSuppGTIN;
	private static String sAttrCaseCount;
	private static String sAttrMarket;
	private static String sAttrAddlDesc;
	private static String sAttrIPMS;
	private static String sAttrMatrlCat;
	private static String sAttrCompType;
	private static String RELATIONSHIP_POA_CASE_TYPE;
	private static String RELATIONSHIP_PRINTING_PROCESS;
	private static String RELATIONSHIP_ASSOCIATED_POA;
	private static String RELATIONSHIP_SUPPLIER_POA;
	private static String RELATIONSHIP_ARTWORK_PACKAGE_CONTENT;
	private static String RELATIONSHIP_POA_TO_ARTWORK_ASSIGNEE;
	private static String ATTRIBUTE_BVE_MOD_DATE;
	private static String ATTRIBUTE_PG_PKG_PROJECT_ID;
	private static String RELATIONSHIP_PGAAA_PROJECTTOPOA;
	private static String SUPPLIER_NAME;
	private static String ATTRIBUTE_PGCIB_AS2_PATH;
	private static String ATTRIBUTE_BVE_DATE;
	//Added By RTA Sogeti for Defect #33235 STARTS
	private static final String STRING_RESOURCE_EMX_CPN = "emxCPN";
	private static final String STRING_RESOURCE_KEY_APP_USER = "emxCPN.AAL.applicationUser";
	private static final String STRING_RESOURCE_KEY_APP_PASS = "emxCPN.AAL.applicationUserPass";
	//Added By RTA Sogeti for Defect #33235 ENDS
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	private static final String STR_DEFAULT_LOCALE_EN = "en_US";
	private static final String STR_TEMP = "Temp";
	private static final String STR_INVALID_SESSION = "INVALID SESSION";
	private String JSON_FILE_NAME = "AAL_CopyElementInfo";
	private static final String STR_REGX_QUOTE_REPLACEMENT = "\\\\\'";
	private static final String STR_REGX_QUOTE_REPLACER = "\\\'";
	private static final String STR_ESCAPE_KEY = "escape";
	private static final String STR_500ERROR_MESSAGE = "Enovia System Error, please contact Administrator.";
	private static final String STR_500ERROR_KEY = "500 INTERNAL SERVER ERROR";
	private static final String STR_401ERROR_KEY = "401 UNAUTHORIZED";
	private static String ATTRIBUTE_MARKETINGNAME;
	private static String ATTRIBUTE_COPY_TEXT;
	private static String SELECT_ATTRIBUTE_MARKETINGNAME;
	private static String SELECT_ATTRIBUTE_COPY_TEXT;
	private i18nNow i18nObject;
	private static final String STR_COND_EQUAL_TO = "==";
	private static final String STR_S_QUOTE = "'";
	private static final String STR_COND_AND = " && ";
	private static final String STR_MATCH = " match ";
	private static final String STR_S_HYPEN = "-";
	private static final String STR_S_PIPE = "|";
	private static final String STR_MASTER_SPACE = "Master ";
	private static final String KINDOF_OPEN = "kindof[";
	private static final String STR_EXTN_JSON = ".json";
	private static final String STR_S_SPACE = " ";
	private static final String STR_MASTER_COPY= "Master Copy";
	private static final String STR_DATE_FORMAT_YMDHS = "yyyy.MM.dd.HH.mm.ss";
	private static final String STR_UNDERSCORE = "_";
	private static final String STR_EXTN_ZIP = ".zip";
	private static final String STR_FILE_CREATED = "fileCreated";
	private static final String STR_CLOSE_DOT = "].";
	private static final String STR_CLOSE_DOT_TO_OPEN = "].to[";
	private static final String STR_ENCODE_BASE64 = "encodeBase64";
	private static final String SERVER_HOST_NOT_FOUND = "Server host not found";
	private static final String ARTWORK_CONTENT_COPY_ELEMENT = "artworkContentCopyElement";
	private static final String LOCAL_COPIES = "localCopies";
	private static final String LANGUAGE = "language";
	private static final String TEXT_CONTENT = "textContent";
	private static final String IMAGE_LOCATION_FOR_MAP = "imageLocation";
	private static final String IS_BASE_COPY = "isBaseCopy";
	private static final String ENOVIA_STATE = "enoviaState";
	private static final String ENOVIA_REVISION = "enoviaRevision";
	private static final String MARKETING_NAME = "marketingName";
	private static final String COPY_ELEMENT_TYPE_CODE = "copyElementTypeCode";
	private static final String ENOVIA_NAME = "enoviaName";
	private static final String IMAGE_LOCATION_FROM_DB = "ImageLocation";
	private static String STR_JSON_ORDER_KEYS_MASTER;
	private static String STR_JSON_ORDER_KEYS_LOCAL;
	private static String RELATIONSHIP_ARTWORK_ELEMENT_CONTENT;
	private static String TYPE_MASTERARTWORKELEMENT;//Master Artwork Element
	private static String TYPE_MASTERARTWORKGRAPHICELEMENT;//'Master Artwork Graphic Element'
	private static String TYPE_ARTWORKELEMENT;//Artwork Element
	private static String SELECT_KINDOF_TYPE_MASTERARTWORKGRAPHICELEMENT;
	private static String ATTRIBUTE_IS_BASE_COPY;
	private static String SELECT_ATTRIBUTE_IS_BASE_COPY;
	private static String RELATIONSHIP_ARTWORK_MASTER;
	private static String TYPE_SUB_BRAND;
	private static String TYPE_PRODUCTTYPE;
	private static String ATTRIBUTE_COPY_TEXT_LANGUAGE;
	private static String SELECT_ATTRIBUTE_COPY_TEXT_LANGUAGE;
	private static String SELECT_GRAPHIC_DOC_ID;
	private boolean bFileCreationSuccess = false;
	private boolean bGraphicFilesPresent = false;
	private static String EMXAWLSTRINGRESOURCE="emxAWLStringResource";
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	@POST
	@javax.ws.rs.Path("/getPOADetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPOADetails(POARequestData poaRequestData) throws Exception {
		logger.info("Inside getPOADetails() method --- starts");
		Context context = null;
		POAResponseData responseData = null;
		try {
			/*
			 * Check if POA exist in System and if it is connected to a project then
			 * generate copy and cover/project xml
			 */
			responseData = findPOA(poaRequestData, context, new String[] { poaRequestData.getPoaNumber() });
		} catch (Exception e) {
			logger.info("Got an exception. " + e);
		}
		logger.info("Inside getPOADetails() method --- ends");
		return Response.ok(responseData).build();
	}

	//Added By RTA Sogeti offshore for 18x.5 Req 33381 STARTS
	/**
	 * 
	 * @param paramHttpServletRequest
	 * @return
	 * @throws Exception
	 */
	private Context getContext(HttpServletRequest paramHttpServletRequest){
		logger.info("Inside getContext() method --- Starts");
		Context context = null;
		if (Framework.isLoggedIn(paramHttpServletRequest)) {
			context = Framework.getContext(paramHttpServletRequest.getSession(false));
		}
		logger.info("Inside getContext() method --- ends");
		return context;
	}
	//Added By RTA Sogeti offshore for 18x.5 Req 33381 ENDS
	
	//Added By RTA Sogeti offshore for 18x.5 Req 33381 Starts
	/**
	 * 
	 * @param request
	 * @return
	 */
	@javax.ws.rs.Path("/logout")
    @Produces({"application/ds-json", "application/xml"})
    @GET
    public Response logout(@javax.ws.rs.core.Context HttpServletRequest request) {
		logger.info("Inside logout() method --- Starts");
        try {
            FrameworkServlet.doLogout(request.getSession());
            logger.info("Inside logout() method --- Ends");
            return Response.status(200).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Inside logout() method --- Ends");
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
	//Added By RTA Sogeti offshore for 18x.5 Req 33381 ENDS
	
	/**
	 * Get the POA and generate the xml files
	 * 
	 * @param poaRequestData
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private POAResponseData findPOA(POARequestData poaRequestData, Context context, String[] args) throws Exception {
		logger.info("Inside findPOA() method --- starts");
		byte[] zipFileByteContent = {};
		String zipfileName = DomainConstants.EMPTY_STRING;
		POAResponseData response = null;
		String errorMsg = DomainConstants.EMPTY_STRING;
		try {
			// get context
			context = new Context(DomainConstants.EMPTY_STRING);
			
			//Modified By RTA Sogeti for Defect #33235 STARTS
			i18nNow i18nObject = new i18nNow();
			String strLocale = context.getLocale().toString();
			String webServiceUser = i18nObject.GetString(STRING_RESOURCE_EMX_CPN, strLocale, STRING_RESOURCE_KEY_APP_USER);
			String webServiceUserPass = i18nObject.GetString(STRING_RESOURCE_EMX_CPN, strLocale, STRING_RESOURCE_KEY_APP_PASS);
			//context.setUser(poaRequestData.getApplicationUser());
			context.setUser(webServiceUser);
			//context.setPassword(EncryptCrypto.decryptString(poaRequestData.getApplicationUserPass()));
			if (BusinessUtil.isNotNullOrEmpty(webServiceUserPass)) {
				webServiceUserPass = EncryptCrypto.decryptString(webServiceUserPass);
			}
			context.setPassword(webServiceUserPass);
			context.connect();
			//Modified By RTA Sogeti for Defect #33235 STARTS
			
			// set the property values
			setPropertyValues(context);

			String poaName = args[0];
			StringList poaSelects = new StringList();
			poaSelects.add(DomainConstants.SELECT_ID);
			poaSelects.add(DomainConstants.SELECT_CURRENT);
			poaSelects.add("to[" + RELATIONSHIP_PGAAA_PROJECTTOPOA + FROM_ID);
			poaSelects.add(
					"to[" + RELATIONSHIP_PGAAA_PROJECTTOPOA + "].from.attribute[" + ATTRIBUTE_PG_PKG_PROJECT_ID + "]");
			poaSelects.add("to[" + RELATIONSHIP_ARTWORK_PACKAGE_CONTENT + "].from.name");
			poaSelects.add("to[" + RELATIONSHIP_SUPPLIER_POA + FROM_NAME);

			MapList poaMap = DomainObject.findObjects(context, pgV3Constants.TYPE_POA, poaName, ONE,
					DomainConstants.QUERY_WILDCARD, pgV3Constants.VAULT_ESERVICEPRODUCTION,
					DomainConstants.EMPTY_STRING, false, poaSelects);

			if (BusinessUtil.isNotNullOrEmpty(poaMap)) {
				Map infoMap = (Map) poaMap.get(0);
				String poaObjectId = (String) infoMap.get(poaSelects.get(0));
				String poaState = (String) infoMap.get(poaSelects.get(1));
				String poaProject = (String) infoMap.get(poaSelects.get(2));
				String projectPKGId = (String) infoMap.get(poaSelects.get(3));
				String artworkPackage = (String) infoMap.get(poaSelects.get(4));
				String supplierName = (String) infoMap.get(poaSelects.get(5));
				poaProject = poaProject == null ? DomainConstants.EMPTY_STRING : poaProject.trim();

				// GENERATE COPY XML
				String copyXMLString = generateCopyXML(context, new String[] { poaObjectId, poaName });
				String projectXMLString = DomainConstants.EMPTY_STRING;
				String coverXMLString = DomainConstants.EMPTY_STRING;
				if (!DomainConstants.EMPTY_STRING.equals(poaProject)) {
					// GENERATE PROJECT XML
					supplierName = supplierName == null ? DomainConstants.EMPTY_STRING : supplierName.trim();
					ContextUtil.pushContext(context);
					projectXMLString = generateProjectXML(context, new String[] { poaProject, poaName, supplierName });
					ContextUtil.popContext(context);
				} else {
					// GENERATE COVER XML
					ContextUtil.pushContext(context);
					coverXMLString = generateCoverXML(context, new String[] { poaObjectId });
					ContextUtil.popContext(context);
				}

				// Generate zip file and get the byte data of the file contents
				String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
				String workSpacePath = context.createWorkspace();
				zipfileName = poaName + '_' + timeStamp;
				workSpacePath = workSpacePath + SEPARATOR + zipfileName;
				File workingDirectory = new File(workSpacePath);
				boolean isFolderCreated = workingDirectory.mkdir();
				if (isFolderCreated) {
					if (BusinessUtil.isNotNullOrEmpty(copyXMLString)) {
						writeToXML(workSpacePath, copyXMLString, poaName + ".xml");
					}
					if (BusinessUtil.isNotNullOrEmpty(projectXMLString)) {
						writeToXML(workSpacePath, projectXMLString, projectPKGId + ".xml");
					}
					if (BusinessUtil.isNotNullOrEmpty(coverXMLString)) {
						writeToXML(workSpacePath, coverXMLString, artworkPackage + ".xml");
					}
				}

				String zipFilePath = workSpacePath + ".zip";
				zipFileByteContent = zipDirectory(workSpacePath, zipFilePath);

				File workSpaceDirectory = new File(zipFilePath);
				if (workSpaceDirectory.exists()) {
					workSpaceDirectory.delete();
				}

				File fileFolder = new File(workSpacePath);
				if (fileFolder.isDirectory()) {
					File currentFile = null;
					String entryPath = fileFolder.getPath();
					String[] entries = fileFolder.list();
					for (String entry : entries) {
						currentFile = new File(entryPath, entry);
						currentFile.delete();
					}
					fileFolder.delete();
				}
				//Modified By RTA Sogeti for Defect #31866 STARTS
				//String sencodeString=Base64.encode(zipFileByteContent);
				String sencodeString = Base64.getEncoder().encodeToString(zipFileByteContent);
				//Modified By RTA Sogeti for Defect #31866 ENDS
				response = new POAResponseData("200 OK", sencodeString, zipfileName);
			} else {
				errorMsg = "POA does not exist in the Enovia System";
				response = new POAResponseData("201 OK", errorMsg);
			}
		} catch (Exception e) {
			errorMsg = e.getMessage().trim();
			if (errorMsg.contains(NO_CONTEXT_USER) || errorMsg.contains(DOES_NOT_EXIST)) {
				e.printStackTrace();
				errorMsg = "User does not exist.";
				response = new POAResponseData("404 NOT FOUND", errorMsg);
			} else if (errorMsg.contains(AUTHORIZED) || errorMsg.contains(INVALID_PASSWORD)) {
				e.printStackTrace();
				errorMsg = "User not authorised.";
				response = new POAResponseData("401 UNAUTHORIZED", errorMsg);
			} else {
				e.printStackTrace();
				errorMsg = "Enovia System Error, please contact Administrator.";
				response = new POAResponseData("500 INTERNAL SERVER ERROR", errorMsg);
			}
		} finally {
			if (null != context) {
				// shut down the context
				context.shutdown();
			}
		}
		logger.info("Inside findPOA() method --- ends");
		return response;
	}

	/**
	 * set the property values
	 * 
	 * @param context
	 */
	private static void setPropertyValues(Context context) {
		logger.info("Inside setPropertyValues() method --- starts");

		sAttrOldIPMS = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_OldIPMS");
		sAttrCustSpec = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_CustomIPMS");
		sAttrFPC = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_FinishedProductCode");
		//Added by Sogeti Offshore for 18x.5 Req #35381 STARTS
		sAttrFPCDescription = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_FPCDescription");
		//Added by Sogeti Offshore for 18x.5 Req #35381 ENDS
		sAttrPackLevel = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_PackagingLevel");
		sAttrGTIN = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_GTIN");
		sAttrSuppGTIN = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_GTIN_Suppressed");
		sAttrCaseCount = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_CaseCount");
		sAttrMarket = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_Market");
		sAttrAddlDesc = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_AdditionalDescription");
		sAttrIPMS = PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_IPMS");
		sAttrMatrlCat = PropertyUtil.getSchemaProperty(context, "attribute_MaterialCategory");
		sAttrCompType = PropertyUtil.getSchemaProperty(context, "attribute_pgRTAComponentType");
		RELATIONSHIP_POA_CASE_TYPE = PropertyUtil.getSchemaProperty(context, "relationship_pgAAA_POATopgPLICaseType");
		RELATIONSHIP_PRINTING_PROCESS = PropertyUtil.getSchemaProperty(context,
				"relationship_pgAAA_POATopgPLIPrintingProcess");
		RELATIONSHIP_ASSOCIATED_POA = PropertyUtil.getSchemaProperty(context, "relationship_AssociatedPOA");
		RELATIONSHIP_SUPPLIER_POA = PropertyUtil.getSchemaProperty(context, "relationship_SupplierPOA");
		RELATIONSHIP_ARTWORK_PACKAGE_CONTENT = PropertyUtil.getSchemaProperty(context,
				"relationship_ArtworkPackageContent");
		RELATIONSHIP_POA_TO_ARTWORK_ASSIGNEE = PropertyUtil.getSchemaProperty(context,
				"relationship_pgAAA_POAToArtworkAssignee");
		ATTRIBUTE_BVE_MOD_DATE = PropertyUtil.getSchemaProperty(context, "attribute_pgBVELastModifiedDate");
		ATTRIBUTE_PG_PKG_PROJECT_ID = PropertyUtil.getSchemaProperty(context, "attribute_pgPKGProjectID");
		RELATIONSHIP_PGAAA_PROJECTTOPOA = PropertyUtil.getSchemaProperty(context, "relationship_pgAAA_ProjectToPOA");
		SUPPLIER_NAME = PropertyUtil.getSchemaProperty(context, "relationship_SupplierPOA");
		ATTRIBUTE_BVE_DATE = PropertyUtil.getSchemaProperty(context, "attribute_pgBVELastModifiedDate");
		ATTRIBUTE_PGCIB_AS2_PATH = PropertyUtil.getSchemaProperty(context, "attribute_pgCIB_AS2_Path");
		
		
		//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
		ATTRIBUTE_MARKETINGNAME = AWLAttribute.MARKETING_NAME.get(context);//attribute_MarketingName;
		ATTRIBUTE_COPY_TEXT = AWLAttribute.COPY_TEXT.get(context);//attribute_CopyText
		SELECT_ATTRIBUTE_MARKETINGNAME = AWLUtil.strcat(ATTRIBUTE, ATTRIBUTE_MARKETINGNAME, "]");
		SELECT_ATTRIBUTE_COPY_TEXT = AWLUtil.strcat(ATTRIBUTE, ATTRIBUTE_COPY_TEXT, "]");
		RELATIONSHIP_ARTWORK_ELEMENT_CONTENT =AWLRel.ARTWORK_ELEMENT_CONTENT.get(context);//relationship_ArtworkElementContent
		TYPE_MASTERARTWORKELEMENT =AWLType.MASTER_ARTWORK_ELEMENT.get(context);
		TYPE_MASTERARTWORKGRAPHICELEMENT = AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context);//type_MasterArtworkGraphicElement
		SELECT_KINDOF_TYPE_MASTERARTWORKGRAPHICELEMENT = AWLUtil.strcat(KINDOF_OPEN, TYPE_MASTERARTWORKGRAPHICELEMENT, "]");
		TYPE_ARTWORKELEMENT =AWLType.ARTWORK_ELEMENT.get(context);//type_LabelElement
		ATTRIBUTE_IS_BASE_COPY = AWLAttribute.IS_BASE_COPY.get(context);//attribute_IsBaseCopy
		SELECT_ATTRIBUTE_IS_BASE_COPY = AWLUtil.strcat(ATTRIBUTE, ATTRIBUTE_IS_BASE_COPY ,"]");
		RELATIONSHIP_ARTWORK_MASTER = AWLRel.ARTWORK_MASTER .get(context);//relationship_ArtworkMaster
		TYPE_SUB_BRAND = AWLType.SUB_BRAND.get(context);//type_Subbrand
		TYPE_PRODUCTTYPE = PropertyUtil.getSchemaProperty(context, "type_ProductType");
		ATTRIBUTE_COPY_TEXT_LANGUAGE = AWLAttribute.COPY_TEXT_LANGUAGE.get(context);
		SELECT_ATTRIBUTE_COPY_TEXT_LANGUAGE = AWLUtil.strcat(ATTRIBUTE, ATTRIBUTE_COPY_TEXT_LANGUAGE ,"]");
		SELECT_GRAPHIC_DOC_ID = AWLUtil.strcat("from[", AWLRel.GRAPHIC_DOCUMENT.get(context), "].to.", DomainObject.SELECT_ID);
		STR_JSON_ORDER_KEYS_MASTER = AWLUtil.strcat(ENOVIA_NAME, STR_S_PIPE, 
				COPY_ELEMENT_TYPE_CODE, STR_S_PIPE, MARKETING_NAME, STR_S_PIPE,
				ENOVIA_REVISION, STR_S_PIPE, ENOVIA_STATE); //Order for Master copy keys in JSON
		STR_JSON_ORDER_KEYS_LOCAL = AWLUtil.strcat(ENOVIA_NAME, STR_S_PIPE, 
				COPY_ELEMENT_TYPE_CODE, STR_S_PIPE, MARKETING_NAME, STR_S_PIPE,
				IS_BASE_COPY, STR_S_PIPE, IMAGE_LOCATION_FOR_MAP, STR_S_PIPE, TEXT_CONTENT, 
				STR_S_PIPE, LANGUAGE, STR_S_PIPE, ENOVIA_REVISION, STR_S_PIPE, ENOVIA_STATE);//Order for Local copy keys in JSON
		//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
		
		logger.info("Inside setPropertyValues() method --- ends");
	}

	private String generateCopyXML(Context context, String[] args) throws Exception {
		logger.info("Inside generateCopyXML() method --- generating copy xml starts");
		StringBuffer copyXMLString = new StringBuffer();
		if (args != null && args.length > 1) {
			HashMap<String, String> hmRequest = new HashMap();
			hmRequest.put(POA_ID, args[0]);
			hmRequest.put(POA_NAME, args[1]);
			String[] methodargs = JPO.packArgs(hmRequest);
		
			String xmlString = (String) JPO.invoke(context, "AWLArtworkAssemblyExport", new String[] {},
					"createArtworkAssemblyXMLFormat", methodargs, String.class);
			
			copyXMLString.append(xmlString.trim());
		}
		logger.info("Inside generateCopyXML() method --- generating copy xml ends");
		return copyXMLString.toString();
	}

	/**
	 * Generate copy xml
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private String generateProjectXML(Context context, String[] args) throws Exception {
		logger.info("Inside generateProjectXML() method --- generating project xml starts");
		StringBuffer projectXMLString = new StringBuffer();


		try {
			if (args != null && args.length > 1) {
				String strProjectState = DomainConstants.EMPTY_STRING;
				String strFinalXML = null;
				String strProjectCreationDate = DomainConstants.EMPTY_STRING;
				String strProjectId = args[0];
				String poaName = args[1];
				String supplierName = args[2];
				
				String strProjectLastBVEDate = DomainConstants.EMPTY_STRING;
				StringList objectSelects = new StringList(DomainConstants.SELECT_CURRENT);
				objectSelects.add(ATTRIBUTE + ATTRIBUTE_BVE_MOD_DATE + "]");
				objectSelects.add(DomainConstants.SELECT_ORIGINATED);
				objectSelects.add(ATTRIBUTE + ATTRIBUTE_PG_PKG_PROJECT_ID + "]");
				DomainObject projectObject = DomainObject.newInstance(context);
				projectObject.setId(strProjectId);
				Map infoMap = projectObject.getInfo(context, objectSelects);
				
				if (infoMap != null) {
					strProjectState = (String) infoMap.get("current");
					strProjectLastBVEDate = (String) infoMap.get(ATTRIBUTE + ATTRIBUTE_BVE_DATE + "]");
					strProjectCreationDate = (String) infoMap.get("originated");
				}
				
				StringList strlObjectSelectable = new StringList(1);
				strlObjectSelectable.add(DomainObject.SELECT_ID);
				Pattern includeType = new Pattern(ArtworkConstants.TYPE_ASSEMBLY_TASK);
				StringList slTaskSelectable = new StringList(4);
				slTaskSelectable.add(DomainObject.SELECT_NAME);
				slTaskSelectable.add(RELATIONSHIP + DomainConstants.RELATIONSHIP_EMPLOYEE + FROM_ID);
				slTaskSelectable.add(RELATIONSHIP + DomainConstants.RELATIONSHIP_EMPLOYEE + "].from.name");
				slTaskSelectable.add(RELATIONSHIP + DomainConstants.RELATIONSHIP_EMPLOYEE + "].from.attribute[]");
				StringList slSelectables = new StringList(3);
				slSelectables.add(DomainObject.SELECT_NAME);
				slSelectables.add(DomainObject.SELECT_ID);
				slSelectables.add("to[" + SUPPLIER_NAME + FROM_ID);
				slSelectables.add("to[" + SUPPLIER_NAME + "].from.name");
				
				ContextUtil.pushContext(context);
				MapList mlTask = projectObject.getRelatedObjects(context, DomainConstants.RELATIONSHIP_SUBTASK,
						ProgramCentralConstants.TYPE_TASK_MANAGEMENT, strlObjectSelectable, null, false, true, (short) 0,
						DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, includeType, null, null);
				ContextUtil.popContext(context);
				
				int taskSize = mlTask.size();
				String strTaskId = DomainConstants.EMPTY_STRING;
				Map mTask = null;
				MapList mlPersonList = null;
				MapList mlPOA = null;
				DomainObject domAssembleTask = null;
				int persSize = 0;
				int poaSize = 0;
				Map mPersonList = null;
				String strPersonName = DomainConstants.EMPTY_STRING;
				String strCompanyId = DomainConstants.EMPTY_STRING;
				String strAssignedPath = DomainConstants.EMPTY_STRING;
				String strCompanyName = DomainConstants.EMPTY_STRING;
				Map mPOA = null;
				String strPOAId = DomainConstants.EMPTY_STRING;
				String strPOAName = DomainConstants.EMPTY_STRING;
				String strSuppId = DomainConstants.EMPTY_STRING;
				StringList slPOAList = new StringList();
				HashMap hmAssignedPOA = new HashMap();
				HashMap hmPersonAssign = new HashMap();
				StringList slAssignedKeyList = new StringList();
				StringList slAssignedPathList = new StringList();
				StringList slAssignedName = new StringList();
				StringList slAssignedComp = new StringList();
				StringList slPOAListNoAS2 = new StringList();
				String strPOAIds = DomainConstants.EMPTY_STRING;
				String strPersonAssignee = DomainConstants.EMPTY_STRING;
				String PROJECT_HOLD_CANCEL_HOLD = ProgramCentralConstants.STATE_PROJECT_SPACE_HOLD_CANCEL_HOLD;
				String strSuppName="";
				String strPOASupplierName ="";
				Map mPOA1 = new HashMap();
				
				for (int i = 0; i < taskSize; i++) {
					mTask = (Map) mlTask.get(i);
					strTaskId = (String) mTask.get(DomainObject.SELECT_ID);
					domAssembleTask = DomainObject.newInstance(context);
					domAssembleTask.setId(strTaskId);

					ContextUtil.pushContext(context);					
					mlPersonList = domAssembleTask.getRelatedObjects(context, DomainConstants.RELATIONSHIP_ASSIGNED_TASKS,
							DomainConstants.TYPE_PERSON, slTaskSelectable, null, true, false, (short) 1,
							DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					
					mlPOA = domAssembleTask.getRelatedObjects(context, DomainConstants.RELATIONSHIP_TASK_DELIVERABLE,
							(AWLType.POA.get(context)), slSelectables, null, false, true, (short) 1,
							DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					
					ContextUtil.popContext(context);
					
					persSize = mlPersonList.size();
					poaSize = mlPOA.size();
					
					if(poaSize>0) {
						mPOA1 = (Map) mlPOA.get(0);
						strPOAId = (String) mPOA1.get(DomainObject.SELECT_ID);
						strPOAName = (String) mPOA1.get(DomainObject.SELECT_NAME);
						strPOASupplierName = (String) mPOA1.get("to[" + SUPPLIER_NAME + "].from.name");
											

						if(UIUtil.isNotNullAndNotEmpty(strPOASupplierName) && strPOASupplierName.equals(supplierName)) {										
							for (int j = 0; j < persSize; j++) {
								mPersonList = (Map) mlPersonList.get(j);
								strPersonName = (String) mPersonList.get(DomainObject.SELECT_NAME);
								strCompanyId = (String) mPersonList
										.get(RELATIONSHIP + DomainConstants.RELATIONSHIP_EMPLOYEE + FROM_ID);
								if (strCompanyId != null && !DomainConstants.EMPTY_STRING.equals(strCompanyId)) {														
									strCompanyName = (String) mPersonList
											.get("relationship[" + DomainConstants.RELATIONSHIP_EMPLOYEE + "].from.name");
									if(!supplierName.contains(strCompanyName)) {
										continue;
									}
								}
								
								if (poaSize > 0) {
									for (int k = 0; k < poaSize; k++) {
										mPOA = (Map) mlPOA.get(k);
										strPOAId = (String) mPOA.get(DomainObject.SELECT_ID);
										strPOAName = (String) mPOA.get(DomainObject.SELECT_NAME);
										strSuppId = (String) mPOA.get("to[" + SUPPLIER_NAME + "].from.id");

										strSuppName = (String) mPOA.get("to[" + SUPPLIER_NAME + "].from.name");
										strAssignedPath = (String) mPOA
												.get("to[" + SUPPLIER_NAME + "].from.attribute[" + ATTRIBUTE_PGCIB_AS2_PATH + "]");
										
										if (BusinessUtil.isNotNullOrEmpty(strSuppId)) {									
											if (BusinessUtil.isNotNullOrEmpty(strSuppName) && BusinessUtil.isNotNullOrEmpty(strCompanyName)
													&& !strSuppName.contains(strCompanyName)) {
												slPOAList.add(strPOAName);
											} else {
												if (hmAssignedPOA.containsKey(strSuppId)) {
													hmAssignedPOA.put(strSuppId, hmAssignedPOA.get(strSuppId) + "," + strPOAId);
													hmPersonAssign.put(strSuppId, hmPersonAssign.get(strSuppId) + "," + strPersonName);

												} else {
													hmAssignedPOA.put(strSuppId, strPOAId);
													hmPersonAssign.put(strSuppId, strPersonName);
													slAssignedKeyList.add(strSuppId);
													slAssignedPathList.add(strAssignedPath);
													slAssignedName.add(strPersonName);
													slAssignedComp.add(strCompanyName);
												}
											}

										}
										
										if ("".equals(strAssignedPath)) {
											slPOAListNoAS2.add(strPOAName);
										}

									}
								}
							}

						}
					}
				}
			
				for (int l = 0; l < slAssignedKeyList.size(); l++) {
					strAssignedPath = (String) slAssignedPathList.get(l);
					strPersonName = (String) slAssignedName.get(l);
					strPOAIds = (String) hmAssignedPOA.get((String) slAssignedKeyList.get(l));
					if (!"null".equals(strPOAIds) && !DomainConstants.EMPTY_STRING.equals(strPOAIds)) {
						strPersonAssignee = (String) hmPersonAssign.get((String) slAssignedKeyList.get(l));
						if (BusinessUtil.isNotNullOrEmpty(strProjectState)
								&& (strProjectState.equalsIgnoreCase(PROJECT_HOLD_CANCEL_HOLD))) {
							strFinalXML = (String) JPO.invoke(context, "pgAAA_Util", new String[] {},
									"printStateIfContentBlank",
									new String[] { strProjectState, strProjectLastBVEDate, strProjectId }, String.class);
							projectXMLString.append(strFinalXML);
						} else {
							strFinalXML = (String) JPO.invoke(context, "pgAAA_Util", new String[] {},
									"getProjectRelatedDeliverables", new String[] { strPersonName, strPOAIds,
											strProjectCreationDate, strProjectId, strPersonAssignee },
									String.class);
							projectXMLString.append(strFinalXML);
						}
					}
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("Inside generateProjectXML() method --- generating project xml ends");		
		return projectXMLString.toString();
	}

	/**
	 * Generate cover xml
	 * 
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private String generateCoverXML(Context context, String[] args) throws Exception {
		logger.info("Inside generateCoverXML() method --- generating cover xml starts");
		StringBuffer coverXMLString = new StringBuffer();
		if (args != null && args.length > 0) {
			StringList slPOASelectables = new StringList(28);
			slPOASelectables.add(DomainObject.SELECT_NAME);
			// Added by RTA Capgemini Offshore for 18x.7 Req 36202  - Starts
			slPOASelectables.add(DomainObject.SELECT_DESCRIPTION);
			// Added by RTA Capgemini Offshore for 18x.7 Req 36202  - Ends
			slPOASelectables.add("to[" + RELATIONSHIP_SUPPLIER_POA + "].from.name");
			slPOASelectables.add("to[" + RELATIONSHIP_SUPPLIER_POA + FROM_ID);
			slPOASelectables.add("to[" + RELATIONSHIP_ARTWORK_PACKAGE_CONTENT + "].from.name");
			slPOASelectables.add("from[" + RELATIONSHIP_POA_TO_ARTWORK_ASSIGNEE + "].to.id");
			slPOASelectables.add("from[" + RELATIONSHIP_POA_TO_ARTWORK_ASSIGNEE + "].to.name");
			slPOASelectables.add(ATTRIBUTE + sAttrOldIPMS + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrCustSpec + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrFPC + "]");
			//Added by Sogeti Offshore for 18x.5 Req #35381 STARTS
			slPOASelectables.add(ATTRIBUTE + sAttrFPCDescription + "]");
			//Added by Sogeti Offshore for 18x.5 Req #35381 ENDS
			slPOASelectables.add(ATTRIBUTE + sAttrPackLevel + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrGTIN + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrSuppGTIN + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrCaseCount + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrMarket + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrAddlDesc + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrIPMS + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrMatrlCat + "]");
			slPOASelectables.add(ATTRIBUTE + sAttrCompType + "]");
			slPOASelectables.add("from[" + RELATIONSHIP_POA_CASE_TYPE + "].to.name");
			slPOASelectables.add("from[" + RELATIONSHIP_PRINTING_PROCESS + "].to.name");
			slPOASelectables.add("to[" + RELATIONSHIP_ASSOCIATED_POA + FROM_ID);
			slPOASelectables.add(ATTRIBUTE + "attribute_pgAAA_CopyXMLVersion" + "]");
			slPOASelectables.add("to[" + RELATIONSHIP_PGAAA_PROJECTTOPOA + "].from.name");
			slPOASelectables.add(
					"to[" + RELATIONSHIP_PGAAA_PROJECTTOPOA + "].from[" + DomainObject.TYPE_PROJECT_SPACE + "].id");
			slPOASelectables.add(DomainObject.SELECT_ID);
			slPOASelectables.add(DomainConstants.SELECT_REVISION);
			DomainObject poaObject = DomainObject.newInstance(context);
			poaObject.setId(args[0]);
			Map mpPOADetails = poaObject.getInfo(context, slPOASelectables);
			String artworkPackageName = (String) mpPOADetails
					.get("to[" + RELATIONSHIP_ARTWORK_PACKAGE_CONTENT + "].from.name");
			String strArtworkAssigneeId = (String) mpPOADetails
					.get("from[" + RELATIONSHIP_POA_TO_ARTWORK_ASSIGNEE + "].to.id");
			HashMap mpSupplierDetails = new HashMap();
			if (!DomainConstants.EMPTY_STRING.equalsIgnoreCase(strArtworkAssigneeId) && null != strArtworkAssigneeId
					&& !"null".equalsIgnoreCase(strArtworkAssigneeId)) {
				Person person = new Person(strArtworkAssigneeId);
				String strAssigneeCompanyId = person.getCompanyId(context);
				String strAssigneeCompanyName = person.getCompany(context).getName();
				mpSupplierDetails.put("AssigneeCompanyId", strAssigneeCompanyId);
				mpSupplierDetails.put("AssigneeCompanyName", strAssigneeCompanyName);
			}
			String workSpacePath = context.createWorkspace();
			mpSupplierDetails.put("SupplierPath", workSpacePath);
			MapList argsMapList = new MapList();
			HashMap hmRequest = getHashMapvalues(mpSupplierDetails, mpPOADetails);
			argsMapList.add(hmRequest);
			String[] methodargs = JPO.packArgs(argsMapList);
			BufferedReader buf = null;
			try {
				//Added by Sogeti Offshore for 2018x.3 Defect #33451 STARTS
				String STR_OUTBOUND_ARCHIVE = EnoviaResourceBundle.getProperty(context, "emxAWL.Supplier.Folder.outbound_Archive");
				String STR_OUTBOUND = EnoviaResourceBundle.getProperty(context, "emxAWL.Supplier.Folder.outbound");
				String STR_OUTBOUND_INPROCESS = EnoviaResourceBundle.getProperty(context, "emxAWL.Supplier.Folder.outbound_Inprocess");
				File outbound_InProcess = new File(DomainConstants.EMPTY_STRING);
				//Added by Sogeti Offshore for 2018x.3 Defect #33451 ENDS
				File folder = new File(workSpacePath);
				File outbound = new File(DomainConstants.EMPTY_STRING);
				File outbound_Archive = new File(DomainConstants.EMPTY_STRING);
				if (folder.isDirectory()) {
					String outBoundPath = workSpacePath + SEPARATOR + STR_OUTBOUND;
					outbound = new File(outBoundPath);
					String outBoundArchivePath = workSpacePath + SEPARATOR + STR_OUTBOUND_ARCHIVE;
					outbound_Archive = new File(outBoundArchivePath);
					//Added by Sogeti Offshore for 2018x.3 Defect #33451 STARTS
					String outBoundInProcessPath = workSpacePath + SEPARATOR + STR_OUTBOUND_INPROCESS;
					outbound_InProcess = new File(outBoundInProcessPath);
					outbound_InProcess.mkdir();
					//Added by Sogeti Offshore for 2018x.3 Defect #33451 ENDS
					outbound.mkdir();
					outbound_Archive.mkdir();
				}
				try {
					JPO.invoke(context, "pgRTA_Util", null, "createArtworkXML", methodargs, Void.class);
				} catch (Exception exp) {
					throw exp;
				}
				if (BusinessUtil.isNotNullOrEmpty(artworkPackageName)) {
					File[] listOfFiles = outbound.listFiles();
					String documentName = DomainConstants.EMPTY_STRING;
					String fileName = DomainConstants.EMPTY_STRING;
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							fileName = listOfFiles[i].getName();
							if (fileName.startsWith(artworkPackageName)) {
								documentName = fileName;
								break;
							}

						}
					}
					if (BusinessUtil.isNotNullOrEmpty(documentName)) {
						File coverXML = new File(outbound + SEPARATOR + documentName);
						if (coverXML.exists()) {
							buf = new BufferedReader(new FileReader(coverXML));
							String line = DomainConstants.EMPTY_STRING;
							while ((line = buf.readLine()) != null) {
								coverXMLString.append(line);
							}
							coverXML.delete();
							coverXML = new File(outbound_Archive + SEPARATOR + documentName);
							//Added by Sogeti Offshore for 2018x.3 Defect #33451 STARTS
							if(coverXML.exists()) {
								coverXML.delete();
							}
							coverXML = new File(outbound_InProcess + SEPARATOR + documentName);
							if(coverXML.exists()) {
								coverXML.delete();
							}
							outbound_InProcess.delete();
							//Added by Sogeti Offshore for 2018x.3 Defect #33451 ENDS
							outbound.delete();
							outbound_Archive.delete();
						}
					}
				}
			} catch (Exception e) {
				logger.info("Exception :: " + e);
				e.printStackTrace();
				//Added by Sogeti Offshore for 2018x.3 Defect #33451 STARTS
				throw e;
				//Added by Sogeti Offshore for 2018x.3 Defect #33451 ENDS
			} finally {
				buf.close();
			}
		}
		logger.info("Inside generateCoverXML() method --- generating cover xml ends");
		return coverXMLString.toString();
	}

	/**
	 * Get the Hashmap values
	 * 
	 * @param mpSupplierDetails
	 * @param mpPOADetails
	 * @return
	 */
	private HashMap getHashMapvalues(Map mpSupplierDetails, Map mpPOADetails) {
		logger.info("Inside getHashMapvalues() method --- starts");
		HashMap hmRequest = new HashMap(12);
		String strSupplierName = (String) mpPOADetails.get("to[" + RELATIONSHIP_SUPPLIER_POA + "].from.name");
		String strSupplierId = (String) mpPOADetails.get("to[" + RELATIONSHIP_SUPPLIER_POA + FROM_ID);
		String strPOAArtworkAssignee = (String) mpPOADetails
				.get("from[" + RELATIONSHIP_POA_TO_ARTWORK_ASSIGNEE + "].to.name");
		String strArtworkPackageName = (String) mpPOADetails
				.get("to[" + RELATIONSHIP_ARTWORK_PACKAGE_CONTENT + "].from.name");
		String strOldIPMS = (String) mpPOADetails.get(ATTRIBUTE + sAttrOldIPMS + "]");
		String strCustSpec = (String) mpPOADetails.get(ATTRIBUTE + sAttrCustSpec + "]");
		String strFPC = (String) mpPOADetails.get(ATTRIBUTE + sAttrFPC + "]");
		//Added by Sogeti Offshore for 18x.5 Req #35381 STARTS
		String strFPCDescription = (String) mpPOADetails.get(ATTRIBUTE + sAttrFPCDescription + "]");
		//Added by Sogeti Offshore for 18x.5 Req #35381 ENDS
		String strPackLevel = (String) mpPOADetails.get(ATTRIBUTE + sAttrPackLevel + "]");
		String strGTIN = (String) mpPOADetails.get(ATTRIBUTE + sAttrGTIN + "]");
		String strSuppGTIN = (String) mpPOADetails.get(ATTRIBUTE + sAttrSuppGTIN + "]");
		String strCaseCount = (String) mpPOADetails.get(ATTRIBUTE + sAttrCaseCount + "]");
		String strAttrMarket = (String) mpPOADetails.get(ATTRIBUTE + sAttrMarket + "]");
		String strAttrAddlDesc = (String) mpPOADetails.get(ATTRIBUTE + sAttrAddlDesc + "]");
		String strIPMS = (String) mpPOADetails.get(ATTRIBUTE + sAttrIPMS + "]");
		String strMatrlCat = (String) mpPOADetails.get(ATTRIBUTE + sAttrMatrlCat + "]");
		String strComponentType = (String) mpPOADetails.get(ATTRIBUTE + sAttrCompType + "]");
		String strCaseType = (String) mpPOADetails.get("from[" + RELATIONSHIP_POA_CASE_TYPE + "].to.name");
		String strPrintProcess = (String) mpPOADetails.get("from[" + RELATIONSHIP_PRINTING_PROCESS + "].to.name");
		String strAssociatedPOA = (String) mpPOADetails.get("to[" + RELATIONSHIP_ASSOCIATED_POA + FROM_ID);
		String strPOAName = (String) mpPOADetails.get(DomainObject.SELECT_NAME);
		String strPOAId = (String) mpPOADetails.get(DomainObject.SELECT_ID);
		String strSupplierPath = (String) mpSupplierDetails.get("SupplierPath");
		String strAssigneeCompanyId = (String) mpSupplierDetails.get("AssigneeCompanyId");
		String strAssigneeCompanyName = (String) mpSupplierDetails.get("AssigneeCompanyName");
		// Added by RTA Capgemini Offshore for 18x.6 OCT_CW Req 36202  - Starts
		String strDescription = (String)mpPOADetails.get(DomainConstants.SELECT_DESCRIPTION);
		
		// Added by RTA Capgemini Offshore for 18x.6 OCT_CW Req 36202  - Ends

		hmRequest.put("workspacePath", strSupplierPath);
		hmRequest.put("folderPath", strSupplierPath + SEPARATOR);
		hmRequest.put("selectedPOAList", new StringList(strPOAId));
		hmRequest.put("AFAassignee", strSupplierPath);
		hmRequest.put("AFAassigneeName", strSupplierName);
		hmRequest.put("POAName", strPOAName);
		hmRequest.put("APName", strArtworkPackageName);
		hmRequest.put("POAArtworkAssigneeName", strPOAArtworkAssignee);
		hmRequest.put("POAAWAssigneeCompanyName", strAssigneeCompanyName);
		hmRequest.put("POAAWAssigneeCompanyId", strAssigneeCompanyId);
		hmRequest.put("POASupplierID", strSupplierId);
		hmRequest.put("OldIPMS", strOldIPMS);
		hmRequest.put("CustSpec", strCustSpec);
		hmRequest.put("FPC", strFPC);
		//Added by Sogeti Offshore for 18x.5 Req #35381 STARTS
		hmRequest.put("FPCDescription", strFPCDescription);
		//Added by Sogeti Offshore for 18x.5 Req #35381 ENDS
		hmRequest.put("PackageLevel", strPackLevel);
		hmRequest.put("GTIN", strGTIN);
		hmRequest.put("SuppressedGTIN", strSuppGTIN);
		hmRequest.put("CaseCount", strCaseCount);
		hmRequest.put("CaseType", strCaseType);
		hmRequest.put("Market", strAttrMarket);
		hmRequest.put("AddlDesc", strAttrAddlDesc);
		hmRequest.put("IPMS", strIPMS);
		hmRequest.put("MaterialCategory", strMatrlCat);
		hmRequest.put("PrintingProcess", strPrintProcess);
		hmRequest.put("ComponentType", strComponentType);
		hmRequest.put("AssociatedPOA", strAssociatedPOA);
		// Added by RTA Capgemini Offshore for 18x.6 OCT_CW Req 36202  - Start
		hmRequest.put(DomainConstants.SELECT_DESCRIPTION, strDescription);
		// Added by RTA Capgemini Offshore for 18x.6 OCT_CW Req 36202  - Ends
		
		logger.info("Inside getHashMapvalues() method --- ends");
		return hmRequest;
	}

	/**
	 * Write the Xml String to a File
	 * 
	 * @param workSpacePath
	 * @param xmlFileSource
	 * @param xmlFileString
	 * @throws Exception
	 */
	private void writeToXML(String workSpacePath, String xmlFileSource, String xmlFileString) throws Exception {
		logger.info("Inside writeToXML() method --- writing xml starts");
		String filePath = workSpacePath + SEPARATOR + xmlFileString;
		File file = new File(filePath);
		file.createNewFile();
		FileWriter fstream;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(file);
			out = new BufferedWriter(fstream);
			out.write(xmlFileSource);
			//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
			bFileCreationSuccess = true;
			//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
		} catch (Exception e) {
			logger.info("Exception :: " + e);
		} finally {
			out.flush();
			out.close();
		}

		logger.info("Inside writeToXML() method --- writing xml ends");
	}

	/**
	 * Generating zip for the generated cover/project and copy xml
	 * 
	 * @param sourceDirectoryPath
	 * @param zipPath
	 * @return
	 * @throws IOException
	 */
	private byte[] zipDirectory(String sourceDirectoryPath, String zipPath) throws IOException {
		logger.info("Inside zipDirectory() method --- creating zip directory starts");
		Path zipFilePath = Files.createFile(Paths.get(zipPath));

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zipFilePath))) {
			Path sourceDirPath = Paths.get(sourceDirectoryPath);
			
			//Modified by RTA Sogeti offshore for 2018x.5 Req-33381 - Starts
			Files.walk(sourceDirPath).filter(path -> !(Files.isDirectory(path) || zipPath.endsWith(path.getFileName().toString()))).forEach(path -> {
			//Modified by RTA Sogeti offshore for 2018x.5 Req-33381 - Ends
				ZipEntry zipEntry = new ZipEntry(sourceDirPath.relativize(path).toString());
				try {
					zipOutputStream.putNextEntry(zipEntry);
					zipOutputStream.write(Files.readAllBytes(path));
					zipOutputStream.closeEntry();
				} catch (Exception e) {
					logger.info("Exception :: " + e);
				}
			});
			zipOutputStream.close();			
			logger.info("Inside zipDirectory() method --- creating zip directory ends");
			return java.nio.file.Files.readAllBytes(zipFilePath);
		}

	}
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * Start point for AAL extension, gets the request parameter and sets in bean and processing further steps.
	 * @param ceRequestData, bean object.
	 * @return
	 * @throws Exception
	 */
	@POST
	@javax.ws.rs.Path("/getCEDetails")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCEDetails(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, CERequestData ceRequestData) throws Exception {
		logger.info("Inside getCEDetails() method --- starts");
		CEResponseData responseData = null;
		boolean isContextPushed = false;
		Context context = null;
		String strLocale = STR_DEFAULT_LOCALE_EN;
		try {
			/*
			 * Check if POA exist in System and if it is connected to a project then
			 * generate copy and cover/project xml
			 */
			logger.info("Request Parameters are:" + ceRequestData);
			context = getContext(paramHttpServletRequest);
			if(context!=null) {
				i18nObject = new i18nNow();
				strLocale = context.getLocale().toString();
				String webServiceUser = i18nObject.GetString(STRING_RESOURCE_EMX_CPN, strLocale, STRING_RESOURCE_KEY_APP_USER);
				String strUser = context.getUser();
				if(!webServiceUser.equals(strUser)) {
					ContextUtil.pushContext(context, webServiceUser, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
					isContextPushed = true;
				}
				responseData = findCEs(context, ceRequestData);
			} else {
				responseData = new CEResponseData(STR_401ERROR_KEY, STR_INVALID_SESSION, true);
			}
		} catch (Exception e) {
			logger.info("Got an exception. " + e);
			responseData = new CEResponseData(STR_500ERROR_KEY, STR_500ERROR_MESSAGE, true);
			e.printStackTrace();
		} finally {
			if(context != null && isContextPushed){
				ContextUtil.popContext(context);
			}
			if(context != null && context.isConnected()){
				context.shutdown();
				logger.info("SuccessFully Disconnected!!!");
			}
			
		}
		logger.info("Inside getCEDetails() method --- ends");
		return Response.ok(responseData).build();
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * Get the CEs from MCs and generate the report
	 * 
	 * @param ceRequestData
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private CEResponseData findCEs(Context context, CERequestData ceRequestData) throws Exception {
		logger.info("Inside findCEs() method --- Starts");
		CEResponseData response = null;
		String strMarketingName = null;
		String strGS1Type = null;
		String strCopyContent = null;
		String strCopyContentMatch = null;
		String strCopyElementId = null;
		String strBrandName = null;
		String strRegion = null;
		boolean bExactMatch = false;
		StringBuilder sbWhereClause = new StringBuilder(DomainConstants.EMPTY_STRING);
		StringBuilder sbMessage = new StringBuilder();
		StringBuilder sbErrorMessage = new StringBuilder();
		String strEnoviaType = null;
		String strEnoviaRevision = null;
		String strEnoviaName = null;
		StringList slSelectables = null;
		MapList mlCopyElementInfo = new MapList();
		int iSize = 0;
		JSONObject jsonFinal = new JSONObject(Order.INSERTION);
		boolean bReqBTRC = false;
		String workSpacePath =null;
		StringList slListOfString= new StringList();
		MapList mlBrandRegionInfo = null;
		StringList slBrdSelects = new StringList();
		String strRegionSelect= null;
		String timeStamp= null;
		String strZipfileName= null;
		Map<String, String> mResponseMap= null;
		String strWinBase64String= null;
		String strFileCreated= null;
		boolean bExpand = true; 
		String strLocale = STR_DEFAULT_LOCALE_EN;
		try{
			strLocale = context.getLocale().toString();
			setPropertyValues(context);
			
			strCopyElementId = ceRequestData.getCopyId();
			strMarketingName = ceRequestData.getMarketingName();
			strGS1Type = ceRequestData.getGs1Type();
			strRegion = ceRequestData.getRegion();
			strBrandName = ceRequestData.getBrandName();
			strCopyContent = ceRequestData.getCopyContent();
			strCopyContentMatch = ceRequestData.getCopyContentMatch();
			if(BusinessUtil.isNotNullOrEmpty(strCopyElementId)){
				strEnoviaType = DomainConstants.QUERY_WILDCARD;
				strEnoviaName = strCopyElementId;
				strEnoviaRevision = DomainConstants.QUERY_WILDCARD;
				sbWhereClause.append(DomainObject.SELECT_REVISION).append(STR_COND_EQUAL_TO).append(AWLConstants.LAST);
			} else if(BusinessUtil.isNotNullOrEmpty(strMarketingName)){
				strEnoviaType = TYPE_MASTERARTWORKELEMENT;
				strEnoviaName = DomainConstants.QUERY_WILDCARD;;
				strEnoviaRevision = DomainConstants.QUERY_WILDCARD;
				strMarketingName = strMarketingName.replaceAll(STR_REGX_QUOTE_REPLACER, STR_REGX_QUOTE_REPLACEMENT);
				sbWhereClause.append(STR_ESCAPE_KEY).append(STR_S_SPACE).append(SELECT_ATTRIBUTE_MARKETINGNAME).append(STR_COND_EQUAL_TO).append(STR_S_QUOTE).append(strMarketingName).append(STR_S_QUOTE);
				sbWhereClause.append(STR_COND_AND).append(DomainObject.SELECT_REVISION).append(STR_COND_EQUAL_TO).append(AWLConstants.LAST);
			} else if(BusinessUtil.isNotNullOrEmpty(strBrandName) && BusinessUtil.isNotNullOrEmpty(strGS1Type) 
					&& BusinessUtil.isNotNullOrEmpty(strRegion) && (BusinessUtil.isNotNullOrEmpty(strCopyContent) ||
							BusinessUtil.isNotNullOrEmpty(strCopyContentMatch))){
				strEnoviaType = ProgramCentralConstants.TYPE_PRODUCTLINE;
				strEnoviaName = DomainConstants.QUERY_WILDCARD;
				strEnoviaRevision = STR_S_HYPEN;
				 
				sbWhereClause.append(SELECT_ATTRIBUTE_MARKETINGNAME).append(STR_COND_EQUAL_TO);
				sbWhereClause.append(STR_S_QUOTE).append(strBrandName).append(STR_S_QUOTE);
				sbWhereClause.append(STR_COND_AND);
				sbWhereClause.append("from[").append(ProductLineConstants.RELATIONSHIP_SUB_PRODUCT_LINES);
				sbWhereClause.append(STR_CLOSE_DOT_TO_OPEN).append(TYPE_PRODUCTTYPE).append(STR_CLOSE_DOT).append(SELECT_ATTRIBUTE_MARKETINGNAME);
				sbWhereClause.append("~~~").append(STR_S_QUOTE).append(strRegion).append(STR_S_QUOTE);
				
				if(BusinessUtil.isNotNullOrEmpty(strCopyContentMatch)){
					if(strCopyContentMatch.equals(DomainConstants.QUERY_WILDCARD)) {
						sbErrorMessage.append(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.CopyContentMatchError"));
						response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.501Error"), sbErrorMessage.toString(), true);
						return response;
					}
					bExactMatch = true;
				} else if(BusinessUtil.isNotNullOrEmpty(strCopyContent)){
					bExactMatch = false;
				}
				bReqBTRC = true;
			} else {
				sbErrorMessage.append(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.MandatoryFieldsError"));
				response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.501Error"), sbErrorMessage.toString(), true); 
				return response;
			}
			workSpacePath = context.createWorkspace();
			String strTempFolder = AWLUtil.strcat(STR_TEMP, STR_UNDERSCORE, System.currentTimeMillis());
			File fileTempWorkspace = new File(AWLUtil.strcat(workSpacePath, SEPARATOR, strTempFolder));
			if(!fileTempWorkspace.exists()){
				fileTempWorkspace.mkdir();
				workSpacePath = fileTempWorkspace.toString();
			}
			slListOfString.addAll(Arrays.asList(strEnoviaType,strEnoviaName,strEnoviaRevision,strCopyContent,strCopyContentMatch,strGS1Type, String.valueOf(bExactMatch)));
			slSelectables = new StringList();
			getSelectables(context, slSelectables, false);
			if(!bReqBTRC) {
				getSelectables(context, slSelectables, true);
				mlCopyElementInfo = DomainObject.findObjects(context, 
						strEnoviaType, 
						strEnoviaName, 
						strEnoviaRevision, 
						DomainConstants.QUERY_WILDCARD, 
						pgV3Constants.VAULT_ESERVICEPRODUCTION, 
						sbWhereClause.toString(), 
						DomainConstants.EMPTY_STRING, 
						bExpand, 
						slSelectables, 
						(short)0);
			} else {
				strRegionSelect = AWLUtil.strcat("from[", ProductLineConstants.RELATIONSHIP_SUB_PRODUCT_LINES, 
						STR_CLOSE_DOT_TO_OPEN, TYPE_PRODUCTTYPE, STR_S_PIPE, SELECT_ATTRIBUTE_MARKETINGNAME, STR_COND_EQUAL_TO, STR_S_QUOTE, 
						strRegion, STR_S_QUOTE, "].id");
				slBrdSelects.add(strRegionSelect);
				mlBrandRegionInfo = DomainObject.findObjects(context, 
						strEnoviaType, 
						strEnoviaName, 
						strEnoviaRevision, 
						DomainConstants.QUERY_WILDCARD, 
						pgV3Constants.VAULT_ESERVICEPRODUCTION, 
						sbWhereClause.toString(), 
						DomainConstants.EMPTY_STRING, 
						bExpand, 
						slBrdSelects, 
						(short)0);
						
				searchByBrandRegionType(context, slListOfString, slSelectables, mlBrandRegionInfo, mlCopyElementInfo);
			}
			
			convertIntoJsonFormat(context, mlCopyElementInfo, slSelectables, jsonFinal, workSpacePath);
			
			iSize = mlCopyElementInfo.size();
			if(iSize > 0){
				
				timeStamp = new SimpleDateFormat(STR_DATE_FORMAT_YMDHS).format(new Date());
			    strZipfileName = AWLUtil.strcat(JSON_FILE_NAME, STR_UNDERSCORE, timeStamp);
				mResponseMap = generateBase64Code(context, jsonFinal, strZipfileName, JSON_FILE_NAME ,workSpacePath);
				strWinBase64String = mResponseMap.get(STR_ENCODE_BASE64);
				strFileCreated = mResponseMap.get(STR_FILE_CREATED);
				if(BusinessUtil.isNotNullOrEmpty(strFileCreated) && AWLConstants.RANGE_TRUE.equalsIgnoreCase(strFileCreated) 
						&& BusinessUtil.isNotNullOrEmpty(strWinBase64String)){
					sbMessage= new StringBuilder(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.DataFoundMsg"));
					response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.200ok"), strWinBase64String, strZipfileName, sbMessage.toString());
				}else{
					sbMessage = new StringBuilder(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.EnoviaError"));
					response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.500Error"), sbMessage.toString(), true);
				}
				
			} else {
				sbMessage = new StringBuilder(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.DataNotFoundMsg"));
				response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.404Error"), sbMessage.toString(), true);
			}
			
		}catch(Throwable th){
			String errorMsg = th.getMessage().trim();
			if (errorMsg.contains(NO_CONTEXT_USER) || errorMsg.contains(DOES_NOT_EXIST)) {
				errorMsg = i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.UserNotMatchingError");
				response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.404Error"), errorMsg, true);
			} else if (errorMsg.contains(AUTHORIZED) || errorMsg.contains(INVALID_PASSWORD)) {
				errorMsg = i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.UnAuthorizedUserError");
				response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.401Error"), errorMsg, true);
			} else if(errorMsg.contains(SERVER_HOST_NOT_FOUND)) {
				errorMsg = i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.ConnectionError");
				response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.404Error"), errorMsg, true);
			} else {
				sbMessage = new StringBuilder(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.EnoviaError"));
				response = new CEResponseData(i18nObject.GetString(EMXAWLSTRINGRESOURCE, strLocale, "Webservice.AALExtn.500Error"), sbMessage.toString(), true);
			}
			th.printStackTrace();
		}
		logger.info("Inside findCEs() method --- Ends");
		return response;
		
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * Used to get the common selectable for either MC or LC criteria
	 * @param context - User context
	 * @param slSelectables - Go on append the selectable as soon as it enters to this method
	 * @param bMaster - Specifies whether selectables need to pick from Master or Local elements
	 * @return
	 * @throws Exception
	 */
	private void getSelectables(Context context, StringList slSelectables, boolean bMaster) {
		// TODO Auto-generated method stub
		logger.info("Inside getSelectables() method --- Starts");
		String strMCSelectablePrefix = DomainObject.EMPTY_STRING;
		
		if(bMaster){
			slSelectables.add(SELECT_KINDOF_TYPE_MASTERARTWORKGRAPHICELEMENT);//kindof[Master Artwork Graphic Element]
			strMCSelectablePrefix = AWLUtil.strcat("to[", RELATIONSHIP_ARTWORK_ELEMENT_CONTENT, "].from.last.");
		}
		
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, DomainObject.SELECT_TYPE));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, DomainObject.SELECT_NAME));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, DomainObject.SELECT_REVISION));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, DomainObject.SELECT_ID));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, DomainObject.SELECT_CURRENT));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, SELECT_ATTRIBUTE_MARKETINGNAME));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, SELECT_ATTRIBUTE_COPY_TEXT));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, SELECT_KINDOF_TYPE_MASTERARTWORKGRAPHICELEMENT));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, SELECT_ATTRIBUTE_IS_BASE_COPY));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, SELECT_ATTRIBUTE_COPY_TEXT_LANGUAGE));
		slSelectables.add(AWLUtil.strcat(strMCSelectablePrefix, DomainObject.SELECT_IS_LAST));
		
		logger.info("Inside getSelectables() method --- Ends");
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * Used to append the master copy name for searchByBrand condition
	 * @param sRISType
	 * @return
	 * @throws Exception
	 */
	private String getRTACopyTypeFromRIS(String sRISType) throws Exception{
		logger.info("Inside getRTACopyTypeFromRIS() method --- Starts");
		String sRTAType = DomainObject.EMPTY_STRING;
		if(BusinessUtil.isNotNullOrEmpty(sRISType)) {
			final char[] delimiters =  { ' ', '_' };
			sRTAType =  WordUtils.capitalizeFully(sRISType, delimiters);
			sRTAType = sRTAType.replace(STR_UNDERSCORE, STR_S_SPACE);
			sRTAType = AWLUtil.strcat(sRTAType, STR_S_SPACE, STR_MASTER_COPY);
		}
		logger.info("Inside getRTACopyTypeFromRIS() method --- Ends");
		return sRTAType;
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS

	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * This method is used to convert the final data into json format
	 * @param context
	 * @param mlCopyElementInfo
	 * @param slListOfString
	 * @param slSelectables
	 * @param jsonFinal
	 * @param workSpacePath
	 * @throws Exception
	 */
	private void convertIntoJsonFormat(Context context, MapList mlCopyElementInfo, StringList slSelectables,JSONObject jsonFinal, String workSpacePath)throws Exception
    {
		logger.info("Inside convertIntoJsonFormat() method --- Starts");
    	int iSize = 0;
    	Map mapCopyElementInfo = null;
    	String strLastObjectId = null;
		String strCurrent = null;
		String strLanguage = null;
		String strResCopyContent = null;
		String strResMarketingName = null;
		Pattern relPattern = null;
		Pattern typePattern = null;
		DomainObject domObj = null;
		String strEnoviaType = null;
		String strEnoviaName = null;
		String strEnoviaRevision = null;
		String strMCSelectablePrefix = AWLUtil.strcat("to[", RELATIONSHIP_ARTWORK_ELEMENT_CONTENT, "].from.last.");
		JSONArray jsonLCArray = null;
		JSONObject jsonTempLCObject = null;
		JSONArray jsonTempFinalArray = null;
		JSONObject jsonTempMCObject = null;
		String strPrefix = null;
		String strLastId = null;
		MapList mlLCMap = null;
		Map<String, String> mTempMCObject = null;
		Map<String, String> mTempLCObject = null;
		iSize = mlCopyElementInfo.size();
		if(iSize > 0){
			slSelectables.add(SELECT_GRAPHIC_DOC_ID);
			for(int iMC=0; iMC<iSize; iMC++){
				strPrefix = DomainObject.EMPTY_STRING;
				mapCopyElementInfo = (Map<String, Object>)mlCopyElementInfo.get(iMC);
				strLastId = (String)mapCopyElementInfo.get(AWLUtil.strcat(strMCSelectablePrefix, DomainObject.SELECT_ID));
				if(BusinessUtil.isNotNullOrEmpty(strLastId)){
					strPrefix = strMCSelectablePrefix;
				}
				strEnoviaType= (String)mapCopyElementInfo.get(strPrefix + DomainObject.SELECT_TYPE);
				strEnoviaName = (String)mapCopyElementInfo.get(strPrefix + DomainObject.SELECT_NAME);
				strEnoviaRevision = (String)mapCopyElementInfo.get(strPrefix + DomainObject.SELECT_REVISION);
				strResMarketingName = (String)mapCopyElementInfo.get(strPrefix + SELECT_ATTRIBUTE_MARKETINGNAME);
				strLastObjectId = (String)mapCopyElementInfo.get(strPrefix + DomainObject.SELECT_ID);
				strCurrent = (String)mapCopyElementInfo.get(strPrefix + DomainObject.SELECT_CURRENT);
				jsonTempMCObject = new JSONObject(Order.INSERTION);
				mTempMCObject = new HashMap<String, String>();
				mTempMCObject.put(ENOVIA_NAME, strEnoviaName);
				mTempMCObject.put(COPY_ELEMENT_TYPE_CODE, strEnoviaType);
				mTempMCObject.put(MARKETING_NAME, strResMarketingName);
				mTempMCObject.put(ENOVIA_REVISION, strEnoviaRevision);
				mTempMCObject.put(ENOVIA_STATE, strCurrent);
				
				getJSONOrderedInsertion(jsonTempMCObject, mTempMCObject, true);
				
				relPattern = new Pattern(RELATIONSHIP_ARTWORK_ELEMENT_CONTENT);
				typePattern = new Pattern(TYPE_ARTWORKELEMENT);
				domObj = DomainObject.newInstance(context, strLastObjectId);
				
				mlLCMap = domObj.getRelatedObjects(context, 
						relPattern.getPattern(),  
						typePattern.getPattern(),
						slSelectables, 
						new StringList(), 
						false, 
						true, 
						(short)1, 	
						DomainObject.EMPTY_STRING, 
						DomainObject.EMPTY_STRING, 
						0);
				for(int iLC=0; iLC<mlLCMap.size(); iLC++){
					bGraphicFilesPresent = false;
					mapCopyElementInfo = (Map)mlLCMap.get(iLC);
					strEnoviaType = (String)mapCopyElementInfo.get(DomainObject.SELECT_TYPE);
					strEnoviaName = (String)mapCopyElementInfo.get(DomainObject.SELECT_NAME);
					strEnoviaRevision = (String)mapCopyElementInfo.get(DomainObject.SELECT_REVISION);
					strResCopyContent = (String)mapCopyElementInfo.get(SELECT_ATTRIBUTE_COPY_TEXT);
					strResMarketingName = (String)mapCopyElementInfo.get(SELECT_ATTRIBUTE_MARKETINGNAME);
					strCurrent = (String)mapCopyElementInfo.get(DomainObject.SELECT_CURRENT);
					strLanguage = (String)mapCopyElementInfo.get(SELECT_ATTRIBUTE_COPY_TEXT_LANGUAGE);
					
					pgCheckoutGraphicElementFiles(context,workSpacePath, mapCopyElementInfo);
					
					jsonTempLCObject = new JSONObject(Order.INSERTION);
					mTempLCObject = new HashMap<String, String>();
					mTempLCObject.put(ENOVIA_NAME, strEnoviaName);
					mTempLCObject.put(COPY_ELEMENT_TYPE_CODE, strEnoviaType);
					mTempLCObject.put(MARKETING_NAME, strResMarketingName);
					mTempLCObject.put(IS_BASE_COPY, (String)mapCopyElementInfo.get(SELECT_ATTRIBUTE_IS_BASE_COPY));
					if(bGraphicFilesPresent){
						mTempLCObject.put(IMAGE_LOCATION_FOR_MAP, (String)mapCopyElementInfo.get(IMAGE_LOCATION_FROM_DB));
					} else {
						mTempLCObject.put(TEXT_CONTENT, strResCopyContent);
						mTempLCObject.put(LANGUAGE, strLanguage);
					}
					mTempLCObject.put(ENOVIA_REVISION, strEnoviaRevision);
					mTempLCObject.put(ENOVIA_STATE, strCurrent);
					
					getJSONOrderedInsertion(jsonTempLCObject, mTempLCObject, false);
					
					if(!jsonTempMCObject.contains(LOCAL_COPIES)){
						jsonLCArray = new JSONArray();
						jsonLCArray.put(jsonTempLCObject);
						jsonTempMCObject.put(LOCAL_COPIES, jsonLCArray);
					} else {
						jsonLCArray = jsonTempMCObject.getJSONArray(LOCAL_COPIES);
						jsonLCArray.put(jsonTempLCObject);
					}
				}
				
				if(!jsonFinal.contains(ARTWORK_CONTENT_COPY_ELEMENT)){
					jsonTempFinalArray = new JSONArray();
					jsonTempFinalArray.put(jsonTempMCObject);
					jsonFinal.put(ARTWORK_CONTENT_COPY_ELEMENT, jsonTempFinalArray);
				} else {
					jsonTempFinalArray = jsonFinal.getJSONArray(ARTWORK_CONTENT_COPY_ELEMENT);
					jsonTempFinalArray.put(jsonTempMCObject);
				}
				
			}
		}
		logger.info("Inside convertIntoJsonFormat() method --- Ends");
    }
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * Used to order the JSON dynamically using TWO common keys in it
	 * STR_JSON_ORDER_KEYS_LOCAL & STR_JSON_ORDER_KEYS_MASTER, holds the actual order of keys in JSON
	 * @param jsonTempMCObject - stores the JSON in ordered format
	 * @param mTempMCObject - Stores all the info, which also equals the JSON produced, but not in Order
	 * @param bMaster - Specifies for either Master Or Local Elements to be ordered
	 * @return
	 * @throws MatrixException 
	 * @throws nothing
	 */
	private void getJSONOrderedInsertion(JSONObject jsonTempMCObject, Map<String, String> mTempMCObject, boolean bMaster) throws MatrixException {
		logger.info("Inside getJSONOrderedInsertion() method --- Starts");
		StringList slJSONKeysInOrder;
		int iSize ;
		String sKey = null;
		if(bMaster){
			slJSONKeysInOrder = FrameworkUtil.split(STR_JSON_ORDER_KEYS_MASTER, STR_S_PIPE);
		} else {
			slJSONKeysInOrder = FrameworkUtil.split(STR_JSON_ORDER_KEYS_LOCAL, STR_S_PIPE);
		}
		iSize = slJSONKeysInOrder.size();
		for(int iOrder=0; iOrder<iSize; iOrder++){
			sKey = slJSONKeysInOrder.get(iOrder);
			if(mTempMCObject != null && mTempMCObject.containsKey(sKey)){
				jsonTempMCObject.put(sKey, mTempMCObject.get(sKey));
			}
		}
		logger.info("Inside getJSONOrderedInsertion() method --- Ends");
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * This method is used to find whether graphics element is presents in master copy or not
	 * @param context
	 * @param workSpacePath
	 * @param mapCopyElementInfo
	 * @throws Exception
	 */
	private void pgCheckoutGraphicElementFiles(Context context,String workSpacePath, Map<String, Object> mapCopyElementInfo) throws Exception {
		logger.info("Inside pgCheckoutGraphicElementFiles() method --- Starts");
		String elementName = null;
		FileList files = new FileList();
		String docId = null;
		DomainObject domDoc = null;
		try {
			elementName = (String)mapCopyElementInfo.get(DomainConstants.SELECT_NAME);
			docId = (String)mapCopyElementInfo.get(SELECT_GRAPHIC_DOC_ID);
			
			if(!BusinessUtil.isNullOrEmpty(docId) && FrameworkUtil.isObjectId(context, docId)){
				domDoc = DomainObject.newInstance(context, docId);
				files = domDoc.getFiles(context, DomainConstants.FORMAT_GENERIC);
				
				if(BusinessUtil.isNullOrEmpty(files)) 
					return;
				
				if(BusinessUtil.isNotNullOrEmpty(files)) {
					mapCopyElementInfo.put(IMAGE_LOCATION_FROM_DB, AWLUtil.strcat(elementName, SEPARATOR, files.get(0)));
					 
					domDoc.checkoutFiles(context, false, DomainConstants.FORMAT_GENERIC, files, workSpacePath+SEPARATOR+elementName);
					bGraphicFilesPresent = true;	 
				 }
			 }
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		logger.info("Inside pgCheckoutGraphicElementFiles() method --- Ends");
			
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * This method is used to fetch the data based on brand, region and its type
	 * @param context
	 * @param slListOfString
	 * @param slSelectables
	 * @param mlBrandRegionInfo
	 * @param mlCopyElementInfo
	 * @throws Exception
	 */
	private void searchByBrandRegionType(Context context, StringList slListOfString, StringList slSelectables,MapList mlBrandRegionInfo,MapList mlCopyElementInfo) throws Exception {
		logger.info("Inside searchByBrandRegionType() method --- Starts");
		String strCopyContentSelectRes = null;
		String strRegionId = null;
		Pattern relPattern = null;
		Pattern typePattern = null;
		DomainObject domRegionObj = null;
		Map<String, String> mapPostPattern = new HashMap<String, String>();
		StringList slRelSelects = new StringList();
		String strObjWher = DomainObject.EMPTY_STRING;
		String strRelWher = DomainObject.EMPTY_STRING;
		String strCopyContent = slListOfString.get(3);
		String strCopyContentMatch = slListOfString.get(4);
		String strGS1Type = slListOfString.get(5);
		boolean bExactMatch = Boolean.parseBoolean(slListOfString.get(6));
		String strRTATypeMaster = getRTACopyTypeFromRIS(strGS1Type);
		String strRTATypeLocal = strRTATypeMaster.replace(STR_MASTER_SPACE, DomainObject.EMPTY_STRING);//"Master "
		String strRegionSelectRes = AWLUtil.strcat("from[", 
				ProductLineConstants.RELATIONSHIP_SUB_PRODUCT_LINES, 
				STR_CLOSE_DOT_TO_OPEN, TYPE_PRODUCTTYPE, STR_CLOSE_DOT, DomainObject.SELECT_ID);
		String strCopyContentSelect = AWLUtil.strcat("from[", RELATIONSHIP_ARTWORK_ELEMENT_CONTENT, 
				STR_CLOSE_DOT_TO_OPEN, strRTATypeLocal, STR_S_PIPE, STR_ESCAPE_KEY, STR_S_SPACE, SELECT_ATTRIBUTE_IS_BASE_COPY, STR_COND_EQUAL_TO, 
				AWLConstants.RANGE_YES, STR_COND_AND, SELECT_ATTRIBUTE_COPY_TEXT);
		Map<String, Object> mTempMap = null;
		int iAllMCSize; 
		int iBRSize = 0;
		MapList mlAllMasterElementInfo = null;
		String sTempCpyCont=null;
		String sTempMCId = null;
		StringList slMCConsideredList = null;
		if(!bExactMatch){
			strCopyContent = strCopyContent.replaceAll(STR_REGX_QUOTE_REPLACER, STR_REGX_QUOTE_REPLACEMENT);
			strCopyContentSelect = AWLUtil.strcat(strCopyContentSelect, STR_MATCH, STR_S_QUOTE, 
					DomainObject.QUERY_WILDCARD, strCopyContent, DomainObject.QUERY_WILDCARD, 
					STR_S_QUOTE, STR_CLOSE_DOT);
		} else {
			strCopyContentMatch = strCopyContentMatch.replaceAll(STR_REGX_QUOTE_REPLACER, STR_REGX_QUOTE_REPLACEMENT);
			strCopyContentSelect = AWLUtil.strcat(strCopyContentSelect, STR_COND_EQUAL_TO, 
					STR_S_QUOTE, strCopyContentMatch, STR_S_QUOTE, STR_CLOSE_DOT);
		}
		strCopyContentSelect = AWLUtil.strcat(strCopyContentSelect, SELECT_ATTRIBUTE_COPY_TEXT);
		logger.info("Method:searchByBrandRegionType:::strCopyContentSelect=====>" + strCopyContentSelect);
		slSelectables.add(strCopyContentSelect);
		strCopyContentSelectRes = AWLUtil.strcat("from[", RELATIONSHIP_ARTWORK_ELEMENT_CONTENT, 
				STR_CLOSE_DOT_TO_OPEN, strRTATypeLocal, STR_CLOSE_DOT, SELECT_ATTRIBUTE_COPY_TEXT);
		
		iBRSize = mlBrandRegionInfo.size();
		if(iBRSize > 0){
			strRegionId = (String)((Map<String, Object>)mlBrandRegionInfo.get(0)).get(strRegionSelectRes);
			if(BusinessUtil.isNotNullOrEmpty(strRegionId)){
				relPattern = new Pattern(ProductLineConstants.RELATIONSHIP_SUB_PRODUCT_LINES);//Sub Product Lines
				relPattern.addPattern(ProductLineConstants.RELATIONSHIP_PRODUCT_LINE_MODELS);//Product Line Models
				relPattern.addPattern(RELATIONSHIP_ARTWORK_MASTER);//Artwork Master
				
				typePattern = new Pattern(ProductLineConstants.TYPE_PRODUCT_LINE);//Product Line
				typePattern.addPattern(ProductLineConstants.TYPE_PRODUCTS);//Products Master
				typePattern.addPattern(TYPE_SUB_BRAND);//Sub-brand
				typePattern.addPattern(strRTATypeMaster);//strGS1Type
				domRegionObj = DomainObject.newInstance(context, strRegionId); 
				mapPostPattern.put(DomainObject.SELECT_IS_LAST, AWLConstants.RANGE_TRUE); //islast == TRUE
				
				mlAllMasterElementInfo = domRegionObj.getRelatedObjects(context, relPattern.getPattern(), 
					typePattern.getPattern(), false, true, 0, slSelectables, slRelSelects, strObjWher, 
						strRelWher, 0, RELATIONSHIP_ARTWORK_MASTER, strRTATypeMaster, mapPostPattern);
				
				slMCConsideredList = new StringList();
				iAllMCSize = mlAllMasterElementInfo.size();
				for(int iMC=0; iMC<iAllMCSize;iMC++){
					mTempMap = (Map<String, Object>)mlAllMasterElementInfo.get(iMC);
					sTempMCId = (String)mTempMap.get(DomainObject.SELECT_ID);
					sTempCpyCont = (String)mTempMap.get(strCopyContentSelectRes);
					if(BusinessUtil.isNotNullOrEmpty(sTempCpyCont) && !slMCConsideredList.contains(sTempMCId)){
						mlCopyElementInfo.add(mTempMap);
						slMCConsideredList.add(sTempMCId);
					}
				}
			}
		}
		logger.info("Inside searchByBrandRegionType() method --- Ends");
	
    }
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
	/**
	 * This method is used to converts a string of bytes into a string of ASCII characters for final output
	 * @param context
	 * @param jsonFinal
	 * @param strZipFileName
	 * @param strFileName
	 * @param workSpacePath
	 * @return
	 * @throws MatrixException
	 * @throws Exception
	 */
	private Map<String, String> generateBase64Code(Context context, JSONObject jsonFinal, String strZipFileName, String strFileName, String workSpacePath) throws Exception{
		logger.info("Inside generateBase64Code() method --- Starts");
		Map<String, String> mResponseMap = new HashMap<String, String>();
		Path tempGraphicFilePath;
		Path actualGraphicFilePath;
		String sDupWorkSpacePath;
		String zipFilePath;
		File fWorkSpaceFile;
		File workingDirectory;
		File fileFolder;
		byte[] zipFileByteContent;
		File[] fArrAllFiles = null;
		int iTempDir = -1;
		if(workSpacePath == null){
			workSpacePath = context.createWorkspace();
		}
		sDupWorkSpacePath = workSpacePath;
		fWorkSpaceFile = new File(workSpacePath);
		
		if(bGraphicFilesPresent){
			fArrAllFiles = fWorkSpaceFile.listFiles();
		}
		workSpacePath = workSpacePath + SEPARATOR + strZipFileName;
		workingDirectory = new File(workSpacePath);
		if(!workingDirectory.exists())
			workingDirectory.mkdir();
		
		if(fArrAllFiles != null) {
			for(int iWSFile=0; iWSFile<fArrAllFiles.length; iWSFile++){
				iTempDir = fArrAllFiles[iWSFile].toString().lastIndexOf(SEPARATOR);
				tempGraphicFilePath = Paths.get(fArrAllFiles[iWSFile].toString());
				actualGraphicFilePath = Paths.get(workSpacePath + SEPARATOR + fArrAllFiles[iWSFile].toString().substring(iTempDir));
				Files.move(tempGraphicFilePath, actualGraphicFilePath, REPLACE_EXISTING);
			}
		}
		
		if(jsonFinal != null && !jsonFinal.toString().isEmpty()) {
			writeToXML(workSpacePath, jsonFinal.toString(5), AWLUtil.strcat(strFileName, STR_EXTN_JSON));
			if(bFileCreationSuccess){
				mResponseMap.put(STR_FILE_CREATED, AWLConstants.RANGE_TRUE);
			}
		}

		zipFilePath = AWLUtil.strcat(workSpacePath, STR_EXTN_ZIP);
		zipFileByteContent = zipDirectory(sDupWorkSpacePath, zipFilePath);
		
		fileFolder = new File(sDupWorkSpacePath);
		deleteFiles(fileFolder);
		
		String sEncodeString=Base64.getEncoder().encodeToString(zipFileByteContent);
		mResponseMap.put(STR_ENCODE_BASE64, sEncodeString);
		logger.info("Inside generateBase64Code() method --- Ends");
		return mResponseMap;
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
	
	//Added by Sogeti Offshore for 2018x.5 Req-33381 STARTS
		/**
		 * This is recursive method used to delete the files from root folder to all levels.
		 * @param sourceFolder - Folder from which files are getting deleted.
		 */
	private void deleteFiles(File sourceFolder) throws IOException {
		logger.info("Inside deleteFiles() method --- Starts");
		File tempFile = null;
		File[] tempFileArr = sourceFolder.listFiles();
		for(int index=0; index<tempFileArr.length; index++) {
			tempFile = tempFileArr[index];
			if(tempFile.isDirectory()){
				logger.info(tempFile + " is a directory!!!");
				deleteFiles(tempFile);
			} else {
				logger.info(tempFile + " is a file deleting!!!");
				Files.deleteIfExists(Paths.get(tempFile.toString()));
			}
		}
		Files.deleteIfExists(Paths.get(sourceFolder.toString()));
		logger.info("AAL file references are all deleted!!!");
		logger.info("Inside deleteFiles() method --- Ends");
	}
	//Added by Sogeti Offshore for 2018x.5 Req-33381 ENDS
}
