/*
 **   FormulationPolicyConstant.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the policy schema constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.Context;

public enum FormulationPolicyConstant {
    PRODUCTION_FORMULATION_PART("policy_ProductionFormulationPart"),
    PICKLIST_ITEM("policy_pgPicklistItem");
    private final String name;

    /**
     * Constructor
     *
     * @param name - String
     * @since DSM 2018x.5
     */
    FormulationPolicyConstant(String name) {
        this.name = name;
    }

    /**
     * Method to policy schema name.
     *
     * @param context - Context
     * @return String - Policy name
     * @since DSM 2018x.5
     */
    public String getPolicy(Context context) {
        return PropertyUtil.getSchemaProperty(context, this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
