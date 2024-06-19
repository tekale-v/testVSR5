package com.pg.dsm.preference.repository;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.interfaces.IPlantDataPreferenceRepository;

import matrix.db.Context;
import matrix.util.MatrixException;

public class PlantDataPreferenceRepository implements IPlantDataPreferenceRepository {
    @Override
    public MapList getAllPlantData(Context context) throws MatrixException {
        return Preferences.PlantDataPreference.ALL.getPlantData(context);
    }

    @Override
    public MapList getPackagingPlantData(Context context) throws MatrixException {
        return Preferences.PlantDataPreference.PACKAGING.getPlantData(context);
    }

    @Override
    public MapList getProductPlantData(Context context) throws MatrixException {
        return Preferences.PlantDataPreference.PRODUCT.getPlantData(context);
    }

    @Override
    public MapList getRawMaterialPlantData(Context context) throws MatrixException {
        return Preferences.PlantDataPreference.RAW_MATERIAL.getPlantData(context);
    }

    @Override
    public MapList getTechnicalSpecificationPlantData(Context context) throws MatrixException {
        return Preferences.PlantDataPreference.TECHNICAL_SPEC.getPlantData(context);
    }
}
