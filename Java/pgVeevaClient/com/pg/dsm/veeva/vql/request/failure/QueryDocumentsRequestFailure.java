/*
 **   QueryDocumentsRequestFailure.java
 **   Description - Introduced as part of Veeva integration.      
 **   Query Documents Request Failure class
 **
 */
package com.pg.dsm.veeva.vql.request.failure;

import java.util.Properties;

import org.apache.log4j.Logger;


import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.config.VeevaConfig;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.util.Mail;
import com.pg.dsm.veeva.vql.json.Response;

import matrix.db.Context;

public class QueryDocumentsRequestFailure extends HTTPRequestFailure implements Veeva {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private String operation; 
	private Exception exception;
	Configurator configurator;
	private Response response;
		
	public QueryDocumentsRequestFailure(String operation, Exception exception, Configurator configurator, Response response) {
		this.operation = operation;
		this.exception = exception;
		this.configurator = configurator;
		this.response = response;
	}

	@Override
	public void update() throws Exception {
		
		VeevaConfig matrixVeevaConfig = configurator.getVeevaConfig();
		Context context = configurator.getContext();
		
		logger.info("isActive--:"+matrixVeevaConfig.getAttrConfigActive());
		logger.info("retry--:"+matrixVeevaConfig.getAttrRetryCount());
		
		//roll-back start/end date
		matrixVeevaConfig.rollbackStartDate();
		logger.info("Rolled back Start date from|"+matrixVeevaConfig.getNextStartDate()+"|to|"+matrixVeevaConfig.getAttrStartDate());
		matrixVeevaConfig.rollbackEndDate();
		logger.info("Rolled back End date from|"+matrixVeevaConfig.getNextEndDate()+"|to|"+matrixVeevaConfig.getAttrEndDate());
		
		// increment retry count and set it.
		int counter = Integer.parseInt(matrixVeevaConfig.getAttrRetryCount());
		matrixVeevaConfig.getBusObj().setAttributeValue(context, ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT, String.valueOf(Integer.toString(counter++)));
		boolean isCronActive=Boolean.TRUE;
		String isConfigActive = matrixVeevaConfig.getAttrConfigActive();
		isCronActive = Boolean.parseBoolean(isConfigActive);
		if(isCronActive){
			if(counter>2) {
				sendEmail();
			}
		}
	}

	@Override
	public void log() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEmail() {
		try {
			Properties veevaProperties = configurator.getProperties();
			Context context = configurator.getContext();
			Mail sendEmail =new Mail();

			String strMessage =  veevaProperties.getProperty("veeva.cron.Failure.Message");
			String sMailSubject = veevaProperties.getProperty("veeva.cron.Failure.Subject");
			String sConfigCommonAdminMailId = configurator.getProperties().getProperty("veeva.configobject.mailid");
			String sEmailFrom	= PersonUtil.getEmail(context, PERSON_USER_AGENT);
			StringBuffer sbMailBody	= new StringBuffer(Veeva.MAIL_START).append(Veeva.SYMBOL_NEXT_LINE).
					append(Veeva.SYMBOL_NEXT_LINE).append(strMessage).append(Veeva.SYMBOL_NEXT_LINE).
					append(Veeva.SYMBOL_NEXT_LINE).append(Veeva.MAIL_END);
            //DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
			boolean bMailSent = sendEmail.sendEmailToUsers(context, sEmailFrom, sConfigCommonAdminMailId, sbMailBody.toString(), sMailSubject, Veeva.EMPTY_STRING);
			//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
			if(bMailSent){
				logger.info(Veeva.MAIL_SUCCESSFULLY_SENT +Veeva.SYMBOL_NEXT_LINE);
			}			
		} catch (FrameworkException e) {
			logger.error("Unable to send mail about cron job failure due to "+e.getMessage());			
		}	
	}

	@Override
	public String getOperation() {
		// TODO Auto-generated method stub
		return this.operation;
	}

	@Override
	public Exception getException() {
		// TODO Auto-generated method stub
		return this.exception;
	}
	@Override
	public Response getResponse() {
		// TODO Auto-generated method stub
		return this.response;
	}
}

