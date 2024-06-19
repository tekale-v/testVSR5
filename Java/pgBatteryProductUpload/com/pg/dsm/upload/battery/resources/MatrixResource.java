/*
 **   MatrixResource.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Class to load database connection.
 **
 */
package com.pg.dsm.upload.battery.resources;

import matrix.db.Context;
import org.apache.log4j.Logger;

public class MatrixResource {
    Context context;

    private MatrixResource(Builder builder) {
        this.context = builder.context;
    }

    public Context getContext() {
        return this.context;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        BatteryPropertyConfig batteryPropertyConfig;

        public Builder(BatteryPropertyConfig batteryPropertyConfig) {
            this.batteryPropertyConfig = batteryPropertyConfig;
        }

        public MatrixResource build() {
            this.getContext();
            return new MatrixResource(this);
        }

        public void getContext() {
            try {
                this.context = new Context(batteryPropertyConfig.getHost());
                this.context.setUser(batteryPropertyConfig.getUser());
                this.context.setPassword(batteryPropertyConfig.getPdw());
                this.context.connect();
                if (this.context.isConnected()) {
                    logger.info("Connected to 3DX instance with context user|" + this.context.getUser());
                }
            } catch (Exception exception) {
                logger.error("Unable to connect 3DX instance: " + exception.getMessage());
            }
        }
    }
}
