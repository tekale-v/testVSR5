package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("input")
public class Input {
	@XStreamAlias("priPack")
	private PriPack priPack;
	@XStreamAlias("ipack")
	private IPack iPack;
	@XStreamAlias("shipper")
	private Shipper shipper;
	@XStreamAlias("unitload")
	private Unitload unitload;
	@XStreamAlias("vehicle")
	private Vehicle vehicle;
	
	public Input() {
		super();
	}
	public PriPack getPriPack() {
		return priPack;
	}
	public void setPriPack(PriPack priPack) {
		this.priPack = priPack;
	}
	public IPack getiPack() {
		return iPack;
	}
	public void setiPack(IPack iPack) {
		this.iPack = iPack;
	}
	public Shipper getShipper() {
		return shipper;
	}
	public void setShipper(Shipper shipper) {
		this.shipper = shipper;
	}
	public Unitload getUnitload() {
		return unitload;
	}
	public void setUnitload(Unitload unitload) {
		this.unitload = unitload;
	}
	public Vehicle getVehicle() {
		return vehicle;
	}
	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + priPack + ", "+ iPack + ", " + shipper + ", " + unitload + ", " + vehicle + "]";
	}
}
