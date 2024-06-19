/*
 **   Part.java
 **   Description - Introduced as part June CW 2022 for Material Group Code (MGC) - Requirement (39763, 39765, 39767, 39764)
 **   About - Bean class.
 **
 */
package com.pg.dsm.sap.mgc.beans;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.sap.mgc.enumeration.MaterialGroupCode;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;

import java.util.Map;

public class Part {
    String id;
    String type;
    String name;
    String revision;
    String symbolicType;
    String attributeClass;
    String attributeSubClass;
    String attributeReportFunction;
    String attributePackagingComponentType;
    String attributePackagingMaterialType;
    String attributePackagingTechnology;
    String attributePrimaryOrganization; // Added by DSM (Sogeti) 22x.04 for REQ 48068
    String attributeChemicalGroup;
    String attributeCasNumber;
    public Part(Context context,  Map<Object, Object> objectMap) {
        this.id = (String)objectMap.get(DomainConstants.SELECT_ID);
        this.type = (String)objectMap.get(DomainConstants.SELECT_TYPE);
        this.name = (String)objectMap.get(DomainConstants.SELECT_NAME);
        this.revision = (String)objectMap.get(DomainConstants.SELECT_REVISION);
        this.symbolicType = (String)objectMap.get(DomainConstants.SELECT_ATTRIBUTE_SYMBOLIC_NAME);

        this.attributeClass = (String)objectMap.get(MaterialGroupCode.Attribute.PG_CLASS.getSelect(context));
        this.attributeSubClass = (String)objectMap.get(MaterialGroupCode.Attribute.PG_SUB_CLASS.getSelect(context));
        this.attributeReportFunction = (Boolean.parseBoolean((String) objectMap.get(pgV3Constants.HAS_FROM_RELATIONSHIP_TEMPLATE_TO_REPORTED_FUNCTION))) ? (String) objectMap.get(pgV3Constants.SELECT_FROM_RELATIONSHIP_TEMPLATE_TO_REPORTED_FUNCTION_TO_NAME) : DomainConstants.EMPTY_STRING;
        this.attributePackagingComponentType = (String)objectMap.get(MaterialGroupCode.Attribute.PG_PACKAGING_COMPONENT_TYPE.getSelect(context));
        this.attributePackagingMaterialType = (String)objectMap.get(MaterialGroupCode.Attribute.PG_PACKAGING_MATERIAL_TYPE.getSelect(context));
        this.attributePackagingTechnology = (String)objectMap.get(MaterialGroupCode.Attribute.PG_PACKAGING_TECHNOLOGY.getSelect(context));
        this.attributePrimaryOrganization = (Boolean.parseBoolean((String) objectMap.get(MaterialGroupCode.Basic.SELECT_HAS_FROM_RELATIONSHIP_PRIMARY_ORGANIZATION.getValue()))) ? (String) objectMap.get(MaterialGroupCode.Basic.SELECT_FROM_RELATIONSHIP_PRIMARY_ORGANIZATION_TO_NAME.getValue()) : DomainConstants.EMPTY_STRING; // Added by DSM (Sogeti) 22x.04 for REQ 48068
        this.attributeChemicalGroup = (String)objectMap.get(MaterialGroupCode.Attribute.PG_CHEMICAL_GROUP.getSelect(context));
        this.attributeCasNumber = (String)objectMap.get(MaterialGroupCode.Attribute.PG_CAS_NUMBER.getSelect(context));
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getRevision() {
        return revision;
    }

    public String getAttributeClass() {
        return attributeClass;
    }

    public String getAttributeSubClass() {
        return attributeSubClass;
    }

    public String getAttributeReportFunction() {
        return attributeReportFunction;
    }

    public String getAttributePackagingComponentType() {
        return attributePackagingComponentType;
    }

    public String getAttributePackagingMaterialType() {
        return attributePackagingMaterialType;
    }

    public String getAttributePackagingTechnology() {
        return attributePackagingTechnology;
    }

    public String getAttributeChemicalGroup() {
        return attributeChemicalGroup;
    }

    public String getAttributeCasNumber() {
        return attributeCasNumber;
    }

    public String getSymbolicType() {
        return symbolicType;
    }
    
    public String getAttributePrimaryOrganization() { // Added by DSM (Sogeti) 22x.04 for REQ 48068
		return attributePrimaryOrganization;
	}

	@Override
	public String toString() {
		return "Part{" +
				"id='" + id + '\'' +
				", type='" + type + '\'' +
				", name='" + name + '\'' +
				", revision='" + revision + '\'' +
				", symbolicType='" + symbolicType + '\'' +
				", attributeClass='" + attributeClass + '\'' +
				", attributeSubClass='" + attributeSubClass + '\'' +
				", attributeReportFunction='" + attributeReportFunction + '\'' +
				", attributePackagingComponentType='" + attributePackagingComponentType + '\'' +
				", attributePackagingMaterialType='" + attributePackagingMaterialType + '\'' +
				", attributePackagingTechnology='" + attributePackagingTechnology + '\'' +
				", attributePrimaryOrganization='" + attributePrimaryOrganization + '\'' +
				", attributeChemicalGroup='" + attributeChemicalGroup + '\'' +
				", attributeCasNumber='" + attributeCasNumber + '\'' +
				'}';
	}
}
