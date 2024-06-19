/*
 **   SAPRFCClient.java
 **   Description - Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)     
 **   About - To make connection with SAP using SNC RFC. 
 **
 */
package com.pg.dsm.sapview.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.v3.custom.pgV3Constants;
import com.sap.mw.jco.JCO;

public class SAPRFCClient {

	boolean isConnected;
	JCO.Client client;
	String errorMessage;
	private SAPRFCClient(Connector connector) {
		this.isConnected = connector.isConnected;
		this.client = connector.client;
		this.errorMessage = connector.errorMessage;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public JCO.Client getClient() {
		return client;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public static class Connector {
		private static final Logger logger = Logger.getLogger(Connector.class.getName());
		String[] parameters;
		JCO.Client client;
		boolean isConnected;
		String errorMessage;

		public Connector(String[] parameters) {
			this.parameters = parameters;
		}

		/**
	     * Method to make connection with SAP using SNC configurations.
	     * @return SAPRFCClient
	     */
		public synchronized SAPRFCClient connect() {
			try {
				establishConnection();
				if(client.isAlive()) {
					this.isConnected = true;
					logger.log(Level.INFO,"** SAP Connection is alive **");
				} else {
					this.isConnected = false;
					logger.log(Level.INFO,"** SAP Connection is lost **");
				}
			} catch (Exception e) {
				this.isConnected = false;
				this.errorMessage = e.getMessage();
				logger.log(Level.WARNING, "Exception occurred ", e);
			}
			return new SAPRFCClient(this);
		}

		/**
	     * Method to make connection with SAP using SNC configurations.
	     */
		private void establishConnection() {
			String sSAPLanguage = pgV3Constants.SAP_LANGUAGE;
			String sSAPHostName = parameters[0]; // SAP Host Name
			String sSAPClientNumber = parameters[1]; // SAP Client Number
			String sSAPSystemNumber = parameters[2]; // SAP System Number
			String sSAPClientSNCMode = parameters[3]; // SNC Mode
			String sSAPClientSNCName = parameters[4]; // SNC Name
			String sSAPClientSNCServiceLibrary = parameters[5]; // SNC Service Library
			String sSAPClientSNCPartnerName = parameters[6]; // SNC Partner Name

			logger.log(Level.INFO, "###############################################################");
			logger.log(Level.INFO,"Client Number: {0}", sSAPClientNumber);
			logger.log(Level.INFO,"System Number: {0}", sSAPSystemNumber);
			logger.log(Level.INFO,"SAP Host: {0}", sSAPHostName);
			logger.log(Level.INFO,"SAP Language: {0}", sSAPLanguage);
			logger.log(Level.INFO,"Client SNC Mode: {0}", sSAPClientSNCMode);
			logger.log(Level.INFO,"Client SNC Name: {0}", sSAPClientSNCName);
			logger.log(Level.INFO,"Client SNC Partner Name: {0}", sSAPClientSNCPartnerName);
			logger.log(Level.INFO,"Client SNC Service Library: {0}", sSAPClientSNCServiceLibrary);

			java.util.Properties props = new java.util.Properties();
			props.setProperty("jco.client.client", sSAPClientNumber); // SAP Client number
			props.setProperty("jco.client.sysnr", sSAPSystemNumber); // SAP System Number
			props.setProperty("jco.client.ashost", sSAPHostName); // SAP Host Name
			props.setProperty("jco.client.snc_mode", sSAPClientSNCMode); // SNC Mode
			props.setProperty("jco.client.snc_partnername", sSAPClientSNCPartnerName); // SNC Partner Name
			props.setProperty("jco.client.snc_lib", sSAPClientSNCServiceLibrary); // SNC Service Library
			props.setProperty("jco.snc_myname", sSAPClientSNCName); // SNC Name
			client = JCO.createClient(props);
			client.connect();
			logger.log(Level.INFO,"** SAP Connection established using secure RFC **");
			logger.log(Level.INFO, "###############################################################");
		}
	}
}
