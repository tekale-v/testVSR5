package com.pg.dsm.rollup_event.mark.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class FilterIntermediate {
    boolean filtered;
    List<ProductPart> intermediates;

    private FilterIntermediate(Filter filter) {
        this.filtered = filter.filtered;
        this.intermediates = filter.intermediates;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public List<ProductPart> getIntermediates() {
        return intermediates;
    }

    public static class Filter {
        Context context;
        List<ProductPart> intermediates;
        boolean filtered;
        String filterTypes;

        public Filter(Context context) {
            this.context = context;
            this.intermediates = new ArrayList<>();
        }

        public FilterIntermediate apply(ProductPart productPart) {
            String releasePhase = productPart.getReleasePhase();
            if (UIUtil.isNotNullAndNotEmpty(productPart.getId())
                    && UIUtil.isNotNullAndNotEmpty(releasePhase)
                    && pgV3Constants.ATTRIBUTE_STAGE_PRODUCTION_VALUE.equalsIgnoreCase(releasePhase)) {
                applyFilter(productPart);
                this.filtered = true;
            } else {
                this.filtered = false;
            }
            return new FilterIntermediate(this);
        }

        public String getFilterTypes() {
            return filterTypes;
        }

        public Filter setFilterTypes(String filterTypes) {
            this.filterTypes = filterTypes;
            return this;
        }

        public void applyFilter(ProductPart productPart) {
            if (productPart.isChildExist()) {
                List<ProductPart> children = productPart.getChildren();

                List<ProductPart> parentBeanList;
                List<String> parentTypeList;
                ProductPart currentChild;
                String currentChildType;

                String currentChildReleasePhase;
                boolean bIsParentAndChildOfSameTypeAsCOP;
                boolean bIsParentAndChildOfSameTypeAsPAP;
                boolean bIsReleasePhaseProduction;

                Iterator<ProductPart> productPartChildItr = children.iterator();
                while (productPartChildItr.hasNext()) {
                    currentChild = productPartChildItr.next();

                    parentBeanList = new ArrayList<>();
                    getBOMParentRecursively(currentChild, parentBeanList);
                    parentTypeList = parentBeanList.stream().map(m -> m.getType()).collect(Collectors.toList());

                    currentChildType = currentChild.getType();
                    currentChildReleasePhase = currentChild.getReleasePhase();
                    currentChildReleasePhase = (currentChildReleasePhase == null) ? DomainConstants.EMPTY_STRING : currentChildReleasePhase;

                    bIsParentAndChildOfSameTypeAsCOP = isParentAndChildOfSameTypeAsCOP(currentChildType, parentTypeList);
                    bIsParentAndChildOfSameTypeAsPAP = isParentAndChildOfSameTypeAsPAP(currentChildType, parentTypeList);
                    bIsReleasePhaseProduction = isReleasePhaseProduction(currentChildReleasePhase);

                    if (!(bIsParentAndChildOfSameTypeAsCOP || bIsParentAndChildOfSameTypeAsPAP) && bIsReleasePhaseProduction) {
                        intermediates.add(currentChild);
                        if (!filterTypes.contains(currentChild.getType())) {
                            applyFilter(currentChild);
                        }
                    }
                }
            }
        }

        public List<ProductPart> getBOMParentRecursively(ProductPart productPart, List<ProductPart> parentBeanList) {
            if (productPart.isParentExist()) {
                ProductPart parent = productPart.getParent();
                parentBeanList.add(parent);
                getBOMParentRecursively(parent, parentBeanList);
            }
            return parentBeanList;
        }

        public boolean isParentAndChildOfSameTypeAsCOP(String childType, List<String> parentTypeList) {
            return pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(childType)
                    && (parentTypeList.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)
                    || parentTypeList.contains(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART));
        }

        public boolean isParentAndChildOfSameTypeAsPAP(String childType, List<String> parentTypeList) {
            return pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(childType) && parentTypeList.contains(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
        }

        public boolean isReleasePhaseProduction(String releasePhase) {
            return pgV3Constants.ATTRIBUTE_STAGE_PRODUCTION_VALUE.equalsIgnoreCase(releasePhase);
        }
    }
}
