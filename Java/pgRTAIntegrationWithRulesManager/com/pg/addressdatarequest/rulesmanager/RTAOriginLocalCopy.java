package com.pg.addressdatarequest.rulesmanager;

public class RTAOriginLocalCopy {

//Added by RTA Capgemini Offshore for 22x.4 Dec_CW Req 48239 Starts	
	private String lcnumber;
	private String copycontent;
	private String language;
	private String distributionType;
	private String lcValidityDate;
	//Added by RTA Capgemini Offshore for Apr CW 22x.5 Defect 56049   - Starts
	private String lcRevision;
	
	
	public String getLcRevision() {
		return lcRevision;
	}
	public void setLcRevision(String lcRevision) {
		this.lcRevision = lcRevision;
	}
	//Added by RTA Capgemini Offshore for Apr CW 22x.5 Defect 56049 - End
	public String getLcnumber() {
		return lcnumber;
	}
	public void setLcnumber(String lcnumber) {
		this.lcnumber = lcnumber;
	}
	public String getCopycontent() {
		return copycontent;
	}
	public void setCopycontent(String copycontent) {
		this.copycontent = copycontent;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getDistributionType() {
		return distributionType;
	}
	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}
	public String getLcValidityDate() {
		return lcValidityDate;
	}
	public void setLcValidityDate(String lcValidityDate) {
		this.lcValidityDate = lcValidityDate;
	}

//Added by RTA Capgemini Offshore for 22x.4 Dec_CW Req 48239 End	
	
}
