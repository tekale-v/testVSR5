package com.pg.addressdataresponse.ws.rulesmanager;

public class LocalCopy {
	  private String language;
	  //Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Starts
	  private String copyContentRTE;
	  //Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Ends
	  private String lcnumber;
	  private String distributionType;
	  private String lcvaliditydate;
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
	
	public void setLanguage(String language) {
	    this.language = language;
	}
	  
	public String getLanguage(){
	    return this.language;
	}
//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Starts	
	  public String getCopyContentRTE() {
		return copyContentRTE;
	}

	public void setCopyContentRTE(String copyContentRTE) {
		this.copyContentRTE = copyContentRTE;
	}
//Added by RTA Capgemini Offshore for Dec CW 22x.4 Defect 55905   - Ends	
	public String getDistributionType() {
		return distributionType;
	}
	public void setDistributionType(String distributionType) {
		this.distributionType = distributionType;
	}
	public String getLcvaliditydate() {
		return lcvaliditydate;
	}

	public void setLcvaliditydate(String lcvaliditydate) {
		this.lcvaliditydate = lcvaliditydate;
	}
	
}
