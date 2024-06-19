package com.pg.dsm.preference.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.CopyDataTable;
import com.pg.dsm.preference.enumeration.PlantPreferenceTable;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.models.Plant;
import com.pg.dsm.preference.models.PlantItem;
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

public class PlantDataPreferenceUtil {
    private static final Logger logger = Logger.getLogger(PlantDataPreferenceUtil.class.getName());

    public PlantDataPreferenceUtil() {
    }

    /**
     * @param context
     * @param keyPair
     * @throws MatrixException
     */
    public void createPlantEntry(Context context, Map<String, String> keyPair) throws MatrixException {
        boolean isCtxPushed = false;
        try {
            FlatTableRowParams rowParams = FlatTableRowParams.getParams();
            rowParams.setFlatTableTypeName(PreferenceConstants.Basic.USER_PLANT_PREFERENCE_TABLE.get());
            Iterator<Map.Entry<String, String>> iterator = keyPair.entrySet().iterator();
            // push context required to update flat table.
            ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, null, context.getVault().getName());
            isCtxPushed = true;
            while (iterator.hasNext()) {
                Map.Entry<String, String> record = (Map.Entry) iterator.next();
                rowParams.addColumn(record.getKey(), record.getValue());
            }
            FlatTableRow flatTableRow = rowParams.create(context);
            String rowUUID = flatTableRow.getRowUUID();
            logger.log(Level.WARNING, "Plant entry created: " + rowUUID);
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error creating entry in flat table UserPlantPreference: " + e);
            throw e;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
    }

