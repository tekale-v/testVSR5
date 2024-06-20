package com.pg.widgets.rtautil;

import matrix.db.Attribute;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import matrix.db.FileList;
import matrix.db.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.pg.Artwork.ArtworkConstants;
import com.matrixone.apps.awl.enumeration.AWLAttribute;
import com.matrixone.apps.awl.enumeration.AWLType;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

public class RTAApproveCopyMatrix {
	static final String POA_ID = "PoaId";
	static final String PGAAA_MARKUP_ID = "pgAAA_MarkupID";
	static final String USER = "User";
	static final String CREATION_DATE = "CreationDate";
	static final String CONTENT = "Content";
	static final String RESP_FUN = "RespFunction";
	static final String RESP_FUN_ID = "RespFunctionId";
	static final String TASK_OVER_DUE = "taskoverdue";
	static final String FORM_DATA = "formData";
	static final String ASSOCIATED_TASK = "AssociatedTask";
	static final String ASSOCIATE_POA = "AssociatePOA";
	static final String POA_STATE = "POAState";
	static final String POA_REVISION = "POARevision";
	static final String REWORK_POAID = "ReworkPOAId";
	static final String REWORK_SEQUENCE = "ReworkSequence";
	static final String ORIGINATED = "Originated";
	static final String TASK_COMPLETE = "TaskCompleted";
	static final String RESULT = "Result";
	static final String COMPLETE = "Complete";
	static final String PASS = "Pass";
	static final String FAILED = "Failed";
	static final String STR_YES = "Yes";
	static final String STR_NO = "No";
	static final String POA_IDs_IN_UI = "poaIdsInUI";
	static final String TEXT_BOX_VALUE = "txtBoxValue";
	static final String TASK_OVERDUE_FLAG = "taskOverdueFlag";
	static final String CAUSE_RANGES = "CauseRanges";
	static final String FUNCTION = "function";
	static final String TASK_OVER_TIME_CAUSE = "taskOverTimeCause";
	static final String TASK_OVER_TIME_COMMENT = "taskOverTimeComment";
	static final String CAUSE = "cause";
	static final String PG_PICKLIST_NAME = "pgPicklistName";
	static final String SETTINGS = "settings";
	static final String FIELD_MAP = "fieldMap";
	static final String NEXT_ID = "next.id";
	static final String PREVIOUS_ID = "previous.id";
	static final String EXCEPTION_MESSAGE = "Exception in RTAApproveCopyMatrix";
	
	private static final Logger logger = Logger.getLogger(RTAUtil.class.getName());
	static String separator = java.io.File.separator;
	static int iOddEven = 1;
	StringBuffer sbHTMLData = new StringBuffer(DomainConstants.EMPTY_STRING);
	static double iClientTimeOffset;
	static Locale lLocale = null;

