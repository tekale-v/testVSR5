package com.pg.widgets.nexusPerformanceChars;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import matrix.util.*;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import com.dassault_systemes.derived_object.messaging.IHasConvertActions.Comparator;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.domain.util.mxType;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloConstants;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.AccessConstants;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Page;
import matrix.db.BusinessObject;

import com.pg.v3.custom.pgV3Constants;

public class PGPerfCharsExportProgramsUtil {
	
	private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PGPerfCharsExportProgramsUtil.class.getName());

	/**
	 * DSO 2013x.4 - This method displays the range values for "pgTMLogic" attribute
	 * on Performance Characteristic object.
	 * 
	 * @param context
	 * @param args    return StringList
	 */

	public StringList getTMLogicRanges(Context context, String[] args) {
		StringList slTMLogicRanges = new StringList();
		try {
			String strTMRangeValues = i18nNow.getI18nString("emxCPN.TMLogic.Ranges", "emxCPNStringResource",
					context.getSession().getLanguage());
			if (UIUtil.isNotNullAndNotEmpty(strTMRangeValues)) {
				slTMLogicRanges = FrameworkUtil.split(strTMRangeValues, PGPerfCharsConstants.CONSTANT_STRING_COMMA);
				slTMLogicRanges.add(DomainConstants.EMPTY_STRING);
			}
		} catch (Exception e) {
			
		}
		return slTMLogicRanges;
	}

	// Method used as Range Function to load populate picklist on for Web Forms
	public Object getPicklistRangeMapForDirectAttr(Context context, String[] args) throws Exception {
		HashMap myMap = (HashMap) JPO.unpackArgs(args);
		HashMap fieldmap = (HashMap) myMap.get("fieldMap");
		if (fieldmap == null) {
			fieldmap = (HashMap) myMap.get("columnMap");
		}

		Map settings = (Map) fieldmap.get("settings");
		String sExpressionBO = "";
		String sAttrName = "";
		String sPLState = "";
		String sPLLocation = (String) settings.get("pgPicklistLocation");
		String sPLName = (String) settings.get("pgPicklistName");
		StringList rangeList = new StringList();
		Map mpReturnMap = new HashMap();

		if (settings.get("pgPicklistState") != null) {
			sPLState = (String) settings.get("pgPicklistState");
		}
		if (sPLName != null && !"".equals(sPLName)) {
			mpReturnMap = getPicklistValuesMap(context, sPLName, sPLState);
		} else {
			sExpressionBO = (String) fieldmap.get("expression_businessobject");
			if (sExpressionBO != null && !"".equals(sExpressionBO)) {
				sAttrName = sExpressionBO.substring(sExpressionBO.indexOf("[") + 1, sExpressionBO.indexOf("]"));

			}
			if (sAttrName.indexOf("attribute_") != -1) {
				sAttrName = sAttrName.replace("attribute_", "");
			}
			mpReturnMap = getPicklistValuesMap(context, sAttrName, sPLState);
		}
		if (myMap.containsKey("requestMap")) {

			Map mRequestMap = (Map) myMap.get("requestMap");
			String strParentOID = (String) mRequestMap.get("parentOID");
			if (UIUtil.isNotNullAndNotEmpty(strParentOID)) {
				DomainObject dObjStudyProtocol = DomainObject.newInstance(context, strParentOID);
				String strParentType = (String) dObjStudyProtocol.getInfo(context, DomainConstants.SELECT_TYPE);
				if (PGPerfCharsConstants.TYPE_PGPKGSTUDYPROTOCOL.equals(strParentType)) {
					StringList slChoicesStudyProto = new StringList(4);
					slChoicesStudyProto.add(DomainConstants.EMPTY_STRING);
					slChoicesStudyProto.add(PGPerfCharsConstants.STR_A5_TEAM_TEST);
					slChoicesStudyProto.add(PGPerfCharsConstants.STR_A6_INVESTIGATIONAL_TEST);
					slChoicesStudyProto.add(PGPerfCharsConstants.STR_A7a1_INVESTIGATIONAL_SALES);
					slChoicesStudyProto.add(PGPerfCharsConstants.STR_A7a2_INVESTIGATIONAL_SALES_SAMPLES);
					mpReturnMap = new HashMap();
					mpReturnMap.put("field_display_choices", slChoicesStudyProto);
					mpReturnMap.put("field_choices", slChoicesStudyProto);
				} else if (PGPerfCharsConstants.TYPE_CONSUMER_RESEARCH.equals(strParentType)) {
					StringList slChoicesConsumerResearch = new StringList(1);
					slChoicesConsumerResearch.add(PGPerfCharsConstants.STR_A2_GPS_APPROVAL);
					mpReturnMap = new HashMap();
					mpReturnMap.put("field_display_choices", slChoicesConsumerResearch);
					mpReturnMap.put("field_choices", slChoicesConsumerResearch);
				}
			}
		}
		return mpReturnMap;
	}

	public Map getPicklistValuesMap(Context context, String picklistname, String pickliststate) throws Exception {
		StringTokenizer strTok = null;
		StringTokenizer strTokItem = null;
		String sPicklistItemName = "";
		StringList picklistItems = new StringList();
		String sNext = "";
		String mqlString = "";
		String strResult = "";
		Map mpReturnMap = new HashMap();
		StringList slPicklistID = new StringList();
		if ("".equals(pickliststate) || "Active".equalsIgnoreCase(pickliststate)
				|| "Exists".equalsIgnoreCase(pickliststate)) {
			mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS + picklistname
					+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_ACTIVE_EXISTS;
		}
		if ("All".equalsIgnoreCase(pickliststate)) {
			mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS + picklistname + PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_ALL;
		}
		if ("Inactive".equalsIgnoreCase(pickliststate)) {
			mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS + picklistname
					+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_INACTIVE;
		}
		try {
			strResult = MqlUtil.mqlCommand(context, mqlString.toString()).trim();
		} catch (Exception e) {
			
		}
		strTok = new StringTokenizer(strResult, "\n");
		if (UIUtil.isNotNullAndNotEmpty(strResult) && !"pgPLIUnitofMeasureWD".equals(picklistname)) {
			picklistItems.add(DomainConstants.EMPTY_STRING);
		}
		while (strTok.hasMoreTokens()) {
			sPicklistItemName = "";
			sNext = strTok.nextToken();
			strTokItem = new StringTokenizer(sNext, "|");
			strTokItem.nextToken();
			sPicklistItemName = strTokItem.nextToken();
			picklistItems.add(sPicklistItemName);
		}
		picklistItems.sort();
		mpReturnMap.put("field_display_choices", picklistItems);
		mpReturnMap.put("field_choices", picklistItems);
		return mpReturnMap;
	}

	public Object getPicklistRangeMap(Context context, String[] args) throws Exception {
		HashMap myMap = (HashMap) JPO.unpackArgs(args);
		HashMap fieldmap = (HashMap) myMap.get("fieldMap");
		if (fieldmap == null) {
			fieldmap = (HashMap) myMap.get("columnMap");
		}
		Map settings = (Map) fieldmap.get("settings");
		String sExpressionBO = "";
		String sAttrName = "";
		String sPLState = "";
		String sPLLocation = (String) settings.get("pgPicklistLocation");
		String sPLName = (String) settings.get("pgPicklistName");
		String strInputType = (String) settings.get("Input Type");
		StringList rangeList = new StringList();
		Map mpReturnMap = new HashMap();

		if (settings.get("pgPicklistState") != null) {
			sPLState = (String) settings.get("pgPicklistState");

		}
		if (sPLName != null && !"".equals(sPLName)) {
			mpReturnMap = getPicklistValuesMap(context, sPLName, sPLState, strInputType);

		} else {

			sExpressionBO = (String) fieldmap.get("expression_businessobject");
			if (sExpressionBO != null && !"".equals(sExpressionBO)) {
				sAttrName = sExpressionBO.substring(sExpressionBO.indexOf("[") + 1, sExpressionBO.indexOf("]"));

			}
			if (sAttrName.indexOf("attribute_") != -1) {
				sAttrName = sAttrName.replace("attribute_", "");

			}
			mpReturnMap = getPicklistValuesMap(context, sAttrName, sPLState, strInputType);

		}
		return mpReturnMap;
	}

	/**
	 * This method is used to return correct mapping for the passed picklist subtype
	 * from the respective picklist configuration object.
	 * 
	 * @Argument context: context of the logged in user
	 * @Argument strConfigObjectName: String Picklist Subtype
	 * @returns String
	 * @throws Exception if the operation fails
	 */
	public static String getPicklistTypeMappingNumber(Context context, String strConfigObjectName) throws Exception {
		String strMappingNumber = DomainConstants.EMPTY_STRING;
		StringList objectSelects = new StringList(1);
		objectSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE_MAPPING_NUMBER);
		MapList mlPLConf = DomainObject.findObjects(context, // context
				PGPerfCharsConstants.TYPE_PG_PL_CONFIGURATION, // type
				strConfigObjectName, // name
				PGPerfCharsConstants.STRING_ZERO, // revision
				PGPerfCharsConstants.SYMB_WILD, // owner
				PGPerfCharsConstants.VAULT_ESERVICEPRODUCTION, // vault
				null, // where clause
				false, // expand
				objectSelects); // object Select
		if (mlPLConf != null && !mlPLConf.isEmpty()) {
			strMappingNumber = (String) ((Map) mlPLConf.get(0))
					.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE_MAPPING_NUMBER);
		}
		return strMappingNumber;
	}

	/**
	 * This method is used to return new object name for the passed picklist subtype
	 * and name entered by the user in an method arguments.
	 * 
	 * @Argument context: context of the logged in user
	 * @Argument strPicklistSubType: String Picklist Subtype
	 * @Argument strPicklistNameValue: String name entered by the user
	 * @returns String
	 * @throws Exception if the operation fails
	 */
	public static String getPicklistObjectNamePattern(Context context, String strPicklistSubType,
			String strPicklistNameValue) throws Exception {
		String strMappingNumber = "";
		StringBuilder sbPicklistObjectName = new StringBuilder();
		int maxNumberCharsAllowedOnName = 121;
		int intPicklistTitleLength = strPicklistNameValue.length();
		if (UIUtil.isNotNullAndNotEmpty(strPicklistSubType)
				&& !strPicklistSubType.equals(PGPerfCharsConstants.STR_BLANK)) {
			strMappingNumber = getPicklistTypeMappingNumber(context, strPicklistSubType);
			sbPicklistObjectName.append(strMappingNumber).append(PGPerfCharsConstants.SYMBOL_UNDER_SCORE);
		} else {
			sbPicklistObjectName.append(PGPerfCharsConstants.STR_PREFIX_PL).append(PGPerfCharsConstants.SYMB_WILD);
		}

		if (intPicklistTitleLength > maxNumberCharsAllowedOnName) {
			strPicklistNameValue = strPicklistNameValue.substring(0, maxNumberCharsAllowedOnName);
		}

		if (UIUtil.isNotNullAndNotEmpty(strPicklistNameValue)) {
			strPicklistNameValue = strPicklistNameValue.replaceAll("[$*';,?^\\\\\"]",
					PGPerfCharsConstants.SYMBOL_TILDE);
			sbPicklistObjectName.append(strPicklistNameValue);
		}
		return sbPicklistObjectName.toString();
	}

	public Map getPicklistValuesMap(Context context, String picklistname, String pickliststate, String strInputType)
			throws Exception

	{
		StringTokenizer strTok = null;
		StringTokenizer strTokItem = null;
		String sPicklistItemName = "";
		String sPicklistItemId = "";
		String sPicklistItemRev = "";
		String sPicklistType = "";
		StringList picklistItems = new StringList();
		StringList sortedPicklistItems = new StringList();
		String sNext = "";
		String mqlString = "";
		String strResult = "";
		HashMap<String, String> sortedMap = new HashMap<String, String>();
		Map mpReturnMap = new HashMap();
		StringList slPicklistID = new StringList();

		if (DomainConstants.EMPTY_STRING.equals(pickliststate)
				|| PGPerfCharsConstants.ACTIVE.equalsIgnoreCase(pickliststate)
				|| PGPerfCharsConstants.STR_EXISTS.equalsIgnoreCase(pickliststate)) {
			if (isPicklistItemType(context, picklistname)) {
				String sFieldName = getPicklistObjectNamePattern(context, picklistname, "*");
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS_PICKLIST + sFieldName
						+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_ACTIVE_EXISTS_PICKLIST;
			} else {
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS + picklistname
						+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_ACTIVE_EXISTS;
			}
		}
		if ("All".equalsIgnoreCase(pickliststate)) {
			if (isPicklistItemType(context, picklistname)) {
				String sFieldName = getPicklistObjectNamePattern(context, picklistname, "*");
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS_PICKLIST + sFieldName
						+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_ALL_PICKLIST;
			} else {
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS + picklistname
						+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_ALL;
			}
		}
		if (PGPerfCharsConstants.INACTIVE.equalsIgnoreCase(pickliststate)) {
			if (isPicklistItemType(context, picklistname)) {
				String sFieldName = getPicklistObjectNamePattern(context, picklistname, "*");
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS_PICKLIST + sFieldName
						+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_INACTIVE_PICKLIST;
			} else {
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS + picklistname
						+ PGPerfCharsConstants.WHERE_CLAUSE_CURRENT_INACTIVE;
			}
		}
		if ("pgPLIProductForm".equals(picklistname)) {
			if (isPicklistItemType(context, picklistname)) {
				String sFieldName = getPicklistObjectNamePattern(context, picklistname, "*");
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS_PICKLIST + sFieldName
						+ PGPerfCharsConstants.WHERE_CLAUSE_ATTR_PROD_TYPE;
			} else {
				mqlString = PGPerfCharsConstants.STR_MQL_PREFIX_TEMP_QUERY_BUS+ "'" + picklistname
						+ PGPerfCharsConstants.WHERE_CLAUSE_ATTR_PROD_TYPE_PICKLIST;
			}
		}
		try {
			strResult = MqlUtil.mqlCommand(context, mqlString.toString()).trim();
		} catch (Exception e) {
			
		}
		strTok = new StringTokenizer(strResult, "\n");
		if (UIUtil.isNotNullAndNotEmpty(strResult)
				&& !PGPerfCharsConstants.OBJ_NAME_PG_UNIT_OF_MEASURE_WD.equals(picklistname)) {
			picklistItems.add(DomainConstants.EMPTY_STRING);
			slPicklistID.add(DomainConstants.EMPTY_STRING);
			if (UIUtil.isNotNullAndNotEmpty(strInputType)
					&& PGPerfCharsConstants.INPUT_TYPE_LISTBOX.equals(strInputType)) {
				picklistItems.add(PGPerfCharsConstants.RANGE_BLANK);
				slPicklistID.add(PGPerfCharsConstants.RANGE_VALUE_BLANK);
			}
		}
		while (strTok.hasMoreTokens()) {
			sPicklistItemName = "";
			sPicklistItemId = "";
			sNext = strTok.nextToken();
			strTokItem = new StringTokenizer(sNext, PGPerfCharsConstants.SYM_PIPE_SEPERATOR);
			int iNumberOfTokens = strTokItem.countTokens();
			sPicklistType = strTokItem.nextToken();
			sPicklistItemName = strTokItem.nextToken();
			if (iNumberOfTokens == 3) {
				sPicklistItemId = strTokItem.nextToken();
			} else if (iNumberOfTokens == 4) {
				String sRevision = strTokItem.nextToken();
				sPicklistItemId = strTokItem.nextToken();
			}
			if (UIUtil.isNotNullAndNotEmpty(picklistname) && UIUtil.isNotNullAndNotEmpty(sPicklistItemId)
					&& PGPerfCharsConstants.TYPE_PG_GLOBALFORM.equalsIgnoreCase(picklistname)) {
				DomainObject doObj = DomainObject.newInstance(context, sPicklistItemId);
				String strMarketingName = doObj.getInfo(context,
						"attribute[" + PGPerfCharsConstants.ATTRIBUTE_MARKETING_NAME + "]");
				if (UIUtil.isNotNullAndNotEmpty(strMarketingName)) {
					sortedMap.put(sPicklistItemId, strMarketingName);
				} else {
					sortedMap.put(sPicklistItemId, "");
				}
			} else {
				sortedMap.put(sPicklistItemId, sPicklistItemName);
			}
		}

		// Map<Integer, String> sortMap = sortByValues(sortedMap);
		Set sortSet = sortedMap.entrySet();
		Iterator sortIterator = sortSet.iterator();
		while (sortIterator.hasNext()) {
			Map.Entry sortEntry = (Map.Entry) sortIterator.next();
			slPicklistID.add(sortEntry.getKey().toString());
			picklistItems.add(sortEntry.getValue().toString());

		}
		mpReturnMap.put(PGPerfCharsConstants.STR_FIELD_DISPLAY_CHOICES, picklistItems);
		mpReturnMap.put(PGPerfCharsConstants.STR_FIELD_CHOICES, slPicklistID);
		return mpReturnMap;
	}

	@SuppressWarnings("unchecked")
	private static HashMap sortByValues(Map map) {
		@SuppressWarnings("rawtypes")
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {
			public int compare(Map.Entry o1, Map.Entry o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	/**
	 * This method is used to return boolean value as true if the respective
	 * PicklistType is considered in Retained list.
	 * 
	 * @Argument context: Context of the logged in User
	 * @Argument strPicklistType: Relationship name
	 * @returns boolean
	 * @throws Exception if the operation fails
	 */
	public static boolean isDerivedFromPicklistTypes(Context context, String strPicklistType) throws Exception {
		boolean isRetainedPicklistType = false;
		if (mxType.isOfParentType(context, strPicklistType, PGPerfCharsConstants.TYPE_PGPICKLISTITEM)
				|| mxType.isOfParentType(context, strPicklistType, PGPerfCharsConstants.TYPE_PG_PLI_CHASSIS)
				|| mxType.isOfParentType(context, strPicklistType, PGPerfCharsConstants.TYPE_PG_PLI_PLATFORM)
				|| mxType.isOfParentType(context, strPicklistType, PGPerfCharsConstants.TYPE_PG_PPM_PHRASE)) {
			isRetainedPicklistType = true;
		}
		return isRetainedPicklistType;
	}

	/**
	 * This method is used to Check the Type is Picklist SubType or Not. If it is
	 * subtype returning true otherwise false.
	 * 
	 * @Argument context: Context of the logged in User
	 * @Argument strType: Picklist subtype name
	 * @returns boolean
	 * @throws Exception if the operation fails
	 */
	public static boolean isPicklistItemType(Context context, String strType) throws Exception {
		boolean isPicklistType = false;
		String strOOTBTypes = getPicklistSchemaProperty(context, "STR_PL_OOTB_TYPES");
		StringList slPicklistOOTBTypes = StringUtil.split(strOOTBTypes, PGPerfCharsConstants.CONSTANT_STRING_COMMA);
		boolean isHavingPLConfigObject = isPicklistConfigExists(context, strType);
		if ((slPicklistOOTBTypes != null && UIUtil.isNotNullAndNotEmpty(strType)
				&& !slPicklistOOTBTypes.contains(strType) && isHavingPLConfigObject)
				&& !isDerivedFromPicklistTypes(context, strType)) {
			isPicklistType = true;
		}
		return isPicklistType;
	}

	/**
	 * This method is used to get schema name of a picklist type (Old/New) from page
	 * object.
	 * 
	 * @Argument context: context of the logged in user
	 * @Argument property: Picklist type property alias name
	 * @returns String
	 * @throws Exception if the operation fails
	 */
	public static String getPicklistSchemaProperty(Context context, String property) throws Exception {
		String strSubType = null;
		Page plPage = new Page(PGPerfCharsConstants.PICKLIST_SUB_TYPE_PAGE_OBJECT);
		plPage.open(context);
		String strPageContext = plPage.getContents(context);
		if (UIUtil.isNotNullAndNotEmpty(strPageContext)) {
			Properties properties = new Properties();
			properties.load(new StringReader(strPageContext));
			strSubType = properties.getProperty(property);
		}
		return strSubType;
	}

	/**
	 * This method is used to Check the Type is Picklist SubType or Not. If it is
	 * subtype returning true otherwise false.
	 * 
	 * @Argument context: Context of the logged in User
	 * @Argument strName: Picklist subtype name
	 * @returns boolean
	 * @throws Exception if the operation fails
	 */
	public static boolean isPicklistConfigExists(Context context, String strName) throws Exception {
		boolean isPLType = false;
		StringList objectSelects = new StringList(1);
		objectSelects.add(DomainConstants.SELECT_ID);
		MapList mlPLConf = DomainObject.findObjects(context, // context
				PGPerfCharsConstants.TYPE_PG_PL_CONFIGURATION, // type
				strName, // name
				PGPerfCharsConstants.STRING_ZERO, // revision
				PGPerfCharsConstants.SYMB_WILD, // owner
				PGPerfCharsConstants.VAULT_ESERVICEPRODUCTION, // vault
				null, // where clause
				false, // expand
				objectSelects); // object Select

		if (mlPLConf != null && !mlPLConf.isEmpty()) {
			isPLType = true;
		}
		return isPLType;
	}

	// FetchData Column Type --Program

	public static Vector pgGetTestMethods(Context context, String[] args) throws Exception {

		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String isPGExport = (String) programMap.get("isPGExport");
		MapList mlObjectList = (MapList) programMap.get("objectList");
		Map mapParamList = (Map) programMap.get("paramList");
		String strParentOID = (String) mapParamList.get("parentOID");

		if (UIUtil.isNullOrEmpty(strParentOID)) {
			strParentOID = (String) mapParamList.get("objectId");
		}

		String strParentCurrent = DomainConstants.EMPTY_STRING;
		boolean isReleased = false;
		if (UIUtil.isNotNullAndNotEmpty(strParentOID)) {
			strParentCurrent = DomainObject.newInstance(context, strParentOID).getInfo(context,
					DomainConstants.SELECT_CURRENT);
			if (PGPerfCharsConstants.STATE_RELEASE.equalsIgnoreCase(strParentCurrent)) {
				isReleased = true;
			}
		}
		String strReportFormat = (String) mapParamList.get("reportFormat");

		DomainObject doObj = DomainObject.newInstance(context);
		Vector slReturnVal = new Vector(1);
		Iterator objListItr = mlObjectList.iterator();
		boolean isLayeredProductPart = false;
		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String charObjId = (String) perMap.get(DomainConstants.SELECT_ID);
			String objReadAccess = (String) perMap.get("objReadAccess");
			StringBuilder pgTMCellValue = new StringBuilder(250);
			if (UIUtil.isNotNullAndNotEmpty(objReadAccess)
					&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess)) {

				StringList objectSelects = new StringList(4);
				objectSelects.add(DomainConstants.SELECT_ID);
				objectSelects.add(DomainObject.SELECT_NAME);
				objectSelects.add(DomainObject.SELECT_TYPE);
				objectSelects.add("last.id");

				doObj.setId(charObjId);

				MapList objList = null;
				StringList relSelects = new StringList(1);
				String sRelRefDoc = DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT;
				String sPropertiesRel = "Properties Testing Requirements";
				objList = doObj.getRelatedObjects(context, sRelRefDoc + "," + sPropertiesRel,
						PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION, objectSelects, relSelects, true, true,
						(short) 1, null, null, 0);
				StringList sortedName = new StringList();
				StringList slTemp = new StringList();
				String strTMId = DomainConstants.EMPTY_STRING;
				HashMap nameIDMap = new HashMap();
				isLayeredProductPart = false;
				if (perMap.containsKey("isLayeredProductPart")) {
					isLayeredProductPart = (boolean) perMap.get("isLayeredProductPart");
					if (isLayeredProductPart) {
						objList = doObj.getRelatedObjects(context,
								pgApolloConstants.RELATIONSHIP_CHARACTERISTIC_TEST_METHOD, // relationship pattern
								PGPerfCharsConstants.TYPE_TEST_METHOD_SPECIFICATION, // type pattern
								objectSelects, // object selects
								relSelects, // relationship selects
								false, // to direction
								true, // from direction
								(short) 1, // recursion level
								null, // object where clause
								null, // relationship where clause
								0);// object limits
					}
				}
				for (int i = 0; i < objList.size(); i++) {
					Hashtable testMehtodsObjs = (Hashtable) objList.get(i);
					String pgTMName = (String) testMehtodsObjs.get("name");
					strTMId = (String) testMehtodsObjs.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strTMId) && !isReleased) {
						nameIDMap.put(pgTMName, strTMId);
					}
					if (!slTemp.contains(pgTMName)) {
						slTemp.addElement(pgTMName);
					}
					if (isLayeredProductPart) {
						nameIDMap.put(pgTMName, strTMId);
					}
				}
				if (isLayeredProductPart) {
					slTemp.sort();
					nameIDMap.put("sortedNames", slTemp);
				} else {
					if (isReleased) {
						nameIDMap = getHigherRevisionTMObj(context, slTemp, charObjId);
					} else {
						slTemp.sort();
						nameIDMap.put("sortedNames", slTemp);
					}
				}
				StringList slTMNames = (StringList) nameIDMap.get("sortedNames");
				int tmCntr = 0;

				DomainObject domTM = null;

				for (int i = 0; i < slTMNames.size(); i++) {
					String pgTMName = (String) slTMNames.get(i);
					if (UIUtil.isNotNullAndNotEmpty(pgTMName)) {
						if ("true".equalsIgnoreCase(isPGExport)) {

							pgTMCellValue.append(pgTMName);
							if (i != objList.size() - 1) {
								pgTMCellValue.append("|");
							}

						} else {
							tmCntr++;
							String pgTMId = (String) nameIDMap.get(pgTMName);
							domTM = DomainObject.newInstance(context, pgTMId);
							String hrefLink = "";
							String hrefSrt = "";
							String hrefEnd = "";
							if (FrameworkUtil.hasAccess(context, domTM, "read") != false) {
								hrefLink = "javascript:showNonModalDialog('../common/emxTree.jsp?objectId=" + pgTMId
										+ "')";
								hrefSrt = "<a href=\"" + hrefLink + "\">";
								hrefEnd = "</a>";
							}
							if (tmCntr == 1) {
								if (strReportFormat != null && strReportFormat.length() > 0) {
									pgTMCellValue.append(pgTMName);
								} else {
									pgTMCellValue.append(hrefSrt).append(pgTMName).append(hrefEnd);
								}
							} else {
								if (strReportFormat != null && strReportFormat.length() > 0) {
									pgTMCellValue.append("\n").append(pgTMName);
								} else {
									pgTMCellValue.append("|").append("<br/>").append(hrefSrt).append(pgTMName)
											.append(hrefEnd);
								}
							}
						}
					}
				}
			}
			slReturnVal.addElement(pgTMCellValue.toString());
		}
		if (slReturnVal.size() == 0)
			slReturnVal.add("");
		return slReturnVal;
	}

	/**
	 * DSO 2013x.5 - ALM : 4033 : Test method appears multiple times for a
	 * performance characteristc : Start
	 * 
	 * @param context
	 * @param StringList
	 * @param String
	 * @return HashMap
	 * @throws Exception
	 * @since DSO
	 */
	public static HashMap getHigherRevisionTMObj(Context context, StringList slTMNames, String charObjId)
			throws Exception {
		HashMap returnMap = new HashMap();
		int higherRev = 0;
		int tempRev = 0;
		String hrefLink = DomainConstants.EMPTY_STRING;
		String hrefSrt = DomainConstants.EMPTY_STRING;
		String hrefEnd = DomainConstants.EMPTY_STRING;
		String pgTMName = DomainConstants.EMPTY_STRING;
		String strTempTMRev = DomainConstants.EMPTY_STRING;

		String strLastRevCurrent = DomainConstants.EMPTY_STRING;
		String strLastReleasedID = DomainConstants.EMPTY_STRING;
		DomainObject doLastRev = DomainObject.newInstance(context);

		DomainObject dob;
		for (Object obj : slTMNames) {
			String strTMId = DomainConstants.EMPTY_STRING;
			String strLastTMId = DomainConstants.EMPTY_STRING;
			StringList slTMIds = new StringList();
			String strCommand = PGPerfCharsConstants.STR_MQL_COL_DATA_PRINT_OBJ;
			String strResult = MqlUtil.mqlCommand(context, strCommand, charObjId,
					"from[Reference Document|to.name=='" + obj.toString() + "'].to.id", "|");
			if (UIUtil.isNullOrEmpty(strResult)) {
				strResult = MqlUtil.mqlCommand(context, strCommand, charObjId,
						"to[Reference Document|from.name=='" + obj.toString() + "'].from.id", "|");
			}

			if (UIUtil.isNotNullAndNotEmpty(strResult)) {
				slTMIds = FrameworkUtil.split(strResult, "|");
				strTMId = (String) slTMIds.get(0);
				if (UIUtil.isNotNullAndNotEmpty(strTMId)) {
					dob = DomainObject.newInstance(context, strTMId);
					BusinessObject revisionObj = dob.getLastRevision(context);
					strLastTMId = (String) revisionObj.getObjectId(context);
					if (UIUtil.isNotNullAndNotEmpty(strLastTMId)) {
						doLastRev.setId(strLastTMId);
						strLastRevCurrent = doLastRev.getInfo(context, DomainConstants.SELECT_CURRENT);
						if (!PGPerfCharsConstants.STATE_RELEASE.equalsIgnoreCase(strLastRevCurrent)) {
							strLastReleasedID = doLastRev.getPreviousRevision(context).getObjectId();
							if (UIUtil.isNotNullAndNotEmpty(strLastReleasedID)) {
								strLastTMId = strLastReleasedID;
							}
						}
					}
				}
			}
			returnMap.put(obj.toString(), strLastTMId);
		}
		returnMap.put("sortedNames", slTMNames);
		return returnMap;
	}

	/**
	 * Gets the reference document names from relationship Properties testing
	 * requirements
	 * @param context
	 * @param StringList
	 * @param String
	 * @return HashMap
	 * @throws Exception 
	 */

	public static Vector pgGetReferenceDocGCAS(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		String isPGExport = (String) programMap.get("isPGExport");
		Vector slReturnVal = new Vector();
		MapList mlObjectList = (MapList) programMap.get("objectList");
		Map mapParamList = (Map) programMap.get("paramList");
		MapList objList = null;
		Iterator objListItr = mlObjectList.iterator();
		String strReportFormat = (String) mapParamList.get("reportFormat");
		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String objReadAccess = (String) perMap.get("objReadAccess");
			StringBuffer slreturn = new StringBuffer();
			StringBuffer objTMRDTypes = new StringBuffer();
			if (UIUtil.isNotNullAndNotEmpty(objReadAccess)
					&& PGPerfCharsConstants.RANGE_VALUE_TRUE.equals(objReadAccess)) {
				String charObjId = (String) perMap.get(DomainConstants.SELECT_ID);
				String charObjType = (String) perMap.get(DomainConstants.SELECT_TYPE);
				DomainObject doObj = DomainObject.newInstance(context, charObjId);
				if (UIUtil.isNullOrEmpty(charObjType))
					charObjType = doObj.getInfo(context, DomainConstants.SELECT_TYPE);
				StringList objectSelect = new StringList(2);
				objectSelect.add(DomainConstants.SELECT_ID);
				objectSelect.add("last.id");
				objectSelect.add(DomainConstants.SELECT_NAME);
				objectSelect.add(DomainConstants.SELECT_TYPE);
				objectSelect.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_PG_CSS_TYPE);
				String sRelRefDoc = PropertyUtil.getSchemaProperty("relationship_ReferenceDocument");
				String sRelPropTestReqr = PropertyUtil.getSchemaProperty(context,
						"relationship_PropertiesTestingRequirements");
				String relType = sRelRefDoc + "," + sRelPropTestReqr;
				if (UIUtil.isNotNullAndNotEmpty(charObjType)
						&& charObjType.equalsIgnoreCase(PGPerfCharsConstants.TYPE_PG_STABILITY_RESULTS))
					objTMRDTypes.append(PGPerfCharsConstants.TYPE_PG_IRM_DOC_TYPES);
				else
					objTMRDTypes.append(PGPerfCharsConstants.TYPE_PG_TMRD_TYPES).append(",")
							.append(pgV3Constants.TYPE_PGTESTMETHOD).append(",")
							.append(pgV3Constants.TYPE_PGSTACKINGPATTERN);
				objList = doObj.getRelatedObjects(context, relType, objTMRDTypes.toString(), objectSelect, null, true,
						true, (short) 1, null, null, 0);
				Iterator objIter = objList.iterator();
				Map objMap = null;
				HashMap nameIDMap = new HashMap();
				StringList sortedNames = new StringList();
				DomainObject domObj = null;
				String strParentId = (String) mapParamList.get("parentOID");
				String objOriginatingSource = null;
				if (null != strParentId) {
					DomainObject parentObj = DomainObject.newInstance(context, strParentId);
					objOriginatingSource = (String) parentObj.getInfo(context,
							"attribute[" + PGPerfCharsConstants.ATTR_PG_ORIGINATINGSOURCE + "]");
				}
				while (objIter.hasNext()) {
					objMap = (Map) objIter.next();
					String objType = (String) objMap.get(DomainConstants.SELECT_TYPE);
					String objPGCssType = (String) objMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
					if (PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equals(objOriginatingSource)) {
						if (!objType.equals("pgTestMethod")) {
							String objId = (String) objMap.get("id");
							String objName = (String) objMap.get(DomainConstants.SELECT_NAME);
							if (!sortedNames.contains(objName)) {
								sortedNames.add(objName);
								nameIDMap.put(objName, objId);
							}
						}
					} else {
						if (objType.equals("pgTestMethod") && (!objPGCssType.equals("TAMU")))
							continue;
						String objId = (String) objMap.get(DomainConstants.SELECT_ID);
						String objName = (String) objMap.get(DomainConstants.SELECT_NAME);
						sortedNames.add(objName);
						nameIDMap.put(objName, objId);
					}
				}
				sortedNames.sort();
				int iNameSize = sortedNames.size();
				int iLoopCount = 1;
				String strPipe = "|";

				for (Iterator namesItr = sortedNames.iterator(); namesItr.hasNext();) {
					String objName = (String) namesItr.next();
					String objId = (String) nameIDMap.get(objName);
					if (UIUtil.isNotNullAndNotEmpty(isPGExport) && "true".equalsIgnoreCase(isPGExport)) {
						slreturn.append(objName);
						if (namesItr.hasNext()) {
							slreturn.append("|");
						}
					} else {
						if (strReportFormat != null && strReportFormat.length() > 0) {
							if (iLoopCount == iNameSize)

							{
								slreturn.append(objName).append("\n");
							} else {
								slreturn.append(objName).append(strPipe).append("\n");
							}
						} else {
							domObj = DomainObject.newInstance(context, objId);
							boolean hasReadAccess = domObj.checkAccess(context, (short) AccessConstants.cRead);
							String hrefSrt = "";
							if (hasReadAccess) {
								hrefSrt = "javascript:showNonModalDialog('../common/emxTree.jsp?objectId=" + objId
										+ "')";
							}
							if (iLoopCount == iNameSize) {
								if (UIUtil.isNotNullAndNotEmpty(hrefSrt)) {
									slreturn.append("<a href=\"" + hrefSrt + "\">").append(objName).append("</a>")
											.append("<br/>");
								} else {
									slreturn.append(objName).append("<br/>");
								}

							} else

							{
								if (UIUtil.isNotNullAndNotEmpty(hrefSrt)) {
									slreturn.append("<a href=\"" + hrefSrt + "\">").append(objName).append("</a>")
											.append(strPipe).append("<br/>");
								} else {
									slreturn.append(objName).append(strPipe).append("<br/>");
								}
							}

						}
					}

					iLoopCount++;

				}

			}
			slReturnVal.addElement((slreturn.toString()).trim());
		}
		return slReturnVal;
	}
	
	/**
	 * Gets the performance characteristics : Pg Nexus Parameter List ID from PG Nexus Parameter Object
	 *
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException 
	 * @throws Exception
	 */
	public static StringList getNexusParametrListIDForRow(Context context, Map<?, ?> programMap) throws MatrixException {
		MapList mlObjectList = (MapList) programMap.get("objectList");
		StringList slReturnVal = new StringList();
		Iterator objListItr = mlObjectList.iterator();
		String strNexusParPCId =DomainConstants.EMPTY_STRING;
		String strNexusParListId =DomainConstants.EMPTY_STRING;
		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			String pcObjId = (String) perMap.get(DomainConstants.SELECT_ID);
			DomainObject doObj = DomainObject.newInstance(context, pcObjId);

			if(UIUtil.isNotNullAndNotEmpty(pcObjId)) {
				//DSM (DS) 2022x : Push context required to get Performance char Id list as context user doesn't have access to perform this action
				ContextUtil.pushContext(context);
				strNexusParPCId = doObj.getInfo(context, PGPerfCharsConstants.SELECT_ATTR_PG_NEXUS_PARAMETER_ID);
				if(UIUtil.isNotNullAndNotEmpty(strNexusParPCId) && new BusinessObject(strNexusParPCId).exists(context)) {
					DomainObject doPCObj = DomainObject.newInstance(context, strNexusParPCId);
					strNexusParListId = doPCObj.getInfo(context, PGPerfCharsConstants.SELECT_ATTR_PG_NEXUS_PARAMETER_LIST_ID);
				} else {
					strNexusParPCId =DomainConstants.EMPTY_STRING;
					strNexusParListId=DomainConstants.EMPTY_STRING;
				}
			}
			slReturnVal.add(strNexusParListId);
		}
		if (slReturnVal.isEmpty()) {
			slReturnVal.add("");
		}
		return slReturnVal;
	}
	
	
	
	/**
	 * Gets the performance characteristics : Sequence data and shows in No. column
	 *
	 * @param context
	 * @param args
	 * @return
	 * @throws MatrixException 
	 * @throws Exception
	 */

	public StringList pgGetSequenceColumnVal(Context context, String[] args) throws Exception {
		HashMap programMap = (HashMap) JPO.unpackArgs(args);
		MapList mlObjectList = (MapList) programMap.get("objectList");
		String sAttrVal = DomainConstants.EMPTY_STRING;
		String sRelAttrVal = DomainConstants.EMPTY_STRING;
		StringList slReturnVal = new StringList();
		Iterator objListItr = mlObjectList.iterator();
		boolean isContextPushed = false;
		//Context pushed as the context user might not have access to get the Char Seq
		ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"),
				DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
		isContextPushed = true;
		try {
			while (objListItr.hasNext()) {
				Map perMap = (Map) objListItr.next();
				sAttrVal = (String) perMap.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
				String sChrRelId = (String) perMap.get(DomainRelationship.SELECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(sChrRelId)) {
					DomainRelationship perfCharRel = DomainRelationship.newInstance(context, sChrRelId);
					if (perfCharRel != null) {
						sRelAttrVal = perfCharRel.getAttributeValue(context,
								PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
					}
					if (UIUtil.isNotNullAndNotEmpty(sRelAttrVal) && !sRelAttrVal.equalsIgnoreCase(sAttrVal))
						sAttrVal = sRelAttrVal;
				}
				slReturnVal.addElement(sAttrVal);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return slReturnVal;
	}

}