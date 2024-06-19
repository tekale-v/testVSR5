/*
 **   AzureBlobFileWatch.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6 July CW Defect 43669
 **   About - Contains method to watch the blob download directory.
 **
 */
package com.pg.dsm.gendoc.cloud.models;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AzureBlobFileWatch {
	Set<String> blobPdfFiles;
	int blobPdfCount;
	boolean isBlobCountTimedOut;
	boolean isUploadAndDownloadBlobFilesCountMatch;
	CloudBlobDirectory directoryReference;

	private AzureBlobFileWatch(ProcessBuilder processBuilder) {
		this.blobPdfFiles = processBuilder.blobPdfFiles;
		this.blobPdfCount = processBuilder.blobPdfCount;
		this.isUploadAndDownloadBlobFilesCountMatch = processBuilder.isUploadAndDownloadBlobFilesCountMatch;
		this.isBlobCountTimedOut = processBuilder.isBlobCountTimedOut;
		this.directoryReference = processBuilder.directoryReference;
	}

	public CloudBlobDirectory getDirectoryReference() {
		return directoryReference;
	}

	public boolean isBlobCountTimedOut() {
		return isBlobCountTimedOut;
	}

	public int getBlobPdfCount() {
		return blobPdfCount;
	}

	public Set<String> getBlobPdfFiles() {
		return blobPdfFiles;
	}

	public boolean isUploadAndDownloadBlobFilesCountMatch() {
		return isUploadAndDownloadBlobFilesCountMatch;
	}

	public static class ProcessBuilder {
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		ICloudDocument cloudDocument;
		String busType;
		String busName;
		String busRev;
		String busId;

		ICloudConfig cloudConfig;
		int uploadedCount;
		CloudBlobContainer cloudBlobContainer;
		CloudBlobDirectory directoryReference;

		Set<String> blobPdfFiles;
		int blobPdfCount;
		boolean isUploadAndDownloadBlobFilesCountMatch;

		boolean isBlobCountTimedOut;

		/**
		 * @param cloudDocument
		 * @param cloudBlobContainer
		 * @param uploadedCount
		 */
		public ProcessBuilder(ICloudDocument cloudDocument, CloudBlobContainer cloudBlobContainer, int uploadedCount) {
			this.cloudDocument = cloudDocument;
			this.cloudConfig = cloudDocument.getCloudConfig();
			this.busType = cloudDocument.getObjectType();
			this.busName = cloudDocument.getObjectName();
			this.busRev = cloudDocument.getObjectRevision();
			this.busId = cloudDocument.getObjectOid();

			this.uploadedCount = uploadedCount;
			this.cloudBlobContainer = cloudBlobContainer;
			this.blobPdfFiles = new HashSet<>();
		}

		/**
		 * @return
		 * @throws URISyntaxException
		 * @throws InterruptedException
		 * @throws StorageException
		 */
		public AzureBlobFileWatch build() throws URISyntaxException, InterruptedException, StorageException {
			StringBuilder pathBuilder = new StringBuilder();
			pathBuilder.append(cloudConfig.getBlobDownloadPath());
			pathBuilder.append(cloudDocument.getRootFolderPrefix());
			pathBuilder.append(CloudConstants.Basic.SYMBOL_FORWARD_SLASH.getValue());
			pathBuilder.append(cloudDocument.getObjectOid());
			pathBuilder.append(CloudConstants.Basic.SYMBOL_FORWARD_SLASH.getValue());
			this.directoryReference = cloudBlobContainer.getDirectoryReference(pathBuilder.toString());
			watchAzureDownloadBlobFolderUntilTimeUp(directoryReference);
			blobPdfCount = blobPdfFiles.size();
			logDownloadBlobAvailableFileNames();
			logger.info(String.format("%s File(s) Available on Azure for Download|For Object|%s|%s|%s|%s|", blobPdfCount, busType, busName, busRev, busId));
			return new AzureBlobFileWatch(this);
		}

		/**
		 * @param directoryReference
		 * @throws InterruptedException
		 * @throws java.net.URISyntaxException
		 * @throws com.microsoft.azure.storage.StorageException
		 */
		private void watchAzureDownloadBlobFolderUntilTimeUp(CloudBlobDirectory directoryReference) throws InterruptedException, URISyntaxException, StorageException {
			Instant startTime = Instant.now();
			final long startWaitTime = System.currentTimeMillis();
			final int waitTime = cloudConfig.getTimer();
			final int sleepInterval = cloudConfig.getSleepInterval();
			if (uploadedCount > 0) {
				this.isUploadAndDownloadBlobFilesCountMatch = false;
				CloudBlob cloudBlob;
				BlobProperties blobProperties;
				long blobSize;
				String blobName;
				do {
					blobPdfCount = directoryReference.listBlobsSegmented().getResults().size();
					final Iterable<ListBlobItem> fileBlobs = directoryReference.listBlobs();
					for (ListBlobItem fileBlob : fileBlobs) {
						if (fileBlob instanceof CloudBlob) {
							cloudBlob = (CloudBlob) fileBlob;
							blobName = cloudBlob.getName();
							if (!blobPdfFiles.contains(blobName)) {
								blobProperties = cloudBlob.getProperties();
								blobSize = blobProperties.getLength();
								if (blobSize > 0) {
									blobPdfFiles.add(blobName);
								}
							}
						}
					}
					if (blobPdfCount == uploadedCount && blobPdfFiles.size() == blobPdfCount) {
						isUploadAndDownloadBlobFilesCountMatch = true;
					}
					if (isUploadAndDownloadBlobFilesCountMatch) {
						logger.info(String.format("Azure Blob Upload & Download folder-files Count Match|For Object|%s|%s|%s|%s|", busType, busName, busRev, busId));
						break;
					} else {
						Thread.sleep(sleepInterval * 100L);
					}
				} while (((System.currentTimeMillis() - startWaitTime) <= waitTime));
			}


			if (!isUploadAndDownloadBlobFilesCountMatch) {
				this.isBlobCountTimedOut = Boolean.TRUE;
				logger.error(String.format("%s File(s) Timed-Out / Azure Blob Upload & Download folder-files count mismatch. Azure Pdf Conversion failed|For Object|%s|%s|%s|%s|", (uploadedCount - blobPdfFiles.size()), busType, busName, busRev, busId));
			}

			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.info(String.format("Watch Blob - took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s|", duration.toMillis(), duration.getSeconds(), duration.toMinutes(), busType, busName, busRev, busId));
		}

		void logDownloadBlobAvailableFileNames() {
			List<String> names = new ArrayList<>();
			File file;
			for(String blob: blobPdfFiles) {
				file = new File(blob);
				names.add(file.getName());
			}
			logger.info(String.format("%s File(s) Available on Azure for Download are|%s|For Object|%s|%s|%s|%s|", names.size(), String.join(CloudConstants.Basic.SYMBOL_COMMA.getValue(), names), busType, busName, busRev, busId));
		}
	}
}


