package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
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

public class IngredientStatementRollupEvent implements RollupEvent {


    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart masterPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;

    public IngredientStatementRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
        this.masterPart = productPart;
        this.rollupConfig = rollupConfig;
        this.resource = resource;
        this.context = resource.getContext();
        rollupAction = new RollupAction(this.resource);
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
                Boolean bChildValidation;
                Boolean bIngredientPartCheck;
                Map mpProductMap = new HashMap();
                String sKeyAssemblyType = RollupConstants.Attribute.MOS_ROLLED_UP_ASSEMBLY_TYPE.getSelect(context);
                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();

                    RollupRule rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    bChildValidation = rollupRule.isChildrenAllowed();
                    // Add sub/alt/
                    if (bChildValidation) {
                        bIngredientPartCheck = performIngredientPartCheck(childrenProduct);

                        rollupAction.processAlternates(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processProducedBy(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);

                        if (bIngredientPartCheck) {
                            mpProductMap.put(pgV3Constants.PHYSICALID, childrenProduct.getPhysicalId());
                            mpProductMap.put(sKeyAssemblyType, childrenProduct.getBomType());
                            mpProductMap.put(DomainConstants.SELECT_TYPE, childrenProduct.getType());
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

    public boolean performIngredientPartCheck(ProductPart productpart) {
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
                    //Added by DSM(Sogeti) for 2018x.6 Req #37624 - Starts
                    Rule childProductPartRule = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());

                    MapList mlCopyListData = rollupAction.getConnectedRollUpData(domProductPart,
                            childProductPartRule.getToType(), childProductPartRule.getRelationshipName(),
                            childProductPartRule.getToType(), RollupUtil.getObjectSelects(), null,
                            resource.getExecutionType());

                    //Added by DSM(Sogeti) for 2018x.6 Req #37624 - Ends
                    StringList slIngredientList = rollupAction.getRollUpObjectList(mlCopyListData);


                    if (!slIngredientList.isEmpty()) {
                        /*
                         * In case of manual rollup, Rollup initiator may not have access to all the
                         * data which needs to be rolledup. So pushing context to User Agent to avoid
                         * data missing in rollup
                         */
                        Rule rollupRelationshipRule = RollupUtil.getRollupRule(rollupConfig,
                                RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                        RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRelationshipRule.getRelationshipName());
                        String[] rollupObjects = slIngredientList.toArray(new String[slIngredientList.size()]);
                        // modify isFrom from config
                        Map mRelConnectedProduct = DomainRelationship.connect(context, domMaster, rFPPtoRollUpRel, true,
                                rollupObjects, true);

                        resource.getDynamicSubscription().add(rollupConfig);
                        String sConnectionId;
                        Map.Entry mapRelId;
                        Iterator itr = mRelConnectedProduct.entrySet().iterator();
                        while (itr.hasNext()) {
                            mapRelId = (Map.Entry) itr.next();
                            sConnectionId = mapRelId.getValue().toString();
                            DomainRelationship.setAttributeValue(context, sConnectionId, pgV3Constants.ATTRIBUTE_PGPRODUCTPARTPHYSICALID, sPhysicalId);
                        }
                    }
                    resource.getSlProcessedObjectsList().add(sPhysicalId);
                }

            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }


}
