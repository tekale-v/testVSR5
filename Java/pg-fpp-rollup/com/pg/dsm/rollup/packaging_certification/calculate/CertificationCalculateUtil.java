package com.pg.dsm.rollup.packaging_certification.calculate;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.rollup_event.common.circular.BOM;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class CertificationCalculateUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public CertificationCalculateUtil() {
    }

    public MapList getMarkedFinishedProductParts(Context context, String mqlQuery) {
        MapList objectList = new MapList();
        try {
            // observed that MQL temp query is much faster than find objects API.
            String mqlResultString = MqlUtil.mqlCommand(context, mqlQuery);
            objectList = filterQueryResult(mqlResultString);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return objectList;
    }

    private MapList filterQueryResult(String mqlResultString) {
        MapList objectList = new MapList();
        try {
            Map<String, String> objectMap = null;
            if (StringUtils.isNotBlank(mqlResultString)) {
                StringList tempList;
                StringList mqlResultList = StringUtil.splitString(mqlResultString, "@");
                for (String mqlResultLine : mqlResultList) {
                    tempList = StringUtil.splitString(mqlResultLine, pgV3Constants.DUMP_CHARACTER);
                    objectMap = new HashMap<>();
                    objectMap.put(DomainConstants.SELECT_TYPE, tempList.get(0));
                    objectMap.put(DomainConstants.SELECT_NAME, tempList.get(1));
                    objectMap.put(DomainConstants.SELECT_REVISION, tempList.get(2));
                    objectMap.put(DomainConstants.SELECT_ID, tempList.get(3));
                    objectMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE, tempList.get(4));
                    objectMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP, tempList.get(5));
                    objectList.add(objectMap);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return objectList;
    }

    /**
     * @param context
     * @param objectId
     * @param resource
     * @return
     * @throws FrameworkException
     */
    public boolean isCircularStructure(Context context, String objectId, Resource resource) throws FrameworkException {
        Instant startTime = Instant.now();
        BOM bomStructure = new BOM.Structure(context, objectId, resource).checkCircular().getInstance();
        Instant endTime = Instant.now();
        final Duration duration = Duration.between(startTime, endTime);
        logger.info("Circular Check- took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return bomStructure.isCircularExist();
    }
}
