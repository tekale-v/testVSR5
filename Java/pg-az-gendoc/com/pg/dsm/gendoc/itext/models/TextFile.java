/*
 **   TextFile.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Implementation class to convert text file to pdf.
 **
 */
package com.pg.dsm.gendoc.itext.models;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.itext.interfaces.PDFDocument;
import com.pg.dsm.gendoc.models.Document;

public class TextFile implements PDFDocument {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    protected String id;
    protected String type;
    protected String name;
    protected String revision;
    protected String fileName;
    protected String filesCount;
    protected String checkOutPath;
    protected String relativeCheckOutDir;
    protected String relativeCheckOutDirName;
    protected String absoluteCheckOutDir;
    protected String absoluteCheckOutDirName;
    protected String absoluteCheckOutFilePath;
    protected String fileExtension;
    protected String blobUploadRelativeFilePath;
    protected String blobUploadRelativeFileDir;
    protected String blobUploadRelativeDir;
    protected String uploadResponse;
    protected String downloadResponse;
    protected String absoluteDownloadFilePath;
    protected String absoluteDownloadDir;
    protected String loaded;
    protected String isPdfGenerated;
    protected String pdfGenErrorMsg;
    protected String timeOut;

    Document document;
    String localDownloadPath;

    public TextFile(Document document, String localDownloadPath) {
        this.document = document;
        this.localDownloadPath = localDownloadPath;
    }

    public TextFile() {
    }

	@Override
	public PDFDocument getPDFDocument() throws DocumentException, IOException, InvocationTargetException, IllegalAccessException {
		// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
		// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
		PDFDocument pdfDocument = new TextFile();
		String sFileName = document.getFileName();
		String fileNameWithOutExtension = FilenameUtils.removeExtension(sFileName);
		com.itextpdf.text.Document iTextDocument = new com.itextpdf.text.Document();
		try {
			StringBuilder downloadPathBuilder = new StringBuilder();
			downloadPathBuilder.append(localDownloadPath);
			downloadPathBuilder.append(File.separator);
			downloadPathBuilder.append(fileNameWithOutExtension);
			downloadPathBuilder.append(CloudConstants.Basic.DOT_FILE_EXTENSION_PDF.getValue());

            StringBuilder inWorkPathBuilder = new StringBuilder();
            inWorkPathBuilder.append(document.getInWorkDir());
            inWorkPathBuilder.append(File.separator);
            inWorkPathBuilder.append(sFileName);

            BeanUtils.copyProperties(pdfDocument, document);
            PdfWriter writer = PdfWriter.getInstance(iTextDocument,
                    new FileOutputStream(downloadPathBuilder.toString()));
            writer.open();
            iTextDocument.open();
            Paragraph paragraph = new Paragraph(getFileContent(inWorkPathBuilder.toString()));
            iTextDocument.add(paragraph);
            iTextDocument.close();
            writer.close();
            pdfDocument.setIsPdfGenerated(String.valueOf(Boolean.TRUE));

		} catch (IOException | DocumentException | IllegalAccessException | InvocationTargetException e) {
			pdfDocument.setIsPdfGenerated(String.valueOf(Boolean.FALSE));
			logger.error(e);
			throw e;
		}
		return pdfDocument;
	}

