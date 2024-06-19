
package com.pg.enovia.mos.restapp.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"column"
})
public class Columns {
	@XmlElement(name = "Column", namespace = "") 
	 protected List<Column> column;
	    
	    
    public List<Column> getColumn() {
    	 if(column == null) {
    		 column = new ArrayList<>();
         }
	   return this.column;
	}

	public void setColumn(List<Column> column) {
		this.column = column;
	}
}
