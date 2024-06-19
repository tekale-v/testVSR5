package com.pg.dsm.rollup_event.rollup.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ParentFPPExpansion;
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

public class BatteryRollupEvent implements RollupEvent {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart productPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;

    public BatteryRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
        this.productPart = productPart;
        this.rollupConfig = rollupConfig;
        this.resource = resource;
        this.context = resource.getContext();
        rollupAction = new RollupAction(this.resource);
    }

    @Override
    public void execute() {
        if (productPart != null) {
            try {
                List<ProductPart> filteredBatteryBean = new ArrayList<>();
                HashMap<String, BigDecimal> mpBatterQuantityMap = new HashMap<>();
                Boolean isModified = rollupAction.disconnectExistingRollupData(productPart, rollupConfig);
                //Modified by DSM in 2018x.6 May CW for Eco-Fees Requirement
                StringList slTopLevelCOPIds = getTopLevelCOPId(productPart);
                if (slTopLevelCOPIds.isEmpty()) {
                    slTopLevelCOPIds.add(productPart.getChildren().get(0).getId());
                }
                // sTopLevelCOPId =  UIUtil.isNotNullAndNotEmpty(sTopLevelCOPId) ? sTopLevelCOPId : productPart.getChildren().get(0).getId();

                applyFilter(productPart, filteredBatteryBean, mpBatterQuantityMap, slTopLevelCOPIds);
                performRollupConnections(productPart, filteredBatteryBean, mpBatterQuantityMap);
                if (isModified) {
                    resource.getDynamicSubscription().add(rollupConfig);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
        }
    }

    //Updated Parameter from String to Stringlist by DSM in 2018x.6 May CW for Eco-Fees Requirement
    public void applyFilter(ProductPart productPartBean, List<ProductPart> filteredBatteryBean,
                            HashMap<String, BigDecimal> mpBatterQuantityMap, StringList slTopLevelCOPIds) {
        try {
            if (productPartBean.isChildExist()) {
                List<ProductPart> children = productPartBean.getChildren();
                Iterator<ProductPart> childrenItr = children.iterator();

                ProductPart childrenProduct;
                String sProductId;
                HashMap mpQuantity = new HashMap();
                StringList slProcessedParentList;
                boolean bValidation;
                boolean bChildValidation;
                RollupRule rollupRule;
                BigDecimal bdQuantity;
                BigDecimal bdBaseQuantity;
                while (childrenItr.hasNext()) {
                    slProcessedParentList = new StringList();
                    mpQuantity.put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY, BigDecimal.valueOf(1.0));
                    mpQuantity.put(pgV3Constants.PARENT_ID, slProcessedParentList);
                    //Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -starts
                    mpQuantity.put(RollupConstants.Basic.ROUND_DPP_QTY.getValue(), pgV3Constants.FALSE);
                    //Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -Ends
                    childrenProduct = childrenItr.next();
                    sProductId = childrenProduct.getId();

                    rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    bChildValidation = rollupRule.isChildrenAllowed();

                    if (bChildValidation) {
                        bValidation = isQualified(childrenProduct);
                        if (bValidation) {
                            // Add logic check for is Battery type and then add to list

                            filteredBatteryBean.add(childrenProduct);
                            calculateQuantity(childrenProduct, mpQuantity, slTopLevelCOPIds);
                            bdQuantity = (BigDecimal) mpQuantity.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);

                            //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts
                            if (mpQuantity.containsKey(RollupConstants.Basic.DPP_ROLLUP_QUANTITY.getValue())) {
                                bdBaseQuantity = (BigDecimal) mpQuantity.get(RollupConstants.Basic.DPP_ROLLUP_QUANTITY.getValue());
                                if (bdBaseQuantity.compareTo(BigDecimal.ZERO) != 0) {
                                    bdQuantity = bdQuantity.divide(bdBaseQuantity);
                                }
                            }

                            //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends
                            //Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -starts
                            if (Boolean.parseBoolean((String) mpQuantity.get(RollupConstants.Basic.ROUND_DPP_QTY.getValue())) && pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(childrenProduct.getType())) {
                                bdQuantity = bdQuantity.setScale(0, RoundingMode.HALF_UP);
                            }
                            ////Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -Ends
                            slProcessedParentList = (StringList) mpQuantity.get(pgV3Constants.PARENT_ID);

                            //Modified by DSM in 2018x.6 May CW for Eco-Fees Requirement
                            //if (!slProcessedParentList.isEmpty() && slProcessedParentList.contains(sTopLevelCOPId)) {
                            if (!slProcessedParentList.isEmpty()) {
                                if (!mpBatterQuantityMap.containsKey(sProductId)) {
                                    mpBatterQuantityMap.put(sProductId, bdQuantity);
                                } else {
                                    mpBatterQuantityMap.put(sProductId, bdQuantity.add(mpBatterQuantityMap.get(sProductId)));
                                }
                            } else {
                                mpBatterQuantityMap.put(sProductId, BigDecimal.valueOf(childrenProduct.getQuantity()));
                            }
                        } else {
                            if (checkForNotPoweredType(childrenProduct)) {
                                applyFilter(childrenProduct, filteredBatteryBean, mpBatterQuantityMap, slTopLevelCOPIds);
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }


    public boolean isQualified(ProductPart childrenProduct) {

        if (childrenProduct != null) {
            String sProductState = childrenProduct.getCurrentState();
            String sPowerSource = childrenProduct.getPowerSource();
            String sProductType = childrenProduct.getType();
            String sIsTheProductABattery = childrenProduct.getIsTheProductABattery();
            //Add RM attribute check
            boolean bRMCheck = UIUtil.isNotNullAndNotEmpty(sProductType)
                    && (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(sProductType)
                    || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(sProductType))
                    && (UIUtil.isNotNullAndNotEmpty(sIsTheProductABattery)
                    && pgV3Constants.KEY_YES_VALUE.equalsIgnoreCase(sIsTheProductABattery));

            boolean bDPPCheck = UIUtil.isNotNullAndNotEmpty(sProductType)
                    && pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductType)
                    && UIUtil.isNotNullAndNotEmpty(sPowerSource)
                    && rollupConfig.getAllowedRanges().contains(sPowerSource);

            return (bRMCheck || bDPPCheck) && pgV3Constants.STATE_RELEASE.equalsIgnoreCase(sProductState);

        }
        return false;
    }

    public boolean checkForNotPoweredType(ProductPart productBean) {
        boolean bProcessChildren = true;
        String sProductType = productBean.getType();
        if (UIUtil.isNotNullAndNotEmpty(sProductType) && pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sProductType)) {
            String sPowerSource = productBean.getPowerSource();
            if (UIUtil.isNotNullAndNotEmpty(sPowerSource)
                    && RollupConstants.Basic.NOT_POWERED.getValue().equalsIgnoreCase(sPowerSource)) {
                bProcessChildren = false;
            }
        }
        return bProcessChildren;
    }

    public void performRollupConnections(ProductPart productPartBean, List<ProductPart> filteredBatteryBean, HashMap<String, BigDecimal> mpBatterQuantityMap) throws FrameworkException {
        try {
            if (productPartBean != null) {
                String sFPPId = productPartBean.getId();
                int iBatteryDataSize = filteredBatteryBean.size();
                ProductPart batteryBean;
                String sObjectId;
                String sQuantityAttribute;
                String sConnectionId;
                Map mRelConnectedProduct = null;

                Rule rollupRule = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());

                RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRule.getRelationshipName());
                DomainObject domFPP = DomainObject.newInstance(context, sFPPId);
                Iterator itr = null;
                StringList slConnectedObjects = new StringList();
                StringList slRollupObjects;
                int iRollupObjectsSize;
                HashMap<String, String> mpFPPAttributeMap = getDefaultAttributeMap();
                Map.Entry mapRelId;
                /*
                 * In case of manual rollup, Rollup initiator may not have access to all the
                 * data which needs to be rolledup. So pushing context to User Agent to avoid
                 * data missing in rollup
                 */
                BigDecimal bdCalculatedValue;
                for (int i = 0; i < iBatteryDataSize; i++) {
                    batteryBean = filteredBatteryBean.get(i);
                    sQuantityAttribute = getQuantityAttribute(batteryBean.getType());
                    sObjectId = batteryBean.getId();
                    performFPPBatteryCalculation(context, batteryBean, mpFPPAttributeMap);
                    slRollupObjects = new StringList(1);
                    slRollupObjects.add(sObjectId);
                    iRollupObjectsSize = slRollupObjects.size();
                    if (!slConnectedObjects.contains(sObjectId)) {
                        //Modified for Defect# 44593 - Starts
                        // Relationship pgRolledUpBattery restricts duplicate connections. Added try/catch block to handle exception in case of duplicate connections
                        try {
                            mRelConnectedProduct = DomainRelationship.connect(context, domFPP, rFPPtoRollUpRel, true, slRollupObjects.toArray(new String[iRollupObjectsSize]), true);
                            slConnectedObjects.add(sObjectId);
                            resource.getDynamicSubscription().add(rollupConfig);
                            if (mRelConnectedProduct != null && mpBatterQuantityMap.containsKey(sObjectId)) {
                                itr = mRelConnectedProduct.entrySet().iterator();
                                while (itr.hasNext()) {
                                    mapRelId = (Map.Entry) itr.next();
                                    sConnectionId = mapRelId.getValue().toString();
                                    bdCalculatedValue = mpBatterQuantityMap.get(sObjectId);
                                    //Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -starts
                                    if (!pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(batteryBean.getType())) {
                                        bdCalculatedValue = bdCalculatedValue.setScale(0, RoundingMode.HALF_UP);
                                    }
                                    //Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -Ends
                                    DomainRelationship.setAttributeValue(context, sConnectionId, sQuantityAttribute, String.valueOf(bdCalculatedValue));
                                }
                            }
                        } catch (FrameworkException e) {
							/* Commented to reduce logger size
                    	   logger.log(Level.INFO, "Avoided duplicate Battery Rollup connection from Object: {0}", sObjectId);
							 */
                        }
                        //Modified for Defect# 44593 - Ends
                    }
                }

                domFPP.setAttributeValues(context, mpFPPAttributeMap);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }


    public void performFPPBatteryCalculation(Context context, ProductPart batteryBean, HashMap<String, String> mpFPPMap) {
        if (batteryBean != null) {
            int iBatteriesInsideDevice = batteryBean.getNumberOfBatteryShippedInsideDeviceDb();
            //Get below value from bean
            int iNumberOfBatteriesRequired = batteryBean.getNumberOfBatteriesRequiredDb();
            double iQuantity = batteryBean.getQuantity();

            String sAreBatteriesBuiltinValue = mpFPPMap.get(RollupConstants.Attribute.ARE_BATTERIES_BUILTIN.getName(context));
            String sAreBatteriesIncludeValue = mpFPPMap.get(RollupConstants.Attribute.ARE_BATTERIES_INCLUDED.getName(context));
            String sAreBatteriesRequiredValue = mpFPPMap.get(RollupConstants.Attribute.ARE_BATTERIES_REQUIRED.getName(context));

            if (iNumberOfBatteriesRequired > 0 && pgV3Constants.KEY_NO_VALUE.equalsIgnoreCase(sAreBatteriesRequiredValue)) {
                mpFPPMap.put(RollupConstants.Attribute.ARE_BATTERIES_REQUIRED.getName(context),
                        pgV3Constants.KEY_YES_VALUE);
            }


            if (iBatteriesInsideDevice > 0
                    && pgV3Constants.KEY_NO_VALUE.equalsIgnoreCase(sAreBatteriesBuiltinValue)) {
                mpFPPMap.put(RollupConstants.Attribute.ARE_BATTERIES_BUILTIN.getName(context),
                        pgV3Constants.KEY_YES_VALUE);
                mpFPPMap.put(RollupConstants.Attribute.ARE_BATTERIES_INCLUDED.getName(context),
                        pgV3Constants.KEY_YES_VALUE);
            }

            if ((pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(batteryBean.getType())
                    || pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(batteryBean.getType()))
                    && pgV3Constants.KEY_NO_VALUE.equalsIgnoreCase(sAreBatteriesIncludeValue) && iQuantity > 0) {
                mpFPPMap.put(RollupConstants.Attribute.ARE_BATTERIES_INCLUDED.getName(context),
                        pgV3Constants.KEY_YES_VALUE);
            }

        }
    }

    public String getQuantityAttribute(String sObjectType) {
        if (UIUtil.isNotNullAndNotEmpty(sObjectType) && pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sObjectType)) {
            return RollupConstants.Attribute.DPP_QTY_PER_COP.getName(context);
        } else {
            return pgV3Constants.ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE;
        }
    }

    //Updated Parameter from String to Stringlist by DSM in 2018x.6 May CW for Eco-Fees Requirement
    public void calculateQuantity(ProductPart batteryBean, HashMap mpQuantity, StringList slTopLevelCOPIds) {
        try {
            Double sQuantity;

            BigDecimal bdQuantity = (BigDecimal) mpQuantity.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
            StringList slParentList = (StringList) mpQuantity.get(pgV3Constants.PARENT_ID);

            if (batteryBean.isParentExist()) {
                ProductPart parent = batteryBean.getParent();
                String sProductId;
                if (parent != null) {

                    sProductId = parent.getId();
                    //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts
                    if (pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(parent.getType()) && RollupConstants.Basic.POWERSOURCE_ROLLUP.getValue().equalsIgnoreCase(parent.getPowerSource())) {
                        String sBaseQuantity = parent.getBomBaseQuantity();
                        if (UIUtil.isNotNullAndNotEmpty(sBaseQuantity)) {
                            mpQuantity.put(RollupConstants.Basic.DPP_ROLLUP_QUANTITY.getValue(), BigDecimal.valueOf(Double.parseDouble(sBaseQuantity)));
                        } else {
                            mpQuantity.put(RollupConstants.Basic.DPP_ROLLUP_QUANTITY.getValue(), BigDecimal.valueOf(0.0));
                        }
                    }
                    //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends
                    //Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -starts

                    if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(parent.getType())) {

                        Rule rollupFPPRule = RollupUtil.getRollupRule(rollupConfig,
                                RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                        String sBaseUnitOfMeasure = parent.getBaseUnitOfMeasure();
                        String sFPPAllowedBUMForQty = rollupFPPRule.getInclusionType();
                        if (UIUtil.isNotNullAndNotEmpty(sBaseUnitOfMeasure) && UIUtil.isNotNullAndNotEmpty(sFPPAllowedBUMForQty) && sFPPAllowedBUMForQty.contains(sBaseUnitOfMeasure)) {
                            mpQuantity.put(RollupConstants.Basic.ROUND_DPP_QTY.getValue(), pgV3Constants.KEY_TRUE);
                        }
                    }
                    //Modified by DSM(Sogeti) for 2018x.6 Jan CW Defect #45404 -Ends
                    //Modified by DSM in 2018x.6 May CW for Eco-Fees Requirement
                    //if (UIUtil.isNotNullAndNotEmpty(sProductId) && !batteryBean.getId().equalsIgnoreCase(sTopLevelCOPId)) {
                    if (UIUtil.isNotNullAndNotEmpty(sProductId) && !(slTopLevelCOPIds.contains(batteryBean.getId()))) {
                        sQuantity = batteryBean.getQuantity();
                        if (isValidForQtyCalculation(batteryBean) && null != sQuantity) {
                            bdQuantity = bdQuantity.multiply(BigDecimal.valueOf(sQuantity));
                            mpQuantity.put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY, bdQuantity);

                        }
                        slParentList.add(sProductId);
                        mpQuantity.put(pgV3Constants.PARENT_ID, slParentList);
                        calculateQuantity(parent, mpQuantity, slTopLevelCOPIds);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    //Modified by DSM in 2018x.6 May CW for Eco-Fees Requirement
    public StringList getTopLevelCOPId(ProductPart productBean) {

        StringList slTopLevelCOPIds = new StringList();
        try {
            if (productBean.isChildExist()) {
                List<ProductPart> children = productBean.getChildren();
                Iterator<ProductPart> childrenItr = children.iterator();
                ProductPart childrenProduct;
                String sProductType;
                String strParentType = productBean.getType();
                String strParentId = productBean.getId();

                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();
                    sProductType = childrenProduct.getType();
                    if (UIUtil.isNotNullAndNotEmpty(strParentType) && pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strParentType)) {
                        Rule rollupFPPRule = RollupUtil.getRollupRule(rollupConfig,
                                RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                        String sFPPAllowedBUMForQty = rollupFPPRule.getInclusionType();
                        String strFPPBUOM = productBean.getBaseUnitOfMeasure();
                        if (sFPPAllowedBUMForQty.contains(strFPPBUOM)) {
                            slTopLevelCOPIds.add(strParentId);
                        } else {
                            slTopLevelCOPIds = getTopLevelCOPId(childrenProduct);
                        }
                    } else if (UIUtil.isNotNullAndNotEmpty(sProductType) && pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sProductType)
                            && (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strParentType) ||
                            pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strParentType))) {
                        slTopLevelCOPIds.add(childrenProduct.getId());
                    } else {
                        if (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sProductType) ||
                                pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sProductType)) {
                            slTopLevelCOPIds = getTopLevelCOPId(childrenProduct);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return slTopLevelCOPIds;
    }

    public HashMap<String, String> getDefaultAttributeMap() {
        HashMap<String, String> mpDefaultAttributes = new HashMap();
        mpDefaultAttributes.put(RollupConstants.Attribute.ARE_BATTERIES_BUILTIN.getName(context),
                pgV3Constants.KEY_NO_VALUE);
        mpDefaultAttributes.put(RollupConstants.Attribute.ARE_BATTERIES_INCLUDED.getName(context),
                pgV3Constants.KEY_NO_VALUE);
        mpDefaultAttributes.put(RollupConstants.Attribute.ARE_BATTERIES_REQUIRED.getName(context),
                pgV3Constants.KEY_NO_VALUE);
        return mpDefaultAttributes;
    }

    //Modified by DSM in 2018x.6 May CW for Eco-Fees Requirement
    public boolean isValidForQtyCalculation(ProductPart productBean) {
        boolean isValid = false;
        try {
            String sType = productBean.getType();
            String sParentType = productBean.getParent().getType();
            boolean bFormulationProcessCheck = pgV3Constants.TYPE_FORMULATIONPROCESS.equalsIgnoreCase(sType);
            boolean bFormulationPhaseCheck = pgV3Constants.TYPE_FORMULATIONPHASE.equalsIgnoreCase(sType);
            boolean bCUPCheck = pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sType);
            boolean bIPCheck = (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sType)
                    && pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sParentType)) || (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sType)
                    && pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sParentType));
            boolean bCOPCheck = pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(sType)
                    && (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(sParentType)
                    || pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(sParentType));

            //Allowing IP and Primary COP in Quantity calculation if child FPP BUM is CS
            if (bIPCheck || bCOPCheck || bCUPCheck) {
                Rule rollupFPPRule = RollupUtil.getRollupRule(rollupConfig,
                        RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                ParentFPPExpansion fppExpansion = new ParentFPPExpansion();
                ProductPart productFPP = fppExpansion.getParentFPP(productBean);

                //if(null != productFPP && !productFPP.getId().equalsIgnoreCase(this.productPart.getId())){
                String sBaseUnitOfMeasure = productFPP.getBaseUnitOfMeasure();
                if (UIUtil.isNotNullAndNotEmpty(sBaseUnitOfMeasure)) {
                    String sFPPAllowedBUMForQty = rollupFPPRule.getInclusionType();
                    if (sFPPAllowedBUMForQty.contains(sBaseUnitOfMeasure)) {
                        bIPCheck = false;
                        bCOPCheck = false;
                        bCUPCheck = false;
                    }
                }
                //}
            }

            if (!(bFormulationProcessCheck || bFormulationPhaseCheck || bCUPCheck || bIPCheck || bCOPCheck)) {
                isValid = true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return isValid;
    }

    @Override
    public void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap)
            throws FrameworkException {
        // TODO Auto-generated method stub

    }

    @Override
    public void processProductParts(ProductPart masterPart) {
        // TODO Auto-generated method stub

    }
}