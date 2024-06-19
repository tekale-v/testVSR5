/*
 **   DeviceProductPartVerifier.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to hold all validation methods for DPP.
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

public class DeviceProductPartVerifier {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    BatteryProduct batteryProduct;
    BatteryProductFeature batteryProductFeature;
    BatteryPropertyConfig batteryPropertyConfig;


    public DeviceProductPartVerifier(BatteryProduct batteryProduct, BatteryProductFeature batteryProductFeature, BatteryPropertyConfig batteryPropertyConfig) {
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
            checkList.add(batteryProductFeature.getName().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorGCASLengthExceedsMessage()));
        }
        return checkList;
    }

    /**
     * Method to validate power source.
     *
     * @return List<String> - error message list.
     * @since DSM 2018x.6
     */
    @SuppressWarnings("unused")
    public List<String> validatePowerSource() {
        List<String> checkList = new ArrayList<>();
        String powerSource = batteryProductFeature.getValue();

        if (batteryProductFeature.isPicklist()) {
            boolean isPowerSourceValueContainsInAllowedRanges = (StringUtil.split(batteryPropertyConfig.getPowerSourceRangeValues(), BatteryConstants.Basic.SYMBOL_PIPE.getValue())).contains(powerSource);
            // if value is not present in picklist && does not contains in allowed power source picklist range.
            if (!batteryProductFeature.getPicklistNames().contains(powerSource) && !isPowerSourceValueContainsInAllowedRanges) {
                checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessagePowerSourceRangeValueMismatch()));
            }
        }
        logger.info("Validate - Power Source: " + powerSource);
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
        String powerSource = batteryProduct.getPowerSource();
        String batteryType = batteryProductFeature.getValue();
        boolean isPowerSourceValueContainsInAllowedRanges = (StringUtil.split(batteryPropertyConfig.getPowerSourceAllowedRangeValues(), BatteryConstants.Basic.SYMBOL_PIPE.getValue())).contains(powerSource);
        boolean isPowerSourceValueContainsInNotAllowedRanges = (StringUtil.split(batteryPropertyConfig.getPowerSourceNotAllowedRanges(), BatteryConstants.Basic.SYMBOL_PIPE.getValue())).contains(powerSource);
        // input value for battery type must be one of the value of the picklist.
        if (UIUtil.isNullOrEmpty(batteryType) && isPowerSourceValueContainsInAllowedRanges) {
            checkList.add(batteryPropertyConfig.getErrorMessageBatteryTypeValueEmpty());
        } else if (batteryProductFeature.isPicklist() && !batteryProductFeature.getPicklistNames().contains(batteryType) && isPowerSourceValueContainsInAllowedRanges) {
            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteryTypeValueDoesNotExist()));
        } else if (UIUtil.isNotNullAndNotEmpty(batteryType) && isPowerSourceValueContainsInNotAllowedRanges) {
            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup().concat(powerSource)));
        }
        logger.info("Validate - Battery Type: " + batteryType);
        return checkList;
    }

    /**
     * Method to validate number of batteries shipped in device (to be numeric).
     *
     * @return List<String> - error message list.
     * @since DSM 2018x.6
     */
    @SuppressWarnings("unused")
    public List<String> validateNumberOfBatteriesShippedInsideDevice() {
        List<String> checkList = new ArrayList<>();
        String numberOfBatteriesShippedInsideDevice = batteryProduct.getNumberOfBatteriesShippedInsideDevice();
        logger.info("Validate - Number of cells/batteries shipped inside Device: " + numberOfBatteriesShippedInsideDevice);
        //Remove validations except numeric check        
        if (UIUtil.isNotNullAndNotEmpty(numberOfBatteriesShippedInsideDevice) && !isNumeric(numberOfBatteriesShippedInsideDevice)) {
            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteriesShippedInsideValueMustBeNumeric()));
        }
        return checkList;
    }

    /**
     * Method to validate number of batteries required (to be numeric).
     *
     * @return List<String> - error message list.
     * @since DSM 2018x.6
     */
    @SuppressWarnings("unused")
    public List<String> validateNumberOfBatteriesRequired() {
        List<String> checkList = new ArrayList<>();
        String numberOfBatteriesRequired = batteryProduct.getNumberOfBatteriesRequired();
        String powerSource = batteryProduct.getPowerSource();
        String value = batteryProductFeature.getValue();
        logger.info("Validate - Number of Batteries Required: " + value);
        //60 - RU
        if (UIUtil.isNullOrEmpty(numberOfBatteriesRequired)
                && (StringUtil.split(batteryPropertyConfig.getPowerSourceAllowedRangeValues(), BatteryConstants.Basic.SYMBOL_PIPE.getValue())).contains(powerSource)) {

            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteriesRequiredValueMandatoryWhenPowerSource().concat(powerSource)));

        } else if (UIUtil.isNotNullAndNotEmpty(numberOfBatteriesRequired)
                && (StringUtil.split(batteryPropertyConfig.getPowerSourceAllowedRangeValues(), BatteryConstants.Basic.SYMBOL_PIPE.getValue())).contains(powerSource)
                && !isNumeric(numberOfBatteriesRequired)) {

            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteriesRequiredValueMustBeNumeric()));

        } else if (UIUtil.isNotNullAndNotEmpty(numberOfBatteriesRequired) && ((StringUtil.split(batteryPropertyConfig.getPowerSourceNotAllowedRanges(), BatteryConstants.Basic.SYMBOL_PIPE.getValue())).contains(powerSource))) {

            checkList.add(batteryProductFeature.getValue().concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup().concat(powerSource)));
        }

        // to-do
        // entered value must be an integer, else add error message
        return checkList;
    }
    /**
     * Method to validate number of batteries required (to be numeric).
     *
     * @return List<String> - error message list.
     * @since DSM 2018x.6
     */
    @SuppressWarnings("unused")
    public List<String> validateNetWeight() {
        List<String> checkList = new ArrayList<>();
        String value = batteryProductFeature.getValue();
        logger.info(String.format("Validate - Net Weight: %s",value));
        if (UIUtil.isNotNullAndNotEmpty(value) && !isDouble(value)) {
            checkList.add(value.concat(BatteryConstants.Basic.SYMBOL_COLON.getValue()).concat(batteryPropertyConfig.getErrorMessageNetWeightValueMustBeNumeric()));
        }
        return checkList;
    }
    public boolean isDouble(String value) {
        boolean bNumber = true;
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            bNumber = false;
        }
        return bNumber;
    }

    public boolean isNumeric(String value) {
        boolean bNumber = true;
        try {
            // checking valid integer using parseInt() method 
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            bNumber = false;
        }
        return bNumber;
    }
}
