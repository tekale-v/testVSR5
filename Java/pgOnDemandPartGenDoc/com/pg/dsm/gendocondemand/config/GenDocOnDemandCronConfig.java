/*Created by DSM for Requirement 47389 2022x.04 Dec CW 2023
 Purpose: Contains methods for on-demand GenDoc Generation cron configuration
*/
package com.pg.dsm.gendocondemand.config;


import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v3.custom.pgV3Util;
import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class GenDocOnDemandCronConfig{
	
	private static final String MESSAGE_BODY = "Hi,\n\nSAP BOM eDelivery cron job is stuck and is not executed properly for last 2 scheduled runs. Please look into the issue.\n\nThanks.";
	private static final String MESSAGE_SUBJECT = "SAP BOM eDelivery scheduled job problem";

	private GenDocOnDemandConfig config;
	private BusinessObject boGenDocConfig;
	private boolean isCronActive;
	private int iGenDocJobCount ;
	private PrintWriter out;
	
	public int getiGenDocJobCount() {
		return iGenDocJobCount;
	}

	public void setiGenDocJobCount(int iGenDocJobCount) {
		this.iGenDocJobCount = iGenDocJobCount;
	}

	public GenDocOnDemandCronConfig(GenDocOnDemandConfig conf) {
		this.config = conf;
	}
	
	public boolean isCronActive() {
		return isCronActive;
	}

	public void setCronActive(boolean isCronActive) {
		this.isCronActive = isCronActive;
	}

	public GenDocOnDemandCronConfig loadConfig(PrintWriter out) throws MatrixException {
		
		this.isCronActive = Boolean.FALSE;
		this.boGenDocConfig = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
				this.config.getProperties().getGenDocConfigurationObjectName(), "-", "eService Production");
		if(boGenDocConfig.exists(this.config.getContext())){
			try{
				boGenDocConfig.open(this.config.getContext());
				StringList slAttributes = new StringList(3);
				slAttributes.add(GenDocConstants.ATTRIBUTE_PGCONFIGISACTIVE);
				slAttributes.add(GenDocConstants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT);
				slAttributes.add(GenDocConstants.ATTRIBUTE_PGCONFIGCOMMONADMINMAILID);
				
				AttributeList attrList = boGenDocConfig.getAttributeValues(this.config.getContext(), slAttributes);
				AttributeItr attriItr = new AttributeItr(attrList);
				String strAttriName = null;
				String  strpgConfigCommonIsActive=null, strpgConfigCommonRetryCount=null,strpgConfigCommonAdminMailId=null;
				while (attriItr.next()) {
					Attribute attribute = attriItr.obj();
					strAttriName = attribute.getName().trim();
				
					if (GenDocConstants.ATTRIBUTE_PGCONFIGISACTIVE.equalsIgnoreCase(strAttriName)) {
						strpgConfigCommonIsActive = attribute.getValue().trim();
					} else if (GenDocConstants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT.equalsIgnoreCase(strAttriName)) {
						strpgConfigCommonRetryCount = attribute.getValue().trim();
					}else if (GenDocConstants.ATTRIBUTE_PGCONFIGCOMMONADMINMAILID.equalsIgnoreCase(strAttriName)) {
						strpgConfigCommonAdminMailId = attribute.getValue().trim();
					}
				}
				boolean isGenDocJobActive  = Boolean.parseBoolean(strpgConfigCommonIsActive);
				 iGenDocJobCount = Integer.parseInt(strpgConfigCommonRetryCount);
				this.setiGenDocJobCount(iGenDocJobCount);
				
				if(isGenDocJobActive){
					out.append("GenDoc generation cron is in Running state \n");
					this.isCronActive = Boolean.TRUE;
					out.flush();
					sendEmail(this.config.getContext(),strpgConfigCommonAdminMailId);					
				}
				
			} catch (Exception e) {
				this.isCronActive = Boolean.TRUE;
				out.append("GenDoc Cron Configuration .."+e.getMessage()+"\n");
			}
		}else{
			this.isCronActive = Boolean.TRUE;
			out.append("Business Object does not found for GenDocConfiguration Object \n");
			out.flush();		
		}
		return this;
		
	}

	/**
	 * @Desc The method will send the mail if the Cron is in running state for past 2 count or stuck.
	 * @param Context - context of Creator
	 * @param String - Email to whom we need to send  
	 * @return boolean - True/False for the mail sent status 
	 * @throws Exception
	 * @author Sogeti
	*/
	public boolean sendEmail(Context context, String toEmailId) throws Exception {
		boolean boolemailSent = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(toEmailId)) {
				String strEnvironment = this.config.getProperties().getGenDocRunningEnvironmentDetails();
				String subject = this.config.getProperties().getGenDocEmailSubject();
				String fromPersonalName = this.config.getProperties().getGenDocEmailFrom();
				String msgBody = this.config.getProperties().getGenDocEmailBody();
				String msgFooter = this.config.getProperties().getGenDocEmailFooter();
				String host = PropertyUtil.getEnvironmentProperty(context,"MX_SMTP_HOST");
				String fromEmailId = PersonUtil.getEmail(context,PropertyUtil.getSchemaProperty(null,"person_UserAgent"));
				
				subject =  subject + " "+ strEnvironment;
				msgBody = "Hi All,\n\n" 	+ msgBody + "\n\n" + msgFooter;
				String[] arguments = new String[7];
				arguments[0] = host;
				arguments[1] = fromEmailId;
				arguments[2] = toEmailId;
				arguments[3] = null;
				arguments[4] = subject;
				arguments[5] = "\n" + msgBody;
				arguments[6] = fromPersonalName;

				pgV3Util.sendEMailToUser(arguments);
				boolemailSent = true;
			} else {
				out.append("Send To email-id incorrect \n");
				throw new RuntimeException();
			}
		} catch (RuntimeException RunEx) {
			out.append("Error while sending mails"+RunEx.getMessage()+"\n");
			throw new Exception(RunEx.getMessage());
		}
		catch (Exception exception) {
			out.append("Error while sending mails"+exception.getMessage()+"\n");
			throw new Exception("GenDoc : exception while sending Email"+exception.getMessage());
		}
		return boolemailSent;
	} 
	
	/**
	/* This method will update configuration attributes value when cron job starts*/

	public void updateCronAttrOnStart() {
		try {
			GregorianCalendar timeForLogFile = new GregorianCalendar();
			SimpleDateFormat sdf_eMatrixDateFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
			String strCronStartTime = String.valueOf(sdf_eMatrixDateFormat.format(timeForLogFile.getTime()));
			AttributeList alConfigAttributeDetails = new AttributeList();
		    alConfigAttributeDetails.add(new Attribute(new AttributeType(GenDocConstants.ATTRIBUTE_PGCONFIGISACTIVE), "TRUE"));
		    alConfigAttributeDetails.add(new Attribute(new AttributeType(GenDocConstants.ATTRIBUTE_PGCONFIGCOMMONDSTARTEDDATE), strCronStartTime));
			alConfigAttributeDetails.add(new Attribute(new AttributeType(GenDocConstants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT), this.getiGenDocJobCount()+1+""));
			this.boGenDocConfig.setAttributeValues(this.config.getContext(), alConfigAttributeDetails);

		} catch (MatrixException e) {
			out.append("Error while update config attributes when cron starts"+e.getMessage()+"\n");
		}
	}

	/**
	 /* This method will update configuration attributes value when cron job ends*/
	 
	public void updateCronAttrOnEnd() {
		try {
			AttributeList alConfigAttributeDetailsObject = new AttributeList();
			alConfigAttributeDetailsObject .add(new Attribute(new AttributeType(GenDocConstants.ATTRIBUTE_PGCONFIGISACTIVE), "FALSE"));
			alConfigAttributeDetailsObject .add(new Attribute(new AttributeType(GenDocConstants.ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT),"0"));
		   	boGenDocConfig.setAttributeValues(this.config.getContext(), alConfigAttributeDetailsObject);
		} catch (MatrixException e) {
			out.append("Error while update config attributes when cron ends"+e.getMessage()+"\n");
		}
	}
}
