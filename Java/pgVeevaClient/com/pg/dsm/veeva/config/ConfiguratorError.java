/*
 **   ConfiguratorError.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to load all error while loading resources bean and notify users.
 **
 */
package com.pg.dsm.veeva.config;

import java.util.Properties;

import org.apache.log4j.Logger;


import com.pg.dsm.veeva.util.Veeva;
import com.pg.v3.custom.pgV3Constants;
import com.pg.dsm.veeva.util.Mail;
import com.pg.dsm.veeva.util.Utility;

import matrix.db.Context;

public class ConfiguratorError {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	Context context;
	VeevaConfig veevaConfig;
	Properties properties;
	String emailBody;
	String emailSubject;
	String emailToAddress;
	String emailFromAddress;
	
	/** 
	 * @about - Private Constructor
	 * @param - Builder - builder object
	 * @since DSM 2018x.3
	 */
	private ConfiguratorError(Builder builder) {
		this.context = builder.context;
		this.veevaConfig = builder.veevaConfig;
		this.properties = builder.properties;
		this.emailBody = builder.emailBody;
		this.emailSubject = builder.emailSubject;
		this.emailToAddress = builder.emailToAddress;
		this.emailFromAddress = builder.emailFromAddress;
	}
	/** 
	 * @about Method to notify user on load failure.
	 * @return void
	 * @throws Exception
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void notifyUserOnConfiguratorLoadFailure() {
		try {
			logger.info("from Email >> "+emailFromAddress);
			logger.info("to Email >> "+emailToAddress);
			logger.info("body Email >> "+emailBody);
			logger.info("body Subject >> "+emailSubject);
			
			StringBuilder sbMailBody	= new StringBuilder(pgV3Constants.MAIL_START).append(pgV3Constants.SYMBOL_NEXT_LINE).
					append(pgV3Constants.SYMBOL_NEXT_LINE).append(emailBody).append(pgV3Constants.SYMBOL_NEXT_LINE).
					append(pgV3Constants.SYMBOL_NEXT_LINE).append(pgV3Constants.MAIL_END);

			if(Utility.isNotNullEmpty(emailFromAddress) && Utility.isNotNullEmpty(emailToAddress)){
				boolean bMailSent = new Mail().sendEmailToUsers(context, emailFromAddress, emailToAddress, sbMailBody.toString(), emailSubject, Veeva.EMPTY_STRING);
				logger.info("Mail sent? "+String.valueOf(bMailSent));
				if(bMailSent){
					logger.info(pgV3Constants.MAIL_SUCCESSFULLY_SENT +pgV3Constants.SYMBOL_NEXT_LINE);
				} else {
					logger.info("Unable to send email");
				}
			}
		} catch(Exception e) {
			logger.error("Error occured while notifying user."+e);
		}	
	}
	public static class Builder {
		Context context;
		VeevaConfig veevaConfig;
		Properties properties;
		String emailBody;
		String emailSubject;
		String emailToAddress;
		String emailFromAddress;
		/** 
		 * @about - Constructor
		 * @since DSM 2018x.3
		 */
		public Builder() {
		}
		/** 
		 * @about Setter method - Set context 
		 * @param Context - matrix context
		 * @return Builder  
		 * @since DSM 2018x.3
		 */
		public Builder setContext(Context context) {
			this.context = context;
			return this;
		}
		/** 
		 * @about Setter method - set properties object. 
		 * @param Properties - properties obj
		 * @return Builder  
		 * @since DSM 2018x.3
		 */
		public Builder setProperties(Properties properties) {
			this.properties = properties;
			return this;
		}
		/** 
		 * @about Setter method - set matrix-veeva config object. 
		 * @param VeevaConfig - veevaConfig
		 * @return Builder  
		 * @since DSM 2018x.3
		 */
		public Builder setVeevaConfig(VeevaConfig veevaConfig) {
			this.veevaConfig = veevaConfig;
			return this;
		}
		/** 
		 * @about Setter method - set email body message
		 * @param String - email body message
		 * @return Builder  
		 * @since DSM 2018x.3
		 */
		public Builder setEmailBody(String emailBody) {
			this.emailBody = emailBody;
			return this;
		}
		/** 
		 * @about Setter method - set email subject message
		 * @param String - email subject message string
		 * @return Builder  
		 * @since DSM 2018x.3
		 */
		public Builder setEmailSubject(String emailSubject) {
			this.emailSubject = emailSubject;
			return this;
		}

		/** 
		 * @about Setter method - set to email address
		 * @param String - email address
		 * @return Builder  
		 * @since DSM 2018x.3
		 */
		public Builder setEmailToAddress(String emailToAddress) {
			this.emailToAddress = emailToAddress;
			return this;
		}
		/** 
		 * @about Setter method - set from email address
		 * @param String - email address
		 * @return Builder  
		 * @since DSM 2018x.3
		 */
		public Builder setEmailFromAddress(String emailFromAddress) {
			this.emailFromAddress = emailFromAddress;
			return this;
		}
		/** 
		 * @about Builder method
		 * @return ConfiguratorError  
		 * @since DSM 2018x.3
		 */
		public ConfiguratorError build() {
			return new ConfiguratorError(this);
		}
	}
}
