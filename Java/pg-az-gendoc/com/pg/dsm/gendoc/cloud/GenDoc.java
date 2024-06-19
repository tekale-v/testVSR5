/*
 **   GenDoc.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains method to get cloud container instance
 **
 */
package com.pg.dsm.gendoc.cloud;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.Instant;

public class GenDoc {
    String errorMessage;
    boolean loaded;
    CloudBlobContainer cloudBlobContainer;

    private GenDoc(Builder builder) {
        this.loaded = builder.loaded;
        this.errorMessage = builder.errorMessage;
        this.cloudBlobContainer = builder.cloudBlobContainer;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public CloudBlobContainer getCloudBlobContainer() {
        return cloudBlobContainer;
    }

    public void setCloudBlobContainer(CloudBlobContainer cloudBlobContainer) {
        this.cloudBlobContainer = cloudBlobContainer;
    }

    public static class Builder {

        private final Logger logger = Logger.getLogger(this.getClass().getName());
        ICloudConfig cloudConfig;
        String errorMessage;
        boolean loaded;
        CloudBlobContainer cloudBlobContainer;

        public Builder(ICloudConfig cloudConfig) {
            this.cloudConfig = cloudConfig;
        }

        /**
         * @return
         */
        public GenDoc build() {
            Instant startTime = Instant.now();
            GenDocStorageAccount genDocStorageAccount = new GenDocStorageAccount.Builder(cloudConfig).build();
            if (genDocStorageAccount.isLoaded()) {
                CloudStorageAccount cloudStorageAccount = genDocStorageAccount.getCloudStorageAccount();
                GenDocBlobClient genDocBlobClient = new GenDocBlobClient.Builder().build(cloudStorageAccount);
                if (genDocBlobClient.isLoaded()) {
                    CloudBlobClient cloudBlobClient = genDocBlobClient.getCloudBlobClient();
                    GenDocBlobContainer genDocBlobContainer = new GenDocBlobContainer.Builder(cloudConfig.getCloudBlobStorageContainerName()).build(cloudBlobClient);
                    if (genDocBlobContainer.isLoaded()) {
                        this.cloudBlobContainer = genDocBlobContainer.getCloudBlobContainer();
                        this.loaded = true;
                    } else {
                        this.errorMessage = genDocBlobContainer.getErrorMessage();
                        this.loaded = false;
                        logger.error(errorMessage);
                    }
                } else {
                    this.errorMessage = genDocBlobClient.getErrorMessage();
                    this.loaded = false;
                    logger.error(errorMessage);
                }
            } else {
                this.errorMessage = genDocStorageAccount.getErrorMessage();
                this.loaded = false;
                logger.error(errorMessage);
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info(String.format("Cloud Container Instantiation - took|%s ms|%s sec|%s min", duration.toMillis(), duration.getSeconds(), duration.toMinutes()));
            return new GenDoc(this);
        }
    }
}
