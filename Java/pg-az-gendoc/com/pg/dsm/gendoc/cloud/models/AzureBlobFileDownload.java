/*
 **   AzureBlobFileDownload.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6 July CW Defect 43669
 **   About - Contains method to download blob files from blob directory.
 **
 */
package com.pg.dsm.gendoc.cloud.models;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.ListBlobItem;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AzureBlobFileDownload {
	int downloadCount;
	boolean isBlobDownloadSuccessful;
	String errorMessage;
	private AzureBlobFileDownload(ProcessBuilder processBuilder) {
		this.isBlobDownloadSuccessful = processBuilder.isBlobDownloadSuccessful;
		this.downloadCount = processBuilder.downloadCount;
		this.errorMessage = processBuilder.errorMessage;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public boolean isBlobDownloadSuccessful() {
		return isBlobDownloadSuccessful;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static class ProcessBuilder {
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		CloudBlobDirectory cloudBlobDirectory;
		ICloudDocument cloudDocument;
		String downloadDirectory;
		String type;
		String name;
		String revision;
		String id;
		int downloadCount;
		String errorMessage;
		List<String> downloadFileNames;
		boolean isBlobDownloadSuccessful;

		public ProcessBuilder(ICloudDocument cloudDocument, CloudBlobDirectory cloudBlobDirectory) {
			this.cloudDocument = cloudDocument;
			this.downloadDirectory = cloudDocument.getAbsoluteDownloadDir();
			this.type = cloudDocument.getObjectType();
			this.name = cloudDocument.getObjectName();
			this.revision = cloudDocument.getObjectRevision();
			this.id = cloudDocument.getObjectOid();
			this.cloudBlobDirectory = cloudBlobDirectory;
			this.downloadCount = 0;
			this.downloadFileNames = new ArrayList<>();
		}

		public AzureBlobFileDownload build() {
			try {
				downloadCloudBlobDirectoryFiles();
				this.isBlobDownloadSuccessful = Boolean.TRUE;
				logger.info(String.format("%s File(s) Downloaded from Azure are|%s|For Object|%s|%s|%s|%s|", downloadCount, String.join(CloudConstants.Basic.SYMBOL_COMMA.getValue(), downloadFileNames), type, name, revision, id));
			} catch (FileNotFoundException | StorageException | URISyntaxException e) {
				logger.error(e);
				this.errorMessage = e.getMessage();
			}
			return new AzureBlobFileDownload(this);
		}

		private void downloadCloudBlobDirectoryFiles() throws URISyntaxException, StorageException, FileNotFoundException {
			Instant startTime = Instant.now();
			download();
			Instant endTime = Instant.now();
			Duration duration = Duration.between(startTime, endTime);
			logger.info(String.format("%s File(s) Downloaded from Azure took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s", downloadCount, duration.toMillis(), duration.getSeconds(), duration.toMinutes(), type, name, revision, id));
		}

		private void download() throws URISyntaxException, StorageException, FileNotFoundException {
			CloudBlob cloudBlob;
			File fileBlobPdf;
			String blobPdfName;
			final Iterable<ListBlobItem> fileBlobs = cloudBlobDirectory.listBlobs();
			for (ListBlobItem fileBlob : fileBlobs) {
				if (fileBlob instanceof CloudBlob) {
					cloudBlob = (CloudBlob) fileBlob;
					fileBlobPdf = new File(cloudBlob.getName());
					blobPdfName = fileBlobPdf.getName();
					cloudBlob.download(new FileOutputStream(downloadDirectory.concat(File.separator).concat(blobPdfName)));
					downloadCount++;
					downloadFileNames.add(blobPdfName);
				}
			}
		}
	}
}
