/**
 * GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent  implements java.io.Serializable {
    /* STPO-IDNRK */
    private java.lang.String matnr;

    /* MAKT */
    private java.lang.String description;

    /* ZTXXPTSTPO-ZMEIN */
    private java.lang.String uoM;

    /* Calculated based on STKO, STPO, ZTXXPTSTPO and MARM */
    private long quantity;

    /* ZTXXPTSTPO-ZNSCP */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponentExcludeFromBOMSUCalc excludeFromBOMSUCalc;

    /* ZTXXPT0104 */
    private java.lang.String CSU_IND;

    /* Calculated based on component SU, quantity, material type,
     * CSU_IND attribute and exclude from BOM SU calculation flag */
    private java.math.BigDecimal SUValue;

    public GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent() {
    }

    public GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent(
           java.lang.String matnr,
           java.lang.String description,
           java.lang.String uoM,
           long quantity,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponentExcludeFromBOMSUCalc excludeFromBOMSUCalc,
           java.lang.String CSU_IND,
           java.math.BigDecimal SUValue) {
           this.matnr = matnr;
           this.description = description;
           this.uoM = uoM;
           this.quantity = quantity;
           this.excludeFromBOMSUCalc = excludeFromBOMSUCalc;
           this.CSU_IND = CSU_IND;
           this.SUValue = SUValue;
    }


    /**
     * Gets the matnr value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @return matnr   * STPO-IDNRK
     */
    public java.lang.String getMatnr() {
        return matnr;
    }


    /**
     * Sets the matnr value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @param matnr   * STPO-IDNRK
     */
    public void setMatnr(java.lang.String matnr) {
        this.matnr = matnr;
    }


    /**
     * Gets the description value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @return description   * MAKT
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @param description   * MAKT
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the uoM value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @return uoM   * ZTXXPTSTPO-ZMEIN
     */
    public java.lang.String getUoM() {
        return uoM;
    }


    /**
     * Sets the uoM value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @param uoM   * ZTXXPTSTPO-ZMEIN
     */
    public void setUoM(java.lang.String uoM) {
        this.uoM = uoM;
    }


    /**
     * Gets the quantity value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @return quantity   * Calculated based on STKO, STPO, ZTXXPTSTPO and MARM
     */
    public long getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @param quantity   * Calculated based on STKO, STPO, ZTXXPTSTPO and MARM
     */
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the excludeFromBOMSUCalc value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @return excludeFromBOMSUCalc   * ZTXXPTSTPO-ZNSCP
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponentExcludeFromBOMSUCalc getExcludeFromBOMSUCalc() {
        return excludeFromBOMSUCalc;
    }


    /**
     * Sets the excludeFromBOMSUCalc value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @param excludeFromBOMSUCalc   * ZTXXPTSTPO-ZNSCP
     */
    public void setExcludeFromBOMSUCalc(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponentExcludeFromBOMSUCalc excludeFromBOMSUCalc) {
        this.excludeFromBOMSUCalc = excludeFromBOMSUCalc;
    }


    /**
     * Gets the CSU_IND value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @return CSU_IND   * ZTXXPT0104
     */
    public java.lang.String getCSU_IND() {
        return CSU_IND;
    }


    /**
     * Sets the CSU_IND value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @param CSU_IND   * ZTXXPT0104
     */
    public void setCSU_IND(java.lang.String CSU_IND) {
        this.CSU_IND = CSU_IND;
    }


    /**
     * Gets the SUValue value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @return SUValue   * Calculated based on component SU, quantity, material type,
     * CSU_IND attribute and exclude from BOM SU calculation flag
     */
    public java.math.BigDecimal getSUValue() {
        return SUValue;
    }


    /**
     * Sets the SUValue value for this GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.
     * 
     * @param SUValue   * Calculated based on component SU, quantity, material type,
     * CSU_IND attribute and exclude from BOM SU calculation flag
     */
    public void setSUValue(java.math.BigDecimal SUValue) {
        this.SUValue = SUValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent)) return false;
        GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent other = (GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.matnr==null && other.getMatnr()==null) || 
             (this.matnr!=null &&
              this.matnr.equals(other.getMatnr()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.uoM==null && other.getUoM()==null) || 
             (this.uoM!=null &&
              this.uoM.equals(other.getUoM()))) &&
            this.quantity == other.getQuantity() &&
            ((this.excludeFromBOMSUCalc==null && other.getExcludeFromBOMSUCalc()==null) || 
             (this.excludeFromBOMSUCalc!=null &&
              this.excludeFromBOMSUCalc.equals(other.getExcludeFromBOMSUCalc()))) &&
            ((this.CSU_IND==null && other.getCSU_IND()==null) || 
             (this.CSU_IND!=null &&
              this.CSU_IND.equals(other.getCSU_IND()))) &&
            ((this.SUValue==null && other.getSUValue()==null) || 
             (this.SUValue!=null &&
              this.SUValue.equals(other.getSUValue())));
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
        if (getMatnr() != null) {
            _hashCode += getMatnr().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getUoM() != null) {
            _hashCode += getUoM().hashCode();
        }
        _hashCode += new Long(getQuantity()).hashCode();
        if (getExcludeFromBOMSUCalc() != null) {
            _hashCode += getExcludeFromBOMSUCalc().hashCode();
        }
        if (getCSU_IND() != null) {
            _hashCode += getCSU_IND().hashCode();
        }
        if (getSUValue() != null) {
            _hashCode += getSUValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>BillOfMaterial>DirectBillOfMaterial>Component"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("matnr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Matnr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("uoM");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "UoM"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("excludeFromBOMSUCalc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ExcludeFromBOMSUCalc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>>GPDB_ProductGetDetailsResult>Product>BillOfMaterial>DirectBillOfMaterial>Component>ExcludeFromBOMSUCalc"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CSU_IND");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CSU_IND"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SUValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "SUValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
