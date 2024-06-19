package com.pg.dsm.preference.enumeration;

import java.util.List;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.Context;
import matrix.util.MatrixException;

public class IRMUPTConstants {
    public enum Attributes {
        UPT_MIGRATED("attribute_pgUPTIsMigrated"),
        UPT_SHARE_WITH_MEMBERS("attribute_pgUPTShareWithMembers"), // stores multiple (physical id)
        UPT_BUSINESS_AREA("attribute_pgUPTBusinessArea"), // stores multiple (physical id)
        UPT_BUSINESS_USE("attribute_pgUPTBusinessUseIPClass"), // stores multiple (physical id)
        UPT_HIGHLY_RESTRICTED("attribute_pgUPTHighlyRestrictedIPClass"), // stores multiple (physical id)
        UPT_REGION("attribute_pgUPTRegion"), // stores multiple (physical id)
        UPT_TITLE("attribute_pgUPTTitle"),
        UPT_POLICY("attribute_pgUPTPolicy"),
        UPT_DESCRIPTION("attribute_pgUPTDescription"),
        UPT_CLASSIFICATION("attribute_pgUPTClassification"),
        UPT_ROUTE_INSTRUCTION("attribute_pgUPTRouteInstruction"),
        UPT_ROUTE_ACTION("attribute_pgUPTRouteAction"),
        UPT_TASK_RECIPIENT_GROUP("attribute_pgUPTTaskRecipientGroup"), // stores multiple values (format: <group-name>~<group-physical-id>)
        UPT_TASK_RECIPIENT_MEMBER("attribute_pgUPTTaskRecipientMember"), // stores multiple values (format: <user-name>~<role>~<user-physical-id>)
        UPT_PID("attribute_pgUPTPhyID"), 	//For storing Template pid in IRM document.   
        ATTRIBUTE_PGUPTTEMPLATEIRM("attribute_pgUPTTemplateIRM"),
        DOCUMENT_BUSINESS_AREA("attribute_pgBusinessArea");
        private final String name;

        Attributes(String name) {
            this.name = name;
        }

        public String getName(Context context) {
            return PropertyUtil.getSchemaProperty(context, this.name);
        }

        public String getSelect(Context context) {
            return DomainObject.getAttributeSelect(this.getName(context));
        }

        public String toString() {
            return this.name;
        }

    }

    public enum AttributeGroups {
        ATTRIBUTE_HOLDING_MULTIPLE_PHYSICAL_ID {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        Attributes.UPT_BUSINESS_USE.getSelect(context),
                        Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context),
                        Attributes.UPT_BUSINESS_AREA.getSelect(context),
                        Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context),
                        Attributes.UPT_REGION.getSelect(context)
                );
            }
        },
        ATTRIBUTE_HOLDING_RECIPIENT_GROUP {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        Attributes.UPT_TASK_RECIPIENT_GROUP.getSelect(context)
                );
            }
        },
        ATTRIBUTE_HOLDING_RECIPIENT_MEMBER {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        Attributes.UPT_TASK_RECIPIENT_MEMBER.getSelect(context)
                );
            }
        },
        ATTRIBUTE_NORMAL {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        Attributes.UPT_MIGRATED.getSelect(context),
                        Attributes.UPT_TITLE.getSelect(context),
                        Attributes.UPT_POLICY.getSelect(context),
                        Attributes.UPT_DESCRIPTION.getSelect(context),
                        Attributes.UPT_CLASSIFICATION.getSelect(context),
                        Attributes.UPT_ROUTE_INSTRUCTION.getSelect(context),
                        Attributes.UPT_ROUTE_ACTION.getSelect(context)
                );
            }
        },
        BASIC {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        DomainConstants.SELECT_NAME,
                        DomainConstants.SELECT_ID,
                        DomainConstants.SELECT_POLICY,
                        DomainConstants.SELECT_PHYSICAL_ID,
                        DomainConstants.SELECT_TYPE,
                        DomainConstants.SELECT_DESCRIPTION,
                        DomainConstants.SELECT_OWNER
                );
            }
        };

        public abstract List<String> getAttributeList(Context context) throws MatrixException;
    }

    public enum Basic {
        FALSE_LITERAL("false"),
        FIELD_DESCRIPTION("description"),
        FIELD_TEMPLATE_DESC("templateDesc"),
        FIELD_TITLE("title"),
        FIELD_POLICY("policy"),
        FIELD_CLASSIFICATION("classification"),
        FIELD_BUSINESS_USE_OID("businessUseOID"),
        FIELD_HIGHLY_RESTRICTED_OID("highlyRestrictedOID"),
        FIELD_BUSINESS_AREA_PID("hiddenBusinessArea"),
        FIELD_SHARE_WITH_MEMBERS("MemberOID"),
        FIELD_SHARE_TEMPLATE_WITH("ShareTemplateOID"),
        FIELD_ROUTE_MEMBERS("memberFinalList"),
        FIELD_ROUTE_GROUPS("groupFinalList"),
        FIELD_ROUTE_INSTRUCTION("routeInstructions"),
        FIELD_ROUTE_ACTION("routeAction"),
        FIELD_REGION_OID("regionOID"),
        FIELD_NAME("templateName"),
        STRING_BUSINESS_USE("Business Use"),
        SYMBOL_RESTRICTED("IRM-R-"),
        SYMBOL_HIG_RESTRICTED("IRM-HiR-"),
        SYMBOL_PRJ_SUFFIX("_PRJ"),
        SYMBOL_TILDE("~"),
        SYMBOL_HYPHEN("-"),
        SYMBOL_PIPE("|"),
        FILTER("pgUserPreferenceTemplateIRMFilter"),
        FILTER_OWNED("Owned"),
        FILTER_SHARED("Shared"),
        OWNERSHIP("ownership"),
        FIELD_CHOICES("field_choices"),
        FIELD_DISPLAY_CHOICES("field_display_choices"),
        MODE("mode"),
        MODE_CREATE("create"),
        MODE_EDIT("edit"),
        STRING_YES("Yes"),
        STRING_NO("No"),
        STRING_ON("on"),
        MEMBER("member"),
        GROUP("group"),
        AUTO_NAME("templateAutoName"),
        STR_POLICY("Policy"),
        STR_BUSINESS_USE("Business Use"),
        //Added by DSM (Sogeti) for 2022x-04 Dec CW - Requirement #47851 Starts
        CLASSIFICATION_RESTRICTED("Highly Restricted"),
        CLASSIFICATION_BUSINESS_USE("Restricted"), 
        UPT_POLICY_SIGNATURE("Signature Reference"),
    	UPT_POLICY_SELF("Self Approval"),
    	POLICY_SIGNATURE("pgPKGSignatureReferenceDoc"),
    	POLICY_SELF("pgSelfApproval"),
    	STR_ROLE("role");
        //Added by DSM (Sogeti) for 2022x-04 Dec CW - Requirement #47851 Ends
        private final String name;

        Basic(String name) {
            this.name = name;
        }

        public String get() {
            return this.name;
        }

    }

}
