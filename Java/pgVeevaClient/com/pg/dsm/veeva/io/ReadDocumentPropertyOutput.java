/*
 **   ReadDocumentOutput.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to read Document Property Response from Veeva.
 **
 */
package com.pg.dsm.veeva.io;

import java.io.File;

import com.pg.dsm.veeva.util.Veeva;

public class ReadDocumentPropertyOutput {
	
	String path;
	public ReadDocumentPropertyOutput(String path) {
		this.path = path;
	}
	public String getDocumentPropertyFile(String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(documentID);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(Veeva.CONST_FILE_PREFIX_JSON);
		return builder.toString();
	}
	public String getDocumentFolderPath(String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(documentID);
		return builder.toString();
	}
	
}
