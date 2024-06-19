package com.pg.dsm.ui.table;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.UOMUtil;
import com.matrixone.apps.framework.ui.UIForm;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UITable;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.ui.table.helper.EnoviaTableHelper;
import com.pg.dsm.ui.table.helper.StringHelper;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.AttributeType;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.Context;
import matrix.db.RelationshipWithSelect;
import matrix.db.RelationshipWithSelectList;
import matrix.util.MatrixException;
import matrix.util.StringList;
import com.matrixone.apps.framework.ui.UITableCommon;
import com.matrixone.apps.framework.ui.UIComponent;
import com.matrixone.apps.framework.ui.UIFormCommon;


/**
 * @author DSM
 *
 */
public class TableUtils {

	private Context ctx;
	private String timestamp;
	private String languageStr;
	private String table;
	private String program;
	private String objectId;
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	/**
	 * @param context
	 * @param strTimeStamp
	 */
	public TableUtils(Context context,String objectId) {
		this.ctx = context;
		this.timestamp = UIComponent.getTimeStamp();
		this.objectId = objectId;
	}

	/**
	 * @return
	 */
	public String get_languageStr() {
		return languageStr;
	}

	/**
	 * @param _languageStr
	 */
	public void set_languageStr(String languageStr) {
		this.languageStr = languageStr;
	}

	/**
	 * @return
	 */
	public String getProgram() {
		return program;
	}

	/**
	 * @param program
	 */
	public void setProgram(String program) {
		this.program = program;
	}

	/**
	 * @return
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @param pageMap
	 * @return
	 * @throws Exception
	 */
	public MapList getTableData(HashMap<String,String> pageMap) {
		MapList columnValueList = new MapList();
		try {
			UITable tableViewBean = new UITable();
			pageMap.put("table", this.table);
			pageMap.put("program", this.program);
			pageMap.put("objectId", this.objectId);
			//Modify by DSM(Sogeti) 2018x.6.1 Defect #43129 - START
			pageMap.put("dynamic", pgV3Constants.KEY_NO_VALUE);
			//Modify by DSM(Sogeti) 2018x.6.1 Defect #43129 - END
			tableViewBean.setTableData(this.ctx, pageMap, this.timestamp, PersonUtil.getAssignments(this.ctx));
			HashMap<?,?> tableData = tableViewBean.getTableData(this.timestamp);

			MapList relBusObjList = tableViewBean.getFilteredObjectList(tableData);
			relBusObjList = EnoviaTableHelper.getFilterTableObjList(relBusObjList);


			MapList relBusObjPageList = new MapList();

			MapList columns = UITableCommon.getColumns(tableData);
			int noOfColumns = columns.size();

			RelationshipWithSelectList rwsl = null;
			BusinessObjectWithSelectList bwsl = null;

			Vector<?>[] programResult = new Vector[noOfColumns];

			boolean reportMode = true;
			if ((relBusObjList != null) && (!relBusObjList.isEmpty())) {
				if (reportMode) {
					relBusObjPageList = relBusObjList;
				}
				HashMap<?,?> columnValuesMap = tableViewBean.getColumnValuesMap(this.ctx, null, relBusObjPageList, tableData);
				bwsl = (BusinessObjectWithSelectList) columnValuesMap.get("Businessobject");
				rwsl = (RelationshipWithSelectList) columnValuesMap.get("Relationship");
				programResult = (Vector[]) columnValuesMap.get("Program");
			}
			int relObjSize = relBusObjPageList.size();
			for (int i = 0; i < relObjSize; ++i) {
				HashMap<String,Object> columnValueMap = new HashMap<>();
				for (int j = 0; j < noOfColumns; ++j) {
					String columnValue = "";
					StringList colValueList = new StringList();
					StringList policy = new StringList();
					

					HashMap<String,Object> columnMap  =  (HashMap<String, Object>) columns.get(j);
					String columnName = UIComponent.getName(columnMap);

					String columnType = UIComponent.getSetting(columnMap, "Column Type");

					String adminType = UIComponent.getSetting(columnMap, "Admin Type");

					if ("File".equalsIgnoreCase(columnType)) {
						continue;
					}

					if (("businessobject".equals(columnType)) || ("relationship".equals(columnType))
							|| ("Program".equalsIgnoreCase(columnType))) {
						
						if ("businessobject".equals(columnType) && "State".equals(adminType)) {							
							policy = ((BusinessObjectWithSelect) bwsl.getElement(i)).getSelectDataList("policy");							
						}						
						colValueList = processAllColumn( columnType, columnMap, i, programResult[j], bwsl, rwsl, adminType);
											
						if ((colValueList != null) && (!colValueList.isEmpty()))
							columnValue =  colValueList.firstElement();
						
						
					} else if ("programHTMLOutput".equals(columnType)) {
						columnValue = StringHelper.replaceBrToNewLine(programResult[j].get(i).toString());
					}

					if ((colValueList != null) && (!colValueList.isEmpty())) {
						columnValue =  colValueList.firstElement();
					}
					columnValueMap.put(columnName, StringHelper.getHrefRemovedData(columnValue));
					
					if("State".equals(adminType)) {
						columnValueMap.put("Policy",policy.firstElement());
					}
				}

				columnValueList.add(columnValueMap);
			}
		} catch (Exception ex) {
			if ((ex.toString() != null) && (ex.toString().trim().length() > 0)) {
				logger.log(Level.WARNING, null, ex);
			}
		}
		return columnValueList;
	}

