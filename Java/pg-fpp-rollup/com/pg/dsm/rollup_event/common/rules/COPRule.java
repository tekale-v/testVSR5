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

public class COPRule implements RollupRule {

    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public COPRule(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        boolean bValidation = false;
        String sProductParentType = productPart.getParent().getType();
        if ((pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sProductParentType) ||
                pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sProductParentType)) &&
                pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(productPart.getType())) {
            Rule rollupRuleCOPinCOP = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_COP_IN_COP.getValue());
            bValidation = Boolean.parseBoolean(rollupRuleCOPinCOP.getFlag());
        } else if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sProductParentType) || pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sProductParentType)) {
            bValidation = true;
        } else if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductParentType)) {
            Rule rollupRuleProductPartChildren = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_CHILDPRODUCTPARTS_RULE.getValue());
            return Boolean.parseBoolean(rollupRuleProductPartChildren.getFlag());
        }
        return bValidation;
    }

    @Override
    public boolean isSubstituteAllowed() {
        boolean bValidation = false;
        String sAssemblyType = productPart.getAssemblyTypeDb();
        String sProductParentType = productPart.getParent().getType();
        String sProductType = productPart.getType();
        String sSubstituteType = substitutePart.getType();

        boolean bCOPBulkCheck = UIUtil.isNotNullAndNotEmpty(sAssemblyType) && pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sProductType) && pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(sAssemblyType);
        boolean bParentCheck = pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductParentType)
                || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sProductParentType);
        boolean bParentCOPCheck = pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sProductParentType);
        boolean bParentCUPCheck = pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sProductParentType);
        boolean bParentIPCheck = pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sProductParentType);
        boolean bParentPAPCheck = pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sProductParentType);


        Rule rollupRuleCOP = RollupUtil.getRollupRule(rollupConfig,
                RollupConstants.Basic.IDENTIFIER_COPS_SUBSTITUTE.getValue());
        boolean bAllowedTypeCheck = rollupRuleCOP.getInclusionType().contains(sSubstituteType);
        if (bCOPBulkCheck && bParentCheck) {

            Rule rollupRuleProductPartSubstitutes = RollupUtil.getRollupRule(rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_PRODUCT_PARTS_SUBSTITUTES.getValue());

            bValidation = Boolean.parseBoolean(rollupRuleProductPartSubstitutes.getFlag()) && bAllowedTypeCheck;

        } else {

            if (bParentCOPCheck || bParentCUPCheck || bParentIPCheck || bParentPAPCheck) {
                bValidation = Boolean.parseBoolean(rollupRuleCOP.getFlag()) && bAllowedTypeCheck;
            }
        }
        return bValidation;
    }
}
