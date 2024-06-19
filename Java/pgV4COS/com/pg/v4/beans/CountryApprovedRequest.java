package com.pg.v4.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class CountryApprovedRequest implements Serializable{

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

	ArrayList countryApprovedRows;
	String SUCCESS;
	String GCAS;
	String VER;

	public CountryApprovedRequest() {
		countryApprovedRows = new ArrayList();
		SUCCESS = "true";
		GCAS = "";
		VER = "";
	}
	
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "GCAS: " + GCAS + "VER: " + VER+" SUCCESS: "+SUCCESS+ "COUNTRIES: " + countryApprovedRows;
		
	}



	public void addCountryApprovedRows(CountryApprovedRow countryApprovedRow) {
		countryApprovedRows.add(countryApprovedRow);
	}

	public void setCountryApprovedRows(ArrayList countryApprovedRows) {
		this.countryApprovedRows = countryApprovedRows;
	}

	/**
	 * @return Returns the csmRows.
	 */
	public ArrayList getCountryApprovedRows() {
		return countryApprovedRows;
	}


	/**
	 * @return Returns the success.
	 */
	public String getSUCCESS() {
		return SUCCESS;
	}

	/**
	 * @param success
	 *            The success to set.
	 */
	public void setSUCCESS(String SUCCESS) {
		this.SUCCESS = SUCCESS;
	}

	/**
	 * @return Returns CSM rows in form of Array.
	 */
	public CountryApprovedRow[] getCountryApprovedRowsArray() {
		return (CountryApprovedRow[]) countryApprovedRows.toArray(new CountryApprovedRow[countryApprovedRows.size()]);
	}

	/**
	 * @return Returns the success.
	 */
	public boolean getSUCCESSBoolean() {
		return SUCCESS.equalsIgnoreCase("true");
	}
}
