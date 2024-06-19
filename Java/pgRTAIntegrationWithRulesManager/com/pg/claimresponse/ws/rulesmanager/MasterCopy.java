// Added by RTA Capgemini Offshore for 18x.6 June_CW Requirement 42530
package com.pg.claimresponse.ws.rulesmanager;

import java.util.List;

public class MasterCopy {

		 private String copyContentRTE;
		 private String mcnumber;
		//Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
		private String instanceSequence;
		//Modified by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
		 //Added by RTA for 22x May 23 CW ALM-46088 - Starts
		 private String sortId;
		//Added by RTA for 22x May 23 CW ALM-46088 - End
		 private List<LocalCopy> localCopies;
		
		 public String getCopyContentRTE() {
				return copyContentRTE;
			}
			public void setCopyContentRTE(String copyContentRTE) {
				this.copyContentRTE = copyContentRTE;
			}
		 public String getMcnumber() {
			return mcnumber;
		}
		public void setMcnumber(String mcnumber) {
			this.mcnumber = mcnumber;
		}
		//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 Starts
		public String getInstanceSequence() {
		return instanceSequence;
		}
		
		public void setInstanceSequence(String instanceSequence) {
		this.instanceSequence = instanceSequence;
		
		}
	//Added by RTA Capgemini Offshore for 22x.6 AUG_24_CW Req 49854 End
		//Added by RTA for 22x May 23 CW ALM-46088 - Starts
		 public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		//Added by RTA for 22x May 23 CW ALM-46088 - End
		public List<LocalCopy> getLocalCopies() {
			return localCopies;
		}
		public void setLocalCopies(List<LocalCopy> localCopies) {
			this.localCopies = localCopies;
		}
}
