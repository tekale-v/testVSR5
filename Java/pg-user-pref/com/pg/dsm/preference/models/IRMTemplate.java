package com.pg.dsm.preference.models;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

import java.util.Map;
//Added by IRM(Sogeti) 2022x.04 Dec CW Requirement 47851 
public class IRMTemplate{
    DomainObject template;
    String templateId;
    String templateName;
    String templateTitle;
    String templateDesc;

    String uptPolicy;
    String uptTitle;
    String uptDescription;
    String uptClassification;
    StringList uptBusinessUseIPClass ;
    StringList uptHiRestrictedIPClass;
    StringList uptShareWithMembers;
    StringList uptBusinessArea;
    String uptBusinessAreaPID;
    String uptIsMigrated  ;
    StringList uptRouteTaskRecipientsMember;
    StringList uptRouteTaskRecipientsGroup;
    public StringList getUptRouteTaskRecipientsGroup() {
		return uptRouteTaskRecipientsGroup;
	}

	public void setUptRouteTaskRecipientsGroup(StringList uptRouteTaskRecipientsGroup) {
		this.uptRouteTaskRecipientsGroup = uptRouteTaskRecipientsGroup;
	}

	String uptRouteInstruction;
    String uptRouteAction;
    StringList uptRegion;
    String uptRegionOID;
    Context context;
    public IRMTemplate(){
    }

    public IRMTemplate(Context context){
        this.context = context;
    }

    public IRMTemplate(Context context, String templateId) throws FrameworkException {
        this.templateId = templateId;
        template = DomainObject.newInstance(context, this.templateId);
    }

    public IRMTemplate(Context context, Map attributeList) throws MatrixException {
       
        template = DomainObject.newInstance(context);
    }
    public DomainObject getTemplate() {
        return template;
    }

    public void setTemplate(DomainObject template) {
        this.template = template;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public void setTemplateTitle(String templateTitle) {
        this.templateTitle = templateTitle;
    }

    public String getTemplateDesc() {
        return templateDesc;
    }

    public void setTemplateDesc(String templateDesc) {
        this.templateDesc = templateDesc;
    }

    public String getUptPolicy() {
        return uptPolicy;
    }

    public void setUptPolicy(String uptPolicy) {
        this.uptPolicy = uptPolicy;
    }

    public String getUptTitle() {
        return uptTitle;
    }

    public void setUptTitle(String uptTitle) {
        this.uptTitle = uptTitle;
    }

    public String getUptDescription() {
        return uptDescription;
    }

    public void setUptDescription(String uptDescription) {
        this.uptDescription = uptDescription;
    }

    public String getUptClassification() {
        return uptClassification;
    }

    public void setUptClassification(String uptClassification) {
        this.uptClassification = uptClassification;
    }

    public StringList getUptBusinessUseIPClass() {
        return uptBusinessUseIPClass;
    }

    public void setUptBusinessUseIPClass(StringList uptBusinessUseIPClass) {
        this.uptBusinessUseIPClass = uptBusinessUseIPClass;
    }

    public StringList getUptHiRestrictedIPClass() {
        return uptHiRestrictedIPClass;
    }

    public void setUptHiRestrictedIPClass(StringList uptHiRestrictedIPClass) {
        this.uptHiRestrictedIPClass = uptHiRestrictedIPClass;
    }

    public StringList getUptShareWithMembers() {
        return uptShareWithMembers;
    }

    public void setUptShareWithMembers(StringList uptShareWithMembers) {
        this.uptShareWithMembers = uptShareWithMembers;
    }

    public StringList getUptBusinessArea() {
        return uptBusinessArea;
    }

    public void setUptBusinessArea(StringList uptBusinessArea) {
        this.uptBusinessArea = uptBusinessArea;
    }

    public String getUptBusinessAreaPID() {
        return uptBusinessAreaPID;
    }

    public void setUptBusinessAreaPID(String uptBusinessAreaPID) {
        this.uptBusinessAreaPID = uptBusinessAreaPID;
    }

    public String getUptIsMigrated() {
        return uptIsMigrated;
    }

    public void setUptIsMigrated(String uptIsMigrated) {
        this.uptIsMigrated = uptIsMigrated;
    }

    public StringList getUptRouteTaskRecipientsMember() {
        return uptRouteTaskRecipientsMember;
    }

    public void setUptRouteTaskRecipientsMember(StringList uptRouteTaskRecipientsMember) {
    	this.uptRouteTaskRecipientsMember = uptRouteTaskRecipientsMember;
    }

    public String getUptRouteInstruction() {
        return uptRouteInstruction;
    }

    public void setUptRouteInstruction(String uptRouteInstruction) {
        this.uptRouteInstruction = uptRouteInstruction;
    }

    public String getUptRouteAction() {
        return uptRouteAction;
    }

    public void setUptRouteAction(String uptRouteAction) {
        this.uptRouteAction = uptRouteAction;
    }

    public StringList getUptRegion() {
        return uptRegion;
    }

    public void setUptRegion(StringList uptRegion) {
        this.uptRegion = uptRegion;
    }

    public void setUptRegionOID(String uptRegionOID) {
        this.uptRegionOID = uptRegionOID;
    }

}