	/***
	 * This method returns Markup Data
	 * @param context
	 * @param paramString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static String getMarkupData(Context context, String paramString, HttpServletRequest request)
			throws Exception {
		String STR_LOGGED_IN_USER = "";
		String strDir = "";
		HashMap returnMap = new HashMap();
		boolean isArtworkProjectManager = false;
		boolean isProductManager = false;
		String strPOACurrentStatus = "";
		StringList strlExistingMarkupId = new StringList();
		int iOddEven = 1;
		int rowCount = 0;
		Map<String, ArrayList<Element>> hmTree;
		StringBuffer sb;
		String sTimeZone;
		String DateFrm;
		String separator = java.io.File.separator;
		Map mMarkupObjectData = new HashMap();
		Map mReplyCountMap = new HashMap();
		Map mSessionMarkupData = new HashMap();
		MapList mlSessionMarkupData = new MapList();
		String outputSessionMarkupData = DomainObject.EMPTY_STRING;
		HttpSession session = request.getSession();
		boolean blnEditMode = false;
		String strPOAReviewState = PropertyUtil.getSchemaProperty(null, "Policy",
				PropertyUtil.getSchemaProperty(null, "policy_POA"), "state_Review");
		String STR_TYPE_ESKO_MARKUP_DATA = PropertyUtil.getSchemaProperty(null, "type_pgAAA_EskoMarkupData");
		String STR_RELATIONSHIP_ESKO_MARKUP_DATA = PropertyUtil.getSchemaProperty(null,
				"relationship_pgAAAEskoViewableToMarkup");
		String STR_ATTRIBUTE_MARKUP_ID = "attribute[" + PropertyUtil.getSchemaProperty(null, "attribute_pgAAA_MarkupID")
				+ "]";
		String STR_ATTRIBUTE_PGPICKLISTCODE = "attribute["
				+ PropertyUtil.getSchemaProperty(null, "attribute_pgPickListCode") + "]";
		String STR_ATTRIBUTE_COMMENT = "attribute[" + PropertyUtil.getSchemaProperty(null, "attribute_pgAAA_Comment")
				+ "]";
		String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE = PropertyUtil.getSchemaProperty(null,
				"relationship_pgAAA_EskoMarkupDataTopgPLIDefectType");
		String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION = PropertyUtil.getSchemaProperty(null,
				"relationship_pgAAA_EskoMarkupDataTopgPLIResponsibleFunction");
		String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON = PropertyUtil.getSchemaProperty(null,
				"relationship_pgAAA_EskoMarkupDataTopgPLIReason");
		String TYPE_PGPLIREASON = PropertyUtil.getSchemaProperty(null, "type_pgPLIReason");
		String TYPE_PGPLIDEFECTTYPE = PropertyUtil.getSchemaProperty(null, "type_pgPLIDefectType");
		String TYPE_PGPLIRESPONSIBLE_FUNCTION = PropertyUtil.getSchemaProperty(null, "type_pgPLIResponsibleFunction");
		String STR_STATE_Active = PropertyUtil.getSchemaProperty(null, "Policy",
				PropertyUtil.getSchemaProperty(null, "policy_pgPicklistItem"), "state_Active");
		StringList strlDefectTypePickListId = new StringList();
		StringList strlDefectTypePickListName = new StringList();
		StringList strlReasonPickListId = new StringList();
		StringList strlReasonPickListName = new StringList();
		StringList strlResponsibleFunctionPickListId = new StringList();
		StringList strlResponsibleFunctionPickListName = new StringList();
		String strResponsibleFunction = "";
		String ROLE_ARTWORK_PROJECT_MANAGER = PropertyUtil.getSchemaProperty(context, "role_ArtworkProjectManager");
		isArtworkProjectManager = PersonUtil.hasAssignment(context, ROLE_ARTWORK_PROJECT_MANAGER);
		isProductManager = PersonUtil.hasAssignment(context,
				PropertyUtil.getSchemaProperty(context, "role_ProductManager"));
		int level = 0;
		int iNumObjAccess = 0;
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String strPOA = jsonInputData.getString(POA_ID);
		String strExtension = ".pdf";
		String strArtworkFileVersionId = DomainConstants.EMPTY_STRING;

		DomainObject domPOA = DomainObject.newInstance(context, strPOA);
		MapList mlArtworkFileAnnot = domPOA.getRelatedObjects(context, // context
				DomainObject.RELATIONSHIP_PART_SPECIFICATION, // relationship name
				AWLType.ARTWORK_FILE.get(context), // type
				new StringList(DomainObject.SELECT_ID), // bus selectable
				null, // rel selectable
				false, // get from
				true, // get to
				(short) 1, // recurse level
				DomainConstants.EMPTY_STRING, // bus where
				DomainConstants.EMPTY_STRING, // rel where
				0);

		Map mArtworkFile = null;
		String strArtworkFileId = DomainConstants.EMPTY_STRING;
		DomainObject dobArtworkFile = null;
		MapList mlArtworkVersion = null;
		Map mArtworkVersion = null;
		String strTitle = null;
		String strExt = null;
		StringList slSelect = new StringList(DomainObject.SELECT_ID);
		slSelect.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);

		for (int l = 0; l < mlArtworkFileAnnot.size(); l++) {
			mArtworkFile = (Map) mlArtworkFileAnnot.get(l);
			strArtworkFileId = (String) mArtworkFile.get(DomainObject.SELECT_ID);
			if (strArtworkFileId != null && !DomainConstants.EMPTY_STRING.equals(strArtworkFileId)) {
				dobArtworkFile = DomainObject.newInstance(context, strArtworkFileId);
				mlArtworkVersion = dobArtworkFile.getRelatedObjects(context, // context
						CommonDocument.RELATIONSHIP_ACTIVE_VERSION, // relationship name
						AWLType.ARTWORK_FILE.get(context), // type
						slSelect, // bus selectable
						null, // rel selectable
						false, // get from
						true, // get to
						(short) 1, // recurse level
						DomainConstants.EMPTY_STRING, // bus where
						DomainConstants.EMPTY_STRING, // rel where
						0);
				for (int m = 0; m < mlArtworkVersion.size(); m++) {
					mArtworkVersion = (Map) mlArtworkVersion.get(m);
					strTitle = (String) mArtworkVersion.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
					strExt = strTitle.substring(strTitle.lastIndexOf("."), strTitle.length());
					if (strExt.equalsIgnoreCase(strExtension)) {
						strArtworkFileVersionId = (String) mArtworkVersion.get(DomainObject.SELECT_ID);
						break;
					}
				}
			}
		}
		if (UIUtil.isNotNullAndNotEmpty(strArtworkFileVersionId)) {
			DomainObject domArtworkFile = DomainObject.newInstance(context, strArtworkFileVersionId);

			String strObjType = domArtworkFile.getInfo(context, DomainConstants.SELECT_TYPE);
			String STR_ATTRIBUTE_PGPICKLISTCODE_SELECT = "attribute["
					+ PropertyUtil.getSchemaProperty(context, "attribute_pgPickListCode") + "]";
			String TYPE_CreateArtworkTemplate_TASK = PropertyUtil.getSchemaProperty(context,
					"type_pgAAA_CreateArtworkTemplateTask");
			
			blnEditMode = true;
			HashMap mpTempData = new HashMap();
			StringList objectSelects = new StringList(1);
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add(DomainConstants.SELECT_TYPE);
			objectSelects.add(STR_ATTRIBUTE_PGPICKLISTCODE_SELECT);
			MapList mlReworkPickListItems = DomainObject.findObjects(context,
					TYPE_PGPLIREASON + "," + TYPE_PGPLIRESPONSIBLE_FUNCTION, "*", "*", "*", "*", "current == Active",
					false, objectSelects);
			MapList mlReworkPickListItemsForDefectType = DomainObject.findObjects(context, // context
					TYPE_PGPLIDEFECTTYPE, // type
					DomainConstants.QUERY_WILDCARD, // name
					DomainConstants.QUERY_WILDCARD, // revison
					DomainConstants.QUERY_WILDCARD, // owner
					ArtworkConstants.VAULT_ESERVICE_PRODUCTION, // vault
					"current==" + STR_STATE_Active, // clause
					false, // expand
					objectSelects);
			// busselect
			if (!mlReworkPickListItemsForDefectType.isEmpty()) {
				mlReworkPickListItemsForDefectType.addSortKey(DomainConstants.SELECT_NAME, "ascending", "string");
				mlReworkPickListItemsForDefectType.sort();
				if (!mlReworkPickListItems.isEmpty()) {
					mlReworkPickListItems.addAll(mlReworkPickListItemsForDefectType);
				}
			}

			String strType = "";
			String strID = "";
			String strName = "";

			StringList slTempID = new StringList(1);
			StringList slTempName = new StringList(1);
			StringList slTempType = new StringList(1);

			if (!"".equals(strObjType) && !"null".equals(strObjType) && strObjType.length() > 0) {
				if (strObjType.equals(ArtworkConstants.TYPE_CIC)
						|| strObjType.equals(ArtworkConstants.TYPE_UPLOAD_CIC_TASK)) {
					mpTempData = getpicklistNames(context, "Artwork Template", mlReworkPickListItems);
				} else {
					mpTempData = getpicklistNames(context, "FA", mlReworkPickListItems);

				}
				if (mpTempData != null) {
					slTempID = (StringList) mpTempData.get(0);

					slTempName = (StringList) mpTempData.get(1);

					slTempType = (StringList) mpTempData.get(2);

				}
				for (int k = 0; k < slTempID.size(); k++) {
					strType = (String) slTempType.get(k);
					strID = (String) slTempID.get(k);
					strName = (String) slTempName.get(k);
					if (strType.equals(TYPE_PGPLIREASON)) {
						strlReasonPickListId.add(strID);
						strlReasonPickListName.add(strName);
					} else if (strType.equals(TYPE_PGPLIDEFECTTYPE)) {
						strlDefectTypePickListId.add(strID);
						strlDefectTypePickListName.add(strName);
					} else if (strType.equals(TYPE_PGPLIRESPONSIBLE_FUNCTION)) {
						strlResponsibleFunctionPickListId.add(strID);
						strlResponsibleFunctionPickListName.add(strName);
					}

				}
			}

			if (strObjType.equals(ArtworkConstants.TYPE_CIC)) {
				strPOACurrentStatus = domArtworkFile.getInfo(context, DomainConstants.SELECT_CURRENT);
				StringList strLatestVersionId = domArtworkFile.getInfoList(context,
						"from[" + CommonDocument.RELATIONSHIP_LATEST_VERSION + "].to.id");
				if (strLatestVersionId.size() > 0) {
					strArtworkFileVersionId = (String) strLatestVersionId.get(0);
					domArtworkFile.setId(strArtworkFileVersionId);
				}
			} else {
				strPOACurrentStatus = domArtworkFile.getInfo(context,
						"to[" + CommonDocument.RELATIONSHIP_ACTIVE_VERSION + "].from["
								+ AWLType.ARTWORK_FILE.get(context) + "].to["
								+ DomainObject.RELATIONSHIP_PART_SPECIFICATION + "].from[" + AWLType.POA.get(context)
								+ "].current");
			}

			try {
				// get EskoViewable object
				StringBuffer sbfEskoViewableToIdSel = new StringBuffer();
				sbfEskoViewableToIdSel.append("relationship[")
						.append(PropertyUtil.getSchemaProperty(context, "relationship_EskoViewableOf")).append("].to.id");
				String strEskoViewableToId = domArtworkFile.getInfo(context, sbfEskoViewableToIdSel.toString());
				logger.log(Level.INFO, "1 strEskoViewableToId()---------------> - {0}", strEskoViewableToId);
				DomainObject doEskoMarkup = new DomainObject(strEskoViewableToId);
				// check out XFDF file to temporary workspace
				String strFileName = getXFDFFileName(strArtworkFileVersionId, context) + ".xfdf";
				FileList fileList = new FileList();
				String strWorkspacePath = context.createWorkspace();
				String FileFormat = domArtworkFile.getDefaultFormat(context);
				File file = new File(strFileName, FileFormat);
				fileList.add(file);
				doEskoMarkup.checkoutFiles(context, false, FileFormat, fileList, strWorkspacePath);
				String strFilePath = strWorkspacePath + separator + strFileName;
				String strCount = "";
				// parse XFDF file
				java.io.File f = new java.io.File(strFilePath);
				Document doc = null;
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				// to be compliant, completely disable DOCTYPE declaration:
				docFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
				// or completely disable external entities declarations:
				docFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
				docFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
				// or prohibit the use of all protocols by external entities:
				docFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
				docFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
				// or disable entity expansion but keep in mind that this doesn't prevent fetching external entities
				// and this solution is not correct for OpenJDK < 13 due to a bug: https://bugs.openjdk.java.net/browse/JDK-8206132
				docFactory.setExpandEntityReferences(false);

				DocumentBuilder builder = docFactory.newDocumentBuilder();
				doc = builder.parse(f);
				NodeList annots = doc.getElementsByTagName("annots");
				if (annots != null && annots.getLength() > 0) {
					ArrayList<Element> lRootElements = new ArrayList<Element>();
					hmTree = new HashMap<String, ArrayList<Element>>();
					String sName = "";
					int iId = 0;
					for (int i = 0; i < annots.getLength(); i++) {
						Node annot = annots.item(i);
						NodeList nodes = annot.getChildNodes();
						for (int j = 0; j < nodes.getLength(); j++) {
							Element elem = (Element) nodes.item(j);
							// Code modification for PG AAA customization starts
							String sTitle = elem.getAttribute("title");

							sName = elem.getAttribute("name");
							String sId = sName.substring(sName.indexOf(".") + 1, sName.lastIndexOf(".")).trim();
							try {
								iId = Integer.parseInt(sId);
								// iId++;
							} catch (Exception exception) {
								iId = 1;
							}
							sId = Integer.toString(iId);
							strCount = "1";
							logger.log(Level.INFO, "1 mReplyCountMap()---------------> - {0}", mReplyCountMap);
							mReplyCountMap.put(sId, strCount);
							logger.log(Level.INFO, "2 mReplyCountMap()---------------> - {0}", mReplyCountMap);
							if (!elem.hasAttribute("inreplyto")) {
								lRootElements.add(elem);
							} else {
								String parentName = elem.getAttribute("inreplyto");
								hmTree.get(parentName).add(elem);
							}
							logger.log(Level.INFO, "1 hmTree()---------------> - {0}", hmTree);
							hmTree.put(elem.getAttribute("name"), new ArrayList<Element>());
							logger.log(Level.INFO, "2 hmTree()---------------> - {0}", hmTree);
						}
					}
					sTimeZone = (String) session.getAttribute("timeZone");
					DateFrm = PersonUtil.getPreferenceDateFormatString(context);
					StringList strlObjectSelect = new StringList(DomainObject.SELECT_ID);
					strlObjectSelect.add(STR_ATTRIBUTE_MARKUP_ID);
					strlObjectSelect.add(STR_ATTRIBUTE_COMMENT);
					strlObjectSelect.add(DomainConstants.SELECT_OWNER);
					strlObjectSelect.add("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE + "].to.id");
					strlObjectSelect
							.add("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE + "].to.name");
					strlObjectSelect
							.add("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION + "].to.id");
					strlObjectSelect.add(
							"relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION + "].to.name");
					strlObjectSelect.add("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON + "].to.id");
					strlObjectSelect.add("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON + "].to.name");
					MapList mlMarkupObjectList = doEskoMarkup.getRelatedObjects(context,
							STR_RELATIONSHIP_ESKO_MARKUP_DATA, STR_TYPE_ESKO_MARKUP_DATA, strlObjectSelect, null, false,
							true, (short) 1, null, null, 0);
					logger.log(Level.INFO, "1 mlMarkupObjectList()---------------> - {0}", mlMarkupObjectList);
					Map<Double, Integer> countMap = new HashMap<>();
					for (int k = 0; k < mlMarkupObjectList.size(); k++) {
						Map mMarkupData = (Map) mlMarkupObjectList.get(k);
						String strMarkupID = (String) mMarkupData.get(STR_ATTRIBUTE_MARKUP_ID);
						String strObjectID = (String) mMarkupData.get(DomainObject.SELECT_ID);
						logger.log(Level.INFO, "1 mMarkupObjectData()---------------> - {0}", mMarkupObjectData);
						logger.log(Level.INFO, "1 mSessionMarkupData()---------------> - {0}", mSessionMarkupData);
						double num = Double.parseDouble(strMarkupID);
						if (countMap.containsKey(num)) {
							int count = countMap.get(num);
							countMap.put(num, count + 1);
							double modifiedValue = num + 0.1 * count;
							mMarkupObjectData.put(modifiedValue,mMarkupData);
							mSessionMarkupData.put(modifiedValue,strObjectID);
						} else {
							countMap.put(num, 1);
							mMarkupObjectData.put(num,mMarkupData);
							mSessionMarkupData.put(num,strObjectID);
						}
						logger.log(Level.INFO, "2 mMarkupObjectData()---------------> - {0}", mMarkupObjectData);
						logger.log(Level.INFO, "2 mSessionMarkupData()---------------> - {0}", mSessionMarkupData);
					}
					session.setAttribute("mMarkupDataId", mSessionMarkupData);
					sb = new StringBuffer();
					strlExistingMarkupId = new StringList();
					
					for (int i = 0; i < lRootElements.size(); i++) {
						Map markupObjectData = new HashMap();
						printElementTree(context, lRootElements.get(i), 0, hmTree, rowCount, iNumObjAccess, blnEditMode,
								markupObjectData, mMarkupObjectData, mReplyCountMap, strPOACurrentStatus,
								strlResponsibleFunctionPickListId, strlResponsibleFunctionPickListName,
								strlReasonPickListId, strlReasonPickListName, strlDefectTypePickListId,
								strlDefectTypePickListName, mlSessionMarkupData, strlExistingMarkupId);
						logger.log(Level.INFO, "1 markupObjectData()---------------> - {0}", markupObjectData);
					}
					context.deleteWorkspace();
				}
			} catch (Exception ex) {
				// Code modification for PG AAA customization starts
				throw ex;
			}
			outputSessionMarkupData = convertMapListToJSON(context, mlSessionMarkupData);
		}
		return outputSessionMarkupData;
	}

	/***
	 * This method Updates the Map of MarkUp Data to show on the MarkUp Table
	 * @param context
	 * @param lRootElements
	 * @param level
	 * @param hmTree
	 * @param rowCount
	 * @param iNumObjAccess
	 * @param blnEditMode
	 * @param markupObjectData
	 * @param mMarkupObjectData
	 * @param mReplyCountMap
	 * @param strPOACurrentStatus
	 * @param strlResponsibleFunctionPickListId
	 * @param strlResponsibleFunctionPickListName
	 * @param strlReasonPickListId
	 * @param strlReasonPickListName
	 * @param strlDefectTypePickListId
	 * @param strlDefectTypePickListName
	 * @param mlSessionMarkupData
	 * @throws Exception
	 */
	private static void printElementTree(Context context, Element lRootElements, int level,
			Map<String, ArrayList<Element>> hmTree, int rowCount, int iNumObjAccess, boolean blnEditMode,
			Map markupObjectData, Map mMarkupObjectData, Map mReplyCountMap, String strPOACurrentStatus,
			StringList strlResponsibleFunctionPickListId, StringList strlResponsibleFunctionPickListName,
			StringList strlReasonPickListId, StringList strlReasonPickListName, StringList strlDefectTypePickListId,
			StringList strlDefectTypePickListName, MapList mlSessionMarkupData, StringList strlExistingMarkupId) throws Exception {
		String ROLE_ARTWORK_PROJECT_MANAGER = PropertyUtil.getSchemaProperty(context, "role_ArtworkProjectManager");
		Boolean isArtworkProjectManager = PersonUtil.hasAssignment(context, ROLE_ARTWORK_PROJECT_MANAGER);
		String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE = PropertyUtil.getSchemaProperty(context,
				"relationship_pgAAA_EskoMarkupDataTopgPLIDefectType");
		String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON = PropertyUtil.getSchemaProperty(context,
				"relationship_pgAAA_EskoMarkupDataTopgPLIReason");
		String RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION = PropertyUtil.getSchemaProperty(context,
				"relationship_pgAAA_EskoMarkupDataTopgPLIResponsibleFunction");
		Boolean isProductManager = PersonUtil.hasAssignment(context,
				PropertyUtil.getSchemaProperty(context, "role_ProductManager"));
		String STR_ATTRIBUTE_COMMENT = "attribute[" + PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_Comment")
				+ "]";
		String strPOAReviewState = PropertyUtil.getSchemaProperty(null, "Policy",
				PropertyUtil.getSchemaProperty(context, "policy_POA"), "state_Review");
		String STR_LOGGED_IN_USER = context.getUser();
		if (level == 0) {
			rowCount++;
		}
		Element elem = lRootElements;
		Node cont = elem.getElementsByTagName("contents-richtext").item(0);
		String sTitle = elem.getAttribute("title");
		String sPageNo = elem.getAttribute("page");

		int iPageNo = 1;
		try {
			iPageNo = Integer.parseInt(sPageNo);
			iPageNo++;
		} catch (Exception exception) {
			iPageNo = 1;
		}
		sPageNo = (level == 0) ? String.valueOf(iPageNo) : "";
		String sName1 = elem.getAttribute("name");
		String sId = sName1.substring(sName1.indexOf(".") + 1, sName1.lastIndexOf(".")).trim();
		int iId1 = 0;
		try {
			iId1 = Integer.parseInt(sId);
		} catch (Exception exception) {
			iId1 = 1;
		}
		sId = (level == 0) ? String.valueOf(iId1) : "";

		String sSubject = (level == 0) ? elem.getTagName() : "";
		String sUserName = sTitle;
		String sState = elem.hasAttribute("state") ? elem.getAttribute("state") : "";
		StringBuffer sbrValue = new StringBuffer();
		HashMap resultMap = getNodeTextContext(cont, sbrValue);
		String strAlignment = (String) resultMap.get("Alignment");
		String sContent = sbrValue.toString();
		if (sContent.startsWith("Review state: ")) {
			sContent = sState;
		}
		String sRowClass = (rowCount % 2) == 0 ? "even" : "odd";
		iOddEven++;
		if (sUserName.equalsIgnoreCase(context.getUser()) || isArtworkProjectManager || isProductManager) {
			iNumObjAccess++;
			markupObjectData.put(PGAAA_MARKUP_ID, sId);
			markupObjectData.put(PGWidgetConstants.KEY_TYPE, sSubject);
			markupObjectData.put(USER, sUserName);
			markupObjectData.put(CREATION_DATE, dateEskoToEnovia(elem.getAttribute("creationdate")));
			markupObjectData.put(CONTENT, sContent);
			if(!sId.equals("")){
				double dId = Double.parseDouble(sId);
				strlExistingMarkupId.add(Double.toString(dId));
			}
			double modifiedValue = 0.0;
			double dId = 0.0;
			Map mDataMap = null;
			String strMarkupID = "";
			logger.log(Level.INFO, "line 557: strlExistingMarkupId----------------->(0)",strlExistingMarkupId);
			if (!sId.equals("")) {
				dId = Double.parseDouble(sId);
				if (!strlExistingMarkupId.contains(sId)) {
					strlExistingMarkupId.add(sId);
					strMarkupID = String.format("%.1f", dId);
					logger.log(Level.INFO, "line 563: strMarkupID----------------->(0)",strMarkupID);
					mDataMap = (Map)mMarkupObjectData.get(Double.parseDouble(strMarkupID));
				} else {
					int count = 1;
					while (strlExistingMarkupId.contains(String.format("%.1f", dId + 0.1 * count))) {
						count++;
					}
					modifiedValue = dId + 0.1 * count;
					strMarkupID = String.format("%.1f", modifiedValue);
					strlExistingMarkupId.add(Double.toString(modifiedValue));
					logger.log(Level.INFO, "line 573: strMarkupID----------------->(1)",strMarkupID);
					mDataMap = (Map)mMarkupObjectData.get(Double.parseDouble(strMarkupID));
				}
			}
			logger.log(Level.INFO, "Line 509 1 markupObjectData()---------------> - {0}", markupObjectData);
			logger.log(Level.INFO, "3 hmTree()---------------> - {0}", hmTree);
			logger.log(Level.INFO, "Line 510: 1 elem.getAttribute(name)()---------------> - {0}", elem.getAttribute("name"));
			ArrayList<Element> lChildren = hmTree.get(elem.getAttribute("name"));
			logger.log(Level.INFO, "Line 512: 1 mMarkupObjectData()---------------> - {0}", mMarkupObjectData);
			logger.log(Level.INFO, "Line 513: 1 sId()---------------> - {0}", sId);
			logger.log(Level.INFO, "Line 514: 1 mDataMap()---------------> - {0}", mDataMap);
			if (mDataMap != null) {
				String strDefectType = (String) mDataMap
						.get("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIDEFECTTYPE + "].to.name");
				if (strDefectType == null) {
					strDefectType = "";
				}
				String strResponsibleFunction = (String) mDataMap
						.get("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIRESPONSIBLEFUNCTION + "].to.name");
				if (strResponsibleFunction == null) {
					strResponsibleFunction = "";
				}
				String strReason = (String) mDataMap
						.get("relationship[" + RELATIONSHIP_ESKOMARKUPDATA_TO_PGPLIREASON + "].to.name");
				if (strReason == null) {
					strReason = "";
				}
				String strComment = (String) mDataMap.get(STR_ATTRIBUTE_COMMENT);
				String strOwner = (String) mDataMap.get(DomainObject.SELECT_OWNER);
				String strCount = (String) mReplyCountMap.get(sId);
				markupObjectData.put(RESP_FUN, strResponsibleFunction);
				markupObjectData.put(RTAUtilConstants.KEY_REASON, strReason);
				markupObjectData.put(RTAUtilConstants.KEY_DEFECT_TYPE, strDefectType);
				markupObjectData.put(RTAUtilConstants.KEY_COMMENT, strComment);
				logger.log(Level.INFO, "Line 544: 1 markupObjectData()---------------> - {0}", markupObjectData);
			}	
			mlSessionMarkupData.add(markupObjectData);			   
			for (int i = 0; i < lChildren.size(); i++) {
				Map childMarkupObjectData = new HashMap();
				printElementTree(context, lChildren.get(i), level + 1, hmTree, rowCount, iNumObjAccess, blnEditMode,
					childMarkupObjectData, new HashMap(), mReplyCountMap, strPOACurrentStatus,
					strlResponsibleFunctionPickListId, strlResponsibleFunctionPickListName,
					strlReasonPickListId, strlReasonPickListName, strlDefectTypePickListId,
					strlDefectTypePickListName, mlSessionMarkupData, strlExistingMarkupId);
			}
			logger.log(Level.INFO, "Line 567: 1 markupObjectData()---------------> - {0}", markupObjectData);
		}
	}

