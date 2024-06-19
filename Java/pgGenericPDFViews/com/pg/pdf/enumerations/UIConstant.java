package com.pg.pdf.enumerations;

public enum UIConstant {
    ATTRIBUTE("attribute"),
    PROGRAM("program"),
    FUNCTION("function"),
    TRUE("true"),
    ADMIN_TYPE_ATTRIBUTE("attribute_"),
    ADMIN_TYPE_STATE("state"),
    PROPERTY_FILE_CONFIG("PROPERTY.FILE.CONFIG"),
    PROPERTY_FILE_LOGGER("PROPERTY.FILE.LOGGER"),
    MATRIX_HOST("MATRIX.HOST"),
    MATRIX_USER("MATRIX.USER"),
    MATRIX_PWD("MATRIX.PASSWORD");
    private final String value;
    UIConstant(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
