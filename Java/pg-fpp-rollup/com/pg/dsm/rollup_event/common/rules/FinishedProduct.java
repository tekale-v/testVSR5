package com.pg.dsm.rollup_event.common.rules;

import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

public class FinishedProduct implements RollupRule {
    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public FinishedProduct(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        boolean bValidation = false;
        Rule rollupFinishedProductRule = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCT.getValue());
        if (Boolean.parseBoolean(rollupFinishedProductRule.getFlag())) {
            Rule rollupHALBRule = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_SHIPPABLE_HALB.getValue());
            String sAssemblyType = productPart.getAssemblyType();
            if (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)) {
                bValidation = Boolean.parseBoolean(rollupHALBRule.getFlag());
            } else {
                bValidation = true;
            }
        }


        return bValidation;
    }

    @Override
    public boolean isSubstituteAllowed() {
        Rule rollupFinishedProductRule = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCT.getValue());
        return Boolean.parseBoolean(rollupFinishedProductRule.getSubstituteAllowed());
    }
}
