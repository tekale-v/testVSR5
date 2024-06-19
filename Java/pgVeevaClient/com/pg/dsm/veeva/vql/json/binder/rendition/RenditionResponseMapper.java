/*
 **   RenditionResponseMapper.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.json.binder.rendition;

public class RenditionResponseMapper {
	
	String responseStatusCode;
	boolean hasFile;
	String fileName;
	String docID;
	String fileDownloadFolder;
	boolean fileDownloaded;
	public String getResponseStatusCode() {
		return responseStatusCode;
	}
	public void setResponseStatusCode(String responseStatusCode) {
		this.responseStatusCode = responseStatusCode;
	}
	public boolean hasFile() {
		return hasFile;
	}
	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDocID() {
		return docID;
	}
	public void setDocID(String docID) {
		this.docID = docID;
	}
	public String getFileDownloadFolder() {
		return fileDownloadFolder;
	}
	public void setFileDownloadFolder(String fileDownloadFolder) {
		this.fileDownloadFolder = fileDownloadFolder;
	}
	public boolean isFileDownloaded() {
		return fileDownloaded;
	}
	public void setFileDownloaded(boolean fileDownloaded) {
		this.fileDownloaded = fileDownloaded;
	}
}
