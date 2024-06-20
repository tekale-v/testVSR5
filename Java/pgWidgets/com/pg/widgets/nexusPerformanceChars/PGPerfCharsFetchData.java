package com.pg.widgets.nexusPerformanceChars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.SelectConstants;
import matrix.util.StringList;

public class PGPerfCharsFetchData {

	PGPerfCharsFetchDataColumns objPerfCharsFetchDataColumns = new PGPerfCharsFetchDataColumns();
	private static final Logger logger = Logger.getLogger(PGPerfCharsFetchData.class.getName());
			
	/**
	 * Method to get the Performance Characteristics object details
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	public String fetchPerfCharsDataFromEnovia(Context context, Map<String,String> mpArgsMap) {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			String strObjectId = mpArgsMap.get(DomainConstants.SELECT_ID);
			String strObjSelects = PGPerfCharsConstants.OBJECT_SELECTS_FOR_PC_DATA;
			String strPerfCharFilter = PGPerfCharsConstants.FILTER_ALL;
			StringList slObjSelectList = StringUtil.split(strObjSelects, ",");

			Map<Object, Object> paramMap = new HashMap<>();
			paramMap.put(PGPerfCharsConstants.KEY_OBJECT_ID, strObjectId);
			paramMap.put(PGPerfCharsConstants.KEY_CHARS_DERIVED_FILTER, strPerfCharFilter);
			paramMap.put(PGPerfCharsConstants.KEY_OBJ_SELECTS, slObjSelectList);
			paramMap.put(PGPerfCharsConstants.KEY_MODE, "");
			paramMap.put(PGPerfCharsConstants.KEY_ADD_ROW, "");
			paramMap.put(PGPerfCharsConstants.KEY_SWITCH_MODE, "");
			paramMap.put(PGPerfCharsConstants.KEY_SELECTED_TABLE, PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE);

			MapList mlPerfCharList = getPerformanceChar(context, paramMap);
			MapList mlPerfCharFinalList = updateFinalListWithColumnProgramData(context, mlPerfCharList, strObjectId, strPerfCharFilter);

			JsonArrayBuilder jsonArrObjInfo = getJsonDataForPerfChars(context, mlPerfCharFinalList);
			jsonReturnObj.add(PGPerfCharsConstants.KEY_DATA, jsonArrObjInfo);

			updatedAttributesRelatedToParentPart(context, strObjectId, jsonReturnObj);
			
			//System.out.println("-------perfChars---json--postman--" + jsonReturnObj.build().toString());

			return jsonReturnObj.build().toString();

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_FETCH_DATA, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonReturnObj.build().toString();
		}
	}

	/**
	 * Method to get the Performance Characteristics object details
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	public String fetchPerfCharsData(Context context, String strJsonInput) {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strObjectId = jsonInputData.getString(DomainConstants.SELECT_ID);
			String strObjSelects = jsonInputData.getString(PGPerfCharsConstants.KEY_OBJ_SELECTS);
			String strPerfCharFilter = jsonInputData.getString(PGPerfCharsConstants.KEY_CHARS_DERIVED_FILTER);
			String strSelectedTable = jsonInputData.getString(PGPerfCharsConstants.KEY_SELECTED_TABLE);
			StringList slObjSelectList = StringUtil.split(strObjSelects, ",");

			Map<Object, Object> paramMap = new HashMap<>();
			paramMap.put(PGPerfCharsConstants.KEY_OBJECT_ID, strObjectId);
			paramMap.put(PGPerfCharsConstants.KEY_CHARS_DERIVED_FILTER, strPerfCharFilter);
			paramMap.put(PGPerfCharsConstants.KEY_OBJ_SELECTS, slObjSelectList);
			paramMap.put(PGPerfCharsConstants.KEY_MODE, "");
			paramMap.put(PGPerfCharsConstants.KEY_ADD_ROW, "");
			paramMap.put(PGPerfCharsConstants.KEY_SWITCH_MODE, "");
			paramMap.put(PGPerfCharsConstants.KEY_SELECTED_TABLE, strSelectedTable);

			MapList mlPerfCharList = getPerformanceCharAllData(context, paramMap);

			MapList mlPerfCharFinalList = updateFinalListWithColumnProgramData(context, mlPerfCharList, strObjectId, strPerfCharFilter);

			JsonArrayBuilder jsonArrObjInfo = getJsonDataForPerfChars(context, mlPerfCharFinalList);
			jsonReturnObj.add(PGPerfCharsConstants.KEY_DATA, jsonArrObjInfo);

			updatedAttributesRelatedToParentPart(context, strObjectId, jsonReturnObj);

			//System.out.println("-------jsonReturnObj-------" + jsonReturnObj.build().toString());

			return jsonReturnObj.build().toString();

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_FETCH_DATA, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonReturnObj.build().toString();
		}
	}

	/**
	 * Method to updated wizard related attributes of Parent PDP part on final Json.
	 * 
	 * @param context
	 * @param strObjectId
	 * @param jsonReturnObj
	 * @throws FrameworkException
	 */
	private void updatedAttributesRelatedToParentPart(Context context, String strObjectId,
			JsonObjectBuilder jsonReturnObj) throws FrameworkException {
		DomainObject dobPDPObj = DomainObject.newInstance(context, strObjectId);
		StringList slAttrSelects = new StringList(2);
		slAttrSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED);
		slAttrSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
		Map<?,?> mpPDPInfoMap = dobPDPObj.getInfo(context, slAttrSelects);
		
