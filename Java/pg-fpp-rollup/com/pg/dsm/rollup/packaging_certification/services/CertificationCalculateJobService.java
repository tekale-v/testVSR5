package com.pg.dsm.rollup.packaging_certification.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.engineering.RelToRelUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup.packaging_certification.ui.PackagingCertificationUI;
import com.pg.dsm.rollup.packaging_certification.utils.BOMUtils;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class CertificationCalculateJobService implements pgV3Constants {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    String contextOid;
    String executionType;
    StringList partWhichHasCertification;
    StringList partWhichHasRollupView;

    public CertificationCalculateJobService(Context context, String contextOid, String executionType) {
        this.context = context;
        this.contextOid = contextOid;
        this.executionType = executionType;
        this.partWhichHasCertification = new StringList();
        this.partWhichHasCertification.addElement(TYPE_PACKAGINGMATERIALPART);
        this.partWhichHasCertification.addElement(TYPE_INTERNALMATERIAL);
        this.partWhichHasCertification.addElement(TYPE_PGCONSUMERUNITPART);
        //Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453 - Starts
        this.partWhichHasCertification.addElement(TYPE_RAWMATERIALPART);
        //Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453 - End
        this.partWhichHasRollupView = new StringList();
        this.partWhichHasRollupView.addElement(TYPE_FABRICATEDPART);
        this.partWhichHasRollupView.addElement(TYPE_FINISHEDPRODUCTPART);
        this.partWhichHasRollupView.addElement(TYPE_PACKAGINGASSEMBLYPART);
    }

    /**
     * @param objectOid
     * @throws Exception
     */
    public void processCalculateJob(String objectOid) throws Exception {
        MapList certList = processParents(objectOid);
        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        Map<Object, Object> infoMap = getObjectInfo(domainObject);
        StringList parentList = new StringList();
        parentList.addElement(pgV3Constants.TYPE_FABRICATEDPART);
        parentList.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
        if (!parentList.contains((String) infoMap.get(DomainConstants.SELECT_TYPE))) {
            // only in case of FPP.
            if (!certList.isEmpty()) {
                this.rollupCertifications(objectOid, certList);
            }
        }
    }

    /**
     * @param domainObject
     * @return
     * @throws FrameworkException
     */
    private Map<Object, Object> getObjectInfo(DomainObject domainObject) throws FrameworkException {
        StringList objectSelectList = new StringList(7);
        objectSelectList.addElement(DomainConstants.SELECT_TYPE);
        objectSelectList.addElement(DomainConstants.SELECT_NAME);
        objectSelectList.addElement(DomainConstants.SELECT_REVISION);
        objectSelectList.addElement(DomainConstants.SELECT_ID);
        objectSelectList.addElement(DomainConstants.SELECT_POLICY);
        objectSelectList.addElement(DomainConstants.SELECT_CURRENT);
        return domainObject.getInfo(context, objectSelectList);
    }

    /**
     * @param objectOid
     * @throws Exception
     */
    private void disconnectCertifications(String objectOid) throws Exception {
        PackagingCertificationUI ui = new PackagingCertificationUI(context);
        MapList rolledUpCertifications = ui.getRolledUpCertifications(objectOid);
        List<String> relIDs = (List<String>) rolledUpCertifications.stream().map(map -> (String) ((Map<Object, Object>) map).get(DomainRelationship.SELECT_ID)).collect(Collectors.toList());
        DomainRelationship.disconnect(context, relIDs.toArray(new String[relIDs.size()]));
    }

    /**
     * Certification Rollup Functionality
     *
     * @param objectOid
     * @return
     * @throws Exception
     */
    public MapList processParents(String objectOid) throws Exception {
        MapList certList = new MapList();
        if (context.isConnected()) {
            MapList partList = getFlatBOMExtraction(objectOid);
            certList = calculate(objectOid, partList);
            logger.log(Level.INFO, "Processing certList: {0}", certList);
        }
        return certList;
    }

    /**
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    public MapList getFlatBOMExtraction(String objectOid) throws FrameworkException {
        BOMUtils bomUtils = new BOMUtils(context);
        MapList partList = bomUtils.getFlatBOMExtraction(objectOid);
        if (contextOid.equals(objectOid)) {
            DomainObject domainObject = DomainObject.newInstance(context, contextOid);
            Map<Object, Object> objectInfo = getObjectInfo(domainObject);
            if (pgV3Constants.TYPE_FABRICATEDPART.equals((String) objectInfo.get(DomainConstants.SELECT_TYPE))
                    || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals((String) objectInfo.get(DomainConstants.SELECT_TYPE))) {
                objectInfo.put("relationship", "EBOM");
                partList.add(objectInfo);
            }
        }
        return partList;
    }

    /**
     * Certification Rollup Functionality
     *
     * @param objectOid
     * @param partList
     * @return
     * @throws Exception
     */
    public MapList calculate(String objectOid, MapList partList) throws Exception {
        MapList returnCertList = new MapList();
        MapList tempCertList = new MapList();
        MapList mepSepCertList = new MapList();
        MapList consolidatedList;

        Map<Object, Object> partMap;
        String partType;
        String partID;

        PackagingCertificationUI certUI = new PackagingCertificationUI(context);

        MapList tempList;
        for (int i = 0; i < partList.size(); i++) {
            partMap = (Map<Object, Object>) partList.get(i);
            partType = (String) partMap.get(SELECT_TYPE);
            partID = (String) partMap.get(SELECT_ID);
            if (partWhichHasCertification.contains(partType)) {
                tempList = certUI.getSelfRelatedPackagingCertifications(partMap,objectOid);
                appendSourceRelationshipType(tempList, partMap);
                mepSepCertList = certUI.getMEPSEPCertifications(partMap,objectOid);
                tempList.addAll(mepSepCertList);
                returnCertList.addAll(tempList);
            }
            if (partWhichHasRollupView.contains(partType)) {
                MapList childCertList = new MapList();
                DomainObject domainObject = DomainObject.newInstance(context, partID);
                String calcCert = domainObject.getInfo(context, SELECT_ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP);

                tempCertList = certUI.getSelfRelatedPackagingCertifications(partMap,objectOid);
                appendSourceRelationshipType(tempCertList, partMap);
                childCertList.addAll(certUI.getMEPSEPCertifications(partMap,objectOid));
                childCertList.addAll(tempCertList);
                // if calculate flag is true - then send for processing.
                if (calcCert.equalsIgnoreCase(TRUE)) {

                    tempList = processParents(partID);
                    childCertList.addAll(tempList);

                    // rollup certifications.
                    this.rollupCertifications(partID, childCertList);

                } else { // if calculate is false - then get rolled-up results from parent.
                    // when the incoming object is PAP, FAB
                    if (partID.equals(contextOid)) {
                        consolidatedList = new MapList();
                        consolidatedList.addAll(returnCertList);
                        consolidatedList.addAll(childCertList);
                        this.rollupCertifications(partID, consolidatedList);
                    } else {
                        childCertList = certUI.getRolledUpCertifications(partID);
                    }
                }
                returnCertList.addAll(childCertList);
            }
        }
        return returnCertList;
    }

    /**
     * Certification Rollup Functionality
     *
     * @param objectList
     * @param partMap
     */
    private void appendSourceRelationshipType(MapList objectList, Map<Object, Object> partMap) {
        Iterator iterator = objectList.iterator();
        Map<Object, Object> tempMap;
        while (iterator.hasNext()) {
            tempMap = (Map<Object, Object>) iterator.next();
            tempMap.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE, (String) partMap.get("relationship"));
            tempMap.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_IDENTIFIER, "Rollup");
        }
    }

    /**
     * Method is used to refine map list (to avoid duplicate connections)
     *
     * @param certList
     * @return
     */
    public MapList removeDuplicateByDistinctKeys(MapList certList) {
        MapList uniqueList = new MapList();
        Set<String> uniqueSet = new HashSet<>();
        Iterator iterator = certList.iterator();
        String sourceID;
        String sourceRelType;
        String certName;
        String key;
        Map<Object, Object> objectMap;
        while (iterator.hasNext()) {
            objectMap = (Map<Object, Object>) iterator.next();
            sourceID = (String) objectMap.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);
            sourceRelType = (String) objectMap.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE);
            certName = (String) objectMap.get(DomainObject.SELECT_NAME);
            key = sourceID.concat(sourceRelType).concat(certName);
            if (!uniqueSet.contains(key)) {
                uniqueList.add(objectMap);
                uniqueSet.add(key);
            }
        }
        return uniqueList;
    }

    /**
     * connect certification, update required attribute and shift RM DOC on rollup
     *
     * @param objectOid
     * @param certList
     * @throws FrameworkException
     */
    private void rollupCertifications(String objectOid, MapList certList) throws Exception {
        try {
            StringList relSelects = new StringList();
            relSelects.addElement(DomainRelationship.SELECT_ID);
            RelToRelUtil certsReltoRel = new RelToRelUtil();
            DomainObject domainObject = DomainObject.newInstance(context, objectOid);
            String type = domainObject.getInfo(context, SELECT_TYPE);
            String name = domainObject.getInfo(context, SELECT_NAME);
            disconnectCertifications(objectOid);
            MapList refinedCertList = removeDuplicateByDistinctKeys(certList);
            if (partWhichHasRollupView.contains(type)) {
                Iterator iterator = refinedCertList.iterator();
                Map<Object, Object> objectMap;
                Map<Object, Object> attributeMap;
                String expirationDate;
                String comment;
                String sourceID;
                String sourceIdentifier;
                String sourceRelType;
                String certName;

                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    String certID = (String) objectMap.get(SELECT_ID);
                    certName = (String) objectMap.get(SELECT_NAME);
                    String certConnectionId = (String) objectMap.get(DomainRelationship.SELECT_ID);

                    // update relationship attributes
                    comment = (String) objectMap.get(SELECT_ATTRIBUTE_COMMENTS);
                    expirationDate = (String) objectMap.get(SELECT_ATTRIBUTE_EXPIRATION_DATE);
                    sourceID = (String) objectMap.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);
                    sourceIdentifier = (String) objectMap.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_IDENTIFIER);
                    sourceRelType = (String) objectMap.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE);

                    DomainRelationship domainRelationship = DomainRelationship.connect(context, domainObject, RELATIONSHIP_ROLLED_UP_PACKAGING_MATERIAL_CERTIFICATIONS, DomainObject.newInstance(context, certID));
                    //Attach RM Doc to pgRolledUpPLIPackagingCertifications
                    Hashtable hashtable = domainRelationship.getRelationshipData(context, relSelects);
                    StringList slRelId = (StringList) hashtable.get(DomainRelationship.SELECT_ID);
                    String sRelId = (String) slRelId.get(0);

                    // connecting multiple supporting docs -
                    StringList supportingDocIDs = certsReltoRel.getFromTomidsToFromObjid(context, certConnectionId, Boolean.TRUE, Boolean.FALSE);
                    if (null != supportingDocIDs && !supportingDocIDs.isEmpty()) {
                        // there is no api as of now to make rel-to-rel connection
                        for (String supportingDocID : supportingDocIDs) {
                            MqlUtil.mqlCommand(context, "add connection $1 to $2 fromrel $3", RELATIONSHIP_ROLLED_UP_CERTIFICATION_DOCUMENT, supportingDocID, sRelId);
                        }
                    }
                    attributeMap = new HashMap<>();
                    attributeMap.put(ATTRIBUTE_COMMENTS, UIUtil.isNullOrEmpty(comment) ? DomainConstants.EMPTY_STRING : comment);
                    attributeMap.put(ATTRIBUTE_EXPIRATION_DATE, UIUtil.isNullOrEmpty(expirationDate) ? DomainConstants.EMPTY_STRING : expirationDate);
                    attributeMap.put(ATTRIBUTE_ROLLUP_SOURCE_ID, UIUtil.isNullOrEmpty(sourceID) ? DomainConstants.EMPTY_STRING : sourceID);
                    attributeMap.put(ATTRIBUTE_ROLLUP_SOURCE_IDENTIFIER, UIUtil.isNullOrEmpty(sourceIdentifier) ? DomainConstants.EMPTY_STRING : sourceIdentifier);
                    attributeMap.put(ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE, UIUtil.isNullOrEmpty(sourceRelType) ? DomainConstants.EMPTY_STRING : sourceRelType);
                    domainRelationship.setAttributeValues(context, attributeMap);
                }
                // set calculate flag to false.
                domainObject.setAttributeValue(context, ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP, FALSE);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
