package com.pg.dsm.rollup_event.mark.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ParentExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.filter.FilterBattery;
import com.pg.dsm.rollup_event.mark.util.MarkAction;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class BatteryFlag {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    ProductPart markedProductPart;
    Resource resource;
    Properties rollupPageProperties;
    Context context;
    MarkAction markAction;
    Rollup rollupConfig;

    public BatteryFlag(ProductPart markedProductPart, Resource resource, MarkAction markAction) {
        this.markedProductPart = markedProductPart;
        this.resource = resource;
        this.rollupPageProperties = resource.getRollupPageProperties();
        this.context = resource.getContext();
        this.markAction = markAction;
        this.rollupConfig = RollupUtil.getRollup(RollupConstants.Basic.BATTERY_ROLLUP_EVENT_IDENTIFIER.getValue(), resource.getRollupRuleConfiguration());
    }

    public Map<String, Map<String, String>> getRollupEventAttributeMap() {
        Map<String, Map<String, String>> rollupEventAttributeMap = new HashMap<>();
        try {
            if (null != markedProductPart) {
                List<ProductPart> productParts = filterBattery();
                if (!productParts.isEmpty()) {
                    rollupEventAttributeMap.putAll(getRollupEventAttributeMap(productParts));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return rollupEventAttributeMap;
    }

    private Map<String, Map<String, String>> getRollupEventAttributeMap(List<ProductPart> productParts) {
        Map<String, Map<String, String>> rollupEventAttributeMap = new HashMap<>();
        try {
            StringBuilder sbEvent;
            String sEvents;

            Map<String, String> attributeMap = new HashMap<>();
            //Modified by DSM (Sogeti) for defect# 44589 - Starts
            attributeMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
            attributeMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_TRUE);
            //Modified by DSM (Sogeti) for defect# 44589 - Ends

            for (ProductPart productPart : productParts) {
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


    private List<ProductPart> filterBattery() {
        List<ProductPart> batteryProducts = new ArrayList<>();
        try {
            ProductPart productPart = getParentBOMExpansion();
            if (null != productPart && UIUtil.isNotNullAndNotEmpty(productPart.getId()) && productPart.isParentExist()) {
                FilterBattery filterBattery = new FilterBattery.Filter(context, rollupConfig).apply(productPart);
                if (filterBattery.isFiltered()) {
                    batteryProducts = filterBattery.getBatteryProducts();
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return batteryProducts;
    }

    private ProductPart getParentBOMExpansion() {
        ProductPart productPart = null;
        try {
            if (null != markedProductPart) {
                String productPartId = markedProductPart.getId();
                if (UIUtil.isNotNullAndNotEmpty(productPartId)) {
                    productPart = new ParentExpansion.Builder(context, markedProductPart.getId(), RollupConstants.Basic.EBOM_PARENT.getValue())
                            .setExpansionType(rollupConfig.getAllowedTypesInFPPTraversion())
                            .setExpansionRelationship(rollupConfig.getAllowedRelationships())
                            .setObjectSelectList(getBusSelects())
                            .setRelationshipSelectList(getRelSelects())
                            .setObjectWhereClause(DomainConstants.EMPTY_STRING)
                            .setMarkedProductPart(markedProductPart)
                            .setExpandLevel((short) Integer.parseInt(RollupConstants.Basic.ZERO.getValue()))
                            .build().getProductPart();
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return productPart;
    }

    private StringList getRelSelects() {
        StringList relSelectList = new StringList();
        relSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
        return relSelectList;
    }

    private StringList getBusSelects() {
        StringList busSelectList = new StringList(12);
        busSelectList.add(DomainConstants.SELECT_ID);
        busSelectList.add(DomainConstants.SELECT_TYPE);
        busSelectList.add(DomainConstants.SELECT_NAME);
        busSelectList.add(DomainConstants.SELECT_REVISION);
        busSelectList.add(DomainConstants.SELECT_CURRENT);
        busSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
        busSelectList.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        busSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISTHEPRODUCTABATTERY);
        busSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
        busSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDINSIDEDEVICE);
        busSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE);
        busSelectList.addElement(RollupConstants.Attribute.NUMBER_OF_BATTERIES_REQUIRED.getSelect(context));
        return busSelectList;
    }
}
