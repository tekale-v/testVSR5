/*
 **   VeevaVaultError.java
 **   Description - Introduced as part of Veeva integration.      
 **   Contains method to notify users on Veeva extraction failure. 
 **
 */
package com.pg.dsm.veeva;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.config.VeevaConfig;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.util.Mail;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.vql.json.Response;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class VeevaVaultError {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	Configurator configurator;
	Context context;
	Properties properties;
	VeevaConfig veevaConfig;
	String sConfigCommonAdminMailId;


	/** 
	 * @about - Constructor
	 * @param - Configurator - configurator object
	 * @since DSM 2018x.3
	 */
	public VeevaVaultError(Configurator configurator) {
		this.configurator = configurator;
		this.context = configurator.getContext();
		this.properties = configurator.getProperties();
		this.veevaConfig = configurator.getVeevaConfig();
		this.sConfigCommonAdminMailId = this.properties.getProperty("veeva.configobject.mailid");
	}

	/** 
	 * @about Method to notify user on multiple request failure
	 * @param Map - failure response messages
	 * @return void - nothing
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 */
	public void notifyOnMultipleVQLRequestFailure(Map<String, List<Response>> failureResponseMap) throws FrameworkException {
		logger.info("Enter notifyOnMultipleVQLRequestFailure");
		StringBuilder builder = new StringBuilder();		
		for(Entry<String, List<Response>> entry: failureResponseMap.entrySet()) {
			String key = entry.getKey();
			String[] keyArray = key.split(Veeva.SYMBOL_UNDERSCORE);
			String docID = keyArray[0];
			String docNumber = keyArray[1];

			List<Response> responseList =  entry.getValue();
			Iterator<?> itr = responseList.iterator();
			while(itr.hasNext()) {
				Response responseObj = (Response)itr.next();
				String errorCode = responseObj.getType();
				String errorMessage = responseObj.getMessage();

				builder.append(docID);
				builder.append(Veeva.SYMBOL_HYPHEN);
				builder.append(docNumber);
				builder.append(Veeva.SYMBOL_HYPHEN);
				builder.append(errorCode);
				builder.append(Veeva.SYMBOL_HYPHEN);
				builder.append(errorMessage);
				builder.append(Veeva.SYMBOL_NEXT_LINE);
			}

		}
		StringBuffer message = new StringBuffer(properties.getProperty("veeva.vql.multiple.request.failure.email.message.body"));
		message.append(Veeva.SYMBOL_NEXT_LINE);
		message.append(builder.toString());
		notifyUser(context, 
				PersonUtil.getEmail(context, Veeva.PERSON_USER_AGENT), 
				properties.getProperty("veeva.configobject.mailid"), 
				properties.getProperty("veeva.vql.multiple.request.failure.email.message.subject"),
				message.toString());	

		logger.info("Exit notifyOnMultipleVQLRequestFailure");
	}
	/** 
	 * @about Method to roll back start/end date on Matrix-veeva config object and notify user.
	 * @return void - nothing
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void updateOnException() {
			logger.info("Enter updateOnException");
			try {
				//roll-back start/end date
				veevaConfig.rollbackStartDate();
				logger.info(" Rolled back Start date from|"+veevaConfig.getNextStartDate()+"|to|"+veevaConfig.getAttrStartDate());
				veevaConfig.rollbackEndDate();
				logger.info(" Rolled back End date from|"+veevaConfig.getNextEndDate()+"|to|"+veevaConfig.getAttrEndDate());

				// increment retry count and set it.
				int counter = Integer.parseInt(veevaConfig.getAttrRetryCount());
				veevaConfig.getBusObj().setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT, String.valueOf(Integer.toString(counter++)));
				logger.info("Retry count updated >>"+String.valueOf(counter));

				String isConfigActive = veevaConfig.getAttrConfigActive();
				boolean isCronActive = Boolean.parseBoolean(isConfigActive);
				
				String strMessage =  properties.getProperty("veeva.cron.Failure.Message");
				String sMailSubject = properties.getProperty("veeva.cron.Failure.Subject");

				String sEmailFrom	= PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT);
				StringBuilder sbMailBody	= new StringBuilder(pgV3Constants.MAIL_START).append(pgV3Constants.SYMBOL_NEXT_LINE).
						append(pgV3Constants.SYMBOL_NEXT_LINE).append(strMessage).append(pgV3Constants.SYMBOL_NEXT_LINE).
						append(pgV3Constants.SYMBOL_NEXT_LINE).append(pgV3Constants.MAIL_END);			
						
						if(isCronActive && counter>2){
							boolean bMailSent = new Mail().sendEmailToUsers(context, sEmailFrom, sConfigCommonAdminMailId, sbMailBody.toString(), sMailSubject, Veeva.EMPTY_STRING);
							if(bMailSent){
								logger.info(" Exception occured - email sent");
							} else {
								logger.error("Unable to send email - General Exception");
							}
						}
			} catch (FrameworkException e) {
				logger.error("General Exception"+e.getMessage());			
			}
		logger.info("Exit updateOnException");
	} 
	/** 
	 * @about Method to notify user on authentication bean initialization failure.
	 * @return void - nothing
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void notifyOnAuthenticationBeanInitializationFailure() {
			logger.info("Enter notifyOnAuthenticationBeanInitializationFailure");
			try {
				//roll-back start/end date
				veevaConfig.rollbackStartDate();
				logger.info("Rolled back Start date from|"+veevaConfig.getNextStartDate()+"|to|"+veevaConfig.getAttrStartDate());
				veevaConfig.rollbackEndDate();
				logger.info("Rolled back End date from|"+veevaConfig.getNextEndDate()+"|to|"+veevaConfig.getAttrEndDate());

				// increment retry count and set it.
				int counter = Integer.parseInt(veevaConfig.getAttrRetryCount());
				veevaConfig.getBusObj().setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT, String.valueOf(Integer.toString(counter++)));
				
				String strMessage =  properties.getProperty("veeva.authentication.bean.initialization.failed.subject");
				String sMailSubject = properties.getProperty("veeva.authentication.bean.initialization.failed.message");

				String sEmailFrom	= PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT);
				StringBuilder sbMailBody	= new StringBuilder(pgV3Constants.MAIL_START).append(pgV3Constants.SYMBOL_NEXT_LINE).
						append(pgV3Constants.SYMBOL_NEXT_LINE).append(strMessage).append(pgV3Constants.SYMBOL_NEXT_LINE).
						append(pgV3Constants.SYMBOL_NEXT_LINE).append(pgV3Constants.MAIL_END);		

						boolean bMailSent = new Mail().sendEmailToUsers(context, sEmailFrom, sConfigCommonAdminMailId, sbMailBody.toString(), sMailSubject, Veeva.EMPTY_STRING);
						if(bMailSent){
							logger.info("Exception occured - email sent");
						} else {
							logger.error("Unable to send email - Veeva Authentication Failure");
						}
			} catch (FrameworkException e) {
				logger.error("Unable to send mail about cron job failure due to "+e.getMessage());			
			}
		logger.info("Exit notifyOnAuthenticationBeanInitializationFailure");
	} 
	/** 
	 * @about Method to notify user on authentication session invalid.
	 * @return void - nothing
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void notifyOnAuthenticationInValidSessionID() {
			logger.info("Enter notifyOnAuthenticationInValidSessionID");
			try {
				//roll-back start/end date
				veevaConfig.rollbackStartDate();
				logger.info("Rolled back Start date from|"+veevaConfig.getNextStartDate()+"|to|"+veevaConfig.getAttrStartDate());
				veevaConfig.rollbackEndDate();
				logger.info("Rolled back End date from|"+veevaConfig.getNextEndDate()+"|to|"+veevaConfig.getAttrEndDate());

				// increment retry count and set it.
				int counter = Integer.parseInt(veevaConfig.getAttrRetryCount());
				veevaConfig.getBusObj().setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT, String.valueOf(Integer.toString(counter++)));
				
				String strMessage =  properties.getProperty("veeva.authentication.bean.initialization.failed.subject");
				String sMailSubject = properties.getProperty("veeva.authentication.bean.initialization.failed.message");

				String sEmailFrom	= PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT);
				StringBuilder sbMailBody	= new StringBuilder(pgV3Constants.MAIL_START).append(pgV3Constants.SYMBOL_NEXT_LINE).
						append(pgV3Constants.SYMBOL_NEXT_LINE).append(strMessage).append(pgV3Constants.SYMBOL_NEXT_LINE).
						append(pgV3Constants.SYMBOL_NEXT_LINE).append(pgV3Constants.MAIL_END);			
						
						boolean bMailSent = new Mail().sendEmailToUsers(context, sEmailFrom, sConfigCommonAdminMailId, sbMailBody.toString(), sMailSubject, Veeva.EMPTY_STRING);
						if(bMailSent){
							logger.info("Exception occured - email sent");
						} else {
							logger.error("Unable to send email - Veeva Invalid Session ID");
						}

			} catch (FrameworkException e) {
				logger.error("Veeva Invalid Session Error"+e.getMessage());			
			}	
		logger.info("Exit notifyOnAuthenticationInValidSessionID");
	}
	/** 
	 * @about Common method to send notification email
	 * @param Context - matrix context
	 * @param String - from email address
	 * @param String - to email address
	 * @param String - message body string
	 * @return void - nothing
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void notifyUser(Context context, String fromEmail, String adminEmail, String subjectEmail, String bodyEmail) {
		try {

			logger.info("from Email >> "+fromEmail);
			logger.info("to Email >> "+adminEmail);
			logger.info("body Email >> "+bodyEmail);
			logger.info("body Subject >> "+subjectEmail);
			if(Utility.isNotNullEmpty(fromEmail) && Utility.isNotNullEmpty(adminEmail)){
				boolean bMailSent = new Mail().sendEmailToUsers(context, fromEmail, adminEmail, bodyEmail, subjectEmail, Veeva.EMPTY_STRING);
				logger.info("Mail sent? "+String.valueOf(bMailSent));
				if(bMailSent){
					logger.info(pgV3Constants.MAIL_SUCCESSFULLY_SENT +pgV3Constants.SYMBOL_NEXT_LINE);
				} else {
					logger.info("Unable to send email");
				}
			}
		}catch(Exception e) {
			logger.error("Error occured while notifying user.");
		}	
	}

 
}
