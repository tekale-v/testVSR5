package com.pg.dsm.preference.template.entity;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.v3.custom.pgV3Constants;

public class Plant {
    String name;
    String id;
    String physicalId;
    String activated;
    String authorizedToUse;
    String authorizedToProduce;
    String authorized;

    public Plant(Map<Object, Object> objectMap) {
        this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);
        this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
        this.physicalId = (String) objectMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get());
        this.activated = (String) objectMap.get(pgV3Constants.ATTRIBUTE_PGISACTIVATED);
        this.authorizedToUse = (String) objectMap.get(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOUSE);
        this.authorizedToProduce = (String) objectMap.get(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
        this.authorized = (String) objectMap.get(pgV3Constants.ATTRIBUTE_PGISAUTHORIZEDTOVIEW); // to-do: check if its correct attribute?
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

    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    @Override
    public String toString() {
        return "Plant{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", physicalId='" + physicalId + '\'' +
                ", activated='" + activated + '\'' +
                ", authorizedToUse='" + authorizedToUse + '\'' +
                ", authorizedToProduce='" + authorizedToProduce + '\'' +
                ", authorized='" + authorized + '\'' +
                '}';
    }
}
