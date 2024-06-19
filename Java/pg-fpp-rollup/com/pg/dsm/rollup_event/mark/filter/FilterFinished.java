package com.pg.dsm.rollup_event.mark.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class FilterFinished {
    boolean filtered;
    List<ProductPart> finishedProducts;

    private FilterFinished(Filter filter) {
        this.finishedProducts = filter.finishedProducts;
        this.filtered = filter.filtered;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public List<ProductPart> getFinishedProducts() {
        return finishedProducts;
    }

    public static class Filter {
        Context context;
        List<ProductPart> finishedProducts;
        boolean filtered;

        public Filter(Context context) {
            this.context = context;
            finishedProducts = new ArrayList<>();
        }

        public FilterFinished apply(ProductPart productPart) {
            applyFilter(productPart);
            this.filtered = true;
            return new FilterFinished(this);
        }

        private void applyFilter(ProductPart productPart) {
            if (productPart.isChildExist()) {
                List<ProductPart> children = productPart.getChildren();

                List<ProductPart> parentBeanList;
                List<String> parentTypeList;
                ProductPart currentChild;
                String currentChildType;
                String currentChildReleasePhase;
                boolean bIsParentAndChildOfSameTypeAsCOP;
                boolean bIsParentAndChildOfSameTypeAsPAP;
                boolean bIsParentOfTypeFPP;
                boolean bIsReleasePhaseProduction;

                Iterator<ProductPart> productPartChildItr = children.iterator();
                while (productPartChildItr.hasNext()) {
                    currentChild = productPartChildItr.next();

                    parentBeanList = new ArrayList<>();
                    getBOMParentRecursively(currentChild, parentBeanList);
                    parentTypeList = parentBeanList.stream().map(m -> m.getType()).collect(Collectors.toList());

                    currentChildType = currentChild.getType();

                    bIsParentAndChildOfSameTypeAsCOP = isParentAndChildOfSameTypeAsCOP(currentChildType, parentTypeList);
                    bIsParentAndChildOfSameTypeAsPAP = isParentAndChildOfSameTypeAsPAP(currentChildType, parentTypeList);
                    bIsParentOfTypeFPP = isParentOfTypeFPP(parentTypeList);

                    currentChildReleasePhase = currentChild.getReleasePhase();
                    currentChildReleasePhase = (currentChildReleasePhase == null) ? DomainConstants.EMPTY_STRING : currentChildReleasePhase;
                    bIsReleasePhaseProduction = isReleasePhaseProduction(currentChildReleasePhase);

                    if (!(bIsParentAndChildOfSameTypeAsCOP || bIsParentAndChildOfSameTypeAsPAP || bIsParentOfTypeFPP) && bIsReleasePhaseProduction) {
                        if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(currentChildType)) {
                            finishedProducts.add(getFinishedProductPartBean(currentChild));
                        }
                        applyFilter(currentChild);
                    }
                }
            }
        }

        private boolean isParentAndChildOfSameTypeAsCOP(String childType, List<String> parentTypeList) {
            return pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(childType) && parentTypeList.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART);
        }

        public boolean isParentAndChildOfSameTypeAsPAP(String childType, List<String> parentTypeList) {
            return pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(childType) && (parentTypeList.contains(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART)
                    || parentTypeList.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART));
        }

        public boolean isParentOfTypeFPP(List<String> parentTypeList) {
            return parentTypeList.contains(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
        }

        public boolean isReleasePhaseProduction(String releasePhase) {
            return pgV3Constants.ATTRIBUTE_STAGE_PRODUCTION_VALUE.equalsIgnoreCase(releasePhase);
        }

        public List<ProductPart> getBOMParentRecursively(ProductPart productPart, List<ProductPart> parentBeanList) {
            if (productPart.isParentExist()) {
                ProductPart parent = productPart.getParent();
                parentBeanList.add(parent);
                getBOMParentRecursively(parent, parentBeanList);
            }
            return parentBeanList;
        }

        public ProductPart getFinishedProductPartBean(ProductPart productPart) {
            Map<Object, Object> productPartMap = new HashMap<>();
            productPartMap.put(DomainConstants.SELECT_TYPE, productPart.getType());
            productPartMap.put(DomainConstants.SELECT_NAME, productPart.getName());
            productPartMap.put(DomainConstants.SELECT_REVISION, productPart.getRevision());
            productPartMap.put(DomainConstants.SELECT_ID, productPart.getId());
            productPartMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGSMARTLABELREADY, productPart.getSmartLabelReadyFlag());
            productPartMap.put(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSREADY, productPart.getDgcReadyFlag());
            productPartMap.put(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE, productPart.getReleasePhase());

            return new ProductPart(context, productPartMap, RollupConstants.Basic.EBOM_CHILDREN.getValue());
        }
    }
}
