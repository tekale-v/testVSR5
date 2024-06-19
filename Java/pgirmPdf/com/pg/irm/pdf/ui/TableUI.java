/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: TableUI
   Purpose: JAVA class created to get table data.
 **/
package com.pg.irm.pdf.ui;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.UOMUtil;
import com.matrixone.apps.framework.ui.UICache;
import com.matrixone.apps.framework.ui.UIComponent;
import com.matrixone.apps.framework.ui.UIForm;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UITable;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.irm.pdf.PDFConstants;
import com.pg.irm.pdf.util.StringHelper;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.AttributeType;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import matrix.util.StringList;
import java.util.logging.Logger;
import java.util.logging.Level;


public class TableUI implements PDFConstants, Serializable {
	private static final long serialVersionUID = -4891962759711072338L;
	//Added by IRM team in 2018x.6 for the Defect #43129 Starts
	private transient Logger logger = Logger.getLogger(this.getClass().getName());

	//Added by IRM team in 2018x.6 for the Defect #43129 Ends

	/**
	 * @Desc Method to get table data in map format
	 * @param _ctx
	 * @param _appCtx      - jsp ServletContext
	 * @param _pageCtx     -jsp PageContext
	 * @param _servletReq  -jsp page request HttpServletRequest
	 * @param _timeStamp
	 * @param _languageStr
	 * @param programMap
	 * @param objectId     -object id
	 * @return table data in map format
	 * @throws Exception 
	 */
	public static Map getTableDataMap(
			Context _ctx, 
			ServletContext _appCtx, 
			PageContext _pageCtx,
			HttpServletRequest _servletReq, 
			String _timeStamp, 
			String _languageStr, 
			Map tablesMap, 
			String objectId) throws Exception {
		Map tableDataMap = new HashMap();
		try {
			if (tablesMap != null) {
				Map tableMap = null;
				HashMap pageMap = UINavigatorUtil.getRequestParameterMap(_pageCtx);
				for (Object key : tablesMap.keySet()) {
					tableMap = (Map)tablesMap.get(key);
					String table = (String) tableMap.get(CONSTANT_NAME);
					String program = (String) tableMap.get(CONSTANT_PROGRAM);
					pageMap.put(CONSTANT_TABLE, table);
					pageMap.put(CONSTANT_OBJECT_ID, objectId);
					pageMap.put(CONSTANT_PROGRAM, program);
					pageMap.put(CONST_MODE, CONST_VIEW_MODE);
					//Added by IRM team in 2018x.6 for the Defect #43129 Starts
					pageMap.put(CONST_DYNAMIC, KEY_NO_VALUE);
					//Added by IRM team in 2018x.6 for the Defect #43129 Ends
					MapList objectList = getTableData(
							_ctx, 
							_appCtx, 
							_pageCtx, 
							_servletReq, 
							_timeStamp,
							_languageStr, 
							table, 
							program, 
							pageMap);
					//Added by IRM team in 2018x.6 for the Defect #43129 Starts
					TableUI tableUI = new TableUI();
					tableUI.filterTableData(_ctx, tableMap, objectList);
					//Added by IRM team in 2018x.6 for the Defect #43129 Ends
					
					
					tableDataMap.put(key.toString(), objectList);

				}
			}
		} catch (Exception e) {
			throw e;
		}
		return tableDataMap;
	}