		String strReleaseCriteriaRequired = (String) mpPDPInfoMap.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED);
		String strNexusPerfCharRequired = (String) mpPDPInfoMap.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
		jsonReturnObj.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED, strReleaseCriteriaRequired);
		jsonReturnObj.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED, strNexusPerfCharRequired);
		
	}

	/**
	 * Method to get Json object from MapList
	 * 
	 * @param mlPerfCharList
	 * @return
	 * @throws FrameworkException 
	 */
	private JsonArrayBuilder getJsonDataForPerfChars(Context context, MapList mlPerfCharList) throws FrameworkException {
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		if (mlPerfCharList != null && !mlPerfCharList.isEmpty()) {
			Iterator<Map<?, ?>> itrPerfChar = mlPerfCharList.iterator();
			while (itrPerfChar.hasNext()) {
				Map<?, ?> mpPerfCharInfoMap = itrPerfChar.next();
				JsonObjectBuilder jsonObjInfo = getJsonFromMap(context, mpPerfCharInfoMap);
				jsonArrObjInfo.add(jsonObjInfo);
			}
		}
		return jsonArrObjInfo;
	}

	/**
	 * Get Json object from Map
	 * 
	 * @param mpPerfCharInfoMap
	 * @return
	 * @throws FrameworkException 
	 */
	private JsonObjectBuilder getJsonFromMap(Context context, Map<?, ?> mpPerfCharInfoMap) throws FrameworkException {
		JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
		for (Map.Entry<?, ?> entry : mpPerfCharInfoMap.entrySet()) {
			String strObjSelect = (String) entry.getKey();
			if(strObjSelect.startsWith(PGPerfCharsConstants.PREFIX_COL_PROG)) {
				MapList mlObjValuesList = (MapList) entry.getValue();
				JsonArrayBuilder jsonArrObjInfo = getJsonArrayForHyperLinkColumns(mlObjValuesList);
				jsonObjInfo.add(strObjSelect, jsonArrObjInfo);
			} else {
				Object objValue = entry.getValue();
				String strValue = getStringValueForJson(objValue);
				jsonObjInfo.add(strObjSelect, strValue);
			}
			
			if(DomainConstants.SELECT_ID.equals(strObjSelect)) {
				String strPerfCharId = (String) entry.getValue();
				updateNexusParamAttributes(context, strPerfCharId, jsonObjInfo);
			}
		}
		return jsonObjInfo;
	}

	/**
	 * Method to update the Nexus Param attributes
	 * 
	 * @param context
	 * @param strPerfCharId
	 * @param jsonObjInfo
	 * @throws FrameworkException 
	 */
	private void updateNexusParamAttributes(Context context, String strPerfCharId, JsonObjectBuilder jsonObjInfo) throws FrameworkException {
		DomainObject dobPCObj = DomainObject.newInstance(context, strPerfCharId);
		String strNexusParamId = dobPCObj.getAttributeValue(context, PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID);
		
		if(UIUtil.isNotNullAndNotEmpty(strNexusParamId)) {
			StringList slAttributeList = new StringList();
			slAttributeList.add(PGPerfCharsConstants.SELECT_ATTR_PG_NEXUS_PARAMETER_LISTID);
			slAttributeList.add(PGPerfCharsConstants.SELECT_PG_NEXUS_PARAM_ID);
			slAttributeList.add(PGPerfCharsConstants.SELECT_PG_NEXUS_PARAM_TYPE);
			slAttributeList.add(PGPerfCharsConstants.SELECT_PG_NEXUS_PARAM_LIST_VAR_ID);
			slAttributeList.add(PGPerfCharsConstants.SELECT_PG_NEXUS_PARAM_LIST_VER_ID);
			slAttributeList.add(PGPerfCharsConstants.SELECT_PG_NEXUS_PARAM_REF_TYPE_ID);
			slAttributeList.add(PGPerfCharsConstants.SELECT_PG_NEXUS_TRANS_RULE);
			
			DomainObject dobNexusParamObj = DomainObject.newInstance(context, strNexusParamId);
			
			Map<?,?> mpNexusParamInfoMap = dobNexusParamObj.getInfo(context, slAttributeList);

			for(String strAttrSelect : slAttributeList) {
				String strAttrValue = (String) mpNexusParamInfoMap.get(strAttrSelect);
				if(strAttrValue == null) {
					strAttrValue = "";
				}
				jsonObjInfo.add(strAttrSelect, strAttrValue);
			}
			
			StringList slMultiValUOMAttValList = dobNexusParamObj.getInfoList(context,
					PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_VALID_CONVERTIBLE_UNITS);
			int iListSize = slMultiValUOMAttValList.size();
			StringBuilder sbAttrVal = new StringBuilder();
			int iCount = 1;
			for (int i = 0; i < iListSize; i++) {
				String strAttrVal = slMultiValUOMAttValList.get(i);
				sbAttrVal.append(strAttrVal);
				if (iCount < iListSize) {
					sbAttrVal.append(" | ");
				}
				iCount++;
			}
			jsonObjInfo.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_VALID_CONVERTIBLE_UNITS, sbAttrVal.toString());
			
		}
	}

	/**
	 * Method to convert Object to String
	 * @param objValue
	 * @return
	 */
	private String getStringValueForJson(Object objValue) {
		String strValue = "";
		if (objValue == null) {
			return "";
		} else {
			StringList slObjValuesList = null;
			if (objValue instanceof StringList) {
				slObjValuesList = (StringList) objValue;
			} else if (objValue instanceof String) {
				String strObjValue = (String) objValue;
				slObjValuesList = StringUtil.split(strObjValue, SelectConstants.cSelectDelimiter);
			}

			if (slObjValuesList == null) {
				return "";
			} else {
				strValue = getStringFromStringList(slObjValuesList);
			}

		}
		return strValue;
	}

	/**
	 * Method to convert MapList to Json Array
	 * @param mlObjValuesList
	 * @return
	 */
	private JsonArrayBuilder getJsonArrayForHyperLinkColumns(MapList mlObjValuesList) {
		JsonArrayBuilder jsonArrHyperLinkData = Json.createArrayBuilder();
		if(mlObjValuesList != null && !mlObjValuesList.isEmpty()) {
			Iterator<Map<String, String>> itrObjValues = mlObjValuesList.iterator();
			while (itrObjValues.hasNext()) {
				Map<String, String> mpHyperColumnInfoMap = itrObjValues.next();
				JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
				for (Map.Entry<String, String> entry : mpHyperColumnInfoMap.entrySet()) {
					String strKey = entry.getKey();
					String strValue = entry.getValue();
					if(UIUtil.isNotNullAndNotEmpty(strKey) && strValue != null) {
						jsonObjInfo.add(strKey, strValue);
					}
				}
				jsonArrHyperLinkData.add(jsonObjInfo);
			}
		}
		return jsonArrHyperLinkData;
	}

	/**
	 * Get string from SL
	 * 
	 * @param slObjValuesList
	 * @return
	 */
	private String getStringFromStringList(StringList slObjValuesList) {
		StringBuilder sbObjValue = new StringBuilder();
		for (String strObjValue : slObjValuesList) {
			sbObjValue.append(strObjValue).append(",");
		}

		String strObjReturnValue = sbObjValue.toString();
		if (UIUtil.isNotNullAndNotEmpty(strObjReturnValue) && strObjReturnValue.endsWith(",")) {
			strObjReturnValue = strObjReturnValue.substring(0, strObjReturnValue.length() - 1);
		}

		return strObjReturnValue;
	}
	
	/**
	 * Method to get data for Column Programs
	 * @param context
	 * @param mlPerfCharList
	 * @param strObjectId
	 * @param strPerfCharFilter
	 * @return
	 * @throws Exception
	 */
	private MapList updateFinalListWithColumnProgramData(Context context, MapList mlPerfCharList, String strObjectId,
			String strPerfCharFilter) throws Exception {
		MapList mlPerfCharFinalList = new MapList();
		StringList slObjSelects = new StringList();
		slObjSelects.add(PGPerfCharsConstants.SELECT_ATTR_REFERENCE_TYPE);
		slObjSelects.add(PGPerfCharsConstants.SELECT_ATTR_PGORIGINATINGSOURCE);

		DomainObject dobParentObj = DomainObject.newInstance(context, strObjectId);
		Map<?, ?> mpParentObjInfoMap = dobParentObj.getInfo(context, slObjSelects);

		Map<String, String> mpParamList = new HashMap<>();
		mpParamList.put(PGPerfCharsConstants.KEY_SELECTED_TABLE, "pgVPDPerformanceCharacteristicTable");
		mpParamList.put("parentOID", strObjectId);
		mpParamList.put("objectId", strObjectId);
		mpParamList.put("IsStructureCompare", "FALSE");
		mpParamList.put("pgVPDCPNCharacteristicDerivedFilter", strPerfCharFilter);
		
		Iterator<Map<?, ?>> itrPerfChar = mlPerfCharList.iterator();
		while (itrPerfChar.hasNext()) {
			Map<Object, Object> mpPerfCharInfoMap = (Map<Object, Object>) itrPerfChar.next();
			updatePerfCharColumnData(context, mpParamList, mpPerfCharInfoMap, mpParentObjInfoMap);
			mlPerfCharFinalList.add(mpPerfCharInfoMap);
		}
		return mlPerfCharFinalList;
	}

	/**
	 * Method to get Path columns details
	 * @param context
	 * @param mpParamList
	 * @param mpPerfCharInfoMap
	 * @param mpParentObjInfoMap
	 * @throws Exception
	 */
	private void updatePerfCharColumnData(Context context, Map<String, String> mpParamList,
			Map<Object, Object> mpPerfCharInfoMap, Map<?,?> mpParentObjInfoMap) throws Exception {
		String strPerfCharId = (String) mpPerfCharInfoMap.get("id");
		String strRelName = (String) mpPerfCharInfoMap.get("relationship");
		String strDerivedPath = (String) mpPerfCharInfoMap.get("derivedPath");
		String strpgPFInheritanceType = (String) mpPerfCharInfoMap.get(PGPerfCharsConstants.SELECT_ATTR_PG_INHERITANCE_TYPE);
		if(UIUtil.isNullOrEmpty(strDerivedPath)) {
			strDerivedPath = "";
		}
		
		Map<Object,Object> programMap = new HashMap<>();
		programMap.put("paramList", mpParamList);
		
		MapList mlObjectList = new MapList();
		Map<Object,Object> objListMap = new HashMap<>();
		objListMap.put("id", strPerfCharId);
		objListMap.put("relationship", strRelName);
		objListMap.put("derivedPath", strDerivedPath);
		objListMap.put(PGPerfCharsConstants.SELECT_ATTR_PG_INHERITANCE_TYPE, strpgPFInheritanceType);
		
		String strReadAccess = "FALSE";
		DomainObject dobPerfChar = DomainObject.newInstance(context, strPerfCharId);
		boolean bUserHasReadAccess = FrameworkUtil.hasAccess(context, dobPerfChar , "read");
		if(bUserHasReadAccess) {
			strReadAccess = "TRUE";
		}

		objListMap.put("objReadAccess", strReadAccess);
		mlObjectList.add(objListMap);
		
		programMap.put("objectList", mlObjectList);
		
		//////Path///////////////////////////////////////////////////////////////////
		String strPath = "";
		StringList slPathList = null;
		String strAttrRefTypeValue = (String) mpParentObjInfoMap.get(PGPerfCharsConstants.SELECT_ATTR_REFERENCE_TYPE);
		if("M".equals(strAttrRefTypeValue)) {
			slPathList = objPerfCharsFetchDataColumns.getMasterPartDetails(context, programMap);
			if(slPathList != null && !slPathList.isEmpty()) {
				strPath = slPathList.get(0);
			}
			mpPerfCharInfoMap.put("MasterPart", strPath);
		} else {
			MapList mlPathList = objPerfCharsFetchDataColumns.getDerivedPathForRow(context, programMap);
			mpPerfCharInfoMap.put("ColProg_Path", mlPathList);
		}
		
		//////TestMethod///////////////////////////////////////////////////////////////////
		MapList mlTestMethodNameList = null;
		String strOriginatingSource = (String) mpParentObjInfoMap.get(PGPerfCharsConstants.SELECT_ATTR_PGORIGINATINGSOURCE);
		if(PGPerfCharsConstants.ORIGINATING_SOURCE_DSO.equals(strOriginatingSource)) {
			mlTestMethodNameList = objPerfCharsFetchDataColumns.pgGetTestMethods(context, programMap);
		} else {
			mlTestMethodNameList = objPerfCharsFetchDataColumns.pgGetTestMethodsNonDSO(context, programMap);
		}
		mpPerfCharInfoMap.put("ColProg_TestMethodName", mlTestMethodNameList);
		
		String strIsNexusTestMethod = "false";
		if(mlTestMethodNameList != null && !mlTestMethodNameList.isEmpty()) {
			Map<?,?> mpTMInfoMap = (Map<?, ?>) mlTestMethodNameList.get(0);
			String strTMId = (String) mpTMInfoMap.get(DomainConstants.SELECT_ID);
			if(UIUtil.isNotNullAndNotEmpty(strTMId)) {
				DomainObject dobTMObj = DomainObject.newInstance(context, strTMId);
				String strAttrAuthingApp = dobTMObj.getAttributeValue(context, PGPerfCharsConstants.ATTR_PG_AUTHORING_APP);
				if("Nexus".equalsIgnoreCase(strAttrAuthingApp)) {
					strIsNexusTestMethod = "true";
				}
			}
		}
		mpPerfCharInfoMap.put("IsNexus", strIsNexusTestMethod);
		
		//////TestMethodReferenceDocumentName///////////////////////////////////////////////////////////////////
		MapList mlTestMethodRefDocsNameList = objPerfCharsFetchDataColumns.pgGetReferenceDocGCAS(context, programMap);
		mpPerfCharInfoMap.put("ColProg_TestMethodRefDoctName", mlTestMethodRefDocsNameList);
		
		
		//////DerivedTitle///////////////////////////////////////////////////////////////////
		String strDerivedTitle = "";
		StringList slDerivedTitleList = objPerfCharsFetchDataColumns.getDerivedTitleForRow(context, programMap);

		if(slDerivedTitleList != null && !slDerivedTitleList.isEmpty()) {
			strDerivedTitle = slDerivedTitleList.get(0);
		}
		mpPerfCharInfoMap.put("DerivedTitle", strDerivedTitle);
		
	}

	/**
	 * This method is copied from table program (JPO)
	 * emxCPNCharacteristicList:getPerformanceChar and changed accordingly.
	 * 
	 * Method to get the list of all characteristics. 
	 * It returns key 'Perf_Char_Type' which has the information to indicate whether the PC is 'Local' or 'Referenced'
	 * The list is decided based on the relationship
	 * attribute pgInhertanceType This method is used to display the characteristics
	 * on the Tables Tab.
	 * 
	 * @param context
	 * @param args
	 * @return MapList with Product Data and it's EBOM and their Master
	 *         characteristics
	 * @throws Exception Since DSM 2015x FD04
	 */
	public MapList getPerformanceCharAllData(Context context, Map<?, ?> paramMap) throws Exception {
		MapList objList = new MapList();
		boolean isContextPushed = false;
		try {
			String strCharInheritanceType = "";
			String partType = null;
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_POLICY);
			objectSelects.add(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
			objectSelects.add(PGPerfCharsConstants.ATTR_REFERENCE_TYPE);

			StringList slArgsObjSelects = (StringList) paramMap.get(PGPerfCharsConstants.KEY_OBJ_SELECTS);
			objectSelects.addAll(slArgsObjSelects);

			StringList relSelects = new StringList();
			relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			relSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
			relSelects.add("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
			relSelects.add("attribute[" + PGPerfCharsConstants.ATTRIBUTE_PG_INHERITED_FROM_PLATFORM + "]");

			String objectId = (String) paramMap.get(PGPerfCharsConstants.KEY_OBJECT_ID);

			if (UIUtil.isNotNullAndNotEmpty(objectId)) {
				DomainObject doObj = DomainObject.newInstance(context, objectId);

				String strSelectedTable = (String) paramMap.get(PGPerfCharsConstants.KEY_SELECTED_TABLE);
				// CPN 110110 zbp-->added for..create new table view error
				// ---------->START
				strSelectedTable = strSelectedTable.substring(strSelectedTable.lastIndexOf('~') + 1,
						strSelectedTable.length());
				// CPN 110110 zbp-->added for..create new table view error
				// ---------->END
				String strTypeSym = FrameworkProperties.getProperty("emxCPN.Characteristic.table." + strSelectedTable);

				String type = PropertyUtil.getSchemaProperty(context, strTypeSym);
				
				// DSM 2015x.1 - Push and pop context is performed since logged in user may not have access to some data
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, PGPerfCharsConstants.PERSON_USER_AGENT),
						DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isContextPushed = true;
				
				// DSM(DS) 2015x.1 added to satisfy the Requirement 4953 and ALM 2899 End
				StringList selStmt = new StringList();
				selStmt.add("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
				selStmt.add("to[Classified Item].frommid[" + PGPerfCharsConstants.REL_PARTFAMILYREFERENCE
						+ "].torel.to.id");
				selStmt.add(DomainConstants.SELECT_CURRENT);

				Map partInfoMap = doObj.getInfo(context, selStmt);
				MapList conextObjectList = new MapList();
				String strContextRefType = (String) partInfoMap
						.get("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
				String strContextCurState = (String) partInfoMap.get(DomainConstants.SELECT_CURRENT);
				/*
				 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
				 * Relationship- START
				 */
				String strConnectedRefId = DomainConstants.EMPTY_STRING;
				if (partInfoMap.containsKey("to[Classified Item].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id")) {
					strConnectedRefId = (String) partInfoMap.get("to[Classified Item].frommid["
							+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id");
				}

				//DSM : 2022x.5 : Get related PCs for Local filter : Start
				MapList mlLocalPCList = new MapList();
				
				objList = doObj.getRelatedObjects(context, CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
						type, objectSelects, relSelects, false, true, (short) 1, null, null, 0);
				
				MapList charObjList = new MapList();	
				
				if ("R".equals(strContextRefType) && UIUtil.isNotNullAndNotEmpty(strConnectedRefId)) {
					doObj.setId(strConnectedRefId);
					
				} else if("M".equals(strContextRefType) || "U".equals(strContextRefType)) {
					
					Iterator objListItr = objList.iterator();
					while (objListItr.hasNext()) {
						Map ObjMap = (Map) objListItr.next();
						if (ObjMap != null && !ObjMap.isEmpty()) {
							strCharInheritanceType = (String) ObjMap
									.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
							
							if(UIUtil.isNotNullAndNotEmpty(strCharInheritanceType)
									&& strCharInheritanceType.equals(PGPerfCharsConstants.STR_REFERENCED)) {
								charObjList.add(ObjMap);
							}
						}
					}
					
				}

				if (!charObjList.isEmpty()
						&& !("R".equals(strContextRefType) && UIUtil.isNullOrEmpty(strConnectedRefId))) {
					objList.removeAll(charObjList);
				}
				
				int iLocalListSize = objList.size();
				for(int i=0; i<iLocalListSize; i++) {
					Map<Object,Object> mpLocalPCInfoMap = (Map<Object, Object>) objList.get(i);
					mpLocalPCInfoMap.put(PGPerfCharsConstants.KEY_PC_TYPE, PGPerfCharsConstants.FILTER_LOCAL);
					mlLocalPCList.add(mpLocalPCInfoMap);
				}
				//DSM : 2022x.5 : Get related PCs for Local filter : End
				
				String masterCurState = doObj.getInfo(context, DomainConstants.SELECT_CURRENT);
				
				//DSM : 2022x.5 : Get related PCs for Referenced filter : Start
				MapList mlReferencedPCList = new MapList();
				objList = new MapList();
				DomainObject dobCurrentPartObj = DomainObject.newInstance(context, objectId);
				partType = dobCurrentPartObj.getInfo(context, DomainConstants.SELECT_TYPE);
				if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(partType)) {
					// get id from EBOM relationship and their Master
					objList = getEbomPartsCharacterstics(context, paramMap, dobCurrentPartObj, objList, "",
							objectSelects);
					objList = updatePerfCharsListForFPPWithSorting(context, objList);

				} else {
					
					if ("R".equals(strContextRefType) && UIUtil.isNotNullAndNotEmpty(strConnectedRefId)
							&& !(PGPerfCharsConstants.STATE_RELEASE.equals(masterCurState)
									|| PGPerfCharsConstants.STATE_COMPLETE.equals(masterCurState))
							&& !(PGPerfCharsConstants.STATE_OBSOLETE.equals(masterCurState)
									&& PGPerfCharsConstants.STATE_OBSOLETE.equals(strContextCurState))) {
						objList = new MapList();
						
					} else {
						String strReferenceType = (String) doObj.getInfo(context,
								"attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");

						if (UIUtil.isNotNullAndNotEmpty(strReferenceType)) {
							MapList charList = doObj.getRelatedObjects(context,
										CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, type, objectSelects, relSelects, false,
										true, (short) 1, null, null, 0);

							Iterator charListItrtr = charList.iterator();
							while (charListItrtr.hasNext()) {
									Map charMap = (Map) charListItrtr.next();
									if (charMap != null && !charMap.isEmpty()) {
										strCharInheritanceType = (String) charMap.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
										if ("Referenced".equals(strCharInheritanceType) ||
											("R".equals(strContextRefType) && (PGPerfCharsConstants.STATE_RELEASE.equals(masterCurState)
											|| PGPerfCharsConstants.STATE_COMPLETE.equals(masterCurState)))) {
													objList.add(charMap);

										}
									}
								}
							}
					}
					
				}
				
				iLocalListSize = objList.size();
				for(int i=0; i<iLocalListSize; i++) {
					Map<Object,Object> mpRefPCInfoMap = (Map<Object, Object>) objList.get(i);
					mpRefPCInfoMap.put(PGPerfCharsConstants.KEY_PC_TYPE, PGPerfCharsConstants.FILTER_REFERENCED);
					mlReferencedPCList.add(mpRefPCInfoMap);
				}
				//DSM : 2022x.5 : Get related PCs for Referenced filter : End

				objList = new MapList();
				objList.addAll(mlLocalPCList);
				objList.addAll(mlReferencedPCList);
				
				objList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending",
						"integer");
				// DSO 2013x.4: Code to display Sequence Number as same logic as that for
				// Performance Specification view - START
				if (objList != null && objList.size() > 0) {
					objList = pgGetCharacteristicList(context, objList);
				}
				// DSO 2013x.4 : Code to display Sequence Number as same logic as that for
				// Performance Specification view - END

				MapList mlTempList = new MapList();
				for (int iSeq = 0; iSeq < objList.size(); iSeq++) {
					Map mpTempList = (Map) objList.get(iSeq);
					mpTempList.put("Sequence", Integer.toString(iSeq + 1));
					mlTempList.add(mpTempList);
				}
				objList.clear();
				objList.addAll(mlTempList);
				
				// DSO 2013x.4- Code Added to Display Product Data and it's EBOM and their
				// Master characteristics - ENDS
			}
		} finally {
			// DSM 2015x.1 - Push and pop context is performed since logged in user may not have access to some data
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return objList;

	}
	
	/**
	 * This method is copied from table program (JPO)
	 * emxCPNCharacteristicList:getPerformanceChar
	 * 
	 * Method to get the list of characteristics based on the filter
	 * (ALL,Referenced,Local) . The list is decided based on the relationship
	 * attribute pgInhertanceType This method is used to display the characteristics
	 * on the Tables Tab.
	 * 
	 * @param context
	 * @param args
	 * @return MapList with Product Data and it's EBOM and their Master
	 *         characteristics
	 * @throws Exception Since DSM 2015x FD04
	 */
	public MapList getPerformanceChar(Context context, Map<?, ?> paramMap) throws Exception {
		MapList objList = new MapList();
		boolean isContextPushed = false;
		try {
			String strCharInheritanceType = "";
			String partType = null;
			new MapList();
			new MapList();
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_POLICY);
			objectSelects.add(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
			objectSelects.add(PGPerfCharsConstants.ATTR_REFERENCE_TYPE);

			StringList slArgsObjSelects = (StringList) paramMap.get(PGPerfCharsConstants.KEY_OBJ_SELECTS);
			objectSelects.addAll(slArgsObjSelects);

			StringList relSelects = new StringList();
			relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			relSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
			relSelects.add("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
			relSelects.add("attribute[" + PGPerfCharsConstants.ATTRIBUTE_PG_INHERITED_FROM_PLATFORM + "]");

			String objectId = (String) paramMap.get(PGPerfCharsConstants.KEY_OBJECT_ID);

			String addNewRow = (String) paramMap.get(PGPerfCharsConstants.KEY_ADD_ROW);
			String strSwitchMode = (String) paramMap.get(PGPerfCharsConstants.KEY_SWITCH_MODE);
			if (UIUtil.isNotNullAndNotEmpty(objectId)) {
				DomainObject doObj = DomainObject.newInstance(context, objectId);

				String strSelectedTable = (String) paramMap.get(PGPerfCharsConstants.KEY_SELECTED_TABLE);
				// CPN 110110 zbp-->added for..create new table view error
				// ---------->START
				strSelectedTable = strSelectedTable.substring(strSelectedTable.lastIndexOf('~') + 1,
						strSelectedTable.length());
				// CPN 110110 zbp-->added for..create new table view error
				// ---------->END
				String strTypeSym = FrameworkProperties.getProperty("emxCPN.Characteristic.table." + strSelectedTable);

				String type = PropertyUtil.getSchemaProperty(context, strTypeSym);

				// DSM 2015x.1 - Push and pop context is performed since logged in user may not have access to some data
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, PGPerfCharsConstants.PERSON_USER_AGENT),
						DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isContextPushed = true;
				
				// DSM(DS) 2015x.1 added to satisfy the Requirement 4953 and ALM 2899 End
				StringList selStmt = new StringList();
				selStmt.add("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
				selStmt.add("to[Classified Item].frommid[" + PGPerfCharsConstants.REL_PARTFAMILYREFERENCE
						+ "].torel.to.id");
				selStmt.add(DomainConstants.SELECT_CURRENT);

				Map partInfoMap = doObj.getInfo(context, selStmt);
				MapList conextObjectList = new MapList();
				String strContextRefType = (String) partInfoMap
						.get("attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
				String strContextCurState = (String) partInfoMap.get(DomainConstants.SELECT_CURRENT);
				/*
				 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
				 * Relationship- START
				 */
				String strConnectedRefId = DomainConstants.EMPTY_STRING;
				if (partInfoMap.containsKey("to[Classified Item].frommid["
						+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id")) {
					strConnectedRefId = (String) partInfoMap.get("to[Classified Item].frommid["
							+ PGPerfCharsConstants.REL_PARTFAMILYREFERENCE + "].torel.to.id");
				}

				final String derivedFilterSelection = (String) paramMap.get(PGPerfCharsConstants.KEY_CHARS_DERIVED_FILTER);
				String rangeAll = PGPerfCharsConstants.FILTER_ALL;
				String rangeLocal = PGPerfCharsConstants.FILTER_LOCAL;
				String rangeReferenced = PGPerfCharsConstants.FILTER_REFERENCED;
				/*
				 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
				 * Relationship- END
				 */
				if ("R".equals(strContextRefType) && UIUtil.isNotNullAndNotEmpty(strConnectedRefId)) {
					conextObjectList = doObj.getRelatedObjects(context, CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC,
							type, objectSelects, relSelects, false, true, (short) 1, null, null, 0);

					if (rangeLocal.equalsIgnoreCase(derivedFilterSelection)) {
						return conextObjectList;

					}

					doObj.setId(strConnectedRefId);
				}

				String masterCurState = doObj.getInfo(context, DomainConstants.SELECT_CURRENT);

				// DSM (DS) 2015x.4 ALM 14327 - INC0390662: Performance Characteistics not being
				// displayed in the UI when TS is copied - STARTS
				// DSM (DS) 2018x.6 Apr CW Req 42353 Update the Perf Char on Obsolete Master
				// made visible on Obsolete Part connected - STARTS
				if ("R".equals(strContextRefType) && UIUtil.isNotNullAndNotEmpty(strConnectedRefId)
						&& !(PGPerfCharsConstants.STATE_RELEASE.equals(masterCurState)
								|| PGPerfCharsConstants.STATE_COMPLETE.equals(masterCurState))
						&& !(PGPerfCharsConstants.STATE_OBSOLETE.equals(masterCurState)
								&& PGPerfCharsConstants.STATE_OBSOLETE.equals(strContextCurState))) {
					// DSM (DS) 2018x.6 Apr CW Req 42353 Update the Perf Char on Obsolete Master
					// made visible on Obsolete Part connected - ENDS
					// DSM (DS) 2015x.4 ALM 14327 - INC0390662: Performance Characteistics not being
					// displayed in the UI when TS is copied - ENDS
					if (rangeReferenced.equalsIgnoreCase(derivedFilterSelection)) {
						// reference filter : From reference context , If Master is not in complete
						// state then none of the chars should be shown in reference filter
						return new MapList();
					} else {
						// local filter and all Filter : From reference context , If Master is not in
						// complete state and then only chars whihc are connected to reference locally
						// should be shown in local filter
						return conextObjectList;
					}
				}

				if (rangeAll.equalsIgnoreCase(derivedFilterSelection)
						|| rangeLocal.equalsIgnoreCase(derivedFilterSelection)) {
					// DSO 2013x.4 -Check for derived filter value -END
					// --------------------------------------------------------------------------------------------------------
					// Code Added for new Approach - START
					String relWhere = null;

					// DSM(DS) 2015x.5.1 - Modified for Stability Results Characteristic Table -
					// START
					StringBuffer typeWhere = new StringBuffer();
					typeWhere.append("type!=").append(PGPerfCharsConstants.TYPE_PG_STABILITY_RESULTS);
					// doObj = DomainObject.newInstance(context, objectId);-- Moving it outside the
					// if as the scope of doObj is extended outside the if as well.
					objList = doObj.getRelatedObjects(context, CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, type,
							objectSelects, relSelects, false, true, (short) 1, typeWhere.toString(), relWhere, 0);
					// DSM(DS) 2015x.5.1 - Modified for Stability Results Characteristic Table - END

					MapList charObjList = new MapList();
					Iterator objListItr = objList.iterator();
					while (objListItr.hasNext()) {
						Map ObjMap = (Map) objListItr.next();
						if (ObjMap != null && !ObjMap.isEmpty()) {
							strCharInheritanceType = (String) ObjMap
									.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
							// DSM (DS) 2018X.0 - ALM -21906 INC1681743 - Reference Characteristics showing
							// up in Local filter. - Starts
							if (rangeLocal.equalsIgnoreCase(derivedFilterSelection)
									&& ("M".equals(strContextRefType) || "U".equals(strContextRefType))
									&& strCharInheritanceType.equals(PGPerfCharsConstants.STR_REFERENCED))
								if (rangeLocal.equalsIgnoreCase(derivedFilterSelection)
										&& UIUtil.isNotNullAndNotEmpty(strCharInheritanceType)
										&& strCharInheritanceType.equals(PGPerfCharsConstants.STR_REFERENCED))
								// DSM (DS) 2018X.0 - ALM -21906 INC1681743 - Reference Characteristics showing
								// up in Local filter. - Ends
								{
									charObjList.add(ObjMap);
								} else if (rangeAll.equalsIgnoreCase(derivedFilterSelection) && ("R"
										.equals(strContextRefType)
										&& strCharInheritanceType.equals(PGPerfCharsConstants.STR_LOCAL)
										&& !(PGPerfCharsConstants.STATE_RELEASE.equals(masterCurState)
												|| PGPerfCharsConstants.STATE_COMPLETE.equals(masterCurState)))) {
									charObjList.add(ObjMap);
								}
						}
					}

					// DSM (DS) 2015x.4 ALM 14327 - INC0390662: Performance Characteistics not being
					// displayed in the UI when TS is copied - STARTS
					if (!charObjList.isEmpty()
							&& !("R".equals(strContextRefType) && UIUtil.isNullOrEmpty(strConnectedRefId))) {
						// DSM (DS) 2015x.4 ALM 14327 - INC0390662: Performance Characteistics not being
						// displayed in the UI when TS is copied - ENDS
						objList.removeAll(charObjList);
					}

					Map mpTemp = new HashMap();
					new MapList();
					for (int x = objList.size() - 1; x >= 0; x--) {
						mpTemp = (Map) objList.get(x);
						String strType = (String) mpTemp.get(DomainConstants.SELECT_TYPE);
						if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
							String strCharType = (String) mpTemp
									.get(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);

							if (strCharType != null && !strCharType.equals(type)) {
								objList.remove(x);
							}
						}
					}

					objList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending",
							"integer");
					// DSO 2013x.4: Code to display Sequence Number as same logic as that for
					// Performance Specification view - START
					if (objList != null && objList.size() > 0) {
						objList = pgGetCharacteristicList(context, objList);
					}
					// DSO 2013x.4 : Code to display Sequence Number as same logic as that for
					// Performance Specification view - END

					if (addNewRow != null && addNewRow.equals("true")
							&& (strSwitchMode == null || "".equals(strSwitchMode))) {
						HashMap m = new HashMap();
						m.put(DomainConstants.SELECT_ID, "BLANK");
						objList.add(m);
					}

					MapList mlTempList = new MapList();
					for (int iSeq = 0; iSeq < objList.size(); iSeq++) {
						Map mpTempList = (Map) objList.get(iSeq);
						mpTempList.put("Sequence", Integer.toString(iSeq + 1));
						mlTempList.add(mpTempList);
					}
					objList.clear();
					objList.addAll(mlTempList);
				}
				// DSO 2013x.4 - Check for derived filter value -START

				String strReferenceType = (String) doObj.getInfo(context,
						"attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
				if (rangeAll.equalsIgnoreCase(derivedFilterSelection)
						|| rangeReferenced.equalsIgnoreCase(derivedFilterSelection)) {

					if (UIUtil.isNotNullAndNotEmpty(strReferenceType)) {
						MapList charList = doObj.getRelatedObjects(context,
								CPNCommonConstants.RELATIONSHIP_CHARACTERISTIC, type, objectSelects, relSelects, false,
								true, (short) 1, null, null, 0);

						Iterator charListItrtr = charList.iterator();
						while (charListItrtr.hasNext()) {
							Map charMap = (Map) charListItrtr.next();
							if (charMap != null && !charMap.isEmpty()) {
								String strType = (String) charMap.get(DomainConstants.SELECT_TYPE);
								strCharInheritanceType = (String) charMap
										.get("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
								if (rangeAll.equalsIgnoreCase(derivedFilterSelection)) {
									if (UIUtil.isNotNullAndNotEmpty(strType)
											&& (strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)
													|| strType.equals(type))) {
										String strCharType = (String) charMap.get(
												CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
										if (UIUtil.isNotNullAndNotEmpty(strCharType) && strCharType.equals(type)
												&& (("M".equals(strContextRefType) || ("R".equals(strContextRefType)
														&& (PGPerfCharsConstants.STATE_RELEASE.equals(masterCurState)
																|| PGPerfCharsConstants.STATE_COMPLETE
																		.equals(masterCurState)))))) {
											objList.add(charMap);

										}

									}
								} else if (rangeReferenced.equalsIgnoreCase(derivedFilterSelection)) {
									if (UIUtil.isNotNullAndNotEmpty(strType)
											&& (strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)
													|| strType.equals(type))) {
										String strCharType = (String) charMap.get(
												CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
										// Modified by DSM (DS) 2015X.1 - characteristic is only displayed in the All
										// and Referenced characteristic view, not on the Local view-START
										if ((UIUtil.isNotNullAndNotEmpty(strCharType) && strCharType.equals(type))
												|| "Referenced".equals(strCharInheritanceType)
												|| ("R".equals(strContextRefType) && (PGPerfCharsConstants.STATE_RELEASE
														.equals(masterCurState)
														|| PGPerfCharsConstants.STATE_COMPLETE.equals(masterCurState))))

										{
											objList.add(charMap);

										}
										// Modified by DSM (DS) 2015X.1 - characteristic is only displayed in the All
										// and Referenced characteristic view, not on the Local view-END
									}
								}
							}
						}
					}
				}
				// DSO 2013x.4 - Check for derived filter value - END

				// DSO 2013x.4- Code Added to Display Product Data and it's EBOM and their
				// Master characteristics-STARTS
				partType = doObj.getInfo(context, DomainConstants.SELECT_TYPE);
				if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(partType)
						&& (rangeAll.equalsIgnoreCase(derivedFilterSelection)
								|| rangeReferenced.equalsIgnoreCase(derivedFilterSelection))) {
					// get id from EBOM relationship and their Master
					objList = getEbomPartsCharacterstics(context, paramMap, doObj, objList, derivedFilterSelection,
							objectSelects);

				}

				if ("R".equals(strContextRefType) && conextObjectList != null && !conextObjectList.isEmpty()
						&& !rangeReferenced.equalsIgnoreCase(derivedFilterSelection)) {
					objList.addAll(conextObjectList);
				}
				// Added by DSM (DS) 2015X.1 - characteristic is only displayed in the All and
				// Referenced characteristic view, not on the Local view-START
				if ("R".equals(strContextRefType) && rangeLocal.equalsIgnoreCase(derivedFilterSelection)
						&& strCharInheritanceType.equals(PGPerfCharsConstants.STR_LOCAL)) {
					return objList;
				}
				// Added by DSM (DS) 2015X.1 - characteristic is only displayed in the All and
				// Referenced characteristic view, not on the Local view-END
				if ("R".equals(strContextRefType) && rangeLocal.equalsIgnoreCase(derivedFilterSelection)) {
					objList = conextObjectList;
				}

				// DSM (DS) 2018x.2 ALM-28448 [TC] FPP perf characteristics are not sorted in
				// the expected order - Start
				if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(partType)
						&& (rangeAll.equalsIgnoreCase(derivedFilterSelection)
								|| rangeReferenced.equalsIgnoreCase(derivedFilterSelection))) {
					objList = updatePerfCharsListForFPPWithSorting(context, objList);

				}
				// DSM (DS) 2018x.2 ALM-28448 [TC] FPP perf characteristics are not sorted in
				// the expected order - End
				// DSO 2013x.4- Code Added to Display Product Data and it's EBOM and their
				// Master characteristics - ENDS
			}
		} finally {
			// DSM 2015x.1 - Push and pop context is performed since logged in user may not have access to some data
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return objList;

	}

	/**
	 * Method to uodate Perf Chars for FPPs with ordering
	 * @param context
	 * @param objList
	 * @return
	 * @throws FrameworkException
	 */
	private MapList updatePerfCharsListForFPPWithSorting(Context context, MapList objList) throws FrameworkException {
		StringList slConnectionIds = BusinessUtil.toStringList(objList, DomainRelationship.SELECT_ID);
		String[] strArrIds = new String[slConnectionIds.size()];
		strArrIds = slConnectionIds.toArray(strArrIds);
		MapList mlConnectedObjTypes = DomainRelationship.getInfo(context, strArrIds, StringList
				.create(DomainConstants.SELECT_FROM_TYPE, DomainConstants.SELECT_RELATIONSHIP_ID));
		Map mpObj = null;
		String sObjConnectionId = DomainConstants.EMPTY_STRING;
		Map mpConnectedObj = null;
		String sConnectionId = DomainConstants.EMPTY_STRING;
		String sConnectedObjType = DomainConstants.EMPTY_STRING;

		MapList mlFPPObjList = new MapList();
		MapList mlCUPObjList = new MapList();
		MapList mlMCUPObjList = new MapList();
		MapList mlIPObjList = new MapList();
		MapList mlMIPObjList = new MapList();
		MapList mlCOPObjList = new MapList();
		MapList mlMCOPObjList = new MapList();

		for (int x = 0; x < objList.size(); x++) {
			mpObj = (Map) objList.get(x);
			sObjConnectionId = (String) mpObj.get(DomainRelationship.SELECT_ID);

			for (int y = 0; y < mlConnectedObjTypes.size(); y++) {
				mpConnectedObj = (Map) mlConnectedObjTypes.get(y);
				sConnectionId = (String) mpConnectedObj.get(DomainConstants.SELECT_RELATIONSHIP_ID);
				if (sConnectionId.equals(sObjConnectionId)) {
					sConnectedObjType = (String) mpConnectedObj.get(DomainConstants.SELECT_FROM_TYPE);
				}
			}
			mpObj.put("type", sConnectedObjType);

			if (sConnectedObjType.equals(PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART))
				mlFPPObjList.add(mpObj);
			else if (sConnectedObjType.equals(PGPerfCharsConstants.TYPE_PG_CUSTOMERUNIT))
				mlCUPObjList.add(mpObj);
			else if (sConnectedObjType.equals(PGPerfCharsConstants.TYPE_PG_MASTERCUSTOMERUNIT))
				mlMCUPObjList.add(mpObj);
			else if (sConnectedObjType.equals(PGPerfCharsConstants.TYPE_PG_INNERPACK))
				mlIPObjList.add(mpObj);
			else if (sConnectedObjType.equals(PGPerfCharsConstants.TYPE_PG_MASTERINNERPACKUNIT))
				mlMIPObjList.add(mpObj);
			else if (sConnectedObjType.equals(PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT))
				mlCOPObjList.add(mpObj);
			else if (sConnectedObjType.equals(PGPerfCharsConstants.TYPE_PG_MASTERCONSUMERUNIT))
				mlMCOPObjList.add(mpObj);

		}
		mlFPPObjList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"ascending", "integer");
		mlCUPObjList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"ascending", "integer");
		mlMCUPObjList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"ascending", "integer");
		mlIPObjList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"ascending", "integer");
		mlMIPObjList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"ascending", "integer");
		mlCOPObjList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"ascending", "integer");
		mlMCOPObjList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"ascending", "integer");

		MapList mlObjList = new MapList();
		mlObjList.addAll(mlFPPObjList);
		mlObjList.addAll(mlCUPObjList);
		mlObjList.addAll(mlMCUPObjList);
		mlObjList.addAll(mlIPObjList);
		mlObjList.addAll(mlMIPObjList);
		mlObjList.addAll(mlCOPObjList);
		mlObjList.addAll(mlMCOPObjList);

		MapList mlTempList = new MapList();
		for (int iSeq = 0; iSeq < mlObjList.size(); iSeq++) {
			Map mpTempList = (Map) mlObjList.get(iSeq);
			mpTempList.put("Sequence", Integer.toString(iSeq + 1));
			mlTempList.add(mpTempList);
		}
		objList.clear();
		objList.addAll(mlTempList);
		
		return objList;
	}

	/*
	 * This method accepts all the performance Characteristic List
	 * @param context the eMatrix <code>Context</code> object
	 * @param
	 * @return Void
	 * @throws Exception if the operation fails Added for V2 on 22-Aug-2012
	 */
	public MapList pgGetCharacteristicList(Context context, MapList inputList) throws Exception {
		Iterator objListItr = inputList.iterator();
		int nCount = 0;
		MapList outputList = new MapList();

		while (objListItr.hasNext()) {
			Map perMap = (Map) objListItr.next();
			nCount++;
			perMap.put("nCount", "" + nCount);
			outputList.add(perMap);
		}
		return outputList;
	}

	/**
	 * DSO 2013x.4 Function Added to traverse through the Product Data EBOM
	 * Structure and return it's Customer , Inner Pack and Consumer unit and their
	 * Master's Characterstics.
	 * 
	 * @param context
	 * @param paramMap
	 * @param finishedPartDO
	 * @param objList
	 * @param derivedFilterSelection
	 * @return MapList with the connected EBOM Part and their Masters Characterstics
	 * @throws Exception
	 */
	private MapList getEbomPartsCharacterstics(Context context, Map paramMap, DomainObject finishedPartDO,
			MapList objList, String derivedFilterSelection, StringList objectSelects) throws Exception {

		MapList ebomCharList = new MapList();
		MapList returnList = new MapList();
		String[] newArguments = null;
		StringList strlObjectSelectable = new StringList(3);
		strlObjectSelectable.add(DomainConstants.SELECT_ID);
		strlObjectSelectable.add(DomainConstants.SELECT_TYPE);
		strlObjectSelectable.add(DomainConstants.SELECT_NAME);

		StringList strlRelSelectable = new StringList(3);
		strlRelSelectable.add(DomainRelationship.SELECT_FROM_TYPE);
		strlRelSelectable.add(DomainRelationship.SELECT_TO_TYPE);
		strlRelSelectable.add(DomainRelationship.SELECT_FROM_ID);

		String ebomPartId = null;
		String ebomPartName = null;
		String strDerivedPath = null;
		String relFromType = null;
		String relToType = null;
		String relFromId = null;
		String rangeAll = FrameworkProperties.getProperty(context, "emxCPN.MasterCharacteristics.DerivedRange.All");

		if (objList != null && objList.size() > 0 && rangeAll.equalsIgnoreCase(derivedFilterSelection)) {
			returnList.addAll(objList);
		}

		MapList conectedEBOMPartList = finishedPartDO.getRelatedObjects(context, DomainObject.RELATIONSHIP_EBOM,
				// DSM (DS) 2015x.4 ALM Defect 14297 - FPC Performance Spec inheriting from
				// Feeder codes -START
				PGPerfCharsConstants.TYPE_PG_CUSTOMERUNIT + "," + PGPerfCharsConstants.TYPE_PG_INNERPACK + ","
						+ PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT,
				// DSM (DS) 2015x.4 ALM Defect 14297 - FPC Performance Spec inheriting from
				// Feeder codes -END
				strlObjectSelectable, // Object Select
				strlRelSelectable, // rel Select
				false, // get To
				true, // get From
				(short) 0, // recurse level
				"", // where Clause
				null, // relationshipWhere Clause
				0);// return all

		Iterator ebomItr = conectedEBOMPartList.iterator();
		while (ebomItr.hasNext()) {
			Map partMap = (Map) ebomItr.next();
			ebomPartId = (String) partMap.get(DomainConstants.SELECT_ID);
			ebomPartName = (String) partMap.get(DomainConstants.SELECT_NAME);
			relFromType = (String) partMap.get(DomainRelationship.SELECT_FROM_TYPE);
			relToType = (String) partMap.get(DomainRelationship.SELECT_TO_TYPE);
			relFromId = (String) partMap.get(DomainRelationship.SELECT_FROM_ID);

			// Skip if If a Consumer Unit has another Consumer Unit
			if (PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT.equals(relFromType)
					&& (PGPerfCharsConstants.TYPE_PG_CONSUMERUNIT.equals(relToType)
							|| PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(relToType))) {
				continue;
			}
			// If the CUP has a FPP, then the Master Specifications inheritance stops at the
			// level of the CUP
			if (PGPerfCharsConstants.TYPE_PG_CUSTOMERUNIT.equals(relFromType)
					&& PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(relToType)) {
				// 2015x.4 - ALM - 14297 - If the CUP has a FPP, the Performance Characteristics
				// inheritance stops at the level of the CUP - START
				returnList.clear();
				ebomPartId = relFromId;
				if (UIUtil.isNotNullAndNotEmpty(ebomPartId)) {
					ebomPartName = new DomainObject(ebomPartId).getInfo(context, DomainConstants.SELECT_NAME);
				}
				// continue;
				// 2015x.4 - ALM - 14297 - If the CUP has a FPP, the Performance Characteristics
				// inheritance stops at the level of the CUP - END
			}
			// Modified by V4-2013x.4 for PDF Views - Starts
			String strMode = (String) paramMap.get("Mode");
			// Modified by V4-2013x.4 for PDF Views - Ends

			paramMap.put(PGPerfCharsConstants.STRING_OBJECTID, ebomPartId);
			paramMap.put("pgVPDCPNCharacteristicDerivedFilter", rangeAll);
			newArguments = JPO.packArgs(paramMap);
			ebomCharList.addAll(getMicroChar(context, newArguments, objectSelects));

			Iterator ebomCharListItr = ebomCharList.iterator();
			while (ebomCharListItr.hasNext()) {
				/*
				 * dso2013x.5 ALM:3020 5302 The order of the Performance Characteristics for
				 * master part displayed in FPP are not same as how they are displayed in the
				 * master part itself- Start
				 */
				MapList tempMapList = new MapList();
				StringList tempList = new StringList();
				/*
				 * dso2013x.5 ALM:3020 5302 The order of the Performance Characteristics for
				 * master part displayed in FPP are not same as how they are displayed in the
				 * master part itself- End
				 */
				Map ebomCharListMap = (Map) ebomCharListItr.next();
				strDerivedPath = (String) ebomCharListMap.get("derivedPath");
				if (null == strDerivedPath) {
					// Modified by V4-2013x.4 for PDF Views - Starts

					if ("PDF".equals(strMode)) {
						ebomCharListMap.put("derivedPath", ebomPartName+"|");
					} else {
						//Below line is commented for widget code
						//ebomCharListMap.put("derivedPath", getLink(ebomPartName,ebomPartId));
						
						ebomCharListMap.put("derivedPath", ebomPartName+"|"+ebomPartId);
					}
					// Modified by V4-2013x.4 for PDF Views - Ends

					ebomCharListMap.put("disableSelection", "true");
					ebomCharListMap.put("RowEditable", "readonly");
					ebomCharListMap.put("derivedCharacteristic", "true");
					returnList.add(ebomCharListMap);
				} else {
					/*
					 * dso2013x.5 ALM:3020 5302 The order of the Performance Characteristics for
					 * master part displayed in FPP are not same as how they are displayed in the
					 * master part itself- Start
					 */
					String strParse = (String) ebomCharListMap.get("derivedPath");
					String strObjectId = strParse.substring(strParse.lastIndexOf("objectId=") + 9);
					String sOid = strObjectId.substring(0, strObjectId.indexOf("'"));
					if (tempList.isEmpty()) {
						tempList.add(sOid);
					}
					if (tempList.contains(sOid)) {
						tempMapList.add(ebomCharListMap);
					}
					tempMapList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
							"ascending", "integer");
					for (int j = 0; j < tempMapList.size(); j++) {
						returnList.add((Map) tempMapList.get(j));
					}
					/*
					 * dso2013x.5 ALM:3020 5302 The order of the Performance Characteristics for
					 * master part displayed in FPP are not same as how they are displayed in the
					 * master part itself- End
					 */
				}

			}
			ebomCharList.clear();
			if (relFromType.equals(PGPerfCharsConstants.TYPE_PG_CUSTOMERUNIT)
					&& relToType.equals(PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART)) {
				break;
			}
		}
		return returnList;
	}

	/**
	 * DSO 2013x.4 : Subsidiary method to set the hyper link of the Object
	 * 
	 * @param label
	 * @param objectId
	 * @return String with hyper link for Object
	 * @throws Exception
	 */
	private String getLink(String label, String objectId) throws Exception {
		StringBuffer linkHTMLBuf = new StringBuffer();
		// DSO 2013x.5 - ALM : 4919 - Path shows Javascript in PMP tables page : Start
		label = FrameworkUtil.findAndReplace(label, "&", "&amp;");
		// DSO 2013x.5 - ALM : 4919 - Path shows Javascript in PMP tables page : End
		linkHTMLBuf.append(
				"<a href=\"javascript:void(0)\" onClick=\"javascript:showNonModalDialog('../common/emxTree.jsp?objectId=")
				.append(objectId).append("','860','520');\" >").append(label).append("</a>");

		return linkHTMLBuf.toString();
	}

	/**
	 * DSO 2013x.4 - Master Characteristics Enhancements(Display Master
	 * Characteristics on the Performance Characteristic Table) : Customized OOTB
	 * Method
	 * 
	 * @param context
	 * @param args
	 * @return MapList with Product Data and it's EBOM and their Master
	 *         characteristics
	 * @throws Exception
	 */
	public MapList getMicroChar(matrix.db.Context context, String[] args, StringList objectSelects) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		String partType = null;
		MapList objList = new MapList();
		StringList relSelects = new StringList();
		relSelects.add(DomainObject.SELECT_RELATIONSHIP_ID);
		relSelects.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
		relSelects.add("attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");

		HashMap paramMap = (HashMap) JPO.unpackArgs(args);

		String objectId = (String) paramMap.get("objectId");
		String addNewRow = (String) paramMap.get("AddRow");
		String strSwitchMode = (String) paramMap.get("SwitchMode");
		DomainObject doObj = DomainObject.newInstance(context, objectId);

		String strSelectedTable = (String) paramMap.get(PGPerfCharsConstants.KEY_SELECTED_TABLE);
		strSelectedTable = strSelectedTable.substring(strSelectedTable.lastIndexOf('~') + 1, strSelectedTable.length());

		String strTypeSym = EnoviaResourceBundle.getProperty(context,
				"emxCPN.Characteristic.table." + strSelectedTable);

		String type = PropertyUtil.getSchemaProperty(context, strTypeSym);

		final String derivedFilterSelection = (String) paramMap.get(PGPerfCharsConstants.KEY_CHARS_DERIVED_FILTER);
		String rangeAll = PGPerfCharsConstants.FILTER_ALL;
		String rangeLocal = PGPerfCharsConstants.FILTER_LOCAL;
		String rangeReferenced = PGPerfCharsConstants.FILTER_REFERENCED;
		if (rangeAll.equalsIgnoreCase(derivedFilterSelection) || rangeLocal.equalsIgnoreCase(derivedFilterSelection)) {
			objList = doObj.getRelatedObjects(context, PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC, type,
					objectSelects, relSelects, false, true, (short) 1, null, null, 0);

			Map mpTemp = new HashMap();
			new MapList();
			for (int x = objList.size() - 1; x >= 0; x--) {
				mpTemp = (Map) objList.get(x);
				String strType = (String) mpTemp.get(DomainConstants.SELECT_TYPE);
				if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
					String strCharType = (String) mpTemp
							.get(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);

					if (strCharType != null && !strCharType.equals(type)) {
						objList.remove(x);
					}
				}
			}

			objList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending",
					"integer");

			if (objList != null && objList.size() > 0) {
				objList = pgGetCharacteristicList(context, objList);
			}

			if (addNewRow != null && addNewRow.equals("true") && (strSwitchMode == null || "".equals(strSwitchMode))) {
				HashMap m = new HashMap();
				m.put(DomainConstants.SELECT_ID, "BLANK");
				objList.add(m);
			}

			MapList mlTempList = new MapList();
			for (int iSeq = 0; iSeq < objList.size(); iSeq++) {
				Map mpTempList = (Map) objList.get(iSeq);
				mpTempList.put("Sequence", Integer.toString(iSeq + 1));
				mlTempList.add(mpTempList);
			}
			objList.clear();
			objList.addAll(mlTempList);

		}
		String strReferenceType = (String) doObj.getInfo(context,
				"attribute[" + PGPerfCharsConstants.ATTR_REFERENCE_TYPE + "]");
		if (rangeAll.equalsIgnoreCase(derivedFilterSelection)
				|| rangeReferenced.equalsIgnoreCase(derivedFilterSelection)) {

			if (UIUtil.isNotNullAndNotEmpty(strReferenceType) && "R".equalsIgnoreCase(strReferenceType)) {
				MapList charList = getMasterCharacteristics(context, args, objectSelects);

				Iterator charListItrtr = charList.iterator();
				while (charListItrtr.hasNext()) {
					Map charMap = (Map) charListItrtr.next();
					if (charMap != null && !charMap.isEmpty()) {
						String strType = (String) charMap.get(DomainConstants.SELECT_TYPE);
						if (strType != null && strType.equals(type)) {
							objList.add(charMap);
						}

						if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
							String strCharType = (String) charMap
									.get(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);
							if (strCharType != null && strCharType.equals(type)) {
								objList.add(charMap);
							}
						}
					}
				}
			}
		}

		partType = doObj.getInfo(context, DomainConstants.SELECT_TYPE);
		if (PGPerfCharsConstants.TYPE_FINISHEDPRODUCT_PART.equals(partType)
				&& (rangeAll.equalsIgnoreCase(derivedFilterSelection)
						|| rangeReferenced.equalsIgnoreCase(derivedFilterSelection))) {
			objList = getEbomPartsCharacterstics(context, paramMap, doObj, objList, derivedFilterSelection,
					objectSelects);
		}

		return objList;

	}

	/**
	 * DSO 2103x.4 -Code for getting master characteristics from the context of
	 * Reference
	 * 
	 * @param context
	 * @param args
	 * @return MapList with master characteristics from the context of Reference
	 * @throws Exception
	 */
	public MapList getMasterCharacteristics(matrix.db.Context context, String[] args, StringList objectSelects)
			throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		MapList objList = new MapList();
		String objectId = getParam(args, PGPerfCharsConstants.KEY_OBJECT_ID);
		String addNewRow = getParam(args, PGPerfCharsConstants.KEY_ADD_ROW);
		String strSwitchMode = getParam(args, PGPerfCharsConstants.KEY_SWITCH_MODE);
		String strSelectedTable = getParam(args, PGPerfCharsConstants.KEY_SELECTED_TABLE);
		// Modified by V4-2013x.4 for PDF Views - Starts
		String strMode = getParam(args, PGPerfCharsConstants.KEY_MODE);
		// Modified by V4-2013x.4 for PDF Views - Ends
		strSelectedTable = strSelectedTable.substring(strSelectedTable.lastIndexOf('~') + 1, strSelectedTable.length());

		String strTypeSym = FrameworkProperties.getProperty(context, "emxCPN.Characteristic.table." + strSelectedTable);

		String type = PropertyUtil.getSchemaProperty(context, strTypeSym);
		if (UIUtil.isNotNullAndNotEmpty(objectId)) {
			DomainObject doObj = DomainObject.newInstance(context, objectId);
			String masterPartIdSelect = getMasterPartSelect(context, DomainConstants.SELECT_ID);
			String masterPartNameSelect = getMasterPartSelect(context, DomainConstants.SELECT_NAME);
			// DSO 2013x.4 - Modified to fix the Part family Issue- Start
			String masterPartTypeSelect = getMasterPartSelect(context, DomainConstants.SELECT_TYPE);
			// Commented to Add the Master part Type select
			// DSO 2013x.4 : Fix for ALM Defect 2109 : START
			String masterPartCurrentSelect = getMasterPartSelect(context, DomainConstants.SELECT_CURRENT);
			// StringList objSelects =
			// UTILS.createSelects(SELECT_ID,SELECT_NAME,masterPartIdSelect,
			// masterPartNameSelect);
			StringList objSelects = createSelects(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME,
					DomainConstants.SELECT_POLICY, "attribute[" + PGPerfCharsConstants.STR_RELEASE_PHASE + "]",
					masterPartIdSelect, masterPartNameSelect, masterPartTypeSelect, masterPartCurrentSelect);
			// DSO 2013x.4 : Fix for ALM Defect 2109 : END
			// DSO 2013x.4 - Modified to fix the Part family Issue- End
			Map objAndMasterMap = doObj.getInfo(context, objSelects);
			if (objAndMasterMap != null && !objAndMasterMap.isEmpty()) {
				String masterPartId = (String) objAndMasterMap.get(masterPartIdSelect);
				objAndMasterMap.get(DomainConstants.SELECT_POLICY);
				String strMasterCurrent = (String) objAndMasterMap.get(masterPartCurrentSelect);
				// DSO 2013x.4 - Modified to fix the Part family Issue- Ends
				if (PGPerfCharsConstants.STATE_RELEASE.equalsIgnoreCase(strMasterCurrent)
						|| PGPerfCharsConstants.STATE_COMPLETE.equalsIgnoreCase(strMasterCurrent)) {
					// DSO 2013x.4 : Fix for ALM Defect 2109 : END
					if (UIUtil.isNotNullAndNotEmpty(masterPartId)) {
						objList = getCharacteristicsList(context, masterPartId, type, addNewRow, strSwitchMode,
								objectSelects);
					}
					// Modified by V4-2013x.4 for PDF Views - Starts
					objAndMasterMap.put("Mode", strMode);
					// Modified by V4-2013x.4 for PDF Views - Ends
					objList = updatePathForImmediateMaster(context, objList, objAndMasterMap);
				}

			}
		}

		return objList;
	}

	/**
	 * DSO 2013x.4 Update the Derived path for the Immediate Master of the Product
	 * Data Part (Reference)
	 * 
	 * @param context
	 * @param characteristics
	 * @param objAndMasterMap
	 * @return MapList
	 */
	private MapList updatePathForImmediateMaster(Context context, MapList characteristics, Map objAndMasterMap) {
		// Modified by V4-2013x.4 for PDF Views - Starts
		String path = "";
		try {
			// Modified by V4-2013x.4 for PDF Views - Ends
			String masterPartIdSelect = getMasterPartSelect(context, DomainConstants.SELECT_ID);
			String masterPartNameSelect = getMasterPartSelect(context, DomainConstants.SELECT_NAME);
			// Modified by V4-2013x.4 for PDF Views - Starts
			String strMode = (String) objAndMasterMap.get("Mode");

			String objLink = "";
			String masterLink = "";

			String partFamilyId = DomainConstants.EMPTY_STRING;
			String partFamilyName = DomainConstants.EMPTY_STRING;
			StringList slPartFamilyIdList = new StringList();

			if (UIUtil.isNotNullAndNotEmpty(strMode) && "PDF".equals(strMode)) {
				objLink = (String) objAndMasterMap.get(DomainConstants.SELECT_NAME);
				String strMasterPFId = (String) objAndMasterMap.get(masterPartIdSelect);
				String strMasterPFName = (String) objAndMasterMap.get(masterPartNameSelect);
				if (UIUtil.isNotNullAndNotEmpty(strMasterPFId)) {
					DomainObject doObj = DomainObject.newInstance(context, strMasterPFId);
					String classifiedItemRel = PropertyUtil.getSchemaProperty(context, "relationship_ClassifiedItem");
					/*
					 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
					 * Relationship- START
					 */
					slPartFamilyIdList = doObj.getInfoList(context, "to[" + classifiedItemRel + "].from.id");
					slPartFamilyIdList = filterRestrictedControlClass(context, slPartFamilyIdList);
					if (slPartFamilyIdList != null && !slPartFamilyIdList.isEmpty()) {
						for (Object objPFId : slPartFamilyIdList) {
							partFamilyId = objPFId.toString();
							if (UIUtil.isNotNullAndNotEmpty(partFamilyId)) {
								partFamilyName = (DomainObject.newInstance(context, partFamilyId)).getInfo(context,
										DomainConstants.SELECT_NAME);
								/*
								 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
								 * Relationship- END
								 */
								masterLink = strMasterPFName;
								String PFLink = partFamilyName;
								// Modified by V4-IPM/DSO-2013x.5 for Defect-4673 Starts
								// path = objLink + ">" + masterLink;
								path = objLink + "->" + masterLink;
								// Modified by V4-IPM/DSO-2013x.5 for Defect-4673 Ends
								if (UIUtil.isNotNullAndNotEmpty(partFamilyId)) {
									// Modified by V4-IPM/DSO-2013x.5 for Defect-4673 Starts
									// path = PFLink + ">" + masterLink;
									path = PFLink + "->" + masterLink;
									// Modified by V4-IPM/DSO-2013x.5 for Defect-4673 Ends
								} else {
									path = masterLink;
								}
								/*
								 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
								 * Relationship- START
								 */
							}
						}
					}
					/*
					 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
					 * Relationship- END
					 */
				}
			} else {
				String strMasterPFId = (String) objAndMasterMap.get(masterPartIdSelect);
				String strMasterPFName = (String) objAndMasterMap.get(masterPartNameSelect);
				if (UIUtil.isNotNullAndNotEmpty(strMasterPFId)) {
					DomainObject doObj = DomainObject.newInstance(context, strMasterPFId);
					String classifiedItemRel = PropertyUtil.getSchemaProperty(context, "relationship_ClassifiedItem");
					/*
					 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
					 * Relationship- START
					 */
					slPartFamilyIdList = doObj.getInfoList(context, "to[" + classifiedItemRel + "].from.id");
					slPartFamilyIdList = filterRestrictedControlClass(context, slPartFamilyIdList);
					if (slPartFamilyIdList != null && !slPartFamilyIdList.isEmpty()) {
						for (Object objPFId : slPartFamilyIdList) {
							partFamilyId = objPFId.toString();
							if (UIUtil.isNotNullAndNotEmpty(partFamilyId)) {
								partFamilyName = (DomainObject.newInstance(context, partFamilyId)).getInfo(context,
										DomainConstants.SELECT_NAME);
								/*
								 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
								 * Relationship- END
								 */
								masterLink = getLink(strMasterPFName, strMasterPFId);
								String PFLink = getLink(partFamilyName, partFamilyId);

								if (UIUtil.isNotNullAndNotEmpty(partFamilyId)) {
									//path = PFLink + PGPerfCharsConstants.ARROW_SYMBOL + masterLink;
									path = partFamilyName + PGPerfCharsConstants.ARROW_SYMBOL + strMasterPFName;
								} else {
									path = strMasterPFName +"|"+ strMasterPFId; //masterLink;
								}
								// Modified by V4-2013x.4 for PDF Views - Starts
								/*
								 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
								 * Relationship- START
								 */
							}
						}
					}
					/*
					 * DSM (DS) 2015x.1 - Changes to handle IP Security classes from Classified Item
					 * Relationship- END
					 */
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_FETCH_DATA, e);
		}
		// Modified by V4-2013x.4 for PDF Views - Ends
		return addSettingToObjects(characteristics, "derivedPath", path);

	}

	/**
	 * Add setting to return Object
	 * 
	 * @param objList
	 * @param settingName
	 * @param settingValue
	 * @return
	 */
	private MapList addSettingToObjects(MapList objList, String settingName, String settingValue) {
		MapList returnList = new MapList();
		Iterator charIterator = objList.iterator();
		while (charIterator.hasNext()) {
			Map objMap = (Map) charIterator.next();
			objMap.put(settingName, settingValue);
			returnList.add(objMap);
		}
		return returnList;
	}

	/**
	 * DSM (DS) 2015x.1 : Utility method to filter IP Control Class from the objects
	 * retrieved expanding over "Classified Item" relationship
	 * 
	 * @param context
	 * @param slIdList
	 * @return slFilteredIdList
	 * @throws FrameworkException
	 */
	public StringList filterRestrictedControlClass(Context context, StringList slIdList) throws FrameworkException {
		StringList slFilteredIdList = new StringList();
		String strObjectType = DomainConstants.EMPTY_STRING;
		StringList slIPControlClassType = FrameworkUtil.split(PGPerfCharsConstants.SECURITYCLASS_LIST, ",");
		StringList slSelectList = new StringList(DomainConstants.SELECT_TYPE);
		slSelectList.add(DomainConstants.SELECT_ID);
		Map mpObjectInfo = new HashMap();
		if (slIdList != null && !slIdList.isEmpty()) {
				MapList mlObjectInfoList = DomainObject.getInfo(context,
						(String[]) slIdList.toArray(new String[slIdList.size()]), slSelectList);
				if (mlObjectInfoList != null && !mlObjectInfoList.isEmpty()) {
					for (Object objInfo : mlObjectInfoList) {
						mpObjectInfo = (Map) objInfo;
						strObjectType = mpObjectInfo.get(DomainConstants.SELECT_TYPE).toString();
						if (slIPControlClassType != null && !slIPControlClassType.isEmpty() && !slIPControlClassType
								.contains(FrameworkUtil.getAliasForAdmin(context, "type", strObjectType, false))) {
							slFilteredIdList.add(mpObjectInfo.get(DomainConstants.SELECT_ID).toString());
						}
					}
				}
		}
		return slFilteredIdList;
	}

	/**
	 * get Perf Chars for FPP
	 * 
	 * @param context
	 * @param objectId
	 * @param type
	 * @param addNewRow
	 * @param strSwitchMode
	 * @return
	 * @throws Exception
	 */
	private MapList getCharacteristicsList(Context context, String objectId, String type, String addNewRow,
			String strSwitchMode, StringList objectSelects) throws Exception {
		MapList objList = new MapList();
		// DSM (DS) 2015x.1.2 ALM defect 9397 - Duplicate perfomance characristics
		// display for FPP -starts
		StringList relSelects = createSelects(DomainConstants.SELECT_RELATIONSHIP_ID,
				PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE,
				"attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
		// DSM (DS) 2015x.1.2 ALM defect 9397 - Duplicate perfomance characristics
		// display for FPP -ends
		DomainObject doMasterPart = DomainObject.newInstance(context, objectId);
		objList = doMasterPart.getRelatedObjects(context, // the eMatrix Context object
				PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC, // Relationship pattern
				type, // Type pattern
				objectSelects, // Object selects
				relSelects, // Relationship selects
				false, // get From relationships
				true, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				null, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available

		Map mpTemp = new HashMap();
		new MapList();
		for (int x = objList.size() - 1; x >= 0; x--) {
			mpTemp = (Map) objList.get(x);
			String strType = (String) mpTemp.get(DomainConstants.SELECT_TYPE);

			if (strType != null && strType.equals(CPNCommonConstants.TYPE_SHARED_TABLE)) {
				String strCharType = (String) mpTemp
						.get(CPNCommonConstants.SELECT_ATTRIBUTE_SHAREDTTABLECHARACTERISTICTYPE);

				if (strCharType != null && !strCharType.equals(type)) {
					objList.remove(x);
				}

			}
		}

		objList.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, "ascending", "integer");

		if (addNewRow != null && addNewRow.equals("true") && (strSwitchMode == null || "".equals(strSwitchMode))) {
			HashMap m = new HashMap();
			m.put(DomainConstants.SELECT_ID, "BLANK");
			objList.add(m);
		}

		MapList mlTempList = new MapList();
		for (int iSeq = 0; iSeq < objList.size(); iSeq++) {
			Map mpTempList = (Map) objList.get(iSeq);
			mpTempList.put("Sequence", Integer.toString(iSeq + 1));
			mlTempList.add(mpTempList);
		}
		objList.clear();
		objList.addAll(mlTempList);

		return objList;
	}

	/**
	 * 
	 * This method returns parameter value from the packed arguments
	 * 
	 * @param args
	 * @param paramName
	 * @return String
	 * @throws Exception
	 */
	public String getParam(String[] args, String paramName) throws Exception {
		Map paramMap = (Map) JPO.unpackArgs(args);
		String paramValue = "";
		if (paramMap.get(paramName) != null) {
			Object value = paramMap.get(paramName);
			if (value instanceof String[]) {
				paramValue = ((String[]) paramMap.get(paramName))[0];
			} else if (value instanceof String) {
				paramValue = (String) paramMap.get(paramName);
			}
		} else {
			Map requestMap = (Map) paramMap.get("requestMap");
			if (requestMap != null)
				paramValue = (String) requestMap.get(paramName);
		}
		return paramValue;
	}

	/**
	 * This Method returns StringList of selectable
	 * 
	 * @param selects
	 * @return selectList
	 */
	public StringList createSelects(String... selects) {
		StringList selectList = new StringList();
		for (String select : selects) {
			selectList.add(select);
		}
		return selectList;
	}

	/**
	 * Method to get Master Part select
	 * 
	 * @param context
	 * @param finalSelect
	 * @return
	 */
	private String getMasterPartSelect(Context context, String finalSelect) {
		String classifiedItemRel = PropertyUtil.getSchemaProperty(context, "relationship_ClassifiedItem");
		String partFamilyReferenceRel = PropertyUtil.getSchemaProperty(context, "relationship_PartFamilyReference");
		StringBuffer masterPartSelectBuf = new StringBuffer();
		masterPartSelectBuf.append("to[").append(classifiedItemRel).append("].frommid[").append(partFamilyReferenceRel)
				.append("].torel.to.").append(finalSelect);
		return masterPartSelectBuf.toString();
	}

}