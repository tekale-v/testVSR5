/*
 * PGGPSAssessmentTaskUtil.java
 * 
 * Added by Dashboard Team
 * For GPS Assessment Task Widget related Webservice
 * 
 */

package com.pg.widgets.gpsassessmenttask;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import com.dassault_systemes.enovia.e6wv2.foundation.FoundationException;
import com.dassault_systemes.enovia.e6wv2.foundation.db.ContextUtil;
import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.common.WorkspaceVault;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.awl.util.BusinessUtil;

import matrix.db.Access;
import matrix.db.BusinessObject;
import matrix.db.ClientTask;
import matrix.db.ClientTaskItr;
import matrix.db.ClientTaskList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.db.State;
import matrix.db.StateList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

/**
 * Class PGGPSAssessmentTaskUtil has all the methods defined for the 'GPS Assessment Task' widget activities.
 * 
 * @Since 2018x.5
 * @author 
 *
 */
class PGGPSAssessmentTaskUtil
{
	static final String KEY_HIERARCHY  = "hierarchy";	
	static final String KEY_OUT_PUT = "output";
	static final String KEY_SIMPLE_DATEFORMAT = "yyyy-MM-dd";
	static final String MAX_STRING_LENGTH = "org.apache.johnzon.max-string-length";
	static final String VALUE_KILOBYTES = "262144";
	static final String KEY_DESCRIPTION = "Description";
	static final String KEY_ATTR_GPS_ASSESSMENT_CATEGORY = "pgGPSAssessmentCategory";
	static final String VALUE_ATTR_GPS_ASSESSMENT_CATEGORY = "A8 - Commercial Use";
	static final String KEY_FORMDATA = "formdata";
	
	static final String ATTRIBUTE_START_OF_SHIPMENT = PropertyUtil.getSchemaProperty(null, "attribute_pgStartofShipment");
	static final String SELECT_ATTR_START_OF_SHIPMENT = DomainObject.getAttributeSelect(ATTRIBUTE_START_OF_SHIPMENT);
	static final String ATTRIBUTE_PRODUCTION_START_DATE = PropertyUtil.getSchemaProperty(null, "attribute_ProductionStartDate");
	static final String SELECT_ATTR_PROUCTION_START_DATE = DomainObject.getAttributeSelect(ATTRIBUTE_PRODUCTION_START_DATE);
	static final String KEY_RELATED_IDS = "RelatedIds";
	static final String KEY_DISCONNECT_IDS = "RemoveIds";
	static final String TYPE_GPS_ASSESSMENT_TASK = PropertyUtil.getSchemaProperty(null, "type_pgGPSAssessmentTask");
	static final String POLICY_GPS_ASSESSMENT_TASK = PropertyUtil.getSchemaProperty(null, "policy_pgGPSAssessmentTask");
	static final String REASON_FOR_CHANGE_VALUE ="New";
	static final String ATTRIBUTE_REASON_FOR_CHANGE = PropertyUtil.getSchemaProperty(null, "attribute_ReasonforChange");
	static final String KEY_TYPE = "Type";
	static final String KEY_POLICY = "Policy";
	static final String KEY_VALUE = "value";
	static final String KEY_NAME = "name";
	static final String SELECT_PHYSICAL_ID = "physicalid";
	static final String SELECT_CONNECTION_ID = "id[connection]";
	static final String SELECT_FROM_PHYSICAL_ID = "from.physicalid";
	static final String KEY_OBJ_SELECTS = "ObjSelectables";
	static final String VALUE_FALSE = "false";
	static final String VALUE_TRUE = "true";
	static final String KEY_PARENT_INFO = "parentInfo";
	static final String KEY_WARNING_MESSAGE = "WarningMessage";
	static final String RANGE_ATTRIBUTE_NAMES = "RangeAttributeNameList";
	 
	static final String ATTRIBUTE_GPS_ASSESSMENT_CATEGORY = PropertyUtil.getSchemaProperty(null,
			"attribute_pgGPSAssessmentCategory");
	static final String SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY = DomainObject
			.getAttributeSelect(ATTRIBUTE_GPS_ASSESSMENT_CATEGORY);
	static final String ATTRIBUTE_EXPECTED_REGULATORY_PRODUCT_CLASSIFICATION = PropertyUtil.getSchemaProperty(null,
			"attribute_pgExpectedRegulatoryProductClassification");
	static final String SELECT_ATTRIBUTE_EXPECTED_REGULATORY_PRODUCT_CLASSIFICATION = DomainObject
			.getAttributeSelect(ATTRIBUTE_EXPECTED_REGULATORY_PRODUCT_CLASSIFICATION);
	static final String RELATIONSHIP_PGGPSASSESSMENTTASK_INPUTS = PropertyUtil.getSchemaProperty(null,
			"relationship_pgGPSAssessmentTaskInputs");
	static final String TYPE_SOFTWARE_PART = PropertyUtil.getSchemaProperty(null,
			"type_pgSoftwarePart");
	static final String TYPE_ASSEMBLED_PRODUCT_PART = PropertyUtil.getSchemaProperty(null,
			"type_pgAssembledProductPart");
	static final String TYPE_DEVICE_PRODUCT_PART = PropertyUtil.getSchemaProperty(null, "type_pgDeviceProductPart");
	static final String TYPE_FORMULATION_PART = PropertyUtil.getSchemaProperty(null, "type_FormulationPart");
	static final String TYPE_FORMULATED_PRODUCT = PropertyUtil.getSchemaProperty(null, "type_pgFormulatedProduct");

	static final String ATTRIBUTE_ALTERNATE_DOSAGE_APPLICABLE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAlternateDosageapplicable");
	static final String ATTRIBUTE_PCKG_SIZE_SAME_AS_CURRENT_MARKET = PropertyUtil.getSchemaProperty(null,
			"attribute_pgPackageSizeSameAsCurrentMarket");
	static final String ATTRIBUTE_PCKG_SHAPECOMP_SAME_AS_CURRENT_MARKET = PropertyUtil.getSchemaProperty(null,
			"attribute_pgPackageShapeCompositonSameAsCurrentMarket");
	static final String ATTRIBUTE_PLGEDB_REGION = PropertyUtil.getSchemaProperty(null,
			"attribute_pgPLGEDBRegion");
	static final String SELECT_ATTRIBUTE_PLGEDB_REGION = DomainObject
			.getAttributeSelect(ATTRIBUTE_PLGEDB_REGION);
	static final String RANGE_VALUE_YES = "Yes";
	static final String RANGE_VALUE_NO = "No";
	static final String ATTRIBUTE_REQUEST_TO_REMOVE_MARKET_FROM_PRODUDCT= PropertyUtil.getSchemaProperty(null,
				"attribute_pgIsReqToRemMktFromProdPart");
	static final String TYPE_COUNTRY = PropertyUtil.getSchemaProperty(null, "type_Country");
	static final String POLICY_COUNTRY = PropertyUtil.getSchemaProperty(null, "policy_Country");
	static final String STATE_ACTIVE = PropertyUtil.getSchemaProperty(null, DomainConstants.SELECT_POLICY,
			POLICY_COUNTRY, "state_Active");

