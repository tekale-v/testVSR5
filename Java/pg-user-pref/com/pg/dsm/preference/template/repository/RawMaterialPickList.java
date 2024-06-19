package com.pg.dsm.preference.template.repository;

import javax.json.JsonArray;

import com.pg.dsm.preference.CreateUserPreference;

import matrix.db.Context;
import matrix.util.MatrixException;

public class RawMaterialPickList {
    JsonArray partTypes;
    JsonArray reportedFunction;
    JsonArray materialFunction;

    public RawMaterialPickList(Context context) throws MatrixException {
        this.partTypes = CreateUserPreference.RawMaterialPickLists.PART_TYPE.asJsonArray(context);
        this.reportedFunction = CreateUserPreference.RawMaterialPickLists.REPORTED_FUNCTION.asJsonArray(context);
        this.materialFunction = CreateUserPreference.RawMaterialPickLists.MATERIAL_FUNCTION.asJsonArray(context);
    }

    public JsonArray getPartTypes() {
        return partTypes;
    }

    public JsonArray getReportedFunction() {
        return reportedFunction;
    }

    public JsonArray getMaterialFunction() {
        return materialFunction;
    }

    @Override
    public String toString() {
        return "RawMaterialPickList{" +
                "partTypes=" + partTypes +
                ", reportedFunction=" + reportedFunction +
                ", materialFunction=" + materialFunction +
                '}';
    }
}
