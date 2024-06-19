package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class Security {
    String businessUse;
    String businessUseDisplay;
    String businessUseOID;

    String businessUsePhysicalID;
    String highlyRestricted;
    String highlyRestrictedDisplay;
    String highlyRestrictedOID;
    String highlyRestrictedPhysicalID;
    String primaryOrganization;
    String primaryOrganizationDisplay;
    String primaryOrganizationOID;
    String primaryOrganizationPhysicalID;
    boolean hasSecurity;

    public Security(Map<Object, Object> objectMap) {

        UserPreferenceTemplateConstants.SecurityFields primaryOrganizationField = UserPreferenceTemplateConstants.SecurityFields.getByFieldIdentifier("PrimaryOrganization");
        UserPreferenceTemplateConstants.SecurityFields businessUseField = UserPreferenceTemplateConstants.SecurityFields.getByFieldIdentifier("BusinessUse");
        UserPreferenceTemplateConstants.SecurityFields highlyRestrictedField = UserPreferenceTemplateConstants.SecurityFields.getByFieldIdentifier("HighlyRestricted");

        this.businessUse = (objectMap.containsKey(businessUseField.getFieldName())) ? (String) objectMap.get(businessUseField.getFieldName()) : DomainConstants.EMPTY_STRING;
        this.businessUseDisplay = (objectMap.containsKey(businessUseField.getFieldDisplayName())) ? (String) objectMap.get(businessUseField.getFieldDisplayName()) : DomainConstants.EMPTY_STRING;
        this.businessUseOID = (objectMap.containsKey(businessUseField.getFieldOID())) ? (String) objectMap.get(businessUseField.getFieldOID()) : DomainConstants.EMPTY_STRING;
        this.businessUsePhysicalID = (objectMap.containsKey(businessUseField.getFieldPhysicalID())) ? (String) objectMap.get(businessUseField.getFieldPhysicalID()) : DomainConstants.EMPTY_STRING;

        this.highlyRestricted = (objectMap.containsKey(highlyRestrictedField.getFieldName())) ? (String) objectMap.get(highlyRestrictedField.getFieldName()) : DomainConstants.EMPTY_STRING;
        this.highlyRestrictedDisplay = (objectMap.containsKey(highlyRestrictedField.getFieldDisplayName())) ? (String) objectMap.get(highlyRestrictedField.getFieldDisplayName()) : DomainConstants.EMPTY_STRING;
        this.highlyRestrictedOID = (objectMap.containsKey(highlyRestrictedField.getFieldOID())) ? (String) objectMap.get(highlyRestrictedField.getFieldOID()) : DomainConstants.EMPTY_STRING;
        this.highlyRestrictedPhysicalID = (objectMap.containsKey(highlyRestrictedField.getFieldPhysicalID())) ? (String) objectMap.get(highlyRestrictedField.getFieldPhysicalID()) : DomainConstants.EMPTY_STRING;

        this.primaryOrganization = (objectMap.containsKey(primaryOrganizationField.getFieldName())) ? (String) objectMap.get(primaryOrganizationField.getFieldName()) : DomainConstants.EMPTY_STRING;
        this.primaryOrganizationDisplay = (objectMap.containsKey(primaryOrganizationField.getFieldName())) ? (String) objectMap.get(primaryOrganizationField.getFieldName()) : DomainConstants.EMPTY_STRING;
        this.primaryOrganizationOID = (objectMap.containsKey(primaryOrganizationField.getFieldOID())) ? (String) objectMap.get(primaryOrganizationField.getFieldOID()) : DomainConstants.EMPTY_STRING;
        this.primaryOrganizationPhysicalID = (objectMap.containsKey(primaryOrganizationField.getFieldPhysicalID())) ? (String) objectMap.get(primaryOrganizationField.getFieldPhysicalID()) : DomainConstants.EMPTY_STRING;

        this.hasSecurity = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
    }

    public String getBusinessUse() {
        return businessUse;
    }

    public String getBusinessUseDisplay() {
        return businessUseDisplay;
    }

    public String getBusinessUseOID() {
        return businessUseOID;
    }

    public String getHighlyRestricted() {
        return highlyRestricted;
    }

    public String getHighlyRestrictedDisplay() {
        return highlyRestrictedDisplay;
    }

    public String getHighlyRestrictedOID() {
        return highlyRestrictedOID;
    }

    public String getPrimaryOrganization() {
        return primaryOrganization;
    }

    public String getPrimaryOrganizationDisplay() {
        return primaryOrganizationDisplay;
    }

    public String getPrimaryOrganizationOID() {
        return primaryOrganizationOID;
    }

    public String getBusinessUsePhysicalID() {
        return businessUsePhysicalID;
    }

    public String getHighlyRestrictedPhysicalID() {
        return highlyRestrictedPhysicalID;
    }

    public String getPrimaryOrganizationPhysicalID() {
        return primaryOrganizationPhysicalID;
    }

    public boolean isHasSecurity() {
        return hasSecurity;
    }

    @Override
    public String toString() {
        return "Security{" +
                "businessUse='" + businessUse + '\'' +
                ", businessUseDisplay='" + businessUseDisplay + '\'' +
                ", businessUseOID='" + businessUseOID + '\'' +
                ", businessUsePhysicalID='" + businessUsePhysicalID + '\'' +
                ", highlyRestricted='" + highlyRestricted + '\'' +
                ", highlyRestrictedDisplay='" + highlyRestrictedDisplay + '\'' +
                ", highlyRestrictedOID='" + highlyRestrictedOID + '\'' +
                ", highlyRestrictedPhysicalID='" + highlyRestrictedPhysicalID + '\'' +
                ", primaryOrganization='" + primaryOrganization + '\'' +
                ", primaryOrganizationDisplay='" + primaryOrganizationDisplay + '\'' +
                ", primaryOrganizationOID='" + primaryOrganizationOID + '\'' +
                ", primaryOrganizationPhysicalID='" + primaryOrganizationPhysicalID + '\'' +
                ", hasSecurity=" + hasSecurity +
                '}';
    }
}
