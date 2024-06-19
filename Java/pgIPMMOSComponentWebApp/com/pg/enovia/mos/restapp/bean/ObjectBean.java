package com.pg.enovia.mos.restapp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.matrixone.apps.domain.util.MapList;


public class ObjectBean implements Serializable {
	String title;
	String isArtExist;
	String isFCExist;
	String isNonReleasedArtExist;
	String country;
	String id;
	String name;
	String onClickUrl;
	String revision;
	String type;
	private MapList countryList = new MapList();
	private List<ObjectBean> child;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the isArtExist
	 */
	public String getIsArtExist() {
		return isArtExist;
	}

	/**
	 * @param isArtExist the isArtExist to set
	 */
	public void setIsArtExist(String isArtExist) {
		this.isArtExist = isArtExist;
	}

	/**
	 * @return the isFCExist
	 */
	public String getIsFCExist() {
		return isFCExist;
	}

	/**
	 * @param isFCExist the isFCExist to set
	 */
	public void setIsFCExist(String isFCExist) {
		this.isFCExist = isFCExist;
	}


	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the countryList
	 */
	public MapList getCountryList() {
		return countryList;
	}

	/**
	 * @param countryList the countryList to set
	 */
	public void setCountryList(MapList countryList) {
		this.countryList = countryList;
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
	 * @return the onClickUrl
	 */
	public String getOnClickUrl() {
		return onClickUrl;
	}

	/**
	 * @param onClickUrl the onClickUrl to set
	 */
	public void setOnClickUrl(String onClickUrl) {
		this.onClickUrl = onClickUrl;
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
	 * @return the child
	 */
	public List<ObjectBean> getChild() {
		if (this.child==null) {
			this.child=new ArrayList<>();
		}
		return this.child;
	}

	/**
	 * @param child the child to set
	 */
	public void setChild(List<ObjectBean> child) {
		
		this.child = child;
	}

	/**
	 * @return the isNonReleasedArtExist
	 */
	public String getIsNonReleasedArtExist() {
		return isNonReleasedArtExist;
	}

	/**
	 * @param isNonReleasedArtExist the isNonReleasedArtExist to set
	 */
	public void setIsNonReleasedArtExist(String isNonReleasedArtExist) {
		this.isNonReleasedArtExist = isNonReleasedArtExist;
	}

}
