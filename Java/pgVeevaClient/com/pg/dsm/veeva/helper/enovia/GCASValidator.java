/*
 **   GCASValidator.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean To capture GCAS validation/exception messages.
 **   DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
 */
package com.pg.dsm.veeva.helper.enovia;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.veeva.util.Veeva;

import matrix.util.StringList;

public class GCASValidator {
	private String gcas;
	private String gcasID, gcasType, gcasName, gcasRev, gcasState, gcasReleasePhase, gcasStatus, gcasParentType, gcasLatestRevisionID;
	private String invalidGcasMessage;
	private String validationError;
	private boolean isGcasObjExist, isGcasValid, isGcasExist, isGcasRelatedConnectionChecked, isGcasHasArtwork, isGcasHasPrimaryOrg, isGcasHasSecondaryOrg, isGcasHasSecurityClass, isGcasHasSegment;
	private Object gcasPrimaryOrg;
	private Object gcasSecondaryOrg;
	private Object gcasSecurityClass;
	private String gcasSegment;
	private String pmpId;
	private DomainObject gcasObj;
	private String pmpState;
	private boolean validationStatus;
	private String errorMsg;
	private boolean isGcasSet;
	private String gcasHasArtworkMessage;
	//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Starts
	private StringList countryList;
	private String changeOrderId;
	private String changeActionId;
	private boolean isArtworkPromoted;
	//DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414 - Ends

