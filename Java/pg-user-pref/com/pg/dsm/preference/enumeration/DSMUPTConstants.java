package com.pg.dsm.preference.enumeration;

import java.util.List;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.dsm.preference.config.xml.PreferenceConfig;
import com.pg.dsm.preference.template.apply.PackagingUserPreferenceTemplateService;
import com.pg.dsm.preference.template.apply.ProductUserPreferenceTemplateService;
import com.pg.dsm.preference.template.apply.RawMaterialUserPreferenceTemplateService;

import matrix.db.Context;
import matrix.util.MatrixException;

public class DSMUPTConstants {

    public enum PartCategory {

        PACKAGING("PKG") {
            @Override
            public boolean isAccessAllowed(Context context, String attributeName) {
                boolean ret = Boolean.FALSE;
                DSMUPTConstants.PackagingAttributeGroup[] attributes = DSMUPTConstants.PackagingAttributeGroup.values();
                for (DSMUPTConstants.PackagingAttributeGroup attribute : attributes) {
                    if (attributeName.equalsIgnoreCase(attribute.getName(context))) {
                        ret = Boolean.TRUE;
                        break;
                    }
                }
                return ret;
            }

            @Override
            public void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception {
                new PackagingUserPreferenceTemplateService.Apply(context, preferenceConfig.getPackagingPreferenceConfig()).now(partObj, partOid, partSymbolicType, templatePhysicalID);
            }


        },
        PRODUCT("PRD") {
            @Override
            public boolean isAccessAllowed(Context context, String attributeName) {
                boolean ret = Boolean.FALSE;
                DSMUPTConstants.ProductAttributeGroup[] attributes = DSMUPTConstants.ProductAttributeGroup.values();
                for (DSMUPTConstants.ProductAttributeGroup attribute : attributes) {
                    if (attributeName.equalsIgnoreCase(attribute.getName(context))) {
                        ret = Boolean.TRUE;
                        break;
                    }
                }
                return ret;
            }

            @Override
            public void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception {
                new ProductUserPreferenceTemplateService.Apply(context, preferenceConfig.getProductPreferenceConfig()).now(partObj, partOid, partSymbolicType, templatePhysicalID);
            }
        },
        RAW_MATERIAL("RM") {
            @Override
            public boolean isAccessAllowed(Context context, String attributeName) {
                boolean ret = Boolean.FALSE;
                DSMUPTConstants.RawMaterialAttributeGroup[] attributes = DSMUPTConstants.RawMaterialAttributeGroup.values();
                for (DSMUPTConstants.RawMaterialAttributeGroup attribute : attributes) {
                    if (attributeName.equalsIgnoreCase(attribute.getName(context))) {
                        ret = Boolean.TRUE;
                        break;
                    }
                }
                return ret;
            }

            @Override
            public void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception {
                new RawMaterialUserPreferenceTemplateService.Apply(context, preferenceConfig.getRawMaterialPreferenceConfig()).now(partObj, partOid, partSymbolicType, templatePhysicalID);
            }
        },
        TECH_SPEC("TS") {
            @Override
            public boolean isAccessAllowed(Context context, String attributeName) {
                boolean ret = Boolean.FALSE;
                DSMUPTConstants.TechSpecAttributeGroup[] attributes = DSMUPTConstants.TechSpecAttributeGroup.values();
                for (DSMUPTConstants.TechSpecAttributeGroup attribute : attributes) {
                    if (attributeName.equalsIgnoreCase(attribute.getName(context))) {
                        ret = Boolean.TRUE;
                        break;
                    }
                }
                return ret;
            }

            @Override
            public void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception {
                // implement in future.
            }
        },
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
        MANUFACTURING_EQUIVALENT("MEP") {
            @Override
            public boolean isAccessAllowed(Context context, String attributeName) {
                boolean ret = Boolean.FALSE;
                DSMUPTConstants.MEPAttributeGroup[] attributes = DSMUPTConstants.MEPAttributeGroup.values();
                for (DSMUPTConstants.MEPAttributeGroup attribute : attributes) {
                    if (attributeName.equalsIgnoreCase(attribute.getName(context))) {
                        ret = Boolean.TRUE;
                        break;
                    }
                }
                return ret;
            }

            @Override
            public void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception {
                // implement in future.
            }
        },
        SUPLLIER_EQUIVALENT("SEP") {
            @Override
            public boolean isAccessAllowed(Context context, String attributeName) {
                boolean ret = Boolean.FALSE;
                DSMUPTConstants.SEPAttributeGroup[] attributes = DSMUPTConstants.SEPAttributeGroup.values();
                for (DSMUPTConstants.SEPAttributeGroup attribute : attributes) {
                    if (attributeName.equalsIgnoreCase(attribute.getName(context))) {
                        ret = Boolean.TRUE;
                        break;
                    }
                }
                return ret;
            }

            @Override
            public void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception {
                // implement in future.
            }
        },
		//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
        EXPLORATION("EXP") {
            @Override
            public boolean isAccessAllowed(Context context, String attributeName) {
                return false;
            }

            @Override
            public void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception {
                // implement in future.
            }
        };

