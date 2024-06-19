/*
 **   MarketConfig.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.mx;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarketConfig {
    @JsonProperty("type")
    String type;
    @JsonProperty("attribute[pgConfigCommonAdminMailId]")
    String configCommonAdminMailId;
    @JsonProperty("attribute[pgConfigCommonIsActive]")
    String configCommonIsActive;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfigCommonAdminMailId() {
        return configCommonAdminMailId;
    }

    public void setConfigCommonAdminMailId(String configCommonAdminMailId) {
        this.configCommonAdminMailId = configCommonAdminMailId;
    }

    public String getConfigCommonIsActive() {
        return configCommonIsActive;
    }

    public void setConfigCommonIsActive(String configCommonIsActive) {
        this.configCommonIsActive = configCommonIsActive;
    }
}
