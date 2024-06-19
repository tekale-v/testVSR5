package com.pg.dra.structure_copy.util;

import static com.pg.v3.custom.pgV3Constants.TYPE_PG_CONSUMERUNIT;
import static com.pg.v3.custom.pgV3Constants.VAULT_ESERVICEPRODUCTION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dra.structure_copy.models.Part;
import com.pg.dra.structure_copy.repository.StructureLoader;

import matrix.db.Context;
import matrix.util.StringList;

public class StructureCopyUtil {

    /**
     * @param part
     * @param id
     * @param parentId
     * @return
     */
    public String getRelIdByPartAndParentId(Part part, String id, String parentId) {
        String relID = DomainConstants.EMPTY_STRING;
        Part foundPart = findPartByIdAndParentId(part, id, parentId);
        if (null != foundPart) {
            relID = foundPart.getRelId();
        }
        return relID;
    }

    /**
     * @param part
     * @param id
     * @param parentId
     * @return
     */
    public Part findPartByIdAndParentId(Part part, String id, String parentId) {
        return findPartByIdAndParentIdRecursive(part, id, parentId);
    }

    /**
     * @param currentPart
     * @param id
     * @param parentId
     * @return
     */
    private Part findPartByIdAndParentIdRecursive(Part currentPart, String id, String parentId) {
        if (currentPart.getId().equals(id) && currentPart.getParent().getId().equals(parentId)) {
            return currentPart;
        }
        for (Part child : currentPart.getChildren()) {
            Part foundPart = findPartByIdAndParentIdRecursive(child, id, parentId);
            if (foundPart != null) {
                return foundPart;
            }
        }
        return null;
    }


    /**
     * @param part
     * @param id
     * @return
     */
    public String getRelIdByPartId(Part part, String id) {
        String relID = DomainConstants.EMPTY_STRING;
        Part foundPart = findPartById(part, id);

        if (null != foundPart) {
            relID = foundPart.getRelId();
        }
        return relID;
    }

    /**
     * @param part
     * @param id
     * @return
     */
    public Part findPartById(Part part, String id) {
        return findPartByIdRecursive(part, id);
    }

    /**
     * @param currentPart
     * @param id
     * @return
     */
    private Part findPartByIdRecursive(Part currentPart, String id) {
        if (currentPart.getId().equals(id)) {
            return currentPart;
        }
        for (Part child : currentPart.getChildren()) {
            Part foundPart = findPartByIdRecursive(child, id);
            if (foundPart != null) {
                return foundPart;
            }
        }
        return null;
    }

    /**
     * Original: validateCOPBOM (StringList)
     *
     * @param context
     * @param inputPartNameList
     * @param inputFPPNameList
     * @return
     * @throws FrameworkException
     */
    public List<String> validateCOPBOM(Context context, StringList inputPartNameList, StringList inputFPPNameList) throws FrameworkException {
        List<String> relIDs = new ArrayList<>();
        String inputFPPName = "";
        if (null != inputFPPNameList && !inputFPPNameList.isEmpty()) {
            inputFPPName = inputFPPNameList.get(0);
        }
        String objectWhere = "revision==last.revision";
        if (UIUtil.isNotNullAndNotEmpty(inputFPPName)) {
            Map<Object, Object> fppInfoMap = getPartInfoByName(context, inputFPPName, objectWhere);
            if (null != fppInfoMap && !fppInfoMap.isEmpty()) {
                String inputFPPID = (String) fppInfoMap.get(DomainConstants.SELECT_ID);
                if (UIUtil.isNotNullAndNotEmpty(inputFPPID)) {
                    if (null != inputPartNameList && !inputPartNameList.isEmpty()) {
                        MapList infoList = getPartInfoListByNameList(context, inputPartNameList, objectWhere);
                        if (null != infoList && !infoList.isEmpty()) {
                            StructureLoader structureLoader = new StructureLoader.Load(context).now(inputFPPID);
                            if (structureLoader.isLoaded()) {
                                Part part = structureLoader.getPart(); // complete BOM (top level node)
                                if (null != part) {
                                    relIDs.addAll(getCOPinCOPRelationshipIDs(part, infoList));
                                }
                            }
                        }
                    }
                }
            }
        }
        return relIDs;
    }

    /**
     * @param part
     * @param infoList
     * @return
     */
    public List<String> getCOPinCOPRelationshipIDs(Part part, MapList infoList) {
        List<String> resultList = new ArrayList<>();
        Map<Object, Object> tempMap;
        String partType;
        String partID;
        String partParentID = DomainConstants.EMPTY_STRING;
        String relID;
        for (int i = 0; i < infoList.size(); i++) {
            tempMap = (Map<Object, Object>) infoList.get(i);
            if (null != tempMap && !tempMap.isEmpty()) {
                partID = (String) tempMap.get(DomainConstants.SELECT_ID);
                partType = (String) tempMap.get(DomainConstants.SELECT_TYPE);
                if (partType.equalsIgnoreCase(TYPE_PG_CONSUMERUNIT)) {
                    if (i > 0) {
                        partParentID = (String) ((Map<Object, Object>) infoList.get(i - 1)).get(DomainConstants.SELECT_ID);
                    }
                    StructureCopyUtil structureCopyUtil = new StructureCopyUtil();
                    relID = structureCopyUtil.getRelIdByPartAndParentId(part, partID, partParentID);
                    if (UIUtil.isNotNullAndNotEmpty(relID)) {
                        resultList.add(relID);
                    }
                }
            }
        }
        return resultList;
    }


    /**
     * @param context
     * @param nameList
     * @param objectWhere
     * @return
     * @throws FrameworkException
     */
    public MapList getPartInfoListByNameList(Context context, StringList nameList, String objectWhere) throws FrameworkException {
        MapList resultList = new MapList();
        MapList tempList;
        for (String name : nameList) {
            resultList.add(getPartInfoByName(context, name, objectWhere));
        }
        return resultList;
    }

    /**
     * @param context
     * @param name
     * @param objectWhere
     * @return
     * @throws FrameworkException
     */
    public Map<Object, Object> getPartInfoByName(Context context, String name, String objectWhere) throws FrameworkException {
        Map<Object, Object> resultMap = new HashMap<>();
        MapList objectList = DomainObject.findObjects(context,
                DomainConstants.TYPE_PART,                  //type pattern
                name.trim(),                                //name pattern
                DomainConstants.QUERY_WILDCARD,             //revision pattern
                DomainConstants.QUERY_WILDCARD,             //owner pattern
                VAULT_ESERVICEPRODUCTION,                   //vault pattern
                objectWhere,                                //where clause
                true,                                       //expand sub-type
                StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_TYPE, DomainConstants.SELECT_NAME));//object selectables

        if (null != objectList && !objectList.isEmpty()) {
            resultMap = ((Map<Object, Object>) objectList.get(0));
        }
        return resultMap;
    }
}
