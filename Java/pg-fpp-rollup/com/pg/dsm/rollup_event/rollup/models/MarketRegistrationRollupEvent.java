package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ChildExpansion;
import com.pg.dsm.rollup_event.common.ebom.PrimaryCOPExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.common.interfaces.RollupEventFactory;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.common.interfaces.RollupRuleFactory;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.util.MarkUtil;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupAction;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.StringList;

public class MarketRegistrationRollupEvent implements RollupEvent {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart masterPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;

    public MarketRegistrationRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
        this.masterPart = productPart;
        this.rollupConfig = rollupConfig;
        this.resource = resource;
        this.context = resource.getContext();
        rollupAction = new RollupAction(this.resource);
    }

    @Override
    public void execute() {
        if (masterPart != null) {
            try {
                boolean isModified = rollupAction.disconnectExistingRollupData(masterPart, rollupConfig);
                processProductParts(masterPart);
                if (isModified) {
                    resource.getDynamicSubscription().add(rollupConfig);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
        }
    }

    public void processProductParts(ProductPart productBean) {
        try {
            String sKeySubstitute = RollupConstants.Basic.KEY_SUBSTITUTE.getValue();
            String sKeyAlternate = RollupConstants.Basic.KEY_ALTERNATE.getValue();
            String sKeyEBOMChildren = RollupConstants.Basic.EBOM_CHILDREN.getValue();
            String sKeyAssemblyType = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getSelect(context);
            String sProductType = productBean.getType();
            String sBOMType = productBean.getBomType();
            boolean bPAPandFABCheck = pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sProductType)
                    || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sProductType);

            Map<String, String> mpProductMap = new HashMap<>();

            if (!productBean.isParentExist() && sKeyEBOMChildren.equalsIgnoreCase(sBOMType) && Boolean.TRUE.equals(bPAPandFABCheck)) {
                Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());


                if (rollupRuleAlternates.getInclusionType().contains(sProductType)) {
                    rollupAction.processAlternates(masterPart, productBean, rollupConfig);
                }
            }

            if (isRollupParentType(productBean.getType()) && (sKeySubstitute.equalsIgnoreCase(sBOMType) || sKeyAlternate.equalsIgnoreCase(sBOMType))) {

                // Process logic for Rollup connections
                mpProductMap.put(pgV3Constants.PHYSICALID, productBean.getPhysicalId());
                mpProductMap.put(sKeyAssemblyType, productBean.getBomType());
                mpProductMap.put(DomainConstants.SELECT_TYPE, productBean.getType());
                performRollupConnections(masterPart, mpProductMap);

            } else if (productBean.isChildExist()) {

                List<ProductPart> children = productBean.getChildren();
                Iterator<ProductPart> childrenItr = children.iterator();
                ProductPart childrenProduct;
                boolean validateForRegistrationType;
                boolean bProductValidation;
                boolean bProcessProductChildren;
                String sChildrenType;
                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();
                    RollupRule rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276

                    bProductValidation = rollupRule.isChildrenAllowed();

                    sChildrenType = childrenProduct.getType();
                    if (bProductValidation) {
                        bProcessProductChildren = setCOPValidation(childrenProduct);
                        if (bProcessProductChildren) {
                            // Modified by (DSM Sogeti) for Defect # 40050 - Starts
                            validateForRegistrationType = registrationPartCheck(childrenProduct);

                            if (validateForRegistrationType) {
                                // Process logic for Rollup connections
                                mpProductMap.put(pgV3Constants.PHYSICALID, childrenProduct.getPhysicalId());
                                mpProductMap.put(sKeyAssemblyType, childrenProduct.getBomType());
                                mpProductMap.put(DomainConstants.SELECT_TYPE, childrenProduct.getType());
                                performRollupConnections(masterPart, mpProductMap);
                            }

                            bPAPandFABCheck = pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sChildrenType)
                                    || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sChildrenType);

                            if (!Boolean.TRUE.equals(bPAPandFABCheck)) {
                                rollupAction.processProducedBy(masterPart, childrenProduct, rollupConfig);
                                rollupAction.processAlternates(masterPart, childrenProduct, rollupConfig);
                            }

                            rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);

                            if (!validateForRegistrationType) {
                                processProductParts(childrenProduct);
                            }
                            //Modified by (DSM Sogeti) for Defect # 40050 - Ends
                        } else {
                            // Process Reshipper CUP
                            rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    public boolean registrationPartCheck(ProductPart childrenProduct) {
        boolean bValidation = false;
        try {
            boolean bCOPValidation;
            boolean bProductPartValidation;
            String sProductType = childrenProduct.getType();

            if (UIUtil.isNotNullAndNotEmpty(sProductType)) {
                String sSetProduct = childrenProduct.getSetProductName();

                String sParentType = DomainConstants.EMPTY_STRING;
                if (childrenProduct.isParentExist()) {
                    sParentType = childrenProduct.getParent().getType();
                }

                Rule rollupRuleProductParts = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
                String sProductParts = rollupRuleProductParts.getInclusionType();

                bCOPValidation = (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sProductType)
                        && UIUtil.isNotNullAndNotEmpty(sSetProduct) && pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sSetProduct)
                        && (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sParentType) || pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sParentType)));

                bProductPartValidation = (UIUtil.isNotNullAndNotEmpty(sProductParts) && sProductParts.contains(sProductType));

                bValidation = bCOPValidation || bProductPartValidation || isRollupParentType(sProductType);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return bValidation;
    }

    public void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap)
            throws FrameworkException {
        try {
            if (masterPart != null && !mpProductMap.isEmpty()) {
                DomainObject domMaster = DomainObject.newInstance(context, masterPart.getId());

                Rule rollupRelationshipRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRelationshipRule.getRelationshipName());
                String sAttrMOSAssemblyType = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getName(context);
                MapList mlRollupData = getRollupDataList(context, mpProductMap);

                /*
                 * In case of manual rollup, Rollup initiator may not have access to all the
                 * data which needs to be rolledup. So pushing context to User Agent to avoid
                 * data missing in rollup
                 */
                if (!mlRollupData.isEmpty()) {

                    int iRollupObjectsSize = mlRollupData.size();
                    Map mRelConnectedProduct;
                    String sConnectionId;
                    Map.Entry mapRelId;
                    Iterator itr;
                    String sObjectId;
                    Map<String, String> mpRollupData;
                    String sBOMType;
                    String sAttrMOSAssemblyTypeSelect = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getSelect(context);
                    for (int i = 0; i < iRollupObjectsSize; i++) {

                        mpRollupData = (Map<String, String>) mlRollupData.get(i);
                        sObjectId = mpRollupData.containsKey(DomainConstants.SELECT_ID) ? (String) mpRollupData.get(DomainConstants.SELECT_ID) :
                                (String) mpRollupData.get(pgV3Constants.PHYSICALID);
                        sBOMType = mpRollupData.get(sAttrMOSAssemblyTypeSelect);

                        sBOMType = sBOMType.equalsIgnoreCase(RollupConstants.Basic.EBOM_CHILDREN.getValue()) ?
                                mpProductMap.get(sAttrMOSAssemblyTypeSelect) : sBOMType;
                        String[] rollupObject = {sObjectId};
                        // Relationship pgMOSRolledUpRegistration restricts duplicate connections. Added try/catch block to handle exception in case of duplicate connections
                        try {
                            mRelConnectedProduct = DomainRelationship.connect(context, domMaster, rFPPtoRollUpRel, true,
                                    rollupObject, true);
                            resource.getDynamicSubscription().add(rollupConfig);
                            itr = mRelConnectedProduct.entrySet().iterator();
                            while (itr.hasNext()) {
                                mapRelId = (Map.Entry) itr.next();
                                sConnectionId = mapRelId.getValue().toString();
                                DomainRelationship.setAttributeValue(context, sConnectionId, sAttrMOSAssemblyType, sBOMType);
                            }
                        } catch (FrameworkException e) {
							/* Commented to reduce logger size
                    		logger.log(Level.INFO, "Avoided duplicate Market Registration Rollup connection from Object: {0}", sObjectId);
							 */
                        }
                    }
                }

            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public MapList getRollupDataList(Context context, Map<String, String> mpProductMap) {
        MapList mlRollupData = new MapList();
        try {
            String sType = mpProductMap.get(DomainConstants.SELECT_TYPE);
            String sObjectId = mpProductMap.get(pgV3Constants.PHYSICALID);
            if (UIUtil.isNotNullAndNotEmpty(sType) && isRollupParentType(sType)) {
                boolean processForRollupData = processChildFPPRollup(context, sObjectId, sType);
                if (processForRollupData) {
                    mlRollupData.addAll(
                            rollupAction.getConnectedRollupDataOnFPP(mpProductMap.get(pgV3Constants.PHYSICALID), rollupConfig));
                }
            } else {
                DomainObject domainObject = DomainObject.newInstance(context, sObjectId);
                StringList slCountries = domainObject.getInfoList(context, "from[" + pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE + "].to.id");
                if (!slCountries.isEmpty()) {
                    mlRollupData.add(mpProductMap);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return mlRollupData;
    }

    public boolean setCOPValidation(ProductPart childrenPart) {

        boolean bProcessChildren = true;
        try {
            String sProductType = childrenPart.getType();
            String sParentName = DomainConstants.EMPTY_STRING;
            if (childrenPart.isParentExist()) {
                sParentName = childrenPart.getParent().getName();
            }
            if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sProductType) && masterPart.getName().equalsIgnoreCase(sParentName)) {
                bProcessChildren = performReshipperSetCOPCheck(childrenPart);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bProcessChildren;
    }

    public boolean performPrimarySetCOPCheck(ProductPart primaryBOMChild, Substitute substitute) {
        boolean bSetCOPValidation = true;
        try {
            if (substitute != null) {
                String sKeySubstitute = RollupConstants.Basic.KEY_SUBSTITUTE.getValue();
                Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
                ProductPart substituteProductPart = new ChildExpansion.Builder(context, substitute.getId(), sKeySubstitute)
                        .setExpansionType(rollupRuleConfiguration.getType())
                        .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                        .setExpandLevel((short) 3)
                        .build().getProductPart();
                PrimaryCOPExpansion filter = new PrimaryCOPExpansion();
                ProductPart substitutePrimaryCOPProduct = filter.getTopLevelCOP(substituteProductPart);

                ProductPart primaryCOPProduct = filter.getTopLevelCOP(primaryBOMChild);

                String sSubstituteIsSetProduct;
                String sPrimaryIsSetProduct;
                if (substitutePrimaryCOPProduct != null && primaryCOPProduct != null) {
                    sSubstituteIsSetProduct = substitutePrimaryCOPProduct.getSetProductNameDb();
                    if (!pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sSubstituteIsSetProduct)) {
                        sPrimaryIsSetProduct = primaryCOPProduct.getSetProductNameDb();
                        if (pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sPrimaryIsSetProduct)) {
                            bSetCOPValidation = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bSetCOPValidation;
    }

    public boolean performReshipperSetCOPCheck(ProductPart childrenProduct) {
        String sKeySubstitute = RollupConstants.Basic.KEY_SUBSTITUTE.getValue();

        boolean bSetCOPValidation = true;
        try {
            PrimaryCOPExpansion filter = new PrimaryCOPExpansion();
            ProductPart copProductPart = filter.getTopLevelCOP(childrenProduct);

            if (null != copProductPart) {
                String sSetProduct = copProductPart.getSetProductNameDb();

                if (!pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sSetProduct)) {
                    if (childrenProduct != null && !sKeySubstitute.equalsIgnoreCase(childrenProduct.getBomType()) && childrenProduct.isSubstituteExist()) {

                        ProductPart substituteProductPart;

                        List<Substitute> slSubstitutes = childrenProduct.getSubstitutes();
                        ProductPart reShipperCOPProduct;
                        Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();

                        for (Substitute substitute : slSubstitutes) {

                            substituteProductPart = new ChildExpansion.Builder(context, substitute.getId(), sKeySubstitute)
                                    .setExpansionType(rollupRuleConfiguration.getType())
                                    .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                                    .setExpandLevel((short) 3)
                                    .build().getProductPart();
                            reShipperCOPProduct = filter.getTopLevelCOP(substituteProductPart);

                            if (reShipperCOPProduct != null) {
                                sSetProduct = reShipperCOPProduct.getSetProductNameDb();
                                if (pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sSetProduct)) {
                                    bSetCOPValidation = false;
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bSetCOPValidation;
    }

    public ProductPart getTopLevelCUP(ProductPart productBean) {
        ProductPart topLevelCUP = null;
        try {
            if (productBean.isParentExist()) {
                ProductPart parentProduct = productBean.getParent();
                String sProductType = parentProduct.getType();
                if (UIUtil.isNotNullAndNotEmpty(sProductType) && pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sProductType)) {
                    topLevelCUP = parentProduct;
                } else {
                    getTopLevelCUP(parentProduct);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return topLevelCUP;
    }

    public boolean isRollupParentType(String sProductType) {
        boolean isParentType = false;
        if (UIUtil.isNotNullAndNotEmpty(sProductType)) {
            isParentType = pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sProductType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sProductType)
                    || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sProductType) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sProductType)
                    || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sProductType);

        }
        return isParentType;
    }

    public boolean processChildFPPRollup(Context context, String sObjectId, String sType) {
        boolean bProcessForRollup = true;
        try {
            //Modified for Defect # 44589 - Starts
            if (isChildRollupValid(context, sObjectId, sType)) {
                //Modified for Defect # 44589 - Ends

                Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
                ProductPart fppPart = new ChildExpansion.Builder(context, sObjectId, RollupConstants.Basic.EBOM_CHILDREN.getValue())
                        .setExpansionType(rollupRuleConfiguration.getType())
                        .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                        .setExpandLevel((short) 0)
                        .build().getProductPart();

                //Getting Parent FPP subscriptions
                List<String> listSubscriptions = resource.getDynamicSubscription().getSubscriptions().stream().collect(Collectors.toList());

                //Resetting Subscriptions List for Child FPPs
                resource.getDynamicSubscription().resetSubscriptions();
                RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(fppPart, rollupConfig,
                        RollupConstants.Basic.MARKET_REGISTRATION_ROLLUP_EVENT_IDENTIFIER.getValue(), resource);
                rollupEvent.execute();

                //Triggering DS Emails for child FPP
                resource.getDynamicSubscription().notifyDSEvents(resource, fppPart.getId());
                //Setting back Parent FPPs subscriptions
                resource.getDynamicSubscription().setSubscriptions(listSubscriptions.stream().collect(Collectors.toSet()));

                //Clear Market Registration Event on Child FPPs
                clearMarketRegistrationEvent(context, fppPart);

            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return bProcessForRollup;
    }

    /**
     * Validates object is allowed for Market Registration Rollup
     *
     * @param context
     * @param ProductPart fppPart
     *                    Added for Defect # 44589
     */
    public void clearMarketRegistrationEvent(Context context, ProductPart fppPart) {
        try {
            DomainObject domProduct = DomainObject.newInstance(context, fppPart.getId());
            if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(fppPart.getType())) {
                StringList rollupEventList = StringUtil.split(fppPart.getEventForRollup(), ",");
                String sEventName = rollupConfig.getEventName();
                if (rollupEventList.contains(sEventName)) {
                    rollupEventList.remove(sEventName);
                    if (!rollupEventList.isEmpty()) {
                        StringBuilder sbEvent = new StringBuilder();
                        for (String sEvent : rollupEventList) {
                            if (sbEvent.length() > 0) {
                                sbEvent.append(pgV3Constants.SYMBOL_COMMA);
                            }
                            sbEvent.append(sEvent);
                        }
                        domProduct.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, sbEvent.toString());
                    } else {
                        Map<String, String> mpFPPResetMap = new HashMap<>();
                        mpFPPResetMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_FALSE);
                        mpFPPResetMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, DomainConstants.EMPTY_STRING);
                        mpFPPResetMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
                        domProduct.setAttributeValues(context, mpFPPResetMap);
                    }
                }
            } else {
                domProduct.setAttributeValues(context, MarkUtil.getUnflagAttributeMap(fppPart));
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    /**
     * Validates object is allowed for Market Registration Rollup
     *
     * @param context
     * @param sObjectId
     * @param sType
     * @return boolean
     * Added for Defect # 44589
     */
    public boolean isChildRollupValid(Context context, String sObjectId, String sType) {
        boolean isChildValid = false;
        try {
            DomainObject domProduct = DomainObject.newInstance(context, sObjectId);
            StringList businessObjectSelects = new StringList(5);
            businessObjectSelects.addElement(DomainConstants.SELECT_CURRENT);
            businessObjectSelects.addElement(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context));
            businessObjectSelects.addElement(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context));
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
            Map<?, ?> mpProductMap = domProduct.getInfo(context, businessObjectSelects);

            boolean bMarkForRollup = mpProductMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP) == null ? false : Boolean.parseBoolean((String) mpProductMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP));
            String sEventForRollup = mpProductMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP) == null ? DomainConstants.EMPTY_STRING : (String) mpProductMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
            String sCurrent = (String) mpProductMap.get(DomainConstants.SELECT_CURRENT);
            boolean bRegistrationFlag = mpProductMap.get(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context)) == null ? false : Boolean.parseBoolean((String) mpProductMap.get(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context)));
            boolean bCalculationFlag = mpProductMap.get(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context)) == null ? false : Boolean.parseBoolean((String) mpProductMap.get(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context)));

            isChildValid = (((bMarkForRollup || bRegistrationFlag) && !pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sType))
                    || (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sType) && (bCalculationFlag || bMarkForRollup)
                    && sEventForRollup.contains(rollupConfig.getEventName()))
                    && pgV3Constants.STATE_RELEASE.equalsIgnoreCase(sCurrent));

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return isChildValid;
    }
}



