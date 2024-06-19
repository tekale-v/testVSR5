package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("input")
public class Output {
	@XStreamAlias("priPack")
	private OutputPriPack priPack;
	@XStreamAlias("ipack")
	private OutputIPack iPack;
	@XStreamAlias("shipper")
	private OutputShipper shipper;
	@XStreamAlias("unitload")
	private OutputUnitload unitload;

	//ENOVIA Mapping
	@XStreamAlias("TUP")
	private TUP emTUP;
	@XStreamAlias("MCOP")
	private MCOP emMCOP;
	@XStreamAlias("MCUP")
	private MCUP emMCUP;
	@XStreamAlias("MIP")
	private MIP emMIP;	
	
	public Output() {
		super();
	}

	public OutputPriPack getPriPack() {
		return priPack;
	}

	public void setPriPack(OutputPriPack priPack) {
		this.priPack = priPack;
	}

	public OutputIPack getiPack() {
		return iPack;
	}

	public void setiPack(OutputIPack iPack) {
		this.iPack = iPack;
	}

	public OutputShipper getShipper() {
		return shipper;
	}

	public void setShipper(OutputShipper shipper) {
		this.shipper = shipper;
	}

	public OutputUnitload getUnitload() {
		return unitload;
	}

	public void setUnitload(OutputUnitload unitload) {
		this.unitload = unitload;
	}

	public TUP getEmTUP() {
		return emTUP;
	}

	public void setEmTUP(TUP emTUP) {
		this.emTUP = emTUP;
	}

	public MCOP getEmMCOP() {
		return emMCOP;
	}

	public void setEmMCOP(MCOP emMCOP) {
		this.emMCOP = emMCOP;
	}

	public MCUP getEmMCUP() {
		return emMCUP;
	}

	public void setEmMCUP(MCUP emMCUP) {
		this.emMCUP = emMCUP;
	}

	public MIP getEmMIP() {
		return emMIP;
	}

	public void setEmMIP(MIP emMIP) {
		this.emMIP = emMIP;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + priPack + ", "+ iPack + ", " + shipper + ", " + unitload +"]";
	}
}
