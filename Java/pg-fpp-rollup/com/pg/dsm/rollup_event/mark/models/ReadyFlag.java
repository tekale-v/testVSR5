package com.pg.dsm.rollup_event.mark.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ChildExpansion;
import com.pg.dsm.rollup_event.common.ebom.ParentExpansion;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.filter.FilterFinished;
import com.pg.dsm.rollup_event.mark.filter.FilterIntermediate;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class ReadyFlag {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    ProductPart markedProductPart;
    Resource resource;
    Properties rollupPageProperties;
    Context context;

    public ReadyFlag(ProductPart markedProductPart, Resource resource) {
        this.markedProductPart = markedProductPart;
        this.resource = resource;
        this.rollupPageProperties = resource.getRollupPageProperties();
        this.context = resource.getContext();
    }

    public void processReadyFlag() {
        if (null != markedProductPart && isQualifiedToUpdateReadyFlag()) {
            if (isReleasePhaseProduction(markedProductPart.getReleasePhase())) {
                if (isFinishedProductPartType(markedProductPart.getType())) {
                    logger.info("Flag Recalculation started for FPP " + markedProductPart.getType() + pgV3Constants.SYMBOL_SPACE
                            + markedProductPart.getName() + pgV3Constants.SYMBOL_SPACE + markedProductPart.getRevision());
                    processFinishedProduct();
                } else {
                    processIntermediateProduct();
                }
            }
        }
    }

    public void processFinishedProduct() {
        List<ProductPart> intermediateProductParts = filterChildExpansionByIntermediateProductType();
        Map<String, String> calculatedAttributeFlagMap = calculateProductPartFlags(markedProductPart, filterIntermediateProductPartResultByReleasePhase(intermediateProductParts));
        if (!calculatedAttributeFlagMap.isEmpty()) {
            try {
                DomainObject productPartObj = DomainObject.newInstance(context);
                productPartObj.setId(markedProductPart.getId());
                productPartObj.setAttributeValues(context, calculatedAttributeFlagMap);
                logger.info("DGC and SL Flags are update for FPP " + markedProductPart.getType() + pgV3Constants.SYMBOL_SPACE
                        + markedProductPart.getName() + pgV3Constants.SYMBOL_SPACE + markedProductPart.getRevision());
            } catch (FrameworkException e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
        }
    }

    private void processIntermediateProduct() {
        try {
            DomainObject productPartObj = DomainObject.newInstance(context);
            List<ProductPart> productParts = filterParentExpansionByFinishedProductType();
            for (ProductPart productPart : productParts) {
                if (isProductPartQualifiedForChildExpansion(productPart)) {
                    List<ProductPart> intermediateProductParts = filterChildExpansionByIntermediateProductType(productPart);
                    Map<String, String> calculatedAttributeFlagMap = calculateProductPartFlags(productPart, filterIntermediateProductPartResultByReleasePhase(intermediateProductParts));
                    productPartObj.setId(productPart.getId());
                    productPartObj.setAttributeValues(context, calculatedAttributeFlagMap);
                    logger.info("DGC and SL Flags are update for FPP " + productPart.getType() + pgV3Constants.SYMBOL_SPACE
                            + productPart.getName() + pgV3Constants.SYMBOL_SPACE + productPart.getRevision());
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    private boolean isQualifiedToUpdateReadyFlag() {
        boolean isQualified = false;
        try {
            StringList notAllowedTypesForReadyFlag = new StringList();
            notAllowedTypesForReadyFlag.addElement(pgV3Constants.TYPE_RAWMATERIALPART);
            notAllowedTypesForReadyFlag.addElement(pgV3Constants.TYPE_PGRAWMATERIAL);

            String markedProductPartType = markedProductPart.getType();
            String releasePhase = markedProductPart.getReleasePhase();
            isQualified = UIUtil.isNotNullAndNotEmpty(releasePhase)
                    && (pgV3Constants.ATTRIBUTE_STAGE_PRODUCTION_VALUE).equalsIgnoreCase(releasePhase)
                    && UIUtil.isNotNullAndNotEmpty(markedProductPart.getId())
                    && !notAllowedTypesForReadyFlag.contains(markedProductPartType);

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return isQualified;
    }

    public boolean isFinishedProductPartType(String markedProductPartType) {
        return UIUtil.isNotNullAndNotEmpty(markedProductPartType)
                && (pgV3Constants.TYPE_FINISHEDPRODUCTPART).equalsIgnoreCase(markedProductPartType);
    }

    private List<ProductPart> filterParentExpansionByFinishedProductType() {
        List<ProductPart> filteredFinishedProducts = new ArrayList<>();
        try {
            ProductPart productPart = getParentBOMExpansion();
            if (null != productPart && UIUtil.isNotNullAndNotEmpty(productPart.getId()) && productPart.isChildExist()) {
                FilterFinished filterFinished = new FilterFinished.Filter(context).apply(productPart);
                if (filterFinished.isFiltered()) {
                    filteredFinishedProducts = filterFinished.getFinishedProducts();
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return filteredFinishedProducts;
    }

    private List<ProductPart> filterChildExpansionByIntermediateProductType() {
        return filterChildExpansionByIntermediateProductType(markedProductPart);
    }

    private List<ProductPart> filterChildExpansionByIntermediateProductType(ProductPart productPart) {
        List<ProductPart> intermediateProductList = new ArrayList<>();
        try {
            if (isQualifiedToFilterByIntermediateProductType(productPart)) {
                ProductPart productPartWithBOM = getChildBOMExpansion(productPart);
                if (null != productPartWithBOM && productPartWithBOM.isChildExist()) {
                    FilterIntermediate filterIntermediate = filterIntermediate(productPartWithBOM);
                    if (null != filterIntermediate && filterIntermediate.isFiltered()) {
                        intermediateProductList = filterIntermediate.getIntermediates();
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return intermediateProductList;
    }

    private FilterIntermediate filterIntermediate(ProductPart productPart) {
        return new FilterIntermediate.Filter(context)
                .setFilterTypes(rollupPageProperties.getProperty("Str_Allowed_ProductParts_Types"))
                .apply(productPart);
    }

    private boolean isQualifiedToFilterByIntermediateProductType(ProductPart finishedProduct) {
        boolean isQualified = false;
        try {
            boolean bIsReleasePhaseProduction = isReleasePhaseProduction(finishedProduct.getReleasePhase());
            isQualified = UIUtil.isNotNullAndNotEmpty(finishedProduct.getId()) && bIsReleasePhaseProduction;
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return isQualified;
    }

    private boolean isProductPartQualifiedForChildExpansion(ProductPart productPart) {
        boolean isProductPartQualified = false;
        try {
            String type = productPart.getType();
            boolean bIsReleasePhaseProduction = isReleasePhaseProduction(productPart.getReleasePhase());
            isProductPartQualified = (UIUtil.isNotNullAndNotEmpty(type)
                    && (pgV3Constants.TYPE_FINISHEDPRODUCTPART).equalsIgnoreCase(type)) && bIsReleasePhaseProduction;
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return isProductPartQualified;
    }

    public boolean isReleasePhaseProduction(String releasePhase) {
        return UIUtil.isNotNullAndNotEmpty(releasePhase) && pgV3Constants.ATTRIBUTE_STAGE_PRODUCTION_VALUE.equalsIgnoreCase(releasePhase);
    }

    private ProductPart getChildBOMExpansion(ProductPart productPart) {
        ProductPart productPartWithBOM = null;
        try {
            if (null != productPart) {
                productPartWithBOM = new ChildExpansion.Builder(context, productPart.getId(), RollupConstants.Basic.EBOM_CHILDREN.getValue())
                        .setExpansionType(rollupPageProperties.getProperty("Str_Allowed_Types_ForRollupFlag"))
                        .setExpansionRelationship(DomainConstants.RELATIONSHIP_EBOM)
                        .setObjectSelectList(getEBOMSelects())
                        .setRelationshipSelectList(DomainConstants.EMPTY_STRINGLIST)
                        .setObjectWhereClause(DomainConstants.EMPTY_STRING)
                        .setMarkedProductPart(productPart)
                        .setExpandLevel((short) Integer.parseInt(RollupConstants.Basic.ZERO.getValue()))
                        .build().getProductPart();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return productPartWithBOM;
    }

    private ProductPart getParentBOMExpansion() {
        ProductPart productPart = null;
        try {
            if (null != markedProductPart) {
                String productPartId = markedProductPart.getId();
                if (UIUtil.isNotNullAndNotEmpty(productPartId)) {
                    productPart = new ParentExpansion.Builder(context, markedProductPart.getId(), RollupConstants.Basic.EBOM_PARENT.getValue())
                            .setExpansionType(rollupPageProperties.getProperty("Str_AllowedTypes_ForRollupFlag"))
                            .setExpansionRelationship(DomainConstants.RELATIONSHIP_EBOM)
                            .setObjectSelectList(getEBOMSelects())
                            .setRelationshipSelectList(DomainConstants.EMPTY_STRINGLIST)
                            .setObjectWhereClause(rollupPageProperties.getProperty("Str_ObjectWhere_Part_State"))
                            .setMarkedProductPart(markedProductPart)
                            .setExpandLevel((short) Integer.parseInt(RollupConstants.Basic.ZERO.getValue()))
                            .build().getProductPart();
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return productPart;
    }

    private Map<String, String> calculateProductPartFlags(ProductPart parentProductPart, List<ProductPart> productParts) {

        Map<String, String> calculatedAttributeFlagMap = new HashMap<>();
        try {
            String dgcReadyAllowedTypes = rollupPageProperties.getProperty("Str_DGCReady_Allowed_Types");
            String smartLabelReadyAllowedTypes = rollupPageProperties.getProperty("Str_SLReady_Allowed_Types");
            String smartLabelReadyFlag = parentProductPart.getSmartLabelReadyFlag();
            String dgcReadyFlag = parentProductPart.getDgcReadyFlag();
            String dgcReadyFlagFinal = pgV3Constants.KEY_NO_VALUE;
            String smartLabelReadyFlagFinal = pgV3Constants.KEY_NO_VALUE;


            ProductPart productPart;
            String productPartType;
            String productSmartLabelReadyFlag;
            String productDGCLabelReadyFlag;

            StringList slDGCFlags = new StringList();
            StringList slSmartLabelFlags = new StringList();

            Iterator<ProductPart> partIterator = productParts.iterator();

            Rollup dgcRollupConfig = RollupUtil.getRollup(RollupConstants.Basic.DGC_ROLLUP_EVENT_IDENTIFIER.getValue(), resource.getRollupRuleConfiguration());

            while (partIterator.hasNext()) {
                productPart = partIterator.next();
                productPartType = productPart.getType();
                productSmartLabelReadyFlag = productPart.getSmartLabelReadyFlag();
                productDGCLabelReadyFlag = productPart.getDgcReadyFlag();

                if (dgcReadyAllowedTypes.contains(productPartType)) {
                    slDGCFlags.add(productDGCLabelReadyFlag);
                }

                if (smartLabelReadyAllowedTypes.contains(productPartType)) {
                    slSmartLabelFlags.add(productSmartLabelReadyFlag);
                }
            }

            if (!slDGCFlags.isEmpty())
                dgcReadyFlagFinal = !slDGCFlags.contains(pgV3Constants.KEY_NO_VALUE) ? pgV3Constants.KEY_YES_VALUE : dgcReadyFlagFinal;

            smartLabelReadyFlagFinal = slSmartLabelFlags.contains(pgV3Constants.KEY_YES_VALUE) ? pgV3Constants.KEY_YES_VALUE : smartLabelReadyFlagFinal;

            if (!dgcReadyFlagFinal.equalsIgnoreCase(dgcReadyFlag) || !smartLabelReadyFlagFinal.equalsIgnoreCase(smartLabelReadyFlag)) {

                calculatedAttributeFlagMap.put(pgV3Constants.ATTRIBUTE_PGSMARTLABELREADY, smartLabelReadyFlagFinal);
                calculatedAttributeFlagMap.put(pgV3Constants.ATTRIBUTE_PGDANGEROUSGOODSREADY, dgcReadyFlagFinal);

                if (rollupPageProperties.getProperty("Str_DGCReadyOnFPP_Enable").equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE)
                        || (parentProductPart.isCircularExist() && !Boolean.parseBoolean(dgcRollupConfig.getAllowCircularDataForRollup()))) {
                    calculatedAttributeFlagMap.remove(pgV3Constants.ATTRIBUTE_PGDANGEROUSGOODSREADY);
                }
                if (rollupPageProperties.getProperty("Str_SLReadyOnFPP_Enable").equalsIgnoreCase(pgV3Constants.KEY_NO_VALUE)) {
                    calculatedAttributeFlagMap.remove(pgV3Constants.ATTRIBUTE_PGSMARTLABELREADY);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return calculatedAttributeFlagMap;
    }

    private List<ProductPart> filterIntermediateProductPartResultByReleasePhase(List<ProductPart> intermediateProductParts) {
        List<ProductPart> productParts = new ArrayList<>();
        try {
            if (!intermediateProductParts.isEmpty()) {
                String allowedProductPartsTypes = rollupPageProperties.getProperty("Str_Allowed_ProductParts_Types");
                ProductPart currentChildProductPart;
                Iterator<ProductPart> intermediateItr = intermediateProductParts.iterator();
                String releasePhase;
                while (intermediateItr.hasNext()) {
                    currentChildProductPart = intermediateItr.next();
                    releasePhase = currentChildProductPart.getReleasePhase();
                    releasePhase = (releasePhase == null) ? DomainConstants.EMPTY_STRING : releasePhase;
                    if (!allowedProductPartsTypes.contains(currentChildProductPart.getType()) && currentChildProductPart.isChildExist() && isReleasePhaseProduction(releasePhase)) {
                        productParts.addAll(currentChildProductPart.getChildren());
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return productParts;
    }

    private StringList getEBOMSelects() {
        StringList slEBOMObjSelects = new StringList(8);
        slEBOMObjSelects.add(DomainConstants.SELECT_ID);
        slEBOMObjSelects.add(DomainConstants.SELECT_TYPE);
        slEBOMObjSelects.add(DomainConstants.SELECT_NAME);
        slEBOMObjSelects.add(DomainConstants.SELECT_REVISION);
        slEBOMObjSelects.add(DomainConstants.SELECT_LEVEL);
        slEBOMObjSelects.add(pgV3Constants.RELATIONSHIP);
        slEBOMObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSMARTLABELREADY);
        slEBOMObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSREADY);
        slEBOMObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
        return slEBOMObjSelects;
    }
}
