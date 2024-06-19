package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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


public class GHSRollupEvent implements RollupEvent {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart masterPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;
    Properties rollupPageProperties;

    public GHSRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
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
                rollupAction.updateCorrosiveOnFPP(masterPart.getId());
                if (isModified) {
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
                boolean bGHSPartCheck;
                Map mpProductMap = new HashMap();
                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();

                    RollupRule rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    bChildValidation = rollupRule.isChildrenAllowed();

                    if (bChildValidation) {
                        bGHSPartCheck = performGHSPartCheck(childrenProduct);

                        rollupAction.processAlternates(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processProducedBy(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);

                        if (bGHSPartCheck) {
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

    public boolean performGHSPartCheck(ProductPart productpart) {
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
    public void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap)
            throws FrameworkException {
        try {
            if (masterPart != null && !mpProductMap.isEmpty()) {
                DomainObject domMaster = DomainObject.newInstance(context, masterPart.getId());
                String sPhysicalId = mpProductMap.get(pgV3Constants.PHYSICALID);

                if (!resource.getSlProcessedObjectsList().contains(sPhysicalId)) {
                    DomainObject domProductPart = DomainObject.newInstance(context, sPhysicalId);

                    Rule childProductPartRule = RollupUtil.getRollupRule(rollupConfig,
                            RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());

                    /*
                     * In case of manual rollup, Rollup initiator may not have access to all the
                     * data which needs to be rolledup. So pushing context to User Agent to avoid
                     * data missing in rollup
                     */
                    MapList mlGHSData = rollupAction.getConnectedRollUpData(domProductPart, childProductPartRule.getToType(), childProductPartRule.getRelationshipName(), null, RollupUtil.getObjectSelects(), null, resource.getExecutionType());

                    Rule rollupRelationshipRule = RollupUtil.getRollupRule(rollupConfig,
                            RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                    RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRelationshipRule.getRelationshipName());
                    //Modified for Defect# 44593 - Starts
                    // Relationship pgRolledUpGHS restricts duplicate connections. Added try/catch block to handle exception in case of duplicate connections
                    Map<?, ?> mpGHSData;
                    StringList slCopyListIds;
                    Map<?, ?> mRelConnectedProduct;
                    String sConnectionId;
                    Entry<?, ?> mapRelId;
                    Iterator<?> itr;
                    for (Object object : mlGHSData) {
                        mpGHSData = (Map<?, ?>) object;
                        slCopyListIds = rollupAction.getRollupObjectId(mpGHSData);
                        try {
                            String[] rollupObjects = slCopyListIds.toArray(new String[slCopyListIds.size()]);
                            // modify isFrom from config
                            mRelConnectedProduct = DomainRelationship.connect(context, domMaster, rFPPtoRollUpRel, true,
                                    rollupObjects, true);

                            resource.getDynamicSubscription().add(rollupConfig);

                            itr = mRelConnectedProduct.entrySet().iterator();
                            while (itr.hasNext()) {
                                mapRelId = (Entry<?, ?>) itr.next();
                                sConnectionId = mapRelId.getValue().toString();
                                DomainRelationship.setAttributeValue(context, sConnectionId, pgV3Constants.ATTRIBUTE_PGPRODUCTPARTPHYSICALID, sPhysicalId);
                            }
                        } catch (FrameworkException e) {
							/* Commented to reduce logger size
                    		 logger.log(Level.INFO, "Avoided duplicate GHS Rollup connection from Object: {0}", sPhysicalId);
							 */
                        }
                        //Modified for Defect# 44593 - Ends
                    }
                    resource.getSlProcessedObjectsList().add(sPhysicalId);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }
}
