/*
 **   CloudGenDocUtil.java
 **   Description - Introduced as part of Cloud GenDoc - 18x.6
 **   About - Utility class.
 **
 */
package com.pg.dsm.gendoc.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.pg.dsm.gendoc.cloud.GenDocDownload;
import com.pg.dsm.gendoc.cloud.models.AzureBlobFileDownload;
import com.pg.dsm.gendoc.cloud.models.AzureBlobFileWatch;
import com.pg.dsm.gendoc.enumeration.CloudConstants;
import com.pg.dsm.gendoc.interfaces.ICloudConfig;
import com.pg.dsm.gendoc.interfaces.ICloudDocument;
import com.pg.dsm.gendoc.models.Document;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudGenDocUtil {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public CloudGenDocUtil() {
		// do nothing
	}

	/**
	 * @param cloudConfig
	 * @return
	 */
	public static String getCloudConnectionString(ICloudConfig cloudConfig) {
		StringBuilder builder = new StringBuilder();
		builder.append(cloudConfig.getCloudEndpointsProtocolParameter());
		builder.append(CloudConstants.Basic.SYMBOL_EQUAL.getValue());
		builder.append(cloudConfig.getCloudEndpointsProtocolValue());
		builder.append(CloudConstants.Basic.SYMBOL_SEMI_COLON.getValue());

		builder.append(cloudConfig.getCloudAccountNameParameter());
		builder.append(CloudConstants.Basic.SYMBOL_EQUAL.getValue());
		builder.append(cloudConfig.getCloudAccountNameValue());
		builder.append(CloudConstants.Basic.SYMBOL_SEMI_COLON.getValue());

		builder.append(cloudConfig.getCloudAccountKeyParameter());
		builder.append(CloudConstants.Basic.SYMBOL_EQUAL.getValue());
		builder.append(cloudConfig.getCloudAccountKeyValue());
		builder.append(CloudConstants.Basic.SYMBOL_SEMI_COLON.getValue());

		return builder.toString();
	}

	/**
	 * @param cloudConfig
	 * @return
	 */
	public static boolean useCloud(ICloudConfig cloudConfig) {
		boolean use = true;
		if (cloudConfig.isLoaded()) {
			use = cloudConfig.isLoaded() && !cloudConfig.getConfigCurrentState().equalsIgnoreCase(pgV3Constants.ATTRIBUTE_CURRENT_INACTIVE);
		} else {
			use = false;
		}
		return use;
	}

	/**
	 * @param context
	 * @param configObjectName
	 * @return
	 * @throws matrix.util.MatrixException
	 */
	public static String getObjectId(Context context, String configObjectName) throws MatrixException {
		BusinessObject configBusinessObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, configObjectName, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
		return configBusinessObject.getObjectId(context);
	}

	/**
	 * @param context
	 * @param objectOid
	 * @return
	 */
	public static Map<Object, Object> getObjectInfo(Context context, String objectOid) throws FrameworkException {
		Map<Object, Object> infoMap = null;
		StringList busSelectList = new StringList(3);
		busSelectList.addElement(DomainConstants.SELECT_ID);
		busSelectList.addElement(DomainConstants.SELECT_TYPE);
		busSelectList.addElement(DomainConstants.SELECT_NAME);
		busSelectList.addElement(DomainConstants.SELECT_REVISION);
		busSelectList.addElement(DomainConstants.SELECT_CURRENT);
		busSelectList.addElement(DomainConstants.SELECT_POLICY);

		DomainObject dObj = DomainObject.newInstance(context, objectOid);
		infoMap = dObj.getInfo(context, busSelectList);

		return infoMap;
	}

	/**
	 * @param context
	 * @param objectOid
	 * @return
	 */
	public static Map<String, String> getCloudConfigInfo(Context context, String objectOid) throws FrameworkException {
		Map<String, String> infoMap = null;
		StringList busSelectList = new StringList(22);
		busSelectList.addElement(DomainConstants.SELECT_ID);
		busSelectList.addElement(DomainConstants.SELECT_TYPE);
		busSelectList.addElement(DomainConstants.SELECT_NAME);
		busSelectList.addElement(DomainConstants.SELECT_REVISION);
		busSelectList.addElement(DomainConstants.SELECT_CURRENT);
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_BLOB_STORAGE_CONTAINER_NAME.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_END_POINTS_PROTOCOL_PARAMETER.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_END_POINTS_PROTOCOL_VALUE.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_NAME_PARAMETER.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_NAME_VALUE.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_KEY_PARAMETER.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_ACCOUNT_KEY_VALUE.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.GEN_DOC_FILE_EXTENSIONS_FOR_CLOUD.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.GEN_DOC_FILE_EXTENSIONS_FOR_ITEXT.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.GEN_DOC_BLOB_UPLOAD_PATH.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.GEN_DOC_BLOB_DOWNLOAD_PATH.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.GEN_DOC_LOCAL_DOWNLOAD_PATH.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.GEN_DOC_SLEEP_INTERVAL.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.GEN_DOC_TIMER.getSelect(context));
		busSelectList.addElement(CloudConstants.Attribute.CLOUD_GEN_DOC_CUSTOM_LOGGER_CONFIG_FILE_PATH.getSelect(context));
		// Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433
		busSelectList.addElement(CloudConstants.Attribute.CHARACTERS_NOT_ALLOWED_FOR_FILE_NAME.getSelect(context));
		// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
                busSelectList.addElement(CloudConstants.Attribute.CHARACTERS_NOT_ALLOWED_FOR_OBJECT_NAME.getSelect(context));
		// Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends
		DomainObject dObj = DomainObject.newInstance(context, objectOid);
		infoMap = dObj.getInfo(context, busSelectList);

		return infoMap;
	}

	/**
	 * @param dataMap
	 * @param selectable
	 * @return
	 */
	public static String getStringFromStringList(Map dataMap, String selectable) {
		String retString = DomainConstants.EMPTY_STRING;
		Object substituteId = (dataMap).get(selectable);
		StringList stringList = new StringList();
		if (null != substituteId) {
			if (substituteId instanceof StringList) {
				stringList = (StringList) substituteId;
			} else {
				stringList.add(substituteId.toString());
			}
		}
		if (!stringList.isEmpty()) {
			retString = stringList.get(0);
		}
		return retString;
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	public Document getDocument(JSONObject jsonObject) {
		List<CloudConstants.DocumentProperty> documentPropertyList = Arrays.asList(CloudConstants.DocumentProperty.values());
		documentPropertyList.forEach(documentProperty -> {
			if (!jsonObject.has(documentProperty.getValue())) {
				try {
					jsonObject.put(documentProperty.getValue(), DomainConstants.EMPTY_STRING);
				} catch (JSONException e) {
					logger.error(e);
				}
			}
		});
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> jsonMap = null;
		try {
			jsonMap = new ObjectMapper().readValue(jsonObject.toString(), new TypeReference<Map<String, String>>() {
			});
		} catch (IOException e) {
			logger.error(e);
		}
		if (null != jsonMap) {
			jsonMap.put(CloudConstants.DocumentProperty.LOADED.getValue(), String.valueOf(Boolean.TRUE));
		} else {
			jsonMap = new HashMap<>();
			jsonMap.put(CloudConstants.DocumentProperty.LOADED.getValue(), String.valueOf(Boolean.FALSE));
		}
		return objectMapper.convertValue(jsonMap, Document.class);
	}

	/**
	 * @param documentList
	 * @param fileExtension
	 * @return
	 */
	public List<Document> filterDocumentsByExtension(List<Document> documentList, String fileExtension) {
		List<Document> filteredDocumentList = new ArrayList<>();
		for (Document document : documentList) {
			if (StringUtils.containsIgnoreCase(fileExtension, document.getFileExtension())) {
				filteredDocumentList.add(document);
			}
		}
		return filteredDocumentList;
	}

	/**
	 * @param context
	 * @param cloudBlobContainer
	 * @param cloudDocument
	 * @param documentList
	 * @return
	 */
	public List<Document> performCloudDownload(Context context, CloudBlobContainer cloudBlobContainer, ICloudDocument cloudDocument, List<Document> documentList) throws InterruptedException {
		// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
		logger.info(String.format("START - CLOUD DOWNLOAD|PROCESSING OBJECT INFO|%s|%s|%s|%s|", cloudDocument.getObjectType(), cloudDocument.getObjectName(), cloudDocument.getObjectRevision(), cloudDocument.getObjectOid()));
		Instant startTime = Instant.now();
		List<Document> downloadedDocuments = new ArrayList<>();
		try {
			String absoluteDownloadFilePath;
			Date date;
			long sleepInterval;
			GenDocDownload genDocDownload;
			Document documentResponse;
			String downloadResponseMessage;
			File downloadedFile;
			long length;

			for (Document document : documentList) {
				date = new Date();
				absoluteDownloadFilePath = document.getAbsoluteDownloadFilePath();
				sleepInterval = (long) cloudDocument.getCloudConfig().getSleepInterval();

				boolean isDownloaded = false;
				while (!isDownloaded) {
					Thread.sleep(sleepInterval * 100);
					genDocDownload = new GenDocDownload.Builder(cloudBlobContainer, cloudDocument, document).build();
					if (genDocDownload.isLoaded()) {
						documentResponse = genDocDownload.getDocumentResponse();
						downloadResponseMessage = documentResponse.getDownloadResponse();
						if (UIUtil.isNotNullAndNotEmpty(downloadResponseMessage) && downloadResponseMessage.contains("success")) {
							downloadedFile = new File(absoluteDownloadFilePath);
							length = -1;
							if (downloadedFile.exists()) {
								length = downloadedFile.length();
								logger.info(String.format("Downloaded PDF Size: %s", length));
							}
							if (length > 0) {
								documentResponse.setIsPdfGenerated(String.valueOf(Boolean.TRUE));
								downloadedDocuments.add(documentResponse);
								logger.info(String.format("PDF downloaded at: %s", absoluteDownloadFilePath));
								isDownloaded = true; // assignment is required as the loop should break based on this assignment
								break;
							} else {
								isDownloaded = false;
								Thread.sleep(sleepInterval * 100);
							}
						}
					}
					if (new Date().getTime() - date.getTime() >= cloudDocument.getCloudConfig().getTimer()) {
						// unable to call the api from jar.
						MqlUtil.mqlCommand(context, "notice " + cloudDocument.getGenDocErrorMessage());
						documentResponse = new Document();
						BeanUtils.copyProperties(documentResponse, document);
						documentResponse.setIsPdfGenerated(String.valueOf(Boolean.FALSE));
						documentResponse.setPdfGenErrorMsg(cloudDocument.getGenDocErrorMessage());
						documentResponse.setTimeOut(String.valueOf(Boolean.TRUE));
						downloadedDocuments.add(documentResponse);
						logger.error("XXXXXXXXX| PDF DOWNLOAD TIMED-OUT - BREAK OUT OF LOOP |XXXXXXXXXXX");
						break;
					}
				}
			}
		} catch (InvocationTargetException | IllegalAccessException | FrameworkException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			throw e;
		}
		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		logger.info(String.format("All Files Cloud Download - took|%s ms|%s sec|%s min", duration.toMillis(), duration.getSeconds(), duration.toMinutes()));

		logger.info(String.format("END - CLOUD DOWNLOAD|PROCESSING OBJECT INFO|%s|%s|%s|%s|", cloudDocument.getObjectType(), cloudDocument.getObjectName(), cloudDocument.getObjectRevision(), cloudDocument.getObjectOid()));
		return downloadedDocuments;
	}

	/**
	 * @param downloadedDocuments
	 * @param cloudDocumentCount
	 * @return
	 */
	public boolean isCloudDownloadSuccessful(List<Document> downloadedDocuments, int cloudDocumentCount) {
		int downloadCount = downloadedDocuments.size();
		boolean isSuccess = true;
		if (downloadCount != cloudDocumentCount) {
			isSuccess = false;
			return isSuccess;
		}
		for (Document document : downloadedDocuments) {
			if (!Boolean.parseBoolean(document.getIsPdfGenerated())) {
				isSuccess = false;
				break;
			}
		}
		logger.info(String.format("Is Cloud Download Successful? %b", isSuccess));
		return isSuccess;
	}


	/**
	 * @param absoluteCheckOutDir
	 * @param downloadDir
	 */
	public void moveCheckOutPDFToDownloadFolder(String absoluteCheckOutDir, String downloadDir) {
		// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
		// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
		String fileExtension;
		String fileName;
		File downloadPath;
		File checkOutDir = new File(absoluteCheckOutDir);
		if (checkOutDir.exists()) {
			File[] files = checkOutDir.listFiles();
			for (File file : files) {
				fileName = file.getName();
				fileExtension = FilenameUtils.getExtension(fileName);
				if (StringUtils.containsIgnoreCase(fileExtension, CloudConstants.Basic.FILE_EXTENSION_PDF.getValue())) {
					downloadPath = new File(downloadDir.concat(File.separator));
					if (!downloadPath.exists()) {
						downloadPath.mkdir();
					}
					downloadPath = new File(downloadDir.concat(File.separator).concat(fileName));
					fileCopy(file.getPath(), downloadPath.getPath());
				}
			}
		}
	}


	/**
	 * @param inWorkDir
	 * @param downloadDir
	 */
	public void moveCheckOutPDFFromInWorkToDownloadFolder(String inWorkDir, String downloadDir) {
		// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
		// Refine logs 2018x.6 July-CW (Defect 43669) by DSM Sogeti
		String fileExtension;
		String fileName;
		File downloadPath;

		File checkOutDir = new File(inWorkDir);
		if (checkOutDir.exists()) {
			File[] files = checkOutDir.listFiles();
			for (File file : files) {
				fileName = file.getName();
				fileExtension = FilenameUtils.getExtension(fileName);
				if (StringUtils.containsIgnoreCase(fileExtension, CloudConstants.Basic.FILE_EXTENSION_PDF.getValue())) {
					downloadPath = new File(downloadDir.concat(File.separator));
					if (!downloadPath.exists()) {
						downloadPath.mkdir();
					}
					downloadPath = new File(downloadDir.concat(File.separator).concat(fileName));
					fileCopy(file.getPath(), downloadPath.getPath());
				}
			}
		}
	}

	/**
	 * @param sourcePath
	 * @param destPath
	 */
	public void fileCopy(String sourcePath, String destPath) {
		try {
			Path source = Paths.get(sourcePath);
			Path target = Paths.get(destPath);
			java.nio.file.Files.move(source, target);
		} catch (Exception e) {
			logger.error("Failed to move the file:" + e);
		}
	}

	/**
	 * @param file
	 * @param cloudDocument
	 * @param fileName
	 * @return
	 * @throws org.json.JSONException
	 */
	public Document getDocument(File file, ICloudDocument cloudDocument, String fileName) throws JSONException {
		Document document = null;
		CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
		JSONObject jsonObject = new JSONObject();
		if (file.exists()) {
			String fileExtension = FilenameUtils.getExtension(fileName);

			jsonObject.put("name", cloudDocument.getObjectName());
			jsonObject.put("filesCount", "1");
			jsonObject.put("checkOutPath", cloudDocument.getWorkspace());

			jsonObject.put("relativeCheckOutDir", file.getParentFile().getParentFile().getPath());
			jsonObject.put("relativeCheckOutDirName", file.getParentFile().getParentFile().getName());

			jsonObject.put("absoluteCheckOutDir", file.getParentFile().getPath());
			jsonObject.put("absoluteCheckOutDirName", file.getParentFile().getName());
			jsonObject.put("absoluteCheckOutFilePath", file.getPath());
			jsonObject.put("fileName", file.getName());
			jsonObject.put("filePath", file.getPath());

			jsonObject.put("fileExtension", fileExtension);
			jsonObject.put(DomainConstants.SELECT_ID, cloudDocument.getObjectOid());
			jsonObject.put(DomainConstants.SELECT_TYPE, cloudDocument.getObjectType());
			jsonObject.put(DomainConstants.SELECT_REVISION, cloudDocument.getObjectRevision());

			jsonObject.put("inWorkDir", cloudDocument.getInWorkDir());
			jsonObject.put("inWorkDirName", CloudConstants.Basic.IN_WORK_DIR_NAME.getValue());
			jsonObject.put("inWorkDirFilePath", cloudDocument.getInWorkDir().concat(File.separator).concat(file.getName()));

			document = cloudGenDocUtil.getDocument(jsonObject);
		}
		return document;
	}

	/**
	 * @param cloudDocument
	 * @return
	 */
	public boolean writeGenDocWorkTextFile(ICloudDocument cloudDocument) {
		boolean isSuccessful = true;
		try {
			String workDirBlobPath = cloudDocument.getWorkDirBlobPath();
			String workDirFilePath = cloudDocument.getWorkDirFilePath();
			logger.info(String.format("Work File Path: %s", workDirBlobPath));
			File outFile = new File(workDirFilePath);
			FileOutputStream fos = new FileOutputStream(outFile);
			try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fos))) {
				bufferedWriter.write(workDirBlobPath);
			}
		} catch (IOException e) {
			isSuccessful = false;
			logger.error(String.format("Error occurred: %s", e.getMessage()));
			logger.error(e);
		}
		return isSuccessful;
	}

	/**
	 * @param documentList
	 * @param inWorkDir
	 * @return
	 */
	public boolean copyCheckOutFiles(List<Document> documentList, String inWorkDir) {
		// Refine logs 2018x.6 June-CW (Defect 43327) by DSM Sogeti
		boolean isCopied = true;
		File inWorkFolder = new File(inWorkDir);
		if (!inWorkFolder.exists()) {
			inWorkFolder.mkdirs();
		}
		if (inWorkFolder.exists()) {
			String fileName;
			String absoluteCheckOutFilePath;
			for (Document document : documentList) {
				fileName = document.getFileName();
				absoluteCheckOutFilePath = document.getAbsoluteCheckOutFilePath();
				try {
					fileCopy(absoluteCheckOutFilePath, inWorkDir.concat(File.separator).concat(fileName), Boolean.TRUE);
				} catch (IOException e) {
					isCopied = false;
					logger.error(e.getMessage());
				}
			}
			logger.info("All files copied successfully to in-work folder");
		}
		return isCopied;
	}

	/**
	 * @param sourcePath
	 * @param destPath
	 * @param flag
	 * @throws java.io.IOException
	 */
	public void fileCopy(String sourcePath, String destPath, boolean flag) throws IOException {
		if (flag) {
			Path source = Paths.get(sourcePath);
			Path target = Paths.get(destPath);
			java.nio.file.Files.move(source, target);
		}
	}

	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
	 *
	 * @param cloudConfig
	 * @param checkOutFileList
	 * @return
	 */
	public boolean isSupportedFileExtension(ICloudConfig cloudConfig, List<File> checkOutFileList) {
		StringList extensionList = new StringList();
		extensionList.addAll(StringUtil.split(cloudConfig.getFileExtensionsForIText(), CloudConstants.Basic.SYMBOL_COMMA.getValue()));
		extensionList.addAll(StringUtil.split(cloudConfig.getFileExtensionsForCloud(), CloudConstants.Basic.SYMBOL_COMMA.getValue()));
		extensionList.add(CloudConstants.Basic.FILE_EXTENSION_PDF.getValue());
		String fileExtension;
		for (File file : checkOutFileList) {
			fileExtension = FilenameUtils.getExtension(file.getName());
			if (extensionList.stream().noneMatch(fileExtension::equalsIgnoreCase)) {
				logger.error(String.format("Unsupported File Extension: %s", file.getName()));
				return false;
			}
		}
		return true;
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
	 *
	 * @param cloudDocument
	 * @param checkOutFileList
	 * @return
	 * @throws org.json.JSONException
	 */
	public List<Document> getDocuments(ICloudDocument cloudDocument, List<File> checkOutFileList) throws JSONException {
		List<Document> documentList = new ArrayList<>();
		JSONObject jsonObject;
		int size = checkOutFileList.size();
		String fileName;
		String fileExtension;

		ICloudConfig cloudConfig = cloudDocument.getCloudConfig();
		StringBuilder pathBuilder;

		String downloadFileName;
		Document document;
		String objectId = cloudDocument.getObjectOid();

		CloudGenDocUtil cloudGenDocUtil = new CloudGenDocUtil();
		for (File file : checkOutFileList) {
			fileName = file.getName();

			downloadFileName = fileName.substring(0, fileName.lastIndexOf(".") + 1).concat(CloudConstants.Basic.FILE_EXTENSION_PDF.getValue());

			fileExtension = FilenameUtils.getExtension(fileName);
			jsonObject = new JSONObject();
			jsonObject.put("fileName", fileName);
			jsonObject.put("filesCount", String.valueOf(size));
			jsonObject.put("checkOutPath", cloudDocument.getWorkspace());
			jsonObject.put("relativeCheckOutDir", file.getParent());
			jsonObject.put("relativeCheckOutDirName", file.getParentFile().getName());
			jsonObject.put("absoluteCheckOutDir", file.getParent());
			jsonObject.put("absoluteCheckOutDirName", file.getParentFile().getName());
			jsonObject.put("absoluteCheckOutFilePath", file.getPath());

			// build blob upload path
			pathBuilder = new StringBuilder();
			pathBuilder.append(cloudConfig.getBlobUploadPath());
			pathBuilder.append(cloudDocument.getRootFolderPrefix());
			pathBuilder.append(File.separator);
			pathBuilder.append(objectId);
			pathBuilder.append(File.separator);
			pathBuilder.append(fileName);

			jsonObject.put("blobFileUploadPath", pathBuilder.toString());

			// build blob download path
			pathBuilder = new StringBuilder();
			pathBuilder.append(cloudConfig.getBlobDownloadPath());
			pathBuilder.append(cloudDocument.getRootFolderPrefix());
			pathBuilder.append(File.separator);
			pathBuilder.append(objectId);
			pathBuilder.append(File.separator);
			pathBuilder.append(downloadFileName);
			jsonObject.put("blobFileDownloadPath", pathBuilder.toString());

			pathBuilder = new StringBuilder();
			pathBuilder.append(cloudDocument.getAbsoluteDownloadDir());
			pathBuilder.append(File.separator);
			pathBuilder.append(downloadFileName);
			jsonObject.put("absoluteDownloadFilePath", pathBuilder.toString());
			jsonObject.put("blobDownloadFileName", downloadFileName);

			jsonObject.put("fileExtension", fileExtension);
			jsonObject.put(DomainConstants.SELECT_ID, cloudDocument.getObjectOid());
			jsonObject.put(DomainConstants.SELECT_NAME, cloudDocument.getObjectName());
			jsonObject.put(DomainConstants.SELECT_TYPE, cloudDocument.getObjectType());
			jsonObject.put(DomainConstants.SELECT_REVISION, cloudDocument.getObjectRevision());

			jsonObject.put("inWorkDir", cloudDocument.getInWorkDir());
			jsonObject.put("inWorkDirName", CloudConstants.Basic.IN_WORK_DIR_NAME.getValue());
			jsonObject.put("inWorkDirFilePath", cloudDocument.getInWorkDir().concat(File.separator).concat(fileName));

			document = cloudGenDocUtil.getDocument(jsonObject);
			if (null != document && Boolean.valueOf(document.getLoaded())) {
				documentList.add(document);
			}
		}
		return documentList;
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends


	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - Starts
	 *
	 * @param fileExtension
	 * @return
	 */
	public static boolean isTextFile(String fileExtension) {
		return StringUtils.containsIgnoreCase(CloudConstants.Basic.FILE_EXTENSION_TXT.getValue(), fileExtension);
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - End

	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - Starts
	 *
	 * @param fileExtension
	 * @return
	 */
	public static boolean isAdobeIllustratorFile(String fileExtension) {
		return StringUtils.containsIgnoreCase(CloudConstants.Basic.FILE_EXTENSION_AI.getValue(), fileExtension);
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - End

	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - Starts
	 *
	 * @param extensions
	 * @param fileExtension
	 * @return
	 */
	public static boolean isImageFile(String extensions, String fileExtension) {
		return StringUtils.containsIgnoreCase(extensions, fileExtension) && !isTextFile(fileExtension) && !isAdobeIllustratorFile(fileExtension);
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43662 - End

	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433 - Starts
	 *
	 * @param fileName
	 * @param charactersNotAllowedForFileName
	 * @return
	 */
	public String getCleanedUpFileName(String fileName, String charactersNotAllowedForFileName) {
		final StringList invalidCharacterList = StringUtil.split(charactersNotAllowedForFileName.trim(), CloudConstants.Basic.SYMBOL_COMMA.getValue());
		for (String character : invalidCharacterList) {
			if(fileName.contains(character)) {
				fileName = fileName.replace(character, DomainConstants.EMPTY_STRING);
			}
		}
		return fileName;
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43433 - Ends

	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43171 - Starts
	 *
	 * @return
	 */
	public static String getTimeStamp() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CloudConstants.Basic.DATE_FORMAT_FOR_TIMESTAMP.getValue());
		return simpleDateFormat.format(new Date());
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43171 - Ends

	/**
	 * Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Starts
	 *
	 * @param cloudDocument
	 * @param cloudBlobContainer
	 * @param cloudDocumentCount
	 * @param uploadSuccessCount
	 * @param uploadedCount
	 * @return
	 * @throws java.net.URISyntaxException
	 * @throws InterruptedException
	 * @throws com.microsoft.azure.storage.StorageException
	 */
	public GenDocProcessResponse getCloudDownloadInfo(ICloudDocument cloudDocument, CloudBlobContainer cloudBlobContainer, int cloudDocumentCount, int uploadSuccessCount, int uploadedCount) throws URISyntaxException, InterruptedException, StorageException {
		// Continuous improvement 2018x.6 Sept-CW (Defect 44025)
		Map<Object, Object> returnMap = new HashMap<>();

		// need to initialize with 1.
		int returnInteger = 1;
		String returnMessage = DomainConstants.EMPTY_STRING;
		boolean isTimedOut = Boolean.TRUE;

		final String objectType = cloudDocument.getObjectType();
		final String objectName = cloudDocument.getObjectName();
		final String objectRevision = cloudDocument.getObjectRevision();
		final String objectOid = cloudDocument.getObjectOid();

		if (uploadSuccessCount == cloudDocumentCount) {
			logger.info(String.format("Azure Input & Upload Count Match|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
			if (uploadedCount > 0) {
				logger.info(String.format("Azure Upload Files Count>0|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
				final AzureBlobFileWatch azureBlobFileWatch = new AzureBlobFileWatch.ProcessBuilder(cloudDocument, cloudBlobContainer, uploadSuccessCount).build();
				if (azureBlobFileWatch.isUploadAndDownloadBlobFilesCountMatch()) {
					logger.info(String.format("Azure Upload & Download Count Match|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
					final AzureBlobFileDownload azureBlobFileDownload = new AzureBlobFileDownload.ProcessBuilder(cloudDocument, azureBlobFileWatch.getDirectoryReference()).build();
					if (azureBlobFileDownload.isBlobDownloadSuccessful()) {
						logger.info(String.format("Azure Download is successful|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
						returnInteger = 0;
						isTimedOut = Boolean.FALSE;
					} else {
						logger.info(String.format("Azure Download is not successful|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
						returnMessage = azureBlobFileDownload.getErrorMessage();
					}
				} else {
					logger.info(String.format("Azure Upload & Download Count Mismatch|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
					returnMessage = CloudConstants.Basic.AZURE_BLOB_UPLOAD_AND_DOWNLOAD_FILES_COUNT_MISMATCH.getValue();
				}
			} else {
				logger.info(String.format("Azure Uploaded count<0|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
				returnMessage = CloudConstants.Basic.ERROR_CLOUD_INPUT_OUTPUT_COUNT_MISMATCH.getValue();
			}
		} else {
			logger.info(String.format("Azure Input & Upload Count Mismatch|For Object|%s|%s|%s|%s", objectType, objectName, objectRevision, objectOid));
			returnMessage = CloudConstants.Basic.ERROR_CLOUD_INPUT_OUTPUT_COUNT_MISMATCH.getValue();
		}
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_INTEGER.getValue(), returnInteger);
		returnMap.put(CloudConstants.Basic.KEY_GEN_DOC_PROCESS_RETURN_STRING.getValue(), returnMessage);
		returnMap.put(CloudConstants.Basic.KEY_WORD_TIMED_OUT.getValue(), isTimedOut);
		logger.info(String.format("Is Timed-Out? -> %s|Return Integer -> %s|Return Message -> %s|For Object|%s|%s|%s|%s", isTimedOut, returnInteger, returnMessage, objectType, objectName, objectRevision, objectOid));
		return new GenDocProcessResponse(returnMap);
	} // Added by DSM (Sogeti) - 2018x.6 July-CW for Defect 43669 - Ends

    /**
     * Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Starts
     *
     * @param fileName
     * @return
     */
    public static String getRandomUUIDForFileNamePostFix(String fileName) {
        final String extension = FilenameUtils.getExtension(fileName);
        StringBuilder builder = new StringBuilder();
        builder.append(CloudConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        builder.append(extension.toUpperCase());
        builder.append(CloudConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        builder.append(java.util.UUID.randomUUID().toString().replace(CloudConstants.Basic.SYMBOL_HYPHEN.getValue(), DomainConstants.EMPTY_STRING).toUpperCase());
        builder.append(CloudConstants.Basic.SYMBOL_UNDERSCORE.getValue());
        builder.append(String.valueOf(System.nanoTime()));
        builder.append(CloudConstants.Basic.SYMBOL_DOT.getValue());
        builder.append(extension.toLowerCase());
        return builder.toString();
    } // Added by DSM (Sogeti) - 2018x.6 Sept-CW for Defect 44025 - Ends
    
    /**
	 * Added by (IRM-Sogeti) for 22x.02 May CW (Defect-45747) -
	 * @param files
	 * @return
	 */
    public MapList getFileNameInSortedOrder(java.io.File[] files) {
		MapList mapList = new MapList();
		Map<Object, Object> objectMap;
		for (File file: files) {
			objectMap = new HashMap<>();
			objectMap.put(DomainConstants.SELECT_NAME, file.getName());
			objectMap.put("file", file);
			mapList.add(objectMap);
		}
		logger.info(String.format("File Names Before Sorting|%s", mapList));
		mapList.sort(DomainConstants.SELECT_NAME, "ascending", "string");
		logger.info(String.format("File Names After Sorting|%s", mapList));
		return mapList;
	}
}
