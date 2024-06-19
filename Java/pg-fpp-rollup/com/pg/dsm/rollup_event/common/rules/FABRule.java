package com.pg.dsm.rollup_event.common.rules;

import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

public class FABRule implements RollupRule {

    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public FABRule(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        boolean bValidation = false;
        String sProductParentType = productPart.getParent().getType();
        Rule rollupRuleProductPartChildren = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_CHILDPRODUCTPARTS_RULE.getValue());
        Rule rollupRuleFAB = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_FAB.getValue());
        if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sProductParentType)) {
            bValidation = Boolean.parseBoolean(rollupRuleFAB.getFlag())
                    && Boolean.parseBoolean(rollupRuleProductPartChildren.getFlag());
        } else {
            bValidation = Boolean.parseBoolean(rollupRuleFAB.getFlag());
        }

        return bValidation;
    }

    @Override
    public boolean isSubstituteAllowed() {
        Rule rollupRuleFAB = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_FAB.getValue());
        return Boolean.parseBoolean(rollupRuleFAB.getSubstituteAllowed());
    }

}
