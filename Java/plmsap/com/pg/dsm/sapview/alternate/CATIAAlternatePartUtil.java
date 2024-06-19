package com.pg.dsm.sapview.alternate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.sapview.beans.bo.Part;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.StringList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CATIAAlternatePartUtil {
    /**
     * Constructor
     *
     * @since DSM 2018x.5
     */
    public CATIAAlternatePartUtil() {
    }

    /**
     * Method to generate Alternate Bean
     *
     * @param tempMap - Map
     * @return List<Part> - List of Part beans
     * @since DSM 2018x.5
     */
    public List<Part> getAlternateBean(Map tempMap) {
        List<Part> alternateList = new ArrayList<>();
		boolean hasAlternate = Boolean.valueOf((String) tempMap.get(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXISTS));
        if (hasAlternate) {
			StringList idList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_ID);
            StringList typeList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_TYPE);
            StringList nameList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_NAME);
            StringList revisionList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_REVISION);
            StringList currentList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_CURRENT);
            StringList policyList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_POLICY);
            StringList assemblyTypeList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_ASSEMBLY_TYPE);

            ObjectMapper objectMapper;
            Map newMap;
            int size = idList.size();
            for (int i = 0; i < size; i++) {
                newMap = new HashMap();
                newMap.put(DomainConstants.SELECT_ID, (String) idList.get(i));
                newMap.put(DomainConstants.SELECT_TYPE, (String) typeList.get(i));
                newMap.put(DomainConstants.SELECT_NAME, (String) nameList.get(i));
                newMap.put(DomainConstants.SELECT_REVISION, (String) revisionList.get(i));
                newMap.put(DomainConstants.SELECT_CURRENT, (String) currentList.get(i));
                newMap.put(DomainConstants.SELECT_POLICY, (String) policyList.get(i));
                newMap.put(SAPViewConstant.IDENTIFIER_ASSEMBLY_TYPE.getValue(), (String) assemblyTypeList.get(i));
                objectMapper = new ObjectMapper();
                alternateList.add(objectMapper.convertValue(newMap, Part.class));
            }
        }
        return alternateList;
    }
    /**
     * Method to generate EBOM Parent Bean
     *
     * @param tempMap - Map
     * @return List<Part> - List of Part beans
     * @since DSM 2018x.5
     */
    public List<Part> getEBOMParentBean(Map tempMap) {
        List<Part> parentList = new ArrayList<>();
        if (null != tempMap) {
			boolean hasEBOM = Boolean.valueOf((String) tempMap.get(pgV3Constants.SELECT_CATIA_APP_EBOM_EXISTS));
            if (hasEBOM) {
                StringList idList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_ID);
                StringList typeList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_TYPE);
                StringList nameList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_NAME);
                StringList revisionList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_REVISION);
                StringList currentList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_CURRENT);
                StringList policyList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_POLICY);
                StringList assemblyTypeList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_ASSEMBLY_TYPE);
                ObjectMapper objectMapper;
                Map newMap;
                int size = idList.size();
                for (int i = 0; i < size; i++) {
                    newMap = new HashMap();
                    newMap.put(DomainConstants.SELECT_ID, (String) idList.get(i));
                    newMap.put(DomainConstants.SELECT_TYPE, (String) typeList.get(i));
                    newMap.put(DomainConstants.SELECT_NAME, (String) nameList.get(i));
                    newMap.put(DomainConstants.SELECT_REVISION, (String) revisionList.get(i));
                    newMap.put(DomainConstants.SELECT_CURRENT, (String) currentList.get(i));
                    newMap.put(DomainConstants.SELECT_POLICY, (String) policyList.get(i));
                    newMap.put(SAPViewConstant.IDENTIFIER_ASSEMBLY_TYPE.getValue(), (String) assemblyTypeList.get(i));
                    objectMapper = new ObjectMapper();
                    parentList.add(objectMapper.convertValue(newMap, Part.class));
                }
            }
        }
        return parentList;
    }
    /**
     * Method to generate EBOM Substitute Bean
     *
     * @param tempMap - Map
     * @return List<Part> - List of Part beans
     * @since DSM 2018x.5
     */
    public List<Part> getParentEBOMSubstituteBean(Map tempMap) {
        List<Part> substituteList = new ArrayList<>();
        boolean hasEBOMSubstitute = Boolean.valueOf(getStringFromStringList(tempMap));
        if (hasEBOMSubstitute) {
			StringList typeList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_TYPE);
            StringList nameList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_NAME);
            StringList revisionList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_REVISION);
            StringList currentList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_CURRENT);
            StringList policyList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_POLICY);
            StringList idList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_ID);
            StringList assemblyTypeList = getStringListFromInstance(tempMap, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_ASSEMBLY_TYPE);

            ObjectMapper objectMapper;
            Map newMap;
            int size = idList.size();
            for (int i = 0; i < size; i++) {
                newMap = new HashMap();
                newMap.put(DomainConstants.SELECT_ID, (String) idList.get(i));
                newMap.put(DomainConstants.SELECT_TYPE, (String) typeList.get(i));
                newMap.put(DomainConstants.SELECT_NAME, (String) nameList.get(i));
                newMap.put(DomainConstants.SELECT_REVISION, (String) revisionList.get(i));
                newMap.put(DomainConstants.SELECT_POLICY, (String) policyList.get(i));
                newMap.put(DomainConstants.SELECT_CURRENT, (String) currentList.get(i));
                newMap.put(SAPViewConstant.IDENTIFIER_ASSEMBLY_TYPE.getValue(), (String) assemblyTypeList.get(i));
                objectMapper = new ObjectMapper();
                substituteList.add(objectMapper.convertValue(newMap, Part.class));
            }
        }
        return substituteList;
    }

    /**
     * Method to get StringList from Object
     *
     * @param map - Map
     * @param key - String
     * @return StringList - list of strings.
     * @since DSM 2018x.5
     */
    public StringList getStringListFromInstance(Map map, String key) {
        StringList stringList = new StringList();
        Object o = map.get(key);
        if (o instanceof StringList) {
            stringList = (StringList) map.get(key);
        } else {
            stringList.addElement((String) map.get(key));
        }
        return stringList;
    }

    /**
     * Method to get String from StringList
     *
     * @param tempMap - Map
     * @return String - string
     * @since DSM 2018x.5
     */
    public String getStringFromStringList(Map tempMap) {
        String retStr = DomainConstants.EMPTY_STRING;
        Object object = tempMap.get(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXISTS);
        if (object instanceof StringList) {
            StringList stringList = (StringList) tempMap.get(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXISTS);
            retStr = stringList.get(0);
        } else {
            retStr = (String) tempMap.get(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXISTS);
        }
        return retStr;
    }

    /**
     * Method to get EBOM Substitute selectable
     *
     * @return StringList - list of string
     * @since DSM 2018x.5
     */
    public StringList getExpansionEBOMSubstituteSelectable() {

        StringList typeSelects = new StringList(9);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXISTS);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_ID);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_TYPE);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_NAME);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_REVISION);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_CURRENT);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_POLICY);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_PREVIOUS);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_ASSEMBLY_TYPE);
        return typeSelects;
    }
    /**
     * Method to get EBOM selectable
     *
     * @return StringList - list of string
     * @since DSM 2018x.5
     */
    public StringList getExpansionEBOMSelectable() {
		
        StringList typeSelects = new StringList(9);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXISTS);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_ID);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_TYPE);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_NAME);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_REVISION);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_CURRENT);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_POLICY);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_PREVIOUS);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_EBOM_EXPAND_ASSEMBLY_TYPE);
        return typeSelects;
    }
    /**
     * Method to get Alternate selectable
     *
     * @return StringList - list of string
     * @since DSM 2018x.5
     */
    public StringList getExpansionAlternateSelectable() {
	
        StringList typeSelects = new StringList(9);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXISTS);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_ID);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_TYPE);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_NAME);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_REVISION);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_CURRENT);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_POLICY);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_PREVIOUS);
        typeSelects.addElement(pgV3Constants.SELECT_CATIA_APP_ALTERNATE_EXPAND_ASSEMBLY_TYPE);
        return typeSelects;
    }

    /**
     * Method to get selectable for Alternate expansion.
     *
     * @return StringList - list of string
     * @since DSM 2018x.5
     */
    public StringList getExpandAlternateSelectable() {
        StringList typeSelects = new StringList();
        typeSelects.addElement(DomainConstants.SELECT_ID);
        typeSelects.addElement(DomainConstants.SELECT_TYPE);
        typeSelects.addElement(DomainConstants.SELECT_NAME);
        typeSelects.addElement(DomainConstants.SELECT_REVISION);
        typeSelects.addElement(DomainConstants.SELECT_CURRENT);
        typeSelects.addElement(DomainConstants.SELECT_POLICY);
        typeSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        typeSelects.addAll(getExpansionEBOMSubstituteSelectable());
        typeSelects.addAll(getExpansionEBOMSelectable());
        typeSelects.addAll(getExpansionAlternateSelectable());
        return typeSelects;
    }

    /**
     * Method to expand alternate.
     *
     * @param context - Context
     * @param dObj - DomainObject
     * @return MapList - map list
     * @since DSM 2018x.5
     */
    public MapList getAlternateRelatedObjects(Context context, DomainObject dObj) throws FrameworkException {
        return dObj.getRelatedObjects(
                context,
                DomainConstants.RELATIONSHIP_ALTERNATE, // rel pattern
                DomainConstants.QUERY_WILDCARD, 		// type pattern
                getExpandAlternateSelectable(),			// object select			
                DomainConstants.EMPTY_STRINGLIST,		// rel select
                true,									// to side
                false,									// from side 
                (short) 1,								// recurseToLevel
                DomainConstants.EMPTY_STRING,			// objectWhere
                DomainConstants.EMPTY_STRING, 			// relationshipWhere
                (short) 0,								// limit
                null,									// Pattern includeType
                null, 									// Pattern includeRelationship
                null); 									// includeMap
    }

    /**
     * Method to expand EBOM.
     *
     * @param context - Context
     * @param dObj - DomainObject
     * @param busSelects - StringList
     * @return MapList - map list
     * @since DSM 2018x.5
     */
    public MapList getEBOMRelatedObjects(Context context, DomainObject dObj, StringList busSelects) throws FrameworkException {
        return dObj.getRelatedObjects(
                context,
                DomainConstants.RELATIONSHIP_EBOM, 		// rel pattern
                DomainConstants.QUERY_WILDCARD,			// type pattern
                busSelects,								// object select
                DomainConstants.EMPTY_STRINGLIST,		// rel select
                true,									// to side
                false,									// from side 
                (short) 0,								// recurseToLevel
                DomainConstants.EMPTY_STRING,			// objectWhere
                DomainConstants.EMPTY_STRING,			// relationshipWhere
                (short) 0,								// limit
                null,									// Pattern includeType
                null,									// Pattern includeRelationshi
                null);									// includeMap
    }
    /**
     * Method to get info of an object.
     *
     * @param context - Context
     * @param dObjAlternate - DomainObject
     * @return MapList - map list
     * @since DSM 2018x.5
     */
    public Map getAlternateInfoMap(Context context, DomainObject dObjAlternate) throws FrameworkException {
        Map infoMap = new HashMap();
        StringList busSelects = new StringList(6);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_REVISION);
        busSelects.addElement(DomainConstants.SELECT_CURRENT);
        busSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
        busSelects.addElement(pgV3Constants.SELECT_PREVIOUS_ID);
        try {
            infoMap = dObjAlternate.getInfo(context, busSelects);
        } catch (FrameworkException e) {
            e.printStackTrace();
        }
        return infoMap;
    }
    /**
     * Method to get EBOM Parents into a bean.
     *
     * @param context - Context
     * @param dObj - DomainObject
     * @return MapList - map list
     * @since DSM 2018x.5
     */
    public MapList getExpandEBOMParents(Context context, DomainObject dObj) throws FrameworkException {

        StringList busSelects = new StringList(7);
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_REVISION);
        busSelects.addElement(DomainConstants.SELECT_CURRENT);
        busSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        busSelects.addAll(getExpansionEBOMSubstituteSelectable());

        MapList expandEBOMParents = getEBOMRelatedObjects(context, dObj, busSelects);
        if (null != expandEBOMParents) {
            Map tempMap;
            for (Object eachObj : expandEBOMParents) {
                tempMap = (Map) eachObj;
                tempMap.put(SAPViewConstant.SUBSTITUTE_LIST.getValue(), getParentEBOMSubstituteBean(tempMap));
            }
        }
        return expandEBOMParents;
    }
    /**
     * Method to get EBOM Substitute into a bean.
     *
     * @param context - Context
     * @param objectId - String
     * @return List<Part>  - part list
     * @since DSM 2018x.5
     */
    public List<Part> getParentEBOMSubstituteBean(Context context, String objectId) throws FrameworkException {
        List<Part> partList = new ArrayList<>();
        StringList typeSelects = new StringList();
        typeSelects.addAll(getExpansionEBOMSubstituteSelectable());
        Map infoMap = BusinessUtil.getInfoList(context, objectId, typeSelects);
        if (null != infoMap) {
            partList = getParentEBOMSubstituteBean(infoMap);
        }
        return partList;
    }
    /**
     * Method to get EBOM Substitute into a bean.
     *
     * @param tempMap - Map
     * @return Part  - bean.
     * @since DSM 2018x.5
     */
    public Part getPartBean(Map tempMap) {
        Map newMap = new HashMap();
        newMap.put(DomainConstants.SELECT_ID, (String) tempMap.get(DomainConstants.SELECT_ID));
        newMap.put(DomainConstants.SELECT_TYPE, (String) tempMap.get(DomainConstants.SELECT_TYPE));
        newMap.put(DomainConstants.SELECT_NAME, (String) tempMap.get(DomainConstants.SELECT_NAME));
        newMap.put(DomainConstants.SELECT_REVISION, (String) tempMap.get(DomainConstants.SELECT_REVISION));
        newMap.put(DomainConstants.SELECT_POLICY, (String) tempMap.get(DomainConstants.SELECT_POLICY));
        newMap.put(DomainConstants.SELECT_CURRENT, (String) tempMap.get(DomainConstants.SELECT_CURRENT));
        newMap.put(SAPViewConstant.IDENTIFIER_ASSEMBLY_TYPE.getValue(), (String) tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(newMap, Part.class);
    }

}
