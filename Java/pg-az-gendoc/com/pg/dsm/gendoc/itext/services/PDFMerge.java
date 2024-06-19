/*
 **   PDFMerge.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Implementation class to merge input pdf(s) to single pdf
 **
 */
package com.pg.dsm.gendoc.itext.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PDFMerge {
    List<String> errorMessages;
    String mergedFilePath;

    private PDFMerge(Builder builder) {
        this.errorMessages = builder.errorMessages;
        this.mergedFilePath = builder.mergedFilePath;
    }

    public String getErrorMessage() {
        return String.join(CloudConstants.Basic.SYMBOL_COMMA.getValue(), getErrorMessagesUnique());
    }

    public boolean isSuccessful() {
        return (!errorMessages.isEmpty()) ? Boolean.FALSE : Boolean.TRUE;
    }

    public List<String> getErrorMessagesUnique() {
        return errorMessages.stream().distinct().collect(Collectors.toList());
    }

    public String getMergedFilePath() {
        return mergedFilePath;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public static class Builder {

		private final Logger logger = Logger.getLogger(this.getClass().getName());
		List<String> errorMessages;
		ICloudDocument cloudDocument;
		boolean usePdfFileName;
		String mergedFilePath;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		String busType;
		String busName;
		String busRev;
		String busId;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

		public Builder(ICloudDocument cloudDocument) {
			this.cloudDocument = cloudDocument;
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
			this.busType = cloudDocument.getObjectType();
			this.busName = cloudDocument.getObjectName();
			this.busRev = cloudDocument.getObjectRevision();
			this.busId = cloudDocument.getObjectOid();
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
			this.errorMessages = new ArrayList<>();
		}

        public PDFMerge build(boolean usePdfFileName) {
            this.usePdfFileName = usePdfFileName;
            merge();
            return new PDFMerge(this);
        }


		private void merge() {
			// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
			// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
			Instant startTime = Instant.now();
			String absoluteDownloadDir = cloudDocument.getAbsoluteDownloadDir();
			String objectName = cloudDocument.getObjectName();
			String objectRevision = cloudDocument.getObjectRevision();
			File localDownloadFolder = new File(absoluteDownloadDir);
			if (localDownloadFolder.exists()) {
				String mergeDir = cloudDocument.getAbsoluteMergeDir();
				File localMergeFolder = new File(mergeDir);
				if (!localMergeFolder.exists()) {
					localMergeFolder.mkdirs();
				}
				StringBuilder pathBuilder = new StringBuilder();
				pathBuilder.append(mergeDir);
				pathBuilder.append(File.separator);
				if (usePdfFileName) {
					pathBuilder.append(cloudDocument.getPdfFileName());
					this.mergedFilePath = pathBuilder.toString();
				} else {
					pathBuilder.append(objectName);
					pathBuilder.append(CloudConstants.Basic.SYMBOL_HYPHEN.getValue());
					pathBuilder.append(CloudConstants.Basic.KEYWORD_REV.getValue());
					pathBuilder.append(objectRevision);
					pathBuilder.append(CloudConstants.Basic.DOT_FILE_EXTENSION_PDF);
					this.mergedFilePath = pathBuilder.toString();
				}
				if (localDownloadFolder.isDirectory()) {
					String timeStamp = cloudDocument.getTimeStamp();
					String filePath = getFirstPDF(localDownloadFolder, timeStamp);
					try (PdfDocument pdfFinalFile = new PdfDocument(new PdfWriter(mergedFilePath))) {
						if (UIUtil.isNotNullAndNotEmpty(filePath)) {
							DecryptPdfReader decryptPdfReader = new DecryptPdfReader(filePath);
							decryptPdfReader.setUnethicalReading(true);
							decryptPdfReader.decryptOnPurpose();
							try (PdfDocument pdfDoc = new PdfDocument(decryptPdfReader)) {
								pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfFinalFile);
							}
						}
						mergePDF(localDownloadFolder, pdfFinalFile, timeStamp);
						if (!errorMessages.isEmpty()) {
							logger.info(String.format("PDF Merge was NOT Successful|%s|%s|%s|%s|", busType, busName, busRev, busId));
						} else {
							logger.info(String.format("PDF Merge was Successful|For Object|%s|%s|%s|%s|", busType, busName, busRev, busId));
						}
					} catch (IOException e) {
						errorMessages.add(e.getMessage());
						logger.error(e);
					}
				}
			}
			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.info(String.format("iText PDF Merge - took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s|", duration.toMillis(), duration.getSeconds(), duration.toMinutes(), busType, busName, busRev, busId));
		}

		/**
		 * @param path
		 * @param timeStamp
		 * @return
		 */
		private String getFirstPDF(File path, String timeStamp) {
			// Refine logs 2018x.6 June-CW (43327)
			Instant startTime = Instant.now();
			File[] files = path.listFiles();
			String fileNameWithoutExtension;
			String filePath = DomainConstants.EMPTY_STRING;
			String fileName;
			if (path.isDirectory()) {
				// Refactoring 2018x.6 July-CW (Defect 43669) by DSM Sogeti
				long fileSize;
				for (File file : files) {
					fileName = file.getName();
					fileSize = file.length();
					if (fileSize > 0) {
						fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
						if (fileNameWithoutExtension.contains(timeStamp)) {
							filePath = file.getPath();
						}
					} else {
						logger.error(String.format("First PDF Page - File Size is 0 KB: %s|For Object|%s|%s|%s|%s|", fileName, busType, busName, busRev, busId));
						errorMessages.add(CloudConstants.Basic.ERROR_FILE_SIZE_ZERO_KB.getValue());
					}
				}
			}

			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.info(String.format("Find First PDF - took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s|", duration.toMillis(), duration.getSeconds(), duration.toMinutes(), busType, busName, busRev, busId));
			return filePath;
		}

		/**
		 * @param path
		 * @param pdfFinalFile
		 * @throws java.io.IOException
		 */
		private void mergePDF(File path, PdfDocument pdfFinalFile, String timeStamp) {
            // Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
            // Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
            Instant startTime = Instant.now();
            File[] files = path.listFiles();
            if (path.isDirectory()) {
                String fileNameWithoutExtn;
                String filePath;
                String fileName;
                long fileSize;
                // Modified by (IRM-Sogeti) for 22x.02 May CW (Defect-45747) - Starts
                if (null != files && files.length > 0) {
                    CloudGenDocUtil genDocUtil = new CloudGenDocUtil();
                    MapList resultList = genDocUtil.getFileNameInSortedOrder(files);
		    File file;
                    for (Object object : resultList) {
                        file = (File) ((Map<Object, Object>) object).get("file");
                        // Modified by (IRM-Sogeti) for 22x.02 May CW (Defect-45747) - End
                        fileName = file.getName();
                        filePath = file.getPath();
                        fileSize = file.length();
                        logger.info(String.format("File Name|%s", fileName));
                        if (fileSize > 0) {
                            fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
                            if (!fileNameWithoutExtn.contains(timeStamp)) {
                                try {
                                    DecryptPdfReader decryptPdfReader = new DecryptPdfReader(filePath);
                                    decryptPdfReader.setUnethicalReading(true);
                                    decryptPdfReader.decryptOnPurpose();
                                    try (PdfDocument pdfDoc = new PdfDocument(decryptPdfReader)) {
                                        pdfDoc.copyPagesTo(1, pdfDoc.getNumberOfPages(), pdfFinalFile);
                                    }
                                } catch (IOException e) {
                                    errorMessages.add(e.getMessage());
                                    logger.error(e);
                                    logger.error(String.format("Error Merging File|%s|For Object|%s|%s|%s|%s|", fileName, busType, busName, busRev, busId));
                                }
                            }
                        } else {
                            errorMessages.add(CloudConstants.Basic.ERROR_FILE_SIZE_ZERO_KB.getValue());
                            logger.error(String.format("PDF Merge - File Size is 0 KB|For Object|%s|%s|%s|%s|", busType, busName, busRev, busId));
                        }
                    }
                }
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info(String.format("PDF Merge - took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s|", duration.toMillis(), duration.getSeconds(), duration.toMinutes(), busType, busName, busRev, busId));
        }
    }

    static class DecryptPdfReader extends PdfReader {
        private static final long serialVersionUID = 1L;

        public DecryptPdfReader(String filename) throws IOException {
            super(filename);
        }

        public void decryptOnPurpose() {
            encrypted = false;
        }
    }
}



