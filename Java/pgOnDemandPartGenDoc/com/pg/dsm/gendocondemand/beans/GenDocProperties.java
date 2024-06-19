/*Created by DSM for Requirement 47389 2022x.04 Dec CW 2023
 Purpose: Setters and Getters for on-demand GenDoc Generation properties values
*/
package com.pg.dsm.gendocondemand.beans;

import java.io.PrintWriter;
import java.util.Properties;



public class GenDocProperties {	

	private String genDocConfigurationObjectName;
	private String genDocConfigurationObjectRevision;
	private String genDocRunningEnvironmentDetails;
	private String genDocEmailSubject;
	private boolean isPropertiesLoaded;
	private String genDocEmailFrom;
	private String genDocEmailBody;
	private String genDocEmailFooter;
	private String genDocLogFolder;
	private String  genDocLogFilePrefix;
	private String genDocLogFileExtension;
	private String genDocPolicyName;
	private String genDocReleasePhase;
	private String pgRenderPDFGenDoc;
	private String pgPortGenDoc;
	private String pgFTPRootFolderPathGenDoc;
	private String pgProtocolGenDoc;
	private String pgHostNameGenDoc;
	public String getPgHostNameGenDoc() {
		return pgHostNameGenDoc;
	}
	public void setPgHostNameGenDoc(String pgHostNameGenDoc) {
		this.pgHostNameGenDoc = pgHostNameGenDoc;
	}
	private String pgAdlibUserNameGenDoc;
	private String pgAdlibPasswordGenDoc;
	private String pgFTPInputFolderPathGenDoc;
	private String pgFTPOutputFolderPathGenDoc;
	private String pgOnDemandGenDocAllowedTypes;
	private PrintWriter out;

