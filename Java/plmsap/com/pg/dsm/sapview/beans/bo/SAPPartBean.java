package com.pg.dsm.sapview.beans.bo;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.sapview.config.SAPConfig;
import com.pg.dsm.sapview.config.SAPConstants;
import com.pg.dsm.sapview.utils.StringHelper;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class SAPPartBean {

	private String id;
	private String type;
	private String name;
	private String revision;
	private boolean isBOMeDeliveryParentToSAP;
	private boolean isBOMeDeliveryToSAP;
	private boolean isValidParentPart;
	private boolean isValidAlternatePart;
	private boolean isValidChildPart;
	private boolean isLoaded;

	/**
	 * 
	 */
	public SAPPartBean() {
	}

	/**
	 * @param objectMap
	 * @param config
	 */
	public SAPPartBean(Map<Object, Object> objectMap, SAPConfig config) {
		this.isLoaded = Boolean.FALSE;
		this.id = StringHelper.convertToString(objectMap, DomainConstants.SELECT_ID);
		this.type = StringHelper.convertToString(objectMap, DomainConstants.SELECT_TYPE);
		this.name = StringHelper.convertToString(objectMap, DomainConstants.SELECT_NAME);
		this.revision = StringHelper.convertToString(objectMap, DomainConstants.SELECT_REVISION);
		this.isBOMeDeliveryToSAP = StringHelper.convertToBoolean(objectMap,
				SAPConstants.SELECT_ATTRIBUTE_PGBOMEDELIVERY);
		this.isBOMeDeliveryParentToSAP = StringHelper.convertToBoolean(objectMap,
				SAPConstants.SELECT_ATTRIBUTE_PGBOMEDELIVERYPARENT);
		this.isValidParentPart = config.getProperties().getSapParentTypes().contains(this.type);
		this.isValidAlternatePart = config.getProperties().getSapAlternateTypes().contains(this.type);
		this.isValidChildPart = config.getProperties().getSapChildTypes().contains(this.type);
		this.isLoaded = Boolean.TRUE;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the revision
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * @param revision the revision to set
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * @return the isValidParentPart
	 */
	public boolean isValidParentPart() {
		return isValidParentPart;
	}

	/**
	 * @param isValidParentPart the isValidParentPart to set
	 */
	public void setValidParentPart(boolean isValidParentPart) {
		this.isValidParentPart = isValidParentPart;
	}

	/**
	 * @return the isValidAlternatePart
	 */
	public boolean isValidAlternatePart() {
		return isValidAlternatePart;
	}

	/**
	 * @param isValidAlternatePart the isValidAlternatePart to set
	 */
	public void setValidAlternatePart(boolean isValidAlternatePart) {
		this.isValidAlternatePart = isValidAlternatePart;
	}

	/**
	 * @return the isValidChildPart
	 */
	public boolean isValidChildPart() {
		return isValidChildPart;
	}

	/**
	 * @param isValidChildPart the isValidChildPart to set
	 */
	public void setValidChildPart(boolean isValidChildPart) {
		this.isValidChildPart = isValidChildPart;
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
	 * @return the isBOMeDeliveryParentToSAP
	 */
	public boolean isBOMeDeliveryParentToSAP() {
		return isBOMeDeliveryParentToSAP;
	}

	/**
	 * @param isBOMeDeliveryParentToSAP the isBOMeDeliveryParentToSAP to set
	 */
	public void setBOMeDeliveryParentToSAP(boolean isBOMeDeliveryParentToSAP) {
		this.isBOMeDeliveryParentToSAP = isBOMeDeliveryParentToSAP;
	}

	/**
	 * @return the isBOMeDeliveryToSAP
	 */
	public boolean isBOMeDeliveryToSAP() {
		return isBOMeDeliveryToSAP;
	}

	/**
	 * @param isBOMeDeliveryToSAP the isBOMeDeliveryToSAP to set
	 */
	public void setBOMeDeliveryToSAP(boolean isBOMeDeliveryToSAP) {
		this.isBOMeDeliveryToSAP = isBOMeDeliveryToSAP;
	}

	/**
	 * @return
	 */
	public SAPPartBean copy() {
		SAPPartBean bean = new SAPPartBean();
		bean.setId(this.id);
		bean.setName(this.name);
		bean.setRevision(this.revision);
		bean.setType(this.type);
		bean.setValidAlternatePart(this.isValidAlternatePart);
		bean.setValidChildPart(this.isValidChildPart);
		bean.setValidParentPart(this.isValidParentPart);
		bean.setBOMeDeliveryToSAP(this.isBOMeDeliveryParentToSAP);
		bean.setBOMeDeliveryParentToSAP(this.isBOMeDeliveryParentToSAP);
		return bean;
	}

}