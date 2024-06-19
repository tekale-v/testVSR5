package com.pg.dsm.preference.models;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.v3.custom.pgV3Constants;

public class Plant {
    String physicalId;
    String id;
    String type;
    String name;
    String revision;

    String authorized;
    String activated;
    String authorizedToUse;
    String authorizedToProduce;

    public Plant(Map<Object, Object> objectMap) {
        this.physicalId = (String) objectMap.get(PreferenceConstants.Basic.PHYSICAL_ID.get());
        this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
        this.type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
        this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);
        this.revision = (String) objectMap.get(DomainConstants.SELECT_REVISION);

        this.authorized = (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOVIEW);
        this.authorized = (UIUtil.isNotNullAndNotEmpty(this.authorized) && pgV3Constants.CAPS_TRUE.equalsIgnoreCase(this.authorized)) ? PreferenceConstants.Basic.YES.get() : PreferenceConstants.Basic.NO.get();

        this.activated = (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);
        this.activated = (UIUtil.isNotNullAndNotEmpty(this.activated) && pgV3Constants.CAPS_TRUE.equalsIgnoreCase(this.activated)) ? PreferenceConstants.Basic.YES.get() : PreferenceConstants.Basic.NO.get();

        this.authorizedToUse = (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
        this.authorizedToUse = (UIUtil.isNotNullAndNotEmpty(this.authorizedToUse) && pgV3Constants.CAPS_TRUE.equalsIgnoreCase(this.authorizedToUse)) ? PreferenceConstants.Basic.YES.get() : PreferenceConstants.Basic.NO.get();

        this.authorizedToProduce = (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
        this.authorizedToProduce = (UIUtil.isNotNullAndNotEmpty(this.authorizedToProduce) && pgV3Constants.CAPS_TRUE.equalsIgnoreCase(this.authorizedToProduce)) ? PreferenceConstants.Basic.YES.get() : PreferenceConstants.Basic.NO.get();
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
