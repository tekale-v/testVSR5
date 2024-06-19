/*
* Java File Name: MarketClearanceCalculation.java
* Created By: PLM DSM-2018x.6 - Sogeti
* Clone From/Reference: NA
* Purpose:  This File contains utility methods to calculate overall market clearance.
* Change History: Added by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance.
*/

package com.pg.v4.util.marketclearance.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.util.marketclearance.bean.MarketRegistrationBean;

import matrix.db.Context;
import matrix.util.StringList;

public class MarketClearanceCalculation {
	
	/**
	 * This method will update overall clearance status of the input market's
	 * @param marketList - list of markets
	 * @return
	 * @throws ParseException 
	 */
	public String getOverallClearanceStatus(Map<String, StringList> mapRegistrationDetails) throws ParseException {
		List<MarketRegistrationBean> marketRegistrationBeans = ProductPart.Expand.getMarketRegistrations(mapRegistrationDetails, false);
		return calculateRegistrationClearanceStatus(marketRegistrationBeans); 
	}
	 
	/**
	 * This method return expand program output in ProductPart class object
	 * @param relationshipWhere - where condition for fetch particular market registration's details
	 * @return - ProductPart bean
	 * @throws FrameworkException 
	 * @throws Exception 
	 */
	
	public ProductPart getConnectedMarketBeans(Context context,DomainObject domObj) throws FrameworkException  {
		return new ProductPart.Expand(context, domObj)
				.setExpandTo(Boolean.FALSE)
				.setRecurseLevel((short) 1)
				.setLimit(0).setRelationshipPattern(pgV3Constants.SELECT_RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE)
				.setTypePattern(CPNCommonConstants.TYPE_COUNTRY)
				.setRelationshipObjectSelectable(setRelSelects())
				.setExpandFrom(Boolean.TRUE)
				.perform();
	}
	
	/**
	 * This method return calculated market overall clearance status based on connected multi-registration's
	 * @param marketRegistrationBeans - Market registration's bean
	 * @return -String calculated overall clearance status
	 * @throws ParseException 
	 */
	public String calculateRegistrationClearanceStatus(List<MarketRegistrationBean> marketRegistrationBeans) throws ParseException {
		String overallClearanceStatus = DomainConstants.EMPTY_STRING;
		if (marketRegistrationBeans != null && !marketRegistrationBeans.isEmpty()) {
			int marketRegistrationSize = marketRegistrationBeans.size();
			boolean isCleared = true;
			boolean isNotCleared = true;
			String strOverAllClearanceStatus;
			for (int j = 0; j < marketRegistrationSize; j++) {

				strOverAllClearanceStatus = calculateRegistrationClearanceStatus(marketRegistrationBeans.get(j));
				
				if (!pgV3Constants.CLEARANCE_STATUS_CLEARED.equals(strOverAllClearanceStatus)) {
					isCleared = false;
				}
				if (!pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED.equals(strOverAllClearanceStatus)) {
					isNotCleared = false;
				}

				if(!isCleared && !isNotCleared) {
					break;
				}
				
			}
			if (isCleared) {
				overallClearanceStatus = pgV3Constants.CLEARANCE_STATUS_CLEARED;
			} else if (isNotCleared) {
				overallClearanceStatus = pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED;
			} else {
				overallClearanceStatus = pgV3Constants.CLEARANCE_STATUS_CLEARED_WITH_CONDITIONS;
			}
		}
		return overallClearanceStatus;
	}

	/**
	 * This method is calculate market clearance status on single registration
	 * @param marketRegistrationBean -Market registration bean
	 * @return
	 * @throws ParseException 
	 */
	protected String calculateRegistrationClearanceStatus(MarketRegistrationBean marketRegistrationBean) throws ParseException {
		String strOverAllClearanceStatus = null;
		String strRegistrationEndDate = marketRegistrationBean.getRegistrationExpirationDate();
		String strPSRAAppStatus = marketRegistrationBean.getGpsApprovalStatus();
		String strRegistrationStatus = marketRegistrationBean.getRegistrationStatus();
		String strPgPackingSite = marketRegistrationBean.getPackingSite();
		String strPlantRestriction = marketRegistrationBean.getRestrictions();
		String strPgManufacturingSite = marketRegistrationBean.getBulkMakingManufacturingSite();
		Date dRegEndDate = convertDate(strRegistrationEndDate);
		//Added and Modified by DSM(Sogeti) 2018x.6 for COS Change Requirment CR-427/Req 8802 Starts
		if ((pgV3Constants.PSRA_APPROVAL_STATUS_NOT_APPROVED.equalsIgnoreCase(strPSRAAppStatus)) ||
				(pgV3Constants.REG_STATUS_IN_PROGRESS.equalsIgnoreCase(strRegistrationStatus)) ||
				(pgV3Constants.REG_STATUS_SUBMITTED_FOR_REG.equalsIgnoreCase(strRegistrationStatus)) ||
				(pgV3Constants.PSRA_APPROVAL_STATUS_REG_REQUIRED.equalsIgnoreCase(strPSRAAppStatus) && pgV3Constants.DOSSIER_SUBMITTED_TO_CUSTOMER.equalsIgnoreCase(strRegistrationStatus)) ||
				((null != dRegEndDate) && isDateBeforeToday(dRegEndDate))) {
			strOverAllClearanceStatus = pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED;
					//Added and Modified by DSM(Sogeti) 2018x.6 for COS Change Requirment CR-427/Req 8802 Ends
		}else if (pgV3Constants.PSRA_APPROVAL_STATUS_REG_REQUIRED.equalsIgnoreCase(strPSRAAppStatus)
				&& strRegistrationStatus.isEmpty()) {
			strOverAllClearanceStatus = pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED;
		}else if (((pgV3Constants.PSRA_APPROVAL_STATUS_APPROVED.equalsIgnoreCase(strPSRAAppStatus))
				&& (strRegistrationStatus.isEmpty()) && (!strRegistrationEndDate.isEmpty()))) {
			if(((UIUtil.isNullOrEmpty(strPlantRestriction) && UIUtil.isNullOrEmpty(strPgPackingSite)
					&& UIUtil.isNullOrEmpty(strPgManufacturingSite))
					|| (UIUtil.isNotNullAndNotEmpty(strPlantRestriction)
							&& UIUtil.isNotNullAndNotEmpty(strPgPackingSite)
							&& UIUtil.isNotNullAndNotEmpty(strPgManufacturingSite)))) {
				strOverAllClearanceStatus = pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED;
			}
		}else if (UIUtil.isNullOrEmpty(strPSRAAppStatus)) {
			strOverAllClearanceStatus = pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED;
		}else if ((UIUtil.isNotNullAndNotEmpty(strPlantRestriction)) || UIUtil.isNotNullAndNotEmpty(strPgPackingSite)
				|| UIUtil.isNotNullAndNotEmpty(strPgManufacturingSite)) {
			strOverAllClearanceStatus = pgV3Constants.CLEARANCE_STATUS_CLEARED_WITH_CONDITIONS;
		} else {
			strOverAllClearanceStatus = pgV3Constants.CLEARANCE_STATUS_CLEARED;
		}
		return strOverAllClearanceStatus;
	}
	
