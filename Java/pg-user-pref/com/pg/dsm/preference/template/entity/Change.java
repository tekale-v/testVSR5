package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;
import com.pg.dsm.preference.template.DSMUserPreferenceTemplateUtil;

public class Change {

    String inWorkRouteTemplate;
    String inWorkRouteTemplateDisplay;
    String inWorkRouteTemplateOID;
    String inWorkRouteTemplatePhysicalID;

    String inApprovalRouteTemplate;
    String inApprovalRouteTemplateDisplay;
    String inApprovalRouteTemplateOID;
    String inApprovalRouteTemplatePhysicalID;
    String changeTemplate;
    String changeTemplateDisplay;
    String changeTemplateOID;
    String changeTemplatePhysicalID;
    String informedUser;
    String informedUserDisplay;
    String informedUserOID;
    String informedUserPhysicalID;

    boolean hasChange;

    public Change(Map<Object, Object> objectMap) {
        UserPreferenceTemplateConstants.ChangeActionFields inWorkRouteTemplateField = UserPreferenceTemplateConstants.ChangeActionFields.getByFieldIdentifier("InWorkRouteTemplate");
        UserPreferenceTemplateConstants.ChangeActionFields inApprovalRouteTemplateField = UserPreferenceTemplateConstants.ChangeActionFields.getByFieldIdentifier("InApprovalRouteTemplate");
        UserPreferenceTemplateConstants.ChangeActionFields changeTemplateField = UserPreferenceTemplateConstants.ChangeActionFields.getByFieldIdentifier("ChangeTemplate");
        UserPreferenceTemplateConstants.ChangeActionFields informedUserField = UserPreferenceTemplateConstants.ChangeActionFields.getByFieldIdentifier("InformedUser");
        this.inWorkRouteTemplate = (String) objectMap.get(inWorkRouteTemplateField.getFieldName());
        this.inWorkRouteTemplateDisplay = (String) objectMap.get(inWorkRouteTemplateField.getFieldDisplayName());
        this.inWorkRouteTemplateOID = (String) objectMap.get(inWorkRouteTemplateField.getFieldOID());
        this.inWorkRouteTemplatePhysicalID = (String) objectMap.get(inWorkRouteTemplateField.getFieldPhysicalID());

        this.inApprovalRouteTemplate = (String) objectMap.get(inApprovalRouteTemplateField.getFieldName());
        this.inApprovalRouteTemplateDisplay = (String) objectMap.get(inApprovalRouteTemplateField.getFieldDisplayName());
        this.inApprovalRouteTemplateOID = (String) objectMap.get(inApprovalRouteTemplateField.getFieldOID());
        this.inApprovalRouteTemplatePhysicalID = (String) objectMap.get(inApprovalRouteTemplateField.getFieldPhysicalID());

        this.changeTemplate = (String) objectMap.get(changeTemplateField.getFieldName());
        this.changeTemplateDisplay = (String) objectMap.get(changeTemplateField.getFieldDisplayName());
        this.changeTemplateOID = (String) objectMap.get(changeTemplateField.getFieldOID());
        this.changeTemplatePhysicalID = (String) objectMap.get(changeTemplateField.getFieldPhysicalID());

        this.informedUser = (String) objectMap.get(informedUserField.getFieldName());
        this.informedUserDisplay = (String) objectMap.get(informedUserField.getFieldDisplayName());
        this.informedUserOID = (String) objectMap.get(informedUserField.getFieldOID());
        this.informedUserPhysicalID = (String) objectMap.get(informedUserField.getFieldPhysicalID());

        this.hasChange = DSMUserPreferenceTemplateUtil.atleastOneKeyHasValue(objectMap);
    }

    public String getInWorkRouteTemplate() {
        return inWorkRouteTemplate;
    }

    public String getInWorkRouteTemplateDisplay() {
        return inWorkRouteTemplateDisplay;
    }

    public String getInWorkRouteTemplateOID() {
        return inWorkRouteTemplateOID;
    }

    public String getInWorkRouteTemplatePhysicalID() {
        return inWorkRouteTemplatePhysicalID;
    }

    public String getInApprovalRouteTemplate() {
        return inApprovalRouteTemplate;
    }

    public String getInApprovalRouteTemplateDisplay() {
        return inApprovalRouteTemplateDisplay;
    }

    public String getInApprovalRouteTemplateOID() {
        return inApprovalRouteTemplateOID;
    }

    public String getInApprovalRouteTemplatePhysicalID() {
        return inApprovalRouteTemplatePhysicalID;
    }

    public String getChangeTemplate() {
        return changeTemplate;
    }

    public String getChangeTemplateDisplay() {
        return changeTemplateDisplay;
    }

    public String getChangeTemplateOID() {
        return changeTemplateOID;
    }

    public String getChangeTemplatePhysicalID() {
        return changeTemplatePhysicalID;
    }

    public String getInformedUser() {
        return informedUser;
    }

    public String getInformedUserDisplay() {
        return informedUserDisplay;
    }

    public String getInformedUserOID() {
        return informedUserOID;
    }

    public String getInformedUserPhysicalID() {
        return informedUserPhysicalID;
    }

    public boolean isHasChange() {
        return hasChange;
    }

    @Override
    public String toString() {
        return "Change{" +
                "inWorkRouteTemplate='" + inWorkRouteTemplate + '\'' +
                ", inWorkRouteTemplateDisplay='" + inWorkRouteTemplateDisplay + '\'' +
                ", inWorkRouteTemplateOID='" + inWorkRouteTemplateOID + '\'' +
                ", inWorkRouteTemplatePhysicalID='" + inWorkRouteTemplatePhysicalID + '\'' +
                ", inApprovalRouteTemplate='" + inApprovalRouteTemplate + '\'' +
                ", inApprovalRouteTemplateDisplay='" + inApprovalRouteTemplateDisplay + '\'' +
                ", inApprovalRouteTemplateOID='" + inApprovalRouteTemplateOID + '\'' +
                ", inApprovalRouteTemplatePhysicalID='" + inApprovalRouteTemplatePhysicalID + '\'' +
                ", changeTemplate='" + changeTemplate + '\'' +
                ", changeTemplateDisplay='" + changeTemplateDisplay + '\'' +
                ", changeTemplateOID='" + changeTemplateOID + '\'' +
                ", changeTemplatePhysicalID='" + changeTemplatePhysicalID + '\'' +
                ", informedUser='" + informedUser + '\'' +
                ", informedUserDisplay='" + informedUserDisplay + '\'' +
                ", informedUserOID='" + informedUserOID + '\'' +
                ", informedUserPhysicalID='" + informedUserPhysicalID + '\'' +
                ", hasChange=" + hasChange +
                '}';
    }
}
