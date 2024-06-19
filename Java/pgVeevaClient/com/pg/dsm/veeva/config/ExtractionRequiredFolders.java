/*
 **   ExtractionRequiredFolders.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to create all required folders for Veeva extraction.
 **
 */
package com.pg.dsm.veeva.config;

import java.io.File;

import org.apache.log4j.Logger;


import com.pg.dsm.veeva.util.Veeva;

public class ExtractionRequiredFolders {
	String path;
	String inwork;
	String processed;
	String success;
	String failed;
	String skip;
	boolean isCreated;
	/** 
	 * @about Constructor
	 * @param Create - builder class
	 * @since DSM 2018x.3
	 */
	private ExtractionRequiredFolders(Create create) {
		this.path = create.path;
		this.processed = create.processed;
		this.success = create.success;
		this.failed = create.failed;
		this.inwork = create.inwork;
		this.skip = create.skip;
		this.isCreated = create.isCreated;
	}
	/** 
	 * @about Getter method - to get extraction path
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getPath() {
		return path;
	}
	/** 
	 * @about Getter method - to get skip folder path
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getSkip() {
		return skip;
	}
	/** 
	 * @about Setter method - to set execution path
	 * @param String - path
	 * @since DSM 2018x.3
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/** 
	 * @about Getter method - to get inwork path
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getInwork() {
		return inwork;
	}
	/** 
	 * @about Setter method - to set inwork path
	 * @param String - path
	 * @since DSM 2018x.3
	 */
	public void setInwork(String inwork) {
		this.inwork = inwork;
	}
	/** 
	 * @about Getter method - to get processed path
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getProcessed() {
		return processed;
	}
	/** 
	 * @about Setter method - to set processed path
	 * @param String - path
	 * @since DSM 2018x.3
	 */
	public void setProcessed(String processed) {
		this.processed = processed;
	}
	/** 
	 * @about Getter method - to get success path
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getSuccess() {
		return success;
	}
	/** 
	 * @about Setter method - to set success path
	 * @param String - path
	 * @since DSM 2018x.3
	 */
	public void setSuccess(String success) {
		this.success = success;
	}
	/** 
	 * @about Getter method - to get failed path
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getFailed() {
		return failed;
	}
	/** 
	 * @about Setter method - to set failed path
	 * @param String - path
	 * @since DSM 2018x.3
	 */
	public void setFailed(String failed) {
		this.failed = failed;
	}
	/** 
	 * @about Getter method - check if folder bean loaded.
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isCreated() {
		return isCreated;
	}
	/** 
	 * @about Setter method - to set folder bean loaded.
	 * @param boolean 
	 * @since DSM 2018x.3
	 */
	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}
	public static class Create {
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		String path;
		String inwork;
		String processed;
		String success;
		String failed;
		String skip;
		boolean isCreated;
		/** 
		 * @about Constructor
		 * @param String - path
		 * @since DSM 2018x.3
		 */
		public Create(String path) {
			this.path = path;
			this.isCreated = false;
		}
		/** 
		 * @about Builder method
		 * @return ExtractionRequiredFolders  
		 * @since DSM 2018x.3
		 */
		public ExtractionRequiredFolders perform() {
			try {
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
				logger.info("failed folder created");
				this.isCreated = true;

			} catch(Exception e) {
				this.isCreated = false;
				logger.error("************FAILED >>> Unable to create required folders "+e);
			}
			return new ExtractionRequiredFolders(this);
		}
	}
}
