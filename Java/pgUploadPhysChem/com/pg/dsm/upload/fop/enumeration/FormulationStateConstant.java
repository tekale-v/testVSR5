/*
 **   FormulationStateConstant.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Holds all the policy-state schema constants.
 **
 */
package com.pg.dsm.upload.fop.enumeration;

import com.matrixone.apps.domain.util.PropertyUtil;
import matrix.db.Context;

public enum FormulationStateConstant {
    FORMULATION_RELEASED("state_Release"),
    FORMULATION_PRELIMINARY("state_Preliminary"),
    FORMULATION_APPROVED("state_Approved"),
    FORMULATION_REVIEW("state_Review"),
    FORMULATION_FORMAL_APPROVAL("state_FormalApproval"),
    FORMULATION_OBSOLETE("state_Obsolete"),
    PICKLIST_ITEM_ACTIVE("state_Active");
    private final String name;

    /**
     * Constructor
     *
     * @param name - String
     * @since DSM 2018x.5
     */
    FormulationStateConstant(String name) {
        this.name = name;
    }

    /**
     * Method to state schema name.
     *
     * @param context - Context
     * @param policy  - String
     * @return String - State name
     * @since DSM 2018x.5
     */
    public String getState(Context context, String policy) {
        return PropertyUtil.getSchemaProperty(context, "policy", policy, this.name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
