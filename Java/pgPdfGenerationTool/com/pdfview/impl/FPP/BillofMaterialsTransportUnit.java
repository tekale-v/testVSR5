package com.pdfview.impl.FPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
 "billofMaterialsFPPs","substitutes"
})
@XmlRootElement(name = "BillofMaterialsTransportUnit", namespace = "")
public class BillofMaterialsTransportUnit {

 public BillofMaterialsFPPs getBillofMaterialsFPPs() {
		return billofMaterialsFPPs;
	}

	public void setBillofMaterialsFPPs(BillofMaterialsFPPs billofMaterialsFPPs) {
		this.billofMaterialsFPPs = billofMaterialsFPPs;
	}

@XmlElement(name = "BillofMaterialsFPPs", namespace = "")
 protected BillofMaterialsFPPs billofMaterialsFPPs;
@XmlElement(name = "Substitutes", namespace = "")
protected Substitutes substitutes;

public Substitutes getSubstitutes() {
	return substitutes;
}

public void setSubstitutes(Substitutes substitutes) {
	this.substitutes = substitutes;
}

}
