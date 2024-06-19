package com.pg.widgets.editCopyList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;

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
import com.matrixone.apps.awl.enumeration.AWLAttribute;
import com.matrixone.apps.awl.enumeration.AWLPolicy;
import com.matrixone.apps.awl.enumeration.AWLRel;
import com.matrixone.apps.awl.enumeration.AWLState;
import com.matrixone.apps.awl.enumeration.AWLType;
import com.matrixone.apps.awl.util.AWLConstants;
import com.matrixone.apps.awl.util.AWLPreferences;
import com.matrixone.apps.awl.util.AWLPropertyUtil;
import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.awl.util.ArtworkUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.awl.util.RouteUtil;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.common.Company;
import com.matrixone.apps.common.InboxTask;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.cpd.dao.CPDCache;
import com.matrixone.apps.cpd.dao.Country;
import com.matrixone.apps.cpd.dao.Language;
import com.matrixone.apps.cpd.dao.Region;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.productline.ProductLineConstants;
import com.pg.v3.custom.pgV3Constants;
import com.pg.widgets.rtautil.RTAUtil;
import com.pg.widgets.rtautil.RTAUtilConstants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Policy;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class EditCLUtil {

	static String MCSURL = DomainConstants.EMPTY_STRING;
	public static final String sortingOrder = "|-|H315|H318|H319|H412|EUH208|EUH210|P101|P102|P280|P301+P310|P301+P312|P301+P330+P331|P302+P352|P305|P310|P312|P351|P501|";

	private static final Logger logger = Logger.getLogger(EditCLUtil.class.getName());

	private EditCLUtil() {
		throw new IllegalStateException("Utility class");
	}

	/***
	 * This method returns the type of Copy Element Selected
	 * 
	 * @param context
	 * @param mLCInfo
	 * @return
	 * @throws Exception
	 */
	public static String getCopyElementType(Context context, Map<String, String> mLCInfo) throws Exception {
		String strFlag = null;
		if ((mLCInfo.get(AWLAttribute.TRANSLATE.getSel(context))).equalsIgnoreCase(AWLConstants.RANGE_YES)
				&& (mLCInfo.get(AWLAttribute.INLINE_TRANSLATION.getSel(context)))
						.equalsIgnoreCase(AWLConstants.RANGE_NO)) {
			strFlag = EditCLConstants.STR_TRANSLATE;
		} else if ((mLCInfo.get(AWLAttribute.TRANSLATE.getSel(context))).equalsIgnoreCase(AWLConstants.RANGE_NO)) {
			strFlag = EditCLConstants.STR_NOTRANSLATE;
		} else if ((mLCInfo.get(AWLAttribute.INLINE_TRANSLATION.getSel(context)))
				.equalsIgnoreCase(AWLConstants.RANGE_YES)) {
			strFlag = EditCLConstants.STR_INLINETYPE;
		} else if ((mLCInfo.get("type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]"))
				.equalsIgnoreCase(AWLConstants.RANGE_TRUE)) {
			strFlag = EditCLConstants.STR_GRAPHICTYPE;
		} else {
			strFlag = EditCLConstants.STR_NONE;
		}
		return strFlag;
	}

	/***
	 * 
	 * @param slMCNotes
	 * @return
	 * @throws Exception
	 */
	public static String getNotesForMC(StringList slMCNotes) throws Exception {
		StringList slMCNotesNew = new StringList();
		if (BusinessUtil.isNotNullOrEmpty(slMCNotes)) {
			for (int i = 0; i < slMCNotes.size(); i++) {
				String strNote = slMCNotes.get(i).trim();
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
	 * This method returns the CopyList Master Copies data
	 * 
	 * @param context
	 * @param mlCopyListMCs
	 * @param mlCopyListLCs
	 * @param mapFinalMCInfo
	 * @param strMCIdsList
	 * @param copylist
	 */
	public static void getCopyListMCDetails(Context context, MapList mlCopyListMCs, MapList mlCopyListLCs,
			HashMap<String, Object> mapFinalMCInfo, StringList strMCIdsList, CopyList copylist) throws Exception {
		String strImage = null;
		int iCopyListMCsSize = mlCopyListMCs.size();
		for (int i = 0; i < iCopyListMCsSize; i++) {
			HashMap<Object, Object> mapInfo = new HashMap<>();
			Map<String, String> mMCInfo = (Map<String, String>) mlCopyListMCs.get(i);
			String strFlag = getCopyElementType(context, mMCInfo);
			String strMCId = mMCInfo.get(DomainConstants.SELECT_ID);
			MapList mlMCLang = new MapList();

			if (BusinessUtil.isNotNullOrEmpty(mlCopyListLCs)) {
				for (int j = 0; j < mlCopyListLCs.size(); j++) {
					Map eachLCs = (Map) mlCopyListLCs.get(j);
					String strArtElemFromId = (String) eachLCs
							.get("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id");
					if (strMCId.equals(strArtElemFromId)) {
						mlMCLang.add(eachLCs);
					}
				}
			}

			StringList slMCLang = BusinessUtil.toStringList(mlMCLang, AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context));
			StringList slMCNotes = BusinessUtil.toStringList(mlMCLang, AWLAttribute.NOTES.getSel(context));
			String strMCNotes = getNotesForMC(slMCNotes);
			JsonArrayBuilder jsonArrRevData = getFormattedRevisions(mMCInfo.get(PGWidgetConstants.KEY_REVISIONS),
					mMCInfo);
			if (!strMCIdsList.contains(strMCId)) {
				strMCIdsList.add(strMCId);
				mapInfo.put(DomainConstants.SELECT_ID, strMCId);
				mapInfo.put(DomainConstants.SELECT_NAME, mMCInfo.get(DomainConstants.SELECT_NAME));
				mapInfo.put(DomainConstants.SELECT_TYPE, mMCInfo.get(DomainConstants.SELECT_TYPE));
				mapInfo.put(DomainConstants.SELECT_REVISION, mMCInfo.get(DomainConstants.SELECT_REVISION));
				mapInfo.put(DomainConstants.SELECT_CURRENT, mMCInfo.get(DomainConstants.SELECT_CURRENT));
				mapInfo.put(EditCLConstants.KEY_TITLE, mMCInfo.get(AWLAttribute.MARKETING_NAME.getSel(context)));
				mapInfo.put(EditCLConstants.KEY_FLAG, strFlag);
				mapInfo.put(PGWidgetConstants.KEY_REVISIONS, jsonArrRevData.build().toString());
				mapInfo.put(EditCLConstants.KEY_LAST, mMCInfo.get(EditCLConstants.KEY_LAST));
				if (strFlag.equals(EditCLConstants.STR_GRAPHICTYPE)) {
					try {
						String[] arrayOfString = new String[2];
						arrayOfString[0] = strMCId;
						arrayOfString[1] = MCSURL;
						strImage = JPO.invoke(context, "AWLGraphicsElementUI", null, "getGraphicImageURLforPOAAction",
								arrayOfString, String.class);
					} catch (Exception e) {
						throw e;
					}

					mapInfo.put(EditCLConstants.KEY_CONTENT, strImage);
				} else {
					mapInfo.put(EditCLConstants.KEY_CONTENT, mMCInfo.get(
							"from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].to.attribute[Copy Text_RTE]"));
				}
				mapInfo.put(EditCLConstants.KEY_LANGUAGES,
						FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
				mapInfo.put(EditCLConstants.STR_LANG,
						FrameworkUtil.join(slMCLang, PGWidgetConstants.KEY_COMMA_SEPARATOR));
				mapInfo.put(EditCLConstants.STR_INSSEQ, mMCInfo.get(AWLAttribute.INSTANCE_SEQUENCE.getSel(context)));
				mapInfo.put(EditCLConstants.STR_ORDER,
						mMCInfo.get(AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context)));
				mapInfo.put(EditCLConstants.STR_NOTES, strMCNotes);
				mapInfo.put(EditCLConstants.KEY_PGSUBCOPYTYPE,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGSUBCOPYTYPE + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTACECATEGORY,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECATEGORY + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTACEBRAND,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACEBRAND + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTACLASSIFICATION,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACLASSIFICATION + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTAPLANTCODE,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAPLANTCODE + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTACOPYELEMENTCATEGORY,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACOPYELEMENTCATEGORY + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTACOPYELEMENTTYPE,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACOPYELEMENTTYPE + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTADESCRIPTION,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTADESCRIPTION + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTAFIXEDORVARIABLE,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAFIXEDORVARIABLE + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTACECOUNTRY,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTACECOUNTRY + "]"));
				mapInfo.put(EditCLConstants.KEY_PGRTAEUCLASSIFICATION,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTAEUCLASSIFICATION + "]"));
				mapInfo.put(EditCLConstants.ATTR_INSTRUCTIONS,
						mMCInfo.get("attribute[" + EditCLConstants.ATTR_INSTRUCTIONS + "]"));
				mapInfo.put(EditCLConstants.KEY_PGConfirmAuthorApproverAssignee,
						mMCInfo.get("attribute[" + EditCLConstants.ATTRIBUTE_PGConfirmAuthorApproverAssignee + "]"));
				mapFinalMCInfo.put(strMCId, mapInfo);
			}
		}
	}

	/**
	 * This method returns Copy List Local copies data
	 * 
	 * @param context
	 * @param mlCopyListLCs
	 * @param mapFinalLCInfo
	 * @param mapFinalMCInfo
	 * @param strLCIdsList
	 * @param strMCIdsList
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void getCopyListLCDetails(Context context, MapList mlCopyListLCs,
			HashMap<String, Object> mapFinalLCInfo, HashMap<String, Object> mapFinalMCInfo, StringList strLCIdsList,
			StringList strMCIdsList) throws Exception {
		String strArtworkMasterSelectable = ArtworkContent.getArtworkMasterIdSel(context);
		int iCopyListLCsSize = mlCopyListLCs.size();
		int iMCIdsListSize = strMCIdsList.size();
		for (int iMC = 0; iMC < iMCIdsListSize; iMC++) {
			MapList mlCopyListMCs = new MapList();
			HashMap<String, Object> mapMCInfo = new HashMap<>();
			HashMap<String, Object> mapLangInfo = new HashMap<>();
			String strFinalMCId = strMCIdsList.get(iMC);
			for (int i = 0; i < iCopyListLCsSize; i++) {
				HashMap<String, Object> mapInfo = new HashMap<>();
				Map<String, String> mLCInfo = (Map<String, String>) mlCopyListLCs.get(i);
				String strLCId = mLCInfo.get(DomainConstants.SELECT_ID);
				String strMCId = mLCInfo.get(strArtworkMasterSelectable);
				if (strFinalMCId.equals(strMCId)) {
					String strMCLCIdsKey = strMCId + PGWidgetConstants.KEY_UNDERSCORE + strLCId;
					if (!strLCIdsList.contains(strMCLCIdsKey)) {
						strLCIdsList.add(strMCLCIdsKey);
						JsonArrayBuilder jsonArrRevData = getFormattedRevisions(
								mLCInfo.get(PGWidgetConstants.KEY_REVISIONS), mLCInfo);
						mapInfo.put(PGWidgetConstants.KEY_REVISIONS, jsonArrRevData.build().toString());
						mapInfo.put(EditCLConstants.KEY_LAST, mLCInfo.get(EditCLConstants.KEY_LAST));
						mapInfo.put(DomainConstants.SELECT_ID, strLCId);
						mapInfo.put(DomainConstants.SELECT_NAME, mLCInfo.get(DomainConstants.SELECT_NAME));
						mapInfo.put(DomainConstants.SELECT_REVISION, mLCInfo.get(DomainConstants.SELECT_REVISION));
						mapInfo.put(DomainConstants.SELECT_TYPE, mLCInfo.get(DomainConstants.SELECT_TYPE));
						mapInfo.put(EditCLConstants.KEY_ISBASECOPY,
								mLCInfo.get(AWLAttribute.IS_BASE_COPY.getSel(context)));
						mapInfo.put(EditCLConstants.KEY_PARENTTYPE,
								mLCInfo.get("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type"));
						mapInfo.put(EditCLConstants.KEY_PARENT_ID,
								mLCInfo.get("to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id"));
						mapInfo.put(DomainConstants.SELECT_CURRENT, mLCInfo.get(DomainConstants.SELECT_CURRENT));
						mapInfo.put(EditCLConstants.KEY_TITLE,
								mLCInfo.get(AWLAttribute.MARKETING_NAME.getSel(context)));
						String strValidityDate = (String) mLCInfo
								.get("attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]");
						mapInfo.put(EditCLConstants.ATTR_VALIDITY_DATE, strValidityDate);
						String strDstribType = (String) mLCInfo
								.get("attribute[" + EditCLConstants.ATTRIBUTE_PGRTADISTRIBTYPE + "]");
						mapInfo.put(EditCLConstants.KEY_PGRTADISTRIBTYPE, strDstribType);
						mapInfo.put(EditCLConstants.KEY_CONTENT,
								mLCInfo.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
						mapInfo.put(EditCLConstants.KEY_LANGUAGES,
								mLCInfo.get(AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context)));
						String strNotes = (String) mLCInfo.get(AWLAttribute.NOTES.getSel(context));
						mapInfo.put(EditCLConstants.STR_NOTES, strNotes);
						String strLangId = PGWidgetUtil.extractMultiValueSelect(mLCInfo,
								"from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
						mapInfo.put(EditCLConstants.STR_LANGUAGE_ID, strLangId);
						if (com.matrixone.apps.cpn.util.BusinessUtil.isNotNullOrEmpty(strLangId)) {
							mapInfo.put(strLangId,
									mLCInfo.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
							mapLangInfo.put(strLangId,
									mLCInfo.get("attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]"));
							mapLangInfo.put(strLangId + PGWidgetConstants.KEY_UNDERSCORE + EditCLConstants.STR_NOTES,
									strNotes);
							mapLangInfo.put(
									strLangId + PGWidgetConstants.KEY_UNDERSCORE + EditCLConstants.ATTR_VALIDITY_DATE,
									strValidityDate);
							mapLangInfo.put(
									strLangId + PGWidgetConstants.KEY_UNDERSCORE + EditCLConstants.KEY_PGRTADISTRIBTYPE,
									strDstribType);
						}
						mlCopyListMCs.add(mapInfo);
					}
					mapFinalMCInfo.put(strMCLCIdsKey, mapInfo);
				}
			}
			mapMCInfo = (HashMap<String, Object>) mapFinalMCInfo.get(strFinalMCId);
			mapMCInfo.put(EditCLConstants.KEY_LANGUAGEINFO, mapLangInfo);
			mapFinalMCInfo.put(strFinalMCId, mapMCInfo);
			mapFinalLCInfo.put(strFinalMCId, mlCopyListMCs);
		}
	}

	/***
	 * This method converts Map/MapList to JSON format
	 * 
	 * @param context
	 * @param mapFinalMCInfo
	 * @param mapFinalLCInfo
	 * @param strMCIdsList
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getFinalMCLCDetailsInJSONFormat(Context context, Map<String, Object> mapFinalMCInfo,
			Map<String, Object> mapFinalLCInfo, StringList strMCIdsList) throws Exception {
		JsonArrayBuilder jsonArrCopyListData = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrErrCopyListData = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjErrKeyVariant = Json.createObjectBuilder();

		String strValue = DomainConstants.EMPTY_STRING;
		try {
			for (Entry<String, Object> entry : mapFinalMCInfo.entrySet()) {
				JsonArrayBuilder jsonArrHier = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrData = Json.createArrayBuilder();
				JsonObjectBuilder jsonObjKeyVariant = Json.createObjectBuilder();
				String strKey = entry.getKey();
				Map<String, String> mapMCData = (Map<String, String>) mapFinalMCInfo.get(strKey);
				if (strKey.contains(PGWidgetConstants.KEY_UNDERSCORE)) {
					StringList slMCLCHierarchy = FrameworkUtil.split(strKey, PGWidgetConstants.KEY_UNDERSCORE);
					jsonArrHier.add(slMCLCHierarchy.get(0));
					jsonArrHier.add(slMCLCHierarchy.get(1));
				} else {
					jsonArrHier.add(strKey);
					MapList mlLCData = (MapList) mapFinalLCInfo.get(strKey);
					for (int j = 0; j < mlLCData.size(); j++) {
						Map<String, String> mapLCData = (Map<String, String>) mlLCData.get(j);
						JsonObjectBuilder jsonLCObjKeyVariant = Json.createObjectBuilder();
						for (Entry<String, String> entryMCLC : mapLCData.entrySet()) {
							strKey = entryMCLC.getKey();
							strValue = entryMCLC.getValue();
							if (BusinessUtil.isNotNullOrEmpty(strValue)) {
								jsonLCObjKeyVariant.add(strKey, strValue);
							} else {
								jsonLCObjKeyVariant.add(strKey, DomainConstants.EMPTY_STRING);
							}
						}
						jsonArrData.add(jsonLCObjKeyVariant.build());
					}
					jsonObjKeyVariant.add(EditCLConstants.KEY_LCES, jsonArrData.build());
				}
				jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());

				for (Entry<?, ?> entryMCLC : mapMCData.entrySet()) {
					if (entryMCLC.getValue() instanceof Map) {
						Map<String, String> temp = (Map<String, String>) entryMCLC.getValue();
						for (Entry<String, String> entryMCLCTemp : temp.entrySet()) {
							strKey = entryMCLCTemp.getKey();
							strValue = entryMCLCTemp.getValue();
							if (BusinessUtil.isNotNullOrEmpty(strValue)) {
								jsonObjKeyVariant.add(strKey, strValue);
							} else {
								jsonObjKeyVariant.add(strKey, DomainConstants.EMPTY_STRING);
							}
						}
					} else if (entryMCLC.getValue() instanceof String) {
						strValue = (String) entryMCLC.getValue();
						if (BusinessUtil.isNotNullOrEmpty(strValue)) {
							jsonObjKeyVariant.add((String) entryMCLC.getKey(), strValue);
						} else {
							jsonObjKeyVariant.add((String) entryMCLC.getKey(), DomainConstants.EMPTY_STRING);
						}
					}

				}
				jsonArrCopyListData.add(jsonObjKeyVariant.build());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonArrErrCopyListData.add(jsonObjErrKeyVariant);
		}
		return jsonArrCopyListData;
	}

	/***
	 * This method returns formatted revision data
	 * 
	 * @param revisions
	 * @param mLCInfo
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getFormattedRevisions(Object revisions, Map<String, String> mLCInfo)
			throws Exception {
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonArrayBuilder jsonArrRevData = Json.createArrayBuilder();
		String strRevId = null;
		String strRevName = null;
		if (revisions instanceof StringList) {
			StringList slRevisions = (StringList) revisions;
			for (String strRev : slRevisions) {
				strRevId = mLCInfo.get("revisions[" + strRev + "].id");
				jsonObjKeyVariant = Json.createObjectBuilder();
				jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, strRev);
				jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, strRev);
				jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strRevId);
				jsonArrRevData.add(jsonObjKeyVariant);
			}
		} else {
			strRevName = (String) revisions;
			strRevId = mLCInfo.get("revisions[" + strRevName + "].id");
			jsonObjKeyVariant = Json.createObjectBuilder();
			jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, strRevName);
			jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, strRevName);
			jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strRevId);
			jsonArrRevData.add(jsonObjKeyVariant);
		}
		return jsonArrRevData;
	}

	/***
	 * This method returns the CopyList Header data
	 * 
	 * @param context
	 * @param strCopyListId
	 * @return
	 * @throws Exception
	 */
	public static String getCopyListHeaderData(Context context, String strCopyListId) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strCopyListId);
		strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
		CopyList copylist = new CopyList(strCopyListId);
		String strOutput = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			String strCLCountryName = AWLUtil.strcat("from[", AWLRel.COPY_LIST_COUNTRY.get(context),
					"].to." + DomainConstants.SELECT_NAME + "");
			String strCLACountryId = AWLUtil.strcat("from[", AWLRel.COPY_LIST_COUNTRY.get(context),
					"].to." + DomainConstants.SELECT_ID + "");
			String strCLACountryPhyId = AWLUtil.strcat("from[", AWLRel.COPY_LIST_COUNTRY.get(context),
					"].to." + DomainConstants.SELECT_PHYSICAL_ID + "");
			String strIsPOAAtBrandLevel = AWLUtil.strcat("to[", AWLRel.ASSOCIATED_COPY_LIST.get(context),
					"].from.type");
			String strSelParentClId = "to["+EditCLConstants.RELATIONSHIP_PG_RTA_COPYLIST_CLONED_FROM+"].from.id";

			StringList clSelects = new StringList();
			clSelects.add(DomainConstants.SELECT_ID);
			clSelects.add(DomainConstants.SELECT_TYPE);
			clSelects.add(DomainConstants.SELECT_NAME);
			clSelects.add(DomainConstants.SELECT_CURRENT);
			clSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			clSelects.add(DomainConstants.SELECT_OWNER);
			clSelects.add(DomainConstants.SELECT_MODIFIED);
			clSelects.add(DomainConstants.SELECT_DESCRIPTION);
			clSelects.add(strIsPOAAtBrandLevel);
			clSelects.add(strCLCountryName);
			clSelects.add(strCLACountryId);
			clSelects.add(strCLACountryPhyId);
			clSelects.add(strSelParentClId);

			clSelects.add(EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO);
			Map<Object, StringList> mapCopyListInfo = BusinessUtil.getInfoList(context, strCopyListId, clSelects);
			fetchCLRegionInfo(context, strCopyListId, mapCopyListInfo);			
			MapList mlCountryLangInfo = getCountryLanguageInfo(context, mapCopyListInfo, strCLCountryName,
					strCLACountryId, strCLACountryPhyId, copylist);
			if(UIUtil.isNotNullAndNotEmpty(BusinessUtil.getString(mapCopyListInfo, strSelParentClId)) && 
					AWLState.RELEASE.get(context, AWLPolicy.COPY_LIST).equals(BusinessUtil.getString(mapCopyListInfo, DomainConstants.SELECT_CURRENT))) {
				mapCopyListInfo.put("CLClonedFrom", StringList.create(BusinessUtil.getString(mapCopyListInfo, strSelParentClId)));
			}
			strOutput = getFinalCopyListHeaderInJSONFormat(context, mlCountryLangInfo, mapCopyListInfo);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return strOutput;
	}

	/***
	 * This method is called to fetch Languages and Countries
	 * 
	 * @param context
	 * @param mapCopyListInfo
	 * @param strCLCountryName
	 * @param strCLACountryId
	 * @param strCLACountryPhyId
	 * @param copylist
	 * @return
	 * @throws Exception
	 */
	public static MapList getCountryLanguageInfo(Context context, Map<Object, StringList> mapCopyListInfo,
			String strCLCountryName, String strCLACountryId, String strCLACountryPhyId, CopyList copylist)
			throws Exception {
		StringList slCountryNames = new StringList();
		StringList slCountryIds = new StringList();
		StringList slCountryPhyIds = new StringList();
		StringList slLangNames = new StringList();
		StringList slLangIds = new StringList();
		StringList slLangPhyIds = new StringList();
		StringList slCountryLangIds = null;
		StringList slLangSeq = new StringList();
		Object object1 = mapCopyListInfo.get(strCLCountryName);
		if (object1 != null) {
			slCountryNames = mapCopyListInfo.get(strCLCountryName);
			slCountryIds = mapCopyListInfo.get(strCLACountryId);
			slCountryPhyIds = mapCopyListInfo.get(strCLACountryPhyId);
		}
		MapList mlCopyListLangs = copylist.related(AWLType.LOCAL_LANGUAGE, AWLRel.COPY_LIST_LOCAL_LANGUAGE).id()
				.sel(DomainConstants.SELECT_PHYSICAL_ID).relid()
				.relAttr((AWLAttribute[]) AWLUtil.toArray((Object[]) new AWLAttribute[] { AWLAttribute.SEQUENCE }))
				.query(context);
		mlCopyListLangs.sort(DomainConstants.SELECT_NAME, PGWidgetConstants.ASCENDING, "string");
		int iCLLangsSize = mlCopyListLangs.size();
		Map<String, String> mIDName = new HashMap<>();
		for (int iCLLang = 0; iCLLang < iCLLangsSize; iCLLang++) {
			Map<String, String> mapCLLang = (Hashtable<String, String>) mlCopyListLangs.get(iCLLang);
			slLangNames.add((String) mapCLLang.get(DomainConstants.SELECT_NAME));
			slLangIds.add((String) mapCLLang.get(DomainConstants.SELECT_ID));
			slLangPhyIds.add((String) mapCLLang.get(DomainConstants.SELECT_PHYSICAL_ID));
			slLangSeq.add((String) mapCLLang.get(AWLAttribute.SEQUENCE.getSel(context)));
			mIDName.put((String) mapCLLang.get(DomainConstants.SELECT_ID),
					(String) mapCLLang.get(DomainConstants.SELECT_NAME));
		}

		MapList mlCountryLangInfo = new MapList();
		HashMap mCLLangMotherChildInfo = getCLLangMotherChildInfo(context, PGWidgetUtil
				.extractMultiValueSelect(mapCopyListInfo, EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO));

		HashMap mMotherChildInfo = (mCLLangMotherChildInfo.containsKey(EditCLConstants.KEY_MOTHERCHILD))
				? (HashMap) mCLLangMotherChildInfo.get(EditCLConstants.KEY_MOTHERCHILD)
				: new HashMap<>();
		HashMap mChildMotherInfo = (mCLLangMotherChildInfo.containsKey(EditCLConstants.KEY_CHILDMOTHER))
				? (HashMap) mCLLangMotherChildInfo.get(EditCLConstants.KEY_CHILDMOTHER)
				: new HashMap<>();
		Map mMotherInfo = null;
		for (int iCountry = 0; iCountry < slCountryIds.size(); iCountry++) {
			MapList mlCountryLangIdName = new MapList();
			Country country = CPDCache.getCountry(slCountryIds.get(iCountry));
			List<Language> list = country.getLanguages(context);
			HashMap<Object, Object> hashMap = new HashMap<>();
			HashMap<Object, Object> mapCountry = new HashMap<>();
			slCountryLangIds = new StringList();
			for (Language language : list) {
				if (!slCountryLangIds.contains(language.getObjectId())) {
					slCountryLangIds.add(language.getObjectId());
				}
			}
			for (int i = 0; i < slLangIds.size(); i++) {
				String strLangId = slLangIds.get(i);
				Map<Object, Object> mapLangInfo = new HashMap();
				if (slCountryLangIds.contains(strLangId)) {
					mapLangInfo.put(PGWidgetConstants.KEY_OBJECT_ID, strLangId);
					mapLangInfo.put(DomainConstants.SELECT_NAME, slLangNames.get(i));
					mapLangInfo.put(DomainConstants.SELECT_PHYSICAL_ID, slLangPhyIds.get(i));
					mapLangInfo.put(EditCLConstants.KEY_SEQ, slLangSeq.get(i));

					if (mMotherChildInfo.containsKey(strLangId)) {
						mapLangInfo.put(EditCLConstants.KEY_CHILD, mMotherChildInfo.get(strLangId));
					} else if (mChildMotherInfo.containsKey(strLangId)) {
						mMotherInfo = new HashMap();
						mMotherInfo.put(DomainConstants.SELECT_ID,
								((HashMap) mChildMotherInfo.get(strLangId)).get(DomainConstants.SELECT_ID));
						mMotherInfo.put(DomainConstants.SELECT_NAME, (String) mIDName
								.get(((HashMap) mChildMotherInfo.get(strLangId)).get(DomainConstants.SELECT_ID)));
						mapLangInfo.put(EditCLConstants.KEY_MOTHER, mMotherInfo);
					}

					mlCountryLangIdName.add(mapLangInfo);
				}
			}
			mapCountry.put(PGWidgetConstants.KEY_OBJECT_ID, slCountryIds.get(iCountry));
			mapCountry.put(DomainConstants.SELECT_NAME, slCountryNames.get(iCountry));
			mapCountry.put(DomainConstants.SELECT_PHYSICAL_ID, slCountryPhyIds.get(iCountry));
			hashMap.put(EditCLConstants.KEY_COUNTRYINFO, mapCountry);
			hashMap.put(EditCLConstants.KEY_LANGUAGEINFO, mlCountryLangIdName);
			mlCountryLangInfo.add(hashMap);
		}
		return mlCountryLangInfo;
	}

	/***
	 * This method returns CopyList Header Data in JSON Format
	 * 
	 * @param context
	 * @param mlCountryLangInfo
	 * @param mapCopyListInfo
	 * @return
	 * @throws Exception
	 */
	public static String getFinalCopyListHeaderInJSONFormat(Context context, MapList mlCountryLangInfo,
			Map<Object, StringList> mapCopyListInfo) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonCopyListData = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrCopyListData = Json.createArrayBuilder();

		String strIsPOAAtBrandLevel = AWLUtil.strcat("to[", AWLRel.ASSOCIATED_COPY_LIST.get(context), "].from.type");
		String strBRAND = AWLType.BRAND.get(context);

		String strKey = null;
		String strValue = DomainConstants.EMPTY_STRING;

		try {
			for (Entry<Object, StringList> entryMCLC : mapCopyListInfo.entrySet()) {
				if (entryMCLC.getValue() instanceof StringList) {
					strValue = (entryMCLC.getValue()).get(0);
					if (BusinessUtil.isNotNullOrEmpty(strValue)) {
						if (entryMCLC.getKey().equals(strIsPOAAtBrandLevel)) {
							jsonCopyListData.add(EditCLConstants.KEY_BRANDLEVEL,
									(BusinessUtil.isNotNullOrEmpty(strValue) && strValue.equalsIgnoreCase(strBRAND)));
						} else {
							jsonCopyListData.add((String) entryMCLC.getKey(), strValue);
						}
					} else {
						jsonCopyListData.add((String) entryMCLC.getKey(), DomainConstants.EMPTY_STRING);
					}
				}
			}

			for (int j = 0; j < mlCountryLangInfo.size(); j++) {
				JsonObjectBuilder jsonObjKeyVariant = Json.createObjectBuilder();
				JsonArrayBuilder jsonLangArrData = Json.createArrayBuilder();
				Map<String, String> mapCountryLangData = (Map<String, String>) mlCountryLangInfo.get(j);
				JsonObjectBuilder jsonCountryLangObjKeyVariant = Json.createObjectBuilder();
				for (Entry<?, ?> entryMCLC : mapCountryLangData.entrySet()) {
					if (entryMCLC.getValue() instanceof Map) {
						Map<String, String> temp = (Map<String, String>) entryMCLC.getValue();
						for (Entry<String, String> entryMCLCTemp : temp.entrySet()) {
							strKey = entryMCLCTemp.getKey();
							strValue = entryMCLCTemp.getValue();
							if (BusinessUtil.isNotNullOrEmpty(strValue)) {
								jsonCountryLangObjKeyVariant.add(strKey, strValue);
							} else {
								jsonCountryLangObjKeyVariant.add(strKey, DomainConstants.EMPTY_STRING);
							}
						}
					} else if (entryMCLC.getValue() instanceof MapList) {
						MapList mlTemp = (MapList) entryMCLC.getValue();
						Object objValue = null;
						for (int i = 0; i < mlTemp.size(); i++) {
							Map<String, Object> mapLangData = (Map<String, Object>) mlTemp.get(i);
							JsonObjectBuilder jsonLangObjKeyVariant = Json.createObjectBuilder();
							for (Entry<String, Object> entryMCLCLang : mapLangData.entrySet()) {
								strKey = (String) entryMCLCLang.getKey();
								objValue = entryMCLCLang.getValue();
								if (objValue instanceof MapList) {
									jsonLangObjKeyVariant.add(strKey,
											PGWidgetUtil.converMaplistToJsonArray(context, (MapList) objValue));
								} else if (objValue instanceof Map) {
									jsonLangObjKeyVariant.add(strKey,
											PGWidgetUtil.getJSONFromMap(context, (Map) objValue));
								} else {
									jsonLangObjKeyVariant.add(strKey,
											UIUtil.isNotNullAndNotEmpty((String) objValue) ? (String) objValue
													: DomainConstants.EMPTY_STRING);
								}
							}
							jsonLangArrData.add(jsonLangObjKeyVariant.build());
						}
					}
				}
				jsonObjKeyVariant.add(EditCLConstants.KEY_COUNTRYINFO, jsonCountryLangObjKeyVariant);
				jsonObjKeyVariant.add(EditCLConstants.KEY_LANGUAGEINFO, jsonLangArrData.build());
				jsonArrCopyListData.add(jsonObjKeyVariant.build());
			}
			jsonCopyListData.add(PGWidgetConstants.KEY_DATA, jsonArrCopyListData.build());
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonCopyListData.build().toString();
	}

	/***
	 * This method is called to connect Master Copy and CopyList
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String connectMCtoCopyList(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutput = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
			String strMCIds = jsonInputData.getString(EditCLConstants.MC_IDS);
			String strMode = jsonInputData.getString(EditCLConstants.MODE);
			String strInstanceSequence = jsonInputData.getString(EditCLConstants.STR_INSSEQ);
			StringList mcIdList = FrameworkUtil.split(strMCIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			StringList selectList = StringList.create(DomainConstants.SELECT_ID, EditCLConstants.KEY_LAST,
					PGWidgetConstants.KEY_REVISIONS, EditCLConstants.STR_REVISIONS_ID, DomainConstants.SELECT_NAME,
					DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION, DomainConstants.SELECT_CURRENT,
					AWLAttribute.MARKETING_NAME.getSel(context));
			Map<String, String> mapMCData = null;
			String MCName = null;

			Map excludeMCEError = new HashMap(); 
			Map excludeMCEWarning = new HashMap();
			JsonArrayBuilder jArrIncludeList = Json.createArrayBuilder();
			JsonArrayBuilder jArrIncludeListFinal = Json.createArrayBuilder();
			StringList slIncludeNames = new StringList();
			Map MCEMap = new HashMap();
			MapList editableObjMapList = BusinessUtil.getInfo(context, mcIdList, selectList);
			for (int i = 0; i < editableObjMapList.size(); i++) {
				mapMCData = (Map<String, String>) editableObjMapList.get(i);
				MCName = mapMCData.get(DomainConstants.SELECT_NAME);
				MCEMap.put(MCName, mapMCData);
			}
			logger.log(Level.INFO, "MCEMap::" + MCEMap);
			if (BusinessUtil.isNotNullOrEmpty(strCopyListId)) {
				CopyList cl = new CopyList(strCopyListId);
				String strCopyListName = cl.getInfo(context, DomainConstants.SELECT_NAME);
				MapList mlCopyListMCs = cl.getArtworkMasters(context, selectList, null, AWLConstants.EMPTY_STRING);
				Map mapCopyListMC = new HashMap();
				for (int i = 0; i < mlCopyListMCs.size(); i++) {
					mapMCData = (Map<String, String>) mlCopyListMCs.get(i);
					MCName = mapMCData.get(DomainConstants.SELECT_NAME);
					mapCopyListMC.put(MCName, mapMCData);
				}
				Set setKeys = MCEMap.keySet();
				Iterator itr = setKeys.iterator();
				while (itr.hasNext()) {
					String keyMCEName = (String) itr.next();
					if (mapCopyListMC.containsKey(keyMCEName)) {
						checkExistingMCEs(strCopyListName, excludeMCEError, excludeMCEWarning, MCEMap, mapCopyListMC,
								keyMCEName);
					} else {
						logger.log(Level.INFO, "CopyListName :: " + strCopyListName);
						String strMasterId = (String) ((Map) MCEMap.get(keyMCEName)).get(DomainConstants.SELECT_ID);
						String strMasterName = (String) ((Map) MCEMap.get(keyMCEName)).get(DomainConstants.SELECT_NAME);
						logger.log(Level.INFO, "strMasterName :: " + strMasterName);
						if (strMode.equals(EditCLConstants.STR_ADDEXISTINGELEMENT)) {
							jArrIncludeList = addNewMasterCopyToCopyList(context, strCopyListId, strMasterId);
							jArrIncludeListFinal.add(jArrIncludeList);
						} else if (strMode.equals(EditCLConstants.STR_ADDARTWORKELEMENT)) {
							String strRet = addArtworkElementToCopyList(context, strCopyListId, strMasterId);
							jArrIncludeList = getCopyListMCLCDetails(context, strCopyListId, strMasterId);
							jArrIncludeListFinal.add(jArrIncludeList);
						} else if (strMode.equals(EditCLConstants.STR_CHANGEREVISION)) {
							String strRet = addArtworkElementToCopyList(context, strCopyListId, strMasterId);
							if (PGWidgetConstants.KEY_SUCCESS.equalsIgnoreCase(strRet)) {
								updateInstanceSequenceIdOnRel(context, strCopyListId, strMasterId, strInstanceSequence);
							}
							jArrIncludeList = getCopyListMCLCDetails(context, strCopyListId, strMasterId);
							jArrIncludeListFinal.add(jArrIncludeList);
						}
						logger.log(Level.INFO, "strOutput :: " + strOutput);
						if (!slIncludeNames.contains(strMasterName)) {
							slIncludeNames.add(strMasterName);
						}
					}
				}
			}
			logger.log(Level.INFO, "Valid for Connection :: " + jArrIncludeListFinal.build());
			buildAddMCEReturnMessage(context, output, excludeMCEError, excludeMCEWarning, slIncludeNames,
					jArrIncludeListFinal.build());
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}

		logger.log(Level.INFO, "output :: " + output.build());
		return output.build().toString();
	}

	/***
	 * This method updates Instance Sequence Value on Relationship
	 * 
	 * @param context
	 * @param strCopyListId
	 * @param strMasterId
	 * @param attribValue
	 * @throws Exception
	 */
	public static void updateInstanceSequenceIdOnRel(Context context, String strCopyListId, String strMasterId,
			String attribValue) throws Exception {
		CopyList copylist = new CopyList(strCopyListId);
		String strWhere = DomainConstants.SELECT_ID + PGWidgetConstants.KEY_DOUBLE_EQUALS + strMasterId;
		MapList mlCopyListMCs = copylist.getArtworkMasters(context, StringList.create(DomainConstants.SELECT_ID),
				StringList.create(PGWidgetConstants.SELECT_CONNECTION_ID), strWhere);
		if (BusinessUtil.isNotNullOrEmpty(mlCopyListMCs)) {
			String strRelId = (String) ((Map) mlCopyListMCs.get(0)).get(PGWidgetConstants.SELECT_CONNECTION_ID);
			DomainRelationship.setAttributeValue(context, strRelId, AWLAttribute.INSTANCE_SEQUENCE.get(context),
					attribValue);
		}
	}

	/***
	 * This method is called to add Master Copy Elements to Copy List
	 * 
	 * @param context
	 * @param strCopyListId
	 * @param strMasterId
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder addNewMasterCopyToCopyList(Context context, String strCopyListId, String strMasterId)
			throws Exception {
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		try {
			ArtworkMaster am = new ArtworkMaster(strMasterId);
			CopyList cl = new CopyList(strCopyListId);
			cl.addArtworkMaster(context, am);
			jsonArrayBuilder = getCopyListMCLCDetails(context, strCopyListId, strMasterId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			jsonObjectBuilder.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonObjectBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			jsonArrayBuilder.add(jsonObjectBuilder);
		}
		return jsonArrayBuilder;
	}

	/***
	 * This method is called to add Artwork Element to Copy List
	 * 
	 * @param context
	 * @param strCopyListId
	 * @param strMasterId
	 * @return
	 * @throws Exception
	 */
	public static String addArtworkElementToCopyList(Context context, String strCopyListId, String strMasterId)
			throws Exception {
		StringList stringList = FrameworkUtil.split(strCopyListId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		return ArtworkUtil.addMasterCopiesToCL(context, BusinessUtil.toStringList(new String[] { strMasterId }),
				stringList);
	}

	/***
	 * This method returns all the MC's and LC's Data
	 * 
	 * @param context
	 * @param strCopyListId
	 * @param strMasterId
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getCopyListMCLCDetails(Context context, String strCopyListId, String strMasterId)
			throws Exception {
		CopyList copylist = new CopyList(strCopyListId);

		HashMap<String, Object> mapFinalMCInfo = new HashMap<>();
		HashMap<String, Object> mapFinalLCInfo = new HashMap<>();

		String strWhere = "id=='" + strMasterId + "'";
		// Master copy details
		StringList selectListMCE = EditCLMainTable.getAllMAinTableSelects(context);
		StringList mceRelSelects = StringList.create(AWLAttribute.INSTANCE_SEQUENCE.getSel(context),
				EditCLConstants.KEY_REL_ID, AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
		MapList mlCopyListMCs = copylist.getArtworkMasters(context, selectListMCE, mceRelSelects, strWhere);

		StringList strMCIdsList = new StringList();

		// local copy details
		StringList lceRelSelects = StringList.create(AWLAttribute.NOTES.getSel(context),
				"attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");
		StringList selectListLCE = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
				DomainConstants.SELECT_TYPE, DomainConstants.SELECT_CURRENT, PGWidgetConstants.KEY_REVISIONS,
				EditCLConstants.STR_REVISIONS_ID, "attribute[" + AWLAttribute.IS_BASE_COPY.get(context) + "]",
				AWLAttribute.MARKETING_NAME.getSel(context), "attribute[" + AWLAttribute.COPY_TEXT.get(context) + "]",
				"attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]",
				AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context), DomainConstants.SELECT_REVISION,
				"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id",
				"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type",
				"from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id",
				"attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTADISTRIBTYPE + "]");
		MapList mlCopyListLCs = copylist.getArtworkElementsForMaster(context, selectListLCE, lceRelSelects, strMasterId,
				false);
		
		EditCLMainTable.formatCopyElementsForCL(context, mlCopyListMCs, mlCopyListLCs, mapFinalMCInfo, mapFinalLCInfo,
				strMCIdsList, copylist);

		JsonArrayBuilder jArrList = EditCLMainTable.formatResponseForMainTable(context, mapFinalMCInfo, mapFinalLCInfo,
				strMCIdsList);
		return jArrList;
	}

	public static JsonArrayBuilder getCopyListArtworkElementsForMce(Context context, String strJsonInput)
			throws Exception {
		
		JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		String strCopyListId = jsonInput.getString(EditCLConstants.COPYLIST_ID);
		String strMasterId = jsonInput.getString(EditCLConstants.KEY_MCE_ID);
		
		StringList slMCEIds = FrameworkUtil.split(strMasterId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		
		CopyList copylist = new CopyList(strCopyListId);

		HashMap<String, Object> mapFinalMCInfo = new HashMap<>();
		HashMap<String, Object> mapFinalLCInfo = new HashMap<>();

		StringList slWhereMCE = new StringList();
		StringList slWhereLCE = new StringList();
		for(int i=0 ; i<slMCEIds.size() ; i++){
			slWhereMCE.add("id=='" + strMasterId + "'");
			slWhereLCE.add("to["+ AWLRel.ARTWORK_ELEMENT_CONTENT.get(context)+ "].from.id=='"+ strMasterId+ "'" );
		}
		String strWhereMCE = slWhereMCE.join(" || ");
		String strWhereLCE = slWhereLCE.join(" || ");
		// Master copy details
		StringList selectListMCE = EditCLMainTable.getAllMAinTableSelects(context);
		StringList mceRelSelects = StringList.create(AWLAttribute.INSTANCE_SEQUENCE.getSel(context),
				EditCLConstants.KEY_REL_ID, AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
		MapList mlCopyListMCs = copylist.getArtworkMasters(context, selectListMCE, mceRelSelects, strWhereMCE);

		StringList strMCIdsList = new StringList();

		// local copy details
		StringList lceRelSelects = StringList.create(AWLAttribute.NOTES.getSel(context),
				"attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");
		StringList selectListLCE = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
				DomainConstants.SELECT_TYPE, DomainConstants.SELECT_CURRENT, PGWidgetConstants.KEY_REVISIONS,
				EditCLConstants.STR_REVISIONS_ID, "attribute[" + AWLAttribute.IS_BASE_COPY.get(context) + "]",
				AWLAttribute.MARKETING_NAME.getSel(context), "attribute[" + AWLAttribute.COPY_TEXT.get(context) + "]",
				"attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]",
				AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context), DomainConstants.SELECT_REVISION,
				"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id",
				"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type",
				"from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id",
				"attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]",
				"attribute[" + EditCLConstants.ATTRIBUTE_PGRTADISTRIBTYPE + "]");
//		MapList mlCopyListLCs = copylist.getArtworkElementsForMaster(context, selectListLCE, lceRelSelects, strMasterId,
//				false);
		MapList mlCopyListLCs = copylist.getArtworkElements(context, selectListLCE, lceRelSelects,  strWhereLCE);
		
		EditCLMainTable.formatCopyElementsForCL(context, mlCopyListMCs, mlCopyListLCs, mapFinalMCInfo, mapFinalLCInfo,
				strMCIdsList, copylist);

		JsonArrayBuilder jArrList = EditCLMainTable.formatResponseForMainTable(context, mapFinalMCInfo, mapFinalLCInfo,
				strMCIdsList);
		return jArrList;
	}

	
	/***
	 * This method checks for the connected MCE's and returns messages accordingly
	 * 
	 * @param POAName
	 * @param excludeMCEError
	 * @param excludeMCEWarning
	 * @param MCEMap
	 * @param MCPOAMap
	 * @param keyMCEName
	 */
	private static void checkExistingMCEs(String POAName, Map excludeMCEError, Map excludeMCEWarning, Map MCEMap,
			Map MCPOAMap, String keyMCEName) throws Exception {
		String typeConnected = (String) ((Map) MCPOAMap.get(keyMCEName)).get(DomainConstants.SELECT_TYPE);
		String typeToConnect = (String) ((Map) MCEMap.get(keyMCEName)).get(DomainConstants.SELECT_TYPE);
		if (typeToConnect.equals(typeConnected)) {
			String revConnected = (String) ((Map) MCPOAMap.get(keyMCEName)).get(DomainConstants.SELECT_REVISION);
			String revToConnect = (String) ((Map) MCEMap.get(keyMCEName)).get(DomainConstants.SELECT_REVISION);
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

	/***
	 * this method is to build return messages on connect MCE
	 * 
	 * @param context
	 * @param output
	 * @param excludeMCEError
	 * @param excludeMCEWarning
	 * @param slIncludeNames
	 * @param jArrIncludeList
	 */
	public static void buildAddMCEReturnMessage(Context context, JsonObjectBuilder output, Map excludeMCEError,
			Map excludeMCEWarning, StringList slIncludeNames, JsonArray jArrIncludeList) throws Exception {
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
				sbError.append(slIncludeNames.join(", ")).append(PGWidgetConstants.KEY_NEW_LINE);
			}
			sbError.append(strAlreadyConnectedMsg);
			Set setKeys = excludeMCEError.keySet();
			Iterator itr = setKeys.iterator();
			while (itr.hasNext()) {
				String itrKey = (String) itr.next();
				sbError.append(itrKey).append(": ").append(
						((StringList) (excludeMCEError.get(itrKey))).join(PGWidgetConstants.KEY_COMMA_SEPARATOR))
						.append(PGWidgetConstants.KEY_NEW_LINE);
			}
		}
		if (excludeMCEWarning.size() > 0) {
			if (slIncludeNames.size() > 0 && excludeMCEError.size() == 0) {
				sbError.append(strSuccessfulConnectionMsg);
				sbError.append(slIncludeNames.join(", ")).append(PGWidgetConstants.KEY_NEW_LINE);
			}
			sbError.append(strDiffRevMsg);
			Set setKeys = excludeMCEWarning.keySet();
			Iterator itr = setKeys.iterator();
			while (itr.hasNext()) {
				String itrKey = (String) itr.next();
				sbError.append(itrKey).append(": ").append(
						((StringList) (excludeMCEWarning.get(itrKey))).join(PGWidgetConstants.KEY_COMMA_SEPARATOR))
						.append(PGWidgetConstants.KEY_NEW_LINE);
			}
		}
		if (sbError.length() > 0) {
			output.add(PGWidgetConstants.KEY_ERROR_MSG, sbError.toString());
		}
		if (slIncludeNames.size() > 0) {
			if (excludeMCEError.size() == 0 && excludeMCEWarning.size() == 0) {
				output.add(PGWidgetConstants.KEY_SUCCESS,
						strSuccessfulConnectionMsg + slIncludeNames.join(PGWidgetConstants.KEY_COMMA_SEPARATOR));
			}
			output.add(PGWidgetConstants.KEY_DATA, jArrIncludeList);
		}
	}

	/***
	 * This method is called to Auto Update Instance Sequence
	 * 
	 * @param context
	 * @param strInput
	 * @return
	 */
	public static String autoUpdateInstanceSequence(Context context, String strInput) throws Exception {
		String strOut = DomainConstants.EMPTY_STRING;
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			JsonArray jsonArrInput = jsonInput.getJsonArray(EditCLConstants.KEY_CL_MC_INST_SEQ);

			for (int i = 0; i < jsonArrInput.size(); i++) {
				StringList slInputValues = FrameworkUtil.split(jsonArrInput.getString(i),
						PGWidgetConstants.KEY_UNDERSCORE);
				String strCLId = (String) slInputValues.get(0);
				String strMCId = (String) slInputValues.get(1);
				if (BusinessUtil.isKindOf(context, strCLId, AWLType.COPY_LIST.get(context))) {
					(new CopyList(strCLId)).setInstanceSequence(context, new ArtworkMaster(strMCId),
							Integer.parseInt(slInputValues.get(2)));
				}
			}
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build()
					.toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			strOut = Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
		return strOut;
	}

	/***
	 * This method is called to fetch regions of Copy Elements and CopyList
	 * 
	 * @param context
	 * @param strCopyListId
	 * @return
	 * @throws Exception
	 */
	public static String getCopyListRegions(Context context, String strCopyListId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjRegion = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strCopyListId);
			strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);

			JsonArrayBuilder jsonArrHier = Json.createArrayBuilder();

			StringList slCountryIds = BusinessUtil.getInfoList(context, strCopyListId,
					AWLUtil.strcat("from[", AWLRel.COPY_LIST_COUNTRY.get(context), "].to.id"));

			String strApplicableCountryIds = EditCLConstants.STR_ALLCOUNTRIES;
			String strSelectedCountryIds = slCountryIds.join(PGWidgetConstants.KEY_COMMA_SEPARATOR);

			List<Region> regions = getTopRegions(context);
			getSubRegions(context, regions, strApplicableCountryIds, strSelectedCountryIds, jsonArrHier);
			jsonObjRegion.add(EditCLConstants.KEY_REGIONS, jsonArrHier);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonObjRegion.build().toString();
	}

	/***
	 * This method is called to fetch top regions of Copy Elements and POA
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
			Iterator<?> localIterator;
			Object localObject;
			if (UIUtil.isNotNullAndNotEmpty(str1)) {
				MapList localMapList = DomainObject.findObjects(context, DomainConstants.TYPE_REGION, // typePattern
						PGWidgetConstants.VAULT_ESERVICE_PRODUCTION, // vaultPattern
						null, // where clause
						localStringList1); // objectSelects
				localMapList.addSortKey(DomainConstants.SELECT_NAME, PGWidgetConstants.STRING_CAPITAL_TRUE,
						EditCLConstants.KEY_STRING);
				localMapList.sort();

				String str4 = EnoviaResourceBundle.getProperty(context,
						"emxCPD.RegionCountryUI.DisplayTopLevelRegions");
				StringList localStringList2 = FrameworkUtil.split(str4, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				StringList localStringList3 = new StringList(localStringList2.size());

				for (localIterator = localStringList2.iterator(); localIterator.hasNext();) {
					localObject = localIterator.next();
					localStringList3.add(PropertyUtil.getSchemaProperty(context, (String) localObject));
				}
				for (localIterator = localMapList.iterator(); localIterator.hasNext();) {
					localObject = localIterator.next();
					Map<String, String> localMap = (Map<String, String>) localObject;
					String strId = localMap.get(DomainConstants.SELECT_ID);
					String strType = localMap.get(DomainConstants.SELECT_TYPE);
					String strRelSubRegion = localMap.get("to[" + DomainConstants.RELATIONSHIP_SUB_REGION + "]");
					localMap.remove("to[" + DomainConstants.RELATIONSHIP_SUB_REGION + "]");
					if (strRelSubRegion.equalsIgnoreCase(AWLConstants.RANGE_FALSE)) {
						if (localStringList3.contains(strType)) {
							Region localRegion = CPDCache.getRegion(strId);
							localRegion.setName(localMap.get(DomainConstants.SELECT_NAME));
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
		JsonObjectBuilder jsonObjKeyVariant = null;
		int iSubRegSize = 0;
		for (Region subregion : regions) {
			jsonObjKeyVariant = Json.createObjectBuilder();
			String strRegionId = subregion.getObjectId();
			String strRegionCurrent = BusinessUtil.getInfo(context, strRegionId, DomainConstants.SELECT_CURRENT);
			if (ProductLineConstants.STATE_INACTIVE.equals(strRegionCurrent)) {
				continue;
			}
			jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, subregion.getName());
			jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strRegionId);
			iSubRegSize = subregion.getSubRegions(context).size();
			if (iSubRegSize <= 0) {
				jsonObjKeyVariant.add(EditCLConstants.KEY_LEAF, AWLConstants.RANGE_TRUE);
			} else {
				List<Region> listSubRegions = subregion.getSubRegions(context);
				getSubRegions(context, listSubRegions, strApplicableCountryIds, strSelectedCountryIds, jsonArrHier);
			}
			List<Country> listCountries = subregion.getCountries(context, true);
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
			String strSelectedCountryIds, JsonArrayBuilder jsonArrHier, JsonObjectBuilder jsonObjKeyVariant)
			throws Exception {
		boolean isApplicable = false;
		for (Country country : listCountries) {
			String currentCountryId = country.getObjectId();
			if ((UIUtil.isNotNullAndNotEmpty(strApplicableCountryIds))
					&& ((strApplicableCountryIds.equalsIgnoreCase(EditCLConstants.STR_ALLCOUNTRIES))
							|| (strApplicableCountryIds.indexOf(currentCountryId) >= 0))) {
				isApplicable = true;
				if (UIUtil.isNotNullAndNotEmpty(strSelectedCountryIds)
						&& (strSelectedCountryIds.indexOf(currentCountryId) >= 0)) {
					jsonObjKeyVariant.add(EditCLConstants.KEY_SELECTED, AWLConstants.RANGE_TRUE);
					jsonObjKeyVariant.add(EditCLConstants.KEY_EXPANDED, AWLConstants.RANGE_TRUE);
					break;
				}
			}
		}
		if (isApplicable) {
			jsonArrHier.add(jsonObjKeyVariant);
		}
	}

	/***
	 * This method is called to Add Local Copies to Copy List Elements
	 * This method is copied from awlaccelerator.jar made for Copy list
	 * package com.dassault_systemes.cap.services.db;
	 * Method Name: addLocalElements
	 * @param context
	 * @param paramString1
	 * @param paramString2
	 * @return
	 * @throws Exception
	 */
	public static String addLocalElements(Context context, String paramString1, String paramString2) throws Exception {
		HashMap<Object, Object> hashMap = new HashMap<>();
		try {
			List<Map<String, Object>> list = JsonHelper.parseJsonArray("[" + paramString1 + "]");
			StringList stringList1 = new StringList();
			for (Map<String, Object> map : list)
				stringList1.add((String) map.get(PGWidgetConstants.KEY_NAME));

			StringList stringList2 = FrameworkUtil.split(paramString2, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			String str = PGWidgetConstants.KEY_EMPTY_STRING;
			for (String str1 : stringList2) {
				str1 = str1.replaceFirst(EditCLConstants.KEY_MC_POA, PGWidgetConstants.KEY_EMPTY_STRING);
				StringList stringList3 = FrameworkUtil.split(str1, PGWidgetConstants.KEY_UNDERSCORE);
				String str2 = (String) stringList3.get(0);
				String str3 = (String) stringList3.get(1);
				ArtworkMaster artworkMaster = new ArtworkMaster(str2);
				try {
					List<CopyElement> list1 = artworkMaster.fetchLocalCopyElements(context, stringList1);
					addLocalCopiesToCopyList(context, AWLUtil.getIdListFromDomainObjects(context, list1), str3);

				} catch (Exception exception) {
					str = AWLUtil.strcat(
							new Object[] { str, exception.getLocalizedMessage(), PGWidgetConstants.KEY_NEW_LINE });
				}
			}
			if (str.length() > 0) {
				hashMap.put(EditCLConstants.KEY_RETURN_STRING, str);
			} else {
				hashMap.put(EditCLConstants.KEY_RETURN_STRING, PGWidgetConstants.KEY_EMPTY_STRING);
			}
			return (new JsonHelper()).getJsonString(hashMap);
		} catch (Exception exception) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, exception);
			hashMap.put(EditCLConstants.KEY_RETURN_STRING, exception.getLocalizedMessage());
			return (new JsonHelper()).getJsonString(hashMap);
		}
	}

	/***
	 * This method is called to connect LCE's to Copy List
	 * 
	 * @param paramContext
	 * @param paramStringList
	 * @param strCopyListId
	 * @throws FrameworkException
	 */
	public static void addLocalCopiesToCopyList(Context paramContext, StringList paramStringList, String strCopyListId)
			throws Exception {
		try {
			String relName = AWLRel.ARTWORK_ASSEMBLY.get(paramContext);
			StringList stringList = BusinessUtil.getInfoList(paramContext, strCopyListId,
					AWLUtil.strcat(new Object[] { "from[", relName, "].to.id" }));
			paramStringList.removeAll((Collection<?>) stringList);
//			for (String str : paramStringList) {
//				DomainRelationship.connect(paramContext, strCopyListId, relName, str, true);
//			}
			DomainObject domCL = DomainObject.newInstance(paramContext, strCopyListId);
			domCL.addRelatedObjects(paramContext, new RelationshipType(relName), true, paramStringList.toStringArray());

		} catch (Exception exception) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, exception);
			throw exception;
		}
	}

	public static boolean isCopyElementMandatory(Context paramContext) throws FrameworkException {
		String str = AWLPropertyUtil.getConfigPropertyString(paramContext, "enoCAP.CopyList.isCopyElementMandatory");
		return (PGWidgetConstants.STRING_TRUE.equalsIgnoreCase(str)
				|| PGWidgetConstants.STRING_YES.equalsIgnoreCase(str));
	}

	/***
	 * This method returns the CopyList Data with Author and Approver Data
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String getAuthorAndApproverCopyListEditData(Context context, String strCopyListId) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strCopyListId);
		strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
		CopyList copylist = new CopyList(strCopyListId);
		DomainObject doCopyList = DomainObject.newInstance(context, strCopyListId);
		String strCopyListCurrent = BusinessUtil.getInfo(context, strCopyListId, DomainConstants.SELECT_CURRENT);
		HashMap<String, Object> mapFinalMCInfo = new HashMap<>();
		HashMap<String, Object> mapFinalLCInfo = new HashMap<>();
		HashMap<String, Object> mapFinalMCLCInfo = new HashMap<>();
		String strOutput = null;
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			StringList selectListMCE = StringList.create(DomainConstants.SELECT_ID, EditCLConstants.KEY_LAST,
					PGWidgetConstants.KEY_REVISIONS, "revisions.id", DomainConstants.SELECT_NAME,
					DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION, DomainConstants.SELECT_CURRENT,
					AWLAttribute.MARKETING_NAME.getSel(context), AWLAttribute.TRANSLATE.getSel(context),
					AWLAttribute.INLINE_TRANSLATION.getSel(context),
					"from[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "|to.attribute["
							+ AWLAttribute.IS_BASE_COPY.get(context) + "]=='Yes'].to.attribute[Copy Text_RTE]",
					"type.kindof[" + AWLType.MASTER_ARTWORK_GRAPHIC_ELEMENT.get(context) + "]");
			StringList mceRelSelects = StringList.create(AWLAttribute.INSTANCE_SEQUENCE.getSel(context),
					AWLAttribute.ARTWORK_ELEMENT_SEQUENCE_ORDER.getSel(context));
			MapList mlCopyListMCs = copylist.getArtworkMasters(context, selectListMCE, mceRelSelects,
					DomainConstants.EMPTY_STRING);

			StringList strMCIdsList = new StringList();
			StringList strLCIdsList = new StringList();

			StringList lceRelSelects = StringList.create(AWLAttribute.NOTES.getSel(context),
					"attribute[" + AWLAttribute.IS_MANDATORY.get(context) + "]");
			StringList selectListLCE = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
					DomainConstants.SELECT_TYPE, DomainConstants.SELECT_REVISION, DomainConstants.SELECT_CURRENT,
					EditCLConstants.KEY_LAST, PGWidgetConstants.KEY_REVISIONS, "revisions.id",
					"attribute[" + AWLAttribute.IS_BASE_COPY.get(context) + "]",
					AWLAttribute.MARKETING_NAME.getSel(context),
					"attribute[" + AWLAttribute.COPY_TEXT.get(context) + "]",
					"attribute[" + AWLAttribute.COPY_TEXT.get(context) + "_RTE]",
					AWLAttribute.COPY_TEXT_LANGUAGE.getSel(context),
					"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id",
					"to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.type",
					"from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id",
					"attribute[" + EditCLConstants.ATTR_VALIDITY_DATE + "]",
					"attribute[" + EditCLConstants.ATTRIBUTE_PGRTADISTRIBTYPE + "]");
			MapList mlCopyListLCs = null;
			if (!strCopyListCurrent.equals(AWLState.OBSOLETE.get(context, AWLPolicy.COPY_LIST))) {
				mlCopyListLCs = doCopyList.getRelatedObjects(context, AWLRel.ARTWORK_ASSEMBLY.get(context),
						AWLType.ARTWORK_ELEMENT.get(context), selectListLCE,
						lceRelSelects,
						false, 
						true, 
						(short) 0,
						null,
						null, 0);
			} else {
				mlCopyListLCs = doCopyList.getRelatedObjects(context, AWLRel.ARTWORK_ASSEMBLY_HISTORY.get(context),
						AWLType.ARTWORK_ELEMENT.get(context), selectListLCE,
						lceRelSelects,
						false,
						true, 
						(short) 0,
						null,
						null, 0);

			}
			getCopyListMCDetails(context, mlCopyListMCs, mlCopyListLCs, mapFinalMCInfo, strMCIdsList, copylist);
			getCopyListLCDetails(context, mlCopyListLCs, mapFinalLCInfo, mapFinalMCInfo, strLCIdsList, strMCIdsList);
			mapFinalMCLCInfo.putAll(mapFinalLCInfo);
			mapFinalMCLCInfo.putAll(mapFinalMCInfo);
			Map MCMap = new HashMap();
			MapList mlAATaskList = new MapList();
			MapList mlAAList = new MapList();
			StringList slMCIds = new StringList();
			ObjectMapper objectMapper = new ObjectMapper();
			for (Entry<String, Object> entry : mapFinalMCLCInfo.entrySet()) {
				Map<String, Object> MC = (Map<String, Object>) entry.getValue();
				String id = (String) MC.get(DomainConstants.SELECT_ID);
				String name = (String) MC.get(DomainConstants.SELECT_NAME);
				if (name.startsWith(EditCLConstants.KEY_MC)) {
					MCMap.put(EditCLConstants.STR_MCIDS, id);
					slMCIds.add(id);
					String jsonMCString = objectMapper.writeValueAsString(MCMap);
					if (jsonMCString.length() > 2) {
						String baseCopiesOut = RTAUtil.getBaseCopiesForMCEs(context, jsonMCString);
						List<Map<String, String>> mapList = objectMapper.readValue(baseCopiesOut, List.class);
						Map<String, String> map = mapList.get(0);
						MC.put(RTAUtilConstants.KEY_LOCAL_BASE_COPY, map.get(DomainConstants.SELECT_ID));
						MC.put(RTAUtilConstants.KEY_AUTHOR, map.get(RTAUtilConstants.KEY_AUTHOR));
						MC.put(RTAUtilConstants.KEY_APPROVER, map.get(RTAUtilConstants.KEY_APPROVER));
						MC.put("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]",
								map.get("from[" + DomainConstants.RELATIONSHIP_OBJECT_ROUTE + "]"));
					}
				}
			}
			for (String mcId : slMCIds) {
				mlAAList = RTAUtil.getTasksConnectedToLC(context, mcId);
				mlAATaskList.addAll(mlAAList);
			}
			for (Entry<String, Object> entry : mapFinalMCLCInfo.entrySet()) {
				Map<String, Object> MC = (Map<String, Object>) entry.getValue();
				String keyValue = entry.getKey();
				String lcId = null;
				if (keyValue.contains(PGWidgetConstants.KEY_UNDERSCORE)) {
					String[] spLcId = keyValue.split(PGWidgetConstants.KEY_UNDERSCORE);
					lcId = spLcId[1];
				}
				MapList mapList = new MapList();
				for (int i = 0; i < mlAATaskList.size(); i++) {
					Map<String, Object> mAALCMap = new HashMap<>();
					mAALCMap = (Map<String, Object>) mlAATaskList.get(i);
					if (mAALCMap.get(DomainConstants.SELECT_ID).equals(lcId)) {
						for (Entry<String, Object> entry1 : MC.entrySet()) {
							String key = entry1.getKey();
							Object value = entry1.getValue();
							if (value == null) {
								mAALCMap.put(key, PGWidgetConstants.KEY_EMPTY_STRING);
							} else {
								mAALCMap.put(key, value);
							}
						}
						mapList.add(mAALCMap);
					}
				}
				String mcName = (String) MC.get(DomainConstants.SELECT_NAME);
				if (mcName.startsWith(EditCLConstants.KEY_MC)) {
					mapList.add(MC);
				}
				mapFinalMCInfo.put(keyValue, mapList);
			}
			strOutput = getFinalMCLCDetailsInJSONFormatForAA(context, mapFinalMCInfo, strMCIdsList).build().toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return strOutput;
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

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		try {
			Map<Object, Object> attrMap = new HashMap<>();
			String strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);

			String strMCType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
			String strDisplayName = jsonInputData.getString(EditCLConstants.KEY_MARKETINGNAME);
			String strCopyText = jsonInputData.getString(EditCLConstants.KEY_MASTERCOPYTEXT);
			String strTranslate = jsonInputData.getString(EditCLConstants.KEY_TRANSLATE);
			String strInline = jsonInputData.getString(EditCLConstants.KEY_INLINETRANSLATE);
			String strValidityDate = jsonInputData.getString(EditCLConstants.KEY_VALIDITYDATE);
			String strInstructions = jsonInputData.getString(EditCLConstants.KEY_INSTRUCTIONS);
			String strReferenceNo = jsonInputData.getString(EditCLConstants.KEY_REFERENCENUMBER);
			String strplaceOfOrigin = jsonInputData.getString(EditCLConstants.KEY_PLACEOFORIGIN);
			String strMasterLang = jsonInputData.getString(EditCLConstants.KEY_MASTERCOPYLANG);
			String strPGSubCopyType = jsonInputData.getString(EditCLConstants.KEY_PGSUBCOPYTYPE);
			AWLPreferences.setPreferedBaseLanguage(context, strMasterLang);
			List<String> placeOfOriginIDs = FrameworkUtil.split(strplaceOfOrigin,
					PGWidgetConstants.KEY_COMMA_SEPARATOR);
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
			copyElementData.put(EditCLConstants.KEY_LISTSEP, DomainConstants.EMPTY_STRING);
			copyElementData.put(EditCLConstants.KEY_LISTSEQ, DomainConstants.EMPTY_STRING);
			copyElementData.put(EditCLConstants.KEY_LISTITEM, DomainConstants.EMPTY_STRING);
			DomainObject placeOfOriginOBJ = BusinessUtil.isNotNullOrEmpty(placeOfOriginIDs)
					? new DomainObject(placeOfOriginIDs.get(0))
					: null;
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
			am.setAttributeValues(context, attrMap);
			String strMCId = am.getObjectId();

			HashMap<String, Object> hmProgramMap = new HashMap<>();
			hmProgramMap.put(EditCLConstants.KEY_MC_ID, strMCId);
			hmProgramMap.put(EditCLConstants.KEY_MC_SUBCOPYTYPE, strPGSubCopyType);

			String[] methodargs = JPO.packArgs(hmProgramMap);
			JPO.invoke(context, "pgRTACopyElementUtil", null, "setSubCopyTypeonMCFromWidgets", methodargs, void.class);

			CopyList cl = new CopyList(strCopyListId);
			cl.addArtworkMaster(context, am);
			jsonArrayBuilder = getCopyListMCLCDetails(context, strCopyListId, strMCId);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return jsonArrayBuilder.build().toString();
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
		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
		boolean isContextPushed = false;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);

			String strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
			Map<String, Object> attrMap = new HashMap<>();
			String strMCType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
			String strDisplayName = jsonInputData.getString(EditCLConstants.KEY_MARKETINGNAME);
			String strDocumentType = jsonInputData.getString(EditCLConstants.KEY_DOCUMENTTYPE);
			String strFileName = jsonInputData.getString(EditCLConstants.KEY_FILENAME);
			String strMasterDesc = jsonInputData.getString(DomainConstants.SELECT_DESCRIPTION);
			String strValidityDate = jsonInputData.getString(EditCLConstants.KEY_VALIDITYDATE);
			String strplaceOfOrigin = jsonInputData.getString(EditCLConstants.KEY_PLACEOFORIGIN);
			String strInstructions = jsonInputData.getString(EditCLConstants.KEY_INSTRUCTIONS);
			List<String> placeOfOriginIDs = FrameworkUtil.split(strplaceOfOrigin,
					PGWidgetConstants.KEY_COMMA_SEPARATOR);
			String strPGSubCopyType = jsonInputData.getString(EditCLConstants.KEY_PGSUBCOPYTYPE);
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
			imageElementData.put(EditCLConstants.KEY_MCSURL, DomainConstants.EMPTY_STRING);
			attrMap.put(RTAUtilConstants.ATTR_VALIDITY_DATE, strValidityDate);
			attrMap.put(RTAUtilConstants.ATTR_INSTRUCTIONS, strInstructions);
			attrMap.put(EditCLConstants.ATTRIBUTE_PGSUBCOPYTYPE, strPGSubCopyType);
			List<Country> countries = new ArrayList<Country>();
			DomainObject ctxObject = BusinessUtil.isNotNullOrEmpty(placeOfOriginIDs)
					? new DomainObject(placeOfOriginIDs.get(0))
					: null;
			ArtworkMaster dobImagekMaster = ArtworkMaster.createMasterImageElement(context, strMCType, imageElementData,
					ctxObject, countries);
			if (BusinessUtil.isNotNullOrEmpty(placeOfOriginIDs)) {
				placeOfOriginIDs.remove(0);
				for (String placeOfOriginID : placeOfOriginIDs) {
					DomainRelationship rel = dobImagekMaster.connectFrom(context, AWLRel.ARTWORK_MASTER,
							new DomainObject(placeOfOriginID));
					rel.setAttributeValue(context, AWLAttribute.PLACE_OF_ORIGIN.get(context), AWLConstants.RANGE_YES);
				}
			}

			GraphicsElement graphicsElement = (GraphicsElement) dobImagekMaster.getBaseArtworkElement(context);
			String strGraphicDocumentId = graphicsElement.getGraphicDocument(context).getObjectId(context);
			String strGraphicElementId = graphicsElement.getObjectId();

			dobImagekMaster.setAttributeValues(context, attrMap);

			String strMCId = dobImagekMaster.getObjectId();
			CopyList cl = new CopyList(strCopyListId);
			cl.addArtworkMaster(context, dobImagekMaster);
			// RTA user do not have access to release the Graphical Element on Creation so
			// need to push context
			ContextUtil.pushContext(context);
			isContextPushed = true;
			graphicElementFileUpload(context, strMCId, strFileName);

			// This code is referred from pg_AWLArtworkElementProcess.jsp
			// For promote the GCE to release state as it gets created
			MqlUtil.mqlCommand(context, false, "trigger off;", true);

			GraphicsElement graphicElement = new GraphicsElement(strGraphicElementId);
			graphicElement.setState(context, AWLState.RELEASE.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT));

			DomainObject dobjGraphicDocument = DomainObject.newInstance(context, strGraphicDocumentId);
			dobjGraphicDocument.setState(context, AWLState.RELEASE.get(context, AWLPolicy.ARTWORK_GRAPHIC));

			dobImagekMaster.setState(context, AWLState.RELEASE.get(context, AWLPolicy.ARTWORK_ELEMENT));

			MqlUtil.mqlCommand(context, false, "trigger on;", true);

			jsonArrayBuilder = getCopyListMCLCDetails(context, strCopyListId, strMCId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		} finally {
			MqlUtil.mqlCommand(context, false, "trigger on;", true);
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return jsonArrayBuilder.build().toString();
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
			String fileName = json.getString(PGWidgetConstants.KEY_FILE_NAME);
			String b64 = json.getString(EditCLConstants.JSON_KEY_DATA);

			ArtworkMaster artworkMaster = new ArtworkMaster(gceID);
			GraphicsElement graphicsElement = (GraphicsElement) artworkMaster.getBaseArtworkElement(context);
			String strGraphicDocumentId = graphicsElement.getGraphicDocument(context).getObjectId(context);
			String strGraphicElementId = graphicsElement.getObjectId();

			String[] blob = b64.split("base64,");

			String strWorkspace = context.createWorkspace();

			File dir = new File(strWorkspace);
			File out = new File(dir, fileName);

			outputStream = new FileOutputStream(out);
			byte[] decoder = Base64.getDecoder()
					.decode(blob[1].toString().replace("\"", PGWidgetConstants.KEY_EMPTY_STRING));
			outputStream.write(decoder);
			outputStream.close();

			String str1 = AWLPropertyUtil.getConfigPropertyString(context, "emxAWL.ImageDocument.Policies");
			if (!BusinessUtil.isNullOrEmpty(fileName)) {
				HashMap<Object, Object> hashMap = new HashMap<>();
				hashMap.put(EditCLConstants.KEY_FCS_ENABLED, PGWidgetConstants.STRING_FALSE);
				hashMap.put(PGWidgetConstants.KEY_OBJECT_ID, strGraphicDocumentId);
				hashMap.put(EditCLConstants.KEY_PARENT_ID, strGraphicElementId);
				hashMap.put(EditCLConstants.KEY_APPEND, PGWidgetConstants.STRING_FALSE);
				hashMap.put(EditCLConstants.KEY_UNLOCK, PGWidgetConstants.STRING_FALSE);
				hashMap.put(EditCLConstants.KEY_type, RTAUtilConstants.TYP_SYMBOL);
				hashMap.put(EditCLConstants.KEY_policy, str1);
				hashMap.put(EditCLConstants.KEY_PARENT_REL_NAME, AWLRel.GRAPHIC_DOCUMENT.toString());
				hashMap.put("fileName0", fileName);
				hashMap.put(CommonDocument.SELECT_TITLE, "testing");
				hashMap.put(EditCLConstants.KEY_NO_OF_FILES, "1");
				hashMap.put(EditCLConstants.KEY_OBJECT_ACTION, "checkin");
				hashMap.put(EditCLConstants.KEY_MCSURL, DomainConstants.EMPTY_STRING);
				hashMap.put(EditCLConstants.KEY_FORMAT, DomainConstants.FORMAT_GENERIC);
				hashMap.put(EditCLConstants.KEY_ATTR_MAP, new HashMap<>());
				String[] arrayOfString = JPO.packArgs(hashMap);

				Map<String, String> map1 = JPO.invoke(context, "emxCommonDocument", null, "commonDocumentCheckin",
						arrayOfString, Map.class);

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	/***
	 * This method is called to delete Authoring Task
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String deleteAuthoringTask(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strReturnMsg = PGWidgetConstants.KEY_EMPTY_STRING;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strMCIds = jsonInputData.getString(EditCLConstants.MC_IDS);
			StringList slCEId = FrameworkUtil.split(strMCIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			HashMap hArgs = new HashMap();
			hArgs.put(EditCLConstants.KEY_CE_IDS, slCEId);
			String[] strArgs = JPO.packArgs(hArgs);
			strReturnMsg = (String) JPO.invoke(context, "pgRTACopyElementUtil", null, "deleteAuthoringTaskInBackground",
					strArgs, String.class);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return strReturnMsg;
	}

	/***
	 * This method is called to Reassign Author and Approver Task
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String reassignTaskToSelectedPerson(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strReturnMsg = PGWidgetConstants.KEY_EMPTY_STRING;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strNewAuthorAssigneeId = jsonInputData.getString(EditCLConstants.KEY_NEW_AUTHOR_ASSIGNEE_ID);
			String strNewApproverAssigneeId = jsonInputData.getString(EditCLConstants.KEY_NEW_APPROVER_ASSIGNEE_ID);
			String strLCIds = jsonInputData.getString(EditCLConstants.KEY_CE_ID_REASSIGN_AUTH_APPROVER);
			StringList slCEIdsForReAssignAuthorApprover = FrameworkUtil.split(strLCIds,
					PGWidgetConstants.KEY_COMMA_SEPARATOR);

			if (com.matrixone.apps.cpn.util.BusinessUtil.isNotNullOrEmpty(slCEIdsForReAssignAuthorApprover)) {
				Object[] objectArray = new Object[] { context, strNewAuthorAssigneeId, strNewApproverAssigneeId,
						slCEIdsForReAssignAuthorApprover };
				Class[] objectTypeArray = new Class[] { matrix.db.Context.class, String.class, String.class,
						StringList.class };
				com.matrixone.apps.domain.util.BackgroundProcess backgroundProcess = new com.matrixone.apps.domain.util.BackgroundProcess();
				backgroundProcess.submitJob(context, new EditCLUtil(), "reassignTaskToSelectedPersonBackground",
						objectArray, objectTypeArray);
				String strMessage = EnoviaResourceBundle.getProperty(context, "emxAWLStringResource",
						context.getLocale(), "emxAWL.ReAssignAuthorApprover.BackgroundJob");
				MqlUtil.mqlCommand(context, "notice $1", strMessage);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return strReturnMsg;
	}

	/**
	 * Method to reassign the Author and Approvers to CE - Background Job
	 * 
	 * @param context
	 * @param strNewAuthorAssigneeId   - New Author Assignee
	 * @param strNewApproverAssigneeId - New Approver Assignee
	 * @param mOIdRelId                - Map contains CE and rel id of route node or
	 *                                 route task
	 * @throws Exception
	 */
	public void reassignTaskToSelectedPersonBackground(Context context, String strNewAuthorAssigneeId,
			String strNewApproverAssigneeId, Map mOIdRelId) throws Exception {
		String strCEId = null;
		StringList strITRelsList = null;
		String strRouteState = null;
		String strRouteId = null;
		MapList mlRoutes = null;
		MapList mlRouteNodeInfo = null;
		String strSelRouteSeq = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
		String strSelAttriDuteDateOffset = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
		String strAttrpgRTABGJobStatus = PropertyUtil.getSchemaProperty(context, "attribute_pgRTABGJobStatus");
		boolean bIsContextPushed = false;
		try {
			SimpleDateFormat sdfFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(),
					context.getLocale());
			String strCurrentFormatDate = sdfFormat.format(java.util.Calendar.getInstance().getTime());
			String strBGSubmitted = EditCLConstants.strBGSubmittedPref + strCurrentFormatDate;
			String strPGRTABGJobStatus = null;
			StringList slRouteNodeSelects = new StringList(3);
			slRouteNodeSelects.add(DomainConstants.SELECT_NAME);
			slRouteNodeSelects.add(DomainConstants.SELECT_TYPE);
			slRouteNodeSelects.add(DomainConstants.SELECT_ID);

			StringList slRouteNodeRelSelects = new StringList(5);
			slRouteNodeRelSelects.add(DomainRelationship.SELECT_ID);
			slRouteNodeRelSelects.add(DomainRelationship.SELECT_NAME);
			slRouteNodeRelSelects.add(EditCLConstants.PHYSICALID_CONNECTION);
			slRouteNodeRelSelects.add(EditCLConstants.SEL_ATTR_SCHEDULED_COMPLETION_DATE);
			slRouteNodeRelSelects.add(strSelAttriDuteDateOffset);
			slRouteNodeRelSelects.add(strSelRouteSeq);

			if (mOIdRelId == null) {
				return;
			}
			ArtworkContent element = null;

			// Pushing the context to fetch claim details
			if (!ArtworkConstants.PERSON_USERAGENT.equalsIgnoreCase(context.getUser())) {
				ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT, null, context.getVault().getName());
				bIsContextPushed = true;
			}
			for (Iterator ceKeyIter = mOIdRelId.keySet().iterator(); ceKeyIter.hasNext();) {
				strCEId = (String) ceKeyIter.next();
				if (BusinessUtil.isNotNullOrEmpty(strCEId)) {
					strPGRTABGJobStatus = BusinessUtil.getAttribute(context, strCEId, strAttrpgRTABGJobStatus);
					if (BusinessUtil.isNotNullOrEmpty(strPGRTABGJobStatus)
							&& strPGRTABGJobStatus.indexOf(EditCLConstants.strBGSubmittedPref) != -1) {
						ceKeyIter.remove();
					} else {
						BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, strBGSubmitted);
					}
				}
			}
			strNewAuthorAssigneeId = checkForAbsenceDelegate(context, strNewAuthorAssigneeId);
			strNewApproverAssigneeId = checkForAbsenceDelegate(context, strNewApproverAssigneeId);
			logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method strNewAuthorAssigneeId ::: {0}",
					strNewAuthorAssigneeId);
			logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method strNewApproverAssigneeId ::: {0}",
					strNewApproverAssigneeId);
			logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method mOIdRelId ::: {0}", mOIdRelId);

			for (Iterator ceKeyIter = mOIdRelId.keySet().iterator(); ceKeyIter.hasNext();) {
				strCEId = (String) ceKeyIter.next();
				if (BusinessUtil.isNotNullOrEmpty(strCEId)) {
					strITRelsList = (StringList) mOIdRelId.get(strCEId);
					logger.log(Level.INFO, "reassignTaskToSelectedPersonBackground method isAuthor ::: {0}",
							strITRelsList);
					element = ArtworkContent.getNewInstance(context, strCEId);

					if (BusinessUtil.isNotNullOrEmpty(strNewAuthorAssigneeId)) {
						// Update Authoring Details
						mlRoutes = getCEConnectedRoutes(context, strCEId, AWLConstants.LOCAL_COPY_AUTHORING);
						if (BusinessUtil.isNotNullOrEmpty(mlRoutes)) {
							mlRoutes.sort(DomainConstants.SELECT_ORIGINATED, PGWidgetConstants.DESCENDING,
									PGWidgetConstants.KEY_DATE);
							Map mCEAuthorRouteInfo = (Map) mlRoutes.get(0);
							logger.log(Level.INFO,
									"reassignTaskToSelectedPersonBackground method mCEAuthorRouteInfo ::: {0}",
									mCEAuthorRouteInfo);
							strRouteState = (String) mCEAuthorRouteInfo.get(DomainConstants.SELECT_CURRENT);
							if (BusinessUtil.isNotNullOrEmpty(strRouteState)
									&& (DomainConstants.STATE_ROUTE_DEFINE.equals(strRouteState)
											|| DomainConstants.STATE_ROUTE_IN_PROCESS.equals(strRouteState))) {
								strRouteId = (String) mCEAuthorRouteInfo.get(DomainConstants.SELECT_ID);
								mlRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, slRouteNodeSelects,
										slRouteNodeRelSelects);
								updateRouteNodeOrTaskWithNewAssignee(context, element, strRouteId, mlRouteNodeInfo,
										strITRelsList, strNewAuthorAssigneeId, true);
							}
						}
					}
					if (BusinessUtil.isNotNullOrEmpty(strNewApproverAssigneeId)) {
						// Update Approval Details
						mlRoutes = getCEConnectedRoutes(context, strCEId, AWLConstants.LOCAL_COPY_APPROVAL);
						if (BusinessUtil.isNotNullOrEmpty(mlRoutes)) {
							mlRoutes.sort(DomainConstants.SELECT_ORIGINATED, PGWidgetConstants.DESCENDING,
									PGWidgetConstants.KEY_DATE);
							Map mCEApproveRouteInfo = (Map) mlRoutes.get(0);
							strRouteState = (String) mCEApproveRouteInfo.get(DomainConstants.SELECT_CURRENT);
							if (BusinessUtil.isNotNullOrEmpty(strRouteState)
									&& (DomainConstants.STATE_ROUTE_DEFINE.equals(strRouteState)
											|| DomainConstants.STATE_ROUTE_IN_PROCESS.equals(strRouteState))) {
								strRouteId = (String) mCEApproveRouteInfo.get(DomainConstants.SELECT_ID);
								mlRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, slRouteNodeSelects,
										slRouteNodeRelSelects);
								if (DomainConstants.STATE_ROUTE_IN_PROCESS.equals(strRouteState)) {
									mCEApproveRouteInfo = getArtworkRouteTasksInfo(context, strRouteId, "Approval");
								}
								updateRouteNodeOrTaskWithNewAssignee(context, element, strRouteId, mlRouteNodeInfo,
										strITRelsList, strNewApproverAssigneeId, false);
							}
						}
					}
					BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus, DomainConstants.EMPTY_STRING);
				}
			}
		} catch (Exception ex) {
			if (BusinessUtil.isNotNullOrEmpty(strCEId)) {
				BusinessUtil.setAttribute(context, strCEId, strAttrpgRTABGJobStatus,
						"Error :" + ex.getLocalizedMessage());
			}
			logger.log(Level.INFO, "Exception in reassignTaskToSelectedPersonBackground method ::: {0}", ex);
		} finally {
			if (bIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}
	}

	/**
	 * Method to get the Absence delegate if any the assignee passed in the
	 * argument.
	 * 
	 * @param context
	 * @param strAssigneeId - Assignee Id
	 * @return Return delegate id if set
	 * @throws Exception
	 */
	private String checkForAbsenceDelegate(Context context, String strAssigneeId) throws Exception {
		try {
			if (BusinessUtil.isNullOrEmpty(strAssigneeId)) {
				return DomainConstants.EMPTY_STRING;
			}
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
			cal.set(Calendar.AM_PM, 0);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MILLISECOND, 0);
			SimpleDateFormat sdf = new SimpleDateFormat(eMatrixDateFormat.getInputDateFormat(), context.getLocale());
			String strAttributeAbsenceDelegate = PropertyUtil.getSchemaProperty(context, "attribute_AbsenceDelegate");
			String strAttributeAbsenceStartDate = PropertyUtil.getSchemaProperty(context, "attribute_AbsenceStartDate");
			String strAttributeAbsenceEndDate = PropertyUtil.getSchemaProperty(context, "attribute_AbsenceEndDate");
			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_NAME);
			slSelects.add("attribute[" + strAttributeAbsenceDelegate + "]");
			slSelects.add("attribute[" + strAttributeAbsenceEndDate + "]");
			slSelects.add("attribute[" + strAttributeAbsenceStartDate + "]");
			Map personInfoMap = BusinessUtil.getInfo(context, strAssigneeId, slSelects);
			String strAbsenceDelegate = (String) personInfoMap.get("attribute[" + strAttributeAbsenceDelegate + "]");
			String strAbsenceStartDate = (String) personInfoMap.get("attribute[" + strAttributeAbsenceStartDate + "]");
			String strAbsenceEndDate = (String) personInfoMap.get("attribute[" + strAttributeAbsenceEndDate + "]");
			if (BusinessUtil.isNotNullOrEmpty(strAbsenceDelegate) && BusinessUtil.isNotNullOrEmpty(strAbsenceStartDate)
					&& BusinessUtil.isNotNullOrEmpty(strAbsenceEndDate)) {
				Date curDate = cal.getTime();
				String strDelegatedPersonObjectId = PersonUtil.getPersonObjectID(context, strAbsenceDelegate);
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
				if (BusinessUtil.isNotNullOrEmpty(strDelegatedPersonObjectId)
						&& (absenceStartDate.equals(curDate) || absenceEndDate.equals(curDate)
								|| (absenceStartDate.before(curDate) && absenceEndDate.after(curDate)))) {
					strAssigneeId = strDelegatedPersonObjectId;
				}
			}
		} catch (Exception ex) {
			// Important Do not throw Exception
			logger.log(Level.INFO, "Exception in resetCEOwnerAndOriginator method ::: {0}", ex);
		}
		return strAssigneeId;
	}

	/**
	 * Method to get CEs connected to Route
	 * 
	 * @param context
	 * @param strCEId       - CE Id
	 * @param strWhereParam - Where clause
	 * @return - Routes list
	 * @throws Exception
	 */
	private MapList getCEConnectedRoutes(Context context, String strCEId, String strWhereParam) throws Exception {
		MapList mlRoutesInfo = new MapList();
		if (BusinessUtil.isNotNullOrEmpty(strCEId)) {
			StringList strBusSelectList = new StringList();
			strBusSelectList.add(DomainConstants.SELECT_ID);
			strBusSelectList.add(DomainConstants.SELECT_NAME);
			strBusSelectList.add(DomainConstants.SELECT_ORIGINATED);
			strBusSelectList.add(DomainConstants.SELECT_CURRENT);

			StringList strRelSelList = new StringList();
			strRelSelList.add(DomainRelationship.SELECT_ID);

			String strWhereClause = new StringBuilder("attribute[").append(AWLAttribute.ARTWORK_INFO.get(context))
					.append("] == ").append(strWhereParam).toString();
			DomainObject domCE = DomainObject.newInstance(context, strCEId);
			mlRoutesInfo = domCE.getRelatedObjects(context, DomainConstants.RELATIONSHIP_OBJECT_ROUTE, 
					DomainConstants.TYPE_ROUTE,
					strBusSelectList,
					strRelSelList,
					false,
					true,
					(short) 1,
					strWhereClause,
					null,
					0);
		}
		return mlRoutesInfo;
	}

	/***
	 * Update the new assignee to route node and inbox task.
	 * 
	 * @param context
	 * @param element          Artwork Element
	 * @param strRouteId       - Route Id
	 * @param mlRouteNodeInfo  - Route Node Info
	 * @param strITRelsList    - Selected Route Node or IT rel
	 * @param strNewAssigneeId - New Assignee Id
	 * @param isAuthor
	 * @throws Exception
	 */
	private void updateRouteNodeOrTaskWithNewAssignee(Context context, ArtworkContent element, String strRouteId,
			MapList mlRouteNodeInfo, StringList strITRelsList, String strNewAssigneeId, boolean isAuthor)
			throws Exception {
		Map mRouteNodeInfo = null;
		String strRouteNodeRelId = null;
		String strInboxTaskId = null;
		String attrRouteNodeId = AWLAttribute.ROUTE_NODE_ID.getSel(context);
		String strSelRouteSeq = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
		String strTaskState = null;
		String strOldAssignee = null;
		String strNewAssignee = null;

		StringList objSelectsList = new StringList();
		objSelectsList.add(DomainConstants.SELECT_ID);
		objSelectsList.add(DomainConstants.SELECT_NAME);
		objSelectsList.add(DomainConstants.SELECT_CURRENT);
		objSelectsList.add(EditCLConstants.SEL_ATTR_SCHEDULED_COMPLETION_DATE);
		objSelectsList.add(EditCLConstants.SEL_ATTR_ACTUAL_COMPLETION_DATE);
		objSelectsList.add(attrRouteNodeId);

		StringList relSelectsList = new StringList();
		relSelectsList.add(DomainRelationship.SELECT_ID);
		relSelectsList.add("to.attribute[" + AWLAttribute.ARTWORK_INFO.get(context) + "]");

		Route route = new Route(strRouteId);

		MapList mlRouteTaskList = route.getRouteTasks(context, objSelectsList, relSelectsList, null, false);
		InboxTask taskBean = null;
		DomainObject domPerson = null;
		logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strNewAssigneeId ::: {0}",
				strNewAssigneeId);
		logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method mlRouteTaskList ::: {0}", mlRouteTaskList);
		// If the new Assignee already present return without updating
		StringList strNodeList = BusinessUtil.toStringList(mlRouteNodeInfo, DomainConstants.SELECT_ID);
		if (BusinessUtil.isNotNullOrEmpty(strNodeList) && strNodeList.contains(strNewAssigneeId)) {
			return;
		}
		logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method mlRouteNodeInfo ::: {0}", mlRouteNodeInfo);

		if (mlRouteNodeInfo.size() == 1) {
			mRouteNodeInfo = (Map) mlRouteNodeInfo.get(0);
			strRouteNodeRelId = (String) mRouteNodeInfo.get(DomainRelationship.SELECT_ID);
			strOldAssignee = (String) mRouteNodeInfo.get(DomainConstants.SELECT_ID);
			if (BusinessUtil.isNotNullOrEmpty(strOldAssignee) && !strOldAssignee.equals(strNewAssigneeId)) {
				if (BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId)) {
					DomainRelationship.setToObject(context, strRouteNodeRelId,
							DomainObject.newInstance(context, strNewAssigneeId));
				}
				logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strRouteNodeRelId ::: {0}",
						strRouteNodeRelId);
				if (BusinessUtil.isNotNullOrEmpty(mlRouteTaskList)) {
					strInboxTaskId = (String) ((Map) mlRouteTaskList.get(0)).get(DomainConstants.SELECT_ID);
					strTaskState = (String) ((Map) mlRouteTaskList.get(0)).get(DomainConstants.SELECT_CURRENT);
					logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strInboxTaskId ::: {0}",
							strInboxTaskId);
					logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method strTaskState ::: {0}",
							strTaskState);
					if (BusinessUtil.isNotNullOrEmpty(strInboxTaskId) && BusinessUtil.isNotNullOrEmpty(strTaskState)
							&& !DomainConstants.STATE_INBOX_TASK_COMPLETE.equals(strTaskState)) {
						taskBean = (InboxTask) DomainObject.newInstance(context, strInboxTaskId);
						domPerson = DomainObject.newInstance(context, strNewAssigneeId);
						taskBean.reAssignTask(context, domPerson, "Person", taskBean.getOwner(context).getName(),
								"Person", domPerson.getInfo(context, DomainConstants.SELECT_NAME));
					}
				}
				logger.log(Level.INFO, "updateRouteNodeOrTaskWithNewAssignee method isAuthor ::: {0}", isAuthor);
				if (isAuthor) {
					element.updateAssignee(context, strNewAssigneeId, element.getApprovalAssigneeId(context));
				} else {
					element.updateAssignee(context, element.getAuthoringAssigneeId(context), strNewAssigneeId);
				}
			}
		} else {
			mlRouteNodeInfo.sort(strSelRouteSeq, PGWidgetConstants.ASCENDING, PGWidgetConstants.KEY_INTEGER);

			Iterator<?> itr = mlRouteNodeInfo.iterator();
			StringList strCopiedRelIds = new StringList();
			strCopiedRelIds.addAll(strITRelsList);
			Map<String, String> mNodePersonInfo = new HashMap<>();
			while (itr.hasNext()) {
				mRouteNodeInfo = (Map) itr.next();
				strRouteNodeRelId = (String) mRouteNodeInfo.get(DomainRelationship.SELECT_ID);
				strOldAssignee = (String) mRouteNodeInfo.get(DomainConstants.SELECT_ID);
				mNodePersonInfo.put((String) mRouteNodeInfo.get(EditCLConstants.PHYSICALID_CONNECTION), strOldAssignee);
				if (BusinessUtil.isNotNullOrEmpty(strRouteNodeRelId) && BusinessUtil.isNotNullOrEmpty(strOldAssignee)
						&& !strOldAssignee.equals(strNewAssigneeId) && strCopiedRelIds.contains(strRouteNodeRelId)) {
					DomainRelationship.setToObject(context, strRouteNodeRelId,
							DomainObject.newInstance(context, strNewAssigneeId));
					strCopiedRelIds.remove(strRouteNodeRelId);
				}
			}

			itr = mlRouteTaskList.iterator();
			while (itr.hasNext()) {
				Map<String, String> mTaskInfo = (Map) itr.next();
				strTaskState = mTaskInfo.get(DomainConstants.SELECT_CURRENT);
				String strProjectTaskRelId = mTaskInfo.get(DomainRelationship.SELECT_ID);
				String strRouteNodePhyId = mTaskInfo.get(attrRouteNodeId);
				strOldAssignee = mNodePersonInfo.get(strRouteNodePhyId);
				strInboxTaskId = mTaskInfo.get(DomainConstants.SELECT_ID);
				if (BusinessUtil.isNotNullOrEmpty(strProjectTaskRelId) && BusinessUtil.isNotNullOrEmpty(strInboxTaskId)
						&& BusinessUtil.isNotNullOrEmpty(strTaskState) && BusinessUtil.isNotNullOrEmpty(strOldAssignee)
						&& !strOldAssignee.equals(strNewAssigneeId) && strCopiedRelIds.contains(strProjectTaskRelId)
						&& !DomainConstants.STATE_INBOX_TASK_COMPLETE.equals(strTaskState)) {
					taskBean = (InboxTask) DomainObject.newInstance(context, strInboxTaskId);
					domPerson = DomainObject.newInstance(context, strNewAssigneeId);
					taskBean.reAssignTask(context, domPerson, PGWidgetConstants.KEY_PERSON,
							taskBean.getOwner(context).getName(), PGWidgetConstants.KEY_PERSON,
							domPerson.getInfo(context, DomainConstants.SELECT_NAME));
					DomainRelationship.setToObject(context, strRouteNodePhyId,
							DomainObject.newInstance(context, strNewAssigneeId));
				}
			}

			StringBuilder sbNewSeq = new StringBuilder();
			StringList slRouteNodeSelects = new StringList(1);
			slRouteNodeSelects.add(DomainConstants.SELECT_ID);
			StringList slRouteNodeRelSelects = new StringList(1);
			slRouteNodeRelSelects.add(strSelRouteSeq);

			MapList mlNewRouteNodeInfo = RouteUtil.getRouteNodeInfo(context, strRouteId, slRouteNodeSelects,
					slRouteNodeRelSelects);
			itr = mlNewRouteNodeInfo.iterator();
			while (itr.hasNext()) {
				mRouteNodeInfo = (Map) itr.next();
				strOldAssignee = (String) mRouteNodeInfo.get(DomainConstants.SELECT_ID);
				String strSeq = (String) mRouteNodeInfo.get(strSelRouteSeq);
				if (BusinessUtil.isNotNullOrEmpty(sbNewSeq.toString())) {
					sbNewSeq.append(PGWidgetConstants.KEY_PIPE_SEPARATOR);
				}
				sbNewSeq.append(strOldAssignee).append(PGWidgetConstants.KEY_COMMA_SEPARATOR).append(strSeq);
			}
			if (BusinessUtil.isNotNullOrEmpty(sbNewSeq.toString())) {
				if (isAuthor) {
					strNewAssignee = getRouteTemplateId(context, sbNewSeq.toString(), false);
					element.updateAssignee(context, strNewAssignee, element.getApprovalAssigneeId(context));
				} else {
					strNewAssignee = getRouteTemplateId(context, sbNewSeq.toString(), true);
					element.updateAssignee(context, element.getAuthoringAssigneeId(context), strNewAssignee);
				}
			}
		}
	}

	/***
	 * This method returns Route Template Id
	 * 
	 * @param context
	 * @param assigneeIds
	 * @param approval
	 * @return
	 * @throws Exception
	 */
	public static String getRouteTemplateId(Context context, String assigneeIds, boolean approval) throws Exception {
		String routeTemplateId = assigneeIds;
		try {
			Map<String, String> assigneeMap = getAssigneeMapfromString(assigneeIds);
			assigneeMap = sortByComparator(assigneeMap, true, true);
			String artworkInfo = joinMapToString(assigneeMap);

			if (assigneeMap.size() > 1) {
				routeTemplateId = getRouteTemplateByArtworkInfo(context, artworkInfo, approval);
				if (BusinessUtil.isNullOrEmpty(routeTemplateId))
					routeTemplateId = createRouteTemplate(context, assigneeMap, artworkInfo, approval);
			} else {
				routeTemplateId = assigneeMap.keySet().iterator().next();
			}

		} catch (Exception e) {
			throw e;
		}
		return routeTemplateId;
	}

	/***
	 * This method called to get the Assignee Sequence
	 * 
	 * @param assigneeIds
	 * @return
	 * @throws FrameworkException
	 */
	public static Map<String, String> getAssigneeMapfromString(String assigneeIds) throws FrameworkException {
		Map<String, String> assigneeMap = new LinkedHashMap<String, String>();
		StringList assigneeSeqList = FrameworkUtil.split(assigneeIds, PGWidgetConstants.KEY_PIPE_SEPARATOR);
		for (int itr = 0; itr < assigneeSeqList.size(); itr++) {
			StringList assigneeDetails = FrameworkUtil.split((String) assigneeSeqList.get(itr),
					PGWidgetConstants.KEY_COMMA_SEPARATOR);

			if (assigneeDetails.size() > 0) {
				String assigneeId = (String) assigneeDetails.get(0);
				String assigneeSeq = "1";

				if (assigneeDetails.size() > 1) {
					try {
						assigneeSeq = (String) assigneeDetails.get(1);
					} catch (Exception e) {
						logger.log(Level.SEVERE, e.getMessage());
					}
				}
				assigneeMap.put(assigneeId, assigneeSeq);
			}
		}
		return assigneeMap;
	}

	/***
	 * This method is called to create Route Template
	 * 
	 * @param context
	 * @param assigneeMap
	 * @param artworkInfo
	 * @param approval
	 * @return
	 * @throws MatrixException
	 */
	public static String createRouteTemplate(Context context, Map<String, String> assigneeMap, String artworkInfo,
			boolean approval) throws MatrixException {
		String routeTemplateId = PGWidgetConstants.KEY_EMPTY_STRING;
		try {
			routeTemplateId = FrameworkUtil.autoName(context, "type_RouteTemplate",
					new Policy(DomainObject.POLICY_ROUTE_TEMPLATE).getFirstInSequence(context), "policy_RouteTemplate");
			String routeBasePurpose = approval ? RouteUtil.ROUTE_APPROVAL : RouteUtil.ROUTE_STANDARD;
			String routeAction = approval ? RouteUtil.RANGE_VALUE_APPROVE : RouteUtil.RANGE_VALUE_COMMENT;
			HashMap<String, String> attributeMap = new HashMap<String, String>();
			attributeMap.put(PropertyUtil.getSchemaProperty(context, "attribute_TaskEditSetting"),
					"Modify/Delete Task List");
			attributeMap.put(AWLAttribute.ARTWORK_INFO.get(context), artworkInfo);
			attributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE, routeBasePurpose);
			DomainObject routeTemplateObj = DomainObject.newInstance(context, routeTemplateId);
			routeTemplateObj.setAttributeValues(context, attributeMap);
			routeTemplateObj.setDescription(context, "Route Template for " + artworkInfo);
			Iterator<String> iter = assigneeMap.keySet().iterator();
			while (iter.hasNext()) {
				String assigneeId = (String) iter.next();
				String taskSequence = (String) assigneeMap.get(assigneeId);
				DomainObject assgineeObj = DomainObject.newInstance(context, assigneeId);
				DomainRelationship routeNodeRel = DomainRelationship.connect(context, routeTemplateObj,
						DomainConstants.RELATIONSHIP_ROUTE_NODE, assgineeObj);
				String duedate = "2";
				try {
					duedate = EnoviaResourceBundle.getProperty(context,
							"emxAWL.ArtworkInboxTaskCompletion.DueDateOffset");
				} catch (Exception e) {
				}
				HashMap<String, String> relAttributeMap = new HashMap<String, String>();
				relAttributeMap.put(PropertyUtil.getSchemaProperty(context, "attribute_ParallelNodeProcessionRule"),
						"All");
				relAttributeMap.put(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET, duedate);
				relAttributeMap.put(DomainConstants.ATTRIBUTE_TITLE,
						assgineeObj.getInfo(context, DomainConstants.SELECT_NAME));
				relAttributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_ACTION, routeAction);
				relAttributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE, taskSequence);
				relAttributeMap.put(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM, "Route Start Date");
				relAttributeMap.put(DomainConstants.ATTRIBUTE_ALLOW_DELEGATION, AWLConstants.RANGE_TRUE);
				relAttributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_INSTRUCTIONS, routeBasePurpose);
				routeNodeRel.setAttributeValues(context, relAttributeMap);
			}
			routeTemplateObj.setState(context, "Active");
		} catch (Exception e) {
			throw e;
		}

		return routeTemplateId;
	}

	/***
	 * This method returns Route Template
	 * 
	 * @param context
	 * @param artworkInfo
	 * @param approval
	 * @return
	 * @throws FrameworkException
	 */
	public static String getRouteTemplateByArtworkInfo(Context context, String artworkInfo, boolean approval)
			throws FrameworkException {
		String routePurpose = approval ? RouteUtil.ROUTE_APPROVAL : RouteUtil.ROUTE_STANDARD;
		StringList selects = BusinessUtil.toStringList(DomainConstants.SELECT_ID,
				"attribute[" + AWLAttribute.ARTWORK_INFO.get(context) + "].value",
				"attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE + "].value");

		String whereClause = "attribute[" + AWLAttribute.ARTWORK_INFO.get(context) + "].value == '" + artworkInfo
				+ "' && " + "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE + "].value == '" + routePurpose
				+ "' ";
		MapList rtMapList = DomainObject.findObjects(context, DomainConstants.TYPE_ROUTE_TEMPLATE, "*", whereClause,
				selects);

		return rtMapList.size() > 0 ? (String) BusinessUtil.getIdList(rtMapList).get(0) : null;
	}

	/***
	 * this method converts Map to String includes key and value pair
	 * 
	 * @param hm
	 * @return
	 */
	public static String joinMapToString(Map<String, String> hm) throws Exception {
		StringList keyValueList = new StringList();
		Iterator<String> iter = hm.keySet().iterator();
		while (iter.hasNext()) {
			String key = (String) iter.next();
			String val = (String) hm.get(key);

			keyValueList.add(key + PGWidgetConstants.KEY_COMMA_SEPARATOR + val);
		}

		return FrameworkUtil.join(keyValueList, PGWidgetConstants.KEY_PIPE_SEPARATOR);
	}

	/***
	 * This method called to sort the Map
	 * 
	 * @param unsortMap
	 * @param keys
	 * @param order
	 * @return
	 */
	public static Map<String, String> sortByComparator(Map<String, String> unsortMap, final boolean keys,
			final boolean order) throws Exception {
		List<Entry<String, String>> list = new LinkedList<Entry<String, String>>(unsortMap.entrySet());
		Collections.sort(list, new Comparator<Entry<String, String>>() {
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {
				if (keys) {
					return order ? o1.getKey().compareTo(o2.getKey()) : o2.getKey().compareTo(o1.getKey());
				} else {
					return order ? o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue());
				}
			}
		});
		Map<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Entry<String, String> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	/**
	 * Method to get the Route Tasks information
	 * 
	 * @param context
	 * @param strRouteId          - Route Id
	 * @param strAuthorOrApprover - Prefix for author or approver
	 * @return Map containing task info
	 * @throws Exception
	 */
	private Map getArtworkRouteTasksInfo(Context context, String strRouteId, String strAuthorOrApprover)
			throws Exception {

		StringBuilder sbTaskName = new StringBuilder();
		StringBuilder sbTaskSD = new StringBuilder();
		StringBuilder sbTaskAD = new StringBuilder();
		String attrRouteNodeId = AWLAttribute.ROUTE_NODE_ID.getSel(context);

		StringList objSelectsList = new StringList();
		objSelectsList.add(DomainConstants.SELECT_ID);
		objSelectsList.add(DomainConstants.SELECT_NAME);
		objSelectsList.add(DomainConstants.SELECT_CURRENT);
		objSelectsList.add(EditCLConstants.SEL_ATTR_SCHEDULED_COMPLETION_DATE);
		objSelectsList.add(EditCLConstants.SEL_ATTR_ACTUAL_COMPLETION_DATE);
		objSelectsList.add(attrRouteNodeId);

		StringList relSelectsList = new StringList();
		relSelectsList.add(DomainRelationship.SELECT_ID);
		relSelectsList.add("to.attribute[" + AWLAttribute.ARTWORK_INFO.get(context) + "]");

		Route route = new Route(strRouteId);

		MapList mlRouteTaskList = route.getRouteTasks(context, objSelectsList, relSelectsList, null, false);

		Iterator itrTasks = mlRouteTaskList.iterator();

		Map mRouteNodeTaskInfo = new HashMap();

		while (itrTasks.hasNext()) {
			Map mapRouteTask = (Map) itrTasks.next();
			Map mapRouteTaskInfo = new HashMap();
			String strRouteNodeId = (String) mapRouteTask.get(attrRouteNodeId);
			String strTaskName = (String) mapRouteTask.get(DomainConstants.SELECT_NAME);
			String strTaskSD = (String) mapRouteTask.get(EditCLConstants.SEL_ATTR_SCHEDULED_COMPLETION_DATE);
			String strTaskAD = (String) mapRouteTask.get(EditCLConstants.SEL_ATTR_ACTUAL_COMPLETION_DATE);

			String strTaskStatus = checkForTaskStatus(strTaskSD, strTaskAD, true);

			strTaskSD = getParsedDate(context, strTaskSD);
			strTaskAD = getParsedDate(context, strTaskAD);

			if (sbTaskName.length() != 0) {
				sbTaskName.append("<br></br>");
				sbTaskSD.append("<br></br>");
			}
			if (sbTaskAD.length() != 0) {
				sbTaskAD.append("<br></br>");
			}
			sbTaskName.append(strTaskName);
			sbTaskSD.append(strTaskSD);
			sbTaskAD.append(strTaskAD);

			mapRouteTaskInfo.put(strAuthorOrApprover + "TaskName", strTaskName);
			mapRouteTaskInfo.put(strAuthorOrApprover + "TaskSD", strTaskSD);
			mapRouteTaskInfo.put(strAuthorOrApprover + "TaskAD", strTaskAD);
			mapRouteTaskInfo.put(strAuthorOrApprover + "TaskStatus", strTaskStatus);
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

	/***
	 * this method called for Parsing Date
	 * 
	 * @param context
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	public static String getParsedDate(Context context, String strData) throws Exception {
		String strDate = PGWidgetConstants.KEY_EMPTY_STRING;
		try {
			if (BusinessUtil.isNotNullOrEmpty(strData)) {
				strDate = (String) (getConvertedDate(context, (strData)));
			}
		} catch (Exception ex) {
			throw ex;
		}
		return strDate;
	}

	/***
	 * this method called to parse date to particular format
	 * 
	 * @param context
	 * @param strDateToConvert
	 * @return
	 * @throws Exception
	 */
	public static String getConvertedDate(Context context, String strDateToConvert) throws Exception {
		String strDate = PGWidgetConstants.KEY_EMPTY_STRING;

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(strDateToConvert);
		strDate = formatter.format(date);
		return strDate;
	}

	/**
	 * Method to return the task status icon
	 * 
	 * @param strDueDate              - Task due date
	 * @param strActualCompletionDate - Actual Completion date
	 * @param isTaskActive            - Is task active
	 * @return - Icon
	 * @throws Exception
	 */
	private String checkForTaskStatus(String strDueDate, String strActualCompletionDate, boolean isTaskActive)
			throws Exception {

		String strTaskStatus = DomainConstants.EMPTY_STRING;
		try {
			Date dueDate = null;
			if (BusinessUtil.isNotNullOrEmpty(strDueDate)) {
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

				if (BusinessUtil.isNotNullOrEmpty(strActualCompletionDate)) {
					completionDate = formatter.parse(strActualCompletionDate);
					cal.setTime(completionDate);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.HOUR_OF_DAY, 17);
					cal.set(Calendar.AM_PM, 0);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MILLISECOND, 0);
					if (cal.equals(calDueDate) || cal.before(calDueDate)) {
						strTaskStatus = "<center><img src='" + RTAUtilConstants.STR_AWL_APPROVAL_RELEASE_IMG_PATH
								+ "' title='On Time Completed' alt='On Time Completed' /></center>";
					} else if (cal.after(calDueDate)) {
						strTaskStatus = "<center><img src='" + RTAUtilConstants.STR_PG_AWL_REDTICK_IMG_PATH
								+ "' title='Not Completed On Time' alt='Not Completed On Time' /></center>";
					}
				} else {
					completionDate = new Date();
					cal.setTime(completionDate);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.HOUR_OF_DAY, 17);
					cal.set(Calendar.AM_PM, 0);
					cal.set(Calendar.HOUR, 0);
					cal.set(Calendar.MILLISECOND, 0);
					if ((cal.before(calDueDate) || cal.equals(calDueDate)) && isTaskActive) {
						strTaskStatus = "<center><img src='" + RTAUtilConstants.STR_PG_BLUE_STATUS_IMG_PATH
								+ "' title='In Process' alt='In Process' /></center>";
					} else if (cal.after(calDueDate)) {
						strTaskStatus = "<center><img src='" + RTAUtilConstants.STR_CPD_TASK_STATUS_RED_IMG_PATH
								+ "' title='Delayed' alt='Delayed' /></center>";
					}
				}
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ex.toString());
			logger.log(Level.SEVERE, ex.getMessage());
		}
		return strTaskStatus;
	}

	/***
	 * This method is called to promote CopyList to Release state
	 * 
	 * @param context
	 * @param paramString
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String promoteCopyList(Context context, String paramString, HttpServletRequest request)
			throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder JsonOutStatus = Json.createObjectBuilder();
		boolean isTransactionActive = false;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;
			
			promoteCopyList(context, request, JsonOutStatus, strCopyListId, AWLState.REVIEW.get(context, AWLPolicy.COPY_LIST));
			
			promoteCopyList(context, request, JsonOutStatus, strCopyListId, AWLState.RELEASE.get(context, AWLPolicy.COPY_LIST));

			
			ContextUtil.commitTransaction(context);
			isTransactionActive = false;
			
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (Exception e) {
			if(isTransactionActive) {
				ContextUtil.abortTransaction(context);
			}			
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			StringBuilder sbError = new StringBuilder(e.getMessage());
			output.add(PGWidgetConstants.KEY_ERROR, sbError.toString());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		
		return output.build().toString();
	}


	/***
	 * This method called to Execute Triggers
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
	public static Map<String, Object> executeTriggers(Context context, MapList mlTriggersList,
			HttpServletRequest request, StringBuffer strMessage, Map<String, Object> mapTriggers) throws Exception {
		boolean boolTriggerFailed = false;
		mapTriggers.put(EditCLConstants.STR_MESSAGE, strMessage);
		mapTriggers.put(EditCLConstants.TRIGGERFAILED, boolTriggerFailed);
		Iterator<Map<String, String>> mlItr = mlTriggersList.iterator();
		com.matrixone.apps.framework.lifecycle.CalculateSequenceNumber myBean = new com.matrixone.apps.framework.lifecycle.CalculateSequenceNumber();
		while (mlItr.hasNext()) {
			Map<String, String> mpTrigger = mlItr.next();
			String strSelectedId = mpTrigger.get(DomainConstants.SELECT_ID);
			String strTriggerId = FrameworkUtil.split(strSelectedId, PGWidgetConstants.KEY_OR).get(1);
			DomainObject doTriggerObject = DomainObject.newInstance(context, strTriggerId);
			String strTriggerRev = doTriggerObject.getInfo(context, DomainConstants.SELECT_REVISION);
			if (UIUtil.isNotNullAndNotEmpty(strTriggerRev)) {
				String[] strMethodArgs = JPO.packArgs(strSelectedId);
				String strJPOResults = (String) JPO.invoke(context, "emxTriggerValidationResults", new String[1],
						"executeTriggers", strMethodArgs, String.class);
				if (UIUtil.isNotNullAndNotEmpty(strJPOResults)) {
					StringList strlResults = FrameworkUtil.split(strJPOResults, PGWidgetConstants.KEY_TILDE);
					if ("Fail".equalsIgnoreCase((String) strlResults.get(0))) {
						StringList strCommentsList = myBean.getClientTasks(context);
						if (BusinessUtil.isNotNullOrEmpty(strCommentsList)) {
							String strTempComments = DomainConstants.EMPTY_STRING;
							for (int k = 0; k < strCommentsList.size(); k++) {
								strTempComments += (String) strCommentsList.get(k) + "  ";
							}
							strMessage.append("Notice :");
							strMessage.append(strTempComments);
							strMessage.append(PGWidgetConstants.KEY_NEW_LINE);
							boolTriggerFailed = true;
							mapTriggers.put(EditCLConstants.STR_MESSAGE, strMessage);
							mapTriggers.put(EditCLConstants.TRIGGERFAILED, boolTriggerFailed);
						}
						break;
					}

				}
			}
		}
		return mapTriggers;
	}

	/***
	 * This method is called for promoting CopyList to Release state
	 * @param context
	 * @param request
	 * @param JsonOutStatus
	 * @param strCopyListId
	 * @param strTargetState
	 * @return
	 * @throws Exception
	 */
	public static Boolean promoteCopyList(Context context, HttpServletRequest request, JsonObjectBuilder JsonOutStatus, String strCopyListId, String strTargetState) throws Exception {
		boolean isCLPromoted = false;
		try {
			if (BusinessUtil.isNotNullOrEmpty(strCopyListId)) {
				StringBuffer strMessage = new StringBuffer();
				boolean boolTriggerFailed = false;
				HashMap<String, Object> mpParam = null;
				MapList mlTriggersList = null;
				Map<?, ?> mapTriggers = new HashMap<>();
				mpParam = new HashMap<>();
				mpParam.put(AWLConstants.OBJECT_ID, strCopyListId);
				mpParam.put(EditCLConstants.REQUESTVALUEMAP, new HashMap<>());
				mlTriggersList = JPO.invoke(context, "emxTriggerValidation", new String[1], "getCheckTriggers",
						JPO.packArgs(mpParam), MapList.class);
				mapTriggers = executeTriggers(context, mlTriggersList, request, strMessage,
						(Map<String, Object>) mapTriggers);

				boolTriggerFailed = (boolean) mapTriggers.get(EditCLConstants.TRIGGERFAILED);
				strMessage = (StringBuffer) mapTriggers.get(EditCLConstants.STR_MESSAGE);				

				if (!boolTriggerFailed && BusinessUtil.isNullOrEmpty(strMessage.toString())) {
					CopyList cl = new CopyList(strCopyListId);
					cl.setState(context, strTargetState);
					isCLPromoted = true;					
				}else {
					StringBuilder sbstrMessage = new StringBuilder();
					sbstrMessage.append(strMessage);
					PGWidgetUtil.createErrorMessage(context, sbstrMessage, JsonOutStatus);
					JsonObject jsonErrObj = JsonOutStatus.build();
					if(BusinessUtil.isNotNullOrEmpty(jsonErrObj.getString(PGWidgetConstants.KEY_ERROR))) {
						throw new Exception(jsonErrObj.getString(PGWidgetConstants.KEY_ERROR));
					}else {
					    throw new Exception(strMessage.toString());
					}				
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return isCLPromoted;
	}


	/***
	 * This method returns CL Hierarchy data
	 * 
	 * @param paramContext
	 * @param paramString
	 * @return
	 * @throws FrameworkException
	 */
	public static String getCLHierachyData(Context paramContext, String paramString) throws FrameworkException {

		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrHier = Json.createArrayBuilder();
		JsonObjectBuilder languageArr = Json.createObjectBuilder();
		JsonObjectBuilder strOutput = Json.createObjectBuilder();
		JsonObjectBuilder POAHierachyData = Json.createObjectBuilder();

		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			paramString = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
			boolean ShowBrandType = jsonInputData.getBoolean(EditCLConstants.KEY_SHOWBRANDTYPE);
			StringList poaIdList = FrameworkUtil.split(paramString, PGWidgetConstants.KEY_OR);
			JsonObjectBuilder jsonObjKeyVariant = null;
			JsonObjectBuilder jsonObjKeyVariantChild = null;
			StringList slSelect = new StringList(3);
			slSelect.add("to[" + AWLRel.ASSOCIATED_COPY_LIST.get(paramContext) + "].from.id");
			slSelect.add("to[" + AWLRel.ASSOCIATED_COPY_LIST.get(paramContext) + "].from.name");
			slSelect.add("to[" + AWLRel.ASSOCIATED_COPY_LIST.get(paramContext) + "].from."
					+ AWLAttribute.MARKETING_NAME.getSel(paramContext));
			String strCPGId = null;
			String strTypeModel = null;
			String strType = null;
			String strId = null;
			String pre_lang = null;
			StringList slProducthierarchy = new StringList();
			Map<String, String> mapProduct = null;
			Map<String, String> mapProductHierarchy = null;
			MapList mlProductHierarchy = null;
			MapList mlProduct = BusinessUtil.getInfo(paramContext, poaIdList, slSelect);
			int iPOASize = poaIdList.size();
			int iPrdHierarchySize = 0;
			pre_lang = AWLPreferences.getPreferedBaseLanguage(paramContext);
			languageArr.add(EditCLConstants.KEY_BASELANGNAME, pre_lang);
			languageArr.add(EditCLConstants.KEY_BASELANGVALUE, pre_lang);
			for (byte b = 0; b < iPOASize; b++) {
				jsonObjKeyVariant = Json.createObjectBuilder();
				mapProduct = (Map<String, String>) mlProduct.get(b);
				strCPGId = mapProduct.get("to[" + AWLRel.ASSOCIATED_COPY_LIST.get(paramContext) + "].from.id");
				if (!slProducthierarchy.contains(strCPGId)) {
					slProducthierarchy.add(strCPGId);
					String physicalId = FrameworkUtil.getPIDfromOID(paramContext, strCPGId);
					jsonObjKeyVariant.add(DomainConstants.SELECT_PHYSICAL_ID, physicalId);
					jsonObjKeyVariant.add(DomainConstants.SELECT_ID, strCPGId);
					jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, (String) mapProduct
							.get("to[" + AWLRel.ASSOCIATED_COPY_LIST.get(paramContext) + "].from.name"));
					jsonObjKeyVariant.add(DomainConstants.SELECT_NAME,
							mapProduct.get("to[" + AWLRel.ASSOCIATED_COPY_LIST.get(paramContext) + "].from."
									+ AWLAttribute.MARKETING_NAME.getSel(paramContext)));
					jsonArrHier.add(jsonObjKeyVariant);
				}
				mlProductHierarchy = (new CPGProduct(strCPGId)).getProductHierarchy(paramContext);
				strTypeModel = AWLType.MODEL.get(paramContext);
				iPrdHierarchySize = mlProductHierarchy.size();
				for (int i = 0; i < iPrdHierarchySize; i++) {
					jsonObjKeyVariantChild = Json.createObjectBuilder();
					mapProductHierarchy = (Map<String, String>) mlProductHierarchy.get(i);
					strType = mapProductHierarchy.get(DomainConstants.SELECT_TYPE);
					strId = mapProductHierarchy.get(DomainConstants.SELECT_ID);

					if (!strType.equalsIgnoreCase(strTypeModel) && !slProducthierarchy.contains(strId)) {
						String physicalId = FrameworkUtil.getPIDfromOID(paramContext, strId);
						logger.log(Level.INFO, AWLUtil.strcat(EditCLConstants.KEY_SHOWBRANDTYPE, ShowBrandType));
						if (!ShowBrandType && EditCLConstants.KEY_BRAND.equals(strType)) {
							continue;
						}
						slProducthierarchy.add(strId);
						jsonObjKeyVariantChild.add(DomainConstants.SELECT_PHYSICAL_ID, physicalId);
						jsonObjKeyVariantChild.add(DomainConstants.SELECT_ID, strId);
						jsonObjKeyVariantChild.add(PGWidgetConstants.KEY_VALUE,
								mapProductHierarchy.get(DomainConstants.SELECT_NAME));
						jsonObjKeyVariantChild.add(DomainConstants.SELECT_NAME,
								mapProductHierarchy.get(AWLAttribute.MARKETING_NAME.getSel(paramContext)));
						jsonArrHier.add(jsonObjKeyVariantChild);
					}
				}

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}

		strOutput.add(EditCLConstants.KEY_PLACEOFORIGIN, jsonArrHier);
		strOutput.add(EditCLConstants.KEY_BASELANG, languageArr);

		POAHierachyData.add(EditCLConstants.KEY_POAHIERARCHYDATA, strOutput);

		return POAHierachyData.build().toString();
	}

	/***
	 * this method convert map to Json
	 * 
	 * @param mapMCData
	 * @param strKey
	 * @param strValue
	 * @param jsonObjKeyVariant
	 */
	public static void nextJSONFormat(Map<String, String> mapMCData, String strKey, String strValue,
			JsonObjectBuilder jsonObjKeyVariant) throws Exception {
		for (Entry<?, ?> entryMCLC : mapMCData.entrySet()) {
			if (entryMCLC.getValue() instanceof Map) {
				Map<String, String> temp = (Map<String, String>) entryMCLC.getValue();
				for (Entry<String, String> entryMCLCTemp : temp.entrySet()) {
					String strKey1 = entryMCLCTemp.getKey();
					String strValue1 = entryMCLCTemp.getValue();
					if (BusinessUtil.isNotNullOrEmpty(strValue1)) {
						jsonObjKeyVariant.add(strKey1, strValue1);
					} else {
						jsonObjKeyVariant.add(strKey1, DomainConstants.EMPTY_STRING);
					}
				}
			} else if (entryMCLC.getValue() instanceof String) {
				String strValue1 = (String) entryMCLC.getValue();
				if (BusinessUtil.isNotNullOrEmpty(strValue1)) {
					jsonObjKeyVariant.add((String) entryMCLC.getKey(), strValue1);
				} else {
					jsonObjKeyVariant.add((String) entryMCLC.getKey(), DomainConstants.EMPTY_STRING);
				}
			}
		}
	}

	/***
	 * This method returns CopyList with Author and Approver Data in JSON Format
	 * 
	 * @param context
	 * @param mapFinalMCInfo
	 * @param strMCIdsList
	 * @return
	 * @throws Exception
	 */
	public static JsonArrayBuilder getFinalMCLCDetailsInJSONFormatForAA(Context context,
			Map<String, Object> mapFinalMCInfo, StringList strMCIdsList) throws Exception {
		JsonArrayBuilder jsonArrCopyListData = Json.createArrayBuilder();
		String strValue = DomainConstants.EMPTY_STRING;
		Map<String, String> mapMCData = new HashMap();
		try {
			for (Entry<String, Object> entry : mapFinalMCInfo.entrySet()) {
				JsonArrayBuilder jsonArrHier = Json.createArrayBuilder();
				JsonObjectBuilder jsonObjKeyVariant = Json.createObjectBuilder();
				String strKey = entry.getKey();
				if (strKey.contains(PGWidgetConstants.KEY_UNDERSCORE)) {
					StringList slMCLCHierarchy = FrameworkUtil.split(strKey, PGWidgetConstants.KEY_UNDERSCORE);
					jsonArrHier.add(slMCLCHierarchy.get(0));
					jsonArrHier.add(slMCLCHierarchy.get(1));
				} else {
					jsonArrHier.add(strKey);
				}
				Object dataFromMap = mapFinalMCInfo.get(strKey);
				if (dataFromMap instanceof Map) {
					mapMCData = (Map<String, String>) mapFinalMCInfo.get(strKey);
					jsonObjKeyVariant = Json.createObjectBuilder();
					jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
					nextJSONFormat(mapMCData, strKey, strValue, jsonObjKeyVariant);
					jsonArrCopyListData.add(jsonObjKeyVariant.build());
				} else if (dataFromMap instanceof MapList) {
					MapList mapListData = (MapList) dataFromMap;
					for (int i = 0; i < mapListData.size(); i++) {
						mapMCData = (Map<String, String>) mapListData.get(i);
						jsonObjKeyVariant = Json.createObjectBuilder();
						jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
						nextJSONFormat(mapMCData, strKey, strValue, jsonObjKeyVariant);
						jsonArrCopyListData.add(jsonObjKeyVariant.build());
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			JsonArrayBuilder jsonArrErrCopyListData = Json.createArrayBuilder();
			JsonObjectBuilder jsonObjErrKeyVariant = Json.createObjectBuilder();
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonArrErrCopyListData.add(jsonObjErrKeyVariant);
		}
		return jsonArrCopyListData;
	}

	/**
	 * This method is to update GPS Attribute
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String updateGPSAddressAttributes(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		JsonArray jsonDataArray = jsonInputData.getJsonArray(PGWidgetConstants.KEY_DATA);
		try {
			for (int i = 0; i < jsonDataArray.size(); i++) {
				JsonObject jsonInputObj = jsonDataArray.getJsonObject(i);
				String strMCObjId = jsonInputObj.getString(PGWidgetConstants.KEY_OBJECTID);
				Map<String, String> attributeMap = PGWidgetUtil.getMapFromJson(context, jsonInputObj);
				attributeMap.remove(PGWidgetConstants.KEY_OBJECTID);
				if (BusinessUtil.isNotNullOrEmpty(strMCObjId) && null != attributeMap) {
					DomainObject domMCId = DomainObject.newInstance(context, strMCObjId);
					AttributeList attributeList = new AttributeList();
					for (Entry entry : attributeMap.entrySet()) {
			            String attrName = (String) entry.getKey();
			            StringList slAttributeValues = FrameworkUtil.split((String)entry.getValue(), PGWidgetConstants.KEY_COMMA_SEPARATOR);
			            AttributeType attributeType = new AttributeType(attrName);
						Attribute attribute = new Attribute(attributeType, slAttributeValues);
						attributeList.add(attribute);
			        }
					domMCId.setAttributes(context,attributeList);
				}
			}
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return output.build().toString();
	}

	/**
	 * The method returns the sub type list
	 * 
	 * @param context
	 * @return The full list of sub type
	 */
	public static Object getSubTypeList(Context context) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonData = Json.createObjectBuilder();
		try {

			String strType = AWLType.MASTER_ARTWORK_ELEMENT.get(context);
			String subtypes = MqlUtil.mqlCommand(context, "print type $1 select $2 dump $3", strType, "derivative",
					"|");

			if (BusinessUtil.isNotNullOrEmpty(subtypes)) {
				StringList strSubCopyTypeList = FrameworkUtil.split(subtypes, PGWidgetConstants.KEY_PIPE_SEPARATOR);

				for (String strSubType : strSubCopyTypeList) {
					JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
					JsonObjectBuilder jsonObjKeyVariant = Json.createObjectBuilder();
					StringList slSubTypesList = fetchTypeSubCopyTypes(context, strSubType);

					if (BusinessUtil.isNotNullOrEmpty(slSubTypesList)) {

						for (String slStrSubType : slSubTypesList) {

							if (BusinessUtil.isNotNullOrEmpty(slStrSubType)) {
								jsonObjKeyVariant.add(PGWidgetConstants.KEY_NAME, slStrSubType);
								jsonObjKeyVariant.add(PGWidgetConstants.KEY_VALUE, slStrSubType);
							}
							jsonArrObjInfo.add(jsonObjKeyVariant);
						}
					}
					jsonData.add(strSubType, jsonArrObjInfo);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build();
		}
		return jsonData.build().toString();
	}

	/**
	 * Method to get Sub Copy Type for the MC Type
	 * 
	 * @param context
	 * @param strType
	 */
	public static StringList fetchTypeSubCopyTypes(Context context, String strType) throws Exception {
		StringList strSubCopyTypeList = new StringList();
		try {
			if (BusinessUtil.isNullOrEmpty(strType)) {
				return strSubCopyTypeList;
			}

			String strSelToSubCopyTypes = new StringBuilder("from[")
					.append(EditCLConstants.RELATIONSHIP_PGPLCOPYELEMENTTOSUBCOPY).append("].to.name").toString();
			MapList mlSubCopyTypes = DomainObject.findObjects(context, 
					EditCLConstants.TYPE_PGPLICOPYELEMENTTYPE,  // type pattern
					strType, 									// name pattern
					DomainConstants.QUERY_WILDCARD, 			// revision pattern
					DomainConstants.QUERY_WILDCARD, 			// owner pattern
					ArtworkConstants.VAULT_ESERVICE_PRODUCTION,	// Vault Pattern
					EditCLConstants.CURRENT_EQUALS_ACTIVE,		// where expression
					null,										// queryName
					false,										// expand type
					StringList.create(DomainConstants.SELECT_ID),// object selects
					(short) 0);									// object limit
			if (BusinessUtil.isNotNullOrEmpty(mlSubCopyTypes)) {
				String strOID = BusinessUtil.getString((Map) mlSubCopyTypes.get(0), DomainConstants.SELECT_ID);
				if (BusinessUtil.isNotNullOrEmpty(strOID)) {
					strSubCopyTypeList = DomainObject.newInstance(context, strOID).getInfoList(context,
							strSelToSubCopyTypes);
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
		return strSubCopyTypeList;
	}

	/**
	 * Method to fetch CL Mother and Child Information
	 * 
	 * @param context
	 * @param strCLLangMotherChildInfo CL Attribute contains Mother Child Info
	 * @return HashMap
	 * @throws Exception
	 */
	private static HashMap getCLLangMotherChildInfo(Context context, String strCLLangMotherChildInfo) throws Exception {
		HashMap mCLLangMotherChildInfo = new HashMap();
		try {
			if (BusinessUtil.isNotNullOrEmpty(strCLLangMotherChildInfo)) {
				HashMap mMotherChildInfo = new HashMap();
				HashMap mChildMotherInfo = new HashMap();
				StringList slLangSel = StringList.create(DomainConstants.SELECT_NAME, DomainConstants.SELECT_ID);
				StringList slMotherChild = new StringList();
				StringList slSplit = FrameworkUtil.split(strCLLangMotherChildInfo,
						PGWidgetConstants.KEY_DOLLAR_SEPERATOR);
				slMotherChild.addAll(slSplit);
				for (String strMotherChild : (List<String>) slMotherChild) {
					String strMother = strMotherChild.substring(0,
							strMotherChild.indexOf(PGWidgetConstants.KEY_COLON_SEPERATOR));
					String strChild = strMotherChild.substring(
							strMotherChild.indexOf(PGWidgetConstants.KEY_COLON_SEPERATOR) + 1, strMotherChild.length());
					StringList slChild = FrameworkUtil.split(strChild, PGWidgetConstants.KEY_COMMA_SEPARATOR);
					MapList mlLangInfo = BusinessUtil.getInfo(context, slChild, slLangSel);
					mMotherChildInfo.put(strMother, mlLangInfo);
					Iterator itr = mlLangInfo.iterator();
					Map mMotherLangInfo = new HashMap();
					mMotherLangInfo.put(DomainConstants.SELECT_ID, strMother);
					while (itr.hasNext()) {
						Map mLangInfo = (Map) itr.next();
						mChildMotherInfo.put((String) mLangInfo.get(DomainConstants.SELECT_ID), mMotherLangInfo);
					}
				}
				mCLLangMotherChildInfo.put(EditCLConstants.KEY_CHILDMOTHER, mChildMotherInfo);
				mCLLangMotherChildInfo.put(EditCLConstants.KEY_MOTHERCHILD, mMotherChildInfo);
			}
		} catch (Exception ex) {
			throw ex;
		}
		return mCLLangMotherChildInfo;
	}

	/***
	 * This method is called to set the Mother Child information on Copy List
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String setCopyListMotherChild(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strReturnMsg = PGWidgetConstants.KEY_EMPTY_STRING;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strCLId = jsonInputData.getString(EditCLConstants.STR_COPYLIST_ID);
			if (BusinessUtil.isNullOrEmpty(strCLId)) {
				return DomainConstants.EMPTY_STRING;
			}
			JsonObject jsonObjMotherChild = jsonInputData.getJsonObject(EditCLConstants.KEY_MOTHERCHILDS);
			StringBuilder sbMotherChild = new StringBuilder();
			Map<String, StringList> mMotherChildInfo = new HashMap<>();
			Set<String> entrySet = jsonObjMotherChild.keySet();

			for (String strMotherKey : entrySet) {
				String strChildValues = jsonObjMotherChild.getString(strMotherKey);
				if (sbMotherChild != null && sbMotherChild.length() > 0) {
					sbMotherChild.append(PGWidgetConstants.KEY_DOLLAR_SEPERATOR);
				}
				sbMotherChild.append(strMotherKey).append(PGWidgetConstants.KEY_COLON_SEPERATOR).append(strChildValues);
				mMotherChildInfo.put(strMotherKey,
						FrameworkUtil.split(strChildValues, PGWidgetConstants.KEY_COMMA_SEPARATOR));
			}
			BusinessUtil.setAttribute(context, strCLId, EditCLConstants.ATTRIBUTE_PGLANGMOTHERCHILDINFO,
					sbMotherChild.toString());
			if(sbMotherChild.length() > 0){		
				updateMotherContentToChild(context, strCLId, mMotherChildInfo);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		strReturnMsg = PGWidgetConstants.KEY_SUCCESS;
		output.add(PGWidgetConstants.KEY_STATUS, strReturnMsg);
		return output.build().toString();
	}

	/**
	 * Set the Mother CE Copy Text to child CEs.
	 * 
	 * @param context
	 * @param strCLId          Copy List Id
	 * @param mMotherChildInfo List of child CE ids to copy Mother CE Copy Text
	 * @throws Exception
	 */

	public static void updateMotherContentToChild(Context context, String strCLId,
			Map<String, StringList> mMotherChildInfo) throws Exception {

		if (BusinessUtil.isNullOrEmpty(strCLId)) {
			return;
		}
		String strCEStatePreliminary = AWLState.PRELIMINARY.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT);
		String strSelContentLanguage = "from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id";
		String strSelCopyTextRTE = DomainObject.getAttributeSelect(AWLAttribute.COPY_TEXT.get(context) + "_RTE");
		String strSelPrentId = "to[" + AWLRel.ARTWORK_ELEMENT_CONTENT.get(context) + "].from.id";
		CopyList copyList = new CopyList(strCLId);
		StringList slBusSel = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT,
				strSelPrentId, strSelCopyTextRTE, strSelContentLanguage);

		String strMotherLangId = null;
		MapList mlLCs = copyList.getArtworkElements(context, slBusSel, null, false);
		MapList mlMCLCs = null;

		Map mLCInfo = null;
		Map mMCLCInfo = new HashMap();
		Map mChildMotherKey = new HashMap();

		for (Iterator langKeyIter = mMotherChildInfo.keySet().iterator(); langKeyIter.hasNext();) {
			String strMotherKey = (String) langKeyIter.next();
			StringList slChildCEIds = mMotherChildInfo.get(strMotherKey);
			for (String strChildId : slChildCEIds) {
				mChildMotherKey.put(strChildId, strMotherKey);
			}
		}

		Iterator itr = mlLCs.iterator();
		while (itr.hasNext()) {
			mLCInfo = (Map) itr.next();
			String strMCId = (String) mLCInfo.get(strSelPrentId);
			strMotherLangId = (String) mLCInfo.get(strSelContentLanguage);
			if (mChildMotherKey.containsKey(strMotherLangId)) {
				mLCInfo.put(EditCLConstants.KEY_MOTHER, mChildMotherKey.get(strMotherLangId));
			}
			if (BusinessUtil.isNotNullOrEmpty(strMCId)) {
				if (mMCLCInfo.containsKey(strMCId)) {
					mlMCLCs = (MapList) mMCLCInfo.get(strMCId);
				} else {
					mlMCLCs = new MapList();
				}
				mlMCLCs.add(mLCInfo);
				mMCLCInfo.put(strMCId, mlMCLCs);
			}
		}

		for (Iterator ceKeyIter = mMCLCInfo.keySet().iterator(); ceKeyIter.hasNext();) {
			mlMCLCs = (MapList) mMCLCInfo.get(ceKeyIter.next());
			itr = mlMCLCs.iterator();
			Map mMotherLangContentInfo = new HashMap();
			while (itr.hasNext()) {
				mLCInfo = (Map) itr.next();
				strMotherLangId = (String) mLCInfo.get(strSelContentLanguage);
				if (mMotherChildInfo.containsKey(strMotherLangId)) {
					String strMotherContent = (String) mLCInfo.get(strSelCopyTextRTE);
					mMotherLangContentInfo.put(strMotherLangId, mLCInfo.get(strSelCopyTextRTE));
				}
			}
			itr = mlMCLCs.iterator();
			while (itr.hasNext()) {
				mLCInfo = (Map) itr.next();
				String strLCState = (String) mLCInfo.get(DomainConstants.SELECT_CURRENT);
				if (strCEStatePreliminary.equals(strLCState)) {
					strMotherLangId = (String) mLCInfo.get(EditCLConstants.KEY_MOTHER);
					String strLCId = (String) mLCInfo.get(DomainConstants.SELECT_ID);
					if (BusinessUtil.isNotNullOrEmpty(strLCId) && mMotherLangContentInfo.containsKey(strMotherLangId)) {
						new CopyElement(strLCId).setCopyText(context,
								(String) mMotherLangContentInfo.get(strMotherLangId));
					}
				}
			}
		}
	}

	/**
	 * This method is to update Copy Text for LCs
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String updateLCCopyText(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);

		try {
			String strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
			String strLCIds = jsonInputData.getString(EditCLConstants.KEY_LCE_IDS);
			String strContent = jsonInputData.containsKey(EditCLConstants.KEY_NEW_COPY_TEXT)
					? jsonInputData.getString(EditCLConstants.KEY_NEW_COPY_TEXT)
					: DomainConstants.EMPTY_STRING;
			StringList lcIdList = FrameworkUtil.split(strLCIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			String strCLMotherChildInfo = null;
			StringList slCLLCIds = new StringList();

			if (BusinessUtil.isNotNullOrEmpty(strCopyListId)) {
				strCLMotherChildInfo = BusinessUtil.getInfo(context, strCopyListId,
						EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO);
				MapList mlCopyListLCs = DomainObject.newInstance(context, strCopyListId).getRelatedObjects(context,
						AWLRel.ARTWORK_ASSEMBLY.get(context), 
						AWLType.ARTWORK_ELEMENT.get(context),
						StringList.create(DomainConstants.SELECT_ID), 
						null, 
						false,
						true,
						(short) 0, 
						null,
						null, 0);

				slCLLCIds = BusinessUtil.toStringList(mlCopyListLCs, DomainConstants.SELECT_ID);
			}

			StringBuilder sbfailedLcMsg = new StringBuilder();
			StringList slFailedLCIds = new StringList();
			for (String strLCObjId : (List<String>) lcIdList) {
				if (BusinessUtil.isNotNullOrEmpty(strLCObjId)) {
					try {
						new CopyElement(strLCObjId).setCopyText(context, strContent);
						if (BusinessUtil.isNotNullOrEmpty(strCLMotherChildInfo)
								&& BusinessUtil.isNotNullOrEmpty(slCLLCIds)) {
							syncMotherContentToChildLCs(context, strCLMotherChildInfo, strLCObjId, strContent,
									slCLLCIds);
						}
					} catch (Exception e) {
						slFailedLCIds.add(BusinessUtil.getInfo(context, strLCObjId, DomainObject.SELECT_NAME));
					}
				}
			}
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			if (slFailedLCIds.size() > 0) {
				sbfailedLcMsg
						.append(EnoviaResourceBundle.getProperty(context, "emxAWLStringResource", context.getLocale(),
								"emxAWL.Alert.contentUpdateFailed"))
						.append(PGWidgetConstants.KEY_NEW_LINE)
						.append(slFailedLCIds.join(PGWidgetConstants.KEY_COMMA_SEPARATOR));
				output.add(EditCLConstants.STR_MESSAGE, sbfailedLcMsg.toString());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return output.build().toString();
	}

	/***
	 * This method called to get the difference on Mother and Child CE Content
	 * 
	 * @param context
	 * @param mapFinalMCInfo
	 * @param strCLMotherChildInfo
	 * @throws Exception
	 */
	private static void motherChildCEContentDifference(Context context, Map<String, Object> mapFinalMCInfo,
			String strCLMotherChildInfo) throws Exception {
		try {
			String strKey = null;
			String strValue = DomainConstants.EMPTY_STRING;
			MapList mapListData = new MapList();
			Map mapMCData = new HashMap();

			HashMap mCLLangMotherChildInfo = getCLLangMotherChildInfo(context, strCLMotherChildInfo);

			HashMap mMotherChildInfo = (mCLLangMotherChildInfo.containsKey(EditCLConstants.KEY_MOTHERCHILD))
					? (HashMap) mCLLangMotherChildInfo.get(EditCLConstants.KEY_MOTHERCHILD)
					: new HashMap<>();
			HashMap mChildMotherInfo = (mCLLangMotherChildInfo.containsKey(EditCLConstants.KEY_CHILDMOTHER))
					? (HashMap) mCLLangMotherChildInfo.get(EditCLConstants.KEY_CHILDMOTHER)
					: new HashMap<>();

			Map<String, String> mLCCopyTextInfo = null;
			Map<String, Map<String, String>> mLCLangInfo = new HashMap();

			for (Entry<String, Object> entry : mapFinalMCInfo.entrySet()) {

				strKey = entry.getKey();

				Object dataFromMap = mapFinalMCInfo.get(strKey);
				if (dataFromMap instanceof Map) {
					mLCCopyTextInfo = new HashMap<String, String>();
					mapMCData = (Map<String, String>) mapFinalMCInfo.get(strKey);
					mLCCopyTextInfo.put(EditCLConstants.KEY_CONTENT,
							(String) mapMCData.get(EditCLConstants.KEY_CONTENT));
					mLCCopyTextInfo.put(EditCLConstants.STR_LANGUAGE_ID,
							(String) mapMCData.get(EditCLConstants.STR_LANGUAGE_ID));
					mLCCopyTextInfo.put(EditCLConstants.KEY_PARENT_ID,
							(String) mapMCData.get(EditCLConstants.KEY_PARENT_ID));
					mLCLangInfo.put((String) mapMCData.get(DomainConstants.SELECT_ID), mLCCopyTextInfo);
				} else if (dataFromMap instanceof MapList) {
					mapListData = (MapList) dataFromMap;
					for (int i = 0; i < mapListData.size(); i++) {
						mLCCopyTextInfo = new HashMap<String, String>();
						mapMCData = (Map<String, String>) mapListData.get(i);
						mLCCopyTextInfo.put(EditCLConstants.KEY_CONTENT,
								(String) mapMCData.get(EditCLConstants.KEY_CONTENT));
						mLCCopyTextInfo.put(EditCLConstants.STR_LANGUAGE_ID,
								(String) mapMCData.get(EditCLConstants.STR_LANGUAGE_ID));
						mLCCopyTextInfo.put(EditCLConstants.KEY_PARENT_ID,
								(String) mapMCData.get(EditCLConstants.KEY_PARENT_ID));
						mLCLangInfo.put((String) mapMCData.get(DomainConstants.SELECT_ID), mLCCopyTextInfo);
					}
				}
			}
			for (Entry<String, Object> entry : mapFinalMCInfo.entrySet()) {
				strKey = entry.getKey();
				Object dataFromMap = mapFinalMCInfo.get(strKey);
				if (dataFromMap instanceof Map) {
					mapMCData = (Map<String, String>) mapFinalMCInfo.get(strKey);
					compareMotherChildCEContent(mapMCData, mChildMotherInfo, mLCLangInfo);
				} else if (dataFromMap instanceof MapList) {
					mapListData = (MapList) dataFromMap;
					for (int i = 0; i < mapListData.size(); i++) {
						mapMCData = (Map<String, String>) mapListData.get(i);
						compareMotherChildCEContent(mapMCData, mChildMotherInfo, mLCLangInfo);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			JsonArrayBuilder jsonArrErrCopyListData = Json.createArrayBuilder();
			JsonObjectBuilder jsonObjErrKeyVariant = Json.createObjectBuilder();
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}

	}

	public static void compareMotherChildCEContent(Map mapMCData, Map mChildMotherInfo,
			Map<String, Map<String, String>> mLCLangInfo) throws Exception {
		String strMotherLangId = new String();
		String strMotherContent = new String();
		String strLCId = (String) mapMCData.get(DomainConstants.SELECT_ID);
		String strLangId = (String) mapMCData.get(EditCLConstants.STR_LANGUAGE_ID);
		if (mChildMotherInfo.containsKey(strLangId)) {
			HashMap<String, String> innerMapResult = (HashMap<String, String>) mChildMotherInfo.get(strLangId);
			if (innerMapResult != null) {
				strMotherLangId = innerMapResult.get(DomainConstants.SELECT_ID);
			}
			String strMCId = (String) mapMCData.get(EditCLConstants.KEY_PARENT_ID);
			String strChildContent = (String) mapMCData.get(EditCLConstants.KEY_CONTENT);
			for (Entry<String, Map<String, String>> contentEntry : mLCLangInfo.entrySet()) {
				Map mLCCopyTextInfo = mLCLangInfo.get(contentEntry.getKey());
				if (BusinessUtil.isNotNullOrEmpty(strMotherLangId) && BusinessUtil.isNotNullOrEmpty(strMCId)
						&& strMotherLangId.equals(mLCCopyTextInfo.get(EditCLConstants.STR_LANGUAGE_ID))
						&& strMCId.equals(mLCCopyTextInfo.get(EditCLConstants.KEY_PARENT_ID))) {
					strMotherContent = (String) mLCCopyTextInfo.get(EditCLConstants.KEY_CONTENT);
					break;
				}
			}
			if (UIUtil.isNotNullAndNotEmpty(strChildContent) && !strChildContent.equals(strMotherContent)) {
				mapMCData.put(EditCLConstants.KEY_MOTHERCONTENT, strMotherContent);
				mapMCData.put(EditCLConstants.KEY_CONTENTMISMATCH, AWLConstants.RANGE_TRUE);
			} else {
				mapMCData.put(EditCLConstants.KEY_CONTENTMISMATCH, AWLConstants.RANGE_FALSE);
			}
		}
	}

	/**
	 * fetching regions as Product Type
	 * 
	 * @param context
	 * @param paramString JSON string containing input parameters
	 * @return String JSON response
	 * @throws Exception
	 */
	public static String getRegionsAsProdType(Context context, String paramString) throws Exception {
		StringList busSelect = new StringList();
		StringList relSelect = new StringList();
		busSelect.add(DomainConstants.SELECT_ID);
		busSelect.add(DomainConstants.SELECT_NAME);
		busSelect.add(RTAUtilConstants.SELECT_MARKETINGNAME);
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		JsonObjectBuilder jsonInputObj = Json.createObjectBuilder();
		JsonObjectBuilder jsonOutputObj = Json.createObjectBuilder();
		JsonObjectBuilder output = Json.createObjectBuilder();
		
		try {
			BusinessObject boObject = new BusinessObject(RTAUtilConstants.TYPE_PRODUCTTYPE,
					EditCLConstants.COPY_MANAGER_ROOT_NODE_NAME, pgV3Constants.SYMBOL_HYPHEN,
					ArtworkConstants.VAULT_ESERVICE_PRODUCTION);
			DomainObject doObject = DomainObject.newInstance(context, boObject);
			MapList mlRegions = doObject.getRelatedObjects(context, AWLRel.SUB_PRODUCT_LINES.get(context),
					RTAUtilConstants.TYPE_PRODUCTTYPE, busSelect, 
					relSelect,
					false,
					true,
					(short) 1,
					null,
					null, 0);

			logger.log(Level.INFO, "mlRegions: " + mlRegions);
			if (BusinessUtil.isNotNullOrEmpty(mlRegions)) {
				Iterator itrRegions = mlRegions.iterator();
				while (itrRegions.hasNext()) {
					Map mpRegion = (Map) itrRegions.next();
					String busId = (String) mpRegion.get(DomainConstants.SELECT_ID);
					String busName = (String) mpRegion.get(DomainConstants.SELECT_NAME);
					String regionName = (String) mpRegion.get(RTAUtilConstants.SELECT_MARKETINGNAME);
					JsonObjectBuilder jsonObj = Json.createObjectBuilder();
					jsonObj.add(DomainConstants.SELECT_ID, busId);
					jsonObj.add(DomainConstants.SELECT_NAME, regionName);
					jsonArray.add(jsonObj);
				}
			}
			jsonOutputObj.add(EditCLConstants.KEY_REGIONS, jsonArray);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			return output.build().toString();
		}
		return jsonOutputObj.build().toString();
	}

	/**
	 * method called to get Copy Manager Table Data
	 * 
	 * @param context
	 * @param paramString JSON string containing input parameters
	 * @return String JSON response
	 * @throws Exception
	 */
	public static String getCopyManagerTableData(Context context, String paramString) throws Exception {

		StringList busSelect = new StringList();
		StringList relSelect = new StringList(DomainConstants.SELECT_LEVEL);
		boolean flag;
		boolean isContextPushed = false;
		boolean isValidProd = false;
		busSelect.add(DomainConstants.SELECT_ID);
		busSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
		busSelect.add(DomainConstants.SELECT_NAME);
		busSelect.add(DomainConstants.SELECT_CURRENT);
		busSelect.add(DomainConstants.SELECT_TYPE);
		busSelect.add(RTAUtilConstants.SELECT_MARKETINGNAME);
		busSelect.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		
		JsonArrayBuilder arrRegionData = Json.createArrayBuilder();
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			if (BusinessUtil.isNullOrEmpty(paramString)) {
				return arrRegionData.build().toString();
			}
			//Need to display the OU Types for all the users
			ContextUtil.pushContext(context, ArtworkConstants.PERSON_USERAGENT,null,context.getVault().getName());
			isContextPushed = true;
			StringList prodTypeStringList = FrameworkUtil.split(RTAUtilConstants.PRODUCT_TYPES_TOBE_SELECTED,
					PGWidgetConstants.KEY_COMMA_SEPARATOR);
			DomainObject doObject = DomainObject.newInstance(context, paramString);
			MapList mlOus = doObject.getRelatedObjects(context, AWLRel.SUB_PRODUCT_LINES.get(context),
					RTAUtilConstants.TYPE_PRODUCTTYPE, busSelect,
					relSelect,
					false,
					true,
					(short) 1,
					null,
					null, 0);
			logger.log(Level.INFO, "mlOus: " + mlOus);
			if (BusinessUtil.isNullOrEmpty(mlOus)) {
				return arrRegionData.build().toString();
			}
			Iterator itrOus = mlOus.iterator();
			while (itrOus.hasNext()) {
				Map mpOu = (Map) itrOus.next();
				String strOuId = (String) mpOu.get(DomainConstants.SELECT_ID);
				String strOuName = (String) mpOu.get(RTAUtilConstants.SELECT_MARKETINGNAME);
				logger.log(Level.INFO, "strOuName: " + strOuName);
				JsonObjectBuilder rowRegionData = Json.createObjectBuilder();
				rowRegionData.add(EditCLConstants.KEY_OU, strOuName);
				rowRegionData.add(EditCLConstants.KEY_OUID, strOuId);
				Pattern relPattern = new Pattern(AWLRel.PRODUCT_LINE_MODELS.get(context));
				relPattern.addPattern(ProductLineConstants.RELATIONSHIP_MAIN_PRODUCT);
				relPattern.addPattern(AWLRel.SUB_PRODUCT_LINES.get(context));
				Pattern typePattern = new Pattern(AWLType.MODEL.get(context));
				typePattern.addPattern(AWLType.CPG_PRODUCT.get(context));
				typePattern.addPattern(RTAUtilConstants.TYPE_PRODUCTTYPE);
				DomainObject doOu = DomainObject.newInstance(context, strOuId);
				MapList mlProducts = doOu.getRelatedObjects(context, relPattern.getPattern(),
						typePattern.getPattern(), busSelect,
						relSelect,
						false,
						true,
						(short) 2,
						null,
						null, 0);
				logger.log(Level.INFO, "mlProducts: " + mlProducts);
				if (BusinessUtil.isNotNullOrEmpty(mlProducts)) {
					Iterator itrProducts = mlProducts.iterator();
					isValidProd =false;
					while (itrProducts.hasNext()) {
						Map mpProd = (Map) itrProducts.next();
						String strType = (String) mpProd.get(DomainConstants.SELECT_TYPE);
						String strLevel=(String)mpProd.get(DomainConstants.SELECT_LEVEL);
						String strProdId = (String) mpProd.get(DomainConstants.SELECT_ID);
						String strProdName = (String) mpProd.get(RTAUtilConstants.SELECT_MARKETINGNAME);
						logger.log(Level.INFO, "strProdName: " + strProdName);
						flag = false;
						for(int i=0; i<prodTypeStringList.size(); i++) {
							if(strProdName.startsWith(prodTypeStringList.get(i))) {
								flag = true;
								break;
							}
						}	
						logger.log(Level.INFO, "flag: " + flag);

						if ((("1".equals(strLevel) && RTAUtilConstants.TYPE_PRODUCTTYPE.equals(strType)) || BusinessUtil.isKindOf(context, strProdId, AWLType.CPG_PRODUCT.get(context))) && flag) {	
							isValidProd=true;
							rowRegionData.add(DomainConstants.SELECT_TYPE, strProdName);
							rowRegionData.add(EditCLConstants.KEY_TYPEID, strProdId);

							relPattern = new Pattern(AWLRel.ASSOCIATED_COPY_LIST.get(context));
							typePattern = new Pattern(AWLType.COPY_LIST.get(context));
							DomainObject doProd = DomainObject.newInstance(context, strProdId);
							MapList mlCopyList = doProd.getRelatedObjects(context, relPattern.getPattern(),
									typePattern.getPattern(), busSelect,
									relSelect,
									false,
									true,
									(short) 2,
									null,
									null, 0);

							logger.log(Level.INFO, "mlCopyList: " + mlCopyList);

							if (BusinessUtil.isNotNullOrEmpty(mlCopyList)) {
								Iterator itrCopyList = mlCopyList.iterator();
								while (itrCopyList.hasNext()) {
									Map mpCopyList = (Map) itrCopyList.next();
									String strCLId = (String) mpCopyList.get(DomainConstants.SELECT_ID);
									String strCLPId = (String) mpCopyList
											.get(DomainConstants.SELECT_PHYSICAL_ID);

									String strCLName = (String) mpCopyList.get(DomainConstants.SELECT_NAME);
									String strCLState = (String) mpCopyList.get(DomainConstants.SELECT_CURRENT);
									if (strCLState.contains(PGWidgetConstants.KEY_PRILIMINARY)
											|| strCLState.contains(PGWidgetConstants.KEY_RELEASE)) {
										JsonObjectBuilder CopyListObj = Json.createObjectBuilder();
										CopyListObj.add(PGWidgetConstants.KEY_VALUE, strCLName);
										CopyListObj.add(EditCLConstants.KEY_P_ID, strCLPId);												

										rowRegionData.add(EditCLConstants.KEY_STATUS, strCLState);
										rowRegionData.add(EditCLConstants.KEY_COPYLIST, CopyListObj);
										rowRegionData.add(EditCLConstants.KEY_TITLE, (String) mpCopyList.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
										
										arrRegionData.add(rowRegionData);
									}
								}
							}else {
								rowRegionData.add(EditCLConstants.KEY_STATUS, DomainConstants.EMPTY_STRING);
								rowRegionData.add(EditCLConstants.KEY_COPYLIST, Json.createObjectBuilder());
								rowRegionData.add(EditCLConstants.KEY_TITLE, DomainConstants.EMPTY_STRING);
								arrRegionData.add(rowRegionData);
							}
						}
					} if(!isValidProd) {
						arrRegionData.add(rowRegionData);								
					}
				}else {
					arrRegionData.add(rowRegionData);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();

		} finally {			
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return arrRegionData.build().toString();
	}

	/***
	 * Method to copy the new content value to child LCs
	 * 
	 * @param context
	 * @param strCLMotherChildInfo
	 * @param strLCID
	 * @param strNewContent
	 * @param slCLLCIds
	 * @throws Exception
	 */
	public static void syncMotherContentToChildLCs(Context context, String strCLMotherChildInfo, String strLCID,
			String strNewContent, StringList slCLLCIds) throws Exception {
		try {
			if (BusinessUtil.isNullOrEmpty(strCLMotherChildInfo) || BusinessUtil.isNullOrEmpty(strLCID)) {
				return;
			}

			String strCEStatePreliminary = AWLState.PRELIMINARY.get(context, AWLPolicy.ARTWORK_ELEMENT_CONTENT);
			String strSelLCLang = AWLUtil.strcat("from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id");
			String strLCLang = BusinessUtil.getInfo(context, strLCID, strSelLCLang);
			HashMap mCLLangMotherChildInfo = getCLLangMotherChildInfo(context, strCLMotherChildInfo);

			HashMap mMotherChildInfo = (mCLLangMotherChildInfo.containsKey(EditCLConstants.KEY_MOTHERCHILD))
					? (HashMap) mCLLangMotherChildInfo.get(EditCLConstants.KEY_MOTHERCHILD)
					: new HashMap<>();

			if (mMotherChildInfo.containsKey(strLCLang)) {

				String strSelContentLanguage = "from[" + AWLRel.CONTENT_LANGUAGE.get(context) + "].to.id";
				StringList slBusSel = StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_CURRENT,
						strSelContentLanguage);
				ArtworkContent element = ArtworkContent.getNewInstance(context, strLCID);
				MapList mlCEs = element.getArtworkMaster(context).getArtworkElements(context, slBusSel, null, null);

				Iterator itr = mlCEs.iterator();
				Map mLCUpdateContent = new HashMap();

				while (itr.hasNext()) {
					Map mLCInfo = (Map) itr.next();
					String strCurrent = (String) mLCInfo.get(DomainConstants.SELECT_CURRENT);
					String strCEId = (String) mLCInfo.get(DomainConstants.SELECT_ID);
					if (strCEStatePreliminary.equals(strCurrent) && slCLLCIds.contains(strCEId)) {
						mLCUpdateContent.put(BusinessUtil.getString(mLCInfo, strSelLCLang), strCEId);
					}
				}
				StringList slChildLangIds = BusinessUtil.toStringList((MapList) mMotherChildInfo.get(strLCLang),
						DomainConstants.SELECT_ID);
				for (String strChildLangId : slChildLangIds) {
					if (BusinessUtil.isNotNullOrEmpty(strChildLangId) && mLCUpdateContent.containsKey(strChildLangId)) {
						new CopyElement((String) mLCUpdateContent.get(strChildLangId)).setCopyText(context,
								strNewContent);
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			JsonArrayBuilder jsonArrErrCopyListData = Json.createArrayBuilder();
			JsonObjectBuilder jsonObjErrKeyVariant = Json.createObjectBuilder();
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonObjErrKeyVariant.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
	}

	/**
	 * Method to update the Copy List Mother Child value on deletion of Language.
	 * 
	 * @param context
	 * @param paramString
	 * @return
	 * @throws Exception
	 */
	public static String updateMotherChildDependency(Context context, String paramString) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);

		try {
			String strCopyListId = jsonInputData.getString(EditCLConstants.COPYLIST_ID);
			String strCLMotherChildInfo = (BusinessUtil.isNotNullOrEmpty(strCopyListId)) ? BusinessUtil.getInfo(context, strCopyListId,
					EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO) : null;
			if (BusinessUtil.isNotNullOrEmpty(strCopyListId) && BusinessUtil.isNotNullOrEmpty(strCLMotherChildInfo)) {
				JsonArray jsonDataArray = jsonInputData.getJsonArray(EditCLConstants.KEY_LANGUAGEINFO);
				StringBuilder sbMotherChild = new StringBuilder();
				StringList slFinalLangIds = new StringList();
				
				for (int i = 0; i < jsonDataArray.size(); i++) {
					JsonObject jsonInputObj = jsonDataArray.getJsonObject(i);
					slFinalLangIds.add(jsonInputObj.getString(PGWidgetConstants.KEY_OBJECTID));
				}
				
				StringList slSplit = FrameworkUtil.split(strCLMotherChildInfo, PGWidgetConstants.KEY_DOLLAR_SEPERATOR);
				boolean isMotherChildInfoModified = false;
				for (String strMotherChild : (List<String>) slSplit) {
					String strMother = strMotherChild.substring(0,
							strMotherChild.indexOf(PGWidgetConstants.KEY_COLON_SEPERATOR));
					String strChild = strMotherChild.substring(strMotherChild.indexOf(PGWidgetConstants.KEY_COLON_SEPERATOR) + 1, strMotherChild.length());
					StringList slChild = FrameworkUtil.split(strChild, PGWidgetConstants.KEY_COMMA_SEPARATOR);
					StringBuilder sbChild = new StringBuilder();
					if(!slFinalLangIds.containsAll(slChild)) {
						isMotherChildInfoModified = true;
					}
					for(String strChildId : (List<String>) slChild) {
						if(slFinalLangIds.contains(strChildId)) {
							if(sbChild.length() > 0) {
								sbChild.append(",");
							}							
							sbChild.append(strChildId);
						}
					}
					if (!slFinalLangIds.contains(strMother) || BusinessUtil.isNullOrEmpty(sbChild.toString())) {
						isMotherChildInfoModified = true;
						continue;
					}
					if(sbMotherChild.length() > 0) {
						sbMotherChild.append("$");
					}
					sbMotherChild.append(strMother).append(":");
					sbMotherChild.append(sbChild.toString());	
				}
				if(isMotherChildInfoModified) {
					DomainObject.newInstance(context, strCopyListId).setAttributeValue(context, EditCLConstants.ATTRIBUTE_PGLANGMOTHERCHILDINFO, sbMotherChild.toString());
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EditCLConstants.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return output.build().toString();
		}
		return output.build().toString();
	}
	
	public static String updateLCEContent(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		Map<String, String> mObjAttrMap;
		try {
			JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
			JsonArray jsonInputArray = jsonInputInfo.getJsonArray("updatedData");

			String strExp = "";
			DomainObject doObj = null;
			
			String strCLMotherChildInfo = null;
			StringList slCLLCIds = null;
			String strSelAttributeCopyText = DomainObject.getAttributeSelect(AWLAttribute.COPY_TEXT.get(context));
			String strCLId = null;
			
			try {
				strCLId = jsonInputInfo.getString(EditCLConstants.COPYLIST_ID);
			}catch(Exception ex) {
				strCLId = null;
			}
			if(BusinessUtil.isNotNullOrEmpty(strCLId)) {
				strCLMotherChildInfo = BusinessUtil.getInfo(context, strCLId, EditCLConstants.SEL_ATTRIBUTE_PGLANGMOTHERCHILDINFO);
				MapList mlCopyListLCs = DomainObject.newInstance(context, strCLId).getRelatedObjects(context, 
						AWLRel.ARTWORK_ASSEMBLY.get(context), //Rel Pattern
						AWLType.ARTWORK_ELEMENT.get(context), //Type Pattern
						StringList.create(DomainConstants.SELECT_ID), // Object Select
						null, // rel Select
						false, // get To
						true, // get From
						(short) 0, // recurse level
						null, // where Clause
						null, 0);				

				slCLLCIds = BusinessUtil.toStringList(mlCopyListLCs, DomainConstants.SELECT_ID);				
			}
			for (int i = 0; i < jsonInputArray.size(); i++) {
				String strObjId = "";
				JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);
				if (jsonElement.containsKey(PGWidgetConstants.KEY_OBJECT_ID)) {
					strObjId = jsonElement.getString(PGWidgetConstants.KEY_OBJECT_ID);
				}
				mObjAttrMap = new HashMap<>();
				String strExpression = jsonElement.getString(PGWidgetConstants.KEY_EXPR);
				String strValue = jsonElement.getString(PGWidgetConstants.KEY_VALUE);
				strExp = PGWidgetUtil.getFormattedExpression(strExpression);
				if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
					mObjAttrMap.put(strExp, strValue);
				}
				if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
					doObj = DomainObject.newInstance(context, strObjId);
					if(strSelAttributeCopyText.equals(strExpression)) {
						new CopyElement(strObjId).setCopyText(context, strValue);
					} else {
						doObj.setAttributeValues(context, mObjAttrMap);
					}
					if(strSelAttributeCopyText.equals(strExpression) && BusinessUtil.isNotNullOrEmpty(strCLMotherChildInfo)) {
						syncMotherContentToChildLCs(context, strCLMotherChildInfo, strObjId, strValue, slCLLCIds);
					}
				}

			}
		} catch (FrameworkException e) {
			jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonStatus.add(PGWidgetConstants.KEY_MESSAGE, e.getMessage());
			jsonStatus.add(PGWidgetConstants.KEY_TRACE, MatrixException.getStackTrace(e));
			return jsonStatus.build().toString();
		}
		jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		return jsonStatus.build().toString();
	}
	
	public static String getLocalCopiesForCopyList(Context context, String strCLId) throws Exception {
		JsonObjectBuilder jsonObjOut = Json.createObjectBuilder();
		try {
			String strSelRoute = "from[" + AWLRel.ARTWORK_ASSEMBLY.get(context) + "].to."+DomainConstants.SELECT_ID;

			CopyList copyList = new CopyList(strCLId);

			long start = System.currentTimeMillis();
			
			StringList slLCEids = copyList.getInfoList(context, strSelRoute);

			logger.log(Level.INFO, "Time taken for loading ALL LCs ---------------> - {0}",
					(System.currentTimeMillis() - start));
			jsonObjOut.add(RTAUtilConstants.KEY_LCE_IDS, slLCEids.join(PGWidgetConstants.KEY_COMMA_SEPARATOR));
			return jsonObjOut.build().toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString());
			logger.log(Level.SEVERE, e.getMessage());
			jsonObjOut.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			return jsonObjOut.build().toString();
		}
	}
	
	/**
	 * Method to retrieve ranges for an attribute
	 * 
	 * @param context
	 * @param strAttributeNames
	 * @return
	 * @throws Exception
	 */
	public static JsonObject getCLAttributeRangeValues(Context context, String strAttributeNames) throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder jsonObj;
		JsonArrayBuilder jsonArray;
		StringList attributeNameList = new StringList();
		if (UIUtil.isNotNullAndNotEmpty(strAttributeNames)) {
			attributeNameList.addAll(strAttributeNames.split(PGWidgetConstants.KEY_COMMA_SEPARATOR));
		}
		String strLanguage = context.getLocale().getLanguage();
		int attributeNameSize = attributeNameList.size();
		String strAttributeName;
		String strAttrNameActual;
		String strRangeActual;
		String strRangeDisplay;
		StringList slRanges = new StringList();
		for (int i = 0; i < attributeNameSize; i++) {
			strAttributeName = attributeNameList.get(i);
			if (UIUtil.isNotNullAndNotEmpty(strAttributeName) && slRanges != null) {
				slRanges.clear();
				strAttrNameActual = PGWidgetUtil.getFormattedExpression(strAttributeName);
				slRanges = FrameworkUtil.getRanges(context, strAttrNameActual);
				String preSelectedValue = DomainConstants.EMPTY_STRING;
		        Comparator<String> customComparator = new Comparator<String>() {
			        List<String> sortOrder = Arrays.asList(sortingOrder.split("\\|"));
			        public int compare(String s1, String s2) {
				        if (s1.equals(preSelectedValue)) {
				            return -1;
				        } else if (s2.equals(preSelectedValue)) {
				            return 1;
				        } else {
				            int index1 = sortOrder.indexOf(s1);
				            int index2 = sortOrder.indexOf(s2);
				            return Integer.compare(index1, index2);
				        }
			        }
		        };
		        slRanges.sort(customComparator);
				jsonArray = Json.createArrayBuilder();
				if (slRanges != null) {
					for (int k = 0; k < slRanges.size(); k++) {
						jsonObj = Json.createObjectBuilder();
						strRangeActual = slRanges.get(k);
						strRangeDisplay = i18nNow.getRangeI18NString(strAttrNameActual, strRangeActual, strLanguage);
						jsonObj.add(PGWidgetConstants.KEY_NAME, strRangeActual);
						jsonObj.add(PGWidgetConstants.KEY_VALUE, strRangeDisplay);
						jsonArray.add(jsonObj);
					}
				}

				output.add(strAttributeName, jsonArray.build());
			}
		}
		return output.build();
	}
	
	
	private static void fetchCLRegionInfo(Context context, String strCLId, Map mCLInfo) throws Exception{
		
		
		String strTypeCPGProd = AWLType.CPG_PRODUCT.get(context);
		
		StringList strBusSelectList = new StringList();
		strBusSelectList.add(DomainConstants.SELECT_ID);
		strBusSelectList.add(DomainConstants.SELECT_NAME);
		strBusSelectList.add(DomainConstants.SELECT_TYPE);
		strBusSelectList.add(RTAUtilConstants.SELECT_MARKETINGNAME);
		strBusSelectList.add(DomainConstants.SELECT_LEVEL);
		
		Pattern relPattern = new Pattern(ProductLineConstants.RELATIONSHIP_MAIN_PRODUCT);
		relPattern.addPattern(AWLRel.PRODUCT_LINE_MODELS.get(context));
		relPattern.addPattern(AWLRel.SUB_PRODUCT_LINES.get(context));
		relPattern.addPattern(AWLRel.ASSOCIATED_COPY_LIST.get(context));
		Pattern typePattern = new Pattern(strTypeCPGProd);
		typePattern.addPattern(AWLType.MODEL.get(context));
		typePattern.addPattern(RTAUtilConstants.TYPE_PRODUCTTYPE);
		
		DomainObject doCopyList = DomainObject.newInstance(context, strCLId);
		
		MapList mlCLParentInfo = doCopyList.getRelatedObjects(context, 
										relPattern.getPattern(),		//Rel Pattern
										typePattern.getPattern(), 		//Type Pattern
										strBusSelectList,				//Bus select
										null,							//Rel Select
										true,							//From
										false,							//To
										(short) 4,						//Level
										null,							//Bus where
										null, 0);						//Rel where
		
		String strOU = null;
		String strOUType = null;
		String strRegion = null;
		String strLevel = null;
		String strType = null;
		String strName = null;
		Map mProdInfo = null;
		mlCLParentInfo.sort(DomainConstants.SELECT_LEVEL, PGWidgetConstants.ASCENDING, "integer");
		Iterator itr = mlCLParentInfo.iterator();
		while(itr.hasNext()) {
			mProdInfo = (Map)itr.next();
			strType = BusinessUtil.getString(mProdInfo, DomainConstants.SELECT_TYPE);
			strLevel = BusinessUtil.getString(mProdInfo, DomainConstants.SELECT_LEVEL);
			if("1".equals(strLevel) && strTypeCPGProd.equals(strType)) {
				strOUType = BusinessUtil.getString(mProdInfo, RTAUtilConstants.SELECT_MARKETINGNAME);
			}
			if(strOUType != null && "3".equals(strLevel) && RTAUtilConstants.TYPE_PRODUCTTYPE.equals(strType)) {
				strOU = BusinessUtil.getString(mProdInfo, RTAUtilConstants.SELECT_MARKETINGNAME);
			}
			if(strOUType != null && strOU != null && "4".equals(strLevel) && RTAUtilConstants.TYPE_PRODUCTTYPE.equals(strType)) {
				strRegion = BusinessUtil.getString(mProdInfo, RTAUtilConstants.SELECT_MARKETINGNAME);
				break;
			}			
		}
		if(strOUType != null && strOU != null && strRegion != null) {
			mCLInfo.put(EditCLConstants.KEY_OU, new StringList(strOU));
			mCLInfo.put(EditCLConstants.KEY_OUTYPE, new StringList(strOUType));
			mCLInfo.put(EditCLConstants.KEY_REGIONS, new StringList(strRegion));
		}
	}

}
