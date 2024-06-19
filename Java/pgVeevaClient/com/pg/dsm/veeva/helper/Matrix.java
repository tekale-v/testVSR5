/*
 **   Matrix.java
 **   Description - Introduced as part of Veeva integration.      
 **   To perform update operation on DSM.
 **
 */
package com.pg.dsm.veeva.helper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.helper.enovia.ArtworkErrorMessage;
import com.pg.dsm.veeva.helper.enovia.bo.Artwork;
import com.pg.dsm.veeva.helper.enovia.bo.ChangeOrder;
import com.pg.dsm.veeva.helper.enovia.bo.ChangeOrderImpl;
import com.pg.dsm.veeva.helper.enovia.failure.FailureView;
import com.pg.dsm.veeva.helper.enovia.failure.factory.ArtworkFailureViewFactory;
import com.pg.dsm.veeva.helper.enovia.failure.factory.FailureViewFactory;
import com.pg.dsm.veeva.io.FileUtil;
import com.pg.dsm.veeva.io.RequiredFolders;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentProperty;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentPropertyMapper;
import com.pg.dsm.veeva.vql.json.binder.users_email.UsersMapper;
import com.pg.dsm.veeva.vql.xml.binder.bo.Select;

import matrix.db.Context;
import matrix.util.StringList;


public class Matrix implements Veeva {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	RequiredFolders folders;
	String documentID;
	String documentNumber;
	File[] renditionFiles;
	boolean isDocumentResponsePathExist;
	boolean isDocumentPropertyFileExist;
	boolean isUserEmailsResponseFileExist;
	boolean isRenditionFileExist;
	boolean isChangeOrderAndArtworkConnected;
	DocumentPropertyMapper documentPropertyMapper;
	DocumentProperty documentProperty;
	UsersMapper usersMapper;
	Configurator configurator;

	List<Select> documentPropertyBusinessObjectSelect;
	Context context;
	Properties properties;
	Artwork artwork;
	ChangeOrder changeOrder;
	String gcas;
	String renditionFolderPath;
	String artworkId;
	String artworkName;

	MatrixValidator matrixValidator;
	List<ArtworkErrorMessage> artworkErrorMessages = new ArrayList<ArtworkErrorMessage>();
    //DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
	boolean isArtworkCreated;
    //DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
	/** 
	 * @about Private Constructor
	 * @param Update - (builder class)
	 * @since DSM 2018x.3
	 */
	private Matrix(Update action) {
		this.configurator = action.configurator;
		this.documentPropertyBusinessObjectSelect = action.documentPropertyBusinessObjectSelect;
		this.folders = action.folders;
		this.documentID = action.documentID;
		this.renditionFiles = action.renditionFiles;
		this.isDocumentResponsePathExist = action.isDocumentResponsePathExist;
		this.isDocumentPropertyFileExist = action.isDocumentPropertyFileExist;
		this.isUserEmailsResponseFileExist = action.isUserEmailsResponseFileExist;
		this.isRenditionFileExist = action.isRenditionFileExist;
		this.documentPropertyMapper = action.documentPropertyMapper;
		this.usersMapper = action.usersMapper;
		this.context = configurator.getContext();
		this.properties = configurator.getProperties();
		this.artwork = null;
		this.changeOrder = null;
		this.isChangeOrderAndArtworkConnected = false;
		this.renditionFolderPath = action.renditionFolderPath;
		this.documentProperty = documentPropertyMapper.getDocumentProperty().get(0);
		this.documentNumber = action.documentNumber;
		this.gcas = documentProperty.getPmp();
		this.artworkId = DomainConstants.EMPTY_STRING;
		//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
		this.artworkName = DomainConstants.EMPTY_STRING;
		this.isArtworkCreated = false;
		//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
	
	}
	/** 
	 * @about Getter method to gcas name
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getGcas() {
		return gcas;
	}
	/** 
	 * @about Getter method to artwork name
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getArtworkName() {
		return artworkName;
	}
	/** 
	 * @about Setter method to set artwork name
	 * @param String - artwork name string
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setArtworkName(String artworkName) {
		this.artworkName = artworkName;
	}
	/** 
	 * @about Getter method to artwork error message list
	 * @return List
	 * @since DSM 2018x.3
	 */
	public List<ArtworkErrorMessage> getArtworkErrorMessages() {
		return artworkErrorMessages;
	}
	/** 
	 * @about Getter method to get document property object
	 * @return DocumentProperty
	 * @since DSM 2018x.3
	 */
	public DocumentProperty getDocumentProperty() {
		return documentProperty;
	}
	/** 
	 * @about Getter method to select list from bean
	 * @return List
	 * @since DSM 2018x.3
	 */
	public List<Select> getDocumentPropertyBusinessObjectSelect() {
		return documentPropertyBusinessObjectSelect;
	}
	
