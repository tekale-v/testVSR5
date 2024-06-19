/*
 **   ReadDocumentOutput.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to read Document Response from Veeva.
 **
 */
package com.pg.dsm.veeva.io;

import java.io.File;

import com.pg.dsm.veeva.util.Veeva;

public class ReadDocumentOutput {
	
	String path;
	public ReadDocumentOutput(String path) {
		this.path = path;
	}
	public String getInputFile() {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(Veeva.VEEVA_INPUT_FILE);
		return builder.toString();
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getInWorkFile() {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(Veeva.DOCUMENT_QUERY_RESPONSE_FILE);
		return builder.toString();
	}
	
}
