/*
 **   BatteryProductConfigurator.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to convert config xml to object.
 **
 */
package com.pg.dsm.upload.battery.services;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.upload.battery.models.config.BatteryProducts;
import com.pg.dsm.upload.battery.resources.BatteryPropertyConfig;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class BatteryProductConfigurator {
    BatteryProducts batteryProducts;

    private BatteryProductConfigurator(Builder builder) {
        this.batteryProducts = builder.batteryProducts;
    }

    public BatteryProducts getBatteryProducts() {
        return batteryProducts;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        BatteryProducts batteryProducts;
        BatteryPropertyConfig batteryPropertyConfig;

        public Builder(BatteryPropertyConfig batteryPropertyConfig) {
            this.batteryPropertyConfig = batteryPropertyConfig;
        }

        public BatteryProductConfigurator build() {
            String xmlContent = readXMLFileAsString();
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(BatteryProducts.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                batteryProducts = (BatteryProducts) unmarshaller.unmarshal(new StringReader(xmlContent));
                logger.info("Converted pgBatteryProducts.xml to Java Object");
            } catch (JAXBException e) {
                logger.error(e.getMessage());
            }
            return new BatteryProductConfigurator(this);
        }

        /**
         * Method to read pgBatteryProducts.xml file as a string.
         *
         * @return String
         * @since DSM 2018x.6
         */
        public String readXMLFileAsString() {
            String xmlContent = DomainConstants.EMPTY_STRING;
            logger.info(batteryPropertyConfig.getConfigXMLFile());
            File file = new File(batteryPropertyConfig.getConfigXMLFile());
            try (FileInputStream inputStream = new FileInputStream(file)) {
                xmlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                logger.info("Read pgBatteryProducts.xml File as String");
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
            return xmlContent;
        }
    }
}
