/*
 **   BatteryPropertyFileResource.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to load property file.
 **
 */
package com.pg.dsm.upload.battery.resources;

import com.pg.dsm.upload.battery.enumeration.BatteryConstants;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class BatteryPropertyFileResource {
    Properties properties;

    private BatteryPropertyFileResource(Builder builder) {
        this.properties = builder.properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Properties properties;

        public Builder() {
            logger.info("Constructor");
        }

        public BatteryPropertyFileResource build() {
            properties = new Properties();
            /* at this point - the custom property file is not loaded.
               so load the logger config file explicitly.
             */
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(System.getProperty(BatteryConstants.Basic.CURRENT_DIR.getValue()));
            pathBuilder.append(File.separator);
            pathBuilder.append(BatteryConstants.Basic.BASE_DIR_DUPLICATE.getValue());
            pathBuilder.append(File.separator);
            pathBuilder.append(BatteryConstants.Basic.HOME_DIR_DUPLICATE.getValue());
            pathBuilder.append(File.separator);
            pathBuilder.append(BatteryConstants.Basic.CONFIG_FOLDER_DUPLICATE.getValue());
            pathBuilder.append(File.separator);
            pathBuilder.append(BatteryConstants.Basic.CONFIG_PROPERTY_FILE_DUPLICATE.getValue());
            pathBuilder.append(File.separator);

            try (InputStream inStream = new FileInputStream(pathBuilder.toString())) {
                properties.load(inStream);
                logger.info("Loaded pgBatteryProducts.properties");
            } catch (Exception e) {
                logger.error("************FAILED >>> Unable to load pgBatteryProducts.properties " + e);
            }
            return new BatteryPropertyFileResource(this);
        }
    }
}
