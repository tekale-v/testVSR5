/*
 **   ProductFormLiquidCategory.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the liquid category constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

public enum ProductFormLiquidCategory {
    CREAM_OR_SOFT_SOLID("Cream/Soft Solid"),
    GEL("Gel"),
    LIQUID("Liquid"),
    SPRAY("Spray");
    private final String value;

    /**
     * Constructor
     *
     * @param value - String
     * @since DSM 2018x.5
     */
    ProductFormLiquidCategory(String value) {
        this.value = value;
    }

    /**
     * Method to get value.
     *
     * @return String -
     * @since DSM 2018x.5
     */
    public String getValue() {
        return value;
    }
}
