package com.pg.v4.util.mos;

import java.util.Map;

import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;

import matrix.util.StringList;

public class ProductHelper {
	private StringList id;
	private StringList type;
	private StringList policy;
	private StringList name;
	private StringList revision;
	private StringList isFCExist;
	private StringList isArtExist;
	private StringList includeInCOS;
	private StringList state;
	private StringList relID;
	private StringList title;
	private StringList sapType;
	private StringList subAssemblyType;
	private StringList isNonReleasedArtExist;
	/**
	 * @return the id
	 */
	public StringList getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	private void setId(StringList id) {
		this.id = id;
	}


	/**
	 * @return the type
	 */
	public StringList getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	private void setType(StringList type) {
		this.type = type;
	}


	/**
	 * @return the policy
	 */
	public StringList getPolicy() {
		return policy;
	}


	/**
	 * @param policy the policy to set
	 */
	private void setPolicy(StringList policy) {
		this.policy = policy;
	}


	/**
	 * @return the name
	 */
	public StringList getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	private void setName(StringList name) {
		this.name = name;
	}


	/**
	 * @return the revision
	 */
	public StringList getRevision() {
		return revision;
	}


	/**
	 * @param revision the revision to set
	 */
	private void setRevision(StringList revision) {
		this.revision = revision;
	}


	/**
	 * @return the isFCExist
	 */
	public StringList getIsFCExist() {
		return isFCExist;
	}


	/**
	 * @param isFCExist the isFCExist to set
	 */
	private void setIsFCExist(StringList isFCExist) {
		this.isFCExist = isFCExist;
	}


	/**
	 * @return the isArtExist
	 */
	public StringList getIsArtExist() {
		return isArtExist;
	}


	/**
	 * @param isArtExist the isArtExist to set
	 */
	private void setIsArtExist(StringList isArtExist) {
		this.isArtExist = isArtExist;
	}


	/**
	 * @return the isNonReleasedArtExist
	 */
	public StringList getIsNonReleasedArtExist() {
		return isNonReleasedArtExist;
	}


	/**
	 * @param isNonReleasedArtExist the isNonReleasedArtExist to set
	 */
	public void setIsNonReleasedArtExist(StringList isNonReleasedArtExist) {
		this.isNonReleasedArtExist = isNonReleasedArtExist;
	}


	/**
	 * @return the includeInCOS
	 */
	public StringList getIncludeInCOS() {
		return includeInCOS;
	}


	/**
	 * @param includeInCOS the includeInCOS to set
	 */
	private void setIncludeInCOS(StringList includeInCOS) {
		this.includeInCOS = includeInCOS;
	}


	/**
	 * @return the state
	 */
	public StringList getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	private void setState(StringList state) {
		this.state = state;
	}


	/**
	 * @return the relID
	 */
	public StringList getRelID() {
		return relID;
	}


	/**
	 * @param relID the relID to set
	 */
	private void setRelID(StringList relID) {
		this.relID = relID;
	}


	/**
	 * @return the title
	 */
	public StringList getTitle() {
		return title;
	}


	/**
	 * @param title the title to set
	 */
	private void setTitle(StringList title) {
		this.title = title;
	}


	/**
	 * @return the sapType
	 */
	public StringList getSapType() {
		return sapType;
	}


	/**
	 * @param sapType the sapType to set
	 */
	private void setSapType(StringList sapType) {
		this.sapType = sapType;
	}
	
	
	/**
	 * @return the subAssemblyType
	 */
	public StringList getSubAssemblyType() {
		return subAssemblyType;
	}


	/**
	 * @param subAssemblyType the subAssemblyType to set
	 */
	private void setSubAssemblyType(StringList subAssemblyType) {
		this.subAssemblyType = subAssemblyType;
	}


	/**
	 * @param mpFPPChildData
	 * @param key
	 * @return
	 */
	public static StringList getPartDetails(Map mpFPPChildData, String key) {
		Object objChild = mpFPPChildData.get(key);
		StringList lsChildData = new StringList();
		if (null != objChild) {
			if (objChild instanceof StringList) {
				lsChildData = (StringList) objChild;
			} else {
				lsChildData.add(objChild.toString());
			}
		}
		return lsChildData;
	}

	/**
	 * @param mlConnectedData
	 * @param iCount
	 */
	public void loadExpandInfo(MapList mlConnectedData,int iCount) {
		Map objectMap=(Map)mlConnectedData.get(iCount);	
		extractSubstitutes(objectMap);
	}
	
	/**
	 * @param objectMap
	 */
	public void extractSubstitutes(Map objectMap) {
		this.setId(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.id"));
		if(this.getId()!=null && !this.getId().isEmpty()) {
			this.setType(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.type"));
			this.setPolicy(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.policy"));
			this.setName(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.name"));
			this.setRevision(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.revision"));
			this.setIsFCExist(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST));
			this.setIsArtExist(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST));
			this.setIsNonReleasedArtExist(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST));
			this.setIncludeInCOS(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS));
			this.setSubAssemblyType(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]"));
			this.setState(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.current"));
			this.setRelID(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].id"));
			this.setTitle(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_TITLE));
			this.setSapType(getPartDetails(objectMap, "frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE));
		}
	}
}
