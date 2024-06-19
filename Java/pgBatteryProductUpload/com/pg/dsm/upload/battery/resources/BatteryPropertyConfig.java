/*
 **   BatteryPropertyConfig.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Bean class to load all property resources.
 **
 */
package com.pg.dsm.upload.battery.resources;

import com.pg.dsm.upload.battery.enumeration.BatteryConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

public class BatteryPropertyConfig {
    String timeStamp;
    String host;
    String user;
    String pdw;
    String homeDirectory;
    String configFolder;
    String inputFolder;
    String inputFileName;
    String inputFileNamePath;
    String outputFolder;
    String processedFolder;
    String retryFolder;
    String configXMLFile;
    String logFolder;
    String customLoggerFile;
    String customPropertyFile;
    String excelFileExtension;
    int excelInputTabPosition;
    String excelTabDPP;
    String excelTabRM;
    String errorMoreInputFile;
    String errorNoInputFile;
    String errorNoRecords;
    String errorEmptySheet;
    String errorIncorrectColumnCount;
    String errorMismatchColumnPosition;
    String errorGCASLengthExceedsMessage;
    String errorGCASNotFound;
    String errorExcelDPPColumn;
    String errorExcelRMColumn;
    String errorExcelRevisionColumn;
    String errorExcelValidationFailureMessageColumn;
    String errorExcelNameColumn;
    String restoreKeyword;
    String errorKeyword;
    String inputKeyword;
    String logKeyword;

    String powerSourceRangeValues;
    String powerSourceAllowedRangeValues;
    String powerSourceNotAllowedRanges;
    String isBatteryAllowedValues;
    String errorMessagePowerSourceRangeValueMismatch;
    String errorMessageBatteryTypeValueEmpty;
    String errorMessageBatteryTypeValueDoesNotExist;
    String errorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup;
    String errorMessageBatteriesShippedInsideValueMustBeNumeric;
    String errorMessageBatteriesRequiredValueMandatoryWhenPowerSource;
    String errorMessageBatteriesRequiredValueMustBeNumeric;
    String errorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup;
    String errorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs;
    String errorMessageIsBatteryValueShouldBeYesOrNo;
    String errorMessageNetWeightValueMustBeNumeric;

