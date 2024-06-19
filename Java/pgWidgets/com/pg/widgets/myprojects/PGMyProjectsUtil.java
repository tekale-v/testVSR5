package com.pg.widgets.myprojects;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.common.ContentReplicateOptions;
import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.util.ComponentsUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.program.ProgramCentralConstants;
import com.matrixone.apps.program.ProgramCentralUtil;
import com.matrixone.apps.program.ProjectSpace;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.StringList;

public class PGMyProjectsUtil {
	public static final String POLICY_PROJECT_SPACE = PropertyUtil.getSchemaProperty(null, "policy_ProjectSpace");
	public static final String STATE_PROJECT_ARCHIVE = PropertyUtil.getSchemaProperty(null, "policy",
			POLICY_PROJECT_SPACE, "state_Archive");
	private static final String STATE_PROJECT_COMPLETE = PropertyUtil.getSchemaProperty(null, "policy",
			POLICY_PROJECT_SPACE, "state_Complete");

	public static final String ATTRIBUTE_PG_PLATFORM_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgPlatformType");
	public static final String TYPE_PG_PLIPLATFORM = PropertyUtil.getSchemaProperty(null, "type_pgPLIPlatform");
	public static final String RELATIONSHIP_PG_PROJECT_TO_PLATFORM = PropertyUtil.getSchemaProperty(null,
			"relationship_pgProjectToPlatform");
	public static final String FRANCHISE_PLATFORM = "Franchise Platform";
	public static final String PRODUCT_CATEGORY_PLATFORM = "Product Category Platform";
	public static final String PRODUCT_FORM_PLATFORM = "Product Form Platform";
	public static final String PRODUCT_TECHNOLOGY_PLATFORM = "Product Technology Platform";
	public static final String PACKAGE_PLATFORM = "Package Platform";
	public static final String MATERIAL_PLATFORM = "Material Platform";
	public static final String TECHNICAL_BUILDING_BLOCKS = "Technical Building Blocks";
	public static final String PRODUCT_PROCESS_PLATFORM = "Product Process Platform";
	public static final String PACKAGE = "Package Chassis";
	public static final String PRODUCT_TECHNOLOGY = "Product Technology Chassis";
	public static final String ATTRIBUTE_PG_CHASSIS_TYPE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgChassisType");

	private static final Logger logger = Logger.getLogger(PGWidgetUtil.class.getName());

