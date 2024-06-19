package com.pg.widgets.structuredats;

import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONObject;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.util.MatrixException;
import matrix.util.StringList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;


public class PGNWStructuredATSImportFromExcelUtil {
	static final Logger logger = Logger.getLogger(PGNWStructuredATSImportFromExcelUtil.class.getName());
	static final int NO_OF_SHEETS = 1;
	StringList slRelSelects = new StringList();
	StringList slProcessedIdList = new StringList();
	StringBuilder sbErrorMessage = new StringBuilder();
	Map<String,String> mpAttributeColNameMap = new HashMap<>();
	Map<String,String> mpValidateAttrMap = new HashMap<>();
	Map<String,MapList> mpBalancingMaterialInfoMap = new HashMap<>();
	Map<String,String> mpRMPChildTypeActualName = new HashMap<>();
	PGNWStructuredATSBOMDataUtil objStructuredATSBOMDataUtil = new PGNWStructuredATSBOMDataUtil();
	Set<String> setHeaderInfo = new LinkedHashSet<>();
	int iNoOfHeaderCol = 1;
		
	/**
	 * Method to import replaced ATS data from an Excel file
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws FrameworkException
	 * @throws IOException 
	 */
	String importBOMData(Context context, String strJsonInput) throws FrameworkException, IOException {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		OutputStream outputStream = null;
		File fileDirectory = null;
		File fileUploadExcel = null;
		try {	
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strATSObjId = jsonInputData.getString(DomainConstants.SELECT_ID);
			String strFileBase64 = jsonInputData.getString(PGStructuredATSConstants.KEY_FILENAME);
			
			JSONObject jsonBase64Obj = new JSONObject(strFileBase64);
			String strFileName = jsonBase64Obj.getString(PGStructuredATSConstants.KEY_FILE_NAME);
			String strBase64Data = jsonBase64Obj.getString(PGStructuredATSConstants.KEY_DATA);
			
			String[] strB64DataArray = strBase64Data.split(PGStructuredATSConstants.KEY_BASE_64);

			String strWorkspace = context.createWorkspace();
			fileDirectory = new File(strWorkspace);
			fileUploadExcel = new File(fileDirectory, strFileName);

			outputStream = new FileOutputStream(fileUploadExcel);
			byte[] decoder = Base64.getDecoder().decode(strB64DataArray[1].toString().replace("\"", ""));
			outputStream.write(decoder);
			outputStream.close();

			initializeGlobalData(context);
			
			parseExcelAndSetAttributeValues(context,fileUploadExcel,strATSObjId);

			String strErrorMsg = sbErrorMessage.toString();
			if(UIUtil.isNullOrEmpty(strErrorMsg)) {
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			} else {
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, PGStructuredATSConstants.MESSAGE_PREFIX_IMPORT+strErrorMsg);
			}
			
			JsonObject jsonBOMData = objStructuredATSBOMDataUtil.fetchSATSBOMDataJson(context,strJsonInput);
			jsonReturnObj.add(PGStructuredATSConstants.KEY_OUTPUT, jsonBOMData);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_IMPORT_FROM_EXCEL, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			
			if (fileUploadExcel != null) {
				boolean bIsFileDeleted = fileUploadExcel.delete();
				if(!bIsFileDeleted) {
					logger.log(Level.WARNING, PGStructuredATSConstants.MESSAGE_DEL_FILE_FAILED, fileUploadExcel.getName());
				}
			}
			if (fileDirectory != null) {
				boolean bIsDirDeleted = fileDirectory.delete();
				if(!bIsDirDeleted) {
					logger.log(Level.WARNING, PGStructuredATSConstants.MESSAGE_DEL_DIR_FAILED, fileDirectory.getName());
				}
			}
		}

