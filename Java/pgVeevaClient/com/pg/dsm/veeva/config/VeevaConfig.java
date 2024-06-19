/*
 **   VeevaConfig.java
 **   Description - Introduced as part of Veeva integration.      
 **   Bean to create all required folders for Veeva extraction.
 **
 */
package com.pg.dsm.veeva.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;

import matrix.db.Context;
import matrix.util.StringList;

public class VeevaConfig implements Veeva {
	boolean isLoaded;
	Context context;
	DomainObject busObj;
	String attrStartDate;
	String attrEndDate;
	String attrConfigActive;
	String attrRetryCount;
	String attrAdminEmail;
	String veevaFormatStartDate;
	String veevaFormatEndDate;
	String fromApprovedForDistributionDate;
	String toApprovedForDistributionDate;
	Date mtxFormatStartDate;
	Date mtxFormatEndDate;
	Date utcFormatStartDate;
	Date utcFormatEndDate;
	String nextStartDate;
	String nextEndDate;
	SimpleDateFormat mtxDateFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
	/** 
	 * @about Constructor
	 * @param Create - builder class
	 * @since DSM 2018x.3
	 */
	private VeevaConfig(Builder builder) {
		this.context = builder.context;
		this.busObj = builder.busObj;
		this.attrStartDate = builder.attrStartDate;
		this.attrEndDate = builder.attrEndDate;
		this.attrConfigActive = builder.attrConfigActive;
		this.attrRetryCount = builder.attrRetryCount;
		this.attrAdminEmail = builder.attrAdminEmail;
		this.veevaFormatStartDate = builder.veevaFormatStartDate;
		this.veevaFormatEndDate = builder.veevaFormatEndDate;
		this.fromApprovedForDistributionDate = builder.fromApprovedForDistributionDate;
		this.toApprovedForDistributionDate = builder.toApprovedForDistributionDate;
		this.mtxFormatStartDate = builder.mtxFormatStartDate;
		this.mtxFormatEndDate = builder.mtxFormatEndDate;
		this.utcFormatStartDate = builder.utcFormatStartDate;
		this.utcFormatEndDate = builder.utcFormatEndDate;
		this.isLoaded = builder.isLoaded;
	}
	/**
	 * @return the isLoaded
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	/**
	 * @param isLoaded the isLoaded to set
	 */
	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
	/**
	 * @return the nextStartDate
	 */
	public String getNextStartDate() {
		return nextStartDate;
	}
	/**
	 * @param nextStartDate the nextStartDate to set
	 */
	public void setNextStartDate(String nextStartDate) {
		this.nextStartDate = nextStartDate;
	}
	/**
	 * @return the nextEndDate
	 */
	public String getNextEndDate() {
		return nextEndDate;
	}
	/**
	 * @param nextEndDate the nextEndDate to set
	 */
	public void setNextEndDate(String nextEndDate) {
		this.nextEndDate = nextEndDate;
	}
	/**
	 * @return the mtxDateFormat
	 */
	public SimpleDateFormat getMtxDateFormat() {
		return mtxDateFormat;
	}
	/**
	 * @param mtxDateFormat the mtxDateFormat to set
	 */
	public void setMtxDateFormat(SimpleDateFormat mtxDateFormat) {
		this.mtxDateFormat = mtxDateFormat;
	}
	/** 
	 * @about Method to update matrix next start date 
	 * @return void
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 */
	public void updateNextRunStartDate() throws FrameworkException {
		if(Utility.isNotNullEmpty(attrEndDate)) {
			nextStartDate = attrEndDate;
		} else {
			nextStartDate = mtxDateFormat.format(new Date());
		}	
		busObj.setAttributeValue(context, ATTRIBUTE_PG_CONFIG_COMMON_START_DATE, nextStartDate);
	}
	/** 
	 * @about Method to update matrix next end date 
	 * @return void
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 */
	public void updateNextRunEndDate() throws FrameworkException {
		nextEndDate = Veeva.EMPTY_STRING;
		busObj.setAttributeValue(context, ATTRIBUTE_PG_CONFIG_COMMON_ENDED_DATE, nextEndDate);
	}
	/** 
	 * @about Method to roll-back matrix start date
	 * @return void
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 */
	public void rollbackStartDate() throws FrameworkException {
		busObj.setAttributeValue(context, ATTRIBUTE_PG_CONFIG_COMMON_START_DATE, attrStartDate);
	}
	/** 
	 * @about Method to roll-back matrix end date 
	 * @return void
	 * @since DSM 2018x.3
	 */
	public void rollbackEndDate() throws FrameworkException {
		busObj.setAttributeValue(context, ATTRIBUTE_PG_CONFIG_COMMON_ENDED_DATE, Veeva.EMPTY_STRING);
	}
	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	/**
	 * @return the busObj
	 */
	public DomainObject getBusObj() {
		return busObj;
	}
	/**
	 * @param busObj the busObj to set
	 */
	public void setBusObj(DomainObject busObj) {
		this.busObj = busObj;
	}
	/**
	 * @return the attrStartDate
	 */
	public String getAttrStartDate() {
		return attrStartDate;
	}
	/**
	 * @param attrStartDate the attrStartDate to set
	 */
	public void setAttrStartDate(String attrStartDate) {
		this.attrStartDate = attrStartDate;
	}
	/**
	 * @return the attrEndDate
	 */
	public String getAttrEndDate() {
		return attrEndDate;
	}
	/**
	 * @param attrEndDate the attrEndDate to set
	 */
	public void setAttrEndDate(String attrEndDate) {
		this.attrEndDate = attrEndDate;
	}
	/**
	 * @return the attrConfigActive
	 */
	public String getAttrConfigActive() {
		return attrConfigActive;
	}
	/**
	 * @param attrConfigActive the attrConfigActive to set
	 */
	public void setAttrConfigActive(String attrConfigActive) {
		this.attrConfigActive = attrConfigActive;
	}
	/**
	 * @return the attrRetryCount
	 */
	public String getAttrRetryCount() {
		return attrRetryCount;
	}
	/**
	 * @param attrRetryCount the attrRetryCount to set
	 */
	public void setAttrRetryCount(String attrRetryCount) {
		this.attrRetryCount = attrRetryCount;
	}
	/**
	 * @return the attrAdminEmail
	 */
	public String getAttrAdminEmail() {
		return attrAdminEmail;
	}
	/**
	 * @param attrAdminEmail the attrAdminEmail to set
	 */
	public void setAttrAdminEmail(String attrAdminEmail) {
		this.attrAdminEmail = attrAdminEmail;
	}
	/**
	 * @return the veevaFormatStartDate
	 */
	public String getVeevaFormatStartDate() {
		return veevaFormatStartDate;
	}
	/**
	 * @param veevaFormatStartDate the veevaFormatStartDate to set
	 */
	public void setVeevaFormatStartDate(String veevaFormatStartDate) {
		this.veevaFormatStartDate = veevaFormatStartDate;
	}
	/**
	 * @return the veevaFormatEnDate
	 */
	public String getVeevaFormatEnDate() {
		return veevaFormatEndDate;
	}
	/**
	 * @param veevaFormatEnDate the veevaFormatEnDate to set
	 */
	public void setVeevaFormatEnDate(String veevaFormatEndDate) {
		this.veevaFormatEndDate = veevaFormatEndDate;
	}
	/**
	 * @return the fromApprovedForDistributionDate
	 */
	public String getFromApprovedForDistributionDate() {
		return fromApprovedForDistributionDate;
	}
	/**
	 * @param fromApprovedForDistributionDate the fromApprovedForDistributionDate to set
	 */
	public void setFromApprovedForDistributionDate(String fromApprovedForDistributionDate) {
		this.fromApprovedForDistributionDate = fromApprovedForDistributionDate;
	}
	/**
	 * @return the toApprovedForDistributionDate
	 */
	public String getToApprovedForDistributionDate() {
		return toApprovedForDistributionDate;
	}
	/**
	 * @param toApprovedForDistributionDate the toApprovedForDistributionDate to set
	 */
	public void setToApprovedForDistributionDate(String toApprovedForDistributionDate) {
		this.toApprovedForDistributionDate = toApprovedForDistributionDate;
	}
	/**
	 * @return the mtxFormatStartDate
	 */
	public Date getMtxFormatStartDate() {
		return mtxFormatStartDate;
	}
	/**
	 * @param mtxFormatStartDate the mtxFormatStartDate to set
	 */
	public void setMtxFormatStartDate(Date mtxFormatStartDate) {
		this.mtxFormatStartDate = mtxFormatStartDate;
	}
	/**
	 * @return the mtxFormatEndDate
	 */
	public Date getMtxFormatEndDate() {
		return mtxFormatEndDate;
	}
	/**
	 * @param mtxFormatEndDate the mtxFormatEndDate to set
	 */
	public void setMtxFormatEndDate(Date mtxFormatEndDate) {
		this.mtxFormatEndDate = mtxFormatEndDate;
	}
	/**
	 * @return the utcFormatStartDate
	 */
	public Date getUtcFormatStartDate() {
		return utcFormatStartDate;
	}
	/**
	 * @param utcFormatStartDate the utcFormatStartDate to set
	 */
	public void setUtcFormatStartDate(Date utcFormatStartDate) {
		this.utcFormatStartDate = utcFormatStartDate;
	}
	/**
	 * @return the utcFormatEndDate
	 */
	public Date getUtcFormatEndDate() {
		return utcFormatEndDate;
	}
	/**
	 * @param utcFormatEndDate the utcFormatEndDate to set
	 */
	public void setUtcFormatEndDate(Date utcFormatEndDate) {
		this.utcFormatEndDate = utcFormatEndDate;
	}
	public static class Builder {

