//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.04.10 at 10:09:18 AM EDT 
//

package com.pdfview.impl.FOP;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "formulationProcesses", "formulationProcessesFormulas" })
@XmlRootElement(name = "FormulationProcessData", namespace = "")
public class FormulationProcessData {

	@XmlElement(name = "FormulationProcesses", namespace = "")
	protected FormulationProcesses formulationProcesses;
	@XmlElement(name = "FormulationProcessesFormulas", namespace = "")
	protected FormulationProcessesFormulas formulationProcessesFormulas;
	public FormulationProcesses getFormulationProcesses() {
		if (formulationProcesses == null) {
			formulationProcesses = new FormulationProcesses();
		}
		return this.formulationProcesses;
	}
	public FormulationProcessesFormulas getFormulationProcessesFormulas() {
		if (formulationProcessesFormulas == null) {
			formulationProcessesFormulas = new FormulationProcessesFormulas();
		}
		return this.formulationProcessesFormulas;
	}
	
}