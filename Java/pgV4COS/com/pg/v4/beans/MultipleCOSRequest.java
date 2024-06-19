package com.pg.v4.beans;

import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"SUCCESS", "GCAS", "VER"})
public class MultipleCOSRequest
  implements Serializable
{
	 private String GCAS;
	 private String VER;
	 private String SUCCESS;
	 protected List<Countries> country;
	 
   private List<Countries> lCountries = new ArrayList<Countries>();

    public void addCountry(Countries countryObj) {
    	lCountries.add(countryObj);
    }
 
	public List<Countries> getCountry() {
		return lCountries;
	}

	public String getGCAS() {
		return GCAS;
	}

	public void setGCAS(String gCAS) {
		GCAS = gCAS;
	}

	public String getVER() {
		return VER;
	}

	public void setVER(String vER) {
		VER = vER;
	}

	public String getSUCCESS() {
		return SUCCESS;
	}

	public void setSUCCESS(String sUCCESS) {
		SUCCESS = sUCCESS;
	}


public static void main(String[] args) {
	System.out.println("jh");
}




}
