package com.pg.dsm.preference.template.repository;

import javax.json.JsonArray;

import com.pg.dsm.preference.CreateUserPreference;

import matrix.db.Context;
import matrix.util.MatrixException;

public class TechSpecPickList {
    JsonArray partTypes;

    public TechSpecPickList(Context context) throws MatrixException {
        this.partTypes = CreateUserPreference.TechSpecPickLists.PART_TYPE.asJsonArray(context);
    }

    public JsonArray getPartTypes() {
        return partTypes;
    }

    @Override
    public String toString() {
        return "TechSpecPickList{" +
                "partTypes=" + partTypes +
                '}';
    }
}
