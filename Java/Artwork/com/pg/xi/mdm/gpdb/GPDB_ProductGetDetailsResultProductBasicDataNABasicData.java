/**
 * GPDB_ProductGetDetailsResultProductBasicDataNABasicData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductBasicDataNABasicData  implements java.io.Serializable {
    /* ZTXXPTCMRA-ZPIDBORGID */
    private java.lang.String orgId;

    /* ZTXXPTCMRA-ZSUTCODE */
    private java.lang.String shipUnitTypeCode;

    /* ZTXXPTCMRA-ZBRANDTCOD */
    private java.lang.String brandTypeCode;

    /* ZTXXPTCMRA-ZTRUCKSTKH */
    private java.lang.String truckStackHeight;

    /* ZTXXPTCMRA-ZFTRUKUNITLD */
    private java.lang.String fullTruckUnitLoad;

    /* ZTXXPTCMRA-ZCUBORDFACT */
    private java.lang.String cubeOrderFactor;

    /* ZTXXPTCMRA-ZNBOFDOZEN */
    private java.lang.String numberOfDozens;

    /* ZTXXPTCMRA-ZPRICEW */
    private java.lang.String priceWeight;

    /* ZTXXPTCMRA-ZTOTINDITEM */
    private java.lang.String totalIndividualItem;

    /* ZTXXPTCMRA-ZCANUPCSFX */
    private java.lang.String canadianUPCSuffix;

    /* ZTXXPTCMRA-ZCSCONVFCT */
    private java.lang.String caseConversionFactor;

    /* ZTXXPTCMRA-ZCONSPKGSZ */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataConsumerPackageSize consumerPackageSize;

    /* ZTXXPTCMRA-ZALTBEGRU */
    private java.lang.String alternativeAuthorizationGroup;

    /* ZTXXPTCMRA-ZSA_SPECL_DESC */
    private java.lang.String SASpecialDescription;

    /* ZTXXPTCMRA-ZSALES_DESC_40 */
    private java.lang.String salesDescription;

    /* ZTXXPTCMRA-ZSHELFPCKIND */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataShelfPackIndicator shelfPackIndicator;

    public GPDB_ProductGetDetailsResultProductBasicDataNABasicData() {
    }

    public GPDB_ProductGetDetailsResultProductBasicDataNABasicData(
           java.lang.String orgId,
           java.lang.String shipUnitTypeCode,
           java.lang.String brandTypeCode,
           java.lang.String truckStackHeight,
           java.lang.String fullTruckUnitLoad,
           java.lang.String cubeOrderFactor,
           java.lang.String numberOfDozens,
           java.lang.String priceWeight,
           java.lang.String totalIndividualItem,
           java.lang.String canadianUPCSuffix,
           java.lang.String caseConversionFactor,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataConsumerPackageSize consumerPackageSize,
           java.lang.String alternativeAuthorizationGroup,
           java.lang.String SASpecialDescription,
           java.lang.String salesDescription,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataShelfPackIndicator shelfPackIndicator) {
           this.orgId = orgId;
           this.shipUnitTypeCode = shipUnitTypeCode;
           this.brandTypeCode = brandTypeCode;
           this.truckStackHeight = truckStackHeight;
           this.fullTruckUnitLoad = fullTruckUnitLoad;
           this.cubeOrderFactor = cubeOrderFactor;
           this.numberOfDozens = numberOfDozens;
           this.priceWeight = priceWeight;
           this.totalIndividualItem = totalIndividualItem;
           this.canadianUPCSuffix = canadianUPCSuffix;
           this.caseConversionFactor = caseConversionFactor;
           this.consumerPackageSize = consumerPackageSize;
           this.alternativeAuthorizationGroup = alternativeAuthorizationGroup;
           this.SASpecialDescription = SASpecialDescription;
           this.salesDescription = salesDescription;
           this.shelfPackIndicator = shelfPackIndicator;
    }


    /**
     * Gets the orgId value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return orgId   * ZTXXPTCMRA-ZPIDBORGID
     */
    public java.lang.String getOrgId() {
        return orgId;
    }


    /**
     * Sets the orgId value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param orgId   * ZTXXPTCMRA-ZPIDBORGID
     */
    public void setOrgId(java.lang.String orgId) {
        this.orgId = orgId;
    }


    /**
     * Gets the shipUnitTypeCode value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return shipUnitTypeCode   * ZTXXPTCMRA-ZSUTCODE
     */
    public java.lang.String getShipUnitTypeCode() {
        return shipUnitTypeCode;
    }


    /**
     * Sets the shipUnitTypeCode value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param shipUnitTypeCode   * ZTXXPTCMRA-ZSUTCODE
     */
    public void setShipUnitTypeCode(java.lang.String shipUnitTypeCode) {
        this.shipUnitTypeCode = shipUnitTypeCode;
    }


    /**
     * Gets the brandTypeCode value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return brandTypeCode   * ZTXXPTCMRA-ZBRANDTCOD
     */
    public java.lang.String getBrandTypeCode() {
        return brandTypeCode;
    }


    /**
     * Sets the brandTypeCode value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param brandTypeCode   * ZTXXPTCMRA-ZBRANDTCOD
     */
    public void setBrandTypeCode(java.lang.String brandTypeCode) {
        this.brandTypeCode = brandTypeCode;
    }


    /**
     * Gets the truckStackHeight value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return truckStackHeight   * ZTXXPTCMRA-ZTRUCKSTKH
     */
    public java.lang.String getTruckStackHeight() {
        return truckStackHeight;
    }


    /**
     * Sets the truckStackHeight value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param truckStackHeight   * ZTXXPTCMRA-ZTRUCKSTKH
     */
    public void setTruckStackHeight(java.lang.String truckStackHeight) {
        this.truckStackHeight = truckStackHeight;
    }


    /**
     * Gets the fullTruckUnitLoad value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return fullTruckUnitLoad   * ZTXXPTCMRA-ZFTRUKUNITLD
     */
    public java.lang.String getFullTruckUnitLoad() {
        return fullTruckUnitLoad;
    }


    /**
     * Sets the fullTruckUnitLoad value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param fullTruckUnitLoad   * ZTXXPTCMRA-ZFTRUKUNITLD
     */
    public void setFullTruckUnitLoad(java.lang.String fullTruckUnitLoad) {
        this.fullTruckUnitLoad = fullTruckUnitLoad;
    }


    /**
     * Gets the cubeOrderFactor value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return cubeOrderFactor   * ZTXXPTCMRA-ZCUBORDFACT
     */
    public java.lang.String getCubeOrderFactor() {
        return cubeOrderFactor;
    }


    /**
     * Sets the cubeOrderFactor value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param cubeOrderFactor   * ZTXXPTCMRA-ZCUBORDFACT
     */
    public void setCubeOrderFactor(java.lang.String cubeOrderFactor) {
        this.cubeOrderFactor = cubeOrderFactor;
    }


    /**
     * Gets the numberOfDozens value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return numberOfDozens   * ZTXXPTCMRA-ZNBOFDOZEN
     */
    public java.lang.String getNumberOfDozens() {
        return numberOfDozens;
    }


    /**
     * Sets the numberOfDozens value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param numberOfDozens   * ZTXXPTCMRA-ZNBOFDOZEN
     */
    public void setNumberOfDozens(java.lang.String numberOfDozens) {
        this.numberOfDozens = numberOfDozens;
    }


    /**
     * Gets the priceWeight value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return priceWeight   * ZTXXPTCMRA-ZPRICEW
     */
    public java.lang.String getPriceWeight() {
        return priceWeight;
    }


    /**
     * Sets the priceWeight value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param priceWeight   * ZTXXPTCMRA-ZPRICEW
     */
    public void setPriceWeight(java.lang.String priceWeight) {
        this.priceWeight = priceWeight;
    }


    /**
     * Gets the totalIndividualItem value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return totalIndividualItem   * ZTXXPTCMRA-ZTOTINDITEM
     */
    public java.lang.String getTotalIndividualItem() {
        return totalIndividualItem;
    }


    /**
     * Sets the totalIndividualItem value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param totalIndividualItem   * ZTXXPTCMRA-ZTOTINDITEM
     */
    public void setTotalIndividualItem(java.lang.String totalIndividualItem) {
        this.totalIndividualItem = totalIndividualItem;
    }


    /**
     * Gets the canadianUPCSuffix value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return canadianUPCSuffix   * ZTXXPTCMRA-ZCANUPCSFX
     */
    public java.lang.String getCanadianUPCSuffix() {
        return canadianUPCSuffix;
    }


    /**
     * Sets the canadianUPCSuffix value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param canadianUPCSuffix   * ZTXXPTCMRA-ZCANUPCSFX
     */
    public void setCanadianUPCSuffix(java.lang.String canadianUPCSuffix) {
        this.canadianUPCSuffix = canadianUPCSuffix;
    }


    /**
     * Gets the caseConversionFactor value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return caseConversionFactor   * ZTXXPTCMRA-ZCSCONVFCT
     */
    public java.lang.String getCaseConversionFactor() {
        return caseConversionFactor;
    }


    /**
     * Sets the caseConversionFactor value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param caseConversionFactor   * ZTXXPTCMRA-ZCSCONVFCT
     */
    public void setCaseConversionFactor(java.lang.String caseConversionFactor) {
        this.caseConversionFactor = caseConversionFactor;
    }


    /**
     * Gets the consumerPackageSize value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return consumerPackageSize   * ZTXXPTCMRA-ZCONSPKGSZ
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataConsumerPackageSize getConsumerPackageSize() {
        return consumerPackageSize;
    }


    /**
     * Sets the consumerPackageSize value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param consumerPackageSize   * ZTXXPTCMRA-ZCONSPKGSZ
     */
    public void setConsumerPackageSize(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataConsumerPackageSize consumerPackageSize) {
        this.consumerPackageSize = consumerPackageSize;
    }


    /**
     * Gets the alternativeAuthorizationGroup value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return alternativeAuthorizationGroup   * ZTXXPTCMRA-ZALTBEGRU
     */
    public java.lang.String getAlternativeAuthorizationGroup() {
        return alternativeAuthorizationGroup;
    }


    /**
     * Sets the alternativeAuthorizationGroup value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param alternativeAuthorizationGroup   * ZTXXPTCMRA-ZALTBEGRU
     */
    public void setAlternativeAuthorizationGroup(java.lang.String alternativeAuthorizationGroup) {
        this.alternativeAuthorizationGroup = alternativeAuthorizationGroup;
    }


    /**
     * Gets the SASpecialDescription value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return SASpecialDescription   * ZTXXPTCMRA-ZSA_SPECL_DESC
     */
    public java.lang.String getSASpecialDescription() {
        return SASpecialDescription;
    }


    /**
     * Sets the SASpecialDescription value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param SASpecialDescription   * ZTXXPTCMRA-ZSA_SPECL_DESC
     */
    public void setSASpecialDescription(java.lang.String SASpecialDescription) {
        this.SASpecialDescription = SASpecialDescription;
    }


    /**
     * Gets the salesDescription value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return salesDescription   * ZTXXPTCMRA-ZSALES_DESC_40
     */
    public java.lang.String getSalesDescription() {
        return salesDescription;
    }


    /**
     * Sets the salesDescription value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param salesDescription   * ZTXXPTCMRA-ZSALES_DESC_40
     */
    public void setSalesDescription(java.lang.String salesDescription) {
        this.salesDescription = salesDescription;
    }


    /**
     * Gets the shelfPackIndicator value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @return shelfPackIndicator   * ZTXXPTCMRA-ZSHELFPCKIND
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataShelfPackIndicator getShelfPackIndicator() {
        return shelfPackIndicator;
    }


    /**
     * Sets the shelfPackIndicator value for this GPDB_ProductGetDetailsResultProductBasicDataNABasicData.
     * 
     * @param shelfPackIndicator   * ZTXXPTCMRA-ZSHELFPCKIND
     */
    public void setShelfPackIndicator(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicDataShelfPackIndicator shelfPackIndicator) {
        this.shelfPackIndicator = shelfPackIndicator;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductBasicDataNABasicData)) return false;
        GPDB_ProductGetDetailsResultProductBasicDataNABasicData other = (GPDB_ProductGetDetailsResultProductBasicDataNABasicData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.orgId==null && other.getOrgId()==null) || 
             (this.orgId!=null &&
              this.orgId.equals(other.getOrgId()))) &&
            ((this.shipUnitTypeCode==null && other.getShipUnitTypeCode()==null) || 
             (this.shipUnitTypeCode!=null &&
              this.shipUnitTypeCode.equals(other.getShipUnitTypeCode()))) &&
            ((this.brandTypeCode==null && other.getBrandTypeCode()==null) || 
             (this.brandTypeCode!=null &&
              this.brandTypeCode.equals(other.getBrandTypeCode()))) &&
            ((this.truckStackHeight==null && other.getTruckStackHeight()==null) || 
             (this.truckStackHeight!=null &&
              this.truckStackHeight.equals(other.getTruckStackHeight()))) &&
            ((this.fullTruckUnitLoad==null && other.getFullTruckUnitLoad()==null) || 
             (this.fullTruckUnitLoad!=null &&
              this.fullTruckUnitLoad.equals(other.getFullTruckUnitLoad()))) &&
            ((this.cubeOrderFactor==null && other.getCubeOrderFactor()==null) || 
             (this.cubeOrderFactor!=null &&
              this.cubeOrderFactor.equals(other.getCubeOrderFactor()))) &&
            ((this.numberOfDozens==null && other.getNumberOfDozens()==null) || 
             (this.numberOfDozens!=null &&
              this.numberOfDozens.equals(other.getNumberOfDozens()))) &&
            ((this.priceWeight==null && other.getPriceWeight()==null) || 
             (this.priceWeight!=null &&
              this.priceWeight.equals(other.getPriceWeight()))) &&
            ((this.totalIndividualItem==null && other.getTotalIndividualItem()==null) || 
             (this.totalIndividualItem!=null &&
              this.totalIndividualItem.equals(other.getTotalIndividualItem()))) &&
            ((this.canadianUPCSuffix==null && other.getCanadianUPCSuffix()==null) || 
             (this.canadianUPCSuffix!=null &&
              this.canadianUPCSuffix.equals(other.getCanadianUPCSuffix()))) &&
            ((this.caseConversionFactor==null && other.getCaseConversionFactor()==null) || 
             (this.caseConversionFactor!=null &&
              this.caseConversionFactor.equals(other.getCaseConversionFactor()))) &&
            ((this.consumerPackageSize==null && other.getConsumerPackageSize()==null) || 
             (this.consumerPackageSize!=null &&
              this.consumerPackageSize.equals(other.getConsumerPackageSize()))) &&
            ((this.alternativeAuthorizationGroup==null && other.getAlternativeAuthorizationGroup()==null) || 
             (this.alternativeAuthorizationGroup!=null &&
              this.alternativeAuthorizationGroup.equals(other.getAlternativeAuthorizationGroup()))) &&
            ((this.SASpecialDescription==null && other.getSASpecialDescription()==null) || 
             (this.SASpecialDescription!=null &&
              this.SASpecialDescription.equals(other.getSASpecialDescription()))) &&
            ((this.salesDescription==null && other.getSalesDescription()==null) || 
             (this.salesDescription!=null &&
              this.salesDescription.equals(other.getSalesDescription()))) &&
            ((this.shelfPackIndicator==null && other.getShelfPackIndicator()==null) || 
             (this.shelfPackIndicator!=null &&
              this.shelfPackIndicator.equals(other.getShelfPackIndicator())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getOrgId() != null) {
            _hashCode += getOrgId().hashCode();
        }
        if (getShipUnitTypeCode() != null) {
            _hashCode += getShipUnitTypeCode().hashCode();
        }
        if (getBrandTypeCode() != null) {
            _hashCode += getBrandTypeCode().hashCode();
        }
        if (getTruckStackHeight() != null) {
            _hashCode += getTruckStackHeight().hashCode();
        }
        if (getFullTruckUnitLoad() != null) {
            _hashCode += getFullTruckUnitLoad().hashCode();
        }
        if (getCubeOrderFactor() != null) {
            _hashCode += getCubeOrderFactor().hashCode();
        }
        if (getNumberOfDozens() != null) {
            _hashCode += getNumberOfDozens().hashCode();
        }
        if (getPriceWeight() != null) {
            _hashCode += getPriceWeight().hashCode();
        }
        if (getTotalIndividualItem() != null) {
            _hashCode += getTotalIndividualItem().hashCode();
        }
        if (getCanadianUPCSuffix() != null) {
            _hashCode += getCanadianUPCSuffix().hashCode();
        }
        if (getCaseConversionFactor() != null) {
            _hashCode += getCaseConversionFactor().hashCode();
        }
        if (getConsumerPackageSize() != null) {
            _hashCode += getConsumerPackageSize().hashCode();
        }
        if (getAlternativeAuthorizationGroup() != null) {
            _hashCode += getAlternativeAuthorizationGroup().hashCode();
        }
        if (getSASpecialDescription() != null) {
            _hashCode += getSASpecialDescription().hashCode();
        }
        if (getSalesDescription() != null) {
            _hashCode += getSalesDescription().hashCode();
        }
        if (getShelfPackIndicator() != null) {
            _hashCode += getShelfPackIndicator().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductBasicDataNABasicData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>BasicData>NABasicData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orgId");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "OrgId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipUnitTypeCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ShipUnitTypeCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("brandTypeCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "BrandTypeCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("truckStackHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "TruckStackHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullTruckUnitLoad");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "FullTruckUnitLoad"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cubeOrderFactor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CubeOrderFactor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfDozens");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "NumberOfDozens"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priceWeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "PriceWeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalIndividualItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "TotalIndividualItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("canadianUPCSuffix");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CanadianUPCSuffix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("caseConversionFactor");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CaseConversionFactor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("consumerPackageSize");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ConsumerPackageSize"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>BasicData>NABasicData>ConsumerPackageSize"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alternativeAuthorizationGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "AlternativeAuthorizationGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SASpecialDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "SASpecialDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("salesDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "SalesDescription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shelfPackIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ShelfPackIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>BasicData>NABasicData>ShelfPackIndicator"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
