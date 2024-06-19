/*
 **   GenDocWorkDir.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains method to upload work directory text file.
 **
 */
package com.pg.dsm.gendoc.cloud;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenDocWorkDir {
    List<String> errorMessages;

    private GenDocWorkDir(Builder builder) {
        this.errorMessages = builder.errorMessages;
    }

    public boolean isUploadSuccessful() {
        return (!errorMessages.isEmpty()) ? Boolean.FALSE : Boolean.TRUE;
    }

    public String getErrorMessage() {
        return String.join(CloudConstants.Basic.SYMBOL_COMMA.getValue(), getErrorMessagesUnique());
    }

    public List<String> getErrorMessagesUnique() {
        return errorMessages.stream().distinct().collect(Collectors.toList());
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

	public static class Builder {
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		CloudBlobContainer cloudBlobContainer;
		ICloudDocument cloudDocument;

		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
		String type;
		String name;
		String revision;
		String id;
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

		List<String> errorMessages;

		public Builder(CloudBlobContainer cloudBlobContainer, ICloudDocument cloudDocument) {
			this.cloudBlobContainer = cloudBlobContainer;
			this.cloudDocument = cloudDocument;
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
			this.type = cloudDocument.getObjectType();
			this.name = cloudDocument.getObjectName();
			this.revision = cloudDocument.getObjectRevision();
			this.id = cloudDocument.getObjectOid();
			// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends
			this.errorMessages = new ArrayList<>();
		}

		public GenDocWorkDir upload() {
			// Refine logs - Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
			try {
				Instant startTime = Instant.now();
				upload(Boolean.TRUE);
				Instant endTime = Instant.now();
				Duration duration = Duration.between(startTime, endTime);
				logger.info(String.format("### Work File UPLOADED to Azure|%s|Took|%s ms|%s sec|%s min|For Object|%s|%s|%s|%s|", cloudDocument.getWorkDirFile(), duration.toMillis(), duration.getSeconds(), duration.toMinutes(), type, name, revision, id));
			} catch (StorageException | URISyntaxException e) {
				errorMessages.add(e.getMessage());
				logger.info(String.format("Work File Upload to Azure Failed|For Object|%s|%s|%s|%s|", type, name, revision, id));
				logger.error(e);
			}
			return new GenDocWorkDir(this);
		}

		private void upload(boolean flag) throws URISyntaxException, StorageException {
			// Refine logs - Modified by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669
			if (flag) {
				CloudBlockBlob blockBlobReference = cloudBlobContainer.getBlockBlobReference(cloudDocument.getWorkDirFile());
				File source = new File(cloudDocument.getWorkDirFilePath());
				try (FileInputStream sourceStream = new FileInputStream(source)) {
					blockBlobReference.upload(sourceStream, source.length());
				} catch (IOException e) {
					errorMessages.add(e.getMessage());
					logger.info(String.format("Work File Upload to Azure Failed|For Object|%s|%s|%s|%s|", type, name, revision, id));
					logger.error(e);
				}
			}
		}
	}
}
