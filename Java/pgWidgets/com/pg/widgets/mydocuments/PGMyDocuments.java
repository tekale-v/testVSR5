package com.pg.widgets.mydocuments;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;

import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.DomainSymbolicConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGMyDocuments {

	private static final String VAULT_ESERVICE_PRODUCTION = PropertyUtil.getSchemaProperty(null, DomainSymbolicConstants.SYMBOLIC_vault_eServiceProduction);
	private static final String VAULT_VPLM = PropertyUtil.getSchemaProperty(null, "vault_vplm");
	private static final String JSON_OUTPUT_KEY_ERROR = "error";
	private static final String JSON_OUTPUT_KEY_TRACE = "trace";
	private static final String MODIFIED = "modified";
	private static final Logger logger = Logger.getLogger(PGMyDocuments.class.getName());

	static final String DENIED = "#DENIED!";
	static final String LAST_WEEK = "LAST WEEK";
	static final String LAST_TWO_WEEKS = "LAST TWO WEEKS";
	static final String LAST_MONTH = "LAST MONTH";
	static final String TIME_FORMAT_1 = "11:59:59 PM";
	static final String TIME_FORMAT_2 = "12:00:00 AM";
	static final String ACCESS_READ = "read";
	static final String ACCESS_MODIFY = "modify";
	static final String ERROR_MYDOCUMENT_GETMYDOCUMENT = "Exception in PGMyDocuments : getMyDocuments";
	static final String EXCEPTION_MESSAGE = "Exception in PGMyDocuments";

	static final String TYPE_PATTERN = "TypePattern";
	static final String NAME_PATTERN = "NamePattern";
	static final String REVISION_PATTERN = "RevisionPattern";
	static final String OBJECT_LIMIT = "ObjectLimit";
	static final String WHERE_EXP = "WhereExpression";
	static final String EXPAND_TYPE = "ExpandType";
	static final String OBJ_SELECT = "ObjectSelects";
	static final String MULTI_VALUE_SELECT = "MultivalueSelects";
	static final String DURATION = "Duration";
	static final String ALLOWED_STATE = "AllowedStates";
	static final String KEY_CLASSIFICATION = "Classification";
	static final String KEY_SECURITY_CATEGORY_CLASSIFICATION = "SecurityCategoryClassification";
	static final String KEY_BUSINESS_AREA = "BusinessArea";
	static final String KEY_DEVELOPMENT_AREA = "DevelopmentArea";
	static final String KEY_FILES = "files";
	static final String KEY_FILES_TO_BE_DELETED = "filesToBeDeleted";
	static final String KEY_CONNECTIONS = "connections";
	static final String DISPLAY_POLICY = "displayPolicy";
	static final String ATTRIBUTE_PGREGION = PropertyUtil.getSchemaProperty(null, "attribute_pgRegion");
	static final String KEY_USERPREFTEMPLATEID = "UserPreftemplateId";
	static final String MULIT_VAL_CHAR = "\u0007";

	/**
	 * The method creates the JSON object to get the list of documents associated
	 * with the user. the input parameters are:
	 * 
	 * @param context The enovia Context object
	 * @param args    the type, name, revision and where clause,allowed state can be
	 *                configured and sent as input to get the desired list of the
	 *                objects to be displayed
	 * @return JSON object consisting of the information to be displayed
	 * @throws Exception When operation fails
	 */
	public static String getMyDocuments(Context context, Map<?, ?> mpParamMAP) throws Exception {

		JsonObjectBuilder output = Json.createObjectBuilder();
		try {

			HashMap<?, ?> programMap = (HashMap<?, ?>) mpParamMAP;
			String strTypePattern = (String) programMap.get(TYPE_PATTERN);
			String strNamePattern = (String) programMap.get(NAME_PATTERN);
			String strRevisionPattern = (String) programMap.get(REVISION_PATTERN);
			String strObjectLimit = (String) programMap.get(OBJECT_LIMIT);
			String strWhereExpression = (String) programMap.get(WHERE_EXP);
			String strExpandType = (String) programMap.get(EXPAND_TYPE);
			String strSelectables = (String) programMap.get(OBJ_SELECT);
			String strMultiSelects = (String) programMap.get(MULTI_VALUE_SELECT);
			String strDuration = (String) programMap.get(DURATION);
			String strAllowedStates = (String) programMap.get(ALLOWED_STATE);

			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_REVISION);
			if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
				objectSelects.addAll(strSelectables.split(","));
			}
			String strWhere = getWhereClause(context, strDuration, strWhereExpression, strAllowedStates);
			Pattern vaultPattern = new Pattern(VAULT_ESERVICE_PRODUCTION);
			vaultPattern.addPattern(VAULT_VPLM);

			MapList mlList = DomainObject.findObjects(context, // context
					getPattern(context, strTypePattern), // String typePattern
					getPattern(context, strNamePattern), // String namePattern
					strRevisionPattern, // String revPattern
					context.getUser(), // String ownerPattern
					vaultPattern.getPattern(), // String vaultPattern
					strWhere, // String whereExpression
					null, // query name to save results
					Boolean.parseBoolean(strExpandType), // true, if the query should find subtypes of the given types
					objectSelects, // StringList objectSelects
					Short.parseShort(strObjectLimit), // short objectLimit
					StringUtil.split(strMultiSelects, PGWidgetConstants.KEY_COMMA_SEPARATOR));

			JsonArrayBuilder jsonArr = Json.createArrayBuilder();

			if (mlList != null && !mlList.isEmpty()) {
				mlList.addSortKey(DomainConstants.SELECT_NAME, "ascending", "String");
				mlList.sort();
				JsonObjectBuilder jsonObject = null;
				String strLanguage = context.getSession().getLanguage();
				Map<?, ?> objMap = null;
				String strKey;
				String strValue;
				String strId;
				String strTypeDisplayName;
				for (int i = 0; i < mlList.size(); i++) {
					jsonObject = Json.createObjectBuilder();
					objMap = (Map<?, ?>) mlList.get(i);
					strId = checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID));
					jsonObject.add(DomainConstants.SELECT_ID, strId);
					strTypeDisplayName = EnoviaResourceBundle.getTypeI18NString(context, (String) objMap.get(DomainConstants.SELECT_TYPE), strLanguage);
					jsonObject.add(DomainConstants.SELECT_TYPE, (String) objMap.get(DomainConstants.SELECT_TYPE));
					jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
					jsonObject.add(DomainConstants.SELECT_POLICY, (String) objMap.get(DomainConstants.SELECT_POLICY));
					jsonObject.add(DISPLAY_POLICY, EnoviaResourceBundle.getAdminI18NString(context, PGWidgetConstants.KEY_POLICY, (String) objMap.get(DomainConstants.SELECT_POLICY), strLanguage));
					jsonObject.add(DomainConstants.SELECT_NAME, checkNullValueforString((String) objMap.get(DomainConstants.SELECT_NAME)));
					jsonObject.add(DomainConstants.SELECT_REVISION, checkNullValueforString((String) objMap.get(DomainConstants.SELECT_REVISION)));
					jsonObject.add(DomainConstants.SELECT_DESCRIPTION, checkNullValueforString((String) objMap.get(DomainConstants.SELECT_DESCRIPTION)));
					jsonObject.add("securityCategoryClassification", checkNullValueforString(PGWidgetUtil.extractMultiValueSelect(objMap, "to[Protected Item].from[IP Control Class].name")));
					

					boolean hasReadAccess = FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strId), ACCESS_READ);
					boolean hasModifyAccess = FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strId), ACCESS_MODIFY);
					jsonObject.add("hasModifyAccess", Boolean.toString(hasModifyAccess));
					for (Entry<?, ?> entry : objMap.entrySet()) {
						strKey = (String) entry.getKey();
						strValue = PGWidgetUtil.extractMultiValueSelect(objMap, strKey);
						strValue = checkNullValueforString(strValue);
						if (!hasReadAccess) {
							if (strValue.equals(DENIED)) {
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
			logger.log(Level.SEVERE, ERROR_MYDOCUMENT_GETMYDOCUMENT, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(JSON_OUTPUT_KEY_TRACE, getExceptionTrace(ex));
		}
		return output.build().toString();
	}
	
	/**
	 * Get Allowed states name and display Names
	 * 
	 * @param context
	 * @param strAllowedStates
	 * @return
	 * @throws MatrixException
	 */
	public static String getAllowedStates(Context context, String strAllowedStates) throws MatrixException {
		String strLanguage = context.getSession().getLanguage();
		JsonObjectBuilder jsonObj = Json.createObjectBuilder();
		if (UIUtil.isNotNullAndNotEmpty(strAllowedStates)) {
			String[] strArrAllowedStates = strAllowedStates.split(",");
			for (int i = 0; i < strArrAllowedStates.length; i++) {
				String strAllowedState = strArrAllowedStates[i];
				String[] strArrPolicyState = strAllowedState.split("\\.");
				String strPolicy = strArrPolicyState[0];
				String strState = strArrPolicyState[1];
				String strStateDisplay = EnoviaResourceBundle.getStateI18NString(context, strPolicy, strState, strLanguage);
				if (UIUtil.isNotNullAndNotEmpty(strAllowedState)) {
					jsonObj.add(strAllowedState, strStateDisplay);
				}
			}
		}
		return jsonObj.build().toString();
	}

	public static String createDocumentAndCheckinFile(Context context, String paramString, HttpServletRequest request) throws Exception {
		Map<String, String> mapOut = new HashMap<>();
		OutputStream outputStream = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectId = null;
			if (jsonInputData.containsKey(DomainConstants.SELECT_ID)) {
				strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
			}
			String strObjectSelects = jsonInputData.getString(OBJ_SELECT);
			String strMultivalueSelects = jsonInputData.getString(MULTI_VALUE_SELECT);
			JsonObject jsonObjAttributes = jsonInputData.getJsonObject("attributes");
			Map mapAttributes = PGWidgetUtil.getMapFromJson(context, jsonObjAttributes);
			JsonArray jsonArrConnections = jsonInputData.getJsonArray(KEY_CONNECTIONS);
			String pgUPIPhysicalId = (String) mapAttributes.get(PGWidgetConstants.ATTRIBUTE_PGUPTPHYSICALID);
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
			if(UIUtil.isNotNullAndNotEmpty(pgUPIPhysicalId)) {
				if(mapAttributes == null || mapAttributes.isEmpty()) mapAttributes = new HashMap();
				mapAttributes.put(ATTRIBUTE_PGREGION, PGWidgetUtil.getRegionFromUPT(context, PGWidgetUtil.convertPhyIdToObjId(context, pgUPIPhysicalId)));
			}
			if (mapAttributes != null && !mapAttributes.isEmpty()) {
				domNew.setAttributeValues(context, mapAttributes);
			}
			if (jsonInputData.containsKey(DomainConstants.SELECT_DESCRIPTION)) {
				domNew.setDescription(context, PGWidgetUtil.checkNullValueforString(jsonInputData.getString(DomainConstants.SELECT_DESCRIPTION)));
			}

			for (int i = 0; i < jsonArrConnections.size(); i++) {
				JsonObject jsonObj = jsonArrConnections.getJsonObject(i);
				if (jsonObj.containsKey("id")) {
					domNew.addRelatedObject(context, new RelationshipType(jsonObj.getString("relType")), jsonObj.getBoolean("isFrom"),
							jsonObj.getString("id"));
				}
			}
			PGWidgetUtil.updateUPTPhyIdByInterface(context, strObjectId, pgUPIPhysicalId);
			mapOut = getDocumentInfo(context, strObjectId, strObjectSelects, strMultivalueSelects);
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			ContextUtil.abortTransaction(context);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED).add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build()
					.toString();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
		return PGWidgetUtil.getJSONFromMap(context, mapOut).toString();

	}

	public static Map getDocumentInfo(Context context, String strObjectId, String strObjectSelects, String strMultivalueSelects) throws MatrixException {

		Map mapReturn = new HashMap();
		String strLanguage = context.getSession().getLanguage();
		DomainObject domObj = DomainObject.newInstance(context, strObjectId);
		mapReturn = domObj.getInfo(context, StringUtil.split(strObjectSelects, PGWidgetConstants.KEY_COMMA_SEPARATOR), StringUtil.split(strMultivalueSelects, PGWidgetConstants.KEY_COMMA_SEPARATOR));
		mapReturn.put(DISPLAY_POLICY, EnoviaResourceBundle.getAdminI18NString(context, PGWidgetConstants.KEY_POLICY, (String) mapReturn.get(DomainConstants.SELECT_POLICY), strLanguage));
		mapReturn.put(DomainConstants.SELECT_ID, strObjectId);
		
		mapReturn.put("securityCategoryClassification", checkNullValueforString(PGWidgetUtil.extractMultiValueSelect(mapReturn, "to[Protected Item].from[IP Control Class].name")));
		
		boolean hasModifyAccess = FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strObjectId), ACCESS_MODIFY);
		mapReturn.put("hasModifyAccess", Boolean.toString(hasModifyAccess));
		
		return mapReturn;

	}

	public static String editDocumentWithFile(Context context, String paramString, HttpServletRequest request) throws Exception {
		Map<String, String> mapOut = new HashMap<>();
		OutputStream outputStream = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				String strObjectSelects = jsonInputData.getString(OBJ_SELECT);
				String strMultivalueSelects = jsonInputData.getString(MULTI_VALUE_SELECT);
				JsonObject jsonObjAttributes = jsonInputData.getJsonObject("attributes");
				Map mapAttributes = PGWidgetUtil.getMapFromJson(context, jsonObjAttributes);
				JsonArray jsonArrConnections = jsonInputData.getJsonArray(KEY_CONNECTIONS);
				
				ContextUtil.startTransaction(context, true);

				DomainObject domNew = DomainObject.newInstance(context, strObjectId);
				if (mapAttributes != null && !mapAttributes.isEmpty()) {
					domNew.setAttributeValues(context, mapAttributes);
				}
				if (jsonInputData.containsKey(DomainConstants.SELECT_DESCRIPTION)) {
					domNew.setDescription(context, PGWidgetUtil.checkNullValueforString(jsonInputData.getString(DomainConstants.SELECT_DESCRIPTION)));
				}
				StringList slSelects = new StringList();
				slSelects.add(DomainConstants.SELECT_TYPE);
				slSelects.add(DomainConstants.SELECT_POLICY);
				for (int i = 0; i < jsonArrConnections.size(); i++) {
					JsonObject jsonObj = jsonArrConnections.getJsonObject(i);
					String strRelType = jsonObj.getString("relType");
					String strConnectionId = domNew.getInfo(context, "from["+strRelType+"].id");
					DomainRelationship.disconnect(context, strConnectionId);
					domNew.addRelatedObject(context, new RelationshipType(strRelType), jsonObj.getBoolean("isFrom"),
							jsonObj.getString("id"));
				}
				if(jsonInputData.containsKey(KEY_FILES_TO_BE_DELETED)) {
					JsonArray jsonArrFilesToBeDeleted = jsonInputData.getJsonArray(KEY_FILES_TO_BE_DELETED);
					for (int i = 0; i < jsonArrFilesToBeDeleted.size(); i++) {
						String strFileId = jsonArrFilesToBeDeleted.getJsonObject(i).getString("id");
						CommonDocument comDoc = new CommonDocument(strFileId);
						comDoc.deleteObject(context);
					}
				}
				mapOut = getDocumentInfo(context, strObjectId, strObjectSelects, strMultivalueSelects);
				ContextUtil.commitTransaction(context);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			ContextUtil.abortTransaction(context);
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED).add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build()
					.toString();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
		return PGWidgetUtil.getJSONFromMap(context, mapOut).toString();

	}

	public static String deleteObjects(Context context, String strObjectIds) throws Exception {
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
				return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED).add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build()
						.toString();
			}
		}
		return Json.createObjectBuilder().add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS).build().toString();
	}

	/**
	 * Check value is null or Empty and if MultiValue character(\u0007) present then
	 * replace with comma
	 * 
	 * @param strString
	 * @return
	 */
	private static String checkNullValueforString(String strString) {
		if (strString != null && strString.contains(MULIT_VAL_CHAR)) {
			strString = strString.replaceAll(MULIT_VAL_CHAR, " , ");
		}
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}

	/**
	 * Get Type Pattern for all Schema
	 * 
	 * @param context
	 * @param strSelectedSchema
	 * @return
	 */
	private static String getPattern(Context context, String strSelectedSchema) {

		if (strSelectedSchema.equals("*")) {
			return strSelectedSchema;
		}

		StringList slSchemaList = FrameworkUtil.split(strSelectedSchema, ",");
		StringBuilder sbSchemaPattern = new StringBuilder();

		for (int iter = 0; iter < slSchemaList.size(); iter++) {
			String strSchema = slSchemaList.get(iter);
			String strSymSchema = PropertyUtil.getSchemaProperty(context, strSchema);
			if (UIUtil.isNullOrEmpty(strSymSchema)) {
				strSymSchema = strSchema;
			}
			sbSchemaPattern.append(strSymSchema);
			sbSchemaPattern.append(",");
		}
		String strResult = sbSchemaPattern.toString();
		if (UIUtil.isNotNullAndNotEmpty(strResult)) {
			strResult = strResult.substring(0, strResult.length() - 1);
		}
		return strResult;

	}

	/**
	 * Build where expression for Allowed States
	 * 
	 * @param strAllowedStates
	 * @return
	 */
	private static StringBuilder buildWhereExpAllowStates(String strAllowedStates) {
		StringBuilder sbObjectWhere = new StringBuilder();
		sbObjectWhere.append("(");
		StringList slallowedStates = FrameworkUtil.split(strAllowedStates, ",");
		int iAllowedStatesCount = slallowedStates.size();
		for (int i = 0; i < iAllowedStatesCount; i++) {
			StringList policyStateInfo = FrameworkUtil.split(slallowedStates.get(i), ".");
			sbObjectWhere.append(DomainConstants.SELECT_CURRENT).append(" == '").append(policyStateInfo.get(1)).append("' || ");
		}
		if (iAllowedStatesCount > 0) {
			sbObjectWhere.delete(sbObjectWhere.lastIndexOf("||"), sbObjectWhere.length());
		}
		sbObjectWhere.append(")");
		return sbObjectWhere;
	}

	/**
	 * Build where condition to filter the data
	 * 
	 * @param context
	 * @param strReleasedInDuration
	 * @return
	 * @throws Exception
	 */
	private static String getWhereClause(Context context, String strDuration, String whereClause, String strAllowedStates) throws Exception {
		StringBuilder sbWhereClause = new StringBuilder();

		if (UIUtil.isNotNullAndNotEmpty(whereClause)) {
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
		double clientTZOffset = (new Double(dbMilisecondsOffset / (1000 * 60 * 60))).doubleValue();

		String strCurrentDate = dtFormat.format(new Date());
		strCurrentDate = eMatrixDateFormat.getFormattedInputDateTime(context, strCurrentDate, TIME_FORMAT_1, clientTZOffset, Locale.US);
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (LAST_WEEK.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -7);
		} else if (LAST_TWO_WEEKS.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -14);
		} else if (LAST_MONTH.equalsIgnoreCase(strDuration)) {
			cal.add(Calendar.DATE, -30);
		}

		Date start = cal.getTime();
		String strFromDate = dtFormat.format(start);
		strFromDate = eMatrixDateFormat.getFormattedInputDateTime(context, strFromDate, TIME_FORMAT_2, clientTZOffset, Locale.US);

		sbBuildQuery.append("((");
		sbBuildQuery.append(MODIFIED).append(" >= ");
		sbBuildQuery.append("'" + strFromDate + "'");
		sbBuildQuery.append(")");
		sbBuildQuery.append(" && ");
		sbBuildQuery.append("(");
		sbBuildQuery.append(MODIFIED).append(" <= ");
		sbBuildQuery.append("'" + strCurrentDate + "'");
		sbBuildQuery.append("))");
		return sbBuildQuery.toString();
	}

	/**
	 * Stack trace to string
	 * 
	 * @param ex
	 * @return
	 */
	private static String getExceptionTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
//		ex.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}

}
