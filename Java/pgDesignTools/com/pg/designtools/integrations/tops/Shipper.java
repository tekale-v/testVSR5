package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("shipper")
public class Shipper {
	@XStreamAlias("selected")
	private Boolean selected;
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("type")
	private String type;
	@XStreamAlias("units")
	private String units;
	@XStreamAlias("fixVar")
	private String fixVar;
	@XStreamAlias("roundUp")
	private String roundUp;
	@XStreamAlias("dims")
	private Dims dims;
	@XStreamAlias("lenUnits")
	private String lenUnits;
	@XStreamAlias("wgtUnits")
	private String wgtUnits;
	@XStreamAlias("info")
	private ShipperInfo info;
	@XStreamAlias("EnoviaMap")
	private String enoviaMap;
	
	public Shipper() {
		super();
	}
	public Shipper(Boolean selected, String name, String type, String units, String fixVar,
			String roundUp) {
		super();
		this.selected = selected;
		this.name = name;
		this.type = type;
		this.units = units;
		this.fixVar = fixVar;
		this.roundUp = roundUp;
	}
	public Shipper(Boolean selected, String name, String type, String units, String fixVar,
			String roundUp, String lenUnits, String wgtUnits) {
		super();
		this.selected = selected;
		this.name = name;
		this.type = type;
		this.units = units;
		this.fixVar = fixVar;
		this.roundUp = roundUp;
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
	public String getFixVar() {
		return fixVar;
	}
	public void setFixVar(String fixVar) {
		this.fixVar = fixVar;
	}
	public String getRoundUp() {
		return roundUp;
	}
	public void setRoundUp(String roundUp) {
		this.roundUp = roundUp;
	}
	public Dims getDims() {
		return dims;
	}
	public void setDims(Dims dims) {
		this.dims = dims;
	}
	public ShipperInfo getInfo() {
		return info;
	}
	public void setInfo(ShipperInfo info) {
		this.info = info;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLenUnits() {
		return lenUnits;
	}
	public void setLenUnits(String lenUnits) {
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
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + selected + ", " + name + ", " + type + ", " + units + ", " + fixVar + ", " + roundUp + ", " + dims + ", " + info + ","+ lenUnits + "," + wgtUnits +"]";
	}
}
