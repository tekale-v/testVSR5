package com.pg.dsm.rollup_event.rollup.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.phys_chem.PhysChemAttribute;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ChildExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.common.interfaces.RollupEventFactory;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.common.interfaces.RollupRuleFactory;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.models.MarketRegistrationRollupEvent;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.StringList;

public class RollupAction {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Resource resource;
    Context context;
    Properties rollupPageProperties;

    public RollupAction(Resource resource) {
        this.resource = resource;
        this.context = resource.getContext();
        rollupPageProperties = resource.getRollupPageProperties();
    }

    public MapList getMarkedFinishedProductParts() {
        MapList mlMarkedFPPData = new MapList();
        try {

            // Get mapping from XML - Keep in properties only
            String strTempQuery = rollupPageProperties.getProperty("Str_Objwhere_For_MarkedFPPs");

            // observed that MQL temp query is much faster than find objects API.
            String strTempQueryResult = MqlUtil.mqlCommand(context, strTempQuery);

            mlMarkedFPPData = filterQueryResult(strTempQueryResult);

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlMarkedFPPData;

    }

    private MapList filterQueryResult(String strTempQueryResult) {
        MapList mlMarkedFPPData = new MapList();

        try {
            Map<String, String> mapProductDetails = null;
            if (StringUtils.isNotBlank(strTempQueryResult)) {
                StringList slMarkedFPPs;
                StringList slMarkedObjects = StringUtil.splitString(strTempQueryResult, "@");
                for (String sFPPData : slMarkedObjects) {
                    slMarkedFPPs = StringUtil.splitString(sFPPData, pgV3Constants.DUMP_CHARACTER);
                    mapProductDetails = new HashMap<>();
                    mapProductDetails.put(DomainConstants.SELECT_TYPE, slMarkedFPPs.get(0));
                    mapProductDetails.put(DomainConstants.SELECT_NAME, slMarkedFPPs.get(1));
                    mapProductDetails.put(DomainConstants.SELECT_REVISION, slMarkedFPPs.get(2));
                    mapProductDetails.put(DomainConstants.SELECT_ID, slMarkedFPPs.get(3));
                    mapProductDetails.put(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE, slMarkedFPPs.get(4));
                    mapProductDetails.put(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP, slMarkedFPPs.get(5));
                    mlMarkedFPPData.add(mapProductDetails);

                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return mlMarkedFPPData;
    }

    public boolean disconnectExistingRollupData(ProductPart partBean, Rollup rollupConfig) throws FrameworkException {
        MapList mlFPPData = getConnectedRollupDataOnFPP(partBean.getId(), rollupConfig);
        boolean isDisconnected = false;
        try {
            if (mlFPPData != null) {
                int iFPPData = mlFPPData.size();
                Map<?, ?> rollupDataMap;
                String strConnectionId;
                String strOwner;
                String strPersonSYSCTRLM = rollupPageProperties.getProperty("Str_Rollup_Ctrlm_User");
                StringList slDisconnection = new StringList();
                /*
                 * In case of manual rollup, Rollup initiator may not have access to all the
                 * data which needs to be rolledup. So pushing context to User Agent to avoid
                 * data missing in rollup
                 */
                for (int i = 0; i < iFPPData; i++) {
                    rollupDataMap = (Map<?, ?>) mlFPPData.get(i);
                    strConnectionId = (String) rollupDataMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
                    //Modified by DSM in 2018x.6 May CW for Eco-Fees Requirement
                    strOwner = (String) rollupDataMap.get(DomainConstants.SELECT_OWNER);
                    if (RollupConstants.Basic.WAREHOUSE_ROLLUP_EVENT_IDENTIFIER.getValue().equalsIgnoreCase(rollupConfig.getIdentifier())) {
                        if (UIUtil.isNotNullAndNotEmpty(strOwner) && (strOwner.equalsIgnoreCase(strPersonSYSCTRLM) || pgV3Constants.PERSON_USER_AGENT.equalsIgnoreCase(strOwner)))
                            slDisconnection.add(strConnectionId);
                    } else {
                        slDisconnection.add(strConnectionId);
                    }
                }
                int iSize = slDisconnection.size();

                if (iSize > 0) {

                    String[] aDisconnections = slDisconnection.toArray(new String[iSize]);
                    DomainRelationship.disconnect(context, aDisconnections);
                    isDisconnected = true;
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return isDisconnected;
    }

    public MapList getConnectedRollupDataOnFPP(String strProductId, Rollup rollupConfig) throws FrameworkException {
        MapList mlFPPData = null;
        try {
            if (UIUtil.isNotNullAndNotEmpty(strProductId)) {
                DomainObject domFPPObj = DomainObject.newInstance(context, strProductId);
                StringList slObjectSelects = new StringList(2);
                slObjectSelects.add(DomainConstants.SELECT_ID);
                slObjectSelects.add(pgV3Constants.PHYSICALID);

                StringList slRelSelects = new StringList(3);
                slRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
                slRelSelects.add(RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getSelect(context));
                slRelSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID);
                //Modified by DSM in 2018x.6 May CW for Eco-Fees Requirement
                slRelSelects.add(DomainConstants.SELECT_OWNER);


                Rule rollupRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());

                String strRelationship = rollupRule.getRelationshipName();
                String strRollupType = rollupRule.getToType();

                mlFPPData = domFPPObj.getRelatedObjects(context, strRelationship, // relationship pattern
                        strRollupType, // Type pattern
                        slObjectSelects, // object selects
                        slRelSelects, // rel selects
                        false, // to side
                        true, // from side
                        (short) 1, // recursion level
                        null, // object where clause
                        null, // rel where clause
                        0);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlFPPData;
    }

    public void processProducedBy(ProductPart masterBean, ProductPart productBean, Rollup rollupConfig) {

        ProductPart productPart;

        try {
            if (productBean != null) {
                String sProductType = productBean.getType();
                Rule rollupRuleProducedBy = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_PRODUCEDBY.getValue());
                Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());
                if (Boolean.parseBoolean(rollupRuleProducedBy.getFlag()) && UIUtil.isNotNullAndNotEmpty(sProductType)
                        && rollupRuleProducedBy.getInclusionType().contains(sProductType)) {
                    MapList mlProducedBy = getProducedBy(productBean.getId());
                    if (!mlProducedBy.isEmpty()) {
                        int iSizeOfProducedByList = mlProducedBy.size();
                        Map<String, String> mpProduct;
                        String sAttrMOSAssemblyType = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE
                                .getSelect(context);
                        String sBomType = productBean.getBomType();

                        Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
                        RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(masterBean, rollupConfig,
                                rollupConfig.getIdentifier(), resource);

                        String sObjectType;
                        String sObjectID;
                        String sCurrent;
                        //Modified by DSM Sogeti for 2018.6x Jan CW for defect 45690-starts
                        String sPolicy;
                        //Modified by DSM Sogeti for 2018.6x Jan CW for defect 45690-Ends
                        String sExecutionType = resource.getExecutionType();
                        for (int j = 0; j < iSizeOfProducedByList; j++) {
                            mpProduct = (Map) mlProducedBy.get(j);
                            sObjectType = mpProduct.get(DomainConstants.SELECT_TYPE);
                            sObjectID = mpProduct.get(DomainConstants.SELECT_ID);
                            sCurrent = mpProduct.get(DomainConstants.SELECT_CURRENT);
                            //Modified by DSM Sogeti for 2018.6x Jan CW for defect 45690-starts
                            sPolicy = mpProduct.get(DomainConstants.SELECT_POLICY);
                            if ((UIUtil.isNotNullAndNotEmpty(sCurrent)
                                    && !pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(sCurrent))
                                    && !(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue().equalsIgnoreCase(sExecutionType)
                                    && !pgV3Constants.STATE_RELEASE.equalsIgnoreCase(sCurrent)) && (UIUtil.isNotNullAndNotEmpty(sPolicy) && !pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(sPolicy))) {
                                //Modified by DSM Sogeti for 2018.6x Jan CW for defect 45690-Ends
                                mpProduct.put(sAttrMOSAssemblyType, sBomType);
                                mpProduct.put(DomainConstants.SELECT_TYPE, sObjectType);
                                processForRollupConnections(masterBean, rollupConfig, mpProduct);
                                processMasterProductParts(masterBean, mpProduct, rollupConfig);

                                if (rollupConfig.getIdentifier().equalsIgnoreCase(
                                        RollupConstants.Basic.ROLLUP_EVENT_WAREHOUSE_CLASSIFICATION.getValue())) {
                                    productPart = new ChildExpansion.Builder(context,
                                            mpProduct.get(DomainConstants.SELECT_ID), sBomType)
                                            .setExpansionType(rollupRuleConfiguration.getType())
                                            .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                                            .setExpandLevel((short) 0).build().getProductPart();

                                    rollupEvent.processProductParts(productPart);
                                }

                                if (Boolean.parseBoolean(rollupRuleAlternates.getFlag())
                                        && rollupRuleAlternates.getInclusionType().contains(sObjectType)) {
                                    productPart = new ProductPart(context, DomainObject.newInstance(context, sObjectID)
                                            .getInfo(context, getBusinessObjectSelects()), sBomType);
                                    processAlternates(masterBean, productPart, rollupConfig);
                                }
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    public MapList getProducedBy(String sProductId) throws FrameworkException {
        MapList mlProducedByList = new MapList();
        if (UIUtil.isNotNullAndNotEmpty(sProductId)) {
            DomainObject domProductDataObj = DomainObject.newInstance(context, sProductId);

            StringList selectStmts = new StringList(5);
            selectStmts.addElement(pgV3Constants.PHYSICALID);
            selectStmts.addElement(DomainConstants.SELECT_ID);
            selectStmts.addElement(DomainConstants.SELECT_TYPE);
            selectStmts.addElement(DomainConstants.SELECT_CURRENT);
            //Modified by DSM Sogeti for 2018.6x Jan CW for defect 45690-starts
            selectStmts.addElement(DomainConstants.SELECT_POLICY);
            //Modified by DSM Sogeti for 2018.6x Jan CW for defect 45690-Ends
            mlProducedByList = domProductDataObj.getRelatedObjects(context,
                    pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL, // relationship pattern
                    RollupUtil.getProductPartTypePattern(), // Type pattern
                    true, // to side
                    false, // from side
                    (short) 1, // recursion level
                    selectStmts, // object selects
                    null, // rel selects
                    RollupUtil.getNotObsoleteCondition(), // object where clause
                    null, // relWhereClause
                    0, // limit
                    null, // postRelPattern,
                    null, // PostPattern
                    null);// Map Post Pattern
        }

        return mlProducedByList;
    }

    public void processMasterProductParts(ProductPart masterBean, Map mpProductMap, Rollup rollupConfig) {
        try {
            String sProductType = (String) mpProductMap.get(DomainConstants.SELECT_TYPE);
            Rule rollupRuleMasterProductParts = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_MASTER_PRODUCT_PART.getValue());

            if (Boolean.parseBoolean(rollupRuleMasterProductParts.getFlag())
                    && rollupRuleMasterProductParts.getInclusionType().contains(sProductType)) {
                String sProductId = (String) mpProductMap.get(pgV3Constants.PHYSICALID);
                MapList mlMasterProductList = getMasterProductParts(sProductId);

                int iMasterSize = mlMasterProductList.size();
                Map masterMap;
                String strMPPType;
                String strMPPState;
                String strMPPPhysicalId;
                String sAttrMOSAssemblyType = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getSelect(context);
                for (int i = 0; i < iMasterSize; i++) {
                    String sProductName = (String) mpProductMap.get(DomainConstants.SELECT_NAME);
                    masterMap = (Map) mlMasterProductList.get(i);
                    strMPPType = (String) masterMap.get(pgV3Constants.SELECT_MASTERTYPE_FROMPRODUCTPART);
                    strMPPState = (String) masterMap.get(pgV3Constants.SELECT_MASTERSTATE_FROMPRODUCTPART);
                    strMPPPhysicalId = (String) masterMap.get(pgV3Constants.SELECT_MASTERPHYSICALID_FROMPRODUCTPART);

                    if (UIUtil.isNotNullAndNotEmpty(strMPPType) && UIUtil.isNotNullAndNotEmpty(strMPPState)
                            && strMPPType.equalsIgnoreCase(pgV3Constants.TYPE_MASTERPRODUCTPART)
                            && strMPPState.equalsIgnoreCase(pgV3Constants.STATE_RELEASE)) {
                        masterMap.put(sAttrMOSAssemblyType, mpProductMap.get(sAttrMOSAssemblyType));
                        masterMap.put(pgV3Constants.PHYSICALID, strMPPPhysicalId);
                        processForRollupConnections(masterBean, rollupConfig, masterMap);

                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    public MapList getMasterProductParts(String sProductId) {
        MapList mlMasterProductList = new MapList();
        try {
            DomainObject domProductDataObj = DomainObject.newInstance(context, sProductId);
            StringList slBusSelects = new StringList(4);
            slBusSelects.addElement(pgV3Constants.SELECT_MASTERID_FROMPRODUCTPART);
            slBusSelects.addElement(pgV3Constants.SELECT_MASTERPHYSICALID_FROMPRODUCTPART);
            slBusSelects.addElement(pgV3Constants.SELECT_MASTERSTATE_FROMPRODUCTPART);
            slBusSelects.addElement(pgV3Constants.SELECT_MASTERTYPE_FROMPRODUCTPART);

            mlMasterProductList = domProductDataObj.getRelatedObjects(context,
                    pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM, // relationship pattern
                    pgV3Constants.TYPE_PARTFAMILY, // object pattern
                    null, // object selects
                    slBusSelects, // relationship selects
                    true, // to direction
                    false, // from direction
                    (short) 1, // recursion level
                    null, // object where clause
                    DomainConstants.EMPTY_STRING, (short) 0);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlMasterProductList;
    }

    public void processAlternates(ProductPart masterBean, ProductPart productBean, Rollup rollupConfig) {
        try {
            Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());
            Rule rollupRuleProducedBy = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCEDBY.getValue());
            String sProductType = productBean.getType();
            String sKeyAlternate = RollupConstants.Basic.KEY_ALTERNATE.getValue();

            if (Boolean.parseBoolean(rollupRuleAlternates.getFlag())
                    && rollupRuleAlternates.getInclusionType().contains(sProductType)
                    && !sKeyAlternate.equalsIgnoreCase(productBean.getBomType())) {
                String sProductId = productBean.getId();
                MapList mlAlternates = getConnectedAlternates(sProductId, rollupConfig);

                int iAlternatesSize = mlAlternates.size();
                Map mpAlternate;
                String sAttrMOSAssemblyType = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getSelect(context);
                String sAlternateType;
                String sAlternateId;
                ProductPart alternatePart;
                Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
                String sCurrent;
                String sExecutionType = resource.getExecutionType();
                ProductPart productPart;
                for (int i = 0; i < iAlternatesSize; i++) {
                    mpAlternate = (Map) mlAlternates.get(i);
                    mpAlternate.put(sAttrMOSAssemblyType, new StringBuffer().append(productBean.getBomType())
                            .append(RollupConstants.Basic.SYMBOL_COLON.getValue()).append(sKeyAlternate).toString());
                    sAlternateType = (String) mpAlternate.get(DomainConstants.SELECT_TYPE);
                    sAlternateId = (String) mpAlternate.get(DomainConstants.SELECT_ID);
                    sCurrent = (String) mpAlternate.get(DomainConstants.SELECT_CURRENT);
                    if ((UIUtil.isNotNullAndNotEmpty(sCurrent)
                            && !pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(sCurrent))
                            && !(RollupConstants.Basic.EXECUTION_TYPE_CTRLM.getValue().equalsIgnoreCase(sExecutionType)
                            && !pgV3Constants.STATE_RELEASE.equalsIgnoreCase(sCurrent))) {

                        if (UIUtil.isNotNullAndNotEmpty(sAlternateType)
                                && (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sAlternateType)
                                || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sAlternateType))) {
                            alternatePart = new ChildExpansion.Builder(context, sAlternateId, sKeyAlternate)
                                    .setExpansionType(rollupRuleConfiguration.getType())
                                    .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                                    .setExpandLevel((short) 0).build().getProductPart();
                            RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(masterBean, rollupConfig,
                                    rollupConfig.getIdentifier(), resource);
                            rollupEvent.processProductParts(alternatePart);
                        } else if (Boolean.parseBoolean(rollupRuleProducedBy.getFlag())
                                && rollupRuleProducedBy.getInclusionType().contains(sAlternateType)) {
                            productPart = new ProductPart(context,
                                    DomainObject.newInstance(context, sAlternateId).getInfo(context,
                                            getBusinessObjectSelects()),
                                    sKeyAlternate);
                            processProducedBy(masterBean, productPart, rollupConfig);
                        } else {
                            processForRollupConnections(masterBean, rollupConfig, mpAlternate);
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    public MapList getConnectedAlternates(String sProductId, Rollup rollupConfig) {
        MapList mlAlternatesList = new MapList();
        try {
            if (UIUtil.isNotNullAndNotEmpty(sProductId)) {
                DomainObject domProduct = DomainObject.newInstance(context, sProductId);

                StringList slBusSelects = new StringList(4);
                slBusSelects.addElement(DomainConstants.SELECT_ID);
                slBusSelects.addElement(DomainConstants.SELECT_NAME);
                slBusSelects.addElement(pgV3Constants.PHYSICALID);
                slBusSelects.addElement(DomainConstants.SELECT_CURRENT);

                StringBuilder sbObjectWhere = new StringBuilder();
                // Add state check for CTRLM
                Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());

                mlAlternatesList = domProduct.getRelatedObjects(context, DomainConstants.RELATIONSHIP_ALTERNATE, // relationship
                        // pattern
                        rollupRuleAlternates.getInclusionType(), // object pattern
                        slBusSelects, // object selects
                        null, // relationship selects
                        false, // to direction
                        true, // from direction
                        (short) 1, // recursion level
                        sbObjectWhere.toString(), // object where clause
                        DomainConstants.EMPTY_STRING, (short) 0);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlAlternatesList;
    }

    public void processSubstitutes(ProductPart masterBean, ProductPart productBean, Rollup rollupConfig)
            throws FrameworkException {
        try {
            String sKeySubstitute = RollupConstants.Basic.KEY_SUBSTITUTE.getValue();
            // Modified by DSM (Sogeti) for 22x.02 - REQ 46276 - Start
            String sKeyEBOMChildren = RollupConstants.Basic.EBOM_CHILDREN.getValue();
            String bomType = productBean.getBomType();
            if (productBean != null && masterBean != null && productBean.isSubstituteExist()
                    && (sKeySubstitute.equalsIgnoreCase(bomType) || sKeyEBOMChildren.equalsIgnoreCase(bomType))) {
                // Modified by DSM (Sogeti) for 22x.02 - REQ 46276 - End
                List<Substitute> slSubstitutes = productBean.getSubstitutes();
                boolean substituteValidation = false;
                String sSubstituteType;
                String sSubstitutePhysicalId;
                Map<String, String> mpSubstitutes = new HashMap<>();
                String sAttrMOSAssemblyType = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getSelect(context);
                RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(masterBean, rollupConfig,
                        rollupConfig.getIdentifier(), resource);
                ProductPart productPart;
                Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
                Rule productPartRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
                Rule substituteAlternateRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_SUBSTITUTE_ALTERNATE.getValue());

                Rule substituteProducedByRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_PRODUCEDBY.getValue());
                Substitute substitute;
                RollupRule rollupRule;
                MarketRegistrationRollupEvent marketRegistration;
                for (Substitute substitutes : slSubstitutes) {
                    substitute = substitutes;
                    rollupRule = RollupRuleFactory.getRollupRule(this.context, productBean, substitute, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    substituteValidation = rollupRule.isSubstituteAllowed();
                    if (substituteValidation) {
                        sSubstituteType = substitute.getType();
                        if (isSubstituteAllowed(rollupConfig, sSubstituteType)) { // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                            productPart = new ChildExpansion.Builder(context, substitute.getId(), sKeySubstitute)
                                    .setExpansionType(rollupRuleConfiguration.getType())
                                    .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                                    .setExpandLevel((short) 0).build().getProductPart();
                            rollupEvent.processProductParts(productPart);
                        } else if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sSubstituteType)) {
                            productPart = new ChildExpansion.Builder(context, substitute.getId(), sKeySubstitute)
                                    .setExpansionType(rollupRuleConfiguration.getType())
                                    .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                                    .setExpandLevel((short) 0).build().getProductPart();
                            marketRegistration = new MarketRegistrationRollupEvent(
                                    productPart, rollupConfig, resource);
                            if (marketRegistration.performPrimarySetCOPCheck(productBean, substitute)) {
                                rollupEvent.processProductParts(productPart);
                            }
                        } else {
                            sSubstitutePhysicalId = substitute.getPhysicalId();
                            if (productPartRule.getInclusionType().contains(sSubstituteType)) {

                                mpSubstitutes.put(pgV3Constants.PHYSICALID, sSubstitutePhysicalId);
                                mpSubstitutes.put(sAttrMOSAssemblyType, sKeySubstitute);
                                mpSubstitutes.put(DomainConstants.SELECT_TYPE, sSubstituteType);

                                processForRollupConnections(masterBean, rollupConfig, mpSubstitutes);
                                if (Boolean.parseBoolean(substituteAlternateRule.getFlag())
                                        && substituteAlternateRule.getInclusionType().contains(sSubstituteType)) {
                                    productPart = new ProductPart(context,
                                            DomainObject.newInstance(context, sSubstitutePhysicalId).getInfo(context,
                                                    getBusinessObjectSelects()),
                                            sKeySubstitute);
                                    processAlternates(masterBean, productPart, rollupConfig);
                                }
                            }

                            if (Boolean.parseBoolean(substituteProducedByRule.getFlag())
                                    && substituteProducedByRule.getInclusionType().contains(sSubstituteType)) {
                                productPart = new ProductPart(context,
                                        DomainObject.newInstance(context, sSubstitutePhysicalId).getInfo(context,
                                                getBusinessObjectSelects()),
                                        sKeySubstitute);
                                processProducedBy(masterBean, productPart, rollupConfig);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    /**
     * Added by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
     * @param rollupConfig
     * @param sSubstituteType
     * @return
     */
    boolean isSubstituteAllowed(Rollup rollupConfig, String sSubstituteType) {
        return (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sSubstituteType)
                || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sSubstituteType)
                || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sSubstituteType)
                || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sSubstituteType)
                || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sSubstituteType)
                || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sSubstituteType)
                || pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sSubstituteType)
                || rollupConfig.getIdentifier().equalsIgnoreCase(
                RollupConstants.Basic.ROLLUP_EVENT_WAREHOUSE_CLASSIFICATION.getValue()));
    }

    public void processForRollupConnections(ProductPart masterPart, Rollup rollupConfig,
                                            Map<String, String> mpProductMap) throws FrameworkException {
        RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(masterPart, rollupConfig,
                rollupConfig.getIdentifier(), resource);
        rollupEvent.performRollupConnections(masterPart, mpProductMap);
    }

    public StringList getStringListFromMap(Map<?, ?> dataMap, String selectable) {
        Object substituteId = (dataMap).get(selectable);
        StringList stringList = new StringList();
        if (null != substituteId) {
            if (substituteId instanceof StringList) {
                stringList = (StringList) substituteId;
            } else {
                stringList.add(substituteId.toString());
            }
        }
        return stringList;
    }

    public Map<Object, Object> getFormulationPartPhysChemInfo(DomainObject dObj,
                                                              List<PhysChemAttribute> physChemAttributes, StringList objSelects) {
        Map<Object, Object> dataMap = null;

        for (PhysChemAttribute physChemAttribute : physChemAttributes) {
            objSelects.addElement(physChemAttribute.getAttributeSelectExpression());
        }
        try {
            dataMap = dObj.getInfo(context, objSelects);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return dataMap;
    }

    public Map<String, String> getFormulationPartProductFormCustomMap(Map<?, ?> dataMap,
                                                                      String owningProductLineRelationshipName) {
        Map<String, String> productFormCustomMap = new HashMap<>();
        StringList productFormRelOIDs = getStringListFromMap(dataMap,
                "to[" + owningProductLineRelationshipName + "].id");
        StringList productFormOIDs = getStringListFromMap(dataMap,
                "to[" + owningProductLineRelationshipName + "].from.id");

        int size = productFormRelOIDs.size();
        for (int i = 0; i < size; i++) {
            productFormCustomMap.put(productFormOIDs.get(i), productFormRelOIDs.get(i));
        }
        return productFormCustomMap;
    }

    public boolean isFormulationPartPhysChemDataValid(Map<?, ?> formulationPartInfoMap,
                                                      List<PhysChemAttribute> physChemAttributes) {
        boolean tempFlag = false;
        String attributeValue;
        for (PhysChemAttribute physChemAttribute : physChemAttributes) {
            attributeValue = (String) formulationPartInfoMap.get(physChemAttribute.getAttributeSelectExpression());
            if (UIUtil.isNotNullAndNotEmpty(attributeValue)
                    && !attributeValue.equals(physChemAttribute.getCustomDefaultValue())) {
                tempFlag = true;
                break;
            }
        }
        return tempFlag;
    }

    public boolean rollupFormulationPartPhysChemData(DomainObject dObj, Map<?, ?> formulationPartInfoMap,
                                                     List<PhysChemAttribute> physChemAttributes) {
        boolean tempFlag = true;
        Map<String, String> physChemAttributeMap = new HashMap<>();
        for (PhysChemAttribute physChemAttribute : physChemAttributes) {
            physChemAttributeMap.put(physChemAttribute.getAttributeActualName(),
                    (String) formulationPartInfoMap.get(physChemAttribute.getAttributeSelectExpression()));
        }
        try {
            dObj.setAttributeValues(context, physChemAttributeMap);
        } catch (FrameworkException e) {
            tempFlag = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return tempFlag;
    }

    public boolean disconnectProductForms(Map<String, String> appProductFormCustomMap,
                                          Map<String, String> fopProductFormCustomMap) {
        boolean tempFlag = true;
        StringList relIDs = new StringList();
        for (Entry<String, String> productFormKeyValue : appProductFormCustomMap.entrySet()) {
            // if the same product form oid is not present in fop product form map. then
            // collect the rel to disconnect.
            if (!fopProductFormCustomMap.containsKey(productFormKeyValue.getKey())) {
                relIDs.addElement(productFormKeyValue.getValue());
            }
        }
        try {
            if (!relIDs.isEmpty()) {
                DomainRelationship.disconnect(context, relIDs.toArray(new String[relIDs.size()]));
            }
        } catch (FrameworkException e) {
            tempFlag = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return tempFlag;
    }

    public boolean connectProductForms(DomainObject appObj, String owningProductLineRelationshipName,
                                       Map<String, String> appProductFormCustomMap, Map<String, String> fopProductFormCustomMap) {
        boolean tempFlag = true;
        StringList oIDs = new StringList();
        String oID;
        for (Entry<String, String> productFormKeyValue : fopProductFormCustomMap.entrySet()) {
            // if the same product form oid is not present in fop product form map. then
            // collect the rel to disconnect.
            oID = productFormKeyValue.getKey();
            if (!appProductFormCustomMap.containsKey(oID)) {
                oIDs.addElement(oID);
            }
        }
        try {
            if (!oIDs.isEmpty()) {
                DomainRelationship.connect(context, appObj, owningProductLineRelationshipName, Boolean.FALSE,
                        oIDs.toArray(new String[oIDs.size()]));
            }
        } catch (FrameworkException e) {
            tempFlag = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return tempFlag;
    }

    public boolean resetPhysChemRollupFlag(DomainObject dObj, String keyTrue) {
        boolean tempFlag = true;
        try {
            dObj.setAttributeValue(context, RollupConstants.Attribute.PHYSICAL_CHEMICAL_ROLL_UP_FLAG.getName(context),
                    String.valueOf(keyTrue));
        } catch (FrameworkException e) {
            tempFlag = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return tempFlag;
    }

    public boolean resetDefaultPhysChemValues(DomainObject dObj, List<PhysChemAttribute> physChemAttributes) {
        boolean tempFlag = true;
        Map<String, String> physChemDefaultValueMap = getPhysChemDefaultValueMap(physChemAttributes);
        try {
            dObj.setAttributeValues(context, physChemDefaultValueMap);
        } catch (FrameworkException e) {
            tempFlag = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return tempFlag;
    }

    private Map<String, String> getPhysChemDefaultValueMap(List<PhysChemAttribute> physChemAttributes) {
        Map<String, String> physChemAttributeMap = new HashMap<>();
        for (PhysChemAttribute physChemAttribute : physChemAttributes) {
            // if setting (updateCustomDefaultValue) is 'true'
            // then update custom default value -
            // else update db default value of attribute.
            if (physChemAttribute.isUpdateCustomDefaultValue()) {
                physChemAttributeMap.put(physChemAttribute.getAttributeActualName(),
                        physChemAttribute.getCustomDefaultValue());
            } else {
                physChemAttributeMap.put(physChemAttribute.getAttributeActualName(),
                        physChemAttribute.getAttributeDefaultValue());
            }
        }
        return physChemAttributeMap;
    }

    /*
     * @description: This method updates the corrosive attribute value on FPP
     *
     * @param context strFPPObjectId : holds FPP Id
     *
     * @return void
     *
     * @throws Exception Modified for 2018x.5 Rollup Configuration requirements
     * 33937
     */
    public void updateCorrosiveOnFPP(String strFPPObjectId) throws FrameworkException {

        try {
            DomainObject domFPPObj = DomainObject.newInstance(context, strFPPObjectId);
            String strGHSCode;

            String strTypePattern = rollupPageProperties.getProperty("Str_AllowedTypes_Corrosive");
            String strRelPattern = rollupPageProperties.getProperty("Str_AllowedRelationship_Corrosive");
            String strPostTypePattern = rollupPageProperties.getProperty("Str_AllowedPostTypes_Corrosive");

            StringList slObjectSelects = new StringList(3);
            slObjectSelects.addElement(DomainConstants.SELECT_TYPE);
            slObjectSelects.addElement(DomainConstants.SELECT_NAME);
            slObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGHSCODE);

            String sCorrosiveVal = pgV3Constants.KEY_NO_VALUE;

            String strType;
            String strName;
            Map tempMap;

            MapList mlConnectedObjects = domFPPObj.getRelatedObjects(context, strRelPattern, // relationship pattern
                    strTypePattern, // Type pattern
                    false, // to side
                    true, // from side
                    (short) 2, // recursion level
                    slObjectSelects, // object selects
                    null, // rel selects
                    DomainConstants.EMPTY_STRING, // object where clause
                    null, // relWhereClause
                    0, // limit
                    null, // postRelPattern,
                    strPostTypePattern, // PostPattern
                    null); // Map post pattern

            String sCorrosive = domFPPObj.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGCORROSIVE);
            boolean bflag = false;

            int iSizeOfRollUpData = mlConnectedObjects.size();
            for (int j = 0; j < iSizeOfRollUpData; j++) {

                tempMap = (Map) mlConnectedObjects.get(j);
                strType = (String) tempMap.get(DomainConstants.SELECT_TYPE);
                strName = (String) tempMap.get(DomainConstants.SELECT_NAME);

                if (UIUtil.isNotNullAndNotEmpty(strType)
                        && (strType.equalsIgnoreCase(pgV3Constants.TYPE_WARNINGSTATEMENTSCOPY)
                        || strType.equalsIgnoreCase(pgV3Constants.TYPE_WARNINGSTATEMENTSMASTERCOPY))) {
                    strGHSCode = (String) tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGGHSCODE);
                    if ((strGHSCode.contains(pgV3Constants.KEY_H314))) {
                        if (sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE))
                            bflag = true;
                        sCorrosiveVal = pgV3Constants.KEY_YES_VALUE;
                    } else if (!(strGHSCode.contains(pgV3Constants.KEY_H314))
                            && sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
                        bflag = true;
                        sCorrosiveVal = pgV3Constants.KEY_NO_VALUE;
                    }
                } else {
                    if (UIUtil.isNotNullAndNotEmpty(strName) && strName.equalsIgnoreCase(pgV3Constants.CORROSIVE)) {
                        if (sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE)) {
                            bflag = true;
                            sCorrosiveVal = pgV3Constants.KEY_YES_VALUE;
                        }
                    } else if (UIUtil.isNotNullAndNotEmpty(strName)
                            && !strName.equalsIgnoreCase(pgV3Constants.CORROSIVE)
                            && sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
                        bflag = true;
                        sCorrosiveVal = pgV3Constants.KEY_NO_VALUE;
                    }
                }

                if (bflag) {

                    break;
                }
            }
            if (bflag || (mlConnectedObjects.isEmpty() && sCorrosive.equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE))) {
                domFPPObj.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCORROSIVE, sCorrosiveVal);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    public void updateNoSpecialStorageRequiredOnFPP(String strFPPObjectId) throws FrameworkException {
        MapList mlWarehouseClassificationData = new MapList();
        MapList returnMapList = null;
        DomainObject domainObjectFPP = DomainObject.newInstance(context, strFPPObjectId);
        StringList slObjectSelect = new StringList(2);
        slObjectSelect.addElement(DomainConstants.SELECT_ID);
        slObjectSelect.addElement(DomainConstants.SELECT_NAME);
        try {

            mlWarehouseClassificationData = getConnectedRollUpData(domainObjectFPP,
                    RollupConstants.Type.WAREHOUSING_CLASSIFICATION.getName(context),
                    RollupConstants.Relationship.WAREHOUSING_CLASSIFICATION.getName(context), null, null, null,
                    resource.getExecutionType());

            if (mlWarehouseClassificationData.isEmpty()) {
                /*
                 * In case of manual rollup, Rollup initiator may not have access to all the
                 * data which needs to be rolledup. So pushing context to User Agent to avoid
                 * data missing in rollup
                 */
                returnMapList = findObjectDetails(RollupConstants.Type.WAREHOUSING_CLASSIFICATION.getName(context),
                        RollupConstants.Basic.NO_SPECIAL_STORAGE_REQUIRED.getValue(), slObjectSelect);
                String[] rollupObjects = {(String) ((HashMap) returnMapList.get(0)).get(DomainConstants.SELECT_ID)};
                DomainRelationship.connect(context, domainObjectFPP,
                        RollupConstants.Relationship.WAREHOUSING_CLASSIFICATION.getName(context), true, rollupObjects,
                        false);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    public MapList findObjectDetails(String sType, String sName, StringList slObjectSelects) throws FrameworkException {

        return DomainObject.findObjects(context, sType, // typePattern
                sName, // namepattern
                pgV3Constants.SYMBOL_STAR, // revpattern
                pgV3Constants.SYMBOL_STAR, // owner pattern
                context.getVault().getName(), // vault pattern
                null, // where exp
                false, // expandType
                slObjectSelects); // objectSelects
    }

    public MapList getConnectedRollUpData(DomainObject domProductPart, String sType, String sRelationship,
                                          String sPostType, StringList slObjectSelects, StringList slRelSelects, String sExecutionType)
            throws FrameworkException {
        MapList mlRollupDataList = new MapList();
        try {
            /*
             * In case of manual rollup, Rollup initiator may not have access to all the
             * data which needs to be rolledup. So pushing context to User Agent to avoid
             * data missing in rollup
             */
            //Modified by DSM(Sogeti) for 2018x.6 Req #37624 - Starts
            mlRollupDataList = domProductPart.getRelatedObjects(context, // Matrix Context
                    sRelationship, // Relationship Pattern
                    sType, // Type Pattern
                    false, // getTo
                    true, // getFrom
                    (short) 1, // Recurse to Level
                    slObjectSelects, // Object selectables
                    slRelSelects, // Relationship selectables
                    RollupUtil.getNotObsoleteCondition(), // Object Where clause
                    null, // Relationship Where clause
                    0, // get objects
                    null, // Post Relationship Pattern
                    sPostType, // Post Type Pattern
                    null);
            //Modified by DSM(Sogeti) for 2018x.6 Req #37624 - Ends
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlRollupDataList;
    }

    public StringList getRollUpObjectList(MapList mlData) {

        StringList slConnectedEventPartIDs = new StringList();
        if (mlData != null && !mlData.isEmpty()) {
            Map mapData;
            for (int i = 0; i < mlData.size(); i++) {
                mapData = (Map) mlData.get(i);
                if (mapData.containsKey(DomainConstants.SELECT_ID)) {
                    slConnectedEventPartIDs.addElement((String) mapData.get(DomainConstants.SELECT_ID));
                } else {
                    slConnectedEventPartIDs.addElement((String) mapData.get(pgV3Constants.PHYSICALID));
                }
            }
        }
        return slConnectedEventPartIDs;
    }

    public StringList getBusinessObjectSelects() {
        StringList businessObjectSelects = new StringList();
        businessObjectSelects.addElement(DomainConstants.SELECT_ID);
        businessObjectSelects.addElement(pgV3Constants.PHYSICALID);
        businessObjectSelects.addElement(DomainConstants.SELECT_TYPE);
        businessObjectSelects.addElement(DomainConstants.SELECT_NAME);
        businessObjectSelects.addElement(DomainConstants.SELECT_REVISION);
        businessObjectSelects.addElement(DomainConstants.SELECT_CURRENT);
        businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID);
        businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
        businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDINSIDEDEVICE);
        businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE);
        businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDOESDEVICECONTAINFLAMMABLELIQUID);
        businessObjectSelects.addElement(RollupConstants.Attribute.SET_PRODUCT_NAME.getSelect(context));
        return businessObjectSelects;
    }

    /**
     * This method gets the object list of FPP data provided in inputfile
     *
     * @param filePath - holds the input file path
     * @return - Maplist holds FPP data
     * @throws Exception Added by DSM Sogeti for defect# 43716
     */
    public MapList getInputFinishedProductParts(String filePath) throws Exception {
        MapList objectList = new MapList();
        if (RollupUtil.isInputFileExist(filePath)) {
            final List<String> inputFileContentAsList = RollupUtil.getInputFileContentAsList(filePath);
            Map<Object, Object> objectMap;
            StringList slOutputLine;
            int outputSize;
            for (String fppData : inputFileContentAsList) {
                objectMap = new HashMap<>();
                slOutputLine = StringUtil.splitString(fppData, pgV3Constants.DUMP_CHARACTER);
                outputSize = slOutputLine.size();
                if (outputSize > 3) {
                    objectMap.put(DomainConstants.SELECT_TYPE, slOutputLine.get(0));
                    objectMap.put(DomainConstants.SELECT_NAME, slOutputLine.get(1));
                    objectMap.put(DomainConstants.SELECT_REVISION, slOutputLine.get(2));
                    objectMap.put(DomainConstants.SELECT_ID, slOutputLine.get(3));
                    objectList.add(objectMap);
                }
            }
        } else {
            logger.log(Level.INFO, "InputFile does not exits at : {0}", filePath);
        }
        return objectList;
    }


    /**
     * This method return Rollup Object List
     *
     * @param mpRollupData holds Rollup Data
     * @return StringList holds Rollup Object Id
     * Added as part of 2018x.6 SEP CW defect 44593
     */
    public StringList getRollupObjectId(Map mpRollupData) {
        StringList slRollupObject = new StringList(1);
        if (mpRollupData.containsKey(DomainConstants.SELECT_ID)) {
            slRollupObject.addElement((String) mpRollupData.get(DomainConstants.SELECT_ID));
        } else {
            slRollupObject.addElement((String) mpRollupData.get(pgV3Constants.PHYSICALID));
        }
        return slRollupObject;
    }

    /**
     * This method returns the FPP map for unmarking
     *
     * @param context
     * @param domFPP
     * @param productPart
     * @return FPP attribute Map
     * Added as part of 2018x.6 SEP CW defect 44593
     * Modified for 2018x.6 OCT CW defect 45017
     */
    public Map<String, String> getFPPAttributeMap(Context context, DomainObject domFPP, ProductPart productPart) {
        Map<String, String> mpFPPMap = new HashMap<>();
        String sOldEvent = productPart.getEventForRollup();
        try {
            String sNewEvent = domFPP.getAttributeValue(context, pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP);
            StringList slNewValue = StringUtil.split(sNewEvent, ",");
            StringBuilder sbNewValue = new StringBuilder();
            for (String sEvent : slNewValue) {
                if (!sOldEvent.contains(sEvent)) {
                    if (sbNewValue.length() > 0) {
                        sbNewValue.append(pgV3Constants.SYMBOL_COMMA);
                    }
                    sbNewValue.append(sEvent);
                }
            }
            if (UIUtil.isNotNullAndNotEmpty(sbNewValue.toString())) {
                mpFPPMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, sbNewValue.toString());
            } else {
                mpFPPMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_FALSE);
                mpFPPMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, DomainConstants.EMPTY_STRING);
            }

            //Added by Sogeti (DSM) for Defect# 45017 - Starts
            if (productPart.isPublishDGCToSAP()) {
                mpFPPMap.put(RollupConstants.Attribute.PUBLISH_DGC_TO_SAP.getName(context), pgV3Constants.KEY_TRUE);
            }
            //Added by Sogeti (DSM) for Defect# 45017 - Ends

            // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - Start
            if (productPart.isPublishITToSAP()) {
                mpFPPMap.put(RollupConstants.Attribute.PUBLISH_INGREDIENTTRANSPARENCY_TO_SAP.getName(context), pgV3Constants.KEY_TRUE);
            }
             // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - End
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mpFPPMap;
    }

    /**
     * This method perform rollup connections
     *
     * @param masterPart
     * @param rollupConfig
     * @param mpProductPartData
     * @throws FrameworkException Added by Sogeti (DSM) for Defect# 45017
     */
    public void performRollupRecalculation(ProductPart masterPart, Rollup rollupConfig,
                                           Map<String, Set<String>> mpProductPartData) throws FrameworkException {
        try {
            if (!mpProductPartData.isEmpty()) {
                String sPhysicalId;
                Set<String> sRollupData;
                Rule rollupRelationshipRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRelationshipRule.getRelationshipName());
                Map<String, String> mpRelConnectedProduct;
                Entry<?, ?> mapRelId;
                Iterator<?> itr;
                String sConnectionId;
                /*
                 * In case of manual rollup, Rollup initiator may not have access to all the
                 * data which needs to be rolledup. So pushing context to User Agent to avoid
                 * data missing in rollup */

                DomainObject domFPP = DomainObject.newInstance(context, masterPart.getId());
                for (Entry<String, Set<String>> entry : mpProductPartData.entrySet()) {
                    sPhysicalId = entry.getKey();
                    sRollupData = entry.getValue();
                    if (!sRollupData.isEmpty()) {
                        try {
                            String[] rollupObjects = sRollupData.toArray(new String[sRollupData.size()]);
                            mpRelConnectedProduct = DomainRelationship.connect(context, domFPP, rFPPtoRollUpRel, true,
                                    rollupObjects, true);
                            itr = mpRelConnectedProduct.entrySet().iterator();
                            while (itr.hasNext()) {
                                mapRelId = (Entry<?, ?>) itr.next();
                                sConnectionId = mapRelId.getValue().toString();
                                DomainRelationship.setAttributeValue(context, sConnectionId,
                                        pgV3Constants.ATTRIBUTE_PGPRODUCTPARTPHYSICALID, sPhysicalId);
                            }
                        } catch (Exception e) {
                            // Logging the exception is not required. Didn't placed any exception logger to avoid logger size issue

                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    /**
     * This method returns existing rollup data on FPP
     *
     * @param master
     * @param rollupConfig
     * @return FPP rollup data Map
     * Added by Sogeti (DSM) for Defect# 45017
     */
    public Map<String, Set<String>> getExistingFPPRollupData(ProductPart master, Rollup rollupConfig) {
        Map<String, Set<String>> mpFPPRollupData = new HashMap();

        try {
            MapList mlFPP = getConnectedRollupDataOnFPP(master.getId(), rollupConfig);
            Map mpRolledUpdata;
            String sProductPartPhysicalId;
            String sRollupObjectId;
            Set<String> sRollupData;
            for (Object object : mlFPP) {
                sRollupData = new HashSet<>();
                mpRolledUpdata = (Map<?, ?>) object;
                sRollupObjectId = (String) mpRolledUpdata.get(DomainConstants.SELECT_ID);
                sProductPartPhysicalId = (String) mpRolledUpdata.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID);
                if (UIUtil.isNotNullAndNotEmpty(sRollupObjectId) && UIUtil.isNotNullAndNotEmpty(sProductPartPhysicalId)) {
                    if (mpFPPRollupData.containsKey(sProductPartPhysicalId)) {
                        sRollupData = mpFPPRollupData.get(sProductPartPhysicalId);
                        sRollupData.add(sRollupObjectId);
                    } else {
                        sRollupData.add(sRollupObjectId);
                    }
                    mpFPPRollupData.put(sProductPartPhysicalId, sRollupData);
                }

            }

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return mpFPPRollupData;
    }

    /**
     * This method returns the product part rollup data
     *
     * @param slProductParts
     * @param rollupConfig
     * @return product part rollup data Map
     * Added by Sogeti (DSM) for Defect# 45017
     */
    public Map<String, Set<String>> getProductPartRollupData(StringList slProductParts, Rollup rollupConfig) {
        Map<String, Set<String>> mpProductPartRollupData = new HashMap();
        try {
            DomainObject domProductPart = DomainObject.newInstance(context);
            Rule childProductPartRule = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
            MapList mlRollupData;
            Map<?, ?> mpRollupData;
            Set<String> sRollupData;
            String sObjectId;
            for (String sPhysicalId : slProductParts) {
                sRollupData = new HashSet<>();
                domProductPart.setId(sPhysicalId);
                mlRollupData = getConnectedRollUpData(domProductPart, childProductPartRule.getToType(), childProductPartRule.getRelationshipName(), null, RollupUtil.getObjectSelects(), null, resource.getExecutionType());

                for (Object object : mlRollupData) {
                    mpRollupData = (Map<?, ?>) object;
                    sObjectId = (String) mpRollupData.get(DomainConstants.SELECT_ID);
                    if (UIUtil.isNotNullAndNotEmpty(sObjectId)) {
                        sRollupData.add(sObjectId);
                    }
                }

                if (!sRollupData.isEmpty()) {
                    mpProductPartRollupData.put(sPhysicalId, sRollupData);
                }
            }

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return mpProductPartRollupData;
    }

    /**
     * This method performs snapshot data check
     *
     * @param mpFPPRolledupData
     * @param mpProductPartData
     * @param masterPart
     * @return boolean
     * Added by Sogeti (DSM) for Defect# 45017
     */
    public boolean performSnapshotCheck(Map<String, Set<String>> mpFPPRolledupData, Map<String, Set<String>> mpProductPartData, ProductPart masterPart) {
        boolean isSnapshotDifference = false;

        try {
            int iFPPDataSize = mpFPPRolledupData.size();
            int iProductDataSize = mpProductPartData.size();

            if (iFPPDataSize == iProductDataSize) {
                String sPhysicalId;
                // {Key == PhysicalIdProductPart , Value == Set of RollupObject}
                for (Entry<String, Set<String>> entry : mpFPPRolledupData.entrySet()) {
                    sPhysicalId = entry.getKey();
                    if (mpProductPartData.containsKey(sPhysicalId)) {
                        isSnapshotDifference = !entry.getValue().equals(mpProductPartData.get(sPhysicalId));
                        if (isSnapshotDifference) {
                            /*
                             * UseCase: Processing for recalculation when FPP Rolledup product parts data and children product parts rollup data objects are not same
                             */
                            masterPart.setPublishDGCToSAP(true);
                            break;
                        }
                    } else {
                        /*
                         * UseCase: Processing for recalculation when FPP Rolledup product parts and children product parts are not same
                         */
                        masterPart.setPublishDGCToSAP(true);
                        isSnapshotDifference = true;
                        break;
                    }
                }
            } else {
                /*
                 * UseCase: Processing for recalculation when FPP Rolledup product parts and children product parts counts not same
                 */
                masterPart.setPublishDGCToSAP(true);
                isSnapshotDifference = true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return isSnapshotDifference;
    }
}
