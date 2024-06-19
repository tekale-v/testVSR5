package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("info")
public class UnitloadInfo {
	@XStreamAlias("maxHgt")
	private Double maxHgt;
	@XStreamAlias("maxWgt")
	private Double maxWgt;
	@XStreamAlias("maxOHLen")
	private Double maxOHLen;
	@XStreamAlias("maxOHWid")
	private Double maxOHWid;
	@XStreamAlias("maxUHLen")
	private Double maxUHLen;
	@XStreamAlias("maxUHWid")
	private Double maxUHWid;
	@XStreamAlias("pkgWgt")
	private Double pkgWgt;
	
	public UnitloadInfo() {
		super();
	}
	public UnitloadInfo(Double maxHgt, Double maxWgt, Double maxOHLen,
			Double maxOHWid, Double maxUHLen, Double maxUHWid, Double pkgWgt) {
		super();
		this.maxHgt = maxHgt;
		this.maxWgt = maxWgt;
		this.maxOHLen = maxOHLen;
		this.maxOHWid = maxOHWid;
		this.maxUHLen = maxUHLen;
		this.maxUHWid = maxUHWid;
		this.pkgWgt = pkgWgt;
	}
	public Double getMaxHgt() {
		return maxHgt;
	}
	public void setMaxHgt(Double maxHgt) {
		this.maxHgt = maxHgt;
	}
	public Double getMaxWgt() {
		return maxWgt;
	}
	public void setMaxWgt(Double maxWgt) {
		this.maxWgt = maxWgt;
	}
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
	public Double getPkgWgt() {
		return pkgWgt;
	}
	public void setPkgWgt(Double pkgWgt) {
		this.pkgWgt = pkgWgt;
	}
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + maxHgt + ", " + maxWgt + ", " + maxOHLen + ", " + maxOHWid + ", " + maxUHLen + ", " + maxUHWid + ", " + pkgWgt + "]";
	}
}
