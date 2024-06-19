/*
 * Name: CircularConstant.java
 * About: To store constants.
 * Since: 18x.5
 */
package com.pg.v4.util.enumeration;

public enum CircularConstant {

    SUBSTITUTE_ID("substituteId"),
    RELATIONSHIP("relationship"),
    LEVEL_ZERO("0"),
    CUSTOM("custom"),
    EBOM("EBOM"),
    SUBSTITUTE("Substitute"),
    FORWARD_ARROWS(" >> "),
    CIRCULAR_REFERENCE_FOUND("Circular reference found in structure "),
    CIRCULAR_REFERENCE_MESSAGE("Circular Reference -> "),
    CIRCULAR_HISTORY_UPDATE("Circular information updated on object history"),
    TO_SIDE("to");

    private final String value;

    /**
     * Constructor
     *
     * @param value - String
     * @since DSM 2018x.5
     */
    CircularConstant(String value) {
        this.value = value;
    }

    /**
     * Method to get value
     *
     * @since DSM 2018x.5
     */
    public String getValue() {
        return value;
    }

}
