/*
 **   MaterialGroupCode.java
 **   Description - Introduced as part June CW 2022 for Material Group Code (MGC) - Requirement (39763, 39765, 39767, 39764)
 **   About - Bean class.
 **
 */
package com.pg.dsm.sap.mgc.enumeration;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.dsm.sap.mgc.beans.InclusionType;
import com.pg.dsm.sap.mgc.beans.MaterialGroup;
import com.pg.dsm.sap.mgc.beans.MaterialGroups;
import com.pg.dsm.sap.mgc.beans.Part;
import com.pg.dsm.sap.mgc.utils.MaterialGroupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

import java.util.List;
import java.util.Optional;

public class MaterialGroupCode {
    public enum Basic {
        SELECT_HAS_FROM_RELATIONSHIP_PRIMARY_ORGANIZATION("from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "]"),
        SELECT_FROM_RELATIONSHIP_PRIMARY_ORGANIZATION_TO_NAME("from[" + pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION + "].to.name"),
        MATERIAL_GROUP_CODE("Material Group Code"),
        SYMBOL_SPACE(" "),
        CUSTOM("custom");
        private final String name;
        Basic(String name) {
            this.name = name;
        }
        public String getValue() {
            return this.name;
        }
    }
    public enum Attribute {
        PG_CLASS("attribute_pgClass"),
        PG_SUB_CLASS("attribute_pgSubClass"),
        PG_REPORTED_FUNCTION("attribute_pgReportedFunction"),
        PG_PACKAGING_COMPONENT_TYPE("attribute_pgPackagingComponentType"),
        PG_PACKAGING_MATERIAL_TYPE("attribute_pgPackagingMaterialType"),
        PG_PACKAGING_TECHNOLOGY("attribute_pgPackagingTechnology"),
        PG_CHEMICAL_GROUP("attribute_pgChemicalGroup"),
        PG_CAS_NUMBER("attribute_pgCASNumber");

        private final String name;

        Attribute(String name) {
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

    public enum ApplicableAttribute {
        ATTRIBUTE_CLASS_TYPE {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getClassType().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    if (materialGroup.getClassType().equalsIgnoreCase(part.getAttributeClass())) {
                        ret = true;
                    }
                }
                return ret;
            }
        },
        ATTRIBUTE_SUB_CLASS_TYPE {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getSubClass().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    if (materialGroup.getSubClass().equalsIgnoreCase(part.getAttributeSubClass())) {
                        ret = true;
                    }
                }
                return ret;
            }
        },
        ATTRIBUTE_REPORTED_FUNCTION {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {

                boolean ret = Boolean.FALSE;
                if (materialGroup.getReportFunction().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    ret = (materialGroup.getReportFunction().equalsIgnoreCase(part.getAttributeReportFunction())) ? true : false;
                }
                return ret;
            }
        },
        ATTRIBUTE_PACKAGING_COMPONENT_TYPE {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getPackagingComponentType().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    ret = (materialGroup.getPackagingComponentType().equalsIgnoreCase(part.getAttributePackagingComponentType())) ? true : false;
                }

                return ret;
            }

        },
        ATTRIBUTE_PACKAGING_MATERIAL_TYPE {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getPackagingMaterialType().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    ret = (materialGroup.getPackagingMaterialType().equalsIgnoreCase(part.getAttributePackagingMaterialType())) ? true : false;
                }
                return ret;
            }
        },
        ATTRIBUTE_PACKAGING_TECHNOLOGY {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getPackagingTechnology().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    ret = (materialGroup.getPackagingTechnology().equalsIgnoreCase(part.getAttributePackagingTechnology())) ? true : false;
                }
                return ret;
            }
        },
        ATTRIBUTE_PRIMARY_ORGANIZATION { // Added by DSM (Sogeti) 22x.04 for REQ 48068
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getPrimaryOrganization().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    ret = (materialGroup.getPrimaryOrganization().equalsIgnoreCase(part.getAttributePrimaryOrganization())) ? true : false;
                }
                return ret;
            }
        },
        ATTRIBUTE_CHEMICAL_GROUP {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getChemicalGroup().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    ret = (materialGroup.getChemicalGroup().equalsIgnoreCase(part.getAttributeChemicalGroup())) ? true : false;
                }
                return ret;
            }
        },
        ATTRIBUTE_CASE_NUMBER {
            @Override
            public boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
                boolean ret = Boolean.FALSE;
                if (materialGroup.getCas().equalsIgnoreCase(DomainConstants.QUERY_WILDCARD)) {
                    ret = true;
                } else {
                    ret = (materialGroup.getCas().equalsIgnoreCase(part.getAttributeCasNumber())) ? true : false;
                }
                return ret;
            }
        };

        ApplicableAttribute() {
        }

        /**
         * @param part
         * @param materialGroup
         * @param inclusionType
         * @return
         */
        public abstract boolean evaluate(Part part, MaterialGroup materialGroup, InclusionType inclusionType);
    }

    public enum ApplicableType {
        //these constants should be type's symbolic name in capitals.
        TYPE_PACKAGINGMATERIALPART {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_PACKAGINGASSEMBLYPART {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_RAWMATERIAL {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_PGPACKINGMATERIAL {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_PGRAWMATERIAL {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_PGFABRICATEDPART {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_FORMULATIONPART {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_PGDEVICEPRODUCTPART {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_PGASSEMBLEDPRODUCTPART {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        },
        TYPE_FINISHEDPRODUCTPART {
            @Override
            public String getMaterialGroupCode(Part part, MaterialGroups materialGroups) {
                String code = DomainConstants.EMPTY_STRING;
                final InclusionType inclusionType = materialGroups.getInclusionType().get(0);
                final List<MaterialGroup> materialGroupList = materialGroups.getMaterialGroup();
                final Optional<MaterialGroup> optionalMaterialGroupCode = materialGroupList.stream().filter(materialGroupCode -> MaterialGroupUtil.evaluateCriteria(part, materialGroupCode, inclusionType)).findFirst();
                if (optionalMaterialGroupCode.isPresent()) {
                    code = optionalMaterialGroupCode.get().getCode();
                }
                return code;
            }
        };

        public abstract String getMaterialGroupCode(Part part, MaterialGroups materialGroups);

        public static String getMaterialGroupCodeByType(Part part, MaterialGroups materialGroups) {
            String code = DomainConstants.EMPTY_STRING;
            // search enum constants with symbolic type name in upper case.
            final ApplicableType applicableType = ApplicableType.valueOf(part.getSymbolicType().toUpperCase());
            if (null != applicableType) {
                code = applicableType.getMaterialGroupCode(part, materialGroups);
            }
            return code;
        }
    }
}
