/*
 **   CloudGenDocUtil.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Class to initialize logger.
 **
 */
package com.pg.dsm.gendoc;

import com.pg.dsm.gendoc.resources.LoggerPropertyFileConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GenDocClient {
    private static final Logger logger = Logger.getLogger(GenDocClient.class.getName());


    public GenDocClient() {
        // do nothing
    }

    /**
     * @param cloudGenDocCustomLoggerConfigFilePath
     */
    public void initializeLogger(String cloudGenDocCustomLoggerConfigFilePath) {
        // Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
        Properties prop = new Properties();
        LoggerPropertyFileConfigurator loggerPropertyFileConfigurator = new LoggerPropertyFileConfigurator.Builder(cloudGenDocCustomLoggerConfigFilePath).build();
        if (loggerPropertyFileConfigurator.isLoggerPropertyFileExist()) {
            try (FileInputStream fileInputStream = new FileInputStream(loggerPropertyFileConfigurator.getLoggerPropertyFilePath())) {
                prop.load(fileInputStream);
                PropertyConfigurator.configure(prop);
                logger.info("####################################################################################");
                logger.info(String.format("Logger Properties loaded: %s", cloudGenDocCustomLoggerConfigFilePath));
            } catch (IOException e) {
                logger.error(String.format("Unable to Load Logger Properties: %s", cloudGenDocCustomLoggerConfigFilePath));
            }
        }
    }
}
