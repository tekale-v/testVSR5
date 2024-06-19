package com.pg.fis.integration.securitymngt.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.json.JsonObject;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISIntegrationUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import matrix.db.Context;
import matrix.util.StringList;
import matrix.db.Page;
import java.io.InputStream;
import java.util.Properties;

public class PGFISSecurityMngtDetails {
	private static final String JSON_OUTPUT_KEY_ERROR = "error";
	private static final Logger logger = Logger.getLogger(PGFISSecurityMngtDetails.class.getName());
	static final String ERROR_Logger = "Exception in PGFISSecurityMngtDetails";
	private static String FIS_CLM_PERSON = "ngf2enovianonprod.im@pg.com";
	private static String FIS_ADMIN_PERSON = "fissysadmin.im@pg.com";

	private static Properties pageProps = null;
	/**
	 * REST method to get IP Control Class details by passing physical Id.
	 * 
	 * @param Context    Context used to call API
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	@SuppressWarnings("unchecked")
	public static Response getIPSecurityDetailsResponse(Context context, String physicalId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder groupArr = Json.createArrayBuilder();
		loadPropertyPage(context);
		if(pageProps != null) {
			FIS_CLM_PERSON = pageProps.getProperty("pgFIS.CloudCLM.User.EmailId");
			FIS_ADMIN_PERSON = pageProps.getProperty("pgFIS.CloudADMIN.User.EmailId");
		}
		
		boolean isContextPushed = false;
		try {
			
			StringList objectSelects = new StringList(DomainObject.SELECT_DESCRIPTION);
			objectSelects.add(DomainObject.SELECT_ATTRIBUTE_TITLE);
			objectSelects.add(DomainObject.SELECT_NAME);
			objectSelects.add(PGFISWSConstants.SELECT_LIBRARY_NAME);
			objectSelects.add(PGFISWSConstants.SELECT_PERSON_EMAIL_FROM_SECURITY_CLASSES);
			objectSelects.add(PGFISWSConstants.SELECT_PERSON_EMP_TYPE_FROM_SECURITY_CLASSES);
			objectSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES);
			objectSelects.add("from[" + PGFISWSConstants.REL_CLASSIFICATION_LICENSE + "].to.from["
					+ PGFISWSConstants.REL_LICENSED_PEOPLE + "].to.name");
			StringList multiSelects = new StringList();
			multiSelects.add(PGFISWSConstants.SELECT_PERSON_EMAIL_FROM_SECURITY_CLASSES);
			multiSelects.add(PGFISWSConstants.SELECT_PERSON_EMP_TYPE_FROM_SECURITY_CLASSES);
			multiSelects.add("from[" + PGFISWSConstants.REL_CLASSIFICATION_LICENSE + "].to.from["
					+ PGFISWSConstants.REL_LICENSED_PEOPLE + "].to.name");

			Map objectDetailsMap = new HashMap();
			DomainObject domObj = null;
			try {
				//Pushing content to fetch the required details from context object, as context user sometime not having read and show access.
				ContextUtil.pushContext(context);
				isContextPushed = true;
				domObj = DomainObject.newInstance(context, physicalId);
				objectDetailsMap = domObj.getInfo(context, objectSelects, multiSelects);
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
			}
			
			String strLibraryName = (String) objectDetailsMap.get(PGFISWSConstants.SELECT_LIBRARY_NAME);
			String strDescription = (String) objectDetailsMap.get(DomainObject.SELECT_DESCRIPTION);
			String strTitle = (String) objectDetailsMap.get(DomainObject.SELECT_ATTRIBUTE_TITLE);
			StringList members = (StringList) objectDetailsMap
					.get(PGFISWSConstants.SELECT_PERSON_EMAIL_FROM_SECURITY_CLASSES);
			StringList empTypeList = (StringList) objectDetailsMap
					.get(PGFISWSConstants.SELECT_PERSON_EMP_TYPE_FROM_SECURITY_CLASSES);
			String strProtectionType = (String) objectDetailsMap
					.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES);
			StringList empList = new StringList();
			StringList contractorList = new StringList();

			if (members != null && empTypeList != null) {
				for (int i = 0; i < empTypeList.size(); i++) {
					String empType = empTypeList.get(i);
					if (PGFISWSConstants.CONST_EMPLOYEE.equals(empType)) {
						empList.add(members.get(i));
					} else if (PGFISWSConstants.CONST_NON_EMP.equals(empType) || PGFISWSConstants.CONST_EBP.equals(empType)) {
						contractorList.add(members.get(i));
					}
				}
			}
			if (UIUtil.isNullOrEmpty(strTitle)) {
				strTitle = (String) objectDetailsMap.get(DomainObject.SELECT_NAME);
			}

			if (PGFISWSConstants.CONST_RESTRICTED.equals(strProtectionType)) {
				MapList personDetailList = new MapList();
				StringList slSelectable = new StringList();
				slSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_EMAIL_ADDRESS);
				StringBuffer sbWhere = new StringBuffer();
				sbWhere.append(PGFISWSConstants.SELECT_ATTRIBUTE_REFERENCE_URI);
				sbWhere.append(" != ");
				sbWhere.append("''");
				try {
					//Pushing content to allow search for Person, as context user sometime not having read and show access.
					ContextUtil.pushContext(context);
					isContextPushed = true;
					personDetailList = DomainObject.findObjects(context, 
							PGFISWSConstants.TYPE_PERSON,	// Type Pattern
							DomainConstants.QUERY_WILDCARD, // Name pattern
							DomainConstants.QUERY_WILDCARD, // Rev pattern
							DomainConstants.QUERY_WILDCARD,  // owner pattern
							PGFISWSConstants.VAULT_ESERVICE_PRODUCTION,// vault pattern
							sbWhere.toString(), // where expression
							false, // expand type
							slSelectable);// bus selects
				} finally {
					if (isContextPushed) {
						ContextUtil.popContext(context);
						isContextPushed = false;
					}
				}
				Iterator personITR = personDetailList.iterator();
				String personEmail = "";
				Map personMap = null;
				while (personITR.hasNext()) {
					personMap = (Map) personITR.next();
					personEmail = (String) personMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_EMAIL_ADDRESS);
					if (UIUtil.isNotNullAndNotEmpty(personEmail) && empList != null && !empList.contains(personEmail)) {
						empList.add(personEmail);
					}
				}
				String strCustomTitle = strTitle;
				if(UIUtil.isNotNullAndNotEmpty(strTitle)) {
					StringList slMemberList = new StringList(FIS_CLM_PERSON);
					slMemberList.add(FIS_ADMIN_PERSON);
					if(strTitle.indexOf("-R") > 0) {
						strCustomTitle = strTitle.substring(0, strTitle.indexOf("-R"));
						groupArr.add(generateGroupJson(strCustomTitle+"-R", strDescription, empList));
						groupArr.add(generateGroupJson(strCustomTitle+"("+PGFISWSConstants.CONST_NAME_NON_EMP+")-R", strDescription, contractorList));
						groupArr.add(generateGroupJson(strCustomTitle+"(CLM)-R", strDescription, slMemberList));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle+"-R", strDescription, strProtectionType));
					} else {
						groupArr.add(generateGroupJson(strCustomTitle, strDescription, empList));
						groupArr.add(generateGroupJson(strCustomTitle+"("+PGFISWSConstants.CONST_NAME_NON_EMP+")", strDescription, contractorList));
						groupArr.add(generateGroupJson(strCustomTitle+"(CLM)", strDescription, slMemberList));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle, strDescription, strProtectionType));
					}
					
				}
			} else if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(strProtectionType)) {
				if (empList != null) {
					empList.add(FIS_CLM_PERSON);
					empList.add(FIS_ADMIN_PERSON);
				} 
				String strCustomTitle = strTitle;
				if(UIUtil.isNotNullAndNotEmpty(strTitle)) {
					if(strTitle.indexOf("-HiR") > 0) {
						strCustomTitle = strTitle.substring(0, strTitle.indexOf("-HiR"));
						groupArr.add(generateGroupJson(strCustomTitle+"-HiR", strDescription, empList));
						groupArr.add(generateGroupJson(strCustomTitle+ "("+PGFISWSConstants.CONST_NAME_NON_EMP+")-HiR", strDescription, contractorList));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle+"-HiR", strDescription, strProtectionType));
					} else {
						groupArr.add(generateGroupJson(strCustomTitle, strDescription, empList));
						groupArr.add(generateGroupJson(strCustomTitle+ "("+PGFISWSConstants.CONST_NAME_NON_EMP+")", strDescription, contractorList));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle, strDescription, strProtectionType));
					}
				}
			} else if (PGFISWSConstants.CONST_NONE.equals(strProtectionType)
					&& (UIUtil.isNotNullAndNotEmpty(strLibraryName) &&  PGFISWSConstants.CONST_SPECIAL_PROJECTS.equals(strLibraryName))) {
				if (members != null) {
					members.add(FIS_CLM_PERSON);
					members.add(FIS_ADMIN_PERSON);
				} else if(members == null) {
					members = new StringList();
					members.add(FIS_CLM_PERSON);
					members.add(FIS_ADMIN_PERSON);
				}
				String strCustomTitle = strTitle;
				if (UIUtil.isNotNullAndNotEmpty(strTitle)) {
					groupArr.add(generateGroupJson(strCustomTitle, strDescription, members));
					output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
					output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS,
							generateSecureCollectionJson(strCustomTitle, strDescription, strProtectionType));
				} 
			}
			
		} catch (Exception ex) {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}

	private static JsonObject generateGroupJson(String strTitle, String strDescription, StringList members) {
		JsonObjectBuilder groupJSON = Json.createObjectBuilder();
		JsonArrayBuilder memberArr = Json.createArrayBuilder();
		groupJSON.add(PGFISWSConstants.JSON_TAG_TITLE, strTitle);
		// secureCollectionJson.add("name", strTitle);
		if (UIUtil.isNotNullAndNotEmpty(strDescription)) {
			groupJSON.add(PGFISWSConstants.JSON_TAG_DESC, strDescription);
			// secureCollectionJson.add("description", strDescription);
		}
		if (members != null) {
			for (String personEmail : members) {
				if(UIUtil.isNotNullAndNotEmpty(personEmail)) {
					memberArr.add(personEmail);
				}
			}
			groupJSON.add(PGFISWSConstants.JSON_TAG_MEMBERS, memberArr.build());
		}
		groupJSON.add(PGFISWSConstants.JSON_TAG_SHARING, "viewer");
		groupJSON.add(PGFISWSConstants.JSON_TAG_VISIBILITY, "public");

		return groupJSON.build();
	}

	private static JsonObject generateSecureCollectionJson(String strTitle, String strDescription,
			String strProtectionType) {
		JsonObjectBuilder secureCollectionJson = Json.createObjectBuilder();
		JsonArrayBuilder arrayOfGrpMember = Json.createArrayBuilder();
		JsonObjectBuilder grpMemberJson1 = Json.createObjectBuilder();
		JsonObjectBuilder grpMemberJson2 = Json.createObjectBuilder();
		JsonObjectBuilder grpMemberJson3 = Json.createObjectBuilder();
		JsonArrayBuilder arrayOfResponsibility1 = Json.createArrayBuilder();
		JsonArrayBuilder arrayOfResponsibility2 = Json.createArrayBuilder();
		JsonArrayBuilder arrayOfResponsibility3 = Json.createArrayBuilder();

		JsonObjectBuilder responsibilityJson1 = Json.createObjectBuilder();
		JsonObjectBuilder responsibilityJson2 = Json.createObjectBuilder();
		JsonObjectBuilder responsibilityJson3 = Json.createObjectBuilder();
		JsonObjectBuilder responsibilityJson4 = Json.createObjectBuilder();
		secureCollectionJson.add(PGFISWSConstants.JSON_TAG_NAME, strTitle);
		if (UIUtil.isNotNullAndNotEmpty(strDescription)) {
			secureCollectionJson.add(PGFISWSConstants.JSON_TAG_DESC, strDescription);
		}
		if (PGFISWSConstants.CONST_RESTRICTED.equals(strProtectionType)) {
			responsibilityJson1.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Viewer");
			responsibilityJson2.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Viewer");
			responsibilityJson3.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Author");
			responsibilityJson4.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Collaborator");
		arrayOfResponsibility1.add(responsibilityJson1);
		arrayOfResponsibility2.add(responsibilityJson2);
		arrayOfResponsibility3.add(responsibilityJson3);
		arrayOfResponsibility3.add(responsibilityJson4);
		grpMemberJson1.add(PGFISWSConstants.JSON_TAG_RESPONSIBILITIES, arrayOfResponsibility1.build());
		grpMemberJson2.add(PGFISWSConstants.JSON_TAG_RESPONSIBILITIES, arrayOfResponsibility2.build());
		grpMemberJson3.add(PGFISWSConstants.JSON_TAG_RESPONSIBILITIES, arrayOfResponsibility3.build());
		arrayOfGrpMember.add(grpMemberJson1);
		arrayOfGrpMember.add(grpMemberJson2);
		arrayOfGrpMember.add(grpMemberJson3);
		} else if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(strProtectionType)) {
			responsibilityJson1.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Author");
			responsibilityJson2.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Author");
			responsibilityJson3.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Collaborator");
			arrayOfResponsibility1.add(responsibilityJson1);
			arrayOfResponsibility1.add(responsibilityJson3);
			arrayOfResponsibility2.add(responsibilityJson2);
			arrayOfResponsibility2.add(responsibilityJson3);
			grpMemberJson1.add(PGFISWSConstants.JSON_TAG_RESPONSIBILITIES, arrayOfResponsibility1.build());
			grpMemberJson2.add(PGFISWSConstants.JSON_TAG_RESPONSIBILITIES, arrayOfResponsibility2.build());
			arrayOfGrpMember.add(grpMemberJson1);
			arrayOfGrpMember.add(grpMemberJson2);
		} else if(PGFISWSConstants.CONST_NONE.equals(strProtectionType)) {
			responsibilityJson1.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Author");
			responsibilityJson2.add(PGFISWSConstants.JSON_TAG_ID, "dsbioseccol:LogicalResponsibility.Collaborator");
			arrayOfResponsibility1.add(responsibilityJson1);
			arrayOfResponsibility1.add(responsibilityJson2);
			grpMemberJson1.add(PGFISWSConstants.JSON_TAG_RESPONSIBILITIES, arrayOfResponsibility1.build());
			arrayOfGrpMember.add(grpMemberJson1);
		}
		secureCollectionJson.add(PGFISWSConstants.JSON_TAG_MEMBERS, arrayOfGrpMember.build());
		return secureCollectionJson.build();
	}

	/**
	 * REST method to get IP/Security Control Class list from User ID
	 * 
	 * @param Context Context used to call API
	 * @param userId  Email Id/trigram Of User
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	@SuppressWarnings("unchecked")
	public static Response getIPSecurityDetailsFromUserIdResponse(Context context, String userId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder groupArr = Json.createArrayBuilder();
		loadPropertyPage(context);
		boolean isContextPushed = false;
		try {
			StringBuffer sbWhere = new StringBuffer();
			sbWhere.append(PGFISWSConstants.SELECT_ATTRIBUTE_EMAIL_ADDRESS);
			sbWhere.append(" == ");
			sbWhere.append(userId);
			sbWhere.append(" || name == ");
			sbWhere.append(userId);
			StringList slSelectable = new StringList();
			slSelectable.add(PGFISWSConstants.SELECT_GROUP_URI_OF_SECURITY_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_NAME_OF_SECURITY_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_HIGH_RESTRICTED_SEC_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_RESTRICTED_SEC_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_SPGS_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_PERSON);
			slSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_SECURITY_EMP_TYPE);
			slSelectable.add(PGFISWSConstants.SELECT_CONTRACT_GROUP_URI_OF_SECURITY_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_APPS);
			slSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_CURRENT_REV_APPS);

			StringList MultiSelectable = new StringList();
			MultiSelectable.add(PGFISWSConstants.SELECT_GROUP_URI_OF_SECURITY_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_NAME_OF_SECURITY_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_CONTRACT_GROUP_URI_OF_SECURITY_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_PERSON);
			MultiSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_HIGH_RESTRICTED_SEC_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_RESTRICTED_SEC_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_APPS);
			MultiSelectable.add(PGFISWSConstants.SELECT_SIGNUPFORM_CURRENT_REV_APPS);
			MapList personDetailList = new MapList();
			try {
				//Pushing content to allow search for Person, as context user sometime not having read and show access.
				ContextUtil.pushContext(context);
				isContextPushed = true;
				personDetailList = DomainObject.findObjects(context, 
						PGFISWSConstants.TYPE_PERSON, // Type Pattern
						DomainConstants.QUERY_WILDCARD, // Name pattern
						DomainConstants.QUERY_WILDCARD, // Rev pattern
						DomainConstants.QUERY_WILDCARD, // owner pattern
						PGFISWSConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern
						sbWhere.toString(),// where expression
						false,  // expand type
						slSelectable, // bus selects
						MultiSelectable); // bus multi selects
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
			}
			if (personDetailList != null && personDetailList.size() != 0 && !personDetailList.isEmpty()) {
				Map<String, ?> personMap = (Map<String, ?>) personDetailList.get(0);
				StringList cloudGrpupsURIList = (StringList) personMap
						.get(PGFISWSConstants.SELECT_GROUP_URI_OF_SECURITY_CLASSES);
				StringList highRestrictedSecClass = (StringList) personMap
						.get(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_HIGH_RESTRICTED_SEC_CLASSES);
				StringList restrictedSecClass = (StringList) personMap
						.get(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_RESTRICTED_SEC_CLASSES);
				String spgsSecClass = (String) personMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_SPGS_CLASSES);
				StringList strSecClassName = (StringList) personMap
						.get(PGFISWSConstants.SELECT_NAME_OF_SECURITY_CLASSES);
				StringList secClassesTypeList = (StringList) personMap
						.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_PERSON);
				StringList cloudContractGrpupsURIList = (StringList) personMap
						.get(PGFISWSConstants.SELECT_CONTRACT_GROUP_URI_OF_SECURITY_CLASSES);
				String employeeType = (String) personMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_SECURITY_EMP_TYPE);
				StringList prevRevApplications = (StringList) personMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_PREV_REV_APPS);
				StringList currentRevApplications = (StringList) personMap.get(PGFISWSConstants.SELECT_SIGNUPFORM_CURRENT_REV_APPS);
				boolean isNewUserInvitation = false;
				if(prevRevApplications != null && currentRevApplications != null && !prevRevApplications.toString().contains(PGFISWSConstants.CONST_FIS) && currentRevApplications.toString().contains(PGFISWSConstants.CONST_FIS)) {
					isNewUserInvitation = true;
				}

				HashMap<String, String> currentSecClassDetailsMap = new HashMap<String, String>();
				HashMap<String, String> noneSecClassDetailsMap = new HashMap<String, String>();
				HashMap<String, String> nonEmpSecClassDetailsMap = new HashMap<String, String>();
				if (secClassesTypeList != null && strSecClassName != null) {
					for (int i = 0; i < secClassesTypeList.size(); i++) {
						String classType = secClassesTypeList.get(i);
						if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(classType) && PGFISWSConstants.CONST_EMPLOYEE.equals(employeeType) && cloudGrpupsURIList != null) {
							currentSecClassDetailsMap.put(strSecClassName.get(i), cloudGrpupsURIList.get(i));
						} else if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(classType)
								&& (PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType)) && cloudContractGrpupsURIList != null) {
							currentSecClassDetailsMap.put(strSecClassName.get(i), cloudContractGrpupsURIList.get(i));
						} else if (PGFISWSConstants.CONST_RESTRICTED.equals(classType)
								&& (PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType)) && cloudContractGrpupsURIList != null) {
							nonEmpSecClassDetailsMap.put(strSecClassName.get(i), cloudContractGrpupsURIList.get(i));
						} else if (PGFISWSConstants.CONST_NONE.equals(classType) && cloudGrpupsURIList != null) {
							noneSecClassDetailsMap.put(strSecClassName.get(i), cloudGrpupsURIList.get(i));
						}
					}
				}
				StringList previousRevSecClassList = new StringList();
				if (highRestrictedSecClass != null && !highRestrictedSecClass.isEmpty()) {
					previousRevSecClassList.addAll(highRestrictedSecClass);
				}
				if (restrictedSecClass != null && !restrictedSecClass.isEmpty() ) {
					previousRevSecClassList.addAll(restrictedSecClass);
				}
				if (UIUtil.isNotNullAndNotEmpty(spgsSecClass)) {
					StringList spgsSecClassList = FrameworkUtil.split(spgsSecClass, ",");
					previousRevSecClassList.addAll(spgsSecClassList);
				}
				StringList removedSecClassList = new StringList();
				if(!isNewUserInvitation) {
					removedSecClassList.addAll(previousRevSecClassList);
					if (strSecClassName != null) {
						removedSecClassList.removeAll(strSecClassName);
						strSecClassName.removeAll(previousRevSecClassList);
					}
				}
				
				StringBuffer secClassNameSB = new StringBuffer();
				for (String secClassName : removedSecClassList) {
					secClassNameSB.append(secClassName);
					secClassNameSB.append(",");
				}
				StringList selectable = new StringList();
				selectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_GROUP_REFERENCE_URI);
				selectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_CONTRACT_GROUP_REFERENCE_URI);
				selectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES);
				StringBuffer sbType = new StringBuffer();
				sbType.append(PGFISWSConstants.TYPE_IP_CONTROL_CLASS);
				sbType.append(",");
				sbType.append(PGFISWSConstants.TYPE_SECURITY_CONTROL_CLASS);
				String strWhere = DomainConstants.EMPTY_STRING;
				if (PGFISWSConstants.CONST_EMPLOYEE.equals(employeeType)) {
					strWhere = PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES +" != "+ PGFISWSConstants.CONST_RESTRICTED;
				}
				MapList prevRevSecClassMapList = new MapList();
				try {
					if (!secClassNameSB.toString().isEmpty()) {
						//Pushing content to allow connection of IP/Security Control class as Integration user context will have no access
						ContextUtil.pushContext(context);
						isContextPushed = true;
						prevRevSecClassMapList = PGFISIntegrationUtil.findObjectAndRetunDetails(context,
								sbType.toString(), secClassNameSB.toString(), selectable, strWhere);
					}
				} finally {
					if (isContextPushed) {
						ContextUtil.popContext(context);
						isContextPushed = false;
					}
				}
				Iterator itr = prevRevSecClassMapList.iterator();
				String strCloudURIforGroup = "";
				while (itr.hasNext()) {
					Map objMap = (Map) itr.next();
					String classType = (String) objMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES);
					if (PGFISWSConstants.CONST_EMPLOYEE.equals(employeeType) || PGFISWSConstants.CONST_NONE.equals(classType)) {
						strCloudURIforGroup = (String) objMap
								.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_GROUP_REFERENCE_URI);
					} else if (PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType)) {
						strCloudURIforGroup = (String) objMap
								.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_CONTRACT_GROUP_REFERENCE_URI);
					}
					if(UIUtil.isNotNullAndNotEmpty(strCloudURIforGroup)) {
						JsonObjectBuilder patchJSON = Json.createObjectBuilder();
						JsonObjectBuilder groupJSON = Json.createObjectBuilder();
						JsonArrayBuilder patchArray = Json.createArrayBuilder();
						JsonArrayBuilder valueArray = Json.createArrayBuilder();
						patchJSON.add(PGFISWSConstants.JSON_TAG_OPERATION, "remove");
						patchJSON.add(PGFISWSConstants.JSON_TAG_FIELD, "members");
						valueArray.add(userId);
						patchJSON.add(PGFISWSConstants.JSON_TAG_VALUE, valueArray);
						patchArray.add(patchJSON);
						groupJSON.add(strCloudURIforGroup, patchArray);
						groupArr.add(groupJSON);
					}

				}
				if (strSecClassName != null) {
					if (PGFISWSConstants.CONST_EMPLOYEE.equals(employeeType)) {
						generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_RESTRICTED, groupArr, userId,
								new StringList(), new HashMap());
					} else if ((PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType))) {
						generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_RESTRICTED, groupArr, userId,
								strSecClassName, nonEmpSecClassDetailsMap);
					}
					if (currentSecClassDetailsMap != null && !currentSecClassDetailsMap.isEmpty()) {
					generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_HIGHLY_RESTRICTED, groupArr, userId,
							strSecClassName, currentSecClassDetailsMap);
					}
					if (noneSecClassDetailsMap != null && !noneSecClassDetailsMap.isEmpty()) {
						generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_NONE, groupArr, userId, strSecClassName,
								noneSecClassDetailsMap);
					}

				}
				addPersonToDefaultGroupCollection(context, employeeType, groupArr, userId);
				output.add(PGFISWSConstants.JSON_TAG_USER_GROUPS, groupArr);
			} else {
				String message = PGFISWSConstants.PERSON_NOT_FOUND + userId;
				output.add(PGFISWSConstants.STRING_MESSAGE, message);
				return Response.status(Response.Status.BAD_REQUEST).entity(output.build().toString()).build();
			}

		} catch (Exception ex) {
			if (isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}

	private static void generateSecurityGroupJsonArray(Context context, String employeeType, String secClassType,
			JsonArrayBuilder groupArr, String userId, StringList strSecClassName, Map<String, String> currentSecClassDetailsMap)
			throws Exception {
		boolean isContextPushed = false;
		if (PGFISWSConstants.CONST_RESTRICTED.equals(secClassType) && PGFISWSConstants.CONST_EMPLOYEE.equals(employeeType)) {
			StringList selectables = new StringList();
			selectables.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_GROUP_REFERENCE_URI);
			StringBuffer strType = new StringBuffer();
			strType.append(PGFISWSConstants.TYPE_IP_CONTROL_CLASS);
			strType.append(",");
			strType.append(PGFISWSConstants.TYPE_SECURITY_CONTROL_CLASS);
			//String strWhere = PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES + " == Restricted && current == Active";
			String strWhere = PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES + " == Restricted";
			MapList restrictedSecClassMapList = new MapList();
			try {
				//Pushing content to allow connection of IP/Security Control class as Integration user context will have no access
				ContextUtil.pushContext(context);
				isContextPushed = true;
				restrictedSecClassMapList = PGFISIntegrationUtil.findObjectAndRetunDetails(context, strType.toString(),
						DomainConstants.QUERY_WILDCARD, selectables, strWhere);
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
			}
			if (restrictedSecClassMapList != null) {
				String strCloudGroupURI = "";
				Iterator iterator = restrictedSecClassMapList.iterator();
				while (iterator.hasNext()) {
					Map objMap = (Map) iterator.next();

					strCloudGroupURI = (String) objMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_GROUP_REFERENCE_URI);
					if(UIUtil.isNotNullAndNotEmpty(strCloudGroupURI)) {
						JsonObjectBuilder patchJSON = Json.createObjectBuilder();
						JsonObjectBuilder groupJSON = Json.createObjectBuilder();
						JsonArrayBuilder patchArray = Json.createArrayBuilder();
						JsonArrayBuilder valueArray = Json.createArrayBuilder();
						patchJSON.add(PGFISWSConstants.JSON_TAG_OPERATION, "add");
						patchJSON.add(PGFISWSConstants.JSON_TAG_FIELD, "members");
						valueArray.add(userId);
						patchJSON.add(PGFISWSConstants.JSON_TAG_VALUE, valueArray);
						patchArray.add(patchJSON);
						groupJSON.add(strCloudGroupURI, patchArray);
						groupArr.add(groupJSON);
					}
				}
			}
		} else if (PGFISWSConstants.CONST_RESTRICTED.equals(secClassType) && strSecClassName != null && currentSecClassDetailsMap != null && (PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType))) {
			String attributeTitle = "";
			for (String secClass : strSecClassName) {
				if (null != currentSecClassDetailsMap && currentSecClassDetailsMap.containsKey(secClass)) {
					attributeTitle = (String) currentSecClassDetailsMap.get(secClass);
					if(UIUtil.isNotNullAndNotEmpty(attributeTitle)) {
						JsonObjectBuilder patchJSON = Json.createObjectBuilder();
						JsonObjectBuilder groupJSON = Json.createObjectBuilder();
						JsonArrayBuilder patchArray = Json.createArrayBuilder();
						JsonArrayBuilder valueArray = Json.createArrayBuilder();
						patchJSON.add(PGFISWSConstants.JSON_TAG_OPERATION, "add");
						patchJSON.add(PGFISWSConstants.JSON_TAG_FIELD, "members");
						valueArray.add(userId);
						patchJSON.add(PGFISWSConstants.JSON_TAG_VALUE, valueArray);
						patchArray.add(patchJSON);
						groupJSON.add(attributeTitle, patchArray);
						groupArr.add(groupJSON);
					}
				}
			}
		} else if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(secClassType) && strSecClassName != null) {
			String attributeTitle = "";
			for (String secClass : strSecClassName) {
				if (null != currentSecClassDetailsMap && currentSecClassDetailsMap.containsKey(secClass)) {
					attributeTitle = (String) currentSecClassDetailsMap.get(secClass);
					if(UIUtil.isNotNullAndNotEmpty(attributeTitle)) {
						JsonObjectBuilder patchJSON = Json.createObjectBuilder();
						JsonObjectBuilder groupJSON = Json.createObjectBuilder();
						JsonArrayBuilder patchArray = Json.createArrayBuilder();
						JsonArrayBuilder valueArray = Json.createArrayBuilder();
						patchJSON.add(PGFISWSConstants.JSON_TAG_OPERATION, "add");
						patchJSON.add(PGFISWSConstants.JSON_TAG_FIELD, "members");
						valueArray.add(userId);
						patchJSON.add(PGFISWSConstants.JSON_TAG_VALUE, valueArray);
						patchArray.add(patchJSON);
						groupJSON.add(attributeTitle, patchArray);
						groupArr.add(groupJSON);
					}
				}
			}
		} else if (PGFISWSConstants.CONST_NONE.equals(secClassType) && strSecClassName != null) {
			String attributeTitle = "";
			for (String secClass : strSecClassName) {
				if (null != currentSecClassDetailsMap && currentSecClassDetailsMap.containsKey(secClass)) {
					attributeTitle = (String) currentSecClassDetailsMap.get(secClass);
					if(UIUtil.isNotNullAndNotEmpty(attributeTitle)) {
						JsonObjectBuilder patchJSON = Json.createObjectBuilder();
						JsonObjectBuilder groupJSON = Json.createObjectBuilder();
						JsonArrayBuilder patchArray = Json.createArrayBuilder();
						JsonArrayBuilder valueArray = Json.createArrayBuilder();
						patchJSON.add(PGFISWSConstants.JSON_TAG_OPERATION, "add");
						patchJSON.add(PGFISWSConstants.JSON_TAG_FIELD, "members");
						valueArray.add(userId);
						patchJSON.add(PGFISWSConstants.JSON_TAG_VALUE, valueArray);
						patchArray.add(patchJSON);
						groupJSON.add(attributeTitle, patchArray);
						groupArr.add(groupJSON);
					}

				}
			}
		}
	}
	
	public static void addPersonToDefaultGroupCollection(Context context, String employeeType, JsonArrayBuilder groupArr, String userId) throws Exception{
		try {
			
			String sSubstanceRGroupURI = "";
			String sNewMaterialRGroupURI = "";
			if(null != pageProps) {
				sSubstanceRGroupURI = pageProps.getProperty("pgFIS.SubstanceR.group.fisid");
				sNewMaterialRGroupURI = pageProps.getProperty("pgFIS.NewMaterialR.group.fisid");
			}
			JsonObjectBuilder patchJSON = Json.createObjectBuilder();
			JsonObjectBuilder groupJSON = Json.createObjectBuilder();
			JsonArrayBuilder patchArray = Json.createArrayBuilder();
			JsonArrayBuilder valueArray = Json.createArrayBuilder();
			if(UIUtil.isNotNullAndNotEmpty(sSubstanceRGroupURI)) {
				patchJSON.add(PGFISWSConstants.JSON_TAG_OPERATION, "add");
				patchJSON.add(PGFISWSConstants.JSON_TAG_FIELD, "members");
				valueArray.add(userId);
				patchJSON.add(PGFISWSConstants.JSON_TAG_VALUE, valueArray);
				patchArray.add(patchJSON);
				groupJSON.add(sSubstanceRGroupURI, patchArray);
				groupArr.add(groupJSON);
			}

			if ( UIUtil.isNotNullAndNotEmpty(sNewMaterialRGroupURI)) {
				patchJSON = Json.createObjectBuilder();
				groupJSON = Json.createObjectBuilder();
				patchArray = Json.createArrayBuilder();
				valueArray = Json.createArrayBuilder();
				patchJSON.add(PGFISWSConstants.JSON_TAG_OPERATION, "add");
				patchJSON.add(PGFISWSConstants.JSON_TAG_FIELD, "members");
				valueArray.add(userId);
				patchJSON.add(PGFISWSConstants.JSON_TAG_VALUE, valueArray);
				patchArray.add(patchJSON);
				groupJSON.add(sNewMaterialRGroupURI, patchArray);
				groupArr.add(groupJSON);
			}
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
			
		}
	}

	public static void loadPropertyPage(Context context) throws Exception {
		try {
			
			String pageName = "pgFIS.properties";
			//Read Page Object
			Page pageIn = new Page(pageName);
			pageIn.open(context);
			InputStream inputStream = pageIn.getContentsAsStream(context);
			if(inputStream != null) {
				pageProps = new Properties();
				pageProps.load(inputStream);
			} else {
				logger.info("Page object " + pageName + " not found");
			}
			
		} catch(Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	/**
	 * REST method to get All IP/Security Control Class list from User ID
	 * 
	 * @param Context Context used to call API
	 * @param userId  Email Id/trigram Of User
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	@SuppressWarnings("unchecked")
	public static Response getAllIPSecurityDetailsFromUserIdResponse(Context context, String userId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder groupArr = Json.createArrayBuilder();
		boolean isContextPushed = false;
		try {
			loadPropertyPage(context);
			StringBuffer sbWhere = new StringBuffer();
			sbWhere.append(PGFISWSConstants.SELECT_ATTRIBUTE_EMAIL_ADDRESS);
			sbWhere.append(" == ");
			sbWhere.append(userId);
			sbWhere.append(" || name == ");
			sbWhere.append(userId);
			StringList slSelectable = new StringList();
			slSelectable.add(PGFISWSConstants.SELECT_GROUP_URI_OF_SECURITY_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_NAME_OF_SECURITY_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_CONTRACT_GROUP_URI_OF_SECURITY_CLASSES);
			slSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_SECURITY_EMP_TYPE);
			slSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_PERSON);

			StringList MultiSelectable = new StringList();
			MultiSelectable.add(PGFISWSConstants.SELECT_GROUP_URI_OF_SECURITY_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_NAME_OF_SECURITY_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_CONTRACT_GROUP_URI_OF_SECURITY_CLASSES);
			MultiSelectable.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_PERSON);
			MapList personDetailList = new MapList();
			try {
				//Pushing content to allow search for Person, as context user sometime not having read and show access.
				ContextUtil.pushContext(context);
				isContextPushed = true;
				personDetailList = DomainObject.findObjects(context, 
						PGFISWSConstants.TYPE_PERSON, // Type Pattern
						DomainConstants.QUERY_WILDCARD,  // Name pattern
						DomainConstants.QUERY_WILDCARD,  // Rev pattern
						DomainConstants.QUERY_WILDCARD,  // owner pattern
						PGFISWSConstants.VAULT_ESERVICE_PRODUCTION,  // vault pattern
						sbWhere.toString(),	// where expression
						false, 	// expand type
						slSelectable, // bus selects
						MultiSelectable);// bus multi selects
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
			}
			if (personDetailList != null && personDetailList.size() != 0 && !personDetailList.isEmpty()) {
				Map<String, ?> personMap = (Map<String, ?>) personDetailList.get(0);
				StringList cloudGrpupsURIList = (StringList) personMap
						.get(PGFISWSConstants.SELECT_GROUP_URI_OF_SECURITY_CLASSES);
				StringList strSecClassName = (StringList) personMap
						.get(PGFISWSConstants.SELECT_NAME_OF_SECURITY_CLASSES);
				StringList cloudContractGrpupsURIList = (StringList) personMap
						.get(PGFISWSConstants.SELECT_CONTRACT_GROUP_URI_OF_SECURITY_CLASSES);
				StringList secClassesTypeList = (StringList) personMap
						.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_PERSON);
				String employeeType = (String) personMap.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_SECURITY_EMP_TYPE);

				HashMap<String, String> currentSecClassDetailsMap = new HashMap<String, String>();
				HashMap<String, String> noneSecClassDetailsMap = new HashMap<String, String>();
				HashMap<String, String> nonEmpSecClassDetailsMap = new HashMap<String, String>();
				if (secClassesTypeList != null && strSecClassName != null) {
					for (int i = 0; i < secClassesTypeList.size(); i++) {
						String classType = secClassesTypeList.get(i);
						if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(classType) && PGFISWSConstants.CONST_EMPLOYEE.equals(employeeType)
								&& cloudGrpupsURIList != null) {
							currentSecClassDetailsMap.put(strSecClassName.get(i), cloudGrpupsURIList.get(i));
						} else if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(classType)
								&& (PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType))
								&& cloudContractGrpupsURIList != null) {
							currentSecClassDetailsMap.put(strSecClassName.get(i), cloudContractGrpupsURIList.get(i));
						} else if (PGFISWSConstants.CONST_RESTRICTED.equals(classType)
								&& (PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType)) && cloudContractGrpupsURIList != null) {
							nonEmpSecClassDetailsMap.put(strSecClassName.get(i), cloudContractGrpupsURIList.get(i));
						} else if (PGFISWSConstants.CONST_NONE.equals(classType) && cloudGrpupsURIList != null) {
							noneSecClassDetailsMap.put(strSecClassName.get(i), cloudGrpupsURIList.get(i));
						}
					}
				}
				
				if (PGFISWSConstants.CONST_EMPLOYEE.equals(employeeType)) {
					generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_RESTRICTED, groupArr, userId,
							new StringList(), new HashMap());
				} else if ((PGFISWSConstants.CONST_NON_EMP.equals(employeeType) || PGFISWSConstants.CONST_EBP.equals(employeeType))) {
					generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_RESTRICTED, groupArr, userId,
							strSecClassName, nonEmpSecClassDetailsMap);
				}

				if (currentSecClassDetailsMap != null && !currentSecClassDetailsMap.isEmpty()) {
					generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_HIGHLY_RESTRICTED, groupArr, userId,
							strSecClassName, currentSecClassDetailsMap);
				}
				if (noneSecClassDetailsMap != null && !noneSecClassDetailsMap.isEmpty()) {
					generateSecurityGroupJsonArray(context, employeeType, PGFISWSConstants.CONST_NONE, groupArr, userId, strSecClassName,
							noneSecClassDetailsMap);
				}
				addPersonToDefaultGroupCollection(context, employeeType, groupArr, userId);
				output.add(PGFISWSConstants.JSON_TAG_USER_GROUPS, groupArr);
			} else {
				String message = PGFISWSConstants.PERSON_NOT_FOUND + userId;
				output.add(PGFISWSConstants.STRING_MESSAGE, message);
				return Response.status(Response.Status.BAD_REQUEST).entity(output.build().toString()).build();
			}

		} catch (Exception ex) {
			if (isContextPushed) {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}
	
	/**
	 * REST method to get IP Control Class details by passing physical Id for Migration.
	 * 
	 * @param Context    Context used to call API
	 * @param pyisicalId Business Object physicalId
	 * @return response in JSON String format
	 * @throws Exception when operation fails
	 */
	@SuppressWarnings("unchecked")
	public static Response getIPSecurityDetailsResponseForMigration(Context context, String physicalId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder groupArr = Json.createArrayBuilder();
		loadPropertyPage(context);
		if(pageProps != null) {
			FIS_CLM_PERSON = pageProps.getProperty("pgFIS.CloudCLM.User.EmailId");
			FIS_ADMIN_PERSON = pageProps.getProperty("pgFIS.CloudADMIN.User.EmailId");
		}

		boolean isContextPushed = false;
		try {
			
			StringList objectSelects = new StringList(DomainObject.SELECT_DESCRIPTION);
			objectSelects.add(DomainObject.SELECT_ATTRIBUTE_TITLE);
			objectSelects.add(PGFISWSConstants.SELECT_LIBRARY_NAME);
			objectSelects.add(DomainObject.SELECT_NAME);
			objectSelects.add(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES);

			Map objectDetailsMap = new HashMap();
			DomainObject domObj = null;
			try {
				//Pushing content to fetch the required details from context object, as context user sometime not having read and show access.
				ContextUtil.pushContext(context);
				isContextPushed = true;
				domObj = DomainObject.newInstance(context, physicalId);
				objectDetailsMap = domObj.getInfo(context, objectSelects);
			} finally {
				if (isContextPushed) {
					ContextUtil.popContext(context);
					isContextPushed = false;
				}
			}
			
			String strLibraryName = (String) objectDetailsMap.get(PGFISWSConstants.SELECT_LIBRARY_NAME);
			String strDescription = (String) objectDetailsMap.get(DomainObject.SELECT_DESCRIPTION);
			String strTitle = (String) objectDetailsMap.get(DomainObject.SELECT_ATTRIBUTE_TITLE);
			String strProtectionType = (String) objectDetailsMap
					.get(PGFISWSConstants.SELECT_ATTRIBUTE_PG_LIBRARY_CLASSIFICATION_FROM_IP_CLASSES);
			
			StringList contractorList = new StringList();


			if (UIUtil.isNullOrEmpty(strTitle)) {
				strTitle = (String) objectDetailsMap.get(DomainObject.SELECT_NAME);
			}
			StringList empList = new StringList();
			empList.add(FIS_CLM_PERSON);
			empList.add(FIS_ADMIN_PERSON);
			if (PGFISWSConstants.CONST_RESTRICTED.equals(strProtectionType)) {
				String strCustomTitle = strTitle;
				if(UIUtil.isNotNullAndNotEmpty(strTitle)) {
					if(strTitle.indexOf("-R") > 0) {
						strCustomTitle = strTitle.substring(0, strTitle.indexOf("-R"));
						groupArr.add(generateGroupJson(strCustomTitle+"-R", strDescription, new StringList()));
						groupArr.add(generateGroupJson(strCustomTitle+"("+PGFISWSConstants.CONST_NAME_NON_EMP+")-R", strDescription, new StringList()));
						groupArr.add(generateGroupJson(strCustomTitle+"(CLM)-R", strDescription, empList));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle+"-R", strDescription, strProtectionType));
					} else {
						groupArr.add(generateGroupJson(strCustomTitle, strDescription, new StringList()));
						groupArr.add(generateGroupJson(strCustomTitle+"("+PGFISWSConstants.CONST_NAME_NON_EMP+")", strDescription, new StringList()));
						groupArr.add(generateGroupJson(strCustomTitle+"(CLM)", strDescription, empList));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle, strDescription, strProtectionType));
					}
					
				}
			} else if (PGFISWSConstants.CONST_HIGHLY_RESTRICTED.equals(strProtectionType)) {
				String strCustomTitle = strTitle;
				if(UIUtil.isNotNullAndNotEmpty(strTitle)) {
					if(strTitle.indexOf("-HiR") > 0) {
						strCustomTitle = strTitle.substring(0, strTitle.indexOf("-HiR"));
						groupArr.add(generateGroupJson(strCustomTitle+"-HiR", strDescription, empList));
						groupArr.add(generateGroupJson(strCustomTitle+ "("+PGFISWSConstants.CONST_NAME_NON_EMP+")-HiR", strDescription, new StringList()));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle+"-HiR", strDescription, strProtectionType));
					} else {
						groupArr.add(generateGroupJson(strCustomTitle, strDescription, empList));
						groupArr.add(generateGroupJson(strCustomTitle+ "("+PGFISWSConstants.CONST_NAME_NON_EMP+")", strDescription, new StringList()));
						output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
						output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS, generateSecureCollectionJson(strCustomTitle, strDescription, strProtectionType));
					}
				}
			} else if (PGFISWSConstants.CONST_NONE.equals(strProtectionType) && (UIUtil.isNotNullAndNotEmpty(strLibraryName) &&  PGFISWSConstants.CONST_SPECIAL_PROJECTS.equals(strLibraryName))) {
				String strCustomTitle = strTitle;
				if (UIUtil.isNotNullAndNotEmpty(strTitle)) {
					groupArr.add(generateGroupJson(strCustomTitle, strDescription, empList));
					output.add(PGFISWSConstants.JSON_TAG_GROUPS, groupArr.build());
					output.add(PGFISWSConstants.JSON_TAG_SECURE_COLLECTIONS,
							generateSecureCollectionJson(strCustomTitle, strDescription, strProtectionType));
				} 
			}
			
		} catch (Exception ex) {
			if (isContextPushed) {
				ContextUtil.popContext(context);
			}
			logger.log(Level.SEVERE, ERROR_Logger, ex);
			output.add(JSON_OUTPUT_KEY_ERROR, ex.getMessage());
			output.add(PGFISWSConstants.JSON_OUTPUT_KEY_TRACE, PGFISIntegrationUtil.getExceptionTrace(ex));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(output.build().toString()).build();
		}
		return Response.status(Status.OK).entity(output.build().toString()).build();
	}


}
