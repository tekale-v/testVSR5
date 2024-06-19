package com.pg.dsm.preference.util;

import java.util.Map;

import javax.json.JsonArray;

import com.pg.designtools.util.CacheManagement;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PackagingUtil {

    public String getPartTypesJsonString() {
        return JSONUtil.asJSONString(getPartTypesJsonArray(), PreferenceConstants.Basic.PACKAGING_TYPES.get());
    }

    public String getMaterialTypeJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getMaterialTypeJsonArray(context), PreferenceConstants.Basic.MATERIAL_TYPES.get());
    }

    public String getComponentTypeJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getComponentTypeJsonArray(context), PreferenceConstants.Basic.COMPONENT_TYPES.get());
    }

    public String getBaseUnitOfMeasureJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getBaseUnitOfMeasureJsonArray(context), PreferenceConstants.Basic.UNIT_OF_MEASURES.get());
    }

    public String getReportedFunctionJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getReportedFunctionJsonArray(context), PreferenceConstants.Basic.REPORTED_FUNCTION.get());
    }

    public JsonArray getPartTypesJsonArray() {
        return JSONUtil.asJSONArrayWithBlank(getPartTypesRange());
    }

    public JsonArray getMaterialTypeJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getMaterialTypeRange(context));
    }

    public JsonArray getComponentTypeJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getComponentTypeRange(context));
    }

    public JsonArray getBaseUnitOfMeasureJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getBaseUnitOfMeasureRange(context));
    }

    public JsonArray getReportedFunctionJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getReportedFunctionRange(context));
    }

    public Map<String, StringList> getPartTypesRange() {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPartTypes(UserPreferenceTemplateConstants.Basic.PACKAGING_TYPES_CONFIGURATION.get());
    }

    public Map<String, StringList> getMaterialTypeRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPackagingMaterialType(context, new CacheManagement(context));
    }

    public Map<String, StringList> getComponentTypeRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPackagingComponentType(context);
    }


    public Map<String, StringList> getBaseUnitOfMeasureRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getBaseUnitOfMeasure(context);
    }

    public Map<String, StringList> getReportedFunctionRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        // reported function is same for all packaging types, so pass any pkg type to get it.
        return userPreferenceUtil.getReportedFunctionRangeByGivenType(context, pgV3Constants.TYPE_PACKAGINGMATERIALPART);
    }

}
