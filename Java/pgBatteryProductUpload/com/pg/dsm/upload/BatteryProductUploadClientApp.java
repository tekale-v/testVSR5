/*
 **   BatteryProductUploadClientApp.java
 **   Description - Introduced as part of Battery Data Load - 18x.6
 **   About - Main class to perform battery data load.
 **
 */
package com.pg.dsm.upload;

import com.pg.dsm.upload.battery.services.BatteryProductUploader;

public class BatteryProductUploadClientApp {
    public static void main(String[] args) {
    	try {
    		BatteryProductUploader batteryProductUploader = new BatteryProductUploader(args);
    		batteryProductUploader.upload();
    	} finally {
    		System.exit(0);
    	}
    }
}