	static final String REL_PGGPSASSESSMENTTASK_INPUTS_COUNTRIES = PropertyUtil.getSchemaProperty(null,
			"relationship_pgCountriesToBeCleared");
	static final String SELECT_COUNTRIES_TO_BE_CLEARED_RELID = "tomid[" + REL_PGGPSASSESSMENTTASK_INPUTS_COUNTRIES
			+ "].id";
	static final String SELECT_COUNTRIES_TO_BE_CLEARED_IDS = "tomid[" + REL_PGGPSASSESSMENTTASK_INPUTS_COUNTRIES
			+ "].from.physicalid";
	 
	static final String PROGRAM_PG_DSO_UTIL = "pgDSOUtil";
	static final String METHOD_GET_PICKLIST_RAGNE_MAP_FOR_ATTR = "getPicklistRangeMapForDirectAttr";
	static final String PROGRAM_DSM_TASK_UTIL = "pgDSMTaskUtil";
	static final String METHOD_UPDATED_COUNTRY_REL_ATTR = "updateContryClearanceRelAttributes";
	static final String METHOD_CONNECT_COUNTIRES_TASKPDP = "connectCountriesToGPSTaskPDP";
	static final String KEY_PICKLIST_NAME = "pgPicklistName";
	static final String KEY_SETTINGS = "settings";
	static final String KEY_FIELD_MAP	= "fieldMap";
	static final String KEY_FIELD_CHOICES = "field_choices";
	static final String KEY_DISPLAY_TYPE = "displayType";
	static final String KEY_PREFIX_FRAMEWORK= "emxFramework";
	static final String VALUE_SUCCESS = "success";
	static final String VALUE_FAILED = "failure";
	static final String KEY_STATUS = "status";
	static final String KEY_ERROR = "error";
	static final String STRING_COLUMNMAP = "columnMap";
	static final String STRING_PARAMMAP = "paramMap";
	static final String STRING_RELID = "relId";
	static final String STRING_NEWVALUE = "New Value";
	static final String CONSTANT_EXPECTEDREGULATORYPRODUCTCLASSIFICATION = "ExpectedRegulatoryProductClassification";
	static final String KEY_TABLE_ROWID = "emxTableRowId";
	static final String KEY_PDP_RELID = "strPDPRelId";
	static final String KEY_OBJECT_ID = "strObjectId";
	static final String OPERATION_PROMOTE = "promote";
	static final String OPERATION_DEMOTE = "demote";
	static final String KEY_IS_CURRENT = "isCurrent";
	static final String KEY_OPERATION = "operation";
	static final String KEY_STATES = "States";
	static final String KEY_PROPERTIES = "Properties";
	static final String KEY_LABEL = "label";
	static final String ATTRIBUTE_BUSINESSAREA = PropertyUtil.getSchemaProperty(null, "attribute_pgBusinessArea");
	static final String SELECT_ATTRIBUTE_BUSINESSAREA = DomainObject.getAttributeSelect(ATTRIBUTE_BUSINESSAREA);
	static final String DEFAULT_BUSINESS_AREA_FOR_GPS_TASK = "Global Development Markets";
	static final String PERSON_USER_AGENT = "person_UserAgent";
	static final String VALUE_PRODUCTS = "Products";
	static final String KEY_OBJ_DETAILS = "ConnectedProductsInfo";
	static final String KEY_UPDATED_VALUES = "updatedValues";
	static final String KEY_EXPR = "expr";
	static final String KEY_GET_PROPERTIES_ONLY = "GetOnlyProperties";
	static final String STATE_PRELIMINARY = "Preliminary";
	static final String STATE_REVIEW = "Review";
	static final String STATE_APPROVED = "Approved";
	static final String STATE_RELEASE = "Release";
	static final String STATE_OBSOLETE = "Obsolete";
	static final String ERROR_MSG_NOT_VALID_PART1 = "Following Products cannot be added: ";
	static final String ERROR_MSG_NOT_VALID_PART2 = ". Please drop only " + STATE_APPROVED + "/" + STATE_RELEASE + " Products.";
	static final String KEY_COMMA_SEPARATOR = ",";
	static final String KEY_PIPE_SEPARATOR = "|";
	
	 /**
	  * Method to Create GPS Assessment Task 
	  * @param context : eMatrix context objet
	  * @param strData : String Json with values for attributes pgGPSAssessmentCategory, pgStartofShipment, Description and rel ids
	  * @return : String created GPS Task info
	  * @throws Exception
	  */
	 String createGPSTask(Context context, String strData) throws Exception 
	{
	   JsonObject jsonInput = getJsonFromJsonString(strData);
	   JsonArray formData=jsonInput.getJsonArray(KEY_FORMDATA);
	   String strObjSelects = jsonInput.getString(KEY_OBJ_SELECTS);
	   String strObjIdsToConnect = jsonInput.getString(KEY_RELATED_IDS);
	   
	   checkIfValidPartsDropped(context, StringUtil.split(strObjIdsToConnect, KEY_COMMA_SEPARATOR));
	   
	   StringList slObjList = StringUtil.split(strObjSelects, KEY_COMMA_SEPARATOR);
	   slObjList.add(SELECT_PHYSICAL_ID);
	   slObjList.add(DomainConstants.SELECT_NAME);
	   slObjList.add(DomainConstants.SELECT_TYPE);
	   
	   String strSymbolicTypeName 	= FrameworkUtil.getAliasForAdmin(context, KEY_TYPE, TYPE_GPS_ASSESSMENT_TASK, true);
	   String strSymbolicPolicyName 	= FrameworkUtil.getAliasForAdmin(context, KEY_POLICY, POLICY_GPS_ASSESSMENT_TASK, true);
		String strTaskName = FrameworkUtil.autoName(context, strSymbolicTypeName, null, strSymbolicPolicyName, null,
				null, true, true);
		
		DomainObject dobGPSTask = DomainObject.newInstance(context);
		dobGPSTask.createObject(context, TYPE_GPS_ASSESSMENT_TASK, strTaskName, DomainConstants.EMPTY_STRING, POLICY_GPS_ASSESSMENT_TASK, context.getVault().getName());

		Map<?, ?> mapObjInfo = dobGPSTask.getInfo(context, slObjList);
		
		Map<Object, Object> mapAttributeMap = new HashMap<>();
		JsonObject field=null;
		String strName;
		String strVal;
		for(int i=0;i<formData.size();i++) {
			field=formData.getJsonObject(i);
			strName = field.getString(KEY_NAME);
			strVal = field.getString(KEY_VALUE);
			if(KEY_DESCRIPTION.equalsIgnoreCase(strName)) {
				dobGPSTask.setDescription(context, strVal);
			} else {
				mapAttributeMap.put(getFormattedExpression(field.getString(KEY_NAME)),field.getString(KEY_VALUE));
			}
		}
		setMandatoryInfoForGPSTask(mapAttributeMap);
	
		
		dobGPSTask.setAttributeValues(context, mapAttributeMap);
		
		String strTaskId = (String) mapObjInfo.get(SELECT_PHYSICAL_ID);
				
		if(UIUtil.isNotNullAndNotEmpty(strObjIdsToConnect))
		{
			connectProductWithTask(context, strTaskId, StringUtil.split(strObjIdsToConnect, KEY_COMMA_SEPARATOR));
		}
		
		return getRelatedProductsForTask(context, strTaskId, strObjSelects, DomainConstants.EMPTY_STRING);
		
	}
	 
