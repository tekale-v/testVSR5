package com.pg.sizebasedataresponse.ws.rulesmanager;

public class LocalCopy {
	 
	  private String lcnumber;
	  private String copyContentRTE;
	  private String language;
	  //Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Starts
	  private String lcRevision;
	  //Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Ends
	//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Starts
	  private String lcValidityDate;
	//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Ends
	
	  public String getLcnumber() {
		return lcnumber;
	}
	public void setLcnumber(String lcnumber) {
		this.lcnumber = lcnumber;
	}
	public String getCopyContentRTE() {
		return copyContentRTE;
	}
	public void setCopyContentRTE(String copyContentRTE) {
		this.copyContentRTE = copyContentRTE;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	} 
	//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Starts
	public String getLcRevision() {
		return lcRevision;
	}
	public void setLcRevision(String lcRevision) {
		this.lcRevision = lcRevision;
	}
	 //Added by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Ends 
	//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Starts
	  public String getLcValidityDate() {
		return lcValidityDate;
	}
	public void setLcValidityDate(String lcValidityDate) {
		this.lcValidityDate = lcValidityDate;
	}
	//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Ends
	  
	  }
