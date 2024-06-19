package com.pg.widgets.structuredats;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.engineering.RelToRelUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.Relationship;
import matrix.util.StringList;

public class PGStructuredATSReplaceOperationsUtil {

	private static final Logger logger = Logger.getLogger(PGStructuredATSReplaceOperationsUtil.class.getName());

	static final String TYPE_PARENT_SUB_REGISTRY_NAME = PGStructuredATSConstants.TYPE_PARENT_SUB_REGISTRY_NAME;
	static final String POLICY_PARENT_SUB_REGISTRY_NAME = PGStructuredATSConstants.POLICY_PARENT_SUB_REGISTRY_NAME;
	Map<String, Map<String, String>> mapFBOMParentSubMap = new HashMap<>();
	PGStructuredATSBOMDataUtil objStructuredATSBOMDataUtil = new PGStructuredATSBOMDataUtil();
	
	/**
	 * Method for replace operations on BOM page for SATS
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return 
	 */
	String replaceOperationsSATS(Context context, String strJsonInput) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonArrayBuilder jsonBOMArrBuilder = Json.createArrayBuilder();
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strSATSId = jsonInputData.getString(DomainConstants.SELECT_ID);
			JsonArray jsonBOMArray = jsonInputData.getJsonArray(PGStructuredATSConstants.KEY_DATA);
			int iBOMArraySize = jsonBOMArray.size();
			Map<String, String> mpActionsVaueMap = getActionsValueMap();
			mapFBOMParentSubMap = new HashMap<>();
			
			Map<String,String> mpObjNamesMap = checkForExistingATSContextConnections(context, jsonBOMArray, iBOMArraySize, jsonBOMArrBuilder);
								
