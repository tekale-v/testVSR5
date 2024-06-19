/*
 **   tops.jar
 **
 **   Copyright (c) 1992-2010 Dassault Systemes IS
 **   All Rights Reserved.
 **   This program contains proprietary and trade secret information of MatrixOne,
 **   Inc.  Copyright notice is precautionary only
 **   and does not evidence any actual or intended publication of such program
 **
 */
/*
Project Name: TOPS Integration
Class Name: pgPKGVPMUtil
Purpose: Defines XML processing for TOPS XML input tags
Change History:

Date            Author          Build           ChgID/Req or QC         Details
-----------------------------------------------------------------------------------------------------------------------------
20-Jan-15       xxw (DS)        v2013x.4         Chg01/ALM6440         Added the following tags: maxOHLen, maxOHWid, maxUHLeh, maxUHWid
                                                                       
 */
package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("info")
public class OutputUnitloadInfo {
	@XStreamAlias("volumeUnits")
	private String volumeUnits;
	@XStreamAlias("volume")
	private Double volume;
	@XStreamAlias("wgtUnits")
	private String wgtUnits;
	@XStreamAlias("netWgt")
	private Double netWgt;
	@XStreamAlias("grsWgt")
	private Double grsWgt;
	@XStreamAlias("grsWgtWithoutPallet")
	private Double grsWgtWithoutPallet;
	@XStreamAlias("areaEff")
	private String areaEff;
	@XStreamAlias("cubicEff")
	private String cubicEff;
	@XStreamAlias("layersPerLoad")
	private String layersPerLoad;
	@XStreamAlias("shippersPerlayer")
	private String shippersPerlayer;
	@XStreamAlias("ipackssPerlayer")
	private String ipackssPerlayer;
	@XStreamAlias("pripacksPerlayer")
	private String pripacksPerlayer;
	@XStreamAlias("palletsPerlayer")
	private String palletsPerlayer;
	@XStreamAlias("shippers")
	private String shippers;
	@XStreamAlias("ipacks")
	private String ipacks;
	@XStreamAlias("priPacks")
	private String priPacks;
	@XStreamAlias("RSCArea")
	private Double rscArea;
	@XStreamAlias("palletLayersPerTruck")
	private String palletLayersPerTruck;
	@XStreamAlias("pattern")
	private String pattern;
	// DSO : Chg 01 Start - Tag Names: maxOHLen, maxOHWid, maxUHLeh, maxUHWid
	@XStreamAlias("maxOHLen")
	private Double maxOHLen;
	@XStreamAlias("maxOHWid")
	private Double maxOHWid;
	@XStreamAlias("maxUHLen")
	private Double maxUHLen;
	@XStreamAlias("maxUHWid")
	private Double maxUHWid;
	// DSO : Chg 01 End

	public OutputUnitloadInfo() {
		super();
	}

