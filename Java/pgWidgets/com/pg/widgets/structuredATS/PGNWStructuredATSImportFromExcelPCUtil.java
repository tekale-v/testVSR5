package com.pg.widgets.structuredats;

import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

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
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
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


public class PGNWStructuredATSImportFromExcelPCUtil {
	static final Logger logger = Logger.getLogger(PGNWStructuredATSImportFromExcelPCUtil.class.getName());
	static final int NO_OF_SHEETS = 1;
	Map<String,Map<String,String>> mpAttributeColNameMap = new HashMap<>();
	StringBuilder sbErrorMessage = new StringBuilder();
	PGStructuredATSPerformanceUtil objPGStructuredATSPerformanceUtil = new PGStructuredATSPerformanceUtil();
	Set<String> setHeaderInfo = new LinkedHashSet<>();
	int iNoOfHeaderCol = 1;
	
	/**
	 * Method to import PC data for ATS side object
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws FrameworkException
	 * @throws IOException 
	 */
	String importPerfCharData(Context context, String strJsonInput) throws FrameworkException, IOException {
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

			initializeGlobalData();
			
			parseExcelAndSetAttributeValues(context,fileUploadExcel,strATSObjId);

			String strErrorMsg = sbErrorMessage.toString();
			if(UIUtil.isNullOrEmpty(strErrorMsg)) {
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			} else {
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, PGStructuredATSConstants.MESSAGE_PREFIX_IMPORT+strErrorMsg);
			}
			
