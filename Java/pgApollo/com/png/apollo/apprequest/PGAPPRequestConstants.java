package com.png.apollo.apprequest;

import com.matrixone.apps.domain.util.PropertyUtil;

public class PGAPPRequestConstants {
	
	public static final String ATTRIBUTE_REASON_FOR_REJECTION = PropertyUtil.getSchemaProperty("attribute_ReasonForRejection");
	public static final String ATTRIBUTE_REASON_FOR_CANCEL = PropertyUtil.getSchemaProperty("attribute_ReasonForCancel"); 
	public static final String ATTRIBUTE_PG_APP_REQUESTCHANGETYPE = PropertyUtil.getSchemaProperty("attribute_pgAPPRequestChangeType");
	public static final String ATTRIBUTE_PG_AFFECTEDITEM = PropertyUtil.getSchemaProperty("attribute_pgAffectedItem");
	public static final String ATTRIBUTE_PG_IMPLEMENTEDITEM = PropertyUtil.getSchemaProperty("attribute_pgImplementedItem");
	public static final String ATTRIBUTE_PG_PROJECT = PropertyUtil.getSchemaProperty("attribute_pgProject");
	public static final String ATTRIBUTE_PG_LPDPLANTS = PropertyUtil.getSchemaProperty("attribute_pgLPDPlants");
	public static final String ATTRIBUTE_PG_APP_REQUEST_SUBSTITUTES = PropertyUtil.getSchemaProperty("attribute_pgAPPRequestSubstitutes");
	public static final String ATTRIBUTE_PG_LPD_DUE_DATE = PropertyUtil.getSchemaProperty("attribute_pgLPDDueDate");
	public static final String ATTRIBUTE_PG_APPREQUESTPLANTS = PropertyUtil.getSchemaProperty("attribute_pgAPPRequestPlants");
	public static final String ATTRIBUTE_PG_EXPIRATIONDATE = PropertyUtil.getSchemaProperty("attribute_pgExpirationDate");
	public static final String ATTRIBUTE_PG_VALIDUNTILDATE = PropertyUtil.getSchemaProperty("attribute_pgValidUntilDate");
	public static final String ATTRIBUTE_PG_VALIDSTARTDATE = PropertyUtil.getSchemaProperty("attribute_pgValidStartDate");
	public static final String ATTRIBUTE_PG_LPD_MULTI_SIZE = PropertyUtil.getSchemaProperty("attribute_pgLPDMultiSize");
	public static final String ATTRIBUTE_PG_LPD_MULTI_REGION = PropertyUtil.getSchemaProperty("attribute_pgLPDMultiRegion");
	public static final String ATTRIBUTE_PG_LPD_MULTI_SUBREGION = PropertyUtil.getSchemaProperty("attribute_pgLPDMultiSubRegion");
	public static final String ATTRIBUTE_PGCLAIMEDBY = PropertyUtil.getSchemaProperty("attribute_pgClaimedBy");
	public static final String ATTRIBUTE_CREATED_ON = PropertyUtil.getSchemaProperty("attribute_CreatedOn");
	public static final String ATTRIBUTE_PGAPPREQUESTCOMMENTS = PropertyUtil.getSchemaProperty("attribute_pgAPPRequestComments");
	public static final String ATTRIBUTE_PG_LIFECYCLE_STATUS = PropertyUtil.getSchemaProperty("attribute_pgLifeCycleStatus");
	public static final String ATTRIBUTE_COMMENT = PropertyUtil.getSchemaProperty("attribute_Comment");
	public static final String ATTRIBUTE_PGFAILEDREASON = PropertyUtil.getSchemaProperty("attribute_pgFailedReason");
	public static final String ATTRIBUTE_DESIGNATEDUSER = PropertyUtil.getSchemaProperty("attribute_DesignatedUser");

	
	public static final String SELECT_ATTRIBUTE_PG_APP_REQUESTCHANGETYPE = "attribute["+ATTRIBUTE_PG_APP_REQUESTCHANGETYPE+"]";
	public static final String SELECT_ATTRIBUTE_PG_AFFECTEDITEM = "attribute["+ATTRIBUTE_PG_AFFECTEDITEM+"]";
	public static final String SELECT_ATTRIBUTE_PG_IMPLEMENTEDITEM = "attribute["+ATTRIBUTE_PG_IMPLEMENTEDITEM+"]";
	public static final String SELECT_ATTRIBUTE_PG_APP_REQUEST_SUBSTITUTES = "attribute["+ATTRIBUTE_PG_APP_REQUEST_SUBSTITUTES+"]";
	public static final String SELECT_ATTRIBUTE_PG_PROJECT = "attribute["+ATTRIBUTE_PG_PROJECT+"]";
	public static final String SELECT_ATTRIBUTE_PG_LPDPLANTS = "attribute["+ATTRIBUTE_PG_LPDPLANTS+"]";
	public static final String SELECT_ATTRIBUTE_PG_LPD_DUE_DATE = "attribute["+ATTRIBUTE_PG_LPD_DUE_DATE+"]";
	public static final String SELECT_ATTRIBUTE_PG_APPREQUESTPLANTS = "attribute["+ATTRIBUTE_PG_APPREQUESTPLANTS+"]";
	public static final String SELECT_ATTRIBUTE_PG_EXPIRATIONDATE = "attribute["+ATTRIBUTE_PG_EXPIRATIONDATE+"]";
	public static final String SELECT_ATTRIBUTE_PG_VALIDUNTILDATE = "attribute["+ATTRIBUTE_PG_VALIDUNTILDATE+"]";
	public static final String SELECT_ATTRIBUTE_PG_VALIDSTARTDATE = "attribute["+ATTRIBUTE_PG_VALIDSTARTDATE+"]";
	public static final String SELECT_ATTRIBUTE_REASON_FOR_REJECTION = "attribute["+ATTRIBUTE_REASON_FOR_REJECTION+"]";
	public static final String SELECT_ATTRIBUTE_REASON_FOR_CANCEL = "attribute["+ATTRIBUTE_REASON_FOR_CANCEL+"]";
	public static final String SELECT_ATTRIBUTE_PG_LPD_MULTI_SIZE = "attribute["+ATTRIBUTE_PG_LPD_MULTI_SIZE+"]";
	public static final String SELECT_ATTRIBUTE_PG_LPD_MULTI_REGION = "attribute["+ATTRIBUTE_PG_LPD_MULTI_REGION+"]";
	public static final String SELECT_ATTRIBUTE_PG_LPD_MULTI_SUBREGION = "attribute["+ATTRIBUTE_PG_LPD_MULTI_SUBREGION+"]";
	public static final String SELECT_ATTRIBUTE_PGCLAIMEDBY = "attribute["+ATTRIBUTE_PGCLAIMEDBY+"]";
	public static final String SELECT_ATTRIBUTE_CREATED_ON = "attribute["+ATTRIBUTE_CREATED_ON+"]";
	public static final String SELECT_ATTRIBUTE_PGAPPREQUESTCOMMENTS = "attribute["+ATTRIBUTE_PGAPPREQUESTCOMMENTS+"]";
	public static final String SELECT_ATTRIBUTE_PG_LIFECYCLE_STATUS = "attribute["+ATTRIBUTE_PG_LIFECYCLE_STATUS+"]";
	public static final String SELECT_ATTRIBUTE_DESIGNATEDUSER = "attribute["+ATTRIBUTE_DESIGNATEDUSER+"]";

