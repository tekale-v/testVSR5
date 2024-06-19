package com.pg.dsm.rollup.packaging_certification.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class IntermediateSetProduct {
    boolean loaded;
    boolean hasSetProductInCUPBOM;
    boolean hasSetProductInCUPSubstituteBOM;
    MapList subsIntermediateList;

    private IntermediateSetProduct(Process process) {
        this.loaded = process.loaded;
        this.hasSetProductInCUPBOM = process.hasSetProductInCUPBOM;
        this.hasSetProductInCUPSubstituteBOM = process.hasSetProductInCUPSubstituteBOM;
        this.subsIntermediateList = process.subsIntermediateList;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isHasSetProductInCUPBOM() {
        return hasSetProductInCUPBOM;
    }

    public boolean isHasSetProductInCUPSubstituteBOM() {
        return hasSetProductInCUPSubstituteBOM;
    }

    public MapList getSubsIntermediateList() {
        return subsIntermediateList;
    }

    public static class Process {
        private static final Logger logger = Logger.getLogger(BOMExtractor.Process.class.getName());
        Context context;
        boolean loaded;
        boolean hasSetProductInCUPBOM;
        boolean hasSetProductInCUPSubstituteBOM;
        MapList subsIntermediateList;

        public Process(Context context, boolean hasSetProductInCUPBOM) {
            this.context = context;
            this.hasSetProductInCUPBOM = hasSetProductInCUPBOM;
            this.subsIntermediateList = new MapList();
        }

        public IntermediateSetProduct load(Map<Object, Object> intermediateMap) {
            try {
                doProcess(intermediateMap);
                this.loaded = true;
            } catch (FrameworkException e) {
                logger.log(Level.WARNING, "Exception - ", e);
                this.loaded = false;
            }
            return new IntermediateSetProduct(this);
        }

        public void doProcess(Map<Object, Object> intermediateMap) throws FrameworkException {
            String level = (String) intermediateMap.get(DomainConstants.SELECT_LEVEL);
            if ("1".equals(level)) {
                identifySetProductInSubstitute(intermediateMap);
            }
        }

        /**
         * @param intermediateMap
         * @throws FrameworkException
         */
        private void identifySetProductInSubstitute(Map<Object, Object> intermediateMap) throws FrameworkException {
            List<String> substituteTypes = new ArrayList<>();
            substituteTypes.add(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
            substituteTypes.add(pgV3Constants.TYPE_PGINNERPACKUNITPART);
            substituteTypes.add(pgV3Constants.TYPE_PGCONSUMERUNITPART);

            BOMUtils bomUtils = new BOMUtils(context);
            final MapList substituteList = bomUtils.extractSubstitutes(intermediateMap);
            Map<Object, Object> substituteMap;
            String substituteOid;
            String substituteType;
            String substituteRevision;

            boolean isSetProduct = false;
            DomainObject substituteObj = DomainObject.newInstance(context);
            StringList objectSelects = new StringList(2);
            objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME);
            objectSelects.add(DomainConstants.SELECT_CURRENT);
            MapList tempList;
            Map<Object, Object> tempMap;

            Map<Integer, Boolean> setProductMap = new HashMap<>();
            for (int i = 0; i < substituteList.size(); i++) {
                substituteMap = (Map<Object, Object>) substituteList.get(i);
                substituteOid = (String) substituteMap.get(DomainConstants.SELECT_ID);
                substituteType = (String) substituteMap.get(DomainConstants.SELECT_TYPE);
                if (substituteTypes.contains(substituteType)) {
                    if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(substituteType)) {
                        substituteObj.setId(substituteOid);
                        tempList = substituteObj.getRelatedObjects(context,//context user
                                pgV3Constants.RELATIONSHIP_EBOM,//relationship pattern
                                DomainConstants.QUERY_WILDCARD,//type pattern
                                false,//get to
                                true,//get from
                                (short) 2,//level
                                objectSelects,//bus select
                                null,//rel select
                                DomainConstants.EMPTY_STRING,//bus where
                                DomainConstants.EMPTY_STRING,//rel where
                                (short) 0,//limit
                                DomainConstants.EMPTY_STRING,//post rel pattern
                                DomainConstants.EMPTY_STRING,//post type pattern
                                null);//post patterns
                        if (tempList != null && !tempList.isEmpty()) {
                            for (int j = 0; j < tempList.size(); j++) {
                                tempMap = (Map<Object, Object>) tempList.get(j);
                                if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase((String) tempMap.get(DomainConstants.SELECT_TYPE)) && !DomainConstants.STATE_PART_OBSOLETE.equalsIgnoreCase((String) tempMap.get(DomainConstants.SELECT_CURRENT))) {
                                    if (pgV3Constants.KEY_YES.equalsIgnoreCase((String) tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
                                        isSetProduct = true;
                                        this.hasSetProductInCUPSubstituteBOM = true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    setProductMap.put(i, isSetProduct);
                }
            }
            if (this.hasSetProductInCUPSubstituteBOM || (!this.hasSetProductInCUPSubstituteBOM && !this.hasSetProductInCUPBOM)) {
                for (int k = 0; k < substituteList.size(); k++) {
                    substituteMap = (Map<Object, Object>) substituteList.get(k);
                    substituteType = (String) substituteMap.get(DomainConstants.SELECT_TYPE);
                    substituteRevision = (String) substituteMap.get(DomainConstants.SELECT_REVISION);
                    substituteOid = (String) substituteMap.get(DomainConstants.SELECT_ID);
                    if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(substituteType) && (Boolean.TRUE.equals(setProductMap.get(k)) || (!this.hasSetProductInCUPBOM && !this.hasSetProductInCUPSubstituteBOM))) {
                        tempMap = new HashMap<>();
                        tempMap.put(DomainConstants.SELECT_ID, substituteOid);
                        tempMap.put(DomainConstants.SELECT_TYPE, substituteType);
                        tempMap.put(DomainConstants.SELECT_REVISION, substituteRevision);
                        this.subsIntermediateList.add(tempMap);
                    }
                }
            }
        }
    }
}