		Context context;
		DomainObject busObj;
		Map<?,?> infoMap;
		String attrStartDate;
		String attrEndDate;
		String attrConfigActive;
		String attrRetryCount;
		String attrAdminEmail;
		String veevaFormatStartDate;
		String veevaFormatEndDate;
		String fromApprovedForDistributionDate;
		String toApprovedForDistributionDate;
		Date mtxFormatStartDate;
		Date mtxFormatEndDate;
		Date utcFormatStartDate;
		Date utcFormatEndDate;
		SimpleDateFormat mtxDateFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
		SimpleDateFormat veevaDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat distributionDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		boolean isLoaded;
		/** 
		 * @about Constructor
		 * @param DomainObject - object
		 * @throws FrameworkException 
		 * @since DSM 2018x.3
		 */
		public Builder(Context context, DomainObject busObj) throws FrameworkException {
			this.context = context;
			this.busObj = busObj;
			this.infoMap = getInfo();
			this.isLoaded = false;
		}
		/** 
		 * @about Setter method - set start date in bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setStartDate() {
			this.attrStartDate = (String)infoMap.get(SELECT_ATTRIBUTE_PG_CONFIG_COMMON_START_DATE);
			return this;
		}
		/** 
		 * @about Setter method - set end date in bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setEndDate() {
			this.attrEndDate = (String)infoMap.get(SELECT_ATTRIBUTE_PG_CONFIG_COMMON_ENDED_DATE);
			if(Utility.isNullEmpty(attrEndDate))
				attrEndDate = mtxDateFormat.format(new Date());
			return this;
		}
		/** 
		 * @about Setter method - set start config active in bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setConfigActive() {
			this.attrConfigActive = (String)infoMap.get(SELECT_ATTRIBUTE_PGCONFIGISACTIVE);
			return this;
		}
		/** 
		 * @about Setter method - set retry count in bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setRetryCount() {
			this.attrRetryCount = (String)infoMap.get(SELECT_ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT);
			return this;
		}
		/** 
		 * @about Setter method - set admin email in bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setAdminEmail() {
			this.attrAdminEmail = (String)infoMap.get(SELECT_ATTRIBUTE_PGCONFIGCOMMONADMINMAILID);
			return this;
		}
		/** 
		 * @about Setter method - set matrix format start date.
		 * @return Builder
		 * @throws ParseException 
		 * @since DSM 2018x.3
		 */
		public Builder setMatrixFormatStartDate() throws ParseException {
			this.mtxFormatStartDate = mtxDateFormat.parse(attrStartDate);
			return this;
		}
		/** 
		 * @about Setter method - set matrix format end date.
		 * @return Builder
		 * @throws ParseException 
		 * @since DSM 2018x.3
		 */
		public Builder setMatrixFormatEndDate() throws ParseException {
			this.mtxFormatEndDate = mtxDateFormat.parse(attrEndDate);
			return this;
		}
		/** 
		 * @about Setter method - set utc format start date.
		 * @return Builder
		 * @throws ParseException 
		 * @since DSM 2018x.3
		 */
		@SuppressWarnings("deprecation")
		public Builder setUTCFormatStartDate() throws ParseException {
			mtxDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			this.utcFormatStartDate = new Date(mtxDateFormat.format(mtxFormatStartDate));
			return this;
		}
		/** 
		 * @about Setter method - set utc format end date.
		 * @return Builder
		 * @throws ParseException 
		 * @since DSM 2018x.3
		 */
		@SuppressWarnings("deprecation")
		public Builder setUTCFormatEndDate() throws ParseException {
			mtxDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			this.utcFormatEndDate = new Date(mtxDateFormat.format(mtxFormatEndDate));
			return this;
		}
		/** 
		 * @about Setter method - set veeva format start date.
		 * @return Builder
		 * @throws ParseException 
		 * @since DSM 2018x.3
		 */
		public Builder setVeevaFormatStartDate() throws ParseException {
			this.veevaFormatStartDate = veevaDateFormat.format(utcFormatStartDate);
			return this;
		}
		/** 
		 * @about Setter method - set veeva format end date.
		 * @return Builder
		 * @throws ParseException 
		 * @since DSM 2018x.3
		 */
		public Builder setVeevaFormatEndDate() throws ParseException {
			this.veevaFormatEndDate = veevaDateFormat.format(utcFormatEndDate);
			return this;
		}
		/** 
		 * @about Setter method - set 'distribution from' in bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setFromApprovedForDistributionDate() {
			this.fromApprovedForDistributionDate = distributionDateFormat.format(mtxFormatStartDate);
			return this;
		}
		/** 
		 * @about Setter method - set 'distribution to' in bean
		 * @return Builder
		 * @since DSM 2018x.3
		 */
		public Builder setToApprovedForDistributionDate() {
			this.toApprovedForDistributionDate = distributionDateFormat.format(mtxFormatEndDate);
			return this;
		}
		/** 
		 * @about Builder method
		 * @return VeevaConfig
		 * @since DSM 2018x.3
		 */
		public VeevaConfig build() {
			this.isLoaded = true;
			return new VeevaConfig(this);
		}
		/** 
		 * @about Setter method - set info map in bean
		 * @return Map
		 * @throws FrameworkException
		 * @since DSM 2018x.3
		 */
		public Map<?,?> getInfo() throws FrameworkException {
			StringList selectList = new StringList();
			selectList.addElement(SELECT_ATTRIBUTE_PG_CONFIG_COMMON_START_DATE);
			selectList.addElement(SELECT_ATTRIBUTE_PG_CONFIG_COMMON_ENDED_DATE);
			selectList.addElement(SELECT_ATTRIBUTE_PGCONFIGISACTIVE);
			selectList.addElement(SELECT_ATTRIBUTE_PGCONFIGCOMMONRETRYCOUNT);
			selectList.addElement(SELECT_ATTRIBUTE_PGCONFIGCOMMONADMINMAILID);
			return busObj.getInfo(context, selectList);
		}
	}
}
