/*
 **   SAPPush.java
 **   Description - Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)     
 **   About - To push BOM to SAP. 
 **
 */
package com.pg.dsm.sapview.services;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.v3.custom.pgV3Constants;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SAPPush {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	String[] connectionParamArray;
	public SAPPush(String[] connectionParamArray) {
		this.connectionParamArray = connectionParamArray;
	}

	/**
	 * This is an existing method moved from (pgEDeliverBOMToSAP_mxJPO) to this class.
	 * Modified by (DSM Sogeti) for SNC-RFC Requirements ID (42702,42703,42704)
	 * @param objectName
	 * @param sArrayEligiblePlants
	 * @param alPartRelatedObjects
	 * @return
	 * @throws Exception
	 */
	public synchronized String initSapConnectionAndStore(String objectName, String[] sArrayEligiblePlants, ArrayList alPartRelatedObjects) throws Exception {
		logger.log(Level.INFO, "Processing BOM e-Delivery for object Name: {0}", objectName);
		String retMessage = SAPViewConstant.BOM_PROCESSED_SUCCESS_MESSAGE.getValue();

		if (null != connectionParamArray && connectionParamArray.length > 0) {
			logger.log(Level.INFO, "SAP Connection Parameter Length >>> {0}", connectionParamArray.length);
			boolean isOkayToMakeConnection = true;
			for (String element : connectionParamArray) {
				if (UIUtil.isNullOrEmpty(element)) {
					isOkayToMakeConnection = false;
					logger.log(Level.INFO, "SAP Connection Parameter is empty");
					break;
				}
			}
			if (isOkayToMakeConnection) {
				if (BusinessUtil.isNotNullOrEmpty(alPartRelatedObjects)) {
                    logger.log(Level.INFO, "Print BOM - Start");
                    for (int i = 0; i < alPartRelatedObjects.size(); i++) {
                        logger.log(Level.INFO, "{0}", alPartRelatedObjects.get(i));
                    }
                    logger.log(Level.INFO, "Print BOM - End");
					// Convert data to be pushed from ArrayList to String double array in format of row and column as required by SAP push APIs.
					String[][] saBOMData = convertArrayListToArray(alPartRelatedObjects, pgV3Constants.INDEX_BOM_ARRAY_SIZE);
					if (saBOMData.length > 0) {
						// Once connection is established successfully the actual data push happens through the method sendDataToSAP
						// Modified by (DSM Sogeti) for SNC-RFC Requirements ID (42702,42703,42704)
						logger.log(Level.INFO, "Initiate call to SAP - START");
                        if (null == sArrayEligiblePlants) {
                            logger.log(Level.WARNING, "Eligible Plant is null/empty");
                        }
                        retMessage = this.sendDataToSAP(saBOMData, sArrayEligiblePlants);
						logger.log(Level.INFO, "Initiate call to SAP - STOP");
						// The method sendDataToSAP() returns error message or success message as "BOM Successfully Processed"
						// Modified by (DSM Sogeti) for SNC-RFC Requirements ID (42702,42703,42704)
					} else {
						logger.log(Level.INFO, "BOM Data is empty - Nothing to send to SAP");
					}
				} else {
					logger.log(Level.INFO, "No Data to send to SAP");
				}
			} else {
				logger.log(Level.INFO, "SAP Connection Parameter(s) on Config Business Object is empty");
			}
		}
		return retMessage;
	}

	/**
	 * This is an existing method moved from (pgEDeliverBOMToSAP_mxJPO) to this class.
	 * Modified by (DSM Sogeti) for SNC-RFC Requirements ID (42702,42703,42704) 
	 * @param saBOMData
	 * @param sArrayEligiblePlants
	 * @param saConnectionParams
	 * @return
	 * @throws Exception
	 */
	private synchronized String sendDataToSAP(String[][] saBOMData, String[] sArrayEligiblePlants) throws Exception {
        JCO.Function function;
        IRepository repository;
        // Method taken from Bean, earlier being used for CSS to SAP push.
        JCO.Client client = null;
        String strMessage = "";
        boolean bClientOpen = false;
        String strChildEffecDate = "";
        String strParentEffecDate = "";
        //Added by DSM - for 2018x.1 requirement #25043 - Starts
        String strValidUntilDate = "";
        //Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
        String strPlantName = DomainConstants.EMPTY_STRING;
        String strTableParameterCheck = DomainConstants.EMPTY_STRING;
        String[] arrPlantDetails = null;
        //Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
        //Added by DSM - for 2018x.1 requirement #25043 - Ends
        try {
            // Modified by (DSM Sogeti) for SNC-RFC Requirements ID (42702,42703,42704): START
            logger.log(Level.INFO, "Make Connection to SAP using SNC RFC");
            com.pg.dsm.sapview.services.SAPRFCClient rfcClient = new com.pg.dsm.sapview.services.SAPRFCClient.Connector(connectionParamArray).connect();
            if (rfcClient.isConnected()) {
                client = rfcClient.getClient();
                if (client.isAlive()) {
                    logger.log(Level.INFO, "3DX-SAP Handshake was successful");
                    strMessage = SAPViewConstant.BOM_PROCESSED_SUCCESS_MESSAGE.getValue();
                    // Modified by (DSM Sogeti) for SNC-RFC Requirements ID (42702,42703,42704): END
                    bClientOpen = true;
                    logger.log(Level.INFO, "*********** Enovia system got connected to SAP ********************");
                    repository = new JCO.Repository("RDB RFC Repository", client);
                    logger.log(Level.INFO, "Getting SAP Repository handle was successful");
                    function = repository.getFunctionTemplate("ZT_RFC_BOM_CREATE_PLM").getFunction();
                    if (function != null) {
                        logger.log(Level.INFO, "Getting SAP Repository-Function handle was successful");
                        JCO.ParameterList plTemp = function.getImportParameterList();
                        logger.log(Level.INFO, "Get JCO Import Parameter List");
                        JCO.Table tTemp = plTemp.getTable("CT_BOM_DATA");
                        logger.log(Level.INFO, "Get JCO BOM (CT_BOM_DATA) Table");
                        //Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
                        JCO.Table tTempAPPlants = plTemp.getTable("CT_BOM_PLANT");
                        logger.log(Level.INFO, "Get JCO Authorized to Produce (CT_BOM_PLANT) - Plants Table");
                        JCO.Table tTempAUPlants = plTemp.getTable("CT_BOM_PLANT_2");
                        logger.log(Level.INFO, "Get JCO Authorized to Use (CT_BOM_PLANT_2) - Plants Table");
                        //Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
                        if (saBOMData != null && saBOMData.length > 0) {
                            for (int i = 0; i < saBOMData.length; i++) {
                                tTemp.appendRow();
                                String strSort = saBOMData[i][pgV3Constants.INDEX_SORT_STRING];
                                if (strSort == null || strSort.equalsIgnoreCase("Null")) {
                                    strSort = DomainConstants.EMPTY_STRING;
                                }
                                String strUpperLimit = saBOMData[i][pgV3Constants.INDEX_UPPER_LIMIT];
                                if (strUpperLimit == null || strUpperLimit.equalsIgnoreCase("Null")) {
                                    strUpperLimit = DomainConstants.EMPTY_STRING;
                                }
                                String strLowerLimit = saBOMData[i][pgV3Constants.INDEX_LOWER_LIMIT];
                                if (strLowerLimit == null || strLowerLimit.equalsIgnoreCase("Null")) {
                                    strLowerLimit = DomainConstants.EMPTY_STRING;
                                }
                                tTemp.setValue("0000000000" + saBOMData[i][pgV3Constants.INDEX_MATERIAL_NUMBER], "MATNR"); //Material Number
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_BOM_USAGE], "STLAN"); // BOM Usage
                                strParentEffecDate = saBOMData[i][pgV3Constants.INDEX_VALID_FROM_DATE_PARENT];
                                if (BusinessUtil.isNotNullOrEmpty(strParentEffecDate)) {
                                    tTemp.setValue(strParentEffecDate, "DATUV"); // Parent Effective Date
                                }
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_ALT_BOM], "STLAL"); // ALT BOM
                                tTemp.setValue("", "WERKS"); // PLANT -- not used
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_BOM_TEXT], "STKTX"); // ALT BOM TEXT
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_ALT_BOM_TEXT], "ZTEXT"); // ALT BOM TEXT
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_CONFIRMED_QUANTITY], "BMENG"); // BOM Base Qty
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_BOM_STATUS], "STLST"); // BOM STATUS
                                tTemp.setValue("0000000000" + saBOMData[i][pgV3Constants.INDEX_COMPONENT_NAME], "IDNRK"); // Comp Name
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_ITEM_CATEGORY], "POSTP"); // Item Category
                                tTemp.setValue((i + 1) * 10, "POSNR"); // Incrementing Number
                                strChildEffecDate = saBOMData[i][pgV3Constants.INDEX_VALID_FROM_DATE_CHILD];
                                if (BusinessUtil.isNotNullOrEmpty(strChildEffecDate)) {
                                    tTemp.setValue(strChildEffecDate, "DATUV_I"); // Child Effective Date
                                }
                                //Added by DSM - for 2018x.1 requirement #25043 - Starts
                                //Added by DSM - for 2018x.1 requirement #25043 - Ends
                                tTemp.setValue(strSort, "SORTF"); // Position Indicator
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_SUBSTITUTE_FLAG], "ALPGR"); // Alt Item Group -- Substitute combination numbers AA-ZZ
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_USAGE_PROBABILITY], "EWAHR"); // Usage Probability
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_RANGE_FLAG], "ZRANGE_FLAG"); // Range Flag
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_TARGET], "MENGE"); // Component Quantity -- BOM Target?
                                tTemp.setValue(strUpperLimit, "ZUPPER_LIMIT"); // Upper Limit -- Max?
                                tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_QUANTITY], "ZTARGET_LIMIT"); // Target --- Target?
                                tTemp.setValue(strLowerLimit, "ZLOWER_LIMIT"); // Lower Limit -- Min?
                            }

                            //Make sure to use the correct material number (may be gcas, may be FPC)
                            //Material number column from BOM data is the same for every row
                            String sMatNumber = "0000000000" + saBOMData[0][pgV3Constants.INDEX_MATERIAL_NUMBER];
                            String saTempPlant = DomainConstants.EMPTY_STRING;
                            for (int i = 0; i < sArrayEligiblePlants.length; i++) {
                                //Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
                                arrPlantDetails = sArrayEligiblePlants[i].split(pgV3Constants.PLANTS_DELIMITER);
                                if (arrPlantDetails != null) {
                                    strPlantName = arrPlantDetails[0];
                                    strTableParameterCheck = arrPlantDetails[1];

                                    if (UIUtil.isNotNullAndNotEmpty(strPlantName) && UIUtil.isNotNullAndNotEmpty(strTableParameterCheck)) {

                                        if (strTableParameterCheck.equalsIgnoreCase(pgV3Constants.TABLE_PARAM_CHECK_AU)) {

                                            tTempAUPlants.appendRow();
                                            tTempAUPlants.setValue(sMatNumber, "MATNR");
                                            tTempAUPlants.setValue(strPlantName, "WERKS");
                                        } else if (strTableParameterCheck.equalsIgnoreCase(pgV3Constants.TABLE_PARAM_CHECK_AP)) {

                                            tTempAPPlants.appendRow();
                                            tTempAPPlants.setValue(sMatNumber, "MATNR");
                                            tTempAPPlants.setValue(strPlantName, "WERKS");
                                        }
                                    }
                                }
                                //Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
                            }
                            logger.log(Level.INFO, "*********** Enovia to SAP PUSH - Starts ********************");
                            client.execute(function);
                            JCO.ParameterList plResponse = function.getExportParameterList();
                            int i = plResponse.getFieldCount();
                            JCO.Structure sReturn = plResponse.getField("EW_RETURN").getStructure();
                            JCO.Field fRetMessage = sReturn.getField("MESSAGE");
                            String response = "" + fRetMessage.getValue();
                            logger.log(Level.INFO, "Message from SAP : {0}", response);
                            logger.log(Level.INFO, "*********** Enovia to SAP PUSH - Ends ********************");
                            strMessage = response;
                        }
                    }
                } else {
                	logger.log(Level.WARNING, "SAP Connection is not alive");
                }
            } else {
                logger.log(Level.WARNING, "Failed to connect to SAP with - error: {0}", rfcClient.getErrorMessage());
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception occurred ", ex);
            strMessage = ex.getMessage();
        }
        //This should be done in finally using variable which suggests connect has happened
        finally {
            if (bClientOpen && null != client) {
                client.disconnect();
                logger.log(Level.INFO, "(Finally) SAP Connection is closed");
            }
        }
        return strMessage;
    }

	/**
	 * Convert arrayList to Array
	 * @param alGeneric
	 * @param iArraySize
	 * @return
	 * @throws Exception
	 */
	private String[][] convertArrayListToArray(ArrayList alGeneric, int iArraySize) throws Exception {
		String[][] saReturn = new String[1][1];
		try {
			if (alGeneric != null) {
				Iterator itr = alGeneric.iterator();
				saReturn = new String[alGeneric.size()][iArraySize];
				int i = 0;
				while (itr.hasNext()) {
					String[] saTemp = (String[]) itr.next();
					saReturn[i] = saTemp;
					i++;
				}
			} else {
				saReturn = new String[1][1];
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception occurred ", e);
			saReturn = new String[1][1];
		}

		return saReturn;
	}

}
