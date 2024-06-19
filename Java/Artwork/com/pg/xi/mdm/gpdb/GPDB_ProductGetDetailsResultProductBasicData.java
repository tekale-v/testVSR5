/**
 * GPDB_ProductGetDetailsResultProductBasicData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductBasicData  implements java.io.Serializable {
    /* MARA-MTART */
    private java.lang.String materialType;

    /* MARA-BISMT */
    private java.lang.String oldMatnr;

    /* MARA-MEINS */
    private java.lang.String baseUnitofMeasure;

    /* MARA-NTGEW */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightMetric netWeightMetric;

    /* ZTXXPTCMRA-ZNTGEW */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightImperial netWeightImperial;

    /* MARA-TRAGR */
    private java.lang.String transportationGroup;

    /* MARA-BEGRU */
    private java.lang.String authorizationGroup;

    /* MARA-PROFL */
    private java.lang.String dangerousGoodsIndicatorProfile;

    /* MARA-MTPOS_MARA */
    private java.lang.String generalItemCategoryGroup;

    private java.math.BigDecimal statisticalUnit;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicData NABasicData;

    public GPDB_ProductGetDetailsResultProductBasicData() {
    }

    public GPDB_ProductGetDetailsResultProductBasicData(
           java.lang.String materialType,
           java.lang.String oldMatnr,
           java.lang.String baseUnitofMeasure,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightMetric netWeightMetric,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightImperial netWeightImperial,
           java.lang.String transportationGroup,
           java.lang.String authorizationGroup,
           java.lang.String dangerousGoodsIndicatorProfile,
           java.lang.String generalItemCategoryGroup,
           java.math.BigDecimal statisticalUnit,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicData NABasicData) {
           this.materialType = materialType;
           this.oldMatnr = oldMatnr;
           this.baseUnitofMeasure = baseUnitofMeasure;
           this.netWeightMetric = netWeightMetric;
           this.netWeightImperial = netWeightImperial;
           this.transportationGroup = transportationGroup;
           this.authorizationGroup = authorizationGroup;
           this.dangerousGoodsIndicatorProfile = dangerousGoodsIndicatorProfile;
           this.generalItemCategoryGroup = generalItemCategoryGroup;
           this.statisticalUnit = statisticalUnit;
           this.NABasicData = NABasicData;
    }


    /**
     * Gets the materialType value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return materialType   * MARA-MTART
     */
    public java.lang.String getMaterialType() {
        return materialType;
    }


    /**
     * Sets the materialType value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param materialType   * MARA-MTART
     */
    public void setMaterialType(java.lang.String materialType) {
        this.materialType = materialType;
    }


    /**
     * Gets the oldMatnr value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return oldMatnr   * MARA-BISMT
     */
    public java.lang.String getOldMatnr() {
        return oldMatnr;
    }


    /**
     * Sets the oldMatnr value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param oldMatnr   * MARA-BISMT
     */
    public void setOldMatnr(java.lang.String oldMatnr) {
        this.oldMatnr = oldMatnr;
    }


    /**
     * Gets the baseUnitofMeasure value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return baseUnitofMeasure   * MARA-MEINS
     */
    public java.lang.String getBaseUnitofMeasure() {
        return baseUnitofMeasure;
    }


    /**
     * Sets the baseUnitofMeasure value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param baseUnitofMeasure   * MARA-MEINS
     */
    public void setBaseUnitofMeasure(java.lang.String baseUnitofMeasure) {
        this.baseUnitofMeasure = baseUnitofMeasure;
    }


    /**
     * Gets the netWeightMetric value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return netWeightMetric   * MARA-NTGEW
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightMetric getNetWeightMetric() {
        return netWeightMetric;
    }


    /**
     * Sets the netWeightMetric value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param netWeightMetric   * MARA-NTGEW
     */
    public void setNetWeightMetric(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightMetric netWeightMetric) {
        this.netWeightMetric = netWeightMetric;
    }


    /**
     * Gets the netWeightImperial value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return netWeightImperial   * ZTXXPTCMRA-ZNTGEW
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightImperial getNetWeightImperial() {
        return netWeightImperial;
    }


    /**
     * Sets the netWeightImperial value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param netWeightImperial   * ZTXXPTCMRA-ZNTGEW
     */
    public void setNetWeightImperial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNetWeightImperial netWeightImperial) {
        this.netWeightImperial = netWeightImperial;
    }


    /**
     * Gets the transportationGroup value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return transportationGroup   * MARA-TRAGR
     */
    public java.lang.String getTransportationGroup() {
        return transportationGroup;
    }


    /**
     * Sets the transportationGroup value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param transportationGroup   * MARA-TRAGR
     */
    public void setTransportationGroup(java.lang.String transportationGroup) {
        this.transportationGroup = transportationGroup;
    }


    /**
     * Gets the authorizationGroup value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return authorizationGroup   * MARA-BEGRU
     */
    public java.lang.String getAuthorizationGroup() {
        return authorizationGroup;
    }


    /**
     * Sets the authorizationGroup value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param authorizationGroup   * MARA-BEGRU
     */
    public void setAuthorizationGroup(java.lang.String authorizationGroup) {
        this.authorizationGroup = authorizationGroup;
    }


    /**
     * Gets the dangerousGoodsIndicatorProfile value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return dangerousGoodsIndicatorProfile   * MARA-PROFL
     */
    public java.lang.String getDangerousGoodsIndicatorProfile() {
        return dangerousGoodsIndicatorProfile;
    }


    /**
     * Sets the dangerousGoodsIndicatorProfile value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param dangerousGoodsIndicatorProfile   * MARA-PROFL
     */
    public void setDangerousGoodsIndicatorProfile(java.lang.String dangerousGoodsIndicatorProfile) {
        this.dangerousGoodsIndicatorProfile = dangerousGoodsIndicatorProfile;
    }


    /**
     * Gets the generalItemCategoryGroup value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return generalItemCategoryGroup   * MARA-MTPOS_MARA
     */
    public java.lang.String getGeneralItemCategoryGroup() {
        return generalItemCategoryGroup;
    }


    /**
     * Sets the generalItemCategoryGroup value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param generalItemCategoryGroup   * MARA-MTPOS_MARA
     */
    public void setGeneralItemCategoryGroup(java.lang.String generalItemCategoryGroup) {
        this.generalItemCategoryGroup = generalItemCategoryGroup;
    }


    /**
     * Gets the statisticalUnit value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return statisticalUnit
     */
    public java.math.BigDecimal getStatisticalUnit() {
        return statisticalUnit;
    }


    /**
     * Sets the statisticalUnit value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param statisticalUnit
     */
    public void setStatisticalUnit(java.math.BigDecimal statisticalUnit) {
        this.statisticalUnit = statisticalUnit;
    }


    /**
     * Gets the NABasicData value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @return NABasicData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicData getNABasicData() {
        return NABasicData;
    }


    /**
     * Sets the NABasicData value for this GPDB_ProductGetDetailsResultProductBasicData.
     * 
     * @param NABasicData
     */
    public void setNABasicData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicDataNABasicData NABasicData) {
        this.NABasicData = NABasicData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductBasicData)) return false;
        GPDB_ProductGetDetailsResultProductBasicData other = (GPDB_ProductGetDetailsResultProductBasicData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.materialType==null && other.getMaterialType()==null) || 
             (this.materialType!=null &&
              this.materialType.equals(other.getMaterialType()))) &&
            ((this.oldMatnr==null && other.getOldMatnr()==null) || 
             (this.oldMatnr!=null &&
              this.oldMatnr.equals(other.getOldMatnr()))) &&
            ((this.baseUnitofMeasure==null && other.getBaseUnitofMeasure()==null) || 
             (this.baseUnitofMeasure!=null &&
              this.baseUnitofMeasure.equals(other.getBaseUnitofMeasure()))) &&
            ((this.netWeightMetric==null && other.getNetWeightMetric()==null) || 
             (this.netWeightMetric!=null &&
              this.netWeightMetric.equals(other.getNetWeightMetric()))) &&
            ((this.netWeightImperial==null && other.getNetWeightImperial()==null) || 
             (this.netWeightImperial!=null &&
              this.netWeightImperial.equals(other.getNetWeightImperial()))) &&
            ((this.transportationGroup==null && other.getTransportationGroup()==null) || 
             (this.transportationGroup!=null &&
              this.transportationGroup.equals(other.getTransportationGroup()))) &&
            ((this.authorizationGroup==null && other.getAuthorizationGroup()==null) || 
             (this.authorizationGroup!=null &&
              this.authorizationGroup.equals(other.getAuthorizationGroup()))) &&
            ((this.dangerousGoodsIndicatorProfile==null && other.getDangerousGoodsIndicatorProfile()==null) || 
             (this.dangerousGoodsIndicatorProfile!=null &&
              this.dangerousGoodsIndicatorProfile.equals(other.getDangerousGoodsIndicatorProfile()))) &&
            ((this.generalItemCategoryGroup==null && other.getGeneralItemCategoryGroup()==null) || 
             (this.generalItemCategoryGroup!=null &&
              this.generalItemCategoryGroup.equals(other.getGeneralItemCategoryGroup()))) &&
            ((this.statisticalUnit==null && other.getStatisticalUnit()==null) || 
             (this.statisticalUnit!=null &&
              this.statisticalUnit.equals(other.getStatisticalUnit()))) &&
            ((this.NABasicData==null && other.getNABasicData()==null) || 
             (this.NABasicData!=null &&
              this.NABasicData.equals(other.getNABasicData())));
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
        if (getMaterialType() != null) {
            _hashCode += getMaterialType().hashCode();
        }
        if (getOldMatnr() != null) {
            _hashCode += getOldMatnr().hashCode();
        }
        if (getBaseUnitofMeasure() != null) {
            _hashCode += getBaseUnitofMeasure().hashCode();
        }
        if (getNetWeightMetric() != null) {
            _hashCode += getNetWeightMetric().hashCode();
        }
        if (getNetWeightImperial() != null) {
            _hashCode += getNetWeightImperial().hashCode();
        }
        if (getTransportationGroup() != null) {
            _hashCode += getTransportationGroup().hashCode();
        }
        if (getAuthorizationGroup() != null) {
            _hashCode += getAuthorizationGroup().hashCode();
        }
        if (getDangerousGoodsIndicatorProfile() != null) {
            _hashCode += getDangerousGoodsIndicatorProfile().hashCode();
        }
        if (getGeneralItemCategoryGroup() != null) {
            _hashCode += getGeneralItemCategoryGroup().hashCode();
        }
        if (getStatisticalUnit() != null) {
            _hashCode += getStatisticalUnit().hashCode();
        }
        if (getNABasicData() != null) {
            _hashCode += getNABasicData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductBasicData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetailsResult>Product>BasicData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "MaterialType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldMatnr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "OldMatnr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("baseUnitofMeasure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "BaseUnitofMeasure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("netWeightMetric");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "NetWeightMetric"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>BasicData>NetWeightMetric"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("netWeightImperial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "NetWeightImperial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>BasicData>NetWeightImperial"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transportationGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "TransportationGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "AuthorizationGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dangerousGoodsIndicatorProfile");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DangerousGoodsIndicatorProfile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("generalItemCategoryGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "GeneralItemCategoryGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("statisticalUnit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "StatisticalUnit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NABasicData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "NABasicData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>BasicData>NABasicData"));
        elemField.setMinOccurs(0);
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
