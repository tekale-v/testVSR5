/**
 * GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization implements java.io.Serializable {
    private java.math.BigInteger _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization(java.math.BigInteger value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.math.BigInteger _value1 = new java.math.BigInteger("1");
    public static final java.math.BigInteger _value2 = new java.math.BigInteger("2");
    public static final java.math.BigInteger _value3 = new java.math.BigInteger("3");
    public static final GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization value1 = new GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization(_value1);
    public static final GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization value2 = new GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization(_value2);
    public static final GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization value3 = new GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization(_value3);
    public java.math.BigInteger getValue() { return _value_;}
    public static GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization fromValue(java.math.BigInteger value)
          throws java.lang.IllegalArgumentException {
        GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization enumeration = (GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        try {
            return fromValue(new java.math.BigInteger(value));
        } catch (Exception e) {
            throw new java.lang.IllegalArgumentException();
        }
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_.toString();}
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
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntryLaunchAuthorization.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>LifeCycleData>LifeCycleEntry>LaunchAuthorization"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
