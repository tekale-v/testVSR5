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
		"formulationProcess"
})
@XmlRootElement(name = "FormulationProcesses", namespace = "")
public class FormulationProcesses {

	@XmlElement(name = "FormulationProcess", namespace = "")
	protected List<FormulationProcess> formulationProcess;

	public List<FormulationProcess> getFormulationProcess() {
		if (formulationProcess == null) {
			formulationProcess = new ArrayList<FormulationProcess>();
		}
		return this.formulationProcess;
	}
}
