/**
 * GPDB_ProductGetDetailsResultProductBillOfMaterial.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductBillOfMaterial  implements java.io.Serializable {
    /* BOM of the material one level down */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent[] directBillOfMaterial;

    /* BOM of the materials, multiple levels down until component
     * is no longer complex */
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship[] completeBillOfMaterial;

    public GPDB_ProductGetDetailsResultProductBillOfMaterial() {
    }

    public GPDB_ProductGetDetailsResultProductBillOfMaterial(
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent[] directBillOfMaterial,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship[] completeBillOfMaterial) {
           this.directBillOfMaterial = directBillOfMaterial;
           this.completeBillOfMaterial = completeBillOfMaterial;
    }


    /**
     * Gets the directBillOfMaterial value for this GPDB_ProductGetDetailsResultProductBillOfMaterial.
     * 
     * @return directBillOfMaterial   * BOM of the material one level down
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent[] getDirectBillOfMaterial() {
        return directBillOfMaterial;
    }


    /**
     * Sets the directBillOfMaterial value for this GPDB_ProductGetDetailsResultProductBillOfMaterial.
     * 
     * @param directBillOfMaterial   * BOM of the material one level down
     */
    public void setDirectBillOfMaterial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialDirectBillOfMaterialComponent[] directBillOfMaterial) {
        this.directBillOfMaterial = directBillOfMaterial;
    }


    /**
     * Gets the completeBillOfMaterial value for this GPDB_ProductGetDetailsResultProductBillOfMaterial.
     * 
     * @return completeBillOfMaterial   * BOM of the materials, multiple levels down until component
     * is no longer complex
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship[] getCompleteBillOfMaterial() {
        return completeBillOfMaterial;
    }


    /**
     * Sets the completeBillOfMaterial value for this GPDB_ProductGetDetailsResultProductBillOfMaterial.
     * 
     * @param completeBillOfMaterial   * BOM of the materials, multiple levels down until component
     * is no longer complex
     */
    public void setCompleteBillOfMaterial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterialCompleteBillOfMaterialRelationship[] completeBillOfMaterial) {
        this.completeBillOfMaterial = completeBillOfMaterial;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductBillOfMaterial)) return false;
        GPDB_ProductGetDetailsResultProductBillOfMaterial other = (GPDB_ProductGetDetailsResultProductBillOfMaterial) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.directBillOfMaterial==null && other.getDirectBillOfMaterial()==null) || 
             (this.directBillOfMaterial!=null &&
              java.util.Arrays.equals(this.directBillOfMaterial, other.getDirectBillOfMaterial()))) &&
            ((this.completeBillOfMaterial==null && other.getCompleteBillOfMaterial()==null) || 
             (this.completeBillOfMaterial!=null &&
              java.util.Arrays.equals(this.completeBillOfMaterial, other.getCompleteBillOfMaterial())));
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
        if (getDirectBillOfMaterial() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDirectBillOfMaterial());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDirectBillOfMaterial(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCompleteBillOfMaterial() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCompleteBillOfMaterial());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCompleteBillOfMaterial(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductBillOfMaterial.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetailsResult>Product>BillOfMaterial"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directBillOfMaterial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DirectBillOfMaterial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>BillOfMaterial>DirectBillOfMaterial>Component"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "Component"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("completeBillOfMaterial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CompleteBillOfMaterial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>>GPDB_ProductGetDetailsResult>Product>BillOfMaterial>CompleteBillOfMaterial>Relationship"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "Relationship"));
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
