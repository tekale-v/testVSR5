/*
 * PGReportGeneratorUtil.java
 * 
 * Added by Dashboard Team
 * For Report Generator Widget related Web services
 * 
 */

package com.pg.widgets.reportgenerator;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import com.dassault_systemes.catrgn.reportNav.modeler.interfaces.CATRgnIReportNav;
import com.dassault_systemes.catrgn.reportNav.modeler.interfaces.CATRgnIReportNav.ReportNavException;
import com.dassault_systemes.iPLMDictionaryPublicItf.IPLMDictionaryPublicClassItf;
import com.dassault_systemes.iPLMDictionaryPublicItf.IPLMDictionaryPublicFactory;
import com.dassault_systemes.iPLMDictionaryPublicItf.IPLMDictionaryPublicItf;
import com.dassault_systemes.vplm.commit.CommitReport;
import com.dassault_systemes.vplm.data.service.PLMIDAnalyser;
import com.dassault_systemes.vplm.dictionary.PLMDictionaryServices;
import com.dassault_systemes.vplm.modeler.PLMCoreAbstractModeler;
import com.dassault_systemes.vplm.modeler.PLMCoreModelerSession;
import com.dassault_systemes.vplm.modeler.PLMxTemplateFactory;
import com.dassault_systemes.vplm.modeler.entity.PLMxReferenceEntity;
import com.dassault_systemes.vplm.modeler.template.IPLMTemplateContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.productline.UnifiedAutonamingServices;
import com.matrixone.jdom.Document;
import com.matrixone.jdom.Element;
import com.matrixone.jdom.input.SAXBuilder;
import com.matrixone.jdom.xpath.XPath;

import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;
import com.matrixone.apps.domain.Job;

import matrix.db.PolicyList;
import matrix.db.Policy;
import matrix.db.StateRequirementList;
import matrix.db.StateRequirement;
import matrix.db.BusinessType;
import com.matrixone.apps.domain.util.PersonUtil;
import matrix.db.Vault;

/**
 * Class PGReportGeneratorUtil has all the web service methods required for
 * report generator widget
 * 
 * @since 2018x.5
 * @author
 *
 */

@SuppressWarnings("deprecation")
class PGReportGeneratorUtil {
	
	static final String TYPE_CAT_REPORT_REFERENCE = PropertyUtil.getSchemaProperty(null, "type_CATReportReference");
	static final String POLICY_VPLM_SMB_DEFINITION = PropertyUtil.getSchemaProperty(null, "policy_VPLM_SMB_Definition");
	
	static final String KEY_INPUT = "InputFormData";
	static final String KEY_INPUT_VALUES = "InputFormDataValues";
	static final String KEY_OUTPUT = "OutputAvailableColumns";
	static final String KEY_BASIC_FORM_COLUMNS = "ReportFormColumns";
	static final String KEY_DEFAULT_REVISION = "001.001";
	
	static final String ATTRIBUTE_PROGRAM_ARGS = PropertyUtil.getSchemaProperty(null, "attribute_ProgramArguments");
	static final String ATTRIBUTE_REPORT_OUTPUT_DATA = PropertyUtil.getSchemaProperty(null,
			"attribute_pgReportOutputData");
	static final String SELECT_ATTRIBUTE_REPORT_OUTPUT_DATA = DomainObject
			.getAttributeSelect(ATTRIBUTE_REPORT_OUTPUT_DATA);
	static final String ATTRIBUTE_REPORT_FORM_DATA = PropertyUtil.getSchemaProperty(null, "attribute_pgReportFormData");
	static final String SELECT_ATTRIBUTE_REPORT_FORM_DATA = DomainObject.getAttributeSelect(ATTRIBUTE_REPORT_FORM_DATA);
	static final String ATTRIBUTE_V_NAME = PropertyUtil.getSchemaProperty(null, "attribute_PLMEntity.V_Name");
	static final String SELECT_ATTRIBUTE_V_NAME = DomainObject.getAttributeSelect(ATTRIBUTE_V_NAME);
	static final String ATTRIBUTE_REPORT_QUERY = PropertyUtil.getSchemaProperty(null, "attribute_pgReportQuery");
	static final String SELECT_ATTRIBUTE_REPORT_QUERY = DomainObject.getAttributeSelect(ATTRIBUTE_REPORT_QUERY);
	static final String ATTRIBUTE_REPORT_INPUT_INFORMATION = "ReportInputInformation";
	static final String SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION = DomainObject
			.getAttributeSelect(ATTRIBUTE_REPORT_INPUT_INFORMATION);
	static final String ATTRIBUTE_COMPLETION_STATUS = PropertyUtil.getSchemaProperty(null,"attribute_CompletionStatus");
	static final String SELECT_ATTRIBUTE_COMPLETION_STATUS = DomainObject
			.getAttributeSelect(ATTRIBUTE_COMPLETION_STATUS);
	static final String ATTRIBUTE_ERROR_MESSAGE = PropertyUtil.getSchemaProperty(null,"attribute_ErrorMessage");
	static final String SELECT_ATTRIBUTE_ERROR_MESSAGE = DomainObject
			.getAttributeSelect(ATTRIBUTE_ERROR_MESSAGE);
	
	static final String TYPE_REPORT_MODEL = PropertyUtil.getSchemaProperty(null, "type_ReportModel");
	static final String NAME_REPORT_MODEL = "Report_Generator_Widget_Model";
	static final String TYPE_REPORT_TEMPLATE = PropertyUtil.getSchemaProperty(null, "type_ReportTemplate");
	static final String NAME_REPORT_TEMPLATE = "Report_Generator_Widget_Output";
	static final String REVISION_MODEL_TEMPLATE = "1";
	
	static final String SELECT_PHYSICAL_ID = "physicalid";
	static final String KEY_OUTPUT_ID = "outputFormatId";
	static final String KEY_TEMPLATE_ID = "templateId";
	static final String KEY_TARGET_DOCUMENT_ID = "targetDocumentId";
	static final String KEY_PARAMETERS = "inputParameters";
	
	static final String REVISION_LATEST = "latest";
	static final String REVISION_LAST = "last";
	static final String WILD_CARD_ASTERESK = "*";
	
	static final String KEY_MQL_QUERY = "MQLQuery";
	static final String KEY_MQL_QUERY_PARAMETERS = "MQLQueryParams";
	static final String KEY_CUSTOM_COLUMN_NAME = "CustomColumnNames";
	
	static final String REPORT_PREFIX = "Report";
	
	static final String KEY_TEMPLATE_NAME = "TemplateName";
	static final String KEY_REPORT_TITLE = "ReportTitle";
	static final String KEY_TARGET_DOCUMENT = "TargetDocument";
	static final String KEY_INPUT_QUERY_DATA = "InputQueryData";
	static final String KEY_REPORT_OUTPUT_COLUMN = "ReportOutputColumn";
	static final String TYPE_REPORT_DATA = "RGNReportData";
	static final String RELATIONSHIP_REPORT_DATA = "RGNReportDataRelation";
	static final String KEY_DATA_OUTPUT_COLUMN = "dataOutputColumn";
	static final String KEY_CAT_REPORT = "CATReport";
	static final String CLASS_CAT_RGN_REPORT_NAV = "com.dassault_systemes.catrgn.reportNav.modeler.implementation.CATRgnReportNav";
	
	static final String KEY_V_NAME = "V_Name";
	static final String KEY_V_DESCRIPTION = "V_description";
	static final String KEY_PLM_EXTERNAL_ID = "PLM_ExternalID";
	static final String KEY_PIPE_SEPARATOR  = "|";
	static final String KEY_ESCAPED_PIPE_SEPARATOR  = "\\|";
	static final String DOCUMENT_INFO = "DocumentInfo";
	static final String KEY_TEMPLATE_INFO = "TemplateInfo";
	static final String KEY_TEMPLATE = "Template";
	static final String KEY_TITLE = "title";
	static final String FILE_NAME = "FileName";
	static final String KEY_JOB_STATUS = "jobStatus";
	static final String KEY_JOB_COMPLETION_STATUS = "jobCompletionStatus";
	static final String KEY_JOB_ERROR_MESSAGE = "jobErrorMessage";	
	static final String KEY_JOB_ID = "jobId";
	static final String SEPARATOR_NEW_LINE = "\n";
	static final String KEY_ACTUAL_QUERY = "CompleteQuery";
	static final String BAKGROUND_JOB_PROGRAM = "PGReportGeneratorJob";
	static final String BAKGROUND_JOB_METHOD = "generateReportAsBackgroundJob";
	static final String BAKGROUND_JOB_TITLE = "Report Generator Widget Background Job";
	static final String KEY_ALLOW_REEXECUTION = "No";
	static final String KEY_RESTART_POINT = "Created";
	static final String KEY_NOTIFY_OWNER = "No";
	static final String EXPR_SEPARATOR = "<@SEP@>";
	static final String VALUE_KILOBYTES = "262144";
	static final String KEY_STATUS = "Status";
	static final String KEY_ERROR = "error";
	static final String VALUE_FAILED = "failed";
	static final String VALUE_SUCCESS = "success";
	static final String VALUE_TRUE = "true";
	static final String VALUE_ERROR_MESSAGE = "Report does not exist!";
	static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	static final String MQL_DUMP_FORMAT = "json";
	static final String KEY_EXPR = "expr";
	static final String KEY_EXPR_SELECT = "exprSelect";
	static final String KEY_LABEL = "label";
	static final String KEY_VALUE = "value";
	static final String KEY_NAME = "name";
	static final String KEY_OPTIONS = "options";
	static final String MQL_TEMP_QUERY_SELECT = "temp query bus ";
	static final String JOB_SUCCEEDED = "Succeeded";
	static final String MQL_QUERY_LIMIT = "10000";
	static final String SELECT_STATE_COMPLETED = "Completed";
	static final String SELECT_STATE_ARCHIVED = "Archived";
	static final String VALUE_MSG_JOB_RUNNING = "Previous Job is still running...";
	static final String SELECT_ADMIN_TYPE = "Type";
	static final String PREFIX_EMX_FRAMEWORK = "emxFramework";
	static final String KEY_DEFAULT_TYPES = "defaultTypes";
	static final String KEY_DEFAULT_STATES = "defaultStates";
	String strExceptionMessage = "Exception occurred during the operation, please check the logs !!!";
	String strExceptionMessageDocNotExist = "Report Document object associated with the Report is not exist or might be deleted, please create the new Report !!!";
	
	static final String KEY_DATE = "date";
	static final String KEY_FROM = "from";
	static final String KEY_TO = "to";
	static final String OPERATOR_FROM_DATE = ">=";
	static final String OPERATOR_TO_DATE = "<=";
	static final String WIDGET_DATE_FORMAT= "yyyy-MM-dd";
	
	static final String KEY_REPORT_INPUT_ARRAY = "ReportInputArray";
	static final String KEY_EXPAND_INFO = "ExpandInfo";
	static final String KEY_REL_NAME = "RelName";
	static final String KEY_REL_DIRECTION = "RelDirection";
	static final String MQL_EXPAND_QUERY_SELECT = "expand bus ";
	static final String KEY_MATCH_LIST = "matchlist";
	static final String KEY_RECURSE_LEVEL = "recurse";
	static final String VALUE_RECURSE_ALL = "-1"; //Get Only leaf level objects
	static final String VALUE_RECURSE_ONE = "1";
	static final String KEY_LEVEL = "level";
	static final String KEY_QUERY_TYPE = "QueryType";
	static final String VALUE_QUERY_TYPE_PRINT = "print";
	static final String VALUE_QUERY_TYPE_EXPAND = "expand";
	static final String KEY_QUERY = "query";
	static final String KEY_HIDE_OUTPUT = "HideOutputColumns";
	static final String KEY_REL_SELECT = "relselect";
	static final String KEY_BUS_SELECT = "busselect";
	static final String PARAM_BUS_SELECT = " select bus ";
	static final String PARAM_REL_SELECT = " select rel ";
	static final String KEY_WHERE_CLAUSE = "whereClause";
	String strReportCreationErrorMsg = "Report Creation Failed. Since there is no expand in first level the Type and Name fields sholud not be blank.";
	
	static final String REPORT_DOC_PREFIX= "ReportDoc";
	static final String TYPE_REPORT_DOCUMENT = PropertyUtil.getSchemaProperty(null, "type_ReportDocument");
	static final String POLICY_REPORT_DOCUMENT = PropertyUtil.getSchemaProperty(null, "policy_ReportDocument");
	static final String DEFAULT_REVISION_REPORT_DOCUMENT = "1";
	static final String KEY_SHOW_PG_CUSTOM_COLUMNS = "showPGCustomColumns";
	static final String KEY_OUTPUT_COLUMNS = "outputColumns";
	static final String KEY_RELMID = "relmid";
	static final String KEY_SELECTS = "selects";
	static final String KEY_SELECTS_LABEL = "selectsLabel";
	static final String KEY_TO_MID = "tomid";
	static final String KEY_FROM_MID = "frommid";

