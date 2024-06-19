package com.pg.dsm.preference.template.repository;

import javax.json.JsonArray;

import com.pg.dsm.preference.CreateUserPreference;

import matrix.db.Context;
import matrix.util.MatrixException;

public class PackagingPickList {
    JsonArray partTypes;
    JsonArray materialTypes;
    JsonArray componentTypes;
    JsonArray unitOfMeasures;
    JsonArray reportedFunction;

    public PackagingPickList(Context context) throws MatrixException {
        this.partTypes = CreateUserPreference.PackagingPickLists.PART_TYPE.asJsonArray(context);
        this.componentTypes = CreateUserPreference.PackagingPickLists.PACKAGING_COMPONENT_TYPE.asJsonArray(context);
        this.materialTypes = CreateUserPreference.PackagingPickLists.PACKAGING_MATERIAL_TYPE.asJsonArray(context);
        this.unitOfMeasures = CreateUserPreference.PackagingPickLists.BASE_UNIT_OF_MEASURE.asJsonArray(context);
        this.reportedFunction = CreateUserPreference.PackagingPickLists.REPORTED_FUNCTION.asJsonArray(context);
    }

    public JsonArray getPartTypes() {
        return partTypes;
    }

    public JsonArray getMaterialTypes() {
        return materialTypes;
    }

    public JsonArray getComponentTypes() {
        return componentTypes;
    }

    public JsonArray getUnitOfMeasures() {
        return unitOfMeasures;
    }

    public JsonArray getReportedFunction() {
        return reportedFunction;
    }

    @Override
    public String toString() {
        return "PackagingPickList{" +
                "partTypes=" + partTypes +
                ", materialTypes=" + materialTypes +
                ", componentTypes=" + componentTypes +
                ", unitOfMeasures=" + unitOfMeasures +
                ", reportedFunction=" + reportedFunction +
                '}';
    }
}
