package com.pg.dsm.rollup_event.common.util;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.pg.dsm.rollup_event.common.config.ConfigBean;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.MxMessageSupport;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ConfigUtil {
    private static final Logger logger = Logger.getLogger(ConfigUtil.class.getName());

    private ConfigUtil() {
    }

    public static String getObjectId(Context context, String configObjectName) {
        String objectOid = DomainConstants.EMPTY_STRING;
        try {
            BusinessObject configBusinessObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, configObjectName, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
            objectOid = configBusinessObject.getObjectId(context);
        } catch (MatrixException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return objectOid;
    }

    public static Map<Object, Object> getObjectInfo(Context context, String objectOid) {
        Map<Object, Object> infoMap = null;
        StringList busSelectList = new StringList(3);
        busSelectList.addElement(DomainConstants.SELECT_ID);
        busSelectList.addElement(DomainConstants.SELECT_TYPE);
        busSelectList.addElement(DomainConstants.SELECT_NAME);
        busSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGISACTIVE);
        busSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT);
        busSelectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONADMINMAILID);
        try {
            DomainObject dObj = DomainObject.newInstance(context, objectOid);
            infoMap = dObj.getInfo(context, busSelectList);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return infoMap;
    }

    public static void resetConfigObject(Context context, ConfigBean configBean, boolean isCronExecuted) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(System.currentTimeMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
        String sCronStartTime = simpleDateFormat.format(gregorianCalendar.getTime());

        Map<Object, Object> configAttributeMap = new HashMap<>();

        if (isCronExecuted) {
            configAttributeMap.put(pgV3Constants.ATTRIBUTE_PGCONFIGISACTIVE, String.valueOf(Boolean.FALSE));
            configAttributeMap.put(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT, pgV3Constants.ZERO);
        } else {
            configAttributeMap.put(pgV3Constants.ATTRIBUTE_PGCONFIGISACTIVE, String.valueOf(Boolean.TRUE));
            configAttributeMap.put(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONDSTARTEDDATE, sCronStartTime);
            configAttributeMap.put(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT, pgV3Constants.ONE);
        }
        try {
            DomainObject dObj = DomainObject.newInstance(context, configBean.getId());
            dObj.setAttributeValues(context, configAttributeMap);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    public static boolean updateRetryCountAndNotify(Context context, ConfigBean configBean, String emailMessage, String emailSubject) {
        boolean bIsUpdatedAndNotified = true;
        int retryCount = configBean.getRetryCount();
        retryCount++;
        try {
            DomainObject dObj = DomainObject.newInstance(context, configBean.getId());
            dObj.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT, String.valueOf(retryCount));

            if (retryCount > 2) {
                bIsUpdatedAndNotified = ConfigUtil.sendEmail(
                        context,
                        configBean.getAdminEmail(),
                        emailMessage,
                        emailSubject);
            }

        } catch (FrameworkException e) {
            bIsUpdatedAndNotified = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bIsUpdatedAndNotified;
    }

    public static boolean sendEmail(Context context, String toEmail, String emailBody, String emailSubject) {
        boolean bMailSent = true;
        try {
            MxMessageSupport msgSupport = new MxMessageSupport();
            msgSupport.getSendMailInfo(context);
            Properties prop = new Properties();

            prop.put("mail.smtp.host", msgSupport.getSmtpHost());
            Session session = Session.getDefaultInstance(prop, null);

            Message msg = new MimeMessage(session);
            // from email
            InternetAddress fromUserInternetAddress = new InternetAddress(PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT));

            msg.setFrom(fromUserInternetAddress);
            msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(emailSubject);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(emailBody);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            msg.setContent(multipart);

            javax.mail.Transport.send(msg);

        } catch (Exception e) {
            bMailSent = false;
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return bMailSent;
    }

    public static String getEmailMessageBody(String message) {
        StringBuilder emailMessageBody = new StringBuilder(pgV3Constants.MAIL_START).append(pgV3Constants.SYMBOL_NEXT_LINE).append(pgV3Constants.SYMBOL_NEXT_LINE).append(message).append(pgV3Constants.SYMBOL_NEXT_LINE).append(pgV3Constants.SYMBOL_NEXT_LINE).append(pgV3Constants.MAIL_END);
        return emailMessageBody.toString();
    }
}
