package com.pg.widgets.learningplan;

import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.SelectList;
import matrix.util.StringList;

/**
 * @since 2018x.5
 * @author 
 *
 */
 class PGLearningPlanUtil {
	
	 static final String TYPE_REQUIREMENT_SPECIFICATION = PropertyUtil.getSchemaProperty(null,"type_SoftwareRequirementSpecification");
	 static final String TYPE_CHAPTER = PropertyUtil.getSchemaProperty(null,"type_Chapter");
	 static final String TYPE_TEST_CASE = PropertyUtil.getSchemaProperty(null,"type_TestCase");
	 static final String TYPE_REQUIREMENT = PropertyUtil.getSchemaProperty(null,"type_Requirement");
	 static final String TYPE_TEST_EXECUTION = PropertyUtil.getSchemaProperty(null,"type_TestExecution");
	 static final String TYPE_SIMULATION = PropertyUtil.getSchemaProperty(null,"type_Simulation");
	
	 static final String RELATIONSHIP_SUB_TEST_CASE = PropertyUtil.getSchemaProperty(null,"relationship_SubTestCase");
	 static final String RELATIONSHIP_SPECIFICATION_STRUCTURE = PropertyUtil.getSchemaProperty(null,"relationship_SpecificationStructure");
	 static final String RELATIONSHIP_SUB_REQUIREMENT = PropertyUtil.getSchemaProperty(null,"relationship_RequirementBreakdown");
	 static final String RELATIONSHIP_DERIVED_REQUIREMENT= PropertyUtil.getSchemaProperty(null,"relationship_DerivedRequirement");
	 static final String RELATIONSHIP_REQUIREMENT_VALIDATION = PropertyUtil.getSchemaProperty(null,"relationship_RequirementValidation");
	 static final String RELATIONSHIP_TEST_EXECUTION_TEST_CASE = PropertyUtil.getSchemaProperty(null,"relationship_TestExecutionTestCase");
	 static final String RELATIONSHIP_REFERENECED_SIMULATIONS = PropertyUtil.getSchemaProperty(null,"relationship_ReferencedSimulations");
	
	 static final String KEY_SIMULATION_INPUT = "SimulationInput";
	 static final String KEY_SIMULATION_OUTPUT = "SimulationOutput";
	
	 static final String RELATIONSHIP_SIMULATION_INPUT = PropertyUtil.getSchemaProperty(null,"relationship_SimulationInput");
	 static final String RELATIONSHIP_SIMULATION_OUTPUT = PropertyUtil.getSchemaProperty(null,"relationship_SimulationOutput");
	
	 static final String SELECT_RELATED_SIMULATION_INPUT_NAME = "from["+RELATIONSHIP_SIMULATION_INPUT+"].to.name";
	 static final String SELECT_RELATED_SIMULATION_INPUT_TYPE = "from["+RELATIONSHIP_SIMULATION_INPUT+"].to.type";
	 static final String SELECT_RELATED_SIMULATION_INPUT_PID = "from["+RELATIONSHIP_SIMULATION_INPUT+"].to.physicalid";
	
	 static final String SELECT_RELATED_SIMULATION_OUTPUT_NAME = "from["+RELATIONSHIP_SIMULATION_OUTPUT+"].to.name";
	 static final String SELECT_RELATED_SIMULATION_OUTPUT_TYPE = "from["+RELATIONSHIP_SIMULATION_OUTPUT+"].to.type";
	 static final String SELECT_RELATED_SIMULATION_OUTPUT_PID = "from["+RELATIONSHIP_SIMULATION_OUTPUT+"].to.physicalid";
	
	 static final String RELATIONSHIP_PGVPDTESTCASETESTMETHOD= PropertyUtil.getSchemaProperty(null,"relationship_pgVPDTestCaseTestMethod");
	 static final String TYPE_TEST_METHOD_SPECIFICATION = PropertyUtil.getSchemaProperty(null,"type_TestMethodSpecification");
	 static final String SELECT_RELATED_TEST_METHOD_SPEC_NAME = "from["+RELATIONSHIP_PGVPDTESTCASETESTMETHOD+"].to["+TYPE_TEST_METHOD_SPECIFICATION+"].name";
	 static final String SELECT_RELATED_TEST_METHOD_SPEC_TYPE = "from["+RELATIONSHIP_PGVPDTESTCASETESTMETHOD+"].to["+TYPE_TEST_METHOD_SPECIFICATION+"].type";
	 static final String SELECT_RELATED_TEST_METHOD_SPEC_PID = "from["+RELATIONSHIP_PGVPDTESTCASETESTMETHOD+"].to["+TYPE_TEST_METHOD_SPECIFICATION+"].physicalid";

	 static final String SELECT_PHYSICAL_ID  = "physicalid";
	 static final String KEY_SELECTS_SEPARATOR  = "-";
	 static final String KEY_PIPE_SEPARATOR  = "|";
	 static final String KEY_ARG_HEADER  = "headers";
	 static final String KEY_ARG_ID  = "objectId";
	 static final String KEY_HEADER_INFO  = "headerInfo";
	 static final String KEY_DATA  = "data";
	 static final String KEY_DISPLAY_TYPE  = "displayType";
	
	 static final String SELECT_ATTRIBUTE_TITLE = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE);
	 static final String ATTRIBUTE_PGSLM_VDV_RESULTS = PropertyUtil.getSchemaProperty(null, "attribute_SCEATTpgSLMVDVResults");
	 static final String SELECT_ATTRIBUTE_PGSLM_VDV_RESULTS = DomainObject.getAttributeSelect(ATTRIBUTE_PGSLM_VDV_RESULTS);
	 static final String ATTRIBUTE_PGSLM_VDV_RESULTS_COMMENTS = PropertyUtil.getSchemaProperty(null,"attribute_SCEATTpgSLMVDVResultsComments");
	 static final String SELECT_ATTRIBUTE_PGSLM_VDV_RESULTS_COMMENTS = DomainObject.getAttributeSelect(ATTRIBUTE_PGSLM_VDV_RESULTS_COMMENTS);
	 static final String ATTRIBUTE_PGSLM_VDV_PROJECT = PropertyUtil.getSchemaProperty(null,"attribute_SCEATTpgSLMVDVProject");
	 static final String SELECT_ATTRIBUTE_PGSLM_VDV_PROJECT = DomainObject.getAttributeSelect(ATTRIBUTE_PGSLM_VDV_PROJECT);
	
	 /**
	  * Method to expand the structure of Requirement Specification to get all the related objects info in a JSON
	  * @param context : eMatrix context
	  * @param strRSPObjectId : String object id of Requirement Specification
	  * @param strRSPSelectsForHeader : String object selectables for RSP
	  * @return : String JSON response with all related data information for RSP
	  * @throws Exception
	  */
	 String getRSPStructure(Context context, String strRSPObjectId, String strRSPSelectsForHeader) throws Exception {	
		
		try {
			JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
			
			if (UIUtil.isNotNullAndNotEmpty(strRSPObjectId)) {
			DomainObject domObjRSP = DomainObject.newInstance(context, strRSPObjectId);
			
			Pattern typePattern = new Pattern(TYPE_REQUIREMENT_SPECIFICATION);
			typePattern.addPattern(TYPE_CHAPTER);
			typePattern.addPattern(TYPE_REQUIREMENT);
			typePattern.addPattern(TYPE_TEST_CASE);
	
			Pattern relPattern = new Pattern(RELATIONSHIP_SUB_TEST_CASE);
			relPattern.addPattern(RELATIONSHIP_SPECIFICATION_STRUCTURE);
			relPattern.addPattern(RELATIONSHIP_SUB_REQUIREMENT);
			relPattern.addPattern(RELATIONSHIP_DERIVED_REQUIREMENT);
			relPattern.addPattern(RELATIONSHIP_REQUIREMENT_VALIDATION);
			
			StringList slObjSelects = new SelectList();
			slObjSelects.add(DomainConstants.SELECT_TYPE);
			slObjSelects.add(DomainConstants.SELECT_NAME);
			slObjSelects.add(DomainConstants.SELECT_REVISION);
			slObjSelects.add(DomainConstants.SELECT_ID);
			slObjSelects.add(DomainConstants.SELECT_CURRENT);
			slObjSelects.add(DomainConstants.SELECT_DESCRIPTION);
			slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
			slObjSelects.add(DomainConstants.SELECT_MODIFIED);
			slObjSelects.add(SELECT_PHYSICAL_ID);
			slObjSelects.add(SELECT_ATTRIBUTE_TITLE);
			slObjSelects.add(SELECT_RELATED_TEST_METHOD_SPEC_TYPE);
			slObjSelects.add(SELECT_RELATED_TEST_METHOD_SPEC_NAME);
			slObjSelects.add(SELECT_RELATED_TEST_METHOD_SPEC_PID);
			
			StringList slRelSelects = new SelectList();
			slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			String strObjWhereClause = null;
			String strRelObjWhereClause = null;
			Pattern postTypePattern = null;
			Pattern postRelPattern = null;
			MapList mlRSPRelObjs = domObjRSP.getRelatedObjects(
					context, 								//the eMatrix Context object
					relPattern.getPattern(),				//Relationship pattern
					typePattern.getPattern(), 				//Type pattern
					slObjSelects, 							//Object selects
					slRelSelects, 							//Relationship selects
					false, 									//get To relationships
					true, 									//get From relationships
					(short) 0, 								//the number of levels to expand, 0 equals expand all.
					strObjWhereClause, 						//Object where clause
					strRelObjWhereClause, 					//Relationship where clause
					0, 										//Limit : The max number of Objects to get in the exapnd.0 to return all the data available
					postTypePattern, 						//filter for business types we want returned
					postRelPattern, 						//filter for relationships we want returned
					null);									//a name/value pair that is used to filter data
	
				JsonArray jsonArrayCompleteStructure = getCompleteStructure(context, mlRSPRelObjs, 0);
				JsonArray jsonArrayFlatTable = getFormattedStructure(context, jsonArrayCompleteStructure);
				
				if (UIUtil.isNotNullAndNotEmpty(strRSPSelectsForHeader)) {
					StringList slRSPHeaderSelect = FrameworkUtil.split(strRSPSelectsForHeader, ",");
					Map<?, ?> mpHeaderInfo = domObjRSP.getInfo(context, slRSPHeaderSelect);
					JsonObject jsonRSPHeaderInfo = convertMapToJsonObj(Json.createObjectBuilder().build(), mpHeaderInfo, DomainConstants.EMPTY_STRING);
					
					jsonObjOutput.add(KEY_HEADER_INFO, jsonRSPHeaderInfo);
				}
				
				jsonObjOutput.add(KEY_DATA, jsonArrayFlatTable);
			}
			
			return jsonObjOutput.build().toString();
		
		} catch (Exception e) {
			throw e;
		}
	}
	
	 /**
	  * Method to get the JSON out put in a particular format
	  * @param context : the eMatrix Context object
	  * @param jsonArrayCompleteStructure : JsonArray with complete expanded data for RSP
	  * @return : JsonArray of expanded data in a particular format
	  * @throws Exception
	  */
	 JsonArray getFormattedStructure(Context context, JsonArray jsonArrayCompleteStructure) throws Exception {
		
		JsonArrayBuilder jsonObjArray = Json.createArrayBuilder();

		JsonObject jsonObjectTemp = null;
		JsonObjectBuilder jsonObjectSelects = null;
		JsonObjectBuilder jsonObjectElement = null;
		
		StringList slSelectInputs = new StringList(3);
		slSelectInputs.add(SELECT_RELATED_SIMULATION_INPUT_TYPE);
		slSelectInputs.add(SELECT_RELATED_SIMULATION_INPUT_NAME);
		slSelectInputs.add(SELECT_RELATED_SIMULATION_INPUT_PID);
		
		StringList slSelectOutputs = new StringList(3);
		slSelectOutputs.add(SELECT_RELATED_SIMULATION_OUTPUT_TYPE);
		slSelectOutputs.add(SELECT_RELATED_SIMULATION_OUTPUT_NAME);
		slSelectOutputs.add(SELECT_RELATED_SIMULATION_OUTPUT_PID);
		
		StringList slTestMethodSpecSelects = new StringList(3);
		slTestMethodSpecSelects.add(SELECT_RELATED_TEST_METHOD_SPEC_TYPE);
		slTestMethodSpecSelects.add(SELECT_RELATED_TEST_METHOD_SPEC_NAME);
		slTestMethodSpecSelects.add(SELECT_RELATED_TEST_METHOD_SPEC_PID);

		StringList slSelectList = new StringList();
		String strKey = DomainConstants.EMPTY_STRING;
		String strKeyValue = DomainConstants.EMPTY_STRING;
		String strTypeValue = DomainConstants.EMPTY_STRING;
		String strTypeDisplayValue = DomainConstants.EMPTY_STRING;
		int iIndex = 1;
		
		for(int i=0; i<jsonArrayCompleteStructure.size(); i++) {
			
			jsonObjectTemp = (JsonObject) jsonArrayCompleteStructure.get(i);
			
			jsonObjectElement= Json.createObjectBuilder();
			jsonObjectSelects= Json.createObjectBuilder();

			strKey = DomainConstants.EMPTY_STRING;
			strKeyValue = DomainConstants.EMPTY_STRING;
			strTypeValue = DomainConstants.EMPTY_STRING;
			strTypeDisplayValue = DomainConstants.EMPTY_STRING;
			iIndex = 1;
			
			for(Iterator<?> iterator = jsonObjectTemp.keySet().iterator(); iterator.hasNext();) {
			    strKey = (String) iterator.next();
			    if(strKey.startsWith(Integer.toString(iIndex)+KEY_SELECTS_SEPARATOR)) {
			    	
				    if(strKey.endsWith(KEY_SELECTS_SEPARATOR+DomainConstants.SELECT_TYPE)) {
				    	strTypeValue = jsonObjectTemp.getString(strKey);
				    	strTypeDisplayValue = EnoviaResourceBundle.getTypeI18NString(context,strTypeValue,context.getSession().getLanguage());
				    }
				    
				    strKeyValue = jsonObjectTemp.getString(strKey);
				    slSelectList = FrameworkUtil.split(strKey, KEY_SELECTS_SEPARATOR); 
				    jsonObjectSelects.add(slSelectList.get(1), strKeyValue);
				    
				    if(!iterator.hasNext()) {
				    	jsonObjectSelects.add(KEY_DISPLAY_TYPE, strTypeDisplayValue);
				    	jsonObjectElement.add(strTypeValue, jsonObjectSelects.build());
				    	
				    	if(TYPE_SIMULATION.equals(strTypeValue)) {
				    		
				    		if(jsonObjectSelects.build().containsKey(SELECT_RELATED_SIMULATION_INPUT_TYPE)) {
					    		JsonArray jsonSimInputArray = convertMultiSelectsToJson(context, slSelectInputs,jsonObjectSelects.build());
					    		jsonObjectSelects.add(KEY_SIMULATION_INPUT, jsonSimInputArray);
				    		}

				    		if(jsonObjectSelects.build().containsKey(SELECT_RELATED_SIMULATION_OUTPUT_TYPE)) {
					    		JsonArray jsonSimOutputArray = convertMultiSelectsToJson(context, slSelectOutputs,jsonObjectSelects.build());
					    		jsonObjectSelects.add(KEY_SIMULATION_OUTPUT, jsonSimOutputArray);
				    		}
				    		jsonObjectElement.add(strTypeValue, jsonObjectSelects.build());
				    	} else if(TYPE_TEST_CASE.equals(strTypeValue) && jsonObjectSelects.build().containsKey(SELECT_RELATED_TEST_METHOD_SPEC_TYPE)) {
				    		JsonArray jsonTestMthodSpecArray = convertMultiSelectsToJson(context, slTestMethodSpecSelects,jsonObjectSelects.build());
				    		jsonObjectSelects.add(TYPE_TEST_METHOD_SPECIFICATION, jsonTestMthodSpecArray);
				    		jsonObjectElement.add(strTypeValue, jsonObjectSelects.build());
				    	}
				    }
			    } else {
			    	if(TYPE_TEST_CASE.equals(strTypeValue) && jsonObjectSelects.build().containsKey(SELECT_RELATED_TEST_METHOD_SPEC_TYPE)) {
			    		JsonArray jsonTestMthodSpecArray = convertMultiSelectsToJson(context, slTestMethodSpecSelects,jsonObjectSelects.build());
			    		jsonObjectSelects.add(TYPE_TEST_METHOD_SPECIFICATION, jsonTestMthodSpecArray);
			    	}
			    	jsonObjectSelects.add(KEY_DISPLAY_TYPE, strTypeDisplayValue);
			    	jsonObjectElement.add(strTypeValue, jsonObjectSelects.build());
			    	
			    	iIndex++;
			    	jsonObjectSelects= Json.createObjectBuilder();
			    	
				    if(strKey.endsWith(KEY_SELECTS_SEPARATOR+DomainConstants.SELECT_TYPE)) {
				    	strTypeValue = jsonObjectTemp.getString(strKey);
				    	strTypeDisplayValue = EnoviaResourceBundle.getTypeI18NString(context,strTypeValue,context.getSession().getLanguage());
				    }
				    
				    strKeyValue = jsonObjectTemp.getString(strKey);
				    slSelectList = FrameworkUtil.split(strKey, KEY_SELECTS_SEPARATOR); 
				    jsonObjectSelects.add(slSelectList.get(1), strKeyValue);
			    	
			    }
			}
			
			jsonObjArray.add(jsonObjectElement.build());
		}
		
		return jsonObjArray.build();
	}

	 /**
	  * Method used to convert selectable values for Simulation Input/Outputs and Test Method Specifications to JSON Arrays
	  * @param context : the eMatrix Context object
	  * @param slSelects : StringList of selectable keys
	  * @param jsonParentObj : JsonObject which has pipe separated values for selectable keys (JsonObject of Simulation or 'Test Case')
	  * @return : JsonArray of JsonObject of Simulation Input/Outputs or 'Test Method Specification'
	  * @throws Exception
	  */
	 JsonArray convertMultiSelectsToJson(Context context, StringList slSelects,JsonObject jsonParentObj) throws Exception {
		JsonObjectBuilder jsonObjectElement= Json.createObjectBuilder();
		JsonArrayBuilder jsonObjArray = Json.createArrayBuilder();
		
		String strTypes = jsonParentObj.getString(slSelects.get(0));
		String strNames = jsonParentObj.getString(slSelects.get(1));
		String strPIDs = jsonParentObj.getString(slSelects.get(2));
		
		StringList slTypesList = FrameworkUtil.split(strTypes, KEY_PIPE_SEPARATOR); 
		StringList slNamesList = FrameworkUtil.split(strNames, KEY_PIPE_SEPARATOR); 
		StringList slPIDsList = FrameworkUtil.split(strPIDs, KEY_PIPE_SEPARATOR); 
		
		int iTypeListSize = slTypesList.size();
		
		if(slNamesList.size() == iTypeListSize && slPIDsList.size() == iTypeListSize) {
			String strType = DomainConstants.EMPTY_STRING;
			String strTypeDisplayValue = DomainConstants.EMPTY_STRING;
			for(int i=0; i<iTypeListSize; i++) {
				jsonObjectElement= Json.createObjectBuilder();
				strType = slTypesList.get(i);
				strTypeDisplayValue = EnoviaResourceBundle.getTypeI18NString(context,strType,context.getSession().getLanguage());
				jsonObjectElement.add(DomainConstants.SELECT_TYPE, strType);
				jsonObjectElement.add(KEY_DISPLAY_TYPE, strTypeDisplayValue);
				jsonObjectElement.add(DomainConstants.SELECT_NAME, slNamesList.get(i));
				jsonObjectElement.add(SELECT_PHYSICAL_ID, slPIDsList.get(i));
				jsonObjArray.add(jsonObjectElement);
			}
			
		}
		
		return jsonObjArray.build();
		
	}

	 /**
	  * This method converts a MapList which is a result of getRelatedObjects API, to a JsonArray
	  * @param mlInput : MapList which is result of getRelatedObjects operation while expanding RSP till 'Test Case' and from 'Test Case' till simulation 
	  * @param iLevel : int current level
	  * @return : JsonArray of expanded data
	  */
	 JsonArray convertMapListToJsonFlatTable(MapList mlInput, int iLevel) {
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
			
			//Leaf level node condition
			if(nLevelCurrent <= nLevelPrev) {
				if(!stackJsonObj.empty()) {
					jsonObjArray.add(stackJsonObj.pop());	
				}
				
				for (int i = nLevelCurrent; i < nLevelPrev; i++) {
					stackJsonObj.pop();
				}
			}
			
			stackJsonObj.push(convertMapToJsonObj(stackJsonObj.empty() ? Json.createObjectBuilder().build() : stackJsonObj.peek(), tempMap, Integer.toString(nLevelAppend)+KEY_SELECTS_SEPARATOR));
			
			nLevelPrev = nLevelCurrent;
		}
		if(!stackJsonObj.empty()) {
			jsonObjArray.add(stackJsonObj.pop());	
		}
		return jsonObjArray.build();
	}

	 /**
	  * This method creates complete structure of RSP till Simulation by expanding RSP till 'Test Case' and from 'Test Case' till Simulation via 'Test Execution'
	  * @param context : the eMatrix Context object
	  * @param mlInput : MapList which has expanded details from RSP to 'Test Case'
	  * @param iLevel : int current level 
	  * @return : JsonArray with complete structure from RSP till Simulation via 'Test Case' 
	  * @throws Exception
	  */
	 JsonArray getCompleteStructure(Context context, MapList mlInput, int iLevel) throws Exception {
		
		Pattern typePattern = new Pattern(TYPE_TEST_EXECUTION);
		typePattern.addPattern(TYPE_SIMULATION);
		
		Pattern relPattern = new Pattern(RELATIONSHIP_TEST_EXECUTION_TEST_CASE);
		relPattern.addPattern(RELATIONSHIP_REFERENECED_SIMULATIONS);
		
		StringList slObjSelects = new SelectList();
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(SELECT_PHYSICAL_ID);
		slObjSelects.add(SELECT_ATTRIBUTE_TITLE);
		slObjSelects.add(SELECT_RELATED_SIMULATION_INPUT_TYPE);
		slObjSelects.add(SELECT_RELATED_SIMULATION_INPUT_NAME);
		slObjSelects.add(SELECT_RELATED_SIMULATION_INPUT_PID);
		slObjSelects.add(SELECT_RELATED_SIMULATION_OUTPUT_TYPE);
		slObjSelects.add(SELECT_RELATED_SIMULATION_OUTPUT_NAME);
		slObjSelects.add(SELECT_RELATED_SIMULATION_OUTPUT_PID);
		slObjSelects.add(SELECT_ATTRIBUTE_PGSLM_VDV_RESULTS);
		slObjSelects.add(SELECT_ATTRIBUTE_PGSLM_VDV_RESULTS_COMMENTS);
		slObjSelects.add(SELECT_ATTRIBUTE_PGSLM_VDV_PROJECT);
		
		StringList slRelSelects = new SelectList();
		slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		
		Stack<JsonObject> stackJsonObj = new Stack<>();
		JsonArrayBuilder jsonObjArray = Json.createArrayBuilder();
		int nLevelCurrent = 1;
		int nLevelPrev = 1;
		int nLevelAppend = 0;
		boolean isTestExecutionExist = false;
		String strId = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		String strTypePrev = DomainConstants.EMPTY_STRING;
		for (Iterator<?> iterator = mlInput.iterator(); iterator.hasNext();) {

			Map<?, ?> tempMap = (Map<?, ?>) iterator.next();
			String strLevel = (String) tempMap.get(DomainConstants.SELECT_LEVEL);
			strType = (String) tempMap.get(DomainConstants.SELECT_TYPE);
			strId = (String) tempMap.get(DomainConstants.SELECT_ID);
			nLevelCurrent = Integer.parseInt(strLevel);
			nLevelAppend = nLevelCurrent + iLevel;
			
			//Leaf level node condition
			if(nLevelCurrent <= nLevelPrev) {
				if(!stackJsonObj.empty() && TYPE_TEST_CASE.equals(strTypePrev) && !isTestExecutionExist) {
					jsonObjArray.add(stackJsonObj.pop());	
				}
				
				for (int i = nLevelCurrent; i < nLevelPrev; i++) {
					stackJsonObj.pop();
				}
			}
			
			stackJsonObj.push(convertMapToJsonObj(stackJsonObj.empty() ? Json.createObjectBuilder().build() : stackJsonObj.peek(), tempMap, Integer.toString(nLevelAppend)+KEY_SELECTS_SEPARATOR));
			
			nLevelPrev = nLevelCurrent;
			strTypePrev = strType;
			
			//Code to expand and merge 'Test Case', till Simulation via 'Test Execution' : Start
			if(TYPE_TEST_CASE.equals(strType)) {
				
				if (UIUtil.isNotNullAndNotEmpty(strId)) {
										
					String strObjWhereClause = null;
					String strRelObjWhereClause = null;
					Pattern postTypePattern = null;
					Pattern postRelPattern = null;

					JsonObject jsonObjectToAppend;
					JsonObject jsonObjectMerged;
					
					DomainObject dobTestCase = DomainObject.newInstance(context, strId);
					
					MapList mlRelTESim = dobTestCase.getRelatedObjects(
							context, 						//the eMatrix Context object
							relPattern.getPattern(), 		//Relationship pattern
							typePattern.getPattern(), 		//Type pattern
							slObjSelects, 					//Object selects
							slRelSelects, 					//Relationship selects
							true, 							//get To relationships
							true, 							//get From relationships
							(short) 2, 						//the number of levels to expand, 0 equals expand all.
							strObjWhereClause, 				//Object where clause
							strRelObjWhereClause, 			//Relationship where clause
							0, 								//Limit : The max number of Objects to get in the exapnd.0 to return all the data available
							postTypePattern, 				//filter for business types we want returned
							postRelPattern, 				//filter for relationships we want returned
							null);							//a name/value pair that is used to filter data
					
					if(mlRelTESim.size() > 0) {
						JsonArray jsonArrayTESim = convertMapListToJsonFlatTable(mlRelTESim, nLevelCurrent);
						
						for(int j=0; j<jsonArrayTESim.size(); j++) {
							jsonObjectToAppend = (JsonObject) jsonArrayTESim.get(j);
							jsonObjectMerged = mergeJsonObjects(stackJsonObj.peek(), jsonObjectToAppend);
							
							//Add jsonObj to final jsonArray
							jsonObjArray.add(jsonObjectMerged);
						}
						isTestExecutionExist = true;
					} else {
						isTestExecutionExist = false;
					}
					
					
				} 
				
			}
			//Code to expand and merge 'Test Case', till Simulation via 'Test Execution' : End
		}
		if(!stackJsonObj.empty() && TYPE_TEST_CASE.equals(strTypePrev) && !isTestExecutionExist) {
			jsonObjArray.add(stackJsonObj.pop());	
		}
		return jsonObjArray.build();
	}
	
	 /**
	  * Method used convert Map to JsonObject
	  * @param inputJsonObj : JsonObject in which converted JsonObject from Map will be merged
	  * @param inputMap : Map to converted to JsonObject
	  * @param strLevel : String current level of object expanded
	  * @return : JsonObject merged with converted Map and parent inputJsonObj
	  */
	 JsonObject convertMapToJsonObj(JsonObject inputJsonObj, Map<?, ?> inputMap, String strLevel) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		inputMap.entrySet().forEach(e -> builder.add(strLevel+(String)e.getKey(), ((e.getValue() instanceof StringList) ? getStringFromSL((StringList) e.getValue()) : (String)e.getValue())));
		return builder.build();
	}

	 /**
	  * Method to merge two JSON objects
	  * @param inputJsonObj : JsonObject input
	  * @param appendJsonObj : JsonObject to merged with inputJsonObj
	  * @return : JsonObject merged
	  */
	 JsonObject mergeJsonObjects(JsonObject inputJsonObj, JsonObject appendJsonObj) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		appendJsonObj.entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		return builder.build();
	}
	
	 /**
	  * This method is used to convert StringList to pipe separated String
	  * @param slSelectValuelist : StringList to be converted
	  * @return : Pipe line separated String
	  */
	 String getStringFromSL(StringList slSelectValuelist) {
		
		StringBuilder sbSelectValue = new StringBuilder();
		String strSelectValue = DomainConstants.EMPTY_STRING;
		String strReturnValue = DomainConstants.EMPTY_STRING;
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