    public BatteryPropertyConfig(String inputFileName, Properties properties) {

        setTimeStamp(generateStartTimeStamp());

        if (properties.size() > 0) {

            setHost(properties.getProperty(BatteryConstants.Basic.MATRIX_URL.getValue()));

            setUser(properties.getProperty(BatteryConstants.Basic.MATRIX_USR.getValue()));

            setPdw(properties.getProperty(BatteryConstants.Basic.MATRIX_PDW.getValue()));

            setHomeDirectory(properties.getProperty(BatteryConstants.Basic.HOME_DIR.getValue()));

            setConfigFolder(properties.getProperty(BatteryConstants.Basic.CONFIG_FOLDER.getValue()));

            setInputFolder(properties.getProperty(BatteryConstants.Basic.INPUT_FOLDER_NAME.getValue()));

            setLogFolder(properties.getProperty(BatteryConstants.Basic.LOGS_FOLDER.getValue()));

            setOutputFolder(properties.getProperty(BatteryConstants.Basic.OUTPUT_FOLDER_NAME.getValue()));

            setProcessedFolder(properties.getProperty(BatteryConstants.Basic.PROCESSED_FOLDER.getValue()));

            setRetryFolder(properties.getProperty(BatteryConstants.Basic.RETRY_FOLDER.getValue()));

            setConfigXMLFile(properties.getProperty(BatteryConstants.Basic.BATTERY_PRODUCTS_CONFIG_XML_FILE_NAME.getValue()));

            setCustomLoggerFile(properties.getProperty(BatteryConstants.Basic.CONFIG_LOGGER_FILE.getValue()));

            setCustomPropertyFile(properties.getProperty(BatteryConstants.Basic.CONFIG_PROPERTY_FILE_NAME.getValue()));

            setExcelFileExtension(properties.getProperty(BatteryConstants.Basic.INPUT_EXCEL_FILE_EXTENSION.getValue()));

            setExcelInputTabPosition(Integer.parseInt(properties.getProperty(BatteryConstants.Basic.EXCEL_USER_INPUT_TAB_POSITION.getValue())));

            setExcelTabDPP(properties.getProperty(BatteryConstants.Basic.EXCEL_TAB_IDENTIFIER_DPP.getValue()));

            setExcelTabRM(properties.getProperty(BatteryConstants.Basic.EXCEL_TAB_IDENTIFIER_RM.getValue()));

            setErrorMoreInputFile(properties.getProperty(BatteryConstants.Basic.EXCEL_PRE_CHECK_MORE_INPUT_FILE_COUNT.getValue()));

            setErrorNoInputFile(properties.getProperty(BatteryConstants.Basic.EXCEL_PRE_CHECK_NO_INPUT_FILE.getValue()));

            setErrorEmptySheet(properties.getProperty(BatteryConstants.Basic.EXCEL_USER_INPUT_SHEET_EMPTY.getValue()));

            setErrorIncorrectColumnCount(properties.getProperty(BatteryConstants.Basic.EXCEL_USER_INPUT_SHEET_COLUMNS_INCORRECT.getValue()));

            setErrorMismatchColumnPosition(properties.getProperty(BatteryConstants.Basic.EXCEL_USER_INPUT_SHEET_COLUMNS_POSITION_MISMATCH_MESSAGE.getValue()));

            setErrorNoRecords(properties.getProperty(BatteryConstants.Basic.EXCEL_USER_INPUT_SHEET_NO_RECORDS.getValue()));

            setErrorGCASLengthExceedsMessage(properties.getProperty(BatteryConstants.Basic.EXCEL_INPUT_GCAS_LENGTH_ERROR_MESSAGE.getValue()));

            setErrorGCASNotFound(properties.getProperty(BatteryConstants.Basic.EXCEL_INPUT_GCAS_NOT_FOUND.getValue()));

            setErrorExcelDPPColumn(properties.getProperty(BatteryConstants.Basic.ERROR_EXCEL_COLUMN_DPP.getValue()));

            setErrorExcelRMColumn(properties.getProperty(BatteryConstants.Basic.ERROR_EXCEL_COLUMN_RM.getValue()));

            setErrorExcelRevisionColumn(properties.getProperty(BatteryConstants.Basic.ERROR_EXCEL_COLUMN_REVISION.getValue()));

            setErrorExcelValidationFailureMessageColumn(properties.getProperty(BatteryConstants.Basic.ERROR_EXCEL_COLUMN_VALIDATION_FAILURE_MESSAGE.getValue()));

            setErrorExcelNameColumn(properties.getProperty(BatteryConstants.Basic.ERROR_EXCEL_COLUMN_NAME.getValue()));

            setRestoreKeyword(properties.getProperty(BatteryConstants.Basic.IDENTIFIER_KEYWORD_RESTORE.getValue()));

            setErrorKeyword(properties.getProperty(BatteryConstants.Basic.IDENTIFIER_KEYWORD_ERROR.getValue()));

            setLogKeyword(properties.getProperty(BatteryConstants.Basic.IDENTIFIER_KEYWORD_LOG.getValue()));

            setInputKeyword(properties.getProperty(BatteryConstants.Basic.IDENTIFIER_KEYWORD_INPUT.getValue()));

            setPowerSourceRangeValues(properties.getProperty(BatteryConstants.Basic.POWER_SOURCE_RANGE_VALUES.getValue()));

            setPowerSourceAllowedRangeValues(properties.getProperty(BatteryConstants.Basic.POWER_SOURCE_RANGE_VALUES_ALLOWED.getValue()));

            setErrorMessagePowerSourceRangeValueMismatch(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_POWER_SOURCE_RANGE_VALUE_MISMATCH.getValue()));

            setErrorMessageBatteryTypeValueEmpty(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERY_TYPE_VALUE_EMPTY.getValue()));

            setPowerSourceNotAllowedRanges(properties.getProperty(BatteryConstants.Basic.POWER_SOURCE_RANGE_VALUES_NOT_ALLOWED.getValue()));

            setErrorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERY_TYPE_CANNOT_UPDATE_WHEN_POWER_SOURCE_RANGE_IS_ROLLUP.getValue()));

            setErrorMessageBatteriesShippedInsideValueMustBeNumeric(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERIES_SHIPPED_INSIDE_VALUE_MUST_BE_NUMERIC.getValue()));

            setErrorMessageBatteriesRequiredValueMandatoryWhenPowerSource(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERIES_REQUIRED_VALUE_MANDATORY_WHEN_POWER_SOURCE.getValue()));

            setErrorMessageBatteriesRequiredValueMustBeNumeric(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERIES_REQUIRED_VALUE_MUST_BE_NUMERIC.getValue()));

            setErrorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERIES_REQUIRED_VALUE_NOT_REQUIRED_WHEN_POWER_SOURCE_RANGE_IS_ROLLUP.getValue()));

            setErrorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERY_TYPE_CANNOT_UPDATE_WHEN_IS_BATTERY_VALUE_IS.getValue()));

            setErrorMessageIsBatteryValueShouldBeYesOrNo(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_IS_BATTERY_VALUE_SHOULD_BE_YES_OR_NO.getValue()));

            setErrorMessageBatteryTypeValueDoesNotExist(properties.getProperty(BatteryConstants.Basic.ERROR_MESSAGE_BATTERY_TYPE_VALUE_DOES_NOT_EXIST.getValue()));

            setInputFileName(inputFileName);

            setInputFileNamePath(inputFileName);
            
            setIsBatteryValues(properties.getProperty(BatteryConstants.Basic.IS_BATTERY_VALUES.getValue()));

            setErrorMessageNetWeightValueMustBeNumeric(BatteryConstants.Basic.ERROR_MESSAGE_NET_WEIGHT_VALUE_MUST_BE_NUMERIC.getValue());


        } else {
            setHomeDirectory(BatteryConstants.Basic.HOME_DIR_DUPLICATE.getValue());
            setConfigFolder(BatteryConstants.Basic.CONFIG_FOLDER_DUPLICATE.getValue());
            setCustomLoggerFile(BatteryConstants.Basic.CONFIG_LOGGER_FILE_DUPLICATE.getValue());
            setInputFolder(BatteryConstants.Basic.INPUT_FOLDER_DUPLICATE.getValue());
            setLogFolder(BatteryConstants.Basic.INPUT_FOLDER_DUPLICATE.getValue());
        }

    }

    public String getErrorMessageNetWeightValueMustBeNumeric() {
        return errorMessageNetWeightValueMustBeNumeric;
    }

    public void setErrorMessageNetWeightValueMustBeNumeric(String errorMessageNetWeightValueMustBeNumeric) {
        this.errorMessageNetWeightValueMustBeNumeric = errorMessageNetWeightValueMustBeNumeric;
    }

	public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPdw() {
        return pdw;
    }

    public void setPdw(String pdw) {
        this.pdw = pdw;
    }

    public String getHomeDirectory() {
        return homeDirectory;
    }

    public void setHomeDirectory(String homeDirectory) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(System.getProperty(BatteryConstants.Basic.CURRENT_DIR.getValue()));
        pathBuilder.append(File.separator);
        pathBuilder.append(BatteryConstants.Basic.BASE_DIR_DUPLICATE.getValue());
        pathBuilder.append(File.separator);
        pathBuilder.append(homeDirectory);
        pathBuilder.append(File.separator);
        this.homeDirectory = pathBuilder.toString();
    }

    public String getConfigFolder() {
        return configFolder;
    }

    public void setConfigFolder(String configFolder) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getHomeDirectory());
        pathBuilder.append(configFolder);
        pathBuilder.append(File.separator);
        this.configFolder = pathBuilder.toString();
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(String inputFolder) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getHomeDirectory());
        pathBuilder.append(inputFolder);
        pathBuilder.append(File.separator);
        this.inputFolder = pathBuilder.toString();
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getHomeDirectory());
        pathBuilder.append(outputFolder);
        pathBuilder.append(File.separator);
        this.outputFolder = pathBuilder.toString();
    }

    public String getProcessedFolder() {
        return processedFolder;
    }

    public void setProcessedFolder(String processedFolder) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getHomeDirectory());
        pathBuilder.append(processedFolder);
        pathBuilder.append(File.separator);
        this.processedFolder = pathBuilder.toString();
    }

    public String getRetryFolder() {
        return retryFolder;
    }

    public void setRetryFolder(String retryFolder) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getHomeDirectory());
        pathBuilder.append(retryFolder);
        pathBuilder.append(File.separator);
        this.retryFolder = pathBuilder.toString();
    }

    public String getConfigXMLFile() {
        return configXMLFile;
    }

    public void setConfigXMLFile(String configXMLFile) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getConfigFolder());
        pathBuilder.append(configXMLFile);
        this.configXMLFile = pathBuilder.toString();
    }

    public String getCustomLoggerFile() {
        return customLoggerFile;
    }

    public void setCustomLoggerFile(String customLoggerFile) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getConfigFolder());
        pathBuilder.append(customLoggerFile);
        this.customLoggerFile = pathBuilder.toString();
    }

    public String getCustomPropertyFile() {
        return customPropertyFile;
    }

    public void setCustomPropertyFile(String customPropertyFile) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getConfigFolder());
        pathBuilder.append(customPropertyFile);
        this.customPropertyFile = pathBuilder.toString();
    }

    public String getExcelFileExtension() {
        return excelFileExtension;
    }

    public void setExcelFileExtension(String excelFileExtension) {
        this.excelFileExtension = excelFileExtension;
    }

    public int getExcelInputTabPosition() {
        return excelInputTabPosition;
    }

    public void setExcelInputTabPosition(int excelInputTabPosition) {
        this.excelInputTabPosition = excelInputTabPosition;
    }

    public String getExcelTabDPP() {
        return excelTabDPP;
    }

    public void setExcelTabDPP(String excelTabDPP) {
        this.excelTabDPP = excelTabDPP;
    }

    public String getExcelTabRM() {
        return excelTabRM;
    }

    public void setExcelTabRM(String excelTabRM) {
        this.excelTabRM = excelTabRM;
    }

    public String getErrorMoreInputFile() {
        return errorMoreInputFile;
    }

    public void setErrorMoreInputFile(String errorMoreInputFile) {
        this.errorMoreInputFile = errorMoreInputFile;
    }

    public String getErrorNoInputFile() {
        return errorNoInputFile;
    }

    public void setErrorNoInputFile(String errorNoInputFile) {
        this.errorNoInputFile = errorNoInputFile;
    }

    public String getErrorNoRecords() {
        return errorNoRecords;
    }

    public void setErrorNoRecords(String errorNoRecords) {
        this.errorNoRecords = errorNoRecords;
    }

    public String getErrorEmptySheet() {
        return errorEmptySheet;
    }

    public void setErrorEmptySheet(String errorEmptySheet) {
        this.errorEmptySheet = errorEmptySheet;
    }

    public String getErrorIncorrectColumnCount() {
        return errorIncorrectColumnCount;
    }

    public void setErrorIncorrectColumnCount(String errorIncorrectColumnCount) {
        this.errorIncorrectColumnCount = errorIncorrectColumnCount;
    }

    public String getErrorMismatchColumnPosition() {
        return errorMismatchColumnPosition;
    }

    public void setErrorMismatchColumnPosition(String errorMismatchColumnPosition) {
        this.errorMismatchColumnPosition = errorMismatchColumnPosition;
    }

    public String getErrorGCASLengthExceedsMessage() {
        return errorGCASLengthExceedsMessage;
    }

    public void setErrorGCASLengthExceedsMessage(String errorGCASLengthExceedsMessage) {
        this.errorGCASLengthExceedsMessage = errorGCASLengthExceedsMessage;
    }

    public String getErrorGCASNotFound() {
        return errorGCASNotFound;
    }

    public void setErrorGCASNotFound(String errorGCASNotFound) {
        this.errorGCASNotFound = errorGCASNotFound;
    }

    public String getErrorExcelDPPColumn() {
        return errorExcelDPPColumn;
    }

    public void setErrorExcelDPPColumn(String errorExcelDPPColumn) {
        this.errorExcelDPPColumn = errorExcelDPPColumn;
    }

    public String getErrorExcelRMColumn() {
        return errorExcelRMColumn;
    }

    public void setErrorExcelRMColumn(String errorExcelRMColumn) {
        this.errorExcelRMColumn = errorExcelRMColumn;
    }

    public String getErrorExcelRevisionColumn() {
        return errorExcelRevisionColumn;
    }

    public void setErrorExcelRevisionColumn(String errorExcelRevisionColumn) {
        this.errorExcelRevisionColumn = errorExcelRevisionColumn;
    }

    public String getErrorExcelValidationFailureMessageColumn() {
        return errorExcelValidationFailureMessageColumn;
    }

    public void setErrorExcelValidationFailureMessageColumn(String errorExcelValidationFailureMessageColumn) {
        this.errorExcelValidationFailureMessageColumn = errorExcelValidationFailureMessageColumn;
    }

    public String getErrorExcelNameColumn() {
        return errorExcelNameColumn;
    }

    public void setErrorExcelNameColumn(String errorExcelNameColumn) {
        this.errorExcelNameColumn = errorExcelNameColumn;
    }

    public String getRestoreKeyword() {
        return restoreKeyword;
    }

    public void setRestoreKeyword(String restoreKeyword) {
        this.restoreKeyword = restoreKeyword;
    }

    public String getErrorKeyword() {
        return errorKeyword;
    }

    public void setErrorKeyword(String errorKeyword) {
        this.errorKeyword = errorKeyword;
    }

    public String getInputKeyword() {
        return inputKeyword;
    }

    public void setInputKeyword(String inputKeyword) {
        this.inputKeyword = inputKeyword;
    }

    public String getPowerSourceRangeValues() {
        return powerSourceRangeValues;
    }

    public BatteryPropertyConfig setPowerSourceRangeValues(String powerSourceRangeValues) {
        this.powerSourceRangeValues = powerSourceRangeValues;
        return this;
    }

    public String getErrorMessagePowerSourceRangeValueMismatch() {
        return errorMessagePowerSourceRangeValueMismatch;
    }

    public BatteryPropertyConfig setErrorMessagePowerSourceRangeValueMismatch(String errorMessagePowerSourceRangeValueMismatch) {
        this.errorMessagePowerSourceRangeValueMismatch = errorMessagePowerSourceRangeValueMismatch;
        return this;
    }

    public String getPowerSourceAllowedRangeValues() {
        return powerSourceAllowedRangeValues;
    }

    public BatteryPropertyConfig setPowerSourceAllowedRangeValues(String powerSourceAllowedRangeValues) {
        this.powerSourceAllowedRangeValues = powerSourceAllowedRangeValues;
        return this;
    }

    public String getErrorMessageBatteryTypeValueEmpty() {
        return errorMessageBatteryTypeValueEmpty;
    }

    public BatteryPropertyConfig setErrorMessageBatteryTypeValueEmpty(String errorMessageBatteryTypeValueEmpty) {
        this.errorMessageBatteryTypeValueEmpty = errorMessageBatteryTypeValueEmpty;
        return this;
    }

    public String getErrorMessageBatteryTypeValueDoesNotExist() {
        return errorMessageBatteryTypeValueDoesNotExist;
    }

    public BatteryPropertyConfig setErrorMessageBatteryTypeValueDoesNotExist(String errorMessageBatteryTypeValueDoesNotExist) {
        this.errorMessageBatteryTypeValueDoesNotExist = errorMessageBatteryTypeValueDoesNotExist;
        return this;
    }

    public String getPowerSourceNotAllowedRanges() {
        return powerSourceNotAllowedRanges;
    }

    public BatteryPropertyConfig setPowerSourceNotAllowedRanges(String powerSourceNotAllowedRanges) {
        this.powerSourceNotAllowedRanges = powerSourceNotAllowedRanges;
        return this;
    }

    public String getErrorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup() {
        return errorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup;
    }

    public BatteryPropertyConfig setErrorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup(String errorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup) {
        this.errorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup = errorMessageBatteryTypeCannotBeUpdatedWhenPowerSourceIsRollup;
        return this;
    }

    public String getErrorMessageBatteriesShippedInsideValueMustBeNumeric() {
        return errorMessageBatteriesShippedInsideValueMustBeNumeric;
    }

    public BatteryPropertyConfig setErrorMessageBatteriesShippedInsideValueMustBeNumeric(String errorMessageBatteriesShippedInsideValueMustBeNumeric) {
        this.errorMessageBatteriesShippedInsideValueMustBeNumeric = errorMessageBatteriesShippedInsideValueMustBeNumeric;
        return this;
    }

    public String getErrorMessageBatteriesRequiredValueMandatoryWhenPowerSource() {
        return errorMessageBatteriesRequiredValueMandatoryWhenPowerSource;
    }

    public BatteryPropertyConfig setErrorMessageBatteriesRequiredValueMandatoryWhenPowerSource(String errorMessageBatteriesRequiredValueMandatoryWhenPowerSource) {
        this.errorMessageBatteriesRequiredValueMandatoryWhenPowerSource = errorMessageBatteriesRequiredValueMandatoryWhenPowerSource;
        return this;
    }

    public String getErrorMessageBatteriesRequiredValueMustBeNumeric() {
        return errorMessageBatteriesRequiredValueMustBeNumeric;
    }

    public BatteryPropertyConfig setErrorMessageBatteriesRequiredValueMustBeNumeric(String errorMessageBatteriesRequiredValueMustBeNumeric) {
        this.errorMessageBatteriesRequiredValueMustBeNumeric = errorMessageBatteriesRequiredValueMustBeNumeric;
        return this;
    }

    public String getErrorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup() {
        return errorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup;
    }

    public BatteryPropertyConfig setErrorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup(String errorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup) {
        this.errorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup = errorMessageBatteriesRequiredValueNotRequiredWhenPowerSourceRangeIsRollup;
        return this;
    }

    public String getErrorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs() {
        return errorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs;
    }

    public BatteryPropertyConfig setErrorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs(String errorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs) {
        this.errorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs = errorMessageBatteryTypeCannotUpdateWhenIsBatteryValueIs;
        return this;
    }

    public String getErrorMessageIsBatteryValueShouldBeYesOrNo() {
        return errorMessageIsBatteryValueShouldBeYesOrNo;
    }

    public BatteryPropertyConfig setErrorMessageIsBatteryValueShouldBeYesOrNo(String errorMessageIsBatteryValueShouldBeYesOrNo) {
        this.errorMessageIsBatteryValueShouldBeYesOrNo = errorMessageIsBatteryValueShouldBeYesOrNo;
        return this;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public BatteryPropertyConfig setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
        return this;
    }

    public String getInputFileNamePath() {
        return inputFileNamePath;
    }

    public BatteryPropertyConfig setInputFileNamePath(String inputFileNamePath) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getInputFolder());
        pathBuilder.append(inputFileNamePath);
        this.inputFileNamePath = pathBuilder.toString();
        return this;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Method to get timestamp
     *
     * @return String - timestamp
     * @since DSM 2018x.6
     */
    public String generateStartTimeStamp() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(BatteryConstants.Basic.CONT_DATE_FORMAT_FOR_PREFIX.getValue(), Locale.US);
        return simpleDateFormat.format(new Date());
    }

    public String getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(String logFolder) {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getHomeDirectory());
        pathBuilder.append(logFolder);
        pathBuilder.append(File.separator);
        this.logFolder = pathBuilder.toString();
    }

    public String getLogKeyword() {
        return logKeyword;
    }

    public void setLogKeyword(String logKeyword) {
        this.logKeyword = logKeyword;
    }
           
    public String getIsBatteryValues() {
		return isBatteryAllowedValues;
    }
    
    public BatteryPropertyConfig setIsBatteryValues(String isBatteryAllowedValues) {
		this.isBatteryAllowedValues = isBatteryAllowedValues;
    	return this;
	}
}
