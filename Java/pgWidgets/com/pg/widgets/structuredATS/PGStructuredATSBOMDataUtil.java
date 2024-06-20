package com.pg.widgets.structuredats;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
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
import matrix.util.StringList;

public class PGStructuredATSBOMDataUtil {

	StringList slObjectSelects = null;
	StringList slRelAttributeList = null;
	StringList slProcessedFBOMSubRelIdList = null;
	Map<String,String> mpAttributeDisplayNameMap = null;
	String strWhereId = "id == ";
	String strLanguage = "en";
	String strSTASId = "";
	String strSelectRelatedATSOperationId = "";
	String strSelectRelatedObjsOfSATS = "";
	int iObjectSelectsSize = 0;
	int iRelAttributeListSize = 0;
	PGStructuredATSWhereUsedUtil objSATSWhereUsedUtil = new PGStructuredATSWhereUsedUtil();
	
	private static final Logger logger = Logger.getLogger(PGStructuredATSBOMDataUtil.class.getName());
		
	/**
	 * Method to get the BOM data along with related SATS data
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	String fetchSATSBOMData(Context context, String strJsonInput) {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			strSTASId = jsonInputData.getString(DomainConstants.SELECT_ID);
			DomainObject dobSATSobj = DomainObject.newInstance(context, strSTASId);
			String strAttrSelectedItems = dobSATSobj.getAttributeValue(context,
					PGStructuredATSConstants.ATTRIBUTE_PGSATSWHEREUSEDSELECTEDITEMS);

			slProcessedFBOMSubRelIdList = new StringList();
			updateObjectSelectList();
			updateRelAttributeList();
			updateAttributeDisplayValMap();
			strLanguage = context.getSession().getLanguage();
			
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

						DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
						Map<?, ?> mapObjInfo = dobParentObj.getInfo(context, slObjectSelects);
						Map<String, String> mpParentInfoMap = updateMapWithObjSelectAndValues(context, mapObjInfo, PGStructuredATSConstants.PREFIX_CTX_FOP);
						mpParentInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strParentId);
						JsonObjectBuilder jsonParentObjInfo = objSATSWhereUsedUtil.getProcessJsonObject(mpParentInfoMap);
						jsonArrObjInfo.add(jsonParentObjInfo);
						
						addChildrenInfoToJsonArray(context, strChildIds, jsonArrObjInfo, dobParentObj, mapObjInfo);

					}
				}
				
				jsonReturnObj.add(PGStructuredATSConstants.KEY_OUTPUT, jsonArrObjInfo);
			}

			return jsonReturnObj.build().toString();

		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonReturnObj.build().toString();
		}
	}

	/**
	 * Method to fetch leaf level child info for APP, FOP and RMP
	 * @param context
	 * @param strChildIds
	 * @param jsonArrObjInfo
	 * @param dobParentObj
	 * @param mapObjInfo
	 */
	private void addChildrenInfoToJsonArray(Context context, String strChildIds,
			JsonArrayBuilder jsonArrObjInfo, DomainObject dobParentObj, Map<?, ?> mapObjInfo) {
		try {
			if (dobParentObj.isKindOf(context, PGStructuredATSConstants.TYPE_FORMULATION_PART)) {
				strSelectRelatedATSOperationId = PGStructuredATSConstants.SELECT_ATS_OPERATION_RELID_FOR_FOP;
				strSelectRelatedObjsOfSATS = PGStructuredATSConstants.SELECT_RELATED_OBJS_FOR_SATS_FOR_FOP;
				
				String strParentId = (String) mapObjInfo.get(DomainConstants.SELECT_ID);
				addFOPChildrenInfoToJsonArray(context, jsonArrObjInfo, strChildIds, strParentId);
			} else {
				strSelectRelatedATSOperationId = PGStructuredATSConstants.SELECT_ATS_OPERATION_RELID;
				strSelectRelatedObjsOfSATS = PGStructuredATSConstants.SELECT_RELATED_OBJS_FOR_SATS;
				
				addAPPnRMPPChildrenInfoToJsonArray(context, jsonArrObjInfo, strChildIds, mapObjInfo);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
		
	}

	/**
	 * Method to add APP, RMP leaf level child structure to JsonArrayBuilder
	 * @param context
	 * @param jsonArrObjInfo
	 * @param strChildIds
	 * @param mapObjInfo
	 */
	private void addAPPnRMPPChildrenInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo,
			String strChildIds, Map<?, ?> mapObjInfo) {
		try {
			String strParentType = (String) mapObjInfo.get(DomainConstants.SELECT_TYPE);
			String strParentId = (String) mapObjInfo.get(DomainConstants.SELECT_ID);
			String strHeirarchy = strParentId;
			addBOMChildsInfoToJsonArray(context, jsonArrObjInfo, strChildIds, strHeirarchy, strParentId, strParentType);
		
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
		
	}

	/**
	 * Method to add FOP leaf level child structure to JsonArrayBuilder
	 * @param context
	 * @param jsonArrObjInfo
	 * @param strChildIds
	 * @param strParentName
	 * @param strParentId
	 */
	private void addFOPChildrenInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo, String strChildIds, String strParentId) {
		try {
		StringList slObjSelects = new StringList(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PROCESS);
		String[] strOIDArray = new String[1];
		strOIDArray[0] = strParentId;
		MapList mpFormulationProcessList = DomainObject.getInfo(context, strOIDArray, slObjSelects);
		Map<?,?> mpObjFPMap = (Map<?, ?>) mpFormulationProcessList.get(0);
		String strFormulationProcessId = (String) mpObjFPMap.get(PGStructuredATSConstants.SELECT_RELATED_FORMULATION_PROCESS);

		String strHierarchy = strParentId + "," + strFormulationProcessId;
		
		DomainObject dobFProcessObj = DomainObject.newInstance(context, strFormulationProcessId);
		Map<?, ?> mapObjInfo = dobFProcessObj.getInfo(context, slObjectSelects);
		Map<String, String> mpFProcessInfoMap = updateMapWithObjSelectAndValues(context, mapObjInfo, PGStructuredATSConstants.PREFIX_CTX_FOP);
		mpFProcessInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strHierarchy);
		JsonObjectBuilder jsonFProcessObjInfo = objSATSWhereUsedUtil.getProcessJsonObject(mpFProcessInfoMap);
		jsonArrObjInfo.add(jsonFProcessObjInfo);
		
		addPhaseChildrenInfoToJsonArray(context, jsonArrObjInfo, strChildIds, strHierarchy);
		
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
		
	}

	/**
	 * Method to add FOP Phase structure for leaf level parts to JsonArrayBuilder
	 * @param context
	 * @param jsonArrObjInfo
	 * @param strChildIds
	 * @param strHierarchy
	 */
	private void addPhaseChildrenInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo, String strChildIds,
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

				DomainObject dobPhaseObj = DomainObject.newInstance(context, strPhaseId);
				Map<?,?> mpPhaseInfoMap = dobPhaseObj.getInfo(context, slObjectSelects);
				String strPhaseType = (String) mpPhaseInfoMap.get(DomainConstants.SELECT_TYPE);
				String strPhaseHierarchy = strHierarchy + "," + strPhaseId;

				Map<String, String> mpFormulationPhaseInfoMap = updateMapWithObjSelectAndValues(context, mpPhaseInfoMap, PGStructuredATSConstants.PREFIX_CTX_FOP);
				mpFormulationPhaseInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strPhaseHierarchy);
				JsonObjectBuilder jsonPhaseObjInfo = objSATSWhereUsedUtil.getProcessJsonObject(mpFormulationPhaseInfoMap);
				jsonArrObjInfo.add(jsonPhaseObjInfo);
				
