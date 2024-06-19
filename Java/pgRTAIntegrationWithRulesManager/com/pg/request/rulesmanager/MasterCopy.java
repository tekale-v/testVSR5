package com.pg.request.rulesmanager;
import java.util.List;

public class MasterCopy {
	private String marketingName;
	private String copyContent;
	private String copyContentRTE;
	private String aicn;
	private String adjTarget;
	private String commonName;
	private List<LocalCopy> localCopies;
	
	public String getMarketingName() {
		return marketingName;
	}
	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}
	public String getCopyContent() {
		return copyContent;
	}
	public void setCopyContent(String copyContent) {
		this.copyContent = copyContent;
	}
	public String getCopyContentRTE() {
		return copyContentRTE;
	}
	public void setCopyContentRTE(String copyContentRTE) {
		this.copyContentRTE = copyContentRTE;
	}
	public String getAicn() {
		return aicn;
	}
	public void setAicn(String aicn) {
		this.aicn = aicn;
	}
	public String getAdjTarget() {
		return adjTarget;
	}
	public void setAdjTarget(String adjTarget) {
		this.adjTarget = adjTarget;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public List<LocalCopy> getLocalCopies() {
		return localCopies;
	}
	public void setLocalCopies(List<LocalCopy> localCopies) {
		this.localCopies = localCopies;
	}
}
