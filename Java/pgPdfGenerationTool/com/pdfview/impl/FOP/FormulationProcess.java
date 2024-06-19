package com.pdfview.impl.FOP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"formulaNumber",
		"revision",
		"formulaNameProcessName",
		"formulationType",
		"phase",
		"state",
		"formulaType",
		"formulaName",
		"name",
		"type",
		"title"

})

public class FormulationProcess {

	@XmlElement(name = "FormulaNumber", namespace = "") 
	protected String formulaNumber;
	@XmlElement(name = "Revision", namespace = "") 
	protected String revision;
	@XmlElement(name = "FormulaNameProcessName", namespace = "") 
	protected String formulaNameProcessName;
	@XmlElement(name = "FormulationType", namespace = "") 
	protected String formulationType;
	@XmlElement(name = "Phase", namespace = "") 
	protected String phase;
	@XmlElement(name = "State", namespace = "") 
	protected String state;
	@XmlElement(name = "FormulaType", namespace = "") 
	protected String formulaType;
	@XmlElement(name = "FormulaName", namespace = "") 
	protected String formulaName;
	@XmlElement(name = "Name", namespace = "") 
	protected String name;
	@XmlElement(name = "Type", namespace = "") 
	protected String type;
	@XmlElement(name = "Title", namespace = "") 
	protected String title;
	public String getFormulaNumber() {
		return formulaNumber;
	}
	public void setFormulaNumber(String formulaNumber) {
		this.formulaNumber = formulaNumber;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getFormulaNameProcessName() {
		return formulaNameProcessName;
	}
	public void setFormulaNameProcessName(String formulaNameProcessName) {
		this.formulaNameProcessName = formulaNameProcessName;
	}
	public String getFormulationType() {
		return formulationType;
	}
	public void setFormulationType(String formulationType) {
		this.formulationType = formulationType;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getFormulaType() {
		return formulaType;
	}
	public void setFormulaType(String formulaType) {
		this.formulaType = formulaType;
	}
	public String getFormulaName() {
		return formulaName;
	}
	public void setFormulaName(String formulaName) {
		this.formulaName = formulaName;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

}
