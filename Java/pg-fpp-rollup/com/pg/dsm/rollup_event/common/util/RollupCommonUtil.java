package com.pg.dsm.rollup_event.common.util;

import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class RollupCommonUtil {
    public static StringList getObjectOidList(List<ProductPart> productPartList) {
        StringList objectOidList = new StringList();
        for (ProductPart productPart : productPartList) {
            objectOidList.addElement(productPart.getId());
        }
        return objectOidList;
    }

    public static MapList getEventNameInfo(Context context, List<ProductPart> productPartList) throws FrameworkException {
        MapList objectList = new MapList();
        try {
            StringList objectOidList = getObjectOidList(productPartList);
            if (!objectOidList.isEmpty()) {
                objectList = DomainObject.getInfo(context, objectOidList.toArray(new String[objectOidList.size()]), new StringList(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP));
            }
        } catch (FrameworkException e) {
            throw e;
        }
        return objectList;
    }

    public static StringList removeDuplicates(StringList slObjects) {
        StringList slUniqueList = new StringList(1);
        for (String objectId : slObjects) {
            if (!slUniqueList.contains(objectId)) {
                slUniqueList.add(objectId);
            }
        }
        return slUniqueList;
    }

    public static MapList removeDuplicateValues(MapList valuesList) {
        MapList returnList = new MapList();
        if (valuesList != null && !valuesList.isEmpty()) {
            StringList processedIds = new StringList();
            Map map;
            String objOid;
            int iObjectSize = valuesList.size();
            for (int i = 0; i < iObjectSize; i++) {
                map = (Map) valuesList.get(i);
                objOid = (String) map.get(DomainConstants.SELECT_ID);
                if (!processedIds.contains(objOid)) {
                    returnList.add(map);
                    processedIds.add(objOid);
                }
            }
        }
        return returnList;
    }

    /**
     * This method checks both StringList are unique or not
     *
     * @param listOne
     * @param listTwo
     * @return true if there is difference in List
     */
    public static boolean isSnapshotDifference(StringList listOne, StringList listTwo) {
        boolean isDifferent = false;
        final int listOneSize = listOne.size();
        final int listTwoSize = listTwo.size();

        if (listOneSize != listTwoSize) {
            isDifferent = true; // meaning there is a difference.
        }
        if (listOneSize == listTwoSize) {
            for (int i = 0; i < listOneSize; i++) {
                // if list two does not contain atleast one element then break.
                if (!listTwo.contains(listOne.get(i))) {
                    isDifferent = true; // meaning there is a difference. break out of loop.
                    break;
                }
            }
        }
        return isDifferent;
    }

}

