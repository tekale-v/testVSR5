package com.pg.v4.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ROWSET")
public class COSRequestWrapper implements Serializable{
	
	private COSRequest ROW;
	
	public COSRequest getROW() {
		return ROW;
	}

	public void setROW(COSRequest ROW) {
		this.ROW = ROW;
	}
		

}
