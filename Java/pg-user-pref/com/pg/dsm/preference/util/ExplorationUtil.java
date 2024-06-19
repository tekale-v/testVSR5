package com.pg.dsm.preference.util;

import java.util.Map;

import javax.json.JsonArray;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;

import matrix.util.StringList;

public class ExplorationUtil {
    public String getPartTypeJsonString() throws FrameworkException {
        return JSONUtil.asJSONString(getPartTypeJsonArray(), PreferenceConstants.Basic.EXPLORATION_TYPES.get());
    }

    public JsonArray getPartTypeJsonArray() throws FrameworkException {
        return JSONUtil.asJSONArrayWithBlank(getPartTypesRange());
    }

    public Map<String, StringList> getPartTypesRange() {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPartTypes(UserPreferenceTemplateConstants.Basic.EXPLORATION_TYPES_CONFIGURATION.get());
    }
}
