/**
 * GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute  implements java.io.Serializable {
    private java.lang.String WFAttributeType;  // attribute

    private java.lang.String WFAttributeValueId;  // attribute

    public GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute() {
    }

    public GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute(
           java.lang.String WFAttributeType,
           java.lang.String WFAttributeValueId) {
           this.WFAttributeType = WFAttributeType;
           this.WFAttributeValueId = WFAttributeValueId;
    }


    /**
     * Gets the WFAttributeType value for this GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute.
     * 
     * @return WFAttributeType
     */
    public java.lang.String getWFAttributeType() {
        return WFAttributeType;
    }


    /**
     * Sets the WFAttributeType value for this GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute.
     * 
     * @param WFAttributeType
     */
    public void setWFAttributeType(java.lang.String WFAttributeType) {
        this.WFAttributeType = WFAttributeType;
    }


    /**
     * Gets the WFAttributeValueId value for this GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute.
     * 
     * @return WFAttributeValueId
     */
    public java.lang.String getWFAttributeValueId() {
        return WFAttributeValueId;
    }


    /**
     * Sets the WFAttributeValueId value for this GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute.
     * 
     * @param WFAttributeValueId
     */
    public void setWFAttributeValueId(java.lang.String WFAttributeValueId) {
        this.WFAttributeValueId = WFAttributeValueId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute)) return false;
        GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute other = (GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.WFAttributeType==null && other.getWFAttributeType()==null) || 
             (this.WFAttributeType!=null &&
              this.WFAttributeType.equals(other.getWFAttributeType()))) &&
            ((this.WFAttributeValueId==null && other.getWFAttributeValueId()==null) || 
             (this.WFAttributeValueId!=null &&
              this.WFAttributeValueId.equals(other.getWFAttributeValueId())));
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
        if (getWFAttributeType() != null) {
            _hashCode += getWFAttributeType().hashCode();
        }
        if (getWFAttributeValueId() != null) {
            _hashCode += getWFAttributeValueId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>WFAttributes>WFAttribute"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("WFAttributeType");
        attrField.setXmlName(new javax.xml.namespace.QName("", "WFAttributeType"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>WFAttributes>WFAttribute>WFAttributeType"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("WFAttributeValueId");
        attrField.setXmlName(new javax.xml.namespace.QName("", "WFAttributeValueId"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>WFAttributes>WFAttribute>WFAttributeValueId"));
        typeDesc.addFieldDesc(attrField);
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
