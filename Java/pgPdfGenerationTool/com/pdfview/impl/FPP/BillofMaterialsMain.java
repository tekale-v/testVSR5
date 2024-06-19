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
@XmlRootElement(name = "BillofMaterialsMain", namespace = "")
public class BillofMaterialsMain {

@XmlElement(name = "BillofMaterialsFPPs", namespace = "")
 protected BillofMaterialsFPPs billofMaterialsFPPs;

@XmlElement(name = "Substitutes", namespace = "")
 protected Substitutes substitutes;

 public BillofMaterialsFPPs getBillofMaterialsFPPs() {
	 if (billofMaterialsFPPs == null) {
		 billofMaterialsFPPs = new BillofMaterialsFPPs();
     }
		return billofMaterialsFPPs;
	}

	public void setBillofMaterialsFPPs(BillofMaterialsFPPs billofMaterialsFPPs) {
		this.billofMaterialsFPPs = billofMaterialsFPPs;
	}

	public Substitutes getSubstitutes() {
		 if (substitutes == null) {
			 substitutes = new Substitutes();
	     }
		return substitutes;
	}

	public void setSubstitutes(Substitutes substitutes) {
		this.substitutes = substitutes;
	}


	

}
