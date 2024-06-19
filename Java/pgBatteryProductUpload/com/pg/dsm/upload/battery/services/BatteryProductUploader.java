/*
 **   BatteryProductUploader.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Intermediate Class to perform battery data load.
 **
 */
package com.pg.dsm.upload.battery.services;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import com.pg.dsm.upload.battery.resources.BatteryPropertyConfig;
import com.pg.dsm.upload.battery.resources.BatteryPropertyFileResource;
import org.apache.commons.io.FilenameUtils;


import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class BatteryProductUploader {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    String[] args;

    public BatteryProductUploader(String[] args) {
    	logger.info("Constructor");
        this.args = args;
    }

    /**
     * Method to perform battery data load for given file or input folder.
     *
     * @return void
     * @since DSM 2018x.6
     */
    public void upload() {
        if (args.length == 1) {
            logger.info("Argument Provided");
            String inputFileName = args[0].trim();
            BatteryPropertyConfig batteryPropertyConfig = new BatteryPropertyConfig(inputFileName, new BatteryPropertyFileResource.Builder().build().getProperties());
            File inputFile = new File(batteryPropertyConfig.getInputFileNamePath());
            if (inputFile.isFile()) {
                logger.info("Processing file: "+inputFileName+" >> STARTED !!");
                
                if (BatteryConstants.Basic.FILE_EXTENSION_XLSM.getValue().equalsIgnoreCase(FilenameUtils.getExtension(inputFileName))) {
	                BatteryProductFileProcessor batteryProductFileProcessor = new BatteryProductFileProcessor(inputFileName);
	                batteryProductFileProcessor.process();
	                logger.info("Processing file: "+inputFileName+" >> COMPLETED !!");
                } else {
                	logger.info("File Execution Aborted : File extension should be .xlsm");
                }
            } else {
                logger.info("Given input file doesn't exist in input folder. Exiting Process!!");
            }
        }
        if (args.length == 0) {
            logger.info("No Argument Provided");
            BatteryPropertyConfig batteryPropertyConfig = new BatteryPropertyConfig(DomainConstants.EMPTY_STRING, new Properties());
            String inputFolder = batteryPropertyConfig.getInputFolder();
            logger.info(String.format("Input Folder %s", inputFolder));
            File inputDir = new File(inputFolder);
            if (inputDir.isDirectory()) {
                File[] files = inputDir.listFiles();
                int numberOfFiles = files.length;
                logger.info("Number of Files inside input folder:" + numberOfFiles);
                if (numberOfFiles > 0) {
                    logger.info("All files inside input folder will be processed.");
                    BatteryProductFileProcessor batteryProductFileProcessor;
                    for (File file : files) {
                        logger.info("Processing file: "+file.getName()+" >> STARTED !!");
                        if (file.isFile()) {
	                        if (BatteryConstants.Basic.FILE_EXTENSION_XLSM.getValue().equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {
		                        batteryProductFileProcessor = new BatteryProductFileProcessor(file.getName());
		                        batteryProductFileProcessor.process();
		                        logger.info("Processing file: "+file.getName()+" >> COMPLETED !!");
	                        }  else {
	                        	logger.info("Execution Aborted : "+file.getName()+" File extension should be .xlsm.");
	                        }
                        } else {
                        	logger.info("Execution Aborted : "+file.getName()+ "should be a file.");
                        }
                    }
                
                } else {
                    logger.info("There are no files inside input folder. Exiting Process!!");
                }
            }
        }
    }
}
