/*
 **   MatrixUtil.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Utility Class to interact with database.
 **
 */
package com.pg.dsm.upload.battery.util;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.models.config.BatteryProductFeature;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MatrixUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;

    public MatrixUtil(Context context) {
        this.context = context;
    }

    /**
     * Method to find RM/DPP
     *
     * @param type            - String - fop type
     * @param nameRevisionMap       - Map<String, List<String>>
     * @param objSelectList String  - StringList
     * @return MapList - objects
     * @since DSM 2018x.6
     */
    public MapList searchObject(String type, Map<String, List<String>> nameRevisionMap, StringList objSelectList) {
        MapList objectList = new MapList();
        if (null != nameRevisionMap && nameRevisionMap.size() > 0) {
            List<String> nameList = nameRevisionMap.get(DomainConstants.SELECT_NAME);
            List<String> revList = nameRevisionMap.get(DomainConstants.SELECT_REVISION);

            if (null != nameList && null != revList && !nameList.isEmpty() && !revList.isEmpty()) {
                try {
                    Instant startTime = Instant.now();
                    objectList = new FindObject.Builder(context)
                            .setTypePattern(PropertyUtil.getSchemaProperty(context, type))
                            .setNamePattern(String.join(BatteryConstants.Basic.SYMBOL_COMMA.getValue(), nameList)) // comma separated object names
                            .setRevisionPattern(String.join(BatteryConstants.Basic.SYMBOL_COMMA.getValue(), revList)) // comma separated object revisions
                            .setObjectSelectList(objSelectList) // object attribute selects
                            .build().getResultList();
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);
                    logger.info(String.format("Searching object took|%s %s|%s %s|%s %s",duration.toMillis(),BatteryConstants.Basic.MILLI_SECONDS.getValue(),duration.getSeconds(),BatteryConstants.Basic.SECONDS.getValue(),duration.toMinutes(),BatteryConstants.Basic.MINUTES.getValue()));
                } catch (FrameworkException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return objectList;
    }

    /**
     * Method to get
     *
     * @param batteryProductFeatureList - List<BatteryProductFeature>
     * @throws matrix.util.MatrixException - exception
     * @since DSM 2018x.6
     */
    public void getAttributeOrPicklistInitialized(List<BatteryProductFeature> batteryProductFeatureList) throws MatrixException {
        Map<String, Map<String, Object>> attributesSchemaMap = getAttributeSchemaInfoMap();
        Map<String, Map<String, StringList>> picklistColumnsMap = getPicklistInfoMap(batteryProductFeatureList);
        String select;
        String columnType;
        Map<String, Object> schemaMap;
        Map<String, StringList> picklistColumnMap;
        String picklistName;

        for (BatteryProductFeature batteryProductFeature : batteryProductFeatureList) {
            select = batteryProductFeature.getSelect();
            columnType = batteryProductFeature.getType();

            if (BatteryConstants.Basic.FIELD_TYPE_BASIC.getValue().equalsIgnoreCase(columnType)) {
                // do-nothing.
                logger.info("Its a basic field like type, name revision etc..");
            }
            if (BatteryConstants.Basic.FIELD_TYPE_ATTRIBUTE.getValue().equalsIgnoreCase(columnType) && UIUtil.isNotNullAndNotEmpty(select) && attributesSchemaMap.containsKey(select)) {
                schemaMap = attributesSchemaMap.get(select);
                batteryProductFeature.setAttributeName((String) schemaMap.get(BatteryConstants.Basic.ATTRIBUTE_NAME.getValue()));
                batteryProductFeature.setAttributeSelect((String) schemaMap.get(BatteryConstants.Basic.ATTRIBUTE_SELECT.getValue()));
                batteryProductFeature.setAttributeDefaultValue((String) schemaMap.get(BatteryConstants.Basic.ATTRIBUTE_DEFAULT_VAL.getValue()));
                batteryProductFeature.setAttributeRanges((StringList) schemaMap.get(BatteryConstants.Basic.ATTRIBUTE_CHOICES.getValue()));
                batteryProductFeature.setAttributeMaxLength((int) schemaMap.get(BatteryConstants.Basic.ATTRIBUTE_MAX_LENGTH.getValue()));
                batteryProductFeature.setAttributeMultiline((boolean) schemaMap.get(BatteryConstants.Basic.IS_ATTRIBUTE_MULTI_LINE.getValue()));
                batteryProductFeature.setAttributeSingleValue((boolean) schemaMap.get(BatteryConstants.Basic.IS_ATTRIBUTE_SINGLE_VAL.getValue()));
                batteryProductFeature.setAttributeMultiValue((boolean) schemaMap.get(BatteryConstants.Basic.IS_ATTRIBUTE_MULTI_VAL.getValue()));
                batteryProductFeature.setAttributeRangeValue((boolean) schemaMap.get(BatteryConstants.Basic.IS_ATTRIBUTE_RANGE_VAL.getValue()));
            }
            if (batteryProductFeature.isPicklist()) {
                picklistName = batteryProductFeature.getPicklistName();
                if (UIUtil.isNotNullAndNotEmpty(picklistName) && picklistColumnsMap.containsKey(picklistName)) {
                    picklistColumnMap = picklistColumnsMap.get(picklistName);
                    batteryProductFeature.setPicklistNames(picklistColumnMap.get(BatteryConstants.Basic.PICKLIST_NAMES.getValue()));
                    batteryProductFeature.setPicklistRevisions(picklistColumnMap.get(BatteryConstants.Basic.PICKLIST_REVISIONS.getValue()));
                    batteryProductFeature.setPicklistIds(picklistColumnMap.get(BatteryConstants.Basic.PICKLIST_IDS.getValue()));
                }
            }
        }
    }

    /**
     * Method to get Attribute schema map information
     *
     * @return Map<String, Map < String, Object>>
     * @throws MatrixException - exception
     * @since DSM 2018x.6
     */
    public Map<String, Map<String, Object>> getAttributeSchemaInfoMap() throws MatrixException {

        Map<String, Map<String, Object>> attributesSchemaMap = new HashMap<>();
        BatteryConstants.Attribute[] attributes = BatteryConstants.Attribute.values();

        Map<String, Object> schemaMap;
        for (BatteryConstants.Attribute attribute : attributes) {
            schemaMap = new HashMap<>();

            schemaMap.put(BatteryConstants.Basic.ATTRIBUTE_NAME.getValue(), attribute.getAttribute(context));
            schemaMap.put(BatteryConstants.Basic.ATTRIBUTE_SELECT.getValue(), attribute.getAttributeSelect(context));
            schemaMap.put(BatteryConstants.Basic.ATTRIBUTE_CHOICES.getValue(), attribute.getChoices(context));
            schemaMap.put(BatteryConstants.Basic.ATTRIBUTE_DEFAULT_VAL.getValue(), attribute.getDefaultValue(context));
            schemaMap.put(BatteryConstants.Basic.ATTRIBUTE_MAX_LENGTH.getValue(), attribute.getMaxLength(context));
            schemaMap.put(BatteryConstants.Basic.IS_ATTRIBUTE_MULTI_LINE.getValue(), attribute.isMultiLine(context));
            schemaMap.put(BatteryConstants.Basic.IS_ATTRIBUTE_SINGLE_VAL.getValue(), attribute.isSingleVal(context));
            schemaMap.put(BatteryConstants.Basic.IS_ATTRIBUTE_MULTI_VAL.getValue(), attribute.isMultiVal(context));
            schemaMap.put(BatteryConstants.Basic.IS_ATTRIBUTE_RANGE_VAL.getValue(), attribute.isRangeVal(context));
            attributesSchemaMap.put(attribute.toString(), schemaMap);

        }
        return attributesSchemaMap;
    }

    /**
     * Method to get Pick List Information
     *
     * @param batteryProductFeatureList - List<BatteryProductFeature> - Bean object
     * @return Map<String, Map < String, Object>>
     * @throws FrameworkException - exception
     * @since DSM 2018x.6
     */
    public Map<String, Map<String, StringList>> getPicklistInfoMap(List<BatteryProductFeature> batteryProductFeatureList) throws FrameworkException {
        Map<String, Map<String, StringList>> picklistColumnsMap = new HashMap<>();
        StringList objectSelects = new StringList();
        objectSelects.addElement(DomainConstants.SELECT_TYPE);
        objectSelects.addElement(DomainConstants.SELECT_NAME);
        objectSelects.addElement(DomainConstants.SELECT_REVISION);
        objectSelects.addElement(DomainConstants.SELECT_ID);

        Iterator<BatteryProductFeature> batteryProductFeatureIterator = batteryProductFeatureList.iterator();
        BatteryProductFeature batteryProductFeature;
        String picklistName;
        while (batteryProductFeatureIterator.hasNext()) {
            batteryProductFeature = batteryProductFeatureIterator.next();
            if (batteryProductFeature.isPicklist()) {
                picklistName = batteryProductFeature.getPicklistName();
                if (UIUtil.isNotNullAndNotEmpty(picklistName)) {
                    logger.info("Search Picklist objects of type: " + picklistName);
                    picklistColumnsMap.put(picklistName, getPicklistInfo(picklistName, DomainConstants.EMPTY_STRING, objectSelects));
                } else {
                    logger.error(batteryProductFeature.getName() + " - column - 'picklist' setting is 'true' in XML but 'picklistName' setting is blank");
                }
            }
        }
        return picklistColumnsMap;
    }

    /**
     * Method to get pick list information
     *
     * @param typePattern   - String - types
     * @param where         - String - where clause
     * @param objectSelects - StringList - object select
     * @return Map<String, StringList>
     * @throws FrameworkException - exception
     * @since DSM 2018x.6
     */
    public Map<String, StringList> getPicklistInfo(String typePattern, String where, StringList objectSelects) throws FrameworkException {
        MapList pickList = new FindObject.Builder(context)
                .setTypePattern(typePattern)
                .setWhereClause(where)
                .setObjectSelectList(objectSelects) // object attribute selects
                .build().getResultList();

        Map<String, StringList> picklistInfo = new HashMap<>();
        StringList nameList = new StringList();
        StringList revisionList = new StringList();
        StringList idList = new StringList();

        if (pickList != null && !pickList.isEmpty()) {
            logger.info(typePattern + " - Number of Picklist objects found:" + pickList.size());
            Iterator<?> itr = pickList.iterator();
            Map<?, ?> tempMap;
            while (itr.hasNext()) {
                tempMap = (Map<?, ?>) itr.next();
                nameList.addElement((String) tempMap.get(DomainConstants.SELECT_NAME));
                revisionList.addElement((String) tempMap.get(DomainConstants.SELECT_REVISION));
                idList.addElement((String) tempMap.get(DomainConstants.SELECT_ID));
            }
        }
        picklistInfo.put(BatteryConstants.Basic.PICKLIST_NAMES.getValue(), nameList);
        picklistInfo.put(BatteryConstants.Basic.PICKLIST_REVISIONS.getValue(), revisionList);
        picklistInfo.put(BatteryConstants.Basic.PICKLIST_IDS.getValue(), idList);
        return picklistInfo;
    }

}