	/**
	 * Method to get the information of Report Template object, specifically input
	 * and out put attribute information
	 * 
	 * @param context        : Context eMatrix context object
	 * @param strType        : String type "Report Model"
	 * @param strName        : String name of the object of type "Report Model"
	 * @param strRevision    : String revision of the object of type "Report Model"
	 * @param strShowAllCols : String with value true or false to decide whether to
	 *                       show custom columns from page object
	 * @return : String of Json object with "Report Model" object info
	 * @throws Exception
	 */
	String getReportTemplateData(Context context, String strType, String strName, String strRevision,
			String strShowAllCols,String strTemplateId) throws Exception {

		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		try {
			if(UIUtil.isNullOrEmpty(strTemplateId))
			{
				strTemplateId = getObjectId(context, strType, strName, strRevision);				
			}			
			if (UIUtil.isNotNullAndNotEmpty(strTemplateId)) {
				DomainObject dobReportModel = DomainObject.newInstance(context, strTemplateId);
				StringList slObjectSelects = new StringList(2);
				slObjectSelects.add(SELECT_ATTRIBUTE_REPORT_FORM_DATA);
				slObjectSelects.add(SELECT_ATTRIBUTE_REPORT_OUTPUT_DATA);
				slObjectSelects.add(DomainConstants.SELECT_NAME);				

				Map<?, ?> objectInfo = dobReportModel.getInfo(context, slObjectSelects);
				String strInputFormData = (String) objectInfo.get(SELECT_ATTRIBUTE_REPORT_FORM_DATA);

				if (UIUtil.isNotNullAndNotEmpty(strInputFormData)) {
					String strUpdatedInputFormData = updateInputFormDataForTypes(context, strInputFormData);
					jsonObjOutput.add(KEY_INPUT, strUpdatedInputFormData);
				}
				
				jsonObjOutput.add(KEY_TEMPLATE_NAME, (String) objectInfo.get(DomainConstants.SELECT_NAME));

				String strAttrValue = (String) objectInfo.get(SELECT_ATTRIBUTE_REPORT_OUTPUT_DATA);
				JsonObject jsonOutputColumnsObj = getJsonFromJsonString(strAttrValue);
				String strShowCustomColunms = jsonOutputColumnsObj.getString(KEY_SHOW_PG_CUSTOM_COLUMNS);
				JsonArray jsonOutputColumnsArray = jsonOutputColumnsObj.getJsonArray(KEY_OUTPUT_COLUMNS);
				jsonObjOutput.add(KEY_BASIC_FORM_COLUMNS, jsonOutputColumnsArray.toString());
				
				String strOutputColumns = "";
				if (VALUE_TRUE.equalsIgnoreCase(strShowCustomColunms)) {
					strOutputColumns = getSystemColumns(context);
				}

				if (UIUtil.isNotNullAndNotEmpty(strOutputColumns)) {
					jsonObjOutput.add(KEY_OUTPUT, strOutputColumns);
				}

			}
			return jsonObjOutput.build().toString();
		} catch (Exception e) {
			jsonObjOutput.add(KEY_STATUS, VALUE_FAILED);
			if (UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(KEY_ERROR, strExceptionMessage);
			throw e;
		}
	}	
	
	/**
	 * Method to update the Types actual and display names in input form data
	 * @param context : Context eMatrix context object
	 * @param strInputFormData : String json with information of form input data from Template
	 * @return : String updated json with types actual and display names
	 * @throws Exception 
	 */
	String updateInputFormDataForTypes(Context context, String strInputFormData) throws Exception {
		
		JsonArrayBuilder jsonUpdatedFormDataArray = Json.createArrayBuilder();
		StringBuilder sbInputFormBuilder = new StringBuilder();
		sbInputFormBuilder.append("{\"").append(KEY_INPUT).append("\" : ").append(strInputFormData).append("}");
		
		JsonObject jsonInputFormObj = getJsonFromJsonString(sbInputFormBuilder.toString());
		JsonArray jsonFormDataArray = jsonInputFormObj.getJsonArray(KEY_INPUT);
		
		for(int j=0; j<jsonFormDataArray.size();j++) {
			
			JsonArrayBuilder jsonInputFormDataArray = Json.createArrayBuilder();
			JsonObject jsonCurrentLevelObj = jsonFormDataArray.getJsonObject(j);
			JsonArray jsonInputFormArray = jsonCurrentLevelObj.getJsonArray(KEY_INPUT_QUERY_DATA);
			
			JsonObjectBuilder jsonStateOptions = Json.createObjectBuilder();
			JsonObjectBuilder jsonStateDefaultValue = Json.createObjectBuilder();
			JsonObject jsonTypeElement = getJsonObjectForTypeField(jsonInputFormArray);
			JsonObject jsonTypeObj = updateTypeInputField(context, jsonTypeElement, jsonStateOptions, jsonStateDefaultValue); 
			jsonInputFormDataArray.add(jsonTypeObj);
			
			JsonArray jsonDefaultStateArray = getJsonArrayForState(jsonStateDefaultValue.build());
			
			for(int i=1; i<jsonInputFormArray.size(); i++) {
				JsonObject jsonInputElmObj = jsonInputFormArray.getJsonObject(i);
				String strFieldName = jsonInputElmObj.getString(DomainConstants.SELECT_NAME);
				if(DomainConstants.SELECT_CURRENT.equals(strFieldName)) {
					JsonObjectBuilder jsonObjBuild = getJsonObjBuilderFromJson(jsonInputElmObj);
					jsonObjBuild.add(KEY_DEFAULT_STATES, jsonStateOptions.build());
					jsonObjBuild.add(KEY_OPTIONS, jsonDefaultStateArray);
					jsonInputFormDataArray.add(jsonObjBuild.build());
				} else {
					jsonInputFormDataArray.add(jsonInputElmObj);
				}
			}
			
			JsonObjectBuilder jsonObjBuild = getJsonObjBuilderFromJson(jsonCurrentLevelObj);
			jsonObjBuild.add(KEY_INPUT_QUERY_DATA, jsonInputFormDataArray);
			jsonUpdatedFormDataArray.add(jsonObjBuild);
		}		
		return jsonUpdatedFormDataArray.build().toString();
	}

	/**
	 * Method to get the Json object for Type input field
	 * @param jsonInputFormArray
	 * @return
	 */
	JsonObject getJsonObjectForTypeField(JsonArray jsonInputFormArray) {
		for(int i=0;i<jsonInputFormArray.size();i++) {
			JsonObject jsonInputField = jsonInputFormArray.getJsonObject(i);
			String strFieldName = jsonInputField.getString(KEY_NAME);
			if(DomainConstants.SELECT_TYPE.equals(strFieldName)) {
				return jsonInputField;
			}
		}
		return null;
	}

	/**
	 * Method to get Json Array for states with Json object with name and value keys
	 * 
	 * @param jsonStateDefaultValue : JsonObject with state info
	 * @return : JsonArray with state info as array of json objects with name and
	 *         value keys
	 */
	JsonArray getJsonArrayForState(JsonObject jsonStateDefaultValue) {
		JsonArrayBuilder jsonDefaultStateArray = Json.createArrayBuilder();
		jsonStateDefaultValue.forEach((key, value) -> {
			JsonObjectBuilder jsonStateOptions = Json.createObjectBuilder();
			jsonStateOptions.add(KEY_NAME, key);
			jsonStateOptions.add(KEY_VALUE, value);
			jsonDefaultStateArray.add(jsonStateOptions);
		});
		return jsonDefaultStateArray.build();
	}

	/**
	 * Method to replace the value of actual type names with JsonArray of actual and display names for each type
	 * @param context : Context eMatrix context object
	 * @param jsonInputElmObj : JsonObject with info of type input field
	 * @return : JsonObject updated for type field with JsonArray of actual and display names for each type
	 * @throws Exception 
	 */
	JsonObject updateTypeInputField(Context context, JsonObject jsonInputElmObj,JsonObjectBuilder jsonStateOptions, JsonObjectBuilder jsonStateDefaultValue) throws Exception {
		JsonArrayBuilder jsonTypeArray = Json.createArrayBuilder();
		String strLanguage = context.getLocale().getLanguage();
		String strTypes = jsonInputElmObj.getString(KEY_OPTIONS);
		StringList slTypeList = StringUtil.split(strTypes, ",");
		for(int i=0; i<slTypeList.size(); i++) {
			String strTypeActualName = slTypeList.get(i);
			String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context, SELECT_ADMIN_TYPE, strTypeActualName, strLanguage);
			if(UIUtil.isNullOrEmpty(strDisplayName) || strDisplayName.startsWith(PREFIX_EMX_FRAMEWORK)) {
				strDisplayName = strTypeActualName;
			}
			JsonObjectBuilder jsonTypeObj = Json.createObjectBuilder();
			jsonTypeObj.add(KEY_NAME, strDisplayName);
			jsonTypeObj.add(KEY_VALUE, strTypeActualName);
			jsonTypeArray.add(jsonTypeObj);
			JsonArray jsonStateOptionsArray = getSatesForType(context, strTypeActualName, jsonStateDefaultValue);
			jsonStateOptions.add(strTypeActualName, jsonStateOptionsArray);
		}
		JsonObjectBuilder jsonObjBuild = getJsonObjBuilderFromJson(jsonInputElmObj);
		jsonObjBuild.add(KEY_OPTIONS, jsonTypeArray.build());
		return jsonObjBuild.build();
	}

	/**
	 * 
	 * Method to get the policy and state details for type along with actual and display names
	 * 
	 * @param context : Context eMatix context object
	 * @param strTypeActualName : String type value
	 * @return : JsonArray with state actual and display name for type
	 * @throws MatrixException  
	 */
	JsonArray getSatesForType(Context context, String strTypeActualName, JsonObjectBuilder jsonStateDefaultValue) throws MatrixException {
		JsonArrayBuilder jsonStatesArray = Json.createArrayBuilder();
		Map<String, StringList> statesMap = getAllStatesForType(context, strTypeActualName);
		for (Entry<String, StringList> entry : statesMap.entrySet()) {
			String strKey = entry.getKey();
			StringList slValuesList = entry.getValue();
			String strValue = StringUtil.join(slValuesList, KEY_PIPE_SEPARATOR);

			updateDefaultValuesForState(strKey, slValuesList, jsonStateDefaultValue);
			JsonObjectBuilder jsonStateObj = Json.createObjectBuilder();
			jsonStateObj.add(KEY_VALUE, strValue);
			jsonStateObj.add(DomainConstants.SELECT_NAME, strKey);
			jsonStatesArray.add(jsonStateObj);
		}
		return jsonStatesArray.build();
	}
	
	/**
	 * Method to get all states for state field
	 * 
	 * @param strKey                : String state display name
	 * @param slValuesList          : StringList state actual name list
	 * @param jsonStateDefaultValue
	 */
	void updateDefaultValuesForState(String strKey, StringList slValuesList, JsonObjectBuilder jsonStateDefaultValue) {
		JsonObject jsonTempObj = jsonStateDefaultValue.build();
		if (!jsonTempObj.containsKey(strKey)) {
			jsonStateDefaultValue.add(strKey, StringUtil.join(slValuesList, KEY_PIPE_SEPARATOR));
		} else {
			String strStateValues = jsonTempObj.getString(strKey);
			StringList slStateValueList = StringUtil.split(strStateValues, KEY_PIPE_SEPARATOR);
			for (int i = 0; i < slValuesList.size(); i++) {
				String strStateName = slValuesList.get(i);
				if (!slStateValueList.contains(strStateName)) {
					slStateValueList.add(strStateName);
				}
			}
			jsonStateDefaultValue.add(strKey, StringUtil.join(slStateValueList, KEY_PIPE_SEPARATOR));
		}

	}

	/**
	 * Method to get all states for type via policies
	 * 
	 * @param context           : Context eMatrix context object
	 * @param strTypeActualName : String type name
	 * @return : Map with display and actual names for states
	 * @throws MatrixException 
	 */
	Map<String, StringList> getAllStatesForType(Context context, String strTypeActualName) throws MatrixException  {
		Map<String, StringList> stateMap = new TreeMap<>();
		if (UIUtil.isNotNullAndNotEmpty(strTypeActualName)) {
			BusinessType busTypObj = new BusinessType(strTypeActualName,
					new Vault(PersonUtil.getDefaultVault(context)));
			PolicyList policyListObj = busTypObj.getPoliciesForPerson(context, true);
			String strLanguage = context.getSession().getLanguage();
			for (int i = 0; i < policyListObj.size(); i++) {
				Policy policyObj = policyListObj.get(i);
				StateRequirementList stateListObj = policyObj.getStateRequirements(context);
				String strPolicyName = policyObj.getName();
				getStateFromPolicy(context, stateListObj, strPolicyName, strLanguage, stateMap);
			}
		}
		return stateMap;
	}
	
