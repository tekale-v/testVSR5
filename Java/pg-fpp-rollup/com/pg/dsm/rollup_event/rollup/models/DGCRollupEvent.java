package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.common.interfaces.RollupRuleFactory;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupAction;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;


public class DGCRollupEvent implements RollupEvent {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;
    ProductPart masterPart;
    Rollup rollupConfig;
    RollupAction rollupAction;
    Resource resource;

    public DGCRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
        this.masterPart = productPart;
        this.rollupConfig = rollupConfig;
        this.context = resource.getContext();
        rollupAction = new RollupAction(resource);
        this.resource = resource;
    }

    @Override
    public void execute() {
        if (masterPart != null) {
            try {
                processProductParts(masterPart);

                Map<String, Set<String>> mpFPPRolledupData = rollupAction.getExistingFPPRollupData(masterPart, rollupConfig);

                Map<String, Set<String>> mpProductPartData = rollupAction.getProductPartRollupData(masterPart.getChildrenList(), rollupConfig);

                Boolean performRecalculation = rollupAction.performSnapshotCheck(mpFPPRolledupData, mpProductPartData, masterPart);

                if (performRecalculation) {
                    rollupAction.disconnectExistingRollupData(masterPart, rollupConfig);
                    rollupAction.performRollupRecalculation(masterPart, rollupConfig, mpProductPartData);
                    resource.getDynamicSubscription().add(rollupConfig);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
        }

    }

    @Override
    public void processProductParts(ProductPart productPart) {
        try {
            if (productPart.isChildExist()) {
                List<ProductPart> children = productPart.getChildren();
                Iterator<ProductPart> childrenItr = children.iterator();
                ProductPart childrenProduct;
                boolean bChildValidation;
                boolean bDGCPartCheck;
                Map mpProductMap = new HashMap();
                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();

                    RollupRule rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    bChildValidation = rollupRule.isChildrenAllowed();

                    if (bChildValidation) {
                        bDGCPartCheck = performDGCPartCheck(childrenProduct);

                        rollupAction.processAlternates(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processProducedBy(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);

                        if (bDGCPartCheck) {
                            mpProductMap.put(pgV3Constants.PHYSICALID, childrenProduct.getPhysicalId());
                            performRollupConnections(masterPart, mpProductMap);
                        } else {
                            processProductParts(childrenProduct);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    public boolean performDGCPartCheck(ProductPart productpart) {
        Boolean iValidation = false;
        try {
            String sProductType = productpart.getType();
            Rule rollupRuleChildProductParts = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
            String sProductParts = rollupRuleChildProductParts.getInclusionType();
            iValidation = (UIUtil.isNotNullAndNotEmpty(sProductParts) && sProductParts.contains(sProductType));
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return iValidation;
    }


    @Override
    public void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap) throws FrameworkException {
        try {
            if (masterPart != null && !mpProductMap.isEmpty()) {
                String sPhysicalId = mpProductMap.get(pgV3Constants.PHYSICALID);
                if (UIUtil.isNotNullAndNotEmpty(sPhysicalId) && !masterPart.getChildrenList().contains(sPhysicalId)) {
                    masterPart.getChildrenList().addElement(sPhysicalId);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }
}
