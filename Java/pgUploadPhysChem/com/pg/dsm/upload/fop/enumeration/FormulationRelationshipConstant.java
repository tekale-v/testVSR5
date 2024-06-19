/*
 **   FormulationRelationshipConstant.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the relationship schema constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.Context;

public enum FormulationRelationshipConstant {
    OWNING_PRODUCT_LINE("relationship_OwningProductLine"),
    DOCUMENT_TO_PLATFORM("relationship_pgDocumentToPlatform"),
    DOCUMENT_TO_BUSINESS_AREA("relationship_pgDocumentToBusinessArea");

    private final String name;

    /**
     * Constructor
     *
     * @param name - String
     * @since DSM 2018x.5
     */
    FormulationRelationshipConstant(String name) {
        this.name = name;
    }

    /**
     * Method to relationship schema name.
     *
     * @param context - Context
     * @return String - Relationship name
     * @since DSM 2018x.5
     */
    public String getRelationship(Context context) {
        return PropertyUtil.getSchemaProperty(context, this.name);
    }


    /**
     * Method to relationship schema select expression.
     *
     * @param context - Context
     * @return String - relationship select to name
     * @since DSM 2018x.5
     */
    public String getRelationshipSelectToName(Context context) {
        return getRelationshipSelectToNameFormat(this.getRelationship(context));
    }

    /**
     * Method to relationship schema select expression.
     *
     * @param name - String
     * @return String - relationship select to name
     * @since DSM 2018x.5
     */
    private String getRelationshipSelectToNameFormat(String name) {
        if (name == null) {
            return "ERROR: NULL ATTRIBUTE";
        } else {
            StringBuilder selectBuilder = new StringBuilder(name.length() + 11);
            selectBuilder.append("relationship[");
            selectBuilder.append(name);
            selectBuilder.append("].to.name");
            return selectBuilder.toString();
        }
    }

}
