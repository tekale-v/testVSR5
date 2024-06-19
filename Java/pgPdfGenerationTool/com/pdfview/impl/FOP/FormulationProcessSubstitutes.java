package com.pdfview.impl.FOP;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "formulationProcessSubstitute","description","instructions" })
@XmlRootElement(name = "FormulationProcessSubstitutes", namespace = "")
public class FormulationProcessSubstitutes {

	@XmlElement(name = "FormulationProcessSubstitute", namespace = "")
	protected List<FormulationProcessSubstitute> formulationProcessSubstitute;
	@XmlElement(name = "Description", namespace = "") 
	 protected String description;
	@XmlElement(name = "Instructions", namespace = "") 
	 protected String instructions;
	
	public List<FormulationProcessSubstitute> getFormulationProcessSubstitute() {
		if (formulationProcessSubstitute == null) {
			formulationProcessSubstitute = new ArrayList<FormulationProcessSubstitute>();
		}
		return this.formulationProcessSubstitute;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public void setFormulationProcessSubstitute(List<FormulationProcessSubstitute> formulationProcessSubstitute) {
		this.formulationProcessSubstitute = formulationProcessSubstitute;
	}
	

}