	public static final String TYPE_PGAPPREQUEST=PropertyUtil.getSchemaProperty("type_pgAPPRequest");
	public static final String TYPE_PGAPPREQUESTDATA=PropertyUtil.getSchemaProperty("type_pgAPPRequestData");
	public static final String TYPE_GROUP = PropertyUtil.getSchemaProperty("type_Group");
	public static final String TYPE_PERSON = PropertyUtil.getSchemaProperty("type_Person");
	public static final String TYPE_PLANT = PropertyUtil.getSchemaProperty("type_Plant");

	public static final String RELATIONSHIP_PGAPPREQUESTAFFECTEDITEM=PropertyUtil.getSchemaProperty("relationship_pgAPPRequestAffectedItem");
	public static final String RELATIONSHIP_PGAPPREQUESTCHANGEDATA=PropertyUtil.getSchemaProperty("relationship_pgAPPRequestChangeData");
	public static final String RELATIONSHIP_PGAPPREQUESTIMPLEMENTEDITEM=PropertyUtil.getSchemaProperty("relationship_pgAPPRequestImplementedItem");
	public static final String RELATIONSHIP_TECHNICALASSIGNEE=PropertyUtil.getSchemaProperty("relationship_TechnicalAssignee");
	public static final String RELATIONSHIP_GROUP_MEMBER=PropertyUtil.getSchemaProperty("relationship_GroupMember");

