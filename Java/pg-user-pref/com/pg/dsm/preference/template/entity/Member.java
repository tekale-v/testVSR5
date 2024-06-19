package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;

public class Member {
    String name;
    String id;
    String physicalId;

    public Member(Map<Object, Object> objectMap) {
        this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);
        this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
        this.physicalId = (String) objectMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", physicalId='" + physicalId + '\'' +
                '}';
    }
}