	//Added by IRM team in 2018x.6 for the Defect #43129 Starts	
	/**
	 * Check for filter Table data
	 * @param cotext
	 * @param tableMap
	 * @param objectList
	 * @return void
	 */
	public void filterTableData(Context context, Map tableMap, MapList objectList) throws FrameworkException {
		try {
			if (null != objectList && !objectList.isEmpty()) {
				String table = (String) tableMap.get(CONSTANT_NAME);
				if ("pgAPPDocumentSummary".equals(table)) {
					Iterator iterator = objectList.iterator();
					Map objectMap;
					while (iterator.hasNext()) {
						objectMap = (Map) iterator.next();
						String objectId = (String) objectMap.get(DomainConstants.SELECT_ID);
						if (UIUtil.isNotNullAndNotEmpty(objectId)) {
							DomainObject dObj = DomainObject.newInstance(context, objectId);
							String developmentType = dObj.getInfo(context, SELECT_ATTRIBUTE_PGPKGDEVELOPMENTTYPE);
							developmentType = UIUtil.isNullOrEmpty(developmentType)?DomainConstants.EMPTY_STRING:developmentType;
							objectMap.put("Attr_pgPKGDevelopmentType", developmentType);
						} else {
							objectMap.put("Attr_pgPKGDevelopmentType", DomainConstants.EMPTY_STRING);
						}
					}
				}
				// Added by (Sogeti) for Requirement (42813) 2018x.6 May CW 2022 Release - Start
				if ("pgGPSAssesmentInputsCountries".equals(table)) {
					getCountriesTableAdditionalColumns(context, tableMap, objectList);
				} //Added by (Sogeti) for Requirement (42813) 2018x.6 May CW 2022 Release - End
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * Added by (Sogeti) for Requirement (42813) 2018x.6 May CW 2022 Release
	 * Method to get additional column details of Countries table view.
	 * @param context
	 * @param tableMap
	 * @param objectList
	 * @throws MatrixException
	 */
	public void getCountriesTableAdditionalColumns(Context context, Map tableMap, MapList objectList) throws MatrixException {
		String tableName = (String) tableMap.get(CONSTANT_NAME);
		MapList columnList = new MapList();
		HashMap tableConfigMap = UICache.getTable(context, tableName, false);
		columnList = (MapList) tableConfigMap.get("columns");
		Map<Object, Object> columnParamMap = new HashMap<>();
		columnParamMap.put("objectList", objectList);
		if (null != columnList && !columnList.isEmpty()) {
			Map<Object, Object> columnMap;
			Map<Object, Object> objectMap;
			Map<Object, Object> settingMap;
			Vector columnVector;
			String columnValue;
			for (int i = 0; i < columnList.size(); i++) {
				columnMap = (Map<Object, Object>) columnList.get(i);
				settingMap = (Map<Object, Object>) columnMap.get("settings");
				if ("program".equals((String) settingMap.get("Column Type"))) {
					columnParamMap.put("columnMap", columnMap);

					// invoke the column program/function explicitly.
					columnVector = JPO.invoke(
							context,
							(String) settingMap.get("program"),
							null,
							(String) settingMap.get("function"),
							JPO.packArgs(columnParamMap),
							Vector.class);

					if(null != columnVector && !columnVector.isEmpty()) {
						// iterate column result vector and put into final maplist.
						for (int j = 0; j < objectList.size(); j++) {
							objectMap = (Map<Object, Object>) objectList.get(j);
							columnValue = (String) columnVector.get(j);
							columnValue = UIUtil.isNullOrEmpty(columnValue)?DomainConstants.EMPTY_STRING:columnValue;
							objectMap.put((String) columnMap.get("name"), columnValue);
						}
					}
				}
			}
		}
		getCountriesTableSpecialColumns(context, objectList);
	}
	
	/**
	 * @param context
	 * @param objectList
	 * @throws FrameworkException
	 */
	private void getCountriesTableSpecialColumns(Context context, MapList objectList) throws FrameworkException {
		final StringList columnNameList = StringUtil.split(PDFConstants.COUNTRIES_TABLE_RELATIONSHIP_COULMN_NAMES, pgV3Constants.SYMBOL_COMMA);
		StringList columnSelectList = StringUtil.split(PDFConstants.COUNTRIES_TABLE_RELATIONSHIP_COULMN_SELECTS, pgV3Constants.SYMBOL_COMMA);
		StringList relSelects = new StringList(columnSelectList);
		relSelects.addElement(DomainRelationship.SELECT_RELATIONSHIP_ID);

		final Iterator iterator = objectList.iterator();
		Map<Object, Object> objectMap;
		Map<Object, Object> relInfoMap;
		String relID;

		StringList relOIDs = new StringList();
		while (iterator.hasNext()) {
			objectMap = (Map<Object, Object>) iterator.next();
			relOIDs.add((String) objectMap.get(DomainRelationship.SELECT_RELATIONSHIP_ID));
		}
		MapList infoList = DomainRelationship.getInfo(context, relOIDs.toArray(new String[relOIDs.size()]), relSelects);
		Map<String, Map<Object, Object>> relMap = new HashMap<>();

		// maplist to flat map.
		for (int i = 0; i < infoList.size(); i++) {
			objectMap = (Map<Object, Object>) infoList.get(i);
			relMap.put((String) objectMap.get(DomainRelationship.SELECT_RELATIONSHIP_ID), objectMap);
		}

		// put rel key/value in final list
		String columnVal;
		for (int i = 0; i < objectList.size(); i++) {
			objectMap = (Map<Object, Object>) objectList.get(i);
			relID = (String) objectMap.get(DomainRelationship.SELECT_RELATIONSHIP_ID);
			if (relMap.containsKey(relID)) {
				relInfoMap = relMap.get(relID);
				for (int j = 0; j < columnNameList.size(); j++) {
					columnVal = (String)relInfoMap.get(columnSelectList.get(j));
					columnVal = UIUtil.isNullOrEmpty(columnVal)?DomainConstants.EMPTY_STRING:columnVal;
					objectMap.put(columnNameList.get(j), columnVal);
				}
			}
		}
	}


	/**
	 * @Desc Method to get table data in MapList format
	 * @param _ctx
	 * @param _appCtx      - jsp ServletContext
	 * @param _pageCtx     -jsp PageContext
	 * @param _servletReq  -jsp page request HttpServletRequest
	 * @param _timeStamp   -
	 * @param _languageStr
	 * @param table
	 * @param program
	 * @param programMap
	 * @param objectId     -object id
	 * @return table data in MapList format
	 */
	public static MapList getTableData(Context _ctx, ServletContext _appCtx, PageContext _pageCtx,
			HttpServletRequest _servletReq, String _timeStamp, String _languageStr, String table, String program,
			HashMap pageMap) throws Exception {

		MapList columnValueList = new MapList();
	
		try {
			
			UITable tableViewBean = new UITable();
			HashMap<?,?> tableData = tableViewBean.getTableData(_timeStamp);
			tableViewBean.setTableData(_ctx, _pageCtx, pageMap, _timeStamp, PersonUtil.getAssignments(_ctx));
			tableData = tableViewBean.getTableData(_timeStamp);
			HashMap requestMap = tableViewBean.getRequestMap(tableData);
			requestMap.put(CONST_UI_TYPE, CONST_UI_TABLE);
			HashMap tableControlMap = tableViewBean.getControlMap(tableData);
			String  header = tableViewBean.getPageHeader(tableControlMap);
			if (UIUtil.isNullOrEmpty(header)) {
				header = FrameworkProperties.getProperty("emxFramework.Common.defaultPageTitle");
			}
			boolean bDataLoaded = ((Boolean) tableControlMap.get(CONST_DATA_LOADED)).booleanValue();

			String strTableName = (String) requestMap.get(CONSTANT_TABLE);
			if (UIUtil.isNotNullAndNotEmpty(strTableName))
				tableViewBean.setCurrentTable(_ctx, strTableName);

			// Only set table data if it is not yet set
			if (!bDataLoaded) {
				// set data objects in Table bean
				tableViewBean.setTableObjects(_ctx, _timeStamp);

				// Sort the table data
				String sortColumnName = _servletReq.getParameter(CONST_SORT_COLUMN_NAME);

				if (sortColumnName != null && sortColumnName.trim().length() > 0
						&& (!(sortColumnName.equals(CONSTANT_NULL)))) {
					tableViewBean.sortObjects(_ctx, _timeStamp);
				}
			}

			tableData = tableViewBean.getTableData(_timeStamp);
			tableControlMap = tableViewBean.getControlMap(tableData);
			requestMap = tableViewBean.getRequestMap(tableData);

			MapList relBusObjList = tableViewBean.getFilteredObjectList(tableData);
			int ObjectlistSize = ((MapList) tableData.get(CONST_OBJECT_LIST)).size();
			
			// business object list for current page
			MapList relBusObjPageList = new MapList();

			// HashMap tableMap = new HashMap();
			MapList columns = tableViewBean.getColumns(tableData);
			int noOfColumns = columns.size();

			RelationshipWithSelectList rwsl = null;
			BusinessObjectWithSelectList bwsl = null;

			boolean columnSpan = false;

			// if selectType is program - get column result by invoking the specified JPO
			Vector programResult[] = new Vector[noOfColumns];

			// if selectType is checkbox - get column result by invoking the specified JPO
			Vector checkboxAccess[] = new Vector[noOfColumns];

			// if selectType is image - get column result by invoking the specified JPO
			Vector imageResult[] = new Vector[noOfColumns];

			// if column type is File - get column result by calling getColumnValuesMap
			Vector fileResult[] = new Vector[noOfColumns];

			// Added for Overriden type Icons
			MapList iconResults[] = new MapList[noOfColumns];

			boolean reportMode = true;
			if (relBusObjList != null && relBusObjList.size() > 0) {
				if (reportMode) {
					relBusObjPageList = relBusObjList;
				}
				HashMap columnValuesMap = tableViewBean.getColumnValuesMap(_ctx, _appCtx, relBusObjPageList, tableData);
				bwsl = (BusinessObjectWithSelectList) columnValuesMap.get(CONST_BUSINESS_OBJECT);
				rwsl = (RelationshipWithSelectList) columnValuesMap.get(CONST_RELATIONSHIP);
				programResult = (Vector[]) columnValuesMap.get(CONST_PROGRAM);
				fileResult = (Vector[]) columnValuesMap.get(CONST_FILE);
				imageResult = (Vector[]) columnValuesMap.get(CONST_IMAGE);
				checkboxAccess = (Vector[]) columnValuesMap.get(CONST_CHECK_BOX);
				iconResults = (MapList[]) columnValuesMap.get(CONST_ICONS);
			}
			int relObjSize = relBusObjPageList.size();
			for (int i = 0; i < relObjSize; i++) {
				Map elementMap = (Map) relBusObjPageList.get(i);
				String elementOID = (String) elementMap.get(SELECT_ID);
				String elementRELID = (String) elementMap.get(SELECT_RELATIONSHIP_ID);
				HashMap columnValueMap = new HashMap();
				//Added by IRM team in 2018x.6 for the Defect #43129 Starts
				columnValueMap.put(SELECT_ID,elementOID);
				columnValueMap.put(SELECT_RELATIONSHIP_ID,elementRELID);
				//Added by IRM team in 2018x.6 for the Defect #43129 Ends
				for (int j = 0; j < noOfColumns; j++) {
					HashMap columnMap = new HashMap();
					String columnValue = "";
					StringList colValueList = new StringList();
					StringList colValueUOMList = new StringList();
					StringList colValueDisplayList = new StringList();
					String i18nNoAccessMsg = "";

					columnMap = (HashMap) columns.get(j);
					String columnName = tableViewBean.getName(columnMap);

					// To get the Column level RMB Setting
					String strRMBSetting = tableViewBean.getSetting(columnMap, CONST_RMB_MENU);
					String nowrapSetting = tableViewBean.getSetting(columnMap, CONST_NO_WRAP);
					if (tableViewBean.isAssociatedWithDimension(columnMap) && !tableViewBean.isAlphanumericField(columnMap)) {
						nowrapSetting = null;
					}

					StringList passOIDList = new StringList();
					String passOID = "";

					String columnType = tableViewBean.getSetting(columnMap, CONST_COLUMN_TYPE);

					String colFormat = tableViewBean.getSetting(columnMap, CONST_FORMAT);
					String adminType = tableViewBean.getSetting(columnMap, CONST_ADMIN_TYPE);
					String alternateOIDSelect = tableViewBean.getSetting(columnMap, CONST_ALTERNATE_OID_EXPRESSION);

					if (columnType.equalsIgnoreCase(CONST_FILE)) {
						continue;
					}

					if (alternateOIDSelect != null && alternateOIDSelect.length() > 0) {
						passOIDList = (StringList) (bwsl.getElement(i).getSelectDataList(alternateOIDSelect));
						if (passOIDList != null) {
							passOID = (String) (passOIDList.firstElement());
						}

					}
					if (columnType.equals(CONST_BO) || columnType.equals(CONST_REL)
							|| columnType.equals(CONST_PROGRAM.toLowerCase())) {

						if (columnType.equals(CONST_BO)) {
							String columnSelect = tableViewBean.getBusinessObjectSelect(columnMap);
							if (tableViewBean.isAssociatedWithDimension(columnMap) && !tableViewBean.isAlphanumericField(columnMap)) {
								String sDBselect = columnSelect;
								if (UOMUtil.isSimpleAttributeExpression(columnSelect)) {
									String sUOMcolumnselect = (String) columnMap.get(CONST_UOM_INPUT_SELECT);
									String attrName = UOMUtil.getAttrNameFromSelect(columnSelect);
									String inputValue = "";
									String systemValue = "";

									colValueList = (StringList) (bwsl.getElement(i)
											.getSelectDataList(sUOMcolumnselect));
									if (colValueList != null) {
										inputValue = (String) colValueList.firstElement();
										// inputValue = UOMUtil.convertToI18nUnitName(inputValue, languageStr);
									}
									sUOMcolumnselect = (String) columnMap.get(CONST_UOM_SYSTEM_SELECT);
									if (sUOMcolumnselect != null) {
										colValueList = (StringList) (bwsl.getElement(i)
												.getSelectDataList(sUOMcolumnselect));
										if (colValueList != null) {
											systemValue = (String) colValueList.firstElement();
											// systemValue = UOMUtil.convertToI18nUnitName(systemValue, languageStr);
										}
									}
									colValueList = new StringList();
									colValueList.addElement(
											UOMUtil.formatDisplayValue(_ctx, inputValue, systemValue, _languageStr));
								} else {
									sDBselect = (String) columnMap.get(CONST_UOM_DB_SELECT);
									colValueList = (StringList) (bwsl.getElement(i).getSelectDataList(columnSelect));
								}
								colValueUOMList = (StringList) (bwsl.getElement(i).getSelectDataList(sDBselect));
							} else {
								String isRTE = (String) columnMap.get(UIComponent.ISRTEFIELD);
								if (isRTE != null && CONSTANT_TRUE.equalsIgnoreCase(isRTE)) {
									colValueList = (StringList) (bwsl.getElement(i)
											.getSelectDataList((String) columnMap.get(tableViewBean.RTE_EXPRESSION)));
									if (colValueList == null || "".equalsIgnoreCase((String) colValueList.get(0))) {
										colValueList = (StringList) (bwsl.getElement(i)
												.getSelectDataList(columnSelect));
									}
								} else {
									colValueList = (StringList) (bwsl.getElement(i).getSelectDataList(columnSelect));
								}
							}
						} else if (columnType.equals(CONST_REL)) {
							String columnSelect = tableViewBean.getRelationshipSelect(columnMap);
							RelationshipWithSelect rws = (RelationshipWithSelect) rwsl.elementAt(i);
							Hashtable columnInfo = rws.getRelationshipData();
							try {
								String strColumnValue = "";
								if (tableViewBean.isAssociatedWithDimension(columnMap) && !tableViewBean.isAlphanumericField(columnMap)) {
									String sUOMDbValue = "";
									if (UOMUtil.isSimpleAttributeExpression(columnSelect)) {
										sUOMDbValue = (String) columnInfo.get(columnSelect);
										String sUOMcolumnselect = (String) columnMap.get(CONST_UOM_INPUT_SELECT);
										String attrName = UOMUtil.getAttrNameFromSelect(columnSelect);
										String inputValue = "";
										String systemValue = "";
										inputValue = (String) columnInfo.get(sUOMcolumnselect);
										sUOMcolumnselect = (String) columnMap.get(CONST_UOM_SYSTEM_SELECT);
										if (sUOMcolumnselect != null) {
											systemValue = (String) columnInfo.get(sUOMcolumnselect);
											if (systemValue == null) {
												systemValue = "";
											}
										}
										strColumnValue = UOMUtil.formatDisplayValue(_ctx, inputValue, systemValue,
												_languageStr);

									} else {
										String sUOMcolumnselect = (String) columnMap.get(CONST_UOM_DB_SELECT);
										sUOMDbValue = (String) columnInfo.get(sUOMcolumnselect);
										strColumnValue = (String) columnInfo.get(columnSelect);
									}
									if (sUOMDbValue == null) {
										sUOMDbValue = "";
									}
									colValueUOMList.add(sUOMDbValue);
								} else {
									strColumnValue = (String) columnInfo.get(columnSelect);
								}

								if (strColumnValue == null) {
									strColumnValue = "";
								}
								colValueList.add(strColumnValue);
							} catch (Exception ex) {
								colValueList = (StringList) columnInfo.get(columnSelect);
							}

						} else if (columnType.equals(CONSTANT_PROGRAM)) {
							if (i <= (programResult[j].size() - 1)) {
								HashMap cellValueMap = (HashMap) programResult[j].get(i);
								Object colActualValue = cellValueMap.get(CONST_ACTUAL_VALUE);
								if (colActualValue instanceof StringList) {
									colValueList = (StringList) colActualValue;
								} else {
									colValueList.add((String) colActualValue);
								}

								Object colDisplayValue = cellValueMap.get(CONST_DISPLAY_VALUE);
								if (colDisplayValue instanceof StringList) {
									colValueDisplayList = (StringList) colDisplayValue;
								} else {
									colValueDisplayList.add((String) colDisplayValue);
								}
							}
						}
						if (colValueList == null)
							colValueList = new StringList();

						if (colValueList.size() > 0) {
							UIForm uf = new UIForm();
							String select = "";
							if (columnType.equals(CONST_BO)) {
								select = uf.getBusinessObjectSelect(columnMap);
							} else if (columnType.equals(CONST_REL)) {
								select = uf.getRelationshipSelect(columnMap);
							}
							String fldType = tableViewBean.getSetting(columnMap, CONST_FIELD_TYPE);
							if (fldType != null && fldType.equalsIgnoreCase(CONST_ATTRIBUTE)
									&& select.startsWith(CONST_ATTRIBUTE))

							{

								AttributeType attType = new AttributeType(uf.getAttrNameFromSelect(select));

								String dataType = (String) columnMap.get(CONST_ATTRIBUTE_DATA_TYPE);
								if (UIUtil.isNullOrEmpty(dataType)) {
									dataType = attType.getDataType(_ctx);
								}
								if (CONST_BOOLEAN.equalsIgnoreCase(dataType))

								{
									attType.open(_ctx);
									StringList fldChoices = attType.getChoices();
									attType.close(_ctx);
									if (fldChoices != null && fldChoices.size() == 2) {
										String clnValue = (String) colValueList.get(0);
										String strChoice = (String) fldChoices.get(0);
										if (CONST_YES.equalsIgnoreCase(strChoice) || CONST_ON.equalsIgnoreCase(strChoice)
												|| "True".equalsIgnoreCase(strChoice)
												|| "1".equalsIgnoreCase(strChoice)) {
											if ("TRUE".equalsIgnoreCase(clnValue))
												clnValue = strChoice;
											else
												clnValue = (String) fldChoices.get(1);
										}
										colValueList = new StringList(clnValue);
									}
								}
							}
						}
						if (colValueList != null && adminType != null) {
							if (adminType.equals(CONST_STATE)) {
								String alternatePolicySelect = tableViewBean.getSetting(columnMap, CONST_ALTERNATE_POLICY_EXPRESSION);

								if (alternatePolicySelect != null && alternatePolicySelect.length() > 0) {
									StringList policyList = (StringList) (bwsl.getElement(i)
											.getSelectDataList(alternatePolicySelect));
									colValueList = UINavigatorUtil.getStateI18NStringList(policyList, colValueList,
											_languageStr);
									StringList noAccessList = new StringList(colValueList.size());
									Iterator colValueListItr = colValueList.iterator();
									while (colValueListItr.hasNext()) {
										String colVal = (String) colValueListItr.next();
										noAccessList.add(tableViewBean.NO_READ_ACCESS.equals(colVal) ? i18nNoAccessMsg : colVal);
									}
									colValueList = noAccessList;
								} else {
									StringList policyList = (StringList) (bwsl.getElement(i)
											.getSelectDataList(CONST_POLICY.toLowerCase()));
									colValueList = UINavigatorUtil.getStateI18NStringList(policyList, colValueList,
											_languageStr);
								}

							} else if (adminType.startsWith(CONST_ATTRIBUTE_UNDERSCORE)) {
								// String attributeName = Framework.getPropertyValue(session, adminType);
								String attributeName = PropertyUtil.getSchemaProperty(_ctx, adminType);
								colValueList = UINavigatorUtil.getAttrRangeI18NStringList(attributeName, colValueList,
										_languageStr);
							} else {
								colValueList = UINavigatorUtil.getAdminI18NStringList(adminType, colValueList,
										_languageStr);
								if (columnType.equals(CONSTANT_PROGRAM)) {
									colValueList = colValueDisplayList;
								}
							}
						}
						if (colValueList != null && colValueList.size() > 0)
							columnValue = (String) (colValueList.firstElement());

					} else if (columnType.equals(CONST_PROGRAM_HTML_OUTPUT)) {
						columnValue = programResult[j].get(i).toString();
					}

					if (colValueList != null && colValueList.size() > 0)
						columnValue = (String) (colValueList.firstElement());

					columnValueMap.put(columnName, StringHelper.getHrefRemovedData(columnValue));
				}
				
				columnValueList.add(columnValueMap);
			}
		} catch (Exception ex) {
			if (ex.toString() != null && (ex.toString().trim()).length() > 0)
				ex.printStackTrace();

		}
		return columnValueList;

	}
}