	/**
	 * @param columnType
	 * @param columnMap
	 * @param i
	 * @param programResult
	 * @param bwsl
	 * @param rwsl
	 * @param adminType
	 * @return
	 * @throws MatrixException
	 */
	private StringList processAllColumn(String columnType, HashMap<String,Object> columnMap,  int i, Vector<?> programResult, BusinessObjectWithSelectList bwsl, RelationshipWithSelectList rwsl, String adminType ) throws MatrixException {
		StringList colValueDisplayList = new StringList();
		StringList colValueList = new StringList();
		if ("businessobject".equals(columnType)) {							
			colValueList = processBusinessObjectColumn(this.ctx,columnMap,i,bwsl,this.languageStr);
			
		} else if ("relationship".equals(columnType)) {
			colValueList = processRelationshipColumn(this.ctx, columnMap,rwsl,i, this.languageStr);
			
		} else if (("program".equals(columnType)) && (i <= programResult.size() - 1)) {
			HashMap<?,?> cellValueMap = (HashMap<?,?>) programResult.get(i);
			Object colActualValue = cellValueMap.get("ActualValue");
			if (colActualValue instanceof StringList)
				colValueList = (StringList) colActualValue;
			else {
				colValueList.add((String) colActualValue);
			}

			Object colDisplayValue = cellValueMap.get("DisplayValue");
			if (colDisplayValue instanceof StringList)
				colValueDisplayList = (StringList) colDisplayValue;
			else {
				colValueDisplayList.add((String) colDisplayValue);
			}
		}

		if (colValueList == null) {
			colValueList = new StringList();
		}
		if (!colValueList.isEmpty()) {
			colValueList = processForm(this.ctx, columnMap, columnType, colValueList);
		}
		if (adminType != null) {
			colValueList = processAttribute(columnMap,adminType,colValueList,i,bwsl,columnType,colValueDisplayList);
		}	
		
		return colValueList;
	}

