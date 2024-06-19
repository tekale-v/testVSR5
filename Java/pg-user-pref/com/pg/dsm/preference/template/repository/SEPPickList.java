package com.pg.dsm.preference.template.repository;

import javax.json.JsonArray;

import com.pg.dsm.preference.CreateUserPreference;

import matrix.db.Context;
import matrix.util.MatrixException;

public class SEPPickList {
    JsonArray partTypes;

    public SEPPickList(Context context) throws MatrixException {
        this.partTypes = CreateUserPreference.SEPPickLists.PART_TYPE.asJsonArray(context);
    }

    public JsonArray getPartTypes() {
        return partTypes;
    }

    @Override
    public String toString() {
        return "SEPPickList{" +
                "partTypes=" + partTypes +
                '}';
    }
}
