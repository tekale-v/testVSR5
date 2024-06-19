package com.pg.dsm.preference.models;

import java.util.logging.Logger;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.preference.Preferences;

import matrix.db.Context;
import matrix.util.MatrixException;

public class CopyDataPreference {
    private static final Logger logger = Logger.getLogger(CopyDataPreference.class.getName());
    Context context;

    public CopyDataPreference(Context context) {
        this.context = context;
    }

    public MapList getCopyDataProduct() throws MatrixException {
        return Preferences.CopyDataPreference.PRODUCT.getCopyData(this.context);
    }

    public MapList getCopyDataPackaging() throws MatrixException {
        return Preferences.CopyDataPreference.PACKAGING.getCopyData(this.context);
    }

    public MapList getCopyDataRawMaterial() throws MatrixException {
        return Preferences.CopyDataPreference.RAW_MATERIAL.getCopyData(this.context);
    }

    public MapList getCopyDataTechSpec() throws MatrixException {
        return Preferences.CopyDataPreference.TECHNICAL_SPEC.getCopyData(this.context);
    }

    public MapList getCopyDataAll() throws MatrixException {
        return Preferences.CopyDataPreference.ALL.getCopyData(this.context);
    }
}
