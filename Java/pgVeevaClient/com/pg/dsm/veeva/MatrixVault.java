/*
 **   MatrixVault.java
 **   Description - Introduced as part of Veeva integration.      
 **   Entry point for updating data on DSM 
 **
 */

package com.pg.dsm.veeva;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.helper.Matrix;
import com.pg.dsm.veeva.helper.MatrixOperation;
import com.pg.dsm.veeva.helper.MatrixValidator;
import com.pg.dsm.veeva.helper.enovia.ArtworkErrorMessage;
import com.pg.dsm.veeva.helper.enovia.GCASValidator;
import com.pg.dsm.veeva.io.FileUtil;
import com.pg.dsm.veeva.io.RequiredFolders;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.VQL;
import com.pg.dsm.veeva.vql.factory.DocumentPropertyVQLFactory;
import com.pg.dsm.veeva.vql.factory.VQLFactory;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentProperty;
import com.pg.dsm.veeva.vql.json.binder.documents_query.Document;
import com.pg.dsm.veeva.vql.json.binder.documents_query.DocumentResponseMapper;
import com.pg.dsm.veeva.vql.json.binder.users_email.UsersEmail;
import com.pg.dsm.veeva.vql.xml.binder.bo.Select;

import matrix.db.Context;
import matrix.util.StringList;

public class MatrixVault {

	Configurator configurator;
	Context context;
	MatrixVaultError matrixVaultError;

