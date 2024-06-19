/*
* Java File Name: MarketRegistrationBean.java
* Created By: PLM DSM-2018x.6 - Sogeti
* Clone From/Reference: NA
* Purpose:  This File contains market registration details bean.
* Change History: Added by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance.
*/

package com.pg.v4.util.marketclearance.bean;

public class MarketRegistrationBean {

	private String gpsApprovalStatus;
	private String registrationStatus;
	private String registrationExpirationDate;
	private String packingSite;
	private String bulkMakingManufacturingSite;
	private String restrictions;
	
	/**
	 * This method return GPS approval Status
	 * @return
	 */
	public String getGpsApprovalStatus() {
		return gpsApprovalStatus;
	}

	/**
	 * This method update GPS approval Status
	 * @param gpsApprovalStatus
	 */
	public void setGpsApprovalStatus(String gpsApprovalStatus) {
		this.gpsApprovalStatus = gpsApprovalStatus;
	}

	/**
	 * This method return Registration Status
	 * @return
	 */
	public String getRegistrationStatus() {
		return registrationStatus;
	}

	/**
	 * This method update Registration Status
	 * @param registrationStatus
	 */
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
	}

	/**
	 * This method return Registration Expiration Date
	 * @return
	 */
	public String getRegistrationExpirationDate() {
		return registrationExpirationDate;
	}

	/**
	 * This method update Registration Expiration Date
	 * @param registrationExpirationDate
	 */
	public void setRegistrationExpirationDate(String registrationExpirationDate) {
		this.registrationExpirationDate = registrationExpirationDate;
	}

	/**
	 * This method return Packing Site
	 * @return
	 */
	public String getPackingSite() {
		return packingSite;
	}

	/**
	 * This method update Packing Site
	 * @param packingSite
	 */
	public void setPackingSite(String packingSite) {
		this.packingSite = packingSite;
	}

	/**
	 * This method return Bulk Making Manufacturing Site
	 * @return
	 */
	public String getBulkMakingManufacturingSite() {
		return bulkMakingManufacturingSite;
	}

	/**
	 * This method update Bulk Making Manufacturing Site
	 * @param bulkMakingManufacturingSite
	 */
	public void setBulkMakingManufacturingSite(String bulkMakingManufacturingSite) {
		this.bulkMakingManufacturingSite = bulkMakingManufacturingSite;
	}

	/**
	 * This method return plant restrictions
	 * @return
	 */
	public String getRestrictions() {
		return restrictions;
	}

	/**
	 * This method update plant restrictions
	 * @param restrictions
	 */
	public void setRestrictions(String restrictions) {
		this.restrictions = restrictions;
	}
}