		return jsonReturnObj.build().toString();
	}

	/**
	 * Method to initialize global variables
	 * @param context
	 * @throws MatrixException 
	 */
	private void initializeGlobalData(Context context) throws MatrixException {
		sbErrorMessage = new StringBuilder();
		slRelSelects = new StringList();
		mpAttributeColNameMap = new HashMap<>();
		mpValidateAttrMap = new HashMap<>();
		slProcessedIdList = new StringList();
		setHeaderInfo = new LinkedHashSet<>();
		mpBalancingMaterialInfoMap = new HashMap<>();
		mpRMPChildTypeActualName = new HashMap<>();
		iNoOfHeaderCol = 1;
		
		slRelSelects.add(DomainConstants.SELECT_ID);
		slRelSelects.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMINACTUALPERCENWET);
		slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMAXACTUALPERCENWET);
		slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MIN_FOP);
		slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MAX_FOP);
		slRelSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_TARGET_WEIGHT_WET);
		
		mpAttributeColNameMap.put(PGStructuredATSConstants.KEY_MIN+PGStructuredATSConstants.KEY_FOP, PGStructuredATSConstants.ATTRIBUTE_MIN_FOP);
		mpAttributeColNameMap.put(PGStructuredATSConstants.KEY_MAX+PGStructuredATSConstants.KEY_FOP, PGStructuredATSConstants.ATTRIBUTE_MAX_FOP);
		mpAttributeColNameMap.put(PGStructuredATSConstants.KEY_MIN, PGStructuredATSConstants.ATTRIBUTE_PGMINACTUALPERCENWET);
		mpAttributeColNameMap.put(PGStructuredATSConstants.KEY_MAX, PGStructuredATSConstants.ATTRIBUTE_PGMAXACTUALPERCENWET);
		mpAttributeColNameMap.put(PGStructuredATSConstants.KEY_QTY, DomainConstants.ATTRIBUTE_QUANTITY);
		mpAttributeColNameMap.put(PGStructuredATSConstants.KEY_TARGET_WET_WEIGHT, PGStructuredATSConstants.ATTRIBUTE_TARGET_WEIGHT_WET);
		
		mpValidateAttrMap.put(PGStructuredATSConstants.KEY_MIN, PGStructuredATSConstants.ATTRIBUTE_PGMINACTUALPERCENWET);
		mpValidateAttrMap.put(PGStructuredATSConstants.KEY_MAX, PGStructuredATSConstants.ATTRIBUTE_PGMAXACTUALPERCENWET);
		mpValidateAttrMap.put(PGStructuredATSConstants.KEY_QTY, DomainConstants.ATTRIBUTE_QUANTITY);
		mpValidateAttrMap.put(PGStructuredATSConstants.KEY_TARGET_WET_WEIGHT, PGStructuredATSConstants.ATTRIBUTE_TARGET_WEIGHT_WET);
		
		mpRMPChildTypeActualName.put(getTypeDisplayName(context,PGStructuredATSConstants.TYPE_PG_RAW_MATERIAL), PGStructuredATSConstants.TYPE_PG_RAW_MATERIAL);
		mpRMPChildTypeActualName.put(getTypeDisplayName(context,PGStructuredATSConstants.TYPE_PG_ARMP), PGStructuredATSConstants.TYPE_PG_ARMP);
		mpRMPChildTypeActualName.put(getTypeDisplayName(context,PGStructuredATSConstants.TYPE_PG_MASTER_RAW_MATERIAL), PGStructuredATSConstants.TYPE_PG_MASTER_RAW_MATERIAL);
	}

	/**
	 * Method to parse the excel into different Maps and set the attribute values to
	 * database
	 * 
	 * @param context
	 * @param fUploadedFile
	 * @param strATSObjId
	 * @return
	 * @throws EncryptedDocumentException
	 * @throws IOException
	 * @throws MatrixException 
	 */
	private void parseExcelAndSetAttributeValues(Context context, File fUploadedFile, String strATSObjId)
			throws EncryptedDocumentException, IOException, MatrixException {
		try (FileInputStream fileInStream = new FileInputStream(fUploadedFile);
				Workbook workbook = WorkbookFactory.create(fileInStream)) {
			Map<String, Map<Integer, String>> mpReplacedInfoMap = getReplacedDataInfo(workbook);
			Map<Integer, String> mpRelAttrIndexMap = getIndicesForAttibutes(workbook);
			iNoOfHeaderCol = setHeaderInfo.size();
			MapList mlRelAttrValueList = getAttributeValueIndexList(workbook);
			setAttributeValues(context, strATSObjId, mpReplacedInfoMap, mpRelAttrIndexMap, mlRelAttrValueList);
		}

	}

	/**
	 * Method to set the values of the attributes from excel data
	 * @param context
	 * @param strATSObjId
	 * @param mpReplacedInfoMap
	 * @param mpRelAttrIndexMap
	 * @param mlRelAttrValueList
	 * @throws MatrixException 
	 */
	private void setAttributeValues(Context context, String strATSObjId,
			Map<String, Map<Integer, String>> mpReplacedInfoMap, Map<Integer, String> mpRelAttrIndexMap,
			MapList mlRelAttrValueList) throws MatrixException {
		if(mlRelAttrValueList != null && !mlRelAttrValueList.isEmpty()) {
			for (Map.Entry<?, ?> entry : mpReplacedInfoMap.entrySet()) {
				String strBOMDataTRNInfo = (String) entry.getKey();
				Map<Integer, String> mpATSDataMap  =  (Map<Integer, String>) entry.getValue();
				
				setAttrValForReplacedItem(context,strATSObjId,mlRelAttrValueList,strBOMDataTRNInfo,mpATSDataMap,mpRelAttrIndexMap);
				
			}
		}
	}

	/**
	 * Method to set the values of the attributes from excel data for current replaced item
	 * @param context
	 * @param strATSObjId
	 * @param mlRelAttrValueList
	 * @param strBOMDataTRNInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws MatrixException 
	 */
	private void setAttrValForReplacedItem(Context context, String strATSObjId, MapList mlRelAttrValueList,
			String strBOMDataTRNInfo, Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap) throws MatrixException {
		int iDataListSize = mlRelAttrValueList.size();
		for(int i=0;i<iDataListSize;i++) {
			Map<Integer,String> mpAttrValIndexMap = (Map<Integer,String>) mlRelAttrValueList.get(i);
			boolean bSetAttrValues = false;
			for (Map.Entry<?, ?> entryATS : mpATSDataMap.entrySet()) {
				Integer iColNo = (Integer) entryATS.getKey();
				for(int j=0;j<iNoOfHeaderCol;j++) {
					if(mpAttrValIndexMap.containsKey(iColNo+j)) {
						bSetAttrValues = true; break;
					}
				}
			}

			if(bSetAttrValues) {
				setAttributeValuesForCurrentRow(context,strATSObjId,mpAttrValIndexMap,strBOMDataTRNInfo,mpATSDataMap,mpRelAttrIndexMap);
			}

		}
		
	}

	/**
	 * Method to set the values of the attributes from excel data for current replaced item
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param strBOMDataTRNInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws MatrixException 
	 */
	private void setAttributeValuesForCurrentRow(Context context, String strATSObjId,
			Map<Integer, String> mpAttrValIndexMap, String strBOMDataTRNInfo, Map<Integer, String> mpATSDataMap,
			Map<Integer, String> mpRelAttrIndexMap) throws MatrixException {
		String strParentInfo = mpAttrValIndexMap.get(0);
		
		if(strBOMDataTRNInfo.startsWith(PGStructuredATSConstants.STR_BALANCING_MATERIAL)) {
			setAttrForBalancingMaterial(context,strATSObjId,mpAttrValIndexMap,strBOMDataTRNInfo,mpATSDataMap,mpRelAttrIndexMap);
		} else {
			if(strBOMDataTRNInfo.contains(PGStructuredATSConstants.VALUE_CURRENT_SUB)) {
				if(strParentInfo.contains("|")) {
					setAttrForFBOMSub(context,strATSObjId,mpAttrValIndexMap,strBOMDataTRNInfo,mpATSDataMap,mpRelAttrIndexMap);
				} else {
					setAttrForEBOMSub(context,strATSObjId,mpAttrValIndexMap,strBOMDataTRNInfo,mpATSDataMap,mpRelAttrIndexMap);
				}
			} else {
				if(strParentInfo.contains("|")) {
					setAttrForFBOM(context,strATSObjId,mpAttrValIndexMap,strBOMDataTRNInfo,mpATSDataMap,mpRelAttrIndexMap);
				} else {
					setAttrForEBOM(context,strATSObjId,mpAttrValIndexMap,strBOMDataTRNInfo,mpATSDataMap,mpRelAttrIndexMap);
				}
			}
		}
	}
	
	/**
	 * Method to import data for 'Balancing Material'
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param strBOMDataTRNInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws FrameworkException
	 */
	private void setAttrForBalancingMaterial(Context context, String strATSObjId, Map<Integer, String> mpAttrValIndexMap,
			String strBOMDataTRNInfo, Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap) throws FrameworkException {
		MapList mlBMInfoList = null;
		String strParentInfo = mpAttrValIndexMap.get(0);
		StringList slPhaseInfoList = StringUtil.split(strParentInfo, "|");
		String strPhaseName = slPhaseInfoList.get(1).trim();
		
		StringBuilder sbPhaseTNR = new StringBuilder();
		sbPhaseTNR.append(PGStructuredATSConstants.TYPE_FORMULATION_PHASE).append(PGStructuredATSConstants.TNR_SEP);
		sbPhaseTNR.append(strPhaseName).append(PGStructuredATSConstants.TNR_SEP);
		sbPhaseTNR.append(DomainConstants.QUERY_WILDCARD);
		String strPhaseId = getObjectIdFromTNR(context,sbPhaseTNR.toString());

		String strBMObjTNR = "";
		Integer iColNo = 0;
		for (Map.Entry<Integer, String> entry : mpATSDataMap.entrySet()) {
			iColNo = entry.getKey();
			strBMObjTNR = entry.getValue();
		}
		String strBMObjId = getObjectIdFromTNR(context, strBMObjTNR);
		
		if(UIUtil.isNotNullAndNotEmpty(strPhaseId) && UIUtil.isNotNullAndNotEmpty(strBMObjId)) {
			if(mpBalancingMaterialInfoMap.containsKey(strPhaseId)) {
				mlBMInfoList = mpBalancingMaterialInfoMap.get(strPhaseId);
			} else {
				mlBMInfoList = getRelatedBMInfoList(context, strPhaseId, strATSObjId);
			}
			
			if(mlBMInfoList != null && !mlBMInfoList.isEmpty()) {
				int iListSize = mlBMInfoList.size();
				String strATSOperationRelId = "";
				for(int i=0;i<iListSize;i++) {
					Map<String,String> mpBMInfoMap = (Map<String, String>) mlBMInfoList.get(i);
					String strRelatedBMObjId = mpBMInfoMap.get(DomainConstants.SELECT_ID);
					if(strBMObjId.equals(strRelatedBMObjId)) {
						strATSOperationRelId = mpBMInfoMap.get(PGStructuredATSConstants.KEY_ATS_OPERATION_RELID);
						break;
					}
				}
				
				boolean isATSOprRelidNotEmpty = UIUtil.isNotNullAndNotEmpty(strATSOperationRelId);
				
				if(isATSOprRelidNotEmpty && !slProcessedIdList.contains(strATSOperationRelId)) {
					Map<String,Object> mpAttrValueMap = getAttributeNameValueMap(iColNo, mpAttrValIndexMap, mpRelAttrIndexMap);
					boolean isValidAttrVal = validateAttributeValues(mpAttrValueMap, strParentInfo, strBMObjTNR, "");

					if(!mpAttrValueMap.isEmpty() && isValidAttrVal) {
						DomainRelationship drATSOperation = DomainRelationship.newInstance(context, strATSOperationRelId);
						drATSOperation.setAttributeValues(context, mpAttrValueMap);
					}
					
					slProcessedIdList.add(strATSOperationRelId);
				} 
				
				if(!isATSOprRelidNotEmpty) {
					sbErrorMessage.append(PGStructuredATSConstants.MSG_NO_BM_PREFIX).append(strBMObjTNR)
							.append(PGStructuredATSConstants.MSG_NO_BM_SUFFIX).append(strPhaseName).append(".\n");
				} 
				
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_NO_BM_PREFIX).append(strBMObjTNR)
				.append(PGStructuredATSConstants.MSG_NO_BM_SUFFIX).append(strPhaseName).append(".\n");
			}

		} else {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_BM_OBJ_NOT_FOUND_PREFIX).append(strPhaseName)
					.append(PGStructuredATSConstants.MSG_BM_OBJ_NOT_FOUND);
			sbErrorMessage.append(strBMObjTNR).append(PGStructuredATSConstants.MSG_BM_OBJ_NOT_FOUND_SUFFIX).append(".\n");
		}

	}

	/**
	 * Method to get all related BM objects for current Phase
	 * @param context
	 * @param strPhaseId
	 * @param strATSObjId
	 * @return
	 * @throws FrameworkException 
	 */
	private MapList getRelatedBMInfoList(Context context, String strPhaseId, String strATSObjId) throws FrameworkException {
		MapList mlBMInfoList = new MapList();
		String strSelectRelatedATSOprRelId = PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID_FBOM_SUB;
		StringList slRelSelects = new StringList();
		slRelSelects.add(strSelectRelatedATSOprRelId);
		
		String[] strObjIdArray = new String[1];
		strObjIdArray[0] = strPhaseId;
		MapList mlObjInfoList = DomainObject.getInfo(context, strObjIdArray, slRelSelects);
		Map<?, ?> mpObjInfoMap = (Map<?, ?>) mlObjInfoList.get(0);
		if(mpObjInfoMap.containsKey(strSelectRelatedATSOprRelId)) {
			Object objATSOperationRelId =  mpObjInfoMap.get(strSelectRelatedATSOprRelId);
			StringList slATSOprRelIdList = objStructuredATSBOMDataUtil.getStringListFromObject(objATSOperationRelId);

			if(slATSOprRelIdList != null && !slATSOprRelIdList.isEmpty()) {
				int iATSOprListSize = slATSOprRelIdList.size();
				for(int i=0;i<iATSOprListSize;i++) {
					String strATSOperationRelId = slATSOprRelIdList.get(i);
					
					slRelSelects = new StringList();
					slRelSelects.add(DomainConstants.SELECT_TO_ID);
					slRelSelects.add(DomainConstants.SELECT_FROM_ID);
					
					String[] strATSOprRelIdArray = new String[1];
					strATSOprRelIdArray[0] = strATSOperationRelId;
					MapList mlConnectionAttrList = DomainRelationship.getInfo(context, strATSOprRelIdArray, slRelSelects);

					Map<?, ?> mpATSOprInfoMap = (Map<?, ?>) mlConnectionAttrList.get(0);
					String strRelatedATSId = (String) mpATSOprInfoMap.get(DomainConstants.SELECT_FROM_ID);
					if(strATSObjId.equals(strRelatedATSId)) {
						Map<String,String> mpBMInfoMap = new HashMap<>();
						mpBMInfoMap.put(PGStructuredATSConstants.KEY_ATS_OPERATION_RELID, strATSOperationRelId);
						String strBMObjId = (String) mpATSOprInfoMap.get(DomainConstants.SELECT_TO_ID);
						mpBMInfoMap.put(DomainConstants.SELECT_ID, strBMObjId);
						
						mlBMInfoList.add(mpBMInfoMap);
					}
				}
			}
			mpBalancingMaterialInfoMap.put(strPhaseId, mlBMInfoList);
		}

		return mlBMInfoList;
	}

	/**
	 * Method to set the value for FBOM Substitute
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param strBOMDataTRNInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws MatrixException 
	 */
	private void setAttrForFBOMSub(Context context, String strATSObjId, Map<Integer, String> mpAttrValIndexMap,
			String strBOMDataTRNInfo, Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap) throws MatrixException {
		String strParentInfo = mpAttrValIndexMap.get(0).trim();
		StringList slBOMPartInfoList = StringUtil.split(strBOMDataTRNInfo, PGStructuredATSConstants.COMMA_SEP);
		String strTNRInfo = slBOMPartInfoList.get(0);
		String strPartColNo = slBOMPartInfoList.get(1);
		String strPartId = getObjectIdFromTNR(context,strTNRInfo);
		
		String strPrimaryObjId = "";
		if(slBOMPartInfoList.size() > 3) {
			String strPrimaryTNRInfo = slBOMPartInfoList.get(3);
			strPrimaryObjId = getObjectIdFromTNR(context,strPrimaryTNRInfo);
			if(UIUtil.isNullOrEmpty(strPrimaryObjId)) {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_SUB+strTNRInfo+PGStructuredATSConstants.MSG_FOR_BOM_DATA+strParentInfo+"'\n");
				return;
			}
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strPartId)) {
			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_NAME);
			
			StringList slRelationshipSelects = new StringList();
			slRelationshipSelects.addAll(slRelSelects);
			slRelationshipSelects.add(PGStructuredATSConstants.SELECT_PARENT_SUB_FOR_FBOM_SUB);
			
			DomainObject dobPartObj = DomainObject.newInstance(context, strPrimaryObjId);
			MapList mlRelatedPhaseObjects = dobPartObj.getRelatedObjects(context, // the eMatrix Context object
					PGStructuredATSConstants.RELATIONSHIP_FBOM, // Relationship pattern
					PGStructuredATSConstants.TYPE_FORMULATION_PHASE, // Type pattern
					slObjSelects, // Object selects
					slRelationshipSelects, // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			Map<String,Map<String,String>> mpRelIdAttrValMap = new HashMap<>();
			if(mlRelatedPhaseObjects != null && !mlRelatedPhaseObjects.isEmpty()) {
				StringList slPhaseInfoList = StringUtil.split(strParentInfo, "|");
				String strPhaseName = slPhaseInfoList.get(1).trim();
				int iListSize = mlRelatedPhaseObjects.size();
				for(int i=0;i<iListSize;i++) {
					Map<?,?> mpPhaseInfoMap = (Map<?, ?>) mlRelatedPhaseObjects.get(i);
					String strRelObjName = (String) mpPhaseInfoMap.get(DomainConstants.SELECT_NAME);
					if(strRelObjName.equals(strPhaseName)) {
						Object objParentSubInfo = mpPhaseInfoMap.get(PGStructuredATSConstants.SELECT_PARENT_SUB_FOR_FBOM_SUB);
						if(UIUtil.isNotNullAndNotEmpty(objParentSubInfo.toString())) {
							updateParentSubInfoList(context,mpRelIdAttrValMap,objParentSubInfo,strPartId);
						}
					}
				}

			}
			
			String strParentSubId = getRelIdFromAttrValCompare(mpRelIdAttrValMap, strPartColNo, mpAttrValIndexMap, mpRelAttrIndexMap);

			if(UIUtil.isNotNullAndNotEmpty(strParentSubId)) {
				setRelAttributesForFBOMSub(context,strParentSubId,strATSObjId,mpAttrValIndexMap,mpATSDataMap,mpRelAttrIndexMap,strTNRInfo);
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_CHILD+strTNRInfo+PGStructuredATSConstants.MSG_FOR_BOM_DATA+strParentInfo+"' OR"
						+ PGStructuredATSConstants.MSG_MIN_MAX_QTY_NOT_MATCHNING+strTNRInfo+"' \n");
			}

		} else {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_OBJ+strTNRInfo+"\n");
		}
		
	}

	/**
	 * Method to set the attributes for FBOM Sub
	 * @param context
	 * @param strParentSubId
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @param strTNRInfo
	 * @throws MatrixException 
	 */
	private void setRelAttributesForFBOMSub(Context context, String strParentSubId, String strATSObjId,
			Map<Integer, String> mpAttrValIndexMap, Map<Integer, String> mpATSDataMap,
			Map<Integer, String> mpRelAttrIndexMap, String strTNRInfo) throws MatrixException {
		String strParentInfo = mpAttrValIndexMap.get(0).trim();
		MapList mlRelatedATSOperationList = DomainObject.getInfo(context, new String[] { strParentSubId },
				new StringList(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID_FBOM_SUB));

		if(mlRelatedATSOperationList != null && !mlRelatedATSOperationList.isEmpty()) {
			Map<?,?> mpATSOpInfMap = (Map<?, ?>) mlRelatedATSOperationList.get(0);
			String strATSOpIds = (String) mpATSOpInfMap.get(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID_FBOM_SUB);
			StringList slATSOpIdList = StringUtil.split(strATSOpIds, SelectConstants.cSelectDelimiter);

			if(slATSOpIdList != null && !slATSOpIdList.isEmpty()) {
				String strATSOprRelId = "";
				int iListSize = slATSOpIdList.size();
				for(int i=0;i<iListSize;i++) {
					String strATSOperationId = slATSOpIdList.get(i);
					MapList mlATSInfoList = DomainRelationship.getInfo(context, new String[] {strATSOperationId}, new StringList(PGStructuredATSConstants.CONST_FROM));
					Map<?,?> mpATSInfoMap = (Map<?, ?>) mlATSInfoList.get(0);
					String strRelatedATSId = (String) mpATSInfoMap.get(PGStructuredATSConstants.CONST_FROM);
							
					if(strRelatedATSId.equals(strATSObjId)) {
						strATSOprRelId = strATSOperationId;
						break;
					} 
				}

				if(UIUtil.isNotNullAndNotEmpty(strATSOprRelId)) {
					setATSOprAttForFBOM(context,strATSOprRelId,mpATSDataMap,mpAttrValIndexMap,mpRelAttrIndexMap,strTNRInfo);
				} else {
					sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA+strParentInfo+PGStructuredATSConstants.MSG_THE_OBJ+strTNRInfo+PGStructuredATSConstants.MSG_NOT_REPLACED);
				}
				
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA+strParentInfo+PGStructuredATSConstants.MSG_THE_OBJ+strTNRInfo+PGStructuredATSConstants.MSG_NOT_REPLACED);
			}

			
		} else {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA+strParentInfo+PGStructuredATSConstants.MSG_THE_OBJ+strTNRInfo+PGStructuredATSConstants.MSG_NOT_REPLACED);
		}
		
	}

	/**
	 *  Method to get 'Parent Sub' ids for FBOM Sub
	 * @param context
	 * @param mpRelIdAttrValMap
	 * @param objParentSubInfo
	 * @param strPartId
	 * @throws FrameworkException
	 */
	private void updateParentSubInfoList(Context context, Map<String,Map<String,String>> mpRelIdAttrValMap, Object objParentSubInfo,
			String strPartId) throws FrameworkException {
		StringList slParentSubIdist = new StringList();
		if (objParentSubInfo instanceof StringList) {
			slParentSubIdist.addAll((StringList) objParentSubInfo);
		} else {
			slParentSubIdist.add((String) objParentSubInfo);
		}

		int iListSize = slParentSubIdist.size();
		StringList slRelationshipSelects = new StringList();
		slRelationshipSelects.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		slRelationshipSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MIN_FOP);
		slRelationshipSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_MAX_FOP);
		slRelationshipSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_TARGET_WEIGHT_WET);

		for (int i = 0; i < iListSize; i++) {
			String strParentSubId = slParentSubIdist.get(i);
			DomainObject dobParentSub = DomainObject.newInstance(context, strParentSubId);
			MapList mlRelatedChildParts = dobParentSub.getRelatedObjects(context, // the eMatrix Context object
					PGStructuredATSConstants.RELATIONSHIP_FBOM, // Relationship pattern
					"*", // Type pattern
					new StringList(DomainConstants.SELECT_ID), // Object selects
					slRelationshipSelects, // Relationship selects
					false, // get From relationships
					true, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available
			
			if(mlRelatedChildParts != null && !mlRelatedChildParts.isEmpty()) {
				int iPartListSize = mlRelatedChildParts.size();
				for (int j = 0; j < iPartListSize; j++) {
					Map<?,?> mpPartInfoMap = (Map<?, ?>) mlRelatedChildParts.get(j);
					String strRelatedPartId = (String) mpPartInfoMap.get(DomainConstants.SELECT_ID);
					if (strRelatedPartId.equals(strPartId)) {
						Map<String,String> mpRelAttrValMap = getRelAttrDispValMap(mpPartInfoMap, PGStructuredATSConstants.KEY_FOP);
						mpRelIdAttrValMap.put(strParentSubId, mpRelAttrValMap);
					}
				}
			}
			
		}
	}

	/**
	 * Method to set the value for EBOM Substitute
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param strBOMDataTRNInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws MatrixException 
	 */
	private void setAttrForEBOMSub(Context context, String strATSObjId, Map<Integer, String> mpAttrValIndexMap,
			String strBOMDataTRNInfo, Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap) throws MatrixException {
		String strParentInfo = mpAttrValIndexMap.get(0).trim();
		StringList slBOMPartInfoList = StringUtil.split(strBOMDataTRNInfo, PGStructuredATSConstants.COMMA_SEP);
		String strTNRInfo = slBOMPartInfoList.get(0);
		String strPartColNo = slBOMPartInfoList.get(1);
		String strPartId = getObjectIdFromTNR(context,strTNRInfo);
		
		String strPrimaryObjId = "";
		if(slBOMPartInfoList.size() > 3) {
			String strPrimaryTNRInfo = slBOMPartInfoList.get(3);
			strPrimaryObjId = getObjectIdFromTNR(context,strPrimaryTNRInfo);
			if(UIUtil.isNullOrEmpty(strPrimaryObjId)) {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_SUB+strTNRInfo+PGStructuredATSConstants.MSG_FOR_BOM_DATA+strParentInfo+"'\n");
				return;
			}
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strPartId)) {
			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_NAME);
			slObjSelects.add(DomainConstants.SELECT_REVISION);
			
			StringList slRelationshipSelects = new StringList();
			slRelationshipSelects.addAll(slRelSelects);
			slRelationshipSelects.add(PGStructuredATSConstants.SELECT_EBOM_SUB_RELID);
			
			DomainObject dobPartObj = DomainObject.newInstance(context, strPrimaryObjId);
			MapList mlRelatedAPPObjects = dobPartObj.getRelatedObjects(context, // the eMatrix Context object
					DomainConstants.RELATIONSHIP_EBOM, // Relationship pattern
					PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART, // Type pattern
					slObjSelects, // Object selects
					slRelationshipSelects, // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			Map<String,Map<String,String>> mpRelIdAttrValMap = new HashMap<>();
			if(mlRelatedAPPObjects != null && !mlRelatedAPPObjects.isEmpty()) {
				StringList slAPPInfoList = StringUtil.split(strParentInfo, "-");
				if(slAPPInfoList.size() > 1) {
					String strAPPName = slAPPInfoList.get(0);
					String strAPPRev = slAPPInfoList.get(1);
					
					int iListSize = mlRelatedAPPObjects.size();
					for(int i=0;i<iListSize;i++) {
						Map<?,?> mpAPPInfoMap = (Map<?, ?>) mlRelatedAPPObjects.get(i);
						String strRelObjName = (String) mpAPPInfoMap.get(DomainConstants.SELECT_NAME);
						String strRelObjRev = (String) mpAPPInfoMap.get(DomainConstants.SELECT_REVISION);
						if(strRelObjName.equals(strAPPName) && strRelObjRev.equals(strAPPRev)) {
							Object objEBOMSubIds = mpAPPInfoMap.get(PGStructuredATSConstants.SELECT_EBOM_SUB_RELID);
							if(UIUtil.isNotNullAndNotEmpty(objEBOMSubIds.toString())) {
								updateEBOMSubRelList(context,mpRelIdAttrValMap,objEBOMSubIds,strPartId);
							}
						}
				    }
				} else {
					sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_NAME_REV+strParentInfo+"'\n");
				}

			}

			String strEBOMSubId = getRelIdFromAttrValCompare(mpRelIdAttrValMap, strPartColNo, mpAttrValIndexMap, mpRelAttrIndexMap);

			if(UIUtil.isNotNullAndNotEmpty(strEBOMSubId)) {
				setRelAttributesForBOM(context,strEBOMSubId,strATSObjId,mpAttrValIndexMap,mpATSDataMap,mpRelAttrIndexMap,strTNRInfo);
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_CHILD+strTNRInfo+PGStructuredATSConstants.MSG_FOR_BOM_DATA+strParentInfo+"' OR"
						+ PGStructuredATSConstants.MSG_MIN_MAX_QTY_NOT_MATCHNING+strTNRInfo+"' \n");
			}
			
		} else {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_OBJ+strTNRInfo+"\n");
		}
		
	}

	/**
	 * Method to get EBOM Sub rel id list
	 * @param context
	 * @param mpRelIdAttrValMap
	 * @param objEBOMSubIds
	 * @param strPartId
	 * @throws FrameworkException
	 */
	private void updateEBOMSubRelList(Context context, Map<String,Map<String,String>> mpRelIdAttrValMap, Object objEBOMSubIds,
			String strPartId) throws FrameworkException {

		StringList slRelIdList = new StringList();
		if (objEBOMSubIds instanceof StringList) {
			slRelIdList.addAll((StringList) objEBOMSubIds);
		} else {
			slRelIdList.add((String) objEBOMSubIds);
		}

		StringList slRelationshipSelects = new StringList();
		slRelationshipSelects.add(PGStructuredATSConstants.CONST_TO);
		slRelationshipSelects.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		slRelationshipSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMINACTUALPERCENWET);
		slRelationshipSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_PGMAXACTUALPERCENWET);
		slRelationshipSelects.add(PGStructuredATSConstants.SELECT_ATTRIBUTE_TARGET_WEIGHT_WET);
		
		int iListSize = slRelIdList.size();
		for (int i = 0; i < iListSize; i++) {
			String strEBOMSubRelId = slRelIdList.get(i);
			MapList mlEBOMSubList = DomainRelationship.getInfo(context, new String[] { strEBOMSubRelId },
					slRelationshipSelects);
			Map<?, ?> mpEBOMSubMap = (Map<?, ?>) mlEBOMSubList.get(0);
			String strRelatedPartId = (String) mpEBOMSubMap.get(PGStructuredATSConstants.CONST_TO);

			if (strPartId.equals(strRelatedPartId)) {
				Map<String,String> mpRelAttrValMap = getRelAttrDispValMap(mpEBOMSubMap, "");
				mpRelIdAttrValMap.put(strEBOMSubRelId, mpRelAttrValMap);
			}
		}

	}

	/**
	 * Method to get attributes and display value Map
	 * @param mpObjInfoMap
	 * @return
	 */
	private Map<String, String> getRelAttrDispValMap(Map<?, ?> mpObjInfoMap, String strSuffix) {
		Map<String,String> mpRelAttrValMap = new HashMap<>();
		Iterator<String> setIterator = setHeaderInfo.iterator();
		while(setIterator.hasNext()) {
			String strColDisplayName = setIterator.next();
			String strDispKey = strColDisplayName;
			if(PGStructuredATSConstants.KEY_MIN.equals(strDispKey) || PGStructuredATSConstants.KEY_MAX.equals(strDispKey)) {
				strDispKey = strDispKey + strSuffix;
			}
			if(mpAttributeColNameMap.containsKey(strDispKey)) {
				String strAttrName = mpAttributeColNameMap.get(strDispKey);
				String strAttrSelect = DomainObject.getAttributeSelect(strAttrName);
				String strAttrValue = (String) mpObjInfoMap.get(strAttrSelect);
				mpRelAttrValMap.put(strColDisplayName, strAttrValue);
			}
		}
		return mpRelAttrValMap;
	}

	/**
	 * Method to set the value for EBOM
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param strBOMDataTRNInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws MatrixException 
	 */
	private void setAttrForEBOM(Context context, String strATSObjId, Map<Integer, String> mpAttrValIndexMap,
			String strBOMDataTRNInfo, Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap) throws MatrixException {
		StringList slBOMPartInfoList = StringUtil.split(strBOMDataTRNInfo, PGStructuredATSConstants.COMMA_SEP);
		String strTNRInfo = slBOMPartInfoList.get(0);
		String strPartColNo = slBOMPartInfoList.get(1);
		String strPartId = getObjectIdFromTNR(context,strTNRInfo);
		
		if(UIUtil.isNotNullAndNotEmpty(strPartId)) {
			String strParentInfo = mpAttrValIndexMap.get(0).trim();
			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_NAME);
			slObjSelects.add(DomainConstants.SELECT_REVISION);
			
			DomainObject dobPartObj = DomainObject.newInstance(context, strPartId);
			MapList mlRelatedAPPObjects = dobPartObj.getRelatedObjects(context, // the eMatrix Context object
					DomainConstants.RELATIONSHIP_EBOM, // Relationship pattern
					PGStructuredATSConstants.TYPE_ASSEMBLED_PRODUCT_PART, // Type pattern
					slObjSelects, // Object selects
					slRelSelects, // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			Map<String,Map<String,String>> mpRelIdAttrValMap = new HashMap<>();
			if(mlRelatedAPPObjects != null && !mlRelatedAPPObjects.isEmpty()) {
				StringList slAPPInfoList = StringUtil.split(strParentInfo, "-");
				if(slAPPInfoList.size() > 1) {
					String strAPPName = slAPPInfoList.get(0);
					String strAPPRev = slAPPInfoList.get(1);
					
					int iListSize = mlRelatedAPPObjects.size();
					for(int i=0;i<iListSize;i++) {
						Map<?,?> mpAPPInfoMap = (Map<?, ?>) mlRelatedAPPObjects.get(i);
						String strRelObjName = (String) mpAPPInfoMap.get(DomainConstants.SELECT_NAME);
						String strRelObjRev = (String) mpAPPInfoMap.get(DomainConstants.SELECT_REVISION);
						if(strRelObjName.equals(strAPPName) && strRelObjRev.equals(strAPPRev)) {
							String strEBOMId = (String) mpAPPInfoMap.get(DomainConstants.SELECT_ID);
							Map<String,String> mpRelAttrValMap = getRelAttrDispValMap(mpAPPInfoMap, "");
							mpRelIdAttrValMap.put(strEBOMId, mpRelAttrValMap);
						}
				    }
				} else {
					sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_NAME_REV+strParentInfo+"'\n");
				}

			}
			
			String strEBOMId = getRelIdFromAttrValCompare(mpRelIdAttrValMap, strPartColNo, mpAttrValIndexMap, mpRelAttrIndexMap);

			if(UIUtil.isNotNullAndNotEmpty(strEBOMId)) {
				setRelAttributesForBOM(context,strEBOMId,strATSObjId,mpAttrValIndexMap,mpATSDataMap,mpRelAttrIndexMap,strTNRInfo);
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_CHILD+strTNRInfo+PGStructuredATSConstants.MSG_FOR_BOM_DATA+strParentInfo+"' OR "
						+ PGStructuredATSConstants.MSG_MIN_MAX_QTY_NOT_MATCHNING+strTNRInfo+"' \n");
			}
			
		} else {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_OBJ+strTNRInfo+"\n");
		}
		
	}

	/**
	 * Method to handle multiple connections between same parent child
	 * @param mpRelIdAttrValMap
	 * @param strPartColNo
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @return
	 */
	private String getRelIdFromAttrValCompare(Map<String, Map<String, String>> mpRelIdAttrValMap, String strPartColNo,
			Map<Integer, String> mpAttrValIndexMap, Map<Integer, String> mpRelAttrIndexMap) {
		String strRelId = "";
		Integer iPartColNo = Integer.parseInt(strPartColNo);
		Map<String,Float> mpColValueMap = getColumnValuesForPart(iPartColNo,mpAttrValIndexMap,mpRelAttrIndexMap);
		for (Map.Entry<String, Map<String, String>> entry : mpRelIdAttrValMap.entrySet()) {
			String strCurrentRelId = entry.getKey();
			boolean bIsValidRelId = true;
			Map<String, String> mpRelAttrValMap = entry.getValue();
			for (Map.Entry<String, String> entryAttInfo : mpRelAttrValMap.entrySet()) {
				String strKey = entryAttInfo.getKey();
				String strRelAttrVal = entryAttInfo.getValue();

				if(UIUtil.isNullOrEmpty(strRelAttrVal)) {
					if(mpColValueMap.containsKey(strKey)) {
						bIsValidRelId = false;
					}
				} else {
					if(mpColValueMap.containsKey(strKey)) {
						Float fColValue = mpColValueMap.get(strKey);
						Float fRelAttrValue = Float.parseFloat(strRelAttrVal);
						
						if (Float.compare(fColValue, fRelAttrValue) != 0) {
							bIsValidRelId = false;
						}
					} else {
						bIsValidRelId = false;
					}
				}
			}

			if(bIsValidRelId && !slProcessedIdList.contains(strCurrentRelId)) {
				strRelId = strCurrentRelId;
				slProcessedIdList.add(strCurrentRelId);
				break;
			}
		}
		return strRelId;
	}

	/**
	 * Method to get column values from excel for Part
	 * @param iPartColNo
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @return
	 */
	private Map<String, Float> getColumnValuesForPart(Integer iPartColNo, Map<Integer, String> mpAttrValIndexMap,
			Map<Integer, String> mpRelAttrIndexMap) {
		Map<String,Float> mpColValueMap = new HashMap<>();
		for(int i=0;i<iNoOfHeaderCol;i++) {
			if(mpAttrValIndexMap.containsKey(iPartColNo)) {
				String strAttrVal = mpAttrValIndexMap.get(iPartColNo);
				String strColName = mpRelAttrIndexMap.get(iPartColNo);

				mpColValueMap.put(strColName, Float.parseFloat(strAttrVal));
			}
			
			iPartColNo = iPartColNo + 1;
		}
		return mpColValueMap;
	}

	/**
	 * Method to set the value for FBOM
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param strBOMDataTRNInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws MatrixException 
	 */
	private void setAttrForFBOM(Context context, String strATSObjId, Map<Integer, String> mpAttrValIndexMap,
			String strBOMDataTRNInfo, Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap) throws MatrixException {
		StringList slBOMPartInfoList = StringUtil.split(strBOMDataTRNInfo, PGStructuredATSConstants.COMMA_SEP);
		String strTNRInfo = slBOMPartInfoList.get(0);
		String strPartColNo = slBOMPartInfoList.get(1);
		String strPartId = getObjectIdFromTNR(context,strTNRInfo);

		if(UIUtil.isNotNullAndNotEmpty(strPartId)) {
			String strParentInfo = mpAttrValIndexMap.get(0);
			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_NAME);
			
			DomainObject dobPartObj = DomainObject.newInstance(context, strPartId);
			MapList mlRelatedPhaseObjects = dobPartObj.getRelatedObjects(context, // the eMatrix Context object
					PGStructuredATSConstants.RELATIONSHIP_FBOM, // Relationship pattern
					PGStructuredATSConstants.TYPE_FORMULATION_PHASE, // Type pattern
					slObjSelects, // Object selects
					slRelSelects, // Relationship selects
					true, // get From relationships
					false, // get To relationships
					(short) 1, // the number of levels to expand, 0 equals expand all.
					null, // Object where clause
					null, // Relationship where clause
					0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
						// data available

			Map<String,Map<String,String>> mpRelIdAttrValMap = new HashMap<>();
			if(mlRelatedPhaseObjects != null && !mlRelatedPhaseObjects.isEmpty()) {
				StringList slPhaseInfoList = StringUtil.split(strParentInfo, "|");
				String strPhaseName = slPhaseInfoList.get(1).trim();
				int iListSize = mlRelatedPhaseObjects.size();
				for(int i=0;i<iListSize;i++) {
					Map<?,?> mpPhaseInfoMap = (Map<?, ?>) mlRelatedPhaseObjects.get(i);
					String strRelObjName = (String) mpPhaseInfoMap.get(DomainConstants.SELECT_NAME);
					if(strRelObjName.equals(strPhaseName)) {
						String strFBOMId = (String) mpPhaseInfoMap.get(DomainConstants.SELECT_ID);
						Map<String,String> mpRelAttrValMap = getRelAttrDispValMap(mpPhaseInfoMap, PGStructuredATSConstants.KEY_FOP);
						mpRelIdAttrValMap.put(strFBOMId, mpRelAttrValMap);
					}
				}

			}
			
			String strFBOMId = getRelIdFromAttrValCompare(mpRelIdAttrValMap, strPartColNo, mpAttrValIndexMap, mpRelAttrIndexMap);
			if(UIUtil.isNotNullAndNotEmpty(strFBOMId)) {
				setRelAttributesForBOM(context,strFBOMId,strATSObjId,mpAttrValIndexMap,mpATSDataMap,mpRelAttrIndexMap,strTNRInfo);
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_CHILD+strTNRInfo+PGStructuredATSConstants.MSG_FOR_BOM_DATA+strParentInfo+"' OR "
						+ PGStructuredATSConstants.MSG_MIN_MAX_QTY_NOT_MATCHNING+strTNRInfo+"' \n");
			}

		} else {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_UNABLE_TO_FIND_OBJ+strTNRInfo+"\n");
		}

	}

	/**
	 * Method to set rel attributes for FBOM and EBOM
	 * 
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws MatrixException 
	 */
	private void setRelAttributesForBOM(Context context, String strBOMId, String strATSObjId,
			Map<Integer, String> mpAttrValIndexMap, Map<Integer, String> mpATSDataMap,
			Map<Integer, String> mpRelAttrIndexMap, String strTNRInfo) throws MatrixException {
		
		String strParentInfo = mpAttrValIndexMap.get(0);
		StringList slRelationshipSelect = new StringList(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID);
		String[] strRelIdArray = new String[1];
		strRelIdArray[0] = strBOMId;
		MapList mlRelatedATSOperationList = DomainRelationship.getInfo(context, strRelIdArray, slRelationshipSelect);
		if(mlRelatedATSOperationList != null && !mlRelatedATSOperationList.isEmpty()) {
			Map<?,?> mpATSOpInfMap = (Map<?, ?>) mlRelatedATSOperationList.get(0);
			if(mpATSOpInfMap.containsKey(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID)) {
				StringList slATSOpIdList = new StringList();
				Object objATSOperationId = mpATSOpInfMap.get(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID);
				if(UIUtil.isNotNullAndNotEmpty(objATSOperationId.toString())) {
					if(objATSOperationId instanceof StringList) {
						slATSOpIdList.addAll((StringList)objATSOperationId);
					} else {
						slATSOpIdList.add((String)objATSOperationId);
					}
					
					String strATSOprRelId = "";
					int iListSize = slATSOpIdList.size();
					for(int i=0;i<iListSize;i++) {
						String strATSOperationId = slATSOpIdList.get(i);
						MapList mlATSInfoList = DomainRelationship.getInfo(context, new String[] {strATSOperationId}, new StringList(PGStructuredATSConstants.CONST_FROM));
						Map<?,?> mpATSInfoMap = (Map<?, ?>) mlATSInfoList.get(0);
						String strRelatedATSId = (String) mpATSInfoMap.get(PGStructuredATSConstants.CONST_FROM);
								
						if(strRelatedATSId.equals(strATSObjId)) {
							strATSOprRelId = strATSOperationId;
							break;
						} 
					}
					
					if(UIUtil.isNotNullAndNotEmpty(strATSOprRelId)) {
						if(strParentInfo.contains("|")) {
							setATSOprAttForFBOM(context,strATSOprRelId,mpATSDataMap,mpAttrValIndexMap,mpRelAttrIndexMap,strTNRInfo);
						} else {
							//EBOM
							Map<String,String> mpATSIdToObjMap = getATSOprReplacedDataMap(context,new StringList(strATSOprRelId));
							setATSOperationRelAttrValues(context, mpATSIdToObjMap, mpATSDataMap, mpAttrValIndexMap, mpRelAttrIndexMap,strTNRInfo);
						}
						
					} else {
						sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA+strParentInfo+PGStructuredATSConstants.MSG_THE_OBJ+strTNRInfo+PGStructuredATSConstants.MSG_NOT_REPLACED);
					}
					
				} else {
					sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA+strParentInfo+PGStructuredATSConstants.MSG_THE_OBJ+strTNRInfo+PGStructuredATSConstants.MSG_NOT_REPLACED);
				}

			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA+strParentInfo+PGStructuredATSConstants.MSG_THE_OBJ+strTNRInfo+PGStructuredATSConstants.MSG_NOT_REPLACED);
			}
		} else {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA+strParentInfo+PGStructuredATSConstants.MSG_THE_OBJ+strTNRInfo+PGStructuredATSConstants.MSG_NOT_REPLACED);
		}
	}

	/**
	 * Method to set rel attributes for FBOM
	 * @param context
	 * @param strATSOprRelId
	 * @param mpATSDataMap
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @param strParentInfo
	 * @throws MatrixException 
	 */
	private void setATSOprAttForFBOM(Context context, String strATSOprRelId, Map<Integer, String> mpATSDataMap,
			Map<Integer, String> mpAttrValIndexMap, Map<Integer, String> mpRelAttrIndexMap,String strTNRInfo) throws MatrixException {
		StringList slRelationshipSelect = new StringList(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID_FOR_FBOM);
		String[] strRelIdArray = new String[1];
		strRelIdArray[0] = strATSOprRelId;
		MapList mlRelatedATSOperationList = DomainRelationship.getInfo(context, strRelIdArray, slRelationshipSelect);
		
		if(mlRelatedATSOperationList != null && !mlRelatedATSOperationList.isEmpty()) {
			Map<?,?> mpATSOpInfMap = (Map<?, ?>) mlRelatedATSOperationList.get(0);
			if(mpATSOpInfMap.containsKey(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID_FOR_FBOM)) {
				StringList slATSOpIdList = new StringList();
				Object objATSOperationId = mpATSOpInfMap.get(PGStructuredATSConstants.SELECT_ATS_OPERSTION_ID_FOR_FBOM);
				if(UIUtil.isNotNullAndNotEmpty(objATSOperationId.toString())) {
					if(objATSOperationId instanceof StringList) {
						slATSOpIdList.addAll((StringList)objATSOperationId);
					} else {
						slATSOpIdList.add((String)objATSOperationId);
					}
				}
							
				Map<String,String> mpATSIdToObjMap = getATSOprReplacedDataMap(context,slATSOpIdList);
				
				setATSOperationRelAttrValues(context, mpATSIdToObjMap, mpATSDataMap, mpAttrValIndexMap, mpRelAttrIndexMap,strTNRInfo);
			}
		}
		
	}

	/**
	 * Generic method to set ATS Operation rel id attributes
	 * 
	 * @param context
	 * @param mpATSIdToObjMap
	 * @param mpATSDataMap
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @throws FrameworkException 
	 */
	private void setATSOperationRelAttrValues(Context context, Map<String, String> mpATSIdToObjMap,
			Map<Integer, String> mpATSDataMap, Map<Integer, String> mpAttrValIndexMap,
			Map<Integer, String> mpRelAttrIndexMap, String strTNRInfo) throws FrameworkException {
		String strParentInfo = mpAttrValIndexMap.get(0);
		for (Map.Entry<Integer, String> entry : mpATSDataMap.entrySet()) {
			String strReplacedPartTNR = entry.getValue();
			if (mpATSIdToObjMap.containsKey(strReplacedPartTNR)) {
				String strATSRelId = mpATSIdToObjMap.get(strReplacedPartTNR);
				Integer iColNo = entry.getKey();
				Map<String,Object> mpAttrValueMap = getAttributeNameValueMap(iColNo, mpAttrValIndexMap, mpRelAttrIndexMap);

				boolean isValidAttrVal = validateAttributeValues(mpAttrValueMap, strParentInfo, strReplacedPartTNR, strTNRInfo);

				if(!mpAttrValueMap.isEmpty() && isValidAttrVal) {
					DomainRelationship drATSOperation = DomainRelationship.newInstance(context, strATSRelId);
					drATSOperation.setAttributeValues(context, mpAttrValueMap);
				}
				
			} else {
				
				boolean bErrorData = false;
				Integer iColNo = (Integer) entry.getKey();
				for(int j=0;j<iNoOfHeaderCol;j++) {
					if(mpAttrValIndexMap.containsKey(iColNo+j)) {
						bErrorData = true; break;
					}
				}

				if(bErrorData) {
					sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FOR_BOM_DATA + strParentInfo + PGStructuredATSConstants.MSG_UNABLE_TO_FIND_REPLACED_DATA
							+ strReplacedPartTNR + PGStructuredATSConstants.MSG_FOR_PART + strTNRInfo + "'.\n");
				}
				
				
			}
		}
	}

	/**
	 * Validate attributes for negative values and Min will always be less than Max
	 * @param mpAttrValueMap
	 * @param strParentInfo
	 * @param strReplacedPartTNR
	 * @param strTNRInfo
	 * @return
	 */
	private boolean validateAttributeValues(Map<String, Object> mpAttrValueMap, String strParentInfo,
			String strReplacedPartTNR, String strTNRInfo) {
		boolean isValidAttrVal = true;
		StringBuilder sbNegativeData = new StringBuilder();
		StringBuilder sbFormatIssueData = new StringBuilder();
		for (Map.Entry<String, String> entry : mpValidateAttrMap.entrySet()) {
			String strColDispName = entry.getKey();
			String strAttrName = entry.getValue();

			if(mpAttrValueMap.containsKey(strAttrName)) {
				String strAttVal = (String) mpAttrValueMap.get(strAttrName);
				if(UIUtil.isNotNullAndNotEmpty(strAttVal)) {
					try {
						Float fRelAttrValue = Float.parseFloat(strAttVal);

						if(fRelAttrValue < 0) {
							sbNegativeData.append(strColDispName).append(",");
							isValidAttrVal = false;
						}
					} catch(NumberFormatException exp) {
						sbFormatIssueData.append(strColDispName).append(",");
						isValidAttrVal = false;
					}
				}
			}
		}

		//Validation 1: Negative values not allowed
		String strNegatvieCols = sbNegativeData.toString();
		if (UIUtil.isNotNullAndNotEmpty(strNegatvieCols)) {
			strNegatvieCols = strNegatvieCols.substring(0, strNegatvieCols.length() - 1);
			sbErrorMessage.append(PGStructuredATSConstants.MSG_NEGATIVE_VALUES_NOT_ALLOWED).append(strNegatvieCols)
					.append(PGStructuredATSConstants.MSG_FOR_ATS_DATA).append(strReplacedPartTNR)
					.append(PGStructuredATSConstants.MSG_FOR_BOM_DATA).append(strParentInfo);
					if(UIUtil.isNotNullAndNotEmpty(strTNRInfo)) {
						sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_PART + strTNRInfo + "'.\n");
					} else {
						sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_BM + ".\n");
					}
		}

		//Validation 2: Non numeric values such as String are not allowed
		String strFormatIssueCols = sbFormatIssueData.toString();
		if (UIUtil.isNotNullAndNotEmpty(strFormatIssueCols)) {
			strFormatIssueCols = strFormatIssueCols.substring(0, strFormatIssueCols.length() - 1);
			sbErrorMessage.append(PGStructuredATSConstants.MSG_NUMERIC_VALUES).append(strFormatIssueCols)
					.append(PGStructuredATSConstants.MSG_FOR_ATS_DATA).append(strReplacedPartTNR)
					.append(PGStructuredATSConstants.MSG_FOR_BOM_DATA).append(strParentInfo);
					if(UIUtil.isNotNullAndNotEmpty(strTNRInfo)) {
						sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_PART + strTNRInfo + "'.\n");
					} else {
						sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_BM + ".\n");
					}
		}
		
		//Validation 3: Less Than
		boolean isInValidAttrVal = validateLessThanValues(PGStructuredATSConstants.ATTRIBUTE_PGMINACTUALPERCENWET,
				PGStructuredATSConstants.ATTRIBUTE_PGMAXACTUALPERCENWET, mpAttrValueMap);
		if(isInValidAttrVal) {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_MIN_LESS_THAN_MAX).append(strReplacedPartTNR)
			.append(PGStructuredATSConstants.MSG_FOR_BOM_DATA).append(strParentInfo);
			
			if(UIUtil.isNotNullAndNotEmpty(strTNRInfo)) {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_PART + strTNRInfo + "'.\n");
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_BM + ".\n");
			}
			
			isValidAttrVal = false;
		}
		
		isInValidAttrVal = validateLessThanValues(PGStructuredATSConstants.ATTRIBUTE_PGMINACTUALPERCENWET,
				DomainConstants.ATTRIBUTE_QUANTITY, mpAttrValueMap);
		if(isInValidAttrVal) {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_MIN_LESS_THAN_QTY).append(strReplacedPartTNR)
			.append(PGStructuredATSConstants.MSG_FOR_BOM_DATA).append(strParentInfo);
			
			if(UIUtil.isNotNullAndNotEmpty(strTNRInfo)) {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_PART + strTNRInfo + "'.\n");
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_BM + ".\n");
			}
			
			isValidAttrVal = false;
		}
		
		isInValidAttrVal = validateLessThanValues(DomainConstants.ATTRIBUTE_QUANTITY,
				PGStructuredATSConstants.ATTRIBUTE_PGMAXACTUALPERCENWET, mpAttrValueMap);
		if(isInValidAttrVal) {
			sbErrorMessage.append(PGStructuredATSConstants.MSG_QTY_LESS_THAN_MAX).append(strReplacedPartTNR)
			.append(PGStructuredATSConstants.MSG_FOR_BOM_DATA).append(strParentInfo);
			
			if(UIUtil.isNotNullAndNotEmpty(strTNRInfo)) {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_PART + strTNRInfo + "'.\n");
			} else {
				sbErrorMessage.append(PGStructuredATSConstants.MSG_FOR_BM + ".\n");
			}
			
			isValidAttrVal = false;
		}
		
		return isValidAttrVal;
	}

	/**
	 * Method to validate <= values for Min, Max, Qy
	 * @param strMinAttrKey
	 * @param strMaxAttrKey
	 * @param mpAttrValueMap
	 * @return
	 */
	private boolean validateLessThanValues(String strMinAttrKey, String strMaxAttrKey, Map<String, Object> mpAttrValueMap) {
		boolean isInValidAttrVal = false;
		if (mpAttrValueMap.containsKey(strMinAttrKey) && mpAttrValueMap.containsKey(strMaxAttrKey)) {
			String strMinVal = (String) mpAttrValueMap.get(strMinAttrKey);
			String strMaxVal = (String) mpAttrValueMap.get(strMaxAttrKey);
			
			if(UIUtil.isNotNullAndNotEmpty(strMinVal) && UIUtil.isNotNullAndNotEmpty(strMaxVal)) {
				try {
					Float fMinVal = Float.parseFloat(strMinVal);
					Float fMaxVal = Float.parseFloat(strMaxVal);
					
					if(fMinVal > fMaxVal) {
						isInValidAttrVal = true;
					}
				} catch(NumberFormatException exp) {
					//Ignore this validation for non-numeric data
				}
			}
		}
		
		return isInValidAttrVal;
	}

	/**
	 * Method to get attribute name value Map
	 * @param iColNo
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @return
	 */
	private Map<String, Object> getAttributeNameValueMap(Integer iColNo, Map<Integer, String> mpAttrValIndexMap,
			Map<Integer, String> mpRelAttrIndexMap) {
		Map<String,Object> mpAttrValueMap = new HashMap<>();
		for(int i=0;i<iNoOfHeaderCol;i++) {
			String strColName = mpRelAttrIndexMap.get(iColNo);
			if(mpAttributeColNameMap.containsKey(strColName)) {
				String strColAttrName = mpAttributeColNameMap.get(strColName);
				if(mpAttrValIndexMap.containsKey(iColNo)) {
					String strAttrVal = mpAttrValIndexMap.get(iColNo);				
					mpAttrValueMap.put(strColAttrName, strAttrVal);
				} else {
					mpAttrValueMap.put(strColAttrName, "");
				}
			} 
						
			iColNo = iColNo + 1;
		}

		return mpAttrValueMap;
	}

	/**
	 * Method to get the type display name
	 * @param context
	 * @param strType
	 * @return
	 * @throws MatrixException 
	 */
	private String getTypeDisplayName(Context context, String strType) throws MatrixException {
		String strLanguage = context.getLocale().getLanguage();
		String strDisplayType = EnoviaResourceBundle.getTypeI18NString(context, strType, strLanguage);
		if(UIUtil.isNullOrEmpty(strDisplayType)){
			strDisplayType = strType;
		}
		return strDisplayType;
	}

	/**
	 * Method to get Map for TNR to obj of ATS OP and rel id
	 * @param context
	 * @param slATSOpIdList
	 * @return
	 * @throws MatrixException 
	 */
	private Map<String, String> getATSOprReplacedDataMap(Context context, StringList slATSOpIdList) throws MatrixException {
		int iListSize = slATSOpIdList.size();
		StringList slBusSelects = new StringList();
		slBusSelects.add(PGStructuredATSConstants.TO_SIDE+DomainConstants.SELECT_TYPE);
		slBusSelects.add(PGStructuredATSConstants.TO_SIDE+DomainConstants.SELECT_NAME);
		slBusSelects.add(PGStructuredATSConstants.TO_SIDE+DomainConstants.SELECT_REVISION);
		
		Map<String,String> mpATSIdToObjMap = new HashMap<>();
		for(int i=0;i<iListSize;i++) {
			String strATSOperationId = slATSOpIdList.get(i);
			MapList mlATSInfoList = DomainRelationship.getInfo(context, new String[] {strATSOperationId}, slBusSelects);
			Map<?,?> mpATSInfoMap = (Map<?, ?>) mlATSInfoList.get(0);
			String strType = (String) mpATSInfoMap.get(PGStructuredATSConstants.TO_SIDE+DomainConstants.SELECT_TYPE);
			String strName = (String) mpATSInfoMap.get(PGStructuredATSConstants.TO_SIDE+DomainConstants.SELECT_NAME);
			String strRevision = (String) mpATSInfoMap.get(PGStructuredATSConstants.TO_SIDE+DomainConstants.SELECT_REVISION);
			
			String strTypeDispName = getTypeDisplayName(context,strType);
			
			StringBuilder sbTNR = new StringBuilder();
			sbTNR.append(strTypeDispName).append(PGStructuredATSConstants.TNR_SEP);
			sbTNR.append(strName).append(PGStructuredATSConstants.TNR_SEP);
			sbTNR.append(strRevision);
			
			mpATSIdToObjMap.put(sbTNR.toString(), strATSOperationId);
			
		}
		
		return mpATSIdToObjMap;
	}
	
	/**
	 * Method to get object id from T-N-R string
	 * @param context
	 * @param strTNRInfo
	 * @return
	 * @throws FrameworkException 
	 */
	private String getObjectIdFromTNR(Context context, String strTNRInfo) throws FrameworkException {
		String strPartId = "";
		StringList slTNRInfoList = StringUtil.split(strTNRInfo, PGStructuredATSConstants.TNR_SEP);
		int iListSize = slTNRInfoList.size();
		if(iListSize > 2) {
			String strType = slTNRInfoList.get(0).trim();
			if(mpRMPChildTypeActualName.containsKey(strType)) {
				strType = mpRMPChildTypeActualName.get(strType);
			}
			String strName = slTNRInfoList.get(1).trim();
			String strRev = slTNRInfoList.get(2).trim();
			
			MapList mlPartIdInfoList = DomainObject.findObjects(context, // context
					strType, // Type Pattern
					strName, // Name pattern
					strRev, // Rev pattern
					DomainConstants.QUERY_WILDCARD, // owner pattern
					DomainConstants.QUERY_WILDCARD, // vault pattern
					null, // where expression
					false, // expand type
					new StringList(DomainConstants.SELECT_ID)); // bus selects
			
				if(null != mlPartIdInfoList && !mlPartIdInfoList.isEmpty() ) {
					Map<?,?> mpPartIdMap = (Map<?, ?>) mlPartIdInfoList.get(0);
					strPartId = (String) mpPartIdMap.get(DomainConstants.SELECT_ID);
				}
		}
		return strPartId;
	}

	/**
	 * Method to get the attribute values and respective column index list
	 * @param workbook
	 * @return
	 */
	private MapList getAttributeValueIndexList(Workbook workbook) {
		MapList mlRelAttrValueList = new MapList();
		
		for (int iSheetCount = 0; iSheetCount < NO_OF_SHEETS; iSheetCount++) {
			Sheet sheet = workbook.getSheetAt(iSheetCount);
			Iterator<Row> rows = sheet.rowIterator();

			//Skip first two rows
			if(rows.hasNext()) {
				rows.next();
			}
			if(rows.hasNext()) {
				rows.next();
			}
			
			// Iterating Excel Rows from 3rd row onward
			while (rows.hasNext()) {
				Row row = rows.next();
				Iterator<Cell> cells = row.cellIterator();
				
				Map<Integer,String> mpAttrValIndexMap = new HashMap<>();

				while (cells.hasNext()) {

					Cell cell = cells.next();
					if(cell != null) {
						CellType cellType = cell.getCellType();
						int iColumnIndex = cell.getColumnIndex();

						if (cellType == CellType.STRING) {
							String strCell = cell.getStringCellValue();
							mpAttrValIndexMap.put(iColumnIndex, strCell.trim());
						} else if(cellType == CellType.NUMERIC || cellType == CellType.FORMULA) {
							String strNumValue = Double.toString(cell.getNumericCellValue());
							mpAttrValIndexMap.put(iColumnIndex, strNumValue.trim());
						}
					}

				}
				mlRelAttrValueList.add(mpAttrValIndexMap);
			}

		}
		
		return mlRelAttrValueList;
	}

	/**
	 * Method to get the indexes for Min, Max and Quantity columns
	 * @param workbook
	 * @return
	 */
	private Map<Integer, String> getIndicesForAttibutes(Workbook workbook) {
		Map<Integer, String> mpRelAttrIndexMap = new HashMap<>();
		
		for (int iSheetCount = 0; iSheetCount < NO_OF_SHEETS; iSheetCount++) {
			Sheet sheet = workbook.getSheetAt(iSheetCount);
			Row row = sheet.getRow(1); //Attribute info row
			Iterator<Cell> cells = row.cellIterator();

			//Skip first two cells : GCAS No and Title
			if(cells.hasNext()) {
				cells.next();
			}
			if(cells.hasNext()) {
				cells.next();
			}
				
			while (cells.hasNext()) {
				Cell cell = cells.next();
				if(cell != null) {
					CellType cellType = cell.getCellType();

					if (cellType == CellType.STRING) {
						int iColumnIndex = cell.getColumnIndex();
						String strCell = cell.getStringCellValue();
						strCell = strCell.trim();
						setHeaderInfo.add(strCell);
						mpRelAttrIndexMap.put(iColumnIndex, strCell);
					}
				}
			}

		}
		
		return mpRelAttrIndexMap;
	}

	/**
	 * Method to get replaced data List
	 * 
	 * @param workbook
	 * @return
	 */
	private Map<String, Map<Integer, String>> getReplacedDataInfo(Workbook workbook) {

		Map<String, Map<Integer, String>> mpReplacedInfoMap = new HashMap<>();

		for (int iSheetCount = 0; iSheetCount < NO_OF_SHEETS; iSheetCount++) {
			Sheet sheet = workbook.getSheetAt(iSheetCount);
			String strPrevCellInfo = "";

			Row row = sheet.getRow(0); //Header row
			Iterator<Cell> cells = row.cellIterator();

			while (cells.hasNext()) {
				Cell cell = cells.next();
				if (cell != null) {
					CellType cellType = cell.getCellType();

					if (cellType == CellType.STRING) {
						String strCell = cell.getStringCellValue();
						String[] strCellInfoList = strCell.split(PGStructuredATSConstants.SEP_NEW_LINE, 0);
						int iCellInfoLen = strCellInfoList.length;
						if (iCellInfoLen > 2) {
							String strDataInfo = strCellInfoList[0].trim();
							String strTNRInfo = strCellInfoList[2].trim();
							int iColumnIndex = cell.getColumnIndex();

							if (UIUtil.isNotNullAndNotEmpty(strPrevCellInfo)
									&& PGStructuredATSConstants.VALUE_ATS.equals(strDataInfo)
									&& !PGStructuredATSConstants.STR_BALANCING_MATERIAL.equals(strDataInfo)) {
								if (mpReplacedInfoMap.containsKey(strPrevCellInfo)) {
									Map<Integer, String> mpATSDataMap = mpReplacedInfoMap.get(strPrevCellInfo);
									mpATSDataMap.put(iColumnIndex, strTNRInfo);
									mpReplacedInfoMap.put(strPrevCellInfo, mpATSDataMap);
								} else {
									Map<Integer, String> mpATSDataMap = new HashMap<>();
									mpATSDataMap.put(iColumnIndex, strTNRInfo);
									mpReplacedInfoMap.put(strPrevCellInfo, mpATSDataMap);
								}
							} else {
								StringBuilder sbBOMItem = new StringBuilder();
								sbBOMItem.append(strTNRInfo).append(PGStructuredATSConstants.COMMA_SEP);
								sbBOMItem.append(Integer.toString(iColumnIndex))
										.append(PGStructuredATSConstants.COMMA_SEP);
								if (PGStructuredATSConstants.VALUE_CURRENT_SUB.equals(strDataInfo)) {
									sbBOMItem.append(strDataInfo).append(PGStructuredATSConstants.COMMA_SEP);
									if (iCellInfoLen > 3) {
										String strPrimaryTNRInfo = strCellInfoList[3].trim();
										strPrimaryTNRInfo = strPrimaryTNRInfo
												.replace(PGStructuredATSConstants.KEY_PRIMARY + ":", "");
										sbBOMItem.append(strPrimaryTNRInfo);
									}
								} else {
									sbBOMItem.append(strDataInfo);
								}
								strPrevCellInfo = sbBOMItem.toString();
							}

							if (PGStructuredATSConstants.STR_BALANCING_MATERIAL.equals(strDataInfo)) {
								StringBuilder sbBMData = new StringBuilder();
								sbBMData.append(PGStructuredATSConstants.STR_BALANCING_MATERIAL).append("|");
								sbBMData.append(Integer.toString(iColumnIndex));
								
								Map<Integer, String> mpBMDataMap = new HashMap<>();
								mpBMDataMap.put(iColumnIndex, strTNRInfo);
								mpReplacedInfoMap.put(sbBMData.toString(), mpBMDataMap);
							}
						}
					}
				}
			}

		}

		return mpReplacedInfoMap;
	}

}
