/**
 * GPDB_ProductGetDetailsResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResult  implements java.io.Serializable {
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProduct product;

    public GPDB_ProductGetDetailsResult() {
    }

    public GPDB_ProductGetDetailsResult(
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProduct product) {
           this.product = product;
    }


    /**
     * Gets the product value for this GPDB_ProductGetDetailsResult.
     * 
     * @return product
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProduct getProduct() {
        return product;
    }


    /**
     * Sets the product value for this GPDB_ProductGetDetailsResult.
     * 
     * @param product
     */
    public void setProduct(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProduct product) {
        this.product = product;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResult)) return false;
        GPDB_ProductGetDetailsResult other = (GPDB_ProductGetDetailsResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.product==null && other.getProduct()==null) || 
             (this.product!=null &&
              this.product.equals(other.getProduct())));
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
        if (getProduct() != null) {
            _hashCode += getProduct().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">GPDB_ProductGetDetailsResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("product");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Product"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>GPDB_ProductGetDetailsResult>Product"));
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
