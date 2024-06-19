package com.pg.dsm.rollup_event.common.rules;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class CUPRule implements RollupRule {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart productPart;
    Rollup rollupConfig;
    Substitute substitutePart;

    public CUPRule(Context context, ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.substitutePart = substitutePart;
        this.context = context; // Added by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
    }

    @Override
    public boolean isChildrenAllowed() {
        return true;
    }

    @Override
    public boolean isSubstituteAllowed() throws FrameworkException {
        boolean bValidation = false;
        if (null != substitutePart) {
            String sSubstituteType = substitutePart.getType();
            if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sSubstituteType)) {
                // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276 - Start
                boolean isSpecSubTypeReShipper = this.isSpecificationSubTypeReShipper();
                logger.log(Level.INFO, "Is CUP re-shipper {0}", isSpecSubTypeReShipper);
                if (isSpecSubTypeReShipper) {
                    Rule rollupRuleCUPReshipper = RollupUtil.getRollupRule(rollupConfig,
                            RollupConstants.Basic.IDENTIFIER_CUP_RESHIPPER.getValue());
                    bValidation = Boolean.parseBoolean(rollupRuleCUPReshipper.getFlag());
                } else {
                    bValidation = Boolean.TRUE;
                } // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276 - End
            }
        }
        return bValidation;
    }

    /**
     * Added by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
     *
     * @return
     * @throws FrameworkException
     */
    private boolean isSpecificationSubTypeReShipper() throws FrameworkException {
        boolean isReShipper = Boolean.FALSE;
        String substitutePartId = this.substitutePart.getId();
        if (UIUtil.isNotNullAndNotEmpty(substitutePartId)) {
            DomainObject domainObject = DomainObject.newInstance(this.context, substitutePartId);
            String specSubType = domainObject.getInfo(this.context, pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
            isReShipper = (UIUtil.isNotNullAndNotEmpty(specSubType) && pgV3Constants.RESHIPPER_ASS_TYPE_VAL.equalsIgnoreCase(specSubType)) ? Boolean.TRUE : Boolean.FALSE;
        }
        return isReShipper;
    }
}