	public GenDocProperties(Properties properties) {
		try {
			this.setPropertiesLoaded(Boolean.FALSE);
			this.setGenDocConfigurationObjectName(properties.getProperty("GenDocPartOnDemandCron.ConfigurationObject.name"));
			this.setGenDocConfigurationObjectRevision(properties.getProperty("GenDocPartOnDemandCron.ConfigurationObject.revision"));
			this.setGenDocRunningEnvironmentDetails(properties.getProperty("GenDocPartOnDemandCron.Running.Evironment.ForEmail"));
			this.setGenDocEmailSubject(properties.getProperty("GenDocPartOnDemandCron.Email.Subject"));
			this.setGenDocEmailFrom(properties.getProperty("GenDocPartOnDemandCron.Email.From.Name"));
			this.setGenDocEmailBody(properties.getProperty("GenDocPartOnDemandCron.Email.Body"));
			this.setGenDocEmailFooter(properties.getProperty("GenDocPartOnDemandCron.Email.Footer"));
			this.setGenDocLogFolder(properties.getProperty("GenDocPartOnDemandCron.logs.logFolder"));
			this.setGenDocLogFilePrefix(properties.getProperty("GenDocPartOnDemandCron.logs.LogFilePrefix"));
			this.setGenDocLogFileExtension(properties.getProperty("GenDocPartOnDemandCron.logs.LogFileExtension"));
			this.setGenDocPolicyName(properties.getProperty("GenDocPartOnDemandCron.policyName"));			
			this.setGenDocReleasePhase(properties.getProperty("GenDocPartOnDemandCron.ReleasePhaseName"));
			this.setPgRenderPDFGenDoc(properties.getProperty("Adlib_setting_pgRenderPDFGenDoc"));
			this.setPgPortGenDoc(properties.getProperty("Adlib_setting_pgPortGenDoc"));
			this.setPgFTPRootFolderPathGenDoc(properties.getProperty("Adlib_setting_pgFTPRootFolderPathGenDoc"));
			this.setPgProtocolGenDoc(properties.getProperty("Adlib_setting_pgProtocolGenDoc"));
			this.setPgHostNameGenDoc(properties.getProperty("Adlib_setting_pgHostNameGenDoc"));
			this.setPgAdlibUserNameGenDoc(properties.getProperty("Adlib_setting_pgAdlibUserNameGenDoc"));
			this.setPgAdlibPasswordGenDoc(properties.getProperty("Adlib_setting_pgAdlibPasswordGenDoc"));
			this.setPgFTPInputFolderPathGenDoc(properties.getProperty("Adlib_setting_pgFTPInputFolderPathGenDoc"));
			this.setPgFTPOutputFolderPathGenDoc(properties.getProperty("Adlib_setting_pgFTPOutputFolderPathGenDoc"));
			this.setPgOnDemandGenDocAllowedTypes(properties.getProperty("GenDocPartOnDemandCron.AllowedTypes"));
			this.setPropertiesLoaded(Boolean.TRUE);
		} catch (Exception e) {
			this.setPropertiesLoaded(Boolean.FALSE);
			out.append("Error while load GenDoc Properties "+e.getMessage()+"\n");			
		}
	}
	public String getGenDocConfigurationObjectName() {
		return genDocConfigurationObjectName;
	}
	public void setGenDocConfigurationObjectName(String genDocConfigurationObjectName) {
		this.genDocConfigurationObjectName = genDocConfigurationObjectName;
	}
	public String getGenDocConfigurationObjectRevision() {
		return genDocConfigurationObjectRevision;
	}
	public void setGenDocConfigurationObjectRevision(String genDocConfigurationObjectRevision) {
		this.genDocConfigurationObjectRevision = genDocConfigurationObjectRevision;
	}
	public String getGenDocRunningEnvironmentDetails() {
		return genDocRunningEnvironmentDetails;
	}
	public void setGenDocRunningEnvironmentDetails(String genDocRunningEnvironmentDetails) {
		this.genDocRunningEnvironmentDetails = genDocRunningEnvironmentDetails;
	}
	public String getGenDocEmailSubject() {
		return genDocEmailSubject;
	}
	public void setGenDocEmailSubject(String genDocEmailSubject) {
		this.genDocEmailSubject = genDocEmailSubject;
	}
	public boolean isPropertiesLoaded() {
		return isPropertiesLoaded;
	}
	public void setPropertiesLoaded(boolean isPropertiesLoaded) {
		this.isPropertiesLoaded = isPropertiesLoaded;
	}
	public String getGenDocEmailFrom() {
		return genDocEmailFrom;
	}
	public void setGenDocEmailFrom(String genDocEmailFrom) {
		this.genDocEmailFrom = genDocEmailFrom;
	}
	public String getGenDocEmailBody() {
		return genDocEmailBody;
	}
	public void setGenDocEmailBody(String genDocEmailBody) {
		this.genDocEmailBody = genDocEmailBody;
	}
	public String getGenDocEmailFooter() {
		return genDocEmailFooter;
	}
	public void setGenDocEmailFooter(String genDocEmailFooter) {
		this.genDocEmailFooter = genDocEmailFooter;
	}
	public String getGenDocLogFolder() {
		return genDocLogFolder;
	}
	public void setGenDocLogFolder(String genDocLogFolder) {
		this.genDocLogFolder = genDocLogFolder;
	}
	public String getGenDocLogFilePrefix() {
		return genDocLogFilePrefix;
	}
	public void setGenDocLogFilePrefix(String genDocLogFilePrefix) {
		this.genDocLogFilePrefix = genDocLogFilePrefix;
	}
	public String getGenDocLogFileExtension() {
		return genDocLogFileExtension;
	}
	public void setGenDocLogFileExtension(String genDocLogFileExtension) {
		this.genDocLogFileExtension = genDocLogFileExtension;
	}
	public String getGenDocPolicyName() {
		return genDocPolicyName;
	}
	public void setGenDocPolicyName(String genDocPolicyName) {
		this.genDocPolicyName = genDocPolicyName;
	}
	public String getGenDocReleasePhase() {
		return genDocReleasePhase;
	}
	public void setGenDocReleasePhase(String genDocReleasePhase) {
		this.genDocReleasePhase = genDocReleasePhase;
	}
	public String getPgRenderPDFGenDoc() {
		return pgRenderPDFGenDoc;
	}
	public void setPgRenderPDFGenDoc(String pgRenderPDFGenDoc) {
		this.pgRenderPDFGenDoc = pgRenderPDFGenDoc;
	}
	public String getPgPortGenDoc() {
		return pgPortGenDoc;
	}
	public void setPgPortGenDoc(String pgPortGenDoc) {
		this.pgPortGenDoc = pgPortGenDoc;
	}
	public String getPgFTPRootFolderPathGenDoc() {
		return pgFTPRootFolderPathGenDoc;
	}
	public void setPgFTPRootFolderPathGenDoc(String pgFTPRootFolderPathGenDoc) {
		this.pgFTPRootFolderPathGenDoc = pgFTPRootFolderPathGenDoc;
	}
	public String getPgProtocolGenDoc() {
		return pgProtocolGenDoc;
	}
	public void setPgProtocolGenDoc(String pgProtocolGenDoc) {
		this.pgProtocolGenDoc = pgProtocolGenDoc;
	}
	public String getPgAdlibUserNameGenDoc() {
		return pgAdlibUserNameGenDoc;
	}
	public void setPgAdlibUserNameGenDoc(String pgAdlibUserNameGenDoc) {
		this.pgAdlibUserNameGenDoc = pgAdlibUserNameGenDoc;
	}
	public String getPgAdlibPasswordGenDoc() {
		return pgAdlibPasswordGenDoc;
	}
	public void setPgAdlibPasswordGenDoc(String pgAdlibPasswordGenDoc) {
		this.pgAdlibPasswordGenDoc = pgAdlibPasswordGenDoc;
	}
	public String getPgFTPInputFolderPathGenDoc() {
		return pgFTPInputFolderPathGenDoc;
	}
	public void setPgFTPInputFolderPathGenDoc(String pgFTPInputFolderPathGenDoc) {
		this.pgFTPInputFolderPathGenDoc = pgFTPInputFolderPathGenDoc;
	}
	public String getPgFTPOutputFolderPathGenDoc() {
		return pgFTPOutputFolderPathGenDoc;
	}
	public void setPgFTPOutputFolderPathGenDoc(String pgFTPOutputFolderPathGenDoc) {
		this.pgFTPOutputFolderPathGenDoc = pgFTPOutputFolderPathGenDoc;
	}
	public String getPgOnDemandGenDocAllowedTypes() {
		return pgOnDemandGenDocAllowedTypes;
	}
	public void setPgOnDemandGenDocAllowedTypes(String pgOnDemandGenDocAllowedTypes) {
		this.pgOnDemandGenDocAllowedTypes = pgOnDemandGenDocAllowedTypes;
	}	
}
