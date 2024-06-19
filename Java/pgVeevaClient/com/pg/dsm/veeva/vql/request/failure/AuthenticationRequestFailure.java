/*
 **   AuthenticationRequestFailure.java
 **   Description - Introduced as part of Veeva integration.      
 **   (jackson bean) 
 **
 */
package com.pg.dsm.veeva.vql.request.failure;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.config.VeevaConfig;
import com.pg.dsm.veeva.util.Mail;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.json.Response;
import com.pg.dsm.veeva.vql.xml.binder.Error;

import matrix.db.Context;

public class AuthenticationRequestFailure extends HTTPRequestFailure implements Veeva {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private String operation; 
	private Exception exception;
	Configurator configurator;
	private Response response;
		
	public AuthenticationRequestFailure(String operation, Exception exception, Configurator configurator, Response response) {
		this.operation = operation;
		this.exception = exception;
		this.configurator = configurator;
		this.response = response;
	}
	
	@Override
	public String getOperation() {
		// TODO Auto-generated method stub
		return this.operation;
	}

	@Override
	public Response getResponse() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void sendEmail() {
		try {
			List<Error> codeList = configurator.getVeevaConfigXML().getErrors().get(0).getError();
			Context context = configurator.getContext();
			String sConfigCommonAdminMailId = configurator.getProperties().getProperty("veeva.configobject.mailid");
			String sEmailFrom = PersonUtil.getEmail(context, Veeva.PERSON_USER_AGENT);
			Mail sendEmail = new Mail();
			Iterator<?> itr = 	codeList.iterator();			
			while(itr.hasNext()) {
				Error error = (Error)itr.next();
				String name = error.getCode();
				String sMailSubject = error.getSubject();
				String sMailBody = error.getBody();
				String errorType = response.getType();
				if(errorType.equals(name)) {
				//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
					boolean bMailSent =sendEmail.sendEmailToUsers(context, sEmailFrom, sConfigCommonAdminMailId, sMailBody, sMailSubject, Veeva.EMPTY_STRING);
			    //DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
					if(bMailSent) {
						logger.info("INFO: "+Veeva.MAIL_SUCCESSFULLY_SENT +Veeva.SYMBOL_NEXT_LINE);
					}
					break;
				}
			}
		} catch (FrameworkException e) {
			logger.error("Error in Sendig Mail"+e.getMessage());
		}
	}
	
	@Override
	public void update() throws Exception {
		try {
			VeevaConfig matrixVeevaConfig = configurator.getVeevaConfig();
			Properties veevaProperties = configurator.getProperties();
			Context context = configurator.getContext();
			Mail sendEmail =new Mail();

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

			String strMessage =  veevaProperties.getProperty("veeva.cron.Failure.Message");
			String sMailSubject = veevaProperties.getProperty("veeva.cron.Failure.Subject");
			String sConfigCommonAdminMailId = configurator.getProperties().getProperty("veeva.configobject.mailid");

			String sEmailFrom	= PersonUtil.getEmail(context, PERSON_USER_AGENT);
			StringBuffer sbMailBody	= new StringBuffer(Veeva.MAIL_START).append(Veeva.SYMBOL_NEXT_LINE).
					append(Veeva.SYMBOL_NEXT_LINE).append(strMessage).append(Veeva.SYMBOL_NEXT_LINE).
					append(Veeva.SYMBOL_NEXT_LINE).append(Veeva.MAIL_END);		
			if(isCronActive){
				if(counter>2) {
				//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
					boolean bMailSent = sendEmail.sendEmailToUsers(context, sEmailFrom, sConfigCommonAdminMailId, sbMailBody.toString(), sMailSubject, Veeva.EMPTY_STRING);
				//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends
					if(bMailSent){
						logger.info(Veeva.MAIL_SUCCESSFULLY_SENT +Veeva.SYMBOL_NEXT_LINE);
					}
				}
			}
		} catch (FrameworkException e) {
			logger.error("Unable to send mail about cron job failure due to "+e.getMessage());			
		}			
	}

	@Override
	public Exception getException() {
		// TODO Auto-generated method stub
		return this.exception;
	}

	@Override
	public void log() {
		// TODO Auto-generated method stub
		
	}


}
