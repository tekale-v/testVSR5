/*
 **   RequiredFolders.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to check Response folders from Veeva.
 **   DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
 */
package com.pg.dsm.veeva.io;

import java.io.File;

import org.apache.log4j.Logger;


import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.veeva.util.Veeva;

public class RequiredFolders {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	String path;
	String input;
	String inwork;
	String processed;
	String success;
	String failed;
	String skip;
	boolean isCreated;
	public RequiredFolders(String path) {
		this.path = path;
		
		StringBuilder buffer = new StringBuilder(path);
		//create in-work folder
		buffer = new StringBuilder(path);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_INWORK_FOLDER);
		buffer.append(File.separator);
		File inworkDir = new File(buffer.toString());
		if(!inworkDir.exists()) {
			inworkDir.mkdir();
			this.inwork = buffer.toString();
		} else {
			this.inwork = buffer.toString();
		} 
		logger.info("inwork folder created");

		//create processed folder
		buffer = new StringBuilder(path);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_PROCESSED_FOLDER);
		buffer.append(File.separator);
		File processedDir = new File(buffer.toString());
		if(!processedDir.exists()) {
			processedDir.mkdir();
			this.processed = buffer.toString();
		} else {
			this.processed = buffer.toString();
		} 
		logger.info("processed folder created");

		//create success folder
		buffer = new StringBuilder(path);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_PROCESSED_FOLDER);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_SUCCESS_FOLDER);
		buffer.append(File.separator);
		File successDir = new File(buffer.toString());
		if(!successDir.exists()) {
			successDir.mkdir();
			this.success = buffer.toString();
		} else {
			this.success = buffer.toString();
		}
		logger.info("success folder created");

		//create failed folder
		buffer = new StringBuilder(path);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_PROCESSED_FOLDER);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_FAILED_FOLDER);
		buffer.append(File.separator);
		File failedDir = new File(buffer.toString());
		if(!failedDir.exists()) {
			failedDir.mkdir();
			this.failed = buffer.toString();
		} else {
			this.failed = buffer.toString();
		}
		logger.info("failed folder created");
		
		
		//create skip folder
		buffer = new StringBuilder(path);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_PROCESSED_FOLDER);
		buffer.append(File.separator);
		buffer.append(Veeva.VEEVA_SKIP_FOLDER);
		buffer.append(File.separator);
		File skipDir = new File(buffer.toString());
		if(!skipDir.exists()) {
			skipDir.mkdir();
			this.skip = buffer.toString();
		} else {
			this.skip = buffer.toString();
		}
		logger.info("skip folder created");
		
		this.isCreated = true;
				
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getInwork() {
		return inwork;
	}
	public void setInwork(String inwork) {
		this.inwork = inwork;
	}
	public String getProcessed() {
		return processed;
	}
	public void setProcessed(String processed) {
		this.processed = processed;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getFailed() {
		return failed;
	}
	public void setFailed(String failed) {
		this.failed = failed;
	}
	public boolean isCreated() {
		return isCreated;
	}
	public String getSkip() {
		return skip;
	}
	
}
