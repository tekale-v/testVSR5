// Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 42530
package com.pg.claimresponse.ws.rulesmanager;

public class LocalCopy {
	//Added by RTA Capgemini Offshore for 18x.6 early-SIT Req 40330
	  private String language;
	  private String lcnumber;
	  private String copyContentRTE;
	  

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
	  
	  public void setCopyContentRTE(String copyContentRTE){
	    this.copyContentRTE = copyContentRTE;
	  }
	  
	  public String getCopyContentRTE(){
	    return this.copyContentRTE;
	  }
}
