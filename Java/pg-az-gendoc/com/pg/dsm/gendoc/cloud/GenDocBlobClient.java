/*
 **   GenDocBlobClient.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains method to get cloud blob client instance
 **
 */
package com.pg.dsm.gendoc.cloud;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.Instant;

public class GenDocBlobClient {
    String errorMessage;
    boolean loaded;
    CloudBlobClient cloudBlobClient;

    private GenDocBlobClient(Builder builder) {
        this.loaded = builder.loaded;
        this.errorMessage = builder.errorMessage;
        this.cloudBlobClient = builder.cloudBlobClient;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public CloudBlobClient getCloudBlobClient() {
        return cloudBlobClient;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        String errorMessage;
        boolean loaded;
        CloudBlobClient cloudBlobClient;

        public Builder() {
            // do nothing
        }

        /**
         * @param cloudStorageAccount
         * @return
         */
        public GenDocBlobClient build(CloudStorageAccount cloudStorageAccount) {
            Instant startTime = Instant.now();
            this.cloudBlobClient = cloudStorageAccount.createCloudBlobClient();
            if (null != cloudBlobClient) {
                this.loaded = true;
            } else {
                this.errorMessage = CloudConstants.Basic.ERROR_BLOB_CLIENT_INSTANCE.getValue();
                logger.error(errorMessage);
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info(String.format("Cloud Blob Client Instantiation - took|%s ms|%s sec|%s min", duration.toMillis(), duration.getSeconds(), duration.toMinutes()));
            return new GenDocBlobClient(this);
        }
    }
}
