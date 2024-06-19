package com.pdfview.UI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UITable;
import com.matrixone.apps.framework.ui.UITableCommon;

import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectList;
import matrix.util.StringList;

public class TableUI {
	public static MapList executeTable(Context context, String objectId, String strTableName, MapList tableValues) {
		MapList tableData = new MapList();
		TableUI tableUI = new TableUI();
		try {
			MapList tableColumns = tableUI.getTableColumns(context, "en", strTableName);
			HashMap nameExpression = new HashMap();
			int itableColumnssize=tableColumns.size();
			for (int i = 0; i < itableColumnssize; i++) {
				Map columnMap = (Map) tableColumns.get(i);
				String columnName = (String) columnMap.get("name");
				String columnLabel = (String) columnMap.get("label");
				String columnExpression = (String) columnMap.get("expression_businessobject");
				nameExpression.put(columnName,
						BusinessUtil.isNotNullOrEmpty(columnExpression) ? columnExpression : columnLabel);
			}
			tableData = tableUI.getColumnValues(context, tableColumns, tableValues, "en");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableData;
	}

	private MapList getTableColumns(Context context, String strLanguage, String strTableName)
			throws FrameworkException {
		Vector v = new Vector(1);
		v.add("all");
		UITable table = new UITable();
		MapList columns = UITable.getColumns(context, strTableName, v);
		HashMap requestMap = new HashMap();
		requestMap.put("languageStr", strLanguage);
		return table.processColumns(context, new HashMap(), columns, requestMap);
	}

	private MapList getColumnValues(Context context, MapList processColumns, MapList objectIdList, String strLanguage)
			throws FrameworkException {
		try {
			HashMap hmRequestMap = new HashMap();
			hmRequestMap.put("languageStr", strLanguage);

			HashMap hmTableData = new HashMap();
			hmTableData.put("RequestMap", hmRequestMap);

			UITableCommon uiTable = new UITableCommon();
			HashMap hmColumnValuesMap = uiTable.getColumnValuesMap(context, processColumns, objectIdList, hmTableData,
					false);

			BusinessObjectWithSelectList bwsl = (BusinessObjectWithSelectList) hmColumnValuesMap.get("Businessobject");
			RelationshipWithSelectList rwsl = (RelationshipWithSelectList) hmColumnValuesMap.get("Relationship");
			Vector[] programResult = (Vector[]) hmColumnValuesMap.get("Program");

			int iColumnSize = processColumns.size();
			int iObjectListSize = objectIdList.size();

			MapList mlFinalList = new MapList();
			for (int i = 0; i < iObjectListSize; i++) {
				HashMap hmFinalMap = new HashMap();

				for (int k = 0; k < iColumnSize; k++) {
					HashMap hmColumnMap = (HashMap) processColumns.get(k);
					String strColumnType = UITable.getSetting(hmColumnMap, "Column Type");
					String strColumnLabel = UITable.getLabel(hmColumnMap);
					String strColumnValue = null;
					String strColumnSelect = null;
					StringList slColValueList = null;

					if ("program".equals(strColumnType)) {
						HashMap hmProgram = (HashMap) programResult[k].get(i);
						strColumnValue = (String) hmProgram.get("DisplayValue");
						hmFinalMap.put(strColumnLabel, strColumnValue);
					} else if ("businessobject".equals(strColumnType)) {
						strColumnSelect = UITable.getBusinessObjectSelect(hmColumnMap);
						slColValueList = (StringList) (bwsl.getElement(i).getSelectDataList(strColumnSelect));
						if (slColValueList != null) {
							strColumnValue = (String) slColValueList.firstElement();
							hmFinalMap.put(strColumnSelect, strColumnValue);
						}
					} else if ("relationship".equals(strColumnType)) {
						strColumnSelect = UITable.getRelationshipSelect(hmColumnMap);
						slColValueList = (StringList) (((RelationshipWithSelect) rwsl.elementAt(i))
								.getSelectDataList(strColumnSelect));
						if (slColValueList != null) {
							strColumnValue = (String) slColValueList.firstElement();
							hmFinalMap.put(strColumnSelect, strColumnValue);
						}
					}
				}

				Map objectMap = (Map) objectIdList.get(i);
				Iterator iterator = objectMap.keySet().iterator();
				while (iterator.hasNext()) {
					String strKey = (String) iterator.next();
					hmFinalMap.put(strKey, objectMap.get(strKey));
				}
				mlFinalList.add(hmFinalMap);
			}
			return mlFinalList;
		} catch (Exception ex) {
			throw new FrameworkException(ex);
		}
	}
}
