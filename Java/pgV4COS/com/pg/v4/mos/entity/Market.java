package com.pg.v4.mos.entity;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainRelationship;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.util.mos.override.MOSOverrideConstants;

public class Market {

	String type;
	String name;
	String revision;
	String id;
	String current;
	String relId;
	String partialOverrideLRR;
	String restriction;

	public Market(Map<Object, Object> objectMap) {
		this.type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
		this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);
		this.revision = (String) objectMap.get(DomainConstants.SELECT_REVISION);
		this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
		this.current = (String) objectMap.get(DomainConstants.SELECT_CURRENT);
		this.relId = (String) objectMap.get(DomainRelationship.SELECT_ID);
		this.partialOverrideLRR = (objectMap.containsKey(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR)) ? (String) objectMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR) : DomainConstants.EMPTY_STRING;
		this.restriction = (objectMap.containsKey("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]")) ? (String) objectMap.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]") : DomainConstants.EMPTY_STRING;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRelId() {
		return relId;
	}

	public String getPartialOverrideLRR() {
		return partialOverrideLRR;
	}

	public String getRestriction() {
		return restriction;
	}

	public String getType() {
		return type;
	}

	public String getRevision() {
		return revision;
	}

	public String getCurrent() {
		return current;
	}

	@Override
	public String toString() {
		return "Market{" +
				"type='" + type + '\'' +
				", name='" + name + '\'' +
				", revision='" + revision + '\'' +
				", id='" + id + '\'' +
				", current='" + current + '\'' +
				", relId='" + relId + '\'' +
				", partialOverrideLRR='" + partialOverrideLRR + '\'' +
				", restriction='" + restriction + '\'' +
				'}';
	}
}
