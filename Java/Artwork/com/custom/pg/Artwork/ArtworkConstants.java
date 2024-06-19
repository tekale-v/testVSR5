package com.custom.pg.Artwork;

import com.matrixone.apps.domain.util.PropertyUtil;

public class ArtworkConstants {

	public static final String TYPE_MASTER_COPY_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_MasterCopyTask");
	public static final String TYPE_LOCAL_COPY_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_LocalCopyTask");
	
	public static final String TYPE_TECHNICAL_SPECIFICATION_PHASE =	PropertyUtil.getSchemaProperty("type_pgAAA_TechSpecPhase");
	public static final String TYPE_CIC_PHASE = PropertyUtil.getSchemaProperty("type_pgAAA_CICPhase");
	public static final String TYPE_COPY_PHASE = PropertyUtil.getSchemaProperty("type_pgAAA_CopyPhase");
	public static final String TYPE_FINAL_ART_PHASE = PropertyUtil.getSchemaProperty("type_pgAAA_FinalArtPhase");
	public static final String TYPE_RIGHT_COPY_PHASE = PropertyUtil.getSchemaProperty("type_pgAAA_RightCopyPhase");
	public static final String TYPE_CPG_PRODUCT = PropertyUtil.getSchemaProperty("type_CPGProduct");
	public static final String TYPE_PROJECT_TEMPLATE = PropertyUtil.getSchemaProperty("type_PojectTemplate");
	//public static final String TYPE_ARTWORK_TEMPLATE = PropertyUtil.getSchemaProperty("type_pgAAA_ArtworkTemplate");
	//public static final String TYPE_ARTWORK_TEMPLATE_PHASE = PropertyUtil.getSchemaProperty("type_pgAAA_ArtworkTemplatePhase");
	//public static final String TYPE_UPLOAD_CIC_TASK =	PropertyUtil.getSchemaProperty("type_pgAAA_CreateArtworkTemplateTask");
	public static final String TYPE_CIC = PropertyUtil.getSchemaProperty("type_pgAAA_CIC");
	public static final String TYPE_SALES_CODES_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_SalesCodesTask");
	public static final String TYPE_LINK_PACKAGING_MATERIAL_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_LinkPackagingMaterialTask");
	public static final String TYPE_LINK_DESIGN_TEMPLATE_TASK	= PropertyUtil.getSchemaProperty("type_pgAAA_LinkDesignTemplateTask");
	public static final String TYPE_UPLOAD_CIC_TASK =	PropertyUtil.getSchemaProperty("type_pgAAA_UploadCICTask");
	public static final String TYPE_ASSIGN_AUTHOR_AND_APPROVER_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_AssignAuthorAndApproverTask");
	public static final String TYPE_APPROVE_POA_COPY_MATRIX_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_RightCopyAuthoringApprovalTask");
	public static final String TYPE_RIGHT_COPY_AUTHORING_TASK	= PropertyUtil.getSchemaProperty("type_pgAAA_RightCopyAuthoringTask");
	public static final String TYPE_MASTER_COPY_AUTHORING_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_MasterCopyAuthoringTask");
	public static final String TYPE_MASTER_COPY_APPROVAL_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_MasterCopyApprovalTask");
	public static final String TYPE_LOCAL_COPY_AUTHORING_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_LocalCopyAuthoringTask");
	public static final String TYPE_LOCAL_COPY_APPROVAL_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_LocalCopyApprovalTask");
	public static final String TYPE_REVIEW_AND_APPROVAL_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_ReviewAndApprovalTask");
	public static final String TYPE_SEND_POA_TO_BVE_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_SendPOAtoBVETask");
	public static final String TYPE_ASSEMBLY_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_AssemblyTask");
	public static final String TYPE_COLOR_SEPARATION_TASK = PropertyUtil.getSchemaProperty("type_pgAAA_ColorSeparationTask");
	public static final String TYPE_TASK_SEND_FOR_REWORK = PropertyUtil.getSchemaProperty("type_pgAAA_TaskSendForRework");
	public static final String TYPE_PG_CONFIGURATION_ADMIN = PropertyUtil.getSchemaProperty("type_pgConfigurationAdmin");
	
	public static final String POLICY_CIC = PropertyUtil.getSchemaProperty("policy_pgAAA_CIC");
	