	/** 
	 * @about - Constructor
	 * @param - Configurator - configurator object
	 * @since DSM 2018x.3
	 */
	public MatrixVault(Configurator configurator) {
		this.configurator = configurator;
		this.context = configurator.getContext();
		matrixVaultError = new MatrixVaultError(configurator);
	}
	/** 
	 * @about - Getter method - get MatrixVaultError object
	 * @return - MatrixVaultError - MatrixVaultError object
	 * @since DSM 2018x.3
	 */
	public MatrixVaultError getMatrixVaultError() {
		return matrixVaultError;
	}
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	/** 
	 * @about - Helper method for veeva-ctrlm execution 
	 * @return void
	 * @throws Exception
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void updateScheduled() throws Exception {
		logger.info("Enter MatrixVault -method updateScheduled >> ");
		if(context.isConnected()) {
			List<Map<String, ArtworkErrorMessage>> allArtworksNotificationErrors = new ArrayList<Map<String, ArtworkErrorMessage>>();
			List<Map<String, List<ArtworkErrorMessage>>> allArtworkExceptions = new ArrayList<Map<String, List<ArtworkErrorMessage>>>();
			Map<String, List<UsersEmail>> artworkEmailMap = new HashMap<String, List<UsersEmail>>();
            Map<String, DocumentProperty> successfulArtworkMap = new HashMap<String, DocumentProperty>();
			update( allArtworksNotificationErrors, 
					artworkEmailMap,
					allArtworkExceptions,
					successfulArtworkMap
					);
			logger.info("Update View and notify document owner start >>");
			matrixVaultError.updateFailureViewAndNotify(allArtworksNotificationErrors, artworkEmailMap);
			logger.info("Update View and notify document owner end >>");
			
			logger.info("Notify execption to Support Team and document owner start >>");
			matrixVaultError.notifyArtworkException(allArtworkExceptions, artworkEmailMap);
			logger.info("Notify execption to Support Team and document owner end >>");
			
			logger.info("Notify artwork success email start >>");
			matrixVaultError.notifyArtworkSuccess(successfulArtworkMap, artworkEmailMap);
			logger.info("Notify artwork success email end >>");
			
		} else {
			logger.error("Matrix context is lost");
		}
		logger.info("Exit MatrixVault -method updateScheduled >> ");
	}

	/** 
	 * @about - Helper method for veeva
	 * @param List - documentList - list of document
	 * @param List allArtworksNotificationErrors - list of artwork messages
	 * @param Map - artworkEmailMap - Map of user emails  
	 * @param List - allArtworkExceptions - list of artwork Exceptions
	 * @param Map - successfulArtworkMap - Map of successful artworks details
	 * @return void
	 * @throws Exception
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void update(List<Map<String, ArtworkErrorMessage>> allArtworksNotificationErrors, Map<String, List<UsersEmail>> artworkEmailMap, List<Map<String, List<ArtworkErrorMessage>>> allArtworkExceptions, Map<String, DocumentProperty> successfulArtworkMap) throws Exception {
		logger.info("Enter MatrixVault -method update >> ");
		logger.info("=========================================>MATRIX UPDATE - START<===============================================================================");
		VQL documentDataPropertyVQL = VQLFactory.getVQL(new DocumentPropertyVQLFactory(configurator));
		logger.info("Document Property Query initialized >> ");
		List<Select> documentPropertyBusinessObjectSelect = documentDataPropertyVQL.getQuery().getQueryBuilder().get(0)
				.getBusinessobject().get(0).getSelectable().get(0).getSelect();
		logger.info("Document Property Business Selects >> "+documentPropertyBusinessObjectSelect.size());

		String path;
		path = configurator.getProperties().getProperty("veeva.extraction.folder");
		logger.info("Extraction folder path >> "+path);
		RequiredFolders folders = new RequiredFolders(path);
		logger.info("Required Folders object initialized >> ");
		String documentOutputFile;
		
		documentOutputFile = FileUtil.getDocumentsQueryResponseFile(folders.getInwork());
		logger.info("Document Query File >> "+documentOutputFile);
		DocumentResponseMapper documentResponseMapper = new ObjectMapper().readValue(new File(documentOutputFile),
				DocumentResponseMapper.class);
		logger.info("Document Query Response bean loaded");

		List<Document> documentList = documentResponseMapper.getDocuments();
		logger.info("Documents List >> "+documentList.size());
		
		String sCurrentDate = Utility.getCurrentDate();
		logger.info("Current Date >> "+sCurrentDate);
		Matrix matrix;
		int count = documentList.size();
		logger.info("Number of documents to update in Matrix: "+String.valueOf(count));

		Map<String, ArtworkErrorMessage> artworkErrorMap = null;
		List<ArtworkErrorMessage> allArtworkExceptionMessages = null;
		List<String> allGcasValidatorExceptionMessages = null;
		List<String> artworkValidationErrorMessages = null;
		Map<String, List<ArtworkErrorMessage>> artworkExceptionMap = null;
		Document document;
		String documentID;
		String documentNumber;
		String artworkName;
		List<UsersEmail> usersEmail;
		MatrixValidator matrixValidator;
		GCASValidator gcasValidator;
		MatrixOperation matrixOperations;
		List<ArtworkErrorMessage> artworkConnectErrorMessages;
		List<ArtworkErrorMessage> artworkCreateExceptionMessages;
		ArtworkErrorMessage artworkErrorMessage;
		int errorCount;

		for (int i = 0; i <count; i++) {
			artworkErrorMap = new HashMap<String, ArtworkErrorMessage>();
			allArtworkExceptionMessages = new ArrayList<ArtworkErrorMessage>();
			allGcasValidatorExceptionMessages = new ArrayList<String>();
			artworkExceptionMap = new HashMap<String, List<ArtworkErrorMessage>>();
			document = documentList.get(i);
			documentID = document.getId();
			documentNumber = document.getDocumentNumber();
			artworkName = DomainConstants.EMPTY_STRING;

			// check if an artwork exist with the given document id - this check should happen only for artwork ctrlm job.
			if(IsArtworkExist(context, documentID)) {
				logger.info("Scheduled CTRL(M) - execution - Artwork exist - move the folder start");
				Utility.moveDirectory(documentID, folders.getInwork(), folders.getSkip());
				if (i == count-1) {
					Utility.moveToProcessedDirectory(documentOutputFile,folders.getProcessed(), sCurrentDate);
				}
				logger.info("Scheduled CTRL(M) - execution - Artwork exist - move the folder end - continue to next record");
				continue;
			}	

			logger.info("***********MATRIX UPDATE - DOCUMENT ID >>"+documentID+"<< DOCUMENT NUMBER >>"+documentNumber+"<< START*********************************");
				
			logger.info("Processing Counter___________"+String.valueOf(count)+" Document ID "+documentID+" Document Number "+ documentNumber+" ______________ started");
			
			matrix = new Matrix.Update(configurator, 
					documentPropertyBusinessObjectSelect, 
					folders, 
					documentID,
					documentNumber)
					.perform();
			logger.info("Matrix update bean loaded");
			usersEmail = matrix.getUsersMapper().getUsersEmail();
			artworkEmailMap.put(documentID, usersEmail);
			logger.info("Users Email: "+usersEmail.size());
			try {
				logger.info("Matrix create or update artwork - start");
				matrix.processArtwork();
				logger.info("Matrix create or update artwork - end");
				matrixValidator = matrix.getMatrixValidator();
				artworkValidationErrorMessages = matrixValidator.getValidationErrorMessages();
				logger.info("Matrix artwork created? "+String.valueOf(matrix.isArtworkCreated()));
				// gcas validation error messages
				allGcasValidatorExceptionMessages.addAll(artworkValidationErrorMessages);
				if(matrix.isArtworkCreated()) {
					artworkName = matrix.getArtworkName();
					gcasValidator = matrixValidator.getGcasValidator();
					logger.info("MatrixVault - is gcas valid>>"+String.valueOf(gcasValidator.isGcasValid()));
					logger.info("MatrixVault - is gcas exist>>"+String.valueOf(gcasValidator.isGcasExist()));
					logger.info("MatrixVault - is gcas has artwork>>"+String.valueOf(gcasValidator.isGcasHasArtwork()));
					logger.info("Proceed for perform connect operations for Document ID <<"+documentID+">> - start");
					matrixOperations = new MatrixOperation.Builder(matrix)
							.connectTemplate()
							.promote()//Modified by IRM (DSM) for Emergency fix Defect #53307
							.connectGcas()
							.connectPrimaryOrganization()
							.connectSecondaryOrganization()
							.connectControlClass()
							.connectSegment()
							.connectCountries()
							.connectIPMDocument()
							.triggerDynamicSubscription()
							.perform();
					logger.info("Proceed for perform connect operations for Document ID <<"+documentID+">> - end");
					// exceptions message while connecting template/gcas/primary.org/secondary.org/control.class/ etc..
					artworkConnectErrorMessages = matrixOperations.getArtworkErrorMessages();
					logger.info("Matrix Vault - Number of Artwork Connect or Update Error Messages:"+artworkConnectErrorMessages.size());
					allArtworkExceptionMessages.addAll(artworkConnectErrorMessages);
					artworkCreateExceptionMessages = matrix.getArtworkErrorMessages();
					logger.info("Matrix Vault - Number of Artwork Create Error Messages:"+artworkCreateExceptionMessages.size());
					// co/ca creation error messages
					allArtworkExceptionMessages.addAll(artworkCreateExceptionMessages);
					// gcas validation exception messages
					allArtworkExceptionMessages.addAll(matrixValidator.getExceptionMessages());
					matrix.updateFailureView(allGcasValidatorExceptionMessages, allArtworkExceptionMessages);
				} 
				logger.info("Matrix Vault - Number of Artwork Validation Error & Exception Messages for Document ID <<"+documentID+">>:"+allGcasValidatorExceptionMessages.size());

			} catch (Exception e) {
				logger.error("Error in processing " + documentID + " : " + documentNumber + " : " + e.getMessage());
				e.printStackTrace();
			}
			logger.info("Matrix Vault - Number of Artwork Error Messages for Document ID <<"+documentID+">> :"+allArtworkExceptionMessages.size());
			logger.info("Matrix Vault - Number of User Emails for Document ID <<"+documentID+">> :"+usersEmail.size());

			if(null == artworkValidationErrorMessages)
				artworkValidationErrorMessages = new ArrayList<String>();

			logger.info("Number of Artwork validation error messages >> "+artworkValidationErrorMessages.size());
			
			artworkErrorMessage = new ArtworkErrorMessage.Builder()
					.setArtworkName(artworkName)
					.setDocumentID(documentID)
					.setDocumentNumber(documentNumber)
					.setGcas(matrix.getGcas())
					.setValidationErrorMessages(artworkValidationErrorMessages)
					.build();
			artworkErrorMap.put(documentID, artworkErrorMessage);

			allArtworksNotificationErrors.add(artworkErrorMap);
			artworkExceptionMap.put(documentID, allArtworkExceptionMessages);
     		allArtworkExceptions.add(artworkExceptionMap);
			
			logger.info("Number of Gcas validation Exception messages >> "+allGcasValidatorExceptionMessages.size());
			logger.info("Processing Counter___________"+String.valueOf(count)+" Document ID "+documentID+" Document Number "+ documentNumber+" ______________ ended");
			errorCount = allArtworkExceptionMessages.size() + allGcasValidatorExceptionMessages.size();
			logger.info("Total Exception & Validation Message count >>"+String.valueOf(errorCount));
			if(errorCount == 0) {
				successfulArtworkMap.put(matrix.getArtworkName(), matrix.getDocumentProperty());
			}
			logger.info("Move folder started for document number "+documentNumber+" document ID "+documentID);
			matrix.moveDirectory(documentID, documentNumber, errorCount, sCurrentDate);
			logger.info("Move folder ended for document number "+documentNumber+" document ID "+documentID);
			if (i == count-1) {
				matrix.moveToProcessedDirectory(documentOutputFile, sCurrentDate);
			}
			logger.info("***********MATRIX UPDATE - DOCUMENT ID >>"+documentID+"<< DOCUMENT NUMBER >>"+documentNumber+"<< END*********************************");
		}
		logger.info("=========================================>MATRIX UPDATE - END<===============================================================================");
		logger.info("Exit MatrixVault -method update >> ");
	}
	/** 
	 * @about - Helper method for to check if an artwork object exist
	 * @param Context - matrix context
	 * @param String - sDocumentId - String document id 
	 * @return true/false - boolean
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public boolean IsArtworkExist(Context context, String sDocumentId) throws Exception{
		boolean isExist = false;
		try {
			StringBuffer sWhere = new StringBuffer();
			StringList slArtworkObjectSelects = new StringList();
			slArtworkObjectSelects.add(DomainConstants.SELECT_ID);

			sWhere.append(Veeva.SELECT_ATTRIBUTE_GEPADEXPROJECTNUMBER).append(Veeva.CONST_SYMBOL_EQUAL).append(Veeva.CONST_SYMBOL_EQUAL).append(sDocumentId); 
			MapList mlArtwork = DomainObject.findObjects(context, Veeva.TYPE_PGARTWORK, // typePattern
					DomainConstants.QUERY_WILDCARD, // name pattern
					DomainConstants.QUERY_WILDCARD, // rev pattern
					DomainConstants.QUERY_WILDCARD, // owner pattern
					Veeva.VAULT_ESERVICEPRODUCTION, // vault pattern
					sWhere.toString(), // where exp
					true, // expandType
					slArtworkObjectSelects); // objectSelects
			if(mlArtwork.size()>0) {
				isExist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();

			throw e;
		}
		return isExist;
	}	
}
