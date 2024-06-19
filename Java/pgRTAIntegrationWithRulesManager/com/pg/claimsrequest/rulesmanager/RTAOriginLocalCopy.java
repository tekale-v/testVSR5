package com.pg.claimsrequest.rulesmanager;

public class RTAOriginLocalCopy {
	
	private String lcnumber;
	private String copycontent;
	private String language;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
	private String lcValidityDate;
	private String lcRevision;
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
	
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
//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts	
	public String getLcValidityDate() {
		return lcValidityDate;
	}
	public void setLcValidityDate(String lcValidityDate) {
		this.lcValidityDate = lcValidityDate;
	}
	public String getLcRevision() {
		return lcRevision;
	}
	public void setLcRevision(String lcRevision) {
		this.lcRevision = lcRevision;
	}
//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End	
}