	private static String dateEskoToEnovia(String sDateEsko) throws Exception {
		// ToDo : ensure timezone information is converted as well (e.g. ".....+02'00'"
		// in Esko date string)
		DateFormat dfEsko = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dCreationDate = (Date) dfEsko.parse(sDateEsko.substring(2));
		DateFormat dfEnovia = new SimpleDateFormat("M/d/yyyy h:m:s a");
		return dfEnovia.format(dCreationDate);
	}

	public static HashMap getNodeTextContext(Node node, StringBuffer sbrValue) {
		String strDir = "";
		HashMap returnMap = new HashMap();
		try {

			NodeList nodeList = node.getChildNodes();
			String strName = node.getNodeName();
			Element elem1;
			Element elem2;
			HashMap mpTemp;
			String strStyle = "";
			Node tempdNode;
			String strTempNodename = "";

			if (nodeList.getLength() == 0) {
				String strNodeContent = node.getNodeValue();
				if (strNodeContent != null) {
					sbrValue.append(strNodeContent);
				} else {
					sbrValue.append("<p/>");
				}
			}
			for (int i = 0; i < nodeList.getLength(); i++) {
				if ("body".equals(strName)) {
					elem1 = (Element) nodeList.item(i);
					strDir = elem1.hasAttribute("dir") ? elem1.getAttribute("dir") : "";
					returnMap.put("Alignment", strDir);
				}
				tempdNode = nodeList.item(i);
				strTempNodename = tempdNode.getNodeName();
				if ("span".equalsIgnoreCase(strTempNodename)) {
					elem2 = (Element) nodeList.item(i);
					strStyle = elem2.hasAttribute("style") ? elem2.getAttribute("style") : "";
					if (BusinessUtil.isNotNullOrEmpty(strStyle)) {
						sbrValue.append("<span style="+strStyle+">");
					} else {
						sbrValue.append("<span>");
					}
				}

				if (i == 0) {
					if(!"contents-richtext".equals(strName) && !"body".equals(strName) && !"span".equals(strName) ) {
						sbrValue.append("<"+strName+">");
					}	
				}
				Node childNode = nodeList.item(i);
				mpTemp = getNodeTextContext(childNode, sbrValue);
				if (i == (nodeList.getLength() - 1)) {
					if (!"contents-richtext".equals(strName) && !"body".equals(strName)) {
						sbrValue.append("</" + strName + ">");
					}
				}
			}
		} catch (Exception e) {
			sbrValue = new StringBuffer();
			throw e;
		}
		return returnMap;
	}

