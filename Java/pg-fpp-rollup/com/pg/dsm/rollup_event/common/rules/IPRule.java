/*
    Added by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
 */

package com.pg.dsm.rollup_event.common.rules;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

public class IPRule implements RollupRule {
    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public IPRule(ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
    }

    @Override
    public boolean isChildrenAllowed() {
        boolean isAllowed = false;
        String sProductType = this.productPart.getType();
        if (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sProductType)) {
            Rule ruleIPinIP = RollupUtil.getRollupRule(this.rollupConfig,
                    RollupConstants.Basic.IDENTIFIER_IP_IN_IP.getValue());
            isAllowed = Boolean.parseBoolean(ruleIPinIP.getFlag());
        }
        return isAllowed;
    }

    @Override
    public boolean isSubstituteAllowed() throws FrameworkException {
        boolean isAllowed = Boolean.FALSE;
        String sSubstituteType = this.substitutePart.getType();
        Rule ipRule = RollupUtil.getRollupRule(this.rollupConfig,
                RollupConstants.Basic.IDENTIFIER_IP_SUBSTITUTE.getValue());
        boolean bAllowedTypeCheck = ipRule.getInclusionType().contains(sSubstituteType);
        if (bAllowedTypeCheck) {
            isAllowed = Boolean.TRUE;
        }
        return isAllowed;
    }
}
