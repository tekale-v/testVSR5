package com.pg.dsm.preference.template.entity;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.enumeration.UserPreferenceTemplateConstants;

public class Template {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    String owner;
    String ownerID;
    String name;
    String autoName;
    String type;
    String policy;
    String partCategory;
    String partType;
    String description;
    boolean migrated;
    Packaging packaging;
    Product product;
    RawMaterial rawMaterial;
    TechnicalSpecification technicalSpecification;
    Exploration exploration;
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    SupplierEquivalent supplierEquivalent;
    ManufacturingEquivalent manufacturingEquivalent;
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

    List<Member> memberList;
    List<Plant> plantList;
    Security security;
    Change change;
    List<TemplateMember> templateMemberList;

    boolean hasSecurity;
    boolean hasChange;
    boolean hasPlant;
    boolean hasMember;
    boolean hasPackaging;
    boolean hasProduct;
    boolean hasRawMaterial;
    boolean hasTechnicalSpecification;
    boolean hasExploration;
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    boolean hasSupplierEquivalent;
    boolean hasManufacturingEquivalent;
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

    boolean hasTemplateMembers;

    public Template(Map<Object, Object> objectMap) {
        logger.log(Level.INFO, "User Preference Template name: " + objectMap);
        this.type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
        this.policy = (String) objectMap.get(DomainConstants.SELECT_POLICY);
        this.owner = (String) objectMap.get(UserPreferenceTemplateConstants.TemplateFields.CONTEXT_USER_NAME.get());
        this.ownerID = (String) objectMap.get(UserPreferenceTemplateConstants.TemplateFields.CONTEXT_USER_ID.get());
        this.name = (String) objectMap.get(UserPreferenceTemplateConstants.TemplateFields.TEMPLATE_NAME.get());
        this.autoName = (String) objectMap.get(UserPreferenceTemplateConstants.TemplateFields.TEMPLATE_AUTO_NAME.get());
        this.partCategory = (String) objectMap.get(UserPreferenceTemplateConstants.TemplateFields.PART_CATEGORY.get());
        this.partType = (String) objectMap.get(UserPreferenceTemplateConstants.TemplateFields.PART_TYPE.get());
        this.description = (String) objectMap.get(UserPreferenceTemplateConstants.TemplateFields.DESCRIPTION.get());
        this.migrated = (objectMap.containsKey(PreferenceConstants.Basic.IS_MIGRATED.get())) ? Boolean.valueOf((String) objectMap.get(PreferenceConstants.Basic.IS_MIGRATED.get())) : Boolean.FALSE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Packaging getPackaging() {
        return packaging;
    }

    public void setPackaging(Packaging packaging) {
        this.packaging = packaging;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RawMaterial getRawMaterial() {
        return rawMaterial;
    }

    public void setRawMaterial(RawMaterial rawMaterial) {
        this.rawMaterial = rawMaterial;
    }

    public TechnicalSpecification getTechnicalSpecification() {
        return technicalSpecification;
    }

    public void setTechnicalSpecification(TechnicalSpecification technicalSpecification) {
        this.technicalSpecification = technicalSpecification;
    }

    public Exploration getExploration() {
        return exploration;
    }

    public void setExploration(Exploration exploration) {
        this.exploration = exploration;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public List<Plant> getPlantList() {
        return plantList;
    }

    public void setPlantList(List<Plant> plantList) {
        this.plantList = plantList;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Change getChange() {
        return change;
    }

    public void setChange(Change change) {
        this.change = change;
    }

    public boolean isHasSecurity() {
        return hasSecurity;
    }

    public void setHasSecurity(boolean hasSecurity) {
        this.hasSecurity = hasSecurity;
    }

    public boolean isHasChange() {
        return hasChange;
    }

    public void setHasChange(boolean hasChange) {
        this.hasChange = hasChange;
    }

    public boolean isHasPlant() {
        return hasPlant;
    }

    public void setHasPlant(boolean hasPlant) {
        this.hasPlant = hasPlant;
    }

    public boolean isHasMember() {
        return hasMember;
    }

    public void setHasMember(boolean hasMember) {
        this.hasMember = hasMember;
    }

    public boolean isHasPackaging() {
        return hasPackaging;
    }

    public void setHasPackaging(boolean hasPackaging) {
        this.hasPackaging = hasPackaging;
    }

    public boolean isHasProduct() {
        return hasProduct;
    }

    public void setHasProduct(boolean hasProduct) {
        this.hasProduct = hasProduct;
    }

    public boolean isHasRawMaterial() {
        return hasRawMaterial;
    }

    public void setHasRawMaterial(boolean hasRawMaterial) {
        this.hasRawMaterial = hasRawMaterial;
    }

    public boolean isHasTechnicalSpecification() {
        return hasTechnicalSpecification;
    }

    public void setHasTechnicalSpecification(boolean hasTechnicalSpecification) {
        this.hasTechnicalSpecification = hasTechnicalSpecification;
    }

    public boolean isHasExploration() {
        return hasExploration;
    }

    public void setHasExploration(boolean hasExploration) {
        this.hasExploration = hasExploration;
    }

    public String getPartCategory() {
        return partCategory;
    }

    public void setPartCategory(String partCategory) {
        this.partCategory = partCategory;
    }

    public String getPartType() { return partType; }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public List<TemplateMember> getTemplateMemberList() {
        return templateMemberList;
    }

    public void setTemplateMemberList(List<TemplateMember> templateMemberList) {
        this.templateMemberList = templateMemberList;
    }

    public boolean isHasTemplateMembers() {
        return hasTemplateMembers;
    }

    public void setHasTemplateMembers(boolean hasTemplateMembers) {
        this.hasTemplateMembers = hasTemplateMembers;
    }

    public String getAutoName() {
        return autoName;
    }

    public void setAutoName(String autoName) {
        this.autoName = autoName;
    }
    
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
    public void setManufacturingEquivalent(ManufacturingEquivalent manufacturingEquivalent) {
        this.manufacturingEquivalent = manufacturingEquivalent;
    }

	public void setHasManufacturingEquivalent(boolean hasManufacturingEquivalent) {
        this.hasManufacturingEquivalent = hasManufacturingEquivalent;
    }

    public void setSupplierEquivalent(SupplierEquivalent supplierEquivalent) {
        this.supplierEquivalent = supplierEquivalent;
    }

    public void setHasSupplierEquivalent(boolean hasSupplierEquivalent) {
        this.hasSupplierEquivalent = hasSupplierEquivalent;
    }
    
    public SupplierEquivalent getSupplierEquivalent() {
 		return supplierEquivalent;
 	}

 	public ManufacturingEquivalent getManufacturingEquivalent() {
 		return manufacturingEquivalent;
 	}

 	public boolean isHasSupplierEquivalent() {
 		return hasSupplierEquivalent;
 	}

 	public boolean isHasManufacturingEquivalent() {
 		return hasManufacturingEquivalent;
 	}
	//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END

    @Override
    public String toString() {
        return "Template{" +
                "owner='" + owner + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", name='" + name + '\'' +
                ", autoName='" + autoName + '\'' +
                ", type='" + type + '\'' +
                ", policy='" + policy + '\'' +
                ", partCategory='" + partCategory + '\'' +
                ", partType='" + partType + '\'' +
                ", description='" + description + '\'' +
                ", migrated=" + migrated +
                ", packaging=" + packaging +
                ", product=" + product +
                ", rawMaterial=" + rawMaterial +
                ", technicalSpecification=" + technicalSpecification +
                ", exploration=" + exploration +
				//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
                ", manufacturingEquivalent=" + manufacturingEquivalent +
                ", supplierEquivalent=" + supplierEquivalent +
				//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
                ", memberList=" + memberList +
                ", plantList=" + plantList +
                ", security=" + security +
                ", change=" + change +
                ", templateMemberList=" + templateMemberList +
                ", hasSecurity=" + hasSecurity +
                ", hasChange=" + hasChange +
                ", hasPlant=" + hasPlant +
                ", hasMember=" + hasMember +
                ", hasPackaging=" + hasPackaging +
                ", hasProduct=" + hasProduct +
                ", hasRawMaterial=" + hasRawMaterial +
                ", hasTechnicalSpecification=" + hasTechnicalSpecification +
                ", hasExploration=" + hasExploration +
				//Added by DSM for 22x CW-06 for Requirement #50186,50187 - START
                ", hasManufacturingEquivalent=" + hasManufacturingEquivalent +
                ", hasSupplierEquivalent=" + hasSupplierEquivalent +
				//Added by DSM for 22x CW-06 for Requirement #50186,50187 - END
                ", hasTemplateMembers=" + hasTemplateMembers +
                '}';
    }
}
