package com.pg.dsm.rollup_event.mark.filter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ChildExpansion;
import com.pg.dsm.rollup_event.common.ebom.PrimaryCOPExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.models.MarketRegistrationFlag;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class FilterSetProduct {
    boolean filtered;
    List<ProductPart> setProductList;

    private FilterSetProduct(Filter filter) {
        this.filtered = filter.filtered;
        this.setProductList = filter.setProductList;
    }

    public static void unFlagRegistration(Context context, String productId) throws FrameworkException {
        DomainObject domProduct = DomainObject.newInstance(context, productId);
        Map mpAttributes = new HashMap();
        mpAttributes.put(pgV3Constants.ATTRIBUTE_PGREGISTRATIONROLLUPFLAG, pgV3Constants.KEY_FALSE);
        domProduct.setAttributeValues(context, mpAttributes);
    }

    public boolean isFiltered() {
        return filtered;
    }

    public List<ProductPart> getSetProductList() {
        return setProductList;
    }

    public static class Filter {
        Context context;
        List<ProductPart> setProductList;
        boolean filtered;
        Resource resource;
        Rollup rollupConfig;

        public Filter(Context context, Resource resource, Rollup rollupConfig) {
            this.context = context;
            this.resource = resource;
            this.rollupConfig = rollupConfig;
        }

        public FilterSetProduct apply(ProductPart productPart) throws FrameworkException {
            setProductList = new ArrayList<>();
            applyFilter(productPart);
            filtered = true;
            return new FilterSetProduct(this);
        }

        public void applyFilter(ProductPart productPart) throws FrameworkException {
            if (productPart.isParentExist()) {
                List<ProductPart> parents = productPart.getParents();
                String sParentType;
                String sParentState;
                if (!parents.isEmpty()) {
                    String sFPPEvents;
                    String sAssemblyType;
                    Rule rollupRuleShippableHALB = RollupUtil.getRollupRule(rollupConfig,
                            RollupConstants.Basic.IDENTIFIER_SHIPPABLE_HALB.getValue());
                    boolean bFPPStateCheck;
                    boolean bShippableHALBCheck;
                    MarketRegistrationFlag marketRegistration;
                    for (ProductPart parent : parents) {
                        sParentType = parent.getType();
                        sParentState = parent.getCurrentState();
                        sAssemblyType = parent.getAssemblyType();
                        bFPPStateCheck = pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(parent.getType())
                                && pgV3Constants.STATE_RELEASE.equalsIgnoreCase(parent.getCurrentState());
                        bShippableHALBCheck = (!pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)
                                || (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)
                                && Boolean.parseBoolean(rollupRuleShippableHALB.getFlag())));
                        if (bFPPStateCheck && bShippableHALBCheck) {
                            if (!isSetProduct(parent)) {
                                sFPPEvents = parent.getEventForRollup();
                                if (!sFPPEvents.contains(rollupConfig.getEventName())) {
                                    setProductList.add(parent);
                                }
                                applyFilter(parent);
                            }
                        } else {
                            if (!(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sParentType)
                                    && pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(parent.getSetProductName()))) {
                                applyFilter(parent);
                            }
                        }
                    }
                }
            }
        }

        public boolean isSetProduct(ProductPart productPart) {
            boolean bIsSetProduct = false;
            Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
            String sEBOMKey = RollupConstants.Basic.EBOM_CHILDREN.getValue();
            ChildExpansion childExpansion = new ChildExpansion.Builder(context, productPart.getId(), sEBOMKey)
                    .setExpansionType(rollupRuleConfiguration.getType())
                    .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                    .setExpandLevel((short) 3)
                    .build();
            if (childExpansion.isLoaded()) {
                ProductPart productFPP = childExpansion.getProductPart();
                if (productFPP != null) {
                    PrimaryCOPExpansion filter = new PrimaryCOPExpansion();
                    ProductPart primaryCOP = filter.getTopLevelCOP(productFPP);
                    if (primaryCOP != null) {
                        if (pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(primaryCOP.getSetProductNameDb())) {
                            bIsSetProduct = true;
                        } else {
                            bIsSetProduct = performSubstituteSetCOPValidation(productFPP);
                        }
                    }
                }
            }
            return bIsSetProduct;
        }

        public boolean performSubstituteSetCOPValidation(ProductPart productPart) {
            boolean bValidation = false;
            String sKeySubstitute = RollupConstants.Basic.KEY_SUBSTITUTE.getValue();
            if (productPart != null) {
                if (productPart.isChildExist()) {
                    List<ProductPart> slChildrenProduct = productPart.getChildren();
                    ChildExpansion childExpansion;
                    ProductPart substituteProductPart;
                    ProductPart reshipperCOPProduct;
                    String sSetProduct;
                    PrimaryCOPExpansion filter;
                    Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
                    List<Substitute> slSubstitutes;
                    for (ProductPart childrenPart : slChildrenProduct) {
                        slSubstitutes = childrenPart.getSubstitutes();
                        if (slSubstitutes != null) {
                            for (Substitute substitute : slSubstitutes) {
                                if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(substitute.getType())) {
                                    childExpansion = new ChildExpansion.Builder(context, substitute.getId(), sKeySubstitute)
                                            .setExpansionType(rollupRuleConfiguration.getType())
                                            .setExpansionRelationship(rollupRuleConfiguration.getRelationship())
                                            .setExpandLevel((short) 3)
                                            .build();
                                    if (childExpansion.isLoaded()) {
                                        substituteProductPart = childExpansion.getProductPart();
                                        filter = new PrimaryCOPExpansion();
                                        reshipperCOPProduct = filter.getTopLevelCOP(substituteProductPart);

                                        if (reshipperCOPProduct != null) {
                                            sSetProduct = reshipperCOPProduct.getSetProductNameDb();
                                            if (pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sSetProduct)) {
                                                bValidation = true;
                                            }
                                        }
                                    }
                                } else {
                                    bValidation = false;
                                }
                            }
                        }
                    }
                }
            }
            return bValidation;
        }
    }

}
