package com.pg.dsm.rollup_event.mark.filter;

import java.util.ArrayList;
import java.util.List;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class FilterBattery {
    boolean filtered;
    List<ProductPart> batteryProducts;

    private FilterBattery(Filter filter) {
        this.filtered = filter.filtered;
        this.batteryProducts = filter.batteryProducts;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public List<ProductPart> getBatteryProducts() {
        return batteryProducts;
    }

    public static class Filter {
        Context context;
        List<ProductPart> batteryProducts;
        boolean filtered;
        Rollup rollupConfig;

        public Filter(Context context, Rollup rollupConfig) {
            this.context = context;
            this.rollupConfig = rollupConfig;
            batteryProducts = new ArrayList<>();
        }

        public FilterBattery apply(ProductPart productPart) {
            applyFilter(productPart);
            this.filtered = true;
            return new FilterBattery(this);
        }

        private void applyFilter(ProductPart productPart) {
            if (productPart.isParentExist()) {
                List<ProductPart> parents = productPart.getParents();
                String sFPPEvents;
                String sAssemblyType;
                Rule rollupRuleShippableHALB = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_SHIPPABLE_HALB.getValue());
                boolean bFPPStateCheck;
                boolean bShippableHALBCheck;
                for (ProductPart parent : parents) {
                    sAssemblyType = parent.getAssemblyType();
                    bFPPStateCheck = pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(parent.getType())
                            && pgV3Constants.STATE_RELEASE.equalsIgnoreCase(parent.getCurrentState());
                    bShippableHALBCheck = (!pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)
                            || (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)
                            && Boolean.parseBoolean(rollupRuleShippableHALB.getFlag())));
                    if (bFPPStateCheck && bShippableHALBCheck) {
                        sFPPEvents = parent.getEventForRollup();
                        if (!sFPPEvents.contains(rollupConfig.getEventName())) {
                            batteryProducts.add(parent);
                        }
                        applyFilter(parent);
                    } else {
                        if (!isBatteryProduct(parent)) {
                            applyFilter(parent);
                        }
                    }
                }
            }
        }

        public boolean isBatteryProduct(ProductPart productPart) {
            boolean bIsBattery = false;
            String type = productPart.getType();
            if (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(type) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(type)) {
                String sIsBatteryType = productPart.getIsTheProductABattery();
                if (UIUtil.isNotNullAndNotEmpty(sIsBatteryType) && pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sIsBatteryType)) {
                    bIsBattery = true;
                }
                // attribute to identify battery -> Is Battery
                // check specific attribute value of RM to identify if it's battery
                // if battery condition is satisfied then set the variable bIsBattery = true
            }
            if (pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(type) && rollupConfig.allowedRanges.contains(productPart.getPowerSourceDb())) {
                bIsBattery = true;
                // attribute to identify battery -> Power Source
                // check specific attribute value of DPP to identify if it's battery
                // if battery condition is satisfied then set the variable bIsBattery = true
            }
            return bIsBattery;
        }
    }
}
