/**
 * GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship  implements java.io.Serializable {
    /* MAST-MATNR */
    private java.lang.String parentMatnr;

    /* STPO-IDNRK */
    private java.lang.String childMatnr;

    /* ZTXXPTSTPO-ZMEIN */
    private java.lang.String childUoM;

    /* MAKT */
    private java.lang.String childDescription;

    public GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship() {
    }

    public GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship(
           java.lang.String parentMatnr,
           java.lang.String childMatnr,
           java.lang.String childUoM,
           java.lang.String childDescription) {
           this.parentMatnr = parentMatnr;
           this.childMatnr = childMatnr;
           this.childUoM = childUoM;
           this.childDescription = childDescription;
    }


    /**
     * Gets the parentMatnr value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @return parentMatnr   * MAST-MATNR
     */
    public java.lang.String getParentMatnr() {
        return parentMatnr;
    }


    /**
     * Sets the parentMatnr value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @param parentMatnr   * MAST-MATNR
     */
    public void setParentMatnr(java.lang.String parentMatnr) {
        this.parentMatnr = parentMatnr;
    }


    /**
     * Gets the childMatnr value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @return childMatnr   * STPO-IDNRK
     */
    public java.lang.String getChildMatnr() {
        return childMatnr;
    }


    /**
     * Sets the childMatnr value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @param childMatnr   * STPO-IDNRK
     */
    public void setChildMatnr(java.lang.String childMatnr) {
        this.childMatnr = childMatnr;
    }


    /**
     * Gets the childUoM value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @return childUoM   * ZTXXPTSTPO-ZMEIN
     */
    public java.lang.String getChildUoM() {
        return childUoM;
    }


    /**
     * Sets the childUoM value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @param childUoM   * ZTXXPTSTPO-ZMEIN
     */
    public void setChildUoM(java.lang.String childUoM) {
        this.childUoM = childUoM;
    }


    /**
     * Gets the childDescription value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @return childDescription   * MAKT
     */
    public java.lang.String getChildDescription() {
        return childDescription;
    }


    /**
     * Sets the childDescription value for this GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.
     * 
     * @param childDescription   * MAKT
     */
    public void setChildDescription(java.lang.String childDescription) {
        this.childDescription = childDescription;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship)) return false;
        GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship other = (GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.parentMatnr==null && other.getParentMatnr()==null) || 
             (this.parentMatnr!=null &&
              this.parentMatnr.equals(other.getParentMatnr()))) &&
            ((this.childMatnr==null && other.getChildMatnr()==null) || 
             (this.childMatnr!=null &&
              this.childMatnr.equals(other.getChildMatnr()))) &&
            ((this.childUoM==null && other.getChildUoM()==null) || 
             (this.childUoM!=null &&
              this.childUoM.equals(other.getChildUoM()))) &&
            ((this.childDescription==null && other.getChildDescription()==null) || 
             (this.childDescription!=null &&
              this.childDescription.equals(other.getChildDescription())));
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
        if (getParentMatnr() != null) {
            _hashCode += getParentMatnr().hashCode();
        }
        if (getChildMatnr() != null) {
            _hashCode += getChildMatnr().hashCode();
        }
        if (getChildUoM() != null) {
            _hashCode += getChildUoM().hashCode();
        }
        if (getChildDescription() != null) {
            _hashCode += getChildDescription().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>BillOfMaterial>CompleteBillOfMaterial>Relationship"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentMatnr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ParentMatnr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("childMatnr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ChildMatnr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("childUoM");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ChildUoM"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("childDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ChildDescription"));
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
