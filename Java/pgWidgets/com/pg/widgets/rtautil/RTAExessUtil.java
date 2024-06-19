package com.pg.widgets.rtautil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.awl.util.AWLConstants;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.DomainConstants;

import com.pg.v3.custom.pgV3Constants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class RTAExessUtil {

	private static final Logger logger = Logger.getLogger(RTAExessUtil.class.getName());
	private RTAExessUtil() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String submitExessRequest(Context context, String strInput) {
		JsonObjectBuilder jsonObjReturn = Json.createObjectBuilder();
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			StringList slPOAList = FrameworkUtil.split(jsonInput.getString(RTAUtilConstants.POA_IDs),
					PGWidgetConstants.KEY_COMMA_SEPARATOR);

			BusinessObject boConfigObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
					EnoviaResourceBundle.getProperty(context,
							"emxAWL.RTABusinessObjectName.ArtworkConfigurationDetails"),
					pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);

			DomainObject domObject = DomainObject.newInstance(context, boConfigObject);
			int iMaxPOA = Integer.parseInt(domObject.getAttributeValue(context,
					PropertyUtil.getSchemaProperty(context, "attribute_pgMaxPOADetailsToRulesManager")));

			if (slPOAList.size() > iMaxPOA) {
				String[] args = { String.valueOf(iMaxPOA) };
				String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.MaxPOAsSelectedForRulesManager",
						args, null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
				jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert).add(PGWidgetConstants.KEY_ERROR, sAlert);
				return jsonObjReturn.build().toString();

			} else {
				Map<String,Object> mRequestMap = new HashMap<>();
				mRequestMap.put(RTAUtil.KEY_SELECTEDPOAs, slPOAList);
				String sInvalidPOA = JPO.invoke(context, "pgAAA_Util", null, "identifyInvalidPOA",
						JPO.packArgs(mRequestMap), String.class);

				if (BusinessUtil.isNotNullOrEmpty(sInvalidPOA)) {
					String[] args = { sInvalidPOA };
					String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.InvalidPOA", args,
							null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);

					jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert).add(PGWidgetConstants.KEY_ERROR, sAlert);
					return jsonObjReturn.build().toString();
				} else {
					mRequestMap.put(RTAUtil.KEY_INTEGRATIONTYPE,
							EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.EXCESSIntegration"));
					Map<Object, Object> mPOAInfo = JPO.invoke(context, "pgAAA_Util", null, "processPOAData",
							matrix.db.JPO.packArgs(mRequestMap), Map.class);
					StringBuilder sbAlert = new StringBuilder();
					if (mPOAInfo != null && !mPOAInfo.isEmpty()) {
						StringList slPOAToBeSubmitted = (StringList) mPOAInfo.get("New");
						StringList slPOAWithKeep = (StringList) mPOAInfo.get("Keep");
						StringList slPOAWithSameAs = (StringList) mPOAInfo.get("SameAs");
						// Modified to accommodate for 22x.3 Aug_23_CW Defect 54109 Starts
						/*
						 * if (slPOAWithKeep != null && !slPOAWithKeep.isEmpty()) { StringList
						 * slPOANameWithKeep = getNameFromID(context, slPOAWithKeep, mPOAInfo); String[]
						 * args = { FrameworkUtil.join(slPOANameWithKeep, ",") }; String sAlert =
						 * MessageUtil.getMessage(context, null,
						 * "emxAWL.Alert.Notification.UnableToRetrieveCE", args, null,
						 * context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
						 * sbAlert.append(sAlert); }
						 */
						if (slPOAWithSameAs != null && !slPOAWithSameAs.isEmpty()) {
							StringList slPOANameWithSameAs = getNameFromID(context, slPOAWithSameAs, mPOAInfo);
							String[] args = { FrameworkUtil.join(slPOANameWithSameAs, ",") };
							String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.SameAsPOA",
									args, null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
							sbAlert.append(sAlert);
						}
						if (slPOAToBeSubmitted != null && !slPOAToBeSubmitted.isEmpty()) {
							mRequestMap.put(RTAUtil.KEY_INTEGRATIONTYPE, slPOAToBeSubmitted);
							mRequestMap.put("jobTitle",
									EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.EXCESSIntegrationJobTitle"));
							StringList slPOANameForSubmitted = getNameFromID(context, slPOAToBeSubmitted, mPOAInfo);
							String[] args = { FrameworkUtil.join(slPOANameForSubmitted, ",") };
							String sAlert = MessageUtil.getMessage(context, null,
									"emxAWL.Alert.Notification.SendReqToRulesManager", args, null, context.getLocale(),
									AWLConstants.AWL_STRING_RESOURCE);
							// create job object and save all the poa ids
							JPO.invoke(context, "pgAAA_Util", null, "createJobObjectForReqToRulesManager",
									matrix.db.JPO.packArgs(mRequestMap), void.class);
							sbAlert.append(sAlert);
						}
						// Modified to accommodate for 22x.3 Aug_23_CW Defect 54109
						else {
							mRequestMap.put("selectedPOAs", slPOAWithKeep);
							mRequestMap.put("jobTitle",
									EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.EXCESSIntegrationJobTitle"));
							StringList slPOANameWithKeep = getNameFromID(context, slPOAWithKeep, mPOAInfo);
							String[] args = { FrameworkUtil.join(slPOANameWithKeep, ",") };
							String sAlert = MessageUtil.getMessage(context, null,
									"emxAWL.Alert.Notification.SendReqToRulesManager", args, null, context.getLocale(),
									AWLConstants.AWL_STRING_RESOURCE);
							// create job object and save all the poa ids
							JPO.invoke(context, "pgAAA_Util", null, "createJobObjectForReqToRulesManager",
									matrix.db.JPO.packArgs(mRequestMap), void.class);
							sbAlert.append(sAlert);
						}
						// Modified to accommodate for 22x.3 Aug_23_CW Defect 54109 End
						if (sbAlert.length() > 0) {
							jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sbAlert.toString());
						}
					} else {
						String sAlert = EnoviaResourceBundle.getProperty(context, AWLConstants.AWL_STRING_RESOURCE,
								context.getLocale(), "emxAWL.Alert.Notification.POANotSubmitted");
						jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert).add(PGWidgetConstants.KEY_ERROR,
								sAlert);
					}
					return jsonObjReturn.build().toString();
				}

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, Arrays.toString( e.getStackTrace()));
			return jsonObjReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
	}
	
	public static StringList getNameFromID(Context context,StringList slPOAID,Map mPOAInfo)throws Exception{
		StringList slPOAName = new StringList(slPOAID.size());
		for(String sPOAID:slPOAID){
			slPOAName.add((String)mPOAInfo.get(sPOAID));
		}
		return slPOAName;
	}
	
	public static String retrieveAddress(Context context, String paramString) throws Exception {
		JsonObjectBuilder jsonObjReturn = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String selectedPOAId = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList slPOAs = null;
			String sPOAId = selectedPOAId;
			String[] sArrSelectedPOAId = sPOAId.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
			slPOAs = BusinessUtil.toStringList(sArrSelectedPOAId);

			int iSize = slPOAs.size();
			BusinessObject boConfigObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
					EnoviaResourceBundle.getProperty(context,
							"emxAWL.RTABusinessObjectName.ArtworkConfigurationDetails"),
					pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
			DomainObject domObject = DomainObject.newInstance(context, boConfigObject);
			int iMaxPOA = Integer.parseInt(domObject.getAttributeValue(context,
					PropertyUtil.getSchemaProperty(context, "attribute_pgMaxPOADetailsToRulesManager")));
			if (iSize > iMaxPOA) {
				String[] args = { String.valueOf(iMaxPOA) };
				String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.MaxPOAsSelectedForRulesManager",
						args, null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
				jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
				return jsonObjReturn.build().toString();
			} else { // iSize can never be < 1 and hence this loop is for isize < iMaxPOA
				Map mRequestMap = new HashMap();
				mRequestMap.put("selectedPOAs", slPOAs);
				mRequestMap.put("integrationType",
						EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.ADDRESSIntegration"));
				// Added by RTA Capgemini Offshore for 22x.3 Defect#54155 - Starts
				mRequestMap.put("IsFromAction", "Manual");
				String sInvalidPOA = JPO.invoke(context, "pgAAA_Util", null, "identifyInvalidPOA",
						matrix.db.JPO.packArgs(mRequestMap), String.class);
				if (BusinessUtil.isNotNullOrEmpty(sInvalidPOA)) {
					String[] args = new String[1];
					String sAlert = DomainConstants.EMPTY_STRING;
					args[0] = sInvalidPOA;
					sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.InvalidPOA", args, null,
							context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
					jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
					return jsonObjReturn.build().toString();
				} else {
					Map mPOAInfo = JPO.invoke(context, "pgAAAExt_Util", null, "processPOAAddressData",
							matrix.db.JPO.packArgs(mRequestMap), Map.class);
					StringBuilder sbAlert = new StringBuilder();
					if (mPOAInfo != null && !mPOAInfo.isEmpty()) {
						// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect#48148 - Starts
						StringList slPOAToBeSubmitted = (StringList) mPOAInfo.get("sPOAObjId");
						// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect#48148 - End
						if (slPOAToBeSubmitted != null && !slPOAToBeSubmitted.isEmpty()) {
							mRequestMap.put("selectedPOAs", slPOAToBeSubmitted);
							mRequestMap.put("jobTitle",
									EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.ADDRESSIntegrationJobTitle"));
							mRequestMap.put("integrationType",
									EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.ADDRESSIntegration"));
							StringList slPOANameForSubmitted = getNameFromID(context, slPOAToBeSubmitted, mPOAInfo);
							String[] args = { FrameworkUtil.join(slPOANameForSubmitted, ",") };
							String sAlert = MessageUtil.getMessage(context, null,
									"emxAWL.Alert.Notification.SendReqToRulesManager", args, null, context.getLocale(),
									AWLConstants.AWL_STRING_RESOURCE);
							// create job object and save all the poa ids
							JPO.invoke(context, "pgAAA_Util", null, "createJobObjectForReqToRulesManager",
									matrix.db.JPO.packArgs(mRequestMap), void.class);
							sbAlert.append(sAlert);
						}
						if (sbAlert.length() > 0) {
							// return sbAlert.toString();
							jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sbAlert.toString());
						}
					} else {
						String sAlert = EnoviaResourceBundle.getProperty(context, AWLConstants.AWL_STRING_RESOURCE,
								context.getLocale(), "emxAWL.Alert.Notification.POANotSubmitted");
						jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
						// return sAlert;
					}
					return jsonObjReturn.build().toString();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
			return jsonObjReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
	}
	
	public static String retrieveDataForSizeBase(Context context, String paramString) throws Exception {
		JsonObjectBuilder jsonObjReturn = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String selectedPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList slPOAs = null;

			String[] sArrSelectedPOAId = selectedPOAIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
			slPOAs = BusinessUtil.toStringList(sArrSelectedPOAId);

			int iSize = slPOAs.size();
			BusinessObject boConfigObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
					EnoviaResourceBundle.getProperty(context,
							"emxAWL.RTABusinessObjectName.ArtworkConfigurationDetails"),
					pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
			DomainObject domObject = DomainObject.newInstance(context, boConfigObject);
			int iMaxPOA = Integer.parseInt(domObject.getAttributeValue(context,
					PropertyUtil.getSchemaProperty(context, "attribute_pgMaxPOADetailsToRulesManager")));
			if (iSize > iMaxPOA) {
				String[] args = { String.valueOf(iMaxPOA) };
				String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.MaxPOAsSelectedForRulesManager",
						args, null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
				jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
				return jsonObjReturn.build().toString();
			} else {
				Map mRequestMap = new HashMap();
				mRequestMap.put("selectedPOAs", slPOAs);
				mRequestMap.put("integrationType",
						EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.SIZEBASEIntegration"));
				// Added by RTA Capgemini Offshore for 22x.3 Defect#54155 - Starts
				mRequestMap.put("IsFromAction", "Manual");
				String sInvalidPOA = JPO.invoke(context, "pgAAA_Util", null, "identifyInvalidPOA",
						matrix.db.JPO.packArgs(mRequestMap), String.class);
				if (BusinessUtil.isNotNullOrEmpty(sInvalidPOA)) {
					String[] args = new String[1];
					String sAlert = DomainConstants.EMPTY_STRING;
					args[0] = sInvalidPOA;
					sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.InvalidPOA", args, null,
							context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
					jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
					return jsonObjReturn.build().toString();
					// Added by RTA Capgemini Offshore for 22x.3 Defect#54155 - Ends
				} else {
					Map mPOAInfo = JPO.invoke(context, "pgAAAExt_Util", null, "processPOASizeBaseData",
							matrix.db.JPO.packArgs(mRequestMap), Map.class);
					StringBuilder sbAlert = new StringBuilder();
					if (mPOAInfo != null && !mPOAInfo.isEmpty()) {
						// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect#48148 - Starts
						StringList slPOAToBeSubmitted = (StringList) mPOAInfo.get("sPOAObjId");
						// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect#48148 - End
						if (slPOAToBeSubmitted != null && !slPOAToBeSubmitted.isEmpty()) {
							mRequestMap.put("selectedPOAs", slPOAToBeSubmitted);
							mRequestMap.put("jobTitle", EnoviaResourceBundle.getProperty(context,
									"emxAWL.RTA.SIZEBASEIntegrationJobTitle"));
							mRequestMap.put("integrationType",
									EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.SIZEBASEIntegration"));
							StringList slPOANameForSubmitted = getNameFromID(context, slPOAToBeSubmitted, mPOAInfo);
							String[] args = { FrameworkUtil.join(slPOANameForSubmitted, ",") };
							String sAlert = MessageUtil.getMessage(context, null,
									"emxAWL.Alert.Notification.SendReqToRulesManager", args, null, context.getLocale(),
									AWLConstants.AWL_STRING_RESOURCE);
							// create job object and save all the poa ids
							JPO.invoke(context, "pgAAAExt_Util", null, "createRTAJobObjectForSizeBase",
									matrix.db.JPO.packArgs(mRequestMap), void.class);
							sbAlert.append(sAlert);
						}
						if (sbAlert.length() > 0) {
							jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sbAlert.toString());
						}
					} else {
						String sAlert = EnoviaResourceBundle.getProperty(context, AWLConstants.AWL_STRING_RESOURCE,
								context.getLocale(), "emxAWL.Alert.Notification.POANotSubmitted");
						jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
					}
					return jsonObjReturn.build().toString();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
			return jsonObjReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}

	}
	
	public static String retrieveDataForGPS(Context context, String paramString) throws Exception {
		JsonObjectBuilder jsonObjReturn = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
			String selectedPOAIds = jsonInputData.getString(RTAUtilConstants.POA_IDs);
			StringList slPOAs = null;

			String[] sArrSelectedPOAId = selectedPOAIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
			slPOAs = BusinessUtil.toStringList(sArrSelectedPOAId);
			// Added by RTA Capgemini Offshore for 22x.3 Dec_CW_04 Req R47874 - End
			int iSize = slPOAs.size();
			BusinessObject boConfigObject = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN,
					EnoviaResourceBundle.getProperty(context,
							"emxAWL.RTABusinessObjectName.ArtworkConfigurationDetails"),
					pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
			DomainObject domObject = DomainObject.newInstance(context, boConfigObject);
			int iMaxPOA = Integer.parseInt(domObject.getAttributeValue(context,
					PropertyUtil.getSchemaProperty(context, "attribute_pgMaxPOADetailsToRulesManager")));
			if (iSize > iMaxPOA) {
				String[] args = { String.valueOf(iMaxPOA) };
				String sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.MaxPOAsSelectedForRulesManager",
						args, null, context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
				jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
				return jsonObjReturn.build().toString();
			} else { // iSize can never be < 1 and hence this loop is for isize < iMaxPOA
				Map mRequestMap = new HashMap();
				mRequestMap.put("selectedPOAs", slPOAs);
				mRequestMap.put("integrationType",
						EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.GPSIntegration"));
				// Added by RTA Capgemini Offshore for 22x.3 Defect#54155 - Starts
				mRequestMap.put("IsFromAction", "Manual");
				String sInvalidPOA = JPO.invoke(context, "pgAAA_Util", null, "identifyInvalidPOA",
						matrix.db.JPO.packArgs(mRequestMap), String.class);
				if (BusinessUtil.isNotNullOrEmpty(sInvalidPOA)) {
					String[] args = new String[1];
					String sAlert = DomainConstants.EMPTY_STRING;
					args[0] = sInvalidPOA;
					sAlert = MessageUtil.getMessage(context, null, "emxAWL.Alert.Notification.InvalidPOA", args, null,
							context.getLocale(), AWLConstants.AWL_STRING_RESOURCE);
					jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
					return jsonObjReturn.build().toString();
				} else {
					Map mPOAInfo = JPO.invoke(context, "pgAAAExt_Util", null, "processPOAGPSData",
							matrix.db.JPO.packArgs(mRequestMap), Map.class);
					StringBuilder sbAlert = new StringBuilder();
					if (mPOAInfo != null && !mPOAInfo.isEmpty()) {
						// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect#48148 - Starts
						StringList slPOAToBeSubmitted = (StringList) mPOAInfo.get("sPOAObjId");
						// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect#48148 - End
						if (slPOAToBeSubmitted != null && !slPOAToBeSubmitted.isEmpty()) {
							mRequestMap.put("selectedPOAs", slPOAToBeSubmitted);
							mRequestMap.put("jobTitle",
									EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.GPSIntegrationJobTitle"));
							mRequestMap.put("integrationType",
									EnoviaResourceBundle.getProperty(context, "emxAWL.RTA.GPSIntegration"));
							StringList slPOANameForSubmitted = getNameFromID(context, slPOAToBeSubmitted, mPOAInfo);
							String[] args = { FrameworkUtil.join(slPOANameForSubmitted, ",") };
							String sAlert = MessageUtil.getMessage(context, null,
									"emxAWL.Alert.Notification.SendReqToRulesManager", args, null, context.getLocale(),
									AWLConstants.AWL_STRING_RESOURCE);
							// create job object and save all the poa ids
							JPO.invoke(context, "pgAAA_Util", null, "createJobObjectForReqToRulesManager",
									matrix.db.JPO.packArgs(mRequestMap), void.class);
							sbAlert.append(sAlert);
						}
						if (sbAlert.length() > 0) {
							jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sbAlert.toString());
						}
					} else {
						String sAlert = EnoviaResourceBundle.getProperty(context, AWLConstants.AWL_STRING_RESOURCE,
								context.getLocale(), "emxAWL.Alert.Notification.POANotSubmitted");
						jsonObjReturn.add(PGWidgetConstants.KEY_MESSAGE, sAlert);
					}
					return jsonObjReturn.build().toString();
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
			return jsonObjReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage()).build().toString();
		}
	}
	
}
