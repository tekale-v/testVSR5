/*
 **   LoggerPropertyFileConfigurator.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Logger Implementation class.
 **
 */
package com.pg.dsm.gendoc.resources;

import java.io.File;

public class LoggerPropertyFileConfigurator {
    String loggerPropertyFilePath;
    boolean isLoggerPropertyFileExist;

    private LoggerPropertyFileConfigurator(Builder builder) {
        this.isLoggerPropertyFileExist = builder.isLoggerPropertyFileExist;
        this.loggerPropertyFilePath = builder.loggerPropertyFilePath;
    }

    public String getLoggerPropertyFilePath() {
        return loggerPropertyFilePath;
    }

    public void setLoggerPropertyFilePath(String loggerPropertyFilePath) {
        this.loggerPropertyFilePath = loggerPropertyFilePath;
    }

    public boolean isLoggerPropertyFileExist() {
        return isLoggerPropertyFileExist;
    }

    public void setLoggerPropertyFileExist(boolean loggerPropertyFileExist) {
        isLoggerPropertyFileExist = loggerPropertyFileExist;
    }

    public static class Builder {
        String loggerPropertyFilePath;
        boolean isLoggerPropertyFileExist;
        String loggerConfigPropertyFile;

        public Builder(String loggerConfigPropertyFile) {
            this.loggerConfigPropertyFile = loggerConfigPropertyFile;
        }

        public LoggerPropertyFileConfigurator build() {
            File loggerPropertyFile = new File(loggerConfigPropertyFile);
            if (loggerPropertyFile.exists()) {
                isLoggerPropertyFileExist = true;
                loggerPropertyFilePath = loggerPropertyFile.getPath();
            }
            return new LoggerPropertyFileConfigurator(this);
        }
    }
}
