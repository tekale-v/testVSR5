package com.pg.dsm.rollup.packaging_certification.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.evp.messaging.utils.UIUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class BOMExtractor {
    boolean loaded;
    MapList partList;
    MapList subsIntermediateList;

    private BOMExtractor(Process process) {
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
        private static final Logger logger = Logger.getLogger(Process.class.getName());
        Context context;
        boolean loaded;
        String objectOid;
        MapList partList;
        MapList subsIntermediateList; // substitute intermediate list
        String identifier;

        public Process(Context context, String objectOid, String identifier) {
            this.context = context;
            this.objectOid = objectOid;
            this.identifier = identifier;
            this.partList = new MapList();
            this.subsIntermediateList = new MapList();
        }

        public BOMExtractor load(StringList objectSelects, StringList relSelects) {
            try {
                doProcess(objectSelects, relSelects);
                this.loaded = true;
            } catch (FrameworkException e) {
                logger.log(Level.WARNING, "Exception - ", e);
                this.loaded = false;
            }
            return new BOMExtractor(this);
        }

        /**
         * @param objectSelects
         * @param relSelects
         * @throws FrameworkException
         */
        private void doProcess(StringList objectSelects, StringList relSelects) throws FrameworkException {
            BOMUtils bomUtils = new BOMUtils(context);

            final DomainObject domainObject = DomainObject.newInstance(context, objectOid);
            Map infoMap = getObjectInfo(domainObject);

            String objectWhere = DomainConstants.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;

            // extract CUP/COP/IP at all-levels
            final MapList intermediateList = bomUtils.getIntermediateAtAllLevel(context, domainObject, objectSelects, relSelects, objectWhere);

            if (null != intermediateList && !intermediateList.isEmpty()) {

                IntermediateBOM inBOM;
                Map<Object, Object> inMap;
                Map<Object, Object> inChildMap;
                MapList inChildList;
                MapList rmpMapList = new MapList();
                String inOid;
                String materialTypes;

                Iterator iterator;
                DomainObject intermediateObj = DomainObject.newInstance(context);

                for (int i = 0; i < intermediateList.size(); i++) {
                    inMap = (Map<Object, Object>) intermediateList.get(i);
                    if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equals((String) inMap.get(DomainConstants.SELECT_TYPE))) {
                        this.partList.add(inMap);
                    }
                    inOid = (String) inMap.get(DomainConstants.SELECT_ID);
                    intermediateObj.setId(inOid);  // set intermediate object id.
                    materialTypes = DomainConstants.QUERY_WILDCARD;

                    inChildList = bomUtils.getFirstLevelBOMOfIntermediate(context, intermediateObj, materialTypes, objectSelects, relSelects, objectWhere);
                    iterator = inChildList.iterator();
                    while (iterator.hasNext()) {
                        inChildMap = (Map<Object, Object>) iterator.next();
                        //Modified by DSM(sogeti) for 2022x.5 rollup Defect 57167 - Start
                        //get Extracted RM
                        rmpMapList = getExtractRMP( context,  inChildMap,  materialTypes,  objectSelects, relSelects,inMap);
                        if(!rmpMapList.isEmpty()) {
                            this.partList.addAll(rmpMapList);
                        }
                        //Modified by DSM(sogeti) for 2022x.5 rollup Defect 57167 - End
                        inBOM = new IntermediateBOM.Process(context, inMap, inChildMap).load(objectSelects);
                        if (inBOM.isLoaded()) {
                            this.partList.addAll(inBOM.getPartList());
                            this.subsIntermediateList.addAll(inBOM.getSubsIntermediateList());
                        }
                    }
                }
            }
            if (this.identifier.equalsIgnoreCase(pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE)) {
                this.subsIntermediateList.add(infoMap);
            }
        }

        private Map<Object, Object> getObjectInfo(DomainObject domainObject) throws FrameworkException {
            StringList objectSelectList = new StringList(7);
            objectSelectList.addElement(DomainConstants.SELECT_TYPE);
            objectSelectList.addElement(DomainConstants.SELECT_NAME);
            objectSelectList.addElement(DomainConstants.SELECT_REVISION);
            objectSelectList.addElement(DomainConstants.SELECT_ID);
            objectSelectList.addElement(DomainConstants.SELECT_POLICY);
            return domainObject.getInfo(context, objectSelectList);
        }
    }

    /**
     * Added by DSM(sogeti) for 2022x.5 rollup Defect 57167 
     * @param context
     * @param inChildMap
     * @param materialTypes
     * @param objectSelects
     * @param relSelects
     * @param inMap
     * @return
     * @throws FrameworkException
     */
    static MapList getExtractRMP(Context context, Map inChildMap, String materialTypes, StringList objectSelects,StringList relSelects,Map<Object, Object> inMap) throws FrameworkException {
        MapList rmpMapList = new MapList();
        MapList childRMPList = new MapList();
        MapList inChildRMPList = new MapList();
        StringList listofRMPIds= new StringList();
        Iterator iterator;
        IntermediateBOM inBOM;
        try {
            String objectWhere = DomainConstants.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
            
            String intermediateChildId = (String) inChildMap.get(DomainConstants.SELECT_ID);
            String intermediateChildType = (String) inChildMap.get(DomainConstants.SELECT_TYPE);

            if(UIUtil.isNotNullAndNotEmpty(intermediateChildId)) {
                DomainObject intermediateObj = DomainObject.newInstance(context, intermediateChildId);
                if(pgV3Constants.TYPE_FORMULATIONPART.equals(intermediateChildType)) {
                    listofRMPIds = intermediateObj.getInfoList(context,"from["+pgV3Constants.RELATIONSHIP_PLANNEDFOR+"].to.from[FBOM].to.id");
                    if(!listofRMPIds.isEmpty()){
                        for(int i = 0;i< listofRMPIds.size(); i++){
                            childRMPList = getAllLevelBOMOfRMPs(context, DomainObject.newInstance(context,listofRMPIds.get(i)), objectSelects, relSelects, objectWhere);
                            inChildRMPList.addAll(childRMPList);
                        }
          
                    }
                    
                } else {
                    inChildRMPList = getAllLevelBOMOfRMPs(context, intermediateObj, objectSelects, relSelects, objectWhere);
                }
                if(!inChildRMPList.isEmpty()) {
	                iterator = inChildRMPList.iterator();
	                while (iterator.hasNext()) {
	                    inChildMap = (Map<Object, Object>) iterator.next();
	                  //Modified by DSM(sogeti) for 2022x.5 rollup Defect 57167 - Start
                        if(!inChildMap.containsValue(pgV3Constants.RELATIONSHIP_FORMULA_INGREDIENT)){
	                    inBOM = new IntermediateBOM.Process(context, inMap, inChildMap).load(objectSelects);
	                    if (inBOM.isLoaded()) {
	                        rmpMapList.addAll( inBOM.getPartList());
	                    }
	                }
                      //Modified by DSM(sogeti) for 2022x.5 rollup Defect 57167 - End
	                }
                }

            }
        } catch(Exception ex){
            throw ex;
        }
        return rmpMapList;
    }


   
    /**
     * Added by DSM(sogeti) for 2022x.5 rollup Defect 57167 
     * @param context
     * @param domainObject
     * @param busSelectList
     * @param relSelectList
     * @param busWhere
     * @return
     * @throws FrameworkException
     */
    static MapList getAllLevelBOMOfRMPs(Context context, DomainObject domainObject, StringList busSelectList, StringList relSelectList, String busWhere) throws FrameworkException {
        StringBuffer sbPostPattern = new StringBuffer(pgV3Constants.TYPE_RAWMATERIALPART);
        StringBuffer sbRelPattern = new StringBuffer(pgV3Constants.RELATIONSHIP_EBOM).append(","+"FBOM");
        return domainObject.getRelatedObjects(context,              // context
                sbRelPattern.toString(),                    // rel pattern
                pgV3Constants.TYPE_PART,                            // type pattern
                false,                                              // to side
                true,                                               // from side
                0,                                                  // level
                busSelectList,                                      // bus selects
                relSelectList,                                      // rel selects
                busWhere,                       						// object where
                DomainConstants.EMPTY_STRING,                                           // rel where
                DomainConstants.EMPTY_STRING,                       // post rel pattern
                sbPostPattern.toString(),                                   // post type pattern
                null);                                              // post pattern
    }

}
