package com.pdfview.impl.FPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"substituteParts",
"substitutePartsRev",
"substitutionCombinationNumber",
"substitutionCombinationNumberTitle",
"type",
"specificationSubType",
"qty",
"baseUnitofMeasure",
"validStartDate",
"validUntilDate",
"refDesOptional",
"components",
"comments",
"revision",
"releaseDate",
"substituteFor",
"substituteForRev",
"title",
"minimum",
"maximum",
"reportedFunction"

})

@XmlRootElement(name = "Substitute", namespace = "")
public class Substitute {
	@XmlElement(name = "SubstituteParts", namespace = "") 
	 protected String substituteParts;
	@XmlElement(name = "SubstitutePartsRev", namespace = "") 
	 protected String substitutePartsRev;
	@XmlElement(name = "SubstitutionCombinationNumber", namespace = "") 
	 protected String substitutionCombinationNumber;
	@XmlElement(name = "SubstitutionCombinationNumberTitle", namespace = "") 
	 protected String substitutionCombinationNumberTitle;
	@XmlElement(name = "Type", namespace = "") 
	 protected String type;
	@XmlElement(name = "SpecificationSubType ", namespace = "") 
	 protected String specificationSubType ;
	@XmlElement(name = "Qty", namespace = "") 
	 protected String qty;
	@XmlElement(name = "BaseUnitofMeasure", namespace = "") 
	 protected String baseUnitofMeasure;
	@XmlElement(name = "ValidStartDate", namespace = "") 
	 protected String validStartDate;
	@XmlElement(name = "ValidUntilDate", namespace = "") 
	 protected String validUntilDate;
	@XmlElement(name = "RefDesOptional", namespace = "") 
	 protected String refDesOptional;
	@XmlElement(name = "Components", namespace = "") 
	 protected String components;
	@XmlElement(name = "Comments", namespace = "") 
	 protected String comments;
	@XmlElement(name = "Revision", namespace = "") 
	 protected String revision;
	@XmlElement(name = "ReleaseDate", namespace = "") 
	 protected String releaseDate;
	@XmlElement(name = "SubstituteFor", namespace = "") 
	 protected String substituteFor;
	@XmlElement(name = "SubstituteForRev", namespace = "") 
	 protected String substituteForRev;
	@XmlElement(name = "Title", namespace = "") 
	 protected String title;
	@XmlElement(name = "Minimum", namespace = "") 
	 protected String minimum;
	@XmlElement(name = "Maximum", namespace = "") 
	 protected String maximum;
	@XmlElement(name = "ReportedFunction", namespace = "") 
	 protected String reportedFunction;
	
	public String getSubstituteParts() {
		return substituteParts;
	}
	public void setSubstituteParts(String substituteParts) {
		this.substituteParts = substituteParts;
	}
	public String getSubstitutePartsRev() {
		return substitutePartsRev;
	}
	public void setSubstitutePartsRev(String substitutePartsRev) {
		this.substitutePartsRev = substitutePartsRev;
	}
	public String getSubstitutionCombinationNumber() {
		return substitutionCombinationNumber;
	}
	public void setSubstitutionCombinationNumber(String substitutionCombinationNumber) {
		this.substitutionCombinationNumber = substitutionCombinationNumber;
	}
	public String getSubstitutionCombinationNumberTitle() {
		return substitutionCombinationNumberTitle;
	}
	public void setSubstitutionCombinationNumberTitle(String substitutionCombinationNumberTitle) {
		this.substitutionCombinationNumberTitle = substitutionCombinationNumberTitle;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSpecificationSubType() {
		return specificationSubType;
	}
	public void setSpecificationSubType(String specificationSubType) {
		this.specificationSubType = specificationSubType;
	}
	public String getQty() {
		return qty;
	}
	public void setQty(String qty) {
		this.qty = qty;
	}
	public String getBaseUnitofMeasure() {
		return baseUnitofMeasure;
	}
	public void setBaseUnitofMeasure(String baseUnitofMeasure) {
		this.baseUnitofMeasure = baseUnitofMeasure;
	}
	public String getValidStartDate() {
		return validStartDate;
	}
	public void setValidStartDate(String validStartDate) {
		this.validStartDate = validStartDate;
	}
	public String getValidUntilDate() {
		return validUntilDate;
	}
	public void setValidUntilDate(String validUntilDate) {
		this.validUntilDate = validUntilDate;
	}
	public String getRefDesOptional() {
		return refDesOptional;
	}
	public void setRefDesOptional(String refDesOptional) {
		this.refDesOptional = refDesOptional;
	}
	public String getComponents() {
		return components;
	}
	public void setComponents(String components) {
		this.components = components;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getSubstituteFor() {
		return substituteFor;
	}
	public void setSubstituteFor(String substituteFor) {
		this.substituteFor = substituteFor;
	}
	public String getSubstituteForRev() {
		return substituteForRev;
	}
	public void setSubstituteForRev(String substituteForRev) {
		this.substituteForRev = substituteForRev;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMinimum() {
		return minimum;
	}
	public void setMinimum(String minimum) {
		this.minimum = minimum;
	}
	public String getMaximum() {
		return maximum;
	}
	public void setMaximum(String maximum) {
		this.maximum = maximum;
	}
	public String getReportedFunction() {
		return reportedFunction;
	}
	public void setReportedFunction(String reportedFunction) {
		this.reportedFunction = reportedFunction;
	}

}
