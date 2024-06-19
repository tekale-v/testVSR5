package com.pg.v4.mos.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.util.mos.override.MOSOverrideConstants;

import matrix.util.StringList;

public class OverrideRequest {
	String type;
	String name;
	String revision;
	String id;
	String current;
	String relId;
	String requestType;
	StringList marketNameList;

	List<Market> marketList;

	boolean isFullOverride;

	public OverrideRequest(Map<Object, Object> objectMap) {
		this.type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
		this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);
		this.revision = (String) objectMap.get(DomainConstants.SELECT_REVISION);
		this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
		this.current = (String) objectMap.get(DomainConstants.SELECT_CURRENT);
		this.relId = (String) objectMap.get(DomainRelationship.SELECT_ID);
		this.requestType = (String) objectMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSOVERRIDEREQUESTTYPE);

		// market names are stored on attribute with pipe delimited.
		this.marketNameList = StringUtil.split((String) objectMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_OVERRIDE_COUNTRIES), pgV3Constants.SYMBOL_PIPE);

		// if map contains marketList, else define new list
		this.marketList = (objectMap.containsKey("marketList")) ? (List<Market>) objectMap.get("marketList") : new ArrayList<>();
		this.marketList = (null == this.marketList) ? new ArrayList<>() : this.marketList;
		this.isFullOverride = (UIUtil.isNotNullAndNotEmpty(this.requestType) && MOSOverrideConstants.KEY_FULL_OVERRIDE.equalsIgnoreCase(this.requestType)) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String getId() {
		return id;
	}

	public String getRequestType() {
		return requestType;
	}

	public StringList getMarketNameList() {
		return marketNameList;
	}

	public List<Market> getMarketList() {
		return marketList;
	}

	public String getRelId() {
		return relId;
	}

	public String getCurrent() {
		return current;
	}

	public boolean isFullOverride() {
		return isFullOverride;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getRevision() {
		return revision;
	}

	@Override
	public String toString() {
		return "OverrideRequest{" +
				"type='" + type + '\'' +
				", name='" + name + '\'' +
				", revision='" + revision + '\'' +
				", id='" + id + '\'' +
				", current='" + current + '\'' +
				", relId='" + relId + '\'' +
				", requestType='" + requestType + '\'' +
				", marketNameList=" + marketNameList +
				", marketList=" + marketList +
				", isFullOverride=" + isFullOverride +
				'}';
	}
}
