/**
 * GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductPlantDataPlantEntry  implements java.io.Serializable {
    /* MARC-EKGRP */
    private java.lang.Object purchasingGroup;

    /* MARC-HERKL */
    private java.lang.Object countryOfOrigin;

    /* MARC-INSMK */
    private java.lang.Object postToInspectionStock;

    /* MARC-KAUTB */
    private java.lang.Object automaticPurchaseOrderAllowed;

    /* MARC-LADGR */
    private java.lang.Object loadingGroup;

    /* MARC-LGFSB */
    private java.lang.Object defaultStorageLocation;

    /* MARC-MFRGR */
    private java.lang.Object materialFreightGroup;

    /* MARC-MTVFP */
    private java.lang.Object checkingGroupForAvailabilityCheck;

    /* MARC-STAWN */
    private java.lang.Object commodityCode;

    /* MARC-USEQU */
    private java.lang.Object quotaArrangementUsage;

    /* MARC-XMCNG */
    private java.lang.Object negativeStocksAllowed;

    private java.lang.Object plant;  // attribute

    public GPDB_ProductGetDetailsResultProductPlantDataPlantEntry() {
    }

    public GPDB_ProductGetDetailsResultProductPlantDataPlantEntry(
           java.lang.Object purchasingGroup,
           java.lang.Object countryOfOrigin,
           java.lang.Object postToInspectionStock,
           java.lang.Object automaticPurchaseOrderAllowed,
           java.lang.Object loadingGroup,
           java.lang.Object defaultStorageLocation,
           java.lang.Object materialFreightGroup,
           java.lang.Object checkingGroupForAvailabilityCheck,
           java.lang.Object commodityCode,
           java.lang.Object quotaArrangementUsage,
           java.lang.Object negativeStocksAllowed,
           java.lang.Object plant) {
           this.purchasingGroup = purchasingGroup;
           this.countryOfOrigin = countryOfOrigin;
           this.postToInspectionStock = postToInspectionStock;
           this.automaticPurchaseOrderAllowed = automaticPurchaseOrderAllowed;
           this.loadingGroup = loadingGroup;
           this.defaultStorageLocation = defaultStorageLocation;
           this.materialFreightGroup = materialFreightGroup;
           this.checkingGroupForAvailabilityCheck = checkingGroupForAvailabilityCheck;
           this.commodityCode = commodityCode;
           this.quotaArrangementUsage = quotaArrangementUsage;
           this.negativeStocksAllowed = negativeStocksAllowed;
           this.plant = plant;
    }


    /**
     * Gets the purchasingGroup value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return purchasingGroup   * MARC-EKGRP
     */
    public java.lang.Object getPurchasingGroup() {
        return purchasingGroup;
    }


    /**
     * Sets the purchasingGroup value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param purchasingGroup   * MARC-EKGRP
     */
    public void setPurchasingGroup(java.lang.Object purchasingGroup) {
        this.purchasingGroup = purchasingGroup;
    }


    /**
     * Gets the countryOfOrigin value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return countryOfOrigin   * MARC-HERKL
     */
    public java.lang.Object getCountryOfOrigin() {
        return countryOfOrigin;
    }


    /**
     * Sets the countryOfOrigin value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param countryOfOrigin   * MARC-HERKL
     */
    public void setCountryOfOrigin(java.lang.Object countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }


    /**
     * Gets the postToInspectionStock value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return postToInspectionStock   * MARC-INSMK
     */
    public java.lang.Object getPostToInspectionStock() {
        return postToInspectionStock;
    }


    /**
     * Sets the postToInspectionStock value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param postToInspectionStock   * MARC-INSMK
     */
    public void setPostToInspectionStock(java.lang.Object postToInspectionStock) {
        this.postToInspectionStock = postToInspectionStock;
    }


    /**
     * Gets the automaticPurchaseOrderAllowed value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return automaticPurchaseOrderAllowed   * MARC-KAUTB
     */
    public java.lang.Object getAutomaticPurchaseOrderAllowed() {
        return automaticPurchaseOrderAllowed;
    }


    /**
     * Sets the automaticPurchaseOrderAllowed value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param automaticPurchaseOrderAllowed   * MARC-KAUTB
     */
    public void setAutomaticPurchaseOrderAllowed(java.lang.Object automaticPurchaseOrderAllowed) {
        this.automaticPurchaseOrderAllowed = automaticPurchaseOrderAllowed;
    }


    /**
     * Gets the loadingGroup value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return loadingGroup   * MARC-LADGR
     */
    public java.lang.Object getLoadingGroup() {
        return loadingGroup;
    }


    /**
     * Sets the loadingGroup value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param loadingGroup   * MARC-LADGR
     */
    public void setLoadingGroup(java.lang.Object loadingGroup) {
        this.loadingGroup = loadingGroup;
    }


    /**
     * Gets the defaultStorageLocation value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return defaultStorageLocation   * MARC-LGFSB
     */
    public java.lang.Object getDefaultStorageLocation() {
        return defaultStorageLocation;
    }


    /**
     * Sets the defaultStorageLocation value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param defaultStorageLocation   * MARC-LGFSB
     */
    public void setDefaultStorageLocation(java.lang.Object defaultStorageLocation) {
        this.defaultStorageLocation = defaultStorageLocation;
    }


    /**
     * Gets the materialFreightGroup value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return materialFreightGroup   * MARC-MFRGR
     */
    public java.lang.Object getMaterialFreightGroup() {
        return materialFreightGroup;
    }


    /**
     * Sets the materialFreightGroup value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param materialFreightGroup   * MARC-MFRGR
     */
    public void setMaterialFreightGroup(java.lang.Object materialFreightGroup) {
        this.materialFreightGroup = materialFreightGroup;
    }


    /**
     * Gets the checkingGroupForAvailabilityCheck value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return checkingGroupForAvailabilityCheck   * MARC-MTVFP
     */
    public java.lang.Object getCheckingGroupForAvailabilityCheck() {
        return checkingGroupForAvailabilityCheck;
    }


    /**
     * Sets the checkingGroupForAvailabilityCheck value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param checkingGroupForAvailabilityCheck   * MARC-MTVFP
     */
    public void setCheckingGroupForAvailabilityCheck(java.lang.Object checkingGroupForAvailabilityCheck) {
        this.checkingGroupForAvailabilityCheck = checkingGroupForAvailabilityCheck;
    }


    /**
     * Gets the commodityCode value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return commodityCode   * MARC-STAWN
     */
    public java.lang.Object getCommodityCode() {
        return commodityCode;
    }


    /**
     * Sets the commodityCode value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param commodityCode   * MARC-STAWN
     */
    public void setCommodityCode(java.lang.Object commodityCode) {
        this.commodityCode = commodityCode;
    }


    /**
     * Gets the quotaArrangementUsage value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return quotaArrangementUsage   * MARC-USEQU
     */
    public java.lang.Object getQuotaArrangementUsage() {
        return quotaArrangementUsage;
    }


    /**
     * Sets the quotaArrangementUsage value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param quotaArrangementUsage   * MARC-USEQU
     */
    public void setQuotaArrangementUsage(java.lang.Object quotaArrangementUsage) {
        this.quotaArrangementUsage = quotaArrangementUsage;
    }


    /**
     * Gets the negativeStocksAllowed value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return negativeStocksAllowed   * MARC-XMCNG
     */
    public java.lang.Object getNegativeStocksAllowed() {
        return negativeStocksAllowed;
    }


    /**
     * Sets the negativeStocksAllowed value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param negativeStocksAllowed   * MARC-XMCNG
     */
    public void setNegativeStocksAllowed(java.lang.Object negativeStocksAllowed) {
        this.negativeStocksAllowed = negativeStocksAllowed;
    }


    /**
     * Gets the plant value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @return plant
     */
    public java.lang.Object getPlant() {
        return plant;
    }


    /**
     * Sets the plant value for this GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.
     * 
     * @param plant
     */
    public void setPlant(java.lang.Object plant) {
        this.plant = plant;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductPlantDataPlantEntry)) return false;
        GPDB_ProductGetDetailsResultProductPlantDataPlantEntry other = (GPDB_ProductGetDetailsResultProductPlantDataPlantEntry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.purchasingGroup==null && other.getPurchasingGroup()==null) || 
             (this.purchasingGroup!=null &&
              this.purchasingGroup.equals(other.getPurchasingGroup()))) &&
            ((this.countryOfOrigin==null && other.getCountryOfOrigin()==null) || 
             (this.countryOfOrigin!=null &&
              this.countryOfOrigin.equals(other.getCountryOfOrigin()))) &&
            ((this.postToInspectionStock==null && other.getPostToInspectionStock()==null) || 
             (this.postToInspectionStock!=null &&
              this.postToInspectionStock.equals(other.getPostToInspectionStock()))) &&
            ((this.automaticPurchaseOrderAllowed==null && other.getAutomaticPurchaseOrderAllowed()==null) || 
             (this.automaticPurchaseOrderAllowed!=null &&
              this.automaticPurchaseOrderAllowed.equals(other.getAutomaticPurchaseOrderAllowed()))) &&
            ((this.loadingGroup==null && other.getLoadingGroup()==null) || 
             (this.loadingGroup!=null &&
              this.loadingGroup.equals(other.getLoadingGroup()))) &&
            ((this.defaultStorageLocation==null && other.getDefaultStorageLocation()==null) || 
             (this.defaultStorageLocation!=null &&
              this.defaultStorageLocation.equals(other.getDefaultStorageLocation()))) &&
            ((this.materialFreightGroup==null && other.getMaterialFreightGroup()==null) || 
             (this.materialFreightGroup!=null &&
              this.materialFreightGroup.equals(other.getMaterialFreightGroup()))) &&
            ((this.checkingGroupForAvailabilityCheck==null && other.getCheckingGroupForAvailabilityCheck()==null) || 
             (this.checkingGroupForAvailabilityCheck!=null &&
              this.checkingGroupForAvailabilityCheck.equals(other.getCheckingGroupForAvailabilityCheck()))) &&
            ((this.commodityCode==null && other.getCommodityCode()==null) || 
             (this.commodityCode!=null &&
              this.commodityCode.equals(other.getCommodityCode()))) &&
            ((this.quotaArrangementUsage==null && other.getQuotaArrangementUsage()==null) || 
             (this.quotaArrangementUsage!=null &&
              this.quotaArrangementUsage.equals(other.getQuotaArrangementUsage()))) &&
            ((this.negativeStocksAllowed==null && other.getNegativeStocksAllowed()==null) || 
             (this.negativeStocksAllowed!=null &&
              this.negativeStocksAllowed.equals(other.getNegativeStocksAllowed()))) &&
            ((this.plant==null && other.getPlant()==null) || 
             (this.plant!=null &&
              this.plant.equals(other.getPlant())));
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
        if (getPurchasingGroup() != null) {
            _hashCode += getPurchasingGroup().hashCode();
        }
        if (getCountryOfOrigin() != null) {
            _hashCode += getCountryOfOrigin().hashCode();
        }
        if (getPostToInspectionStock() != null) {
            _hashCode += getPostToInspectionStock().hashCode();
        }
        if (getAutomaticPurchaseOrderAllowed() != null) {
            _hashCode += getAutomaticPurchaseOrderAllowed().hashCode();
        }
        if (getLoadingGroup() != null) {
            _hashCode += getLoadingGroup().hashCode();
        }
        if (getDefaultStorageLocation() != null) {
            _hashCode += getDefaultStorageLocation().hashCode();
        }
        if (getMaterialFreightGroup() != null) {
            _hashCode += getMaterialFreightGroup().hashCode();
        }
        if (getCheckingGroupForAvailabilityCheck() != null) {
            _hashCode += getCheckingGroupForAvailabilityCheck().hashCode();
        }
        if (getCommodityCode() != null) {
            _hashCode += getCommodityCode().hashCode();
        }
        if (getQuotaArrangementUsage() != null) {
            _hashCode += getQuotaArrangementUsage().hashCode();
        }
        if (getNegativeStocksAllowed() != null) {
            _hashCode += getNegativeStocksAllowed().hashCode();
        }
        if (getPlant() != null) {
            _hashCode += getPlant().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductPlantDataPlantEntry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>PlantData>PlantEntry"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("plant");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Plant"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchasingGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "PurchasingGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryOfOrigin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CountryOfOrigin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postToInspectionStock");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "PostToInspectionStock"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("automaticPurchaseOrderAllowed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "AutomaticPurchaseOrderAllowed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadingGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "LoadingGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("defaultStorageLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DefaultStorageLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialFreightGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "MaterialFreightGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkingGroupForAvailabilityCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CheckingGroupForAvailabilityCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commodityCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CommodityCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quotaArrangementUsage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "QuotaArrangementUsage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("negativeStocksAllowed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "NegativeStocksAllowed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
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
