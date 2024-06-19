/*
 **   ProductFormSolidCategory.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the solid category constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

public enum ProductFormSolidCategory {
    CAPSULE("Capsule"),
    CREAM_OR_SOFT_SOLID_APDO("Cream/Soft Solid - APDO"),
    GEL_AIR_CARE("Gel - Air Care"),
    LOZENGE("Lozenge"),
    PASTE("Paste"),
    POWDER("Powder"),
    STICK_OR_BAR("Stick/Bar"),
    TABLET("Tablet"),
    SOLID("Solid");
    private final String value;

    /**
     * Constructor
     *
     * @param value - String
     * @since DSM 2018x.5
     */
    ProductFormSolidCategory(String value) {
        this.value = value;
    }

    /**
     * Method to get the constant.
     *
     * @return String -
     * @since DSM 2018x.5
     */
    public String getValue() {
        return value;
    }
}
