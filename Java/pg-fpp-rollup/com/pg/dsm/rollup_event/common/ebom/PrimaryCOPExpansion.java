package com.pg.dsm.rollup_event.common.ebom;

import java.util.Iterator;
import java.util.List;

import com.pg.v3.custom.pgV3Constants;

public class PrimaryCOPExpansion {


    public ProductPart getTopLevelCOP(ProductPart productpart) {
        Filter filter = new Filter();
        filter.filterTopLevelCOP(productpart);
        ProductPart consumerUnitPart = filter.getConsumerUnitPart();
        return consumerUnitPart;
    }

    public static class Filter {
        ProductPart consumerUnitPart;

        public Filter() {

        }

        public void filterTopLevelCOP(ProductPart productPart) {
            if (productPart.isChildExist()) {
                List<ProductPart> children = productPart.getChildren();
                Iterator<ProductPart> iterator = children.iterator();
                ProductPart child;
                while (iterator.hasNext()) {
                    child = iterator.next();
                    if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(child.getType())) {
                        this.consumerUnitPart = child;
                        break;
                    } else {
                        filterTopLevelCOP(child);
                    }
                }
            }
        }

        public ProductPart getConsumerUnitPart() {
            return consumerUnitPart;
        }
    }


}
