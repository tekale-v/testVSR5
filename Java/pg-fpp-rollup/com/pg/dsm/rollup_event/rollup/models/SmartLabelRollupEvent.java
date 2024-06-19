package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.StringList;

public class SmartLabelRollupEvent implements RollupEvent {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart masterPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;

    public SmartLabelRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
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
                boolean bChildValidation;
                boolean bSmartLabelPartCheck;
                Map mpProductMap = new HashMap();

                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();

                    RollupRule rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    bChildValidation = rollupRule.isChildrenAllowed();

                    if (bChildValidation) {
                        bSmartLabelPartCheck = performSmartLabelPartCheck(childrenProduct);

                        rollupAction.processAlternates(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processProducedBy(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);

                        if (bSmartLabelPartCheck) {
                            mpProductMap.put(pgV3Constants.PHYSICALID, childrenProduct.getPhysicalId());
                            performRollupConnections(masterPart, mpProductMap);
							// Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - Start
							masterPart.setPublishITToSAP(true);
							// Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - End
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

    /**
     * 
     * @param productpart
     * @return
     */
    public boolean performSmartLabelPartCheck(ProductPart productpart) {
        Boolean iValidation = false;
        boolean isValidProductPart=false;
        try {
            String sProductType = productpart.getType();
            //Modified by DSM for 22x CW-05 for Defect 57224 -Start
            String sProductId = productpart.getId();
            isValidProductPart = isPickListConnectedToSLRowInClculation(this.context,sProductId);
            logger.log(Level.INFO, "isValidProductPart: {0}", isValidProductPart);
            //Change the rule to "Product Parts - Smart Label" to get inclusion types and updated inclusion types in in config file in rule.
            Rule rollupRuleChildProductParts = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
            String sProductParts = rollupRuleChildProductParts.getInclusionType();
            if(isValidProductPart) {
            iValidation = (UIUtil.isNotNullAndNotEmpty(sProductParts) && sProductParts.contains(sProductType));
            }
            //Modified by DSM for 22x CW-05 for Defect 57224 -End
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        logger.log(Level.INFO, "iValidation: {0}", iValidation);
        return iValidation;
    }

    /**
     * Added by DSM for 22x CW-05 for Defect 57224
     * @param context
     * @return
     * @throws FrameworkException
     */
    boolean isPickListConnectedToSLRowInClculation(Context context, String sProductId) throws FrameworkException{
        final String REL_INGREDIENTFUNCTION = PropertyUtil.getSchemaProperty("relationship_pgIngredientFunction");
        final String REL_SMARTLABELROW = PropertyUtil.getSchemaProperty("relationship_pgSmartLabelRow");
        final String REL_SMARTLABEL = PropertyUtil.getSchemaProperty("relationship_pgSmartLabel");
        final String PP_CONNECTION = "from[" + REL_SMARTLABEL + "].to.from[" + REL_SMARTLABELROW + "].to.id";
        Boolean isPickListConnected = false;
        
        if(UIUtil.isNotNullAndNotEmpty(sProductId)) {
            DomainObject domainObj = DomainObject.newInstance(context, sProductId);
            StringList slSmartLabelRow = new StringList();
            try {
                slSmartLabelRow = domainObj.getInfoList(context, PP_CONNECTION);
                logger.log(Level.INFO, "slSmartLabelRow: {0}", slSmartLabelRow);
                int size = slSmartLabelRow.size();
                if (!slSmartLabelRow.isEmpty()) {
                    for (int i = 0; i < size; i++) {
                        if (DomainObject.newInstance(context, slSmartLabelRow.get(i)).hasRelatedObjects(context, REL_INGREDIENTFUNCTION, true)) {
                            isPickListConnected = true;
                        } else {
                            isPickListConnected = false;
                            break;
                        }
                    }
                }
            } catch (FrameworkException e) {
                throw e;
            }
        }
        logger.log(Level.INFO, "isPickListConnected: {0}", isPickListConnected);
        return isPickListConnected;
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

                    MapList mlSmartLabelData = rollupAction.getConnectedRollUpData(domProductPart, childProductPartRule.getToType(), childProductPartRule.getRelationshipName(), null, RollupUtil.getObjectSelects(), null, resource.getExecutionType());
                    StringList slSmartLabelIDs = rollupAction.getRollUpObjectList(mlSmartLabelData);

                    if (!slSmartLabelIDs.isEmpty()) {

                        /*
                         * In case of manual rollup, Rollup initiator may not have access to all the
                         * data which needs to be rolledup. So pushing context to User Agent to avoid
                         * data missing in rollup
                         */
                        Rule rollupRelationshipRule = RollupUtil.getRollupRule(rollupConfig,
                                RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                        RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRelationshipRule.getRelationshipName());
                        String[] rollupObjects = slSmartLabelIDs.toArray(new String[slSmartLabelIDs.size()]);
                        // modify isFrom from config
                        Map mRelConnectedProduct = DomainRelationship.connect(context, domMaster, rFPPtoRollUpRel, true,
                                rollupObjects, true);
                        resource.getDynamicSubscription().add(rollupConfig);
                        String sConnectionId;
                        Iterator itr = mRelConnectedProduct.entrySet().iterator();
                        Map.Entry mapRelId;
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
