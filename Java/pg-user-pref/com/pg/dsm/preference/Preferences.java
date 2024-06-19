package com.pg.dsm.preference;

import javax.json.JsonArray;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.preference.enumeration.PlantPreferenceTable;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.util.CopyDataPreferenceUtil;
import com.pg.dsm.preference.util.GPSPreferenceUtil;
import com.pg.dsm.preference.util.IRMAttributePreferenceUtil;
import com.pg.dsm.preference.util.IRMProjectSpacePreferenceUtil;
import com.pg.dsm.preference.util.PackagingPreferenceUtil;
import com.pg.dsm.preference.util.ProductPreferenceUtil;
import com.pg.dsm.preference.util.RawMaterialPreferenceUtil;
import com.pg.dsm.preference.util.TechnicalSpecificationPreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;

public class Preferences {
    public enum IPSecurityPreference {
        BUSINESS_USE {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredBusinessUseClass(context);
            }

            @Override
            public String getID(Context context) throws FrameworkException {
                return util.getPreferredBusinessUseClassID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredBusinessUseClassPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.BUSINESS_CLASS.get();
            }
        },
        HIGHLY_RESTRICTED {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredHighlyRestrictedClass(context);
            }

            @Override
            public String getID(Context context) throws FrameworkException {
                return util.getPreferredHighlyRestrictedClassID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredHighlyRestrictedClassPhyscialID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.HIGHLY_RESTRICTED_CLASS.get();
            }
        },
        PRIMARY_ORGANIZATION {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredPrimaryOrgName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPreferredPrimaryOrgID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredPrimaryOrgPhyscialID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRIMARY_ORGANIZATION.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum ChangeActionPreference {
        PRIMARY_ORGANIZATION {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredRouteTemplatePrimaryOrgName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPreferredRouteTemplatePrimaryOrgID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredRouteTemplatePrimaryOrgPhyscialID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_ROUTE_TEMPLATE_PRIMARY_ORG.get();
            }
        },
        IN_WORK_ROUTE_TEMPLATE {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredInWorkRouteTemplateName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPreferredInWorkRouteTemplateID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredInWorkRouteTemplatePhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_ROUTE_TEMPLATE_IN_WORK.get();
            }
        },
        IN_APPROVAL_ROUTE_TEMPLATE {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredInApprovalRouteTemplateName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPreferredInApprovalRouteTemplateID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredInApprovalRouteTemplatePhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_ROUTE_TEMPLATE_IN_APPROVAL.get();
            }
        },
        CHANGE_TEMPLATE {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredChangeTemplateName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPreferredChangeTemplateID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredChangeTemplatePhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_CHANGE_TEMPLATE.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum DefaultCreatePartPreference {
        ON_CREATE_EQUIVALENT {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDefaultTypeOnEquivalent(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_DEFAULT_TYPE_ON_CREATE_MEP_SEP.get();
            }
        },
        ON_CREATE_PRODUCT {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDefaultTypeOnProduct(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_DEFAULT_TYPE_ON_CREATE_PRODUCT.get();
            }
        },
        ON_CREATE_SPEC {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDefaultTypeOnSpec(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_DEFAULT_TYPE_ON_CREATE_SPEC.get();
            }
        },
        DEFAULT_PLANTS {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDefaultPlantName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPreferredDefaultPlantID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredDefaultPlantPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_DEFAULT_PLANTS.get();
            }
        },
        DEFAULT_SHARING_MEMBERS {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDefaultSharingMemberName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPreferredDefaultSharingMemberID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPreferredDefaultSharingMemberPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PREFERRED_DEFAULT_SHARING_MEMBERS.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum DIPreference {
        PART_TYPE {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDIPartType(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_PART_TYPE.get();
            }
        },
        PHASE {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDIPhase(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_PHASE.get();
            }
        },
        MANUFACTURING_STATUS {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDIManufacturingStatus(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_MANUFACTURING_STATUS.get();
            }
        },
        STRUCTURE_RELEASE_CRITERIA {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDIStructureReleaseCriteria(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_RELEASE_CRITERIA_STATUS.get();
            }
        },
        PACKAGING_MATERIAL_TYPE {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDIPackagingMaterialType(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_PACKAGING_MATERIAL_TYPE.get();
            }
        },
        SEGMENT {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDISegment(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_SEGMENT.get();
            }
        },
        CLASS {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDIClass(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_CLASS.get();
            }
        },
        REPORTED_FUNCTION {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredDIReportedFunction(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.DI_PREFERRED_REPORTED_FUNCTION.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum IRMAttributePreference {
        TITLE {
            transient IRMAttributePreferenceUtil util = new IRMAttributePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getTitle(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_TITLE.get();
            }
        },
        DESCRIPTION {
            transient IRMAttributePreferenceUtil util = new IRMAttributePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getDescription(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_DESCRIPTION.get();
            }
        },
        POLICY {
            transient IRMAttributePreferenceUtil util = new IRMAttributePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPolicy(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_POLICY.get();
            }
        },
        CLASSIFICATION {
            transient IRMAttributePreferenceUtil util = new IRMAttributePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getClassification(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_CLASSIFICATION.get();
            }
        },
        REGION {
            transient IRMAttributePreferenceUtil util = new IRMAttributePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getRegion(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getRegionID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getRegionPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_REGION.get();
            }
        },
        SHARING_MEMBERS {
            transient IRMAttributePreferenceUtil util = new IRMAttributePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getSharingMember(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getSharingMemberID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getSharingMemberPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_SHARING_MEMBERS.get();
            }
        },
        BUSINESS_AREA {
            transient IRMAttributePreferenceUtil util = new IRMAttributePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getBusinessArea(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getBusinessAreaID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getBusinessAreaPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_BUSINESS_AREA.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum IRMApprovalPreference {
        ROUTE_INSTRUCTION {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredRouteInstruction(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_INSTRUCTION.get();
            }
        },
        ROUTE_ACTION {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredRouteAction(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_ACTION.get();
            }
        },
        ROUTE_TASK_RECIPIENT_MEMBER {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredRouteTaskRecipientMembers(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_TASK_RECIPIENT_MEMBERS.get();
            }
        },
        ROUTE_TASK_RECIPIENT_USER_GROUP {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPreferredRouteTaskRecipientUserGroups(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PREFERRED_ROUTE_TASK_RECIPIENT_USER_GROUPS.get();
            }
        },
        IS_ROUTE_TASK_RECIPIENT_USER_GROUP {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getIsPreferredRouteTaskRecipientUserGroup(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IS_IRM_PREFERRED_ROUTE_TASK_RECIPIENTS_USER_GROUPS.get();
            }
        },
        IS_ROUTE_TASK_RECIPIENT_MEMBER {
            transient UserPreferenceUtil util = new UserPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getIsPreferredRouteTaskRecipientMembers(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IS_IRM_PREFERRED_ROUTE_TASK_RECIPIENTS_MEMBERS.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum PackagingPreference {
        PART_TYPE {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPartType(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getPartTypeJson();
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_PART_TYPE.get();
            }
        },
        PHASE {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPhase(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // phase is based on type selection.
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_PHASE.get();
            }
        },
        MANUFACTURING_STATUS {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getManufacturingStatus(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // mfg statis is based on phase & type selection.
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_MANUFACTURING_STATUS.get();
            }
        },
        STRUCTURE_RELEASE_CRITERIA {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getStructureReleaseCriteria(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getReleaseCriteriaJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_RELEASE_CRITERIA.get();
            }
        },
        PACKAGING_MATERIAL_TYPE {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPackagingMaterialType(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPackagingMaterialTypeIDs(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPackagingMaterialTypePhysicalIDs(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getPackagingMaterialTypeJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_MATERIAL_TYPE.get();
            }
        },
        PACKAGING_COMPONENT_TYPE {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPackagingComponentType(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getPackagingComponentTypeIDs(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getPackagingComponentTypePhysicalIDs(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getPackagingComponentTypeJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_COMPONENT_TYPE.get();
            }
        },
        SEGMENT {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getSegmentName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getSegmentID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getSegmentPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getSegmentJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_SEGMENT.get();
            }
        },
        CLASS {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getClassValue(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getClassValueIDs(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getClassValuePhysicalIDs(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getClassesJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_CLASS.get();
            }
        },
        BASE_UNIT_OF_MEASURE {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getBaseUnitOfMeasure(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getBaseUnitOfMeasureIDs(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getBaseUnitOfMeasurePhysicalIDs(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getBaseUnitOfMeasureJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_BASE_UOM.get();
            }
        },
        REPORTED_FUNCTION {
            transient PackagingPreferenceUtil util = new PackagingPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getReportedFunctionName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getReportedFunctionID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getReportedFunctionPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                // reported function is type specific.
                return util.getReportedFunctionJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PACKAGING_REPORTED_FUNCTION.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getJson(Context context) throws MatrixException;

        public abstract String getPreferencePropertyKey();
    }

    public enum ProductPreference {
        PART_TYPE {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPartType(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getPartTypeJson();
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_PART_TYPE.get();
            }
        },
        PHASE {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPhase(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_PHASE.get();
            }
        },
        MANUFACTURING_STATUS {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getManufacturingStatus(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_MANUFACTURING_STATUS.get();
            }
        },
        STRUCTURE_RELEASE_CRITERIA {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getStructureReleaseCriteria(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getReleaseStatusCriteriaJson(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_RELEASE_CRITERIA.get();
            }
        },
        SEGMENT {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getSegmentName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getSegmentID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getSegmentPhysicalID(context);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getSegmentJson(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_SEGMENT.get();
            }
        },
        BUSINESS_AREA {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getBusinessAreaName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getBusinessAreaID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getBusinessAreaPhysicalID(context);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getBusinessAreaJson(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_BUSINESS_AREA.get();
            }
        },
        PRODUCT_CATEGORY_PLATFORM {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getProductCategoryPlatformName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getProductCategoryPlatformID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getProductCategoryPlatformPhysicalID(context);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // Depends on Business Area.
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_CATEGORY_PLATFORM.get();
            }
        },
        CLASS {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getClassValue(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getClassValueIDs(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getClassValuePhysicalIDs(context);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getClassesJson(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_CLASS.get();
            }
        },
        REPORTED_FUNCTION {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getReportedFunctionName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getReportedFunctionID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getReportedFunctionPhysicalID(context);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // type specific. (all products have same reported function except Formulation Part).
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_REPORTED_FUNCTION.get();
            }
        },
        PRODUCT_COMPLIANCE_REQUIRED {
            transient ProductPreferenceUtil util = new ProductPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getProductComplianceRequired(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getProductComplianceJson(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.PRODUCT_COMPLIANCE_REQUIRED.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract String getJson(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum RawMaterialPreference {
        PART_TYPE {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPartType(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getPartTypeJson();
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_PART_TYPE.get();
            }
        },
        PHASE {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getPhase(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // phase is type specific.
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_PHASE.get();
            }
        },
        MANUFACTURING_STATUS {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getManufacturingStatus(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // mfg status depends on type & phase.
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_MANUFACTURING_STATUS.get();
            }
        },
        STRUCTURE_RELEASE_CRITERIA {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getStructureReleaseCriteria(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING;
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getReleaseStatusCriteriaJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_RELEASE_CRITERIA.get();
            }
        },
        SEGMENT {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getSegmentName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getSegmentID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getSegmentPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getSegmentJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_SEGMENT.get();
            }
        },
        CLASS {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getClassValue(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getClassValueIDs(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getClassValuePhysicalIDs(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getClassesJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_CLASS.get();
            }
        },
        BUSINESS_AREA {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getBusinessAreaName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getBusinessAreaID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getBusinessAreaPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getBusinessAreaJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_BUSINESS_AREA.get();
            }
        },
        PRODUCT_CATEGORY_PLATFORM {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getProductCategoryPlatformName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getProductCategoryPlatformID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getProductCategoryPlatformPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // depends on business area.
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_PRODUCT_CATEGORY_PLATFORM.get();
            }
        },
        REPORTED_FUNCTION {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getReportedFunctionName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getReportedFunctionID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getReportedFunctionPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return DomainConstants.EMPTY_STRING; // type specific (confirm in all raw material types has same Reported Function)
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_REPORTED_FUNCTION.get();
            }
        },
        MATERIAL_FUNCTION {
            transient RawMaterialPreferenceUtil util = new RawMaterialPreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getMaterialFunctionName(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getMaterialFunctionID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getMaterialFunctionPhysicalID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, getPreferencePropertyKey(), value);
            }

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getMaterialFunctionJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.RAW_MATERIAL_FUNCTION.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getJson(Context context) throws MatrixException;

        public abstract String getPreferencePropertyKey();
    }

    public enum CopyDataPreference {
        PRODUCT {
            @Override
            public MapList getCopyData(Context context) throws MatrixException {
                CopyDataPreferenceUtil copyDataPreferenceUtil = new CopyDataPreferenceUtil();
                return copyDataPreferenceUtil.getCopyDataByCategory(context, PreferenceConstants.Basic.PRODUCT.get());
            }
        },
        PACKAGING {
            @Override
            public MapList getCopyData(Context context) throws MatrixException {
                CopyDataPreferenceUtil copyDataPreferenceUtil = new CopyDataPreferenceUtil();
                return copyDataPreferenceUtil.getCopyDataByCategory(context, PreferenceConstants.Basic.PACKAGING.get());
            }
        },
        RAW_MATERIAL {
            @Override
            public MapList getCopyData(Context context) throws MatrixException {
                CopyDataPreferenceUtil copyDataPreferenceUtil = new CopyDataPreferenceUtil();
                return copyDataPreferenceUtil.getCopyDataByCategory(context, PreferenceConstants.Basic.RAW_MATERIAL.get());
            }
        },
        TECHNICAL_SPEC {
            @Override
            public MapList getCopyData(Context context) throws MatrixException {
                CopyDataPreferenceUtil copyDataPreferenceUtil = new CopyDataPreferenceUtil();
                return copyDataPreferenceUtil.getCopyDataByCategory(context, PreferenceConstants.Basic.TECHNICAL_SPEC.get());
            }
        },
        ALL {
            @Override
            public MapList getCopyData(Context context) throws MatrixException {
                CopyDataPreferenceUtil copyDataPreferenceUtil = new CopyDataPreferenceUtil();
                return copyDataPreferenceUtil.getAllCopyData(context);
            }
        };

        public abstract MapList getCopyData(Context context) throws MatrixException;
    }

    public enum PlantDataPreference {
        PRODUCT {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                return PlantPreferenceTable.Retrieve.PRODUCT.getPlantData(context);
            }
        },
        PACKAGING {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                return PlantPreferenceTable.Retrieve.PACKAGING.getPlantData(context);
            }
        },
        RAW_MATERIAL {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                return PlantPreferenceTable.Retrieve.RAW_MATERIAL.getPlantData(context);
            }
        },
        TECHNICAL_SPEC {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                return PlantPreferenceTable.Retrieve.TECHNICAL_SPEC.getPlantData(context);
            }
        },
        ALL {
            @Override
            public MapList getPlantData(Context context) throws MatrixException {
                return PlantPreferenceTable.Retrieve.ALL.getPlantData(context);
            }
        };

        public abstract MapList getPlantData(Context context) throws MatrixException;
    }

    public enum IRMProjectSpacePreference {
        BUSINESS_AREA {
            transient IRMProjectSpacePreferenceUtil util = new IRMProjectSpacePreferenceUtil();

            @Override
            public String getName(Context context) throws FrameworkException {
                return util.getBusinessArea(context);
            }

            @Override
            public String getID(Context context) throws MatrixException {
                return util.getBusinessAreaID(context);
            }

            @Override
            public String getPhysicalID(Context context) throws MatrixException {
                return util.getBusinessAreaID(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.IRM_PROJECT_SPACE_BUSINESS_AREA.get();
            }
        };

        public abstract String getName(Context context) throws FrameworkException;

        public abstract String getID(Context context) throws MatrixException;

        public abstract String getPhysicalID(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }

    public enum TechnicalSpecificationPreference {
        PART_TYPE {
            transient TechnicalSpecificationPreferenceUtil util = new TechnicalSpecificationPreferenceUtil();

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getPartTypeJson();
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.TECHNICAL_SPEC_SEGMENT.get();
            }
        },
        SEGMENT {
            transient TechnicalSpecificationPreferenceUtil util = new TechnicalSpecificationPreferenceUtil();

            @Override
            public String getJson(Context context) throws MatrixException {
                return util.getSegmentJson(context);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.TECHNICAL_SPEC_SEGMENT.get();
            }
        };

        public abstract String getJson(Context context) throws MatrixException;

        public abstract String getPreferencePropertyKey();
    }

    public enum GPSPreference {
        SHARE_WITH_MEMBERS {
            transient GPSPreferenceUtil util = new GPSPreferenceUtil();

            @Override
            public String asStored(Context context) throws Exception {
                return util.getSharingMember(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getSharingMemberJsonArray(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.GPS_PREFERRED_SHARE_WITH_MEMBERS.get();
            }
        },
        PRE_TASK_NOTIFICATION_USERS {
            transient GPSPreferenceUtil util = new GPSPreferenceUtil();

            @Override
            public String asStored(Context context) throws Exception {
                return util.getPreTaskNotificationUser(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPreTaskNotificationUserJsonArray(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.GPS_PREFERRED_PRE_TASK_NOTIFICATION_USERS.get();
            }
        },
        POST_TASK_NOTIFICATION_USERS {
            transient GPSPreferenceUtil util = new GPSPreferenceUtil();

            @Override
            public String asStored(Context context) throws Exception {
                return util.getPostTaskNotificationUser(context);
            }

            @Override
            public JsonArray asJsonArray(Context context) throws MatrixException {
                return util.getPostTaskNotificationUserJsonArray(context);
            }

            @Override
            public void update(Context context, String value) throws Exception {
                util.setUserPreference(context, context.getUser(), getPreferencePropertyKey(), value);
            }

            @Override
            public String getPreferencePropertyKey() {
                return PreferenceConstants.Preferences.GPS_PREFERRED_POST_TASK_NOTIFICATION_USERS.get();
            }
        };

        public abstract String asStored(Context context) throws Exception;

        public abstract JsonArray asJsonArray(Context context) throws MatrixException;

        public abstract void update(Context context, String value) throws Exception;

        public abstract String getPreferencePropertyKey();
    }
}
