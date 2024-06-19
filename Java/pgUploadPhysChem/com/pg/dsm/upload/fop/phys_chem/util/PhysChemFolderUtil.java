package com.pg.dsm.upload.fop.phys_chem.util;

import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;

import java.io.File;

public class PhysChemFolderUtil {

    private PhysChemFolderUtil() {
    }

    /**
     * Method to get input excel file path
     *
     * @return String - file path
     * @since DSM 2018x.5
     */
    private static String getFilePath(String folderName, String fileName) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(System.getProperty(FormulationGeneralConstant.CURRENT_DIR.getValue()));
        pathBuilder.append(File.separator);
        pathBuilder.append(FormulationGeneralConstant.HOME_DIR.getValue());
        pathBuilder.append(File.separator);
        pathBuilder.append(folderName);
        pathBuilder.append(File.separator);
        pathBuilder.append(fileName);
        return pathBuilder.toString();
    }

    /**
     * Method to get input folder path.
     *
     * @return String - folder path
     * @since DSM 2018x.5
     */
    private static String getFolderPath(String folderName) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(System.getProperty(FormulationGeneralConstant.CURRENT_DIR.getValue()));
        pathBuilder.append(File.separator);
        pathBuilder.append(FormulationGeneralConstant.HOME_DIR.getValue());
        pathBuilder.append(File.separator);
        pathBuilder.append(folderName);
        pathBuilder.append(File.separator);
        return pathBuilder.toString();
    }

    /**
     * Method to get input excel file path
     *
     * @return String - file path
     * @since DSM 2018x.5
     */
    public static String getPhysChemInputExcelFilePath(String fileName) {
        return getFilePath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_INPUT_FOLDER.getValue(), fileName);
    }

    /**
     * Method to get config xml file path
     *
     * @return String - file path
     * @since DSM 2018x.5
     */
    public static String getPhysChemXMLConfigFilePath() {
        return getFilePath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_CONFIG_FOLDER.getValue(), FormulationGeneralConstant.UPLOAD_PHYS_CHEM_XML_CONFIGURATION_FILE.getValue());
    }

    /**
     * Method to get config property file path
     *
     * @return String - file path
     * @since DSM 2018x.5
     */
    public static String getPhysChemPropertyConfigFilePath() {
        return getFilePath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_CONFIG_FOLDER.getValue(), FormulationGeneralConstant.UPLOAD_PHYS_CHEM_PROPERTY_FILE.getValue());
    }

    /**
     * Method to get logger config file path
     *
     * @return String - file path
     * @since DSM 2018x.5
     */
    public static String getPhysChemLoggerConfigFilePath() {
        return getFilePath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_CONFIG_FOLDER.getValue(), FormulationGeneralConstant.UPLOAD_PHYS_CHEM_LOGGER_CONFIGURATION_FILE.getValue());
    }

    /**
     * Method to get input folder path.
     *
     * @return String - folder path
     * @since DSM 2018x.5
     */
    public static String getInputFolderPath() {
        return getFolderPath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_INPUT_FOLDER.getValue());
    }

    /**
     * Method to get config folder path.
     *
     * @return String - folder path
     * @since DSM 2018x.5
     */
    public static String getConfigFolderPath() {
        return getFolderPath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_CONFIG_FOLDER.getValue());
    }

    /**
     * Method to get output folder path.
     *
     * @return String - folder path
     * @since DSM 2018x.5
     */
    public static String getOutputFolderPath() {
        return getFolderPath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_OUTPUT_FOLDER.getValue());
    }
    
    /**
     * Method to get Logger folder path.
     *
     * @return String - folder path
     * @since DSM 2018x.6
     */
    public static String getLogsFolderPath() {
        return getFolderPath(FormulationGeneralConstant.UPLOAD_PHYS_CHEM_LOGS_FOLDER.getValue());
    }
}
