package com.pg.dsm.preference.util;

import java.util.logging.Logger;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.enumeration.PreferenceConstants;

import matrix.db.Context;

public class IRMAttributePreferenceUtil {
    private static final Logger logger = Logger.getLogger(IRMAttributePreferenceUtil.class.getName());

    public IRMAttributePreferenceUtil() {
    }

    public void setUserPreference(Context context, String user, String key, String value) throws Exception {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        userPreferenceUtil.setUserPreference(context, user, key, value);
    }

    public void setUserPreference(Context context, String key, String value) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        userPreferenceUtil.setUserPreference(context, key, value);
    }

    public String getBusinessArea(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.IRM_BUSINESS_AREA.get());
    }

    public String getBusinessArea(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_BUSINESS_AREA.get());
    }

    public String getBusinessAreaName(Context context, UserPreferenceUtil userPreferenceUtil, String businessArea) throws FrameworkException {
        return userPreferenceUtil.getPipeSeparatedNameFromID(context, businessArea);
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

    public String getTitle(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.IRM_PREFERRED_TITLE.get());
    }

    public String getTitle(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PREFERRED_TITLE.get());
    }

    public String getDescription(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.IRM_PREFERRED_DESCRIPTION.get());
    }

    public String getDescription(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PREFERRED_DESCRIPTION.get());
    }

    public String getPolicy(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.IRM_PREFERRED_POLICY.get());
    }

    public String getPolicy(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PREFERRED_POLICY.get());
    }

    public String getRegion(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.IRM_PREFERRED_REGION.get());
    }

    public String getRegion(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PREFERRED_REGION.get());
    }

    public String getClassification(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.IRM_PREFERRED_CLASSIFICATION.get());
    }

    public String getClassification(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PREFERRED_CLASSIFICATION.get());
    }

    public String getSharingMember(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.IRM_PREFERRED_SHARING_MEMBERS.get());
    }

    public String getSharingMember(Context context, PreferenceManagement preferenceManagement, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, preferenceManagement, PreferenceConstants.Preferences.IRM_PREFERRED_SHARING_MEMBERS.get());
    }

    public String getSharingMemberID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPreferredIRMSharingMembersID(context);
    }

    public String getSharingMemberPhysicalID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPreferredIRMSharingMembersPhysicalID(context);
    }

    public String getRegionID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPreferredIRMRegionID(context);
    }

    public String getRegionPhysicalID(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getPreferredIRMRegionPhysicalID(context);
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