	public static final String POLICY_PGAPPREQUESTDATA=PropertyUtil.getSchemaProperty("policy_pgAPPRequestData");
	public static final String POLICY_PGAPPREQUEST=PropertyUtil.getSchemaProperty("policy_pgAPPRequest");
	public static final String POLICY_PGREJECTED=PropertyUtil.getSchemaProperty("policy_pgRejected");
	public static final String POLICY_PGARCHIVED=PropertyUtil.getSchemaProperty("policy_pgArchived");
	public static final String POLICY_CANCELLED = PropertyUtil.getSchemaProperty("policy_Cancelled");
	
	public static final String PGAPPREQUEST_STATE_CREATE = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Create");
	public static final String PGAPPREQUEST_STATE_REVIEW = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Review");
	public static final String PGAPPREQUEST_STATE_READY_TO_IMPLEMENT = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_ReadyToImplement");
	public static final String PGAPPREQUEST_STATE_IMPLEMENTED = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Implemented");
	public static final String PGAPPREQUEST_STATE_RELEASED = PropertyUtil.getSchemaProperty("policy", POLICY_PGAPPREQUEST, "state_Released");
	public static final String PGAPPREQUEST_STATE_REJECTED = PropertyUtil.getSchemaProperty("policy", POLICY_PGREJECTED, "state_Rejected");
	public static final String PGAPPREQUEST_STATE_CANCELLED = PropertyUtil.getSchemaProperty("policy", POLICY_CANCELLED, "state_Cancelled");
	public static final String PGAPPREQUEST_STATE_ARCHIVED = PropertyUtil.getSchemaProperty("policy", POLICY_PGARCHIVED, "state_Archived");

	
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_SUBSTITUTE_ADDITION = "Substitutes Addition";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_PLANTS_ADDITION = "Plants Addition";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_EXPIRATION_DATE_CHANGE = "Expiration Date Change";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_MFG_STATUS_CHANGE = "Manufacturing Status Change";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_OBSOLESENCE = "Obsolescence Request";
	public static final String RANGE_PGAPPREQUESTCHANGETYPE_ACCELERATED_RELEASE_ADDITION = "Accelerated Release";
	
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_EXPERIMENTAL="DSOLifeCycleStatusExperimental";
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_PILOT="DSOLifeCycleStatusPilot";
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTION="DSOLifeCycleStatusProduction";
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTIONAPP="DSOLifeCycleStatusProductionAPP";
	
	public static final String STR_SUCCESS_AUTOFULLFILLMENT_REVISE ="Revise for APP Request Auto-Fullfillment Successful";

