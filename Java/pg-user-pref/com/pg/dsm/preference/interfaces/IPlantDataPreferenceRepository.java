package com.pg.dsm.preference.interfaces;

import com.matrixone.apps.domain.util.MapList;

import matrix.db.Context;
import matrix.util.MatrixException;

public interface IPlantDataPreferenceRepository {
    MapList getAllPlantData(Context context) throws MatrixException;

    MapList getPackagingPlantData(Context context) throws MatrixException;

    MapList getProductPlantData(Context context) throws MatrixException;

    MapList getRawMaterialPlantData(Context context) throws MatrixException;

    MapList getTechnicalSpecificationPlantData(Context context) throws MatrixException;
}
