package com.pg.dsm.preference.util;

import java.util.Map;

import javax.json.JsonArray;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ProductUtil {
    public String getPartTypeJsonString() throws FrameworkException {
        return JSONUtil.asJSONString(getPartTypeJsonArray(), PreferenceConstants.Basic.PRODUCT_TYPES.get());
    }

    public String getProductComplianceJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getProductComplianceJsonArray(context), PreferenceConstants.Basic.PRODUCT_COMPLIANCE.get());
    }

    public String getReportedFunctionString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getReportedFunctionArray(context), PreferenceConstants.Basic.REPORTED_FUNCTION.get());
    }

    public JsonArray getProductComplianceJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getProductComplianceRange(context));
    }

    public JsonArray getReportedFunctionArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getReportedFunctionRange(context));
    }

    public JsonArray getPartTypeJsonArray() throws FrameworkException {
        return JSONUtil.asJSONArrayWithBlank(getPartTypesRange());
    }

    public Map<String, StringList> getReportedFunctionRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getReportedFunctionRangeByGivenType(context, pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
    }


    public Map<String, StringList> getProductComplianceRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getProductComplianceRequiredRanges(context);
    }

    public Map<String, StringList> getPartTypesRange() {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPartTypes(UserPreferenceTemplateConstants.Basic.PRODUCT_TYPES_CONFIGURATION.get());
    }
}
