/*
 **   GenDocBlobContainer.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains method to get cloud blob container instance
 **
 */
package com.pg.dsm.gendoc.cloud;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;

public class GenDocBlobContainer {

    String errorMessage;
    boolean loaded;
    CloudBlobContainer cloudBlobContainer;

    private GenDocBlobContainer(Builder builder) {
        this.loaded = builder.loaded;
        this.errorMessage = builder.errorMessage;
        this.cloudBlobContainer = builder.cloudBlobContainer;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public CloudBlobContainer getCloudBlobContainer() {
        return cloudBlobContainer;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        String blobContainerName;
        String errorMessage;
        boolean loaded;
        CloudBlobContainer cloudBlobContainer;

        public Builder(String blobContainerName) {
            this.blobContainerName = blobContainerName;
        }

        /**
         * @param cloudBlobClient
         * @return
         */
        public GenDocBlobContainer build(CloudBlobClient cloudBlobClient) {
            Instant startTime = Instant.now();
            try {
                this.cloudBlobContainer = cloudBlobClient.getContainerReference(blobContainerName);
                cloudBlobContainer.createIfNotExists();
                logger.info("Cloud Blob Storage Container created/exist");
                this.loaded = true;
            } catch (URISyntaxException | StorageException e) {
                this.loaded = false;
                this.errorMessage = e.getMessage();
                logger.error(e);
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info(String.format("Cloud Blob Client Container Instantiation - took|%s ms|%s sec|%s min", duration.toMillis(), duration.getSeconds(), duration.toMinutes()));
            return new GenDocBlobContainer(this);
        }
    }
}