	public static HashMap getpicklistNames(Context context, String sWBSType, MapList mlReworkPickListItems) {
		HashMap mpTemp = new HashMap();
		StringList slTempID = new StringList(1);
		StringList slTempName = new StringList(1);
		StringList slTempType = new StringList(1);
		String STR_ATTRIBUTE_PGPICKLISTCODE_SELECT = "attribute["
				+ PropertyUtil.getSchemaProperty(context, "attribute_pgPickListCode") + "]";

		try {

			for (int count = 0; count < mlReworkPickListItems.size(); count++) {
				Map mReworkPicklistItem = (Map) mlReworkPickListItems.get(count);
				String strType = (String) mReworkPicklistItem.get(DomainObject.SELECT_TYPE);
				String strID = (String) mReworkPicklistItem.get(DomainObject.SELECT_ID);
				String strCurrent = (String) mReworkPicklistItem.get(DomainObject.SELECT_CURRENT);
				String strName = (String) mReworkPicklistItem.get(DomainObject.SELECT_NAME);
				String strPicklistCode = (String) mReworkPicklistItem.get(STR_ATTRIBUTE_PGPICKLISTCODE_SELECT);
				if (!"null".equalsIgnoreCase(strPicklistCode) && strPicklistCode != null
						&& strPicklistCode.length() > 0) {
					StringTokenizer strSelectTok = new StringTokenizer(strPicklistCode, "~");
					int iElementCounter = 0;
					while (strSelectTok.hasMoreTokens()) {
						String strTokVal = strSelectTok.nextToken();
						strTokVal = strTokVal.trim(); // artworktemplate~FA~abs~xyz
						if (strTokVal.equalsIgnoreCase(sWBSType)) {
							slTempID.add(strID);
							slTempName.add(strName);
							slTempType.add(strType);

						}
					}
				}
			}

			mpTemp.put(0, slTempID);
			mpTemp.put(1, slTempName);
			mpTemp.put(2, slTempType);
		} catch (Exception e) {
			throw e;
		}

		return mpTemp;
	}

