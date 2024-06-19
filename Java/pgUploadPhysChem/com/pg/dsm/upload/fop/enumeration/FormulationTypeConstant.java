/*
 **   FormulationTypeConstant.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the type schema constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.Context;

public enum FormulationTypeConstant {
    FORMULATION_PART("type_FormulationPart"),
    //Modify as per 2018x.6 - Start
    ASSEMBLEDPRODUCTPART_PART("type_pgAssembledProductPart"),
  	//Modify as per 2018x.6 - Ends
    PLI_PRODUCT_FORM("type_pgPLIProductForm"),
    PRODUCT_CATEGORY_PLATFORM("type_pgPLIProductCategoryPlatform"),
    PLATFORM("type_pgPLIPlatform"),
    PRODUCT_TECHNOLOGY_PLATFORM("type_pgPLIProductTechnologyPlatform"),
    BUSINESS_AREA("type_pgPLIBusinessArea");
    private final String name;

    /**
     * Constructor
     *
     * @param name - String
     * @since DSM 2018x.5
     */
    FormulationTypeConstant(String name) {
        this.name = name;
    }

    /**
     * Method to type schema name.
     *
     * @param context - Context
     * @return String - Type name
     * @since DSM 2018x.5
     */
    public String getType(Context context) {
        return PropertyUtil.getSchemaProperty(context, this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
