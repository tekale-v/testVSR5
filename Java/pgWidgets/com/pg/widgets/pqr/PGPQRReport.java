/**
 * PGPQRReport.java : Class to fetch PQR data for EPs from 3dspace for PGPQRWidget
 * Copyright (c) Dassault Systemes.
 * All Rights Reserved.
 */
package com.pg.widgets.pqr;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGPQRReport {
	private static final String MEP_VENDOR = "to[" + DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY + "].from.name";
	private static final String SEP_VENDOR = "to[" + pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY + "].from.name";
	private static final String SEP_ALLOWED_MEP_TITLE = "from[" + DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT + "].to.attribute[" + DomainConstants.ATTRIBUTE_TITLE + "]";
	private static final String SEP_ALLOWED_MEP_NAME = "from[" + DomainConstants.RELATIONSHIP_MANUFACTURER_EQUIVALENT + "].to.name";
	private static final String QUAL_ID = "tomid[" + pgV3Constants.TYPE_QUALIFICATION + "].from.id";
	private static final String QUAL_NAME = "tomid[" + pgV3Constants.TYPE_QUALIFICATION + "].from.name";
	private static final String QUAL_STATE = "tomid[" + pgV3Constants.TYPE_QUALIFICATION + "].from.current";
	private static final String QUAL_DESC = "tomid[" + pgV3Constants.TYPE_QUALIFICATION + "].from.attribute[" + pgV3Constants.ATTRIBUTE_QUALIFICATIONDESCRIPTION + "]";
	private static final String QUAL_STATUS = "tomid[" + pgV3Constants.TYPE_QUALIFICATION + "].from.attribute[" + pgV3Constants.ATTRIBUTE_PGLOCATIONSTATUS + "]";
	private static final String QUAL_COMMENT = "tomid[" + pgV3Constants.TYPE_QUALIFICATION + "].from.attribute[" + pgV3Constants.ATTRIBUTE_COMMENT + "]";
	private static final String JSON_OUTPUT_KEY_EPNAME = "EPNAME";
	private static final String JSON_OUTPUT_KEY_EPTYPE = "EPTYPE";
	private static final String JSON_OUTPUT_KEY_EPREV = "EPREV";
	private static final String JSON_OUTPUT_KEY_EPTITLE = "EPTITLE";
	private static final String JSON_OUTPUT_KEY_DATA = "data";
	private static final String JSON_OUTPUT_KEY_ERROR = "error";
	private static final String JSON_OUTPUT_KEY_TRACE = "trace";
	private static final String JSON_OUTPUT_KEY_PQR_QUAL_NAME = "PQR Qualification Name";
	private static final String JSON_OUTPUT_KEY_PQR_QUAL_STATE = "PQR Qualification State";
	private static final String JSON_OUTPUT_KEY_PQR_QUAL_DESC = "PQR Qualification Description";
	private static final String JSON_OUTPUT_KEY_PQR_LOCATION_STATUS = "PQR Location Status";
	private static final String JSON_OUTPUT_KEY_COMMENTS = "Comments";
	private static final String JSON_OUTPUT_KEY_PQR_BUSINESS_AREA = "PQR Business Area";
	private static final String JSON_OUTPUT_KEY_PQR_PROD_CAT_PLATFORM = "PQR Product Category Platform";
	private static final String JSON_OUTPUT_KEY_PQR_PLANTS = "PQR Plants";
	private static final String JSON_OUTPUT_MEP_SEP_NAME = "MEP/SEP NAME";
	private static final String JSON_OUTPUT_REVISION = "Revision";
	private static final String JSON_OUTPUT_MEP_SEP_STATE = "MEP/SEP STATE";
	private static final String JSON_OUTPUT_MEP_SEP_TITLE = "MEP/SEP TITLE";
	private static final String JSON_OUTPUT_MEP_SEP_VENDOR = "MEP/SEP VENDOR";
	private static final String JSON_OUTPUT_ALLOWED_MEP_NAME = "Allowed MEP Name";
	private static final String JSON_OUTPUT_ALLOWED_MEP_TITLE = "Allowed MEP Title";
	private static final Logger logger = Logger.getLogger(PGPQRReport.class.getName());
	static final String ERROR_PQRREPORT_INCORRECTSECURITCONTEXT = "Exception in PGPQRReport : getSecurityContexts ::";
	static final String ERROR_PQRREPORT_MEPSEPDETAILS = "Exception in PGPQRReport : getMEPSEPDetailsJSON";
	
	static final String ALLOWED_TYPE = "AllowedTypes";
	static final String ALLOWED_STATES = "AllowedStates";
	static final String ENTERPRISE_ID = "EnterprisePartID";
	static final String BUILD_APPLICATION_JSON = "application/json";

	/**
	 * private constructor to hide the implicit public one
	 */
	private PGPQRReport() {
	}

	/**
	 * This method return exception traces for debugging
	 * 
	 * @param ex
	 *            Exception class object
	 * @return exception information
	 */
	private static String getExceptionTrace(Exception ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}

	/**
	 * The method returns the default security context of the user who has sent the request to display the PQR report in the widget
	 * 
	 * @param context
	 *            the Enovia Context object
	 * @return The default security context of the logged in user
	 */
	public static Object getSecurityContexts(Context context) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			JsonArrayBuilder outArr = Json.createArrayBuilder();
			String defaultSC = PersonUtil.getDefaultSecurityContext(context);
			if (UIUtil.isNullOrEmpty(defaultSC)) {
				Vector<?> resSCs = PersonUtil.getSecurityContextAssignments(context);
				if (resSCs != null && !resSCs.isEmpty()) {
					for (Object strSC : resSCs) {
						outArr.add((String) strSC);
					}
				}
			} else if (UIUtil.isNotNullAndNotEmpty(defaultSC)) {
				outArr.add(defaultSC);
			}
			output.add(JSON_OUTPUT_KEY_DATA, outArr);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_PQRREPORT_INCORRECTSECURITCONTEXT + ex.getMessage(), ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
		}
		return output;
	}

	/**
	 * The method creates the JSON object for the MEPS dropped in the widget. the input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @param args
	 *            Should have values for 'Allowed types', 'Allowed States', 'MEP ID'
	 * @return JSON object consisting of the information to be displayed on the PQR widget for the ID's dropped
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getMEPSEPDetailsJSON(Context context, String[] args) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		final String POLICY_PQR = PropertyUtil.getSchemaProperty(context, "policy_Qualification");
		final String STATE_PQR_APPROVED = PropertyUtil.getSchemaProperty(null, "policy", POLICY_PQR, "state_Approved");
		final String STATE_PQR_QUALIFIED = PropertyUtil.getSchemaProperty(null, "policy", POLICY_PQR, "state_Qualified");
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -10);
			Map<?, ?> programMap = JPO.unpackArgs(args);
			String strAllowedTypes = (String) programMap.get(ALLOWED_TYPE);
			StringList slAllowedTypes = FrameworkUtil.split(strAllowedTypes, ",");
			String strAllowedStates = (String) programMap.get(ALLOWED_STATES);
			String sObjectId = (String) programMap.get(ENTERPRISE_ID);
			StringList slObjectsIds = FrameworkUtil.split(sObjectId, ",");
			StringList slParentSelects = new StringList(7);
			StringList slChildSelects = new StringList(10);
			StringList slRelSelects = new StringList(6);
			addSelectables(slParentSelects, slChildSelects, slRelSelects);
			Pattern relPattern = new Pattern(pgV3Constants.RELATIONSHIP_MANUFACTUREREQUIVALENT);
			relPattern.addPattern(pgV3Constants.RELATIONSHIP_SUPPLIEREQUIVALENT);
			StringBuilder sbPQRName = new StringBuilder();
			StringBuilder sbPQRState = new StringBuilder();
			StringBuilder sbPQRDescription = new StringBuilder();
			StringBuilder sbPQRStatus = new StringBuilder();
			StringBuilder sbPQRBA = new StringBuilder();
			StringBuilder sbPQRPCP = new StringBuilder();
			StringBuilder sbPQRPlants = new StringBuilder();
			StringBuilder sbPQRComment = new StringBuilder();
			StringBuilder sbObjectWhere = new StringBuilder();
			DomainObject domEP = null;
			Map<?, ?> mpData = null;
			JsonArrayBuilder jsonArr = null;
			String strLanguage = context.getSession().getLanguage();
			String strMEPState;
			String strQualState;
			String strEPNAME;
			String strEPTYPE;
			String strEPREV;
			String strEPTITLE;
			String strEquivalentPartPolicy;
			String strEPTypeDisplayName;
			StringList slQualId;
			StringList slQualName;
			StringList slQualState;
			StringList slQualDesc;
			StringList slQualStatus;
			StringList slQualComment;
			Map<?, ?> hmMap = null;
			if (UIUtil.isNotNullAndNotEmpty(strAllowedStates)) {
				sbObjectWhere = buildWhereExp(strAllowedStates);
			}
			int nSize = slObjectsIds.size();
			for (int x = 0; x < nSize; x++) {
				domEP = DomainObject.newInstance(context, slObjectsIds.get(x));
				jsonArr = Json.createArrayBuilder();
				mpData = (Map<?, ?>) domEP.getInfo(context, slParentSelects);
				strEPNAME = (String) mpData.get(DomainConstants.SELECT_NAME);
				strEPTYPE = (String) mpData.get(DomainConstants.SELECT_TYPE);
				strEPREV = (String) mpData.get(DomainConstants.SELECT_REVISION);
				strEPTITLE = (String) mpData.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				strEPTypeDisplayName = EnoviaResourceBundle.getTypeI18NString(context, strEPTYPE, strLanguage);
				if (slAllowedTypes != null && !slAllowedTypes.contains(strEPTYPE)) {
					output.add(JSON_OUTPUT_KEY_ERROR, "Object of Type " + strEPTypeDisplayName + " is not Allowed ");
					return output.build().toString();
				}
				output.add(JSON_OUTPUT_KEY_EPNAME, strEPNAME);
				output.add(JSON_OUTPUT_KEY_EPTYPE, strEPTypeDisplayName);
				output.add(JSON_OUTPUT_KEY_EPREV, strEPREV);
				output.add(JSON_OUTPUT_KEY_EPTITLE, checkNullValueforString(strEPTITLE));
				MapList mlMEPSEPData = domEP.getRelatedObjects(context, relPattern.getPattern(), // relationshipPattern
						DomainConstants.QUERY_WILDCARD, // typePattern
						slChildSelects, // objectSelects
						slRelSelects, // relationshipSelects
						true, // getTo
						true, // getFrom
						(short) 1, // recurseToLevel
						sbObjectWhere.toString(), // objectWhere
						DomainConstants.EMPTY_STRING, // relationshipWhere
						0);// limit
				if (mlMEPSEPData != null && !mlMEPSEPData.isEmpty()) {
					for (int i = 0, size = mlMEPSEPData.size(); i < size; i++) {
						hmMap = (Map<?, ?>) mlMEPSEPData.get(i);
						if (hmMap != null) {
							strEquivalentPartPolicy = (String) hmMap.get(DomainConstants.SELECT_POLICY);
							strMEPState = EnoviaResourceBundle.getStateI18NString(context, strEquivalentPartPolicy, (String) hmMap.get(DomainConstants.SELECT_CURRENT),
									strLanguage);
							JsonObjectBuilder jsonObject = Json.createObjectBuilder();
							jsonObject.add("ObjectId", (String) hmMap.get(DomainConstants.SELECT_ID));
							jsonObject.add(JSON_OUTPUT_KEY_EPNAME, strEPNAME);
							jsonObject.add(JSON_OUTPUT_MEP_SEP_NAME, checkNullValueforString((String) hmMap.get(DomainConstants.SELECT_NAME)));
							jsonObject.add(JSON_OUTPUT_REVISION, checkNullValueforString((String) hmMap.get(DomainConstants.SELECT_REVISION)));
							jsonObject.add(JSON_OUTPUT_MEP_SEP_STATE, checkNullValueforString(strMEPState));
							jsonObject.add(JSON_OUTPUT_MEP_SEP_TITLE, checkNullValueforString((String) hmMap.get(DomainConstants.SELECT_ATTRIBUTE_TITLE)));
							if (DomainConstants.POLICY_MANUFACTURER_EQUIVALENT.equals(strEquivalentPartPolicy)) {
								jsonObject.add(JSON_OUTPUT_MEP_SEP_VENDOR, checkNullValueforString(FrameworkUtil.join(extractMultiValueSelect(hmMap, MEP_VENDOR), "|")));
								jsonObject.add(JSON_OUTPUT_ALLOWED_MEP_NAME, "");
								jsonObject.add(JSON_OUTPUT_ALLOWED_MEP_TITLE, "");
							} else if (pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equals(strEquivalentPartPolicy)) {
								jsonObject.add(JSON_OUTPUT_MEP_SEP_VENDOR, checkNullValueforString(FrameworkUtil.join(extractMultiValueSelect(hmMap, SEP_VENDOR), "|")));
								jsonObject.add(JSON_OUTPUT_ALLOWED_MEP_NAME, checkNullValueforString(FrameworkUtil.join(extractMultiValueSelect(hmMap, SEP_ALLOWED_MEP_NAME), "|")));
								jsonObject.add(JSON_OUTPUT_ALLOWED_MEP_TITLE, checkNullValueforString(FrameworkUtil.join(extractMultiValueSelect(hmMap, SEP_ALLOWED_MEP_TITLE), "|")));
							}
							sbPQRName.setLength(0);
							sbPQRState.setLength(0);
							sbPQRDescription.setLength(0);
							sbPQRStatus.setLength(0);
							sbPQRBA.setLength(0);
							sbPQRPCP.setLength(0);
							sbPQRPlants.setLength(0);
							sbPQRComment.setLength(0);
							slQualId = extractMultiValueSelect(hmMap, QUAL_ID);
							slQualName = extractMultiValueSelect(hmMap, QUAL_NAME);
							slQualState = extractMultiValueSelect(hmMap, QUAL_STATE);
							slQualDesc = extractMultiValueSelect(hmMap, QUAL_DESC);
							slQualStatus = extractMultiValueSelect(hmMap, QUAL_STATUS);
							slQualComment = extractMultiValueSelect(hmMap, QUAL_COMMENT);
							for (int j = 0, nQualSize = slQualId.size(); j < nQualSize; j++) {
								strQualState = slQualState.get(j);
								if (STATE_PQR_APPROVED.equals(strQualState) || STATE_PQR_QUALIFIED.equals(strQualState)) {
									sbPQRState.append(strQualState).append("|");
									sbPQRName.append((String) slQualName.get(j)).append("|");
									sbPQRDescription.append(slQualDesc.get(j)).append("|");
									sbPQRStatus.append(slQualStatus.get(j)).append("|");
									sbPQRComment.append(slQualComment.get(j)).append("|");
									addPQRRelatedInfo(context, slQualId.get(j), sbPQRBA, sbPQRPCP, sbPQRPlants);
								}
							}
							
						
							cleanData(sbPQRName, sbPQRState, sbPQRDescription, sbPQRStatus, sbPQRBA, sbPQRPCP,
									sbPQRPlants, sbPQRComment);
							
							jsonObject.add(JSON_OUTPUT_KEY_PQR_QUAL_NAME, checkNullValueforString(sbPQRName.toString()));
							jsonObject.add(JSON_OUTPUT_KEY_PQR_QUAL_STATE, checkNullValueforString(sbPQRState.toString()));
							jsonObject.add(JSON_OUTPUT_KEY_PQR_QUAL_DESC, checkNullValueforString(sbPQRDescription.toString()));
							jsonObject.add(JSON_OUTPUT_KEY_PQR_LOCATION_STATUS, checkNullValueforString(sbPQRStatus.toString()));
							jsonObject.add(JSON_OUTPUT_KEY_PQR_BUSINESS_AREA, checkNullValueforString(sbPQRBA.toString()));
							jsonObject.add(JSON_OUTPUT_KEY_PQR_PROD_CAT_PLATFORM, checkNullValueforString(sbPQRPCP.toString()));
							jsonObject.add(JSON_OUTPUT_KEY_PQR_PLANTS, checkNullValueforString(sbPQRPlants.toString()));
							jsonObject.add(JSON_OUTPUT_KEY_COMMENTS, checkNullValueforString(sbPQRComment.toString()));
							jsonArr.add(jsonObject);
						}
					}
				}
				JsonObjectBuilder jsonObjectIdValue = Json.createObjectBuilder();
				jsonObjectIdValue.add(JSON_OUTPUT_KEY_EPNAME, strEPNAME);
				jsonObjectIdValue.add(JSON_OUTPUT_KEY_EPTYPE, strEPTypeDisplayName);
				jsonObjectIdValue.add(JSON_OUTPUT_KEY_EPREV, strEPREV);
				jsonObjectIdValue.add(JSON_OUTPUT_KEY_EPTITLE, checkNullValueforString(strEPTITLE));
				jsonObjectIdValue.add(JSON_OUTPUT_KEY_DATA, jsonArr);
				output.add(slObjectsIds.get(x), jsonObjectIdValue);
			}
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, ERROR_PQRREPORT_MEPSEPDETAILS, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(JSON_OUTPUT_KEY_TRACE, getExceptionTrace(ex));
		}
		return output.build().toString();
	}

	private static void cleanData(StringBuilder sbPQRName, StringBuilder sbPQRState,
			StringBuilder sbPQRDescription, StringBuilder sbPQRStatus, StringBuilder sbPQRBA, StringBuilder sbPQRPCP,
			StringBuilder sbPQRPlants, StringBuilder sbPQRComment) {
		if(sbPQRBA.length()>0) {
			sbPQRBA.setLength(sbPQRBA.length()-1);
			
		}
		if(sbPQRPCP.length()>0) {
			sbPQRPCP.setLength(sbPQRPCP.length()-1);
			
		}
		if(sbPQRPlants.length()>0) {
			sbPQRPlants.setLength(sbPQRPlants.length()-1);
			
		}
		if(sbPQRState.length()>0) {
			sbPQRState.setLength(sbPQRState.length()-1);
			
		}
		if(sbPQRName.length()>0) {
			sbPQRName.setLength(sbPQRName.length()-1);
			
		}
		if(sbPQRDescription.length()>0) {
			sbPQRDescription.setLength(sbPQRDescription.length()-1);
			
		}
		if(sbPQRStatus.length()>0) {
			sbPQRStatus.setLength(sbPQRStatus.length()-1);
			
		}
		if(sbPQRComment.length()>0) {
			sbPQRComment.setLength(sbPQRComment.length()-1);
			
		}
	}

	private static StringList extractMultiValueSelect(Map<?, ?> infoMap, String strSelect) {
		StringList slMultiValues = new StringList();
		try {
			String strValue = (String) infoMap.get(strSelect);
			slMultiValues.add(strValue);
		} catch (ClassCastException castQualName) {
			slMultiValues = (StringList) infoMap.get(strSelect);
		}
		return slMultiValues;
	}

	private static void addSelectables(StringList slParentSelects, StringList slChildSelects, StringList slRelSelects) {
		slParentSelects.add(DomainConstants.SELECT_ID);
		slParentSelects.add(DomainConstants.SELECT_NAME);
		slParentSelects.add(DomainConstants.SELECT_TYPE);
		slParentSelects.add(DomainConstants.SELECT_REVISION);
		slParentSelects.add(DomainConstants.SELECT_POLICY);
		slParentSelects.add(DomainConstants.SELECT_CURRENT);
		slParentSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slChildSelects.add(DomainConstants.SELECT_MODIFIED);
		slChildSelects.add(DomainConstants.SELECT_ID);
		slChildSelects.add(DomainConstants.SELECT_NAME);
		slChildSelects.add(DomainConstants.SELECT_REVISION);
		slChildSelects.add(DomainConstants.SELECT_CURRENT);
		slChildSelects.add(DomainConstants.SELECT_POLICY);
		slChildSelects.add(MEP_VENDOR);
		slChildSelects.add(SEP_VENDOR);
		slChildSelects.add(SEP_ALLOWED_MEP_NAME);
		slChildSelects.add(SEP_ALLOWED_MEP_TITLE);
		slChildSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slRelSelects.add(QUAL_NAME);
		slRelSelects.add(QUAL_ID);
		slRelSelects.add(QUAL_STATE);
		slRelSelects.add(QUAL_DESC);
		slRelSelects.add(QUAL_STATUS);
		slRelSelects.add(QUAL_COMMENT);
	}

	private static StringBuilder buildWhereExp(String strAllowedStates) {
		StringBuilder sbObjectWhere = new StringBuilder();
		sbObjectWhere.append("(");
		StringList slallowedStates = FrameworkUtil.split(strAllowedStates, ",");
		int iAllowedStatesCount = slallowedStates.size();
		for (int i = 0; i < iAllowedStatesCount; i++) {
			StringList policyStateInfo = FrameworkUtil.split(slallowedStates.get(i), ".");
			sbObjectWhere.append(DomainConstants.SELECT_CURRENT).append(" == '").append(policyStateInfo.get(1)).append("' || ");
		}
		sbObjectWhere.delete(sbObjectWhere.lastIndexOf("||"), sbObjectWhere.length());
		sbObjectWhere.append(")");
		return sbObjectWhere;
	}

	/**
	 * The method is to add the information of the PQR to the MEP details The input paramters are: ID for PQR Returns the following with information,
	 * if associated with the PQR PQR Business Area, PQR Document Platform, PQR manufacturing responsibility
	 * 
	 * @param context
	 *            The enovia context object
	 * @param sPQRID
	 *            ObjectId of Qualification object
	 * @param sbPQRBA
	 *            Name of Qualification Business Area
	 * @param sbPQRPCP
	 *            Name of Product Category Platform
	 * @param sbPQRPlants
	 *            Name of Qualification Plant
	 * @throws FrameworkException
	 *             when operation fails
	 */
	private static void addPQRRelatedInfo(Context context, String sPQRID, StringBuilder sbPQRBA, StringBuilder sbPQRPCP, StringBuilder sbPQRPlants) throws FrameworkException {
		DomainObject domPQR = DomainObject.newInstance(context, sPQRID);
		String strPQRId = DomainConstants.SELECT_ID;
		StringList slPQRSelects = new StringList(DomainConstants.SELECT_NAME);
		slPQRSelects.add(strPQRId);
		Pattern relPattern = new Pattern(pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA);
		relPattern.addPattern(pgV3Constants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY);
		relPattern.addPattern(pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM);
		MapList mlPQRData = domPQR.getRelatedObjects(context, relPattern.getPattern(), // relationshipPattern
				DomainConstants.QUERY_WILDCARD, // typePattern
				slPQRSelects, // objectSelects
				null, // relationshipSelects
				true, // getTo
				true, // getFrom
				(short) 1, // recurseToLevel
				"", // objectWhere
				DomainConstants.EMPTY_STRING, // relationshipWhere
				0);// limit
		if (mlPQRData != null && !mlPQRData.isEmpty()) {
			Map<?, ?> hmMap = null;
			String strRelationship = null;
			for (int i = 0, size = mlPQRData.size(); i < size; i++) {
				hmMap = (Map<?, ?>) mlPQRData.get(i);
				strRelationship = (String) hmMap.get(DomainConstants.KEY_RELATIONSHIP);
				if (pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA.equals(strRelationship))
					sbPQRBA.append(hmMap.get(DomainConstants.SELECT_NAME)).append(",");
				else if (pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM.equals(strRelationship))
					sbPQRPCP.append(hmMap.get(DomainConstants.SELECT_NAME)).append(",");
				else if (pgV3Constants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY.equals(strRelationship))
					sbPQRPlants.append(hmMap.get(DomainConstants.SELECT_NAME)).append(",");
			}
		}
		updateQualificationData(sbPQRBA);
		updateQualificationData(sbPQRPCP);
		updateQualificationData(sbPQRPlants);
	}
	/**
	 * The method checks for the last character value for stringbuilder 
	 * if the value is "," then it is removed
	 * the symbol "|" is always appended to have the new line seperation in the JS file.
	 * @return strQualification 
	 */
	private static StringBuilder updateQualificationData (StringBuilder strQualification)
	{

		int strQualificationLength = strQualification.length();
		if (strQualificationLength > 0 && (strQualification.charAt(strQualificationLength-1) == ','))
		{
			strQualification.setLength(strQualificationLength - 1);
		}
			strQualification.append("|");
			return strQualification;
	}

	/**
	 * The method checks for the input string value returns "" if the value is null
	 * 
	 * @param strValue
	 *            any valid String
	 * @return strValue if not null, blank otherwise
	 */
	private static String checkNullValueforString(String strValue) {
		return null != strValue ? strValue : "";
	}

	/**
	 * The method has the following input parameters: Request from the session strAllowedStates: The Allowed states from the settings file Returns:
	 * The display name for the list of states
	 * 
	 * @param context
	 *            The enovia context object
	 * @param strAllowedStates
	 *            comma separated list of policy.state names
	 * @return json with policy.state name mapped to display value of the state
	 * @throws MatrixException
	 *             when operation fails
	 */
	public static String getAllowedStates(Context context, String strAllowedStates) throws MatrixException {
		String strLanguage = context.getSession().getLanguage();
		JsonObjectBuilder jsonObj = Json.createObjectBuilder();
		if (UIUtil.isNotNullAndNotEmpty(strAllowedStates)) {
			String[] strArrAllowedStates = strAllowedStates.split(",");
			for (int i = 0; i < strArrAllowedStates.length; i++) {
				String strAllowedState = strArrAllowedStates[i];
				String[] strArrPolicyState = strAllowedState.split("\\.");
				String strPolicy = strArrPolicyState[0];
				String strState = strArrPolicyState[1];
				String strStateDisplay = EnoviaResourceBundle.getStateI18NString(context, strPolicy, strState, strLanguage);
				if (UIUtil.isNotNullAndNotEmpty(strAllowedState)) {
					jsonObj.add(strAllowedState, strStateDisplay);
				}
			}
		}
		return jsonObj.build().toString();
	}
}
