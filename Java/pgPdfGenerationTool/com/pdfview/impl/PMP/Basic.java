//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:09 AM EDT 
//


package com.pdfview.impl.PMP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pdfview.impl.APP.MasterAttributes;
import com.pdfview.impl.APP.MaterialsProduced;
import com.pdfview.impl.APP.WeightCharacteristic;
import com.pdfview.impl.DPP.ManufacturerEquivalents;
import com.pdfview.impl.DPP.SupplierEquivalents;
import com.pdfview.impl.FPP.DerivedParts;
import com.pdfview.impl.FPP.LifeCycle;
import com.pdfview.impl.FPP.MasterSpecifications;
import com.pdfview.impl.FPP.Organizations;
import com.pdfview.impl.FPP.Ownership;
import com.pdfview.impl.FPP.PerformanceCharacteristics;
import com.pdfview.impl.FPP.Plants;
import com.pdfview.impl.FPP.ReferenceDocuments;
import com.pdfview.impl.FPP.RelatedATS;
import com.pdfview.impl.FPP.RelatedSpecifications;
import com.pdfview.impl.FPP.SecurityClasses;
import com.pdfview.impl.common.GenericModel;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attributes",
    "organizations",
    "plants",
    "referenceDocuments",
    "lifeCycle",
    "ownership",
    "relatedATS",
    "securityClasses",
    "derivedParts",
    "relatedSpecifications",
    "masterAttributes",
    "performanceCharacteristics",
    "masterSpecifications",
    "supplierEquivalents",
    "manufacturerEquivalents",
    "substancesMaterials",
    "weightCharacteristic",
    "genericModel"
})
@XmlRootElement(name = "Basic", namespace = "")
public class Basic {

    @XmlElement(name = "Attributes", namespace = "")
    protected Attributes attributes;
  
    @XmlElement(name = "Organizations", namespace = "")
    protected Organizations organizations;
	@XmlElement(name = "Plants", namespace = "")
	protected Plants plants;
	@XmlElement(name = "ReferenceDocuments", namespace = "")
	protected ReferenceDocuments  referenceDocuments;
	@XmlElement(name = "Ownership", namespace = "")
	protected Ownership ownership;
	@XmlElement(name = "LifeCycle", namespace = "")
	protected LifeCycle lifeCycle;
	@XmlElement(name = "SecurityClasses", namespace = "")
	protected SecurityClasses securityClasses;
	@XmlElement(name = "RelatedATS", namespace = "")
	protected RelatedATS relatedATS;
	@XmlElement(name = "DerivedParts", namespace = "")
	protected DerivedParts derivedParts;
	@XmlElement(name = "RelatedSpecifications", namespace = "")
	protected RelatedSpecifications relatedSpecifications;
	@XmlElement(name = "MasterAttributes", namespace = "")
	protected MasterAttributes masterAttributes;
	@XmlElement(name="PerformanceCharacteristics", namespace="")
	protected PerformanceCharacteristics performanceCharacteristics;
	@XmlElement(name="MasterSpecifications", namespace="")
	protected MasterSpecifications masterSpecifications;
	@XmlElement(name="SupplierEquivalents", namespace="")
	protected SupplierEquivalents supplierEquivalents;
	@XmlElement(name="ManufacturerEquivalents", namespace="")
	protected ManufacturerEquivalents manufacturerEquivalents;
	@XmlElement(name="SubstancesMaterials", namespace="")
	protected SubstancesMaterials substancesMaterials;
	@XmlElement(name="WeightCharacteristic", namespace="")
	protected WeightCharacteristic weightCharacteristic;
	@XmlElement(name = "GenericModel", namespace = "")
	protected GenericModel genericModel;
	
	
	public Attributes getAttributes() {
		return attributes;
	}
	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
	public Organizations getOrganizations() {
		return organizations;
	}
	public void setOrganizations(Organizations organizations) {
		this.organizations = organizations;
	}
	public Plants getPlants() {
		return plants;
	}
	public void setPlants(Plants plants) {
		this.plants = plants;
	}
	public ReferenceDocuments getReferenceDocuments() {
		return referenceDocuments;
	}
	public void setReferenceDocuments(ReferenceDocuments referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}
	public Ownership getOwnership() {
		return ownership;
	}
	public void setOwnership(Ownership ownership) {
		this.ownership = ownership;
	}
	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}
	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}
	public SecurityClasses getSecurityClasses() {
		return securityClasses;
	}
	public void setSecurityClasses(SecurityClasses securityClasses) {
		this.securityClasses = securityClasses;
	}
	public RelatedATS getRelatedATS() {
		return relatedATS;
	}
	public void setRelatedATS(RelatedATS relatedATS) {
		this.relatedATS = relatedATS;
	}
	public DerivedParts getDerivedParts() {
		return derivedParts;
	}
	public void setDerivedParts(DerivedParts derivedParts) {
		this.derivedParts = derivedParts;
	}
	public RelatedSpecifications getRelatedSpecifications() {
		return relatedSpecifications;
	}
	public void setRelatedSpecifications(RelatedSpecifications relatedSpecifications) {
		this.relatedSpecifications = relatedSpecifications;
	}
	public MasterAttributes getMasterAttributes() {
		return masterAttributes;
	}
	public void setMasterAttributes(MasterAttributes masterAttributes) {
		this.masterAttributes = masterAttributes;
	}
	public PerformanceCharacteristics getPerformanceCharacteristics() {
		return performanceCharacteristics;
	}
	public void setPerformanceCharacteristics(PerformanceCharacteristics performanceCharacteristics) {
		this.performanceCharacteristics = performanceCharacteristics;
	}
	public MasterSpecifications getMasterSpecifications() {
		return masterSpecifications;
	}
	public void setMasterSpecifications(MasterSpecifications masterSpecifications) {
		this.masterSpecifications = masterSpecifications;
	}
	public SupplierEquivalents getSupplierEquivalents() {
		return supplierEquivalents;
	}
	public void setSupplierEquivalents(SupplierEquivalents supplierEquivalents) {
		this.supplierEquivalents = supplierEquivalents;
	}
	public ManufacturerEquivalents getManufacturerEquivalents() {
		return manufacturerEquivalents;
	}
	public void setManufacturerEquivalents(ManufacturerEquivalents manufacturerEquivalents) {
		this.manufacturerEquivalents = manufacturerEquivalents;
	}
	public SubstancesMaterials getSubstancesMaterials() {
		return substancesMaterials;
	}
	public void setSubstancesMaterials(SubstancesMaterials substancesMaterials) {
		this.substancesMaterials = substancesMaterials;
	}
	public WeightCharacteristic getWeightCharacteristic() {
		return weightCharacteristic;
	}
	public void setWeightCharacteristic(WeightCharacteristic weightCharacteristic) {
		this.weightCharacteristic = weightCharacteristic;
	}
	public GenericModel getGenericModel() {
		return genericModel;
	}
	public void setGenericModel(GenericModel genericModel) {
		this.genericModel = genericModel;
	}
    
	
	
}
