/*
 * PGReportTemplateUtil.java
 * 
 * Added by Dashboard Team
 * For Report Template Widget related Web services
 * 
 */

package com.pg.widgets.reportgenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.productline.UnifiedAutonamingServices;

import matrix.db.Context;
import matrix.db.RelationshipTypeList;
import matrix.db.RelationshipType;
import matrix.db.AttributeTypeList;
import matrix.db.AttributeType;
import matrix.db.BusinessType;
import matrix.db.BusinessTypeList;
import matrix.db.Vault;

import matrix.util.MatrixException;
import matrix.util.StringList;

/**
 * Class PGReportTemplateUtil has all the web service methods required for
 * report template widget
 * 
 * @since 2018x.5
 * @author
 *
 */

class PGReportTemplateUtil {

	 static final String KEY_DENIED  = "#DENIED!";
	 static final String KEY_NO_ACCESS  = "No Access";
	 static final String  REPORT_TEMPLATE_PREFIX = "ReportModel";
	 static final String POLICY_REPORT_MODEL = PropertyUtil.getSchemaProperty(null, "policy_pgMBDReportModel");
	 static final String STATE_OBSOLETE = PropertyUtil.getSchemaProperty(null, DomainConstants.SELECT_POLICY, POLICY_REPORT_MODEL, "state_Obsolete");
	 String strExceptionMessage = "Exception occurred during the operation, please check the logs !!!";
	 String strExceptionMessageObsoleteState = "Selected object is already in Obsolete state";
	 
	 static final String PIPE_SEPARATOR = "|";
	 static final String NEWLINE_SEPARATOR = "\n";
	 static final String KEY_ATTRIBUTE = "attribute";
	 static final String SELECT_ADMIN_ATTRIBUTE = "Attribute";
	 static final String PREFIX_EMX_FRAMEWORK = "emxFramework";
	 static final String KEY_ALL_TYPES = "allTypes";
	 static final String KEY_SELECTED_TYPES = "selectedTypes";
	 static final String KEY_SELECTED_ATTRIBUTES = "selectedAttributes";
	 static final String TABLE_COLUMNS = "Table_Columns";
	 static final String KEY_BASIC = "basicColumns";
	 static final String KEY_TYPE_DATE = "timestamp";
	 static final String KEY_TYPE_STRING = "string";
	 static final String ATTRIBUTE_INFO_SEPARATOR = "<@ATT@>";
	 static final String ATTRIBUTE_RANGE_SEPARATOR = "<@RNG@>";
	 static final String KEY_RANGE = "range";
	 static final String SHOW_OBSOLETE = "showObsolete";
	 static final String KEY_RELATIONSHIP_TYPE = "Relationship";
	 static final String KEY_ADMIN_TYPE = "adminType";
  	 static final String VALUE_BUS_SELECT = "busselect";
	 static final String VALUE_REL_SELECT = "relselect";
	 static final String KEY_REL_ATTRIUTES = "RelationshipAttributes";
	 static final String KEY_SHOW_PG_CUSTOM_COLUMNS = "showPGCustomColumns";
	 static final String KEY_OUTPUT_COLUMNS = "outputColumns";

