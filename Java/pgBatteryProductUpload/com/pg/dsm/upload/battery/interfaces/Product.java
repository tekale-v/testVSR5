/*
 **   Product.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Interface for battery data load.
 **
 */
package com.pg.dsm.upload.battery.interfaces;

import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.upload.battery.models.config.BatteryProduct;

import java.util.List;
import java.util.Map;

public interface Product {
    Map<String, List<String>> getNameRevisionFromExcel();

    MapList searchObject(String type, Map<String, List<String>> nameRevisionMap);

    Map<String, ProductPartBean> getProductPartBeanMap(MapList objectList);

    List<BatteryProduct> getExcelDataBean(Map<String, ProductPartBean> productPartBeanMap);

    void performValidations(List<BatteryProduct> batteryProductList);

    void createRestoreExcel(List<BatteryProduct> batteryProductList);

    boolean isVerificationPassed(List<BatteryProduct> batteryProductList);

    void createErrorExcel(List<BatteryProduct> batteryProductList);

    void updateProductFeatures(List<BatteryProduct> batteryProductList);

    void moveGivenInputExcelFile(String destinationFolderName);
}
