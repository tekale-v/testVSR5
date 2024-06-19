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
20-Jan-15       xxw (DS)        v2013x.4         Chg01/ALM6440         Added the following dimension tags for Blister Pack: indent, topindent, botindent, sideindent
                                                                       Added lenUnits tag where missing. NOTE: lenUnits is currently not used in the TOPS generated XML file.
 */

package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("dims")
public class Dims {
	@XStreamAlias("inOut")
	private String inOut;
	@XStreamAlias("len")
	private Double len;
	@XStreamAlias("wid")
	private Double wid;
	@XStreamAlias("hgt")
	private Double hgt;
	@XStreamAlias("bodyShape")
	private String bodyShape;
	@XStreamAlias("topDiameter")
	private Double topDiameter;
	@XStreamAlias("bottomDiameter")
	private Double bottomDiameter;
	@XStreamAlias("topLength")
	private Double topLength;
	@XStreamAlias("topWidth")
	private Double topWidth;
	@XStreamAlias("botWidth")
	private Double botWidth;
	@XStreamAlias("bodyLength")
	private Double bodyLength;
	@XStreamAlias("bodyWidth")
	private Double bodyWidth;
	@XStreamAlias("bodyDiameter")
	private Double bodyDiameter;
	@XStreamAlias("neckDiameter")
	private Double neckDiameter;
	@XStreamAlias("neckHgt")
	private Double neckHgt;
	@XStreamAlias("shHgt")
	private Double shHgt;
	@XStreamAlias("diameter")
	private Double diameter;
	@XStreamAlias("pitch")
	private Double pitch;
	@XStreamAlias("lenUnits")
	private String lenUnits;
	//DSO : Chg01 Start - Tag names: indent, topindent, botindent, sideindent
	@XStreamAlias("Indent")
	private String indent;
	@XStreamAlias("TopIndent")
	private String topIndent;
	@XStreamAlias("BotIndent")
	private String botIndent;
	@XStreamAlias("SideIndent")
	private String sideIndent;
	//DSO : Chg01 End
	
	public Dims() {
		super();
	}
	public Dims(String inOut, Double len, Double wid, Double hgt) {
		super();
		this.inOut = inOut;
		this.len = len;
		this.wid = wid;
		this.hgt = hgt;
}
	
	public Dims(String inOut, String lenUnits, Double len, Double wid, Double hgt) {
		super();
		this.inOut = inOut;
		this.lenUnits = lenUnits;
		this.len = len;
		this.wid = wid;
		this.hgt = hgt;
}
	public Dims(Double len, Double wid, Double hgt) {
		super();
		this.len = len;
		this.wid = wid;
		this.hgt = hgt;
}	
	//DSO : Chg01 Start - Tag names: indent, topindent, botindent, sideindent, lenUnits
	public Dims(String inOut, Double len, Double wid, Double hgt,
			String bodyShape , Double topDiameter, Double bottomDiameter, Double topLength,
			Double topWidth, Double botWidth, Double bodyLength,
			Double bodyWidth, Double bodyDiameter, Double neckDiameter,
			Double neckHgt, Double shHgt, Double diameter, Double pitch, String lenUnits,
			String indent, String topIndent, String botIndent, String sideIndent) {
		super();
		this.inOut = inOut;
		this.len = len;
		this.wid = wid;
		this.hgt = hgt;
		this.bodyShape = bodyShape;
		this.topDiameter = topDiameter;
		this.bottomDiameter = bottomDiameter;
		this.topLength = topLength;
		this.topWidth = topWidth;
		this.botWidth = botWidth;
		this.bodyLength = bodyLength;
		this.bodyWidth = bodyWidth;
		this.bodyDiameter = bodyDiameter;
		this.neckDiameter = neckDiameter;
		this.neckHgt = neckHgt;
		this.shHgt = shHgt;
		this.diameter = diameter;
		this.pitch = pitch;
		this.lenUnits = lenUnits;
		this.indent = indent;
		this.topIndent = topIndent;
		this.botIndent = botIndent;
		this.sideIndent = sideIndent;
		//DSO : Chg01 End
	}
	//DSO 2013x.6 - Added for Packaging Authoring FSD Enhancement - Start
	
