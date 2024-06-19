package com.pdfview.impl.FOP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"propellantemulsifiedforlifeofproduct",
"propellantisnonflammable",
"wTParameterized",
"kstDustDeflagrationIndex",
"pmaxmaxexplosionpressure"

})


public class ChemicalandPhysicalProertiesEnginuity {

	@XmlElement(name = "Propellantemulsifiedforlifeofproduct", namespace = "") 
	 protected String propellantemulsifiedforlifeofproduct;
	@XmlElement(name = "Propellantisnonflammable", namespace = "") 
	 protected String propellantisnonflammable;
	@XmlElement(name = "WTParameterized", namespace = "") 
	 protected String wTParameterized;
	@XmlElement(name = "KstDustDeflagrationIndex", namespace = "") 
	 protected String kstDustDeflagrationIndex;
	@XmlElement(name = "pmaxmaxexplosionpressure", namespace = "") 
	 protected String pmaxmaxexplosionpressure;
	public String getPropellantemulsifiedforlifeofproduct() {
		return propellantemulsifiedforlifeofproduct;
	}
	public void setPropellantemulsifiedforlifeofproduct(
			String propellantemulsifiedforlifeofproduct) {
		this.propellantemulsifiedforlifeofproduct = propellantemulsifiedforlifeofproduct;
	}
	public String getPropellantisnonflammable() {
		return propellantisnonflammable;
	}
	public void setPropellantisnonflammable(String propellantisnonflammable) {
		this.propellantisnonflammable = propellantisnonflammable;
	}
	public String getwTParameterized() {
		return wTParameterized;
	}
	public void setwTParameterized(String wTParameterized) {
		this.wTParameterized = wTParameterized;
	}
	public String getKstDustDeflagrationIndex() {
		return kstDustDeflagrationIndex;
	}
	public void setKstDustDeflagrationIndex(String kstDustDeflagrationIndex) {
		this.kstDustDeflagrationIndex = kstDustDeflagrationIndex;
	}
	public String getPmaxmaxexplosionpressure() {
		return pmaxmaxexplosionpressure;
	}
	public void setPmaxmaxexplosionpressure(String pmaxmaxexplosionpressure) {
		this.pmaxmaxexplosionpressure = pmaxmaxexplosionpressure;
	}

}

