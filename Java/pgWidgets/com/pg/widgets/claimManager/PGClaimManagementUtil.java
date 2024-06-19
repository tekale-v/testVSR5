/*
 * PGClaimManagementUtil.java
 * 
 * Added by DSM Claim Manager Team
 * For Claim Management Widget related Web Service
 * 
 */

package com.pg.widgets.claimManager;

import static com.matrixone.apps.domain.DomainConstants.EMPTY_STRING;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.aspose.cells.PdfCompliance;
import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.tasks.StringBuilder;
import com.aspose.words.Document;
import com.dassault_systemes.attributesmngt.JsonArrayMaker;
import com.dassault_systemes.attributesmngt.JsonObjectMaker;
import com.dassault_systemes.enovia.pdfrender.services.GLSAsposeLicense;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.common.InboxTask;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.common.SubscriptionManager;
import com.matrixone.apps.common.UserTask;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MailUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.mxType;
import com.matrixone.apps.engineering.RelToRelUtil;
import com.matrixone.apps.framework.ui.UICache;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectAttributes;
import matrix.db.BusinessObjectWithSelect;
import matrix.db.BusinessObjectWithSelectList;
import matrix.db.ClientTask;
import matrix.db.ClientTaskItr;
import matrix.db.ClientTaskList;
import matrix.db.Context;
import matrix.db.FileItr;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.db.Person;
import matrix.db.Policy;
import matrix.db.RelationshipType;
import matrix.db.SelectConstants;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;
/**
 * Class PGClaimManagementUtil has all the methods defined for the 'Claim Management' widget activities.
 * 
 * @Since 2018x.6
 * @author DCM (DS)
 *
 */
public class PGClaimManagementUtil 
{		

	private static final String STRING_COPY = "copy";
	private static final String STRING_ASSIGNED = "Assigned";
	private static final String STRING_DISCLAIMER = "Disclaimer";

	private static final String STRING_CLAIM_REQUEST = "Claim Request";
	private static final String STRING_CLAIM_SUPPORT = "Claim Support";
	static final Logger logger = Logger.getLogger(PGClaimManagementUtil.class.getName());

	//STRING CONSTANTS
	private static final String STATUS_SUCCESS = "success";
	private static final String STATUS_ERROR = "error";
	private static final String CURRENT_ACCESS = "current.access";
	private static final String ROLE_PG_CLAIM_USER = "pgClaimUser";
	//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - START
	private static final String ROLE_PG_CLAIM_AUTHOR = "pgClaimAuthor";
	private static final String STRING_CHECK_AUTHOR_ROLE = "CheckAuthorRole";
	//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - END
	static final String SELECT_FROM = "from[";
	static final String SELECT_TO_ID = "].to.id";
	static final String STRING_OBJECT_ID = "objectId";
	static final String EXCEPTION_MESSAGE  = "Exception in PGClaimManagementUtil";
	static final String RANGE_REVIEWER = "Reviewer";
	static final String RANGE_FINAL_APPROVER = "Final Approver";
	static final String CONST_REVIEWERS = "Reviewers";
	static final String CONST_APPROVERS = "Approvers";
	static final String STRING_MESSAGE = "message";
	static final String STATUS_INFO = "info";
	static final String STRING_STATUS = "status";
	static final String STRING_TREE = "tree";
	static final String STRING_FILE = "File";
	static final String STRING_POC_DATA = "POCData";
	static final String STRING_PART_DATA = "PartData";
	static final String STRING_CLAIM = "Claim";
	static final String STRING_KO = "KO";
	static final String STRING_TRUE = "true";
	static final String STRING_DATA = "data";
	static final String STRING_OK = "OK";	
	static final String CONSTANT_STRING_COMMA = ",";
	static final String STRING_APPROVED = "Approved";
	static final String STRING_COLLABORATION = "Collaboration";
	static final String STRING_CO_OWNED = "Co-owned";
	static final String STRING_REVIEW = "Review";
	static final String STRING_OWNED = "Owned";
	static final String STRING_CLAIM_ID = "Claim Id";
	static final String STRING_IMAGE_DATA = "imageData";
	static final String STRING_HTML_BREAK_LINE = "<br/>";
	static final String SELECT_PHYSICALID = "physicalid";
	static final String SELECT_TASK_ID = "from[Object Route].to.to[Route Task].from.id";
	static final String SELECT_ROUTE_ID = "from[Object Route].to.id";
	static final String SELECT_ROUTE_CURRENT = "from[Object Route].to.current";
	static final String CONST_PACKAGING_ARTWORK = "Packaging Artwork";
	static final String STR_COLON = ":";
	static final String CONST_NEW_LINE = "\n";
	static final String FROM_PG_CLAIMS_ID = "from[pgClaims].to.id";
	static final String FROM_PG_CLAIMS_TO_NAME = "from[pgClaims].to.name";
	static final String FROM_PG_CLAIMS_TO_CURRENT = "from[pgClaims].to.current";
	static final String FROM_PG_CLAIMS_TO_REVISION = "from[pgClaims].to.revision";
	static final String FROM_PG_CLAIMS_TO_TYPE = "from[pgClaims].to.type";
	static final String FROM_PG_CLAIMS_TO_PHYSICALID = "from[pgClaims].to.physicalid";
	static final String FROM_PG_CLAIMS_TO_CLAIM_RTE = "from[pgClaims].to.attribute[pgClaimName_RTE]";
	static final String FROM_PG_CLAIMS_TO_DISCLAIMER_RTE = "from[pgClaims].to.attribute[pgDisclaimer_RTE]";
	static final String VAULT_ESERVICE_PRODUCTION = "eService Production";
	static final String FROM_ACTIVE_VERSION_TO_TITLE="from[Active Version].to.attribute[Title]";
	//DCM(DS) 2022x-04 - Added for MS Files Preview - START
	static final String FROM_ACTIVE_VERSION_TO_DESCRIPTION="from[Active Version].to.description";
	//DCM(DS) 2022x-04 - Added for MS Files Preview - END
	static final String FROM_ACTIVE_VERSION_TO_ORIGINATED="from[Active Version].to.originated";
	static final String FROM_ACTIVE_VERSION_TO_OWNER="from[Active Version].to.owner";
	static final String FROM_ACTIVE_VERSION_TO_ID="from[Active Version].to.id";
	static final String FROM_ACTIVE_VERSION_TO_FILE_SIZE="from[Active Version].to.attribute[File Size]";
	static final String FROM_ACTIVE_VERSION_TO_FILE_FORMAT="from[Active Version].to.default";
	static final String SELECT_ASSIGNMENTS_FUNCTION = "from[pgAssignments].attribute[pgAssignmentsFunction]";
	static final String SELECT_ASSIGNMENTS_COLLABORATION_STATUS = "from[pgAssignments].attribute[pgCollaborationStatus]";
	static final String SELECT_ASSIGNMENTS_TO_NAME = "from[pgAssignments].to.name";
	static final String SELECT_FROM_PG_CLAIM_SUPPORT_TO_ID = "from[pgClaimSupport].to.id";
	//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
	static final String SELECT_FROM_PG_CLAIM_SUPPORT_TO_REVISION= "from[pgClaimSupport].to.revision";
	static final String SELECT_FROM_PG_CLAIM_SUPPORT_TO_OWNER = "from[pgClaimSupport].to.owner";
	static final String SELECT_FROM_PG_CLAIM_SUPPORT_REL_ID = "from[pgClaimSupport].id";
	//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - START
	static final String SELECT_FROM_PG_CLAIM_SUPPORT = "from[pgClaimSupport]";
	static final String SELECT_FROM_PG_CLAIM_SUPPORT_TO = "from[pgClaimSupport].to";
	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - END
	static final String FROM_OBJECT_ROUTE_TO_FROM_ROUTE_NODE_TO_NAME = "from[Object Route].to.from[Route Node].to.name";
	static final String KEY_CONTENT_ID = "ContentId";
	static final String SELECT_FROM_ClAIM_MEDIA_TO_ID = "from[pgClaimMedia].to.id";	
	//Added for Claim Media upload issue - START
	static final String SELECT_TO_ClAIM_MEDIA_FROM = "to[pgClaimMedia].from";
	//Added for Claim Media upload issue - END
	static final String PERSON_USER_AGENT = "person_UserAgent";
	static final String VAULT_PRODUCTION = "eService Production";
	static final String STRING_RELNAME = "relName";
	static final String STRING_HISTORY = "History";
	static final String STRING_PR_ASSIGNEE = "PR Assignee";
	static final String STRING_RELATIONSHIP = "relationship";
	//Added for Replace with latest revision not replacing further revisions - START
	static final String SELECT_LAST_ID = "last.id";
	static final String SELECT_LAST_CURRENT = "last.current";
	//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
	static final String SELECT_CLAIMIMAGE_CLAIMREQUESTID = "to[pgClaimImage].from.to[pgClaimRequestImage].from.id";
	//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END
	static final String SELECT_CLAIMIMAGE_RELID = "to[pgClaimImage].id";
	//Added for Replace with latest revision not replacing further revisions - END
	private static final String CONST_STR_PIPE = "|";
	private static final String SELECT_TO_PROTECTED_ITEM_FROM_NAME = "to[Protected Item].from.name";
	private static final String FROM_PG_CLAIMS_CURRENT = "from[pgClaims].to.current";
	private static final String SELECT_TO_PROTECTED_ITEM_FROM_ID = "to[Protected Item].from.id";
	private static final String SELECT_TO_PROTECTED_ITEM = "to[Protected Item].id";
	private static final String SELECT_ASSIGNMENTS_ROLE = "from[pgAssignments].attribute[pgAssignmentsRole]";
	private static final String SELECT_TASK_DUEDATE = "from[Object Route].to.to[Route Task].from.attribute[Scheduled Completion Date]" ;
	private static final String SELECT_TASK_COMMENTS = "from[Object Route].to.to[Route Task].from.attribute[Comments]";
	private static final String SELECT_TASK_INSTRUCTIONS = "from[Object Route].to.to[Route Task].from.attribute[Route Instructions]";
	private static final String SELECT_TASK_STATUS = "from[Object Route].to.to[Route Task].from.attribute[Approval Status]" ;
	private static final String SELECT_TASK_ASSIGNEE_NAME = "from[Object Route].to.to[Route Task].from.from[Project Task].to.name" ;
	private static final String SELECT_TASK_NAME = "from[Object Route].to.to[Route Task].from.name";
	private static final String SELECT_ROUTE_STATUS = "from[Object Route].to.to[Route Task].to.attribute[Route Status]";
	private static final String SELECT_ROUTE_TASK_STATUS = "from[Object Route].to.to[Route Task].from.current";
	private static final String SELECT_CLAIMMEDIA_DESCRIPTION = "from[pgClaimMedia].to.description";
	private static final String SELECT_CLAIMMEDIA_PHYSICALID = "from[pgClaimMedia].to.from[Active Version].to.physicalid";
	private static final String SELECT_CLAIMMEDIA_TITLE = "from[pgClaimMedia].to.from[Active Version].to.attribute[Title]";
	private static final String SELECT_CLAIMMEDIA_SEQUENCE = "from[pgClaimMedia].to.attribute[Sequence]";
	private static final String SELECT_CLAIMMEDIA_MEDIA_CONTENT = "from[pgClaimMedia].to.attribute[pgMediaContent]";
	private static final String SELECT_CLAIMMEDIA_COMMENTS = "from[pgClaimMedia].to.attribute[Comments]";
	private static final String SELECT_VERSION_PHYSICALID = "from[Active Version].to.physicalid";
	//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
	private static final String SELECT_CLAIMIMAGE_REL_ID = "from[pgClaimImage].id";
	private static final String SELECT_CLAIMIMAGE_CLAIM_ID = "from[pgClaimImage].to.id";

	private static final String SELECT_CLAIMIMAGE_TO = "from[pgClaimImage].to.";
	private static final String SELECT_CLAIMIMAGE_CLAIM_CURRENT = "from[pgClaimImage].to.current";

	//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END
	private static final String SELECT_CLAIMREQUEST_IMAGE_ID = "from[pgClaimRequestImage].to.id";
	private static final String SELECT_CLAIM_DISCLAIMER_ID = "from[pgDisclaimer].to.id";
	//DCM (DS) - US 1609 - Security inheritance on Disclaimer - START
	private static final String SELECT_CLAIM_DISCLAIMER_CURRENT = "from[pgDisclaimer].to.current";
	//DCM (DS) - US 1609 - Security inheritance on Disclaimer - END
	private static final String TO_PG_MASTER_COPY_CLAIM = "to[pgMasterCopyClaim]";
	private static final String TO_PG_MASTER_COPY_CLAIM_FROM_NAME = "to[pgMasterCopyClaim].from.name";
	private static final String TO_PG_MASTER_COPY_CLAIM_FROM_REV = "to[pgMasterCopyClaim].from.revision";
	private static final String TO_IMAGE_HOLDER_FROM_ID = "to[Image Holder].from.id";
	private static final String FROM_ARTWORK_ELEMENT_CONTENT_TO_ATTRIBUTE_IS_BASE_COPY = "from[Artwork Element Content].to.attribute[Is Base Copy]";
	private static final String FROM_ARTWORK_ELEMENT_CONTENT_TO_ATTRIBUTE_COPY_TEXT_RTE = "from[Artwork Element Content].to.attribute[Copy Text_RTE]";

	private static final String SELECT_TO_PG_DISCLAIMER_FROM_REVISION = "to[pgDisclaimer].from.revision";
	private static final String SELECT_TO_PG_DISCLAIMER_FROM_NAME = "to[pgDisclaimer].from.name";
	private static final String SELECT_TO_PG_DISCLAIMER_FROM_CURRENT = "to[pgDisclaimer].from.current";
	private static final String SELECT_TO_PG_DISCLAIMER_FROM_ID = "to[pgDisclaimer].from.id";

	private static final  String PG_CLAIM_PRODUCT_CONFIGURATION = "pgClaimProductConfiguration";
	private static final  String SELECT_FROMMID_PG_CLAIM_PRODUCT_CONFIGURATION_TO_ID = "frommid[pgClaimProductConfiguration].to.id";
	//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates :Start
	private static final String FROMMID_PG_CLAIM_PRODUCT_CONFIGURATION_TO_ID = "frommid[pgClaimProductConfiguration].to.id";
	private static final String FROMMID_PGCLAIMPRODUCTCONFIG_TO = "frommid[pgClaimProductConfiguration].to.";
	//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates :End
	//DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - Start
	private static final String SELECT_PRODUCTCONFIG_PANEL_LOCATION = "frommid[pgClaimProductConfiguration].to.attribute[pgPanelLocation]";
	private static final String SELECT_TO_DISCLAIMERNAME_RTE = "to.attribute[pgDisclaimerName_RTE]";
	private static final String SELECT_TO_CLAIMNAME_RTE = "to.attribute[pgClaimName_RTE]";
	private static final String SELECT_TO_CLAIM_IMAGE_ID = "to.to[pgClaimImage].id";
	private static final String SELECT_CLAIMIMAGE_CLAIMS_ID = "from[pgClaimImage].to.to[pgClaims].id";
	private static final String SELECT_CLAIMIMAGE_DISCLAIMER_ID = "from[pgClaimImage].to.to[pgDisclaimer].id";
	//DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - End
	private static final List<String> LIST_FILE_IMAGE_FILE_FORMAT = Arrays.asList("gif","jpg","png","giff","jpeg","jfif","GIF","JPG","PNG","GIFF","JPEG","JFIF");

	private static long startTime = 0;
	private static final String DENIED = "#DENIED!";
	private static final String FIRST_REV = "001";
	private static final String STRING_NONE = "None";
	private static final String STRING_DISCLAIMERS = "Disclaimers";
	private static final String STRING_CLONE = "clone";
	private static final String STRING_DISCLAIMER_MASTER_COPY = "Disclaimer Master Copy";
	private static final String STRING_REVISE = "revise";
	//TYPE CONSTANTS
	static final String TYPE_PG_CLAIM_MEDIA = "pgClaimMedia";
	static final String TYPE_TASK = "Task";
	static final String TYPE_PG_CLAIM_SUPPORT = "pgClaimSupport";
	static final String TYPE_PG_CLAIM_REQUEST = "pgClaimRequest";
	static final String TYPE_PG_CLAIM = "pgClaim";
	static final String TYPE_SECURITY_CONTROL_CLASS = "Security Control Class";
	static final String TYPE_IP_CONTROL_CLASS = "IP Control Class";
	static final String TYPE_PART = "Part";
	static final String TYPE_MASTER_COPY_ELEMENT = "Master Copy Element";
	static final String TYPE_PG_IRM_DOCUMENT = "pgIRMDocument";
	static final String TYPE_PG_DISCLAIMER = "pgDisclaimer";
	//RELATIONSHIP CONSTANTS
	private static final String RELATIONSHIP_PG_CLAIM_REQUEST_IMAGE = "pgClaimRequestImage";
	private static final String RELATIONSHIP_PG_CLAIM_IMAGE = "pgClaimImage";
	static final String RELATIONSHIP_PG_CLAIM_SUPPORT = "pgClaimSupport";
	static final String RELATIONSHIP_ASSIGNMENTS = "pgAssignments";
	static final String RELATIONSHIP_CLAIMS = "pgClaims";
	static final String RELATIONSHIP_CLAIMREQUESTS = "pgClaimRequests";
	static final String RELATIONSHIP_DERIVED  = "Derived";
	static final String RELATIONSHIP_PG_CLAIM_RELATED_PART = "pgClaimRelatedPart";
	static final String RELATIONSHIP_PG_CLAIMSUPPORT_REFERENCE = "pgClaimSupportReference";	
	static final String RELATIONSHIP_CLAIM_MEDIA = "pgClaimMedia";
	static final String RELATIONSHIP_PG_CLAIMSUPPORT_RELATED_PART = "pgClaimSupportRelatedPart";
	static final String RELATIONSHIP_PG_MASTER_COPY_CLAIM = "pgMasterCopyClaim";
	static final String RELATIONSHIP_PG_DISCLAMER = "pgDisclaimer";
	//POLICY CONSTANTS
	static final String POLICY_PG_DISCLAIMER = "pgDisclaimer";
	//US- 1608 - Disclaimer Data Model changes - Create changes Start
	static final String POLICY_PG_CLAIM = "pgClaim";
	//US- 1608 - Disclaimer Data Model changes - Create changes End
	//STATE CONSTANTS
	static final String STATE_PRELIMINARY = "Preliminary";
	private static final String STATE_OBSOLETE = "Obsolete";
	private static final String STATE_RELEASED = "Released";
	private static final String STATE_APPROVED = "Approved";
	private static final String STATE_COLLABORATION = "Collaboration";
	private static final String IS_EDITABLE = "isEditable";
	private static final String STATE_VERSION = "Version";
	private static final String STATE_AWAITING_APPROVAL = "Awaiting Approval";
	private static final String STATE_REJECTED = "Rejected";
	private static final String STATE_STARTED = "Started";
	private static final String STATE_STOPPED = "Stopped";
	private static final String STATE_APPROVE = "Approve";
	private static final String STATE_REJECT = "Reject";
	private static final String FUNCTIONAL_APPROVAL = "Functional Approval";
	private static final String MARKETING_APPROVAL = "Marketing Approval";
	private static final String FINAL_APPROVAL = "Final Approval";
	//ATTRIBUTE CONSTANTS
	private static final String ATTRIBUTE_PG_INTENDED_MARKETS = "pgClaimIntendedMarkets";
	private static final String ATTRIBUTE_EMPLOYEE_TYPE = "pgSecurityEmployeeType";
	private static final String ATTRIBUTE_COPY_ELEMENT_TYPE = "pgCopyElementType";
	private static final String ATTRIBUTE_CLAIMNAME_RTE = "pgClaimName_RTE";
	private static final String ATTRIBUTE_SEQUENCE = "Sequence";
	private static final String ATTRIBUTE_MEDIA_CONTENT = "pgMediaContent";
	private static final String ATTRIBUTE_COMMENTS = "Comments";
	private static final String ATTRIBUTE_ASSIGNMENTS_ROLE = "pgAssignmentsRole";
	private static final String ATTRIBUTE_ASSIGNMENTS_FUNCTION = "pgAssignmentsFunction";
	private static final String ATTRIBUTE_DISCLAIMERNAME_RTE= "pgDisclaimerName_RTE";
	private static final String ATTRIBUTE_CLAIM_PANEL_LOCATION = "pgPanelLocation";
	private static final String ATTRIBUTE_BRAND = "pgClaimBrand";
	private static final String ATTRIBUTE_EXECUTION_TYPE = "pgExecutionType";
	private static final String ATTRIBUTE_PGCLAIMSEQUENCE = "pgClaimSequence";
	private static final String ATTRIBUTE_PG_COLLABORATION_STATUS = "pgCollaborationStatus";
	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - START
	private static final String ATTRIBUTE_PG_BENEFIT = "pgBenefit";
	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - END
	//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - START
	private static final String ATTRIBUTE_EXPIRATION_DATE = "Expiration Date";
	private static final String ATTRIBUTE_PG_EXPIRY_CADENCE= "pgClaimExpiryCadence";
	//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - END
	private static final String SCHEMA_TYPE = "Type";

	//Added for Route Service - START
	static final String TASK_ID_KEY = "taskId";

	static final String JSON_OUTPUT_KEY_FALSE = "false";
	static final String JSON_OUTPUT_KEY_TRUE = "true";

	static final String TASK_APPROVAL_STATUS_APPROVE = "Approve";
	static final String TASK_APPROVAL_STATUS_REJECT = "Reject";
	static final String TASK_APPROVAL_STATUS_ABSTAIN = "Abstain";

	static final String ACTION_COMPLETE = "Complete";
	static final String ACTION_APPROVE = "Approve";

	static final String GET_COMMENT_FROM_TASK_ID = "getCommentsFromTaskId";
	static final String TRUE_VALUE = "true";

	static final String SHOWFDA = "showFDA"; 

	static final String JSON_OUTPUT_KEY_FLAG = "flag";
	static final String JSON_OUTPUT_KEY_NOT_ROUTE_ID = "routeId";
	static final String JSON_OUTPUT_KEY_TASK_STATUS = "taskStatus";

	static final String GRANTOR = "grantor";
	static final String GRANTEE = "grantee";
	static final String GRANTEEACCESS = "granteeaccess";

	static final String OBJECT_ID = "objectId";
	private static final String CONST_WHITE_SPACE = " ";
	private static final String CONST_HIPHEN = "-";
	static final String COMMENTS = "comments";
	static final String FAILURE = "Failure";
	static final String OFFSET_FROM_TASK_CREATE_DATE     = "Task Create Date";
	static final String NO_VALUE = "No";
	static final String YES_VALUE = "Yes";
	static final String ALL_VALUE = "All";
	static final String MQL_ADD_HISTORY_QUERY = "Modify bus $1 add history $2 comment $3";
	static final String APPROVE = "approve";
	static final String MQL_REVOKE_GRANTOR_GRANTEE = "modify bus $1 revoke grantor $2 grantee $3";

	static final String FLAG_FDA = "fda";
	static final String RETURN_BACK = "returnBack";
	static final String LANGUAGE_STR = "languageStr";
	static final String LOCALE_OBJ = "localeObj";
	static final String TIME_ZONE = "timeZone";
	static final String REVIEWER_COMMENTS = "ReviewerComments";
	static final String DUE_DATE = "DueDate";
	static final String ROUTE_TIME = "routeTime";
	static final String SUCESS_VALUE = "Success";
	static final String REQUEST_MAP = "requestMap";
	//Added for Route Service - END

	static final String HIDDEN_SEPERATOR = "&#8205;";

	private static final String STATE_PRELIMINARY_OWNED = "Preliminary - Owned";
	private static final String STATE_PRELIMINARY_CO_OWNED = "Preliminary - Coowned";
	//DCM(DS) 2022x-04 - Added for MS Files Preview - START
	static final String SELECT_MEDIA_ID = "MediaId";
	//DCM(DS) 2022x-04 - Added for MS Files Preview - END

	static final String FILESIZE="format.file.size";
	static final String FILENAME="format.file";
	static final String FROM_ACTIVE_VERSION_TO_PHYSICAL_ID="from[Active Version].to.physicalid";

	//DCM (DS) User Story 457 - Claim User/Originator - VIEW files instead of downloading them - Start
	public static final String CONST_VERSION = "Version"; 
	public static final String CONST_SYSTEM_GENERATED_PDF = "SystemGeneratedPDF";
	public static final String STRING_FILEID = "fileId";
	public static final String STRING_MASTERID = "masterId";
	public static final String STRING_ISLATESTREVISION = "isLatestRevision";
	static final String FROM_LATESTVERSION_TO_ID = "from[Latest Version].to.id";
	static final String FROM_LATESTVERSION_TO_TITLE = "from[Latest Version].to.attribute[Title]";
	static final String CONST_DOCUMENT_OWNER = "DocumentOwner";
	//DCM (DS) User Story 457 - Claim User/Originator - VIEW files instead of downloading them - End

	//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - Start
	static final String LOGICAL_ID = "LogicalId";
	//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - End
	//DCM Sprint 7 US-2624:Enable related parts at product configuration level instead at claim level : Start
	static final String SELECT_PRODUCTCONF_RELATEDPART_ID = "frommid[pgClaimProductConfiguration].to.to[pgClaimRelatedPart].from.id";
	static final String SELECT_PRODUCTCONF_RELATEDPART_NAME = "frommid[pgClaimProductConfiguration].to.to[pgClaimRelatedPart].from.name";
	static final String SELECT_PRODUCTCONF_RELATEDPART_REVISION = "frommid[pgClaimProductConfiguration].to.to[pgClaimRelatedPart].from.revision";
	static final String SELECT_PRODUCTCONF_RELATEDPART_REL_ID = "frommid[pgClaimProductConfiguration].to.to[pgClaimRelatedPart].id";
    //DCM Sprint 7 US-2624:Enable related parts at product configuration level instead at claim level : End
	
	/**Creates the Claim and connects the related objects with the created Claim
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws MatrixException
	 */
	Response createClaim(Context context, Map<String,Object> mpRequestMap) throws MatrixException 
	{
		Map<String,Object> mpAttribute =(Map<String,Object>) mpRequestMap.get("attribute");
		String strType = (String) mpRequestMap.get("type");
		String strPolicy =(String) mpRequestMap.get("policy");
		String strDescription =(String) mpRequestMap.get("description");
		String strCreateAndConnect = (String)mpRequestMap.get("createAndConnect");
		String strClaimId =(String) mpRequestMap.get("claimId");
		String strSourceId =(String) mpRequestMap.get("sourceId");
		String strevent =(String) mpRequestMap.get("event");
		String strNumberOfCopy =(String) mpRequestMap.get("numberOfCopy");
		ArrayList<String> alDisclaimers = (ArrayList<String>) mpRequestMap.get("disclaimers");
		String strRelId;

		JsonObjectBuilder output = Json.createObjectBuilder();
		try 
		{
			//DCM (DS) 2022x-02 CW - REQ 46293 - Allow multiple claims in create Disclaimer - START
			StringList slParentIds = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strClaimId))
			{
				slParentIds = StringUtil.split(strClaimId, CONSTANT_STRING_COMMA);
			}
			//DCM (DS) 2022x-02 CW - REQ 46293 - Allow multiple claims in create Disclaimer - END
			if(UIUtil.isNotNullAndNotEmpty(strevent) && strevent.equalsIgnoreCase(STRING_COPY))
			{
				String sbCopyResponse = null;
				//DCM (DS) 2022x-02 CW - REQ 44028 - Copy Disclaimer - START
				int iNumberOfCopy = 1; 
				if(UIUtil.isNotNullAndNotEmpty(strNumberOfCopy))
				{
					iNumberOfCopy=Integer.parseInt(strNumberOfCopy);  
				}
				//DCM (DS) 2022x-02 CW - REQ 44028 - Copy Disclaimer - END
				for(int i=0; i<iNumberOfCopy; i++)
				{
					//DCM (DS) 2022x-02 CW - REQ 44028 - Copy Disclaimer - START
					for(int j=0; j<slParentIds.size(); j++)
					{
						sbCopyResponse = cloneObject(context, strSourceId, slParentIds.get(j), mpAttribute);
					}
					//DCM (DS) 2022x-02 CW - REQ 44028 - Copy Disclaimer - END
				}
				return Response.status(HttpServletResponse.SC_OK).entity(sbCopyResponse).build();
			}
			Map mpConnection = (Map<String,Object>) mpRequestMap.get("connection");
			String strRelationship = (String) mpRequestMap.get(STRING_RELATIONSHIP);

			String strOutputMsg = STRING_OK;
			if(RELATIONSHIP_ASSIGNMENTS.equals(strRelationship))
			{
				addAssignments(context, mpRequestMap, output);
			}
			else if(DomainConstants.RELATIONSHIP_PROTECTED_ITEM.equals(strRelationship))
			{
				String res = addSecurityClassification(context, mpRequestMap);
				if(UIUtil.isNotNullAndNotEmpty(res))
				{
					return Response.status(HttpServletResponse.SC_OK).entity(res).build();
				}
			}
			else
			{
				//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - START
				AttributeList attrMdtryList = new AttributeList();
				AttributeList attrNonMdtryList = new AttributeList();
				//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - END
				//DCM (DS) 2022x-01 CW - Modified for Multiple Disclaimer create - START
				DomainObject domNewObj = DomainObject.newInstance(context);
				String strName = DomainConstants.EMPTY_STRING;
				if(!(TYPE_PG_DISCLAIMER.equals(strType) && alDisclaimers!=null && !alDisclaimers.isEmpty()))
					//DCM (DS) 2022x-01 CW - Modified for Multiple Disclaimer create - END
				{
					strName = (String) mpRequestMap.get("Name");
					if(UIUtil.isNullOrEmpty(strName))
					{
						String strSymbolicName = FrameworkUtil.getAliasForAdmin(context, "Type",strType,false);
						String sObjGeneratorName = UICache.getObjectGenerator(context, strSymbolicName, "");
						strName = DomainObject.getAutoGeneratedName(context, sObjGeneratorName, "");
					}

					domNewObj.createObject(context, strType, strName, FIRST_REV, strPolicy,VAULT_ESERVICE_PRODUCTION);
					output.add(STRING_DATA, strName);

					if(null!=mpAttribute)
					{
						//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - START
						StringList slMandatoryAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM."+ strType.replace("pg", "") +".MandatoryAttributes"), CONSTANT_STRING_COMMA);
						//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - END
						AttributeType attrType = null;
						Attribute attr = null;
						StringList value;
						for(String key :mpAttribute.keySet())
						{
							attrType = new AttributeType(key);
							if(mpAttribute.get(key) instanceof String)
							{
								attr = new Attribute(attrType, (String) mpAttribute.get(key));
							}
							else
							{
								value = new StringList();
								value.addAll((ArrayList<String>)mpAttribute.get(key));
								attr = new Attribute(attrType,value );
							}
							//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - START
							if(slMandatoryAttrList.contains(key))
							{
								attrMdtryList.add(attr);
							}
							else {
								attrNonMdtryList.add(attr);
							}
						}
						//Set Mandatory attributes
						domNewObj.setAttributeValues(context, attrMdtryList);
						//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - END
					}
					domNewObj.setDescription(context, strDescription);
					if(null != mpConnection && (!mpConnection.isEmpty()))
					{
						strOutputMsg = connectOtherData(context,mpConnection,domNewObj.getId(context));
					}
				}
				//DCM (DS) 2022x-02 CW - REQ 46293 - Allow multiple claims in create Disclaimer - START
				if(BusinessUtil.isNotNullOrEmpty(slParentIds) && STRING_TRUE.equalsIgnoreCase(strCreateAndConnect))
				{
					for(String slParentId : slParentIds)
					{
						DomainObject doParentObj = DomainObject.newInstance(context,slParentId);
						//DCM (DS) 2022x-02 CW - REQ 46293 - Allow multiple claims in create Disclaimer - END				
						if(TYPE_PG_CLAIM.equals(strType) && doParentObj.isKindOf(context, TYPE_PG_CLAIM_REQUEST))
						{
							//US 1608: Disclaimer Data Model changes - Create changes :Start
							//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
							Map<String, Object> mapMethodParameters = new HashMap<>();
							mapMethodParameters.put("doParentObj", doParentObj);
							mapMethodParameters.put("doNewObj", domNewObj);
							mapMethodParameters.put("strRelName", RELATIONSHIP_CLAIMS);
							mapMethodParameters.put("strType", strType);
							mapMethodParameters.put("strName", strName);
							mapMethodParameters.put("bCopyIPClass", true);
							mapMethodParameters.put("mpRelAttributes", mpRequestMap.get("relAttribute"));
							strRelId = connectAfterCreate(context, mapMethodParameters);
							createClaimProductConfiguration(context, strRelId, slParentId, strType, strName);
							//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
							//US 1608: Disclaimer Data Model changes - Create changes :End
						}
						//DCM (DS) 2022x-02 CW - REQ 46293 - Allow multiple claims in create Disclaimer - START
					}
					//DCM (DS) 2022x-02 CW - REQ 46293 - Allow multiple claims in create Disclaimer - END
				}
				//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - START
				if(domNewObj.exists(context))
				{
					domNewObj.setAttributeValues(context, attrNonMdtryList);
				}
				//DCM (DS) 2022x-01 CW - REQ 45349 - Segregating mandatory and temporary attributes set - END
				if(UIUtil.isNotNullAndNotEmpty(strOutputMsg))
				{
					output.add(STRING_MESSAGE, strOutputMsg);
				}
			}
			//add MQL notice if any
			createErrorMessage(context, output);
		} catch (Exception e) {
			//DCM (DS) 2022x-01 CW - REQ 45666 - To send MQL error on Widget UI - START
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			createErrorMessage(context, output);
			//DCM (DS) 2022x-01 CW - REQ 45666 - To send MQL error on Widget UI - END
		}
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}

	/**
	 * This method adds the Persons to Context object with Assignments relationship
	 * @param context
	 * @param mpRequestMap
	 * @param output
	 * @throws Exception
	 */
	public void addAssignments(Context context, Map<String,Object> mpRequestMap, JsonObjectBuilder output) throws Exception
	{
		String strClaimId =(String) mpRequestMap.get("claimId");
		Map<String,Object> mpAttribute =(Map<String,Object>) mpRequestMap.get("attribute");
		if(UIUtil.isNotNullAndNotEmpty(strClaimId))
		{
			String strOutputMsg = validatePersonForNotEBPAndClaimUser(context,mpRequestMap);
			String strPersonId =(String) mpRequestMap.get("personId");
			String strPersonName = (String) mpRequestMap.get("personName");
			if(strOutputMsg.equals(STRING_OK))
			{
				DomainObject domObj=DomainObject.newInstance(context, strClaimId);
				if(UIUtil.isNotNullAndNotEmpty(strPersonId))
				{
					//DCM(DS) 2022x-01 CW - REQ 45599 - Changed for Assignments Tab changes
					StringList slPersonIds = StringUtil.split(strPersonId, ",");
					StringList slPersonNames = StringUtil.split(strPersonName, ",");
					StringList slPersonsAdded = new StringList(); 
					String strPersonsAdded = null;

					for(int i=0; i<slPersonIds.size(); i++)
					{
						DomainRelationship domRel = domObj.addToObject(context,new RelationshipType(RELATIONSHIP_ASSIGNMENTS),slPersonIds.get(i));
						if(null != mpAttribute)
						{
							domRel.setAttributeValues(context, mpAttribute);
							slPersonsAdded.add(slPersonNames.get(i));
							//Adding PR assignee as co owner
							String strAssignmentRole = (String) mpAttribute.get(ATTRIBUTE_ASSIGNMENTS_ROLE);
							if(UIUtil.isNotNullAndNotEmpty(strAssignmentRole) && "PR".equals(strAssignmentRole))
							{
								addCoOwner(context, strClaimId, slPersonIds.get(i), STRING_PR_ASSIGNEE);
							}
						}
					}
					strPersonsAdded = slPersonsAdded.toString().replace("[",DomainConstants.EMPTY_STRING).replace("]",DomainConstants.EMPTY_STRING);
					output.add(STRING_DATA, "Assigment with "+ strPersonsAdded);
				}
			}
			output.add(STRING_MESSAGE, strOutputMsg);
		}
	}

	/**
	 * This method connects the input IP Classes to Context object
	 * @param context
	 * @param mpRequestMap
	 * @return 
	 * @throws Exception
	 */
	public String addSecurityClassification(Context context, Map<String,Object> mpRequestMap) throws Exception
	{
		String output = null;
		String strClaimId =(String) mpRequestMap.get("claimId");
		Map mpConnection = (Map<String,String>) mpRequestMap.get("connection");
		if(null != mpConnection)
		{
			JsonObjectMaker jObject = new JsonObjectMaker();
			String strTypePattern = new StringBuilder(TYPE_IP_CONTROL_CLASS).append(CONSTANT_STRING_COMMA).append(TYPE_SECURITY_CONTROL_CLASS).toString();
			jObject.add(PGWidgetConstants.KEY_OBJECT_ID, strClaimId);
			String strIPClass =(String) mpConnection.get(DomainConstants.RELATIONSHIP_PROTECTED_ITEM);
			StringList slSelect = new StringList(); 
			slSelect.addElement(DomainConstants.SELECT_ID);
			MapList mlObjs = DomainObject.findObjects(context,	// eMatrix context
					strTypePattern,							// type pattern
					strIPClass,                                 // name pattern    
					DomainConstants.QUERY_WILDCARD,             // revision pattern
					DomainConstants.QUERY_WILDCARD,             // owner pattern   
					VAULT_ESERVICE_PRODUCTION,                  // Vault Pattern	
					DomainConstants.EMPTY_STRING,               // where expression
					true,                                       // expand type     
					slSelect);									// object selects

			StringList slObj = new StringList();
			if(BusinessUtil.isNotNullOrEmpty(mlObjs))
			{
				for(int i=0;i<mlObjs.size();i++)
				{
					slObj.add((String)((Map)mlObjs.get(i)).get(DomainConstants.SELECT_ID));
				}							
				jObject.add(KEY_CONTENT_ID,PGWidgetUtil.getStringFromSL(slObj,CONSTANT_STRING_COMMA));
			}	
			if(BusinessUtil.isNotNullOrEmpty(slObj))
			{
				output = addObject(context, jObject.toString());	
			}
		}
		return output;
	}

	/**
	 * This methods connects the two input objects with the input relationship
	 * @param context
	 * @param mapMethodParameters
	 * @return String
	 * @throws Exception
	 */
	public String connectAfterCreate(Context context, Map<String, Object> mapMethodParameters) throws Exception
	{
		DomainObject doParentObj = (DomainObject) mapMethodParameters.get("doParentObj");
		DomainObject doNewObj =  (DomainObject) mapMethodParameters.get("doNewObj");
		String strRelName =  (String) mapMethodParameters.get("strRelName");
		String strType = (String) mapMethodParameters.get("strType");
		String strName = (String) mapMethodParameters.get("strName");
		boolean bCopyIPClass = (boolean) mapMethodParameters.get("bCopyIPClass");
		Map<String, Object> mpRelAttributes = (Map<String, Object>) mapMethodParameters.get("mpRelAttributes");
		DomainRelationship domRel = DomainRelationship.connect(context, doParentObj, strRelName, doNewObj);
		
		//DCM (DS) US-1596 Claim Data Model Changes - Create Screen - Rel Attributes start
		if(null != mpRelAttributes)
		{
			mpRelAttributes = getValidAttributeMap(mpRelAttributes);
			domRel.setAttributeValues(context, mpRelAttributes);
			//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
			Map<String, Object> mpEmptyAttributeMap = new HashMap<>();
			copyMapWithEmptyValues(mpEmptyAttributeMap, mpRelAttributes);
			strType = EnoviaResourceBundle.getAdminI18NString(context,SCHEMA_TYPE, strType, context.getSession().getLanguage());
			updateHistoryOnBaseObject(context, mpRelAttributes, mpEmptyAttributeMap, new StringBuilder(strType).append(strName).toString(), doParentObj.getObjectId(context));
			//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
		}
		//DCM (DS) US-1596 Claim Data Model Changes - Create Screen - Rel Attributes -End
		//For stamping Claim Request ownership on claim
		//To change created Claim owner to Claim Request owner
		String strCtxUser = context.getUser();
		String strParObjOwner = doParentObj.getInfo(context, DomainConstants.SELECT_OWNER);
		if(UIUtil.isNotNullAndNotEmpty(strParObjOwner) && UIUtil.isNotNullAndNotEmpty(strCtxUser) && !strParObjOwner.equals(strCtxUser))
		{
			//DCM (DS) 2022x-02 CW - Add ownership to CLR owner on CLM - START
			String strPersonId = PersonUtil.getPersonObjectID(context, strParObjOwner);
			addCoOwner(context, doNewObj.getId(context), strPersonId, "Parent Owner");
			//DCM (DS) 2022x-02 CW - Add ownership to CLR owner on CLM - END
		}
		if(bCopyIPClass)
		{
			copyIPClassificationOnClaim(context,doParentObj,doNewObj);
		}
		return domRel.toString();
	}

	/**This Method is use to valid Person is not EBP and Claim User
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception
	 */
	private String validatePersonForNotEBPAndClaimUser(Context context,Map mpRequestMap) throws Exception {
		String strOutputMsg = STRING_OK;
		String strPersonId =(String) mpRequestMap.get("personId");
		String strPersonName = (String) mpRequestMap.get("personName");
		//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - START
		boolean bAuthorCheck = mpRequestMap.containsKey(STRING_CHECK_AUTHOR_ROLE) && (boolean) mpRequestMap.get(STRING_CHECK_AUTHOR_ROLE);
		String strRoleToCheck = ROLE_PG_CLAIM_USER;
		String strBlockMsg = "emxFramework.ClaimManager.NotClaimUser";
		if(bAuthorCheck)
		{
			strRoleToCheck =  ROLE_PG_CLAIM_AUTHOR ;
			strBlockMsg = "emxFramework.ClaimManager.NotClaimAuthor";
		}
		//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - END
		//DCM(DS) 2022x-01 CW - REQ 45599 - Changed for Multi person Assignment - START
		StringList slPersonId = new StringList();
		StringList slPersonName = new StringList();
		if(UIUtil.isNotNullAndNotEmpty(strPersonId) && strPersonId.contains(","))
		{
			slPersonId = StringUtil.split(strPersonId, ",");
			slPersonName = StringUtil.split(strPersonName, ",");
		} else {
			slPersonId.add(strPersonId);
			slPersonName.add(strPersonName);
		}
		for(int i=0; i<slPersonId.size(); i++)
		{
			//DCM(DS) 2022x-01 CW - REQ 45599 - Changed for Multi person Assignment - END
			Person p = new Person(slPersonName.get(i));
			DomainObject domPerson = DomainObject.newInstance(context,slPersonId.get(i));
			String strEmpType = domPerson.getInfo(context, DomainObject.getAttributeSelect(ATTRIBUTE_EMPLOYEE_TYPE));
			if("EBP".equals(strEmpType))
			{
				strOutputMsg = MessageUtil.getMessage(context, null, "emxFramework.ClaimManager.NotExternalUser", new String[]{slPersonName.get(i)}, null,context.getLocale(), "emxFrameworkStringResource");
			}
			//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - START
			else if(!p.isAssigned(context, strRoleToCheck))
			{
				strOutputMsg = MessageUtil.getMessage(context, null, strBlockMsg, new String[]{slPersonName.get(i)}, null,context.getLocale(), "emxFrameworkStringResource");
				//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - END
			}
			if(!STRING_OK.equals(strOutputMsg))
			{
				break;
			}
		}
		return strOutputMsg;
	}

	/**this Method copy IP Classification of Claim Request on Claim
	 * @param context
	 * @param doParentObj
	 * @param domNewObj
	 * @throws FrameworkException 
	 */
	private void copyIPClassificationOnClaim(Context context, DomainObject doParentObj, DomainObject domNewObj) throws FrameworkException 
	{
		try {
			//PushContext to connect all IP class connected, even operation done by co-owner
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			StringList slClassification= doParentObj.getInfoList(context, SELECT_TO_PROTECTED_ITEM_FROM_ID);
			if(BusinessUtil.isNotNullOrEmpty(slClassification))
			{
				String[] arrClassification = new String[slClassification.size()];
				slClassification.toArray(arrClassification);
				DomainRelationship.connect(context, domNewObj, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, false, arrClassification);
			}
		}
		finally {
			ContextUtil.popContext(context);
		}
	}

	/**connect new Object with other data like classification
	 * @param context
	 * @param mpConnection
	 * @param domNewObj
	 * @throws FrameworkException 
	 */
	private String connectOtherData(Context context, Map<String,Object> mpConnection, String newObjectID) throws FrameworkException {
		String strkey;
		String strValue;
		StringList slObj; 
		StringList slSelect = new StringList(); 
		slSelect.addElement(DomainConstants.SELECT_ID);
		DomainObject domFrom;
		DomainObject domTo;
		StringList slErrorMsgs = new StringList();
		for (Map.Entry<String,Object> entry : mpConnection.entrySet()) 
		{
			strkey =  entry.getKey();
			//strValue = entry.getValue();
			slObj = StringList.asList(((ArrayList<String>)mpConnection.get(strkey))) ;
			//slObj = StringUtil.split(strValue, CONSTANT_STRING_COMMA);
			String strObj;
			MapList mlObjs;
			for(int i=0;i<slObj.size();i++)
			{
				if(DomainConstants.RELATIONSHIP_PROTECTED_ITEM.equals(strkey))
				{
					String strTypePattern = new StringBuilder(TYPE_IP_CONTROL_CLASS).append(CONSTANT_STRING_COMMA).append(TYPE_SECURITY_CONTROL_CLASS).toString();
					strObj = slObj.get(i);
					mlObjs = DomainObject.findObjects(context,         // eMatrix context 
							strTypePattern,                  // type pattern    
							strObj,                            // name pattern    
							DomainConstants.QUERY_WILDCARD,    // revision pattern
							DomainConstants.QUERY_WILDCARD,    // owner pattern   
							VAULT_ESERVICE_PRODUCTION,         // Vault Pattern	
							DomainConstants.EMPTY_STRING,      // where expression
							true,                              // expand type     
							slSelect);                         // object selects  

					if(BusinessUtil.isNotNullOrEmpty(mlObjs))
					{
						strObj = (String)((Map)mlObjs.get(0)).get(DomainConstants.SELECT_ID);							
					}			
					domFrom = DomainObject.newInstance(context,strObj);
					domTo = DomainObject.newInstance(context, newObjectID);
				}
				else
				{
					domFrom = DomainObject.newInstance(context, newObjectID);
					domTo = DomainObject.newInstance(context, slObj.get(i));
				}

				try {
					DomainRelationship.connect(context,domFrom , strkey, domTo);
				} 
				catch(Exception ex) {
					slErrorMsgs.add(ex.getMessage());
				}	
			}

		}
		if(BusinessUtil.isNotNullOrEmpty(slErrorMsgs))
		{
			return slErrorMsgs.toString();
		}
		else
		{
			return EMPTY_STRING;
		}
	}

	/**This method use to get all objects of type which is pass in request and also filter data
	 * @param context
	 * @param mpRequestData
	 * @return
	 * @throws FrameworkException
	 */
	public Response findObjects(Context context, Map<String, String> mpRequestData) throws FrameworkException 
	{
		startTime = System.currentTimeMillis();
		long lstartTimeSingleObject = 0;
		JsonObjectBuilder output = Json.createObjectBuilder();
		String typeObj = mpRequestData.get("type");
		String strFilter = mpRequestData.get("filter");
		try {
			output.add(STRING_MESSAGE, STRING_KO);

			MapList mlObjs = getObjects(context, mpRequestData);
			long  findObjectsTime= System.currentTimeMillis() - startTime;
			String strFindObjMiliSec = new StringBuilder("Time after findObjects call in mili Seconds is :").append(findObjectsTime).toString() ;
			logger.log(Level.INFO, strFindObjMiliSec);

			JsonArrayBuilder outArr = Json.createArrayBuilder();
			Map<String, Object> mapObj;
			String strType ;
			String strPolicy;
			String strCurrent;
			StringList slId;
			StringList slClaimName;
			StringList slClaimRev;
			StringList slClaimPhysicalID;
			StringList slClaimType;
			StringList slClaimCurrent;
			StringList slClaimAttrClaim = new StringList();
			StringList slClaimAttrDisclaimer = new StringList();
			DomainObject domObj = DomainObject.newInstance(context);
			String strValue;
			String strPropKey;
			MapList mlClaimData = null;
			Map mpClaim;
			Map mpClaimData ; 
			String strPOCTypeValue;
			JsonObjectBuilder jsonPOCObj;
			long claimGetInfoTime ;
			long  totalExecutionTimeSingleLoop ;
			long  totalTimeInSecondsSingleLoop ;
			String strClaimInfoTimeMsg = null;
			String strTotalExecTimeLoopMsg = null;
			String strTotalTimeInSecLoopMsg = null;
			if(null != mlObjs && !mlObjs.isEmpty())
			{
				for(int i=0;i<mlObjs.size();i++)
				{
					lstartTimeSingleObject = System.currentTimeMillis();
					mapObj = (Map<String, Object>) mlObjs.get(i);
					//Remove Bell Char Start
					removeBellChar(mapObj);												
					//Remove Bell Char End
					mapObj.put(IS_EDITABLE, isEditable(context,mapObj));
					//DCM (DS) 2022x-01 CW Req 44675- extend condition for owned and co-owned filter  - START
					if(STRING_COLLABORATION.equals(strFilter) || STRING_OWNED.equals(strFilter) || STRING_CO_OWNED.equals(strFilter) )
					{
						setEditableForFunction(context,mapObj);						
					}
					//DCM (DS) 2022x-01 CW Req 44675- extend condition for owned and co-owned filter  - - End
					strType =(String) mapObj.get(DomainConstants.SELECT_TYPE);
					strPolicy =(String) mapObj.get(DomainConstants.SELECT_POLICY);
					strCurrent =(String) mapObj.get(DomainConstants.SELECT_CURRENT);
					strPropKey = "emxFramework.Type."+ strType;
					strValue = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
							strPropKey, context.getLocale());
					if(!strPropKey.equals(strValue))
					{
						mapObj.put(DomainConstants.SELECT_TYPE,strValue);	
					}
					strPropKey = new StringBuilder("emxFramework.State.").append(strPolicy).append(".").append(strCurrent.replace(CONST_WHITE_SPACE, "_")).toString();
					strValue = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
							strPropKey, context.getLocale());
					if(!strPropKey.equals(strValue))
					{
						mapObj.put(DomainConstants.SELECT_CURRENT,strValue);	
					}
					mapObj.put(DomainConstants.SELECT_CURRENT, mapObj.get(DomainConstants.SELECT_CURRENT));
					domObj.setId((String) mapObj.get(DomainConstants.SELECT_ID));
					mlClaimData = new MapList();
					if(mapObj.containsKey(FROM_PG_CLAIMS_ID))
					{
						slId =returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_ID));
						slClaimName = returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_TO_NAME));
						slClaimRev = returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_TO_REVISION));
						slClaimPhysicalID = returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_TO_PHYSICALID));
						slClaimType = returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_TO_TYPE));
						slClaimCurrent = returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_TO_CURRENT));
						//For displaying Claim and Disclaimer in CLM under CLR
						if(TYPE_PG_CLAIM_REQUEST.equals(typeObj) && mapObj.containsKey(FROM_PG_CLAIMS_TO_CLAIM_RTE))
						{
							slClaimAttrClaim = returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_TO_CLAIM_RTE));
							slClaimAttrDisclaimer = returnStringListForObject(mapObj.get(FROM_PG_CLAIMS_TO_DISCLAIMER_RTE));
						}
						for(int k=0;k<slId.size();k++)
						{
							mpClaim = new HashMap<>();
							mpClaim.put(DomainConstants.SELECT_ID, slId.get(k));
							mpClaim.put(DomainConstants.SELECT_NAME, slClaimName.get(k));
							mpClaim.put(SELECT_PHYSICALID, slClaimPhysicalID.get(k));
							mpClaim.put(DomainConstants.SELECT_REVISION, slClaimRev.get(k));
							mpClaim.put(DomainConstants.SELECT_TYPE, slClaimType.get(k));
							mpClaim.put(DomainConstants.SELECT_CURRENT, slClaimCurrent.get(k));
							//For displaying Claim and Disclaimer in CLM under CLR
							if(TYPE_PG_CLAIM_REQUEST.equals(typeObj) && mapObj.containsKey(FROM_PG_CLAIMS_TO_CLAIM_RTE))
							{
								mpClaim.put(DomainObject.getAttributeSelect(ATTRIBUTE_CLAIMNAME_RTE), slClaimAttrClaim.get(k));
								mpClaim.put(DomainObject.getAttributeSelect(ATTRIBUTE_DISCLAIMERNAME_RTE), slClaimAttrDisclaimer.get(k));
							}
							mlClaimData.add(mpClaim);
						}
						claimGetInfoTime= System.currentTimeMillis() - startTime;
						strClaimInfoTimeMsg = new StringBuilder("Time after getInfo call in mili Seconds is :").append(claimGetInfoTime).toString();
						logger.log(Level.INFO, strClaimInfoTimeMsg);
					}
					mapObj.put(STRING_TREE,new StringBuffer((String) mapObj.get(DomainConstants.SELECT_NAME)).append(CONST_WHITE_SPACE).append(mapObj.get(DomainConstants.SELECT_REVISION).toString()));
					JsonObjectBuilder jsonObj = Json.createObjectBuilder();
					PGClaimModuler.map2JsonBuilder(jsonObj, mapObj);
					outArr.add(jsonObj);
					for(int k = 0;k<mlClaimData.size();k++)
					{
						mpClaimData =(Map) mlClaimData.get(k);
						strType =(String) mpClaimData.get(DomainConstants.SELECT_TYPE);
						strPOCTypeValue = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
								"emxFramework.Type."+ strType, context.getLocale());
						mpClaimData.put(DomainConstants.SELECT_TYPE,strPOCTypeValue);

						mpClaimData.put(STRING_TREE,new StringBuffer((String) mapObj.get(DomainConstants.SELECT_NAME)).append(CONST_WHITE_SPACE).append((String) mapObj.get(DomainConstants.SELECT_REVISION)).append("/").append((String) mpClaimData.get(DomainConstants.SELECT_NAME)).append(CONST_WHITE_SPACE).append((String) mpClaimData.get(DomainConstants.SELECT_REVISION)).toString());
						jsonPOCObj = Json.createObjectBuilder();
						PGClaimModuler.map2JsonBuilder(jsonPOCObj, mpClaimData);
						outArr.add(jsonPOCObj);
					}

					totalExecutionTimeSingleLoop= System.currentTimeMillis() - lstartTimeSingleObject;
					strTotalExecTimeLoopMsg = new StringBuilder("Total Execution Time for Single object in findObjects in mili Seconds is :").append(totalExecutionTimeSingleLoop).toString();
					totalTimeInSecondsSingleLoop = (totalExecutionTimeSingleLoop / 1000);
					strTotalTimeInSecLoopMsg = new StringBuilder("Total Execution Time for Single object in findObjects in Seconds is :").append(totalTimeInSecondsSingleLoop).toString();
					logger.log(Level.INFO, strTotalExecTimeLoopMsg);
					logger.log(Level.INFO, strTotalTimeInSecLoopMsg);
				}
			}
			output.add(STRING_MESSAGE, STRING_OK);
			output.add(STRING_DATA, outArr);

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		finally
		{		  
			long  totalExecutionTime= System.currentTimeMillis() - startTime;
			long  totalTimeInSeconds = (totalExecutionTime / 1000);
			String strExecTimeMiliSecMsg = new StringBuilder("Total Execution Time in findObjects in mili Seconds is :").append(totalExecutionTime).toString();
			String strExecTimeSecMsg =new StringBuilder("Total Execution Time in findObjects in Seconds is :").append(totalTimeInSeconds).toString();
			logger.log(Level.INFO, strExecTimeMiliSecMsg);
			logger.log(Level.INFO, strExecTimeSecMsg); 	

		}
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();

	}

	/**
	 * Private method to get related objects or find objects
	 * @param context
	 * @param mpRequestData
	 * @return
	 */
	private MapList getObjects(Context context, Map<String, String> mpRequestData) throws FrameworkException {
		MapList mlObjs=null;
		try {
			String typeObj = mpRequestData.get("type");
			String strSelect = mpRequestData.get("ObjSelectables");
			String strWhereCondition = mpRequestData.get("whereCondition");
			String strFilter = mpRequestData.get("filter");
			String strLimit = mpRequestData.get("limit");
			String relName = mpRequestData.get(STRING_RELATIONSHIP);
			if(UIUtil.isNullOrEmpty(strLimit))
			{
				strLimit = "0";
			}

			StringList slObj = new StringList();
			StringList relList = new StringList();
			relList.add(DomainRelationship.SELECT_ID);
			if(UIUtil.isNotNullAndNotEmpty(strSelect))
			{
				slObj = StringUtil.split(strSelect, CONSTANT_STRING_COMMA);
			}

			slObj.add(DomainConstants.SELECT_NAME);
			slObj.add(DomainConstants.SELECT_REVISION);
			slObj.add(DomainConstants.SELECT_POLICY);
			slObj.add(DomainConstants.SELECT_TYPE);
			slObj.add(DomainConstants.SELECT_CURRENT);
			slObj.add(DomainConstants.SELECT_OWNER);
			slObj.add(FROM_PG_CLAIMS_TO_NAME);
			slObj.add(FROM_PG_CLAIMS_TO_PHYSICALID);
			slObj.add(FROM_PG_CLAIMS_TO_REVISION);
			slObj.add(FROM_PG_CLAIMS_TO_TYPE);
			slObj.add(FROM_PG_CLAIMS_TO_CURRENT);
			slObj.add(FROM_PG_CLAIMS_ID);
			slObj.add(CURRENT_ACCESS);
			slObj.add(FROM_PG_CLAIMS_TO_CLAIM_RTE);
			slObj.add(FROM_PG_CLAIMS_TO_DISCLAIMER_RTE);
			slObj.addElement(DomainConstants.SELECT_ID);
			slObj.addElement(SELECT_PHYSICALID);
			if(STRING_COLLABORATION.equals(strFilter) || STRING_OWNED.equals(strFilter) || STRING_CO_OWNED.equals(strFilter))
			{
				relList.add(DomainObject.getAttributeSelect(ATTRIBUTE_PG_COLLABORATION_STATUS));
				slObj.add(SELECT_ASSIGNMENTS_TO_NAME);
				slObj.add(SELECT_ASSIGNMENTS_ROLE);
			}
			Short limitObj = Short.parseShort(strLimit);
			StringList orderBy = new StringList(new StringBuilder(CONST_HIPHEN).append(DomainConstants.SELECT_MODIFIED).toString());

			if(STRING_COLLABORATION.equals(strFilter)) {
				DomainObject domPerson = DomainObject.newInstance(context,new BusinessObject
						(DomainConstants.TYPE_PERSON, context.getUser(), CONST_HIPHEN, VAULT_ESERVICE_PRODUCTION));
				String strRelWhere = DomainObject.getAttributeSelect(ATTRIBUTE_PG_COLLABORATION_STATUS)+ " == 'Assigned'";
				mlObjs = domPerson.getRelatedObjects(
						context, //context
						relName, //relPattern
						typeObj, //typePattern
						slObj, //busSelects
						relList, //relSelects
						true, // getTo
						false, // getFrom
						(short) 1,  //recurseToLevel
						strWhereCondition, //busWhere
						strRelWhere, //relWhere
						0 //limit
						);
			} else {
				StringList multiValueList = new StringList(FROM_PG_CLAIMS_TO_CLAIM_RTE);
				multiValueList.add(FROM_PG_CLAIMS_TO_DISCLAIMER_RTE);
				slObj.add(FROM_PG_CLAIMS_TO_CLAIM_RTE);
				slObj.add(FROM_PG_CLAIMS_TO_DISCLAIMER_RTE);
				mlObjs = DomainObject.findObjects(
						context,					// eMatrix context
						typeObj,					// type pattern    
						DomainConstants.QUERY_WILDCARD,// name pattern
						DomainConstants.QUERY_WILDCARD,// rev pattern
						DomainConstants.QUERY_WILDCARD,//owner
						VAULT_ESERVICE_PRODUCTION,	// Vault Pattern	
						strWhereCondition,			// where expression
						DomainConstants.EMPTY_STRING,//query Name
						false,						// expand type 
						slObj,						//Object Select
						limitObj,					//Limit
						DomainConstants.EMPTY_STRING,//search format
						DomainConstants.EMPTY_STRING,//search test
						multiValueList,	//multi value list
						orderBy);//order by
			}
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		} catch (MatrixException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return mlObjs;
	}

	/**This Method return Role of current user for Claim Request.
	 * @param context
	 * @param mapObj
	 * @return
	 */
	private void setEditableForFunction(Context context, Map<String, Object> mapObj) {
		StringList slRole = new StringList();
		String strCurrent = (String) mapObj.get(DomainConstants.SELECT_CURRENT);
		String strType = (String) mapObj.get(DomainConstants.SELECT_TYPE);
		if((TYPE_PG_CLAIM_REQUEST.equals(strType) || TYPE_PG_CLAIM_SUPPORT.equals(strType) )&& ("Create".equals(strCurrent)||STATE_PRELIMINARY.equals(strCurrent)||"Collaboration".equals(strCurrent)))
		{
			StringList slAssignee = returnStringListForObject(mapObj.get(SELECT_ASSIGNMENTS_TO_NAME));
			StringList slAssigneeROLE = returnStringListForObject(mapObj.get(SELECT_ASSIGNMENTS_ROLE));
			String strLoginUser = context.getUser();
			if(BusinessUtil.isNotNullOrEmpty(slAssignee))
			{
				for(int i=0;i<slAssignee.size();i++)
				{
					if(strLoginUser.equals(slAssignee.get(i)))
					{
						//DCM (DS) 2022x-01 CW - ALM 51521 - Error when opening Co-owned tab - START
						try {
							//DCM (DS) 2022x-01 CW - ALM 51521 - Error when opening Co-owned tab - END
							slRole.add(slAssigneeROLE.get(i));
							//DCM (DS) 2022x-01 CW - ALM 51521 - Error when opening Co-owned tab - START
						} 
						catch(IndexOutOfBoundsException iobe)
						{
							slRole.add(DomainConstants.EMPTY_STRING);
						}
						//DCM (DS) 2022x-01 CW - ALM 51521 - Error when opening Co-owned tab - END
					}
				}
			}
			mapObj.put("EditableFor",slRole );
		}
	}

	/**This Method check ownership of user on object
	 * @param context
	 * @param busid
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private boolean hasOwnership(Context context, String busid,String user) throws Exception {
		boolean hasMultipleOwnership = DomainAccess.hasObjectOwnership(context, busid, EMPTY_STRING, user+"_PRJ", DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
		boolean hasPRAssignment = DomainAccess.hasObjectOwnership(context, busid, EMPTY_STRING, user+"_PRJ", STRING_PR_ASSIGNEE);
		return (hasMultipleOwnership || hasPRAssignment) ; 
	}

	/**This method Check state and Owner and return boolean value.
	 * @param mapObj
	 * @param user
	 * @return
	 * @throws FrameworkException 
	 * @throws Exception 
	 */
	private boolean isEditable(Context context,Map<String, Object> mapObj) throws FrameworkException {
		String objId =  (String) mapObj.get(DomainConstants.SELECT_ID);
		return FrameworkUtil.hasAccess(context,DomainObject.newInstance(context,objId),"modify");
	}

	/**This Method Promote and Demote Object.
	 * @param context
	 * @param request
	 * @param typeObj
	 * @param strEvent
	 * @return
	 * @throws Exception 
	 * @throws MatrixException 
	 */
	@SuppressWarnings("rawtypes")
	public String promoteDemoteObject(Context context,String objectId,String strEvent) throws MatrixException 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strMessage =DomainConstants.EMPTY_STRING;
		String strTNR =DomainConstants.EMPTY_STRING;
		String strEventMessage = DomainConstants.EMPTY_STRING;
		String strStatus = STATUS_INFO;
		try {
			StringList slObjectList = StringUtil.split(objectId, CONSTANT_STRING_COMMA);

			DomainObject domObj = DomainObject.newInstance(context);
			StringList slSelect = new StringList(DomainConstants.SELECT_NAME);
			slSelect.add(DomainConstants.SELECT_TYPE);
			slSelect.add(DomainConstants.SELECT_REVISION);
			slSelect.add(DomainConstants.SELECT_POLICY);
			slSelect.add(DomainConstants.SELECT_CURRENT);
			slSelect.add(DomainConstants.SELECT_ID);
			Map mpContentData;
			MapList mlContentData = null;

			if(BusinessUtil.isNotNullOrEmpty(slObjectList))
			{
				mlContentData = DomainObject.getInfo(context, slObjectList.toArray(new String[0]), slSelect);
			}
			for (int iCount = 0; iCount<mlContentData.size(); iCount++)
			{
				mpContentData = (Map) mlContentData.get(iCount);
				strTNR = (String) mpContentData.get(DomainConstants.SELECT_NAME);
				domObj.setId((String) mpContentData.get(DomainConstants.SELECT_ID));
				try
				{
					String response = PGWidgetUtil.promoteDemoteObject(context, (String) mpContentData.get(DomainConstants.SELECT_ID), strEvent);

					strMessage =  new StringBuilder(strMessage).append(strTNR).append(" :: ").append(strEventMessage).append(" Successfully").append("\n\n").toString();
					strStatus = STATUS_SUCCESS;
				}
				catch(Exception ex)
				{
					strMessage = ex.getMessage();
					logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
					strMessage =  new StringBuilder(strTNR).append(" :: ").append(strEventMessage).append(" Failed").append(" - ").append(ex.getMessage()).append(CONST_NEW_LINE).toString();
					strStatus = STATUS_ERROR;
				}
			}
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			strMessage =  new StringBuilder(strMessage).append(strTNR).append(" :: ").append(strEventMessage).append(" Failed").append(" - ").append(e.getMessage()).append(CONST_NEW_LINE).toString();
		}

		jsonReturnObj.add(STRING_STATUS, strStatus);
		jsonReturnObj.add(STRING_MESSAGE,strMessage);

		return jsonReturnObj.build().toString();
	}

	/**This Method update attribute and description.
	 * @param context
	 * @param request
	 * @param mpUpdateValue
	 * @return
	 * @throws MatrixException 
	 */
	public String updateObject(Context context, Map<String,Object> mpUpdateValue) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonObjectBuilder tempReturnObj = Json.createObjectBuilder();
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		Map<String,Object> mpData ;
		Map<String,Object> mpOldData ;
		boolean isContextPushed = false;
		try
		{
			DomainObject domObj = DomainObject.newInstance(context);
			String strMessage;
			String strStatus = STATUS_SUCCESS;
			String name ;
			String strType ;
			Map<String,Object> mpAttributeMap = null;
			Map<String,Object> mpOldAttributeMap = null;
			Map<String,Object> mpProductConfigAttributeMap = null;
			//mpupdateValue map have objectId as Key
			if(null!=mpUpdateValue && !mpUpdateValue.isEmpty())
			{
				Map mpObjectNameMap = (Map) mpUpdateValue.get("objectNameMap");
				Map<String,Object> mpUpdatedValue = (Map) mpUpdateValue.get("updatedValue");
				// US-1598: Claim Data Model changes - CLR ---> Claims tab changes : Start
				Map<String,Object> mpUpdatedRelValue = (Map) mpUpdateValue.get("updatedRelMap");
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
				Map<String,Object> mpUpdateObjectTypeMap = (Map) mpUpdateValue.get("updateObjectTypeMap");
				Map<String,Object> mpOldValueMap = (Map) mpUpdateValue.get("oldValueMap");
				Map<String,Object> mpOldValueRelMap = (Map) mpUpdateValue.get("oldValueRelMap");
				String strBaseObjectID = (String) mpUpdateValue.get("baseObjectID");
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
				if(mpUpdatedRelValue != null && !mpUpdatedRelValue.isEmpty())
				{
					Map<String, Object> mapMethodParametersUpdate = new HashMap<>();
					mapMethodParametersUpdate.put("tempReturnObj", tempReturnObj);
					mapMethodParametersUpdate.put("strBaseObjectID", strBaseObjectID);
					mapMethodParametersUpdate.put("mpUpdatedValue", mpUpdatedRelValue);
					mapMethodParametersUpdate.put("mpOldValueMap", mpOldValueRelMap);
					mapMethodParametersUpdate.put("jsonArray", jsonArray);
					for (String strRelKey : mpUpdatedRelValue.keySet())  
					{

						name = (String) mpObjectNameMap.get(strRelKey);
						//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
						strType = (String) mpUpdateObjectTypeMap.get(strRelKey);
						mapMethodParametersUpdate.put("name", name);
						mapMethodParametersUpdate.put("strType", strType);
						mapMethodParametersUpdate.put("strKey", strRelKey);
						updateAttributeonRel(context, mapMethodParametersUpdate);
						//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
						jsonArray.add(tempReturnObj);
					}
				}
				Map<String, Object> mapMethodParameters = new HashMap<>();
				mapMethodParameters.put("jsonReturnObj", jsonReturnObj);
				mapMethodParameters.put("tempReturnObj", tempReturnObj);
				mapMethodParameters.put("jsonArray", jsonArray);
				mapMethodParameters.put("isContextPushed", isContextPushed);
				mapMethodParameters.put("domObj", domObj);
				mapMethodParameters.put("strStatus", strStatus);
				mapMethodParameters.put("mpObjectNameMap", mpObjectNameMap);
				mapMethodParameters.put("mpUpdatedValue", mpUpdatedValue);
				mapMethodParameters.put("mpUpdateObjectTypeMap", mpUpdateObjectTypeMap);
				mapMethodParameters.put("mpOldValueMap", mpOldValueMap);
				mapMethodParameters.put("strBaseObjectID", strBaseObjectID);
				isContextPushed = setAttributesAndMessage(context, mapMethodParameters);

			}
		} catch (FrameworkException e1) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e1);
			jsonReturnObj.add(STATUS_ERROR,e1.getMessage());
		}
		finally {
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		jsonReturnObj.add(STRING_DATA,  jsonArray);
		return jsonReturnObj.build().toString();
	}

	/**Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim
	 * Method to refactor code
	 * @param context
	 * @param mapMethodParameters
	 * @return
	 * @throws MatrixException 
	 */
	private boolean setAttributesAndMessage(Context context, Map<String, Object> mapMethodParameters)
			throws MatrixException {
		JsonObjectBuilder jsonReturnObj = (JsonObjectBuilder) mapMethodParameters.get("jsonReturnObj");
		JsonObjectBuilder tempReturnObj = (JsonObjectBuilder) mapMethodParameters.get("tempReturnObj");
		JsonArrayBuilder jsonArray = (JsonArrayBuilder) mapMethodParameters.get("jsonArray");
		boolean isContextPushed = (boolean) mapMethodParameters.get("isContextPushed");
		DomainObject domObj = (DomainObject) mapMethodParameters.get("domObj");
		String strStatus = (String) mapMethodParameters.get("strStatus");
		Map mpObjectNameMap = (Map) mapMethodParameters.get("mpObjectNameMap");
		Map<String,Object> mpUpdatedValue = (Map<String, Object>) mapMethodParameters.get("mpUpdatedValue");
		Map<String,Object> mpUpdateObjectTypeMap = (Map<String, Object>) mapMethodParameters.get("mpUpdateObjectTypeMap");
		Map<String,Object> mpOldValueMap = (Map<String, Object>) mapMethodParameters.get("mpOldValueMap");
		String strBaseObjectID = (String) mapMethodParameters.get("strBaseObjectID");
		
		Map<String, Object> mpData;
		Map<String, Object> mpOldData;
		String strMessage;
		String name;
		String strType = DomainConstants.EMPTY_STRING;
		Map<String, Object> mpAttributeMap;
		Map<String, Object> mpOldAttributeMap;
		Map<String, Object> mpProductConfigAttributeMap;
		if(mpUpdatedValue != null && !mpUpdatedValue.isEmpty())
		{
			// US-1598: Claim Data Model changes - CLR ---> Claims tab changes : End
			for (String strKey : mpUpdatedValue.keySet())  
			{
				strMessage = DomainConstants.EMPTY_STRING;
				mpData= (Map)mpUpdatedValue.get(strKey);
				if(mpOldValueMap != null && mpUpdateObjectTypeMap != null)
				{
					mpOldData= (Map)mpOldValueMap.get(strKey);
					strType = (String) mpUpdateObjectTypeMap.get(strKey);
				}else {
					mpOldData = new HashMap<>();
				}
				name = (String) mpObjectNameMap.get(strKey);
				
				if(!(mpData.containsKey(DomainObject.getAttributeSelect(ATTRIBUTE_ASSIGNMENTS_ROLE)) || mpData.containsKey(DomainObject.getAttributeSelect(ATTRIBUTE_ASSIGNMENTS_FUNCTION)) || mpData.containsKey(DomainObject.getAttributeSelect("pgClaimSequence")) || mpData.containsKey(DomainObject.getAttributeSelect(ATTRIBUTE_PG_COLLABORATION_STATUS))))
				{
					domObj.setId(strKey);	
					mpAttributeMap =new HashMap<>();
					mpOldAttributeMap =new HashMap<>();
					mpProductConfigAttributeMap =new HashMap<>();

					try
					{
						if(null!=mpData && !mpData.isEmpty())
						{
							for (String strUpdateKey : mpData.keySet())  
							{

								strMessage = getValidAttributeMapForUpdate(context, mpOldData, domObj, mpOldAttributeMap,	strUpdateKey);
								strMessage = getValidAttributeMapForUpdate(context, mpData, domObj, mpAttributeMap,	strUpdateKey);
							}
						}
						Map<String, Object> mapMethodParametersHistory = new HashMap<>();
						mapMethodParametersHistory.put("domObj", domObj);
						mapMethodParametersHistory.put("strBaseObjectID", strBaseObjectID);
						mapMethodParametersHistory.put("strName", name);
						mapMethodParametersHistory.put("strType", strType);
						mapMethodParametersHistory.put("mpAttributeMap", mpAttributeMap);
						mapMethodParametersHistory.put("mpOldAttributeMap", mpOldAttributeMap);
						mapMethodParametersHistory.put("mpProductConfigAttributeMap", mpProductConfigAttributeMap);
						setAttrAndUpdateHistory(context, mapMethodParametersHistory);
					}
					catch (FrameworkException e) 
					{
						strStatus = STATUS_ERROR;
						logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
						jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
						jsonReturnObj.add(STRING_MESSAGE,e.getMessage());
					}
					createErrorMessage(context, jsonReturnObj);
					String strJsonMessage = jsonReturnObj.build().containsKey(STRING_MESSAGE)?jsonReturnObj.build().getString(STRING_MESSAGE) : DomainConstants.EMPTY_STRING;
					if(UIUtil.isNotNullAndNotEmpty(strJsonMessage))
					{
						strMessage = strJsonMessage;
					}
					jsonReturnObj.add(STRING_STATUS, strStatus);

					if(UIUtil.isNotNullAndNotEmpty(strMessage))
					{
						if(strMessage.contains("Warning"))
						{
							strStatus = STATUS_INFO;
						}else
						{
							strStatus = STATUS_ERROR;
						}
						tempReturnObj.add(STRING_STATUS, strStatus);
						tempReturnObj.add("name", name);
						tempReturnObj.add(STRING_MESSAGE, strMessage);
					}
					else
					{
						tempReturnObj.add(STRING_STATUS, strStatus);
						tempReturnObj.add("name", name);
					}
				}
				jsonArray.add(tempReturnObj);
				// US-1598: Claim Data Model changes - CLR ---> Claims tab changes : End

				if(isContextPushed)
				{
					ContextUtil.popContext(context);
					isContextPushed =false;
				}
			}
		}
		return isContextPushed;
	}

	/**Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim
	 * Method to refactor code
	 * @param context
	 * @param mapMethodParametersHistory
	 * @return
	 * @throws MatrixException 
	 */
	private void setAttrAndUpdateHistory(Context context, Map<String, Object> mapMethodParametersHistory) throws MatrixException {
		
		
		DomainObject domObj = (DomainObject) mapMethodParametersHistory.get("domObj");
		String strBaseObjectID = (String) mapMethodParametersHistory.get("strBaseObjectID");
		String strName = (String) mapMethodParametersHistory.get("strName");
		String strType = (String) mapMethodParametersHistory.get("strType");
		Map<String, Object> mpAttributeMap = (Map<String, Object>) mapMethodParametersHistory.get("mpAttributeMap");
		Map<String, Object> mpOldAttributeMap = (Map<String, Object>) mapMethodParametersHistory.get("mpOldAttributeMap");
		Map<String, Object> mpProductConfigAttributeMap = (Map<String, Object>) mapMethodParametersHistory.get("mpProductConfigAttributeMap");
		if(null!=mpAttributeMap && !mpAttributeMap.isEmpty())
		{
			domObj.setAttributeValues(context, mpAttributeMap);
			//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
			if(TYPE_PG_CLAIM.equals(strType) || TYPE_PG_DISCLAIMER.equals(strType) || STRING_CLAIM.equals(strType) || STRING_DISCLAIMER.equals(strType))
			{
				prepareProductConfigAttributesMap(context, mpAttributeMap, mpProductConfigAttributeMap);
				if(!mpProductConfigAttributeMap.isEmpty())
				{
					updateHistoryOnBaseObject(context, mpProductConfigAttributeMap, mpOldAttributeMap, new StringBuilder(strType).append(CONST_WHITE_SPACE).append(strName).toString(), strBaseObjectID);
				}
			}
			//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
		}
	}
	/**DCM (DS) Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
	 * This Method update history of base object on rel and product config attribute modification
	 * @param context
	 * @param mpAttributeMap
	 * @param mpAttributeOldValueMap
	 * @param strType
	 * @param name
	 * @param strBaseObjectID
	 * @return void
	 * @throws FrameworkException 
	 */
	public void updateHistoryOnBaseObject(Context context, Map<String, Object> mpAttributeMap, Map<String, Object> mpAttributeOldValueMap, String strTypeNameRevision, String strBaseObjectID)  throws MatrixException {
		try {
			StringList slMultiValueAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.MultiVlaue.Attribute.List"), CONSTANT_STRING_COMMA);  
			String strKey;
			String strOldValue;
			String strNewValue;
			for (Map.Entry entry : mpAttributeMap.entrySet())  
			{
				strKey = (String) entry.getKey();
				strOldValue = mpAttributeOldValueMap.get(entry.getKey()).toString();
				strNewValue = entry.getValue().toString();
				if(slMultiValueAttrList.contains(strKey)) {
					strOldValue = strOldValue.replace("[", "").replace("]", "");
					strNewValue = strNewValue.replace("[", "").replace("]", "");
				}
				StringBuilder sbComment = new StringBuilder();
				sbComment.append(strTypeNameRevision).append(STR_COLON).append(EnoviaResourceBundle.getAdminI18NString(context, "Attribute", strKey, context.getSession().getLanguage())).append(STR_COLON)
				.append(CONST_WHITE_SPACE).append(strNewValue).append(" was: ").append(strOldValue);
				MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, strBaseObjectID,"Modify", sbComment.toString());
			}
		} catch (MatrixException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
	}
	
	/**DCM (DS) Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
	 * This Method to update attribute map keys without Attribute[]
	 * @param mpProductConfigAttribute
	 * @param mpPlainAttributeMap
	 * @return void
	 * @throws FrameworkException 
	 */
	public void getMapWithPlainAttributeKeys(Map<String, Object> mpProductConfigAttribute, Map<String, Object> mpPlainAttributeMap)  throws FrameworkException {
		String strKey;
		for (Map.Entry entry : mpProductConfigAttribute.entrySet())  
		{
			strKey = entry.getKey().toString().replace("attribute[","").replace("]","");
			mpPlainAttributeMap.put(strKey, entry.getValue());
			
		}
	}
	
	/**DCM (DS) Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
	 * This Method copies the map and empty the values
	 * @param mpEmptyAttributeMap
	 * @param mpAttributes
	 * @return void
	 * @throws FrameworkException 
	 */
	public void copyMapWithEmptyValues(Map<String, Object> mpEmptyAttributeMap, Map<String, Object> mpAttributes)  throws FrameworkException {
		try {
			if(mpAttributes != null && !mpAttributes.isEmpty())
			{
				for (Map.Entry entry : mpAttributes.entrySet())  
				{
					mpEmptyAttributeMap.put((String) entry.getKey(), DomainConstants.EMPTY_STRING);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
	}
	
	/**DCM (DS) Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
	 * This Method removes claim attributes from map
	 * @param context
	 * @param mpAttributeMap
	 * @param mpAttributeOldValueMap
	 * @param strType
	 * @param name
	 * @param strBaseObjectID
	 * @return void
	 * @throws FrameworkException 
	 */
	public void prepareProductConfigAttributesMap(Context context, Map<String, Object> mpAttributeMap, Map<String, Object> mpProductConfigAttributeMap)  throws FrameworkException {
		try {
			StringList slProductConfigAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.ProductConfig.Attributes"),CONSTANT_STRING_COMMA);
			for (Map.Entry entry : mpAttributeMap.entrySet())  
			{
				if(slProductConfigAttrList.contains(entry.getKey()))
				{
					mpProductConfigAttributeMap.put((String) entry.getKey(), entry.getValue());
				}
			}
		} catch (FrameworkException e1) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e1);
		}
	}

	/**DCM (DS) 2022x-01 CW ALM-51457 - Two users editing functional comments at the same time
	 * This Method appends existing comments to new value for update
	 * @param context
	 * @param mpAttributeMap
	 * @return void
	 * @throws FrameworkException 
	 */
	private void appendCommentValues(Context context, Map<String, Object> mpAttributeMap, DomainRelationship domRel) throws FrameworkException {
		Map mapExistingAttributeValues = domRel.getAttributeMap(context);
		StringList slCommentAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.CommentAttributes"),CONSTANT_STRING_COMMA);
		for(String strCommentAttr : slCommentAttrList)
		{
			mpAttributeMap.computeIfPresent(strCommentAttr,
					(key, val) -> {
						String strInputValue = (String) val;
						if(strInputValue.contains(HIDDEN_SEPERATOR))
						{
							strInputValue = strInputValue.substring(strInputValue.indexOf(HIDDEN_SEPERATOR) + HIDDEN_SEPERATOR.length()).replace(HIDDEN_SEPERATOR, DomainConstants.EMPTY_STRING);
						}
						StringBuilder sbExistingValue = new StringBuilder((String) mapExistingAttributeValues.get(key));
						if(UIUtil.isNotNullAndNotEmpty(sbExistingValue.toString()))
						{
							sbExistingValue.append(STRING_HTML_BREAK_LINE);
						}
						return sbExistingValue.append(strInputValue).toString();
					});
		}
	}

	/**This Method create list of valid attribute for update
	 * @param context
	 * @param mpData
	 * @param domObj
	 * @param mpAttributeMap
	 * @param strUpdateKey
	 * @return
	 * @throws FrameworkException 
	 */
	private String getValidAttributeMapForUpdate(Context context, Map<String, Object> mpData, DomainObject domObj,
			Map<String, Object> mpAttributeMap, String strUpdateKey) throws FrameworkException {
		String strMessage = EMPTY_STRING;
		if(mpData != null)
		{
			if("name".equals(strUpdateKey)||"revision".equals(strUpdateKey)||"type".equals(strUpdateKey))
			{
				strMessage = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.UpdateTNR.Warning", context.getLocale());
				mpData.remove(strUpdateKey);
				return strMessage;
			}
			if(DomainConstants.SELECT_DESCRIPTION.equals(strUpdateKey))
			{
				try
				{
					domObj.setDescription(context,(String) mpData.get(strUpdateKey));
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
					strMessage = e.getMessage();
				}
	
			}
			else if(strUpdateKey.contains("attribute["))
			{		
					if(mpData.containsKey(strUpdateKey))
					{
						Object obj = mpData.get(strUpdateKey);
						if(obj instanceof List)
						{
							mpAttributeMap.put((strUpdateKey.replace("attribute[","")).replace("]",""),StringList.asList((ArrayList<String>)obj)); 
						}
						else
						{
							//DCM (DS) 2022x-02 - Defect 52822 - Dragging down numeric value cannot be saved - START
							mpAttributeMap.put((strUpdateKey.replace("attribute[","")).replace("]",""), obj.toString()); 
							//DCM (DS) 2022x-02 - Defect 52822 - Dragging down numeric value cannot be saved - END
						}
					}
			}
		}
		return strMessage;
	}

	/**This Method Use to update attribute on Relationship
	 * @param context
	 * @param mapMethodParametersUpdate
	 * @throws FrameworkException
	 */
	private void updateAttributeonRel(Context context, Map<String, Object> mapMethodParametersUpdate) throws Exception 
	{
		JsonObjectBuilder tempReturnObj = (JsonObjectBuilder) mapMethodParametersUpdate.get("tempReturnObj");
		String strBaseObjectID = (String) mapMethodParametersUpdate.get("strBaseObjectID");
		Map<String, Object> mpUpdatedValue = (Map<String, Object>) mapMethodParametersUpdate.get("mpUpdatedValue");
		Map<String, Object> mpOldValueMap = (Map<String, Object>) mapMethodParametersUpdate.get("mpOldValueMap");
		String name = (String) mapMethodParametersUpdate.get("name");
		String strType = (String) mapMethodParametersUpdate.get("strType");
		String strKey = (String) mapMethodParametersUpdate.get("strKey");
		
		DomainRelationship domRel = DomainRelationship.newInstance(context, strKey);
		Map<String, Object> mpData= (Map) mpUpdatedValue.get(strKey);
		Map<String, Object> mpOldData= (Map) mpOldValueMap.get(strKey);
		Map<String, Object> mpAttributeMap = new HashMap<>();
		Map<String, Object> mpAttributeOldValueMap = new HashMap<>();
		String strUpdatKey = null;
		String strUpdatVal = null;
		boolean bAddPRAssignee = false;
		boolean bRemovePRAssignee = false;
		String strAssignmentRole = null;
		String strClaimRequestId = null;
		String strPersonId = null;
		StringList slRelSelects = new StringList(3);
		slRelSelects.add(DomainObject.getAttributeSelect(ATTRIBUTE_ASSIGNMENTS_ROLE));
		slRelSelects.add("from.id");
		slRelSelects.add("to.id");
		Map mpRelInfo = null;
		Object objUpdateValue;
		Object objOldValue;
		for (String strUpdateKey : mpData.keySet())  
		{
			if(strUpdateKey.contains("attribute["))
			{
				strUpdatKey = (strUpdateKey.replace("attribute[","")).replace("]","") ;
				
				objUpdateValue = mpData.get(strUpdateKey);
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
				formatAttributeMap(mpAttributeMap, objUpdateValue, strUpdatKey);
				if(null != mpOldData && !mpOldData.isEmpty())
				{
					objOldValue = mpOldData.get(strUpdateKey);
					strUpdatVal = objUpdateValue.toString();					
					formatAttributeMap(mpAttributeOldValueMap, objOldValue, strUpdatKey);
				}
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
			}
			//For adding/removing PR assignee as co owner
			if(ATTRIBUTE_ASSIGNMENTS_ROLE.equals(strUpdatKey))
			{
				mpRelInfo = (Map) ((MapList) DomainRelationship.getInfo(context, new String[]{strKey} , slRelSelects)).get(0);
				strAssignmentRole = (String) mpRelInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_ASSIGNMENTS_ROLE));
				strClaimRequestId = (String) mpRelInfo.get("from.id");
				strPersonId = (String) mpRelInfo.get("to.id");
				//If Role set as PR
				if("PR".equals(strUpdatVal))
				{
					bAddPRAssignee = true;
				} 
				//If Role changed from PR
				else if(UIUtil.isNotNullAndNotEmpty(strAssignmentRole) && "PR".equals(strAssignmentRole))
				{
					bRemovePRAssignee = true;
				}
			}
		}
		boolean isContextPushed = false;
		try 
		{
			if(null!=mpAttributeMap && !mpAttributeMap.isEmpty())
			{			
				//DCM (DS) 2022x-02 CW - REQ 46455 - The system shall allow the collaborator to mark their collaboration is complete - START
				
				if(hasCollaborationAttributes(context, mpAttributeMap)|| hasCommentAttributes(context, mpAttributeMap))
				{
					//PushContext to update attribute on relationship for user who is assigned, but does not have modify access
					isContextPushed = true;
					ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				}
				//DCM (DS) 2022x-01 CW ALM-51457 - Two users editing functional comments at the same time - START
				appendCommentValues(context, mpAttributeMap, domRel);
				//DCM (DS) 2022x-01 CW ALM-51457 - Two users editing functional comments at the same time - END
				//DCM (DS) 2022x-02 CW - REQ 46455 - The system shall allow the collaborator to mark their collaboration is complete - END
				domRel.setAttributeValues(context, mpAttributeMap);
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
				if(STRING_CLAIM.equals(strType) || STRING_DISCLAIMER.equals(strType) || TYPE_PG_CLAIM.equals(strType) || TYPE_PG_DISCLAIMER.equals(strType))
				{
					updateHistoryOnBaseObject(context, mpAttributeMap, mpAttributeOldValueMap, new StringBuilder(strType).append(CONST_WHITE_SPACE).append(name).toString(), strBaseObjectID);
				}
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
				if(bAddPRAssignee)
				{
					addCoOwner(context, strClaimRequestId, strPersonId, STRING_PR_ASSIGNEE);
				}
				else if(bRemovePRAssignee)
				{
					deleteCoOwner(context, strClaimRequestId, EMPTY_STRING, name+"_PRJ", STRING_PR_ASSIGNEE);
				}
			}
			tempReturnObj.add("name", name);
			tempReturnObj.add(STRING_STATUS, STATUS_SUCCESS);
		}
		finally {
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
	}
	
	/**Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim
	 * This Method formats Attribute map without selectable
	 * @param mpAttributeMap
	 * @param objUpdateValue
	 * @param objUpdateValue
	 * @return
	 * @throws Exception 
	 */
	public void formatAttributeMap(Map<String, Object> mpAttributeMap, Object objUpdateValue, String strUpdatKey)
	{
		try {
			if(objUpdateValue instanceof Integer)
			{
				mpAttributeMap.put(strUpdatKey, String.valueOf(objUpdateValue));
			}
			else if(objUpdateValue instanceof StringList)
			{
				mpAttributeMap.put(strUpdatKey, objUpdateValue);
				
			}
			else if(objUpdateValue instanceof ArrayList)
			{
				
				mpAttributeMap.put(strUpdatKey, StringList.create((ArrayList)objUpdateValue));
			}
			else 
			{
				mpAttributeMap.put(strUpdatKey, objUpdateValue.toString());
			}
		}catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		
	}

	/**This Method Promote and Demote Object.
	 * @param context
	 * @param request
	 * @param typeObj
	 * @param strEvent
	 * @return
	 * @throws Exception 
	 * @throws MatrixException 
	 */
	public String approveRejectObject(Context context, String objectId,	String strEvent) throws MatrixException 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strTNR =DomainConstants.EMPTY_STRING;
		String strEventMessage = DomainConstants.EMPTY_STRING;
		String strStatus = STATUS_INFO;
		try {
			StringList slObjectList = StringUtil.split(objectId, CONSTANT_STRING_COMMA);
			DomainObject domObj = DomainObject.newInstance(context);
			StringList slSelect = new StringList(DomainConstants.SELECT_NAME);
			slSelect.add(DomainConstants.SELECT_TYPE);
			slSelect.add(DomainConstants.SELECT_REVISION);
			slSelect.add(DomainConstants.SELECT_POLICY);
			slSelect.add(DomainConstants.SELECT_CURRENT);
			slSelect.add(DomainConstants.SELECT_ID);
			slSelect.add(FROM_OBJECT_ROUTE_TO_FROM_ROUTE_NODE_TO_NAME);
			slSelect.add(SELECT_TASK_ID);
			Map mpContentData;
			MapList mlObjInfo = null;
			if(BusinessUtil.isNotNullOrEmpty(slObjectList))
			{
				mlObjInfo = DomainObject.getInfo(context, slObjectList.toArray(new String[0]), slSelect);
			}
			for (int iCount = 0; iCount<mlObjInfo.size(); iCount++)
			{
				mpContentData = (Map) mlObjInfo.get(iCount);
				strTNR = (String) mpContentData.get(DomainConstants.SELECT_NAME);
				domObj.setId((String) mpContentData.get(DomainConstants.SELECT_ID));
				try
				{
					if(STRING_APPROVED.equals(strEvent))
					{
						domObj.promote (context);
					}
					else
					{
						domObj.demote(context);
					}
				}
				catch (Exception e) {
					logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
					strEventMessage =  new StringBuilder(strEventMessage).append(strTNR).append(" :: ").append(e.getMessage()).append(CONST_NEW_LINE).toString();
					continue;
				}
				strEventMessage =new StringBuilder(strEventMessage).append(strTNR).append(" :: ").append(CONST_WHITE_SPACE).append(strEvent).append(CONST_WHITE_SPACE).append("Successfully").append(CONST_NEW_LINE).toString();
			}

			jsonReturnObj.add(STRING_STATUS, strStatus);
			jsonReturnObj.add(STRING_MESSAGE, strEventMessage);
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonReturnObj.add(STRING_STATUS, strStatus);
			jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
		}
		return jsonReturnObj.build().toString();
	}

	/** This Method use to Delete Object.
	 * @param context
	 * @param request
	 * @param strOjectId
	 * @return
	 * @throws java.lang.Exception 
	 */
	public String deleteObject(matrix.db.Context context, String strOjectId) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strEventMessage = DomainConstants.EMPTY_STRING;
		String strStatus = STATUS_ERROR;

		try {
			//Pushing context to allow delete to happen even if there is no disconnect access on connected objects
			ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			//DCM (DS) 2022x-01 CW - Multi Object Delete - START
			StringList slObjectList = StringUtil.split(strOjectId, CONSTANT_STRING_COMMA);
			DomainObject.deleteObjects(context, (String[])slObjectList.toArray(new String[slObjectList.size()]));
			//DCM (DS) 2022x-01 CW - Multi Object Delete - END
			strStatus = STATUS_SUCCESS;
			strEventMessage = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.DeleteObject.Message", context.getLocale());
		}catch(Exception ex){
			strEventMessage = ex.getMessage();
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
		} finally
		{
			ContextUtil.popContext(context);
			jsonReturnObj.add(STRING_STATUS, strStatus);
			jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
		}
		return jsonReturnObj.build().toString();
	}

	/**This Method Used to delete relationship.
	 * @param context
	 * @param strRelId
	 * @return
	 * @throws FrameworkException 
	 */
	public String deleteConnection(Context context, Map<String,String> mpRequestMap) throws Exception  {
		String strRelId = mpRequestMap.get("ConnectionIds");
		String strRelName = mpRequestMap.get("relationship");
		String strBusNames = mpRequestMap.get("conectedBusName");
		String strRoles = mpRequestMap.get("roles");
		String strObjId = mpRequestMap.get("Busid");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strEventMessage = DomainConstants.EMPTY_STRING;
		String strStatus = STATUS_INFO;

		StringList slObjectList =StringUtil.split(strRelId, CONSTANT_STRING_COMMA);
		StringList slRoles = StringUtil.split(strRoles, CONSTANT_STRING_COMMA);
		StringList slBusNames = StringUtil.split(strBusNames, CONSTANT_STRING_COMMA);
		String strRole = null;
		String strPersonName = null;

		for(int i=0;i<slObjectList.size();i++)
		{
			try {
				DomainRelationship.disconnect(context, slObjectList.get(i));
				//To disconnect PR assignee as Co owners
				//DCM(DS) 2022x-01 CW - REQ 45599 - Modified for Assignment changes
				if(RELATIONSHIP_ASSIGNMENTS.equals(strRelName) && BusinessUtil.isNotNullOrEmpty(slRoles))
				{
					strRole = slRoles.get(i);
					strPersonName = slBusNames.get(i);
					if("PR".equals(strRole)) {
						deleteCoOwner(context, strObjId, EMPTY_STRING, strPersonName+"_PRJ", STRING_PR_ASSIGNEE);
					}
				}
				strEventMessage = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.RemoveOwnership.Success", context.getLocale()) ;
			} catch (FrameworkException e) {
				strEventMessage = new StringBuilder(CONST_NEW_LINE).append(e.getMessage()).toString();
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			}
		}
		if(DomainConstants.RELATIONSHIP_PROTECTED_ITEM.equals(strRelName))
		{
			disconnectfromConnectedClaims(context,mpRequestMap);
		}
		jsonReturnObj.add(STRING_STATUS, strStatus);
		jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
		return jsonReturnObj.build().toString();
	}

	/**
	 * This method disconnects the connected relationship in child claims
	 * @param context
	 * @param mpRequestMap
	 * @throws FrameworkException
	 */
	private void disconnectfromConnectedClaims(Context context, Map<String, String> mpRequestMap) throws FrameworkException {
		String strID= mpRequestMap.get("Busid");
		String strConnectedClassName= mpRequestMap.get("conectedBusName");
		if(UIUtil.isNotNullAndNotEmpty(strID))
		{
			StringList slValidClaims =getValidClaimsFromClaimRequest(context, strID);
			if(BusinessUtil.isNotNullOrEmpty(slValidClaims))
			{
				String[] arr = BusinessUtil.toStringArray(slValidClaims);

				StringList slSelect = new StringList();
				slSelect.add(SELECT_TO_PROTECTED_ITEM_FROM_ID);
				slSelect.add(SELECT_TO_PROTECTED_ITEM_FROM_NAME);
				slSelect.add(SELECT_TO_PROTECTED_ITEM);
				MapList mlIPClassData = DomainObject.getInfo(context, arr, slSelect);
				if(BusinessUtil.isNotNullOrEmpty(mlIPClassData))
				{
					Map mpData;
					StringList slID ;
					StringList slName;
					StringList slRelId;
					StringList slRelIdsToDisconnect = new StringList();
					for(int i=0;i<mlIPClassData.size();i++)
					{
						mpData = (Map) mlIPClassData.get(i);
						removeBellChar(mpData);
						if(mpData.containsKey(SELECT_TO_PROTECTED_ITEM_FROM_ID))
						{
							slID = returnStringListForObject(mpData.get(SELECT_TO_PROTECTED_ITEM_FROM_ID));
							slName = returnStringListForObject(mpData.get(SELECT_TO_PROTECTED_ITEM_FROM_NAME));
							slRelId = returnStringListForObject(mpData.get(SELECT_TO_PROTECTED_ITEM));
							for(int j=0;j<slID.size();j++)
							{
								if(UIUtil.isNotNullAndNotEmpty(strConnectedClassName)&& strConnectedClassName.contains(slName.get(j)))
								{
									slRelIdsToDisconnect.add(slRelId.get(j));
								}
							}
						}
					}
					if(BusinessUtil.isNotNullOrEmpty(slRelIdsToDisconnect))
					{
						String[] arrRelIdsToDisconnect = new String[slRelIdsToDisconnect.size()];
						slRelIdsToDisconnect.toArray(arrRelIdsToDisconnect);
						DomainRelationship.disconnect(context, arrRelIdsToDisconnect);
					}
				}
			}

		}

	}

	/**This method return related Data of objects in from side
	 * @param context
	 * @param strObjectId
	 * @param strSelect 
	 * @param strDocumentSelect 
	 * @param strRelatedSelect 
	 * @param strExecutionsSelect 
	 * @param strRevisionSelect 
	 * @param strPOCSelect 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String getRelatedDataInfo(matrix.db.Context context, Map<String,String> mpRequestMap ) throws Exception {
		String strObjectId =mpRequestMap.get("id");
		String strTypePatten=mpRequestMap.get("types");
		String strRelPatten=mpRequestMap.get("rels");
		String strPOCSelect=mpRequestMap.get("POCSelect");
		String strRevisionSelect=mpRequestMap.get("RevisionSelect");
		String strRelatedSelect=mpRequestMap.get("RelatedSelect");
		String strAssignmentSelect=mpRequestMap.get("AssignmentSelect");
		String strRelSelect=mpRequestMap.get("RelSelect");
		String strOwnershipSelect=mpRequestMap.get("OwnershipColSelect");
		String strClaimSupportColSelect=mpRequestMap.get("ClaimSupportColSelect");
		String strMasterCopyElementsSelect=mpRequestMap.get("MasterCopyElementsSelect");
		String strCopyListColSelect=mpRequestMap.get("CopyListColSelect");
		String strGetTo = mpRequestMap.get("getTo");
		String strGetFrom = mpRequestMap.get("getFrom");
		boolean bGetTo = UIUtil.isNotNullAndNotEmpty(strGetTo) && "true".equalsIgnoreCase(strGetTo);
		boolean bGetFrom = UIUtil.isNotNullAndNotEmpty(strGetFrom) && "true".equalsIgnoreCase(strGetFrom);
		boolean bEditAccessOnContextObject = FrameworkUtil.hasAccess(context,DomainObject.newInstance(context,strObjectId),"modify");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();

		try {
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)){
				StringList slBusSelects = new StringList();
				slBusSelects.addAll(StringUtil.split(strRelatedSelect, CONSTANT_STRING_COMMA));
				slBusSelects.addAll(StringUtil.split(strPOCSelect, CONSTANT_STRING_COMMA));
				slBusSelects.addAll(StringUtil.split(strAssignmentSelect, CONSTANT_STRING_COMMA));
				slBusSelects.addAll(StringUtil.split(strMasterCopyElementsSelect, CONSTANT_STRING_COMMA));
				slBusSelects.addAll(StringUtil.split(strCopyListColSelect, CONSTANT_STRING_COMMA));
				slBusSelects.add(FROM_ACTIVE_VERSION_TO_TITLE);
				slBusSelects.add(DomainConstants.SELECT_ID);
				slBusSelects.add(DomainConstants.SELECT_POLICY);
				slBusSelects.add(DomainConstants.SELECT_CURRENT);
				slBusSelects.add(DomainConstants.SELECT_REVISION);
				slBusSelects.add(DomainConstants.SELECT_OWNER);
				slBusSelects.add(SELECT_PHYSICALID);
				//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
				slBusSelects.add(SELECT_FROM_PG_CLAIM_SUPPORT_TO_OWNER);
				slBusSelects.add(SELECT_FROM_PG_CLAIM_SUPPORT_TO_ID);
				slBusSelects.add(SELECT_FROM_PG_CLAIM_SUPPORT_TO_REVISION);
				slBusSelects.add(SELECT_FROM_PG_CLAIM_SUPPORT_REL_ID);
				//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
				slBusSelects.add(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE));
				//US 1608: Disclaimer Data Model changes - Create changes :Start
				slBusSelects.add(DomainObject.getAttributeSelect(ATTRIBUTE_DISCLAIMERNAME_RTE));
				//US 1608: Disclaimer Data Model changes - Create changes :End
				StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);
				slRelSelects.add(DomainRelationship.SELECT_DIRECTION);
				//DCM(DS) US_1598_ClaimTabChanges to Update Product config data -Start
				slRelSelects.add(FROMMID_PG_CLAIM_PRODUCT_CONFIGURATION_TO_ID);
				//DCM(DS) US_1598_ClaimTabChanges to Update Product config data -End
				slRelSelects.addAll(StringUtil.split(strRelSelect, CONSTANT_STRING_COMMA));
				
				//DCM Sprint 7 US-2624:Enable related parts at product configuration level instead at claim level : Start
				slRelSelects.add(SELECT_PRODUCTCONF_RELATEDPART_ID);
				slRelSelects.add(SELECT_PRODUCTCONF_RELATEDPART_NAME);
				slRelSelects.add(SELECT_PRODUCTCONF_RELATEDPART_REVISION);
				slRelSelects.add(SELECT_PRODUCTCONF_RELATEDPART_REL_ID);
				//DCM Sprint 7 US-2624:Enable related parts at product configuration level instead at claim level : End
				DomainObject domObj = DomainObject.newInstance(context, strObjectId);

				if((domObj.isKindOf(context,TYPE_PG_CLAIM_SUPPORT)|| domObj.isKindOf(context,TYPE_PG_CLAIM))&& BusinessUtil.isNotNullOrEmpty(strClaimSupportColSelect)) {
					slBusSelects.addAll(StringUtil.split(strClaimSupportColSelect, CONSTANT_STRING_COMMA));
				}

				MapList mlObjs = domObj.getRelatedObjects
						(
								context, //context
								strRelPatten, //relPattern
								strTypePatten, //typePattern
								slBusSelects, //busSelects
								slRelSelects, //relSelects
								bGetTo, // getTo
								bGetFrom, // getFrom
								(short) 1,  //recurseToLevel
								null, //busWhere
								DomainConstants.EMPTY_STRING, //relWhere
								0 //limit
								);

				Map<String, Object> mapObj;
				String strType;
				String strRel;
				String strSymbolicValue;
				String strPropkey ; 
				JsonArrayBuilder jsonAllRelated = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrayClaims = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrayParts = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrayDerived = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrayAssignment = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrayClaimSupport = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrayCopyList = Json.createArrayBuilder();
				JsonArrayBuilder jsonArrayMasterCopyElements = Json.createArrayBuilder();

				String strNLSType = null;
				String strNLSCurrent = null;

				for(int i=0;i<mlObjs.size();i++)
				{
					mapObj = (Map<String, Object>) mlObjs.get(i);
					strType =(String) mapObj.get(DomainConstants.SELECT_TYPE);
					mapObj.put(DomainConstants.SELECT_TYPE, getDisplayName(context,  "emxFramework.Type."+ strType,strType));
					strPropkey =new StringBuilder("emxFramework.State.").append((String) mapObj.get(DomainConstants.SELECT_POLICY)).append(".").append(((String) mapObj.get(DomainConstants.SELECT_CURRENT)).replace(CONST_WHITE_SPACE,"_")).toString();
					strNLSCurrent = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
							strPropkey, context.getLocale());

					if(!strPropkey.equals(strNLSCurrent))
					{
						mapObj.put(DomainConstants.SELECT_CURRENT,strNLSCurrent);
					}
					strRel =(String) mapObj.get(STRING_RELATIONSHIP);
					if((TYPE_PG_CLAIM.equals(strType) && RELATIONSHIP_PG_CLAIM_SUPPORT.equals(strRel))|| RELATIONSHIP_PG_CLAIMSUPPORT_REFERENCE.equalsIgnoreCase((String)mapObj.get(STRING_RELATIONSHIP)))
					{						
						mapObj.put(IS_EDITABLE, false);
					}else {
						mapObj.put(IS_EDITABLE,isEditable(context,mapObj));
					}
					if(RELATIONSHIP_ASSIGNMENTS.equals(strRel))
					{
						mapObj.put(IS_EDITABLE,bEditAccessOnContextObject);
						getTaskDetails(context,mapObj,strObjectId);
					}
					//Added for Disclaimer value fix
					mapObj.put(STRING_FILE,getStringListAsString(returnStringListForObject(mapObj.get(FROM_ACTIVE_VERSION_TO_TITLE))));
					//For hyperlink
					getIdNameOtherData(context, mapObj);
					// merge name Revision
					//US 1608: Disclaimer Data Model changes - Create changes :Start
					if(TYPE_PG_DISCLAIMER.equals(strType) || TYPE_PG_CLAIM.equals(strType) || TYPE_PG_CLAIM_REQUEST.equals(strType) || TYPE_PG_CLAIM_SUPPORT.equals(strType) || RELATIONSHIP_PG_CLAIMSUPPORT_REFERENCE.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP)))
					{
						mapObj.put(DomainConstants.SELECT_NAME,new StringBuilder((String)mapObj.get(DomainConstants.SELECT_NAME)).append(CONST_WHITE_SPACE).append((String)mapObj.get(DomainConstants.SELECT_REVISION)).toString());
					}
					if(TYPE_PG_DISCLAIMER.equals(strType))
					{
						mapObj.put(DomainObject.getAttributeSelect(ATTRIBUTE_CLAIMNAME_RTE),(String)mapObj.get(DomainObject.getAttributeSelect(ATTRIBUTE_DISCLAIMERNAME_RTE)));
					}
					//US 1608: Disclaimer Data Model changes - Create changes :End
					JsonObjectBuilder jsonObj = Json.createObjectBuilder();
					PGClaimModuler.map2JsonBuilder(jsonObj, mapObj);
					//for get Related Objects Window
					jsonAllRelated.add(jsonObj);
					//US 1608: Disclaimer Data Model changes - Create changes :Start
					if((TYPE_PG_CLAIM.equals(strType) 
							|| TYPE_PG_CLAIM_REQUEST.equals(strType) || TYPE_PG_DISCLAIMER.equals(strType))
							&& (RELATIONSHIP_CLAIMS.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP))
									|| RELATIONSHIP_PG_CLAIM_SUPPORT.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP)) 
									|| RELATIONSHIP_PG_DISCLAMER.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP))))
					{
						jsonArrayClaims.add(jsonObj);
					}
					//US 1608: Disclaimer Data Model changes - Create changes :End					
					else if (RELATIONSHIP_CLAIMREQUESTS.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP)) || RELATIONSHIP_PG_CLAIM_RELATED_PART.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP)) || RELATIONSHIP_PG_CLAIMSUPPORT_RELATED_PART.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP)))
					{
						jsonArrayParts.add(jsonObj);
					}
					else if(RELATIONSHIP_DERIVED.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP)))
					{
						jsonArrayDerived.add(jsonObj);
					}
					else if(RELATIONSHIP_ASSIGNMENTS.equalsIgnoreCase((String) mapObj.get(STRING_RELATIONSHIP)))
					{
						jsonArrayAssignment.add(jsonObj);
					}
					else if("pgCopyListClaimRequest".equalsIgnoreCase((String)mapObj.get(STRING_RELATIONSHIP)))
					{
						jsonArrayCopyList.add(jsonObj);
					}
					else if("pgMasterCopyClaim".equalsIgnoreCase((String)mapObj.get(STRING_RELATIONSHIP)))
					{
						jsonArrayMasterCopyElements.add(jsonObj);
					}
					else if(TYPE_PG_CLAIM_SUPPORT.equals(strType) || RELATIONSHIP_PG_CLAIMSUPPORT_REFERENCE.equalsIgnoreCase((String)mapObj.get(STRING_RELATIONSHIP))){
						jsonArrayClaimSupport.add(jsonObj);
					}
				}
				jsonReturnObj.add("Related", jsonArrayParts);
				jsonReturnObj.add(STRING_CLAIM, jsonArrayClaims);
				jsonReturnObj.add(RELATIONSHIP_DERIVED, jsonArrayDerived);
				jsonReturnObj.add("Assignment", jsonArrayAssignment);
				jsonReturnObj.add("ClaimSupport", jsonArrayClaimSupport);
				jsonReturnObj.add("CopyList", jsonArrayCopyList);
				jsonReturnObj.add("MasterCopyElements", jsonArrayMasterCopyElements);
				getClaimPOCOtherDetails(context,jsonReturnObj,strObjectId,strRevisionSelect);
				getOwnershipDetails(context,jsonReturnObj,strObjectId,strOwnershipSelect);
			}
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}

		return jsonReturnObj.build().toString();
	}

	private String getDisplayName(matrix.db.Context context, String strPropkey, String strType) throws FrameworkException {
		String strSymbolicValue;
		String strNLSType;
		strNLSType = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
				strPropkey, context.getLocale());
		if(!strPropkey.equals(strNLSType))
		{
			strSymbolicValue = strNLSType;
		}
		else
		{
			strSymbolicValue = strType;
		}
		
		return strSymbolicValue;
	}
	
	/**This method returns data for specific tab of objects
	 * @param context
	 * @param strObjectId
	 * @param strTabName 
	 * @return
	 * @throws Exception 
	 */
	public String getRelatedTabInfo(matrix.db.Context context, Map<String,String> mpRequestMap ) throws Exception {
		String strObjectId = mpRequestMap.get("id");
		String strTabName = mpRequestMap.get("tab");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();

		try {
			if(UIUtil.isNotNullAndNotEmpty(strObjectId) && UIUtil.isNotNullAndNotEmpty(strTabName)){
				//Added for History
				if(STRING_HISTORY.equalsIgnoreCase(strTabName))
				{
					jsonReturnObj.add(strTabName,getHistoryDataOfObject(context,strObjectId));
				}else if("Images".equalsIgnoreCase(strTabName))
				{
					jsonReturnObj.add(strTabName,getRelatedClaimsForImage(context,strObjectId));
				}else if("IPClass".equalsIgnoreCase(strTabName))
				{
					jsonReturnObj.add(strTabName,getRelatedDataAsPerRequest(context,mpRequestMap));
				}
			}
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}

		return jsonReturnObj.build().toString();
	}

	/** Get Related data as per given Request Parameter
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws FrameworkException
	 */
	private JsonArrayBuilder getRelatedDataAsPerRequest(Context context, Map<String, String> mpRequestMap) throws FrameworkException {
		JsonArrayBuilder jsonArr = Json.createArrayBuilder();
		String strObjectId = mpRequestMap.get("id");
		DomainObject domObj = DomainObject.newInstance(context, strObjectId);
		String strGetTo = mpRequestMap.get("getTo");
		String strGetFrom = mpRequestMap.get("getFrom");
		String strTypePatten=mpRequestMap.get("types");
		String strRelPatten=mpRequestMap.get("rels");
		String strRelSelect=mpRequestMap.get("relSelect");
		String strBusSelects=mpRequestMap.get("objectSelect");
		StringList slBusSelects =new StringList(DomainRelationship.SELECT_ID);
		if(UIUtil.isNotNullAndNotEmpty(strBusSelects))
		{
			slBusSelects.addAll(StringUtil.split(strBusSelects, CONSTANT_STRING_COMMA));	
		}
		
		StringList slRelSelects =new StringList(DomainRelationship.SELECT_ID);
		if(UIUtil.isNotNullAndNotEmpty(strRelSelect))
		{
			slRelSelects.addAll(StringUtil.split(strRelSelect, CONSTANT_STRING_COMMA));
		}		
		boolean bGetTo = UIUtil.isNotNullAndNotEmpty(strGetTo) && "true".equalsIgnoreCase(strGetTo);
		boolean bGetFrom = UIUtil.isNotNullAndNotEmpty(strGetFrom) && "true".equalsIgnoreCase(strGetFrom);
		MapList mlObjs = domObj.getRelatedObjects
				(
						context, //context
						strRelPatten, //relPattern
						strTypePatten, //typePattern
						slBusSelects, //busSelects
						slRelSelects, //relSelects
						bGetTo, // getTo
						bGetFrom, // getFrom
						(short) 1,  //recurseToLevel
						null, //busWhere
						DomainConstants.EMPTY_STRING, //relWhere
						0 //limit						
						);
		if(BusinessUtil.isNotNullOrEmpty(mlObjs))
		{
			Map mpData;
			JsonObjectBuilder jsonObj;
			String strType;
			for(int i=0;i<mlObjs.size();i++)
			{
				mpData = (Map) mlObjs.get(i);
				strType =(String) mpData.get(DomainConstants.SELECT_TYPE);
				mpData.put(DomainConstants.SELECT_TYPE, getDisplayName(context, "emxFramework.Type."+ strType, strType));
				jsonObj = Json.createObjectBuilder();
				PGClaimModuler.map2JsonBuilder(jsonObj, mpData);
				jsonArr.add(jsonObj);
			}
		}
		return jsonArr;
	}

	/**Get Task Details of assign to person.
	 * @param context
	 * @param mapObj
	 * @param strObjectId
	 * @throws FrameworkException 
	 */
	private void getTaskDetails(Context context, Map<String, Object> mapObj, String strObjectId) throws FrameworkException {

		DomainObject domObj = DomainObject.newInstance(context, strObjectId);
		StringList slObject = new StringList();
		slObject.addElement(SELECT_TASK_ASSIGNEE_NAME);
		slObject.addElement(SELECT_TASK_DUEDATE);
		slObject.addElement(SELECT_TASK_STATUS);
		slObject.addElement(SELECT_TASK_NAME);
		slObject.addElement(SELECT_TASK_ID);
		slObject.addElement(SELECT_TASK_COMMENTS);
		slObject.addElement(SELECT_TASK_INSTRUCTIONS);
		slObject.addElement(SELECT_ROUTE_STATUS);

		Map mpTaskData = domObj.getInfo(context, slObject,slObject);
		String strPersonName =(String) mapObj.get(DomainConstants.SELECT_NAME);
		String strPersonFunction =(String) mapObj.get(DomainObject.getAttributeSelect(ATTRIBUTE_ASSIGNMENTS_FUNCTION));
		StringList slAssignedPersonName = returnStringListForObject(mpTaskData.get(SELECT_TASK_ASSIGNEE_NAME));
		String strRouteInstructions = null; 
		String strTaskDueDate = null;
		String strTaskComments = null;
		String strTaskStatus = null;
		String strRouteStatus = null;
		MapList mlTaskData = null;
		Map mpTaskName = null;
		if(BusinessUtil.isNotNullOrEmpty(slAssignedPersonName))
		{
			for(int i=0;i<slAssignedPersonName.size();i++)
			{
				strRouteInstructions = (returnStringListForObject(mpTaskData.get(SELECT_TASK_INSTRUCTIONS))).get(i) ;
				if(strPersonName.equals(slAssignedPersonName.get(i)) && strPersonFunction.equalsIgnoreCase(strRouteInstructions))
				{
					strTaskDueDate = (returnStringListForObject(mpTaskData.get(SELECT_TASK_DUEDATE))).get(i);
					strTaskComments = (returnStringListForObject(mpTaskData.get(SELECT_TASK_COMMENTS))).get(i);
					strTaskStatus = returnStringListForObject(mpTaskData.get(SELECT_TASK_STATUS)).get(i);
					strRouteStatus = returnStringListForObject(mpTaskData.get(SELECT_ROUTE_STATUS)).get(i);
					mlTaskData = new MapList();
					mpTaskName = new HashMap();
					mpTaskName.put("id",(returnStringListForObject(mpTaskData.get(SELECT_TASK_ID))).get(i));
					mpTaskName.put("name",(returnStringListForObject(mpTaskData.get(SELECT_TASK_NAME))).get(i));
					mlTaskData.add(mpTaskName);
					if(UIUtil.isNotNullAndNotEmpty(strTaskStatus) && STRING_NONE.equalsIgnoreCase(strTaskStatus) && UIUtil.isNotNullAndNotEmpty(strRouteStatus) && STATE_STARTED.equalsIgnoreCase(strRouteStatus))
					{
						strTaskStatus = STATE_AWAITING_APPROVAL ;
					} 
					else if(UIUtil.isNotNullAndNotEmpty(strTaskStatus) && STATE_APPROVE.equalsIgnoreCase(strTaskStatus))
					{
						strTaskStatus = STRING_APPROVED ;
					}
					else if(UIUtil.isNotNullAndNotEmpty(strTaskStatus) && STATE_REJECT.equalsIgnoreCase(strTaskStatus))
					{
						strTaskStatus = STATE_REJECTED ;
					}
					else if (UIUtil.isNotNullAndNotEmpty(strTaskStatus) && STRING_NONE.equalsIgnoreCase(strTaskStatus) && UIUtil.isNotNullAndNotEmpty(strRouteStatus) && STATE_STOPPED.equalsIgnoreCase(strRouteStatus))
					{
						strTaskDueDate = EMPTY_STRING;
						strTaskComments = EMPTY_STRING;
						strTaskStatus = EMPTY_STRING;
						mlTaskData = new MapList();
					}

					mapObj.put("attribute[Scheduled Completion Date]", strTaskDueDate);
					mapObj.put("TaskComments", strTaskComments);
					mapObj.put("TaskStatus", strTaskStatus);
					mapObj.put("TaskName", mlTaskData);
				}
			}
		}
	}

	/**This method Return Id name and Doc Related to Claim Support with | separated .
	 * e.g <<ID>>|<<Name>>|<<DocName1>>/<<DocName2>>
	 * @param context
	 * @param mapObj
	 * @throws FrameworkException
	 */
	private void getIdNameOtherData(Context context, Map<String, Object> mapObj) throws FrameworkException {
		String strValue;
		Object strKeyValueName;
		Object strKeyValueId;
		StringList slKeyValue;
		StringList slKeyValueId = null;
		String strKeyId;
		//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
		Object strKeyValueRevision;
		Object strKeyValueOwner;
		StringList slKeyRevision;
		StringList slKeyValueOwner = null;
		String strKeyRevision;
		String strKeyOwner = null;
		//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
		MapList mlDocData = null;
		Map mpSupportData = new HashMap<String, Object>();
		MapList mlSupportData = new MapList();
		Object strRelId;
		StringList slRelId;
		for(String key:mapObj.keySet())
		{
			if(key.contains(TYPE_PG_CLAIM_SUPPORT) && key.contains(".name")&&mapObj.containsKey(key.replace(".name", ".id")))
			{
				strKeyValueName = mapObj.get(key);
				strKeyValueId = mapObj.get(key.replace(".name", ".id"));
				//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
				strKeyValueOwner = mapObj.get(key.replace(".name", ".owner"));
				strKeyValueRevision =  mapObj.get(key.replace(".name", ".revision"));
				strRelId = mapObj.get(SELECT_FROM_PG_CLAIM_SUPPORT_REL_ID);
				//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
				if(strKeyValueName instanceof String && ((String) strKeyValueName).contains(CONSTANT_STRING_COMMA))
				{
					strKeyValueName = StringUtil.split((String)strKeyValueName,CONSTANT_STRING_COMMA);
					strKeyValueId = StringUtil.split((String)strKeyValueId,CONSTANT_STRING_COMMA);
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
					strKeyValueOwner = StringUtil.split((String)strKeyValueOwner,CONSTANT_STRING_COMMA);
					strKeyValueRevision = StringUtil.split((String)strKeyValueRevision,CONSTANT_STRING_COMMA);
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
				}

				if(strKeyValueName instanceof String)
				{
					strKeyId = key.replace(".name", ".id");
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
					strKeyRevision = key.replace(".name", ".revision");
					strKeyOwner	= key.replace(".name", ".owner");
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
					if(strKeyId.equals(SELECT_FROM_PG_CLAIM_SUPPORT_TO_ID) && UIUtil.isNotNullAndNotEmpty((String)mapObj.get(strKeyId)))
					{
						if(DENIED.equalsIgnoreCase((String)mapObj.get(strKeyId)))
						{
							return;
						}
						mlDocData = getFileIdAndTitle(context,(String)mapObj.get(strKeyId));
					}
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START

					//strValue = new StringBuilder((String)mapObj.get(strKeyId)).append(CONST_STR_PIPE).append((String)strKeyValueName).append(CONST_WHITE_SPACE).append((String)mapObj.get(strKeyRevision)).toString();
					mpSupportData = new HashMap<String, Object>();
					mpSupportData.put("id", mapObj.get(strKeyId));
					mpSupportData.put("name", strKeyValueName);
					mpSupportData.put("revision", mapObj.get(strKeyRevision));
					mpSupportData.put("relId", strRelId);
					mpSupportData.put("owner",mapObj.get(strKeyOwner));
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
					if(BusinessUtil.isNotNullOrEmpty(mlDocData))
					{
						//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
						//strValue = new StringBuilder(strDocData.replace(",", "/"));
						mpSupportData.put("document",mlDocData);
						//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
					}
					mlSupportData.add(mpSupportData);
					mapObj.put(key, mlSupportData);
				}
				else if(strKeyValueName instanceof StringList)
				{
					slKeyValue =(StringList) strKeyValueName;
					slKeyValueId =(StringList)strKeyValueId;
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
					slKeyValueOwner = (StringList)strKeyValueOwner;
					slKeyRevision = (StringList)strKeyValueRevision;
					slRelId =  (StringList)strRelId;
					//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
					strValue = EMPTY_STRING;
					for(int j=0;j<slKeyValue.size();j++)
					{
						if(key.replace(".name", ".id").equals(SELECT_FROM_PG_CLAIM_SUPPORT_TO_ID) && UIUtil.isNotNullAndNotEmpty(slKeyValueId.get(j)))
						{
							mlDocData  = getFileIdAndTitle(context,slKeyValueId.get(j));
						}
						//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
						mpSupportData = new HashMap<String, Object>();
						mpSupportData.put("id", slKeyValueId.get(j));
						mpSupportData.put("name", slKeyValue.get(j));
						mpSupportData.put("revision", slKeyRevision.get(j));
						mpSupportData.put("relId",slRelId.get(j) );
						mpSupportData.put("owner",slKeyValueOwner.get(j));
						//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
						if(BusinessUtil.isNotNullOrEmpty(mlDocData))
						{
							//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - START
							mpSupportData.put("document",mlDocData);
							//DCM (DS) 2022x-01 CW - REQ 44754 - Allow access to Supporting Documents only for owner - END
						}
						mlSupportData.add(mpSupportData);
					}
					mapObj.put(key, mlSupportData);
				}
			}
		}
	}

	/** get File Data of  Object like <FILE_ID>:<FILE_TITLE>
	 * @param context
	 * @param strClaimSupport
	 * @return
	 * @throws FrameworkException
	 */
	private MapList getFileIdAndTitle(Context context, String strClaimSupport) throws FrameworkException {
		DomainObject domClaimSupport = DomainObject.newInstance(context,strClaimSupport);
		StringList slFileSelect = new StringList(FROM_ACTIVE_VERSION_TO_TITLE);
		slFileSelect.add(FROM_ACTIVE_VERSION_TO_ID);
		Map mpFiles = domClaimSupport.getInfo(context,slFileSelect, slFileSelect);
		StringList slDocTitle = returnStringListForObject(mpFiles.get(FROM_ACTIVE_VERSION_TO_TITLE));
		StringList slDocId = returnStringListForObject(mpFiles.get(FROM_ACTIVE_VERSION_TO_ID));
		StringList slDOcData = new StringList();
		String strDoc;
		Map mpData = new HashMap();
		MapList mlData = new MapList();
		for(int i=0;i<slDocTitle.size();i++)
		{
			mpData = new HashMap();
			mpData.put("id",(String)slDocId.get(i));
			mpData.put("name",(String)slDocTitle.get(i));
			mlData.add(mpData);
		}
		return mlData;
	}

	/** This Method return Other Details of Claim like History Related Executions etc.
	 * @param context
	 * @param jsonReturnObj
	 * @param strObjectId
	 * @param strDocumentSelect 
	 * @param strPOCSelect 
	 * @param strExecutionsSelect 
	 * @param strRelatedSelect 
	 * @throws MatrixException 
	 */
	private void getClaimPOCOtherDetails(Context context, JsonObjectBuilder jsonReturnObj, String strObjectId, String strRevisionSelect) throws MatrixException {
		HashMap mArgsMap = new HashMap();
		mArgsMap.put(STRING_OBJECT_ID, strObjectId);
		mArgsMap.put("strRevisionSelect", strRevisionSelect);
		Map mRangeMap =getClaimPOCOtherDetails(context, mArgsMap);
		jsonReturnObj.add("Revisions",(JsonArrayBuilder) mRangeMap.get("Revisions"));
	}

	/** This Method return Ownership details of object.
	 * @param context
	 * @param jsonReturnObj
	 * @param strObjectId
	 * @param strOwnershipSelect 
	 * @throws MatrixException 
	 */
	private void getOwnershipDetails(Context context, JsonObjectBuilder jsonReturnObj, String strObjectId, String strOwnershipSelect) throws Exception {
		jsonReturnObj.add("Ownership",getOwnershipData(context, strObjectId, strOwnershipSelect));
	}

	/**
	 * DSM(DS) 2018x.5 - This is a utility method to get all the elements in a StringList as a comma separated String
	 * @param slValues : Input StringList
	 * @return <code>String</code> as described above
	 * @throws Exception
	 */
	public String getStringListAsString(StringList slValues) {
		StringBuilder sbOut = new StringBuilder();
		if(null != slValues){
			int iListSize = slValues.size();
			for (int i = 0; i < iListSize; i++) {
				sbOut.append(slValues.get(i));
				if (i < (iListSize-1)){
					sbOut.append(CONSTANT_STRING_COMMA);
				}
			}
		}else{
			return EMPTY_STRING;
		}
		return sbOut.toString();
	}

	/** This method is used to return StringList for selected Object 
	 * @param Object
	 * @return StringList
	 *  
	 */
	public StringList returnStringListForObject(Object obj)
	{
		StringList sl = new StringList();
		if(obj != null)
		{
			if(obj instanceof StringList)
			{
				sl = (StringList)obj;
			}
			else if(obj instanceof ArrayList)
			{
				sl = StringList.create((ArrayList)obj);
			}
			else
			{
				sl.addElement((String)obj);
			}
		}
		return sl;
	}

	/**Requirement 45425 : The user shall have a graphical view of number of objects in their work space for each maturity state on claim support and claim request types.
	 * @param context
	 * @return
	 * @throws FrameworkException
	 */
	@SuppressWarnings({ "unchecked"})
	public Response getCount(Context context) throws FrameworkException 
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonObjectBuilder stateData = Json.createObjectBuilder();
		String typeObj = new StringBuffer(TYPE_PG_CLAIM_REQUEST).append(CONSTANT_STRING_COMMA).append(TYPE_PG_CLAIM_SUPPORT).toString();
		try {
			output.add(STRING_MESSAGE, STRING_KO);

			StringList slObj = new StringList();
			slObj.add(DomainConstants.SELECT_NAME);
			slObj.add(DomainConstants.SELECT_REVISION);
			slObj.add(DomainConstants.SELECT_POLICY);
			slObj.add(DomainConstants.SELECT_CURRENT);
			slObj.add(DomainConstants.SELECT_OWNER);
			slObj.add(DomainConstants.SELECT_OWNERSHIP);
			slObj.add(SELECT_TASK_ASSIGNEE_NAME);
			slObj.add(SELECT_ASSIGNMENTS_TO_NAME);
			slObj.add(SELECT_ASSIGNMENTS_COLLABORATION_STATUS);
			slObj.add(SELECT_ROUTE_STATUS);
			slObj.add(SELECT_ROUTE_TASK_STATUS);
			slObj.add(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE));

			StringList slMultiSelect = new StringList();
			slMultiSelect.add(SELECT_ASSIGNMENTS_TO_NAME);
			slObj.add(DomainConstants.SELECT_ID);
			String strWherePolicy = DomainConstants.SELECT_POLICY+" != "+STATE_VERSION;

			StringList multiValueList = new StringList(SELECT_TASK_ASSIGNEE_NAME);
			multiValueList.add(SELECT_ASSIGNMENTS_TO_NAME);
			multiValueList.add(SELECT_ASSIGNMENTS_COLLABORATION_STATUS);
			multiValueList.add(SELECT_ROUTE_STATUS);
			multiValueList.add(SELECT_ROUTE_TASK_STATUS);
			multiValueList.add(DomainConstants.SELECT_OWNERSHIP);

			MapList mlObjs = DomainObject.findObjects(
					context,					// eMatrix context
					typeObj,					// type pattern    
					DomainConstants.QUERY_WILDCARD,// name pattern
					DomainConstants.QUERY_WILDCARD,// rev pattern
					DomainConstants.QUERY_WILDCARD,//owner
					VAULT_ESERVICE_PRODUCTION,	// Vault Pattern	
					strWherePolicy,			// where expression
					DomainConstants.EMPTY_STRING,//query Name
					false,						// expand type 
					slObj,						//Object Select
					(short)0,					//Limit
					DomainConstants.EMPTY_STRING,//search format
					DomainConstants.EMPTY_STRING,//search test
					multiValueList,	//multi value list
					DomainConstants.EMPTY_STRINGLIST);//order by

			Map<String, Object> mpCLRCount = getCountForType(context, mlObjs, TYPE_PG_CLAIM_REQUEST);
			Map<String, Object> mpCLMSCount = getCountForType(context, mlObjs, TYPE_PG_CLAIM_SUPPORT);

			JsonObjectBuilder jsonCounterObj = Json.createObjectBuilder();
			Map mpCounter = new HashMap<>();
			mpCounter.put(STRING_CLAIM_REQUEST, mpCLRCount.remove(STRING_OWNED));
			mpCounter.put(STRING_CLAIM_SUPPORT, mpCLMSCount.remove(STRING_OWNED));
			PGClaimModuler.map2JsonBuilder(jsonCounterObj, mpCounter);
			output.add("count", jsonCounterObj);

			JsonObjectBuilder jsonCLRCounterObj = Json.createObjectBuilder();
			PGClaimModuler.map2JsonBuilder(jsonCLRCounterObj, mpCLRCount);
			stateData.add(STRING_CLAIM_REQUEST, jsonCLRCounterObj);

			JsonObjectBuilder jsonCLMSCounterObj = Json.createObjectBuilder();
			mpCLMSCount.remove(STRING_APPROVED);
			PGClaimModuler.map2JsonBuilder(jsonCLMSCounterObj, mpCLMSCount);
			stateData.add(STRING_CLAIM_SUPPORT, jsonCLMSCounterObj);

			output.add("data", stateData);
			output.add(STRING_MESSAGE, STRING_OK);
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}
	/**
	 * This method returns the count of different states for a specific type
	 * @param context
	 * @param mlObjs
	 * @param strTypeFilter
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getCountForType(Context context, MapList mlObjs, String strTypeFilter) throws Exception
	{
		Map<String, Object> countMap = new HashMap<>();
		String contextUser = context.getUser();
		int iObjCounter = 0;
		int iObjCollabCounter=0;
		int iObjPreliminaryOwnedCounter=0;
		int iObjPreliminaryCoOwnedCounter=0;
		int iObjReviewCounter=0;
		int iObjApprovedCounter=0;

		Map<String, Object> mapObj;
		String strType ;
		String strCurrent;
		String strOwner;
		StringList slOwnership;
		StringList slAssignee;
		StringList slTaskAssignee;
		StringList slTaskStatus;
		StringList slRouteStatus;
		StringList slCollbStatus;

		for(int i=0;i<mlObjs.size();i++)
		{
			mapObj = (Map<String, Object>) mlObjs.get(i);
			//Remove Bell Char Start
			removeBellChar(mapObj);
			strType =(String) mapObj.get(DomainConstants.SELECT_TYPE);
			strCurrent =(String) mapObj.get(DomainConstants.SELECT_CURRENT);
			strOwner = (String) mapObj.get(DomainConstants.SELECT_OWNER);
			slOwnership = (StringList) mapObj.get(DomainConstants.SELECT_OWNERSHIP);
			slAssignee = (StringList) mapObj.get(SELECT_ASSIGNMENTS_TO_NAME);
			slTaskAssignee = (StringList) mapObj.get(SELECT_TASK_ASSIGNEE_NAME);
			slTaskStatus = (StringList) mapObj.get(SELECT_ROUTE_TASK_STATUS);
			slRouteStatus = (StringList) mapObj.get(SELECT_ROUTE_STATUS);
			slCollbStatus =  (StringList) mapObj.get(SELECT_ASSIGNMENTS_COLLABORATION_STATUS);
			//filter Data Start
			if(UIUtil.isNotNullAndNotEmpty(strTypeFilter) && strTypeFilter.equals(strType) && !STATE_OBSOLETE.equals(strCurrent) && !STATE_RELEASED.equals(strCurrent))
			{
				if(contextUser.equals(strOwner) || slOwnership.toString().contains(contextUser))
				{
					iObjCounter++;
				}
				if(STATE_PRELIMINARY.equals(strCurrent) && contextUser.equals(strOwner))
				{
					iObjPreliminaryOwnedCounter++;
				}
				if(STATE_PRELIMINARY.equals(strCurrent) && BusinessUtil.isNotNullOrEmpty(slOwnership) && slOwnership.toString().contains(contextUser))
				{
					iObjPreliminaryCoOwnedCounter++;
				}
				else if((FUNCTIONAL_APPROVAL.equals(strCurrent) || MARKETING_APPROVAL.equals(strCurrent) || FINAL_APPROVAL.equals(strCurrent)) && BusinessUtil.isNotNullOrEmpty(slTaskStatus))
				{
					for(int j=0;j<slTaskStatus.size();j++)
					{
						if(slTaskAssignee.get(j).equals(contextUser) && (slTaskStatus.get(j)).equals(STRING_ASSIGNED) && slRouteStatus.get(j).equals(STATE_STARTED))
						{
							iObjReviewCounter++;
						}
					}
				}
				else if(STATE_COLLABORATION.equalsIgnoreCase(strCurrent) && BusinessUtil.isNotNullOrEmpty(slAssignee) && slAssignee.contains(contextUser))
				{		
					for(int j=0;j<slCollbStatus.size();j++)
					{
						if(slAssignee.get(j).equals(contextUser) && slCollbStatus.get(j).equals(STRING_ASSIGNED))
						{
							iObjCollabCounter++;
							break;
						}
					}
				}					
				else if(STATE_APPROVED.equalsIgnoreCase(strCurrent) && contextUser.equals(strOwner))
				{
					iObjApprovedCounter++;
				}
			}
		}

		countMap.put(STRING_OWNED, iObjCounter);
		countMap.put(STATE_PRELIMINARY_OWNED, iObjPreliminaryOwnedCounter);
		countMap.put(STATE_PRELIMINARY_CO_OWNED, iObjPreliminaryCoOwnedCounter);
		countMap.put(STATE_COLLABORATION, iObjCollabCounter);
		countMap.put(STRING_REVIEW, iObjReviewCounter);
		countMap.put(STATE_APPROVED, iObjApprovedCounter);

		return countMap;
	}

	private void removeBellChar(Map<String, Object> mapObj) {
		String strValue;
		byte [] bByte = new byte[1];
		bByte[0] = 0x7;//hexadecimal value of bell character
		String sBellChar = new String(bByte);
		for (Map.Entry entry : mapObj.entrySet())  
		{
			if( entry.getValue() instanceof String && ((String) entry.getValue()).contains(sBellChar))
			{ 
				strValue = (String) entry.getValue();		
				mapObj.put((String) entry.getKey(), StringUtil.split(strValue,sBellChar));
			}
		}
	}

	/**
	 * Method to add existing variants and connect to an Experiment
	 * @param context
	 * @param strInputData
	 * @return String in json format 
	 * @throws java.lang.Exception 
	 */
	public String addObject(Context context, String strInputData) throws Exception {
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strInputData);
		String sObjectId = jsonInputData.getString(PGWidgetConstants.KEY_OBJECT_ID);
		String strContentIds = jsonInputData.getString(KEY_CONTENT_ID);
		try {
			StringList slObjectIds = StringUtil.split(sObjectId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			StringList slContentIds = StringUtil.split(strContentIds, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			Map<String,String> mpConnectionDetails;
			if (slObjectIds != null && !slObjectIds.isEmpty()) {
				for (int count = 0; count < slObjectIds.size(); count++) {
					if (slContentIds != null && !slContentIds.isEmpty()) {
						for (int i = 0; i < slContentIds.size(); i++) {
							mpConnectionDetails= getRelName(context,slObjectIds.get(count),slContentIds.get(i));
							connectObject(context, jsonObjBuilder,  mpConnectionDetails.get("from"), mpConnectionDetails.get("to"),mpConnectionDetails.get(STRING_RELNAME));
							connectToConnectedClaims(context,jsonObjBuilder,sObjectId,mpConnectionDetails);
						}
					}
				}}
			return jsonObjBuilder.build().toString();
		} catch(Exception ex ) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonObjBuilder.add(STRING_MESSAGE, ex.getMessage());
			jsonObjBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			createErrorMessage(context, jsonObjBuilder);
			return jsonObjBuilder.build().toString();
		}
	}

	/**This method perform connection on connected Claims of Claims Request; 
	 * @param context
	 * @param sObjectId
	 * @param mpConnectionDetails
	 * @throws Exception 
	 */
	private StringList connectToConnectedClaims(Context context,JsonObjectBuilder jsonObjBuilder, String sObjectId, Map<String, String> mpConnectionDetails) throws Exception {
		StringList slValidClaims= new StringList();
		if(DomainConstants.RELATIONSHIP_PROTECTED_ITEM.equals(mpConnectionDetails.get(STRING_RELNAME)))
		{
			slValidClaims = getValidClaimsFromClaimRequest(context, sObjectId);
			if(BusinessUtil.isNotNullOrEmpty(slValidClaims))
			{
				for(int i=0;i<slValidClaims.size();i++)
				{
					connectObject(context, jsonObjBuilder,  mpConnectionDetails.get("from"), slValidClaims.get(i),mpConnectionDetails.get(STRING_RELNAME));	
				}
			}
		}
		return slValidClaims;
	}

	/**This Method returns Claim connected to Claim Request which are not in Approve,Released and obsolete
	 * @param context
	 * @param sObjectId
	 * @return
	 * @throws FrameworkException
	 */
	private StringList getValidClaimsFromClaimRequest(Context context, String sObjectId) throws FrameworkException{
		StringList slValidClaims = new StringList();
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			DomainObject domObj = DomainObject.newInstance(context, sObjectId);
			StringList slSelectable = new StringList();
			slSelectable.add(FROM_PG_CLAIMS_ID);
			slSelectable.add(FROM_PG_CLAIMS_CURRENT);
			//DCM (DS) - US 1609 - Security inheritance on Disclaimer - START
			slSelectable.add(SELECT_CLAIM_DISCLAIMER_ID);
			slSelectable.add(SELECT_CLAIM_DISCLAIMER_CURRENT);
			//DCM (DS) - US 1609 - Security inheritance on Disclaimer - END
			Map mpClaims = domObj.getInfo(context, slSelectable,slSelectable);
			//DCM (DS) - US 1609 - Security inheritance on Disclaimer - START
			if(null != mpClaims && (mpClaims.containsKey(FROM_PG_CLAIMS_ID) || mpClaims.containsKey(SELECT_CLAIM_DISCLAIMER_ID)))
			{
				StringList slClaimsID = mpClaims.containsKey(FROM_PG_CLAIMS_ID) ? returnStringListForObject(mpClaims.get(FROM_PG_CLAIMS_ID)) : new StringList();
				StringList slClaimsCurrent = mpClaims.containsKey(FROM_PG_CLAIMS_CURRENT) ? returnStringListForObject(mpClaims.get(FROM_PG_CLAIMS_CURRENT)) : new StringList();
				StringList slDisclaimersID = mpClaims.containsKey(SELECT_CLAIM_DISCLAIMER_ID) ? returnStringListForObject(mpClaims.get(SELECT_CLAIM_DISCLAIMER_ID)) : new StringList();
				StringList slDisclaimersCurrent = mpClaims.containsKey(SELECT_CLAIM_DISCLAIMER_CURRENT) ? returnStringListForObject(mpClaims.get(SELECT_CLAIM_DISCLAIMER_CURRENT)) : new StringList();
				slClaimsID.addAll(slDisclaimersID);
				slClaimsCurrent.addAll(slDisclaimersCurrent);
				//DCM (DS) - US 1609 - Security inheritance on Disclaimer - END
				String strCurrent;
				for(int i=0;i<slClaimsID.size();i++)
				{
					strCurrent = slClaimsCurrent.get(i);
					if(!(STRING_APPROVED.equals(strCurrent) || STATE_RELEASED.equals(strCurrent)||STATE_OBSOLETE.equals(strCurrent)))
					{
						slValidClaims.add(slClaimsID.get(i));
					}
				}

			}
		}
		return slValidClaims;
	}

	/**
	 * This method returns the Relationship details (name and direction) between two input types
	 * @param context
	 * @param sObjectId
	 * @param sContentIds
	 * @return
	 * @throws FrameworkException
	 */
	private static Map<String, String> getRelName(Context context, String sObjectId, String sContentIds) throws FrameworkException {
		DomainObject domFrom = DomainObject.newInstance(context, sObjectId);
		DomainObject domTo = DomainObject.newInstance(context,sContentIds);
		String strFromType = domFrom.getInfo(context,DomainConstants.SELECT_TYPE);
		String strToType = domTo.getInfo(context,DomainConstants.SELECT_TYPE);
		HashMap<String, String> mpReturn = new HashMap<>();
		if(TYPE_PG_CLAIM_REQUEST.equals(strFromType) && TYPE_PG_CLAIM.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_CLAIMS);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		else if(TYPE_PG_CLAIM.equals(strFromType) && TYPE_PG_CLAIM_REQUEST.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_CLAIMS);
			mpReturn.put("from",sContentIds);
			mpReturn.put("to",sObjectId);
		}
		//DCM (DS) Sprint 7 - US 1617 - Enable Add Existing Disclaimers - START
		if(TYPE_PG_CLAIM_REQUEST.equals(strFromType) && TYPE_PG_DISCLAIMER.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_DISCLAMER);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		//DCM (DS) Sprint 7 - US 1617 - Enable Add Existing Disclaimers - END
		else if(TYPE_PG_CLAIM.equals(strFromType)&& TYPE_PG_CLAIM_SUPPORT.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIM_SUPPORT);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		else if(TYPE_PG_CLAIM_SUPPORT.equals(strFromType)&& TYPE_PG_CLAIM.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIM_SUPPORT);
			mpReturn.put("from",sContentIds);
			mpReturn.put("to",sObjectId);
		}
		else if(domFrom.isKindOf(context, DomainConstants.TYPE_PART)&&TYPE_PG_CLAIM_REQUEST.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_CLAIMREQUESTS);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		else if(domTo.isKindOf(context, DomainConstants.TYPE_PART)&&TYPE_PG_CLAIM_REQUEST.equals(strFromType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_CLAIMREQUESTS);
			mpReturn.put("from",sContentIds);
			mpReturn.put("to",sObjectId);
		}
		else if(TYPE_PG_CLAIM_REQUEST.equals(strFromType)&&DomainConstants.TYPE_PERSON.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_ASSIGNMENTS);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		else if(domFrom.isKindOf(context, DomainConstants.TYPE_PART)&&(TYPE_PG_CLAIM.equals(strToType) || TYPE_PG_DISCLAIMER.equals(strToType)))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIM_RELATED_PART);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		//DCM Sprint 7 US-2624:Enable related parts at product configuration level instead at claim level : Start
		else if(domTo.isKindOf(context, DomainConstants.TYPE_PART)&& (TYPE_PG_CLAIM.equals(strFromType) || TYPE_PG_DISCLAIMER.equals(strFromType) || PG_CLAIM_PRODUCT_CONFIGURATION.equals(strFromType)))
		{
		//DCM Sprint 7 US-2624:Enable related parts at product configuration level instead at claim level : End
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIM_RELATED_PART);
			mpReturn.put("from",sContentIds);
			mpReturn.put("to",sObjectId);
		}
		else if(domFrom.isKindOf(context, DomainConstants.TYPE_PART) && TYPE_PG_CLAIM_SUPPORT.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIMSUPPORT_RELATED_PART);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		else if(domTo.isKindOf(context, DomainConstants.TYPE_PART)&&TYPE_PG_CLAIM_SUPPORT.equals(strFromType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIMSUPPORT_RELATED_PART);
			mpReturn.put("from",sContentIds);
			mpReturn.put("to",sObjectId);
		}
		else if(domFrom.isKindOf(context, TYPE_PG_CLAIM_SUPPORT) && domTo.isKindOf(context,TYPE_PG_IRM_DOCUMENT))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIMSUPPORT_REFERENCE);
			mpReturn.put("from",sObjectId);
			mpReturn.put("to",sContentIds);
		}
		else if(domTo.isKindOf(context, TYPE_PG_CLAIM_SUPPORT) && TYPE_PG_CLAIM_SUPPORT.equals(strFromType))
		{
			mpReturn.put(STRING_RELNAME,RELATIONSHIP_PG_CLAIMSUPPORT_REFERENCE);
			mpReturn.put("from",sContentIds);
			mpReturn.put("to",sObjectId);
		}
		else if(TYPE_IP_CONTROL_CLASS.equals(strToType) || TYPE_SECURITY_CONTROL_CLASS.equals(strToType))
		{
			mpReturn.put(STRING_RELNAME,DomainConstants.RELATIONSHIP_PROTECTED_ITEM);
			mpReturn.put("from",sContentIds);
			mpReturn.put("to",sObjectId);
		}
		return mpReturn;
	}

	/**
	 *  Method to connect an existing content object
	 * @param context
	 * @param jsonObjBuilder
	 * @param jsonArrBuilder
	 * @param sObjectId
	 * @param strObjectSels
	 * @param strContentId
	 * @throws java.lang.Exception 
	 */
	private static void connectObject(Context context, JsonObjectBuilder jsonObjBuilder,String sObjectId, String strContentId, String sRelName) throws Exception {
		boolean isCtxPushed = false;   
		try {
			DomainObject domFromObj = DomainObject.newInstance(context, sObjectId);
			StringList toObjectList = StringUtil.split(strContentId, PGWidgetConstants.KEY_COMMA_SEPARATOR);
			DomainObject domToObj = DomainObject.newInstance(context, toObjectList.get(0));
			if(domFromObj.isKindOf(context, TYPE_PART) && domToObj.isKindOf(context, TYPE_PG_CLAIM))
			{
				//Pushing context to support requirement: The system shall allow to add a related part to the Claim even though the user does not have access to the part.
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isCtxPushed = true;
			}
			StringList slSelectable = new StringList();
			PGWidgetUtil.addExisting(context, sObjectId, strContentId,
					sRelName, true);
			jsonObjBuilder.add(STRING_STATUS, STATUS_SUCCESS);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonObjBuilder.add(STRING_STATUS, STATUS_ERROR);
			jsonObjBuilder.add(STRING_MESSAGE, ex.getMessage());
			jsonObjBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			createErrorMessage(context, jsonObjBuilder);
		}finally 
		{
			if(isCtxPushed)
			{
				ContextUtil.popContext(context);

			}
		}
	}

	/**This Method use to get Basic and attribute data of Object Id
	 * @param context
	 * @param busID
	 * @return
	 * @throws java.lang.Exception 
	 */
	public Response getObjectInfo(matrix.db.Context context,List<String> busID) throws Exception {
		Response response = null ;
		try{

			if (context != null) {
				JsonObjectMaker jsonResult = getBasicData(context,busID);
				JsonArrayMaker jsonAttrib= getAttributeDetails(context,busID);
				jsonResult.add("attributes", jsonAttrib);
				response = Response.status(HttpServletResponse.SC_OK).entity(jsonResult.build()).build();
			}
		}catch(Exception ex){
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			response =  Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
		}
		return response;
	}

	/**this method return connected to object to show on properties page. 
	 * @param context
	 * @param busID
	 * @return
	 * @throws FrameworkException 
	 */
	private JsonArrayMaker getconnectedDataforProperties(Context context, List<String> busID) throws FrameworkException {
		JsonArrayMaker jsonArray = new JsonArrayMaker();
		DomainObject domObj = DomainObject.newInstance(context, busID.get(0));
		StringList slSelectable = new StringList();
		slSelectable.addElement(SELECT_TO_PROTECTED_ITEM_FROM_ID);
		slSelectable.addElement(SELECT_TO_PROTECTED_ITEM_FROM_NAME);
		Map mpData = domObj.getInfo(context, slSelectable, slSelectable);

		if(mpData.containsKey(SELECT_TO_PROTECTED_ITEM_FROM_ID) && BusinessUtil.isNotNullOrEmpty(returnStringListForObject(mpData.get(SELECT_TO_PROTECTED_ITEM_FROM_ID))))
		{
			JsonArrayMaker jsonValue = new JsonArrayMaker();
			JsonObjectMaker jsonObjValue;
			StringList slval = returnStringListForObject(mpData.get(SELECT_TO_PROTECTED_ITEM_FROM_NAME));
			StringList slid = returnStringListForObject(mpData.get(SELECT_TO_PROTECTED_ITEM_FROM_ID));
			for(int j=0;j<slval.size();j++)
			{
				jsonObjValue = new JsonObjectMaker();
				jsonObjValue.add("id",slid.get(j));
				jsonObjValue.add("name",slval.get(j));
				jsonValue.add(jsonObjValue);
			}

			JsonArrayMaker searchType = new JsonArrayMaker();
			searchType.add(TYPE_IP_CONTROL_CLASS);
			JsonObjectMaker jsonTemp = new JsonObjectMaker();
			jsonTemp.add("UIPosition", "100");
			jsonTemp.add("multiple", true);
			jsonTemp.add("name", "IPClassification");
			jsonTemp.add("nls", "Security Category Classification");
			jsonTemp.add("readOnly", true);
			jsonTemp.add("selectable", DomainConstants.RELATIONSHIP_PROTECTED_ITEM);
			jsonTemp.add("type", "search");
			jsonTemp.add("value", jsonValue);
			jsonTemp.add("SearchTypes", searchType);
			jsonArray.add(jsonTemp);
		}

		return jsonArray;
	}

	/**
	 * This method gets the basic data of the object
	 * @param context
	 * @param busID
	 * @return
	 * @throws FrameworkException
	 */
	private JsonObjectMaker getBasicData(matrix.db.Context context, List<String> busID) throws FrameworkException {
		JsonArrayMaker jsonArray = null;
		JsonObjectMaker jsonResult = new JsonObjectMaker() ;
		Map mpData = new HashMap();

		jsonArray = new JsonArrayMaker();
		DomainObject domObj = DomainObject.newInstance(context, busID.get(0));
		StringList slBasicData = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.ClaimsManagement.basicData"),CONSTANT_STRING_COMMA);
		String strPosition;
		StringList slKeyValues;
		String strAttrName;
		Map<String, String> mBasicPositionMap = new HashMap<>();
		StringList slSelectable = new StringList(DomainConstants.SELECT_ID);
		for(int i=0; i<slBasicData.size(); i++){
			String strAttrKey = slBasicData.get(i);
			if(strAttrKey.contains(STR_COLON)){
				slKeyValues = StringUtil.split(strAttrKey, STR_COLON);
				strPosition = slKeyValues.get(0);
				strAttrName = slKeyValues.get(1);
				mBasicPositionMap.put(strAttrName, strPosition);
				slSelectable.addElement(strAttrName);
			}else{
				slSelectable.addElement(strAttrKey);
			}
		}
		slSelectable.addElement(SELECT_PHYSICALID);
		//ADO-US-1595-Properties tab changes-Start
		slSelectable.addElement(DomainConstants.SELECT_MODIFIED);
		//ADO-US-1595-Properties tab changes-End
		Map<String,Object> mpBasicData = domObj.getInfo(context, slSelectable);
		String nlsValue;
		String tempName;
		JsonObjectMaker jsonTemp = null;
		JsonArrayMaker jsonVal = null;
		jsonResult.add("id",(String) mpBasicData.get(SELECT_PHYSICALID));
		boolean isValid = true;
		//ADO-US-1595-Properties tab changes-Start
		String sType = (String)mpBasicData.get(DomainConstants.SELECT_TYPE);
		if(TYPE_PG_CLAIM_REQUEST.equalsIgnoreCase(sType)){
			mBasicPositionMap.put(DomainConstants.SELECT_MODIFIED,String.valueOf(slBasicData.size()));
		}
		//ADO-US-1595-Properties tab changes-End
		for (Map.Entry entry : mpBasicData.entrySet()) {
			tempName = (String) entry.getKey();
			isValid = true;
			if("id".equals(tempName)||SELECT_PHYSICALID.equals(tempName))
			{
				isValid = false;
			}
			if(tempName.equals(DomainConstants.SELECT_CURRENT)||tempName.equals(DomainConstants.SELECT_OWNER))
			{
				mpData.put(tempName,entry.getValue());
			}		    

			jsonTemp = new JsonObjectMaker();
			jsonTemp.add("name",tempName);
			nlsValue = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
					"emxFramework.Claim.Basic."+tempName, context.getLocale());
			jsonTemp.add("nls",nlsValue);
			jsonTemp.add("type","string");
			jsonTemp.add("selectable",tempName);
			jsonTemp.add("readOnly",true);
			//ADO-US-1595-Properties tab changes-Start
			if(TYPE_PG_CLAIM_REQUEST.equalsIgnoreCase(sType) && tempName.equals(DomainConstants.SELECT_MODIFIED)){
				jsonTemp.add("nls", "Last Updated");
			}
			//ADO-US-1595-Properties tab changes-End
			if(tempName.equals(DomainConstants.SELECT_DESCRIPTION))
			{
				if(TYPE_PG_CLAIM.equals(mpBasicData.get(DomainConstants.SELECT_TYPE)) || TYPE_PG_DISCLAIMER.equals(mpBasicData.get(DomainConstants.SELECT_TYPE)))
				{
					jsonTemp.add("readOnly",false);
					jsonTemp.add("type", "textArea");
				}
				else
				{
					isValid = false;
				}
			}

			jsonVal= new JsonArrayMaker();
			if(tempName.equals(DomainConstants.SELECT_TYPE))
			{
				jsonVal.add(EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
						"emxFramework.Type."+entry.getValue(), context.getLocale()));
			}
			else if(tempName.equals(DomainConstants.SELECT_DESCRIPTION))
			{
				jsonVal.add(entry.getValue().toString().replaceAll("\\R+", "\n")); 
			}
			else
			{
				jsonVal.add(entry.getValue());
			}

			jsonTemp.add("value",jsonVal);
			if(mBasicPositionMap.containsKey(tempName))
			{
				jsonTemp.add("UIPosition",mBasicPositionMap.get(tempName));
			}
			else
			{
				continue;
			}
			if(isValid)
			{
				jsonArray.add(jsonTemp);
			}

		}
		jsonResult.add("basicData", jsonArray);
		try
		{
			if((TYPE_PG_CLAIM.equals(mpBasicData.get(DomainConstants.SELECT_TYPE)) || TYPE_PG_DISCLAIMER.equals(mpBasicData.get(DomainConstants.SELECT_TYPE))) && isEditable(context,mpBasicData))
			{
				jsonResult.add("ClaimRequestData", getClaimRequestData(context,busID.get(0)));
			}
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		jsonResult.add(IS_EDITABLE, isEditable(context,mpBasicData));
		return jsonResult;
	}

	/**Thsi Method use get ClaimRequest data for pgBrand,pgIntenedMarkets,pgProductForm
	 * @param context 
	 * @param string
	 * @return
	 * @throws FrameworkException 
	 */	
	private JsonObjectMaker getClaimRequestData(Context context, String strObjectId) throws FrameworkException {
		JsonObjectMaker jsonObj = new JsonObjectMaker();
		if(UIUtil.isNotNullAndNotEmpty(strObjectId))
		{
			DomainObject domObj = DomainObject.newInstance(context, strObjectId);
			StringList slBusSelects = new StringList();
			slBusSelects.add(DomainObject.getAttributeSelect(ATTRIBUTE_BRAND));
			//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request - START
			slBusSelects.add(DomainObject.getAttributeSelect("pgBusinessArea"));
			slBusSelects.add(DomainObject.getAttributeSelect("pgProductCategoryPlatform"));
			slBusSelects.add(DomainObject.getAttributeSelect("pgFranchisePlatform"));
			//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request - END
			slBusSelects.add(DomainObject.getAttributeSelect(ATTRIBUTE_PG_INTENDED_MARKETS));
			String strRelPattern = new StringBuilder(RELATIONSHIP_CLAIMS).append(CONSTANT_STRING_COMMA).append(RELATIONSHIP_PG_DISCLAMER).toString(); 
			String strTypePattern = new StringBuilder(TYPE_PG_CLAIM_REQUEST).append(CONSTANT_STRING_COMMA).append(TYPE_PG_CLAIM).toString(); 
			String strWhere = DomainConstants.SELECT_CURRENT+" == '"+STATE_PRELIMINARY+"' || "+DomainConstants.SELECT_CURRENT+" == '"+STRING_COLLABORATION+"'";
			MapList mlObjs = domObj.getRelatedObjects(
					context, //context
					strRelPattern, //relPattern
					strTypePattern, //typePattern
					slBusSelects, //busSelects
					null, //relSelects
					true, // getTo
					false, // getFrom
					(short) 1,  //recurseToLevel
					strWhere, //busWhere
					DomainConstants.EMPTY_STRING, //relWhere
					0 //limit
					);	
			if(null!=mlObjs)
			{
				Map mpData ;
				JsonArrayMaker slpgBrand = new JsonArrayMaker();
				JsonArrayMaker slpgIntendedMarkets = new JsonArrayMaker();
				//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request
				JsonArrayMaker slpgBusinessArea = new JsonArrayMaker();
				JsonArrayMaker slpgProductCategoryPlatform = new JsonArrayMaker();
				JsonArrayMaker slpgFranchisePlatform = new JsonArrayMaker();
				//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request - END
				for(int i=0;i<mlObjs.size();i++)
				{
					mpData = (Map)mlObjs.get(i);
					StringList slBrand = returnStringListForObject(mpData.get(DomainObject.getAttributeSelect(ATTRIBUTE_BRAND)));
					StringList slIntendedMarket = returnStringListForObject(mpData.get(DomainObject.getAttributeSelect(ATTRIBUTE_PG_INTENDED_MARKETS)));
					//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request
					StringList slBusinessArea = returnStringListForObject(mpData.get(DomainObject.getAttributeSelect("pgBusinessArea")));
					StringList slProductCategoryPlatform = returnStringListForObject(mpData.get(DomainObject.getAttributeSelect("pgProductCategoryPlatform")));
					StringList slFranchisePlatform = returnStringListForObject(mpData.get(DomainObject.getAttributeSelect("pgFranchisePlatform")));

					//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request - END
					slpgBrand.addAll(slBrand );
					slpgIntendedMarkets.addAll(slIntendedMarket);
					//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request - START
					slpgBusinessArea.addAll(slBusinessArea );
					slpgProductCategoryPlatform.addAll(slProductCategoryPlatform );
					slpgFranchisePlatform.addAll(slFranchisePlatform );
					//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request - END
				}
				jsonObj.add(ATTRIBUTE_BRAND, slpgBrand);
				jsonObj.add(ATTRIBUTE_PG_INTENDED_MARKETS, slpgIntendedMarkets);
				//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request
				jsonObj.add("pgBusinessArea", slpgBusinessArea);
				jsonObj.add("pgProductCategoryPlatform", slpgProductCategoryPlatform);
				jsonObj.add("pgFranchisePlatform", slpgFranchisePlatform);
				//DCM(DS) 2022x-01 CW - REQ 45253 - On claim, BA/PCP/Franchise Platform attribute values list shall be narrowed down based on the values selected in the claim request - END
			}
		}
		return jsonObj;
	}

	/**
	 * This method builds the JSON having attribute details of the object
	 * @param context
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private JsonArrayMaker getAttributeDetails(matrix.db.Context context, List<String> id) throws Exception {
		JsonArrayMaker jsonArray = null;
		jsonArray = new JsonArrayMaker();
		DomainObject domObj = DomainObject.newInstance(context, id.get(0));
		Map mpAttributeMap = domObj.getAttributeMap(context);  
		JsonObjectMaker jsonTempAttrib = null;
		String nlsValue = null ;
		String strkey;
		boolean isClaimSupport = false;
		//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - START
		boolean isClaim = false;
		//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - END
		boolean isCopyElementTypeAndEditable = false;
		StringList slClaimAttribValues = new StringList();
		StringList slClaimMandAttrib = new StringList();
		StringList sltextAreaAttribute = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.TextArea.Attribute.List"),CONSTANT_STRING_COMMA);
		StringList slSearchAttribute = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.search.Attribute.List"),CONSTANT_STRING_COMMA);
		StringList slMultiVlaueAttribute = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.MultiVlaue.Attribute.List"),CONSTANT_STRING_COMMA);
		StringList slLabelChangeAttrib = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.LabelChangeAttribute.List"),CONSTANT_STRING_COMMA);
		StringList slPickListAttrib = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.PickListAttribute.List"),CONSTANT_STRING_COMMA);
		//DCM (DS) 2022x-01 CW - REQ 44675 - Make Comment field readonly on properties page - START
		StringList slReadOnlyAttrib = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.ClaimManagement.ReadOnly.Properties.List"),CONSTANT_STRING_COMMA);
		//DCM (DS) 2022x-01 CW - REQ 44675 - Make Comment field readonly on properties page - End
		if(domObj.isKindOf(context, PGClaimManagementUtil.TYPE_PG_CLAIM_REQUEST)){
			slClaimAttribValues = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.ClaimRequest.Attribute.List"),CONSTANT_STRING_COMMA);
			slClaimMandAttrib = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.ClaimRequest.MandatoryAttributes"),CONSTANT_STRING_COMMA);
		}else if(domObj.isKindOf(context, PGClaimManagementUtil.TYPE_PG_CLAIM)){
			slClaimAttribValues = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.Attribute.List"),CONSTANT_STRING_COMMA);
			slClaimMandAttrib = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.MandatoryAttributes"),CONSTANT_STRING_COMMA);
			//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - START
			isClaim = true;
			//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - END
		}else if(domObj.isKindOf(context, PGClaimManagementUtil.TYPE_PG_CLAIM_SUPPORT)){
			slClaimAttribValues = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.ClaimSupport.Attribute.List"),CONSTANT_STRING_COMMA);
			slClaimMandAttrib = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.ClaimSupport.MandatoryAttributes"),CONSTANT_STRING_COMMA);
			isClaimSupport= true;
		} else if(domObj.isKindOf(context, PGClaimManagementUtil.TYPE_PG_DISCLAIMER)){
			slClaimAttribValues = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Disclaimer.Attribute.List"),CONSTANT_STRING_COMMA);
			slClaimMandAttrib = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Disclaimer.MandatoryAttributes"),CONSTANT_STRING_COMMA);
		}
		StringList slRichTextFeild = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.RichTextAttribute.List"),CONSTANT_STRING_COMMA);
		StringList slClaimAttrib = new StringList();
		Map mAttrPositionMap = new HashMap();
		if(BusinessUtil.isNotNullOrEmpty(slClaimAttribValues)){
			String strPosition;
			StringList slKeyValues;
			String strAttrName;
			for(int i=0; i<slClaimAttribValues.size(); i++){
				String strAttrKey = slClaimAttribValues.get(i);
				if(strAttrKey.contains(STR_COLON)){
					slKeyValues = StringUtil.split(strAttrKey, STR_COLON);
					strPosition = slKeyValues.get(0);
					strAttrName = slKeyValues.get(1);
					mAttrPositionMap.put(strAttrName, strPosition);
					slClaimAttrib.addElement(strAttrName);
				}else{
					slClaimAttrib.addElement(strAttrKey);
				}
			}
		}
		AttributeType attibType = null;
		StringList slRange;
		JsonArrayMaker jsonVal;
		String strAttributeType = null;
		StringList slObj = new StringList();
		slObj.addElement(DomainConstants.SELECT_NAME);
		String strPosition;
		String strPickList;
		JsonArrayMaker searchType = new JsonArrayMaker();
		for (Object attribKey : mpAttributeMap.keySet())  
		{
			if(slClaimAttrib.contains(attribKey))
			{
				strPosition = (String)mAttrPositionMap.get(attribKey);
				if(UIUtil.isNullOrEmpty(strPosition)){
					strPosition = "0";
				}
				jsonTempAttrib = new JsonObjectMaker();
				jsonTempAttrib.add("name",(String)attribKey);
				jsonVal = new JsonArrayMaker();
				jsonVal.add(getStringListAsString(PGClaimModuler.returnStringListForObject(mpAttributeMap.get(attribKey))));
				jsonTempAttrib.add("value",jsonVal);
				strkey  = "emxFramework.Attribute."+(String)attribKey;
				if(slLabelChangeAttrib.contains(attribKey))
				{
					//DCM (DS) 2022x-02 CW - Update Expiry attribute - START
					strkey  = "emxFramework.Claim.Attribute."+((String)attribKey).replaceAll("\\s", "_");
					//DCM (DS) 2022x-02 CW - Update Expiry attribute - END
				}

				nlsValue = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
						strkey, context.getLocale());
				if(strkey.equals(nlsValue))
				{
					nlsValue = (String)attribKey;
				}

				attibType = new AttributeType((String)attribKey);
				attibType.open(context);
				slRange = attibType.getChoices();
				if(BusinessUtil.isNotNullOrEmpty(slRange))
				{
					jsonVal = new JsonArrayMaker();
					jsonVal.addAll(slRange);

					jsonTempAttrib.add("range", jsonVal);
					jsonTempAttrib.add("rangeNLS",jsonVal);
				}
				strAttributeType = attibType.getDataType(context);

				if("boolean".equals(strAttributeType))
				{
					jsonVal = new JsonArrayMaker();
					jsonVal.add("true");
					jsonVal.add("false");
					jsonTempAttrib.add("range", jsonVal);
					jsonVal = new JsonArrayMaker();
					jsonVal.add("Yes");
					jsonVal.add("No");
					jsonTempAttrib.add("rangeNLS",jsonVal);
					jsonVal = new JsonArrayMaker();
					if("TRUE".equalsIgnoreCase((String) mpAttributeMap.get(attribKey)))
					{
						jsonVal.add(getStringListAsString(PGClaimModuler.returnStringListForObject("Yes")));
					}
					else
					{
						jsonVal.add(getStringListAsString(PGClaimModuler.returnStringListForObject("No")));
					}
					jsonTempAttrib.add("value",jsonVal);	
				}

				if(slPickListAttrib.contains(attribKey))
				{
					strPickList = EnoviaResourceBundle.getProperty(context,
							"pgDCM.Claim.PickListAttribute."+attribKey);
					jsonTempAttrib.add("picklist", strPickList);
				}

				jsonTempAttrib.add("nls",nlsValue);
				if("pgClaimBackground".equals(attribKey) && isClaimSupport)
				{
					jsonTempAttrib.add("type","richText");
				}
				else if(slRichTextFeild.contains(attribKey))
				{
					jsonTempAttrib.add("type","richText");
				}
				else if(sltextAreaAttribute.contains(attribKey))
				{
					jsonTempAttrib.add("type","textArea");
				}
				else if(slSearchAttribute.contains(attribKey))
				{
					jsonTempAttrib.add("type","search");
					searchType = new JsonArrayMaker();
					searchType.add(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.searchType."+attribKey));
					jsonTempAttrib.add("SearchTypes",searchType);
				}
				else
				{
					jsonTempAttrib.add("type",strAttributeType);
				}				
				if(ATTRIBUTE_COPY_ELEMENT_TYPE.equals(attribKey))
				{
					StringList slClaimInfo = new StringList(DomainConstants.SELECT_REVISION);
					slClaimInfo.add(DomainConstants.SELECT_POLICY);
					slClaimInfo.add(DomainConstants.SELECT_TYPE);
					Map mapClaimInfo = domObj.getInfo(context, slClaimInfo);
					Policy policyObj = new Policy(mapClaimInfo.get(DomainConstants.SELECT_POLICY).toString());
					String strRevision = policyObj.getFirstInSequence(context);
					String strObjRevision = mapClaimInfo.get(DomainConstants.SELECT_REVISION).toString();
					if(!strObjRevision.equals(strRevision)||TYPE_PG_DISCLAIMER.equalsIgnoreCase((String)mapClaimInfo.get(DomainConstants.SELECT_TYPE)))	
					{
						isCopyElementTypeAndEditable = true;
					}
				}
				//DCM (DS) 2022x-01 CW - REQ 44675 - Make Comment field readonly on properties page - START
				//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - START
				jsonTempAttrib.add("selectable",DomainObject.getAttributeSelect((String)attribKey) );
				boolean isReadOnly = ("Originator".equals(attribKey) && isClaimSupport) || isCopyElementTypeAndEditable || slReadOnlyAttrib.contains(attribKey)  ;
				if(ATTRIBUTE_EXPIRATION_DATE.equals(attribKey) && isClaim)
				{
					isReadOnly = true;
					if(UIUtil.isNullOrEmpty((String)mpAttributeMap.get(ATTRIBUTE_EXPIRATION_DATE)))
					{
						String strExpiryCadence = (String)mpAttributeMap.get(ATTRIBUTE_PG_EXPIRY_CADENCE);
						if(UIUtil.isNotNullAndNotEmpty(strExpiryCadence))
						{
							jsonVal.clear();
							jsonVal.add(strExpiryCadence);
							jsonTempAttrib.add("value", jsonVal);
							jsonTempAttrib.add("selectable",DomainObject.getAttributeSelect(ATTRIBUTE_PG_EXPIRY_CADENCE) );
						}
					}
				}
				jsonTempAttrib.add("readOnly", isReadOnly);
				//DCM (DS) 2022x-05 CW - Expiry Cadence based on Claim Type - END
				//DCM (DS) 2022x-01 CW - REQ 44675 - Make Comment field readonly on properties page - End
				jsonTempAttrib.add("isMandatory",(slClaimMandAttrib.contains(attribKey)));
				jsonTempAttrib.add("UIPosition",strPosition);
				jsonTempAttrib.add("multiple", false);
				if(slMultiVlaueAttribute.contains(attribKey))
				{
					jsonTempAttrib.add("multiple", true);
				}

				attibType.close(context);
				jsonArray.add(jsonTempAttrib);
			}
			isCopyElementTypeAndEditable = false;
		}
		return jsonArray;
	}

	/**
	 *  Method to create Media for Claim/Claim Request
	 * @param context
	 * @param mpRequestMap
	 * @throws java.lang.Exception 
	 */
	@SuppressWarnings("unused")
	public Response createMedia(Context context, Map<String,Object> mpRequestMap) {
		JsonObjectBuilder output = Json.createObjectBuilder();
		StringBuilder sbNewObjectId = new StringBuilder();
		try {
			//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
			String strType = (String) mpRequestMap.get("type");
			String strPolicy =(String) mpRequestMap.get("policy");
			String strRelName =(String) mpRequestMap.get("relationship");
			String strCLRId =(String) mpRequestMap.get("id");
			Map<String,Object> mpAttributeMap = (Map<String,Object>) mpRequestMap.get("attribute");

			DomainObject domObj = DomainObject.newInstance(context);
			DomainObject domNewObj = DomainObject.newInstance(context);
			String strMediaRelId;
			if(RELATIONSHIP_CLAIM_MEDIA.equalsIgnoreCase(strRelName))
			{
				//Create CLM Toolkit media
				if(mpRequestMap.containsKey("claimId") && null!=mpAttributeMap)
				{
					StringList slClaimIds = returnStringListForObject(mpRequestMap.get("claimId"));
					String strDescription = (String) mpRequestMap.get("description");
					for(int iIdCount = 0; iIdCount<slClaimIds.size(); iIdCount++)
					{
						domObj = DomainObject.newInstance(context, slClaimIds.get(iIdCount));
						domNewObj = DomainObject.newInstance(context);
						domNewObj.createObject(context, strType, null, FIRST_REV, strPolicy,VAULT_ESERVICE_PRODUCTION);

						domNewObj.setAttributeValues(context, mpAttributeMap);	
						domNewObj.setDescription(context, strDescription);

						DomainRelationship.connect(context, domObj, RELATIONSHIP_CLAIM_MEDIA, domNewObj);				
						sbNewObjectId.append(domNewObj.getObjectId(context)).append(",");
					}
				}
				//Create CLR Reference Document
				else 
				{
					domObj.setId(strCLRId);
					strMediaRelId = domObj.getInfo(context, SELECT_FROM_ClAIM_MEDIA_TO_ID);
					if(UIUtil.isNullOrEmpty(strMediaRelId)) 
					{
						domNewObj = DomainObject.newInstance(context);
						domNewObj.createObject(context, strType, null, FIRST_REV, strPolicy, VAULT_ESERVICE_PRODUCTION);
						DomainRelationship.connect(context, domObj, RELATIONSHIP_CLAIM_MEDIA, domNewObj);
					} 
					else 
					{
						domNewObj = DomainObject.newInstance(context, strMediaRelId);
					}
					sbNewObjectId.append(domNewObj.getObjectId(context)).append(",");
				} 
			}
			//Create CLR Image, Add Claims to Image
			else if(RELATIONSHIP_PG_CLAIM_REQUEST_IMAGE.equalsIgnoreCase(strRelName)) 
			{
				String strMediaId = (String) mpRequestMap.get("ClaimRequestImageId");
				if(UIUtil.isNullOrEmpty(strMediaId)) 
				{
					domObj.setId(strCLRId);
					domNewObj = DomainObject.newInstance(context);
					domNewObj.createObject(context, strType, null, FIRST_REV, strPolicy,VAULT_ESERVICE_PRODUCTION);
					if(null!=mpAttributeMap)
					{
						domNewObj.setAttributeValues(context, mpAttributeMap);
					}
					sbNewObjectId.append(domNewObj.getObjectId(context)).append(",");
					DomainRelationship domrel = DomainRelationship.connect(context, domObj, strRelName, domNewObj);
					strMediaId = domNewObj.getObjectId(context);
				}
				if(mpRequestMap.containsKey("claimId"))
				{
					StringList slClaimIds = returnStringListForObject(mpRequestMap.get("claimId"));
					String [] arrClaimIds = slClaimIds.toArray(new String[slClaimIds.size()]);
					String[] arrMediaId = strMediaId.split(CONSTANT_STRING_COMMA);
					DomainObject domMediaObj = DomainObject.newInstance(context);

					for(int i=0; i<arrMediaId.length; i++)
					{
						domMediaObj.setId(arrMediaId[i]);
						DomainRelationship.connect(context, domMediaObj, RELATIONSHIP_PG_CLAIM_IMAGE, true, arrClaimIds);
					}
				}
			}
			//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END
			if(sbNewObjectId.getLength() > 0)
			{
				output.add("id", sbNewObjectId.toString(0, sbNewObjectId.getLength()-1));
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
		}
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}

	/**This method return related Media Data of Claim Media objects
	 * @param context
	 * @param strObjectId
	 * @param strSelect 
	 * @param strRelSelect 
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String getRelatedMediaDataInfo(matrix.db.Context context, String strObjectId,String strTypePatten,String strRelPatten, String strSelect) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();		
		String strAttrSelectSequence = DomainObject.getAttributeSelect(ATTRIBUTE_SEQUENCE);
		String strAttrSelectMediaContent = DomainObject.getAttributeSelect(ATTRIBUTE_MEDIA_CONTENT);
		String strAttrSelectComments = DomainObject.getAttributeSelect(ATTRIBUTE_COMMENTS);
		try {		
			StringList slBusSelects = new StringList();		
			if(BusinessUtil.isNotNullOrEmpty(strSelect))
			{
				slBusSelects.addAll(StringUtil.split(strSelect, CONSTANT_STRING_COMMA));
			}
			slBusSelects.add(DomainConstants.SELECT_ID);
			slBusSelects.add(DomainConstants.SELECT_NAME);
			slBusSelects.add(SELECT_FROM + RELATIONSHIP_CLAIM_MEDIA + SELECT_TO_ID);
			slBusSelects.add(SELECT_VERSION_PHYSICALID);			
			slBusSelects.add(SELECT_CLAIMMEDIA_TITLE);
			slBusSelects.add(SELECT_CLAIMMEDIA_SEQUENCE);
			slBusSelects.add(SELECT_CLAIMMEDIA_DESCRIPTION);
			slBusSelects.add(SELECT_CLAIMMEDIA_MEDIA_CONTENT);
			slBusSelects.add(SELECT_CLAIMMEDIA_COMMENTS);
			slBusSelects.add(SELECT_CLAIMMEDIA_PHYSICALID);
			slBusSelects.add(FROM_ACTIVE_VERSION_TO_PHYSICAL_ID);
			slBusSelects.add(FROM_ACTIVE_VERSION_TO_ID);
			//Added for Claim Media upload issue - START
			slBusSelects.add(strAttrSelectSequence);
			slBusSelects.add(DomainConstants.SELECT_DESCRIPTION);
			slBusSelects.add(strAttrSelectMediaContent);
			slBusSelects.add(strAttrSelectComments);
			slBusSelects.add(SELECT_TO_ClAIM_MEDIA_FROM);
			//Added for Claim Media upload issue - END
			//DCM(DS) 2022x-04 - Added for MS Files Preview - START
			slBusSelects.add(FROM_ACTIVE_VERSION_TO_DESCRIPTION);
			slBusSelects.add(FILESIZE);
			slBusSelects.add(FILENAME);
			//DCM(DS) 2022x-04 - Added for MS Files Preview - END

			DomainObject domObj = DomainObject.newInstance(context, strObjectId);

			MapList mlObjs = domObj.getRelatedObjects(
					context, //context
					strRelPatten, //relPattern
					strTypePatten, //typePattern
					slBusSelects, //busSelects
					null, //relSelects
					false, // getTo
					true, // getFrom
					//(short) 1,  //recurseToLevel
					(short) 2,  //recurseToLevel
					null, //busWhere
					DomainConstants.EMPTY_STRING, //relWhere
					0 //limit
					);		

			Map<String, Object> mapObj;
			String strType;
			String strName;
			String strRel;
			JsonArrayBuilder jsonArrayDocumentsMedia = null;
			JsonArrayBuilder jsonArrayMedia = null;
			JsonObjectBuilder jsonMediaObj = null; 
			JsonObjectBuilder jsonMediaVal = null;
			Object mediaList;
			int iSize;
			String strTitle;
			String strOwner;
			String strOriginated;
			String strFileSize;
			String strFileFormat;
			StringList slMediaId;
			StringList slMediaDescription;
			StringList slMediaTitle;
			StringList slMediaOwner;
			StringList slMediaOriginated;
			StringList slMediaFileSize;
			StringList slMediaFileFormat;
			StringList slMediaFileName;
			StringList slVersionLogicalID ;

			//Added for Claim Media upload issue - START
			String mediaDataId;
			String strLevel;
			String strMediaName;
			StringList slDummyClaimMedia = new StringList();
			StringList slName;
			StringList slMediaName = new StringList();
			//Added for Claim Media upload issue - END

			if(BusinessUtil.isNotNullOrEmpty(mlObjs)) {
				jsonArrayMedia = Json.createArrayBuilder();
				jsonArrayDocumentsMedia = Json.createArrayBuilder();

				for(int i=0;i<mlObjs.size();i++)
				{
					mapObj = (Map<String, Object>) mlObjs.get(i);
					strType =(String) mapObj.get(DomainConstants.SELECT_TYPE);
					//DCM(DS) 2022x-04 - Added for MS Files Preview - START
					mediaDataId = (String) mapObj.get(DomainConstants.SELECT_ID);
					//DCM(DS) 2022x-04 - Added for MS Files Preview - END
					//Added for Claim Media upload issue - START
					strLevel = (String) mapObj.get(DomainConstants.SELECT_LEVEL);
					if(TYPE_PG_CLAIM_MEDIA.equals(strType))
					{
						if("2".equals(strLevel)) {
							//DCM (DS) 2022x-01 CW - Defect 51540 - After CLM copy, the CLR-Toolkit Media Tab is blank - Start
							//code added for same media displayed multiple times
							strMediaName =(String) mapObj.get(DomainConstants.SELECT_NAME);
							if(slMediaName.contains(strMediaName))
							{
								continue;
							}else
							{
								slMediaName.add(strMediaName);
							}
							//code to fix media tab issue after claim clone
							slName = returnStringListForObject(mapObj.get(SELECT_TO_ClAIM_MEDIA_FROM));
							//DCM (DS) 2022x-01 CW - Defect 51540 - After CLM copy, the CLR-Toolkit Media Tab is blank - End
							//DCM(DS) 2022x-04 - Added for MS Files Preview - START
							if(UIUtil.isNotNullAndNotEmpty(mediaDataId)) {
								StringList slVersionDescription = returnStringListForObject(mapObj.get(FROM_ACTIVE_VERSION_TO_DESCRIPTION));
								StringList slVersionTitle = returnStringListForObject(mapObj.get(FROM_ACTIVE_VERSION_TO_TITLE));
								StringList slVersionID = returnStringListForObject(mapObj.get(FROM_ACTIVE_VERSION_TO_PHYSICAL_ID));
								//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - Start
								slVersionLogicalID = returnStringListForObject(mapObj.get(FROM_ACTIVE_VERSION_TO_ID));
								//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - End

								int iIdSize = slVersionID.size();
								//DCM(DS) 2022x-04 - Added for MS Files Preview - END
								String mediaDescription = (String) mapObj.get(DomainConstants.SELECT_DESCRIPTION);
								String mediaSeq = (String) mapObj.get(strAttrSelectSequence);
								String mediaContent = (String) mapObj.get(strAttrSelectMediaContent);
								String mediaComments = (String) mapObj.get(strAttrSelectComments);
								//DCM (DS) 2022x-01 CW - Defect 51540 - After CLM copy, the CLR-Toolkit Media Tab is blank - Start
								int iSlNameSize = slName.size();
								for ( int j = 0; j <  iSlNameSize ; j++)
								{
									//DCM(DS) 2022x-04 - Added for MS Files Preview - START
									if(BusinessUtil.isNotNullOrEmpty(slVersionID) )
									{
										strMediaName = slName.get(j);
										for ( int k = 0; k <  iIdSize ; k++)
										{
											//DCM(DS) 2022x-04 - Added for MS Files Preview - END
											jsonMediaVal = Json.createObjectBuilder();
											//DCM(DS) 2022x-04 - Added for MS Files Preview - START
											jsonMediaVal.add(DomainConstants.SELECT_NAME, strMediaName);
											jsonMediaVal.add(DomainConstants.SELECT_DESCRIPTION, mediaDescription);
											jsonMediaVal.add("versionDescription", slVersionDescription.get(k));
											jsonMediaVal.add(DomainConstants.SELECT_ID, slVersionID.get(k));
											//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - Start
											jsonMediaVal.add(LOGICAL_ID, slVersionLogicalID.get(k));
											//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - End
											jsonMediaVal.add(SELECT_PHYSICALID, mediaDataId);
											jsonMediaVal.add(STRING_IMAGE_DATA,getFileBase64(context,mediaDataId));
											jsonMediaVal.add(FROM_ACTIVE_VERSION_TO_TITLE, slVersionTitle.get(k));
											//DCM(DS) 2022x-04 - Added for MS Files Preview - END
											jsonMediaVal.add(strAttrSelectSequence, mediaSeq);
											jsonMediaVal.add(strAttrSelectMediaContent, mediaContent);
											jsonMediaVal.add(strAttrSelectComments, mediaComments);
											jsonArrayMedia.add(jsonMediaVal);		
										}
									} 
									else 
									{
										slDummyClaimMedia.add(mediaDataId);
									}
								}
								//DCM (DS) 2022x-01 CW - Defect 51540 - After CLM copy, the CLR-Toolkit Media Tab is blank - End
							}						
						} 
						else if("1".equals(strLevel)) {
							//Added for Claim Media upload issue - END
							strRel =(String) mapObj.get(STRING_RELATIONSHIP);
							if(RELATIONSHIP_CLAIM_MEDIA.equals(strRel)) {
								mediaList = mapObj.get(FROM_ACTIVE_VERSION_TO_TITLE);
								if(mediaList instanceof StringList) {								
									slMediaTitle = (StringList) mediaList;
									slMediaId = (StringList) mapObj.get(FROM_ACTIVE_VERSION_TO_PHYSICAL_ID);
									slMediaDescription = (StringList) mapObj.get(FROM_ACTIVE_VERSION_TO_DESCRIPTION);
									slMediaOwner = (StringList) mapObj.get(FROM_ACTIVE_VERSION_TO_OWNER);
									slMediaOriginated = (StringList) mapObj.get(FROM_ACTIVE_VERSION_TO_ORIGINATED);
									slMediaFileName = (StringList) mapObj.get(FILENAME);
									slMediaFileSize = (StringList) mapObj.get(FILESIZE);
									//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - Start
									slVersionLogicalID = (StringList) mapObj.get(FROM_ACTIVE_VERSION_TO_ID);
									//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - End

									slMediaFileFormat = (StringList) mapObj.get(FROM_ACTIVE_VERSION_TO_FILE_FORMAT);
									iSize = slMediaTitle.size();
									if ( iSize > 0){
										for ( int k = 0; k <  iSize ; k++){
											jsonMediaObj = Json.createObjectBuilder();
											strTitle = slMediaTitle.get(k);
											strFileSize = slMediaFileSize.get(slMediaFileName.indexOf(":"+strTitle));

											//DCM(DS) 2022x-04 - Added for MS Files Preview - START
											jsonMediaObj.add(DomainConstants.SELECT_ID, slMediaId.get(k));
											//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - Start
											jsonMediaObj.add(LOGICAL_ID, slVersionLogicalID.get(k));
											//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - End
											jsonMediaObj.add("versionDescription", slMediaDescription.get(k));
											jsonMediaObj.add(SELECT_MEDIA_ID, mediaDataId);
											//DCM(DS) 2022x-04 - Added for MS Files Preview - END
											jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_TITLE, strTitle);
											strOwner =  slMediaOwner.get(k);
											jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_OWNER, strOwner);
											strOriginated = slMediaOriginated.get(k);
											jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_ORIGINATED, strOriginated);
											jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_FILE_SIZE, strFileSize);
											strFileFormat = slMediaFileFormat.get(k);
											jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_FILE_FORMAT, strFileFormat);
											jsonArrayDocumentsMedia.add(jsonMediaObj);
										}
									}
								} else if(mediaList instanceof String) {
									strTitle = (String) mediaList;
									jsonMediaObj = Json.createObjectBuilder();
									//DCM(DS) 2022x-04 - Added for MS Files Preview - START
									jsonMediaObj.add(DomainConstants.SELECT_ID, (String) mapObj.get(FROM_ACTIVE_VERSION_TO_PHYSICAL_ID));
									//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - Start
									jsonMediaObj.add(LOGICAL_ID, (String) mapObj.get(FROM_ACTIVE_VERSION_TO_ID));
									//DCM (DS) User Story 327 - Enable Multiple document uploads at once in CLMS, CLM and Toolkit media - End
									jsonMediaObj.add("versionDescription", (String) mapObj.get(FROM_ACTIVE_VERSION_TO_DESCRIPTION));
									//DCM(DS) 2022x-04 - Added for MS Files Preview - END
									jsonMediaObj.add(SELECT_MEDIA_ID, mediaDataId);
									jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_TITLE, strTitle);
									strOwner =  (String) mapObj.get(FROM_ACTIVE_VERSION_TO_OWNER);
									jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_OWNER, strOwner);
									strOriginated = (String) mapObj.get(FROM_ACTIVE_VERSION_TO_ORIGINATED);
									jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_ORIGINATED, strOriginated);
									strFileSize = (String) mapObj.get(FROM_ACTIVE_VERSION_TO_FILE_SIZE);
									jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_FILE_SIZE, strFileSize);
									strFileFormat = (String) mapObj.get(FROM_ACTIVE_VERSION_TO_FILE_FORMAT);
									jsonMediaObj.add(FROM_ACTIVE_VERSION_TO_FILE_FORMAT, strFileFormat);
									jsonArrayDocumentsMedia.add(jsonMediaObj);
								}
							}
						}
					}
				}

				jsonReturnObj.add("Media", jsonArrayMedia);
				jsonReturnObj.add("Documents", jsonArrayDocumentsMedia);
				//Added for Claim Media upload issue - START
				jsonReturnObj.add("NoFileMediaObjects", slDummyClaimMedia.toString().replaceAll("[\\[\\]]", ""));
				//Added for Claim Media upload issue - END
			}
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}

		return jsonReturnObj.build().toString();
	}

	/**This Method Return Claim Media File in base64 format
	 * @param context 
	 * @param string
	 * @return
	 * @throws MatrixException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	private String getFileBase64(Context context, String strMediaId) throws MatrixException, IOException  {
		String strBase64ImageData = EMPTY_STRING;
		try
		{
			DomainObject domObj = DomainObject.newInstance(context, strMediaId);
			String strWorkspace = context.createWorkspace();
			String strFileName = null;             
			String filepath=null;
			String fileext = null;
			String genericFormat = PropertyUtil.getSchemaProperty(context,"format_generic");
			FileList listOfFiles = domObj.getFiles(context,genericFormat);
			domObj.checkoutFiles(context, false, genericFormat, listOfFiles, strWorkspace);
			StringList slBase64ImageString =new StringList();
			String strBase64String = EMPTY_STRING;

			for(int i=0;i<listOfFiles.size();i++)
			{
				strFileName = listOfFiles.get(i).toString();
				filepath= strWorkspace + File.separator +strFileName;
				fileext = filepath.substring(filepath.lastIndexOf(".")+1);			
				if(LIST_FILE_IMAGE_FILE_FORMAT.contains(fileext))
				{
					resize(filepath, 250, 250);
					strBase64String = encodeFileToBase64Binary(new File(filepath));
					strBase64String = new StringBuilder("<img src='data:image/").append(fileext).append(";base64,").append(strBase64String).append("'></img>").toString();
				}
				slBase64ImageString.addElement(strBase64String);
			}
			if(BusinessUtil.isNotNullOrEmpty(slBase64ImageString))
			{
				strBase64ImageData = String.join(CONSTANT_STRING_COMMA,slBase64ImageString);	
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		finally {
			context.deleteWorkspace();
		}
		return strBase64ImageData;
	}

	/**This method convert file into base64 String
	 * @param file
	 * @param fileext 
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	private static String encodeFileToBase64Binary(File file) throws IOException
	{
		try(FileInputStream fileInputStreamReader = new FileInputStream(file))
		{
			byte[] bytes = new byte[(int)file.length()];
			int size = fileInputStreamReader.read(bytes);
			return new String(Base64.encodeBase64(bytes), "UTF-8");
		}
	}
	/**
	 * This method fetches the other details of the object
	 * @param context
	 * @param paramMap
	 * @return
	 * @throws MatrixException
	 */
	public Map<String, JsonArrayBuilder> getClaimPOCOtherDetails(Context context,HashMap paramMap) throws MatrixException {
		String strObjectId = (String)paramMap.get(STRING_OBJECT_ID);
		Map<String, JsonArrayBuilder> mpDetails = new HashMap<>();
		mpDetails.put("Revisions", getRevisionsData(context,strObjectId,(String)paramMap.get("strRevisionSelect")));
		return mpDetails;
	}

	/**
	 * This method returns the History data of the object in JSON format
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws MatrixException
	 */
	private JsonArrayBuilder getHistoryDataOfObject(Context context, String strObjectId) throws MatrixException 
	{
		JsonArrayBuilder outArr = Json.createArrayBuilder();
		try {
			HashMap mpHistoryData = UINavigatorUtil.getHistoryData(context, strObjectId);
			StringList slUser =(StringList) mpHistoryData.get("user");
			StringList slTime =(StringList) mpHistoryData.get("time");
			StringList slDescription =(StringList) mpHistoryData.get("description");
			StringList slAction =(StringList) mpHistoryData.get("action");
			StringList slState =(StringList) mpHistoryData.get("state");
			JsonObjectBuilder jsonObjectBuilder = null ;

			if(!slUser.isEmpty())
			{
				for(int i=0;i<slUser.size();i++)
				{
					jsonObjectBuilder = Json.createObjectBuilder() ;
					jsonObjectBuilder.add("user", slUser.get(i).split(STR_COLON)[1]);
					jsonObjectBuilder.add("time",  slTime.get(i).replace("time:",""));
					jsonObjectBuilder.add("description",  slDescription.get(i));
					jsonObjectBuilder.add("action",  slAction.get(i));
					jsonObjectBuilder.add("state",  slState.get(i).split(STR_COLON)[1]);
					outArr.add(jsonObjectBuilder);
				}
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return outArr;
	}

	/**
	 * This method returns the Revisions data of the object in JSON format
	 * @param context
	 * @param strObjectId
	 * @param strRevsionSelect
	 * @return
	 * @throws FrameworkException
	 */
	private JsonArrayBuilder getRevisionsData(Context context, String strObjectId, String strRevsionSelect) throws FrameworkException {
		JsonObjectBuilder jsonObjectBuilder = null ;
		JsonArrayBuilder outArr = Json.createArrayBuilder();
		DomainObject domObj = DomainObject.newInstance(context, strObjectId);
		StringList slRevsionSelect = StringUtil.split(strRevsionSelect,CONSTANT_STRING_COMMA);
		slRevsionSelect.addElement(DomainConstants.SELECT_ID);
		MapList mlRevisionData = domObj.getRevisionsInfo(context, slRevsionSelect, new StringList());
		String strValue ;
		for(int i=0;i<mlRevisionData.size();i++)
		{
			jsonObjectBuilder = Json.createObjectBuilder();
			for(int j=0;j<slRevsionSelect.size();j++)
			{
				strValue = (String)((Map)mlRevisionData.get(i)).get(slRevsionSelect.get(j));
				if(DomainConstants.SELECT_TYPE.equals(slRevsionSelect.get(j)))
				{
					strValue = EnoviaResourceBundle.getFrameworkStringResourceProperty(context,
							"emxFramework.Type."+ strValue, context.getLocale());
				}
				if(DomainConstants.SELECT_CURRENT.equals(slRevsionSelect.get(j)))
				{
					strValue = new StringBuilder((String)((Map)mlRevisionData.get(i)).get(DomainConstants.SELECT_ID)).append(CONST_STR_PIPE).append((String)((Map)mlRevisionData.get(i)).get(DomainConstants.SELECT_CURRENT)).toString();
				}
				jsonObjectBuilder.add(slRevsionSelect.get(j), strValue);			

			}
			jsonObjectBuilder.add(IS_EDITABLE,isEditable(context, (Map)mlRevisionData.get(i)));
			outArr.add(jsonObjectBuilder);
		}
		return outArr;
	}

	/**
	 * This method returns the Ownership data of the object in JSON format
	 * @param context
	 * @param strObjectId
	 * @param strOwnershipSelect
	 * @return
	 * @throws Exception
	 */
	private JsonArrayBuilder getOwnershipData(Context context, String strObjectId, String strOwnershipSelect) throws Exception {
		JsonObjectBuilder jsonObjectBuilder = null ;
		JsonArrayBuilder outArr = Json.createArrayBuilder();
		StringList slOwnershipSelect = StringUtil.split(strOwnershipSelect,CONSTANT_STRING_COMMA);
		slOwnershipSelect.addElement(DomainConstants.SELECT_ID);
		MapList mlOwnershipData = DomainAccess.getAccessSummaryList(context, strObjectId);
		String strValue ;
		for(int i=0;i<mlOwnershipData.size();i++)
		{
			jsonObjectBuilder = Json.createObjectBuilder();
			for(int j=0;j<slOwnershipSelect.size();j++)
			{
				strValue = (String)((Map)mlOwnershipData.get(i)).get(slOwnershipSelect.get(j));
				if(UIUtil.isNotNullAndNotEmpty(strValue)) 
				{
					jsonObjectBuilder.add(slOwnershipSelect.get(j), strValue);
				}

				if("comment".equalsIgnoreCase(slOwnershipSelect.get(j)) && "Primary".equals(strValue))
				{
					jsonObjectBuilder.add(IS_EDITABLE, false);
				}

			}
			outArr.add(jsonObjectBuilder);
		}
		return outArr;
	}

	/**
	 * Method to clone Claim Request Object
	 * @param context
	 * @param request
	 * @param strOjectId : context object id
	 * @return JSON
	 * @throws java.lang.Exception 
	 */
	public String cloneObject(Context context, String strBaseOjectId, String strParentId, Map<String, Object> mpProductConfigAttribute) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strEventMessage = DomainConstants.EMPTY_STRING;
		String strStatus = STATUS_ERROR;
		String strType = DomainConstants.EMPTY_STRING;
		String strNewCloneObjectId = DomainConstants.EMPTY_STRING;

		Map mpRelAttributeMap = new HashMap<>();
		try {
			if(UIUtil.isNotNullAndNotEmpty(strBaseOjectId)){
				DomainObject domBaseObj = DomainObject.newInstance(context,strBaseOjectId);
				StringList slAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.CloneAttribute.List"), CONSTANT_STRING_COMMA);   
				if(domBaseObj.isKindOf(context, TYPE_PG_CLAIM_SUPPORT)) {
					strType = TYPE_PG_CLAIM_SUPPORT;
				} else if(domBaseObj.isKindOf(context, TYPE_PG_CLAIM_REQUEST)) {
					strType = TYPE_PG_CLAIM_REQUEST;
				} else if(domBaseObj.isKindOf(context, TYPE_PG_CLAIM)) {
					strType = TYPE_PG_CLAIM;
					//DCM (DS) US 1602 clone Product Config object from source claim rel and connect it on new Claim Rel on Copy or Revise.- Start
					    
					createRelAttributeMap(context,slAttrList, mpRelAttributeMap, domBaseObj, strParentId,RELATIONSHIP_CLAIMS);
					//DCM (DS) US 1602 clone Product Config object from source claim rel and connect it on new Claim Rel on Copy or Revise.- End
				}
				//DCM (DS) 2022x-02 CW - REQ 44028 - Copy Disclaimer - START
				else if(domBaseObj.isKindOf(context, TYPE_PG_DISCLAIMER)) {
					strType = TYPE_PG_DISCLAIMER;
					//DCM (DS) US 1612 clone Product Config object from source claim rel and connect it on new Disclaimer Rel on Copy or Revise.- Start
					createRelAttributeMap(context,slAttrList, mpRelAttributeMap, domBaseObj, strParentId,RELATIONSHIP_PG_DISCLAMER);
					//DCM (DS) US 1612 clone Product Config object from source claim rel and connect it on new Disclaimer Rel on Copy or Revise.- End
				}
				//DCM (DS) 2022x-02 CW - REQ 44028 - Copy Disclaimer - END

				//clone object
				String strAutoName = DomainObject.getAutoGeneratedName(context, FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, strType, true), DomainConstants.EMPTY_STRING);

				//clone existing Claim objects
				BusinessObject busNewObj;
				//Handle Claim Support separately for attached files to be copied and Active Version rel to be created
				if(TYPE_PG_CLAIM_SUPPORT.equals(strType)) {
					//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - START
					busNewObj = domBaseObj.cloneObject(context,strAutoName,null, null, false);
					//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - END
				} else {
					busNewObj = domBaseObj.clone(context, null, strAutoName, FIRST_REV, VAULT_PRODUCTION, false);
				}
				strNewCloneObjectId = busNewObj.getObjectId(context);

				if(TYPE_PG_CLAIM_SUPPORT.equals(strType) && BusinessUtil.isNotNullOrEmpty(strNewCloneObjectId)) {
					//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - START
					checkinFilesFromObject(context, busNewObj, domBaseObj);
					//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - END
				}

				//connect Derived from object to new clone object
				Map<String, Object> mapMethodParameters = new HashMap<>();
				mapMethodParameters.put("strBaseOjectId", strBaseOjectId);
				mapMethodParameters.put("strParentId", strParentId);
				mapMethodParameters.put("mpProductConfigAttribute", mpProductConfigAttribute);
				mapMethodParameters.put("strType", strType);
				mapMethodParameters.put("strNewCloneObjectId", strNewCloneObjectId);
				mapMethodParameters.put("mpRelAttributeMap", mpRelAttributeMap);
				mapMethodParameters.put("strAutoName", strAutoName);
				MapList mlInfo = setAttributesAndConnectionsOfClone(context, mapMethodParameters);
				strStatus = STATUS_INFO;
				strEventMessage = getOutputMessage(context, STRING_CLONE, mlInfo);
			}
		} catch (FrameworkException e) {
			strEventMessage = e.getMessage();
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			strStatus = STATUS_ERROR;			
		} catch(Exception ex){
			strEventMessage = ex.getMessage();
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			strStatus = STATUS_ERROR;
		} finally {

			jsonReturnObj.add(STRING_STATUS, strStatus);
			jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
			jsonReturnObj.add(STRING_DATA,strNewCloneObjectId);
		}
		return jsonReturnObj.build().toString();
	}

	/**DCM (DS) 2022x-01 CW ALM-51457 - Two users editing functional comments at the same time
	 * This Method to refactor code
	 * @param context
	 * @param mapMethodParameters
	 * @return MapList
	 * @throws Exception 
	 */
	private MapList setAttributesAndConnectionsOfClone(Context context, Map<String, Object> mapMethodParameters) throws Exception {
		MapList mlInfo = null;
		
		String strBaseOjectId = (String) mapMethodParameters.get("strBaseOjectId");
		String strParentId = (String) mapMethodParameters.get("strParentId");
		Map<String, Object> mpProductConfigAttribute = (Map<String, Object>) mapMethodParameters.get("mpProductConfigAttribute");
		String strType = (String) mapMethodParameters.get("strType");
		String strNewCloneObjectId = (String) mapMethodParameters.get("strNewCloneObjectId");
		Map mpRelAttributeMap = (Map) mapMethodParameters.get("mpRelAttributeMap");
		String strAutoName = (String) mapMethodParameters.get("strAutoName");
				
		if(UIUtil.isNotNullAndNotEmpty(strNewCloneObjectId))
		{
			connectDerivedObject(context, strBaseOjectId, strNewCloneObjectId);
			if(!TYPE_PG_CLAIM.equals(strType) && !TYPE_PG_CLAIM_REQUEST.equals(strType))
			{
				//clone and connect all the Claim objects connected to old Claim Req object to new clone object
				cloneAndConnectRelatedClaimObjects(context, strBaseOjectId, strNewCloneObjectId);
			}
			//clone and connect Media objects and it's Claim connections and versions
			if(TYPE_PG_CLAIM_REQUEST.equals(strType))
			{
				cloneAndConnectRelatedMediaObjects(context, strBaseOjectId, strNewCloneObjectId, STRING_CLONE);
			}
			//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
			if(TYPE_PG_CLAIM.equals(strType) || TYPE_PG_DISCLAIMER.equals(strType) || STRING_CLAIM.equals(strType) || STRING_DISCLAIMER.equals(strType))
			{
				String strRelName = RELATIONSHIP_CLAIMS;
				if(TYPE_PG_DISCLAIMER.equals(strType)) {
					strRelName = RELATIONSHIP_PG_DISCLAMER;
				}
				//DCM (DS) US 1602 clone Product Config object from source claim rel and connect it on new Claim Rel on Copy or Revise.- Start
				DomainObject fromObj= new DomainObject(strParentId);
				DomainObject toObj= new DomainObject(strNewCloneObjectId);
				//clone and connect Claim object to context Claim Request object
				DomainRelationship domRel = DomainRelationship.connect(context, fromObj,  strRelName, toObj);
				domRel.setAttributeValues(context, mpRelAttributeMap);
				
				
				Map<String, Object> mpEmptyAttributeMap = new HashMap<>();
				copyMapWithEmptyValues(mpEmptyAttributeMap, mpRelAttributeMap);
				String strTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(context,SCHEMA_TYPE, strType, context.getSession().getLanguage());
				String strTypeNameRevision = new StringBuilder(strTypeDisplayName).append(CONST_WHITE_SPACE).append(strAutoName).append(CONST_WHITE_SPACE).append(FIRST_REV).toString();
				updateHistoryOnBaseObject(context, mpRelAttributeMap, mpEmptyAttributeMap, strTypeNameRevision, strParentId);
				Map<String, Object> mapMethodParameter = new HashMap<>();
				mapMethodParameter.put("strSourceObj", strBaseOjectId);
				mapMethodParameter.put("strNewObjectid", strNewCloneObjectId);
				mapMethodParameter.put("isFromClaim", true);
				mapMethodParameter.put("strWhere", DomainConstants.EMPTY_STRING);
				mapMethodParameter.put("mpProductConfigAttribute", mpProductConfigAttribute);
				mapMethodParameter.put("strParentId", strParentId);
				mapMethodParameter.put("strTypeNameRevision", strTypeNameRevision);
				cloneClaimProductConfigurationOnCopyOrReviseClaim(context, mapMethodParameter);
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
				//DCM (DS) US 1602 clone Product Config object from source claim rel and connect it on new Claim Rel on Copy or Revise.- End
			}
			
			//DCM (DS) - ALM 50806 - Newly revised claim has different owner than the person created the revision - START
			if(UIUtil.isNotNullAndNotEmpty(strParentId))
			{
				mlInfo = getObjectsInfo(context, strParentId, strNewCloneObjectId);
			} 
			else 
			{
				//DCM (DS) - ALM 50806 - Newly revised claim has different owner than the person created the revision - END
				mlInfo = getObjectsInfo(context, strBaseOjectId, strNewCloneObjectId);
				//DCM (DS) - ALM 50806 - Newly revised claim has different owner than the person created the revision - START
			}
			//DCM (DS) - ALM 50806 - Newly revised claim has different owner than the person created the revision - END
			//set owner on new clone object
			if(BusinessUtil.isNotNullOrEmpty(mlInfo))
			{
				DomainObject domCloneObject = DomainObject.newInstance(context,strNewCloneObjectId);
				// copy base object owner to cloned object owner for Claim
				//DCM (DS) 2022x-02 CW - REQ 44028 - Copy Disclaimer - START
				if(TYPE_PG_CLAIM.equals(strType) || TYPE_PG_DISCLAIMER.equals(strType) || STRING_CLAIM.equals(strType) || STRING_DISCLAIMER.equals(strType))
				{
					String strOwner = (String)((Map)mlInfo.get(0)).get(DomainConstants.SELECT_OWNER);
					//DCM (DS) 2022x-02 CW - Add ownership to CLR owner on CLM - START
					String strPersonId = PersonUtil.getPersonObjectID(context, strOwner);
					addCoOwner(context, strNewCloneObjectId, strPersonId, "Parent Owner");
					//DCM (DS) 2022x-02 CW - Add ownership to CLR owner on CLM - END
				}
				// copy base object Originator to cloned object Originator for Claim Support
				if(TYPE_PG_CLAIM_SUPPORT.equals(strType))
				{
					domCloneObject.setAttributeValue(context, DomainConstants.ATTRIBUTE_ORIGINATOR, context.getUser());
				}
			}
		}
		return mlInfo;
	}

	/**This method get attributes from pgClaims relationship and create and return map
	 * @param context
	 * @param slAttrList
	 * @param mpRelAttributeMap
	 * @param domBaseObj
	 * @throws FrameworkException
	 */
	private void createRelAttributeMap(Context context, StringList slAttrList,Map mpRelAttributeMap, DomainObject domBaseObj, String strParentId,String strRelName) throws FrameworkException {
		StringList slObjSelects  = new StringList();
		StringList slMultiObjSelets = new StringList();
		String strMultiAttrList = EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.MultiVlaue.Attribute.List");
		for(int i=0;i<slAttrList.size();i++){
			//Bug 4356,4360 - System duplicates the attribute value on Copy and revise of Claim - START
			slObjSelects.add(new StringBuilder("to[").append(strRelName).append("|from.id=='").append(strParentId).append("'].").append(DomainObject.getAttributeSelect(slAttrList.get(i))).toString());
			//Bug 4356,4360 - System duplicates the attribute value on Copy and revise of Claim - END
			if(strMultiAttrList.contains(slAttrList.get(i))){
				slMultiObjSelets.add("to["+strRelName+"]." +DomainObject.getAttributeSelect(slAttrList.get(i)));
			}
		}
		Map attributeValMap = (Map)domBaseObj.getInfo(context, slObjSelects, slMultiObjSelets);

		// Define the pattern for matching and extracting the desired key
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("to\\["+strRelName+"\\]\\.attribute\\[([^\\]]+)\\]");

		// Create a new map with modified keys
		String attrName ;
		Object val ; 
		java.util.regex.Matcher matcher ;
		String modifiedKey ;
		for (Object key : attributeValMap.keySet())  
		{ 
			// search  for value 
			attrName = (String)key;
			val = attributeValMap.get(attrName); 
			matcher = pattern.matcher(attrName);

			// Check if the pattern matches
			if (matcher.find()) {
				modifiedKey = matcher.group(1);
				mpRelAttributeMap.put(modifiedKey, val);
			}
		} 
		//DCM (DS) US 1602 clone Product Config object from source claim rel and connect it on new Claim Rel on Copy or Revise.- End
	}

	/**
	 * Method to revise Claim Request Object
	 * @param context
	 * @param request
	 * @param strObjectIds : context object id
	 * @return JSON
	 * @throws java.lang.Exception 
	 */
	public String reviseObject(Context context,String strObjectIds, String strParentId, Map<String, Object> mapAttribute) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strEventMessage = DomainConstants.EMPTY_STRING;
		String strStatus = STATUS_ERROR;
		String strNewRevObjectId =DomainConstants.EMPTY_STRING;
		//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - START
		boolean isTransactionActive = false;
		//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - END
		try {
			if(UIUtil.isNotNullAndNotEmpty(strObjectIds)){
				//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - START
				StringList slObjIds = StringUtil.split(strObjectIds, CONSTANT_STRING_COMMA);
				if (slObjIds != null && !slObjIds.isEmpty()) {
					StringList slObjSelectables = new StringList(2);
					slObjSelectables.add(DomainConstants.SELECT_CURRENT);
					slObjSelectables.add(DomainConstants.SELECT_POLICY);
					ContextUtil.startTransaction(context, true);
					isTransactionActive = true;
					StringBuilder sbEventMsg = new StringBuilder();
					String strClaimId;
					Map<String, Object> mapClaimAttribute = null;
					for(int i=0 ; i<slObjIds.size() ; i++) {
						strClaimId = slObjIds.get(i);
						DomainObject domObj = DomainObject.newInstance(context,strClaimId);
						//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - END
						//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
						if(mapAttribute != null)
						{
							mapClaimAttribute = (Map<String, Object>) mapAttribute.get(strClaimId);
						}
						//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
						BusinessObject busLastRevObj = domObj.getLastRevision(context);
						DomainObject domLastRevision = DomainObject.newInstance(context,busLastRevObj);
						StringList slSelects = new StringList(DomainConstants.SELECT_TYPE);
						slSelects.add(DomainConstants.SELECT_NAME);
						slSelects.add(DomainConstants.SELECT_REVISION);
						slSelects.add(DomainConstants.SELECT_CURRENT);
						Map mapLastRevisionInfo = domLastRevision.getInfo(context, slSelects);
						String strLastRevisionCurrent = (String) mapLastRevisionInfo.get(DomainConstants.SELECT_CURRENT);
						String strObjName = (String) mapLastRevisionInfo.get(DomainConstants.SELECT_NAME);
						if(STATE_RELEASED.equals(strLastRevisionCurrent) || STATE_APPROVED.equals(strLastRevisionCurrent))
						{
							boolean bReviseAllowed = true;
							if(UIUtil.isNotNullAndNotEmpty(strParentId)) {
								//If disclaimer revise, check if Claim is in Preliminary state
								DomainObject domParentObj = DomainObject.newInstance(context,strParentId);
								if(domParentObj.isKindOf(context, TYPE_PG_CLAIM))
								{
									Map mpParentInfo = domParentObj.getInfo(context, slSelects);
									String strParentName = (String) mpParentInfo.get(DomainConstants.SELECT_NAME);
									String strParentState = (String) mpParentInfo.get(DomainConstants.SELECT_CURRENT);
									if(UIUtil.isNullOrEmpty(strParentState) || !STATE_PRELIMINARY.equals(strParentState))
									{
										String[] strMsgArgs = {strParentName, strObjName};
										strEventMessage = MessageUtil.getMessage(context, null, "emxCPN.ClaimManager.Message.ConnectedClaimShouldBePreliminary", strMsgArgs, null,context.getLocale(), "emxCPNStringResource");
										bReviseAllowed = false;
										//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - START
										break;
										//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - END
									}
								}
							}

							if(bReviseAllowed)
							{
								strNewRevObjectId = reviseAndConnectRelatedObjects(context, strParentId,
										mapClaimAttribute, domLastRevision);
								strStatus = STATUS_INFO;

								//DCM (DS) 2022x-01 CW - ALM 50906 - Incorrect alert message after Revise Claim/Disclaimer objects - START
								MapList mlInfo = null;
								if(UIUtil.isNotNullAndNotEmpty(strParentId))
								{
									//DCM (DS) - ALM 50806 - Newly revised claim has different owner than the person created the revision - START
									mlInfo = getObjectsInfo(context, strParentId, strNewRevObjectId);
									//DCM (DS) - ALM 50806 - Newly revised claim has different owner than the person created the revision - END
								} 
								else 
								{
									mlInfo = getObjectsInfo(context, slObjIds.get(i), strNewRevObjectId);
								}
								//DCM (DS) 2022x-01 CW - ALM 50906 - Incorrect alert message after Revise Claim/Disclaimer objects - END

								//set owner on new clone object
								//DCM (DS) 2022x-01 CW - ALM 50906 - Incorrect alert message after Revise Claim/Disclaimer objects - START
								if(BusinessUtil.isNotNullOrEmpty(mlInfo))
								{
									strEventMessage = addCoOwner(context, strNewRevObjectId, sbEventMsg,
											domLastRevision, mapLastRevisionInfo, mlInfo);
								}
							}
						}else
						{
							String strTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(context,SCHEMA_TYPE, mapLastRevisionInfo.get(DomainConstants.SELECT_TYPE).toString(), context.getSession().getLanguage());
							String[] strMsgArgs = {
									strTypeDisplayName,
									(String)mapLastRevisionInfo.get(DomainConstants.SELECT_NAME),
									(String)mapLastRevisionInfo.get(DomainConstants.SELECT_REVISION)
							};
							strEventMessage = MessageUtil.getMessage(context, null, "emxCPN.ClaimManager.Message.LatestRevisionNotApprovedOrReleased", strMsgArgs, null,context.getLocale(), "emxCPNStringResource");
							//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - START
							break;
							//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - END
						}

					}
					//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - START
					ContextUtil.commitTransaction(context);
					isTransactionActive = false;
					strEventMessage = UIUtil.isNotNullAndNotEmpty(sbEventMsg.toString()) ? sbEventMsg.toString() : strEventMessage;
					//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - END
					jsonReturnObj.add(STRING_STATUS, strStatus);
					jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
					jsonReturnObj.add(STRING_DATA,strNewRevObjectId);
				}
			}

		} catch(Exception ex){
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			createErrorMessage(context, jsonReturnObj);
		} 
		//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - START
		if(isTransactionActive) {
			ContextUtil.abortTransaction(context);
		}
		//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - END
		return jsonReturnObj.build().toString();
	}

	/**DCM (DS) 2022x-01 CW ALM-51457 - Two users editing functional comments at the same time
	 * This Method to refactor code
	 * @param context
	 * @param strParentId
	 * @param mapClaimAttribute
	 * @param domLastRevision
	 * @return String
	 * @throws Exception 
	 */
	private String reviseAndConnectRelatedObjects(Context context, String strParentId,
			Map<String, Object> mapClaimAttribute, DomainObject domLastRevision)
			throws Exception {
		String strNewRevObjectId;
		//revise object
		BusinessObject busNewRevObj;
		String strNextRevision = domLastRevision.getNextSequence(context);
		if(domLastRevision.isKindOf(context, TYPE_PG_CLAIM_SUPPORT)) {
			//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - START
			busNewRevObj = domLastRevision.reviseObject(context, strNextRevision, false);
			//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - END
		} else {
			busNewRevObj = domLastRevision.revise(context, strNextRevision, VAULT_PRODUCTION);
		}
		strNewRevObjectId = busNewRevObj.getObjectId(context);

		if(domLastRevision.isKindOf(context, TYPE_PG_CLAIM_SUPPORT) && BusinessUtil.isNotNullOrEmpty(strNewRevObjectId)) {
			//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - START
			checkinFilesFromObject(context, busNewRevObj, domLastRevision);
			//2022-04 CW - Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - END
		}
		//clone and connect Media objects and it's Claim connections and versions
		if(domLastRevision.isKindOf(context, TYPE_PG_CLAIM_REQUEST))
		{
			cloneAndConnectRelatedMediaObjects(context, domLastRevision.getId(context), strNewRevObjectId, STRING_REVISE);
		}

		//On revise of Claim, connect parent Claim Request to new revision of Claim
		//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
		connectNewRevision(context, domLastRevision, strParentId, strNewRevObjectId, mapClaimAttribute);
		//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
		return strNewRevObjectId;
	}

	/**DCM (DS) 2022x-01 CW ALM-51457 - Two users editing functional comments at the same time
	 * This Method to refactor code
	 * @param context
	 * @param strNewRevObjectId
	 * @param sbEventMsg
	 * @param domLastRevision
	 * @param mapLastRevisionInfo
	 * @param mlInfo
	 * @return String
	 * @throws Exception 
	 */
	private String addCoOwner(Context context, String strNewRevObjectId, StringBuilder sbEventMsg,
			DomainObject domLastRevision, Map mapLastRevisionInfo, MapList mlInfo)
			throws Exception {
		String strEventMessage;
		//Resetting the owner for Claim and Disclaimer Revise to match with Parent
		if(domLastRevision.isKindOf(context, TYPE_PG_CLAIM) || domLastRevision.isKindOf(context, TYPE_PG_DISCLAIMER))
		{
			//DCM (DS) - ALM 50806 - Newly revised claim has different owner than the person created the revision - END
			String strOwner = (String)((Map)mlInfo.get(0)).get(DomainConstants.SELECT_OWNER);
			//DCM (DS) 2022x-02 CW - Add ownership to CLR owner on CLM - START
			String strPersonId = PersonUtil.getPersonObjectID(context, strOwner);
			addCoOwner(context, strNewRevObjectId, strPersonId, "Parent Owner");
			//DCM (DS) 2022x-02 CW - Add ownership to CLR owner on CLM - END
			//DCM (DS) 2022x-01 CW - ALM 50906 - Incorrect alert message after Revise Claim/Disclaimer objects - START
			mlInfo.set(0, mapLastRevisionInfo);
		}
		//DCM (DS) 2022x-01 CW - ALM 50906 - Incorrect alert message after Revise Claim/Disclaimer objects - END
		strEventMessage = getOutputMessage(context, STRING_REVISE, mlInfo);
		//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - START
		sbEventMsg.append(strEventMessage).append("<br/>");
		//DCM (DS) 2022x-01 CW - REQ 44729 - Allow Mass Revise - END
		return strEventMessage;
	}

	/**
	 * This method connects the new revision of Claim/Disclaimer with the Parent Claim Request/Claim
	 * and disconnects the old revision from the parent
	 * Connecting and disconnecting to fire the Create Action triggers which add the ownership stamping on the child object
	 * @param context
	 * @param domLastRevObj
	 * @param strParentId
	 * @param strNewRevObjectId
	 * @param mapClaimAttribute
	 * @throws Exception
	 */
	public void connectNewRevision(Context context, DomainObject domLastRevObj, String strParentId, String strNewRevObjectId, Map<String, Object> mapClaimAttribute) throws Exception
	{
		Map mpRelAttributeMap = new HashMap<>();
		//On revise of Claim, connect parent Claim Request to new revision of Claim
		if((domLastRevObj.isKindOf(context, TYPE_PG_CLAIM) || domLastRevObj.isKindOf(context, TYPE_PG_DISCLAIMER)) && BusinessUtil.isNotNullOrEmpty(strNewRevObjectId))
		{
			String strRelationship = null;
			String strExpandType = null;
			String strType=null;
			if(domLastRevObj.isKindOf(context, TYPE_PG_CLAIM))
			{
				strRelationship = RELATIONSHIP_CLAIMS;
				strExpandType = TYPE_PG_CLAIM_REQUEST;
				strType = TYPE_PG_CLAIM;
			}
			else if(domLastRevObj.isKindOf(context, TYPE_PG_DISCLAIMER))
			{
				strRelationship = RELATIONSHIP_PG_DISCLAMER;
				//DCM (DS) US 1602/1612 clone Product Config object from source claim rel and connect it on new Claim/Disclaimer Rel on Copy or Revise. Start
				strExpandType = TYPE_PG_CLAIM_REQUEST;
				//DCM (DS) US 1602/1612 clone Product Config object from source claim rel and connect it on new Claim/Disclaimer Rel on Copy or Revise. End
				strType = TYPE_PG_DISCLAIMER;
			}	
			//Feature: 2165 US 1603/1612: Claim Data Model changes - Revise claim : start
			StringList slAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.ReviseAttribute.List"), CONSTANT_STRING_COMMA);                   
			createRelAttributeMap(context,slAttrList, mpRelAttributeMap, domLastRevObj, strParentId,strRelationship);
			//Feature: 2165 US 1603/1612: Claim Data Model changes - Revise claim : End
			//connect CLR with new CLM revision
			//Feature: 2165 US 1603: Claim Data Model changes - Revise claim : start
			DomainObject fromObj= new DomainObject(strParentId);
			DomainObject toObj= new DomainObject(strNewRevObjectId);
			DomainRelationship domRel = DomainRelationship.connect(context, fromObj,  strRelationship, toObj);
			//Feature: 2165 US 1603: Claim Data Model changes - Revise claim : End
			//DCM (DS) US 1602/1612 clone Product Config object from source claim rel and connect it on new Claim/Disclaimer Rel on Copy or Revise. -Start
			if(TYPE_PG_CLAIM.equals(strType) || TYPE_PG_DISCLAIMER.equals(strType))
			{
				//Feature: 2165 US 1603: Claim Data Model changes - Revise claim : start
				domRel.setAttributeValues(context, mpRelAttributeMap);
				//Feature: 2165 US 1603: Claim Data Model changes - Revise claim : End
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
				String strTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(context,SCHEMA_TYPE, strType, context.getSession().getLanguage());
				StringList slSelect = new StringList(DomainConstants.SELECT_NAME);
				slSelect.add(DomainConstants.SELECT_REVISION);
				Map mapObjectInfo = toObj.getInfo(context, slSelect);
				String strTypeNameRevision = new StringBuilder(strTypeDisplayName).append(CONST_WHITE_SPACE).append((String)mapObjectInfo.get(DomainConstants.SELECT_NAME)).append(CONST_WHITE_SPACE).append((String)mapObjectInfo.get(DomainConstants.SELECT_REVISION)).toString();
				Map<String, Object> mpEmptyAttributeMap = new HashMap<>();
				copyMapWithEmptyValues(mpEmptyAttributeMap, mpRelAttributeMap);
				updateHistoryOnBaseObject(context, mpRelAttributeMap, mpEmptyAttributeMap, strTypeNameRevision, strParentId);
				
				Map<String, Object> mapMethodParameter = new HashMap<>();
				mapMethodParameter.put("strSourceObj", domLastRevObj.getObjectId(context));
				mapMethodParameter.put("strNewObjectid", strNewRevObjectId);
				mapMethodParameter.put("isFromClaim", true);
				mapMethodParameter.put("strWhere", DomainConstants.EMPTY_STRING);
				mapMethodParameter.put("mpProductConfigAttribute", mapClaimAttribute);
				mapMethodParameter.put("strParentId", strParentId);
				mapMethodParameter.put("strTypeNameRevision", strTypeNameRevision);
				cloneClaimProductConfigurationOnCopyOrReviseClaim(context, mapMethodParameter);
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
			}
			//DCM (DS) US 1602 clone Product Config object from source claim rel and connect it on new Claim Rel on Copy or Revise. End
			//disconnect CLR with old CLM revision
			StringList slRelSelects = new StringList(4);
			slRelSelects.addElement(DomainRelationship.SELECT_ID);

			StringBuilder sbBusWhere = new StringBuilder(DomainConstants.SELECT_ID).append("==").append(strParentId);
			MapList mlClaimReqInfo = domLastRevObj.getRelatedObjects( context, 
					strRelationship, //relPattern
					strExpandType, //typePattern
					null, //busSelects
					slRelSelects, //relSelects
					true, // getTo
					false, // getFrom
					(short) 1,  //recurseToLevel
					sbBusWhere.toString(), //busWhere
					null, //relWhere
					0 //limit
					);
			if(BusinessUtil.isNotNullOrEmpty(mlClaimReqInfo)){
				Map mpClaimReqInfo;
				String strClaimToCLRRelId;
				StringList slClaimToCLRRelIds = new StringList();
				for(Object objClaimReqInfo: mlClaimReqInfo){
					mpClaimReqInfo = (Map)objClaimReqInfo;
					if(mpClaimReqInfo.containsKey(DomainRelationship.SELECT_ID)){
						strClaimToCLRRelId = mpClaimReqInfo.get(DomainRelationship.SELECT_ID).toString();
						if(!UIUtil.isNullOrEmpty(strClaimToCLRRelId)){
							slClaimToCLRRelIds.add(strClaimToCLRRelId);
						}
					}
				}
				if(BusinessUtil.isNotNullOrEmpty(slClaimToCLRRelIds))
				{
					String[] arrClaimToCLRRelIds = new String[slClaimToCLRRelIds.size()];
					slClaimToCLRRelIds.toArray(arrClaimToCLRRelIds);
					DomainRelationship.disconnect(context, arrClaimToCLRRelIds);
				}
				//Added for Claim Request Image float issue during Claim revise - START
				//US:2657 Float Image on new revision of Disclaimer
				if(domLastRevObj.isKindOf(context, TYPE_PG_CLAIM) || domLastRevObj.isKindOf(context, TYPE_PG_DISCLAIMER))
				{
					floatClaimRequestImage(context, domLastRevObj.getObjectId(context), strNewRevObjectId, strParentId);
				}
				//Added for Claim Request Image float issue during Claim revise - END
			}
		}
	}

	/**
	 * Method to create clone of related Media and connect to new cloned Claim Request
	 * @param context
	 * @param strOjectId : context object id
	 * @param strNewCloneObjectId : cloned object id
	 * @return void
	 * @throws Exception 
	 */
	private static void cloneAndConnectRelatedMediaObjects(Context context, String strOjectId, String strNewCloneObjectId, String strEvent) throws Exception{
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strOjectId) && UIUtil.isNotNullAndNotEmpty(strNewCloneObjectId))
			{
				DomainObject domObj = DomainObject.newInstance(context,strOjectId);
				DomainObject domClaimRequestCloned = DomainObject.newInstance(context,strNewCloneObjectId);
				//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
				//Get related Claim Media ids
				StringList slClaimReqImgMedObjIds = domObj.getInfoList(context, SELECT_CLAIMREQUEST_IMAGE_ID);	
				String [] strArrClaimReqImgMedObjIds = slClaimReqImgMedObjIds.toArray(new String[slClaimReqImgMedObjIds.size()]);
				StringList slSelect = new StringList(SELECT_CLAIMIMAGE_CLAIM_ID);
				slSelect.add(SELECT_CLAIMIMAGE_CLAIM_CURRENT);
				slSelect.add(DomainConstants.SELECT_ID);
				MapList mlClaimInfo = DomainObject.getInfo(context, strArrClaimReqImgMedObjIds, slSelect);
				//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END
				if(BusinessUtil.isNotNullOrEmpty(mlClaimInfo))
				{
					Map mpClaimInfo;
					String strClaimId;
					String strClaimCurrent;
					String strMediaId;
					StringList slClaimId;
					StringList slClaimCurrent;
					DomainObject domMediaObject;
					DomainObject domMediaClonedObject;
					for(Object objClaimReqInfo: mlClaimInfo)
					{
						mpClaimInfo = (Map)objClaimReqInfo;
						strMediaId = mpClaimInfo.get(DomainConstants.SELECT_ID).toString();
						//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
						strClaimId = (String) mpClaimInfo.get(SELECT_CLAIMIMAGE_CLAIM_ID);
						strClaimCurrent = (String) mpClaimInfo.get(SELECT_CLAIMIMAGE_CLAIM_CURRENT);
						//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END
						slClaimId = StringUtil.split(strClaimId,SelectConstants.cSelectDelimiter);
						slClaimCurrent = StringUtil.split(strClaimCurrent,SelectConstants.cSelectDelimiter);
						if(STRING_CLONE.equals(strEvent))
						{
							slClaimId = removeStateBeforeApproved (slClaimId, slClaimCurrent);
						}

						domMediaObject = DomainObject.newInstance(context,strMediaId);
						//clone existing Media objects
						BusinessObject busNewClaimObj = domMediaObject.cloneObject(context, null, FIRST_REV, VAULT_PRODUCTION, true); 
						domMediaClonedObject = DomainObject.newInstance(context,busNewClaimObj);
						//on connect fetched rel id is used for further operation so keeping this connect operation in loop
						DomainRelationship domRelMediaConnection = DomainRelationship.connect(context, domClaimRequestCloned, RELATIONSHIP_PG_CLAIM_REQUEST_IMAGE, domMediaClonedObject);
						//create version objects of cloned media
						createVersionObjects(context, busNewClaimObj);
						//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
						String[] arrClaimIds = slClaimId.toArray(new String[slClaimId.size()]);
						DomainRelationship.connect(context, domMediaClonedObject, RELATIONSHIP_PG_CLAIM_IMAGE, true, arrClaimIds);
						//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END
					}
				}	
			}
		}
		catch(Exception e)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
	}

	/**
	 * Method returns StringList from an object
	 * @param objToConvert
	 * @return StringList
	 */
	private static StringList removeStateBeforeApproved (StringList slObjectId, StringList slObjectCurrent)   {
		StringList slReturn = new StringList();
		String strCurrent;
		for(int i=0; i<slObjectCurrent.size(); i++)
		{
			strCurrent = slObjectCurrent.get(i).trim();
			if(STATE_APPROVED.equals(strCurrent) || STATE_RELEASED.equals(strCurrent))
			{
				slReturn.add(slObjectId.get(i));
			}
		}
		return slReturn;
	}

	/**
	 * Method to create version objects
	 * @param context
	 * @param busObj
	 * @return void
	 * @throws Exception 
	 * @throws FrameworkException 
	 */
	private static void createVersionObjects (Context context, BusinessObject busObj) throws Exception  {
		CommonDocument cDoc = new CommonDocument(busObj);
		FileList fileList = busObj.getFiles(context, "generic");
		FileItr itr = new FileItr(fileList);
		while(itr.next())
		{
			String sfileName = itr.obj().getName();
			cDoc.createVersion(context,sfileName,sfileName,null);
		}
	}

	/**
	 * Method to create clone of related Claims and connect to new cloned Claim Request
	 * @param context
	 * @param strOjectId : context object id
	 * @param strNewCloneObjectId : cloned object id
	 * @return void
	 * @throws MatrixException 
	 */
	private void cloneAndConnectRelatedClaimObjects(Context context, String strOjectId, String strNewCloneObjectId) throws MatrixException{
		StringList slCloneObjects = new StringList();
		if(UIUtil.isNotNullAndNotEmpty(strOjectId)){
			DomainObject domObj = DomainObject.newInstance(context,strOjectId);
			if(domObj.isKindOf(context, TYPE_PG_CLAIM_REQUEST)){
				String strSelectClaimObjId = new StringBuilder(SELECT_FROM).append(RELATIONSHIP_CLAIMS).append(SELECT_TO_ID).toString(); 
				String strObjGenName = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, TYPE_PG_CLAIM, true);
				String strClaimAutoName;
				//Get related Claim object ids related to Claim Request
				if(UIUtil.isNotNullAndNotEmpty(strSelectClaimObjId ))
				{
					slCloneObjects = domObj.getInfoList(context, strSelectClaimObjId);	
				}

				if(!slCloneObjects.isEmpty()){

					String strClaimObjId;
					StringList slNewCloneClaimObjIds = new StringList();
					for(int i=0; i<slCloneObjects.size(); i++){
						strClaimObjId = slCloneObjects.get(i);
						strClaimAutoName = DomainObject.getAutoGeneratedName(context, strObjGenName, DomainConstants.EMPTY_STRING);

						//clone existing Claim objects
						BusinessObject busNewClaimObj = DomainObject.newInstance(context,strClaimObjId).clone(context, null, strClaimAutoName, FIRST_REV, VAULT_PRODUCTION, false); 
						String strNewClaimObjId = busNewClaimObj.getObjectId(context);
						slNewCloneClaimObjIds.addElement(strNewClaimObjId);
					}
					//connect newly cloned claim objects to new Claim Request object
					connectClaimObjects(context, strNewCloneObjectId, slNewCloneClaimObjIds);
				}
			}
		}
	}

	/**
	 * Method to Claims object to Claim Request
	 * @param context
	 * @param strOjectId : context object id
	 * @param slClaimObjectIds : Claim object id list
	 * @return void
	 * @throws FrameworkException 
	 */
	private void connectClaimObjects (Context context, String strOjectId, StringList slClaimObjectIds) throws FrameworkException {
		if(UIUtil.isNotNullAndNotEmpty(strOjectId) && !slClaimObjectIds.isEmpty()){
			DomainObject domObj = DomainObject.newInstance(context,strOjectId);
			String [] strClaimObjectIds = slClaimObjectIds.toArray(new String[slClaimObjectIds.size()]);
			DomainRelationship.connect(context, domObj, RELATIONSHIP_CLAIMS, true, strClaimObjectIds);
		}
	}

	/**
	 * Method to get object info
	 * @param context
	 * @param strOjectId : context object id
	 * @param strNewCloneObjectId : Clone object id
	 * @return MapList
	 * @throws FrameworkException 
	 */
	private MapList getObjectsInfo(Context context, String strOjectId, String strNewCloneObjectId) throws FrameworkException {
		MapList mlData = new MapList();
		if(UIUtil.isNotNullAndNotEmpty(strOjectId) && UIUtil.isNotNullAndNotEmpty(strNewCloneObjectId))
		{
			String[] strObjectIds = {strOjectId,strNewCloneObjectId};
			StringList slSelects = new StringList(DomainConstants.SELECT_TYPE);
			slSelects.addElement(DomainConstants.SELECT_NAME);
			slSelects.addElement(DomainConstants.SELECT_REVISION);
			slSelects.addElement(DomainConstants.SELECT_OWNER);
			mlData =  DomainObject.getInfo(context, strObjectIds, slSelects);
		}
		return mlData;
	}

	/**
	 * Method to get message on clone or revise event
	 * @param context
	 * @param strEvent : event type
	 * @param mlInfo : old and new object info in maplist
	 * @return MapList
	 * @throws Exception 
	 */
	private String getOutputMessage(Context context, String strEvent, MapList mlInfo) throws Exception{
		String strMsg = DomainConstants.EMPTY_STRING;

		if(null!= mlInfo && mlInfo.size()==2){
			Map mBaseObjInfo = (Map)mlInfo.get(0);
			Map mNewObjInfo = (Map)mlInfo.get(1);
			String strActionMsg= DomainConstants.EMPTY_STRING;
			if(STRING_CLONE.equals(strEvent)){
				strActionMsg = "copied";
			}else if(STRING_REVISE.equals(strEvent)){
				strActionMsg = "revised";
			}

			String[] strMsgArgs = {
					(String)mBaseObjInfo.get(DomainConstants.SELECT_TYPE),
					(String)mBaseObjInfo.get(DomainConstants.SELECT_NAME),
					(String)mBaseObjInfo.get(DomainConstants.SELECT_REVISION),
					strActionMsg,
					(String)mNewObjInfo.get(DomainConstants.SELECT_TYPE),
					(String)mNewObjInfo.get(DomainConstants.SELECT_NAME),
					(String)mNewObjInfo.get(DomainConstants.SELECT_REVISION),
			};
			strMsg = MessageUtil.getMessage(context, null, "emxFramework.ClaimManager.CloneClaimReqOutputMessage", strMsgArgs, null,context.getLocale(), "emxFrameworkStringResource");
		}

		return strMsg;
	}

	/**
	 * Method to connect derived object
	 * @param context
	 * @param strDerivedFromOjectId 
	 * @param strCloneObjectId
	 * @return void
	 * @throws FrameworkException 
	 */
	private void connectDerivedObject(Context context, String strDerivedFromOjectId, String strCloneObjectId) throws FrameworkException{
		if(UIUtil.isNotNullAndNotEmpty(strDerivedFromOjectId) && UIUtil.isNotNullAndNotEmpty(strCloneObjectId))
		{
			DomainRelationship.connect(context, strDerivedFromOjectId, RELATIONSHIP_DERIVED, strCloneObjectId, false);
		}
	}

	/**To add  the members for security context with access
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public String addMember(Context context, Map<String,Object> mpRequestMap) throws Exception
	{
		JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
		try
		{			
			//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - START
			mpRequestMap.put(STRING_CHECK_AUTHOR_ROLE, true);
			//DCM (DS) 2022x-02 CW - Defect 52777 - Add coowner with Claim Originator Role - END
			String strOutputMsg = validatePersonForNotEBPAndClaimUser(context,mpRequestMap);
			String strPersonId =(String) mpRequestMap.get("personId");
			if(!strOutputMsg.equals(STRING_OK))
			{
				jsonObjBuilder.add(STRING_STATUS,STATUS_ERROR);
				jsonObjBuilder.add(STRING_MESSAGE,strOutputMsg);
			}
			else
			{
				String strObjId =(String) mpRequestMap.get(STRING_OBJECT_ID);

				StringList accessNames = DomainAccess.getLogicalNames(context, strObjId);
				String ownerAccess = accessNames.get(accessNames.size()-1);   

				DomainAccess.createObjectOwnership(context, strObjId, strPersonId, ownerAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
				jsonObjBuilder.add(STRING_STATUS, STATUS_SUCCESS);
			}

		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			jsonObjBuilder.add(STRING_STATUS, STATUS_ERROR);
			jsonObjBuilder.add(STRING_MESSAGE, ex.getMessage());
			jsonObjBuilder.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
		}
		return jsonObjBuilder.build().toString();
	}

	/**To remove the access for a workspace for security context with access
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public String deleteAccess (Context context, Map<String,Object> mpRequestMap) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strEventMessage = null;
		String strStatus = STATUS_INFO;

		String strAccessList =(String) mpRequestMap.get(STRING_OBJECT_ID);
		StringList slAccessList = StringUtil.split(strAccessList, CONSTANT_STRING_COMMA);

		String secContextId = null;
		int fIndex;
		int sIndex;
		int tIndex ;
		String strObjId = null;
		String strOrganization = null;
		String strProject =null;
		String strComments = null;

		for(int i=0;i<slAccessList.size();i++)
		{
			try {
				secContextId = slAccessList.get(i);
				sIndex = -1;
				tIndex = -1;
				fIndex = secContextId.indexOf(STR_COLON);

				if (fIndex != -1)
				{
					sIndex =secContextId.indexOf(STR_COLON, fIndex+1);
					strObjId = secContextId.substring(0, fIndex);
				}
				if (sIndex != -1)
				{
					tIndex =secContextId.indexOf(STR_COLON, sIndex+1);
				}
				if (fIndex != -1 && sIndex != -1)
				{
					strOrganization = secContextId.substring(fIndex+1, sIndex);
				}
				if (sIndex != -1 && tIndex != -1)
				{
					strProject = secContextId.substring(sIndex+1, tIndex);
				}
				strComments = secContextId.substring(tIndex+1);

				if (strOrganization!=null && strProject!=null && strComments!=null)
				{
					if("Primary".equalsIgnoreCase(strComments) || STRING_PR_ASSIGNEE.equalsIgnoreCase(strComments))
					{
						jsonReturnObj.add(STRING_MESSAGE, EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.RemoveOwnership.Warning", context.getLocale()));
					}
					else {
						DomainAccess.deleteObjectOwnership(context, strObjId, strOrganization, strProject, strComments);
						strEventMessage = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.RemoveOwnership.Success", context.getLocale()) ;
						jsonReturnObj.add(STRING_STATUS, strStatus);
						jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
					}
				}
			} catch (FrameworkException ex) {
				logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
				jsonReturnObj.add(STRING_MESSAGE, ex.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(ex));
			}
		}
		return jsonReturnObj.build().toString();
	}

	/**
	 * This method adds the Person as Co owner with full access
	 * @param context
	 * @param objectId
	 * @param personId
	 * @param comment
	 * @throws Exception
	 */
	public void addCoOwner(Context context, String objectId, String personId, String comment) throws Exception
	{
		StringList accessNames = DomainAccess.getLogicalNames(context, objectId);
		String ownerAccess = accessNames.get(accessNames.size()-1);   
		//Adding owner (all) access by default
		DomainAccess.createObjectOwnership(context, objectId, personId, ownerAccess, comment);
	}

	/**
	 * This method removes the SOV added for a person as co owner
	 * @param context
	 * @param objectId
	 * @param organization
	 * @param project
	 * @param comment
	 * @throws Exception
	 */
	public void deleteCoOwner(Context context, String objectId, String organization, String project, String comment) throws Exception
	{
		DomainAccess.deleteObjectOwnership(context, objectId, organization, project, comment);
	}

	/**
	 * This method adds the ownership of Claim Request to the created Claim for inheriting the co owners
	 * @param context
	 * @param strClaimId
	 * @param strClaimRequestID
	 * @throws Exception
	 */
	public void addOwnershipStamping(Context context,String strClaimId,String strClaimRequestID) throws Exception
	{
		String command = "modify bus $1 add ownership businessobject $2";
		//no alternate api for adding ownership
		MqlUtil.mqlCommand(context, command, false, strClaimId, strClaimRequestID);
	}
	/**This is method use to get Static Data Like Picklist.
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws FrameworkException
	 */
	public Response getPickListData(matrix.db.Context context, Map<String, Object> mpRequestMap) throws FrameworkException {
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		try {
			String strWhere = (String) mpRequestMap.get("whereCondition");
			String strType =(String) mpRequestMap.get("type");
			StringList slObj = StringUtil.split((String) mpRequestMap.get("objectSelect"),CONSTANT_STRING_COMMA);

			slObj.addElement(DomainConstants.SELECT_NAME);
			slObj.addElement(DomainConstants.SELECT_TYPE);
			MapList mlObjs = DomainObject.findObjects(context,	// eMatrix context
					strType,							// type pattern
					VAULT_ESERVICE_PRODUCTION,			// Vault Pattern
					strWhere,						// where expression
					slObj);								// object selects
			PGClaimModuler.mapList2JsonArray(jsonArray, mlObjs);
			output.add("data", jsonArray);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
		}
		return Response.status(HttpServletResponse.SC_OK).entity(output.build().toString()).build();
	}

	/**This Method use to get MQL notice from user context
	 * @param context
	 * @param jsonStatus
	 * @throws MatrixException
	 */
	public static void createErrorMessage(Context context, JsonObjectBuilder jsonStatus)
			throws MatrixException {
		StringBuilder sbMessage =new StringBuilder();
		context.updateClientTasks();
		ClientTaskList ctlLoggedInUserTaskList = context.getClientTasks();
		ClientTaskItr clientTaskItr = new ClientTaskItr(ctlLoggedInUserTaskList);

		while (clientTaskItr.next()) {
			ClientTask ctUserTask = clientTaskItr.obj();
			String strNotice = ctUserTask.getTaskData();
			sbMessage.append(strNotice).append(CONST_NEW_LINE);
		}

		context.clearClientTasks();
		if(UIUtil.isNotNullAndNotEmpty(sbMessage.toString()))
		{
			jsonStatus.add(STRING_STATUS, STATUS_ERROR);
			jsonStatus.add(STRING_MESSAGE, sbMessage.toString());
		}
	}

	/**This Method demotes the Claim Request object to Preliminary on rejection of Approval task on it
	 * @param context
	 * @param strCLRId
	 * @return
	 * @throws FrameworkException 
	 */
	public String demoteOnTaskRejection(Context context, String strCLRId) throws Exception  {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strEventMessage = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.TaskRejection.DemoteMsg", context.getLocale());
		String strStatus = STATUS_INFO;

		boolean isCtxPushed = false;   
		try{
			String objectId = strCLRId;

			if(UIUtil.isNotNullAndNotEmpty(objectId))
			{
				DomainObject doClaimRequest = DomainObject.newInstance(context,strCLRId);
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isCtxPushed = true;
				doClaimRequest.setState(context, STATE_PRELIMINARY);
			}
		} catch (Exception ex) 
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
		}
		finally 
		{
			if(isCtxPushed)
			{
				ContextUtil.popContext(context);

			}
		}

		jsonReturnObj.add(STRING_STATUS, strStatus);
		jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
		return jsonReturnObj.build().toString();
	}

	/**This method use to set State.
	 * @param context
	 * @param strOjectId
	 * @param strNextState
	 * @return
	 * @throws MatrixException 
	 */
	public Response setState(Context context, Map mpRequestMap) throws MatrixException {
		String strOjectId = (String) mpRequestMap.get("id");
		String strNextState = (String) mpRequestMap.get("nextState");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject doClaimRequest = DomainObject.newInstance(context,strOjectId);
		String strStatus = STATUS_SUCCESS;
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strNextState))
			{
				doClaimRequest.setState(context, strNextState);
				jsonReturnObj.add(STRING_STATUS, strStatus);
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE,e.getMessage());
			createErrorMessage(context, jsonReturnObj);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build();

	}
	/**
	 * Method to replace the Obsolete claims in Claim Request Object
	 * @param context
	 * @param request
	 * @param strCLRId : context object id
	 * @return JSON
	 * @throws java.lang.Exception 
	 */
	public String replaceWithLatestRev(Context context, String strCLRId) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strEventMessage = DomainConstants.EMPTY_STRING;
		String strStatus = STATUS_ERROR;
		String strNewRevObjectId =DomainConstants.EMPTY_STRING;
		//DCM (DS) - ALM 49929 - Check for Latest Revision of Claims on promote of Claim Request does not work on No access Claims - START
		boolean isContextPushed = false;
		//DCM (DS) - ALM 49929 - Check for Latest Revision of Claims on promote of Claim Request does not work on No access Claims - END
		try {
			boolean isReplaced = false;
			if(UIUtil.isNotNullAndNotEmpty(strCLRId)){
				DomainObject domObj = DomainObject.newInstance(context,strCLRId);

				StringList slBusSelects = new StringList(DomainConstants.SELECT_TYPE);
				slBusSelects.addElement(DomainConstants.SELECT_ID);
				slBusSelects.addElement(DomainConstants.SELECT_NAME);
				slBusSelects.addElement(DomainConstants.SELECT_REVISION);
				slBusSelects.addElement(DomainConstants.SELECT_CURRENT);
				slBusSelects.addElement(SELECT_LAST_CURRENT);
				slBusSelects.addElement(SELECT_LAST_ID);
				StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);
				//DCM (DS) - ALM 49929 - Check for Latest Revision of Claims on promote of Claim Request does not work on No access Claims - START
				//Pushing context to get details of No Access connected objects as well
				ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isContextPushed = true;
				//DCM (DS) - ALM 49929 - Check for Latest Revision of Claims on promote of Claim Request does not work on No access Claims - END
				//US -1617 Enable Replace claim with highest revision command to disclaimer objects as well - start
				Pattern relPattern = new Pattern(RELATIONSHIP_CLAIMS);
				relPattern.addPattern(RELATIONSHIP_PG_DISCLAMER);
				Pattern typePattern = new Pattern(TYPE_PG_CLAIM);
				typePattern.addPattern(TYPE_PG_DISCLAIMER);
				//US -1617 Enable Replace claim with highest revision command to disclaimer objects as well - End
				MapList mlClaimsInfo = domObj.getRelatedObjects( context,
						//US -1617 Enable Replace claim with highest revision command to disclaimer objects as well - start
						relPattern.getPattern(), //relPattern
						typePattern.getPattern(), //typePattern
						//US -1617 Enable Replace claim with highest revision command to disclaimer objects as well - End
						slBusSelects, //busSelects
						slRelSelects, //relSelects
						false, // getTo
						true, // getFrom
						(short) 1,  //recurseToLevel
						null, //busWhere
						null, //relWhere
						0 //limit
						);
				if(BusinessUtil.isNotNullOrEmpty(mlClaimsInfo))
				{
					Map mpClaimInfo;
					String strClaimName;
					String strClaimState;
					String strClaimLastRevState;
					String strClaimRelId;
					StringList slObsoleteClaimRels = new StringList();
					StringList slNewRevCLMIds = new StringList();
					//Added for Claim Request Image float issue during Claim revise - START
					StringList slCLMIdsWithNewRew = new StringList();
					//Added for Claim Request Image float issue during Claim revise - END

					for(Object objClaimReqInfo: mlClaimsInfo)
					{
						mpClaimInfo =  (Map) objClaimReqInfo;
						strClaimName = (String) mpClaimInfo.get(DomainConstants.SELECT_NAME);
						strClaimState = (String) mpClaimInfo.get(DomainConstants.SELECT_CURRENT);
						strClaimRelId = (String) mpClaimInfo.get(DomainRelationship.SELECT_ID);

						//DCM (DS) Bug 5179 - Replace with highest revision - wrong alert message - START
						if(STATE_OBSOLETE.equalsIgnoreCase(strClaimState) && mpClaimInfo.containsKey(SELECT_LAST_CURRENT))
							//DCM (DS) Bug 5179 - Replace with highest revision - wrong alert message - END
						{
							strClaimLastRevState = (String) mpClaimInfo.get(SELECT_LAST_CURRENT);

							if(UIUtil.isNotNullAndNotEmpty(strClaimLastRevState) &&
									(STATE_APPROVED.equalsIgnoreCase(strClaimLastRevState) || STATE_RELEASED.equalsIgnoreCase(strClaimLastRevState)))
							{
								slObsoleteClaimRels.add(strClaimRelId);
								slNewRevCLMIds.add((String) mpClaimInfo.get(SELECT_LAST_ID));
								//Added for Claim Request Image float issue during Claim revise - START
								slCLMIdsWithNewRew.add((String) mpClaimInfo.get(DomainConstants.SELECT_ID));
								//Added for Claim Request Image float issue during Claim revise - END
							}
						}
					}

					if(BusinessUtil.isNotNullOrEmpty(slObsoleteClaimRels) && BusinessUtil.isNotNullOrEmpty(slNewRevCLMIds))
					{
						DomainObject domNewRev = DomainObject.newInstance(context);
						for(int i=0; i <slObsoleteClaimRels.size(); i++)
						{
							//connect CLR with new CLM revision
							domNewRev.setId(slNewRevCLMIds.get(i));
							DomainRelationship.setToObject(context, slObsoleteClaimRels.get(i), domNewRev);
							//Added for Claim Request Image float issue during Claim revise - START
							floatClaimRequestImage(context, slCLMIdsWithNewRew.get(i), slNewRevCLMIds.get(i), strCLRId);
							//Added for Claim Request Image float issue during Claim revise - END
						}
						isReplaced = true;
					}
				}
				if(isReplaced)
				{
					strStatus = STATUS_SUCCESS;
					strEventMessage = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.ReplaceWithLatestRev.Success", context.getLocale());
				} 
				else {
					strStatus = STATUS_INFO;
					strEventMessage = EnoviaResourceBundle.getFrameworkStringResourceProperty(context, "emxFramework.ClaimManager.ReplaceWithLatestRev.Warning", context.getLocale());
				}
			}

		}  catch(Exception ex){
			strEventMessage = ex.getMessage();
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, ex);
			strStatus = STATUS_ERROR;
		} finally {
			//DCM (DS) - ALM 49929 - Check for Latest Revision of Claims on promote of Claim Request does not work on No access Claims - START
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
			}
			//DCM (DS) - ALM 49929 - Check for Latest Revision of Claims on promote of Claim Request does not work on No access Claims - END
			jsonReturnObj.add(STRING_STATUS, strStatus);
			jsonReturnObj.add(STRING_MESSAGE,strEventMessage);
			jsonReturnObj.add(STRING_DATA,strNewRevObjectId);
		}
		return jsonReturnObj.build().toString();
	}

	/**
	 * This method returns the non null value for a Map selectable
	 * @param mpClaimInfo
	 * @param strMapSelectable
	 * @return Object
	 * @throws MatrixException
	 */
	public Object getNonNullValueFromMap(Map mpClaimInfo, String strMapSelectable) throws Exception 
	{
		Object objReturn;
		if(mpClaimInfo.containsKey(strMapSelectable))
		{
			objReturn = mpClaimInfo.get(strMapSelectable);
		}
		else
		{
			objReturn = DomainConstants.EMPTY_STRING;
		}
		return objReturn;
	}

	/**
	 * This method returns the related Claim objects for Image Group in JSON format
	 * @param context
	 * @param strObjectId
	 * @return JsonArrayBuilder
	 * @throws MatrixException
	 */
	private JsonArrayBuilder getRelatedClaimsForImage(Context context, String strObjectId) throws MatrixException 
	{
		JsonArrayBuilder outArr = Json.createArrayBuilder();
		try 
		{
			MapList mlClaimInfo = getRelatedClaimsMediaInfo(context, strObjectId);
			if(BusinessUtil.isNotNullOrEmpty(mlClaimInfo))
			{
				Map mpClaimInfo;
				String strMediaObjectId;
				for(Object objClaimInfo: mlClaimInfo)
				{
					mpClaimInfo = (Map)objClaimInfo;
					strMediaObjectId = (String) mpClaimInfo.get(DomainConstants.SELECT_ID);
					outArr.add(getClaimsForImage(context, mpClaimInfo, strMediaObjectId));
				}
			}
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return outArr;
	}

	/**
	 * This method returns the related Claim objects for Image Group in JSON format
	 * @param context
	 * @param mpClaimInfo
	 * @param strMediaObjectId
	 * @return JsonObjectBuilder
	 * @throws Exception 
	 */
	private JsonObjectBuilder getClaimsForImage(Context context, Map mpClaimInfo, String strMediaObjectId) throws Exception  
	{
		JsonObjectBuilder jsonObjectBuilder = null ;
		Object objImageTitle = getNonNullValueFromMap(mpClaimInfo, FROM_ACTIVE_VERSION_TO_TITLE);
		Object objFileID = getNonNullValueFromMap(mpClaimInfo, FROM_ACTIVE_VERSION_TO_ID);
		String strClaimRequestRelId = (String)  mpClaimInfo.get(DomainRelationship.SELECT_ID);

		jsonObjectBuilder = Json.createObjectBuilder() ;
		jsonObjectBuilder.add(DomainConstants.SELECT_ID, strMediaObjectId);
		jsonObjectBuilder.add("FileID", (String) objFileID);
		jsonObjectBuilder.add(STRING_IMAGE_DATA,getFileBase64(context, strMediaObjectId));
		jsonObjectBuilder.add(DomainConstants.SELECT_NAME, (String) objImageTitle);
		jsonObjectBuilder.add(DomainObject.getAttributeSelect(ATTRIBUTE_SEQUENCE), (String) mpClaimInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_SEQUENCE)));
		//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
		jsonObjectBuilder.add(DomainObject.getAttributeSelect(ATTRIBUTE_CLAIM_PANEL_LOCATION), (String) mpClaimInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_CLAIM_PANEL_LOCATION)));
		//DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END
		jsonObjectBuilder.add("ClaimRequestImageRelId", strClaimRequestRelId);
		jsonObjectBuilder.add("ClaimData", getClaimData(context, mpClaimInfo));

		return jsonObjectBuilder;
	}

	/**
	 * This method returns Claim Info
	 * @param context
	 * @param slClaimObjectId, slClaimId, slClaimImageRelId, mapComments
	 * @return String
	 * @throws FrameworkException 
	 */
	public JsonArrayBuilder getClaimData(Context context, Map<String, Object> mpClaimInfo) throws Exception
	{
		// DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - Start
		Map<String, Object> mpReturn;
		MapList mlReturn = new MapList();
		int index;
		// DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
		StringList slClaimImageRelId = BusinessUtil.getStringList(mpClaimInfo, SELECT_CLAIMIMAGE_REL_ID);
		StringList slClaimRelId = BusinessUtil.getStringList(mpClaimInfo, SELECT_CLAIMIMAGE_CLAIMS_ID);
		StringList slDisclaimerRelId = BusinessUtil.getStringList(mpClaimInfo, SELECT_CLAIMIMAGE_DISCLAIMER_ID);
		slClaimRelId.addAll(slDisclaimerRelId);

		StringList relationshipSelects = new StringList(DomainObject.getAttributeSelect(ATTRIBUTE_PGCLAIMSEQUENCE));
		relationshipSelects.add(DomainConstants.SELECT_TO_ID);
		relationshipSelects.add(DomainConstants.SELECT_TO_TYPE);
		relationshipSelects.add(DomainConstants.SELECT_TO_NAME);
		relationshipSelects.add(DomainConstants.SELECT_ID);
		relationshipSelects.add(DomainConstants.SELECT_NAME);
		relationshipSelects.add(SELECT_TO_DISCLAIMERNAME_RTE);
		relationshipSelects.add(SELECT_TO_CLAIMNAME_RTE);
		relationshipSelects.add(SELECT_TO_CLAIM_IMAGE_ID);
		relationshipSelects.add(SELECT_PRODUCTCONFIG_PANEL_LOCATION);

		StringList slCommentAttributes = StringUtil
				.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.CommentAttributes"), ",");
		for (int i = 0; i < slCommentAttributes.size(); i++) {
			relationshipSelects.add(DomainObject.getAttributeSelect(slCommentAttributes.get(i)));
		}

		MapList mlRelInfo = DomainRelationship.getInfo(context, slClaimRelId.toArray(new String[slClaimRelId.size()]),
				relationshipSelects);

		if (mlRelInfo != null && !mlRelInfo.isEmpty()) {
			String strClaimText;
			String strClaimName1;
			String strClaimId;
			String strRelId = "";
			String strObjType;
			String strPanelLocation = "";
			String strSequence;
			String strComments;
			String strTypeDisplayName;
			StringList slRelId;
			StringList slPanelLocation;

			Map<String, Integer> mapClaimSequence = new HashMap<>();
			Map<String, Integer> mapIndex = new HashMap<>();
			for (int i = 0; i < mlRelInfo.size(); i++) {
				Hashtable mapRelInfo = (Hashtable) mlRelInfo.get(i);
				strSequence = (String) mapRelInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_PGCLAIMSEQUENCE));
				strClaimId = (String) mapRelInfo.get(DomainConstants.SELECT_TO_ID);
				mapClaimSequence.put(strClaimId, Integer.parseInt(strSequence));
				mapIndex.put(strClaimId, i);
			}
			mapClaimSequence = sortByValue(mapClaimSequence);
			Set<String> keys = mapClaimSequence.keySet();
			for (Iterator<String> iteratorClaimSequence = keys.iterator(); iteratorClaimSequence.hasNext();) {
				strClaimId = iteratorClaimSequence.next();
				index = mapIndex.get(strClaimId);
				Hashtable mapRelInfo = (Hashtable) mlRelInfo.get(index);
				strObjType = (String) mapRelInfo.get(DomainConstants.SELECT_TO_TYPE);
				strTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(context, SCHEMA_TYPE, strObjType,
						context.getSession().getLanguage());
				if (TYPE_PG_CLAIM.equals(strObjType)) {
					strClaimText = (String) mapRelInfo.get(SELECT_TO_CLAIMNAME_RTE);
				} else {
					strClaimText = (String) mapRelInfo.get(SELECT_TO_DISCLAIMERNAME_RTE);
				}
				strClaimId = (String) mapRelInfo.get(DomainConstants.SELECT_TO_ID);
				slRelId = BusinessUtil.getStringList(mapRelInfo, SELECT_TO_CLAIM_IMAGE_ID);
				for (int j = 0; j < slRelId.size(); j++) {
					if (slClaimImageRelId.contains(slRelId.get(j))) {
						strRelId = slRelId.get(j);
					}
				}
				strClaimName1 = (String) mapRelInfo.get(DomainConstants.SELECT_TO_NAME);
				slPanelLocation = BusinessUtil.getStringList(mapRelInfo, SELECT_PRODUCTCONFIG_PANEL_LOCATION);
				strPanelLocation = String.join(",", slPanelLocation);
				strComments = getComments(context, mapRelInfo);

				mpReturn = new HashMap<>();
				mpReturn.put(DomainConstants.SELECT_ID, strClaimId);
				mpReturn.put("claimText", strClaimText);
				mpReturn.put(DomainConstants.SELECT_NAME, strClaimName1);
				mpReturn.put(DomainConstants.SELECT_TYPE, strTypeDisplayName);
				mpReturn.put("relId", strRelId);
				mpReturn.put("location", strPanelLocation);
				mpReturn.put(COMMENTS, strComments);
				mlReturn.add(mpReturn);
				// DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - End
			}
		}
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		PGClaimModuler.mapList2JsonArray(jsonArray, mlReturn);
		return jsonArray;
	}

	/**
	 * This method sorts hashmap by values
	 * @param context
	 * @param mpClaimInfo, slClaimId
	 * @return Map<Integer, String>
	 * @throws FrameworkException 
	 */
	public static Map<String, Integer> sortByValue(Map<String, Integer> hmToBeSorted)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer> > list =
				new LinkedList<Map.Entry<String, Integer> >(hmToBeSorted.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	/**
	 * This method returns the Comment Info for further processing
	 * @param context
	 * @param mpClaimInfo, slClaimId
	 * @return String
	 * @throws FrameworkException 
	 */
	private  String getComments(Context context, Hashtable mpClaimInfo) throws FrameworkException 
	{
		// DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - Start
		StringBuilder sbCommentAttributeValue = new StringBuilder();
		try {
			String strCommentAttrName;
			String strCommentValue;
			StringList slCommentAttributes = StringUtil
					.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.CommentAttributes"), ",");
			int iAttributeSize = slCommentAttributes.size();
			Map<String, String> mapCommentMapping = getMapForNameMapping(context);
			for (int i = 0; i < iAttributeSize; i++) {
				strCommentAttrName = slCommentAttributes.get(i);
				String strCommentLabel = mapCommentMapping.get(strCommentAttrName);
				strCommentValue = (String) mpClaimInfo.get(DomainObject.getAttributeSelect(strCommentAttrName));

				if (BusinessUtil.isNullOrEmpty(strCommentValue)) {
					strCommentValue = DomainConstants.EMPTY_STRING;
				}
				if (UIUtil.isNotNullAndNotEmpty(strCommentValue)) {
					sbCommentAttributeValue.append("<b>").append(strCommentLabel).append("</b>").append(STR_COLON)
					.append(CONST_WHITE_SPACE).append(strCommentValue).append("<br>");
				} else {
					sbCommentAttributeValue.append(strCommentValue);
				}
			}
			// DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - End
		} catch (FrameworkException e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return sbCommentAttributeValue.toString();
	}

	/**
	 * This method returns the Comment values appended in a StringBuilder
	 * @param context
	 * @param strObjectId, mapCommentReturn, slCommentValueTemp, strCommentAttrName, i, j
	 * @return StringBuilder
	 * @throws FrameworkException 
	 */
	private static StringBuilder getCommentValue(Context context, Map mapCommentReturn, StringList slCommentValueTemp, String strCommentAttrName, int j) throws FrameworkException 
	{
		// get attribute label mapping
		Map<String, String> mapCommentMapping = getMapForNameMapping(context);
		String strCommentLabel = mapCommentMapping.get(strCommentAttrName);
		StringBuilder sbCommentAttributeValue = new StringBuilder();
		String strCommentValueTemp;
		String strCommentTemp = (String) mapCommentReturn.get(j);
		if(BusinessUtil.isNullOrEmpty(strCommentTemp))
		{
			strCommentTemp = DomainConstants.EMPTY_STRING  ;
		}
		if(BusinessUtil.isNotNullOrEmpty(slCommentValueTemp))
		{
			strCommentValueTemp = slCommentValueTemp.get(j).trim();
			if(UIUtil.isNotNullAndNotEmpty(strCommentValueTemp))
			{
				if(UIUtil.isNotNullAndNotEmpty(strCommentTemp))
				{
					sbCommentAttributeValue.append(strCommentTemp);
				}
				sbCommentAttributeValue.append("<b>").append(strCommentLabel).append("</b>").append(STR_COLON).append(CONST_WHITE_SPACE).append(strCommentValueTemp).append("<br>");
			}
			else
			{
				sbCommentAttributeValue.append(strCommentTemp);
			}
		}
		else
		{
			sbCommentAttributeValue.append(strCommentTemp);
		}
		return sbCommentAttributeValue;
	}

	/**
	 * This method returns Comment attributes and label mapping
	 * @param context
	 * @return Map<String, String>
	 * @throws FrameworkException 
	 */
	private static Map<String, String> getMapForNameMapping(Context context) throws FrameworkException 
	{
		String[] arrCommentMapping;
		StringList slCommentNameMapping = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.CommentAttributes.NameMappings"),",");
		Map<String, String> mapCommentMapping = new HashMap<>();
		for(int i=0; i<slCommentNameMapping.size(); i++)
		{
			arrCommentMapping = slCommentNameMapping.get(i).split(STR_COLON);
			mapCommentMapping.put(arrCommentMapping[1], arrCommentMapping[0]);
		}
		return mapCommentMapping;
	}
	/**
	 * This method returns the related Claim Media Info for further processing
	 * @param context
	 * @param strObjectId
	 * @return MapList
	 * @throws FrameworkException 
	 */
	private static MapList getRelatedClaimsMediaInfo(Context context, String strObjectId) throws FrameworkException 
	{
		StringList slBusSelects = new StringList(DomainConstants.SELECT_ID);
		slBusSelects.add(FROM_ACTIVE_VERSION_TO_TITLE);
		slBusSelects.add(FROM_ACTIVE_VERSION_TO_ID);
		slBusSelects.add(DomainObject.getAttributeSelect(ATTRIBUTE_SEQUENCE));
		// DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - START
		slBusSelects.add(DomainObject.getAttributeSelect(ATTRIBUTE_CLAIM_PANEL_LOCATION));
		// DCM (DS) Sprint3 - ADO US 1623 - Claim Request Panel Image changes - END

		// DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - Start
		StringBuilder sbClaimRelIdSelect = new StringBuilder().append(SELECT_CLAIMIMAGE_TO).append("to[")
				.append(RELATIONSHIP_CLAIMS).append("|from.id=='").append(strObjectId).append("'].")
				.append(DomainConstants.SELECT_ID);
		StringBuilder sbDisclaimerRelIdSelect = new StringBuilder().append(SELECT_CLAIMIMAGE_TO).append("to[")
				.append(RELATIONSHIP_PG_DISCLAMER).append("|from.id=='").append(strObjectId).append("'].")
				.append(DomainConstants.SELECT_ID);
		slBusSelects.add(sbClaimRelIdSelect.toString());
		slBusSelects.add(sbDisclaimerRelIdSelect.toString());
		slBusSelects.add(SELECT_CLAIMIMAGE_REL_ID);

		StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);

		// Modified for Claim Request Image float issue during Claim revise - START
		Pattern relPattern = new Pattern(RELATIONSHIP_PG_CLAIM_REQUEST_IMAGE);
		// Modified for Claim Request Image float issue during Claim revise - END
		Pattern typePattern = new Pattern(TYPE_PG_CLAIM_MEDIA);
		// DCM Sprint 7: US: 2657: Disclaimer - Images tab changes - End
		DomainObject doClaimRequest = DomainObject.newInstance(context, strObjectId);
		return doClaimRequest.getRelatedObjects(context, 
				relPattern.getPattern(), // relPattern
				typePattern.getPattern(), // typePattern
				slBusSelects, // busSelects
				slRelSelects, // relSelects
				false, // getTo
				true, // getFrom
				(short) 1, // recurseToLevel
				null, // busWhere
				null, // relWhere
				0 // limit
				);
	}

	/**This method use create claim and connect with Master Copy Element if no claim connected to it 
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception 
	 */
	public String createClaimAndconnectMCE(matrix.db.Context context, Map<String, Object> mpRequestMap) throws Exception {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try
		{
			//DCM (DS) 2022x-01 CW - Modified for Attribute multivalue change - START
			String strSelectedMCE = (String) mpRequestMap.get("MasterElementId");
			String strClaimRequestId = (String) mpRequestMap.get("ClaimRequestId");
			//DCM (DS) 2022x-01 CW - Modified for Attribute multivalue change - END
			StringList slNewClaimName = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(strSelectedMCE)|| UIUtil.isNotNullAndNotEmpty(strClaimRequestId))
			{
				String[] arrObj = strSelectedMCE.split(CONSTANT_STRING_COMMA);
				StringList slSelect = new StringList(TO_PG_MASTER_COPY_CLAIM);
				slSelect.add(TO_PG_MASTER_COPY_CLAIM_FROM_NAME);
				slSelect.add(TO_PG_MASTER_COPY_CLAIM_FROM_REV);
				slSelect.add(TO_IMAGE_HOLDER_FROM_ID);
				slSelect.add(DomainConstants.SELECT_NAME);
				slSelect.add(DomainConstants.SELECT_ID);
				slSelect.add(DomainConstants.SELECT_TYPE);
				slSelect.add(FROM_ARTWORK_ELEMENT_CONTENT_TO_ATTRIBUTE_COPY_TEXT_RTE);
				slSelect.add(FROM_ARTWORK_ELEMENT_CONTENT_TO_ATTRIBUTE_IS_BASE_COPY);
				BusinessObjectWithSelectList objMapList = BusinessObject.getSelectBusinessObjectData(context, arrObj, slSelect);
				Iterator<matrix.db.BusinessObjectWithSelect> itrObject = objMapList.iterator();
				String strNotValidData = EMPTY_STRING;
				String strExistingClaimName = EMPTY_STRING;
				if(null != objMapList && !objMapList.isEmpty()){
					while(itrObject.hasNext()) {
						matrix.db.BusinessObjectWithSelect busSel = itrObject.next();
						if("true".equalsIgnoreCase(busSel.getSelectData(TO_PG_MASTER_COPY_CLAIM)))
						{
							strNotValidData =busSel.getSelectData(DomainConstants.SELECT_NAME);
							strExistingClaimName =new StringBuilder(busSel.getSelectData(TO_PG_MASTER_COPY_CLAIM_FROM_NAME)).append(CONST_WHITE_SPACE).append(busSel.getSelectData(TO_PG_MASTER_COPY_CLAIM_FROM_REV)).toString();
							break;
						}
					}
				}
				if(UIUtil.isNotNullAndNotEmpty(strNotValidData))
				{
					//Show Error Message as Claim is already connect to Master Artwork Element
					String strMessage = MessageUtil.getMessage(context, null, "emxCPN.ClaimManager.Message.AlreadyClaimConnected", new String[]{strNotValidData,strExistingClaimName}, null,context.getLocale(), "emxCPNStringResource");
					jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
					jsonReturnObj.add(STRING_MESSAGE,strMessage);
				}
				else
				{
					if(null != objMapList && !objMapList.isEmpty()){
						Iterator<matrix.db.BusinessObjectWithSelect> itrObject2 = objMapList.iterator();
						String strNewClaimName;
						while(itrObject2.hasNext()) {
							matrix.db.BusinessObjectWithSelect busSel = itrObject2.next();
							//DCM (DS) 2022x-01 CW - Modified for Attribute multivalue change - START
							strNewClaimName = createClaimAndConnectMasterCopy(context,busSel,strClaimRequestId);
							if(UIUtil.isNotNullAndNotEmpty(strNewClaimName))
							{
								slNewClaimName.addElement(strNewClaimName);
							}
							//DCM (DS) 2022x-01 CW - Modified for Attribute multivalue change - END
						}
						if(slNewClaimName.size()>0)
						{
							String strMessage = MessageUtil.getMessage(context, null, "emxCPN.ClaimManager.Message.ClaimConnected", new String[]{getStringListAsString(slNewClaimName)}, null,context.getLocale(), "emxCPNStringResource");
							jsonReturnObj.add(STRING_STATUS, STATUS_SUCCESS);
							jsonReturnObj.add(STRING_MESSAGE,strMessage);
						}
					}
				}
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE,e.getMessage());
		}
		return jsonReturnObj.build().toString();
	}

	/**this method is use to create Claim and connect it with master artwork element and set Claim Name attribute from Copy Text of Master Copy Element
	 * @param context
	 * @param busSel
	 * @param strClaimRequestId
	 * @return 
	 * @throws Exception 
	 */
	private String createClaimAndConnectMasterCopy(Context context, BusinessObjectWithSelect busSel, String strClaimRequestId) throws Exception {
		boolean isCtxPushed = false;
		String strMasterObjectId = busSel.getSelectData(DomainConstants.SELECT_ID);
		//US : 4869 : Enable add existing disclaimer using existing master copy element Start
		String strMasterObjectType = busSel.getSelectData(DomainConstants.SELECT_TYPE);
		String strNewObjectType = TYPE_PG_CLAIM;
		String strRelAsPerType = RELATIONSHIP_CLAIMS;
		String strClaimDisclaimerAttribName = ATTRIBUTE_CLAIMNAME_RTE;
		if(STRING_DISCLAIMER_MASTER_COPY.equals(strMasterObjectType))
		{
			strNewObjectType = TYPE_PG_DISCLAIMER;
			strRelAsPerType = RELATIONSHIP_PG_DISCLAMER;
			strClaimDisclaimerAttribName = ATTRIBUTE_DISCLAIMERNAME_RTE;
		}
		//US : 4869 : Enable add existing disclaimer using existing master copy element End
		DomainObject domMasterObj = DomainObject.newInstance(context, strMasterObjectId);
		//Create Claim and Connect to master copy element
		//US : 4869 : Enable add existing disclaimer using existing master copy element Start
		String strSymbolicName = FrameworkUtil.getAliasForAdmin(context, "Type",strNewObjectType,false);
		//US : 4869 : Enable add existing disclaimer using existing master copy element End
		String sObjGeneratorName = UICache.getObjectGenerator(context, strSymbolicName, "");
		String strName = DomainObject.getAutoGeneratedName(context, sObjGeneratorName, "");
		DomainObject domNewObj =DomainObject.newInstance(context);
		//US : 4869 : Enable add existing disclaimer using existing master copy element Start
		domNewObj.createObject(context, strNewObjectType, strName, null, null,VAULT_ESERVICE_PRODUCTION);
		//US : 4869 : Enable add existing disclaimer using existing master copy element End
		try
		{
			// push context to connect Claim to Release Master Artwork Element
			ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
			isCtxPushed = true;
			DomainRelationship.connect(context, domNewObj, RELATIONSHIP_PG_MASTER_COPY_CLAIM, domMasterObj);
			Map mpAttribute = new HashMap<>();
			//US : 4869 : Enable add existing disclaimer using existing master copy element Start
			mpAttribute.put(ATTRIBUTE_COPY_ELEMENT_TYPE, strMasterObjectType);
			//US : 4869 : Enable add existing disclaimer using existing master copy element End
			domNewObj.setAttributeValues(context, mpAttribute);
		}
		finally {
			if(isCtxPushed)
			{
				ContextUtil.popContext(context);
				isCtxPushed= false;
			}
		}
		//connect claim to context CLR
		DomainObject doParentObj = DomainObject.newInstance(context, strClaimRequestId);
		//US : 4869 : Enable add existing disclaimer using existing master copy element Start
		DomainRelationship domRel =DomainRelationship.connect(context, doParentObj, strRelAsPerType, domNewObj);
		//US : 4869 : Enable add existing disclaimer using existing master copy element End
		copyIPClassificationOnClaim(context, doParentObj, domNewObj);
		//US 1608: Disclaimer Data Model changes - Create changes :Start
		//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
		createClaimProductConfiguration(context, domRel.toString(), strClaimRequestId, strNewObjectType, strName);
		//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
		//US 1608: Disclaimer Data Model changes - Create changes :End
		//DCM (DS) Set Execution Type on Rel on Add Master Copy - Start
		Map mpRelAttribute = new HashMap<>();
		mpRelAttribute.put(ATTRIBUTE_EXECUTION_TYPE, CONST_PACKAGING_ARTWORK);
		domRel.setAttributeValues(context, mpRelAttribute);
		
		//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
		Map<String, Object> mpEmptyAttributeMap = new HashMap<>();
		copyMapWithEmptyValues(mpEmptyAttributeMap, mpRelAttribute);
		strNewObjectType = EnoviaResourceBundle.getAdminI18NString(context,SCHEMA_TYPE, strNewObjectType, context.getSession().getLanguage());
		updateHistoryOnBaseObject(context, mpRelAttribute, mpEmptyAttributeMap, new StringBuilder(strNewObjectType).append(CONST_WHITE_SPACE).append(strName).toString(), doParentObj.getObjectId(context));
		//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
		
		//DCM (DS) Set Execution Type on Rel on Add Master Copy - End

		if(domMasterObj.isKindOf(context,"Master Artwork Graphic Element"))
		{
			setClaimGraphicalImageFromImageHolder(context,domNewObj,busSel.getSelectData(TO_IMAGE_HOLDER_FROM_ID));
		}	
		else if(domMasterObj.isKindOf(context,"Master Artwork Element"))
		{
			//US : 4869 : Enable add existing disclaimer using existing master copy element Start
			setClaimNameFromCopyText(context,domNewObj,busSel,strClaimDisclaimerAttribName);
			//US : 4869 : Enable add existing disclaimer using existing master copy element End
		}
		return strName;
	}


	/**get Image Holder Generic Image and set it on pgClaimGraphicalImage
	 * @param context
	 * @param domNewObj
	 * @param strImageHolderId
	 * @throws MatrixException
	 * @throws IOException
	 */
	private void setClaimGraphicalImageFromImageHolder(Context context, DomainObject domNewObj, String strImageHolderId) throws MatrixException, IOException {
		String strBase64String = null;
		if(UIUtil.isNotNullAndNotEmpty(strImageHolderId))
		{
			strBase64String = getFileBase64(context, strImageHolderId);
		}
		if(UIUtil.isNotNullAndNotEmpty(strBase64String))
		{
			domNewObj.setAttributeValue(context, "pgClaimGraphicalImage", strBase64String);
		}
	}
	/**Get Copy Text and set ClaimName attribute
	 * @param context
	 * @param domNewObj
	 * @param busSel
	 * @throws FrameworkException
	 */
	private void setClaimNameFromCopyText(Context context, DomainObject domNewObj, BusinessObjectWithSelect busSel,String strClaimDisclaimerAttribName) throws FrameworkException {
		try {
			String strCopyText = null;
			if(null != busSel)
			{
				StringList slBaseCopy ;
				StringList slCopyText;
				slBaseCopy =busSel.getSelectDataList(FROM_ARTWORK_ELEMENT_CONTENT_TO_ATTRIBUTE_IS_BASE_COPY);
				slCopyText =busSel.getSelectDataList(FROM_ARTWORK_ELEMENT_CONTENT_TO_ATTRIBUTE_COPY_TEXT_RTE);
				for(int count = 0;count<slBaseCopy.size();count++)
				{
					if("Yes".equalsIgnoreCase(slBaseCopy.get(count)))
					{
						strCopyText = slCopyText.get(count);
						break;
					}
				}

				if(UIUtil.isNotNullAndNotEmpty(strCopyText))
				{
					domNewObj.setAttributeValue(context, strClaimDisclaimerAttribName, strCopyText);
				}

			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
	}

	/**
	 * Resizes an image to a absolute width and height (the image may not be
	 * proportional)
	 * @param inputImagePath Path of the original image
	 * @param outputImagePath Path to save the resized image
	 * @param scaledWidth absolute width in pixels
	 * @param scaledHeight absolute height in pixels
	 * @throws IOException
	 */
	public static void resize(String inputImagePath, int scaledWidth, int scaledHeight)
			throws IOException {
		// reads input image
		File inputFile = new File(inputImagePath);
		BufferedImage inputImage = ImageIO.read(inputFile);

		// creates output image
		BufferedImage outputImage = new BufferedImage(scaledWidth,
				scaledHeight, inputImage.getType());

		// scales the input image to the output image
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
		g2d.dispose();

		// extracts extension of output file
		String formatName = inputImagePath.substring(inputImagePath
				.lastIndexOf(".") + 1);

		// writes to output file
		ImageIO.write(outputImage, formatName, new File(inputImagePath));
	}
	/**
	 * This method validates Claim objects for Context Claim Request for duplicates in database
	 * Uniqueness is defined as combination of pgBrand and pgClaimName_RTE attributes 
	 * @param context
	 * @param strInputData
	 * @return
	 */
	public String validateClaims(Context context, Map mpCtxtClaimsInfo, String strTabName) throws Exception
	{
		if("POC".equalsIgnoreCase(strTabName)){
			strTabName = STRING_CLAIM;
		}
		//Defining html tag
		StringBuilder sbTableBuilder = new StringBuilder("<html><head>");
		//defining style for the table
		sbTableBuilder.append("<style>table, th, td {border: 1px solid black;border-collapse: collapse;}th, td {padding: 4px;}table#alter th {color: black;background-color: #87CAEB;}  </style></head>");
		//define table headers
		sbTableBuilder.append("<body><table id=\"alter\"><tr><th>Context ").append(strTabName).append("</th><th>Brand</th><th>").append(strTabName).append(" Value</th><th>Duplicate ").append(strTabName).append("</th></tr>");
		boolean duplicateClaimsFound = false;
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {			
			Map<String, String> mpUpdatedClaimsInfo = new HashMap();
			StringList slCtxtClaimBrand;
			StringList slCtxtBrandClaimValue;
			List<String> listBrandClaim ;
			String strSortedBrand;
			if(mpCtxtClaimsInfo != null && !mpCtxtClaimsInfo.isEmpty()){
				java.util.Set mapKeys = mpCtxtClaimsInfo.keySet();
				java.util.Iterator keyIterator = mapKeys.iterator();
				String strBrandClaimValue;
				String strClaimValue;
				Object objKey;
				while(keyIterator.hasNext())
				{
					objKey = keyIterator.next();
					strBrandClaimValue = (String) mpCtxtClaimsInfo.get(objKey);
					listBrandClaim  = Arrays.asList(strBrandClaimValue.split("~~"));
					slCtxtBrandClaimValue = StringList.asList(listBrandClaim);
					if(BusinessUtil.isNotNullOrEmpty(slCtxtBrandClaimValue)){
						slCtxtClaimBrand = StringUtil.split(slCtxtBrandClaimValue.get(0), ",");
						slCtxtClaimBrand.sort();
						//If slCtxtClaimBrand is null or empty then set strSortedBrand as empty string.
						strSortedBrand = BusinessUtil.isNotNullOrEmpty(slCtxtClaimBrand)?(slCtxtClaimBrand.toString().replaceAll("[\\[\\]]", "")):CONST_WHITE_SPACE;
						strClaimValue = slCtxtBrandClaimValue.size()>1 ? slCtxtBrandClaimValue.get(1) : CONST_WHITE_SPACE;
						if(BusinessUtil.isNullOrEmpty(strClaimValue)){
							strClaimValue = CONST_WHITE_SPACE;
						}
						//Form Map of unique key of Brand and Claim Name value for individual Claim objects 
						mpUpdatedClaimsInfo.put(objKey.toString(), new StringBuilder(strSortedBrand).append("~~").append(strClaimValue).toString());
					}
				}
			}
			StringList slObjSelectable = new StringList(DomainObject.getAttributeSelect(ATTRIBUTE_BRAND));
			slObjSelectable.addElement(DomainObject.getAttributeSelect(ATTRIBUTE_CLAIMNAME_RTE));
			slObjSelectable.addElement(DomainObject.getAttributeSelect(ATTRIBUTE_DISCLAIMERNAME_RTE));

			Map mpRequestData = new HashMap();
			mpRequestData.put("type", TYPE_PG_CLAIM);
			if("Disclaimers".equalsIgnoreCase(strTabName)){
				mpRequestData.put("type", TYPE_PG_DISCLAIMER);
			}
			mpRequestData.put("ObjSelectables", getStringListAsString(slObjSelectable));
			mpRequestData.put("filter", "");
			mpRequestData.put("limit", "0");
			mpRequestData.put("whereCondition", new StringBuilder(DomainConstants.SELECT_CURRENT).append("!=").append(STATE_OBSOLETE).toString());

			//Find all the non obsolete claims from database
			MapList mlObjs = getObjects(context, mpRequestData);
			if(null != mlObjs && !mlObjs.isEmpty()){
				Map mpInfo;
				String strRev;
				String strName;
				String strBrand;
				String strClaimName;
				String strClaimNameRev;
				String strCtxtClaimName;
				String strCtxtClaimRev;
				StringList slBrandValue;
				for(Object objMap: mlObjs){
					mpInfo = (Map)objMap;
					slBrandValue = StringUtil.split(mpInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_BRAND)).toString(), ",");
					slBrandValue.sort();
					strClaimName = mpInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_CLAIMNAME_RTE)).toString();
					if("Disclaimers".equalsIgnoreCase(strTabName))
					{
						strClaimName = mpInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_DISCLAIMERNAME_RTE)).toString();
					}
					strClaimName = BusinessUtil.isNotNullOrEmpty(strClaimName)?strClaimName:CONST_WHITE_SPACE;
					strBrand = slBrandValue.toString().replaceAll("[\\[\\]]", "");
					strBrand = BusinessUtil.isNotNullOrEmpty(strBrand)?strBrand:CONST_WHITE_SPACE;
					strName = (String) mpInfo.get(DomainConstants.SELECT_NAME) ;
					strRev = (String) mpInfo.get(DomainConstants.SELECT_REVISION) ;

					//If the above Brand And Claim Value combination exist in the context claim map consolidate the result and finally throw warning message to user
					for (Map.Entry<String,String> entry : mpUpdatedClaimsInfo.entrySet()) {
						strClaimNameRev = entry.getKey();
						strCtxtClaimName = (strClaimNameRev.split("::"))[0];
						strCtxtClaimRev = (strClaimNameRev.split("::"))[1];
						if(!strCtxtClaimName.equals(strName) && 
								entry.getValue().equalsIgnoreCase(new StringBuilder(strBrand).append("~~").append(strClaimName).toString()))
						{
							duplicateClaimsFound = true;
							sbTableBuilder.append("<tr><td>").append(strCtxtClaimName).append(" ").append(strCtxtClaimRev).append("</td>");
							sbTableBuilder.append("<td>").append(strBrand).append("</td><td>").append(strClaimName).append("</td><td>").append(strName);
							sbTableBuilder.append(" ").append(strRev).append("</td></tr>");
						}
					}
				}
			}
		} catch (FrameworkException fme) {
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, fme.getMessage());
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, fme.getMessage());
		}
		//end html tags
		sbTableBuilder.append("</table></body></html>");
		if(duplicateClaimsFound) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, sbTableBuilder.toString());
		} else {
			jsonReturnObj.add(STRING_STATUS, STATUS_INFO);
			jsonReturnObj.add(STRING_MESSAGE, MessageUtil.getMessage(context, null, "emxCPN.ClaimManager.NoDuplicateClaims", new String[]{strTabName}, null,context.getLocale(), "emxCPNStringResource"));
		}
		return jsonReturnObj.build().toString();
	}
	/**
	 * Method taken from com.pg.widgets.Route.PGRoute.java
	 * Get all complete tasks and update task details
	 * @param context enovia context object
	 * @param theMepAttributesMap map with details of the MEP
	 * @return name of the completed task
	 * @throws Exception when operation fails
	 */
	public static String getCompleteTask(Context context,Map<?, ?> theMepAttributesMap) throws Exception{
		HashMap<?,?> programMap = (HashMap <?,?>)theMepAttributesMap;

		String sgetCommentsFromTaskId=(String) programMap.get(GET_COMMENT_FROM_TASK_ID);
		boolean getCommentsFromTaskId=false;
		if(TRUE_VALUE.equals(sgetCommentsFromTaskId)) {
			getCommentsFromTaskId=true;
		}

		String taskId = (String) programMap.get(TASK_ID_KEY);	
		String routeId=(String) programMap.get(JSON_OUTPUT_KEY_NOT_ROUTE_ID);
		String taskStatus=(String) programMap.get(JSON_OUTPUT_KEY_TASK_STATUS);
		String flag = (String) programMap.get(JSON_OUTPUT_KEY_FLAG);
		String showFDA = (String) programMap.get(SHOWFDA);
		String comments=(String) programMap.get(COMMENTS);		
		String reviewcomments="";
		String strStatus = FAILURE;
		//Added for fetching Route promote failure errors - START
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		//Added for fetching Route promote failure errors - END

		try{

			HashMap<Object , Object> hmParam = new HashMap<>();
			String i18NReadAndUnderstand =EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", context.getLocale(),
					"emxFramework.UserAuthentication.ReadAndUnderstand");
			String strApprovalStatusAttr = PropertyUtil.getSchemaProperty(context, "attribute_ApprovalStatus" );			
			String strLanguage = context.getSession().getLanguage();
			Locale strLocale = context.getLocale();

			Route route = (Route)DomainObject.newInstance(context,  routeId);
			com.matrixone.apps.common.Person person = new com.matrixone.apps.common.Person();
			DomainObject task = DomainObject.newInstance(context,  taskId);

			if(getCommentsFromTaskId && (comments == null || "".equals(comments))) {
				comments = task.getInfo(context, "attribute[" + DomainConstants.ATTRIBUTE_COMMENTS + "]");
			}

			String attributeBracket = "attribute[";
			String closeBracket = "]";
			DomainRelationship domainRel            = null;
			String attrReviewCommentsNeeded         = PropertyUtil.getSchemaProperty(context, "attribute_ReviewCommentsNeeded");
			String sAttParallelNodeProcessonRule    = PropertyUtil.getSchemaProperty(context, "attribute_ParallelNodeProcessionRule");
			String attrTaskCommentsNeeded   = PropertyUtil.getSchemaProperty(context, "attribute_TaskCommentsNeeded");
			String strRouteActionAttr         = PropertyUtil.getSchemaProperty(context, "attribute_RouteAction" );
			String SELECT_ROUTE_ACTION          = "attribute[" + strRouteActionAttr + "]";
			boolean bTaskCommentGiven               = false;
			boolean returnBack                      = false;
			boolean isProjectSpace                  = false;

			String routeTime                        = "";
			String taskScheduledDate  = "";

			StringBuilder sAttrComments = new StringBuilder(attributeBracket);
			sAttrComments.append(DomainConstants.ATTRIBUTE_COMMENTS);
			sAttrComments.append(closeBracket);
			StringBuilder sAttrReviewTask =new StringBuilder(attributeBracket);
			sAttrReviewTask.append(DomainConstants.ATTRIBUTE_REVIEW_TASK);
			sAttrReviewTask.append(closeBracket);
			StringBuilder sAttrRouteNodeId =new StringBuilder(attributeBracket);
			sAttrRouteNodeId.append(DomainConstants.ATTRIBUTE_ROUTE_NODE_ID);
			sAttrRouteNodeId.append(closeBracket);
			StringBuilder sAttrApprovalStatus =new StringBuilder(attributeBracket);
			sAttrApprovalStatus.append(DomainConstants.ATTRIBUTE_APPROVAL_STATUS);
			sAttrApprovalStatus.append(closeBracket);
			StringBuilder sAttrScheduledCompletionDate =new StringBuilder(attributeBracket);
			sAttrScheduledCompletionDate.append(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE );
			sAttrScheduledCompletionDate.append(closeBracket);

			StringList selectStmt = new StringList(7);
			selectStmt.addElement(sAttrComments.toString());
			selectStmt.addElement(sAttrReviewTask.toString());
			selectStmt.addElement(sAttrRouteNodeId.toString());
			selectStmt.addElement(sAttrApprovalStatus.toString());
			selectStmt.addElement(sAttrScheduledCompletionDate.toString());
			selectStmt.addElement("from["+DomainConstants.RELATIONSHIP_ROUTE_TASK+"].to.to["+DomainConstants.RELATIONSHIP_ROUTE_SCOPE+"].from.id");
			selectStmt.addElement(SELECT_ROUTE_ACTION);

			person=com.matrixone.apps.common.Person.getPerson(context);

			String personName = person.getInfo(context,DomainConstants.SELECT_NAME);

			AttributeList attrList                  = new AttributeList();
			Map<?, ?> taskInfoMap           = task.getInfo(context, selectStmt);
			String taskComments       = (String)taskInfoMap.get(sAttrComments.toString());
			taskScheduledDate                = (String)taskInfoMap.get(sAttrScheduledCompletionDate.toString());
			//taskInfoMap.get(SELECT_ROUTE_ACTION);
			EnoviaResourceBundle.getProperty(context,"emxComponents.Routes.EnforceAssigneeApprovalComments");

			if(comments !=null && (!"".equals(comments.trim()))){
				String oldComment=task.getInfo(context,"attribute["+DomainConstants.ATTRIBUTE_COMMENTS+"]");
				if(!oldComment.equals(comments)){
					bTaskCommentGiven =true;
				}
			}else{
				bTaskCommentGiven =false;
				comments="";
			}

			if(!returnBack)
			{
				String treeMenu = "";
				boolean bIsTransActive = ContextUtil.isTransactionActive(context);
				try{
					if(!bIsTransActive) {
						ContextUtil.startTransaction(context, true);
						bIsTransActive = true;
					}

					String strDateTime              = "";

					if(UIUtil.isNotNullAndNotEmpty(taskScheduledDate)){
						strDateTime  = taskScheduledDate;
					}
					String routeNodeId        = (String)taskInfoMap.get(sAttrRouteNodeId.toString());
					//Get the correct relId for the RouteNodeRel given the attr routeNodeId from the InboxTask.
					routeNodeId = route.getRouteNodeRelId(context, routeNodeId);
					String reviewTask         = (String)taskInfoMap.get(sAttrReviewTask.toString());

					if(bTaskCommentGiven){
						task.setAttributeValue(context,attrTaskCommentsNeeded, NO_VALUE);
					} else {
						task.setAttributeValue(context,attrTaskCommentsNeeded, YES_VALUE);
					}

					// Setting the attributes to the task object
					if (!"".equals(taskStatus)){
						attrList.addElement(new Attribute(new AttributeType(strApprovalStatusAttr), taskStatus));
					}
					if(!"".equals(strDateTime)){
						attrList.addElement(new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE), strDateTime));
					}
					attrList.addElement(new Attribute(new AttributeType(DomainConstants.ATTRIBUTE_COMMENTS), comments));
					task.setAttributes(context,attrList);

					treeMenu = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"eServiceComponents.treeMenu.InboxTask");	
					if(  treeMenu  != null && !"null".equals( treeMenu  ) && !"".equals( treeMenu )) {
						MailUtil.setTreeMenuName(context, treeMenu );
					}

					// promote task object
					if (YES_VALUE.equalsIgnoreCase(reviewTask)){
						task.promote(context);
						AttributeList attrList1 = new AttributeList();
						attrList1.addElement(new Attribute(new AttributeType(attrReviewCommentsNeeded), "Yes"));
						task.setAttributes(context,attrList1);

						try{
							domainRel             = DomainRelationship.newInstance(context ,routeNodeId);
							Map<String, String> attrMap           = new HashMap<>();
							attrMap.put(attrReviewCommentsNeeded,YES_VALUE);
							Route.modifyRouteNodeAttributes(context, routeNodeId, attrMap);
						}catch(Exception ex){
							//ex.printStackTrace();
							throw ex;
						}

					}else{
						// do all operations for delta due-date offsets in this loop
						try {
							StringBuilder sAttrDueDateOffset =new StringBuilder(attributeBracket);
							sAttrDueDateOffset.append(DomainConstants.ATTRIBUTE_DUEDATE_OFFSET);
							sAttrDueDateOffset.append(closeBracket);
							StringBuilder sAttrDueDateOffsetFrom =new StringBuilder(attributeBracket);
							sAttrDueDateOffsetFrom.append(DomainConstants.ATTRIBUTE_DATE_OFFSET_FROM);
							sAttrDueDateOffsetFrom.append(closeBracket);
							StringBuilder sAttrSequence =new StringBuilder(attributeBracket);
							sAttrSequence.append(DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
							sAttrSequence.append(closeBracket);
							String selState               = DomainConstants.SELECT_CURRENT;  // get state

							StringList relSelects            = new StringList();

							StringBuilder sWhereExp           = new StringBuilder();
							int nextTaskSeq                  = 0;
							int currTaskSeq                  = 0;
							domainRel                     = DomainRelationship.newInstance(context ,routeNodeId);
							String currTaskSeqStr         = domainRel.getAttributeValue(context, DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
							String parallelType                  = domainRel.getAttributeValue(context, sAttParallelNodeProcessonRule);

							// get current / next task sequence
							if(UIUtil.isNotNullAndNotEmpty(currTaskSeqStr)){
								currTaskSeq = Integer.parseInt(currTaskSeqStr);
								nextTaskSeq = currTaskSeq+1;
							}
							relSelects.addElement(sAttrDueDateOffset.toString());
							relSelects.addElement(sAttrDueDateOffsetFrom.toString());
							relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
							sWhereExp.append("(");
							sWhereExp.append(selState);
							sWhereExp.append(" != \"");
							sWhereExp.append(DomainConstants.STATE_INBOX_TASK_COMPLETE);
							sWhereExp.append("\")");
							sWhereExp.append(" && (");
							sWhereExp.append(sAttrSequence.toString());
							sWhereExp.append(" == \"");
							sWhereExp.append(String.valueOf(currTaskSeq));
							sWhereExp.append("\")");
							// finds all same order tasks which are not yet complete; if any such exists, next offset task due-date is not set till all complete
							boolean shouldSetNextTaskDueDate = shouldSetNextTaskDueDate(context, route,currTaskSeq+"");
							boolean completeAny              = true;
							if(ALL_VALUE.equals(parallelType)){
								completeAny = false;
							}
							// can proceed to set offset due-date for next task
							// only if Parallel node rule is 'Any' or corresponding flag got is true
							if(completeAny || shouldSetNextTaskDueDate){
								// where clause filters to next order tasks offset from this task complete
								sWhereExp = new StringBuilder();
								sWhereExp.append("(");
								sWhereExp.append(sAttrDueDateOffset.toString());
								sWhereExp.append(" !~~ \"\")");
								sWhereExp.append(" && (");
								sWhereExp.append(sAttrDueDateOffsetFrom.toString());
								sWhereExp.append(" ~~ \"");
								sWhereExp.append(OFFSET_FROM_TASK_CREATE_DATE);
								sWhereExp.append("\")");
								sWhereExp.append(" && (");
								sWhereExp.append(sAttrSequence.toString());
								sWhereExp.append(" == \"");
								sWhereExp.append(String.valueOf(nextTaskSeq));
								sWhereExp.append("\")");

								MapList nextOrderOffsetList = getNextOrderOffsetTasks(context, route, relSelects, sWhereExp.toString());


								Route.setDueDatesFromOffset(context,nextOrderOffsetList);
							}
						}catch(Exception e) {
							logger.log(Level.SEVERE,e.getMessage(),e);	
						}

						MapList subRouteList=route.getAllSubRoutes(context);

						try {

							task.promote(context);
							{
								String isFDAEnabled = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.EnableFDA");
								if(UIUtil.isNotNullAndNotEmpty(isFDAEnabled) && isFDAEnabled.equals(JSON_OUTPUT_KEY_TRUE)) {
									String strRouteTaskUser = task.getAttributeValue(context,DomainConstants.ATTRIBUTE_ROUTE_TASK_USER);
									String isResponsibleRoleEnabled = DomainConstants.EMPTY_STRING;
									try {
										isResponsibleRoleEnabled = EnoviaResourceBundle.getProperty(context,"emxFramework.Routes.ResponsibleRoleForSignatureMeaning.Preserve");
									} catch(Exception e) {
										isResponsibleRoleEnabled = JSON_OUTPUT_KEY_FALSE;
										logger.log(Level.SEVERE,e.getMessage(),e);	
									}
									if(UIUtil.isNotNullAndNotEmpty(isResponsibleRoleEnabled) && isResponsibleRoleEnabled.equalsIgnoreCase("true") && UIUtil.isNotNullAndNotEmpty(strRouteTaskUser) && strRouteTaskUser.startsWith("role_")) {
										i18NReadAndUnderstand = MessageUtil.getMessage(context, null, "emxFramework.UserAuthentication.ReadAndUnderstandRole", new String[] {
												PropertyUtil.getSchemaProperty(context, strRouteTaskUser)}, null, context.getLocale(),
												"emxFrameworkStringResource");
										if (taskStatus.equalsIgnoreCase(TASK_APPROVAL_STATUS_APPROVE)){
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,APPROVE,i18NReadAndUnderstand);
										} else {
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,taskStatus,i18NReadAndUnderstand);
										}
									} else {
										if (taskStatus.equalsIgnoreCase(TASK_APPROVAL_STATUS_APPROVE)) {
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,APPROVE,i18NReadAndUnderstand);
										} else {
											MqlUtil.mqlCommand(context, MQL_ADD_HISTORY_QUERY,false, taskId,taskStatus,i18NReadAndUnderstand);
										}
									}
								}
							}


							// Added to notify to the subscribed person, if Task completion event is subscribed -start
							String taskState = task.getInfo(context, DomainConstants.SELECT_CURRENT);

							if(Route.STATE_ROUTE_COMPLETE.equals(taskState)) {
								try {
									SubscriptionManager subscriptionMgr = route.getSubscriptionManager();
									subscriptionMgr.publishEvent(context, Route.EVENT_TASK_COMPLETED, task.getId(context));
								} catch(Exception e) {
									logger.log(Level.SEVERE,e.getMessage(),e);	
								}
							}
							String sSubject =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.common.TaskDeletionNotice");		
							String sMessage1 =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.common.TaskDeletionMessage3");		
							String sMessage2 =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.common.TaskDeletionMessage2");		

							String sMessage=sMessage1+CONST_WHITE_SPACE+task.getName()+CONST_WHITE_SPACE+sMessage2;
							Route.deleteOrphanSubRoutes(context,subRouteList,sMessage,sSubject);
							// set Inbox Task title for auto-namer tasks
							boolean bContextPushed = false;
							try{

								ContextUtil.pushContext(context);
								bContextPushed = true;
								InboxTask.setTaskTitle(context, routeId);
							}catch(Exception e){
								logger.log(Level.SEVERE,e.getMessage(),e);	
							}
							finally {
								if(bContextPushed)
									ContextUtil.popContext(context);
							}

							if (!"".equals(routeId)) {
								treeMenu = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"eServiceComponents.treeMenu.Route");	
								if(  treeMenu  != null && !"null".equals( treeMenu  ) && !"".equals( treeMenu )) {
									MailUtil.setTreeMenuName(context, treeMenu );
								}
								StringList stringlist = new StringList(10);
								stringlist.clear();
								stringlist.add(DomainConstants.SELECT_ID);
								stringlist.add(DomainConstants.SELECT_TYPE);
								stringlist.add(GRANTOR);
								stringlist.add(GRANTEE);
								stringlist.add(GRANTEEACCESS);

								DomainObject domainobject1 = DomainObject.newInstance(context, routeId);

								MapList maplist = domainobject1.getRelatedObjects(context,  //Context context
										DomainConstants.RELATIONSHIP_OBJECT_ROUTE, // String relationshipPattern
										"*",  // String typePattern
										stringlist, //StringList objectSelects
										DomainConstants.EMPTY_STRINGLIST, // StringList relationshipSelects
										true, // boolean getTo
										false, // boolean getFrom
										(short)1, // short recurseToLevel
										DomainConstants.EMPTY_STRING, //String objectWhere
										DomainConstants.EMPTY_STRING,// String relationshipWhere
										0);//  int limit
								Map<?, ?> map1 = domainobject1.getInfo(context, stringlist);
								maplist.add(0, map1);
								Iterator<Map<?, ?>> iterator = maplist.iterator();
								while(iterator.hasNext()){
									Map<?, ?> map2 = iterator.next();
									Object obj1 = null;
									Object obj2 = null;
									Object obj3 = null;
									Object obj4 = map2.get(GRANTOR);
									if((obj4 instanceof String) || obj4 == null){
										if(obj4 == null || "".equals(obj4))
											continue;
										obj1 = new ArrayList<Object>(1);
										obj2 = new ArrayList<Object>(1);
										obj3 = new ArrayList<Object>(1);
										((java.util.List) (obj1)).add(obj4);
										((java.util.List) (obj2)).add(map2.get(GRANTEE));
										((java.util.List) (obj3)).add(map2.get(GRANTEEACCESS));
									}else{
										obj1 = (java.util.List)map2.get(GRANTOR);
										obj2 = (java.util.List)map2.get(GRANTEE);
										obj3 = (java.util.List)map2.get(GRANTEEACCESS);
									}

									String objId = (String)map2.get("id");
									BusinessObject doc=new BusinessObject(objId);
									doc.open(context);
									if (doc.getTypeName().equals(DomainConstants.TYPE_DOCUMENT)){

										for(int i = 0; i < ((java.util.List) (obj1)).size(); i++) {
											String strGrantee = (String)((java.util.List) (obj2)).get(i);

											if(strGrantee.equals(personName)) {
												String strGrantor = (String)((java.util.List) (obj1)).get(i);

												if(strGrantor.equals(DomainConstants.PERSON_ROUTE_DELEGATION_GRANTOR)) {
													boolean isCntxPushed=false;
													try {

														ContextUtil.pushContext(context, DomainConstants.PERSON_ROUTE_DELEGATION_GRANTOR, null, context.getVault().getName());
														isCntxPushed = true;
														MqlUtil.mqlCommand(context, MQL_REVOKE_GRANTOR_GRANTEE,false, objId,strGrantor,personName);

													} catch(Exception exception4){
														throw new FrameworkException(exception4);
													}
													finally{
														if(isCntxPushed) {
															ContextUtil.popContext(context);
														}
													}
												}
											}
										}
									}
									doc.close(context);
								}
							}
						} catch(Exception ex)
						{
							logger.log(Level.SEVERE,ex.getMessage(),ex);
							//Added for fetching Route promote failure errors - START
							ClientTaskList listNotices 	= context.getClientTasks();	
							ClientTaskItr itrNotices 	= new ClientTaskItr(listNotices);
							String message = "";
							while (itrNotices.next()) {
								ClientTask clientTaskMessage =  itrNotices.obj();
								String emxMessage = clientTaskMessage.getTaskData();
								if(UIUtil.isNotNullAndNotEmpty(emxMessage)){
									message =emxMessage +":" + message;
								}
							}
							if(bIsTransActive) {
								ContextUtil.abortTransaction(context); 
								bIsTransActive = false;
							}
							jsonReturnObj.add(STRING_STATUS, strStatus);
							jsonReturnObj.add(STRING_MESSAGE,message);
							return jsonReturnObj.build().toString();
							//Added for fetching Route promote failure errors - END
						}

					}  
					if(bIsTransActive) {
						ContextUtil.commitTransaction(context); 
						bIsTransActive = false;
					}
				}catch(Exception e){
					String strPromoteConnectedError =EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"emxComponents.Common.PromoteConnectObjectFailed");
					ClientTaskList listNotices 	= context.getClientTasks();	
					ClientTaskItr itrNotices 	= new ClientTaskItr(listNotices);
					String message = "";
					boolean isPromoteConnectObject = false;
					while (itrNotices.next()) {
						ClientTask clientTaskMessage =  itrNotices.obj();
						String emxMessage = clientTaskMessage.getTaskData();
						if(UIUtil.isNotNullAndNotEmpty(emxMessage) && strPromoteConnectedError.equalsIgnoreCase(emxMessage.trim())){
							isPromoteConnectObject = true;
							message =emxMessage +":" + message;
						}else {
							message = message + emxMessage;  					
						}
					}
					if(isPromoteConnectObject){
						context.clearClientTasks();
						MqlUtil.mqlCommand(context, "notice $1",message);
					}
					if(bIsTransActive) {
						ContextUtil.abortTransaction(context);
						bIsTransActive = false;
					}
				}

				String routeState = route.getInfo(context,DomainConstants.SELECT_CURRENT);

				if(routeState != null && ACTION_COMPLETE.equals(routeState)){

					treeMenu = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource",strLocale,"eServiceComponents.treeMenu.Route");

					boolean validTreeMenu=false;
					if(  treeMenu  != null && !"null".equals( treeMenu  ) && !"".equals( treeMenu )){
						validTreeMenu=true;
					}
					// PMC Bug 313582:java.lang.ClassCastException - While completing the Route task
					// Added to get the "type" of the "from" end object(Workspace Vault) connected through "Route Scope" relationship as "Super User" since the context user does not have access
					boolean isContextPushed = false ;
					String sScopeObjType;
					String sWorkspaceId = "";
					try {
						ContextUtil.pushContext(context);
						isContextPushed = true;
						sScopeObjType = route.getInfo(context,"to["+route.RELATIONSHIP_ROUTE_SCOPE+"].from.type");
					}finally {
						if(isContextPushed) {
							ContextUtil.popContext(context);
							isContextPushed = false;
						}	
					}
					// PMC Bug 313582 Ends here

					//To get the Workspace object to publish the corresponding subscribed events
					if(DomainConstants.TYPE_PROJECT_VAULT.equals(sScopeObjType)) {
						try {
							if(!isContextPushed) {
								ContextUtil.pushContext(context);
								isContextPushed = true;
							}
							String sWorkspaceVaultId = route.getInfo(context,"to["+DomainConstants.RELATIONSHIP_ROUTE_SCOPE+"].from.id");
							if(validTreeMenu) {
								MailUtil.setTreeMenuName(context, treeMenu );
							}

							sWorkspaceId = UserTask.getProjectId(context, sWorkspaceVaultId);
							if (sWorkspaceId!=null)
							{
								DomainObject domainObject = DomainObject.newInstance(context);
								domainObject.setId(sWorkspaceId);
								String sType=domainObject.getInfo(context,DomainConstants.SELECT_TYPE);
								if(sType.equals(DomainConstants.TYPE_PROJECT_SPACE) || mxType.isOfParentType(context,sType,DomainConstants.TYPE_PROJECT_SPACE)) //Modified for Sub type
									isProjectSpace=true;
							}
						}finally {
							if(isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
						}
					}else{
						try {
							if(!isContextPushed) {
								ContextUtil.pushContext(context);
								isContextPushed = true;
							}
							sWorkspaceId  = route.getInfo(context,"to["+DomainConstants.RELATIONSHIP_ROUTE_SCOPE+"].from.id");	
						}finally {
							if(isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
						}
					}
					if(( UIUtil.isNotNullAndNotEmpty(sWorkspaceId)) && !(isProjectSpace)) {
						try {
							ContextUtil.pushContext(context);
							isContextPushed = true;
							if(validTreeMenu){
								MailUtil.setTreeMenuName(context, treeMenu);
							}
						}finally {
							if(isContextPushed) {
								ContextUtil.popContext(context);
								isContextPushed = false;
							}
						}
					}
					StringList listContentIds = new StringList();
					try {
						ContextUtil.pushContext(context);
						isContextPushed = true;
						//To get list of documents attached to Route and publish Route Completed Event for the document
						listContentIds = route.getInfoList(context,"to["+DomainConstants.RELATIONSHIP_OBJECT_ROUTE+"].from.id");
					}finally {
						if(isContextPushed) {
							ContextUtil.popContext(context);
							isContextPushed = false;
						}

					}

					DomainObject document = DomainObject.newInstance(context,DomainConstants.TYPE_DOCUMENT);
					if(listContentIds == null){
						listContentIds = new StringList();
					}

					Iterator<?> contentItr = listContentIds.iterator();

					while(contentItr.hasNext()){

						String sDocId = (String)contentItr.next();

						document.setId(sDocId);

						if(validTreeMenu) {
							MailUtil.setTreeMenuName(context, treeMenu );
						}
					}
					if(validTreeMenu) {
						MailUtil.setTreeMenuName(context, treeMenu );
					}
				}
			}
			Boolean canComplete = false;
			/*if(flag != null && FLAG_FDA.equalsIgnoreCase(flag))
			{
				if(returnBack){
					hmParam.put(RETURN_BACK,returnBack);                   //give alert emxComponents.RejectComments.
				}else {
					canComplete =  true;
				}
			}else {*/
			if(returnBack){
				hmParam.put(RETURN_BACK,returnBack);                   //give alert emxComponents.RejectComments.Comments
			}else {
				canComplete =  true;
			}

			//}
			if(canComplete) {
				HashMap<String, Serializable> requestMap = new HashMap<String, Serializable>();
				requestMap.put(LANGUAGE_STR,strLanguage);
				requestMap.put(LOCALE_OBJ,strLocale);
				TimeZone tz = TimeZone.getTimeZone(context.getSession().getTimezone());
				Calendar cal = Calendar.getInstance(tz);
				requestMap.put(TIME_ZONE,String.valueOf(cal.DST_OFFSET));
				requestMap.put(OBJECT_ID,taskId);
				requestMap.put(COMMENTS,comments);

				requestMap.put(REVIEWER_COMMENTS,reviewcomments);
				java.util.Date date = new java.util.Date(taskScheduledDate);
				int intDateFormat = eMatrixDateFormat.getEMatrixDisplayDateFormat();
				java.text.DateFormat formatter = java.text.DateFormat.getDateInstance(intDateFormat, context.getLocale());
				taskScheduledDate =  formatter.format(date);
				requestMap.put(DUE_DATE,taskScheduledDate);
				routeTime =  "00:00:00 AM";
				requestMap.put(ROUTE_TIME,routeTime); 
				InboxTask inboxTaskObj  					= (InboxTask)DomainObject.newInstance(context, taskId);
				BusinessObjectAttributes boAttrGeneric = inboxTaskObj.getAttributes(context);
				AttributeItr attrItrGeneric   = new AttributeItr(boAttrGeneric.getAttributes());

				while (attrItrGeneric.next()) {
					Attribute attrGeneric = attrItrGeneric.obj();
					String strAttrName=attrGeneric.getName() ;					
					if (strAttrName.equals(DomainConstants.ATTRIBUTE_APPROVAL_STATUS) ) {
						requestMap.put(strAttrName,taskStatus); 
					}
					else if(strAttrName.equals(DomainConstants.ATTRIBUTE_SCHEDULED_COMPLETION_DATE)){
						requestMap.put(strAttrName,taskScheduledDate); 
					}
					else if(strAttrName.equals(DomainConstants.ATTRIBUTE_COMMENTS)) {
						//requestMap.put(strAttrName,taskComments);
						requestMap.put(strAttrName,comments);
					}					 
					else if(strAttrName.equals(reviewcomments)) {
						requestMap.put(strAttrName,reviewcomments); 
					}
					else
					{
						String sAttrValue = inboxTaskObj.getAttributeValue(context, strAttrName);
						requestMap.put(strAttrName,sAttrValue); 
					}
				}

				HashMap<String, HashMap<String, Serializable>> hmArguments = new HashMap<String, HashMap<String, Serializable>>();
				hmArguments.put(REQUEST_MAP,requestMap);
				try {
					if( !showFDA.equalsIgnoreCase(JSON_OUTPUT_KEY_TRUE))
					{
						JPO.invoke(context, "emxInboxTask", null, "updateTaskDetails", JPO.packArgs(hmArguments), HashMap.class);
					}

				}catch(Exception e){
					logger.log(Level.SEVERE,e.getMessage(),e);	
				}
				strStatus= SUCESS_VALUE;
				//Added for fetching Route promote failure errors - START
				jsonReturnObj.add(STRING_STATUS, strStatus);
				jsonReturnObj.add(STRING_MESSAGE, DomainConstants.EMPTY_STRING);
				//Added for fetching Route promote failure errors - END
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE,e.getMessage(),e); 
			//Added for fetching Route promote failure errors - START
			return jsonReturnObj.build().toString();
			//Added for fetching Route promote failure errors - END
		}
		//Added for fetching Route promote failure errors - START
		return jsonReturnObj.build().toString();
		//Added for fetching Route promote failure errors - END
	}
	/**
	 * Method taken from com.pg.widgets.Route.PGRoute.java
	 * metod to set due date in the next task
	 * @param context enovia context object
	 * @param route Route object
	 * @param currTaskSeq seq number of the next task
	 * @return true when due date must be set; false, otherwise
	 * @throws Exception when operation fails
	 */
	public static boolean shouldSetNextTaskDueDate(Context context, Route route, String currTaskSeq) throws Exception{

		boolean retVal =false;
		StringList objSelect=new StringList();
		objSelect.add(DomainConstants.SELECT_ID);
		objSelect.add("attribute["+DomainConstants.ATTRIBUTE_ROUTE_NODE_ID+"]");
		objSelect.add(DomainConstants.SELECT_CURRENT);


		MapList taskMapList = route.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_ROUTE_TASK, //String relPattern
				DomainConstants.TYPE_INBOX_TASK,         //String typePattern
				objSelect,                     //StringList objectSelects,
				null,                          //StringList relationshipSelects,
				true,                          //boolean getTo,
				false,                         //boolean getFrom,
				(short)1,                      //short recurseToLevel,
				"",                           //String objectWhere,
				"",                           //String relationshipWhere,
				0);                       //Map includeMap

		// for getting all Same sequence tasks which are incomplete
		MapList incompleteSameSeqMapList=new MapList();
		Iterator<?> it=taskMapList.iterator();
		String state = "";
		String relId = "";
		String currTaskSeqStr = "";
		DomainRelationship domRel = null;
		HashMap<?,?> hashTable ;
		while(it.hasNext()){
			hashTable=(HashMap<?,?>)it.next();
			state=(String)hashTable.get(DomainConstants.SELECT_CURRENT);
			relId=(String)hashTable.get("attribute["+DomainConstants.ATTRIBUTE_ROUTE_NODE_ID+"]");

			//Get the correct relId for the RouteNodeRel given the attr routeNodeId from the InboxTask.
			relId = route.getRouteNodeRelId(context, relId);

			domRel=DomainRelationship.newInstance(context,relId);
			currTaskSeqStr    = domRel.getAttributeValue(context, DomainConstants.ATTRIBUTE_ROUTE_SEQUENCE);
			if(currTaskSeq.equals(currTaskSeqStr) && !state.equalsIgnoreCase(DomainConstants.STATE_INBOX_TASK_COMPLETE)){
				incompleteSameSeqMapList.add(hashTable);
			}
		}
		if(incompleteSameSeqMapList.size()==1 ){
			retVal = true;
		}
		return retVal;
	}
	/**
	 * Method taken from com.pg.widgets.Route.PGRoute.java
	 * Get the task of the next order in the route
	 * @param context enovia context object
	 * @param route domain object of route 
	 * @param relSelects list of relationship select
	 * @param sWhereExp  where expression to get Related object 
	 * @return Maplist with details of tasks of the next order
	 * @throws Exception when operation fails
	 */
	public static MapList getNextOrderOffsetTasks(Context context, DomainObject route, StringList relSelects, String sWhereExp) throws Exception{

		return route.getRelatedObjects(context,
				DomainConstants.RELATIONSHIP_ROUTE_NODE, //String relPattern
				DomainConstants.QUERY_WILDCARD,  //String typePattern
				null,                         //StringList objectSelects,
				relSelects,                     //StringList relationshipSelects,
				false,                     //boolean getTo,
				true,                     //boolean getFrom,
				(short)1,                 //short recurseToLevel,
				"",                       //String objectWhere,
				sWhereExp,           //String relationshipWhere,
				0);                // int limit
	}

	/**This method is use to update Attribute on Object
	 * @param context
	 * @param map
	 * @return
	 * @throws MatrixException 
	 */
	public static String updateAttributesOnObject(Context context, Map<String,Object> map) throws MatrixException {
		JsonObjectBuilder jsonReturn = Json.createObjectBuilder();
		String strObjId = (String) map.remove("id");
		String strDescription = EMPTY_STRING;
		if(map.containsKey(DomainConstants.SELECT_DESCRIPTION))
		{
			strDescription = (String) map.remove(DomainConstants.SELECT_DESCRIPTION);
		}
		String strStatus = STATUS_INFO;
		try {
			DomainObject domObj = DomainObject.newInstance(context, strObjId);
			map = getValidAttributeMap(map);
			domObj.setAttributeValues(context, map);
			if(UIUtil.isNotNullAndNotEmpty(strDescription))
			{
				domObj.setDescription(context,strDescription);
			}
		} catch (FrameworkException ex) {
			logger.log(Level.SEVERE, "Exception in PGWidgetUtil", ex);
			strStatus = STATUS_ERROR;
		} finally {
			createErrorMessage(context, jsonReturn);
			jsonReturn.add(STRING_STATUS, strStatus);
		}
		return jsonReturn.build().toString();
	}
	//DCM (DS) 2022x-01 CW - REQ 45296 - The system shall allow to mass create the claims - START
	/**Main method  to Create the Claim and connects the related objects with the created Claim
	 * @param context
	 * @param mpRequestMap
	 * @return Response
	 * @throws MatrixException
	 */
	public Response massCreateClaim(Context context, Map<String,Object> mpRequestMap) throws MatrixException 
	{
		String strSourceId = (String) mpRequestMap.get("claimRequestId");
		ArrayList<String> alClaimList = (ArrayList<String>) mpRequestMap.get("claimList");
		//US- 1608 - Disclaimer Data Model changes - Create changes Start
		ArrayList<String> alDisclaimerList = (ArrayList<String>) mpRequestMap.get("disclaimerList");
		//US- 1608 - Disclaimer Data Model changes - Create changes End
		String strIsBackgroundJobEnable = (String) mpRequestMap.get("isBackgroundJobEnable");
		int iClaimCountForBackGroundJob = (int) mpRequestMap.get("claimCountForBackGroundJob");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		alClaimList.removeIf(Objects::isNull);
		try 
		{
			//if Claim count is greater than expected count then call background job for mass claim creation
			//US- 1608 - Disclaimer Data Model changes - Create changes Start
			if(((BusinessUtil.isNotNullOrEmpty(alClaimList) && alClaimList.size() > iClaimCountForBackGroundJob) || (BusinessUtil.isNotNullOrEmpty(alDisclaimerList) && alDisclaimerList.size() > iClaimCountForBackGroundJob)) && STRING_TRUE.equalsIgnoreCase(strIsBackgroundJobEnable))
			{
				//US- 1608 - Disclaimer Data Model changes - Create changes End
				// convert map to JSONObject to pass to request
				JSONObject jsonObj = new JSONObject(mpRequestMap);
				Map<String, Object> mapToBePassed = new HashMap<>();
				mapToBePassed.put("jsonObj", jsonObj.toString());
				mapToBePassed.put("claimRequestId", strSourceId);
				// backgroung job creation method call
				JPO.invoke(context, "pgWidgetClaimMangementUtil", null, "createBackgroundJobForClaimCreate", JPO.packArgs(mapToBePassed), Response.class);
				createErrorMessage(context, jsonReturnObj);
				jsonReturnObj.add(STRING_STATUS, STATUS_INFO);
			}
			//else direct call mass claim creation method
			else
			{
				massCreateClaimDisclaimer(context, mpRequestMap);

				jsonReturnObj.add(STRING_STATUS, STATUS_INFO);
				jsonReturnObj.add(STRING_MESSAGE,EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(),"emxCPN.Alert.DCMMassCreateClaimSuccess"));
			}
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			createErrorMessage(context, jsonReturnObj);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build();
	}

	/**Mass Claim and Disclaimer creation and connection logic
	 * @param context
	 * @param mpRequestMap
	 * @return String
	 * @throws MatrixException
	 */
	public String massCreateClaimDisclaimer(Context context, Map<String,Object> mpRequestMap) throws Exception 
	{
		//US- 1608 - Disclaimer Data Model changes - Create changes Start
		ArrayList<String> alClaimList = (ArrayList<String>) mpRequestMap.get("claimList");
		ArrayList<String> alDisclaimerList = (ArrayList<String>) mpRequestMap.get("disclaimerList");
		Map<String, Object> mpAttribute = (Map<String, Object>) mpRequestMap.get("attribute");

		try 
		{
			if(!alClaimList.isEmpty()) 
			{
				createClaimDisclaimer(context, alClaimList, mpRequestMap, TYPE_PG_CLAIM, ATTRIBUTE_CLAIMNAME_RTE, RELATIONSHIP_CLAIMS, POLICY_PG_CLAIM) ;
			}
			else if (mpAttribute.containsKey("pgClaimGraphicalImage") && UIUtil.isNotNullAndNotEmpty((String)mpAttribute.get("pgClaimGraphicalImage")))
			{
				String strSourceId = (String) mpRequestMap.get("claimRequestId");
				mpRequestMap.put("claimId", strSourceId);
				createClaim(context, mpRequestMap);
			}
			if(!alDisclaimerList.isEmpty()) 
			{
				createClaimDisclaimer(context, alDisclaimerList, mpRequestMap, TYPE_PG_DISCLAIMER, ATTRIBUTE_DISCLAIMERNAME_RTE, RELATIONSHIP_PG_DISCLAMER, POLICY_PG_DISCLAIMER) ;
			}
			//US- 1608 - Disclaimer Data Model changes - Create changes End
		}
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}

		return Response.status(HttpServletResponse.SC_OK).build().toString();
	}

	/** method added for US- 1608 - Disclaimer Data Model changes - Create changes
	 * Claim or Disclaimer creation and connection logic
	 * @param context
	 * @return void
	 * @throws MatrixException
	 */
	public void createClaimDisclaimer(Context context, ArrayList<String> alClaimDisclaimerList, Map<String,Object> mpRequestMap, String strObjectType, String strObjectName, String strRelName, String strPolicy) throws Exception 
	{
		String strCreateAndConnect = (String) mpRequestMap.get("createAndConnect");
		String strSourceId = (String) mpRequestMap.get("claimRequestId");
		Map<String, Object> mpAttribute = (Map<String, Object>) mpRequestMap.get("attribute");
		Map<String, Object> mpRelAttribute =  (Map<String, Object>) mpRequestMap.get("relAttribute");
		try {
			Iterator<String> iteratorClaimDisclaimer = alClaimDisclaimerList.iterator();
			//create mass claim and disclaimer logic
			while (iteratorClaimDisclaimer.hasNext()) 
			{
				String strClaimName = iteratorClaimDisclaimer.next();
				if (UIUtil.isNotNullAndNotEmpty(strClaimName)) 
				{
					String strClaimSymbolicName = FrameworkUtil.getAliasForAdmin(context, "Type", strObjectType, false);
					String strClaimObjGeneratorName = UICache.getObjectGenerator(context, strClaimSymbolicName, "");
					String strName = DomainObject.getAutoGeneratedName(context, strClaimObjGeneratorName, "");
					DomainObject domNewObj = DomainObject.newInstance(context);
					domNewObj.createObject(context, strObjectType, strName, FIRST_REV, strPolicy, VAULT_ESERVICE_PRODUCTION);
					mpAttribute.put(strObjectName, strClaimName);
					// Bug 5057 copy element for disclaimer should only set when execution type is Execution type 
					if(TYPE_PG_DISCLAIMER.equals(strObjectType) && mpRelAttribute.containsKey(ATTRIBUTE_EXECUTION_TYPE)  && ( returnStringListForObject(mpRelAttribute.get(ATTRIBUTE_EXECUTION_TYPE)).contains(CONST_PACKAGING_ARTWORK)))
					{
						mpAttribute.put(ATTRIBUTE_COPY_ELEMENT_TYPE, STRING_DISCLAIMER_MASTER_COPY);
					}
					updateClaimAttributes(context, mpAttribute, domNewObj);
					if (STRING_TRUE.equalsIgnoreCase(strCreateAndConnect)) 
					{
						DomainObject doParentObj = DomainObject.newInstance(context, strSourceId);
						if (doParentObj.isKindOf(context, TYPE_PG_CLAIM_REQUEST)) 
						{
							//DCM (DS) US 1597 Create Product Config object and set config attributes during create claim process. -START
							//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
							Map<String, Object> mapMethodParameters = new HashMap<>();
							mapMethodParameters.put("doParentObj", doParentObj);
							mapMethodParameters.put("doNewObj", domNewObj);
							mapMethodParameters.put("strRelName", strRelName);
							mapMethodParameters.put("strType", strObjectType);
							mapMethodParameters.put("strName", strName);
							mapMethodParameters.put("bCopyIPClass", true);
							mapMethodParameters.put("mpRelAttributes", mpRelAttribute);
							String strRelId = connectAfterCreate(context, mapMethodParameters);
							createClaimProductConfiguration(context, strRelId,strSourceId, strObjectType, strName);
							//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
							//DCM (DS) US 1597 Create Product Config object and set config attributes during create claim process. -End
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			throw new Exception(e.getMessage());
		}
	}
	//DCM (DS) 2022x-01 CW - REQ 45296 - The system shall allow to mass create the claims - END

	/**
	 * This method modifies attribute values from map
	 * @param context
	 * @param mpRequestMap
	 * @param domNewObj
	 * @throws Exception
	 */
	public void updateClaimAttributes(Context context, Map<String, Object> mpAttributeMap, DomainObject domNewObj) throws Exception
	{
		if (null != mpAttributeMap) 
		{
			mpAttributeMap = getValidAttributeMap(mpAttributeMap);
			domNewObj.setAttributeValues(context, mpAttributeMap);

		}
	}
	//DCM (DS) 2022x-01 CW - REQ 45296 - The system shall allow to mass create the claims - END

	/**This Method create Attribute Map for conevert value to stringList for multivalue attribute.
	 * @param map
	 */
	public static Map<String, Object> getValidAttributeMap(Map<String,Object> map)
	{
		Map<String, Object> mpAttributeMap = new HashMap();
		Object obj;
		for (Map.Entry<String,Object> entry : map.entrySet()) 
		{
			obj = entry.getValue();
			if(obj instanceof List)
			{
				mpAttributeMap.put(entry.getKey(),StringList.asList((ArrayList<String>)obj)); 
			}
			else if(obj instanceof Boolean)
			{
				mpAttributeMap.put(entry.getKey(),(boolean)obj); 
			}
			else
			{
				mpAttributeMap.put(entry.getKey(),(String)obj); 
			}
		}

		return new TreeMap(mpAttributeMap);
	}

	/**
	 * DCM (DS) 2022x-01 CW - Added for Modify access check for Comment attribute update
	 * @param context
	 * @param mpAttributes
	 * @return
	 * @throws FrameworkException
	 */
	public static boolean hasCommentAttributes(Context context, Map mpAttributes) throws FrameworkException
	{
		boolean bHasCommentAttr = false;
		StringList slCommentAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.CommentAttributes"),CONSTANT_STRING_COMMA);

		for(String strCommentAttr : slCommentAttrList)
		{
			if((mpAttributes.containsKey(strCommentAttr)))
			{
				bHasCommentAttr = true;
				break;
			}
		}
		return bHasCommentAttr;
	}

	/**
	 * DCM (DS) 2022x-02 CW - REQ 46455 - The system shall allow the collaborator to mark their collaboration is complete
	 * @param context
	 * @param mpAttributes
	 * @return
	 * @throws FrameworkException
	 */
	public static boolean hasCollaborationAttributes(Context context, Map mpAttributes) throws FrameworkException
	{
		boolean bHasCollabAttr = false;
		StringList slCollabAttrList = StringUtil.split(EnoviaResourceBundle.getProperty(context, "pgDCM.Claim.CollaborationAttributes"),CONSTANT_STRING_COMMA);

		for(String strCollabAttr : slCollabAttrList)
		{
			if((mpAttributes.containsKey(strCollabAttr)))
			{
				bHasCollabAttr = true;
				break;
			}
		}
		return bHasCollabAttr;
	}

	/** Requirement 46151: The user shall have a graphical view of number of objects which are going to expired in next 90, 60 and 30 days on Claim, Claim support and Disclaimer types which are in Approved and Released states.
	 * @param context
	 * @param slObjectSelect 
	 * @return
	 * @throws FrameworkException 
	 */
	public Response getExpirationData(Context context, List slObjectSelect) throws FrameworkException {
		JsonObjectBuilder jsonObj = Json.createObjectBuilder();
		JsonObjectBuilder jsonFinalObj = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjCounter = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjData = Json.createObjectBuilder();
		jsonFinalObj.add(STRING_MESSAGE, STRING_KO);
		String contextuser = context.getUser();
		String typeObj = new StringBuffer(TYPE_PG_CLAIM).append(CONSTANT_STRING_COMMA).append(TYPE_PG_CLAIM_SUPPORT).append(CONSTANT_STRING_COMMA).append(TYPE_PG_DISCLAIMER).toString();
		String strWhereOwner = "(owner=="+contextuser+" || '*"+contextuser+"*'~~ownership)";
		String strWhere = strWhereOwner + " && " + "(current != " + STATE_OBSOLETE + ") && "+ DomainObject.getAttributeSelect(ATTRIBUTE_EXPIRATION_DATE)+" != ''";
		slObjectSelect.add(DomainConstants.SELECT_TYPE);
		slObjectSelect.add(DomainConstants.SELECT_ID);
		slObjectSelect.add(DomainConstants.SELECT_REVISION);
		slObjectSelect.add(SELECT_TO_PG_DISCLAIMER_FROM_REVISION);
		slObjectSelect.add(SELECT_TO_PG_DISCLAIMER_FROM_ID);
		slObjectSelect.add(SELECT_TO_PG_DISCLAIMER_FROM_CURRENT);
		StringList slObj = new StringList(slObjectSelect);
		MapList mlObjs = DomainObject.findObjects(context,	// eMatrix context
				typeObj,							// type pattern
				VAULT_ESERVICE_PRODUCTION,			// Vault Pattern
				strWhere,						// where expression
				slObj);
		Map<String, Object> mpData;
		String strExpiry;
		Date dateExpiry;
		long diff;
		Date today = new Date();
		int dueDateDuration;
		int iCLMS90 = 0;
		int iCLMS60 = 0 ;
		int iCLMS30 = 0;
		int iCLM90 = 0;
		int iCLM60 = 0;
		int iCLM30 = 0;
		int iCLMD90 = 0;
		int iCLMD60 = 0;
		int iCLMD30 = 0;
		JsonArrayBuilder arrCLMS90 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLMS60 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLMS30 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLM90 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLM60 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLM30 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLMD90 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLMD60 = Json.createArrayBuilder();
		JsonArrayBuilder arrCLMD30 = Json.createArrayBuilder();
		JsonObjectBuilder jsonObjmpData = Json.createObjectBuilder();
		String strType;
		StringList slClaimID;
		StringList slClaimRevision;
		StringList slClaimName;
		StringList slClaimCurrent;
		for(int i=0;i<mlObjs.size();i++)
		{
			mpData = (Map) mlObjs.get(i);
			removeBellChar(mpData);		
			strExpiry = (String) mpData.get(DomainObject.getAttributeSelect(ATTRIBUTE_EXPIRATION_DATE));
			strType = (String) mpData.get(DomainConstants.SELECT_TYPE);
			dateExpiry = eMatrixDateFormat.getJavaDate(strExpiry);
			diff = dateExpiry.getTime() - today.getTime();
			dueDateDuration =   (int) (diff / (1000*60*60*24)); 
			mpData.put(DomainConstants.SELECT_NAME,new StringBuilder((String)mpData.get(DomainConstants.SELECT_NAME)).append(CONST_WHITE_SPACE).append((String)mpData.get(DomainConstants.SELECT_REVISION)).toString());
			if(mpData.containsKey(SELECT_TO_PG_DISCLAIMER_FROM_NAME))
			{
				slClaimID = returnStringListForObject(mpData.get(SELECT_TO_PG_DISCLAIMER_FROM_ID));
				slClaimRevision= returnStringListForObject(mpData.get(SELECT_TO_PG_DISCLAIMER_FROM_REVISION));
				slClaimName= returnStringListForObject(mpData.get(SELECT_TO_PG_DISCLAIMER_FROM_NAME));
				slClaimCurrent = returnStringListForObject(mpData.get(SELECT_TO_PG_DISCLAIMER_FROM_CURRENT));
				if(slClaimCurrent.contains(STRING_APPROVED) || slClaimCurrent.contains(STATE_RELEASED))
				{
					for(int j=0;j<slClaimID.size();j++)
					{
						if(STRING_APPROVED.equals(slClaimCurrent.get(j)) || STATE_RELEASED.equals(slClaimCurrent.get(j)))
						{
							mpData.put(SELECT_TO_PG_DISCLAIMER_FROM_NAME,new StringBuilder(slClaimID.get(j)).append(CONST_STR_PIPE).append(slClaimName.get(j)).append(CONST_WHITE_SPACE).append(slClaimRevision.get(j)).toString());
							break;
						}
					}
				} else {
					mpData.put(SELECT_TO_PG_DISCLAIMER_FROM_NAME,new StringBuilder(slClaimID.get(0)).append(CONST_STR_PIPE).append(slClaimName.get(0)).append(CONST_WHITE_SPACE).append(slClaimRevision.get(0)).toString());
				}
			}
			PGClaimModuler.map2JsonBuilder(jsonObjmpData, mpData);
			if(dueDateDuration <= 30 && dueDateDuration > 0)
			{
				if (TYPE_PG_CLAIM.equals(strType))
					iCLM30++;
				else if(strType.equals(TYPE_PG_DISCLAIMER))
					iCLMD30++;
				else							
					iCLMS30++;


				addDataInTypeArray(arrCLMS30, arrCLM30, arrCLMD30, jsonObjmpData, strType);
			}
			else if(dueDateDuration <= 60 && dueDateDuration > 30)
			{
				if (TYPE_PG_CLAIM.equals(strType))
					iCLM60++;
				else if (strType.equals(TYPE_PG_DISCLAIMER))
					iCLMD60++;
				else
					iCLMS60++;
				addDataInTypeArray(arrCLMS60, arrCLM60, arrCLMD60, jsonObjmpData, strType);
			}
			else if(dueDateDuration <= 90 && dueDateDuration > 60)
			{
				if (TYPE_PG_CLAIM.equals(strType))
					iCLM90++;
				else if (strType.equals(TYPE_PG_DISCLAIMER))
					iCLMD90++;
				else
					iCLMS90++;
				addDataInTypeArray(arrCLMS90, arrCLM90, arrCLMD90, jsonObjmpData, strType);
			}
		}

		JsonObjectBuilder jsonCLMSCounterObj = Json.createObjectBuilder();
		jsonCLMSCounterObj.add("90 Days", iCLMS90);
		jsonCLMSCounterObj.add("60 Days", iCLMS60);
		jsonCLMSCounterObj.add("30 Days", iCLMS30);
		JsonObjectBuilder jsonCLMCounterObj = Json.createObjectBuilder();
		jsonCLMCounterObj.add("90 Days", iCLM90);
		jsonCLMCounterObj.add("60 Days", iCLM60);
		jsonCLMCounterObj.add("30 Days", iCLM30);
		JsonObjectBuilder jsonCLMDCounterObj = Json.createObjectBuilder();
		jsonCLMDCounterObj.add("90 Days", iCLMD90);
		jsonCLMDCounterObj.add("60 Days", iCLMD60);
		jsonCLMDCounterObj.add("30 Days", iCLMD30);
		jsonObj.add(STRING_CLAIM, jsonCLMCounterObj);
		jsonObj.add(STRING_CLAIM_SUPPORT, jsonCLMSCounterObj);
		jsonObj.add(STRING_DISCLAIMER, jsonCLMDCounterObj);
		//Table Data
		JsonObjectBuilder jsonCLMSDataObj = Json.createObjectBuilder();
		jsonCLMSDataObj.add("90 Days", arrCLMS90);
		jsonCLMSDataObj.add("60 Days", arrCLMS60);
		jsonCLMSDataObj.add("30 Days", arrCLMS30);
		JsonObjectBuilder jsonCLMDataObj = Json.createObjectBuilder();
		jsonCLMDataObj.add("90 Days", arrCLM90);
		jsonCLMDataObj.add("60 Days", arrCLM60);
		jsonCLMDataObj.add("30 Days", arrCLM30);
		JsonObjectBuilder jsonCLMDDataObj = Json.createObjectBuilder();
		jsonCLMDDataObj.add("90 Days", arrCLMD90);
		jsonCLMDDataObj.add("60 Days", arrCLMD60);
		jsonCLMDDataObj.add("30 Days", arrCLMD30);
		jsonObjData.add(STRING_CLAIM, jsonCLMDataObj);
		jsonObjData.add(STRING_CLAIM_SUPPORT, jsonCLMSDataObj);
		jsonObjData.add(STRING_DISCLAIMER, jsonCLMDDataObj);
		//count
		jsonObjCounter.add(STRING_CLAIM, iCLM90 + iCLM60 + iCLM30);
		jsonObjCounter.add(STRING_CLAIM_SUPPORT, iCLMS90 + iCLMS60 + iCLMS30);
		jsonObjCounter.add(STRING_DISCLAIMER, iCLMD90 + iCLMD60 + iCLMD30);
		jsonFinalObj.add(STRING_DATA, jsonObj);
		jsonFinalObj.add("count", jsonObjCounter);
		jsonFinalObj.add("tableData", jsonObjData);
		jsonFinalObj.add(STRING_MESSAGE, STRING_OK);

		return Response.status(HttpServletResponse.SC_OK).entity(jsonFinalObj.build().toString()).build();
	}

	private void addDataInTypeArray(JsonArrayBuilder arrCLMS30, JsonArrayBuilder arrCLM30, JsonArrayBuilder arrCLMD30,
			JsonObjectBuilder jsonObjmpData, String strType) {
		if(TYPE_PG_CLAIM.equals(strType))
			arrCLM30.add(jsonObjmpData);
		else if (strType.equals(TYPE_PG_DISCLAIMER))
			arrCLMD30.add(jsonObjmpData);
		else
			arrCLMS30.add(jsonObjmpData);
	}

	/**REQ 46020 - Validate Indented market on CLR promotion
	 * This method returns CLR info for related claims
	 * @param context
	 * @param mpRequestMap
	 * @return
	 * @throws Exception 
	 */
	public Response getConnectedObjectsData(matrix.db.Context context, Map<String,Object> mpRequestMap ) throws Exception {
		String strObjectId = (String) mpRequestMap.get("objectId");
		String strSelect = (String) mpRequestMap.get("ObjSelectables");
		StringList slObj = new StringList();
		JsonArrayBuilder jsonArray = Json.createArrayBuilder();
		if(UIUtil.isNotNullAndNotEmpty(strSelect))
		{
			slObj = StringUtil.split(strSelect, CONSTANT_STRING_COMMA);
		}
		JsonObjectBuilder jsonClaimInfoReturn = Json.createObjectBuilder();
		StringList slObjId = StringUtil.split(strObjectId, CONSTANT_STRING_COMMA);
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {
			if(BusinessUtil.isNotNullOrEmpty(slObjId))
			{
				String strCLRId;
				for(int i=0; i<slObjId.size();i++ )
				{
					strCLRId = slObjId.get(i);
					Pattern relPattern = new Pattern(RELATIONSHIP_CLAIMS);
					relPattern.addPattern(RELATIONSHIP_PG_DISCLAMER);
					Pattern typePattern = new Pattern(TYPE_PG_CLAIM);
					typePattern.addPattern(TYPE_PG_DISCLAIMER);
					DomainObject domObj = DomainObject.newInstance(context, strCLRId);
					MapList mlObjs = domObj.getRelatedObjects(
							context, //context
							relPattern.getPattern(), //relPattern
							typePattern.getPattern(), //typePattern
							slObj, //busSelects
							null, //relSelects
							false, // getTo
							true, // getFrom
							(short) 2,  //recurseToLevel
							null, //busWhere
							DomainConstants.EMPTY_STRING, //relWhere
							0 //limit
							);	
					if(BusinessUtil.isNotNullOrEmpty(mlObjs))
					{
						PGClaimModuler.mapList2JsonArray(jsonArray, mlObjs);
						jsonClaimInfoReturn.add(strCLRId, jsonArray);
					}
				}
			}
			jsonReturnObj.add("data", jsonClaimInfoReturn);
			jsonReturnObj.add(STRING_MESSAGE, STRING_OK);
		} catch (FrameworkException e) {
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, e.getMessage());
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build(); 
	}

	//Added for Claim Request Image float issue during Claim revise - START
	/**
	 * This method is called on connect of new revision of Claim to CLR
	 * This will replace old revision with new revision Claim in pgClaimImage rel 
	 * @param context
	 * @param strCLMId
	 * @param strCLMNewRevId
	 * @param strParentCLRId
	 * @throws Exception
	 */
	public void floatClaimRequestImage(Context context, String strCLMId, String strCLMNewRevId, String strParentCLRId) throws Exception
	{
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strCLMId) && UIUtil.isNotNullAndNotEmpty(strCLMNewRevId) && UIUtil.isNotNullAndNotEmpty(strParentCLRId))
			{
				//PushContext to fetch connected CLRs even on No access Claims
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				DomainObject domCLMObj = DomainObject.newInstance(context, strCLMId);
				StringList slObjSelects = new StringList();
				slObjSelects.add(SELECT_CLAIMIMAGE_CLAIMREQUESTID);
				slObjSelects.add(SELECT_CLAIMIMAGE_RELID);

				Map mpCLMObjData = domCLMObj.getInfo(context, slObjSelects, slObjSelects);
				StringList slClmImgCLRIds = returnStringListForObject(mpCLMObjData.get(SELECT_CLAIMIMAGE_CLAIMREQUESTID));
				StringList slClmImgRelIds = returnStringListForObject(mpCLMObjData.get(SELECT_CLAIMIMAGE_RELID));

				if(BusinessUtil.isNotNullOrEmpty(slClmImgCLRIds) && slClmImgCLRIds.contains(strParentCLRId))
				{
					for(int i=0; i<slClmImgCLRIds.size(); i++)
					{
						if(strParentCLRId.equals(slClmImgCLRIds.get(i)))
						{
							//replace old revision with new revision Claim in pgClaimImage rel
							DomainRelationship.setToObject(context, slClmImgRelIds.get(i), DomainObject.newInstance(context, strCLMNewRevId));
						}
					}
				}
			}
		} 
		finally {
			ContextUtil.popContext(context);
		}
	}
	//Added for Claim Request Image float issue during Claim revise - END

	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - START
	/**
	 * This method validates Claim objects for any mismatch of attribute values with its Claim Support
	 * @param context
	 * @param mpRequestMap
	 * @return
	 */
	public String validateCLMSDataOnClaims(Context context, Map mpRequestMap) throws Exception
	{
		String strClaimRequestId = (String) mpRequestMap.get("objectId");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		try {			
			if(UIUtil.isNotNullAndNotEmpty(strClaimRequestId))
			{
				JsonObjectBuilder jsonObjMarkets = Json.createObjectBuilder();
				JsonObjectBuilder jsonObjBenefit = Json.createObjectBuilder();
				Map mpMismatchDetails = checkCLMSDataMismatch(context, strClaimRequestId);
				String strMarketsValMsg = (String) mpMismatchDetails.get("Markets");
				String strBenefitsValMsg = (String) mpMismatchDetails.get("Benefits");

				if(UIUtil.isNotNullAndNotEmpty(strMarketsValMsg) || UIUtil.isNotNullAndNotEmpty(strBenefitsValMsg))
				{
					jsonObjMarkets.add(STRING_STATUS, STATUS_ERROR);
					jsonObjBenefit.add(STRING_STATUS, STATUS_ERROR);
					jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
					jsonReturnObj.add(STRING_MESSAGE, "Claim Support Validation Has Failed");
				}
				else 
				{
					jsonObjMarkets.add(STRING_STATUS, STATUS_INFO);
					jsonObjBenefit.add(STRING_STATUS, STATUS_INFO);
					jsonReturnObj.add(STRING_STATUS, STATUS_INFO);
					jsonReturnObj.add(STRING_MESSAGE, "Claim Support Data Validation Passed");
				}
				jsonObjMarkets.add(STRING_MESSAGE, strMarketsValMsg);
				jsonObjBenefit.add(STRING_MESSAGE, strBenefitsValMsg);
				jsonReturnObj.add("INTENDED MARKETS", jsonObjMarkets);
				jsonReturnObj.add("BENEFIT", jsonObjBenefit);
			}
		}
		catch (FrameworkException fme) 
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, fme.getMessage());
			jsonReturnObj.add(STRING_STATUS, STATUS_ERROR);
			jsonReturnObj.add(STRING_MESSAGE, fme.getMessage());
		}

		return jsonReturnObj.build().toString();
	}

	/**
	 * @param context
	 * @param strClaimRequestId
	 * @return
	 * @throws Exception
	 */
	public Map checkCLMSDataMismatch(Context context, String strClaimRequestId) throws Exception 
	{
		boolean isContextPushed = false;
		HashMap mpMismatchDetails = new HashMap();
		try {
			StringBuilder sbMarketsValMsg = new StringBuilder();
			StringBuilder sbBenefitsValMsg = new StringBuilder();
			//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : start
			StringBuilder sbClaimIndentedMarket = new StringBuilder(FROMMID_PGCLAIMPRODUCTCONFIG_TO).append(DomainObject.getAttributeSelect(ATTRIBUTE_PG_INTENDED_MARKETS));
			//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : End
			StringList slBusSelects = new StringList(7);

			slBusSelects.add(DomainConstants.SELECT_TYPE);
			slBusSelects.add(DomainConstants.SELECT_NAME);
			slBusSelects.add(DomainConstants.SELECT_REVISION);
			slBusSelects.add(DomainConstants.SELECT_ID);
			//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : start
			StringList slRelSelects = new StringList();
			slRelSelects.add(sbClaimIndentedMarket.toString());
			slRelSelects.add(DomainObject.getAttributeSelect(ATTRIBUTE_PG_BENEFIT));
			//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : End
			slBusSelects.add(SELECT_FROM_PG_CLAIM_SUPPORT);
			slBusSelects.add(SELECT_FROM_PG_CLAIM_SUPPORT_TO);
			slBusSelects.add(new StringBuilder(SELECT_FROM_PG_CLAIM_SUPPORT_TO).append(".").append(DomainObject.getAttributeSelect(ATTRIBUTE_PG_INTENDED_MARKETS)).toString());
			slBusSelects.add(new StringBuilder(SELECT_FROM_PG_CLAIM_SUPPORT_TO).append(".").append(DomainObject.getAttributeSelect(ATTRIBUTE_PG_BENEFIT)).toString());
			Pattern relPattern = new Pattern(RELATIONSHIP_CLAIMS);
			Pattern typePattern = new Pattern(TYPE_PG_CLAIM);
			//Push context user to allow context user to fetch connected No access objects details
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context, PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;
			DomainObject doClaimRequest = DomainObject.newInstance(context, strClaimRequestId);
			MapList mlClaimInfo = doClaimRequest.getRelatedObjects(context, 
					relPattern.getPattern(), //relPattern
					typePattern.getPattern(), //typePattern
					slBusSelects, //busSelects
					//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : start
					slRelSelects, //relSelects
					//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : End
					false, // getTo
					true, // getFrom
					(short) 1,  //recurseToLevel
					null, //busWhere
					null, //relWhere
					0 //limit
					);
			if(BusinessUtil.isNotNullOrEmpty(mlClaimInfo))
			{
				Map mpClaimInfo;
				String strClaimName;
				String strClaimRevision;
				String strCLMSConnected;
				StringList slClaimMarkets;
				StringList slClaimBenefit;
				StringList slClaimSupports;
				StringList slCLMSMarkets;
				StringList slCLMSBenefits;
				StringList slMissingMarkets;
				StringList slMissingBenefits;
				String[] strMsgArgs = new String[3];
				for(Object objClaimInfo: mlClaimInfo) 
				{
					mpClaimInfo = (Map)objClaimInfo;
					strClaimName = mpClaimInfo.get(DomainConstants.SELECT_NAME).toString();
					strClaimRevision = mpClaimInfo.get(DomainConstants.SELECT_REVISION).toString();
					strCLMSConnected = mpClaimInfo.get(SELECT_FROM_PG_CLAIM_SUPPORT).toString();
					//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : start
					slClaimMarkets = returnStringListForObject(mpClaimInfo.get(sbClaimIndentedMarket.toString()));
					//Feature: 2165 US 1599: Claim Data Model changes - Trigger updates : End
					slClaimBenefit = returnStringListForObject(mpClaimInfo.get(DomainObject.getAttributeSelect(ATTRIBUTE_PG_BENEFIT)));
					slClaimSupports = returnStringListForObject(mpClaimInfo.get(SELECT_FROM_PG_CLAIM_SUPPORT_TO));
					slCLMSMarkets = returnStringListForObject(mpClaimInfo.get(new StringBuilder(SELECT_FROM_PG_CLAIM_SUPPORT_TO).append(".").append(DomainObject.getAttributeSelect(ATTRIBUTE_PG_INTENDED_MARKETS)).toString()));
					slCLMSBenefits = returnStringListForObject(mpClaimInfo.get(new StringBuilder(SELECT_FROM_PG_CLAIM_SUPPORT_TO).append(".").append(DomainObject.getAttributeSelect(ATTRIBUTE_PG_BENEFIT)).toString()));
					slClaimMarkets = removeEmptyValuesFromStringList(slClaimMarkets);
					slClaimBenefit = removeEmptyValuesFromStringList(slClaimBenefit);
					slCLMSMarkets = removeEmptyValuesFromStringList(slCLMSMarkets);
					slCLMSBenefits = removeEmptyValuesFromStringList(slCLMSBenefits);
					//Added to Resolve Production issue of comma separate value of markets and benefits - Start 
					slClaimMarkets= convertCommaSeparatedStringtoStringList(slClaimMarkets);
					slClaimBenefit = convertCommaSeparatedStringtoStringList(slClaimBenefit);
					slCLMSBenefits = convertCommaSeparatedStringtoStringList(slCLMSBenefits);
					slCLMSMarkets = convertCommaSeparatedStringtoStringList(slCLMSMarkets);
					//Added to Resolve Production issue of comma separate value of markets and benefits - End
					if(UIUtil.isNotNullAndNotEmpty(strCLMSConnected) && STRING_TRUE.equalsIgnoreCase(strCLMSConnected))
					{
						//Remove CLMS obj values in CLM list to find CLMS missing Market value
						slMissingMarkets = new StringList(slClaimMarkets);
						slMissingMarkets.removeAll(slCLMSMarkets);
						if(!slMissingMarkets.isEmpty())
						{
							strMsgArgs[0] = new StringBuilder("<b><i>").append(slMissingMarkets.toString().replaceAll("[\\[\\]]", DomainConstants.EMPTY_STRING)).append("</i></b>").toString();
							strMsgArgs[1] = new StringBuilder("<b>").append(strClaimName).append(" ").append(strClaimRevision).append("</b>").toString();
							strMsgArgs[2] = new StringBuilder("<b>").append(slClaimSupports.toString().replaceAll("[\\[\\]]", DomainConstants.EMPTY_STRING)).append("</b>").toString();
							sbMarketsValMsg.append("<li>").append(MessageUtil.getMessage(context, null, "emxCPN.ClaimManager.Message.MissingIntendedMarketsOnCLMS", strMsgArgs, null,context.getLocale(), "emxCPNStringResource")).append("</li>");
						}
						//Remove CLMS obj values in CLM list to find CLMS missing Benefits value
						slMissingBenefits = new StringList(slClaimBenefit);
						slMissingBenefits.removeAll(slCLMSBenefits);

						if(!slMissingBenefits.isEmpty())
						{
							strMsgArgs[0] = new StringBuilder("<b><i>").append(slMissingBenefits.toString().replaceAll("[\\[\\]]", DomainConstants.EMPTY_STRING)).append("</i></b>").toString();
							strMsgArgs[1] = new StringBuilder("<b>").append(strClaimName).append(" ").append(strClaimRevision).append("</b>").toString();
							strMsgArgs[2] = new StringBuilder("<b>").append(slClaimSupports.toString().replaceAll("[\\[\\]]", DomainConstants.EMPTY_STRING)).append("</b>").toString();
							sbBenefitsValMsg.append("<li>").append(MessageUtil.getMessage(context, null, "emxCPN.ClaimManager.Message.MissingBenefitsOnCLMS", strMsgArgs, null,context.getLocale(), "emxCPNStringResource")).append("</li>");
						}
					}
				}
			}
			String strMarketsValMsg = UIUtil.isNotNullAndNotEmpty(sbMarketsValMsg.toString()) ? new StringBuilder("<ul style=\"list-style-type:disc;\">").append(sbMarketsValMsg).append("</ul>").toString() : DomainConstants.EMPTY_STRING;
			String strBenefitsValMsg = UIUtil.isNotNullAndNotEmpty(sbBenefitsValMsg.toString()) ? new StringBuilder("<ul style=\"list-style-type:disc;\">").append(sbBenefitsValMsg).append("</ul>").toString() : DomainConstants.EMPTY_STRING;
			mpMismatchDetails.put("Markets", strMarketsValMsg);
			mpMismatchDetails.put("Benefits", strBenefitsValMsg);
		}  
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
		}
		finally {
			if(isContextPushed)
			{
				ContextUtil.popContext(context);
				isContextPushed = false;
			}
		}
		return mpMismatchDetails;
	}

	/**Added to Resolve Production issue of comma separate value of markets and benefits. 
	 * @param slObject
	 * @return
	 */
	private StringList convertCommaSeparatedStringtoStringList(StringList slObject)
	{
		StringList slFinalList = new StringList();
		if(BusinessUtil.isNotNullOrEmpty(slObject))
		{
			for(int i=0;i<slObject.size();i++)
			{
				slFinalList.addAll(slObject.get(i).split(","));
			}
		}
		return slFinalList;
	}

	/**
	 * Removes empty string and null values from the input StringList
	 * @param sl
	 * @return
	 */
	public StringList removeEmptyValuesFromStringList(StringList sl)
	{
		StringList returnStringList = new StringList();
		if(sl != null && !sl.isEmpty())
		{
			for(String value : sl)
			{
				if(UIUtil.isNotNullAndNotEmpty(value))
				{
					returnStringList.add(value);
				}
			}
		}
		return returnStringList;
	}
	//DCM(DS) 2022x-04 CW - Validation check across CLM and CLMS - END

	//DCM (DS) User Story 457 - Claim User/Originator - VIEW files instead of downloading them - START
	/**Method  to generate PDF files of existing checked in files
	 * @param context
	 * @param mpRequestMap
	 * @return Response
	 * @throws MatrixException
	 */
	public Response massGeneratePDF(Context context, Map<String,Object> mpRequestMap) throws MatrixException 
	{
		ArrayList<Map<String, String>> arrFileInfo = (ArrayList<Map<String, String>>) mpRequestMap.get("FileInfo");
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		StringBuilder sbFilenamesSuccess = new StringBuilder();
		StringBuilder sbFilenamesFail = new StringBuilder();
		int iResult = 0;
		String strStatus = STATUS_INFO;
		try 
		{
			Iterator<Map<String, String>> iteratorFileInfo = arrFileInfo.iterator();
			while (iteratorFileInfo.hasNext())
			{
				Map<String, String> mapFileInfo = iteratorFileInfo.next();
				String strObjectType = mapFileInfo.get(DomainConstants.SELECT_TYPE);
				String strFileName = mapFileInfo.get("fileName");
				String strObjectId = mapFileInfo.get(DomainConstants.SELECT_ID);
				iResult = createAndCheckinPDF(context, strObjectId, strFileName, strObjectType);
				if(iResult == 0)
				{
					sbFilenamesSuccess.append(strFileName).append(STRING_HTML_BREAK_LINE);
				}else if(iResult == 1)
				{
					sbFilenamesFail.append(strFileName).append(STRING_HTML_BREAK_LINE);
				}
			}
			if(sbFilenamesFail.getLength() > 0)
			{
				jsonReturnObj.add(STRING_MESSAGE, sbFilenamesFail.toString());
				strStatus = STATUS_ERROR;
			}else if(sbFilenamesSuccess.getLength() > 0)
			{
				jsonReturnObj.add(STRING_MESSAGE, sbFilenamesSuccess.toString());		        	
			}				
			jsonReturnObj.add(STRING_STATUS, strStatus);
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, EXCEPTION_MESSAGE, e);
			createErrorMessage(context, jsonReturnObj);
		}
		return Response.status(HttpServletResponse.SC_OK).entity(jsonReturnObj.build().toString()).build();
	}

	/**
	 * This method will convert document file to PDF and check-in to same object
	 * @param context
	 * @param strObjectId
	 * @param strFileName
	 * @param strObjectType
	 * @return
	 * @throws Exception
	 */
	public int createAndCheckinPDF(Context context, String strObjectId, String strFileName, String strObjectType) throws Exception
	{
		int iReturn = 0;
		String strCheckoutPath = DomainConstants.EMPTY_STRING;
		String strCheckinPath = null;
		String strPDFFileName = null;
		String strCheckoutPathWithFileName = null;
		DomainObject domObj;
		String strPolicyVersion = PropertyUtil.getSchemaProperty(context, "policy_Version");
		String strFormatAutogeneratedPDF = PropertyUtil.getSchemaProperty(context,"format_AutoGeneratedPDF");
		String fileSeparator = System.getProperty("file.separator");
		boolean isCtxtPushed = false;
		try {
			String strGenericFormat = PropertyUtil.getSchemaProperty(context, "format_generic");
			String strstrFileNameLowercase = DomainConstants.EMPTY_STRING;
			if(UIUtil.isNotNullAndNotEmpty(strFileName))
			{
				strstrFileNameLowercase = strFileName.toLowerCase();
			}

			if (UIUtil.isNotNullAndNotEmpty(strObjectId) && 
					(strstrFileNameLowercase.endsWith(".doc") || strstrFileNameLowercase.endsWith(".docx") || 
							strstrFileNameLowercase.endsWith(".txt") || strstrFileNameLowercase.endsWith(".ppt") || 
							strstrFileNameLowercase.endsWith(".pptx") || strstrFileNameLowercase.endsWith(".xls") || 
							strstrFileNameLowercase.endsWith(".xlsx")  || strstrFileNameLowercase.endsWith(".csv"))) {

				domObj = DomainObject.newInstance(context, strObjectId);
				strCheckoutPath = context.createWorkspace();
				Instant timestamp = Instant.now();
				long milliseconds = timestamp.toEpochMilli();
				strPDFFileName = strFileName + "." + milliseconds + ".pdf";
				strCheckinPath = strCheckoutPath + fileSeparator + strPDFFileName;
				strCheckoutPathWithFileName = strCheckoutPath + fileSeparator + strFileName;
				FileList fileList = new FileList();
				matrix.db.File file = new matrix.db.File(strFileName, strGenericFormat);
				fileList.add(file);
				domObj.checkoutFiles(context, false, strGenericFormat, fileList, strCheckoutPath);
				if (strstrFileNameLowercase.endsWith(".doc") || strstrFileNameLowercase.endsWith(".docx") || strstrFileNameLowercase.endsWith(".txt")) {
					//Setting license
					GLSAsposeLicense.checkWordLicense();
					Document doc = new Document(strCheckoutPathWithFileName);
					doc.save(strCheckinPath);
				}
				else if(strstrFileNameLowercase.endsWith(".ppt") || strstrFileNameLowercase.endsWith(".pptx"))
				{
					//Setting license
					GLSAsposeLicense.checkSlidesLicense();
					// instantiate a Presentation object that represents a PPT file
					Presentation presentation = new Presentation(strCheckoutPathWithFileName);
					// save the presentation as PDF
					presentation.save(strCheckinPath, SaveFormat.Pdf);  
				}
				else if(strstrFileNameLowercase.endsWith(".xls") || strstrFileNameLowercase.endsWith(".xlsx")  || strstrFileNameLowercase.endsWith(".csv"))
				{
					//Setting license
					GLSAsposeLicense.checkCellsLicense();
					Workbook workbook = new Workbook(strCheckoutPathWithFileName);
					//workbook.getWorksheets().get(0).getPageSetup().setOrientation(PageOrientationType.PORTRAIT);
					PdfSaveOptions options = new PdfSaveOptions();
					options.setCompliance(PdfCompliance.PDF_A_1_A);
					options.setOnePagePerSheet(false);
					options.setAllColumnsInOnePagePerSheet(true);
					workbook.save(strCheckinPath, options);
					//workbook.save(strCheckinPath); 
				}
				if(UIUtil.isNotNullAndNotEmpty(strPDFFileName) && UIUtil.isNotNullAndNotEmpty(strCheckoutPath))
				{
					if(!FrameworkUtil.hasAccess(context,domObj,"checkin"))
					{
						//PushContext to allow checkin of generated preview files in Released state CLMS
						isCtxtPushed = true;
						ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
					}
					domObj.checkinFile(context, true, true, "", strFormatAutogeneratedPDF, strPDFFileName, strCheckoutPath);
					StringList slSelectable = new StringList(FROM_LATESTVERSION_TO_ID);
					slSelectable.add(FROM_LATESTVERSION_TO_TITLE);
					Map<String, Object> versionObjectMap = domObj.getInfo(context,slSelectable, slSelectable);
					StringList slTitle = returnStringListForObject (versionObjectMap.get(FROM_LATESTVERSION_TO_TITLE));
					StringList slId = returnStringListForObject (versionObjectMap.get(FROM_LATESTVERSION_TO_ID));
					String strVersionObjectId = slId.get(slTitle.indexOf(strFileName));

					DomainObject version = DomainObject.newInstance(context);
					version.createObject(context, strObjectType, null, "1", strPolicyVersion, VAULT_ESERVICE_PRODUCTION);
					version.setDescription(context, CONST_SYSTEM_GENERATED_PDF + "|"+strVersionObjectId + "|" +strFileName);
					//Set attributes
					Map mapVersionAttributes = new HashMap();
					mapVersionAttributes.put("Title", strPDFFileName);
					mapVersionAttributes.put("Is Version Object", "True");
					version.setAttributeValues(context, mapVersionAttributes);
					DomainRelationship.connect(context, domObj, CommonDocument.RELATIONSHIP_ACTIVE_VERSION, version);
					DomainRelationship.connect(context, domObj, CommonDocument.RELATIONSHIP_LATEST_VERSION, version);

					Files.deleteIfExists(Paths.get(strCheckoutPath + fileSeparator + strFileName));
					Files.deleteIfExists(Paths.get(strCheckinPath));
				}
			}
		} catch (Exception ex) {
			iReturn = 1;
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		finally {
			if(isCtxtPushed)
			{
				ContextUtil.popContext(context);
				isCtxtPushed =false;
			}
		}
		return iReturn;
	}
	//DCM (DS) User Story 457 - Claim User/Originator - VIEW files instead of downloading them - End

	//Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - START
	/**
	 * Method to copy files from One object to another
	 * @param context
	 * @param refObj
	 * @param baseObj
	 * @throws Exception
	 */
	public void checkinFilesFromObject(Context context, BusinessObject refObj, BusinessObject baseObj) throws Exception 
	{
		FileList files = new FileList();				
		String strWorkspacePath = context.createWorkspace();
		String sFileNameAbsolute = null;
		matrix.db.File checkoutFile = null;
		matrix.db.File fileDb = null;

		java.io.File file = null;
		if(UIUtil.isNotNullAndNotEmpty(strWorkspacePath))
		{
			java.io.File fEmatrixWebRoot = new java.io.File(strWorkspacePath);

			FileList fileList = baseObj.getFiles(context, DomainConstants.FORMAT_GENERIC);
			if(fileList != null && !fileList.isEmpty()){
				baseObj.checkoutFiles(context, false, DomainConstants.FORMAT_GENERIC, fileList, strWorkspacePath);
				FileItr fileItr = new FileItr(fileList);
				while(fileItr.next())
				{
					checkoutFile = fileItr.obj();
					file = new java.io.File(fEmatrixWebRoot, checkoutFile.getName());
					sFileNameAbsolute = file.getAbsolutePath();
					fileDb = new matrix.db.File(sFileNameAbsolute, DomainConstants.FORMAT_GENERIC);
					files.add(fileDb);
					CommonDocument cDoc = new CommonDocument(refObj);
					cDoc.createVersion(context,null,checkoutFile.getName(),null);
				}
				if (!files.isEmpty())
				{
					refObj.checkinFromServer(context, true, true, DomainConstants.FORMAT_GENERIC, null, files);
					FileItr fileItr2 = new FileItr(files);
					while(fileItr2.next())
					{
						Files.deleteIfExists(Paths.get(fileItr2.obj().getName()));
					}
				}
			}
		}
	}
	//Added for deleting,cloning Media objects on delete,copy/revise of Claim Objects - END

	//DCM (DS) US 1597 Create Product Config object and set config attributes during create claim process. -START
	/** This method create ClaimProductConfiguration and set attribute on it and connect it with Claim object.
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public void createClaimProductConfiguration(Context context, String strClaimRelID,String strClaimRequestObjectid, String strClaimType, String strClaimName) throws Exception
	{
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strClaimRequestObjectid) && UIUtil.isNotNullAndNotEmpty(strClaimRelID))
			{
				DomainObject domObj = DomainObject.newInstance(context, strClaimRequestObjectid);
				StringList strIntendedMarketValue =domObj.getInfoList(context, DomainObject.getAttributeSelect(ATTRIBUTE_PG_INTENDED_MARKETS));
				String strSymbolicName = FrameworkUtil.getAliasForAdmin(context, "Type",PG_CLAIM_PRODUCT_CONFIGURATION,false);
				String sObjGeneratorName = UICache.getObjectGenerator(context, strSymbolicName, "");
				String strName = DomainObject.getAutoGeneratedName(context, sObjGeneratorName, "");
				DomainObject domProdConfigObj = DomainObject.newInstance(context);
				domProdConfigObj.createObject(context, PG_CLAIM_PRODUCT_CONFIGURATION, strName, EMPTY_STRING, PG_CLAIM_PRODUCT_CONFIGURATION,VAULT_ESERVICE_PRODUCTION);
				if(BusinessUtil.isNotNullOrEmpty(strIntendedMarketValue))
				{
					Map mpAttributeMap = new HashMap();
					mpAttributeMap.put(ATTRIBUTE_PG_INTENDED_MARKETS, strIntendedMarketValue);
					domProdConfigObj.setAttributeValues(context, mpAttributeMap);
					//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
					Map<String, Object> mpEmptyAttributeMap = new HashMap<>();
					copyMapWithEmptyValues(mpEmptyAttributeMap, mpAttributeMap);
					strClaimType = EnoviaResourceBundle.getAdminI18NString(context,SCHEMA_TYPE, strClaimType, context.getSession().getLanguage());
					updateHistoryOnBaseObject(context, mpAttributeMap, mpEmptyAttributeMap, new StringBuilder(strClaimType).append(CONST_WHITE_SPACE).append(strClaimName).toString(), strClaimRequestObjectid);
					//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
				}
				RelToRelUtil reltorel =new RelToRelUtil();
				reltorel.connect(context,PG_CLAIM_PRODUCT_CONFIGURATION,strClaimRelID,domProdConfigObj.getObjectId(),false, true);	
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
	//DCM (DS) US 1597 Create Product Config object and set config attributes during create claim process.  End

	//DCM (DS) US 1602 clone Product Config object from source claim rel and connect it on new Claim Rel on Copy or Revise.
	/** This method is a trigger method which create ClaimProductConfiguration and set attribute on it and connect it with Claim object.
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public void cloneClaimProductConfigurationOnCopyOrReviseClaim(Context context, Map<String, Object> mapMethodParameter) throws Exception
	{
		try
		{
			Map<String, Object> mpProductConfigAttribute = (Map<String, Object>) mapMethodParameter.get("mpProductConfigAttribute");
			String strSourceObj = (String) mapMethodParameter.get("strSourceObj");
			String strNewObjectid = (String) mapMethodParameter.get("strNewObjectid");
			Boolean isFromClaim = (Boolean) mapMethodParameter.get("isFromClaim");
			String strWhere = (String) mapMethodParameter.get("strWhere");
			String strParentId = (String) mapMethodParameter.get("strParentId");
			String strTypeNameRevision = (String) mapMethodParameter.get("strTypeNameRevision");
			if(UIUtil.isNotNullAndNotEmpty(strSourceObj) && UIUtil.isNotNullAndNotEmpty(strNewObjectid))
			{
				DomainObject domSourceObj = DomainObject.newInstance(context, strSourceObj);
				DomainObject domNeweObj = DomainObject.newInstance(context, strNewObjectid);
				StringList slBusSelects = new StringList(DomainConstants.SELECT_ID);
				StringList slRelSelects = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
				slRelSelects.add(SELECT_FROMMID_PG_CLAIM_PRODUCT_CONFIGURATION_TO_ID);
				//DCM (DS) US 1602/1612 clone Product Config object from source claim rel and connect it on new Claim/Disclaimer Rel on Copy or Revise. Start
				Pattern relPattern = new Pattern(RELATIONSHIP_CLAIMS);
				relPattern.addPattern(RELATIONSHIP_PG_DISCLAMER);
				Pattern typePattern = new Pattern(TYPE_PG_CLAIM);
				typePattern.addPattern(TYPE_PG_DISCLAIMER);
				//DCM (DS) US 1602/1612 clone Product Config object from source claim rel and connect it on new Claim/Disclaimer Rel on Copy or Revise. End
				MapList mlSourceClaimInfo = domSourceObj.getRelatedObjects(context, 
						relPattern.getPattern(),		 //relPattern
						isFromClaim?TYPE_PG_CLAIM_REQUEST:typePattern.getPattern(),				 //typePattern
								slBusSelects,		//busSelects
								slRelSelects,		//relSelects
								isFromClaim,		//getTo
								!isFromClaim,		//getFrom
								(short) 1,			//recurseToLevel
								strWhere,		 	//busWhere
								null,				//relWhere
								0					//limit
						);
				MapList mlClonedClaimInfo = domNeweObj.getRelatedObjects(context, 
						relPattern.getPattern(),		//relPattern
						isFromClaim?TYPE_PG_CLAIM_REQUEST:typePattern.getPattern(),				 //typePattern
								slBusSelects,		//busSelects
								slRelSelects,		//relSelects
								isFromClaim,		//getTo
								!isFromClaim,		//getFrom
								(short) 1,			//recurseToLevel
								strWhere,		 	//busWhere
								null,				//relWhere
								0					//limit
						);

				Map mpSource;
				Map mpCloned;
				String strSourceId;
				StringList slProductConfigList;
				String strSymbolicName;
				String sObjGeneratorName;
				String strPCAutoName;
				DomainObject domSourcePC;
				BusinessObject busClonedPC;
				PGClaimManagementUtil pgClaimUtil = new PGClaimManagementUtil();
				StringList slNewClonedPC;
				RelToRelUtil reltorel =new RelToRelUtil();
				for (int i=0;i<mlSourceClaimInfo.size();i++)
				{
					mpSource = (Map)mlSourceClaimInfo.get(i);
					strSourceId = (String) mpSource.get(DomainConstants.SELECT_ID);
					if(mpSource.containsKey(SELECT_FROMMID_PG_CLAIM_PRODUCT_CONFIGURATION_TO_ID))
					{
						slProductConfigList = pgClaimUtil.returnStringListForObject(mpSource.get(SELECT_FROMMID_PG_CLAIM_PRODUCT_CONFIGURATION_TO_ID));
						slNewClonedPC = new StringList();
						//clone all PC connected to single pgClaims rel
						for(int j=0;j<slProductConfigList.size();j++)
						{
							strSymbolicName = FrameworkUtil.getAliasForAdmin(context, "Type",PG_CLAIM_PRODUCT_CONFIGURATION,false);
							sObjGeneratorName = UICache.getObjectGenerator(context, strSymbolicName, "");
							strPCAutoName = DomainObject.getAutoGeneratedName(context, sObjGeneratorName, "");
							domSourcePC =DomainObject.newInstance(context, slProductConfigList.get(j));
							busClonedPC = domSourcePC.cloneObject(context,strPCAutoName,null, null, false);
							slNewClonedPC.add(busClonedPC.getObjectId());
						}
						// connect Cloned PC to new PgClaims Rel
						for (int t=0;t<mlClonedClaimInfo.size();t++)
						{
							mpCloned = (Map) mlClonedClaimInfo.get(t);
							if(strSourceId.equals(mpCloned.get(DomainConstants.SELECT_ID))) {
								for(int k=0;k<slNewClonedPC.size();k++)
								{
									reltorel.connect(context,PG_CLAIM_PRODUCT_CONFIGURATION,(String) mpCloned.get(DomainRelationship.SELECT_ID),slNewClonedPC.get(k),false, true);	
								}
							}
						}
					}
				}
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : Start
				if(mpProductConfigAttribute != null) {
					Map<String, Object> mpEmptyAttributeMap = new HashMap<>();
					Map<String, Object> mpPlainAttributeMap = new HashMap<>();
					getMapWithPlainAttributeKeys(mpProductConfigAttribute, mpPlainAttributeMap);
					copyMapWithEmptyValues(mpEmptyAttributeMap, mpPlainAttributeMap);
					updateHistoryOnBaseObject(context, mpPlainAttributeMap, mpEmptyAttributeMap, strTypeNameRevision, strParentId);
				}
				//Sprint 8: US 4358: CLR history - update on CLR for product config changes and rel attributes on claim : End
			}
		}
		catch(Exception ex)
		{
			logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
