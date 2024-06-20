/*
 * PGTestCaseManagementUtil.java
 * 
 * Added by Dashboard Team
 * For Test Case Management Widget related Webservice
 * 
 */

package com.pg.widgets.learningplan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.productline.TestExecution;

import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.StringList;

import com.matrixone.apps.productline.UnifiedAutonamingServices;

/**
 * Class PGTestCaseManagementUtil has all the methods defined for the 'Test Case Management' widget activities.
 * 
 * @Since 2018x.5
 * @author 
 *
 */
class PGTestCaseManagementUtil
{
	
	 static final String TYPE_TEST_CASE = PropertyUtil.getSchemaProperty(null,"type_TestCase");
	 static final String TYPE_REQUIREMENT = PropertyUtil.getSchemaProperty(null,"type_Requirement");
	 static final String TYPE_TEST_METHOD_SPEC = PropertyUtil.getSchemaProperty(null,"type_TestMethodSpecification");
	 static final String TYPE_TEST_EXECUTION = PropertyUtil.getSchemaProperty(null,"type_TestExecution");
	 static final String TYPE_SIMULATION_TEMPLATE = PropertyUtil.getSchemaProperty(null,"type_SimulationTemplate");
	 static final String TYPE_SIMULATION = PropertyUtil.getSchemaProperty(null,"type_Simulation");
	 static final String TYPE_PARAMETER = PropertyUtil.getSchemaProperty(null,"type_PlmParameter");
			
	 static final String POLICY_TEST_CASE = PropertyUtil.getSchemaProperty(null,"policy_TestCase");
	 static final String POLICY_TEST_EXECUTION = PropertyUtil.getSchemaProperty(null,"policy_TestExecution");
	
	 static final String ATTRTIBUTE_ESTIMATED_START_DATE = PropertyUtil.getSchemaProperty(null,"attribute_EstimatedStartDate");
	 static final String ATTRTIBUTE_ESTIMATED_END_DATE = PropertyUtil.getSchemaProperty(null,"attribute_EstimatedEndDate");
	
