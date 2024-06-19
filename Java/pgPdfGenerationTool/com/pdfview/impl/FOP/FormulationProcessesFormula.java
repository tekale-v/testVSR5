package com.pdfview.impl.FOP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"name",
	"type",
	"seqNumber",
	"title",
	"minPercentage",
	"wetPercentage",
	"maxPercentage",
	"targetWetWeight",
	"dryPercentage",
	"targetDryWeight",
	"processingLoss",
	"materialFunction",
	"virtualIntermediateName",
	"processingNote"

})


public class FormulationProcessesFormula {

	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "SeqNumber", namespace = "") 
	 protected String seqNumber;
	@XmlElement(name = "Title", namespace = "") 
	 protected String title;
	@XmlElement(name = "MinPercentage", namespace = "") 
	 protected String minPercentage;
	@XmlElement(name = "WetPercentage", namespace = "") 
	 protected String wetPercentage;
	@XmlElement(name = "MaxPercentage", namespace = "") 
	 protected String maxPercentage;
	@XmlElement(name = "TargetWetWeight", namespace = "") 
	 protected String targetWetWeight;
	@XmlElement(name = "DryPercentage", namespace = "") 
	 protected String dryPercentage;
	@XmlElement(name = "TargetDryWeight", namespace = "") 
	 protected String targetDryWeight;
	@XmlElement(name = "ProcessingLoss", namespace = "") 
	 protected String processingLoss;
	@XmlElement(name = "MaterialFunction", namespace = "") 
	 protected String materialFunction;
	@XmlElement(name = "VirtualIntermediateName", namespace = "") 
	 protected String virtualIntermediateName;
	@XmlElement(name = "ProcessingNote", namespace = "") 
	 protected String processingNote;
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
	public String getSeqNumber() {
		return seqNumber;
	}
	public void setSeqNumber(String seqNumber) {
		this.seqNumber = seqNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMinPercentage() {
		return minPercentage;
	}
	public void setMinPercentage(String minPercentage) {
		this.minPercentage = minPercentage;
	}
	public String getWetPercentage() {
		return wetPercentage;
	}
	public void setWetPercentage(String wetPercentage) {
		this.wetPercentage = wetPercentage;
	}
	public String getMaxPercentage() {
		return maxPercentage;
	}
	public void setMaxPercentage(String maxPercentage) {
		this.maxPercentage = maxPercentage;
	}
	public String getTargetWetWeight() {
		return targetWetWeight;
	}
	public void setTargetWetWeight(String targetWetWeight) {
		this.targetWetWeight = targetWetWeight;
	}
	public String getDryPercentage() {
		return dryPercentage;
	}
	public void setDryPercentage(String dryPercentage) {
		this.dryPercentage = dryPercentage;
	}
	public String getTargetDryWeight() {
		return targetDryWeight;
	}
	public void setTargetDryWeight(String targetDryWeight) {
		this.targetDryWeight = targetDryWeight;
	}
	public String getProcessingLoss() {
		return processingLoss;
	}
	public void setProcessingLoss(String processingLoss) {
		this.processingLoss = processingLoss;
	}
	public String getMaterialFunction() {
		return materialFunction;
	}
	public void setMaterialFunction(String materialFunction) {
		this.materialFunction = materialFunction;
	}
	public String getVirtualIntermediateName() {
		return virtualIntermediateName;
	}
	public void setVirtualIntermediateName(String virtualIntermediateName) {
		this.virtualIntermediateName = virtualIntermediateName;
	}
	public String getProcessingNote() {
		return processingNote;
	}
	public void setProcessingNote(String processingNote) {
		this.processingNote = processingNote;
	}
	
	
	
}