	 /**
	  * Method to update overall clearance status
	 * @param relId
	 * @param overallClearanceStatus
	 * @throws FrameworkException
	 */
	public void updateOverallClearanceStatus(Context context,String relId, String overallClearanceStatus) throws FrameworkException
	{
		DomainRelationship.setAttributeValue(context, relId, pgV3Constants.ATTRIBUTE_PGOVERALLCLEARANCESTATUS, overallClearanceStatus);
	}
	/**
	 * This method is to convert date in given format
	 * @param strRegistrationEndDate -date in string format
	 * @return
	 * @throws ParseException 
	 */
	private Date convertDate(String strRegistrationEndDate) throws ParseException {
		SimpleDateFormat smdfOfPLM = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
		SimpleDateFormat smdfOfRMT = new SimpleDateFormat("MM/dd/yyyy");
		Date dRegEndDate = null;
	
		if (UIUtil.isNotNullAndNotEmpty(strRegistrationEndDate)) {
			strRegistrationEndDate = smdfOfPLM.format(smdfOfRMT.parse(strRegistrationEndDate.trim()));
			dRegEndDate = new Date(strRegistrationEndDate);
		}
		return dRegEndDate;
	}
	
	/**
	 * This method is used for cron to check any registration is expired or not
	 * @param marketRegistrationBeans
	 * @return
	 */
	public boolean isAnyRegistrationExpired(List<MarketRegistrationBean> marketRegistrationBeans,Date today) {
		boolean isExpired=false;
		if (marketRegistrationBeans != null && !marketRegistrationBeans.isEmpty()) {
			int marketRegistrationSize = marketRegistrationBeans.size();
			String expirationDate;
			Date expDateDt =null;
			for (int j = 0; j < marketRegistrationSize; j++) {
				expirationDate=marketRegistrationBeans.get(j).getRegistrationExpirationDate();
				if(null!= expirationDate && (!expirationDate.isEmpty())){
					expDateDt = new Date(expirationDate);
					if(expDateDt.before(today)){
						isExpired=true;
						break;
					}
				}
			}
		}
		return isExpired;
	}
	
	/**
	 * This method will return true if passed date is < today.Will return false if
	 * date is >= today
	 * 
	 * @param d1
	 * 
	 * @return
	 * 
	 */
	private boolean isDateBeforeToday(Date d1) {

		if (null == d1)
			return false;

		Calendar expDate = Calendar.getInstance();

		expDate.setTime(d1);

		Calendar today = Calendar.getInstance();

		if (expDate.get(Calendar.YEAR) > today.get(Calendar.YEAR))

			return false;

		else if (expDate.get(Calendar.YEAR) < today.get(Calendar.YEAR))

			return true;

		// year is same

		if (expDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))

			return false;

		else if (expDate.get(Calendar.MONTH) < today.get(Calendar.MONTH))

			return true;

		// month is also same

		return (expDate.get(Calendar.DAY_OF_MONTH) >= today.get(Calendar.DAY_OF_MONTH))? Boolean.FALSE : Boolean.TRUE;
	}
	
	/**
	 * This method is retrieve required relationship select tables
	 * @return - StringList
	 */
	private static StringList setRelSelects() {
		StringList selectList = new StringList(7);
		selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPSRAAPPROVALSTATUS);
		selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONSTATUS);
		selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONENDDATE);
		selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTRESTRICTION);
		selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGSITE);
		selectList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMANUFACTURINGSITE);
		selectList.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
		return selectList;
	}
}
