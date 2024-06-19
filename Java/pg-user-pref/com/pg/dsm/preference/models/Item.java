package com.pg.dsm.preference.models;

import java.util.Map;

public class Item {
    String physicalId;
    String type;
    String name;
    String revision;
    String category;
    String title;
    String preference;
    String person;
    String originated;
    String uptName;
    String uptPhysicalID;

    public Item(Map<String, String> objectMap) {
        this.physicalId = (String) objectMap.get("column[BusPhysicalId]");
        this.type = (String) objectMap.get("column[BusType]");
        this.name = (String) objectMap.get("column[BusName]");
        this.revision = (String) objectMap.get("column[BusRevision]");
        this.category = (String) objectMap.get("column[BusCategory]");
        this.title = (String) objectMap.get("column[BusTitle]");
        this.preference = (String) objectMap.get("column[PreferenceKey]");
        this.person = (String) objectMap.get("column[PersonKey]");
        this.originated = (String) objectMap.get("column[originated]");
        this.uptName = (String) objectMap.get("column[UPTName]");
        this.uptPhysicalID = (String) objectMap.get("column[UPTPhysicalID]");
    }

    public String getPhysicalId() {
        return physicalId;
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

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getPreference() {
        return preference;
    }

    public String getPerson() {
        return person;
    }

    public String getOriginated() {
        return originated;
    }

    public String getUPTName() {
        return uptName;
    }

    public String getUPTPhysicalID() {
        return uptPhysicalID;
    }
}
