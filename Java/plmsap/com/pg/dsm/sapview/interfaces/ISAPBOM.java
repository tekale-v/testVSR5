package com.pg.dsm.sapview.interfaces;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import matrix.db.Context;

import java.util.Map;

public interface ISAPBOM {
    MapList getBOMInfoList(Context context, String objectOid, MapList objectList) throws FrameworkException;
    Map<Object, Object> getComplexInfo(Context context, Map<Object, Object> paramMap) throws FrameworkException;
    MapList getBOMInfoList(Context context, MapList objectList) throws FrameworkException;
    MapList getDisplayBOMInfoList(Context context, MapList objectList) throws FrameworkException;
    MapList getBOMStructure(Map<Object, Object> rootMap, MapList rootList, boolean isFirstLevelComplex);
    void displayBOMStructure(Context context, String objectOid) throws FrameworkException;
}
