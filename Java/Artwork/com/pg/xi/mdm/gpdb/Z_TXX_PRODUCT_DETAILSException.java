/**
 * Z_TXX_PRODUCT_DETAILSException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class Z_TXX_PRODUCT_DETAILSException  extends org.apache.axis.AxisFault  implements java.io.Serializable {
    private com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionName name;

    private java.lang.String text;

    private com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionMessage message1;

    private com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionAttributes attributes;

    public Z_TXX_PRODUCT_DETAILSException() {
    }

    public Z_TXX_PRODUCT_DETAILSException(
           com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionName name,
           java.lang.String text,
           com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionMessage message1,
           com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionAttributes attributes) {
        this.name = name;
        this.text = text;
        this.message1 = message1;
        this.attributes = attributes;
    }


    /**
     * Gets the name value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @return name
     */
    public com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionName getName() {
        return name;
    }


    /**
     * Sets the name value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @param name
     */
    public void setName(com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionName name) {
        this.name = name;
    }


    /**
     * Gets the text value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @return text
     */
    public java.lang.String getText() {
        return text;
    }


    /**
     * Sets the text value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @param text
     */
    public void setText(java.lang.String text) {
        this.text = text;
    }


    /**
     * Gets the message1 value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @return message1
     */
    public com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionMessage getMessage1() {
        return message1;
    }


    /**
     * Sets the message1 value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @param message1
     */
    public void setMessage1(com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionMessage message1) {
        this.message1 = message1;
    }


    /**
     * Gets the attributes value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @return attributes
     */
    public com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionAttributes getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this Z_TXX_PRODUCT_DETAILSException.
     * 
     * @param attributes
     */
    public void setAttributes(com.pg.xi.mdm.gpdb.Z_TXX_PRODUCT_DETAILSExceptionAttributes attributes) {
        this.attributes = attributes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Z_TXX_PRODUCT_DETAILSException)) return false;
        Z_TXX_PRODUCT_DETAILSException other = (Z_TXX_PRODUCT_DETAILSException) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.text==null && other.getText()==null) || 
             (this.text!=null &&
              this.text.equals(other.getText()))) &&
            ((this.message1==null && other.getMessage1()==null) || 
             (this.message1!=null &&
              this.message1.equals(other.getMessage1()))) &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              this.attributes.equals(other.getAttributes())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getText() != null) {
            _hashCode += getText().hashCode();
        }
        if (getMessage1() != null) {
            _hashCode += getMessage1().hashCode();
        }
        if (getAttributes() != null) {
            _hashCode += getAttributes().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Z_TXX_PRODUCT_DETAILSException.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">Z_TXX_PRODUCT_DETAILS.Exception"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>Z_TXX_PRODUCT_DETAILS.Exception>Name"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("text");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Text"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>Z_TXX_PRODUCT_DETAILS.Exception>Message"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>Z_TXX_PRODUCT_DETAILS.Exception>Attributes"));
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


    /**
     * Writes the exception data to the faultDetails
     */
    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {
        context.serialize(qname, null, this);
    }
}