	public Dims(String inOut, String bodyShape , Double topDiameter, Double bottomDiameter, Double topLength,
			Double topWidth, Double botWidth, Double bodyLength,
			Double bodyWidth, Double bodyDiameter, Double neckDiameter,
			Double neckHgt, Double shHgt, Double diameter, Double pitch, String topIndent, String botIndent, String sideIndent,Double len, Double wid, Double hgt, String pgInterfaceName) {
		super();
		
		if("pgTOPSCan".equals(pgInterfaceName) || "pgTOPSDrum".equals(pgInterfaceName))
		{
			this.diameter = diameter;
			this.hgt = hgt;
		} else if("pgTOPSTubRound".equals(pgInterfaceName) || "pgTOPSBucketRound".equals(pgInterfaceName)){
			this.topDiameter = topDiameter;
			this.bottomDiameter = bottomDiameter;
			this.pitch = pitch;
			this.hgt = hgt;
		} else if("pgTOPSTubRectangular".equals(pgInterfaceName) || "pgTOPSBucketRectangular".equals(pgInterfaceName)){
			this.topLength = topLength;
			this.topWidth = topWidth;
			this.botWidth = botWidth;
			this.pitch = pitch;
			this.hgt = hgt;
		} else if("pgTOPSBottleRound".equals(pgInterfaceName)){
			this.bodyDiameter = bodyDiameter;
			this.neckDiameter = neckDiameter;
			this.neckHgt = neckHgt;
			this.shHgt = shHgt;
			this.hgt = hgt;
		} else if("pgTOPSBottleRectangular".equals(pgInterfaceName) || "pgTOPSBottleOval".equals(pgInterfaceName)){
			this.bodyLength = bodyLength;
			this.bodyWidth = bodyWidth;
			this.neckDiameter = neckDiameter;
			this.neckHgt = neckHgt;
			this.shHgt = shHgt;
			this.hgt = hgt;
		} else if("pgTOPSFRADimensions".equals(pgInterfaceName)){
				//Pending TOPS type names
		} else if("pgTOPSBlisterPack".equals(pgInterfaceName)){
			this.topIndent = topIndent;
			this.botIndent = botIndent;
			this.sideIndent = sideIndent;
			this.len = len;
			this.wid = wid;
			this.hgt = hgt;
		}else{
			this.len = len;
			this.wid = wid;
			this.hgt = hgt;
		}
		this.inOut = inOut;
		this.bodyShape = bodyShape;
	}
	//DSO 2013x.6 - Added for Packaging Authoring FSD Enhancement - Start
	public String getLenUnits() {
		return lenUnits;
	}
	public void setLenUnits(String lenUnits) {
		this.lenUnits = lenUnits;
	}
	public String getInOut() {
		return inOut;
	}
	public void setInOut(String inOut) {
		this.inOut = inOut;
	}
	public Double getLen() {
		return len;
	}
	public void setLen(Double len) {
		this.len = len;
	}
	public Double getWid() {
		return wid;
	}
	public void setWid(Double wid) {
		this.wid = wid;
	}
	public Double getHgt() {
		return hgt;
	}
	public void setHgt(Double hgt) {
		this.hgt = hgt;
	}
	public String getBodyShape() {
		return bodyShape;
	}
	public void setBodyShape(String bodyShape) {
		this.bodyShape = bodyShape;
	}
	public Double getTopDiameter() {
		return topDiameter;
	}
	public void setTopDiameter(Double topDiameter) {
		this.topDiameter = topDiameter;
	}
	public Double getBottomDiameter() {
		return bottomDiameter;
	}
	public void setBottomDiameter(Double bottomDiameter) {
		this.bottomDiameter = bottomDiameter;
	}
	public Double getTopLength() {
		return topLength;
	}
	public void setTopLength(Double topLength) {
		this.topLength = topLength;
	}
	public Double getTopWidth() {
		return topWidth;
	}
	public void setTopWidth(Double topWidth) {
		this.topWidth = topWidth;
	}
	public Double getBotWidth() {
		return botWidth;
	}
	public void setBotWidth(Double botWidth) {
		this.botWidth = botWidth;
	}
	public Double getBodyLength() {
		return bodyLength;
	}
	public void setBodyLength(Double bodyLength) {
		this.bodyLength = bodyLength;
	}
	public Double getBodyWidth() {
		return bodyWidth;
	}
	public void setBodyWidth(Double bodyWidth) {
		this.bodyWidth = bodyWidth;
	}
	public Double getBodyDiameter() {
		return bodyDiameter;
	}
	public void setBodyDiameter(Double bodyDiameter) {
		this.bodyDiameter = bodyDiameter;
	}
	public Double getNeckDiameter() {
		return neckDiameter;
	}
	public void setNeckDiameter(Double neckDiameter) {
		this.neckDiameter = neckDiameter;
	}
	public Double getNeckHgt() {
		return neckHgt;
	}
	public void setNeckHgt(Double neckHgt) {
		this.neckHgt = neckHgt;
	}
	public Double getShHgt() {
		return shHgt;
	}
	public void setShHgt(Double shHgt) {
		this.shHgt = shHgt;
	}
	public Double getDiameter() {
		return diameter;
	}
	public void setDiameter(Double diameter) {
		this.diameter = diameter;
	}
	public Double getPitch() {
		return pitch;
	}
	public void setPitch(Double pitch) {
		this.pitch = pitch;
	}
	//DSO : Chg01 Start - Tag names: indent, topindent, botindent, sideindent
	public String getIndent() {
		return indent;
	}
	public void setIndent(String indent) {
		this.indent = indent;
	}
	public String gettopIndent() {
		return topIndent;
	}
	public void settopIndent(String topindent) {
		this.topIndent = topindent;
	}
	public String getbotIndent() {
		return botIndent;
	}
	public void setbotindent(String botIndent) {
		this.botIndent = botIndent;
	}
	public String getsideIndent() {
		return sideIndent;
	}
	public void setsideindent(String sideIndent) {
		this.sideIndent = sideIndent;
	}
	//DSO : Chg01 End
	//DSO : Chg01 Start - Tag names: indent, topindent, botindent, sideindent
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + inOut + ", "+ lenUnits + ", " + len + ", " + wid + ", " + hgt + ", " +
				bodyShape + ", "+ topDiameter + ", " + bottomDiameter + ", " + topLength + ", " +
				topWidth + ", "+ botWidth + ", " + bodyLength + ", " + bodyWidth + ", " +
				bodyDiameter + ", "+ neckDiameter + ", " + neckHgt + ", " + shHgt + ", " +
				diameter + ", "+ pitch + ", "+ indent + ", "+ topIndent + ", "+ botIndent + ", "+ sideIndent + "]";
	}
}
//DSO : Chg01 End