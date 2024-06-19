package com.pg.dsm.preference.models;

import java.time.Instant;
import java.util.logging.Logger;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.util.IRMProjectSpacePreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;

public class IRMProjectSpacePreference {
    private static final Logger logger = Logger.getLogger(IRMProjectSpacePreference.class.getName());
    Context context;
    String user;
    String businessArea;
    String businessAreaID;
    String businessAreaDisplay;

    public IRMProjectSpacePreference(Context context) throws Exception {
        Instant startTime = Instant.now();
        this.context = context;
        this.user = context.getUser();
        PreferenceManagement preferenceManagement = new PreferenceManagement(this.context);
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        IRMProjectSpacePreferenceUtil projectSpacePreferenceUtil = new IRMProjectSpacePreferenceUtil();
        this.businessArea = projectSpacePreferenceUtil.getBusinessArea(this.context, preferenceManagement, userPreferenceUtil); // stores physical id.
        this.businessAreaID = projectSpacePreferenceUtil.getBusinessAreaID(this.context, userPreferenceUtil, this.businessArea);
        this.loadBusinessArea();
    }

    void loadBusinessArea() throws FrameworkException {
        IRMProjectSpacePreferenceUtil util = new IRMProjectSpacePreferenceUtil();
        this.businessAreaDisplay = util.getBusinessAreaJSON(this.context);
    }

    public Context getContext() {
        return context;
    }

    public String getUser() {
        return user;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public String getBusinessAreaDisplay() {
        return businessAreaDisplay;
    }

    public String getBusinessAreaID() {
        return businessAreaID;
    }

    @Override
    public String toString() {
        return "IRMProjectSpacePreference{" +
                "context=" + context +
                ", user='" + user + '\'' +
                ", businessArea='" + businessArea + '\'' +
                ", businessAreaID='" + businessAreaID + '\'' +
                ", businessAreaDisplay='" + businessAreaDisplay + '\'' +
                '}';
    }
}
