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
20-Jan-15       xxw (DS)        v2013x.4         Chg01/ALM6440         Added the following tags for locking mechanism: lockType
                                                                       
 */
package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("info")
public class ShipperInfo {
	@XStreamAlias("material")
	private String material;
	@XStreamAlias("flute")
	private String flute;
	@XStreamAlias("caliper")
	private Double caliper;
	@XStreamAlias("lenVert")
	private String lenVert;
	@XStreamAlias("widVert")
	private String widVert;
	@XStreamAlias("hgtVert")
	private String hgtVert;
	@XStreamAlias("netWgt")
	private Double netWgt;
	@XStreamAlias("grsWgt")
	private Double grsWgt;
	@XStreamAlias("maxWgt")
	private Double maxWgt;
	@XStreamAlias("lenBulge")
	private Double lenBulge;
	@XStreamAlias("widBulge")
	private Double widBulge;
	@XStreamAlias("hgtBulge")
	private Double hgtBulge;
	@XStreamAlias("lenSlack")
	private Double lenSlack;
	@XStreamAlias("widSlack")
	private Double widSlack;
	@XStreamAlias("hgtSlack")
	private Double hgtSlack;
	@XStreamAlias("bagStyle")
	private String bagStyle;
	@XStreamAlias("sealStyle")
	private String sealStyle;
	@XStreamAlias("topSeal")
	private Double topSeal;
	@XStreamAlias("botSeal")
	private Double botSeal;
	@XStreamAlias("backSeal")
	private Double backSeal;
	@XStreamAlias("isRange")
	private Boolean isRange;
	@XStreamAlias("values")
	private Values values;
	@XStreamAlias("range")
	private Range range;
	//DSO : Chg 01 Start - Tag Names: lockType
	@XStreamAlias("lockType")
	private String lockType;	
	//DSO : Chg 01 End
	
	public ShipperInfo() {
		super();
	}
	//DSO : Chg 01 Start - Tag Names: lockType
	public ShipperInfo(String material, String flute, String lenVert,
			String widVert, String hgtVert, Double caliper, Double netWgt,
			Double grsWgt, Double maxWgt, Double lenBulge, Double widBulge, Double hgtBulge,
			Double lenSlack, Double widSlack, Double hgtSlack, 
			String bagStyle , String sealStyle ,
			Double topSeal, Double botSeal, Double backSeal, Boolean isRange, String lockType) {
		//DSO : Chg 01 End
		super();
		this.material = material;
		this.flute = flute;
		this.lenVert = lenVert;
		this.widVert = widVert;
		this.hgtVert = hgtVert;
		this.caliper = caliper;
		this.netWgt = netWgt;
		this.grsWgt = grsWgt;
		this.maxWgt = maxWgt;
		this.lenBulge = lenBulge;
		this.widBulge = widBulge;
		this.hgtBulge = hgtBulge;
		this.lenSlack = lenSlack;
		this.widSlack = widSlack;
		this.hgtSlack = hgtSlack;
		this.bagStyle = bagStyle;
		this.sealStyle = sealStyle;
		this.topSeal = topSeal;
		this.botSeal = botSeal;
		this.backSeal = backSeal;		
		this.isRange = isRange;
		//DSO : Chg 01 Start - Tag Names: lockType
		this.lockType = lockType;
		//DSO : Chg 01 End
	}
	//DSO : Chg 01 Start - Tag Names: lockType
	public ShipperInfo(String material, String flute, String lenVert,
			String widVert, String hgtVert, Double caliper, Double netWgt,
			Double grsWgt, Double maxWgt, Boolean isRange, String lockType) {
		//DSO : Chg 01 End
		super();
		this.material = material;
		this.flute = flute;
		this.lenVert = lenVert;
		this.widVert = widVert;
		this.hgtVert = hgtVert;
		this.caliper = caliper;
		this.netWgt = netWgt;
		this.grsWgt = grsWgt;
		this.maxWgt = maxWgt;	
		this.isRange = isRange;
		//DSO : Chg 01 Start - Tag Names: lockType
		this.lockType = lockType;
		//DSO : Chg 01 End
	}
	
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public String getFlute() {
		return flute;
	}
	public void setFlute(String flute) {
		this.flute = flute;
	}
	public String getLenVert() {
		return lenVert;
	}
	public void setLenVert(String lenVert) {
		this.lenVert = lenVert;
	}
	public String getWidVert() {
		return widVert;
	}
	public void setWidVert(String widVert) {
		this.widVert = widVert;
	}
	public String getHgtVert() {
		return hgtVert;
	}
	public void setHgtVert(String hgtVert) {
		this.hgtVert = hgtVert;
	}
	public Double getCaliper() {
		return caliper;
	}
	public void setCaliper(Double caliper) {
		this.caliper = caliper;
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
	public Double getMaxWgt() {
		return maxWgt;
	}
	public void setMaxWgt(Double maxWgt) {
		this.maxWgt = maxWgt;
	}
	public Boolean getIsRange() {
		return isRange;
	}
	public void setIsRange(Boolean isRange) {
		this.isRange = isRange;
	}
	public Values getValues() {
		return values;
	}
	public void setValues(Values values) {
		this.values = values;
	}
	public Range getRange() {
		return range;
	}
	public void setRange(Range range) {
		this.range = range;
	}	
	public Double getLenBulge() {
		return lenBulge;
	}
	public void setLenBulge(Double lenBulge) {
		this.lenBulge = lenBulge;
	}
	public Double getWidBulge() {
		return widBulge;
	}
	public void setWidBulge(Double widBulge) {
		this.widBulge = widBulge;
	}
	public Double getHgtBulge() {
		return hgtBulge;
	}
	public void setHgtBulge(Double hgtBulge) {
		this.hgtBulge = hgtBulge;
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
	public String getBagStyle() {
		return bagStyle;
	}
	public void setBagStyle(String bagStyle) {
		this.bagStyle = bagStyle;
	}
	public String getSealStyle() {
		return sealStyle;
	}
	public void setSealStyle(String sealStyle) {
		this.sealStyle = sealStyle;
	}
	public Double getTopSeal() {
		return topSeal;
	}
	public void setTopSeal(Double topSeal) {
		this.topSeal = topSeal;
	}
	public Double getBotSeal() {
		return botSeal;
	}
	public void setBotSeal(Double botSeal) {
		this.botSeal = botSeal;
	}
	public Double getBackSeal() {
		return backSeal;
	}
	public void setBackSeal(Double backSeal) {
		this.backSeal = backSeal;
	}
	//DSO : Chg 01 Start - Tag Names: lockType
	public String getLockType() {
		return lockType;
	}
	public void setLockType(String lockType) {
		this.lockType = lockType;
	}	
	//DSO : Chg 01 End
	//DSO : Chg 01 Start - Tag Names: lockType
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + material + ", " + flute + ", " + lenVert + ", "
				+ widVert + ", " + hgtVert + ", " + caliper + ", " + netWgt + ", " + grsWgt + ", " + maxWgt + ", "
				+ lenBulge + ", " + widBulge + ", " + hgtBulge + ", " + lenSlack + ", " + widSlack + ", "
				+ hgtSlack + ", " + bagStyle + ", " + sealStyle + ", " + topSeal + ", " + botSeal + ", " + backSeal + ", "
				+ isRange + ", " + lockType + ", " + values + "]";
	}
	//DSO : Chg 01 End
}
