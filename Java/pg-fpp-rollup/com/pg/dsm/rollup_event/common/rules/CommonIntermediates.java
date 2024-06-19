package com.pg.dsm.rollup_event.common.rules;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;

public class CommonIntermediates implements RollupRule {
    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public CommonIntermediates(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        boolean isValid = false;
        String sAllowedRelationships = rollupConfig.getAllowedRelationships();
        String sProductFromRelationship = productPart.getRelationship();
        if (UIUtil.isNotNullAndNotEmpty(sAllowedRelationships) && UIUtil.isNotNullAndNotEmpty(sProductFromRelationship)
                && sAllowedRelationships.contains(sProductFromRelationship)) {
            isValid = true;
        }

        return isValid;
    }

    @Override
    public boolean isSubstituteAllowed() {
        return false;
    }
}
