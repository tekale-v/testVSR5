package com.pg.dsm.rollup.packaging_certification.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class BOMExtractorRecursive {
    boolean loaded;
    MapList partList;

    private BOMExtractorRecursive(Processor processor) {
        this.loaded = processor.loaded;
        this.partList = processor.partList;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public MapList getPartList() {
        return partList;
    }

    public static class Processor {
        private static final Logger logger = Logger.getLogger(Processor.class.getName());
        boolean loaded;
        Context context;
        BOMUtils bomUtils;
        MapList partList;
        StringList recursiveParts;

        public Processor(Context context) {
            this.context = context;
            this.bomUtils = new BOMUtils(context);
            this.partList = new MapList();
            recursiveParts = new StringList();
            recursiveParts.addElement(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
            recursiveParts.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
            recursiveParts.addElement(pgV3Constants.TYPE_FABRICATEDPART);
        }

        public BOMExtractorRecursive load(String objectOid) {
            try {
                doBOMExtract(objectOid);
                this.loaded = true;
            } catch (FrameworkException e) {
                logger.log(Level.WARNING, "Exception - ", e);
                this.loaded = false;
            }
            return new BOMExtractorRecursive(this);
        }

        private void doBOMExtract(String objectOid) throws FrameworkException {
            MapList mapList = bomUtils.getFlatBOMExtraction(objectOid);
            Iterator iterator = mapList.iterator();
            Map<Object, Object> objectMap;
            while (iterator.hasNext()) {
                objectMap = (Map<Object, Object>) iterator.next();
                String type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
                String oid = (String) objectMap.get(DomainConstants.SELECT_ID);
                if (recursiveParts.contains(type)) {
                    doBOMExtract(oid);
                } else {
                    partList.add(objectMap);
                }
            }
        }
    }
}
