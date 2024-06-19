/*
 **   ProductFormAerosolCategory.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the aerosol category constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ProductFormAerosolCategory {
    AEROSOL("Aerosol"),
    GAS("Gas"),
    MOUSSE_OR_FOAM("Mousse/Foam");

    private static final Map<String, ProductFormAerosolCategory> attributeMap = Arrays.stream(values()).collect(Collectors.toMap(Enum::toString, Function.identity()));

    private final String value;

    /**
     * Constructor
     *
     * @param value - String
     * @since DSM 2018x.5
     */
    ProductFormAerosolCategory(String value) {
        this.value = value;
    }

    /**
     * Method to get attribute of Product Form Aerosol Category.
     *
     * @param name - String
     * @return ProductFormAerosolCategory -
     * @since DSM 2018x.5
     */
    public static ProductFormAerosolCategory get(String name) {
        return attributeMap.get(name);
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
