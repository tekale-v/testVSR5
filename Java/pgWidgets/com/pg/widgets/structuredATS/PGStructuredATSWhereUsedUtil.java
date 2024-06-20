package com.pg.widgets.structuredats;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGStructuredATSWhereUsedUtil {

	StringList slObjectSelects = new StringList();
	String strLanguage = "en";
	int iObjectSelectsSize = 0;
	int iObjectIdsSize = 0;

	private static final Logger logger = Logger.getLogger(PGStructuredATSWhereUsedUtil.class.getName());
	
	//Methods (getWhereUsedObjects) related to Where Used fetch use case : Start
	/**
	 * Method to get the where used object for input RMPs based on FOP or Plant
	 * filters
	 * 
	 * @param context
	 * @param argsMap
	 * @return
	 * @throws Exception 
	 */
	String getWhereUsedObjects(Context context, Map<String, String> argsMap) throws Exception {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		try {
			String strObjSelects = argsMap.get(PGStructuredATSConstants.KEY_OBJ_SELECTS);
			String strInputConsumingFormulas = argsMap.get(PGStructuredATSConstants.KEY_CONSUMING_FORMULA);
			String strRMPIds = argsMap.get(PGStructuredATSConstants.KEY_SEARCH_MATERIAL);
			String strPlantIds = argsMap.get(PGStructuredATSConstants.KEY_SEARCH_PLANT);
			String strIsAuthorizedToProduce = argsMap.get(PGStructuredATSConstants.KEY_AUTHORIZEDTOPRODUCE);
			String strIsAuthorizedToUse = argsMap.get(PGStructuredATSConstants.KEY_AUTHORIZEDTOUSE);
			String strAlternates = argsMap.get(PGStructuredATSConstants.KEY_ALTERNATES);

			slObjectSelects = new StringList();
			iObjectSelectsSize = 0;
			iObjectIdsSize = 0;
			strLanguage = context.getSession().getLanguage();
			getObjectSelectList(strObjSelects);
			iObjectSelectsSize = slObjectSelects.size();
			
			Map<String, MapList> mapWhereUsedObjMap = new HashMap<>();
			StringList slProcessedIdList = new StringList();
			StringList slRelatedFOPList = new StringList();
			StringList slInputIdList = new StringList();
			if (UIUtil.isNotNullAndNotEmpty(strRMPIds)) {
				slInputIdList = StringUtil.split(strRMPIds, ",");
			}

			slInputIdList = filterInputMaterialsForPlants(context, slInputIdList, strPlantIds);

			StringList slObjectIds = getObjectIdsForPhysicalIds(context, slInputIdList);
			iObjectIdsSize = slObjectIds.size();

			StringList slValidFOPList = new StringList();
			StringList slValidAPPList = new StringList();
			StringList slFilteredFOPList = new StringList();
			int iAPPFOPFilterIdsSize = 0;
			if (UIUtil.isNotNullAndNotEmpty(strInputConsumingFormulas)) {
				StringList slInputFormulaIdList = StringUtil.split(strInputConsumingFormulas, ",");
				StringList slAPPFOPFilterIdList = getObjectIdsForPhysicalIds(context, slInputFormulaIdList);
				iAPPFOPFilterIdsSize = slAPPFOPFilterIdList.size();
				
				Map<String,StringList> mpFilteredIdsMap = filterInputConsumingFormulas(context, slObjectIds, slAPPFOPFilterIdList);
				slValidFOPList = mpFilteredIdsMap.get(PGStructuredATSConstants.TYPE_FORMULATION_PART);
				slValidAPPList = mpFilteredIdsMap.get(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART);
			}
			
			if(iAPPFOPFilterIdsSize == 0) {
				updateWhereUsedObjListWithrelatedAPPs(context, slObjectIds, slProcessedIdList, mapWhereUsedObjMap);
				updateWhereUsedSubstitutesForAPPs(context, slObjectIds, mapWhereUsedObjMap);
			} else {
				updateWhereUsedObjListWithInputAPPs(context, slObjectIds, slValidAPPList, mapWhereUsedObjMap);
			}

			Map<String, MapList> mapWhereUsedAPPFOPMap = filterWhereUsedAPPsWithPlantInfo(context, strPlantIds,
					strIsAuthorizedToProduce, strIsAuthorizedToUse, mapWhereUsedObjMap);

			if(iAPPFOPFilterIdsSize == 0) {
				updateWhereUsedObjListWithrelatedFOPs(context, slObjectIds, slProcessedIdList, slRelatedFOPList);
				slFilteredFOPList.addAll(slRelatedFOPList);
			} else {
				slFilteredFOPList.addAll(slValidFOPList);
			}

			StringList slFilteredFOPIdList = filterObjectsWithPlantInfo(context, strPlantIds, strIsAuthorizedToProduce,
					strIsAuthorizedToUse, slFilteredFOPList);
				
			updateWhereUsedMapWithFOPStructure(context, slObjectIds, slFilteredFOPIdList, mapWhereUsedAPPFOPMap);
	
			// Added by DSM 2022x.06 to remove alternates 
			/*
			if (PGStructuredATSConstants.VALUE_TRUE.equalsIgnoreCase(strAlternates) && (iAPPFOPFilterIdsSize == 0) && 1==0) 
			{
				updateWhereUsedObjListWithAlternates(context, slObjectIds, slProcessedIdList, mapWhereUsedAPPFOPMap);
			} */

			updateJsonArrayWithWhereUsedObjDetails(mapWhereUsedAPPFOPMap, jsonArrObjInfo);
	
			jsonReturnObj.add(PGStructuredATSConstants.KEY_OUTPUT, jsonArrObjInfo);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			throw e;
		}
		
		return jsonReturnObj.build().toString();
	}

	/**
	 * Method to filter input RMPs based on plants as per new requirement for CW6
	 * @param context
	 * @param slInputIdList
	 * @param strPlantIds
	 * @return
	 * @throws FrameworkException 
	 */
	private StringList filterInputMaterialsForPlants(Context context, StringList slInputIdList, String strPlantIds) throws FrameworkException {
		StringList slFilteredIdsList = new StringList();

		if (UIUtil.isNotNullAndNotEmpty(strPlantIds)) {
			StringList slPlantIdList = StringUtil.split(strPlantIds, ",");

			Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_PLANT);
			Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY);

			StringList slObjSelects = new StringList(DomainConstants.SELECT_ID);
			slObjSelects.add(DomainConstants.SELECT_PHYSICAL_ID);

			StringList slRelSelects = new StringList();
			slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);

			boolean isContextPushed = false;
			try {
				ContextUtil.pushContext(context);
				isContextPushed = true;
				int iRelatedObjListSize = slInputIdList.size();
				for (int i = 0; i < iRelatedObjListSize; i++) {
					String strObjectId = slInputIdList.get(i);
					DomainObject dobInputMaterialObj = DomainObject.newInstance(context, strObjectId);
					MapList mlRelatedPlants = dobInputMaterialObj.getRelatedObjects(context, // the eMatrix Context object
							relPattern.getPattern(), // Relationship pattern
							typePattern.getPattern(), // Type pattern
							slObjSelects, // Object selects
							slRelSelects, // Relationship selects
							true, // get From relationships
							false, // get To relationships
							(short) 1, // the number of levels to expand, 0 equals expand all.
							null, // Object where clause
							null, // Relationship where clause
							0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
								// data available

					checkForExistingPlantInfoForInputMaterials(mlRelatedPlants, slPlantIdList, slFilteredIdsList, strObjectId);

				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
				}
			}
		} else {
			return slInputIdList;
		}

		return slFilteredIdsList;
	}

	/**
	 * Method to filter input RMPs based on plants as per new requirement for CW6
	 * @param mlRelatedPlants
	 * @param slPlantIdList
	 * @param slFilteredIdsList
	 * @param strObjectId
	 */
	private void checkForExistingPlantInfoForInputMaterials(MapList mlRelatedPlants, StringList slPlantIdList,
			StringList slFilteredIdsList, String strObjectId) {
		int iRelatedPlantsSize = mlRelatedPlants.size();
		for (int j = 0; j < iRelatedPlantsSize; j++) {
			Map<?, ?> mapPlantInfo = (Map<?, ?>) mlRelatedPlants.get(j);
			String strPlantId = (String) mapPlantInfo.get(DomainConstants.SELECT_ID);
			String strPlantPhysicalId = (String) mapPlantInfo.get(DomainConstants.SELECT_PHYSICAL_ID);
			String strAttrAuthorized = (String) mapPlantInfo
					.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
			if ((slPlantIdList.contains(strPlantId) || slPlantIdList.contains(strPlantPhysicalId))
					&& (PGStructuredATSConstants.VALUE_TRUE.equalsIgnoreCase(strAttrAuthorized))) {
				slFilteredIdsList.add(strObjectId);
				break;
			}
		}
		
	}

	//Methods to expand input Consuming Formulas (APPs and FOPs) : Start
	/**
	 * Method to filter the input consuming formulas (Input APPs and FOPs)
	 * 
	 * @param context
	 * @param slObjectIds
	 * @throws FrameworkException
	 */
	private Map<String, StringList> filterInputConsumingFormulas(Context context, StringList slObjectIds,
			StringList slAPPFOPFilterIdList) throws FrameworkException {

		Map<String, StringList> mpFilteredIdsMap = new HashMap<>();
		StringList slAPPList = new StringList();
		StringList slFOPList = new StringList();
		StringList slFilteredFOPList = new StringList();
		StringList slFilteredAPPList = new StringList();

		StringList slObjSelects = new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		slObjSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);

		String[] strOIDArray = slAPPFOPFilterIdList.toStringArray();

		boolean isContextPushed = false;
		try {
			ContextUtil.pushContext(context);
			isContextPushed = true;

			MapList mlContextFormulaInfoList = DomainObject.getInfo(context, strOIDArray, slObjSelects);
			int iListSize = mlContextFormulaInfoList.size();
			for (int i = 0; i < iListSize; i++) {
				Map<?, ?> mpCtxFormulaInfoMap = (Map<?, ?>) mlContextFormulaInfoList.get(i);
				String strCurrent = (String) mpCtxFormulaInfoMap.get(DomainConstants.SELECT_CURRENT);
				String strAttrReleasePhase = (String) mpCtxFormulaInfoMap
						.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
				String strAttrAuthoringApplication = (String) mpCtxFormulaInfoMap
						.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);
				
				if ((DomainConstants.STATE_PART_PRELIMINARY.equals(strCurrent)
						|| DomainConstants.STATE_PART_RELEASE.equals(strCurrent))
						&& PGStructuredATSConstants.RANGE_PRODUCTION.equals(strAttrReleasePhase)) {
					String strType = (String) mpCtxFormulaInfoMap.get(DomainConstants.SELECT_TYPE);
					String strInputId = (String) mpCtxFormulaInfoMap.get(DomainConstants.SELECT_ID);
					if (PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strType)
							&& !PGStructuredATSConstants.VALUE_LPD_APOLLO.equals(strAttrAuthoringApplication)) {
						slAPPList.add(strInputId);
					} else if (PGStructuredATSConstants.TYPE_FORMULATION_PART.equals(strType)) {
						slFOPList.add(strInputId);
					}
				}

			}

			if (!slFOPList.isEmpty()) {
				checkForInputIdsForFOPs(context, slFOPList, slObjectIds, slFilteredFOPList);
			}

			if (!slAPPList.isEmpty()) {
				checkForInputIdsForAPPs(context, slAPPList, slObjectIds, slFilteredAPPList);
			}

			mpFilteredIdsMap.put(PGStructuredATSConstants.TYPE_FORMULATION_PART, slFilteredFOPList);
			mpFilteredIdsMap.put(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART, slFilteredAPPList);

		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}

		return mpFilteredIdsMap;

	}
	
	/**
	 * Method to filter the input consuming formulas (APPs)
	 * @param context
	 * @param slAPPList
	 * @param slObjectIds
	 * @param slFilteredAPPList
	 * @throws FrameworkException 
	 */
	private void checkForInputIdsForAPPs(Context context, StringList slAPPList, StringList slObjectIds,
			StringList slFilteredAPPList) throws FrameworkException {
		int iListSize = slAPPList.size();
		for(int i=0;i<iListSize;i++) {
			String strAPPId = slAPPList.get(i);
			String[] strAPPIdArray = new String[1];
			strAPPIdArray[0] = strAPPId;
			
			StringList slAPPObjSelects = new StringList();
			slAPPObjSelects.add(PGStructuredATSConstants.SELECT_RELATED_EBOM_OBJ_IDS);
			slAPPObjSelects.add(PGStructuredATSConstants.SELECT_RELATED_EBOM_SUB_OBJ_IDS);
			
			MapList mlAPPInfoList = DomainObject.getInfo(context, strAPPIdArray, slAPPObjSelects);

			if(mlAPPInfoList != null && !mlAPPInfoList.isEmpty()) {
				Map<?,?> mpAPPObjInfoMap = (Map<?, ?>) mlAPPInfoList.get(0);
				boolean isInputIdRelatedToAPP = validateInputIdsForAPP(mpAPPObjInfoMap, slObjectIds);
				if(isInputIdRelatedToAPP) {
					slFilteredAPPList.add(strAPPId);
				}
			}
			
		}
		
	}

	/**
	 * Method to filter the input consuming formulas for APPs
	 * @param mpAPPObjInfoMap
	 * @param slObjectIds
	 * @return
	 */
	private boolean validateInputIdsForAPP(Map<?, ?> mpAPPObjInfoMap, StringList slObjectIds) {
		Object objEBOMObjIds = mpAPPObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_EBOM_OBJ_IDS);
		StringList slEOMObjIdList = getStringListFromObject(objEBOMObjIds);
		if(slEOMObjIdList != null && !slEOMObjIdList.isEmpty()) {
			int iEBOMListSize = slEOMObjIdList.size();
			for(int k=0;k<iEBOMListSize;k++) {
				String strEBOMObjId = slEOMObjIdList.get(k);
				if(slObjectIds.contains(strEBOMObjId)) {
					return true;
				}
			}
		}
		
		Object objEBOMSubObjIds = mpAPPObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_EBOM_SUB_OBJ_IDS);
		StringList slEOMSubObjIdList = getStringListFromObject(objEBOMSubObjIds);
		if(slEOMSubObjIdList != null && !slEOMSubObjIdList.isEmpty()) {
			int iEBOMSubListSize = slEOMSubObjIdList.size();
			for(int j=0;j<iEBOMSubListSize;j++) {
				String strEBOMSubObjId = slEOMSubObjIdList.get(j);
				if(slObjectIds.contains(strEBOMSubObjId)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method to filter the input consuming formulas
	 * @param context
	 * @param slFOPList
	 * @param slObjectIds
	 * @param slFilteredFOPList
	 * @throws FrameworkException
	 */
	private void checkForInputIdsForFOPs(Context context, StringList slFOPList, StringList slObjectIds, StringList slFilteredFOPList) throws FrameworkException {
		int iListSize = slFOPList.size();
		for(int i=0;i<iListSize;i++) {
			String strFOPId = slFOPList.get(i);
			String[] strFOPIdArray = new String[1];
			strFOPIdArray[0] = strFOPId;
			
			StringList slPhaseIdSelect = new StringList(PGStructuredATSConstants.SELECT_RELATED_PHASE_FOR_FOP);
			
			MapList mlFOPInfoList = DomainObject.getInfo(context, strFOPIdArray, slPhaseIdSelect);
			if(mlFOPInfoList != null && !mlFOPInfoList.isEmpty()) {
				Map<?,?> mpPhaseIdList = (Map<?, ?>) mlFOPInfoList.get(0);
				Object objPhaseIds = mpPhaseIdList.get(PGStructuredATSConstants.SELECT_RELATED_PHASE_FOR_FOP);
				StringList slPhaseIdList = getStringListFromObject(objPhaseIds);
				if(slPhaseIdList != null && !slPhaseIdList.isEmpty()) {
					boolean isInputIdRelatedToFOP = checkInputIdsFromPhase(context, slPhaseIdList, slObjectIds);
					if(isInputIdRelatedToFOP) {
						slFilteredFOPList.add(strFOPId);
					}
				}
			}
			
		}
	}
	
	/**
	 * Method to get the StringList from object
	 * @param objInputValue
	 * @return
	 */
	public StringList getStringListFromObject(Object objInputValue) {
		StringList slObjValuesList = null;
		if(objInputValue == null) {
			return slObjValuesList;
		} else {
			if(objInputValue instanceof StringList) {
				slObjValuesList = (StringList)objInputValue;
			} else {
				String strObjValue = (String)objInputValue;
				slObjValuesList = StringUtil.split(strObjValue, SelectConstants.cSelectDelimiter);
			}
		}
		return slObjValuesList;
	}

	/**
	 * Method to filter the input consuming formulas
	 * @param context
	 * @param slPhaseIdList
	 * @param slObjectIds
	 * @return
	 * @throws FrameworkException
	 */
	private boolean checkInputIdsFromPhase(Context context, StringList slPhaseIdList, StringList slObjectIds) throws FrameworkException {
		int iListSize = slPhaseIdList.size();
		for(int i=0;i<iListSize;i++) {
			String strPhaseId = slPhaseIdList.get(i);
			String[] strPhaseIdArray = new String[1];
			strPhaseIdArray[0] = strPhaseId;
			
			StringList slPhaseIdSelect = new StringList();
			slPhaseIdSelect.add(PGStructuredATSConstants.SELECT_RELATED_FBOM_OBJ_ID);
			slPhaseIdSelect.add(PGStructuredATSConstants.SELECT_RELATED_FBOM_SUBSTITUTES_IDS);
			
			MapList mlPhaseInfoList = DomainObject.getInfo(context, strPhaseIdArray, slPhaseIdSelect);

			if(mlPhaseInfoList != null && !mlPhaseInfoList.isEmpty()) {
				Map<?,?> mpRelatedObjList = (Map<?, ?>) mlPhaseInfoList.get(0);
				
				Object objFBOMObjId = mpRelatedObjList.get(PGStructuredATSConstants.SELECT_RELATED_FBOM_OBJ_ID);
				StringList slFBOMObjIdList = getStringListFromObject(objFBOMObjId);
				if(slFBOMObjIdList != null && !slFBOMObjIdList.isEmpty()) {
					int iFBOMListSize = slFBOMObjIdList.size();
					for(int j=0;j<iFBOMListSize;j++) {
						String strFBOMObjId = slFBOMObjIdList.get(j);
						if(slObjectIds.contains(strFBOMObjId)) {
							return true;
						}
					}
				}
				
				Object objFBOMSubObjId = mpRelatedObjList.get(PGStructuredATSConstants.SELECT_RELATED_FBOM_SUBSTITUTES_IDS);
				StringList slFBOMSubObjIdList = getStringListFromObject(objFBOMSubObjId);
				if(slFBOMSubObjIdList != null && !slFBOMSubObjIdList.isEmpty()) {
					int iFBOMSubListSize = slFBOMSubObjIdList.size();
					for(int k=0;k<iFBOMSubListSize;k++) {
						String strFBOMSubObjId = slFBOMSubObjIdList.get(k);
						if(slObjectIds.contains(strFBOMSubObjId)) {
							return true;
						}
					}
				}
			}
			
			
		}
		
		return false;
	}
	
	/**
	 * Method to expand input APPs to update mapWhereUsedObjMap
	 * @param context
	 * @param slObjectIds
	 * @param slValidAPPList
	 * @param mapWhereUsedObjMap
	 * @throws FrameworkException 
	 */
	private void updateWhereUsedObjListWithInputAPPs(Context context, StringList slObjectIds, StringList slValidAPPList,
			Map<String, MapList> mapWhereUsedObjMap) throws FrameworkException {
		int iAPPListSize = slValidAPPList.size();
		for(int i=0;i<iAPPListSize;i++) {
			String strAPPId = slValidAPPList.get(i);
			MapList mlAPPList = new MapList();
			
			DomainObject dobAPPObj = DomainObject.newInstance(context, strAPPId);
			Map<?, ?> mpAPPObjInfoMap = dobAPPObj.getInfo(context, slObjectSelects);
			
			Map<String, String> objParentMap = updateMapWithParentChildObjSelects(context, mpAPPObjInfoMap);
			objParentMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strAPPId);
			mlAPPList.add(objParentMap);
			
			expandInputAPPsToGetRelatedObjects(context, slObjectIds, dobAPPObj, mlAPPList, strAPPId);
			
			mapWhereUsedObjMap.put(strAPPId, mlAPPList);
		}
		
	}
	
	/**
	 * Method to expand input APPs to get EBOM and Substitutes data
	 * 
	 * @param context
	 * @param slObjectIds
	 * @param dobAPPObj
	 * @param mlAPPList
	 * @param strAPPId
	 * @throws FrameworkException
	 */
	private void expandInputAPPsToGetRelatedObjects(Context context, StringList slObjectIds, DomainObject dobAPPObj,
			MapList mlAPPList, String strAPPId) throws FrameworkException {

		MapList mlRelatedObjects = dobAPPObj.getRelatedObjects(context, // the eMatrix Context object
				DomainConstants.RELATIONSHIP_EBOM, // Relationship pattern
				"*", // Type pattern
				new StringList(DomainConstants.SELECT_ID), // Object selects
				new StringList(PGStructuredATSConstants.SELECT_RELATED_EBOM_SUB_OBJ_IDS_FROM_APP), // Relationship
																									// selects
				false, // get From relationships
				true, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				null, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available

		int iListSize = mlRelatedObjects.size();
		for (int i = 0; i < iListSize; i++) {
			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlRelatedObjects.get(i);
			
			String strRelatedObjId = (String) mpObjInfoMap.get(DomainConstants.SELECT_ID);
			if (slObjectIds.contains(strRelatedObjId)) {
				DomainObject dobInputObj = DomainObject.newInstance(context, strRelatedObjId);
				Map<?, ?> mpInputObjInfo = dobInputObj.getInfo(context, slObjectSelects);
				Map<String, String> objChildMap = updateMapWithParentChildObjSelects(context, mpInputObjInfo);
				objChildMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strAPPId + "," + strRelatedObjId);
				objChildMap.put(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.VALUE_BOM);
				mlAPPList.add(objChildMap);
			}
			
			Object objEBOMSubIds = mpObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_EBOM_SUB_OBJ_IDS_FROM_APP);
			StringList slEBOMSubIdList = getStringListFromObject(objEBOMSubIds);
			if(slEBOMSubIdList != null && !slEBOMSubIdList.isEmpty()) {
				updateWhereUsedObjListWithSubstitues(context, slEBOMSubIdList, slObjectIds, strRelatedObjId, mlAPPList, strAPPId);
			}
			
		}

	}
	
	/**
	 * Method to update Substitutes information
	 * 
	 * @param context
	 * @param slEBOMSubIdList
	 * @param slObjectIds
	 * @param strRelatedObjId
	 * @param mlAPPList
	 * @param strAPPId
	 * @throws FrameworkException 
	 */
	private void updateWhereUsedObjListWithSubstitues(Context context, StringList slEBOMSubIdList,
			StringList slObjectIds, String strRelatedObjId, MapList mlAPPList, String strAPPId) throws FrameworkException {
		int iListSize = slEBOMSubIdList.size();
		for(int i=0;i<iListSize;i++) {
			String strEBOMSubId = slEBOMSubIdList.get(i);
			if(slObjectIds.contains(strEBOMSubId)) {
				DomainObject dobSubObj = DomainObject.newInstance(context, strEBOMSubId);
				Map<?, ?> mpSubInfoList = dobSubObj.getInfo(context, slObjectSelects);
				
				Map<String, String> objChildMap = updateMapWithParentChildObjSelects(context, mpSubInfoList);
				objChildMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strAPPId + "," + strEBOMSubId);
				objChildMap.put(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.VALUE_SUBSTITUTE);
				objChildMap.put(PGStructuredATSConstants.KEY_PRIMARY, strRelatedObjId);
				mlAPPList.add(objChildMap);
			}
		}
	}
	//Methods to expand input Consuming Formulas (APPs and FOPs) : End

	/**
	 * Method to get the Substitutes for APPs
	 * 
	 * @param context
	 * @param slObjectIds
	 * @param mapWhereUsedObjMap
	 */
	private void updateWhereUsedSubstitutesForAPPs(Context context, StringList slObjectIds,
			Map<String, MapList> mapWhereUsedObjMap) {
		try {
			StringList slFromObjSelects = new StringList();
			slFromObjSelects.add(DomainConstants.SELECT_TYPE);
			slFromObjSelects.add(DomainConstants.SELECT_CURRENT);
			slFromObjSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			slFromObjSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);

			StringList slRelSelects = new StringList(2);
			slRelSelects.add(DomainConstants.SELECT_FROM_ID);
			slRelSelects.add(DomainConstants.SELECT_TO_ID);

			for (int i = 0; i < iObjectIdsSize; i++) {
				String strInputObjId = slObjectIds.get(i);
				String[] strOIDArray = new String[1];
				strOIDArray[0] = strInputObjId;
				StringList slObjSelects = new StringList(PGStructuredATSConstants.SELECT_EBOM_REL_ID);

				MapList mlEBOMRelIdList = DomainObject.getInfo(context, strOIDArray, slObjSelects);

				if (!mlEBOMRelIdList.isEmpty()) {
					Map<?, ?> mpObjRelIdMap = (Map<?, ?>) mlEBOMRelIdList.get(0);
					if (mpObjRelIdMap != null && !mpObjRelIdMap.isEmpty()) {
						String strEBOMIds = (String) mpObjRelIdMap.get(PGStructuredATSConstants.SELECT_EBOM_REL_ID);
						StringList slEBOMIdsList = StringUtil.split(strEBOMIds, SelectConstants.cSelectDelimiter);

						updateSubstituesInfo(context, slEBOMIdsList, mapWhereUsedObjMap, slFromObjSelects,
								strInputObjId, slRelSelects);
					}
				}

			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}

	}

	/**
	 * Method to get the Substitutes for APPs
	 * 
	 * @param context
	 * @param slEBOMIdsList
	 * @param mapWhereUsedObjMap
	 * @param slFromObjSelects
	 * @param strInputObjId
	 * @param slRelSelects
	 */
	private void updateSubstituesInfo(Context context, StringList slEBOMIdsList,
			Map<String, MapList> mapWhereUsedObjMap, StringList slFromObjSelects, String strInputObjId,
			StringList slRelSelects) {
		try {
			Map<String, String> mpParentPrimaryMap = new HashMap<>();
			int iRelIdlistSize = slEBOMIdsList.size();
			for (int i = 0; i < iRelIdlistSize; i++) {
				String strEBOMRelId = slEBOMIdsList.get(i);
				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strEBOMRelId;

				MapList mlEOMRelInfoList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
				Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlEOMRelInfoList.get(0);
				String strFromId = (String) mpObjInfoMap.get(DomainConstants.SELECT_FROM_ID);
				String strToId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);

				DomainObject dobFromObj = DomainObject.newInstance(context, strFromId);
				Map<?, ?> mapObjInfo = dobFromObj.getInfo(context, slFromObjSelects);
				boolean isValidAPPObj = checkForValidStateAndPhaseForAPP(context, strFromId, mapObjInfo);
				if (isValidAPPObj) {
					updateSubstituesInfoForValidAPPs(strFromId, strToId, mpParentPrimaryMap);
				}

			}

			if (!mpParentPrimaryMap.isEmpty()) {
				updateWhereUsedMapWithSubstituteInfo(context, strInputObjId, mpParentPrimaryMap, mapWhereUsedObjMap);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to filter the parent APP from consuming formula filter
	 * 
	 * @param strFromId
	 * @param strToId
	 * @param mpParentPrimaryMap
	 */
	private void updateSubstituesInfoForValidAPPs(String strFromId, String strToId,
			Map<String, String> mpParentPrimaryMap) {
		try {
			if (mpParentPrimaryMap.containsKey(strFromId)) {
				String strPrimaryObj = mpParentPrimaryMap.get(strFromId);
				mpParentPrimaryMap.put(strFromId, strPrimaryObj + "," + strToId);
			} else {
				mpParentPrimaryMap.put(strFromId, strToId);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to get the Substitutes for APPs
	 * 
	 * @param context
	 * @param strInputObjId
	 * @param mpParentPrimaryMap
	 * @param mapWhereUsedObjMap
	 */
	private void updateWhereUsedMapWithSubstituteInfo(Context context, String strInputObjId,
			Map<String, String> mpParentPrimaryMap, Map<String, MapList> mapWhereUsedObjMap) {
		try {
			for (Map.Entry<String, String> entry : mpParentPrimaryMap.entrySet()) {
				String strParentId = entry.getKey();
				String strPrimaryIds = entry.getValue();

				DomainObject dobInputObj = DomainObject.newInstance(context, strInputObjId);
				Map<?, ?> mpInputObjInfo = dobInputObj.getInfo(context, slObjectSelects);
				Map<String, String> objChildMap = updateMapWithParentChildObjSelects(context, mpInputObjInfo);
				objChildMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId + "," + strInputObjId);
				objChildMap.put(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.VALUE_SUBSTITUTE);
				objChildMap.put(PGStructuredATSConstants.KEY_PRIMARY, strPrimaryIds);

				if (mapWhereUsedObjMap.containsKey(strParentId)) {
					MapList mlExistingInfoList = mapWhereUsedObjMap.get(strParentId);
					mlExistingInfoList.add(objChildMap);

					mapWhereUsedObjMap.put(strParentId, mlExistingInfoList);
				} else {
					DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
					Map<?, ?> mapObjInfo = dobParentObj.getInfo(context, slObjectSelects);

					MapList mlAPPList = new MapList();
					Map<String, String> objParentMap = updateMapWithParentChildObjSelects(context, mapObjInfo);
					objParentMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId);
					mlAPPList.add(objParentMap);

					mlAPPList.add(objChildMap);

					mapWhereUsedObjMap.put(strParentId, mlAPPList);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to convert all input ids to object ids
	 * 
	 * @param context
	 * @param slInputIdList
	 * @return
	 */
	private StringList getObjectIdsForPhysicalIds(Context context, StringList slInputIdList) {
		StringList slObjectIds = new StringList();
		try {
			StringList slObjSelects = new StringList(DomainConstants.SELECT_ID);
			String[] strOIDArray = slInputIdList.toStringArray();

			MapList mpObjIdList = DomainObject.getInfo(context, strOIDArray, slObjSelects);
			int iObjListSize = mpObjIdList.size();
			for (int i = 0; i < iObjListSize; i++) {
				Map<?, ?> mpObjIdsMap = (Map<?, ?>) mpObjIdList.get(i);
				String strObjectId = (String) mpObjIdsMap.get(DomainConstants.SELECT_ID);
				if (!slObjectIds.contains(strObjectId)) {
					slObjectIds.add(strObjectId);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
		return slObjectIds;
	}

	/**
	 * Method to Update final Json array with where used object details
	 * 
	 * @param mapWhereUsedAPPFOPMap
	 * @param jsonArrObjInfo
	 */
	private void updateJsonArrayWithWhereUsedObjDetails(Map<String, MapList> mapWhereUsedAPPFOPMap,
			JsonArrayBuilder jsonArrObjInfo) {
		for (Map.Entry<String, MapList> entry : mapWhereUsedAPPFOPMap.entrySet()) {
			MapList mlObjInfoList = entry.getValue();
			int iSize = mlObjInfoList.size();
			for (int i = 0; i < iSize; i++) {
				Map<?, ?> objInfoMap = (Map<?, ?>) mlObjInfoList.get(i);
				JsonObjectBuilder jsonObjInfo = getProcessJsonObject(objInfoMap);
				jsonArrObjInfo.add(jsonObjInfo);
			}
		}

	}

	/**
	 * Method to process the Map and return the json object
	 * 
	 * @param objInfoMap
	 * @return
	 */
	public JsonObjectBuilder getProcessJsonObject(Map<?, ?> objInfoMap) {
		JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
		for (Map.Entry<?, ?> entry : objInfoMap.entrySet()) {
			String strKey = (String) entry.getKey();
			String strValue = (String) entry.getValue();

			if (strValue == null) {
				strValue = PGStructuredATSConstants.STRING_NULL;
			}
			
			if (PGStructuredATSConstants.KEY_ACCESS_DENIED.equals(strValue)) {
				strValue = PGStructuredATSConstants.VALUE_NO_ACCESS;
			}

			if (PGStructuredATSConstants.KEY_HIERARCHY.equals(strKey) || PGStructuredATSConstants.KEY_GROUP_REL.equals(strKey)) {
				StringList slHierarchyList = StringUtil.split(strValue, ",");
				JsonArrayBuilder jsonArrHierarchy = Json.createArrayBuilder();
				int iHierarchyListSize = slHierarchyList.size();
				for (int i = 0; i < iHierarchyListSize; i++) {
					jsonArrHierarchy.add(slHierarchyList.get(i));
				}
				jsonObjInfo.add(strKey, jsonArrHierarchy);
			} else {
				jsonObjInfo.add(strKey, strValue);
			}
		}
		return jsonObjInfo;
	}

	/**
	 * Method to get the complete FOP structure via FBOM
	 * 
	 * @param context
	 * @param slObjectIds
	 * @param slRelatedFOPList
	 * @param mapWhereUsedObjMap
	 */
	private void updateWhereUsedMapWithFOPStructure(Context context, StringList slObjectIds,
			StringList slRelatedFOPList, Map<String, MapList> mapWhereUsedAPPFOPMap) {
		try {
			int iRelatedFOPListSize = slRelatedFOPList.size();
			for (int i = 0; i < iRelatedFOPListSize; i++) {
				String strFOPId = slRelatedFOPList.get(i);
				DomainObject dobFOPObj = DomainObject.newInstance(context, strFOPId);

				StringList slObjSelects = new StringList();
				slObjSelects.add(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PROCESS);
				slObjSelects.addAll(slObjectSelects);

				Map<?, ?> mpFOPInfo = dobFOPObj.getInfo(context, slObjSelects);

				Map<String, String> mpFOPSelectsMap = updateMapWithParentChildObjSelects(context, mpFOPInfo);
				mpFOPSelectsMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strFOPId);

				MapList mlFOPList = new MapList();
				mlFOPList.add(mpFOPSelectsMap);

				String strFormulationProcessId = (String) mpFOPInfo
						.get(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PROCESS);

				if (UIUtil.isNotNullAndNotEmpty(strFormulationProcessId)) {
					updateWhereUsedMapWithProcessStructure(context, strFormulationProcessId, slObjectIds, mlFOPList,
							strFOPId);
				}

				mapWhereUsedAPPFOPMap.put(strFOPId, mlFOPList);

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to get the Formulation process till root level objects
	 * 
	 * @param context
	 * @param strFormulationProcessId
	 * @param slObjectIds
	 * @param mlFOPList
	 * @param strFOPId
	 */
	private void updateWhereUsedMapWithProcessStructure(Context context, String strFormulationProcessId,
			StringList slObjectIds, MapList mlFOPList, String strFOPId) {
		try {
			DomainObject dobFProcessObj = DomainObject.newInstance(context, strFormulationProcessId);
			Map<?, ?> mpFProcessInfo = dobFProcessObj.getInfo(context, slObjectSelects);

			Map<String, String> mpFProcessSelectsMap = updateMapWithParentChildObjSelects(context, mpFProcessInfo);
			String strHierarchy = strFOPId + "," + strFormulationProcessId;
			mpFProcessSelectsMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strHierarchy);
			mlFOPList.add(mpFProcessSelectsMap);

			StringList slPhaseIdList = dobFProcessObj.getInfoList(context,
					PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PHASE); // To get multiple phase ids

			updateWhereUsedMapWithPhaseStructure(context, slPhaseIdList, slObjectIds, strHierarchy, mlFOPList);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}

	}

	/**
	 * Method to get the Formulation Phase till root level objects
	 * 
	 * @param context
	 * @param slPhaseIdList
	 * @param slObjectIds
	 * @param strHierarchy
	 * @param mlFOPList
	 */
	private void updateWhereUsedMapWithPhaseStructure(Context context, StringList slPhaseIdList, StringList slObjectIds,
			String strHierarchy, MapList mlFOPList) {
		try {
			int iPhaseIdListSize = slPhaseIdList.size();
			StringList slPhaseSelects = new StringList(2);
			slPhaseSelects.add(PGStructuredATSConstants.SELECT_RELATED_FBOM_OBJ_ID);
			slPhaseSelects.add(PGStructuredATSConstants.SELECT_RELATED_FBOM_SUBSTITUTES_IDS);

			for (int i = 0; i < iPhaseIdListSize; i++) {
				boolean isPhaseInfoAdded = false;
				String strPhaseId = slPhaseIdList.get(i);
				String[] strPhaseIdArray = new String[1];
				strPhaseIdArray[0] = strPhaseId;
				MapList mlPhaseInfoList = DomainObject.getInfo(context, strPhaseIdArray, slPhaseSelects);

				DomainObject dobPhaseObj = DomainObject.newInstance(context, strPhaseId);
				Map<?, ?> mpPhasInfoMap = dobPhaseObj.getInfo(context, slObjectSelects);
				String strNewHierarchy = strHierarchy + "," + strPhaseId;
				Map<String, String> mpFPhaseSelectsMap = updateMapWithParentChildObjSelects(context, mpPhasInfoMap);
				mpFPhaseSelectsMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strNewHierarchy);

				if (!mlPhaseInfoList.isEmpty()) {
					Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlPhaseInfoList.get(0);
					updateWhereUsedMapWithFOPsRMPs(context, mpObjInfoMap, mlFOPList, strNewHierarchy, isPhaseInfoAdded,
							slObjectIds, mpFPhaseSelectsMap);

				}

			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}

	}

	/**
	 * Method to get the root level FOPs and RMPs for FBOM
	 * 
	 * @param context
	 * @param mpObjInfoMap
	 * @param mlFOPList
	 * @param strNewHierarchy
	 * @param isPhaseInfoAdded
	 * @param slObjectIds
	 * @param mpFPhaseSelectsMap
	 */
	private void updateWhereUsedMapWithFOPsRMPs(Context context, Map<?, ?> mpObjInfoMap, MapList mlFOPList,
			String strNewHierarchy, boolean isPhaseInfoAdded, StringList slObjectIds,
			Map<String, String> mpFPhaseSelectsMap) {
		try {
			String strOIDs = (String) mpObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_FBOM_OBJ_ID);
			if (UIUtil.isNotNullAndNotEmpty(strOIDs)) {
				updateWhereUsedForFBOMChildren(context, strOIDs, mlFOPList, strNewHierarchy, isPhaseInfoAdded,
						slObjectIds, mpFPhaseSelectsMap);

			}

			String strSubIds = (String) mpObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_FBOM_SUBSTITUTES_IDS);
			if (UIUtil.isNotNullAndNotEmpty(strSubIds)) {
				updateWhereUsedForFBOMSubstituteChildren(context, strSubIds, mlFOPList, strNewHierarchy,
						isPhaseInfoAdded, slObjectIds, mpFPhaseSelectsMap);

			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to update the leaf level BOM children for FBOM
	 * 
	 * @param context
	 * @param strOIDs
	 * @param mlFOPList
	 * @param strNewHierarchy
	 * @param isPhaseInfoAdded
	 * @param slObjectIds
	 * @param mpFPhaseSelectsMap
	 */
	private void updateWhereUsedForFBOMChildren(Context context, String strOIDs, MapList mlFOPList,
			String strNewHierarchy, boolean isPhaseInfoAdded, StringList slObjectIds,
			Map<String, String> mpFPhaseSelectsMap) {
		try {
			StringList slIdList = StringUtil.split(strOIDs, SelectConstants.cSelectDelimiter);

			for (int j = 0; j < iObjectIdsSize; j++) {
				String strInputId = slObjectIds.get(j);
				if (slIdList.contains(strInputId)) {
					if (!isPhaseInfoAdded) {
						mlFOPList.add(mpFPhaseSelectsMap);
						isPhaseInfoAdded = true;
					}

					DomainObject dobInputObj = DomainObject.newInstance(context, strInputId);
					Map<?, ?> mpInputObjMap = dobInputObj.getInfo(context, slObjectSelects);
					String strInputObjId = (String) mpInputObjMap.get(DomainConstants.SELECT_ID);
					Map<String, String> mpInputObjSelectsMap = updateMapWithParentChildObjSelects(context,
							mpInputObjMap);
					mpInputObjSelectsMap.put(PGStructuredATSConstants.KEY_HIERARCHY,
							strNewHierarchy + "," + strInputObjId);

					mpInputObjSelectsMap.put(PGStructuredATSConstants.KEY_OPERATION,
							PGStructuredATSConstants.VALUE_BOM);

					mlFOPList.add(mpInputObjSelectsMap);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to update the leaf level Substitute for FBOM
	 * 
	 * @param context
	 * @param strOIDs
	 * @param mlFOPList
	 * @param strNewHierarchy
	 * @param isPhaseInfoAdded
	 * @param slObjectIds
	 * @param mpFPhaseSelectsMap
	 */
	private void updateWhereUsedForFBOMSubstituteChildren(Context context, String strSubIds, MapList mlFOPList,
			String strNewHierarchy, boolean isPhaseInfoAdded, StringList slObjectIds,
			Map<String, String> mpFPhaseSelectsMap) {
		try {
			StringList slSubIdList = StringUtil.split(strSubIds, SelectConstants.cSelectDelimiter);

			for (int k = 0; k < iObjectIdsSize; k++) {
				String strInputId = slObjectIds.get(k);
				if (slSubIdList.contains(strInputId)) {
					if (!isPhaseInfoAdded) {
						mlFOPList.add(mpFPhaseSelectsMap);
						isPhaseInfoAdded = true;
					}

					DomainObject dobInputObj = DomainObject.newInstance(context, strInputId);
					Map<?, ?> mpInputObjMap = dobInputObj.getInfo(context, slObjectSelects);
					String strInputObjId = (String) mpInputObjMap.get(DomainConstants.SELECT_ID);
					Map<String, String> mpInputObjSelectsMap = updateMapWithParentChildObjSelects(context,
							mpInputObjMap);
					mpInputObjSelectsMap.put(PGStructuredATSConstants.KEY_HIERARCHY,
							strNewHierarchy + "," + strInputObjId);

					mpInputObjSelectsMap.put(PGStructuredATSConstants.KEY_OPERATION,
							PGStructuredATSConstants.VALUE_SUBSTITUTE);

					String strPhaseId = mpFPhaseSelectsMap.get(DomainConstants.SELECT_ID);
					String strPrimaryForSub = getPrimaryPartForSub(context, strPhaseId, strInputObjId);

					mpInputObjSelectsMap.put(PGStructuredATSConstants.KEY_PRIMARY, strPrimaryForSub);

					mlFOPList.add(mpInputObjSelectsMap);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to get Primary parts for Substitutes
	 * 
	 * @param context
	 * @param strPhaseId
	 * @param strInputObjId
	 * @return
	 */
	private String getPrimaryPartForSub(Context context, String strPhaseId, String strInputObjId) {
		String strPrimaryIds = "";
		StringList slPrimaryList = new StringList();
		try {
			StringList slObjSelects = new StringList(PGStructuredATSConstants.SELECT_FBOM_REL_ID);
			StringList slRelSelects = new StringList(DomainConstants.SELECT_TO_ID);
			slRelSelects.add(DomainConstants.SELECT_FROM_ID);

			String[] strOIDArray = new String[1];
			strOIDArray[0] = strInputObjId;

			MapList mpFBOMRelIdList = DomainObject.getInfo(context, strOIDArray, slObjSelects);
			if (!mpFBOMRelIdList.isEmpty()) {
				Map<?, ?> mpFBOMRelIdMap = (Map<?, ?>) mpFBOMRelIdList.get(0);
				String strFBOMRelIds = (String) mpFBOMRelIdMap.get(PGStructuredATSConstants.SELECT_FBOM_REL_ID);
				if (UIUtil.isNotNullAndNotEmpty(strFBOMRelIds)) {
					StringList slFBOMRelIds = StringUtil.split(strFBOMRelIds, SelectConstants.cSelectDelimiter);
					int iSize = slFBOMRelIds.size();
					for (int i = 0; i < iSize; i++) {
						String strFBOMRelId = slFBOMRelIds.get(i);
						String[] strRelIdArray = new String[1];
						strRelIdArray[0] = strFBOMRelId;
						MapList mlFBOMRelInfoList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
						Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlFBOMRelInfoList.get(0);
						String strFromId = (String) mpObjInfoMap.get(DomainConstants.SELECT_FROM_ID);
						String strToId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);
						if (strPhaseId.equals(strFromId) && !slPrimaryList.contains(strToId)) {
							slPrimaryList.add(strToId);
						}
					}
				}
			}

			strPrimaryIds = constructPrimaryString(slPrimaryList);

			if (UIUtil.isNotNullAndNotEmpty(strPrimaryIds)) {
				strPrimaryIds = strPrimaryIds.substring(0, strPrimaryIds.length() - 1);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
		return strPrimaryIds;
	}

	/**
	 * Method to construct Primary String
	 * 
	 * @param slPrimaryList
	 * @return
	 */
	private String constructPrimaryString(StringList slPrimaryList) {
		StringBuilder sbPrimary = new StringBuilder();
		int iSize = slPrimaryList.size();
		for (int i = 0; i < iSize; i++) {
			sbPrimary.append(slPrimaryList.get(i)).append(",");
		}
		return sbPrimary.toString();
	}

	/**
	 * Method to get the related FOPs for RMPs and FOPs
	 * 
	 * @param context
	 * @param slObjectIds
	 * @param slProcessedIdList
	 * @param slRelatedFOPList
	 */
	private void updateWhereUsedObjListWithrelatedFOPs(Context context, StringList slObjectIds,
			StringList slProcessedIdList, StringList slRelatedFOPList) {

		Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_FORMULATION_PROCESS);
		typePattern.addPattern(PGStructuredATSConstants.TYPE_FORMULATION_PART);

		Pattern relPattern = new Pattern(PGStructuredATSConstants.RELATIONSHIP_FBOM);
		relPattern.addPattern(PGStructuredATSConstants.RELATIONSHIP_PLANNED_FOR);

		StringList slObjselects = new StringList(4);
		slObjselects.add(DomainConstants.SELECT_ID);
		slObjselects.add(DomainConstants.SELECT_TYPE);
		slObjselects.add(DomainConstants.SELECT_CURRENT);
		slObjselects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);

		StringList slFormulationPhaseList = getFormulationPhaseForBOMandSubstitutes(context, slObjectIds);

		int iPhaseListSize = slFormulationPhaseList.size();
		for (int i = 0; i < iPhaseListSize; i++) {
			String strObjectId = slFormulationPhaseList.get(i);
			updateWhereUsedListForFOPObjs(context, strObjectId, slProcessedIdList, slRelatedFOPList, typePattern,
					relPattern, slObjselects);

		}

	}

	/**
	 * Method to get all the related Formulation Phases for childs as BOMs and
	 * Substitutes
	 * 
	 * @param context
	 * @param slObjectIds
	 * @return
	 */
	private StringList getFormulationPhaseForBOMandSubstitutes(Context context, StringList slObjectIds) {
		StringList slFormulationPhaseList = new StringList();
		try {
			StringList slObjSelects = new StringList(2);
			slObjSelects.add(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PHASE_FOR_CHILDREN);
			slObjSelects.add(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PHASE_FOR_SUBSTITUTES);

			String[] strOIDArray = slObjectIds.toStringArray();

			MapList mpFormulationPhaseList = DomainObject.getInfo(context, strOIDArray, slObjSelects);

			int iListSize = mpFormulationPhaseList.size();
			for (int i = 0; i < iListSize; i++) {
				Map<?, ?> mpObjInfoMap = (Map<?, ?>) mpFormulationPhaseList.get(i);
				updatePhaseIdList(slFormulationPhaseList, mpObjInfoMap);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
		return slFormulationPhaseList;
	}

	/**
	 * Method to get all the related Formulation Phases
	 * 
	 * @param slFormulationPhaseList
	 * @param mpObjInfoMap
	 */
	private void updatePhaseIdList(StringList slFormulationPhaseList, Map<?, ?> mpObjInfoMap) {
		for (Map.Entry<?, ?> entry : mpObjInfoMap.entrySet()) {
			String strOIDs = (String) entry.getValue();
			if (UIUtil.isNotNullAndNotEmpty(strOIDs)) {
				StringList slPhaseIdList = StringUtil.split(strOIDs, SelectConstants.cSelectDelimiter);
				int iPhaselistSize = slPhaseIdList.size();
				for (int j = 0; j < iPhaselistSize; j++) {
					String strPhaseId = slPhaseIdList.get(j);
					if (!slFormulationPhaseList.contains(strPhaseId)) {
						slFormulationPhaseList.add(strPhaseId);
					}
				}
			}
		}

	}

	/**
	 * Method to get the related FOPs for RMPs and FOPs
	 * 
	 * @param context
	 * @param strObjectId
	 * @param slProcessedIdList
	 * @param slRelatedFOPList
	 * @param typePattern
	 * @param relPattern
	 * @param slObjselects
	 */
	private void updateWhereUsedListForFOPObjs(Context context, String strObjectId, StringList slProcessedIdList,
			StringList slRelatedFOPList, Pattern typePattern, Pattern relPattern, StringList slObjselects) {

		try {
			DomainObject dobInputObj = DomainObject.newInstance(context, strObjectId);

			MapList mlRelatedObjects = dobInputObj.getRelatedObjects(context, // the eMatrix Context object
					relPattern.getPattern(), // Relationship pattern
					typePattern.getPattern(), // Type pattern
					slObjselects, // Object selects
					null, // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 2, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			int iRelatedObjectsSize = mlRelatedObjects.size();
			for (int j = 0; j < iRelatedObjectsSize; j++) {
				Map<?, ?> mapObjInfo = (Map<?, ?>) mlRelatedObjects.get(j);
				String strFOPId = (String) mapObjInfo.get(DomainConstants.SELECT_ID);
				String strType = (String) mapObjInfo.get(DomainConstants.SELECT_TYPE);
				String strCurrent = (String) mapObjInfo.get(DomainConstants.SELECT_CURRENT);
				String strAttrReleasePhase = (String) mapObjInfo
						.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
				
				if(strCurrent.contains(PGStructuredATSConstants.KEY_DENIED)) {
					Map<String,String> mpInfoMap = getCurrentAndPhaseForShowAccessObjs(context, strFOPId);
					strCurrent = mpInfoMap.get(DomainConstants.SELECT_CURRENT);
					strAttrReleasePhase = mpInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
				}

				if (!slProcessedIdList.contains(strFOPId)
						&& PGStructuredATSConstants.TYPE_FORMULATION_PART.equals(strType)
						&& (DomainConstants.STATE_PART_PRELIMINARY.equals(strCurrent)
								|| DomainConstants.STATE_PART_RELEASE.equals(strCurrent))
						&& PGStructuredATSConstants.RANGE_PRODUCTION.equals(strAttrReleasePhase)) {
					slRelatedFOPList.add(strFOPId);
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Push context to get No Access data
	 * 
	 * @param context
	 * @param strFOPId
	 * @param strCurrent
	 * @param strAttrReleasePhase
	 * @throws FrameworkException
	 */
	private Map<String,String> getCurrentAndPhaseForShowAccessObjs(Context context, String strFOPId) throws FrameworkException {
		Map<String,String> mpInfoMap = new HashMap<>();
		mpInfoMap.put(DomainConstants.SELECT_CURRENT, "");
		mpInfoMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE, "");
		mpInfoMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION, "");
		boolean isContextPushed = false;
		try {
			ContextUtil.pushContext(context);
			isContextPushed = true;
			
			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_CURRENT);
			slObjSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			slObjSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);
			
			DomainObject dobInputObj = DomainObject.newInstance(context, strFOPId);
			Map<?, ?> objInfoMap = dobInputObj.getInfo(context, slObjSelects);
			String strCurrent = (String) objInfoMap.get(DomainConstants.SELECT_CURRENT);
			String strAttrReleasePhase = (String) objInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			mpInfoMap.put(DomainConstants.SELECT_CURRENT, strCurrent);
			mpInfoMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE, strAttrReleasePhase);
			
			String strAttrAuthoringApplication = (String) objInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);
			if(UIUtil.isNotNullAndNotEmpty(strAttrAuthoringApplication)) {
				mpInfoMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION, strAttrAuthoringApplication);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return mpInfoMap;
	}

	/**
	 * Method to get the related APPs for input FOP and RMP ids
	 * 
	 * @param context
	 * @param slObjectIds
	 * @param slProcessedIdList
	 * @param mapWhereUsedObjMap
	 * @return
	 */
	private void updateWhereUsedObjListWithrelatedAPPs(Context context, StringList slObjectIds,
			StringList slProcessedIdList, Map<String, MapList> mapWhereUsedObjMap) {
		Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART);
		Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_EBOM);

		StringList slObjselects = new StringList();
		slObjselects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		slObjselects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);
		slObjselects.addAll(slObjectSelects);
		
		for (int i = 0; i < iObjectIdsSize; i++) {
			String strObjectId = slObjectIds.get(i);
			updateWhereUsedListForAPPObjs(context, strObjectId, slProcessedIdList, mapWhereUsedObjMap, typePattern,
					relPattern, slObjselects);
		}

	}

	/**
	 * Method to get the related APP objects for RMP and FOP
	 * 
	 * @param context
	 * @param strObjectId
	 * @param slProcessedIdList
	 * @param mapWhereUsedObjMap
	 * @param relPattern
	 * @param typePattern
	 * @param slObjselects
	 */
	private void updateWhereUsedListForAPPObjs(Context context, String strObjectId, StringList slProcessedIdList,
			Map<String, MapList> mapWhereUsedObjMap, Pattern typePattern, Pattern relPattern, StringList slObjselects) {

		try {
			DomainObject dobInputObj = DomainObject.newInstance(context, strObjectId);

			MapList mlRelatedObjects = dobInputObj.getRelatedObjects(context, // the eMatrix Context object
					relPattern.getPattern(), // Relationship pattern
					typePattern.getPattern(), // Type pattern
					slObjselects, // Object selects
					null, // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			int iRelatedObjectsSize = mlRelatedObjects.size();
			if (iRelatedObjectsSize > 0) {
				Map<?, ?> mpInputObjInfo = dobInputObj.getInfo(context, slObjectSelects);

				for (int i = 0; i < iRelatedObjectsSize; i++) {
					Map<?, ?> mapObjInfo = (Map<?, ?>) mlRelatedObjects.get(i);
					String strParentId = (String) mapObjInfo.get(DomainConstants.SELECT_ID);
					boolean isValidAPPObj = checkForValidStateAndPhaseForAPP(context, strParentId, mapObjInfo);
					if (isValidAPPObj) {
						if (!slProcessedIdList.contains(strParentId)) {
							updateWhereUsedListObjectInfo(context, mapObjInfo, mapWhereUsedObjMap, slProcessedIdList,
									strParentId, mpInputObjInfo);
						} else {
							updateWhereUsedListObjectInfoForExistingParent(context, mpInputObjInfo, mapWhereUsedObjMap,
									strParentId);
						}
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to check for valid Phase and Current for APPs
	 * 
	 * @param context
	 * @param strParentId
	 * @param mapObjInfo
	 * @return
	 * @throws FrameworkException
	 */
	private boolean checkForValidStateAndPhaseForAPP(Context context, String strParentId, Map<?, ?> mapObjInfo)
			throws FrameworkException {
		boolean isValidAPP = false;
		String strType = (String) mapObjInfo.get(DomainConstants.SELECT_TYPE);
		String strCurrent = (String) mapObjInfo.get(DomainConstants.SELECT_CURRENT);
		String strAttrReleasePhase = (String) mapObjInfo.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		String strAttrAuthoringApplication = (String) mapObjInfo.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);
		
		if (strCurrent.contains(PGStructuredATSConstants.KEY_DENIED)) {
			Map<String, String> mpInfoMap = getCurrentAndPhaseForShowAccessObjs(context, strParentId);
			strCurrent = mpInfoMap.get(DomainConstants.SELECT_CURRENT);
			strAttrReleasePhase = mpInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			strAttrAuthoringApplication = mpInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_AUTHORING_APPLICATION);
		}

		if ((DomainConstants.STATE_PART_PRELIMINARY.equals(strCurrent)
						|| DomainConstants.STATE_PART_RELEASE.equals(strCurrent))
				&& PGStructuredATSConstants.RANGE_PRODUCTION.equals(strAttrReleasePhase)
				&& PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strType)
				&& !PGStructuredATSConstants.VALUE_LPD_APOLLO.equals(strAttrAuthoringApplication)) {
			isValidAPP = true;
		}

		return isValidAPP;
	}

	/**
	 * method to put the object selects value into where used Map mapWhereUsedObjMap
	 * 
	 * @param context
	 * @param mapObjInfo
	 * @param mapWhereUsedObjMap
	 * @param slProcessedIdList
	 * @param strParentId
	 * @param mpInputObjInfo
	 */
	private void updateWhereUsedListObjectInfo(Context context, Map<?, ?> mapObjInfo,
			Map<String, MapList> mapWhereUsedObjMap, StringList slProcessedIdList, String strParentId,
			Map<?, ?> mpInputObjInfo) {
		MapList mlAPPList = new MapList();
		Map<String, String> objParentMap = updateMapWithParentChildObjSelects(context, mapObjInfo);
		objParentMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId);
		mlAPPList.add(objParentMap);

		Map<String, String> objChildMap = updateMapWithParentChildObjSelects(context, mpInputObjInfo);
		String strChildOID = (String) mpInputObjInfo.get(DomainConstants.SELECT_ID);
		objChildMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId + "," + strChildOID);
		objChildMap.put(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.VALUE_BOM);
		mlAPPList.add(objChildMap);

		mapWhereUsedObjMap.put(strParentId, mlAPPList);
		slProcessedIdList.add(strParentId);

	}

	/**
	 * Update MapList with slObjectSelects Map
	 * 
	 * @param context
	 * @param mapObjInfo
	 * @return
	 */
	private Map<String, String> updateMapWithParentChildObjSelects(Context context, Map<?, ?> mapObjInfo) {
		Map<String, String> mapObjSelectValues = new HashMap<>();
		try {
			for (int j = 0; j < iObjectSelectsSize; j++) {
				String strObjSelectValue = "";
				String strObjSelectKey = slObjectSelects.get(j);
				Object objSelectValue = mapObjInfo.get(strObjSelectKey);
				if (objSelectValue instanceof StringList) {
					strObjSelectValue = formatStringListData(objSelectValue);
				} else {
					strObjSelectValue = (String) objSelectValue;
					if (strObjSelectValue == null) {
						strObjSelectValue = PGStructuredATSConstants.STRING_NULL;
					}
				}
				mapObjSelectValues.put(strObjSelectKey, strObjSelectValue);
			}
			String strTypeName = (String) mapObjInfo.get(DomainConstants.SELECT_TYPE);
			String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context,
					PGStructuredATSConstants.STR_SCHEMA_TYPE, strTypeName, strLanguage);

			if (strDisplayName == null) {
				strDisplayName = strTypeName;
			}
			mapObjSelectValues.put(PGStructuredATSConstants.KEY_DISPLAY_TYPE, strDisplayName);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
		return mapObjSelectValues;
	}

	/**
	 * Format multi-values to String with separator
	 * 
	 * @param objSelectValue
	 * @return
	 */
	public String formatStringListData(Object objSelectValue) {
		StringBuilder sbObjValue = new StringBuilder();
		StringList slObjValuesList = (StringList) objSelectValue;
		int iListSize = slObjValuesList.size();
		int iCount = 1;
		for (int i = 0; i < iListSize; i++) {
			String strObjValue = slObjValuesList.get(i);
			sbObjValue.append(strObjValue);

			if (iCount < iListSize) {
				sbObjValue.append(",");
			}
			iCount++;
		}
		return sbObjValue.toString();
	}

	/**
	 * Update child to existing parent
	 * 
	 * @param context
	 * @param mpInputObjInfo
	 * @param mapWhereUsedObjMap
	 * @param strParentId
	 * @param strObjectId
	 */
	private void updateWhereUsedListObjectInfoForExistingParent(Context context, Map<?, ?> mpInputObjInfo,
			Map<String, MapList> mapWhereUsedObjMap, String strParentId) {
		Map<String, String> objChildMap = updateMapWithParentChildObjSelects(context, mpInputObjInfo);
		String strChilId = (String) mpInputObjInfo.get(DomainConstants.SELECT_ID);
		objChildMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId + "," + strChilId);
		objChildMap.put(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.VALUE_BOM);

		MapList mlExistingInfoList = mapWhereUsedObjMap.get(strParentId);
		
		//check to avoid duplicate children
		boolean isDuplicateChild = checkForDuplicateChild(strChilId, mlExistingInfoList);
		
		if(!isDuplicateChild) {
			mlExistingInfoList.add(objChildMap);
			mapWhereUsedObjMap.put(strParentId, mlExistingInfoList);
		}
	}

	/**
	 * Method to avoid duplicate child for EBOM
	 * @param strChilId
	 * @param mlExistingInfoList
	 * @return
	 */
	private boolean checkForDuplicateChild(String strChilId, MapList mlExistingInfoList) {
		boolean isDuplicateChild = false;
		int iListSize = mlExistingInfoList.size();
		for(int i=0;i<iListSize;i++) {
			Map<?,?> mpExistingInfoMap = (Map<?, ?>) mlExistingInfoList.get(i);
			String strObjId = (String) mpExistingInfoMap.get(DomainConstants.SELECT_ID);
			if(strChilId.equals(strObjId)) {
				isDuplicateChild = true;
				break;
			}
		}
				
		return isDuplicateChild;
	}

	/**
	 * Method to filter APP based on Plant info
	 * 
	 * @param context
	 * @param strPlantIds
	 * @param strIsAuthorizedToProduce
	 * @param strIsAuthorizedToUse
	 * @param mapWhereUsedObjMap
	 * @param mapWhereUsedAPPFOPMap
	 */
	private Map<String, MapList> filterWhereUsedAPPsWithPlantInfo(Context context, String strPlantIds,
			String strIsAuthorizedToProduce, String strIsAuthorizedToUse, Map<String, MapList> mapWhereUsedObjMap) {
		Map<String, MapList> mapWhereUsedAPPFOPMap = new HashMap<>();
		try {
			if (!mapWhereUsedObjMap.isEmpty()) {
				if (UIUtil.isNullOrEmpty(strPlantIds)) {
					return mapWhereUsedObjMap;
				} else {
					StringList slRelatedObjList = getAPPIdsList(mapWhereUsedObjMap);
					StringList slFilteredIdList = filterObjectsWithPlantInfo(context, strPlantIds, strIsAuthorizedToProduce,
							strIsAuthorizedToUse, slRelatedObjList);
	
					int iFilteredIdListSize = slFilteredIdList.size();
					for (int i = 0; i < iFilteredIdListSize; i++) {
						String strAPPId = slFilteredIdList.get(i);
						MapList mlAPPInfoList = mapWhereUsedObjMap.get(strAPPId);
						mapWhereUsedAPPFOPMap.put(strAPPId, mlAPPInfoList);
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		} 
		return mapWhereUsedAPPFOPMap;
	}

	/**
	 * Method to get APP ids
	 * 
	 * @param mapWhereUsedObjMap
	 * @return
	 */
	private StringList getAPPIdsList(Map<String, MapList> mapWhereUsedObjMap) {
		StringList slRelatedObjList = new StringList();
		for (Map.Entry<String, MapList> entry : mapWhereUsedObjMap.entrySet()) {
			slRelatedObjList.add(entry.getKey());
		}
		return slRelatedObjList;
	}

	/**
	 * Method to filter the related APP and FOPs based on the input Plant details
	 * 
	 * @param context
	 * @param strPlantIds
	 * @param strIsAuthorizedToProduce
	 * @param strIsAuthorizedToUse
	 * @param slRelatedObjList
	 * @throws FrameworkException
	 */
	private StringList filterObjectsWithPlantInfo(Context context, String strPlantIds, String strIsAuthorizedToProduce,
			String strIsAuthorizedToUse, StringList slRelatedObjList) throws FrameworkException {

		StringList slFilteredIdsList = new StringList();

		if (UIUtil.isNotNullAndNotEmpty(strPlantIds)) {
			StringList slPlantIdList = StringUtil.split(strPlantIds, ",");

			Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_PLANT);
			Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY);

			StringList slObjSelects = new StringList(DomainConstants.SELECT_ID);
			slObjSelects.add(DomainConstants.SELECT_PHYSICAL_ID);

			StringList slRelSelects = new StringList(2);
			slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
			slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);

			boolean isContextPushed = false;
			try {
				ContextUtil.pushContext(context);
				isContextPushed = true;
				int iRelatedObjListSize = slRelatedObjList.size();
				for (int i = 0; i < iRelatedObjListSize; i++) {
					String strObjectId = slRelatedObjList.get(i);
					DomainObject dobWhereUsedObj = DomainObject.newInstance(context, strObjectId);
					MapList mlRelatedPlants = dobWhereUsedObj.getRelatedObjects(context, // the eMatrix Context object
							relPattern.getPattern(), // Relationship pattern
							typePattern.getPattern(), // Type pattern
							slObjSelects, // Object selects
							slRelSelects, // Relationship selects
							true, // get From relationships
							false, // get To relationships
							(short) 1, // the number of levels to expand, 0 equals expand all.
							null, // Object where clause
							null, // Relationship where clause
							0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
								// data available

					checkForExistingPlantInfo(mlRelatedPlants, strIsAuthorizedToProduce, strIsAuthorizedToUse,
							slPlantIdList, slFilteredIdsList, strObjectId);

				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
				}
			}
		} else {
			return slRelatedObjList;
		}

		return slFilteredIdsList;

	}

	/**
	 * Method to check whether the APPs or FOPs has the Plant info
	 * 
	 * @param mlRelatedPlants
	 * @param strIsAuthorizedToProduce
	 * @param strIsAuthorizedToUse
	 * @param slPlantIdList
	 * @param slFilteredIdsList
	 * @param strObjectId
	 */
	private void checkForExistingPlantInfo(MapList mlRelatedPlants, String strIsAuthorizedToProduce,
			String strIsAuthorizedToUse, StringList slPlantIdList, StringList slFilteredIdsList, String strObjectId) {
		int iRelatedPlantsSize = mlRelatedPlants.size();
		for (int j = 0; j < iRelatedPlantsSize; j++) {
			Map<?, ?> mapPlantInfo = (Map<?, ?>) mlRelatedPlants.get(j);
			String strPlantId = (String) mapPlantInfo.get(DomainConstants.SELECT_ID);
			String strPlantPhysicalId = (String) mapPlantInfo.get(DomainConstants.SELECT_PHYSICAL_ID);
			String strAttrAuthorizedToProduce = (String) mapPlantInfo
					.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
			String strAttrAuthorizedToUse = (String) mapPlantInfo
					.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
			if ((slPlantIdList.contains(strPlantId) || slPlantIdList.contains(strPlantPhysicalId))
					&& (strAttrAuthorizedToProduce.equalsIgnoreCase(strIsAuthorizedToProduce)
					|| strAttrAuthorizedToUse.equalsIgnoreCase(strIsAuthorizedToUse))) {
				slFilteredIdsList.add(strObjectId);
				break;
			}
		}

	}

	/**
	 * Method to get the Alternate part for the RMPs
	 * 
	 * @param context
	 * @param slObjectIds
	 * @param slProcessedIdList
	 * @param mapWhereUsedAPPFOPMap
	 */
	private void updateWhereUsedObjListWithAlternates(Context context, StringList slObjectIds,
			StringList slProcessedIdList, Map<String, MapList> mapWhereUsedAPPFOPMap) {

		Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_RAW_MATERIAL);
		Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_ALTERNATE);

		for (int i = 0; i < iObjectIdsSize; i++) {
			String strObjectId = slObjectIds.get(i);
			updateWhereUsedObjMapWithAlternates(context, strObjectId, slProcessedIdList, typePattern, relPattern,
					mapWhereUsedAPPFOPMap);
		}
	}

	/**
	 * Method to get the Alternate part for the RMPs
	 * 
	 * @param context
	 * @param strObjectId
	 * @param slProcessedIdList
	 * @param typePattern
	 * @param relPattern
	 * @param jsonArrObjInfo
	 */
	private void updateWhereUsedObjMapWithAlternates(Context context, String strObjectId, StringList slProcessedIdList,
			Pattern typePattern, Pattern relPattern, Map<String, MapList> mapWhereUsedAPPFOPMap) {
		try {
			DomainObject dobInputObj = DomainObject.newInstance(context, strObjectId);
			MapList mlRelatedObjects = dobInputObj.getRelatedObjects(context, // the eMatrix Context object
					relPattern.getPattern(), // Relationship pattern
					typePattern.getPattern(), // Type pattern
					slObjectSelects, // Object selects
					null, // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			int iRelatedObjectsSize = mlRelatedObjects.size();
			if (iRelatedObjectsSize > 0) {
				Map<?, ?> mpInputObjInfo = dobInputObj.getInfo(context, slObjectSelects);

				for (int i = 0; i < iRelatedObjectsSize; i++) {
					Map<?, ?> mapObjInfo = (Map<?, ?>) mlRelatedObjects.get(i);
					String strParentId = (String) mapObjInfo.get(DomainConstants.SELECT_ID);

					Map<String, String> objChildMap = updateMapWithParentChildObjSelects(context, mpInputObjInfo);
					String strChildOID = (String) mpInputObjInfo.get(DomainConstants.SELECT_ID);
					objChildMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId + "," + strChildOID);
					objChildMap.put(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.VALUE_ALTERNATE);

					if (!slProcessedIdList.contains(strParentId)) {
						MapList mlAlternateList = new MapList();
						Map<String, String> objParentMap = updateMapWithParentChildObjSelects(context, mapObjInfo);
						objParentMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId);
						mlAlternateList.add(objParentMap);
						mlAlternateList.add(objChildMap);
						mapWhereUsedAPPFOPMap.put(strParentId, mlAlternateList);
						slProcessedIdList.add(strParentId);
					} else {
						MapList mlExistingInfoList = mapWhereUsedAPPFOPMap.get(strParentId);
						mlExistingInfoList.add(objChildMap);
						mapWhereUsedAPPFOPMap.put(strParentId, mlExistingInfoList);
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to form the object select list
	 * 
	 * @param strObjSelects
	 */
	private void getObjectSelectList(String strObjSelects) {

		slObjectSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_REVISION);
		slObjectSelects.add(DomainConstants.SELECT_CURRENT);
		slObjectSelects.add(DomainConstants.SELECT_DESCRIPTION);
		slObjectSelects.add(DomainConstants.SELECT_OWNER);
		slObjectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);

		StringList slObjSelectArgList = StringUtil.split(strObjSelects, ",");
		for (int i = 0; i < slObjSelectArgList.size(); i++) {
			String strObjSelect = slObjSelectArgList.get(i);
			if (!slObjectSelects.contains(strObjSelect)) {
				slObjectSelects.add(strObjSelect);
			}
		}
	}
	//Methods (getWhereUsedObjects) related to Where Used fetch use case : End
	
	/**
	 * Method to connected the where used objects with SATS object on Save
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	String updateWhereusedSelectedItem(Context context, String strJsonInput) {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strSTASId = jsonInputData.getString(DomainConstants.SELECT_ID);
			JsonObject jsonWhereUsedData = jsonInputData.getJsonObject(PGStructuredATSConstants.KEY_DATA);

			StringList slObjIdList = new StringList();

			jsonWhereUsedData.forEach((strKey, value) -> {
				String strObjId = strKey;
				slObjIdList.add(strObjId);
			});

			if (UIUtil.isNotNullAndNotEmpty(strSTASId) && !slObjIdList.isEmpty()) {
				ContextUtil.startTransaction(context, true);
				DomainObject dobSATSObj = DomainObject.newInstance(context, strSTASId);
				StringList slObjIdsToBeConnectedList = checkForAlreadyConnectedIds(context, dobSATSObj, slObjIdList);
				StringList slPlantsToBeConnectedList = checkForAlreadyConnectedPlantIds(context, strSTASId,
						slObjIdList);
				if (!slObjIdsToBeConnectedList.isEmpty()) {
					String[] strObjIDArray = slObjIdsToBeConnectedList.toStringArray();
					DomainRelationship.connect(context, dobSATSObj,
							PGStructuredATSConstants.RELATIONSHIP_AUTHORIZED_TEMPORARY_SPECIFICATION, true,
							strObjIDArray);
				}

				if (!slPlantsToBeConnectedList.isEmpty()) {
					String[] strObjIDArray = slPlantsToBeConnectedList.toStringArray();
					DomainRelationship.connect(context, dobSATSObj,
							DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, false, strObjIDArray);
				}

				String strAttrValue = getWhereUsedSelectedItemsAttributeValue(jsonWhereUsedData);
				if (UIUtil.isNotNullAndNotEmpty(strAttrValue)) {
					strAttrValue = strAttrValue.substring(0, strAttrValue.length() - 1);
					dobSATSObj.setAttributeValue(context,
							PGStructuredATSConstants.ATTRIBUTE_PGSATSWHEREUSEDSELECTEDITEMS, strAttrValue);
				}
				ContextUtil.commitTransaction(context);
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			}
			return jsonReturnObj.build().toString();

		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonReturnObj.build().toString();
		}
	}

	/**
	 * Construct the value for the attribute pgSATSWhereUsedSelectedItems
	 * 
	 * @param jsonWhereUsedData
	 * @return
	 */
	private String getWhereUsedSelectedItemsAttributeValue(JsonObject jsonWhereUsedData) {
		StringBuilder sbAttrValue = new StringBuilder();
		jsonWhereUsedData.forEach((strKey, value) -> {
			JsonArray jsonObjArray = (JsonArray) value;
			JsonObject jsonParentObj = jsonObjArray.getJsonObject(0);
			String strParentType = jsonParentObj.getString(DomainConstants.SELECT_TYPE);
			String strParentId = jsonParentObj.getString(DomainConstants.SELECT_ID);
			if (PGStructuredATSConstants.TYPE_FORMULATION_PART.equals(strParentType)) {
				appendAttrValueForFOP(sbAttrValue, strParentId, jsonObjArray);
			} else {
				appendAttrValueForOtherTypes(sbAttrValue, strParentId, jsonObjArray);
			}

		});

		return sbAttrValue.toString();
	}

	/**
	 * Method to update attribute value for APP, RMPs and children
	 * 
	 * @param sbAttrValue
	 * @param strParentId
	 * @param jsonObjArray
	 */
	private void appendAttrValueForOtherTypes(StringBuilder sbAttrValue, String strParentId, JsonArray jsonObjArray) {
		sbAttrValue.append(strParentId).append(":");
		int iJsonArraySize = jsonObjArray.size();
		int iCount = 2;
		for (int i = 1; i < iJsonArraySize; i++) {
			JsonObject jsonChildObj = jsonObjArray.getJsonObject(i);
			String strChildId = jsonChildObj.getString(DomainConstants.SELECT_ID);
			if (jsonChildObj.containsKey(PGStructuredATSConstants.KEY_PRIMARY)) {
				String strPrimaryObjNames = jsonChildObj.getString(PGStructuredATSConstants.KEY_PRIMARY);
				String strSubstitutesInfo = getSubstitutesInformation(strChildId, strPrimaryObjNames);
				sbAttrValue.append(strSubstitutesInfo);
			} else {
				sbAttrValue.append(strChildId);
			}

			if (iCount < iJsonArraySize) {
				sbAttrValue.append(",");
			}
			iCount++;
		}
		sbAttrValue.append(";");
	}

	/**
	 * Method to update Substitutes info
	 * 
	 * @param strChildId
	 * @param strPrimaryObjNames
	 * @return
	 */
	private String getSubstitutesInformation(String strChildId, String strPrimaryObjNames) {
		StringBuilder sbSubstitutesInfo = new StringBuilder();
		StringList slPrimaryInfoList = StringUtil.split(strPrimaryObjNames, ",");
		int iListSize = slPrimaryInfoList.size();
		int iCount = 1;
		for (int i = 0; i < iListSize; i++) {
			String strPrimaryObjName = slPrimaryInfoList.get(i);
			sbSubstitutesInfo.append(strPrimaryObjName).append("_").append(strChildId);

			if (iCount < iListSize) {
				sbSubstitutesInfo.append(",");
			}
			iCount++;
		}
		return sbSubstitutesInfo.toString();
	}

	/**
	 * Method to update attribute value for FOP and children
	 * 
	 * @param sbAttrValue
	 * @param strParentId
	 * @param jsonObjArray
	 */
	private void appendAttrValueForFOP(StringBuilder sbAttrValue, String strParentId, JsonArray jsonObjArray) {
		sbAttrValue.append(strParentId).append(":");
		int iJsonArraySize = jsonObjArray.size();
		int iCount = 3;
		for (int i = 2; i < iJsonArraySize; i++) {
			appendAttrValueForCurrentFOPObj(jsonObjArray, iJsonArraySize, iCount, i, sbAttrValue);
			iCount++;
		}
		sbAttrValue.append(";");

	}

	/**
	 * Method to update attribute value for FOP and children
	 * 
	 * @param jsonObjArray
	 * @param iJsonArraySize
	 * @param iCount
	 * @param i
	 * @param sbAttrValue
	 */
	private void appendAttrValueForCurrentFOPObj(JsonArray jsonObjArray, int iJsonArraySize, int iCount, int i,
			StringBuilder sbAttrValue) {
		JsonObject jsonChildObj = jsonObjArray.getJsonObject(i);
		String strChildType = jsonChildObj.getString(DomainConstants.SELECT_TYPE);
		String strChildId = jsonChildObj.getString(DomainConstants.SELECT_ID);
		if (PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strChildType)) {
			if (i > 2) {
				sbAttrValue.append("|");
			}
			sbAttrValue.append(strChildId).append("-");
		} else {
			if (jsonChildObj.containsKey(PGStructuredATSConstants.KEY_PRIMARY)) {
				String strPrimaryObjNames = jsonChildObj.getString(PGStructuredATSConstants.KEY_PRIMARY);
				String strSubstitutesInfo = getSubstitutesInformation(strChildId, strPrimaryObjNames);
				sbAttrValue.append(strSubstitutesInfo);
			} else {
				sbAttrValue.append(strChildId);
			}

			if (iCount < iJsonArraySize) {
				JsonObject jsonNextChild = jsonObjArray.getJsonObject(i + 1);
				String strNextChildType = jsonNextChild.getString(DomainConstants.SELECT_TYPE);

				if (!PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strNextChildType)) {
					sbAttrValue.append(",");
				}
			}
		}

	}

	/**
	 * Method to check for the already connected objects to SATS object
	 * 
	 * @param context
	 * @param dobSATSObj
	 * @param slObjIdList
	 * @return
	 */
	private StringList checkForAlreadyConnectedIds(Context context, DomainObject dobSATSObj, StringList slObjIdList) {
		StringList slObjIdsToBeConnectedList = new StringList();
		try {
			StringList slRelatedIdList = new StringList();
			StringList slObjSelectList = new StringList();
			slObjSelectList.add(DomainConstants.SELECT_ID);
			slObjSelectList.add(DomainConstants.SELECT_PHYSICAL_ID);

			Pattern relPattern = new Pattern(PGStructuredATSConstants.RELATIONSHIP_AUTHORIZED_TEMPORARY_SPECIFICATION);

			MapList mlRelatedObjects = dobSATSObj.getRelatedObjects(context, // the eMatrix Context object
					relPattern.getPattern(), // Relationship pattern
					"*", // Type pattern
					slObjSelectList, // Object selects
					null, // Relationship selects
					false, // get From relationships
					true, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			for (int i = 0; i < mlRelatedObjects.size(); i++) {
				Map<?, ?> objInfoMap = (Map<?, ?>) mlRelatedObjects.get(i);
				String strObjId = (String) objInfoMap.get(DomainConstants.SELECT_ID);
				slRelatedIdList.add(strObjId);
				String strPhysicalId = (String) objInfoMap.get(DomainConstants.SELECT_PHYSICAL_ID);
				slRelatedIdList.add(strPhysicalId);
			}

			for (int j = 0; j < slObjIdList.size(); j++) {
				String strObjId = slObjIdList.get(j);
				if (!slRelatedIdList.contains(strObjId)) {
					slObjIdsToBeConnectedList.add(strObjId);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}

		return slObjIdsToBeConnectedList;
	}

	/**
	 * Method to filter out the Plants id to be connected to
	 * 
	 * @param context
	 * @param strSTASId
	 * @param slObjIdList
	 * @return
	 */
	private StringList checkForAlreadyConnectedPlantIds(Context context, String strSTASId, StringList slObjIdList) {
		StringList slPlantsToBeConnectedList = new StringList();
		StringList slUniquePlantIdList = getRelatedPlantsForObjs(context, slObjIdList);

		StringList slSATSIdList = new StringList();
		slSATSIdList.add(strSTASId);

		StringList slConnectedPlantsToSATS = getRelatedPlantsForObjs(context, slSATSIdList);

		int iSize = slUniquePlantIdList.size();
		for (int i = 0; i < iSize; i++) {
			String strPlantId = slUniquePlantIdList.get(i);
			if (!slConnectedPlantsToSATS.contains(strPlantId)) {
				slPlantsToBeConnectedList.add(strPlantId);
			}
		}
		return slPlantsToBeConnectedList;
	}

	/**
	 * Method to get related Plants for parents
	 * 
	 * @param context
	 * @param slObjIdList
	 * @return
	 */
	private StringList getRelatedPlantsForObjs(Context context, StringList slObjIdList) {
		StringList slPlantIdList = new StringList();
		try {
			Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_PLANT);
			Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY);

			StringList slObjSelects = new StringList(DomainConstants.SELECT_ID);

			int iRelatedObjListSize = slObjIdList.size();
			for (int i = 0; i < iRelatedObjListSize; i++) {
				String strObjectId = slObjIdList.get(i);
				DomainObject dobCurrentObj = DomainObject.newInstance(context, strObjectId);
				MapList mlRelatedPlants = dobCurrentObj.getRelatedObjects(context, // the eMatrix Context object
						relPattern.getPattern(), // Relationship pattern
						typePattern.getPattern(), // Type pattern
						slObjSelects, // Object selects
						null, // Relationship selects
						true, // get From relationships
						false, // get To relationships
						(short) 1, // the number of levels to expand, 0 equals expand all.
						null, // Object where clause
						null, // Relationship where clause
						0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
							// data available

				int iRelatedPlantsSize = mlRelatedPlants.size();
				for (int j = 0; j < iRelatedPlantsSize; j++) {
					Map<?, ?> mpPlantInfoMap = (Map<?, ?>) mlRelatedPlants.get(j);
					String strPlantId = (String) mpPlantInfoMap.get(DomainConstants.SELECT_ID);
					if (!slPlantIdList.contains(strPlantId)) {
						slPlantIdList.add(strPlantId);
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
		return slPlantIdList;
	}

	/**
	 * Method to get the data to be shown on drop zone for where used tab
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	String fetchSATSWhereUsedDropzoneData(Context context, String strJsonInput) {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strSTASId = jsonInputData.getString(DomainConstants.SELECT_ID);
			DomainObject dobSATSobj = DomainObject.newInstance(context, strSTASId);
			String strAttrSelectedItems = dobSATSobj.getAttributeValue(context,
					PGStructuredATSConstants.ATTRIBUTE_PGSATSWHEREUSEDSELECTEDITEMS);

			String strObjSelects = jsonInputData.getString(PGStructuredATSConstants.KEY_OBJ_SELECTS);
			getObjectSelectList(strObjSelects);
			iObjectSelectsSize = slObjectSelects.size();
			strLanguage = context.getSession().getLanguage();
			Map<String, MapList> mapWhereUsedObjMap = new HashMap<>();

			if (UIUtil.isNotNullAndNotEmpty(strAttrSelectedItems)) {
				StringList slWhereUsedObjList = StringUtil.split(strAttrSelectedItems, ";");
				int iListSize = slWhereUsedObjList.size();
				for (int i = 0; i < iListSize; i++) {
					String strWhereUsedObj = slWhereUsedObjList.get(i);
					StringList slParentChildList = StringUtil.split(strWhereUsedObj, ":");
					int iParentChildListSize = slParentChildList.size();
					if (iParentChildListSize == 2) {
						String strParentId = slParentChildList.get(0);
						String strChildIds = slParentChildList.get(1);

						MapList mlParentChildList = new MapList();
						DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
						Map<?, ?> mapObjInfo = dobParentObj.getInfo(context, slObjectSelects);
						Map<String, String> objParentMap = updateMapWithParentChildObjSelects(context, mapObjInfo);
						objParentMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId);
						mlParentChildList.add(objParentMap);

						addChildrenInfoToParentChildList(context, mlParentChildList, strChildIds, strParentId,
								dobParentObj);

						mapWhereUsedObjMap.put(strParentId, mlParentChildList);
					}
				}

				updateJsonArrayWithWhereUsedObjDetails(mapWhereUsedObjMap, jsonArrObjInfo);
				jsonReturnObj.add(PGStructuredATSConstants.KEY_OUTPUT, jsonArrObjInfo);
			}

			return jsonReturnObj.build().toString();

		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonReturnObj.build().toString();
		}
	}

	/**
	 * Method to add child structure to mlParentChildList
	 * 
	 * @param context
	 * @param mlParentChildList
	 * @param strChildIds
	 * @param strParentId
	 * @param dobParentObj
	 */
	private void addChildrenInfoToParentChildList(Context context, MapList mlParentChildList, String strChildIds,
			String strParentId, DomainObject dobParentObj) {
		try {
			if (dobParentObj.isKindOf(context, PGStructuredATSConstants.TYPE_FORMULATION_PART)) {
				addFOPChildrenInfoToList(context, mlParentChildList, strChildIds, strParentId);
			} else if (dobParentObj.isKindOf(context, PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART)) {
				addBOMChildsToInfoList(context, mlParentChildList, strChildIds, strParentId,
						PGStructuredATSConstants.VALUE_BOM);
			} else {
				addBOMChildsToInfoList(context, mlParentChildList, strChildIds, strParentId,
						PGStructuredATSConstants.VALUE_ALTERNATE);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to add FOP child structure to mlParentChildList
	 * 
	 * @param context
	 * @param mlParentChildList
	 * @param strChildIds
	 * @param strParentId
	 */
	private void addFOPChildrenInfoToList(Context context, MapList mlParentChildList, String strChildIds,
			String strParentId) {
		try {
			StringList slObjSelects = new StringList(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PROCESS);
			String[] strOIDArray = new String[1];
			strOIDArray[0] = strParentId;
			MapList mpFormulationProcessList = DomainObject.getInfo(context, strOIDArray, slObjSelects);
			Map<?, ?> mpObjFPMap = (Map<?, ?>) mpFormulationProcessList.get(0);
			String strFormulationProcessId = (String) mpObjFPMap
					.get(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PROCESS);

			String strHierarchy = strParentId + "," + strFormulationProcessId;
			DomainObject dobFProcessObj = DomainObject.newInstance(context, strFormulationProcessId);
			Map<?, ?> mapObjInfo = dobFProcessObj.getInfo(context, slObjectSelects);
			Map<String, String> objFProcessMap = updateMapWithParentChildObjSelects(context, mapObjInfo);
			objFProcessMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strHierarchy);
			mlParentChildList.add(objFProcessMap);

			addPhaseChildrenInfoToList(context, mlParentChildList, strChildIds, strHierarchy);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}

	}

	/**
	 * Method to add FOP Phase structure to mlParentChildList
	 * 
	 * @param context
	 * @param mlParentChildList
	 * @param strChildIds
	 * @param strHierarchy
	 */
	private void addPhaseChildrenInfoToList(Context context, MapList mlParentChildList, String strChildIds,
			String strHierarchy) {
		try {
			StringList slPhaseInfoList = StringUtil.split(strChildIds, "|");
			int iListSize = slPhaseInfoList.size();
			for (int i = 0; i < iListSize; i++) {
				String strPhaseInfo = slPhaseInfoList.get(i);
				StringList slPhaseChildList = StringUtil.split(strPhaseInfo, "-");
				int iPhaseChildListSize = slPhaseChildList.size();
				if (iPhaseChildListSize == 2) {
					String strPhaseId = slPhaseChildList.get(0);
					String strBOMChildIds = slPhaseChildList.get(1);

					String strNewHierarchy = strHierarchy + "," + strPhaseId;
					DomainObject dobPhaseObj = DomainObject.newInstance(context, strPhaseId);
					Map<?, ?> mapObjInfo = dobPhaseObj.getInfo(context, slObjectSelects);
					Map<String, String> mpPhaseInfoMap = updateMapWithParentChildObjSelects(context, mapObjInfo);
					mpPhaseInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strNewHierarchy);
					mlParentChildList.add(mpPhaseInfoMap);

					addBOMChildsToInfoList(context, mlParentChildList, strBOMChildIds, strNewHierarchy,
							PGStructuredATSConstants.VALUE_BOM);

				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}
	}

	/**
	 * Method to add leaf level BOM children info to mlParentChildList
	 * 
	 * @param context
	 * @param mlParentChildList
	 * @param strBOMChildIds
	 * @param strNewHierarchy
	 * @param strOperationValue
	 */
	private void addBOMChildsToInfoList(Context context, MapList mlParentChildList, String strBOMChildIds,
			String strNewHierarchy, String strOperationValue) {
		try {
			StringList slBOMChildInfoList = StringUtil.split(strBOMChildIds, ",");
			int iListSize = slBOMChildInfoList.size();
			Map<Object, Object> mpBOMChildsnSubstituesInfoMap = separateBOMnSubstituteInfo(iListSize,
					slBOMChildInfoList);
			StringList slBOMChildIdList = (StringList) mpBOMChildsnSubstituesInfoMap
					.get(PGStructuredATSConstants.VALUE_BOM);

			int iBOMChildListSize = slBOMChildIdList.size();
			for (int i = 0; i < iBOMChildListSize; i++) {
				String strBOMChildId = slBOMChildIdList.get(i);
				DomainObject dobBOMChildObj = DomainObject.newInstance(context, strBOMChildId);
				Map<?, ?> mapObjInfo = dobBOMChildObj.getInfo(context, slObjectSelects);
				Map<String, String> mpBOMChildInfoMap = updateMapWithParentChildObjSelects(context, mapObjInfo);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strNewHierarchy + "," + strBOMChildId);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_OPERATION, strOperationValue);
				mlParentChildList.add(mpBOMChildInfoMap);
			}

			Map<String, String> mpSubstituesInfoMap = (Map<String, String>) mpBOMChildsnSubstituesInfoMap
					.get(PGStructuredATSConstants.VALUE_SUBSTITUTE);
			if (mpSubstituesInfoMap != null && !mpSubstituesInfoMap.isEmpty()) {
				for (Map.Entry<String, String> entry : mpSubstituesInfoMap.entrySet()) {
					String strSubId = entry.getKey();
					String strPrimaryIds = entry.getValue();

					DomainObject dobSubObj = DomainObject.newInstance(context, strSubId);
					Map<?, ?> mpInputObjInfo = dobSubObj.getInfo(context, slObjectSelects);
					Map<String, String> mpSubInfoMap = updateMapWithParentChildObjSelects(context, mpInputObjInfo);
					mpSubInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strNewHierarchy + "," + strSubId);
					mpSubInfoMap.put(PGStructuredATSConstants.KEY_OPERATION, PGStructuredATSConstants.VALUE_SUBSTITUTE);
					mpSubInfoMap.put(PGStructuredATSConstants.KEY_PRIMARY, strPrimaryIds);
					mlParentChildList.add(mpSubInfoMap);
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED, e);
		}

	}

	/**
	 * Method to get Substitutes and BOM child details separately
	 * 
	 * @param iListSize
	 * @param slFBOMChildInfoList
	 * @return
	 */
	private Map<Object, Object> separateBOMnSubstituteInfo(int iListSize, StringList slBOMChildInfoList) {
		Map<Object, Object> mpBOMChildsnSubstituesInfoMap = new HashMap<>();
		Map<String, String> mpSubstituesInfoMap = new HashMap<>();
		StringList slBOMChildIdList = new StringList();
		StringList slSubsChildIdList = new StringList();
		for (int i = 0; i < iListSize; i++) {
			String strBOMChildId = slBOMChildInfoList.get(i);
			if (strBOMChildId.contains(PGStructuredATSConstants.SEPARATOR_FOR_SUBSTITUTES)) {
				slSubsChildIdList.add(strBOMChildId);
			} else {
				slBOMChildIdList.add(strBOMChildId);
			}
		}

		int iSubListSize = slSubsChildIdList.size();
		for (int j = 0; j < iSubListSize; j++) {
			String strSubInfo = slSubsChildIdList.get(j);
			StringList slSubInfoList = StringUtil.split(strSubInfo, PGStructuredATSConstants.SEPARATOR_FOR_SUBSTITUTES);
			if (slSubInfoList.size() == 2) {
				String strSubId = slSubInfoList.get(1);
				String strPrimaryId = slSubInfoList.get(0);
				if (mpSubstituesInfoMap.containsKey(strSubId)) {
					String strExistingPrimaryId = mpSubstituesInfoMap.get(strSubId);
					mpSubstituesInfoMap.put(strSubId, strExistingPrimaryId + "," + strPrimaryId);
				} else {
					mpSubstituesInfoMap.put(strSubId, strPrimaryId);
				}
			}
		}

		mpBOMChildsnSubstituesInfoMap.put(PGStructuredATSConstants.VALUE_BOM, slBOMChildIdList);
		mpBOMChildsnSubstituesInfoMap.put(PGStructuredATSConstants.VALUE_SUBSTITUTE, mpSubstituesInfoMap);

		return mpBOMChildsnSubstituesInfoMap;
	}

}
