package com.pg.dsm.rollup_event.common.interfaces;

import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.models.BatteryRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.DGCRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.GHSRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.IngredientStatementRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.MarketRegistrationRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.MaterialCertificationRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.PhysChemRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.SmartLabelRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.StabilityDocumentRollupEvent;
import com.pg.dsm.rollup_event.rollup.models.WarehouseClassificationRollupEvent;
import com.pg.dsm.rollup_event.rollup.services.Resource;

public class RollupEventFactory {
    public static RollupEvent getRollupEvent(ProductPart productPart, Rollup rollupConfig, String rollupEventIdentifier, Resource resource) {
        RollupEvent rollupEvent = null;

        if (RollupConstants.Basic.BATTERY_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new BatteryRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.DGC_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new DGCRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.MARKET_REGISTRATION_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new MarketRegistrationRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.INGREDIENT_STATEMENT_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new IngredientStatementRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.SMART_LABEL_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new SmartLabelRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.STABILITY_RESULTS_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new StabilityDocumentRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.GHS_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new GHSRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.WAREHOUSE_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new WarehouseClassificationRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.MATERIAL_CERTIFICATION_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new MaterialCertificationRollupEvent(productPart, rollupConfig, resource);
        }

        if (RollupConstants.Basic.PHYS_CHEM_ROLLUP_EVENT_IDENTIFIER.getValue().equals(rollupConfig.getIdentifier())) {
            rollupEvent = new PhysChemRollupEvent(productPart, rollupConfig, resource);
        }


        return rollupEvent;
    }
}
