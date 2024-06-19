/**
 * GPDB_ProductGetDetailsResultProductWFAttributes.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductWFAttributes  implements java.io.Serializable {
    private java.lang.String[] WFAttributeTemplate;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute[] WFAttribute;

    private java.lang.String WFInitiativeID;  // attribute

    public GPDB_ProductGetDetailsResultProductWFAttributes() {
    }

    public GPDB_ProductGetDetailsResultProductWFAttributes(
           java.lang.String[] WFAttributeTemplate,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute[] WFAttribute,
           java.lang.String WFInitiativeID) {
           this.WFAttributeTemplate = WFAttributeTemplate;
           this.WFAttribute = WFAttribute;
           this.WFInitiativeID = WFInitiativeID;
    }


    /**
     * Gets the WFAttributeTemplate value for this GPDB_ProductGetDetailsResultProductWFAttributes.
     * 
     * @return WFAttributeTemplate
     */
    public java.lang.String[] getWFAttributeTemplate() {
        return WFAttributeTemplate;
    }


    /**
     * Sets the WFAttributeTemplate value for this GPDB_ProductGetDetailsResultProductWFAttributes.
     * 
     * @param WFAttributeTemplate
     */
    public void setWFAttributeTemplate(java.lang.String[] WFAttributeTemplate) {
        this.WFAttributeTemplate = WFAttributeTemplate;
    }

    public java.lang.String getWFAttributeTemplate(int i) {
        return this.WFAttributeTemplate[i];
    }

    public void setWFAttributeTemplate(int i, java.lang.String _value) {
        this.WFAttributeTemplate[i] = _value;
    }


    /**
     * Gets the WFAttribute value for this GPDB_ProductGetDetailsResultProductWFAttributes.
     * 
     * @return WFAttribute
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute[] getWFAttribute() {
        return WFAttribute;
    }


    /**
     * Sets the WFAttribute value for this GPDB_ProductGetDetailsResultProductWFAttributes.
     * 
     * @param WFAttribute
     */
    public void setWFAttribute(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute[] WFAttribute) {
        this.WFAttribute = WFAttribute;
    }

    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute getWFAttribute(int i) {
        return this.WFAttribute[i];
    }

    public void setWFAttribute(int i, com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributesWFAttribute _value) {
        this.WFAttribute[i] = _value;
    }


    /**
     * Gets the WFInitiativeID value for this GPDB_ProductGetDetailsResultProductWFAttributes.
     * 
     * @return WFInitiativeID
     */
    public java.lang.String getWFInitiativeID() {
        return WFInitiativeID;
    }


    /**
     * Sets the WFInitiativeID value for this GPDB_ProductGetDetailsResultProductWFAttributes.
     * 
     * @param WFInitiativeID
     */
    public void setWFInitiativeID(java.lang.String WFInitiativeID) {
        this.WFInitiativeID = WFInitiativeID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductWFAttributes)) return false;
        GPDB_ProductGetDetailsResultProductWFAttributes other = (GPDB_ProductGetDetailsResultProductWFAttributes) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.WFAttributeTemplate==null && other.getWFAttributeTemplate()==null) || 
             (this.WFAttributeTemplate!=null &&
              java.util.Arrays.equals(this.WFAttributeTemplate, other.getWFAttributeTemplate()))) &&
            ((this.WFAttribute==null && other.getWFAttribute()==null) || 
             (this.WFAttribute!=null &&
              java.util.Arrays.equals(this.WFAttribute, other.getWFAttribute()))) &&
            ((this.WFInitiativeID==null && other.getWFInitiativeID()==null) || 
             (this.WFInitiativeID!=null &&
              this.WFInitiativeID.equals(other.getWFInitiativeID())));
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
        if (getWFAttributeTemplate() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWFAttributeTemplate());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWFAttributeTemplate(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getWFAttribute() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWFAttribute());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWFAttribute(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getWFInitiativeID() != null) {
            _hashCode += getWFInitiativeID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductWFAttributes.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetailsResult>Product>WFAttributes"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("WFInitiativeID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "WFInitiativeID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>WFAttributes>WFInitiativeID"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("WFAttributeTemplate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "WFAttributeTemplate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>WFAttributes>WFAttributeTemplate"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("WFAttribute");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "WFAttribute"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>WFAttributes>WFAttribute"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
