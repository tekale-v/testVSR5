package com.pg.dsm.rollup.packaging_certification.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class BOMUtils {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;

    public BOMUtils(Context context) {
        this.context = context;
    }

    /**
     * Get flat BOM expansion of a given object id
     *
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    public MapList getFlatBOMExtraction(String objectOid) throws FrameworkException {
        MapList partList = new MapList();
        MapList subsIntermediateList = new MapList();

        String objectWhere = DomainConstants.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
        final StringList objectSelects = this.getBusSelects();
        final StringList relSelects = this.getRelSelects();

        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        final Map<Object, Object> infoMap = this.getInfoMap(context, domainObject);

        BOMExtractor bomExtractor = new BOMExtractor.Process(context, objectOid, DomainConstants.RELATIONSHIP_EBOM).load(objectSelects, relSelects);
        if (bomExtractor.isLoaded()) {
            partList.addAll(bomExtractor.getPartList());
            subsIntermediateList.addAll(bomExtractor.getSubsIntermediateList());

            for (int i = 0; i < subsIntermediateList.size(); i++) {
                Map<Object, Object> substituteIntermediateMap = (Map<Object, Object>) subsIntermediateList.get(i);
                String substituteIntermediateOid = (String) substituteIntermediateMap.get(DomainConstants.SELECT_ID);
                bomExtractor = new BOMExtractor.Process(context, substituteIntermediateOid, pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE).load(objectSelects, relSelects);
                if (bomExtractor.isLoaded()) {
                    partList.addAll(bomExtractor.getPartList());
                }
            }
            for (int j = 0; j < subsIntermediateList.size(); j++) {
                Map<Object, Object> substituteIntermediateMap = (Map<Object, Object>) subsIntermediateList.get(j);
                String substituteIntermediateOid = (String) substituteIntermediateMap.get(DomainConstants.SELECT_ID);
                domainObject.setId(substituteIntermediateOid);
                Map<Object, Object> intermediateMap = getInfoMap(context, domainObject);
                intermediateMap.put(DomainConstants.SELECT_LEVEL, DomainConstants.EMPTY_STRING);
                MapList mapList = getSubstituteIntermediateBOM(context, domainObject, objectWhere);
                for (int k = 0; k < mapList.size(); k++) {
                    Map<Object, Object> intermediateChildMap = (Map<Object, Object>) mapList.get(k);
                    IntermediateBOM intermediateBOM = new IntermediateBOM.Process(context, intermediateMap, intermediateChildMap).load(objectSelects);
                    if (intermediateBOM.isLoaded()) {
                        partList.addAll(intermediateBOM.getPartList());
                    }
                }
            }
            domainObject.setId(objectOid); // top level object.
            MapList uBOMList = this.getFirstLevelBOM(context, domainObject, objectWhere);
            for (int l = 0; l < uBOMList.size(); l++) {
                Map<Object, Object> uBOM = (Map<Object, Object>) uBOMList.get(l);
                String uType = (String) uBOM.get(DomainConstants.SELECT_TYPE);
                String uPolicy = (String) uBOM.get(DomainConstants.SELECT_POLICY);
                String uLevel = (String) uBOM.get(DomainConstants.SELECT_LEVEL);
                String uOid = (String) uBOM.get(DomainConstants.SELECT_ID);
                if (this.isEligibleChildType(uType, uPolicy)) {
                    partList.add(uBOM);
                    if (this.isEligibleAlternateType(uType)) {
                        MapList alternates = this.getAlternates(context, uOid, uType, this.getBusSelects());
                        for (int m = 0; m < alternates.size(); m++) {
                            partList.add((Map<Object, Object>) alternates.get(m));
                        }
                    }
                }
                MapList substitutes = this.extractSubstitutes(uBOM);
                for (int n = 0; n < substitutes.size(); n++) {
                    Map<Object, Object> uSubstitute = (Map<Object, Object>) substitutes.get(n);
                    uType = (String) uSubstitute.get(DomainConstants.SELECT_TYPE);
                    uPolicy = (String) uSubstitute.get(DomainConstants.SELECT_POLICY);
                    uLevel = (String) uSubstitute.get(DomainConstants.SELECT_LEVEL);
                    uOid = (String) uSubstitute.get(DomainConstants.SELECT_ID);
                    if (this.isEligibleChildType(uType, uPolicy)) {
                        partList.add(uSubstitute);
                        if (this.isEligibleAlternateType(uType)) {
                            MapList alternates = this.getAlternates(context, uOid, uType, this.getBusSelects());
                            for (int o = 0; o < alternates.size(); o++) {
                                partList.add((Map<Object, Object>) alternates.get(o));
                            }
                        }
                    }
                }
            }
            String strParentType = (String) infoMap.get(DomainConstants.SELECT_TYPE);
            if (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strParentType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strParentType)) {
                MapList selfAlternates = this.getAlternateOfIntermediatesChild(context, new HashMap<>(), infoMap, objectSelects);
                if (null != selfAlternates && !selfAlternates.isEmpty()) {
                    partList.addAll(selfAlternates);
                }
            }
        }
        return partList;
    }

    public void print(MapList inventory) {
        final Iterator iterator = inventory.iterator();
        while (iterator.hasNext()) {
            Map<Object, Object> objectMap = (Map<Object, Object>) iterator.next();
            String intermediateType = objectMap.containsKey("IntermediateType") ? (String) objectMap.get("IntermediateType") : "No Intermediate Type";
            String intermediateName = objectMap.containsKey("IntermediateName") ? (String) objectMap.get("IntermediateName") : "No Intermediate Name";
            String intermediateRev = objectMap.containsKey("IntermediateRevision") ? (String) objectMap.get("IntermediateRevision") : "No Intermediate Revision";
            String relationship = objectMap.containsKey("relationship") ? (String) objectMap.get("relationship") : "No Relationship";
            String type = objectMap.containsKey("type") ? (String) objectMap.get("type") : "No Type";
            String name = objectMap.containsKey("name") ? (String) objectMap.get("name") : "No Name";
            String revision = objectMap.containsKey("revision") ? (String) objectMap.get("revision") : "No Revision";

            System.out.println(intermediateType + "|" + intermediateName + "|" + intermediateRev + "|" + relationship + "|" + type + "|" + name + "|" + revision);
        }
    }

    public String getPrintLine(Map<Object, Object> objectMap) {
        String intermediateType = objectMap.containsKey("IntermediateType") ? (String) objectMap.get("IntermediateType") : "No Intermediate Type";
        String intermediateName = objectMap.containsKey("IntermediateName") ? (String) objectMap.get("IntermediateName") : "No Intermediate Name";
        String intermediateRev = objectMap.containsKey("IntermediateRevision") ? (String) objectMap.get("IntermediateRevision") : "No Intermediate Revision";
        String relationship = objectMap.containsKey("relationship") ? (String) objectMap.get("relationship") : "No Relationship";
        String type = objectMap.containsKey("type") ? (String) objectMap.get("type") : "No Type";
        String name = objectMap.containsKey("name") ? (String) objectMap.get("name") : "No Name";
        String revision = objectMap.containsKey("revision") ? (String) objectMap.get("revision") : "No Revision";
        String returnString = intermediateType + "|" + intermediateName + "|" + intermediateRev + "|" + relationship + "|" + type + "|" + name + "|" + revision;
        return returnString;
    }

    /**
     * Method to get object info.
     *
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    public Map<Object, Object> getInfoMap(Context context, DomainObject domainObject) throws FrameworkException {
        StringList busSelectList = new StringList(4);
        busSelectList.addElement(DomainConstants.SELECT_TYPE);
        busSelectList.addElement(DomainConstants.SELECT_NAME);
        busSelectList.addElement(DomainConstants.SELECT_REVISION);
        busSelectList.addElement(DomainConstants.SELECT_ID);
        return domainObject.getInfo(context, busSelectList);
    }

    /**
     * Method to get intermediates (CUP, COP, IP) at all levels
     *
     * @param context
     * @param domainObject
     * @param busSelectList
     * @param relSelectList
     * @param busWhere
     * @return
     * @throws FrameworkException
     */
    public MapList getIntermediateAtAllLevel(Context context, DomainObject domainObject, StringList busSelectList, StringList relSelectList, String busWhere) throws FrameworkException {
        StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append(pgV3Constants.TYPE_PGCONSUMERUNITPART);
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGINNERPACKUNITPART);
        String types = typeBuilder.toString();
        return domainObject.getRelatedObjects(
                context,                             // Context
                DomainConstants.RELATIONSHIP_EBOM,   // String
                types,                               // String
                false,                            // boolean
                true,                            // boolean
                0,                                // int
                busSelectList,                       // StringList
                relSelectList,                       // StringList
                busWhere,                            // String
                DomainConstants.EMPTY_STRING,        // String
                0,                                // int
                DomainConstants.EMPTY_STRING,        // String
                types,                               // String

                null);                          // Map
    }

    /**
     * Get first level BOM expansion of given object id.
     *
     * @param context
     * @param domainObject
     * @param types
     * @param busSelectList
     * @param relSelectList
     * @param busWhere
     * @return
     * @throws FrameworkException
     */
    public MapList getFirstLevelBOMOfIntermediate(Context context, DomainObject domainObject, String types, StringList busSelectList, StringList relSelectList, String busWhere) throws FrameworkException {
        return domainObject.getRelatedObjects(context,//context user
                pgV3Constants.RELATIONSHIP_EBOM,//rel pattern
                types,//types pattern
                busSelectList,//bus select
                relSelectList,//rel select
                false,//get to
                true,//get from
                (short) 1,//level
                busWhere,//bus where
                "",//rel where
                0);///limit
    }

    /**
     * Get alternates of a given object id in (intermediateChildMap)
     *
     * @param context
     * @param intermediateChildMap
     * @param busSelectList
     * @return
     * @throws FrameworkException
     */
    public MapList getAlternates(Context context, Map<Object, Object> intermediateChildMap, StringList busSelectList) throws FrameworkException {
        String objectOid = (String) intermediateChildMap.get(DomainConstants.SELECT_ID);
        String objectType = (String) intermediateChildMap.get(DomainConstants.SELECT_TYPE);

        final DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        StringList typeList = new StringList();
        if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(objectType)) {
            typeList.addElement(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
        } else {
            typeList.addElement(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
            typeList.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
            typeList.addElement(pgV3Constants.TYPE_FABRICATEDPART);
        }
        return domainObject.getRelatedObjects(
                context,                                    // context user
                DomainConstants.RELATIONSHIP_ALTERNATE,     // relationship
                StringUtil.join(typeList, ","),         // types
                busSelectList,                              // object select
                null,                               // rel select
                false,                                  // get to
                true,                                   // get from
                (short) 1,                                  // level
                "",                                     // object where
                null,                                   // rel where
                0);                                     // count
    }

    /**
     * Get released alternates of a given object id.
     *
     * @param context
     * @param objectOid
     * @param objectType
     * @param busSelectList
     * @return
     * @throws FrameworkException
     */
    public MapList getAlternates(Context context, String objectOid, String objectType, StringList busSelectList) throws FrameworkException {
        final DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        StringList typeList = new StringList();
        if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(objectType)) {
            typeList.addElement(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
        } else {
            typeList.addElement(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
            typeList.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
            typeList.addElement(pgV3Constants.TYPE_FABRICATEDPART);
        }
        String objectWhere = new StringBuilder(DomainConstants.SELECT_CURRENT).append(" == ").append(pgV3Constants.STATE_RELEASE).toString();
        return domainObject.getRelatedObjects(
                context,                                    // context user
                DomainConstants.RELATIONSHIP_ALTERNATE,     // relationship
                StringUtil.join(typeList, ","),         // types
                busSelectList,                              // object select
                null,                               // rel select
                false,                                  // get to
                true,                                   // get from
                (short) 1,                                  // level
                objectWhere,                                     // object where
                null,                                   // rel where
                0);                                     // count
    }

    /**
     * Get alternate with intermediate info.
     *
     * @param context
     * @param intermediateMap
     * @param intermediateChildMap
     * @param busSelectList
     * @return
     * @throws FrameworkException
     */
    public MapList getIncludedAlternate(Context context, Map<Object, Object> intermediateMap, Map<Object, Object> intermediateChildMap, StringList busSelectList) throws FrameworkException {
        MapList retList = new MapList();
        final MapList alternateList = getAlternates(context, intermediateChildMap, busSelectList);
        if (null != alternateList && !alternateList.isEmpty()) {
            final String intermediateChildType = (String) intermediateChildMap.get(DomainConstants.SELECT_TYPE);
            String intermediateChildName = (String) intermediateChildMap.get(DomainConstants.SELECT_NAME);
            String intermediateChildRevision = (String) intermediateChildMap.get(DomainConstants.SELECT_REVISION);
            String intermediateChildID = (String) intermediateChildMap.get(DomainConstants.SELECT_ID);
            String intermediateLevel = (String) intermediateMap.get(DomainConstants.SELECT_LEVEL);
            final Iterator iterator = alternateList.iterator();
            Map<Object, Object> alternateMap;
            while (iterator.hasNext()) {
                alternateMap = (Map<Object, Object>) iterator.next();
                alternateMap.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_ALTERNATE);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_TYPE, intermediateChildType);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_NAME, intermediateChildName);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_REVISION, intermediateChildRevision);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_ID, intermediateChildID);
                alternateMap.put(DomainConstants.SELECT_LEVEL, intermediateLevel);
                retList.add(alternateMap);
            }
        }
        return retList;
    }

    /**
     * Get alternate with intermediate info.
     *
     * @param context
     * @param intermediateMap
     * @param intermediateChildMap
     * @param busSelectList
     * @return
     * @throws FrameworkException
     */
    public MapList getAlternateOfIntermediatesChild(Context context, Map<Object, Object> intermediateMap, Map<Object, Object> intermediateChildMap, StringList busSelectList) throws FrameworkException {
        MapList retList = new MapList();
        final MapList alternateList = getAlternates(context, intermediateChildMap, busSelectList);
        if (null != alternateList && !alternateList.isEmpty()) {
            final String intermediateChildType = (String) intermediateChildMap.get(DomainConstants.SELECT_TYPE);
            String intermediateChildName = (String) intermediateChildMap.get(DomainConstants.SELECT_NAME);
            String intermediateChildRevision = (String) intermediateChildMap.get(DomainConstants.SELECT_REVISION);
            String intermediateChildID = (String) intermediateChildMap.get(DomainConstants.SELECT_ID);
            String intermediateLevel = intermediateMap.containsKey(DomainConstants.SELECT_LEVEL) ? (String) intermediateMap.get(DomainConstants.SELECT_LEVEL) : DomainConstants.EMPTY_STRING;

            final Iterator iterator = alternateList.iterator();
            Map<Object, Object> alternateMap;
            while (iterator.hasNext()) {
                alternateMap = (Map<Object, Object>) iterator.next();
                alternateMap.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_ALTERNATE);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_TYPE, intermediateChildType);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_NAME, intermediateChildName);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_REVISION, intermediateChildRevision);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_ID, intermediateChildID);
                alternateMap.put(DomainConstants.SELECT_LEVEL, intermediateLevel);

                if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase((String) alternateMap.get(DomainConstants.SELECT_CURRENT))) {
                    retList.add(alternateMap);
                }
            }
        }
        return retList;
    }

    /**
     * Get substitute's alternate. (via intermediate)
     *
     * @param context
     * @param intermediateMap
     * @param intermediateChildMap
     * @param substituteList
     * @param busSelectList
     * @return
     * @throws FrameworkException
     */
    public MapList extractSubstituteAlternate(Context context, Map<Object, Object> intermediateMap, Map<Object, Object> intermediateChildMap, MapList substituteList, StringList busSelectList) throws FrameworkException {
        MapList retList = new MapList();
        final Iterator iterator = substituteList.iterator();
        Map<Object, Object> substituteMap;
        String substituteType;
        String substitutePolicy;
        while (iterator.hasNext()) {
            substituteMap = (Map<Object, Object>) iterator.next();
            substituteType = (String) substituteMap.get(DomainConstants.SELECT_TYPE);
            substitutePolicy = (String) substituteMap.get(DomainConstants.SELECT_POLICY);
            if (isEligibleChildType(substituteType, substitutePolicy)) {
                substituteList.add(substituteMap);
                if (isEligibleAlternateType(substituteType)) {
                    substituteList.addAll(extractSubstituteAlternate(context, intermediateMap, substituteMap, busSelectList));
                }
            } else if (!pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(substitutePolicy)) {
                substituteList.add(substituteMap);
            }
        }
        return retList;
    }

    /**
     * Get substitute's alternate.
     *
     * @param context
     * @param intermediateMap
     * @param substituteMap
     * @param busSelectList
     * @return
     * @throws FrameworkException
     */
    public MapList extractSubstituteAlternate(Context context, Map<Object, Object> intermediateMap, Map<Object, Object> substituteMap, StringList busSelectList) throws FrameworkException {
        MapList retList = new MapList();
        final MapList alternateList = getAlternates(context, substituteMap, busSelectList);
        if (null != alternateList && !alternateList.isEmpty()) {
            final String intermediateChildType = (String) substituteMap.get(DomainConstants.SELECT_TYPE);
            String intermediateChildName = (String) substituteMap.get(DomainConstants.SELECT_NAME);
            String intermediateChildRevision = (String) substituteMap.get(DomainConstants.SELECT_REVISION);
            String intermediateChildID = (String) substituteMap.get(DomainConstants.SELECT_ID);
            String intermediateLevel = (String) intermediateMap.get(DomainConstants.SELECT_LEVEL);
            final Iterator iterator = alternateList.iterator();
            Map<Object, Object> alternateMap;
            while (iterator.hasNext()) {
                alternateMap = (Map<Object, Object>) iterator.next();
                alternateMap.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_ALTERNATE);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_TYPE, intermediateChildType);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_NAME, intermediateChildName);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_REVISION, intermediateChildRevision);
                alternateMap.put(pgV3Constants.KEY_INTERMEDIATE_ID, intermediateChildID);
                alternateMap.put(DomainConstants.SELECT_LEVEL, intermediateLevel);
                if (pgV3Constants.STATE_RELEASE.equalsIgnoreCase((String) alternateMap.get(DomainConstants.SELECT_CURRENT))) {
                    retList.add(alternateMap);
                }
            }
        }
        return retList;
    }

    /**
     * Extract substitute from a given map.
     *
     * @param intermediateChildMap
     * @return
     */
    public MapList extractSubstitutes(Map<Object, Object> intermediateChildMap) {
        MapList substituteList = new MapList();
        if (intermediateChildMap.containsKey(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID)) {

            // extract substitute from intermediate's child
            StringList iDList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
            StringList typeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
            StringList nameList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
            StringList revisionList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
            StringList stateList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
            StringList policyList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_POLICY);

            StringList subAssemblyTypeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_ASSEMBLY_TYPE);
            StringList sapTypeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_SAP_TYPE);
            StringList titleList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TITLE);
            StringList relIDList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);

            Map<Object, Object> substituteMap;
            String substituteType;
            String substitutePolicy;
            for (int i = 0; i < iDList.size(); i++) {
                substituteMap = new HashMap<>();
                substituteType = typeList.get(i);
                substitutePolicy = policyList.get(i);

                substituteMap.put(DomainConstants.SELECT_ID, iDList.get(i));
                substituteMap.put(DomainConstants.SELECT_TYPE, substituteType);
                substituteMap.put(DomainConstants.SELECT_NAME, nameList.get(i));
                substituteMap.put(DomainConstants.SELECT_REVISION, revisionList.get(i));
                substituteMap.put(DomainConstants.SELECT_CURRENT, stateList.get(i));
                substituteMap.put(DomainConstants.SELECT_POLICY, substitutePolicy);
                substituteMap.put(DomainConstants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, relIDList.get(i));
                substituteMap.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);

                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE, sapTypeList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE, subAssemblyTypeList.get(i));
                substituteList.add(substituteMap);
            }
        }
        return substituteList;
    }

    /**
     * Extract substitute from a given map with intermediate.
     *
     * @param intermediateMap
     * @param intermediateChildMap
     * @return
     * @throws FrameworkException
     */
    public MapList extractSubstitutesOfIntermediatesChild(Map<Object, Object> intermediateMap, Map<Object, Object> intermediateChildMap) throws FrameworkException {
        MapList substituteList = new MapList();
        if (intermediateChildMap.containsKey(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID)) {

            // extract intermediate info
            String intermediateName = (String) intermediateMap.get(DomainConstants.SELECT_NAME);
            String intermediateType = (String) intermediateMap.get(DomainConstants.SELECT_TYPE);
            String intermediateRevision = (String) intermediateMap.get(DomainConstants.SELECT_REVISION);
            String intermediateID = (String) intermediateMap.get(DomainConstants.SELECT_ID);
            String intermediateLevel = (String) intermediateMap.get(DomainConstants.SELECT_LEVEL);

            // extract substitute from intermediate's child
            StringList iDList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
            StringList typeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
            StringList nameList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
            StringList revisionList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
            StringList stateList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
            StringList policyList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_POLICY);

            StringList subAssemblyTypeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_ASSEMBLY_TYPE);
            StringList sapTypeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_SAP_TYPE);
            StringList titleList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TITLE);
            StringList relIDList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);

            Map<Object, Object> substituteMap;
            String substituteType;
            String substitutePolicy;
            for (int i = 0; i < iDList.size(); i++) {
                substituteMap = new HashMap<>();
                substituteType = typeList.get(i);
                substitutePolicy = policyList.get(i);

                substituteMap.put(DomainConstants.SELECT_ID, iDList.get(i));
                substituteMap.put(DomainConstants.SELECT_TYPE, substituteType);
                substituteMap.put(DomainConstants.SELECT_NAME, nameList.get(i));
                substituteMap.put(DomainConstants.SELECT_REVISION, revisionList.get(i));
                substituteMap.put(DomainConstants.SELECT_CURRENT, stateList.get(i));
                substituteMap.put(DomainConstants.SELECT_POLICY, substitutePolicy);
                substituteMap.put(DomainConstants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, relIDList.get(i));
                substituteMap.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);

                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_ID, intermediateID);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_TYPE, intermediateType);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_NAME, intermediateName);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_REVISION, intermediateRevision);
                substituteMap.put(DomainConstants.SELECT_LEVEL, intermediateLevel);

                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE, sapTypeList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE, subAssemblyTypeList.get(i));
                substituteList.add(substituteMap);
            }
        }
        return substituteList;
    }

    /**
     * Added by (DSM) Sogeti for 22x.02 - (May CW) - REQ 46280
     *
     * @param type
     * @return
     */
    public boolean isAllowedIntermediateTypeForSubstituteExpansion(String type) {
        StringList intermediateTypeList = new StringList();
        intermediateTypeList.add(pgV3Constants.TYPE_PGCONSUMERUNITPART);
        intermediateTypeList.add(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
        intermediateTypeList.add(pgV3Constants.TYPE_PGINNERPACKUNITPART);
        return intermediateTypeList.contains(type);
    }

    /**
     * Added by (DSM) Sogeti for 22x.02 - (May CW) - REQ 46280
     *
     * @param intermediateMap
     * @return
     */
    public MapList extractSubstitutesOfIntermediate(Map<Object, Object> intermediateMap) {
        MapList substituteList = new MapList();
        if (intermediateMap.containsKey(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID)) {

            // extract intermediate info
            String intermediateName = (String) intermediateMap.get(DomainConstants.SELECT_NAME);
            String intermediateType = (String) intermediateMap.get(DomainConstants.SELECT_TYPE);
            String intermediateRevision = (String) intermediateMap.get(DomainConstants.SELECT_REVISION);
            String intermediateID = (String) intermediateMap.get(DomainConstants.SELECT_ID);
            String intermediateLevel = (String) intermediateMap.get(DomainConstants.SELECT_LEVEL);

            // extract substitute from intermediate's child
            StringList iDList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
            StringList typeList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
            StringList nameList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
            StringList revisionList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
            StringList stateList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
            StringList policyList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_POLICY);

            StringList subAssemblyTypeList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_ASSEMBLY_TYPE);
            StringList sapTypeList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_SAP_TYPE);
            StringList titleList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TITLE);
            StringList relIDList = getStringListFromMap(intermediateMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);

            Map<Object, Object> substituteMap;
            String substituteType;
            String substitutePolicy;
            for (int i = 0; i < iDList.size(); i++) {
                substituteMap = new HashMap<>();
                substituteType = typeList.get(i);
                substitutePolicy = policyList.get(i);

                substituteMap.put(DomainConstants.SELECT_ID, iDList.get(i));
                substituteMap.put(DomainConstants.SELECT_TYPE, substituteType);
                substituteMap.put(DomainConstants.SELECT_NAME, nameList.get(i));
                substituteMap.put(DomainConstants.SELECT_REVISION, revisionList.get(i));
                substituteMap.put(DomainConstants.SELECT_CURRENT, stateList.get(i));
                substituteMap.put(DomainConstants.SELECT_POLICY, substitutePolicy);
                substituteMap.put(DomainConstants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, relIDList.get(i));
                substituteMap.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);

                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_ID, intermediateID);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_TYPE, intermediateType);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_NAME, intermediateName);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_REVISION, intermediateRevision);
                substituteMap.put(DomainConstants.SELECT_LEVEL, intermediateLevel);

                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE, sapTypeList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE, subAssemblyTypeList.get(i));
                substituteList.add(substituteMap);
            }
        }
        return substituteList;
    }


    /**
     * Intermediate method to extract substitute & substitute alternate from a given map with intermediate.
     *
     * @param context
     * @param intermediateMap
     * @param intermediateChildMap
     * @param busSelectList
     * @return
     * @throws FrameworkException
     */
    private MapList extractSubstitutesAndSubstituteAlternate(Context context, Map<Object, Object> intermediateMap, Map<Object, Object> intermediateChildMap, StringList busSelectList) throws FrameworkException {
        MapList substituteList = new MapList();
        if (intermediateChildMap.containsKey(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID)) {

            // extract intermediate info
            String intermediateName = (String) intermediateMap.get(DomainConstants.SELECT_NAME);
            String intermediateType = (String) intermediateMap.get(DomainConstants.SELECT_TYPE);
            String intermediateRevision = (String) intermediateMap.get(DomainConstants.SELECT_REVISION);
            String intermediateID = (String) intermediateMap.get(DomainConstants.SELECT_ID);
            String intermediateLevel = (String) intermediateMap.get(DomainConstants.SELECT_LEVEL);

            // extract substitute from intermediate's child
            StringList iDList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
            StringList typeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
            StringList nameList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
            StringList revisionList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
            StringList stateList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
            StringList policyList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_POLICY);

            StringList subAssemblyTypeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_ASSEMBLY_TYPE);
            StringList sapTypeList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ATTRIBUTE_SAP_TYPE);
            StringList titleList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TITLE);
            StringList relIDList = getStringListFromMap(intermediateChildMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);

            Map<Object, Object> substituteMap;
            String substituteType;
            String substitutePolicy;
            for (int i = 0; i < iDList.size(); i++) {
                substituteMap = new HashMap<>();
                substituteType = typeList.get(i);
                substitutePolicy = policyList.get(i);

                substituteMap.put(DomainConstants.SELECT_ID, iDList.get(i));
                substituteMap.put(DomainConstants.SELECT_TYPE, substituteType);
                substituteMap.put(DomainConstants.SELECT_NAME, nameList.get(i));
                substituteMap.put(DomainConstants.SELECT_REVISION, revisionList.get(i));
                substituteMap.put(DomainConstants.SELECT_CURRENT, stateList.get(i));
                substituteMap.put(DomainConstants.SELECT_POLICY, substitutePolicy);
                substituteMap.put(DomainConstants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, relIDList.get(i));
                substituteMap.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);

                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_ID, intermediateID);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_TYPE, intermediateType);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_NAME, intermediateName);
                substituteMap.put(pgV3Constants.KEY_INTERMEDIATE_REVISION, intermediateRevision);
                substituteMap.put(DomainConstants.SELECT_LEVEL, intermediateLevel);

                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, titleList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE, sapTypeList.get(i));
                substituteMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE, subAssemblyTypeList.get(i));

                if (isEligibleChildType(substituteType, substitutePolicy)) {
                    substituteList.add(substituteMap);
                    if (isEligibleAlternateType(substituteType)) {
                        substituteList.addAll(extractSubstituteAlternate(context, intermediateMap, substituteMap, busSelectList));
                    }
                } else if (!pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(substitutePolicy)) {
                    substituteList.add(substituteMap);
                }
            }
        }
        return substituteList;
    }

    /**
     * Method to extract stringlist from a given map and key.
     *
     * @param dataMap
     * @param selectable
     * @return
     */
    private StringList getStringListFromMap(Map<Object, Object> dataMap, String selectable) {
        Object resList = (dataMap).get(selectable);
        StringList retList = new StringList();
        if (null != resList) {
            if (resList instanceof StringList) {
                retList = (StringList) resList;
            } else {
                retList.add(resList.toString());
            }
        }
        return retList;
    }

    /**
     * @param intermediateMap
     * @param intermediateChildMap
     * @return
     */
    public Map<Object, Object> getIntermediateParentChildStructure(Map<Object, Object> intermediateMap, Map<Object, Object> intermediateChildMap) {
        final String intermediateChildType = (String) intermediateChildMap.get(DomainConstants.SELECT_TYPE);
        final String intermediateChildPolicy = (String) intermediateChildMap.get(DomainConstants.SELECT_POLICY);
        Map<Object, Object> newMap = new HashMap<>(intermediateChildMap);
        if (isEligibleChildType(intermediateChildType, intermediateChildPolicy)) {
            String intermediateName = (String) intermediateMap.get(DomainConstants.SELECT_NAME);
            String intermediateType = (String) intermediateMap.get(DomainConstants.SELECT_TYPE);
            String intermediateRevision = (String) intermediateMap.get(DomainConstants.SELECT_REVISION);
            String intermediateID = (String) intermediateMap.get(DomainConstants.SELECT_ID);
            intermediateChildMap.put(pgV3Constants.KEY_INTERMEDIATE_ID, intermediateID);
            intermediateChildMap.put(pgV3Constants.KEY_INTERMEDIATE_TYPE, intermediateType);
            intermediateChildMap.put(pgV3Constants.KEY_INTERMEDIATE_NAME, intermediateName);
            intermediateChildMap.put(pgV3Constants.KEY_INTERMEDIATE_REVISION, intermediateRevision);
        }
        return intermediateChildMap;
    }

    /**
     * Method to check if type & policy are qualified.
     *
     * @param strType
     * @param strPolicy
     * @return
     */
    public boolean isEligibleChildType(String strType, String strPolicy) {
        boolean isEligible = false;
        if (!pgV3Constants.TYPE_PGPHASE.equalsIgnoreCase(strType)
                && !pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strType)
                && !pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strType)
                && !pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strType)
                && !pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART.equalsIgnoreCase(strType)
                && !pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equalsIgnoreCase(strType)
                && !pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(strPolicy)) {
            isEligible = true;
        }
        return isEligible;
    }

    /**
     * Method to check qualified alternate type.
     *
     * @param strType
     * @return
     */
    public boolean isEligibleAlternateType(String strType) {
        boolean isEligible = false;
        if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strType)) {
            isEligible = true;
        }
        return isEligible;
    }

    /**
     * Method to get object selects.
     *
     * @return
     */
    public StringList getBusSelects() {
        StringList selectList = new StringList(12);

        // basic selectables.
        selectList.addElement(DomainConstants.SELECT_ID);
        selectList.addElement(DomainConstants.SELECT_TYPE);
        selectList.addElement(DomainConstants.SELECT_NAME);
        selectList.addElement(DomainConstants.SELECT_REVISION);
        selectList.addElement(DomainConstants.SELECT_CURRENT);
        selectList.addElement(DomainConstants.SELECT_POLICY);

        // attribute selectables.
        selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
        selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
        selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        return selectList;
    }

    /**
     * Method to get rel selects.
     *
     * @return
     */
    public StringList getRelSelects() {
        StringList selectList = new StringList(15);
        selectList.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_POLICY);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TITLE);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_SAP_TYPE);
        selectList.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ASSEMBLY_TYPE);
        selectList.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);
        return selectList;
    }

    /**
     * Method to get BOM of substitute intermediate.
     *
     * @param context
     * @param domainObject
     * @param objectWhere
     * @return
     * @throws FrameworkException
     */
    public MapList getSubstituteIntermediateBOM(Context context, DomainObject domainObject, String objectWhere) throws FrameworkException {
        return domainObject.getRelatedObjects(context,//context user
                pgV3Constants.RELATIONSHIP_EBOM,//relationship
                DomainConstants.QUERY_WILDCARD,//type pattern
                getBusSelects(),//bus select
                getRelSelects(),//rel select
                false,//get to
                true,//get from
                (short) 1,//level
                objectWhere,//object where
                "",//rel where
                0);//limit
    }

    /**
     * Method to get object info.
     *
     * @param context
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    private Map<Object, Object> getObjectInfo(Context context, DomainObject domainObject) throws FrameworkException {
        StringList objectSelectList = new StringList(4);
        objectSelectList.addElement(DomainConstants.SELECT_TYPE);
        objectSelectList.addElement(DomainConstants.SELECT_NAME);
        objectSelectList.addElement(DomainConstants.SELECT_REVISION);
        objectSelectList.addElement(DomainConstants.SELECT_ID);
        return domainObject.getInfo(context, objectSelectList);
    }

    /**
     * Method to get first level BOM only.
     *
     * @param context
     * @param domainObject
     * @param objectWhere
     * @return
     * @throws FrameworkException
     */
    public MapList getFirstLevelBOM(Context context, DomainObject domainObject, String objectWhere) throws FrameworkException {
        return domainObject.getRelatedObjects(context,//context user
                pgV3Constants.RELATIONSHIP_EBOM,//relationship pattern
                DomainConstants.QUERY_WILDCARD, //type pattern
                getBusSelects(),//bus select
                getRelSelects(),//rel select
                false,//get to
                true,//get from
                (short) 1,//level
                objectWhere,//bus where
                DomainConstants.EMPTY_STRING,//rel where
                0);//limit
    }

}

