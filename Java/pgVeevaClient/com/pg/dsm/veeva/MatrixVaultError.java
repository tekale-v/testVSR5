/*
 **   MatrixVaultError.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to capture all errors that occurs on DSM and notify user. 
 **
 */
package com.pg.dsm.veeva;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.helper.enovia.ArtworkErrorMessage;
import com.pg.dsm.veeva.helper.enovia.failure.FailureView;
import com.pg.dsm.veeva.helper.enovia.failure.factory.ArtworkFailureViewFactory;
import com.pg.dsm.veeva.helper.enovia.failure.factory.FailureViewFactory;
import com.pg.dsm.veeva.util.Mail;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentProperty;
import com.pg.dsm.veeva.vql.json.binder.documents_query.Document;
import com.pg.dsm.veeva.vql.json.binder.users_email.UsersEmail;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class MatrixVaultError {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	Configurator configurator;
	Context context;
	Properties properties = new Properties();
	List<String> onDemandGeneralErrorList =  new ArrayList<String>();
	List<String> validatorErrorMessages =  new ArrayList<String>();

	/** 
	 * @about - Constructor
	 * @param - Configurator - configurator object
	 * @since DSM 2018x.3
	 */
	public MatrixVaultError(Configurator configurator) {
		this.configurator = configurator;
		this.context = configurator.getContext();
		this.properties = configurator.getProperties();
	}
	/** 
	 * @about - Getter method - to get List of messages
	 * @return - List - message list
	 * @since DSM 2018x.3
	 */
	public List<String> getOnDemandGeneralErrorList() {
		return onDemandGeneralErrorList;
	}
	/** 
	 * @about - Getter method - to get List of validation messages
	 * @return - List - message list
	 * @since DSM 2018x.3
	 */
	public List<String> getValidatorErrorMessages() {
		return validatorErrorMessages;
	}
	/** 
	 * @about - Setter method - set List of validation messages
	 * @param List - list of messages
	 * @return - void
	 * @since DSM 2018x.3
	 */
	public void setValidatorErrorMessages(List<String> validatorErrorMessages) {
		this.validatorErrorMessages = validatorErrorMessages;
	}
	/** 
	 * @about - Method to send failure notification on veeva-ctrlm execution
	 * @param List - allArtworksNotificationErrors - list of artwork messages
	 * @param Map - artworkEmailMap - users email Map
	 * @return void
	 * @throws Exception
	 * @since DSM 2018x.3
	 * DSM modified for 2018.x.5 Requriement 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void updateFailureViewAndNotify(List<Map<String, ArtworkErrorMessage>> allArtworksNotificationErrors, Map<String, List<UsersEmail>> artworkEmailMap) {
         int iAllArtworkNotificationErrors = allArtworksNotificationErrors.size();
         int iUserEmailList = 0;
         int iValidationErrorMessages = 0; 
         String sAdditionalMails = properties.getProperty("veeva.additional.emails");
         Map<String, ArtworkErrorMessage> temp;
         String documentID;
         StringBuilder sbToEmails;
         List<UsersEmail> usersEmailList;
         UsersEmail userEmail;
         ArtworkErrorMessage artworkErrorMessage;
         String documentNumber;
         String gcas;
         List<String> validationErrorMessages;
         String gcasErrorMessage;
         String sendMailOnInvalidGcasCase;
         boolean blnSendEmail;
         StringBuilder sbErrorMessage;
         FailureView failureView;
		for(int i = 0; i<iAllArtworkNotificationErrors; i++) {
			temp = (Map<String, ArtworkErrorMessage>)allArtworksNotificationErrors.get(i);
			for (Entry<String, ArtworkErrorMessage> entry : temp.entrySet()) {

				documentID = entry.getKey();
				sbToEmails = new StringBuilder();

				// emails
				usersEmailList = artworkEmailMap.get(documentID);
				iUserEmailList = usersEmailList.size();

				for (int j = 0; j < iUserEmailList; j++) {
					userEmail = usersEmailList.get(j);
					if (sbToEmails.length() > 0)
						sbToEmails.append(pgV3Constants.SYMBOL_COMMA);
					sbToEmails.append(userEmail.getUserEmailV());
				}
				if(UIUtil.isNotNullAndNotEmpty(sAdditionalMails)) {
					sbToEmails.append(pgV3Constants.SYMBOL_COMMA).append(sAdditionalMails);
				}
				// errors
				artworkErrorMessage = entry.getValue();
				documentNumber = artworkErrorMessage.getDocumentNumber();
				gcas = artworkErrorMessage.getGcas();
				validationErrorMessages = artworkErrorMessage.getValidationErrorMessages();

				gcasErrorMessage = properties.getProperty("veeva.not.valid.gacs.error");
				sendMailOnInvalidGcasCase = properties.getProperty("veeva.send.email.on.invalid.gcas");

				blnSendEmail = true;
				if(validationErrorMessages.contains(gcasErrorMessage)) {
					if(validationErrorMessages.size()>1) 
						blnSendEmail = true;
					else {
						if(sendMailOnInvalidGcasCase.equalsIgnoreCase(Veeva.CONST_YES)) {
							blnSendEmail = true;
						} else {
							blnSendEmail = false;
						}
					}
				} 
				
				sbErrorMessage = new StringBuilder();
				sbErrorMessage.append(pgV3Constants.SYMBOL_NEXT_LINE);
				sbErrorMessage.append(documentNumber)
				.append(pgV3Constants.SYMBOL_SPACE).append(properties.getProperty("veeva.on.artwork"))
				.append(pgV3Constants.SYMBOL_SPACE);
				if(UIUtil.isNotNullAndNotEmpty(artworkErrorMessage.getArtworkName())) {
					sbErrorMessage.append(artworkErrorMessage.getArtworkName())
					.append(pgV3Constants.SYMBOL_SPACE);
				}
				
				sbErrorMessage.append(properties.getProperty("veeva.dsm.reasons"))
				.append(pgV3Constants.SYMBOL_NEXT_LINE)
				.append(pgV3Constants.SYMBOL_NEXT_LINE)
				.append(pgV3Constants.SYMBOL_HYPHEN)
				.append(pgV3Constants.SYMBOL_SPACE)
				.append(getMessageFormat(validationErrorMessages));
				iValidationErrorMessages = validationErrorMessages.size();
				if(iValidationErrorMessages>0) {
					logger.info("Proceed to send email >>");
					logger.info("Send Email?: "+String.valueOf(blnSendEmail));
					if(blnSendEmail) {
						try {
							failureView = FailureViewFactory.getFailure(
									new ArtworkFailureViewFactory(
											configurator,
											context, 
											sbErrorMessage.toString(), 
											documentNumber, 
											Veeva.EMPTY_STRING, 
											Veeva.EMPTY_STRING,
											sbToEmails.toString(), 
											gcas,
											Veeva.EMPTY_STRING)
									);
							
							logger.info("Call Failure view send email start>>");
							logger.info("Send Email Message body: "+sbErrorMessage.toString());
							failureView.sendValidationErrorEmail();
							logger.info("Call Failure view send email end>>");
							logger.info("Mail sent to " + sbToEmails.toString() + "  for document: " + documentNumber + " GCAS : "
									+ gcas);
						} catch (Exception e) {
							logger.error("Sending email failed to " + sbToEmails.toString() + " for document: " + documentNumber
									+ "  GCAS: " + gcas + "  with exception " + e.getMessage());
						}
					}
				}

			}
		}
	} 
	/** 
	 * @about - Method to parse the list of messages into a String. 
	 * @param List - validationErrorMessages - list of artwork messages
	 * @return String - message
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public String getMessageFormat(List<String> validationErrorMessages) {
		StringBuffer sbValidationError = new StringBuffer();
		for (int i = 0; i < validationErrorMessages.size(); i++) {
			if (sbValidationError.length() > 0)
				sbValidationError.append(Veeva.SYMBOL_NEXT_LINE).append(Veeva.SYMBOL_HYPHEN).append(Veeva.SYMBOL_SPACE);
			sbValidationError.append(validationErrorMessages.get(i));
		}
		return sbValidationError.toString();
	}
	
	/** 
	 * @about - Method to send exception notification on veeva-ctrlm execution
	 * @param List - allArtworksExceptionErrors - list of artwork exception errors
	 * @param Map - artworkEmailMap - users email Map
	 * @return void
	 * @throws Exception
	 * @since DSM 2018x.5 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void notifyArtworkException(List<Map<String, List<ArtworkErrorMessage>>> allArtworksExceptionErrors,Map<String, List<UsersEmail>> artworkEmailMap ) {
		
		String strGcas;
		String strDocumentNumber;
		String strDocumentID;
		StringBuilder sbErrorMessage = new StringBuilder();
		List<ArtworkErrorMessage> allArtworkExceptionMessages = null;
		Map<String, List<ArtworkErrorMessage>> mpArtworkExceptions = null;
		int iAllArtworkExceptions =  0;
		iAllArtworkExceptions = allArtworksExceptionErrors.size();
		String sCCList;
		String sExceptionMessage;
		List<UsersEmail> usersEmailList;
		for(int i = 0; i<iAllArtworkExceptions; i++) {
			sbErrorMessage.delete(0, sbErrorMessage.length());
			mpArtworkExceptions = allArtworksExceptionErrors.get(i);
			for (Entry<String, List<ArtworkErrorMessage>> entry : mpArtworkExceptions.entrySet()) {
				strDocumentID = entry.getKey();
				
				// emails
				usersEmailList = artworkEmailMap.get(strDocumentID);
				
				sCCList = getUserList(usersEmailList);
				logger.info("sbCcEmailsList     >>"+sCCList);
				
				allArtworkExceptionMessages = entry.getValue();
				sExceptionMessage = getExceptionMessage(allArtworkExceptionMessages);
				logger.info("sbErrorMessage     >>"+sbErrorMessage.toString());
				if(!allArtworkExceptionMessages.isEmpty()) {
					strGcas = allArtworkExceptionMessages.get(0).getGcas();
					strDocumentNumber = allArtworkExceptionMessages.get(0).getDocumentNumber();
					try {
						FailureView failureView = FailureViewFactory.getFailure(
								new ArtworkFailureViewFactory(
										configurator,
										context, 
										sExceptionMessage, 
										strDocumentNumber, 
										Veeva.EMPTY_STRING, 
										Veeva.EMPTY_STRING,
										properties.getProperty("veeva.configobject.mailid"), 
										strGcas,
										sCCList)
								);
						logger.info("Call artwork exception send email start>>"); 
						failureView.sendExceptionErrorEmail();
						logger.info("Call artwork exception send email end>>");
						logger.info("Exception mail sent to Support Team and veeva document owner" + sCCList + " for document: " + strDocumentNumber + "  GCAS: "
								+ strGcas);
					}catch (Exception e) {
						logger.error("Sending exception email failed to Support Team and veeva document owner" + sCCList + "for document: " + strDocumentNumber
								+ " GCAS: " + strGcas + " with exception " + e.getMessage());
					}
				}					
			}
		}
	}
	
	/** 
	 * @about - Method to send artwork success notification on veeva-ctrlm execution
	 * @param Map - successfulArtworkMap - contains success artwork details
	 * @param Map - artworkEmailMap - users email Map
	 * @return void
	 * @throws Exception
	 * @since DSM 2018x.5
	 * Added as part of 34228
	 */
	public void notifyArtworkSuccess(Map<String, DocumentProperty> successfulArtworkMap, Map<String, List<UsersEmail>> artworkEmailMap) {
		String strArtworkName = Veeva.EMPTY_STRING;
		String strDocumentId = Veeva.EMPTY_STRING;
		String strDocumentNumber = Veeva.EMPTY_STRING;
		String strGcas = Veeva.EMPTY_STRING;
		String sToEmailId = Veeva.EMPTY_STRING;
		try {
			DocumentProperty documentProperty;
			List<UsersEmail> usersEmailList;
			StringBuilder sbMessage;
			StringBuilder sbSubject;
			boolean bMailSent;
			String sEmailFrom = PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT);
			for (Entry<String, DocumentProperty> entry : successfulArtworkMap.entrySet()) {
				strArtworkName = entry.getKey();
				documentProperty = entry.getValue();
				strDocumentId = documentProperty.getId();
				strDocumentNumber = documentProperty.getDocumentNumber();
				strGcas = documentProperty.getPmp();
				usersEmailList = artworkEmailMap.get(strDocumentId);
				
				//To Emails List
				sToEmailId = getUserList(usersEmailList);
				
				logger.info("notifyArtworkSuccess -- sbToEmails "+sToEmailId);
				sbMessage = new StringBuilder();
				sbMessage.append(properties.getProperty("veeva.artwork.success.message").replace("<DOCUMENTNUMBER>", strDocumentNumber))
				.append(pgV3Constants.SYMBOL_NEXT_LINE)
				.append(Veeva.SYMBOL_HYPHEN)
				.append(pgV3Constants.SYMBOL_SPACE)
				.append(strArtworkName);
				logger.info("notifyArtworkSuccess -- sbMessage "+sbMessage.toString());
				
				sbSubject = new StringBuilder();
				sbSubject.append(properties.getProperty("veeva.artWork.success.subject").replace("<DOC ID>", strDocumentNumber).replace("<PMP>",strGcas));
				logger.info("notifyArtworkSuccess -- sbSubject "+sbSubject.toString());
				if(!properties.getProperty("veeva.stop.user.successemail").equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
					bMailSent = new Mail().sendEmailToUsers(context, sEmailFrom, sToEmailId, sbMessage.toString(), sbSubject.toString(), Veeva.EMPTY_STRING);
					logger.info("Email sent? "+String.valueOf(bMailSent));
					if(bMailSent) {
						logger.info(pgV3Constants.MAIL_SUCCESSFULLY_SENT);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Sending artwork success email failed to veeva document owner" + sToEmailId + "for document: " + strDocumentNumber
					+ " GCAS: " + strGcas + " with exception " + e.getMessage());
		}
	}
	
	
	/** 
	 * @about - Method return User list
	 * @param List<UsersEmail> - holds users emails
	 * @return String
	 * @since DSM 2018x.5
	 */
	public String getUserList(List<UsersEmail> usersEmailList){
		String sUserEmailIds = DomainConstants.EMPTY_STRING;
		try {
			String sAdditionalMails = properties.getProperty("veeva.additional.emails");
			StringBuilder sbUserEmails = new StringBuilder();
			int iUserEmailList = usersEmailList.size();
			if(properties.getProperty("veeva.testing.enable").equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
				sUserEmailIds = properties.getProperty("veeva.testing.email");
				logger.info("Send email to Tester "+sUserEmailIds);
			}else {
				UsersEmail userEmail;
				for (int j = 0; j < iUserEmailList; j++) {
					userEmail = usersEmailList.get(j);
					if (sbUserEmails.length() > 0)
						sbUserEmails.append(pgV3Constants.SYMBOL_COMMA);
					sbUserEmails.append(userEmail.getUserEmailV());
				}
				if(UIUtil.isNotNullAndNotEmpty(sAdditionalMails)) {
					sbUserEmails.append(pgV3Constants.SYMBOL_COMMA).append(sAdditionalMails);
				}
				sUserEmailIds = sbUserEmails.toString();
			}
			
		}catch (Exception e) {
			logger.error("Error in getToList method "+ e.getMessage());
		}
		return sUserEmailIds;
	}
	
	/** 
	 * @about - Method return Exception Message
	 * @param List<ArtworkErrorMessage> - holds artwork exceptions
	 * @return String
	 * @since DSM 2018x.5
	 */
	public String getExceptionMessage(List<ArtworkErrorMessage> allArtworkExceptionMessages) {
		StringBuilder sbErrorMessage = new StringBuilder();
		try {
			String strDocumentNumber;
			String strArtworkName;
			String strErrorMessage;
			String strCustomErrorMessage;
			int iAllArtworkMessageList = allArtworkExceptionMessages.size();
			for (int j = 0; j < iAllArtworkMessageList; j++) {
				ArtworkErrorMessage artworkError = allArtworkExceptionMessages.get(j);
				strDocumentNumber = artworkError.getDocumentNumber();
				strArtworkName = artworkError.getArtworkName();
				strErrorMessage = artworkError.getErrorMessage();
				strCustomErrorMessage = artworkError.getErrorCustomMessage();
                if(sbErrorMessage.length() == 0) {
					sbErrorMessage.append(pgV3Constants.SYMBOL_NEXT_LINE)
					.append(strDocumentNumber)
					.append(pgV3Constants.SYMBOL_SPACE)
				    .append(properties.getProperty("veeva.artwork.processing.failed.prefix"));
				    if(UIUtil.isNotNullAndNotEmpty(strArtworkName)) {
				    	sbErrorMessage.append(pgV3Constants.SYMBOL_SPACE).append(strArtworkName);
				    }
				    sbErrorMessage.append(pgV3Constants.SYMBOL_SPACE)
				    .append(properties.getProperty("veeva.artwork.processing.failed.suffix"))
				    .append(pgV3Constants.SYMBOL_SPACE)
					.append(properties.getProperty("veeva.artwork.exception.message"))
					.append(pgV3Constants.SYMBOL_NEXT_LINE);
                }
			    
			     sbErrorMessage.append(pgV3Constants.SYMBOL_NEXT_LINE)
				.append(pgV3Constants.SYMBOL_HYPHEN)
				.append(pgV3Constants.SYMBOL_SPACE)
				.append(strCustomErrorMessage)
			    .append(Veeva.CONST_SYMBOL_COLON)
			    .append(pgV3Constants.SYMBOL_SPACE)
			    .append(strErrorMessage);
				
			}
		}catch (Exception e) {
			logger.error("Error in getExceptionMessage method "+ e.getMessage());
		}
		return sbErrorMessage.toString();
	}
}
