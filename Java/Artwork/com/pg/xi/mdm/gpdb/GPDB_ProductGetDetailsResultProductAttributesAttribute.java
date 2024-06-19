/**
 * GPDB_ProductGetDetailsResultProductAttributesAttribute.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductAttributesAttribute  implements java.io.Serializable {
    private java.lang.String attributeType;  // attribute

    private java.lang.String attributeTypeDescription;  // attribute

    private java.lang.String attributeValueId;  // attribute

    private java.lang.String attributeValueDescription;  // attribute

    public GPDB_ProductGetDetailsResultProductAttributesAttribute() {
    }

    public GPDB_ProductGetDetailsResultProductAttributesAttribute(
           java.lang.String attributeType,
           java.lang.String attributeTypeDescription,
           java.lang.String attributeValueId,
           java.lang.String attributeValueDescription) {
           this.attributeType = attributeType;
           this.attributeTypeDescription = attributeTypeDescription;
           this.attributeValueId = attributeValueId;
           this.attributeValueDescription = attributeValueDescription;
    }


    /**
     * Gets the attributeType value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @return attributeType
     */
    public java.lang.String getAttributeType() {
        return attributeType;
    }


    /**
     * Sets the attributeType value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @param attributeType
     */
    public void setAttributeType(java.lang.String attributeType) {
        this.attributeType = attributeType;
    }


    /**
     * Gets the attributeTypeDescription value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @return attributeTypeDescription
     */
    public java.lang.String getAttributeTypeDescription() {
        return attributeTypeDescription;
    }


    /**
     * Sets the attributeTypeDescription value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @param attributeTypeDescription
     */
    public void setAttributeTypeDescription(java.lang.String attributeTypeDescription) {
        this.attributeTypeDescription = attributeTypeDescription;
    }


    /**
     * Gets the attributeValueId value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @return attributeValueId
     */
    public java.lang.String getAttributeValueId() {
        return attributeValueId;
    }


    /**
     * Sets the attributeValueId value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @param attributeValueId
     */
    public void setAttributeValueId(java.lang.String attributeValueId) {
        this.attributeValueId = attributeValueId;
    }


    /**
     * Gets the attributeValueDescription value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @return attributeValueDescription
     */
    public java.lang.String getAttributeValueDescription() {
        return attributeValueDescription;
    }


    /**
     * Sets the attributeValueDescription value for this GPDB_ProductGetDetailsResultProductAttributesAttribute.
     * 
     * @param attributeValueDescription
     */
    public void setAttributeValueDescription(java.lang.String attributeValueDescription) {
        this.attributeValueDescription = attributeValueDescription;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductAttributesAttribute)) return false;
        GPDB_ProductGetDetailsResultProductAttributesAttribute other = (GPDB_ProductGetDetailsResultProductAttributesAttribute) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attributeType==null && other.getAttributeType()==null) || 
             (this.attributeType!=null &&
              this.attributeType.equals(other.getAttributeType()))) &&
            ((this.attributeTypeDescription==null && other.getAttributeTypeDescription()==null) || 
             (this.attributeTypeDescription!=null &&
              this.attributeTypeDescription.equals(other.getAttributeTypeDescription()))) &&
            ((this.attributeValueId==null && other.getAttributeValueId()==null) || 
             (this.attributeValueId!=null &&
              this.attributeValueId.equals(other.getAttributeValueId()))) &&
            ((this.attributeValueDescription==null && other.getAttributeValueDescription()==null) || 
             (this.attributeValueDescription!=null &&
              this.attributeValueDescription.equals(other.getAttributeValueDescription())));
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
        if (getAttributeType() != null) {
            _hashCode += getAttributeType().hashCode();
        }
        if (getAttributeTypeDescription() != null) {
            _hashCode += getAttributeTypeDescription().hashCode();
        }
        if (getAttributeValueId() != null) {
            _hashCode += getAttributeValueId().hashCode();
        }
        if (getAttributeValueDescription() != null) {
            _hashCode += getAttributeValueDescription().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductAttributesAttribute.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>Attributes>Attribute"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("attributeType");
        attrField.setXmlName(new javax.xml.namespace.QName("", "AttributeType"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>Attributes>Attribute>AttributeType"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("attributeTypeDescription");
        attrField.setXmlName(new javax.xml.namespace.QName("", "AttributeTypeDescription"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>Attributes>Attribute>AttributeTypeDescription"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("attributeValueId");
        attrField.setXmlName(new javax.xml.namespace.QName("", "AttributeValueId"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("attributeValueDescription");
        attrField.setXmlName(new javax.xml.namespace.QName("", "attributeValueDescription"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>Attributes>Attribute>attributeValueDescription"));
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
