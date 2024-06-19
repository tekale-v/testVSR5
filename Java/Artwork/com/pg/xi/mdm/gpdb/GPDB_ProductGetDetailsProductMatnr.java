/**
 * GPDB_ProductGetDetailsProductMatnr.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsProductMatnr  implements java.io.Serializable, org.apache.axis.encoding.SimpleType, org.apache.axis.encoding.MixedContentType {
    private java.lang.String _value;

    private org.apache.axis.message.MessageElement [] _any;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeBasicData includeBasicData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeUoMData includeUoMData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeSalesData includeSalesData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeAttributeData includeAttributeData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes includeDerivedAttributes;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeGroupedAttributes includeGroupedAttributes;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeClassifications includeClassifications;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeBomData includeBomData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludePlantData includePlantData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeLifecycleData includeLifecycleData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeLeadingZeros includeLeadingZeros;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeHierarchyData includeHierarchyData;  // attribute

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeWFAttributeData includeWFAttributeData;  // attribute

    private java.lang.String WFInitiativeID;  // attribute

    private java.lang.String WFAttributeTemplate;  // attribute

    public GPDB_ProductGetDetailsProductMatnr() {
    }

    // Simple Types must have a String constructor
    public GPDB_ProductGetDetailsProductMatnr(java.lang.String _value) {
        this._value = _value;
    }
    // Simple Types must have a toString for serializing the value
    public java.lang.String toString() {
        return _value;
    }


    /**
     * Gets the _value value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return _value
     */
    public java.lang.String get_value() {
        return _value;
    }


    /**
     * Sets the _value value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param _value
     */
    public void set_value(java.lang.String _value) {
        this._value = _value;
    }


    /**
     * Gets the _any value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return _any
     */
    public org.apache.axis.message.MessageElement [] get_any() {
        return _any;
    }


    /**
     * Sets the _any value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param _any
     */
    public void set_any(org.apache.axis.message.MessageElement [] _any) {
        this._any = _any;
    }


    /**
     * Gets the includeBasicData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeBasicData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeBasicData getIncludeBasicData() {
        return includeBasicData;
    }


    /**
     * Sets the includeBasicData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeBasicData
     */
    public void setIncludeBasicData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeBasicData includeBasicData) {
        this.includeBasicData = includeBasicData;
    }


    /**
     * Gets the includeUoMData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeUoMData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeUoMData getIncludeUoMData() {
        return includeUoMData;
    }


    /**
     * Sets the includeUoMData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeUoMData
     */
    public void setIncludeUoMData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeUoMData includeUoMData) {
        this.includeUoMData = includeUoMData;
    }


    /**
     * Gets the includeSalesData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeSalesData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeSalesData getIncludeSalesData() {
        return includeSalesData;
    }


    /**
     * Sets the includeSalesData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeSalesData
     */
    public void setIncludeSalesData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeSalesData includeSalesData) {
        this.includeSalesData = includeSalesData;
    }


    /**
     * Gets the includeAttributeData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeAttributeData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeAttributeData getIncludeAttributeData() {
        return includeAttributeData;
    }


    /**
     * Sets the includeAttributeData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeAttributeData
     */
    public void setIncludeAttributeData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeAttributeData includeAttributeData) {
        this.includeAttributeData = includeAttributeData;
    }


    /**
     * Gets the includeDerivedAttributes value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeDerivedAttributes
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes getIncludeDerivedAttributes() {
        return includeDerivedAttributes;
    }


    /**
     * Sets the includeDerivedAttributes value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeDerivedAttributes
     */
    public void setIncludeDerivedAttributes(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeDerivedAttributes includeDerivedAttributes) {
        this.includeDerivedAttributes = includeDerivedAttributes;
    }


    /**
     * Gets the includeGroupedAttributes value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeGroupedAttributes
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeGroupedAttributes getIncludeGroupedAttributes() {
        return includeGroupedAttributes;
    }


    /**
     * Sets the includeGroupedAttributes value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeGroupedAttributes
     */
    public void setIncludeGroupedAttributes(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeGroupedAttributes includeGroupedAttributes) {
        this.includeGroupedAttributes = includeGroupedAttributes;
    }


    /**
     * Gets the includeClassifications value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeClassifications
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeClassifications getIncludeClassifications() {
        return includeClassifications;
    }


    /**
     * Sets the includeClassifications value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeClassifications
     */
    public void setIncludeClassifications(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeClassifications includeClassifications) {
        this.includeClassifications = includeClassifications;
    }


    /**
     * Gets the includeBomData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeBomData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeBomData getIncludeBomData() {
        return includeBomData;
    }


    /**
     * Sets the includeBomData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeBomData
     */
    public void setIncludeBomData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeBomData includeBomData) {
        this.includeBomData = includeBomData;
    }


    /**
     * Gets the includePlantData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includePlantData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludePlantData getIncludePlantData() {
        return includePlantData;
    }


    /**
     * Sets the includePlantData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includePlantData
     */
    public void setIncludePlantData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludePlantData includePlantData) {
        this.includePlantData = includePlantData;
    }


    /**
     * Gets the includeLifecycleData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeLifecycleData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeLifecycleData getIncludeLifecycleData() {
        return includeLifecycleData;
    }


    /**
     * Sets the includeLifecycleData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeLifecycleData
     */
    public void setIncludeLifecycleData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeLifecycleData includeLifecycleData) {
        this.includeLifecycleData = includeLifecycleData;
    }


    /**
     * Gets the includeLeadingZeros value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeLeadingZeros
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeLeadingZeros getIncludeLeadingZeros() {
        return includeLeadingZeros;
    }


    /**
     * Sets the includeLeadingZeros value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeLeadingZeros
     */
    public void setIncludeLeadingZeros(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeLeadingZeros includeLeadingZeros) {
        this.includeLeadingZeros = includeLeadingZeros;
    }


    /**
     * Gets the includeHierarchyData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeHierarchyData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeHierarchyData getIncludeHierarchyData() {
        return includeHierarchyData;
    }


    /**
     * Sets the includeHierarchyData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeHierarchyData
     */
    public void setIncludeHierarchyData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeHierarchyData includeHierarchyData) {
        this.includeHierarchyData = includeHierarchyData;
    }


    /**
     * Gets the includeWFAttributeData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return includeWFAttributeData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeWFAttributeData getIncludeWFAttributeData() {
        return includeWFAttributeData;
    }


    /**
     * Sets the includeWFAttributeData value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param includeWFAttributeData
     */
    public void setIncludeWFAttributeData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsProductMatnrIncludeWFAttributeData includeWFAttributeData) {
        this.includeWFAttributeData = includeWFAttributeData;
    }


    /**
     * Gets the WFInitiativeID value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return WFInitiativeID
     */
    public java.lang.String getWFInitiativeID() {
        return WFInitiativeID;
    }


    /**
     * Sets the WFInitiativeID value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param WFInitiativeID
     */
    public void setWFInitiativeID(java.lang.String WFInitiativeID) {
        this.WFInitiativeID = WFInitiativeID;
    }


    /**
     * Gets the WFAttributeTemplate value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @return WFAttributeTemplate
     */
    public java.lang.String getWFAttributeTemplate() {
        return WFAttributeTemplate;
    }


    /**
     * Sets the WFAttributeTemplate value for this GPDB_ProductGetDetailsProductMatnr.
     * 
     * @param WFAttributeTemplate
     */
    public void setWFAttributeTemplate(java.lang.String WFAttributeTemplate) {
        this.WFAttributeTemplate = WFAttributeTemplate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsProductMatnr)) return false;
        GPDB_ProductGetDetailsProductMatnr other = (GPDB_ProductGetDetailsProductMatnr) obj;
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
            ((this._any==null && other.get_any()==null) || 
             (this._any!=null &&
              java.util.Arrays.equals(this._any, other.get_any()))) &&
            ((this.includeBasicData==null && other.getIncludeBasicData()==null) || 
             (this.includeBasicData!=null &&
              this.includeBasicData.equals(other.getIncludeBasicData()))) &&
            ((this.includeUoMData==null && other.getIncludeUoMData()==null) || 
             (this.includeUoMData!=null &&
              this.includeUoMData.equals(other.getIncludeUoMData()))) &&
            ((this.includeSalesData==null && other.getIncludeSalesData()==null) || 
             (this.includeSalesData!=null &&
              this.includeSalesData.equals(other.getIncludeSalesData()))) &&
            ((this.includeAttributeData==null && other.getIncludeAttributeData()==null) || 
             (this.includeAttributeData!=null &&
              this.includeAttributeData.equals(other.getIncludeAttributeData()))) &&
            ((this.includeDerivedAttributes==null && other.getIncludeDerivedAttributes()==null) || 
             (this.includeDerivedAttributes!=null &&
              this.includeDerivedAttributes.equals(other.getIncludeDerivedAttributes()))) &&
            ((this.includeGroupedAttributes==null && other.getIncludeGroupedAttributes()==null) || 
             (this.includeGroupedAttributes!=null &&
              this.includeGroupedAttributes.equals(other.getIncludeGroupedAttributes()))) &&
            ((this.includeClassifications==null && other.getIncludeClassifications()==null) || 
             (this.includeClassifications!=null &&
              this.includeClassifications.equals(other.getIncludeClassifications()))) &&
            ((this.includeBomData==null && other.getIncludeBomData()==null) || 
             (this.includeBomData!=null &&
              this.includeBomData.equals(other.getIncludeBomData()))) &&
            ((this.includePlantData==null && other.getIncludePlantData()==null) || 
             (this.includePlantData!=null &&
              this.includePlantData.equals(other.getIncludePlantData()))) &&
            ((this.includeLifecycleData==null && other.getIncludeLifecycleData()==null) || 
             (this.includeLifecycleData!=null &&
              this.includeLifecycleData.equals(other.getIncludeLifecycleData()))) &&
            ((this.includeLeadingZeros==null && other.getIncludeLeadingZeros()==null) || 
             (this.includeLeadingZeros!=null &&
              this.includeLeadingZeros.equals(other.getIncludeLeadingZeros()))) &&
            ((this.includeHierarchyData==null && other.getIncludeHierarchyData()==null) || 
             (this.includeHierarchyData!=null &&
              this.includeHierarchyData.equals(other.getIncludeHierarchyData()))) &&
            ((this.includeWFAttributeData==null && other.getIncludeWFAttributeData()==null) || 
             (this.includeWFAttributeData!=null &&
              this.includeWFAttributeData.equals(other.getIncludeWFAttributeData()))) &&
            ((this.WFInitiativeID==null && other.getWFInitiativeID()==null) || 
             (this.WFInitiativeID!=null &&
              this.WFInitiativeID.equals(other.getWFInitiativeID()))) &&
            ((this.WFAttributeTemplate==null && other.getWFAttributeTemplate()==null) || 
             (this.WFAttributeTemplate!=null &&
              this.WFAttributeTemplate.equals(other.getWFAttributeTemplate())));
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
        if (get_any() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(get_any());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getIncludeBasicData() != null) {
            _hashCode += getIncludeBasicData().hashCode();
        }
        if (getIncludeUoMData() != null) {
            _hashCode += getIncludeUoMData().hashCode();
        }
        if (getIncludeSalesData() != null) {
            _hashCode += getIncludeSalesData().hashCode();
        }
        if (getIncludeAttributeData() != null) {
            _hashCode += getIncludeAttributeData().hashCode();
        }
        if (getIncludeDerivedAttributes() != null) {
            _hashCode += getIncludeDerivedAttributes().hashCode();
        }
        if (getIncludeGroupedAttributes() != null) {
            _hashCode += getIncludeGroupedAttributes().hashCode();
        }
        if (getIncludeClassifications() != null) {
            _hashCode += getIncludeClassifications().hashCode();
        }
        if (getIncludeBomData() != null) {
            _hashCode += getIncludeBomData().hashCode();
        }
        if (getIncludePlantData() != null) {
            _hashCode += getIncludePlantData().hashCode();
        }
        if (getIncludeLifecycleData() != null) {
            _hashCode += getIncludeLifecycleData().hashCode();
        }
        if (getIncludeLeadingZeros() != null) {
            _hashCode += getIncludeLeadingZeros().hashCode();
        }
        if (getIncludeHierarchyData() != null) {
            _hashCode += getIncludeHierarchyData().hashCode();
        }
        if (getIncludeWFAttributeData() != null) {
            _hashCode += getIncludeWFAttributeData().hashCode();
        }
        if (getWFInitiativeID() != null) {
            _hashCode += getWFInitiativeID().hashCode();
        }
        if (getWFAttributeTemplate() != null) {
            _hashCode += getWFAttributeTemplate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsProductMatnr.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>GPDB_ProductGetDetails>ProductMatnr"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeBasicData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeBasicData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeBasicData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeUoMData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeUoMData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeUoMData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeSalesData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeSalesData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeSalesData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeAttributeData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeAttributeData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeAttributeData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeDerivedAttributes");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeDerivedAttributes"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeDerivedAttributes"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeGroupedAttributes");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeGroupedAttributes"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeGroupedAttributes"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeClassifications");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeClassifications"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeClassifications"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeBomData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeBomData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeBomData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includePlantData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includePlantData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includePlantData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeLifecycleData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeLifecycleData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeLifecycleData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeLeadingZeros");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeLeadingZeros"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeLeadingZeros"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeHierarchyData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeHierarchyData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeHierarchyData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("includeWFAttributeData");
        attrField.setXmlName(new javax.xml.namespace.QName("", "includeWFAttributeData"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetails>ProductMatnr>includeWFAttributeData"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("WFInitiativeID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "WFInitiativeID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("WFAttributeTemplate");
        attrField.setXmlName(new javax.xml.namespace.QName("", "WFAttributeTemplate"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "_value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
