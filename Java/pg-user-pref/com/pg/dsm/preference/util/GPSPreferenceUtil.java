package com.pg.dsm.preference.util;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class GPSPreferenceUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void setUserPreference(Context context, String user, String key, String value) throws Exception {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        userPreferenceUtil.setUserPreference(context, user, key, value);
    }

    public String getSharingMember(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.GPS_PREFERRED_SHARE_WITH_MEMBERS.get());
    }

    public String getShareWithMemberJsonString(Context context) throws FrameworkException {
        return JSONUtil.asJSONString(getSharingMemberJsonArray(context), "output");
    }

    public String getShareWithMemberName(String shareWithMemberJsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(shareWithMemberJsonString));
        JsonObject shareWithMemberJsonObject = jsonReader.readObject();
        JsonArray shareWithMemberJsonArray = shareWithMemberJsonObject.getJsonArray("output");
        StringBuilder dataBuilder = new StringBuilder();
        JsonObject jsonObject;
        for (int i = 0; i < shareWithMemberJsonArray.size(); i++) {
            jsonObject = shareWithMemberJsonArray.getJsonObject(i);
            dataBuilder.append(jsonObject.getString(DomainConstants.SELECT_NAME).replaceAll("\"", ""));
            dataBuilder.append(PreferenceConstants.Basic.SYMBOL_PIPE.get());
        }
        if (UIUtil.isNotNullAndNotEmpty(dataBuilder.toString())) {
            dataBuilder.setLength(dataBuilder.length() - 1);
        }
        return dataBuilder.toString();
    }

    public String getShareWithMemberOID(String shareWithMemberJsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(shareWithMemberJsonString));
        JsonObject shareWithMemberJsonObject = jsonReader.readObject();
        JsonArray shareWithMemberJsonArray = shareWithMemberJsonObject.getJsonArray("output");
        StringBuilder dataBuilder = new StringBuilder();
        JsonObject jsonObject;
        for (int i = 0; i < shareWithMemberJsonArray.size(); i++) {
            jsonObject = shareWithMemberJsonArray.getJsonObject(i);
            dataBuilder.append(jsonObject.getString(DomainConstants.SELECT_ID).replaceAll("\"", ""));
            dataBuilder.append(PreferenceConstants.Basic.SYMBOL_PIPE.get());
        }
        if (UIUtil.isNotNullAndNotEmpty(dataBuilder.toString())) {
            dataBuilder.setLength(dataBuilder.length() - 1);
        }
        return dataBuilder.toString();
    }

    public MapList getSharingMemberInfoList(Context context) throws FrameworkException {
        String names = getSharingMember(context);
        return getMultipleObjectInfoByTypeAndName(context, DomainConstants.TYPE_PERSON, names, pgV3Constants.SYMBOL_PIPE);
    }

    public JsonArray getSharingMemberJsonArray(Context context) throws FrameworkException {
        MapList infoList = getSharingMemberInfoList(context);
        return JSONUtil.getMapListToJSONArray(infoList);
    }

    public String getPreTaskNotificationUser(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.GPS_PREFERRED_PRE_TASK_NOTIFICATION_USERS.get());
    }

    public MapList getPreTaskNotificationUserList(Context context) throws FrameworkException {
        String names = getPreTaskNotificationUser(context);
        return getMultipleObjectInfoByTypeAndName(context, DomainConstants.TYPE_PERSON, names, pgV3Constants.SYMBOL_PIPE);
    }

    public JsonArray getPreTaskNotificationUserJsonArray(Context context) throws FrameworkException {
        MapList infoList = getPreTaskNotificationUserList(context);
        return JSONUtil.getMapListToJSONArray(infoList);
    }

    public String getPreTaskNotificationUserJsonString(Context context) throws FrameworkException {
        return JSONUtil.asJSONString(getPreTaskNotificationUserJsonArray(context), "output");
    }

    public String getPostTaskNotificationUser(Context context) throws FrameworkException {
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        return userPreferenceUtil.getAdminPropertyWithoutCache(context, PreferenceConstants.Preferences.GPS_PREFERRED_POST_TASK_NOTIFICATION_USERS.get());
    }

    public MapList getPostTaskNotificationUserList(Context context) throws FrameworkException {
        String names = getPostTaskNotificationUser(context);
        return getMultipleObjectInfoByTypeAndName(context, DomainConstants.TYPE_PERSON, names, pgV3Constants.SYMBOL_PIPE);
    }

    public JsonArray getPostTaskNotificationUserJsonArray(Context context) throws FrameworkException {
        MapList infoList = getPostTaskNotificationUserList(context);
        return JSONUtil.getMapListToJSONArray(infoList);
    }

    public String getPostTaskNotificationUserJsonString(Context context) throws FrameworkException {
        return JSONUtil.asJSONString(getPostTaskNotificationUserJsonArray(context), "output");
    }

    public Map<Object, Object> getObjectInfoByTypeAndName(Context context, String type, String name) throws FrameworkException {
        Map<Object, Object> infoMap = new HashMap<>();
        try {
            MapList objectList = DomainObject.findObjects(context, // context
                    type.trim(),                                    // typePattern
                    name.trim(),                                   // name pattern
                    DomainConstants.QUERY_WILDCARD,         // revision pattern
                    DomainConstants.QUERY_WILDCARD,         // owner pattern
                    pgV3Constants.VAULT_ESERVICEPRODUCTION, // vault pattern
                    DomainConstants.EMPTY_STRING,           // where expression
                    false,                                  // expandType
                    StringList.create(DomainConstants.SELECT_ID, DomainConstants.SELECT_NAME, DomainConstants.SELECT_PHYSICAL_ID));// objectSelects

            if (null != objectList && !objectList.isEmpty()) {
                infoMap = (Map<Object, Object>) objectList.get(0);
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
            throw e;
        }
        return infoMap;
    }

    MapList getMultipleObjectInfoByTypeAndName(Context context, String type, String names, String separator) throws FrameworkException {
        MapList infoList = new MapList();
        if (UIUtil.isNotNullAndNotEmpty(names)) {
            StringList nameList = StringUtil.split(names, separator);
            if (null != nameList && !nameList.isEmpty()) {
                Map<Object, Object> info;
                for (String name : nameList) {
                    if (UIUtil.isNotNullAndNotEmpty(name)) {
                        info = getObjectInfoByTypeAndName(context, type, name);
                        if (null != info && !info.isEmpty()) {
                            infoList.add(info);
                        }
                    }
                }
            }
        }
        return infoList;
    }
}
