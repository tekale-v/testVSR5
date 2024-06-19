/*
 **   GenDocUpload.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains method to upload storage account details.
 **
 */
package com.pg.dsm.gendoc.cloud;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.models.Document;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenDocUpload {
    boolean loaded;
    CloudBlobContainer cloudBlobContainer;
    ICloudDocument cloudDocument;
    int uploadSuccessCount;
    int uploadFailureCount;
    List<String> errorMessages;
    List<Document> uploadResponseList;

	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts-
	String errorMessage;
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

	private GenDocUpload(Builder builder) {
		this.loaded = builder.loaded;
		this.uploadSuccessCount = builder.uploadSuccessCount;
		this.uploadFailureCount = builder.uploadFailureCount;
		this.uploadResponseList = builder.uploadResponseList;
		this.errorMessages = builder.errorMessages;
		this.cloudBlobContainer = builder.cloudBlobContainer;
		this.cloudDocument = builder.cloudDocument;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts-
		this.errorMessage = setErrorMessage();
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
	}

	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts-
	public String getErrorMessage() {
		return errorMessage;
	}

	public String setErrorMessage() {
		return String.join(CloudConstants.Basic.SYMBOL_COMMA.getValue(), errorMessages);
	}
	// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

    public boolean isLoaded() {
        return loaded;
    }

    public List<Document> getUploadResponseList() {
        return uploadResponseList;
    }

    public int getUploadSuccessCount() {
        return uploadSuccessCount;
    }

    public boolean isUploadSuccessful() {
        return (!errorMessages.isEmpty()) ? Boolean.FALSE : Boolean.TRUE;
    }

    public int getUploadFailureCount() {
        return uploadFailureCount;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        boolean loaded;
        CloudBlobContainer cloudBlobContainer;
        ICloudDocument cloudDocument;
        List<Document> documentList;
        List<Document> uploadResponseList;
        ICloudConfig cloudConfig;
        int uploadSuccessCount = 0;
        int uploadFailureCount = 0;

        List<String> errorMessages;

		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		String busType;
		String busName;
		String busRev;
		String busId;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

		public Builder(CloudBlobContainer cloudBlobContainer, ICloudDocument cloudDocument, List<Document> documentList) {
			this.cloudBlobContainer = cloudBlobContainer;
			this.documentList = documentList;
			this.cloudDocument = cloudDocument;
			this.uploadResponseList = new ArrayList<>();
			this.cloudConfig = cloudDocument.getCloudConfig();
			this.errorMessages = new ArrayList<>();
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
			this.busType = cloudDocument.getObjectType();
			this.busName = cloudDocument.getObjectName();
			this.busRev = cloudDocument.getObjectRevision();
			this.busId = cloudDocument.getObjectOid();
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
		}

		public GenDocUpload build() {
			// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
			Instant startTime = Instant.now();
			int filesCount = documentList.size();
			// Refine logs - Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
			logger.info(String.format("### For Object|%s|%s|%s|%s|Blob Dir: %s", busType, busName, busRev, busId, cloudDocument.getWorkDirBlobPath()));
			logger.info(String.format("### For Object|%s|%s|%s|%s|Work Dir: %s", busType, busName, busRev, busId, cloudDocument.getWorkDir()));
			logger.info(String.format("%s File(s) TO-BE sent to Azure|For Object|%s|%s|%s|%s|", filesCount, busType, busName, busRev, busId));
			for (Document document : documentList) {
				uploadResponseList.add(upload(document));
			}
			logger.info(String.format("%s File(s) UPLOADED to Azure Are #|%s|For Object|%s|%s|%s|%s|", filesCount, uploadResponseList.stream().map(Document::getFileName).collect(Collectors.toList()), busType, busName, busRev, busId));
			this.loaded = true;
			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.info(String.format("%s File(s) UPLOADED to Azure|Took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s|", filesCount, duration.toMillis(), duration.getSeconds(), duration.toMinutes(), busType, busName, busRev, busId));
			return new GenDocUpload(this);
		}

		/**
		 * @param document
		 * @return
		 */
		private Document upload(Document document) {
			// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
			// Refine logs - Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
			Document documentResponse = new Document();
			try {
				if (null != document && Boolean.valueOf(document.getLoaded())) {
					BeanUtils.copyProperties(documentResponse, document);
					String absoluteCheckOutFilePath = document.getAbsoluteCheckOutFilePath();
					String blobDownloadFilePath = document.getBlobFileDownloadPath();
					String blobUploadFilePath = document.getBlobFileUploadPath();
					String inWorkDirFilePath = document.getInWorkDirFilePath();
					documentResponse.setBlobFileDownloadPath(blobDownloadFilePath);
					documentResponse.setBlobFileUploadPath(blobUploadFilePath);
					documentResponse.setAbsoluteCheckOutFilePath(absoluteCheckOutFilePath);
					documentResponse.setBlobDownloadFileName(document.getBlobDownloadFileName());

					File localFile = new File(inWorkDirFilePath);
					if (localFile.exists()) {
						CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(blobUploadFilePath);
						if (null != cloudBlockBlob) {
							try (FileInputStream sourceStream = new FileInputStream(localFile)) {
								cloudBlockBlob.upload(sourceStream, localFile.length());
							}
							documentResponse.setUploadResponse(CloudConstants.Basic.SUCCESS_KEY_WORD.getValue());
							this.uploadSuccessCount++;
						} else {
							logger.error("Error getting blob reference");
							// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
							documentResponse.setUploadResponse(CloudConstants.Basic.ERROR_GETTING_BLOB_REFERENCE.getValue());
							errorMessages.add(CloudConstants.Basic.ERROR_GETTING_BLOB_REFERENCE.getValue());
						}
					} else {
						// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
						logger.error(String.format("File does not exist: %s", inWorkDirFilePath));
						documentResponse.setUploadResponse(CloudConstants.Basic.ERROR_LOCAL_DOWNLOAD_PATH_DOES_NOT_EXIST.getValue());
						errorMessages.add(CloudConstants.Basic.ERROR_LOCAL_DOWNLOAD_PATH_DOES_NOT_EXIST.getValue());
					}
				} else {
					// Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
					documentResponse.setUploadResponse(CloudConstants.Basic.ERROR_INPUT_JSON_IS_EMPTY_OR_NULL.getValue());
					errorMessages.add(CloudConstants.Basic.ERROR_INPUT_JSON_IS_EMPTY_OR_NULL.getValue());
					this.uploadFailureCount++;
				}
			} catch (IllegalAccessException | InvocationTargetException | StorageException | URISyntaxException | IOException e) {
				documentResponse.setUploadResponse("error - ".concat(e.getMessage()));
				this.uploadFailureCount++;
				logger.error(e);
				errorMessages.add(e.getMessage());
			}
			return documentResponse;
		}
	}
}
