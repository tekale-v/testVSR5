package com.pg.dsm.preference.services;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.preference.models.DSMUserPreferenceConfig;
import com.pg.dsm.preference.repository.DSMUserPreferenceConfigRepository;

import matrix.db.Context;

public class DSMUserPreferenceConfigService {

    public DSMUserPreferenceConfigService() {
    }

    public DSMUserPreferenceConfig getDSMUserPreferenceConfig(Context context) throws FrameworkException {
        DSMUserPreferenceConfigRepository repository = new DSMUserPreferenceConfigRepository();
        return repository.getDSMUserPreferenceConfig(context);
    }
}
