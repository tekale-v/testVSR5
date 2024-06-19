package com.pg.v4.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.util.MapList;

public class pgCOSDetailBean {
	private String id;
	private String type;
	private String name;
	private String rev;
	private String description;
	private String lastCOSRunDate;
	
	private String parentid;
	private String parentType;
	private String parentName;
	private String parentRev;
	private String parentDescription;
	private String IntermediateID;
	public String getIntermediateID() {
		return IntermediateID;
	}


	public void setIntermediateID(String intermediateID) {
		IntermediateID = intermediateID;
	}



	private String IntermediateName;
	public String getIntermediateName() {
		return IntermediateName;
	}


	public void setIntermediateName(String intermediateName) {
		IntermediateName = intermediateName;
	}



	private String isFCExist;
	private String isArtExist;
	private String isNonReleasedArtExist;	
	private MapList countryList = new MapList();
	public List<pgCOSDetailBean> childrenPD = new ArrayList<pgCOSDetailBean>();
	

	public String getParentDescription() {
		return parentDescription;
	}


	public void setParentDescription(String parentDescription) {
		this.parentDescription = parentDescription;
	}


	public String getParentType() {
		return parentType;
	}


	public void setParentType(String parentType) {
		this.parentType = parentType;
	}


	public String getParentName() {
		return parentName;
	}


	public void setParentName(String parentName) {
		this.parentName = parentName;
	}


	public String getParentRev() {
		return parentRev;
	}


	public void setParentRev(String parentRev) {
		this.parentRev = parentRev;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getRev() {
		return rev;
	}


	public void setRev(String rev) {
		this.rev = rev;
	}
	
	//Added by V4-2013x.4 for Finished Product Part
	public void setIsFCExist(String isFCExist) {
		this.isFCExist = isFCExist;
	}
	
	public String getIsFCExist() {
		return isFCExist;
	}
	
	public void setIsArtExist(String isArtExist) {
		this.isArtExist = isArtExist;
	}
	
	public String getIsArtExist() {
		return isArtExist;
	}
	
	/*public void setIntermediateid(String IntermediateID) {
		System.out.println("\n Inside setIntermediateid to set interMediate id "+IntermediateID);
		this.IntermediateID = IntermediateID;
	}
	
	public void setIntermediatename(String IntermediateName) {
		System.out.println("\n Inside setIntermediatename to set interMediate name "+IntermediateName);
		this.IntermediateName = IntermediateName;
	}
	
	public String getIntermediateID() {
		System.out.println("\n Inside getIntermediateID to get interMediate name ");
		return IntermediateID;
	}
	
	public String getIntermediateName() {
		System.out.println("\n Inside getIntermediateName to set interMediate name ");
		return IntermediateName;
	}*/
	
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getLastCOSRunDate() {
		return lastCOSRunDate;
	}


	public void setLastCOSRunDate(String lastCOSRunDate) {
		this.lastCOSRunDate = lastCOSRunDate;
	}


	public String getParentid() {
		return parentid;
	}


	public void setParentid(String parentid) {
		this.parentid = parentid;
	}


	public List<pgCOSDetailBean> getChildrenPD() {
		return childrenPD;
	}
	
	
	public void addChildrenPD(pgCOSDetailBean pgCOSDetailBean){
		childrenPD.add(pgCOSDetailBean);
	}
	
	//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
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
	//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
	
	public void addCountryList(String name, String restriction){
		Map country = new HashMap();
		country.put("Name", name);
		country.put("Restriction", restriction);
		countryList.add(country);
	
	}
	
	public MapList getCountryList(){
		return countryList;
	}
	
	
	
	public String toString() {
		return "(id: " + id + " , parentId: " + parentid + " , type: " + type+ " , description: " + description+ " , parentDescription: " + parentDescription + " , name: " + name + " , rev: " + rev + " , parentType: " + parentType + " , parentName: " + parentName
				+ " , parentrev: " + parentRev	+ " , countriesList " + countryList + " childrenPD: " + childrenPD + " IntermediateID: "+ IntermediateID + " IntermediateName: "+IntermediateName+")";
	}
	

}
