/*
 **   SAPRFCConfig.java
 **   Description - Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)
 **   About - To parse SNC configurations.
 **
 */
package com.pg.dsm.sapview.config;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.dsm.sapview.services.SAPRFCClient;
import com.pg.v3.custom.pgV3Constants;
import com.sap.mw.jco.JCO;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SAPRFCConfig {

	private static final Logger logger = Logger.getLogger(SAPRFCConfig.class.getName());

	String[] connectionParameterArray;
	boolean isLoaded;
	String errorMessage;

	private SAPRFCConfig(Builder builder) {
		this.isLoaded = builder.isLoaded;
		this.errorMessage = builder.errorMessage;
		this.connectionParameterArray = builder.connectionParameterArray;
	}

	public String[] getConnectionParameterArray() {
		return connectionParameterArray;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Method to test SAP connection
	 */
	public void testSAPConnection() {
		try {
			// iterate through connection parameter array and verify that none of the array element should be empty.
			boolean isConnectionParametersOkay = true;
			for (String element : connectionParameterArray) {
				if (UIUtil.isNullOrEmpty(element)) {
					isConnectionParametersOkay = false;
					logger.log(Level.WARNING, "SAP RFC Connection Parameter is empty");
					break;
				}
			}
			if (isConnectionParametersOkay) {
				SAPRFCClient rfcClient = new SAPRFCClient.Connector(connectionParameterArray).connect();
				if (rfcClient.isConnected()) {
					JCO.Client client = rfcClient.getClient();
					if (client.isAlive()) {
						logger.log(Level.INFO, "SAP Connection is alive");
						client.disconnect();
						logger.log(Level.INFO, "Connection closed");
					} else {
						logger.log(Level.WARNING, "SAP Connection is lost");
					}
				} else {
					logger.log(Level.WARNING, "Not connected to SAP");
				}
			} else {
				logger.log(Level.WARNING, "SAP RFC Connection Parameter(s) on Config Business Object is empty");
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred ", e);
		}
	}

	public static class Builder {
		private static final Logger logger = Logger.getLogger(Builder.class.getName());
		Context context;
		String[] connectionParameterArray;
		boolean isLoaded;
		String errorMessage;

		public Builder(Context context) {
			this.context = context;
			this.connectionParameterArray = new String[7];
		}

		public SAPRFCConfig build() {
			try {
				buildConnectionParameters();
				if (this.isLoaded) {
					logger.log(Level.INFO, "SAP Config Map - loaded successfully");
				} else {
					logger.log(Level.INFO, "SAP Config Map - failed to load with error: {0}", this.errorMessage);
				}
			} catch (Exception e) {
				this.isLoaded = Boolean.FALSE;
				this.errorMessage = e.toString();
				logger.log(Level.WARNING, "Exception occurred ", e);
			}
			return new SAPRFCConfig(this);
		}

		/**
		 * Method to load BOM e-Delivery Configuration object
		 *
		 * @return array
		 */
		private void buildConnectionParameters() throws MatrixException {
			DomainObject domainObject = DomainObject.newInstance(context,
					new BusinessObject(
							pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
							pgV3Constants.CONFIG_OBJECT_PGSENDPLANTSSAPCONFIG,
							pgV3Constants.SYMBOL_HYPHEN,
							pgV3Constants.VAULT_ESERVICEPRODUCTION));
			if (domainObject.exists(context)) {
				Map<Object, Object> objectMap = domainObject.getInfo(context, getBusSelects());
				if (null != objectMap) {
					parseConnectionParameters(objectMap);
				} else {
					this.errorMessage = SAPViewConstant.BOM_EDELIVERY_CONFIG_OBJECT_MAP_ISSUE.getValue();
					logger.log(Level.WARNING, errorMessage);
				}
			} else {
				this.errorMessage = SAPViewConstant.BOM_EDELIVERY_CONFIG_OBJECT_NOT_FOUND.getValue();
				logger.log(Level.WARNING, errorMessage);
			}
		}

		/**
		 * Method to parse connection attribute Map to Array.
		 * @param objectMap
		 */
		private void parseConnectionParameters(Map<Object, Object> objectMap) {
			
			/*
			 * Configuration Parameter Order
			 * 0 - SAP Host
			 * 1 - SAP Client Number
			 * 2 - SAP System Number
			 * 3 - SNC Mode
			 * 4 - SNC Name
			 * 5 - SNC Service Library
			 * 6 - SNC Partner Name
			 */ 
			
			connectionParameterArray[0] = (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGSERVERHOST);
			// SAP Client Number, SAP System Number is stored on this attribute in this format (SAP Client Number|SAP System Number)
			String sCommonConfigAttribute = (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONATTR);
			int index = sCommonConfigAttribute.indexOf(pgV3Constants.SYMBOL_PIPE);
			if (index > 0) {
				connectionParameterArray[1] = sCommonConfigAttribute.substring(0, index); // SAP Client Number
				connectionParameterArray[2] = sCommonConfigAttribute.substring(index + 1); // SAP System Number
				connectionParameterArray[3] = (String) objectMap.get(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_MODE); // SNC Mode
				connectionParameterArray[4] = (String) objectMap.get(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_NAME); // SNC Name
				connectionParameterArray[5] = (String) objectMap.get(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_SERVICE_LIBRARY); // SNC Service Library
				connectionParameterArray[6] = (String) objectMap.get(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_PARTNER_NAME); // SNC Partner Name
				this.isLoaded = Boolean.TRUE;
				logger.log(Level.INFO, ">>>> SAP Connection Parameter Value on Config Object - START >>>>>>");
				logger.log(Level.INFO, "Host Name: {0}", connectionParameterArray[0]);
				logger.log(Level.INFO, "Client Number: {0}", connectionParameterArray[1]);
				logger.log(Level.INFO, "System Number: {0}", connectionParameterArray[2]);
				logger.log(Level.INFO, "SNC Mode: {0}", connectionParameterArray[3]);
				logger.log(Level.INFO, "SNC Name: {0}", connectionParameterArray[4]);
				logger.log(Level.INFO, "SNC Service Library: {0}", connectionParameterArray[5]);
				logger.log(Level.INFO, "SNC Partner Name: {0}", connectionParameterArray[6]);
				logger.log(Level.INFO, ">>>> SAP Connection Parameter Value on Config Object - STOP >>>>>>");
			} else {
				this.errorMessage = SAPViewConstant.BOM_EDELIVERY_CONFIG_PARAMETER_ISSUE.getValue();
				logger.log(Level.WARNING, errorMessage);
			}
		}

		/**
		 * Method to hold list of selectable.
		 * @return
		 */
		private StringList getBusSelects() {
			StringList busSelects = new StringList(7);
			busSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSERVERHOST);
			busSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONATTR);
			busSelects.addElement(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_MODE);
			busSelects.addElement(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_NAME);
			busSelects.addElement(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_SERVICE_LIBRARY);
			busSelects.addElement(SAPConstants.SELECT_ATTRIBUTE_JCO_CLIENT_SNC_PARTNER_NAME);
			return busSelects;
		}
	}
}