	public static final String STR_ERROR_AUTOFULLFILLMENT_FAILED ="Auto-Fullfillment failed :";
	public static final String STR_ERROR_IMPLEMENT_PLANT_ADDITION ="Error Occurred during Plant Update :";
	public static final String STR_ERROR_IMPLEMENT_SUBSTIUTE_UPDATE ="Error Occurred during Substitute Addition :";
	public static final String STR_ERROR_IMPLEMENT_ATTRIBUTE_UPDATE ="Error Occurred during Attribute Update :";

	public static final String STR_ERROR_SUBSTIUTE_ALREADY_PRESENT ="Requested Substitute <OBJECTNAME> is already present for Layer <LAYERNAME>";
	public static final String STR_ERROR_LAYER_NOT_PRESENT ="Requested Layer <LAYERNAME> is not present on Implemented Item";
	public static final String STR_ERROR_PLANT_ALREADY_PRESENT ="Following Plants are already present :";
	public static final String STR_ERROR_MFG_STATUS_ALREADY_SET ="Requested Manufacturing Status is already set ";
	public static final String STR_ERROR_MFG_STATUS_INVALID ="Invalid Mfg. Status Value";
	public static final String STR_ERROR_EXPIRATION_DATE_ALREADY_SET ="Requested Expiration Date is set already";
	public static final String STR_ERROR_AUTOFULLFILLMENT_STATE_OBSOLETE ="Implemented Item is in Obsolete state";
	public static final String STR_ERROR_AUTOFULLFILLMENT_STATE_FROZENAPPROVED ="Implemented Item is in Frozen or Approved state";
	public static final String STR_ERROR_AUTOFULLFILLMENT_REQUEST_NOT_READY_TO_IMPLEMENT ="Request should be in 'Ready To Implement' state";
	public static final String STR_ERROR_GROUP_LAYER_MISSING_ONEBOM ="Layer <LAYERNAME> is not present on Implemented Item";
	public static final String STR_ERROR_AUTOFULLFILLMENT_NOREADACCESS ="User does not have read access on Implemented Item";
	public static final String STR_ERROR_AUTOFULLFILLMENT_NOMODIFYACCESS ="User does not have modify access on Implemented Item";
	public static final String STR_ERROR_AUTOFULLFILLMENT_NOREVISEACCESS ="User does not have revise access on Implemented Item";

	public static final String STR_ERROR_AUTOFULLFILLMENT_REVISE ="Revision of LPD APP is failed during APP Request Auto-Fullfillment ";
	public static final String STR_ERROR_AUTOFULLFILLMENT_FAILED_INVALID_TYPE_OF_CHANGE ="Auto-Fullfillment cannot be proceeded for ";
	public static final String STR_ERROR_AUTOFULLFILLMENT_ACCELERATED_RELEASE ="Implemented Item is not in Released State for ";
	public static final String STR_ERROR_AUTOFULLFILLMENT_OBSOLESCENCE ="Implemented Item is not in Obsolete State for ";

	public static final String STR_ERROR_AUTOFULLFILLMENT_NO_SUBSTIUTES_TO_IMPLEMENT ="No Substitute to implement";
	public static final String STR_ERROR_AUTOFULLFILLMENT_NO_PLANTS_TO_IMPLEMENT ="No Plants to implement";
	public static final String STR_ERROR_AUTOFULLFILLMENT_NO_EXPIRATIONDATE_TO_IMPLEMENT ="Empty Expiration Date to implement";
	public static final String STR_ERROR_AUTOFULLFILLMENT_POSTPROCESS = "Error ocurred while post processing APP Request";
	public static final String STR_ERROR_AUTOFULLFILLMENT_HISTORY_STAMPING = "Error ocurred while History Stamping during APP Request Auto fulfillment";
	public static final String STR_ERROR_UNAUTHORIZED_ACCESS = "Not Authorized User";
	public static final String STR_ERROR_INVALIDKEYS_GETAPPREQUEST = "Invalid Keys to get APP Requests";

