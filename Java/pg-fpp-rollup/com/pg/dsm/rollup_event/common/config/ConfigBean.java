package com.pg.dsm.rollup_event.common.config;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

public class ConfigBean {

    String id;
    String type;
    String name;
    boolean isActive;
    int retryCount;
    String adminEmail;

    public ConfigBean(Map<Object, Object> objectMap) {
        setId((String) objectMap.get(DomainConstants.SELECT_ID));
        setType((String) objectMap.get(DomainConstants.SELECT_TYPE));
        setName((String) objectMap.get(DomainConstants.SELECT_NAME));
        setIsActive((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGISACTIVE));
        setRetryCount((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT));
        setAdminEmail((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONADMINMAILID));
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

    public void setIsActive(String activeValue) {
        if (UIUtil.isNotNullAndNotEmpty(activeValue)) {
            setActive(Boolean.parseBoolean(activeValue));
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(String retryCountValue) {
        if (UIUtil.isNotNullAndNotEmpty(retryCountValue)) {
            setRetryCount(Integer.parseInt(retryCountValue));
        }
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}