			JsonObject jsonBOMData = objPGStructuredATSPerformanceUtil.fetchPerformanceCharactersticsJson(context,strJsonInput);
			jsonReturnObj.add(PGStructuredATSConstants.KEY_OUTPUT, jsonBOMData);

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_IMPORT_FROM_PCEXCEL, e);
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
	 * Get common rel selects
	 * @return
	 */
	private void initializeGlobalData() {
		mpAttributeColNameMap = new HashMap<>();
		sbErrorMessage = new StringBuilder();
		setHeaderInfo = new LinkedHashSet<>();
		iNoOfHeaderCol = 1;

		Map<String,String> mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_TM_LOGIC);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_TM_LOGIC, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_METHOD_ORIGIN);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_TM_ORIGIN, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_METHOD_NUM);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_TM_NUM, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_TM_SPECIFCS);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_TM_SPECIFICS, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_SAMPLING);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_SAMPLING, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_SUB_GRP);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_SUB_GRP, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_PLANT_TESTING);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_PLANT_TESTING_LEVEL, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_PLANT_RETESTING);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_PLANT_RETESTING, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_RETESTING_UOM);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_RETESTING_UOM, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_LSL);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_LSL, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_LRRL);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_LRRL, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_LT);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_LT, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_TARGET);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_TARGET, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_UT);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_UT, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_URRL);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_URRL, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_USL);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_USL, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_REPORT_NEAREST);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_REPORT_NEAREST, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_REPORT_TYPE);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_REPORT_TYPE, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_REL_CRITERIA);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_RELEASE_CRITERIA, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_ACTION_REQ);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_ACTION_RQD, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_CRITICALITY_FACTOR);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_CRITICALITY_FACT, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_BASIS);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_BASIS, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_TEST_GRP);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_TEST_GRP, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_APPLICATION);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_APPLICATION, mpAttributeInfo);
		
		mpAttributeInfo = new HashMap<>(); 
		mpAttributeInfo.put(DomainConstants.SELECT_NAME, PGStructuredATSConstants.ATTRIBUTE_UOM);
		mpAttributeInfo.put(DomainConstants.SELECT_TYPE, PGStructuredATSConstants.VALUE_ATTR_STRING);
		mpAttributeColNameMap.put(PGStructuredATSConstants.DISP_UOM, mpAttributeInfo);
	}


	/**
	 * Method to parse the excel into different Maps and set the attribute values to
	 * database
	 * 
	 * @param context
	 * @param fUploadedFile
	 * @param strATSObjId
	 * @return
	 * @throws Exception 
	 */
	private void parseExcelAndSetAttributeValues(Context context, File fUploadedFile, String strATSObjId)
			throws Exception {
		try (FileInputStream fileInStream = new FileInputStream(fUploadedFile);
				Workbook workbook = WorkbookFactory.create(fileInStream)) {
			Map<String, Map<Integer, String>> mpReplacedInfoMap = updateModifiedAddedDataInfo(workbook);
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
	 * @param mpAddATSMap
	 * @throws Exception 
	 */
	private void setAttributeValues(Context context, String strATSObjId,
			Map<String, Map<Integer, String>> mpReplacedInfoMap, Map<Integer, String> mpRelAttrIndexMap,
			MapList mlRelAttrValueList) throws Exception {
		if(mlRelAttrValueList != null && !mlRelAttrValueList.isEmpty()) {
			for (Map.Entry<?, ?> entry : mpReplacedInfoMap.entrySet()) {
				String strCurrentOrAddInfo = (String) entry.getKey();
				Map<Integer, String> mpATSDataMap  =  (Map<Integer, String>) entry.getValue();
				
				setAttrValForReplacedItem(context,strATSObjId,mlRelAttrValueList,strCurrentOrAddInfo,mpATSDataMap,mpRelAttrIndexMap);
				
			}
		}
	}

	/**
	 * Method to set the values of the attributes from excel data for current
	 * replaced item
	 * 
	 * @param context
	 * @param strATSObjId
	 * @param mlRelAttrValueList
	 * @param strCurrentOrAddInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws Exception
	 */
	private void setAttrValForReplacedItem(Context context, String strATSObjId, MapList mlRelAttrValueList,
			String strCurrentOrAddInfo, Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap)
			throws Exception {
		int iDataListSize = mlRelAttrValueList.size();
		for (int i = 0; i < iDataListSize; i++) {
			Map<Integer, String> mpAttrValIndexMap = (Map<Integer, String>) mlRelAttrValueList.get(i);
			String strHiddenPCId = getHiddenPCId(mpATSDataMap, mpAttrValIndexMap, mpRelAttrIndexMap);
			boolean bSetAttrValues = false;
			Integer iColNo = 0;
			for (Map.Entry<?, ?> entryATS : mpATSDataMap.entrySet()) {
				iColNo = (Integer) entryATS.getKey();
				for (int j = 0; j < iNoOfHeaderCol; j++) {
					if (mpAttrValIndexMap.containsKey(iColNo + j)) {
						String strColHeader = mpRelAttrIndexMap.get(iColNo + j);
						if (!PGStructuredATSConstants.COL_HIDDEN_PCID.equals(strColHeader)) {
							String strColValue = mpAttrValIndexMap.get(iColNo + j);
							if(UIUtil.isNotNullAndNotEmpty(strColValue)) {
								bSetAttrValues = true;
								break;
							}
						}
					}
				}
			}

			if (bSetAttrValues && UIUtil.isNotNullAndNotEmpty(strHiddenPCId)) {
				setAttributeValuesForCurrentRow(context, strATSObjId, mpAttrValIndexMap, strCurrentOrAddInfo,
						mpATSDataMap, mpRelAttrIndexMap, strHiddenPCId);

			} else if (bSetAttrValues && UIUtil.isNullOrEmpty(strHiddenPCId)) {
				String strParentInfo = mpAttrValIndexMap.get(0).trim();
				sbErrorMessage.append(PGStructuredATSConstants.MSG_PREFIX_FORMULA);
				sbErrorMessage.append(strParentInfo).append(PGStructuredATSConstants.MSG_NO_MOD_ADD_DATA_EXIST);
				String strCharAndCharSpecInfo = mpATSDataMap.get(iColNo);
				sbErrorMessage.append(strCharAndCharSpecInfo);
				sbErrorMessage.append("\n");

			} else if (!bSetAttrValues && UIUtil.isNotNullAndNotEmpty(strHiddenPCId)) {
				// Delete PC for empty data
				String[] strPCIdArray = new String[1];
				strPCIdArray[0] = strHiddenPCId;
				try {
					DomainObject.deleteObjects(context, strPCIdArray);
				} catch(Exception exp) {
					//Ignore if object is already deleted or user don't have delete access
				}
			}

		}

	}

	/**
	 * Method to get Hidden PC Id
	 * @param mpATSDataMap
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @return
	 */
	private String getHiddenPCId(Map<Integer, String> mpATSDataMap, Map<Integer, String> mpAttrValIndexMap,
			Map<Integer, String> mpRelAttrIndexMap) {
		String strPCId = "";
		for (Map.Entry<?, ?> entryATS : mpATSDataMap.entrySet()) {
			Integer iColNo = (Integer) entryATS.getKey();
			for(int i=0;i<iNoOfHeaderCol;i++) {
				int iIndex = iColNo+i;
				if(mpAttrValIndexMap.containsKey(iIndex)) {
					String strColHeader = mpRelAttrIndexMap.get(iIndex);
					if(PGStructuredATSConstants.COL_HIDDEN_PCID.equals(strColHeader)) {
						strPCId = mpAttrValIndexMap.get(iIndex).trim();
					}
				}
			}
		}
		
		return strPCId;
	}

	/**
	 * Method to set the values of the attributes from excel data for current replaced item
	 * @param context
	 * @param strATSObjId
	 * @param mpAttrValIndexMap
	 * @param strCurrentOrAddInfo
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @throws FrameworkException 
	 */
	private void setAttributeValuesForCurrentRow(Context context, String strATSObjId,
			Map<Integer, String> mpAttrValIndexMap, String strCurrentOrAddInfo, Map<Integer, String> mpATSDataMap,
			Map<Integer, String> mpRelAttrIndexMap, String strHiddenPCId) throws FrameworkException {
		String strParentInfo = mpAttrValIndexMap.get(0).trim();
		if(FrameworkUtil.isObjectId(context, strHiddenPCId)) {
			String strError = validatePCData(strParentInfo, mpAttrValIndexMap, mpATSDataMap, mpRelAttrIndexMap, strCurrentOrAddInfo);
			if(UIUtil.isNullOrEmpty(strError)) {
				modifyExistingATSPCData(context,strHiddenPCId,mpAttrValIndexMap,mpRelAttrIndexMap,mpATSDataMap);
			} else {
				sbErrorMessage.append("\n").append(strError);
			}
		}	
	}

	/**
	 * Method to modify existing ATS side PC object
	 * @param context
	 * @param strHiddenPCId
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @param mpATSDataMap
	 * @throws FrameworkException 
	 */
	private void modifyExistingATSPCData(Context context, String strHiddenPCId, Map<Integer, String> mpAttrValIndexMap,
			Map<Integer, String> mpRelAttrIndexMap, Map<Integer, String> mpATSDataMap) throws FrameworkException {
		for (Map.Entry<Integer, String> entry : mpATSDataMap.entrySet()) {
			Integer iColNo = entry.getKey();
			Map<String,Map<String,Object>> mpColValuesMap = getAttributeNameValueMap(iColNo, mpAttrValIndexMap, mpRelAttrIndexMap);
			Map<String,Object> mpAttrValueMap = mpColValuesMap.get(PGStructuredATSConstants.KEY_ATTR_COL);
			Map<String,Object> mpConnectionColMap = mpColValuesMap.get(PGStructuredATSConstants.KEY_REL_COL);
		
			DomainObject dobPCObj = DomainObject.newInstance(context, strHiddenPCId);
			dobPCObj.setAttributeValues(context, mpAttrValueMap);
			updateConnectionInfo(context, dobPCObj, mpConnectionColMap);
		}
	}

	/**
	 * Update 'Test Method Name' and 'Test Method Reference Document Name'
	 * @param context
	 * @param dobPCObj
	 * @param mpConnectionColMap
	 * @throws FrameworkException 
	 */
	private void updateConnectionInfo(Context context, DomainObject dobPCObj, Map<String, Object> mpConnectionColMap) throws FrameworkException {
		MapList mlConnectTMList = getExistingTMConnectionsInfo(context, dobPCObj); 
		StringList slAlreadyConnectedTMs = getExistingTMConnections(context, mlConnectTMList);
		if(mpConnectionColMap.containsKey(PGStructuredATSConstants.DISP_TM_NAME)) {
			String strTypes = PGStructuredATSConstants.TYPE_PG_TEST_METHOD + "," + PGStructuredATSConstants.TYPE_PG_TEST_METHOD_SPEC;
			String strTMObjNames = (String) mpConnectionColMap.get(PGStructuredATSConstants.DISP_TM_NAME);
			if(UIUtil.isNotNullAndNotEmpty(strTMObjNames)) {
				StringList slTMNamesList = StringUtil.split(strTMObjNames, "|");
				StringList slLatestTMIdList = updateTMConnections(context, slTMNamesList, slAlreadyConnectedTMs, strTypes, dobPCObj);
				deleteOldConnections(context, strTypes, mlConnectTMList, slLatestTMIdList);
			} else {
				deleteExistingConnections(context, strTypes, mlConnectTMList);
			}
			
		}
		
		if(mpConnectionColMap.containsKey(PGStructuredATSConstants.DISP_TM_REF_DOC)) {
			String strTMRefDocObjNames = (String) mpConnectionColMap.get(PGStructuredATSConstants.DISP_TM_REF_DOC);
			if(UIUtil.isNotNullAndNotEmpty(strTMRefDocObjNames)) {
				StringList slTMRefDocNamesList = StringUtil.split(strTMRefDocObjNames, "|");
				StringList slLatestTMIdList = updateTMConnections(context, slTMRefDocNamesList, slAlreadyConnectedTMs, PGStructuredATSConstants.TYPE_PG_TMRD_TYPES, dobPCObj);
				deleteOldConnections(context, PGStructuredATSConstants.TYPE_PG_TMRD_TYPES, mlConnectTMList, slLatestTMIdList);
			} else {
				deleteExistingConnections(context, PGStructuredATSConstants.TYPE_PG_TMRD_TYPES, mlConnectTMList);
			}
			
		}
	}

	/**
	 * Method to delete old connections after updating new ones
	 * @param context
	 * @param strTypes
	 * @param mlConnectTMList
	 * @param slLatestTMIdList
	 * @throws FrameworkException 
	 */
	private void deleteOldConnections(Context context, String strTypes, MapList mlConnectTMList,
			StringList slLatestTMIdList) throws FrameworkException {
		if (mlConnectTMList != null) {
			StringList slTypeList = StringUtil.split(strTypes, ",");
			int iListSize = mlConnectTMList.size();
			for (int i = 0; i < iListSize; i++) {
				Map<?, ?> mpTMInfoMap = (Map<?, ?>) mlConnectTMList.get(i);
				String strTypeName = (String) mpTMInfoMap.get(DomainConstants.SELECT_TYPE);
				String strObjId = (String) mpTMInfoMap.get(DomainConstants.SELECT_ID);
				if (slTypeList.contains(strTypeName) && !slLatestTMIdList.contains(strObjId)) {
					String strRelId = (String) mpTMInfoMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					DomainRelationship.disconnect(context, strRelId);
				}
			}
		}
		
	}

	/**
	 * Method to delete existing connections
	 * @param context
	 * @param strTypes
	 * @param mlConnectTMList
	 * @throws FrameworkException 
	 */
	private void deleteExistingConnections(Context context, String strTypes, MapList mlConnectTMList) throws FrameworkException {
		if (mlConnectTMList != null) {
			StringList slTypeList = StringUtil.split(strTypes, ",");
			int iListSize = mlConnectTMList.size();
			for (int i = 0; i < iListSize; i++) {
				Map<?, ?> mpTMInfoMap = (Map<?, ?>) mlConnectTMList.get(i);
				String strTypeName = (String) mpTMInfoMap.get(DomainConstants.SELECT_TYPE);
				if (slTypeList.contains(strTypeName)) {
					String strRelId = (String) mpTMInfoMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					DomainRelationship.disconnect(context, strRelId);
				}
			}
		}
	}

	/**
	 * Method to update connections
	 * 
	 * @param context
	 * @param slTMNamesList
	 * @param slAlreadyConnectedTMs
	 * @param strTypes
	 * @param dobPCObj
	 * @return
	 * @throws FrameworkException
	 */
	private StringList updateTMConnections(Context context, StringList slTMNamesList, StringList slAlreadyConnectedTMs,
			String strTypes, DomainObject dobPCObj) throws FrameworkException {
		StringList slLatestTMIdList = getLatestTestMethodIds(context, slTMNamesList, strTypes);

		int iListSize = slLatestTMIdList.size();
		for (int i = 0; i < iListSize; i++) {
			String strTMId = slLatestTMIdList.get(i);
			if (!slAlreadyConnectedTMs.contains(strTMId)) {
				DomainObject dobTMObj = DomainObject.newInstance(context, strTMId);
				DomainRelationship.connect(context, dobTMObj,
						DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, dobPCObj);
			}
		}
		
		return slLatestTMIdList;
	}

	/**
	 * 
	 * Method copied from
	 * 'com.pg.widgets.nexusPerformanceChars.PGFetchNexusTMSpecificsPerfChars:getLatestTestMethodIds'
	 * Since type pattern is hard coded in above original method
	 * 
	 * Below mathod gets the latest Test Method Ids by using TM name . We can input
	 * the TM names in a stringlist for which we want to get the latest version of
	 * TMs . to the part.In case of Transport Unit Part only one one SPS spec can be
	 * added.
	 * 
	 * @param context
	 * @param slTMNameList
	 * @param strTypePattern
	 * @return Latest Test Method or Ref Doc Ids
	 * @throws FrameworkException
	 */
	public StringList getLatestTestMethodIds(Context context, StringList slTMNameList, String strTypePattern)
			throws FrameworkException {

		StringList slTMIds = new StringList();
		boolean isContextPushed = false;
		try {
			// DSM (DS) 2022x : Push context required to get TestMethod info as context user
			// doesn't have access to perform this action
			ContextUtil.pushContext(context, PGStructuredATSConstants.PERSON_AGENT, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING);
			isContextPushed = true;
			
			String strObjWhere = PGStructuredATSConstants.TM_WHERE_CLAUSE;
			MapList objectsList = new MapList();
			String strTMID = DomainConstants.EMPTY_STRING;
			String strTMCurrentState = DomainConstants.EMPTY_STRING;
			String strReleasedTM = DomainConstants.EMPTY_STRING;
			String strNonReleasedTM = DomainConstants.EMPTY_STRING;
			Map tMobject = null;

			StringList slObjSelect = new StringList();
			slObjSelect.add(DomainConstants.SELECT_LAST_ID);
			slObjSelect.add(DomainConstants.SELECT_ID);
			slObjSelect.add(DomainConstants.SELECT_CURRENT);

			for (int i = 0; i < slTMNameList.size(); i++) {
				strReleasedTM = DomainConstants.EMPTY_STRING;
				strNonReleasedTM = DomainConstants.EMPTY_STRING;
				objectsList = DomainObject.findObjects(context, strTypePattern, slTMNameList.get(i), "*", "*", null,
						strObjWhere, true, slObjSelect);

				if (objectsList != null && !objectsList.isEmpty()) {
					for (Iterator iterator = objectsList.iterator(); iterator.hasNext();) {
						tMobject = (Map) iterator.next();
						strTMID = (String) tMobject.get(DomainConstants.SELECT_ID);
						strTMCurrentState = (String) tMobject.get(DomainConstants.SELECT_CURRENT);

						if (UIUtil.isNotNullAndNotEmpty(strTMID)) {

							if (!PGStructuredATSConstants.STATE_OBSOLETE.equals(strTMCurrentState)
									&& (PGStructuredATSConstants.STATE_RELEASE.equals(strTMCurrentState)
											|| PGStructuredATSConstants.STATE_COMPLETE.equals(strTMCurrentState))) {
								strReleasedTM = strTMID;

							} else {
								strNonReleasedTM = strTMID;

							}
						}

					}
				}

				if ((UIUtil.isNotNullAndNotEmpty(strReleasedTM) && UIUtil.isNotNullAndNotEmpty(strNonReleasedTM))
						|| (UIUtil.isNotNullAndNotEmpty(strReleasedTM) && UIUtil.isNullOrEmpty(strNonReleasedTM))) {

					slTMIds.add(strReleasedTM);

				} else if (UIUtil.isNotNullAndNotEmpty(strNonReleasedTM)) {

					slTMIds.add(strNonReleasedTM);

				}
			}
		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return slTMIds;
	}

	/**
	 * Method to get already connected TM objects Ids
	 * 
	 * @param context
	 * @param dobPCObj
	 * @return
	 * @throws FrameworkException
	 */
	private StringList getExistingTMConnections(Context context, MapList mlConnectTMList) {
		StringList slAlreadyConnectedTMs = new StringList();
		if (mlConnectTMList != null) {
			int iListSize = mlConnectTMList.size();
			for (int i = 0; i < iListSize; i++) {
				Map<?, ?> mpTMInfoMap = (Map<?, ?>) mlConnectTMList.get(i);
				String strTMId = (String) mpTMInfoMap.get(DomainConstants.SELECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(strTMId)) {
					slAlreadyConnectedTMs.add(strTMId);
				}
			}
		}
		return slAlreadyConnectedTMs;
	}
	
	/**
	 * Method to get already connected TM objects details
	 * 
	 * @param context
	 * @param dobPCObj
	 * @return
	 * @throws FrameworkException
	 */
	private MapList getExistingTMConnectionsInfo(Context context, DomainObject dobPCObj) throws FrameworkException {
		MapList mlConnectTMList = null;
		StringBuilder sbTypePattern = new StringBuilder();
		sbTypePattern.append(PGStructuredATSConstants.TYPE_PG_TEST_METHOD).append(",");
		sbTypePattern.append(PGStructuredATSConstants.TYPE_PG_TEST_METHOD_SPEC).append(",");
		sbTypePattern.append(PGStructuredATSConstants.TYPE_PG_TMRD_TYPES);

		boolean isContextPushed = false;
		try {
			// DSM (DS) 2022x : Push context required to get TestMethod info as context user
			// doesn't have access to perform this action
			ContextUtil.pushContext(context, PGStructuredATSConstants.PERSON_AGENT, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING);
			isContextPushed = true;

			StringList slObjSelects = new StringList();
			slObjSelects.add(DomainConstants.SELECT_ID);
			slObjSelects.add(DomainConstants.SELECT_TYPE);
			
			StringList slRelSelects = new StringList();
			slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			
			mlConnectTMList = dobPCObj.getRelatedObjects(context,
					DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // Rel pattern
					sbTypePattern.toString(), // Type pattern
					slObjSelects, slRelSelects, true, false, (short) 1, null, null, 0);

		} finally {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
		}

		return mlConnectTMList;
	}
	
	/**
	 * Method to get attribute name value Map
	 * @param iColNo
	 * @param mpAttrValIndexMap
	 * @param mpRelAttrIndexMap
	 * @return
	 */
	private Map<String,Map<String,Object>> getAttributeNameValueMap(Integer iColNo, Map<Integer, String> mpAttrValIndexMap,
			Map<Integer, String> mpRelAttrIndexMap) {
		Map<String,Map<String,Object>> mpColValuesMap = new HashMap<>();
		Map<String,Object> mpAttrValueMap = new HashMap<>();
		Map<String,Object> mpConnectionColMap = new HashMap<>();
		for(int i=0;i<iNoOfHeaderCol;i++) {
			String strColName = mpRelAttrIndexMap.get(iColNo);
			if(mpAttrValIndexMap.containsKey(iColNo)) {
				String strColValue = mpAttrValIndexMap.get(iColNo);
				if(mpAttributeColNameMap.containsKey(strColName)) {
					Map<String,String> mpAttributeInfo = mpAttributeColNameMap.get(strColName);
					String strColAttrName = mpAttributeInfo.get(DomainConstants.SELECT_NAME);
					mpAttrValueMap.put(strColAttrName, strColValue);
				} 
				
				if(PGStructuredATSConstants.DISP_TM_NAME.equals(strColName)) {
					mpConnectionColMap.put(PGStructuredATSConstants.DISP_TM_NAME, strColValue);
				}
				
				if(PGStructuredATSConstants.DISP_TM_REF_DOC.equals(strColName)) {
					mpConnectionColMap.put(PGStructuredATSConstants.DISP_TM_REF_DOC, strColValue);
				}
			} else {
				if(mpAttributeColNameMap.containsKey(strColName)) {
					Map<String,String> mpAttributeInfo = mpAttributeColNameMap.get(strColName);
					String strColAttrName = mpAttributeInfo.get(DomainConstants.SELECT_NAME);
					mpAttrValueMap.put(strColAttrName, "");
				} 
				
				if(PGStructuredATSConstants.DISP_TM_NAME.equals(strColName)) {
					mpConnectionColMap.put(PGStructuredATSConstants.DISP_TM_NAME, "");
				}
				
				if(PGStructuredATSConstants.DISP_TM_REF_DOC.equals(strColName)) {
					mpConnectionColMap.put(PGStructuredATSConstants.DISP_TM_REF_DOC, "");
				}
			}
			
			iColNo = iColNo + 1;
		}

		mpColValuesMap.put(PGStructuredATSConstants.KEY_ATTR_COL, mpAttrValueMap);
		mpColValuesMap.put(PGStructuredATSConstants.KEY_REL_COL, mpConnectionColMap);
		
		return mpColValuesMap;
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
	 * Method to get info of Modify and Add data
	 * @param workbook
	 * @return
	 */
	private Map<String, Map<Integer, String>> updateModifiedAddedDataInfo(Workbook workbook) {
		Map<String, Map<Integer, String>> mpReplacedInfoMap = new HashMap<>();
		for (int iSheetCount = 0; iSheetCount < NO_OF_SHEETS; iSheetCount++) {
			Sheet sheet = workbook.getSheetAt(iSheetCount);
			String strPrevCellInfo = "";

			Row row = sheet.getRow(0); // Header row
			Iterator<Cell> cells = row.cellIterator();

			while (cells.hasNext()) {
				Cell cell = cells.next();
				if (cell != null) {
					CellType cellType = cell.getCellType();

					if (cellType == CellType.STRING) {
						String strCell = cell.getStringCellValue();
						String[] strCellInfoList = strCell.split(PGStructuredATSConstants.SEP_NEW_LINE, 0);
						int iCellInfoLen = strCellInfoList.length;
						if (iCellInfoLen > 3) {
							String strDataInfo = strCellInfoList[0].trim();
							String strCharInfo = strCellInfoList[1].trim(); // Split by : if needed
							String strCharSpecInfo = strCellInfoList[2].trim();
							String strTMSInfo = strCellInfoList[3].trim();
							
							StringBuilder sbUniqueAttrsInfo = new StringBuilder();
							sbUniqueAttrsInfo.append(strCharInfo).append(PGStructuredATSConstants.MSG_SEP);
							sbUniqueAttrsInfo.append(strCharSpecInfo).append(PGStructuredATSConstants.MSG_SEP);
							sbUniqueAttrsInfo.append(strTMSInfo);
							
							strCharInfo = sbUniqueAttrsInfo.toString();
							int iColumnIndex = cell.getColumnIndex();

							if (strDataInfo.contains(PGStructuredATSConstants.KEY_ADD_ATS_PC)) {
								StringBuilder sbAddKey = new StringBuilder();
								sbAddKey.append(Integer.toString(iColumnIndex)).append("|")
										.append(PGStructuredATSConstants.ACTION_CAP_ADD);
								String strAddKey = sbAddKey.toString();
								
								Map<Integer, String> mpATSDataMap = new HashMap<>();
								mpATSDataMap.put(iColumnIndex, strCharInfo);
								mpReplacedInfoMap.put(strAddKey, mpATSDataMap);
								
							} else if (UIUtil.isNotNullAndNotEmpty(strPrevCellInfo)
									&& PGStructuredATSConstants.KEY_ATS_PC.equals(strDataInfo)) {
								Map<Integer, String> mpATSDataMap = new HashMap<>();
								mpATSDataMap.put(iColumnIndex, strCharInfo);
								mpReplacedInfoMap.put(strPrevCellInfo, mpATSDataMap);

							} else {
								StringBuilder sbCurrentInfo = new StringBuilder();
								sbCurrentInfo.append(Integer.toString(iColumnIndex)).append("|")
										.append(PGStructuredATSConstants.ACTION_MODIFY);
								strPrevCellInfo = sbCurrentInfo.toString();
							}

						}
					}
				}
			}

		}
		return mpReplacedInfoMap;
	}

	/**
	 * Method to validate PC data
	 * @param strParentInfo
	 * @param mpAttrValIndexMap
	 * @param mpATSDataMap
	 * @param mpRelAttrIndexMap
	 * @param strCurrentOrAddInfo
	 * @return
	 */
	private String validatePCData(String strParentInfo, Map<Integer, String> mpAttrValIndexMap,
			Map<Integer, String> mpATSDataMap, Map<Integer, String> mpRelAttrIndexMap, String strCurrentOrAddInfo) {
		Map<String, String> mpColumnValues = new HashMap<>();
		
		Integer iColNo = 0;
		for (Map.Entry<?, ?> entryATS : mpATSDataMap.entrySet()) {
			iColNo = (Integer) entryATS.getKey();
			for(int i=0;i<iNoOfHeaderCol;i++) {
				int iIndex = iColNo+i;
				String strColHeader = mpRelAttrIndexMap.get(iIndex).trim();
				if(mpAttrValIndexMap.containsKey(iIndex)) {
					String strColValue = mpAttrValIndexMap.get(iIndex).trim();
					mpColumnValues.put(strColHeader, strColValue);
				} else {
					mpColumnValues.put(strColHeader, "");
				}
			}
		}

		String strCharInfo = mpATSDataMap.get(iColNo);
		StringList slOperationInfoList = StringUtil.split(strCurrentOrAddInfo, "|");
		String strOperation = slOperationInfoList.get(1);

		StringBuilder sbMessagePrefix = new StringBuilder();
		sbMessagePrefix.append(PGStructuredATSConstants.MSG_FORMULA).append(strParentInfo).append(PGStructuredATSConstants.MSG_SEP);
		sbMessagePrefix.append(PGStructuredATSConstants.KEY_OPERATION).append(":").append(strOperation).append(PGStructuredATSConstants.MSG_SEP);
		sbMessagePrefix.append(strCharInfo);
		
		return validateExcelDataBeforeImport(mpColumnValues, sbMessagePrefix.toString());
	}
	
	//Below methods are copied from 'com.pg.widgets.nexusPerformanceChars.PGPerfCharsImportFromFileUtil'
	//Methods to validate excel sheet data (UI level basic validations) : Start
	/**
	 * Method to perform some basic validations on imported excel data
	 * @param mpColumnValuesMap
	 * @return
	 */
	private String validateExcelDataBeforeImport(Map<String, String> mpColumnValues, String strMessagePrefix) {
		StringBuilder sbErrorMsg = new StringBuilder();

			if(mpColumnValues.containsKey(PGStructuredATSConstants.DISP_ACTION_RQD)) {
				String strActionRequired = mpColumnValues.get(PGStructuredATSConstants.DISP_ACTION_RQD);
				if(UIUtil.isNullOrEmpty(strActionRequired)) {
					sbErrorMsg.append(strMessagePrefix).append(PGStructuredATSConstants.MSG_COLON);
					sbErrorMsg.append(PGStructuredATSConstants.MSG_ACTION_REQUIRED);
				} else {
					validateBasedOnReportTypeColum(sbErrorMsg, strMessagePrefix, strActionRequired, mpColumnValues);
				}
			}
			
			if(mpColumnValues.containsKey(PGStructuredATSConstants.DISP_REPORT_TYPE)) {
				String strRportType = mpColumnValues.get(PGStructuredATSConstants.DISP_REPORT_TYPE);
				if (PGStructuredATSConstants.VAL_ATTRIBUTE.equals(strRportType)
						|| PGStructuredATSConstants.VAL_VARIABLE.equals(strRportType)) {
					validateTargetAndLimitColumns(sbErrorMsg, mpColumnValues, strRportType, strMessagePrefix);

				}
			}
			checkForTargetColumns(sbErrorMsg, strMessagePrefix, mpColumnValues);
			compareLimitAndTargetColunms(sbErrorMsg, strMessagePrefix, mpColumnValues);

		
		return sbErrorMsg.toString();
	}

	/**
	 * Validate other columns based on Report Type column
	 * @param sbErrorMsg
	 * @param strKey
	 * @param strActionRequired
	 * @param mpColumnValues
	 */
	private void validateBasedOnReportTypeColum(StringBuilder sbErrorMsg, String strMessagePrefix, String strActionRequired,
			Map<String, String> mpColumnValues) {
		if ((PGStructuredATSConstants.VAL_REPORT.equals(strActionRequired)
				|| PGStructuredATSConstants.VAL_SUMMARY.equals(strActionRequired))
				&& mpColumnValues.containsKey(PGStructuredATSConstants.DISP_REPORT_TYPE)) {
			String strRportType = mpColumnValues.get(PGStructuredATSConstants.DISP_REPORT_TYPE);
			if (!(PGStructuredATSConstants.VAL_ATTRIBUTE.equals(strRportType)
					|| PGStructuredATSConstants.VAL_VARIABLE.equals(strRportType))) {
				sbErrorMsg.append(strMessagePrefix)
						.append(PGStructuredATSConstants.MSG_COLON);
				sbErrorMsg.append(PGStructuredATSConstants.MSG_REPORT_TYPE);
				
			}
		}

	}

	/**
	 * Method to validate limit and target columns
	 * @param sbErrorMsg
	 * @param mpColumnValues
	 * @param strRportType
	 * @param strKey
	 */
	private void validateTargetAndLimitColumns(StringBuilder sbErrorMsg, Map<String, String> mpColumnValues,
			String strRportType, String strKey) {
		if(PGStructuredATSConstants.VAL_VARIABLE.equals(strRportType)) {
			boolean isAllLimitOrTagetColsEmpty = checkTargetAndLimitColumnsValues(mpColumnValues, true);
			if(isAllLimitOrTagetColsEmpty) {
				sbErrorMsg.append(strKey).append(PGStructuredATSConstants.MSG_COLON);
				sbErrorMsg.append(PGStructuredATSConstants.MSG_TARGET_LIMIT);
			} 
		} else {
			//for value ATTRIBUTE
			boolean isAllLimitOrTagetColsNotEmpty = checkTargetAndLimitColumnsValues(mpColumnValues, false);
			if (isAllLimitOrTagetColsNotEmpty
					&& mpColumnValues.containsKey(PGStructuredATSConstants.DISP_LSL)
					&& mpColumnValues.containsKey(PGStructuredATSConstants.DISP_USL)) {
				String strLowerSpecificationLimit = mpColumnValues.get(PGStructuredATSConstants.DISP_LSL);
				String strUpperSpecificationLimit = mpColumnValues.get(PGStructuredATSConstants.DISP_USL);
					
				if(UIUtil.isNullOrEmpty(strLowerSpecificationLimit) && UIUtil.isNullOrEmpty(strUpperSpecificationLimit)) {
					sbErrorMsg.append(strKey).append(PGStructuredATSConstants.MSG_COLON);
					sbErrorMsg.append(PGStructuredATSConstants.MSG_LSL_USL);
				}
			}
		}
		
	}

	/**
	 * Check for Target columns
	 * @param sbErrorMsg
	 * @param strKey
	 * @param mpColumnValues
	 */
	private void checkForTargetColumns(StringBuilder sbErrorMsg, String strMessagePrefix, Map<String, String> mpColumnValues) {
		if (mpColumnValues.containsKey(PGStructuredATSConstants.DISP_TARGET)
				&& mpColumnValues.containsKey(PGStructuredATSConstants.DISP_LT)
				&& mpColumnValues.containsKey(PGStructuredATSConstants.DISP_UT)) {
			String strLowerTarget = mpColumnValues.get(PGStructuredATSConstants.DISP_LT);
			String strTarget = mpColumnValues.get(PGStructuredATSConstants.DISP_TARGET);
			String strUpperTarget = mpColumnValues.get(PGStructuredATSConstants.DISP_UT);
			
			if(UIUtil.isNotNullAndNotEmpty(strTarget) && 
					(UIUtil.isNotNullAndNotEmpty(strLowerTarget) || UIUtil.isNotNullAndNotEmpty(strUpperTarget))) {
				sbErrorMsg.append(strMessagePrefix).append(PGStructuredATSConstants.MSG_COLON);
				sbErrorMsg.append(PGStructuredATSConstants.MSG_ERROR_TARGET_COLS);
			}
		}
		
	}

	/**
	 * Check for limit and target columns
	 * 
	 * @param mpColumnValues
	 * @return
	 */
	private boolean checkTargetAndLimitColumnsValues(Map<String, String> mpColumnValues, boolean bCheckForBlankValues) {
		String strLowerSpecificationLimit = mpColumnValues.get(PGStructuredATSConstants.DISP_LSL);
		String strLowerRoutineReleaseLimit = mpColumnValues.get(PGStructuredATSConstants.DISP_LRRL);
		String strLowerTarget = mpColumnValues.get(PGStructuredATSConstants.DISP_LT);
		String strTarget = mpColumnValues.get(PGStructuredATSConstants.DISP_TARGET);
		String strUpperTarget = mpColumnValues.get(PGStructuredATSConstants.DISP_UT);
		String strUpperSpecificationLimit = mpColumnValues.get(PGStructuredATSConstants.DISP_USL);
		String strUpperRoutineReleaseLimit = mpColumnValues.get(PGStructuredATSConstants.DISP_URRL);

		if(bCheckForBlankValues) {
			return (UIUtil.isNullOrEmpty(strLowerSpecificationLimit) && UIUtil.isNullOrEmpty(strLowerRoutineReleaseLimit)
					&& UIUtil.isNullOrEmpty(strLowerTarget) && UIUtil.isNullOrEmpty(strTarget)
					&& UIUtil.isNullOrEmpty(strUpperTarget) && UIUtil.isNullOrEmpty(strUpperSpecificationLimit)
					&& UIUtil.isNullOrEmpty(strUpperRoutineReleaseLimit));
			
		} else {
			return (UIUtil.isNotNullAndNotEmpty(strLowerSpecificationLimit) || UIUtil.isNotNullAndNotEmpty(strLowerRoutineReleaseLimit)
					|| UIUtil.isNotNullAndNotEmpty(strLowerTarget) || UIUtil.isNotNullAndNotEmpty(strTarget)
					|| UIUtil.isNotNullAndNotEmpty(strUpperTarget) || UIUtil.isNotNullAndNotEmpty(strUpperSpecificationLimit)
					|| UIUtil.isNotNullAndNotEmpty(strUpperRoutineReleaseLimit));
			
		}

	}
	
	/**
	 * Compare the Limit and Target columns for 'lesser than' or 'greater than' checks
	 * 
	 * @param sbErrorMsg
	 * @param strKey
	 * @param mpColumnValues
	 */
	private void compareLimitAndTargetColunms(StringBuilder sbErrorMsg, String strKey,
			Map<String, String> mpColumnValues) {
		compareColumnsAndUpdateMessage(PGStructuredATSConstants.DISP_LSL, PGStructuredATSConstants.DISP_LRRL, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGStructuredATSConstants.DISP_LRRL, PGStructuredATSConstants.DISP_LT, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGStructuredATSConstants.DISP_LT, PGStructuredATSConstants.DISP_UT, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGStructuredATSConstants.DISP_UT, PGStructuredATSConstants.DISP_URRL, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGStructuredATSConstants.DISP_URRL, PGStructuredATSConstants.DISP_USL, sbErrorMsg, strKey, mpColumnValues);
		
	}

	/**
	 * Compare the Limit and Target columns for 'lesser than' or 'greater than' checks
	 * 
	 * @param strLesserColName
	 * @param strGreatorColName
	 * @param sbErrorMsg
	 * @param strKey
	 * @param mpColumnValues
	 */
	private void compareColumnsAndUpdateMessage(String strLesserColName, String strGreatorColName, StringBuilder sbErrorMsg, String strKey,
			Map<String, String> mpColumnValues) {
		if(mpColumnValues.containsKey(strLesserColName) && mpColumnValues.containsKey(strGreatorColName)){
			String strLesserColVal = mpColumnValues.get(strLesserColName);
			String strGreatorColVal = mpColumnValues.get(strGreatorColName);
			
			if(UIUtil.isNotNullAndNotEmpty(strLesserColVal) && UIUtil.isNotNullAndNotEmpty(strGreatorColVal)) {
				float fLesserColVal =Float.parseFloat(strLesserColVal); 
				float fGreatorColVal =Float.parseFloat(strGreatorColVal); 
				
				if(fLesserColVal > fGreatorColVal) {
					sbErrorMsg.append(strKey).append(PGStructuredATSConstants.MSG_COLON);
					sbErrorMsg.append(strLesserColName).append(PGStructuredATSConstants.MSG_LESS_THAN).append(strGreatorColName).append("\n");
				}
			}
		}
		
	}
	//Methods to validate excel sheet data (UI level basic validations) : End
	
}
