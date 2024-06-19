package com.pdfview.impl.PMP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)

@XmlType(name = "", propOrder = {"name",
		"targetPercentageWeightbyWeight",
		"manufacturer",
		"type",
		"seq",
		"description",
		"title",
		"tradeName",
		"legacyEnvironmentalClass",
		"layerComponentDescription",
		"minimumPercentageWeightbyWeight",
		"maximumPercentageWeightbyWeight",
		"postConsumerRecycledContent",
		"postIndustrialRecycledContent",
		"comments",
		"state",
		"quantityUnitOfMeasure"

})

public class SubstancesMaterial {

	@XmlElement(name = "Name", namespace = "") 
	 protected String name;
	@XmlElement(name = "TargetPercentageWeightbyWeight", namespace = "") 
	 protected String targetPercentageWeightbyWeight;
	@XmlElement(name = "Manufacturer", namespace = "") 
	 protected String manufacturer;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "Seq", namespace = "") 
	 protected String seq;
	@XmlElement(name = "Description", namespace = "") 
	 protected String description;
	@XmlElement(name = "Title", namespace = "") 
	 protected String title;
	@XmlElement(name = "TradeName", namespace = "") 
	 protected String tradeName;
	@XmlElement(name = "LegacyEnvironmentalClass", namespace = "") 
	 protected String legacyEnvironmentalClass;
	@XmlElement(name = "LayerComponentDescription", namespace = "") 
	 protected String layerComponentDescription;
	@XmlElement(name = "MinimumPercentageWeightbyWeight", namespace = "") 
	 protected String minimumPercentageWeightbyWeight;
	@XmlElement(name = "MaximumPercentageWeightbyWeight", namespace = "") 
	 protected String maximumPercentageWeightbyWeight;
	@XmlElement(name = "PostConsumerRecycledContent", namespace = "") 
	 protected String postConsumerRecycledContent;
	@XmlElement(name = "PostIndustrialRecycledContent", namespace = "") 
	 protected String postIndustrialRecycledContent;
	@XmlElement(name = "Comments", namespace = "") 
	 protected String comments;
	@XmlElement(name = "State", namespace = "") 
	 protected String state;
	@XmlElement(name = "QuantityUnitOfMeasure", namespace = "") 
	 protected String quantityUnitOfMeasure;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTargetPercentageWeightbyWeight() {
		return targetPercentageWeightbyWeight;
	}
	public void setTargetPercentageWeightbyWeight(
			String targetPercentageWeightbyWeight) {
		this.targetPercentageWeightbyWeight = targetPercentageWeightbyWeight;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTradeName() {
		return tradeName;
	}
	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}
	public String getLegacyEnvironmentalClass() {
		return legacyEnvironmentalClass;
	}
	public void setLegacyEnvironmentalClass(String legacyEnvironmentalClass) {
		this.legacyEnvironmentalClass = legacyEnvironmentalClass;
	}
	public String getLayerComponentDescription() {
		return layerComponentDescription;
	}
	public void setLayerComponentDescription(String layerComponentDescription) {
		this.layerComponentDescription = layerComponentDescription;
	}
	public String getMinimumPercentageWeightbyWeight() {
		return minimumPercentageWeightbyWeight;
	}
	public void setMinimumPercentageWeightbyWeight(
			String minimumPercentageWeightbyWeight) {
		this.minimumPercentageWeightbyWeight = minimumPercentageWeightbyWeight;
	}
	public String getMaximumPercentageWeightbyWeight() {
		return maximumPercentageWeightbyWeight;
	}
	public void setMaximumPercentageWeightbyWeight(
			String maximumPercentageWeightbyWeight) {
		this.maximumPercentageWeightbyWeight = maximumPercentageWeightbyWeight;
	}
	public String getPostConsumerRecycledContent() {
		return postConsumerRecycledContent;
	}
	public void setPostConsumerRecycledContent(String postConsumerRecycledContent) {
		this.postConsumerRecycledContent = postConsumerRecycledContent;
	}
	public String getPostIndustrialRecycledContent() {
		return postIndustrialRecycledContent;
	}
	public void setPostIndustrialRecycledContent(
			String postIndustrialRecycledContent) {
		this.postIndustrialRecycledContent = postIndustrialRecycledContent;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getQuantityUnitOfMeasure() {
		return quantityUnitOfMeasure;
	}
	public void setQuantityUnitOfMeasure(String quantityUnitOfMeasure) {
		this.quantityUnitOfMeasure = quantityUnitOfMeasure;
	}

	
}
