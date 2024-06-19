package com.pg.dsm.rollup_event.common.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.enumeration.RollupConstants;

public class RollupPropertyResource {

    Properties properties;
    boolean loaded;
    String host;
    String user;
    String pdw;
    String markingConfigBusinessObjectName;
    String rollupConfigBusinessObjectName;
    String attributeRollupConfigBusinessObjectName;
    String errorMessageSendingMailFailed;
    String markForRollupSubject;
    String rollupSubject;
    String attributeRollupSubject;
    String markForRollupMessage;
    String rollupMessage;
    String attributeRollupMessage;
    String rollupPageFileName;
    String jobName1;
    String jobName2;
    String jobName3;
    String attributeRollupInputFilePath;
    String attributeRollupInputFileName;
    String attributeRollupInputFileExtension;
    String preReleaseRollupInputFilePath;
    //Added for Defect# 44220 - Starts
    String ctrlmCircularCheck;
    //Added for Defect# 44220 - Ends
    String allowedTypesForCircularCheck;

    // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - Start
    String packagingCertificationRollupMarkingJobPerformCircularCheck;
    String packagingCertificationRollupCalculateJobPerformCircularCheck;
    String packagingCertificationRollupCalculateJobCircularCheckAllowedTypes;
    String packagingCertificationRollupMarkingJobCircularCheckAllowedTypes;
    String packagingCertificationRollupCalculateConfigObjectName;
    String packagingCertificationRollupMarkingConfigObjectName;
    String packagingCertificationRollupCalculateJobSearchMQLQuery;
    String packagingCertificationRollupMarkingJobSearchMQLQuery;
    String packagingCertificationRollupCalculateJobStuckEmailMessage;
    String packagingCertificationRollupCalculateJobStuckEmailSubject;
    String packagingCertificationRollupMarkingJobStuckEmailMessage;
    String packagingCertificationRollupMarkingJobStuckEmailSubject;
    // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - End

