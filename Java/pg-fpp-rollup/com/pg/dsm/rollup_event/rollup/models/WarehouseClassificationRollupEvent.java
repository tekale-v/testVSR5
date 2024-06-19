package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
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
import matrix.db.RelationshipType;
import matrix.util.StringList;

public class WarehouseClassificationRollupEvent implements RollupEvent {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart masterPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;
    Properties rollupPageProperties;

    public WarehouseClassificationRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
        this.masterPart = productPart;
        this.rollupConfig = rollupConfig;
        this.resource = resource;
        this.context = resource.getContext();
        rollupAction = new RollupAction(this.resource);
        rollupPageProperties = resource.getRollupPageProperties();
    }

    @Override
    public void execute() {
        if (masterPart != null) {
            try {
                Boolean isModified = rollupAction.disconnectExistingRollupData(masterPart, rollupConfig);
                processProductParts(masterPart);
                if (isModified) {
                    resource.getDynamicSubscription().add(rollupConfig);
                }
                rollupAction.updateCorrosiveOnFPP(masterPart.getId());
                rollupAction.updateNoSpecialStorageRequiredOnFPP(masterPart.getId());
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
                boolean bWarehouseClassificationPartCheck;
                Map mpProductMap = new HashMap();
                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();

                    RollupRule rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    bChildValidation = rollupRule.isChildrenAllowed();

                    if (bChildValidation) {
                        bWarehouseClassificationPartCheck = performWarehouseClassificationPartCheck(childrenProduct);

                        rollupAction.processAlternates(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processProducedBy(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);

                        if (bWarehouseClassificationPartCheck) {
                            mpProductMap.put(pgV3Constants.PHYSICALID, childrenProduct.getPhysicalId());
                            performRollupConnections(masterPart, mpProductMap);
                        }
                        processProductParts(childrenProduct);
                    }
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    public boolean performWarehouseClassificationPartCheck(ProductPart productpart) {
        boolean iValidation = false;
        try {
            String sProductType = productpart.getType();
            Rule rollupRuleChildProductParts = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
            String sProductParts = rollupRuleChildProductParts.getInclusionType();
            iValidation = (UIUtil.isNotNullAndNotEmpty(sProductParts) && sProductParts.contains(sProductType));

            //To Be Discussed with Santosh
            if (iValidation && pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductType) && !RollupConstants.Basic.VALUE_YES.getValue().equalsIgnoreCase(productpart.getwHCDoesDeviceContainsFlammableLiquid())) {

                iValidation = false;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return iValidation;
    }

    @Override
    public void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap)
            throws FrameworkException {
        try {
            if (masterPart != null && !mpProductMap.isEmpty()) {
                DomainObject domMaster = DomainObject.newInstance(context, masterPart.getId());
                String sPhysicalId = mpProductMap.get(pgV3Constants.PHYSICALID);
                DomainObject domProductPart = DomainObject.newInstance(context, sPhysicalId);

                Rule childProductPartRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());

                MapList mlWarehouseClassificationData = rollupAction.getConnectedRollUpData(domProductPart, childProductPartRule.getToType(), childProductPartRule.getRelationshipName(), null, RollupUtil.getObjectSelects(), null, resource.getExecutionType());
                StringList slWarehouseClassificationIDs = rollupAction.getRollUpObjectList(mlWarehouseClassificationData);

                if (!slWarehouseClassificationIDs.isEmpty()) {

                    /*
                     * In case of manual rollup, Rollup initiator may not have access to all the
                     * data which needs to be rolledup. So pushing context to User Agent to avoid
                     * data missing in rollup
                     */
                    Rule rollupRelationshipRule = RollupUtil.getRollupRule(rollupConfig,
                            RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                    RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRelationshipRule.getRelationshipName());
                    int iRollupDataSize = slWarehouseClassificationIDs.size();

                    for (int i = 0; i < iRollupDataSize; i++) {
                        String[] rollupObjects = {slWarehouseClassificationIDs.get(i)};
                        try {
                            DomainRelationship.connect(context, domMaster, rFPPtoRollUpRel, true, rollupObjects, true);
                            resource.getDynamicSubscription().add(rollupConfig);
                        } catch (Exception e) {
							/* Commented to reduce logger size
                        	 logger.log(Level.INFO, "Avoided duplicate Warehouse Rollup connection from Object: {0}", sPhysicalId);
							 */
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }
}