        private final String name;

        PartCategory(String name) {
            this.name = name;
        }

        public static PartCategory getPartCategoryFromName(String categoryName) {
            for (PartCategory category : PartCategory.values()) {
                if (category.getName().equals(categoryName)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("No PartCategory enum value found for name: " + categoryName);
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        public abstract boolean isAccessAllowed(Context context, String attributeName);

        public abstract void propagateAttributes(Context context, PreferenceConfig preferenceConfig, String partOid, String partSymbolicType, DomainObject partObj, String templatePhysicalID) throws Exception;
    }


    public enum Attributes {
        UPT_SHARE_WITH_MEMBERS("attribute_pgUPTShareWithMembers"), // multiple (physical id)
        UPT_BUSINESS_USE("attribute_pgUPTBusinessUseIPClass"), // multiple (physical id)
        UPT_HIGHLY_RESTRICTED("attribute_pgUPTHighlyRestrictedIPClass"), // multiple (physical id)
        UPT_BUSINESS_AREA("attribute_pgUPTBusinessArea"), // multiple (physical id)
        UPT_ROUTE_TEMPLATE_IN_WORK("attribute_pgUPTInWork"), // multiple (physical id)
        UPT_ROUTE_TEMPLATE_IN_APPROVAL("attribute_pgUPTInApproval"), // multiple (physical id)
        UPT_INFORMED_USERS("attribute_pgUPTInformedUsers"), // multiple (object id)
        UPT_MATERIAL_FUNCTION("attribute_pgUPTMaterialFunction"), // multiple (physical id)
        UPT_PRODUCT_CATEGORY_PLATFORM("attribute_pgUPTProductCategoryPlatform"), // multiple (physical id)
        UPT_REPORTED_FUNCTION("attribute_pgUPTReportedFunction"), // single (physical id)
        UPT_SEGMENT("attribute_pgUPTSegment"), // single (physical id)
        UPT_PRIMARY_ORGANIZATION("attribute_pgUPTPrimaryOrganization"), // single (physical id)
        UPT_CHANGE_TEMPLATE("attribute_pgUPTChangeTemplate"), // single (object id)

        UPT_DESCRIPTION("attribute_pgUPTDescription"),
        UPT_TITLE("attribute_Title"),
        UPT_MIGRATED("attribute_pgUPTIsMigrated"),
        UPT_CLASS("attribute_pgUPTClass"),
        UPT_RELEASE_PHASE("attribute_pgUPTReleasePhase"),
        UPT_MFG_STATUS("attribute_pgUPTLifeCycleStatus"),
        UPT_RELEASE_CRITERIA_REQUIRED("attribute_pgUPTStructuredReleaseCriteriaRequired"),
        UPT_PACKAGING_COMPONENT_TYPE("attribute_pgUPTPackagingComponentType"),
        UPT_PACKAGING_MATERIAL_TYPE("attribute_pgUPTPackagingMaterialType"),
        UPT_BASE_UNIT_OF_MEASURE("attribute_pgUPTBaseUnitOfMeasure"),
        UPT_PART_TYPE("attribute_pgUPTPartType"),
        UPT_PART_CATEGORY("attribute_pgUPTPartCategory"),
        UPT_PRODUCT_COMPLIANCE_REQUIRED("attribute_pgUPTIsProductComplianceRequired");
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
        ATTRIBUTE_HOLDING_SINGLE_PHYSICAL_ID {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        DSMUPTConstants.Attributes.UPT_PRIMARY_ORGANIZATION.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_CHANGE_TEMPLATE.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_REPORTED_FUNCTION.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_SEGMENT.getSelect(context)
                );
            }
        },
        ATTRIBUTE_HOLDING_MULTIPLE_PHYSICAL_ID {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        DSMUPTConstants.Attributes.UPT_BUSINESS_USE.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_HIGHLY_RESTRICTED.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_BUSINESS_AREA.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_WORK.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_ROUTE_TEMPLATE_IN_APPROVAL.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_MATERIAL_FUNCTION.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_PRODUCT_CATEGORY_PLATFORM.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_INFORMED_USERS.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_SHARE_WITH_MEMBERS.getSelect(context)
                );
            }
        },
        ATTRIBUTE_NORMAL {
            @Override
            public List<String> getAttributeList(Context context) throws MatrixException {
                return List.of(
                        DSMUPTConstants.Attributes.UPT_RELEASE_PHASE.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_MFG_STATUS.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_RELEASE_CRITERIA_REQUIRED.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_PACKAGING_COMPONENT_TYPE.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_PACKAGING_MATERIAL_TYPE.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_BASE_UNIT_OF_MEASURE.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_CLASS.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_PRODUCT_COMPLIANCE_REQUIRED.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_PART_TYPE.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_PART_CATEGORY.getSelect(context),
                        DSMUPTConstants.Attributes.UPT_MIGRATED.getSelect(context)
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
                        DomainConstants.SELECT_OWNER
                );
            }
        };

