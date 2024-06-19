package com.pg.dsm.preference.template.repository;

import javax.json.JsonArray;

import com.pg.dsm.preference.CreateUserPreference;

import matrix.db.Context;
import matrix.util.MatrixException;

public class CommonPickList {

    JsonArray partCategory;
    JsonArray releaseCriteria;
    JsonArray classTypes;
    JsonArray segment;
    JsonArray businessArea;

    public CommonPickList(Context context) throws MatrixException {
        this.partCategory = CreateUserPreference.CommonPickLists.PART_CATEGORY.asJsonArray(context);
        this.releaseCriteria = CreateUserPreference.CommonPickLists.STRUCTURE_RELEASE_CRITERIA.asJsonArray(context);
        this.classTypes = CreateUserPreference.CommonPickLists.CLASS.asJsonArray(context);
        this.segment = CreateUserPreference.CommonPickLists.SEGMENT.asJsonArray(context);
        this.businessArea = CreateUserPreference.CommonPickLists.BUSINESS_AREA.asJsonArray(context);
    }

    public JsonArray getPartCategory() {
        return partCategory;
    }

    public JsonArray getReleaseCriteria() {
        return releaseCriteria;
    }

    public JsonArray getClassTypes() {
        return classTypes;
    }

    public JsonArray getSegment() {
        return segment;
    }

    public JsonArray getBusinessArea() {
        return businessArea;
    }

    @Override
    public String toString() {
        return "CommonPickList{" +
                "partCategory=" + partCategory +
                ", releaseCriteria=" + releaseCriteria +
                ", classTypes=" + classTypes +
                ", segment=" + segment +
                ", businessArea=" + businessArea +
                '}';
    }
}
