package com.pg.dra.structure_copy.interfaces;

import java.util.Map;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dra.structure_copy.models.Part;

import matrix.db.Context;
import matrix.util.StringList;

public interface IStructureCopyPreProcessSteps {
    StringList getBasicSelectList();
    StringList getBOMChildExpansionObjectSelectList();
    StringList getBOMChildExpansionRelationshipSelectList();
    Map<Object, Object> getObjectInfo(Context context, DomainObject domainObject) throws FrameworkException;
    Part getPart(Map<Object, Object> objectMap);
    MapList getBOMChildExpansionList(Context context, DomainObject domainObject) throws FrameworkException;
    Part getBOMStructure(Context context, String objectOid) throws FrameworkException;
}
