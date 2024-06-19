package com.pg.dsm.preference.interfaces;

import java.util.List;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.preference.models.Item;

import matrix.db.Context;
import matrix.util.MatrixException;

public interface ICopyDataPreferenceRepository {

    MapList getAllFilterDataList(Context context) throws MatrixException;

    MapList getPackagingFilterDataList(Context context) throws MatrixException;

    MapList getProductFilterDataList(Context context) throws MatrixException;

    MapList getRawMaterialFilterDataList(Context context) throws MatrixException;

    MapList getTechnicalSpecificationFilterDataList(Context context) throws MatrixException;

    List<Item> getAllFilterData(Context context) throws MatrixException;

    List<Item> getPackagingFilterData(Context context) throws MatrixException;

    List<Item> getProductFilterData(Context context) throws MatrixException;

    List<Item> getRawMaterialFilterData(Context context) throws MatrixException;

    List<Item> getTechnicalSpecificationFilterData(Context context) throws MatrixException;
}
