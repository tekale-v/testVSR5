/*
 **   TextFile.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Implementation class to convert input file to pdf.
 **
 */
package com.pg.dsm.gendoc.itext.services;

import com.itextpdf.text.DocumentException;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.itext.factory.DocumentFactory;
import com.pg.dsm.gendoc.itext.interfaces.PDFDocument;
import com.pg.dsm.gendoc.models.Document;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PDFConvert {
    List<String> errorMessages;
    List<Document> inputDocumentList;
    List<PDFDocument> pdfDocumentList;

    private PDFConvert(Builder builder) {
        this.errorMessages = builder.errorMessages;
        this.pdfDocumentList = builder.pdfDocumentList;
        this.inputDocumentList = builder.inputDocumentList;
    }

    public int getInputDocumentsCount() {
        return inputDocumentList.size();
    }

    public int getConvertedPDFDocumentsCount() {
        return pdfDocumentList.size();
    }

    public List<String> getErrorMessagesUnique() {
        return errorMessages.stream().distinct().collect(Collectors.toList());
    }

    public String getErrorMessage() {
        return String.join(CloudConstants.Basic.SYMBOL_COMMA.getValue(), getErrorMessagesUnique());
    }

    public boolean isSuccessful() {
        return (!errorMessages.isEmpty()) ? Boolean.FALSE : Boolean.TRUE;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<Document> getInputDocumentList() {
        return inputDocumentList;
    }

    public List<PDFDocument> getPdfDocumentList() {
        return pdfDocumentList;
    }

	public static class Builder {
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		ICloudDocument cloudDocument;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		String busType;
		String busName;
		String busRev;
		String busId;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
		List<String> errorMessages;
		List<PDFDocument> pdfDocumentList;
		List<Document> inputDocumentList;

		public Builder(ICloudDocument cloudDocument) {
			this.cloudDocument = cloudDocument;
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
			this.busType = cloudDocument.getObjectType();
			this.busName = cloudDocument.getObjectName();
			this.busRev = cloudDocument.getObjectRevision();
			this.busId = cloudDocument.getObjectOid();
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
			this.errorMessages = new ArrayList<>();
			this.pdfDocumentList = new ArrayList<>();
			this.inputDocumentList = new ArrayList<>();
		}

		private static boolean isAllowedExtension(String fileExtensionsForIText, String fileExtension) {
			// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - Starts
			boolean bIsAllowedExtension = false;
			if (StringUtils.containsIgnoreCase(fileExtensionsForIText, fileExtension)) {
				bIsAllowedExtension = true;
			}
			if (CloudConstants.Basic.FILE_EXTENSION_PDF.getValue().equalsIgnoreCase(fileExtension)) {
				bIsAllowedExtension = false;
			}
			return bIsAllowedExtension;
			// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - Ends

		}

		/**
		 * @return
		 */
		public PDFConvert convert() {
			// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
			// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
			Instant startTime = Instant.now();
			String fileExtensionsForIText = cloudDocument.getCloudConfig().getFileExtensionsForIText();
			String inWorkDirPath = cloudDocument.getInWorkDir();
			String localDownloadPath = cloudDocument.getAbsoluteDownloadDir();

			File downloadFolder = new File(localDownloadPath);
			if (!downloadFolder.exists()) {
				downloadFolder.mkdirs();
			}
			File inWorkDir = new File(inWorkDirPath);
			if (inWorkDir.isDirectory()) {
				File[] files = inWorkDir.listFiles();
				logger.info(String.format("Number of File(s) in In-Work Dir Folder: %s|For Object|%s|%s|%s|%s", files.length, busType, busName, busRev, busId));
				if (null != files && files.length > 0) {
					CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
					String fileName;
					String fileExtension;
					Document document;
					PDFDocument pdfDocument;

                    for (File file : files) {
                        fileName = file.getName();
                        try {
                            document = cloudGenDocUtil.getDocument(file, cloudDocument, fileName);
                            fileExtension = document.getFileExtension();

							if (isAllowedExtension(fileExtensionsForIText, fileExtension)) {
								inputDocumentList.add(document);
								// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - Starts
								pdfDocument = DocumentFactory.getDocType(document, localDownloadPath, fileExtensionsForIText).getPDFDocument();
								// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - Ends
								if (null != pdfDocument) {
									pdfDocumentList.add(pdfDocument);
								} else {
									logger.error(String.format("%s|iText Failed to Convert to Pdf|For Object|%s|%s|%s|%s", fileName, busType, busName, busRev, busId));
								}
							}
						} catch (JSONException | InvocationTargetException | IllegalAccessException | IOException | DocumentException e) {
							logger.error(e);
							logger.error(String.format("iText PDF Convert - Was Not Successful for File: %s|For Object|%s|%s|%s|%s", fileName, busType, busName, busRev, busId));
							errorMessages.add(e.getMessage());
						}
					}
				}
			}
			logger.info(String.format("%s File(s) TO-BE converted to Pdf using iText|For Object|%s|%s|%s|%s", inputDocumentList.size(), busType, busName, busRev, busId));
			logger.info(String.format("%s File(s) CONVERTED to Pdf using iText Are # %s|For Object|%s|%s|%s|%s", pdfDocumentList.size(), pdfDocumentList.stream().map(PDFDocument::getFileName).collect(Collectors.toList()), busType, busName, busRev, busId));

			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.info(String.format("%s File(s) CONVERTED to Pdf using iText|Took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s", pdfDocumentList.size(), duration.toMillis(), duration.getSeconds(), duration.toMinutes(), busType, busName, busRev, busId));
			return new PDFConvert(this);
		}
	}
}
