/*
 **   DocumentQueryResponseFolder.java
 **   Description - Introduced as part of Veeva integration.      
 **   Utility methods to act on documents query response from Veeva.
 **
 */
package com.pg.dsm.veeva.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.json.binder.documents_query.Document;


public class DocumentQueryResponseFolder {
	String path;
	String file;
	public DocumentQueryResponseFolder(String path) {
		this.path = path;
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(Veeva.DOCUMENT_QUERY_RESPONSE_FILE);
		this.file = builder.toString();
	}
	public void saveDocumentQuery(List<Document> documents) throws IOException {	
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(Veeva.DOCUMENT_ID_FILE);
		File f = new File(builder.toString());
		FileWriter fw = new FileWriter(f);
		for (int i = 0; i < documents.size(); i++) {
			Document document = documents.get(i);
			fw.write(document.getId());
			fw.write("\n");
		}
		fw.close();
	}
	public void saveDocumentQueryResponse(String json) {
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(json);
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void moveDocumentQueryResponseFile(String toLoc) throws IOException {
		StringBuilder toBuilder = new StringBuilder(toLoc);
		toBuilder.append(File.separator);
		toBuilder.append(new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()));
		toBuilder.append("_");
		toBuilder.append(Veeva.DOCUMENT_QUERY_RESPONSE_FILE);
		File from = new File(file);
		File to = new File(toBuilder.toString());
		FileUtils.copyFile(from, to);
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
}
