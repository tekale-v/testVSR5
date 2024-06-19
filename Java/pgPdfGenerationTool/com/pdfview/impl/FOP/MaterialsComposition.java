package com.pdfview.impl.FOP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"name",
		"type",
		"title",
		"minQuantity",
		"dry",
		"maxQuantity",
		"quantityUofM",
		"qSTarget",
		"usageFlags",
		"applicationsFunctions",
		"isContaminant",
		"cASNumber",
		"state",
		"revision",
		"description"

})

public class MaterialsComposition {

	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "Title", namespace = "") 
	 protected String title;
	@XmlElement(name = "MinQuantity", namespace = "") 
	 protected String minQuantity;
	@XmlElement(name = "Dry", namespace = "") 
	 protected String dry;
	@XmlElement(name = "MaxQuantity", namespace = "") 
	 protected String maxQuantity;
	@XmlElement(name = "QuantityUofM", namespace = "") 
	 protected String quantityUofM;
	@XmlElement(name = "QSTarget", namespace = "") 
	 protected String qSTarget;
	@XmlElement(name = "UsageFlags", namespace = "") 
	 protected String usageFlags;
	@XmlElement(name = "ApplicationsFunctions", namespace = "") 
	 protected String applicationsFunctions;
	@XmlElement(name = "IsContaminant", namespace = "") 
	 protected String isContaminant;
	@XmlElement(name = "CASNumber", namespace = "") 
	 protected String cASNumber;
	@XmlElement(name = "State", namespace = "") 
	 protected String state;
	@XmlElement(name = "Revision", namespace = "") 
	 protected String revision;
	@XmlElement(name = "Description", namespace = "") 
	 protected String description;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMinQuantity() {
		return minQuantity;
	}
	public void setMinQuantity(String minQuantity) {
		this.minQuantity = minQuantity;
	}
	public String getDry() {
		return dry;
	}
	public void setDry(String dry) {
		this.dry = dry;
	}
	public String getMaxQuantity() {
		return maxQuantity;
	}
	public void setMaxQuantity(String maxQuantity) {
		this.maxQuantity = maxQuantity;
	}
	public String getQuantityUofM() {
		return quantityUofM;
	}
	public void setQuantityUofM(String quantityUofM) {
		this.quantityUofM = quantityUofM;
	}
	public String getqSTarget() {
		return qSTarget;
	}
	public void setqSTarget(String qSTarget) {
		this.qSTarget = qSTarget;
	}
	public String getUsageFlags() {
		return usageFlags;
	}
	public void setUsageFlags(String usageFlags) {
		this.usageFlags = usageFlags;
	}
	public String getApplicationsFunctions() {
		return applicationsFunctions;
	}
	public void setApplicationsFunctions(String applicationsFunctions) {
		this.applicationsFunctions = applicationsFunctions;
	}
	public String getIsContaminant() {
		return isContaminant;
	}
	public void setIsContaminant(String isContaminant) {
		this.isContaminant = isContaminant;
	}
	public String getcASNumber() {
		return cASNumber;
	}
	public void setcASNumber(String cASNumber) {
		this.cASNumber = cASNumber;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	

}