	public static final String STR_ERROR_MISSING_MANDATORY_KEYS = "Missing Mandatory Information";
	public static final String STR_ERROR_INVALID_ACTION = "Invalid Action on Request Line Item";
	public static final String STR_ERROR_MISSING_ACTION = "Missing Action on Request Line Item";
	public static final String STR_ERROR_NOT_ALLOWED_OPERATION_REQUEST = "No Operation Allowed on any Request given in Payload";
	public static final String STR_ERROR_MISSING_INVALID_PICKLIST = "Invalid Input PickList or Existing Dependent Picklist values";
	public static final String STR_ERROR_INVALID_APP_REQUEST_STATE = "Invalid APP Request State";
	public static final String STR_ERROR_UPDATE_REQUEST_FAIL_TYPE_OF_CHANGE_PRESENT = "Update Request Failed, Type of Change not allowed";	
	public static final String STR_ERROR_DUPLICATE_AFFECTEDITEM_FORADDITION = "Not Allowed to use existing affected item of request for given Request Set";
	public static final String STR_ERROR_UPDATE_NO_CREATE_OR_INPROGRESS_APPREQUEST = "Cannot update Request, Some Requests are in progress or No APP Request is in 'Create' state";
	public static final String STR_ERROR_INVALID_AFFECTED_ITEM = "Invalid Affected Items";
	public static final String STR_ERROR_INVALID_REQUEST_SET = "Invalid Request Set";
	public static final String STR_ERROR_INVALID_SIZEVALUES = "Size values with Size ALL should not contain other Sizes";
	public static final String STR_ERROR_OBJECT_NOT_FOUND = "Object does not exist";
	public static final String STR_PG_APPREQUEST_CONFIG = "pgAPPRequestConfig";	
	public static final String STR_ERROR_EMPTY_PICKLIST = "Empty Input Value of PickList not allowed";

	public static final String STR_MESSAGE_MARK_AS_IMPLMENTED ="Request is marked as Implemented manually by <CONTEXTUSER>, Requested Changes are not applied on Implemented Item";
	public static final String STR_MESSAGE_REQUETPROMOTIONRELEASED_CHANGES_IMPLEMENTED ="Request is promoted to Released as Changes already implemented";

	public static final String HISTORY_APP_REQUEST_IMPLEMENTATION="APP Request Implementation : ";
	public static final String APP_REQUEST_IMPLEMENTATION_HISTORY_STARTS= "Start";
	public static final String APP_REQUEST_IMPLEMENTATION_HISTORY_END= "End";


	public static final String KEY_AUTHORIZEDTOUSE = "AuthorizedToUse";
	public static final String KEY_AUTHORIZEDTOPRODUCE = "AuthorizedToProduce";
	public static final String KEY_ACTIVATED = "Activated";
	public static final String KEY_MANUFACTURINGSTATUS = "ManufacturingStatus";
	public static final String KEY_EXPIRATIONDATE = "ExpirationDate";
	public static final String KEY_DUEDATE = "DueDate";
	public static final String KEY_TYPE_OF_CHANGE = "TypeOfChange";
	public static final String KEY_APPREQUEST_COMMENTS = "ApprovedByComments";
	public static final String KEY_TYPE_OF_CHANGE_LABEL = "TypeOfChangeLabel";
	public static final String STR_FULL_NAME_DETAILS_KEY = "_FullName";
	public static final String KEY_PROCEED_TO_IMPLEMENT = "ProceedToImplement";
	public static final String KEY_CHANGES_IMPLEMENTED = "ChangesImplemented";
	public static final String KEY_STATE = "State";
	public static final String KEY_BUSINESS_AREA = "BusinesArea";
	public static final String KEY_LPDREGION = "LPDRegion";
	public static final String KEY_LPDSUBREGION = "LPDSubRegion";
	public static final String KEY_RESPONSEKEY_STATUS = "Status";
	public static final String KEY_MISSING_PICKLIST = "MissingPicklist";
	public static final String KEY_CREATEDON = "CreatedOn";
	public static final String KEY_CLAIMEDBY = "ClaimedBy";
	public static final String KEY_APPROVER_LIST = "ApproverList";
	public static final String KEY_VALID_START_DATE = "ValidStartDate";
	public static final String KEY_VALID_UNTIL_DATE = "ValidUntilDate";
	public static final String KEY_COMMENTS = "Comments";
	public static final String KEY_OWNER = "Owner";
	public static final String KEY_NAME = "Name";
	public static final String KEY_INVALID_PICKLIST = "InvalidPickList";

	
	public static final String KEY_REQUEST_CHANGE_DETAILS = "RequestChangeDetails";
	public static final String KEY_REQUEST_LINE_ITEMS = "RequestLineItems";
	public static final String KEY_APP_REQUEST_INFOLIST = "APPRequestInfoList";
	public static final String KEY_PARENTREQUESTREQUESTEDCHANGEINFO = "RequestSetRequestedChangeInfo";

