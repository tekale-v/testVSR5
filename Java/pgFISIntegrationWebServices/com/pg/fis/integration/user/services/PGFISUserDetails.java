package com.pg.fis.integration.user.services;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISIntegrationUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Base64;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import matrix.db.Context;
import matrix.util.StringList;

public class PGFISUserDetails {

	private static final Logger logger = Logger.getLogger(PGFISUserDetails.class.getName());
	static final String ERROR_Logger = "Exception in PGFISUserDetails";
	private static final String INVITATION_MESSAGE = "Welcome to 3DEXPERIENCE BIOVIA cloud platform";

	/**
	 * REST method to get User Details
	 * 
	 * @param Context    Context used to call API
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getUserDetailsResponse(Context context, String physicalId, String strEmail) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			DomainObject domObj = DomainObject.newInstance(context, physicalId);
			StringBuilder userData = new StringBuilder();

			StringList busSelects = new StringList();
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_EMAIL_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
			busSelects.add(DomainConstants.SELECT_CURRENT);
			busSelects.add(DomainConstants.SELECT_NAME);
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_EMPLOYEE_TYPE_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_CURRENT_REV_APPS);
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_APPS);
			
			StringList busMultiSelects = new StringList();
			busMultiSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_CURRENT_REV_APPS);
			busMultiSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_APPS);

			Map userDataMap = domObj.getInfo(context, busSelects, busMultiSelects);
			if (userDataMap != null) {
				String personRefURI = (String) userDataMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
				String emailAddress = (String) userDataMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_EMAIL_FROM_PERSON);
				String personState = (String) userDataMap.get(DomainConstants.SELECT_CURRENT);
				String personName = (String) userDataMap.get(DomainConstants.SELECT_NAME);
				String employeeType = (String) userDataMap
						.get(PGFISWSConstants.SELECT_SIGNUPFORM_EMPLOYEE_TYPE_FROM_PERSON);
				StringList userRoles = (StringList) userDataMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_CURRENT_REV_APPS);
				StringList userRolesfromPrevRev = (StringList) userDataMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_APPS);

				// User Email
				/*if(UIUtil.isNotNullAndNotEmpty(personRefURI) && UIUtil.isNotNullAndNotEmpty(personName)) {
					userData.append(personName);
					userData.append(";");
				} else*/
				if (UIUtil.isNotNullAndNotEmpty(emailAddress) && (UIUtil.isNullOrEmpty(strEmail) || PGFISWSConstants.CONST_TRUE.equalsIgnoreCase(strEmail))) {
					userData.append(emailAddress);
					userData.append(";");
				} else if (UIUtil.isNotNullAndNotEmpty(personName)) {
					userData.append(personName);
					userData.append(";");
				}

				// Rights for Member
				userData.append("0");
				userData.append(";");

				// Agreement
				if (UIUtil.isNotNullAndNotEmpty(employeeType)
						&& (employeeType.equals(PGFISWSConstants.CONST_EMPLOYEE) || employeeType.equals(PGFISWSConstants.CONST_NON_EMPLOYEE))) {
					userData.append("0");
					userData.append(";");
				} else {
					userData.append("1");
					userData.append(";");
				}
				// roles
				boolean isEnginuityRoleAssignedInOldRev = false;
				boolean isEnginuityRoleAssignedInNewRev = false;
				if(userRolesfromPrevRev != null && userRolesfromPrevRev.size() > 0 && userRolesfromPrevRev.toString().contains(PGFISWSConstants.CONST_FORMULA_INNOVATION_SUIT)) {
					isEnginuityRoleAssignedInOldRev = true;
				}
				if (userRoles != null && userRoles.size() > 0 && userRoles.toString().contains(PGFISWSConstants.CONST_FORMULA_INNOVATION_SUIT)) {
					isEnginuityRoleAssignedInNewRev = true;
				}
				if(isEnginuityRoleAssignedInOldRev && isEnginuityRoleAssignedInNewRev) {
					userData.append("BFS,CSV");
					userData.append(";");
				} else if(!isEnginuityRoleAssignedInOldRev && isEnginuityRoleAssignedInNewRev) {
					userData.append("BFS,CSV");
					userData.append(";");
				} else if(isEnginuityRoleAssignedInOldRev && !isEnginuityRoleAssignedInNewRev) {
					userData.append("#BFS,#CSV");
					userData.append(";");
				} else {
					userData.append(";");
				}
				/*
				 * if (UIUtil.isNotNullAndNotEmpty(userRoles) &&
				 * (userRoles.contains("Enginuity") || userRoles.contains("Formulator") ||
				 * userRoles.contains("Material Administrator"))) { userData.append("BFS,CSV");
				 * userData.append(";"); } else { userData.append(";"); }
				 */
				// email notification
				if(UIUtil.isNotNullAndNotEmpty(personRefURI)) {
					userData.append("false");
					userData.append(";");
				} else {
				userData.append("true");
				userData.append(";");
				}

