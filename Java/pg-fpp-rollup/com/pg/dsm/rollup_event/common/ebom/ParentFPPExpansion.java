package com.pg.dsm.rollup_event.common.ebom;

import com.pg.v3.custom.pgV3Constants;

public class ParentFPPExpansion {

    public ProductPart getParentFPP(ProductPart productPart) {
        Filter filter = new Filter();
        filter.filterParentFPP(productPart);
        return filter.getProductFPP();
    }

    public static class Filter {
        ProductPart productFPP;

        public Filter() {
        }

        public void filterParentFPP(ProductPart productPart) {
            if (productPart.isParentExist()) {
                ProductPart parent = productPart.getParent();
                if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(parent.getType())) {
                    this.productFPP = parent;
                } else {
                    filterParentFPP(parent);
                }
            }
        }

        public ProductPart getProductFPP() {
            return productFPP;
        }
    }


}