	/** 
	 * @about Getter method to artwork id
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getArtworkId() {
		return artworkId;
	}
	
	/** 
	 * @about Getter method to MatrixValidator object
	 * @return MatrixValidator
	 * @since DSM 2018x.3
	 */
	public MatrixValidator getMatrixValidator() {
		return matrixValidator;
	}
	/** 
	 * @about Getter method to document number
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getDocumentNumber() {
		return documentNumber;
	}
	/** 
	 * @about Getter method to rendition folder path
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getRenditionFolderPath() {
		return renditionFolderPath;
	}
	/** 
	 * @about Getter method to RequiredFolders bean
	 * @return RequiredFolders
	 * @since DSM 2018x.3
	 */
	public RequiredFolders getFolders() {
		return folders;
	}
	/** 
	 * @about Getter method to document id
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getDocumentID() {
		return documentID;
	}
	/** 
	 * @about Getter method to list of rendition Files 
	 * @return File[]
	 * @since DSM 2018x.3
	 */
	public File[] getRenditionFiles() {
		return renditionFiles;
	}
	/** 
	 * @about Getter method to check if document response path exist 
	 * @return booelan
	 * @since DSM 2018x.3
	 */
	public boolean isDocumentResponsePathExist() {
		return isDocumentResponsePathExist;
	}
	/** 
	 * @about Getter method to check if document property response path exist 
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isDocumentPropertyFileExist() {
		return isDocumentPropertyFileExist;
	}
	/** 
	 * @about Getter method to check if users email response path exist 
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isUserEmailsResponseFileExist() {
		return isUserEmailsResponseFileExist;
	}
	/** 
	 * @about Getter method to get check if rendition response path exist 
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isRenditionFileExist() {
		return isRenditionFileExist;
	}
	/** 
	 * @about Getter method to get jackson DocumentPropertyMapper bean
	 * @return DocumentPropertyMapper
	 * @since DSM 2018x.3
	 */
	public DocumentPropertyMapper getDocumentPropertyMapper() {
		return documentPropertyMapper;
	}
	/** 
	 * @about Getter method to get jackson UsersMapper bean
	 * @return UsersMapper
	 * @since DSM 2018x.3
	 */
	public UsersMapper getUsersMapper() {
		return usersMapper;
	}
	/** 
	 * @about Getter method to Configurator bean
	 * @return Configurator
	 * @since DSM 2018x.3
	 */
	public Configurator getConfigurator() {
		return configurator;
	}
	/** 
	 * @about Getter method to check artwork and co are connected
	 * @return booelan
	 * @since DSM 2018x.3
	 */
	public boolean isChangeOrderAndArtworkConnected() {
		return isChangeOrderAndArtworkConnected;
	}
	/** 
	 * @about Getter method to get Context 
	 * @return Context
	 * @since DSM 2018x.3
	 */
	public Context getContext() {
		return context;
	}
	/** 
	 * @about Getter method to Properties object
	 * @return Context
	 * @since DSM 2018x.3
	 */
	public Properties getProperties() {
		return properties;
	}
	/** 
	 * @about Getter method to Artwork object
	 * @return Artwork
	 * @since DSM 2018x.3
	 */
	public Artwork getArtwork() {
		return artwork;
	}
	/** 
	 * @about Getter method to ChangeOrder object
	 * @return ChangeOrder
	 * @since DSM 2018x.3
	 */
	public ChangeOrder getChangeOrder() {
		return changeOrder;
	}
	/** 
	 * @about Getter artwork creation status
	 * @return boolean
	 * @since DSM 2018x.5
	 */
	public boolean isArtworkCreated() {
		return isArtworkCreated;
	}
	public static class Update {
		private final Logger logger = Logger.getLogger(Update.class.getName());
		RequiredFolders folders;
		String documentID;
		String documentNumber;
		File[] renditionFiles;
		boolean isDocumentResponsePathExist;
		boolean isDocumentPropertyFileExist;
		boolean isUserEmailsResponseFileExist;
		boolean isRenditionFileExist;
		String renditionFolderPath;
		DocumentPropertyMapper documentPropertyMapper;
		UsersMapper usersMapper;
		Configurator configurator;

