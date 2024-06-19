package com.pg.dsm.preference.models;

import java.util.Map;

import com.pg.dsm.preference.enumeration.PlantPreferenceTable;

public class PlantItem {
    String physicalId;
    String type;
    String name;
    String revision;
    String category;
    String categoryType;
    String person;
    String originated;

    String authorized;
    String activated;
    String authorizedToUse;
    String authorizedToProduce;

    public PlantItem(Map<String, String> objectMap) {
        this.physicalId = (String) objectMap.get(PlantPreferenceTable.Columns.PHYSICAL_ID.getSelectColumn());
        this.type = (String) objectMap.get(PlantPreferenceTable.Columns.TYPE.getSelectColumn());
        this.name = (String) objectMap.get(PlantPreferenceTable.Columns.NAME.getSelectColumn());
        this.revision = (String) objectMap.get(PlantPreferenceTable.Columns.REVISION.getSelectColumn());
        this.category = (String) objectMap.get(PlantPreferenceTable.Columns.CATEGORY.getSelectColumn());
        this.categoryType = (String) objectMap.get(PlantPreferenceTable.Columns.CATEGORY_TYPE.getSelectColumn());
        this.person = (String) objectMap.get(PlantPreferenceTable.Columns.PERSON_KEY.getSelectColumn());
        this.originated = (String) objectMap.get(PlantPreferenceTable.Columns.ORIGINATED.getSelectColumn());

        this.authorized = (String) objectMap.get(PlantPreferenceTable.Columns.AUTHORIZED.getSelectColumn());
        this.activated = (String) objectMap.get(PlantPreferenceTable.Columns.ACTIVATED.getSelectColumn());
        this.authorizedToUse = (String) objectMap.get(PlantPreferenceTable.Columns.AUTHORIZED_TO_USE.getSelectColumn());
        this.authorizedToProduce = (String) objectMap.get(PlantPreferenceTable.Columns.AUTHORIZED_TO_PRODUCE.getSelectColumn());
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getOriginated() {
        return originated;
    }

    public void setOriginated(String originated) {
        this.originated = originated;
    }

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }

    public String getAuthorizedToUse() {
        return authorizedToUse;
    }

    public void setAuthorizedToUse(String authorizedToUse) {
        this.authorizedToUse = authorizedToUse;
    }

    public String getAuthorizedToProduce() {
        return authorizedToProduce;
    }

    public void setAuthorizedToProduce(String authorizedToProduce) {
        this.authorizedToProduce = authorizedToProduce;
    }
}