	/**
	 * Set the mandatory information for GPS Task on its creation
	 * @param context
	 * @param dobGPSTask 
	 * @param mapAttributeMap
	 * @param strGPSAssessmentCategory 
	 * @throws ParseException 
	 * @throws FrameworkException 
	 */
	void setMandatoryInfoForGPSTask(Map<Object, Object> mapAttributeMap) throws ParseException {
		
		mapAttributeMap.put(ATTRIBUTE_ALTERNATE_DOSAGE_APPLICABLE, RANGE_VALUE_NO);
		mapAttributeMap.put(ATTRIBUTE_PCKG_SIZE_SAME_AS_CURRENT_MARKET, RANGE_VALUE_YES);
		mapAttributeMap.put(ATTRIBUTE_PCKG_SHAPECOMP_SAME_AS_CURRENT_MARKET, RANGE_VALUE_YES);
		mapAttributeMap.put(ATTRIBUTE_REASON_FOR_CHANGE, REASON_FOR_CHANGE_VALUE);
		mapAttributeMap.put(ATTRIBUTE_BUSINESSAREA, new StringList(DEFAULT_BUSINESS_AREA_FOR_GPS_TASK));
		mapAttributeMap.put(ATTRIBUTE_REQUEST_TO_REMOVE_MARKET_FROM_PRODUDCT,RANGE_VALUE_NO);
		Date today=new Date();
		DateFormat formatter = new SimpleDateFormat(KEY_SIMPLE_DATEFORMAT);
		
		String strToday=formatter.format(today);
		String strFormattedDate = getEMatrixDateFormat(strToday);
		mapAttributeMap.put(ATTRIBUTE_START_OF_SHIPMENT, strFormattedDate);
	
		
		mapAttributeMap.put(ATTRIBUTE_PRODUCTION_START_DATE, strFormattedDate);
	}

	/**
	 * Method to convert the date to eMatrix date format
	 * @param context
	 * @param strStartOfShipment
	 * @return
	 * @throws ParseException
	 */
	String getEMatrixDateFormat(String strStartOfShipment) throws ParseException {
		DateFormat formatter = new SimpleDateFormat(KEY_SIMPLE_DATEFORMAT);
		Date date = formatter.parse(strStartOfShipment);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		String strFormattedDate = convertDateToServerTimeZone(cal);
		return strFormattedDate;
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
	 * Method to connect GPS Task with Product
	 * @param context
	 * @param strTaskId
	 * @param slRelObjIds
	 * @throws FrameworkException
	 */
	 void connectProductWithTask(Context context, String strTaskId, StringList slRelObjIds) throws FrameworkException  {
			WorkspaceVault workspaceVault =(WorkspaceVault) DomainObject.newInstance(context, DomainConstants.TYPE_WORKSPACE_VAULT);
			workspaceVault.setId(strTaskId);
			
			int iSize = slRelObjIds.size();
			for (int i = 0; i < iSize; i++) {
				String strObjectIdType = slRelObjIds.get(i);
				StringList slOIDTypeList = StringUtil.split(strObjectIdType, KEY_PIPE_SEPARATOR);
				String strObjectId = slOIDTypeList.get(0);
				
				if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
					DomainObject dobRelatedObj = DomainObject.newInstance(context, strObjectId);
					DomainRelationship.connect(context, workspaceVault, RELATIONSHIP_PGGPSASSESSMENTTASK_INPUTS, dobRelatedObj);
				}
			}
	}
	
	 /**
	  * Method to get the related Product parts with GPS Task
	  * @param context
	  * @param strObjectPID
	  * @param strObjSelectables
	  * @param strWarning
	  * @return
	  * @throws Exception
	  */
	 String getRelatedProductsForTask(Context context, String strObjectPID, String strObjSelectables, String strWarning) throws Exception {
			
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonTemp = null;
					
			if(UIUtil.isNotNullAndNotEmpty(strObjectPID) && UIUtil.isNotNullAndNotEmpty(strObjSelectables)){
				DomainObject dobCurrentObj = DomainObject.newInstance(context, strObjectPID);
				
				StringList slObjectSelects = StringUtil.split(strObjSelectables, KEY_COMMA_SEPARATOR);
				slObjectSelects.add(SELECT_PHYSICAL_ID);
				slObjectSelects.add(DomainConstants.SELECT_TYPE);
				slObjectSelects.add(DomainConstants.SELECT_NAME);
				slObjectSelects.add(DomainConstants.SELECT_OWNER);
				slObjectSelects.add(SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY);
				
				Map<?,?> objectInfo = dobCurrentObj.getInfo(context, slObjectSelects);
				jsonTemp = Json.createObjectBuilder();

				for(int j=0; j<slObjectSelects.size(); j++) {
					String strSelect = slObjectSelects.get(j);
					String strValue = (String) objectInfo.get(strSelect);
					jsonTemp.add(strSelect, checkNullValueforString(strValue));
				}		
				
				String strTypeDisplayName = getTypeDisplayName(context, (String) objectInfo.get(DomainConstants.SELECT_TYPE));
				jsonTemp.add(KEY_DISPLAY_TYPE, checkNullValueforString(strTypeDisplayName));
				
				StringList hierarchyList = new StringList();				
				String strParentKey = (String)objectInfo.get(DomainConstants.SELECT_NAME);
				hierarchyList.add(strParentKey);
				jsonTemp.add(KEY_HIERARCHY,hierarchyList.toString());
				
				jsonReturnObj.add(KEY_PARENT_INFO,jsonTemp);
								
				StringList slRelSelects = new StringList(2);
				slRelSelects.add(DomainRelationship.SELECT_ID);
				slRelSelects.add(SELECT_COUNTRIES_TO_BE_CLEARED_RELID);
				
				MapList mlRelatedProductsFortask = getRelatedObjectsForGPSTask(context, dobCurrentObj, slObjectSelects, slRelSelects);
				
				for(int i=0; i<mlRelatedProductsFortask.size(); i++) {
					Map<?, ?> mpObjMap = (Map<?, ?>) mlRelatedProductsFortask.get(i);
					jsonTemp = Json.createObjectBuilder();
					for(int j=0; j<slObjectSelects.size(); j++) {
						String strKey = slObjectSelects.get(j);
						jsonTemp.add(strKey, checkNullValueforString((String) mpObjMap.get(strKey)));
					}
					hierarchyList.clear();
					hierarchyList.add((String) mpObjMap.get(DomainConstants.SELECT_NAME));		
					
					jsonTemp.add(KEY_HIERARCHY,hierarchyList.toString());
					
					jsonArr.add(jsonTemp);
					
					Object objCountryRel = mpObjMap.get(SELECT_COUNTRIES_TO_BE_CLEARED_RELID);
					if(objCountryRel != null) {
						StringList countriesRelList = new StringList();
						if(objCountryRel instanceof String)
							countriesRelList.add((String)objCountryRel);
						else if(objCountryRel instanceof StringList)
							countriesRelList.addAll((StringList)objCountryRel);
						
						if(!countriesRelList.isEmpty()) {
							StringList slObjectSelect = StringUtil.split(strObjSelectables, KEY_COMMA_SEPARATOR);
							getCountriesToBeClearedForProducts(context, countriesRelList, hierarchyList, jsonArr,slObjectSelect);
						}
					}
					
				}
							
			}		
		
		jsonReturnObj.add(KEY_OUT_PUT, jsonArr.build());
		
		if(UIUtil.isNotNullAndNotEmpty(strWarning)) {
			jsonReturnObj.add(KEY_WARNING_MESSAGE, strWarning);
		}
		
		return jsonReturnObj.build().toString();
	}
	 
