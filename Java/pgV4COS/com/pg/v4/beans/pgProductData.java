package com.pg.v4.beans;

import java.util.HashSet;
import java.util.Set;

public class pgProductData {

	private String id;
	private String parentid;
	private Set<String> children = new HashSet<String>();
	private Set<String> countries = new HashSet<String>();
	private Set<String> restrictedCountries = new HashSet<String>();
	private int level;
	private boolean substitute;
	private String includeInCos;
	private String strIsCircular;
   	private boolean isIPMSconsider;
	public boolean isIPMSconsider() {
		return isIPMSconsider;
	}

	public void setIPMSconsider(boolean isIPMSconsider) {
		this.isIPMSconsider = isIPMSconsider;
	}
	public String getStrIsCircular() {
		return strIsCircular;
	}

	public void setStrIsCircular(String strIsCircular) {
		this.strIsCircular = strIsCircular;
	}

	public boolean isSubstitute() {
		return substitute;
	}

	public void setSubstitute(boolean substitute) {
		this.substitute = substitute;
	}

	private String type;

	public pgProductData clone() {

		pgProductData pgPdClone = new pgProductData();

		pgPdClone.setId(this.getId());
		pgPdClone.setParentid(this.getParentid());
		pgPdClone.setLevel(this.getLevel());
		pgPdClone.setChildren(this.getChildren());
		pgPdClone.setCountries(this.getCountries());

		return pgPdClone;
	}

	public Set<String> getRestrictedCountries() {
		return restrictedCountries;
	}

	public void setRestrictedCountries(Set<String> restrictedCountries) {
		this.restrictedCountries = restrictedCountries;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public Set<String> getChildren() {
		return children;
	}

	public void setChildren(Set<String> children) {
		this.children = children;
	}

	public Set<String> getCountries() {
		return countries;
	}

	public void setCountries(Set<String> countries) {
		this.countries = countries;
	}

	public void addChildren(String child) {
		children.add(child);
	}

	public String toString() {
		return "(id: " + id + " , parentId: " + parentid + " , type: " + type + " , children: " + children
				+ " , countries: " + countries + " isSubstitute: " + substitute + ")";
	}

	public String getIncludeInCos() {
		return includeInCos;
	}

	public void setIncludeInCos(String includeInCos) {
		this.includeInCos = includeInCos;
	}
	
	

}
