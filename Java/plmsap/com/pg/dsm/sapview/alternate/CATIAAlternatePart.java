package com.pg.dsm.sapview.alternate;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.sapview.beans.bo.Part;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CATIAAlternatePart {
    private static final Logger logger = Logger.getLogger(CATIAAlternatePart.class.getName());
    List<Part> qualifiedWhereUsedParts;

    /**
     * Private Constructor.
     *
     * @since DSM 2018x.5
     */
    private CATIAAlternatePart(Expand builder) {
        this.qualifiedWhereUsedParts = builder.qualifiedWhereUsedParts;
    }

    /**
     * Method to get all qualified where used parts.
     *
     * @return List<Part>  - Part list
     * @since DSM 2018x.5
     */
    public List<Part> getQualifiedWhereUsedParts() {
        return qualifiedWhereUsedParts;
    }
    public static class Expand {
        String inProcessAlternateObjectId;
        Context context;
        DomainObject inProcessAlternateDomainObj;
        List<Part> qualifiedWhereUsedParts;
        CATIAAlternatePartUtil catiaAlternatePartUtil;
        Instant startTime = Instant.now();

        /**
         * Inner class constructor.
         *
         * @since DSM 2018x.5
         */

        public Expand(String inProcessAlternateObjectId) throws FrameworkException {
            this.inProcessAlternateObjectId = inProcessAlternateObjectId;
            this.context = CATIAAlternatePartContext.getContext();
            this.inProcessAlternateDomainObj = DomainObject.newInstance(context, inProcessAlternateObjectId);
            this.catiaAlternatePartUtil = new CATIAAlternatePartUtil();
        }
        /**
         * Method to load parent class
         *
         * @return CATIAAlternatePart  - object
         * @since DSM 2018x.5
         */
        public CATIAAlternatePart load() {
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info("CATIA Alternate Part on release - Total execution time |"+duration.toMillis()+" ms.|"+duration.toMinutes()+" min.|"+duration.getSeconds()+" sec.");
            return new CATIAAlternatePart(this);
        }
        /**
         * Method to get where used.
         *
         * @return Expand  - this class.
         * @throws FrameworkException - exception
         * @since DSM 2018x.5
         */
        public Expand filter() throws FrameworkException {
            List<Part> resultPartList = new ArrayList<>();
            MapList alternateRelatedObjectList = catiaAlternatePartUtil.getAlternateRelatedObjects(context, inProcessAlternateDomainObj);
            Map alternateInfoMap = catiaAlternatePartUtil.getAlternateInfoMap(context, inProcessAlternateDomainObj);
            String partName = (String)alternateInfoMap.get(DomainConstants.SELECT_NAME);
            logger.info("Processing Part Name "+partName);
            if (alternateRelatedObjectList != null && !alternateRelatedObjectList.isEmpty()) {
                Map tempMap;
                for (Object eachObj : alternateRelatedObjectList) {
                    tempMap = (Map) eachObj;
                    tempMap.put(SAPViewConstant.PARENT_EBOM_LIST.getValue(), catiaAlternatePartUtil.getEBOMParentBean(tempMap));
                    tempMap.put(SAPViewConstant.ALTERNATE_LIST.getValue(), catiaAlternatePartUtil.getAlternateBean(tempMap));
                    tempMap.put(SAPViewConstant.SUBSTITUTE_LIST.getValue(), catiaAlternatePartUtil.getParentEBOMSubstituteBean(tempMap));
                }
                MapList expandAlternateFilteredList = getExpandAlternateFilteredList(context, alternateRelatedObjectList, alternateInfoMap);
                if (!expandAlternateFilteredList.isEmpty()) {
                    resultPartList = getAlternateTopNode(context, expandAlternateFilteredList);
                }
            }
            Set<String> set = new HashSet<>(resultPartList.size());
            this.qualifiedWhereUsedParts = resultPartList.stream().filter(part -> set.add(part.getId())).collect(Collectors.toList());
            return this;
        }
        /**
         * Method to filter object list
         *
         * @param context - Context
         * @param alternateRelatedObjectList - MapList
         * @param alternateInfoMap - Map
         * @return Expand  - this class.
         * @throws FrameworkException - exception
         * @since DSM 2018x.5
         */
        private MapList getExpandAlternateFilteredList(Context context, MapList alternateRelatedObjectList, Map alternateInfoMap) {
            MapList expandAlternateResultFilteredList = new MapList();
			//Selectable Moved To V3
            String alternatePartPreviousRevObjectId = (String) alternateInfoMap.get(pgV3Constants.SELECT_PREVIOUS_ID);
            Map tempMap;
            Part alternatePart;
            List<Part> alternateList;
            for (Object eachMap : alternateRelatedObjectList) {
                tempMap = (Map) eachMap;
                alternateList = (List) tempMap.get(SAPViewConstant.ALTERNATE_LIST.getValue());
                if (UIUtil.isNotNullAndNotEmpty(alternatePartPreviousRevObjectId)) {
                    alternatePart = alternateList.stream().filter(eachAlternatePart -> eachAlternatePart.getId().equals(alternatePartPreviousRevObjectId)).findAny().orElse(null);

                    // if null - meaning not connected. skip if previous revision is already connected.
                    if (alternatePart == null) {
                        expandAlternateResultFilteredList.add(tempMap);
                    }
                } else { // else found.
                    expandAlternateResultFilteredList.add(tempMap);
                }
            }
            return expandAlternateResultFilteredList;
        }

        /**
         * Method to filter object list
         *
         * @param context - Context
         * @param objectList - MapList.
         * @return List<Part>  - list of parts.
         * @throws FrameworkException - exception
         * @since DSM 2018x.5
         */
        private List<Part> getAlternateTopNode(Context context, MapList objectList) throws FrameworkException {

            List<Part> resultPartList = new ArrayList<>();
			
			String noExpandBusTypeList = EnoviaResourceBundle.getProperty (context, "emxCPNStringResource", context.getLocale(), "emxCPN.SAPBOM.nonExpand.Types");
            List<Part> expandPartBeanList = new ArrayList<>();
            List<Part> noExpandPartBeanList = new ArrayList<>();

            Map tempMap;
            List<Part> parentEBOMList;
            List<Part> substituteList;
            for (Object eachMap : objectList) {
                tempMap = (Map) eachMap;
                parentEBOMList = (List) tempMap.get(SAPViewConstant.PARENT_EBOM_LIST.getValue());
                parentEBOMList.forEach(parentEBOM -> {
                    if (!pgV3Constants.THIRD_PARTY.equalsIgnoreCase(parentEBOM.getAssemblyType())) {
                        if (noExpandBusTypeList.contains(parentEBOM.getType())) {
                            noExpandPartBeanList.add(parentEBOM);
                        } else {
                            expandPartBeanList.add(parentEBOM);
                        }
                    }
                });
                substituteList = (List) tempMap.get(SAPViewConstant.SUBSTITUTE_LIST.getValue());
                substituteList.forEach(substitutePart -> {
                    if (!pgV3Constants.THIRD_PARTY.equalsIgnoreCase(substitutePart.getAssemblyType())) {
                        if (noExpandBusTypeList.contains(substitutePart.getType())) {
                            noExpandPartBeanList.add(substitutePart);
                        } else {
                            expandPartBeanList.add(substitutePart);
                        }
                    }
                });
            }
            resultPartList.addAll(noExpandPartBeanList);
            resultPartList.addAll(getIntermediatesTopNode(context, expandPartBeanList));
            return resultPartList;
        }

        /**
         * Method to get top level parents.
         *
         * @param context - Context
         * @param expandPartBeanList - List<Part>.
         * @return List<Part>  - list of parts.
         * @throws FrameworkException - exception
         * @since DSM 2018x.5
         */
        private List<Part> getIntermediatesTopNode(Context context, List<Part> expandPartBeanList) throws FrameworkException {

            List<Part> resultPartList = new ArrayList<>();

            DomainObject dObj;
            MapList objectList;
            Map tempMap;

            for (Part partBean : expandPartBeanList) {
                dObj = DomainObject.newInstance(context, partBean.getId());

                // get ebom parent recursively.
                objectList = catiaAlternatePartUtil.getExpandEBOMParents(context, dObj);

                // if there are no ebom parents.
                if (objectList == null || objectList.isEmpty()) {
                    // check if type is customer unit part.
                    // check if assembly type is reshipper.
                    if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(partBean.getType()) && SAPViewConstant.RESHIPPER.getValue().equalsIgnoreCase(partBean.getAssemblyType())) {

                        // get parent nodes.
                        resultPartList.addAll(catiaAlternatePartUtil.getParentEBOMSubstituteBean(context, partBean.getId()));
                    }
                } else {
                    int iFPPLevel = 1;
                    objectList.sort(DomainConstants.SELECT_LEVEL, "descending", "integer");

                    for (Object eachMap : objectList) {
                        tempMap = (Map) eachMap;
                        int level = Integer.parseInt((String) tempMap.get(DomainConstants.SELECT_LEVEL));

                        // if state is not obsolete.
                        if (!DomainConstants.STATE_PART_OBSOLETE.equalsIgnoreCase((String) tempMap.get(DomainConstants.SELECT_CURRENT))) {

                            // if type is customer unit part && assembly type is re-shipper.
                            if (SAPViewConstant.RESHIPPER.getValue().equalsIgnoreCase((String) tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE))
                                    && pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase((String) tempMap.get(DomainConstants.SELECT_TYPE))) {

                                // get parent fpp id.
                                resultPartList.addAll((List<Part>) tempMap.get(SAPViewConstant.SUBSTITUTE_LIST.getValue()));
                            }

                            // if type is fpp.
                            if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase((String) tempMap.get(DomainConstants.SELECT_TYPE)) && iFPPLevel <= level) {
                                iFPPLevel = level;

                                // that fpp itself is top level part.
                                resultPartList.add(catiaAlternatePartUtil.getPartBean(tempMap));
                            }
                        }
                    }
                }
            }
            return resultPartList;
        }
    }
}
