package com.pg.excessrequest.rulesmanager;

import java.util.List;
import com.pg.request.rulesmanager.RTAOriginLocalCopy;
//Added by RTA Capgemini Offshore for 18x.6 early-SIT Req 40330
public class RTAOriginlMasterCopy {
	
	private String gs1copytype;
	private String mcnumber;
	private String marketingName;
	private String inlinetranslation;
	private String copycontent;
	private String instanceSequence;
	//Added by RTA 22x Req 45604,45680 Starts
	private String mcILType;
	//Added by RTA for 22x May 23 CW Defect-52304 - Starts
	private String mcPassType;
	public String getMcPassType() {
		return mcPassType;
	}
	public void setMcPassType(String mcPassType) {
		this.mcPassType = mcPassType;
	}
	private String translate;
	
	public String getTranslate() {
		return translate;
	}
	public void setTranslate(String translate) {
		this.translate = translate;
	}
	//Added by RTA for 22x May 23 CW Defect-52304 - End
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49853 Starts
	private String subCopyType;
	
	private String mcValidityDate;
	private String mcRevision;
    //Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49853 End
	private String mcRegulatoryClassification;
	
	public String getMcRegulatoryClassification() {
		return mcRegulatoryClassification;
	}
	public void setMcRegulatoryClassification(String mcRegulatoryClassification) {
		this.mcRegulatoryClassification = mcRegulatoryClassification;
	}
	public String getMcILType() {
		return mcILType;
	}
	public void setMcILType(String mcILType) {
		this.mcILType = mcILType;
	}
	//Added by RTA 22x Req 45604,45680 Ends	
	
	public String getInstanceSequence() {
		return instanceSequence;
	}
	public void setInstanceSequence(String instanceSequence) {
		this.instanceSequence = instanceSequence;
	}
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
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49853 Starts
	public String getSubCopyType() {
		return subCopyType;
	}
	public void setSubCopyType(String subCopyType) {
		this.subCopyType = subCopyType;
	}
	public String getMcValidityDate() {
		return mcValidityDate;
	}
	public void setMcValidityDate(String mcValidityDate) {
		this.mcValidityDate = mcValidityDate;
	}
	public String getMcRevision() {
		return mcRevision;
	}
	public void setMcRevision(String mcRevision) {
		this.mcRevision = mcRevision;
	}
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49853 End
	public List<RTAOriginLocalCopy> getRtaoriginlocalcopy() {
		return rtaoriginlocalcopy;
	}
	public void setRtaoriginlocalcopy(List<RTAOriginLocalCopy> rtaoriginlocalcopy) {
		this.rtaoriginlocalcopy = rtaoriginlocalcopy;
	}

}
