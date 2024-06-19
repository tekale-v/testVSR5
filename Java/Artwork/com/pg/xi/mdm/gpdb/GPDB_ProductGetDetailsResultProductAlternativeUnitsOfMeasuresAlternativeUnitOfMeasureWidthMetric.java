/**
 * GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric  implements java.io.Serializable, org.apache.axis.encoding.SimpleType {
    private java.math.BigDecimal _value;

    private java.lang.String uoM;  // attribute

    public GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric() {
    }

    // Simple Types must have a String constructor
    public GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric(java.math.BigDecimal _value) {
        this._value = _value;
    }
    public GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric(java.lang.String _value) {
        this._value = new java.math.BigDecimal(_value);
    }

    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return _value == null ? null : _value.toString();
    }


    /**
     * Gets the _value value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric.
     * 
     * @return _value
     */
    public java.math.BigDecimal get_value() {
        return _value;
    }


    /**
     * Sets the _value value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric.
     * 
     * @param _value
     */
    public void set_value(java.math.BigDecimal _value) {
        this._value = _value;
    }


    /**
     * Gets the uoM value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric.
     * 
     * @return uoM
     */
    public java.lang.String getUoM() {
        return uoM;
    }


    /**
     * Sets the uoM value for this GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric.
     * 
     * @param uoM
     */
    public void setUoM(java.lang.String uoM) {
        this.uoM = uoM;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric)) return false;
        GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric other = (GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this._value==null && other.get_value()==null) || 
             (this._value!=null &&
              this._value.equals(other.get_value()))) &&
            ((this.uoM==null && other.getUoM()==null) || 
             (this.uoM!=null &&
              this.uoM.equals(other.getUoM())));
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
        if (get_value() != null) {
            _hashCode += get_value().hashCode();
        }
        if (getUoM() != null) {
            _hashCode += getUoM().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasureWidthMetric.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>WidthMetric"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("uoM");
        attrField.setXmlName(new javax.xml.namespace.QName("", "UoM"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure>WidthMetric>UoM"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
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
          new  org.apache.axis.encoding.ser.SimpleSerializer(
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
          new  org.apache.axis.encoding.ser.SimpleDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
