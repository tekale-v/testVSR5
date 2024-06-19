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

public class IntermediateBOM {
    boolean loaded;
    MapList partList;
    MapList subsIntermediateList;

    private IntermediateBOM(Process process) {
        this.loaded = process.loaded;
        this.partList = process.partList;
        this.subsIntermediateList = process.subsIntermediateList;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public MapList getPartList() {
        return partList;
    }

    public MapList getSubsIntermediateList() {
        return subsIntermediateList;
    }

    public static class Process {
        private static final Logger logger = Logger.getLogger(BOMExtractor.Process.class.getName());
        boolean loaded;
        MapList partList;
        MapList subsIntermediateList;

        Context context;
        Map<Object, Object> intermediateMap;
        Map<Object, Object> intermediateChildMap;
        BOMUtils bomUtils;

        public Process(Context context, Map<Object, Object> intermediateMap, Map<Object, Object> intermediateChildMap) {
            this.context = context;
            this.bomUtils = new BOMUtils(context);
            this.intermediateMap = intermediateMap;
            this.intermediateChildMap = intermediateChildMap;
            this.partList = new MapList();
            this.subsIntermediateList = new MapList();
        }

        public IntermediateBOM load(StringList objectSelects) {
            try {
                doProcess(objectSelects);
                this.loaded = true;
            } catch (FrameworkException e) {
                logger.log(Level.WARNING, "Exception - ", e);
                this.loaded = false;
            }
            return new IntermediateBOM(this);
        }

        public void doProcess(StringList objectSelects) throws FrameworkException {
            final String intermediateChildType = (String) intermediateChildMap.get(DomainConstants.SELECT_TYPE);
            final String intermediateChildPolicy = (String) intermediateChildMap.get(DomainConstants.SELECT_POLICY);
            if (this.bomUtils.isEligibleChildType(intermediateChildType, intermediateChildPolicy)) {
                this.partList.add(this.bomUtils.getIntermediateParentChildStructure(intermediateMap, intermediateChildMap));
                if (this.bomUtils.isEligibleAlternateType(intermediateChildType)) {
                    this.partList.addAll(this.bomUtils.getAlternateOfIntermediatesChild(context, intermediateMap, intermediateChildMap, objectSelects));
                }
            }
            MapList substituteList = this.bomUtils.extractSubstitutesOfIntermediatesChild(intermediateMap, intermediateChildMap);
            if (null != substituteList && !substituteList.isEmpty()) {
                extractSubstituteAlternate(substituteList, objectSelects);
            }

            // Added by (DSM) Sogeti for 22x.02 - (May CW) - REQ 46280 - start
            // if incoming type is CUP/COP/IP then extract its substitutes.
            if (this.bomUtils.isAllowedIntermediateTypeForSubstituteExpansion((String) this.intermediateMap.get(DomainConstants.SELECT_TYPE))) {
                logger.log(Level.INFO, "Include Intermediate Substitute");
                MapList intermediateSubstitutes = this.bomUtils.extractSubstitutesOfIntermediate(this.intermediateMap);
                if (null != intermediateSubstitutes && !intermediateSubstitutes.isEmpty()) {
                    this.subsIntermediateList.addAll(intermediateSubstitutes);
                }
            } // Added by (DSM) Sogeti for 22x.02 - (May CW) - REQ 46280 - end
        }

        public void extractSubstituteAlternate(MapList substituteList, StringList objectSelects) throws FrameworkException {
            final Iterator iterator = substituteList.iterator();
            Map<Object, Object> substituteMap;
            String substituteType;
            String substitutePolicy;
            while (iterator.hasNext()) {
                substituteMap = (Map<Object, Object>) iterator.next();
                substituteType = (String) substituteMap.get(DomainConstants.SELECT_TYPE);
                substitutePolicy = (String) substituteMap.get(DomainConstants.SELECT_POLICY);
                if (this.bomUtils.isEligibleChildType(substituteType, substitutePolicy)) {
                    this.partList.add(substituteMap);
                    if (this.bomUtils.isEligibleAlternateType(substituteType)) {
                        this.partList.addAll(this.bomUtils.extractSubstituteAlternate(context, intermediateMap, substituteMap, objectSelects));
                    }
                } else if (!pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(substitutePolicy)) {
                    this.subsIntermediateList.add(substituteMap);
                }
            }
        }
    }
}
