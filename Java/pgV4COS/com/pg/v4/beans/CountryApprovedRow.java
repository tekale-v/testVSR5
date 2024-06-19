package com.pg.v4.beans;

import java.io.Serializable;

public class CountryApprovedRow implements Serializable{
	
	String COUNTRY_REQUESTED ;
	String MODIFIED_BY;
	String OVERALL_CLEARANCE_STATUS ;
	String PSRA_APPROVAL_STATUS;
	public String getCOUNTRY_REQUESTED() {
		return COUNTRY_REQUESTED;
	}
	public void setCOUNTRY_REQUESTED(String cOUNTRY_REQUESTED) {
		COUNTRY_REQUESTED = cOUNTRY_REQUESTED;
	}
	public String getMODIFIED_BY() {
		return MODIFIED_BY;
	}
	public void setMODIFIED_BY(String mODIFIED_BY) {
		MODIFIED_BY = mODIFIED_BY;
	}
	public String getOVERALL_CLEARANCE_STATUS() {
		return OVERALL_CLEARANCE_STATUS;
	}
	public void setOVERALL_CLEARANCE_STATUS(String oVERALL_CLEARANCE_STATUS) {
		OVERALL_CLEARANCE_STATUS = oVERALL_CLEARANCE_STATUS;
	}
	public String getPSRA_APPROVAL_STATUS() {
		return PSRA_APPROVAL_STATUS;
	}
	public void setPSRA_APPROVAL_STATUS(String pSRA_APPROVAL_STATUS) {
		PSRA_APPROVAL_STATUS = pSRA_APPROVAL_STATUS;
	}
	
	
	
	

}
