package com.pdfview.impl.FOP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"name",
	"title",
	"type",
	"minPercent",
	"wetPercent",
	"maxPercent",
	"targetWetWeight",
	"wetWeightMin",
	"wetWeightMax",
	"wetDiffPercent",
	"validUntilDate",
	"certifications",
	"processingNote",
	"sustituteForName",
	"sustituteForTitle",
	"sustituteForWetPercent"

})
public class FormulationProcessSubstitute {
	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "Title", namespace = "") 
	 protected String title;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "MinPercent", namespace = "") 
	 protected String minPercent;
	@XmlElement(name = "WetPercent", namespace = "") 
	 protected String wetPercent;
	@XmlElement(name = "MaxPercent", namespace = "") 
	 protected String maxPercent;
	@XmlElement(name = "TargetWetWeight", namespace = "") 
	 protected String targetWetWeight;
	@XmlElement(name = "WetWeightMin", namespace = "") 
	 protected String wetWeightMin;
	@XmlElement(name = "WetWeightMax", namespace = "") 
	 protected String wetWeightMax;
	@XmlElement(name = "WetDiffPercent", namespace = "") 
	 protected String wetDiffPercent;
	@XmlElement(name = "ValidUntilDate", namespace = "") 
	 protected String validUntilDate;
	@XmlElement(name = "Certifications", namespace = "") 
	 protected String certifications;
	@XmlElement(name = "ProcessingNote", namespace = "") 
	 protected String processingNote;
	@XmlElement(name = "SustituteForName", namespace = "") 
	 protected String sustituteForName;
	@XmlElement(name = "SustituteForTitle", namespace = "") 
	 protected String sustituteForTitle;
	@XmlElement(name = "SustituteForWetPercent", namespace = "") 
	 protected String sustituteForWetPercent;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMinPercent() {
		return minPercent;
	}
	public void setMinPercent(String minPercent) {
		this.minPercent = minPercent;
	}
	public String getWetPercent() {
		return wetPercent;
	}
	public void setWetPercent(String wetPercent) {
		this.wetPercent = wetPercent;
	}
	public String getMaxPercent() {
		return maxPercent;
	}
	public void setMaxPercent(String maxPercent) {
		this.maxPercent = maxPercent;
	}
	public String getTargetWetWeight() {
		return targetWetWeight;
	}
	public void setTargetWetWeight(String targetWetWeight) {
		this.targetWetWeight = targetWetWeight;
	}
	public String getWetWeightMin() {
		return wetWeightMin;
	}
	public void setWetWeightMin(String wetWeightMin) {
		this.wetWeightMin = wetWeightMin;
	}
	public String getWetWeightMax() {
		return wetWeightMax;
	}
	public void setWetWeightMax(String wetWeightMax) {
		this.wetWeightMax = wetWeightMax;
	}
	public String getWetDiffPercent() {
		return wetDiffPercent;
	}
	public void setWetDiffPercent(String wetDiffPercent) {
		this.wetDiffPercent = wetDiffPercent;
	}
	public String getValidUntilDate() {
		return validUntilDate;
	}
	public void setValidUntilDate(String validUntilDate) {
		this.validUntilDate = validUntilDate;
	}
	public String getCertifications() {
		return certifications;
	}
	public void setCertifications(String certifications) {
		this.certifications = certifications;
	}
	public String getProcessingNote() {
		return processingNote;
	}
	public void setProcessingNote(String processingNote) {
		this.processingNote = processingNote;
	}
	public String getSustituteForName() {
		return sustituteForName;
	}
	public void setSustituteForName(String sustituteForName) {
		this.sustituteForName = sustituteForName;
	}
	public String getSustituteForTitle() {
		return sustituteForTitle;
	}
	public void setSustituteForTitle(String sustituteForTitle) {
		this.sustituteForTitle = sustituteForTitle;
	}
	public String getSustituteForWetPercent() {
		return sustituteForWetPercent;
	}
	public void setSustituteForWetPercent(String sustituteForWetPercent) {
		this.sustituteForWetPercent = sustituteForWetPercent;
	}
	
	
}
