package com.pg.widgets.nexusPerformanceChars;

import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.json.JSONArray;

import com.google.gson.Gson;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UITable;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetUtil;
import com.technia.tvc.commons.io.output.ByteArrayOutputStream;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Page;
import matrix.util.StringList;

public class PGPerfCharsExportExcel {
	PGPerfCharsFetchDataColumns pgPerfCharsFetchDataColumns = new PGPerfCharsFetchDataColumns();
	PGPerfCharsExportProgramsUtil pgPerfCharsExportProgramsUtil = new PGPerfCharsExportProgramsUtil();

	private static final Logger logger = Logger.getLogger(PGPerfCharsExportExcel.class.getName());

	/**
	 * This method is used to export the Perf Chars Details into Excel
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return String
	 * @throws Exception
	 */

	public HashMap exportToExcel(Context context, String strJsonInput) throws Exception {

		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
		String strObjId = jsonInputData.getString("objectId");
		String strTableName = jsonInputData.getString("tableName");
		String tableColumnDetails = jsonInputData.getString("table Column Details");

		HSSFWorkbook workbook = null;
		byte[] bytes = null;
		HashMap retMap = new HashMap();
		String getSelectedKey = null;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjId)) {

				// Case-1 From DB
				/*
				 * Vector v = new Vector(1); v.add("all"); UITable table = new UITable();
				 * MapList UIcolumns = UITable.getColumns(context, strTableName, v);
				 * System.out.println("RGEXPORT------UITable--------UIcolumnsDB----------DB--"+
				 * UIcolumns);
				 */

				Map mpNexusfinal = new HashMap<>();
				Map mpsettings = new HashMap<>();
				mpNexusfinal.put("headerName", "Nexus Parameter List ID");
				mpNexusfinal.put("field", "Nexus Parameter List ID");
				mpNexusfinal.put("label", EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",
						context.getLocale(), "emxCPN.Characteristics.Nexus.Parameter.ListID"));

				mpNexusfinal.put("expression_businessobject", "attribute[pgNexusParameterListID]");
				mpNexusfinal.put("name", "Nexus Parameter List ID");
				mpNexusfinal.put("settings", mpsettings);
				mpsettings.put(PGPerfCharsConstants.SETTING_COLUMNTYPE, PGPerfCharsConstants.SETTING_PROGRAM);
				mpsettings.put("Editable", PGPerfCharsConstants.RANGE_VALUE_SMALL_FALSE);
				mpsettings.put("Export", PGPerfCharsConstants.RANGE_VALUE_SMALL_TRUE);
				mpsettings.put("Registered Suite", "CPN");
				mpsettings.put("function", "getNexusParametrListIDForRow");
				mpsettings.put("program", "pgDSOUtil");

				// case-2 Convert From Json Array to MapList
				Page pgPCTable = new Page("pgNexusPerfCharTables");
				pgPCTable.open(context);
				String strPageContent = pgPCTable.getContents(context);
				JsonObject jsonPageContent = PGWidgetUtil.getJsonFromJsonString(strPageContent);
				JsonArray jsonTableColumnArray = jsonPageContent.getJsonArray("pgVPDPerformanceCharacteristicTable");

				JSONArray array = new JSONArray(jsonTableColumnArray.toString());
				MapList UIcolumns = new MapList();
				for (int i = 0; i < array.length(); i++) {
					HashMap<String, Object> map = new Gson().fromJson(array.getJSONObject(i).toString(), HashMap.class);
					UIcolumns.add(map);
				}
				// Case-3
				/*
				 * int iJsonArraySize = jsonTableColumnArray.size(); System.out.println(
				 * "UIcolumns---FROM---Page-------iJsonArraySize------------"+iJsonArraySize);
				 * 
				 * for(int i=0; i<iJsonArraySize; i++) { JsonObject jsonColumnObj = (JsonObject)
				 * jsonTableColumnArray.get(i);
				 * System.out.println("-Dec17---UICOlumns--PAGE--jsonColumnObj----"+
				 * jsonColumnObj);
				 * 
				 * Map mp = new HashMap(); Map mpsettings = new HashMap();
				 * //mp.put("name",jsonColumnObj.containsKey("headerName") ?
				 * jsonColumnObj.getString("headerName") : "");
				 * mp.put("name",jsonColumnObj.containsKey("name") ?
				 * jsonColumnObj.getString("headerName") : "");
				 * mp.put("field",jsonColumnObj.containsKey("field") ?
				 * jsonColumnObj.getString("field") : "");
				 * mp.put("label",jsonColumnObj.containsKey("label") ?
				 * jsonColumnObj.getString("label") : "");
				 * mp.put("relationship",jsonColumnObj.containsKey("relationship") ?
				 * jsonColumnObj.getString("relationship") : "");
				 * mp.put("expression_relationship",jsonColumnObj.containsKey(
				 * "expression_relationship") ?
				 * jsonColumnObj.getString("expression_relationship") : "");
				 * mp.put("sorttype",jsonColumnObj.containsKey("sorttype") ?
				 * jsonColumnObj.getString("sorttype") : "");
				 * mp.put("businessobject",jsonColumnObj.containsKey("businessobject") ?
				 * jsonColumnObj.getString("businessobject") : "");
				 * mp.put("expression_businessobject",jsonColumnObj.containsKey(
				 * "expression_businessobject") ?
				 * jsonColumnObj.getString("expression_businessobject") : "");
				 * 
				 * //mp.put("name",jsonColumnObj.getString("name"));
				 * 
				 * JsonObject jsonsettings = jsonColumnObj.getJsonObject("settings");
				 * 
				 * mpsettings.put("Admin Type",jsonsettings.containsKey("Admin Type") ?
				 * jsonsettings.getString("Admin Type") : "");
				 * mpsettings.put("CPNFieldType",jsonsettings.containsKey("CPNFieldType") ?
				 * jsonsettings.getString("CPNFieldType") : "");
				 * mpsettings.put("Edit Access Function",jsonsettings.
				 * containsKey("Edit Access Function") ?
				 * jsonsettings.getString("Edit Access Function") : "");
				 * mpsettings.put("Edit Access Program",jsonsettings.
				 * containsKey("Edit Access Program") ?
				 * jsonsettings.getString("Edit Access Program") : "");
				 * mpsettings.put("Editable",jsonsettings.containsKey("Editable") ?
				 * jsonsettings.getString("Editable") : "");
				 * mpsettings.put("Export",jsonsettings.containsKey("Export") ?
				 * jsonsettings.getString("Export") : "");
				 * mpsettings.put("Field Type",jsonsettings.containsKey("Field Type") ?
				 * jsonsettings.getString("Field Type") : "");
				 * mpsettings.put("Input Type",jsonsettings.containsKey("Input Type") ?
				 * jsonsettings.getString("Input Type") : "");
				 * mpsettings.put("On Change Handler",jsonsettings.
				 * containsKey("On Change Handler") ?
				 * jsonsettings.getString("On Change Handler") : "");
				 * mpsettings.put("OnFocus Handler",jsonsettings.containsKey("OnFocus Handler")
				 * ? jsonsettings.getString("OnFocus Handler") : "");
				 * mpsettings.put("Registered Suite",jsonsettings.containsKey("Registered Suite"
				 * ) ? jsonsettings.getString("Registered Suite") : "");
				 * mpsettings.put("Reload Function",jsonsettings.containsKey("Reload Function")
				 * ? jsonsettings.getString("Reload Function") : "");
				 * mpsettings.put("Reload Program",jsonsettings.containsKey("Reload Program") ?
				 * jsonsettings.getString("Reload Program") : "");
				 * mpsettings.put("Show Clear Button",jsonsettings.
				 * containsKey("Show Clear Button") ?
				 * jsonsettings.getString("Show Clear Button") : "");
				 * mpsettings.put("Sortable",jsonsettings.containsKey("Sortable") ?
				 * jsonsettings.getString("Sortable") : "");
				 * mpsettings.put("ValidateOnApply",jsonsettings.containsKey("ValidateOnApply")
				 * ? jsonsettings.getString("ValidateOnApply") : "");
				 * mpsettings.put("grpHeader",jsonsettings.containsKey("grpHeader") ?
				 * jsonsettings.getString("grpHeader") : "");
				 * mpsettings.put("pgExportRangeFunction",jsonsettings.containsKey(
				 * "pgExportRangeFunction") ? jsonsettings.getString("pgExportRangeFunction") :
				 * ""); mpsettings.put("pgExportRangeProgram",jsonsettings.containsKey(
				 * "pgExportRangeProgram") ? jsonsettings.getString("pgExportRangeProgram") :
				 * "");
				 * mpsettings.put("pgPicklistName",jsonsettings.containsKey("pgPicklistName") ?
				 * jsonsettings.getString("pgPicklistName") : "");
				 * mpsettings.put("pgPicklistType",jsonsettings.containsKey("pgPicklistType") ?
				 * jsonsettings.getString("pgPicklistType") : "");
				 * 
				 * mpsettings.put("pgPicklistType",jsonsettings.containsKey("pgPicklistType") ?
				 * jsonsettings.getString("pgPicklistType") : "");
				 * mpsettings.put("pgPicklistType",jsonsettings.containsKey("pgPicklistType") ?
				 * jsonsettings.getString("pgPicklistType") : "");
				 * mpsettings.put("pgPicklistType",jsonsettings.containsKey("pgPicklistType") ?
				 * jsonsettings.getString("pgPicklistType") : "");
				 * mpsettings.put("pgPicklistType",jsonsettings.containsKey("pgPicklistType") ?
				 * jsonsettings.getString("pgPicklistType") : "");
				 * 
				 * System.out.println("RGEXPORT---Settings--UICOlumns----mpsettings----"+
				 * mpsettings); mp.put("settings",mpsettings); UIcolumns.add(mp);
				 * 
				 * } //case -3 //pgPCTable.close(context);
				 */

				StringList slColumnDetailsList = FrameworkUtil.split(tableColumnDetails, ",");

				for (int i = 0; i < UIcolumns.size(); i++) {
					Map columnMap = (Map) UIcolumns.get(i);
					String strColumnName = (String) columnMap.get("headerName"); // page --headerName // db name							
					String strHeaderColumnName = (String) columnMap.get("headerName");
					String strColumnLabel = (String) columnMap.get("label");

					if (!slColumnDetailsList.contains(strColumnName)) {

						UIcolumns.remove(i);
						i--;
					}
					String strLabel = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource",
							context.getLocale(), strColumnLabel);

					// columnMap.put("label", strHeaderColumnName); // with header Name from page  file
					columnMap.put("label", strLabel);

				}
				UIcolumns.add(mpNexusfinal);

				StringList slObjSelectsExp = new StringList();
				slObjSelectsExp.add(DomainConstants.SELECT_TYPE);
				slObjSelectsExp.add(DomainConstants.SELECT_NAME);
				slObjSelectsExp.add(DomainConstants.SELECT_POLICY);
				slObjSelectsExp.add(DomainConstants.SELECT_REVISION);
				slObjSelectsExp.add(DomainConstants.SELECT_ID);
				slObjSelectsExp.add(PGPerfCharsConstants.SELECT_ATTR_PG_CHARACTERISTIC);
				slObjSelectsExp.add(PGPerfCharsConstants.SELECT_ATTR_PG_NEXUS_PARAMETER_ID);

				StringList slRelSelectsExp = new StringList();
				slRelSelectsExp.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
				slRelSelectsExp.add(DomainConstants.SELECT_RELATIONSHIP_ID);
				slRelSelectsExp.add(DomainRelationship.SELECT_CURRENT);
				slRelSelectsExp.add(DomainRelationship.SELECT_ID);
				slRelSelectsExp.add(DomainRelationship.SELECT_CURRENT);
				slRelSelectsExp.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHAREDTABLECHARACTERISTICSEQUENCE);
				slRelSelectsExp.add(PGPerfCharsConstants.SELECT_ATTR_PG_INHERITANCE_TYPE);
				slRelSelectsExp.add(PGPerfCharsConstants.SELECT_ATTR_PG_INHERITED_FROM_PLATFORM);

				DomainObject doObj = DomainObject.newInstance(context,strObjId);
				Map strObjMap = doObj.getInfo(context, slObjSelectsExp);
				String strName = (String) strObjMap.get(DomainConstants.SELECT_NAME);
				String strRev = (String) strObjMap.get(DomainConstants.SELECT_REVISION);
				MapList mldetails = doObj.getRelatedObjects(context, PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC,
						PGPerfCharsConstants.TYPE_PG_PERFORMANCE_CHARACTERSTIC, slObjSelectsExp, slRelSelectsExp, false,
						true, (short) 1, null, null, 0);
				mldetails.sort(PGPerfCharsConstants.SELECT_ATTRIBUTE_SHARED_TABLE_CHAR_SEQUENCE, PGPerfCharsConstants.STRING_ASCENDING, PGPerfCharsConstants.INTEGER);
				for (int i = 0; i < mldetails.size(); i++) {
					Map mp = (Map) mldetails.get(i);
					String characteristic = (String) mp.get(PGPerfCharsConstants.SELECT_ATTR_PG_CHARACTERISTIC);
					mp.put("id[parent]", strObjId);
					mp.put("Characteristic New", characteristic);
					mp.put("objReadAccess", "TRUE");
				}
				Map mpSBTableData = new HashMap();
				Map reqMap = new HashMap();
				mpSBTableData.put("ObjectList", mldetails);
				mpSBTableData.put("columns", UIcolumns);
				Map paramList = new HashMap();
				paramList.put("selectedTable", strTableName); // From Payload
				paramList.put("parentOID", strObjId);
				paramList.put("table", strTableName); // From Payload
				paramList.put("objectId", strObjId);
				paramList.put("IsStructureCompare", "FALSE"); // IsStructureCompare
				paramList.put("reportFormat", "TRUE"); // reportFormat
				mpSBTableData.put("RequestMap", paramList);
				HashMap programMap = new HashMap();
				workbook = new HSSFWorkbook();
				programMap.put("workbook", workbook);
				programMap.put("sbTableData", mpSBTableData); // // From Payload
				programMap.put("objectId", strObjId);
				programMap.put("tableHeader", strName + "_rev_" + strRev + "_Structure_View"); // Sheet Name
				programMap.put("paramList", paramList);
				programMap.put("isPGExport", "true"); // isPGExport
				programMap.put("isExport", "true"); // isExport

				workbook = exportSBData(context, JPO.packArgs(programMap));
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				workbook.write(bos);
				bytes = bos.toByteArray();
				java.util.Date dt = new java.util.Date();
				java.text.SimpleDateFormat sd = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");
				StringBuilder strExpFileNameBuilder = new StringBuilder();
				strExpFileNameBuilder.append("Export_");
				strExpFileNameBuilder.append(strName);
				strExpFileNameBuilder.append("_");
				strExpFileNameBuilder.append(sd.format(dt));
				strExpFileNameBuilder.append(".xls");
				retMap.put("bytes", bytes);
				retMap.put("filename", strExpFileNameBuilder.toString());
			}
		} catch (Exception excep) {
			
		}
		return retMap;
	}

	/**
	 * This method exports all the data retrieved from Structure browser table into
	 * Excel workbook
	 * 
	 * @param context
	 * @param args    : program arguments should contain :
	 *                <ul>
	 *                <li>'workbook' : org.apache.poi.hssf.usermodel.HSSFWorkbook
	 *                object</li>
	 *                <li>'sbTableData' : Structure Browser data retrieved from
	 *                indentedTableBean</li>
	 *                <li>'objectId' : object id of the context Domain Object in SB
	 *                view</li>
	 *                <li>'tableHeader' : Table header retrieved from SB data</li>
	 *                </ul>
	 * @return
	 * @throws Exception
	 */
	public HSSFWorkbook exportSBData(Context context, String[] args) throws Exception {
		HSSFWorkbook workbook = null;
		String DATA_SHEET_NAME = getPageProperty(context, "pgImportExportConfigurations",
				"pgExport.Worksheet.DefaultDataSheetName");// default data sheet name

		// Case-1 From DB
		/*
		 * Vector v = new Vector(1); v.add("all"); UITable table = new UITable();
		 * MapList UIcolumns = UITable.getColumns(context,
		 * "pgVPDPerformanceCharacteristicTable", v);
		 */

		try {
			Map programMap = (HashMap) JPO.unpackArgs(args);
			workbook = (HSSFWorkbook) programMap.get("workbook");
			Map mSBTableData = (Map) programMap.get("sbTableData");
			Map mRequestMap = (Map) mSBTableData.get("RequestMap");
			MapList mlObjectList = (MapList) mSBTableData.get("ObjectList");
			String strSbOjectId = (String) programMap.get("objectId");
			String strTableHeader = (String) programMap.get("tableHeader");
			if (UIUtil.isNotNullAndNotEmpty(strTableHeader)) {
				DATA_SHEET_NAME = strTableHeader;
			}

			StringList slObjSelects = new StringList();
			StringList slRelSelects = new StringList();
			StringList slExportHiddenColumnList = new StringList();// added for Handle "Path" column in Export and
																	// Import of Performance Characteristics

			Map mAllColumnNameData = getAllColumnNameDataFromSB(mSBTableData);
			Map mObjIdsConnIdsMap = getAllObjIdConnIdFromSB(mlObjectList);
			Map mRangeColumns = new HashMap();

			StringList slColumnNameList = getAllColumnNameList(mSBTableData);
			HSSFSheet worksheet = workbook.createSheet(DATA_SHEET_NAME);

			MapList mlColumns = (MapList) mSBTableData.get("columns");

			Iterator itrColumns = mlColumns.iterator();
			Map mColumnNameBusExpression = new HashMap();
			Map mColumnNameRelExpression = new HashMap();
			Map mProgramTypeColumns = new HashMap();

			while (itrColumns.hasNext()) {
				Map mColumnData = (Map) itrColumns.next();
				String strColumnName = (String) mColumnData.get("name");
				String strRelSelectExpression = (String) mColumnData.get(PGPerfCharsConstants.EXPRESSION_REL);
				String strBusSelectExpression = (String) mColumnData.get(PGPerfCharsConstants.EXPRESSION_BUS);

				Map mColumnSettings = (Map) mColumnData.get("settings");
				String strColumnType = (String) mColumnSettings.get(PGPerfCharsConstants.SETTING_COLUMNTYPE);
				String strRangeProgram = (String) mColumnSettings.get(PGPerfCharsConstants.SETTING_RANGE_PROGRAM);
				String strRangeFunction = (String) mColumnSettings.get(PGPerfCharsConstants.SETTING_RANGE_FUNCTION);
				String strExpRangeProgram = (String) mColumnSettings
						.get(PGPerfCharsConstants.SETTING_PG_EXP_RANGE_PROGRAM);// Custom Setting to display ranges in
																				// Excel Column
				String strExpRangeFunction = (String) mColumnSettings
						.get(PGPerfCharsConstants.SETTING_PG_EXP_RANGE_FUNCTION);// Custom Setting to display ranges in
																					// Excel Column
				String strIsExcludedfromExport = (String) mColumnSettings
						.get(PGPerfCharsConstants.SETTING_PG_EXPORT_HIDDEN);// Custom Setting 'pgExportHidden=true' to
																			// hide a column from Exported Excel sheet

				// Excluding invalid Columns from Export
				if ((UIUtil.isNotNullAndNotEmpty(strIsExcludedfromExport)
						&& "true".equalsIgnoreCase(strIsExcludedfromExport))
						|| (PGPerfCharsConstants.COLUMNTYPE_ICON.equals(strColumnType))
						|| (PGPerfCharsConstants.COLUMNTYPE_SEPARATOR.equals(strColumnType))
						|| (PGPerfCharsConstants.COLUMNTYPE_IMAGE.equals(strColumnType))
						|| (PGPerfCharsConstants.COLUMNTYPE_CHECKBOX.equals(strColumnType))) {
					slColumnNameList.remove(strColumnName);
					continue;
				}

				// preparing a separate list of hidden columns
				if ((UIUtil.isNotNullAndNotEmpty(strIsExcludedfromExport)
						&& "true".equalsIgnoreCase(strIsExcludedfromExport))) {
					slExportHiddenColumnList.add(strColumnName);
				}
				// added for Handle "Path" column in Export and Import of Performance
				// Characteristics : End

				if ((UIUtil.isNotNullAndNotEmpty(strRangeProgram) && UIUtil.isNotNullAndNotEmpty(strRangeFunction))
						|| (UIUtil.isNotNullAndNotEmpty(strExpRangeProgram)
								&& UIUtil.isNotNullAndNotEmpty(strExpRangeFunction))) {
					// if column contains Range Function and Range Program
					mRangeColumns.put(strColumnName, mColumnData);
				}

				if ((PGPerfCharsConstants.COLUMNTYPE_PROGRAM_HTML_OUTPUT.equals(strColumnType)
						|| PGPerfCharsConstants.COLUMNTYPE_PROGRAM.equals(strColumnType))) {
					mProgramTypeColumns.put(strColumnName, mColumnData);
				} else {
					if (UIUtil.isNotNullAndNotEmpty(strRelSelectExpression)) {
						// first check if column select expr is on relationship
						slRelSelects.add(strRelSelectExpression);
						mColumnNameRelExpression.put(strColumnName, strRelSelectExpression);
					} else if (UIUtil.isNotNullAndNotEmpty(strBusSelectExpression)) {
						// second check if column select expr is on business object
						slObjSelects.add(strBusSelectExpression);
						mColumnNameBusExpression.put(strColumnName, strBusSelectExpression);
					}
				}
			}

			MapList mlObjectData = new MapList();
			Iterator itrObjectList = mObjIdsConnIdsMap.entrySet().iterator();

			while (itrObjectList.hasNext()) {
				Map.Entry mapObjIdConnId = (Map.Entry) itrObjectList.next();
				String strObjId = (String) mapObjIdConnId.getKey();
				String strConnectionId = (String) mapObjIdConnId.getValue();

				Map mInfoDataMap = new LinkedHashMap();
				if (slObjSelects.size() > 0) {

					DomainObject domObj = DomainObject.newInstance(context, strObjId);
					Map mapBusObjInfo = (Map) domObj.getInfo(context, slObjSelects);

					// getting bus select data
					Iterator itrColumnBusExpr = mColumnNameBusExpression.keySet().iterator();
					while (itrColumnBusExpr.hasNext()) {
						String strColumnName = (String) itrColumnBusExpr.next();
						String strBusExpr = (String) mColumnNameBusExpression.get(strColumnName);

						Object objValue = mapBusObjInfo.get(strBusExpr);
						mInfoDataMap.put(strColumnName, objValue);

					}

				}
				if (slRelSelects.size() > 0) {

					DomainRelationship domConnObj = DomainRelationship.newInstance(context, strConnectionId);
					Map mapConnObjInfo = (Map) domConnObj.getRelationshipData(context, slRelSelects);
					// getting rel select data
					Iterator itrColumnRelExpr = mColumnNameRelExpression.keySet().iterator();
					while (itrColumnRelExpr.hasNext()) {
						String strColumnName = (String) itrColumnRelExpr.next();
						String strRelExpr = (String) mColumnNameRelExpression.get(strColumnName);
						Object objValue = mapConnObjInfo.get(strRelExpr);
						mInfoDataMap.put(strColumnName, objValue);
					}
				}
				mlObjectData.add(mInfoDataMap);
			}
			// Generate Excel sheet containing Object info Data
			generateExcelForSBData(context, slColumnNameList, slExportHiddenColumnList, mlObjectData, workbook,
					worksheet, mObjIdsConnIdsMap.size(), mRangeColumns, mRequestMap, mAllColumnNameData,
					mProgramTypeColumns, mlObjectList);
			// added for Handle "Path" column in Export and Import of Performance
			// Characteristics : End

		} catch (Exception ex) {
			
			throw ex;
		}

		// return excel workbook
		return workbook;
	}

	/**
	 * This method generates the Excel file which contains the data exported from
	 * the Structure Browser.
	 * 
	 * @param context                  : matrix context
	 * @param slColumnNameList         : list of all the table column names
	 * @param slExportHiddenColumnList : list of all the hidden table column names
	 *                                 whose value should be skipped from export and
	 *                                 import
	 * @param mlObjectData             : MapList of Object Info based on SB object
	 *                                 list
	 * @param workbook                 : Excel workbook object to be generated
	 * @param worksheet                : data sheet present in the Excel workbook
	 * @param iObjectListSize          : Total number of Objects in SB data
	 * @param mRangeColumns            : Map of Column names and column data having
	 *                                 Range Values(drop down)
	 * @param mRequestMap              : SB request map
	 * @param mAllColumnNameData       : Map of Table Column names and corresponding
	 *                                 Column Data retrieved from SB Table
	 * @param mProgramTypeColumns      : Map of Table Column names having 'Column
	 *                                 Type' = 'program' OR 'programHTMLOutput'
	 * @param mlObjectList             : object list from SB data
	 * @throws Exception
	 */
	private void generateExcelForSBData(Context context, StringList slColumnNameList,
			StringList slExportHiddenColumnList, MapList mlObjectData, HSSFWorkbook workbook, HSSFSheet worksheet,
			int iObjectListSize, Map mRangeColumns, Map mRequestMap, Map mAllColumnNameData, Map mProgramTypeColumns,
			MapList mlObjectList) throws Exception {

		final int MAX_ROW_LIMIT_PER_SHEET = SpreadsheetVersion.EXCEL97.getMaxRows() - 1;// Max Rows supported by
																						// org.apache.poi is (64k-1) =
																						// 65535 for MS-EXCEL97

		String RANGE_CONSTRAINT_SHEET_NAME = getPageProperty(context, "pgImportExportConfigurations",
				"pgExport.Worksheet.HiddenRangesSheetName");
		int iTotalRows = 0;
		HashMap hmPickListMap = new HashMap();

		try {
			if (slColumnNameList == null || slColumnNameList.size() == 0) {
				return;
			}
			// Defining limit for row count per sheet
			iTotalRows = mlObjectData.size();
			int totalPages = iTotalRows / MAX_ROW_LIMIT_PER_SHEET;
			int remPages = iTotalRows % MAX_ROW_LIMIT_PER_SHEET;
			if (remPages > 0) {
				totalPages = totalPages + 1;
			}
			// Setting Column header row(Row=0) as Freeze Pane
			worksheet.createFreezePane(0, 1);
			worksheet.setDefaultRowHeight((short) 350);

			// Data cell style :start
			HSSFCellStyle cellDataStyle = workbook.createCellStyle();
			// Added to handle the data format to text for every cell in worksheet
			HSSFDataFormat dataformat = workbook.createDataFormat();
			HSSFCellStyle cellDataStyleText = workbook.createCellStyle();
			cellDataStyleText.setDataFormat(dataformat.getFormat("@"));
			Font fontDataColumn = workbook.createFont();
			fontDataColumn.setColor(HSSFFont.COLOR_NORMAL);
			fontDataColumn.setFontName(HSSFFont.FONT_ARIAL);
			fontDataColumn.setFontHeight((short) 200);
			cellDataStyle.setFont(fontDataColumn);
			cellDataStyle.setBorderBottom(BorderStyle.THIN);
			cellDataStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
			cellDataStyle.setBorderRight(BorderStyle.MEDIUM);
			cellDataStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
			// Data cell style :end

			if (iTotalRows > 0) {

				// Creating data rows,columns and putting data into Excel cells
				for (int sheetNumber = 1; sheetNumber <= totalPages; sheetNumber++) {
					// START : Creating Column Header Row (row=0)
					HSSFRow row1 = worksheet.createRow(0);
					int iTotalNoOfColumns = slColumnNameList.size();
					for (int iColumnIndex = 0; iColumnIndex < iTotalNoOfColumns; iColumnIndex++) {
						String strColumnName = (String) slColumnNameList.get(iColumnIndex);
						Map mColumnMap = (Map) mAllColumnNameData.get(strColumnName);
						String strColumnLabel = (String) mColumnMap.get("label");
						Map mColumnSettingMap = (Map) mColumnMap.get("settings");
						String isRequired = (String) mColumnSettingMap.get("Required");
						HSSFCell cellHeader = row1.createCell(iColumnIndex);
						cellHeader.setCellValue(strColumnLabel);
						// setting header cell style :start
						HSSFCellStyle cellHeaderStyle = workbook.createCellStyle();
						Font fontColumnHeader = workbook.createFont();
						fontColumnHeader.setBold(true);
						fontColumnHeader.setFontName(HSSFFont.FONT_ARIAL);
						fontColumnHeader.setFontHeight((short) 230);
						if (UIUtil.isNotNullAndNotEmpty(isRequired) && "true".equalsIgnoreCase(isRequired)) {
							// Style for Mandatory Fields
							fontColumnHeader.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());

						} else {
							// Style for Non-Mandatory Fields
							fontColumnHeader.setColor(Font.COLOR_NORMAL);
						}

						cellHeaderStyle.setFont(fontColumnHeader);
						cellHeaderStyle
								.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
						cellHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);
						cellHeaderStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
						cellHeaderStyle.setBorderRight(BorderStyle.MEDIUM);
						cellHeaderStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
						cellHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						cellHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
						cellHeader.setCellStyle(cellHeaderStyle);

						worksheet.autoSizeColumn(iColumnIndex);// column width auto size
						// Added to handle the data format to text for every cell in worksheet
						worksheet.setDefaultColumnStyle(iColumnIndex, cellDataStyleText);
						// setting header cell style :end

						hmPickListMap.put(strColumnName, iColumnIndex);
					}
					// END : Creating Column Header Row (row=0)

					// START : Creating Object Data Rows
					Map mCacheColumnProgramResults = null;
					HSSFRow dataRow = null;
					StringList slCellValues = null;

					int iTotalObjects = mlObjectData.size();
					for (int iRowIndex = 0; iRowIndex < iTotalObjects; iRowIndex++) {

						if (iRowIndex == 0) {
							// DB Call for Column Program is done only once for each row i.e. first row and
							// persist in a Cache
							mCacheColumnProgramResults = new HashMap();
						}

						Map mObjectInfo = (HashMap) mlObjectData.get(iRowIndex);

						// Create New Row in excel sheet
						dataRow = worksheet.createRow(iRowIndex + 1);

						for (int iColumnIndex = 0; iColumnIndex < iTotalNoOfColumns; iColumnIndex++) {

							HSSFCell cellData = dataRow.createCell(iColumnIndex);

							// setting data cell style
							cellData.setCellStyle(cellDataStyle);

							String strColumnName = (String) slColumnNameList.get(iColumnIndex);
							Object objCellData = mObjectInfo.get(strColumnName);

							if (UIUtil.isNotNullAndNotEmpty(strColumnName)) {
								String strCellValue = DomainConstants.EMPTY_STRING;
								;
								// setting up cell values
								if (objCellData == null) {
									// If no data found in ObjectInfo Map, check if the column is a Program Column
									if (mProgramTypeColumns.containsKey(strColumnName)) {
										if (iRowIndex == 0) {
											// DB Call for Column Program is done only once for first row and persist
											// results in a Cache

											Map mColumnData = (Map) mProgramTypeColumns.get(strColumnName);
											Map mColumnSettingMap = (Map) mColumnData.get("settings");
											if (mColumnSettingMap.size() > 0) {
												String strProgram = (String) mColumnSettingMap.get("program");
												String strFunction = (String) mColumnSettingMap.get("function");
												String strColumnLabel = (String) mColumnSettingMap.get("name");

												Map programArgs = new HashMap();
												programArgs.put("requestMap", mRequestMap);
												programArgs.put("columnMap", mColumnData);
												programArgs.put("objectList", mlObjectList);
												programArgs.put("paramList", mRequestMap);
												programArgs.put("isPGExport", "true");// Parameter to identify function
																						// call from PG Export
																						// Functionality

												// getting Column Program results from JPO's
												// Collection objCellValue = (Collection) JPO.invoke(context,
												// strProgram,
												// null, strFunction, JPO.packArgs(programArgs), Collection.class);
												Collection objCellValue = null;
												Map objectMap;
												switch (strColumnName) {
												case "Sequence":
													objCellValue =
													 pgPerfCharsExportProgramsUtil.pgGetSequenceColumnVal(context,
															 JPO.packArgs(programArgs));
													StringList slList1 = new StringList();
													Iterator<?> objectListItr1 = objCellValue.iterator();
													while (objectListItr1.hasNext()) {
														String strName = (String) objectListItr1.next();
														slList1.add(strName);
													}
													objCellValue = slList1;
													break;
												case "isDerived":
													objCellValue = pgPerfCharsFetchDataColumns
															.getDerivedPathForRow(context, programArgs);
													StringList slList = new StringList();
													for (int j = 0; j < objCellValue.size(); j++) {
														slList.add((String) ((Map) ((MapList) objCellValue).get(j))
																.get(DomainConstants.SELECT_NAME));
													}
													objCellValue = slList;

													break;
												case "Path":
													// objCellValue =
													// pgPerfCharsFetchDataColumns.displayConfOptionPath(context,
													// programArgs);
													break;
												case "Master Part":
													// Collection objCellValue =
													// pgPerfCharsFetchDataColumns.displayConfOptionPath(context,
													// programArgs);
													break;
												case "pgTestMethod":
													objCellValue = PGPerfCharsExportProgramsUtil.pgGetTestMethods(context,
															JPO.packArgs(programArgs));

													StringList slList5 = new StringList();
													Iterator<?> objectListItr = objCellValue.iterator();
													while (objectListItr.hasNext()) {
														String strName = (String) objectListItr.next();
														slList5.add(strName);
													}

													objCellValue = slList5;
													break;
												case "Test Method Name":
													objCellValue = PGPerfCharsExportProgramsUtil.pgGetTestMethods(context,
															JPO.packArgs(programArgs));

													StringList slList6 = new StringList();
													for (int j = 0; j < objCellValue.size(); j++) {
														slList6.add((String) ((Map) ((MapList) objCellValue).get(j))
																.get(DomainConstants.SELECT_NAME));
													}
													objCellValue = slList6;
													break;
												case "Characteristic":
													objCellValue = pgPerfCharsFetchDataColumns
															.pgGetCharacteristicColumnVal(context, programArgs);
													break;
												case "Nexus Parameter List ID":
													objCellValue = PGPerfCharsExportProgramsUtil
															.getNexusParametrListIDForRow(context, programArgs);
													break;
												case "Characteristic New":
													objCellValue = pgPerfCharsFetchDataColumns
															.pgGetCharacteristicColumnVal(context, programArgs);
													break;
												case "Test Method Reference Document Name":
													objCellValue = PGPerfCharsExportProgramsUtil
															.pgGetReferenceDocGCAS(context, JPO.packArgs(programArgs));

													StringList slList8 = new StringList();
													Iterator<?> objectItr = objCellValue.iterator();
													while (objectItr.hasNext()) {
														String strName = (String) objectItr.next();
														slList8.add(strName);
													}
													objCellValue = slList8;
													break;

												case "Common Performance Specifications":
													// Collection objCellValue =
													// pgPerfCharsFetchDataColumns.pgGetCommonPerformanceSpecsColVal(context,
													// programArgs);
													// Do Nothing here
													break;
												case "DerivedTitle":
													objCellValue = pgPerfCharsFetchDataColumns
															.getDerivedTitleForRow(context, programArgs);
													break;
												}

												// Method method =
												// PGPerfCharsFetchDataColumns.class.getDeclaredMethod(strFunction);
												// Collection objCellValue = (Collection)
												// method.invoke(context,programArgs);

												// Class<?> base = Class.forName("PGPerfCharsFetchDataColumns");
												// Class<?> base =
												// Class.forName("com.pg.widgets.nexusPerformanceChars.PGPerfCharsFetchDataColumns");
												// Method serverMethod = base.getMethod(strFunction, HashMap.class);
												// Collection objCellValue = (Collection)
												// serverMethod.invoke(base.newInstance(), context,programArgs);

												/*
												 * Collection objCellValue = null; Method[] methods =
												 * PGPerfCharsFetchDataColumns.class.getMethods(); for (Method m :
												 * methods){ System.out.println("1210::::::::::method::m:::"+m); if
												 * (m.getName().equals(strFunction)){ objCellValue = (Collection)
												 * m.invoke(m.newInstance(), context,programArgs); break; } }
												 */
												// Modified for P&G 2018x Upgrade - START
												slCellValues = new StringList();
												if (objCellValue != null)
													slCellValues.addAll(objCellValue);
												// Vector vectCellValue = (Vector)objCellValue;
												if (iRowIndex < slCellValues.size()) {
													// Get Column Program result for the current Row object
													strCellValue = (String) slCellValues.get(iRowIndex);
												} else {
													strCellValue = DomainConstants.EMPTY_STRING;
												}
												mCacheColumnProgramResults.put(strColumnName, slCellValues);
												// Modified for P&G 2018x Upgrade - END
											}
										} else if (mCacheColumnProgramResults != null
												&& mCacheColumnProgramResults.size() > 0) {
											
											// Vector vectCellValue =
											// (Vector)mCacheColumnProgramResults.get(strColumnName);
											StringList slCellValue = (StringList) mCacheColumnProgramResults
													.get(strColumnName);
											if (iRowIndex < slCellValue.size()) {
												// Get Column Program result for the current Row object
												strCellValue = (String) slCellValue.get(iRowIndex);
												// Modified for P&G 2018x Upgrade - END
											} else {
												strCellValue = DomainConstants.EMPTY_STRING;
											}
										}
									} else {
										strCellValue = DomainConstants.EMPTY_STRING;// Empty value
									}
								} else {
									// added for Handle "Path" column in Export and Import of Performance
									// Characteristics
									if (slExportHiddenColumnList.contains(strColumnName)) {
										strCellValue = DomainConstants.EMPTY_STRING;
									} else if (objCellData instanceof String) {
										strCellValue = (String) objCellData;
									} else if (objCellData instanceof StringList) {
										strCellValue = getStringListAsString((StringList) objCellData);
									}

								}

								// setting cell value
								cellData.setCellValue(strCellValue);
							}
						}
					}
				}
				// END : Creating Object Data Rows

			} else {
				// START : If no objects found, Create empty template with only Column Header
				// Row (row=0)
				HSSFRow row1 = worksheet.createRow(0);
				int iTotalNoOfColumns = slColumnNameList.size();
				for (int iColumnIndex = 0; iColumnIndex < iTotalNoOfColumns; iColumnIndex++) {
					String strColumnName = (String) slColumnNameList.get(iColumnIndex);
					Map mColumnMap = (Map) mAllColumnNameData.get(strColumnName);
					String strColumnLabel = (String) mColumnMap.get("label");
					Map mColumnSettingMap = (Map) mColumnMap.get("settings");
					String isRequired = (String) mColumnSettingMap.get("Required");
					HSSFCell cellHeader = row1.createCell(iColumnIndex);
					cellHeader.setCellValue(strColumnLabel);
					// setting header cell style :start
					HSSFCellStyle cellHeaderStyle = workbook.createCellStyle();
					Font fontColumnHeader = workbook.createFont();
					fontColumnHeader.setBold(true);
					fontColumnHeader.setFontName(HSSFFont.FONT_ARIAL);
					fontColumnHeader.setFontHeight((short) 230);
					if (UIUtil.isNotNullAndNotEmpty(isRequired) && "true".equalsIgnoreCase(isRequired)) {
						// Style for Mandatory Fields
						fontColumnHeader.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
					} else {
						// Style for Non-Mandatory Fields
						fontColumnHeader.setColor(Font.COLOR_NORMAL);
					}
					cellHeaderStyle.setFont(fontColumnHeader);
					cellHeaderStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
					cellHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);
					cellHeaderStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
					cellHeaderStyle.setBorderRight(BorderStyle.MEDIUM);
					cellHeaderStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.GREY_50_PERCENT.getIndex());
					cellHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cellHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
					cellHeader.setCellStyle(cellHeaderStyle);

					worksheet.autoSizeColumn(iColumnIndex);// column width auto size

					// Added to handle the data format to text for every cell in worksheet
					worksheet.setDefaultColumnStyle(iColumnIndex, cellDataStyleText);
					// setting header cell style :end

					hmPickListMap.put(strColumnName, iColumnIndex);
				}
				// END : If no objects found, Create empty template with only Column Header Row
				// (row=0)
			}

			// START : Adding Cell Range sheets to display Dropdown in Picklist columns
			Iterator iteratorPickList = hmPickListMap.entrySet().iterator();
			int iListConstraintColumnCount = 0;

			// Defining Separate sheet for Column Ranges
			HSSFSheet hiddenListConstraintSheet = workbook.createSheet(RANGE_CONSTRAINT_SHEET_NAME);
			// iListConstraintSheetCount++;

			while (iteratorPickList.hasNext()) {

				Map.Entry mapPickList = (Map.Entry) iteratorPickList.next();
				String strColumnName = (String) mapPickList.getKey();
				int iColumnIndex = (Integer) mapPickList.getValue();
				Vector collPicklistRanges = new Vector();
				// Added for 2018x Upgrade STARTS
				StringList slcollPicklistRanges = new StringList();
				// Added for 2018x Upgrade ENDS
//Range Prog Func
				if (UIUtil.isNotNullAndNotEmpty(strColumnName) && mRangeColumns.containsKey(strColumnName)) {
					Map mColumnMap = (Map) mRangeColumns.get(strColumnName);
					Map mColumnSettingMap = (Map) mColumnMap.get("settings");
					if (mColumnSettingMap.size() > 0) {
						String strProgram = (String) mColumnSettingMap.get(PGPerfCharsConstants.SETTING_RANGE_PROGRAM);
						String strFunction = (String) mColumnSettingMap
								.get(PGPerfCharsConstants.SETTING_RANGE_FUNCTION);

						if (UIUtil.isNullOrEmpty(strProgram) || UIUtil.isNullOrEmpty(strFunction)) {
							strProgram = (String) mColumnSettingMap
									.get(PGPerfCharsConstants.SETTING_PG_EXP_RANGE_PROGRAM);// Custom Setting to
																							// explicitly display ranges
																							// in Excel Column
							strFunction = (String) mColumnSettingMap
									.get(PGPerfCharsConstants.SETTING_PG_EXP_RANGE_FUNCTION);// Custom Setting to
																								// explicitly display
																								// ranges in Excel
																								// Column
						}

						Map programArgs = new HashMap();
						programArgs.put("requestMap", mRequestMap);
						programArgs.put("columnMap", mColumnMap);
						programArgs.put("isPGExport", "true");// Parameter to identify function call from PG Export
																// Functionality

						// Range Programs From DB Jpo's
						// Object objFieldRangeMap = (Object) JPO.invoke(context, strProgram, null,
						// strFunction,
						// JPO.packArgs(programArgs), Object.class);

						Object objFieldRangeMap = null;
						switch (strColumnName) {
						case "Test Method Logic":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getTMLogicRanges(context,
									JPO.packArgs(programArgs));
							break;
						case "Report Type":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "Characteristic New":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMap(context,
									JPO.packArgs(programArgs));
							break;
						case "Method Origin":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "Action Required":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "Unit Of Measure":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "Plant Testing":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "Change":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "RetestingUnitofMeasure":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "Characteristic Specifics":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMap(context,
									JPO.packArgs(programArgs));
							break;
						case "Criticality Factor":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						case "Test Group":
							objFieldRangeMap = pgPerfCharsExportProgramsUtil.getPicklistRangeMapForDirectAttr(context,
									JPO.packArgs(programArgs));
							break;
						}

						if (objFieldRangeMap instanceof Map) {
							Map mpFieldRangeMap = (Map) objFieldRangeMap;
							// Added for 2018x Upgrade STARTS
							slcollPicklistRanges = new StringList();
							slcollPicklistRanges = (StringList) mpFieldRangeMap.get("field_display_choices");
							// collPicklistRanges =
							// (StringList)mpFieldRangeMap.get("field_display_choices");
							collPicklistRanges.addAll(slcollPicklistRanges);
							// Added for 2018x Upgrade ENDS

						} else if (objFieldRangeMap instanceof Collection) {
							// Modified for P&G 2018x Upgrade - START
							collPicklistRanges.addAll((Collection) objFieldRangeMap);
							// Modified for P&G 2018x Upgrade - END
						}
					}

					String[] array = new String[collPicklistRanges.size()];
					collPicklistRanges.toArray(array);

					// putting in each range values per Column in the hidden sheet
					for (int iColumnRanges = 0; iColumnRanges < array.length; iColumnRanges++) {
						String name = array[iColumnRanges];
						HSSFRow rowRanges = hiddenListConstraintSheet.getRow(iColumnRanges);
						if (rowRanges == null) {
							rowRanges = hiddenListConstraintSheet.createRow(iColumnRanges);
						}
						HSSFCell cell = rowRanges.createCell(iListConstraintColumnCount);
						cell.setCellValue(name);
					}
					if (array.length > 0) {
						// Start : Creating Ranges for a column
						HSSFName namedRange = workbook.createName();
						String strNameOfNamedRange = new StringBuilder(RANGE_CONSTRAINT_SHEET_NAME)
								.append(iListConstraintColumnCount).toString();
						// Name of the Named Range should be unique for each column
						namedRange.setNameName(strNameOfNamedRange);
						String strColumnIndexAplhabet = CellReference.convertNumToColString(iListConstraintColumnCount);
						// Formula to define column ranges
						String strNamedRangeFormula = new StringBuilder(RANGE_CONSTRAINT_SHEET_NAME).append("!$")
								.append(strColumnIndexAplhabet).append("$1:$").append(strColumnIndexAplhabet)
								.append("$").append(array.length).toString();// formula example : "Ranges!$A1:$A50"

						namedRange.setRefersToFormula(strNamedRangeFormula);

						DVConstraint dvConstraint = DVConstraint
								.createFormulaListConstraint(RANGE_CONSTRAINT_SHEET_NAME + iListConstraintColumnCount);
						// End : Creating Ranges for a column

						// START : applying ranges to Data sheet columns
						CellRangeAddressList addressList = new CellRangeAddressList(1, MAX_ROW_LIMIT_PER_SHEET,
								iColumnIndex, iColumnIndex);// params : int firstRow, int lastRow, int firstCol, int
															// lastCol
						DataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
						dataValidation.setSuppressDropDownArrow(false);
						worksheet.addValidationData(dataValidation);
					}
					// END : applying ranges to Data sheet columns

					iListConstraintColumnCount++;// increase column count for storing the ranges
				}
			}
			// Hide Range sheet containing Pick List for Drop down columns
			workbook.setSheetHidden(1, true);
			// END : Adding Cell Range sheets to display Drop down in Picklist columns

		} catch (Exception ex) {
			
			throw ex;
		}
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

	/**
	 * This method returns Map of all the Table Column names and corresponding
	 * Column Data retrieved from Structure Browser Table
	 * 
	 * @param mSBTableData : Structure Browser Table data map
	 * @return <code>Map</code> as described above
	 * @throws Exception
	 */
	private Map getAllColumnNameDataFromSB(Map mSBTableData) throws Exception {

		Map mAllColumnList = new LinkedHashMap();
		try {
			MapList mlColumns = (MapList) mSBTableData.get("columns");
			Iterator itrColumns = mlColumns.iterator();
			while (itrColumns.hasNext()) {
				Map mColumnData = (Map) itrColumns.next();
				String strColumnName = (String) mColumnData.get("name");
				mAllColumnList.put(strColumnName, mColumnData);
			}
		} catch (Exception ex) {
			
			throw ex;
		}
		return mAllColumnList;
	}

	/**
	 * This is a utility method to get all the elements in a <code>StringList</code>
	 * as a comma separated <code>String</code>
	 * 
	 * @param slValues
	 * @return <code>String</code> as described above
	 * @throws Exception
	 */
	private String getStringListAsString(StringList slValues) throws Exception {

		StringBuilder sbOut = new StringBuilder();
		try {
			if (slValues != null) {
				int iListSize = slValues.size();
				for (int i = 0; i < iListSize; i++) {
					sbOut.append(slValues.get(i));
					if (i < (iListSize - 1)) {
						sbOut.append(",");
					}
				}
			} else {
				return DomainConstants.EMPTY_STRING;
			}
		} catch (Exception ex) {
			
			throw ex;
		}
		return sbOut.toString();
	}

	/**
	 * This method returns Map of all the object ids as key and connection id as
	 * corresponding value retrieved from Structure Browser Table Data
	 * 
	 * @param mlObjectList : Object MapList retrieved from SB Table Data
	 * @return <code>Map</code> as described above
	 * @throws Exception
	 */
	private Map getAllObjIdConnIdFromSB(MapList mlObjectList) throws Exception {

		Map mObjectIds = new LinkedHashMap();
		try {
			Iterator itrObjectList = mlObjectList.iterator();
			while (itrObjectList.hasNext()) {
				Map mObjectMap = (Map) itrObjectList.next();
				String strObjId = (String) mObjectMap.get(DomainConstants.SELECT_ID);
				String strConnectionId = (String) mObjectMap.get(DomainRelationship.SELECT_ID);
				mObjectIds.put(strObjId, strConnectionId);
			}
		} catch (Exception ex) {
			
			throw ex;
		}
		return mObjectIds;
	}

	/**
	 * This method returns StringList of all the Table Column names retrieved from
	 * Structure Browser Table
	 * 
	 * @param mSBTableData : Structure Browser Table data map
	 * @return <code>StringList</code> as described above
	 * @throws Exception
	 */
	private StringList getAllColumnNameList(Map mSBTableData) throws Exception {

		StringList slAllColumnList = new StringList();
		try {
			MapList mlColumns = (MapList) mSBTableData.get("columns");
			Iterator itrColumns = mlColumns.iterator();
			while (itrColumns.hasNext()) {
				Map mColumnData = (Map) itrColumns.next();
				String strColumnName = (String) mColumnData.get("name");
				slAllColumnList.add(strColumnName);
			}
		} catch (Exception ex) {
			
			throw ex;
		}
		return slAllColumnList;
	}

}