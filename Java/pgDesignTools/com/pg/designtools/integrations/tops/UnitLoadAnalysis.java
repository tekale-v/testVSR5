package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("analysis0")
public class UnitLoadAnalysis {
	
	//unique to TOPS Analysis
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("infomation")
	private UnitLoadAnalysisInfo analysisInfo;
	@XStreamAlias("CubeSpec")
	private UnitLoadAnalysisSpec analysisSpec;
	
	@XStreamAlias("priPack")
	private PriPack priPack;
	
	@XStreamAlias("shipper")
	private Shipper shipper;
	
	@XStreamAlias("unitload")
	private Unitload unitload;
	
	@XStreamAlias("ipack")
	private IPack ipack;
	
	//Pranjali Duplicate this to outputInfo or Output.java
	//ENOVIA Mapping
	@XStreamAlias("TUP")
	private TUP emTUP;
	@XStreamAlias("MCOP")
	private MCOP emMCOP;
	@XStreamAlias("MCUP")
	private MCUP emMCUP;
	@XStreamAlias("MIP")
	private MCUP emMIP;	
	
	public PriPack getPriPack() {
		return priPack;
	}
	public void setPriPack(PriPack priPack) {
		this.priPack = priPack;
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
	public IPack getIPack() {
		return ipack;
	}
	public void setIPack(IPack iPack) {
		this.ipack = iPack;
	}
	
	public UnitLoadAnalysisInfo getanalysisInfo() {
		return analysisInfo;
	}
	public void setanalysisInfo(UnitLoadAnalysisInfo analysisInfo) {
		this.analysisInfo = analysisInfo;
	}
	
	public UnitLoadAnalysisSpec getanalysisSpec() {
		return analysisSpec;
	}
	public void setanalysisSpec(UnitLoadAnalysisSpec analysisSpec) {
		this.analysisSpec = analysisSpec;
	}
	
	public MCOP getMCOP() {
		return null;
	}


	public UnitLoadAnalysis() {
		super();
	}
	
	
	public String toString() {
		return this.getClass().getSimpleName() + " [" +  "]";
	}

}