				// remove
				if (UIUtil.isNotNullAndNotEmpty(personState) && personState.equals(PGFISWSConstants.CONST_STATE_ACTIVE)) {
					userData.append("0");
					userData.append(";");
				} else {
					userData.append("1");
					userData.append(";");
				}

				// passport
				userData.append("true");
				userData.append(";");

				// force
				userData.append("true");
				userData.append(";");

				// userLocation
				userData.append("null");

				String userDataString = userData.toString();
				String strWorkspacePath = context.createWorkspace();
				File fEmatrixWebRoot = new File(strWorkspacePath);
				File file = new File(fEmatrixWebRoot, "userDetails.csv");
				
				try (FileWriter outputfile = new FileWriter(file)){
					outputfile.write(userData.toString());
				} catch ( Exception ex) {
					throw new Exception(ex.getMessage());
				}

				String encodedString = getcsvAsBase64String(file.toString());
				output.add(PGFISWSConstants.JSON_TAG_DATA, encodedString);
				output.add(PGFISWSConstants.JSON_TAG_INVITATION_MSG, INVITATION_MESSAGE);
				file.deleteOnExit();
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}

	@SuppressWarnings("resource")
	private static String getcsvAsBase64String(String filename) throws Exception {
		String dataToReturn = "";
		try {
			int read;
			ByteArrayOutputStream bos_request = new ByteArrayOutputStream();
			BufferedInputStream streamdata = new BufferedInputStream(new FileInputStream(filename));
			while ((read = streamdata.read()) != -1)
				bos_request.write(read);
			byte[] csvAsByte = bos_request.toByteArray();
			if (csvAsByte != null) {
				dataToReturn = Base64.getEncoder().encodeToString(csvAsByte);
			}
		} catch (Exception ex) {
			throw new Exception("Exception Message: " + ex.getMessage());
		}
		return dataToReturn;
	}

	/**
	 * REST method to get User Details
	 * 
	 * @param Context    Context used to call API
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getUserDetailsForMigrationResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			DomainObject domObj = DomainObject.newInstance(context, physicalId);
			StringBuilder userData = new StringBuilder();

			StringList busSelects = new StringList();
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_EMAIL_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
			busSelects.add(DomainConstants.SELECT_CURRENT);
			busSelects.add(DomainConstants.SELECT_NAME);
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_EMPLOYEE_TYPE_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_ROLES_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_PREV_SIGNUPFORM_ROLES_FROM_PERSON);

			Map userDataMap = domObj.getInfo(context, busSelects);
			if (userDataMap != null) {
				String personRefURI = (String) userDataMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
				String emailAddress = (String) userDataMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_EMAIL_FROM_PERSON);
				String personState = (String) userDataMap.get(DomainConstants.SELECT_CURRENT);
				String personName = (String) userDataMap.get(DomainConstants.SELECT_NAME);
				String employeeType = (String) userDataMap
						.get(PGFISWSConstants.SELECT_SIGNUPFORM_EMPLOYEE_TYPE_FROM_PERSON);
				String userRoles = (String) userDataMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_ROLES_FROM_PERSON);
				String userRolesfromPrevRev = (String) userDataMap.get(PGFISWSConstants.SELECT_PREV_SIGNUPFORM_ROLES_FROM_PERSON);

				// User Email
				/*if(UIUtil.isNotNullAndNotEmpty(personRefURI) && UIUtil.isNotNullAndNotEmpty(personName)) {
					userData.append(personName);
					userData.append(";");
				} else*/
				if (UIUtil.isNotNullAndNotEmpty(emailAddress)) {
					userData.append(emailAddress);
					userData.append(";");
				} else if (UIUtil.isNotNullAndNotEmpty(personName)) {
					userData.append(personName);
					userData.append(";");
				}

				// Rights for Member
				
				userData.append("0");
				userData.append(";");

				// Agreement
				if (UIUtil.isNotNullAndNotEmpty(employeeType)
						&& (employeeType.equals(PGFISWSConstants.CONST_EMPLOYEE) || employeeType.equals(PGFISWSConstants.CONST_NON_EMPLOYEE))) {
					userData.append("0");
					userData.append(";");
				} else {
					userData.append("1");
					userData.append(";");
				}
				// roles
				
				 //For first time load via migration assign license by default
				 userData.append("BFS,CSV");
				 userData.append(";");
				 
				// email notification
				if(UIUtil.isNotNullAndNotEmpty(personRefURI)) {
					userData.append("false");
					userData.append(";");
				} else {
					userData.append("false");
					userData.append(";");
				}

				// remove
				//if (UIUtil.isNotNullAndNotEmpty(personState) && personState.equals("Active")) {
					userData.append("0");
					userData.append(";");
				/*} else {
					userData.append("1");
					userData.append(";");
				}*/

				// passport
				userData.append("true");
				userData.append(";");

				// force
				userData.append("true");
				userData.append(";");

				// userLocation
				userData.append("null");

				String userDataString = userData.toString();
				String strWorkspacePath = context.createWorkspace();
				File fEmatrixWebRoot = new File(strWorkspacePath);
				File file = new File(fEmatrixWebRoot, "userDetails.csv");
				try (FileWriter outputfile = new FileWriter(file)){
					outputfile.write(userData.toString());
				} catch ( Exception ex) {
					throw new Exception(ex.getMessage());
				}

				String encodedString = getcsvAsBase64String(file.toString());
				output.add(PGFISWSConstants.JSON_TAG_DATA, encodedString);
				output.add(PGFISWSConstants.JSON_TAG_INVITATION_MSG, INVITATION_MESSAGE);
				file.deleteOnExit();
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}
	
	
	/**
	 * REST method to get User Details
	 * 
	 * @param Context    Context used to call API
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	public static Response getUserDetailsForMigrationRevokeResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			DomainObject domObj = DomainObject.newInstance(context, physicalId);
			StringBuilder userData = new StringBuilder();

			StringList busSelects = new StringList();
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_EMAIL_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
			busSelects.add(DomainConstants.SELECT_CURRENT);
			busSelects.add(DomainConstants.SELECT_NAME);
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_EMPLOYEE_TYPE_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_SIGNUPFORM_ROLES_FROM_PERSON);
			busSelects.add(PGFISWSConstants.SELECT_PREV_SIGNUPFORM_ROLES_FROM_PERSON);

			Map userDataMap = domObj.getInfo(context, busSelects);
			if (userDataMap != null) {
				String personRefURI = (String) userDataMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
				String emailAddress = (String) userDataMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_EMAIL_FROM_PERSON);
				String personState = (String) userDataMap.get(DomainConstants.SELECT_CURRENT);
				String personName = (String) userDataMap.get(DomainConstants.SELECT_NAME);
				String employeeType = (String) userDataMap
						.get(PGFISWSConstants.SELECT_SIGNUPFORM_EMPLOYEE_TYPE_FROM_PERSON);
				String userRoles = (String) userDataMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_ROLES_FROM_PERSON);
				String userRolesfromPrevRev = (String) userDataMap.get(PGFISWSConstants.SELECT_PREV_SIGNUPFORM_ROLES_FROM_PERSON);

				// User Email
				/*if(UIUtil.isNotNullAndNotEmpty(personRefURI) && UIUtil.isNotNullAndNotEmpty(personName)) {
					userData.append(personName);
					userData.append(";");
				} else*/
				if (UIUtil.isNotNullAndNotEmpty(emailAddress)) {
					userData.append(emailAddress);
					userData.append(";");
				} else if (UIUtil.isNotNullAndNotEmpty(personName)) {
					userData.append(personName);
					userData.append(";");
				}

				// Rights for Member
				
				userData.append("0");
				userData.append(";");

				// Agreement
				if (UIUtil.isNotNullAndNotEmpty(employeeType)
						&& (employeeType.equals(PGFISWSConstants.CONST_EMPLOYEE) || employeeType.equals(PGFISWSConstants.CONST_NON_EMPLOYEE))) {
					userData.append("0");
					userData.append(";");
				} else {
					userData.append("1");
					userData.append(";");
				}
				// roles
				
				 //For first time load via migration revoke license once invited
				 userData.append("#BFS,#CSV");
				 userData.append(";");
				 
				// email notification
				
					userData.append("false");
					userData.append(";");
				 

				// remove
				//if (UIUtil.isNotNullAndNotEmpty(personState) && personState.equals("Active")) {
					userData.append("0");
					userData.append(";");
				/*} else {
					userData.append("1");
					userData.append(";");
				}*/

				// passport
				userData.append("true");
				userData.append(";");

				// force
				userData.append("true");
				userData.append(";");

				// userLocation
				userData.append("null");

				String userDataString = userData.toString();
				String strWorkspacePath = context.createWorkspace();
				File fEmatrixWebRoot = new File(strWorkspacePath);
				File file = new File(fEmatrixWebRoot, "userDetails.csv");
				try (FileWriter outputfile = new FileWriter(file)){
					outputfile.write(userData.toString());
				} catch ( Exception ex) {
					throw new Exception(ex.getMessage());
				}

				String encodedString = getcsvAsBase64String(file.toString());
				output.add(PGFISWSConstants.JSON_TAG_DATA, encodedString);
				output.add(PGFISWSConstants.JSON_TAG_INVITATION_MSG, INVITATION_MESSAGE);
				file.deleteOnExit();
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}
}
