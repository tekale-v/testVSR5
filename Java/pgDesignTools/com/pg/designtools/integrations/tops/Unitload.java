package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("unitload")
public class Unitload {
	@XStreamAlias("selected")
	private Boolean selected;
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("units")
	private String units;
	@XStreamAlias("info")
	private UnitloadInfo info;
	@XStreamAlias("lenUnits")
	private String lenUnits;
	@XStreamAlias("wgtUnits")
	private String wgtUnits;
	@XStreamAlias("EnoviaMap")
	private String enoviaMap;
	@XStreamAlias("palType")
	private String palType;
	
	public Unitload() {
		super();
	}
	public Unitload(Boolean selected, String name, String units) {
		super();
		this.selected = selected;
		this.name = name;
		this.units = units;
	}
	public Unitload(Boolean selected, String name, String palType, String units, String lenUnits, String wgtUnits) {
		super();
		this.selected = selected;
		this.name = name;
		this.palType = palType;
		this.units = units;
		this.lenUnits = lenUnits;
		this.wgtUnits = wgtUnits;
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public UnitloadInfo getInfo() {
		return info;
	}
	public void setInfo(UnitloadInfo info) {
		this.info = info;
	}
	public String getlenUnits() {
		return lenUnits;
	}
	public void setlenUnits(String lenUnits) {
		this.lenUnits = lenUnits;
	}
	public String getWgtUnits() {
		return wgtUnits;
	}
	public void setWgtUnits(String wgtUnits) {
		this.wgtUnits = wgtUnits;
	}
	public String getEnoviaMap() {
		return enoviaMap;
	}
	public void setEnoviaMap(String enoviaMap) {
		this.enoviaMap = enoviaMap;
	}
	public String getPalType() {
		return palType;
	}
	public void setPalType(String palType) {
		this.palType = palType;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + selected + ", " + name + ", " + info + ", " + units + ","+ lenUnits + "," + wgtUnits +"]";
	}
}
