package com.pg.widgets.nexusPerformanceChars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import org.json.JSONObject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import matrix.db.Page;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PGPerfCharsImportFromFileUtil {

	private static final Logger logger = Logger.getLogger(PGPerfCharsImportFromFileUtil.class.getName());
	PGPerfCharsCreateEditUtil objPerfCharsCreateEditUtil = new PGPerfCharsCreateEditUtil();
	StringList slHiddenColumnList = new StringList();

	/**
	 * Method to import PCs from an Excel file
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws FrameworkException
	 * @throws IOException 
	 */
	String importPerfCharsFromExcel(Context context, String strJsonInput) throws FrameworkException, IOException {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonDataArray = Json.createArrayBuilder();
		String strParentId = "";
		String strObjSelects = "";
		String strPerfCharFilter = "";
		OutputStream outputStream = null;
		File fileDirectory = null;
		File fileUploadExcel = null;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			strParentId = jsonInputData.getString(DomainConstants.SELECT_ID);
			strObjSelects = jsonInputData.getString(PGPerfCharsConstants.KEY_OBJ_SELECTS);
			strPerfCharFilter = jsonInputData.getString(PGPerfCharsConstants.KEY_CHARS_DERIVED_FILTER);
			String strFileBase64 = jsonInputData.getString(PGPerfCharsConstants.KEY_FILENAME);
			String strTableHeaders = jsonInputData.getString(PGPerfCharsConstants.KEY_TABLE_COL_DETAILS);
			StringList slSBColumnLabelList = StringUtil.split(strTableHeaders, ",");
			
			JSONObject jsonBase64Obj = new JSONObject(strFileBase64);
			String strFileName = jsonBase64Obj.getString(PGPerfCharsConstants.KEY_FILEN_AME);
			String strBase64Data = jsonBase64Obj.getString(PGPerfCharsConstants.KEY_DATA);
			
			String[] strB64DataArray = strBase64Data.split(PGPerfCharsConstants.KEY_BASE_64);

			String strWorkspace = context.createWorkspace();
			fileDirectory = new File(strWorkspace);
			fileUploadExcel = new File(fileDirectory, strFileName);

			outputStream = new FileOutputStream(fileUploadExcel);
			byte[] decoder = Base64.getDecoder().decode(strB64DataArray[1].toString().replace("\"", ""));
			outputStream.write(decoder);
			outputStream.close();
			
			JsonArray jsonTableColumnArray = getJsonTableColumnsFromPage(context);

			Map mpColumnValuesMap = importPerformanceCharacteristic(context, fileUploadExcel, strParentId,
					jsonTableColumnArray, slSBColumnLabelList);
			if (mpColumnValuesMap.containsKey(PGPerfCharsConstants.ERROR)) {
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR,
						(String) mpColumnValuesMap.get(PGPerfCharsConstants.ERROR));
			} else {
				jsonReturnObj = validateExcelDataBeforeImport(mpColumnValuesMap);

			}
			
			if (jsonReturnObj.build().isEmpty()) {
				jsonDataArray = getJsonDataArrayForUpdateOperation(context, mpColumnValuesMap, jsonTableColumnArray,
						strParentId);
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_CREATE_EDIT_UTIL, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			if (fileUploadExcel != null) {
				boolean bIsFileDeleted = fileUploadExcel.delete();
				if(!bIsFileDeleted) {
					logger.log(Level.WARNING, PGPerfCharsConstants.MESSAGE_DEL_FILE_FAILED, fileUploadExcel.getName());
				}
			}
			if (fileDirectory != null) {
				boolean bIsDirDeleted = fileDirectory.delete();
				if(!bIsDirDeleted) {
					logger.log(Level.WARNING, PGPerfCharsConstants.MESSAGE_DEL_DIR_FAILED, fileDirectory.getName());
				}
			}
		}

		if (jsonReturnObj.build().isEmpty()) {
			JsonObjectBuilder jsonCreateEditPayloadObj = Json.createObjectBuilder();
			jsonCreateEditPayloadObj.add(PGPerfCharsConstants.KEY_DATA, jsonDataArray);
			jsonCreateEditPayloadObj.add(DomainConstants.SELECT_ID, strParentId);
			jsonCreateEditPayloadObj.add(PGPerfCharsConstants.KEY_OBJ_SELECTS, strObjSelects);
			jsonCreateEditPayloadObj.add(PGPerfCharsConstants.KEY_CHARS_DERIVED_FILTER, strPerfCharFilter);
			jsonCreateEditPayloadObj.add(PGPerfCharsConstants.KEY_SELECTED_TABLE,
					PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE);

			String strCreateEditOprPayload = jsonCreateEditPayloadObj.build().toString();

			return objPerfCharsCreateEditUtil.createUpdatePerfChars(context, strCreateEditOprPayload);

		} else {
			return jsonReturnObj.build().toString();
		}
	}

	/**
	 * Method to create PCs from imported excel file
	 * 
	 * @param context
	 * @param mpColumnValuesMap
	 * @param jsonTableColumnArray
	 * @return
	 * @throws FrameworkException
	 */
	private JsonArrayBuilder getJsonDataArrayForUpdateOperation(Context context, Map<Object, Object> mpColumnValuesMap,
			JsonArray jsonTableColumnArray, String strParentId) throws FrameworkException {
		JsonArrayBuilder jsonDataArray = Json.createArrayBuilder();
		Map<String, String> mpHeaderAttrExpMap = new HashMap<>();
		int iJsonArraySize = jsonTableColumnArray.size();
		for (int i = 0; i < iJsonArraySize; i++) {
			JsonObject jsonColumnObj = (JsonObject) jsonTableColumnArray.get(i);
			String strColumnLabel = jsonColumnObj.getString("headerName");
			String strFieldName = jsonColumnObj.getString("field");
			mpHeaderAttrExpMap.put(strColumnLabel, strFieldName);
		}

		for (Map.Entry<Object, Object> entry : mpColumnValuesMap.entrySet()) {
			Map<String, String> mpColumnValues = (Map<String, String>) entry.getValue();

			JsonObjectBuilder jsonColObj = Json.createObjectBuilder();
			JsonObjectBuilder jsonAttrObj = Json.createObjectBuilder();
			JsonArrayBuilder jsonRelAttrArray = Json.createArrayBuilder();
			JsonArrayBuilder jsonConnectionsArray = Json.createArrayBuilder();
			for (Map.Entry<String, String> entryCol : mpColumnValues.entrySet()) {
				String strKey = entryCol.getKey();
				String strValue = entryCol.getValue();

				String strExprKey = mpHeaderAttrExpMap.get(strKey);
				if(strExprKey != null) {
					if (strExprKey.startsWith(PGPerfCharsConstants.PREFIX_ATTR_SELECT)) {
						String strExpKeyTemp = strExprKey.replace(PGPerfCharsConstants.PREFIX_ATTR_SELECT, "");
						String strAttrName = strExpKeyTemp.replace(PGPerfCharsConstants.SUFFIX_ATTR_SELECT, "");
						jsonAttrObj.add(strAttrName, strValue);
					} else if (PGPerfCharsConstants.KEY_SEQUENCE.equals(strExprKey)) {
						JsonObjectBuilder jsonSequenceObj = Json.createObjectBuilder();
						jsonSequenceObj.add(PGPerfCharsConstants.ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE, strValue);
						jsonSequenceObj.add(PGPerfCharsConstants.KEY_RELID, "");
						jsonRelAttrArray.add(jsonSequenceObj);
					} else if (strExprKey.contains("TestMethodName") || strExprKey.contains("TestMethodRefDoc")) {
						JsonObjectBuilder jsonConnInfoObj = Json.createObjectBuilder();
						jsonConnInfoObj.add(DomainConstants.SELECT_TYPE, PGPerfCharsConstants.KEY_CONNECTIONS);
						jsonConnInfoObj.add(DomainConstants.SELECT_NAME, strExprKey);
						jsonConnInfoObj.add("names", strValue);
						jsonConnInfoObj.add("ids", "");
						jsonConnectionsArray.add(jsonConnInfoObj);
					}
				}
			}

			jsonColObj.add(PGPerfCharsConstants.KEY_OPERATION, PGPerfCharsConstants.KEY_CREATE);
			jsonColObj.add(PGPerfCharsConstants.KEY_ATTRIBUTE, jsonAttrObj);
			jsonColObj.add(PGPerfCharsConstants.KEY_REL_ATTRIBUTE, jsonRelAttrArray);
			jsonColObj.add(PGPerfCharsConstants.KEY_CONNECTIONS, jsonConnectionsArray);
			jsonDataArray.add(jsonColObj);
		}

		updateJsonDataForDeleteOperation(context, strParentId, jsonDataArray);

		return jsonDataArray;
	}

	/**
	 * Method to updated Json with delete operation information
	 * 
	 * @param context
	 * @param jsonDataArray
	 * @throws FrameworkException
	 */
	private void updateJsonDataForDeleteOperation(Context context, String strParentId, JsonArrayBuilder jsonDataArray)
			throws FrameworkException {
		DomainObject dobParentObj = DomainObject.newInstance(context, strParentId);
		MapList mlRelatedPCObjList = dobParentObj.getRelatedObjects(context, // the eMatrix Context object
				PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC, // Relationship pattern
				PGPerfCharsConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC, // Type pattern
				StringList.create(DomainConstants.SELECT_ID), // Object selects
				null, // Relationship selects
				false, // get From relationships
				true, // get To relationships
				(short) 1, // the number of levels to expand, 0 equals expand all.
				null, // Object where clause
				null, // Relationship where clause
				0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
					// data available

		if (mlRelatedPCObjList != null) {
			int iListSize = mlRelatedPCObjList.size();
			for (int i = 0; i < iListSize; i++) {
				JsonObjectBuilder jsonDelPCobj = Json.createObjectBuilder();
				Map<?, ?> mpPCObjMap = (Map<?, ?>) mlRelatedPCObjList.get(i);
				String strRelatedPCId = (String) mpPCObjMap.get(DomainConstants.SELECT_ID);
				jsonDelPCobj.add(PGPerfCharsConstants.KEY_OPERATION, PGPerfCharsConstants.KEY_DEL);
				jsonDelPCobj.add(DomainConstants.SELECT_ID, strRelatedPCId);
				jsonDataArray.add(jsonDelPCobj);
			}
		}
	}

	/**
	 * Method to get Json table columns from page 'pgNexusPerfCharTables'
	 * 
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	private JsonArray getJsonTableColumnsFromPage(Context context) throws MatrixException {
		Page pgPCTable = new Page(PGPerfCharsConstants.PAGE_NEXUS_PERF_CHAR_TABLE);
		pgPCTable.open(context);
		String strPageContent = pgPCTable.getContents(context);
		JsonObject jsonPageContent = PGWidgetUtil.getJsonFromJsonString(strPageContent);
		pgPCTable.close(context);
		return jsonPageContent.getJsonArray(PGPerfCharsConstants.STR_PG_VPD_PERFORMANCE_CHARACTERISTIC_TABLE);
	}

	/**
	 * Method copied from pgDSOExportChracteristic:importPerformanceCharacteristic
	 * 
	 * @param context
	 * @param fileUploadExcel
	 * @param strPDObjectId
	 * @param jsonTableColumnArray
	 * @param slSBColumnLabelList
	 * @return
	 * @throws Exception
	 */
	public Map importPerformanceCharacteristic(Context context, File fileUploadExcel, String strPDObjectId,
			JsonArray jsonTableColumnArray, StringList slSBColumnLabelList) throws Exception {
		HashMap resultmap = new HashMap();
		try {
			MapList mlExcelData = parseLoadExcel(context, fileUploadExcel);
			Map generalInfo = new HashMap();
			generalInfo.put("strObjectId", strPDObjectId);

			if (mlExcelData != null && mlExcelData.size() > 0) {

				Map mExcelSheetMap = (Map) mlExcelData.get(0);// Assumption : Data will exist in first sheet in the
																// excel. All other sheets will be ignored for upload.

				// Start : Validation of input Excel file data
				boolean isValid = validateImportExcel(context, mExcelSheetMap, jsonTableColumnArray, slSBColumnLabelList);

				if (!isValid) {
					String strErrorMsg = new StringBuilder(
							getPageProperty(context, "pgImportExportConfigurations", "pgImport.Messge.ERROR"))
									.append(" : ").append(getPageProperty(context, "pgImportExportConfigurations",
											"pgImport.Messge.ImportCanNotBeProcessed"))
									.toString();
					// If the EXCEL Sheet is not valid, Abort the import process
					resultmap.put(PGPerfCharsConstants.ERROR, strErrorMsg);
					return resultmap;
				}
				// End : Validation of input Excel file data

				ContextUtil.startTransaction(context, true);

				mExcelSheetMap.put("generalInfo", generalInfo);
				resultmap = (HashMap) createAndUploadPerformanceCharacteristicsData(context, mExcelSheetMap,
						strPDObjectId);

				ContextUtil.commitTransaction(context);
			}

		} catch (Exception ex) {

			if (context.isTransactionActive()) {
				// Abort trasaction in case of Exception
				ContextUtil.abortTransaction(context);
			}
			
			resultmap.put(PGPerfCharsConstants.ERROR,
					new StringBuilder(getPageProperty(context, "pgImportExportConfigurations", "pgImport.Messge.ERROR"))
							.append(" : ").append(ex.getMessage()).toString());
		}

		return resultmap;
	}

	/**
	 * This method will parse the input Excel file and returns the result in
	 * <code>MapList</code> where each containing Map represents data of one excel
	 * sheet
	 * 
	 * @param context
	 * @param fUploadedFile : Input Excel file
	 * @return <code>MapList</code> as described above
	 * @throws Exception
	 */
	private MapList parseLoadExcel(Context context, File fUploadedFile) throws Exception {

		MapList mlExcelDataMapList = new MapList();
		FileInputStream fileInStream = null;
		Map mapExcelContent = new HashMap();

		try {
			Workbook workbook = WorkbookFactory.create(new FileInputStream(fUploadedFile));
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

			fileInStream = new FileInputStream(fUploadedFile);
			Sheet sheet = null;
			Iterator rows = null;
			Cell cell = null;
			Iterator cells = null;
			Row row = null;
			int iTotalRowCount = 0;
			int iTotalColumnCount = 0;
			String strSheetName = "";

			int iNumberOfSheets = 1;

			for (int iSheetCount = 0; iSheetCount < iNumberOfSheets; iSheetCount++) {
				sheet = workbook.getSheetAt(iSheetCount);
				strSheetName = sheet.getSheetName();
				mapExcelContent = new HashMap();

				rows = sheet.rowIterator();

				// Iterating Excel Rows
				while (rows.hasNext()) {
					iTotalRowCount++;
					row = ((Row) rows.next());
					cells = row.cellIterator();

					// Iterating Excel cells (columns)
					while (cells.hasNext()) {

						if (iTotalRowCount == 1) {
							// Count the columns only for Header row i.e. Row = 1
							iTotalColumnCount++;
						}
						cell = (Cell) cells.next();
						CellType cellType = cell.getCellType();
						int iRowIndex = cell.getRowIndex();
						int iColumnIndex = cell.getColumnIndex();
						String strCellPosition = iRowIndex + "," + iColumnIndex;

						if (cellType == CellType.NUMERIC) {

							String strNumValue = Double.toString(cell.getNumericCellValue());

							if (UIUtil.isNotNullAndNotEmpty(strNumValue) && strNumValue.indexOf("E") != -1) {
								long longVal = new Double(cell.getNumericCellValue()).longValue(); // This will bring
																									// the exact value
																									// in variable
								strNumValue = Long.toString(longVal);
							}

							if (strNumValue != null && strNumValue.indexOf(".") != -1) {
								StringList slTemp = FrameworkUtil.split(strNumValue, ".");
								String strIntValue = (String) slTemp.get(0);
								String strDecimalValue = (String) slTemp.get(1);
								if (strDecimalValue.equals("0")) {
									strNumValue = strIntValue;
								}
							}
							mapExcelContent.put(strCellPosition, strNumValue);

						} else if (cellType == CellType.STRING) {

							String strValue = cell.getStringCellValue();
							mapExcelContent.put(strCellPosition, strValue);

						} else if (cellType == CellType.BLANK) {

							mapExcelContent.put(strCellPosition, DomainConstants.EMPTY_STRING);

						} else if (cellType == CellType.FORMULA) {

							CellValue cellValue = evaluator.evaluate(cell);

							switch (cellValue.getCellType()) {
							case NUMERIC:
								String strNumValue = Double.toString(cell.getNumericCellValue());
								if (strNumValue != null && strNumValue.indexOf(".") != -1) {
									StringList slTemp = FrameworkUtil.split(strNumValue, ".");
									String strIntValue = (String) slTemp.get(0);
									String strDecimalValue = (String) slTemp.get(1);
									if (strDecimalValue.equals("0")) {
										strNumValue = strIntValue;
									}
								}
								mapExcelContent.put(strCellPosition, strNumValue);

								break;
							case STRING:
								mapExcelContent.put(strCellPosition, cellValue.getStringValue());
								break;
							case BLANK:
								break;
							case ERROR:
								break;
							case FORMULA:
								break;
							}
						} else {
							// in rest of the condition put the cell as is.
							mapExcelContent.put(strCellPosition, cell);
						}
					}
				}
				mapExcelContent.put("TOTAL_ROW_COUNT", iTotalRowCount);// Total Row count per excel sheet
				mapExcelContent.put("TOTAL_COLUMN_COUNT", iTotalColumnCount);// Total Column count per excel sheet
			}

			// Add sheet data into mapList
			mlExcelDataMapList.add(mapExcelContent);

		} catch (Exception e) {

			
			throw e;

		} finally {

			if (fileInStream != null) {
				fileInStream.close();
			}
		}
		return mlExcelDataMapList;
	}

	/**
	 * This method will validate the imported Excel Sheet Columns with the Structure
	 * Browser Table View for Import Validation.
	 * 
	 * @param context
	 * @param mExcelData
	 * @param jsonTableColumnArray
	 * @param slSBColumnLabelList
	 * @return
	 * @throws Exception
	 */
	private boolean validateImportExcel(Context context, Map mExcelData, JsonArray jsonTableColumnArray, StringList slSBColumnLabelList)
			throws Exception {

		boolean isValid = true;

		StringList slExcelColumnLabelList = new StringList();

		try {
			// Getting header info from 'pgNexusPerfCharTables' page object : Start
			int iJsonArraySize = jsonTableColumnArray.size();
			for (int i = 0; i < iJsonArraySize; i++) {
				JsonObject jsonColumnObj = (JsonObject) jsonTableColumnArray.get(i);
				String strColumnLabel = jsonColumnObj.getString("headerName");
				if (slSBColumnLabelList.contains(strColumnLabel) && jsonColumnObj.containsKey("settings")) {
					// added for Handle "Path" column in Export and Import of Performance
					// Characteristics : Start
					JsonObject jsonSettingsObj = jsonColumnObj.getJsonObject("settings");
					if (jsonSettingsObj.containsKey(PGPerfCharsConstants.SETTING_PG_EXPORT_HIDDEN)) {
						String strIsHiddenColumn = jsonSettingsObj
								.getString(PGPerfCharsConstants.SETTING_PG_EXPORT_HIDDEN);
						if (UIUtil.isNotNullAndNotEmpty(strIsHiddenColumn)
								&& strIsHiddenColumn.equalsIgnoreCase("true")) {
							slHiddenColumnList.add(strColumnLabel);
						}
					}
					// added for Handle "Path" column in Export and Import of Performance
					// Characteristics : End
				}

			}
			// Getting header info from 'pgNexusPerfCharTables' page object : End

			// Getting column list from imported Excel sheet
			int iTotalRowCount = (Integer) mExcelData.get("TOTAL_ROW_COUNT");
			int iTotalColumnCount = (Integer) mExcelData.get("TOTAL_COLUMN_COUNT");
			int iheaderRowIndex = 0;// First row in the excel sheet should be the Header Row
			Map mColumnValueMap = new LinkedHashMap();

			// Iterating Excel Columns
			for (int iColumnIndex = 0; iColumnIndex < iTotalColumnCount; iColumnIndex++) {

				String COLUMN_HEADER = DomainConstants.EMPTY_STRING;
				String HEADER_CELL_INDEX = iheaderRowIndex + "," + iColumnIndex;
				Object oHEADER_CELL_VALUE = mExcelData.get(HEADER_CELL_INDEX);

				if (oHEADER_CELL_VALUE instanceof String) {
					COLUMN_HEADER = ((String) oHEADER_CELL_VALUE).trim();
				} else if (oHEADER_CELL_VALUE instanceof Integer) {
					COLUMN_HEADER = ((Integer) oHEADER_CELL_VALUE).toString();
				}
				slExcelColumnLabelList.add(COLUMN_HEADER);
			}

			//DSM : 2022x.5 : Code segment to handle the column 'Nexus Parameter List ID' which does not exist on the table : Start
			String strNexusParameterListIDColName = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",
					context.getLocale(), "emxCPN.Characteristics.Nexus.Parameter.ListID");
			if (UIUtil.isNotNullAndNotEmpty(strNexusParameterListIDColName)
					&& slExcelColumnLabelList.contains(strNexusParameterListIDColName)
					&& !slSBColumnLabelList.contains(strNexusParameterListIDColName)) {
				slSBColumnLabelList.add(strNexusParameterListIDColName);
				
			}
			//DSM : 2022x.5 : Code segment to handle the column 'Nexus Parameter List ID' which does not exist on the table : End

			//DSM : 2022x.5 : Code to handle 'Nexus Parameters' custom view column mismatch
			if(slSBColumnLabelList.size() != slExcelColumnLabelList.size()) {
				slSBColumnLabelList = getUpdatedColHeaderList(slSBColumnLabelList);
			}
			
			slSBColumnLabelList.sort();
			slExcelColumnLabelList.sort();

			// column sequence check
			isValid = matchLists(slSBColumnLabelList, slExcelColumnLabelList);

		} catch (Exception ex) {
			
			throw ex;
		}
		return isValid;
	}

	/**
	 * Method to get the final column header list by removing Nexus param attribute related columns
	 * Purpose of this method is to fix issue related to 'Nexus Parameters' custom view column mismatch
	 * 
	 * @param slTableHeaderColList
	 * @return
	 */
	private StringList getUpdatedColHeaderList(StringList slTableHeaderColList) {
		StringList slSBColumnLabelList = new StringList();
		StringList slNexusParamAttrColList = StringUtil.split(PGPerfCharsConstants.NEXUS_PARAM_ATTR_COL_LIST, ",");
		
		for(String strColHeader : slTableHeaderColList) {
			if(!slNexusParamAttrColList.contains(strColHeader)) {
				slSBColumnLabelList.add(strColHeader);
			}
		}

		return slSBColumnLabelList;
	}
	
	/**
	 * This is a utility method to compare the elements of two different lists. If
	 * elements and their sequence of both the lists are same then it returns true
	 * else false.
	 * 
	 * @param list1 : first <code>StringList</code
	 * @param list2 : second <code>StringList</code
	 * @return boolean as described above
	 * @throws Exception
	 */
	private boolean matchLists(StringList list1, StringList list2) throws Exception {

		boolean bResult = true;

		try {
			int iSize1 = list1.size();
			int iSize2 = list2.size();
			if (iSize1 == iSize2) {
				for (int i = 0; i < iSize1; i++) {
					String strVAl1 = (String) list1.get(i);
					String strVAl2 = (String) list2.get(i);
					if (!(strVAl1.equals(strVAl2))) {
						bResult = false;
						break;
					}
				}
			} else {
				bResult = false;
			}
		} catch (Exception ex) {
			
			throw ex;
		}
		return bResult;
	}

	/**
	 * Method to create Map with formatted data from excel sheet
	 * 
	 * @param context
	 * @param mExcelSheetData
	 * @param strPDObjectId
	 * @return
	 * @throws Exception
	 */
	private Map createAndUploadPerformanceCharacteristicsData(Context context, Map mExcelSheetData,
			String strPDObjectId) throws Exception {
		LinkedHashMap exportTableMap = new LinkedHashMap();
		try {
			String strSamplingHeader = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",
					context.getLocale(), "emxCPN.Common.Sampling");
			int iTotalRowCount = (Integer) mExcelSheetData.get("TOTAL_ROW_COUNT");
			int iTotalColumnCount = (Integer) mExcelSheetData.get("TOTAL_COLUMN_COUNT");
			DomainObject doProductData = DomainObject.newInstance(context, strPDObjectId);

			DomainObject doNewPCObj = null;
			String strSeqNumber = "";

			LinkedHashMap colValMap = new LinkedHashMap();
			int iheaderRowIndex = 0;
			for (int iRow = 0; iRow < iTotalRowCount; iRow++) {

				if (iRow == 0) {

					continue;
				}

				// Array for table column names
				String[] ColumnNames = new String[iTotalColumnCount];

				// Initialize the map for every new row
				colValMap = new LinkedHashMap();

				// Iterating Excel Columns
				for (int iColumnIndex = 0; iColumnIndex < iTotalColumnCount; iColumnIndex++) {

					String COLUMN_HEADER = DomainConstants.EMPTY_STRING;
					String CELL_VALUE = DomainConstants.EMPTY_STRING;

					String HEADER_CELL_INDEX = iheaderRowIndex + "," + iColumnIndex;
					Object oHEADER_CELL_VALUE = mExcelSheetData.get(HEADER_CELL_INDEX);

					String CELL_INDEX = iRow + "," + iColumnIndex;
					Object oCELL_VALUE = mExcelSheetData.get(CELL_INDEX);

					if (oCELL_VALUE instanceof String) {
						CELL_VALUE = ((String) oCELL_VALUE).trim();
					} else if (oCELL_VALUE instanceof Integer) {
						CELL_VALUE = ((Integer) oCELL_VALUE).toString();
					}

					if (oHEADER_CELL_VALUE instanceof String) {
						COLUMN_HEADER = ((String) oHEADER_CELL_VALUE).trim();
						if (COLUMN_HEADER.equalsIgnoreCase("Action Required")) {
							if (CELL_VALUE != null && CELL_VALUE.length() > 0) {
								CELL_VALUE = CELL_VALUE.toUpperCase();
							}
						}

					} else if (oHEADER_CELL_VALUE instanceof Integer) {
						COLUMN_HEADER = ((Integer) oHEADER_CELL_VALUE).toString();
					}

					if (COLUMN_HEADER != null && CELL_VALUE != null) {

						if (slHiddenColumnList.contains(COLUMN_HEADER)) {
							CELL_VALUE = DomainConstants.EMPTY_STRING;
						}

						//DSM 2022x.5 : Sampling data can have new line
						if(!strSamplingHeader.equals(COLUMN_HEADER)) {
							CELL_VALUE = CELL_VALUE.replaceAll("\n", " ");
						}

						colValMap.put(COLUMN_HEADER, CELL_VALUE);
						if (iColumnIndex == 0) {
							strSeqNumber = CELL_VALUE;
						}
					}
				}

				if (!exportTableMap.containsKey(strSeqNumber))
					exportTableMap.put(strSeqNumber, colValMap);
				else {
					String dupSequenceAlert = getPageProperty(context, "pgImportExportConfigurations",
							"pgImport.Messge.DuplicateSequenceNumberFound");
					dupSequenceAlert = dupSequenceAlert + strSeqNumber;
					exportTableMap.clear();
					exportTableMap.put(PGPerfCharsConstants.ERROR, dupSequenceAlert);
				}
			}

		} catch (Exception ex) {
			
			throw ex;
		}
		return exportTableMap;
	}

	/**
	 * This is a utility method to get the value from Page file property
	 * 
	 * @param context
	 * @param strPageName : Page File name
	 * @param strKey      : Property Key
	 * @return : returns value of the given property key
	 * @throws Exception
	 */
	public static String getPageProperty(Context context, String strPageName, String strKey) throws Exception {
		String strValue = DomainConstants.EMPTY_STRING;
		try {
			String isPageExists = MqlUtil.mqlCommand(context, "list page $1", strPageName);
			String strProperties = UIUtil.isNotNullAndNotEmpty(isPageExists)
					? MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageName)
					: "";
			if (UIUtil.isNotNullAndNotEmpty(strProperties) && UIUtil.isNotNullAndNotEmpty(strKey)) {
				Properties properties = new Properties();
				properties.load(new StringReader(strProperties));
				strValue = properties.getProperty(strKey);
			}
		} catch (Exception ex) {
			
			throw ex;
		}
		return strValue;
	}
	
	//Methods to validate excel sheet data (UI level basic validations) : Start
	/**
	 * Method to perform some basic validations on imported excel data
	 * @param mpColumnValuesMap
	 * @return
	 */
	private JsonObjectBuilder validateExcelDataBeforeImport(Map<Object,Object> mpColumnValuesMap) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		StringBuilder sbErrorMsg = new StringBuilder();
		for (Map.Entry<Object, Object> entry : mpColumnValuesMap.entrySet()) {
			String strKey = (String) entry.getKey();
			Map<String, String> mpColumnValues = (Map<String, String>) entry.getValue();
			if(mpColumnValues.containsKey(PGPerfCharsConstants.COL_ACTION_REQUIRED)) {
				String strActionRequired = mpColumnValues.get(PGPerfCharsConstants.COL_ACTION_REQUIRED);
				if(UIUtil.isNullOrEmpty(strActionRequired)) {
					sbErrorMsg.append(PGPerfCharsConstants.MSG_PREFIX_SEQ_NO).append(strKey).append(PGPerfCharsConstants.MSG_COLON);
					sbErrorMsg.append(PGPerfCharsConstants.MSG_ACTION_REQUIRED);
				} else {
					validateBasedOnReportTypeColum(sbErrorMsg, strKey, strActionRequired, mpColumnValues);
				}
			}
			
			if(mpColumnValues.containsKey(PGPerfCharsConstants.COL_REPORT_TYPE)) {
				String strRportType = mpColumnValues.get(PGPerfCharsConstants.COL_REPORT_TYPE);
				if (PGPerfCharsConstants.VAL_ATTRIBUTE.equals(strRportType)
						|| PGPerfCharsConstants.VAL_VARIABLE.equals(strRportType)) {
					validateTargetAndLimitColumns(sbErrorMsg, mpColumnValues, strRportType, strKey);

				}
			}
			checkForTargetColumns(sbErrorMsg, strKey, mpColumnValues);
			compareLimitAndTargetColunms(sbErrorMsg, strKey, mpColumnValues);
		}
		
		String strErrorMsg = sbErrorMsg.toString();
		if(UIUtil.isNotNullAndNotEmpty(strErrorMsg)) {
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, strErrorMsg);
		}
		return jsonReturnObj;
	}

	/**
	 * Validate other columns based on Report Type column
	 * @param sbErrorMsg
	 * @param strKey
	 * @param strActionRequired
	 * @param mpColumnValues
	 */
	private void validateBasedOnReportTypeColum(StringBuilder sbErrorMsg, String strKey, String strActionRequired,
			Map<String, String> mpColumnValues) {
		if ((PGPerfCharsConstants.VAL_REPORT.equals(strActionRequired)
				|| PGPerfCharsConstants.VAL_SUMMARY.equals(strActionRequired))
				&& mpColumnValues.containsKey(PGPerfCharsConstants.COL_REPORT_TYPE)) {
			String strRportType = mpColumnValues.get(PGPerfCharsConstants.COL_REPORT_TYPE);
			if (!(PGPerfCharsConstants.VAL_ATTRIBUTE.equals(strRportType)
					|| PGPerfCharsConstants.VAL_VARIABLE.equals(strRportType))) {
				sbErrorMsg.append(PGPerfCharsConstants.MSG_PREFIX_SEQ_NO).append(strKey)
						.append(PGPerfCharsConstants.MSG_COLON);
				sbErrorMsg.append(PGPerfCharsConstants.MSG_REPORT_TYPE);
				
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
		if(PGPerfCharsConstants.VAL_VARIABLE.equals(strRportType)) {
			boolean isAllLimitOrTagetColsEmpty = checkTargetAndLimitColumnsValues(mpColumnValues, true);
			if(isAllLimitOrTagetColsEmpty) {
				sbErrorMsg.append(PGPerfCharsConstants.MSG_PREFIX_SEQ_NO).append(strKey).append(PGPerfCharsConstants.MSG_COLON);
				sbErrorMsg.append(PGPerfCharsConstants.MSG_TARGET_LIMIT);
			} 
		} else {
			//for value ATTRIBUTE
			boolean isAllLimitOrTagetColsNotEmpty = checkTargetAndLimitColumnsValues(mpColumnValues, false);
			if (isAllLimitOrTagetColsNotEmpty
					&& mpColumnValues.containsKey(PGPerfCharsConstants.COL_LOWER_SPECIFICATION_LIMIT)
					&& mpColumnValues.containsKey(PGPerfCharsConstants.COL_UPPER_SPECIFICATION_LIMIT)) {
				String strLowerSpecificationLimit = mpColumnValues.get(PGPerfCharsConstants.COL_LOWER_SPECIFICATION_LIMIT);
				String strUpperSpecificationLimit = mpColumnValues.get(PGPerfCharsConstants.COL_UPPER_SPECIFICATION_LIMIT);
					
				if(UIUtil.isNullOrEmpty(strLowerSpecificationLimit) && UIUtil.isNullOrEmpty(strUpperSpecificationLimit)) {
					sbErrorMsg.append(PGPerfCharsConstants.MSG_PREFIX_SEQ_NO).append(strKey).append(PGPerfCharsConstants.MSG_COLON);
					sbErrorMsg.append(PGPerfCharsConstants.MSG_LSL_USL);
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
	private void checkForTargetColumns(StringBuilder sbErrorMsg, String strKey, Map<String, String> mpColumnValues) {
		if (mpColumnValues.containsKey(PGPerfCharsConstants.COL_TARGET)
				&& mpColumnValues.containsKey(PGPerfCharsConstants.COL_LOWER_TARGET)
				&& mpColumnValues.containsKey(PGPerfCharsConstants.COL_UPPER_TARGET)) {
			String strLowerTarget = mpColumnValues.get(PGPerfCharsConstants.COL_LOWER_TARGET);
			String strTarget = mpColumnValues.get(PGPerfCharsConstants.COL_TARGET);
			String strUpperTarget = mpColumnValues.get(PGPerfCharsConstants.COL_UPPER_TARGET);
			
			if(UIUtil.isNotNullAndNotEmpty(strTarget) && 
					(UIUtil.isNotNullAndNotEmpty(strLowerTarget) || UIUtil.isNotNullAndNotEmpty(strUpperTarget))) {
				sbErrorMsg.append(PGPerfCharsConstants.MSG_PREFIX_SEQ_NO).append(strKey).append(PGPerfCharsConstants.MSG_COLON);
				sbErrorMsg.append(PGPerfCharsConstants.MSG_ERROR_TARGET_COLS);
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
		String strLowerSpecificationLimit = mpColumnValues.get(PGPerfCharsConstants.COL_LOWER_SPECIFICATION_LIMIT);
		String strLowerRoutineReleaseLimit = mpColumnValues.get(PGPerfCharsConstants.COL_LOWER_ROUTINE_RELEASE_LIMIT);
		String strLowerTarget = mpColumnValues.get(PGPerfCharsConstants.COL_LOWER_TARGET);
		String strTarget = mpColumnValues.get(PGPerfCharsConstants.COL_TARGET);
		String strUpperTarget = mpColumnValues.get(PGPerfCharsConstants.COL_UPPER_TARGET);
		String strUpperSpecificationLimit = mpColumnValues.get(PGPerfCharsConstants.COL_UPPER_SPECIFICATION_LIMIT);
		String strUpperRoutineReleaseLimit = mpColumnValues.get(PGPerfCharsConstants.COL_UPPER_ROUTINE_RELEASE_LIMIT);

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
		compareColumnsAndUpdateMessage(PGPerfCharsConstants.COL_LOWER_SPECIFICATION_LIMIT, PGPerfCharsConstants.COL_LOWER_ROUTINE_RELEASE_LIMIT, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGPerfCharsConstants.COL_LOWER_ROUTINE_RELEASE_LIMIT, PGPerfCharsConstants.COL_LOWER_TARGET, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGPerfCharsConstants.COL_LOWER_TARGET, PGPerfCharsConstants.COL_UPPER_TARGET, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGPerfCharsConstants.COL_UPPER_TARGET, PGPerfCharsConstants.COL_UPPER_ROUTINE_RELEASE_LIMIT, sbErrorMsg, strKey, mpColumnValues);
		compareColumnsAndUpdateMessage(PGPerfCharsConstants.COL_UPPER_ROUTINE_RELEASE_LIMIT, PGPerfCharsConstants.COL_UPPER_SPECIFICATION_LIMIT, sbErrorMsg, strKey, mpColumnValues);
		
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
					sbErrorMsg.append(PGPerfCharsConstants.MSG_PREFIX_SEQ_NO).append(strKey).append(PGPerfCharsConstants.MSG_COLON);
					sbErrorMsg.append(strLesserColName).append(PGPerfCharsConstants.MSG_LESS_THAN).append(strGreatorColName).append("\n");
				}
			}
		}
		
	}
	//Methods to validate excel sheet data (UI level basic validations) : End

}