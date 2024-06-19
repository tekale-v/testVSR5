package com.pdfview.impl.PMP;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"substancesMaterial"
})
@XmlRootElement(name = "SubstancesMaterials", namespace = "")

public class SubstancesMaterials {

	@XmlElement(name = "SubstancesMaterial", namespace = "")
	protected List<SubstancesMaterial> substancesMaterial;


	public List<SubstancesMaterial> getSubstancesandMaterials() {
		if (substancesMaterial == null) {
			substancesMaterial = new ArrayList<SubstancesMaterial>();
		}
		return this.substancesMaterial;
	}

}
