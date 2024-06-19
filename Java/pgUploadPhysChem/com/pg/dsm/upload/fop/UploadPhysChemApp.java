package com.pg.dsm.upload.fop;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.phys_chem.services.PhysChemFileProcessor;
import com.pg.dsm.upload.fop.phys_chem.util.PhysChemFolderUtil;

public class UploadPhysChemApp {
	
	public static final Logger logger = Logger.getLogger(UploadPhysChemApp.class.getName());
	
	/**
     * Method to perform update operation.
     *
     * @param args - String[]
     * @return void
     * @since DSM 2018x.6
     */
    public static void main(String[] args) throws IOException {
    	String inputFileName = DomainConstants.EMPTY_STRING;
        try {
        	if(args.length == 1) {
        		Instant startTime = Instant.now();
        		inputFileName = args[0].trim();
        		PhysChemFileProcessor app = new PhysChemFileProcessor(inputFileName);
        		loggerInitialize(inputFileName, app);
                boolean isTimeStampSet = app.setTimestamp();
        		if(FormulationGeneralConstant.CONST_FILE_EXTENSION_XLSM.getValue().equalsIgnoreCase(FilenameUtils.getExtension(inputFileName))) {
                    app.run(isTimeStampSet);
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);
                    logger.info("Update PhysChem - Total execution took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        		} else {
        			logger.error("Execution Aborted : File "+inputFileName+" should be .xlsm extention");
        			logger.info("physChem Process Ends - cleanup thread");
        		}
        	} else {
        		logger.info("Execution Aborted : File name is not passed as an argument or passed extra arguments");
        	}
        	if(args.length == 0) {
        		String inputFolder = PhysChemFolderUtil.getInputFolderPath();
        		File inputDir = new File(inputFolder);
        		 if (inputDir.isDirectory()) {
        			 File[] files = inputDir.listFiles();
        			 int numberOfFiles = files.length;
        			 if (numberOfFiles > 0) {
        				 for (File file : files) {
        					 inputFileName = file.getName();
        					 PhysChemFileProcessor app = new PhysChemFileProcessor(inputFileName);
        					 loggerInitialize(inputFileName, app);
        					 if (new File(inputFolder, inputFileName).isFile()) {
        						 if(FormulationGeneralConstant.CONST_FILE_EXTENSION_XLSM.getValue().equalsIgnoreCase(FilenameUtils.getExtension(inputFileName))) {
        							 Instant startTime = Instant.now();
            						 boolean isTimeStampSet = app.setTimestamp();
            						 app.run(isTimeStampSet);
            						 Instant endTime = Instant.now();
            						 Duration duration = Duration.between(startTime, endTime);
            						 logger.info("Update PhysChem - Total execution took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min"); 
        						 } else {
        							 logger.error("Execution Aborted : File "+inputFileName+" should be .xlsm extention"); 
        							 logger.info("physChem Process Ends - cleanup thread");
        						 }
        					 } else {
        						 logger.error("Execution Aborted : "+inputFileName+" should be a file");
    					         logger.info("physChem Process Ends - cleanup thread");
        					 }
        				 }
        			 }
        		 }
        	}
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            System.exit(0);
        }
    }
    
    /**
     * Method to initialize the logger.
     *
     * @param inputFileName - String
     * @param app - PhysChemFileProcessor
     * @return void
     * @since DSM 2018x.6
     */
    private static void loggerInitialize(String inputFileName, PhysChemFileProcessor app) {
		try {
			app.init(FilenameUtils.removeExtension(inputFileName));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
    }
}
