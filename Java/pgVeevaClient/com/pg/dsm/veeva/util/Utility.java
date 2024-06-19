/*
 **   Utility.java
 **   Description - Introduced as part of Veeva integration.      
 **   Utility methods.
 **
 */
package com.pg.dsm.veeva.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.io.DocumentQueryResponseFolder;
import com.pg.dsm.veeva.io.FileUtil;

import matrix.db.Context;
import matrix.db.Page;

public class Utility implements Veeva {
	
	public static String encode(String query) throws UnsupportedEncodingException {
		return String.format("q=%s", URLEncoder.encode(query, StandardCharsets.UTF_8.name()));
	}
	
	public static boolean isNullEmpty(String str) { 
		return (str == null || str.trim().length() == 0 || NULL.equalsIgnoreCase(str)); 
	}
	public static boolean isNotNullEmpty(String str) { 
		return !isNullEmpty(str); 
	}
	public static DocumentQueryResponseFolder getDocumentQueryResponseFolder(Configurator configurator) {
		return new DocumentQueryResponseFolder(configurator.getExtractionRequiredFolders().getPath());
	}
	public static String readVeevaXMLPage(Context context) throws Exception {
		String out = EMPTY_STRING;
		Page page = null;
		try {
			page = new Page(Veeva.PAGE_VEEVA_XML);
			page.open(context);
			out = page.getContents(context);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(page.isOpen())
				page.close(context);
		}
		return out;
	}
	
	public static String getCurrentDate() {
		java.util.Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
		String sCurrentDate = dateFormat.format(today);
		return sCurrentDate;
	}
	
	public static String getMatrixFormatCurrentDate() {
		return new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat()).format(new Date());
	}
	public static void moveDirectory(String documentID, String inwork, String skip) throws IOException {
		try {
			String documentFolderPath = FileUtil.getDocumentFolderPath(inwork, documentID);
			String documentSkipPath = FileUtil.getDocumentSkipFolderPath(skip, documentID);
			File sourceFile = new File(documentFolderPath);
			File destinationFile = new File(documentSkipPath);
			if (destinationFile.isDirectory()) {
				if (destinationFile.exists()) {
					FileUtils.deleteDirectory(destinationFile);
				}
			}
			FileUtils.moveDirectory(sourceFile, destinationFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void moveToProcessedDirectory(String masterJsonPath,String processedPath,  String sCurrentDate)
			throws IOException {
		try {
			File sourceFile = new File(masterJsonPath);
			File destinationFile = new File(
					processedPath + Veeva.DOCUMENT_QUERY_RESPONSE_FILE + Veeva.SYMBOL_HYPHEN + sCurrentDate);
			FileUtils.moveFile(sourceFile, destinationFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
