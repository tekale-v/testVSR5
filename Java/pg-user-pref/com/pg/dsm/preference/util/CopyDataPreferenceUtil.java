package com.pg.dsm.preference.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.enumeration.CopyDataTable;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.models.Item;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.FlatTableRow;
import matrix.db.FlatTableRowParams;
import matrix.db.FlatTableRowQueryIterator;
import matrix.db.FlatTableRowQueryParams;
import matrix.db.FlatTableRowWithSelect;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CopyDataPreferenceUtil {
    private static final Logger logger = Logger.getLogger(CopyDataPreferenceUtil.class.getName());

    public CopyDataPreferenceUtil() {
    }

    /**
     * @param context
     * @param whereClause
     * @return
     * @throws MatrixException
     */
    public MapList getUserCopyPreferenceByWhereClause(Context context, String whereClause) throws MatrixException {
        MapList objectList = new MapList();
        try {
            FlatTableRowQueryParams queryParams = FlatTableRowQueryParams.getParams();
            queryParams.setFlatTableName(PreferenceConstants.Basic.USER_COPY_PREFERENCE_TABLE.get());
            if (UIUtil.isNotNullAndNotEmpty(whereClause)) {
                queryParams.setWhereExpression(whereClause);
            }
            StringList orderByList = new StringList(CopyDataTable.Columns.ORIGINATED.getSelectColumn());
            // queryParams.setOrderbys(orderByList);

            StringList selectList = new StringList();
            selectList.add(CopyDataTable.Columns.PHYSICAL_ID.getSelectColumn());
            selectList.add(CopyDataTable.Columns.TYPE.getSelectColumn());
            selectList.add(CopyDataTable.Columns.NAME.getSelectColumn());
            selectList.add(CopyDataTable.Columns.REVISION.getSelectColumn());
            selectList.add(CopyDataTable.Columns.CATEGORY.getSelectColumn());
            selectList.add(CopyDataTable.Columns.TITLE.getSelectColumn());
            selectList.add(CopyDataTable.Columns.USE_PREFERENCE.getSelectColumn());
            selectList.add(CopyDataTable.Columns.PERSON_KEY.getSelectColumn());
            selectList.add(CopyDataTable.Columns.ORIGINATED.getSelectColumn());

            queryParams.setSelectStatements(selectList);
            FlatTableRowQueryIterator flatTableRowQueryIterator = queryParams.exec(context);
            while (flatTableRowQueryIterator.hasNext()) {
                FlatTableRowWithSelect flatTableRowWithSelect = flatTableRowQueryIterator.next();
                Map<String, String> flatDataMap = flatTableRowWithSelect.getSelectData();
                objectList.add(flatDataMap);
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching UserCopyPreference: " + e);
            throw e;
        }
        logger.log(Level.INFO, "Result: " + objectList);
        return objectList;
    }

    /**
     * @param context
     * @param condition
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataByCategoryCriteria(Context context, String condition) throws MatrixException {
        MapList objectList = new MapList();
        try {
            List<Item> itemList = getCopyDataItemByCriteria(context, condition);
            Map<Object, Object> objectMap;
            for (Item item : itemList) {
                objectMap = new HashMap<>();
                objectMap.put(DomainConstants.SELECT_ID, DomainObject.newInstance(context, item.getPhysicalId()).getInfo(context, DomainConstants.SELECT_ID));
                objectMap.put(DomainConstants.SELECT_TYPE, item.getType());
                objectMap.put(DomainConstants.SELECT_NAME, item.getName());
                objectMap.put(DomainConstants.SELECT_REVISION, item.getRevision());
                objectMap.put("category", item.getCategory());
                objectMap.put("title", item.getTitle());
                objectMap.put("preference", item.getPreference());
                objectMap.put("person", item.getPerson());
                objectMap.put("originated", item.getOriginated());
                objectMap.put("UPTName", item.getUPTName());
                objectMap.put("UPTPhysicalID", item.getUPTPhysicalID());
                objectList.add(objectMap);
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching UserCopyPreference: " + e);
            throw e;
        }
        objectList.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
        return objectList;
    }

    /**
     * @param context
     * @param whereClause
     * @return
     * @throws MatrixException
     */
    public List<Item> getCopyDataItemByCriteria(Context context, String whereClause) throws MatrixException {
        List<Item> itemList = new ArrayList<>();
        try {
            MapList objectList = getCopyDataByCriteria(context, whereClause);
            if (null != objectList && !objectList.isEmpty()) {
                for (Object object : objectList) {
                    itemList.add(new Item((Map<String, String>) object));
                }
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching UserCopyPreference: " + e);
            throw e;
        }
        return itemList;
    }

    /**
     * @param context
     * @param whereClause
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataByCriteria(Context context, String whereClause) throws MatrixException {
    	//Modified by DSM for 22x CW-04 for Defect #55844-Start
        MapList objectList = new MapList();
        try {
			//Removed Push context code due to JVM error
        	//Modified by DSM for 22x CW-04 for Defect #55844-End
            FlatTableRowQueryParams queryParams = FlatTableRowQueryParams.getParams();
            queryParams.setFlatTableName(PreferenceConstants.Basic.USER_COPY_PREFERENCE_TABLE.get());
            if (UIUtil.isNotNullAndNotEmpty(whereClause)) {
                queryParams.setWhereExpression(whereClause);
            }
            StringList orderByList = new StringList(CopyDataTable.Columns.ORIGINATED.getSelectColumn());
            // queryParams.setOrderbys(orderByList);

            StringList selectList = new StringList();
            selectList.add(CopyDataTable.Columns.PHYSICAL_ID.getSelectColumn());
            selectList.add(CopyDataTable.Columns.TYPE.getSelectColumn());
            selectList.add(CopyDataTable.Columns.NAME.getSelectColumn());
            selectList.add(CopyDataTable.Columns.REVISION.getSelectColumn());
            selectList.add(CopyDataTable.Columns.CATEGORY.getSelectColumn());
            selectList.add(CopyDataTable.Columns.TITLE.getSelectColumn());
            selectList.add(CopyDataTable.Columns.USE_PREFERENCE.getSelectColumn());
            selectList.add(CopyDataTable.Columns.PERSON_KEY.getSelectColumn());
            selectList.add(CopyDataTable.Columns.ORIGINATED.getSelectColumn());
            selectList.add(CopyDataTable.Columns.UPTNAME.getSelectColumn());
            selectList.add(CopyDataTable.Columns.UPTPHYSICALID.getSelectColumn());

            queryParams.setSelectStatements(selectList);
            FlatTableRowQueryIterator flatTableRowQueryIterator = queryParams.exec(context);
            while (flatTableRowQueryIterator.hasNext()) {
                FlatTableRowWithSelect flatTableRowWithSelect = flatTableRowQueryIterator.next();
                Map<String, String> flatDataMap = flatTableRowWithSelect.getSelectData();
                objectList.add(flatDataMap);
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching UserCopyPreference: " + e);
            throw e;
        } 
        return objectList;
    }

    /**
     * @param context
     * @param category
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataPreferenceByCategory(Context context, String category) throws MatrixException {
        MapList objectList = new MapList();
        try {
            StringBuilder whereClauseBuilder = new StringBuilder();
            whereClauseBuilder.append(CopyDataTable.Columns.PERSON_KEY.getSelectColumn());
            whereClauseBuilder.append("==");
            whereClauseBuilder.append(context.getUser());
            whereClauseBuilder.append(" && ");
            whereClauseBuilder.append(CopyDataTable.Columns.CATEGORY.getSelectColumn());
            whereClauseBuilder.append("==");
            whereClauseBuilder.append(category);
            logger.log(Level.INFO, "Where Clause for find: " + whereClauseBuilder.toString());
            objectList = getCopyDataByCategoryCriteria(context, whereClauseBuilder.toString());
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fectching UserCopyPreference: " + e);
            throw e;
        }
        logger.log(Level.INFO, "UserCopyPreference Result: " + objectList);
        return objectList;
    }

    /**
     * @param context
     * @param category
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataByCategory(Context context, String category) throws MatrixException {
        MapList objectList = new MapList();
        try {
            StringBuilder whereClauseBuilder = new StringBuilder();
            whereClauseBuilder.append(CopyDataTable.Columns.PERSON_KEY.getSelectColumn());
            whereClauseBuilder.append("==");
            whereClauseBuilder.append(context.getUser());
            if (UIUtil.isNotNullAndNotEmpty(category)) {
                whereClauseBuilder.append(" && ");
                whereClauseBuilder.append(CopyDataTable.Columns.CATEGORY.getSelectColumn());
                whereClauseBuilder.append("==");
                whereClauseBuilder.append(category);
            }
            logger.log(Level.INFO, "Where Clause by category: " + whereClauseBuilder.toString());
            objectList = getCopyDataByCategoryCriteria(context, whereClauseBuilder.toString());
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching UserCopyPreference: " + e);
            throw e;
        }
        return objectList;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public MapList getAllCopyData(Context context) throws MatrixException {
        MapList objectList = new MapList();
        try {
            StringBuilder whereClauseBuilder = new StringBuilder();
            whereClauseBuilder.append(CopyDataTable.Columns.PERSON_KEY.getSelectColumn());
            whereClauseBuilder.append("==");
            whereClauseBuilder.append(context.getUser());
            logger.log(Level.INFO, "Where Clause by all: " + whereClauseBuilder.toString());
            objectList = getCopyDataByCategoryCriteria(context, whereClauseBuilder.toString());
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error fetching UserCopyPreference: " + e);
            throw e;
        }
        return objectList;
    }

    /**
     * @param context
     * @param keyPair
     * @throws MatrixException
     */
	public Map updateUserCopyPreference(Context context, Map<String, String> keyPair) throws MatrixException {
    	boolean isCtxPushed = false;
        Map objectMap= new HashMap();
    	String sMessage=DomainConstants.EMPTY_STRING;
    	try {
    		objectMap.put(DomainConstants.KEY_STATUS, sMessage);
    		FlatTableRowParams rowParams = FlatTableRowParams.getParams();
    		rowParams.setFlatTableTypeName(PreferenceConstants.Basic.USER_COPY_PREFERENCE_TABLE.get());
    		Iterator<Map.Entry<String, String>> iterator = keyPair.entrySet().iterator();
    		// push context required to update flat table.
    		ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, null, context.getVault().getName());
    		isCtxPushed = true;

    		boolean isDuplicateBus = restrictDuplicateData(context, keyPair);
    		if(isDuplicateBus) {
    			while (iterator.hasNext()) {
    				Map.Entry<String, String> mCopyData = iterator.next();
    				rowParams.addColumn(mCopyData.getKey(), mCopyData.getValue());
    			}
    			FlatTableRow flatTableRow = rowParams.create(context);
    			String rowUUID = flatTableRow.getRowUUID();
    			logger.log(Level.WARNING, "Flat table updated: " + rowUUID);
    		} else {
    			sMessage = EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource", context.getLocale(), "emxComponents.CopyProductData.Notice");
    			//Added by DSM for 22x.06 CW JAS for Requirement 46096 - START
    			//Get Alert message from Property file.
    			String sPartName = keyPair.get("BusName");
    			String sPartRevision = keyPair.get("BusRevision");
    			if(UIUtil.isNotNullAndNotEmpty(sPartName) && UIUtil.isNotNullAndNotEmpty(sPartRevision)) {
    				sMessage=sMessage.replace("{0}",sPartName);
    				sMessage=sMessage.replace("{1}",sPartRevision);
    				objectMap.put(DomainConstants.KEY_STATUS, sMessage);
    			}
    			//Added by DSM for 22x.06 CW JAS for Requirement 46096 - END
    		}
    	} catch (Exception e) {
    		logger.log(Level.WARNING, "Error updating flat table UserCopyPreference: " + e);

    	} finally {
    		if (isCtxPushed) {
    			ContextUtil.popContext(context);
    		}
    	}
    	return objectMap;
    }
   
    /**
	 *
	 * @param context
	 * @param keyPair
	 * @return
	 * @throws MatrixException
	 */
	public boolean restrictDuplicateData(Context context, Map<String, String> keyPair) throws MatrixException{
		boolean isDuplicate = true;
		FlatTableRowQueryIterator flatTableRowQueryIterator=null;
		try {
			String user = (String) keyPair.get("PersonKey");
			String objectPhysicalId = (String) keyPair.get("BusPhysicalId");
			String busCategory = (String) keyPair.get("BusCategory");

			StringList selectList = new StringList();
			selectList.add(CopyDataTable.Columns.PHYSICAL_ID.getSelectColumn());

			StringBuilder whereClauseBuilder = new StringBuilder();
			whereClauseBuilder.append(CopyDataTable.Columns.PERSON_KEY.getSelectColumn());
			whereClauseBuilder.append("==");
			whereClauseBuilder.append("'" + user + "'");
			whereClauseBuilder.append(" && ");
			whereClauseBuilder.append(CopyDataTable.Columns.CATEGORY.getSelectColumn());
			whereClauseBuilder.append("==");
			whereClauseBuilder.append(busCategory);

			FlatTableRowQueryParams queryParams = FlatTableRowQueryParams.getParams();
			queryParams.setFlatTableName(PreferenceConstants.Basic.USER_COPY_PREFERENCE_TABLE.get());
			if (UIUtil.isNotNullAndNotEmpty(whereClauseBuilder.toString())) {
				queryParams.setWhereExpression(whereClauseBuilder.toString());
			}
			queryParams.setSelectStatements(selectList);
			flatTableRowQueryIterator = queryParams.exec(context);
			while (flatTableRowQueryIterator.hasNext()) {
				FlatTableRowWithSelect flatTableRowWithSelect = flatTableRowQueryIterator.next();
				Map<String, String> flatDataMap = flatTableRowWithSelect.getSelectData();
				if (!flatDataMap.isEmpty() && objectPhysicalId.equals((String) flatDataMap.get(CopyDataTable.Columns.PHYSICAL_ID.getSelectColumn()))) {
					isDuplicate= false;
					break;
				}
			}

		} catch (Exception e){
			logger.log(Level.WARNING, "Error updating restrictDuplicateData: " + e);
		} finally {
			if(null!=flatTableRowQueryIterator)
				flatTableRowQueryIterator.close();
		}
		return isDuplicate;
	}
    
    
    
    

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataProduct(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.PRODUCT.getCopyData(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataPackaging(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.PACKAGING.getCopyData(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataRawMaterial(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.RAW_MATERIAL.getCopyData(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataTechSpec(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.TECHNICAL_SPEC.getCopyData(context);
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public MapList getCopyDataAll(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.ALL.getCopyData(context);
    }

    /**
     * @param context
     * @param args
     * @return
     * @throws Exception
     */
    public boolean updateUserPreferenceValue(Context context, String[] args) throws Exception {
        boolean isCtxPushed = false;
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        HashMap paramMap = (HashMap) programMap.get("paramMap");
        String objId = (String) paramMap.get("objectId");
        String newValue = (String) paramMap.get("New Value");
        try {
            if (UIUtil.isNotNullAndNotEmpty(objId)) {
                DomainObject domainObject = DomainObject.newInstance(context, objId);
                StringList selects = new StringList();
                selects.addElement(DomainConstants.SELECT_PHYSICAL_ID);
                Map map = domainObject.getInfo(context, selects);
                String physicalId = (String) map.get(DomainConstants.SELECT_PHYSICAL_ID);
                String whereClause = getWhereClauseForUpdateOrDelete(context, physicalId);
                logger.log(Level.INFO, "Where Clause for update: {0}", whereClause);
                // push context required to update flat table.
                ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, null, context.getVault().getName());
                isCtxPushed = true;
                String rowUUID = getFlatTableRowId(context, physicalId, whereClause);
                if (UIUtil.isNotNullAndNotEmpty(rowUUID)) {
                    FlatTableRowParams rowParams = FlatTableRowParams.getParams();
                    rowParams.setFlatTableTypeName(PreferenceConstants.Basic.USER_COPY_PREFERENCE_TABLE.get());
                    MqlUtil.mqlCommand(context, rowParams.setRowuuid(rowUUID).addColumn("PreferenceKey", newValue).getMqlModifyString());
                    logger.log(Level.WARNING, "Flat table row updated");
                }
            }
            return true;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error updating row:" + e);
            return false;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
    }

    /**
     * @param context
     * @param physicalId
     * @return
     */
    private String getWhereClauseForUpdateOrDelete(Context context, String physicalId) {
        StringBuilder whereClauseBuilder = new StringBuilder();
        whereClauseBuilder.append(CopyDataTable.Columns.PERSON_KEY.getSelectColumn());
        whereClauseBuilder.append("==");
        whereClauseBuilder.append(context.getUser());
        whereClauseBuilder.append(" && ");
        whereClauseBuilder.append(CopyDataTable.Columns.PHYSICAL_ID.getSelectColumn());
        whereClauseBuilder.append("==");
        whereClauseBuilder.append(physicalId);
        return whereClauseBuilder.toString();
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void deleteUserCopyPreference(Context context, String[] args) throws Exception {
        boolean isCtxPushed = false;
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            String physicalOid = (String) programMap.get("BusPhysicalId");
            String whereClause = getWhereClauseForUpdateOrDelete(context, physicalOid);
            logger.log(Level.INFO, "Where Clause for delete: {0}", whereClause);
            // push context required to update flat table.
            ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, null, context.getVault().getName());
            isCtxPushed = true;
            FlatTableRowParams rowParams = FlatTableRowParams.getParams();
            rowParams.setFlatTableTypeName(PreferenceConstants.Basic.USER_COPY_PREFERENCE_TABLE.get());
            String rowUUID = getFlatTableRowId(context, physicalOid, whereClause);
            if (UIUtil.isNotNullAndNotEmpty(rowUUID)) {
                rowParams.setRowuuid(rowUUID);
            }
            rowParams.delete(context);
            logger.log(Level.WARNING, "Flat table row deleted");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error deleting row: " + e);
            throw e;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
    }

    public String getFlatTableRowId(Context context, String physicalOid, String whereClause) {
        String getRowId = DomainConstants.EMPTY_STRING;
        try {
            FlatTableRowQueryParams queryParams = FlatTableRowQueryParams.getParams();
            queryParams.setFlatTableName(PreferenceConstants.Basic.USER_COPY_PREFERENCE_TABLE.get());
            if (UIUtil.isNotNullAndNotEmpty(whereClause)) {
                queryParams.setWhereExpression(whereClause);
            }
            String rowUUID = DomainConstants.EMPTY_STRING;
            StringList selectList = new StringList();
            selectList.add("rowuuid");
            queryParams.setSelectStatements(selectList);
            FlatTableRowQueryIterator flatTableRowQueryIterator = queryParams.exec(context);
            while (flatTableRowQueryIterator.hasNext()) {
                FlatTableRowWithSelect flatTableRowWithSelect = flatTableRowQueryIterator.next();
                getRowId = flatTableRowWithSelect.getRowUUID();
            }
            flatTableRowQueryIterator.close();
        } catch (Exception e) {
            logger.log(Level.INFO, "Error: " + e);
        }
        return getRowId;
    }

    public MapList getCopyDataByFilter(Context context, String[] args) {
        MapList objectList = new MapList();
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            String category = (String) programMap.get("pgCopyDataPreferenceFilter");
            if (UIUtil.isNotNullAndNotEmpty(category) && category.equals("Raw Material")) {
                category = PreferenceConstants.Basic.RAW_MATERIAL.get();
            } else if (UIUtil.isNotNullAndNotEmpty(category) && category.equals("Product")) {
                category = PreferenceConstants.Basic.PRODUCT.get();
            } else if (UIUtil.isNotNullAndNotEmpty(category) && category.equals("Packaging")) {
                category = PreferenceConstants.Basic.PACKAGING.get();
            } else if (UIUtil.isNotNullAndNotEmpty(category) && category.equals("Technical Specification")) {
                category = PreferenceConstants.Basic.TECHNICAL_SPEC.get();
            } else {
                category = DomainConstants.EMPTY_STRING; // filter = all
            }
            objectList = getCopyDataByCategory(context, category);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error fetching UserCopyPreference: " + e);
        }
        return objectList;
    }

}