		List<Select> documentPropertyBusinessObjectSelect;
		/** 
		 * @about Constructor
		 * @param Update
		 * @since DSM 2018x.3
		 */
		public Update(Configurator configurator, List<Select> documentPropertyBusinessObjectSelect,
				RequiredFolders folders, String documentID, String documentNumber) {
			this.folders = folders;
			this.documentPropertyBusinessObjectSelect = documentPropertyBusinessObjectSelect;
			this.configurator = configurator;
			this.documentID = documentID;
			this.isDocumentResponsePathExist = false;
			this.isDocumentPropertyFileExist = false;
			this.isUserEmailsResponseFileExist = false;
			this.isRenditionFileExist = false;
			this.documentNumber = documentNumber;
		}
		/** 
		 * @about Builder method
		 * @return Matrix
		 * @throws JsonParseException,JsonMappingException,IOException
		 * @since DSM 2018x.3
		 */
		public Matrix perform() throws JsonParseException, JsonMappingException, IOException {
			String documentFolderPath = FileUtil.getDocumentFolderPath(folders.getInwork(), documentID);
			File directory = new File(documentFolderPath);
			if (directory.exists()) {
				this.isDocumentResponsePathExist = true;
				this.renditionFolderPath = documentFolderPath;
				logger.info("document folder path >> " + documentFolderPath);
				String documentPropertyResponseFile = FileUtil.getDocumentPropertyResponseFile(documentFolderPath,
						documentID);
				File file = new File(documentPropertyResponseFile);
				if (file.exists()) {
					this.isDocumentPropertyFileExist = true;
					this.documentPropertyMapper = new ObjectMapper().readValue(file, DocumentPropertyMapper.class);
				} else {
					logger.info(documentID + " Folder not found in inwork directory");
				}
				String usersEmailsFile = FileUtil.getUsersEmailsResponseFile(documentFolderPath, documentID);
				file = new File(usersEmailsFile);
				if (file.exists()) {
					this.isUserEmailsResponseFileExist = true;
					this.usersMapper = new ObjectMapper().readValue(file, UsersMapper.class);
					logger.info("user email file path >>" + usersEmailsFile);
				} else {
					logger.info(documentID + " document is not having owner emails");
				}
				FileFilter fileFilter = new WildcardFileFilter(documentID + STR_RENDITION_PREFIX + SYMBOL_STAR);
				this.renditionFiles = directory.listFiles(fileFilter);
				if (renditionFiles.length > 0) {
					this.isRenditionFileExist = true;
				} else {
					logger.info(documentID + " document is not having rendition files");
				}
			}
			return new Matrix(this);
		}
	}


