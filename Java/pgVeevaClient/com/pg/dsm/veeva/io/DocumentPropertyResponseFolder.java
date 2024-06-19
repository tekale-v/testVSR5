/*
 **   DocumentPropertyResponseFolder.java
 **   Description - Introduced as part of Veeva integration.      
 **   Utility methods to act on document property response from Veeva.
 **
 */
package com.pg.dsm.veeva.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.pg.dsm.veeva.util.Veeva;

public class DocumentPropertyResponseFolder {
	String path;
	public DocumentPropertyResponseFolder(String path) {
		this.path = path;
	}
	public void saveDocumentProperty(String documentID, String jString) throws IOException {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(File.separator);
		File directory = new File(builder.toString());
		if(!directory.exists()) {
			directory.mkdir();
			File file = new File(builder.toString()+documentID+Veeva.CONST_DOCUMENT_PROPERTY_FILE_POSTFIX+Veeva.CONST_FILE_PREFIX_JSON);
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(jString);
				fw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}  
		} else {
			File file = new File(builder.toString()+documentID+Veeva.CONST_DOCUMENT_PROPERTY_FILE_POSTFIX+Veeva.CONST_FILE_PREFIX_JSON);
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(jString);
				fw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	public String getRenditionDownloadPath(String documentID) throws IOException {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(File.separator);
		File directory = new File(builder.toString());
		if(!directory.exists()) {
			directory.mkdir();
		}
		return builder.toString();
	}
	public boolean isFolderExist(String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(File.separator);
		File directory = new File(builder.toString());
		return directory.exists();
	}
}