    private String getFileContent(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRevision() {
        return revision;
    }

    @Override
    public void setRevision(String revision) {
        this.revision = revision;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFilesCount() {
        return filesCount;
    }

    @Override
    public void setFilesCount(String filesCount) {
        this.filesCount = filesCount;
    }

    @Override
    public String getCheckOutPath() {
        return checkOutPath;
    }

    @Override
    public void setCheckOutPath(String checkOutPath) {
        this.checkOutPath = checkOutPath;
    }

    @Override
    public String getRelativeCheckOutDir() {
        return relativeCheckOutDir;
    }

    @Override
    public void setRelativeCheckOutDir(String relativeCheckOutDir) {
        this.relativeCheckOutDir = relativeCheckOutDir;
    }

    @Override
    public String getRelativeCheckOutDirName() {
        return relativeCheckOutDirName;
    }

    @Override
    public void setRelativeCheckOutDirName(String relativeCheckOutDirName) {
        this.relativeCheckOutDirName = relativeCheckOutDirName;
    }

    @Override
    public String getAbsoluteCheckOutDir() {
        return absoluteCheckOutDir;
    }

    @Override
    public void setAbsoluteCheckOutDir(String absoluteCheckOutDir) {
        this.absoluteCheckOutDir = absoluteCheckOutDir;
    }

    @Override
    public String getAbsoluteCheckOutDirName() {
        return absoluteCheckOutDirName;
    }

    @Override
    public void setAbsoluteCheckOutDirName(String absoluteCheckOutDirName) {
        this.absoluteCheckOutDirName = absoluteCheckOutDirName;
    }

    @Override
    public String getAbsoluteCheckOutFilePath() {
        return absoluteCheckOutFilePath;
    }

    @Override
    public void setAbsoluteCheckOutFilePath(String absoluteCheckOutFilePath) {
        this.absoluteCheckOutFilePath = absoluteCheckOutFilePath;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public String getBlobUploadRelativeFilePath() {
        return blobUploadRelativeFilePath;
    }

    @Override
    public void setBlobUploadRelativeFilePath(String blobUploadRelativeFilePath) {
        this.blobUploadRelativeFilePath = blobUploadRelativeFilePath;
    }

    @Override
    public String getBlobUploadRelativeFileDir() {
        return blobUploadRelativeFileDir;
    }

    @Override
    public void setBlobUploadRelativeFileDir(String blobUploadRelativeFileDir) {
        this.blobUploadRelativeFileDir = blobUploadRelativeFileDir;
    }

    @Override
    public String getBlobUploadRelativeDir() {
        return blobUploadRelativeDir;
    }

    @Override
    public void setBlobUploadRelativeDir(String blobUploadRelativeDir) {
        this.blobUploadRelativeDir = blobUploadRelativeDir;
    }

    @Override
    public String getUploadResponse() {
        return uploadResponse;
    }

    @Override
    public void setUploadResponse(String uploadResponse) {
        this.uploadResponse = uploadResponse;
    }

    @Override
    public String getDownloadResponse() {
        return downloadResponse;
    }

    @Override
    public void setDownloadResponse(String downloadResponse) {
        this.downloadResponse = downloadResponse;
    }

    @Override
    public String getAbsoluteDownloadFilePath() {
        return absoluteDownloadFilePath;
    }

    @Override
    public void setAbsoluteDownloadFilePath(String absoluteDownloadFilePath) {
        this.absoluteDownloadFilePath = absoluteDownloadFilePath;
    }

    @Override
    public String getAbsoluteDownloadDir() {
        return absoluteDownloadDir;
    }

    @Override
    public void setAbsoluteDownloadDir(String absoluteDownloadDir) {
        this.absoluteDownloadDir = absoluteDownloadDir;
    }

    @Override
    public String getLoaded() {
        return loaded;
    }

    @Override
    public void setLoaded(String loaded) {
        this.loaded = loaded;
    }

    @Override
    public String getIsPdfGenerated() {
        return isPdfGenerated;
    }

    @Override
    public void setIsPdfGenerated(String isPdfGenerated) {
        this.isPdfGenerated = isPdfGenerated;
    }

    @Override
    public String getPdfGenErrorMsg() {
        return pdfGenErrorMsg;
    }

    @Override
    public void setPdfGenErrorMsg(String pdfGenErrorMsg) {
        this.pdfGenErrorMsg = pdfGenErrorMsg;
    }

    @Override
    public String getTimeOut() {
        return timeOut;
    }

    @Override
    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }
}
