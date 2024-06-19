/*
 **   BatteryProductFileProcessor.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Intermediate Class to perform battery data load.
 **
 */
package com.pg.dsm.upload.battery.services;

import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.models.config.BatteryProduct;
import com.pg.dsm.upload.battery.models.config.BatteryProducts;
import com.pg.dsm.upload.battery.resources.BatteryConfigPreChecker;
import com.pg.dsm.upload.battery.resources.BatteryPropertyConfig;
import com.pg.dsm.upload.battery.resources.BatteryPropertyFileResource;
import com.pg.dsm.upload.battery.resources.MatrixResource;
import com.pg.dsm.upload.battery.util.MatrixUtil;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class BatteryProductFileProcessor {
    public static final Logger logger = Logger.getLogger(BatteryProductFileProcessor.class.getName());

    String inputFileName;

    public BatteryProductFileProcessor(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    /**
     * Method to process battery data load for given file or input folder.
     *
     * @return void
     * @since DSM 2018x.6
     */
    public void process() {

        // load custom property file.
        BatteryPropertyConfig batteryPropertyConfig = new BatteryPropertyConfig(inputFileName, new BatteryPropertyFileResource.Builder().build().getProperties());

        // initialize custom logger.
        initializeInputFileSpecificLogger(batteryPropertyConfig);

        logger.info("home directory:" + batteryPropertyConfig.getHomeDirectory());
        logger.info("config directory: " + batteryPropertyConfig.getConfigFolder());
        logger.info("custom config xml file: " + batteryPropertyConfig.getConfigXMLFile());
        logger.info("input excel file name:" + batteryPropertyConfig.getInputFileName());
        logger.info("input excel file name with path:" + batteryPropertyConfig.getInputFileNamePath());

        // build 3dx-instance context
        logger.info("On the way to get 3DX-context");
        Context context = new MatrixResource.Builder(batteryPropertyConfig).build().getContext();

        if(context.isConnected()) {
            // run..
            logger.info("Execute !!");
            performBatteryUpload(context, batteryPropertyConfig);
        } else {
            logger.error("Unable to get 3DX-context");
        }
    }

    /**
     * Method to perform battery data load for given file or input folder.
     *
     * @return void
     * @since DSM 2018x.6
     */
    public void performBatteryUpload(Context context, BatteryPropertyConfig batteryPropertyConfig) {

        try {
            // load battery config xml
            BatteryProducts batteryProducts = new BatteryProductConfigurator.Builder(batteryPropertyConfig).build().getBatteryProducts();

            // perform pre-checks.
            BatteryConfigPreChecker batteryConfigPreChecker = new BatteryConfigPreChecker.Builder(batteryProducts, batteryPropertyConfig).build();
            List<String> preCheckList = batteryConfigPreChecker.getPreCheckList();

            if (null != preCheckList && !preCheckList.isEmpty()) {
                logger.error(preCheckList);
            } else {

                BatteryProduct batteryProductConfig = batteryConfigPreChecker.getBatteryProduct();
                MatrixUtil matrixUtil = new MatrixUtil(context);
                matrixUtil.getAttributeOrPicklistInitialized(batteryProductConfig.getBatteryProductFeatures());

                BatteryUpdateEnabler batteryUpdateEnabler = new BatteryUpdateEnabler.Builder()
                        .setContext(context)
                        .setBatteryConfigPreChecker(batteryConfigPreChecker)
                        .setBatteryPropertyConfig(batteryPropertyConfig)
                        .setBatteryProductConfig(batteryProductConfig)
                        .build();
                batteryUpdateEnabler.perform();

            }
        } catch (MatrixException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Method to load log4j properties file.
     *
     * @return void
     * @since DSM 2018x.6
     */
    public void initializeInputFileSpecificLogger(BatteryPropertyConfig batteryPropertyConfig) {
        String inputFileName = FilenameUtils.removeExtension(new File(batteryPropertyConfig.getInputFileNamePath()).getName());

        StringBuilder pathBuilder = new StringBuilder(batteryPropertyConfig.getLogFolder());
        pathBuilder.append(batteryPropertyConfig.getTimeStamp());
        pathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        pathBuilder.append(batteryPropertyConfig.getLogKeyword());
        pathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        pathBuilder.append(inputFileName);
        pathBuilder.append(BatteryConstants.Basic.DEBUG_LOG_FILE_EXTENSION.getValue());

        System.setProperty(BatteryConstants.Basic.DEBUG_LOG_FILE_CONFIG.getValue(), pathBuilder.toString());

        pathBuilder = new StringBuilder(batteryPropertyConfig.getLogFolder());
        pathBuilder.append(batteryPropertyConfig.getTimeStamp());
        pathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        pathBuilder.append(batteryPropertyConfig.getLogKeyword());
        pathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        pathBuilder.append(inputFileName);
        pathBuilder.append(BatteryConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        pathBuilder.append(BatteryConstants.Basic.ERROR_LOG_FILE_EXTENSION.getValue());

        System.setProperty(BatteryConstants.Basic.ERROR_LOG_FILE_CONFIG.getValue(), pathBuilder.toString());

        Properties config = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(batteryPropertyConfig.getCustomLoggerFile())) {
            config.load(fileInputStream);
            PropertyConfigurator.configure(config);
            logger.info("log4j loaded");
        } catch (IOException e) {
            logger.error("Unable to load file pgBatteryDataLoad-log4j.properties");
        }
    }
}
