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

public class ProducedByRule implements RollupRule {
    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public ProducedByRule(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
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
        Rule rollupRuleProducedBy = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_PRODUCEDBY.getValue());
        String sAllowedRelationships = rollupConfig.getAllowedRelationships();
        String sProductFromRelationship = productPart.getRelationship();
        boolean isFromValidRel = UIUtil.isNotNullAndNotEmpty(sAllowedRelationships) && UIUtil.isNotNullAndNotEmpty(sProductFromRelationship)
                && sAllowedRelationships.contains(sProductFromRelationship);
        if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sProductParentType)) {
            bValidation = Boolean.parseBoolean(rollupRuleProducedBy.getFlag())
                    && Boolean.parseBoolean(rollupRuleProductPartChildren.getFlag()) && isFromValidRel;
        } else {
            bValidation = Boolean.parseBoolean(rollupRuleProducedBy.getFlag()) && isFromValidRel;
        }

        return bValidation;
    }

    @Override
    public boolean isSubstituteAllowed() {

        Rule rollupRuleProductPartSubstitutes = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES.getValue());
        Rule rollupRuleProducedBy = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_PRODUCEDBY.getValue());
        return Boolean.parseBoolean(rollupRuleProductPartSubstitutes.getFlag())
                && Boolean.parseBoolean(rollupRuleProducedBy.getFlag());
    }


}
