/**
 * GPDB_ProductGetDetails.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetails  implements java.io.Serializable {
    /* Enter materialnumber (18 digits) to retrieve details. */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnr productMatnr;

    public GPDB_ProductGetDetails() {
    }

    public GPDB_ProductGetDetails(
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnr productMatnr) {
           this.productMatnr = productMatnr;
    }


    /**
     * Gets the productMatnr value for this GPDB_ProductGetDetails.
     * 
     * @return productMatnr   * Enter materialnumber (18 digits) to retrieve details.
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnr getProductMatnr() {
        return productMatnr;
    }


    /**
     * Sets the productMatnr value for this GPDB_ProductGetDetails.
     * 
     * @param productMatnr   * Enter materialnumber (18 digits) to retrieve details.
     */
    public void setProductMatnr(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnr productMatnr) {
        this.productMatnr = productMatnr;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetails)) return false;
        GPDB_ProductGetDetails other = (GPDB_ProductGetDetails) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.productMatnr==null && other.getProductMatnr()==null) || 
             (this.productMatnr!=null &&
              this.productMatnr.equals(other.getProductMatnr())));
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
        if (getProductMatnr() != null) {
            _hashCode += getProductMatnr().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetails.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">GPDB_ProductGetDetails"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productMatnr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ProductMatnr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>GPDB_ProductGetDetails>ProductMatnr"));
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
