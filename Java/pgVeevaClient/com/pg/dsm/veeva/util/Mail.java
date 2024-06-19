/*
 **   Mail.java
 **   Description - Introduced as part of Veeva integration.      
 **   Utility class to send email
 **
 */
package com.pg.dsm.veeva.util;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import matrix.db.Context;
import matrix.db.MxMessageSupport;

import org.apache.log4j.Logger;


import com.pg.dsm.veeva.util.Veeva;

public class Mail {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public boolean sendEmailToUsers(Context context, String sEmailFrom,
			String sConfigCommonAdminMailId, String sMailBody, String sMailSubject, String sCCList) {
		boolean bMailSent = true;
		
		logger.info("Enter Mail sendEmailToUsers >>");
		logger.info("From Email: "+sEmailFrom);
		logger.info("To Admin Email: "+sConfigCommonAdminMailId);
		logger.info("Email Subject: "+sMailSubject);
		logger.info("Email Message: "+sMailBody);
		logger.info("CC List: "+sCCList);
		try {
			MxMessageSupport msgSupport = new MxMessageSupport();
			msgSupport.getSendMailInfo(context);
			
			logger.info("SMTP HOST: "+msgSupport.getSmtpHost());
			
			Properties props = new Properties();

			props.put("mail.smtp.host", msgSupport.getSmtpHost());
			Session session = Session.getDefaultInstance(props, null);

			Message msg = new MimeMessage(session);
			InternetAddress fromUserInternetAddress = new InternetAddress(sEmailFrom);

			msg.setFrom(fromUserInternetAddress);
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(sConfigCommonAdminMailId));
		    //DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
			msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse(sCCList));
	        //DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends	
			msg.setSubject(sMailSubject);

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(sMailBody);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			msg.setContent(multipart);

			javax.mail.Transport.send(msg);
			
			logger.info("Email Sent ! "+String.valueOf(bMailSent));

		} catch (Exception ex) {
			bMailSent = false;
			logger.error(Veeva.SENDING_MAIL_FAILED + ex.getMessage() +Veeva.SYMBOL_NEXT_LINE);
		}
		logger.info("Exit Mail sendEmailToUsers >>");
		return bMailSent;
	}
}
