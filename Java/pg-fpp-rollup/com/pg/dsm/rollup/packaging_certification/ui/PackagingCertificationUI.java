package com.pg.dsm.rollup.packaging_certification.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PackagingCertificationUI implements pgV3Constants {
    private static final Logger logger = Logger.getLogger(PackagingCertificationUI.class.getName());
    Context context;

    public PackagingCertificationUI(Context context) {
        this.context = context;
    }

    /**
     * @param partID
     * @return
     * @throws Exception
     */
    public MapList getRolledUpCertifications(String partID) throws Exception {
        MapList objectList = new MapList();
        try {
            Pattern relPattern = new Pattern(RELATIONSHIP_ROLLED_UP_PACKAGING_MATERIAL_CERTIFICATIONS);
            Pattern typePattern = new Pattern(TYPE_PG_PLI_PACKAGING_MATERIAL_CERTIFICATION);
            String strPosition = "1";
            StringList busSelect = new StringList(3);
            busSelect.addElement(DomainConstants.SELECT_ID);
            busSelect.addElement(DomainConstants.SELECT_NAME);
            busSelect.addElement(DomainConstants.SELECT_TYPE);
            StringList relSelects = new StringList(4);
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);

            relSelects.addElement(SELECT_ATTRIBUTE_COMMENTS);
            relSelects.addElement(SELECT_ATTRIBUTE_EXPIRATION_DATE);
            relSelects.add(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);
            relSelects.add(SELECT_ATTRIBUTE_ROLLUP_SOURCE_IDENTIFIER);
            relSelects.add(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE);

            DomainObject domainObject = DomainObject.newInstance(context, partID);
            objectList = domainObject.getRelatedObjects(context,
                    relPattern.getPattern(),  //relationship pattern
                    typePattern.getPattern(),  // object pattern
                    busSelect,                 // object selects
                    relSelects,              // relationship selects
                    false,                        // to direction
                    true,                       // from direction
                    (short) 1,                    // recursion level
                    null,                        // object where clause
                    null,
                    0);

        } catch (Exception e) {
            throw e;
        }
        return objectList;
    }


    /**
     * Get the Certifications connected to MCPs.
     *
     * @param domObj            - Parent Part Object
     * @param strSourcePartName - Source Part Name
     * @param strSourcePartId   - Source Part Id
     * @return MapList Certifications connected to MCPs
     * @throws Exception
     */
    private MapList getMCPRelatedPackagingCertifications(DomainObject domObj, String strSourcePartName, String strSourcePartId,String objectOid) throws Exception {
        MapList mlReturn = new MapList();
        try {
            StringList busSelects = new StringList(3);                     // putting elements to StringList
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);

            MapList listMCPs = domObj.getRelatedObjects(context,
                    REL_COMPONENT_MATERIAL,// relationship pattern
                    TYPE_INTERNAL_MATERIAL, // object pattern
                    busSelects, // object selects
                    null, // relationship selects
                    false, // to direction
                    true, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    null,
                    0); // relationship where clause
            Iterator<?> itr = listMCPs.iterator();
            MapList mlCertifications = null;
            MapList mlChildMCP = null;
            Map<?, ?> mMCPInfo = null;
            String strMCPId = null;
            DomainObject domMCP = null;
            Map<String, String> mPartInfo = null;
            while (itr.hasNext()) {
                mMCPInfo = (Map) itr.next();
                strMCPId = (String) mMCPInfo.get(DomainConstants.SELECT_ID);
                if (UIUtil.isNotNullAndNotEmpty(strMCPId)) {
                    domMCP = DomainObject.newInstance(context, strMCPId);
                    mPartInfo = new HashMap<>();
                    mPartInfo.put(STR_LABEL_SOURCE_PART_NAME, strSourcePartName);
                    //mPartInfo.put(STR_LABEL_SOURCE_PART_ID, strSourcePartId);
                    mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID, strMCPId);
                    mPartInfo.put(STR_LABEL_PART_NAME, (String) mMCPInfo.get(DomainConstants.SELECT_NAME));
                    mPartInfo.put(STR_LABEL_PART_ID, strMCPId);
                    mPartInfo.put(STR_LABEL_PART_TYPE, (String) mMCPInfo.get(DomainConstants.SELECT_TYPE));
                    mPartInfo.put(STR_IS_DISABLE_SELECTION, RANGE_VALUE_SMALL_TRUE);
                    mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE, REL_COMPONENT_MATERIAL);
                    mlCertifications = getRelatedPackagingCertifications(domMCP, strSourcePartName, strSourcePartId, RANGE_VALUE_SMALL_FALSE, null,objectOid);
                    addPartNameRevisionToList(mlCertifications, mPartInfo);
                    mlReturn.addAll(mlCertifications);

                    mlChildMCP = getChildMCPs(domMCP, strMCPId,objectOid);
                    if (!mlChildMCP.isEmpty()) {
                        mlReturn.addAll(mlChildMCP);
                    }

                }
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception - ", ex);
            throw ex;
        }
        return mlReturn;
    }

    /**
     * Get the Certifications connected to Chils MCPs.
     *
     * @return MapList Certifications connected to MCPs
     * @throws Exception
     */
    private MapList getChildMCPs(DomainObject domMCP, String strSourceId,String parentObjectOid) throws Exception {
        MapList mlReturn = new MapList();
        StringList busSelects = new StringList(3);                     // putting elements to StringList
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        String strSourcePartName = domMCP.getName();
        MapList listChildMCPs = domMCP.getRelatedObjects(context,
                REL_COMPONENT_MATERIAL,// relationship pattern
                TYPE_INTERNAL_MATERIAL, // object pattern
                busSelects, // object selects
                null, // relationship selects
                false, // to direction
                true, // from direction
                (short) 1, // recursion level
                null, // object where clause
                null,
                0);
        MapList mlCertifications = null;
        Map<?, ?> mChildMCPInfo = null;
        String strChildMCPId = null;
        Map<String, String> mPartInfo = null;
        Iterator<?> itrChild = listChildMCPs.iterator();
        DomainObject domChildMCP = null;
        while (itrChild.hasNext()) {
            mChildMCPInfo = (Map) itrChild.next();
            strChildMCPId = (String) mChildMCPInfo.get(DomainConstants.SELECT_ID);
            if (UIUtil.isNotNullAndNotEmpty(strChildMCPId)) {
                domChildMCP = DomainObject.newInstance(context, strChildMCPId);
                mPartInfo = new HashMap<>();
                mPartInfo.put(STR_LABEL_SOURCE_PART_NAME, strSourcePartName);
                mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID, strChildMCPId);
                mPartInfo.put(STR_LABEL_PART_NAME, (String) mChildMCPInfo.get(DomainConstants.SELECT_NAME));
                mPartInfo.put(STR_LABEL_PART_ID, strChildMCPId);
                mPartInfo.put(STR_LABEL_PART_TYPE, (String) mChildMCPInfo.get(DomainConstants.SELECT_TYPE));
                mPartInfo.put(STR_IS_DISABLE_SELECTION, RANGE_VALUE_SMALL_TRUE);
                mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE, REL_COMPONENT_MATERIAL);
                mlCertifications = getRelatedPackagingCertifications(domChildMCP, strSourcePartName, strSourceId, RANGE_VALUE_SMALL_FALSE, null, parentObjectOid);
                addPartNameRevisionToList(mlCertifications, mPartInfo);
                mlReturn.addAll(mlCertifications);
            }
        }
        return mlReturn;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public MapList getSelfRelatedPackagingCertifications(Map<Object, Object> inputMap,String objectOid) throws Exception {
        MapList mlReturn = new MapList();
        try {
            String strSourcePartId = (String) inputMap.get(DomainConstants.SELECT_ID);
            String strSourcePartName = (String) inputMap.get(DomainConstants.SELECT_NAME);
            String strSourcePartType = (String) inputMap.get(DomainConstants.SELECT_TYPE);
            String strIsDisableSelection = RANGE_VALUE_SMALL_FALSE;
            String strPosition = "1";

            StringList busSelects = new StringList(3);                     // putting elements to StringList
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            StringList relSelects = new StringList(4);
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID); // Relationship Selects
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);
            relSelects.addElement(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_COMMENTS));
            relSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);

            DomainObject domObj = DomainObject.newInstance(context, strSourcePartId);
            Map mObjInfo = domObj.getInfo(context, busSelects);

            //CW-05 -To Do
            disconnectParentCertWhichNotConnectedToChild( context, objectOid, strSourcePartId, strSourcePartType);
            //CW-05 -To Do
            
            // Retrieving related item
            Pattern relPattern = new Pattern(RELATIONSHIP_PG_PLI_PACKAGING_CERTIFICATIONS);
            Pattern typePattern = new Pattern(TYPE_PG_PLI_PACKAGING_MATERIAL_CERTIFICATION);
            mlReturn = domObj.getRelatedObjects(context,
                    relPattern.getPattern(),  //relationship pattern
                    typePattern.getPattern(),  // object pattern
                    busSelects,                 // object selects
                    relSelects,              // relationship selects
                    false,                        // to direction
                    true,                       // from direction
                    (short) 1,                    // recursion level
                    null,                        // object where clause
                    null,
                    0);
            Map<String, String> mPartInfo = new HashMap<>();
            mPartInfo.put(STR_LABEL_SOURCE_PART_NAME, strSourcePartName);
            mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID, strSourcePartId);
            mPartInfo.put(STR_LABEL_PART_NAME, (String) mObjInfo.get(DomainConstants.SELECT_NAME));
            mPartInfo.put(STR_LABEL_PART_ID, (String) mObjInfo.get(DomainConstants.SELECT_ID));
            mPartInfo.put(STR_LABEL_PART_TYPE, (String) mObjInfo.get(DomainConstants.SELECT_TYPE));
            mPartInfo.put(STR_IS_DISABLE_SELECTION, strIsDisableSelection);
            mPartInfo.put(STR_POSITION, strPosition);
            addPartNameRevisionToList(mlReturn, mPartInfo);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception - ", ex);
            throw ex;
        }
        return mlReturn;
    }

    /**
     * @param inputMap
     * @return
     * @throws Exception
     */
    public MapList getMEPSEPCertifications(Map<Object, Object> inputMap,String objectOid) throws Exception {
        MapList mlReturn = new MapList();
        MapList mlCertification = new MapList();
        try {
            String strSourcePartId = (String) inputMap.get(DomainConstants.SELECT_ID);
            String strSourceType = (String) inputMap.get(DomainConstants.SELECT_TYPE);
            String strSourcePartName = (String) inputMap.get(DomainConstants.SELECT_NAME);
            String strSourcePolicy = (String) inputMap.get(DomainConstants.SELECT_POLICY);
            DomainObject domPart = DomainObject.newInstance(context, strSourcePartId);
            //Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453 - Starts
            if(!TYPE_RAWMATERIALPART.equals(strSourceType)){ 
            if (strSourceType.equals(TYPE_PACKAGINGMATERIAL_PART) || strSourceType.equals(TYPE_PACKAGINGASSEMBLYPART)) {
	                mlCertification = getMCPRelatedPackagingCertifications(domPart, strSourcePartName, strSourcePartId,objectOid);
                mlReturn.addAll(mlCertification);
            }
            if (strSourceType.equals(TYPE_PACKAGINGMATERIAL_PART) || strSourceType.equals(TYPE_PACKAGINGASSEMBLYPART)) {
	                mlCertification = getEquivalentRelatedPackagingCertifications(domPart, strSourcePartName, strSourcePartId, strSourcePolicy,objectOid);
                mlReturn.addAll(mlCertification);
            }
            }
            //Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453 - End
          
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception - ", e);
            throw e;
        }
        return mlReturn;
    }


    /**
     * Return Certifications connected to MEP/SEP/Alternates.
     *
     * @param domObj            - Context Domain Object
     * @param strSourcePartName - Source Part Name
     * @param strSourcePartId   - Source Part Id
     * @param strPolicy         - String Part Policy
     * @return Certifications list
     * @throws Exception
     */
    private MapList getEquivalentRelatedPackagingCertifications(DomainObject domObj, String strSourcePartName, String strSourcePartId, String strPolicy,String parentObjectOid) throws Exception {
        MapList mlReturn = new MapList();
        try {
            StringList busSelects = new StringList(4);                     // putting elements to StringList
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            busSelects.addElement(DomainConstants.SELECT_CURRENT);

            StringList relSelects = new StringList(1);
            relSelects.add(DomainRelationship.SELECT_TYPE);
            relSelects.add(DomainRelationship.SELECT_NAME);

            boolean isFrom = true;
            boolean isTo = false;

            if (POLICY_MANUFACTURER_EQUIVALENT.equals(strPolicy) || POLICY_SUPPLIER_EQUIVALENT.equals(strPolicy)) {
                isFrom = false;
                isTo = true;
            }

            Pattern relationshipPattern = new Pattern(RELATIONSHIP_SUPPLIER_EQUIVALENT);
            relationshipPattern.addPattern(DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT);

            MapList listMEPsSEPs = domObj.getRelatedObjects(context,
                    relationshipPattern.getPattern(),// relationship pattern
                    DomainConstants.TYPE_PART, // object pattern
                    busSelects, // object selects
                    relSelects, // relationship selects
                    isTo, // to direction
                    isFrom, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    null,
                    0); // relationship where clause
            Iterator<?> itr = listMEPsSEPs.iterator();
            MapList mlCertifications = null;
            Map<?, ?> mEquivalentInfo = null;
            String strPartId = null;
            String strRelType = null;
            String strCurrent = null;
            DomainObject domEqvPart = null;
            Map<String, String> mPartInfo = null;
            while (itr.hasNext()) {
                mEquivalentInfo = (Map) itr.next();
                strPartId = (String) mEquivalentInfo.get(DomainConstants.SELECT_ID);
                strCurrent = (String) mEquivalentInfo.get(DomainConstants.SELECT_CURRENT);
                if (UIUtil.isNotNullAndNotEmpty(strPartId) && !strCurrent.equals(STATE_OBSOLETE)) {
                    strRelType = (String) mEquivalentInfo.get(DomainRelationship.SELECT_TYPE);
                    domEqvPart = DomainObject.newInstance(context, strPartId);
                    mlCertifications = getRelatedPackagingCertifications(domEqvPart, strSourcePartName, strSourcePartId, RANGE_VALUE_SMALL_FALSE, null,parentObjectOid);
                    mPartInfo = new HashMap<>();
                    mPartInfo.put(STR_LABEL_SOURCE_PART_NAME, strSourcePartName);
                    //mPartInfo.put(STR_LABEL_SOURCE_PART_ID, strSourcePartId);
                    mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID, strPartId);
                    mPartInfo.put(STR_LABEL_PART_NAME, (String) mEquivalentInfo.get(DomainConstants.SELECT_NAME));
                    mPartInfo.put(STR_LABEL_PART_ID, strPartId);
                    mPartInfo.put(STR_LABEL_PART_TYPE, (String) mEquivalentInfo.get(DomainConstants.SELECT_TYPE));
                    mPartInfo.put(STR_IS_DISABLE_SELECTION, RANGE_VALUE_SMALL_TRUE);
                    mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE, (String) mEquivalentInfo.get(DomainRelationship.SELECT_NAME));
                    addPartNameRevisionToList(mlCertifications, mPartInfo);
                    mlReturn.addAll(mlCertifications);
                    if (!DomainConstants.RELATIONSHIP_ALTERNATE.equals(strRelType)) {
                        mlCertifications = getMCPRelatedPackagingCertifications(domEqvPart, strSourcePartName, strSourcePartId,parentObjectOid);
                        mlReturn.addAll(mlCertifications);
                        mlCertifications = getAlternatePartRelatedPackagingCertifications(domEqvPart, strSourcePartName, strSourcePartId,parentObjectOid);
                        mlReturn.addAll(mlCertifications);
                    }
                }
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception - ", ex);
            throw ex;
        }
        return mlReturn;
    }

    /**
     * Method to get the Related Certifications connected to Packaging materials.
     *
     * @param domObj                - Domain Object for which we need to get the cer
     * @param strSourcePartName     - Source Part Name
     * @param strSourcePartId       - Source Part Id
     * @param strIsDisableSelection - Is Disable Selection of checkbox
     * @param strPosition           - Level at which the objects need to be added in the Maplist
     * @return MapList of Certifications connected to object
     * @throws Exception
     */
    private MapList getRelatedPackagingCertifications(DomainObject domObj, String strSourcePartName, String strSourcePartId, String strIsDisableSelection, String strPosition,String parentObjectOid) throws Exception {
        MapList mlReturn = new MapList();
        try {
            StringList busSelects = new StringList(3);                     // putting elements to StringList
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            StringList relSelects = new StringList(4);
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID); // Relationship Selects
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);
            relSelects.addElement(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_COMMENTS));
            relSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);

            Map mObjInfo = domObj.getInfo(context, busSelects);

            // Retrieving related item
            Pattern relPattern = new Pattern(RELATIONSHIP_PG_PLI_PACKAGING_CERTIFICATIONS);
            Pattern typePattern = new Pattern(TYPE_PG_PLI_PACKAGING_MATERIAL_CERTIFICATION);

            //Added to get COrrect Source Id
            String sSourcePartId = domObj.getInfo(context, DomainConstants.SELECT_ID);
            
            //Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453 - Starts
            String sSourcePartType = domObj.getInfo(context, DomainConstants.SELECT_TYPE);
            disconnectParentCertWhichNotConnectedToChild( context, parentObjectOid, sSourcePartId, sSourcePartType);
            //Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453 - End
            
            mlReturn = domObj.getRelatedObjects(context,
                    relPattern.getPattern(),  //relationship pattern
                    typePattern.getPattern(),  // object pattern
                    busSelects,                 // object selects
                    relSelects,              // relationship selects
                    false,                        // to direction
                    true,                       // from direction
                    (short) 1,                    // recursion level
                    null,                        // object where clause
                    null,
                    0);
            Map<String, String> mPartInfo = new HashMap<>();
            mPartInfo.put(STR_LABEL_SOURCE_PART_NAME, strSourcePartName);
            mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID, sSourcePartId);
            mPartInfo.put(STR_LABEL_PART_NAME, (String) mObjInfo.get(DomainConstants.SELECT_NAME));
            mPartInfo.put(STR_LABEL_PART_ID, (String) mObjInfo.get(DomainConstants.SELECT_ID));
            mPartInfo.put(STR_LABEL_PART_TYPE, (String) mObjInfo.get(DomainConstants.SELECT_TYPE));
            mPartInfo.put(STR_IS_DISABLE_SELECTION, strIsDisableSelection);
            mPartInfo.put(STR_POSITION, strPosition);
            addPartNameRevisionToList(mlReturn, mPartInfo);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception - ", ex);
            throw ex;
        }
        return mlReturn;
    }

    /**
     * Method to update the final MapList with the additional Part info.
     *
     * @param mlCertifications - Original MapList
     * @param mPartInfo        - Contains additional part info
     * @throws Exception
     */
    private void addPartNameRevisionToList(MapList mlCertifications, Map<String, String> mPartInfo) throws Exception {
        Iterator<?> itr = mlCertifications.iterator();
        Map mCertificationInfo = null;
        String strPosition = mPartInfo.get(STR_POSITION);
        String strIsDisableSelection = mPartInfo.get(STR_IS_DISABLE_SELECTION);
        String strRollUpSourceRelType = mPartInfo.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE);
        while (itr.hasNext()) {
            mCertificationInfo = (Map) itr.next();
            mCertificationInfo.put(STR_LABEL_PART_NAME, mPartInfo.get(STR_LABEL_PART_NAME));
            mCertificationInfo.put(STR_LABEL_SOURCE_PART_NAME, mPartInfo.get(STR_LABEL_SOURCE_PART_NAME));
            mCertificationInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID, mPartInfo.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID));
            mCertificationInfo.put(STR_LABEL_PART_ID, mPartInfo.get(STR_LABEL_PART_ID));
            mCertificationInfo.put(STR_LABEL_PART_TYPE, mPartInfo.get(STR_LABEL_PART_TYPE));

            if (UIUtil.isNotNullAndNotEmpty(strRollUpSourceRelType)) {
                mCertificationInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE, strRollUpSourceRelType);
            }
            if (UIUtil.isNotNullAndNotEmpty(strPosition)) {
                mCertificationInfo.put(STR_POSITION, strPosition);
            } else {
                mCertificationInfo.put(STR_POSITION, "2");
            }
            mCertificationInfo.put("disableSelection", strIsDisableSelection);
        }
    }

    /**
     * Method to get the Alternate Part certifications.
     *
     * @param domObj
     * @param strSourcePartName
     * @param strSourcePartId
     * @return ALternate Certifications list
     * @throws Exception
     */
    private MapList getAlternatePartRelatedPackagingCertifications(DomainObject domObj, String strSourcePartName, String strSourcePartId,String parentObjectOid) throws Exception {
        MapList mlReturn = new MapList();
        try {
            StringList selectStmts = new StringList(4);                     // putting elements to StringList
            selectStmts.addElement(DomainConstants.SELECT_ID);
            selectStmts.addElement(DomainConstants.SELECT_NAME);
            selectStmts.addElement(DomainConstants.SELECT_TYPE);
            selectStmts.addElement(DomainConstants.SELECT_CURRENT);

            StringList strRelSelList = new StringList(1);
            strRelSelList.add(DomainRelationship.SELECT_TYPE);

            Pattern relationshipPattern = new Pattern(DomainConstants.RELATIONSHIP_ALTERNATE);

            MapList listAlternates = domObj.getRelatedObjects(context,
                    relationshipPattern.getPattern(),// relationship pattern
                    DomainConstants.TYPE_PART, // object pattern
                    selectStmts, // object selects
                    strRelSelList, // relationship selects
                    false, // to direction
                    true, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    null,
                    0); // relationship where clause
            Iterator<?> itr = listAlternates.iterator();
            MapList mlCertifications = null;
            Map<?, ?> mEquivalentInfo = null;
            String strPartId = null;
            String strCurrent = null;
            DomainObject domAltPart = null;
            Map<String, String> mPartInfo = null;
            while (itr.hasNext()) {
                mEquivalentInfo = (Map) itr.next();
                strPartId = (String) mEquivalentInfo.get(DomainConstants.SELECT_ID);
                strCurrent = (String) mEquivalentInfo.get(DomainConstants.SELECT_CURRENT);
                if (UIUtil.isNotNullAndNotEmpty(strPartId) && !strCurrent.equals(STATE_OBSOLETE)) {
                    domAltPart = DomainObject.newInstance(context, strPartId);
                    mlCertifications = getRelatedPackagingCertifications(domAltPart, strSourcePartName, strSourcePartId, RANGE_VALUE_SMALL_FALSE, null,parentObjectOid);
                    mPartInfo = new HashMap<>();
                    mPartInfo.put(STR_LABEL_SOURCE_PART_NAME, strSourcePartName);
                    //mPartInfo.put(STR_LABEL_SOURCE_PART_ID, strSourcePartId);
                    mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID, strPartId);
                    mPartInfo.put(STR_LABEL_PART_NAME, (String) mEquivalentInfo.get(DomainConstants.SELECT_NAME));
                    mPartInfo.put(STR_LABEL_PART_ID, strPartId);
                    mPartInfo.put(STR_LABEL_PART_TYPE, (String) mEquivalentInfo.get(DomainConstants.SELECT_TYPE));
                    mPartInfo.put(STR_IS_DISABLE_SELECTION, RANGE_VALUE_SMALL_TRUE);
                    mPartInfo.put(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE, relationshipPattern.getPattern());
                    addPartNameRevisionToList(mlCertifications, mPartInfo);
                    mlReturn.addAll(mlCertifications);
                }
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception - ", ex);
            throw ex;
        }
        return mlReturn;
    }


    /**
     * Return Certifications connected to MEP/SEP/Alternates.
     *
     * @param domObj            - Context Domain Object
     * @param strSourcePartName - Source Part Name
     * @param strSourcePartId   - Source Part Id
     * @param strPolicy         - String Part Policy
     * @return Certifications list
     * @throws Exception
     */
    private MapList getEquivalentAndAlternatePartRelatedPackagingCertifications(DomainObject domObj, String strSourcePartName, String strSourcePartId, String strPolicy,String parentObjectOid) throws Exception {
        MapList mlReturn = new MapList();
        try {
            StringList selectStmts = new StringList(4);                     // putting elements to StringList
            selectStmts.addElement(DomainConstants.SELECT_ID);
            selectStmts.addElement(DomainConstants.SELECT_NAME);
            selectStmts.addElement(DomainConstants.SELECT_TYPE);
            selectStmts.addElement(DomainConstants.SELECT_CURRENT);

            StringList strRelSelList = new StringList(1);
            strRelSelList.add(DomainRelationship.SELECT_TYPE);

            boolean isFrom = true;
            boolean isTo = false;

            if (POLICY_MANUFACTURER_EQUIVALENT.equals(strPolicy) || POLICY_SUPPLIER_EQUIVALENT.equals(strPolicy)) {
                isFrom = false;
                isTo = true;
            }

            Pattern relationshipPattern = new Pattern(RELATIONSHIP_SUPPLIER_EQUIVALENT);
            relationshipPattern.addPattern(DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT);
            relationshipPattern.addPattern(DomainConstants.RELATIONSHIP_ALTERNATE);

            MapList listMEPsSEPs = domObj.getRelatedObjects(context,
                    relationshipPattern.getPattern(),// relationship pattern
                    DomainConstants.TYPE_PART, // object pattern
                    selectStmts, // object selects
                    strRelSelList, // relationship selects
                    isTo, // to direction
                    isFrom, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    null,
                    0); // relationship where clause
            Iterator<?> itr = listMEPsSEPs.iterator();
            MapList mlCertifications = null;
            Map<?, ?> mEquivalentInfo = null;
            String strPartId = null;
            String strRelType = null;
            String strCurrent = null;
            DomainObject domEqvPart = null;
            Map<String, String> mPartInfo = null;
            while (itr.hasNext()) {
                mEquivalentInfo = (Map) itr.next();
                strPartId = (String) mEquivalentInfo.get(DomainConstants.SELECT_ID);
                strCurrent = (String) mEquivalentInfo.get(DomainConstants.SELECT_CURRENT);
                if (UIUtil.isNotNullAndNotEmpty(strPartId) && !strCurrent.equals(STATE_OBSOLETE)) {
                    strRelType = (String) mEquivalentInfo.get(DomainRelationship.SELECT_TYPE);
                    domEqvPart = DomainObject.newInstance(context, strPartId);
                    mlCertifications = getRelatedPackagingCertifications(domEqvPart, strSourcePartName, strSourcePartId, RANGE_VALUE_SMALL_FALSE, null,parentObjectOid);
                    mPartInfo = new HashMap<>();
                    mPartInfo.put(STR_LABEL_SOURCE_PART_NAME, strSourcePartName);
                    mPartInfo.put(STR_LABEL_SOURCE_PART_ID, strSourcePartId);
                    mPartInfo.put(STR_LABEL_PART_NAME, (String) mEquivalentInfo.get(DomainConstants.SELECT_NAME));
                    mPartInfo.put(STR_LABEL_PART_ID, strPartId);
                    mPartInfo.put(STR_LABEL_PART_TYPE, (String) mEquivalentInfo.get(DomainConstants.SELECT_TYPE));
                    mPartInfo.put(STR_IS_DISABLE_SELECTION, RANGE_VALUE_SMALL_TRUE);
                    addPartNameRevisionToList(mlCertifications, mPartInfo);
                    mlReturn.addAll(mlCertifications);
                    if (!DomainConstants.RELATIONSHIP_ALTERNATE.equals(strRelType)) {
                        mlCertifications = getMCPRelatedPackagingCertifications(domEqvPart, strSourcePartName, strSourcePartId,parentObjectOid);
                        mlReturn.addAll(mlCertifications);
                        mlCertifications = getAlternatePartRelatedPackagingCertifications(domEqvPart, strSourcePartName, strSourcePartId,parentObjectOid);
                        mlReturn.addAll(mlCertifications);
                    } else if (DomainConstants.RELATIONSHIP_ALTERNATE.equals(strRelType)) {
                        mlCertifications = getEquivalentAndAlternatePartRelatedPackagingCertifications(domEqvPart, strSourcePartName, strSourcePartId, strPolicy,parentObjectOid);
                        mlReturn.addAll(mlCertifications);
                    }
                }
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception - ", ex);
            throw ex;
        }
        return mlReturn;
    }

    /**
     * Get certifications from substitute parts connected to PAP or COP part.
     *
     * @param domObj            - Part Object
     * @param strSourcePartName - Source Part Name
     * @param strSourcePartId   - Source Part Id
     * @return - Certifications list
     * @throws Exception
     */
    private MapList getPAPSubtitutePRelatedPackagingCertifications(DomainObject domObj, String strSourcePartName, String strSourcePartId, String parentObjectOid) throws Exception {
        MapList mlReturn = new MapList();
        try {
            StringList busSelects = new StringList(5);                     // putting elements to StringList
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            busSelects.addElement(DomainConstants.SELECT_POLICY);
            busSelects.addElement(DomainConstants.SELECT_CURRENT);

            String strSelSubId = new StringBuilder("frommid[").append(RELATIONSHIP_EBOM_SUBSTITUTE).append("].to.id").toString();
            StringList relSelects = new StringList(1);
            relSelects.addElement(strSelSubId);

            Pattern patternRel = new Pattern(DomainConstants.RELATIONSHIP_EBOM);
            Pattern patternType = new Pattern(TYPE_PACKAGINGMATERIAL_PART);
            patternType.addPattern(TYPE_PACKAGINGASSEMBLYPART);
            patternType.addPattern(TYPE_PG_CONSUMERUNIT);

            MapList mlPMPs = domObj.getRelatedObjects(context,
                    patternRel.getPattern(),// relationship pattern
                    patternType.getPattern(), // object pattern
                    busSelects, // object selects
                    relSelects, // relationship selects
                    false, // to direction
                    true, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    null,
                    0); // relationship where clause
            Iterator<?> itr = mlPMPs.iterator();
            MapList mlCertifications = null;
            Map<?, ?> mPMPInfo = null;
            Map<?, ?> mSubPMPInfo = null;
            String strPMPId = null;
            String strType = null;
            String strCurrent = null;
            String strPolicy = null;
            StringList strSubPMPIdsList = null;
            StringList strSubstituteTypesList = StringList.create(TYPE_PACKAGINGASSEMBLYPART, TYPE_PACKAGINGMATERIAL_PART);

            DomainObject domPMP = null;
            DomainObject domSubPMP = null;
            while (itr.hasNext()) {
                mPMPInfo = (Map) itr.next();
                strPMPId = (String) mPMPInfo.get(DomainConstants.SELECT_ID);
                strCurrent = (String) mPMPInfo.get(DomainConstants.SELECT_CURRENT);
                if (UIUtil.isNotNullAndNotEmpty(strPMPId) && !strCurrent.equals(STATE_OBSOLETE)) {
                    domPMP = DomainObject.newInstance(context, strPMPId);
                    strType = (String) mPMPInfo.get(DomainConstants.SELECT_TYPE);
                    strPolicy = (String) mPMPInfo.get(DomainConstants.SELECT_POLICY);
                    mlCertifications = getRelatedPackagingCertifications(domPMP, strSourcePartName, strSourcePartId, RANGE_VALUE_SMALL_TRUE, null, parentObjectOid);
                    mlReturn.addAll(mlCertifications);
                    if (TYPE_PACKAGINGMATERIAL_PART.equals(strType)) {
                        mlCertifications = getMCPRelatedPackagingCertifications(domPMP, strSourcePartName, strSourcePartId,parentObjectOid);
                        mlReturn.addAll(mlCertifications);
                    }
                    mlCertifications = getEquivalentAndAlternatePartRelatedPackagingCertifications(domPMP, strSourcePartName, strSourcePartId, strPolicy, parentObjectOid);
                    mlReturn.addAll(mlCertifications);
                    strSubPMPIdsList = BusinessUtil.getStringList(mPMPInfo, strSelSubId);
                    for (String stOID : (java.util.List<String>) strSubPMPIdsList) {
                        domSubPMP = DomainObject.newInstance(context, stOID);
                        mSubPMPInfo = getSubstitutePartInfo(domSubPMP, busSelects);
                        strType = (String) mSubPMPInfo.get(DomainConstants.SELECT_TYPE);
                        strCurrent = (String) mSubPMPInfo.get(DomainConstants.SELECT_CURRENT);
                        if (strSubstituteTypesList.contains(strType) && !strCurrent.equals(STATE_OBSOLETE)) {
                            mlCertifications = getRelatedPackagingCertifications(domSubPMP, strSourcePartName, strSourcePartId, RANGE_VALUE_SMALL_TRUE, null, parentObjectOid);
                            mlReturn.addAll(mlCertifications);
                            mlCertifications = getEquivalentAndAlternatePartRelatedPackagingCertifications(domSubPMP, strSourcePartName, strSourcePartId, strPolicy, parentObjectOid);
                            mlReturn.addAll(mlCertifications);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception - ", ex);
            throw ex;
        }
        return mlReturn;
    }

    /**
     * Get substitute part info.
     *
     * @param domSub      Substitute Part Domain Object
     * @param selectStmts Select statements
     * @return Map part info
     * @throws Exception
     */
    private Map<?, ?> getSubstitutePartInfo(DomainObject domSub, StringList selectStmts) throws Exception {
        return domSub.getInfo(context, selectStmts);
    }

     
    /**
     * Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453
     * @param context
     * @param objectOid
     * @param partID
     * @param partType
     * @throws Exception
     */
    public void disconnectParentCertWhichNotConnectedToChild(Context context,String parentObjectOid,String partID,String partType) throws Exception {
        String sourceID=DomainConstants.EMPTY_STRING;
        String certID=DomainConstants.EMPTY_STRING;
        String relID=DomainConstants.EMPTY_STRING;
        MapList mlReturn = new MapList();
        try {
            DomainObject parentDomainObject = DomainObject.newInstance(context, parentObjectOid);
            DomainObject partIdDomainObject = DomainObject.newInstance(context, partID);
            boolean isCertConnectedToParent = parentDomainObject.hasRelatedObjects(context, RELATIONSHIP_ROLLED_UP_PACKAGING_MATERIAL_CERTIFICATIONS, true);
            if (isCertConnectedToParent) {
                mlReturn = getParentCertDetails(context, parentDomainObject, partID);
                Iterator<?> itr = mlReturn.iterator();
                Map next = null;
                while (itr.hasNext()) {
                    next = (Map) itr.next();
                    certID = (String) next.get(DomainConstants.SELECT_ID);
                    sourceID = (String) next.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);
                    relID = (String) next.get(DomainConstants.SELECT_RELATIONSHIP_ID);
                    if(UIUtil.isNotNullAndNotEmpty(certID) && UIUtil.isNotNullAndNotEmpty(sourceID) && !isChildPartHasCert(context,sourceID,certID)) {
                        if (sourceID.equals(partID)) {
                            DomainRelationship.disconnect(context, relID);
                        }
                    }
                }
            }
        } catch(Exception e){
            e.getMessage();
        }
    }

    /**
     * Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453
     * @param context
     * @param sourceID
     * @param certID
     * @return
     * @throws Exception
     */
    public boolean isChildPartHasCert(Context context,String sourceID,String certID)throws Exception{
        Boolean isCertConnected=true;
        StringList certIds = DomainObject.newInstance(context,sourceID).getInfoList(context,"from["+RELATIONSHIP_PACKAGING_MATERIAL_CERTIFICATIONS+"].to.id");
        if(!certIds.isEmpty()){
            if(certIds.contains(certID)) {
                isCertConnected = true;
            } else {
                isCertConnected=false;
            }
        } else {
            isCertConnected=false;
        }
        return isCertConnected;
    }
    /**
     * Modified by DSM(sogeti) for 2022x.5 rollup requirement 48453
     * @param context
     * @param parentDomainObject
     * @param partID
     * @return
     * @throws Exception
     */
    public MapList getParentCertDetails(Context context,DomainObject parentDomainObject,String  partID) throws Exception {
        MapList mlReturn = new MapList();
        StringList busSelects = new StringList(1);                     // putting elements to StringList
        busSelects.addElement(DomainConstants.SELECT_ID);

        StringList relSelects = new StringList(2);
        relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID); // Relationship Selects
        relSelects.addElement(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);

        Pattern relPattern = new Pattern(RELATIONSHIP_ROLLED_UP_PACKAGING_MATERIAL_CERTIFICATIONS);
        Pattern typePattern = new Pattern(TYPE_PG_PLI_PACKAGING_MATERIAL_CERTIFICATION);
        try{
            DomainObject partIdDomainObject = DomainObject.newInstance(context, partID);
            mlReturn = parentDomainObject.getRelatedObjects(context,
                    relPattern.getPattern(),  //relationship pattern
                    typePattern.getPattern(),  // object pattern
                    busSelects,                 // object selects
                    relSelects,              // relationship selects
                    false,                        // to direction
                    true,                       // from direction
                    (short) 1,                    // recursion level
                    null,                        // object where clause
                    null,
                    0);
        }catch(Exception e){
            System.out.println("Exception is: "+e);
        }
        return mlReturn;
    }
}