	/**
	 * Method to get all states from a policy
	 * 
	 * @param context       : Context eMatrix context object
	 * @param stateListObj  : StateRequirementList object with states list
	 * @param strPolicyName : String policy name
	 * @param strLanguage   : String Language
	 * @param stateMap      : Map with display and actual names for states
	 * @throws MatrixException
	 */
	void getStateFromPolicy(Context context, StateRequirementList stateListObj, String strPolicyName,
			String strLanguage, Map<String, StringList> stateMap) throws MatrixException {

		for (int j = 0; j < stateListObj.size(); j++) {
			StateRequirement stateObj = stateListObj.get(j);
			String strStateActualName = stateObj.getName();
			String strStateDisplayName = EnoviaResourceBundle.getStateI18NString(context, strPolicyName,
					strStateActualName, strLanguage);
			strStateDisplayName = strStateDisplayName.trim();
			if (UIUtil.isNotNullAndNotEmpty(strStateDisplayName)
					|| !strStateDisplayName.startsWith(PREFIX_EMX_FRAMEWORK)) {
				updateStateMap(stateMap, strStateDisplayName, strStateActualName);
			} else {
				updateStateMap(stateMap, strStateActualName, strStateActualName);
			}
		}

	}

	/**
	 * Method to updated the state Map
	 * 
	 * @param stateMap            : Map with display and actual names for states to
	 *                            be updated
	 * @param strStateDisplayName : String state actual name
	 * @param strStateActualName  : String state display name
	 */
	void updateStateMap(Map<String, StringList> stateMap, String strStateDisplayName, String strStateActualName) {
		if (!stateMap.containsKey(strStateDisplayName)) {
			stateMap.put(strStateDisplayName, new StringList(strStateActualName));
		} else {
			StringList slActualNameList = stateMap.get(strStateDisplayName);
			if (!slActualNameList.contains(strStateActualName)) {
				slActualNameList.add(strStateActualName);
				stateMap.put(strStateDisplayName, slActualNameList);
			}
		}
	}

