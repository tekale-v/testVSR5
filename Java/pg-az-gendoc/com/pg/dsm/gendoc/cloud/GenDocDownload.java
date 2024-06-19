/*
 **   GenDocDownload.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains method to download file from cloud blob.
 **
 */
package com.pg.dsm.gendoc.cloud;

import com.matrixone.apps.framework.ui.UIUtil;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.models.Document;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenDocDownload {
    boolean loaded;
    Document documentResponse;
    int downloadSuccessCount;
    int downloadFailureCount;
    List<String> errorMessages;

    private GenDocDownload(Builder builder) {
        this.loaded = builder.loaded;
        this.documentResponse = builder.documentResponse;
        this.downloadSuccessCount = builder.downloadFailureCount;
        this.downloadFailureCount = builder.downloadFailureCount;
        this.errorMessages = builder.errorMessages;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isDownloadSuccessful() {
        return (!errorMessages.isEmpty()) ? Boolean.FALSE : Boolean.TRUE;
    }

    public List<String> getErrorMessagesUnique() {
        return errorMessages.stream().distinct().collect(Collectors.toList());
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public Document getDocumentResponse() {
        return documentResponse;
    }

    public int getDownloadSuccessCount() {
        return downloadSuccessCount;
    }

    public int getDownloadFailureCount() {
        return downloadFailureCount;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        boolean loaded;
        CloudBlobContainer cloudBlobContainer;
        ICloudDocument cloudDocument;
        Document document;
        Document documentResponse;
        ICloudConfig cloudConfig;
        int downloadSuccessCount = 0;
        int downloadFailureCount = 0;
        List<String> errorMessages;

        public Builder(CloudBlobContainer cloudBlobContainer, ICloudDocument cloudDocument, Document document) {
            this.cloudBlobContainer = cloudBlobContainer;
            this.cloudDocument = cloudDocument;
            this.document = document;
            this.cloudConfig = cloudDocument.getCloudConfig();
            this.documentResponse = new Document();
            this.errorMessages = new ArrayList<>();
        }

        public GenDocDownload build() {
            download();
            this.loaded = true;
            return new GenDocDownload(this);
        }

        /**
         * @return
         */
        private Document download() {
            // Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
			// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
            try {
                if (null != document && Boolean.valueOf(document.getLoaded())) {
                    BeanUtils.copyProperties(documentResponse, document);
                    String blobFileDownloadPath = document.getBlobFileDownloadPath();
                    if (UIUtil.isNotNullAndNotEmpty(blobFileDownloadPath)) {
                        CloudBlob cloudBlob = cloudBlobContainer.getBlobReferenceFromServer(blobFileDownloadPath);
						// Modified for 2018x.6 July-CW (Defect 43669) by DSM Sogeti
                        if (null != cloudBlob) {
                            // create same folder structure on local
                            File file = new File(cloudBlob.getName());
                            String name = file.getName();
                            String path = cloudDocument.getAbsoluteDownloadDir();
                            File downloadFolder = new File(path);
                            if (!downloadFolder.exists()) {
                                downloadFolder.mkdirs();
                            }
							// Modified (remove useless variable)for 2018x.6 July-CW (Defect 43669) by DSM Sogeti 
                            String downloadFolderFilePath = downloadFolder.getPath().concat(File.separator).concat(name);
                            documentResponse.setAbsoluteDownloadDir(path);
                            documentResponse.setAbsoluteDownloadFilePath(downloadFolderFilePath);

                            // download the file
                            Instant startTime = Instant.now();
                            cloudBlob.download(new FileOutputStream(downloadFolderFilePath));
                            Instant endTime = Instant.now();
                            Duration duration = Duration.between(startTime, endTime);

                            File downloadedFile = new File(downloadFolderFilePath);
                            if (downloadedFile.exists()) {
								logger.info(String.format("%s|Download Size|%s|Download - took|%s ms|%s sec|%s min", name, downloadedFile.length(), duration.toMillis(), duration.getSeconds(), duration.toMinutes()));
							} else {
								logger.error("Downloaded File Does not exist");
							}
							documentResponse.setDownloadResponse(CloudConstants.Basic.SUCCESS_KEY_WORD.getValue());
							this.downloadSuccessCount++;
						}
					}
				} else {
					documentResponse.setDownloadResponse(CloudConstants.Basic.ERROR_DOCUMENT_ERROR.getValue());
					this.downloadFailureCount++;
				}
			} catch (IllegalAccessException | InvocationTargetException | StorageException | URISyntaxException | FileNotFoundException e) {
				documentResponse.setDownloadResponse(CloudConstants.Basic.ERROR_HYPHEN.getValue().concat(e.getMessage()));
				this.downloadFailureCount++;
				errorMessages.add(e.getMessage());
				// Modified for 2018x.6 July-CW (Defect 43669) by DSM Sogeti - Starts
				if(e.getMessage().contains(CloudConstants.Basic.AZURE_BLOB_DOES_NOT_EXIST_ERROR.getValue())) {
					logger.info("Retry download");
				} else {
					logger.error(e);
				} // Modified for 2018x.6 July-CW (Defect 43669) by DSM Sogeti - Ends
			}
			return documentResponse;
		}
	}
}