	/**
	 * @param columnMap
	 * @param adminType
	 * @param colValueList
	 * @param i
	 * @param bwsl
	 * @param columnType
	 * @param colValueDisplayList
	 * @return
	 * @throws MatrixException
	 */
	private StringList processAttribute(HashMap<String, Object> columnMap, String adminType,
			StringList colValueList, int i, BusinessObjectWithSelectList bwsl, String columnType, StringList colValueDisplayList) throws MatrixException {

		if ("State".equals(adminType)) {
			String alternatePolicySelect = UIComponent.getSetting(columnMap, "Alternate Policy expression");

			if ((alternatePolicySelect != null) && (alternatePolicySelect.length() > 0)) {
				StringList policyList = ((BusinessObjectWithSelect) bwsl.getElement(i))
						.getSelectDataList(alternatePolicySelect);

				colValueList = UINavigatorUtil.getStateI18NStringList(policyList, colValueList, this.languageStr);

				StringList noAccessList = new StringList(colValueList.size());
				Iterator<?> colValueListItr = colValueList.iterator();
				String colVal;
				while (colValueListItr.hasNext()) {
					colVal = (String) colValueListItr.next();
					noAccessList.add(("#DENIED!".equals(colVal)) ? "" : colVal);
				}
				colValueList = noAccessList;
			} else {
				StringList policyList = ((BusinessObjectWithSelect) bwsl.getElement(i))
						.getSelectDataList("Policy".toLowerCase());
				colValueList = UINavigatorUtil.getStateI18NStringList(policyList, colValueList, this.languageStr);
			}

		} else if (adminType.startsWith("attribute_")) {
			String attributeName = PropertyUtil.getSchemaProperty(this.ctx, adminType);
			colValueList = UINavigatorUtil.getAttrRangeI18NStringList(attributeName, colValueList, this.languageStr);
		} else {
			colValueList = UINavigatorUtil.getAdminI18NStringList(adminType, colValueList, this.languageStr);

			if ("program".equals(columnType)) {
				colValueList = colValueDisplayList;
			}
		}
		return colValueList;
	}

	/**
	 * @param ctx
	 * @param columnMap
	 * @param columnType
	 * @param colValueList
	 * @return
	 * @throws MatrixException
	 */
	private StringList processForm(Context ctx, HashMap<String, Object> columnMap, String columnType, StringList colValueList) throws MatrixException {
		UIForm uf = new UIForm();
		String select = "";
		if ("businessobject".equals(columnType))
			select = UIFormCommon.getBusinessObjectSelect(columnMap);
		else if ("relationship".equals(columnType)) {
			select = UIFormCommon.getRelationshipSelect(columnMap);
		}
		String fldType = UIComponent.getSetting(columnMap, "Field Type");
		if ((fldType != null) && ("attribute".equalsIgnoreCase(fldType))
				&& (select.startsWith("attribute"))) {
			AttributeType attType = new AttributeType(uf.getAttrNameFromSelect(select));

			String dataType = (String) columnMap.get("Attribute Data Type");
			if (UIUtil.isNullOrEmpty(dataType)) {
				dataType = attType.getDataType(ctx);
			}
			if ("boolean".equalsIgnoreCase(dataType)) {
				attType.open(ctx);
				StringList fldChoices = attType.getChoices();
				attType.close(ctx);
				if ((fldChoices != null) && (fldChoices.size() == 2)) {
					String clnValue =  colValueList.get(0);
					String strChoice =  fldChoices.get(0);
					if (("Yes".equalsIgnoreCase(strChoice)) || ("On".equalsIgnoreCase(strChoice))
							|| ("True".equalsIgnoreCase(strChoice))
							|| ("1".equalsIgnoreCase(strChoice))) {
						if ("TRUE".equalsIgnoreCase(clnValue))
							clnValue = strChoice;
						else
							clnValue =  fldChoices.get(1);
					}
					colValueList = new StringList(clnValue);
				}
			}
		}
		return colValueList;
	}

