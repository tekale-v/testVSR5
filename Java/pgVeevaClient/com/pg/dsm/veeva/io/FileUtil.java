/*
 **   FileUtil.java
 **   Description - Introduced as part of Veeva integration.      
 **   Utility methods to act on response files received from Veeva.
 **
 */
package com.pg.dsm.veeva.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.pg.dsm.veeva.util.Veeva;

public class FileUtil {

	public static void saveDocumentQueryResponse(String path, String json) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(Veeva.DOCUMENT_QUERY_RESPONSE_FILE);
		String file = builder.toString();
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(json);
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveDocumentProperty(String path, String documentID, String jString) throws IOException {
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
	public static void saveDocumentDataset(String path, String documentID, String jString) throws IOException {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(File.separator);
		File directory = new File(builder.toString());
		if(!directory.exists()) {
			directory.mkdir();
			File file = new File(builder.toString()+documentID+Veeva.CONST_DATA_SET_FILE_POSTFIX+Veeva.CONST_FILE_PREFIX_JSON);
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(jString);
				fw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} else {
			File file = new File(builder.toString()+documentID+Veeva.CONST_DATA_SET_FILE_POSTFIX+Veeva.CONST_FILE_PREFIX_JSON);
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(jString);
				fw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	public static void saveUsersResponse(String path, String documentID, String jString) throws IOException {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(File.separator);
		File directory = new File(builder.toString());
		if(!directory.exists()) {
			directory.mkdir();
			File file = new File(builder.toString()+documentID+Veeva.CONST_USERS_FILE_POSTFIX+Veeva.CONST_FILE_PREFIX_JSON);
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(jString);
				fw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		} else {
			File file = new File(builder.toString()+documentID+Veeva.CONST_USERS_FILE_POSTFIX+Veeva.CONST_FILE_PREFIX_JSON);
			try (FileWriter fw = new FileWriter(file)) {
				fw.write(jString);
				fw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	public static String getRenditionDownloadPath(String path, String documentID) throws IOException {
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
	public static String getDocumentsQueryResponseFile(String path) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(Veeva.DOCUMENT_QUERY_RESPONSE_FILE);
		return builder.toString();
	}
	public static String getDocumentPropertyResponseFile(String path, String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(Veeva.CONST_DOCUMENT_PROPERTY_FILE_POSTFIX);
		builder.append(Veeva.CONST_FILE_PREFIX_JSON);
		return builder.toString();
	}
	public static String getUsersEmailsResponseFile(String path, String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(File.separator);
		builder.append(documentID);
		builder.append(Veeva.CONST_USERS_FILE_POSTFIX);
		builder.append(Veeva.CONST_FILE_PREFIX_JSON);
		return builder.toString();
	}
	public static String getDocumentFolderPath(String path, String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(documentID);
		return builder.toString();
	}
	
	public static String getDocumentSuccessFolderPath(String path, String sCurrentDate, String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(sCurrentDate);
		builder.append(File.separator);
		File successCurrentDir = new File(builder.toString());
		if(!successCurrentDir.exists()) {
			successCurrentDir.mkdir();
		} 
		builder.append(documentID);
		return builder.toString();
	}
	public static String getDocumentSkipFolderPath(String path, String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(documentID);
		builder.append(File.separator);
		return builder.toString();
	}
	
	public static String getDocumentFailedFolderPath(String path, String documentID) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(documentID);
		return builder.toString();
	}
	
	public static String getDocumentProcessedFolderPath(String path, String sCurrentDate) {
		StringBuilder builder = new StringBuilder(path);
		builder.append(sCurrentDate);
		builder.append(File.separator);
		File successCurrentDir = new File(builder.toString());
		if(!successCurrentDir.exists()) {
			successCurrentDir.mkdir();
		} 
		builder.append(Veeva.DOCUMENT_QUERY_RESPONSE_FILE);
		return builder.toString();
	}
}
