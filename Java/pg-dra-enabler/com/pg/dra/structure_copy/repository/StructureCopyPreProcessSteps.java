package com.pg.dra.structure_copy.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dra.structure_copy.interfaces.IStructureCopyPreProcessSteps;
import com.pg.dra.structure_copy.models.Part;

import matrix.db.Context;
import matrix.util.StringList;

public class StructureCopyPreProcessSteps implements IStructureCopyPreProcessSteps {
    @Override
    public StringList getBasicSelectList() {
        StringList selectList = new StringList();
        selectList.add(DomainConstants.SELECT_ID);
        selectList.add(DomainConstants.SELECT_TYPE);
        selectList.add(DomainConstants.SELECT_NAME);
        selectList.add(DomainConstants.SELECT_REVISION);
        selectList.add(DomainConstants.SELECT_LEVEL);
        return selectList;
    }

    @Override
    public StringList getBOMChildExpansionObjectSelectList() {
        return getBasicSelectList();
    }

    @Override
    public StringList getBOMChildExpansionRelationshipSelectList() {
        StringList selectList = new StringList();
        selectList.add(DomainConstants.SELECT_RELATIONSHIP_ID);
        selectList.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
        return selectList;
    }

    @Override
    public Map<Object, Object> getObjectInfo(Context context, DomainObject domainObject) throws FrameworkException {
        return domainObject.getInfo(context, getBasicSelectList());
    }

    @Override
    public Part getPart(Map<Object, Object> objectMap) {
        return new Part(objectMap);
    }

    @Override
    public MapList getBOMChildExpansionList(Context context, DomainObject domainObject) throws FrameworkException {
        return domainObject.getRelatedObjects(context,
                DomainConstants.RELATIONSHIP_EBOM, // relationship pattern
                DomainConstants.QUERY_WILDCARD, // Type pattern
                false, // to side
                true, // from side
                0, // recursion level
                getBOMChildExpansionObjectSelectList(), // object selects
                getBOMChildExpansionRelationshipSelectList(), // rel selects
                DomainConstants.EMPTY_STRING, // object where clause
                DomainConstants.EMPTY_STRING, // relWhereClause
                0, //limit
                null, // postRelPattern,
                null, // PostPattern
                null);// Map Post Pattern
    }

    @Override
    public Part getBOMStructure(Context context, String objectOid) throws FrameworkException {
        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        IStructureCopyPreProcessSteps steps = new StructureCopyPreProcessSteps();
        Map<Object, Object> objectMap = steps.getObjectInfo(context, domainObject);
        Part part = steps.getPart(objectMap);
        MapList objectList = steps.getBOMChildExpansionList(context, domainObject);

        List<Part> tempList = new ArrayList<>(); // parents...
        Part childPart;
        Part parentPart;
        int level;
        for (Object object : objectList) {
            childPart = new Part((Map<Object, Object>) object);
            level = Integer.parseInt(childPart.getLevel());
            if (level == 1) {
                tempList.clear();
                tempList.add(part);
                childPart.setParent(part);
                childPart.setParentExist(true);
                part.addChild(childPart);
                part.setChildExist(true);
            } else {
                parentPart = tempList.get(level - 1);
                childPart.setParent(parentPart);
                childPart.setParentExist(true);
                parentPart.addChild(childPart);
                parentPart.setChildExist(true);
            }
            tempList.add(level, childPart);
        }
        tempList.clear();
        return part;
    }
}
