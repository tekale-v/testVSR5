/*
 **   PDFDocument.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Interface class
 **
 */
package com.pg.dsm.gendoc.itext.interfaces;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface PDFDocument {

    PDFDocument getPDFDocument() throws InvocationTargetException, IllegalAccessException, IOException, DocumentException;

    public String getId();

    public void setId(String id);

    public String getType();

    public void setType(String type);

    public String getName();

    public void setName(String name);

    public String getRevision();

    public void setRevision(String revision);

    public String getFileName();

    public void setFileName(String fileName);

    public String getFilesCount();

    public void setFilesCount(String filesCount);

    public String getCheckOutPath();

    public void setCheckOutPath(String checkOutPath);

    public String getRelativeCheckOutDir();

    public void setRelativeCheckOutDir(String relativeCheckOutDir);

    public String getRelativeCheckOutDirName();

    public void setRelativeCheckOutDirName(String relativeCheckOutDirName);

    public String getAbsoluteCheckOutDir();

    public void setAbsoluteCheckOutDir(String absoluteCheckOutDir);

    public String getAbsoluteCheckOutDirName();

    public void setAbsoluteCheckOutDirName(String absoluteCheckOutDirName);

    public String getAbsoluteCheckOutFilePath();

    public void setAbsoluteCheckOutFilePath(String absoluteCheckOutFilePath);

    public String getFileExtension();

    public void setFileExtension(String fileExtension);

    public String getBlobUploadRelativeFilePath();

    public void setBlobUploadRelativeFilePath(String blobUploadRelativeFilePath);

    public String getBlobUploadRelativeFileDir();

    public void setBlobUploadRelativeFileDir(String blobUploadRelativeFileDir);

    public String getBlobUploadRelativeDir();

    public void setBlobUploadRelativeDir(String blobUploadRelativeDir);

    public String getUploadResponse();

    public void setUploadResponse(String uploadResponse);

    public String getDownloadResponse();

    public void setDownloadResponse(String downloadResponse);

    public String getAbsoluteDownloadFilePath();

    public void setAbsoluteDownloadFilePath(String absoluteDownloadFilePath);

    public String getAbsoluteDownloadDir();

    public void setAbsoluteDownloadDir(String absoluteDownloadDir);

    public String getLoaded();

    public void setLoaded(String loaded);

    public String getIsPdfGenerated();

    public void setIsPdfGenerated(String isPdfGenerated);

    public String getPdfGenErrorMsg();

    public void setPdfGenErrorMsg(String pdfGenErrorMsg);

    public String getTimeOut();

    public void setTimeOut(String timeOut);
}