	 static final String RELATIONSHIP_REQUIREMENT_VALIDATION = PropertyUtil.getSchemaProperty(null,"relationship_RequirementValidation");
	 static final String RELATIONSHIP_TEST_EXECUTION_TEST_CASE = PropertyUtil.getSchemaProperty(null,"relationship_TestExecutionTestCase");
	 static final String RELATIONSHIP_SIMULATION_OBJECT_REFERENCE = PropertyUtil.getSchemaProperty(null,"relationship_SimulationObjectReference");
	 static final String RELATIONSHIP_VPD_TEST_CASE_TEST_METHOD = PropertyUtil.getSchemaProperty(null,"relationship_pgVPDTestCaseTestMethod");
	 static final String RELATIONSHIP_PARAMETER_AGGREGATION = PropertyUtil.getSchemaProperty(null,"relationship_ParameterAggregation");
	 static final String RELATIONSHIP_REFERENCED_SIMULATIONS = PropertyUtil.getSchemaProperty(null,"relationship_ReferencedSimulations");
	 static final String RELATIONSHIP_PARAMETER_USAGE = PropertyUtil.getSchemaProperty(null,"relationship_ParameterUsage");
		
	 
	 static final String SELECT_ATTRIBUTE_TITLE = DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE);
	 static final String SELECT_PHYSICAL_ID  = "physicalid";
	 
	 static final String TEST_CASE_DEFAULT_REVISION  = "001";
	 static final String TEST_EXECUTION_DEFAULT_REVISION  = "1";
	
	 static final String KEY_HIERARCHY  = "hierarchy";	
	 static final String KEY_OUT_PUT = "output";
	 static final String KEY_TRUE = "true";
	 static final String KEY_ALL = "All";
	 static final String KEY_SIMPLE_DATEFORMAT = "yyyy-mm-dd";
	 static final String KEY_TE_COPY_PARAM  = "copyParam";
	 static final String KEY_TE_COPY_PARAM_VALUE  = "on";
	 static final String KEY_TE_TCID  = "testCaseId";
	 static final String KEY_TE_TEID = "testExeId";
	 static final String KEY_WARNING_MESSAGE = "WarningMessage";
	 static final String EXCEPTION_MESSAGE_ON_CONNECT = "All selected objects are already connected to the current object!";
	 static final String WARNING_MESSAGE = "These are the objects which are already connected and hence ignored: ";
	
	/**
	 * Creates the Test Case and connects the related objects with the created Test Case
	 * @param context : eMatrix context
	 * @param strTitle : String Title of the new Test Case going to be created
	 * @param strDescription : String Description of the new Test Case going to be created
	 * @param strObjSelectables : String object selectables to get current and related object info
	 * @param strObjIdsToConnect : String Info of the object going to be connected with test case
	 * @return : String JSON info of selectables of current and related objects
	 * @throws Exception
	 */
	 String createAndConnectTestCase(Context context, String strTitle, String strDescription, String strObjSelectables, String strObjIdsToConnect) throws Exception 
	{
		String strTestCaseId = DomainConstants.EMPTY_STRING;
		
		try {
			String strName = UnifiedAutonamingServices.autoname(context, TYPE_TEST_CASE);
			DomainObject dobTestCase = DomainObject.newInstance(context);
			dobTestCase.createObject(context, TYPE_TEST_CASE, strName, TEST_CASE_DEFAULT_REVISION, POLICY_TEST_CASE, context.getVault().getName());
			
			strTestCaseId = dobTestCase.getInfo(context,SELECT_PHYSICAL_ID);
			dobTestCase.setDescription(context, strDescription);
			dobTestCase.setAttributeValue(context, DomainConstants.ATTRIBUTE_TITLE, strTitle);
			
			if(UIUtil.isNotNullAndNotEmpty(strObjIdsToConnect))
			{
				connectRelatedObjects(context, dobTestCase, StringUtil.split(strObjIdsToConnect, ","));
			}
			
		} catch (Exception e) {
			throw e;
		}
			
		return getRelatedObjectsForTCorTE(context, TYPE_TEST_EXECUTION, RELATIONSHIP_TEST_EXECUTION_TEST_CASE, strTestCaseId, strObjSelectables,DomainConstants.EMPTY_STRING);
	}
	
	 
	 /**
	  * Method used to maintain type rel mapping for TE and TC
	  * @param strType :String type
	  * @return : Map of type rel mapping
	  */
	 Map<String, String> getTypeRelMapping(String strType) {
		 
			Map<String, String> mpTypeRel = new HashMap<>();
			
			if(TYPE_TEST_CASE.equals(strType)) {
				mpTypeRel.put(TYPE_REQUIREMENT, RELATIONSHIP_REQUIREMENT_VALIDATION);
				mpTypeRel.put(TYPE_TEST_METHOD_SPEC, RELATIONSHIP_VPD_TEST_CASE_TEST_METHOD);
				mpTypeRel.put(TYPE_TEST_EXECUTION, RELATIONSHIP_TEST_EXECUTION_TEST_CASE);
				mpTypeRel.put(TYPE_SIMULATION_TEMPLATE, RELATIONSHIP_SIMULATION_OBJECT_REFERENCE);	
				mpTypeRel.put(TYPE_PARAMETER, RELATIONSHIP_PARAMETER_USAGE);	
				
			} else if(TYPE_TEST_EXECUTION.equals(strType)){
				mpTypeRel.put(TYPE_SIMULATION, RELATIONSHIP_REFERENCED_SIMULATIONS);
				mpTypeRel.put(TYPE_TEST_CASE, RELATIONSHIP_TEST_EXECUTION_TEST_CASE);
				mpTypeRel.put(TYPE_PARAMETER, RELATIONSHIP_PARAMETER_USAGE);	
			}	
			
			return mpTypeRel;
	 }
	 
	/**
	 * Generic method which connects the related objects
	 * @param context  : eMatrix context
	 * @param dobTestCase : DomainObject of the object to be connected with its related objects
	 * @param slRelObjIds : StringList of pipe separated object Ids to be connected
	 * @return : String status
	 * @throws Exception 
	 */
	 void connectRelatedObjects(Context context, DomainObject dobTestCase, StringList slRelObjIds) throws Exception {
		try {
			Map<String, String> mpTypeRel = getTypeRelMapping(TYPE_TEST_CASE);
			StringList slOIDTypeList = new StringList();
			String strObjectId = DomainConstants.EMPTY_STRING;
			String strObjectIdType = DomainConstants.EMPTY_STRING;
			String strType = DomainConstants.EMPTY_STRING; 
			String strRelname = DomainConstants.EMPTY_STRING; 
			DomainObject dobRelatedObj = null;
						
				for(int i=0; i<slRelObjIds.size(); i++) {
					strObjectIdType = slRelObjIds.get(i);
					slOIDTypeList = StringUtil.split(strObjectIdType, "|");
					strObjectId = slOIDTypeList.get(0);
					strType = slOIDTypeList.get(1);
					if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strType)){
						dobRelatedObj = DomainObject.newInstance(context, strObjectId);
						strRelname = mpTypeRel.get(strType);
						if(TYPE_TEST_METHOD_SPEC.equals(strType) || TYPE_SIMULATION_TEMPLATE.equals(strType) || TYPE_PARAMETER.equals(strType)) {
							DomainRelationship.connect(context, dobTestCase, strRelname, dobRelatedObj);
						} else {
							DomainRelationship.connect(context, dobRelatedObj, strRelname, dobTestCase);
						}
					}
				}
			
		}  catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Creates the Test Execution and connects the Test Case to newly created Test Execution
	 * @param context : eMarix context
	 * @param strTitle : String Title of the new Test Execution going to be created
	 * @param strDescription : String strDescription of the new Test Execution going to be created
	 * @param strTestCaseId : String Test Case Ids to be connected
	 * @param strStartDate : String Estimated Start Date for 'Test Execution'
	 * @param strEndDate : String Estimated End Date for 'Test Execution'
	 * @param strCopyParameter : String which has values true or false, indicates whether to copy params from Test Case to Test Execution or not.
	 * @param strObjSelects : String object selectables
	 * @return : String JSON created Test Execution info  along with related objects info
	 * @throws Exception
	 */
	 String createAndConnectTestExecution(Context context, String strTitle, String strDescription, String strTestCaseId, String strStartDate, String strEndDate, String strCopyParameter, String strObjSelects) throws Exception 
	{
		String strTestExecId = DomainConstants.EMPTY_STRING;
		String strTestCaseName = DomainConstants.EMPTY_STRING;
		
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		
		try {
			
			String strName = UnifiedAutonamingServices.autoname(context, TYPE_TEST_EXECUTION);
			DomainObject dobTestExecution = DomainObject.newInstance(context);
			dobTestExecution.createObject(context, TYPE_TEST_EXECUTION, strName, TEST_EXECUTION_DEFAULT_REVISION, POLICY_TEST_EXECUTION, context.getVault().getName());
			
			StringList slObjectSelects = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strObjSelects)) {
				slObjectSelects = StringUtil.split(strObjSelects, ",");
			}
			slObjectSelects.add(SELECT_PHYSICAL_ID);
			Map<?,?> objectInfo = dobTestExecution.getInfo(context, slObjectSelects);

			for(int j=0; j<slObjectSelects.size(); j++) {
				String strSelect = slObjectSelects.get(j);
				jsonOutput.add(strSelect, objectInfo.get(strSelect).toString());
			}	
			
			strTestExecId = objectInfo.get(SELECT_PHYSICAL_ID).toString();
			dobTestExecution.setDescription(context, strDescription);
			
			strTitle = checkNullValueforString(strTitle);
			
			Map<String, String> mpAttributeMap = new HashMap<>();
			mpAttributeMap.put(DomainConstants.ATTRIBUTE_TITLE, strTitle);
			
			SimpleDateFormat initialFormat = new SimpleDateFormat(KEY_SIMPLE_DATEFORMAT);
			SimpleDateFormat ematrixFormat = new SimpleDateFormat( eMatrixDateFormat.getEMatrixDateFormat(), context.getLocale());			
			
			Date date = null;
			if(UIUtil.isNotNullAndNotEmpty(strStartDate))
			{
				date = initialFormat.parse(strStartDate);   	    
				strStartDate = ematrixFormat.format(date);
			}
			
			if(UIUtil.isNotNullAndNotEmpty(strEndDate))
			{
				date = initialFormat.parse(strEndDate);
				strEndDate = ematrixFormat.format(date);
			}			    	  		
			
			mpAttributeMap.put(ATTRTIBUTE_ESTIMATED_START_DATE, strStartDate);
			mpAttributeMap.put(ATTRTIBUTE_ESTIMATED_END_DATE, strEndDate);
			dobTestExecution.setAttributeValues(context, mpAttributeMap);			
			
			if(UIUtil.isNotNullAndNotEmpty(strTestCaseId)) {
				DomainObject dobTestCase = DomainObject.newInstance(context, strTestCaseId);
				DomainRelationship.connect(context, dobTestExecution, RELATIONSHIP_TEST_EXECUTION_TEST_CASE, dobTestCase);
				strTestCaseName = dobTestCase.getName();
				StringList hierarchyList = new StringList(strTestCaseName);				
				hierarchyList.add(strName);		
				
				jsonOutput.add(KEY_HIERARCHY,hierarchyList.toString());
				jsonOutput.add(SELECT_ATTRIBUTE_TITLE, strTitle);
				jsonOutput.add(DomainObject.SELECT_NAME, strName);
				jsonOutput.add(DomainObject.SELECT_TYPE, TYPE_TEST_EXECUTION);
				
				Map<String, String> argsMap = new HashMap<>();
	          	  argsMap.put(KEY_TE_TCID,strTestCaseId);
	          	  argsMap.put(KEY_TE_TEID, strTestExecId);
				if(KEY_TRUE.equalsIgnoreCase(strCopyParameter)) {
		              argsMap.put(KEY_TE_COPY_PARAM, KEY_TE_COPY_PARAM_VALUE);
				}
				TestExecution.createTestExeStructure(context, argsMap);
			}
			
		} catch (Exception e) {
			throw e;
		}
		
		jsonReturnObj.add(KEY_OUT_PUT, jsonOutput.build());
		
		return jsonReturnObj.build().toString();
	}
	
	/**
	 * Generic method which returns related objects info of Test case or Test Execution 
	 * @param context : eMarix context
	 * @param strTypeName : String type pattern
	 * @param strRelName : String relationship pattern
	 * @param strObjectPID : String physical Id of Test case or Test Execution 
	 * @param strObjSelectables : String object selectables
	 * @return : String JSON of related object info along with current object info
	 * @throws Exception
	 */
	 String getRelatedObjectsForTCorTE(Context context, String strTypeName, String strRelName, String strObjectPID, String strObjSelectables, String strWarning) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonTemp = null;
					
			if(UIUtil.isNotNullAndNotEmpty(strObjectPID) && UIUtil.isNotNullAndNotEmpty(strTypeName) && UIUtil.isNotNullAndNotEmpty(strRelName) && UIUtil.isNotNullAndNotEmpty(strObjSelectables)){
				DomainObject dobCurrentObj = DomainObject.newInstance(context, strObjectPID);
				
				StringList slObjectSelects = StringUtil.split(strObjSelectables, ",");
				slObjectSelects.add(SELECT_PHYSICAL_ID);
				slObjectSelects.add(DomainConstants.SELECT_TYPE);
				slObjectSelects.add(DomainConstants.SELECT_NAME);
								
				Map<?,?> objectInfo = dobCurrentObj.getInfo(context, slObjectSelects);
				jsonTemp = Json.createObjectBuilder();
				String strSelect = DomainConstants.EMPTY_STRING;
				for(int j=0; j<slObjectSelects.size(); j++) {
					strSelect = slObjectSelects.get(j);
					jsonTemp.add(strSelect, objectInfo.get(strSelect).toString());
				}				
				StringList hierarchyList = new StringList();				
				String strParentKey = (String)objectInfo.get(DomainConstants.SELECT_NAME);
				hierarchyList.add(strParentKey);
				jsonTemp.add(KEY_HIERARCHY,hierarchyList.toString());
				
				jsonReturnObj.add("parentInfo",jsonTemp);
				
				Pattern typePattern = new Pattern(strTypeName);

				if(KEY_ALL.equalsIgnoreCase(strRelName)) {
					strRelName = DomainConstants.QUERY_WILDCARD;
				}
				
				Pattern relPattern = new Pattern(strRelName);
				relPattern.addPattern(RELATIONSHIP_PARAMETER_AGGREGATION);
				
				MapList mlRelatedObjsTC = dobCurrentObj.getRelatedObjects(
						context,    														//the eMatrix Context object
						relPattern.getPattern(),											//Relationship pattern
						typePattern.getPattern(),											//Type pattern
						slObjectSelects,													//Object selects
						new StringList(DomainRelationship.SELECT_ID),						//Relationship selects
						true,																//get To relationships
						true,																//get From relationships
						(short)1,															//the number of levels to expand, 0 equals expand all.
						null,																//Object where clause
						null,																//Relationship where clause
						0);																	//Limit : The max number of Objects to get in the exapnd.0 to return all the data available

				slObjectSelects.add(DomainRelationship.SELECT_ID);
				Map<?, ?> mpObjMap = new HashMap<>();
				String strKey = DomainConstants.EMPTY_STRING;
				for(int i=0; i<mlRelatedObjsTC.size(); i++) {
					mpObjMap = (Map<?, ?>) mlRelatedObjsTC.get(i);
					jsonTemp = Json.createObjectBuilder();
					for(int j=0; j<slObjectSelects.size(); j++) {
						strKey = slObjectSelects.get(j);
						jsonTemp.add(strKey, checkNullValueforString((String) mpObjMap.get(strKey)));
					}
					hierarchyList.clear();
					hierarchyList.add(strParentKey);
					hierarchyList.add((String) mpObjMap.get(DomainConstants.SELECT_NAME));		
					
					jsonTemp.add(KEY_HIERARCHY,hierarchyList.toString());
					
					jsonArr.add(jsonTemp);
				}
				
				String[] hierarchyArray = new String[1];
				hierarchyArray[0] = (String)objectInfo.get(SELECT_ATTRIBUTE_TITLE);
				jsonTemp.add(KEY_HIERARCHY,hierarchyArray.toString());				
			}		
		
		jsonReturnObj.add(KEY_OUT_PUT, jsonArr.build());
		
		if(UIUtil.isNotNullAndNotEmpty(strWarning)) {
			jsonReturnObj.add(KEY_WARNING_MESSAGE, strWarning);
		}
		
		return jsonReturnObj.build().toString();
	}
	
	
	/**
	 * Check value is null or Empty and if multi-value character and replace it with comma
	 * @param strString : String for null check
	 * @return : String strString after null check
	 */
	 static String checkNullValueforString(  String strString )
	{
		return null != strString ? strString : DomainConstants.EMPTY_STRING;
	}
	
	/**
	 * Connects the objects to Test Case
	 * @param context : eMarix context
	 * @param strObjIdsToConnect : String Id of object to be connected with Test Case
	 * @param strTestCaseId : String OID of Test case
	 * @param strObjTypePattern : String type pattern
	 * @param strObjRelPattern : String relationship pattern
	 * @param strObjSelectables : String object selectables
	 * @return : String JSON of connected object info
	 * @throws Exception
	 */
	String connectToTestCase(Context context, String strObjIdsToConnect, String strTestCaseId, String strObjTypePattern, String strObjRelPattern, String strObjSelectables) throws Exception 
	{		
		 String strWarningMessage = null;
			if(UIUtil.isNotNullAndNotEmpty(strObjIdsToConnect) && UIUtil.isNotNullAndNotEmpty(strTestCaseId))
			{
				DomainObject dobTestCase =  DomainObject.newInstance(context, strTestCaseId);
				StringList slRelObjIds = StringUtil.split(strObjIdsToConnect, ",");
				
				Map<String, String> mpTypeRel = getTypeRelMapping(TYPE_TEST_CASE);
				Pattern typePattern = new Pattern(TYPE_REQUIREMENT);
				typePattern.addPattern(TYPE_TEST_METHOD_SPEC);
				typePattern.addPattern(TYPE_TEST_EXECUTION);
				typePattern.addPattern(TYPE_SIMULATION_TEMPLATE);
				typePattern.addPattern(TYPE_PARAMETER);
				
				Pattern relPattern = new Pattern(RELATIONSHIP_REQUIREMENT_VALIDATION);
				relPattern.addPattern(RELATIONSHIP_VPD_TEST_CASE_TEST_METHOD);
				relPattern.addPattern(RELATIONSHIP_TEST_EXECUTION_TEST_CASE);
				relPattern.addPattern(RELATIONSHIP_SIMULATION_OBJECT_REFERENCE);
				relPattern.addPattern(RELATIONSHIP_PARAMETER_USAGE);
				
				int iSize = slRelObjIds.size();
				MapList mlAlreadyConnectedObj = getConnectedObjects(context, typePattern, relPattern, slRelObjIds, dobTestCase, mpTypeRel);
				int iConnectedListSize = mlAlreadyConnectedObj.size();
				
				if(iConnectedListSize == iSize) {
					throw new Exception(EXCEPTION_MESSAGE_ON_CONNECT); 
				} else {
					connectRelatedObjects(context, dobTestCase, slRelObjIds);
					
					if(iConnectedListSize > 0) {
						strWarningMessage = getMessage(mlAlreadyConnectedObj, iConnectedListSize);
					}
				}
			}
			
		return getRelatedObjectsForTCorTE(context, strObjTypePattern, strObjRelPattern, strTestCaseId, strObjSelectables, strWarningMessage);	
	}
	
	/**
	 * Generic method to disconnect objects from Test case or Test Execution 
	 * @param context : eMarix context
     * @param strConnectionIds : String Ids of the objects to be disconnected
     * @param strObjectId : String Test case or Test Execution  Id
     * @param strObjTypePattern : String type pattern
     * @param strObjRelPattern : String relationship pattern
     * @param strObjSelectables : String object selectables
     * @return : String JSON info of selectable for related objects
     * @throws Exception
     */
	 String disconnectFromObject(Context context, String strConnectionIds, String strObjectId, String strObjTypePattern, String strObjRelPattern, String strObjSelectables) throws Exception 
	{	
		try {
			StringList slRelIDsList = StringUtil.split(strConnectionIds, ",");
			if(BusinessUtil.isNotNullOrEmpty(slRelIDsList)) {
				DomainRelationship.disconnect(context, BusinessUtil.toStringArray(slRelIDsList));	
			}
			
		} catch (Exception e) {
			throw e;
		}
						
		return getRelatedObjectsForTCorTE(context, strObjTypePattern, strObjRelPattern, strObjectId, strObjSelectables,DomainConstants.EMPTY_STRING);
	}

	/**
	 * Connects the objects to Test Execution
	 * @param context : eMarix context
	 * @param strObjIdsToConnect : String Ids of objects to be connected with Test Execution
	 * @param strTextExecutionId : String OID of Test Execution
	 * @param strObjTypePattern : String type pattern
	 * @param strObjRelPattern : String relationship pattern
	 * @param strObjSelectables : String object selectables
	 * @return : String JSON of connected object info
	 * @throws Exception
	 */
	 String connectToTestExecution(Context context, String strObjIdsToConnect, String strTextExecutionId, String strObjTypePattern, String strObjRelPattern, String strObjSelectables) throws Exception {
				
			Map<String, String> mpTypeRel = getTypeRelMapping(TYPE_TEST_EXECUTION);
			
			StringList slRelObjIds = StringUtil.split(strObjIdsToConnect, ",");
			StringList slOIDTypeList = new StringList();
			String strObjectId = DomainConstants.EMPTY_STRING;
			String strObjectIdType = DomainConstants.EMPTY_STRING;
			String strType = DomainConstants.EMPTY_STRING; 
			String strRelname = DomainConstants.EMPTY_STRING; 
			String strWarningMessage = null;
			
			if(UIUtil.isNotNullAndNotEmpty(strTextExecutionId)) {
				DomainObject dobTestExecution = DomainObject.newInstance(context, strTextExecutionId);

				Pattern typePattern = new Pattern(TYPE_SIMULATION);
				typePattern.addPattern(TYPE_TEST_CASE);
				typePattern.addPattern(TYPE_PARAMETER);
				
				Pattern relPattern = new Pattern(RELATIONSHIP_REFERENCED_SIMULATIONS);
				relPattern.addPattern(RELATIONSHIP_TEST_EXECUTION_TEST_CASE);
				relPattern.addPattern(RELATIONSHIP_PARAMETER_USAGE);
				
				int iSize = slRelObjIds.size();
				MapList mlAlreadyConnectedObj = getConnectedObjects(context, typePattern, relPattern, slRelObjIds, dobTestExecution, mpTypeRel);
				int iConnectedListSize = mlAlreadyConnectedObj.size();
				
				if(iConnectedListSize == iSize) {
					throw new Exception(EXCEPTION_MESSAGE_ON_CONNECT); 
				} else {
					DomainObject dobRelatedObj = null;
					for(int i=0; i<iSize; i++) {
						strObjectIdType = slRelObjIds.get(i);
						slOIDTypeList = StringUtil.split(strObjectIdType, "|");
						strObjectId = slOIDTypeList.get(0);
						strType = slOIDTypeList.get(1);
						if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strType)){
							dobRelatedObj = DomainObject.newInstance(context, strObjectId);
							strRelname = mpTypeRel.get(strType);
							
							DomainRelationship.connect(context, dobTestExecution, strRelname, dobRelatedObj);
						}
					}
					
					if(iConnectedListSize > 0) {
						strWarningMessage = getMessage(mlAlreadyConnectedObj, iConnectedListSize);
					}
				}			
			}
			
			return getRelatedObjectsForTCorTE(context, strObjTypePattern, strObjRelPattern, strTextExecutionId, strObjSelectables, strWarningMessage);
	}
	
	/**
	 * Method to construct message by using already connected objects MapList
	 * @param mlAlreadyConnectedObj : MapList already connected objects MapList
	 * @param iListSize : int size of MapList mlAlreadyConnectedObj
	 * @return : String message constructed with the help of MapList mlAlreadyConnectedObj
	 */
	private String getMessage(MapList mlAlreadyConnectedObj, int iListSize) {
		
		StringBuilder sbMsgBuilder = new StringBuilder();
		sbMsgBuilder.append(WARNING_MESSAGE);
		
		Map<?, ?> mpObjMap = new HashMap<>();
		for(int i=0;i<iListSize;i++) {
			mpObjMap = (Map<?, ?>) mlAlreadyConnectedObj.get(i);
			String strType = (String) mpObjMap.get(DomainConstants.SELECT_TYPE);
			String strName = (String) mpObjMap.get(DomainConstants.SELECT_NAME);
			String strRevision = (String) mpObjMap.get(DomainConstants.SELECT_REVISION);
			sbMsgBuilder.append("'").append(strType).append("'").append("  ").append("'").append(strName).append("'").append("  ").append("'").append(strRevision).append("'").append(",");
		}
		
		String strMessage = sbMsgBuilder.toString();
		strMessage = strMessage.substring(0, strMessage.length() - 1);
		
		return strMessage;
	}

	/**
	 * Method to get the already connected objects to avoid duplicate connections
	 * @param context : eMatrix context object
	 * @param typePattern : Pattern Type names pattern
	 * @param relPattern : Pattern Relationship names pattern
	 * @param slObjsList : StringList with details for related objects 
	 * @param dobCurrentObj : DomainObject current object
	 * @param mpTypeRelMapping : Map with type and relationship mapping
	 * @return : MapList with details for already connected objects
	 * @throws Exception
	 */
	MapList getConnectedObjects(Context context, Pattern typePattern, Pattern relPattern, StringList slRelObjIds, DomainObject dobCurrentObj, Map<String, String> mpTypeRelMapping) throws Exception {
		 	
		MapList mlConnectedObjectList = new MapList();
		try {
			StringList slObjectSelects = new StringList(4);
			slObjectSelects.add(SELECT_PHYSICAL_ID);
			slObjectSelects.add(DomainConstants.SELECT_TYPE);
			slObjectSelects.add(DomainConstants.SELECT_NAME);
			slObjectSelects.add(DomainConstants.SELECT_REVISION);
			
			MapList mlRelatedObjects = dobCurrentObj.getRelatedObjects(
					context,    														//the eMatrix Context object
					relPattern.getPattern(),											//Relationship pattern
					typePattern.getPattern(),											//Type pattern
					slObjectSelects,													//Object selects
					new StringList(DomainRelationship.SELECT_NAME),						//Relationship selects
					true,																//get To relationships
					true,																//get From relationships
					(short)1,															//the number of levels to expand, 0 equals expand all.
					null,																//Object where clause
					null,																//Relationship where clause
					0);																	//Limit : The max number of Objects to get in the exapnd.0 to return all the data available
			
			int iSize = mlRelatedObjects.size();
			if(iSize > 0) {				
				Map<?, ?> mpObjMap = new HashMap<>();
				for(int j=0; j<iSize; j++) {
					mpObjMap = (Map<?, ?>) mlRelatedObjects.get(j);
					String strType = (String) mpObjMap.get(DomainConstants.SELECT_TYPE);
					if(mpTypeRelMapping.containsKey(strType)) {
						String strMappedRelName = mpTypeRelMapping.get(strType);
						String strPID = (String) mpObjMap.get(SELECT_PHYSICAL_ID);
						String strRelName = (String) mpObjMap.get(DomainRelationship.SELECT_NAME);
						if(strRelName.equals(strMappedRelName) && slRelObjIds.contains(strPID+"|"+strType) && !mlConnectedObjectList.contains(mpObjMap)) {
							mlConnectedObjectList.add(mpObjMap);
							slRelObjIds.remove(strPID+"|"+strType);
						} 
					}
				}
			}
				
		}  catch (Exception e) {
			throw e;
		}
		
		return mlConnectedObjectList;
	 }
}