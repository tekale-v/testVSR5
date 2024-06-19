package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("vehicle")
public class Vehicle {
	@XStreamAlias("selected")
	private Boolean selected;
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("units")
	private String units;
	@XStreamAlias("dims")
	private Dims dims;
	@XStreamAlias("info")
	private VehicleInfo info;
	
	public Vehicle() {
		super();
	}
	public Vehicle(Boolean selected, String name, String units) {
		super();
		this.selected = selected;
		this.name = name;
		this.units = units;
	}
	public Vehicle(Boolean selected, String name) {
		super();
		this.selected = selected;
		this.name = name;
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
	public Dims getDims() {
		return dims;
	}
	public void setDims(Dims dims) {
		this.dims = dims;
	}
	public VehicleInfo getInfo() {
		return info;
	}
	public void setInfo(VehicleInfo info) {
		this.info = info;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + selected + ", " + name + ", " + units + ", " + dims + ", " + info + "]";
	}
}