	public static final String STATE_CIC_PRELIMINARY = PropertyUtil.getSchemaProperty("Policy",  POLICY_CIC, "state_Preliminary");
	public static final String STATE_CIC_ARTWORKINPROCESS = PropertyUtil.getSchemaProperty("Policy",  POLICY_CIC, "state_ArtworkInProcess");
	public static final String STATE_CIC_REVIEW = PropertyUtil.getSchemaProperty("Policy",  POLICY_CIC, "state_Review");
	public static final String STATE_CIC_APPROVED = PropertyUtil.getSchemaProperty("Policy",  POLICY_CIC, "state_Approved");
	public static final String STATE_CIC_RELEASE = PropertyUtil.getSchemaProperty("Policy",  POLICY_CIC, "state_Release");
	public static final String STATE_CIC_OBSOLETE = PropertyUtil.getSchemaProperty("Policy",  POLICY_CIC, "state_Obsolete");
	
	public static final String ATTRIBUTE_IS_GRAPHIC_ARTWORK_PACKAGE=PropertyUtil.getSchemaProperty("attribute_pgAAA_IsGraphicArtworkPackage");
	public static final String ATTRIBUTE_IS_PRIMARY_BRAND=PropertyUtil.getSchemaProperty("attribute_pgAAA_IsPrimaryBrand");
	public static final String ATTRIBUTE_PROJECT_ROLE=PropertyUtil.getSchemaProperty("attribute_ProjectRole");
	public static final String ATTRIBUTE_PROJECT_TEMPLATE_TYPE=PropertyUtil.getSchemaProperty("attribute_pgAAA_ProjectTemplateType");
	public static final String ATTRIBUTE_MINIMUM_LANGUAGE=PropertyUtil.getSchemaProperty("attribute_pgAAA_MinimumLanguage");
	public static final String ATTRIBUTE_MAXIMUM_LANGUAGE=PropertyUtil.getSchemaProperty("attribute_pgAAA_MaximumLanguage");
	public static final String ATTRIBUTE_MASTER_COPY_TASK_ID=PropertyUtil.getSchemaProperty("attribute_pgAAA_MasterCopyTaskID");
	public static final String ATTRIBUTE_LOCAL_COPY_TASK_ID=PropertyUtil.getSchemaProperty("attribute_pgAAA_LocalCopyTaskID");
	public static final String ATTRIBUTE_PG_PROJECT_ROLE=PropertyUtil.getSchemaProperty("attribute_pgAAA_ProjectRole");
	public static final String ATTRIBUTE_POST_TASK_PERSON_ASSIGNEE=PropertyUtil.getSchemaProperty("attribute_pgAAA_PostTaskPersonAssignee");
	public static final String ATTRIBUTE_POST_TASK_ROLE_ASSIGNEE=PropertyUtil.getSchemaProperty("attribute_pgAAA_PostTaskRoleAssignee");
	public static final String ATTRIBUTE_PRE_TASK_PERSON_ASSIGNEE=PropertyUtil.getSchemaProperty("attribute_pgAAA_PreTaskPersonAssignee");
	public static final String ATTRIBUTE_PRE_TASK_ROLE_ASSIGNEE=PropertyUtil.getSchemaProperty("attribute_pgAAA_PreTaskRoleAssignee");
	public static final String ATTRIBUTE_FIRST_SEQUENCE_APPROVER_ROLES=PropertyUtil.getSchemaProperty("attribute_pgAAA_FristSequenceApproverRoles");
	public static final String ATTRIBUTE_SECOND_SEQUENCE_APPROVER_ROLES=PropertyUtil.getSchemaProperty("attribute_pgAAA_SecondSequenceApproverRoles");
	public static final String ATTRIBUTE_FIRST_SEQUENCE_APPROVER_ASSIGNEES=PropertyUtil.getSchemaProperty("attribute_pgAAA_FirstSequenceApproverAssignees");
	public static final String ATTRIBUTE_SECOND_SEQUENCE_APPROVER_ASSIGNEES=PropertyUtil.getSchemaProperty("attribute_pgAAA_SecondSequenceApproverAssignees");
	public static final String ATTRIBUTE_APPROVAL_TASK_OBJECT_STATE=PropertyUtil.getSchemaProperty("attribute_pgAAA_ApprovalTaskObjectState");
	public static final String ATTRIBUTE_CO_BARCODE=PropertyUtil.getSchemaProperty("attribute_pgAAA_COBarcode");
	public static final String ATTRIBUTE_CU_BARCODE=PropertyUtil.getSchemaProperty("attribute_pgAAA_CUBarcode");
	public static final String ATTRIBUTE_FINISHED_PRODUCT_CODE=PropertyUtil.getSchemaProperty("attribute_pgAAA_FinishedProductCode");
	public static final String ATTRIBUTE_FPC_WEBSERVICE_URL=PropertyUtil.getSchemaProperty("attribute_pgAAA_FPCWebserviceURL");
	public static final String ATTRIBUTE_FPC_WEBSERVICE_USERNAME=PropertyUtil.getSchemaProperty("attribute_pgAAA_FPCWebserviceUsername");
	public static final String ATTRIBUTE_FPC_WEBSERVICE_PASSWORD=PropertyUtil.getSchemaProperty("attribute_pgAAA_FPCWebservicePassword");
	public static final String ATTRIBUTE_REUSE_AS_INPUT=PropertyUtil.getSchemaProperty("attribute_pgAAA_ReuseAsInput");
	public static final String ATTRIBUTE_REUSE_AS_DELIVERABLE=PropertyUtil.getSchemaProperty("attribute_pgAAA_ReuseAsDeliverable");
	public static final String ATTRIBUTE_OLD_IPMS=PropertyUtil.getSchemaProperty("attribute_pgAAA_OldIPMS");
	public static final String ATTRIBUTE_IPMS=PropertyUtil.getSchemaProperty("attribute_pgAAA_IPMS");
	public static final String ATTRIBUTE_CUSTOM_IPMS=PropertyUtil.getSchemaProperty("attribute_pgAAA_CustomIPMS");
	public static final String ATTRIBUTE_RIGHTCOPYAPPROVALSTATUS = PropertyUtil.getSchemaProperty("attribute_pgRightCopyApprovalStatus");
	public static final String ATTRIBUTE_PGAWLISPOAMANUALLYUPDATED = PropertyUtil.getSchemaProperty("attribute_pgAWLIsPOAManuallyUpdated");
	public static final String ATTRIBUTE_REWORK_OBJECT = PropertyUtil.getSchemaProperty("attribute_pgAAA_ReworkObject");
	public static final String ATTRIBUTE_REWORK_COMMENT = PropertyUtil.getSchemaProperty("attribute_pgAAA_Comment");
	public static final String ATTRIBUTE_LOCK_CONTENT  = PropertyUtil.getSchemaProperty("attribute_pgRTALockContent");
	public static final String ATTRIBUTE_POASECURITYGROUP = PropertyUtil.getSchemaProperty("attribute_pgRTAPOASecurityGroup");
	public static final String ATTRIBUTE_RTATASK_TYPE = PropertyUtil.getSchemaProperty( "attribute_pgRTATaskType");
	public static final String ATTRIBUTE_COMMENTS = PropertyUtil.getSchemaProperty("attribute_pgRTAComments");
	public static final String ATTRIBUTE_TECHNICAL_DRAWING = PropertyUtil.getSchemaProperty("attribute_pgAAA_TechnicalDrawing");
    public static final String ATTRIBUTE_HASMULTIPLEGTINS = PropertyUtil.getSchemaProperty("attribute_pgHasMultipleGTINS");

