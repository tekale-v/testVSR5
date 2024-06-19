package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("info")
public class OutputInfo {
	@XStreamAlias("vert")
	private String vert;
	@XStreamAlias("wgtUnits")
	private String wgtUnits;
	@XStreamAlias("netWgt")
	private Double netWgt;
	@XStreamAlias("grsWgt")
	private Double grsWgt;
	@XStreamAlias("priPacks")
	private String priPacks;
	@XStreamAlias("ipacks")
	private String ipacks;
	@XStreamAlias("pattern")
	private String pattern;

	
	public OutputInfo() {
		super();
	}
	public OutputInfo(String vert, String wgtUnits, Double netWgt, Double grsWgt) {
		super();
		this.vert = vert;
		this.wgtUnits = wgtUnits;
		this.netWgt = netWgt;
		this.grsWgt = grsWgt;
	}
	public OutputInfo(String vert, String wgtUnits, Double netWgt, Double grsWgt, String priPacks) {
		super();
		this.vert = vert;
		this.wgtUnits = wgtUnits;
		this.netWgt = netWgt;
		this.grsWgt = grsWgt;
		this.priPacks = priPacks;
	}	
	public OutputInfo(String vert, String wgtUnits, Double netWgt, Double grsWgt, String pattern, String priPacks, String ipacks) {
		super();
		this.vert = vert;
		this.wgtUnits = wgtUnits;
		this.netWgt = netWgt;
		this.grsWgt = grsWgt;
		this.pattern = pattern;		
		this.priPacks = priPacks;
		this.ipacks = ipacks;
	}
	

	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getVert() {
		return vert;
	}
	public void setVert(String vert) {
		this.vert = vert;
	}
	public String getWgtUnits() {
		return wgtUnits;
	}
	public void setWgtUnits(String wgtUnits) {
		this.wgtUnits = wgtUnits;
	}
	public Double getNetWgt() {
		return netWgt;
	}
	public void setNetWgt(Double netWgt) {
		this.netWgt = netWgt;
	}
	public Double getGrsWgt() {
		return grsWgt;
	}
	public void setGrsWgt(Double grsWgt) {
		this.grsWgt = grsWgt;
	}
	public String getPriPacks() {
		return priPacks;
	}
	public void setPriPacks(String priPacks) {
		this.priPacks = priPacks;
	}
	public String getIpacks() {
		return ipacks;
	}
	public void setIpacks(String ipacks) {
		this.ipacks = ipacks;
	}
	

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + vert + ", "+ wgtUnits + ", " + netWgt + ", " + 
				grsWgt + ", " + pattern + ", " + priPacks + ", " + ipacks + "]";			
	}
}
