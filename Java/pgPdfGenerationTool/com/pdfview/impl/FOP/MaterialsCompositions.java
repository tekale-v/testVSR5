package com.pdfview.impl.FOP;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.pdfview.impl.FOP.MaterialsComposition;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"materialsComposition"
})
@XmlRootElement(name = "MaterialsCompositions", namespace = "")
public class MaterialsCompositions {


	@XmlElement(name = "MaterialsComposition", namespace = "")
	protected List<MaterialsComposition> materialsComposition;

	
	public List<MaterialsComposition> getMaterialandComposition() {
		if (materialsComposition == null) {
			materialsComposition = new ArrayList<MaterialsComposition>();
		}
		return this.materialsComposition;
	}


}
