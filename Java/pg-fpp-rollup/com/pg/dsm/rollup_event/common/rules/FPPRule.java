package com.pg.dsm.rollup_event.common.rules;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

public class FPPRule implements RollupRule {

    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public FPPRule(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        boolean bValidate = false;
        Rule rollupRuleFPPinFPP = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_FPP_IN_FPP.getValue());
        Rule rollupRuleShippableHALB = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_SHIPPABLE_HALB.getValue());
        String sAssemblyType = productPart.getAssemblyType();
        if (UIUtil.isNotNullAndNotEmpty(sAssemblyType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sAssemblyType)) {
            bValidate = Boolean.parseBoolean(rollupRuleFPPinFPP.getFlag()) && Boolean.parseBoolean(rollupRuleShippableHALB.getFlag());
        } else {
            bValidate = Boolean.parseBoolean(rollupRuleFPPinFPP.getFlag());
        }

        return bValidate;
    }

    @Override
    public boolean isSubstituteAllowed() {
        Rule rollupRuleFPPinFPP = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_FPP_IN_FPP.getValue());
        return Boolean.parseBoolean(rollupRuleFPPinFPP.getSubstituteAllowed());

    }

}
