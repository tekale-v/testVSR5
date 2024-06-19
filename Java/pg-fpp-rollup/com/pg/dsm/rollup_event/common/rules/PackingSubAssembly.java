package com.pg.dsm.rollup_event.common.rules;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;

public class PackingSubAssembly implements RollupRule {

    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public PackingSubAssembly(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        Rule rollupRulePSUB = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_PSUB.getValue());
        return Boolean.parseBoolean(rollupRulePSUB.getFlag());
    }

    @Override
    public boolean isSubstituteAllowed() {
        Rule rollupRulePSUB = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_PSUB.getValue());
        String substituteType = substitutePart.getType();

        return UIUtil.isNotNullAndNotEmpty(substituteType) && Boolean.parseBoolean(rollupRulePSUB.getFlag())
                && rollupRulePSUB.getSubstituteInclusionType().contains(substituteType);
    }
}
