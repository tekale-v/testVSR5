package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Map;
import java.util.Map.Entry;

import matrix.util.StringList;
import matrix.util.MatrixException;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.SelectConstants;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.MultiValueSelects;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.engineering.RelToRelUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;

public class PGStructuredATSReviseUtil {

	private static final Logger logger = Logger.getLogger(PGStructuredATSReviseUtil.class.getName());
	PGStructuredATSBOMDataUtil objStructuredATSBOMDataUtil = new PGStructuredATSBOMDataUtil();
	PGStructuredATSReplaceOperationsUtil objATSReplaceOperationsUtil = new PGStructuredATSReplaceOperationsUtil();
	PGStructuredATSWhereUsedUtil objSATSWhereUsedUtil = new PGStructuredATSWhereUsedUtil();
	
	/**
	 * Method to revise the SATS object and replicate the connections from previous
	 * revision
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 */
	String reviseStructuredATSObject(Context context, String strJsonInput) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			ContextUtil.startTransaction(context, true);
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strSATSId = jsonInputData.getString(DomainConstants.SELECT_ID);
			DomainObject dobSATSObj = DomainObject.newInstance(context, strSATSId);
			boolean isLastRevision = dobSATSObj.isLastRevision(context);
			if(isLastRevision) {
				String strObjSelects = jsonInputData.getString(PGStructuredATSConstants.KEY_OBJECT_SELECTS);

				String strRevisedSATSId = reviseSATSObject(context, strSATSId);
				createAndConnectCA(context, strRevisedSATSId, PGStructuredATSConstants.STRING_CREATENEW);
				replicateConnectionsFromPreviousRevision(context, strSATSId, strRevisedSATSId);

				ContextUtil.commitTransaction(context);

				JsonArrayBuilder jsonArrObjInfo = getRevisedObjDetails(context, strRevisedSATSId, strObjSelects);
				jsonReturnObj.add(PGStructuredATSConstants.KEY_DATA, jsonArrObjInfo);
				
			} else {
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, PGStructuredATSConstants.ERROR_MSG_REVISE_LAST_REVISION);
			}

		} catch (Exception e) {
			ContextUtil.abortTransaction(context);
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_REVISE_UTIL, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
		}
		
		return jsonReturnObj.build().toString();
	}

	/**
	 * This Method revises the Structured ATS object and sets the Originator
	 * 
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws MatrixException
	 */
	public String reviseSATSObject(Context context, String strObjectId) throws MatrixException {

		DomainObject dObjectSATS = DomainObject.newInstance(context, strObjectId);
		BusinessObject boRevisedSATS = dObjectSATS.reviseObject(context, true); // revising Structured ATS with file
		boRevisedSATS.setAttributeValue(context, DomainConstants.ATTRIBUTE_ORIGINATOR, context.getUser());

		return DomainObject.newInstance(context, boRevisedSATS).getObjectId(context);
	}
	
	/**
	 * Method to get revised object basic details in Json array
	 * 
	 * @param context
	 * @param strRevisedSATSId
	 * @param strObjSelects
	 * @return
	 * @throws FrameworkException
	 */
	private JsonArrayBuilder getRevisedObjDetails(Context context, String strRevisedSATSId, String strObjSelects)
			throws FrameworkException {
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		JsonObjectBuilder jsonRevisedObjInfo = Json.createObjectBuilder();
		StringList slObjSelectList = StringUtil.split(strObjSelects, ",");
		DomainObject dobRevisedSATSObj = DomainObject.newInstance(context, strRevisedSATSId);
		Map<?, ?> mpRevisedObjMap = dobRevisedSATSObj.getInfo(context, slObjSelectList);

		for (Entry<?, ?> entry : mpRevisedObjMap.entrySet()) {
			String strKey = (String) entry.getKey();
			Object objValue = entry.getValue();
			String strObjSelectValue = "";

			if (objValue instanceof StringList) {
				strObjSelectValue = objSATSWhereUsedUtil.formatStringListData(objValue);
			} else {
				strObjSelectValue = (String) objValue;
				if (strObjSelectValue == null) {
					strObjSelectValue = PGStructuredATSConstants.STRING_NULL;
				}
			}

			jsonRevisedObjInfo.add(strKey, strObjSelectValue);
		}

		jsonArrObjInfo.add(jsonRevisedObjInfo);

		return jsonArrObjInfo;
	}

	/**
	 * Method to replicate the connections (pgATSOperation and pgATSContext) from
	 * previous revision on new revision object.
	 * 
	 * @param context
	 * @param strSATSId
	 * @param strRevisedSATSId
	 * @throws Exception
	 */
	private void replicateConnectionsFromPreviousRevision(Context context, String strSATSId, String strRevisedSATSId)
			throws Exception {

		String[] strOIDArray = new String[1];
		strOIDArray[0] = strSATSId;
		StringList slObjSelects = new StringList(PGStructuredATSConstants.SELECT_RELATED_ATS_OPERATION_IDS);

		MapList mlATSOperationIdList = DomainObject.getInfo(context, strOIDArray, slObjSelects);
		if (mlATSOperationIdList != null && !mlATSOperationIdList.isEmpty()) {
			Map<?, ?> mpATSOperationIdsMap = (Map<?, ?>) mlATSOperationIdList.get(0);
			if (mpATSOperationIdsMap != null && !mpATSOperationIdsMap.isEmpty()) {
				String strATSOperationsIds = (String) mpATSOperationIdsMap
						.get(PGStructuredATSConstants.SELECT_RELATED_ATS_OPERATION_IDS);
				if (UIUtil.isNotNullAndNotEmpty(strATSOperationsIds)) {
					StringList slATSOperationIds = StringUtil.split(strATSOperationsIds,
							SelectConstants.cSelectDelimiter);
					updateATSOperationAndATSContextOnNewRev(context, strRevisedSATSId, slATSOperationIds, strSATSId);
				}

			}
		}

	}

	/**
	 * Method to replicate the connections (pgATSOperation and pgATSContext) from
	 * previous revision on new revision object.
	 * 
	 * @param context
	 * @param strRevisedSATSId
	 * @param slATSOperationIds
	 * @param strSATSId
	 * @throws Exception 
	 */
	private void updateATSOperationAndATSContextOnNewRev(Context context, String strRevisedSATSId,
			StringList slATSOperationIds, String strSATSId) throws Exception {
		int iListSize = slATSOperationIds.size();
		for (int i = 0; i < iListSize; i++) {
			String strATSOperationId = slATSOperationIds.get(i);
			StringList slRelSelects = new StringList();
			slRelSelects.add(DomainConstants.SELECT_TO_ID);
			slRelSelects.add(DomainConstants.SELECT_TO_TYPE);
			slRelSelects.add(PGStructuredATSConstants.SELECT_RELATED_ATS_CONTEXT_IDS);

			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strATSOperationId;
			MapList mlRelatedObjInfoList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);

			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlRelatedObjInfoList.get(0);
			String strRelatedObjType = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_TYPE);

			if (PGStructuredATSConstants.TYPE_PARENT_SUB.equals(strRelatedObjType)) {
				updateConnectionsRelatedToFOP(context, strRevisedSATSId, mpObjInfoMap);
			} else if (PGStructuredATSConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC.equals(strRelatedObjType)) {
				updateConnectionsRelatedToPerfChars(context, strRevisedSATSId, mpObjInfoMap, strATSOperationId, strSATSId);
			} else {
				updateConnectionsRelatedAPPnRMP(context, strRevisedSATSId, mpObjInfoMap, strATSOperationId);
			}
		}

	}

	/**
	 * Method to replicate the connections (pgATSOperation and pgATSContext) from
	 * previous revision on new revision object for Performance Characteristics
	 * 
	 * @param context
	 * @param strRevisedSATSId
	 * @param mpObjInfoMap
	 * @param strATSOperationId
	 * @param strSATSId
	 * @throws MatrixException 
	 */
	private void updateConnectionsRelatedToPerfChars(Context context, String strRevisedSATSId, Map<?, ?> mpObjInfoMap,
			String strATSOperationId, String strSATSId) throws MatrixException {
		String strOldPerfCharObj = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);
		DomainObject dobOldPerfCharObj = DomainObject.newInstance(context, strOldPerfCharObj);
		BusinessObject boNewPerfCharObj = dobOldPerfCharObj.cloneObject(context, null);
		String strNewPerfCharId = boNewPerfCharObj.getObjectId(context);
		DomainObject dobNewPerfCharObj = DomainObject.newInstance(context, strNewPerfCharId);
		
		disconnectClonedPerfCharObjFromOldRevision(context, dobNewPerfCharObj, strSATSId);
		
		connectReferenceDocumentsToNewPerfCharObj(context, dobNewPerfCharObj, dobOldPerfCharObj);
		
		DomainObject dobSATSObj = DomainObject.newInstance(context, strRevisedSATSId);
		DomainRelationship drNewATSOperationObj = DomainRelationship.connect(context, dobSATSObj,
				PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobNewPerfCharObj);

		DomainRelationship drOldATSOperationObj = new DomainRelationship(strATSOperationId);
		Map<?, ?> mpATSOperationAttrInfoMap = drOldATSOperationObj.getAttributeMap(context);

		drNewATSOperationObj.setAttributeValues(context, mpATSOperationAttrInfoMap);
		String strNewATSOperationId = drNewATSOperationObj.getPhysicalId(context);

		Object objATSContextRelId = mpObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_ATS_CONTEXT_IDS);
		StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);

		if (slATSCtxRelIds != null) {
			int iATSCtxListSize = slATSCtxRelIds.size();
			for (int i = 0; i < iATSCtxListSize; i++) {
				String strATSContextRelId = slATSCtxRelIds.get(i);

				DomainRelationship drATSContextObj = new DomainRelationship(strATSContextRelId);
				Map<?, ?> mpATSCtxAttrMap = drATSContextObj.getAttributeMap(context);

				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strATSContextRelId;
				MapList mlRelatedObjInfoList = DomainRelationship.getInfo(context, strRelIdArray,
						new StringList(DomainConstants.SELECT_TO_ID));

				Map<?, ?> mpATSCtxObjMap = (Map<?, ?>) mlRelatedObjInfoList.get(0);
				String strRelatedBOMPerfCharObjId = (String) mpATSCtxObjMap.get(DomainConstants.SELECT_TO_ID);

				RelToRelUtil relTorelUtil = new RelToRelUtil();
				String strNewATSContextRelId = relTorelUtil.connect(context,
						PGStructuredATSConstants.REL_PG_ATS_CONTEXT, strNewATSOperationId, strRelatedBOMPerfCharObjId, false,
						true);

				DomainRelationship drNewATSContextObj = new DomainRelationship(strNewATSContextRelId);
				drNewATSContextObj.setAttributeValues(context, mpATSCtxAttrMap);

			}
		}
		
	}

	/**
	 * Method to create all 'Reference Document' connections from previous revision Perf Char object.
	 * @param context
	 * @param dobNewPerfCharObj
	 * @param dobOldPerfCharObj
	 * @throws FrameworkException 
	 */
	private void connectReferenceDocumentsToNewPerfCharObj(Context context, DomainObject dobNewPerfCharObj,
			DomainObject dobOldPerfCharObj) throws FrameworkException {
		MapList mlRelatedRefDocObjList = dobOldPerfCharObj.getRelatedObjects(context, // the eMatrix Context object
				MultiValueSelects.RELATIONSHIP_REFERENCE_DOCUMENT, // Relationship pattern
				"*", // Type pattern
				StringList.create(DomainConstants.SELECT_ID), // Object selects
				null, // Relationship selects
				true, // get From relationships
				false, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				null, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available

		if(mlRelatedRefDocObjList != null) {
			int iListSize = mlRelatedRefDocObjList.size();
			for(int i=0; i<iListSize; i++) {
				Map<?,?> mpRefDocObjMap = (Map<?, ?>) mlRelatedRefDocObjList.get(i);
				String strRefDocObjId = (String) mpRefDocObjMap.get(DomainConstants.SELECT_ID);
				DomainObject dobRefDocObj = DomainObject.newInstance(context, strRefDocObjId);
				DomainRelationship.connect(context, dobRefDocObj, MultiValueSelects.RELATIONSHIP_REFERENCE_DOCUMENT, dobNewPerfCharObj);
			}
		}
		
	}

	/**
	 * Once we clone Perf Char object then it automatically connects to previous S
	 * @param context
	 * @param strNewPerfCharId
	 * @param strSATSId
	 * @throws FrameworkException 
	 */
	private void disconnectClonedPerfCharObjFromOldRevision(Context context, DomainObject dobNewPerfCharObj,
			String strSATSId) throws FrameworkException {

		MapList mlRelatedSATSObjList = dobNewPerfCharObj.getRelatedObjects(context, // the eMatrix Context object
				PGStructuredATSConstants.REL_PG_ATS_OPERATION, // Relationship pattern
				PGStructuredATSConstants.TYPE_SATS, // Type pattern
				StringList.create(DomainConstants.SELECT_ID), // Object selects
				StringList.create(DomainConstants.SELECT_RELATIONSHIP_ID), // Relationship selects
				true, // get From relationships
				false, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				null, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available
		
		if(mlRelatedSATSObjList != null) {
			int iListSize = mlRelatedSATSObjList.size();
			for(int i=0; i<iListSize; i++) {
				Map<?,?> mpSATSObjMap = (Map<?, ?>) mlRelatedSATSObjList.get(i);
				String strRelatedSATSObjId = (String) mpSATSObjMap.get(DomainConstants.SELECT_ID);
				if(strSATSId.equals(strRelatedSATSObjId)) {
					String strRelId = (String) mpSATSObjMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					DomainRelationship.disconnect(context, strRelId);
				}
			}
		}
		
	}

	/**
	 * Method to replicate the connections (pgATSOperation and pgATSContext) from
	 * previous revision on new revision object for FOPs
	 * 
	 * @param context
	 * @param strRevisedSATSId
	 * @param mpObjInfoMap
	 * @throws Exception 
	 */
	private void updateConnectionsRelatedToFOP(Context context, String strRevisedSATSId, Map<?, ?> mpObjInfoMap)
			throws Exception {

		String strNewParentSubId = objATSReplaceOperationsUtil.createNewParentSubObj(context);

		DomainObject dobRevisedSATSObj = DomainObject.newInstance(context, strRevisedSATSId);
		DomainObject dobNewParentSubObj = DomainObject.newInstance(context, strNewParentSubId);
		DomainRelationship drNewATSOperationPSObj = DomainRelationship.connect(context, dobRevisedSATSObj,
				PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobNewParentSubObj);
		String strNewATSOperationRelId = drNewATSOperationPSObj.getPhysicalId(context);

		String strParentSubObjId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);
		String[] strOIDArray = new String[1];
		strOIDArray[0] = strParentSubObjId;
		StringList slObjSelects = new StringList(PGStructuredATSConstants.SELECT_RELATED_ATS_OPERATION_IDS);
		MapList mlATSOperationIdList = DomainObject.getInfo(context, strOIDArray, slObjSelects);
		if (mlATSOperationIdList != null && !mlATSOperationIdList.isEmpty()) {
			Map<?, ?> mpATSOperationIdsMap = (Map<?, ?>) mlATSOperationIdList.get(0);
			String strATSOperationsIds = (String) mpATSOperationIdsMap
					.get(PGStructuredATSConstants.SELECT_RELATED_ATS_OPERATION_IDS);
			if (UIUtil.isNotNullAndNotEmpty(strATSOperationsIds)) {
				StringList slATSOperationIdsList = StringUtil.split(strATSOperationsIds,
						SelectConstants.cSelectDelimiter);
				connectRelatedObjToNewParentSubObj(context, dobNewParentSubObj, slATSOperationIdsList);
			}

		}

		Object objATSContextRelId = mpObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_ATS_CONTEXT_IDS);
		StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);

		if (slATSCtxRelIds != null) {
			createNewATSContextIdForNewATSOperationId(context, strNewATSOperationRelId, slATSCtxRelIds);
		}

	}

	/**
	 * Method to create new ATS Context rels for FBOM and FBOM Substitutes
	 * 
	 * @param context
	 * @param strNewATSOperationRelId
	 * @param slATSCtxRelIds
	 * @throws FrameworkException
	 */
	private void createNewATSContextIdForNewATSOperationId(Context context, String strNewATSOperationRelId,
			StringList slATSCtxRelIds) throws FrameworkException {
		int iATSCtxListSize = slATSCtxRelIds.size();
		for (int i = 0; i < iATSCtxListSize; i++) {
			String strATSContextRelId = slATSCtxRelIds.get(i);

			DomainRelationship drATSContextObj = new DomainRelationship(strATSContextRelId);
			Map<?, ?> mpATSCtxAttrMap = drATSContextObj.getAttributeMap(context);

			StringList slRelSelects = new StringList();
			slRelSelects.add(PGStructuredATSConstants.SELECT_TO_REL_ID);
			slRelSelects.add(DomainConstants.SELECT_TO_ID);

			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strATSContextRelId;
			MapList mlRelatedObjInfoList = DomainRelationship.getInfo(context, strRelIdArray, slRelSelects);

			Map<?, ?> mpATSCtxObjMap = (Map<?, ?>) mlRelatedObjInfoList.get(0);
			String strRelatedBOMRelId = (String) mpATSCtxObjMap.get(PGStructuredATSConstants.SELECT_TO_REL_ID);
			String strRelatedFBOMParentSubId = (String) mpATSCtxObjMap.get(DomainConstants.SELECT_TO_ID);
			RelToRelUtil relTorelUtil = new RelToRelUtil();
			String strNewATSContextRelId = "";

			if (UIUtil.isNotNullAndNotEmpty(strRelatedBOMRelId)) {
				strNewATSContextRelId = relTorelUtil.connect(context, PGStructuredATSConstants.REL_PG_ATS_CONTEXT,
						strNewATSOperationRelId, strRelatedBOMRelId, false, false);

			} else if (UIUtil.isNotNullAndNotEmpty(strRelatedFBOMParentSubId)) {
				strNewATSContextRelId = relTorelUtil.connect(context, PGStructuredATSConstants.REL_PG_ATS_CONTEXT,
						strNewATSOperationRelId, strRelatedFBOMParentSubId, false, true);

			}

			DomainRelationship drNewATSContextObj = new DomainRelationship(strNewATSContextRelId);
			drNewATSContextObj.setAttributeValues(context, mpATSCtxAttrMap);

		}
	}

	/**
	 * Connect the related objects to newly created Parent Sub object
	 * 
	 * @param context
	 * @param dobNewParentSubObj
	 * @param slATSOperationIdsList
	 * @throws FrameworkException
	 */
	private void connectRelatedObjToNewParentSubObj(Context context, DomainObject dobNewParentSubObj,
			StringList slATSOperationIdsList) throws FrameworkException {
		int iListSize = slATSOperationIdsList.size();
		for (int i = 0; i < iListSize; i++) {
			String strATSOperationId = slATSOperationIdsList.get(i);
			DomainRelationship drOldATSOperationObj = new DomainRelationship(strATSOperationId);
			Map<?, ?> mpATSOperationAttrInfoMap = drOldATSOperationObj.getAttributeMap(context);

			String[] strRelIdArray = new String[1];
			strRelIdArray[0] = strATSOperationId;
			MapList mlRelatedObjInfoList = DomainRelationship.getInfo(context, strRelIdArray,
					new StringList(DomainConstants.SELECT_TO_ID));

			Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlRelatedObjInfoList.get(0);
			String strRelatedObjId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);

			DomainObject dobTargetObj = DomainObject.newInstance(context, strRelatedObjId);
			DomainRelationship drNewATSOperationObj = DomainRelationship.connect(context, dobNewParentSubObj,
					PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);

			drNewATSOperationObj.setAttributeValues(context, mpATSOperationAttrInfoMap);
		}

	}

	/**
	 * Method to replicate the connections (pgATSOperation and pgATSContext) from
	 * previous revision on new revision object for APPs and RMPs
	 * 
	 * @param context
	 * @param strRevisedSATSId
	 * @param mpObjInfoMap
	 * @param strATSOperationId
	 * @throws MatrixException
	 */
	private void updateConnectionsRelatedAPPnRMP(Context context, String strRevisedSATSId, Map<?, ?> mpObjInfoMap,
			String strATSOperationId) throws MatrixException {
		String strRelatedObjId = (String) mpObjInfoMap.get(DomainConstants.SELECT_TO_ID);
		DomainObject dobSATSObj = DomainObject.newInstance(context, strRevisedSATSId);
		DomainObject dobTargetObj = DomainObject.newInstance(context, strRelatedObjId);
		DomainRelationship drNewATSOperationObj = DomainRelationship.connect(context, dobSATSObj,
				PGStructuredATSConstants.REL_PG_ATS_OPERATION, dobTargetObj);

		DomainRelationship drOldATSOperationObj = new DomainRelationship(strATSOperationId);
		Map<?, ?> mpATSOperationAttrInfoMap = drOldATSOperationObj.getAttributeMap(context);

		drNewATSOperationObj.setAttributeValues(context, mpATSOperationAttrInfoMap);
		String strNewATSOperationId = drNewATSOperationObj.getPhysicalId(context);

		Object objATSContextRelId = mpObjInfoMap.get(PGStructuredATSConstants.SELECT_RELATED_ATS_CONTEXT_IDS);
		StringList slATSCtxRelIds = objStructuredATSBOMDataUtil.getStringListFromObject(objATSContextRelId);

		if (slATSCtxRelIds != null) {
			int iATSCtxListSize = slATSCtxRelIds.size();
			for (int i = 0; i < iATSCtxListSize; i++) {
				String strATSContextRelId = slATSCtxRelIds.get(i);

				DomainRelationship drATSContextObj = new DomainRelationship(strATSContextRelId);
				Map<?, ?> mpATSCtxAttrMap = drATSContextObj.getAttributeMap(context);

				String[] strRelIdArray = new String[1];
				strRelIdArray[0] = strATSContextRelId;
				MapList mlRelatedObjInfoList = DomainRelationship.getInfo(context, strRelIdArray,
						new StringList(PGStructuredATSConstants.SELECT_TO_REL_ID));

				Map<?, ?> mpATSCtxObjMap = (Map<?, ?>) mlRelatedObjInfoList.get(0);
				String strRelatedBOMRelId = (String) mpATSCtxObjMap.get(PGStructuredATSConstants.SELECT_TO_REL_ID);

				RelToRelUtil relTorelUtil = new RelToRelUtil();
				String strNewATSContextRelId = relTorelUtil.connect(context,
						PGStructuredATSConstants.REL_PG_ATS_CONTEXT, strNewATSOperationId, strRelatedBOMRelId, false,
						false);

				DomainRelationship drNewATSContextObj = new DomainRelationship(strNewATSContextRelId);
				drNewATSContextObj.setAttributeValues(context, mpATSCtxAttrMap);

			}
		}

	}

	/**
	 * This method connects the CO with revised Structured SATS
	 * 
	 * @param context
	 * @param object  Id for Revised SATS
	 * @return
	 * @throws Exception
	 */
	public void createAndConnectCA(Context context, String strObjectId, String strChangeActionId) throws Exception {
		ChangeAction changeAction = new ChangeAction();
		String strCAId = null;
		try {
			if (UIUtil.isNullOrEmpty(strChangeActionId)
					|| PGStructuredATSConstants.STRING_CREATENEW.equalsIgnoreCase(strChangeActionId)) {
				strCAId = (new ChangeAction()).create(context);
				changeAction = new ChangeAction(strCAId);
			} else if (UIUtil.isNotNullAndNotEmpty(strChangeActionId)
					&& !PGStructuredATSConstants.STRING_CREATENEW.equalsIgnoreCase(strChangeActionId)) {
				changeAction = new ChangeAction(strChangeActionId);
			}
			changeAction.connectAffectedItems(context, new StringList(strObjectId));
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_REVISE_UTIL, excep);
			throw excep;
		}
	}

}