	/**
	 * Method to list all the Report Model (Template) objects to which user has access
	 * @param context : Context eMatrix context object
	 * @param strObjSelects : String pipe separated object selects
	 * @return : String json with templates info list
	 * @throws Exception
	 */
	public String getReportTemplates(Context context, String strObjSelects, boolean selectObsoleteData) throws Exception {
		
		JsonObjectBuilder output = Json.createObjectBuilder();		
		try {
			
			StringList objectSelects = StringUtil.split(strObjSelects, PIPE_SEPARATOR);
			objectSelects.add(PGReportGeneratorUtil.SELECT_PHYSICAL_ID);
			objectSelects.add(DomainConstants.SELECT_CURRENT);
			objectSelects.add(DomainConstants.SELECT_ID);
			
			StringBuilder sbWhereClause = new StringBuilder();
			sbWhereClause.append(PGReportGeneratorUtil.SELECT_ATTRIBUTE_REPORT_FORM_DATA + " != ''");
			
			if(!selectObsoleteData) {
				sbWhereClause.append(" && ").append(DomainConstants.SELECT_CURRENT).append(" != ").append(STATE_OBSOLETE);
			}

			MapList mlList = DomainObject.findObjects(context, 
					PGReportGeneratorUtil.TYPE_REPORT_MODEL, 			//type pattern
					DomainConstants.QUERY_WILDCARD, 					//name pattern
					DomainConstants.QUERY_WILDCARD, 					//revision pattern
					null, 												// ownerPattern
					DomainConstants.QUERY_WILDCARD, 					// vaultPattern
					sbWhereClause.toString(), 							// where clause
					null,												// queryName
					true,												// expandType
					objectSelects,										// objectSelects
					Short.parseShort("0"));    							// objectLimit

			mlList.addSortKey(DomainConstants.SELECT_ATTRIBUTE_TITLE, null, null);
			mlList.sort();
			
			JsonArray jsonTemplateArray = getReportTemplateDetails(mlList, objectSelects);

			output.add("data", jsonTemplateArray);
		} catch (MatrixException e) {
			output.add(PGReportGeneratorUtil.KEY_STATUS, PGReportGeneratorUtil.VALUE_FAILED);
			if(UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			output.add(PGReportGeneratorUtil.KEY_ERROR, strExceptionMessage);
		}
		return output.build().toString();
	}

	/**
	 * Method to get the info of Template object
	 * 
	 * @param mlList        : MapList with template object details
	 * @param objectSelects : StringList object selects
	 * @return : JsonArray with template objects info
	 */
	JsonArray getReportTemplateDetails(MapList mlList, StringList objectSelects) {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		for (int i = 0; i < mlList.size(); i++) {
			Map<?, ?> objMap = (Map<?, ?>) mlList.get(i);
			JsonObjectBuilder jsonObjElem = Json.createObjectBuilder();
			for (int j = 0; j < objectSelects.size(); j++) {
				String strSelectkey = objectSelects.get(j);
				String strTemplateInfo = (String) objMap.get(strSelectkey);
				if (KEY_DENIED.equals(strTemplateInfo)) {
					jsonObjElem.add(strSelectkey, KEY_NO_ACCESS);
				} else {
					jsonObjElem.add(strSelectkey, strTemplateInfo);
				}
			}
			jsonArr.add(jsonObjElem);
		}
		return jsonArr.build();
	}

	/**
	 * Method to create Report Model (Template) object
	 * 
	 * @param context
	 * @param strInputInfo
	 * @return
	 * @throws MatrixException
	 */
	String createReportTemplate(Context context, String strData) throws MatrixException {	
		
		PGReportGeneratorUtil objReportGeneratorUtil = new PGReportGeneratorUtil();
		JsonObject jsonObject = objReportGeneratorUtil.getJsonFromJsonString(strData);
		String strObjectSelects = "";
		if (jsonObject.containsKey(TABLE_COLUMNS)) {
			strObjectSelects = jsonObject.getString(TABLE_COLUMNS);
		}		
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strName = REPORT_TEMPLATE_PREFIX
				+ UnifiedAutonamingServices.autoname(context, PGReportGeneratorUtil.TYPE_REPORT_MODEL);
		DomainObject dobReportTemplateObj = DomainObject.newInstance(context);

		dobReportTemplateObj.createObject(context, PGReportGeneratorUtil.TYPE_REPORT_MODEL, strName,
				PGReportGeneratorUtil.REVISION_MODEL_TEMPLATE, POLICY_REPORT_MODEL, context.getVault().getName());
		dobReportTemplateObj.promote(context);

		setReportTemplateAttributes(context, jsonObject, dobReportTemplateObj);

		getObjectSelectsDetails(context, strObjectSelects, output, dobReportTemplateObj);

		return output.build().toString();
	}

	/**
	 * Method to get object selects information for created Template object
	 * 
	 * @param context
	 * @param strObjectSelects
	 * @param output
	 * @param dobReportTemplateObj
	 * @throws FrameworkException
	 */
	void getObjectSelectsDetails(Context context, String strObjectSelects, JsonObjectBuilder output,
			DomainObject dobReportTemplateObj) throws FrameworkException {
		StringList objectSelects = StringUtil.split(strObjectSelects, PIPE_SEPARATOR);
		objectSelects.add(PGReportGeneratorUtil.SELECT_PHYSICAL_ID);
		Map<?, ?> objectSelectsMap = dobReportTemplateObj.getInfo(context, objectSelects);
		for (int i = 0; i < objectSelects.size(); i++) {
			String strSelectKey = objectSelects.get(i);
			String strSelectValue = (String) objectSelectsMap.get(strSelectKey);
			output.add(strSelectKey, strSelectValue);
		}
	}

	/**
	 * Method to set the attribute info on template objects
	 * 
	 * @param context
	 * @param strInputInfo
	 * @param dobReportTemplateObj
	 * @throws FrameworkException
	 */
	void setReportTemplateAttributes(Context context, JsonObject jsonObject, DomainObject dobReportTemplateObj)
			throws FrameworkException {
		String strTitle = "";
		String strDesc = "";
		if (jsonObject.containsKey(DomainConstants.ATTRIBUTE_TITLE)) {
			strTitle = jsonObject.getString(DomainConstants.ATTRIBUTE_TITLE);
		}
		if (jsonObject.containsKey(DomainConstants.SELECT_DESCRIPTION)) {
			strDesc = jsonObject.getString(DomainConstants.SELECT_DESCRIPTION);
		}

		JsonArray jsonInputFormDataArray = jsonObject.getJsonArray(PGReportGeneratorUtil.KEY_INPUT);
		JsonObjectBuilder jsonOutputColumns = Json.createObjectBuilder();
		JsonArray jsonOutputAvailabelColumns = jsonObject.getJsonArray(PGReportGeneratorUtil.KEY_OUTPUT);
		jsonOutputColumns.add(KEY_SHOW_PG_CUSTOM_COLUMNS, "false");
		jsonOutputColumns.add(KEY_OUTPUT_COLUMNS, jsonOutputAvailabelColumns);

		Map<String, String> mpAttributeMap = new HashMap<>();
		mpAttributeMap.put(PGReportGeneratorUtil.ATTRIBUTE_REPORT_FORM_DATA, jsonInputFormDataArray.toString());
		mpAttributeMap.put(PGReportGeneratorUtil.ATTRIBUTE_REPORT_OUTPUT_DATA, jsonOutputColumns.build().toString());
		mpAttributeMap.put(DomainConstants.ATTRIBUTE_TITLE, strTitle);

		dobReportTemplateObj.setDescription(context, strDesc);
		dobReportTemplateObj.setAttributeValues(context, mpAttributeMap);
	}
	
	/**
	 * Method to set the state of the Report Template to Obsolete on delete
	 * operation
	 * 
	 * @param context
	 * @param strReportTemplateId
	 * @return
	 * @throws Exception
	 */
	String deleteReportTemplate(Context context, String strReportTemplateId) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		try {
			if (UIUtil.isNotNullAndNotEmpty(strReportTemplateId)) {
				DomainObject dobReportTemplateObj = DomainObject.newInstance(context, strReportTemplateId);
				String strCurrentState = dobReportTemplateObj.getInfo(context, DomainConstants.SELECT_CURRENT);
				if(STATE_OBSOLETE.equals(strCurrentState)) {
					jsonStatus.add(PGReportGeneratorUtil.KEY_STATUS, PGReportGeneratorUtil.VALUE_FAILED);
					jsonStatus.add(PGReportGeneratorUtil.KEY_ERROR, strExceptionMessageObsoleteState);
				} else {
					dobReportTemplateObj.setState(context, STATE_OBSOLETE);
					jsonStatus.add(PGReportGeneratorUtil.KEY_STATUS, PGReportGeneratorUtil.VALUE_SUCCESS);
				}
			}
			return jsonStatus.build().toString();
		} catch (Exception e) {
			jsonStatus.add(PGReportGeneratorUtil.KEY_STATUS, PGReportGeneratorUtil.VALUE_FAILED);
			if (UIUtil.isNotNullAndNotEmpty(e.getMessage())) {
				strExceptionMessage = e.getMessage();
			}
			jsonStatus.add(PGReportGeneratorUtil.KEY_ERROR, strExceptionMessage);
			throw e;
		}
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	JsonArray getAllSchemaInfo(Context context, String strAdminObjType, String strPropertyKey) throws MatrixException {
		JsonArrayBuilder jsonSchemaArray = Json.createArrayBuilder();
		String strLanguage = context.getLocale().getLanguage();
		StringList slAdminObjList = getAllAdminObjects(context, strAdminObjType);
		
		for(int i=0; i<slAdminObjList.size(); i++) {
			String strActualName = slAdminObjList.get(i);
			String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context, strPropertyKey, strActualName, strLanguage);
			
			if(UIUtil.isNullOrEmpty(strDisplayName) || strDisplayName.startsWith(PREFIX_EMX_FRAMEWORK)) {
				strDisplayName = strActualName;
			}
			JsonObjectBuilder jsonAdminObj = Json.createObjectBuilder();
			jsonAdminObj.add(PGReportGeneratorUtil.KEY_VALUE, strActualName);
			jsonAdminObj.add(DomainConstants.SELECT_NAME, strDisplayName);
			jsonSchemaArray.add(jsonAdminObj);
		}
		
		return jsonSchemaArray.build();
	}
	
