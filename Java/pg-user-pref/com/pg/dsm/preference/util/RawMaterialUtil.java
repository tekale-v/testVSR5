package com.pg.dsm.preference.util;

import java.util.Map;

import javax.json.JsonArray;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class RawMaterialUtil {
    public String getPartTypeJsonString() throws FrameworkException {
        return JSONUtil.asJSONString(getPartTypeJsonArray(), PreferenceConstants.Basic.RAW_MATERIAL_TYPES.get());
    }

    public String getMaterialFunctionJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getMaterialFunctionJsonArray(context), PreferenceConstants.Basic.MATERIAL_FUNCTION.get());
    }

    public String getReportedFunctionJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getReportedFunctionJsonArray(context), PreferenceConstants.Basic.REPORTED_FUNCTION.get());
    }

    public JsonArray getPartTypeJsonArray() throws FrameworkException {
        return JSONUtil.asJSONArrayWithBlank(getPartTypesRange());
    }

    public JsonArray getMaterialFunctionJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getMaterialFunctionRange(context));
    }

    public JsonArray getReportedFunctionJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getReportedFunctionRange(context));
    }

    public Map<String, StringList> getPartTypesRange() {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPartTypes(UserPreferenceTemplateConstants.Basic.RAW_MATERIAL_TYPES_CONFIGURATION.get());
    }

    public Map<String, StringList> getMaterialFunctionRange(Context context) throws MatrixException {
        return JPO.invoke(context, "pgUserPreferencesCustom", null, "getMaterialFunctionRange", new String[]{}, Map.class);
    }

    public Map<String, StringList> getReportedFunctionRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        // raw-materials have same reported functions, so get it by passing any rm type.
        return userPreferenceUtil.getReportedFunctionRangeByGivenType(context, pgV3Constants.TYPE_RAWMATERIALPART);
    }

}