	public static final String KEY_DATA = "data";
	
	public static final String KEY_REQUEST_ID = "RequestId";
	public static final String KEY_REQUEST_PHYSICAL_ID = "RequestPhysicalId";
	public static final String KEY_REQUEST_STATE = "RequestState";

	public static final String KEY_REQUEST_SET_ID = "RequestSetId";
	public static final String KEY_REQUEST_SET_PHYSICAL_ID = "RequestSetPhysicalId";
	
	public static final String KEY_AFFECTED_ITEM = "AffectedItem";
	public static final String KEY_AFFECTED_ITEM_PHYSICAL_ID = "AffectedItemPhysicalId";
	public static final String KEY_AFFECTED_ITEM_TYPE = "AffectedItemType";
	public static final String KEY_AFFECTED_ITEM_REVISION = "AffectedItemRevision";
	public static final String KEY_AFFECTED_ITEM_STATE = "AffectedItemState";
	public static final String KEY_AFFECTED_ITEM_POLICY = "AffectedItemPolicy";
	
	public static final String KEY_IMPLEMENTED_ITEM = "ImplementedItem";
	public static final String KEY_IMPLEMENTED_ITEM_PHYSICAL_ID = "ImplementedItemPhysicalId";
	public static final String KEY_IMPLEMENTED_ITEM_TYPE = "ImplementedItemType";
	public static final String KEY_IMPLEMENTED_ITEM_REVISION = "ImplementedItemRevision";
	public static final String KEY_IMPLEMENTED_ITEM_STATE = "ImplementedItemState";
	public static final String KEY_IMPLEMENTED_ITEM_POLICY = "ImplementedItemPolicy";
	public static final String KEY_IMPLEMENTED_ITEM_DISPLAY_NAME = "ImplementedItemDisplayName";
	
	public static final String KEY_BACKGROUNDPROCESS = "BackgroundProcess";
	public static final String KEY_BACKGROUNDPROCESS_PHYSICALID = "BackgroundProcessPhysicalId";

	public static final String KEY_ACTION = "Action";
	public static final String KEY_IMPLEMENTED_ITEM_VARIANT = " (Variant)";
	public static final String KEY_IMPLEMENTED_ITEM_DEFAULT = " (Default)";

	public static final String STR_ACTION_ADD = "add";
	public static final String STR_ACTION_CANCEL = "cancel";
	public static final String STR_ACTION_REJECT = "reject";
	public static final String STR_ACTION_PROMOTE = "promote";
	public static final String STR_ACTION_DEMOTE = "demote";
	
	public static final String STR_MODE_IMPLEMENTAPPREQUEST="ImplementAPPRequests";
	public static final String STR_TITLE_IMPLEMENTAPPREQUEST="Implement APP Requests";
	
	public static final String SELECT_LAST_PHYSICALID = "last.physicalid";
	public static final String SELECT_LAST_CURRENT = "last.current";
	public static final String SELECT_LAST_NAME = "last.name";
	public static final String SELECT_LAST_REVISION = "last.revision";

	
	
	
}