	/** 
	 * @about Method to check is gcas object exist
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isGcasObjExist() {
		return isGcasObjExist;
	}
	/** 
	 * @about Getter method to get gcas exist 
	 * @return String
	 * @since DSM 2018x.3
	 */
	public void setGcasObjExist(boolean isGcasObjExist) {
		this.isGcasObjExist = isGcasObjExist;
	}
	/** 
	 * @about Getter method to get gcas obj
	 * @return DomainObject
	 * @since DSM 2018x.3
	 */
	public DomainObject getGcasObj() {
		return gcasObj;
	}
	public void setGcasObj(DomainObject gcasObj) {
		this.gcasObj = gcasObj;
	}
	/** 
	 * @about Method to check is gcas object exist
	 * @return boolean
	 * @since DSM 2018x.3
	 */
	public boolean isGcasExist() {
		return isGcasExist;
	}
	/** 
	 * @about Getter method to get latest rev id
	 * @return DomainObject
	 * @since DSM 2018x.3
	 */
	public String getGcasLatestRevisionID() {
		return gcasLatestRevisionID;
	}
	/** 
	 * @about Setter method to set gcas latest rev id
	 * @param String
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasLatestRevisionID(String gcasLatestRevisionID) {
		this.gcasLatestRevisionID = gcasLatestRevisionID;
	}
	/** 
	 * @about Getter method to get segement
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getGcasSegment() {
		return gcasSegment;
	}
	/** 
	 * @about Setter method to set segment
	 * @param String 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasSegment(String gcasSegment) {
		this.gcasSegment = gcasSegment;
	}
	/** 
	 * @about Getter method to get segement
	 * @return Object
	 * @since DSM 2018x.3
	 */
	public Object getGcasSecondaryOrg() {
		return gcasSecondaryOrg;
	}
	/** 
	 * @about Setter method to set secondary org
	 * @param String 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasSecondaryOrg(Object gcasSecondaryOrg) {
		this.gcasSecondaryOrg = gcasSecondaryOrg;
	}
	/** 
	 * @about Getter method to get primary org
	 * @return Object
	 * @since DSM 2018x.3
	 */
	public Object getGcasPrimaryOrg() {
		return gcasPrimaryOrg;
	}
	/** 
	 * @about Setter method to set primary org
	 * @param String 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasPrimaryOrg(Object gcasPrimaryOrg) {
		this.gcasPrimaryOrg = gcasPrimaryOrg;
	}
	/** 
	 * @about Getter method to get gcas related connection query performed
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public boolean isGcasRelatedConnectionChecked() {
		return isGcasRelatedConnectionChecked;
	}
	/** 
	 * @about Setter method to set gcas related connection query performed
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasRelatedConnectionChecked(boolean isGcasRelatedConnectionChecked) {
		this.isGcasRelatedConnectionChecked = isGcasRelatedConnectionChecked;
	}
	/** 
	 * @about Setter method to set gcas exsit
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasExist(boolean isGcasExist) {
		this.isGcasExist = isGcasExist;
	}

	public String getGcasHasArtworkMessage() {
		return gcasHasArtworkMessage;
	}

	public void setGcasHasArtworkMessage(String gcasHasArtworkMessage) {
		this.gcasHasArtworkMessage = gcasHasArtworkMessage;
	}

	public String getGcasType() {
		return gcasType;
	}

	public void setGcasType(String gcasType) {
		this.gcasType = gcasType;
	}

	public boolean isGcasHasPrimaryOrg() {
		return isGcasHasPrimaryOrg;
	}
	/** 
	 * @about Setter method to set gcas has primary org
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasHasPrimaryOrg(boolean isGcasHasPrimaryOrg) {
		this.isGcasHasPrimaryOrg = isGcasHasPrimaryOrg;
	}

	public boolean isGcasHasSecondaryOrg() {
		return isGcasHasSecondaryOrg;
	}
	/** 
	 * @about Setter method to set gcas has secondary org
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasHasSecondaryOrg(boolean isGcasHasSecondaryOrg) {
		this.isGcasHasSecondaryOrg = isGcasHasSecondaryOrg;
	}

	public boolean isGcasHasSecurityClass() {
		return isGcasHasSecurityClass;
	}
	/** 
	 * @about Setter method to set gcas has security control
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasHasSecurityClass(boolean isGcasHasSecurityClass) {
		this.isGcasHasSecurityClass = isGcasHasSecurityClass;
	}

	public boolean isGcasHasSegment() {
		return isGcasHasSegment;
	}
	/** 
	 * @about Setter method to set gcas has segment
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasHasSegment(boolean isGcasHasSegment) {
		this.isGcasHasSegment = isGcasHasSegment;
	}
	/** 
	 * @about Getter method to get gcas release phase
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getGcasReleasePhase() {
		return gcasReleasePhase;
	}
	/** 
	 * @about Getter method to get gcas release phase
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getGcasParentType() {
		return gcasParentType;
	}
	/** 
	 * @about Setter method to set gcas parent type
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasParentType(String gcasParentType) {
		this.gcasParentType = gcasParentType;
	}
	/** 
	 * @about Getter method to get gcas status
	 * @return String
	 * @since DSM 2018x.3
	 */
	public String getGcasStatus() {
		return gcasStatus;
	}
	/** 
	 * @about Setter method to set gcas status
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasStatus(String gcasStatus) {
		this.gcasStatus = gcasStatus;
	}
	/** 
	 * @about Setter method to set gcas rel phase
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasReleasePhase(String gcasReleasePhase) {
		this.gcasReleasePhase = gcasReleasePhase;
	}
	public boolean isGcasHasArtwork() {
		return isGcasHasArtwork;
	}

	public void setGcasHasArtwork(boolean isGcasHasArtwork) {
		this.isGcasHasArtwork = isGcasHasArtwork;
	}

	public boolean isGcasSet() {
		return isGcasSet;
	}
	/** 
	 * @about Setter method to set gcas is set
	 * @param boolean 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void setGcasSet(boolean isGcasSet) {
		this.isGcasSet = isGcasSet;
	}

	public String getGcasID() {
		return gcasID;
	}

	public void setGcasID(String gcasID) {
		this.gcasID = gcasID;
	}

	public String getGcasName() {
		return gcasName;
	}

	public void setGcasName(String gcasName) {
		this.gcasName = gcasName;
	}

	public String getGcasRev() {
		return gcasRev;
	}

	public void setGcasRev(String gcasRev) {
		this.gcasRev = gcasRev;
	}

	public String getGcasState() {
		return gcasState;
	}

	public void setGcasState(String gcasState) {
		this.gcasState = gcasState;
	}
	public String getInvalidGcasMessage() {
		return invalidGcasMessage;
	}

	public void setInvalidGcasMessage(String invalidGcasMessage) {
		this.invalidGcasMessage = invalidGcasMessage;
	}

	public boolean isGcasValid() {
		return isGcasValid;
	}

	public void setGcasValid(boolean isGcasValid) {
		this.isGcasValid = isGcasValid;
	}

	public String getGcas() {
		return gcas;
	}

	public void setGcas(String gcas) {
		this.gcas = gcas;
	}
	public Object getGcasSecurityClass() {
		return gcasSecurityClass;
	}

	public void setGcasSecurityClass(Object gcasSecurityClass) {
		this.gcasSecurityClass = gcasSecurityClass;
	}
	public String getPmpId() {
		return pmpId;
	}

	public void setPmpId(String pmpId) {
		this.pmpId = pmpId;
	}
	public String getPmpState() {
		return pmpState;
	}

	public void setPmpState(String pmpState) {
		this.pmpState = pmpState;
	}
	public boolean getValidationStatus() {
		return validationStatus;
	}
	public void setValidationStatus(boolean validationStatus) {
		this.validationStatus = validationStatus;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		if(UIUtil.isNotNullAndNotEmpty(this.errorMsg) && UIUtil.isNotNullAndNotEmpty(errorMsg) ) {
			this.errorMsg = this.errorMsg + Veeva.SYMBOL_COMMA + errorMsg;
		}else {
			this.errorMsg = errorMsg;
		}
	}
	public String getValidationError() {
		return validationError;
	}
	public void setValidationError(String validationError) {
		if(UIUtil.isNotNullAndNotEmpty(this.validationError) && UIUtil.isNotNullAndNotEmpty(validationError) ) {
			this.validationError = this.validationError + Veeva.SYMBOL_COMMA + validationError;
		}else {
			this.validationError = validationError;
		}
	}
    
	/** 
	 * @about getter method to get country list
	 * @return StringList
	 * @since DSM 2018x.5
	 */
	public StringList getCountryList() {
		return countryList;
	}
	
