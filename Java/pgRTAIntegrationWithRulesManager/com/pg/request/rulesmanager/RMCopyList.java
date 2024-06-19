package com.pg.request.rulesmanager;
import java.util.List;
//Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532

public class RMCopyList {

		// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
		private String ilCopyListNumber ;
		// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Ends
	    private String ilCopyListCountries;
	    private String ilCopyListLanguages;
		//added by RTA 22x Req 45604,45680 Starts
		private String ilCopyListRegulatoryClassification;
		//added by RTA 22x Req 45604,45680 Ends
	    private List<MasterCopy> masterCopy;
	    
	    
		/**
		 * @return the ilCopyList
		 */
		 //Added by RTA 22x Req 45604,45680 Starts    
	public String getIlCopyListRegulatoryClassification() {
		return ilCopyListRegulatoryClassification;
	}

	public void setIlCopyListRegulatoryClassification(String ilCopyListRegulatoryClassification) {
			this.ilCopyListRegulatoryClassification = ilCopyListRegulatoryClassification;
		}
	
	//Added by RTA 22x Req 45604,45680 ends
		 // Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
		public String getIlCopyListNumber() {
			return ilCopyListNumber;
		}
		/**
		 * @param ilCopyList the ilCopyList to set
		 */
		public void setIlCopyListNumber(String ilCopyListNumber) {
			this.ilCopyListNumber = ilCopyListNumber;
		}
		// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Ends
		/**
		 * @return the ilCopyListCountries
		 */
		public String getIlCopyListCountries() {
			return ilCopyListCountries;
		}
		/**
		 * @param ilCopyListCountries the ilCopyListCountries to set
		 */
		public void setIlCopyListCountries(String ilCopyListCountries) {
			this.ilCopyListCountries = ilCopyListCountries;
		}
		/**
		 * @return the ilCopyListLanguages
		 */
		public String getIlCopyListLanguages() {
			return ilCopyListLanguages;
		}
		/**
		 * @param ilCopyListLanguages the ilCopyListLanguages to set
		 */
		public void setIlCopyListLanguages(String ilCopyListLanguages) {
			this.ilCopyListLanguages = ilCopyListLanguages;
		}
		public List<MasterCopy> getMasterCopy() {
			return masterCopy;
		}
		public void setMasterCopy(List<MasterCopy> masterCopy) {
			this.masterCopy = masterCopy;
		}


}
