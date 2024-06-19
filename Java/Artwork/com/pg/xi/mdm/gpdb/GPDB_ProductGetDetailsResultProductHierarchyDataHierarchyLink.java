/**
 * GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink  implements java.io.Serializable {
    private java.lang.String hierarchy;  // attribute

    private java.lang.String parentNodeID;  // attribute

    public GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink() {
    }

    public GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink(
           java.lang.String hierarchy,
           java.lang.String parentNodeID) {
           this.hierarchy = hierarchy;
           this.parentNodeID = parentNodeID;
    }


    /**
     * Gets the hierarchy value for this GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink.
     * 
     * @return hierarchy
     */
    public java.lang.String getHierarchy() {
        return hierarchy;
    }


    /**
     * Sets the hierarchy value for this GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink.
     * 
     * @param hierarchy
     */
    public void setHierarchy(java.lang.String hierarchy) {
        this.hierarchy = hierarchy;
    }


    /**
     * Gets the parentNodeID value for this GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink.
     * 
     * @return parentNodeID
     */
    public java.lang.String getParentNodeID() {
        return parentNodeID;
    }


    /**
     * Sets the parentNodeID value for this GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink.
     * 
     * @param parentNodeID
     */
    public void setParentNodeID(java.lang.String parentNodeID) {
        this.parentNodeID = parentNodeID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink)) return false;
        GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink other = (GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.hierarchy==null && other.getHierarchy()==null) || 
             (this.hierarchy!=null &&
              this.hierarchy.equals(other.getHierarchy()))) &&
            ((this.parentNodeID==null && other.getParentNodeID()==null) || 
             (this.parentNodeID!=null &&
              this.parentNodeID.equals(other.getParentNodeID())));
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
        if (getHierarchy() != null) {
            _hashCode += getHierarchy().hashCode();
        }
        if (getParentNodeID() != null) {
            _hashCode += getParentNodeID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>HierarchyData>HierarchyLink"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("hierarchy");
        attrField.setXmlName(new javax.xml.namespace.QName("", "Hierarchy"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("parentNodeID");
        attrField.setXmlName(new javax.xml.namespace.QName("", "ParentNodeID"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>HierarchyData>HierarchyLink>ParentNodeID"));
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
