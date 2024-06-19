package com.pg.dsm.rollup_event.common.interfaces;

import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.ebom.Substitute;
import com.pg.dsm.rollup_event.common.rules.COPRule;
import com.pg.dsm.rollup_event.common.rules.CUPRule;
import com.pg.dsm.rollup_event.common.rules.CommonIntermediates;
import com.pg.dsm.rollup_event.common.rules.FABRule;
import com.pg.dsm.rollup_event.common.rules.FPPRule;
import com.pg.dsm.rollup_event.common.rules.FinishedProduct;
import com.pg.dsm.rollup_event.common.rules.IPRule;
import com.pg.dsm.rollup_event.common.rules.PAPRule;
import com.pg.dsm.rollup_event.common.rules.PackingSubAssembly;
import com.pg.dsm.rollup_event.common.rules.ProducedByRule;
import com.pg.dsm.rollup_event.common.rules.ProductParts;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class RollupRuleFactory {

    public static RollupRule getRollupRule(Context context, ProductPart productPart, Substitute substitutePart, Rollup rollupConfig) {
        String sProductType = productPart.getType();
        RollupRule rollupRule = null;

        if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sProductType)) {
            rollupRule = new CUPRule(context, productPart, substitutePart, rollupConfig);

        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sProductType)) {

            rollupRule = new COPRule(productPart, substitutePart, rollupConfig);

        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sProductType)) {

            rollupRule = new PAPRule(productPart, substitutePart, rollupConfig);

        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sProductType)) {

            rollupRule = new FABRule(productPart, substitutePart, rollupConfig);
        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sProductType)) {

            rollupRule = new FPPRule(productPart, substitutePart, rollupConfig);
        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(sProductType)
                || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(sProductType))) {

            rollupRule = new ProducedByRule(productPart, substitutePart, rollupConfig);
        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sProductType)
                || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductType)
                || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sProductType)
                || pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(sProductType))) {

            rollupRule = new ProductParts(productPart, substitutePart, rollupConfig);

        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sProductType)) {

            rollupRule = new FinishedProduct(productPart, substitutePart, rollupConfig);
        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sProductType)) {

            rollupRule = new PackingSubAssembly(productPart, substitutePart, rollupConfig);
        } else if (UIUtil.isNotNullAndNotEmpty(sProductType)
                && pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sProductType)) { // Added by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276 - Start
            rollupRule = new IPRule(productPart, substitutePart, rollupConfig);
        }
        else {
            rollupRule = new CommonIntermediates(productPart, substitutePart, rollupConfig);
        }
        return rollupRule;

    }
}
