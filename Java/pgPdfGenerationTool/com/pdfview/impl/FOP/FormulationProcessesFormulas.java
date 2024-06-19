package com.pdfview.impl.FOP;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"formulationProcessesFormula"
})
@XmlRootElement(name = "FormulationProcessesFormulas", namespace = "")
public class FormulationProcessesFormulas {

	@XmlElement(name = "FormulationProcessesFormula", namespace = "")
	protected List<FormulationProcessesFormula> formulationProcessesFormula;

	
	public List<FormulationProcessesFormula> getFormulationProcessesFormula() {
		if (formulationProcessesFormula == null) {
			formulationProcessesFormula = new ArrayList<FormulationProcessesFormula>();
		}
		return this.formulationProcessesFormula;
	}

}
