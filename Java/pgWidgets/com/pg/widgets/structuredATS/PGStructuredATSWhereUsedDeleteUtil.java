package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArray;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.StringUtil;
import matrix.db.Context;
import matrix.util.StringList;

public class PGStructuredATSWhereUsedDeleteUtil {
	
	PGNWStructuredATSBOMDataUtil objStructuredATSBOMDataUtil = new PGNWStructuredATSBOMDataUtil();
	static final Logger logger = Logger.getLogger(PGStructuredATSWhereUsedDeleteUtil.class.getName());
	String strWhereId = "id == ";
	
	/**
	 * This method is used to delete connected WhereUsed Operations to SATS
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	public String deleteWhereUsedBOMOps(Context context, String strJsonInput) {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			ContextUtil.startTransaction(context, true);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			JsonObject jsonWhereUsedData = jsonInputData.getJsonObject(PGStructuredATSConstants.KEY_TARGET_IDS);
			String strSATSId = jsonInputData.getString(DomainConstants.SELECT_ID);

			DomainObject doATSObj = DomainObject.newInstance(context, strSATSId);
			String strWhereUsedAttribute = doATSObj.getAttributeValue(context,
					PGStructuredATSConstants.ATTRIBUTE_PGSATSWHEREUSEDSELECTEDITEMS);
			
			JsonObjectBuilder jsonUpdatedWhereUsedData = Json.createObjectBuilder();
			String strAPPorFOPId = getParentIdsForAllChildSelects(jsonWhereUsedData, jsonUpdatedWhereUsedData);
			String strUpdatedAttribute = "";
			if (UIUtil.isNotNullAndNotEmpty(strAPPorFOPId)) {
				strUpdatedAttribute = deleteWhereUsedParentStructure(context, strAPPorFOPId, strSATSId, strWhereUsedAttribute);
			}

			String strFinalWhereUsedAttribute = getUpdatedWhereUsedForSelectedChilds(context, strWhereUsedAttribute,
					strUpdatedAttribute, jsonUpdatedWhereUsedData, strAPPorFOPId, strSATSId);

			doATSObj.setAttributeValue(context, PGStructuredATSConstants.ATTRIBUTE_PGSATSWHEREUSEDSELECTEDITEMS,
					strFinalWhereUsedAttribute);
			
			ContextUtil.commitTransaction(context);
			jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			
		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_WHEREUSED_DELETE, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}

		return jsonReturnObj.build().toString();
	}
	
	/**
	 * Method to update where used attribute for selected child objects
	 * @param context
	 * @param strWhereUsedAttribute
	 * @param strUpdatedAttribute
	 * @param jsonUpdatedWhereUsedData
	 * @param strAPPorFOPId
	 * @param strSATSId
	 * @return
	 * @throws Exception 
	 */
	private String getUpdatedWhereUsedForSelectedChilds(Context context, String strWhereUsedAttribute, String strUpdatedAttribute,
			JsonObjectBuilder jsonUpdatedWhereUsedData, String strAPPorFOPId, String strSATSId) throws Exception {
		String strAttrValue = "";
		if(UIUtil.isNotNullAndNotEmpty(strAPPorFOPId)) {
			strAttrValue = strUpdatedAttribute;
		} else {
			strAttrValue = strWhereUsedAttribute;
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strAttrValue)) {
			Map<String,String> mpParentChildMap = getWhereUsedParentChildMap(strAttrValue);
			StringList slATSOperationRelId = new StringList();
			updateParentChildInfo(context, mpParentChildMap, jsonUpdatedWhereUsedData.build(), slATSOperationRelId);
			deleteReplacedDataForSelectedChilds(context, slATSOperationRelId, strSATSId);
			
			return getUpdatedAttrFromMap(mpParentChildMap);
			
		} else {
			return DomainConstants.EMPTY_STRING;
		}
	}

	/**
	 * Method to clean replaced data for selected child items
	 * @param context
	 * @param slATSOperationRelId
	 * @param strSATSId
	 * @throws Exception 
	 */
	private void deleteReplacedDataForSelectedChilds(Context context, StringList slATSOperationRelId,
			String strSATSId) throws Exception {
		int iListSize = slATSOperationRelId.size();
		for(int i=0;i<iListSize;i++) {
			String strATSOperationRelId = slATSOperationRelId.get(i);
			StringList slRelSelects = new StringList();
			slRelSelects.add(PGStructuredATSConstants.CONST_FROM);
			slRelSelects.add(PGStructuredATSConstants.CONST_TO);
			slRelSelects.add(PGStructuredATSConstants.CONST_TO_TYPE);
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strATSOperationRelId;
			MapList mlATSOprRelInfoList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlATSOprRelInfoList.get(0);
			
			String strRelatedATSId = (String) mpObjInfoMap.get(PGStructuredATSConstants.CONST_FROM);

			if(strSATSId.equals(strRelatedATSId)) {
				String strToObjType = (String) mpObjInfoMap.get(PGStructuredATSConstants.CONST_TO_TYPE);
				if(PGStructuredATSConstants.TYPE_PARENT_SUB.equals(strToObjType)) {
					String strParentSubObjId = (String) mpObjInfoMap.get(PGStructuredATSConstants.CONST_TO);
					DomainObject dobParentSubObj = DomainObject.newInstance(context, strParentSubObjId);
					dobParentSubObj.deleteObject(context);
				} else {
					DomainRelationship.disconnect(context, strATSOperationRelId);
				}
			}
		}
	}

	/**
	 * Method to get final attribute
	 * @param mpParentChildMap
	 * @return
	 */
	private String getUpdatedAttrFromMap(Map<String, String> mpParentChildMap) {
		StringBuilder sbWhereUsedAttribute = new StringBuilder();
		for (Map.Entry<String, String> entry : mpParentChildMap.entrySet()) {
			String strParentId = entry.getKey();
			String strChildInfo = entry.getValue();
			sbWhereUsedAttribute.append(strParentId).append(PGStructuredATSConstants.CONSTANT_STRING_COLON);
			sbWhereUsedAttribute.append(strChildInfo).append(PGStructuredATSConstants.CONSTANT_STRING_SEMICOLON);
		}
		
		String strWhereUsedAttributeValue = sbWhereUsedAttribute.toString();
		if(UIUtil.isNotNullAndNotEmpty(strWhereUsedAttributeValue)) {
			strWhereUsedAttributeValue = strWhereUsedAttributeValue.substring(0, strWhereUsedAttributeValue.length()-1);
		}
		
		return strWhereUsedAttributeValue;
	}

	/**
	 * Method to update Parent Child Map
	 * @param context
	 * @param mpParentChildMap
	 * @param jsonUpdatedWhereUsedData
	 * @param slATSOperationRelId
	 * @throws FrameworkException 
	 */
	private void updateParentChildInfo(Context context, Map<String, String> mpParentChildMap, JsonObject jsonUpdatedWhereUsedData,
			StringList slATSOperationRelId) throws FrameworkException {
		for (Entry<?, ?> entry : jsonUpdatedWhereUsedData.entrySet()) {
			String strKey = (String) entry.getKey();
			JsonArray jsonParentChildArray = jsonUpdatedWhereUsedData.getJsonArray(strKey);
			StringList slParentTypeList = StringUtil.split(strKey, PGStructuredATSConstants.CONSTANT_STRING_PIPE);
			String strParentId = slParentTypeList.get(0);
			String strParentType = slParentTypeList.get(1);

			JsonObject jsonSelectedChildObj = jsonParentChildArray.getJsonObject(0);
			JsonArray jsonHierarchy = jsonSelectedChildObj.getJsonArray(PGStructuredATSConstants.KEY_HIERARCHY);
			String strParentObjId = jsonHierarchy.getString(0);
			String strChildIds = mpParentChildMap.get(strParentObjId);
			
			if(PGStructuredATSConstants.TYPE_FORMULATION_PHASE.equals(strParentType)) {
				StringList slPhaseInfoList = StringUtil.split(strChildIds, "|");
				int iPhaseSize = slPhaseInfoList.size();
				StringBuilder sbPhase = new StringBuilder();
				String strPhaseToBeDeleted = "";
				for(int i=0;i<iPhaseSize;i++) {
					String strCurrentPhase = slPhaseInfoList.get(i);
					if(!strCurrentPhase.startsWith(strParentId)) {
						sbPhase.append(strCurrentPhase).append("|");
					} else {
						strPhaseToBeDeleted = strCurrentPhase;
					}
				}
				
				String strUpdatedPhaseInfo = sbPhase.toString();
				if(UIUtil.isNotNullAndNotEmpty(strUpdatedPhaseInfo)) {
					strUpdatedPhaseInfo = strUpdatedPhaseInfo.substring(0, strUpdatedPhaseInfo.length()-1);
				}
				
				mpParentChildMap.put(strParentObjId, strUpdatedPhaseInfo);
				
				updateATSOperationIdsForPhaseChilds(context, strPhaseToBeDeleted, slATSOperationRelId);
				
			} else {
				String strObjId = jsonSelectedChildObj.getString(DomainConstants.SELECT_ID);
				if(jsonSelectedChildObj.containsKey(PGStructuredATSConstants.KEY_PRIMARY)) {
					String strPrimary = jsonSelectedChildObj.getString(PGStructuredATSConstants.KEY_PRIMARY);
					if(UIUtil.isNotNullAndNotEmpty(strPrimary)) {
						strObjId = strPrimary + "_" + strObjId;
					}
				}
				
				if(jsonHierarchy.size() > 3) { //For FOP
					String strPhaseId = jsonHierarchy.getString(2);
					StringList slPhaseInfoList = StringUtil.split(strChildIds, "|");
					int iPhaseSize = slPhaseInfoList.size();
					StringBuilder sbPhase = new StringBuilder();
					for(int i=0;i<iPhaseSize;i++) {
						String strCurrentPhase = slPhaseInfoList.get(i);
						if(strCurrentPhase.startsWith(strPhaseId)) {
							if(strCurrentPhase.contains(PGStructuredATSConstants.COMMA_SEP+strObjId)) {
								strCurrentPhase = strCurrentPhase.replace(PGStructuredATSConstants.COMMA_SEP+strObjId, "");
							} else {
								strCurrentPhase = strCurrentPhase.replace(strObjId+PGStructuredATSConstants.COMMA_SEP, "");
							}
							sbPhase.append(strCurrentPhase);
							
						} else {
							sbPhase.append(strCurrentPhase);
						}
						
						if(i+1 < iPhaseSize) {
							sbPhase.append("|");
						}
					}
					mpParentChildMap.put(strParentObjId, sbPhase.toString());
					
					updateATSOperationsForFOP(context, strPhaseId, strObjId, slATSOperationRelId);
					
				} else {  //For APP
					if(strChildIds.contains(PGStructuredATSConstants.COMMA_SEP+strObjId)) {
						strChildIds = strChildIds.replace(PGStructuredATSConstants.COMMA_SEP+strObjId, "");
					} else {
						strChildIds = strChildIds.replace(strObjId+PGStructuredATSConstants.COMMA_SEP, "");
					}
					mpParentChildMap.put(strParentObjId, strChildIds);
					
					updateATSOperationsForAPP(context, strParentObjId, strObjId, slATSOperationRelId);
				}
			}
		}
	}

	/**
	 * Method to get ATS Operation rel Ids for phase data
	 * @param context
	 * @param strPhaseToBeDeleted
	 * @param slATSOperationRelId
	 * @throws FrameworkException 
	 */
	private void updateATSOperationIdsForPhaseChilds(Context context, String strPhaseToBeDeleted,
			StringList slATSOperationRelId) throws FrameworkException {
		if(UIUtil.isNotNullAndNotEmpty(strPhaseToBeDeleted)) {
			StringList slPhaseChildList = StringUtil.split(strPhaseToBeDeleted, "-");
			if(slPhaseChildList.size() > 1) {
				String strPhaseId = slPhaseChildList.get(0);
				String strPhaseChildIds = slPhaseChildList.get(1);
				StringList slChildIdList = StringUtil.split(strPhaseChildIds, ",");
				int iListSize = slChildIdList.size();
				for(int i=0;i<iListSize;i++) {
					String strObjId = slChildIdList.get(i);
					updateATSOperationsForFOP(context, strPhaseId, strObjId, slATSOperationRelId);
				}
			}
		}
	}

	/**
	 * Method to get ATS Operations for FOPs
	 * @param context
	 * @param strPhaseId
	 * @param strObjId
	 * @param slATSOperationRelId
	 * @throws FrameworkException 
	 */
	private void updateATSOperationsForFOP(Context context, String strPhaseId, String strObjId,
			StringList slATSOperationRelId) throws FrameworkException {
		StringList slConnectionIds = new StringList();
		if (strObjId.contains("_")) { // FBOM Substitutes
			StringList slSubInfoList = StringUtil.split(strObjId, "_");
			String strPrimaryId = slSubInfoList.get(0);
			String strSubObjId = slSubInfoList.get(1);
			DomainObject dobPrimaryObj = DomainObject.newInstance(context, strPrimaryId);
			StringList slFBOMConnections = getChildConnectionIds(context, dobPrimaryObj,
					PGStructuredATSConstants.TYPE_FORMULATION_PHASE, strPhaseId);

			slConnectionIds = getConnectionIdsForFBOMSub(context, slFBOMConnections, strSubObjId);
					
			updateATSOperationRelIds(context, slConnectionIds, slATSOperationRelId, true);
			
		} else {
			DomainObject dobBOMChildObj = DomainObject.newInstance(context, strObjId);
			slConnectionIds = getChildConnectionIds(context, dobBOMChildObj,
					PGStructuredATSConstants.TYPE_FORMULATION_PHASE, strPhaseId);
			
			updateATSOperationRelIds(context, slConnectionIds, slATSOperationRelId, false);
		}
	}

	/**
	 * Method to get FBOM Sub rel ids from  FBOM rel
	 * @param context
	 * @param slFBOMConnections
	 * @param strSubObjId
	 * @return
	 * @throws FrameworkException 
	 */
	private StringList getConnectionIdsForFBOMSub(Context context, StringList slFBOMConnections, String strSubObjId) throws FrameworkException {
		StringList slConnectionIds = new StringList();
		int iConnListSize = slFBOMConnections.size();
		for (int j = 0; j < iConnListSize; j++) {
			String strBOMRelId = slFBOMConnections.get(j);
			StringList slRelSelects = new StringList();
			slRelSelects.add(PGStructuredATSConstants.SELECT_FBOM_SUB_RELID);
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strBOMRelId;
			MapList mlFBOMSubRelIdList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlFBOMSubRelIdList.get(0);
			if(mpObjInfoMap.containsKey(PGStructuredATSConstants.SELECT_FBOM_SUB_RELID)) {
				Object objFBOMSubRelObj =  mpObjInfoMap.get(PGStructuredATSConstants.SELECT_FBOM_SUB_RELID);
				StringList slFBOMSubRelIdList = objStructuredATSBOMDataUtil.getStringListFromObject(objFBOMSubRelObj);
				
				if(slFBOMSubRelIdList != null) {
					String strSelectRelatedSubObjIds = PGStructuredATSConstants.SELECT_FBOM_SUB_OBJ_IDS;
					int iFBOMSubListSize = slFBOMSubRelIdList.size();
					for(int i=0; i<iFBOMSubListSize; i++) {
						String strFBOMSubRelId = slFBOMSubRelIdList.get(i);
						String[] strFBOMSubArray = new String[1];
						strFBOMSubArray[0] = strFBOMSubRelId;
						MapList mlFBOMSubRelIds = DomainRelationship.getInfo(context, strFBOMSubArray, new StringList(strSelectRelatedSubObjIds));
						Map<?, ?> mpFBOMSubObjId= (Map<?, ?>) mlFBOMSubRelIds.get(0);
						Object objRelatedFBOMObjIds=  mpFBOMSubObjId.get(strSelectRelatedSubObjIds);
						StringList slFBOMObjIdList = objStructuredATSBOMDataUtil.getStringListFromObject(objRelatedFBOMObjIds);
						
						if(slFBOMObjIdList != null && !slFBOMObjIdList.isEmpty() && slFBOMObjIdList.contains(strSubObjId)) {
							slConnectionIds.add(strFBOMSubRelId);
						}
					}
				}
			}
		}
		
		return slConnectionIds;
	}

	/**
	 * Method to get ATS Operations for APPs
	 * 
	 * @param context
	 * @param strParentObjId
	 * @param strObjId
	 * @param slATSOperationRelId
	 * @throws FrameworkException
	 */
	private void updateATSOperationsForAPP(Context context, String strParentObjId, String strObjId,
			StringList slATSOperationRelId) throws FrameworkException {
		StringList slConnectionIds = new StringList();
		if (strObjId.contains("_")) { // EBOM Substitutes
			StringList slSubInfoList = StringUtil.split(strObjId, "_");
			String strPrimaryId = slSubInfoList.get(0);
			String strSubObjId = slSubInfoList.get(1);
			DomainObject dobPrimaryObj = DomainObject.newInstance(context, strPrimaryId);
			StringList slEBOMConnections = getChildConnectionIds(context, dobPrimaryObj,
					PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART, strParentObjId);

			slConnectionIds = getConnectionIdsForEBOMSub(context, slEBOMConnections, strSubObjId);

		} else {
			DomainObject dobBOMChildObj = DomainObject.newInstance(context, strObjId);
			slConnectionIds = getChildConnectionIds(context, dobBOMChildObj,
					PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART, strParentObjId);
		}

		updateATSOperationRelIds(context, slConnectionIds, slATSOperationRelId, false);
	}

	/**
	 * Method to update ATS Operation rel ids
	 * 
	 * @param context
	 * @param slConnectionIds
	 * @param slATSOperationRelId
	 * @param isFBOMSub
	 * @throws FrameworkException
	 */
	private void updateATSOperationRelIds(Context context, StringList slConnectionIds, StringList slATSOperationRelId,
			boolean isFBOMSub) throws FrameworkException {
		String strSelectRelatedATSOperationRelId = PGStructuredATSConstants.SELECT_RELATED_ATS_OPERATION_REL_IDS;
		if (isFBOMSub) {
			strSelectRelatedATSOperationRelId = PGStructuredATSConstants.SELECT_RELATED_ATS_OPERATION_REL_IDS_FBOMSUB;
		}
		int iConnListSize = slConnectionIds.size();
		for (int i = 0; i < iConnListSize; i++) {
			String strRelId = slConnectionIds.get(i);

			StringList slRelSelects = new StringList();
			slRelSelects.add(strSelectRelatedATSOperationRelId);

			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strRelId;
			MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);

			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlConnectionAttrList.get(0);
			if (mpObjInfoMap.containsKey(strSelectRelatedATSOperationRelId)) {
				Object objATSOperationRelIds = mpObjInfoMap.get(strSelectRelatedATSOperationRelId);
				StringList slATSOpRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSOperationRelIds);
				if (slATSOpRelIds != null) {
					int iATSOpListSize = slATSOpRelIds.size();
					for (int j = 0; j < iATSOpListSize; j++) {
						String strATSOprRelId = slATSOpRelIds.get(j);
						if (!slATSOperationRelId.contains(strATSOprRelId)) {
							slATSOperationRelId.add(strATSOprRelId);
						}
					}
				}
			}
		}
	}

	/**
	 * Method to get EBOM Sub rel ids from  EBOM rel
	 * @param context
	 * @param slEBOMConnections
	 * @param strSubObjId
	 * @return
	 * @throws FrameworkException 
	 */
	private StringList getConnectionIdsForEBOMSub(Context context, StringList slEBOMConnections, String strSubChildId) throws FrameworkException {
		StringList slConnectionIds = new StringList();
		int iEBOMListSize = slEBOMConnections.size();
		for(int i=0;i<iEBOMListSize;i++) {
			String strEBOMId = slEBOMConnections.get(i);
			StringList slRelSelects = new StringList();
			slRelSelects.add(PGStructuredATSConstants.SELECT_EBOM_SUB_RELID);
			
			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strEBOMId;
			MapList mlEBOMSubRelIdList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);
			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlEBOMSubRelIdList.get(0);
			if(mpObjInfoMap.containsKey(PGStructuredATSConstants.SELECT_EBOM_SUB_RELID)) {
				Object objEBOMSubRelObj =  mpObjInfoMap.get(PGStructuredATSConstants.SELECT_EBOM_SUB_RELID);
				StringList slEBOMSubRelIdList = objStructuredATSBOMDataUtil.getStringListFromObject(objEBOMSubRelObj);
				
				if(slEBOMSubRelIdList != null) {
					int iEBOMSubListSize = slEBOMSubRelIdList.size();
					for(int j=0; j<iEBOMSubListSize; j++) {
						String strEBOMSubRelId = slEBOMSubRelIdList.get(j);
						String[] strEBOMSubArray = new String[1];
						strEBOMSubArray[0] = strEBOMSubRelId;
						MapList mlEBOMSubRelIds = DomainRelationship.getInfo(context, strEBOMSubArray, new StringList(DomainConstants.SELECT_TO_ID));
						Map<?, ?> mpEBOMSubRelId= (Map<?, ?>) mlEBOMSubRelIds.get(0);
						String strRelatedSubPartId =  (String) mpEBOMSubRelId.get(DomainConstants.SELECT_TO_ID);
						if(strRelatedSubPartId.equals(strSubChildId)) {
							slConnectionIds.add(strEBOMSubRelId);
						}
					}
				}
			}
		}
		
		return slConnectionIds;
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
		String strRelName = "";
		
		if(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strParentType)) {
			strRelName = DomainConstants.RELATIONSHIP_EBOM;
		} else {
			strRelName = PGStructuredATSConstants.RELATIONSHIP_FBOM;
		} 
		
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
	 * Method to get where used data Map
	 * @param strAttrValue
	 * @return
	 */
	private Map<String, String> getWhereUsedParentChildMap(String strAttrValue) {
		Map<String,String> mpParentChildMap = new LinkedHashMap<>();
		StringList slCtxFOPStructure = StringUtil.split(strAttrValue,
				PGStructuredATSConstants.CONSTANT_STRING_SEMICOLON);
		for (String strCtxFOPStructure : slCtxFOPStructure) {
			StringList slCtxFOP = StringUtil.split(strCtxFOPStructure, PGStructuredATSConstants.CONSTANT_STRING_COLON);
			String strParentId = slCtxFOP.get(0);
			String strChilStructure = slCtxFOP.get(1);
			mpParentChildMap.put(strParentId, strChilStructure);
		}
		return mpParentChildMap;
	}

	/**
	 * Method to get Parent ids for all case
	 * @param jsonWhereUsedData
	 * @param jsonUpdatedWhereUsedData
	 * @return
	 */
	private String getParentIdsForAllChildSelects(JsonObject jsonWhereUsedData, JsonObjectBuilder jsonUpdatedWhereUsedData) {
		StringBuilder sbParentIds = new StringBuilder();
		for (Entry<?, ?> entry : jsonWhereUsedData.entrySet()) {
			String strKey = (String) entry.getKey();
			JsonArray jsonParentChildArray = jsonWhereUsedData.getJsonArray(strKey);
			int iArraySize = jsonParentChildArray.size();
			StringList slParentTypeList = StringUtil.split(strKey, PGStructuredATSConstants.CONSTANT_STRING_PIPE);
			String strParentId = slParentTypeList.get(0);
			String strParentType = slParentTypeList.get(1);
			
			if(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strParentType) || 
				(PGStructuredATSConstants.TYPE_FORMULATION_PART.equals(strParentType) && iArraySize > 3)) {
				sbParentIds.append(strParentId).append(PGStructuredATSConstants.CONSTANT_STRING_PIPE);
			} else {
				jsonUpdatedWhereUsedData.add(strKey, jsonParentChildArray);
			}
		}
		
		String strParentIds = sbParentIds.toString();
		
		if(UIUtil.isNotNullAndNotEmpty(strParentIds)) {
			strParentIds = strParentIds.substring(0, strParentIds.length()-1);
		}
		
		return strParentIds;
	}

	/**
	 * Method to delete where used for complete Parent structure selected
	 * 
	 * @param context
	 * @param strAPPorFOPId
	 * @param strSATSId
	 * @param strWhereUsedAttribute
	 * @return
	 * @throws Exception
	 */
	private String deleteWhereUsedParentStructure(Context context, String strAPPorFOPId, String strSATSId,
			String strWhereUsedAttribute) throws Exception {
		StringList slFormulaList = StringUtil.split(strAPPorFOPId, PGStructuredATSConstants.CONSTANT_STRING_PIPE);

		String strFinalWhereUsedAttribute = updateWhereUsedSATSAttr(context, strSATSId, slFormulaList,
				strWhereUsedAttribute);
		deleteATSRelationships(context, strSATSId, slFormulaList);

		for (String strParentId : slFormulaList) {
			DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
			String strType = dobParentObj.getInfo(context, DomainConstants.SELECT_TYPE);

			if (strType.equals(PGStructuredATSConstants.TYPE_FORMULATION_PART)) {
				deleteReplacedDataForFOP(context, strSATSId, strParentId);
				deleteReplacedDataForPerfChars(context, strSATSId, strParentId);
			} else if (strType.equals(PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART)) {
				deleteReplacedDataForAPP(context, strSATSId, strParentId);
				deleteReplacedDataForPerfChars(context, strSATSId, strParentId);
			} else {
				deleteReplacedDataForAlternates(context, strSATSId, strParentId);
			}
		}

		return strFinalWhereUsedAttribute;
	}

	/**
	 * Method to clean replaced data for 'Performance Characteristic'
	 * @param context
	 * @param strSATSId
	 * @param strParentId
	 * @throws Exception 
	 */
	private void deleteReplacedDataForPerfChars(Context context, String strSATSId, String strParentId) throws Exception {
		MapList mlATSContextRelIdList = DomainObject.getInfo(context, new String[] { strParentId },
				new StringList(PGStructuredATSConstants.SELECT_ATS_CTX_PERF_CHAR_RELID));

		if(mlATSContextRelIdList != null && !mlATSContextRelIdList.isEmpty()) {
			Map<?,?> mpATSRelIdMap = (Map<?, ?>) mlATSContextRelIdList.get(0);
			Object objATSContextRelId =  mpATSRelIdMap.get(PGStructuredATSConstants.SELECT_ATS_CTX_PERF_CHAR_RELID);
			StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);
			
			if(slATSCtxRelIds != null && !slATSCtxRelIds.isEmpty()) {
				StringList slPerfCharObjIdList = getRelatedIdsForATS(context, strSATSId, slATSCtxRelIds,
						PGStructuredATSConstants.SELECT_RELATED_OBJS_FOR_SATS_PERF_CHAR);
				
				int iListSize = slPerfCharObjIdList.size();
				for (int i=0;i<iListSize;i++) {
					String strPerfCharId = slPerfCharObjIdList.get(i);
					DomainObject dobPerfCharObj = DomainObject.newInstance(context, strPerfCharId);
					dobPerfCharObj.deleteObject(context);
				}
			}
		}
		
		//clean up 'Performance Characteristic' for add operation
		deletePerfCharForAddOperation(context, strSATSId, strParentId);
		
	}

	/**
	 * Method to clean up add operation for selected consuming formula
	 * @param context
	 * @param strSATSId
	 * @param strParentId
	 * @throws Exception 
	 */
	private void deletePerfCharForAddOperation(Context context, String strSATSId, String strParentId) throws Exception {
		DomainObject dobSATSObj = DomainObject.newInstance(context, strSATSId);
		
		MapList mlRelatedSATSObjList = dobSATSObj.getRelatedObjects(context, // the eMatrix Context object
				PGStructuredATSConstants.REL_PG_ATS_OPERATION, // Relationship pattern
				PGStructuredATSConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC, // Type pattern
				StringList.create(DomainConstants.SELECT_ID), // Object selects
				StringList.create(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGSATSPCCONTEXT), // Relationship selects
				false, // get From relationships
				true, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				null, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available
		
		if(mlRelatedSATSObjList != null) {
			int iListSize = mlRelatedSATSObjList.size();
			for(int i=0; i<iListSize; i++) {
				Map<?,?> mpPerfCharObjMap = (Map<?, ?>) mlRelatedSATSObjList.get(i);
				String strAttrRelatedConsumingFormula = (String) mpPerfCharObjMap.get(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGSATSPCCONTEXT);
				if(UIUtil.isNotNullAndNotEmpty(strAttrRelatedConsumingFormula) && strAttrRelatedConsumingFormula.equals(strParentId)) {
					String strPerfCharObjId = (String) mpPerfCharObjMap.get(DomainConstants.SELECT_ID);
					DomainObject dobPerfCharObj = DomainObject.newInstance(context, strPerfCharObjId);
					dobPerfCharObj.deleteObject(context);
				}
			}
		}
		
	}

	/**
	 * Method to clean replaced data for Alternates
	 * @param context
	 * @param strSATSId
	 * @param strParentId
	 * @throws FrameworkException 
	 */
	private void deleteReplacedDataForAlternates(Context context, String strSATSId, String strParentId) throws FrameworkException {

		MapList mlATSContextRelIdList = DomainObject.getInfo(context, new String[] { strParentId },
				new StringList(PGStructuredATSConstants.SELECT_ATS_CTX_ALTERNATE_RELID));

		if(mlATSContextRelIdList != null && !mlATSContextRelIdList.isEmpty()) {
			Map<?,?> mpATSRelIdMap = (Map<?, ?>) mlATSContextRelIdList.get(0);
			Object objATSContextRelId =  mpATSRelIdMap.get(PGStructuredATSConstants.SELECT_ATS_CTX_ALTERNATE_RELID);
			StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);
			
			if(slATSCtxRelIds != null && !slATSCtxRelIds.isEmpty()) {
				StringList slATSOperationRelIdList = getRelatedIdsForATS(context, strSATSId, slATSCtxRelIds,
						PGStructuredATSConstants.SELECT_ATS_OPERATION_RELID);
				
				int iListSize = slATSOperationRelIdList.size();
				for (int i=0;i<iListSize;i++) {
					String strATSOperationRelId = slATSOperationRelIdList.get(i);
					DomainRelationship.disconnect(context, strATSOperationRelId);
				}
			}
		}
		
	}

	/**
	 * Method to clean replaced data for APPs
	 * @param context
	 * @param strSATSId
	 * @param strParentId
	 * @throws FrameworkException 
	 */
	private void deleteReplacedDataForAPP(Context context, String strSATSId, String strParentId) throws FrameworkException {
		StringList slRelSelect = new StringList();
		slRelSelect.add(PGStructuredATSConstants.SELECT_ATS_CTX_ESUB_RELID);
		slRelSelect.add(PGStructuredATSConstants.SELECT_ATS_CTX_EBOM_RELID);
		
		MapList mlATSContextRelIdList = DomainObject.getInfo(context, new String[] { strParentId }, slRelSelect);
		
		if(mlATSContextRelIdList != null && !mlATSContextRelIdList.isEmpty()) {
			StringList slATSContextRelIdList = new StringList();
			Map<?,?> mpATSRelIdMap = (Map<?, ?>) mlATSContextRelIdList.get(0);
			Object objATSContextRelId =  mpATSRelIdMap.get(PGStructuredATSConstants.SELECT_ATS_CTX_EBOM_RELID);
			StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);
			Object objATSContextRelIdForSub =  mpATSRelIdMap.get(PGStructuredATSConstants.SELECT_ATS_CTX_ESUB_RELID);
			StringList slATSCtxRelIdsForSub = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelIdForSub);
			
			if(slATSCtxRelIds != null && !slATSCtxRelIds.isEmpty()) {
				slATSContextRelIdList.addAll(slATSCtxRelIds);
			}
			
			if(slATSCtxRelIdsForSub != null && !slATSCtxRelIdsForSub.isEmpty()) {
				slATSContextRelIdList.addAll(slATSCtxRelIdsForSub);
			}
			
			StringList slATSOperationRelIdList = getRelatedIdsForATS(context, strSATSId, slATSContextRelIdList,
					PGStructuredATSConstants.SELECT_ATS_OPERATION_RELID);
			
			int iListSize = slATSOperationRelIdList.size();
			for (int i=0;i<iListSize;i++) {
				String strATSOperationRelId = slATSOperationRelIdList.get(i);
				DomainRelationship.disconnect(context, strATSOperationRelId);
			}
		}
		
	}

	/**
	 * Get related ids such as Parent Sub, ATS Operation for current SATS object
	 * @param context
	 * @param strSATSId
	 * @param slATSContextRelIdList
	 * @return
	 * @throws FrameworkException 
	 */
	private StringList getRelatedIdsForATS(Context context, String strSATSId,
			StringList slATSContextRelIdList, String strRelSelect) throws FrameworkException {
		StringList slRelatedIdList = new StringList();
		StringList slRelSelect = new StringList();
		slRelSelect.add(PGStructuredATSConstants.SELECT_RELATED_SATS_ID);
		slRelSelect.add(strRelSelect);
		
		int iListSize = slATSContextRelIdList.size();
		for (int i=0;i<iListSize;i++) {
			String strATSContextRelId = slATSContextRelIdList.get(i);
			MapList mlATSOperationInfoList = DomainRelationship.getInfo(context, new String[] { strATSContextRelId }, slRelSelect);
			Map<?, ?> mpATSOperationInfoMap = (Map<?, ?>) mlATSOperationInfoList.get(0);
			String strRelatedATSId = (String) mpATSOperationInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_SATS_ID);
			if(strSATSId.equals(strRelatedATSId)) {
				String strRelatedId = (String) mpATSOperationInfoMap.get(strRelSelect);
				if(!slRelatedIdList.contains(strRelatedId)) {
					slRelatedIdList.add(strRelatedId);
				}
			}
		}

		return slRelatedIdList;

	}

	/**
	 * Method to clean replaced data for FOPs
	 * @param context
	 * @param strSATSId
	 * @param strParentId
	 * @throws Exception 
	 */
	private void deleteReplacedDataForFOP(Context context, String strSATSId, String strParentId) throws Exception {
		StringList slRelSelect = new StringList();
		slRelSelect.add(PGStructuredATSConstants.SELECT_ATS_CTX_RELID);
		slRelSelect.add(PGStructuredATSConstants.SELECT_ATS_CTX_FSUB_RELID);

		MapList mlATSContextRelIdList = DomainObject.getInfo(context, new String[] { strParentId }, slRelSelect);
		
		if(mlATSContextRelIdList != null && !mlATSContextRelIdList.isEmpty()) {
			StringList slATSContextRelIdList = new StringList();
			Map<?,?> mpATSRelIdMap = (Map<?, ?>) mlATSContextRelIdList.get(0);
			Object objATSContextRelId =  mpATSRelIdMap.get(PGStructuredATSConstants.SELECT_ATS_CTX_RELID);
			StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);
			Object objATSContextRelIdForSub =  mpATSRelIdMap.get(PGStructuredATSConstants.SELECT_ATS_CTX_FSUB_RELID);
			StringList slATSCtxRelIdsForSub = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelIdForSub);
			
			if(slATSCtxRelIds != null && !slATSCtxRelIds.isEmpty()) {
				slATSContextRelIdList.addAll(slATSCtxRelIds);
			}
			
			if(slATSCtxRelIdsForSub != null && !slATSCtxRelIdsForSub.isEmpty()) {
				slATSContextRelIdList.addAll(slATSCtxRelIdsForSub);
			}
			
			StringList slParentSubObjIdList = getRelatedIdsForATS(context, strSATSId, slATSContextRelIdList,
					PGStructuredATSConstants.SELECT_RELATED_OBJS_FOR_SATS_PARENT_SUB);
			
			int iListSize = slParentSubObjIdList.size();
			for (int i=0;i<iListSize;i++) {
				String strParentSubObjId = slParentSubObjIdList.get(i);
				DomainObject dobParentSubObj = DomainObject.newInstance(context, strParentSubObjId);
				dobParentSubObj.deleteObject(context);
			}
		}
		
	}

	/**
	 * Method to cleanup 'Authorized Temporary Specification' relationships
	 * @param context
	 * @param strSATSId
	 * @param slFormulaList
	 * @throws FrameworkException 
	 */
	private void deleteATSRelationships(Context context, String strSATSId, StringList slFormulaList) throws FrameworkException {
		
		DomainObject dobATSObj = DomainObject.newInstance(context, strSATSId);
		MapList mlRelatedObjects = dobATSObj.getRelatedObjects(context, // the eMatrix Context object
				PGStructuredATSConstants.RELATIONSHIP_AUTHORIZED_TEMPORARY_SPECIFICATION, // Relationship pattern
				"*", // Type pattern
				new StringList(DomainConstants.SELECT_ID), // Object selects
				new StringList(DomainConstants.SELECT_RELATIONSHIP_ID), // Relationship elects
				false, // get From relationships
				true, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				null, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available

		if(mlRelatedObjects != null) {
			int iListSize = mlRelatedObjects.size();
			for (int i = 0; i < iListSize; i++) {
				Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlRelatedObjects.get(i);
				String strRelatedObjId = (String) mpObjInfoMap.get(DomainConstants.SELECT_ID);
				if(slFormulaList.contains(strRelatedObjId)) {
					String strRelId = (String) mpObjInfoMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					DomainRelationship.disconnect(context, strRelId);
				}
			}
		}
	}
	
	/**
	 * This method is used to updated WhereUsed Attribute to SATS
	 * 
	 * @param context
	 * @param strSATSId
	 * @param slSelectedCtxFop
	 * @param strWhereUsedAttribute
	 * @return
	 * @throws FrameworkException
	 */
	public String updateWhereUsedSATSAttr(Context context, String strSATSId, StringList slSelectedCtxFop,
			String strWhereUsedAttribute) throws FrameworkException {
		StringList slCtxFOP = new StringList();
		String strParentId = DomainConstants.EMPTY_STRING;
		String strFinalWhereUsedAttribute = DomainConstants.EMPTY_STRING;

		StringList slCtxFOPStructure = StringUtil.split(strWhereUsedAttribute,
				PGStructuredATSConstants.CONSTANT_STRING_SEMICOLON);
		for (String strCtxFOPStructure : slCtxFOPStructure) {
			slCtxFOP = StringUtil.split(strCtxFOPStructure, PGStructuredATSConstants.CONSTANT_STRING_COLON);
			strParentId = slCtxFOP.get(0);
			if (!slSelectedCtxFop.contains(strParentId)) {
				strFinalWhereUsedAttribute = strFinalWhereUsedAttribute + strCtxFOPStructure
						+ PGStructuredATSConstants.CONSTANT_STRING_SEMICOLON;
			}
		}

		if (UIUtil.isNotNullAndNotEmpty(strFinalWhereUsedAttribute) && strFinalWhereUsedAttribute
				.endsWith(String.valueOf(PGStructuredATSConstants.CONSTANT_STRING_SEMICOLON))) {
			strFinalWhereUsedAttribute = strFinalWhereUsedAttribute.substring(0,
					strFinalWhereUsedAttribute.length() - 1);
		}
		
		return strFinalWhereUsedAttribute;
	}
}
