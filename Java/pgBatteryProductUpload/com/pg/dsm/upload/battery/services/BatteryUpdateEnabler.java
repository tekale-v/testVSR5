/*
 **   BatteryUpdateEnabler.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to enable battery data load.
 **
 */
package com.pg.dsm.upload.battery.services;

import com.pg.dsm.upload.battery.interfaces.Product;
import com.pg.dsm.upload.battery.models.config.BatteryProduct;
import com.pg.dsm.upload.battery.resources.BatteryConfigPreChecker;
import com.pg.dsm.upload.battery.resources.BatteryPropertyConfig;
import matrix.db.Context;
import org.apache.log4j.Logger;

import java.util.List;

public class BatteryUpdateEnabler {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    BatteryConfigPreChecker batteryConfigPreChecker;
    BatteryPropertyConfig batteryPropertyConfig;
    BatteryProduct batteryProductConfig;

    private BatteryUpdateEnabler(Builder builder) {
        this.context = builder.context;
        this.batteryConfigPreChecker = builder.batteryConfigPreChecker;
        this.batteryPropertyConfig = builder.batteryPropertyConfig;
        this.batteryProductConfig = builder.batteryProductConfig;
    }

    /**
     * Method to perform battery data load.
     *
     * @return void
     * @since DSM 2018x.6
     */
    public void perform() {
        String inputFileNamePath = batteryPropertyConfig.getInputFileNamePath();

        if (batteryPropertyConfig.getExcelTabDPP().equals(batteryConfigPreChecker.getBatteryProductIdentifier())) {
            logger.info("Input Excel File is for DPP: " + batteryPropertyConfig.getInputFileNamePath());
            Product product = new DeviceProductPart(context, batteryPropertyConfig, batteryProductConfig);
            logger.info("Convert Excel to Bean");
            List<BatteryProduct> batteryProductList = product.getExcelDataBean(product.getProductPartBeanMap(product.searchObject(batteryProductConfig.getSchema(), product.getNameRevisionFromExcel())));
            logger.info("Perform Excel Data Verification & Validation");
            product.performValidations(batteryProductList);
            if (product.isVerificationPassed(batteryProductList)) {
                logger.info("Excel Data Validations Passed. Go ahead and create RESTORE Excel File for Input Excel File " + inputFileNamePath);
                product.createRestoreExcel(batteryProductList);
                logger.info("Go ahead and UPDATE the database for Input Excel File " + inputFileNamePath);
                product.updateProductFeatures(batteryProductList);
                logger.info("All Records in the given Input Excel File are Processed Successfully " + inputFileNamePath);
                product.moveGivenInputExcelFile(batteryPropertyConfig.getProcessedFolder());
            } else {
                logger.error("Excel Data Validation(s) Failed. None of the records were processed. Input Excel File was:" + inputFileNamePath);
                logger.error("Please refer the ERROR Excel File. Correct all the Validation Error(s) and re-run the shell. Input Excel File was:" + inputFileNamePath);
                product.createErrorExcel(batteryProductList);
                product.moveGivenInputExcelFile(batteryPropertyConfig.getRetryFolder());
            }
        }
        if (batteryPropertyConfig.getExcelTabRM().equals(batteryConfigPreChecker.getBatteryProductIdentifier())) {
            logger.info("Input Excel File is for RM: " + inputFileNamePath);
            Product product = new RawMaterial(context, batteryPropertyConfig, batteryProductConfig);
            logger.info("Convert Excel to Bean");
            List<BatteryProduct> batteryProductList = product.getExcelDataBean(product.getProductPartBeanMap(product.searchObject(batteryProductConfig.getSchema(), product.getNameRevisionFromExcel())));
            logger.info("Perform Excel Data Verification & Validation");
            product.performValidations(batteryProductList);
            if (product.isVerificationPassed(batteryProductList)) {
                logger.info("Excel Data Validations Passed. Go ahead and create RESTORE Excel File for Input Excel File " + inputFileNamePath);
                product.createRestoreExcel(batteryProductList);
                logger.info("Go ahead and UPDATE the database for Input Excel File " + inputFileNamePath);
                product.updateProductFeatures(batteryProductList);
                logger.info("All Records in the given Input Excel File are Processed Successfully " + inputFileNamePath);
                product.moveGivenInputExcelFile(batteryPropertyConfig.getProcessedFolder());
            } else {
                logger.error("Excel Data Validation(s) Failed. None of the records were processed. Input Excel File was:" + inputFileNamePath);
                logger.error("Please refer the ERROR Excel File. Correct all the Validation Error(s) and re-run the shell. Input Excel File was:" + inputFileNamePath);
                product.createErrorExcel(batteryProductList);
                product.moveGivenInputExcelFile(batteryPropertyConfig.getRetryFolder());
            }
        }
    }

    public static class Builder {

        private final Logger logger = Logger.getLogger(this.getClass().getName());

        BatteryPropertyConfig batteryPropertyConfig;
        Context context;
        BatteryConfigPreChecker batteryConfigPreChecker;
        BatteryProduct batteryProductConfig;

        public Builder() {
            logger.info("Constructor");
        }

        public Builder setBatteryPropertyConfig(BatteryPropertyConfig batteryPropertyConfig) {
            this.batteryPropertyConfig = batteryPropertyConfig;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setBatteryConfigPreChecker(BatteryConfigPreChecker batteryConfigPreChecker) {
            this.batteryConfigPreChecker = batteryConfigPreChecker;
            return this;
        }

        public Builder setBatteryProductConfig(BatteryProduct batteryProductConfig) {
            this.batteryProductConfig = batteryProductConfig;
            return this;
        }

        public BatteryUpdateEnabler build() {
            return new BatteryUpdateEnabler(this);
        }
    }
}