	/**
	 * Method to get the related Product Parts and Country rel info for GPS Task
	 * @param context
	 * @param dobCurrentObj
	 * @param slObjectSelects
	 * @param slRelSelects
	 * @return
	 * @throws FrameworkException
	 */
	MapList getRelatedObjectsForGPSTask(Context context, DomainObject dobCurrentObj, StringList slObjectSelects, StringList slRelSelects) throws FrameworkException {
		
		Pattern typePattern = new Pattern(TYPE_ASSEMBLED_PRODUCT_PART);
		typePattern.addPattern(TYPE_DEVICE_PRODUCT_PART);
		typePattern.addPattern(TYPE_FORMULATION_PART);
		typePattern.addPattern(TYPE_FORMULATED_PRODUCT);
		typePattern.addPattern(TYPE_SOFTWARE_PART);
		
		Pattern relPattern = new Pattern(RELATIONSHIP_PGGPSASSESSMENTTASK_INPUTS);
		
		MapList mlRelatedProductsFortask = dobCurrentObj.getRelatedObjects(
				context,    														//the eMatrix Context object
				relPattern.getPattern(),											//Relationship pattern
				typePattern.getPattern(),											//Type pattern
				slObjectSelects,													//Object selects
				slRelSelects,														//Relationship selects
				false,																//get From relationships
				true,																//get To relationships
				(short)1,															//the number of levels to expand, 0 equals expand all.
				null,																//Object where clause
				null,																//Relationship where clause
				0);																	//Limit : The max number of Objects to get in the exapnd.0 to return all the data available

		slObjectSelects.add(DomainRelationship.SELECT_ID);
		
		return mlRelatedProductsFortask;
	}

	/**
	 * Method to get the display name for type for header info
	 * @param context
	 * @param strTypeName
	 * @return
	 * @throws MatrixException 
	 */
		String getTypeDisplayName(Context context, String strTypeName) throws MatrixException {
			String strDisplayName = "";
			if(UIUtil.isNotNullAndNotEmpty(strTypeName)) {
				strDisplayName = EnoviaResourceBundle.getAdminI18NString(context, KEY_TYPE, strTypeName, context.getLocale().getLanguage());
	           if(UIUtil.isNullOrEmpty(strDisplayName) || strDisplayName.startsWith(KEY_PREFIX_FRAMEWORK)) {
	                 strDisplayName = strTypeName;
	            }
			}
			return strDisplayName;
		}

	/**
	 * Method to get the countries related to GPS Task products
	 * @param countriesRelList
	 * @param hierarchyList
	 * @param jsonArr
	 * @throws FrameworkException 
	 */
	void getCountriesToBeClearedForProducts(Context context, StringList countriesRelList, StringList hierarchyList,
			JsonArrayBuilder jsonArr, StringList slObjectSelects) throws FrameworkException {
		StringList slSelectsList = new StringList();
		slSelectsList.add(DomainConstants.SELECT_ID);
		slSelectsList.add(DomainConstants.SELECT_FROM_NAME);
		slSelectsList.add(DomainConstants.SELECT_FROM_TYPE);
		slSelectsList.add(SELECT_FROM_PHYSICAL_ID);
		slSelectsList.addAll(slObjectSelects);
		
		int iSize = countriesRelList.size();
		String[] strRelIdsArray = new String[iSize];
		
		for(int i=0; i<iSize; i++) {
			strRelIdsArray[i] = countriesRelList.get(i);
		}
		
		MapList relInfoList	= DomainRelationship.getInfo(context, strRelIdsArray, slSelectsList);
		String strUniqueName = "";
		for(int j=0;j<relInfoList.size();j++) {
			JsonObjectBuilder jsonTempObj = Json.createObjectBuilder();
			Map<?,?> infoMap = (Map<?, ?>) relInfoList.get(j);
			jsonTempObj.add(DomainRelationship.SELECT_ID, (String)infoMap.get(DomainConstants.SELECT_ID));
			jsonTempObj.add(SELECT_PHYSICAL_ID, (String)infoMap.get(SELECT_FROM_PHYSICAL_ID));
			jsonTempObj.add(DomainConstants.SELECT_TYPE, (String)infoMap.get(DomainConstants.SELECT_FROM_TYPE));
			int kSize = slObjectSelects.size();
			for(int k=0; k<kSize; k++) {
				jsonTempObj.add(slObjectSelects.get(k), (String)infoMap.get(slObjectSelects.get(k)));
			}
			String strCountryName = (String)infoMap.get(DomainConstants.SELECT_FROM_NAME);
			jsonTempObj.add(DomainConstants.SELECT_NAME, strCountryName);

		    strUniqueName = strCountryName.concat((String)infoMap.get(DomainConstants.SELECT_ID));
			hierarchyList.add(strUniqueName);
			jsonTempObj.add(KEY_HIERARCHY,hierarchyList.toString());
			hierarchyList.remove(strUniqueName);
			jsonArr.add(jsonTempObj);
		}
		
	}

	/**
	 * Method to validate the attribute GPS_ASSESSMENT_CATEGORY for Task
	 * 
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception
	 */
	String validateGPSAssessmentCategory(Context context, String strObjectId) throws Exception {
		String strReturnValue = VALUE_FALSE;
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();

		if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			DomainObject dobGPSTask = DomainObject.newInstance(context, strObjectId);
			String strAttrValue = dobGPSTask.getInfo(context, SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY);
			if (VALUE_ATTR_GPS_ASSESSMENT_CATEGORY.equals(strAttrValue)) {
				strReturnValue = VALUE_TRUE;
			}
		}

		jsonOutput.add(KEY_VALUE, strReturnValue);

