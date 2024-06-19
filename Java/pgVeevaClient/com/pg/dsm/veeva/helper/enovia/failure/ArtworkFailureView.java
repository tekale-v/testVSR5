/*
 **   ArtworkFailureView.java
 **   Description - Introduced as part of Veeva integration.      
 **   Artwork Failure View bean.
 **   DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
 */
package com.pg.dsm.veeva.helper.enovia.failure;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.v3.custom.pgV3Constants;
import com.pg.dsm.veeva.util.Mail;
import com.pg.dsm.veeva.util.Utility;
import org.apache.log4j.Logger;


import matrix.db.Context;

public class ArtworkFailureView extends FailureView {
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private String message;
	private String artworkID;
	private Context context;
	private String currentDate;
	private Configurator configurator;
	private String sToEmailId;
	private String sPMP;
	private String sDocumentNumber;
	private String sCcEmailId;
	
	public ArtworkFailureView(Configurator configurator, Context context, String message, String sDocumentNumber, String artworkID, String currentDate, String sToEmailId, String sPMP, String sCcEmailId) {
		this.context = context;
		this.message = message;
		this.artworkID = artworkID;
		this.currentDate = currentDate;
		this.configurator = configurator;
		this.sToEmailId = sToEmailId;
		this.sPMP = sPMP;
		this.sDocumentNumber = sDocumentNumber;
		this.sCcEmailId = sCcEmailId;
		
	}
	
	@Override
	public String getArtworkID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFailureMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log() {
		// TODO Auto-generated method stub
		logger.error("Document Number-"+sDocumentNumber+Veeva.SYMBOL_PIPE+"Artwork ID-"+artworkID+Veeva.SYMBOL_PIPE+"Validations Failed - Check Failed View");
		
	}
	
    /*
	 * This method is to send validation failure mail
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	@Override
	public void sendValidationErrorEmail() {
		
		logger.info("PMP >>"+sPMP);
		logger.info("To Email >>"+sToEmailId);
		logger.info("Email Body >>"+message);
		
		try {
			if(Utility.isNotNullEmpty(sPMP) && Utility.isNotNullEmpty(sToEmailId) && Utility.isNotNullEmpty(message)){
				
				String sEmailFrom = PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT);
				logger.info("From Email "+sEmailFrom);
				
				Properties veevaProperties = configurator.getProperties();
				String sMailSubject = veevaProperties.getProperty("veeva.artwork.failure.subject");
				sMailSubject = sMailSubject.replace("<DOC ID>", sDocumentNumber);
				sMailSubject = sMailSubject.replace("<PMP>", sPMP);
				
				logger.info("Email Subject "+sMailSubject);
				
				if(!veevaProperties.getProperty("veeva.stop.user.email").equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
					logger.info("Enable Failure Email to Actual user");
					if(veevaProperties.getProperty("veeva.testing.enable").equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
						sToEmailId = veevaProperties.getProperty("veeva.testing.email");
						logger.info("Send email to Tester "+sToEmailId);
					}
					boolean bMailSent = new Mail().sendEmailToUsers(context, sEmailFrom, sToEmailId, message, sMailSubject, Veeva.EMPTY_STRING);
					logger.info("Email sent? "+String.valueOf(bMailSent));
					if(bMailSent) {
						logger.info(pgV3Constants.MAIL_SUCCESSFULLY_SENT);
					}
				}
			}
		} catch (FrameworkException e) {
			logger.error("Unable to send mail about Artwork failure due to "+e.getMessage());	
		}
		
	}

	@Override
	public void update() throws Exception {
		// TODO Auto-generated method stub
		try {
			String classification = configurator.getProperties().getProperty("veeva.artwork.attribute.pgerrorclassification.default");
			if(message.length()!=0) {
				Map<String, String> attributes = new HashMap<String, String>();
				attributes.put(Veeva.ATTRIBUTE_PGFAILEDREASON, message);
				attributes.put(Veeva.ATTRIBUTE_PGERRORCLASSIFICATION, classification);
				attributes.put(Veeva.ATTRIBUTE_PGERRORDATE, currentDate);
				DomainObject dObjArt = DomainObject.newInstance(context,artworkID);
				dObjArt.setAttributeValues(context, attributes);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}	
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	* This method is to send artwork exception emails
	* DSM Added for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	*/
	@Override
	public void sendExceptionErrorEmail(){
		logger.info("sendExceptionErrorEmail --  PMP >>"+sPMP);
		logger.info("sendExceptionErrorEmail -- To Email >>"+sToEmailId);
		logger.info("sendExceptionErrorEmail -- Email Body >>"+message);
		try {
			if(Utility.isNotNullEmpty(sPMP) && Utility.isNotNullEmpty(sToEmailId) && Utility.isNotNullEmpty(message)){
				String sEmailFrom = PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT);
				logger.info("sendExceptionErrorEmail -- From Email "+sEmailFrom);
				
				Properties veevaProperties = configurator.getProperties();
				String sMailSubject = veevaProperties.getProperty("veeva.artWork.exception.subject");
				sMailSubject = sMailSubject.replace("<DOC ID>", sDocumentNumber);
				sMailSubject = sMailSubject.replace("<PMP>", sPMP);
				
				logger.info("sendExceptionErrorEmail -- Email Subject "+sMailSubject);
				
				if(!veevaProperties.getProperty("veeva.stop.user.exceptionemail").equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
					logger.info("Enable Failure Email to Actual user");
					if(veevaProperties.getProperty("veeva.testing.enable").equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE)) {
						sToEmailId = veevaProperties.getProperty("veeva.testing.email");
						sCcEmailId = Veeva.EMPTY_STRING;
						logger.info("Send email to Tester "+sToEmailId);
					}
					if(veevaProperties.getProperty("veeva.exceptionmail.cclist.disable").equalsIgnoreCase(pgV3Constants.KEY_YES_VALUE))
						sCcEmailId = Veeva.EMPTY_STRING;
					
					boolean bMailSent = new Mail().sendEmailToUsers(context, sEmailFrom, sToEmailId, message, sMailSubject, sCcEmailId);
					logger.info("Email sent? "+String.valueOf(bMailSent));
					if(bMailSent) {
						logger.info(pgV3Constants.MAIL_SUCCESSFULLY_SENT);
					}
				}	
			}
		} catch (Exception e) {
			logger.error("Unable to send mail about Artwork Exception due to "+e.getMessage());	
		}
		
		
	}
	

}