	/***
	 * This method is called to get the XFDF FileName 
	 * @param strOid
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static String getXFDFFileName(String strOid, matrix.db.Context context) throws Exception {
		BusinessObject busObj = new BusinessObject(strOid);
		DomainObject doObj = new DomainObject(strOid);
		StringBuffer sbfBusSel = new StringBuffer();
		String strMinorType = new String();
		String strFileName = null;
		StringBuffer sbfEskoConfigSel = new StringBuffer();
		String strEskoConfig;

		try {

			sbfEskoConfigSel.append("relationship[")
					.append(PropertyUtil.getSchemaProperty(context, "relationship_EskoViewableOf"))
					.append("].to.attribute[")
					.append(PropertyUtil.getSchemaProperty(context, "attribute_EskoViewableProofScopeId")).append("]");

			strEskoConfig = doObj.getInfo(context, sbfEskoConfigSel.toString());
			if (UIUtil.isNotNullAndNotEmpty(strEskoConfig)) {
				Attribute oidAttr = busObj.getAttributeValues(context, "Title");
				strFileName = oidAttr.getValue();
				String[] strSplitFileName = strFileName.split(separator + ".");
				strFileName = strSplitFileName[0];
			} else {
				strFileName = null;
			}

		} catch (Exception ex) {
			ex.getMessage();
			strFileName = null;

		}
		return strFileName;
	}
	
	/***
	 * This method called to get Rework Details
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String getReworkDetails(Context context, String paramString) throws Exception {

		String TYPE_COPY_PHASE = PropertyUtil.getSchemaProperty(context, "type_pgAAA_CopyPhase");
		String STR_TYPE_REWORK = PropertyUtil.getSchemaProperty(context, "type_pgAAA_TaskSendForRework");
		String relRework = PropertyUtil.getSchemaProperty(context, "relationship_pgAAA_ObjectToTaskSendForRework");

		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String strPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
		String strPOAIdsArr[] = strPOAIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
		StringList slReworkObjectIds = new StringList(strPOAIdsArr);
		String outputSessionReworkData = DomainConstants.EMPTY_STRING;
		try {
			MapList mSessionReworkData = new MapList();
			DomainObject domObj = null;
			MapList mlReworkObjects = null;
			Map mapReworkDate = new HashMap();
			if (slReworkObjectIds != null && !DomainConstants.EMPTY_STRING.equals(slReworkObjectIds)) {
				int iTotalRowCount = 0;
				for (int i = 0; i < slReworkObjectIds.size(); i++) {
					String strReworkObjectId = (String) slReworkObjectIds.get(i);
					domObj = DomainObject.newInstance(context, strReworkObjectId);
					String strWBSType = domObj.getInfo(context, DomainObject.SELECT_TYPE);
					if (TYPE_COPY_PHASE.equals(strWBSType)) {
						continue;
					}
					StringList strlReworkObjectSelect = new StringList(DomainObject.SELECT_ID);
					mlReworkObjects = domObj.getRelatedObjects(context, relRework, STR_TYPE_REWORK, strlReworkObjectSelect, null, false, true,
							(short) 1, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, 0);
					iTotalRowCount = iTotalRowCount + mlReworkObjects.size();
					if (mlReworkObjects.size() > 0) {
						for (int l = 0; l < mlReworkObjects.size(); l++) {
							Map mReworkObjectData = (Map) mlReworkObjects.get(l);
							mapReworkDate = printReworkObjectHTML(context, (String) mReworkObjectData.get(DomainObject.SELECT_ID));
							mSessionReworkData.add(mapReworkDate);
						}
						outputSessionReworkData = convertMapListToJSON(context, mSessionReworkData);
					}
				}
			}
			
		} catch (Exception e) {
			throw e;
		}
		return outputSessionReworkData;
	}

	private static Map printReworkObjectHTML(Context context, String strReworkObjectId) throws Exception {
		Map mReworkObjectData = new HashMap();
		try {
			String STR_ATTRIBUTE_COMMENT_SELECT = "attribute[" + PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_Comment") + "]";
			String STR_RELATIONSHIP_COPY_ELEMENT_TO_REWORK = PropertyUtil.getSchemaProperty(context, "relationship_pgAAA_ObjectToTaskSendForRework");
			String STR_ATTRIBUTE_REWORK_COPY_ELEMENT_SELECT = "attribute[" + PropertyUtil.getSchemaProperty(context, "attribute_pgAAA_ReworkObject")
					+ "]";
			String RELATIONSHIP_WBSTaskTOsendForRework = PropertyUtil.getSchemaProperty(context, "relationship_pgAAA_WBSTaskToSendForRework");
			String STR_RELATIONSHIP_OBJECT_TO_TASK_SEND_FOR_REWORK = PropertyUtil.getSchemaProperty(context,
					"relationship_pgAAA_ObjectToTaskSendForRework");
			String STR_RELATIONSHIP_RESPONSIBLE_FUNCTION = PropertyUtil.getSchemaProperty(context,
					"relationship_pgAAA_WBSTaskTopgPLIResponsibleFunction");
			String STR_RELATIONSHIP_REASON = PropertyUtil.getSchemaProperty(context, "relationship_pgAAA_WBSTaskTopgPLIReason");
			String STR_RELATIONSHIP_DEFECT_TYPE = PropertyUtil.getSchemaProperty(context, "relationship_pgAAA_WBSTaskTopgPLIDefectType");
			TimeZone tz = TimeZone.getTimeZone(context.getSession().getTimezone());
			int tzoff = tz.getRawOffset();
			Double timezone = ((double)(tzoff ))/(3600000);	
			iClientTimeOffset = (timezone).doubleValue();
			lLocale = context.getLocale();

			iOddEven++;
			if (strReworkObjectId != null && !DomainConstants.EMPTY_STRING.equals(strReworkObjectId)) {
				StringList strlselect = new StringList(NEXT_ID);
				strlselect.add(PREVIOUS_ID);
				strlselect.add(DomainObject.SELECT_ORIGINATED);
				strlselect.add(DomainObject.SELECT_REVISION);
				strlselect.add(STR_ATTRIBUTE_COMMENT_SELECT);
				strlselect.add("to[" + STR_RELATIONSHIP_COPY_ELEMENT_TO_REWORK + "].from.attribute["
						+ AWLAttribute.MARKETING_NAME.get(context) + "]");
				strlselect.add(STR_ATTRIBUTE_REWORK_COPY_ELEMENT_SELECT);
				strlselect.add("to[" + STR_RELATIONSHIP_COPY_ELEMENT_TO_REWORK + "].from.id");
				strlselect.add("last.to[" + RELATIONSHIP_WBSTaskTOsendForRework + "].from.name");
				strlselect.add("last.to[" + STR_RELATIONSHIP_OBJECT_TO_TASK_SEND_FOR_REWORK + "].from.name");
				
				// Multi-value selects need to be added in both select lists, hence below code
				strlselect.add("from[" + STR_RELATIONSHIP_RESPONSIBLE_FUNCTION + "].to.name");
				strlselect.add("from[" + STR_RELATIONSHIP_REASON + "].to.name");
				strlselect.add("from[" + STR_RELATIONSHIP_DEFECT_TYPE + "].to.name");
				
				StringList strMultiselect = new StringList();
				strMultiselect.add("from[" + STR_RELATIONSHIP_RESPONSIBLE_FUNCTION + "].to.name");
				strMultiselect.add("from[" + STR_RELATIONSHIP_REASON + "].to.name");
				strMultiselect.add("from[" + STR_RELATIONSHIP_DEFECT_TYPE + "].to.name");
				
				DomainObject dObjRework = DomainObject.newInstance(context, strReworkObjectId);
				Map mSelectData = dObjRework.getInfo(context, strlselect, strMultiselect);
				
				StringList slResponsibleFunction = (StringList) mSelectData.get("from[" + STR_RELATIONSHIP_RESPONSIBLE_FUNCTION + "].to.name");
				String strResponsibleFunction = FrameworkUtil.join(slResponsibleFunction, ", ");
				if (strResponsibleFunction == null) {
					strResponsibleFunction = DomainConstants.EMPTY_STRING;
				}
				StringList slReason = (StringList) mSelectData.get("from[" + STR_RELATIONSHIP_REASON + "].to.name");
				String strReason = FrameworkUtil.join(slReason, ", ");
				if (strReason == null) {
					strReason = DomainConstants.EMPTY_STRING;
				}
				StringList slDefectType = (StringList) mSelectData.get("from[" + STR_RELATIONSHIP_DEFECT_TYPE + "].to.name");
				String strDefectType = FrameworkUtil.join(slDefectType, ", ");
				if (strDefectType == null) {
					strDefectType = DomainConstants.EMPTY_STRING;
				}

				String strPreviousId = (String) mSelectData.get(PREVIOUS_ID);
				if (strPreviousId == null) {
					strPreviousId = DomainConstants.EMPTY_STRING;
				}

				String strNextId = (String) mSelectData.get(NEXT_ID);
				if (strNextId == null) {
					strNextId = DomainConstants.EMPTY_STRING;
				}

				String strOriginated = (String) mSelectData.get(DomainObject.SELECT_ORIGINATED);
				if (strOriginated == null) {
					strOriginated = DomainConstants.EMPTY_STRING;
				} else {
					// DateFormat
					int iDateFormat = PersonUtil.getPreferenceDateFormatValue(context);

					// Formatting Date to Ematrix Date Format
					strOriginated = eMatrixDateFormat.getFormattedDisplayDateTime(context, strOriginated, true, iDateFormat, iClientTimeOffset,
							lLocale);
				}

				String strSequence = (String) mSelectData.get(DomainObject.SELECT_REVISION);
				if (strSequence == null) {
					strSequence = DomainConstants.EMPTY_STRING;
				}

				String strComment = (String) mSelectData.get(STR_ATTRIBUTE_COMMENT_SELECT);
				if (strComment == null) {
					strComment = DomainConstants.EMPTY_STRING;
				}

				String strAssociatedTaskName = (String) mSelectData.get("last.to[" + RELATIONSHIP_WBSTaskTOsendForRework + "].from.name");

				String strAssociatePOA = (String) mSelectData
						.get("last.to[" + STR_RELATIONSHIP_OBJECT_TO_TASK_SEND_FOR_REWORK + "].from.name");

				String strReworkPOADetails = (String) mSelectData.get(STR_ATTRIBUTE_REWORK_COPY_ELEMENT_SELECT);
				StringList slReworkPOADetails = FrameworkUtil.split(strReworkPOADetails, PGWidgetConstants.KEY_OR);

				String strReworkPOAId = (String) slReworkPOADetails.get(0);
				String strReworkPOARevision = (String) slReworkPOADetails.get(1);
				String strReworkPOAState = (String) slReworkPOADetails.get(2);

				mReworkObjectData.put(ASSOCIATED_TASK, strAssociatedTaskName);
				mReworkObjectData.put(ASSOCIATE_POA, strAssociatePOA);
				mReworkObjectData.put(POA_STATE, strReworkPOAState);
				mReworkObjectData.put(POA_REVISION, strReworkPOARevision);
				mReworkObjectData.put(REWORK_POAID, strReworkPOAId);
				mReworkObjectData.put(REWORK_SEQUENCE, strSequence);
				mReworkObjectData.put(RESP_FUN, strResponsibleFunction);
				mReworkObjectData.put(RTAUtilConstants.KEY_REASON, strReason);
				mReworkObjectData.put(RTAUtilConstants.KEY_DEFECT_TYPE, strDefectType);
				mReworkObjectData.put(RTAUtilConstants.KEY_COMMENT, strComment);
				mReworkObjectData.put(ORIGINATED, strOriginated);

			}
		} catch (Exception e) {
			throw e;
		}
		return mReworkObjectData;
	}
	public static String submitPOA(Context context, String paramString) throws Exception {
		String outputSubmitData = null;
		MapList mSubmitData = new MapList();
		MapList mFailedData = new MapList();
		boolean isTransactionActive = false;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			JsonArray jsonDataArray = jsonInputData.getJsonArray(RTAUtilConstants.JSON_KEY_DATA);		
						
			Map mapSubmitDate = null;
			Map mapFailedPOA = new HashMap();;		

			String strResult = DomainConstants.EMPTY_STRING;
			String ATTRIBUTE_PROJECT_TYPE = PropertyUtil.getSchemaProperty(context, "attribute_ProjectType");
			StringList slObjSelects = new StringList(3);
			slObjSelects.add(DomainConstants.SELECT_ID);
			slObjSelects.add(DomainConstants.SELECT_NAME);
			slObjSelects.add("attribute[" + ATTRIBUTE_PROJECT_TYPE + "]");
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;
			for (int i = 0; i < jsonDataArray.size(); i++) {
				JsonObject jsonPayloadData = jsonDataArray.getJsonObject(i);
				String strPOAIds = jsonPayloadData.getString(POA_ID);
				boolean bTaskOverdue = jsonPayloadData.getBoolean(TASK_OVER_DUE);
				String strSubmitPoaFormData = jsonPayloadData.getString(FORM_DATA);
				
				DomainObject domPOAObj = DomainObject.newInstance(context, strPOAIds);
				String sProjectId = (String) domPOAObj.getInfo(context,
						"to[" + ArtworkConstants.RELATIONSHIP_PROJECT_TO_POA + "].from.id");
				String POAName = domPOAObj.getName();
				if (UIUtil.isNullOrEmpty(sProjectId)) {
					throw new Exception("POA is not connected to Artwork Project.");
				} else {
					DomainObject domProject = DomainObject.newInstance(context, sProjectId);
					String strProjectType = (String) domProject.getInfo(context,
							"attribute[" + ATTRIBUTE_PROJECT_TYPE + "]");

					StringList objSelect = new StringList(2);
					objSelect.add(DomainConstants.SELECT_ID);
					objSelect.add("attribute[" + DomainConstants.ATTRIBUTE_PROJECT_ROLE + "]");
					String strApprMatrxFunction = null;
					MapList mlDeliverableTask = domPOAObj.getRelatedObjects(context, // Context
							DomainConstants.RELATIONSHIP_TASK_DELIVERABLE, // Relationship
							ArtworkConstants.TYPE_APPROVE_POA_COPY_MATRIX_TASK, // Type
							objSelect, // Bus Select
							null, // Rel Select
							true, // get To side
							false, // get from side
							(short) 1, // recurse level
							DomainConstants.EMPTY_STRING, // Bus where
							DomainConstants.EMPTY_STRING, // Rel Where
							0); // limit

					if (!mlDeliverableTask.isEmpty()) {
						for (int j = 0; j < mlDeliverableTask.size(); j++) {
							strApprMatrxFunction = (String) ((Map) mlDeliverableTask.get(j))
									.get("attribute[" + DomainConstants.ATTRIBUTE_PROJECT_ROLE + "]");
						}
					}

					mapSubmitDate = new HashMap();
					mapFailedPOA= new HashMap();
					
					strResult = pgRTAPromotePOAToPremilinary(context, strPOAIds, bTaskOverdue,
							strSubmitPoaFormData, strProjectType, POAName);
					strResult = strResult.replaceAll("/[\r\n]+/g", DomainConstants.EMPTY_STRING).replace("/[\r\n]+$/g", DomainConstants.EMPTY_STRING);
					StringList strResultList = FrameworkUtil.split(strResult, "~");
					strResult = strResultList.get(0).trim();
					
					if (strResult.equalsIgnoreCase(PASS)) {						
						strResult = pgRTAPromotePOAToPremilinary(context, strPOAIds, false, strSubmitPoaFormData,
								strProjectType, POAName);
						strResult = strResult.replaceAll("/[\r\n]+/g", DomainConstants.EMPTY_STRING).replace("/[\r\n]+$/g", DomainConstants.EMPTY_STRING);
						strResultList = FrameworkUtil.split(strResult, "~");
						strResult = strResultList.get(0).trim();

						if (strResult.equalsIgnoreCase(COMPLETE)) {
							mapSubmitDate.put(RTAUtilConstants.STR_POANAME, POAName);
							mapSubmitDate.put(TASK_COMPLETE, STR_YES);
							mapSubmitDate.put(RESULT, strResult);							
						} else {
							mapSubmitDate.put(RTAUtilConstants.STR_POANAME, POAName);
							mapSubmitDate.put(TASK_COMPLETE, STR_NO);
							mapSubmitDate.put(RESULT, strResult);
						}
					}
					
					if (strResult.equalsIgnoreCase(COMPLETE)) {
						mapSubmitDate.put(RTAUtilConstants.STR_POANAME, POAName);
						mapSubmitDate.put(TASK_COMPLETE, STR_YES);
						mapSubmitDate.put(RESULT, strResult);
					}

					if (strResult.equals(FAILED)) {
						mapFailedPOA.put(RTAUtilConstants.STR_POANAME, POAName);
						mapFailedPOA.put(TASK_OVERDUE_FLAG, PGWidgetConstants.STRING_TRUE);
						StringList slCauseRanges = overtimeCauseRanges(context);
						mapFailedPOA.put(CAUSE_RANGES, slCauseRanges.toString());
						mapFailedPOA.put(FUNCTION, strApprMatrxFunction);
						mapFailedPOA.put(TASK_COMPLETE, DomainConstants.EMPTY_STRING);
						mapFailedPOA.put(POA_ID, strPOAIds);
						mFailedData.add(mapFailedPOA);	
					}
					mapSubmitDate.put(POA_ID, strPOAIds);
					mSubmitData.add(mapSubmitDate);
				}
			}
			if(mFailedData.size()>0) {
				if (isTransactionActive) {
					ContextUtil.abortTransaction(context);
					isTransactionActive=false;
				}
				outputSubmitData = convertMapListToJSON(context, mFailedData);
			}else {
				outputSubmitData = convertMapListToJSON(context, mSubmitData);
			}
			if (isTransactionActive) {
				ContextUtil.commitTransaction(context);
			}
		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
				throw e;
			}

		}
		return outputSubmitData;
	}
	
	/***
	 * Copied from 	pgRTAPromotePOAToPremilinary.jsp
	 * if(ArtworkConstants.PROJECTTYPE_ARTWORK_PROJECT.equalsIgnoreCase(strProjectType) block
	 * @param context
	 * @param Margslist
	 * @param mArgsMap
	 * @return
	 * @throws Exception
	 */
	public static String pgRTAPromotePOAToPremilinary(Context context, String poaIdsInUI, boolean bTaskOverdue,
			String strSubmitPoaFormData, String strProjectType, String POAName) throws Exception {

		Map mArgsMap = new HashMap();
		mArgsMap.put(POA_IDs_IN_UI, poaIdsInUI);
		mArgsMap.put(TEXT_BOX_VALUE, DomainConstants.EMPTY_STRING);

		String strCauseOverDue = null, strCommentOverDue = null;

		JsonObject jsonSubmitPoaFormData = PGWidgetUtil.getJsonFromJsonString(strSubmitPoaFormData);
		if (!jsonSubmitPoaFormData.isEmpty()) {
			strCauseOverDue = jsonSubmitPoaFormData.getString(TASK_OVER_TIME_CAUSE);
			strCommentOverDue = jsonSubmitPoaFormData.getString(TASK_OVER_TIME_COMMENT);
		}
		mArgsMap.put(CAUSE, strCauseOverDue);
		mArgsMap.put("comment", strCommentOverDue);

		String[] Margslist = JPO.packArgs(mArgsMap);
		String strOut = DomainConstants.EMPTY_STRING;
		if (bTaskOverdue) {
			if (ArtworkConstants.PROJECTTYPE_ARTWORK_PROJECT.equalsIgnoreCase(strProjectType)) {
				strOut = (String) JPO.invoke(context, "pgRTATask", null, "validateCopyMatrixTask", Margslist,
						String.class);
			} else {
				strOut = "Pass~";
			}
			return strOut;

		} else if (ArtworkConstants.PROJECTTYPE_ARTWORK_PROJECT.equalsIgnoreCase(strProjectType)) {
			boolean bRetStateValue = false;
			String[] argsPOAIdArray = null;
			String sPOAValidtoProcess = DomainConstants.EMPTY_STRING;
			String sUserNotification = DomainConstants.EMPTY_STRING;
			String strAlertMessage = DomainConstants.EMPTY_STRING;

			sUserNotification = i18nNow.getI18nString("emxAWL.ArtworkPromotion.Restricted", "emxAWLStringResource",
					context.getSession().getLanguage());

			HashMap hmCheckPOA = (HashMap) JPO.invoke(context, "pgAAA_Util", null,
					"mCheckDataAddedForIntegAttributeCheck", Margslist, HashMap.class);
			StringList slPOAIdToProcess = (StringList) hmCheckPOA.get("ValidPOA");
			StringList slPOAIdForAlert = (StringList) hmCheckPOA.get("AlertPOA");
			if (BusinessUtil.isNullOrEmpty(slPOAIdForAlert)) {
				strOut = (String) JPO.invoke(context, "pgRTATask", null, "setAttributepgRightCopyApprovalStatus",
						Margslist, String.class);
			} else {
				if (BusinessUtil.isNullOrEmpty(slPOAIdToProcess)) {
					StringBuilder sbValidPOA = new StringBuilder();
					for (String sEachValidPOA : slPOAIdToProcess) {
						sbValidPOA.append(sEachValidPOA);
						sbValidPOA.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
					}
					if (BusinessUtil.isNotNullOrEmpty(sbValidPOA.toString())) {
						sPOAValidtoProcess = (sbValidPOA.toString()).substring(0, (sbValidPOA.toString()).length() - 1);
						mArgsMap.put(POA_IDs_IN_UI, sPOAValidtoProcess);
						String[] saMargslist_Modified = JPO.packArgs(mArgsMap);
						strOut = (String) JPO.invoke(context, "pgRTATask", null,
								"setAttributepgRightCopyApprovalStatus", saMargslist_Modified, String.class);
					}
				}
				StringBuilder sbInValidPOA = new StringBuilder();
				DomainObject domPOAObj = null;
				String sNamePOA = DomainConstants.EMPTY_STRING;
				for (String sEachInValidPOA : slPOAIdForAlert) {
					domPOAObj = DomainObject.newInstance(context, sEachInValidPOA);
					sNamePOA = domPOAObj.getInfo(context, DomainConstants.SELECT_NAME);
					sbInValidPOA.append(sNamePOA);
					sbInValidPOA.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
				}
				if (BusinessUtil.isNotNullOrEmpty(sbInValidPOA.toString())) {
					String sPOAINValidAlert = (sbInValidPOA.toString()).substring(0,
							(sbInValidPOA.toString()).length() - 1);

					strAlertMessage = "Notice :";
					strAlertMessage += sUserNotification;
					strAlertMessage += " For " + POAName;
					strOut = strAlertMessage;
					throw new Exception(strOut);
				}
			}
		}

		return strOut;
	}

