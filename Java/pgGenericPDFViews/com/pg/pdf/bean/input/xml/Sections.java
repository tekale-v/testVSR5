
package com.pg.pdf.bean.input.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder = {
		"section"
})
@XmlRootElement(name = "sections")
public class Sections {
	@XmlElement(required = true)
	protected List<Section> section;

	public List<Section> getSection() {
		if (section == null) {
			section = new ArrayList<Section>();
		}
		return this.section;
	}
}