			if(mpObjNamesMap.isEmpty()) {
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			} else {				
				String strSelectedObjNames = mpObjNamesMap.get(PGStructuredATSConstants.KEY_AFFECTED_PART);
				String strConnectedATSNames = mpObjNamesMap.get(PGStructuredATSConstants.TYPE_SATS);
				
				StringBuilder sbErroMsg = new StringBuilder();
				sbErroMsg.append(PGStructuredATSConstants.ERROR_MSG_SELECTED_FORMULAS).append(strSelectedObjNames);
				sbErroMsg.append(PGStructuredATSConstants.ERROR_MSG_FORMULAS_REPLACED).append(strConnectedATSNames);
				
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, sbErroMsg.toString());
			}
			
			JsonArray jsonValidBOMArray = jsonBOMArrBuilder.build();
			int iValidBOMArraySize = jsonValidBOMArray.size();
			
			for (int i = 0; i < iValidBOMArraySize; i++) {
				JsonObject jsonInputObj = jsonValidBOMArray.getJsonObject(i);
				String strRelId = jsonInputObj.getString(PGStructuredATSConstants.KEY_REL_ID);
				Relationship relationship = new Relationship(strRelId);
				relationship.open(context);
				String strRelationshipName = relationship.getTypeName();
				relationship.close(context);
				String strActionsAttrValue = mpActionsVaueMap.get(strRelationshipName);

				if (DomainConstants.RELATIONSHIP_EBOM.equals(strRelationshipName)
						|| PGStructuredATSConstants.RELATIONSHIP_EBOM_SUBSTITUTE.equals(strRelationshipName)) {
					replaceOperationsForEBOM(context, strSATSId, jsonInputObj, strActionsAttrValue);
				} else if (PGStructuredATSConstants.RELATIONSHIP_FBOM.equals(strRelationshipName)
						|| PGStructuredATSConstants.RELATIONSHIP_FBOM_SUBSTITUTE.equals(strRelationshipName)) {
					replaceOperationsForFBOM(context, strSATSId, jsonInputObj, strActionsAttrValue);
				} else if (PGStructuredATSConstants.RELATIONSHIP_ALTERNATE.equals(strRelationshipName)) {
					replaceOperationsForAlternate(context, strSATSId, jsonInputObj, strActionsAttrValue);
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		
		return jsonReturnObj.build().toString();
	}

	/**
	 * Check for the already connected objects with existing 'ATS Context' relationship
	 * @param context
	 * @param jsonBOMArray
	 * @param iBOMArraySize
	 * @param jsonBOMArrBuilder
	 * @return
	 * @throws FrameworkException
	 */
	private Map<String, String> checkForExistingATSContextConnections(Context context, JsonArray jsonBOMArray,
			int iBOMArraySize, JsonArrayBuilder jsonBOMArrBuilder) throws FrameworkException {
		Map<String,String> mpObjNamesMap = new HashMap<>();
		StringList slExistingATSObjNameList = new StringList();
		StringList slSelectedObjNameList = new StringList();
		for (int i = 0; i < iBOMArraySize; i++) {
			JsonObject jsonInputObj = jsonBOMArray.getJsonObject(i);
			String strRelId = jsonInputObj.getString(PGStructuredATSConstants.KEY_REL_ID);
			String strParentType = "";
			if(jsonInputObj.containsKey(PGStructuredATSConstants.KEY_PARENT_TYPE)) {
				strParentType = jsonInputObj.getString(PGStructuredATSConstants.KEY_PARENT_TYPE);
			} else if(jsonInputObj.containsKey(PGStructuredATSConstants.KEY_PARENT_ID)) {
				String strParentId = jsonInputObj.getString(PGStructuredATSConstants.KEY_PARENT_ID);
				DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
				strParentType = dobParentObj.getInfo(context, DomainConstants.SELECT_TYPE);
			}
					
			StringList slRelSelects = new StringList();
			slRelSelects.add(PGStructuredATSConstants.SELECT_ATS_CONTEXT_RELID);
			slRelSelects.add(PGStructuredATSConstants.SELECT_ATS_CONTEXT_RELID_FOR_FBOM_SUBSTITUTES);
			
			if (PGStructuredATSConstants.TYPE_FORMULATION_PART.equals(strParentType)
					|| PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strParentType)) {
				slRelSelects.add(PGStructuredATSConstants.SELECT_RELATED_FOP_FOR_FBOM);
				slRelSelects.add(PGStructuredATSConstants.SELECT_RELATED_FOP_FOR_FBOM_SUB);
			} else {
				slRelSelects.add(PGStructuredATSConstants.SELECT_RELATED_APP_FOR_EBOM_SUB);
				slRelSelects.add(DomainConstants.SELECT_FROM_NAME);
			}
			
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strRelId;
			MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlConnectionAttrList.get(0);

			Object objATSContextRelId =  mpObjInfoMap.get(PGStructuredATSConstants.SELECT_ATS_CONTEXT_RELID);
			Object objATSContextRelIdFBOMSub =  mpObjInfoMap.get(PGStructuredATSConstants.SELECT_ATS_CONTEXT_RELID_FOR_FBOM_SUBSTITUTES);
			
			StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);
			StringList slATSCtxRelIdsFBOMSubs = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelIdFBOMSub);
			
			if((slATSCtxRelIds != null && !slATSCtxRelIds.isEmpty()) || (slATSCtxRelIdsFBOMSubs != null && !slATSCtxRelIdsFBOMSubs.isEmpty())) {
				updateListWithSelectedObjNames(mpObjInfoMap, slSelectedObjNameList, strParentType);				
				updateListWithConnectedATSObjNames(context, slATSCtxRelIds, slATSCtxRelIdsFBOMSubs, slExistingATSObjNameList);
			} else {
				jsonBOMArrBuilder.add(jsonInputObj);
			}
		}
		
		if(!slExistingATSObjNameList.isEmpty()) {
			String strConnectedATSNames = getStringFromStringList(slExistingATSObjNameList);
			String strSelectedObjNames = getStringFromStringList(slSelectedObjNameList);
			mpObjNamesMap.put(PGStructuredATSConstants.KEY_AFFECTED_PART, strSelectedObjNames);
			mpObjNamesMap.put(PGStructuredATSConstants.TYPE_SATS, strConnectedATSNames);
		}
				
		return mpObjNamesMap;
	}

	/**
	 * Method to update the list with selected object names
	 * 
	 * @param mpObjInfoMap
	 * @param slSelectedObjNameList
	 */
	private void updateListWithSelectedObjNames(Map<?, ?> mpObjInfoMap, StringList slSelectedObjNameList,
			String strParentType) {
		StringList slParentObjNameList = new StringList();
		if (PGStructuredATSConstants.TYPE_FORMULATION_PART.equals(strParentType)
				|| PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strParentType)) {
			updateListWithParentObjNames(slParentObjNameList, mpObjInfoMap,
					PGStructuredATSConstants.SELECT_RELATED_FOP_FOR_FBOM_SUB,
					PGStructuredATSConstants.SELECT_RELATED_FOP_FOR_FBOM);

		} else {

			updateListWithParentObjNames(slParentObjNameList, mpObjInfoMap,
					PGStructuredATSConstants.SELECT_RELATED_APP_FOR_EBOM_SUB, DomainConstants.SELECT_FROM_NAME);
		}

		int iListSize = slParentObjNameList.size();
		for (int i = 0; i < iListSize; i++) {
			String strParentName = slParentObjNameList.get(i);
			if (!slSelectedObjNameList.contains(strParentName)) {
				slSelectedObjNameList.add(strParentName);
			}
		}
	}

	/**
	 * Method to update final list with Parent names
	 * @param slParentObjNameList
	 * @param mpObjInfoMap
	 * @param selectRelatedFopForFbomSub
	 * @param selectRelatedFopForFbom
	 */
	private void updateListWithParentObjNames(StringList slParentObjNameList, Map<?, ?> mpObjInfoMap,
			String strSelectSubstituteParent, String strSelectBOMParent) {
		Object objParentNamesForSubs = mpObjInfoMap.get(strSelectSubstituteParent);
		StringList slPanentNameForSubsList = objStructuredATSBOMDataUtil.getStringListFromObject(objParentNamesForSubs);
		if(slPanentNameForSubsList != null && !slPanentNameForSubsList.isEmpty()) {
			slParentObjNameList.addAll(slPanentNameForSubsList);
		} else {
			Object objParentNamesForBOMObj = mpObjInfoMap.get(strSelectBOMParent);
			StringList slParentNameForBOMList = objStructuredATSBOMDataUtil.getStringListFromObject(objParentNamesForBOMObj);
			if(slParentNameForBOMList != null && !slParentNameForBOMList.isEmpty()) {
				slParentObjNameList.addAll(slParentNameForBOMList);
			}
		}
		
	}

	/**
	 * Method to get comma separated string value from StringList
	 * @param slObjNameList
	 * @return
	 */
	private String getStringFromStringList(StringList slObjNameList) {
		StringBuilder sbConnectedObjName = new StringBuilder();
		int iSize = slObjNameList.size();
		int iCount = 1;
		for(int j=0;j<iSize;j++) {
			String strATSName = slObjNameList.get(j);
			sbConnectedObjName.append(strATSName);
			
			if (iCount < iSize) {
				sbConnectedObjName.append(",");
			}
			iCount++;
		}
		return sbConnectedObjName.toString();
	}

	/**
	 * Method to updated the list with already connected ATS object names.
	 * @param context
	 * @param slATSCtxRelIds
	 * @param slATSCtxRelIdsFBOMSubs
	 * @param slExistingATSObjNameList
	 * @throws FrameworkException 
	 */
	private void updateListWithConnectedATSObjNames(Context context, StringList slATSCtxRelIds,
			StringList slATSCtxRelIdsFBOMSubs, StringList slExistingATSObjNameList) throws FrameworkException {
		String strATSCtxRelId = "";
		if(slATSCtxRelIds == null || slATSCtxRelIds.isEmpty()) {
			strATSCtxRelId = slATSCtxRelIdsFBOMSubs.get(0);
		} else {
			strATSCtxRelId = slATSCtxRelIds.get(0);
		}

		String[] strATSCtxRelIdArray = new String[1];
		strATSCtxRelIdArray[0] = strATSCtxRelId;
		MapList mlRelatedSATSObjList = DomainRelationship.getInfo(context, strATSCtxRelIdArray, new StringList(PGStructuredATSConstants.SELECT_RELATED_SATS_NAME));
		Map<?, ?> mpSTASObj = (Map<?, ?>) mlRelatedSATSObjList.get(0);

		Object objRelatedSATS =  mpSTASObj.get(PGStructuredATSConstants.SELECT_RELATED_SATS_NAME);
		StringList slRelatedSATSObjList = objStructuredATSBOMDataUtil.getStringListFromObject(objRelatedSATS);
		
		if(slRelatedSATSObjList != null) {
			String strATSObjName = slRelatedSATSObjList.get(0);
			if(!slExistingATSObjNameList.contains(strATSObjName)) {
				slExistingATSObjNameList.add(strATSObjName);
			}
		}
		
	}

	/**
	 * Method for many to many replace operation of FOP on BOM page for SATS
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	String replaceOperationsSATSForMultipleSources(Context context, String strJsonInput) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonArrayBuilder jsonBOMArrBuilder = Json.createArrayBuilder();
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strSATSId = jsonInputData.getString(DomainConstants.SELECT_ID);
			JsonArray jsonSubGroupArray = jsonInputData.getJsonArray(PGStructuredATSConstants.KEY_DATA);
			
			boolean isDataAlreadyReplaced = false;
			StringList slFormulaNamesList = new StringList();
			StringList slConnectedATSNamesList = new StringList();
			
			int iSubGroupArraySize = jsonSubGroupArray.size();
			for(int i=0; i<iSubGroupArraySize; i++) {
				JsonObject jsonSubGroupObj = jsonSubGroupArray.getJsonObject(i);
				
				JsonArray jsonSourceArray = jsonSubGroupObj.getJsonArray(PGStructuredATSConstants.KEY_SOURCE_DATA);
				int iSourceArraySize = jsonSourceArray.size();
				Map<String,String> mpObjNamesMap = checkForExistingATSContextConnections(context, jsonSourceArray, iSourceArraySize, jsonBOMArrBuilder);
				
				if(mpObjNamesMap.isEmpty()) {
					JsonArray jsonTargetArray = jsonSubGroupObj.getJsonArray(PGStructuredATSConstants.KEY_TARGET_DATA);
					
					DomainObject dobSATSObj = DomainObject.newInstance(context, strSATSId);
					String strParentSubId = createNewParentSubObj(context);
					DomainObject dobParentSubObj = DomainObject.newInstance(context, strParentSubId);
					DomainRelationship drATSOperationPSObj = DomainRelationship.connect(context, dobSATSObj,
							PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobParentSubObj);
					String strATSOperationRelId = drATSOperationPSObj.getPhysicalId(context);

					addTargetObjectsToParentSub(context, strParentSubId, jsonTargetArray);

					connectSourceFBOMIdsToATSOperationRel(context, strATSOperationRelId, jsonSourceArray);

				} else {
					String strSelectedObjNames = mpObjNamesMap.get(PGStructuredATSConstants.KEY_AFFECTED_PART);
					slFormulaNamesList = updateFormulaATSNamesList(slFormulaNamesList, strSelectedObjNames);

					String strConnectedATSNames = mpObjNamesMap.get(PGStructuredATSConstants.TYPE_SATS);
					slConnectedATSNamesList = updateFormulaATSNamesList(slConnectedATSNamesList, strConnectedATSNames);
							
					isDataAlreadyReplaced = true;
				}
			}
			
			if(isDataAlreadyReplaced) {
				StringBuilder sbErroMsg = new StringBuilder();
				sbErroMsg.append(PGStructuredATSConstants.ERROR_MSG_SELECTED_FORMULAS).append(getStringFromStringList(slFormulaNamesList));
				sbErroMsg.append(PGStructuredATSConstants.ERROR_MSG_FORMULAS_REPLACED).append(getStringFromStringList(slConnectedATSNamesList));
				
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, sbErroMsg.toString());
			} else {
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}

		return jsonReturnObj.build().toString();
	}

	/**
	 * Method to update list with unique Formula or ATS names
	 * @param slFormulaNamesList
	 * @param strSelectedObjNames
	 */
	private StringList updateFormulaATSNamesList(StringList slNamesList, String strSelectedObjNames) {
		StringList slFinalNamesList = new StringList();
		slFinalNamesList.addAll(slNamesList);
		StringList slSelectedNamesList = StringUtil.split(strSelectedObjNames, ",");
		int iListSize = slSelectedNamesList.size();
		for(int i=0;i<iListSize;i++) {
			String strSelectedName = slSelectedNamesList.get(i);
			if(!slFinalNamesList.contains(strSelectedName)) {
				slFinalNamesList.add(strSelectedName);
			}
		}
		
		return slFinalNamesList;
	}

	/**
	 * Method to add the Balancing Material to SATS
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	String addBalancingMaterialToSATS(Context context, String strJsonInput) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			JsonArray jsonBOMArray = jsonInputData.getJsonArray(PGStructuredATSConstants.KEY_DATA);
			int iJsonArraySize = jsonBOMArray.size();

			String strPhaseForBM = checkIfBMExistInAnySource(context, jsonBOMArray, iJsonArraySize);
			addBalancingMaterial(context, iJsonArraySize, jsonBOMArray, strPhaseForBM);

			jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			
			return jsonReturnObj.build().toString();
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			jsonReturnObj = Json.createObjectBuilder();
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonReturnObj.build().toString();
		}
		
	}

	/**
	 * Method to add the Balancing Material to SATS
	 * 
	 * @param context
	 * @param iJsonArraySize
	 * @param jsonBOMArray
	 * @param strPhaseForBM
	 * @throws Exception 
	 */
	private void addBalancingMaterial(Context context, int iJsonArraySize, JsonArray jsonBOMArray,
			String strPhaseForBM) throws Exception {
		try {
			for (int i = 0; i < iJsonArraySize; i++) {
				JsonObject jsonBOMObj = jsonBOMArray.getJsonObject(i);
				String strATSContextRelId = jsonBOMObj.getString(PGStructuredATSConstants.KEY_REL_ID);
				String strBMId = jsonBOMObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);

				JsonObject jsonBMAttributes = jsonBOMObj.getJsonObject(PGStructuredATSConstants.KEY_REL_ATTRIBUTES);
				Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonBMAttributes);
				mpAttributeInfoMap.put(PGStructuredATSConstants.ATTRIBUTE_BALANCING_MATERIAL,
						PGStructuredATSConstants.VALUE_TRUE);

				if (UIUtil.isNotNullAndNotEmpty(strPhaseForBM)) {
					mpAttributeInfoMap.put(PGStructuredATSConstants.ATTRIBUTE_BM_PHASE, strPhaseForBM);
				} else {
					String strParentId = jsonBOMObj.getString(PGStructuredATSConstants.KEY_PARENT_ID);
					DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
					String strPhaseName = dobParentObj.getInfo(context, DomainConstants.SELECT_NAME);
					mpAttributeInfoMap.put(PGStructuredATSConstants.ATTRIBUTE_BM_PHASE, strPhaseName);
				}

				StringList slRelSelects = new StringList(
						PGStructuredATSConstants.SELECT_RELATED_PARENT_SUB_FOR_ATS_CONTEXT);
				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strATSContextRelId;
				MapList mlRelatedParentSubInfoList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);

				Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlRelatedParentSubInfoList.get(0);
				String strParentSubId = (String) mpObjInfoMap
						.get(PGStructuredATSConstants.SELECT_RELATED_PARENT_SUB_FOR_ATS_CONTEXT);

				DomainObject dobParentSubObj = DomainObject.newInstance(context, strParentSubId);
				DomainObject dobTargetObj = DomainObject.newInstance(context, strBMId);
				DomainRelationship drATSOperationObjTargets = DomainRelationship.connect(context, dobParentSubObj,
						PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);
				drATSOperationObjTargets.setAttributeValues(context, mpAttributeInfoMap);

			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Method to check whether the selected Balancing Material is also a source
	 * part, if so then send the Phase name of the source.
	 * 
	 * @param context
	 * @param jsonBOMArray
	 * @param iJsonArraySize
	 * @return
	 * @throws FrameworkException 
	 */
	private String checkIfBMExistInAnySource(Context context, JsonArray jsonBOMArray, int iJsonArraySize) throws FrameworkException {
		String strPhaseForBM = "";
		JsonObject jsonFirstObj = jsonBOMArray.getJsonObject(0);
		String strBMId = jsonFirstObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);
		for (int i = 0; i < iJsonArraySize; i++) {
			JsonObject jsonBOMObj = jsonBOMArray.getJsonObject(i);
			String strSourceObjId = jsonBOMObj.getString(PGStructuredATSConstants.KEY_SOURCE_ID);
			if (strBMId.equals(strSourceObjId)) {
				String strParentId = jsonBOMObj.getString(PGStructuredATSConstants.KEY_PARENT_ID);
				DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
				strPhaseForBM = dobParentObj.getInfo(context, DomainConstants.SELECT_NAME);
				break;
			}
		}

		return strPhaseForBM;
	}

	/**
	 * Method to connect the multiple selected source FBOM to common ATS Operation
	 * rel (of SATS object and Parent Sub)
	 * 
	 * @param context
	 * @param strATSOperationRelId
	 * @param jsonSourceArray
	 * @throws Exception 
	 */
	private void connectSourceFBOMIdsToATSOperationRel(Context context, String strATSOperationRelId,
			JsonArray jsonSourceArray) throws Exception {
		int iSourceArraySize = jsonSourceArray.size();
		for (int i = 0; i < iSourceArraySize; i++) {
			JsonObject jsonSourceObj = jsonSourceArray.getJsonObject(i);
			String strInputRelId = jsonSourceObj.getString(PGStructuredATSConstants.KEY_REL_ID);

			Relationship relationship = new Relationship(strInputRelId);
			relationship.open(context);
			String strRelationshipName = relationship.getTypeName();
			relationship.close(context);

			if (PGStructuredATSConstants.RELATIONSHIP_FBOM.equals(strRelationshipName)) {
				connectATSOperationsWithBOM(context, strATSOperationRelId, strInputRelId,
						PGStructuredATSConstants.VALUE_BOM);
			} else if (PGStructuredATSConstants.RELATIONSHIP_FBOM_SUBSTITUTE.equals(strRelationshipName)) {
				connectATSOperationsWithParentSub(context, strATSOperationRelId, strInputRelId,
						PGStructuredATSConstants.VALUE_SUBSTITUTE);
			}

		}
	}

	/**
	 * Method to add Target objects to Parent Sub
	 * 
	 * @param context
	 * @param strParentSubId
	 * @param jsonTargetArray
	 * @throws Exception 
	 */
	private void addTargetObjectsToParentSub(Context context, String strParentSubId, JsonArray jsonTargetArray) throws Exception {
		try {
			DomainObject dobParentSubObj = DomainObject.newInstance(context, strParentSubId);
			int iTargetArraySize = jsonTargetArray.size();
			for (int i = 0; i < iTargetArraySize; i++) {
				JsonObject jsonTargetObj = jsonTargetArray.getJsonObject(i);
				String strTargetId = jsonTargetObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);
				JsonObject jsonRelAttributes = jsonTargetObj.getJsonObject(PGStructuredATSConstants.KEY_REL_ATTRIBUTES);
				Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonRelAttributes);

				DomainObject dobTargetObj = DomainObject.newInstance(context, strTargetId);
				DomainRelationship drATSOperationObjTargets = DomainRelationship.connect(context, dobParentSubObj,
						PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);
				drATSOperationObjTargets.setAttributeValues(context, mpAttributeInfoMap);

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Method to replace FBOM for FOP structure
	 * 
	 * @param context
	 * @param strSATSId
	 * @param jsonInputObj
	 * @param strActionsAttrValue
	 * @throws Exception 
	 */
	private void replaceOperationsForFBOM(Context context, String strSATSId, JsonObject jsonInputObj,
			String strActionsAttrValue) throws Exception {
		try {
			String strInputRelId = jsonInputObj.getString(PGStructuredATSConstants.KEY_REL_ID);
			String strTargetId = jsonInputObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);
			JsonObject jsonRelAttributes = jsonInputObj.getJsonObject(PGStructuredATSConstants.KEY_REL_ATTRIBUTES);
			Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonRelAttributes);
			DomainObject dobSATSObj = DomainObject.newInstance(context, strSATSId);
			String strParentSubId = "";

			if (!mapFBOMParentSubMap.isEmpty() && mapFBOMParentSubMap.containsKey(strInputRelId)) {
				Map<String, String> mapParentSubMap = mapFBOMParentSubMap.get(strInputRelId);
				strParentSubId = mapParentSubMap.get(DomainConstants.SELECT_ID);
			} else {
				strParentSubId = createNewParentSubObj(context);
				DomainObject dobParentSubObj = DomainObject.newInstance(context, strParentSubId);
				DomainRelationship drATSOperationPSObj = DomainRelationship.connect(context, dobSATSObj,
						PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobParentSubObj);
				String strATSOperationRelId = drATSOperationPSObj.getPhysicalId(context);

				if (PGStructuredATSConstants.VALUE_BOM.equals(strActionsAttrValue)) {
					connectATSOperationsWithBOM(context, strATSOperationRelId, strInputRelId, strActionsAttrValue);
				} else {
					connectATSOperationsWithParentSub(context, strATSOperationRelId, strInputRelId,
							strActionsAttrValue);
				}

				Map<String, String> mapParentSubMap = new HashMap<>();
				mapParentSubMap.put(DomainConstants.SELECT_ID, strParentSubId);
				mapParentSubMap.put(PGStructuredATSConstants.KEY_REL_ID, strATSOperationRelId);
				mapFBOMParentSubMap.put(strInputRelId, mapParentSubMap);
			}

			DomainObject dobParentSubObj = DomainObject.newInstance(context, strParentSubId);
			DomainObject dobTargetObj = DomainObject.newInstance(context, strTargetId);
			DomainRelationship drATSOperationObjTargets = DomainRelationship.connect(context, dobParentSubObj,
					PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);
			drATSOperationObjTargets.setAttributeValues(context, mpAttributeInfoMap);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Method to connect ATS Operation rel with Parent Sub object on replace
	 * Substitute operation
	 * 
	 * @param context
	 * @param strATSOperationRelId
	 * @param strInputRelId
	 * @param strActionsAttrValue
	 * @throws Exception 
	 */
	private void connectATSOperationsWithParentSub(Context context, String strATSOperationRelId, String strInputRelId,
			String strActionsAttrValue) throws Exception {
		try {
			StringList slRelSelects = new StringList(DomainConstants.SELECT_TO_ID);
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strInputRelId;
			MapList mlFBOMSubstituteRelInfoList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlFBOMSubstituteRelInfoList.get(0);
			String strFBOMParentSubObjId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);

			RelToRelUtil relTorelUtil = new RelToRelUtil();
			String strATSContextRelId = relTorelUtil.connect(context, PGStructuredATSConstants.REL_PG_ATS_CONTEXT,
					strATSOperationRelId, strFBOMParentSubObjId, false, true);
			DomainRelationship drATSContextObj = new DomainRelationship(strATSContextRelId);
			drATSContextObj.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION,
					strActionsAttrValue);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Method to create Parent Sub object
	 * 
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public String createNewParentSubObj(Context context) throws Exception {
		String strParentSubObjId = "";
		try {
			String strAutoName = FrameworkUtil.autoName(context, TYPE_PARENT_SUB_REGISTRY_NAME,
					POLICY_PARENT_SUB_REGISTRY_NAME);
			DomainObject dobParentSub = DomainObject.newInstance(context);
			dobParentSub.createObject(context, PGStructuredATSConstants.TYPE_PARENT_SUB, strAutoName,
					PGStructuredATSConstants.PARENT_SUB_REVISION, PGStructuredATSConstants.POLICY_PARENT_SUB, null);
			strParentSubObjId = dobParentSub.getObjectId(context);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
		return strParentSubObjId;
	}

	/**
	 * Method to replace EBOM for APP structure
	 * 
	 * @param context
	 * @param strSTASId
	 * @param jsonInputObj
	 * @param strActionsAttrValue
	 * @throws Exception 
	 */
	private void replaceOperationsForEBOM(Context context, String strSATSId, JsonObject jsonInputObj,
			String strActionsAttrValue) throws Exception {
		try {
			String strInputRelId = jsonInputObj.getString(PGStructuredATSConstants.KEY_REL_ID);
			String strTargetId = jsonInputObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);
			JsonObject jsonRelAttributes = jsonInputObj.getJsonObject(PGStructuredATSConstants.KEY_REL_ATTRIBUTES);
			Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonRelAttributes);

			DomainObject dobSATSObj = DomainObject.newInstance(context, strSATSId);
			DomainObject dobTargetObj = DomainObject.newInstance(context, strTargetId);
			DomainRelationship drATSOperationObj = DomainRelationship.connect(context, dobSATSObj,
					PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);
			drATSOperationObj.setAttributeValues(context, mpAttributeInfoMap);
			String strATSOperationRelId = drATSOperationObj.getPhysicalId(context);

			connectATSOperationsWithBOM(context, strATSOperationRelId, strInputRelId, strActionsAttrValue);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Method to replace EBOM for APP structure
	 * 
	 * @param context
	 * @param strSTASId
	 * @param jsonInputObj
	 * @param strActionsAttrValue
	 * @throws Exception 
	 */
	private void replaceOperationsForAlternate(Context context, String strSATSId, JsonObject jsonInputObj,
			String strActionsAttrValue) throws Exception {
		try {
			String strInputRelId = jsonInputObj.getString(PGStructuredATSConstants.KEY_REL_ID);
			String strTargetId = jsonInputObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);
			JsonObject jsonRelAttributes = jsonInputObj.getJsonObject(PGStructuredATSConstants.KEY_REL_ATTRIBUTES);
			Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonRelAttributes);

			DomainObject dobSATSObj = DomainObject.newInstance(context, strSATSId);
			DomainObject dobTargetObj = DomainObject.newInstance(context, strTargetId);
			DomainRelationship drATSOperationObj = DomainRelationship.connect(context, dobSATSObj,
					PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);
			drATSOperationObj.setAttributeValues(context, mpAttributeInfoMap);
			String strATSOperationRelId = drATSOperationObj.getPhysicalId(context);

			connectATSOperationsWithAlternate(context, strATSOperationRelId, strInputRelId, strActionsAttrValue);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Connect 'ATS Operation' connection to 'EBOM|FBOM' connections
	 * 
	 * @param context
	 * @param strActionsAttrValue
	 * @param slATSOperationIds
	 * @param strInputRelId
	 * @throws Exception 
	 */
	private void connectATSOperationsWithBOM(Context context, String strATSOperationRelId, String strInputRelId,
			String strActionsAttrValue) throws Exception {
		try {
			RelToRelUtil relTorelUtil = new RelToRelUtil();
			String strATSContextRelId = relTorelUtil.connect(context, PGStructuredATSConstants.REL_PG_ATS_CONTEXT,
					strATSOperationRelId, strInputRelId, false, false);
			DomainRelationship drATSContextObj = new DomainRelationship(strATSContextRelId);
			drATSContextObj.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION,
					strActionsAttrValue);
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Connect 'ATS Operation' connection to 'Alternate' connections
	 * 
	 * @param context
	 * @param strActionsAttrValue
	 * @param slATSOperationIds
	 * @param strInputRelId
	 * @throws Exception 
	 */
	private void connectATSOperationsWithAlternate(Context context, String strATSOperationRelId, String strInputRelId,
			String strActionsAttrValue) throws Exception {
		try {
			RelToRelUtil relTorelUtil = new RelToRelUtil();
			String strATSContextRelId = relTorelUtil.connect(context, PGStructuredATSConstants.REL_PG_ATS_CONTEXT,
					strATSOperationRelId, strInputRelId, false, false);
			DomainRelationship drATSContextObj = new DomainRelationship(strATSContextRelId);
			drATSContextObj.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION,
					strActionsAttrValue);
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			throw e;
		}
	}

	/**
	 * Method to get attributes Map from Json object
	 * 
	 * @param jsonRelAttributes
	 * @return
	 */
	private Map<String, String> getAttributeMapFromJson(JsonObject jsonRelAttributes) {
		Map<String, String> mpAttributeInfoMap = new HashMap<>();

		jsonRelAttributes.forEach((strKey, strValue) -> {
			mpAttributeInfoMap.put(strKey, strValue.toString());
		});

		return mpAttributeInfoMap;
	}

	/**
	 * Method to Map with rel name and actions mapping
	 * 
	 * @return
	 */
	private Map<String, String> getActionsValueMap() {
		Map<String, String> mpActionsVaueMap = new HashMap<>();
		mpActionsVaueMap.put(DomainConstants.RELATIONSHIP_EBOM, PGStructuredATSConstants.VALUE_BOM);
		mpActionsVaueMap.put(PGStructuredATSConstants.RELATIONSHIP_FBOM, PGStructuredATSConstants.VALUE_BOM);
		mpActionsVaueMap.put(PGStructuredATSConstants.RELATIONSHIP_EBOM_SUBSTITUTE,
				PGStructuredATSConstants.VALUE_SUBSTITUTE);
		mpActionsVaueMap.put(PGStructuredATSConstants.RELATIONSHIP_FBOM_SUBSTITUTE,
				PGStructuredATSConstants.VALUE_SUBSTITUTE);
		mpActionsVaueMap.put(PGStructuredATSConstants.RELATIONSHIP_ALTERNATE,
				PGStructuredATSConstants.VALUE_ALTERNATE);
		return mpActionsVaueMap;
	}
	/**Method to add balancing Material to SATS and phase
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	String connectBalancingMaterial(Context context, String strJsonInput)
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			ContextUtil.startTransaction(context, true);
			RelToRelUtil relTorelUtil = new RelToRelUtil();
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			JsonArray jsonBOMArray = jsonInputData.getJsonArray(PGStructuredATSConstants.KEY_DATA);
			String strATSId = jsonInputData.getString("id");
			int iJsonArraySize = jsonBOMArray.size();

			DomainObject dobSATSObj = DomainObject.newInstance(context, strATSId);
			for(int i=0; i <iJsonArraySize ; i++)
			{
				JsonObject jsonBOMObj = jsonBOMArray.getJsonObject(i);
				String strBMId = jsonBOMObj.getString(PGStructuredATSConstants.KEY_TARGET_ID);
				String strPhaseId = jsonBOMObj.getString(PGStructuredATSConstants.PREFIX_CTX_PHASE+"id");
				JsonObject jsonBMAttributes = jsonBOMObj.getJsonObject(PGStructuredATSConstants.KEY_REL_ATTRIBUTES);
				Map<String, String> mpAttributeInfoMap = getAttributeMapFromJson(jsonBMAttributes);
				mpAttributeInfoMap.put(PGStructuredATSConstants.ATTRIBUTE_BALANCING_MATERIAL,
						PGStructuredATSConstants.VALUE_TRUE);
				
				DomainObject dobTargetObj = DomainObject.newInstance(context, strBMId);
				DomainRelationship drATSOperationObj = DomainRelationship.connect(context, dobSATSObj,
						PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);
				drATSOperationObj.setAttributeValues(context, mpAttributeInfoMap);
				
				String strATSOperationRelId = drATSOperationObj.getPhysicalId(context);
				String strATSContextRelId = relTorelUtil.connect(context, PGStructuredATSConstants.REL_PG_ATS_CONTEXT,
						strATSOperationRelId, strPhaseId, false, true);
			}

			ContextUtil.commitTransaction(context);

			jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			return jsonReturnObj.build().toString();
		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_REPLACE, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonReturnObj.build().toString();
		}
	}
}