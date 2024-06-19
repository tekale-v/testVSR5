package com.pg.widgets.studyprotocol;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.custom.pg.Artwork.ArtworkConstants;
import com.matrixone.apps.awl.util.AWLConstants;
import com.matrixone.apps.awl.util.AWLUtil;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.cache.CacheManager;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.common.WorkspaceVault;
import com.matrixone.apps.configuration.ConfigurationUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.util.IRMUPT;
import com.pg.pdf.enumerations.PDFConstants;
import com.pg.pdf.views.PDFApp;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.Access;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.db.State;
import matrix.db.StateList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGStudyProtocol {

	private static final String KEY_ID = "Id";
	private static final String SELECT_DESCRIPTION = "description";
	private static final String KEY_HIERARCHY = "hierarchy";
	static final String PERSON_USER_AGENT = PropertyUtil.getSchemaProperty(null, "person_UserAgent");
	static final String KEY_STATUS = "status";
	private static final Logger logger = Logger.getLogger(PGStudyProtocol.class.getName());

	static final String ERROR_PGSTUDYPROTOCOL_GETMYSTUDYPROTOCOL = "Exception in PGStudyProtocol";

	static final String POLICY_SIGNATURE_REFERENCE_SYMBOLIC = "policy_pgPKGSignatureReferenceDoc";
	static final String POLICY_SIGNATURE_REFERENCE_STATE_REVIEW_SYMBOLIC = "state_Review";
	static final String POLICY_SELF_APPROVAL = "Self Approval";
	static final String ATTRIBUTE_PGNUMBEROFPANELISTS = PropertyUtil.getSchemaProperty(null,
			"attribute_pgNumberOfPanelists");
	static final String SELECT_ATTRIBUTE_PGNUMBEROFPANELISTS = "attribute[" + ATTRIBUTE_PGNUMBEROFPANELISTS + "]";
	static final String ATTRIBUTE_PGPRODUCTCODEPANELISTS = PropertyUtil.getSchemaProperty(null,
			"attribute_pgProductCodePanelists");
	static final String SELECT_ATTRIBUTE_PGPRODUCTCODEPANELISTS = "attribute[" + ATTRIBUTE_PGPRODUCTCODEPANELISTS + "]";

	static final String ATTRIBUTE_PGAEROSOL_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgAerosolType");
	static final String SELECT_PGAEROSOL_TYPE = "attribute[" + ATTRIBUTE_PGAEROSOL_TYPE + "]";
	static final String SELECT_RELATED_PRODUCT_FORM = "to[Owning Product Line].from[pgPLIProductForm].name";
	static final String SELECT_RELATED_BA = "from[pgDocumentToBusinessArea].to[pgPLIBusinessArea].name";

	static final String ATTRIBUTE_ROUTE_BASE_PURPOSE = "attribute[" + DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE
			+ "]";
	static final String TYPE_PG_PLI_PLATFORM = PropertyUtil.getSchemaProperty(null, "type_pgPLIPlatform");
	static final String ATTRIBUTE_PLATFORM_TYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgPlatformType");
	static final String PRODUCT_CATEGORY_PLATFORM = "Product Category Platform";

	static final String TYPE_PG_PKG_STUDY_PROTOCOL = PropertyUtil.getSchemaProperty(null, "type_pgPKGStudyProtocol");

	static final String KEY_ATTR_TYPE = "attributeType";
	static final String KEY_ATTR_RELATIONSHIP = "relationship";
	static final String KEY_ATTR_SIDETOCONNECT = "sideToConnect";

	static final String ATTR_TYPE_SINGLEVALUE = "singlevalue";
	static final String ATTR_TYPE_MULTIVALUE = "multivalue";
	static final String ATTR_TYPE_BASIC = "basic";
	static final String ATTR_TYPE_DATE = "date";
	static final String ATTR_TYPE_DROPPABLE = "droppable";
	static final String ATTR_TYPE_PICKLIST = "picklist";
	static final String PGIPCLASSIFICATION = "pgIPClassification";
	static final String ACCESSTYPE = "Access Type";
	static final String PGIPPROJECTSECURITY = "pgIPProjectSecurity";
	static final String PGBUSINESSAREA = "pgBusinessArea";
	static final String PGUPIPHYSICALID = "pgUPTPhyID";
	static final String PGPRODUCTCATEGORYPLATFORM = "pgProductCategoryPlatform";

	static final String TYPE_PGSTUDYLEG = PropertyUtil.getSchemaProperty(null, "type_pgStudyLeg");
	static final String TYPE_DOCUMENTS = PropertyUtil.getSchemaProperty(null, "type_DOCUMENTS");
	static final String RELATIONSHIP_PGLEGPROCESS = PropertyUtil.getSchemaProperty(null, "relationship_pgLegProcess");
	static final String RELATIONSHIP_PGLEGINPUT = PropertyUtil.getSchemaProperty(null, "relationship_pgLegInput");
	static final String POLICY_VERSION = PropertyUtil.getSchemaProperty(null, "policy_Version");
	static final String STRING_LEGONE = "LEG 1";
	static final String STRING_LEG = "LEG ";
	static final String LEG_NAME_PREFIX = "LEG-";

	static final String FRANCHISEPLATFORM = "FranchisePlatform";
	static final String PRODUCTCATEGORYPLATFORM = "ProductCategoryPlatform";
	static final String CONST_BUSINESS_AREA_DISPLAY = "Business Area";
	static final String ERROR_MSG_NOT_DOCUMENTS = "Please drop only Documents";
	static final String ERROR_MSG_NOT_ROUTE_TEMPLATE = "Please drop only approval Active/Production Route Template";
	static final String EMXCOMMONDOCUMENT = "emxCommonDocument";
	static final String RELATIONSHIP_PLATFORM_TO_BUSINESSAREA = PropertyUtil.getSchemaProperty(null,
			"relationship_pgPlatformToBusinessArea");
	static final String ROUTE_BASE_PURPOSE_APPROVAL = "Approval";
	static final String STATE_PRELIMINARY = "Preliminary";
	static final String STATE_REVIEW = "Review";
	static final String POLICY_PG_PKGSIGNATUREREFERENCEDOC = PropertyUtil.getSchemaProperty(null,
			POLICY_SIGNATURE_REFERENCE_SYMBOLIC);
	static final String EXCEPTION_MESSAGE = "Exception in PGStudyProtocol";
	static final String SYMBOLIC_STATE_IN_APPROVAL = "state_InApproval";
	static final String PICKLIST_NAMES = "PickListNames";
	static final String PICKLIST_VALUES = "PickListValues";
	static final String RANGE_ATTRIBUTE_NAMES = "RangeAttributeNames";
	static final String KEY_ATTR_GPS_ASSESSMENT_CATEGORY = "pgGPSAssessmentCategory";
	static final String ATTRIBUTE_GPS_ASSESSMENT_CATEGORY = PropertyUtil.getSchemaProperty(null,
			"attribute_pgGPSAssessmentCategory");
	static final String SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY = DomainObject
			.getAttributeSelect(ATTRIBUTE_GPS_ASSESSMENT_CATEGORY);
	static final String ATTRIBUTE_ESTIMATED_DURATION = PropertyUtil.getSchemaProperty(null,
			"attribute_TaskEstimatedDuration");
	static final String ATTRIBUTE_PGAAA_PRE_TASK_PERSON_ASSIGNEE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAAA_PreTaskPersonAssignee");
	static final String ATTRIBUTE_PGAAA_POST_TASK_PERSON_ASSIGNEE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAAA_PostTaskPersonAssignee");
	static final String ATTRIBUTE_PGAAA_PRE_TASK_ROLE_ASSIGNEE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAAA_PreTaskRoleAssignee");
	static final String ATTRIBUTE_PGAAA_POST_TASK_ROLE_ASSIGNEE = PropertyUtil.getSchemaProperty(null,
			"attribute_pgAAA_PostTaskRoleAssignee");
	static final String KEY_TASK_ID = "TaskId";
	static final String KEY_TASK_NAME = "TaskName";
	static final String KEY_PROMOTION_STATUS = "PromoteStatus";
	static final String TYPE_GPS_ASSESSMENT_TASK = PropertyUtil.getSchemaProperty(null, "type_pgGPSAssessmentTask");
	static final String POLICY_GPS_ASSESSMENT_TASK = PropertyUtil.getSchemaProperty(null, "policy_pgGPSAssessmentTask");
	static final String REASON_FOR_CHANGE_VALUE = "New";
	static final String ATTRIBUTE_REASON_FOR_CHANGE = PropertyUtil.getSchemaProperty(null, "attribute_ReasonforChange");
	static final String KEY_RELATED_IDS = "RelatedIds";
	static final String KEY_ADD_PERSON_PRE = "addPersonPre";
	static final String KEY_ADD_PERSON_POST = "addPersonPost";
	static final String KEY_ADD_PROJECT_ROLE_POST = "addProjectRolePost";
    static final String KEY_ADD_PROJECT_ROLE_PRE = "addProjectRolePre";
	static final String RELATIONSHIP_PGGPSASSESSMENTTASK_INPUTS = PropertyUtil.getSchemaProperty(null,
			"relationship_pgGPSAssessmentTaskInputs");
	static final String UPDATED_DATA = "updatedData";
	static final String PRODUCT_SITE_EXPR = "PRODUCT_SITE_EXPR";
	static final String EXPR_SITE_FOR_PRODUCT_MANUFACTURING = "tomid[pgManufacturingResponsibilityLeg].from.name";
	static final String KEY_COMMA_SEPARATOR = ",";
	static final String STR_ATTRIBUTE_OPEN = AWLUtil.strcat(PGWidgetConstants.STR_ATTRIBUTE,
			PGWidgetConstants.STR_OPENBRACKET);
	static final String KEY_BUSINESSAREA = "BusinessArea";
	static final String IPCLASSIFICATION = "IPClassification";
	static final String ERROR_PREFERENCE = "Default User Preferences are not set.";
	static final String ATTRIBUTE_PGREGION = PropertyUtil.getSchemaProperty(null, "attribute_pgRegion");
	static final String VT_PLANT_RD_SITES = "VT-PlantsRDSites";
	static final String PICKLIST_TYPE_PLANT = "Plant";
	static final String COMMAND_CLONE = "Clone";
	static final String COMMAND_REVISETONEW = "ReviseToNew";
	static final String ERROR_REVISE_STUDY_PROTOCOL = "The selected Study Protocol objects are not latest revision. Please select latest revision for : ";
	static final String TYPE_PG_AffectedFPPLIST = PropertyUtil.getSchemaProperty("type_pgDSOAffectedFPPList");
	static final String SYM_STATE_LOCKED = "Locked";
	static final String STATE_RELEASE = "Release";

	/**
	 * The method creates the JSON object to get the list of Study Protocol
	 * associated with the user. the input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @param args
	 *            the type, name, revision and where clause,allowed state can be
	 *            configured and sent as input to get the desired list of the
	 *            objects to be displayed
	 * @return JSON object consisting of the information to be displayed
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getMyStudyProtocol(Context context, Map<?, ?> mpParamMAP) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			HashMap<?, ?> programMap = (HashMap<?, ?>) mpParamMAP;
			String strTypePattern = (String) programMap.get(PGWidgetConstants.TYPE_PATTERN);
			String strNamePattern = (String) programMap.get(PGWidgetConstants.NAME_PATTERN);
			String strRevisionPattern = (String) programMap.get(PGWidgetConstants.REVISION_PATTERN);
			String strObjectLimit = (String) programMap.get(PGWidgetConstants.OBJECT_LIMIT);
			String strWhereExpression = (String) programMap.get(PGWidgetConstants.WHERE_EXP);
			String strExpandType = (String) programMap.get(PGWidgetConstants.EXPAND_TYPE);
			String strSelectables = (String) programMap.get(PGWidgetConstants.OBJ_SELECT);
			String strDuration = (String) programMap.get(PGWidgetConstants.DURATION);
			String strAllowedStates = (String) programMap.get(PGWidgetConstants.ALLOWED_STATE);
			String strShowOwned = (String) programMap.get(PGWidgetConstants.SHOW_OWNED);
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			objectSelects.add(PGWidgetConstants.SELECT_PHYSICAL_ID);
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_REVISION);
			if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
				objectSelects.addAll(strSelectables.split(KEY_COMMA_SEPARATOR));
			}

			String strOwnerPattern = DomainConstants.QUERY_WILDCARD;
			if (PGWidgetConstants.STRING_TRUE.equalsIgnoreCase(strShowOwned))
				strOwnerPattern = context.getUser();

			String strWhere = PGWidgetUtil.getWhereClause(context, strDuration, strWhereExpression, strAllowedStates);
			StringBuilder sbWhereBuilder = new StringBuilder();
			sbWhereBuilder.append(strWhere);
			sbWhereBuilder.append(" && ");
			sbWhereBuilder.append("(");
			sbWhereBuilder.append("ownership.project=='");
			sbWhereBuilder.append(context.getUser());
			sbWhereBuilder.append("_PRJ'");
			sbWhereBuilder.append(")");
			MapList mlList = DomainObject.findObjects(context, // Context
					PGWidgetUtil.getPattern(context, strTypePattern), // Type
					PGWidgetUtil.getPattern(context, strNamePattern), // Name
					strRevisionPattern, // Revision
					strOwnerPattern, // context.getUser(),//ownerPattern
					DomainConstants.QUERY_WILDCARD, // String vaultPattern
					sbWhereBuilder.toString(), null, // The query name to save results
					Boolean.getBoolean(strExpandType), // true, if the query should find subtypes of the given types
					objectSelects, // Bus select
					Short.parseShort(strObjectLimit));// limit

			JsonArrayBuilder jsonArr = Json.createArrayBuilder();

			if (mlList != null && !mlList.isEmpty()) {
				mlList.addSortKey(DomainConstants.SELECT_NAME, PGWidgetConstants.ASCENDING, PGWidgetConstants.STRING);
				mlList.sort();
				JsonObjectBuilder jsonObject = null;
				String strLanguage = context.getSession().getLanguage();
				Map<?, ?> objMap = null;
				String strKey;
				String strValue;
				String strId;
				String strTypeDisplayName;
				for (int i = 0; i < mlList.size(); i++) {
					jsonObject = Json.createObjectBuilder();
					objMap = (Map<?, ?>) mlList.get(i);
					strId = PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID));
					jsonObject.add(DomainConstants.SELECT_ID, strId);
					strTypeDisplayName = EnoviaResourceBundle.getTypeI18NString(context,
							(String) objMap.get(DomainConstants.SELECT_TYPE), strLanguage);
					jsonObject.add(PGWidgetConstants.KEY_TYPE, strTypeDisplayName);
					jsonObject.add(PGWidgetConstants.KEY_NAME,
							PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_NAME)));
					jsonObject.add(PGWidgetConstants.KEY_REVISIOM,
							PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_REVISION)));

					objMap.remove(DomainConstants.SELECT_TYPE);
					objMap.remove(DomainConstants.SELECT_NAME);
					objMap.remove(DomainConstants.SELECT_REVISION);
					objMap.remove(DomainConstants.SELECT_ID);
					boolean hasAccess = FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strId),
							PGWidgetConstants.ACCESS_READ);

					for (Entry<?, ?> entry : objMap.entrySet()) {
						strKey = (String) entry.getKey();
						strValue = PGWidgetUtil.checkNullValueforString((String) entry.getValue());
						if (!hasAccess) {
							if (strValue.equals(PGWidgetConstants.DENIED)) {
								jsonObject.add(strKey,
										EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource",
												context.getLocale(), "emxFramework.Access.NoAccess"));
							} else {
								jsonObject.add(strKey, strValue);
							}
						} else {
							jsonObject.add(strKey, strValue);
						}
					}
					jsonArr.add(jsonObject);
				}
			}
			output.add("data", jsonArr.build());
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, ERROR_PGSTUDYPROTOCOL_GETMYSTUDYPROTOCOL, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return output.build().toString();
	}

	/**
	 * This method returns information about Study Protocol object
	 * 
	 * @param context
	 * @param mpParamMAP
	 * @return
	 * @throws Exception
	 */
	public static String getStudyProtocolProperties(Context context, Map<?, ?> mpParamMAP) {
		String objectId = (String) mpParamMAP.get(DomainConstants.SELECT_ID);
		StringList objSelectablesList = (StringList) mpParamMAP.get(PGWidgetConstants.OBJ_SELECT);
		StringList slObjSelectables = new StringList();
		StringList slMultivalueSelectables = new StringList();
		String strEntry;
		StringList slMultipleValueList = null;
		StringBuilder multipleFinalValue = null;
		String strListTemp;

		for (String objSelectable : objSelectablesList) {
			if (objSelectable.contains(PGWidgetConstants.STR_FROM_OPEN)
					|| objSelectable.contains(PGWidgetConstants.STR_TO_OPEN)
					|| objSelectable.contains(STR_ATTRIBUTE_OPEN)) {
				slMultivalueSelectables.add(objSelectable);
			}
			slObjSelectables.addElement(objSelectable);
		}

		slObjSelectables.addElement(DomainConstants.SELECT_CURRENT);
		slObjSelectables.addElement(DomainConstants.SELECT_POLICY);
		slObjSelectables.addElement(DomainConstants.SELECT_STATES);
		slObjSelectables.addElement(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slObjSelectables.addElement(PGWidgetConstants.SELECT_PHYSICAL_ID);
		
		slObjSelectables.add("attribute[pgUPTPhyID]");
		slObjSelectables.add("from["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].to.id");
		slMultivalueSelectables.add("from["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].to.id");
		JsonObjectBuilder output = Json.createObjectBuilder();
		Map<?, ?> objMap = null;
		DomainObject doObject = null;
		JsonObjectBuilder jsonObject = null;
		String objPolicy = null;
		String strDisplayValue = null;
		String strKey = null;
		String strLanguage = context.getSession().getLanguage();

		try {
			if (UIUtil.isNotNullAndNotEmpty(objectId)) {
				doObject = DomainObject.newInstance(context, objectId);
				// Fix for 22x Upgrade MultiValueList Changes - Start
				objMap = doObject.getInfo(context, slObjSelectables, slMultivalueSelectables);
				// Fix for 22x Upgrade MultiValueList Changes - End

				if (null != objMap && objMap.size() > 0) {
					jsonObject = Json.createObjectBuilder();
					String strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
					objPolicy = (String) objMap.get(DomainConstants.SELECT_POLICY);
					objMap.remove(DomainConstants.SELECT_STATES);
					for (Entry<?, ?> entry : objMap.entrySet()) {
						strEntry = (String) entry.getKey();
						if (strEntry.contains(PGWidgetConstants.STR_FROM_OPEN)
								|| strEntry.contains(PGWidgetConstants.STR_TO_OPEN)
								|| strEntry.contains(STR_ATTRIBUTE_OPEN)) {
							multipleFinalValue = new StringBuilder();
							slMultipleValueList = PGWidgetUtil.returnStringListForObject((Object) entry.getValue());
							for (int i = 0; i < slMultipleValueList.size(); i++) {
								strListTemp = slMultipleValueList.get(i);
								if (multipleFinalValue.length() > 0) {
									if (strEntry.contains(STR_ATTRIBUTE_OPEN)) {
										multipleFinalValue.append(KEY_COMMA_SEPARATOR).append(strListTemp);
									} else {
										multipleFinalValue.append(PGWidgetConstants.STR_PIPE_SEPARATED_WITHSPACE)
												.append(strListTemp);
									}
								} else {
									if (strEntry.contains(PGIPCLASSIFICATION)) {
										strDisplayValue = EnoviaResourceBundle.getRangeI18NString(context,
												PGIPCLASSIFICATION, PGWidgetUtil.checkNullValueforString(strListTemp),
												strLanguage);
										multipleFinalValue.append(strDisplayValue);
									} else {
										multipleFinalValue.append(strListTemp);
									}
								}
							}
							jsonObject.add((String) entry.getKey(), multipleFinalValue.toString());
						} else {
							strKey = (String) entry.getKey();
							if (strKey.equals(DomainConstants.SELECT_TYPE)) {
								strDisplayValue = EnoviaResourceBundle.getTypeI18NString(context,
										PGWidgetUtil.checkNullValueforString((String) entry.getValue()), strLanguage);
								jsonObject.add((String) entry.getKey(), strDisplayValue);
							} else if (strKey.equals(DomainConstants.SELECT_POLICY)) {
								strDisplayValue = EnoviaResourceBundle.getAdminI18NString(context,
										PGWidgetConstants.KEY_POLICY,
										PGWidgetUtil.checkNullValueforString((String) entry.getValue()), strLanguage);
								jsonObject.add((String) entry.getKey(), strDisplayValue);
							} else {
								jsonObject.add((String) entry.getKey(),
										PGWidgetUtil.checkNullValueforString((String) entry.getValue()));
							}
						}
						
						String strRTId = PGWidgetUtil.extractMultiValueSelect(objMap, "from["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].to.id");
						if(UIUtil.isNotNullAndNotEmpty(strRTId)) {
							jsonObject.add("RoutePresent", true);
						}
					}
					JsonObject jsonObj = jsonObject.build();
					for (String objSelectable : objSelectablesList) {
						if (!jsonObj.containsKey(objSelectable)) {
							jsonObject.add(objSelectable, "");
						}

					}
					jsonObject.add(PGWidgetConstants.KEY_STATES,
							PGWidgetUtil.getObjectStates(context, objectId, objPolicy, strCurrent));
					output.add("StudyProtocolProperties", jsonObject.build());
				}
			}
		} catch (MatrixException ex) {
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return output.build().toString();
	}

	/**
	 * The method creates the JSON object to get the list of policy, Business Area
	 * and Product Category Platform for Study Protocol. the input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @return JSON object consisting of the information to be displayed
	 * @throws MatrixException
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getInitialDataOnCreate(Context context) throws MatrixException, Exception {

		JsonObjectBuilder jsonData = Json.createObjectBuilder();
		JsonObjectBuilder output = Json.createObjectBuilder();

		jsonData.add(PGWidgetConstants.KEY_POLICY, getPolicies(context));
		// jsonData.add(KEY_BUSINESSAREA, getBusinessArea(context));
		jsonData.add(PGWidgetConstants.KEY_TITLE, getUserPreferences(context, PGWidgetConstants.KEY_TITLE));
		jsonData.add(PGWidgetConstants.KEY_DESCRIPTION, getUserPreferences(context, PGWidgetConstants.KEY_DESCRIPTION));
		output.add("data", jsonData);
		return output.build().toString();
	}

	/**
	 * The method creates the JSON object to get the list of user/person preferences
	 * The input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @return Stringified JSON object consisting of the information to be displayed
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getUserPreferences(Context context, String preferenceName) throws Exception {
		String strPreferenceValue = MqlUtil.mqlCommand(context, "print person $1 select $2 dump $3",
				new String[] { context.getUser(), "property[" + preferenceName + "].value", "" });
		if (UIUtil.isNullOrEmpty(strPreferenceValue)) {
			strPreferenceValue = "Global Value";
		}
		return strPreferenceValue;
	}

	/**
	 * The method creates the JSON object to get the region preferences The input
	 * parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @return Stringified JSON object consisting of the information to be displayed
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getPGRegion(Context context) throws Exception {
		String strPreferenceSelects = "preference_Region";
		String strPref = MqlUtil.mqlCommand(context, "print person $1 select $2 dump $3",
				new String[] { context.getUser(), "property[" + strPreferenceSelects + "].value", "" });
		if (UIUtil.isNullOrEmpty(strPref)) {
			strPref = "India";
		}
		return strPref;
	}

	/**
	 * The method creates the JSON object to get the list of Policies for type Study
	 * Protocol. the input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @return JSON object consisting of the information to be displayed
	 * @throws MatrixException
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getPolicies(Context context) throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObject = null;
		String strPolicySignatureReferenceDoc = PropertyUtil.getSchemaProperty(context,
				POLICY_SIGNATURE_REFERENCE_SYMBOLIC);
		String strPolicySelfApproval = PropertyUtil.getSchemaProperty(context, "policy_pgSelfApproval");
		String sLanguage = context.getSession().getLanguage();
		String strSignatureReferencePolicy = i18nNow.getMXI18NString(strPolicySignatureReferenceDoc,
				DomainConstants.EMPTY_STRING, sLanguage, PGWidgetConstants.KEY_POLICY);
		String strSelfApproval = i18nNow.getMXI18NString(strPolicySelfApproval, DomainConstants.EMPTY_STRING, sLanguage,
				PGWidgetConstants.KEY_POLICY);

		jsonObject = Json.createObjectBuilder();
		jsonObject.add(PGWidgetConstants.KEY_NAME, strSignatureReferencePolicy);
		jsonObject.add(PGWidgetConstants.KEY_VALUE, strPolicySignatureReferenceDoc);
		jsonArr.add(jsonObject);
		jsonObject.add(PGWidgetConstants.KEY_NAME, strSelfApproval);
		jsonObject.add(PGWidgetConstants.KEY_VALUE, strPolicySelfApproval);
		jsonArr.add(jsonObject);

		output.add("data", jsonArr.build());

		return output.build().toString();
	}

	/**
	 * The method creates the JSON object to get the list of Business Area for type
	 * Study Protocol. the input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @return JSON object consisting of the information to be displayed
	 * @throws FrameworkException
	 * @throws Exception
	 *             When operation fails
	 */
	public static String getBusinessArea(Context context) throws FrameworkException {
		JsonObjectBuilder output = Json.createObjectBuilder();

		String strBusinessAreaRevision = "-";
		StringList slObjectSelects = new StringList(2);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		MapList mlPicklist = null;

		mlPicklist = DomainObject.findObjects(context, // Context
				pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA, // Type
				DomainConstants.QUERY_WILDCARD, // Name
				strBusinessAreaRevision, // Revision
				DomainConstants.QUERY_WILDCARD, // owner
				ArtworkConstants.VAULT_ESERVICE_PRODUCTION, // vault
				null, // Where clause
				true, // Consider Sub types
				slObjectSelects);// Bus select

		JsonArrayBuilder jsonArr = Json.createArrayBuilder();

		if (mlPicklist != null && !mlPicklist.isEmpty()) {
			mlPicklist.addSortKey(DomainConstants.SELECT_NAME, PGWidgetConstants.ASCENDING, PGWidgetConstants.STRING);
			mlPicklist.sort();
			JsonObjectBuilder jsonObject = null;
			Map<?, ?> objMap = null;
			String strId;
			String strName;
			for (int i = 0; i < mlPicklist.size(); i++) {
				jsonObject = Json.createObjectBuilder();
				objMap = (Map<?, ?>) mlPicklist.get(i);
				strId = PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID));
				strName = PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_NAME));
				jsonObject.add(KEY_ID, strId);
				jsonObject.add(PGWidgetConstants.KEY_NAME, strName);
				jsonObject.add(PGWidgetConstants.KEY_VALUE, strName);
				objMap.remove(DomainConstants.SELECT_NAME);
				objMap.remove(DomainConstants.SELECT_ID);

				jsonObject.add(PRODUCTCATEGORYPLATFORM, getProductCategoryPlatform(context, strId));
				jsonArr.add(jsonObject);
			}
		}

		output.add("data", jsonArr.build());

		return output.build().toString();
	}

	/**
	 * The method creates the JSON object to get the list of Product Category. the
	 * input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @return JSON object consisting of the information to be displayed
	 * @throws FrameworkException
	 * @throws Exception
	 *             When operation fails
	 */
	public static JsonArrayBuilder getProductCategoryPlatform(Context context, String strBusinessAreaId)
			throws FrameworkException {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		StringList slObjectSelects = new StringList(2);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		MapList mlProductCategory = null;
		DomainObject domObj = null;

		domObj = DomainObject.newInstance(context, strBusinessAreaId);

		String sWhereExpCategory = "attribute[" + ATTRIBUTE_PLATFORM_TYPE + "]=='" + PRODUCT_CATEGORY_PLATFORM + "'";

		mlProductCategory = domObj.getRelatedObjects(context, RELATIONSHIP_PLATFORM_TO_BUSINESSAREA, // relationshipPattern
				TYPE_PG_PLI_PLATFORM, // typePattern
				slObjectSelects, // objectSelects
				null, // relationshipSelects
				true, // getTo
				false, // getFrom
				(short) 1, // recurseToLevel
				sWhereExpCategory, // objectWhere
				null, // relationshipWhere
				0);// limit

		if (mlProductCategory != null && !mlProductCategory.isEmpty()) {
			mlProductCategory.addSortKey(DomainConstants.SELECT_NAME, PGWidgetConstants.ASCENDING,
					PGWidgetConstants.STRING);
			mlProductCategory.sort();
			JsonObjectBuilder jsonObject = null;
			Map<?, ?> objMap = null;
			for (int i = 0; i < mlProductCategory.size(); i++) {
				jsonObject = Json.createObjectBuilder();
				objMap = (Map<?, ?>) mlProductCategory.get(i);
				jsonObject.add(KEY_ID,
						PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID)));
				jsonObject.add(PGWidgetConstants.KEY_NAME,
						PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_NAME)));
				jsonObject.add(PGWidgetConstants.KEY_VALUE,
						PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_NAME)));

				objMap.remove(DomainConstants.SELECT_NAME);
				objMap.remove(DomainConstants.SELECT_ID);

				jsonArr.add(jsonObject);
			}
		}

		return jsonArr;
	}

	/**
	 * Method to create Study Protocol object for selected policy, title,
	 * description, business area
	 * 
	 * @param context
	 * @param mpParamMAP
	 * @return
	 * @throws Exception
	 */
	public static String createtStudyProtocol(Context context, Map<?, ?> mpParamMAP) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOutputProp = DomainConstants.EMPTY_STRING;

		try {
			String studyProtocolObjId = createMaster(context, mpParamMAP);
			String strObjSelectables = (String) mpParamMAP.get(PGWidgetConstants.OBJ_SELECT);
			StringList slObjectSelectables = new StringList();
			if (UIUtil.isNotNullAndNotEmpty(strObjSelectables)) {
				slObjectSelectables = StringUtil.split(strObjSelectables, KEY_COMMA_SEPARATOR);
			}
			Map<Object, Object> mpParamMAPProp = new HashMap<>();
			mpParamMAPProp.put(DomainConstants.SELECT_ID, studyProtocolObjId);
			mpParamMAPProp.put(PGWidgetConstants.OBJ_SELECT, slObjectSelectables);
			strOutputProp = PGStudyProtocol.getStudyProtocolProperties(context, mpParamMAPProp);
		} catch (Exception ex) {
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			throw ex;
		}
		return strOutputProp;
	}

	/**
	 * Method to create Master
	 * 
	 * @param context
	 * @param uploadParamsMap
	 * @return
	 * @throws Exception
	 */
	public static String createMaster(Context context, Map<?, ?> uploadParamsMap) throws Exception {
		final String POLICY_PGPKGSIGNATUREREFERENCEDOC = PropertyUtil.getSchemaProperty(context,
				POLICY_SIGNATURE_REFERENCE_SYMBOLIC);
		String studyProtocolId = "";
		StringList slAttrValues;
		boolean isTransactionActive = false;
		try {
			String policy = (String) uploadParamsMap.get(PGWidgetConstants.KEY_POLICY);
			String mDescription = (String) uploadParamsMap.get(PGWidgetConstants.KEY_DESCRIPTION);
			String title = (String) uploadParamsMap.get(DomainConstants.ATTRIBUTE_TITLE);
			String businessArea = (String) uploadParamsMap.get(KEY_BUSINESSAREA);
			String productCategoryPlatform = (String) uploadParamsMap.get(PRODUCTCATEGORYPLATFORM);
			String pgIPClassification = (String) uploadParamsMap.get(IPCLASSIFICATION);
            String pgUPIPhysicalId = (String) uploadParamsMap.get(PGWidgetConstants.ATTRIBUTE_PGUPTPHYSICALID);
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;

			Map<String, Object> mAttrMap = new HashMap<>();
			mAttrMap.put(PGIPCLASSIFICATION, pgIPClassification);
			mAttrMap.put(ACCESSTYPE, "Specific");
			mAttrMap.put(PGIPPROJECTSECURITY, "No");
			mAttrMap.put(PGBUSINESSAREA, businessArea);
			if (productCategoryPlatform.contains(KEY_COMMA_SEPARATOR)) {
				slAttrValues = StringUtil.split(productCategoryPlatform, KEY_COMMA_SEPARATOR);
				mAttrMap.put(PGPRODUCTCATEGORYPLATFORM, slAttrValues);
			} else {
				mAttrMap.put(PGPRODUCTCATEGORYPLATFORM, productCategoryPlatform);
			}

			/// read region from user preference
			if(UIUtil.isNotNullAndNotEmpty(pgUPIPhysicalId)) {
				mAttrMap.put(ATTRIBUTE_PGREGION, PGWidgetUtil.getRegionFromUPT(context, PGWidgetUtil.convertPhyIdToObjId(context, pgUPIPhysicalId)));
			}
			CommonDocument object = (CommonDocument) DomainObject.newInstance(context, CommonDocument.TYPE_DOCUMENTS);

			// parameters->context,type,name,revision,policy,description,vault,title,language,parentObject,parentRelName,isFrom,AttributeMap,objectGeneratorRevision
			object = object.createAndConnect(context, TYPE_PG_PKG_STUDY_PROTOCOL, DomainConstants.EMPTY_STRING, null,
					policy, mDescription, null, title, null, null, null, null, mAttrMap);

			object.setOwner(context, context.getUser());
			studyProtocolId = object.getObjectId(context);
			PGWidgetUtil.updateUPTPhyIdByInterface(context, studyProtocolId, pgUPIPhysicalId);
			connectSecurityGroups(context, uploadParamsMap, studyProtocolId);
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
			}
			logger.log(Level.SEVERE, ERROR_PGSTUDYPROTOCOL_GETMYSTUDYPROTOCOL, e);
			throw new Exception(e);
		}
		return studyProtocolId;
	}
	
	/**
	 * Method to Study Protocol object to IP Control Class
	 * 
	 * @param context
	 * @param uploadParamsMap
	 * @param domObjectId
	 * @return
	 * @throws Exception
	 */
	public static void connectSecurityGroups(Context context, Map<?, ?> uploadParamsMap, String domObjectId)
			throws Exception {
		try {
			String languageStr = context.getLocale().getDisplayName();
			String strpgIPClassification = (String) uploadParamsMap.get(IPCLASSIFICATION);
			String strUPTId = (String) uploadParamsMap.get(PGWidgetConstants.ATTRIBUTE_PGUPTPHYSICALID);
			String strRangeRestricted = EnoviaResourceBundle.getProperty(context, "ExportControl",
					"pgExportControl.IPClassification.Restricted", languageStr);
			String strRangeHighlyRestricted = EnoviaResourceBundle.getProperty(context, "ExportControl",
					"pgExportControl.IPClassification.HighlyRestricted", languageStr);
			logger.log(Level.INFO, "strUPTId :: {0} " , strUPTId);
			IRMUPT domIRMTemplate = new IRMUPT(context, strUPTId);
			MapList mlSecurityGroups = new MapList();
			if (strpgIPClassification.equals(strRangeHighlyRestricted)) {
				Map map = domIRMTemplate.getHighlyRestrictedClass();
				mlSecurityGroups = (MapList) map.get("attribute[pgUPTHighlyRestrictedIPClass]");
			} else if (strpgIPClassification.equals(strRangeRestricted)) {
				Map map = domIRMTemplate.getBusinessUseClass();
				mlSecurityGroups = (MapList) map.get("attribute[pgUPTBusinessUseIPClass]");
			}
			StringList slSecurityGroups = new StringList();
			if (mlSecurityGroups != null) {
				for (int i = 0; i < mlSecurityGroups.size(); i++) {
					String strTemp = (String) ((Map) (mlSecurityGroups.get(i))).get(DomainConstants.SELECT_NAME);
					if (UIUtil.isNotNullAndNotEmpty(strTemp))
						slSecurityGroups.add(strTemp);
				} 
			}
			Map<String, String> argsMap = new HashMap<>();
			argsMap.put("strpgIPClassification", strpgIPClassification);
			if(slSecurityGroups != null && !slSecurityGroups.isEmpty()) {
				argsMap.put("strpgIPRestrictedSecurityGroups", slSecurityGroups.join(PGWidgetConstants.KEY_PIPE_SEPARATOR));
			}
			argsMap.put("strpgProjectSecurity", "No");
			argsMap.put("strpgProjectSecurityGroup", DomainConstants.EMPTY_STRING);
			argsMap.put(PGWidgetConstants.KEY_OBJECT_ID, domObjectId);
			JPO.invoke(context, "pgIPSecurityCommonUtil", null, "connectSecurityGroups", JPO.packArgs(argsMap));
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * The method sets updated attribute values. the input parameters are:
	 * 
	 * @param context
	 *            The enovia Context object
	 * @return JSON object consisting of the information to be displayed
	 * @throws MatrixException,ParseException
	 * @throws Exception
	 *             When operation fails
	 */
	@SuppressWarnings("rawtypes")
	public static String setUpdatedValues(Context context, String strInputData) throws MatrixException, ParseException {
		HashMap<String, Object> mpParamMAP = new HashMap<>();
		Map<String, Object> mAttrMap = new HashMap<>();
		DomainObject doObject = null;
		JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strDescription = "";
		String strExpression = "";
		String strValue = "";
		String strExp = "";
		String strAttributeType = "";
		String strName = "";
		String strRelName = "";
		String strRelExpression = "";
		String strRelationship = "";

		String strSideToConnect = "";
		StringList slIdList = new StringList();

		String strObjectId = jsonInputInfo.getString(DomainConstants.SELECT_ID);
		StringList slAttrValues;

		JsonArray jsonInputArray = jsonInputInfo.getJsonArray("updatedValues");
		new SimpleDateFormat(PGWidgetConstants.KEY_SIMPLE_DATEFORMAT);
		new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), context.getLocale());
		StringList slObjSelectables = new StringList();
		JsonObject jsonObjectValue = null;
		StringList slNameList = new StringList();
		for (int i = 0; i < jsonInputArray.size(); i++) {
			JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);
			strExpression = jsonElement.getString(PGWidgetConstants.KEY_EXPR);

			slObjSelectables.add(strExpression);
			strExp = PGWidgetUtil.getFormattedExpression(strExpression);
			strAttributeType = jsonElement.getString(KEY_ATTR_TYPE);

			if (strAttributeType.equalsIgnoreCase(ATTR_TYPE_DROPPABLE)) {
				jsonObjectValue = jsonElement.getJsonObject(PGWidgetConstants.KEY_VALUE);
			} else {
				strValue = jsonElement.getString(PGWidgetConstants.KEY_VALUE);
			}

			if (strAttributeType.equalsIgnoreCase(ATTR_TYPE_MULTIVALUE)) {
				if (strValue.contains(KEY_COMMA_SEPARATOR)) {
					slAttrValues = StringUtil.split(strValue, KEY_COMMA_SEPARATOR);
					mAttrMap.put(strExp, slAttrValues);
				} else {
					mAttrMap.put(strExp, strValue);
				}
			} else if (strAttributeType.equalsIgnoreCase(ATTR_TYPE_SINGLEVALUE)) {
				mAttrMap.put(strExp, strValue);
			} else if (strAttributeType.equalsIgnoreCase(ATTR_TYPE_BASIC)) {
				strDescription = strValue;
			} else if (strAttributeType.equalsIgnoreCase(ATTR_TYPE_DROPPABLE)) {
				if (jsonObjectValue.containsKey("relExpression")) {
					strRelExpression = jsonObjectValue.getString("relExpression");
				}
				if (jsonObjectValue.containsKey(KEY_ATTR_RELATIONSHIP)) {
					strRelName = jsonObjectValue.getString(KEY_ATTR_RELATIONSHIP);
				}
				if (jsonObjectValue.containsKey(KEY_ATTR_SIDETOCONNECT)) {
					strSideToConnect = jsonObjectValue.getString(KEY_ATTR_SIDETOCONNECT);
				}

				if (UIUtil.isNotNullAndNotEmpty(strRelName) && UIUtil.isNotNullAndNotEmpty(strRelExpression)) {
					slObjSelectables.add(strRelExpression);
				}
				processDroppableField(context, mAttrMap, strExp, slIdList, strObjectId, slNameList, jsonObjectValue,
						strSideToConnect);
			} else if (strAttributeType.equalsIgnoreCase(ATTR_TYPE_DATE)) {
				if (UIUtil.isNotNullAndNotEmpty(strValue)) {
					strValue = PGWidgetUtil.getEMatrixDateFormat(strValue);
				}
				mAttrMap.put(strExp, strValue);
			} else if (strAttributeType.equalsIgnoreCase(ATTR_TYPE_PICKLIST)) {
				strName = jsonElement.getString(DomainConstants.SELECT_NAME);
				strRelationship = jsonElement.getString(KEY_ATTR_RELATIONSHIP);
				strSideToConnect = jsonElement.getString(KEY_ATTR_SIDETOCONNECT);
				disconnectAndConnectPicklist(context, strObjectId, strName, strValue, strRelationship,
						strSideToConnect);
			}
		}
		if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			doObject = DomainObject.newInstance(context, strObjectId);

			if (BusinessUtil.isNotNullOrEmpty(strDescription)) {
				doObject.setDescription(context, strDescription);
			}
			if (!mAttrMap.isEmpty()) {
				doObject.setAttributeValues(context, mAttrMap);
			}
		}
		mpParamMAP.put(DomainConstants.SELECT_ID, strObjectId);
		mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, slObjSelectables);
		return getStudyProtocolProperties(context, mpParamMAP);
	}

	/**
	 * The method retrives connection ids and names of droppable objects
	 * 
	 * @param context
	 *            The enovia Context object
	 * @param mAttrMap
	 *            map of attributes to be saved
	 * @param strExp
	 *            expression
	 * @param slIdList
	 *            list of ids to be connected
	 * @param strObjectId
	 *            object id
	 * @param slNameList
	 * @param jsonObjectValue
	 *            input json
	 * @return nothing
	 * @throws Exception
	 *             When operation fails
	 */
	private static void processDroppableField(Context context, Map<String, Object> mAttrMap, String strExp,
			StringList slIdList, String strObjectId, StringList slNameList, JsonObject jsonObjectValue,
			String strSideToConnect) throws FrameworkException {
		JsonArray jsonArrayFieldValue = jsonObjectValue.getJsonArray("fieldvalue");
		String strTypePattern = jsonObjectValue.getString("typeAllowed");

		String strRelName = DomainConstants.EMPTY_STRING;
		String strRelExpression = DomainConstants.EMPTY_STRING;
		String strGetTo = DomainConstants.EMPTY_STRING;
		String strGetFrom = DomainConstants.EMPTY_STRING;

		if (jsonObjectValue.containsKey(KEY_ATTR_RELATIONSHIP)) {
			strRelName = jsonObjectValue.getString(KEY_ATTR_RELATIONSHIP);
			strRelExpression = jsonObjectValue.getString("relExpression");
			strGetTo = jsonObjectValue.getString("getTo");
			strGetFrom = jsonObjectValue.getString("getFrom");
		}

		String strName;
		String strId;
		for (int j = 0; j < jsonArrayFieldValue.size(); j++) {
			JsonObject jsonValueElement = (JsonObject) jsonArrayFieldValue.get(j);
			strName = jsonValueElement.getString(DomainConstants.SELECT_NAME);
			strId = jsonValueElement.getString(PGWidgetConstants.SELECT_PHYSICAL_ID);
			if (UIUtil.isNullOrEmpty(strRelName)) {
				slNameList.add(strName);
			} else {
				slIdList.add(strId);
			}
		}
		if (UIUtil.isNullOrEmpty(strRelName)) {
			mAttrMap.put(strExp, String.join(PGWidgetConstants.KEY_PIPE_SEPARATOR, slNameList));
		}

		if (UIUtil.isNotNullAndNotEmpty(strRelName) && UIUtil.isNotNullAndNotEmpty(strRelExpression)) {
			connectDisconnectForDroppable(context, strRelName, slIdList, strTypePattern, strGetTo, strGetFrom,
					strObjectId, strSideToConnect);
		}
	}

	/**
	 * The method disconnects and connects droppable objects
	 * 
	 * @param context
	 *            The enovia Context object
	 * @param strRelName
	 *            Relationship name
	 * @param slIdList
	 *            list of ids
	 * @param strTypePattern
	 *            type of object
	 * @param strGetTo
	 *            to direction
	 * @param strGetFrom
	 *            from direction
	 * @param strObjectId
	 *            object id
	 * @return nothing
	 * @throws Exception
	 *             When operation fails
	 */
	private static void connectDisconnectForDroppable(Context context, String strRelName, StringList slIdList,
			String strTypePattern, String strGetTo, String strGetFrom, String strObjectId, String strSideToConnect)
			throws FrameworkException {
		boolean relDirection = false;
		Map<String, String> mapIds = new HashMap<>();
		List<String> relToDisconnect = new ArrayList<>();
		List<String> relToConnect = new ArrayList<>();
		DomainObject domObj = DomainObject.newInstance(context, strObjectId);

		StringList busSelects = new StringList(1);
		StringList relSelects = new StringList(1);
		relSelects.add(DomainRelationship.SELECT_ID);
		busSelects.add(DomainConstants.SELECT_ID);

		MapList mapList = domObj.getRelatedObjects(context, strRelName, // relationshipPattern
				PGWidgetUtil.getPattern(context, strTypePattern), // typePattern
				busSelects, // objectSelects
				relSelects, // relationshipSelects
				Boolean.parseBoolean(strGetTo), // getTo
				Boolean.parseBoolean(strGetFrom), // getFrom
				(short) 1, // recurseToLevel
				null, // objectWhere
				null, // relationshipWhere
				0);// limit
		Iterator<?> itr = mapList.iterator();
		while (itr.hasNext()) {
			Map<?, ?> mpData = (Map<?, ?>) itr.next();

			String relId = (String) mpData.get(DomainRelationship.SELECT_ID);
			String objId = (String) mpData.get(DomainConstants.SELECT_ID);
			mapIds.put(objId, relId);
		}

		for (Map.Entry mapElement : mapIds.entrySet()) {
			String key = (String) mapElement.getKey();
			String value = (String) mapElement.getValue();
			if (!slIdList.contains(key) || slIdList.isEmpty()) {
				relToDisconnect.add(value);
			}

		}
		for (int k = 0; k < slIdList.size(); k++) {
			if (!mapIds.containsKey(slIdList.get(k))) {
				relToConnect.add(slIdList.get(k));
			}
		}

		if (!relToDisconnect.isEmpty()) {
			DomainRelationship.disconnect(context,
					(String[]) relToDisconnect.toArray(new String[relToDisconnect.size()]));
		}
		if ("from".equalsIgnoreCase(strSideToConnect))
			relDirection = true;
		if (!relToConnect.isEmpty()) {
			DomainRelationship.connect(context, domObj, strRelName, relDirection,
					BusinessUtil.toStringArray(relToConnect));
		}
	}

	/**
	 * Method to get Study Protocol Properties with given selectables
	 * 
	 * @param context
	 * @param mpParamMAP
	 * @return
	 * @throws Exception
	 */
	public static String getStudyLegDetails(Context context, Map<?, ?> mpParamMAP) throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strOjectId = (String) mpParamMAP.get(PGWidgetConstants.KEY_OBJECT_ID);
		String strSelectables = (String) mpParamMAP.get(PGWidgetConstants.OBJ_SELECT);
		StringList objectSelects = new StringList();
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		objectSelects.add(DomainConstants.SELECT_TYPE);
		objectSelects.add(SELECT_DESCRIPTION);
		objectSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		objectSelects.add(SELECT_ATTRIBUTE_PGNUMBEROFPANELISTS);
		objectSelects.add(SELECT_ATTRIBUTE_PGPRODUCTCODEPANELISTS);
		objectSelects.add(SELECT_PGAEROSOL_TYPE);
		DomainConstants.MULTI_VALUE_LIST.add(SELECT_RELATED_BA);
		DomainConstants.MULTI_VALUE_LIST.add(SELECT_RELATED_PRODUCT_FORM);
		objectSelects.add(SELECT_RELATED_BA);
		objectSelects.add(SELECT_RELATED_PRODUCT_FORM);

		String strLanguage = context.getSession().getLanguage();
		String strDisplayValue = null;
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		MapList mlStudyLegDetails = null;
		String strReturn = "";
		Object strPlant;

		if (BusinessUtil.isNotNullOrEmpty(strOjectId)) {
			DomainObject domObj = DomainObject.newInstance(context, strOjectId);
			StringList relSelects = new StringList(2);
			relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			relSelects.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
			relSelects.add(EXPR_SITE_FOR_PRODUCT_MANUFACTURING);

			if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
				relSelects.addAll(strSelectables.split(KEY_COMMA_SEPARATOR));
			}

			relSelects.remove(DomainConstants.SELECT_TYPE);
			relSelects.remove(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			relSelects.remove(SELECT_ATTRIBUTE_PGNUMBEROFPANELISTS);
			relSelects.remove(SELECT_ATTRIBUTE_PGPRODUCTCODEPANELISTS);
			String strRelPattern = RELATIONSHIP_PGLEGINPUT + KEY_COMMA_SEPARATOR + RELATIONSHIP_PGLEGPROCESS;

			JsonObjectBuilder jsonObject = null;
			jsonObject = Json.createObjectBuilder();
			StringList hierarchyList = null;
			Pattern sTypePattern = new Pattern(TYPE_PGSTUDYLEG);
			sTypePattern.addPattern(DomainConstants.TYPE_PART);

			mlStudyLegDetails = domObj.getRelatedObjects(context, strRelPattern, // relationshipPattern
					sTypePattern.getPattern(), // typePattern
					objectSelects, // objectSelects
					relSelects, // relationshipSelects
					false, // getTo
					true, // getFrom
					(short) 0, // recurseToLevel
					null, // objectWhere
					null, // relationshipWhere
					0);// limit

			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_RELATED_PRODUCT_FORM);
			DomainConstants.MULTI_VALUE_LIST.remove(SELECT_RELATED_BA);
			if (mlStudyLegDetails != null && !mlStudyLegDetails.isEmpty()) {
				Map<?, ?> objMap = null;
				String strKey = "";
				String strStudyLeg = "";
				for (int i = 0; i < mlStudyLegDetails.size(); i++) {
					objMap = (Map<?, ?>) mlStudyLegDetails.get(i);
					for (int j = 0; j < relSelects.size(); j++) {
						strKey = relSelects.get(j);
						if (EXPR_SITE_FOR_PRODUCT_MANUFACTURING.equals(strKey)) {
							strPlant = objMap.get(strKey);
							if (strPlant instanceof StringList) {
								strReturn = (ConfigurationUtil.convertStringListToString(context,
										(StringList) strPlant));
							} else {
								strReturn = (String) strPlant;
							}

							jsonObject.add(PRODUCT_SITE_EXPR, PGWidgetUtil.checkNullValueforString(strReturn));
						} else {
							if (!PRODUCT_SITE_EXPR.equals(strKey)) {
								jsonObject.add(strKey,
										PGWidgetUtil.checkNullValueforString((String) objMap.get(strKey)));
							}
						}
					}
					for (int j = 0; j < objectSelects.size(); j++) {
						strKey = objectSelects.get(j);
						if (strKey.equals(DomainConstants.SELECT_TYPE)) {
							strDisplayValue = EnoviaResourceBundle.getTypeI18NString(context,
									PGWidgetUtil.checkNullValueforString((String) objMap.get(strKey)), strLanguage);
							jsonObject.add(strKey, strDisplayValue);
						} else {
							strDisplayValue = PGWidgetUtil.extractMultiValueSelect(objMap, strKey);
							jsonObject.add(strKey, PGWidgetUtil.checkNullValueforString(strDisplayValue));
						}
					}

					if ("1".equalsIgnoreCase((String) objMap.get("level"))) {
						strStudyLeg = (String) objMap.get(DomainConstants.SELECT_NAME);
						hierarchyList = new StringList();
						hierarchyList.add(strStudyLeg);
					} else {
						hierarchyList = new StringList();
						hierarchyList.add(strStudyLeg);
						hierarchyList.add((String) objMap.get(DomainConstants.SELECT_NAME));
					}
					jsonObject.add(KEY_HIERARCHY, hierarchyList.toString());
					jsonArr.add(jsonObject);
				}
				output.add("data", jsonArr.build());
			}
		}
		return output.build().toString();
	}

	/**
	 * Method to remove Study Leg or Product Part connected to Study Protocol object
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static String removeStudyLegOrProductPart(Context context, String strInputData) throws Exception {
		boolean isCtxtPushed = false;
		try {
			HashMap<String, Object> mpParamMAP = new HashMap<>();
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strRelIds = jsonInputData.getString("RelIds");
			String strTypeOfSelectedObj = jsonInputData.getString("TypeOfSelectedObj");
			String strIdOfSelectedObj = jsonInputData.getString("IdOfSelectedObj");
			String strStudyProtocolId = jsonInputData.getString("StudyProtocolId");
			String strSelectables = jsonInputData.getString(PGWidgetConstants.OBJ_SELECT);

			StringList relIdsList = StringUtil.split(strRelIds, KEY_COMMA_SEPARATOR);
			StringList typeList = StringUtil.split(strTypeOfSelectedObj, KEY_COMMA_SEPARATOR);
			StringList slIdsList = StringUtil.split(strIdOfSelectedObj, KEY_COMMA_SEPARATOR);
			StringList slIdsToDelete = new StringList();
			for (int i = 0; i < slIdsList.size(); i++) {
				if (TYPE_PGSTUDYLEG.equalsIgnoreCase(typeList.get(i))) {
					slIdsToDelete.add(slIdsList.get(i));
				}
			}
			//Context pushed reason: Logged-in user may not have access, but it is functionally required
			ContextUtil.pushContext(context);
			isCtxtPushed = true;
			if (!relIdsList.isEmpty()) {
				DomainRelationship.disconnect(context, BusinessUtil.toStringArray(relIdsList));
			}
			if (!slIdsToDelete.isEmpty()) {
				DomainObject.deleteObjects(context, BusinessUtil.toStringArray(slIdsToDelete));
			}
			mpParamMAP.put(PGWidgetConstants.KEY_OBJECT_ID, strStudyProtocolId);
			mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, strSelectables);
			return getStudyLegDetails(context, mpParamMAP);
		} finally {
			if (isCtxtPushed) {
				ContextUtil.popContext(context);
			}
		}
	}

	/**
	 * Method to create and connect Study Leg to Study Protocol object
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static String createStudyLeg(Context context, Map<?, ?> mpParamMAP) throws Exception {
		Json.createObjectBuilder();
		HashMap<String, Object> mpParam = new HashMap<>();

		String strtitle = (String) mpParamMAP.get(DomainConstants.ATTRIBUTE_TITLE);
		String strDescription = (String) mpParamMAP.get(SELECT_DESCRIPTION);
		String strStudyProtocolId = (String) mpParamMAP.get(PGWidgetConstants.KEY_OBJECT_ID);
		String strSelectables = (String) mpParamMAP.get(PGWidgetConstants.OBJ_SELECT);
		DomainObject domStudyProtocol = DomainObject.newInstance(context, strStudyProtocolId);

		String strAutoNewName = FrameworkUtil.autoName(context,
				FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, TYPE_PGSTUDYLEG, true),
				LEG_NAME_PREFIX, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING,
				DomainConstants.EMPTY_STRING, true, false);

		DomainObject domStudyLegObject = DomainObject.newInstance(context);
		domStudyLegObject.createAndConnect(context, TYPE_PGSTUDYLEG, strAutoNewName, RELATIONSHIP_PGLEGPROCESS,
				domStudyProtocol, true);
		domStudyLegObject.setOwner(context, context.getUser());
		domStudyLegObject.setDescription(context, strDescription);
		domStudyLegObject.setAttributeValue(context, DomainConstants.ATTRIBUTE_TITLE, strtitle);
		String strStudyLegObjectId = domStudyLegObject.getObjectId(context);

		performPostProcessLegCreate(context, strStudyLegObjectId, domStudyProtocol);
		mpParam.put(PGWidgetConstants.KEY_OBJECT_ID, strStudyProtocolId);
		mpParam.put(PGWidgetConstants.OBJ_SELECT, strSelectables);

		return getStudyLegDetails(context, mpParam);
	}

	/**
	 * Method to post processing for Study Leg
	 * 
	 * @param context
	 * @param strStudyLegId
	 * @param domStudyProtocol
	 * @return
	 * @throws Exception
	 */
	public static void performPostProcessLegCreate(Context context, String strStudyLegId, DomainObject domStudyProtocol)
			throws FrameworkException {
		boolean isContextPushed = false;
		try {
			StringList busSelects = new StringList(DomainConstants.SELECT_NAME);
			MapList mlStudyLegData = domStudyProtocol.getRelatedObjects(context, RELATIONSHIP_PGLEGPROCESS, // relationshipPattern
					TYPE_PGSTUDYLEG, // typePattern
					busSelects, // objectSelects
					null, // relationshipSelects
					false, // getTo
					true, // getFrom
					(short) 1, // recurseToLevel
					DomainConstants.EMPTY_STRING, // objectWhere
					null, // relationshipWhere
					0);// limit
			//Context pushed reason: Logged-in user may not have access, but it is functionally required
			ContextUtil.pushContext(context);
			isContextPushed = true;
			String strLegName = STRING_LEGONE;
			String strLegPrefix = STRING_LEG;
			if (mlStudyLegData.size() > 1) {
				mlStudyLegData.sort(DomainConstants.SELECT_NAME, PGWidgetConstants.DESCENDING,
						PGWidgetConstants.STRING);
				Map<?, ?> map = (Map<?, ?>) mlStudyLegData.get(0);
				String sName = (String) map.get(DomainConstants.SELECT_NAME);
				String sNum = sName.substring(4, sName.length());
				int iNum = Integer.parseInt(sNum);
				iNum++;
				StringBuilder sFinalName = new StringBuilder();
				sFinalName.append(strLegPrefix).append(iNum);
				strLegName = sFinalName.toString().trim();
			}
			String cmd = "mod bus $1 name '$2' revision '$3'";
			MqlUtil.mqlCommand(context, cmd, strStudyLegId, strLegName, String.valueOf(System.currentTimeMillis()));
		} finally {
			if (isContextPushed)
				ContextUtil.popContext(context);
		}
	}

	/**
	 * Method to retrieve picklist values for a field of Study Protocol
	 * 
	 * @param context
	 * @param pgPicklistNames
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static JsonObject getStudyProtocolPicklistValues(Context context, String pgPicklistNames) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		StringList pgPicklistNamesList = new StringList();

		Map<String, String> mapTemp;
		Set<String> setUniques;
		String strId;

		if (UIUtil.isNotNullAndNotEmpty(pgPicklistNames)) {
			pgPicklistNamesList.addAll(pgPicklistNames.split(KEY_COMMA_SEPARATOR));
		}

		String pgPicklistName = "";
		StringBuilder sbWhere = null;
		StringList slSelects = null;
		MapList mlPlants, mlRDSites = null;
		Map<String, String> mapParams = null;
		JsonArrayBuilder jsonArrBlr = null;
		JsonObjectBuilder jsonTempBldr = null;

		if (null != pgPicklistNamesList && !pgPicklistNamesList.isEmpty()) {
			for (int iter = 0; iter < pgPicklistNamesList.size(); iter++) {
				pgPicklistName = pgPicklistNamesList.get(iter);
				try {
					sbWhere = new StringBuilder(DomainConstants.SELECT_CURRENT).append(" != New && ")
							.append(DomainConstants.SELECT_CURRENT).append(" != Inactive");
					slSelects = new StringList();
					slSelects.add(DomainConstants.SELECT_NAME);
					slSelects.add(DomainConstants.SELECT_ID);
					mlPlants = DomainObject.findObjects(context, // Context
							pgPicklistName, // Type
							DomainConstants.QUERY_WILDCARD, // Name
							DomainConstants.QUERY_WILDCARD, // Revision
							DomainConstants.QUERY_WILDCARD, // Owner
							DomainConstants.QUERY_WILDCARD, // Vault
							sbWhere.toString(), // Where clause
							false, // Consider Sub types
							slSelects);// Bus select
					if (PICKLIST_TYPE_PLANT.equals(pgPicklistName)) {
						mapParams = new HashMap<>();
						mapParams.put("pgPicklistName", VT_PLANT_RD_SITES);
						mlRDSites = JPO.invoke(context, "pgVPDPickList", null, "getPickListRangeValuesForTable",
								JPO.packArgs(mapParams), MapList.class);
						mlPlants.addAll(mlRDSites);
					}
					jsonArrBlr = Json.createArrayBuilder();
					setUniques = new HashSet<>();
					if (mlPlants != null && !mlPlants.isEmpty()) {
						jsonTempBldr = null;
						mlPlants.sort(DomainConstants.SELECT_NAME, PGWidgetConstants.ASCENDING,
								PGWidgetConstants.STRING);
						for (int j = 0; j < mlPlants.size(); j++) {
							mapTemp = (Map<String, String>) mlPlants.get(j);
							strId = mapTemp.get(DomainConstants.SELECT_ID).toString();
							if (!setUniques.contains(strId)) {
								jsonTempBldr = Json.createObjectBuilder();
								jsonTempBldr.add(DomainConstants.SELECT_NAME,
										mapTemp.get(DomainConstants.SELECT_NAME).toString());
								jsonTempBldr.add(PGWidgetConstants.KEY_VALUE,
										mapTemp.get(DomainConstants.SELECT_NAME).toString());
								jsonTempBldr.add(PGWidgetConstants.KEY_OBJECT_ID, strId);
								jsonArrBlr.add(jsonTempBldr.build());
								setUniques.add(strId);
							}
						}
					}
					output.add(pgPicklistName, jsonArrBlr.build());
				} catch (MatrixException ex) {
					output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
					output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
				}
			}
		}

		return output.build();
	}

	/**
	 * Method to connect picklist objects to Study Protocol object
	 * 
	 * @param context
	 * @param sObjectId
	 * @param typeName
	 * @param newIds
	 * @param relName
	 * @param direction
	 * @return
	 * @throws Exception
	 */
	public static DomainRelationship disconnectAndConnectPicklist(Context context, String sObjectId, String typeName,
			String newIds, String relName, String direction) throws MatrixException {
		DomainRelationship drship = null;
		String vaultPattern = pgApolloConstants.VAULT_ESERVICE_PRODUCTION;
		StringList objectSelects = new StringList(2);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		StringList connectIDs = new StringList();
		StringList newIdList = new StringList();
		Map<?, ?> mDevStudy;
		String newId;
		String selectRel = "from[" + relName + "].to.id";
		boolean relDirection = false;

		if (UIUtil.isNotNullAndNotEmpty(newIds)) {
			newIdList.addAll(newIds.split(KEY_COMMA_SEPARATOR));
		}

		StringList oldIDS = BusinessUtil.getInfoList(context, sObjectId, selectRel);

		MapList mlMap1 = DomainObject.findObjects(context, // Context
				typeName, // Type
				DomainConstants.QUERY_WILDCARD, // Name
				DomainConstants.QUERY_WILDCARD, // Revision
				DomainConstants.QUERY_WILDCARD, // Owner
				vaultPattern, // Vault
				null, // Where clause
				false, // Consider Sub types
				objectSelects);// Bus select
		for (int k = 0; k < mlMap1.size(); k++) {
			mDevStudy = (Map<?, ?>) mlMap1.get(k);
			newId = (String) mDevStudy.get(DomainConstants.SELECT_ID);
			if (newIdList.contains(newId)) {
				connectIDs.add((String) mDevStudy.get(DomainConstants.SELECT_ID));
			}
		}

		if ("from".equalsIgnoreCase(direction))
			relDirection = true;
		disconnectAndConnect(context, sObjectId, oldIDS, connectIDs, relName, relDirection);

		return drship;
	}

	/**
	 * Common method for disconnect and connect a) Disconnects the objectId with the
	 * given relationship and with from/to disconnectObjectId b) Connects tht
	 * objectId with the given relationship and with from/to connectObjectId
	 *
	 * @param context
	 *            the matrix context
	 * @param objectId
	 *            the objectId
	 * @param disconnectObjectId
	 *            the objectId to be disconnected
	 * @param connectObjectId
	 *            the objectId to be connected
	 * @param strRelationship
	 *            the relationship
	 * @param isFromside
	 *            true if the objectId is from side
	 * @return DomainRelationship new DomainRealtionship
	 * @throws FrameworkException,MatrixException
	 * @throws Exception
	 *             if operation fails
	 */
	public static DomainRelationship disconnectAndConnect(Context context, String objectId,
			StringList disconnectObjectId, StringList connectObjectId, String relationship, boolean isFromside)
			throws MatrixException {
		DomainRelationship drship = null;
		String disconnectId = "";
		String connectId = "";

		RelationshipType relType = new RelationshipType(relationship);
		DomainObject doObj = DomainObject.newInstance(context, objectId);
		if (null != disconnectObjectId && !disconnectObjectId.isEmpty()) {
			int disconnectObjectIdSize = disconnectObjectId.size();
			for (int i = 0; i < disconnectObjectIdSize; i++) {
				disconnectId = disconnectObjectId.get(i);
				if (UIUtil.isNotNullAndNotEmpty(disconnectId))
					doObj.disconnect(context, relType, isFromside, DomainObject.newInstance(context, disconnectId));
			}
		}
		if (null != connectObjectId && !connectObjectId.isEmpty()) {
			int connectObjectIdSize = connectObjectId.size();
			for (int j = 0; j < connectObjectIdSize; j++) {
				connectId = connectObjectId.get(j);
				if (UIUtil.isNotNullAndNotEmpty(connectId))
					drship = doObj.addRelatedObject(context, relType, !isFromside, connectId);
			}
		}

		return drship;
	}

	/**
	 * This method is used to get Ranges of Attribute
	 * 
	 * @param context
	 * @param strInputData
	 * @throws MatrixException
	 * @throws Exception
	 */
	public static String getInitialDataOnEdit(Context context, String strInputData) throws MatrixException {
		JsonObjectBuilder output = Json.createObjectBuilder();

		JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strAttributeNames = jsonInputInfo.getString(RANGE_ATTRIBUTE_NAMES);
		String strPickListNames = jsonInputInfo.getString("PickListInput");

		output.add("RangeAttributeValues", PGWidgetUtil.getAttributeRangeValues(context, strAttributeNames));
		if (UIUtil.isNotNullAndNotEmpty(strPickListNames)) {
			output.add("PickListValuesData", getStudyProtocolPicklistValues(context, strPickListNames));
		}

		return output.build().toString();
	}

	/**
	 * This method is used to connect existing product part to study leg *
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws FrameworkException,MatrixException
	 * @throws Exception
	 */
	public static String addExistingProductPart(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		HashMap<String, Object> mpParamMAP = new HashMap<>();

		JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strFromObjectId = jsonInputInfo.getString("fromObjectId");
		String strToObjectId = jsonInputInfo.getString("toObjectId");
		StringList toObjectList = StringUtil.split(strToObjectId, KEY_COMMA_SEPARATOR);
		String strStudyProtocolId = jsonInputInfo.getString("studyProtocolId");
		String strSelectables = jsonInputInfo.getString("objectSelects");
		DomainObject domFromObject = null;

		if (BusinessUtil.isNotNullOrEmpty(strFromObjectId) && BusinessUtil.isNotNullOrEmpty(toObjectList)) {
			String[] toObjectId = toObjectList.toArray(new String[toObjectList.size()]);
			domFromObject = DomainObject.newInstance(context, strFromObjectId);
			DomainRelationship.connect(context, domFromObject, RELATIONSHIP_PGLEGINPUT, true, toObjectId);
			jsonObjOutput.add(PGWidgetConstants.KEY_SUCCESS, PGWidgetConstants.KEY_SUCCESS);
		}
		mpParamMAP.put(PGWidgetConstants.KEY_OBJECT_ID, strStudyProtocolId);
		mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, strSelectables);

		return getStudyLegDetails(context, mpParamMAP);
	}

	/**
	 * Method to retrieve values for Chassis and Platform for a Study Protocol
	 * object
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static String getConnectedChassisAndPlatformValues(Context context, String strInputData)
			throws MatrixException {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strSelectedFieldIds = jsonInputData.getString("fieldObjectIds");
		String strReloadFieldNames = jsonInputData.getString("reloadFieldNames");
		String strType = jsonInputData.getString(DomainConstants.SELECT_TYPE);

		StringList selectedFieldIdList = StringUtil.split(strSelectedFieldIds, KEY_COMMA_SEPARATOR);
		StringList reloadFiledNameList = StringUtil.split(strReloadFieldNames, KEY_COMMA_SEPARATOR);

		HashMap<String, Object> programMap;

		JsonObjectBuilder jsonObject = Json.createObjectBuilder();

		for (String reloadFieldName : reloadFiledNameList) {
			programMap = new HashMap<>();
			programMap.put("selectedPlatformList", selectedFieldIdList);
			programMap.put("strgetAttrObject", reloadFieldName);
			String[] methodargs = JPO.packArgs(programMap);
			jsonObject.add(reloadFieldName, getProductFormPlatform(context, methodargs, strType));
		}

		return jsonObject.build().toString();
	}

	/**
	 * Method to retrieve values for Chassis and Platform for a Study Protocol
	 * object based on selection of Business Area
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static JsonArray getProductFormPlatform(Context context, String[] methodargs, String strType)
			throws MatrixException {
		Map<?, ?> hMap = null;

		StringList fieldDisplay = null;
		StringList fieldChoice = null;
		String strValue;

		JsonObjectBuilder jsonObject = null;
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();

		if (strType.equalsIgnoreCase("Platform")) {
			hMap = (HashMap<?, ?>) JPO.invoke(context, EMXCOMMONDOCUMENT, null, "getConnectedPlatform", methodargs,
					Map.class);
		} else if (strType.equalsIgnoreCase("Chassis")) {
			hMap = (HashMap<?, ?>) JPO.invoke(context, EMXCOMMONDOCUMENT, null, "getConnectedChassisToPlatform",
					methodargs, Map.class);
		} else {
			hMap = (HashMap<?, ?>) JPO.invoke(context, EMXCOMMONDOCUMENT, null, "getConnectedPlatformToBusinessArea",
					methodargs, Map.class);
		}
		fieldDisplay = (StringList) hMap.get("field_display_choices");
		fieldChoice = (StringList) hMap.get("field_choices");

		for (int itrFinalField = 0; itrFinalField < fieldDisplay.size(); itrFinalField++) {
			jsonObject = Json.createObjectBuilder();
			strValue = fieldDisplay.get(itrFinalField);
			if (UIUtil.isNotNullAndNotEmpty(strValue)) {
				jsonObject.add(PGWidgetConstants.KEY_NAME,
						PGWidgetUtil.checkNullValueforString((fieldDisplay.get(itrFinalField))));
				jsonObject.add(PGWidgetConstants.KEY_VALUE,
						PGWidgetUtil.checkNullValueforString((fieldDisplay.get(itrFinalField))));
				jsonObject.add(PGWidgetConstants.KEY_OBJECT_ID, fieldChoice.get(itrFinalField));

				jsonArr.add(jsonObject);
			}
		}
		return jsonArr.build();
	}

	/**
	 * Method to disconnect a connection for Study Leg
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static String removeSelected(Context context, String strInputData) throws FrameworkException {
		boolean isCtxtPushed = false;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strRelIds = jsonInputData.getString("RelIds");
			StringList relIdsList = StringUtil.split(strRelIds, KEY_COMMA_SEPARATOR);
			//Context pushed reason: Logged-in user may not have access, but it is functionally required
			ContextUtil.pushContext(context);
			isCtxtPushed = true;
			if (!relIdsList.isEmpty()) {
				DomainRelationship.disconnect(context, BusinessUtil.toStringArray(relIdsList));
			}
			return getRelatedObjects(context, strInputData);
		} finally {
			if (isCtxtPushed) {
				ContextUtil.popContext(context);
			}
		}
	}

	/**
	 * This method connects objects
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public static String addExisting(Context context, String strInputData) throws Exception {
		JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strFromObjectId = jsonInputInfo.getString("ObjectId");
		String strToObjectId = jsonInputInfo.getString("ToObjectId");
		String strRelName = jsonInputInfo.getString("RelPattern");
		StringList toObjectList = StringUtil.split(strToObjectId, KEY_COMMA_SEPARATOR);

		StringList slIdsToConnect = new StringList();
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		DomainObject domFromObject = null;
		String strObjId = "";
		Map mRelIds = null;
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		ContextUtil.startTransaction(context, true);
		isTransactionActive = true;
		StringBuilder sbMessage = new StringBuilder();

		try {
			boolean isTrue = false;
			if (BusinessUtil.isNotNullOrEmpty(strFromObjectId) && BusinessUtil.isNotNullOrEmpty(toObjectList)) {
				for (int i = 0; i < toObjectList.size(); i++) {
					strObjId = toObjectList.get(i);
					if (strRelName.equalsIgnoreCase(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT)) {
						isTrue = isReferenceDocument(context, strObjId);
						if (isTrue) {
							slIdsToConnect.add(toObjectList.get(i));
						} else {
							throw new Exception(ERROR_MSG_NOT_DOCUMENTS);
						}
					} else if (strRelName.equalsIgnoreCase(DomainConstants.RELATIONSHIP_OBJECT_ROUTE)) {
						isTrue = isApprovalRouteTemplate(context, strObjId);
						if (isTrue) {
							slIdsToConnect.add(toObjectList.get(i));
							StringList slDisconnectId = new StringList(jsonInputInfo.getString("disconnectId"));
							disconnectAndConnect(context, strFromObjectId, slDisconnectId, null, strRelName, true);
						} else {
							throw new Exception(ERROR_MSG_NOT_ROUTE_TEMPLATE);
						}
					}
				}
				if (!slIdsToConnect.isEmpty()) {
					domFromObject = DomainObject.newInstance(context, strFromObjectId);
					String[] toObjectId = slIdsToConnect.toArray(new String[slIdsToConnect.size()]);
					mRelIds = DomainRelationship.connect(context, domFromObject, strRelName, true, toObjectId);
					if (strRelName.equalsIgnoreCase(DomainConstants.RELATIONSHIP_OBJECT_ROUTE)) {
						setRelAttributesAndCreateStartRoute(context, mRelIds, domFromObject);
					}
				}
			}
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
				isExceptionOccurred = true;
				sbMessage.append(e.getMessage()).append("\n");
			}
			logger.log(Level.SEVERE, ERROR_PGSTUDYPROTOCOL_GETMYSTUDYPROTOCOL, e);
			if (isExceptionOccurred) {
				PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
				return jsonStatus.build().toString();

			}
			throw new FrameworkException(e);
		}
		return getRelatedObjects(context, strInputData);
	}

	/**
	 * This method gets relationship id
	 * 
	 * @param context
	 * @param strFromObjectId
	 *            object id
	 * @param strRelName
	 *            rel name
	 * @return StringList of connection ids
	 * @throws FrameworkException
	 */
	private static StringList getRelIds(Context context, String strFromObjectId, String strRelName)
			throws FrameworkException {
		DomainObject domFromObject = DomainObject.newInstance(context, strFromObjectId);
		return domFromObject.getInfoList(context, "from[" + strRelName + "].id");
	}

	/**
	 * This method Checks whether Object is of Documents
	 * 
	 * @param context
	 * @param strObjId
	 * @return
	 * @throws FrameworkException
	 * @throws Exception
	 */
	private static boolean isReferenceDocument(Context context, String strObjId) throws FrameworkException {
		DomainObject domObj = DomainObject.newInstance(context, strObjId);
		return domObj.isKindOf(context, TYPE_DOCUMENTS);
	}

	/**
	 * This method checks whether route template is Approval
	 * 
	 * @param context
	 * @param strObjId
	 * @return
	 * @throws FrameworkException
	 * @throws Exception
	 */
	private static boolean isApprovalRouteTemplate(Context context, String strObjId) throws FrameworkException {
		Map mpRTDetails = null;
		StringList busSelect = new StringList();
		busSelect.add(DomainConstants.SELECT_CURRENT);
		busSelect.add(ATTRIBUTE_ROUTE_BASE_PURPOSE);
		DomainObject domObj = DomainObject.newInstance(context, strObjId);
		mpRTDetails = domObj.getInfo(context, busSelect);
		String strRTCurrent = "";
		String strRouteBasePurpose = "";
		if (!mpRTDetails.isEmpty()) {
			strRTCurrent = (String) mpRTDetails.get(DomainConstants.SELECT_CURRENT);
			strRouteBasePurpose = (String) mpRTDetails.get(ATTRIBUTE_ROUTE_BASE_PURPOSE);
			if (strRouteBasePurpose.equals(AWLConstants.ROUTE_APPROVAL)
					&& (strRTCurrent.equals(PGWidgetConstants.STATE_PRODUCTION)
							|| (strRTCurrent.equals(PGWidgetConstants.STATE_ACTIVE)))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Method to get related objects based on selectables
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static String getRelatedObjects(Context context, String strInputData) throws FrameworkException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
		if (jsonInputInfo.containsKey("ObjectId")) {
			String strObjectId = jsonInputInfo.getString("ObjectId");
			String strRelPattern = jsonInputInfo.getString("RelPattern");
			String strSelectedTypes = jsonInputInfo.getString(PGWidgetConstants.TYPE_PATTERN);
			String strExpandLevel = jsonInputInfo.getString("ExpandLevel");
			String strWhereCondition = jsonInputInfo.getString("WhereCondition");
			String strGetTo = jsonInputInfo.getString("GetTo");
			String strGetFrom = jsonInputInfo.getString("GetFrom");
			String strLimit = jsonInputInfo.getString("Limit");
			String strRelWhereCondition = jsonInputInfo.getString("RelWhereCondition");
			String strRelationshipSelects = jsonInputInfo.getString("RelationshipSelects");
			String strSelectables = jsonInputInfo.getString(PGWidgetConstants.OBJ_SELECT);

			String strTypePattern = PGWidgetUtil.getPattern(context, strSelectedTypes);

			JsonArrayBuilder jsonArr = Json.createArrayBuilder();
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);
			if (BusinessUtil.isNotNullOrEmpty(strObjectId)) {

				if (UIUtil.isNotNullAndNotEmpty(strSelectables)) {
					objectSelects.addAll(strSelectables.split(KEY_COMMA_SEPARATOR));
				}
				StringList slRelationshipSelectList = new StringList();
				if (UIUtil.isNotNullAndNotEmpty(strRelationshipSelects)) {
					slRelationshipSelectList = StringUtil.split(strRelationshipSelects, KEY_COMMA_SEPARATOR);
				}
				DomainObject domObj = DomainObject.newInstance(context, strObjectId);
				MapList mlObjectList = domObj.getRelatedObjects(context, strRelPattern, // relationshipPattern
						strTypePattern, // typePattern
						objectSelects, // objectSelects
						slRelationshipSelectList, // relationshipSelects
						Boolean.parseBoolean(strGetTo), // getTo
						Boolean.parseBoolean(strGetFrom), // getFrom
						Short.parseShort(strExpandLevel), // recurseToLevel
						strWhereCondition, // objectWhere
						strRelWhereCondition, // relationshipWhere
						Short.parseShort(strLimit));// limit

				if (!mlObjectList.isEmpty()) {
					Map<?, ?> objMap = null;
					for (int i = 0; i < mlObjectList.size(); i++) {
						objMap = (Map<?, ?>) mlObjectList.get(i);

						jsonArr.add(PGWidgetUtil.getJSONFromMap(context, objMap));

					}
					output.add("data", jsonArr.build());
				}
			}
		}
		return output.build().toString();
	}

	/**
	 * This method is used to get saved data on Chassis and Platform page
	 * 
	 * @param context
	 * @param strInputData
	 * @throws Exception
	 */
	public static String getInitialDataOnChassisPlatform(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();

		JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
		JsonArray jsonInputArray = jsonInputInfo.getJsonArray("inputdata");
		JsonObject jsonPicklistInfo;
		String strId;
		String strReloadFieldNames;
		String strPickListValues;
		String strPickListNames;
		String strCategoryType;
		String strObjectId = null;
		StringList selectedFieldIdList;
		HashMap<String, Object> programMap;
		MapList mlPicklist;
		StringList selects = new StringList();
		selects.addElement(DomainObject.SELECT_ID);
		selects.addElement(DomainObject.SELECT_REVISION);
		String whereClause = "current == " + PGWidgetConstants.STATE_ACTIVE;

		Map<?, ?> objMap;

		for (int i = 0; i < jsonInputArray.size(); i++) {
			JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);
			strId = jsonElement.getString(DomainConstants.SELECT_ID);
			strPickListValues = jsonElement.getString(PGWidgetConstants.KEY_VALUE);
			strPickListNames = jsonElement.getString(DomainConstants.SELECT_NAME);
			strCategoryType = jsonElement.getString("categoryType");
			strReloadFieldNames = jsonElement.getString("reloadFieldNames");
			if (CONST_BUSINESS_AREA_DISPLAY.equals(strId)) {
				jsonPicklistInfo = getStudyProtocolPicklistValues(context, strPickListNames);
				jsonObject.add(strId, jsonPicklistInfo.get(strPickListNames));
			}

			StringList strPickListValuesList = StringUtil.split(strPickListValues, KEY_COMMA_SEPARATOR);
			selectedFieldIdList = new StringList();

			for (int cnt = 0; cnt < strPickListValuesList.size(); cnt++) {
				mlPicklist = DomainObject.findObjects(context, // Context
						strPickListNames, // Type
						strPickListValuesList.elementAt(cnt), // Name
						DomainConstants.QUERY_WILDCARD, // Revision
						DomainConstants.QUERY_WILDCARD, // Owner
						ArtworkConstants.VAULT_ESERVICE_PRODUCTION, // Vault
						whereClause, // Where clause
						false, // Consider Sub types
						selects);// Bus select

				if (null != mlPicklist && mlPicklist.size() == 1) {
					objMap = (Map<?, ?>) mlPicklist.get(0);
					strObjectId = (String) objMap.get(DomainConstants.SELECT_ID);
				} else {
					strObjectId = DomainConstants.EMPTY_STRING;
				}
				selectedFieldIdList.addElement(strObjectId);
			}

			StringList reloadFiledNameList = StringUtil.split(strReloadFieldNames, KEY_COMMA_SEPARATOR);

			for (String reloadFieldName : reloadFiledNameList) {
				programMap = new HashMap<>();
				programMap.put("selectedPlatformList", selectedFieldIdList);
				programMap.put("strgetAttrObject", reloadFieldName);
				String[] methodargs = JPO.packArgs(programMap);
				jsonObject.add(reloadFieldName, getProductFormPlatform(context, methodargs, strCategoryType));
			}
		}
		return jsonObject.build().toString();
	}

	/**
	 * Method to get next and previous states. Method is copied from
	 * emxExtendedHeader:genHeaderStatus program and hence has push pop statements
	 * to fix some bugs related to print policy.
	 * 
	 * @param context
	 * @param strObjectId
	 * @param sPolicy
	 * @param sCurrent
	 * @return JSON Array consisting of the information to be displayed
	 * @throws FrameworkException
	 * @throws Exception
	 */
	public static JsonArray getStates(Context context, String strObjectId, String sPolicy, String sCurrent)
			throws MatrixException {
		JsonArrayBuilder jsonStatesArray = Json.createArrayBuilder();
		DomainObject dObject = DomainObject.newInstance(context, strObjectId);
		StateList sList = dObject.getStates(context);
		Access access = dObject.getAccessMask(context);
		boolean bAccessPromote = access.hasPromoteAccess();
		boolean bAccessDemote = access.hasDemoteAccess();

		boolean isUserAgentContextStr = false;
		try {
			//Context pushed reason: This is a read operation and logged-in user may not have access, which is functionally required
			ContextUtil.pushContext(context, PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING);
			isUserAgentContextStr = true;

			// remove any hidden states
			List<String> hiddenStateNames = CacheManager.getInstance().getValue(context,
					CacheManager._entityNames.HIDDEN_STATES, sPolicy);
			List<State> hiddenStates = new ArrayList<>();
			if (!hiddenStateNames.isEmpty()) {
				for (int i = 0; i < sList.size(); i++) {
					State state = sList.get(i);
					if (hiddenStateNames.contains(state.getName())) {
						hiddenStates.add(state);
					}
				}
				sList.removeAll(hiddenStates);
			}
		} finally {
			if (isUserAgentContextStr) {
				ContextUtil.popContext(context);
				isUserAgentContextStr = false;
			}
		}

		int iCurrent = 0;
		for (int i = 0; i < sList.size(); i++) {
			State state = sList.get(i);
			String sStateName = state.getName();
			if (sStateName.equals(sCurrent)) {
				iCurrent = i;
				break;
			}
		}

		if (bAccessDemote && iCurrent > 0) {
			State statePrev = sList.get(iCurrent - 1);
			jsonStatesArray.add(PGWidgetUtil.getStateJsonObj(context, sPolicy, statePrev, false,
					PGWidgetConstants.OPERATION_DEMOTE));
		}

		State stateCurrent = sList.get(iCurrent);
		jsonStatesArray
				.add(PGWidgetUtil.getStateJsonObj(context, sPolicy, stateCurrent, true, DomainConstants.EMPTY_STRING));

		if (bAccessPromote && (iCurrent < sList.size() - 1)) {
			State stateNext = sList.get(iCurrent + 1);
			jsonStatesArray.add(PGWidgetUtil.getStateJsonObj(context, sPolicy, stateNext, false,
					PGWidgetConstants.OPERATION_PROMOTE));
		}

		return jsonStatesArray.build();
	}

	/**
	 * Method to edit and save properties of Study Protocol object
	 * 
	 * @param context
	 * @param strInputData
	 * @return
	 * @throws Exception
	 */
	public static String editStudyLegDetails(Context context, String strInputData) throws Exception {
		HashMap<String, Object> mpParamMAP = new HashMap<>();
		Map<String, String> mRelAttrMap = new HashMap<>();
		Map<String, String> mObjAttrMap;

		JsonObject jsonInputInfo = PGWidgetUtil.getJsonFromJsonString(strInputData);
		JsonArray jsonInputArray = jsonInputInfo.getJsonArray(UPDATED_DATA);
		String strStudyProtocolId = jsonInputInfo.getString(PGWidgetConstants.KEY_OBJECT_ID);
		String strSelectables = jsonInputInfo.getString(PGWidgetConstants.OBJ_SELECT);
		String strPickListNames = jsonInputInfo.getString(PICKLIST_NAMES);

		String strExp = "";
		DomainObject doObj = null;
		Map programMap;
		HashMap paramMap;
		String descriptionValue = "";
		for (int i = 0; i < jsonInputArray.size(); i++) {
			String strObjId = "";
			String strRelId = "";
			JsonObject jsonElement = (JsonObject) jsonInputArray.get(i);

			strRelId = jsonElement.getString("relId");
			if (jsonElement.containsKey(PGWidgetConstants.KEY_OBJECT_ID)) {
				strObjId = jsonElement.getString(PGWidgetConstants.KEY_OBJECT_ID);
			}
			JsonArray jsonUpdatedArray = jsonElement.getJsonArray("updatedValue");
			mObjAttrMap = new HashMap<>();

			for (int j = 0; j < jsonUpdatedArray.size(); j++) {
				JsonObject jsonDataElement = (JsonObject) jsonUpdatedArray.get(j);
				String strExpression = jsonDataElement.getString(PGWidgetConstants.KEY_EXPR);
				String strValue = jsonDataElement.getString(PGWidgetConstants.KEY_VALUE);
				if (strValue.indexOf(KEY_COMMA_SEPARATOR) != -1) {
					strValue = FrameworkUtil.findAndReplace(strValue, KEY_COMMA_SEPARATOR,
							PGWidgetConstants.KEY_PIPE_SEPARATOR);
				}

				if (PRODUCT_SITE_EXPR.equals(strExpression)) {
					programMap = new HashMap();
					paramMap = new HashMap();
					paramMap.put("relId", strRelId);
					paramMap.put("New Value", strValue);
					programMap.put("paramMap", paramMap);
					JPO.invoke(context, "pgVT_Util", null, "ConnectPlantToLegInputRel", JPO.packArgs(programMap));
				} else {
					if (!strExpression.equals(SELECT_DESCRIPTION)) {
						strExp = PGWidgetUtil.getFormattedExpression(strExpression);
						if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
							mObjAttrMap.put(strExp, strValue);
						} else {
							mRelAttrMap.put(strExp, strValue);
						}
					} else {
						descriptionValue = strValue;
					}

				}
			}

			if (UIUtil.isNotNullAndNotEmpty(strObjId)) {
				doObj = DomainObject.newInstance(context, strObjId);
				doObj.setAttributeValues(context, mObjAttrMap);
				if (!descriptionValue.equals("")) {
					doObj.setDescription(context, descriptionValue);
				}

			}

			DomainRelationship.setAttributeValues(context, strRelId, mRelAttrMap);
		}
		mpParamMAP.put(PGWidgetConstants.KEY_OBJECT_ID, strStudyProtocolId);
		mpParamMAP.put(PGWidgetConstants.OBJ_SELECT, strSelectables);
		mpParamMAP.put("PickListInput", strPickListNames);
		return getStudyLegDetails(context, mpParamMAP);
	}

	/**
	 * Method to download PDF for All Info View for Study Protocol object
	 * 
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception
	 */
	public static Response getAgencyPDFViewFile(Context context, javax.servlet.http.HttpServletResponse response,
			String strObjectId) throws Exception {
		String strContextUser = context.getUser();
		String[] tmpArgs = new String[3];
		tmpArgs[0] = strObjectId;
		tmpArgs[1] = PDFConstants.CONSTANT_PLACEMENTAGENCY_VIEW;
		tmpArgs[2] = strContextUser;

		try {
			PDFApp pdf = new PDFApp(context);
			String strFileInfo = pdf.generatePDF(context, tmpArgs);

			File pdfFileName = new java.io.File(strFileInfo);
			String fileName = pdfFileName.getName();
			ResponseBuilder rbResponse = Response.ok(pdfFileName);
			rbResponse.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			return rbResponse.build();

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Method to get allowed states for the policy
	 * 
	 * @param context
	 * @param strAllowedStates
	 * @return
	 * @throws Exception
	 */
	public static String getAllowedStates(Context context, String strAllowedStates) throws MatrixException {
		String strLanguage = context.getSession().getLanguage();
		JsonObjectBuilder jsonObj = Json.createObjectBuilder();
		String strAllowedState = null;
		String[] strArrPolicyState = null;
		String strPolicy = null;
		String strState = null;
		String strStateDisplay = null;
		if (UIUtil.isNotNullAndNotEmpty(strAllowedStates)) {
			String[] strArrAllowedStates = strAllowedStates.split(KEY_COMMA_SEPARATOR);
			for (int i = 0, size = strArrAllowedStates.length; i < size; i++) {
				strAllowedState = strArrAllowedStates[i];
				if (UIUtil.isNotNullAndNotEmpty(strAllowedState)) {
					strArrPolicyState = strAllowedState.split("\\.");
					strPolicy = strArrPolicyState[0];
					strState = strArrPolicyState[1];
					strStateDisplay = EnoviaResourceBundle.getStateI18NString(context, strPolicy, strState,
							strLanguage);
					jsonObj.add(strAllowedState, strStateDisplay);
				}
			}
		}
		return jsonObj.build().toString();
	}

	/**
	 * Method to create and start Route in Review state for Study Protocol with
	 * Signature Reference policy
	 * 
	 * @param context
	 * @param strStudyProtocolObjectId
	 * @return
	 * @throws Exception
	 */
	public static void createAndStartApprovalRoute(Context context, String strStudyProtocolObjectId) throws Exception {
		try {
			String args[] = new String[5];
			args[0] = strStudyProtocolObjectId;
			args[1] = ROUTE_BASE_PURPOSE_APPROVAL;
			args[2] = POLICY_PG_PKGSIGNATUREREFERENCEDOC;
			args[3] = STATE_REVIEW;
			JPO.invoke(context, "enoECMChangeOrder", null, "createRouteFromRouteTemplate", args, int.class);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;
		}
	}

	/**
	 * Method to set attributes on relationship 'Object Route' , create and start
	 * Route in Review state for Study Protocol with Signature Reference policy
	 * 
	 * @param context
	 * @param mRelIds
	 * @param domFromObject
	 * @return
	 * @throws Exception
	 */
	private static void setRelAttributesAndCreateStartRoute(Context context, Map mRelIds, DomainObject domFromObject)
			throws MatrixException, Exception {
		DomainRelationship domRouteTemplateRel;
		StringList slObjSelectables = new StringList(2);
		slObjSelectables.add(DomainConstants.SELECT_CURRENT);
		slObjSelectables.add(DomainConstants.SELECT_POLICY);
		Map<String, String> mapRelAttributes = new HashMap<String, String>();
		mapRelAttributes.put(DomainObject.ATTRIBUTE_ROUTE_BASE_STATE, SYMBOLIC_STATE_IN_APPROVAL);
		mapRelAttributes.put(DomainObject.ATTRIBUTE_ROUTE_BASE_PURPOSE, ROUTE_BASE_PURPOSE_APPROVAL);
		mapRelAttributes.put(DomainObject.ATTRIBUTE_ROUTE_BASE_POLICY, POLICY_SIGNATURE_REFERENCE_SYMBOLIC);
		if (mRelIds != null && !mRelIds.isEmpty()) {
			@SuppressWarnings("rawtypes")
			java.util.Set setInfoKey = mRelIds.keySet();
			Iterator<?> itrInfoKey = setInfoKey.iterator();
			String sKey = "";
			String sValue = "";
			while (itrInfoKey.hasNext()) {
				sKey = (String) itrInfoKey.next();
				sValue = (String) mRelIds.get(sKey);
				domRouteTemplateRel = new DomainRelationship(sValue);
				//Context pushed reason: Logged-in user may not have access, but it is functionally required
				ContextUtil.pushContext(context, "User Agent", null, context.getVault().getName());
				domRouteTemplateRel.setAttributeValues(context, mapRelAttributes);
				ContextUtil.popContext(context);
			}
		}
		Map<?, ?> objMap = domFromObject.getInfo(context, slObjSelectables);
		String strPolicy = (String) objMap.get(DomainConstants.SELECT_POLICY);
		String strCurrent = (String) objMap.get(DomainConstants.SELECT_CURRENT);
		if ((POLICY_PG_PKGSIGNATUREREFERENCEDOC.equals(strPolicy)) && (STATE_REVIEW.equals(strCurrent))) {
			createAndStartApprovalRoute(context, domFromObject.getObjectId(context));
		}
	}

	/**
	 * Method to create A6 and A7a1 gps task for study protocol
	 * 
	 * @param context
	 * @param strData
	 * @return
	 * @throws Exception
	 */
	static String createGPSTaskForStudyProtocol(Context context, String strData) throws Exception {
		JsonObjectBuilder jsonOutput = Json.createObjectBuilder();
		JsonObject jsonInput = PGWidgetUtil.getJsonFromJsonString(strData);
		String strDescription = jsonInput.getString(PGWidgetConstants.KEY_DESCRIPTION);
		String strGPSAssessmentCategory = jsonInput.getString(KEY_ATTR_GPS_ASSESSMENT_CATEGORY);
		String strStudyProtocolDocId = jsonInput.getString(KEY_RELATED_IDS);
        String strPreTaskAssignee = jsonInput.containsKey(KEY_ADD_PERSON_PRE) ? jsonInput.getString(KEY_ADD_PERSON_PRE) : null;
        String strPostTaskAssignee =  jsonInput.containsKey(KEY_ADD_PERSON_POST) ? jsonInput.getString(KEY_ADD_PERSON_POST) : null;
        String strProjectRolePost =  jsonInput.containsKey(KEY_ADD_PROJECT_ROLE_POST) ? jsonInput.getString(KEY_ADD_PROJECT_ROLE_POST) : null;
        String strProjectRolePre =  jsonInput.containsKey(KEY_ADD_PROJECT_ROLE_PRE) ? jsonInput.getString(KEY_ADD_PROJECT_ROLE_PRE) : null;
		String strSymbolicTypeName = FrameworkUtil.getAliasForAdmin(context, PGWidgetConstants.KEY_TYPE,
				TYPE_GPS_ASSESSMENT_TASK, true);
		String strSymbolicPolicyName = FrameworkUtil.getAliasForAdmin(context, PGWidgetConstants.KEY_POLICY,
				POLICY_GPS_ASSESSMENT_TASK, true);
		String strTaskName = FrameworkUtil.autoName(context, strSymbolicTypeName, null, strSymbolicPolicyName, null,
				null, true, true);

		DomainObject dobGPSTask = DomainObject.newInstance(context);
		dobGPSTask.createObject(context, TYPE_GPS_ASSESSMENT_TASK, strTaskName, DomainConstants.EMPTY_STRING,
				POLICY_GPS_ASSESSMENT_TASK, context.getVault().getName());

		dobGPSTask.setDescription(context, strDescription);

		Map<Object, Object> mapAttributeMap = new HashMap<>();
		mapAttributeMap.put(ATTRIBUTE_GPS_ASSESSMENT_CATEGORY, strGPSAssessmentCategory);
		mapAttributeMap.put(ATTRIBUTE_REASON_FOR_CHANGE, REASON_FOR_CHANGE_VALUE);
		mapAttributeMap.put(ATTRIBUTE_ESTIMATED_DURATION, "1.0");
		if(UIUtil.isNotNullAndNotEmpty(strPreTaskAssignee)) {
			mapAttributeMap.put(ATTRIBUTE_PGAAA_PRE_TASK_PERSON_ASSIGNEE, strPreTaskAssignee);
		}
		if(UIUtil.isNotNullAndNotEmpty(strPostTaskAssignee)) {
			mapAttributeMap.put(ATTRIBUTE_PGAAA_POST_TASK_PERSON_ASSIGNEE, strPostTaskAssignee);
		}
		if(UIUtil.isNotNullAndNotEmpty(strProjectRolePre)) {
			mapAttributeMap.put(ATTRIBUTE_PGAAA_PRE_TASK_ROLE_ASSIGNEE, strProjectRolePre);
		}
		if(UIUtil.isNotNullAndNotEmpty(strProjectRolePost)) {
			mapAttributeMap.put(ATTRIBUTE_PGAAA_POST_TASK_ROLE_ASSIGNEE, strProjectRolePost);
		}
		dobGPSTask.setAttributeValues(context, mapAttributeMap);

		String strTaskId = dobGPSTask.getInfo(context, PGWidgetConstants.SELECT_PHYSICAL_ID);

		if (UIUtil.isNotNullAndNotEmpty(strStudyProtocolDocId)) {
			connectProductWithTask(context, strTaskId, StringUtil.split(strStudyProtocolDocId, KEY_COMMA_SEPARATOR));
		}

		String strTaskPromoteStatus = PGWidgetUtil.promoteDemoteObject(context, strTaskId,
				PGWidgetConstants.OPERATION_PROMOTE);
		jsonOutput.add(KEY_TASK_ID, strTaskId);
		jsonOutput.add(KEY_TASK_NAME, strTaskName);
		try {
			JsonObject jsonPromoteStatus = PGWidgetUtil.getJsonFromJsonString(strTaskPromoteStatus);
			jsonOutput.add(KEY_PROMOTION_STATUS, jsonPromoteStatus);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonOutput.add(KEY_PROMOTION_STATUS, strTaskPromoteStatus);
		}
		return jsonOutput.build().toString();
	}

	/**
	 * Method to connect GPS Task with Product
	 * 
	 * @param context
	 * @param strTaskId
	 * @param slRelObjIds
	 * @throws FrameworkException
	 */
	static void connectProductWithTask(Context context, String strTaskId, StringList slRelObjIds)
			throws FrameworkException {

		WorkspaceVault workspaceVault = (WorkspaceVault) DomainObject.newInstance(context,
				DomainConstants.TYPE_WORKSPACE_VAULT);
		workspaceVault.setId(strTaskId);

		int iSize = slRelObjIds.size();
		for (int i = 0; i < iSize; i++) {
			String strObjectIdType = slRelObjIds.get(i);
			StringList slOIDTypeList = StringUtil.split(strObjectIdType, PGWidgetConstants.KEY_PIPE_SEPARATOR);
			String strObjectId = slOIDTypeList.get(0);

			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				DomainObject dobRelatedObj = DomainObject.newInstance(context, strObjectId);
				DomainRelationship.connect(context, workspaceVault, RELATIONSHIP_PGGPSASSESSMENTTASK_INPUTS,
						dobRelatedObj);
			}
		}
	}

	/**
	 * Method to get next and previous states for GPS Task. Method is copied from
	 * emxExtendedHeader:genHeaderStatus program and hence has push pop statements
	 * to fix some bugs related to print policy.
	 * 
	 * @param context
	 * @param strObjectId
	 * @param sPolicy
	 * @param sCurrent
	 * @return
	 * @throws Exception
	 */
	static JsonArray getGPSTaskStates(Context context, String strObjectId, String sPolicy, String sCurrent)
			throws Exception {
		JsonArrayBuilder jsonStatesArray = Json.createArrayBuilder();
		DomainObject dObject = DomainObject.newInstance(context, strObjectId);
		StateList sList = dObject.getStates(context);
		Access access = dObject.getAccessMask(context);
		boolean bAccessPromote = access.hasPromoteAccess();
		boolean bAccessDemote = access.hasDemoteAccess();

		boolean isUserAgentContextStr = false;
		try {
			//Context pushed reason: This is a read operation and logged-in user may not have access, which is functionally required
			ContextUtil.pushContext(context, PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING);
			isUserAgentContextStr = true;

			// remove any hidden states
			List<String> hiddenStateNames = CacheManager.getInstance().getValue(context,
					CacheManager._entityNames.HIDDEN_STATES, sPolicy);
			List<State> hiddenStates = new ArrayList<>();
			if (!hiddenStateNames.isEmpty()) {
				for (int i = 0; i < sList.size(); i++) {
					State state = sList.get(i);
					if (hiddenStateNames.contains(state.getName())) {
						hiddenStates.add(state);
					}
				}
				sList.removeAll(hiddenStates);
			}
		} finally {
			if (isUserAgentContextStr) {
				ContextUtil.popContext(context);
				isUserAgentContextStr = false;
			}
		}

		int iCurrent = 0;
		for (int i = 0; i < sList.size(); i++) {
			State state = sList.get(i);
			String sStateName = state.getName();
			if (sStateName.equals(sCurrent)) {
				iCurrent = i;
				break;
			}
		}

		if (bAccessDemote && iCurrent > 0) {
			State statePrev = sList.get(iCurrent - 1);
			jsonStatesArray.add(PGWidgetUtil.getStateJsonObj(context, sPolicy, statePrev, false,
					PGWidgetConstants.OPERATION_DEMOTE));
		}

		State stateCurrent = sList.get(iCurrent);
		jsonStatesArray.add(PGWidgetUtil.getStateJsonObj(context, sPolicy, stateCurrent, true, ""));

		if (bAccessPromote && (iCurrent < sList.size() - 1)) {
			State stateNext = sList.get(iCurrent + 1);
			jsonStatesArray.add(PGWidgetUtil.getStateJsonObj(context, sPolicy, stateNext, false,
					PGWidgetConstants.OPERATION_PROMOTE));
		}

		return jsonStatesArray.build();
	}

	/**
	 * Method to Copy or Revise Study Protocol Document
	 * 
	 * @param context
	 * @param strInputData
	 *            parameters in JSON format
	 * @return
	 * @throws Exception
	 */
	public static String copyOrReviseStudyProtocol(Context context, String strInputData) throws Exception {
		boolean isTransactionActive = false;
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String sCopyType = jsonInputData.getString("CreateDocument");
			String sCopyTypeReference = jsonInputData.getString("CreateDocumentReference");
			String sType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
			String sObjectIds = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
			String sCommandName = jsonInputData.getString("createNewDocument");
			String strUPTPhysId = jsonInputData.getString(PGWidgetConstants.ATTRIBUTE_PGUPTPHYSICALID);

			HashMap<String, Object> mpParamMAP = new HashMap<>();
			mpParamMAP.put("CreateDocument", sCopyType);
			mpParamMAP.put("CreateDocumentReference", sCopyTypeReference);

			mpParamMAP.put(DomainConstants.SELECT_TYPE, sType);
			mpParamMAP.put("createNewDocument", sCommandName);

			// These attribute are present only for Copy Study Protocol.
			if ("Clone".equals(sCommandName)) {
//				mpParamMAP.put("CreateDocumentRoutePreferences", jsonInputData.getString("RoutePrefernce"));
//				mpParamMAP.put("CreateDocumentShareWithMembersPreferences",
//						jsonInputData.getString("ShareWithMemberPrefrence"));
				mpParamMAP.put("NumberOfCopies", jsonInputData.getString("NumberOfCopies"));
			}
			if(UIUtil.isNotNullAndNotEmpty(strUPTPhysId)) {
				mpParamMAP.put("IRMPreferenceTemplate", strUPTPhysId);
			}

			StringList slObjectIdList = StringUtil.split(sObjectIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			StringList oIds;
			Map<?, ?> studyProtocolMap = null;
			StringList lastIds;
			StringList names;
			StringBuilder msgBuffer = new StringBuilder();
			StringBuilder initMsg = new StringBuilder(ERROR_REVISE_STUDY_PROTOCOL);
			int iSize = 0;
			String oId;
			String lastId;
			String name;

			if (sCommandName.equals(COMMAND_REVISETONEW)) {
				MapList studyProtocolInfoList = BusinessUtil.getInfoList(context, slObjectIdList,
						BusinessUtil.toStringList(DomainConstants.SELECT_ID, DomainConstants.SELECT_LAST_ID,
								DomainConstants.SELECT_NAME));
				if (null != studyProtocolInfoList) {
					int iSPSize = studyProtocolInfoList.size();
					for (int index = 0; index < iSPSize; index++) {
						studyProtocolMap = (Map) studyProtocolInfoList.get(index);
						if (null != studyProtocolMap) {
							oIds = (StringList) studyProtocolMap.get(DomainConstants.SELECT_ID);
							if (null != oIds) {
								iSize = oIds.size();
								lastIds = (StringList) studyProtocolMap.get(DomainConstants.SELECT_LAST_ID);
								names = (StringList) studyProtocolMap.get(DomainConstants.SELECT_NAME);
								for (int j = 0; j < iSize; j++) {
									oId = oIds.get(j);
									lastId = lastIds.get(j);
									name = names.get(j);
									if (!oId.equals(lastId)) {
										if (msgBuffer.length() > 0)
											msgBuffer.append(PGWidgetConstants.KEY_PIPE_SEPARATOR);
										msgBuffer.append(name);
									}
								}
							}
						}
					}

					if (msgBuffer.length() > 0) {
						throw new Exception(initMsg.append(msgBuffer.toString()).toString());
					}
				}
			}

			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;

			int iObjSize = slObjectIdList.size();
			for (int iCount = 0; iCount < iObjSize; iCount++) {
				mpParamMAP.put(PGWidgetConstants.KEY_OBJECT_ID, slObjectIdList.get(iCount));
				Map mapRet = JPO.invoke(context, "pgVT_Util", null, "copyORReviseDocumentToNew",
						JPO.packArgs(mpParamMAP), Map.class);
				if (mapRet != null) {
					String strNewDocId = (String) mapRet.get(DomainConstants.SELECT_ID);
					if (UIUtil.isNotNullAndNotEmpty(strNewDocId) && UIUtil.isNotNullAndNotEmpty(strUPTPhysId)) {
						PGWidgetUtil.updateUPTPhyIdByInterface(context, strNewDocId, strUPTPhysId);
					}
				}
			}
			ContextUtil.commitTransaction(context);

			String typePattern = jsonInputData.getString(PGWidgetConstants.TYPE_PATTERN);
			String namePattern = jsonInputData.getString(PGWidgetConstants.NAME_PATTERN);
			String revisionPattern = jsonInputData.getString(PGWidgetConstants.REVISION_PATTERN);
			String whereExpression = jsonInputData.getString(PGWidgetConstants.WHERE_EXP);
			String expandType = jsonInputData.getString(PGWidgetConstants.EXPAND_TYPE);
			int objectLimit = jsonInputData.getInt(PGWidgetConstants.OBJECT_LIMIT);
			String objectSelects = jsonInputData.getString(PGWidgetConstants.OBJ_SELECT);
			String duration = jsonInputData.getString(PGWidgetConstants.DURATION);
			String allowedStates = jsonInputData.getString(PGWidgetConstants.ALLOWED_STATE);
			String showOwned = jsonInputData.getString(PGWidgetConstants.SHOW_OWNED);

			HashMap<String, Object> allSPMAP = new HashMap<>();
			allSPMAP.put(PGWidgetConstants.TYPE_PATTERN, typePattern);
			allSPMAP.put(PGWidgetConstants.NAME_PATTERN, namePattern);
			allSPMAP.put(PGWidgetConstants.REVISION_PATTERN, revisionPattern);
			allSPMAP.put(PGWidgetConstants.WHERE_EXP, whereExpression);
			allSPMAP.put(PGWidgetConstants.EXPAND_TYPE, expandType);
			allSPMAP.put(PGWidgetConstants.OBJECT_LIMIT, Integer.toString(objectLimit));
			allSPMAP.put(PGWidgetConstants.OBJ_SELECT, objectSelects);
			allSPMAP.put(PGWidgetConstants.DURATION, duration);
			allSPMAP.put(PGWidgetConstants.ALLOWED_STATE, allowedStates);
			allSPMAP.put(PGWidgetConstants.SHOW_OWNED, showOwned);
			return PGStudyProtocol.getMyStudyProtocol(context, allSPMAP);
		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
			}
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
			jsonBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonBuilder.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonBuilder.build().toString();
		}
	}

	/**
	 * Method to get Bookmark folders of a Project Space
	 * 
	 * @param context
	 * @param strInputData
	 *            parameters in JSON format
	 * @return
	 * @throws Exception
	 */
	public static String getProjectBookmarkFolders(Context context, String strInputData) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String sProjectId = jsonInputData.getString("projectId");
			String sProjectName = jsonInputData.getString("projectName");
			String sProjectType = jsonInputData.getString("projectType");

			HashMap<String, Object> mapParams = new HashMap<>();
			mapParams.put(PGWidgetConstants.KEY_OBJECT_ID, sProjectId);
			mapParams.put("selectedTable", "PMCFolderSummary");
			mapParams.put("expandLevel", "0");

			MapList mlList = JPO.invoke(context, "emxProjectFolder", null, "getTableExpandProjectVaultData",
					JPO.packArgs(mapParams), MapList.class);
			if (mlList != null && !mlList.isEmpty()) {
				JsonArrayBuilder jsonArrBookmarkFolder = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrHier = null;
				ArrayList<String> arrList = new ArrayList<>();
				JsonArrayBuilder jsonArrRootHier = Json.createArrayBuilder();
				jsonArrRootHier.add(sProjectName);
				JsonObjectBuilder jsonRootObj = Json.createObjectBuilder();
				jsonRootObj.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrRootHier.build());
				jsonRootObj.add(PGWidgetConstants.KEY_OBJECT_ID, sProjectId);
				jsonRootObj.add(PGWidgetConstants.KEY_TYPE, sProjectType);
				jsonArrBookmarkFolder.add(jsonRootObj.build());

				JsonArray bookmarkJSONArr = PGWidgetUtil.convertMapListToJsonFlatTable(mlList, 0);
				JsonObjectBuilder jsonObj = null;
				String strTypeValue = null;
				JsonObject jsonElement;
				String strTypeKey;
				String strNameKey;
				String strNameValue = null;
				String strIdValue = null;
				String strIdKey;
				String strBookmarkFolderType = null;
				int iCnt;

				int iJSONArrSize = bookmarkJSONArr.size();
				for (int i = 0; i < iJSONArrSize; i++) {
					jsonElement = (JsonObject) bookmarkJSONArr.get(i);
					jsonArrHier = Json.createArrayBuilder();
					jsonArrHier.add(sProjectName);
					arrList = new ArrayList<>();
					arrList.add(sProjectName);
					jsonObj = Json.createObjectBuilder();
					iCnt = 1;

					strTypeKey = Integer.toString(iCnt) + "-type";
					strIdValue = null;
					strBookmarkFolderType = null;

					while (jsonElement.containsKey(strTypeKey)) {
						strTypeValue = jsonElement.getString(strTypeKey);
						if (DomainConstants.TYPE_WORKSPACE_VAULT.equals(strTypeValue)) {
							strNameKey = Integer.toString(iCnt) + "-name";
							strNameValue = jsonElement.getString(strNameKey);
							arrList.add(strNameValue);
							strIdKey = Integer.toString(iCnt) + "-id";
							strIdValue = jsonElement.getString(strIdKey);
							strBookmarkFolderType = jsonElement.getString(strTypeKey);

						}
						jsonObj.add(PGWidgetConstants.KEY_HIERARCHY, createJsonArrayFromList(arrList));
						jsonObj.add(PGWidgetConstants.KEY_OBJECT_ID, strIdValue);
						jsonObj.add(PGWidgetConstants.KEY_TYPE, strBookmarkFolderType);
						jsonObj.add(PGWidgetConstants.KEY_NAME, strNameValue);
						jsonArrBookmarkFolder.add(jsonObj.build());

						strTypeKey = Integer.toString(++iCnt) + "-type";
					}
				}
				output.add("data", jsonArrBookmarkFolder.build());
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
			jsonBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonBuilder.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonBuilder.build().toString();
		}
		return output.build().toString();
	}

	public static JsonArray createJsonArrayFromList(List<String> list) {
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		for (String folder : list) {
			jsonArray.add(folder);
		}
		JsonArray jsonArrayObject = jsonArray.build();
		return jsonArrayObject;
	}

	/**
	 * Method to Copy Study Leg
	 * 
	 * @param context
	 * @param strInputData
	 *            parameters in JSON format
	 * @return
	 * @throws Exception
	 */
	public static String copyStudyLeg(Context context, String strInputData) throws Exception {
		boolean isTransactionActive = false;
		Map mapProgramMap = new HashMap();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strLegId = jsonInputData.getString("objectId");
			String strDocId = jsonInputData.getString("strDocId");
			int noOfCopies = Integer.parseInt(jsonInputData.getString("NoOfCopies"));
			int iNum = 0;
			mapProgramMap.put("DocumentId", strDocId);
			mapProgramMap.put("LegObjectId", strLegId);
			mapProgramMap.put("noOfCopies", noOfCopies);
			if (mapProgramMap != null && !mapProgramMap.isEmpty()) {
				ContextUtil.startTransaction(context, true);
				isTransactionActive = true;
				JPO.invoke(context, "pgVT_Util", null, "performLegClone", JPO.packArgs(mapProgramMap), String.class);
				ContextUtil.commitTransaction(context);
			}

			mapProgramMap = new HashMap();
			mapProgramMap.put(PGWidgetConstants.KEY_OBJECT_ID, strDocId);
			mapProgramMap.put(PGWidgetConstants.OBJ_SELECT, jsonInputData.getString(PGWidgetConstants.OBJ_SELECT));
			return getStudyLegDetails(context, mapProgramMap);

		} catch (Exception e) {
			if (isTransactionActive) {
				ContextUtil.abortTransaction(context);
			}
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
			jsonBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonBuilder.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonBuilder.build().toString();
		}

	}
}