	/**
	 * Method to get the input information during generation of report with changes
	 * @param context : Context eMatrix context object
	 * @param strReportId : String Report object id
	 * @return : String json with input information on Report and Report Template
	 * @throws Exception
	 */
	String getReportTemplateDataWithChanges(Context context, String strReportId) throws Exception {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strReportId)) {		
				DomainObject dobReportObj = DomainObject.newInstance(context, strReportId);
				
				StringList slObjectSelects = new StringList(1);
				slObjectSelects.add(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);

				String strAttInputInfo = dobReportObj.getInfo(context, SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);
				
				JsonObject jsonInputInfo = getJsonFromJsonString(strAttInputInfo);
				JsonArray jsonInputArray = jsonInputInfo.getJsonArray(KEY_PARAMETERS);
				if (!jsonInputArray.isEmpty()) {
					jsonObjOutput.add(KEY_INPUT_VALUES, jsonInputArray.toString());
				}
				
				if(jsonInputInfo.containsKey(KEY_TEMPLATE_ID)) {
					String strReportTemplateId = jsonInputInfo.getString(KEY_TEMPLATE_ID); 				
					DomainObject dobReportModel = DomainObject.newInstance(context, strReportTemplateId);
										
					String strInputFormData = dobReportModel.getInfo(context, SELECT_ATTRIBUTE_REPORT_FORM_DATA);

					if (UIUtil.isNotNullAndNotEmpty(strInputFormData)) {
						String strUpdatedInputFormData = updateInputFormDataForTypes(context, strInputFormData);
						jsonObjOutput.add(KEY_INPUT, strUpdatedInputFormData);
					}
				}
			}			
			return jsonObjOutput.build().toString();			
		} catch (Exception e) {
			jsonObjOutput.add(KEY_STATUS, VALUE_FAILED);
			if(UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(KEY_ERROR, strExceptionMessage);
			throw e;
		}
	}
	
	/**
	 * Generic method to get physical id from TNR by using MQL query
	 * @param context : Context eMatrix context object
	 * @param strType : String type
	 * @param strName : String name
	 * @param strRevision : String revision
	 * @return : String physical id from TNR
	 * @throws FrameworkException
	 */
	public String getObjectId(Context context, String strType, String strName, String strRevision) throws FrameworkException {
		String strObjPhysicalId = "";
		MapList mlList = DomainObject.findObjects(context, 
				strType, 								//type pattern
				strName, 								//name pattern
				strRevision, 							//revision pattern
				null, 									// ownerPattern
				DomainConstants.QUERY_WILDCARD, 		// vaultPattern
				null, 									// where clause
				null,									// queryName
				true,									// expandType
				new StringList(SELECT_PHYSICAL_ID),		// objectSelects
				Short.parseShort("0"));    				// objectLimit
		if(!mlList.isEmpty()) {
			Map<?,?> objMap = (Map<?, ?>) mlList.get(0);
			strObjPhysicalId = (String) objMap.get(SELECT_PHYSICAL_ID);
		}
		return strObjPhysicalId;
	}
	
	/**
	 * Method to create CAT Report Reference (frequently referred as 'Report') object
	 * @param context : Context eMatrix context object
	 * @param strInputData : String input Json with details such as Report Doc id need to be connected, report 'V_Name' ,input and out put json etc needed to create Report
	 * @return : String json with created Report object info
	 * @throws Exception
	 */
	String createCATReportObject(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		try {
			String strVName = "";
			String strDocId = "";
			
			JsonObject jsonObject = getJsonFromJsonString(strInputData);
			if (jsonObject.containsKey(KEY_REPORT_TITLE)) {
				strVName = jsonObject.getString(KEY_REPORT_TITLE);
			}

			JsonArray jsonReportInputArray = jsonObject.getJsonArray(KEY_REPORT_INPUT_ARRAY);
			boolean bValidationStatus = validateInputForQuery(jsonReportInputArray);

			if(bValidationStatus) {
				JsonObject jsonInputOutputObj = getInputOutputJsonArrays(jsonReportInputArray);
				String strTemplateId = jsonObject.getString(KEY_TEMPLATE_ID);
								
				String strName = "";
				String strReportId = createCATReportObjectWithDefaultSchema(context, strVName, strTemplateId);
				if (UIUtil.isNotNullAndNotEmpty(strReportId)) {
					createReportDataObject(context, strReportId);
					DomainObject dobReportObject = DomainObject.newInstance(context, strReportId);
					strName = dobReportObject.getInfo(context, DomainConstants.SELECT_NAME);
					if (jsonObject.containsKey(KEY_TARGET_DOCUMENT_ID)) {
						strDocId = jsonObject.getString(KEY_TARGET_DOCUMENT_ID);
					} else {
						strDocId = createReportDocumentObject(context, strReportId);
					}
					String strQuery = generateMQLQueryArray(jsonReportInputArray);
					setAttributesOnReportCreation(context, dobReportObject, strDocId, strQuery, jsonInputOutputObj, strTemplateId, jsonReportInputArray);
				}
				
				JsonObjectBuilder templateJson =Json.createObjectBuilder();
				
				jsonObjOutput.add(DomainConstants.SELECT_ID, strReportId);
				jsonObjOutput.add(KEY_TITLE, strVName);
				templateJson.add(KEY_TEMPLATE_ID,strTemplateId);
				jsonObjOutput.add(KEY_TEMPLATE_INFO,getTemplateDetails(context, templateJson.build().toString()));
				jsonObjOutput.add(DomainConstants.SELECT_NAME, strName);
			} else {
				jsonObjOutput.add(KEY_STATUS, VALUE_FAILED);
				jsonObjOutput.add(KEY_ERROR, strReportCreationErrorMsg);
			}
			
			return jsonObjOutput.build().toString();			
		} catch (Exception e) {
			jsonObjOutput.add(KEY_STATUS, VALUE_FAILED);
			if(UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(KEY_ERROR, strExceptionMessage);
			throw e;
		}
	}
	
	 /**
	  * Method to create 'Report Document' object
	  * @param context : Context eMatrix context object
	 * @param strReportId 
	  * @return : String object id of created 'Report Document' object
	  * @throws Exception
	  */
	private String createReportDocumentObject(Context context, String strReportId) throws Exception {
			String strName = REPORT_DOC_PREFIX+UnifiedAutonamingServices.autoname(context, TYPE_REPORT_DOCUMENT);
			DomainObject dobReportDocObj = DomainObject.newInstance(context);
			String strReportDocId = "";
			try {
			dobReportDocObj.createObject(context, TYPE_REPORT_DOCUMENT, strName, DEFAULT_REVISION_REPORT_DOCUMENT, POLICY_REPORT_DOCUMENT, context.getVault().getName());
			dobReportDocObj.promote(context);
			strReportDocId = dobReportDocObj.getInfo(context,SELECT_PHYSICAL_ID);
			setDocumentInformationOnReport(context, strReportDocId, strReportId);
			}catch (Exception ex){
				throw ex;
			}			
			return strReportDocId;
	 } 
	
	/**
	 * This method sets OOTB relation between Report object with 'Report Document'
	 * object, we are using OOTB APIs which are deprecated but unavoidable
	 * 
	 * @param context        : Context eMatrix context object
	 * @param strReportDocId : String 'Report Document' object id
	 * @param strReportId    : String Report object id
	 * @throws Exception
	 */
	private void setDocumentInformationOnReport(Context context, String strReportDocId, String strReportId)
			throws Exception {
		boolean isActiveTransaction = false;
		PLMCoreModelerSession pLMCoreModelerSession = PLMCoreModelerSession
				.getPLMCoreModelerSessionFromContext(context);
		try {
			isActiveTransaction = ContextUtil.isTransactionActive(context);
			if (!isActiveTransaction) {
				isActiveTransaction = ContextUtil.startTransaction(context, true);
			}
			pLMCoreModelerSession.openSession();
			CATRgnIReportNav cATRgnIReportNav = (CATRgnIReportNav) pLMCoreModelerSession
					.getModeler(CLASS_CAT_RGN_REPORT_NAV);
			String[] reportPlmIds = cATRgnIReportNav.convertM1ID2PLMID(Collections.singletonList(strReportId));
			String[] reportPlmDocIds = cATRgnIReportNav.convertM1ID2PLMID(Collections.singletonList(strReportDocId));

			PLMxReferenceEntity[] reports = cATRgnIReportNav.getCATReportReferences(reportPlmIds);

			cATRgnIReportNav.updateCATReportGeneratedDocument(reports[0], reportPlmDocIds[0]);
			pLMCoreModelerSession.commitSession(false);
			pLMCoreModelerSession.closeSession(false);
			if (isActiveTransaction) {
				ContextUtil.commitTransaction(context);
			}
		} catch (Exception e) {
			if (isActiveTransaction) {
				ContextUtil.abortTransaction(context);
			}
			throw e;
		} finally {
			pLMCoreModelerSession.closeSession(true);
		}
	}
	
	/**
	 * 
	 * @param jsonReportInputArray
	 * @return
	 */
	boolean validateInputForQuery(JsonArray jsonReportInputArray) {
		boolean bReturnStatus = true;
		JsonObject jsonFirstPageobject = jsonReportInputArray.getJsonObject(0);
		if (!jsonFirstPageobject.containsKey(KEY_EXPAND_INFO)) {
			JsonArray jsonInputArray = jsonFirstPageobject.getJsonArray(KEY_INPUT_QUERY_DATA);
			bReturnStatus = checkForTypeNameInputFields(jsonInputArray);
		}
		return bReturnStatus;
	}

	/**
	 * 
	 * @param jsonInputArray
	 * @return
	 */
	boolean checkForTypeNameInputFields(JsonArray jsonInputArray) {
		boolean bReturnValue = true;
		boolean bIsTypeFieldExist = false;
		boolean bIsNameFieldExist = false;
		for (int i = 0; i < jsonInputArray.size(); i++) {
			JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);
			String strFieldValue = jsonElement.getString(KEY_VALUE);
			String strFieldType = jsonElement.getString(DomainConstants.SELECT_TYPE);

			if(DomainConstants.SELECT_TYPE.equals(strFieldType)) {
				boolean bTypeFieldValidation = checkForTypeField(jsonElement, strFieldValue);
				if(!bTypeFieldValidation) {
					bReturnValue = false;
				}
				bIsTypeFieldExist = true;
			} else if(DomainConstants.SELECT_NAME.equals(strFieldType)) {
				if (UIUtil.isNullOrEmpty(strFieldValue)) {
					bReturnValue = false;
				}
				bIsNameFieldExist = true;
			}
		}
		
		if(bIsTypeFieldExist && bIsNameFieldExist) {
			return bReturnValue;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param jsonElement
	 * @param strFieldValue
	 * @return
	 */
	boolean checkForTypeField(JsonObject jsonElement, String strFieldValue) {
		if (UIUtil.isNullOrEmpty(strFieldValue)) {
			if(jsonElement.containsKey(KEY_DEFAULT_TYPES)) {
				strFieldValue = jsonElement.getString(KEY_DEFAULT_TYPES);
				if (UIUtil.isNullOrEmpty(strFieldValue)) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param jsonReportInputArray
	 * @return
	 */
	JsonObject getInputOutputJsonArrays(JsonArray jsonReportInputArray) {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonArrayBuilder jsonInputArray = Json.createArrayBuilder();
		JsonArrayBuilder jsonOutputArray = Json.createArrayBuilder();
		for(int i=0; i<jsonReportInputArray.size(); i++) {
			JsonObject jsonTempObj = jsonReportInputArray.getJsonObject(i);
			if(jsonTempObj.containsKey(KEY_INPUT_QUERY_DATA)) {
				jsonInputArray.add(jsonTempObj.getJsonArray(KEY_INPUT_QUERY_DATA));
			}
			if(jsonTempObj.containsKey(KEY_REPORT_OUTPUT_COLUMN)) {
				jsonOutputArray.add(jsonTempObj.getJsonArray(KEY_REPORT_OUTPUT_COLUMN));
			}
		}
		jsonObjOutput.add(KEY_INPUT_QUERY_DATA, jsonInputArray);
		jsonObjOutput.add(KEY_REPORT_OUTPUT_COLUMN, jsonOutputArray);
		return jsonObjOutput.build();
	}

	/**
	 * 
	 * @param jsonReportInputArray
	 * @param strTemplateType 
	 * @return
	 * @throws Exception 
	 */
	String generateMQLQueryArray(JsonArray jsonReportInputArray) throws Exception {
		JsonArrayBuilder jsonQueryArray = Json.createArrayBuilder();		
		JsonObject jsonFirstPageobject = jsonReportInputArray.getJsonObject(0);
		JsonObject jsonFirstPageQuery = getFirstPageQuery(jsonFirstPageobject);
		jsonQueryArray.add(jsonFirstPageQuery);
		int iSize = jsonReportInputArray.size();
		if(iSize == 1) {
			return jsonQueryArray.build().toString();
		} else {
			setQueryArray(jsonReportInputArray, iSize,jsonQueryArray);
			return jsonQueryArray.build().toString();
		}
	}
	
	/**
	 * 
	 * @param jsonReportInputArray
	 * @param iSize
	 * @param jsonQueryArray
	 * @throws ParseException 
	 */
	void setQueryArray(JsonArray jsonReportInputArray, int iSize, JsonArrayBuilder jsonQueryArray) throws ParseException {
		for (int i = 1; i < iSize; i++) {
			JsonObject jsonPageInfoObj = jsonReportInputArray.getJsonObject(i);
			JsonObject jsonQueryObj = generateRelatedObjectsInfo(jsonPageInfoObj);
			jsonQueryArray.add(jsonQueryObj);
		}
	}

	/**
	 * 
	 * @param jsonFirstPageobject
	 * @return
	 * @throws Exception
	 */
	JsonObject getFirstPageQuery(JsonObject jsonFirstPageobject) throws Exception {

		if (jsonFirstPageobject.containsKey(KEY_EXPAND_INFO) && jsonFirstPageobject.getJsonObject(KEY_EXPAND_INFO).containsKey(KEY_VALUE)) {
			return generateRelatedObjectsInfo(jsonFirstPageobject);
		} else {
			JsonArray jsonInputArray = jsonFirstPageobject.getJsonArray(KEY_INPUT_QUERY_DATA);
			JsonArray jsonOutputArray = jsonFirstPageobject.getJsonArray(KEY_REPORT_OUTPUT_COLUMN);
			String strLevel = jsonFirstPageobject.getString(KEY_LEVEL);
			String strHideResults = "";
			if(jsonFirstPageobject.containsKey(KEY_HIDE_OUTPUT)) {
				strHideResults = jsonFirstPageobject.getString(KEY_HIDE_OUTPUT);
			}
			return generateMQLQuery(jsonInputArray, jsonOutputArray, strLevel, strHideResults);
		}
	}

	/**
	 * 
	 * @param jsonReportInputArray
	 * @return
	 * @throws ParseException 
	 */
	JsonObject generateRelatedObjectsInfo(JsonObject jsonInputInfo) throws ParseException {
		
		JsonObjectBuilder jsonQueryObject = Json.createObjectBuilder();
		JsonObject jsonExpandInfo = jsonInputInfo.getJsonObject(KEY_EXPAND_INFO);
		getBasicsListForExpand(jsonExpandInfo, jsonQueryObject);
		
		JsonArray jsonInputArray = jsonInputInfo.getJsonArray(KEY_INPUT_QUERY_DATA);
		JsonArray jsonOutputArray = jsonInputInfo.getJsonArray(KEY_REPORT_OUTPUT_COLUMN);
		JsonObject jsonObjRelSelects = getObjectRelSelectArray(jsonOutputArray);
		JsonArray jsonObjSelectArray = jsonObjRelSelects.getJsonArray(KEY_BUS_SELECT);
		jsonQueryObject.add(KEY_BUS_SELECT, jsonObjSelectArray);
		JsonArray jsonRelSelectArray = jsonObjRelSelects.getJsonArray(KEY_REL_SELECT);
		if(!jsonRelSelectArray.isEmpty()) {
			jsonQueryObject.add(KEY_REL_SELECT, jsonRelSelectArray);
		}
		JsonArray jsonRelmidSelectArray = jsonObjRelSelects.getJsonArray(KEY_RELMID);
		if(!jsonRelmidSelectArray.isEmpty()) {
			jsonQueryObject.add(KEY_RELMID, jsonRelmidSelectArray);
		}
		
		getWhereClauseForRelatedObjects(jsonInputArray, jsonQueryObject);
		String strLevel = jsonInputInfo.getString(KEY_LEVEL);
		
		jsonQueryObject.add(KEY_LEVEL, strLevel);
		jsonQueryObject.add(KEY_QUERY_TYPE, VALUE_QUERY_TYPE_EXPAND);
		
		if(jsonInputInfo.containsKey(KEY_HIDE_OUTPUT)) {
			String strHideResults = jsonInputInfo.getString(KEY_HIDE_OUTPUT);
			jsonQueryObject.add(KEY_HIDE_OUTPUT, strHideResults);
		}
		
		return jsonQueryObject.build();
	}
	
	/**
	 * Method to get two separate json arrays, one for object selects and another for rel selects
	 * @param jsonOutputArray : JsonArray out put selects array
	 * @return
	 */
	JsonObject getObjectRelSelectArray(JsonArray jsonOutputArray) {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		JsonArrayBuilder jsonBusSelectArray = Json.createArrayBuilder();
		JsonArrayBuilder jsonRelSelectArray = Json.createArrayBuilder();
		JsonArrayBuilder jsonRelmidArray = Json.createArrayBuilder();
		Map<String,StringList> relmidMap = new HashMap<>();
		for (int i = 0; i < jsonOutputArray.size(); i++) {
			JsonObject jsonSelectObj = jsonOutputArray.getJsonObject(i);
			String strSelectType = jsonSelectObj.getString("type");
			if (KEY_REL_SELECT.equals(strSelectType)) {
				jsonRelSelectArray.add(jsonSelectObj);
			} else if (KEY_BUS_SELECT.equals(strSelectType)) {
				jsonBusSelectArray.add(jsonSelectObj);
			} else if (strSelectType.startsWith(KEY_TO_MID) || strSelectType.startsWith(KEY_FROM_MID)) {
				getRelmidColumnsMap(jsonSelectObj, relmidMap);
			}
		}
		
		if(!relmidMap.isEmpty()) {
			jsonRelmidArray = setRelmidColumnsJson(relmidMap);
		}
		jsonObjOutput.add(KEY_BUS_SELECT, jsonBusSelectArray);
		jsonObjOutput.add(KEY_REL_SELECT, jsonRelSelectArray);
		jsonObjOutput.add(KEY_RELMID, jsonRelmidArray);
		return jsonObjOutput.build();
	}

	/**
	 * Method to parse frommid or tomid columns into Json array
	 * @param relmidMap
	 * @return
	 */
	JsonArrayBuilder setRelmidColumnsJson(Map<String, StringList> relmidMap) {
		JsonArrayBuilder jsonRelmidArray = Json.createArrayBuilder();
		for (Map.Entry<String, StringList> entry : relmidMap.entrySet()) {
			JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
			JsonArrayBuilder jsonSelectExprArray = Json.createArrayBuilder();
			JsonArrayBuilder jsonSelectLabelArray = Json.createArrayBuilder();
			StringBuilder sbRelmidExpr = new StringBuilder();
			sbRelmidExpr.append(entry.getKey()).append(".").append(DomainConstants.SELECT_ID);
			StringList slSelectList = entry.getValue();

			jsonObjOutput.add(KEY_EXPR, sbRelmidExpr.toString());
			jsonObjOutput.add(KEY_EXPR_SELECT, sbRelmidExpr.toString());

			for (int i = 0; i < slSelectList.size(); i++) {
				String strSelect = slSelectList.get(i);
				String[] strExprLabelArray = strSelect.split(java.util.regex.Pattern.quote(EXPR_SEPARATOR), -1);
				if (strExprLabelArray.length > 1) {
					jsonSelectExprArray.add(strExprLabelArray[0]);
					jsonSelectLabelArray.add(strExprLabelArray[1]);
				}
			}

			jsonObjOutput.add(KEY_SELECTS, jsonSelectExprArray);
			jsonObjOutput.add(KEY_SELECTS_LABEL, jsonSelectLabelArray);
			jsonRelmidArray.add(jsonObjOutput);
		}
		return jsonRelmidArray;
	}

	/**
	 * Method to parse frommid or tomid columns
	 * @param jsonSelectObj
	 * @param relmidMap 
	 * @return
	 */
	void getRelmidColumnsMap(JsonObject jsonSelectObj, Map<String, StringList> relmidMap) {
		StringList slSelectList = new StringList();
		String strSelectType = jsonSelectObj.getString("type");
		StringBuilder sbSelect = new StringBuilder();
		sbSelect.append(jsonSelectObj.getString(KEY_EXPR)).append(EXPR_SEPARATOR).append(jsonSelectObj.getString(KEY_LABEL));
		if(relmidMap.containsKey(strSelectType)) {
			StringList slExistingSelectList = relmidMap.get(strSelectType);
			slExistingSelectList.add(sbSelect.toString());
			relmidMap.put(strSelectType, slExistingSelectList);
		} else {
			slSelectList.add(sbSelect.toString());
			relmidMap.put(strSelectType, slSelectList);
		}
	}

	/**
	 * 
	 * @param jsonExpandInfo
	 * @param jsonQueryObject
	 * @return
	 */
	void getBasicsListForExpand(JsonObject jsonExpandInfo, JsonObjectBuilder jsonQueryObject) {

		String strObjectId = DomainConstants.SELECT_ID;
		String strDefaultTypePattern = WILD_CARD_ASTERESK;
		if(jsonExpandInfo.containsKey(KEY_VALUE)) {
			strObjectId = jsonExpandInfo.getString(KEY_VALUE);
		}
		jsonQueryObject.add(DomainConstants.SELECT_ID, strObjectId);
		jsonQueryObject.add("limit", MQL_QUERY_LIMIT);
		
		String strRelName = jsonExpandInfo.getString(KEY_REL_NAME);
		String strDirection = jsonExpandInfo.getString(KEY_REL_DIRECTION);
		jsonQueryObject.add("direction", strDirection);

		jsonQueryObject.add(DomainConstants.KEY_RELATIONSHIP, strRelName);

		jsonQueryObject.add(DomainConstants.SELECT_TYPE, strDefaultTypePattern);
		
		String strRecurseLevel = VALUE_RECURSE_ONE;
		if(jsonExpandInfo.containsKey(KEY_RECURSE_LEVEL)) {
			strRecurseLevel = jsonExpandInfo.getString(KEY_RECURSE_LEVEL);
			if(!VALUE_RECURSE_ALL.equals(strRecurseLevel)) {
				strRecurseLevel = VALUE_RECURSE_ONE;
			}
		}

		jsonQueryObject.add(KEY_RECURSE_LEVEL, strRecurseLevel);

	}

	/**
	 * Method to get whereClause and type pattern
	 * @param jsonInputArray
	 * @param jsonOrderValues
	 * @throws ParseException
	 */
	void getWhereClauseForRelatedObjects(JsonArray jsonInputArray, JsonObjectBuilder jsonQueryObject) throws ParseException {
		StringBuilder sbWhereClauseBuilder = new StringBuilder();
		boolean isWhereClassExist = false;
		String strTypePattern = WILD_CARD_ASTERESK;
		for (int i = 0; i < jsonInputArray.size(); i++) {
			JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);
			String strFieldName = jsonElement.getString(DomainConstants.SELECT_NAME);
			String strFieldValue = jsonElement.getString(KEY_VALUE);
			String strFieldType = jsonElement.getString(DomainConstants.SELECT_TYPE);
			
			switch (strFieldType) {
				case DomainConstants.SELECT_TYPE:
					if(UIUtil.isNullOrEmpty(strFieldValue) && jsonElement.containsKey(KEY_DEFAULT_TYPES)) {
						strFieldValue = jsonElement.getString(KEY_DEFAULT_TYPES);
					}
					strTypePattern = getBasicsPattern(strFieldName, strFieldValue);
					break;
					
				case REVISION_LATEST:
					appendToWhereClauseBuilder(REVISION_LATEST, VALUE_TRUE, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
				
				case REVISION_LAST:
					appendToWhereClauseBuilder(DomainConstants.SELECT_REVISION, REVISION_LAST, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
				
				case KEY_DATE:
					appendToWhereClauseBuilderForDate(strFieldName, strFieldValue, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
					
				case KEY_QUERY:
					String  strExpression = jsonElement.getString(KEY_EXPR);
					appendToWhereClauseBuilder(strExpression, strFieldValue, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
					
				default:
					appendToWhereClauseBuilder(strFieldName, strFieldValue, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
			}
		}
		
		if (isWhereClassExist) {
			String strWhereClause = sbWhereClauseBuilder.toString();
			if (UIUtil.isNotNullAndNotEmpty(strWhereClause)) {
				strWhereClause = strWhereClause.substring(0, strWhereClause.length() - 5);
				jsonQueryObject.add(KEY_WHERE_CLAUSE, strWhereClause);
			}
		}

		if(UIUtil.isNotNullAndNotEmpty(strTypePattern) && !WILD_CARD_ASTERESK.equals(strTypePattern)) {
			jsonQueryObject.add(DomainConstants.SELECT_TYPE, strTypePattern);
		}

	}
	
	/**
	 * This method creates the CAT Report Reference (frequently referred as 'Report') object. We are using OOTB APIs to create Report object along with its OOTB schema to match its data model, 
	 * for which we need to use OOTB APIs which are deprecated but unavoidable
	 * 
	 * @param context : Context eMatrix context object
	 * @param strVName : String V_Name attribute for Report
	 * @param strReportTemplateId : String Report Template id
	 * @return : String created Report object id
	 * @throws Exception
	 */
     String createCATReportObjectWithDefaultSchema(Context context, String strVName, String strReportTemplateId) throws Exception { 
    	PLMCoreModelerSession pLMCoreModelerSession = PLMCoreModelerSession.getPLMCoreModelerSessionFromContext(context);
    	boolean isActiveTransaction = false;
		try {			
			Map<String, String> iPublicAttributes = new HashMap<>();
			iPublicAttributes.put(KEY_V_NAME, strVName);
			iPublicAttributes.put(KEY_V_DESCRIPTION, strVName);
			iPublicAttributes.put(KEY_PLM_EXTERNAL_ID, null);

			String strOutputId = getObjectId(context, TYPE_REPORT_TEMPLATE, NAME_REPORT_TEMPLATE, REVISION_MODEL_TEMPLATE);
			
			List<List<String>> finalList = new ArrayList<>();
			finalList.add(Collections.singletonList(strReportTemplateId));
			finalList.add(Collections.singletonList(strOutputId));
			
			isActiveTransaction = ContextUtil.isTransactionActive(context);
			if(!isActiveTransaction) {
				isActiveTransaction = ContextUtil.startTransaction(context, true);
			}
			
			pLMCoreModelerSession.openSession(); 
			CATRgnIReportNav cATRgnIReportNav = (CATRgnIReportNav)pLMCoreModelerSession.getModeler(CLASS_CAT_RGN_REPORT_NAV);
			IPLMTemplateContext iPLMTemplateContext = PLMxTemplateFactory.newContext((PLMCoreAbstractModeler)cATRgnIReportNav); 
			IPLMDictionaryPublicItf iPLMDictionaryPublicItf = (new IPLMDictionaryPublicFactory()).getDictionary();

			String typeName = PLMDictionaryServices.getPLMType(KEY_CAT_REPORT, PLMDictionaryServices.REFERENCE);
			IPLMDictionaryPublicClassItf classReportReference = iPLMDictionaryPublicItf.getClass(context, typeName);
			PLMxReferenceEntity reportReference = cATRgnIReportNav.createCATReportReferenceFromType(classReportReference, iPLMTemplateContext, iPublicAttributes, finalList);
			
			CommitReport cr = pLMCoreModelerSession.commitSession(false);
			if (cr.getGlobalStatus().equals(CommitReport.FAILED)) throw new ReportNavException(cr.toString());
			pLMCoreModelerSession.closeSession(false);
			
			if(isActiveTransaction) {
				ContextUtil.commitTransaction(context);
			}

			String plmIdentifier = reportReference.getPLMIdentifier();
			String strReportId = PLMIDAnalyser.getPhysicalID(plmIdentifier); 
			
			return strReportId;
			
		} catch (Exception e) {
			if(isActiveTransaction) {
				ContextUtil.abortTransaction(context);
			}
			throw e;
		} finally {
			pLMCoreModelerSession.closeSession(true);
		}
	}
	
	/**
	 * Method to create and connect object of type RGNReportData with Report to
	 * retain the data model
	 * 
	 * @param context     : Context eMatrix context object
	 * @param strReportID : String Report object id
	 * @throws MatrixException
	 */
	private void createReportDataObject(Context context, String strReportID) throws MatrixException {
		boolean isActiveTransaction = false;
		try {
			isActiveTransaction = ContextUtil.isTransactionActive(context);
			if (!isActiveTransaction) {
				isActiveTransaction = ContextUtil.startTransaction(context, true);
			}
			DomainObject bo = DomainObject.newInstance(context);
			bo.createObject(context, TYPE_REPORT_DATA, null, null, null, null);
			bo.promote(context);
			DomainRelationship.connect(context, strReportID, RELATIONSHIP_REPORT_DATA, bo.getId(context), true);
			if (isActiveTransaction) {
				ContextUtil.commitTransaction(context);
			}
		} catch (MatrixException e) {
			if (isActiveTransaction) {
				ContextUtil.abortTransaction(context);
			}
			throw e;
		}
	}
	
	/**
	 * Method to convert String json which is usually the value of attributes ReportInputInformation, pgReportFormData and pgReportOutputData etc  to JsonObject
	 * @param strJsonString : String json (usually the value of attributes ReportInputInformation, pgReportFormData and pgReportOutputData etc)
	 * @return : JsonObject created from String json
	 */
	JsonObject getJsonFromJsonString(String strJsonString) {
		StringReader srJsonString = new StringReader(strJsonString);
		Map<String, String> configMap = new HashMap<>();
		configMap.put(MAX_STRING_LENGTH, VALUE_KILOBYTES);
		JsonReaderFactory factory = Json.createReaderFactory(configMap);					
		try(JsonReader jsonReader = factory.createReader(srJsonString)) {
			return jsonReader.readObject();
		} finally {
			srJsonString.close();
		}
	}
		
	/**
	 * Method to delete Report object
	 * @param context : Context eMatrix context object
	 * @param strReportObjectIds : String of Report object id/ids
	 * @return : String json status of the delete operation
	 * @throws Exception
	 */
	String deleteReportObjects(Context context, String strReportObjectIds) throws Exception {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strReportObjectIds)) {
				String[] strObjIdsArray = strReportObjectIds.split(KEY_ESCAPED_PIPE_SEPARATOR);
				DomainObject.deleteObjects(context, strObjIdsArray);
				jsonObjOutput.add(KEY_STATUS, VALUE_SUCCESS);
			}
		} catch (Exception e) {
			jsonObjOutput.add(KEY_STATUS, VALUE_FAILED);
			if(UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(KEY_ERROR, strExceptionMessage);
			throw e;
		}
		return jsonObjOutput.build().toString();
	}
	
	/**
	 * Method to get the Job status
	 * @param context : Context eMatrix context object
	 * @param strReportId : String report object is
	 * @param strJobId : String Job object id
	 * @return : String json with Job details else report doc info
	 * @throws Exception
	 */
	public String getJobStatus(Context context, String strReportId, String strJobId) throws Exception {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		try {
			if(UIUtil.isNotNullAndNotEmpty(strJobId))
			{
				DomainObject jobObject = DomainObject.newInstance(context,strJobId);
				
				StringList slJobSelect = new StringList(3);
				slJobSelect.add(DomainConstants.SELECT_CURRENT);
				slJobSelect.add(SELECT_ATTRIBUTE_COMPLETION_STATUS);
				slJobSelect.add(SELECT_ATTRIBUTE_ERROR_MESSAGE);
				Map<?, ?> jobMap = jobObject.getInfo(context,slJobSelect);

				if(!JOB_SUCCEEDED.equals((String)jobMap.get(SELECT_ATTRIBUTE_COMPLETION_STATUS)))
				{
					jsonObjOutput.add(KEY_JOB_ID, strJobId);
					jsonObjOutput.add(KEY_JOB_STATUS,(String)jobMap.get(DomainConstants.SELECT_CURRENT));
					jsonObjOutput.add(KEY_JOB_COMPLETION_STATUS,(String)jobMap.get(SELECT_ATTRIBUTE_COMPLETION_STATUS));
					jsonObjOutput.add(KEY_JOB_ERROR_MESSAGE,(String)jobMap.get(SELECT_ATTRIBUTE_ERROR_MESSAGE));
					return jsonObjOutput.build().toString();
				}
			}
			if (UIUtil.isNotNullAndNotEmpty(strReportId)) {
				DomainObject dobReportObject = DomainObject.newInstance(context, strReportId);
				Map<?,?> objectInfo = dobReportObject.getInfo(context, new StringList(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION));
				String strAttInputInfo = (String) objectInfo.get(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);
				return getDocumentDetails(context, strAttInputInfo);
			} else {
				jsonObjOutput.add(KEY_STATUS, VALUE_FAILED);
				jsonObjOutput.add(KEY_ERROR, VALUE_ERROR_MESSAGE);
			}
		} catch (Exception e) {
			jsonObjOutput.add(KEY_STATUS, VALUE_FAILED);
			if(UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonObjOutput.add(KEY_ERROR, strExceptionMessage);
			throw e;
		}
		return jsonObjOutput.build().toString();
	}
		
	/**
	 * Method to generate report with new input
	 * 
	 * @param context      : Context eMatrix context object
	 * @param strInputInfo : String json with new input details
	 * @return : String json status of the operation
	 * @throws Exception
	 */
	String generateReportWithChanges(Context context, String strInputInfo) throws Exception {
		String strStatus = "";
		JsonObject jsonInputInfo = getJsonFromJsonString(strInputInfo);
		String strReportId = jsonInputInfo.getString(DomainConstants.SELECT_ID);

		if (UIUtil.isNotNullAndNotEmpty(strReportId)) {
			strStatus = checkJobStatus(context, strReportId);
			if (UIUtil.isNullOrEmpty(strStatus)) {
				DomainObject dobReportObj = DomainObject.newInstance(context, strReportId);

				StringList slSelectList = new StringList(1);
				slSelectList.add(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);

				Map<?, ?> mapObjInfo = dobReportObj.getInfo(context, slSelectList);
				String strReportInputInfo = (String) mapObjInfo.get(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);
				
				JsonObject jsonReportInputObject = getJsonFromJsonString(strReportInputInfo);
				
				JsonArray jsonInputData = jsonInputInfo.getJsonArray(KEY_REPORT_INPUT_ARRAY);
				JsonObject jsonInputOutputObj = getInputOutputJsonArrays(jsonInputData);
				JsonArray jsonInputArray = jsonInputOutputObj.getJsonArray(KEY_INPUT_QUERY_DATA);

				JsonObject jsonObjBuilInputInfo = getExpandInfoForFirstPage(jsonInputData);			
				JsonArray jsonReportInputArray = getUpdatedInputJsonArray(jsonReportInputObject, jsonInputArray, jsonObjBuilInputInfo);

				
				String strQuery = generateMQLQueryArray(jsonReportInputArray);
				setAttributesOnReportWithChanges(context, dobReportObj, strQuery, jsonReportInputObject, jsonInputArray,
						jsonReportInputArray);
				strStatus = generateReport(context, strReportId);
			}
		}
		return strStatus;
	}

	/**
	 * Method to get Expand Info for first page
	 * @param jsonInputData
	 * @return
	 */
	JsonObject getExpandInfoForFirstPage(JsonArray jsonInputData) {
		JsonObjectBuilder jsonExpandInfo = Json.createObjectBuilder();
		JsonObject jsonFirstPageObject = (JsonObject) jsonInputData.get(0);
		if(jsonFirstPageObject.containsKey(KEY_EXPAND_INFO)) {
			jsonExpandInfo.add(KEY_EXPAND_INFO, jsonFirstPageObject.getJsonObject(KEY_EXPAND_INFO));
		}
		return jsonExpandInfo.build();
	}

	/**
	 * 
	 * @param jsonReportInputObject
	 * @param jsonInputArray
	 * @param jsonInputInfo
	 * @return
	 */
	JsonArray getUpdatedInputJsonArray(JsonObject jsonReportInputObject, JsonArray jsonInputArray,
			JsonObject jsonInputInfo) {
		JsonArrayBuilder jsonReportInputArray = Json.createArrayBuilder();
		JsonArray jsonCurrentInputArray = jsonReportInputObject.getJsonArray(KEY_REPORT_INPUT_ARRAY);
		for(int i=0; i<jsonCurrentInputArray.size(); i++) {
			JsonObject jsonCurrentLevelObj = jsonCurrentInputArray.getJsonObject(i);
			JsonObjectBuilder jsonBuildCurrentLevelObj = getJsonObjBuilderFromJson(jsonCurrentLevelObj);
			jsonBuildCurrentLevelObj.add(KEY_INPUT_QUERY_DATA, jsonInputArray.get(i));
			if(i == 0) {
				updateInputArrayForFirstLevel(jsonInputInfo, jsonBuildCurrentLevelObj, jsonReportInputArray, jsonCurrentLevelObj);
			} else {
				jsonReportInputArray.add(jsonBuildCurrentLevelObj);
			}
		}
		return jsonReportInputArray.build();
	}

	/**
	 * 
	 * @param jsonInputInfo
	 * @param jsonBuildCurrentLevelObj
	 * @param jsonReportInputArray
	 * @param jsonCurrentLevelObj
	 */
	void updateInputArrayForFirstLevel(JsonObject jsonInputInfo, JsonObjectBuilder jsonBuildCurrentLevelObj,
			JsonArrayBuilder jsonReportInputArray, JsonObject jsonCurrentLevelObj) {
		if(jsonInputInfo.containsKey(KEY_EXPAND_INFO)) {
			JsonObject jsonExpandInfo = jsonInputInfo.getJsonObject(KEY_EXPAND_INFO);
			jsonBuildCurrentLevelObj.add(KEY_EXPAND_INFO, jsonExpandInfo);
			jsonReportInputArray.add(jsonBuildCurrentLevelObj);
		} else {
			if(jsonCurrentLevelObj.containsKey(KEY_EXPAND_INFO)) {
				JsonObject jsonTempObj = jsonBuildCurrentLevelObj.build();
				jsonTempObj.remove(KEY_EXPAND_INFO);
				jsonReportInputArray.add(jsonTempObj);
			} else {
				jsonReportInputArray.add(jsonBuildCurrentLevelObj);
			}
		}		
	}

	/**
	 * Method to set updated information on the Report object on the generation of
	 * report with new input information
	 * 
	 * @param context              : Context eMatrix context object
	 * @param dobReportObj         : DomainObject of report to set updated details
	 * @param strQuery             : String updated query with new input
	 * @param jsonReportInputObject   : JsonObject value of attribute
	 *                             ReportInputInformation
	 * @param jsonInputArray       : JsonArray of new input info
	 * @param jsonReportInputArray : JsonArray updated input information
	 * @throws FrameworkException
	 */
	void setAttributesOnReportWithChanges(Context context, DomainObject dobReportObj, String strQuery,
			JsonObject jsonReportInputObject, JsonArray jsonInputArray, JsonArray jsonReportInputArray)
			throws FrameworkException {

		JsonObjectBuilder jsonObjBuilInputInfo = getJsonObjBuilderFromJson(jsonReportInputObject);
		jsonObjBuilInputInfo.add(KEY_PARAMETERS, jsonInputArray);
		jsonObjBuilInputInfo.add(KEY_REPORT_INPUT_ARRAY, jsonReportInputArray);

		Map<String, String> mpAttributeMap = new HashMap<>();
		mpAttributeMap.put(ATTRIBUTE_REPORT_INPUT_INFORMATION, jsonObjBuilInputInfo.build().toString());
		mpAttributeMap.put(ATTRIBUTE_REPORT_QUERY, strQuery);

		dobReportObj.setAttributeValues(context, mpAttributeMap);
	}
	
	/**
	 * Method to get JsonObjectBuilder from JsonObject to append info to JsonObject
	 * @param jsonInputInfo : JsonObject to be converted to JsonObjectBuilder
	 * @return : JsonObjectBuilder for input JsonObject jsonInputInfo
	 */
	JsonObjectBuilder getJsonObjBuilderFromJson(JsonObject jsonInputInfo) {
		JsonObjectBuilder jsonAttrValue = Json.createObjectBuilder();
		jsonInputInfo.forEach((key, value) -> {
			jsonAttrValue.add(key, value);
		});
		return jsonAttrValue;
	}
	
	/**
	 * Method to set the attribute informations on newly created Report object
	 * @param context : Context eMatrix context object
	 * @param dobReportObject : DomainObject of Report object to set the attribute values
	 * @param strDocId : String Report Document id
	 * @param strQuery : String MQL query generated by using input and out json info
	 * @param jsonOutputArray : JsonArray with information of custom columns used as selectables in MQL query
	 * @param jsonInputArray : JsonArray with basic information to be used in MQL query
	 * @param strReportTemplateId : String Report Template id
	 * @param jsonReportInputArray : JsonArray with input information
	 * @throws FrameworkException
	 */
	void setAttributesOnReportCreation(Context context, DomainObject dobReportObject, String strDocId,
			String strQuery, JsonObject jsonInputOutputObj, String strReportTemplateId, JsonArray jsonReportInputArray) throws FrameworkException  {
		
		String strOutputId = getObjectId(context, TYPE_REPORT_TEMPLATE, NAME_REPORT_TEMPLATE, REVISION_MODEL_TEMPLATE);
		
		JsonObjectBuilder jsonAttrInfo = Json.createObjectBuilder();
		if (UIUtil.isNotNullAndNotEmpty(strDocId)) {
			jsonAttrInfo.add(KEY_TARGET_DOCUMENT_ID, strDocId);
		}
		if (UIUtil.isNotNullAndNotEmpty(strReportTemplateId)) {
			jsonAttrInfo.add(KEY_TEMPLATE_ID, strReportTemplateId);
		}
		if (UIUtil.isNotNullAndNotEmpty(strOutputId)) {
			jsonAttrInfo.add(KEY_OUTPUT_ID, strOutputId);
		}
		
		JsonArray jsonInputArray = jsonInputOutputObj.getJsonArray(KEY_INPUT_QUERY_DATA);
		JsonArray jsonOutputArray = jsonInputOutputObj.getJsonArray(KEY_REPORT_OUTPUT_COLUMN);
		jsonAttrInfo.add(KEY_PARAMETERS, jsonInputArray);
		jsonAttrInfo.add(KEY_REPORT_INPUT_ARRAY, jsonReportInputArray);
		
		Map<String, String> mpAttributeMap = new HashMap<>();
		mpAttributeMap.put(ATTRIBUTE_REPORT_INPUT_INFORMATION, jsonAttrInfo.build().toString());
		mpAttributeMap.put(ATTRIBUTE_REPORT_QUERY, strQuery);
		
		JsonObjectBuilder jsonOutputInfo = Json.createObjectBuilder();
		jsonOutputInfo.add(KEY_DATA_OUTPUT_COLUMN, jsonOutputArray);
		mpAttributeMap.put(ATTRIBUTE_REPORT_OUTPUT_DATA, jsonOutputInfo.build().toString());
		
		dobReportObject.setAttributeValues(context, mpAttributeMap);
	}
	
	/**
	 * Method to use basic TNRs in MQL query by using Pattern
	 * @param strBasicSelectValue : String TNR info
	 * @return : String TNR with Pattern
	 */
	String getBasicsPattern(String strFieldName, String strBasicSelectValue) {
		if(DomainConstants.SELECT_TYPE.equals(strFieldName)) {
			strBasicSelectValue = strBasicSelectValue.replace(",", KEY_PIPE_SEPARATOR);
		}
		StringList slBasicsValueList = StringUtil.split(strBasicSelectValue, KEY_PIPE_SEPARATOR);
		int iListSie = slBasicsValueList.size();
		if (iListSie > 0) {
			StringList slFilteredList = getUpdatedStringList(slBasicsValueList, iListSie);
			String strBasicPattern = StringUtil.join(slFilteredList, ",");
			return strBasicPattern;
		} else {
			return WILD_CARD_ASTERESK;
		}
	}

	/**
	 * Method to get StringList without empty or null values
	 * @param slBasicsValueList
	 * @param iListSie
	 * @return
	 */
	StringList getUpdatedStringList(StringList slBasicsValueList, int iListSie) {
		StringList slFilteredList = new StringList();
		for (int i = 0; i < iListSie; i++) {
			String strValue = slBasicsValueList.get(i);
			if(UIUtil.isNotNullAndNotEmpty(strValue)) {
				slFilteredList.add(strValue);
			}
		}
		return slFilteredList;
	}

	/**
	 * Method to construct where clause by iterating form input data
	 * @param strFieldName : String field name in input form
	 * @param strFieldValue : String field value in input form
	 * @param sbWhereClauseBuilder : StringBuilder to construct where clause
	 */
	void appendToWhereClauseBuilder(String strFieldName, String strFieldValue, StringBuilder sbWhereClauseBuilder) {
		if(UIUtil.isNotNullAndNotEmpty(strFieldValue)) {
			if(DomainConstants.SELECT_CURRENT.equals(strFieldName) || DomainConstants.SELECT_TYPE.equals(strFieldName)) {
				strFieldValue = strFieldValue.replace(",", KEY_PIPE_SEPARATOR);
			}
			sbWhereClauseBuilder.append("( ");
			sbWhereClauseBuilder.append(strFieldName).append(" ").append(KEY_MATCH_LIST).append(" '").append(strFieldValue).append("'");
			sbWhereClauseBuilder.append(" '").append(KEY_PIPE_SEPARATOR).append("' ");
			sbWhereClauseBuilder.append(" ) AND ");
		}
	}
	
	/**
	 * Method to format the date and append to where clause
	 * 
	 * @param strFieldName         : String field name in input form
	 * @param strFieldValue        : String field value in input form
	 * @param sbWhereClauseBuilder : StringBuilder to construct where clause
	 * @throws ParseException
	 */
	void appendToWhereClauseBuilderForDate(String strFieldName, String strFieldValue,
			StringBuilder sbWhereClauseBuilder) throws ParseException {
		if (UIUtil.isNotNullAndNotEmpty(strFieldValue)) {
			StringList slDatelist = StringUtil.split(strFieldValue, KEY_PIPE_SEPARATOR);
			int listSize = slDatelist.size();
			if (listSize > 0) {
				sbWhereClauseBuilder.append("(");
				if (listSize == 2) {
					String strDateObj1 = slDatelist.get(0);
					String strDateObj2 = slDatelist.get(1);

					appendDateWhereClause(strDateObj1, strFieldName, sbWhereClauseBuilder);
					sbWhereClauseBuilder.append(" AND ");
					appendDateWhereClause(strDateObj2, strFieldName, sbWhereClauseBuilder);
				} else {
					String strDateObj = slDatelist.get(0);
					appendDateWhereClause(strDateObj, strFieldName, sbWhereClauseBuilder);
				}
				sbWhereClauseBuilder.append(") AND ");
			}
		}
	}

	/**
	 * Method to format the date and append to where clause
	 * 
	 * @param strFieldName         : String field name in input form
	 * @param strDateObj           : String date value
	 * @param sbWhereClauseBuilder : StringBuilder to construct where clause
	 * @throws ParseException
	 */
	void appendDateWhereClause(String strDateObj, String strFieldName, StringBuilder sbWhereClauseBuilder)
			throws ParseException {
		Map<String, String> mapOperators = new HashMap<>();
		mapOperators.put(KEY_FROM, OPERATOR_FROM_DATE);
		mapOperators.put(KEY_TO, OPERATOR_TO_DATE);

		StringList slDateInfo = StringUtil.split(strDateObj, ":");
		String strDateOperator = slDateInfo.get(0);
		String strDateString = slDateInfo.get(1);

		DateFormat formatter = new SimpleDateFormat(WIDGET_DATE_FORMAT);
		Date date = formatter.parse(strDateString);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// If it is 'to' date, then take the ending time of the day i.e. 11:59:59 PM
		if (KEY_TO.equals(strDateOperator)) {
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
		}

		String strFormattedDate = convertDateToServerTimeZone(cal);
		strDateOperator = mapOperators.get(strDateOperator);
		sbWhereClauseBuilder.append(strFieldName).append(" ").append(strDateOperator).append(" ").append("'")
				.append(strFormattedDate).append("'");

	}

	/**
	 * Method to convert date to server time zone
	 * 
	 * @param cal : Calendar object for current date
	 * @return : String formatted date in server time zone
	 */
	String convertDateToServerTimeZone(Calendar cal) {
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
	 * Method to append the custom columns info as selectable info to MQL query
	 * @param jsonOutputArray : JsonArray of custom columns or selectables
	 * @param sbMQLDynamicQuery : StringBuilder of MQL query
	 * @param jsonOrderValues : JsonObjectBuilder for selectables along with their order which is used in MQL query
	 * @param sbReportColumns : StringBuilder of header info in report
	 * @param iArgOrder : int to maintain order of selectables
	 */
	void appendSelectablesToQuery(JsonArray jsonOutputArray, StringBuilder sbMQLDynamicQuery,
			JsonObjectBuilder jsonOrderValues, int iArgOrder) {
		StringBuilder sbSelect = new StringBuilder();
		StringBuilder sbSelectDynamic = new StringBuilder();
		int iOutPutArraySize = jsonOutputArray.size();
		if (iOutPutArraySize > 0) {
			sbSelect.append(" select ");
			sbSelectDynamic.append(" select ");
			sbSelect.append(DomainConstants.SELECT_ID).append(" ");
			sbSelectDynamic.append("$").append(String.valueOf(iArgOrder)).append(" ");
			jsonOrderValues.add(String.valueOf(iArgOrder), DomainConstants.SELECT_ID);
			iArgOrder++;
			for (int i = 0; i < iOutPutArraySize; i++) {
				JsonObject jsonElement = (JsonObject) jsonOutputArray.get(i);
				String strFieldName = jsonElement.getString(KEY_EXPR);
				sbSelect.append(strFieldName).append(" ");
				sbSelectDynamic.append("$").append(String.valueOf(iArgOrder)).append(" ");
				jsonOrderValues.add(String.valueOf(iArgOrder), strFieldName);
				iArgOrder++;
			}

			sbSelect.append(" dump ").append(MQL_DUMP_FORMAT);
			sbSelectDynamic.append(" dump ").append("$").append(String.valueOf(iArgOrder));
			jsonOrderValues.add(String.valueOf(iArgOrder), MQL_DUMP_FORMAT);

			sbMQLDynamicQuery.append(sbSelectDynamic);
		}
	}
		
	/**
	 * Method to generate MQL query by using input and out json with basics and select info
	 * @param jsonInputArray : JsonArray which has input form (TNR) info to generate MQL query
	 * @param jsonOutputArray : JsonArray which has out put custom columns info as selectables to generate MQL query
	 * @param strHideResults : String (true/false) to indicate whether result of this query will be shown on exported file or not
	 * @return : JsonObject json having info of generated query
	 * @throws Exception
	 */
	JsonObject generateMQLQuery(JsonArray jsonInputArray, JsonArray jsonOutputArray, String strLevel, String strHideResults) throws Exception {
		
		JsonObjectBuilder jsonQueryObject = Json.createObjectBuilder();
		JsonObjectBuilder jsonOrderValues = Json.createObjectBuilder();
		StringBuilder sbMQLDynamicQuery = new StringBuilder();
		StringBuilder sbWhereClauseBuilder = new StringBuilder();
		int iSelectOrder = 5;
		
		sbMQLDynamicQuery.append(MQL_TEMP_QUERY_SELECT);
		boolean isWhereClassExist = false;
		
		String strTypePattern = WILD_CARD_ASTERESK;
		String strNamePattern = WILD_CARD_ASTERESK;
		String strRevPattern = WILD_CARD_ASTERESK;
		
		for (int i = 0; i < jsonInputArray.size(); i++) {
			JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);
			String strFieldName = jsonElement.getString(DomainConstants.SELECT_NAME);
			String strFieldValue = jsonElement.getString(KEY_VALUE);
			String strFieldType = jsonElement.getString(DomainConstants.SELECT_TYPE);
			
			switch (strFieldType) {
				case DomainConstants.SELECT_TYPE:
					if(UIUtil.isNullOrEmpty(strFieldValue) && jsonElement.containsKey(KEY_DEFAULT_TYPES)) {
						strFieldValue = jsonElement.getString(KEY_DEFAULT_TYPES);
					}
					strTypePattern = getBasicsPattern(strFieldName, strFieldValue);
					break;
				
				case DomainConstants.SELECT_NAME:
					strNamePattern = getBasicsPattern(strFieldName, strFieldValue);
					break;
				
				case DomainConstants.SELECT_REVISION:
					strRevPattern = getBasicsPattern(strFieldName, strFieldValue);
					break;
				
				case REVISION_LATEST:
					appendToWhereClauseBuilder(REVISION_LATEST, VALUE_TRUE, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
				
				case REVISION_LAST:
					appendToWhereClauseBuilder(DomainConstants.SELECT_REVISION, REVISION_LAST, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
				
				case KEY_DATE:
					appendToWhereClauseBuilderForDate(strFieldName, strFieldValue, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;

				case KEY_QUERY:
					String  strExpression = jsonElement.getString(KEY_EXPR);
					appendToWhereClauseBuilder(strExpression, strFieldValue, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
					
				default:
					appendToWhereClauseBuilder(strFieldName, strFieldValue, sbWhereClauseBuilder);
					isWhereClassExist = true;
					break;
			}
		}
		
		StringList slBasicList = new StringList(3);
		slBasicList.add(strTypePattern);
		slBasicList.add(strNamePattern);
		slBasicList.add(strRevPattern);
		
		writeBsicsToQuery(slBasicList, sbMQLDynamicQuery, jsonOrderValues);

		String strWhereClause = "";
		if (isWhereClassExist) {
			strWhereClause = sbWhereClauseBuilder.toString();
			if (UIUtil.isNotNullAndNotEmpty(strWhereClause)) {
				strWhereClause = strWhereClause.substring(0, strWhereClause.length() - 5);
				sbMQLDynamicQuery.append("where ").append("$5");
				jsonOrderValues.add("5", strWhereClause);
				iSelectOrder++;
			}
		}
		
		appendSelectablesToQuery(jsonOutputArray, sbMQLDynamicQuery, jsonOrderValues, iSelectOrder);
				
		if(UIUtil.isNotNullAndNotEmpty(strHideResults)) {
			jsonQueryObject.add(KEY_HIDE_OUTPUT, strHideResults);
		}
		
		jsonQueryObject.add(KEY_LEVEL, strLevel);
		jsonQueryObject.add(KEY_QUERY_TYPE, VALUE_QUERY_TYPE_PRINT);
		jsonQueryObject.add(DomainConstants.SELECT_TYPE, strTypePattern);
		jsonQueryObject.add(DomainConstants.SELECT_NAME, strNamePattern);
		jsonQueryObject.add(DomainConstants.SELECT_REVISION, strRevPattern);
		jsonQueryObject.add(KEY_WHERE_CLAUSE, strWhereClause);
		jsonQueryObject.add(KEY_BUS_SELECT, jsonOutputArray);

		return jsonQueryObject.build();
		
	}
	
	/**
	 * Method to write TNR patterns to MQL query
	 * @param slBasicList : StringList with TNR info
	 * @param sbMQLDynamicQuery : StringBuilder to generate MQL query
	 * @param jsonOrderValues : JsonObjectBuilder with info of order values for selectables
	 */
	void writeBsicsToQuery(StringList slBasicList, StringBuilder sbMQLDynamicQuery,JsonObjectBuilder jsonOrderValues) {
		for(int i=0; i<slBasicList.size(); i++) {
			sbMQLDynamicQuery.append("$").append(String.valueOf(i+1)).append(" ");
			jsonOrderValues.add(String.valueOf(i+1), slBasicList.get(i));
		}
		
		sbMQLDynamicQuery.append("limit $4").append(" ");
		jsonOrderValues.add("4", MQL_QUERY_LIMIT);
	}
	
	/**
	 * Method to generate report as background Job
	 * 
	 * @param context     : Context eMatrix context object
	 * @param strReportId : String report object id
	 * @return : String status of the operation
	 * @throws Exception
	 */
	String generateReport(Context context, String strReportId) throws Exception {
		String strStatus = "";
		if (UIUtil.isNotNullAndNotEmpty(strReportId)) {
			strStatus = checkJobStatus(context, strReportId);
			String strDocumentId = getReportDocumentId(context, strReportId);
			if (UIUtil.isNullOrEmpty(strStatus) && UIUtil.isNotNullAndNotEmpty(strDocumentId)) {
				boolean isDocExist = isDocumentObjectExist(context, strDocumentId);
				if(isDocExist) {
					strStatus = runQueryAsBackgroundJob(context, strReportId, strDocumentId);
				} else {
					throw new Exception(strExceptionMessageDocNotExist);
				}
			}
		}
		return strStatus;
	}
	
	/**
	 * Method to check whether document exist in the system
	 * @param context
	 * @param strDocId
	 * @return
	 * @throws FrameworkException 
	 * @throws Exception
	 */
	boolean isDocumentObjectExist(Context context, String strDocId) throws FrameworkException  {
		boolean returnValue = true;

		String[] objIDs = new String[] { strDocId };

		MapList mapList = DomainObject.getInfo(context, objIDs, new StringList(DomainConstants.SELECT_EXISTS));

		Map<?, ?> objectInformation = (Map<?, ?>) mapList.get(0);

		returnValue = Boolean.parseBoolean((String) objectInformation.get(DomainConstants.SELECT_EXISTS));
		return returnValue;
	}
	
	/**
	 * Method to get document id
	 * @param context
	 * @param strReportId
	 * @return
	 * @throws FrameworkException 
	 */
	String getReportDocumentId(Context context, String strReportId) throws FrameworkException {
		DomainObject dobReportObject = DomainObject.newInstance(context, strReportId);
		Map<?,?> objectInfo = dobReportObject.getInfo(context, new StringList(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION));
		String strAttInputInfo = (String) objectInfo.get(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);
		String strReportDocId = "";
		 if(UIUtil.isNotNullAndNotEmpty(strAttInputInfo)) {
		    JsonObject jsonobject = getJsonFromJsonString(strAttInputInfo);
		    if(jsonobject.containsKey(KEY_TARGET_DOCUMENT_ID)) {
		    	strReportDocId = jsonobject.getString(KEY_TARGET_DOCUMENT_ID);
		    } 
		 }
		return strReportDocId;
	}

	/**
	 * Method to check whether a previous Job on Report is still running before creating a new Job
	 * @param context : Context eMatrix context object
	 * @param strReportId : String report object id
	 * @return : String status of the Job
	 * @throws FrameworkException
	 */
    String checkJobStatus(Context context, String strReportId) throws FrameworkException {
        JsonObjectBuilder jsonJobStatus = Json.createObjectBuilder();
        String strReturnStatus = "";
        DomainObject dobReportObj = DomainObject.newInstance(context, strReportId);
        Map<?, ?> mapObjInfo = dobReportObj.getInfo(context, new StringList(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION));
        String strReportInputInfo = (String) mapObjInfo.get(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);
        JsonObject jsonObject = getJsonFromJsonString(strReportInputInfo);
        if(jsonObject.containsKey(KEY_JOB_ID)) {
               String strJobId = jsonObject.getString(KEY_JOB_ID);
               DomainObject dobJobObj = DomainObject.newInstance(context, strJobId);
               String strCurrentState = dobJobObj.getInfo(context, DomainConstants.SELECT_CURRENT);
               if(!(SELECT_STATE_COMPLETED.equals(strCurrentState) || SELECT_STATE_ARCHIVED.equals(strCurrentState))) {
                      jsonJobStatus.add(KEY_STATUS, VALUE_FAILED);
                      jsonJobStatus.add(KEY_ERROR, VALUE_MSG_JOB_RUNNING);
                      strReturnStatus =  jsonJobStatus.build().toString();
               }
        }
        return strReturnStatus;
  }

	
	/**
	 * Method which creates background job object and generates report as background Job
	 * @param context : Context eMatrix context object
	 * @param strReportId : String report object id
	 * @param strDocumentId : String Doc id
	 * @return : String status of the operation
	 * @throws Exception
	 */
	String runQueryAsBackgroundJob(Context context, String strReportId, String strDocumentId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		Job bgJob = null;
		String strJobId = null;
		
		String[] strArgs = {};
		bgJob = new Job(BAKGROUND_JOB_PROGRAM, BAKGROUND_JOB_METHOD, strArgs);
		bgJob.setTitle(BAKGROUND_JOB_TITLE);
		
		bgJob.setAllowreexecute(KEY_ALLOW_REEXECUTION);
		bgJob.setRestartPoint(KEY_RESTART_POINT); // Code is working for any String value
		bgJob.setNotifyOwner(KEY_NOTIFY_OWNER);
		bgJob.create(context);
		strJobId = bgJob.getInfo(context, SELECT_PHYSICAL_ID);
		
		String[] strProgArgsArray = { strReportId, strJobId, strDocumentId };
		String strEncodedArgs = StringUtil.join(strProgArgsArray, SEPARATOR_NEW_LINE);
		
		bgJob.setAttributeValue(context, ATTRIBUTE_PROGRAM_ARGS, strEncodedArgs);
		submitJob(context, bgJob);

		output.add(KEY_JOB_STATUS, bgJob.getInfo(context, DomainConstants.SELECT_CURRENT));
		output.add(KEY_JOB_ID, strJobId);
		
		return output.build().toString();
	}
	
	/**
	 * Method which used to submit the created Job
	 * @param context : Context eMatrix context object
	 * @param bgJob : Job object needs to be submitted
	 * @throws MatrixException 
	 */
	void submitJob(Context context, Job bgJob) throws MatrixException {
		bgJob.setStartDate(context);
		bgJob.setProgressPercent(20);
		bgJob.update(context);
		bgJob.submit(context);
	}
	
	/***
	 * Get all CAT Report Reference report objects
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public String getReports(Context context) throws Exception {

		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();

		StringList objectSelects = new StringList();
		objectSelects.add(SELECT_PHYSICAL_ID);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		objectSelects.add(DomainConstants.SELECT_MODIFIED);
		objectSelects.add(SELECT_ATTRIBUTE_V_NAME);
		objectSelects.add(SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION);

		String strWhereClause = SELECT_ATTRIBUTE_REPORT_QUERY + " != ''";

		MapList mlList = DomainObject.findObjects(context, TYPE_CAT_REPORT_REFERENCE, // type pattern
				DomainConstants.QUERY_WILDCARD, // name pattern
				DomainConstants.QUERY_WILDCARD, // revision pattern
				context.getUser(), // ownerPattern
				DomainConstants.QUERY_WILDCARD, // vaultPattern
				strWhereClause, // where clause
				null, // queryName
				true, // expandType
				objectSelects, // objectSelects
				Short.parseShort("0")); // objectLimit

		if (mlList != null && !mlList.isEmpty()) {
			jsonArr = buildJsonArrayFromMaplist(context, mlList);
		}
		output.add("data", jsonArr.build());

		return output.build().toString();
	}

	/**
	 * Converts maplist data to json array
	 * @param context 
	 * @param mlList list of maps containing object details
	 * @return 
	 * @throws Exception
	 */
	private JsonArrayBuilder buildJsonArrayFromMaplist(Context context, MapList mlList)
			throws Exception {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObject = null;
		Map<?, ?> objMap = null;
		String strId = "";
		for (int i = 0; i < mlList.size(); i++) {
			jsonObject = Json.createObjectBuilder();
			objMap = (Map<?, ?>) mlList.get(i);
			
			strId = checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID));				
			
			for (Entry<?, ?> entry : objMap.entrySet()) {
				if (!FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strId), "read")) {
					if (checkNullValueforString((String) entry.getValue()).equals("#DENIED!")) {
						jsonObject.add((String) entry.getKey(),
								EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource",
										context.getLocale(), "emxFramework.Access.NoAccess"));
					}
				} else if (SELECT_ATTRIBUTE_REPORT_INPUT_INFORMATION.equals(entry.getKey())) {
					jsonObject.add(DOCUMENT_INFO,getDocumentDetails(context, (String) entry.getValue()));
					jsonObject.add(KEY_TEMPLATE_INFO,getTemplateDetails(context, (String) entry.getValue()));
				} else if (SELECT_ATTRIBUTE_V_NAME.equals(entry.getKey())) {
					jsonObject.add(KEY_TITLE, (String) entry.getValue());
				} else {
					jsonObject.add((String) entry.getKey(), checkNullValueforString((String) entry.getValue()));
				}						
			}
			jsonArr.add(jsonObject);
		}
		return jsonArr;
	}
	
	/**
	 * This method returns the template details
	 * @param context	
	 * @param strReportInputInfo
	 * @return string representation of json object containing template id and name
	 * @throws Exception
	 */
	private String getTemplateDetails(Context context, String strReportInputInfo) throws Exception {
		JsonObjectBuilder outputJson = Json.createObjectBuilder();
		JsonObject jsonObject = getJsonFromJsonString(strReportInputInfo);
		
		if (jsonObject.containsKey(KEY_TEMPLATE_ID)) {
			String strTemplateId = jsonObject.getString(KEY_TEMPLATE_ID);
			DomainObject dobTemplate = DomainObject.newInstance(context, strTemplateId);
			String strTemplatename = dobTemplate.getInfo(context, DomainConstants.SELECT_NAME);
			outputJson.add(DomainConstants.SELECT_ID, strTemplateId);
			outputJson.add(DomainConstants.SELECT_NAME, strTemplatename);
		}
		return outputJson.build().toString();
	}
	
	/***
	 * Returns the document object info and file associated with report object 
	 * @param context
	 * @param strReportInputInfo
	 * @return string representation of json object containing document and job details
	 * @throws Exception
	 */
	private String getDocumentDetails(Context context, String strReportInputInfo) throws Exception {
		JsonObjectBuilder outputJson = Json.createObjectBuilder();
		if(UIUtil.isNotNullAndNotEmpty(strReportInputInfo)) {
			JsonObject jsonObject = getJsonFromJsonString(strReportInputInfo);
			
			if (jsonObject.containsKey(KEY_TARGET_DOCUMENT_ID)) {
				String strDocId = jsonObject.getString(KEY_TARGET_DOCUMENT_ID);
				boolean isDocExist = isDocumentObjectExist(context, strDocId);
				if(isDocExist) {
					DomainObject domDoc = DomainObject.newInstance(context, strDocId);
					
					Map<?, ?> dataMap = domDoc.getInfo(context, getDocumentInfoSelect());
					
					outputJson.add(SELECT_PHYSICAL_ID, (String) dataMap.get(SELECT_PHYSICAL_ID));
					outputJson.add(DomainConstants.SELECT_ID, (String) dataMap.get(DomainConstants.SELECT_ID));
					outputJson.add(DomainConstants.SELECT_NAME, (String) dataMap.get(DomainConstants.SELECT_NAME));
					outputJson.add(FILE_NAME, ((StringList) dataMap.get("format.file.name")).get(0));
				}
			}
			
			if (jsonObject.containsKey(KEY_JOB_ID)) {
				String strJobId = jsonObject.getString(KEY_JOB_ID);
				outputJson.add(KEY_JOB_ID, strJobId);
				
				DomainObject jobObject = DomainObject.newInstance(context, strJobId);
				StringList slJobSelect = new StringList(3);
				slJobSelect.add(DomainConstants.SELECT_CURRENT);
				slJobSelect.add(SELECT_ATTRIBUTE_COMPLETION_STATUS);
				slJobSelect.add(SELECT_ATTRIBUTE_ERROR_MESSAGE);
				Map<?, ?> jobMap = jobObject.getInfo(context,slJobSelect);

				outputJson.add(KEY_JOB_STATUS,(String)jobMap.get(DomainConstants.SELECT_CURRENT));
				outputJson.add(KEY_JOB_COMPLETION_STATUS,(String)jobMap.get(SELECT_ATTRIBUTE_COMPLETION_STATUS));
				outputJson.add(KEY_JOB_ERROR_MESSAGE,(String)jobMap.get(SELECT_ATTRIBUTE_ERROR_MESSAGE));

			}
		}
		return outputJson.build().toString();
	}
	
	/**
	 * Check value is null or Empty and if MultiValue character(\u0007) present
	 * then replace with comma	 * 
	 * @param strString
	 * @return
	 */
	private static String checkNullValueforString(String strString) {
		if (strString != null && strString.contains("\u0007")) {
			strString = strString.replaceAll("\u0007", " , ");
		}
		return null != strString ? strString : "";
	}
	
	/**
	 * return selectables for Document object
	 * @return list of all selectables
	 */
	private StringList getDocumentInfoSelect() {
		StringList var1 = new StringList();
		var1.add(SELECT_PHYSICAL_ID);
		var1.add("id");
		var1.add("type");
		var1.add("revision");		
		var1.add("format.file.name");
		var1.add("format.file.format");	
		var1.add(DomainConstants.SELECT_NAME);	
		return var1;
	}
	
	
	/**
	 * This method returns the column details present in pgCustomDynamicColumnDetails page file
	 * @param context
	 * @return return jsonarray of label and expression of all columns
	 * @throws Exception
	 */
	public String getSystemColumns(Context context) throws Exception {
		MapList customColumnList = getExpressionListForCustomColumn(context, "pgProductDataSearchTable",
				"pgCustomDynamicColumnDetails");		
		JsonArrayBuilder outputArray = convertMapListToJsonFlatTable(customColumnList, 0);		
		return outputArray.build().toString();		
	}	
	
	/**
	 * Builds maplist of column information of given table of page file
	 * @param context
	 * @param strCurrentTable table name 
	 * @param strCustomTableConfigXML page file object 
	 * @return maplist of column information
	 * @throws Exception
	 */
	public MapList getExpressionListForCustomColumn(Context context,				
			String strCurrentTable,
			String strCustomTableConfigXML) throws Exception {		
		
		MapList expList = new MapList();
		try
		{
			String languageStr = context.getLocale().getDisplayName();
			
			SAXBuilder localSAXBuilder = new SAXBuilder();
			
			StringList strCurrentTableList = FrameworkUtil.split(strCurrentTable, "~");
			strCurrentTable = strCurrentTableList.get(strCurrentTableList.size() - 1);
			
			Page page= new Page(strCustomTableConfigXML);
			page.open(context);
			strCustomTableConfigXML = page.getContents(context);
			page.close(context);
			Document document = localSAXBuilder.build(new StringReader(strCustomTableConfigXML));
			XPath xpath = XPath.newInstance("/CUSTOM_TABLE_VIEW/TABLE[@name='" + strCurrentTable + "']");
			Element elemTable = (Element) xpath.selectSingleNode(document);
			
			if (elemTable == null) {
				xpath = XPath.newInstance("/CUSTOM_TABLE_VIEW/TABLE[@name='default']");
				elemTable = (Element) xpath.selectSingleNode(document);
			}
			
			List<?> listElemColumns = elemTable.getChildren("COLUMN");
			Element elemColumn;
			String strDisplayLabel = "";			
			String strLabel = "";
			String strExpr = "";
			String strExprSelect = "";
			HashMap<Object, Object> columnMap = null;
			Element elemColSettings = null;
			String strColRegSuite = "";
			for (int m = 0, list3Size = listElemColumns.size(); m < list3Size; m++) {
				elemColumn = (Element) listElemColumns.get(m);
				xpath = XPath.newInstance("./SETTINGS[@name='Registered Suite']");
				elemColSettings = (Element) xpath.selectSingleNode(elemColumn);
				strColRegSuite = elemColSettings.getText();
				if(UIUtil.isNullOrEmpty(strColRegSuite)){
					strColRegSuite = "Framework";
				}
				
				strLabel = elemColumn.getChildTextTrim("LABEL");
				strExpr = elemColumn.getChildTextTrim("EXPRESSION");
				strExprSelect = elemColumn.getChildTextTrim("EXPRESSION_SELECT");
				strDisplayLabel = EnoviaResourceBundle.getProperty(context, strColRegSuite, strLabel, languageStr);
				
				if("dummy".equals(strExpr)) {
					strExpr = elemColumn.getChildTextTrim("PROGRAMEXPRESSION");
				}
				
				columnMap = new HashMap<>();
				columnMap.put("type", KEY_BUS_SELECT);
				columnMap.put(KEY_EXPR, strExpr);
				columnMap.put(KEY_LABEL, strDisplayLabel);	
				if(UIUtil.isNotNullAndNotEmpty(strExprSelect)) {
					columnMap.put(KEY_EXPR_SELECT, strExprSelect);
				} else {
					columnMap.put(KEY_EXPR_SELECT, strExpr);
				}
				expList.add(columnMap);
			}
			expList.sort(KEY_LABEL, "ascending", "String");			
		}
		catch (Exception e) {
			throw e;
		}
		return expList;
	}	
	
	/**
	 * This method converts a MapList which is a result of getRelatedObjects API, to
	 * a JsonArray
	 * 
	 * @param mlInput : MapList which is result of getRelatedObjects operation while
	 *                expanding RSP till 'Test Case' and from 'Test Case' till
	 *                simulation
	 * @param iLevel  : int current level
	 * @return : JsonArray of expanded data
	 */
	JsonArrayBuilder convertMapListToJsonFlatTable(MapList mlInput, int iLevel) {
		JsonArrayBuilder jsonObjArray = Json.createArrayBuilder();

		try {
			Stack<JsonObject> stackJsonObj = new Stack<>();
			int nLevelCurrent = 1;
			int nLevelPrev = 1;
			for (Iterator<?> iterator = mlInput.iterator(); iterator.hasNext();) {

				Map<?, ?> tempMap = (Map<?, ?>) iterator.next();

				// Leaf level node condition
				if (nLevelCurrent <= nLevelPrev) {
					if (!stackJsonObj.empty()) {
						jsonObjArray.add(stackJsonObj.pop());
					}

					for (int i = nLevelCurrent; i < nLevelPrev; i++) {
						stackJsonObj.pop();
					}
				}

				stackJsonObj.push(convertMapToJsonObj(
						stackJsonObj.empty() ? Json.createObjectBuilder().build() : stackJsonObj.peek(), tempMap));

				nLevelPrev = nLevelCurrent;
			}
			if (!stackJsonObj.empty()) {
				jsonObjArray.add(stackJsonObj.pop());
			}
		} catch (Exception e) {
			throw e;
		}
		return jsonObjArray;
	}
	/**
	 * Method used convert Map to JsonObject
	 * @param inputJsonObj : JsonObject in which converted JsonObject from Map will be merged
	 * @param inputMap : Map to converted to JsonObject
	 * @return : JsonObject merged with converted Map and parent inputJsonObj
	 */
	JsonObject convertMapToJsonObj(JsonObject inputJsonObj, Map<?, ?> inputMap) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		inputMap.entrySet().forEach(e -> builder.add((String)e.getKey(), ((e.getValue() instanceof StringList) ? getStringFromSL((StringList) e.getValue()) : (String)e.getValue())));
		return builder.build();
	}
	/**
	 * This method is used to convert StringList to pipe separated String
	 * @param slSelectValuelist : StringList to be converted
	 * @return : Pipe line separated String
	 */
	String getStringFromSL(StringList slSelectValuelist) {
		
		StringBuilder sbSelectValue = new StringBuilder();
		String strSelectValue = "";
		String strReturnValue = "";
		for(int i=0; i<slSelectValuelist.size(); i++ ) {
			strSelectValue = slSelectValuelist.get(i);
			sbSelectValue.append(strSelectValue).append(KEY_PIPE_SEPARATOR);
		}
		
		strReturnValue = sbSelectValue.toString();
		
		if (UIUtil.isNotNullAndNotEmpty(strReturnValue)) {
			strReturnValue = strReturnValue.substring(0, strReturnValue.length() - 1);
		}
		
		return strReturnValue;
	}
	
	
}