	/** 
	 * @about Method to create/update artwork and create co, ca
	 * @param String - artwork id string  
	 * @return void
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void processArtwork() {
		try {
			logger.info("proceed to GCAS validation");
			performGcasValidation();	
			if(this.matrixValidator.isCreateArtwork()) {
				// create or update artwork
				createArtwork();
				if(artwork.isCreated()) {
					//Create change order.
					createChangeOrder();
					if(changeOrder.isCreated()) {
						String sChangeOrderId = changeOrder.getChangeOrderID();
						this.matrixValidator.getGcasValidator().setChangeOrderId(sChangeOrderId);
						//Create change action.
						createChangeAction();
					}
					
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	

	/** 
	 * @about Method to perform gcas validations
	 * @return void
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void performGcasValidation() throws Exception {
		this.matrixValidator = new MatrixValidator.Builder(configurator, documentProperty)
				.performGCASNumberOfDigitsValidation()
				.performGcasSearch()
				.peformGcasConnectedArtworkCheck()
				.performGcasRelatedConnectionQuery()
				.setGcasReleasePhase()
				.setGcasStatus()
				.setGcasParentType()
				.performGcasPrimaryOrgCheck()
				.performGcasSecondaryOrgCheck()
				.performGcasSecurityClassCheck()
				.performGcasSegmentCheck()
				.performGcasStageCheck()
				.peformGcasStateCheck()
				.performCountryCheck()
				.build();
	}
	/** 
	 * @about Method to create or update artwork
	 * @param String - artworkID string
	 * @return void
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void createArtwork() {
		try {
			this.artwork = new Artwork.Process(context, 
					properties, 
					documentPropertyMapper.getDocumentProperty().get(0),
					documentPropertyBusinessObjectSelect)
					.perform();

			if(artwork.isCreated()) {
				artworkId = artwork.getArtworkID();	
				artworkName = artwork.getArtworkName();
				this.isArtworkCreated = true;
			} else {
					ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
							.setArtworkName(Veeva.EMPTY_STRING)
							.setDocumentID(this.documentID)
							.setDocumentNumber(this.documentNumber)
							.setGcas(this.gcas)
							.setErrorCode(artwork.getArtworkCreationOrUpdationExceptionMessage())
							.setErrorMessage(artwork.getArtworkCreationOrUpdationExceptionMessage())
							.setCustomErrorMessage(properties.getProperty("veeva.artwork.updation.process.failure.error"))
							.build();
					artworkErrorMessages.add(artworkErrorMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in createOrUpdateArtwork " + e);
		}
	}
	/** 
	 * @about Method to create change action
	 * @return boolean
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public boolean createChangeAction() {
		boolean isConnected = false;
		try {
			isConnected = (boolean) new ChangeOrderImpl().connectArtwork(context, changeOrder.getChangeOrderID(),
					artworkId);
			isChangeOrderAndArtworkConnected = isConnected;

			if(isConnected) {
				String sChangeActionId = getChangeOrderCA(changeOrder.getChangeOrderID());
				if (UIUtil.isNotNullAndNotEmpty(sChangeActionId)) {
					logger.info("Created CA Id: " + sChangeActionId);
					this.matrixValidator.getGcasValidator().setChangeActionId(sChangeActionId);
				}
			}
		} catch (Exception e) {
			isChangeOrderAndArtworkConnected = false;
			logger.error("Error in CA creation " + e.getMessage());
			ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
					.setArtworkName(this.artworkName)
					.setDocumentID(this.documentID)
					.setDocumentNumber(this.documentNumber)
					.setGcas(this.gcas)
					.setErrorCode(e.getClass().getCanonicalName())
					.setErrorMessage(e.getLocalizedMessage())
					.setCustomErrorMessage(properties.getProperty("veeva.co.connetion.failure.error"))
					.build();
			artworkErrorMessages.add(artworkErrorMessage);
		}
		return isConnected;
	}
	/** 
	 * @about Method to create change order
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void createChangeOrder() {
		try {
			this.changeOrder = new ChangeOrder.Create(context, properties, artwork.getObjectId()).perform();
		} catch (Exception e) {
			logger.error("Error in CO creation " + e.getMessage());
			ArtworkErrorMessage artworkErrorMessage = new ArtworkErrorMessage.Builder()
					.setArtworkName(this.artworkName)
					.setDocumentID(this.documentID)
					.setDocumentNumber(this.documentNumber)
					.setGcas(this.gcas)
					.setErrorCode(e.getClass().getCanonicalName())
					.setErrorMessage(e.getLocalizedMessage())
					.setCustomErrorMessage(properties.getProperty("veeva.co.creation.failure.error"))
					.build();
			artworkErrorMessages.add(artworkErrorMessage);
		}
	}

	/** 
	 * @about Method to object info
	 * @return Map
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public Map<?,?> getInfoBean(String objectId) throws Exception {
		DomainObject obj = DomainObject.newInstance(context);
		obj.setId(objectId);
		StringList select = new StringList();
		select.addElement(Veeva.SELECT_NAME);
		select.addElement(Veeva.SELECT_TYPE);
		select.addElement(Veeva.SELECT_REVISION);
		select.addElement(Veeva.SELECT_ID);
		select.addElement(Veeva.SELECT_CURRENT);
		return obj.getInfo(context, select);
	}


	/** 
	 * @about Method to get CA from CO.
	 * @param String - change order id
	 * @return void
	 * @since DSM 2018x.3
	 */
	private String getChangeOrderCA(String sChangeOrderId) throws Exception {
		DomainObject busObj = DomainObject.newInstance(this.context);
		busObj.setId(sChangeOrderId);
		return busObj.getInfo(this.context, "from["+Veeva.RELATIONSHIP_CHANGEACTION+"].to.id");
	}
	