	/**
	 * @param ctx
	 * @param columnMap
	 * @param rwsl
	 * @param i
	 * @param languageStr
	 * @return
	 */
	private StringList processRelationshipColumn(Context ctx,HashMap columnMap, RelationshipWithSelectList rwsl, int i,String languageStr ) {
		StringList colValueList;
		String columnSelect = UITableCommon.getRelationshipSelect(columnMap);
		RelationshipWithSelect rws = rwsl.elementAt(i);
		Hashtable columnInfo = rws.getRelationshipData();
		try {
			String strColumnValue = "";
			if ((UITableCommon.isAssociatedWithDimension(columnMap))
					&& (!UITableCommon.isAlphanumericField(columnMap))) {
				if (UOMUtil.isSimpleAttributeExpression(columnSelect)) {
					String sUOMcolumnselect = (String) columnMap.get("UOM Input Select");
					String inputValue = "";
					String systemValue = "";
					inputValue = (String) columnInfo.get(sUOMcolumnselect);
					sUOMcolumnselect = (String) columnMap.get("UOM System Select");
					if (sUOMcolumnselect != null) {
						systemValue = (String) columnInfo.get(sUOMcolumnselect);
						if (systemValue == null) {
							systemValue = "";
						}
					}
					strColumnValue = UOMUtil.formatDisplayValue(ctx, inputValue, systemValue, languageStr);
				} else {
					strColumnValue = (String) columnInfo.get(columnSelect);
				}
				
			} else {
				strColumnValue = (String) columnInfo.get(columnSelect);
			}

			if (strColumnValue == null) {
				strColumnValue = "";
			}
			colValueList = new StringList();
			colValueList.add(strColumnValue);
		} catch (Exception ex) {
			colValueList = (StringList) columnInfo.get(columnSelect);
		}
		
		return colValueList;
	}

	/**
	 * @param ctx
	 * @param columnMap
	 * @param i
	 * @param bwsl
	 * @param languageStr
	 * @return
	 * @throws FrameworkException
	 */
	private StringList processBusinessObjectColumn(Context ctx, HashMap columnMap, int i, BusinessObjectWithSelectList bwsl, String languageStr) throws FrameworkException {
		StringList colValueList;
		String columnSelect = UITableCommon.getBusinessObjectSelect(columnMap);
		if ((UITableCommon.isAssociatedWithDimension(columnMap))
				&& (!UITableCommon.isAlphanumericField(columnMap))) {
			
			if (UOMUtil.isSimpleAttributeExpression(columnSelect)) {
				String sUOMcolumnselect = (String) columnMap.get("UOM Input Select");
				String inputValue = "";
				String systemValue = "";

				colValueList = ((BusinessObjectWithSelect) bwsl.getElement(i))
						.getSelectDataList(sUOMcolumnselect);

				if (colValueList != null) {
					inputValue = colValueList.firstElement();
				}

				sUOMcolumnselect = (String) columnMap.get("UOM System Select");
				if (sUOMcolumnselect != null) {
					colValueList = ((BusinessObjectWithSelect) bwsl.getElement(i))
							.getSelectDataList(sUOMcolumnselect);

					if (colValueList != null) {
						systemValue = colValueList.firstElement();
					}
				}

				colValueList = new StringList();
				colValueList.addElement(UOMUtil.formatDisplayValue(ctx, inputValue, systemValue, languageStr));
			} else {
				colValueList = ((BusinessObjectWithSelect) bwsl.getElement(i))
						.getSelectDataList(columnSelect);
			}
		} else {
			String isRTE = (String) columnMap.get("isRTEField");
			if ((isRTE != null) && ("true".equalsIgnoreCase(isRTE))) {
				colValueList = ((BusinessObjectWithSelect) bwsl.getElement(i))
						.getSelectDataList((String) columnMap.get("RTE Expression"));
				if ((colValueList == null) || ("".equalsIgnoreCase(colValueList.get(0)))) {
					colValueList = ((BusinessObjectWithSelect) bwsl.getElement(i)).getSelectDataList(columnSelect);
				}
			} else {
				colValueList = ((BusinessObjectWithSelect) bwsl.getElement(i))
						.getSelectDataList(columnSelect);
				
			}		
		}
		return colValueList;
	}
		
}
