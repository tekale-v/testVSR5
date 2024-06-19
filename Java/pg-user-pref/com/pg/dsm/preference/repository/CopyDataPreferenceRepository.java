package com.pg.dsm.preference.repository;

import java.util.List;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.interfaces.ICopyDataPreferenceRepository;
import com.pg.dsm.preference.models.Item;

import matrix.db.Context;
import matrix.util.MatrixException;

public class CopyDataPreferenceRepository implements ICopyDataPreferenceRepository {
    @Override
    public MapList getAllFilterDataList(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.ALL.getCopyData(context);
    }

    @Override
    public MapList getPackagingFilterDataList(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.PACKAGING.getCopyData(context);
    }

    @Override
    public MapList getProductFilterDataList(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.PRODUCT.getCopyData(context);
    }

    @Override
    public MapList getRawMaterialFilterDataList(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.RAW_MATERIAL.getCopyData(context);
    }

    @Override
    public MapList getTechnicalSpecificationFilterDataList(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.TECHNICAL_SPEC.getCopyData(context);
    }

    @Override
    public List<Item> getAllFilterData(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.ALL.getCopyData(context);
    }

    @Override
    public List<Item> getPackagingFilterData(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.PACKAGING.getCopyData(context);
    }

    @Override
    public List<Item> getProductFilterData(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.PRODUCT.getCopyData(context);
    }

    @Override
    public List<Item> getRawMaterialFilterData(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.RAW_MATERIAL.getCopyData(context);
    }

    @Override
    public List<Item> getTechnicalSpecificationFilterData(Context context) throws MatrixException {
        return Preferences.CopyDataPreference.TECHNICAL_SPEC.getCopyData(context);
    }
}
