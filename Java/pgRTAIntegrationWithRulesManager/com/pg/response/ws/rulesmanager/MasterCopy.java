package com.pg.response.ws.rulesmanager;

import java.util.List;

public class MasterCopy {

		 private String copyContentRTE;
		 private String inLineTranslation;
		 private List<LocalCopy> localCopies;
		 // Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
		 private  String ilCopyListNumber ;
		 // Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - ends
		 // Added by RTA Capgemini Offshore for 18x.6 DEC_CW Req 41129  - Starts
		 private String mcnumber;
		// Added by RTA Capgemini Offshore for 18x.6 June CW Defect 48466 - Starts
		 private String gs1copytype;
		// Added by RTA Capgemini Offshore for 18x.6 June CW Defect 48466 - Ends
		// RTA 22x_01 Feb 23 CW - Added for Defect-51826 - Starts
		 private String translate;
		 // RTA 22x_01 Feb 23 CW - Added for Defect-51826 - End
		//Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 starts
		// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Starts
		 public String getIlCopyListNumber() {
				return ilCopyListNumber ;
			}
			public void setIlCopyListNumber(String ilCopyListNumber ) {
				this.ilCopyListNumber  = ilCopyListNumber ;
			}
			// Added by RTA Capgemini Offshore for 18x.6 June_CW Defect 48282  - Ends
			//Added by RTA Capgemini Offshore for 18x.6 June_22_CW Req 42532 ends

		 public String getMcnumber() {
			return mcnumber;
		}
		public void setMcnumber(String mcnumber) {
			this.mcnumber = mcnumber;
		}
		// Added by RTA Capgemini Offshore for 18x.6 June CW Defect 48466 - Starts
		 public String getGs1copytype() {
				return gs1copytype;
			}
			public void setGs1copytype(String gs1copytype) {
				this.gs1copytype = gs1copytype;
			}
			// Added by RTA Capgemini Offshore for 18x.6 June CW Defect 48466 - Ends
		// Added by RTA Capgemini Offshore for 18x.6 DEC_CW Req 41129  - Ends
		
		 
		public String getCopyContentRTE() {
			return copyContentRTE;
		}
		public void setCopyContentRTE(String copyContentRTE) {
			this.copyContentRTE = copyContentRTE;
		}
		public String getInLineTranslation() {
			return inLineTranslation;
		}
		public void setInLineTranslation(String inLineTranslation) {
			this.inLineTranslation = inLineTranslation;
		}
	// RTA 22x_01 Feb 23 CW - Added for Defect-51826 - Starts - Starts
		public String getTranslate() {
			return translate;
		}
		public void setTranslate(String translate) {
			this.translate = translate;
		}
		// RTA 22x_01 Feb 23 CW - Added for Defect-51826 - Starts - End
		public List<LocalCopy> getLocalCopies() {
			return localCopies;
		}
		public void setLocalCopies(List<LocalCopy> localCopies) {
			this.localCopies = localCopies;
		}
}
