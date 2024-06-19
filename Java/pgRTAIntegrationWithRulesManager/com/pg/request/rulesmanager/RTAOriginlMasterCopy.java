package com.pg.request.rulesmanager;

import java.util.List;

public class RTAOriginlMasterCopy {
	
	private String gs1copytype;
	private String mcnumber;
	private String marketingName;
	private String inlinetranslation;
	private String copycontent;
	//Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 starts

	private String ilCopyList;
	
	public String getILCopyList() {
		return ilCopyList;
	}
	public void setILCopyList(String ilCopyList) {
		this.ilCopyList = ilCopyList;
	}
	//Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 ends

	public String getCopycontent() {
		return copycontent;
	}
	public void setCopycontent(String copycontent) {
		this.copycontent = copycontent;
	}
	private List<RTAOriginLocalCopy> rtaoriginlocalcopy;
	
	public String getGs1copytype() {
		return gs1copytype;
	}
	public void setGs1copytype(String gs1copytype) {
		this.gs1copytype = gs1copytype;
	}
	public String getMcnumber() {
		return mcnumber;
	}
	public void setMcnumber(String mcnumber) {
		this.mcnumber = mcnumber;
	}
	public String getMarketingName() {
		return marketingName;
	}
	public void setMarketingName(String marketingName) {
		this.marketingName = marketingName;
	}
	public String getInlinetranslation() {
		return inlinetranslation;
	}
	public void setInlinetranslation(String inlinetranslation) {
		this.inlinetranslation = inlinetranslation;
	}
	public List<RTAOriginLocalCopy> getRtaoriginlocalcopy() {
		return rtaoriginlocalcopy;
	}
	public void setRtaoriginlocalcopy(List<RTAOriginLocalCopy> rtaoriginlocalcopy) {
		this.rtaoriginlocalcopy = rtaoriginlocalcopy;
	}

}
