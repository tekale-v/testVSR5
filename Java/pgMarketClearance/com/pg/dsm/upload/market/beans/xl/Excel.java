/*
 **   Excel.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.xl;

import com.pg.dsm.upload.market.beans.xml.XML;
import matrix.db.Context;

import java.time.Instant;
import java.util.List;

public class Excel {
    private int totalRecords;
    private int numberOfProcessedSuccessRecords;
    private int numberOfProcessedFailedRecords;
    private int numberOfFailedValidationRecords;
    private int numberOfPassedValidationRecords;
    private Context context;
    private XML xml;
    private String path;
    private String errorFilePath;
    private String processedFilePath;
    private String excelProcessMessage;
    private String excelProcessStartTime;
    private List<EachRow> rows;
    private List<String> gcasMarketCombolist;
    private String allowedTypes;
    private String allowedPolicies;
    private boolean isPassed;
    private boolean isPassedClearanceStatusCalculation;
    private boolean isErrorFileCreated;
    private boolean isProcessedFileCreated;
    private boolean isHistoryRecorded;
    private String validationErrorEmailSubject;
    private String validationErrorEmailBody;
    private String processedEmailSubject;
    private String processedEmailBody;
    private Instant execStartTime;

    private Excel(Builder builder) {
        this.isPassed = true;
        this.isPassedClearanceStatusCalculation = true;
        this.isErrorFileCreated = false;
        this.context = builder.context;
        this.xml = builder.xml;
        this.path = builder.path;
        this.rows = builder.rows;
        this.allowedTypes = builder.allowedTypes;
        this.allowedPolicies = builder.allowedPolicies;
        this.validationErrorEmailSubject = builder.validationErrorEmailSubject;
        this.validationErrorEmailBody = builder.validationErrorEmailBody;
        this.processedEmailSubject = builder.processedEmailSubject;
        this.processedEmailBody = builder.processedEmailBody;
        this.totalRecords = this.rows.size();
    }

    public Instant getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Instant execStartTime) {
        this.execStartTime = execStartTime;
    }

    public String getValidationErrorEmailSubject() {
        return validationErrorEmailSubject;
    }

    public void setValidationErrorEmailSubject(String validationErrorEmailSubject) {
        this.validationErrorEmailSubject = validationErrorEmailSubject;
    }

    public String getValidationErrorEmailBody() {
        return validationErrorEmailBody;
    }

    public void setValidationErrorEmailBody(String validationErrorEmailBody) {
        this.validationErrorEmailBody = validationErrorEmailBody;
    }

    public String getProcessedEmailSubject() {
        return processedEmailSubject;
    }

    public void setProcessedEmailSubject(String processedEmailSubject) {
        this.processedEmailSubject = processedEmailSubject;
    }

    public String getProcessedEmailBody() {
        return processedEmailBody;
    }

    public void setProcessedEmailBody(String processedEmailBody) {
        this.processedEmailBody = processedEmailBody;
    }

    public boolean isProcessedFileCreated() {
        return isProcessedFileCreated;
    }

    public void setProcessedFileCreated(boolean isProcessedFileCreated) {
        this.isProcessedFileCreated = isProcessedFileCreated;
    }

    public String getProcessedFilePath() {
        return processedFilePath;
    }

    public void setProcessedFilePath(String processedFilePath) {
        this.processedFilePath = processedFilePath;
    }

    public Context getContext() {
        return context;
    }

    public XML getXml() {
        return xml;
    }

    public String getPath() {
        return path;
    }

    public List<EachRow> getRows() {
        return rows;
    }

    public String getAllowedTypes() {
        return allowedTypes;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean isPassed) {
        this.isPassed = isPassed;
    }

    public String getErrorFilePath() {
        return errorFilePath;
    }

    public void setErrorFilePath(String errorFilePath) {
        this.errorFilePath = errorFilePath;
    }

    public boolean isErrorFileCreated() {
        return isErrorFileCreated;
    }

    public void setErrorFileCreated(boolean isErrorFileCreated) {
        this.isErrorFileCreated = isErrorFileCreated;
    }

    public boolean isPassedClearanceStatusCalculation() {
        return isPassedClearanceStatusCalculation;
    }

    public void setPassedClearanceStatusCalculation(boolean isPassedClearanceStatusCalculation) {
        this.isPassedClearanceStatusCalculation = isPassedClearanceStatusCalculation;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getNumberOfProcessedSuccessRecords() {
        return numberOfProcessedSuccessRecords;
    }

    public void setNumberOfProcessedSuccessRecords(int numberOfProcessedSuccessRecords) {
        this.numberOfProcessedSuccessRecords = numberOfProcessedSuccessRecords;
    }

    public int getNumberOfProcessedFailedRecords() {
        return numberOfProcessedFailedRecords;
    }

    public void setNumberOfProcessedFailedRecords(int numberOfProcessedFailedRecords) {
        this.numberOfProcessedFailedRecords = numberOfProcessedFailedRecords;
    }

    public String getExcelProcessMessage() {
        return excelProcessMessage;
    }

    public void setExcelProcessMessage(String excelProcessMessage) {
        this.excelProcessMessage = excelProcessMessage;
    }

    public int getNumberOfFailedValidationRecords() {
        return numberOfFailedValidationRecords;
    }

    public void setNumberOfFailedValidationRecords(int numberOfFailedValidationRecords) {
        this.numberOfFailedValidationRecords = numberOfFailedValidationRecords;
    }

    public int getNumberOfPassedValidationRecords() {
        return numberOfPassedValidationRecords;
    }

    public void setNumberOfPassedValidationRecords(int numberOfPassedValidationRecords) {
        this.numberOfPassedValidationRecords = numberOfPassedValidationRecords;
    }

    public String getExcelProcessStartTime() {
        return excelProcessStartTime;
    }

    public void setExcelProcessStartTime(String excelProcessStartTime) {
        this.excelProcessStartTime = excelProcessStartTime;
    }

    public boolean isHistoryRecorded() {
        return isHistoryRecorded;
    }

    public void setHistoryRecorded(boolean isHistoryRecorded) {
        this.isHistoryRecorded = isHistoryRecorded;
    }

    public List<String> getGcasMarketCombolist() {
        return gcasMarketCombolist;
    }

    public void setGcasMarketCombolist(List<String> gcasMarketCombolist) {
        this.gcasMarketCombolist = gcasMarketCombolist;
    }

    public String getAllowedPolicies() {
        return allowedPolicies;
    }

    public static class Builder {
        private Context context;
        private XML xml;
        private String path;
        private String validationErrorEmailSubject;
        private String validationErrorEmailBody;
        private String processedEmailSubject;
        private String processedEmailBody;
        private List<EachRow> rows;
        private String allowedTypes;
        private String allowedPolicies;

        public Builder(Context context, XML xml, String allowedTypes, String allowedPolicies, String path, List<EachRow> rows) {
            this.context = context;
            this.xml = xml;
            this.path = path;
            this.rows = rows;
            this.allowedTypes = allowedTypes;
            this.allowedPolicies = allowedPolicies;
            this.validationErrorEmailSubject = xml.getValidationErrorEmailSubject();
            this.validationErrorEmailBody = xml.getValidationErrorEmailBody();
            this.processedEmailSubject = xml.getProcessedEmailSubject();
            this.processedEmailBody = xml.getProcessedEmailBody();
        }

        public Excel build() {
            return new Excel(this);
        }
    }
}