	/** 
	 * @about Method to update failure view (ctrlm)
	 * @param List - list of all validation error message
	 * @param List - list of all artwork exception messages
	 * @throws Exception
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void updateFailureView(List<String> validatorErrorMessages, List<ArtworkErrorMessage> allArtworkExceptionMessages) throws Exception {
		logger.info("Enter Matrix -method updateFailureView");
		int numberOfArtworkValidatorErrorMessages = validatorErrorMessages.size();
		int numberOfArtworkExceptions = allArtworkExceptionMessages.size();
		logger.info("Number of Validation Error Messages >>"+String.valueOf(numberOfArtworkValidatorErrorMessages));
		String currentDate = Utility.getMatrixFormatCurrentDate();
		StringBuilder sbValidationError = new StringBuilder();
		//Modified for Defect#33020 - Starts
		if(numberOfArtworkValidatorErrorMessages > 0 || numberOfArtworkExceptions > 0) {
			sbValidationError.append(properties.getProperty("veeva.pmp.validation.error.prefix").replace("<PAWNUMBER>",this.documentNumber).replace("<PMP>", this.gcas));
		}
		//Modified for Defect#33020 - Ends
		for (int i = 0; i < numberOfArtworkValidatorErrorMessages; i++) {
			sbValidationError.append(Veeva.SYMBOL_NEXT_LINE).append(Veeva.SYMBOL_HYPHEN).append(Veeva.SYMBOL_SPACE);
			sbValidationError.append(validatorErrorMessages.get(i));
		}
		logger.info("Number of Artwork Exception Messages: "+allArtworkExceptionMessages.size());
		String artworkExceptionMessage = getArtworkAllExceptionMessages(allArtworkExceptionMessages);
		logger.info("Artwork Exception Message >>"+artworkExceptionMessage);
		if(artworkExceptionMessage.length()>0) {
			sbValidationError.append(Veeva.SYMBOL_NEXT_LINE);
			sbValidationError.append(artworkExceptionMessage);
		}
		logger.info("Artwork Exception & Validation Error for Update View View Message: "+sbValidationError.toString());
		if(Utility.isNotNullEmpty(this.artworkId)) {
			FailureView failureView = FailureViewFactory.getFailure(new ArtworkFailureViewFactory(configurator, context,
					sbValidationError.toString(), this.documentNumber, this.artworkId, currentDate, EMPTY_STRING, EMPTY_STRING,EMPTY_STRING));
			try {
				failureView.update();
				logger.info("Failure view updation is successfull for artworkId " + this.artworkId);
				if(sbValidationError.length()>0)
					failureView.log();
			} catch (Exception e) {
				logger.error("Exception in failure view updation " + e.getMessage());
				e.printStackTrace();
			}
		} 
		logger.info("Exit Matrix -method updateFailureView");
	}
	/** 
	 * @about Method to parse list of artwork exception message to string
	 * @param List - list of all artwork exception messages
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getArtworkAllExceptionMessages(List<ArtworkErrorMessage> allArtworkExceptionMessages) {
		String errorCode,errorMessage,customErrorMessage;
		StringBuffer messages = new StringBuffer();

		for (int j = 0; j < allArtworkExceptionMessages.size(); j++) {
			ArtworkErrorMessage artworkError = allArtworkExceptionMessages.get(j);
			gcas = artworkError.getGcas();
			documentNumber = artworkError.getDocumentNumber();
			artworkName = artworkError.getArtworkName();
			errorCode = artworkError.getErrorCode();
			errorMessage = artworkError.getErrorMessage();
			customErrorMessage = artworkError.getErrorCustomMessage();

			if(messages.length() > 0)
				messages.append(Veeva.SYMBOL_NEXT_LINE);

			messages.append(Veeva.SYMBOL_HYPHEN)
			.append(Veeva.SYMBOL_SPACE)
			.append(errorCode)
			.append(Veeva.SYMBOL_SPACE).append(errorMessage)
			.append(Veeva.CONST_SYMBOL_COLON)
			.append(Veeva.SYMBOL_SPACE)
			.append(customErrorMessage);
		}
		return messages.toString().replaceAll(Veeva.NULL, Veeva.EMPTY_STRING);
	}

	/** 
	 * @about Method to moved one directory to another directory
	 * @param String - document id
	 * @param String - document number
	 * @param int - counter
	 * @param String - date in string form
	 * @throws IOException
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void moveDirectory(String documentID, String documentNumber,
			int errorCount, String sCurrentDate) throws IOException {

		logger.info("Enter Matrix moveDirectory method");
		logger.info("Document ID >> " + documentID);
		logger.info("Document Number >> " + documentNumber);

		try {
			String documentFolderPath = FileUtil.getDocumentFolderPath(folders.getInwork(), documentID);
			logger.info("In work folder path >> " + documentFolderPath);
			String sTargetFolder = EMPTY_STRING;
			if (errorCount > 0) {
				String documentFailedPath = FileUtil.getDocumentFailedFolderPath(folders.getFailed(), documentID);
				logger.info("failed folder path >> " + documentFailedPath);
				sTargetFolder = documentFailedPath;
			} else {
				String documentSuccessPath = FileUtil.getDocumentSuccessFolderPath(folders.getSuccess(), sCurrentDate,
						documentID);
				logger.info("success folder path >> " + documentSuccessPath);
				sTargetFolder = documentSuccessPath;
			}
			File sourceFile = new File(documentFolderPath);
			File destinationFile = new File(sTargetFolder);

			if (destinationFile.isDirectory()) {
				if (destinationFile.exists()) {
					logger.info(documentID + " folder already exist");
					FileUtils.deleteDirectory(destinationFile);
					logger.info(documentID + " existing folder deleted");
				}
			}

			FileUtils.moveDirectory(sourceFile, destinationFile);
			logger.info(documentID + " folder moved successfully");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception in " + documentID + " directory change " + e.getMessage());
		}
		logger.info("Exit Matrix moveDirectory method");

	}
	/** 
	 * @about Method to move file to another directory
	 * @param String - file name
	 * @param String - date in string form
	 * @throws IOException
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void moveToProcessedDirectory(String masterJsonPath, String sCurrentDate)
			throws IOException {
		try {
			File sourceFile = new File(masterJsonPath);
			File destinationFile = new File(
					folders.getProcessed() + Veeva.DOCUMENT_QUERY_RESPONSE_FILE + Veeva.SYMBOL_HYPHEN + sCurrentDate);
			FileUtils.moveFile(sourceFile, destinationFile);
			logger.info("Master JSON moved successfully to processed folder");
		} catch (Exception e) {
			logger.error("Exception in Master JSON directory change" + e.getMessage());
			e.printStackTrace();
		}
	}

}