    private RollupPropertyResource(Builder builder) {
        this.loaded = builder.loaded;
        if (loaded) {
            this.properties = builder.properties;
            setHost(properties.getProperty(RollupConstants.Basic.MATRIX_HOST.getValue()));
            setUser(properties.getProperty(RollupConstants.Basic.CONTEXT_USER.getValue()));
            setPdw(properties.getProperty(RollupConstants.Basic.CONTEXT_PASSWORD.getValue()));
            setMarkingConfigBusinessObjectName(properties.getProperty(RollupConstants.Basic.FPP_MARKING_CONFIG_OBJECT_NAME.getValue()));
            setRollupConfigBusinessObjectName(properties.getProperty(RollupConstants.Basic.PERFORM_ROLLUP_CONFIG_OBJECT_NAME.getValue()));
            setAttributeRollupConfigBusinessObjectName(properties.getProperty(RollupConstants.Basic.PERFORM_ATTRIBUTE_ROLLUP_CONFIG_OBJECT_NAME.getValue()));
            setErrorMessageSendingMailFailed(properties.getProperty(RollupConstants.Basic.SENDING_MAIL_FAILED.getValue()));
            setMarkForRollupSubject(properties.getProperty(RollupConstants.Basic.MAIL_SUBJECT_MARK_FPPS_FOR_ROLLUP.getValue()));
            setRollupSubject(properties.getProperty(RollupConstants.Basic.MAIL_SUBJECT_PROCESS_FPP_ROLLUP.getValue()));
            setAttributeRollupSubject(properties.getProperty(RollupConstants.Basic.MAIL_SUBJECT_ATTRIBUTE_ROLLUP.getValue()));
            setMarkForRollupMessage(properties.getProperty(RollupConstants.Basic.MAIL_MESSAGE_MARK_FPPS_FOR_ROLLUP.getValue()));
            setRollupMessage(properties.getProperty(RollupConstants.Basic.MAIL_MESSAGE_PROCESS_FPP_ROLLUP.getValue()));
            setAttributeRollupMessage(properties.getProperty(RollupConstants.Basic.MAIL_MESSAGE_ATTRIBUTE_ROLLUP.getValue()));
            setRollupPageFileName(properties.getProperty(RollupConstants.Basic.PAGE_FILE_NAME.getValue()));
            setJobName1(properties.getProperty(RollupConstants.Basic.CTRLM1_CRON_JOB_NAME.getValue()));
            setJobName2(properties.getProperty(RollupConstants.Basic.CTRLM2_CRON_JOB_NAME.getValue()));
            setJobName3(properties.getProperty(RollupConstants.Basic.CTRLM3_CRON_JOB_NAME.getValue()));
            setAttributeRollupInputFilePath(properties.getProperty(RollupConstants.Basic.FPP_ATTRIBUTE_ROLLUP_INPUTFILE_PATH.getValue()));
            setAttributeRollupInputFileName(properties.getProperty(RollupConstants.Basic.FPP_ATTRIBUTE_ROLLUP_INPUTFILE_NAME.getValue()));
            setAttributeRollupInputFileExtension(properties.getProperty(RollupConstants.Basic.FPP_ATTRIBUTE_ROLLUP_INPUTFILE_EXTENSION.getValue()));
            setPreReleaseRollupInputFilePath();
            //Added for Defect# 44220 - Starts
            setCtrlmCircularCheck(properties.getProperty(RollupConstants.Basic.CTRLM_CIRCULAR_CHECK.getValue()));
            //Added for Defect# 44220 - Ends
            setAllowedTypesForCircularCheck(properties.getProperty(RollupConstants.Basic.ALLOWED_TYPE_FOR_CIRCULAR_CHECK.getValue()));

            // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - Start
            setPackagingCertificationRollupCalculateJobCircularCheckAllowedTypes(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_CIRCULAR_CHECK_ALLOWED_TYPES.getValue()));
            setPackagingCertificationRollupMarkingJobCircularCheckAllowedTypes(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_CIRCULAR_CHECK_ALLOWED_TYPES.getValue()));
            setPackagingCertificationRollupMarkingJobPerformCircularCheck(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_PERFORM_CIRCULAR_CHECK.getValue()));
            setPackagingCertificationRollupCalculateJobPerformCircularCheck(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_PERFORM_CIRCULAR_CHECK.getValue()));
            setPackagingCertificationRollupCalculateConfigObjectName(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_CONFIG_OBJECT_NAME.getValue()));
            setPackagingCertificationRollupMarkingConfigObjectName(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_MARKING_CONFIG_OBJECT_NAME.getValue()));
            setPackagingCertificationRollupCalculateJobSearchMQLQuery(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_SEARCH_MQL_QUERY.getValue()));
            setPackagingCertificationRollupMarkingJobSearchMQLQuery(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_SEARCH_MQL_QUERY.getValue()));
            setPackagingCertificationRollupCalculateJobStuckEmailMessage(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_STUCK_EMAIL_MESSAGE.getValue()));
            setPackagingCertificationRollupCalculateJobStuckEmailSubject(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_CALCULATE_JOB_STUCK_EMAIL_SUBJECT.getValue()));
            setPackagingCertificationRollupMarkingJobStuckEmailMessage(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_STUCK_EMAIL_MESSAGE.getValue()));
            setPackagingCertificationRollupMarkingJobStuckEmailSubject(properties.getProperty(RollupConstants.Basic.PACKAGING_CERTIFICATION_ROLLUP_MARKING_JOB_STUCK_EMAIL_SUBJECT.getValue()));
            // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - End
        }
    }

    public boolean isLoaded() {
        return loaded;
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

    public String getMarkingConfigBusinessObjectName() {
        return markingConfigBusinessObjectName;
    }

    public void setMarkingConfigBusinessObjectName(String markingConfigBusinessObjectName) {
        this.markingConfigBusinessObjectName = markingConfigBusinessObjectName;
    }

    public String getRollupConfigBusinessObjectName() {
        return rollupConfigBusinessObjectName;
    }

    public void setRollupConfigBusinessObjectName(String rollupConfigBusinessObjectName) {
        this.rollupConfigBusinessObjectName = rollupConfigBusinessObjectName;
    }

    public String getAttributeRollupConfigBusinessObjectName() {
        return attributeRollupConfigBusinessObjectName;
    }

    public void setAttributeRollupConfigBusinessObjectName(String attributeRollupConfigBusinessObjectName) {
        this.attributeRollupConfigBusinessObjectName = attributeRollupConfigBusinessObjectName;
    }

    public String getErrorMessageSendingMailFailed() {
        return errorMessageSendingMailFailed;
    }

    public void setErrorMessageSendingMailFailed(String errorMessageSendingMailFailed) {
        this.errorMessageSendingMailFailed = errorMessageSendingMailFailed;
    }

    public String getAttributeRollupSubject() {
        return attributeRollupSubject;
    }

    public void setAttributeRollupSubject(String attributeRollupSubject) {
        this.attributeRollupSubject = attributeRollupSubject;
    }

    public String getMarkForRollupSubject() {
        return markForRollupSubject;
    }

    public void setMarkForRollupSubject(String markForRollupSubject) {
        this.markForRollupSubject = markForRollupSubject;
    }

    public String getMarkForRollupMessage() {
        return markForRollupMessage;
    }

    public void setMarkForRollupMessage(String markForRollupMessage) {
        this.markForRollupMessage = markForRollupMessage;
    }

    public String getRollupSubject() {
        return rollupSubject;
    }

    public void setRollupSubject(String rollupSubject) {
        this.rollupSubject = rollupSubject;
    }

    public String getRollupMessage() {
        return rollupMessage;
    }

    public void setRollupMessage(String rollupMessage) {
        this.rollupMessage = rollupMessage;
    }

    public String getAttributeRollupMessage() {
        return attributeRollupMessage;
    }

    public void setAttributeRollupMessage(String attributeRollupMessage) {
        this.attributeRollupMessage = attributeRollupMessage;
    }

    public String getRollupPageFileName() {
        return rollupPageFileName;
    }

    public void setRollupPageFileName(String rollupPageFileName) {
        this.rollupPageFileName = rollupPageFileName;
    }

    public String getJobName1() {
        return jobName1;
    }

    public void setJobName1(String jobName1) {
        this.jobName1 = jobName1;
    }

    public String getJobName2() {
        return jobName2;
    }

    public void setJobName2(String jobName2) {
        this.jobName2 = jobName2;
    }

    public String getJobName3() {
        return jobName3;
    }

    public void setJobName3(String jobName3) {
        this.jobName3 = jobName3;
    }

    public String getAttributeRollupInputFilePath() {
        return attributeRollupInputFilePath;
    }

    public void setAttributeRollupInputFilePath(String attributeRollupInputFilePath) {
        this.attributeRollupInputFilePath = attributeRollupInputFilePath;
    }

    public String getAttributeRollupInputFileName() {
        return attributeRollupInputFileName;
    }

    public void setAttributeRollupInputFileName(String attributeRollupInputFileName) {
        this.attributeRollupInputFileName = attributeRollupInputFileName;
    }

    public String getAttributeRollupInputFileExtension() {
        return attributeRollupInputFileExtension;
    }

    public void setAttributeRollupInputFileExtension(String attributeRollupInputFileExtension) {
        this.attributeRollupInputFileExtension = attributeRollupInputFileExtension;
    }

    public String getPreReleaseRollupInputFilePath() {
        return preReleaseRollupInputFilePath;
    }

    public void setPreReleaseRollupInputFilePath(String preReleaseRollupInputFilePath) {
        this.preReleaseRollupInputFilePath = preReleaseRollupInputFilePath;
    }

    public void setPreReleaseRollupInputFilePath() {
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(getAttributeRollupInputFilePath());
        pathBuilder.append(File.separator);
        pathBuilder.append(getAttributeRollupInputFileName());
        pathBuilder.append(RollupConstants.Basic.SYMBOL_DOT.getValue());
        pathBuilder.append(getAttributeRollupInputFileExtension());
        setPreReleaseRollupInputFilePath(pathBuilder.toString());
    }

    public Properties getProperties() {
        return properties;
    }

    // Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - Start

    public String getPackagingCertificationRollupCalculateJobCircularCheckAllowedTypes() {
        return packagingCertificationRollupCalculateJobCircularCheckAllowedTypes;
    }

    public void setPackagingCertificationRollupCalculateJobCircularCheckAllowedTypes(String packagingCertificationRollupCalculateJobCircularCheckAllowedTypes) {
        this.packagingCertificationRollupCalculateJobCircularCheckAllowedTypes = packagingCertificationRollupCalculateJobCircularCheckAllowedTypes;
    }

    public String getPackagingCertificationRollupMarkingJobCircularCheckAllowedTypes() {
        return packagingCertificationRollupMarkingJobCircularCheckAllowedTypes;
    }

    public void setPackagingCertificationRollupMarkingJobCircularCheckAllowedTypes(String packagingCertificationRollupMarkingJobCircularCheckAllowedTypes) {
        this.packagingCertificationRollupMarkingJobCircularCheckAllowedTypes = packagingCertificationRollupMarkingJobCircularCheckAllowedTypes;
    }

    public String getPackagingCertificationRollupMarkingJobPerformCircularCheck() {
        return packagingCertificationRollupMarkingJobPerformCircularCheck;
    }

    public void setPackagingCertificationRollupMarkingJobPerformCircularCheck(String packagingCertificationRollupMarkingJobPerformCircularCheck) {
        this.packagingCertificationRollupMarkingJobPerformCircularCheck = packagingCertificationRollupMarkingJobPerformCircularCheck;
    }

    public String getPackagingCertificationRollupCalculateJobPerformCircularCheck() {
        return packagingCertificationRollupCalculateJobPerformCircularCheck;
    }

    public void setPackagingCertificationRollupCalculateJobPerformCircularCheck(String packagingCertificationRollupCalculateJobPerformCircularCheck) {
        this.packagingCertificationRollupCalculateJobPerformCircularCheck = packagingCertificationRollupCalculateJobPerformCircularCheck;
    }

    public String getPackagingCertificationRollupCalculateConfigObjectName() {
        return packagingCertificationRollupCalculateConfigObjectName;
    }

    public void setPackagingCertificationRollupCalculateConfigObjectName(String packagingCertificationRollupCalculateConfigObjectName) {
        this.packagingCertificationRollupCalculateConfigObjectName = packagingCertificationRollupCalculateConfigObjectName;
    }

    public String getPackagingCertificationRollupMarkingConfigObjectName() {
        return packagingCertificationRollupMarkingConfigObjectName;
    }

    public void setPackagingCertificationRollupMarkingConfigObjectName(String packagingCertificationRollupMarkingConfigObjectName) {
        this.packagingCertificationRollupMarkingConfigObjectName = packagingCertificationRollupMarkingConfigObjectName;
    }

    public String getPackagingCertificationRollupCalculateJobSearchMQLQuery() {
        return packagingCertificationRollupCalculateJobSearchMQLQuery;
    }

    public void setPackagingCertificationRollupCalculateJobSearchMQLQuery(String packagingCertificationRollupCalculateJobSearchMQLQuery) {
        this.packagingCertificationRollupCalculateJobSearchMQLQuery = packagingCertificationRollupCalculateJobSearchMQLQuery;
    }

    public String getPackagingCertificationRollupMarkingJobSearchMQLQuery() {
        return packagingCertificationRollupMarkingJobSearchMQLQuery;
    }

    public void setPackagingCertificationRollupMarkingJobSearchMQLQuery(String packagingCertificationRollupMarkingJobSearchMQLQuery) {
        this.packagingCertificationRollupMarkingJobSearchMQLQuery = packagingCertificationRollupMarkingJobSearchMQLQuery;
    }

    public String getPackagingCertificationRollupCalculateJobStuckEmailMessage() {
        return packagingCertificationRollupCalculateJobStuckEmailMessage;
    }

    public void setPackagingCertificationRollupCalculateJobStuckEmailMessage(String packagingCertificationRollupCalculateJobStuckEmailMessage) {
        this.packagingCertificationRollupCalculateJobStuckEmailMessage = packagingCertificationRollupCalculateJobStuckEmailMessage;
    }

    public String getPackagingCertificationRollupCalculateJobStuckEmailSubject() {
        return packagingCertificationRollupCalculateJobStuckEmailSubject;
    }

    public void setPackagingCertificationRollupCalculateJobStuckEmailSubject(String packagingCertificationRollupCalculateJobStuckEmailSubject) {
        this.packagingCertificationRollupCalculateJobStuckEmailSubject = packagingCertificationRollupCalculateJobStuckEmailSubject;
    }

    public String getPackagingCertificationRollupMarkingJobStuckEmailMessage() {
        return packagingCertificationRollupMarkingJobStuckEmailMessage;
    }

    public void setPackagingCertificationRollupMarkingJobStuckEmailMessage(String packagingCertificationRollupMarkingJobStuckEmailMessage) {
        this.packagingCertificationRollupMarkingJobStuckEmailMessage = packagingCertificationRollupMarkingJobStuckEmailMessage;
    }

    public String getPackagingCertificationRollupMarkingJobStuckEmailSubject() {
        return packagingCertificationRollupMarkingJobStuckEmailSubject;
    }

    public void setPackagingCertificationRollupMarkingJobStuckEmailSubject(String packagingCertificationRollupMarkingJobStuckEmailSubject) {
        this.packagingCertificationRollupMarkingJobStuckEmailSubject = packagingCertificationRollupMarkingJobStuckEmailSubject;
    }
// Added (Sogeti) for Requirement (39920) 2018x.6 May CW 2022 Release - End

    //Added for Defect# 44220 - Starts
    public String getCtrlmCircularCheck() {
        return ctrlmCircularCheck;
    }

    public void setCtrlmCircularCheck(String ctrlmCircularCheck) {
        this.ctrlmCircularCheck = ctrlmCircularCheck;
    }

    public String getAllowedTypesForCircularCheck() {
        return allowedTypesForCircularCheck;
    }
    //Added for Defect# 44220 - Ends

    public void setAllowedTypesForCircularCheck(String allowedTypesForCircularCheck) {
        this.allowedTypesForCircularCheck = allowedTypesForCircularCheck;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());

        Properties properties;
        boolean loaded;
        String customPropertyFile;

        public Builder(String customPropertyFile) {
            this.customPropertyFile = customPropertyFile;
            properties = new Properties();
        }

        public RollupPropertyResource build() {
            try (InputStream inputStream = new FileInputStream(new File(customPropertyFile))) {
                properties.load(inputStream);
                loaded = true;
            } catch (IOException e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
            return new RollupPropertyResource(this);
        }
    }

}