        public abstract List<String> getAttributeList(Context context) throws MatrixException;
    }

    public enum PackagingAttributeGroup {
        UPT_RELEASE_CRITERIA_REQUIRED("attribute_pgUPTStructuredReleaseCriteriaRequired"),
        UPT_CLASS("attribute_pgUPTClass"),
        UPT_REPORTED_FUNCTION("attribute_pgUPTReportedFunction"), // single (physical id)
        UPT_SEGMENT("attribute_pgUPTSegment"), // single (physical id)
        UPT_PACKAGING_COMPONENT_TYPE("attribute_pgUPTPackagingComponentType"),
        UPT_PACKAGING_MATERIAL_TYPE("attribute_pgUPTPackagingMaterialType"),
        UPT_BASE_UNIT_OF_MEASURE("attribute_pgUPTBaseUnitOfMeasure");
        private final String name;

        PackagingAttributeGroup(String name) {
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

    public enum ProductAttributeGroup {
        UPT_RELEASE_CRITERIA_REQUIRED("attribute_pgUPTStructuredReleaseCriteriaRequired"),
        UPT_CLASS("attribute_pgUPTClass"),
        UPT_REPORTED_FUNCTION("attribute_pgUPTReportedFunction"), // single (physical id)
        UPT_SEGMENT("attribute_pgUPTSegment"), // single (physical id)
        UPT_BUSINESS_AREA("attribute_pgUPTBusinessArea"), // multiple (physical id)
        UPT_PRODUCT_CATEGORY_PLATFORM("attribute_pgUPTProductCategoryPlatform"), // multiple (physical id)
        UPT_PRODUCT_COMPLIANCE_REQUIRED("attribute_pgUPTIsProductComplianceRequired");
        private final String name;

        ProductAttributeGroup(String name) {
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

    public enum RawMaterialAttributeGroup {
        UPT_RELEASE_CRITERIA_REQUIRED("attribute_pgUPTStructuredReleaseCriteriaRequired"),
        UPT_CLASS("attribute_pgUPTClass"),
        UPT_REPORTED_FUNCTION("attribute_pgUPTReportedFunction"), // single (physical id)
        UPT_SEGMENT("attribute_pgUPTSegment"), // single (physical id)
        UPT_BUSINESS_AREA("attribute_pgUPTBusinessArea"), // multiple (physical id)
        UPT_PRODUCT_CATEGORY_PLATFORM("attribute_pgUPTProductCategoryPlatform"), // multiple (physical id)
        UPT_MATERIAL_FUNCTION("attribute_pgUPTMaterialFunction"); // multiple (physical id)

        private final String name;

        RawMaterialAttributeGroup(String name) {
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

    public enum TechSpecAttributeGroup {
        UPT_SEGMENT("attribute_pgUPTSegment"); // single (physical id)

        private final String name;

        TechSpecAttributeGroup(String name) {
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
    
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    public enum MEPAttributeGroup {
        UPT_VENDOR("attribute_pgUPTVendor"); // single (physical id)

        private final String name;

        MEPAttributeGroup(String name) {
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

    public enum SEPAttributeGroup {
        UPT_VENDOR("attribute_pgUPTVendor"); // single (physical id)

        private final String name;

        SEPAttributeGroup(String name) {
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
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
}
