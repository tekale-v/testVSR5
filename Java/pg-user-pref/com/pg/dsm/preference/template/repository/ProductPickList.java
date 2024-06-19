package com.pg.dsm.preference.template.repository;

import javax.json.JsonArray;

import com.pg.dsm.preference.CreateUserPreference;

import matrix.db.Context;
import matrix.util.MatrixException;

public class ProductPickList {
    JsonArray partTypes;
    JsonArray complianceRequired;
    JsonArray reportedFunction;

    public ProductPickList(Context context) throws MatrixException {
        this.partTypes = CreateUserPreference.ProductPickLists.PART_TYPE.asJsonArray(context);
        this.complianceRequired = CreateUserPreference.ProductPickLists.PRODUCT_COMPLIANCE_REQUIRED.asJsonArray(context);
        this.reportedFunction = CreateUserPreference.ProductPickLists.REPORTED_FUNCTION.asJsonArray(context);
    }

    public JsonArray getPartTypes() {
        return partTypes;
    }

    public JsonArray getComplianceRequired() {
        return complianceRequired;
    }

    public JsonArray getReportedFunction() {
        return reportedFunction;
    }

    @Override
    public String toString() {
        return "ProductPickList{" +
                "partTypes=" + partTypes +
                ", complianceRequired=" + complianceRequired +
                ", reportedFunction=" + reportedFunction +
                '}';
    }
}