	public static StringList overtimeCauseRanges(Context context) throws Exception {
		Map programMap = new HashMap();
		Map fieldMap = new HashMap();
		Map settings = new HashMap();
		settings.put(PG_PICKLIST_NAME, "pgAAACauseList");
		fieldMap.put(SETTINGS,settings);
		programMap.put(FIELD_MAP,fieldMap);
		StringList slDisplayValues = new StringList();
		Map mpPickList = (Map)JPO.invoke(context, "pgPLPicklist", null, "getCausePickListRangeValues", JPO.packArgs(programMap), Map.class);
		if(mpPickList != null && !mpPickList.isEmpty()){
			slDisplayValues = (StringList)mpPickList.get("field_display_choices");
			slDisplayValues.sort();
		}
		return slDisplayValues;
	}
	public static String convertArrayToJsonArray(Context context,StringList arr) {
		JsonArrayBuilder jsonArrBldr = Json.createArrayBuilder();
		for (int i = 0; i < arr.size(); ++i) {
			jsonArrBldr.add((String) arr.get(i));
		}
		return jsonArrBldr.build().toString();
	}

	public static String convertMapListToJSON(Context context, MapList mlReturn) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrData = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjKeyVariant = null;
		String strKey = null;
		String strValue = DomainConstants.EMPTY_STRING;
		Map<String, String> mapData = null;
		try {
			for (int i = 0; i < mlReturn.size(); i++) {
				mapData = (Map) mlReturn.get(i);
				jsonObjKeyVariant = Json.createObjectBuilder();
				for (Map.Entry<String, String> entryMCLC : mapData.entrySet()) {
					strKey = entryMCLC.getKey();
					strValue = entryMCLC.getValue();
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
}
