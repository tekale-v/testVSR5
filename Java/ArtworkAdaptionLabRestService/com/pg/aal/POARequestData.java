package com.pg.aal;
public class POARequestData {
	private String poaNumber;
	private String zipFileData;
	
	public POARequestData() {
		super();
	}

	public String getPoaNumber() {
		return poaNumber;
	}

	public void setPoaNumber(String poaNumber) {
		this.poaNumber = poaNumber;
	}
	
	public void setZipFileData(String zipFileData) {
		this.zipFileData = zipFileData;
	}
	
	public String getZipFileData() {
		return zipFileData;
	}
}
