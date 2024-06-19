package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("info")
public class VehicleInfo {
	@XStreamAlias("lenSlack")
	private Double lenSlack;
	@XStreamAlias("widSlack")
	private Double widSlack;
	@XStreamAlias("hgtSlack")
	private Double hgtSlack;
	@XStreamAlias("maxWgt")
	private Double maxWgt;
	
	public VehicleInfo() {
		super();
	}
	public VehicleInfo(Double lenSlack, Double widSlack, Double hgtSlack,
			Double maxWgt) {
		super();
		this.lenSlack = lenSlack;
		this.widSlack = widSlack;
		this.hgtSlack = hgtSlack;
		this.maxWgt = maxWgt;
	}
	public Double getLenSlack() {
		return lenSlack;
	}
	public void setLenSlack(Double lenSlack) {
		this.lenSlack = lenSlack;
	}
	public Double getWidSlack() {
		return widSlack;
	}
	public void setWidSlack(Double widSlack) {
		this.widSlack = widSlack;
	}
	public Double getHgtSlack() {
		return hgtSlack;
	}
	public void setHgtSlack(Double hgtSlack) {
		this.hgtSlack = hgtSlack;
	}
	public Double getMaxWgt() {
		return maxWgt;
	}
	public void setMaxWgt(Double maxWgt) {
		this.maxWgt = maxWgt;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + lenSlack + ", " + widSlack + ", " + hgtSlack + ", " + maxWgt + "]";
	}
}
