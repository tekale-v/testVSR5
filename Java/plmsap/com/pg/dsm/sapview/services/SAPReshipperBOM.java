package com.pg.dsm.sapview.services;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.sapview.enumeration.SAPConstants;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.dsm.sapview.interfaces.ISAPBOM;
import com.pg.dsm.sapview.utils.SAPUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SAPReshipperBOM implements ISAPBOM {
    private static final Logger logger = Logger.getLogger(SAPBOM.class.getName());

    @Override
    public MapList getBOMInfoList(Context context, String objectOid, MapList objectList) throws FrameworkException {
        Instant startTime = Instant.now();
        // incoming object is CUP.
        DomainObject incomingObj = DomainObject.newInstance(context, objectOid);
        Map incomingMap = incomingObj.getInfo(context, SAPUtil.getBusSelects());
        String incomingType = (String) incomingMap.get(DomainConstants.SELECT_TYPE);
        boolean isFirstLevelComplex = Boolean.FALSE;
        SAPUtil sapUtil = new SAPUtil();
        if (incomingType.equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART)) {
            List<String> intermediateTypes = sapUtil.getFirstLevelIntermediateTypes(context, objectOid);
            if (null != intermediateTypes && !intermediateTypes.isEmpty()) {
                isFirstLevelComplex = sapUtil.whenTypeIsCustomerUnitPart(incomingType, Boolean.TRUE, intermediateTypes);
            }
        }
        objectList = getBOMStructure(incomingMap, objectList, isFirstLevelComplex);
        objectList = getBOMInfoList(context, objectList);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("SAP (Identify Complex BOM) - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return objectList;
    }

    @Override
    public Map<Object, Object> getComplexInfo(Context context, Map<Object, Object> paramMap) throws FrameworkException {
        Map<Object, Object> objectMap = new HashMap<>(paramMap);

        String type = (String) (objectMap).get(DomainConstants.SELECT_TYPE);
        int level = Integer.parseInt((String) objectMap.get(DomainConstants.SELECT_LEVEL));
        boolean isComplex = false;

        // get parent info starts
        Map<Object, Object> parentMap = new HashMap<>();
        boolean isParentComplex = false;

        boolean hasParent = (objectMap.containsKey(SAPViewConstant.KEY_HAS_PARENT.getValue())) ? (Boolean) objectMap.get(SAPViewConstant.KEY_HAS_PARENT.getValue()) : Boolean.FALSE;
        if (hasParent) { // if it has parent. initialize parent map, (is parent complex) flag.
            parentMap = (objectMap.containsKey(SAPViewConstant.KEY_PARENT.getValue())) ? (Map<Object, Object>) objectMap.get(SAPViewConstant.KEY_PARENT.getValue()) : parentMap;
            if (parentMap.isEmpty()) {
                logger.log(Level.WARNING, "Error - Parent Map is empty");
            }
        } // get parent info ends.

        // get component info starts
        boolean hasComponent = (objectMap.containsKey(SAPViewConstant.KEY_HAS_COMPONENT.getValue())) ? (Boolean) objectMap.get(SAPViewConstant.KEY_HAS_COMPONENT.getValue()) : Boolean.FALSE;
        List<String> componentTypes = new ArrayList<>();
        if (hasComponent) { // if has component. initialize components types list.
            componentTypes = (objectMap.containsKey(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue())) ? (List<String>) objectMap.get(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue()) : componentTypes;
            if (componentTypes.isEmpty()) {
                logger.log(Level.WARNING, "Error - Component Type List is empty");
            }
        } // get component info ends

        switch (level) {
            case 1:
            case 2:
                isComplex = isParentMarkedAsComplex(context, objectMap, parentMap);
                break;
            default:
                if (hasParent) {
                    isParentComplex = (parentMap.containsKey(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue())) ? (Boolean) parentMap.get(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue()) : Boolean.FALSE;
                    if (isParentComplex) {
                        objectMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE);
                        if (hasComponent) {
                            isComplex = Boolean.TRUE;
                        }
                    } else {
                        isComplex = SAPConstants.IntermediateParts.isComplexBOM(context, objectMap); // case when type is: CUP/IP/COP
                    }
                }
                break;
        }
        if (isComplex) {
            List<Map<Object, Object>> components = (objectMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) ? (List<Map<Object, Object>>) objectMap.get(SAPViewConstant.KEY_COMPONENTS.getValue()) : new ArrayList<>();
            for (Map<Object, Object> component : components) {
                component.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE);
            }
        }
        return objectMap;
    }

    /**
     * @param context
     * @param objectMap
     * @param parentMap
     * @return
     * @throws FrameworkException
     */
    public boolean isParentMarkedAsComplex(Context context, Map<Object, Object> objectMap, Map<Object, Object> parentMap) throws FrameworkException {
        boolean isComplex = false;
        if(parentMap.containsKey(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue())) {
            if((Boolean) parentMap.get(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue())) {
                isComplex = Boolean.TRUE;
            }
        }
        if(!isComplex) {
            isComplex = SAPConstants.IntermediateParts.isComplexBOM(context, objectMap); // case when type is: CUP
        }
        return isComplex;
    }

    @Override
    public MapList getBOMInfoList(Context context, MapList objectList) throws FrameworkException {
        MapList infoList = new MapList();
        Iterator iterator = objectList.iterator();
        while (iterator.hasNext()) {
            infoList.add(getComplexInfo(context, (Map<Object, Object>) iterator.next()));
        }
        // cleanup maplist starts
        iterator = infoList.iterator();
        Map<Object, Object> objectMap;
        while (iterator.hasNext()) {
            objectMap = (Map<Object, Object>) iterator.next();
            if (objectMap.containsKey(SAPViewConstant.KEY_PARENT.getValue())) {
                objectMap.remove(SAPViewConstant.KEY_PARENT.getValue());
            }
            if (objectMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) {
                objectMap.remove(SAPViewConstant.KEY_COMPONENTS.getValue());
            }
        } // cleanup maplist end
        return infoList;
    }

    @Override
    public MapList getDisplayBOMInfoList(Context context, MapList objectList) throws FrameworkException {
        MapList infoList = new MapList();
        Iterator iterator = objectList.iterator();
        while (iterator.hasNext()) {
            infoList.add(getComplexInfo(context, (Map<Object, Object>) iterator.next()));
        }
        return infoList;
    }

    @Override
    public MapList getBOMStructure(Map<Object, Object> rootMap, MapList rootList, boolean isFirstLevelComplex) {
        Instant startTime = Instant.now();
        MapList retList = new MapList();

        Map<Object, Object> childMap;
        String childType;
        Map<Object, Object> parentMap;

        List<Map<Object, Object>> components;
        List<String> componentTypes;

        Iterator iterator = rootList.iterator();
        List<Map<Object, Object>> sequenceList = new ArrayList<>();
        int level;
        boolean isParentComplex;

        while (iterator.hasNext()) {
            childMap = (Map<Object, Object>) iterator.next();
            childType = (String) (childMap).get(DomainConstants.SELECT_TYPE);
            level = Integer.parseInt((String) (childMap).get(DomainConstants.SELECT_LEVEL));
            isParentComplex = Boolean.FALSE;
            if (level == 1) {
                parentMap = rootMap;
                sequenceList.clear();
                parentMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.FALSE); // set on root.

                // for CUP-reshipper BOM start
                if (isFirstLevelComplex) {
                    childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE); // set on first level
                } else {
                    childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.FALSE); // set on first level
                }// for CUP-reshipper BOM end

                sequenceList.add(parentMap); // add root object at zeroth index of temporary list.
            } else {
                parentMap = sequenceList.get(level - 1);  // get parent from (level-1) index of temporary list.
            }
            if (parentMap.containsKey(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue())) {
                isParentComplex = (Boolean) parentMap.get(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue());
            }

            if (isParentComplex) { // if parent is complex - mark child as complex - only in case of CUP - reshipper - start
                childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE);
            } // if parent is complex - mark child as complex - only in case of CUP - reshipper - end

            // collect components map and set it on its parents starts
            if (!parentMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) {
                components = new ArrayList<>();
                components.add(childMap);
                parentMap.put(SAPViewConstant.KEY_COMPONENTS.getValue(), components);
            } else {
                components = (List<Map<Object, Object>>) parentMap.get(SAPViewConstant.KEY_COMPONENTS.getValue());
                components.add(childMap);
            } // collect components map and set it on its parents ends.
            // collect components types and set it on its parent starts.
            if (!parentMap.containsKey(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue())) {
                componentTypes = new ArrayList<>();
                componentTypes.add(childType);
                parentMap.put(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue(), componentTypes);
            } else {
                componentTypes = (List<String>) parentMap.get(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue());
                componentTypes.add(childType);
            } // collect components types and set it on its parent ends.
            if (parentMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) { // if components are present then set true.
                parentMap.put(SAPViewConstant.KEY_HAS_COMPONENT.getValue(), Boolean.TRUE);
            } else { // if components are not present then set as false.
                parentMap.put(SAPViewConstant.KEY_HAS_COMPONENT.getValue(), Boolean.FALSE);
            }
            // represent root object as parent of (iterating object) and set additional keys.
            childMap.put(SAPViewConstant.KEY_PARENT.getValue(), parentMap);
            childMap.put(SAPViewConstant.KEY_HAS_PARENT.getValue(), Boolean.TRUE);
            childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), (String) parentMap.get(DomainConstants.SELECT_ID));
            childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), (String) parentMap.get(DomainConstants.SELECT_TYPE));

            sequenceList.add(level, childMap); // add iterating child map at its level(th) index in temporary list.
            retList.add(childMap);
        }
        rootList.clear();
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("SAP (Create Structure) - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return retList;
    }

    @Override
    public void displayBOMStructure(Context context, String objectOid) throws FrameworkException {
        Instant startTime = Instant.now();

        // incoming object is CUP.
        DomainObject incomingObj = DomainObject.newInstance(context, objectOid);
        Map incomingMap = incomingObj.getInfo(context, SAPUtil.getBusSelects());
        String incomingType = (String) incomingMap.get(DomainConstants.SELECT_TYPE);
        boolean isFirstLevelComplex = Boolean.FALSE;
        SAPUtil sapUtil = new SAPUtil();
        if (incomingType.equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART)) {
            List<String> intermediateTypes = sapUtil.getFirstLevelIntermediateTypes(context, objectOid);
            if (null != intermediateTypes && !intermediateTypes.isEmpty()) {
                isFirstLevelComplex = sapUtil.whenTypeIsCustomerUnitPart(incomingType, Boolean.TRUE, intermediateTypes);
            }
        }
        StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append(pgV3Constants.TYPE_PGCONSUMERUNITPART);
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGINNERPACKUNITPART);

        String objectWhere = DomainObject.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
        MapList objectList = incomingObj.getRelatedObjects(context,
                pgV3Constants.RELATIONSHIP_EBOM,        // relationship pattern
                typeBuilder.toString(),   // Type pattern
                SAPUtil.getBusSelects(),                  // object selects
                SAPUtil.getRelSelects(),           // rel selects
                false,                               // to side
                true,                               // from side
                (short) 0,                              // recursion level
                objectWhere,                            // object where clause
                DomainConstants.EMPTY_STRING,           // rel where clause
                0);                                 // limit
        objectList = getBOMStructure(incomingMap, objectList, isFirstLevelComplex);
        objectList = getDisplayBOMInfoList(context, objectList);
        printMapList(objectList, false);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("SAP BOM e-Delivery Interceptor - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }
    /**
     * @param objectMap
     */
    private void printInfoMap(Map<Object, Object> objectMap) {
        String type = (String) (objectMap).get(DomainConstants.SELECT_TYPE);
        String name = (String) (objectMap).get(DomainConstants.SELECT_NAME);
        String rev = (String) (objectMap).get(DomainConstants.SELECT_REVISION);
        int level = Integer.parseInt((String) (objectMap).get(DomainConstants.SELECT_LEVEL));
        boolean isComplex = (objectMap.containsKey(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue())) ? (Boolean) objectMap.get(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue()) : Boolean.FALSE;

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        }
        messageBuilder.append(level);
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append(DomainConstants.RELATIONSHIP_EBOM);
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append("to");
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append(type);
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append(name);
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append(rev);
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append(isComplex);
        System.out.println(messageBuilder.toString());
    }

    /**
     * @param objectMap
     */
    private void printMap(Map<Object, Object> objectMap) {
        printInfoMap(objectMap);
        boolean hasComponent = objectMap.containsKey(SAPViewConstant.KEY_HAS_COMPONENT.getValue()) ? (Boolean) objectMap.get(SAPViewConstant.KEY_HAS_COMPONENT.getValue()) : Boolean.FALSE;
        if (hasComponent) {
            List<Map<Object, Object>> components = (objectMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) ? (List<Map<Object, Object>>) objectMap.get(SAPViewConstant.KEY_COMPONENTS.getValue()) : new ArrayList<>();
            for (Map<Object, Object> component : components) {
                printMap(component);
            }
        }
    }

    /**
     * @param objectList
     */
    public void printMapList(MapList objectList, boolean applyBreak) {
        Iterator iterator = objectList.iterator();
        while (iterator.hasNext()) {
            printMap((Map<Object, Object>) iterator.next());
            if (applyBreak)
                break;
        }
    }
}