	public static final String SELECT_ATTRIBUTE_LOCK_CONTENT = "attribute[" + ATTRIBUTE_LOCK_CONTENT + "]";
	public static final String SELECT_ATTRIBUTE_POASECURITYGROUP = "attribute["+ ATTRIBUTE_POASECURITYGROUP + "]";
    public static final String SELECT_ATTRIBUTE_HASMULTIPLEGTINS = "attribute["+ATTRIBUTE_HASMULTIPLEGTINS+"]";
    public static final String SELECT_ATTRIBUTE_RTATASK_TYPE ="attribute["+ ArtworkConstants.ATTRIBUTE_RTATASK_TYPE + "]";
    public static final String SELECT_ATTRIBUTE_TECHNICAL_DRAWING ="attribute["+ATTRIBUTE_TECHNICAL_DRAWING+"]";

	public static final String VAULT_ESERVICE_PRODUCTION = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
	
	public static final String RELATIONSHIP_PROJECT_TO_CPG_PRODUCT = PropertyUtil.getSchemaProperty("relationship_pgAAA_ProjectToCPGProduct");
	public static final String RELATIONSHIP_PROJECT_TO_PRODUCT_LINE = PropertyUtil.getSchemaProperty("relationship_pgAAA_ProjectToProductLine");
	public static final String RELATIONSHIP_PROJECT_TO_MARKETING_OPTION = PropertyUtil.getSchemaProperty("relationship_pgAAA_ProjectToMarketingOption");
	public static final String RELATIONSHIP_ARTWORK_TEMPLATE_TO_POA = PropertyUtil.getSchemaProperty("relationship_pgAAA_ArtworkTemplateToPOA");
	public static final String RELATIONSHIP_ARTWORK_TEMPLATE_TO_PRODUCT_VARIANT = PropertyUtil.getSchemaProperty("relationship_pgAAA_ArtworkTemplateToProductVariant");
	public static final String RELATIONSHIP_REUSE_DELIVERABLE = PropertyUtil.getSchemaProperty("relationship_pgAAA_ReuseDeliverable");
	public static final String RELATIONSHIP_POA_TO_COUNTRY = PropertyUtil.getSchemaProperty("relationship_pgAAA_POAToCountry");
	public static final String RELATIONSHIP_PG_COUNTRY = PropertyUtil.getSchemaProperty("relationship_pgCountry");
	public static final String RELATIONSHIP_PGMCLCLONEDFROM = PropertyUtil.getSchemaProperty("relationship_pgMCLClonedFrom");
	public static final String RELATIONSHIP_OBJECT_TO_TASKSENDFORREWORK = PropertyUtil.getSchemaProperty("relationship_pgAAA_ObjectToTaskSendForRework");
	public static final String RELATIONSHIP_WBSTASK_TO_SENDFORREWORK = PropertyUtil.getSchemaProperty("relationship_pgAAA_WBSTaskToSendForRework");
	public static final String RELATIONSHIP_WBSTASK_TO_PGPLIDEFECTTYPE =  PropertyUtil.getSchemaProperty("relationship_pgAAA_WBSTaskTopgPLIDefectType");
	public static final String RELATIONSHIP_WBSTASK_TO_PGPLIRESPONSIBLEFUNCTION =  PropertyUtil.getSchemaProperty("relationship_pgAAA_WBSTaskTopgPLIResponsibleFunction");
	public static final String RELATIONSHIP_WBSTASK_TO_PGPLIREASON =  PropertyUtil.getSchemaProperty("relationship_pgAAA_WBSTaskTopgPLIReason");
	public static final String RELATIONSHIP_ARTWORK_PACKAGE_TO_PROJECT = PropertyUtil.getSchemaProperty("relationship_pgRTAArtworkPackageToProject");
	public static final String RELATIONSHIP_PROJECT_TO_POA = PropertyUtil.getSchemaProperty("relationship_pgAAA_ProjectToPOA");

	public static final String PROJECTTYPE_ARTWORK_PROJECT = "Artwork Project";
	public static final String RCM_STATUS_STARTED		= "Started";
	public static final String RCM_STATUS_APPROVED		= "Approved";
	public static final String RCM_STATUS_REJECTED		= "Rejected";
	public static final String RCM_STATUS_NOT_STARTED	= "Not Started";
	
	public static final String RIGHT_COPY_ARTWORK_ACTION	= "RightCopy";
	public static final String AT_ARTWORK_ACTION	= "ATApproval";
	public static final String FA_ARTWORK_ACTION	= "POAApproval";
	public static final String MCL_ARTWORK_ACTION	= "MCLApproval";
	public static final String PERSON_USERAGENT = PropertyUtil.getSchemaProperty("person_UserAgent");
	public static final String LOCAL_REQUIREMENT	= "LocalRequirement";
	public static final String CACE	= "CACE";
	public static final String GCASPA	= "GCASPA";
	public static final String FA	= "FA";
	public static final String CIC	= "CIC";
	public static final String GS1_RESPONCE	= "GS1 Response";

}