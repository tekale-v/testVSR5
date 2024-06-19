package com.pg.dsm.rollup_event.common.ebom;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;

public class Substitute {
    String id;
    String type;
    String physicalId;
    String relId;

    public Substitute(Map<?, ?> busMap) {
        this.id = (String) busMap.get(DomainConstants.SELECT_ID);
        this.type = (String) busMap.get(DomainConstants.SELECT_TYPE);
        this.relId = (String) busMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
        this.physicalId = (String) busMap.get(RollupConstants.Basic.PHYSICAL_ID.getValue());
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

    public String getRelId() {
        return relId;
    }

    public void setRelId(String relId) {
        this.relId = relId;
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }
}
