/*
 **   GenDocStorageAccount.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Contains method to get cloud storage account details.
 **
 */
package com.pg.dsm.gendoc.cloud;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.util.CloudGenDocUtil;
import org.apache.log4j.Logger;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.Duration;
import java.time.Instant;

public class GenDocStorageAccount {
    String errorMessage;
    boolean loaded;
    CloudStorageAccount cloudStorageAccount;

    private GenDocStorageAccount(Builder builder) {
        this.loaded = builder.loaded;
        this.errorMessage = builder.errorMessage;
        this.cloudStorageAccount = builder.cloudStorageAccount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public CloudStorageAccount getCloudStorageAccount() {
        return cloudStorageAccount;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        ICloudConfig cloudConfig;
        CloudStorageAccount cloudStorageAccount;
        String errorMessage;
        boolean loaded;

        public Builder(ICloudConfig cloudConfig) {
            this.cloudConfig = cloudConfig;
        }

        /**
         * @return
         */
        public GenDocStorageAccount build() {
            Instant startTime = Instant.now();
            if (cloudConfig.isLoaded()) {
                String azureConnectionString = CloudGenDocUtil.getCloudConnectionString(cloudConfig);
                try {
                    this.cloudStorageAccount = CloudStorageAccount.parse(azureConnectionString);
                    this.loaded = true;
                } catch (URISyntaxException | InvalidKeyException e) {
                    this.errorMessage = e.getMessage();
                    this.loaded = false;
                    logger.error(e);
                }
            }
            Instant endTime = Instant.now();
            Duration duration = Duration.between(startTime, endTime);
            logger.info(String.format("Cloud Account Authentication - took|%s ms|%s sec|%s min", duration.toMillis(), duration.getSeconds(), duration.toMinutes()));
            return new GenDocStorageAccount(this);
        }
    }
}
