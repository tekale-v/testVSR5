package com.pg.rulesengine;

public class CERequestData {
/*
 * 
 * 1. marketingName - Marketing Name Attribute value.
 * 2. copyContent or copyContentMatch - Copy Text of LC element
 * 3. brandName - Brand name
 * 4. gs1Type - GS1 Standard type
 * 5. copyId - LC or MC or MAE or CP
 * 6. region - Product Type Name which is directly connected to the given Brand.
 * 
 */
 
	private String marketingName;
	private String copyContent;
	private String copyContentMatch;
	private String brandName;
	private String gs1Type;
	private String copyId;
	private String region;

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getCopyId() {
		return copyId;
	}

	public void setCopyId(String copyId) {
		this.copyId = copyId;
	}

	public String getMarketingName() {
		return marketingName;
	}
	
	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}
	
	public String getCopyContentMatch() {
		return copyContentMatch;
	}

	public void setCopyContentMatch(String copyContentMatch) {
		this.copyContentMatch = copyContentMatch;
	}

	public String getCopyContent() {
		return copyContent;
	}

	public void setCopyContent(String copyContent) {
		this.copyContent = copyContent;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getGs1Type() {
		return gs1Type;
	}

	public void setGs1Type(String gs1Type) {
		this.gs1Type = gs1Type;
	}


	public String toString(){
		return "Marketing Name:" + marketingName 
				+ "\nBrand Name:" + brandName 
				+ "\nGS1 Standard Type:" + gs1Type 
				+ "\nRegion:" + region
				+ "\nCopy Id:" + copyId 
				+ "\nCopyContent:" + copyContent 
				+ "\nCopyContentMatch:" + copyContentMatch;
	}

}
