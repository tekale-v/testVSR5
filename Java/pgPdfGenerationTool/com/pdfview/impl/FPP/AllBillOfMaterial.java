package com.pdfview.impl.FPP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"billofMaterialsMain",
"billofMaterialsCustomerUnit",
"billofMaterialsInnerPack",
"billofMaterialsConsumerUnit",
"billofMaterialsTransportUnit"

})

@XmlRootElement(name = "AllBillOfMaterial", namespace = "")
public class AllBillOfMaterial {

	@XmlElement(name = "BillofMaterialsMain", namespace = "") 
	 protected BillofMaterialsMain billofMaterialsMain;
	@XmlElement(name = "BillofMaterialsCustomerUnit", namespace = "") 
	 protected BillofMaterialsCustomerUnit billofMaterialsCustomerUnit;
	@XmlElement(name = "BillofMaterialsInnerPack", namespace = "") 
	 protected BillofMaterialsInnerPack billofMaterialsInnerPack;
	@XmlElement(name = "BillofMaterialsConsumerUnit", namespace = "") 
	 protected BillofMaterialsConsumerUnit billofMaterialsConsumerUnit;
	@XmlElement(name = "BillofMaterialsTransportUnit", namespace = "") 
	 protected BillofMaterialsTransportUnit billofMaterialsTransportUnit;
	public BillofMaterialsMain getBillofMaterialsMain() {
		return billofMaterialsMain;
	}
	public void setBillofMaterialsMain(BillofMaterialsMain billofMaterialsMain) {
		this.billofMaterialsMain = billofMaterialsMain;
	}
	public BillofMaterialsCustomerUnit getBillofMaterialsCustomerUnit() {
		return billofMaterialsCustomerUnit;
	}
	public void setBillofMaterialsCustomerUnit(BillofMaterialsCustomerUnit billofMaterialsCustomerUnit) {
		this.billofMaterialsCustomerUnit = billofMaterialsCustomerUnit;
	}
	public BillofMaterialsInnerPack getBillofMaterialsInnerPack() {
		return billofMaterialsInnerPack;
	}
	public void setBillofMaterialsInnerPack(BillofMaterialsInnerPack billofMaterialsInnerPack) {
		this.billofMaterialsInnerPack = billofMaterialsInnerPack;
	}
	public BillofMaterialsConsumerUnit getBillofMaterialsConsumerUnit() {
		return billofMaterialsConsumerUnit;
	}
	public void setBillofMaterialsConsumerUnit(BillofMaterialsConsumerUnit billofMaterialsConsumerUnit) {
		this.billofMaterialsConsumerUnit = billofMaterialsConsumerUnit;
	}
	public BillofMaterialsTransportUnit getBillofMaterialsTransportUnit() {
		return billofMaterialsTransportUnit;
	}
	public void setBillofMaterialsTransportUnit(BillofMaterialsTransportUnit billofMaterialsTransportUnit) {
		this.billofMaterialsTransportUnit = billofMaterialsTransportUnit;
	}
	

}
