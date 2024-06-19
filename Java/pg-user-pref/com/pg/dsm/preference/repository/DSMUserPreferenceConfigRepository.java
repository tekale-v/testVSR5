package com.pg.dsm.preference.repository;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.preference.models.DSMUserPreferenceConfig;
import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;

public class DSMUserPreferenceConfigRepository {
    Context context;

    public DSMUserPreferenceConfigRepository() {
    }

    public DSMUserPreferenceConfig getDSMUserPreferenceConfig(Context context) throws FrameworkException {
        UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
        return preferenceUtil.getDSMUserPreferenceConfig(context);
    }
}