    /**
     * @param context
     * @param whereClause
     * @return
     * @throws MatrixException
     */
    public List<PlantItem> retrievePlantEntries(Context context, String whereClause) throws MatrixException {
        List<PlantItem> itemList = new ArrayList<>();
        try {
            MapList objectList = retrievePlant(context, whereClause);
            if (null != objectList && !objectList.isEmpty()) {
                for (Object object : objectList) {
                    itemList.add(new PlantItem((Map<String, String>) object));
                }
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error retrieving Plant entries from flat table UserPlantPreference: " + e);
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
    public MapList retrievePlant(Context context, String whereClause) throws MatrixException {
        boolean isCtxPushed = false;
        MapList objectList = new MapList();
        try {
            // push context required to update flat table.
            ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, null, context.getVault().getName());
            isCtxPushed = true;
            FlatTableRowQueryParams queryParams = FlatTableRowQueryParams.getParams();
            queryParams.setFlatTableName(PreferenceConstants.Basic.USER_PLANT_PREFERENCE_TABLE.get());
            if (UIUtil.isNotNullAndNotEmpty(whereClause)) {
                queryParams.setWhereExpression(whereClause);
            }
            StringList orderByList = new StringList(CopyDataTable.Columns.ORIGINATED.getSelectColumn());
            // queryParams.setOrderbys(orderByList);

            StringList selectList = new StringList();
            PlantPreferenceTable.Columns[] columns = PlantPreferenceTable.Columns.values();
            for (PlantPreferenceTable.Columns column : columns) {
                selectList.add(column.getSelectColumn());
            }

            queryParams.setSelectStatements(selectList);
            FlatTableRowQueryIterator flatTableRowQueryIterator = queryParams.exec(context);
            while (flatTableRowQueryIterator.hasNext()) {
                FlatTableRowWithSelect flatTableRowWithSelect = flatTableRowQueryIterator.next();
                Map<String, String> flatDataMap = flatTableRowWithSelect.getSelectData();
                objectList.add(flatDataMap);
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error retrieving Plant entries from flat table UserPlantPreference: " + e);
            throw e;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
        return objectList;
    }

    /**
     * @param context
     * @param condition
     * @return
     * @throws MatrixException
     */
    public MapList retrievePlantEntriesByCategory(Context context, String condition) throws MatrixException {
        MapList objectList = new MapList();
        try {
            List<PlantItem> itemList = retrievePlantEntries(context, condition);
            Map<Object, Object> objectMap;
            for (PlantItem item : itemList) {
                objectMap = new HashMap<>();
                objectMap.put(DomainConstants.SELECT_ID, DomainObject.newInstance(context, item.getPhysicalId()).getInfo(context, DomainConstants.SELECT_ID));
                objectMap.put(DomainConstants.SELECT_TYPE, item.getType());
                objectMap.put(DomainConstants.SELECT_NAME, item.getName());
                objectMap.put(DomainConstants.SELECT_REVISION, item.getRevision());

                objectMap.put("category", item.getCategory());
                objectMap.put("categoryType", item.getCategoryType());
                objectMap.put("authorized", item.getAuthorized());
                objectMap.put("authorizedToUse", item.getAuthorizedToUse());
                objectMap.put("authorizedToProduce", item.getAuthorizedToProduce());
                objectMap.put("activated", item.getActivated());
                objectMap.put("person", item.getPerson());
                objectMap.put("originated", item.getOriginated());

                objectList.add(objectMap);
            }
        } catch (MatrixException e) {
            logger.log(Level.WARNING, "Error retrieving Plant entries from flat table UserPlantPreference: " + e);
            throw e;
        }
        objectList.sort(DomainConstants.SELECT_ORIGINATED, "descending", "date");
        return objectList;
    }

    public void createPlantEntries(Context context, String[] args) throws MatrixException {
        String objectId = args[0];
        String categoryType = args[1];
        String category = args[2];
        DomainObject domainObject = DomainObject.newInstance(context, objectId);
        List<Plant> plantList = getRelatedPlantList(context, domainObject);
        for (Plant plant : plantList) {
            Map<String, String> keyPair = new HashMap<>();
            keyPair.put("BusPhysicalId", plant.getPhysicalId());
            keyPair.put("category", category);
            keyPair.put("categoryType", categoryType);
            keyPair.put("authorized", plant.getAuthorized());
            keyPair.put("authorizedToUse", plant.getAuthorizedToUse());
            keyPair.put("authorizedToProduce", plant.getAuthorizedToProduce());
            keyPair.put("activated", plant.getActivated());
            keyPair.put("personKey", context.getUser());
            createPlantEntry(context, keyPair);
        }
    }

    public List<Plant> getRelatedPlantList(Context context, DomainObject domainObject) throws FrameworkException {
        List<Plant> plantList = new ArrayList<>();
        MapList objectList = getRelatedPlants(context, domainObject);
        if (null != objectList && !objectList.isEmpty()) {
            for (Object object : objectList) {
                plantList.add(new Plant((Map<Object, Object>) object));
            }
        }
        return plantList;
    }

    public MapList getRelatedPlants(Context context, DomainObject domainObject) throws FrameworkException {
        StringList relSelects = new StringList();
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);

        StringList objSelects = new StringList();
        objSelects.add(DomainConstants.SELECT_ID);
        objSelects.add(PreferenceConstants.Basic.PHYSICAL_ID.get());
        objSelects.add(DomainConstants.SELECT_TYPE);
        objSelects.add(DomainConstants.SELECT_NAME);
        objSelects.add(DomainConstants.SELECT_REVISION);

        return domainObject.getRelatedObjects(context, // context
                DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, // relationship
                PropertyUtil.getSchemaProperty(context, "type_Plant"), // type
                objSelects, // object selects
                relSelects, // rel selects
                Boolean.TRUE, // to side
                Boolean.FALSE, // from side
                (short) 1, // recurse level
                DomainConstants.EMPTY_STRING, // object where clause
                DomainConstants.EMPTY_STRING, // rel where clause
                0); // limit
    }

    /*public boolean updatePlantEntry(Context context, String[] args) throws Exception {
        boolean isCtxPushed = false;
        HashMap programMap = (HashMap) JPO.unpackArgs(args);
        HashMap paramMap = (HashMap) programMap.get("paramMap");
        String objectId = (String) paramMap.get("objectId");
        String newValue = (String) paramMap.get("New Value");
        try {
            if (UIUtil.isNotNullAndNotEmpty(objectId)) {
                DomainObject domainObject = DomainObject.newInstance(context, objectId);
                StringList selects = new StringList();
                selects.add(DomainConstants.SELECT_PHYSICAL_ID);
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
    }*/

    /**
     * Called from UI Command href.
     *
     * @param context
     * @param args
     * @return
     */
    public MapList retrieveUserPlantEntries(Context context, String[] args) {
        MapList objectList = new MapList();
        try {
            HashMap programMap = (HashMap) JPO.unpackArgs(args);
            String filter = (String) programMap.get("pgUserPlantDataPreferenceFilter");
            logger.log(Level.INFO, "User Plant Preference - Incoming filter: " + filter);
            if (UIUtil.isNotNullAndNotEmpty(filter) && filter.equals(PreferenceConstants.Basic.FILTER_RAW_MATERIAL.get())) {
                objectList = PlantPreferenceTable.Retrieve.RAW_MATERIAL.getPlantData(context);
            } else if (UIUtil.isNotNullAndNotEmpty(filter) && filter.equals(PreferenceConstants.Basic.FILTER_PRODUCT.get())) {
                objectList = PlantPreferenceTable.Retrieve.PRODUCT.getPlantData(context);
            } else if (UIUtil.isNotNullAndNotEmpty(filter) && filter.equals(PreferenceConstants.Basic.FILTER_PACKAGING.get())) {
                objectList = PlantPreferenceTable.Retrieve.PACKAGING.getPlantData(context);
            } else if (UIUtil.isNotNullAndNotEmpty(filter) && filter.equals(PreferenceConstants.Basic.FILTER_TECHNICAL_SPECIFICATION.get())) {
                objectList = PlantPreferenceTable.Retrieve.TECHNICAL_SPEC.getPlantData(context);
            } else {
                objectList = PlantPreferenceTable.Retrieve.ALL.getPlantData(context);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error fetching UserPlantPreference: " + e);
        }
        logger.log(Level.INFO, "User Plant Preference - result: " + objectList);
        return objectList;
    }
}