	/** 
	 * @about Setter method to set Country List
	 * @param StringList 
	 * @return void
	 * @since DSM 2018x.5
	 */
	public void setCountryList(StringList countryList) {
		this.countryList = countryList;
	}
	
	/** 
	 * @about getter method to get Change Order Id
	 * @return String
	 * @since DSM 2018x.5
	 */
	public String getChangeOrderId() {
		return changeOrderId;
	}
	
	/** 
	 * @about Setter method to set Change Order Id
	 * @param String
	 * @return void
	 * @since DSM 2018x.5
	 */
	public void setChangeOrderId(String changeOrderId) {
		this.changeOrderId = changeOrderId;
	}
	
	/** 
	 * @about getter method to get Change Action Id
	 * @return String
	 * @since DSM 2018x.5
	 */
	public String getChangeActionId() {
		return changeActionId;
	}
	
	/** 
	 * @about Setter method to set Change Action Id
	 * @param String
	 * @return void
	 * @since DSM 2018x.5
	 */
	public void setChangeActionId(String changeActionId) {
		this.changeActionId = changeActionId;
	}
	
	/** 
	 * @about getter method to get artwork promoted flag
	 * @return boolean
	 * @since DSM 2018x.5
	 */
	public boolean isArtworkPromoted() {
		return isArtworkPromoted;
	}
	
	/** 
	 * @about setter method to set artwork promoted flag
	 * param boolean
	 * @return void
	 * @since DSM 2018x.5
	 */
	public void setArtworkPromoted(boolean isArtworkPromoted) {
		this.isArtworkPromoted = isArtworkPromoted;
	}
}
