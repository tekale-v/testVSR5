/**
 * GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Y = "Y";
    public static final java.lang.String _N = "N";
    public static final GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes Y = new GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes(_Y);
    public static final GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes N = new GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes(_N);
    public java.lang.String getValue() { return _value_;}
    public static GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes enumeration = (GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeDerivedAttributes"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
