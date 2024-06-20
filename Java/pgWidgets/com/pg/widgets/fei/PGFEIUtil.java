
package com.pg.widgets.fei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.dassault_systemes.enovia.formulation.enumeration.FormulationAttribute;
import com.matrixone.apps.cpn.util.BusinessUtil;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGFEIUtil{

	static final String TYPE_PGEXPERIMENT = PropertyUtil.getSchemaProperty(null, "type_pgExperiment");
	static final String POLICY_PGEXPERIMENT = PropertyUtil.getSchemaProperty(null, "policy_pgExperiment");
	static final String RELATIONSHIP_PGVARIANTS = PropertyUtil.getSchemaProperty(null, "relationship_pgVariants");
	public static final String RELATIONSHIP_PGCENTERLINE = PropertyUtil.getSchemaProperty(null, "relationship_pgCenterline");
	public static final String TYPE_CHARACTERISTIC = PropertyUtil.getSchemaProperty(null, "type_pgPerformanceCharacteristic");
	public static final String TYPE_CENTERLINE = PropertyUtil.getSchemaProperty(null, "type_pgCenterline");
	static final String ATTRIBUTE_PGUOM = PropertyUtil.getSchemaProperty(null, "attribute_pgUnitOfMeasure");
	static final String POLICY_EXTENDED_DATA = PropertyUtil.getSchemaProperty(null,"policy_Characteristic");
	static final String CENTERLINE_PICKLIST = PropertyUtil.getSchemaProperty(null,"type_pgPLICenterline");
	static final String ATTRIBUTE_COMPLETION_STATUS = PropertyUtil.getSchemaProperty(null,"attribute_CompletionStatus");
	static final String ATTRIBUTE_ERROR_MESSAGE = PropertyUtil.getSchemaProperty(null,"attribute_ErrorMessage");
	static final String REL_EXTENDED_DATA = PropertyUtil.getSchemaProperty(null,"relationship_Characteristic");
	static final String ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS= PropertyUtil.getSchemaProperty(null,"attribute_pgCharacteristicSpecifics");
	static final String ATTR_PG_LOWERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty(null,"attribute_pgLowerSpecificationLimit");
	static final String ATTR_PG_UPPERSPECIFICATIONLIMIT = PropertyUtil.getSchemaProperty(null,"attribute_pgUpperSpecificationLimit");
	static final String ATTR_PG_LOWERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty(null,"attribute_pgLowerRoutineReleaseLimit");
	static final String ATTR_PG_TARGET = PropertyUtil.getSchemaProperty(null,"attribute_pgTarget");
	static final String ATTR_PG_LOWER_TARGET = PropertyUtil.getSchemaProperty(null,"attribute_pgLowerTarget");
	static final String ATTR_PG_UPPER_TARGET = PropertyUtil.getSchemaProperty(null,"attribute_pgUpperTarget");
	static final String ATTR_PG_UPPERROUTINERELEASELIMIT = PropertyUtil.getSchemaProperty(null,"attribute_pgUpperRoutineReleaseLimit");
	static final String TYPE_ASSEMBLED_PRODUCT_PART = PropertyUtil.getSchemaProperty(null,"type_pgAssembledProductPart");
	static final String TYPE_PG_RAW_MATERIAL = PropertyUtil.getSchemaProperty(null, "type_pgRawMaterial");
	static final String TYPE_RAW_MATERIAL = PropertyUtil.getSchemaProperty(null, "type_RawMaterial");
	static final String REL_PG_CHARS_TO_PGLICHARACTERISTIC = PropertyUtil.getSchemaProperty(null, "relationship_pgPCharstopgPLICharacteristic");
	static final String TYPE_PG_PLICHARSPECIFICS = PropertyUtil.getSchemaProperty(null,"type_pgPLICharacteristicSpecifics");
	static final String TYPE_TEST_METHOD_SPECIFICATION = PropertyUtil.getSchemaProperty(null,"type_TestMethodSpecification");
	static final String ATTR_PG_CHARACTERISTIC = PropertyUtil.getSchemaProperty(null,"attribute_pgCharacteristic"); 
	static final String ATTR_PG_SHARED_TABLE_CHAR_SEQUENCE = PropertyUtil.getSchemaProperty(null,"attribute_SharedTableCharacteristicSequence");
	static final String TYPE_PG_PLICHARACTERISTIC = PropertyUtil.getSchemaProperty(null,"type_pgPLICharacteristic");
	static final String ATTRIBUTE_NOTEBOOKNUMBER = PropertyUtil.getSchemaProperty(null,"attribute_NotebookNumber");
	static final String ATTRIBUTE_PGOPERATIONALLINE = PropertyUtil.getSchemaProperty(null,"attribute_pgOperationalLine");
	static final String ATTRIBUTE_PGPLANT = PropertyUtil.getSchemaProperty(null,"attribute_pgPlant");
	static final String TYPE_PLANT = PropertyUtil.getSchemaProperty(null, "type_Plant");
	static final String TYPE_PG_PL_SUBSET_LIST = PropertyUtil.getSchemaProperty(null, "type_pgPLSubsetList");
	static final String TYPE_PG_PLI_OPERATIONALLINE = PropertyUtil.getSchemaProperty(null, "type_pgPLIOperationalLine");
	static final String ATTR_COMMENT = PropertyUtil.getSchemaProperty(null, "attribute_Comment");
	static final String ATTR_COMMENTS = PropertyUtil.getSchemaProperty(null, "attribute_Comments");
	static final String ATTR_PG_BASEUOM = PropertyUtil.getSchemaProperty(null, "attribute_pgBaseUnitOfMeasure");
	static final String ATTR_PG_MIN_QUANTITY = PropertyUtil.getSchemaProperty(null, "attribute_pgMinActualPercenWet");
	static final String ATTR_PG_MAX_QUANTITY = PropertyUtil.getSchemaProperty(null, "attribute_pgMaxActualPercenWet");
	static final String RELATIONSHIP_PENDING_JOB = PropertyUtil.getSchemaProperty(null, "relationship_PendingJob");
	static final String RELATIONSHIP_PL_COMMON_REL = PropertyUtil.getSchemaProperty(null, "relationship_pgPLCommonRel");
	static final String RELATIONSHIP_PL_SUBSET_ITEM = PropertyUtil.getSchemaProperty(null, "relationship_pgPLSubsetItem");
	static final String RELATIONSHIP_TEMPLATE_TO_REPORT_FUNCTION = PropertyUtil.getSchemaProperty(null, "relationship_pgPDTemplatestopgPLIReportedFunction");
	static final String ATTRIBUTE_PROGRAM_ARGS = PropertyUtil.getSchemaProperty(null, "attribute_ProgramArguments");
	static final String ATTRIBUTE_ACTION_REQUIRED = PropertyUtil.getSchemaProperty(null, "attribute_pgActionRequired");
	static final String ATTRIBUTE_JOB_ID = PropertyUtil.getSchemaProperty(null, "attribute_pgJobId");
	static final String ATTRIBUTE_JOB_PROGRAM_AGRS = PropertyUtil.getSchemaProperty(null, "attribute_ProgramArguments");
	static final String ATTRIBUTE_JOB_METHOD_NAME = PropertyUtil.getSchemaProperty(null, "attribute_MethodName");
	static final String PERSON_AGENT = PropertyUtil.getSchemaProperty(null, "person_UserAgent");
	static final String ATTRIBUTE_PGEMPTYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgSecurityEmployeeType");
	static final String ATTRIBUTE_PG_FEI_SEQUENCE = PropertyUtil.getSchemaProperty(null, "attribute_pgFEISequence");
	static final String ATTRIBUTE_PG_REPORTED_FUNCTION = PropertyUtil.getSchemaProperty(null, "attribute_pgReportedFunction");
	static final String ATTR_EMPLOYEETYPE_EBP = "EBP";

	static final String KEY_JOB_STATUS = "jobStatus";
	static final String KEY_JOB_ID = "jobId";
	static final String JOB_SUCCEEDED = "Succeeded";
	static final String KEY_NUMBEROFCOPIES = "numberofcopies";
	static final String KEY_RESULT = "Result";
	static final String KEY_EXPERIMENTS = "ExperimentInfo";
	static final String KEY_JOB_COMPLETION_STATUS = "jobCompletionStatus";
	static final String KEY_JOB_ERROR_MESSAGE = "jobErrorMessage";
	static final String KEY_JOB_METHOD_NAME = "jobMethodName";
	static final String KEY_FOLDER_CONTENT = "folderContent";
	static final String KEY_EXPERIMENT_STATUS = "ExperimentStatus";
	static final String KEY_HASH = "#";
	static final String KEY_HIPHEN = "-";
	static final String KEY_1_TYPE = "1-type";
	static final String KEY_2_TYPE = "2-type";
	static final String KEY_1_NAME = "1-name";
	static final String KEY_1_ID = "1-id";
	static final String KEY_2_NAME = "2-name";
	static final String KEY_1_REVISION = "1-revision";
	static final String KEY_2_REVISION = "2-revision";
	static final String KEY_2_ID = "2-id";
	static final String KEY_2_PHYSICAL_ID = "2-physicalid";
	static final String KEY_1_CURRENT = "1-current";
	static final String KEY_2_CURRENT = "2-current";
	static final String KEY_2_RELEASE_PHASE = "2-attribute[Release Phase]";
	static final String KEY_2_ORIGINATED = "2-originated";
	static final String KEY_2_ORIGINATOR = "2-attribute[Originator]";
	static final String KEY_2_TITLE = "2-attribute[Title]";
	static final String KEY_2_QUANTITY = "2-attribute[Quantity]";
	static final String KEY_2_MIN_QUANTITY = "2-attribute[pgMinActualPercenWet]";
	static final String KEY_2_MAX_QUANTITY = "2-attribute[pgMaxActualPercenWet]";
	static final String KEY_2_BASE_UNIT_OF_MEASURE = "2-attribute[pgBaseUnitOfMeasure]";
	static final String KEY_2_COMMENT = "2-attribute[Comment]";
	static final String KEY_2_COMMENTS = "2-attribute[Comments]";
	static final String KEY_2_ID_CONNECTION = "2-id[connection]";
	static final String KEY_TABLE_NAME = "tableName";
	static final String KEY_CHARACTERISTIC = "characteristics";
	static final String KEY_CENTER_LINE = "centerline";
	static final String KEY_VALUE  = "value";
	static final String KEY_BOM = "BOM";
	static final String KEY_MIN = "Min";
	static final String KEY_MAX = "Max";
	static final String KEY_UNIT_OF_MEASURE = "Unit of Measure";
	static final String KEY_COMMENTS = "Comments";
	static final String KEY_LOWER_SPEC_LIMIT = "Lower Specification Limit";
	static final String KEY_UPPER_SPEC_LIMIT = "Upper Specification Limit";
	static final String KEY_ACTION_REQUIRED = "Action Required";
	static final String KEY_EXTRA_INFO = "extraInfo";
	static final String KEY_FIELD = "field";
	static final String KEY_REL_ID = "relId";
	static final String KEY_COL_ID = "colId";
	static final String KEY_ROW_ID = "rowId";
	static final String KEY_ROW_IDENTIFIER = "rowIdentifier";
	static final String KEY_UPDATED_VALUES = "updatedValues";
	static final String KEY_NEW_VALUE = "newValue";
	static final String KEY_NEW_DATA = "newData";
	static final String KEY_CONTENT_ID = "ContentId";
	static final String KEY_ACTION  = "action";
	static final String STATE_OBSOLETE = "Obsolete";
	static final String STATE_RELEASE = "Release";
	static final String KEY_ACTION_COPY_VARIANTS  = "copyVariants";
	static final String KEY_ACTION_REUSE_VARIANTS  = "reuseVariants";
	static final String KEY_REL_IDS = "RelIds";
	static final String KEY_SOURCE_ID = "sourceId";
	static final String KEY_SOURCE_NAME = "sourceName";

	static final String KEY_JPO_FEI_MASS_EDIT = "PGFEIProcess";
	static final String KEY_METHOD_CREATE_CLONE_APP = "createCloneAPP";
	static final String KEY_METHOD_ADD_EXISTING = "addExisting";
	static final String KEY_BACKGROUND_JOB_TITLE = "Background Job for FEI Widget";
	static final String KEY_SUCCEEDED = "Succeeded";
	static final String KEY_NOTIFY_OWNER = "Notify Owner";
	static final String KEY_NO = "No";
	static final String KEY_CENTERLINE = "CenterlineInfo";
	static final String KEY_PERFORMANCECHAR = "PerformanceCharInfo";
	static final String KEY_2_ATTRIBUTE_CHARACTERISTIC = "2-attribute[pgCharacteristic]";
	static final String KEY_2_ATTRIBUTE_CHARACTERISTIC_SPECIFIC = "2-attribute[pgCharacteristicSpecifics]";
	static final String KEY_2_ATTRIBUTE_LOWER_SPECIFICATION_LIMIT = "2-attribute[pgLowerSpecificationLimit]";
	static final String KEY_2_ATTRIBUTE_TARGET = "2-attribute[pgTarget]";
	static final String KEY_2_ATTRIBUTE_LOWER_TARGET = "2-attribute[pgLowerTarget]";
	static final String KEY_2_ATTRIBUTE_UPPER_TARGET = "2-attribute[pgUpperTarget]";
	static final String KEY_2_ATTRIBUTE_UPPER_SPECIFICATION_LIMIT = "2-attribute[pgUpperSpecificationLimit]";
	static final String KEY_2_ATTRIBUTE_UNIT_OF_MEASURE = "2-attribute[pgUnitOfMeasure]";
	static final String KEY_2_ATTRIBUTE_ACTION_REQUIRED = "2-attribute[pgActionRequired]";
	static final String KEY_2_ATTRIBUTE_REPORTED_FUNCTION = "2-attribute[pgReportedFunction]";
	static final String KEY_2_TEST_METHOD_NAME  = "2-to[Reference Document].from[pgTestMethod].name";
	static final String KEY_2_TEST_METHOD_ID  = "2-to[Reference Document].from[pgTestMethod].id";
	static final String KEY_2_TEST_METHOD_SPEC_NAME  = "2-to[Reference Document].from[Test Method Specification].name";
	static final String KEY_2_TEST_METHOD_SPEC_ID  = "2-to[Reference Document].from[Test Method Specification].id";
	static final String KEY_TARGET = "Target";
	static final String KEY_CHARACTERISTIC_SPECIFIC = "Characteristic Specifics";
	static final String KEY_TEST_METHOD = "Test Method";
	static final String VALUE_TEST_METHOD = "testMethod";
	static final String VALUE_TEST_METHOD_ID  = "testMethodId";
	static final String CONST_STRING_ZERO = "0";
	static final String CONST_STRING_TEN = "10";
	static final String CONST_STRING_ONE = "1";
	static final String CONSTANT_STRING_STAR = "*";
	static final String ERROR_EMPTYID = "Object Id empty";
	static final String ERROR_COPY_EXPERIMENT_ID_EMPTY = "Id for Experiment to be copied is empty.";
	static final String STIRNG_INPUTDATA = "strInputData";
	static final String KEY_REVISE_CONTENT = "reviseContent";
	static final String KEY_CHARSPECDATA = "CharSpecDataInfo";
	static final String LATESTREV_WHERECLAUSE = "latest == TRUE";
	static final String ATTRIBUTE_LEFT_BRACKET = "attribute[";
	static final String RIGHT_BRACKET = "]";
	static final String SELECTABLE_TO = "to[";
	static final String SELECTABLE_FROM_NAME = "].from.name";
	static final String SELECTABLE_FROM_ID = "].from.id";
	static final String SELECTABLE_REL_ID = "].id";
	static final String SELECTABLE_REL_NAME = "].name";
	static final String SELECTABLE_CURRENT_ID = "].current";
	static final String TEST_METHOD_SPECIFICATION = "Test Method Specification";
	static final String SELECTABLE_FROM = "from[";
	static final String KEY_EXTENDEDDATA = "ExtendedDataInfo";
	static final String KEY_OPERATIONALLINE = "operationalLine";
	static final String KEY_ELNNUMBER = "ELMNumber";
	static final String KEY_PLANTID = "plantid";
	static final String ACTIVEPLANT_WHERECLAUSE = "current == Active";
	static final String KEY_EXPID = "ExperimentId";
	static final String KEY_PLANTDATA = "PlantDataInfo";
	static final String KEY_DOUBELEQUALS = "==";
	static final String KEY_ACCESS = "access";
	static final String KEY_SELECTEDMEMBERS = "selectedMembers";
	static final String KEY_METHOD_REVISE_VARIENTS = "reviseVarients";
	static final String KEY_REVISE = "REVISE";
	static final String KEY_CREATE = "CREATE";
	static final String STATE_COMPLETED = "Completed";
	static final String STATE_ARCHIVED = "Archived";
	static final String SELECTABLE_FROMTO = "].to.";
	static final String STATE_INACTIVE = "Inactive";
	static final String STATE_ACTIVE = "Active";
	static final String STATE_NEW = "New";
	static final String STATE_PRELIMINARY = "Preliminary";
	static final String SEPARATOR_NEW_LINE = "\n";
	static final String VALUE_DEVELOPMENT = "Development";
	static final String VALUE_PILOT = "Pilot";
	static final String VALUE_PRODUCTION = "Production";
	static final String KEY_PROGRAM_ARGUMENTS = "ProgramArgs";
	static final String SELECTABLE_PLANT = "to[Manufacturing Responsibility].from.name";
	static final String FAMILY_CARE_PILOT_PLANT = "Family Care Pilot Plant";
	static final String KEY_JOB_ACTION = "jobAction";
	static final String KEY_JOB_RUNNING = "Running";
	static final String KEY_JOB_SUBMITTED = "Submitted";
	static final String KEY_JOB_FAILED = "Failed";
	static final String KEY_JOB_ABORTED = "Aborted";
	static final String EXCEPTION_MESSAGE  = "Exception in PGFEIUtil";
	static final String METHOD_REVISE_JOB  = "reviseVarients";
	static final String ERROR_VALID_SOURCE_MESSAGE = "Please drop only non-obsolete Pilot or Production DSM Originated APPs, matching your security classification preferences.";
	static final String ERROR_VALID_APP_MESSAGE = "Please drop only non-obsolete Pilot DSM Originated APPs, matching your security classification preferences.";
	static final String SELECTABLE_FROM_PATH = ".from[";
	static final String SELECTABLE_TO_PATH = ".to[";
	static final String KEY_DELETE_ACTION = "delete";
	static final String KEY_EXPERIMENT_READONLY = "isExperimentReadOnly";
	static final String KEY_SECURITY_CLASSIFICATION = "securityClassification";
	static final String ERROR_INVALID_APP = "Invalid APP: ";
	static final String VALUE_COPY_OF = "Copy of ";
	private static final Logger logger = Logger.getLogger(PGFEIUtil.class.getName());
	private static final String DELETE_MSG1 = "Delete failed! The APP's of the Experiment ";
	private static final String DELETE_MSG3 = " are in Released state";
	private static final String DELETE_APP_FAILURE = "APP's that are connected to other Active Experiment are not deleted.";
	private static final String NEW_LINE = "\n";
	private static final String CONTENT_SELECT = "contentSelects";
	private static final String NOT_EBP_USERS = "Following selected users are not EBP users. Please remove them and then proceed: ";

	public PGFEIUtil(Context context, String[] args) {
	}
	
	/**
	 * Create copy of varient
	 * @param context
	 * @param strInputData
	 * @return String as json
	 * @throws Exception
	 */
	public static String createClone(Context context, String strInputData) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String strExperimentsId = jsonInputData.getString(DomainConstants.SELECT_ID);
		String strSourceProdDataId = jsonInputData.getString(KEY_SOURCE_ID);
		String strNumberOfCopies = jsonInputData.getString(KEY_NUMBEROFCOPIES);
		String strObjectSelects = jsonInputData.getString(PGWidgetConstants.OBJ_SELECT);
		String relSelectablesList = jsonInputData.getString(PGWidgetConstants.KEY_RELATIONSHIPSELECTS);
		createVariant(context, strExperimentsId, strSourceProdDataId, strNumberOfCopies, strObjectSelects);
		return getExperimentContents(context, strExperimentsId, strObjectSelects, relSelectablesList);
	}

	/**
	 * Create copy of varient
	 * @param context
	 * @param strExperimentsId
	 * @param strSourceProdDataId
	 * @param strNumberOfCopies
	 * @param strSelectables
	 * @return String as json
	 * @throws Exception
	 */
	public static void createVariant(Context context, String strExperimentsId, String strSourceProdDataId,
			String strNumberOfCopies, String strSelectables) throws NumberFormatException, MatrixException {
		try {
			String[] args1 = { strSourceProdDataId, strNumberOfCopies, strExperimentsId, strSelectables };
			if (Integer.parseInt(strNumberOfCopies) > 1) {
				String[] strArgs = {};
				Job bgJob = new Job(KEY_JPO_FEI_MASS_EDIT, KEY_METHOD_CREATE_CLONE_APP, strArgs);
				bgJob.setTitle(KEY_BACKGROUND_JOB_TITLE);

				bgJob.setAllowreexecute(KEY_NO);			
				bgJob.setNotifyOwner(KEY_NO);
				bgJob.create(context);
				String[] strProgArgsArray = { strSourceProdDataId, strNumberOfCopies, strExperimentsId, strSelectables };
				String strEncodedArgs = StringUtil.join(strProgArgsArray, SEPARATOR_NEW_LINE);
				bgJob.setAttributeValue(context, ATTRIBUTE_PROGRAM_ARGS, strEncodedArgs);
				submitJob(context, bgJob);

				DomainObject domExp = DomainObject.newInstance(context, strExperimentsId);
				domExp.setAttributeValue(context, ATTRIBUTE_JOB_ID, bgJob.getId(context));

			} else {
				JPO.invoke(context, KEY_JPO_FEI_MASS_EDIT, null, KEY_METHOD_CREATE_CLONE_APP, args1, JsonArray.class);
			}

		} catch (NumberFormatException | MatrixException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;
		}
	}

	/**
	 * Method to create background job
	 * @param context
	 * @param jobArgs
	 * @param strProgramName
	 * @param strMethodName
	 * @param strTitle
	 * @param strAllowExecute
	 * @param strNotifyOwner
	 * @return Job object
	 * @throws MatrixException
	 */
	public static Job createBackgroundJob(Context context, String[] jobArgs, String strProgramName,
			String strMethodName, String strTitle, String strAllowExecute, String strNotifyOwner) throws MatrixException {
		Job bgJob = null;
		try {
			String[] strArgs = {};
			bgJob = new Job(strProgramName, strMethodName, strArgs);
			bgJob.setTitle(strTitle);
			bgJob.setAllowreexecute(strAllowExecute);			
			bgJob.setNotifyOwner(strNotifyOwner);
			bgJob.create(context);
			String strEncodedArgs = StringUtil.join(jobArgs, SEPARATOR_NEW_LINE);
			bgJob.setAttributeValue(context, ATTRIBUTE_PROGRAM_ARGS, strEncodedArgs);
			submitJob(context, bgJob);
		} catch (MatrixException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;
		}
		return bgJob;
	}

	/**
	 * Method which used to submit the created Job
	 * 
	 * @param context : Context eMatrix context object
	 * @param bgJob   : Job object needs to be submitted
	 * @throws MatrixException
	 */
	static void submitJob(Context context, Job bgJob) throws MatrixException {

		try {
			bgJob.setStartDate(context);
			bgJob.submit(context);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;
		}
	}

	/**
	 * Method to get experiment info
	 * @param context
	 * @param strExperimentsId
	 * @param strSelectables
	 * @return String as json
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	static String getExperimentsDetail(Context context, String strExperimentsId, String strSelectables) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		DomainObject domObj = DomainObject.newInstance(context, strExperimentsId);
		JsonArrayBuilder jsonArrRelated = Json.createArrayBuilder();
		StringList slSelects = new StringList();
		slSelects.add(PGWidgetConstants.SELECT_PHYSICAL_ID);
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_DESCRIPTION);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		slSelects.add(DomainConstants.SELECT_TYPE);
		StringList slObjectSels = StringUtil.split(strSelectables, PGWidgetConstants.KEY_COMMA_SEPARATOR);
		slSelects.addAll(slObjectSels);
		Map<?, ?> dataMap = domObj.getInfo(context, slSelects);
		MapList mlList = new MapList();
		mlList.add(dataMap);
		if (dataMap != null && !dataMap.isEmpty()) {
			jsonArrRelated = buildJsonArrayFromMaplist(context, mlList);
		}
		output.add(KEY_EXPERIMENTS, jsonArrRelated);
		return output.build().toString();
	}

	/**
	 * Method to get the Job status
	 * @param context : Context eMatrix context object
	 * @param strReportId : String report object is
	 * @param strJobId : String Job object id
	 * @return : String json with Job details else report doc info
	 * @throws Exception
	 */
	public static JsonObject getJobStatus(Context context, String strObjectId) throws FrameworkException {
		JsonObjectBuilder jsonObjOutput = Json.createObjectBuilder();
		boolean isContextPushed = false;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				DomainObject domExp = DomainObject.newInstance(context, strObjectId);
				String strExperimentState = domExp.getInfo(context, DomainConstants.SELECT_CURRENT);
				String strJobId = domExp.getAttributeValue(context, ATTRIBUTE_JOB_ID);
				if (UIUtil.isNullOrEmpty(strJobId)) {
					jsonObjOutput.add(KEY_JOB_STATUS, STATE_COMPLETED);
				} else {
					StringList slJobSelect = new StringList(3);
					slJobSelect.add(DomainConstants.SELECT_CURRENT);
					slJobSelect.add(DomainObject.getAttributeSelect(ATTRIBUTE_COMPLETION_STATUS));
					slJobSelect.add(DomainObject.getAttributeSelect(ATTRIBUTE_ERROR_MESSAGE));
					slJobSelect.add(DomainObject.getAttributeSelect(ATTRIBUTE_PROGRAM_ARGS));
					slJobSelect.add(DomainObject.getAttributeSelect(ATTRIBUTE_JOB_METHOD_NAME));

					ContextUtil.pushContext(context);
					isContextPushed = true;
					DomainObject jobObject = DomainObject.newInstance(context, strJobId);
					Map<?, ?> jobMap = jobObject.getInfo(context, slJobSelect);

					jsonObjOutput.add(KEY_JOB_ID, strJobId);
					jsonObjOutput.add(KEY_EXPERIMENT_STATUS, strExperimentState);
					jsonObjOutput.add(KEY_JOB_STATUS, (String) jobMap.get(DomainConstants.SELECT_CURRENT));
					jsonObjOutput.add(KEY_JOB_COMPLETION_STATUS, (String) jobMap.get(DomainObject.getAttributeSelect(ATTRIBUTE_COMPLETION_STATUS)));
					jsonObjOutput.add(KEY_JOB_ERROR_MESSAGE, (String) jobMap.get(DomainObject.getAttributeSelect(ATTRIBUTE_ERROR_MESSAGE)));
					jsonObjOutput.add(KEY_JOB_METHOD_NAME, (String) jobMap.get(DomainObject.getAttributeSelect(ATTRIBUTE_JOB_METHOD_NAME)));
					jsonObjOutput.add(KEY_PROGRAM_ARGUMENTS, (String) jobMap.get(DomainObject.getAttributeSelect(ATTRIBUTE_PROGRAM_ARGS)));
				}
			}
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonObjOutput.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonObjOutput.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			throw e;
		}
		finally {
			if (isContextPushed) {
				try {
					ContextUtil.popContext(context);
				} catch (FrameworkException e) {
					logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
				}
				isContextPushed = false;
			}
		}
		return jsonObjOutput.build();
	}	

	/**
	 * Method to create a new Experiment(Workspace Vault) under a workspace
	 * @param context
	 * @param jsonInputData
	 * @return String in json format with created Experiment Id
	 * @throws Exception
	 */
	public static String createExperiment(Context context, String strInputData) throws Exception {
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strPlants = "";
			String strELMNumber = "";
			String strOperationalLine = "";
			String strDescription = "";

			String strExperimentName = jsonInputData.getString(DomainConstants.SELECT_NAME);
			String strSourceProdDataId = jsonInputData.getString(KEY_SOURCE_ID);
			
			String strNumberOfCopies = jsonInputData.getString(KEY_NUMBEROFCOPIES);
			String strObjectSelects = jsonInputData.getString(PGWidgetConstants.OBJ_SELECT);
			if (jsonInputData.containsKey(KEY_PLANTID)) {
				strPlants = jsonInputData.getString(KEY_PLANTID);
			}
			if (jsonInputData.containsKey(KEY_ELNNUMBER)) {
				strELMNumber = jsonInputData.getString(KEY_ELNNUMBER);
			}
			if (jsonInputData.containsKey(KEY_OPERATIONALLINE)) {
				strOperationalLine = jsonInputData.getString(KEY_OPERATIONALLINE);
			}
			if (jsonInputData.containsKey(PGWidgetConstants.KEY_DESCRIPTION)) {
				strDescription = jsonInputData.getString(PGWidgetConstants.KEY_DESCRIPTION);
			}
			DomainObject domObj = PGWidgetUtil.createObjectWithAutoname(context, TYPE_PGEXPERIMENT, POLICY_PGEXPERIMENT);
			String strExperimentId = domObj.getId(context);
			Map<String, String> mapAttr = new HashMap<String, String>();
			mapAttr.put(DomainConstants.ATTRIBUTE_TITLE, strExperimentName);
			mapAttr.put(ATTRIBUTE_NOTEBOOKNUMBER, strELMNumber);
			mapAttr.put(ATTRIBUTE_PGOPERATIONALLINE, strOperationalLine);
			mapAttr.put(ATTRIBUTE_PGPLANT, strPlants);
			domObj.setAttributeValues(context, mapAttr);
			domObj.setDescription(context,strDescription);

//			StringList slPlantIds = StringUtil.split(strPlants, PGWidgetConstants.KEY_PIPE_SEPARATOR);
//			String[] sPlantIdsArray = slPlantIds.toArray(new String[slPlantIds.size()]);
//			domObj.addRelatedObjects(context,new RelationshipType(DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY),false,sPlantIdsArray);

			createVariant(context, strExperimentId, strSourceProdDataId, strNumberOfCopies, strObjectSelects);
			setCharSequenceOnExperiment(context, strExperimentId, strSourceProdDataId);
			return getAllExperiments(context, strInputData);
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;
		}
	}

	/**
	 * Method to check is valid source APP
	 * @param context
	 * @param strSourceID
	 * @param strSecurityClass
	 * @return String as json format with status
	 * @throws Exception
	 */
	public static String isValidPart(Context context, String strSourceID, String strSecurityClass) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		    JsonObject outputObj = isInvalidSourceProduct(context, strSourceID, strSecurityClass);
		    boolean isInvalidSourceAPP = outputObj.getBoolean(PGWidgetConstants.KEY_STATUS);
		    output.add(PGWidgetConstants.KEY_DISPLAY_NAME,outputObj.getString(PGWidgetConstants.KEY_DISPLAY_NAME));
		    if(isInvalidSourceAPP){
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			output.add(PGWidgetConstants.KEY_ERROR,ERROR_VALID_SOURCE_MESSAGE);
			return output.build().toString();
		}
		output.add(PGWidgetConstants.KEY_STATUS,PGWidgetConstants.KEY_SUCCESS);
		return output.build().toString();
	}
	
	/**
	 * Method to check if given source APP is valid or not
	 * @param context
	 * @param strSourceProdDataId
	 * @param strSecurityClass
	 * @return response as json object
	 * @throws Exception
	 */
	private static JsonObject isInvalidSourceProduct(Context context, String strSourceProdDataId, String strSecurityClass) throws Exception {		
		JsonObjectBuilder result = Json.createObjectBuilder();
		String strSecClassesSelect = new StringBuilder(SELECTABLE_TO).append(DomainConstants.RELATIONSHIP_PROTECTED_ITEM).append(SELECTABLE_FROM_NAME).toString();	
		String strPolicyECPart = PropertyUtil.getSchemaProperty(context, "policy_ECPart");
		Map<?, ?> dataMap = getValidAPPDetails(context, strSourceProdDataId, strSecClassesSelect);
		StringList slSecClasses=null;
		if(dataMap.get(strSecClassesSelect) instanceof StringList) {
		slSecClasses = (StringList) dataMap.get(strSecClassesSelect);
		}else {
			slSecClasses=new StringList();
			slSecClasses.add((String) dataMap.get(strSecClassesSelect));
		}
		boolean hasSecurityClass = hasValidSecurityClass(strSecurityClass, slSecClasses);
		result.add(PGWidgetConstants.KEY_DISPLAY_NAME,(String)dataMap.get(DomainConstants.SELECT_NAME));	
		String strReleasePhase = (String) dataMap.get(DomainObject.getAttributeSelect(FormulationAttribute.STAGE.getAttribute(context)));
		if(!hasSecurityClass || STATE_OBSOLETE.equals(dataMap.get(DomainConstants.SELECT_CURRENT)) || 
				VALUE_DEVELOPMENT.equals(strReleasePhase) ||
				!strPolicyECPart.equals((dataMap.get(DomainConstants.SELECT_POLICY))) ||
				 !com.pg.v3.custom.pgV3Constants.DSM_ORIGIN.equals((dataMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE))) ||
				 pgApolloConstants.TRACE_LPD.equals(dataMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION)) )
		{
			return result.add(PGWidgetConstants.KEY_STATUS,true).build();	
		}
		return result.add(PGWidgetConstants.KEY_STATUS,false).build();
	}

	public static String isValidBOMItem(Context context, String strSourceID, String strSecurityClass) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObject outputObj = isInvalidBOMItem(context, strSourceID, strSecurityClass);
		boolean isInvalidSourceAPP = outputObj.getBoolean(PGWidgetConstants.KEY_STATUS);
		if(isInvalidSourceAPP){
			output.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			output.add(PGWidgetConstants.KEY_ERROR,ERROR_VALID_SOURCE_MESSAGE);
			return output.build().toString();
		}
		output.add(PGWidgetConstants.KEY_STATUS,PGWidgetConstants.KEY_SUCCESS);
		return output.build().toString();
	}
	private static JsonObject isInvalidBOMItem(Context context, String strSourceProdDataId, String strSecurityClass) throws Exception {		
		JsonObjectBuilder result = Json.createObjectBuilder();
		String strSecClassesSelect = new StringBuilder(SELECTABLE_TO).append(DomainConstants.RELATIONSHIP_PROTECTED_ITEM).append(SELECTABLE_FROM_NAME).toString();	
		String strPolicyECPart = PropertyUtil.getSchemaProperty(context, "policy_ECPart");
		
		DomainObject domObj = DomainObject.newInstance(context, strSourceProdDataId); 
		Map<?, ?> dataMap = getValidAPPDetails(context, strSourceProdDataId, strSecClassesSelect);
		String strType = dataMap.get(DomainConstants.SELECT_TYPE).toString();
		if(!(domObj.isKindOf(context, TYPE_RAW_MATERIAL) || TYPE_ASSEMBLED_PRODUCT_PART.equals(strType))) {
			return result.add(PGWidgetConstants.KEY_STATUS,true).build();
		}
		StringList slSecClasses=null;
		if(dataMap.get(strSecClassesSelect) instanceof StringList) {
		slSecClasses = (StringList) dataMap.get(strSecClassesSelect);
		}else {
			slSecClasses=new StringList();
			slSecClasses.add((String) dataMap.get(strSecClassesSelect));
		}
		boolean hasSecurityClass = hasValidSecurityClass(strSecurityClass, slSecClasses);
		result.add(PGWidgetConstants.KEY_DISPLAY_NAME,(String)dataMap.get(DomainConstants.SELECT_NAME));	
		String strReleasePhase = (String) dataMap.get(DomainObject.getAttributeSelect(FormulationAttribute.STAGE.getAttribute(context)));
		
		if(!hasSecurityClass || STATE_OBSOLETE.equals(dataMap.get(DomainConstants.SELECT_CURRENT)) || 
				!strPolicyECPart.equals((dataMap.get(DomainConstants.SELECT_POLICY))) ||
				(!VALUE_PILOT.equals(strReleasePhase) && !VALUE_PRODUCTION.equals(strReleasePhase)) ||
				!com.pg.v3.custom.pgV3Constants.DSM_ORIGIN.equals((dataMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE))) ||
				pgApolloConstants.TRACE_LPD.equals(dataMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION)) )
		{
			return result.add(PGWidgetConstants.KEY_STATUS,true).build();	
		}
		return result.add(PGWidgetConstants.KEY_STATUS,false).build();
	}

	private static boolean hasValidSecurityClass(String strSecurityClass, StringList slSecClasses) {
		boolean hasSecurityClass = false;
		if(UIUtil.isNotNullAndNotEmpty(strSecurityClass)) {
			StringList slSecClassesToCheck = StringUtil.split(strSecurityClass, PGWidgetConstants.KEY_PIPE_SEPARATOR);
			for(int i=0 ; i<slSecClassesToCheck.size() ; i++) {
				if(slSecClasses.contains(slSecClassesToCheck.get(i))) {
					hasSecurityClass = true;
					break;
				}
			}
		}else {
			hasSecurityClass = true;
		}
		return hasSecurityClass;
	}
	
	
	/**
	 * Get APP Details for given part
	 * @param context
	 * @param strSourceProdDataId
	 * @param strSecClassesSelect
	 * @return Map
	 * @throws FrameworkException
	 */
	private static Map<?, ?> getValidAPPDetails(Context context, String strSourceProdDataId, String strSecClassesSelect )
			throws FrameworkException {	
		StringList slSelects = new StringList(6);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		slSelects.add(DomainConstants.SELECT_POLICY);
		slSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		slSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		slSelects.add(DomainObject.getAttributeSelect(FormulationAttribute.STAGE.getAttribute(context)));
		slSelects.add(strSecClassesSelect);
		
		DomainObject sourceProdDom = DomainObject.newInstance(context,strSourceProdDataId.trim());
		
		// Fix for 22x Upgrade MultiValueList Changes - Start
		StringList slMultiSelect = new StringList();
		slMultiSelect.add(strSecClassesSelect);
		Map<?, ?> dataMap = sourceProdDom.getInfo(context, slSelects, slMultiSelect);
		// Fix for 22x Upgrade MultiValueList Changes - End
		
		return dataMap;
	}

	/**
	 * Method to check if given APP is valid or not
	 * @param context
	 * @param strSourceProdDataId
	 * @param strSecurityClass
	 * @return response as json object
	 * @throws Exception
	 */
	private static JsonObject isInvalidVariant(Context context, String strSourceProdDataId, String strSecurityClass) throws Exception {		
		JsonObjectBuilder result = Json.createObjectBuilder();
		String strSecClassesSelect = new StringBuilder(SELECTABLE_TO).append(DomainConstants.RELATIONSHIP_PROTECTED_ITEM).append(SELECTABLE_FROM_NAME).toString();	
		String strPolicyECPart = PropertyUtil.getSchemaProperty(context, "policy_ECPart");
		Map<?, ?> dataMap = getValidAPPDetails(context, strSourceProdDataId, strSecClassesSelect);
		StringList slSecClasses=null;
		if(dataMap.get(strSecClassesSelect) instanceof StringList) {
		slSecClasses = (StringList) dataMap.get(strSecClassesSelect);
		}else {
			slSecClasses=new StringList();
			slSecClasses.add((String) dataMap.get(strSecClassesSelect));
		}
		
		boolean hasSecurityClass = hasValidSecurityClass(strSecurityClass, slSecClasses);
		result.add(PGWidgetConstants.KEY_DISPLAY_NAME,(String)dataMap.get(DomainConstants.SELECT_NAME));	
		String strReleasePhase = (String) dataMap.get(DomainObject.getAttributeSelect(FormulationAttribute.STAGE.getAttribute(context)));
		if(!hasSecurityClass || STATE_OBSOLETE.equals(dataMap.get(DomainConstants.SELECT_CURRENT)) || 
				!VALUE_PILOT.equals(strReleasePhase) ||
				!strPolicyECPart.equals((dataMap.get(DomainConstants.SELECT_POLICY))) ||
				 !com.pg.v3.custom.pgV3Constants.DSM_ORIGIN.equals((dataMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE))) ||
				 pgApolloConstants.TRACE_LPD.equals(dataMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION)) )
		{
			return result.add(PGWidgetConstants.KEY_STATUS,true).build();	
		}
		return result.add(PGWidgetConstants.KEY_STATUS,false).build();
	}
	public static void addExisting(Context context, String[] args) throws Exception{
		try {
			String strFromObjectId = args[0];
			String strIdsToConnect = args[1]; 
			String strRelName = args[2]; 
			String allowDuplicates = args[3];
			PGWidgetUtil.addExisting(context, strFromObjectId, strIdsToConnect, strRelName, Boolean.parseBoolean(allowDuplicates));
			if(STATE_INACTIVE.equals(DomainObject.newInstance(context, strFromObjectId).getInfo(context, DomainConstants.SELECT_CURRENT))) {
				PGWidgetUtil.promoteDemoteObject(context, strFromObjectId, PGWidgetUtil.OPERATION_PROMOTE);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;
		}
	}
	// remove this
	public static String copyExperiment(Context context, String args[]) throws Exception {
		Map<?, ?> programMap = (Map<?, ?>) JPO.unpackArgs(args);
		String strInputData = (String) programMap.get("strInputData");
		return copyExperiment(context, strInputData);

	}

	/**
	 * Copy and create new Experiment and create clone of all connected variants
	 * @param context
	 * @param strInputData
	 * @return String as json
	 * @throws Exception
	 */
	public static String copyExperiment(Context context, String strInputData) {
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strSourceExpId = jsonInputData.getString(KEY_SOURCE_ID);
			String strContentIds = jsonInputData.getString(KEY_CONTENT_ID);
			String strAction = jsonInputData.getString(KEY_ACTION);
			String strObjSelects = jsonInputData.getString(PGWidgetConstants.OBJ_SELECT);
			String strContentSelects = jsonInputData.getString(CONTENT_SELECT);

			JsonObjectBuilder jsonObjBd = Json.createObjectBuilder();
			if (UIUtil.isNotNullAndNotEmpty(strSourceExpId)) {
				DomainObject domExp = DomainObject.newInstance(context, strSourceExpId);
				String strSymbolicTypeName = FrameworkUtil.getAliasForAdmin(context, PGWidgetConstants.KEY_TYPE, TYPE_PGEXPERIMENT, true);
				String strAutoName = DomainObject.getAutoGeneratedName(context,strSymbolicTypeName,DomainConstants.EMPTY_STRING);  
				BusinessObject busExp = domExp.cloneObject(context, strAutoName, null, null, true);
				
				String strOldExpName = domExp.getInfo(context, DomainConstants.SELECT_NAME);
				String strNewExpId = busExp.getObjectId(context);
				DomainObject domNewExp = DomainObject.newInstance(context, strNewExpId);
				Map<String, String> mapAttr = new HashMap<>();
				mapAttr.put(DomainConstants.ATTRIBUTE_TITLE, VALUE_COPY_OF.concat(strOldExpName));
				mapAttr.put(ATTRIBUTE_NOTEBOOKNUMBER, DomainConstants.EMPTY_STRING);
				domNewExp.setAttributeValues(context, mapAttr);
			
				StringList slContentIds = StringUtil.split(strContentIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				if (slContentIds != null && !slContentIds.isEmpty()) {
					String[] strProgArgsArray = { strContentIds, CONST_STRING_ONE, strNewExpId, DomainConstants.EMPTY_STRING };
					if(slContentIds.size() > 5) {
						Job job = null;
						if(KEY_ACTION_COPY_VARIANTS.equals(strAction)) {
							job = createBackgroundJob(context, strProgArgsArray, KEY_JPO_FEI_MASS_EDIT, KEY_METHOD_CREATE_CLONE_APP, KEY_BACKGROUND_JOB_TITLE, 
									KEY_NO, KEY_NO);
						}else {
							strProgArgsArray = new String[]{ strNewExpId, strContentIds, RELATIONSHIP_PGVARIANTS,"true" };
							job = createBackgroundJob(context, strProgArgsArray, KEY_JPO_FEI_MASS_EDIT, KEY_METHOD_ADD_EXISTING, KEY_BACKGROUND_JOB_TITLE, 
									KEY_NO, KEY_NO);
						}
					
						domNewExp.setAttributeValue(context, ATTRIBUTE_JOB_ID, job.getId(context));
					}else {
						if(KEY_ACTION_COPY_VARIANTS.equals(strAction)) {
							JPO.invoke(context, KEY_JPO_FEI_MASS_EDIT, null, KEY_METHOD_CREATE_CLONE_APP, strProgArgsArray, JsonArray.class);
						}else {
							PGWidgetUtil.addExisting(context, strNewExpId, strContentIds, RELATIONSHIP_PGVARIANTS, true);
							if(STATE_INACTIVE.equals(DomainObject.newInstance(context, strNewExpId).getInfo(context, DomainConstants.SELECT_CURRENT))) {
								PGWidgetUtil.promoteDemoteObject(context, strNewExpId, PGWidgetUtil.OPERATION_PROMOTE);
							}
						}
						
						String strJson = getExperimentContents(context, strNewExpId, strContentSelects, PGWidgetConstants.SELECT_CONNECTION_ID);
						JsonObject jsonObj = PGWidgetUtil.getJsonFromJsonString(strJson);
						jsonObjBd.add(KEY_FOLDER_CONTENT, jsonObj.get(KEY_FOLDER_CONTENT));
					}
					String strJsonExp = getExperimentsDetail(context, strNewExpId, strObjSelects);
					JsonObject jsonExp = PGWidgetUtil.getJsonFromJsonString(strJsonExp);
					jsonObjBd.add(KEY_EXPERIMENTS, jsonExp.getJsonArray(KEY_EXPERIMENTS));
				}
			}
			else {
				jsonObjBd.add(PGWidgetConstants.KEY_ERROR, ERROR_COPY_EXPERIMENT_ID_EMPTY);
			}
			return jsonObjBd.build().toString();
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
			jsonBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonBuilder.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));
			return jsonBuilder.build().toString();
		}
	}

	/**
	 * Method to add existing variants and connect to an Experiment
	 * @param context
	 * @param strInputData
	 * @return String in json format 
	 */
	public static String addNewVariant(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrBuilder = Json.createArrayBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String sObjectId = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
		String strContentIds = jsonInputData.getString(KEY_CONTENT_ID);
		String strObjectSels = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
		String strRelSelects = jsonInputData.getString(PGWidgetConstants.KEY_RELATIONSHIPSELECTS);
		String strSecurityClass = jsonInputData.getString(KEY_SECURITY_CLASSIFICATION);
		String sbErrorMessage; 
		try {
			StringList slContentIds = StringUtil.split(strContentIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			if (slContentIds != null && !slContentIds.isEmpty()) {
				for (int i = 0; i < slContentIds.size(); i++) {
					JsonObject outputObj = isInvalidVariant(context, slContentIds.get(i), strSecurityClass);
					boolean isInvalidSourceAPP = outputObj.getBoolean(PGWidgetConstants.KEY_STATUS);
					if(isInvalidSourceAPP){
						sbErrorMessage = new StringBuilder().append(ERROR_INVALID_APP).append(outputObj.getString(PGWidgetConstants.KEY_DISPLAY_NAME))
								.append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
								.append(ERROR_VALID_APP_MESSAGE).toString();
						jsonObjBuilder.add(PGWidgetConstants.KEY_ERROR,sbErrorMessage);
						return jsonObjBuilder.build().toString();
					}
					connectObject(context, jsonObjBuilder, jsonArrBuilder, sObjectId, strObjectSels, slContentIds.get(i).trim());
				}
			}
			String strJson = getExperimentContents(context, sObjectId, strObjectSels, strRelSelects);
			JsonObject jsonObj = PGWidgetUtil.getJsonFromJsonString(strJson);
			jsonObjBuilder.add(KEY_FOLDER_CONTENT, jsonObj.get(KEY_FOLDER_CONTENT));
			return jsonObjBuilder.build().toString();
		} catch(Exception ex ) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonObjBuilder.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			jsonObjBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			return jsonObjBuilder.build().toString();
		}
	}
	/**
	 * Method to connect an existing content object to workspace vault
	 * @param context
	 * @param jsonInputData
	 * @return String in json format with connection status
	 * @throws Exception
	 */
	public static String addExistingContent(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrBuilder = Json.createArrayBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String sObjectId = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
		String strContentIds = jsonInputData.getString(KEY_CONTENT_ID);
		String strObjectSels = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);

		try {
			StringList slContentIds = StringUtil.split(strContentIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			if (slContentIds != null && !slContentIds.isEmpty()) {
				for (int i = 0; i < slContentIds.size(); i++) {				
					connectObject(context, jsonObjBuilder, jsonArrBuilder, sObjectId, strObjectSels, slContentIds.get(i).trim());
				}
			}
		} catch(Exception ex ) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonObjBuilder.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			jsonObjBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return jsonObjBuilder.build().toString();
	}
	
	/**
	 *  Method to connect an existing content object
	 * @param context
	 * @param jsonObjBuilder
	 * @param jsonArrBuilder
	 * @param sObjectId
	 * @param strObjectSels
	 * @param strContentId
	 * @throws FrameworkException
	 * @throws Exception
	 * @throws MatrixException
	 */
	private static void connectObject(Context context, JsonObjectBuilder jsonObjBuilder,
			JsonArrayBuilder jsonArrBuilder, String sObjectId, String strObjectSels, String strContentId) throws FrameworkException, Exception, MatrixException {
		try {
			DomainObject domFrom = DomainObject.newInstance(context, sObjectId);
			String strExpression = new StringBuilder(SELECTABLE_FROM)
					.append(RELATIONSHIP_PGVARIANTS)
					.append(SELECTABLE_FROMTO)
					.append(PGWidgetConstants.SELECT_PHYSICAL_ID)
					.toString();
			StringList slToIdList = domFrom.getInfoList(context, strExpression);
			if(slToIdList != null && slToIdList.contains(strContentId)) {
				jsonObjBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
				jsonObjBuilder.add(PGWidgetConstants.KEY_ERROR, "Selected Variant(s) is already connected to this experiment.");
			}else {
				PGWidgetUtil.addExisting(context, sObjectId, strContentId,
						RELATIONSHIP_PGVARIANTS, true);
				jsonObjBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonObjBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonObjBuilder.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			jsonObjBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
	}
	

	/**
	 * Method to get Experiments(Workspace Vault) under a workspace
	 * @param context
	 * @param strObjectId
	 * @param strSelectables
	 * @return String in json format with all Experiments in array
	 * @throws Exception
	 */
	static String getAllExperiments(Context context, String strInputData) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder inputData = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData =  PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strSelectables = jsonInputData.getString(PGWidgetConstants.OBJ_SELECT);
			String strDuration = jsonInputData.getString(PGWidgetConstants.DURATION);
			String strObjLimit = jsonInputData.getString(PGWidgetConstants.OBJECT_LIMIT);
			strObjLimit = UIUtil.isNotNullAndNotEmpty(strObjLimit)?strObjLimit:"0";
			inputData.add(PGWidgetConstants.TYPE_PATTERN, TYPE_PGEXPERIMENT);
			inputData.add(PGWidgetConstants.NAME_PATTERN, CONSTANT_STRING_STAR);
			inputData.add(PGWidgetConstants.REVISION_PATTERN, CONSTANT_STRING_STAR);
			inputData.add(PGWidgetConstants.OBJECT_LIMIT, strObjLimit);
			inputData.add(PGWidgetConstants.WHERE_EXP, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.EXPAND_TYPE, PGWidgetConstants.STRING_FALSE);
			inputData.add(PGWidgetConstants.OBJ_SELECT, strSelectables);
			inputData.add(PGWidgetConstants.DURATION, strDuration);
			inputData.add(PGWidgetConstants.ALLOWED_STATE, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.SHOW_OWNED, DomainConstants.EMPTY_STRING);

			MapList mlList = PGWidgetUtil.findObjects(context, inputData.build());
			JsonArrayBuilder jsonArrRelated = Json.createArrayBuilder();
			if (mlList != null && !mlList.isEmpty()) {
				jsonArrRelated = buildJsonArrayFromMaplist(context, mlList);
			}
			output.add(KEY_EXPERIMENTS, jsonArrRelated.build());
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return output.build().toString();
	}

	/**
	 * Converts maplist data to json array
	 * @param context 
	 * @param mlList list of maps containing object details
	 * @return json array format 
	 * @throws Exception
	 */
	private static JsonArrayBuilder buildJsonArrayFromMaplist(Context context, MapList mlList) throws Exception {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		try {
			JsonObjectBuilder jsonObject = null;
			Map<?, ?> objMap = null;
			boolean isExperimentReadable;
			String strId, strValue = DomainConstants.EMPTY_STRING;
			Object objValue = null;
			StringList slTemp = null;
			StringBuilder sbValues = null;
			String strTypeDisplayName = null;
			String strNoAccessMsg = EnoviaResourceBundle.getProperty(context, PGWidgetConstants.FILE_FRAMEWORK_STRING_RESOURCE, context.getLocale(), "emxFramework.Access.NoAccess");
			String strLocale = context.getLocale().getLanguage();
			boolean hasReadAccess = false;
			boolean hasModifyAccess = false;
			for (int i = 0; i < mlList.size(); i++) {
				jsonObject = Json.createObjectBuilder();
				objMap = (Map<?, ?>) mlList.get(i);
				isExperimentReadable = false;
				strId = PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID));
				hasReadAccess = PGWidgetConstants.STRING_TRUE.equalsIgnoreCase((String) objMap.get("current.access[read]"));
				hasModifyAccess =  PGWidgetConstants.STRING_TRUE.equalsIgnoreCase((String)objMap.get("current.access[modify]")); 
				for (Entry<?, ?> entry : objMap.entrySet()) {

					objValue = entry.getValue();									
					if(objValue == null || "".equals(objValue))
					{
						objValue = DomainConstants.EMPTY_STRING;
					}
					if(objValue instanceof String) {
						strValue = (String) objValue;
					}else if(objValue instanceof StringList) {
						slTemp = (StringList) objValue;
						sbValues = new StringBuilder();
						for (int j = 0; j < slTemp.size(); j++) {
							if(UIUtil.isNotNullAndNotEmpty(slTemp.get(j))) {
								sbValues.append(slTemp.get(j));
							}
							if(j != slTemp.size() - 1) {
								sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);
							}
						}
						strValue = sbValues.toString();
					}
					if(entry.getKey().equals(DomainConstants.SELECT_TYPE) && strValue.equals(TYPE_PGEXPERIMENT))
					{
						if(objMap.get(DomainConstants.SELECT_CURRENT).equals(STATE_INACTIVE)) {
							jsonObject.add(KEY_EXPERIMENT_STATUS, getExperimentStatus(context, strId));	
						}else {
							jsonObject.add(KEY_EXPERIMENT_STATUS, STATE_COMPLETED);
						}
						if((!objMap.get(DomainConstants.SELECT_CURRENT).equals(STATE_ACTIVE)) || !hasModifyAccess){
							isExperimentReadable = true; 
						}
						jsonObject.add(KEY_EXPERIMENT_READONLY, isExperimentReadable);
					}

					if (!hasReadAccess) {
						if (objValue.equals(PGWidgetConstants.DENIED)) {
							jsonObject.add((String) entry.getKey(), strNoAccessMsg);
						}
					} 
					else if(DomainConstants.SELECT_TYPE.equals(entry.getKey())) {
						strTypeDisplayName = i18nNow.getTypeI18NString((String)objValue, strLocale);
						jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
						jsonObject.add(PGWidgetConstants.KEY_OBJECT_TYPE, (String)objValue);
					}
					else if(DomainConstants.SELECT_ATTRIBUTE_TITLE.equals(entry.getKey())) {
						jsonObject.add(PGWidgetConstants.KEY_DISPLAY_NAME, UIUtil.isNotNullAndNotEmpty(strValue) ? strValue : objMap.get(DomainConstants.SELECT_NAME).toString());
					}
					else if(DomainConstants.SELECT_ID.equals(entry.getKey())) {
						jsonObject.add(KEY_VALUE,(String)objValue);
					}
					jsonObject.add((String) entry.getKey(), strValue);
				}
				jsonArr.add(jsonObject);
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
		}
		return jsonArr;
	}

	/**
	 * Method to get the Job status
	 * @param context : Context eMatrix context object
	 * @param strId : String Experiment id
	 * @param strCurrentState : Experiment state
	 * @return : String json with Job details else report doc info
	 * @throws Exception
	 */
	private static JsonObject getExperimentStatus(Context context, String strId) throws Exception {
		return getJobStatus(context, strId);
	}	

	/**
	 * Method to get Content
 
	 * @param context
	 * @param mpParamMAP
	 * @return String in json format with all content objects in array
	 * @throws Exception
	 */
	public static String getExperimentContents(Context context,String objectId,String objSelectablesList,String relSelectablesList) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		if (UIUtil.isNotNullAndNotEmpty(objectId)) {
			JsonObjectBuilder inputData = Json.createObjectBuilder();
			try {
				JsonObject jsonObjJob = getJobStatus(context, objectId);
				inputData.add(PGWidgetConstants.KEY_OBJECT_ID, objectId);
				inputData.add(PGWidgetConstants.KEY_RELPATTERN, RELATIONSHIP_PGVARIANTS);
				inputData.add(PGWidgetConstants.TYPE_PATTERN, CONSTANT_STRING_STAR);
				inputData.add(PGWidgetConstants.KEY_EXPANDLEVEL, CONST_STRING_ONE);
				inputData.add(PGWidgetConstants.KEY_WHERECONDITION, DomainConstants.EMPTY_STRING);
				inputData.add(PGWidgetConstants.KEY_GETTO, PGWidgetConstants.STRING_FALSE);
				inputData.add(PGWidgetConstants.KEY_GETFROM, PGWidgetConstants.STRING_TRUE);
				inputData.add(PGWidgetConstants.KEY_LIMIT, CONST_STRING_ZERO);
				inputData.add(PGWidgetConstants.KEY_RELWHERECONDITION, DomainConstants.EMPTY_STRING);
				inputData.add(PGWidgetConstants.KEY_RELATIONSHIPSELECTS, relSelectablesList);
				inputData.add(PGWidgetConstants.KEY_OBJECT_SELECTS, objSelectablesList);

				JsonArrayBuilder jsonArrRelated = Json.createArrayBuilder();
				MapList mlObjectList = PGWidgetUtil.getRelatedObjectsMapList(context,inputData.build());

				if (mlObjectList != null && !mlObjectList.isEmpty()) {
					jsonArrRelated = buildJsonArrayFromMaplistForVariants(context, mlObjectList, jsonObjJob);
				}

				output.add(KEY_FOLDER_CONTENT, jsonArrRelated.build());
			} catch (MatrixException ex) {
				output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
				output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			}
		} else {
			output.add(PGWidgetConstants.KEY_ERROR, ERROR_EMPTYID);
		}

		return output.build().toString();
	}

	/**
	 * Method to get the BOM structure (BOMs and variants) as per hierarchy structure of table
	 * @param context
	 * @param strExperimentId
	 * @return
	 * @throws Exception
	 */
	public static String getBOMWithVariants(Context context, String strExperimentId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			Pattern relPat = new Pattern(RELATIONSHIP_PGVARIANTS);
			relPat.addPattern(DomainConstants.RELATIONSHIP_EBOM);

			String objSelectablesList = (new StringBuilder()).append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(PGWidgetConstants.SELECT_PHYSICAL_ID).append(PGWidgetConstants.KEY_COMMA_SEPARATOR).append(DomainConstants.SELECT_CURRENT)
					.append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainObject.getAttributeSelect(FormulationAttribute.STAGE.getAttribute(context)))
					.append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_NAME).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_REVISION).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_ORIGINATED).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_ORIGINATOR).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(SELECTABLE_FROM).append(RELATIONSHIP_TEMPLATE_TO_REPORT_FUNCTION).append(SELECTABLE_FROMTO).append(DomainConstants.SELECT_NAME).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_BASEUOM).append(RIGHT_BRACKET).toString();
			String relSelects = (new StringBuilder()).append(ATTRIBUTE_LEFT_BRACKET).append(DomainConstants.ATTRIBUTE_QUANTITY).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_MIN_QUANTITY).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_MAX_QUANTITY).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_COMMENT).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(PGWidgetConstants.SELECT_CONNECTION_ID).toString();
			/*
			 * slKeysHierarchy : List of fields in sequence as per hierarchy table (to be displayed in column 0 of table)
			 * index 0 is not used as it is as cell value. It's a placeholder for object's <name.revision>
			 */
			StringList slKeysHierarchy = new StringList();
			slKeysHierarchy.add(KEY_2_NAME);
			slKeysHierarchy.add(KEY_MIN);
			slKeysHierarchy.add(KEY_MAX);
			slKeysHierarchy.add(KEY_UNIT_OF_MEASURE);
			slKeysHierarchy.add(KEY_COMMENTS);
			/*
			 * mapping : mapping of fields to attribute/value to be shown in variant columns against respective BOM
			 * Keys of format '2-attribute[<>]' represents object's level of expansion with starting digit
			 */
			Map<String, String> mapping = new HashMap<>();
			mapping.put(KEY_2_NAME, KEY_2_QUANTITY);
			mapping.put(KEY_MIN, KEY_2_MIN_QUANTITY);
			mapping.put(KEY_MAX, KEY_2_MAX_QUANTITY);
			mapping.put(KEY_UNIT_OF_MEASURE, KEY_2_BASE_UNIT_OF_MEASURE);
			mapping.put(KEY_COMMENTS, KEY_2_COMMENT);