	// DSO : Chg 01 Start - Tag Names: maxOHLen, maxOHWid, maxUHLeh, maxUHWid
	public OutputUnitloadInfo(String volumeUnits, Double volume, String wgtUnits, Double netWgt, Double grsWgt,
			Double grsWgtWithoutPallet, String areaEff, String cubicEff, String pattern, String layersPerLoad,
			String shippersPerlayer, String ipackssPerlayer, String pripacksPerlayer, String palletsPerlayer,
			String shippers, String ipacks, String priPacks, Double rscArea, String palletLayersPerTruck,
			Double maxOHLen, Double maxOHWid, Double maxUHLen, Double maxUHWid) {
		// DSO : Chg 01 End
		super();
		this.volumeUnits = volumeUnits;
		this.volume = volume;
		this.wgtUnits = wgtUnits;
		this.netWgt = netWgt;
		this.grsWgt = grsWgt;
		this.grsWgtWithoutPallet = grsWgtWithoutPallet;
		this.areaEff = areaEff;
		this.cubicEff = cubicEff;
		this.pattern = pattern;
		this.layersPerLoad = layersPerLoad;
		this.shippersPerlayer = shippersPerlayer;
		this.ipackssPerlayer = ipackssPerlayer;
		this.pripacksPerlayer = pripacksPerlayer;
		this.palletsPerlayer = palletsPerlayer;
		this.shippers = shippers;
		this.ipacks = ipacks;
		this.priPacks = priPacks;
		this.rscArea = rscArea;
		this.palletLayersPerTruck = palletLayersPerTruck;
		// DSO : Chg 01 Start - Tag Names: maxOHLen, maxOHWid, maxUHLeh, maxUHWid
		this.maxOHLen = maxOHLen;
		this.maxOHWid = maxOHWid;
		this.maxUHLen = maxUHLen;
		this.maxUHWid = maxUHWid;
		// DSO : Chg 01 End
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getVolumeUnits() {
		return volumeUnits;
	}

	public void setVolumeUnits(String volumeUnits) {
		this.volumeUnits = volumeUnits;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
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

	public Double getGrsWgtWithoutPallet() {
		return grsWgtWithoutPallet;
	}

	public void setGrsWgtWithoutPallet(Double grsWgtWithoutPallet) {
		this.grsWgtWithoutPallet = grsWgtWithoutPallet;
	}

	public String getAreaEff() {
		return areaEff;
	}

	public void setAreaEff(String areaEff) {
		this.areaEff = areaEff;
	}

	public String getCubicEff() {
		return cubicEff;
	}

	public void setCubicEff(String cubicEff) {
		this.cubicEff = cubicEff;
	}

	public String getLayersPerLoad() {
		return layersPerLoad;
	}

	public void setLayersPerLoad(String layersPerLoad) {
		this.layersPerLoad = layersPerLoad;
	}

	public String getShippersPerlayer() {
		return shippersPerlayer;
	}

	public void setShippersPerlayer(String shippersPerlayer) {
		this.shippersPerlayer = shippersPerlayer;
	}

	public String getIpackssPerlayer() {
		return ipackssPerlayer;
	}

	public void setIpackssPerlayer(String ipackssPerlayer) {
		this.ipackssPerlayer = ipackssPerlayer;
	}

	public String getPripacksPerlayer() {
		return pripacksPerlayer;
	}

	public void setPripacksPerlayer(String pripacksPerlayer) {
		this.pripacksPerlayer = pripacksPerlayer;
	}

	public String getPalletsPerlayer() {
		return palletsPerlayer;
	}

	public void setPalletsPerlayer(String palletsPerlayer) {
		this.palletsPerlayer = palletsPerlayer;
	}

	public String getShippers() {
		return shippers;
	}

	public void setShippers(String shippers) {
		this.shippers = shippers;
	}

	public String getIpacks() {
		return ipacks;
	}

	public void setIpacks(String ipacks) {
		this.ipacks = ipacks;
	}

	public String getPriPacks() {
		return priPacks;
	}

	public void setPriPacks(String priPacks) {
		this.priPacks = priPacks;
	}

	public Double getRSCArea() {
		return rscArea;
	}

	public void setRSCArea(Double rSCArea) {
		rscArea = rSCArea;
	}

	public String getPalletLayersPerTruck() {
		return palletLayersPerTruck;
	}

	public void setPalletLayersPerTruck(String palletLayersPerTruck) {
		this.palletLayersPerTruck = palletLayersPerTruck;
	}

	// DSO : Chg 01 Start - Tag Names: maxOHLen, maxOHWid, maxUHLeh, maxUHWid
	public Double getMaxOHLen() {
		return maxOHLen;
	}

	public void setMaxOHLen(Double maxOHLen) {
		this.maxOHLen = maxOHLen;
	}

	public Double getMaxOHWid() {
		return maxOHWid;
	}

	public void setMaxOHWid(Double maxOHWid) {
		this.maxOHWid = maxOHWid;
	}

	public Double getMaxUHLen() {
		return maxUHLen;
	}

	public void setMaxUHLen(Double maxUHLen) {
		this.maxUHLen = maxUHLen;
	}

	public Double getMaxUHWid() {
		return maxUHWid;
	}

	public void setMaxUHWid(Double maxUHWid) {
		this.maxUHWid = maxUHWid;
	}

	// DSO : Chg 01 End
	// DSO : Chg 01 Start - Tag Names: maxOHLen, maxOHWid, maxUHLeh, maxUHWid
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + volumeUnits + ", " + volume + ", " + wgtUnits + ", " + netWgt
				+ ", " + grsWgt + ", " + grsWgtWithoutPallet + ", " + areaEff + "," + pattern + "," + cubicEff + ", "
				+ layersPerLoad + "," + shippersPerlayer + ", " + ipackssPerlayer + ", " + pripacksPerlayer + ","
				+ palletsPerlayer + ", " + shippers + "," + ipacks + ", " + priPacks + ", " + rscArea + ","
				+ palletLayersPerTruck + ", " + maxOHLen + ", " + maxOHWid + ", " + maxUHLen + ", " + maxUHWid + "]";
	}
	// DSO : Chg 01 End
}
