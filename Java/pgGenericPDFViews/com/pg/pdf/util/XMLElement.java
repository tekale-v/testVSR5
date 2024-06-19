/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: XMLElement
 **/
package com.pg.pdf.util;

import java.util.Map;

import com.matrixone.jdom.Element;

import matrix.util.StringList;

public class XMLElement extends Element {

	public XMLElement(String element) {
		super(element);
	}

	public XMLElement(String element, String sValue) {
		super(element);
			setAttribute(element, sValue);
	}
	public XMLElement(String element, Map<String, String> attrMap) {
		super(element);
		for (Map.Entry<String,String> entry : attrMap.entrySet()) {
			String key = entry.getKey();
			key = key.replaceAll("\\s+","");
			setAttribute(key, entry.getValue());
		}
	}

	/**
	 * @Desc Method to add attribute(element) in xml document
	 * @param element -xml element name
	 * @param attrMap -xml element data
	 */
	public XMLElement(String element, Map<String, String> attrMap, StringList columnList) {
		super(element);
		for (Map.Entry<String,String> entry : attrMap.entrySet()) {
			String key = entry.getKey();
			if(columnList.contains(key))
				setAttribute(key, entry.getValue());
		}
	}
}
