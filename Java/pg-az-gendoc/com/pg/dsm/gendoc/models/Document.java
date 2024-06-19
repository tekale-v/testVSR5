/*
 **   Document.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Interface class.
 **
 */
package com.pg.dsm.gendoc.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Document {
    @JsonProperty("id")
    protected String id;
    @JsonProperty("type")
    protected String type;
    @JsonProperty("name")
    protected String name;
    @JsonProperty("revision")
    protected String revision;
    @JsonProperty("fileName")
    protected String fileName;
    @JsonProperty("filesCount")
    protected String filesCount;
    @JsonProperty("checkOutPath")
    protected String checkOutPath;
    @JsonProperty("relativeCheckOutDir")
    protected String relativeCheckOutDir;
    @JsonProperty("relativeCheckOutDirName")
    protected String relativeCheckOutDirName;
    @JsonProperty("absoluteCheckOutDir")
    protected String absoluteCheckOutDir;
    @JsonProperty("absoluteCheckOutDirName")
    protected String absoluteCheckOutDirName;
    @JsonProperty("absoluteCheckOutFilePath")
    protected String absoluteCheckOutFilePath;
    @JsonProperty("fileExtension")
    protected String fileExtension;
    @JsonProperty("blobUploadRelativeFilePath")
    protected String blobUploadRelativeFilePath;
    @JsonProperty("blobUploadRelativeFileDir")
    protected String blobUploadRelativeFileDir;
    @JsonProperty("blobUploadRelativeDir")
    protected String blobUploadRelativeDir;
    @JsonProperty("uploadResponse")
    protected String uploadResponse;
    @JsonProperty("downloadResponse")
    protected String downloadResponse;
    @JsonProperty("absoluteDownloadFilePath")
    protected String absoluteDownloadFilePath;
    @JsonProperty("absoluteDownloadDir")
    protected String absoluteDownloadDir;
    @JsonProperty("blobFileDownloadPath")
    protected String blobFileDownloadPath;
    @JsonProperty("blobFileUploadPath")
    protected String blobFileUploadPath;
    @JsonProperty("blobDownloadFileName")
    protected String blobDownloadFileName;
    @JsonProperty("loaded")
    protected String loaded;
    @JsonProperty("isPdfGenerated")
    protected String isPdfGenerated;
    @JsonProperty("pdfGenErrorMsg")
    protected String pdfGenErrorMsg;
    @JsonProperty("timeOut")
    protected String timeOut;

    @JsonProperty("inWorkDir")
    protected String inWorkDir;

    @JsonProperty("inWorkDirName")
    protected String inWorkDirName;

    @JsonProperty("inWorkDirFilePath")
    protected String inWorkDirFilePath;

    @JsonProperty("filePath")
    protected String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(String filesCount) {
        this.filesCount = filesCount;
    }

    public String getCheckOutPath() {
        return checkOutPath;
    }

    public void setCheckOutPath(String checkOutPath) {
        this.checkOutPath = checkOutPath;
    }

    public String getRelativeCheckOutDir() {
        return relativeCheckOutDir;
    }

    public void setRelativeCheckOutDir(String relativeCheckOutDir) {
        this.relativeCheckOutDir = relativeCheckOutDir;
    }

    public String getRelativeCheckOutDirName() {
        return relativeCheckOutDirName;
    }

    public void setRelativeCheckOutDirName(String relativeCheckOutDirName) {
        this.relativeCheckOutDirName = relativeCheckOutDirName;
    }

    public String getAbsoluteCheckOutDir() {
        return absoluteCheckOutDir;
    }

    public void setAbsoluteCheckOutDir(String absoluteCheckOutDir) {
        this.absoluteCheckOutDir = absoluteCheckOutDir;
    }

    public String getAbsoluteCheckOutDirName() {
        return absoluteCheckOutDirName;
    }

    public void setAbsoluteCheckOutDirName(String absoluteCheckOutDirName) {
        this.absoluteCheckOutDirName = absoluteCheckOutDirName;
    }

    public String getAbsoluteCheckOutFilePath() {
        return absoluteCheckOutFilePath;
    }

    public void setAbsoluteCheckOutFilePath(String absoluteCheckOutFilePath) {
        this.absoluteCheckOutFilePath = absoluteCheckOutFilePath;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getBlobUploadRelativeFilePath() {
        return blobUploadRelativeFilePath;
    }

    public void setBlobUploadRelativeFilePath(String blobUploadRelativeFilePath) {
        this.blobUploadRelativeFilePath = blobUploadRelativeFilePath;
    }

    public String getBlobUploadRelativeFileDir() {
        return blobUploadRelativeFileDir;
    }

    public void setBlobUploadRelativeFileDir(String blobUploadRelativeFileDir) {
        this.blobUploadRelativeFileDir = blobUploadRelativeFileDir;
    }

    public String getBlobUploadRelativeDir() {
        return blobUploadRelativeDir;
    }

    public void setBlobUploadRelativeDir(String blobUploadRelativeDir) {
        this.blobUploadRelativeDir = blobUploadRelativeDir;
    }

    public String getUploadResponse() {
        return uploadResponse;
    }

    public void setUploadResponse(String uploadResponse) {
        this.uploadResponse = uploadResponse;
    }

    public String getDownloadResponse() {
        return downloadResponse;
    }

    public void setDownloadResponse(String downloadResponse) {
        this.downloadResponse = downloadResponse;
    }

    public String getAbsoluteDownloadFilePath() {
        return absoluteDownloadFilePath;
    }

    public void setAbsoluteDownloadFilePath(String absoluteDownloadFilePath) {
        this.absoluteDownloadFilePath = absoluteDownloadFilePath;
    }

    public String getBlobFileDownloadPath() {
        return blobFileDownloadPath;
    }

    public Document setBlobFileDownloadPath(String blobFileDownloadPath) {
        this.blobFileDownloadPath = blobFileDownloadPath;
        return this;
    }

    public String getAbsoluteDownloadDir() {
        return absoluteDownloadDir;
    }

    public void setAbsoluteDownloadDir(String absoluteDownloadDir) {
        this.absoluteDownloadDir = absoluteDownloadDir;
    }

    public String getLoaded() {
        return loaded;
    }

    public void setLoaded(String loaded) {
        this.loaded = loaded;
    }

    public String getIsPdfGenerated() {
        return isPdfGenerated;
    }

    public void setIsPdfGenerated(String isPdfGenerated) {
        this.isPdfGenerated = isPdfGenerated;
    }

    public String getPdfGenErrorMsg() {
        return pdfGenErrorMsg;
    }

    public void setPdfGenErrorMsg(String pdfGenErrorMsg) {
        this.pdfGenErrorMsg = pdfGenErrorMsg;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getBlobDownloadFileName() {
        return blobDownloadFileName;
    }

    public Document setBlobDownloadFileName(String blobDownloadFileName) {
        this.blobDownloadFileName = blobDownloadFileName;
        return this;
    }

    public String getBlobFileUploadPath() {
        return blobFileUploadPath;
    }

    public void setBlobFileUploadPath(String blobFileUploadPath) {
        this.blobFileUploadPath = blobFileUploadPath;
    }

    public String getInWorkDir() {
        return inWorkDir;
    }

    public void setInWorkDir(String inWorkDir) {
        this.inWorkDir = inWorkDir;
    }

    public String getInWorkDirName() {
        return inWorkDirName;
    }

    public void setInWorkDirName(String inWorkDirName) {
        this.inWorkDirName = inWorkDirName;
    }

    public String getInWorkDirFilePath() {
        return inWorkDirFilePath;
    }

    public void setInWorkDirFilePath(String inWorkDirFilePath) {
        this.inWorkDirFilePath = inWorkDirFilePath;
    }
}
