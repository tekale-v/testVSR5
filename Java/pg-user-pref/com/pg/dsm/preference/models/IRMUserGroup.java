package com.pg.dsm.preference.models;
//Added by IRM(Sogeti) 2022x.04 Dec CW Requirement 47851 
public class IRMUserGroup {
    String name;
    String id;

    public IRMUserGroup(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "UserGroup{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
