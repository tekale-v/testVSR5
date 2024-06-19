/*
 **   ScheduledExecution.java
 **   Description - Introduced as part of Veeva integration.      
 **   Entry point for Veeva CTRLM Functionality. 
 **
 */
package com.pg.dsm.veeva;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.matrixone.apps.domain.util.PersonUtil;
import com.pg.dsm.veeva.config.Configurator;

import com.pg.dsm.veeva.config.VeevaVQL;
import com.pg.dsm.veeva.util.Mail;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.v3.custom.pgV3Constants;

public class ScheduledExecution {

	private static final Logger logger = Logger.getLogger(ScheduledExecution.class.getName());

	/**
	 * @about - Constructor
	 */
	public ScheduledExecution() {
		init();
	}

	/**
	 * @about - Helper method for veeva-ctrlm execution
	 * @return void
	 * @throws Exception
	 * @since DSM 2018x.3 DSM 
	 * modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void perform() throws Exception {
		try {
			Configurator configurator = new Configurator.Builder()
					.setProperties()
					.setContext()
					.setMatrixVeevaConfigBusinessObject()
					.setVeevaConfig()
					.setVeevaConfigXML()
					.setExtractionRequiredFolders()
					.build();

			if (configurator.isLoaded()) {
				logger.info("Configurator is loaded for scheduled extraction");
				if (Boolean.parseBoolean(configurator.getVeevaConfig().getAttrConfigActive())) {
					if (Integer.parseInt(configurator.getVeevaConfig().getAttrRetryCount()) > 2) {
						Properties properties = configurator.getProperties();
						logger.info("Notify Admin on Veeva Cron Job Struck");
						boolean bMailSent = new Mail().sendEmailToUsers(configurator.getContext(),
								PersonUtil.getEmail(configurator.getContext(), pgV3Constants.PERSON_USER_AGENT),
								properties.getProperty("veeva.configobject.mailid"),
								properties.getProperty("veeva.cron.Failure.Message"),
								properties.getProperty("veeva.cron.Failure.Subject"),
								Veeva.EMPTY_STRING);
						logger.info("Mail sent? " + String.valueOf(bMailSent));
						logger.info("Mail sent to Admin Successfully");
					}
				} else {

					logger.info("___________________________Scheduled Veeva extraction started >>");

					VeevaVQL veevaVQL = new VeevaVQL.Builder(configurator)
							.setAuthenticationVQL()
							.setDocumentsVQL()
							.setDocumentDataSetVQL()
							.setUsersEmailVQL()
							.setDocumentDataPropertyVQL()
							.setRenditionVQL()
							.build();

					if (veevaVQL.isLoaded()) {
						VeevaVault veevaVault = new VeevaVault(configurator, veevaVQL);
						veevaVault.extractScheduled();
						logger.info(
								"Veeva extraction folder " + configurator.getExtractionRequiredFolders().getInwork());
						logger.info("___________________________Scheduled Veeva extraction completed >>");

						logger.info("___________________________Scheduled Matrix updation started >>");
						MatrixVault matrixVault = new MatrixVault(configurator);
						matrixVault.updateScheduled();
						logger.info("___________________________Scheduled Matrix updation completed >>");
					} else {
						logger.error("Failed to form VQL Queries");
					}
				}
			} else {
				logger.error("Failed to load Configurator for scheduled extraction");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	/**
	 * @about - Method to load the logger property file.
	 * @return void - nothing
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public void init() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(Veeva.VEEVA_LOG4J_FILE));
			PropertyConfigurator.configure(prop);
			logger.info("log4j loaded");
		} catch (FileNotFoundException e) {
			logger.error("Unable to load file log4j.properties");
		} catch (IOException e) {
			logger.error("Unable to load file log4j.properties");
		}
	}

	/**
	 * @about - Main Method to run veeva-ctrlm
	 * @return void - nothing
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public static void main(String[] args) throws Exception {
		try {
			ScheduledExecution scheduledExecution = new ScheduledExecution();
			scheduledExecution.perform();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		logger.info("Exit ScheduledExecution - main method");
		System.exit(0);
	}
}
