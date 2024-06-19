package com.pg.dsm.sapview.utils;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.StringList;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SAPComplexBOMUtil {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public SAPComplexBOMUtil() {
    }

    /**
     * @param context
     * @param incomingID
     * @param incomingObjMap
     * @param objectList
     * @return
     * @throws FrameworkException
     */
    public MapList getComplexBOMInfo(Context context, String incomingID, Map<Object, Object> incomingObjMap, MapList objectList) throws FrameworkException {

        Instant startTime = Instant.now();
        MapList retList = new MapList();
        if (null != objectList && !objectList.isEmpty()) {

            List<Map<Object, Object>> sequenceList = new ArrayList<>();
            int iLevel;

            String incomingObjType = (String) incomingObjMap.get(DomainConstants.SELECT_TYPE);

            Map<Object, Object> childMap;
            Iterator iterator = objectList.iterator();
            String childOid;
            String childType;
            String childLevel;
            String childName;
            String childRev;

            boolean isSelfComplex;
            boolean isParentComplex;

            Map<Object, Object> parentMap;
            String parentType;
            String parentName;
            String parentID;

            StringList typesAtFirstLevelOfCUP = new StringList();
            typesAtFirstLevelOfCUP.addElement(pgV3Constants.TYPE_PGCONSUMERUNITPART);
            typesAtFirstLevelOfCUP.addElement(pgV3Constants.TYPE_PGINNERPACKUNITPART);

            MapList mlBOM = new MapList();// to store BOM (only intermediates) of first level CUP.
            Map<Object, Object> mBOM;

            MapList mlBOMIPs = new MapList();// to store BOM (only intermediates) of first level CUP.
            Map<Object, Object> mBOMIP;

            boolean hasCUPinCUP = false;
            boolean hasMultiIPs = false;
            boolean hasMultiCOPs = false;

            boolean ipHasMultiIPs = false;
            boolean ipHasCUPinCUP = false;
            boolean ipHasMultiCOPs = false;

            List<Map<Object, Object>> components;

            // iterate through each component.
            while (iterator.hasNext()) {
                childMap = (Map<Object, Object>) iterator.next();
                iLevel = Integer.parseInt((String) (childMap).get(DomainConstants.SELECT_LEVEL));
                childOid = (String) (childMap).get(DomainConstants.SELECT_ID);
                childType = (String) (childMap).get(DomainConstants.SELECT_TYPE);
                childRev = (String) (childMap).get(DomainConstants.SELECT_REVISION);
                childLevel = (String) (childMap).get(DomainConstants.SELECT_LEVEL);
                childName = (String) (childMap).get(DomainConstants.SELECT_NAME);
                isSelfComplex = false;
                isParentComplex = false;

                if (iLevel == 1) {
                    // when iterating level is 1 (satisfies only for first level CUP)

                    if (iLevel == 1) {
                        sequenceList.clear();
                        // add incoming object at zeroth index of temporary list.
                        sequenceList.add(incomingObjMap);
                    }
                    // for first level CUP - its parent is incoming object (or Top level FPP).
                    childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), incomingID);
                    childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), incomingObjType);
                    childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), Boolean.FALSE);
                    childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.FALSE);

                    // CUP is complex BOM when:
                    // CUP has - multiple CUP (if atleast one CUP is present then flag all child of CUP as complex bom)
                    // CUP has - multiple IP
                    // CUP has - multiple COP

                    // get first level intermediates of CUP (first level CUP)
                    mlBOM = getFirstLevelIntermediates(context, childOid);
                    hasMultiIPs = isCaseMultipleIPs(mlBOM);     // if CUP has multiple IPs
                    hasCUPinCUP = isCaseCUPinCUP(mlBOM);        // if CUP has atleast one CUP
                    hasMultiCOPs = isCaseMultipleCOPs(mlBOM);   // if CUP has multiple COPs
                    // represent this CUP as child of incoming object.
                    if (!incomingObjMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) {
                        components = new ArrayList<>();
                        components.add(childMap);
                        incomingObjMap.put(SAPViewConstant.KEY_COMPONENTS.getValue(), components);
                        incomingObjMap.put(SAPViewConstant.KEY_HAS_COMPONENT.getValue(), Boolean.TRUE);
                    } else {
                        components = (List<Map<Object, Object>>) incomingObjMap.get(SAPViewConstant.KEY_COMPONENTS.getValue());
                        components.add(childMap);
                    }
                    // represent incoming object as parent of this CUP.
                    childMap.put(SAPViewConstant.KEY_PARENT.getValue(), incomingObjMap);
                    childMap.put(SAPViewConstant.KEY_HAS_PARENT.getValue(), Boolean.TRUE);


                } else {
                    parentMap = sequenceList.get(iLevel - 1);  // get parent from (level-1) index of temporary list.
                    parentType = (String) parentMap.get(DomainConstants.SELECT_TYPE);
                    parentName = (String) parentMap.get(DomainConstants.SELECT_NAME);
                    parentID = (String) parentMap.get(DomainConstants.SELECT_ID);
                    isParentComplex = (Boolean) parentMap.get(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue());

                    // represent parent-child structure - start
                    childMap.put(SAPViewConstant.KEY_PARENT.getValue(), parentMap);
                    childMap.put(SAPViewConstant.KEY_HAS_PARENT.getValue(), Boolean.TRUE);

                    if (!parentMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) {
                        components = new ArrayList<>();
                        components.add(childMap);
                        parentMap.put(SAPViewConstant.KEY_COMPONENTS.getValue(), components);
                        parentMap.put(SAPViewConstant.KEY_HAS_COMPONENT.getValue(), Boolean.TRUE);
                    } else {
                        components = (List<Map<Object, Object>>) parentMap.get(SAPViewConstant.KEY_COMPONENTS.getValue());
                        components.add(childMap);
                    } // represent parent-child structure - end

                    if (iLevel == 2 && childType.equalsIgnoreCase(pgV3Constants.TYPE_PGINNERPACKUNITPART)) {
                        mlBOMIPs = getFirstLevelIntermediates(context, childOid);
                        ipHasMultiIPs = isCaseMultipleIPs(mlBOMIPs);     // if CUP has multiple IPs
                        ipHasCUPinCUP = isCaseCUPinCUP(mlBOMIPs);        // if CUP has atleast one CUP
                        ipHasMultiCOPs = isCaseMultipleCOPs(mlBOMIPs);   // if CUP has multiple COPs
                    }

                    if((iLevel == 3) && (ipHasMultiIPs || ipHasCUPinCUP || ipHasMultiCOPs)) {
                        for (int i = 0; i < mlBOMIPs.size(); i++) {
                            mBOMIP = (Map<Object, Object>) mlBOMIPs.get(i);
                            final boolean isLevelEqual = childLevel.equalsIgnoreCase(String.valueOf(Integer.parseInt((String) mBOMIP.get(DomainConstants.SELECT_LEVEL)) + 2));
                            final boolean isTypeEqual = childType.equalsIgnoreCase((String) mBOMIP.get(DomainConstants.SELECT_TYPE));
                            final boolean isNameEqual = childName.equalsIgnoreCase((String) mBOMIP.get(DomainConstants.SELECT_NAME));
                            final boolean isRevisionEqual = childRev.equalsIgnoreCase((String) mBOMIP.get(DomainConstants.SELECT_REVISION));
                            if (isLevelEqual && isTypeEqual && isNameEqual && isRevisionEqual) {
                                childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), isParentComplex);
                                childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), parentID);
                                // flag is COP/IP as Complex BOM
                                childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE);
                                childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), parentType);
                            }
                        }
                    }

                    // to check if first level CUP has 2 COP / IP - then flag those COP, IP as complex BOM.
                    // CUP can have only COPs or IPs but not both at the same level.
                    // flag first level intermediate (of first level CUP) as complex bom - if condition is satisfied.
                    if ((iLevel == 2) && (hasCUPinCUP || hasMultiIPs || hasMultiCOPs)) {
                        for (int i = 0; i < mlBOM.size(); i++) {
                            mBOM = (Map<Object, Object>) mlBOM.get(i);
                            final boolean isLevelEqual = childLevel.equalsIgnoreCase(String.valueOf(Integer.parseInt((String) mBOM.get(DomainConstants.SELECT_LEVEL)) + 1));
                            final boolean isTypeEqual = childType.equalsIgnoreCase((String) mBOM.get(DomainConstants.SELECT_TYPE));
                            final boolean isNameEqual = childName.equalsIgnoreCase((String) mBOM.get(DomainConstants.SELECT_NAME));
                            final boolean isRevisionEqual = childRev.equalsIgnoreCase((String) mBOM.get(DomainConstants.SELECT_REVISION));

                            if (isLevelEqual && isTypeEqual && isNameEqual && isRevisionEqual) {
                                childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), isParentComplex);
                                childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), parentID);
                                // flag is COP/IP as Complex BOM
                                childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE);
                                childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), parentType);
                            }
                        }
                        isSelfComplex = false;
                        isParentComplex = false;
                    } else {

                        // parent type is equal to child type - IP in IP case
                        if (parentType.equalsIgnoreCase(childType) && !pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(childType)) {
                            isSelfComplex = Boolean.TRUE;
                        } else {
                            // if parent is complex then - set child also as complex.
                            if (isParentComplex) {
                                isSelfComplex = Boolean.TRUE;
                            } else {
                                // calculate is child is complex.
                                isSelfComplex = isComplexBOMStructure(context, childOid, childType, parentType);
                            }
                        }
                        childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), isParentComplex);
                        childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), parentID);
                        childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), isSelfComplex);
                        childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), parentType);
                    }
                }
                sequenceList.add(iLevel, childMap); // add iterating child map at its level(th) index in temporary list.
                retList.add(childMap);
            }
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("SAP BOM e-Delivery Interceptor - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return retList;
    }


    /**
     * @param context
     * @param incomingID
     * @param incomingObjMap
     * @param objectList
     * @return
     * @throws FrameworkException
     */
    public MapList getComplexBOMInfoReshipper(Context context, String incomingID, MapList objectList) throws FrameworkException {

        Instant startTime = Instant.now();
        MapList retList = new MapList();
        if (null != objectList && !objectList.isEmpty()) {

            List<Map<Object, Object>> sequenceList = new ArrayList<>();
            int iLevel;

            DomainObject dObj = DomainObject.newInstance(context, incomingID);
            StringList busSelects = getBusSelects();
            Map incomingObjMap = dObj.getInfo(context, busSelects);
            String incomingObjType = (String) incomingObjMap.get(DomainConstants.SELECT_TYPE);

            Map<Object, Object> childMap;
            Iterator iterator = objectList.iterator();
            String childOid;
            String childType;
            String childLevel;
            String childName;
            String childRev;

            boolean isSelfComplex;
            boolean isParentComplex;

            Map<Object, Object> parentMap;
            String parentType;
            String parentName;
            String parentID;

            StringList typesAtFirstLevelOfCUP = new StringList();
            typesAtFirstLevelOfCUP.addElement(pgV3Constants.TYPE_PGCONSUMERUNITPART);
            typesAtFirstLevelOfCUP.addElement(pgV3Constants.TYPE_PGINNERPACKUNITPART);

            MapList mlBOM = new MapList();// to store BOM (only intermediates) of first level CUP.
            Map<Object, Object> mBOM;

            MapList mlBOMIPs = new MapList();// to store BOM (only intermediates) of first level CUP.
            Map<Object, Object> mBOMIP;

            boolean hasCUPinCUP = false;
            boolean hasMultiIPs = false;
            boolean hasMultiCOPs = false;

            boolean ipHasMultiIPs = false;
            boolean ipHasCUPinCUP = false;
            boolean ipHasMultiCOPs = false;

            MapList cupsBOMList = new MapList();

            List<Map<Object, Object>> components;

            boolean cupHasMultiIPs = false;
            boolean cupHasCUPinCUP = false;
            boolean cupHasMultiCOPs = false;
            if(incomingObjType.equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART)) {
                cupsBOMList = getFirstLevelIntermediates(context, incomingID);
                cupHasMultiIPs = isCaseMultipleIPs(cupsBOMList);     // if CUP has multiple IPs
                cupHasCUPinCUP = isCaseCUPinCUP(cupsBOMList);        // if CUP has atleast one CUP
                cupHasMultiCOPs = isCaseMultipleCOPs(cupsBOMList);   // if CUP has multiple COPs
            }
            // iterate through each component.
            while (iterator.hasNext()) {
                childMap = (Map<Object, Object>) iterator.next();
                iLevel = Integer.parseInt((String) (childMap).get(DomainConstants.SELECT_LEVEL));
                childOid = (String) (childMap).get(DomainConstants.SELECT_ID);
                childType = (String) (childMap).get(DomainConstants.SELECT_TYPE);
                childRev = (String) (childMap).get(DomainConstants.SELECT_REVISION);
                childLevel = (String) (childMap).get(DomainConstants.SELECT_LEVEL);
                childName = (String) (childMap).get(DomainConstants.SELECT_NAME);
                isSelfComplex = false;
                isParentComplex = false;

                if (iLevel == 1) {
                    // when iterating level is 1 (satisfies only for first level CUP)
                    if (iLevel == 1) {
                        sequenceList.clear();
                        // add incoming object at zeroth index of temporary list.
                        sequenceList.add(incomingObjMap);
                    }
                    // for first level CUP - its parent is incoming object (or Top level FPP).
                    childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), incomingID);
                    childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), incomingObjType);
                    childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), Boolean.FALSE);
                    childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.FALSE);

                    // CUP is complex BOM when:
                    // CUP has - multiple CUP (if atleast one CUP is present then flag all child of CUP as complex bom)
                    // CUP has - multiple IP
                    // CUP has - multiple COP
                    // get first level intermediates of CUP (first level CUP)
                    mlBOM = getFirstLevelIntermediates(context, childOid);
                    hasMultiIPs = isCaseMultipleIPs(mlBOM);     // if CUP has multiple IPs
                    hasCUPinCUP = isCaseCUPinCUP(mlBOM);        // if CUP has atleast one CUP
                    hasMultiCOPs = isCaseMultipleCOPs(mlBOM);   // if CUP has multiple COPs
                    // represent this CUP as child of incoming object.
                    if (!incomingObjMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) {
                        components = new ArrayList<>();
                        components.add(childMap);
                        incomingObjMap.put(SAPViewConstant.KEY_COMPONENTS.getValue(), components);
                        incomingObjMap.put(SAPViewConstant.KEY_HAS_COMPONENT.getValue(), Boolean.TRUE);
                    } else {
                        components = (List<Map<Object, Object>>) incomingObjMap.get(SAPViewConstant.KEY_COMPONENTS.getValue());
                        components.add(childMap);
                    }
                    // represent incoming object as parent of this CUP.
                    childMap.put(SAPViewConstant.KEY_PARENT.getValue(), incomingObjMap);
                    childMap.put(SAPViewConstant.KEY_HAS_PARENT.getValue(), Boolean.TRUE);

                    if ((iLevel == 1) && (cupHasMultiIPs || cupHasMultiCOPs || cupHasCUPinCUP)) {
                        for (int i = 0; i < cupsBOMList.size(); i++) {
                            Map<Object, Object> cupBOM = (Map<Object, Object>) cupsBOMList.get(i);
                            final boolean isLevelEqual = childLevel.equalsIgnoreCase(String.valueOf(Integer.parseInt((String) cupBOM.get(DomainConstants.SELECT_LEVEL))));
                            final boolean isTypeEqual = childType.equalsIgnoreCase((String) cupBOM.get(DomainConstants.SELECT_TYPE));
                            final boolean isNameEqual = childName.equalsIgnoreCase((String) cupBOM.get(DomainConstants.SELECT_NAME));
                            final boolean isRevisionEqual = childRev.equalsIgnoreCase((String) cupBOM.get(DomainConstants.SELECT_REVISION));

                            if (isLevelEqual && isTypeEqual && isNameEqual && isRevisionEqual) {
                                childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), isParentComplex);
                                childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), incomingID);
                                // flag is COP/IP as Complex BOM
                                childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE);
                                childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), incomingObjType);
                            }
                        }
                        isSelfComplex = false;
                        isParentComplex = false;
                    }


                } else {
                    parentMap = sequenceList.get(iLevel - 1);  // get parent from (level-1) index of temporary list.
                    parentType = (String) parentMap.get(DomainConstants.SELECT_TYPE);
                    parentName = (String) parentMap.get(DomainConstants.SELECT_NAME);
                    parentID = (String) parentMap.get(DomainConstants.SELECT_ID);
                    isParentComplex = (Boolean) parentMap.get(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue());

                    // represent parent-child structure - start
                    childMap.put(SAPViewConstant.KEY_PARENT.getValue(), parentMap);
                    childMap.put(SAPViewConstant.KEY_HAS_PARENT.getValue(), Boolean.TRUE);

                    if (!parentMap.containsKey(SAPViewConstant.KEY_COMPONENTS.getValue())) {
                        components = new ArrayList<>();
                        components.add(childMap);
                        parentMap.put(SAPViewConstant.KEY_COMPONENTS.getValue(), components);
                        parentMap.put(SAPViewConstant.KEY_HAS_COMPONENT.getValue(), Boolean.TRUE);
                    } else {
                        components = (List<Map<Object, Object>>) parentMap.get(SAPViewConstant.KEY_COMPONENTS.getValue());
                        components.add(childMap);
                    } // represent parent-child structure - end
                    // to check if first level CUP has 2 COP / IP - then flag those COP, IP as complex BOM.
                    // CUP can have only COPs or IPs but not both at the same level.
                    // flag first level intermediate (of first level CUP) as complex bom - if condition is satisfied.
                    if ((iLevel == 2) && (hasCUPinCUP || hasMultiIPs || hasMultiCOPs)) {
                        for (int i = 0; i < mlBOM.size(); i++) {
                            mBOM = (Map<Object, Object>) mlBOM.get(i);
                            final boolean isLevelEqual = childLevel.equalsIgnoreCase(String.valueOf(Integer.parseInt((String) mBOM.get(DomainConstants.SELECT_LEVEL)) + 1));
                            final boolean isTypeEqual = childType.equalsIgnoreCase((String) mBOM.get(DomainConstants.SELECT_TYPE));
                            final boolean isNameEqual = childName.equalsIgnoreCase((String) mBOM.get(DomainConstants.SELECT_NAME));
                            final boolean isRevisionEqual = childRev.equalsIgnoreCase((String) mBOM.get(DomainConstants.SELECT_REVISION));

                            if (isLevelEqual && isTypeEqual && isNameEqual && isRevisionEqual) {
                                childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), isParentComplex);
                                childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), parentID);
                                // flag is COP/IP as Complex BOM
                                childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), Boolean.TRUE);
                                childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), parentType);
                            }
                        }
                        isSelfComplex = false;
                        isParentComplex = false;
                    } else {

                        // parent type is equal to child type - IP in IP case
                        if (parentType.equalsIgnoreCase(childType) && !pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(childType)) {
                            isSelfComplex = Boolean.TRUE;
                        } else {
                            // if parent is complex then - set child also as complex.
                            if (isParentComplex) {
                                isSelfComplex = Boolean.TRUE;
                            } else {
                                // calculate is child is complex.
                                isSelfComplex = isComplexBOMStructure(context, childOid, childType, parentType);
                            }
                        }
                        childMap.put(SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue(), isParentComplex);
                        childMap.put(SAPViewConstant.KEY_PARENT_ID.getValue(), parentID);
                        childMap.put(SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue(), isSelfComplex);
                        childMap.put(SAPViewConstant.KEY_PARENT_TYPE.getValue(), parentType);
                    }
                }
                sequenceList.add(iLevel, childMap); // add iterating child map at its level(th) index in temporary list.
                retList.add(childMap);
            }
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("SAP BOM e-Delivery Interceptor - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return retList;
    }

    /**
     * @param objectList
     */
    public void printLikeMQL(MapList objectList) {
        Map<Object, Object> objectMap;
        List<Map<Object, Object>> components;
        final Iterator iterator = objectList.iterator();
        String type;
        String name;
        String revision;
        while (iterator.hasNext()) {
            objectMap = (Map<Object, Object>) iterator.next();
            type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
            name = (String) objectMap.get(DomainConstants.SELECT_NAME);
            revision = (String) objectMap.get(DomainConstants.SELECT_REVISION);
            boolean hasParent = (Boolean) objectMap.get(SAPViewConstant.KEY_HAS_PARENT.getValue());
            logger.info(type + " " + name + " " + revision);
            logger.info("Parent is:");
            if (hasParent) {
                Map<Object, Object> parentMap = (Map<Object, Object>) objectMap.get(SAPViewConstant.KEY_PARENT.getValue());
                type = (String) parentMap.get(DomainConstants.SELECT_TYPE);
                name = (String) parentMap.get(DomainConstants.SELECT_NAME);
                revision = (String) parentMap.get(DomainConstants.SELECT_REVISION);
                logger.info(type + " " + name + " " + revision);
            }
            logger.info("Components are:");
            if (objectMap.containsKey(SAPViewConstant.KEY_HAS_COMPONENT.getValue())) {
                components = (List<Map<Object, Object>>) objectMap.get(SAPViewConstant.KEY_COMPONENTS.getValue());
                for (int i = 0; i < components.size(); i++) {
                    objectMap = components.get(i);
                    type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
                    name = (String) objectMap.get(DomainConstants.SELECT_NAME);
                    revision = (String) objectMap.get(DomainConstants.SELECT_REVISION);
                    logger.info(type + " " + name + " " + revision);
                }
            }
        }
    }


    /**
     * @param context
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    MapList getFirstLevelIntermediates(Context context, String objectOid) throws FrameworkException {

        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        final String type = domainObject.getInfo(context, DomainConstants.SELECT_TYPE);
        StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append(pgV3Constants.TYPE_PGCONSUMERUNITPART);
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGCUSTOMERUNITPART); // test it...
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGINNERPACKUNITPART);

        String types = typeBuilder.toString();

        StringList objectSelects = new StringList();
        objectSelects.addElement(DomainConstants.SELECT_ID);
        objectSelects.addElement(DomainConstants.SELECT_TYPE);
        objectSelects.addElement(DomainConstants.SELECT_NAME);
        objectSelects.addElement(DomainConstants.SELECT_REVISION);
        objectSelects.addElement(DomainConstants.SELECT_LEVEL);

        String objectWhere = DomainObject.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
        return domainObject.getRelatedObjects(
                context,                             // Context
                DomainConstants.RELATIONSHIP_EBOM,   // String
                types,                               // String
                false,                            // boolean
                true,                            // boolean
                1,                                // int
                objectSelects,                       // StringList
                DomainConstants.EMPTY_STRINGLIST,    // StringList
                objectWhere,                            // String
                DomainConstants.EMPTY_STRING,        // String
                0,                                // int
                DomainConstants.EMPTY_STRING,        // String
                types,                               // String post type pattern
                null);// Map
    }

    /**
     * @param objectList
     * @return
     */
    int getFirstLevelIntermediateTypeCount(MapList objectList) {
        int count = 0;
        if (null != objectList && !objectList.isEmpty()) {
            List<String> typesList = (List<String>) objectList.stream().map(map -> ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE)).collect(Collectors.toList());
            if (null != typesList && !typesList.isEmpty()) {
                count = typesList.size();
            }
        }
        return count;
    }

    boolean isCaseCUPinCUP(MapList objectList) {
        boolean isComplex = false;
        if (null != objectList && !objectList.isEmpty()) {
            Optional optional = objectList.stream().filter(map -> ((String) ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE)).equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART)).findFirst();
            if (optional.isPresent()) {
                isComplex = true;
            }
        }
        return isComplex;
    }

    boolean isCaseMultipleIPs(MapList objectList) {
        boolean isComplex = false;
        if (null != objectList && !objectList.isEmpty()) {
            List<String> resultList = (List<String>) objectList.stream().filter(map -> ((String) ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE)).equalsIgnoreCase(pgV3Constants.TYPE_PGINNERPACKUNITPART)).collect(Collectors.toList());
            if (null != resultList && !resultList.isEmpty()) {
                if (resultList.size() > 1) {
                    isComplex = true;
                }
            }
        }
        return isComplex;
    }

    boolean isCaseMultipleCOPs(MapList objectList) {
        boolean isComplex = false;
        if (null != objectList && !objectList.isEmpty()) {
            List<String> resultList = (List<String>) objectList.stream().filter(map -> ((String) ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE)).equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)).collect(Collectors.toList());
            if (null != resultList && !resultList.isEmpty()) {
                if (resultList.size() > 1) {
                    isComplex = true;
                }
            }
        }
        return isComplex;
    }

    /**
     * @param objectList
     * @return
     */
    int getFirstLevelIntermediateTypeUniqueCount(MapList objectList) {
        int count = 0;
        if (null != objectList && !objectList.isEmpty()) {
            Set<String> typesList = (Set<String>) objectList.stream().map(map -> ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE)).collect(Collectors.toSet());
            if (null != typesList && !typesList.isEmpty()) {
                count = typesList.size();
            }
        }
        return count;
    }

    /**
     * @param context
     * @param objectOid
     * @param objectType
     * @return
     * @throws FrameworkException
     */
    boolean isComplexBOMStructure(Context context, String objectOid, String objectType, String parentType) throws FrameworkException {
        boolean isComplexBOM = false;
        if (!parentType.equalsIgnoreCase(objectType) && pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(objectType)) {
            return false;
        }
        String typeSymbolicName = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, objectType, false);
        MapList objectList = getFirstLevelIntermediates(context, objectOid);
        List<String> typesList = (List<String>) objectList.stream().map(map -> ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE)).collect(Collectors.toList());
        Map<String, Integer> typeCountMap = getTypeCountMap(typesList);

        return IntermediateTypes.isCompexBOMByType(typeSymbolicName, typeCountMap);
    }

    public enum IntermediateTypes {
        TYPE_PGCUSTOMERUNITPART {
            @Override
            public boolean isComplexBOM(Map<String, Integer> typeCountMap) {
                boolean isComplexBOM = false;
                // CUP is complex when:
                // if it has 1 CUP
                // if it has 2 COP / IP

                if (typeCountMap.containsKey(pgV3Constants.TYPE_PGCUSTOMERUNITPART)) {
                    isComplexBOM = true;
                }
                if (typeCountMap.containsKey(pgV3Constants.TYPE_PGCONSUMERUNITPART)) {
                    if (typeCountMap.get(pgV3Constants.TYPE_PGCONSUMERUNITPART) > 1) {
                        isComplexBOM = true;
                    }
                }
                if (typeCountMap.containsKey(pgV3Constants.TYPE_PGINNERPACKUNITPART)) {
                    if (typeCountMap.get(pgV3Constants.TYPE_PGINNERPACKUNITPART) > 1) {
                        isComplexBOM = true;
                    }
                }
                return isComplexBOM;
            }
        },
        TYPE_PGINNERPACKUNITPART {
            @Override
            public boolean isComplexBOM(Map<String, Integer> typeCountMap) {
                // IP is complex when:
                // if it has 1 IP
                // if it has 2 COP
                boolean isComplexBOM = false;
                if (typeCountMap.containsKey(pgV3Constants.TYPE_PGINNERPACKUNITPART)) {
                    isComplexBOM = true;
                }
                if (typeCountMap.containsKey(pgV3Constants.TYPE_PGCONSUMERUNITPART)) {
                    if (typeCountMap.get(pgV3Constants.TYPE_PGCONSUMERUNITPART) > 1) {
                        isComplexBOM = true;
                    }
                }

                return isComplexBOM;
            }
        },
        TYPE_PGCONSUMERUNITPART {
            @Override
            public boolean isComplexBOM(Map<String, Integer> typeCountMap) {
                // COP is complex when:
                // if it has 2 COP
                boolean isComplexBOM = false;
                if (typeCountMap.containsKey(pgV3Constants.TYPE_PGCONSUMERUNITPART)) {
                    if (typeCountMap.get(pgV3Constants.TYPE_PGCONSUMERUNITPART) > 1) {
                        isComplexBOM = true;
                    }
                }
                return isComplexBOM;
            }
        };

        public abstract boolean isComplexBOM(Map<String, Integer> typeCountMap);

        public static boolean isCompexBOMByType(String typeSymbolicName, Map<String, Integer> typeCountMap) {
            boolean isComplexBOM = false;
            IntermediateTypes intermediateTypes = IntermediateTypes.valueOf(typeSymbolicName.toUpperCase());
            if (null != intermediateTypes) {
                isComplexBOM = intermediateTypes.isComplexBOM(typeCountMap);
            }
            return isComplexBOM;
        }
    }

    /**
     * @param typesList
     * @return
     */
    Map<String, Integer> getTypeCountMap(List<String> typesList) {
        Map<String, Integer> typeMap = new HashMap<>();
        Integer integer;
        for (String type : typesList) {
            if (typeMap.containsKey(type)) {
                integer = typeMap.get(type);
                typeMap.put(type, integer + 1);
            } else {
                typeMap.put(type, new Integer(1));
            }
        }
        return typeMap;
    }

    /**
     * @return
     */
    public static StringList getBusSelects() {
        StringList objectSelects = new StringList(16);
        objectSelects.add(DomainConstants.SELECT_TYPE);
        objectSelects.add(DomainConstants.SELECT_NAME);
        objectSelects.add(DomainConstants.SELECT_REVISION);
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
        objectSelects.add(pgV3Constants.SELECT_ALTERNATE_ID);
        objectSelects.add(pgV3Constants.SELECT_ALTERNATE_TYPE);
        objectSelects.add(pgV3Constants.SELECT_ALTERNATE_NAME);
        objectSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]");
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
        objectSelects.add("from[" + pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION + "].to.name");
        objectSelects.add("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.name");
        objectSelects.addElement(DomainConstants.SELECT_LEVEL);
        //Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
        //Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
        return objectSelects;
    }

    /**
     * @return
     */
    public static StringList getRelSelects() {
        StringList relSelects = new StringList(33);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
        relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
        relSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]");
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PG_BOM_BASEQUANTITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MIN_QUANTITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MAX_QUANTITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
        //Added by DSM - for 2018x.1 requirement #25043 - Starts
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_START_EFFECTIVITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
        //Added by DSM - for 2018x.1 requirement #25043 - Ends
        return relSelects;
    }

    /**
     * @param context
     * @param incomingID
     * @throws FrameworkException
     */
    public void getComplexBOMInfo(Context context, String incomingID) throws FrameworkException {
        DomainObject incomingObj = DomainObject.newInstance(context, incomingID);

        StringList busSelects = getBusSelects();
        Map incomingObjMap = incomingObj.getInfo(context, busSelects);

        Pattern pIntermediateObjectType = new Pattern(pgV3Constants.TYPE_PGCONSUMERUNITPART);
        pIntermediateObjectType.addPattern(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
        pIntermediateObjectType.addPattern(pgV3Constants.TYPE_PGINNERPACKUNITPART);

        String objectWhere = DomainObject.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;

        MapList incomingChildList = incomingObj.getRelatedObjects(context,
                pgV3Constants.RELATIONSHIP_EBOM,        // relationship pattern
                pIntermediateObjectType.getPattern(),   // Type pattern
                busSelects,                  // object selects
                getRelSelects(),           // rel selects
                false,                               // to side
                true,                               // from side
                (short) 0,                              // recursion level
                objectWhere,                            // object where clause
                DomainConstants.EMPTY_STRING,           // rel where clause
                0);                                 // limit
        incomingChildList = getComplexBOMInfo(context, incomingID, incomingObjMap, incomingChildList);
        logger.log(Level.INFO, "Maplist after marking as complex: {0}", incomingChildList);
    }
}
