/*
 **   PGWidgetUtil
 *    This file contains common methods which can reused across widgets
 **
 **   Copyright (c) 1992-2021 Dassault Systemes.
 **   All Rights Reserved.
 **   This program contains proprietary and trade secret information of MatrixOne,
 **   Inc.  Copyright notice is precautionary only
 **   and does not evidence any actual or intended publication of such program
 **
 */

package com.pg.widgets.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.custom.pg.Artwork.ArtworkConstants;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.exportcontrol.ExportControlConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;
import com.pg.dsm.preference.util.DSMUPT;
import com.pg.dsm.preference.util.IRMUPT;
import com.pg.pl.custom.pgPLConstants;
import com.pg.pl.custom.pgPLUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.Access;
import matrix.db.BusinessInterface;
import matrix.db.BusinessObject;
import matrix.db.ClientTask;
import matrix.db.ClientTaskItr;
import matrix.db.ClientTaskList;
import matrix.db.Context;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.db.State;
import matrix.db.StateList;
import matrix.db.Vault;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGWidgetUtil {

	public static final String OPERATION_PROMOTE = "promote";
	public static final String OPERATION_DEMOTE = "demote";
	static final String PROGRAM_IP_SECURITY_COMMON_UTIL = "pgIPSecurityCommonUtil";
	static final String METHOD_GET_ALL_IP_CLASSES = "includeSecurityCategoryClassification";

	static final String KEY_CLASSIFICATION_VALUE = "vSelectedValue";
	static final String KEY_PGIPCLASSIFICATION = "pgIPClassification";
	static final String SELECT_HAS_CLASS_ACCESS = "current.access[fromdisconnect]";
	static final String KEY_CLASS_LIST = "ClassList";
	static final String EXCEPTION_MESSAGE = "Exception in PGWidgetUtil";

	static final String POLICY_IP_CLASS = (String) ExportControlConstants.POLICY_EXPORT_CONTROL_CLASSIFICATION;
	static final String STATE_ACTIVE = PropertyUtil.getSchemaProperty(null, DomainConstants.SELECT_POLICY, POLICY_IP_CLASS, "state_Active");
	static final String POLICY_SIGNATURE_REFERENCE_SYMBOLIC = "policy_pgPKGSignatureReferenceDoc";
	static final String FROM_ACTIVE_VERSION_TO_TITLE = "from[Active Version].to.attribute[Title]";
	static final String FROM_ACTIVE_VERSION_TO_ID = "from[Active Version].to.id";
	static final String STRING_IMAGE_DATA = "imageData";
	private static final List<String> LIST_FILE_IMAGE_FILE_FORMAT = Arrays.asList("gif", "jpg", "png", "giff", "jpeg", "jfif", "GIF", "JPG", "PNG", "GIFF", "JPEG", "JFIF");
	static final String CONSTANT_STRING_COMMA = ",";

	static final String KEY_RETURN_DATA = "returnData";
	static final String VAULT_PRODUCTION = "eService Production";

	private static final Logger logger = Logger.getLogger(PGWidgetUtil.class.getName());
	DSMUserPreferenceTemplateUtil objUserPreferenceTemplateUtil = new DSMUserPreferenceTemplateUtil();
	
	/**
	 * Constructor.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param args    holds no arguments
	 * @throws Exception if the operation fails
	 */
	public PGWidgetUtil(Context context, String[] args) throws Exception {

	}

	public PGWidgetUtil() {
		
	}

	public static JsonObjectBuilder getObjectInfo(Context context, Map<?, ?> mpParamMAP) throws MatrixException {
		String objectId = (String) mpParamMAP.get(DomainConstants.SELECT_ID);
		String objSelectablesList = mpParamMAP.get(PGWidgetConstants.KEY_OBJECT_SELECTS).toString();
		StringList slObjSelectables = new StringList();
		StringList slMultivalueSelectables = new StringList();
		String strEntry;
		StringList slMultipleValueList = null;
		StringBuilder multipleFinalValue = null;
		String strListTemp;

		StringList slSelects = StringUtil.split(objSelectablesList, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		for (String objSelectable : slSelects) {
			if (objSelectable.contains(PGWidgetConstants.STR_FROM_OPEN) || objSelectable.contains(PGWidgetConstants.STR_ATTRIBUTE_OPEN)) {
				slMultivalueSelectables.add(objSelectable);
			}
			slObjSelectables.add(objSelectable);
		}

		slObjSelectables.add(DomainConstants.SELECT_CURRENT);
		slObjSelectables.add(DomainConstants.SELECT_POLICY);
		slObjSelectables.add(DomainConstants.SELECT_STATES);
		slObjSelectables.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slObjSelectables.add(PGWidgetConstants.SELECT_PHYSICAL_ID);

		Map<?, ?> objMap = null;
		DomainObject doObject = null;
		JsonObjectBuilder jsonObject = null;
		String objPolicy = null;
		String strDisplayValue = null;
		String strKey = null;
		String strLanguage = context.getSession().getLanguage();

		if (UIUtil.isNotNullAndNotEmpty(objectId)) {
			doObject = DomainObject.newInstance(context, objectId);
			// Fix for 22x Upgrade MultiValueList Changes - Start
			objMap = doObject.getInfo(context, slObjSelectables, slMultivalueSelectables);
			// Fix for 22x Upgrade MultiValueList Changes - End

			if (null != objMap && objMap.size() > 0) {
				jsonObject = Json.createObjectBuilder();
				String strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
				objPolicy = (String) objMap.get(DomainConstants.SELECT_POLICY);
				objMap.remove(DomainConstants.SELECT_STATES);
				for (Entry<?, ?> entry : objMap.entrySet()) {
					strEntry = (String) entry.getKey();
					if (strEntry.contains(PGWidgetConstants.STR_FROM_OPEN) || strEntry.contains(PGWidgetConstants.STR_ATTRIBUTE_OPEN)) {
						multipleFinalValue = new StringBuilder();
						slMultipleValueList = returnStringListForObject((Object) entry.getValue());
						for (int i = 0; i < slMultipleValueList.size(); i++) {
							strListTemp = slMultipleValueList.get(i);
							if (multipleFinalValue.length() > 0) {
								if (strEntry.contains(PGWidgetConstants.STR_ATTRIBUTE_OPEN)) {
									multipleFinalValue.append(PGWidgetConstants.KEY_COMMA_SEPARATOR).append(strListTemp);
								} else {
									multipleFinalValue.append(PGWidgetConstants.STR_PIPE_SEPARATED_WITHSPACE).append(strListTemp);
								}
							} else {
								multipleFinalValue.append(strListTemp);
							}
						}
						jsonObject.add((String) entry.getKey(), multipleFinalValue.toString());
					} else {
						strKey = (String) entry.getKey();
						if (strKey.equals(DomainConstants.SELECT_TYPE)) {
							strDisplayValue = EnoviaResourceBundle.getTypeI18NString(context, checkNullValueforString((String) entry.getValue()), strLanguage);
							jsonObject.add((String) entry.getKey(), (String) entry.getValue());
							jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strDisplayValue);
						} else if (strKey.equals(DomainConstants.SELECT_POLICY)) {
							strDisplayValue = EnoviaResourceBundle.getAdminI18NString(context, PGWidgetConstants.KEY_POLICY,
									checkNullValueforString((String) entry.getValue()), strLanguage);
							jsonObject.add((String) entry.getKey(), strDisplayValue);
						} else {
							jsonObject.add((String) entry.getKey(), checkNullValueforString((String) entry.getValue()));
						}
					}
				}
				JsonObject jsonObj = jsonObject.build();
				for (String objSelectable : slSelects) {
					if (!jsonObj.containsKey(objSelectable)) {
						jsonObject.add(objSelectable, "");
					}
				}

				jsonObject.add(PGWidgetConstants.KEY_STATES, getObjectStates(context, objectId, objPolicy, strCurrent));
			}
		}
		return jsonObject;
	}

	/**
	 * Checks the policy of object and returns all states
	 * 
	 * @param context
	 * @param strObjectId
	 * @param sPolicy
	 * @param sCurrent
	 * @return
	 * @throws MatrixException
	 * @throws Exception
	 */
	public static JsonArray getObjectStates(Context context, String strObjectId, String sPolicy, String sCurrent) throws MatrixException {
		JsonArrayBuilder jsonStatesArray = Json.createArrayBuilder();
		DomainObject dObject = DomainObject.newInstance(context, strObjectId);
		StateList sList = dObject.getStates(context);
		Access access = dObject.getAccessMask(context);
		boolean bAccessPromote = access.hasPromoteAccess();
		boolean bAccessDemote = access.hasDemoteAccess();
		List<String> hiddenStateNames = CacheManager.getInstance().getValue(context, CacheManager._entityNames.HIDDEN_STATES, sPolicy);
		List<State> hiddenStates = new ArrayList<>();
		State state = null;
		if (!hiddenStateNames.isEmpty()) {
			for (int i = 0; i < sList.size(); i++) {
				state = sList.get(i);
				if (hiddenStateNames.contains(state.getName())) {
					hiddenStates.add(state);
				}
			}
			sList.removeAll(hiddenStates);
		}
		int iCurrent = 0;
		for (int i = 0; i < sList.size(); i++) {
			state = sList.get(i);
			String sStateName = state.getName();
			if (sStateName.equals(sCurrent)) {
				iCurrent = i;
				break;
			}
		}

		if (bAccessDemote && iCurrent > 0) {
			State statePrev = sList.get(iCurrent - 1);
			jsonStatesArray.add(getStateJsonObj(context, sPolicy, statePrev, false, PGWidgetConstants.OPERATION_DEMOTE));
		}
		State stateCurrent = sList.get(iCurrent);
		jsonStatesArray.add(getStateJsonObj(context, sPolicy, stateCurrent, true, DomainConstants.EMPTY_STRING));
		if (bAccessPromote && (iCurrent < sList.size() - 1)) {
			State stateNext = sList.get(iCurrent + 1);
			jsonStatesArray.add(getStateJsonObj(context, sPolicy, stateNext, false, PGWidgetConstants.OPERATION_PROMOTE));
		}
		return jsonStatesArray.build();
	}

	/**
	 * Method to get State Json object with actual and display values *
	 * 
	 * @param context
	 * @param sPolicy
	 * @param stateObj
	 * @param isCurrent
	 * @return
	 * @throws MatrixException
	 */
	public static JsonObject getStateJsonObj(Context context, String sPolicy, State stateObj, boolean isCurrent, String strOperation) throws MatrixException {
		String strStateActualName = stateObj.getName();
		String sLanguage = context.getSession().getLanguage();
		String strStateDisplayName = EnoviaResourceBundle.getStateI18NString(context, sPolicy, strStateActualName, sLanguage);

		JsonObjectBuilder jsonStateObj = Json.createObjectBuilder();
		jsonStateObj.add(PGWidgetConstants.KEY_VALUE, strStateDisplayName);
		jsonStateObj.add(DomainConstants.SELECT_NAME, strStateActualName);
		jsonStateObj.add(PGWidgetConstants.KEY_IS_CURRENT, isCurrent);
		jsonStateObj.add(PGWidgetConstants.KEY_OPERATION, strOperation);
		return jsonStateObj.build();
	}

	/**
	 * Check value is null or Empty and if MultiValue character(\u0007) present then
	 * replace with comma
	 * 
	 * @param strString
	 * @return
	 */
	public static String checkNullValueforString(String strString) {
		if (strString != null && strString.contains(PGWidgetConstants.MULIT_VAL_CHAR)) {
			strString = strString.replace(PGWidgetConstants.MULIT_VAL_CHAR, " , ");
		}
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}

	/**
	 * This method is used to return StringList for selected Object
	 * 
	 * @param Object
	 * @return StringList
	 */
	public static StringList returnStringListForObject(Object obj) {
		StringList sl = new StringList();

		if (obj != null) {
			if (obj instanceof StringList) {
				sl = (StringList) obj;
			} else {
				sl.add((String) obj);
			}
		}
		return sl;
	}

	public static JsonArrayBuilder getRelatedObjectsJsonArray(Context context, JsonObject jsonInputInfo) throws FrameworkException {

		MapList mlObjectList = getRelatedObjectsMapList(context, jsonInputInfo);
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		if (!mlObjectList.isEmpty()) {
			Map<?, ?> objMap = null;
			for (int i = 0; i < mlObjectList.size(); i++) {
				objMap = (Map<?, ?>) mlObjectList.get(i);
				jsonArr.add(getJSONFromMap(context, objMap));
			}
		}
		return jsonArr;
	}

	/**
	 * Method to get related objects based on selectables
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static MapList getRelatedObjectsMapList(Context context, JsonObject jsonInputInfo) throws FrameworkException {
		String strObjectId = jsonInputInfo.getString(PGWidgetConstants.KEY_OBJECT_ID);
		String strRelPattern = jsonInputInfo.getString(PGWidgetConstants.KEY_RELPATTERN);
		String strSelectedTypes = jsonInputInfo.getString(PGWidgetConstants.TYPE_PATTERN);
		String strExpandLevel = jsonInputInfo.getString(PGWidgetConstants.KEY_EXPANDLEVEL);
		String strWhereCondition = jsonInputInfo.getString(PGWidgetConstants.KEY_WHERECONDITION);
		String strGetTo = jsonInputInfo.getString(PGWidgetConstants.KEY_GETTO);
		String strGetFrom = jsonInputInfo.getString(PGWidgetConstants.KEY_GETFROM);
		String strLimit = jsonInputInfo.getString(PGWidgetConstants.KEY_LIMIT);
		String strRelWhereCondition = jsonInputInfo.getString(PGWidgetConstants.KEY_RELWHERECONDITION);
		String strRelationshipSelects = jsonInputInfo.getString(PGWidgetConstants.KEY_RELATIONSHIPSELECTS);
		String strSelectables = jsonInputInfo.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		String strTypePattern = getPattern(context, strSelectedTypes);

		StringList slObjectSelects = new StringList();
		slObjectSelects.add(DomainConstants.SELECT_ID);
		if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
			slObjectSelects.addAll(strSelectables.split(PGWidgetConstants.KEY_COMMA_SEPARATOR));
			slObjectSelects.add(PGWidgetConstants.SELECT_PHYSICAL_ID);
			slObjectSelects.add(DomainConstants.SELECT_NAME);
			slObjectSelects.add(DomainConstants.SELECT_REVISION);
			slObjectSelects.add(DomainConstants.SELECT_CURRENT);
		}
		StringList slRelationshipSelectList = new StringList();
		if (UIUtil.isNotNullAndNotEmpty(strRelationshipSelects)) {
			slRelationshipSelectList = StringUtil.split(strRelationshipSelects, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		}
		DomainObject domObj = DomainObject.newInstance(context, strObjectId);
		MapList mlObjectList = domObj.getRelatedObjects(context, strRelPattern, // relationshipPattern
				strTypePattern, // typePattern
				slObjectSelects, // objectSelects
				slRelationshipSelectList, // relationshipSelects
				Boolean.parseBoolean(strGetTo), // getTo
				Boolean.parseBoolean(strGetFrom), // getFrom
				Short.parseShort(strExpandLevel), // recurseToLevel
				strWhereCondition, // objectWhere
				strRelWhereCondition, // relationshipWhere
				Short.parseShort(strLimit));// limit

		return mlObjectList;
	}

	/**
	 * Method to convert String json which is usually the value of attributes to
	 * JsonObject
	 * 
	 * @param strJsonString : String json
	 * @return : JsonObject created from String json
	 */
	public static JsonObject getJsonFromJsonString(String strJsonString) {
		StringReader srJsonString = new StringReader(strJsonString);
		Map<String, String> configMap = new HashMap<>();
		configMap.put(PGWidgetConstants.MAX_STRING_LENGTH, PGWidgetConstants.VALUE_KILOBYTES);
		JsonReaderFactory factory = Json.createReaderFactory(configMap);
		try (JsonReader jsonReader = factory.createReader(srJsonString)) {
			return jsonReader.readObject();
		} finally {
			srJsonString.close();
		}
	}

	/**
	 * Method to get JSON from Map
	 * 
	 * @param context
	 * @param mpObjectInfo
	 * @return
	 * @throws Exception
	 */
	public static JsonObject getJSONFromMap(Context context, Map<?, ?> mpObjectInfo) {
		JsonObjectBuilder jsonObject = null;
		context.getSession().getLanguage();

		jsonObject = Json.createObjectBuilder();
		String strAttrName = "";
		String strAttrValue = "";
		for (Map.Entry<?, ?> entry : mpObjectInfo.entrySet()) {
			strAttrName = (String) entry.getKey();
			strAttrValue = extractMultiValueSelect(mpObjectInfo, strAttrName);
			if (DomainConstants.SELECT_TYPE.equals(strAttrName)) {
				try {
					String strTypeDisplayName = i18nNow.getTypeI18NString(strAttrValue, context.getLocale().getLanguage());
					jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
				} catch (MatrixException e) {
					jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strAttrValue);
				}
				jsonObject.add(PGWidgetConstants.KEY_OBJECT_TYPE, strAttrValue);
			}
			if (DomainConstants.SELECT_ATTRIBUTE_TITLE.equals(strAttrName)) {
				jsonObject.add(PGWidgetConstants.KEY_DISPLAY_NAME,
						UIUtil.isNotNullAndNotEmpty(strAttrValue) ? strAttrValue : mpObjectInfo.get(DomainConstants.SELECT_NAME).toString());
			}
			jsonObject.add(strAttrName, checkNullValueforString(strAttrValue));
		}
		return jsonObject.build();
	}

	/**
	 * Method to get Map from Json
	 * 
	 * @param context
	 * @param jsonObj
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	public static Map<String, String> getMapFromJson(Context context, JsonObject jsonObj) {
		Map<String, String> mpObjectInfo = new HashMap<>();
		context.getSession().getLanguage();

		String strAttrName = "";
		String strAttrValue = "";
		for (Entry<?, ?> entry : jsonObj.entrySet()) {
			strAttrName = (String) entry.getKey();
			strAttrValue = extractMultiValueSelect(jsonObj, strAttrName);
			mpObjectInfo.put(strAttrName, checkNullValueforString(strAttrValue));
		}
		return mpObjectInfo;
	}

	/**
	 * Method to extract MultiValue Select
	 * 
	 * @param mpData
	 * @param strSelect
	 * @return
	 * @throws Exception
	 */
	public static String extractMultiValueSelect(Map<?, ?> mpData, String strSelect) {
		String strValue = DomainConstants.EMPTY_STRING;
		try {
			strValue = (String) mpData.get(strSelect);
		} catch (Exception e) {
			StringBuilder sbValues = new StringBuilder();
			@SuppressWarnings("unchecked")
			List<String> listValues = (List<String>) mpData.get(strSelect);
			listValues = removeDuplicates(listValues);
			Collections.sort(listValues);

			for (int i = 0; i < listValues.size(); i++) {
				sbValues.append(listValues.get(i));
				sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
			}
			if (sbValues.length() > 0) {
				sbValues.setLength(sbValues.length() - 1);
			}
			strValue = sbValues.toString();
		}
		return strValue;
	}

	public static String extractMultiValueSelect(JsonObject jsonObj, String strSelect) {
		String strValue = DomainConstants.EMPTY_STRING;
		try {
			strValue = jsonObj.getString(strSelect);
		} catch (Exception e) {
			StringBuilder sbValues = new StringBuilder();
			@SuppressWarnings("unchecked")
			List<String> listValues = (List<String>) jsonObj.get(strSelect);
			listValues = removeDuplicates(listValues);
			Collections.sort(listValues);

			for (int i = 0; i < listValues.size(); i++) {
				sbValues.append(listValues.get(i));
				sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
			}
			if (sbValues.length() > 0) {
				sbValues.setLength(sbValues.length() - 1);
			}
			strValue = sbValues.toString();
		}
		return strValue;
	}

	/**
	 * Method to remove duplicate values from a list
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private static List<String> removeDuplicates(List<String> list) {
		List<String> tempList = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if (!tempList.contains(list.get(i))) {
				tempList.add(list.get(i));
			}
		}

		return tempList;
	}

	/**
	 * Get Type Pattern for all Schema
	 * 
	 * @param context
	 * @param strSelectedSchema
	 * @return
	 */
	public static String getPattern(Context context, String strSelectedSchema) {
		if (strSelectedSchema.equals(DomainConstants.QUERY_WILDCARD)) {
			return strSelectedSchema;
		}

		StringList slSchemaList = StringUtil.split(strSelectedSchema, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		StringBuilder sbSchemaPattern = new StringBuilder();

		for (int iter = 0; iter < slSchemaList.size(); iter++) {
			String strSchema = slSchemaList.get(iter);
			String strSymSchema = PropertyUtil.getSchemaProperty(context, strSchema);
			if (UIUtil.isNullOrEmpty(strSymSchema)) {
				strSymSchema = strSchema;
			}
			sbSchemaPattern.append(strSymSchema);
			sbSchemaPattern.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
		}
		String strResult = sbSchemaPattern.toString();
		if (UIUtil.isNotNullAndNotEmpty(strResult)) {
			strResult = strResult.substring(0, strResult.length() - 1);
		}
		return strResult;
	}

	/**
	 * Method to get formatted expression for attribute selectable
	 * 
	 * @param context
	 * @param strExpression
	 * @return
	 * @throws Exception
	 */
	public static String getFormattedExpression(String strExpression) {
		String strFormattedExp = "";
		strFormattedExp = strExpression.replace("attribute[", DomainConstants.EMPTY_STRING);
		strFormattedExp = strFormattedExp.replace("]", DomainConstants.EMPTY_STRING);
		return strFormattedExp;
	}

	/**
	 * Method to convert the date to eMatrix date format
	 * 
	 * @param context
	 * @param strValue
	 * @return
	 * @throws ParseException
	 */
	public static String getEMatrixDateFormat(String strValue) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(PGWidgetConstants.KEY_SIMPLE_DATEFORMAT);
		Date date = formatter.parse(strValue);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return convertDateToServerTimeZone(cal);
	}

	/**
	 * Method to convert date to server time zone
	 * 
	 * @param cal : Calendar object for current date
	 * @return : String formatted date in server time zone
	 */
	public static String convertDateToServerTimeZone(Calendar cal) {
		Calendar calObj = Calendar.getInstance();
		TimeZone tzCurrentObj = calObj.getTimeZone();
		String strServerTimeZone = tzCurrentObj.getID();

		String strMatrixDateFormat = eMatrixDateFormat.getEMatrixDateFormat();
		SimpleDateFormat sdfDateTime = new SimpleDateFormat(strMatrixDateFormat);
		DateTimeFormatter dtmMatrixFormat = DateTimeFormatter.ofPattern(strMatrixDateFormat);

		String dateInString = sdfDateTime.format(cal.getTime());
		LocalDateTime ldt = LocalDateTime.parse(dateInString, dtmMatrixFormat);
		ZoneId ziSeverZoneId = ZoneId.of(strServerTimeZone);
		ZonedDateTime zdtServerZonedDateTime = ldt.atZone(ziSeverZoneId);
		return dtmMatrixFormat.format(zdtServerZonedDateTime);
	}

	/**
	 * Method to get MQL notices
	 * 
	 * @param context
	 * @param sbMessage
	 * @param jsonStatus
	 * @throws MatrixException
	 */
	public static void createErrorMessage(Context context, StringBuilder sbMessage, JsonObjectBuilder jsonStatus) throws MatrixException {
		context.updateClientTasks();
		ClientTaskList ctlLoggedInUserTaskList = context.getClientTasks();
		ClientTaskItr clientTaskItr = new ClientTaskItr(ctlLoggedInUserTaskList);
		while (clientTaskItr.next()) {
			ClientTask ctUserTask = clientTaskItr.obj();
			String strNotice = ctUserTask.getTaskData();
			sbMessage.append(strNotice).append("\n");
		}
		context.clearClientTasks();
		jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
		jsonStatus.add(PGWidgetConstants.KEY_ERROR, sbMessage.toString());
	}

	/**
	 * Stack trace to string
	 * 
	 * @param ex
	 * @return
	 */
	public static String getExceptionTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		return sw.toString(); // stack trace as a string
	}
	
	public static String addExistingObjects(Context context, String strInput) {
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			String strFromId = jsonInput.getString(PGWidgetConstants.KEY_FROMOBJECTID);
			String strToId = jsonInput.getString(PGWidgetConstants.KEY_TOOBJECTID);
			String strRelName = jsonInput.getString(PGWidgetConstants.KEY_REL_NAME);

			Map mapConnection = addExisting(context, strFromId, strToId, strRelName, false);
			if (mapConnection != null && !mapConnection.isEmpty()) {
				return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS)
						.build().toString();
			} else {
				throw new Exception("Error occured while connection objects");
			}
		} catch (Exception e) {
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
	}

	/**
	 * This method connects objects
	 * 
	 * @param context
	 * @param strFromObjectId From side objectId
	 * @param strIdsToConnect To side objectIds
	 * @param strRelName      relationship name for connecting from and to side
	 *                        objects
	 * @return nothing
	 * @throws Exception
	 */
	public static Map<String, String> addExisting(Context context, String strFromObjectId, String strIdsToConnect, String strRelName, boolean allowDuplicates)
			throws Exception {
		Map<String, String> mapReturn = new HashMap<>();
		if (UIUtil.isNotNullAndNotEmpty(strFromObjectId) && UIUtil.isNotNullAndNotEmpty(strIdsToConnect)) {
			StringList slIdsToConnect = new StringList();
			DomainObject domFromObject = null;
			String strObjId = DomainConstants.EMPTY_STRING;
			StringList toObjectList = StringUtil.split(strIdsToConnect, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			ContextUtil.startTransaction(context, true);
			try {
				for (int i = 0; i < toObjectList.size(); i++) {
					strObjId = toObjectList.get(i);
					slIdsToConnect.add(strObjId);
				}
				if (!allowDuplicates) {
					// TODO: handle logic if duplicates are not allowed
				}
				if (!slIdsToConnect.isEmpty()) {
					domFromObject = DomainObject.newInstance(context, strFromObjectId);
					String[] toObjectId = slIdsToConnect.toArray(new String[slIdsToConnect.size()]);
					mapReturn = DomainRelationship.connect(context, domFromObject, strRelName, true, toObjectId);
				}
				ContextUtil.commitTransaction(context);
			} catch (Exception e) {
				ContextUtil.abortTransaction(context);
				throw new FrameworkException(e);
			}
		}
		return mapReturn;
	}

	/**
	 * Method to delete multiple objects
	 * 
	 * @param context
	 * @param strObjectIds
	 * @throws Exception
	 */
	public static void deleteObjects(Context context, String strObjectIds) throws Exception {
		if (UIUtil.isNotNullAndNotEmpty(strObjectIds)) {
			StringList slObjectIds = StringUtil.split(strObjectIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			ContextUtil.startTransaction(context, true);
			try {
				if (!slObjectIds.isEmpty()) {
					String[] toObjectId = slObjectIds.toArray(new String[slObjectIds.size()]);
					DomainObject.deleteObjects(context, toObjectId);
				}
				ContextUtil.commitTransaction(context);
			} catch (Exception e) {
				ContextUtil.abortTransaction(context);
				throw e;
			}
		}
	}

	/**
	 * Method to disconnect a connection
	 * 
	 * @param context
	 * @param strRelIds connection ids to be disconnected
	 * @return nothing
	 * @throws Exception
	 */
	public static void removeSelected(Context context, String strRelIds) throws FrameworkException {
		StringList relIdsList = StringUtil.split(strRelIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		if (!relIdsList.isEmpty()) {
			try {
				DomainRelationship.disconnect(context, BusinessUtil.toStringArray(relIdsList));
			} catch (FrameworkException e) {
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
				throw e;
			}
		}
	}

	/**
	 * Method to perform promote or demote operations on any Object
	 * 
	 * @param context
	 * @param strOperation
	 * @return JSON Object consisting of the information to be displayed
	 * @throws MatrixException
	 * @throws Exception
	 */
	public static String promoteDemoteObject(Context context, String strObjectId, String strOperation) throws MatrixException {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		JsonArrayBuilder jsonStatusArr = Json.createArrayBuilder();
		StringList slObjIds = StringUtil.split(strObjectId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		if (slObjIds != null && UIUtil.isNotNullAndNotEmpty(strOperation)) {
			try {
				StringList slObjSelectables = new StringList(2);
				slObjSelectables.add(DomainConstants.SELECT_CURRENT);
				slObjSelectables.add(DomainConstants.SELECT_POLICY);
				ContextUtil.startTransaction(context, true);
				isTransactionActive = true;
				for (int i = 0; i < slObjIds.size(); i++) {
					DomainObject domObj = DomainObject.newInstance(context, slObjIds.get(i));
					if (OPERATION_PROMOTE.equals(strOperation)) {
						domObj.promote(context);
					} else {
						domObj.demote(context);
					}
					Map<?, ?> objMap = domObj.getInfo(context, slObjSelectables);
					String strPolicy = (String) objMap.get(DomainConstants.SELECT_POLICY);
					String strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
					JsonArray jsonStatesArray = getObjectStates(context, slObjIds.get(i), strPolicy, strCurrent);
					jsonStatus.add(PGWidgetConstants.KEY_STATES, jsonStatesArray);
					jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
					jsonStatus.add(DomainConstants.SELECT_CURRENT, strCurrent);
					jsonStatus.add(DomainConstants.SELECT_ID, slObjIds.get(i));
					jsonStatusArr.add(jsonStatus);
				}
				ContextUtil.commitTransaction(context);
			} catch (MatrixException e) {
				if (isTransactionActive) {
					ContextUtil.abortTransaction(context);
					isExceptionOccurred = true;
					sbMessage.append(e.getMessage()).append("\n");
				}
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			}
			if (isExceptionOccurred) {
				PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
				return jsonStatus.build().toString();

			}
		}
		return jsonStatusArr.build().toString();

	}

	/**
	 * Method to perform promote or demote to any state on any Object *
	 * 
	 * @param context
	 * @param strOperation
	 * @return JSON Object consisting of the information to be displayed
	 * @throws MatrixException
	 * @throws Exception
	 */
	public static String setObjectState(Context context, String strObjectId, String strOperation, String strResultState) throws MatrixException {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		JsonArrayBuilder jsonStatusArr = Json.createArrayBuilder();
		StringList slObjIds = StringUtil.split(strObjectId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		if (slObjIds != null && UIUtil.isNotNullAndNotEmpty(strOperation)) {
			try {
				StringList slObjSelectables = new StringList(2);
				slObjSelectables.add(DomainConstants.SELECT_CURRENT);
				slObjSelectables.add(DomainConstants.SELECT_POLICY);
				ContextUtil.startTransaction(context, true);
				isTransactionActive = true;
				for (int i = 0; i < slObjIds.size(); i++) {
					DomainObject domObj = DomainObject.newInstance(context, slObjIds.get(i));
					if (UIUtil.isNotNullAndNotEmpty(strResultState)) {
						domObj.setState(context, strResultState);
					} else if (OPERATION_PROMOTE.equals(strOperation)) {
						domObj.promote(context);
					} else if (OPERATION_DEMOTE.equals(strOperation)) {
						domObj.demote(context);
					}
					Map<?, ?> objMap = domObj.getInfo(context, slObjSelectables);
					String strPolicy = (String) objMap.get(DomainConstants.SELECT_POLICY);
					String strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
					JsonArray jsonStatesArray = getObjectStates(context, slObjIds.get(i), strPolicy, strCurrent);
					jsonStatus.add(PGWidgetConstants.KEY_STATES, jsonStatesArray);
					jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
					jsonStatus.add(DomainConstants.SELECT_CURRENT, strCurrent);
					jsonStatus.add(DomainConstants.SELECT_ID, slObjIds.get(i));
					jsonStatusArr.add(jsonStatus);
				}
				ContextUtil.commitTransaction(context);
			} catch (MatrixException e) {
				if (isTransactionActive) {
					ContextUtil.abortTransaction(context);
					isExceptionOccurred = true;
					sbMessage.append(e.getMessage()).append("\n");
				}
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			}
			if (isExceptionOccurred) {
				PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
				return jsonStatus.build().toString();

			}
		}
		return jsonStatusArr.build().toString();

	}

	/**
	 * Method used convert Map to JsonObject
	 * 
	 * @param inputJsonObj : JsonObject in which converted JsonObject from Map will
	 *                     be merged
	 * @param inputMap     : Map to converted to JsonObject
	 * @return : JsonObject merged with converted Map and parent inputJsonObj
	 */
	public static JsonObject convertMapToJsonObj(JsonObject inputJsonObj, Map<?, ?> inputMap) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		inputMap.entrySet().forEach(e -> builder.add((String) e.getKey(),
				((e.getValue() instanceof StringList) ? getStringFromSL((StringList) e.getValue(), PGWidgetConstants.KEY_PIPE_SEPARATOR) : (String.valueOf(e.getValue())))));
		return builder.build();
	}

	/**
	 * This method is used to convert StringList to pipe separated String
	 * 
	 * @param slSelectValuelist : StringList to be converted
	 * @return : Pipe line separated String
	 */
	public static String getStringFromSL(StringList slSelectValuelist, String strSeparator) {

		StringBuilder sbSelectValue = new StringBuilder();
		String strSelectValue = "";
		String strReturnValue = "";
		for (int i = 0; i < slSelectValuelist.size(); i++) {
			strSelectValue = slSelectValuelist.get(i);
			sbSelectValue.append(strSelectValue).append(strSeparator);
		}

		strReturnValue = sbSelectValue.toString();

		if (UIUtil.isNotNullAndNotEmpty(strReturnValue)) {
			strReturnValue = strReturnValue.substring(0, strReturnValue.length() - 1);
		}

		return strReturnValue;
	}

	/**
	 * This method is used to convert JsonArray to comma separated String
	 * 
	 * @param jsonArray
	 * @return Comma line separated String
	 */
	public static String getStringFromJsonArray(JsonArray jsonArray) {

		StringBuilder sbSelectValue = new StringBuilder();
		String strSelectValue = "";
		String strReturnValue = "";
		for (int i = 0; i < jsonArray.size(); i++) {
			strSelectValue = jsonArray.getString(i);
			sbSelectValue.append(strSelectValue).append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
		}

		strReturnValue = sbSelectValue.toString();

		if (UIUtil.isNotNullAndNotEmpty(strReturnValue)) {
			strReturnValue = strReturnValue.substring(0, strReturnValue.length() - 1);
		}

		return strReturnValue;
	}

	public static JsonArray convertMapListToJsonFlatTable(MapList mlInput, int iLevel) throws NumberFormatException {
		Stack<JsonObject> stackJsonObj = new Stack<>();
		JsonArrayBuilder jsonObjArray = Json.createArrayBuilder();
		int nLevelCurrent = 1;
		int nLevelPrev = 1;
		int nLevelAppend = 0;
		for (Iterator<?> iterator = mlInput.iterator(); iterator.hasNext();) {

			Map<?, ?> tempMap = (Map<?, ?>) iterator.next();
			String strLevel = (String) tempMap.get(DomainConstants.SELECT_LEVEL);
			nLevelCurrent = Integer.parseInt(strLevel);
			nLevelAppend = nLevelCurrent + iLevel;

			// Leaf level node condition
			if (nLevelCurrent <= nLevelPrev) {
				if (!stackJsonObj.empty()) {
					jsonObjArray.add(stackJsonObj.pop());
				}

				for (int i = nLevelCurrent; i < nLevelPrev; i++) {
					stackJsonObj.pop();
				}
			}

			stackJsonObj.push(convertMapToJsonObj(stackJsonObj.empty() ? Json.createObjectBuilder().build() : stackJsonObj.peek(), tempMap,
					Integer.toString(nLevelAppend) + PGWidgetConstants.KEY_SELECTS_SEPARATOR));

			nLevelPrev = nLevelCurrent;
		}
		if (!stackJsonObj.empty()) {
			jsonObjArray.add(stackJsonObj.pop());
		}
		return jsonObjArray.build();
	}

	public static JsonObject convertMapToJsonObj(JsonObject inputJsonObj, Map<?, ?> inputMap, String strLevel) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		inputMap.entrySet().forEach(e -> builder.add(strLevel + (String) e.getKey(),
				((e.getValue() instanceof StringList) ? getStringFromSL((StringList) e.getValue(), PGWidgetConstants.KEY_PIPE_SEPARATOR) : (String) e.getValue())));
		return builder.build();
	}

	/**
	 * Converts maplist data to json array
	 * 
	 * @param context
	 * @param mlList  list of maps containing object details
	 * @return json array format
	 * @throws Exception
	 */
	public static JsonArray converMaplistToJsonArray(Context context, MapList mlList) throws Exception {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		try {
			JsonObjectBuilder jsonObject = null;
			Map<?, ?> objMap = null;
			String strValue = DomainConstants.EMPTY_STRING;
			Object objValue = null;
			StringList slTemp = null;
			StringBuilder sbValues = null;
			String strTypeDisplayName = null;
			String strLocale = context.getLocale().getLanguage();
			for (int i = 0; i < mlList.size(); i++) {
				jsonObject = Json.createObjectBuilder();
				objMap = (Map<?, ?>) mlList.get(i);
				for (Entry<?, ?> entry : objMap.entrySet()) {
					objValue = entry.getValue();
					if (objValue == null || "".equals(objValue)) {
						objValue = DomainConstants.EMPTY_STRING;
					}
					if (objValue instanceof String) {
						strValue = (String) objValue;
					} else if (objValue instanceof StringList) {
						slTemp = (StringList) objValue;
						sbValues = new StringBuilder();
						for (int j = 0; j < slTemp.size(); j++) {
							if (UIUtil.isNotNullAndNotEmpty(slTemp.get(j))) {
								sbValues.append(slTemp.get(j));
							}
							if (j != slTemp.size() - 1) {
								sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
							}
						}
						strValue = sbValues.toString();
					} else if (DomainConstants.SELECT_TYPE.equals(entry.getKey())) {
						strTypeDisplayName = i18nNow.getTypeI18NString((String) objValue, strLocale);
						jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
						jsonObject.add(PGWidgetConstants.KEY_OBJECT_TYPE, (String) objValue);
					} else if (DomainConstants.SELECT_ATTRIBUTE_TITLE.equals(entry.getKey())) {
						jsonObject.add(PGWidgetConstants.KEY_DISPLAY_NAME,
								UIUtil.isNotNullAndNotEmpty(strValue) ? strValue : objMap.get(DomainConstants.SELECT_NAME).toString());
					} else if (DomainConstants.SELECT_ID.equals(entry.getKey())) {
						jsonObject.add(PGWidgetConstants.KEY_VALUE, (String) objValue);
					}
					if (DomainConstants.SELECT_CURRENT.equals(entry.getKey())) {
						jsonObject.add(PGWidgetConstants.KEY_STATE, i18nNow.getStateI18NString((String)objMap.get(DomainConstants.SELECT_POLICY), (String)entry.getValue(), strLocale));
					}
					jsonObject.add((String) entry.getKey(), strValue);
				}
				jsonArr.add(jsonObject);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
		}
		return jsonArr.build();
	}

	public static JsonObject updateAttributesOnObject(Context context, Map map) throws FrameworkException {
		JsonObjectBuilder jsonReturn = Json.createObjectBuilder();
		String strObjId = (String) map.remove(DomainConstants.SELECT_ID);
		DomainObject domObj;
		try {
			domObj = DomainObject.newInstance(context, strObjId);
			domObj.setAttributeValues(context, map);
			jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			throw e;
		}
		return jsonReturn.build();
	}

	/**
	 * Generic method to get physical id from TNR by using MQL query
	 * 
	 * @param context     : Context eMatrix context object
	 * @param strType     : String type
	 * @param strName     : String name
	 * @param strRevision : String revision
	 * @return : String physical id from TNR
	 * @throws FrameworkException
	 */
	public static String getObjectId(Context context, String strType, String strName, String strRevision) throws FrameworkException {
		String strObjId = "";
		MapList mlList = DomainObject.findObjects(context, strType, // type pattern
				strName, // name pattern
				strRevision, // revision pattern
				null, // ownerPattern
				DomainConstants.QUERY_WILDCARD, // vaultPattern
				null, // where clause
				null, // queryName
				true, // expandType
				new StringList(DomainConstants.SELECT_ID), // objectSelects
				Short.parseShort("0")); // objectLimit
		if (!mlList.isEmpty()) {
			Map<?, ?> objMap = (Map<?, ?>) mlList.get(0);
			strObjId = (String) objMap.get(DomainConstants.SELECT_ID);
		}
		return strObjId;
	}
	
	/**
	 * This method is used to retrieve ObjectId from passed find criteria
	 * 
	 * @param context The enovia Context object
	 * @param strId   PhysicalId or UUID passed
	 * @return Stringified JSON object consisting of the ObjectId
	 */
	public static String getObjectIdFromUUID(Context context, String strInput) throws Exception {
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
		if(!jsonInput.containsKey(PGWidgetConstants.TYPE_PATTERN) && !jsonInput.containsKey(PGWidgetConstants.NAME_PATTERN)) {
			throw new IllegalArgumentException();
		}
		JsonObjectBuilder jObjParams = Json.createObjectBuilder();
		jObjParams.add(PGWidgetConstants.TYPE_PATTERN,jsonInput.containsKey(PGWidgetConstants.TYPE_PATTERN) ? jsonInput.getString(PGWidgetConstants.TYPE_PATTERN) : PGWidgetConstants.CONSTANT_STRING_STAR);
		jObjParams.add(PGWidgetConstants.NAME_PATTERN,jsonInput.containsKey(PGWidgetConstants.NAME_PATTERN) ? jsonInput.getString(PGWidgetConstants.NAME_PATTERN) : PGWidgetConstants.CONSTANT_STRING_STAR);
		jObjParams.add(PGWidgetConstants.REVISION_PATTERN,jsonInput.containsKey(PGWidgetConstants.REVISION_PATTERN) ? jsonInput.getString(PGWidgetConstants.REVISION_PATTERN) : PGWidgetConstants.CONSTANT_STRING_STAR);
		jObjParams.add(PGWidgetConstants.OBJECT_LIMIT,jsonInput.containsKey(PGWidgetConstants.OBJECT_LIMIT) ? jsonInput.getString(PGWidgetConstants.OBJECT_LIMIT) : PGWidgetConstants.CONSTANT_STRING_ZERO);
		jObjParams.add(PGWidgetConstants.WHERE_EXP,jsonInput.containsKey(PGWidgetConstants.WHERE_EXP) ? jsonInput.getString(PGWidgetConstants.WHERE_EXP) : DomainConstants.EMPTY_STRING);
		jObjParams.add(PGWidgetConstants.EXPAND_TYPE,jsonInput.containsKey(PGWidgetConstants.EXPAND_TYPE) ? jsonInput.getString(PGWidgetConstants.EXPAND_TYPE) : PGWidgetConstants.STRING_FALSE);
		jObjParams.add(PGWidgetConstants.OBJ_SELECT,jsonInput.containsKey(PGWidgetConstants.OBJ_SELECT) ? jsonInput.getString(PGWidgetConstants.OBJ_SELECT) : DomainConstants.EMPTY_STRING);
		jObjParams.add(PGWidgetConstants.DURATION,jsonInput.containsKey(PGWidgetConstants.DURATION) ? jsonInput.getString(PGWidgetConstants.DURATION) : DomainConstants.EMPTY_STRING);
		jObjParams.add(PGWidgetConstants.ALLOWED_STATE,jsonInput.containsKey(PGWidgetConstants.ALLOWED_STATE) ? jsonInput.getString(PGWidgetConstants.ALLOWED_STATE) : DomainConstants.EMPTY_STRING);
		jObjParams.add(PGWidgetConstants.SHOW_OWNED,jsonInput.containsKey(PGWidgetConstants.SHOW_OWNED) ? jsonInput.getString(PGWidgetConstants.SHOW_OWNED) : PGWidgetConstants.STRING_TRUE);
		
		MapList objDetailList = findObjects(context, jObjParams.build());
		String strObjId = null;
		if (objDetailList.size() != 0 && !objDetailList.isEmpty()) {
			Map<String, String> objectMap = (Map<String, String>) objDetailList.get(0);
			strObjId = objectMap.get(PGWidgetConstants.KEY_OBJECTID);
		}
		if (strObjId != null) {
			jsonOutput.add(PGWidgetConstants.KEY_OBJECTID, strObjId);
		}else {
			String strNoObjectId = EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", context.getLocale(), "emxFramework.UIForm.NoObjectID");
			jsonOutput.add(PGWidgetConstants.KEY_ERROR, strNoObjectId);
			throw new Exception(jsonOutput.build().toString());
		}
		return jsonOutput.build().toString();
	}
	
	public static String convertPhyIdToObjId(Context context,String strPhysicalId) throws Exception
	{
        return FrameworkUtil.getOIDfromPID(context,strPhysicalId);
	}

	public static MapList findObjects(Context context, JsonObject jsonInputInfo) throws Exception {
		String strTypePattern = jsonInputInfo.getString(PGWidgetConstants.TYPE_PATTERN);
		String strNamePattern = jsonInputInfo.getString(PGWidgetConstants.NAME_PATTERN);
		String strRevisionPattern = jsonInputInfo.getString(PGWidgetConstants.REVISION_PATTERN);
		String strObjectLimit = jsonInputInfo.getString(PGWidgetConstants.OBJECT_LIMIT);
		String strWhereExpression = jsonInputInfo.getString(PGWidgetConstants.WHERE_EXP);
		String strExpandType = jsonInputInfo.getString(PGWidgetConstants.EXPAND_TYPE);
		String strSelectables = jsonInputInfo.getString(PGWidgetConstants.OBJ_SELECT);
		String strDuration = jsonInputInfo.getString(PGWidgetConstants.DURATION);
		String strAllowedStates = jsonInputInfo.getString(PGWidgetConstants.ALLOWED_STATE);
		String strShowOwned = jsonInputInfo.getString(PGWidgetConstants.SHOW_OWNED);

		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(PGWidgetConstants.SELECT_PHYSICAL_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
			objectSelects.addAll(strSelectables.split(PGWidgetConstants.KEY_COMMA_SEPARATOR));
		}
		String strOwnerPattern = DomainConstants.QUERY_WILDCARD;

		if (UIUtil.isNotNullAndNotEmpty(strOwnerPattern) && "true".equalsIgnoreCase(strShowOwned))
			strOwnerPattern = context.getUser();

		String strWhere = getWhereClause(context, strDuration, strWhereExpression, strAllowedStates);
		return DomainObject.findObjects(context, 
				getPattern(context, strTypePattern), 
				getPattern(context, strNamePattern), 
				strRevisionPattern, 
				strOwnerPattern, // ownerPattern
				DomainConstants.QUERY_WILDCARD, // String vaultPattern
				strWhere, // where expression
				null, // The query name to save results
				Boolean.getBoolean(strExpandType), // true, if the query should find subtypes of the given types
				objectSelects, Short.parseShort(strObjectLimit));
	}

	static JsonObject findObjectsJson(Context context, JsonObject jsonInputInfo) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {

			JsonArrayBuilder jsonArr = Json.createArrayBuilder();

			MapList mlList = findObjects(context, jsonInputInfo);

			if (mlList != null && !mlList.isEmpty()) {
				mlList.addSortKey(DomainConstants.SELECT_NAME, PGWidgetConstants.ASCENDING, PGWidgetConstants.STRING);
				mlList.sort();
				JsonObjectBuilder jsonObject = null;
				String strLanguage = context.getSession().getLanguage();
				Map<?, ?> objMap = null;
				String strKey;
				String strValue = DomainConstants.EMPTY_STRING;
				String strId;
				Object objValue;
				String strTypeDisplayName;
				StringList slTemp;
				StringBuilder sbValues;
				for (int i = 0; i < mlList.size(); i++) {
					jsonObject = Json.createObjectBuilder();
					objMap = (Map<?, ?>) mlList.get(i);
					strId = checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID));
					jsonObject.add(DomainConstants.SELECT_ID, strId);
					strTypeDisplayName = EnoviaResourceBundle.getTypeI18NString(context, (String) objMap.get(DomainConstants.SELECT_TYPE), strLanguage);
					jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
					jsonObject.add(PGWidgetConstants.KEY_NAME, checkNullValueforString((String) objMap.get(DomainConstants.SELECT_NAME)));
					jsonObject.add(PGWidgetConstants.KEY_REVISIOM, checkNullValueforString((String) objMap.get(DomainConstants.SELECT_REVISION)));

					objMap.remove(DomainConstants.SELECT_NAME);
					objMap.remove(DomainConstants.SELECT_ID);
					boolean hasAccess = FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strId), PGWidgetConstants.KEY_READ);

					for (Entry<?, ?> entry : objMap.entrySet()) {
						strKey = (String) entry.getKey();						
						 objValue = entry.getValue();
						if (objValue == null || "".equals(objValue)) {
							objValue = DomainConstants.EMPTY_STRING;
						}
						if (objValue instanceof String) {
							strValue = (String) objValue;
							strValue = checkNullValueforString(strValue);
						} else if (objValue instanceof StringList) {
							slTemp = (StringList) objValue;
							sbValues = new StringBuilder();
							for (int j = 0; j < slTemp.size(); j++) {
								if (UIUtil.isNotNullAndNotEmpty(slTemp.get(j))) {
									sbValues.append(slTemp.get(j));
								}
								if (j != slTemp.size() - 1) {
									sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
								}
							}
							strValue = sbValues.toString();
						}
						if (!hasAccess) {
							if (strValue.equals(PGWidgetConstants.DENIED)) {
								jsonObject.add(strKey,
										EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", context.getLocale(), "emxFramework.Access.NoAccess"));
							} else {
								jsonObject.add(strKey, strValue);
							}
						} else {
							jsonObject.add(strKey, strValue);
						}
					}
					jsonArr.add(jsonObject);
				}
			}
			output.add("data", jsonArr.build());
		} catch (MatrixException ex) {
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, getExceptionTrace(ex));
		}
		return output.build();
	}

	public static String getWhereClause(Context context, String strDuration, String whereClause, String strAllowedStates) throws Exception {
		StringBuilder sbWhereClause = new StringBuilder();

		sbWhereClause.append("(policy != '").append(DomainConstants.POLICY_VERSION_DOCUMENT).append("')");
		if (UIUtil.isNotNullAndNotEmpty(whereClause)) {
			sbWhereClause.append(" && ");
			sbWhereClause.append(whereClause);

		}
		if (UIUtil.isNotNullAndNotEmpty(strAllowedStates)) {
			if (UIUtil.isNotNullAndNotEmpty(sbWhereClause.toString())) {
				sbWhereClause.append(" && ");
			}

			sbWhereClause.append(buildWhereExpAllowStates(strAllowedStates));
		}
		if (UIUtil.isNotNullAndNotEmpty(strDuration)) {
			String dateWhereClause = getDateQuery(context, strDuration);
			if (UIUtil.isNotNullAndNotEmpty(sbWhereClause.toString())) {
				sbWhereClause.append(" && ");
			}
			sbWhereClause.append(dateWhereClause);

		}
		return sbWhereClause.toString();
	}

	/**
	 * Generate date expression on Released on value
	 * 
	 * @param context
	 * @param strReleasedInDuration
	 * @return
	 * @throws Exception
	 */
	private static String getDateQuery(Context context, String strDuration) throws Exception {
		StringBuilder sbBuildQuery = new StringBuilder();

		new SimpleDateFormat(eMatrixDateFormat.strEMatrixDateFormat, Locale.US);
		DateFormat dtFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
		java.util.TimeZone tz = java.util.TimeZone.getTimeZone(context.getSession().getTimezone());
		double dbMilisecondsOffset = (double) (-1) * tz.getRawOffset();
		double clientTZOffset = (Double.valueOf(dbMilisecondsOffset / (1000 * 60 * 60))).doubleValue();

		String strCurrentDate = dtFormat.format(new Date());
		strCurrentDate = eMatrixDateFormat.getFormattedInputDateTime(context, strCurrentDate, PGWidgetConstants.TIME_FORMAT_1, clientTZOffset, Locale.US);
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (PGWidgetConstants.LAST_WEEK.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -7);
		} else if (PGWidgetConstants.LAST_TWO_WEEKS.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -14);
		} else if (PGWidgetConstants.LAST_MONTH.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -30);
		} else if (PGWidgetConstants.LAST_THREE_MONTHS.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -90);
		} else if (PGWidgetConstants.LAST_SIX_MONTHS.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -180);
		}

		Date start = cal.getTime();
		String strFromDate = dtFormat.format(start);
		strFromDate = eMatrixDateFormat.getFormattedInputDateTime(context, strFromDate, PGWidgetConstants.TIME_FORMAT_2, clientTZOffset, Locale.US);

		sbBuildQuery.append("((");
		sbBuildQuery.append(PGWidgetConstants.KEY_MODIFIED).append(" >= ");
		sbBuildQuery.append("'" + strFromDate + "'");
		sbBuildQuery.append(")");
		sbBuildQuery.append(" && ");
		sbBuildQuery.append("(");
		sbBuildQuery.append(PGWidgetConstants.KEY_MODIFIED).append(" <= ");
		sbBuildQuery.append("'" + strCurrentDate + "'");
		sbBuildQuery.append("))");
		return sbBuildQuery.toString();
	}

	public static StringBuilder buildWhereExpAllowStates(String strAllowedStates) {
		StringBuilder sbObjectWhere = new StringBuilder();
		sbObjectWhere.append("(");
		StringList slallowedStates = StringUtil.split(strAllowedStates, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		int iAllowedStatesCount = slallowedStates.size();
		for (int i = 0; i < iAllowedStatesCount; i++) {
			StringList policyStateInfo = StringUtil.split(slallowedStates.get(i), ".");
			sbObjectWhere.append(DomainConstants.SELECT_CURRENT).append(" == '").append(policyStateInfo.get(1)).append("' || ");
		}
		if (iAllowedStatesCount > 0) {
			sbObjectWhere.delete(sbObjectWhere.lastIndexOf("||"), sbObjectWhere.length());
		}
		sbObjectWhere.append(")");
		return sbObjectWhere;
	}

	public static DomainObject createObjectWithAutoname(Context context, String strType, String strPolicy) {
		DomainObject domObj = null;
		try {
			domObj = DomainObject.newInstance(context);
			String strSymbolicTypeName = FrameworkUtil.getAliasForAdmin(context, PGWidgetConstants.KEY_TYPE, strType, true);
			String strAutoName = DomainObject.getAutoGeneratedName(context, strSymbolicTypeName, "");

			domObj.createObject(context, strType, strAutoName, DomainConstants.EMPTY_STRING, strPolicy, context.getVault().getName());
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return domObj;
	}

	public static boolean isConnected(Context context, String strFromId, String strToId, String strRelName) throws FrameworkException {

		DomainObject domFrom = DomainObject.newInstance(context, strFromId);
		StringList slToIdList = domFrom.getInfoList(context, "from[" + strRelName + "].to.id");
		if (slToIdList != null && slToIdList.contains(strToId)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Method to get all IP Control classes for current user
	 * 
	 * @param request      : HttpServletRequest request param
	 * @param strInputData : String classification value Restricted or 'Highly
	 *                     Restricted' along with object selects
	 * @return
	 * @return : String json with list of IP Classes
	 * @throws FrameworkException,MatrixException
	 * @throws Exception
	 */
	public static String getIPClassesForUser(Context context, String strInputData) throws MatrixException {

		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjResult = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);

		String strClassificationValue = jsonInputData.getString(KEY_PGIPCLASSIFICATION);
		String strObjSelectsString = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		StringList slIPValues = new StringList();

		if (UIUtil.isNotNullAndNotEmpty(strClassificationValue)) {
			slIPValues.addAll(strClassificationValue.split(PGWidgetConstants.KEY_COMMA_SEPARATOR));
			StringList tempList, slIPClasslist = null;
			String[] strJPOArgs = null;
			HashMap<String, String> hmArgsMap = null;
			for (int i = 0; i < slIPValues.size(); i++) {
				hmArgsMap = new HashMap<>();
				tempList = new StringList();
				hmArgsMap.put(KEY_CLASSIFICATION_VALUE, slIPValues.get(i));
				strJPOArgs = JPO.packArgs(hmArgsMap);
				slIPClasslist = JPO.invoke(context, PROGRAM_IP_SECURITY_COMMON_UTIL, strJPOArgs, METHOD_GET_ALL_IP_CLASSES, strJPOArgs, StringList.class);
				tempList.addAll(slIPClasslist);
				getObjSelectsDetailsForClass(context, tempList, strObjSelectsString, jsonObjOutput);
				jsonObjResult.add(slIPValues.get(i), jsonObjOutput);
			}

		}

		return jsonObjResult.build().toString();
	}

	/**
	 * Method to add get selectables details for classes
	 * 
	 * @param context
	 * @param slClasslist
	 * @param strObjSelectsString
	 * @param jsonObjOutput
	 * @throws FrameworkException
	 */
	private static void getObjSelectsDetailsForClass(Context context, StringList slClasslist, String strObjSelectsString, JsonObjectBuilder jsonObjOutput)
			throws FrameworkException {

		JsonArrayBuilder jsonArrayObj = Json.createArrayBuilder();
		String[] strObjectIds = getStringArrayFromStringList(slClasslist);
		JsonObjectBuilder jsonObjInfo = null;
		StringList slObjectselects = StringUtil.split(strObjSelectsString, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		slObjectselects.add(DomainConstants.SELECT_CURRENT);
		slObjectselects.add(SELECT_HAS_CLASS_ACCESS);
		slObjectselects.add(PGWidgetConstants.SELECT_PHYSICAL_ID);
		slObjectselects.add(DomainConstants.SELECT_ID);
		slObjectselects.add(DomainConstants.SELECT_TYPE);
		MapList mlClassInfoList = DomainObject.getInfo(context, strObjectIds, slObjectselects);

		for (int i = 0; i < mlClassInfoList.size(); i++) {
			Map<?, ?> objectMap = (Map<?, ?>) mlClassInfoList.get(i);
			String strState = (String) objectMap.get(DomainConstants.SELECT_CURRENT);

			if (STATE_ACTIVE.equals(strState)) {
				jsonObjInfo = Json.createObjectBuilder();
				jsonObjInfo.add(DomainConstants.SELECT_NAME, objectMap.get(DomainConstants.SELECT_NAME).toString());
				jsonObjInfo.add(PGWidgetConstants.KEY_VALUE, objectMap.get(DomainConstants.SELECT_NAME).toString());
				jsonObjInfo.add(DomainConstants.SELECT_ID, objectMap.get(DomainConstants.SELECT_ID).toString());
				jsonArrayObj.add(jsonObjInfo);
			}
		}

		jsonObjOutput.add(KEY_CLASS_LIST, jsonArrayObj);
	}

	/**
	 * Method to convert StrinList to Array of String
	 * 
	 * @param slIPClasslist
	 * @return
	 */
	private static String[] getStringArrayFromStringList(StringList slIPClasslist) {
		int iSize = slIPClasslist.size();
		String[] strOIDArray = new String[iSize];
		for (int i = 0; i < iSize; i++) {
			strOIDArray[i] = slIPClasslist.get(i);
		}
		return strOIDArray;
	}

	/**
	 * Method to retrieve ranges for an attribute
	 * 
	 * @param context
	 * @param strAttributeNames
	 * @return
	 * @throws Exception
	 */
	public static JsonObject getAttributeRangeValues(Context context, String strAttributeNames) throws MatrixException {
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

	public static MapList getTreeStructureMaplist(Context context, MapList mlInput, int iLevel) throws NumberFormatException {
		Stack<Map> stackJsonObj = new Stack<>();
		MapList mlReturn = new MapList();
		int nLevelCurrent = 1;
		int nLevelPrev = 1;
		int nLevelAppend = 0;
		for (Iterator<?> iterator = mlInput.iterator(); iterator.hasNext();) {
			Map<?, ?> tempMap = (Map<?, ?>) iterator.next();
			String strLevel = (String) tempMap.get(DomainConstants.SELECT_LEVEL);
			nLevelCurrent = Integer.parseInt(strLevel);
			nLevelAppend = nLevelCurrent + iLevel;
			if (nLevelCurrent <= nLevelPrev) {
				if (!stackJsonObj.empty()) {
					mlReturn.add(stackJsonObj.pop());
				}
				for (int i = nLevelCurrent; i < nLevelPrev; i++) {
					stackJsonObj.pop();
				}
			}
			stackJsonObj.push(convertTreeMapToFlatMap(stackJsonObj.empty() ? (Map) new HashMap() : stackJsonObj.peek(), tempMap, Integer.toString(nLevelAppend)));
			nLevelPrev = nLevelCurrent;
		}
		if (!stackJsonObj.empty()) {
			mlReturn.add(stackJsonObj.pop());
		}
		return mlReturn;
	}

	public static Map convertTreeMapToFlatMap(Map inputMapObj, Map<?, ?> inputMapTemp, String strLevel) {
		Map mapReturn = new HashMap();
		Map mapInner = new HashMap();
		inputMapObj.entrySet().forEach(e -> mapInner.put(((Entry<?, ?>) e).getKey(), ((Entry<?, ?>) e).getValue()));
		inputMapTemp.entrySet()
				.forEach(e -> mapInner.put((String) e.getKey(),
						((e.getValue() instanceof StringList) ? PGWidgetUtil.getStringFromSL((StringList) e.getValue(), PGWidgetConstants.KEY_PIPE_SEPARATOR)
								: (String) e.getValue())));
		mapReturn.put(strLevel, mapInner);
		return mapReturn;
	}

	public static MapList formatTreeTable(Context context, MapList mlInput, int iStartLevel) throws MatrixException {
		MapList mlReturn = new MapList();
		for (int i = 0; i < mlInput.size(); i++) {
			Map mapTree = new HashMap();
			mapTree.putAll((Map) mlInput.get(i));
			Map mapTreeCopy = new HashMap();
			mapTreeCopy.putAll((Map) mlInput.get(i));
			for (Iterator<?> itr = mapTree.keySet().iterator(); itr.hasNext();) {
				String strKey = (String) itr.next();
				int iCurrentLevel = Integer.parseInt(strKey);
				int iHierLength = (iCurrentLevel - iStartLevel) + 1;
				if (iHierLength > 0) {
					while (iCurrentLevel >= 1) {
						Map<String, Object> mapTreeObj = new HashMap();
						List<String> listHier = new ArrayList();
						strKey = Integer.toString(iCurrentLevel);
						for (int level = iCurrentLevel; level >= 1; level--) {
							String strLevel = Integer.toString(level);
							Map mapObject = new HashMap();
							mapObject.putAll((Map) mapTree.get(strLevel));
							if (mapObject != null) {
								if (level >= iStartLevel) {
									listHier.add(mapObject.get(DomainConstants.SELECT_NAME).toString());
								}
								mapTree = new HashMap();
								mapTree.putAll(mapObject);
								if (mapObject.containsKey(Integer.toString(level - 1))) {
									mapObject.remove(Integer.toString(level - 1));
								}
							}
							mapTreeObj.put(strLevel, mapObject);
						}
						if (!listHier.isEmpty()) {
							Collections.reverse(listHier);
							mapTreeObj.put("hierarchy", listHier);
							mapTreeObj.put("hierarchy-level", strKey);
						}
						mlReturn.add(mapTreeObj);

						mapTree = new HashMap();

						if (mapTreeCopy != null) {
							mapTreeCopy = (Map) mapTreeCopy.get(strKey);
							if (mapTreeCopy != null) {
								mapTree.putAll(mapTreeCopy);
							}
						}
						iCurrentLevel--;
					}
				}
			}
		}
		return mlReturn;
	}

	public static JsonArray formatTreeTableInJson(Context context, MapList mlInput, int iStartLevel) throws MatrixException {
		JsonArrayBuilder jsonArrOut = Json.createArrayBuilder();
		for (int i = 0; i < mlInput.size(); i++) {
			Map mapTree = new HashMap();
			mapTree.putAll((Map) mlInput.get(i));
			Map mapTreeCopy = new HashMap();
			mapTreeCopy.putAll((Map) mlInput.get(i));
			for (Iterator<?> itr = mapTree.keySet().iterator(); itr.hasNext();) {
				String strKey = (String) itr.next();
				int iCurrentLevel = Integer.parseInt(strKey);
				int iHierLength = (iCurrentLevel - iStartLevel) + 1;
				if (iHierLength > 0) {
					while (iCurrentLevel >= 1) {
						JsonObjectBuilder inputBuilder = Json.createObjectBuilder();
						List<String> listHier = new ArrayList();
						strKey = Integer.toString(iCurrentLevel);
						for (int level = iCurrentLevel; level >= 1; level--) {
							String strLevel = Integer.toString(level);
							Map mapObject = new HashMap();
							mapObject.putAll((Map) mapTree.get(strLevel));
							if (mapObject != null) {
								if (level >= iStartLevel) {
									listHier.add(mapObject.get(DomainConstants.SELECT_NAME).toString());
								}
								mapTree = new HashMap();
								mapTree.putAll(mapObject);
								if (strLevel.equals(strKey)) {
									if (mapObject.containsKey(Integer.toString(level - 1))) {
										mapObject.remove(Integer.toString(level - 1));
									}
									putJsonInJsonBuilder(inputBuilder, convertMapToJsonObj(Json.createObjectBuilder().build(), mapObject));
								}
							}
							// jsonObjTree.add(strLevel,
							// convertMapToJsonObj(Json.createObjectBuilder().build(), mapObject));
						}
						if (!listHier.isEmpty()) {
							Collections.reverse(listHier);

							inputBuilder.add("hierarchy", convertArrayToJsonArray(listHier));
							inputBuilder.add("hierarchy-level", strKey);
						}
						jsonArrOut.add(inputBuilder.build());

						mapTree = new HashMap();

						if (mapTreeCopy != null) {
							mapTreeCopy = (Map) mapTreeCopy.get(strKey);
							if (mapTreeCopy != null) {
								mapTree.putAll(mapTreeCopy);
							}
						}
						iCurrentLevel--;
					}
				}
			}
		}
		return jsonArrOut.build();
	}

	public static JsonArray convertArrayToJsonArray(List<String> arr) {
		JsonArrayBuilder jsonArrBldr = Json.createArrayBuilder();
		for (int i = 0; i < arr.size(); i++) {
			jsonArrBldr.add(arr.get(i));
		}
		return jsonArrBldr.build();
	}

	public static void putJsonInJsonBuilder(JsonObjectBuilder inputBuilder, JsonObject inputJsonObj) {
		inputJsonObj.entrySet().forEach(e -> inputBuilder.add(e.getKey(), e.getValue()));
	}

	public static String getGridTreeFormat(Context context, String strInputData) throws Exception {

		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		if (!jsonInputData.containsKey(PGWidgetConstants.KEY_OBJECT_ID)) {
			return (Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, "Bad request").add(PGWidgetConstants.KEY_MESSAGE, "Object id cannot be empty")).toString();
		}
		String strObjectId = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
		String strObjSelects = jsonInputData.containsKey(PGWidgetConstants.OBJ_SELECT) ? jsonInputData.getString(PGWidgetConstants.OBJ_SELECT) : DomainConstants.EMPTY_STRING;
		String strRelSelects = jsonInputData.containsKey(PGWidgetConstants.KEY_RELATIONSHIPSELECTS) ? jsonInputData.getString(PGWidgetConstants.KEY_RELATIONSHIPSELECTS)
				: DomainConstants.EMPTY_STRING;
		String strRelPattern = jsonInputData.containsKey(PGWidgetConstants.KEY_RELPATTERN) ? jsonInputData.getString(PGWidgetConstants.KEY_RELPATTERN)
				: PGWidgetConstants.CONSTANT_STRING_STAR;
		String strTypePattern = jsonInputData.containsKey(PGWidgetConstants.TYPE_PATTERN) ? jsonInputData.getString(PGWidgetConstants.TYPE_PATTERN)
				: PGWidgetConstants.CONSTANT_STRING_STAR;
		String strExpandLevel = jsonInputData.containsKey(PGWidgetConstants.KEY_EXPANDLEVEL) ? jsonInputData.getString(PGWidgetConstants.KEY_EXPANDLEVEL)
				: PGWidgetConstants.CONSTANT_STRING_ZERO;
		String strObjectWhere = jsonInputData.containsKey(PGWidgetConstants.KEY_WHERECONDITION) ? jsonInputData.getString(PGWidgetConstants.KEY_WHERECONDITION)
				: DomainConstants.EMPTY_STRING;
		String strGetTo = jsonInputData.containsKey(PGWidgetConstants.KEY_GETTO) ? jsonInputData.getString(PGWidgetConstants.KEY_GETTO) : PGWidgetConstants.STRING_FALSE;
		String strGetFrom = jsonInputData.containsKey(PGWidgetConstants.KEY_GETFROM) ? jsonInputData.getString(PGWidgetConstants.KEY_GETFROM) : PGWidgetConstants.STRING_TRUE;
		String strLimit = jsonInputData.containsKey(PGWidgetConstants.KEY_LIMIT) ? jsonInputData.getString(PGWidgetConstants.KEY_LIMIT)
				: PGWidgetConstants.CONSTANT_STRING_ZERO;
		String strRelWhere = jsonInputData.containsKey(PGWidgetConstants.KEY_RELWHERECONDITION) ? jsonInputData.getString(PGWidgetConstants.KEY_RELWHERECONDITION)
				: DomainConstants.EMPTY_STRING;

		JsonObjectBuilder inputData = Json.createObjectBuilder();
		inputData.add(PGWidgetConstants.KEY_OBJECT_ID, strObjectId);
		inputData.add(PGWidgetConstants.KEY_RELPATTERN, strRelPattern);
		inputData.add(PGWidgetConstants.TYPE_PATTERN, strTypePattern);
		inputData.add(PGWidgetConstants.KEY_EXPANDLEVEL, strExpandLevel);
		inputData.add(PGWidgetConstants.KEY_WHERECONDITION, strObjectWhere);
		inputData.add(PGWidgetConstants.KEY_GETTO, strGetTo);
		inputData.add(PGWidgetConstants.KEY_GETFROM, strGetFrom);
		inputData.add(PGWidgetConstants.KEY_LIMIT, strLimit);
		inputData.add(PGWidgetConstants.KEY_RELWHERECONDITION, strRelWhere);
		inputData.add(PGWidgetConstants.KEY_RELATIONSHIPSELECTS, strRelSelects);
		inputData.add(PGWidgetConstants.KEY_OBJECT_SELECTS, strObjSelects);

		MapList mlList = PGWidgetUtil.getRelatedObjectsMapList(context, inputData.build());

		MapList mlTree = getTreeStructureMaplist(context, mlList, 0);

		return formatTreeTableInJson(context, mlTree, 1).toString();

	}

	public static String updateGridData(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		Map<String, String> mRelAttrMap = new HashMap<>();
		Map<String, String> mObjAttrMap;
		try {
			JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
			JsonArray jsonInputArray = jsonInputInfo.getJsonArray("updatedData");

			String strExp = "";
			DomainObject doObj = null;
			for (int i = 0; i < jsonInputArray.size(); i++) {
				String strObjId = "";
				String strRelId = "";
				JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);

				if (jsonElement.containsKey(PGWidgetConstants.KEY_REL_ID)) {
					strRelId = jsonElement.getString(PGWidgetConstants.KEY_REL_ID);
				}
				if (jsonElement.containsKey(PGWidgetConstants.KEY_OBJECT_ID)) {
					strObjId = jsonElement.getString(PGWidgetConstants.KEY_OBJECT_ID);
				}
				JsonArray jsonUpdatedArray = jsonElement.getJsonArray("updatedValue");
				mObjAttrMap = new HashMap<>();
				for (int j = 0; j < jsonUpdatedArray.size(); j++) {
					JsonObject jsonDataElement = (JsonObject) jsonUpdatedArray.get(j);
					String strExpression = jsonDataElement.getString(PGWidgetConstants.KEY_EXPR);
					String strValue = jsonDataElement.getString(PGWidgetConstants.KEY_VALUE);
					if (strValue.indexOf(PGWidgetConstants.KEY_COMMA_SEPARATOR) != -1) {
						strValue = FrameworkUtil.findAndReplace(strValue, PGWidgetConstants.KEY_COMMA_SEPARATOR, PGWidgetConstants.KEY_PIPE_SEPARATOR);
					}
					strExp = PGWidgetUtil.getFormattedExpression(strExpression);
					if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
						mObjAttrMap.put(strExp, strValue);
					} else if (UIUtil.isNotNullAndNotEmpty(strRelId)) {
						mRelAttrMap.put(strExp, strValue);
					}
				}
				if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
					doObj = DomainObject.newInstance(context, strObjId);
					doObj.setAttributeValues(context, mObjAttrMap);
				}
				if (UIUtil.isNotNullAndNotEmpty(strRelId)) {
					DomainRelationship.setAttributeValues(context, strRelId, mRelAttrMap);
				}

			}
			if (jsonInputInfo.containsKey(KEY_RETURN_DATA)) {
				return getGridTreeFormat(context, jsonInputInfo.getJsonObject(KEY_RETURN_DATA).toString());
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

	public static String updateObjectOrRel(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		Map<String, String> mRelAttrMap = new HashMap<>();
		Map<String, String> mObjAttrMap;
		try {
			JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
			JsonArray jsonInputArray = jsonInputInfo.getJsonArray("updatedData");

			String strExp = "";
			DomainObject doObj = null;
			for (int i = 0; i < jsonInputArray.size(); i++) {
				String strObjId = "";
				String strRelId = "";
				JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);

				if (jsonElement.containsKey(PGWidgetConstants.KEY_REL_ID)) {
					strRelId = jsonElement.getString(PGWidgetConstants.KEY_REL_ID);
				}
				if (jsonElement.containsKey(PGWidgetConstants.KEY_OBJECT_ID)) {
					strObjId = jsonElement.getString(PGWidgetConstants.KEY_OBJECT_ID);
				}
				mObjAttrMap = new HashMap<>();
				String strExpression = jsonElement.getString(PGWidgetConstants.KEY_EXPR);
				String strValue = jsonElement.getString(PGWidgetConstants.KEY_VALUE);
				strExp = PGWidgetUtil.getFormattedExpression(strExpression);
				if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
					mObjAttrMap.put(strExp, strValue);
				} else if (UIUtil.isNotNullAndNotEmpty(strRelId)) {
					mRelAttrMap.put(strExp, strValue);
				}
				if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
					doObj = DomainObject.newInstance(context, strObjId);
					doObj.setAttributeValues(context, mObjAttrMap);
				}
				if (UIUtil.isNotNullAndNotEmpty(strRelId)) {
					DomainRelationship.setAttributeValues(context, strRelId, mRelAttrMap);
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

	public static String addNewRowToTree(Context context, String strInputData) {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			JsonArray jsonNewData = jsonInputData.getJsonArray("newData");
			if (jsonNewData != null && !jsonNewData.isEmpty()) {
				for (int i = 0; i < jsonNewData.size(); i++) {
					JsonObject jsonElem = jsonNewData.getJsonObject(i);
					addExisting(context, jsonElem.getString("FromObjectId"), jsonElem.getString("ToObjectIds"), jsonElem.getString("RelationshipName"), true);
				}
			}
			if (jsonInputData.containsKey(KEY_RETURN_DATA)) {
				return getGridTreeFormat(context, jsonInputData.getJsonObject(KEY_RETURN_DATA).toString());
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonStatus.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			jsonStatus.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			return jsonStatus.build().toString();
		}
		jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		return jsonStatus.build().toString();

	}

	public static String removeRowsFromTree(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		try {
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;
			JsonArray jsonArrIds = jsonInputData.getJsonArray("RelIds");
			PGWidgetUtil.removeSelected(context, PGWidgetUtil.getStringFromJsonArray(jsonArrIds));
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
				isExceptionOccurred = true;
				sbMessage.append(e.getMessage()).append("\n");
			}
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
			return jsonStatus.build().toString();
		} else {
			if (jsonInputData.containsKey(KEY_RETURN_DATA)) {
				return getGridTreeFormat(context, jsonInputData.getJsonObject(KEY_RETURN_DATA).toString());
			} else {
				jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
				return jsonStatus.build().toString();
			}
		}

	}

	public static String getObjectInfoData(Context context, String strInputData) throws Exception {

		if (UIUtil.isNotNullAndNotEmpty(strInputData)) {

			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strObjectids = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
			String strColumnSelectable = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);

			StringList columnSelectable = StringUtil.split(strColumnSelectable, ",");

			MapList objInfoList = DomainObject.getInfo(context, strObjectids.split(","), columnSelectable);

			JsonArray outputArray = converMaplistToJsonArray(context, objInfoList);
			return outputArray.toString();
		}
		return "";

	}

	public static boolean fileUpload(Context context, String objectID, String fileBase64) throws Exception {
//		String dirpath = "";
		OutputStream outputStream = null;
		try {

			JSONObject json = new JSONObject(fileBase64);
			String fileName = json.getString("fileName");
			String b64 = json.getString("data");

			String[] blob = b64.split("base64,");

			String strWorkspace = context.createWorkspace();
//			dirpath = strWorkspace + "\\" + fileName;

			File outDir = new File(strWorkspace);
			File out = new File(outDir, fileName);
			outputStream = new FileOutputStream(out);
			byte[] decoder = Base64.getDecoder().decode(blob[1].toString().replace("\"", ""));
			outputStream.write(decoder);
			outputStream.close();

			DomainObject domObject = DomainObject.newInstance(context, objectID);
			domObject.checkinFile(context, true, true, DomainConstants.EMPTY_STRING, DomainConstants.FORMAT_GENERIC, fileName, strWorkspace);

//			File file = new File(dirpath);
			Files.delete(outDir.toPath());

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
		return true;
	}
	
	public static String fileUploadCustom(Context context, HttpServletRequest request, String strInput)
			throws Exception {
		try {

			// create doc
			// checkin file
			// connect doc to parent obj by given rel

			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInput);
			String strObjectId = null;
			String strDocObjId = null;
			if (jsonInputData.containsKey(DomainConstants.SELECT_ID)) {
				strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);

				JsonArray jsonFilesArr = jsonInputData.getJsonArray("files");
				ContextUtil.startTransaction(context, true);
				for (int i = 0; i < jsonFilesArr.size(); i++) {
					DomainObject domNew = DomainObject.newInstance(context);
					String strType = DomainConstants.TYPE_DOCUMENT;
					String strPolicy = DomainConstants.POLICY_DOCUMENT;
					String strObjName = FrameworkUtil.autoName(context,
							FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, strType, false), null,
							FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_POLICY, strPolicy, false),
							null, null, true, true);

					domNew.createObject(context, strType, strObjName, "001", strPolicy, "eService Production");
					strDocObjId = domNew.getObjectId(context);

					if (UIUtil.isNotNullAndNotEmpty(strDocObjId) && UIUtil.isNotNullAndNotEmpty(strObjectId)) {
						boolean bUploaded = fileUpload(context, strDocObjId, jsonFilesArr.getJsonObject(i).toString());
						if (bUploaded) {
							domNew.addRelatedObject(context, new RelationshipType(jsonInputData.getString("relType")),
									true, jsonInputData.getString("id"));
						}
					}
				}
			}
			ContextUtil.commitTransaction(context);

			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).add("docId", strDocObjId).build()
					.toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			ContextUtil.abortTransaction(context);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
	}

	/**
	 * This method returns data for specific tab of objects
	 * 
	 * @param context
	 * @param strObjectId
	 * @param strTabName
	 * @return
	 * @throws Exception
	 */
	public static String getImageTabInfo(matrix.db.Context context, Map<String, String> mpRequestMap) throws Exception {

		String strObjectId = mpRequestMap.get("id");
		String strTabName = mpRequestMap.get("tab");
		String strTypePattern = mpRequestMap.get("imageTypePattern");
		String strRelPattern = mpRequestMap.get("imageRelPattern");
		// JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonReturnArr = Json.createArrayBuilder();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strTabName)) {
				// Added for History

				jsonReturnArr.add(getRelatedInfoForImage(context, strObjectId, strTypePattern, strRelPattern));
			}
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return jsonReturnArr.build().toString();
	}

	/**
	 * This method returns the related Claim objects for Image Group in JSON format
	 * 
	 * @param context
	 * @param strObjectId
	 * @return JsonArrayBuilder
	 * @throws MatrixException
	 */
	private static JsonArrayBuilder getRelatedInfoForImage(Context context, String strObjectId, String strTypePattern, String strRelPattern) throws MatrixException {
		JsonArrayBuilder outArr = Json.createArrayBuilder();
		try {
			StringList strObjectIds = StringUtil.split(strObjectId, ",");
			for (int i = 0; i < strObjectIds.size(); i++) {
				MapList mlInfo = getRelatedMediaInfo(context, strObjectIds.get(i), strTypePattern, strRelPattern);

				if (BusinessUtil.isNotNullOrEmpty(mlInfo)) {
					Map mpInfo;
					String strMediaObjectId;
					for (Object objInfo : mlInfo) {
						mpInfo = (Map) objInfo;
						strMediaObjectId = (String) mpInfo.get(DomainConstants.SELECT_ID);
						outArr.add(getResponseForImage(context, mpInfo, strMediaObjectId, strObjectIds.get(i)));
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return outArr;
	}

	/**
	 * This method returns the related Claim Media Info for further processing
	 * 
	 * @param context
	 * @param strObjectId
	 * @return MapList
	 * @throws FrameworkException
	 */
	private static MapList getRelatedMediaInfo(Context context, String strObjectId, String strTypePattern, String strRelPattern) throws FrameworkException {
		MapList mlReturn = new MapList();
		// MapList mlList = null;
		StringList slBusSelects = new StringList(DomainConstants.SELECT_ID);
		// slBusSelects.add(FROM_ACTIVE_VERSION_TO_TITLE);
		// slBusSelects.add(FROM_ACTIVE_VERSION_TO_ID);
		// slBusSelects.addElement(DomainObject.getAttributeSelect(ATTRIBUTE_SEQUENCE));
		// StringList slRelSelects = new StringList(SELECT_CLAIMIMAGE_NAME);
		StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);

		DomainObject domRequest = DomainObject.newInstance(context, strObjectId);
		MapList mlList = domRequest.getRelatedObjects(context,
				// relPattern.getPattern(), //relPattern
				strRelPattern,
				// "*",
				// typePattern.getPattern(), //typePattern
				strTypePattern,
				// "*",
				slBusSelects, // busSelects
				slRelSelects, // relSelects
				true, // getTo
				true, // getFrom
				(short) 0, // recurseToLevel
				null, // busWhere
				null, // relWhere
				0 // limit
		);
		mlReturn.addAll(mlList);
		return mlReturn;
	}

	/**
	 * This method returns the related Claim objects for Image Group in JSON format
	 * 
	 * @param context
	 * @param mpClaimInfo
	 * @param strMediaObjectId
	 * @return JsonObjectBuilder
	 * @throws Exception
	 */
	private static JsonObjectBuilder getResponseForImage(Context context, Map mpClaimInfo, String strMediaObjectId, String strObjectId) throws Exception {
		JsonObjectBuilder jsonObjectBuilder = null;
		String strClaimRequestRelId = (String) mpClaimInfo.get(DomainRelationship.SELECT_ID);
		jsonObjectBuilder = Json.createObjectBuilder();
		jsonObjectBuilder.add(DomainConstants.SELECT_ID, strMediaObjectId);
		jsonObjectBuilder.add("FileID", strObjectId);
		jsonObjectBuilder.add(STRING_IMAGE_DATA, getFileBase64(context, strMediaObjectId));
		// jsonObjectBuilder.add(DomainConstants.SELECT_NAME, (String) objImageTitle);

		return jsonObjectBuilder;
	}

	/**
	 * This method returns the non null value for a Map selectable
	 * 
	 * @param mpClaimInfo
	 * @param strMapSelectable
	 * @return Object
	 * @throws MatrixException
	 */
	public static Object getNonNullValueFromMap(Map mpClaimInfo, String strMapSelectable) throws Exception {
		Object objReturn;
		if (mpClaimInfo.containsKey(strMapSelectable)) {
			objReturn = mpClaimInfo.get(strMapSelectable);
		} else {
			objReturn = DomainConstants.EMPTY_STRING;
		}
		return objReturn;
	}

	/**
	 * This Method Return Claim Media File in base64 format
	 * 
	 * @param context
	 * @param string
	 * @return
	 * @throws MatrixException
	 * @throws IOException
	 * @throws Exception
	 */
	private static String getFileBase64(Context context, String strMediaId) throws MatrixException, IOException {
		String strBase64ImageData = DomainConstants.EMPTY_STRING;
		try {
			DomainObject domObj = DomainObject.newInstance(context, strMediaId);
			String strWorkspace = context.createWorkspace();
			String strFileName = null;
			String filepath = null;
			String fileext = null;
			String genericFormat = PropertyUtil.getSchemaProperty(context, "format_generic");
			FileList listOfFiles = domObj.getFiles(context, genericFormat);
			domObj.checkoutFiles(context, false, genericFormat, listOfFiles, strWorkspace);
			StringList slBase64ImageString = new StringList();
			String strBase64String = DomainConstants.EMPTY_STRING;

			for (int i = 0; i < listOfFiles.size(); i++) {
				strFileName = listOfFiles.get(i).toString();
				filepath = strWorkspace + File.separator + strFileName;
				fileext = filepath.substring(filepath.lastIndexOf(".") + 1);
				if (LIST_FILE_IMAGE_FILE_FORMAT.contains(fileext)) {
					resize(filepath, 250, 250);
					strBase64String = encodeFileToBase64Binary(new File(filepath));
					strBase64String = new StringBuilder("<img src='data:image/").append(fileext).append(";base64,").append(strBase64String).append("'>").toString();
				}
				slBase64ImageString.add(strBase64String);
			}
			if (BusinessUtil.isNotNullOrEmpty(slBase64ImageString)) {
				strBase64ImageData = String.join(CONSTANT_STRING_COMMA, slBase64ImageString);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		} finally {
			context.deleteWorkspace();
		}
		return strBase64ImageData;
	}

	/**
	 * Resizes an image to a absolute width and height (the image may not be
	 * proportional)
	 * 
	 * @param inputImagePath  Path of the original image
	 * @param outputImagePath Path to save the resized image
	 * @param scaledWidth     absolute width in pixels
	 * @param scaledHeight    absolute height in pixels
	 * @throws IOException
	 */
	public static void resize(String inputImagePath, int scaledWidth, int scaledHeight) throws IOException {
		// reads input image
		File inputFile = new File(inputImagePath);
		BufferedImage inputImage = ImageIO.read(inputFile);

		// creates output image
		BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

		// scales the input image to the output image
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
		g2d.dispose();

		// extracts extension of output file
		String formatName = inputImagePath.substring(inputImagePath.lastIndexOf(".") + 1);

		// writes to output file
		ImageIO.write(outputImage, formatName, new File(inputImagePath));
	}

	/**
	 * This method convert file into base64 String
	 * 
	 * @param file
	 * @param fileext
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private static String encodeFileToBase64Binary(File file) throws IOException {
		try (FileInputStream fileInputStreamReader = new FileInputStream(file)) {
			byte[] bytes = new byte[(int) file.length()];
			int size = fileInputStreamReader.read(bytes);
			return new String(Base64.getEncoder().encode(bytes), "UTF-8");
		}
	}

	public static String getDefaultIRMUserPreferences(Context context) throws MatrixException, Exception {

		JsonObjectBuilder jsonData = Json.createObjectBuilder();
		JsonObjectBuilder output = Json.createObjectBuilder();

		jsonData.add(PGWidgetConstants.KEY_IRM_POLICY_VALUES, getPolicies(context));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_USER_PREF_TITLE,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_USER_PREF_TITLE));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_USER_PREF_DESCRIPTION,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_USER_PREF_DESCRIPTION));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_USER_PREF_POLICY,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_USER_PREF_POLICY));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_USER_PREF_BUSINESS_USE_CLASS,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_USER_PREF_BUSINESS_USE_CLASS));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_USER_PREF_HIGHLY_RESTRICTED_CLASS,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_USER_PREF_HIGHLY_RESTRICTED_CLASS));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_USER_PREF_CLASSIFICATION,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_USER_PREF_CLASSIFICATION));
		
		String strPrefBAId = getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMBUSINESSAREA);
		StringList slPrefBAObjIds = new StringList();
		if(UIUtil.isNotNullAndNotEmpty(strPrefBAId)) {
			StringList slPrefBAPhyIds = StringUtil.split(strPrefBAId, PGWidgetConstants.KEY_SEMICOLON_SEPARATOR);
			for(int i=0; i < slPrefBAPhyIds.size(); i++) {
				slPrefBAObjIds.add(convertPhyIdToObjId(context, slPrefBAPhyIds.get(i)));
			}
			strPrefBAId = StringUtil.join(slPrefBAObjIds, PGWidgetConstants.KEY_SEMICOLON_SEPARATOR);
		}
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMBUSINESSAREA,
				strPrefBAId);
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMPREFERREDREGION,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMPREFERREDREGION));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMPREFERREDSHARINGMEMBERS,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMPREFERREDSHARINGMEMBERS));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMPREFERREDROUTEINSTRUCTION,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_PREFERENCE_IRMPREFERREDROUTEINSTRUCTION));
		
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_PREFERENCE_GPS_PREF_POST_TASK_NOTIFY,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_PREFERENCE_GPS_PREF_POST_TASK_NOTIFY));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_PREFERENCE_GPS_PREF_PRE_TASK_NOTIFY,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_PREFERENCE_GPS_PREF_PRE_TASK_NOTIFY));
		jsonData.add(PGWidgetConstants.KEY_DEFAULT_PREFERENCE_GPS_PREF_SHARE_WITH_MEMBERS,
				getUserPreferences(context, PGWidgetConstants.KEY_DEFAULT_PREFERENCE_GPS_PREF_SHARE_WITH_MEMBERS));

		output.add("data", jsonData);
		return output.build().toString();
	}

	public static JsonObject getPolicies(Context context) throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObject = null;
		String strPolicySignatureReferenceDoc = PropertyUtil.getSchemaProperty(context, POLICY_SIGNATURE_REFERENCE_SYMBOLIC);
		String strPolicySelfApproval = PropertyUtil.getSchemaProperty(context, "policy_pgSelfApproval");
		String sLanguage = context.getSession().getLanguage();
		String strSignatureReferencePolicy = i18nNow.getMXI18NString(strPolicySignatureReferenceDoc, DomainConstants.EMPTY_STRING, sLanguage, PGWidgetConstants.KEY_POLICY);
		String strSelfApproval = i18nNow.getMXI18NString(strPolicySelfApproval, DomainConstants.EMPTY_STRING, sLanguage, PGWidgetConstants.KEY_POLICY);

		jsonObject = Json.createObjectBuilder();
		jsonObject.add(PGWidgetConstants.KEY_NAME, strSignatureReferencePolicy);
		jsonObject.add(PGWidgetConstants.KEY_VALUE, strPolicySignatureReferenceDoc);
		jsonArr.add(jsonObject);
		jsonObject.add(PGWidgetConstants.KEY_NAME, strSelfApproval);
		jsonObject.add(PGWidgetConstants.KEY_VALUE, strPolicySelfApproval);
		jsonArr.add(jsonObject);

		output.add("data", jsonArr.build());

		return output.build();
	}

	/**
	 * The method creates the JSON object to get the list of user/person preferences
	 * The input parameters are:
	 * 
	 * @param context The enovia Context object
	 * @return Stringified JSON object consisting of the information to be displayed
	 * @throws Exception When operation fails
	 */
	public static String getUserPreferences(Context context, String preferenceName) throws Exception {
		String strPreferenceValue = MqlUtil.mqlCommand(context, "print person $1 select $2 dump $3",
				new String[] { context.getUser(), "property[" + preferenceName + "].value", "" });
		if (UIUtil.isNullOrEmpty(strPreferenceValue)) {
			strPreferenceValue = "";
		}
		return strPreferenceValue;
	}
	
	public static String connectDisconnectObjects(Context context, String strInput) {
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		try {
			JsonObject jsonInput = getJsonFromJsonString(strInput);
			JsonArray jsonArrData = jsonInput.getJsonArray(PGWidgetConstants.KEY_DATA);
			if (jsonArrData != null && !jsonArrData.isEmpty()) {
				for (int i = 0; i < jsonArrData.size(); i++) {
					JsonObject jsonObj = jsonArrData.getJsonObject(i);
					String strObjectId = jsonObj.getString(DomainConstants.SELECT_ID);
					String strRelName = jsonObj.getString(PGWidgetConstants.KEY_REL_NAME);
					boolean isFrom = true;
					if (jsonObj.containsKey(PGWidgetConstants.KEY_IS_FROM)) {
						isFrom = jsonObj.getBoolean(PGWidgetConstants.KEY_IS_FROM);
					}
					DomainObject domObj = DomainObject.newInstance(context, strObjectId);
					JsonObject jsonObjectRelData = jsonObj.getJsonObject(PGWidgetConstants.KEY_RELATED_DATA);
					Set<String> setKeys = jsonObjectRelData.keySet();
					Iterator<String> itr = setKeys.iterator();
					while (itr.hasNext()) {
						JsonArray jsonArrRelatedData = jsonObj.getJsonObject(PGWidgetConstants.KEY_RELATED_DATA)
								.getJsonArray(itr.next());
						StringList slConnectList = getConnectDisconnectList(jsonArrRelatedData,
								PGWidgetConstants.KEY_CAPITAL_CONNECT);
						StringList slDisconnectList = getConnectDisconnectList(jsonArrRelatedData,
								PGWidgetConstants.KEY_CAPITAL_DISCONNECT);
						if (!slDisconnectList.isEmpty()) {
							for (int j = 0; j < slDisconnectList.size(); j++) {
								domObj.disconnect(context, new RelationshipType(strRelName), isFrom,
										new BusinessObject(slDisconnectList.get(j)));
							}
						}
						if (!slConnectList.isEmpty()) {
							domObj.addRelatedObjects(context, new RelationshipType(strRelName), isFrom,
									slConnectList.toStringArray());
						}
					}
				}
			}
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (Exception e) {
			jsonOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonOutput.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
		}
		return jsonOutput.build().toString();
	}
	
	private static StringList getConnectDisconnectList(JsonArray jsonArrInput, String strKey) throws Exception{
		StringList slReturn = new StringList();
		
		for(int i=0 ; i<jsonArrInput.size() ; i++) {
			JsonObject jsonObj = jsonArrInput.getJsonObject(i);
			if(strKey.equals(jsonObj.getString(PGWidgetConstants.KEY_UPDATE_ACTION))) {
				slReturn.add(jsonObj.getString(DomainConstants.SELECT_ID));
			}
		}
		return slReturn;
	}
	
	public static String getRelatedObjectData(Context context, String strInput) throws FrameworkException {
		JsonArrayBuilder jsonArrOutput = Json.createArrayBuilder();
		boolean isContextPushed = false;
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			MapList mlObjectList = getRelatedObjectsMapList(context, jsonInput);
			for (int i = 0; i < mlObjectList.size(); i++) {
				Map mapTemp = (Map) mlObjectList.get(i);
				String strPhysicalid = FrameworkUtil.getPIDfromOID(context,
						(String) mapTemp.get(DomainConstants.SELECT_ID));
				JsonObjectBuilder jsonDataElem = Json.createObjectBuilder();
				jsonDataElem.add(DomainConstants.SELECT_ID, strPhysicalid)
						.add(PGWidgetConstants.KEY_OBJECT_ID, (String) mapTemp.get(DomainConstants.SELECT_ID))
						.add(DomainConstants.SELECT_TYPE, (String) mapTemp.get(DomainConstants.SELECT_TYPE))
						.add(PGWidgetConstants.KEY_REL_ID,
								(String) mapTemp.get(PGWidgetConstants.SELECT_CONNECTION_ID));

				boolean hasCheckoutAccess = (Boolean
						.valueOf((String) mapTemp.get(CommonDocument.SELECT_HAS_CHECKOUT_ACCESS))).booleanValue();
				boolean hasCheckinAccess = (Boolean
						.valueOf((String) mapTemp.get(CommonDocument.SELECT_HAS_CHECKIN_ACCESS))).booleanValue();
				boolean canCheckin = (hasCheckinAccess && hasCheckoutAccess)
						|| (context.getUser().equalsIgnoreCase((String) mapTemp.get(DomainConstants.SELECT_OWNER))
								&& hasCheckinAccess);
				
				String strHasFiles = PGWidgetConstants.STRING_CAPITAL_FALSE;
				// context pushed for functional requirement, to be able to display connected
				// files. It is duly popped
				ContextUtil.pushContext(context);
				isContextPushed = false;
				CommonDocument comDoc = new CommonDocument((String) mapTemp.get(DomainConstants.SELECT_ID));
				MapList mlFiles = comDoc.getAllFiles(context);
				if (mlFiles != null && !mlFiles.isEmpty()) {
					strHasFiles = PGWidgetConstants.STRING_CAPITAL_TRUE;
				}
				String strTitle = (String) mapTemp.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				jsonDataElem.add(PGWidgetConstants.KEY_DATA_ELEMENTS, Json.createObjectBuilder()
						.add(PGWidgetConstants.SELECT_PHYSICAL_ID, strPhysicalid)
						.add(PGWidgetConstants.KEY_OBJECT_ID, (String) mapTemp.get(DomainConstants.SELECT_ID))
						.add(DomainConstants.SELECT_TYPE, (String) mapTemp.get(DomainConstants.SELECT_TYPE))
						.add(DomainConstants.SELECT_NAME, (String) mapTemp.get(DomainConstants.SELECT_NAME))
						.add(DomainConstants.SELECT_REVISION, (String) mapTemp.get(DomainConstants.SELECT_REVISION))
						.add("title",
								PGWidgetConstants.DENIED.equals(strTitle) ? PGWidgetConstants.KEY_NO_ACCESS : strTitle)
						.add(PGWidgetConstants.KEY_HAS_FILES, strHasFiles)
						.add(PGWidgetConstants.KEY_CAN_CHECKIN, canCheckin));

				jsonArrOutput.add(jsonDataElem.build());
			}
		} catch (Exception e) {
			Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
		}
		finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_INPUTS, jsonArrOutput).build().toString();
	}
	
	public static String getWidgetAppIdFromDisplayName(Context context, String strAppDisplayName) throws FrameworkException {
		String strAppName = null;
		String strWhere = "attribute[" + PGWidgetConstants.ATTRIBUTE_APP_DISPLAY_NAME + "]=='" + strAppDisplayName
				+ "'";

		MapList mlList = DomainObject.findObjects(context, PGWidgetConstants.TYPE_APP_DEFINITION, // type pattern
				PGWidgetConstants.VAULT_ESERVICE_PRODUCTION, // vaultPattern
				strWhere, // where clause
				new StringList(DomainConstants.SELECT_NAME)); // objectSelects

		logger.log(Level.INFO, "mlList :: "+mlList);
		if(mlList != null && !mlList.isEmpty()) {
			Map<String, String> map = (Map) mlList.get(0);
			strAppName = (String) map.get(DomainConstants.SELECT_NAME);
		}
		logger.log(Level.INFO, "strAppName :: "+strAppName);
		return strAppName;
	}
	
	public static String createDomainObject(Context context, String paramString) throws Exception {
		Map<String, String> mapOut = new HashMap<>();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectId = null;
			if (jsonInputData.containsKey(DomainConstants.SELECT_ID)) {
				strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
			}
			String strObjectSelects = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
			JsonObject jsonObjAttributes = jsonInputData.getJsonObject(PGWidgetConstants.KEY_ATTRIBUTES);
			Map mapAttributes = PGWidgetUtil.getMapFromJson(context, jsonObjAttributes);
			JsonArray jsonArrConnections = null;
			if(jsonInputData.containsKey(PGWidgetConstants.KEY_CONNECTIONS)) {
				jsonArrConnections = jsonInputData.getJsonArray(PGWidgetConstants.KEY_CONNECTIONS);
			}
			ContextUtil.startTransaction(context, true);

			DomainObject domNew = DomainObject.newInstance(context);
			if (UIUtil.isNullOrEmpty(strObjectId)) {
				String strType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
				String strPolicy = jsonInputData.getString(DomainConstants.SELECT_POLICY);
				String strObjName = FrameworkUtil.autoName(context, FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, strType, false), null,
						FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_POLICY, strPolicy, false), null, null, true, true);

				domNew.createObject(context, strType, strObjName, "001", strPolicy, "eService Production");
				strObjectId = domNew.getObjectId(context);
			}
			domNew = DomainObject.newInstance(context, strObjectId);
			if (jsonInputData.containsKey(DomainConstants.SELECT_DESCRIPTION)) {
				domNew.setDescription(context, PGWidgetUtil.checkNullValueforString(jsonInputData.getString(DomainConstants.SELECT_DESCRIPTION)));
			}
			if(mapAttributes != null && !mapAttributes.isEmpty()) {
				domNew.setAttributeValues(context, mapAttributes);
			}
			if(jsonArrConnections != null && !jsonArrConnections.isEmpty()) {
				for (int i = 0; i < jsonArrConnections.size(); i++) {
					JsonObject jsonObj = jsonArrConnections.getJsonObject(i);
					if (jsonObj.containsKey("id")) {
						domNew.addRelatedObject(context, new RelationshipType(jsonObj.getString("relType")), jsonObj.getBoolean("isFrom"),
								jsonObj.getString("id"));
					}
				}
			}
			mapOut = domNew.getInfo(context, FrameworkUtil.split(strObjectSelects, PGWidgetConstants.KEY_COMMA_SEPARATOR));
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			ContextUtil.abortTransaction(context);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED).add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build()
					.toString();
		}
		return PGWidgetUtil.getJSONFromMap(context, mapOut).toString();
	}

	public static String getUserPreferenceTemplateData(Context context, String strObjId) throws MatrixException {
		try {
			logger.log(Level.INFO, "strObjId :: {0} " , strObjId);
			IRMUPT domIRMTemplate = new IRMUPT(context, strObjId);
			String strJson = domIRMTemplate.getAttributeJson();
			logger.log(Level.INFO, "strJson :: {0} " , strJson);
			return strJson;
		} catch (Exception e) {
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
	}
	
	public static String getDSMUserPreferenceTemplateData(Context context, String strObjId) throws MatrixException {
		try {
			logger.log(Level.INFO, "strObjId :: {0} " , strObjId);
	        DSMUPT upt = new DSMUPT(context, strObjId);
			String strJson = upt.getAttributeJson();
			logger.log(Level.INFO, "strJson :: {0} " , strJson);
			return strJson;
		} catch (Exception e) {
			logger.log(Level.SEVERE, getExceptionTrace(e));
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED)
					.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
	}
	
	public static void updateUPTPhyIdByInterface(Context context, String strNewObjectId, String strAttrUPTPhyId)
			throws MatrixException {
		boolean isContextPushed = false;
		try {
			Vault vault = context.getVault();
			String strUPTInterface = UserPreferenceTemplateConstants.Interface.INTERFACE_UPT_PHYSICAL_ID_EXTN.getName(context);
			BusinessInterface pgUPTPhysicalIDExtn = new BusinessInterface(strUPTInterface, vault);
			logger.log(Level.INFO, "strUPTInterface :: {}", strUPTInterface);
			DomainObject domObj = DomainObject.newInstance(context, strNewObjectId);

			ContextUtil.pushContext(context);
			isContextPushed = true;

			StringList slInterfaces = DomainObject.newInstance(context, strNewObjectId).getInfoList(context, "interface");	//Getting all interfaces on the Document object
			logger.log(Level.INFO, "slInterfaces :: {}", slInterfaces);
			logger.log(Level.INFO, "slInterfaces.contains(pgUPTPhysicalIDExtn) :: {}", slInterfaces.contains(pgUPTPhysicalIDExtn));
			if ((slInterfaces != null && !slInterfaces.contains(strUPTInterface)) || slInterfaces == null
					|| slInterfaces.isEmpty()) {	//If added interfaces contain the UPT interface or if there are no interfaces present
				logger.log(Level.INFO, "inside if slInterfaces");
				domObj.addBusinessInterface(context, pgUPTPhysicalIDExtn);	//Add the UPT interface on Doc Object
				if (UIUtil.isNotNullAndNotEmpty(strAttrUPTPhyId)) {
					strAttrUPTPhyId = DomainObject.newInstance(context, strAttrUPTPhyId).getPhysicalId(context);//Get the PhysId of UPT
					domObj.setAttributeValue(context, PGWidgetConstants.ATTRIBUTE_PGUPTPHYSICALID, strAttrUPTPhyId);//Set PhysId of UPT on Doc Object
				}
			} else if (UIUtil.isNotNullAndNotEmpty(strAttrUPTPhyId)) {	//This block is for a case when Interface is already present on the Doc, but UPT Id is not set. 
				logger.log(Level.INFO, "inside else if");
				strAttrUPTPhyId = DomainObject.newInstance(context, strAttrUPTPhyId).getPhysicalId(context);//The case is observed while copy/revise Study Protocol
				logger.log(Level.INFO, "strAttrUPTPhyId :: {}", strAttrUPTPhyId);
				domObj.setAttributeValue(context, PGWidgetConstants.ATTRIBUTE_PGUPTPHYSICALID, strAttrUPTPhyId);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new MatrixException(e);
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
			isContextPushed = false;
		}

	}
	
	public static String getRegionFromUPT(Context context, String strUPTPhysId) throws MatrixException {

		try {
			StringList slReturnRegions = new StringList();
			IRMUPT irmUPT = new IRMUPT(context, strUPTPhysId);
			Map mapTemp = irmUPT.getRegion();
			logger.log(Level.INFO, "mapTemp Regions from UPT :: {}", mapTemp);
			if (mapTemp != null && !mapTemp.isEmpty()) {
				MapList mlTemp = (MapList) mapTemp.get("attribute[pgUPTRegion]");
				for (int i = 0; i < mlTemp.size(); i++) {
					slReturnRegions.add((String) ((Map) mlTemp.get(i)).get(DomainConstants.SELECT_NAME));
				}
			}
			return slReturnRegions.join(PGWidgetConstants.KEY_PIPE_SEPARATOR);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return DomainConstants.EMPTY_STRING;
		}
	}
	
	public static String reviseObject(Context context, String strInputData) throws Exception {
		
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		BusinessObject busNewRevObj = new BusinessObject();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		try {
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;
			JsonArray jsonArrIds = jsonInputData.getJsonArray(PGWidgetConstants.KEY_OBJECT_ID);
			for(int i=0; i<jsonArrIds.size(); i++) {
				String strObjectId = jsonArrIds.getString(i);
				DomainObject domObj = DomainObject.newInstance(context,strObjectId);
				String nextRevSeq = domObj.getNextSequence(context);
				busNewRevObj = domObj.revise(context, nextRevSeq, VAULT_PRODUCTION);
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
				isExceptionOccurred = true;
				sbMessage.append(e.getMessage()).append("\n");
			}
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
			return jsonStatus.build().toString();
		} else {
				jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
				return jsonStatus.build().toString();
			}
	}
	
	public static String getPersonRoles(Context context) {
		JsonArrayBuilder jsonArrReturn = Json.createArrayBuilder();
		try {
			String strExcludeRoleList = EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource",
					context.getLocale(), "emxFramework.Range.Project_Role_Exclude_Role_List");
			StringList slProjectRole = FrameworkUtil.getRanges(context, ArtworkConstants.ATTRIBUTE_PROJECT_ROLE);
			StringList slExcludeRoleList = FrameworkUtil.split(strExcludeRoleList, ",");
			for (int i = 0; i < slProjectRole.size(); i++) {
				String strRole = (String) slProjectRole.get(i);
				if (UIUtil.isNotNullAndNotEmpty(strRole) && !slExcludeRoleList.contains(strRole)) {
					JsonObjectBuilder jsonObjBldr = Json.createObjectBuilder();
					jsonObjBldr.add("projectRole", strRole);
					jsonArrReturn.add(jsonObjBldr);
				}
			}
			logger.log(Level.INFO, "jsonArrReturn :: "+jsonArrReturn.build());
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED).add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build()
					.toString();
		}
		return jsonArrReturn.build().toString();
	}

	/*
	 * Returns all the revision of an object
	 */
	public static JsonObject getAllRevision(matrix.db.Context context, String paramString) throws FrameworkException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try{
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);

			StringList slObjectSelects = new StringList();
			slObjectSelects.add(DomainConstants.SELECT_ID);

			String strObjectSelects = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
			String strId = jsonInputData.getString(DomainConstants.SELECT_ID);
			if(UIUtil.isNotNullAndNotEmpty(strObjectSelects)) {
				slObjectSelects.addAll(StringUtil.split(strObjectSelects, PGWidgetConstants.KEY_COMMA_SEPARATOR));
			}

			DomainObject domSATS = DomainObject.newInstance(context,strId);

			MapList mlRevisioninfo = domSATS.getRevisionsInfo(context, slObjectSelects, new StringList());
			mlRevisioninfo.addSortKey(DomainConstants.SELECT_NAME, "ascending", "String");
			mlRevisioninfo.sort();

			JsonObjectBuilder jsonObject = null;
			JsonArrayBuilder jsonArr = Json.createArrayBuilder();
			Map<?, ?> objMap = null;
			String strLanguage = context.getSession().getLanguage();
			String strTypeDisplayName;
			String strKey;
			String strValue;
			for (int i = 0; i < mlRevisioninfo.size(); i++) {
				jsonObject = Json.createObjectBuilder();
				objMap = (Map<?, ?>) mlRevisioninfo.get(i);

				for (Entry<?, ?> entry : objMap.entrySet()) {
					strKey = (String) entry.getKey();						
					strValue = (String) entry.getValue();
					strTypeDisplayName = EnoviaResourceBundle.getTypeI18NString(context, (String) objMap.get(DomainConstants.SELECT_TYPE), strLanguage);
					jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);

					jsonObject.add(strKey, strValue);
				}
				jsonArr.add(jsonObject);
			}
			output.add("data", jsonArr.build());
		} catch (MatrixException ex) {
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, getExceptionTrace(ex));
		}
		return output.build();			
	}
	/**
	 * Method to get all the 'User Preference Templates'
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public String getAllUserPreferenceTemplates(Context context, String strJsonInput) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		String strLoggedInUser = context.getUser();
		
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		String strTemplateType = jsonInputData.getString(PGWidgetConstants.KEY_TEMPLATE_TYPE);
		
		if(PGWidgetConstants.VALUE_DSM.equalsIgnoreCase(strTemplateType)) {
			Map<String, String> mpParamsMap = new HashMap<>();
			mpParamsMap.put(PGWidgetConstants.ATTRIBUTE_UPT_PART_CATEGOTY, PGWidgetConstants.VALUE_CATEGOTY_TS);
			mpParamsMap.put(PGWidgetConstants.ATTRIBUTE_UPT_PART_TYPE, PGWidgetConstants.TYPE_PG_SATS);
			MapList mlATSUPTList = objUserPreferenceTemplateUtil.getUserPreferenceTemplates(context, mpParamsMap);
			
			mpParamsMap.put(PGWidgetConstants.ATTRIBUTE_UPT_PART_TYPE, "");
			MapList mlTSUPTList = objUserPreferenceTemplateUtil.getUserPreferenceTemplates(context, mpParamsMap);
			
			mlATSUPTList.addAll(mlTSUPTList);
			
			updateJsonArrayForDSM(mlATSUPTList, jsonArrObjInfo, strLoggedInUser);
			
		} else if(PGWidgetConstants.VALUE_IRM.equalsIgnoreCase(strTemplateType)) {
			StringList slUserPrefTemplatesList = JPO.invoke(context, PGWidgetConstants.PROG_IRMUPT_CLIENT, null, PGWidgetConstants.METHOD_GET_IRM_USER_PREF_TEMPLATES, null, StringList.class);

			if(slUserPrefTemplatesList != null && !slUserPrefTemplatesList.isEmpty()) {
				StringList slObjSelectList = new StringList();
				slObjSelectList.add(DomainConstants.SELECT_NAME);
				slObjSelectList.add(DomainConstants.SELECT_OWNER);
				slObjSelectList.add(DomainConstants.SELECT_ID);
				
				String[] strOIDArray = slUserPrefTemplatesList.toStringArray();
				MapList mlUserPrefTemplatesInfoList = DomainObject.getInfo(context, strOIDArray, slObjSelectList);
				
				updateJsonArrayForDSM(mlUserPrefTemplatesInfoList, jsonArrObjInfo, strLoggedInUser);
			}
		}
		
		jsonReturnObj.add(PGWidgetConstants.KEY_OUTPUT, jsonArrObjInfo);
		
		return jsonReturnObj.build().toString();
	}

	/**
	 * Method to get all the 'User Preference Templates' for DSM (ATS)
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private void updateJsonArrayForDSM(MapList mlATSUPTList, JsonArrayBuilder jsonArrObjInfo, String strLoggedInUser) {

		Iterator<Map<String, String>> objItr = mlATSUPTList.iterator();
		while (objItr.hasNext()) {
			Map<String, String> mpUserPrefTemplatesMap = objItr.next();

			JsonObjectBuilder jsonUserPrefTempObj = Json.createObjectBuilder();
			String strObjName = mpUserPrefTemplatesMap.get(DomainConstants.SELECT_NAME);
			String strObjID = mpUserPrefTemplatesMap.get(DomainConstants.SELECT_ID);
			String strObjOwner = mpUserPrefTemplatesMap.get(DomainConstants.SELECT_OWNER);

			jsonUserPrefTempObj.add(DomainConstants.SELECT_NAME, strObjName);
			jsonUserPrefTempObj.add(DomainConstants.SELECT_ID, strObjID);

			if (strLoggedInUser.equals(strObjOwner)) {
				jsonUserPrefTempObj.add(PGWidgetConstants.KEY_OWNED, PGWidgetConstants.STRING_CAPITAL_TRUE);
			} else {
				jsonUserPrefTempObj.add(PGWidgetConstants.KEY_OWNED, PGWidgetConstants.STRING_CAPITAL_FALSE);
			}

			jsonArrObjInfo.add(jsonUserPrefTempObj);

		}

	}
 	
	/**
	 * Method to get Pick list from Multi Parents
	 * @param context
	 * @param sParamString
	 * @return
	 * @throws Exception
	 */
	public static JsonObject getPickListForMultiParents(matrix.db.Context context, String sParamString) throws Exception {
		
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(sParamString);			

			String sChildTypes = jsonInputData.getString("childTypes");
			String sParentTypes = jsonInputData.getString("parentTypes");

			StringList slParentTypes = StringUtil.split(sParentTypes, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			JsonObjectBuilder childJsonObjectBuilder = Json.createObjectBuilder();

			StringList slChildTypes = StringUtil.split(sChildTypes, PGWidgetConstants.KEY_COMMA_SEPARATOR);

			Map mapParentObjectList = getParentObjectList(jsonInputData);

			Map<String, String> mapChildPickList = new HashMap();

			if(!slChildTypes.isEmpty() && slChildTypes.size() == 1 && slChildTypes.contains(pgPLConstants.TYPE_PGPLIPRODUCTTECHNOLOGYCHASSIS))
			{
				mapChildPickList = getRelatedPTCList(context, slParentTypes, mapParentObjectList);
				mapChildPickList.entrySet().forEach(e -> childJsonObjectBuilder.add((String) e.getKey(), (String) e.getValue()));
			}		

			output.add("data", childJsonObjectBuilder.build());

		}
		catch (Exception ex)
		{
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, getExceptionTrace(ex));
		}
		return output.build();		
		
	}

	/**
	 * Method to get Related PTC List
	 * @param context 
	 * @param slParentTypes 
	 * @param mapParentObjectList
	 * @return
	 * @throws Exception 
	 */
	public static Map getRelatedPTCList(Context context, StringList slParentTypes, Map mapParentObjectList) throws Exception
	{
		Map<String, String> mapPTCList = new HashMap();
		
		if(null != mapParentObjectList && !mapParentObjectList.isEmpty())
		{
			MapList mlLocalList;
			Set<String> setKeys = mapParentObjectList.keySet();			

			if(null != slParentTypes && !slParentTypes.isEmpty() && slParentTypes.size() > 1 && slParentTypes.size() == setKeys.size())
			{
				String sParentType1 = slParentTypes.get(0);
				String sParentType2 = slParentTypes.get(1);
				
				if(pgPLConstants.TYPE_PGPLIPRODUCTCATEGORYPLATFORM.equalsIgnoreCase(sParentType1) && pgPLConstants.TYPE_PGPLIPRODUCTTECHNOLOGYPLATFORM.equalsIgnoreCase(sParentType2))
				{
					StringList slParent1List = (StringList)mapParentObjectList.get(sParentType1);
					
					StringList slParent2List = (StringList)mapParentObjectList.get(sParentType2);
					

					StringList slUniqueList = getRelatedPickListBasedOnParentNames(context, slParent1List, slParent2List, pgApolloConstants.TYPE_PG_PLI_PRODUCTCATEGORYPLATFORM, pgApolloConstants.TYPE_PG_PLI_PRODUCTTECHNOLOGYPLATFORM);
					

					String sPCPObjectId;
					String sPTPObjectId;
					StringList slParentList;
					String sObjectId;
					String sObjectName;
					Map mapLocal;
					StringList slUniqueObjectIdList = new StringList();
					
					for(String sUnique : slUniqueList)
					{
						slParentList = StringUtil.split(sUnique, pgApolloConstants.CONSTANT_STRING_PIPE);
						
						if(null != slParentList && !slParentList.isEmpty() && slParentList.size() > 1)
						{
							sPCPObjectId = slParentList.get(0);
							sPTPObjectId = slParentList.get(1);
							
							mlLocalList = pgPLUtil.getRelatedPTC(context, sPCPObjectId, sPTPObjectId);
							

							for(Object objMap : mlLocalList)
							{
								mapLocal = (Map)objMap;
								sObjectId = (String)mapLocal.get(DomainConstants.SELECT_ID);
								sObjectName = (String)mapLocal.get(DomainConstants.SELECT_NAME);

								if(UIUtil.isNotNullAndNotEmpty(sObjectId) && !slUniqueObjectIdList.contains(sObjectId))
								{
									slUniqueObjectIdList.add(sObjectId);
									mapPTCList.put(sObjectName, sObjectId);
								}
							}
						}
						
					}
										
				}

			}

		}	
		
		if(!mapPTCList.isEmpty())
		{
			mapPTCList = sortbykey(mapPTCList);
		}
		
		return mapPTCList;
	}
	
	
	/**
	 * Method to get Related PickList based on Parent 1 and Parent 2
	 * @param context
	 * @param sPCPName
	 * @param sPTPName
	 * @return
	 * @throws Exception
	 */
	public static StringList getRelatedPickListBasedOnParentNames(matrix.db.Context context, StringList slParent1List, StringList slParent2List, String sParent1Type, String sParent2Type) throws Exception {
		
		StringList slUniqueList = new StringList();
		
		StringBuilder sbUnique;
		
		Map mapParent1List = getPickListObjectIds(context, slParent1List, sParent1Type);
		
		Map mapParent2List = getPickListObjectIds(context, slParent2List, sParent2Type);
		
		String sParent1ObjectId;
		String sParent2ObjectId;		
		
		for(String sParent1Value : slParent1List)
		{
			sParent1ObjectId = (String)mapParent1List.get(sParent1Value);
			
			if(UIUtil.isNotNullAndNotEmpty(sParent1ObjectId))
			{
				for(String sParent2Value : slParent2List)
				{
					sParent2ObjectId = (String)mapParent2List.get(sParent2Value);
					
					if(UIUtil.isNotNullAndNotEmpty(sParent2ObjectId))
					{					
						sbUnique = new StringBuilder();
						
						sbUnique.append(sParent1ObjectId).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sParent2ObjectId);
						
						slUniqueList.add(sbUnique.toString());
						
					}
				}
			}						
			
		}
		
		Set setUniqueList = new HashSet();
		setUniqueList.addAll(slUniqueList);
		slUniqueList.clear();
		slUniqueList.addAll(setUniqueList);
		
		return slUniqueList;
	}

	/**
	 * Method to get Parent Object List
	 * @param slParentTypes
	 * @param jsonInputData
	 * @return
	 */
	public static Map getParentObjectList(JsonObject jsonInputData)
	{
		Map<String, StringList> mapParentObjectList = new HashMap();
		String sObjectIdList;
		StringList slObjectIdList;
				
		String sParentTypes = jsonInputData.getString("parentTypes");
		
		StringList slParentTypes = StringUtil.split(sParentTypes, PGWidgetConstants.KEY_COMMA_SEPARATOR);
	
		for(String sParentType : slParentTypes)
		{
			sObjectIdList = jsonInputData.getString(sParentType);
			if(UIUtil.isNotNullAndNotEmpty(sObjectIdList))
			{
				slObjectIdList = StringUtil.split(sObjectIdList, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				mapParentObjectList.put(sParentType, slObjectIdList);
			}			
		}
		
		return mapParentObjectList;
	}	
	
	
	/**
	 * Method to get PickList object list based on picklist names and type
	 * @param context
	 * @param slPicklistNames
	 * @param sPickListType
	 * @return
	 * @throws MatrixException
	 */
	public static Map getPickListObjectIds(Context context, StringList slPicklistNames, String sPickListType) throws MatrixException 
	{
		Map mapPickList = new HashMap();
		
		String sPickListObjectId;
		boolean bPickListObjectExists = false;
		BusinessObject boPickList;
		
		for(String sPickLitsName : slPicklistNames)
		{
			boPickList = new BusinessObject(sPickListType ,sPickLitsName,"-",pgApolloConstants.VAULT_ESERVICE_PRODUCTION);
			
			bPickListObjectExists = boPickList.exists(context);
			
			sPickListObjectId = DomainConstants.EMPTY_STRING;
			
			if(bPickListObjectExists)
			{
				sPickListObjectId = boPickList.getObjectId(context);
			}
			
			mapPickList.put(sPickLitsName, sPickListObjectId);
		}		

		return mapPickList;
	}
	
	/**
	 * Method to sort Map by key
	 * @param map
	 */
    public static Map<String, String> sortbykey(Map map)
    {
        TreeMap<String, String> sortedMap = new TreeMap<>();
        sortedMap.putAll(map); 
        for (Map.Entry<String, String> entry : sortedMap.entrySet());
        return sortedMap;
    }
    

	public static String createOrUpdateDataObjects(Context context, String strInputData) throws FrameworkException {
		JSONObject object = new JSONObject(strInputData);
		String key = object.get("key").toString();
		Object valueObject = object.get("value");
		String value = "";
		if (valueObject instanceof String) {
			value = (String) valueObject;
		} else if (valueObject instanceof JSONObject) {
			JSONObject object2 = (JSONObject) valueObject;
			value = object2.toString();
		}
		try {
			MqlUtil.mqlCommand(context, "add dataobject $1 value $2", key, value);
		} catch (Exception e) {
			try {
				MqlUtil.mqlCommand(context, "mod dataobject $1 value $2", key, value);
			} catch (Exception el) {
				return Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, el.getMessage())
						.add(PGWidgetConstants.KEY_TRACE, getExceptionTrace(el)).build().toString();
			}
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build().toString();
	}
    
	public static String fetchDataObject(Context context, String strInputData) throws FrameworkException {
		JSONObject object = new JSONObject(strInputData);
		String key = object.get("key").toString();
		JsonObjectBuilder jsonBldr = Json.createObjectBuilder();
		try {
			List<String> listSelects = new ArrayList();
			listSelects.add("value");
			listSelects.add("description");
			String result = MqlUtil.mqlCommand(context, "print dataobject $1 select value description dump $2", key,
					PGWidgetConstants.KEY_PIPE_SEPARATOR);

			StringList slResult = FrameworkUtil.split(result.trim(), PGWidgetConstants.KEY_PIPE_SEPARATOR);
			
			if (slResult != null && !slResult.isEmpty()) {
				for (int i = 0; i < slResult.size(); i++) {
					jsonBldr.add(listSelects.get(i), slResult.get(i));
				}
			}
		} catch (Exception e) {
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, e.getMessage())
					.add(PGWidgetConstants.KEY_TRACE, getExceptionTrace(e)).build().toString();
		}
		return jsonBldr.build().toString();
	}

	public static String setDataObjectDescription(Context context, String strInputData) {
		JSONObject object = new JSONObject(strInputData);
		String key = object.get("key").toString();
		String value = object.get("value").toString();
		try {
			MqlUtil.mqlCommand(context, "mod dataobject $1 description $2", key, value);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build().toString();
		} catch (Exception el) {
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, el.getMessage())
					.add(PGWidgetConstants.KEY_TRACE, getExceptionTrace(el)).build().toString();
		}
	}
}
