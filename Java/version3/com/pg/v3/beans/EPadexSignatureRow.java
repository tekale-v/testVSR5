package com.pg.v3.beans;
import java.io.Serializable;
import java.util.ArrayList;

public class EPadexSignatureRow implements Serializable{
	private String EPADEX_Signature_Name;
	
	private String EPADEX_Signature_Timestamp;
	
	public String getEPADEX_Signature_Name() {
		return EPADEX_Signature_Name;
	}
	public void setEPADEX_Signature_Name(String EPADEX_Signature_Name) {
		this.EPADEX_Signature_Name = EPADEX_Signature_Name;
	}
	public String getEPADEX_Signature_Timestamp() {
		return EPADEX_Signature_Timestamp;
	}
	public void setEPADEX_Signature_Timestamp(String EPADEX_Signature_Timestamp) {
		this.EPADEX_Signature_Timestamp = EPADEX_Signature_Timestamp;
	}
	
}