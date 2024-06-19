package com.pg.dsm.rollup_event.common.rules;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

public class PAPRule implements RollupRule {

    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public PAPRule(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        Boolean bValidation = false;
        String sProductParentType = productPart.isParentExist() ? productPart.getParent().getType() : DomainConstants.EMPTY_STRING;
        Rule rollupRuleProductPartChildren = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_CHILDPRODUCTPARTS_RULE.getValue());
        Rule rollupRulePAP = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_PAP.getValue());
        if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sProductParentType)) {
            bValidation = Boolean.parseBoolean(rollupRulePAP.getFlag())
                    && Boolean.parseBoolean(rollupRuleProductPartChildren.getFlag());
        } else {
            bValidation = Boolean.parseBoolean(rollupRulePAP.getFlag());
        }

        return bValidation;
    }

    @Override
    public boolean isSubstituteAllowed() {
        Boolean bValidation = false;
        String sSubstituteType = substitutePart.getType();
        String sProductParentType = productPart.getParent().getType();

        Rule rollupRulePAP = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_PAP.getValue());

        Rule rollupRuleProductPartSubstitutes = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES.getValue());

        boolean bParentCheck = pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sProductParentType);

        if (bParentCheck) {

            bValidation = Boolean.parseBoolean(rollupRuleProductPartSubstitutes.getFlag())
                    && Boolean.parseBoolean(rollupRulePAP.getSubstituteAllowed())
                    && rollupRulePAP.getSubstituteInclusionType().contains(sSubstituteType);
        } else {

            bValidation = Boolean.parseBoolean(rollupRulePAP.getSubstituteAllowed()) && rollupRulePAP.getSubstituteInclusionType().contains(sSubstituteType);
        }
        return bValidation;
    }
}
