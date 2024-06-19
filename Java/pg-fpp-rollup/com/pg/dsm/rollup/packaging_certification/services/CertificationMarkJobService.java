package com.pg.dsm.rollup.packaging_certification.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.awl.util.Access;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.StringList;

public class CertificationMarkJobService {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;

    public CertificationMarkJobService(Context context) {
        this.context = context;
    }

    public void executeMarkingJob(Map<Object, Object> objectMap) throws FrameworkException {
        final String oid = (String) objectMap.get(DomainConstants.SELECT_ID);
        getCertCalculationsFromCron(objectMap);
        if (UIUtil.isNotNullAndNotEmpty(oid)) {
            DomainObject dObj = DomainObject.newInstance(this.context, oid);
            dObj.setAttributeValue(this.context, pgV3Constants.ATTRIBUTE_MARK_FOR_CERTIFICATION_ROLLUP, pgV3Constants.KEY_FALSE);
        }
    }

    /**
     * @param objectMap
     * @throws Exception
     */
    private void getCertCalculationsFromCron(Map<Object, Object> objectMap) throws FrameworkException {
        try {
            if (null != objectMap && !objectMap.isEmpty()) {
                final String oid = (String) objectMap.get(DomainConstants.SELECT_ID);
                final String oType = (String) objectMap.get(DomainConstants.SELECT_TYPE);

                DomainObject dObj = DomainObject.newInstance(this.context, oid);
                if ((UIUtil.isNotNullAndNotEmpty(oType) && UIUtil.isNotNullAndNotEmpty(oid))) {
                    this.setParentIPSCertCalculate(dObj);
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred ", e);
            throw e;
        }
    }

    /**
     * @param
     * @throws Exception
     */
    private void setParentIPSCertCalculate(DomainObject domainObj) throws FrameworkException {
        try {
            String strRelationshipPattern = pgV3Constants.RELATIONSHIP_EBOM + pgV3Constants.SYMBOL_COMMA + pgV3Constants.RELATIONSHIP_ALTERNATE;
            StringBuffer sbPostPattern = new StringBuffer(pgV3Constants.TYPE_FINISHEDPRODUCTPART)
                    .append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART)
                    .append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.TYPE_FABRICATEDPART);

            String sPostTypePattern = sbPostPattern.toString();
            StringList busSelects = new StringList(5);
            busSelects.addElement(DomainObject.SELECT_ID);
            busSelects.addElement(DomainObject.SELECT_TYPE);
            busSelects.addElement(DomainObject.SELECT_CURRENT);
            busSelects.addElement(DomainObject.SELECT_POLICY);
            busSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP);

            String arrLatestRev[] = new String[3];
            StringList processedParentList = new StringList();

            //Get Parent object Roll Up Marking
            MapList mlIPS = domainObj.getRelatedObjects(this.context,  // context
                    strRelationshipPattern,                            // rel pattern
                    pgV3Constants.TYPE_PART,                           // type pattern
                    true,                                           // to side
                    false,                                         // from side
                    0,                                              // level
                    busSelects,                                        // bus selects
                    DomainConstants.EMPTY_STRINGLIST,                  // rel selects
                    DomainConstants.EMPTY_STRING,                      // object where
                    DomainConstants.EMPTY_STRING,                      // rel where
                    DomainConstants.EMPTY_STRING,                      // post rel pattern
                    sPostTypePattern,                                  // post type pattern
                    null);                                        // post pattern

            // Iterate each parent.
            Iterator iter = mlIPS.iterator();
            Map parentMap;
            String parentOid;
            String parentState;
            DomainObject domainParentObj;
            while (iter.hasNext()) {
                parentMap = (Map) iter.next();
                parentOid = (String) parentMap.get(DomainConstants.SELECT_ID);
                parentState = (String) parentMap.get(DomainConstants.SELECT_CURRENT);

                // get latest revision of parent.
                arrLatestRev = getLatestRelease(parentOid);
                if (null != arrLatestRev && arrLatestRev.length > 2) {
                    parentOid = arrLatestRev[0];
                    parentState = arrLatestRev[2];
                }
                // check if the state is release (confirm with Kannan also)
                if (pgV3Constants.STATE_PART_RELEASE.equalsIgnoreCase(parentState) &&
                        null != processedParentList &&
                        !processedParentList.contains(parentOid)) {

                    domainParentObj = DomainObject.newInstance(this.context, parentOid);

                    //turn on calculate flag on parent object.
                    this.setMultipleAttributeValues(domainParentObj);

                    // add the processed parent ID in the list.
                    processedParentList.add(parentOid);
                }
            }

            // get information of incoming object.
            Map selfInfoMap = domainObj.getInfo(this.context, busSelects);
            String selfOid = (String) selfInfoMap.get(DomainConstants.SELECT_ID);
            String selfType = (String) selfInfoMap.get(DomainConstants.SELECT_TYPE);
            String selfState = (String) selfInfoMap.get(DomainConstants.SELECT_CURRENT);
            String selfPolicy = (String) selfInfoMap.get(DomainConstants.SELECT_POLICY);

            //Self Roll-Up marking only if incoming object is of Parent type (FPP, PAP & FAB) and it's not MEP/SEP.
            if (sPostTypePattern.contains(selfType) &&
                    !(pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase(selfPolicy) ||
                            pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(selfPolicy))) {

                // get the revision if the incoming object.
                arrLatestRev = this.getLatestRelease(selfOid);
                parentState = selfState;
                if (null != arrLatestRev && arrLatestRev.length > 2) {
                    parentOid = arrLatestRev[0];
                    parentState = arrLatestRev[2];
                }
                // check if the state is release (confirm with Kannan also)
                if (pgV3Constants.STATE_PART_RELEASE.equalsIgnoreCase(parentState) && null != processedParentList && !processedParentList.contains(selfOid)) {

                    //turn on calculate flag on parent object.
                    this.setMultipleAttributeValues(domainObj);

                    // add the processed incoming ID in the list.
                    processedParentList.add(selfOid);
                }
            }

            // When incoming object is MEP/SEP.
            if (pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equals(selfPolicy) || pgV3Constants.POLICY_SUPPLIER_EQUIVALENT.equals(selfPolicy)) {

                // get its (MEP/SEP & alternate) and traverse through its parent and turn-on calculate flag.
                this.getMEPSEPAndAlternateParentObject(domainObj);
            }
            //When incoming object is MCP.
            if (pgV3Constants.POLICY_INTERNALMATERIAL.equals(selfPolicy)) {

                // get all where used parents.
                this.getMCPParentObject(domainObj);
            }
            // Get the substitute's of incoming object.
            StringList slSubstituteIDs = domainObj.getInfoList(this.context, pgV3Constants.SELECT_CATIA_APP_EBOM_SUBSTITUTE_EXPAND_ID);

            // if incoming object has substitute.
            if (slSubstituteIDs.size() > 0) {

                // get where used (as substitute) parents.
                this.getEBOMSubstituteIPS(domainObj, slSubstituteIDs);
            }

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred ", e);
            throw e;
        }
    }

    /**
     * @param domainObj
     * @throws Exception
     */
    private void getMCPParentObject(DomainObject domainObj) throws FrameworkException {
        try {
            String parentOid = DomainConstants.EMPTY_STRING;
            String parentPolicy = DomainConstants.EMPTY_STRING;

            // these are the parts which can have MCP.
            StringBuffer sbPostPattern = new StringBuffer(pgV3Constants.TYPE_PACKAGINGMATERIALPART)
                    .append(",")
                    .append(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART)
                    .append(",")
                    .append(pgV3Constants.TYPE_FABRICATEDPART);

            String sPostTypePattern = sbPostPattern.toString();

            StringList busSelects = new StringList(4);
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            busSelects.addElement(DomainConstants.SELECT_CURRENT);
            busSelects.addElement(DomainConstants.SELECT_POLICY);

            StringList relSelects = new StringList(1);
            relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);

            MapList listMCPs = domainObj.getRelatedObjects(this.context,    // context
                    pgV3Constants.REL_COMPONENT_MATERIAL,                   // rel pattern
                    pgV3Constants.TYPE_PART,                                // type pattern
                    true,                                                // to side
                    false,                                              // from side
                    (short) 1,                                              // recursion level
                    busSelects,                                             // object selects
                    DomainConstants.EMPTY_STRINGLIST,                       // rel selects
                    DomainConstants.EMPTY_STRING,                           // object where
                    DomainConstants.EMPTY_STRING,                           // rel where
                    DomainConstants.EMPTY_STRING,                           // post rel pattern
                    sPostTypePattern,                                       // post type pattern
                    null);                                             // post pattern

            if (null != listMCPs && !listMCPs.isEmpty()) {
                Iterator iter = listMCPs.iterator();
                Map parentMap;
                DomainObject domainParentObj;
                while (iter.hasNext()) {
                    parentMap = (Map) iter.next();
                    parentOid = (String) parentMap.get(DomainConstants.SELECT_ID);
                    parentPolicy = (String) parentMap.get(DomainConstants.SELECT_POLICY);
                    domainParentObj = DomainObject.newInstance(this.context, parentOid);

                    //if MCP's Parent is MEP/SEP.
                    if (pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equals(parentPolicy) ||
                            pgV3Constants.POLICY_SUPPLIER_EQUIVALENT.equals(parentPolicy)) {

                        // get its where-used parent (MEP/SEP) and turn-on calculate flag.
                        this.getMEPSEPAndAlternateParentObject(domainParentObj);

                    } else {

                        // get its where-used parent and turn-on calculate flag.
                        this.setParentIPSCertCalculate(domainParentObj);
                    }
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred ", e);
            throw e;
        }
    }

    /**
     * @param domainObj
     * @return
     * @throws Exception
     */
    private void getMEPSEPAndAlternateParentObject(DomainObject domainObj) throws FrameworkException {
        try {
            StringList busSelects = new StringList(4);
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            busSelects.addElement(DomainConstants.SELECT_CURRENT);
            busSelects.addElement(DomainConstants.SELECT_POLICY);

            StringList relSelects = new StringList(1);
            relSelects.add(DomainRelationship.SELECT_RELATIONSHIP_ID);

            String parentOid;
            String parentPolicy;

            Pattern relationshipPattern = new Pattern(pgV3Constants.RELATIONSHIP_SUPPLIER_EQUIVALENT);
            relationshipPattern.addPattern(DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT);
            relationshipPattern.addPattern(DomainConstants.RELATIONSHIP_ALTERNATE);

            //get alternate or MEP/SEP Parent (or where used as Alternate or as MEP/SEP)
            MapList mlIPS = domainObj.getRelatedObjects(this.context,
                    relationshipPattern.getPattern(),// relationship pattern
                    DomainConstants.TYPE_PART, // object pattern
                    busSelects, // object selects
                    relSelects, // relationship selects
                    true, // to direction
                    false, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    null,
                    0); // relationship where clause

            if (null != mlIPS && !mlIPS.isEmpty()) {
                Iterator iter = mlIPS.iterator();
                Map parentMap;
                DomainObject domainParentObj;
                while (iter.hasNext()) {
                    parentMap = (Map) iter.next();
                    parentOid = (String) parentMap.get(DomainConstants.SELECT_ID);
                    parentPolicy = (String) parentMap.get(pgV3Constants.SELECT_POLICY);
                    domainParentObj = DomainObject.newInstance(this.context, parentOid);
                    //if getting alternate parent obj as MEP/SEP (or where used as Alternate or as MEP/SEP)
                    if (pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equals(parentPolicy) || pgV3Constants.POLICY_SUPPLIER_EQUIVALENT.equals(parentPolicy)) {
                        getMEPSEPAndAlternateParentObject(domainParentObj);
                    } else {
                        setParentIPSCertCalculate(domainParentObj);
                    }
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred ", e);
            throw e;
        }
    }

    /**
     * This Method will set pgCOSCalculate value to True for IPS having current IPS as Substitute
     *
     * @param domainObj
     */
    private void getEBOMSubstituteIPS(DomainObject domainObj, StringList slSubstituteIDs) throws FrameworkException {
        try {
            if (null != slSubstituteIDs) {
                String substituteOid;
                DomainObject substituteObj;
                for (int i = 0; i < slSubstituteIDs.size(); i++) {
                    substituteOid = (String) slSubstituteIDs.get(i);
                    substituteObj = DomainObject.newInstance(this.context, substituteOid);
                    setParentIPSCertCalculate(substituteObj);
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred ", e);
            throw e;
        }
    }


    /**
     * to get latest released revision
     *
     * @param sObjectID
     * @return
     * @throws Exception
     */
    private String[] getLatestRelease(String sObjectID) throws FrameworkException {
        try {
            StringList busSelects = new StringList();
            busSelects.add(pgV3Constants.SELECT_ID);
            busSelects.add(pgV3Constants.SELECT_REVISION);
            busSelects.add(pgV3Constants.SELECT_CURRENT);
            String oid = "";
            String rev = "";
            String current = "";
            String[] arrayString = new String[3];
            if (sObjectID != null && sObjectID.length() > 0) {
                DomainObject dom = DomainObject.newInstance(this.context, sObjectID);

                MapList revInfoList = dom.getRevisionsInfo(this.context, busSelects, new StringList());
                int size = revInfoList.size();
                for (int i = size - 1; i >= 0; i--) {
                    Map revInfoMap = (Map) revInfoList.get(i);

                    oid = (String) revInfoMap.get(pgV3Constants.SELECT_ID);
                    rev = (String) revInfoMap.get(pgV3Constants.SELECT_REVISION);
                    current = (String) revInfoMap.get(pgV3Constants.SELECT_CURRENT);
                    if (pgV3Constants.STATE_PART_RELEASE.equalsIgnoreCase(current)) {
                        arrayString[0] = oid;
                        arrayString[1] = rev;
                        arrayString[2] = current;
                        return arrayString;
                    }
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred ", e);
            throw e;
        }

        return null;
    }


    /**
     * @param
     * @return void
     * @throws Exception
     * @Desc This utility method is used to set attribute value.
     * @Added by DSM-2015x.1 for COS on 02-02-2016
     */
    private void setMultipleAttributeValues(DomainObject domainObj) throws FrameworkException {
        try {
            StringList busSelects = new StringList(4);
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_POLICY);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            busSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
            Map infoMap = domainObj.getInfo(this.context, busSelects);
            String objectId = (String) infoMap.get(DomainConstants.SELECT_ID);
            if (UIUtil.isNotNullAndNotEmpty(objectId)) {
                boolean hasModifyAccess = Access.hasAccess(this.context, objectId, "modify");
                if (hasModifyAccess) {
                    String strPolicy = (String) infoMap.get(DomainConstants.SELECT_POLICY);
                    if (!(pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase(strPolicy) || pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy))) {
                        Map attributeMap = new HashMap();
                        attributeMap.put(pgV3Constants.ATTRIBUTE_CALCULATE_CERTIFICATION_ROLLUP, pgV3Constants.KEY_TRUE);
                        domainObj.setAttributeValues(this.context, attributeMap);
                    }
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred ", e);
            throw e;
        }
    }
}
