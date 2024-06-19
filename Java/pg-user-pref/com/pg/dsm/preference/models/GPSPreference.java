package com.pg.dsm.preference.models;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.preference.util.GPSPreferenceUtil;

import matrix.db.Context;

public class GPSPreference {
    String shareWithMemberJsonString;
    String shareWithMemberName;
    String shareWithMemberOID;
    String preTaskNotificationUserJsonString;
    String postTaskNotificationUserJsonString;

    public GPSPreference(Context context) throws FrameworkException {
        GPSPreferenceUtil preferenceUtil = new GPSPreferenceUtil();
        this.shareWithMemberJsonString = preferenceUtil.getShareWithMemberJsonString(context);
        this.preTaskNotificationUserJsonString = preferenceUtil.getPreTaskNotificationUserJsonString(context);
        this.postTaskNotificationUserJsonString = preferenceUtil.getPostTaskNotificationUserJsonString(context);
        this.shareWithMemberName = preferenceUtil.getShareWithMemberName(this.shareWithMemberJsonString);
        this.shareWithMemberOID = preferenceUtil.getShareWithMemberOID(this.shareWithMemberJsonString);
    }

    public String getShareWithMemberJsonString() {
        return shareWithMemberJsonString;
    }

    public String getShareWithMemberName() {
        return shareWithMemberName;
    }

    public String getShareWithMemberOID() {
        return shareWithMemberOID;
    }

    public String getPreTaskNotificationUserJsonString() {
        return preTaskNotificationUserJsonString;
    }

    public String getPostTaskNotificationUserJsonString() {
        return postTaskNotificationUserJsonString;
    }
}
