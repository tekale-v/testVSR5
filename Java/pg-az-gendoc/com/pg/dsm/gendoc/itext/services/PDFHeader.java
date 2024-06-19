/*
 **   PDFHeader.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Implementation class to create header.
 **
 */
package com.pg.dsm.gendoc.itext.services;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class PDFHeader {
    boolean isHeaderCreated;
    String errorMessage;

    private PDFHeader(Builder builder) {
        this.errorMessage = builder.errorMessage;
        this.isHeaderCreated = builder.isHeaderCreated;
    }

    public boolean isHeaderCreated() {
        return isHeaderCreated;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        String errorMessage;
        ICloudDocument cloudDocument;
        boolean isHeaderCreated;

        public Builder(ICloudDocument cloudDocument) {
            this.cloudDocument = cloudDocument;
        }

        public PDFHeader build(String filePath) {
            buildHeader(filePath);
            return new PDFHeader(this);
        }

		/**
		 * @param filePath
		 */
		public void buildHeader(String filePath) {
			// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
			String inWorkDir = cloudDocument.getInWorkDir();
			File mergeFile = new File(filePath);
			if (mergeFile.exists()) {
				String fileName = mergeFile.getName();
				String mergeFilePath = mergeFile.getPath();
				String inWorkFilePath = inWorkDir.concat(File.separator).concat(fileName);
				CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
				cloudGenDocUtil.fileCopy(mergeFile.getPath(), inWorkFilePath);
				File inWorkFile = new File(inWorkFilePath);
				if (inWorkFile.exists()) {
					try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(inWorkFile.getPath()), new PdfWriter(mergeFilePath))) {
						addHeader(pdfDocument);
						this.isHeaderCreated = true;
					} catch (IOException e) {
						this.isHeaderCreated = false;
						errorMessage = e.getMessage();
						logger.error(e);
					}
				}
			}
		}

        /**
         * @param pdfDocument
         * @throws java.io.IOException
         */
        private void addHeader(PdfDocument pdfDocument) throws IOException {
            try (Document document = new Document(pdfDocument)) {
                int numberOfPages = pdfDocument.getNumberOfPages();
                addHeader(pdfDocument, document, numberOfPages);
            }
        }

        /**
         * @param document
         * @param iTotalPages
         * @param pdfDocument
         * @throws Exception
         */
        private void addHeader(PdfDocument pdfDocument, Document document, int iTotalPages) throws IOException {
            String sDocTitle = cloudDocument.getTitle();
            String sIPClassification = cloudDocument.getiPClassification().toUpperCase();
            PdfFont fontTimesBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

            float fHeight;
            float fWidth;
            int xAxis;
            int yAxis;
            DeviceCmyk redColor;

            for (int i = 1; i <= iTotalPages; i++) {

                fHeight = pdfDocument.getPage(i).getPageSize().getHeight();
                fWidth = pdfDocument.getPage(i).getPageSize().getWidth();
                xAxis = (int) fWidth / 2;
                yAxis = (int) fHeight - 20;

                redColor = Color.convertRgbToCmyk(new DeviceRgb(255, 0, 0));
                if (i == 1) {
                    document.showTextAligned(new Paragraph(sDocTitle.trim()).setFont(fontTimesBold).setFontSize(11).setFontColor(redColor), xAxis, yAxis, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
                } else {
                    document.showTextAligned(new Paragraph(sDocTitle.trim()).setFont(fontTimesBold).setFontSize(11).setFontColor(redColor), fWidth / 2, fHeight - 20, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
                    document.showTextAligned(new Paragraph(sIPClassification.trim()).setFont(fontTimesBold).setFontSize(11).setFontColor(redColor), fWidth / 2, fHeight - 35, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
                }
            }
        }
    }
}
