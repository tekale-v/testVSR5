package com.pg.v4.mos.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.util.mos.override.MOSOverrideConstants;

public class Part {
	String type;
	String name;
	String revision;
	String id;
	String current;
	String previousId;
	boolean isFullOverride;
	boolean isPartialOverride;
	List<Market> marketList;
	List<OverrideRequest> overrideRequestList;

	boolean hasMarket;
	boolean hasOverride;

	public Part(Map<Object, Object> objectMap) {
		this.type = (String) objectMap.get(DomainConstants.SELECT_TYPE);
		this.name = (String) objectMap.get(DomainConstants.SELECT_NAME);
		this.revision = (String) objectMap.get(DomainConstants.SELECT_REVISION);
		this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
		this.current = (String) objectMap.get(DomainConstants.SELECT_CURRENT);
		this.previousId = (String) objectMap.get("previous.id");

		// is pgHasCOSFPPOverridden is Yes then its full override.
		this.isFullOverride = (pgV3Constants.KEY_YES_VALUE).equalsIgnoreCase((String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN));

		// if pgHasMOSPartialOverridden is No, then is partial override.
		this.isPartialOverride = (pgV3Constants.KEY_YES_VALUE).equalsIgnoreCase((String) objectMap.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN));

		// if map contains overrideRequestList, else define new list
		this.overrideRequestList = (objectMap.containsKey("overrideRequestList")) ? (List<OverrideRequest>) objectMap.get("overrideRequestList") : new ArrayList<>();
		this.overrideRequestList = (null == this.overrideRequestList) ? new ArrayList<>() : this.overrideRequestList;
		// if map contains marketList, else define new list
		this.marketList = (objectMap.containsKey("marketList")) ? (List<Market>) objectMap.get("marketList") : new ArrayList<>();
		this.marketList = (null == this.marketList) ? new ArrayList<>() : this.marketList;

		// if marketList is not null or empty then define has market boolean true, else false.
		this.hasMarket = (null != marketList && !marketList.isEmpty()) ? Boolean.TRUE : Boolean.FALSE;

		// if overrideRequestList is not null or empty then define has override boolean true, else false
		this.hasOverride = (null != overrideRequestList && !overrideRequestList.isEmpty()) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String getId() {
		return id;
	}

	public String getCurrent() {
		return current;
	}

	public String getPreviousId() {
		return previousId;
	}

	public boolean isFullOverride() {
		return isFullOverride;
	}

	public boolean isPartialOverride() {
		return isPartialOverride;
	}

	public List<OverrideRequest> getOverrideRequestList() {
		return overrideRequestList;
	}

	public List<Market> getMarketList() {
		return marketList;
	}

	public boolean isHasMarket() {
		return hasMarket;
	}

	public boolean isHasOverride() {
		return hasOverride;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getRevision() {
		return revision;
	}

	@Override
	public String toString() {
		return "Part{" +
				"type='" + type + '\'' +
				", name='" + name + '\'' +
				", revision='" + revision + '\'' +
				", id='" + id + '\'' +
				", current='" + current + '\'' +
				", previousId='" + previousId + '\'' +
				", isFullOverride=" + isFullOverride +
				", isPartialOverride=" + isPartialOverride +
				", marketList=" + marketList +
				", overrideRequestList=" + overrideRequestList +
				", hasMarket=" + hasMarket +
				", hasOverride=" + hasOverride +
				'}';
	}
}
