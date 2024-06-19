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
20-Jan-15       xxw (DS)        v2013x.4         Chg01/ALM6440         Added the following tags:
                                                                       For BlisterPack: nestPack
                                                                       For Stand Up bag: standup, bagInvNest, bagPackTight
                                                                       For locking mechanism: lockType
                                                                       
 */
package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("info")
public class PriPackInfo {
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
	@XStreamAlias("lenBulge")
	private Double lenBulge;
	@XStreamAlias("widBulge")
	private Double widBulge;
	@XStreamAlias("hgtBulge")
	private Double hgtBulge;
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
	//DSO : Chg 01 Start - Tag Names: nestPack, standup, bagInvNest, bagPackTight, lockType
	@XStreamAlias("nestPack")
	private String nestPack;
	@XStreamAlias("standup")
	private String standup;
	@XStreamAlias("bagInvNest")
	private String bagInvNest;
	@XStreamAlias("bagPackTight")
	private String bagPackTight;
	@XStreamAlias("lockType")
	private String lockType;
	//DSO : Chg 01 End
	public PriPackInfo() {
		super();
	}
	//DSO : Chg 01 Start - Tag Names: nestPack, standup, bagInvNest, bagPackTight, lockType
	public PriPackInfo(String lenVert, String widVert, String hgtVert,
			Double netWgt, Double grsWgt, Double caliper,
			Double lenBulge, Double widBulge, Double hgtBulge,
			String bagStyle , String sealStyle ,
			Double topSeal, Double botSeal, Double backSeal, String nestPack,
			String standup, String bagInvNest, String bagPackTight, String lockType) {
		//DSO : Chg 01 End
		super();
		this.lenVert = lenVert;
		this.widVert = widVert;
		this.hgtVert = hgtVert;
		this.netWgt = netWgt;
		this.grsWgt = grsWgt;
		this.caliper = caliper;
		this.lenBulge = lenBulge;
		this.widBulge = widBulge;
		this.hgtBulge = hgtBulge;
		this.bagStyle = bagStyle;
		this.sealStyle = sealStyle;
		this.topSeal = topSeal;
		this.botSeal = botSeal;
		this.backSeal = backSeal;
		//DSO : Chg 01 Start - Tag Names: nestPack, standup, bagInvNest, bagPackTight, lockType
		this.nestPack = nestPack;
		this.standup = standup;
		this.bagInvNest = bagInvNest;
		this.bagPackTight = bagPackTight;
		this.lockType = lockType;
		//DSO : Chg 01 End
		
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
	public Double getCaliper() {
		return caliper;
	}
	public void setCaliper(Double caliper) {
		this.caliper = caliper;
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
	//DSO : Chg 01 Start - Tag Names: nestPack, standup, bagInvNest, bagPackTight, lockType
	public String getNestPack() {
		return nestPack;
	}
	public void setNestPack(String nestPack) {
		this.nestPack = nestPack;
	}
	public String getStandup() {
		return standup;
	}
	public void setStandup(String standup) {
		this.standup = standup;
	}
	public String getBagInvNest() {
		return bagInvNest;
	}
	public void setBagInvNest(String bagInvNest) {
		this.bagInvNest = bagInvNest;
	}
	public String getBagPackTight() {
		return bagPackTight;
	}
	public void setBagPackTight(String bagPackTight) {
		this.bagPackTight = bagPackTight;
	}
	public String getLockType() {
		return lockType;
	}
	public void setLockType(String lockType) {
		this.lockType = lockType;
	}
	//DSO : Chg 01 End
	//DSO : Chg 01 Start - Tag Names: nestPack, standup, bagInvNest, bagPackTight, lockType
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + lenVert + ", " + widVert + ", " + hgtVert + ", " + netWgt + ", " + grsWgt + ", " + caliper + ", " +
				lenBulge + ", " + widBulge + ", " + hgtBulge + ", " + bagStyle  + ", " + sealStyle  + ", " + 
				topSeal + ", " + botSeal + ", " + backSeal + ", " + nestPack + ", " + standup + ", " + bagInvNest + ", " + bagPackTight + ", " + lockType + "]";
	}
	//DSO : Chg 01 End
}