				addBOMChildsInfoToJsonArray(context, jsonArrObjInfo, strBOMChildIds, strPhaseHierarchy, strPhaseId, strPhaseType);

			}
		}
		
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
		
	}

	/**
	 * Method to add leaf level BOM children info to
	 * @param context
	 * @param jsonArrObjInfo
	 * @param strBOMChildIds
	 * @param strPhaseHierarchy
	 * @param strParentId
	 * @param strParentType
	 */
	private void addBOMChildsInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo, String strBOMChildIds,
			String strNewHierarchy, String strParentId, String strParentType) {
		try {
		StringList slBOMChildInfoList = StringUtil.split(strBOMChildIds, ",");
		int iListSize = slBOMChildInfoList.size();
		Map<Object, Object> mpBOMChildsnSubstituesInfoMap = separateBOMnSubstituteInfo(iListSize, slBOMChildInfoList);

		StringList slBOMChildIdList =  (StringList) mpBOMChildsnSubstituesInfoMap.get(PGStructuredATSConstants.VALUE_BOM);
		addBOMChildnSATSInfoToJsonArray(context, jsonArrObjInfo, strNewHierarchy, slBOMChildIdList, strParentId, strParentType);
		
		Map<?, ?> mpSubstituesInfoMap =  (Map<?, ?>) mpBOMChildsnSubstituesInfoMap.get(PGStructuredATSConstants.VALUE_SUBSTITUTE);
		if(PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strParentType)) {
			addFBOMSubstituesInfoToJsonArray(context, jsonArrObjInfo, mpSubstituesInfoMap, strParentId, strNewHierarchy, strParentType);
		} else {
			addEBOMSubstituesInfoToJsonArray(context, jsonArrObjInfo, mpSubstituesInfoMap, strParentId, strNewHierarchy, strParentType);
		}
		
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
		
	}

	/**
	 * Method to add the FBOM Substitutes info to Json Array
	 * 
	 * @param context
	 * @param jsonArrObjInfo
	 * @param mpSubstituesInfoMap
	 * @param strParentId
	 * @param strNewHierarchy
	 * @param strParentType
	 */
	private void addFBOMSubstituesInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo,
			Map<?, ?> mpSubstituesInfoMap, String strParentId, String strNewHierarchy, String strParentType) {
		try {
			for (Map.Entry<?, ?> entry : mpSubstituesInfoMap.entrySet()) {
				String strPrimaryId = (String) entry.getKey();
				String strSubIds = (String) entry.getValue();
				
				DomainObject dobPrimaryObj = DomainObject.newInstance(context, strPrimaryId);
				String strPrimaryObjName = dobPrimaryObj.getInfo(context, DomainConstants.SELECT_NAME);

				StringList slConnectionIds = getChildConnectionIds(context, dobPrimaryObj, strParentType, strParentId);
				int iConnListSize = slConnectionIds.size();
				StringList slSubsChildIdList = StringUtil.split(strSubIds, ",");

				int iSubListSize = slSubsChildIdList.size();
				for (int i = 0; i < iSubListSize; i++) {
					String strSubChildId = slSubsChildIdList.get(i);
					Map<String, Object> mpFBOMSubRelParentSubChildIdsMap = new HashMap<>();
					for (int j = 0; j < iConnListSize; j++) {
						String strBOMRelId = slConnectionIds.get(j);
						updateFBOMSubRelIdMap(context, strBOMRelId, strSubChildId, mpFBOMSubRelParentSubChildIdsMap);
					}

					if (!mpFBOMSubRelParentSubChildIdsMap.isEmpty()) {
						addSATSRelatedObjInfoToJsonArrayForFBOMSub(context,mpFBOMSubRelParentSubChildIdsMap,jsonArrObjInfo,strPrimaryObjName,strNewHierarchy);
					}
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}

	}

	/**
	 * Fetch SATS details for 'FBOM Substitutes' : Common method to insert left and right hand side leaf level Json objects only for 'FBOM Substitutes'
	 * @param context
	 * @param mpFBOMSubRelParentSubChildIdsMap
	 * @param jsonArrObjInfo
	 * @param strPrimaryObjName
	 * @param strPrimaryHierarchy
	 */
	private void addSATSRelatedObjInfoToJsonArrayForFBOMSub(Context context,
			Map<String, Object> mpFBOMSubRelParentSubChildIdsMap, JsonArrayBuilder jsonArrObjInfo,
			String strPrimaryObjName, String strPrimaryHierarchy) {
		try {
			String strSelectRelatedATSContextRelId = PGStructuredATSConstants.SELECT_ATS_CONTEXT_RELID_FOR_FBOM_SUBSTITUTES;
			for (Map.Entry<String, Object> entry : mpFBOMSubRelParentSubChildIdsMap.entrySet()) {
				String strFBOMSubRelId =  entry.getKey();
				Map<?,?> mpSubGrpsPrimaryQtyMap = (Map<?, ?>) entry.getValue();

				StringList slRelSelects = new StringList();
				slRelSelects.add(strSelectRelatedATSContextRelId);
				
				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strFBOMSubRelId;
				MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
				Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlConnectionAttrList.get(0);
				
				Object objATSContextRelId =  mpObjInfoMap.get(strSelectRelatedATSContextRelId);
				StringList slATSCtxRelIds = getStringListFromObject(objATSContextRelId);
				StringList slATSContextRelIdList = getRelatedATSContextIdsForSATSObj(context, slATSCtxRelIds);

				if(slATSContextRelIdList == null || slATSContextRelIdList.isEmpty()) {
					addFBOMSubInfoToJsonArray(context, jsonArrObjInfo, mpSubGrpsPrimaryQtyMap, strPrimaryObjName, strPrimaryHierarchy, strFBOMSubRelId);
				} else {
					Map<String,String> mpRelatedSATSObjInfoMap = getRelatedGroupObjIdsForFBOMSub(context, slATSContextRelIdList);
					StringBuilder sbPrimaryHierarchy = new StringBuilder();
					sbPrimaryHierarchy.append(strPrimaryHierarchy).append("|").append(strPrimaryObjName).append("|").append(slATSContextRelIdList.get(0));
					addRelatedSATSInfoFromATSContextRelIdForFBOMSub(context, jsonArrObjInfo, mpSubGrpsPrimaryQtyMap, sbPrimaryHierarchy.toString(), strFBOMSubRelId, mpRelatedSATSObjInfoMap);
				}
				
				slProcessedFBOMSubRelIdList.add(strFBOMSubRelId);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
	}

	/**
	 * Method to create leaf level left and right side data for FBOM Substitutes
	 * @param context
	 * @param jsonArrObjInfo
	 * @param mpSubGrpsPrimaryQtyMap
	 * @param strPrimaryHierarchy
	 * @param strFBOMSubRelId
	 * @param mpRelatedSATSObjInfoMap
	 */
	private void addRelatedSATSInfoFromATSContextRelIdForFBOMSub(Context context, JsonArrayBuilder jsonArrObjInfo,
			Map<?,?> mpSubGrpsPrimaryQtyMap, String strPrimaryHierarchy,
			String strFBOMSubRelId, Map<String, String> mpRelatedSATSObjInfoMap) {
		try {
			MapList mlFBOMRelIdObjIdList = (MapList) mpSubGrpsPrimaryQtyMap.get(PGStructuredATSConstants.KEY_SOURCE_ID);
			String strPrimaryQty = (String) mpSubGrpsPrimaryQtyMap.get(PGStructuredATSConstants.DISP_PRIMARY_QUANTITY);
			
			StringList slPrimaryHierarchyInfoList = StringUtil.split(strPrimaryHierarchy, "|");
			String strHierarchy = slPrimaryHierarchyInfoList.get(0);
			String strPrimaryObjName = slPrimaryHierarchyInfoList.get(1);
			String strATSContextRelId = slPrimaryHierarchyInfoList.get(2);
								
			StringList slGroupIdList = getRelatedGroupIdList(mpRelatedSATSObjInfoMap);
			StringList slRelatedRMToSATSIdList = new StringList();
			slRelatedRMToSATSIdList.addAll(slGroupIdList);
			
			int iNoOfRows = balanceSourceAndTargetListSize(mlFBOMRelIdObjIdList, slRelatedRMToSATSIdList);
									
			for(int i=0;i<iNoOfRows;i++) {
				Map<?,?> mpObjRelIdMap = (Map<?, ?>) mlFBOMRelIdObjIdList.get(i);
				String strParentSubChildId = (String) mpObjRelIdMap.get(DomainConstants.SELECT_ID);
				
				Map<String, String> mpBOMChildInfoMap  = null;
				if(strParentSubChildId.startsWith(PGStructuredATSConstants.PREFIX_CTX_FOP_ID)) {
					mpBOMChildInfoMap  = new HashMap<>();
					mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strHierarchy+","+strParentSubChildId);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+DomainConstants.SELECT_ID, strParentSubChildId);
				} else {					
					DomainObject dobSubChildObj = DomainObject.newInstance(context, strParentSubChildId);
					Map<?, ?> mapObjInfo = dobSubChildObj.getInfo(context, slObjectSelects);
					String strChildName = (String) mapObjInfo.get(DomainConstants.SELECT_NAME);
					
					mpBOMChildInfoMap = updateMapWithObjSelectAndValues(context, mapObjInfo, PGStructuredATSConstants.PREFIX_CTX_FOP);

					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_AFFECTED_PART, strChildName);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strHierarchy+","+strParentSubChildId);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_PRIMARY, strPrimaryObjName);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_OPERATION,PGStructuredATSConstants.VALUE_SUBSTITUTE);

					String strParentSubFBOMRelId = (String) mpObjRelIdMap.get(PGStructuredATSConstants.KEY_REL_ID);
					updateRelAttributesForFBOMSubstitutes(context, strParentSubFBOMRelId, strPrimaryQty, mpBOMChildInfoMap);

				}
								
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_ATS_CONTEXT_RELID, strATSContextRelId);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_RELATIONSHIP_ID, strFBOMSubRelId);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_RELATIONSHIP_TYPE, PGStructuredATSConstants.RELATIONSHIP_FBOM_SUBSTITUTE);
				
				String strRelatedRMPId = slRelatedRMToSATSIdList.get(i);
				if(UIUtil.isNotNullAndNotEmpty(strRelatedRMPId)) {
					String strATSOprRelId = mpRelatedSATSObjInfoMap.get(strRelatedRMPId);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_ATS_OPERATION_RELID, strATSOprRelId);

					StringList slRelSelects = new StringList();
					slRelSelects.add(DomainConstants.SELECT_FROM_ID);
					slRelSelects.addAll(slRelAttributeList);
					
					String[] strRelIdArray = new String[1];
					strRelIdArray[0] = strATSOprRelId;
					MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);

					Map<?, ?> mpObjInfoSATSMap = (Map<?, ?>) mlConnectionAttrList.get(0);
					for(int j=0;j<iRelAttributeListSize;j++) {
						String strRelAttrSelect = slRelAttributeList.get(j);
						String strRelAttrVal = (String) mpObjInfoSATSMap.get(strRelAttrSelect);
						String strAttributeName = getAttributeNameFromSelect(strRelAttrSelect);
						mpBOMChildInfoMap.put(strAttributeName, strRelAttrVal);

					}
					
					String strParentSubId = (String) mpObjInfoSATSMap.get(DomainConstants.SELECT_FROM_ID);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_PARENT_SUB_ID, strParentSubId);
					
					DomainObject dobRelatedObj = DomainObject.newInstance(context, strRelatedRMPId);
					Map<?, ?> mapObjInfo = dobRelatedObj.getInfo(context, slObjectSelects);
					Map<String, String> mpSATSRelatedObjMap = updateMapWithObjSelectAndValues(context, mapObjInfo, "");

					for (Map.Entry<String, String> entry : mpSATSRelatedObjMap.entrySet()) {
						String strKey = entry.getKey();
						String strValue = entry.getValue();
						if(DomainConstants.SELECT_ATTRIBUTE_TITLE.equals(strKey)) {
							String strAttrName = getAttributeNameFromSelect(strKey);
							mpBOMChildInfoMap.put(strAttrName, strValue);
						} else {
							mpBOMChildInfoMap.put(strKey, strValue);
						}
						
					}
				}

				StringList slCurrentGroupObjIdList = new StringList();
				slCurrentGroupObjIdList.addAll(slGroupIdList);
				
				if(slCurrentGroupObjIdList.contains(strRelatedRMPId)) {
					slCurrentGroupObjIdList.remove(strRelatedRMPId);
				}

				String strGroupRelInfo = getGroupRelInfo(strParentSubChildId, slCurrentGroupObjIdList);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_GROUP_REL, strGroupRelInfo);
				
				JsonObjectBuilder jsonObjInfo = objSATSWhereUsedUtil.getProcessJsonObject(mpBOMChildInfoMap);
				jsonArrObjInfo.add(jsonObjInfo);

			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
		
	}

	/**
	 * Method to balance the source and target lists
	 * @param mlFBOMRelIdObjIdList
	 * @param slRelatedRMToSATSIdList
	 * @return
	 */
	private int balanceSourceAndTargetListSize(MapList mlFBOMRelIdObjIdList, StringList slRelatedRMToSATSIdList) {
		int iNoOfRows = 0;
		int iParentSubChildIdsSize = mlFBOMRelIdObjIdList.size();
		int iRelatedRMTIDsSize = slRelatedRMToSATSIdList.size();
		
		if(iParentSubChildIdsSize > iRelatedRMTIDsSize) {
			int iDiff = iParentSubChildIdsSize - iRelatedRMTIDsSize;
			for(int i=0;i<iDiff;i++) {
				slRelatedRMToSATSIdList.add("");
			}
			iNoOfRows = iParentSubChildIdsSize;
		} else if(iRelatedRMTIDsSize > iParentSubChildIdsSize) {
			int iDiffIndex = iRelatedRMTIDsSize - iParentSubChildIdsSize;
			for(int i=0;i<iDiffIndex;i++) {
				String strDummyId = PGStructuredATSConstants.PREFIX_CTX_FOP_ID + Integer.toString(i);
				Map<String,String> mpObjInfoMap = new HashMap<>();
				mpObjInfoMap.put(DomainConstants.SELECT_ID, strDummyId);
				mpObjInfoMap.put(PGStructuredATSConstants.KEY_REL_ID, "");
				mlFBOMRelIdObjIdList.add(mpObjInfoMap);
			}
			iNoOfRows = iRelatedRMTIDsSize;
		} else {
			iNoOfRows = iParentSubChildIdsSize; //or it can be iRelatedRMTIDsSize as well
		}
		return iNoOfRows;
	}

	/**
	 * Get the Object ids list of related RMPs of SATS
	 * @param mpRelatedSATSObjInfoMap
	 * @return
	 */
	private StringList getRelatedGroupIdList(Map<String, String> mpRelatedSATSObjInfoMap) {
		StringList slGroupIdList = new StringList();
		for (Map.Entry<String, String> entry : mpRelatedSATSObjInfoMap.entrySet()) {
			String strRelatedRMId = entry.getKey();
			slGroupIdList.add(strRelatedRMId);
		}
		return slGroupIdList;
	}

	/**
	 * Method to get the related sibling RMPs under same SATS object and their ATS
	 * Operation rels
	 * 
	 * @param context
	 * @param slATSContextRelIdList
	 * @return
	 */
	private Map<String, String> getRelatedGroupObjIdsForFBOMSub(Context context, StringList slATSContextRelIdList) {
		Map<String, String> mpRelatedSATSObjInfoMap = new HashMap<>();
		try {
			String strATSCtxRelId = slATSContextRelIdList.get(0);
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strATSCtxRelId;
			MapList mlATSOperationRelIdsList = DomainRelationship.getInfo(context, strRelIdArray,
					new StringList(PGStructuredATSConstants.SELECT_ATS_OPERATION_RELID_FOR_FOP));
			Map<?, ?> mpATSOperationIdMap = (Map<?, ?>) mlATSOperationRelIdsList.get(0);
			Object objATSOperationRelId = mpATSOperationIdMap
					.get(PGStructuredATSConstants.SELECT_ATS_OPERATION_RELID_FOR_FOP);
			StringList slATSOperationRelIdList = getStringListFromObject(objATSOperationRelId);
			if (slATSOperationRelIdList != null) {
				int iObjlistSize = slATSOperationRelIdList.size();
				for (int i = 0; i < iObjlistSize; i++) {
					String strATSOperationRelId = slATSOperationRelIdList.get(i);
					String[] strATSOprRelIdArray = new String[1];
					strATSOprRelIdArray[0] = strATSOperationRelId;
					MapList mlRelatedObjList = DomainRelationship.getInfo(context, strATSOprRelIdArray,
							new StringList(DomainConstants.SELECT_TO_ID));
					Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlRelatedObjList.get(0);
					String strToId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);
					mpRelatedSATSObjInfoMap.put(strToId, strATSOperationRelId);
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}

		return mpRelatedSATSObjInfoMap;
	}
	
	/**
	 * Method to add Parent Sub childs for FBOM Sub rel which dont have related SATS object
	 * @param context
	 * @param jsonArrObjInfo
	 * @param mpSubGrpsPrimaryQtyMap
	 * @param strPrimaryObjName
	 * @param strPrimaryHierarchy
	 */
	private void addFBOMSubInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo,
			Map<?,?> mpSubGrpsPrimaryQtyMap, String strPrimaryObjName, String strPrimaryHierarchy, String strFBOMSubRelId) {
		try {
			MapList mlFBOMRelIdObjIdList = (MapList) mpSubGrpsPrimaryQtyMap.get(PGStructuredATSConstants.KEY_SOURCE_ID);
			String strPrimaryQty = (String) mpSubGrpsPrimaryQtyMap.get(PGStructuredATSConstants.DISP_PRIMARY_QUANTITY);
			int iListSize = mlFBOMRelIdObjIdList.size();
			for(int i=0;i<iListSize;i++) {
				Map<?,?> mpObjRelIdMap = (Map<?, ?>) mlFBOMRelIdObjIdList.get(i);
				String strParentSubFBOMRelId = (String) mpObjRelIdMap.get(PGStructuredATSConstants.KEY_REL_ID);
				
				String strSubChildId = (String) mpObjRelIdMap.get(DomainConstants.SELECT_ID);
				DomainObject dobSubChildObj = DomainObject.newInstance(context, strSubChildId);
				Map<?, ?> mapObjInfo = dobSubChildObj.getInfo(context, slObjectSelects);
				String strChildName = (String) mapObjInfo.get(DomainConstants.SELECT_NAME);

				Map<String, String> mpBOMChildInfoMap = updateMapWithObjSelectAndValues(context, mapObjInfo, PGStructuredATSConstants.PREFIX_CTX_FOP);

				mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_AFFECTED_PART, strChildName);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strPrimaryHierarchy+","+strSubChildId);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_PRIMARY, strPrimaryObjName);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_OPERATION,PGStructuredATSConstants.VALUE_SUBSTITUTE);
				
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_RELATIONSHIP_ID, strFBOMSubRelId);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_RELATIONSHIP_TYPE, PGStructuredATSConstants.RELATIONSHIP_FBOM_SUBSTITUTE);
				
				updateRelAttributesForFBOMSubstitutes(context, strParentSubFBOMRelId, strPrimaryQty, mpBOMChildInfoMap);
				
				JsonObjectBuilder jsonObjInfo = objSATSWhereUsedUtil.getProcessJsonObject(mpBOMChildInfoMap);
				jsonArrObjInfo.add(jsonObjInfo);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
	}

	/**
	 * Method to update the relationship (FBOM) attributes for FBOM Substitutes
	 * 
	 * @param context
	 * @param strParentSubFBOMRelId
	 * @param strPrimaryQty
	 * @param mpBOMChildInfoMap
	 */
	private void updateRelAttributesForFBOMSubstitutes(Context context, String strParentSubFBOMRelId,
			String strPrimaryQty, Map<String, String> mpBOMChildInfoMap) {
		try {
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strParentSubFBOMRelId;
			MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strRelIdArray, slRelAttributeList);

			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlConnectionAttrList.get(0);
			for (int j = 0; j < iRelAttributeListSize; j++) {
				String strRelAttrSelect = slRelAttributeList.get(j);
				String strRelAttrVal = (String) mpObjInfoMap.get(strRelAttrSelect);
				if (mpAttributeDisplayNameMap.containsKey(strRelAttrSelect)) {
					String strDisplayValue = mpAttributeDisplayNameMap.get(strRelAttrSelect);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP + strDisplayValue, strRelAttrVal);
				} else {
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP + strRelAttrSelect, strRelAttrVal);
				}

			}

			String strMinValue = (String) mpObjInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_MIN_FOP);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP + PGStructuredATSConstants.DISP_MIN_PER, strMinValue);
			String strMaxValue = (String) mpObjInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_MAX_FOP);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP + PGStructuredATSConstants.DISP_MAX_PER, strMaxValue);

			String strQtyValue = (String) mpObjInfoMap.get(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP + PGStructuredATSConstants.DISP_SUB_QUANTITY, strQtyValue);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP + PGStructuredATSConstants.DISP_PRIMARY_QUANTITY, strPrimaryQty);
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
	}

	/**
	 * Method to get the related 'FBOM Substitutes' rel id
	 * @param context
	 * @param strBOMRelId
	 * @param mpFBOMSubRelParentSubChildIdsMap
	 * @return
	 */
	private void updateFBOMSubRelIdMap(Context context, String strBOMRelId, String strSubChildId, Map<String,Object> mpFBOMSubRelParentSubChildIdsMap) {
		try {
		StringList slRelSelects = new StringList(2);
		slRelSelects.add(PGStructuredATSConstants.SELECT_FBOM_SUB_RELID);
		slRelSelects.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		String[] strRelIdArray = new String[1];
		strRelIdArray[0] = strBOMRelId;
		MapList mlFBOMSubRelIdList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
		Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlFBOMSubRelIdList.get(0);
		Object objFBOMSubRelObj =  mpObjInfoMap.get(PGStructuredATSConstants.SELECT_FBOM_SUB_RELID);
		StringList slFBOMSubRelIdList = getStringListFromObject(objFBOMSubRelObj);
		
		if(slFBOMSubRelIdList != null) {
			String strSelectRelatedFBOMSubPartRelIds = PGStructuredATSConstants.SELECT_FBOM_RELIDS_FROM_PARENT_SUB;
			int iFBOMSubListSize = slFBOMSubRelIdList.size();
			for(int i=0; i<iFBOMSubListSize; i++) {
				String strFBOMSubRelId = slFBOMSubRelIdList.get(i);
				String[] strFBOMSubArray = new String[1];
				strFBOMSubArray[0] = strFBOMSubRelId;
				MapList mlFBOMSubRelIds = DomainRelationship.getInfo(context, strFBOMSubArray, new StringList(strSelectRelatedFBOMSubPartRelIds));
				Map<?, ?> mpFBOMSubRelId= (Map<?, ?>) mlFBOMSubRelIds.get(0);
				Object objRelatedFBOMRelIds=  mpFBOMSubRelId.get(strSelectRelatedFBOMSubPartRelIds);
				StringList slFBOMRelIdList = getStringListFromObject(objRelatedFBOMRelIds);
				
				if(slFBOMRelIdList != null && !slFBOMRelIdList.isEmpty()) {
					Map<String, Object> mpFBOMRelIdObjIdMap = getRelatedToSideObjForFBOM(context, slFBOMRelIdList);
					StringList slFBOMSubPartIds = (StringList) mpFBOMRelIdObjIdMap.get(DomainConstants.SELECT_ID);
					
					if(!slProcessedFBOMSubRelIdList.contains(strFBOMSubRelId) && slFBOMSubPartIds.contains(strSubChildId)) {
						Map<String,Object> mpSubGrpsPrimaryQtyMap = new HashMap<>();
						MapList mlFBOMRelIdObjIdList = (MapList) mpFBOMRelIdObjIdMap.get(DomainConstants.SELECT_TO_ID);
						mpSubGrpsPrimaryQtyMap.put(PGStructuredATSConstants.KEY_SOURCE_ID, mlFBOMRelIdObjIdList);
						String strPrimaryQty =  (String) mpObjInfoMap.get(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
						mpSubGrpsPrimaryQtyMap.put(PGStructuredATSConstants.DISP_PRIMARY_QUANTITY, strPrimaryQty);
						mpFBOMSubRelParentSubChildIdsMap.put(strFBOMSubRelId, mpSubGrpsPrimaryQtyMap);
					}
				}
				

			}
		}

	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
	}
	
	/**
	 * Method to create Map of FBOM rel ids and to side objects
	 * @param context
	 * @param slFBOMRelIdList
	 * @return
	 * @throws FrameworkException 
	 */
	private Map<String, Object> getRelatedToSideObjForFBOM(Context context, StringList slFBOMRelIdList) throws FrameworkException {
		Map<String, Object> mpFBOMRelIdObjIdMap = new HashMap<>();
		StringList slFBOMSubPartIds = new StringList();
		MapList mlFBOMRelIdObjIdList = new MapList();
		int iListSize = slFBOMRelIdList.size();
		for(int i=0;i<iListSize;i++) {
			String strFBOMRelId = slFBOMRelIdList.get(i);
			
			String[] strFBOMArray = new String[1];
			strFBOMArray[0] = strFBOMRelId;
			MapList mlFBOMRelIds = DomainRelationship.getInfo(context, strFBOMArray, new StringList(DomainConstants.SELECT_TO_ID));
			Map<?, ?> mpFBOMRelId= (Map<?, ?>) mlFBOMRelIds.get(0);
			String strRelatedObjId =  (String) mpFBOMRelId.get(DomainConstants.SELECT_TO_ID);
			slFBOMSubPartIds.add(strRelatedObjId);
			
			Map<String,String> mpObjInfoMap = new HashMap<>();
			mpObjInfoMap.put(DomainConstants.SELECT_ID, strRelatedObjId);
			mpObjInfoMap.put(PGStructuredATSConstants.KEY_REL_ID, strFBOMRelId);
			mlFBOMRelIdObjIdList.add(mpObjInfoMap);
		}
		
		mpFBOMRelIdObjIdMap.put(DomainConstants.SELECT_ID, slFBOMSubPartIds);
		mpFBOMRelIdObjIdMap.put(DomainConstants.SELECT_TO_ID, mlFBOMRelIdObjIdList);
		
		return mpFBOMRelIdObjIdMap;
	}

	/**
	 * Method to add the EBOM Substitutes info to Json Array
	 * @param context
	 * @param jsonArrObjInfo
	 * @param mpSubstituesInfoMap
	 * @param strParentId
	 * @param strNewHierarchy
	 * @param strParentType
	 */
	private void addEBOMSubstituesInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo,
			Map<?, ?> mpSubstituesInfoMap, String strParentId, String strNewHierarchy, String strParentType) {
		try {
			for (Map.Entry<?, ?> entry : mpSubstituesInfoMap.entrySet()) {
				String strPrimaryId = (String) entry.getKey();
				String strSubIds = (String) entry.getValue();
				
				DomainObject dobPrimaryObj = DomainObject.newInstance(context, strPrimaryId); 
				String strPrimaryObjName = dobPrimaryObj.getInfo(context, DomainConstants.SELECT_NAME);
				
				StringList slConnectionIds = getChildConnectionIds(context, dobPrimaryObj, strParentType, strParentId);
				int iConnListSize = slConnectionIds.size();
				StringList slSubsChildIdList = StringUtil.split(strSubIds, ",");
				
				int iSubListSize = slSubsChildIdList.size();
				for (int i = 0; i < iSubListSize; i++) {
					String strSubChildId = slSubsChildIdList.get(i);
					DomainObject dobSubChildObj = DomainObject.newInstance(context, strSubChildId);
					Map<?, ?> mapObjInfo = dobSubChildObj.getInfo(context, slObjectSelects);
					String strChildName = (String) mapObjInfo.get(DomainConstants.SELECT_NAME);

					Map<String, String> mpBOMChildInfoMap = updateMapWithObjSelectAndValues(context, mapObjInfo, PGStructuredATSConstants.PREFIX_CTX_FOP);

					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_AFFECTED_PART, strChildName);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strNewHierarchy+","+strSubChildId);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_PRIMARY, strPrimaryObjName);
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_OPERATION,PGStructuredATSConstants.VALUE_SUBSTITUTE);
						
					StringList slBOMSubRelIds = new StringList();
					Map<String,String> mpEBOMSubPriQtyMap = new HashMap<>();
					for(int j=0; j<iConnListSize; j++) {
						String strBOMRelId = slConnectionIds.get(j);
						getRelatedEBOMSubRelId(context, strBOMRelId, strSubChildId, slBOMSubRelIds, mpEBOMSubPriQtyMap);
					}

					addSATSRelatedObjInfoToJsonArray(context, slBOMSubRelIds, mpBOMChildInfoMap, jsonArrObjInfo, strParentType, true, mpEBOMSubPriQtyMap);	
				}
			}

	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
		
	}

	/**
	 * Method to get the related 'EBOM Substitutes' rel id along with Primary Quantity attribute.
	 * @param context
	 * @param strEBOMId
	 * @param strSubChildId
	 * @param mpEBOMSubPriQtyMap
	 * @return
	 */
	private void getRelatedEBOMSubRelId(Context context, String strEBOMId, String strSubChildId, StringList slBOMSubRelIds, Map<String,String> mpEBOMSubPriQtyMap) {
		try {
		StringList slRelSelects = new StringList(2);
		slRelSelects.add(PGStructuredATSConstants.SELECT_EBOM_SUB_RELID);
		slRelSelects.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		
		String[] strRelIdArray = new String[1];
		strRelIdArray[0] = strEBOMId;
		MapList mlEBOMSubRelIdList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
		Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlEBOMSubRelIdList.get(0);
		Object objEBOMSubRelObj =  mpObjInfoMap.get(PGStructuredATSConstants.SELECT_EBOM_SUB_RELID);
		StringList slEBOMSubRelIdList = getStringListFromObject(objEBOMSubRelObj);
		
		if(slEBOMSubRelIdList != null) {
			int iEBOMSubListSize = slEBOMSubRelIdList.size();
			for(int i=0; i<iEBOMSubListSize; i++) {
				String strEBOMSubRelId = slEBOMSubRelIdList.get(i);
				String[] strEBOMSubArray = new String[1];
				strEBOMSubArray[0] = strEBOMSubRelId;
				MapList mlEBOMSubRelIds = DomainRelationship.getInfo(context, strEBOMSubArray, new StringList(DomainConstants.SELECT_TO_ID));
				Map<?, ?> mpEBOMSubRelId= (Map<?, ?>) mlEBOMSubRelIds.get(0);
				String strRelatedSubPartId =  (String) mpEBOMSubRelId.get(DomainConstants.SELECT_TO_ID);
				if(strRelatedSubPartId.equals(strSubChildId) && !slBOMSubRelIds.contains(strEBOMSubRelId)) {
					slBOMSubRelIds.add(strEBOMSubRelId);
					String strPrimaryQty =  (String) mpObjInfoMap.get(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
					mpEBOMSubPriQtyMap.put(strEBOMSubRelId, strPrimaryQty);
				}
			}
		}

	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
	}

	/**
	 * For BOM operation add BOM child and related SATS info to Array
	 * @param context
	 * @param jsonArrObjInfo
	 * @param strNewHierarchy
	 * @param slBOMChildIdList
	 * @param strParentId
	 */
	private void addBOMChildnSATSInfoToJsonArray(Context context, JsonArrayBuilder jsonArrObjInfo,
			String strNewHierarchy, StringList slBOMChildIdList, String strParentId, String strParentType) {
		try {
		int iBOMChildListSize = slBOMChildIdList.size();
		for (int i = 0; i < iBOMChildListSize; i++) {
			String strBOMChildId = slBOMChildIdList.get(i);
				DomainObject dobBOMChildObj = DomainObject.newInstance(context, strBOMChildId);
				Map<?, ?> mapObjInfo = dobBOMChildObj.getInfo(context, slObjectSelects);
				String strChildName = (String) mapObjInfo.get(DomainConstants.SELECT_NAME);
				String strChildId = (String) mapObjInfo.get(DomainConstants.SELECT_ID);
				Map<String, String> mpBOMChildInfoMap = updateMapWithObjSelectAndValues(context, mapObjInfo, PGStructuredATSConstants.PREFIX_CTX_FOP);

				mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_AFFECTED_PART, strChildName);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_HIERARCHY, strNewHierarchy+ "," +strChildId);
				
				if(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strParentType) || PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strParentType)) {
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_OPERATION,PGStructuredATSConstants.VALUE_BOM);
				} else {
					mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.KEY_OPERATION,PGStructuredATSConstants.VALUE_ALTERNATE);
				}
				
				StringList slConnectionIds = getChildConnectionIds(context, dobBOMChildObj, strParentType, strParentId);
				addSATSRelatedObjInfoToJsonArray(context, slConnectionIds, mpBOMChildInfoMap, jsonArrObjInfo, strParentType, false, null);				
		}
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
	}

	/**
	 * Fetch SATS details for FOP, APP and RMP : Common method to insert left and right hand side leaf level Json objects except 'FBOM Substitutes'
	 * @param context
	 * @param slConnectionIds
	 * @param mpBOMChildInfoMap
	 * @param jsonArrObjInfo
	 * @param strParentType
	 * @param isSubstitutePart
	 * @param mpEBOMSubPriQtyMap
	 */
	private void addSATSRelatedObjInfoToJsonArray(Context context, StringList slConnectionIds,
			Map<String, String> mpBOMChildInfoMap, JsonArrayBuilder jsonArrObjInfo, String strParentType, boolean isSubstitutePart, Map<String,String> mpEBOMSubPriQtyMap) {
		try {
			int iConnListSize = slConnectionIds.size();
			String strSelectRelatedATSContextRelId = PGStructuredATSConstants.SELECT_ATS_CONTEXT_RELID;	
			for(int i=0;i<iConnListSize;i++) {
				String strRelId = slConnectionIds.get(i);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_RELATIONSHIP_ID, strRelId);
				
				StringList slRelSelects = new StringList();
				slRelSelects.add(strSelectRelatedATSContextRelId);
				slRelSelects.addAll(slRelAttributeList);
				slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_TYPE);

				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strRelId;
				MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);

				Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlConnectionAttrList.get(0);
				String strRelType = (String) mpObjInfoMap.get(DomainConstants.SELECT_RELATIONSHIP_TYPE);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_RELATIONSHIP_TYPE, strRelType);
				
				for(int j=0;j<iRelAttributeListSize;j++) {
					String strRelAttrSelect = slRelAttributeList.get(j);
					String strRelAttrVal = (String) mpObjInfoMap.get(strRelAttrSelect);
					if(mpAttributeDisplayNameMap.containsKey(strRelAttrSelect)) {
						String strDisplayValue = mpAttributeDisplayNameMap.get(strRelAttrSelect);
						mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+strDisplayValue, strRelAttrVal);
					} else {
						mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+strRelAttrSelect, strRelAttrVal);
					}

				}
				
				String strPrimaryQtyForSubs = "";
				if(isSubstitutePart && mpEBOMSubPriQtyMap != null && mpEBOMSubPriQtyMap.containsKey(strRelId)) {
					strPrimaryQtyForSubs = mpEBOMSubPriQtyMap.get(strRelId);
				}
				
				updateQuentityMinMaxAttributeDisplayName(mpObjInfoMap, mpBOMChildInfoMap, isSubstitutePart, strParentType, strPrimaryQtyForSubs);
				
				Object objATSContextRelId =  mpObjInfoMap.get(strSelectRelatedATSContextRelId);
				StringList slATSCtxRelIds = getStringListFromObject(objATSContextRelId);
				StringList slATSContextRelIdList = getRelatedATSContextIdsForSATSObj(context, slATSCtxRelIds);

				if(slATSContextRelIdList == null || slATSContextRelIdList.isEmpty()) {
					JsonObjectBuilder jsonObjInfo = objSATSWhereUsedUtil.getProcessJsonObject(mpBOMChildInfoMap);
					jsonArrObjInfo.add(jsonObjInfo);
				} else {
					StringList slGroupObjIdList = getRelatedGroupObjIds(context, slATSContextRelIdList);
					addRelatedSATSInfoFromATSContextRelId(context,jsonArrObjInfo,mpBOMChildInfoMap,slATSContextRelIdList,slGroupObjIdList);
				}

			}
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
		
	}

	/**
	 * Method to get the related sibling RMPs under same SATS object 
	 * @param context
	 * @param slATSContextRelIdList
	 * @return
	 */
	private StringList getRelatedGroupObjIds(Context context, StringList slATSContextRelIdList) {
		StringList slGroupRelIdList = new StringList();
		try {
			int iSize = slATSContextRelIdList.size();
			for(int i=0; i<iSize; i++) {
				String strATSCtxRelId = slATSContextRelIdList.get(i);
				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strATSCtxRelId;
				MapList mlRelatedSATSObjList = DomainRelationship.getInfo(context, strRelIdArray, new StringList(strSelectRelatedObjsOfSATS));
				Map<?, ?> mpSTASObj = (Map<?, ?>) mlRelatedSATSObjList.get(0);
				Object objRelatedObjsOfSATS =  mpSTASObj.get(strSelectRelatedObjsOfSATS);
				StringList slRelatedObjsOfSATSList = getStringListFromObject(objRelatedObjsOfSATS);
				if(slRelatedObjsOfSATSList != null) {
					int iObjlistSize = slRelatedObjsOfSATSList.size();
					for(int j=0; j<iObjlistSize; j++) {
						String strObjId = slRelatedObjsOfSATSList.get(j);
						if(!slGroupRelIdList.contains(strObjId)) {
							slGroupRelIdList.add(strObjId);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
		
		return slGroupRelIdList;
	}

	/**
	 * Method to get only those ATS Context rel Ids which are related to current SATS object
	 * @param context
	 * @param slATSCtxRelIds
	 * @return
	 */
	private StringList getRelatedATSContextIdsForSATSObj(Context context, StringList slATSCtxRelIds) {
		StringList slATSContextRelIdList = null;
		try {
		if(slATSCtxRelIds == null || slATSCtxRelIds.isEmpty()) {
			return slATSContextRelIdList;
		} else {
			slATSContextRelIdList = new StringList();
			int iSize = slATSCtxRelIds.size();
			for(int i=0; i<iSize; i++) {
				String strATSCtxRelId = slATSCtxRelIds.get(i);
				
				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strATSCtxRelId;
				MapList mlRelatedSATSObjList = DomainRelationship.getInfo(context, strRelIdArray, new StringList(PGStructuredATSConstants.SELECT_RELATED_SATS_ID));
				Map<?, ?> mpSTASObj = (Map<?, ?>) mlRelatedSATSObjList.get(0);
				Object objRelatedSATSIds =  mpSTASObj.get(PGStructuredATSConstants.SELECT_RELATED_SATS_ID);
				StringList slRelatedSATSObjIdList = getStringListFromObject(objRelatedSATSIds);

				if(slRelatedSATSObjIdList != null && slRelatedSATSObjIdList.contains(strSTASId)) {
					slATSContextRelIdList.add(strATSCtxRelId);
				}
			}
		}
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
		return slATSContextRelIdList;
	}

	/**
	 * Method to update the display value for the attribute Quentity
	 * @param mpObjInfoMap
	 * @param mpBOMChildInfoMap
	 * @param isSubstitutePart
	 * @param strParentType
	 * @param strPrimaryQtyForSubs
	 */
	private void updateQuentityMinMaxAttributeDisplayName(Map<?, ?> mpObjInfoMap, Map<String, String> mpBOMChildInfoMap,
			boolean isSubstitutePart, String strParentType, String strPrimaryQtyForSubs) {
		String strQtyValue = (String) mpObjInfoMap.get(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		if(isSubstitutePart) {
			mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.DISP_SUB_QUANTITY, strQtyValue);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.DISP_PRIMARY_QUANTITY, strPrimaryQtyForSubs);
		} else {
			mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.DISP_QUANTITYWETPER, strQtyValue);
			
			if(PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strParentType)) {
				String strMinValue = (String) mpObjInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_MIN_FOP);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.DISP_MIN_PER, strMinValue);
				String strMaxValue = (String) mpObjInfoMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_MAX_FOP);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.PREFIX_CTX_FOP+PGStructuredATSConstants.DISP_MAX_PER, strMaxValue);
				
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
	 * Expand ATS Context rel ids to get related SATS data
	 * @param context
	 * @param jsonArrObjInfo
	 * @param mpBOMChildInfoMap
	 * @param slATSContextRelIdList
	 * @param slGroupObjIdList
	 */
	private void addRelatedSATSInfoFromATSContextRelId(Context context, JsonArrayBuilder jsonArrObjInfo,
			Map<String, String> mpBOMChildInfoMap, StringList slATSContextRelIdList, StringList slGroupObjIdList) {
		try {
		int iATSContextListSize = slATSContextRelIdList.size();
		for(int i=0;i<iATSContextListSize;i++) {
			String strATSContextRelId = slATSContextRelIdList.get(i);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_ATS_CONTEXT_RELID, strATSContextRelId);

			StringList slRelSelects = new StringList(strSelectRelatedATSOperationId);
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strATSContextRelId;
			MapList mlATSOperationRelIdList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
			Map<?, ?> mpATSOprMap = (Map<?, ?>) mlATSOperationRelIdList.get(0);
			
			StringList strATSOprRelIdList = null;
			Object objATSOprRelIds =  mpATSOprMap.get(strSelectRelatedATSOperationId);
			if(objATSOprRelIds instanceof StringList) {
				strATSOprRelIdList = (StringList) objATSOprRelIds;
			} else {
				String strATSOprRelIds = (String) objATSOprRelIds;
				strATSOprRelIdList = StringUtil.split(strATSOprRelIds, SelectConstants.cSelectDelimiter);
			}
	
			addRelatedSATSInfoFromATSOperationRelId(context,strATSOprRelIdList,mpBOMChildInfoMap,jsonArrObjInfo,slGroupObjIdList);
		}
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
	}

	/**
	 * Expand ATS Operation rel ids to get related SATS data
	 * @param context
	 * @param strATSOprRelIdList
	 * @param mpBOMChildInfoMap
	 * @param jsonArrObjInfo
	 * @param slGroupObjIdList
	 */
	private void addRelatedSATSInfoFromATSOperationRelId(Context context, StringList strATSOprRelIdList,
			Map<String, String> mpBOMChildInfoMap, JsonArrayBuilder jsonArrObjInfo, StringList slGroupObjIdList) {
		try {
		int iATSOprListSize = strATSOprRelIdList.size();
		for(int i=0;i<iATSOprListSize;i++) {
			String strATSOprRelId = strATSOprRelIdList.get(i);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_ATS_OPERATION_RELID, strATSOprRelId);

			StringList slRelSelects = new StringList();
			slRelSelects.add(DomainConstants.SELECT_TO_ID);
			slRelSelects.add(DomainConstants.SELECT_FROM_TYPE);
			slRelSelects.add(DomainConstants.SELECT_FROM_ID);
			slRelSelects.addAll(slRelAttributeList);
			
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strATSOprRelId;
			MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);

			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlConnectionAttrList.get(0);
			for(int j=0;j<iRelAttributeListSize;j++) {
				String strRelAttrSelect = slRelAttributeList.get(j);
				String strRelAttrVal = (String) mpObjInfoMap.get(strRelAttrSelect);
				String strAttributeName = getAttributeNameFromSelect(strRelAttrSelect);
				mpBOMChildInfoMap.put(strAttributeName, strRelAttrVal);

			}
			
			String strFromType = (String) mpObjInfoMap.get(DomainConstants.SELECT_FROM_TYPE);
			if(PGStructuredATSConstants.TYPE_PARENT_SUB.equals(strFromType)) {
				String strParentSubId = (String) mpObjInfoMap.get(DomainConstants.SELECT_FROM_ID);
				mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_PARENT_SUB_ID, strParentSubId);
			}
			
			String strRelatedObjId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);
			DomainObject dobRelatedObj = DomainObject.newInstance(context, strRelatedObjId);
			Map<?, ?> mapObjInfo = dobRelatedObj.getInfo(context, slObjectSelects);
			Map<String, String> mpSATSRelatedObjMap = updateMapWithObjSelectAndValues(context, mapObjInfo, "");

			for (Map.Entry<String, String> entry : mpSATSRelatedObjMap.entrySet()) {
				String strKey = entry.getKey();
				String strValue = entry.getValue();
				if(DomainConstants.SELECT_ATTRIBUTE_TITLE.equals(strKey)) {
					String strAttrName = getAttributeNameFromSelect(strKey);
					mpBOMChildInfoMap.put(strAttrName, strValue);
				} else {
					mpBOMChildInfoMap.put(strKey, strValue);
				}
				
			}
			
			String strCtxFOPObjId = mpBOMChildInfoMap.get(PGStructuredATSConstants.PREFIX_CTX_FOP+DomainConstants.SELECT_ID);
			StringList slCurrentGroupObjIdList = new StringList();
			slCurrentGroupObjIdList.addAll(slGroupObjIdList);
			
			slCurrentGroupObjIdList.remove(strRelatedObjId);
			String strGroupRelInfo = getGroupRelInfo(strCtxFOPObjId, slCurrentGroupObjIdList);
			mpBOMChildInfoMap.put(PGStructuredATSConstants.KEY_GROUP_REL, strGroupRelInfo);

			JsonObjectBuilder jsonObjInfo = objSATSWhereUsedUtil.getProcessJsonObject(mpBOMChildInfoMap);
			jsonArrObjInfo.add(jsonObjInfo);
		}
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
	}

	/**
	 * Method to add 'groupRel' information to final mpBOMChildInfoMap
	 * @param strCtxFOPObjId
	 * @param slCurrentGroupObjIdList
	 * @return
	 */
	private String getGroupRelInfo(String strCtxFOPObjId, StringList slCurrentGroupObjIdList) {
		StringBuilder sbGroup = new StringBuilder();
		sbGroup.append(strCtxFOPObjId);
		int iSize = slCurrentGroupObjIdList.size();
		for(int i=0;i<iSize;i++) {
			sbGroup.append(",");
			sbGroup.append(slCurrentGroupObjIdList.get(i));
		}
		return sbGroup.toString();
	}

	/**
	 * Format attribute select to get actual attribute name
	 * @param strRelAttrSelect
	 * @return
	 */
	private String getAttributeNameFromSelect(String strRelAttrSelect) {
			String strAttributeName = "";
			strAttributeName = strRelAttrSelect.replace(PGStructuredATSConstants.PREFIX_ATTRIBUTE_SELECT, "");
			strAttributeName = strAttributeName.replace(PGStructuredATSConstants.SUFFIX_ATTRIBUTE_SELECT, "");
			return strAttributeName;
	}

	/**
	 * Method to get EBOM, FBOM or Alternate connection ids for leaf level children
	 * @param context
	 * @param dobBOMChildObj
	 * @param strParentType
	 * @param strParentId
	 * @return
	 */
	private StringList getChildConnectionIds(Context context, DomainObject dobBOMChildObj, String strParentType,
			String strParentId) {
		StringList slConnectionIds = new StringList();
		try {
		String strWhereClause = strWhereId+strParentId;
		String strRelName = getRelationshipName(strParentType);
		MapList mlRelatedObjects = dobBOMChildObj.getRelatedObjects(context, // the eMatrix Context object
				strRelName, // Relationship pattern
				strParentType, // Type pattern
				StringList.create(DomainConstants.SELECT_ID), // Object selects
				StringList.create(DomainConstants.SELECT_RELATIONSHIP_ID), // Relationship selects
				true, // get From relationships
				false, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				strWhereClause, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available

		int iRelatedObjectsSize = mlRelatedObjects.size();
		for (int j = 0; j < iRelatedObjectsSize; j++) {
			Map<?, ?> mapObjInfoMap = (Map<?, ?>) mlRelatedObjects.get(j);
			String strChildRelId = (String) mapObjInfoMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
			slConnectionIds.add(strChildRelId);
		}
	} catch (Exception e) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
	}
		return slConnectionIds;
	}

	/**
	 * Get the type rel name to expand
	 * @param strParentType
	 * @return
	 */
	private String getRelationshipName(String strParentType) {
		String strRelName = "";
		if(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strParentType)) {
			strRelName = DomainConstants.RELATIONSHIP_EBOM;
		} else if(PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strParentType)) {
			strRelName = PGStructuredATSConstants.RELATIONSHIP_FBOM;
		} else {
			strRelName = DomainConstants.RELATIONSHIP_ALTERNATE;
		}

		return strRelName;
	}

	/**
	 * Method to get Substitutes and BOM child details separately
	 * @param iListSize
	 * @param slFBOMChildInfoList
	 * @return
	 */
	private Map<Object, Object> separateBOMnSubstituteInfo(int iListSize, StringList slBOMChildInfoList) {
		Map<Object, Object> mpBOMChildsnSubstituesInfoMap = new HashMap<>();
		StringList slBOMChildIdList = new StringList();
		StringList slSubsChildIdList = new StringList();
		for (int i = 0; i < iListSize; i++) {
			String strBOMChildId = slBOMChildInfoList.get(i);
			if(strBOMChildId.contains(PGStructuredATSConstants.SEPARATOR_FOR_SUBSTITUTES)) {
				if(!slSubsChildIdList.contains(strBOMChildId)) {
					slSubsChildIdList.add(strBOMChildId);
				}
			} else {
				if(!slBOMChildIdList.contains(strBOMChildId)) {
					slBOMChildIdList.add(strBOMChildId);
				}
				
			}
		}
						
		mpBOMChildsnSubstituesInfoMap.put(PGStructuredATSConstants.VALUE_BOM, slBOMChildIdList);
		
		Map<String, String> mpSubstituesInfoMap = getSubstirutesInfoMap(slSubsChildIdList);
		mpBOMChildsnSubstituesInfoMap.put(PGStructuredATSConstants.VALUE_SUBSTITUTE, mpSubstituesInfoMap);
		
		return mpBOMChildsnSubstituesInfoMap;
	}
	
	/**
	 * Method to get Substitutes Map with Primary as key
	 * @param slSubsChildIdList
	 * @return
	 */
	private Map<String, String> getSubstirutesInfoMap(StringList slSubsChildIdList) {
		Map<String, String> mpSubstituesInfoMap = new HashMap<>();
		int iSubListSize = slSubsChildIdList.size();
		for(int j=0; j<iSubListSize; j++) {
			String strSubInfo = slSubsChildIdList.get(j);
			StringList slSubInfoList = StringUtil.split(strSubInfo, PGStructuredATSConstants.SEPARATOR_FOR_SUBSTITUTES);
			if (slSubInfoList.size() == 2) {
				String strSubId = slSubInfoList.get(1);
				String strPrimaryId = slSubInfoList.get(0);
				if (mpSubstituesInfoMap.containsKey(strPrimaryId)) {
					String strExistingSubId = mpSubstituesInfoMap.get(strPrimaryId);
					mpSubstituesInfoMap.put(strPrimaryId, strExistingSubId + "," + strSubId);
				} else {
					mpSubstituesInfoMap.put(strPrimaryId, strSubId);
				}
			}
		}
		return mpSubstituesInfoMap;
	}

	/**
	 * Update MapList with slObjectSelects Map
	 * 
	 * @param context
	 * @param mapObjInfo
	 * @return
	 */
	public Map<String, String> updateMapWithObjSelectAndValues(Context context, Map<?, ?> mapObjInfo, String strPrefix) {
		Map<String, String> mapObjSelectValues = new HashMap<>();
		try {
			if(strPrefix == null) {
				strPrefix = "";
			}
			for (int j = 0; j < iObjectSelectsSize; j++) {
				String strObjSelectValue = "";
				String strObjSelectKey = slObjectSelects.get(j);
				Object objSelectValue = mapObjInfo.get(strObjSelectKey);
				if (objSelectValue instanceof StringList) {
					strObjSelectValue = objSATSWhereUsedUtil.formatStringListData(objSelectValue);
				} else {
					strObjSelectValue = (String) objSelectValue;
					if (strObjSelectValue == null) {
						strObjSelectValue = PGStructuredATSConstants.STRING_NULL;
					}
				}
				
				if(DomainConstants.SELECT_ATTRIBUTE_TITLE.equals(strObjSelectKey)) {
					String strAttrName = getAttributeNameFromSelect(strObjSelectKey);
					mapObjSelectValues.put(strPrefix+strAttrName, strObjSelectValue);
				} else {
					mapObjSelectValues.put(strPrefix+strObjSelectKey, strObjSelectValue);
				}
			}
			String strTypeName = (String) mapObjInfo.get(DomainConstants.SELECT_TYPE);
			String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context,
					PGStructuredATSConstants.STR_SCHEMA_TYPE, strTypeName, strLanguage);

			if (strDisplayName == null) {
				strDisplayName = strTypeName;
			}
			mapObjSelectValues.put(strPrefix+PGStructuredATSConstants.KEY_DISPLAY_TYPE, strDisplayName);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_BOM_DATA, e);
		}
		return mapObjSelectValues;
	}
	
	/**
	 * Method to create the object selects StringList for the data
	 */
	private void updateObjectSelectList() {
		slObjectSelects = new StringList();
		slObjectSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_REVISION);
		slObjectSelects.add(DomainConstants.SELECT_CURRENT);
		slObjectSelects.add(DomainConstants.SELECT_DESCRIPTION);
		slObjectSelects.add(DomainConstants.SELECT_OWNER);
		slObjectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);

		iObjectSelectsSize = slObjectSelects.size();
	}
	
	/**
	 * Method to create the attribute and display values Map
	 */
	private void updateAttributeDisplayValMap() {
		mpAttributeDisplayNameMap = new HashMap<>();
		mpAttributeDisplayNameMap.put(DomainConstants.SELECT_ATTRIBUTE_QUANTITY, PGStructuredATSConstants.DISP_QUANTITY);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_TARGET_WEIGHT_DRY, PGStructuredATSConstants.DISP_TARGETDRYWEIGHT);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_TARGET_WEIGHT_WET, PGStructuredATSConstants.DISP_TARGETWETWEIGHT);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PROCESSING_NOTE, PGStructuredATSConstants.DISP_PROCESSINGNOTE);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_TOTAL, PGStructuredATSConstants.DISP_DRYPER);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_LOSS, PGStructuredATSConstants.DISP_PROCESS_LOSS_PER);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_MAXIMUM_ACTUAL_WEIGHT_WET, PGStructuredATSConstants.DISP_WETWEIGHTMAX);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_MINIMUM_ACTUAL_WEIGHT_WET, PGStructuredATSConstants.DISP_WETWEIGHTMIN);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGSATSPCCONTEXT, PGStructuredATSConstants.ATTRIBUTE_PGSATSPCCONTEXT);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGREPLACEDORMODIFIED, PGStructuredATSConstants.ATTRIBUTE_PGREPLACEDORMODIFIED);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMINACTUALPERCENWET, PGStructuredATSConstants.DISP_MIN_PER);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMAXACTUALPERCENWET, PGStructuredATSConstants.DISP_MAX_PER);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_BM_PHASE, PGStructuredATSConstants.DISP_BM_PHASE);
		mpAttributeDisplayNameMap.put(PGStructuredATSConstants.SELECT_ATTRIBUTE_BALANCING_MATERIAL, PGStructuredATSConstants.DISP_BALANCINGMATERIAL);
		
	}
	
	/**
	 * Method to create the rel attributes StringList for the connections
	 */
	private void updateRelAttributeList() {
		slRelAttributeList = new StringList();
		slRelAttributeList.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_TARGET_WEIGHT_DRY);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_TARGET_WEIGHT_WET);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PROCESSING_NOTE);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_TOTAL);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_LOSS);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MAXIMUM_ACTUAL_WEIGHT_WET);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MINIMUM_ACTUAL_WEIGHT_WET);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGSATSPCCONTEXT);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGREPLACEDORMODIFIED);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMINACTUALPERCENWET);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMAXACTUALPERCENWET);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_BM_PHASE);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_BALANCING_MATERIAL);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MIN_FOP);
		slRelAttributeList.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MAX_FOP);
		
		
		iRelAttributeListSize = slRelAttributeList.size();
	}

}