		return jsonOutput.build().toString();
	}
	 
	/**
	 * Method to convert String json which is usually the value of attributes
	 * ReportInputInformation, pgReportFormData and pgReportOutputData etc to
	 * JsonObject
	 * 
	 * @param strJsonString : String json (usually the value of attributes
	 *                      ReportInputInformation, pgReportFormData and
	 *                      pgReportOutputData etc)
	 * @return : JsonObject created from String json
	 */
	JsonObject getJsonFromJsonString(String strJsonString) {
		StringReader srJsonString = new StringReader(strJsonString);
		Map<String, String> configMap = new HashMap<>();
		configMap.put(MAX_STRING_LENGTH, VALUE_KILOBYTES);
		JsonReaderFactory factory = Json.createReaderFactory(configMap);
		try (JsonReader jsonReader = factory.createReader(srJsonString)) {
			return jsonReader.readObject();
		} finally {
			srJsonString.close();
		}
	}

	/**
	 * Method to remove Markets from Task products
	 * 
	 * @param context
	 * @param strConnectionIds
	 * @param strObjectId
	 * @param strObjSelectables
	 * @return
	 * @throws Exception
	 */
	String removeMarketsFromTaskProducts(Context context, String strConnectionIds, String strObjectId,
			String strObjSelectables) throws Exception {
			try {
				StringList slRelIDsList = StringUtil.split(strConnectionIds, KEY_COMMA_SEPARATOR);
				if(BusinessUtil.isNotNullOrEmpty(slRelIDsList)) {
					DomainRelationship.disconnect(context, BusinessUtil.toStringArray(slRelIDsList));	
				}			
			} catch (Exception e) {
				throw e;
			}

		return getRelatedProductsForTask(context, strObjectId, strObjSelectables, DomainConstants.EMPTY_STRING);
	}
	public static String getFormattedExpression(String strExpression) {
		String strFormattedExp = "";
		strFormattedExp = strExpression.replace("attribute[", DomainConstants.EMPTY_STRING);
		strFormattedExp = strFormattedExp.replace("]", DomainConstants.EMPTY_STRING);
		return strFormattedExp;
	}
	
	String getAttributeRangeValues(Context context, Map<?, ?> mpParamMAP) throws MatrixException  {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String pgPicklistNames =(String) mpParamMAP.get(RANGE_ATTRIBUTE_NAMES);
		StringList pgPicklistNamesList = new StringList();
		if (UIUtil.isNotNullAndNotEmpty(pgPicklistNames)) {
			pgPicklistNamesList.addAll(pgPicklistNames.split(KEY_COMMA_SEPARATOR));
		}
		if (null != pgPicklistNamesList && !pgPicklistNamesList.isEmpty()) {
			for (int iter = 0; iter < pgPicklistNamesList.size(); iter++) {
				String pgPicklistName = pgPicklistNamesList.get(iter);
				if (UIUtil.isNotNullAndNotEmpty(pgPicklistName)) {
					HashMap<String, String> hmArgsMap = new HashMap<>();
					hmArgsMap.put(KEY_PICKLIST_NAME, pgPicklistName);

					HashMap<String, HashMap<?, ?>> hmSettingsMap = new HashMap<>();
					hmSettingsMap.put(KEY_SETTINGS, hmArgsMap);
					
					HashMap<String, HashMap<?, ?>> hmFieldMap = new HashMap<>();
					hmFieldMap.put(KEY_FIELD_MAP, hmSettingsMap);

					String[] strJPOArgs = JPO.packArgs(hmFieldMap);
					Map<?, ?> attributeRangeMap = JPO.invoke(context, PROGRAM_PG_DSO_UTIL, strJPOArgs,
							METHOD_GET_PICKLIST_RAGNE_MAP_FOR_ATTR, strJPOArgs, Map.class);
					StringList slRangeList = (StringList) attributeRangeMap.get(KEY_FIELD_CHOICES);
					JsonArrayBuilder jsonArr = Json.createArrayBuilder();
					for (int i = 0; i < slRangeList.size(); i++) {
						String strRangeValue = checkNullValueforString(slRangeList.get(i));
						jsonArr.add(strRangeValue);
					}
					output.add(pgPicklistName, jsonArr);
				}
			}
		}		
		return output.build().toString();
	}
	/**
	 * Method to set the value for rel attribute
	 * pgExpectedRegulatoryProductClassification
	 * 
	 * @param context
	 * @param strRelId
	 * @param strNewValue
	 * @return
	 * @throws MatrixException
	 */
	String setAttributeValue(Context context, String strRelId, String strNewValue, String strType, String strAttributeName)
			throws MatrixException {
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		if (UIUtil.isNotNullAndNotEmpty(strRelId) && TYPE_COUNTRY.equals(strType) && strNewValue != null && UIUtil.isNotNullAndNotEmpty(strAttributeName)) {

			HashMap<String, String> hmColumnMap = new HashMap<>();
			String strAttName = getFormattedExpression(strAttributeName);
			strAttName = strAttName.substring(2);
			hmColumnMap.put(DomainConstants.SELECT_NAME, strAttName);
			HashMap<String, String> hmParamMap = new HashMap<>();
			hmParamMap.put(STRING_RELID, strRelId);
			hmParamMap.put(STRING_NEWVALUE, strNewValue);

			HashMap<String, HashMap<String, String>> hmArgsMap = new HashMap<>();
			hmArgsMap.put(STRING_COLUMNMAP, hmColumnMap);
			hmArgsMap.put(STRING_PARAMMAP, hmParamMap);

			String[] strJPOArgs = JPO.packArgs(hmArgsMap);
			JPO.invoke(context, PROGRAM_DSM_TASK_UTIL, strJPOArgs, METHOD_UPDATED_COUNTRY_REL_ATTR, strJPOArgs);

			jsonOutput.add(KEY_STATUS, VALUE_SUCCESS);
		}
		return jsonOutput.build().toString();
	}
	
	/**
	 * Check value is null or Empty and if multi-value character and replace it with
	 * comma
	 * 
	 * @param strString : String for null check
	 * @return : String strString after null check
	 */
	static String checkNullValueforString(String strString) {
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}

	/**
	 * Method to get all counties list
	 * @param context
	 * @return
	 * @throws FrameworkException 
	 */
	String getAllCountriesList(Context context) throws FrameworkException {
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		JsonArrayBuilder jsonCountryArray = Json.createArrayBuilder();
		
		StringList slObjectSelects = new StringList(5);
		slObjectSelects.add(SELECT_PHYSICAL_ID);
		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(SELECT_ATTRIBUTE_PLGEDB_REGION);
		
		StringBuilder sbWhereClause = new StringBuilder();
		sbWhereClause.append(DomainConstants.SELECT_POLICY).append(" == ").append(POLICY_COUNTRY);
		sbWhereClause.append(" AND ").append(DomainConstants.SELECT_CURRENT).append(" == ").append(STATE_ACTIVE);
		
		MapList countryObjMapList = DomainObject.findObjects(context, //Context
															TYPE_COUNTRY, //Type
															DomainConstants.QUERY_WILDCARD,//Name
															sbWhereClause.toString(), //Where clause
															slObjectSelects);//Bus select
		
		for(int i=0; i<countryObjMapList.size(); i++) {
			Map<?, ?> objMap = (Map<?, ?>) countryObjMapList.get(i);
			JsonObject jsonCountryObj = convertMapToJsonObj(Json.createObjectBuilder().build(), objMap);
			jsonCountryArray.add(jsonCountryObj);
		}
		
		jsonOutput.add(TYPE_COUNTRY, jsonCountryArray);
		return jsonOutput.build().toString();
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
		inputMap.entrySet().forEach(e -> builder.add((String)e.getKey(), (String)e.getValue()));
		return builder.build();
	}
	
	/**
	 * Method to connect Countries to GPS Task Products
	 * @param context
	 * @param strRelId
	 * @param strSelectedCountries
	 * @return
	 * @throws Exception 
	 */
	String connectCountriesToProductTask(Context context, String strRelId, String strSelectedCountries, String strGPSTaskId, String strObjselects)
			throws Exception {
		if (UIUtil.isNotNullAndNotEmpty(strRelId) && UIUtil.isNotNullAndNotEmpty(strSelectedCountries)) {
			StringList slSelectedCountries = StringUtil.split(strSelectedCountries, KEY_COMMA_SEPARATOR);
			int iSize = slSelectedCountries.size();
			if(iSize > 0) {
				String[] strCountryArray = new String[iSize];
				for(int i=0; i<iSize; i++) {
					strCountryArray[i] = slSelectedCountries.get(i);
				}
				
				String[] strRelIds = new String[1];
				strRelIds[0] = strRelId;
				String[] strTaskId = new String[1];
				strTaskId[0] = strGPSTaskId;
				
				HashMap<String, String[]> hmArgsMap = new HashMap<>();
				hmArgsMap.put(KEY_TABLE_ROWID, strCountryArray);
				hmArgsMap.put(KEY_PDP_RELID, strRelIds);
				hmArgsMap.put(KEY_OBJECT_ID, strTaskId);
				
				String[] strJPOArgs = JPO.packArgs(hmArgsMap);
				JPO.invoke(context, PROGRAM_DSM_TASK_UTIL, strJPOArgs, METHOD_CONNECT_COUNTIRES_TASKPDP, strJPOArgs);
			}
			
		}
		return getRelatedProductsForTask(context, strGPSTaskId, strObjselects, DomainConstants.EMPTY_STRING);
	}
	
	/**
	 * Method to get Related Countries for Product task
	 * @param context
	 * @param strRelId
	 * @return
	 * @throws FrameworkException 
	 */
	StringList getRelatedCountriesForProduct(Context context, String strRelId) throws FrameworkException {		
		StringList slRelatedCountriesIds = new StringList();
		StringList slRelIds = StringUtil.split(strRelId, KEY_PIPE_SEPARATOR);
		int iSize = slRelIds.size();
		String[] strRelIdsArray = new String[iSize];
		for(int i=0;i<iSize;i++) {
			strRelIdsArray[i] = slRelIds.get(i);
		}
		
		MapList relInfoList	= DomainRelationship.getInfo(context, strRelIdsArray, new StringList(SELECT_COUNTRIES_TO_BE_CLEARED_IDS));
		if(!relInfoList.isEmpty()) {
			Map<?,?> mpObjMap = (Map<?, ?>) relInfoList.get(0);
			Object objCountryRel = mpObjMap.get(SELECT_COUNTRIES_TO_BE_CLEARED_IDS);
			if(objCountryRel != null) {
				if(objCountryRel instanceof String)
					slRelatedCountriesIds.add((String)objCountryRel);
				else if(objCountryRel instanceof StringList)
					slRelatedCountriesIds.addAll((StringList)objCountryRel);
			}
		}
		return slRelatedCountriesIds;
	}

	/**
	 * Method to edit GPS Task Properties page
	 * @param context
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	String editGPSTaskProperties(Context context, String strData) throws Exception 
	{
	   JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
	   JsonObject jsonInput = getJsonFromJsonString(strData);
	   String strTaskId = jsonInput.getString(DomainConstants.SELECT_ID);
	   JsonArray jsonRelatedProductsArray = jsonInput.getJsonArray(KEY_OBJ_DETAILS);
	   JsonArray jsonUpdatedFieldsArray = jsonInput.getJsonArray(KEY_UPDATED_VALUES);

	   if(UIUtil.isNotNullAndNotEmpty(strTaskId)) {
		   DomainObject dobGPSTask = DomainObject.newInstance(context, strTaskId);
		   for(int i=0; i<jsonUpdatedFieldsArray.size(); i++) {
			   JsonObject jsonFieldObj = jsonUpdatedFieldsArray.getJsonObject(i);
		   	   String strSelect = jsonFieldObj.getString(KEY_EXPR);
		   	   if(KEY_DESCRIPTION.equalsIgnoreCase(strSelect)) {
		   		   String strDescription = jsonFieldObj.getString(KEY_VALUE);
		   		   if(UIUtil.isNotNullAndNotEmpty(strDescription)) {
						dobGPSTask.setDescription(context, strDescription); 
					}
		   	   } else if(strSelect.contains(ATTRIBUTE_START_OF_SHIPMENT) || strSelect.contains(ATTRIBUTE_PRODUCTION_START_DATE)) {
					Map<String, String> mapAttributeMap = new HashMap<>();
					String strValue = jsonFieldObj.getString(KEY_VALUE);
					if(UIUtil.isNotNullAndNotEmpty(strValue))
					{
						String strFormattedDate = getEMatrixDateFormat(strValue);
						if(strSelect.contains(ATTRIBUTE_START_OF_SHIPMENT))
						{
						mapAttributeMap.put(ATTRIBUTE_START_OF_SHIPMENT, strFormattedDate);
						}
						else
						{
							mapAttributeMap.put(ATTRIBUTE_PRODUCTION_START_DATE, strFormattedDate);
						}
					}
					dobGPSTask.setAttributeValues(context, mapAttributeMap);
				}
				else if(strSelect.contains(ATTRIBUTE_BUSINESSAREA)) {
					String strValue = jsonFieldObj.getString(KEY_VALUE);
					dobGPSTask.setAttributeValue(context, ATTRIBUTE_BUSINESSAREA, strValue);
				}
				else if(strSelect.equals(VALUE_PRODUCTS)) {
		   		   JsonArray jsonFieldValue = jsonFieldObj.getJsonArray(KEY_VALUE);
		   		   updatedProductsOnEdit(context, jsonRelatedProductsArray, jsonFieldValue, dobGPSTask, strTaskId);
		   	   }
		   }

		   return getPropertiesOnEdit(context, strTaskId);
	   }
		
	   return jsonOutput.build().toString();
	}

	/**
	 * Method to get Properties details on edit
	 * @param context
	 * @param strTaskId
	 * @return
	 * @throws Exception
	 */
	String getPropertiesOnEdit(Context context, String strTaskId) throws Exception {
		JsonObjectBuilder jsonInputArgs = Json.createObjectBuilder();
		jsonInputArgs.add(DomainConstants.SELECT_ID, strTaskId);
		StringList slSelects = new StringList();
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY);
		slSelects.add(DomainConstants.SELECT_DESCRIPTION);
		slSelects.add(VALUE_PRODUCTS);
		slSelects.add(SELECT_ATTR_START_OF_SHIPMENT);
		slSelects.add(SELECT_ATTR_PROUCTION_START_DATE);
		slSelects.add(SELECT_ATTRIBUTE_BUSINESSAREA);
		jsonInputArgs.add(KEY_OBJ_SELECTS, String.join(KEY_COMMA_SEPARATOR, slSelects));
		jsonInputArgs.add(KEY_GET_PROPERTIES_ONLY, true);

		return getGPSTaskProperties(context, jsonInputArgs.build().toString());
	}

	/**
	 * Method to updated Products on edit Properties for GPS Task
	 * @param context
	 * @param jsonRelatedProductsArray
	 * @param jsonFieldValue
	 * @param dobGPSTask
	 * @param strTaskId
	 * @throws Exception 
	 */
	void updatedProductsOnEdit(Context context, JsonArray jsonRelatedProductsArray, JsonArray jsonFieldValue,
			DomainObject dobGPSTask, String strTaskId) throws Exception {
		StringList slRemoveIdList = new StringList();
		StringList slFieldsIdslist = new StringList();
		for(int i=0; i<jsonFieldValue.size(); i++) {
			JsonObject jsonFieldObj = jsonFieldValue.getJsonObject(i);
			String strObjId = jsonFieldObj.getString(SELECT_PHYSICAL_ID);
			slFieldsIdslist.add(strObjId);
		}
		
		for(int j=0; j<jsonRelatedProductsArray.size(); j++) {
			JsonObject jsonRelObj = jsonRelatedProductsArray.getJsonObject(j);
			String strRelObjId = jsonRelObj.getString(SELECT_PHYSICAL_ID);
			if(slFieldsIdslist.contains(strRelObjId)) {
				slFieldsIdslist.remove(strRelObjId);
			} else {
				slRemoveIdList.add(strRelObjId);
			}
		}
		
		checkIfValidPartsDropped(context, slFieldsIdslist);
		
		if(!slRemoveIdList.isEmpty()) {
			disConnectProductsFromTask(context, slRemoveIdList, dobGPSTask);
		}
		if(!slFieldsIdslist.isEmpty())
		{
			connectProductWithTask(context, strTaskId, slFieldsIdslist);
		}
	}

	/**
	 * Method to remove Product from GPS Task. Code is referred from existing
	 * Product remove operation used inside the \cpn\pgRemoveConnectedObjects.jsp.
	 * Using push pop context is part of original code copied from JSP
	 * 
	 * @param context
	 * @param strObjIdsToDisconnect
	 * @param dobGPSTask
	 * @throws FoundationException
	 * @throws MatrixException
	 */
	void disConnectProductsFromTask(Context context, StringList slRemoveIdList, DomainObject dobGPSTask)
			throws FoundationException, MatrixException {
			boolean isContextPushed = false;
			try {
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, PERSON_USER_AGENT),
						DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isContextPushed = true;

				boolean isFrom = true;
				for (int i = 0; i < slRemoveIdList.size(); i++) {
					String strObjId = slRemoveIdList.get(i);
					dobGPSTask.disconnect(context, new RelationshipType(RELATIONSHIP_PGGPSASSESSMENTTASK_INPUTS),
							isFrom, new BusinessObject(strObjId));
				}
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
			}
	}

	/**
	 * Method to get properties page details for GPS Task
	 * @param context
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	String getGPSTaskProperties(Context context, String strData)  throws Exception {		
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject jsonInput = getJsonFromJsonString(strData);
		String strObjectId = jsonInput.getString(DomainConstants.SELECT_ID);
		String strObjSelectables = jsonInput.getString(KEY_OBJ_SELECTS);

		StringList slObjSelectables = StringUtil.split(strObjSelectables,KEY_COMMA_SEPARATOR);
        slObjSelectables.add(DomainConstants.SELECT_CURRENT);
        slObjSelectables.add(DomainConstants.SELECT_POLICY);
        slObjSelectables.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);

		if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			DomainObject doObject = DomainObject.newInstance(context, strObjectId);
			Map<?, ?> objMap = doObject.getInfo(context, slObjSelectables);
			if (!objMap.isEmpty()) {
				JsonObjectBuilder jsonPropertiesObj = Json.createObjectBuilder();
				for(int j=0; j<slObjSelectables.size(); j++) {
					String strSelect = slObjSelectables.get(j);
					if(VALUE_PRODUCTS.equals(strSelect)) {
						updateRelatedProductsForTask(context, doObject, jsonPropertiesObj, strSelect);
					} else {
						String strSelectValue = checkNullValueforString((String) objMap.get(strSelect));
						if(DomainConstants.SELECT_TYPE.equals(strSelect)) {
							strSelectValue = getTypeDisplayName(context, strSelectValue);
						}
						jsonPropertiesObj.add(strSelect, strSelectValue);
					}
				}
				
				if(!jsonInput.containsKey(KEY_GET_PROPERTIES_ONLY)) {
					String strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
					String strPolicy = (String) objMap.get(DomainConstants.SELECT_POLICY);
					JsonArray jsonStatesArray = getGPSTaskStates(context, strObjectId, strPolicy, strCurrent);
					output.add(KEY_STATES, jsonStatesArray);
				}
				output.add(KEY_PROPERTIES, jsonPropertiesObj);
			}
		}
        return output.build().toString();             
	}
	
	/**
	 * Method to get next and previous states for GPS Task. Method is copied from emxExtendedHeader:genHeaderStatus program and hence has push pop statements to fix some bugs related to print policy.
	 * @param context
	 * @param strObjectId
	 * @param sPolicy
	 * @param sCurrent
	 * @return
	 * @throws Exception
	 */
	JsonArray getGPSTaskStates(Context context, String strObjectId, String sPolicy, String sCurrent) throws Exception {
		JsonArrayBuilder jsonStatesArray = Json.createArrayBuilder();
        DomainObject dObject    = DomainObject.newInstance(context, strObjectId);
        StateList sList 	= dObject.getStates(context);
        Access access 		= dObject.getAccessMask(context);
        boolean bAccessPromote 	= access.hasPromoteAccess();
        boolean bAccessDemote 	= access.hasDemoteAccess();
		
		boolean isUserAgentContextStr = false;
		try {
			ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, PERSON_USER_AGENT),DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			isUserAgentContextStr = true;
	
	        // remove any hidden states
	        List<String> hiddenStateNames = CacheManager.getInstance().getValue(context, CacheManager._entityNames.HIDDEN_STATES, sPolicy);
	        List<State> hiddenStates = new ArrayList<>();
	        if(!hiddenStateNames.isEmpty()) {
	            for(int i = 0; i < sList.size(); i++) {
	            	State state = sList.get(i);
	            	if (hiddenStateNames.contains(state.getName())) {
	            		hiddenStates.add(state);
	            	}
	            }
	            sList.removeAll(hiddenStates);
	        }
		}finally{
			if(isUserAgentContextStr){
				ContextUtil.popContext(context);
				isUserAgentContextStr = false;
			}
		}
		
        int iCurrent = 0;
        for (int i = 0; i < sList.size(); i++) {
            State state = sList.get(i);
            String sStateName = state.getName();
            if(sStateName.equals(sCurrent)) {
                iCurrent = i;
                break;
            }
        }

        if(bAccessDemote && iCurrent > 0) {
        	State statePrev = sList.get(iCurrent - 1);
        	jsonStatesArray.add(getStateJsonObj(context, sPolicy, statePrev, false, OPERATION_DEMOTE));
        }
        
        State stateCurrent = sList.get(iCurrent);
        jsonStatesArray.add(getStateJsonObj(context, sPolicy, stateCurrent, true, ""));

        if(bAccessPromote && (iCurrent < sList.size() - 1)) {
            State stateNext = sList.get(iCurrent + 1);
            jsonStatesArray.add(getStateJsonObj(context, sPolicy, stateNext, false, OPERATION_PROMOTE));
        }

        return  jsonStatesArray.build();

    }
	
	/**
	 * Method to get State Json object with actual and display values
	 * @param context
	 * @param sPolicy
	 * @param stateObj
	 * @param isCurrent
	 * @return
	 * @throws MatrixException
	 */
	JsonObject getStateJsonObj(Context context, String sPolicy, State stateObj, boolean isCurrent, String strOperation) throws MatrixException {
    	String strStateActualName = stateObj.getName();
		String sLanguage = context.getSession().getLanguage();
    	String strStateDisplayName = EnoviaResourceBundle.getStateI18NString(context, sPolicy, strStateActualName, sLanguage);
    	
    	JsonObjectBuilder jsonStateObj = Json.createObjectBuilder();
		jsonStateObj.add(KEY_VALUE, strStateDisplayName);
		jsonStateObj.add(DomainConstants.SELECT_NAME, strStateActualName);
		jsonStateObj.add(KEY_IS_CURRENT, isCurrent);
		jsonStateObj.add(KEY_OPERATION, strOperation);
		return jsonStateObj.build();
		
	}

	/**
	 * 
	 * @param context
	 * @param doObject
	 * @param jsonObjArray
	 * @param strSelect 
	 * @throws FrameworkException 
	 */
	void updateRelatedProductsForTask(Context context, DomainObject doObject, JsonObjectBuilder jsonPropertiesObj, String strSelect) throws FrameworkException {
				
		StringList slObjectSelects = new StringList();
		slObjectSelects.add(SELECT_PHYSICAL_ID);
		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		
		StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);
		
		MapList mlRelatedProductsFortask = getRelatedObjectsForGPSTask(context, doObject, slObjectSelects, slRelSelects);
		
		JsonArrayBuilder jsonProductArray = Json.createArrayBuilder();
		StringBuilder sbNameBuilder = new StringBuilder();
		for(int i=0; i<mlRelatedProductsFortask.size(); i++) {
			Map<?, ?> mpObjMap = (Map<?, ?>) mlRelatedProductsFortask.get(i);
			sbNameBuilder.append(mpObjMap.get(DomainConstants.SELECT_NAME)).append(KEY_COMMA_SEPARATOR);
			JsonObjectBuilder jsonTemp = Json.createObjectBuilder();
			for(int j=0; j<slObjectSelects.size(); j++) {
				String strKey = slObjectSelects.get(j);
				jsonTemp.add(strKey, checkNullValueforString((String) mpObjMap.get(strKey)));
			}
			jsonProductArray.add(jsonTemp);
		}
		
		String strProdNames = sbNameBuilder.toString();
		if (UIUtil.isNotNullAndNotEmpty(strProdNames)) {
			strProdNames = strProdNames.substring(0, strProdNames.length() - 1);
		}

		jsonPropertiesObj.add(strSelect, strProdNames);
		jsonPropertiesObj.add(KEY_OBJ_DETAILS, jsonProductArray);
	}
	
	/**
	 * Method to perform promote or demote operations on GPS Task
	 * @param context
	 * @param strObjectId
	 * @param strOperation
	 * @return
	 * @throws Exception 
	 */
	String promotedemoteGPSTask(Context context, String strObjectId, String strOperation) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strOperation)) {
			try {
				DomainObject dobGPSTask = DomainObject.newInstance(context, strObjectId);
				if(OPERATION_PROMOTE.equals(strOperation)) {
					dobGPSTask.promote(context);
				} else {
					dobGPSTask.demote(context);
				}
		        
				StringList slObjSelectables = new StringList(2);
				slObjSelectables.add(DomainConstants.SELECT_CURRENT);
		        slObjSelectables.add(DomainConstants.SELECT_POLICY);

				DomainObject doObject = DomainObject.newInstance(context, strObjectId);
				Map<?, ?> objMap = doObject.getInfo(context, slObjSelectables);
				String strPolicy = (String) objMap.get(DomainConstants.SELECT_POLICY);
				String strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
				JsonArray jsonStatesArray = getGPSTaskStates(context, strObjectId, strPolicy, strCurrent);
				jsonStatus.add(KEY_STATES, jsonStatesArray);
				jsonStatus.add(KEY_STATUS, VALUE_SUCCESS);
				
			}catch(Exception exp) {
				isExceptionOccurred = true;				
				sbMessage.append(exp.getMessage()).append("\n");
			}
		}
		
		if(isExceptionOccurred) {
			createErrorMessage(context, sbMessage, jsonStatus);
		}
		return jsonStatus.build().toString();
	}

	/**
	 * Method to get MQL notices
	 * 
	 * @param context
	 * @param sbMessage
	 * @param jsonStatus
	 * @throws MatrixException
	 */
	void createErrorMessage(Context context, StringBuilder sbMessage, JsonObjectBuilder jsonStatus)
			throws MatrixException {
		context.updateClientTasks();
		ClientTaskList ctlLoggedInUserTaskList = context.getClientTasks();
		ClientTaskItr clientTaskItr = new ClientTaskItr(ctlLoggedInUserTaskList);
		while (clientTaskItr.next()) {
			ClientTask ctUserTask = clientTaskItr.obj();
			String strNotice = ctUserTask.getTaskData();
			sbMessage.append(strNotice).append("\n");

		}

		context.clearClientTasks();

		jsonStatus.add(KEY_STATUS, VALUE_FAILED);
		jsonStatus.add(KEY_ERROR, sbMessage.toString());

	}
	
	/**
	 * Method to check if a dropped Part is in valid state or not
	 * @param context
	 * @param slRelatedIds, ids of dropped parts
	 * @return nothing
	 * @throws Exception 
	 */
	void checkIfValidPartsDropped(Context context, StringList slRelatedIds ) throws Exception{
		StringList slPartIds = new StringList();
		StringList objectSelects = new StringList(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		objectSelects.add(DomainConstants.SELECT_CURRENT);
		StringList slOIDTypeList;
		Map<?, ?> objMap;
		DomainObject dobRelatedObj;
		String strObjectId;
		String strCurrent;
		
		if(null != slRelatedIds)
		{
			for (int i = 0; i < slRelatedIds.size(); i++) {
				String strObjectIdType = slRelatedIds.get(i);
				if(strObjectIdType.contains(KEY_PIPE_SEPARATOR)) {
					slOIDTypeList = StringUtil.split(strObjectIdType, KEY_PIPE_SEPARATOR);
					strObjectId = slOIDTypeList.get(0);						
				} else {
					strObjectId = strObjectIdType;
				}
				
				if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
					dobRelatedObj = DomainObject.newInstance(context, strObjectId);
					objMap = dobRelatedObj.getInfo(context, objectSelects);
					strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
					if(!STATE_APPROVED.equals(strCurrent) && !STATE_RELEASE.equals(strCurrent)){
						slPartIds.add((String) objMap.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
					}
				}
			}
			
			if(!slPartIds.isEmpty()) {
				   throw new Exception(ERROR_MSG_NOT_VALID_PART1 + slPartIds.toString() + ERROR_MSG_NOT_VALID_PART2);
			}
		}
		return;
	}
	
}
