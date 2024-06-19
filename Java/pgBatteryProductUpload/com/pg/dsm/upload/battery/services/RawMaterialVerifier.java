/*
 **   RawMaterialVerifier.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to hold all validation methods for RM.
 **
 */
package com.pg.dsm.upload.battery.services;

import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.models.config.BatteryProduct;
import com.pg.dsm.upload.battery.models.config.BatteryProductFeature;
import com.pg.dsm.upload.battery.resources.BatteryPropertyConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RawMaterialVerifier {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    BatteryProduct batteryProduct;
    BatteryProductFeature batteryProductFeature;
    BatteryPropertyConfig batteryPropertyConfig;

    public RawMaterialVerifier(BatteryProduct batteryProduct, BatteryProductFeature batteryProductFeature, BatteryPropertyConfig batteryPropertyConfig) {
        this.batteryProduct = batteryProduct;
        this.batteryProductFeature = batteryProductFeature;
        this.batteryPropertyConfig = batteryPropertyConfig;
    }

    /**
     * Method to validate part name.
     *
     * @return List<String> - error message list.
     * @since DSM 2018x.6
     */
    @SuppressWarnings("unused")
    public List<String> validateName() {
        List<String> checkList = new ArrayList<>();
        String value = batteryProductFeature.getValue();
        if (value.length() > 8) {
            checkList.add(batteryProductFeature.getName().concat(batteryPropertyConfig.getErrorGCASLengthExceedsMessage()));
        }
        return checkList;
    }

    /**
     * Method to validate is battery.
     *
     * @return List<String> - error message list.
     * @since DSM 2018x.6
     */
    @SuppressWarnings("unused")
    public List<String> validateIsBattery() {
        List<String> checkList = new ArrayList<>();
        String isBattery = batteryProduct.getIsBattery();
        String value = batteryProductFeature.getValue();
        logger.info("Validate - Is Battery: " + value);
        
        boolean isBatteryValue = (StringUtil.split(batteryPropertyConfig.getIsBatteryValues(), BatteryConstants.Basic.SYMBOL_PIPE.getValue())).contains(isBattery);
        
        if (!isBatteryValue) {
            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageIsBatteryValueShouldBeYesOrNo()));
        }
        return checkList;
    }

    /**
     * Method to validate battery type.
     *
     * @return List<String> - error message list.
     * @since DSM 2018x.6
     */
    @SuppressWarnings("unused")
    public List<String> validateBatteryType() {
        List<String> checkList = new ArrayList<>();
        String isBattery = batteryProduct.getIsBattery();
        String batteryType = batteryProduct.getBatteryType();
        String value = batteryProductFeature.getValue();
        if (BatteryConstants.Basic.VALUE_NO.getValue().equalsIgnoreCase(isBattery) && UIUtil.isNotNullAndNotEmpty(batteryType)) {
            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs()));
        }
        if (BatteryConstants.Basic.VALUE_YES.getValue().equalsIgnoreCase(isBattery) && batteryProductFeature.isPicklist() && !batteryProductFeature.getPicklistNames().contains(value)) {
            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteryTypeValueDoesNotExist()));
        }
        logger.info("Validate - Battery Type: " + value);
        return checkList;
    }
}
