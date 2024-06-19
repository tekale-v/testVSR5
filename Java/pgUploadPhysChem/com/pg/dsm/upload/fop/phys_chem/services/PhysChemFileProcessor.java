/*
 **   PhysChemFileProcessor.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Contains main class to perform update of Formulation Part -  Physical Chemical Properties.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.services;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.phys_chem.models.PhysChemContext;
import com.pg.dsm.upload.fop.phys_chem.models.PhysChemExcelPreCheck;
import com.pg.dsm.upload.fop.phys_chem.models.PhysChemProcess;
import com.pg.dsm.upload.fop.phys_chem.models.xml.PhysChemBean;
import com.pg.dsm.upload.fop.phys_chem.util.PhysChemExcelUtil;
import com.pg.dsm.upload.fop.phys_chem.util.PhysChemFolderUtil;
import com.pg.dsm.upload.fop.phys_chem.util.PhysChemMatrixUtil;
import matrix.db.Context;
import matrix.util.MatrixException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class PhysChemFileProcessor {
	
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private String inputFileName = DomainConstants.EMPTY_STRING;

    /**
     * Constructor
     *
     * @since DSM 2018x.6
     */
    public PhysChemFileProcessor(String fileName) {
		this.inputFileName = fileName;		
	}

	/**
     * Method to load log4j properties file.
     *
     * @return void
     * @throws IOException 
     * @since DSM 2018x.5
     */
    public void init(String inputFileName) throws IOException {
    	Properties prop = new Properties();
        try(FileInputStream propertiesFile = new FileInputStream(PhysChemFolderUtil.getPhysChemLoggerConfigFilePath())) {
            prop.load(propertiesFile);
            System.setProperty(FormulationGeneralConstant.DEBUG_LOG_FILE_CONFIG.getValue(), new StringBuilder(PhysChemFolderUtil.getLogsFolderPath()).append(getTimeStamp()).append(FormulationGeneralConstant.CONST_SYMBOL_UNDERSCORE.getValue()).append(inputFileName).append(FormulationGeneralConstant.DEBUG_LOG_FILE_EXTENSION.getValue()).toString());
            System.setProperty(FormulationGeneralConstant.ERROR_LOG_FILE_CONFIG.getValue(), new StringBuilder(PhysChemFolderUtil.getLogsFolderPath()).append(getTimeStamp()).append(FormulationGeneralConstant.CONST_SYMBOL_UNDERSCORE.getValue()).append(inputFileName).append(FormulationGeneralConstant.ERROR_LOG_FILE_EXTENSION.getValue()).toString());
            PropertyConfigurator.configure(prop);
            logger.info("log4j loaded");
        } catch (IOException e) {
            logger.error("Unable to load file pgUploadPhysChem-log4j.properties");
        }
    }

    /**
     * Method to perform excel file validation, generate the db restore file, check column validation, database updation, error file geneation.
     *
     * @return void
     * @since DSM 2018x.5
     */
    public void run(boolean isTimeStampSet) throws IOException {
        boolean isContextSet = false;

        try {
            PhysChemMatrixUtil physChemMatrixUtil = new PhysChemMatrixUtil();
            Properties physChemProperties = physChemMatrixUtil.loadPhysChemPropertyFile();
            Context context = physChemMatrixUtil.getContext(physChemProperties);
            if (!PhysChemContext.isContextSet()) {
                PhysChemContext.setContext(context);
                isContextSet = true;
            }
            
            // convert xml to java object
            PhysChemBean physChemBean = physChemMatrixUtil.getPhysChemBean(physChemProperties);
            
            // do excel pre-checks.
            PhysChemExcelPreCheck physChemExcelPreCheck = new PhysChemExcelPreCheck(physChemProperties, physChemBean, inputFileName);
            List<String> excelPreCheckList = physChemExcelPreCheck.getExcelPreCheck();
            if (null != excelPreCheckList && !excelPreCheckList.isEmpty()) {
                logger.error("Excel Pre-check failed " + excelPreCheckList);
            } else {
                PhysChemExcelUtil physChemExcelUtil = new PhysChemExcelUtil(physChemProperties);
                
                // load excel data + db data
                List<PhysChemBean> excelDataList = physChemExcelUtil.getExcelData(physChemBean, inputFileName);

                // create restore excel file with db data.
                physChemExcelUtil.generateRestoreExcelReport(excelDataList, inputFileName);

                // perform excel column validation.
                physChemExcelUtil.performValidations(physChemProperties, excelDataList);

                // check if all columns validations satisfied.
                boolean checkPassed = physChemExcelUtil.isCheckPassed(excelDataList);

                if (checkPassed) { // if all check passed. update excel data in db.
                    PhysChemProcess physChemProcess = new PhysChemProcess(excelDataList);
                    physChemProcess.updatePhysChem();
                } else { // if any check failed. generate error excel.
                    physChemExcelUtil.generateErrorExcelReport(excelDataList, inputFileName);
                }
            }
        } catch (MatrixException | IOException | NoSuchMethodException | ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
        } finally {
            moveInputExcelFileToOutputFolder();
            logger.info("physChem Process Ends - cleanup thread");
            PhysChemContext.endContext(isContextSet, isTimeStampSet);
        }
    }

    /**
     * Method to get timestamp
     *
     * @return String - timestamp
     * @since DSM 2018x.5
     */
    public String getTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FormulationGeneralConstant.CONT_DATE_FORMAT_FOR_PREFIX.getValue(), Locale.US);
        return simpleDateFormat.format(new Date());
    }

    /**
     * Method to move the input excel file to output folder.
     *
     * @return String - timestamp
     * @since DSM 2018x.5
     */
    public void moveInputExcelFileToOutputFolder() throws IOException {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(PhysChemFolderUtil.getOutputFolderPath());
        pathBuilder.append(PhysChemContext.getTimeStamp());
        pathBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_UNDERSCORE.getValue());
        pathBuilder.append(getFileName(inputFileName));
        pathBuilder.append(FormulationGeneralConstant.CONST_SYMBOL_HYPHEN_INPUT_XLSM.getValue());
        try {
            File sourceFile = new File(PhysChemFolderUtil.getPhysChemInputExcelFilePath(inputFileName));
            if (sourceFile.exists()) {
                File destinationFile = new File(pathBuilder.toString());
                FileUtils.moveFile(sourceFile, destinationFile);
                logger.info("Input file moved to output folder");
            }
        } catch (Exception e) {
            logger.error("Exception in moving Input file to output folder" + e.getMessage());
        }
    }
    
    /**
     * Method to get the file name.
     *
     * @return String - file name
     * @since DSM 2018x.6
     */
    public static String getFileName(String inputFileName) {
    	String[] tokens = inputFileName.split("\\.(?=[^\\.]+$)");
    	return tokens[0];
    }

    /**
     * Method to set time stamp value.
     *
     * @return boolean - 
     * @since DSM 2018x.6
     */
	public boolean setTimestamp() {
		boolean isTimeStampSet = false;
		 if (!PhysChemContext.isTimeStampSet()) {
             PhysChemContext.setTimeStamp(getTimeStamp());
             isTimeStampSet = true;
         }
		 return isTimeStampSet;
	}

}
