package com.pg.dsm.rollup_event.mark.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.util.RollupCommonUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.util.MarkAction;
import com.pg.dsm.rollup_event.mark.util.MarkUtil;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class OtherFlag {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    ProductPart markedProductPart;
    Resource resource;
    Properties rollupPageProperties;
    Context context;
    MarkAction markAction;

    public OtherFlag(ProductPart markedProductPart, Resource resource, MarkAction markAction) {
        this.markedProductPart = markedProductPart;
        this.resource = resource;
        this.rollupPageProperties = resource.getRollupPageProperties();
        this.context = resource.getContext();
        this.markAction = markAction;
    }

    public Map<String, Map<String, String>> getRollupEventAttributeMap() {
        Map<String, Map<String, String>> rollupEventAttributeMap = new HashMap<>();
        try {
            if (null != markedProductPart) {

                StringList markedEventAttributeNameList = markedProductPart.getMarkedEventAttributeNameList();
                int size = markedEventAttributeNameList.size();

                String markedEventAttributeName;


                StringList productOidList;
                StringList producedByOidList;
                StringList nonSubstituteOidList;

                Map<Object, Object> tempResultantMap;
                Rollup rollupConfig;
                Map<String, Map<String, String>> batteryFlagRollupEventAttributeMap;
                BatteryFlag batteryFlag;
                StringList productOidListFromAlternate;
                for (int i = 0; i < size; i++) {
                    productOidList = new StringList();
                    producedByOidList = new StringList();
                    nonSubstituteOidList = new StringList();

                    markedEventAttributeName = markedEventAttributeNameList.get(i);
                    rollupConfig = RollupUtil.getRollup(markedEventAttributeName, resource.getRollupRuleConfiguration());
                    if (RollupConstants.Basic.BATTERY_ROLLUP_EVENT_IDENTIFIER.getValue().equalsIgnoreCase(rollupConfig.getIdentifier())) {
                        //Traversing for where used FPP's allowed for battery Rollup
                        batteryFlag = new BatteryFlag(markedProductPart, resource, markAction);
                        batteryFlagRollupEventAttributeMap = batteryFlag.getRollupEventAttributeMap();
                        rollupEventAttributeMap.putAll(markAction.updateEventAttributeMap(rollupEventAttributeMap, batteryFlagRollupEventAttributeMap));
                    } else {
                        // #Added by DSM for 22x CW-05 for Requirement 49480
                        if (isDeviceProductPartWarehouseClassified(rollupConfig) || pgV3Constants.KEY_TRUE.equalsIgnoreCase(markedProductPart.getMarkForRollupFlag())) {
                           continue;
                        }
                        // Skipping MPP in where used FPP traversing
                        productOidList.addAll(skipMasterProductPart());
                        // if COP(BULK) is added as a Substitute to ProductPart
                        productOidList.addAll(getEBOMSubstituteOid(rollupConfig));
                        productOidList.addAll(getFBOMSubstitutesOfProductParts(rollupConfig));

                        // Getting EBOM object from produced by for allowed rollup - DGC/GHS/WHC/SR
                        tempResultantMap = getProducedByResultantMap(rollupConfig);
                        producedByOidList.addAll((StringList) tempResultantMap.get(RollupConstants.Basic.PRODUCED_BY_OID.getValue()));
                        nonSubstituteOidList.addAll((StringList) tempResultantMap.get(RollupConstants.Basic.NON_SUBSTITUTE_OID.getValue()));
                        productOidList.addAll((StringList) tempResultantMap.get(RollupConstants.Basic.PRODUCT_OID.getValue()));

                        // Getting product parts from MPP for allowed rollup - SR
                        tempResultantMap = getProductPartFromMasterResultantMap(rollupConfig);
                        nonSubstituteOidList.addAll((StringList) tempResultantMap.get(RollupConstants.Basic.NON_SUBSTITUTE_OID.getValue()));
                        productOidList.addAll((StringList) tempResultantMap.get(RollupConstants.Basic.PRODUCT_OID.getValue()));

                        // Traversing from alternate to EBOM for allowed rollups
                        tempResultantMap = getProductFromAlternateResultantMap(rollupConfig);
                        nonSubstituteOidList.addAll((StringList) tempResultantMap.get(RollupConstants.Basic.NON_SUBSTITUTE_OID.getValue()));
                        productOidList.addAll((StringList) tempResultantMap.get(RollupConstants.Basic.PRODUCT_OID.getValue()));

                        //DGC/GHS/SR/Certifications/WHC
                        nonSubstituteOidList.add(markedProductPart.getId());

                        if (RollupConstants.Basic.MARKET_REGISTRATION_ROLLUP_EVENT_IDENTIFIER.getValue().equalsIgnoreCase(rollupConfig.getIdentifier())) {
                            // Processes Market Registration rollup calculation on FAB,PAP,FP,PSUB
                            MarketRegistrationFlag marketRegistrationFlag = new MarketRegistrationFlag();
                            if (MarkUtil.isRegistrationParentType(markedProductPart) &&
                                    !pgV3Constants.KEY_TRUE.equalsIgnoreCase(markedProductPart.getMarkForRollupFlag())) {
                                marketRegistrationFlag.processForMarketRegistrationRollup(context, markedProductPart, resource);
                            }
                           /*Modified by DSM (Sogeti) for defect# 44589  - Starts
                            MarketRegistrationFlag marketRegistrationFlag = new MarketRegistrationFlag(productOidList, resource);
                            marketRegistrationRollupEventAttributeMap = marketRegistrationFlag.getRollupEventAttributeMap();
                            rollupEventAttributeMap.putAll(markAction.updateEventAttributeMap(rollupEventAttributeMap, marketRegistrationRollupEventAttributeMap));
                            */
                        } else {
                            // Traversion to primary EBOM from alternate side
                            productOidListFromAlternate = markAction.getPrimaryIntermediateFromAlternate(rollupConfig, new StringList(markedProductPart.getId()));
                            nonSubstituteOidList.addAll(productOidListFromAlternate);
                            productOidList.addAll(productOidListFromAlternate);
                            // Traversing for primary FPP - If product part is on substitute side
                            tempResultantMap = getChildCOPSubstituteResultantMap(rollupConfig, RollupCommonUtil.removeDuplicates(nonSubstituteOidList));
                            rollupEventAttributeMap.putAll(markAction.updateEventAttributeMap(rollupEventAttributeMap, (Map<String, Map<String, String>>) tempResultantMap.get(RollupConstants.Basic.EVENT_ATTRIBUTE_MAP.getValue())));
                            // Traversing for where used FPP's in Primary EBOM
                            rollupEventAttributeMap.putAll(markAction.updateEventAttributeMap(rollupEventAttributeMap, markAction.getFinishedProductPartRollupEventAttributeMap(rollupConfig, RollupCommonUtil.removeDuplicates(productOidList))));
                            //Modified by DSM (Sogeti) for defect# 44589  - Ends
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return rollupEventAttributeMap;
    }

    public Map<String, String> getUnflagAttributeMap(ProductPart productPart) {
        Map<String, String> attributeMap = new HashMap<>();
        try {
            if (pgV3Constants.KEY_TRUE.equalsIgnoreCase(productPart.getMarkForRollupFlag())
                    && !pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(productPart.getType())) {
                attributeMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
            } else {
                StringList markedEventAttributeNameList = markedProductPart.getMarkedEventAttributeNameList();
                for (String attributeName : markedEventAttributeNameList) {
                    attributeMap.put(attributeName, pgV3Constants.KEY_FALSE);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return attributeMap;
    }

    public StringList skipMasterProductPart() {
        StringList productOidList = new StringList();
        try {
            String markedProductPartType = markedProductPart.getType();
            if (!(pgV3Constants.TYPE_MASTERPRODUCTPART.equalsIgnoreCase(markedProductPartType))) {
                productOidList.add(markedProductPart.getId());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return productOidList;
    }

    public StringList getFBOMSubstitutesOfProductParts(Rollup rollupConfig) {
        StringList productOidList = new StringList();
        try {
            Rule rollupRuleProductPartSubstitutes = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES.getValue());
            if (Boolean.parseBoolean(rollupConfig.getnLevel()) && Boolean.parseBoolean(rollupRuleProductPartSubstitutes.getFlag())) {
                productOidList.addAll(markedProductPart.getpLBOMSubstituteFromID());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return productOidList;
    }

    public Map<Object, Object> getChildCOPSubstituteResultantMap(Rollup rollupConfig, StringList nonSubstituteOidList) {
        Map<Object, Object> mpSusbtitutes = new HashMap<>();
        try {
            Rule rollupRuleCOPSubstitute = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_COPS_SUBSTITUTE.getValue());
            Map<String, Map<String, String>> productPartRollupEventAttributeMap = new HashMap<>();
            //DGC/GHS/SR/Certifications/WHC
            if (Boolean.parseBoolean(rollupRuleCOPSubstitute.getFlag())) {
                // Method call to get the primary COPs, In-case of product part is on substitute COP.
                productPartRollupEventAttributeMap = markAction.getFinishedProductPartRollupEventAttributeMapFromSubstitute(rollupConfig, nonSubstituteOidList);
            }
            mpSusbtitutes.put(RollupConstants.Basic.EVENT_ATTRIBUTE_MAP.getValue(), productPartRollupEventAttributeMap);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mpSusbtitutes;
    }

    public Map<Object, Object> getProductFromAlternateResultantMap(Rollup rollupConfig) {
        Map<Object, Object> mpProductParts = new HashMap<>();
        try {
            StringList productOidList = new StringList();
            StringList nonSubstituteOidList = new StringList();
            Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());
            //Certifications
            if (Boolean.parseBoolean(rollupRuleAlternates.getFlag())) {
                // Getting the products ids from alternates
                StringList alternateFromOidList = markedProductPart.getAlternateFromID();

                productOidList.addAll(alternateFromOidList);
                nonSubstituteOidList.addAll(alternateFromOidList);
                productOidList.addAll(filterSubstituteByProducedBy(getSubstitutesInfo(alternateFromOidList)));
            }
            mpProductParts.put(RollupConstants.Basic.PRODUCT_OID.getValue(), productOidList);
            mpProductParts.put(RollupConstants.Basic.NON_SUBSTITUTE_OID.getValue(), nonSubstituteOidList);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mpProductParts;
    }

    public Map<Object, Object> getProductPartFromMasterResultantMap(Rollup rollupConfig) {

        Map<Object, Object> mpProductParts = new HashMap<>();
        try {
            StringList productOidList = new StringList();
            StringList nonSubstituteOidList = new StringList();
            Rule rollupRuleMasterProductParts = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_MASTER_PRODUCT_PART.getValue());
            if (Boolean.parseBoolean(rollupRuleMasterProductParts.getFlag())) {
                // Getting the products ids from Masters
                String productPartFromMasterType;
                String productPartFromMasterOid;
                StringList fromMasterOidList;

                StringList productPartFromMasterOidList = markedProductPart.getProductPartIDFromMaster();
                StringList productPartFromMasterTypeList = markedProductPart.getProductPartTypeFromMaster();
                int fromMasterSize = productPartFromMasterOidList.size();

                for (int j = 0; j < fromMasterSize; j++) {
                    productPartFromMasterType = productPartFromMasterTypeList.get(j);
                    productPartFromMasterOid = productPartFromMasterOidList.get(j);
                    if (rollupRuleMasterProductParts.getInclusionType().contains(productPartFromMasterType)) {
                        fromMasterOidList = filterRawMaterialByProducedBy(getConnectedRawMaterial(productPartFromMasterOid, false, rollupConfig), true);
                        fromMasterOidList.addElement(productPartFromMasterOid);
                        productOidList.addAll(fromMasterOidList);
                        productOidList.addAll(filterSubstituteByProducedBy(getSubstitutesInfo(fromMasterOidList)));
                        nonSubstituteOidList.addElement(productPartFromMasterOid);
                    }
                }
            }
            mpProductParts.put(RollupConstants.Basic.PRODUCT_OID.getValue(), productOidList);
            mpProductParts.put(RollupConstants.Basic.NON_SUBSTITUTE_OID.getValue(), nonSubstituteOidList);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mpProductParts;
    }

    public Map<Object, Object> getProducedByResultantMap(Rollup rollupConfig) {
        Map<Object, Object> mpProducedBy = new HashMap<>();
        try {
            StringList producedByOidList = new StringList();
            StringList productOidList = new StringList();
            StringList nonSubstituteOidList = new StringList();

            Rule rollupRuleProductPartSubstitutes = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES.getValue());
            Rule rollupRuleProducedBy = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCEDBY.getValue());
            Rule rollupRuleAlternate = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());

            if (Boolean.parseBoolean(rollupRuleProducedBy.getFlag()) && !rollupRuleProducedBy.getInclusionType().isEmpty()) {
                if (Boolean.parseBoolean(rollupRuleProducedBy.getChildrenAllowed())) {
                    StringList oIDList = filterRawMaterialByProducedBy(getConnectedRawMaterial(markedProductPart.getId(), true, rollupConfig), true);
                    if (null != oIDList && !oIDList.isEmpty()) {
                        producedByOidList.addAll(oIDList);
                    }
                }
                StringList definesMaterialToIDList = markedProductPart.getDefinesMaterialToID();
                if (null != definesMaterialToIDList && !definesMaterialToIDList.isEmpty()) {
                    producedByOidList.addAll(definesMaterialToIDList);
                }
                productOidList.addAll(producedByOidList);
                nonSubstituteOidList.addAll(producedByOidList);

                if (Boolean.parseBoolean(rollupRuleProductPartSubstitutes.getFlag())) {
                    productOidList.addAll(filterSubstituteByProducedBy(getSubstitutesInfo(producedByOidList)));
                }

                if (Boolean.parseBoolean(rollupRuleAlternate.getFlag())) {
                    productOidList.addAll(filterAlternateByProducedBy(getAlternatesInfo(producedByOidList)));
                }
            }
            mpProducedBy.put(RollupConstants.Basic.PRODUCED_BY_OID.getValue(), producedByOidList);
            mpProducedBy.put(RollupConstants.Basic.PRODUCT_OID.getValue(), productOidList);
            mpProducedBy.put(RollupConstants.Basic.NON_SUBSTITUTE_OID.getValue(), nonSubstituteOidList);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mpProducedBy;
    }

    MapList getSubstitutesInfo(StringList producedByOidList) {
        MapList mlSubstituteInfo = new MapList();
        try {
            mlSubstituteInfo = DomainObject.getInfo(context, producedByOidList.toArray(new String[producedByOidList.size()]), new StringList(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID));
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlSubstituteInfo;
    }

    MapList getAlternatesInfo(StringList producedByOidList) {
        MapList mlSubstituteInfo = new MapList();
        try {
            mlSubstituteInfo = DomainObject.getInfo(context, producedByOidList.toArray(new String[producedByOidList.size()]), new StringList(pgV3Constants.SELECT_ALTERNATE_FROMID));
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlSubstituteInfo;
    }

    StringList filterSubstituteByProducedBy(MapList objectList) {
        StringList producedByOidList = new StringList();
        try {
            if (!objectList.isEmpty()) {
                for (Object obj : objectList) {
                    producedByOidList.addAll(MarkUtil.getStringListFromMap((Map<?, ?>) obj, pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return producedByOidList;
    }

    StringList filterAlternateByProducedBy(MapList objectList) {
        StringList producedByOidList = new StringList();
        try {
            if (!objectList.isEmpty()) {
                for (Object obj : objectList) {
                    producedByOidList.addAll(MarkUtil.getStringListFromMap((Map<?, ?>) obj, pgV3Constants.SELECT_ALTERNATE_FROMID));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return producedByOidList;
    }

    MapList getConnectedRawMaterial(String objectOid, boolean isExpansionLevelN, Rollup rollupConfig) {

        MapList objectList = new MapList();
        try {
            StringBuilder sbObjWhere = new StringBuilder();
            sbObjWhere.append(pgV3Constants.CONST_OPEN_BRACKET).append(DomainConstants.SELECT_CURRENT)
                    .append(pgV3Constants.CONST_SYMBOL_EQUAL).append(pgV3Constants.STATE_RELEASE)
                    .append(pgV3Constants.CONST_CLOSED_BRACKET);

            boolean fromSide = !isExpansionLevelN;
            boolean toSide = isExpansionLevelN;

            Rule rollupRuleProducedBy = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCEDBY.getValue());

            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                try {
                    DomainObject dObj = DomainObject.newInstance(context, objectOid);
                    objectList = dObj.getRelatedObjects(context,
                            (isExpansionLevelN) ? pgV3Constants.RELATIONSHIP_EBOM : pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL, // relationship pattern
                            (isExpansionLevelN) ? rollupConfig.getAllowedTypesInFPPTraversion() : rollupRuleProducedBy.getInclusionType(), // Type pattern
                            toSide, // to side
                            fromSide, // from side
                            (isExpansionLevelN) ? (short) 0 : (short) 1, // recursion level
                            (isExpansionLevelN) ? new StringList(pgV3Constants.SELECT_PGDEFINESMATERIAL_TOID) : new StringList(DomainConstants.SELECT_ID), // object selects
                            null, // rel selects
                            sbObjWhere.toString(), // object where clause
                            null, // relWhereClause
                            0, //limit
                            null, // postRelPattern,
                            (isExpansionLevelN) ? rollupRuleProducedBy.getFromType() : rollupRuleProducedBy.getInclusionType(),// PostPattern
                            null);// Map post pattern
                } catch (FrameworkException e) {
                    logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return objectList;
    }

    StringList filterRawMaterialByProducedBy(MapList objectList, boolean isExpansionLevelN) {
        StringList producedByOidList = new StringList();
        try {
            if (!objectList.isEmpty()) {
                Map<?, ?> objMap;
                StringList oIDList;
                String oID;
                Iterator<?> iterator = objectList.iterator();
                while (iterator.hasNext()) {
                    objMap = (Map<?, ?>) iterator.next();
                    oIDList = MarkUtil.getStringListFromMap(objMap, pgV3Constants.SELECT_PGDEFINESMATERIAL_TOID);
                    if (null != oIDList && !oIDList.isEmpty() && isExpansionLevelN) {
                        producedByOidList.addAll(oIDList);
                    } else {
                        oID = (String) objMap.get(DomainConstants.SELECT_ID);
                        if (UIUtil.isNotNullAndNotEmpty(oID)) {
                            producedByOidList.add(oID);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return producedByOidList;
    }

    public StringList getEBOMSubstituteOid(Rollup rollupConfig) {
        StringList oIDList = new StringList();
        try {
            StringList bomSubstituteFromOidList = markedProductPart.geteBOMSubstituteFromID();
            StringList bomSubstituteFromTypeList = markedProductPart.geteBOMSubstituteFromType();

            int bomSize = bomSubstituteFromOidList.size();
            String substituteType;
            for (int j = 0; j < bomSize; j++) {
                substituteType = bomSubstituteFromTypeList.get(j);
                // checks if COP(BULK) is added as a Substitute to ProductPart
                if (isCOPBulkAddedAsSubstituteToProductPart(rollupConfig, substituteType)) {
                    oIDList.add(bomSubstituteFromOidList.get(j));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return oIDList;
    }

    /**
     * Performs DPP validation for Warehouse Classification Rollup
     *
     * @param rollupConfig
     * @return boolean
     */
    public boolean isDeviceProductPartWarehouseClassified(Rollup rollupConfig) {
        boolean bValidation = false;
        try {
            String markedProductPartType = markedProductPart.getType();
            String wHCDoesDeviceContainsFlammableLiquid = markedProductPart.getwHCDoesDeviceContainsFlammableLiquid();
            bValidation = pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(markedProductPartType)
                    && RollupConstants.Basic.WAREHOUSE_ROLLUP_EVENT_IDENTIFIER.getValue().equalsIgnoreCase(rollupConfig.getIdentifier())
                    && pgV3Constants.KEY_NO_VALUE.equalsIgnoreCase(wHCDoesDeviceContainsFlammableLiquid);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return bValidation;

    }

    public boolean isCOPBulkAddedAsSubstituteToProductPart(Rollup rollupConfig, String substituteType) {
        boolean bValidation = false;
        try {
            Rule rollupRuleProductPartSubstitutes = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES.getValue());
            Rule rollupRuleCOPSubstitute = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_COPS_SUBSTITUTE.getValue());
            bValidation = Boolean.parseBoolean(rollupRuleProductPartSubstitutes.getFlag())
                    || (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(substituteType)
                    && Boolean.parseBoolean(rollupRuleCOPSubstitute.getFlag())
                    && rollupRuleProductPartSubstitutes.getInclusionType().contains(substituteType));
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bValidation;
    }


}