	/**
	 * Method to return all admin Types and Relationships names
	 * @param context
	 * @return
	 * @throws MatrixException 
	 */
	String getAllTypesAndRels(Context context) throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		
		JsonArray jsonTypeArray = getAllSchemaInfo(context, DomainConstants.SELECT_TYPE, PGReportGeneratorUtil.SELECT_ADMIN_TYPE);
		output.add(DomainConstants.SELECT_TYPE, jsonTypeArray);
		
		JsonArray jsonRelArray = getAllSchemaInfo(context, DomainConstants.KEY_RELATIONSHIP, KEY_RELATIONSHIP_TYPE);
		output.add(DomainConstants.KEY_RELATIONSHIP, jsonRelArray);
		
		return output.build().toString();
	}
		
	/**
	 * 
	 * @param context
	 * @param strData
	 * @return
	 * @throws MatrixException
	 */
	String getTypeAttributeMapping(Context context, String strData) throws MatrixException {
		JsonObjectBuilder jsonTypeAttributes = Json.createObjectBuilder();
		setBasicFields(jsonTypeAttributes);
		
		PGReportGeneratorUtil objReportGeneratorUtil = new PGReportGeneratorUtil();
		JsonObject jsonObject = objReportGeneratorUtil.getJsonFromJsonString(strData);
		
		if(jsonObject.containsKey(DomainConstants.SELECT_TYPE)) {
			String strTypes = jsonObject.getString(DomainConstants.SELECT_TYPE);
			StringList slTypeList = StringUtil.split(strTypes, PIPE_SEPARATOR);
			setTypAndRelAttributes(context, slTypeList, jsonTypeAttributes, VALUE_BUS_SELECT);
		}

		if(jsonObject.containsKey(DomainConstants.KEY_RELATIONSHIP)) {
			JsonObjectBuilder jsonRelAttributes = Json.createObjectBuilder();
			String strRelationships = jsonObject.getString(DomainConstants.KEY_RELATIONSHIP);
			StringList slRelList = StringUtil.split(strRelationships, PIPE_SEPARATOR);
			setTypAndRelAttributes(context, slRelList, jsonRelAttributes, VALUE_REL_SELECT);
			jsonTypeAttributes.add(KEY_REL_ATTRIUTES, jsonRelAttributes);
		}

		return jsonTypeAttributes.build().toString();
	}
		
	/**
	 * 
	 * @param context
	 * @param slTypeList
	 * @param jsonTypeAttributes
	 * @param valueBusSelect
	 * @throws MatrixException 
	 */
	void setTypAndRelAttributes(Context context, StringList slTypeList, JsonObjectBuilder jsonTypeAttributes,
			String strAdminType) throws MatrixException {
		String strLanguage = context.getLocale().getLanguage();
		String strPersonVault = PersonUtil.getDefaultVault(context);
		
		for (int i = 0; i < slTypeList.size(); i++) {
			String strTypeActualName = slTypeList.get(i);
			JsonArray jsonCurrentTypeAttributes = getAttributesJson(context, strTypeActualName, strLanguage, strPersonVault, strAdminType);
			jsonTypeAttributes.add(strTypeActualName, jsonCurrentTypeAttributes);
		}
		
	}
	
	/**
	 * Method to set basic information on type attribute mapping json
	 * @param jsonTypeAttributes
	 */
	
	String getTypeAttributeMappingBasic(Context context) throws MatrixException {
		JsonObjectBuilder jsonTypeAttributes = Json.createObjectBuilder();
		setBasicFields(jsonTypeAttributes);
		return jsonTypeAttributes.build().toString();
	}
	void setBasicFields(JsonObjectBuilder jsonTypeAttributes) {
		JsonArrayBuilder jsonBasicArray = Json.createArrayBuilder();

		Map<String, String> mapBasics = getBasicMap();
		for (Entry<String, String> entry : mapBasics.entrySet()) {
			String strKey = entry.getKey();
			JsonObjectBuilder jsonBasicObj = Json.createObjectBuilder();
			jsonBasicObj.add(PGReportGeneratorUtil.KEY_NAME, strKey);
			jsonBasicObj.add(PGReportGeneratorUtil.KEY_VALUE, entry.getValue());
			if(strKey.equals(DomainConstants.SELECT_ORIGINATED) || strKey.equals(DomainConstants.SELECT_MODIFIED)) {
				jsonBasicObj.add(DomainConstants.SELECT_TYPE, KEY_TYPE_DATE);
			} else {
				jsonBasicObj.add(DomainConstants.SELECT_TYPE, KEY_TYPE_STRING);
			}
			jsonBasicArray.add(jsonBasicObj);
		}

		jsonTypeAttributes.add(KEY_BASIC, jsonBasicArray.build());
	}
	
	/**
	 * Method to return generic map with basic info
	 * @return
	 */
	Map<String, String> getBasicMap() {
		Map<String, String> mapBasics = new HashMap<>();
		mapBasics.put(DomainConstants.ATTRIBUTE_TITLE, DomainConstants.SELECT_ATTRIBUTE_TITLE);
		mapBasics.put(DomainConstants.SELECT_DESCRIPTION, DomainConstants.SELECT_DESCRIPTION);
		mapBasics.put(DomainConstants.SELECT_CURRENT, DomainConstants.SELECT_CURRENT);
		mapBasics.put(DomainConstants.SELECT_OWNER, DomainConstants.SELECT_OWNER);
		mapBasics.put(DomainConstants.SELECT_ORIGINATED, DomainConstants.SELECT_ORIGINATED);
		mapBasics.put(DomainConstants.SELECT_MODIFIED, DomainConstants.SELECT_MODIFIED);
		return mapBasics;
	}

	/**
	 * 
	 * @param context
	 * @param strTypeActualName
	 * @param strLanguage
	 * @return
	 * @throws MatrixException
	 */
	JsonArray getAttributesJson(Context context, String strActualName, String strLanguage,
			String strPersonVault, String strAdminType) throws MatrixException {
		JsonArrayBuilder jsonAttributeArray = Json.createArrayBuilder();
		AttributeTypeList attrTypeList = getAttributeListForTypeAndRel(context, strActualName, strPersonVault, strAdminType);
		TreeSet<String> attributeInfoSet = new TreeSet<>();
		for (int i = 0; i < attrTypeList.size(); i++) {
			AttributeType attrType = attrTypeList.get(i);
			String strAttributeName = attrType.getName();
			String strAttrType = attrType.getDataType(context);
			StringList slAttrRangeList = attrType.getChoices();
			String strAttrRanges = "";
			if(slAttrRangeList != null && !slAttrRangeList.isEmpty()) {
				strAttrRanges = StringUtil.join(slAttrRangeList, ATTRIBUTE_RANGE_SEPARATOR);
			}

			String strAttrDisplayName = EnoviaResourceBundle.getAdminI18NString(context, SELECT_ADMIN_ATTRIBUTE,
					strAttributeName, strLanguage);

			if (UIUtil.isNullOrEmpty(strAttrDisplayName) || strAttrDisplayName.startsWith(PREFIX_EMX_FRAMEWORK)) {
				strAttrDisplayName = strAttributeName;
			}
			StringBuilder sbAttrBuilder = new StringBuilder();
			sbAttrBuilder.append(strAttrDisplayName).append(ATTRIBUTE_INFO_SEPARATOR).append(strAttributeName)
					.append(ATTRIBUTE_INFO_SEPARATOR).append(strAttrType).append(ATTRIBUTE_INFO_SEPARATOR)
					.append(strAttrRanges).append(ATTRIBUTE_INFO_SEPARATOR).append(strAdminType);
			attributeInfoSet.add(sbAttrBuilder.toString());
		}

		writeSortedAttributeInfoJson(attributeInfoSet, jsonAttributeArray, strLanguage);

		return jsonAttributeArray.build();
	}

	/**
	 * Method to get the AttributeTypeList for type and relationship attributes
	 * @param context
	 * @param strActualName
	 * @param strPersonVault
	 * @param strAdminType
	 * @return
	 * @throws MatrixException
	 */
	@SuppressWarnings("deprecation")
	AttributeTypeList getAttributeListForTypeAndRel(Context context, String strActualName,
			String strPersonVault, String strAdminType) throws MatrixException {
		if(VALUE_BUS_SELECT.equals(strAdminType)) {
			BusinessType busTypeObj = new BusinessType(strActualName, new Vault(strPersonVault));
			AttributeTypeList attrTypeList = busTypeObj.getAttributeTypes(context, false);
			return attrTypeList;
		} else {
			RelationshipType relType = new RelationshipType(strActualName);
			AttributeTypeList attrTypeList = relType.getAttributeTypes(context);
			return attrTypeList;
		}

	}

	/**
	 * 
	 * @param attributeInfoSet
	 * @param jsonAttributeArray
	 * @throws MatrixException 
	 */
	void writeSortedAttributeInfoJson(TreeSet<String> attributeInfoSet, JsonArrayBuilder jsonAttributeArray, String strLanguage) throws MatrixException {
		Iterator<String> attributeSetIter = attributeInfoSet.iterator();
		while (attributeSetIter.hasNext()) {
			String strAttrInfo = attributeSetIter.next();
			String[] strAttrInfoArray = strAttrInfo.split(Pattern.quote(ATTRIBUTE_INFO_SEPARATOR), -1);
			if(strAttrInfoArray.length > 4) {
				JsonObjectBuilder jsonAttributeObj = Json.createObjectBuilder();
				jsonAttributeObj.add(PGReportGeneratorUtil.KEY_VALUE, DomainObject.getAttributeSelect(strAttrInfoArray[1]));
				jsonAttributeObj.add(PGReportGeneratorUtil.KEY_NAME, strAttrInfoArray[0]);
				jsonAttributeObj.add(DomainConstants.SELECT_TYPE, strAttrInfoArray[2]);
				
				String strAttrRanges = strAttrInfoArray[3];
				if(UIUtil.isNotNullAndNotEmpty(strAttrRanges)) {
					JsonArray jsonRangeInfoArray = getAttributeRangeDetails(strAttrRanges, strAttrInfoArray[1], strLanguage);
					jsonAttributeObj.add(KEY_RANGE, jsonRangeInfoArray);
				}
				jsonAttributeObj.add(KEY_ADMIN_TYPE, strAttrInfoArray[4]);
				jsonAttributeArray.add(jsonAttributeObj);
			}
		}
	}

	/**
	 * 
	 * @param strAttrRanges
	 * @param strAttrName
	 * @param strLanguage
	 * @return
	 * @throws MatrixException
	 */
	JsonArray getAttributeRangeDetails(String strAttrRanges, String strAttrName, String strLanguage)
			throws MatrixException {
		JsonArrayBuilder jsonRangeArray = Json.createArrayBuilder();
		String[] strAttrInfoArray = strAttrRanges.split(Pattern.quote(ATTRIBUTE_RANGE_SEPARATOR), -1);
		for (int i = 0; i < strAttrInfoArray.length; i++) {
			JsonObjectBuilder jsonRangeObj = Json.createObjectBuilder();
			String strRangeActualName = strAttrInfoArray[i];
			String strRangeDisplayName = i18nNow.getRangeI18NString(strAttrName, strRangeActualName, strLanguage);
			if (UIUtil.isNullOrEmpty(strRangeDisplayName) || strRangeDisplayName.startsWith(PREFIX_EMX_FRAMEWORK)) {
				strRangeDisplayName = strRangeActualName;
			}
			jsonRangeObj.add(PGReportGeneratorUtil.KEY_NAME, strRangeDisplayName);
			jsonRangeObj.add(PGReportGeneratorUtil.KEY_VALUE, strRangeActualName);
			jsonRangeArray.add(jsonRangeObj);
		}

		return jsonRangeArray.build();
	}

	/**
	 * 
	 * @param context
	 * @param strAdminObjName
	 * @return
	 * @throws MatrixException 
	 */
	StringList getAllAdminObjects(Context context, String strAdminObjName) throws MatrixException {
		StringList slAdminObjList = new StringList();
		if(DomainConstants.SELECT_TYPE.equals(strAdminObjName)) {
			BusinessTypeList typeList = BusinessType.getBusinessTypes(context);
			for(int i=0; i<typeList.size();i++) {
				BusinessType busType = typeList.get(i);
				slAdminObjList.add(busType.getName());
			}
		} else {
			RelationshipTypeList relList = RelationshipType.getRelationshipTypes(context);
			for(int i=0; i<relList.size();i++) {
				RelationshipType relType = relList.get(i);
				slAdminObjList.add(relType.getName());
			}
		}
		
		return slAdminObjList;
	}
	
	/**
	 * Method to get the details of Report Template object
	 * 
	 * @param context
	 * @param strReportTemplateId
	 * @return
	 * @throws MatrixException
	 */
	String getReportTemplateDetails(Context context, String strReportTemplateId, boolean fromPropertiesPage)
			throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		PGReportGeneratorUtil objReportGeneratorUtil = new PGReportGeneratorUtil();
		DomainObject dobReportTemplateObj = DomainObject.newInstance(context, strReportTemplateId);
		StringBuilder sbObjSelect = new StringBuilder();
		if (fromPropertiesPage) {
			sbObjSelect.append(DomainConstants.SELECT_NAME).append(PIPE_SEPARATOR);
			sbObjSelect.append(DomainConstants.SELECT_CURRENT).append(PIPE_SEPARATOR);
		} else {
			JsonArray jsonTypeArray = getAllSchemaInfo(context, DomainConstants.SELECT_TYPE, PGReportGeneratorUtil.SELECT_ADMIN_TYPE);
			output.add(KEY_ALL_TYPES, jsonTypeArray);
		}
		sbObjSelect.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(PIPE_SEPARATOR);
		sbObjSelect.append(DomainConstants.SELECT_DESCRIPTION).append(PIPE_SEPARATOR);
		sbObjSelect.append(PGReportGeneratorUtil.SELECT_ATTRIBUTE_REPORT_FORM_DATA);

		getObjectSelectsDetails(context, sbObjSelect.toString(), output, dobReportTemplateObj);

		getSelectedTypesForTemplate(context,output, objReportGeneratorUtil, fromPropertiesPage);

		JsonObject jsonOutputInfo = output.build();
		if (fromPropertiesPage) {
			jsonOutputInfo.remove(PGReportGeneratorUtil.ATTRIBUTE_REPORT_FORM_DATA);
			jsonOutputInfo.remove(PGReportGeneratorUtil.SELECT_PHYSICAL_ID);
		}

		return jsonOutputInfo.toString();
	}

	/**
	 * 
	 * @param context
	 * @param output
	 * @param objReportGeneratorUtil
	 * @param fromPropertiesPage
	 * @throws MatrixException 
	 */
	void getSelectedTypesForTemplate(Context context, JsonObjectBuilder output, PGReportGeneratorUtil objReportGeneratorUtil, boolean fromPropertiesPage) throws MatrixException {
		JsonArrayBuilder jsonSelectedAttrArray = Json.createArrayBuilder();
		JsonObject jsonOutputs = output.build();
		String strAttrValue = jsonOutputs.getString(PGReportGeneratorUtil.ATTRIBUTE_REPORT_FORM_DATA);
		if (UIUtil.isNotNullAndNotEmpty(strAttrValue)) {
			StringBuilder sbInputFormBuilder = new StringBuilder();
			sbInputFormBuilder.append("{\"").append(PGReportGeneratorUtil.KEY_INPUT).append("\" : ")
					.append(strAttrValue).append("}");

			JsonObject jsonInputFormObj = objReportGeneratorUtil.getJsonFromJsonString(sbInputFormBuilder.toString());
			JsonArray jsonInputFormArray = jsonInputFormObj.getJsonArray(PGReportGeneratorUtil.KEY_INPUT);
			String strLanguage = context.getLocale().getLanguage();
			
			for (int i = 0; i < jsonInputFormArray.size(); i++) {
				JsonObject jsonElemObj = jsonInputFormArray.getJsonObject(i);
				String strFieldType = jsonElemObj.getString(PGReportGeneratorUtil.KEY_NAME);
				if (DomainConstants.SELECT_TYPE.equals(strFieldType)) {
					String strSelectedTypes = jsonElemObj.getString(PGReportGeneratorUtil.KEY_VALUE);
					if(fromPropertiesPage) {
						JsonArray jsonTypeDisplayNameArray = getTypeDisplayNames(context, strSelectedTypes, strLanguage);
						output.add(KEY_SELECTED_TYPES, jsonTypeDisplayNameArray);
					} else {
						output.add(KEY_SELECTED_TYPES, strSelectedTypes);
					}
				} else {
					jsonSelectedAttrArray.add(jsonElemObj);
				}
			}
			output.add(KEY_SELECTED_ATTRIBUTES, jsonSelectedAttrArray.build());
		}

	}

	/**
	 * 
	 * @param context
	 * @param strSelectedTypes
	 * @return
	 * @throws MatrixException
	 */
	JsonArray getTypeDisplayNames(Context context, String strSelectedTypes, String strLanguage) throws MatrixException {
		JsonArrayBuilder jsonTypeArray = Json.createArrayBuilder();
		StringList slTypeList = StringUtil.split(strSelectedTypes, ",");

		for (int i = 0; i < slTypeList.size(); i++) {
			String strTypeActualName = slTypeList.get(i);
			String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context,
					PGReportGeneratorUtil.SELECT_ADMIN_TYPE, strTypeActualName, strLanguage);
			if (UIUtil.isNullOrEmpty(strDisplayName) || strDisplayName.startsWith(PREFIX_EMX_FRAMEWORK)) {
				strDisplayName = strTypeActualName;
			}
			JsonObjectBuilder jsonTypeObj = Json.createObjectBuilder();
			jsonTypeObj.add(PGReportGeneratorUtil.KEY_NAME, strTypeActualName);
			jsonTypeObj.add(PGReportGeneratorUtil.KEY_LABEL, strDisplayName);
			jsonTypeArray.add(jsonTypeObj);
		}
		return jsonTypeArray.build();
	}
		
	/**
	 * -----------------------------------This method can be used in future-------------------------------------------
	 * @param context
	 * @param strTypes
	 * @param jsonRelationshipsObj
	 * @param strLanguage 
	 * @throws MatrixException 
	 */
	void getTypeRelationshipMapping(Context context, String strTypes, JsonObjectBuilder jsonRelationshipsObj, String strLanguage) throws MatrixException {
		StringList strTypeList  = StringUtil.split(strTypes, PIPE_SEPARATOR);
		for(int i=0; i<strTypeList.size(); i++) {
			String strTypeName = strTypeList.get(i);
			BusinessType busTypObj = new BusinessType(strTypeName, new Vault(PersonUtil.getDefaultVault(context)));
			RelationshipTypeList relTypeList = busTypObj.getRelationshipTypes(context, true, true, true);
			JsonArrayBuilder jsonRelArray = Json.createArrayBuilder();
			for(int j=0; j<relTypeList.size(); j++) {
				RelationshipType relType = relTypeList.get(j);
				String strRelationshipName = relType.getName();
				
				String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context, KEY_RELATIONSHIP_TYPE, strRelationshipName, strLanguage);
				
				if(UIUtil.isNullOrEmpty(strDisplayName) || strDisplayName.startsWith(PREFIX_EMX_FRAMEWORK)) {
					strDisplayName = strRelationshipName;
				}
				JsonObjectBuilder jsonRelObj = Json.createObjectBuilder();
				jsonRelObj.add(PGReportGeneratorUtil.KEY_VALUE, strRelationshipName);
				jsonRelObj.add(DomainConstants.SELECT_NAME, strDisplayName);
				jsonRelArray.add(jsonRelObj);
			}
			jsonRelationshipsObj.add(strTypeName, jsonRelArray);
		}
	}

}
