package com.pg.designtools.integrations.tops;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import matrix.db.AttributeList;

@XStreamAlias("priPack")
public class PriPack {
	@XStreamAlias("selected")
	private Boolean selected;
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("type")
	private String type;
	@XStreamAlias("units")
	private String units;
	@XStreamAlias("filmName")
	private String filmName;	
	@XStreamAlias("dims")
	private Dims dims;
	@XStreamAlias("lenUnits")
	private String lenUnits;	
	@XStreamAlias("wgtUnits")
	private String wgtUnits;
	@XStreamAlias("info")
	private PriPackInfo info;	
	@XStreamAlias("EnoviaMap")
	private String enoviaMap;
	@XStreamAlias("bundle")
	private Bundle bundle;
	
	public PriPack() {
		super();
	}
	
	public PriPack(Boolean selected, String name, String type, String filmName, String units) {
		super();
		this.selected = selected;
		this.name = name;
		this.type = type;
		this.filmName = filmName;	
		this.units = units;

	
	}
	public PriPack(Boolean selected, String name, String type, String filmName, String units, String lenUnits, String wgtUnits) {
		super();
		this.selected = selected;
		this.name = name;
		this.type = type;
		this.filmName = filmName;	
		this.units = units;
		this.lenUnits = lenUnits;
		this.wgtUnits = wgtUnits;

	
	}
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public String getFilmName() {
		return filmName;
	}
	public void setFilmName(String filmName) {
		this.filmName = filmName;
	}	
	public Dims getDims() {
		return dims;
	}
	public void setDims(Dims dims) {
		this.dims = dims;
	}
	public PriPackInfo getInfo() {
		return info;
	}
	public void setInfo(PriPackInfo info) {
		this.info = info;
	}
	public String getLenUnits() {
		return lenUnits;
	}
	public void setLenUnits(String lenUnits) {
		this.lenUnits = lenUnits;
	}
	public String getWgtUnits() {
		return wgtUnits;
	}
	public void setWgtUnits(String wgtUnits) {
		this.wgtUnits = wgtUnits;
	}
	
	public String getEnoviaMap() {
		if("NONE".equalsIgnoreCase(enoviaMap) && bundle!=null) {
			enoviaMap = bundle.getEnoviaMap();
		}
		return enoviaMap;
	}
	public void setEnoviaMap(String enoviaMap) {
		this.enoviaMap = enoviaMap;
	}	
	public Bundle getBundle() {
		return bundle;
	}
	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [" + selected + ", "+ name + ", " + type + ", " + filmName + ", " + units +  ", " + dims + ", " + info  + ","+ lenUnits + "," + wgtUnits  + "]";
	}
	
	
	//IMappableComponent implementation
	/**
	 * @return an AttributeList of mapping attributes defined 
	 * in processMappingAttributes and called by mapAttributes(node)
	 */
	public AttributeList getMappingAttributes() {
		
		return new AttributeList();
		
	}
	
	

}
