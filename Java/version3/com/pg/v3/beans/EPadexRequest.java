package com.pg.v3.beans;


import java.io.Serializable;
import java.util.ArrayList;

public class EPadexRequest implements Serializable{
	private String ARTWORK_PDF;
	
	private String EPADEX_POA_Number;
	
	private String EPADEX_Project_Name;
	
	private String EPADEX_Project_Number;
	
	private String DESCRIPTION;
	
	private String SAP_Description;
	
	private String OWNING_Region;
	
	private String OWNING_GBU;
	
	private String OWNING_Category;
	
	private String OWNING_Standards_Office;
	
	private String COUNTRY;
	
	private String EPADEX_Signature_Name;
	
	private String EPADEX_Signature_Timestamp;
	
	private String EPADEX_Contact_Email_Address;
	
	private String RELATED_IPMS_GCAS_Code;
	
	private String  ARTWORK_Language;
	
	private String  EPADEX_Release_Date;
	
	private String CR_Description;
	
	private String CR_GBU;
	
	private String EC_Owning_Standards_Office;
	
	private String  PRIMARY_Affected_Category;
	
	private String  CR_Reason_For_Change;
	
	public String getEPADEX_Project_Number() {
		return EPADEX_Project_Number;
	}
	public void setEPADEX_Project_Number(String EPADEX_Project_Number) {
		this.EPADEX_Project_Number = EPADEX_Project_Number;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String DESCRIPTION) {
		this.DESCRIPTION = DESCRIPTION;
	}
	public String getSAP_Description() {
		return SAP_Description;
	}
	public void setSAP_Description(String SAP_Description) {
		this.SAP_Description = SAP_Description;
	}
	public String getOWNING_Region() {
		return OWNING_Region;
	}
	public void setOWNING_Region(String OWNING_Region) {
		this.OWNING_Region = OWNING_Region;
		
	}
	public String getOWNING_GBU() {
		return OWNING_GBU;
	}
	public void setOWNING_GBU(String OWNING_GBU) {
		this.OWNING_GBU = OWNING_GBU;
	}
	public String getOWNING_Category() {
		return OWNING_Category;
	}
	public void setOWNING_Category(String OWNING_Category) {
		this.OWNING_Category = OWNING_Category;
	}
	public String getOWNING_Standards_Office() {
		return OWNING_Standards_Office;
	}
	public void setOWNING_Standards_Office(String OWNING_Standards_Office) {
		this.OWNING_Standards_Office = OWNING_Standards_Office;
	}
	public String getCOUNTRY() {
		return COUNTRY;
	}
	public void setCOUNTRY(String COUNTRY) {
		this.COUNTRY = COUNTRY;
	}
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
	public String getEPADEX_Contact_Email_Address() {
		return EPADEX_Contact_Email_Address;
	}
	public void setEPADEX_Contact_Email_Address(String EPADEX_Contact_Email_Address) {
		this.EPADEX_Contact_Email_Address = EPADEX_Contact_Email_Address;

	}
	public String getRELATED_IPMS_GCAS_Code() {
		return RELATED_IPMS_GCAS_Code;
	}
	public void setRELATED_IPMS_GCAS_Code(String RELATED_IPMS_GCAS_Code) {
		this.RELATED_IPMS_GCAS_Code = RELATED_IPMS_GCAS_Code;
	}
	public String getARTWORK_Language() {
		return ARTWORK_Language;
	}
	public void setARTWORK_Language(String ARTWORK_Language) {
		this.ARTWORK_Language = ARTWORK_Language;
	}
	public String getEPADEX_Release_Date() {
		return EPADEX_Release_Date;
	}
	public void setEPADEX_Release_Date(String EPADEX_Release_Date) {
		this.EPADEX_Release_Date = EPADEX_Release_Date;
	}
	public String getCR_Description() {
		return CR_Description;
	}
	public void setCR_Description(String CR_Description) {
		this.CR_Description = CR_Description;
	}
	public String getCR_GBU() {
		return CR_GBU;
	}
	public void setCR_GBU(String CR_GBU) {
		this.CR_GBU = CR_GBU;
	}
	public String getEC_Owning_Standards_Office() {
		return EC_Owning_Standards_Office;
	}
	public void setEC_Owning_Standards_Office(String EC_Owning_Standards_Office) {
		this.EC_Owning_Standards_Office = EC_Owning_Standards_Office;
	}
	public String getPRIMARY_Affected_Category() {
		return PRIMARY_Affected_Category;
	}
	public void setPRIMARY_Affected_Category(String PRIMARY_Affected_Category) {
		this.PRIMARY_Affected_Category = PRIMARY_Affected_Category;
	}
	public String getCR_Reason_For_Change() {
		return CR_Reason_For_Change;
	}
	public void setCR_Reason_For_Change(String CR_Reason_For_Change) {
		this.CR_Reason_For_Change = CR_Reason_For_Change;
	}
	public String getEPADEX_Project_Name() {
		return EPADEX_Project_Name;
	}
	public void setEPADEX_Project_Name(String EPADEX_Project_Name) {
		this.EPADEX_Project_Name = EPADEX_Project_Name;
	}
	public String getARTWORK_PDF() {
		return ARTWORK_PDF;
	}
	public void setARTWORK_PDF(String ARTWORK_PDF) {
		this.ARTWORK_PDF = ARTWORK_PDF;
	}
	public String getEPADEX_POA_Number() {
		return EPADEX_POA_Number;
	}
	public void setEPADEX_POA_Number(String EPADEX_POA_Number) {
		this.EPADEX_POA_Number = EPADEX_POA_Number;
	}
	
	ArrayList COUNTRYEpadexRows;
	ArrayList EPADEXRows;
	ArrayList IPMSRows;
	ArrayList ARTWORKRows;
	ArrayList CHANGERequest;
	public EPadexRequest() {
		COUNTRYEpadexRows = new ArrayList();
		EPADEXRows = new ArrayList();
		IPMSRows = new ArrayList();
		ARTWORKRows = new ArrayList();
		CHANGERequest = new ArrayList();
		
	}

	public void addCOUNTRYEpadexRows(CountryEpadexRow countryEpadex) {
		COUNTRYEpadexRows.add(countryEpadex);
	}

	public void setCOUNTRYEpadexRows(ArrayList COUNTRYEpadexRows) {
		this.COUNTRYEpadexRows = COUNTRYEpadexRows;
	}
	
	public ArrayList getCOUNTRYEpadexRows() {
		return COUNTRYEpadexRows;
	}
	
	public void addEPADEXRows(EPadexSignatureRow epadexRowsObj) {
		EPADEXRows.add(epadexRowsObj);
	}

	public void setEPADEXRows(ArrayList EPADEXRows) {
		this.EPADEXRows = EPADEXRows;
	}
	
	public ArrayList getEpadexRows() {
		return EPADEXRows;
	}
	
	public void addIPMSRows(RelatedIPMSRow iMPSRowsEpadex) {
		IPMSRows.add(iMPSRowsEpadex);
	}

	public void setIPMSRows(ArrayList IPMSRows) {
		this.IPMSRows = IPMSRows;
	}

	public ArrayList getIPMSRows() {
		return IPMSRows;
	}
	
	

	public void addARTWORKRows(RelatedArtworkRow artworkRowsEpadex) {
		ARTWORKRows.add(artworkRowsEpadex);
	}

	public void setARTWORKRows(ArrayList ARTWORKRows) {
		this.ARTWORKRows = ARTWORKRows;
	}

	
	public ArrayList getARTWORKRows() {
		return ARTWORKRows;
	}
	
	
	
	
}
