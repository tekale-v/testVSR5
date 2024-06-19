package com.pg.dsm.preference.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.enumeration.PreferenceConstants;

import matrix.db.Context;
import matrix.util.StringList;

public class IRMProjectSpacePreferenceUtil {
    private static final Logger logger = Logger.getLogger(IRMProjectSpacePreferenceUtil.class.getName());

    public IRMProjectSpacePreferenceUtil() {
    }

    public void setUserPreference(Context context, String user, String key, String value) throws Exception {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        userPreferenceUtil.setUserPreference(context, user, key, value);
    }

    public void setUserPreference(Context context, String key, String value) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        userPreferenceUtil.setUserPreference(context, key, value);
    }

    /**
     * @param context
     * @param preferenceManagement
     * @param userPreferenceUtil
     * @return
     * @throws FrameworkException
     */
    public String getBusinessArea(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PROJECT_SPACE_BUSINESS_AREA.get());
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getBusinessArea(Context context) throws FrameworkException {
        PreferenceManagement preferenceManagement = new PreferenceManagement(context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PROJECT_SPACE_BUSINESS_AREA.get());
    }

    public String getBusinessAreaID(Context context, UserPreferenceUtil userPreferenceUtil, String businessArea) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedObjectIDFromPhysicalID(context, businessArea);
    }

    public String getBusinessAreaID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPipeSeparatedObjectIDFromPhysicalID(context, getBusinessArea(context));
    }

    public String getBusinessAreaPhysicalID(Context context) throws FrameworkException {
        return getBusinessArea(context);
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public Map<String, StringList> getBusinessAreaRanges(Context context) throws FrameworkException {
        Map<String, StringList> rangeMap = new HashMap<>();
        StringList fieldDisplayChoices = new StringList();
        StringList fieldChoices = new StringList();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        MapList resultList = userPreferenceUtil.findBusinessArea(context);
        if (null != resultList && !resultList.isEmpty()) {
            resultList.sort("name", "ascending", "String");
            Iterator iterator = resultList.iterator();
            Map<Object, Object> resultMap;
            while (iterator.hasNext()) {
                resultMap = (Map<Object, Object>) iterator.next();
                fieldDisplayChoices.add((String) resultMap.get(DomainConstants.SELECT_NAME));
                fieldChoices.add((String) resultMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get()));
            }
        }
        rangeMap.put(DataConstants.CONST_FIELD_DISPLAY_CHOICES, fieldDisplayChoices);
        rangeMap.put(DataConstants.CONST_FIELD_CHOICES, fieldChoices);
        return rangeMap;
    }

    /**
     * @param context
     * @return
     * @throws FrameworkException
     */
    public String getBusinessAreaJSON(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getBusinessAreaJSON(context);
    }
}
