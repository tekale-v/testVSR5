package com.pg.dsm.preference.models;

public class Member {
    String id;
    String name;
    String role;

    String fullName;

    public Member(String id, String name, String fullName, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
