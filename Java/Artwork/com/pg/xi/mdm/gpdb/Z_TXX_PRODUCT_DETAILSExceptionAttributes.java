/**
 * Z_TXX_PRODUCT_DETAILSExceptionAttributes.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class Z_TXX_PRODUCT_DETAILSExceptionAttributes  implements java.io.Serializable {
    private java.lang.String v1;

    private java.lang.String v2;

    private java.lang.String v3;

    private java.lang.String v4;

    public Z_TXX_PRODUCT_DETAILSExceptionAttributes() {
    }

    public Z_TXX_PRODUCT_DETAILSExceptionAttributes(
           java.lang.String v1,
           java.lang.String v2,
           java.lang.String v3,
           java.lang.String v4) {
           this.v1 = v1;
           this.v2 = v2;
           this.v3 = v3;
           this.v4 = v4;
    }


    /**
     * Gets the v1 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @return v1
     */
    public java.lang.String getV1() {
        return v1;
    }


    /**
     * Sets the v1 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @param v1
     */
    public void setV1(java.lang.String v1) {
        this.v1 = v1;
    }


    /**
     * Gets the v2 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @return v2
     */
    public java.lang.String getV2() {
        return v2;
    }


    /**
     * Sets the v2 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @param v2
     */
    public void setV2(java.lang.String v2) {
        this.v2 = v2;
    }


    /**
     * Gets the v3 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @return v3
     */
    public java.lang.String getV3() {
        return v3;
    }


    /**
     * Sets the v3 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @param v3
     */
    public void setV3(java.lang.String v3) {
        this.v3 = v3;
    }


    /**
     * Gets the v4 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @return v4
     */
    public java.lang.String getV4() {
        return v4;
    }


    /**
     * Sets the v4 value for this Z_TXX_PRODUCT_DETAILSExceptionAttributes.
     * 
     * @param v4
     */
    public void setV4(java.lang.String v4) {
        this.v4 = v4;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Z_TXX_PRODUCT_DETAILSExceptionAttributes)) return false;
        Z_TXX_PRODUCT_DETAILSExceptionAttributes other = (Z_TXX_PRODUCT_DETAILSExceptionAttributes) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.v1==null && other.getV1()==null) || 
             (this.v1!=null &&
              this.v1.equals(other.getV1()))) &&
            ((this.v2==null && other.getV2()==null) || 
             (this.v2!=null &&
              this.v2.equals(other.getV2()))) &&
            ((this.v3==null && other.getV3()==null) || 
             (this.v3!=null &&
              this.v3.equals(other.getV3()))) &&
            ((this.v4==null && other.getV4()==null) || 
             (this.v4!=null &&
              this.v4.equals(other.getV4())));
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
        if (getV1() != null) {
            _hashCode += getV1().hashCode();
        }
        if (getV2() != null) {
            _hashCode += getV2().hashCode();
        }
        if (getV3() != null) {
            _hashCode += getV3().hashCode();
        }
        if (getV4() != null) {
            _hashCode += getV4().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Z_TXX_PRODUCT_DETAILSExceptionAttributes.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>Z_TXX_PRODUCT_DETAILS.Exception>Attributes"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("v1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "V1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("v2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "V2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("v3");
        elemField.setXmlName(new javax.xml.namespace.QName("", "V3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("v4");
        elemField.setXmlName(new javax.xml.namespace.QName("", "V4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
