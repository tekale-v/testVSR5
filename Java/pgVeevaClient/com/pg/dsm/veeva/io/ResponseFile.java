/*
 **   ResponseFile.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to read Response file from Veeva.
 **
 */
package com.pg.dsm.veeva.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentPropertyMapper;
import com.pg.dsm.veeva.vql.json.binder.users_email.UsersMapper;

public class ResponseFile {
	RequiredFolders folders;
	String documentID;
	File[] renditionFiles;
	boolean isDocumentResponsePathExist;
	boolean isDocumentPropertyFileExist;
	boolean isUserEmailsResponseFileExist;
	boolean isRenditionFileExist;
	private ResponseFile(Check checker) {
		this.folders = checker.folders;
		this.documentID = checker.documentID;
		this.renditionFiles = checker.renditionFiles;
		this.isDocumentResponsePathExist = checker.isDocumentResponsePathExist;
		this.isDocumentPropertyFileExist = checker.isDocumentPropertyFileExist;
		this.isUserEmailsResponseFileExist = checker.isUserEmailsResponseFileExist;
		this.isRenditionFileExist = checker.isRenditionFileExist;
	}
	public RequiredFolders getFolders() {
		return folders;
	}
	public String getDocumentID() {
		return documentID;
	}
	public File[] getRenditionFiles() {
		return renditionFiles;
	}
	public boolean isDocumentResponsePathExist() {
		return isDocumentResponsePathExist;
	}
	public boolean isDocumentPropertyFileExist() {
		return isDocumentPropertyFileExist;
	}
	public boolean isUserEmailsResponseFileExist() {
		return isUserEmailsResponseFileExist;
	}
	public boolean isRenditionFileExist() {
		return isRenditionFileExist;
	}
	public static class Check {
		private final Logger logger = Logger.getLogger(this.getClass().getName());
		RequiredFolders folders;
		String documentID;
		File[] renditionFiles;
		boolean isDocumentResponsePathExist;
		boolean isDocumentPropertyFileExist;
		boolean isUserEmailsResponseFileExist;
		boolean isRenditionFileExist;
		DocumentPropertyMapper documentPropertyMapper;
		UsersMapper usersMapper;
		public Check(RequiredFolders folders, String documentID) {
			this.folders = folders;
			this.documentID = documentID;
			this.isDocumentResponsePathExist = false;
			this.isDocumentPropertyFileExist = false;
			this.isUserEmailsResponseFileExist = false;
			this.isRenditionFileExist = false;
		}
		public ResponseFile perform() throws JsonParseException, JsonMappingException, IOException {
			String documentFolderPath = FileUtil.getDocumentFolderPath(folders.getInwork(), documentID);
			File directory = new File(documentFolderPath);
			if(directory.exists()) {
				this.isDocumentResponsePathExist = true;
				logger.info("ResponseFile document folder path >> "+documentFolderPath);
				String documentPropertyResponseFile =  FileUtil.getDocumentPropertyResponseFile(documentFolderPath, documentID);
				File file = new File(documentPropertyResponseFile);
				if(file.exists()) {
					this.isDocumentPropertyFileExist = true;
					this.documentPropertyMapper = new ObjectMapper().readValue(file, DocumentPropertyMapper.class);
					logger.info(documentPropertyResponseFile);
				}
				String usersEmailsFile = FileUtil.getUsersEmailsResponseFile(documentFolderPath, documentID);
				file = new File(usersEmailsFile);
				if(file.exists()) {
					this.isUserEmailsResponseFileExist = true;
					this.usersMapper = new ObjectMapper().readValue(file, UsersMapper.class);
					logger.info("ResponseFile user email folder path >> "+usersEmailsFile);
				}
				FileFilter fileFilter = new WildcardFileFilter(documentID+"_rendition_*");
				File[] renditionFiles = directory.listFiles(fileFilter);
				if(renditionFiles.length>0) {
					this.isRenditionFileExist = true;
				}
			}
			return new ResponseFile(this);
		}
	} 
}
