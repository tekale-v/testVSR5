package com.pg.v4.beans;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlType;
@XmlType(propOrder={"SUCCESS", "GCAS", "VER"})
public class COSRequest implements Serializable
{
  private String GCAS;
  private String VER;
  private String SUCCESS;
  private String CT_NUMBER;
  private String COUNTRY_REQUESTED;
  private String CLEARANCE_COMMENT;
  private String PLANT_RESTRICTION;
  private String DATE_MOD;
  private String DATE_CREATED;
  private String PRODUCT_REGULATORY_CLASSIFICATION;
  private String COUNTRY_PRODUCT_REGISTRATION_NUMBER;
  private String MODIFIED_BY;
  private String OVERALL_CLEARANCE;
  private String REGISTRATION_STATUS;
  private String REGISTRATION_END_DATE;
  private String PSRA_APPROVAL_STATUS;
  //Added by DSM(Sogeti) 2018x.3 For COS SMART Integration  28169, 28307, 28308 & 28309 Starts
  private String REGISTRATION_RENEWAL_LEAD_TIME;
  private String REGISTRATION_RENEWAL_STATUS;
  private String PACKING_SITE;
  private String BULK_MAKING_MANUFACTURING_SITE;
  //Added by DSM(Sogeti) 2018x.3 For COS SMART Integration  28169, 28307, 28308 & 28309 Ends

  //Added by DSM(Sogeti) 2018x.5 For COS SMART Integration  34225 Starts
  private String REGISTERED_PRODUCT_NAME;
  //Added by DSM(Sogeti) 2018x.5 For COS SMART Integration  34225 Ends.


  public String getCT_NUMBER()
  {
     return CT_NUMBER;
  }

  public void setCT_NUMBER(String cT_NUMBER)
  {
     CT_NUMBER = cT_NUMBER;
  }

  public String getCOUNTRY_REQUESTED()
  {
     return COUNTRY_REQUESTED;
  }

  public void setCOUNTRY_REQUESTED(String cOUNTRY_REQUESTED)
  {
     COUNTRY_REQUESTED = cOUNTRY_REQUESTED;
  }

  public String getCLEARANCE_COMMENT()
  {
     return CLEARANCE_COMMENT;
  }

  public void setCLEARANCE_COMMENT(String cLEARANCE_COMMENT)
  {
     CLEARANCE_COMMENT = cLEARANCE_COMMENT;
  }

  public String getPLANT_RESTRICTION()
  {
    return PLANT_RESTRICTION;
  }

  public void setPLANT_RESTRICTION(String pLANT_RESTRICTION)
  {
    PLANT_RESTRICTION = pLANT_RESTRICTION;
  }

  public String getDATE_MOD()
  {
    return DATE_MOD;
  }

  public void setDATE_MOD(String dATE_MOD)
  {
    DATE_MOD = dATE_MOD;
  }

  public String getPRODUCT_REGULATORY_CLASSIFICATION()
  {
    return PRODUCT_REGULATORY_CLASSIFICATION;
  }

  public void setPRODUCT_REGULATORY_CLASSIFICATION(String pRODUCT_REGULATORY_CLASSIFICATION) {
    PRODUCT_REGULATORY_CLASSIFICATION = pRODUCT_REGULATORY_CLASSIFICATION;
  }
	public String getCOUNTRY_PRODUCT_REGISTRATION_NUMBER() {
		return COUNTRY_PRODUCT_REGISTRATION_NUMBER;
	}
	public void setCOUNTRY_PRODUCT_REGISTRATION_NUMBER(String cOUNTRY_PRODUCT_REGISTRATION_NUMBER) {
		COUNTRY_PRODUCT_REGISTRATION_NUMBER = cOUNTRY_PRODUCT_REGISTRATION_NUMBER;
	}
  public String getDATE_CREATED()
  {
    return DATE_CREATED;
  }

  public void setDATE_CREATED(String dATE_CREATED)
  {
    DATE_CREATED = dATE_CREATED;
  }

