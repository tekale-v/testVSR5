package com.pg.sizebasedataresponse.ws.rulesmanager;

import java.util.List;
public class MasterCopy {
		//Modified by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Starts
	     private String gs1copytype;
	     private String mcnumber;
	     private String marketingName;
	     private String translate;
	     private String inLineTranslation;
	     private String instanceSequence;
	     private String ilCopyListNumber;
	     private String mcILType;
	     private String mcPassType;
	     private String mcRegulatoryClassification;
		 private String copyContentRTE;
		 private String mcRevision;
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Starts
		 private String sortId;
		private String mcValidityDate;
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Ends
		 private List<com.pg.sizebasedataresponse.ws.rulesmanager.LocalCopy> localCopies;
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
		public String getTranslate() {
			return translate;
		}
		public void setTranslate(String translate) {
			this.translate = translate;
		}
		public String getInLineTranslation() {
			return inLineTranslation;
		}
		public void setInLineTranslation(String inLineTranslation) {
			this.inLineTranslation = inLineTranslation;
		}
		public String getInstanceSequence() {
			return instanceSequence;
		}
		public void setInstanceSequence(String instanceSequence) {
			this.instanceSequence = instanceSequence;
		}
		public String getIlCopyListNumber() {
			return ilCopyListNumber;
		}
		public void setIlCopyListNumber(String ilCopyListNumber) {
			this.ilCopyListNumber = ilCopyListNumber;
		}
		public String getMcILType() {
			return mcILType;
		}
		public void setMcILType(String mcILType) {
			this.mcILType = mcILType;
		}
		public String getMcPassType() {
			return mcPassType;
		}
		public void setMcPassType(String mcPassType) {
			this.mcPassType = mcPassType;
		}
		public String getMcRegulatoryClassification() {
			return mcRegulatoryClassification;
		}
		public void setMcRegulatoryClassification(String mcRegulatoryClassification) {
			this.mcRegulatoryClassification = mcRegulatoryClassification;
		}
		public String getCopyContentRTE() {
			return copyContentRTE;
		}
		public void setCopyContentRTE(String copyContentRTE) {
			this.copyContentRTE = copyContentRTE;
		}
		public String getMcRevision() {
			return mcRevision;
		}
		public void setMcRevision(String mcRevision) {
			this.mcRevision = mcRevision;
		}
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Starts
		 public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getMcValidityDate() {
			return mcValidityDate;
		}
		public void setMcValidityDate(String mcValidityDate) {
			this.mcValidityDate = mcValidityDate;
		}
		//Added by RTA Capgemini Offshore for 22x.5 April_24_CW Defect 56590  Ends
		public List<com.pg.sizebasedataresponse.ws.rulesmanager.LocalCopy> getLocalCopies() {
			return localCopies;
		}
		public void setLocalCopies(List<com.pg.sizebasedataresponse.ws.rulesmanager.LocalCopy> localCopies) {
			this.localCopies = localCopies;
		}
		//Modified by RTA Capgemini Offshore for 22x.5 April_24_CW Req 48844 Ends

		 
}
