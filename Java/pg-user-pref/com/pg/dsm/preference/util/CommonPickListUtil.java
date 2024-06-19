package com.pg.dsm.preference.util;

import java.util.Map;
import java.util.logging.Logger;

import javax.json.JsonArray;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.util.CacheManagement;
import com.pg.dsm.preference.enumeration.PreferenceConstants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CommonPickListUtil {
    private static final Logger logger = Logger.getLogger(CommonPickListUtil.class.getName());

    public String getReleaseCriteriaJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getReleaseCriteriaJsonArray(context), "releaseCriteria");
    }

    public String getClassesJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getClassesJsonArray(context), "classes");
    }

    public String getSegmentJsonString(Context context) throws MatrixException {
        return JSONUtil.asJSONString(getSegmentJsonArray(context), "segment");
    }

    public String getBusinessAreaJsonString(Context context) throws FrameworkException {
        return JSONUtil.asJSONString(getBusinessAreaJsonArray(context), "businessArea");
    }

    public String getPartCategoryJsonString() {
        return JSONUtil.asJSONString(getPartCategoryJsonArray(), PreferenceConstants.Basic.PART_CATEGORY.get());
    }

    public JsonArray getPartCategoryJsonArray() {
        return JSONUtil.asJSONArrayWithBlank(getPartCategoryRange());
    }

    public JsonArray getReleaseCriteriaJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getReleaseCriteriaRange(context));
    }

    public JsonArray getClassesJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getClassesRange(context));
    }

    public JsonArray getSegmentJsonArray(Context context) throws MatrixException {
        return JSONUtil.asJSONArray(getSegmentRange(context));
    }

    public JsonArray getBusinessAreaJsonArray(Context context) throws FrameworkException {
        return JSONUtil.asJSONArray(getBusinessAreaRange(context));
    }

    public Map<String, StringList> getReleaseCriteriaRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getReleaseCriteriaStatus(context);
    }

    public Map<String, StringList> getSegmentRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getSegment(context, new CacheManagement(context));
    }

    public Map<String, StringList> getBusinessAreaRange(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getBusinessAreaRanges(context);
    }

    public Map<String, StringList> getClassesRange(Context context) throws MatrixException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getClasses(context);
    }

    public Map<String, StringList> getPartCategoryRange() {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPartCategoryRange();
    }
}