//			Map<String, List<JsonObject>> mapReturn = getBOMStructure(context, strExperimentId, objSelectablesList, relSelects);
//			output.add(KEY_BOM, formatBOM(context, mapReturn, slKeysHierarchy, mapping));
//			return output.build().toString();
			return getExtendedDataDetails(context, strExperimentId, objSelectablesList, relSelects, CONSTANT_STRING_STAR, relPat.getPattern(), slKeysHierarchy, mapping);
		}catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			return output.build().toString();
		}
	}

	/**
	 * Method to getBOMs and variants for given experiment in flat table json format
	 * @param context
	 * @param strExperimentsId
	 * @param strSelectables
	 * @param strRelSelects
	 * @return String in json format with all content objects in array
	 * @throws Exception
	 */
	public static Map<String, List<JsonObject>> getBOMStructure(Context context, String strExperimentsId, String strSelectables, String strRelSelects) throws Exception {		
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder inputData = Json.createObjectBuilder();
		Map<String, List<JsonObject>> mapReturn = new HashMap<>();
		try {
			Pattern relPat = new Pattern(RELATIONSHIP_PGVARIANTS);
			relPat.addPattern(DomainConstants.RELATIONSHIP_EBOM);
			inputData.add(PGWidgetConstants.KEY_OBJECT_ID, strExperimentsId);
			inputData.add(PGWidgetConstants.KEY_RELPATTERN, relPat.getPattern());
			inputData.add(PGWidgetConstants.TYPE_PATTERN, CONSTANT_STRING_STAR);
			inputData.add(PGWidgetConstants.KEY_EXPANDLEVEL, CONST_STRING_ZERO);
			inputData.add(PGWidgetConstants.KEY_WHERECONDITION, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.KEY_GETTO, PGWidgetConstants.STRING_FALSE);
			inputData.add(PGWidgetConstants.KEY_GETFROM, PGWidgetConstants.STRING_TRUE);
			inputData.add(PGWidgetConstants.KEY_LIMIT, CONST_STRING_ZERO);
			inputData.add(PGWidgetConstants.KEY_RELWHERECONDITION, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.KEY_RELATIONSHIPSELECTS, strRelSelects);
			inputData.add(PGWidgetConstants.KEY_OBJECT_SELECTS, strSelectables);

			MapList mlList = PGWidgetUtil.getRelatedObjectsMapList(context, inputData.build());
			/*
			 * convertMapListToJsonFlatTable : converts maplist into flat json array.
			 * Object on any level in expansion is placed on the same level in resultant array
			 * information of its actual level in expansion is also stored
			 */
			mapReturn = getDataTableFormat(mlList, 0);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return mapReturn;
	}

	/**
	 * Method to format JsonArray in the required format so display in UI 
	 * @param jsonArr
	 * @return String in json format with all content objects in array
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	static JsonArray formatBOM(Context context, Map<String, List<JsonObject>> mapFormat, StringList slKeys, Map<String, String> mapping) throws MatrixException {
		JsonArrayBuilder jsonArrBOM = Json.createArrayBuilder();
		JsonObject variant = null;
		JsonObject variantBlank = null;
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonObjectBuilder jsonObjKeyVariantExtra = null;
		JsonArrayBuilder jsonArrHier = null;
		List<JsonObject> jsonArrBlanks = null;
		String strPartNameRev = DomainConstants.EMPTY_STRING;
		String strBOMNameRev = DomainConstants.EMPTY_STRING;
		String strTitle = DomainConstants.EMPTY_STRING;
		String strField = null;
		List<JsonObject> jsonArrVariant = null;
		String strSelectReportFunction = new StringBuilder().append("2-").append(SELECTABLE_FROM).append(RELATIONSHIP_TEMPLATE_TO_REPORT_FUNCTION)
				.append(SELECTABLE_FROMTO).append(DomainConstants.SELECT_NAME).toString();
		try {
			/*
			 * outer most loop : loops around list of keys to generalize behavior for each key in hierarchy
			 */
			for(int index=0 ; index<slKeys.size() ; index++) {
				for(Entry<?, ?> entry : mapFormat.entrySet()) {
					jsonObjKeyVariant = Json.createObjectBuilder();
					jsonObjKeyVariantExtra = Json.createObjectBuilder();
					if(KEY_HASH.equals(entry.getKey())) {
						jsonArrBlanks = (List<JsonObject>) entry.getValue();
					}
					else {
						jsonArrVariant = (List<JsonObject>) entry.getValue();
						for(int i=0 ; i<jsonArrVariant.size() ; i++) {
							jsonArrHier = Json.createArrayBuilder();
							variant = jsonArrVariant.get(i);
							strBOMNameRev = variant.getString(KEY_2_NAME) + "."+ variant.getString(KEY_2_REVISION);
							strPartNameRev = variant.getString(KEY_1_NAME) + "_"+ variant.getString(KEY_1_REVISION);
							strTitle = variant.getString(KEY_2_TITLE);
							jsonObjKeyVariantExtra.add(strPartNameRev, 
									Json.createObjectBuilder().add(KEY_REL_ID, variant.getString(KEY_2_ID_CONNECTION))
									.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_2_TYPE)).add(KEY_COL_ID,variant.getString(KEY_1_ID)).build());

							jsonObjKeyVariant.add(strPartNameRev, variant.getString(mapping.get(slKeys.get(index))));
							if(variant.containsKey(strSelectReportFunction)) {
								jsonObjKeyVariant.add(ATTRIBUTE_PG_REPORTED_FUNCTION, variant.getString(strSelectReportFunction));
							}
							if(variant.containsKey(KEY_2_BASE_UNIT_OF_MEASURE)) {
								jsonObjKeyVariant.add(ATTR_PG_BASEUOM, variant.getString(KEY_2_BASE_UNIT_OF_MEASURE));
							}
							if(jsonArrBlanks != null) {
								for(int j=0 ; j<jsonArrBlanks.size() ; j++) {
									variantBlank = jsonArrBlanks.get(j);
									jsonObjKeyVariant.add(variantBlank.getString(KEY_1_NAME) + "_"+ variantBlank.getString(KEY_1_REVISION), DomainConstants.EMPTY_STRING);
								}
							}
							if(index == 0) {
								jsonArrHier.add(strBOMNameRev);
								jsonObjKeyVariant.add(DomainConstants.ATTRIBUTE_TITLE, strTitle);
								jsonObjKeyVariant.add(PGWidgetConstants.KEY_DISPLAY_TYPE, i18nNow.getTypeI18NString(variant.getString(KEY_2_TYPE), context.getLocale().getLanguage()));
								jsonObjKeyVariant.add(PGWidgetConstants.KEY_OBJECT_TYPE, variant.getString(KEY_2_TYPE));
								jsonObjKeyVariant.add(PGWidgetConstants.KEY_DISPLAY_NAME, 
										UIUtil.isNotNullAndNotEmpty(strTitle) ? strTitle : variant.getString(KEY_2_NAME));
								jsonObjKeyVariant.add(PGWidgetConstants.SELECT_PHYSICAL_ID, variant.getString(KEY_2_PHYSICAL_ID));
								jsonObjKeyVariant.add(DomainConstants.SELECT_CURRENT, variant.getString(KEY_2_CURRENT));
								jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, variant.getString(KEY_2_NAME));
								jsonObjKeyVariant.add(DomainConstants.SELECT_REVISION, variant.getString(KEY_2_REVISION));
								jsonObjKeyVariant.add(DomainConstants.SELECT_ORIGINATED, variant.getString(KEY_2_ORIGINATED));
								jsonObjKeyVariant.add(DomainConstants.SELECT_ORIGINATOR, variant.getString(KEY_2_ORIGINATOR));
								jsonObjKeyVariant.add(DomainObject.getAttributeSelect(FormulationAttribute.STAGE.getAttribute(context)), variant.getString(KEY_2_RELEASE_PHASE));
							}else {
								jsonArrHier.add(strBOMNameRev)
								.add((slKeys.get(index)));
							}
							jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
							jsonObjKeyVariant.add(DomainConstants.SELECT_ID, variant.getString(KEY_2_ID));
							jsonObjKeyVariant.add(KEY_EXTRA_INFO, jsonObjKeyVariantExtra.build());
							strField = mapping.get(slKeys.get(index));
							if(UIUtil.isNotNullAndNotEmpty(strField)) {
								jsonObjKeyVariant.add(KEY_FIELD, StringUtil.split(strField, PGWidgetConstants.KEY_SELECTS_SEPARATOR).get(1));
							}
							jsonObjKeyVariant.add(KEY_TABLE_NAME, KEY_BOM);
						}
						jsonArrBOM.add(jsonObjKeyVariant.build());
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return jsonArrBOM.build();
	}

	/**
	 * Method to get flat table format for BOM structure
	 * @param mlInput
	 * @param iLevel
	 * @return map 
	 * @throws Exception
	 */
	public static Map<String,List<JsonObject>> getDataTableFormat (MapList mlInput, int iLevel) throws Exception {
		Map<String,List<JsonObject>> mapReturn = new HashMap<>();
		try {
			JsonArray jsonArr = PGWidgetUtil.convertMapListToJsonFlatTable(mlInput, iLevel);
			List<JsonObject> listJson = null;
			JsonObject jsonObj;
			String strBOMId;
			for(int i = 0; i < jsonArr.size(); i++) {
				jsonObj = jsonArr.getJsonObject(i);
				try {
					strBOMId = jsonObj.getString(KEY_2_ID);
				}catch (Exception e) {
					strBOMId = KEY_HASH;
				}
				if(mapReturn.containsKey(strBOMId)) {
					listJson = mapReturn.get(strBOMId);
					listJson.add(jsonObj);
				}else {
					listJson = new ArrayList<JsonObject>();
					listJson.add(jsonObj);
				}
				mapReturn.put(strBOMId, listJson);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return mapReturn;
	}
	
	/**
	 * Method to delete any object
	 * @param request
	 * @param jsonInputData
	 * @return String as json with status
	 */
	public static String deleteExperiments(Context context, JsonObject jsonInputData) throws Exception {

		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		boolean isExceptionOccurred = false;
		int iActiveExperiment = 0;
		JsonArray jsonArrIds = jsonInputData.getJsonArray(PGWidgetConstants.KEY_OBJECT_ID);
		String strAction = jsonInputData.getString(KEY_ACTION);
		String strId = "";
		DomainObject domObj = null;
		String strAPPIds = "";
		StringBuilder sbMessage = new StringBuilder();

				
		StringList slObjectSelects = new StringList(2);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_CURRENT);
				
		String strExpressionExpState =new StringBuilder(SELECTABLE_TO)
				.append(RELATIONSHIP_PGVARIANTS)
				.append("].from.current")
				.toString();
		slObjectSelects.add(strExpressionExpState);

	    String strInputExperimentName;
		ContextUtil.startTransaction(context, true);
		try {
			if(strAction.equals(KEY_DELETE_ACTION)){
				for(int i=0; i<jsonArrIds.size(); i++ ) {
					strId = jsonArrIds.getString(i);
					domObj = DomainObject.newInstance(context, strId);
					strInputExperimentName= domObj.getInfo(context,DomainConstants.SELECT_NAME);
				MapList mlExperiment =	domObj.getRelatedObjects(context,
							RELATIONSHIP_PGVARIANTS, // relationshipPattern
							TYPE_ASSEMBLED_PRODUCT_PART, // typePattern
							slObjectSelects, // objectSelects
							null, // relationshipSelects
							false, // getTo
							true, // getFrom
							(short) 1, // recurseToLevel
							"", // objectWhere
							"", // relationshipWhere
							(short) 0);// limit
					Map<?,?> mAppDetails = null;
					for(Object objAPPDetails: mlExperiment) {
						mAppDetails = (Map) objAPPDetails;
						iActiveExperiment = 0;
						if(STATE_RELEASE.equals( mAppDetails.get(DomainConstants.SELECT_CURRENT))) {
							throw new Exception(new StringBuilder(DELETE_MSG1).append(strInputExperimentName).append(DELETE_MSG3).toString()) ;
						}
						//if APP is connected to more than one experiment
						if(mAppDetails.get(strExpressionExpState) instanceof StringList) {
							StringList slSelectValuelist = (StringList) mAppDetails.get(strExpressionExpState);
							for(int iItr=0; iItr<slSelectValuelist.size(); iItr++ ) {
								if(STATE_ACTIVE.equals(slSelectValuelist.get(iItr))) {
									iActiveExperiment++;
								}
							}
							if(iActiveExperiment > 0) {
								jsonStatus.add(PGWidgetConstants.KEY_MESSAGE, DELETE_APP_FAILURE);
							} else {
								strAPPIds += (String)mAppDetails.get(DomainConstants.SELECT_ID) + PGWidgetConstants.KEY_COMMA_SEPARATOR;
							}
							
						} else {						
							strAPPIds += (String)mAppDetails.get(DomainConstants.SELECT_ID) + PGWidgetConstants.KEY_COMMA_SEPARATOR;
						}
					}
				    PGWidgetUtil.deleteObjects(context, strId);
					if (UIUtil.isNotNullAndNotEmpty(strAPPIds)) {
						strAPPIds = strAPPIds.substring(0, strAPPIds.length() - 1);
						PGWidgetUtil.deleteObjects(context, strAPPIds);		
					}
				}
			}
			else {
				PGWidgetUtil.deleteObjects(context, PGWidgetUtil.getStringFromJsonArray(jsonArrIds));
			}
			jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);	
			ContextUtil.commitTransaction(context);
		} catch (Exception e) {
			isExceptionOccurred = true;
			ContextUtil.abortTransaction(context);
			sbMessage.append(e.getMessage()).append(NEW_LINE);
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
		}
		return jsonStatus.build().toString();
	}
	
	/**
	 * Method to disconnect any relationship
	 * @param request
	 * @param jsonInputData
	 * @return String as json with status
	 * @throws Exception
	 */
	public static String disconnectObject(Context context, JsonObject jsonInputData) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		try {
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;
			JsonArray jsonArrIds = jsonInputData.getJsonArray(KEY_REL_IDS);
			String strExperimentId = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
			String strObjectSels = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_SELECTS);
			String strRelSelects = jsonInputData.getString(PGWidgetConstants.KEY_RELATIONSHIPSELECTS);
			PGWidgetUtil.removeSelected(context, PGWidgetUtil.getStringFromJsonArray(jsonArrIds));
			jsonStatus.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			ContextUtil.commitTransaction(context);
			String strJson = getExperimentContents(context, strExperimentId, strObjectSels, strRelSelects);
			JsonObject jsonObj = PGWidgetUtil.getJsonFromJsonString(strJson);
			jsonStatus.add(KEY_FOLDER_CONTENT, jsonObj.get(KEY_FOLDER_CONTENT));
		} catch (FrameworkException e) {
			if(isTransactionActive) {
				ContextUtil.abortTransaction(context);
				isExceptionOccurred = true;
				sbMessage.append(e.getMessage()).append("\n");
			}
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
		}
		return jsonStatus.build().toString();
	}

	
	/**
	 * Method to disconnect BOM from varaint
	 * @param request
	 * @param jsonInputData
	 * @return String as json with BOM structure
	 * @throws Exception 
	 */
	public static String removeBOM(Context context, JsonObject jsonInputData) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		try {		
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;
			JsonArray jsonArrIds = jsonInputData.getJsonArray(KEY_REL_IDS);
			PGWidgetUtil.removeSelected(context, PGWidgetUtil.getStringFromJsonArray(jsonArrIds));
			ContextUtil.commitTransaction(context);
		}catch (Exception e) {
			if(isTransactionActive) {
				ContextUtil.abortTransaction(context);
				isExceptionOccurred = true;
				sbMessage.append(e.getMessage()).append("\n");
			}
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
			return jsonStatus.build().toString();
		}
		String strExperimentId = jsonInputData.getString(DomainConstants.SELECT_ID);
		return getBOMWithVariants(context, strExperimentId);
	}
	
	/**
	 * Method to remove Characteristic / CenterLine object
	 * @param request
	 * @param jsonInputData
	 * @return String as json with extended structure
	 * @throws Exception 
	 */
	public static String removeCharOrCenterline(Context context, JsonObject jsonInputData) throws Exception {
		JsonObjectBuilder jsonStatus = Json.createObjectBuilder();
		boolean isTransactionActive = false;
		boolean isExceptionOccurred = false;
		StringBuilder sbMessage = new StringBuilder();
		try {		
			ContextUtil.startTransaction(context, true);
			isTransactionActive = true;
			JsonArray jsonArrIds = jsonInputData.getJsonArray(PGWidgetConstants.KEY_OBJECT_ID);
			PGWidgetUtil.deleteObjects(context, PGWidgetUtil.getStringFromJsonArray(jsonArrIds));
			ContextUtil.commitTransaction(context);

		}catch (Exception e) {
			if(isTransactionActive) {
				ContextUtil.abortTransaction(context);
				isExceptionOccurred = true;
				sbMessage.append(e.getMessage()).append("\n");
			}
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		if (isExceptionOccurred) {
			PGWidgetUtil.createErrorMessage(context, sbMessage, jsonStatus);
			return jsonStatus.build().toString();
		}
		String strType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
		String strExperimentId = jsonInputData.getString(DomainConstants.SELECT_ID);
		if(TYPE_CHARACTERISTIC.equals(strType)) {
			return getPerformanceChars(context, strExperimentId);	
		}else {
			return getCenterline(context, strExperimentId);		
		}
	}	

	/**
	 * Method to update object data
	 * @param request
	 * @param strInputData
	 * @return String as json with status
	 * @throws Exception
	 */
	public static String updateObject(Context context, String strInputData) throws FrameworkException {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		JsonObjectBuilder jsonReturn = Json.createObjectBuilder();
		String strAttrName = null;
		String strAttrValue = null;
		String strDescValue = DomainConstants.EMPTY_STRING;
		JsonObject jsonAttr = null;
		String strObjId = null;
		DomainObject domObj = null;
		Map<String, String> mpObjectInfo = new HashMap<>();
		for (Entry<?, ?> entry : jsonInputData.entrySet()) {
			jsonAttr = (JsonObject) entry.getValue();
			strObjId = (String) entry.getKey();
			mpObjectInfo = new HashMap<>();
			for (Entry<?, ?> attrEntry : jsonAttr.entrySet()) {
				strAttrName = (String) attrEntry.getKey();
				if (!DomainConstants.SELECT_DESCRIPTION.equals(strAttrName)) {
					strAttrValue = PGWidgetUtil.extractMultiValueSelect(jsonAttr, strAttrName);
					mpObjectInfo.put(PGWidgetUtil.getFormattedExpression(strAttrName), PGWidgetUtil.checkNullValueforString(strAttrValue));
				} else {
					strDescValue = PGWidgetUtil.extractMultiValueSelect(jsonAttr, strAttrName);
				}
			}
			
			try {
				domObj = DomainObject.newInstance(context, strObjId);
				domObj.setAttributeValues(context, mpObjectInfo);
				domObj.setDescription(context, strDescValue);
				jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			} catch (FrameworkException e) {
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
				jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
				jsonReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
				throw e;
			}
		}
		return jsonReturn.build().toString();
	}
	
	/**
	 * Method to update object data
	 * @param request
	 * @param strInputData
	 * @return String as json with status
	 * @throws Exception 
	 */	
	public static String updateExperiment(Context context, String strInputData) throws Exception {
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		JsonObject jsonUpdatedData = jsonInputData.getJsonObject(KEY_UPDATED_VALUES);
		JsonObjectBuilder jsonReturn = Json.createObjectBuilder();
		String strAttrName = "";
		String strAttrValue = "";
		String strDescription = "";
		boolean updateDescription = false;
		String strPlantId = DomainConstants.EMPTY_STRING;
		JsonObject jsonAttr;
		String strObjId;
		Map<String, String> mpObjectInfo;
		DomainObject domObj;
		for (Entry<?, ?> entry : jsonUpdatedData.entrySet()) {
			jsonAttr = (JsonObject) entry.getValue();
			strObjId = (String) entry.getKey();
			mpObjectInfo = new HashMap<>();
			for (Entry<?, ?> attrEntry : jsonAttr.entrySet()) {
				strAttrName = (String) attrEntry.getKey();
				if(DomainConstants.SELECT_DESCRIPTION.equals(strAttrName))
				{
					strDescription = PGWidgetUtil.extractMultiValueSelect(jsonAttr, strAttrName);
					updateDescription = true;
				} 
				else if(strAttrName.contains((new StringBuilder()).append(SELECTABLE_TO).append(DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY).append(SELECTABLE_FROM_NAME))){
					strPlantId = PGWidgetUtil.extractMultiValueSelect(jsonAttr, strAttrName);
				}
				else {
					strAttrValue = PGWidgetUtil.extractMultiValueSelect(jsonAttr, strAttrName);
					mpObjectInfo.put(PGWidgetUtil.getFormattedExpression(strAttrName), PGWidgetUtil.checkNullValueforString(strAttrValue));
				}
			}
			try {
				domObj = DomainObject.newInstance(context, strObjId);
				domObj.setAttributeValues(context, mpObjectInfo);
				if(updateDescription)
					domObj.setDescription(context, strDescription);
				if(UIUtil.isNotNullAndNotEmpty(strPlantId)){
					connectRelatedObjects(context, domObj, DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY,strPlantId);
				}
				jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
			} catch (Exception e) {
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
				jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
				jsonReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
				throw e;
			}
		}
		return getAllExperiments(context, strInputData);
	}	

	/**
	 * Method to update EBOM attributes
	 * @param request
	 * @param strInputData
	 * @return String as json with status
	 * @throws Exception
	 */
	public static String updateData(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonReturn = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			JsonArray updateValuesArray = jsonInputData.getJsonArray(KEY_UPDATED_VALUES);
			JsonArray newValuesArray = jsonInputData.getJsonArray(KEY_NEW_DATA);
			String strExperimentId = jsonInputData.getString(DomainConstants.SELECT_ID);

			for (int i = 0; i < updateValuesArray.size(); i++) {
				JsonArray jsonData = updateValuesArray.getJsonArray(i);
				addNewBOMorUpdateBOM(context, jsonData);
			}
			for (int i = 0; i < newValuesArray.size(); i++) {
				JsonArray jsonData = newValuesArray.getJsonArray(i);
				addNewBOMorUpdateBOM(context, jsonData);
			}
			return getBOMWithVariants(context, strExperimentId);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			throw e;
		}
	}

	/**
	 * Method to update EBOM attributes for existing data dn create new BOM for new set of data
	 * @param request
	 * @param strInputData
	 * @return String as json with status
	 * @throws Exception
	 */
	public static void addNewBOMorUpdateBOM(Context context, JsonArray jsonArrData) throws Exception {
		String strFromObjId = DomainConstants.EMPTY_STRING;
		String strToObjId = DomainConstants.EMPTY_STRING;
		String strRelId = DomainConstants.EMPTY_STRING;
		String strAttribName = null;
		Map<String, Object> mapAttrib = new HashMap<>();
		Map<String, Object> mapAttribPart = new HashMap<>();
		JsonObject jsonObj = null;
		String strNewValue = null;
		boolean isDeleteCase = false;
		for (int i = 0; i < jsonArrData.size(); i++) {
			jsonObj = jsonArrData.getJsonObject(i);
			strAttribName = PGWidgetUtil.getFormattedExpression(jsonObj.getString(KEY_FIELD));
			strNewValue = jsonObj.getString(KEY_NEW_VALUE);
			if(jsonObj.containsKey(KEY_REL_ID)) {
				strRelId = jsonObj.getString(KEY_REL_ID);
			}
			if(DomainConstants.ATTRIBUTE_QUANTITY.equals(strAttribName) && UIUtil.isNullOrEmpty(strNewValue) && UIUtil.isNotNullAndNotEmpty(strRelId)) {
				PGWidgetUtil.removeSelected(context, strRelId);
				isDeleteCase = true;
			}else {
				if (!ATTR_PG_BASEUOM.equals(strAttribName)) {
					mapAttrib.put(strAttribName, jsonObj.getString(KEY_NEW_VALUE));
				} else {
					mapAttribPart.put(strAttribName, jsonObj.getString(KEY_NEW_VALUE));
				}
				if (jsonObj.containsKey(KEY_COL_ID)) {
					strFromObjId = jsonObj.getString(KEY_COL_ID);
				}
				if (UIUtil.isNullOrEmpty(strRelId)) {
					strFromObjId = jsonObj.getString(KEY_COL_ID);
					strToObjId = jsonObj.getString(KEY_ROW_ID);
				}
			}
		}
		if(!isDeleteCase) {
			DomainObject domCol = null;
			if (UIUtil.isNotNullAndNotEmpty(strFromObjId) && UIUtil.isNotNullAndNotEmpty(strToObjId)) {
				domCol = DomainObject.newInstance(context, strFromObjId);
				StringList slFindNum = domCol.getInfoList(context, "from["+DomainConstants.RELATIONSHIP_EBOM+"].attribute["+DomainConstants.ATTRIBUTE_FIND_NUMBER+"]");
				ArrayList<Integer> arrIntFN = new ArrayList<Integer>();
				for(int ki=0 ; ki<slFindNum.size() ; ki++) {
					if(UIUtil.isNotNullAndNotEmpty(slFindNum.get(ki))) {
						arrIntFN.add(Integer.parseInt(slFindNum.get(ki)));
					}
				}
				int iFindNum = 1;
				if(!arrIntFN.isEmpty()) {
					Collections.sort(arrIntFN);	
					iFindNum = arrIntFN.get(arrIntFN.size()-1) + 1;
				}
				mapAttrib.put(DomainConstants.ATTRIBUTE_FIND_NUMBER, Integer.toString(iFindNum));
				Map<?,?> map = PGWidgetUtil.addExisting(context, strFromObjId, strToObjId, DomainConstants.RELATIONSHIP_EBOM, false);
				strRelId = map.get(strToObjId).toString();
			}
			try {
				if (UIUtil.isNotNullAndNotEmpty(strRelId)) {
					DomainRelationship.setAttributeValues(context, strRelId, mapAttrib);
				}
				if (domCol != null && !mapAttribPart.isEmpty()) {
					domCol.setAttributeValues(context, mapAttribPart);
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
				throw e;
			}
		}
	}
	

	/**
	 * Get Details of Performance Chars of Cloned APP
	 * @param context
	 * @param strInputData
	 * @return String as json
	 * @throws Exception
	 */
	public static String getPerformanceChars(Context context, String objectId) throws Exception {
		Pattern relPat = new Pattern(RELATIONSHIP_PGVARIANTS);
		relPat.addPattern(REL_EXTENDED_DATA);

		String objSelectablesList = (new StringBuilder()).append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_CHARACTERISTIC).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_LOWERSPECIFICATIONLIMIT).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_TARGET).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_UPPERSPECIFICATIONLIMIT).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTRIBUTE_PGUOM).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(SELECTABLE_TO).append(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT).append(RIGHT_BRACKET).append(SELECTABLE_FROM_PATH).append(TYPE_TEST_METHOD_SPECIFICATION).append(SELECTABLE_REL_NAME).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(SELECTABLE_TO).append(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT).append(RIGHT_BRACKET).append(SELECTABLE_FROM_PATH).append(TYPE_TEST_METHOD_SPECIFICATION).append(SELECTABLE_REL_ID)
				.toString();

		String relSelects = (new StringBuilder()).append(PGWidgetConstants.SELECT_CONNECTION_ID).toString();
		Pattern typePat = new Pattern(TYPE_ASSEMBLED_PRODUCT_PART);
		typePat.addPattern(TYPE_CHARACTERISTIC);

		StringList slKeysHierarchy = new StringList();
		slKeysHierarchy.add(KEY_2_ATTRIBUTE_CHARACTERISTIC);
		slKeysHierarchy.add(KEY_LOWER_SPEC_LIMIT);
		slKeysHierarchy.add(KEY_UPPER_SPEC_LIMIT);
		slKeysHierarchy.add(KEY_UNIT_OF_MEASURE);
		slKeysHierarchy.add(KEY_TEST_METHOD);

		Map<String, String> mapping = new HashMap<>();
		mapping.put(KEY_2_ATTRIBUTE_CHARACTERISTIC, KEY_2_ATTRIBUTE_TARGET);
		mapping.put(KEY_LOWER_SPEC_LIMIT, KEY_2_ATTRIBUTE_LOWER_SPECIFICATION_LIMIT);
		mapping.put(KEY_UPPER_SPEC_LIMIT, KEY_2_ATTRIBUTE_UPPER_SPECIFICATION_LIMIT);
		mapping.put(KEY_UNIT_OF_MEASURE, KEY_2_ATTRIBUTE_UNIT_OF_MEASURE);
		mapping.put(KEY_TEST_METHOD, KEY_2_TEST_METHOD_NAME);
		return getExtendedDataDetails(context, objectId, objSelectablesList, relSelects, typePat.getPattern(), relPat.getPattern(), slKeysHierarchy, mapping);
	}	

	/**
	 * Get Details of Centerlines of Cloned APP
	 * @param context
	 * @param strInputData
	 * @return String as json
	 * @throws Exception
	 */
	public static String getCenterline(Context context, String strVarID) throws Exception {
		Pattern relPat = new Pattern(RELATIONSHIP_PGVARIANTS);
		relPat.addPattern(RELATIONSHIP_PGCENTERLINE);

		String objSelectablesList = (new StringBuilder()).append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_LOWER_TARGET).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_TARGET).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_UPPER_TARGET).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_COMMENTS).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTRIBUTE_PGUOM).append(RIGHT_BRACKET).append(PGWidgetConstants.KEY_COMMA_SEPARATOR).toString();

		String relSelects = (new StringBuilder()).append(PGWidgetConstants.SELECT_CONNECTION_ID).toString();
		Pattern typePat = new Pattern(TYPE_ASSEMBLED_PRODUCT_PART);
		typePat.addPattern(TYPE_CENTERLINE);

		StringList slKeysHierarchy = new StringList();
		slKeysHierarchy.add(KEY_2_TITLE);
		slKeysHierarchy.add(KEY_MIN);
		slKeysHierarchy.add(KEY_MAX);
		slKeysHierarchy.add(KEY_UNIT_OF_MEASURE);
		slKeysHierarchy.add(KEY_COMMENTS);

		Map<String, String> mapping = new HashMap<>();
		mapping.put(KEY_2_TITLE, KEY_2_ATTRIBUTE_TARGET);
		mapping.put(KEY_MIN, KEY_2_ATTRIBUTE_LOWER_TARGET);
		mapping.put(KEY_MAX, KEY_2_ATTRIBUTE_UPPER_TARGET);
		mapping.put(KEY_UNIT_OF_MEASURE, KEY_2_ATTRIBUTE_UNIT_OF_MEASURE);
		mapping.put(KEY_COMMENTS, KEY_2_COMMENTS);

		return getExtendedDataDetails(context, strVarID, objSelectablesList, relSelects, typePat.getPattern(), relPat.getPattern(), slKeysHierarchy, mapping);
	}

	/**
	 * Get related data of APP 
	 * @param context
	 * @param strInputData
	 * @return String as json
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String getExtendedDataDetails(Context context, String strVarID,String objSelectablesList, String relSelects,String  strType, String strRelName, StringList slKeys, Map<String, String> mapping) throws Exception  {
		JsonObjectBuilder output = Json.createObjectBuilder();
		StringList slObjSelectables = new StringList();
		StringList slMultivalueSelectables = new StringList();

		for (String objSelectable : StringUtil.split(objSelectablesList, PGWidgetConstants.KEY_COMMA_SEPARATOR)) {
			if (objSelectable.contains(PGWidgetConstants.STR_FROM_OPEN) || objSelectable.contains(PGWidgetConstants.STR_ATTRIBUTE_OPEN)) {
				DomainConstants.MULTI_VALUE_LIST.add(objSelectable);
				slMultivalueSelectables.add(objSelectable);
			}
			slObjSelectables.add(objSelectable);
		}
		JsonObjectBuilder inputData = Json.createObjectBuilder();
		try {
			inputData.add(PGWidgetConstants.KEY_OBJECT_ID, strVarID);
			inputData.add(PGWidgetConstants.KEY_RELPATTERN, strRelName);
			inputData.add(PGWidgetConstants.TYPE_PATTERN, strType);
			inputData.add(PGWidgetConstants.KEY_EXPANDLEVEL, "2");
			inputData.add(PGWidgetConstants.KEY_WHERECONDITION, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.KEY_GETTO, PGWidgetConstants.STRING_FALSE);
			inputData.add(PGWidgetConstants.KEY_GETFROM, PGWidgetConstants.STRING_TRUE);
			inputData.add(PGWidgetConstants.KEY_LIMIT, CONST_STRING_ZERO);
			inputData.add(PGWidgetConstants.KEY_RELWHERECONDITION, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.KEY_RELATIONSHIPSELECTS, relSelects);
			inputData.add(PGWidgetConstants.KEY_OBJECT_SELECTS, objSelectablesList);

			MapList mlObjectList = PGWidgetUtil.getRelatedObjectsMapList(context, inputData.build());

			if (!slMultivalueSelectables.isEmpty()) {
				for (String mlSelectable : slMultivalueSelectables) {
					DomainConstants.MULTI_VALUE_LIST.remove(mlSelectable);
				}
			}
			if (strType.indexOf(TYPE_CHARACTERISTIC) != -1) {
				List<JsonObjectBuilder> arrListFlatTable = convertMapListToJsonFlatTable(mlObjectList, 0);
				output.add(KEY_RESULT, formatExtendedResponseChar(arrListFlatTable, slKeys, mapping));
			} else if (strType.indexOf(TYPE_CENTERLINE) != -1) {
				JsonArray jsonArrBOM = PGWidgetUtil.convertMapListToJsonFlatTable(mlObjectList, 0);
				output.add(KEY_RESULT, formatExtendedResponseCenterline(jsonArrBOM, slKeys, mapping));
			} else {
				List<JsonObjectBuilder> arrListFlatTable = convertMapListToJsonFlatTable(mlObjectList, 0);
				output.add(KEY_BOM, formatExtendedResponseBOM(context, arrListFlatTable, slKeys, mapping));
			}
			
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return output.build().toString();
	}
	
	/**
	 * Method to convert maplist to json format flat table
	 * @param mlInput
	 * @param iLevel
	 * @return list of jsonobject
	 * @throws NumberFormatException
	 */
	public static List<JsonObjectBuilder> convertMapListToJsonFlatTable (MapList mlInput, int iLevel) throws NumberFormatException {
		Stack<JsonObjectBuilder> stackJsonObj = new Stack<>();
		List<JsonObjectBuilder> arrListJson = new ArrayList<>();
		int nLevelCurrent = 1;
		int nLevelPrev = 1;
		int nLevelAppend = 0;
		Map<?, ?> tempMap;
		String strLevel;
		for (Iterator<?> iterator = mlInput.iterator(); iterator.hasNext();) {
			tempMap = (Map<?, ?>) iterator.next();
			strLevel = (String) tempMap.get(DomainConstants.SELECT_LEVEL);
			nLevelCurrent = Integer.parseInt(strLevel);
			nLevelAppend = nLevelCurrent + iLevel;
			//Leaf level node condition
			if(nLevelCurrent <= nLevelPrev) {
				if(!stackJsonObj.empty()) {
					arrListJson.add(stackJsonObj.pop());	
				}
				for (int i = nLevelCurrent; i < nLevelPrev; i++) {
					stackJsonObj.pop();
				}
			}
			stackJsonObj.push(convertMapToJsonObj(stackJsonObj.empty() ? Json.createObjectBuilder() : stackJsonObj.peek(), tempMap, Integer.toString(nLevelAppend)+PGWidgetConstants.KEY_SELECTS_SEPARATOR));
			nLevelPrev = nLevelCurrent;
		}
		if(!stackJsonObj.empty()) {
			arrListJson.add(stackJsonObj.pop());	
		}
		return arrListJson;
	}
	
	/**
	 * Method to convert map to json object
	 * @param inputJsonObj
	 * @param inputMap
	 * @param strLevel
	 * @return json object
	 */
	public static JsonObjectBuilder convertMapToJsonObj(JsonObjectBuilder inputJsonObj, Map<?, ?> inputMap, String strLevel) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		inputJsonObj.build().entrySet().forEach(e -> builder.add(e.getKey(), e.getValue()));
		inputMap.entrySet().forEach(e -> builder.add(strLevel+(String)e.getKey(), ((e.getValue() instanceof StringList) ? PGWidgetUtil.getStringFromSL((StringList) e.getValue(), PGWidgetConstants.KEY_PIPE_SEPARATOR) : (String)e.getValue())));
		return builder;
	}
	
	/**
	 * Method to format JsonArray in the required format so display in UI 
	 * @param jsonArr
	 * @param slKeys
	 * @param mapping
	 * @return String in json format with all content objects in array
	 */
	static JsonArray formatExtendedResponseCenterline(JsonArray jsonArr, StringList slKeys,  Map<String, String> mapping) {
		JsonArrayBuilder jsonArrCenterline = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrHier = null;
		String sreNameRevision = null;
		Set<String> set = new HashSet<>();
		JsonObject hierarchyObj = null;
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonObjectBuilder jsonObjKeyVariantExtra = null;
		JsonObject variant = null;
		String strField = null;
		String strUOM = DomainConstants.EMPTY_STRING; 
		for (int index = 0; index < slKeys.size(); index++) {
			for (int i = 0; i < jsonArr.size(); i++) {
				hierarchyObj = jsonArr.getJsonObject(i);
				jsonObjKeyVariant = Json.createObjectBuilder();
				jsonObjKeyVariantExtra = Json.createObjectBuilder();
				if(hierarchyObj.containsKey(KEY_2_ATTRIBUTE_UNIT_OF_MEASURE)) {
					strUOM = hierarchyObj.getString(KEY_2_ATTRIBUTE_UNIT_OF_MEASURE);
				}
				for (int j = 0; j < jsonArr.size(); j++) {
					variant = jsonArr.getJsonObject(j);
					sreNameRevision = variant.getString(KEY_1_NAME) + "_" + variant.getString(KEY_1_REVISION);
					if (variant.containsKey(KEY_2_NAME) && hierarchyObj.containsKey(KEY_2_NAME)) {
						if ((hierarchyObj.getString(KEY_2_TITLE).equals(variant.getString(KEY_2_TITLE)))) {
							try {
								jsonObjKeyVariant.add(sreNameRevision, variant.getString(mapping.get(slKeys.get(index))));
								jsonObjKeyVariantExtra.add(sreNameRevision,
										Json.createObjectBuilder().add(KEY_REL_ID, variant.getString(KEY_2_ID_CONNECTION))
										.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_1_TYPE))
										.add(KEY_COL_ID, variant.getString(KEY_1_ID))
										.add(KEY_ROW_ID, variant.getString(KEY_2_ID)).build());
							} catch (Exception e) {
								jsonObjKeyVariant.add(sreNameRevision, DomainConstants.EMPTY_STRING);
								jsonObjKeyVariantExtra.add(sreNameRevision, 
										Json.createObjectBuilder().add(KEY_REL_ID, DomainConstants.EMPTY_STRING)
										.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_1_TYPE))
										.add(KEY_COL_ID, variant.getString(KEY_1_ID))
										.add(KEY_ROW_ID, variant.getString(KEY_2_ID)).build());
							}
						} else if (!jsonObjKeyVariant.build().containsKey(sreNameRevision)) {
							jsonObjKeyVariant.add(sreNameRevision, DomainConstants.EMPTY_STRING);
						}
					} else {
						jsonObjKeyVariant.add(sreNameRevision, DomainConstants.EMPTY_STRING);
					}
					if (!jsonObjKeyVariantExtra.build().containsKey(sreNameRevision)) {
						jsonObjKeyVariantExtra.add(sreNameRevision,
								Json.createObjectBuilder().add(KEY_REL_ID, DomainConstants.EMPTY_STRING)
								.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_1_TYPE))
								.add(KEY_COL_ID, variant.getString(KEY_1_ID))
								.add(KEY_ROW_ID, DomainConstants.EMPTY_STRING).build());
					}
					if(KEY_UNIT_OF_MEASURE.equals(slKeys.get(index))) {
						jsonObjKeyVariant.add(sreNameRevision, strUOM);
					}
				}
				if (hierarchyObj.containsKey(KEY_2_NAME)) {
					jsonArrHier = Json.createArrayBuilder();
					if (index == 0) {
						jsonArrHier.add(hierarchyObj.getString(slKeys.get(index)));
						jsonObjKeyVariant.add(DomainConstants.ATTRIBUTE_TITLE, hierarchyObj.getString(KEY_2_TITLE));
						jsonObjKeyVariant.add(KEY_ROW_IDENTIFIER, hierarchyObj.getString(slKeys.get(0)));
					} else {
						jsonArrHier.add(hierarchyObj.getString(slKeys.get(0)))
						.add((slKeys.get(index)));
						jsonObjKeyVariant.add(KEY_ROW_IDENTIFIER, (slKeys.get(index)));
					}
					jsonObjKeyVariant.add(KEY_TABLE_NAME, KEY_CENTER_LINE);
					jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
					jsonObjKeyVariant.add(DomainConstants.SELECT_ID, hierarchyObj.getString(KEY_2_ID));
					jsonObjKeyVariant.add(KEY_EXTRA_INFO, jsonObjKeyVariantExtra.build());
					strField = mapping.get(slKeys.get(index));
					if(UIUtil.isNotNullAndNotEmpty(strField)) {
						jsonObjKeyVariant.add(KEY_FIELD, StringUtil.split(strField, PGWidgetConstants.KEY_SELECTS_SEPARATOR).get(1));
					}
					if(!set.contains(PGWidgetUtil.getStringFromJsonArray(jsonArrHier.build()))) {
						jsonArrCenterline.add(jsonObjKeyVariant.build());
						set.add(PGWidgetUtil.getStringFromJsonArray(jsonArrHier.build()));	
					}
				}
			}
		}
		return jsonArrCenterline.build();
	}
	
	/**
	 * Method to format JsonArray in the required format so display in UI 
	 * @param arrListExpanded
	 * @param slKeys
	 * @param mapping
	 * @return String in json format with all content objects in array
	 */
	static JsonArray formatExtendedResponseChar(List<JsonObjectBuilder> arrListExpanded, StringList slKeys,  Map<String, String> mapping) {
		JsonArrayBuilder jsonArrChar = Json.createArrayBuilder();
		List<JsonObjectBuilder> jsonArrDuplicates = null;
		Set<String> set = null;
		for (int index = 0; index < slKeys.size(); index++) {
			set = new HashSet<>();
			jsonArrDuplicates = buildCharVariantObject(arrListExpanded, mapping, slKeys, index, jsonArrChar, set);
			while (!jsonArrDuplicates.isEmpty()) {
				jsonArrDuplicates = buildCharVariantObject(jsonArrDuplicates, mapping, slKeys, index, jsonArrChar, set);
			}
		}
		return jsonArrChar.build();
	}

	static JsonArray formatExtendedResponseBOM(Context context, List<JsonObjectBuilder> arrListExpanded, StringList slKeys,  Map<String, String> mapping) throws Exception {
		JsonArrayBuilder jsonArrChar = Json.createArrayBuilder();
		List<JsonObjectBuilder> jsonArrDuplicates = null;
		Set<String> set = null;
		for (int index = 0; index < slKeys.size(); index++) {
			set = new HashSet<>();
			jsonArrDuplicates = buildBOMVariantObject(context, arrListExpanded, mapping, slKeys, index, jsonArrChar, set);
			while (!jsonArrDuplicates.isEmpty()) {
				jsonArrDuplicates = buildBOMVariantObject(context, jsonArrDuplicates, mapping, slKeys, index, jsonArrChar, set);
			}
		}
		return jsonArrChar.build();
	}
	/**
	 * Build Characteristic json object
	 * @param arrListExpanded
	 * @param mapping
	 * @param slKeys
	 * @param index
	 * @param jsonArrChar
	 * @param set
	 * @return list of jsonobject
	 */
	private static List<JsonObjectBuilder> buildCharVariantObject(List<JsonObjectBuilder> arrListExpanded,  Map<String, String> mapping, StringList slKeys, int index, JsonArrayBuilder jsonArrChar, Set<String> set){
		String strNameRevision = null;
		String strCharKeyVarId = null;
		String strFirstColumnValue = null;
		String strUniqueKey = null;
		String strKey = null;
		String constKeyCharRow = "keyCharacteristicRow";
		JsonArrayBuilder jsonArrHier = null;
		JsonObject hierarchyObj = null;
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonObjectBuilder jsonObjKeyVariantExtra = null;
		JsonObjectBuilder variantBuilder = null;
		JsonObject variant = null;
		Map<String, Integer> mapCharVarPairCount = null;
		List<JsonObjectBuilder> arrListDuplicates = new ArrayList<>();
		StringBuilder sCharKeyVarId = null;
		StringBuilder sUniqueKey = null;
		List<JsonObjectBuilder> arrExpanded = new ArrayList<>();
		arrExpanded.addAll(arrListExpanded);
		int countDuplicate = 0;
		ArrayList<JsonObject> arrJsonObjTemp = new ArrayList<>();
		try {
			for (int i = 0; i < arrExpanded.size(); i++) {
				hierarchyObj = arrExpanded.get(i).build();
				jsonObjKeyVariant = Json.createObjectBuilder();
				jsonObjKeyVariantExtra = Json.createObjectBuilder();
				mapCharVarPairCount = new HashMap<>();
				for(int j=0 ; j < arrExpanded.size() ; j++) {
					variantBuilder = arrExpanded.get(j);
					variant = variantBuilder.build();
					strNameRevision = variant.getString(KEY_1_NAME) + "_" + variant.getString(KEY_1_REVISION);
					if (variant.containsKey(KEY_2_NAME) && hierarchyObj.containsKey(KEY_2_NAME)) {
						strFirstColumnValue = hierarchyObj.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC);
						if (hierarchyObj.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC).equals(variant.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC)) &&
								hierarchyObj.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC_SPECIFIC).equals(variant.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC_SPECIFIC))) {
							
							sCharKeyVarId = new StringBuilder();
							strCharKeyVarId = sCharKeyVarId.append(variant.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).
							append(variant.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC_SPECIFIC)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).append(variant.getString(KEY_1_ID)).toString();		
							
							sUniqueKey = new StringBuilder();
							strUniqueKey = sUniqueKey.append(variant.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).
									append(variant.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC_SPECIFIC)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).append(countDuplicate).toString();
							if(mapCharVarPairCount.containsKey(strCharKeyVarId)) {
								arrExpanded.remove(j);
								j--;
								countDuplicate = mapCharVarPairCount.get(strCharKeyVarId) + 1;
								mapCharVarPairCount.put(strCharKeyVarId, countDuplicate);
								if(!variantBuilder.build().containsKey(constKeyCharRow)) variantBuilder.add(constKeyCharRow, strUniqueKey);
								arrListDuplicates.add(variantBuilder);								
							}else {
								mapCharVarPairCount.put(strCharKeyVarId, 0);
								buildCharJSON(mapping, slKeys, index, strNameRevision, jsonObjKeyVariant,
										jsonObjKeyVariantExtra, variant);
							}
						}
					}
					if (!jsonObjKeyVariantExtra.build().containsKey(strNameRevision)) {
						jsonObjKeyVariantExtra.add(strNameRevision,
								Json.createObjectBuilder().add(KEY_REL_ID, DomainConstants.EMPTY_STRING)
								.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_1_TYPE))
								.add(DomainConstants.SELECT_CURRENT, variant.getString(KEY_1_CURRENT))
								.add(KEY_COL_ID, variant.getString(KEY_1_ID))
								.add(KEY_ROW_ID, DomainConstants.EMPTY_STRING).build());
					}
				}
				if (hierarchyObj.containsKey(KEY_2_NAME)) {
					jsonArrHier = Json.createArrayBuilder();
					strUniqueKey = hierarchyObj.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC) + PGWidgetConstants.KEY_PIPE_SEPARATOR + hierarchyObj.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC_SPECIFIC);
					strKey = hierarchyObj.containsKey(constKeyCharRow) ? hierarchyObj.getString(constKeyCharRow) : strUniqueKey;
					if(!set.contains(strKey)) {
						if (index == 0) {
							jsonArrHier.add(strKey);
							jsonObjKeyVariant.add(DomainConstants.ATTRIBUTE_TITLE, hierarchyObj.getString(KEY_2_ATTRIBUTE_CHARACTERISTIC_SPECIFIC));
							jsonObjKeyVariant.add(KEY_ROW_IDENTIFIER, strFirstColumnValue);
						} else {
							jsonArrHier.add(strKey)
							.add((slKeys.get(index)));
							jsonObjKeyVariant.add(KEY_ROW_IDENTIFIER, (slKeys.get(index)));
						}
						jsonObjKeyVariant.add(KEY_TABLE_NAME, KEY_CHARACTERISTIC);
						
						jsonObjKeyVariant.add(KEY_EXTRA_INFO, jsonObjKeyVariantExtra.build());
						String strField = mapping.get(slKeys.get(index));
						if(KEY_TEST_METHOD.equals(slKeys.get(index))) {
							jsonObjKeyVariant.add(KEY_FIELD, VALUE_TEST_METHOD);
						}
						else if(UIUtil.isNotNullAndNotEmpty(strField)) {
							jsonObjKeyVariant.add(KEY_FIELD, StringUtil.split(strField, PGWidgetConstants.KEY_SELECTS_SEPARATOR).get(1));
						}

						//strKey = hierarchyObj.containsKey(constKeyCharRow) ? hierarchyObj.getString(constKeyCharRow) : strUniqueKey;

						if(!arrJsonObjTemp.contains(jsonObjKeyVariant.build())) {
							arrJsonObjTemp.add(jsonObjKeyVariant.build());
							
							jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
							jsonArrChar.add(jsonObjKeyVariant.build());
							set.add(strKey);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;		
		}
		return arrListDuplicates;
	}

	private static List<JsonObjectBuilder> buildBOMVariantObject(Context context, List<JsonObjectBuilder> arrListExpanded,  Map<String, String> mapping, StringList slKeys, int index, JsonArrayBuilder jsonArrChar, Set<String> set) throws Exception{
		String strNameRevision = null;
		String strCharKeyVarId = null;
		String strFirstColumnValue = null;
		String strUniqueKey = null;
		String strKey = null;
		String constKeyCharRow = "keyBOMRow";
		JsonArrayBuilder jsonArrHier = null;
		JsonObject hierarchyObj = null;
		JsonObjectBuilder jsonObjKeyVariant = null;
		JsonObjectBuilder jsonObjKeyVariantExtra = null;
		JsonObjectBuilder variantBuilder = null;
		JsonObject variant = null;
		Map<String, Integer> mapCharVarPairCount = null;
		List<JsonObjectBuilder> arrListDuplicates = new ArrayList<>();
		StringBuilder sCharKeyVarId = null;
		StringBuilder sUniqueKey = null;
		List<JsonObjectBuilder> arrExpanded = new ArrayList<>();
		arrExpanded.addAll(arrListExpanded);
		int countDuplicate = 0;
		ArrayList<JsonObject> arrJsonObjTemp = new ArrayList<>();
		String strSelectReportFunction = new StringBuilder().append("2-").append(SELECTABLE_FROM).append(RELATIONSHIP_TEMPLATE_TO_REPORT_FUNCTION)
				.append(SELECTABLE_FROMTO).append(DomainConstants.SELECT_NAME).toString();
		try {
			for (int i = 0; i < arrExpanded.size(); i++) {
				hierarchyObj = arrExpanded.get(i).build();
				jsonObjKeyVariant = Json.createObjectBuilder();
				jsonObjKeyVariantExtra = Json.createObjectBuilder();
				mapCharVarPairCount = new HashMap<>();
				for(int j=0 ; j < arrExpanded.size() ; j++) {
					variantBuilder = arrExpanded.get(j);
					variant = variantBuilder.build();
					strNameRevision = variant.getString(KEY_1_NAME) + "_" + variant.getString(KEY_1_REVISION);
					if (variant.containsKey(KEY_2_NAME) && hierarchyObj.containsKey(KEY_2_NAME)) {
						strFirstColumnValue = hierarchyObj.getString(KEY_2_NAME) +"."+ hierarchyObj.getString(KEY_2_REVISION);
						if (hierarchyObj.getString(KEY_2_NAME).equals(variant.getString(KEY_2_NAME)) &&
								hierarchyObj.getString(KEY_2_REVISION).equals(variant.getString(KEY_2_REVISION))) {
							sCharKeyVarId = new StringBuilder();
							strCharKeyVarId = sCharKeyVarId.append(variant.getString(KEY_2_NAME)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).
									append(variant.getString(KEY_2_REVISION)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).append(variant.getString(KEY_1_ID)).toString();		
							sUniqueKey = new StringBuilder();
							strUniqueKey = sUniqueKey.append(variant.getString(KEY_2_NAME)).append(".").append(variant.getString(KEY_2_REVISION)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).
									append(variant.getString(KEY_2_REVISION)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).append(countDuplicate).toString();
							if(mapCharVarPairCount.containsKey(strCharKeyVarId)) {
								arrExpanded.remove(j);
								j--;
								countDuplicate = mapCharVarPairCount.get(strCharKeyVarId) + 1;
								mapCharVarPairCount.put(strCharKeyVarId, countDuplicate);
								if(!variantBuilder.build().containsKey(constKeyCharRow)) variantBuilder.add(constKeyCharRow, strUniqueKey);
								arrListDuplicates.add(variantBuilder);
							}else {
								mapCharVarPairCount.put(strCharKeyVarId, 0);
								buildCharJSON(mapping, slKeys, index, strNameRevision, jsonObjKeyVariant,
										jsonObjKeyVariantExtra, variant);
								if(variant.containsKey(strSelectReportFunction)) {
									jsonObjKeyVariant.add(ATTRIBUTE_PG_REPORTED_FUNCTION, variant.getString(strSelectReportFunction));
								}
								//Defect: Defect ID : 49517
								if(variant.containsKey(KEY_2_BASE_UNIT_OF_MEASURE)) {
									jsonObjKeyVariant.add(ATTR_PG_BASEUOM, variant.getString(KEY_2_BASE_UNIT_OF_MEASURE));
								}
							}
						}
					}
					if (!jsonObjKeyVariantExtra.build().containsKey(strNameRevision)) {
						jsonObjKeyVariantExtra.add(strNameRevision,
								Json.createObjectBuilder().add(KEY_REL_ID, DomainConstants.EMPTY_STRING)
								.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_1_TYPE))
								.add(DomainConstants.SELECT_CURRENT, variant.getString(KEY_1_CURRENT))
								.add(KEY_COL_ID, variant.getString(KEY_1_ID))
								.add(KEY_ROW_ID, DomainConstants.EMPTY_STRING).build());
					}
				}
				if (hierarchyObj.containsKey(KEY_2_NAME)) {
					jsonArrHier = Json.createArrayBuilder();
					strUniqueKey = hierarchyObj.getString(KEY_2_NAME) + "."+ hierarchyObj.getString(KEY_2_REVISION) + PGWidgetConstants.KEY_PIPE_SEPARATOR + hierarchyObj.getString(KEY_2_REVISION);
					strKey = hierarchyObj.containsKey(constKeyCharRow) ? hierarchyObj.getString(constKeyCharRow) : strUniqueKey;
					if(!set.contains(strKey)) {
						if (index == 0) {
							jsonArrHier.add(strKey);
							jsonObjKeyVariant.add(DomainConstants.ATTRIBUTE_TITLE, hierarchyObj.getString(KEY_2_TITLE));
							jsonObjKeyVariant.add(KEY_ROW_IDENTIFIER, strFirstColumnValue);
							jsonObjKeyVariant.add(PGWidgetConstants.KEY_DISPLAY_TYPE, i18nNow.getTypeI18NString(hierarchyObj.getString(KEY_2_TYPE), context.getLocale().getLanguage()));
							jsonObjKeyVariant.add(PGWidgetConstants.KEY_DISPLAY_NAME, 
									UIUtil.isNotNullAndNotEmpty(hierarchyObj.getString(KEY_2_TITLE)) ? hierarchyObj.getString(KEY_2_TITLE) : hierarchyObj.getString(KEY_2_NAME));
							jsonObjKeyVariant.add(PGWidgetConstants.SELECT_PHYSICAL_ID, hierarchyObj.getString(KEY_2_PHYSICAL_ID));
							jsonObjKeyVariant.add(DomainConstants.SELECT_CURRENT, hierarchyObj.getString(KEY_2_CURRENT));
							jsonObjKeyVariant.add(DomainConstants.SELECT_NAME, hierarchyObj.getString(KEY_2_NAME));
							jsonObjKeyVariant.add(DomainConstants.SELECT_REVISION, hierarchyObj.getString(KEY_2_REVISION));
							jsonObjKeyVariant.add(DomainConstants.SELECT_ORIGINATED, hierarchyObj.getString(KEY_2_ORIGINATED));
							jsonObjKeyVariant.add(DomainConstants.SELECT_ORIGINATOR, hierarchyObj.getString(KEY_2_ORIGINATOR));
							jsonObjKeyVariant.add(DomainObject.getAttributeSelect(FormulationAttribute.STAGE.getAttribute(context)), hierarchyObj.getString(KEY_2_RELEASE_PHASE));
						} else {
							jsonArrHier.add(strKey)
							.add((slKeys.get(index)));
							jsonObjKeyVariant.add(KEY_ROW_IDENTIFIER, (slKeys.get(index)));
						}
						jsonObjKeyVariant.add(PGWidgetConstants.KEY_OBJECT_TYPE, hierarchyObj.getString(KEY_2_TYPE));
						jsonObjKeyVariant.add(KEY_TABLE_NAME, KEY_BOM);
						jsonObjKeyVariant.add(DomainConstants.SELECT_ID, hierarchyObj.getString(KEY_2_ID));
						jsonObjKeyVariant.add(KEY_EXTRA_INFO, jsonObjKeyVariantExtra.build());
						String strField = mapping.get(slKeys.get(index));
						if(UIUtil.isNotNullAndNotEmpty(strField)) {
							jsonObjKeyVariant.add(KEY_FIELD, StringUtil.split(strField, PGWidgetConstants.KEY_SELECTS_SEPARATOR).get(1));
						}
						if(!arrJsonObjTemp.contains(jsonObjKeyVariant.build())) {
							arrJsonObjTemp.add(jsonObjKeyVariant.build());
							jsonObjKeyVariant.add(PGWidgetConstants.KEY_HIERARCHY, jsonArrHier.build());
							jsonArrChar.add(jsonObjKeyVariant.build());
							set.add(strKey);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			throw e;		
		}
		return arrListDuplicates;
	}
	
	private static void buildCharJSON(Map<String, String> mapping, StringList slKeys, int index, String strNameRevision,
			 JsonObjectBuilder jsonObjKeyVariant, JsonObjectBuilder jsonObjKeyVariantExtra,
			JsonObject variant) {
		String strTMId  = DomainConstants.EMPTY_STRING;
		String strTMName  = DomainConstants.EMPTY_STRING;
		try {
			if(KEY_TEST_METHOD.equals(slKeys.get(index))) {
				if(variant.containsKey(KEY_2_TEST_METHOD_NAME)) {
					strTMName = variant.getString(KEY_2_TEST_METHOD_NAME);
				}
				if(variant.containsKey(KEY_2_TEST_METHOD_SPEC_NAME)) {
					if(UIUtil.isNotNullAndNotEmpty(strTMName)) {
						strTMName = new StringBuilder().append(strTMName).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).append(variant.getString(KEY_2_TEST_METHOD_SPEC_NAME)).toString();
					}else {
						strTMName = variant.getString(KEY_2_TEST_METHOD_SPEC_NAME);
					}
				}
				jsonObjKeyVariant.add(strNameRevision, strTMName);
			}else {
			jsonObjKeyVariant.add(strNameRevision, variant.getString(mapping.get(slKeys.get(index))));
			}
			if(variant.containsKey(KEY_2_TEST_METHOD_ID)) {
				 strTMId = variant.getString(KEY_2_TEST_METHOD_ID);
			}
			if(variant.containsKey(KEY_2_TEST_METHOD_SPEC_ID)) {
				if(UIUtil.isNotNullAndNotEmpty(strTMId)) {
					strTMId = new StringBuilder().append(strTMId).append(PGWidgetConstants.KEY_PIPE_SEPARATOR).append(variant.getString(KEY_2_TEST_METHOD_SPEC_ID)).toString();
				}else {
					strTMId = variant.getString(KEY_2_TEST_METHOD_SPEC_ID);
				}
			}
			jsonObjKeyVariantExtra.add(strNameRevision,
					Json.createObjectBuilder().add(KEY_REL_ID, variant.getString(KEY_2_ID_CONNECTION))
					.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_1_TYPE))
					.add(DomainConstants.SELECT_CURRENT, variant.getString(KEY_1_CURRENT))
					.add(KEY_ROW_ID, variant.getString(KEY_2_ID))
					.add(KEY_COL_ID, variant.getString(KEY_1_ID))
					.add(VALUE_TEST_METHOD_ID, strTMId).build());
		} catch (Exception e) {
			jsonObjKeyVariant.add(strNameRevision, DomainConstants.EMPTY_STRING);
			jsonObjKeyVariantExtra.add(strNameRevision, 
					Json.createObjectBuilder().add(KEY_REL_ID, DomainConstants.EMPTY_STRING)
					.add(DomainConstants.SELECT_TYPE, variant.getString(KEY_1_TYPE))
					.add(DomainConstants.SELECT_CURRENT, variant.getString(KEY_1_CURRENT))
					.add(KEY_COL_ID, variant.getString(KEY_1_ID))
					.add(KEY_ROW_ID, variant.getString(KEY_2_ID)).build());
		}
		
	}

	/**
	 * Copy and create new Experiment and create clone of all varients
	 * @param context
	 * @param strInputData
	 * @return String as json
	 * @throws Exception
	 */
	/*public static String copyExperiment(Context context, String objectId) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrRelated = Json.createArrayBuilder();
		String[] args = { objectId };
		MapList mlClonedInfo = JPO.invoke(context, KEY_JPO_FEI_MASS_EDIT, null, "copyExperimentAndVarient", args, MapList.class);
		if (mlClonedInfo != null && !mlClonedInfo.isEmpty()) {
			jsonArrRelated = buildJsonArrayFromMaplist(context, mlClonedInfo);
		}
		output.add(KEY_CENTERLINE, jsonArrRelated);
		return output.build().toString();
	}*/
	
	/**
	 * Revise varient and replicate relationship with Experiment
	 * @param context
	 * @param objectId
	 * @return String as json
	 * @throws Exception
	 */
	public static String reviseVariant(Context context, String strInputData) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strExperimentId = "";
		String strObjSelects = "";
		String strRelSelects = "";
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			String strVarId = jsonInputData.getString(DomainConstants.SELECT_ID);
			strExperimentId = jsonInputData.getString(KEY_EXPID);
			strObjSelects = jsonInputData.getString(PGWidgetConstants.OBJ_SELECT);
			strRelSelects = jsonInputData.getString(PGWidgetConstants.KEY_RELATIONSHIPSELECTS);
			StringList slVarientList = StringUtil.split(strVarId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			int iVarListSize = slVarientList.size();
			String[] args1 = { strVarId, strExperimentId };

			if (iVarListSize > 5) {
				Job bgjob = new com.matrixone.apps.domain.Job(KEY_JPO_FEI_MASS_EDIT, KEY_METHOD_REVISE_VARIENTS, args1, false);
				bgjob.setTitle(KEY_BACKGROUND_JOB_TITLE);
				bgjob.create(context);
				bgjob.setStartDate(context);
				bgjob.setAllowreexecute(KEY_NO);
				bgjob.setAttributeValue(context, KEY_NOTIFY_OWNER, KEY_NO);
				bgjob.submit(context);
				DomainObject domExp = DomainObject.newInstance(context, strExperimentId);
				domExp.setAttributeValue(context, ATTRIBUTE_JOB_ID, bgjob.getId(context));
			} else {
				JPO.invoke(context, KEY_JPO_FEI_MASS_EDIT, null, KEY_METHOD_REVISE_VARIENTS, args1, JsonArray.class);

			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		String strJson = getExperimentContents(context, strExperimentId, strObjSelects, strRelSelects);
		JsonObject jsonObj = PGWidgetUtil.getJsonFromJsonString(strJson);
		output.add(KEY_FOLDER_CONTENT, jsonObj.get(KEY_FOLDER_CONTENT));
		return output.build().toString();
	}
	
	/**
	 * Method to get  All Performance Characteristics or Centerlines based on type passed
	 * @param context
	 * @param strType
	 * @return String in json format with all Performance Characteristics or Centerlines in array
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String getAllExtendedData(Context context, String strType, String strValue) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			JsonObjectBuilder inputData = Json.createObjectBuilder();
			String strSelectables = (new StringBuilder()).append(DomainConstants.SELECT_NAME).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_ID).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(PGWidgetConstants.KEY_COMMA_SEPARATOR).toString();
			String strWhere = LATESTREV_WHERECLAUSE +" && ("+ DomainConstants.SELECT_ATTRIBUTE_TITLE +" ~~ '*"+ strValue +"*' || "+ DomainConstants.SELECT_NAME +" ~~ '*"+ strValue +"*')";
			inputData.add(PGWidgetConstants.TYPE_PATTERN, strType);
			inputData.add(PGWidgetConstants.NAME_PATTERN, CONSTANT_STRING_STAR);
			inputData.add(PGWidgetConstants.REVISION_PATTERN, CONSTANT_STRING_STAR);
			inputData.add(PGWidgetConstants.OBJECT_LIMIT, CONST_STRING_TEN);
			inputData.add(PGWidgetConstants.WHERE_EXP, strWhere);
			inputData.add(PGWidgetConstants.EXPAND_TYPE, PGWidgetConstants.STRING_FALSE);
			inputData.add(PGWidgetConstants.OBJ_SELECT, strSelectables);
			inputData.add(PGWidgetConstants.DURATION, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.ALLOWED_STATE, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.SHOW_OWNED, DomainConstants.EMPTY_STRING);

			MapList mlList = PGWidgetUtil.findObjects(context, inputData.build());
			@SuppressWarnings("rawtypes")
			Map tempMap = null;
			JsonArrayBuilder jsonArrRelated = Json.createArrayBuilder();
			if (mlList != null && !mlList.isEmpty()) {
				for (Iterator<?> iterator = mlList.iterator(); iterator.hasNext();) {
					tempMap = (Map<?, ?>) iterator.next();
					tempMap.put(PGWidgetConstants.KEY_VALUE, tempMap.get(DomainConstants.SELECT_ID));
					tempMap.put(DomainConstants.ATTRIBUTE_TITLE, tempMap.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				}
				jsonArrRelated = buildJsonArrayFromMaplist(context, mlList);
			}
			output.add(KEY_EXTENDEDDATA, jsonArrRelated.build());
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return output.build().toString();
	}	

	/**
	 * Create or update Performance Characteristics/CenterLine of Variants
	 * @param request
	 * @param strInputData
	 * @return Response as json cloned object info
	 * @throws Exception
	 */
	public static String createOrUpdateCharOrCenterlineOfVariant(Context context, String strInputData,String strType)throws Exception {
		JsonObjectBuilder jsonReturn = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			JsonArray updateValuesArray	= jsonInputData.getJsonArray(KEY_UPDATED_VALUES);
			JsonArray newValuesArray	= jsonInputData.getJsonArray(KEY_NEW_DATA);
			String strExperimentId		= jsonInputData.getString(DomainConstants.SELECT_ID);
			JsonArray jsonData;
			/*
			 * updateValuesArray : this has the Chars/CenterLine that need to be modified AND
			 * also the Chars/CenterLine that need to be connected to variant and later set attributes
			 */
			if(TYPE_CHARACTERISTIC.equals(strType)) {
				for(int i = 0; i<updateValuesArray.size() ;i++) {
					jsonData = updateValuesArray.getJsonArray(i);
					addNewCharOrUpdatechar(context, jsonData);
				}
				/*
				 * newValuesArray : this has the Chars/CenterLine that need to be connected to variant and later set attributes
				 */
				for(int i = 0; i<newValuesArray.size() ;i++) {
					jsonData = newValuesArray.getJsonArray(i);
					addNewCharOrUpdatechar(context, jsonData);
				}
				return getPerformanceChars(context, strExperimentId);
			}
			else {
				for(int i = 0; i<updateValuesArray.size() ;i++) {
					jsonData = updateValuesArray.getJsonArray(i);
					addNewOrUpdateCenterline(context, jsonData);
				}
				/*
				 * newValuesArray : this has the Chars/CenterLine that need to be connected to variant and later set attributes
				 */
				for(int i = 0; i<newValuesArray.size() ;i++) {
					jsonData = newValuesArray.getJsonArray(i);
					addNewOrUpdateCenterline(context, jsonData);
				}
				return getCenterline(context, strExperimentId);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonReturn.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonReturn.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			throw e;
		}
	}

	/**
	 * Create or update Performance Characteristics/CenterLine of Variants
	 * @param context
	 * @param jsonArrData
	 * @param strType
	 * @throws Exception
	 */
	public static void addNewCharOrUpdatechar(Context context, JsonArray jsonArrData) throws Exception {
		String strVarObjId = DomainConstants.EMPTY_STRING;
		String strCharObjId = DomainConstants.EMPTY_STRING;
		String strAttribName = null;
		String strTestMethodId = DomainConstants.EMPTY_STRING;
		String strRowIdentifier = DomainConstants.EMPTY_STRING;
		String strCharSpecValue = DomainConstants.EMPTY_STRING;
		String strNewValue = null;
		Map<String, Object> mapAttrib = new HashMap<>();
		JsonObject jsonObj = null;
		boolean isDeleteCase = false;
		boolean isCharSpecUpdated = false;
		StringList slCharIds;
		DomainObject domChar = null;
		// loop for collect the attributes to be modified in a map
		for(int i = 0; i<jsonArrData.size() ;i++) {
			jsonObj = jsonArrData.getJsonObject(i);
			strAttribName = PGWidgetUtil.getFormattedExpression(jsonObj.getString(KEY_FIELD));
			strNewValue = jsonObj.getString(KEY_NEW_VALUE);
			if(jsonObj.containsKey(KEY_ROW_ID)) {
				strCharObjId = jsonObj.getString(KEY_ROW_ID);
			}
			if(jsonObj.containsKey(KEY_COL_ID)) {
				strVarObjId = jsonObj.getString(KEY_COL_ID);
			}
			if(ATTR_PG_TARGET.equals(strAttribName) && UIUtil.isNullOrEmpty(strNewValue) && UIUtil.isNotNullAndNotEmpty(strCharObjId)) {
				PGWidgetUtil.deleteObjects(context, strCharObjId);
				isDeleteCase = true;
			}
			else if(ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS.equals(strAttribName) && UIUtil.isNotNullAndNotEmpty(strCharObjId)) {
				slCharIds = StringUtil.split(strCharObjId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				for(int index = 0 ; index < slCharIds.size() ; index++) {
					if(UIUtil.isNotNullAndNotEmpty(slCharIds.get(index))) {
						domChar = DomainObject.newInstance(context, slCharIds.get(index));
						domChar.setAttributeValue(context, strAttribName, strNewValue);
					}
				}
				isCharSpecUpdated = true;
			}
			else if(ATTR_PG_LOWERSPECIFICATIONLIMIT.equals(strAttribName) && UIUtil.isNotNullAndNotEmpty(strNewValue) ){
				mapAttrib.put(ATTR_PG_LOWERSPECIFICATIONLIMIT, jsonObj.getString(KEY_NEW_VALUE));
				mapAttrib.put(ATTR_PG_LOWERROUTINERELEASELIMIT, jsonObj.getString(KEY_NEW_VALUE));
			}
			else if(ATTR_PG_UPPERSPECIFICATIONLIMIT.equals(strAttribName) && UIUtil.isNotNullAndNotEmpty(strNewValue) ){
				mapAttrib.put(ATTR_PG_UPPERSPECIFICATIONLIMIT, jsonObj.getString(KEY_NEW_VALUE));
				mapAttrib.put(ATTR_PG_UPPERROUTINERELEASELIMIT, jsonObj.getString(KEY_NEW_VALUE));
			}
			else {
				if(strAttribName.equals(VALUE_TEST_METHOD)) {
					if(jsonObj.containsKey(VALUE_TEST_METHOD)) {
					strTestMethodId = jsonObj.getString(VALUE_TEST_METHOD);
					}
					updateTestMethod(context, strCharObjId, strTestMethodId, isDeleteCase);
				}
				else {
					mapAttrib.put(strAttribName, jsonObj.getString(KEY_NEW_VALUE));
				}
			}
				/*
				 * if char/centerLine ID is not provided -> it is a new connection 
				 * and hence the <strRowIdentifier> is provided to set on newly created Char/CenterLine
				 */
			if(jsonObj.containsKey(KEY_ROW_IDENTIFIER)) {
					strRowIdentifier = FrameworkUtil.split(jsonObj.getString(KEY_ROW_IDENTIFIER), PGWidgetConstants.KEY_PIPE_SEPARATOR).get(0) ;
				}
			if(jsonObj.containsKey(ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS)) {
					strCharSpecValue = jsonObj.getString(ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS);
			}
		}
		//Only Modify condition
		if(UIUtil.isNotNullAndNotEmpty(strCharObjId) && !isDeleteCase && !isCharSpecUpdated) {
			domChar = DomainObject.newInstance(context, strCharObjId);
			domChar.setAttributeValues(context, mapAttrib);
//			if(UIUtil.isNotNullAndNotEmpty(strTestMethodId)) {
//				connectRelatedObjects(context, domChar, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT,strTestMethodId);
//			}
		}
		//Create, connect and then set attributes condition
		else if(UIUtil.isNotNullAndNotEmpty(strVarObjId) && !isDeleteCase) {

			String strRelName;
			String strConnectType = TYPE_CHARACTERISTIC;
			strRelName 	= REL_EXTENDED_DATA;
			mapAttrib.put(ATTR_PG_CHARACTERISTIC , strRowIdentifier);
			mapAttrib.put(ATTRIBUTE_ACTION_REQUIRED , VALUE_DEVELOPMENT);
			mapAttrib.put(ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS , strCharSpecValue);
			mapAttrib.put(DomainConstants.ATTRIBUTE_TITLE , strRowIdentifier);
			domChar = DomainObject.newInstance(context);
			domChar.createObject(context, strConnectType, null, CONST_STRING_ZERO, POLICY_EXTENDED_DATA, context.getVault().getName());
			String strCharId = domChar.getId(context);

			domChar.setAttributeValues(context, mapAttrib);

			PGWidgetUtil.addExisting(context, strVarObjId, strCharId, strRelName, false);
			if(UIUtil.isNotNullAndNotEmpty(strTestMethodId)) {
				connectRelatedObjects(context, domChar, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT,strTestMethodId);
			}
		}
	}

	private static void updateTestMethod(Context context, String strCharObjId, String strTestMethodId,
			boolean isDeleteCase) throws Exception {
		DomainObject domObj;
		try {
			if(UIUtil.isNotNullAndNotEmpty(strCharObjId) && !isDeleteCase) {
				//DomainConstants.MULTI_VALUE_LIST.add(SELECTABLE_TO + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT + SELECTABLE_REL_ID);
			domObj = DomainObject.newInstance(context, strCharObjId);
				StringList slTMOldId = domObj.getInfoList(context, SELECTABLE_TO + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT + SELECTABLE_FROM_ID);
				StringList slToAdd = new StringList();
				StringList slToRemove = new StringList();
				if(UIUtil.isNotNullAndNotEmpty(strTestMethodId)){
					StringList slTMNew = FrameworkUtil.split(strTestMethodId, PGWidgetConstants.KEY_PIPE_SEPARATOR);
					if(slTMOldId == null || slTMOldId.isEmpty()) {
						slToAdd.addAll(slTMNew);
					}else if(slTMOldId != null && !slTMOldId.isEmpty()) {
					for(int i=0 ; i<slTMOldId.size() ; i++) {
						if(!slTMNew.contains(slTMOldId.get(i))) {
							slToRemove.add(slTMOldId.get(i));
						}
					}
					for(int i=0 ; i<slTMNew.size() ; i++) {
						if(!slTMOldId.contains(slTMNew.get(i))) {
							slToAdd.add(slTMNew.get(i));
						}
					}
				}
				}
				else if(UIUtil.isNullOrEmpty(strTestMethodId) && slTMOldId != null && !slTMOldId.isEmpty()) {
					slToRemove.addAll(slTMOldId);
				}
				if((slToRemove != null && !slToRemove.isEmpty())) {
					disconnectObjects(context, domObj, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, false, slToRemove);
				}
				if(slToAdd != null && !slToAdd.isEmpty()) {
					String[] sConnectIdsArray = slToAdd.toArray(new String[slToAdd.size()]);
					domObj.addRelatedObjects(context, new RelationshipType(DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT), false, sConnectIdsArray);
				}
			}
		} catch (FrameworkException e) {
			throw e;
		}finally {
			//DomainConstants.MULTI_VALUE_LIST.remove(SELECTABLE_TO + DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT + SELECTABLE_FROM_ID);
		}
	}

	public static void disconnectObjects(Context context, DomainObject domParent, String strRelationship, boolean bIsFrom, StringList slChildrenIds) throws MatrixException {
		RelationshipType relType = new RelationshipType(strRelationship);
		for(int i=0 ; i<slChildrenIds.size() ; i++) {
			domParent.disconnect(context, relType, bIsFrom, new BusinessObject(slChildrenIds.get(i)));
		}
	}

	public static void addNewOrUpdateCenterline(Context context, JsonArray jsonArrData) throws Exception {
		String strVarObjId = DomainConstants.EMPTY_STRING;
		String strCharObjId = DomainConstants.EMPTY_STRING;
		String strAttribName = null;
		String strTestMethodId = DomainConstants.EMPTY_STRING;
		String strRowIdentifier = DomainConstants.EMPTY_STRING;
		String strNewValue = null;
		Map<String, Object> mapAttrib = new HashMap<>();
		JsonObject jsonObj = null;
		boolean isTargetUpdated = false;
		boolean isCommentsUpdated = false;
		boolean isCharSpecUpdated = false;
		boolean isTargetEmpty = false;
		boolean isCommentsEmpty = false;
		boolean isCharIdEmpty = false;
		boolean isNewValueEmpty = false;
		DomainObject domChar = null;
		// loop for collect the attributes to be modified in a map
		for(int i = 0; i<jsonArrData.size() ;i++) {
			jsonObj = jsonArrData.getJsonObject(i);
			strAttribName = PGWidgetUtil.getFormattedExpression(jsonObj.getString(KEY_FIELD));
			strNewValue = jsonObj.getString(KEY_NEW_VALUE);
			isNewValueEmpty = UIUtil.isNullOrEmpty(strNewValue);
			if(jsonObj.containsKey(KEY_ROW_ID)) {
				strCharObjId = jsonObj.getString(KEY_ROW_ID);
			}
			isCharIdEmpty = UIUtil.isNullOrEmpty(strCharObjId);
			if(jsonObj.containsKey(KEY_COL_ID)) {
				strVarObjId = jsonObj.getString(KEY_COL_ID);
			}
			if(jsonObj.containsKey(ATTRIBUTE_PGUOM)) {
				mapAttrib.put(ATTRIBUTE_PGUOM, jsonObj.getString(ATTRIBUTE_PGUOM));
			}
			if(ATTR_PG_TARGET.equals(strAttribName)){
				isTargetUpdated = true;
				if(isNewValueEmpty && !isCharIdEmpty) {
					isTargetEmpty = true;
				}else {
					mapAttrib.put(strAttribName, strNewValue);
				}
			}
			if(ATTR_COMMENTS.equals(strAttribName)){
				isCommentsUpdated = true;
				if(isNewValueEmpty && !isCharIdEmpty) {
					isCommentsEmpty = true;
				}else {
					mapAttrib.put(strAttribName, strNewValue);
				}

			}
			else {
				mapAttrib.put(strAttribName, jsonObj.getString(KEY_NEW_VALUE));
			}
				/*
				 * if char/centerLine ID is not provided -> it is a new connection 
				 * and hence the <strRowIdentifier> is provided to set on newly created Char/CenterLine
				 */
				if(isCharIdEmpty) {
					strRowIdentifier = jsonObj.getString(KEY_ROW_IDENTIFIER);
			}
		}
		if(!isCharIdEmpty && (!isTargetUpdated || (isTargetUpdated && isTargetEmpty)) && (!isCommentsUpdated || (isCommentsUpdated && isCommentsEmpty)) && mapAttrib.isEmpty()) {
			PGWidgetUtil.deleteObjects(context, strCharObjId);
		}
		//Only Modify condition
		else if(!isCharIdEmpty && !isCharSpecUpdated) {
			domChar = DomainObject.newInstance(context, strCharObjId);
			domChar.setAttributeValues(context, mapAttrib);
			if(UIUtil.isNotNullAndNotEmpty(strTestMethodId)) {
				connectRelatedObjects(context, domChar, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT,strTestMethodId);
			}
		}
		//Create, connect and then set attributes condition
		else if(UIUtil.isNotNullAndNotEmpty(strVarObjId)) {

			String strRelName = RELATIONSHIP_PGCENTERLINE;
			String strConnectType = TYPE_CENTERLINE;
			mapAttrib.put(DomainConstants.ATTRIBUTE_TITLE , strRowIdentifier);
			domChar = DomainObject.newInstance(context);
			domChar.createObject(context, strConnectType, null, CONST_STRING_ZERO, POLICY_EXTENDED_DATA, context.getVault().getName());
			String strCharId = domChar.getId(context);

			domChar.setAttributeValues(context, mapAttrib);

			PGWidgetUtil.addExisting(context, strVarObjId, strCharId, strRelName, false);
			if(UIUtil.isNotNullAndNotEmpty(strTestMethodId)) {
				connectRelatedObjects(context, domChar, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT,strTestMethodId);
			}
		}
	}

	/**
	 * Method to connect related objects
	 * @param context
	 * @param domParent
	 * @param strRelName
	 * @param strConnectObjIdList
	 * @throws Exception
	 */
	public static void connectRelatedObjects(Context context, DomainObject domParent, String strRelName,String strConnectObjIdList) throws Exception {
		try {
			StringList slConnectedIds = domParent.getInfoList(context, (new StringBuilder()).append(SELECTABLE_TO).append(strRelName).append(SELECTABLE_REL_ID).toString());
			if(BusinessUtil.isNotNullOrEmpty(slConnectedIds))
				DomainRelationship.disconnect(context, BusinessUtil.toStringArray(slConnectedIds));
			StringList slToConnectIds= StringUtil.splitString(strConnectObjIdList, PGWidgetConstants.KEY_PIPE_SEPARATOR);
			String[] sConnectIdsArray = slToConnectIds.toArray(new String[slToConnectIds.size()]);
			domParent.addRelatedObjects(context,new RelationshipType(strRelName),false,sConnectIdsArray);

		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
	}
	
	/**
	 * This method displays Characteristic specifics
	 * @param context
	 * @param args
	 * @return String in json format with all Characteristic Specifics in array
	 * @throws Exception
	 */
	public static String getCharSpecifics(Context context) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strSelectables = (new StringBuilder()).append(DomainConstants.SELECT_NAME).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(DomainConstants.SELECT_ID).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(PGWidgetConstants.KEY_COMMA_SEPARATOR).toString();
		output.add(KEY_CHARACTERISTIC_SPECIFIC, getFindObjectsList(context,TYPE_PG_PLICHARSPECIFICS,CONSTANT_STRING_STAR,LATESTREV_WHERECLAUSE,strSelectables));
		return output.build().toString();
	}	

	/**
	 * This method gets Test Method Specifications
	 * 
	 * @param context
	 * @param args
	 * @return json string
	 * @throws Exception
	 */
	public static String getTestMethodSpecs(Context context) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strSelectables = (new StringBuilder()).append(DomainConstants.SELECT_NAME).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(DomainConstants.SELECT_ID).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(PGWidgetConstants.KEY_COMMA_SEPARATOR).toString();
		output.add(KEY_EXPERIMENTS, getFindObjectsList(context,TYPE_TEST_METHOD_SPECIFICATION,CONSTANT_STRING_STAR,LATESTREV_WHERECLAUSE,strSelectables));
		return output.build().toString();
	}
	
	/**
	 * Get picklist values for all types
	 * @param context
	 * @param strInputData
	 * @return String in json format with status
	 * @throws Exception
	 */
	public static String getPickListValues(Context context, String strInputData) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			String strName = CONSTANT_STRING_STAR;
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);	
			String strType = jsonInputData.getString(DomainConstants.SELECT_TYPE);
			String strWhere = jsonInputData.getString(PGWidgetConstants.WHERE_EXP);

			if (jsonInputData.containsKey(PGWidgetConstants.NAME_PATTERN)) {
				strName = jsonInputData.getString(PGWidgetConstants.NAME_PATTERN);
			}

			String strSelectables = (new StringBuilder()).append(DomainConstants.SELECT_NAME).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_ID).append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
					.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).toString();
			if(UIUtil.isNotNullAndNotEmpty(strType)) {
				StringList slPickListNames = StringUtil.split(strType, PGWidgetConstants.KEY_COMMA_SEPARATOR);
				JsonObject jsonObj;
				for(int i=0 ; i<slPickListNames.size() ; i++) {
					if(TYPE_PLANT.equals(slPickListNames.get(i)))
					{
						jsonObj = getPlantsAndOperationalLines(context);
						output.add(TYPE_PLANT, jsonObj.get(TYPE_PLANT));
						output.add(KEY_OPERATIONALLINE, jsonObj.get(KEY_OPERATIONALLINE));
					}else {
						output.add(slPickListNames.get(i), getFindObjectsList(context, slPickListNames.get(i), strName, strWhere, strSelectables));
					}
				}
			}
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			output.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return output.build().toString();
	}
	
	/**
	 * Get Plant and operational lines data
	 * @param context
	 * @return json format
	 * @throws FrameworkException
	 */
	public static JsonObject getPlantsAndOperationalLines(Context context) throws FrameworkException {
		String strSubsetId = PGWidgetUtil.getObjectId(context, TYPE_PG_PL_SUBSET_LIST, FAMILY_CARE_PILOT_PLANT, KEY_HIPHEN);
		JsonObjectBuilder inputData = Json.createObjectBuilder();
		inputData.add(PGWidgetConstants.KEY_OBJECT_ID, strSubsetId);
		inputData.add(PGWidgetConstants.KEY_RELPATTERN, RELATIONSHIP_PL_SUBSET_ITEM);
		inputData.add(PGWidgetConstants.TYPE_PATTERN, TYPE_PLANT);
		inputData.add(PGWidgetConstants.KEY_EXPANDLEVEL, CONST_STRING_ZERO);
		inputData.add(PGWidgetConstants.KEY_WHERECONDITION, ACTIVEPLANT_WHERECLAUSE);
		inputData.add(PGWidgetConstants.KEY_GETTO, PGWidgetConstants.STRING_FALSE);
		inputData.add(PGWidgetConstants.KEY_GETFROM, PGWidgetConstants.STRING_TRUE);
		inputData.add(PGWidgetConstants.KEY_LIMIT, CONST_STRING_ZERO);
		inputData.add(PGWidgetConstants.KEY_RELWHERECONDITION, DomainConstants.EMPTY_STRING);
		inputData.add(PGWidgetConstants.KEY_RELATIONSHIPSELECTS, DomainConstants.EMPTY_STRING);
		inputData.add(PGWidgetConstants.KEY_OBJECT_SELECTS, DomainConstants.SELECT_ID);
		MapList mlObjectList = PGWidgetUtil.getRelatedObjectsMapList(context,inputData.build());
		JsonObjectBuilder jsonObjBldr = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjOperationBldr = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrBlr = Json.createArrayBuilder();
		JsonArrayBuilder jsonArrPlantBlr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjectBldr = null;
		JsonObjectBuilder jsonTempBldr = null;
		if (mlObjectList != null && !mlObjectList.isEmpty()) {
			inputData.add(PGWidgetConstants.KEY_RELPATTERN, RELATIONSHIP_PL_COMMON_REL);
			inputData.add(PGWidgetConstants.TYPE_PATTERN, TYPE_PG_PLI_OPERATIONALLINE);
			inputData.add(PGWidgetConstants.KEY_GETTO, PGWidgetConstants.STRING_TRUE);
			inputData.add(PGWidgetConstants.KEY_GETFROM, PGWidgetConstants.STRING_FALSE);
			Map<String, String> mapPlant;
			MapList mlOperationlines;
			Map<String, String> mapTemp ;
			for(int i=0 ; i<mlObjectList.size() ; i++) {
				mapPlant = (Map<String, String>) mlObjectList.get(i);
				jsonObjectBldr = Json.createObjectBuilder();
				jsonObjectBldr.add(DomainConstants.SELECT_NAME, mapPlant.get(DomainConstants.SELECT_NAME));
				jsonObjectBldr.add(KEY_VALUE, mapPlant.get(DomainConstants.SELECT_NAME));
				jsonObjectBldr.add(DomainConstants.SELECT_ID, mapPlant.get(DomainConstants.SELECT_ID));
				jsonArrPlantBlr.add(jsonObjectBldr.build());
				inputData.add(PGWidgetConstants.KEY_OBJECT_ID, mapPlant.get(DomainConstants.SELECT_ID));
				mlOperationlines = PGWidgetUtil.getRelatedObjectsMapList(context,inputData.build());
				if (mlOperationlines != null && !mlOperationlines.isEmpty()) {
					jsonArrBlr = Json.createArrayBuilder();
					for(int j=0 ; j<mlOperationlines.size() ; j++) {
						mapTemp = (Map<String, String>) mlOperationlines.get(j);
						jsonTempBldr = Json.createObjectBuilder();
						jsonTempBldr.add(DomainConstants.SELECT_NAME, mapTemp.get(DomainConstants.SELECT_NAME).toString());
						jsonTempBldr.add(KEY_VALUE, mapTemp.get(DomainConstants.SELECT_NAME));
						jsonArrBlr.add(jsonTempBldr.build());
					}
				}
				jsonObjOperationBldr.add(mapPlant.get(DomainConstants.SELECT_NAME), jsonArrBlr.build());
			}
			jsonObjBldr.add(TYPE_PLANT, jsonArrPlantBlr);
			jsonObjBldr.add(KEY_OPERATIONALLINE, jsonObjOperationBldr);
		}
		return jsonObjBldr.build();
	}
	public static JsonArray getFindObjectsList(Context context, String strType,String strName, String strWhereClause,String strSelectables) throws Exception {
		JsonObjectBuilder inputData = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrRelated = Json.createArrayBuilder();
		try {
			inputData.add(PGWidgetConstants.TYPE_PATTERN, strType);
			inputData.add(PGWidgetConstants.NAME_PATTERN, strName);
			inputData.add(PGWidgetConstants.REVISION_PATTERN, CONSTANT_STRING_STAR);
			inputData.add(PGWidgetConstants.OBJECT_LIMIT, CONST_STRING_ZERO);
			inputData.add(PGWidgetConstants.WHERE_EXP, strWhereClause);
			inputData.add(PGWidgetConstants.EXPAND_TYPE, PGWidgetConstants.STRING_FALSE);
			inputData.add(PGWidgetConstants.OBJ_SELECT, strSelectables);
			inputData.add(PGWidgetConstants.DURATION, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.ALLOWED_STATE, DomainConstants.EMPTY_STRING);
			inputData.add(PGWidgetConstants.SHOW_OWNED, DomainConstants.EMPTY_STRING);

			MapList mlList = PGWidgetUtil.findObjects(context, inputData.build());
			if (mlList != null && !mlList.isEmpty()) {
				jsonArrRelated = buildJsonArrayFromMaplist(context, mlList);
			}
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
		}
		return jsonArrRelated.build();
	}

	/**
	 * Method to set selected Persons as Co Owners of experiment
	 * 
	 * @param context
	 * @return String in json format with success or failure in array
	 * @throws Exception
	 */
	public static String setCoOwnersOnExperiment(Context context,String strExperimentId,String strInputData) throws Exception {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
			JsonArray coOwnerArray = jsonInputData.getJsonArray(KEY_SELECTEDMEMBERS);
			JsonObject jsonObj = null;
			String strPersonId = null;
			String strPersonAccess = null;
			JsonArray jsonArrData;
			for (int i = 0; i < coOwnerArray.size(); i++) {
				jsonArrData = coOwnerArray.getJsonArray(i);
				for (int j = 0; j < jsonArrData.size(); j++) {
					jsonObj = jsonArrData.getJsonObject(j);
					strPersonId = PGWidgetUtil.getFormattedExpression(jsonObj.getString(DomainConstants.SELECT_ID));
					strPersonAccess = PGWidgetUtil.getFormattedExpression(jsonObj.getString(KEY_ACCESS));
					DomainAccess.createObjectOwnership(context, strExperimentId, strPersonId, strPersonAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
				}
			}
			jsonObjBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		} catch (MatrixException ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonObjBuilder.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonObjBuilder.add(PGWidgetConstants.KEY_ERROR, ex.getMessage());
			jsonObjBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return jsonObjBuilder.build().toString();
	}
	/**
	 * Converts maplist data to json array for varients
	 * @param context 
	 * @param mlList list of maps containing object details
	 * @param revisedVarientIds 
	 * @return json array
	 * @throws Exception
	 */
	private static JsonArrayBuilder buildJsonArrayFromMaplistForVariants(Context context, MapList mlList, JsonObject jsonObjJob) throws MatrixException {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		try {
			JsonObjectBuilder jsonObject = null;
			Map<?, ?> objMap = null;
			String strId = null;
			String strJobStatus = DomainConstants.EMPTY_STRING;
			String strErrorTrace = DomainConstants.EMPTY_STRING;
			String strVariantIds = DomainConstants.EMPTY_STRING;
			Object objValue;
			String strTypeDisplayName;
			StringList slTemp;
			StringBuilder sbValues;
			if(jsonObjJob.containsKey(KEY_JOB_STATUS) && jsonObjJob.containsKey(KEY_JOB_COMPLETION_STATUS)) {
				strJobStatus = jsonObjJob.getString(KEY_JOB_COMPLETION_STATUS);
				if("None".equalsIgnoreCase(strJobStatus)) {
					strJobStatus = jsonObjJob.getString(KEY_JOB_STATUS);
				}
				if(jsonObjJob.containsKey(KEY_JOB_ERROR_MESSAGE)) {
					strErrorTrace = jsonObjJob.getString(KEY_JOB_ERROR_MESSAGE);
				}
				if(jsonObjJob.containsKey(KEY_PROGRAM_ARGUMENTS)) {
					strVariantIds = jsonObjJob.getString(KEY_PROGRAM_ARGUMENTS);
				}
			}
			for (int i = 0; i < mlList.size(); i++) {
				jsonObject = Json.createObjectBuilder();
				objMap = (Map<?, ?>) mlList.get(i);

				strId = PGWidgetUtil.checkNullValueforString((String) objMap.get(DomainConstants.SELECT_ID));

				for (Entry<?, ?> entry : objMap.entrySet()) {

					objValue = entry.getValue();									
					if(objValue == null || "".equals(objValue))
					{
						objValue = DomainConstants.EMPTY_STRING;
					}
					if (!FrameworkUtil.hasAccess(context, DomainObject.newInstance(context, strId), PGWidgetConstants.ACCESS_READ) && objValue.equals(PGWidgetConstants.DENIED)) {
						jsonObject.add((String) entry.getKey(),
								EnoviaResourceBundle.getProperty(context, PGWidgetConstants.FILE_FRAMEWORK_STRING_RESOURCE,
										context.getLocale(), "emxFramework.Access.NoAccess"));
					}
					else if(DomainConstants.SELECT_REVISION.equals(entry.getKey()))
					{
						if(jsonObjJob.containsKey(KEY_JOB_METHOD_NAME))
						{
							if(METHOD_REVISE_JOB.equals(jsonObjJob.getString(KEY_JOB_METHOD_NAME)) && strVariantIds.contains(strId)) {
								if(strJobStatus.equals(KEY_JOB_FAILED) || strJobStatus.equals(KEY_JOB_ABORTED)) {
									jsonObject.add(KEY_JOB_ERROR_MESSAGE, strErrorTrace);
								}else if(strJobStatus.equals(Job.STATE_JOB_CREATED) || strJobStatus.equals(Job.STATE_JOB_SUBMITTED) || strJobStatus.equals(Job.STATE_JOB_RUNNING)) {
									strJobStatus = KEY_JOB_RUNNING;
								}else{
									strJobStatus = (String) objValue;
								}
								jsonObject.add(DomainConstants.SELECT_REVISION, strJobStatus);
							}
							else
							{						
								jsonObject.add(DomainConstants.SELECT_REVISION, (String) objValue);
							}
						}
						else
						{						
							jsonObject.add(DomainConstants.SELECT_REVISION, (String) objValue);
						}
					}
					else if(DomainConstants.SELECT_TYPE.equals(entry.getKey())) {
						strTypeDisplayName = i18nNow.getTypeI18NString((String)objValue, context.getLocale().getLanguage());
						jsonObject.add(PGWidgetConstants.KEY_DISPLAY_TYPE, strTypeDisplayName);
						jsonObject.add(DomainConstants.SELECT_TYPE, (String)objValue);
					}
					else if(DomainConstants.SELECT_ID.equals(entry.getKey())) {
						jsonObject.add(DomainConstants.SELECT_ID,(String)objValue);
						jsonObject.add(KEY_VALUE,(String)objValue);
					}
					else
					{					
						if (objValue instanceof String) {
							jsonObject.add((String) entry.getKey(), (String) objValue);
						} else if (objValue instanceof StringList) {
							slTemp = (StringList) objValue;
							sbValues = new StringBuilder();
							for (int j = 0; j < slTemp.size(); j++) {
								if(UIUtil.isNotNullAndNotEmpty(slTemp.get(j))) {
									sbValues.append(slTemp.get(j));
									sbValues.append(PGWidgetConstants.KEY_COMMA_SEPARATOR);}
							}
							jsonObject.add((String) entry.getKey(), sbValues.toString());
						}
					}
				}
				jsonArr.add(jsonObject);
			}
		} catch (MatrixException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return jsonArr;
	}

	/**
	 * Checks if context user is PG Employee.
	 * @param context ENOVIA context
	 * @param args
	 */
	public static String isEBPUser(Context context, String strInputData) throws Exception{
		JsonObjectBuilder jsonResp = Json.createObjectBuilder();
		StringBuilder sbMsg = new StringBuilder();
		StringList slInvalid = new StringList();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		JsonArray jsonArrPersons = jsonInputData.getJsonArray("UserInfo") ;
		for(int i=0 ;i <jsonArrPersons.size() ; i++) {
			String strName = jsonArrPersons.getJsonObject(i).getString(DomainConstants.SELECT_NAME);
			if(!PERSON_AGENT.equals(strName))
			{
				//Get Employee Type for Person
				DomainObject personObj= DomainObject.newInstance(context, jsonArrPersons.getJsonObject(i).getString(DomainConstants.SELECT_ID));
				String employeeType = (String)personObj.getAttributeValue(context,ATTRIBUTE_PGEMPTYPE);
				if((UIUtil.isNotNullAndNotEmpty(employeeType) && employeeType.equals(ATTR_EMPLOYEETYPE_EBP)))
					slInvalid.add(strName);
			}
		}
		if(!slInvalid.isEmpty()) {
			sbMsg.append(NOT_EBP_USERS).append(PGWidgetUtil.getStringFromSL(slInvalid, PGWidgetConstants.KEY_COMMA_SEPARATOR));
			jsonResp.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_FAILED);
			jsonResp.add(PGWidgetConstants.KEY_MESSAGE, sbMsg.toString());
		}else {
			jsonResp.add(PGWidgetConstants.KEY_STATUS, PGWidgetConstants.KEY_SUCCESS);
		}

		return jsonResp.build().toString();
	} 
	public static void setCharSequenceOnExperiment(Context context, String strExperimentId, String strVariantId) throws FrameworkException {
		String objSelectablesList = (new StringBuilder()).append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_CHARACTERISTIC).append(RIGHT_BRACKET)
				.append(PGWidgetConstants.KEY_COMMA_SEPARATOR)
				.append(ATTRIBUTE_LEFT_BRACKET).append(ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS).append(RIGHT_BRACKET).toString();
		String relSelectablesList = (new StringBuilder()).append(ATTRIBUTE_LEFT_BRACKET).append(ATTR_PG_SHARED_TABLE_CHAR_SEQUENCE).append(RIGHT_BRACKET).toString();
		Pattern typePat = new Pattern(TYPE_CHARACTERISTIC);
		Pattern relPat = new Pattern(REL_EXTENDED_DATA);
		JsonObjectBuilder inputData = Json.createObjectBuilder();
		inputData.add(PGWidgetConstants.KEY_OBJECT_ID, strVariantId);
		inputData.add(PGWidgetConstants.KEY_RELPATTERN, relPat.getPattern());
		inputData.add(PGWidgetConstants.TYPE_PATTERN, typePat.getPattern());
		inputData.add(PGWidgetConstants.KEY_EXPANDLEVEL, "1");
		inputData.add(PGWidgetConstants.KEY_WHERECONDITION, DomainConstants.EMPTY_STRING);
		inputData.add(PGWidgetConstants.KEY_GETTO, PGWidgetConstants.STRING_FALSE);
		inputData.add(PGWidgetConstants.KEY_GETFROM, PGWidgetConstants.STRING_TRUE);
		inputData.add(PGWidgetConstants.KEY_LIMIT, CONST_STRING_ZERO);
		inputData.add(PGWidgetConstants.KEY_RELWHERECONDITION, DomainConstants.EMPTY_STRING);
		inputData.add(PGWidgetConstants.KEY_RELATIONSHIPSELECTS, relSelectablesList);
		inputData.add(PGWidgetConstants.KEY_OBJECT_SELECTS, objSelectablesList);
		MapList mlObjectList = PGWidgetUtil.getRelatedObjectsMapList(context, inputData.build());
		mlObjectList.addSortKey(ATTRIBUTE_LEFT_BRACKET + ATTR_PG_SHARED_TABLE_CHAR_SEQUENCE + RIGHT_BRACKET, PGWidgetConstants.ASCENDING, "integer");
		mlObjectList.sort();
		JsonArrayBuilder jsonArrBldr = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjBldr = Json.createObjectBuilder();
		for(int i=0 ; i<mlObjectList.size() ; i++) {
			Map mapTemp = (Map) mlObjectList.get(i);
			jsonArrBldr.add(new StringBuilder().append(mapTemp.get(ATTRIBUTE_LEFT_BRACKET + ATTR_PG_CHARACTERISTIC + RIGHT_BRACKET)).append(PGWidgetConstants.KEY_PIPE_SEPARATOR)
					.append(mapTemp.get(ATTRIBUTE_LEFT_BRACKET + ATTRIBUTE_PG_CHARACTERISTICSSPECIFICS + RIGHT_BRACKET)).toString());
		}
		jsonObjBldr.add("charHierarchy", jsonArrBldr.build());
		DomainObject domExp = DomainObject.newInstance(context, strExperimentId);
		domExp.setAttributeValue(context, ATTRIBUTE_PG_FEI_SEQUENCE, jsonObjBldr.build().toString());
	}
}
