package com.pg.dsm.rollup_event.mark.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ChildExpansion;
import com.pg.dsm.rollup_event.common.ebom.ParentExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.common.interfaces.RollupEventFactory;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.filter.FilterSetProduct;
import com.pg.dsm.rollup_event.mark.util.MarkUtil;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class MarketRegistrationFlag {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Resource resource;
    Properties rollupPageProperties;
    Context context;
    Rollup rollupConfig;
    StringList slProductsList;

    public MarketRegistrationFlag() {

    }

    public MarketRegistrationFlag(StringList slProductsList, Resource resource) {
        this.slProductsList = slProductsList;
        this.resource = resource;
        this.rollupPageProperties = resource.getRollupPageProperties();
        this.context = resource.getContext();
        this.rollupConfig = RollupUtil.getRollup(RollupConstants.Basic.MARKET_REGISTRATION_ROLLUP_EVENT_IDENTIFIER.getValue(), resource.getRollupRuleConfiguration());
    }

    public Map<String, Map<String, String>> getRollupEventAttributeMap() {
        Map<String, Map<String, String>> rollupEventAttributeMap = new HashMap<>();
        try {
            if (!slProductsList.isEmpty()) {
                List<ProductPart> setProductList = filterSetProduct();
                if (!setProductList.isEmpty()) {
                    rollupEventAttributeMap.putAll(getRollupEventAttributeMap(setProductList));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return rollupEventAttributeMap;
    }

    private Map<String, Map<String, String>> getRollupEventAttributeMap(List<ProductPart> setProductList) {
        Map<String, Map<String, String>> rollupEventAttributeMap = new HashMap<>();
        try {
            StringBuilder sbEvent;
            String sEvents;
            Map<String, String> attributeMap = new HashMap<>();
            //Modified by DSM (Sogeti) for defect# 44589 - Starts
            attributeMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
            attributeMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_TRUE);
            //Modified by DSM (Sogeti) for defect# 44589 - Ends

            for (ProductPart productPart : setProductList) {
                sbEvent = new StringBuilder();
                sEvents = productPart.getEventForRollup();
                sEvents = (sEvents == null) ? DomainConstants.EMPTY_STRING : sEvents;
                sbEvent.append(sEvents);
                if (!sEvents.contains(rollupConfig.getEventName())) {
                    if (sbEvent.length() > 0) {
                        sbEvent.append(pgV3Constants.SYMBOL_COMMA);
                    }
                    sbEvent.append(rollupConfig.getEventName());
                }
                attributeMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, sbEvent.toString());
                rollupEventAttributeMap.put(productPart.getId(), attributeMap);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return rollupEventAttributeMap;
    }

    private List<ProductPart> filterSetProduct() {
        List<ProductPart> setProductList = new ArrayList<>();
        try {
            int iProductsSize = slProductsList.size();
            ProductPart productPart;
            FilterSetProduct filterSetProduct;
            for (int i = 0; i < iProductsSize; i++) {
                productPart = getParentBOMExpansion(slProductsList.get(i));
                if (null != productPart && UIUtil.isNotNullAndNotEmpty(productPart.getId()) && productPart.isParentExist()) {
                    filterSetProduct = new FilterSetProduct.Filter(context, resource, rollupConfig).apply(productPart);
                    if (filterSetProduct.isFiltered()) {
                        setProductList = filterSetProduct.getSetProductList();
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return setProductList;
    }

    private ProductPart getParentBOMExpansion(String productPartId) {
        ProductPart productPart = null;
        try {
            if (UIUtil.isNotNullAndNotEmpty(productPartId)) {
                productPart = new ParentExpansion.Builder(context, productPartId, RollupConstants.Basic.EBOM_PARENT.getValue())
                        .setExpansionType(rollupConfig.getAllowedTypesInFPPTraversion())
                        .setExpansionRelationship(rollupConfig.getAllowedRelationships())
                        .setObjectSelectList(getBusSelects())
                        .setRelationshipSelectList(getRelSelects())
                        .setObjectWhereClause(DomainConstants.EMPTY_STRING)
                        .setMarkedProductPart(null)
                        .setExpandLevel((short) Integer.parseInt(RollupConstants.Basic.ZERO.getValue()))
                        .build().getProductPart();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return productPart;
    }


    private StringList getRelSelects() {
        StringList slRelSelects = new StringList(1);
        slRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID);
        return slRelSelects;
    }

    private StringList getBusSelects() {
        StringList busSelectList = new StringList(8);
        busSelectList.add(DomainConstants.SELECT_ID);
        busSelectList.add(DomainConstants.SELECT_TYPE);
        busSelectList.add(DomainConstants.SELECT_NAME);
        busSelectList.add(DomainConstants.SELECT_REVISION);
        busSelectList.add(DomainConstants.SELECT_CURRENT);
        busSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
        busSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        busSelectList.add(RollupConstants.Attribute.SET_PRODUCT_NAME.getSelect(context));
        return busSelectList;
    }

    /**
     * This method process market registration rollup for PAP/FAB/PSUB/FP
     *
     * @param context
     * @param markedProductPart
     * @param resource
     */
    public void processForMarketRegistrationRollup(Context context, ProductPart markedProductPart, Resource resource) {
        try {
            DomainObject domProduct = DomainObject.newInstance(context, markedProductPart.getId());
            StringList slSelectables = new StringList(2);
            slSelectables.addElement(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context));
            slSelectables.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP);
            Map<?, ?> mpProductMap = domProduct.getInfo(context, slSelectables);
            boolean bRegistrationFlag = mpProductMap.get(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context)) == null ? false : Boolean.parseBoolean((String) mpProductMap.get(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context)));
            boolean bMarkForRollup = mpProductMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP) == null ? false : Boolean.parseBoolean((String) mpProductMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP));
            if (bRegistrationFlag || bMarkForRollup) {
                Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
                Rollup marketRegistrationRollupConfig = RollupUtil.getRollup(RollupConstants.Basic.MARKET_REGISTRATION_ROLLUP_EVENT_IDENTIFIER.getValue(), resource.getRollupRuleConfiguration());
                //Modified by DSM (Sogeti) for defect# 44589 - Starts
                ChildExpansion childExpansion = new ChildExpansion.Builder(context, markedProductPart.getId(), RollupConstants.Basic.EBOM_CHILDREN.getValue())
                        .setExpansionType(rollupRuleConfiguration.getType())
                        .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                        .setExpandLevel((short) 0)
                        .build();
                if (childExpansion.isLoaded()) {
                    ProductPart productPart = childExpansion.getProductPart();
                    if (Boolean.parseBoolean(productPart.getMarketRegistrationRollupFlag()) || Boolean.parseBoolean(productPart.getMarkForRollupFlag())) {
                        RollupEvent rollupEvent = RollupEventFactory.getRollupEvent(productPart, marketRegistrationRollupConfig, marketRegistrationRollupConfig.getIdentifier(), resource);
                        if (rollupEvent != null) {
                            rollupEvent.execute();
                            domProduct.setAttributeValues(context, MarkUtil.getUnflagAttributeMap(markedProductPart));
                        }
                    }
                } else {
                    logger.log(Level.WARNING, RollupConstants.Basic.ERROR_LOADING_CHILD_EXPANSION_BEAN.getValue());
                }
            }
            //Modified by DSM (Sogeti) for defect# 44589 - Ends

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }
}
