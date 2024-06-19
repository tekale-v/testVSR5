package com.pg.dsm.sapview.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.MxMessageSupport;
import matrix.util.MatrixException;
import matrix.util.StringList;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 *
 */
public class SAPCronConfig {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String MESSAGE_BODY = "Hi,\n\nSAP BOM eDelivery cron job is stuck and is not executed properly for last 2 scheduled runs. Please look into the issue.\n\nThanks.";
	private static final String MESSAGE_SUBJECT = "SAP BOM eDelivery scheduled job problem";

	private SAPConfig config;
	private BusinessObject boConfig;
	private boolean isCronActive;

	/**
	 * @param conf
	 */
	public SAPCronConfig(SAPConfig conf) {
		this.config = conf;
	}

	/**
	 * @return the isCronActive
	 */
	public boolean isCronActive() {
		return isCronActive;
	}

	/**
	 * @param isCronActive the isCronActive to set
	 */
	public void setCronActive(boolean isCronActive) {
		this.isCronActive = isCronActive;
	}

	/**
	 * @return
	 */
	public SAPCronConfig loadConfig() {

		this.isCronActive = Boolean.FALSE;
		try {
			this.boConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
					SAPViewConstant.CONFIG_OBJECT_NAME.getValue(), "-", "eService Production");
			String strSAPIsactive = "";
			String costryCount = "";
			String toEmailID = "";
			if (this.boConfig.exists(this.config.getContext())) {
				StringList slattribute = new StringList(3);
				slattribute.addElement(SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONISACTIVE);
				slattribute.addElement(SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONRETRYCOUNT);
				slattribute.addElement(SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONADMINMAILID);

				AttributeList attlist = this.boConfig.getAttributeValues(this.config.getContext(), slattribute);
				AttributeItr attrItr = new AttributeItr(attlist);
				while (attrItr.next()) {
					Attribute attribute = attrItr.obj();
					String attrName = attribute.getName().trim();
					if (SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONISACTIVE.equalsIgnoreCase(attrName)) {
						strSAPIsactive = attribute.getValue().trim();
					} else if (SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONRETRYCOUNT.equalsIgnoreCase(attrName)) {
						costryCount = attribute.getValue().trim();
					} else if (SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONADMINMAILID.equalsIgnoreCase(attrName)) {
						toEmailID = attribute.getValue().trim();
					}
				}
				if (pgV3Constants.CONST_TRUE.equalsIgnoreCase(strSAPIsactive)) {
					int count = Integer.parseInt(costryCount);
					count++;
					costryCount = Integer.toString(count);

					String stFormEmail = PersonUtil.getEmail(this.config.getContext());

					this.boConfig.setAttributeValue(this.config.getContext(),
							SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONRETRYCOUNT, costryCount);
					if (count > 2) {

						boolean emailStatus = sendEmail(stFormEmail, toEmailID, MESSAGE_BODY, MESSAGE_SUBJECT);
						if (!emailStatus) {
							logger.log(Level.WARNING, "Failed to sent cron failuer mail..");
						}
					}
					this.isCronActive = Boolean.TRUE;
				}
			} else {
				this.isCronActive = Boolean.TRUE;
			}
		} catch (Exception e) {
			this.isCronActive = Boolean.TRUE;
			logger.log(Level.WARNING, "SAP Cron Configuration obejct causing issue..", e);
		}
		return this;
	}

	/**
	 * This Method is called to send email to inform existing COS JOB still running
	 * after 5 re-try.
	 * 
	 * @param context
	 * @param fromUserEmailAddress
	 * @param userEmailAddress
	 * @param message
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public boolean sendEmail(String fromUserEmailAddress, String userEmailAddress, String message, String subject)
			throws Exception {
		boolean debug = true;
		try {

			MxMessageSupport support = new MxMessageSupport();
			support.getSendMailInfo(this.config.getContext());
			Properties props = new Properties();

			props.put("mail.smtp.host", support.getSmtpHost());
			Session session = Session.getDefaultInstance(props, null);

			Message msg = new MimeMessage(session);
			InternetAddress addressFrom = new InternetAddress(fromUserEmailAddress);
			msg.setFrom(addressFrom);
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmailAddress));

			msg.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			// Fill the message
			messageBodyPart.setText(message);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// Put parts in message
			msg.setContent(multipart);
			javax.mail.Transport.send(msg);
		} catch (Exception ex) {
			debug = false;
			logger.log(Level.WARNING, null, ex);
		}
		return debug;
	}

	/**
	 * 
	 */
	public void updateCronAttrOnStart() {
		try {
			Date today = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			String strToday = formatter.format(today);
			AttributeList attributes = new AttributeList(1);
			attributes
					.addElement(new Attribute(new AttributeType(SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONISACTIVE),
							pgV3Constants.CONST_TRUE));
			attributes.addElement(
					new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_ATTR), strToday));
			attributes.addElement(
					new Attribute(new AttributeType(SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONRETRYCOUNT), "1"));
			this.boConfig.setAttributeValues(this.config.getContext(), attributes);

		} catch (MatrixException e) {
			logger.log(Level.WARNING, null, e);
		}
	}

	/**
	 * 
	 */
	public void updateCronAttrOnEnd() {
		try {
			AttributeList attributes = new AttributeList(1);
			attributes.addElement(
					new Attribute(new AttributeType(SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONISACTIVE), "FALSE"));
			attributes.addElement(
					new Attribute(new AttributeType(SAPConstants.ATTRIBUTE_PGCONFIGCOMMON_SAPCRONRETRYCOUNT), "0"));
			boConfig.setAttributeValues(this.config.getContext(), attributes);
		} catch (MatrixException e) {
			logger.log(Level.WARNING, null, e);
		}
	}
}