	public static String getProjectsData(Context context, String paramString) throws Exception {
		logger.log(Level.INFO, "In getProjectsData");
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		String tableExpressionArray = jsonInputData.getString("tableExpressionArray");
		StringList busSelects = FrameworkUtil.split(tableExpressionArray, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		JsonArray jsonArr = Json.createArrayBuilder().build();
		MapList projectList = null;
		// Check license while listing Project Concepts, Project Space, if license check
		// fails here
		// the projects will not be listed. This is mainly done to avoid Project
		// Concepts from being listed
		// but as this is the common method, the project space objects will also not be
		// listed.
		//
		ComponentsUtil.checkLicenseReserved(context, ProgramCentralConstants.PGE_LICENSE_ARRAY);
		com.matrixone.apps.common.Person person = com.matrixone.apps.common.Person.getPerson(context);
		try {

			// Retrieve the person's project's info
			StringBuilder busWhere = new StringBuilder();
			StringBuilder relWhere = new StringBuilder();
			StringBuilder sbCurrent = new StringBuilder(" && current!=");

			String vaultPattern = "";

			String vaultOption = PersonUtil.getSearchDefaultSelection(context);

			vaultPattern = PersonUtil.getSearchVaults(context, false, vaultOption);

			if (!vaultOption.equals(PersonUtil.SEARCH_ALL_VAULTS) && vaultPattern.length() > 0) {
				busWhere.append("vault matchlist '").append(vaultPattern).append("' ','");
			}

			// Active Projects - not in the complete state or in the archive state
			if (busWhere.length() == 0) {
				busWhere.append("current!=").append(STATE_PROJECT_COMPLETE).append(sbCurrent)
						.append(STATE_PROJECT_ARCHIVE).append(sbCurrent)
						.append(ProgramCentralConstants.STATE_PROJECT_SPACE_HOLD_CANCEL_HOLD).append(sbCurrent)
						.append(ProgramCentralConstants.STATE_PROJECT_SPACE_HOLD_CANCEL_CANCEL);
			} else {
				busWhere.append(sbCurrent).append(STATE_PROJECT_COMPLETE).append(sbCurrent)
						.append(STATE_PROJECT_ARCHIVE).append(sbCurrent)
						.append(ProgramCentralConstants.STATE_PROJECT_SPACE_HOLD_CANCEL_HOLD).append(sbCurrent)
						.append(ProgramCentralConstants.STATE_PROJECT_SPACE_HOLD_CANCEL_CANCEL);
			}

			if (busWhere.length() == 0) {
				busWhere.append(" type!=" + ProgramCentralConstants.TYPE_EXPERIMENT).append("&&type!='")
						.append(ProgramCentralConstants.TYPE_PROJECT_BASELINE).append("'");
			} else {
				busWhere.append(" && type!=").append(ProgramCentralConstants.TYPE_EXPERIMENT).append("&&type!='")
						.append(ProgramCentralConstants.TYPE_PROJECT_BASELINE).append("'");
			}

			projectList = ProjectSpace.getProjects(context, person, busSelects, null, busWhere.toString(),
					relWhere.toString());
			projectList.sort(DomainConstants.SELECT_NAME, "ascending", "string");
			jsonArr = PGWidgetUtil.converMaplistToJsonArray(context, projectList);
		} catch (Exception ex) {
			throw ex;
		}
		return jsonArr.toString();
	}

	public static String createProject(Context context, String strInput) {

		String strOut = null;
		try {
			JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strInput);
			String strMode = jsonInput.getString("mode");
			if ("create".equals(strMode)) {
				strOut = createNewProject(context, jsonInput);
			} else if ("edit".equals(strMode)) {
				strOut = editProject(context, jsonInput);
			}
		} catch (Exception e) {
			return Json.createObjectBuilder().add(PGWidgetConstants.KEY_ERROR, e.getMessage())
					.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e)).build().toString();
		}
		return strOut;

	}

	public static String createNewProject(Context context, JsonObject jsonInput) throws Exception

	{
		ProjectSpace project = (ProjectSpace) DomainObject.newInstance(context,
				ProgramCentralConstants.TYPE_PROJECT_SPACE, "Program");
		ProjectSpace newProject = (ProjectSpace) DomainObject.newInstance(context,
				ProgramCentralConstants.TYPE_PROJECT_SPACE, "Program");
		JsonArray jsonArrReturn = null;
		try {
			ContextUtil.startTransaction(context, true);
			String pgPKGProjectID = PropertyUtil.getSchemaProperty(context, "attribute_pgPKGProjectID");
			Map<String, String> attributeMap = new HashMap<>();
			Map<String, String> basicProjectInfo = new HashMap<>();
			Map<String, String> relatedProjectInfo = new HashMap<>();

			JsonArray jsonArrConn = jsonInput.containsKey("connections") ? jsonInput.getJsonArray("connections") : null;

			logger.log(Level.INFO, "createNewProject jsonInput :: " + jsonInput);

			String projectName = jsonInput.containsKey(DomainConstants.SELECT_NAME) ? jsonInput.getString(DomainConstants.SELECT_NAME) : null;
			boolean projectAutoName = jsonInput.getBoolean("autoName");
			String projectDescrption = jsonInput.containsKey(DomainConstants.SELECT_DESCRIPTION) ? jsonInput.getString(DomainConstants.SELECT_DESCRIPTION) : null;
			String projectVault = "eService Production";
			String projectPolicy = jsonInput.getString(DomainConstants.SELECT_POLICY);
			String projectSpaceType = jsonInput.getString(DomainConstants.SELECT_TYPE);
			String selectedProjectId = FrameworkUtil.getOIDfromPID(context, jsonInput.getString("projectTemplateId"));
			
			if (jsonInput.containsKey("attributes")
					&& jsonInput.getJsonObject("attributes").containsKey("projectDate")) {
				String projectDate = jsonInput.getJsonObject("attributes").getString("projectDate");

				Locale locale = context.getLocale();
				String strTimeZone = "-5.5";// (String) programMap.get("timeZone");
				double dClientTimeZoneOffset = (new Double(strTimeZone)).doubleValue();
				if (ProgramCentralUtil.isNotNullString(projectDate)) {
					TimeZone tz = TimeZone.getTimeZone(context.getSession().getTimezone());
					double dbMilisecondsOffset = -1.0D * tz.getRawOffset();
					dClientTimeZoneOffset = (new Double(dbMilisecondsOffset / 3600000.0D)).doubleValue();
					projectDate = projectDate.trim();
					int iDateFormat = eMatrixDateFormat.getEMatrixDisplayDateFormat();
					String strInputTime = eMatrixDateFormat.adjustTimeStringForInputFormat("");
					projectDate = eMatrixDateFormat.getFormattedInputDateTime(projectDate, strInputTime, iDateFormat,
							dClientTimeZoneOffset, locale);
				}
				attributeMap.put(DomainObject.ATTRIBUTE_TASK_ESTIMATED_START_DATE, projectDate);

				attributeMap.put(DomainObject.ATTRIBUTE_TASK_CONSTRAINT_DATE, projectDate);
			}
			StringList calendarIds = new StringList();

			if (ProgramCentralUtil.isNullString(projectName) && projectAutoName) {
				String symbolicTypeName = FrameworkUtil.getAliasForAdmin(context, "Type", projectSpaceType, true);
				String symbolicPolicyName = FrameworkUtil.getAliasForAdmin(context, "Policy", projectPolicy, true);
				projectName = FrameworkUtil.autoName(context, symbolicTypeName, null, symbolicPolicyName, null, null,
						true, true);
				attributeMap.put(pgPKGProjectID, projectName);
			} else {
				String symbolicTypeName = FrameworkUtil.getAliasForAdmin(context, "Type", projectSpaceType, true);
				String symbolicPolicyName = FrameworkUtil.getAliasForAdmin(context, "Policy", projectPolicy, true);
				String projectAutoNameValue = FrameworkUtil.autoName(context, symbolicTypeName, null,
						symbolicPolicyName, null, null, true, true);
				attributeMap.put(pgPKGProjectID, projectAutoNameValue);
			}
			basicProjectInfo.put(DomainConstants.SELECT_NAME, projectName);
			basicProjectInfo.put(DomainConstants.SELECT_TYPE, projectSpaceType);
			basicProjectInfo.put(DomainConstants.SELECT_POLICY, projectPolicy);
			basicProjectInfo.put(DomainConstants.SELECT_VAULT, projectVault);
			basicProjectInfo.put(DomainConstants.SELECT_DESCRIPTION, projectDescrption);
			boolean isCopyFolderData = true;
			boolean isCopyFinancialData = true;
			boolean keepSourceConstraints = false;

			ContentReplicateOptions selectedOptionForReferenceDocument = ContentReplicateOptions.COPY;

			boolean isTemplateTaskAutoName = false;
			Map<String, String> questionResponseMap = new HashMap<>();
			DomainObject domTemplate = DomainObject.newInstance(context, selectedProjectId);
			String strTemp_ProjectType = domTemplate.getInfo(context,
					"attribute[" + DomainConstants.ATTRIBUTE_PROJECT_TYPE + "]");
			if (UIUtil.isNotNullAndNotEmpty(strTemp_ProjectType))
				attributeMap.put(DomainConstants.ATTRIBUTE_PROJECT_TYPE, strTemp_ProjectType);
			newProject = project.cloneTemplateToCreateProject(context, selectedProjectId, basicProjectInfo,
					relatedProjectInfo, attributeMap, questionResponseMap, isTemplateTaskAutoName, isCopyFolderData,
					isCopyFinancialData, keepSourceConstraints, selectedOptionForReferenceDocument, calendarIds);
			newProject.setDescription(context, projectDescrption);
			newProject.setAttributeValues(context, attributeMap);
			
			String newProjectObjectId = newProject.getObjectId();
			logger.log(Level.INFO, "newProjectId :: ", newProjectObjectId);
			
			HashMap<Object, Object> requestMap = new HashMap<>();
			requestMap.put("sourceId", selectedProjectId);
			requestMap.put("targetId", newProjectObjectId);
			requestMap.put("answerList", questionResponseMap);
			String[] methodargs = JPO.packArgs(requestMap);
			JPO.invoke(context, "pgAAA_ReuseDeliverable", null, "cloneReuseDeliverable", methodargs);
			
			HashMap<String, String> paramMap = new HashMap<>();
			paramMap.put("projectId", newProjectObjectId);
			paramMap.put("templateId", selectedProjectId);
			JPO.invoke(context, "emxProjectSpace", null, "projectTaskConnectSimObjects", JPO.packArgs(paramMap));
			
			if (jsonArrConn != null && !jsonArrConn.isEmpty()) {
				updatePlatformAttributes(context, jsonInput, newProjectObjectId);
			}
			ContextUtil.commitTransaction(context);

			StringList slSelects = FrameworkUtil.split(jsonInput.getString("tableExpressionArray"),
					PGWidgetConstants.KEY_COMMA_SEPARATOR);
//			MapList projectList = DomainObject.getInfo(context, new String[] { newProjectObjectId }, slSelects);
			String strWhere = DomainConstants.SELECT_ID +"=="+ newProjectObjectId;
			MapList projectList = ProjectSpace.getProjects(context, Person.getPerson(context), slSelects, null, strWhere,
					DomainConstants.EMPTY_STRING);
			logger.log(Level.INFO, "projectList :: ", projectList);
			jsonArrReturn = PGWidgetUtil.converMaplistToJsonArray(context, projectList);
		} catch (Exception ex) {
			ContextUtil.abortTransaction(context);
			if (ex.getMessage().contains("No create access"))
				throw new Exception(EnoviaResourceBundle.getProperty(context, "ProgramCentral",
						"emxProgramCentral.Project.NoCreateAccess", context.getSession().getLanguage()));
			throw new Exception(ex.getMessage());
		} finally {
			PropertyUtil.setGlobalRPEValue(context, "CopyColorAttribute", "true");
		}
		return jsonArrReturn.toString();
	}
	
	public static void updatePlatformAttributes(Context context, JsonObject jsonInput, String strObjectId)
			throws Exception {

		JsonArray jsonArrConn = jsonInput.getJsonArray("connections");

		logger.log(Level.INFO, "updatePlatformAttributes jsonArrConn :: " + jsonArrConn);

		if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			StringList slRelselects = new StringList(1);
			slRelselects.add(DomainRelationship.SELECT_ID);
			StringBuffer strFieldNameWhere = new StringBuffer();

			DomainObject doProjectObj = DomainObject.newInstance(context, strObjectId);
			for (int i = 0; i < jsonArrConn.size(); i++) {
				StringList strConnectedRelId = new StringList();
				String strPicklistName = jsonArrConn.getJsonObject(i).getString(DomainConstants.SELECT_TYPE);
				String strFieldValue = jsonArrConn.getJsonObject(i).getString(DomainConstants.SELECT_ID);
				String strAttrValue = jsonArrConn.getJsonObject(i).getString("label");
				String strRelName = jsonArrConn.getJsonObject(i).getString(PGWidgetConstants.KEY_REL_NAME);
				boolean strIsFrom = jsonArrConn.getJsonObject(i).getBoolean(PGWidgetConstants.KEY_IS_FROM);

				strFieldNameWhere = new StringBuffer();
				strFieldNameWhere.append("attribute[");
				strFieldNameWhere.append(ATTRIBUTE_PG_PLATFORM_TYPE);
				strFieldNameWhere.append("]==\"");
				strFieldNameWhere.append(strAttrValue);
				strFieldNameWhere.append("\"");

				MapList mlOldIDs = doProjectObj.getRelatedObjects(context, strRelName, strPicklistName, null,
						slRelselects, !strIsFrom, strIsFrom, (short) 1, null, null, 0);
				if (!mlOldIDs.isEmpty()) {
					for (int mlOldIDsValues = 0; mlOldIDsValues < mlOldIDs.size(); mlOldIDsValues++) {
						Map mpConnected = (Map) mlOldIDs.get(mlOldIDsValues);
						strConnectedRelId.add(mpConnected.get(DomainRelationship.SELECT_ID).toString());
					}
				}
				DomainRelationship.disconnect(context, BusinessUtil.toStringArray(strConnectedRelId));
				connectPlatformAndChassisValues(context, strFieldValue.split(PGWidgetConstants.KEY_COMMA_SEPARATOR),
						strRelName, strObjectId, strAttrValue);
			}
		}
	}

	public static void connectPlatformAndChassisValues(Context context, String[] strpgPlatformandChassisIDS,
			String sRelationshipName, String strObjectId, String strAttrValue) throws Exception {
		String strSelectedPlatformId = DomainConstants.EMPTY_STRING;
		String strSelectedChassisId = DomainConstants.EMPTY_STRING;
		DomainRelationship drelship = null;
		int strpgPlatformandChassisIDSSize = 0;
		DomainObject domObjpgChassisId = null;
		DomainObject domObjpgPlatformId = null;
		try {
			DomainObject domObject = DomainObject.newInstance(context, strObjectId);
			if (strAttrValue.equalsIgnoreCase(PACKAGE) || strAttrValue.equalsIgnoreCase(PRODUCT_TECHNOLOGY)) {
				if (strpgPlatformandChassisIDS != null) {
					strpgPlatformandChassisIDSSize = strpgPlatformandChassisIDS.length;
					for (int strpgChassisValues = 0; strpgChassisValues < strpgPlatformandChassisIDSSize; strpgChassisValues++) {
						strSelectedChassisId = strpgPlatformandChassisIDS[strpgChassisValues];
						if (UIUtil.isNotNullAndNotEmpty(strSelectedChassisId)) {
							domObjpgChassisId = DomainObject.newInstance(context, strSelectedChassisId);
							drelship = domObjpgChassisId.addRelatedObject(context,
									new RelationshipType(sRelationshipName), true, strObjectId);
							drelship.setAttributeValue(context, ATTRIBUTE_PG_CHASSIS_TYPE, strAttrValue);
						}
					}
				}
			} else {
				if (strpgPlatformandChassisIDS != null) {
					int strpgPlatformIDSLength = strpgPlatformandChassisIDS.length;
					for (int pgPlatformIDValues = 0; pgPlatformIDValues < strpgPlatformIDSLength; pgPlatformIDValues++) {
						strSelectedPlatformId = strpgPlatformandChassisIDS[pgPlatformIDValues];
						if (UIUtil.isNotNullAndNotEmpty(strSelectedPlatformId)) {
							domObjpgPlatformId = DomainObject.newInstance(context, strSelectedPlatformId);
							drelship = domObjpgPlatformId.connect(context, sRelationshipName, domObject, true);
							drelship.setAttributeValue(context, ATTRIBUTE_PG_PLATFORM_TYPE, strAttrValue);
						}
					}
				}
			}
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static String editProject(Context context, JsonObject jsonInput) throws Exception {
		JsonArray jsonArrReturn = null;
		try {
			ContextUtil.startTransaction(context, true);

			Map<String, String> attributeMap = (HashMap) PGWidgetUtil.getMapFromJson(context,
					jsonInput.getJsonObject("attributes"));

			JsonArray jsonArrConn = jsonInput.containsKey("connections") ? jsonInput.getJsonArray("connections") : null;

			logger.log(Level.INFO, "editProject jsonInput :: " + jsonInput);

			String objectId = jsonInput.getString("objectId");
			String projectName = jsonInput.containsKey(DomainConstants.SELECT_NAME) ? jsonInput.getString(DomainConstants.SELECT_NAME) : null;
			String projectDescrption = jsonInput.containsKey(DomainConstants.SELECT_DESCRIPTION) ? jsonInput.getString(DomainConstants.SELECT_DESCRIPTION) : null;
			if (jsonInput.containsKey("attributes")
					&& jsonInput.getJsonObject("attributes").containsKey("projectDate")) {
				String projectDate = jsonInput.getJsonObject("attributes").getString("projectDate");
				if (ProgramCentralUtil.isNotNullString(projectDate)) {
					Locale locale = context.getLocale();
					String strTimeZone = "-5.5";// (String) programMap.get("timeZone");
					double dClientTimeZoneOffset = (new Double(strTimeZone)).doubleValue();
					TimeZone tz = TimeZone.getTimeZone(context.getSession().getTimezone());
					double dbMilisecondsOffset = -1.0D * tz.getRawOffset();
					dClientTimeZoneOffset = (new Double(dbMilisecondsOffset / 3600000.0D)).doubleValue();
					projectDate = projectDate.trim();
					int iDateFormat = eMatrixDateFormat.getEMatrixDisplayDateFormat();
					String strInputTime = eMatrixDateFormat.adjustTimeStringForInputFormat("");
					projectDate = eMatrixDateFormat.getFormattedInputDateTime(projectDate, strInputTime, iDateFormat,
							dClientTimeZoneOffset, locale);
				}
				attributeMap.remove("projectDate");
				attributeMap.put(DomainObject.ATTRIBUTE_TASK_ESTIMATED_START_DATE, projectDate);
				attributeMap.put(DomainObject.ATTRIBUTE_TASK_ESTIMATED_FINISH_DATE, projectDate);
				attributeMap.put(DomainObject.ATTRIBUTE_TASK_CONSTRAINT_DATE, projectDate);
			}
			DomainObject domProject = DomainObject.newInstance(context, objectId);

			if (UIUtil.isNotNullAndNotEmpty(projectName)) {
				domProject.setName(projectName);
			}
			if (UIUtil.isNotNullAndNotEmpty(projectDescrption)) {
				domProject.setDescription(context, projectDescrption);
			}
			if (attributeMap != null && !attributeMap.isEmpty()) {
				domProject.setAttributeValues(context, attributeMap);
			}
			if (jsonArrConn != null && !jsonArrConn.isEmpty()) {
				updatePlatformAttributes(context, jsonInput, objectId);
			}
			ContextUtil.commitTransaction(context);
			
			StringList slSelects = FrameworkUtil.split(jsonInput.getString("tableExpressionArray"),
					PGWidgetConstants.KEY_COMMA_SEPARATOR);
//			MapList projectList = DomainObject.getInfo(context, new String[] { objectId }, slSelects);
			String strWhere = DomainConstants.SELECT_ID +"=="+ objectId;
			MapList projectList = ProjectSpace.getProjects(context, Person.getPerson(context), slSelects, null, strWhere,
					DomainConstants.EMPTY_STRING);
			logger.log(Level.INFO, "projectList :: " + projectList);
			jsonArrReturn = PGWidgetUtil.converMaplistToJsonArray(context, projectList);
			
		} catch (Exception ex) {
			ContextUtil.abortTransaction(context);
//			throw new Exception(EnoviaResourceBundle.getProperty(context, ProgramCentralConstants.PROGRAMCENTRAL,
//					"emxProgramCentral.Experiment.ModifyAsscess", context.getSession().getLanguage()));
			
			throw new Exception(ex.getMessage());
		} finally {
			PropertyUtil.setGlobalRPEValue(context, "CopyColorAttribute", "true");
		}
		return jsonArrReturn.toString();

	}

}
