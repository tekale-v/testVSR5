package com.pg.dsm.rollup_event.mark.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.util.RollupCommonUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class MarkAction {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    ProductPart markedProductPart;
    Context context;
    Properties rollupPageProperties;
    Properties rollupCustomProperties;
    Resource resource;

    public MarkAction(Resource resource) {
        this.context = resource.getContext();
        this.resource = resource;
        this.rollupPageProperties = resource.getRollupPageProperties();
        this.rollupCustomProperties = resource.getRollupCustomProperties();
    }


    public void setMarkedProductPart(ProductPart markedProductPart) {
        this.markedProductPart = markedProductPart;
    }

    public List<ProductPart> getProductPartsMarkedForRollup() throws FrameworkException {
        List<ProductPart> markedProductPartList = new ArrayList<>();
        try {
            MapList objectList = DomainObject.findObjects(context, //context
                    rollupPageProperties.getProperty("Str_Allowed_ProductPart_Types_ForMarking"), //Type Pattern
                    DomainConstants.QUERY_WILDCARD, //Vault
                    rollupPageProperties.getProperty("Str_Objwhere_ProductParts_RollupFlags"), //Object Where
                    getMarkedForRollupObjectSelects()); //Object Selects

            if (null != objectList && !objectList.isEmpty()) {
                Map<Object, Object> productPartMap;
                Iterator productPartIterator = objectList.iterator();
                while (productPartIterator.hasNext()) {
                    productPartMap = (Map) productPartIterator.next();
                    productPartMap.put(RollupConstants.Basic.DEFAULT_QUANTITY_VALUE.getValue(),
                            rollupPageProperties.getProperty(RollupConstants.Basic.DEFAULT_QUANTITY_VALUE.getValue()));
                    productPartMap.put(
                            RollupConstants.Basic.NUMBER_OF_BATTERIES_SHIPPED_OUTSIDE_DEVICE_DEFAULT_VALUE.getValue(),
                            rollupPageProperties.getProperty(
                                    RollupConstants.Basic.NUMBER_OF_BATTERIES_SHIPPED_OUTSIDE_DEVICE_DEFAULT_VALUE
                                            .getValue()));
                    productPartMap.put(
                            RollupConstants.Basic.NUMBER_OF_BATTERIES_SHIPPED_INSIDE_DEVICE_DEFAULT_VALUE.getValue(),
                            rollupPageProperties.getProperty(
                                    RollupConstants.Basic.NUMBER_OF_BATTERIES_SHIPPED_INSIDE_DEVICE_DEFAULT_VALUE
                                            .getValue()));
                    productPartMap.put(RollupConstants.Basic.NUMBER_OF_BATTERIES_REQUIRED_DEFAULT_VALUE.getValue(),
                            rollupPageProperties.getProperty(
                                    RollupConstants.Basic.NUMBER_OF_BATTERIES_REQUIRED_DEFAULT_VALUE.getValue()));
                    markedProductPartList.add(new ProductPart(context, productPartMap, DomainConstants.RELATIONSHIP_EBOM));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return markedProductPartList;
    }

    public Map<String, Map<String, String>> getFinishedProductPartRollupEventAttributeMapFromSubstitute(
            Rollup rollupConfig, StringList objectOidList) {
        Map<String, Map<String, String>> eventAttributeMap = new HashMap<>();
        try {
            if (null != objectOidList && !objectOidList.isEmpty()) {
                Rule rollupRuleFPPinFPP = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_FPP_IN_FPP.getValue());
                StringList substitutePrimaryIntermediateList = getPrimaryIntermediateOidFromSubstituteIntermediate(
                        rollupConfig, objectOidList);
              /* Modified by DSM (Sogeti) for defect# 44589 - Starts
                if (RollupConstants.Basic.MARKET_REGISTRATION_ROLLUP_EVENT_IDENTIFIER.getValue()
                        .equalsIgnoreCase(rollupConfig.getIdentifier())) {
                    MarketRegistrationFlag marketRegistrationFlag = new MarketRegistrationFlag(
                            substitutePrimaryIntermediateList, resource);
                    Map<String, Map<String, String>> marketRegistrationRollupEventAttributeMap = marketRegistrationFlag
                            .getRollupEventAttributeMap();
                    eventAttributeMap
                            .putAll(updateEventAttributeMap(eventAttributeMap, marketRegistrationRollupEventAttributeMap));
                } else {
                Modified by DSM (Sogeti) for defect# 44589 - Ends */
                MapList finishedProductMapList = getIntermediateFinishedProduct(rollupConfig,
                        RollupCommonUtil.removeDuplicates(substitutePrimaryIntermediateList));

                if (!Boolean.parseBoolean(rollupRuleFPPinFPP.getFlag())
                        && !pgV3Constants.INTERMEDIATE.equalsIgnoreCase(rollupConfig.getIdentifier())) {
                    finishedProductMapList = getFinishedProductFirstLevelFinishedProduct(finishedProductMapList);
                }
                Map<String, Map<String, String>> finishedProductRollupEventMap = getFinishedProductRollupEventMap(
                        rollupConfig, finishedProductMapList);

                if (null != finishedProductRollupEventMap && !finishedProductRollupEventMap.isEmpty()) {
                    eventAttributeMap.putAll(finishedProductRollupEventMap);
                }
                //  }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return eventAttributeMap;
    }

    public Map<String, Map<String, String>> getFinishedProductPartRollupEventAttributeMap(Rollup rollupConfig,
                                                                                          StringList objectOidList) {

        Map<String, Map<String, String>> eventAttributeMap = new HashMap<>();
        try {
            Rule rollupRuleFPPinFPP = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_FPP_IN_FPP.getValue());

            MapList finishedProductMapList = getIntermediateFinishedProduct(rollupConfig, objectOidList);

            if (!Boolean.parseBoolean(rollupRuleFPPinFPP.getFlag())) {
                finishedProductMapList = getFinishedProductFirstLevelFinishedProduct(finishedProductMapList);
            }
            Map<String, Map<String, String>> finishedProductRollupEventMap = getFinishedProductRollupEventMap(rollupConfig,
                    finishedProductMapList);

            if (null != finishedProductRollupEventMap && !finishedProductRollupEventMap.isEmpty()) {
                eventAttributeMap.putAll(finishedProductRollupEventMap);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return eventAttributeMap;
    }

    private StringList getPrimaryIntermediateOidFromSubstituteIntermediate(Rollup rollupConfig,
                                                                           StringList substituteOidList) {

        StringList objectSelectList = new StringList(3);
        objectSelectList.add(DomainConstants.SELECT_TYPE);
        objectSelectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROMTYPE);
        //Modified by DSM (Sogeti) for defect# 44589 - Starts
        objectSelectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID);
        // Modified by DSM (Sogeti) for defect# 44589 - Ends
        StringList intermediateOidList = new StringList();
        try {
            int size = substituteOidList.size();
            String substituteOid;
            DomainObject dObj = DomainObject.newInstance(context);
            for (int i = 0; i < size; i++) {
                substituteOid = substituteOidList.get(i);
                dObj.setId(substituteOid);
                intermediateOidList.addAll(getIntermediateOidList(dObj, rollupConfig, objectSelectList));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        intermediateOidList = RollupCommonUtil.removeDuplicates(intermediateOidList);
        return intermediateOidList;
    }

    public MapList getIntermediateFinishedProduct(Rollup rollupConfig, StringList intermediateOidList) {

        MapList objectList;
        MapList mlFPPList = new MapList();
        Rule rollupRuleFPPinFPP = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_FPP_IN_FPP.getValue());
        String typePattern = rollupConfig.getAllowedTypesInFPPTraversion();
        String relPattern = rollupConfig.getAllowedRelationships();

        String postTypePattern = pgV3Constants.TYPE_FINISHEDPRODUCTPART;

        StringList objectSelectList = new StringList(4);
        objectSelectList.add(DomainConstants.SELECT_ID);
        if (Boolean.parseBoolean(rollupRuleFPPinFPP.getFlag())) {
            objectSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
            objectSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
            objectSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP);
            //Modified by DSM (Sogeti) for defect# 44589 - Starts
            objectSelectList.add(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context));
            //Modified by DSM (Sogeti) for defect# 44589 - Ends
        }
        if (!Boolean.parseBoolean(rollupRuleFPPinFPP.getFlag())) {
            postTypePattern = pgV3Constants.TYPE_PGCUSTOMERUNITPART;
        }

        try {
            int size = intermediateOidList.size();
            String intermediateOid;

            DomainObject intermediateObj = DomainObject.newInstance(context);

            for (int i = 0; i < size; i++) {
                intermediateOid = intermediateOidList.get(i);

                intermediateObj.setId(intermediateOid);
                objectList = intermediateObj.getRelatedObjects(context, relPattern, // relationship pattern
                        typePattern, // Type pattern
                        true, // to side
                        false, // from side
                        (short) 0, // recursion level
                        objectSelectList, // object selects
                        null, // rel selects
                        getParentFinishedProductWhereClause(rollupConfig), // object where clause
                        null, // relWhereClause
                        0, // limit
                        pgV3Constants.RELATIONSHIP_EBOM, // postRelPattern,
                        postTypePattern, // PostPattern
                        null);// Map post pattern
                mlFPPList.addAll(objectList);
            }
            mlFPPList = RollupCommonUtil.removeDuplicateValues(mlFPPList);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return mlFPPList;
    }

    public MapList getFinishedProductFirstLevelFinishedProduct(MapList objectList) {
        MapList returnMapList = new MapList();
        StringList objectSelectList = new StringList(4);
        objectSelectList.add(DomainConstants.SELECT_ID);
        objectSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
        objectSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP);
        //Modified by DSM (Sogeti) for defect# 44589 - Starts
        objectSelectList.add(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context));
        //Modified by DSM (Sogeti) for defect# 44589 - Ends
        if (null != objectList && !objectList.isEmpty()) {
            Iterator<?> objMapItr = objectList.iterator();
            Map<?, ?> objMap;
            String objOid;
            MapList objMapList;

            StringBuilder sbObjectWhere = new StringBuilder();
            sbObjectWhere.append(rollupPageProperties.getProperty("Str_ObjectWhere_Part_State"))
                    .append(pgV3Constants.SYMBOL_SPACE)
                    .append(rollupPageProperties.getProperty("Str_ObjectWhere_FPP_HALB"));
            try {
                DomainObject dObj = DomainObject.newInstance(context);
                while (objMapItr.hasNext()) {
                    objMap = (Map<?, ?>) objMapItr.next();
                    objOid = (String) objMap.get(DomainConstants.SELECT_ID);

                    if (UIUtil.isNotNullAndNotEmpty(objOid)) {
                        dObj.setId(objOid);
                        objMapList = dObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM, // relationship
                                // pattern
                                pgV3Constants.TYPE_FINISHEDPRODUCTPART, // Type pattern
                                true, // to side
                                false, // from side
                                (short) 1, // recursion level
                                objectSelectList, // object selects
                                null, // rel selects
                                sbObjectWhere.toString(), // object where clause
                                null, // relWhereClause
                                0, // limit
                                null, // postRelPattern,
                                null, // PostPattern
                                null);// Map post pattern

                        if (null != objMapList && !objMapList.isEmpty()) {
                            returnMapList.addAll(objMapList);
                        }
                    }
                }
            } catch (FrameworkException e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
        }
        return returnMapList;
    }

    /**
     * This method generates the FPP marking map for all where used FPP's
     *
     * @param rollupConfig
     * @param objectList
     * @return Map eventMap < fppObjectId, fppMarkingMap >
     */
    public Map<String, Map<String, String>> getFinishedProductRollupEventMap(Rollup rollupConfig, MapList objectList) {
        Map<String, Map<String, String>> eventMap = new HashMap<>();
        try {
            Map<?, ?> objMap;
            String objOid;
            String sAssemblyType;
            String sEvents;
            Map<String, String> attributeMap;
            Iterator<?> objItr = objectList.iterator();
            String rollupType = rollupConfig.getIdentifier();
            String rollupEventName = rollupConfig.getEventName();
            StringBuilder sbEvent;
            while (objItr.hasNext()) {
                attributeMap = new HashMap<>();
                objMap = (Map<?, ?>) objItr.next();
                objOid = (String) objMap.get(DomainConstants.SELECT_ID);
                objOid = (objOid == null) ? DomainConstants.EMPTY_STRING : objOid;
                sAssemblyType = (String) objMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
                sEvents = (String) objMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
                sEvents = (sEvents == null) ? DomainConstants.EMPTY_STRING : sEvents;

                if (pgV3Constants.INTERMEDIATE.equalsIgnoreCase(rollupType)) {
                    attributeMap.putAll(getIntermediateMap(sAssemblyType, sEvents));
                } else {
                    if (!sEvents.contains(rollupEventName)) {
                        sbEvent = new StringBuilder();
                        if (UIUtil.isNotNullAndNotEmpty(sEvents)) {
                            sbEvent.append(sEvents);
                            sbEvent.append(pgV3Constants.SYMBOL_COMMA);
                        }
                        sbEvent.append(rollupEventName);
                        attributeMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, sbEvent.toString());
                        //Modified by DSM (Sogeti) for defect# 44589 - Starts
                        attributeMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_TRUE);
                        attributeMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
                        //Modified by DSM (Sogeti) for defect# 44589 - Ends
                    }
                }
                if (!attributeMap.isEmpty()) {
                    eventMap.put(objOid, attributeMap);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return eventMap;
    }

    MapList getParentExpansionByRollupType(DomainObject productObj, Rollup rollupConfig, StringList objectSelectList, String sPostTypePattern)
            throws FrameworkException {
        return productObj.getRelatedObjects(context, rollupConfig.getAllowedRelationships(), // relationship pattern
                rollupConfig.getAllowedTypesInFPPTraversion(), // Type pattern
                true, // to side
                false, // from side
                (short) 0, // recursion level
                objectSelectList, // object selects
                null, // rel selects
                getObjectWhereClauseByRollupType(), // object where clause
                null, // relWhereClause
                0, // limit
                rollupConfig.getAllowedRelationships(), // postRelPattern,
                sPostTypePattern, // PostPattern
                null);// Map post pattern
    }

    String getObjectWhereClauseByRollupType() {
        return DomainConstants.SELECT_CURRENT.concat(pgV3Constants.CONST_SYMBOL_EQUAL)
                .concat(pgV3Constants.STATE_RELEASE);
    }

    StringList getIntermediateOidList(DomainObject productObj, Rollup rollupConfig, StringList objectSelectList) {
        Map<Object, Object> productMap;
        String productType;
        StringList eBOMSubstituteFromOidList;
        StringList eBOMSubstituteFromTypeList;
        int objSize;
        String substituteType;
        StringList intermediateOidList = new StringList();
        try {
            MapList productList = getParentExpansionByRollupType(productObj, rollupConfig, objectSelectList, rollupConfig.getAllowedIntermediateSubstitutes());
            Iterator<Object> productItr = productList.iterator();
            while (productItr.hasNext()) {
                productMap = (Map<Object, Object>) productItr.next();
                productType = (String) productMap.get(DomainConstants.SELECT_TYPE);
                eBOMSubstituteFromOidList = MarkUtil.getStringListFromMap(productMap,
                        pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID);
                eBOMSubstituteFromTypeList = MarkUtil.getStringListFromMap(productMap,
                        pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROMTYPE);
                objSize = eBOMSubstituteFromOidList.size();
                for (int j = 0; j < objSize; j++) {
                    substituteType = eBOMSubstituteFromTypeList.get(j);
                    if (isQualifiedSubstitute(productType, substituteType, rollupConfig)) {
                        intermediateOidList.addElement(eBOMSubstituteFromOidList.get(j));
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return intermediateOidList;
    }

    public String getParentFinishedProductWhereClause(Rollup rollupConfig) {
        StringBuilder sbObjectWhere = new StringBuilder();
        Rule rollupRuleShippableHALB = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_SHIPPABLE_HALB.getValue());
        try {
            String sRollupType = rollupConfig.getIdentifier();
            if (UIUtil.isNotNullAndNotEmpty(sRollupType)) {
                sbObjectWhere.append(rollupPageProperties.getProperty("ALLOWED.STATE.RELEASE.FOR.FPP.SHIPPABLE_HALB"));
                if (!Boolean.parseBoolean(rollupRuleShippableHALB.getFlag())) {
                    sbObjectWhere.append(pgV3Constants.SYMBOL_SPACE)
                            .append(rollupPageProperties.getProperty("OBJECT.WHERE.CLAUSE.FOR.FPP.SHIPPABLE_HALB"));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return sbObjectWhere.toString();
    }

    public boolean isQualifiedSubstitute(String productType, String substituteType, Rollup rollupConfig) {
        boolean isQualified = false;
        try {
            Rule rollupRuleProductPart = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
            Rule rollupRuleProductPartChildren = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_CHILDPRODUCTPARTS_RULE.getValue());
            Rule rollupRulePAP = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PAP.getValue());
            Rule rollupRuleCOPBulk = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_COP_BULK.getValue());
            Rule rollupRuleProductPartSubstitutes = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES.getValue());
            Rule rollupRuleCUPReshipper = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_CUP_RESHIPPER.getValue());
            Rule rollupRuleSubstituteOfCOP = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_COPS_SUBSTITUTE.getValue());
            Rule rollupRuleFPPinFPP = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_FPP_IN_FPP.getValue());
            Rule rollupRulePSUB = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PSUB.getValue());


            if (null != rollupRuleProductPart && null != rollupRuleProductPartChildren && null != rollupRulePAP
                    && null != rollupRuleCOPBulk && null != rollupRuleProductPartSubstitutes && null != rollupRuleCUPReshipper &&
                    null != rollupRuleSubstituteOfCOP) {

                boolean bIsSubstituteTypeCOP = pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(substituteType);
                boolean bIsSubstituteTypePAP = pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(substituteType);
                boolean bIsSubstituteTypeFAB = pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(substituteType);
                boolean bIsSubstituteTypeCUP = pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(substituteType);
                boolean bIsAllowedProductPartTypes = rollupRuleProductPart.getInclusionType().contains(productType);
                boolean bIsAllowedProductPartChildTypes = Boolean.parseBoolean(rollupRuleProductPartSubstitutes.getChildrenAllowed());
                boolean bIsAllowedRollupTypesForPAPSubstitutes = Boolean.parseBoolean(rollupRulePAP.getSubstituteAllowed());
                boolean bIsAllowedCOPBulkSubstituteForProductParts = Boolean.parseBoolean(rollupRuleCOPBulk.getFlag())
                        && rollupRuleCOPBulk.getInclusionType().contains(substituteType);
                boolean bIsAllowedRollupTypesForSubstitutesOfProductParts = Boolean
                        .parseBoolean(rollupRuleProductPartSubstitutes.getFlag());
                boolean bIsRollupTypeIntermediate = pgV3Constants.INTERMEDIATE.equalsIgnoreCase(rollupConfig.getIdentifier());
                boolean bIsReshipperAllowed = Boolean.parseBoolean(rollupRuleCUPReshipper.getFlag());
                boolean bCOPBulkAllowed = Boolean.parseBoolean(rollupRuleCOPBulk.getFlag());
                boolean bSubstituteofCOPAllowed = Boolean.parseBoolean(rollupRuleSubstituteOfCOP.getFlag());
                boolean bSubstituteFPPAllowed = Boolean.parseBoolean(rollupRuleFPPinFPP.getFlag())
                        && Boolean.parseBoolean(rollupRuleFPPinFPP.getSubstituteAllowed())
                        && rollupRuleFPPinFPP.getSubstituteInclusionType().contains(substituteType);
                boolean bPSUBIsAllowed = pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(substituteType)
                        && Boolean.parseBoolean(rollupRulePSUB.getFlag())
                        && Boolean.parseBoolean(rollupRulePSUB.getSubstituteAllowed());


                /* Below substitutes checks
                   check1: Child COP substitute check and WC type check when COP is bulk and substitute is productpart
                   check2: Substitute PAP check
                   check3: COP Bulk Check  APP/DPP/PAP --Sub--COP BULK
                   check4: Substitue FAB check
                   check5: COP Bulk Check COPBULK --Sub--APP/DPP/PAP & COPBULK --sub--COPBULK
                   check6: Intermediate
                   check7: Substitute FPP Check
                   check8: PSUB Check
                 */

                isQualified = (bIsSubstituteTypeCOP && !(bIsAllowedProductPartTypes && !bIsAllowedProductPartChildTypes))
                        || (bIsSubstituteTypePAP && bIsAllowedRollupTypesForPAPSubstitutes)
                        || (bIsAllowedCOPBulkSubstituteForProductParts && bIsAllowedRollupTypesForSubstitutesOfProductParts
                        && rollupRuleProductPartSubstitutes.getInclusionType().contains(substituteType))
                        || (bIsSubstituteTypeFAB && bIsAllowedRollupTypesForSubstitutesOfProductParts)
                        || (bIsSubstituteTypeCUP && bIsReshipperAllowed)
                        || (bIsSubstituteTypeCOP && bIsAllowedProductPartTypes && bCOPBulkAllowed && bSubstituteofCOPAllowed)
                        || (bIsRollupTypeIntermediate)
                        || (bSubstituteFPPAllowed)
                        || (bPSUBIsAllowed)
                ;
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return isQualified;
    }

    public StringList getMarkedForRollupObjectSelects() {
        StringList objSelects = new StringList(31);

        // basics.
        objSelects.addElement(pgV3Constants.PHYSICALID);
        objSelects.addElement(DomainConstants.SELECT_ID);
        objSelects.addElement(DomainConstants.SELECT_TYPE);
        objSelects.addElement(DomainConstants.SELECT_NAME);
        objSelects.addElement(DomainConstants.SELECT_REVISION);
        objSelects.addElement(DomainConstants.SELECT_CURRENT);

        // general attributes
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISTHEPRODUCTABATTERY);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDINSIDEDEVICE);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSMARTLABELREADY);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSREADY);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
        objSelects.addElement(RollupConstants.Attribute.SET_PRODUCT_NAME.getSelect(context));

        // rel expansion selects
        //Modified by DSM (Sogeti) for defect# 44589 - Starts
        objSelects.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID);
        //Modified by DSM (Sogeti) for defect# 44589 - Ends
        objSelects.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROMTYPE);
        objSelects.addElement(pgV3Constants.SELECT_PGDEFINESMATERIAL_TOID);
        objSelects.addElement(pgV3Constants.SELECT_PRODUCTPARTID_FROM_MASTER);
        objSelects.addElement(pgV3Constants.SELECT_PRODUCTPARTTYPE_FROM_MASTER);
        objSelects.addElement(pgV3Constants.SELECT_ALTERNATE_FROMID);
        objSelects.addElement(pgV3Constants.SELECT_PLBOMSUBSTITUTE_FROMID);

        // rollup flag attributes
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDGCROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGHSROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSLROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSRROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCERTIFICATIONSROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGWAREHOUSEROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDOESDEVICECONTAINFLAMMABLELIQUID);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYROLLUPFLAG);
        objSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINGREDIENTSTATEMENTROLLUPFLAG);
        //Modified by DSM (Sogeti) for defect# 44589 - Starts
        objSelects.addElement(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context));
        //Modified by DSM (Sogeti) for defect# 44589 - Ends
        return objSelects;
    }

    public StringList getProductObjectOidList(List<ProductPart> productPartList) {
        StringList objectOidList = new StringList();
        for (ProductPart productPart : productPartList) {
            objectOidList.addElement(productPart.getId());
        }
        return objectOidList;
    }

    public MapList getProductEventNameInfo(Context context, List<ProductPart> productPartList)
            throws FrameworkException {
        MapList objectList = new MapList();
        try {
            StringList objectOidList = getProductObjectOidList(productPartList);
            if (!objectOidList.isEmpty()) {
                objectList = DomainObject.getInfo(context, objectOidList.toArray(new String[objectOidList.size()]),
                        new StringList(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP));
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return objectList;
    }

    /**
     * This method consolidates FPP event Maps
     *
     * @param consolidatedMap
     * @param subMap
     * @return consolidated FPP event Map
     */
    public Map<String, Map<String, String>> updateEventAttributeMap(Map<String, Map<String, String>> consolidatedMap,
                                                                    Map<String, Map<String, String>> subMap) {
        String subEntryKey;
        Map<String, String> mainAttributeMap;
        Map<String, String> subAttributeMap;
        String subEvent;
        String mainCommaSeparatedEvents;
        StringList slSubEventList;
        // create a copy of the consolidate map.
        Map<String, Map<String, String>> mainMap = new HashMap<>(consolidatedMap);
        try {
            String sEvent;
            for (Map.Entry<String, Map<String, String>> subEntry : subMap.entrySet()) {
                subEntryKey = subEntry.getKey();
                subAttributeMap = subEntry.getValue();
                if (mainMap.containsKey(subEntryKey)) {
                    mainAttributeMap = mainMap.get(subEntryKey);
                    mainCommaSeparatedEvents = mainAttributeMap.get(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP);
                    subEvent = subAttributeMap.get(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP);
                    slSubEventList = StringUtil.split(subEvent, pgV3Constants.SYMBOL_COMMA);
                    int iSubEventListSize = slSubEventList.size();
                    for (int i = 0; i < iSubEventListSize; i++) {
                        sEvent = slSubEventList.get(i);
                        if (!mainCommaSeparatedEvents.contains(sEvent)) {
                            //Modified by DSM (Sogeti) for defect# 43716 - Starts
                            mainCommaSeparatedEvents = mainCommaSeparatedEvents.concat(pgV3Constants.SYMBOL_COMMA).concat(sEvent);
                            mainAttributeMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, mainCommaSeparatedEvents);
                            mainMap.put(subEntryKey, mainAttributeMap);
                            //Modified by DSM (Sogeti) for defect# 43716 - Ends
                        }
                    }
                } else {
                    mainMap.put(subEntryKey, subAttributeMap);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mainMap;
    }

    public Map<String, String> getIntermediateMap(String sAssemblyType, String sFPPEvent) {

        Map<String, String> attributeMap = new HashMap<>();
        try {
            String sEvents;
            String sObjectType = markedProductPart.getType();

            if (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(sObjectType) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(sObjectType)) {
                sEvents = rollupPageProperties.getProperty("Str_RM_RollupEvents");

            } else if (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sObjectType)) {
                sEvents = rollupPageProperties.getProperty("Str_FAB_RollupEvents");

            } else if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sObjectType)) {
                sEvents = markedProductPart.getEventForRollup();
            } else {
                if (UIUtil.isNotNullAndNotEmpty(sAssemblyType)
                        && sAssemblyType.equalsIgnoreCase(pgV3Constants.SHIPPABLE_HALB)) {
                    sEvents = rollupPageProperties.getProperty("Str_FPPHALB_RollupEvents");
                } else {
                    sEvents = rollupPageProperties.getProperty("Str_FPP_RollupEvents");
                }
            }
            //Modified by DSM (Sogeti) for defect# 44589 - Starts
            StringList rollupEventList = StringUtil.split(sEvents, ",");

            StringBuilder sbEvent = new StringBuilder();
            sbEvent.append(sFPPEvent);
            for (String rollupEventName : rollupEventList) {
                if (!sFPPEvent.contains(rollupEventName)) {
                    if (sbEvent.length() > 0) {
                        sbEvent.append(pgV3Constants.SYMBOL_COMMA);
                    }
                    sbEvent.append(rollupEventName);
                }
            }
            sEvents = sbEvent.toString();
            attributeMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_TRUE);
            attributeMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
            //Modified by DSM (Sogeti) for defect# 44589 - Ends
            attributeMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, sEvents);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return attributeMap;
    }

    public StringList getPrimaryIntermediateFromAlternate(Rollup rollupConfig, StringList objectOidList) {
        StringList intermediateOidList = new StringList();
        try {
            Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());

            if (Boolean.parseBoolean(rollupRuleAlternates.getFlag())) {

                int iSize = objectOidList.size();
                StringList objectSelectList = new StringList(3);
                objectSelectList.add(DomainConstants.SELECT_TYPE);
                objectSelectList.add(pgV3Constants.SELECT_ALTERNATE_FROMID);
                objectSelectList.add("to[" + pgV3Constants.RELATIONSHIP_ALTERNATE + "].from.type");

                DomainObject dObj = DomainObject.newInstance(context);
                for (int i = 0; i < iSize; i++) {
                    dObj.setId(objectOidList.get(i));
                    intermediateOidList.addAll(getIntermediateOidListFromAlternates(dObj, rollupConfig, objectSelectList));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return intermediateOidList;
    }

    public StringList getIntermediateOidListFromAlternates(DomainObject domObj, Rollup rollupConfig, StringList slObjectSelects) {
        StringList intermediateOidList = new StringList();
        try {
            Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());

            MapList mlIntermediates = getParentExpansionByRollupType(domObj, rollupConfig, slObjectSelects, rollupRuleAlternates.getInclusionType());
            Iterator<Object> productItr = mlIntermediates.iterator();

            Map productMap;
            String sIntermediateType;
            StringList eBOMAlternateFromOidList;
            StringList eBOMAlternateFromTypeList;
            int iObjSize;
            while (productItr.hasNext()) {
                productMap = (Map<Object, Object>) productItr.next();
                eBOMAlternateFromOidList = MarkUtil.getStringListFromMap(productMap,
                        pgV3Constants.SELECT_ALTERNATE_FROMID);
                eBOMAlternateFromTypeList = MarkUtil.getStringListFromMap(productMap,
                        "to[" + pgV3Constants.RELATIONSHIP_ALTERNATE + "].from.type");
                iObjSize = eBOMAlternateFromOidList.size();
                for (int i = 0; i < iObjSize; i++) {
                    sIntermediateType = eBOMAlternateFromTypeList.get(i);
                    if (isQualifiedAlternate(sIntermediateType, rollupConfig)) {
                        intermediateOidList.add(eBOMAlternateFromOidList.get(i));
                    }
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return intermediateOidList;
    }

    public boolean isQualifiedAlternate(String alternateFromType, Rollup rollupConfig) {
        Boolean isQualified = false;
        try {
            Rule rollupRuleAlternates = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_ALTERNATES.getValue());

            isQualified = Boolean.parseBoolean(rollupRuleAlternates.getFlag())
                    && rollupRuleAlternates.getInclusionType().contains(alternateFromType);
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return isQualified;
    }


    public boolean isValidIntermediate(ProductPart markedPart) {
        boolean isValidIntermediate = false;
        String sProductType = markedPart.getType();
        try {

            if (UIUtil.isNotNullAndNotEmpty(sProductType) && (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(sProductType)) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(sProductType)) {
                String sRMId = markedPart.getId();
                DomainObject domRM = DomainObject.newInstance(context, sRMId);

                String sRMPrevRevId = domRM.getInfo(context, pgV3Constants.SELECT_PREVIOUS_ID);
                StringList slAlternateList = domRM.getInfoList(context, pgV3Constants.SELECT_ALTERNATE_ID);
                StringList slProducingFormula = domRM.getInfoList(context, "to[" + pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL + "].from.id");

                if (UIUtil.isNotNullAndNotEmpty(sRMPrevRevId)) {
                    DomainObject domPrevRevRM = DomainObject.newInstance(context, sRMPrevRevId);

                    StringList slPrevRevAlternateList = domPrevRevRM.getInfoList(context, pgV3Constants.SELECT_ALTERNATE_ID);
                    StringList slPrevRevProducingFormula = domPrevRevRM.getInfoList(context, "to[" + pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL + "].from.id");

                    if (RollupCommonUtil.isSnapshotDifference(slAlternateList, slPrevRevAlternateList) || RollupCommonUtil.isSnapshotDifference(slProducingFormula, slPrevRevProducingFormula)) {
                        isValidIntermediate = true;
                    }

                } else {
                    if (!slProducingFormula.isEmpty() || !slAlternateList.isEmpty()) {
                        isValidIntermediate = true;
                    }
                }

            } else {
                isValidIntermediate = true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return isValidIntermediate;
    }


}