  public String getMODIFIED_BY()
  {
    return MODIFIED_BY;
  }

  public void setMODIFIED_BY(String mODIFIED_BY)
  {
    MODIFIED_BY = mODIFIED_BY;
  }

  public String getOVERALL_CLEARANCE()
  {
    return OVERALL_CLEARANCE;
  }

  public void setOVERALL_CLEARANCE(String oVERALL_CLEARANCE)
  {
    OVERALL_CLEARANCE = oVERALL_CLEARANCE;
  }

  public String getREGISTRATION_STATUS()
  {
    return REGISTRATION_STATUS;
  }

  public void setREGISTRATION_STATUS(String rEGISTRATION_STATUS)
  {
    REGISTRATION_STATUS = rEGISTRATION_STATUS;
  }

  public String getREGISTRATION_END_DATE()
  {
    return REGISTRATION_END_DATE;
  }

  public void setREGISTRATION_END_DATE(String rEGISTRATION_END_DATE)
  {
    REGISTRATION_END_DATE = rEGISTRATION_END_DATE;
  }

  public String getPSRA_APPROVAL_STATUS()
  {
    return PSRA_APPROVAL_STATUS;
  }

  public void setPSRA_APPROVAL_STATUS(String pSRA_APPROVAL_STATUS)
  {
    PSRA_APPROVAL_STATUS = pSRA_APPROVAL_STATUS;
  }

  public String getSUCCESS()
  {
    return SUCCESS;
  }

  public void setSUCCESS(String sUCCESS)
  {
    SUCCESS = sUCCESS;
  }

  public String getGCAS()
  {
    return GCAS;
  }

  public void setGCAS(String gCAS)
  {
    GCAS = gCAS;
  }

  public String getVER()
  {
    return VER;
  }

  public void setVER(String vER)
  {
    VER = vER;
  }

  //Added by DSM(Sogeti) 2018x.3 For COS SMART Integration  28169, 28307, 28308 & 28309 Starts
  public String getREGISTRATION_RENEWAL_LEAD_TIME() {
	return REGISTRATION_RENEWAL_LEAD_TIME;
  }
	
  public void setREGISTRATION_RENEWAL_LEAD_TIME(String strREGISTRATION_RENEWAL_LEAD_TIME) {
	REGISTRATION_RENEWAL_LEAD_TIME = strREGISTRATION_RENEWAL_LEAD_TIME;
  }
	
  public String getREGISTRATION_RENEWAL_STATUS() {
	return REGISTRATION_RENEWAL_STATUS;
  }
	
  public void setREGISTRATION_RENEWAL_STATUS(String strREGISTRATION_RENEWAL_STATUS) {
	 REGISTRATION_RENEWAL_STATUS = strREGISTRATION_RENEWAL_STATUS;
  }

  public String getPACKING_SITE() {
	 return PACKING_SITE;
  }

  public void setPACKING_SITE(String strPACKING_SITE) {
	 PACKING_SITE = strPACKING_SITE;
  }

  public String getBULK_MAKING_MANUFACTURING_SITE() {
	 return BULK_MAKING_MANUFACTURING_SITE;
  }

  public void setBULK_MAKING_MANUFACTURING_SITE(String strBULK_MAKING_MANUFACTURING_SITE) {
	 BULK_MAKING_MANUFACTURING_SITE = strBULK_MAKING_MANUFACTURING_SITE;
  }
    
  //Added by DSM(Sogeti) 2018x.3 For COS SMART Integration  28169, 28307, 28308 & 28309 Ends
    
  //Added by DSM(Sogeti) 2018x.5 For COS SMART Integration  34225 Starts
  public String getREGISTERED_PRODUCT_NAME() {
        return REGISTERED_PRODUCT_NAME;
  }
  public void setREGISTERED_PRODUCT_NAME(String strREGISTERED_PRODUCT_NAME) {
 	REGISTERED_PRODUCT_NAME = strREGISTERED_PRODUCT_NAME;
  }
  //Added by DSM(Sogeti) 2018x.5 For COS SMART Integration  34225 Ends

}
