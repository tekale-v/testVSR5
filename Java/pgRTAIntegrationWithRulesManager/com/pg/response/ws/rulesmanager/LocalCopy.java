package com.pg.response.ws.rulesmanager;

public class LocalCopy {

	  private String language;
	  private String copyContentRTE;
	  // Added by RTA Capgemini Offshore for 18x.6 DEC_CW Req 41129  - Starts
	  private String lcnumber;
	  
	  public String getLcnumber() {
		return lcnumber;
	}

	public void setLcnumber(String lcnumber) {
		this.lcnumber = lcnumber;
	}
	// Added by RTA Capgemini Offshore for 18x.6 DEC_CW Req 41129  - Ends
	public void setLanguage(String language) {
	    this.language = language;
	  }
	  
	  public String getLanguage(){
	    return this.language;
	  }
	  
	  public void setCopyContentRTE(String copyContentRTE){
	    this.copyContentRTE = copyContentRTE;
	  }
	  
	  public String getCopyContentRTE(){
	    return this.copyContentRTE;
	  }
}
