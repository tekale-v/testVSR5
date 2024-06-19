package com.pg.dsm.preference.models;

public class UserGroup {
    String name;
    String id;

    public UserGroup(String id, String name) {
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
