/**
 * GPDB_ProductGetDetailsResultProduct.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProduct  implements java.io.Serializable {
    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicData basicData;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductDescriptionsDescription[] descriptions;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure[] alternativeUnitsOfMeasures;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAttributesAttribute[] attributes;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductClassificationsClassification[] classifications;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductSalesDataSalesEntry[] salesData;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductPlantDataPlantEntry[] plantData;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterial billOfMaterial;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry[] lifeCycleData;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink[] hierarchyData;

    private com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributes[] WFAttributes;

    private java.lang.String prodMatnr;  // attribute

    public GPDB_ProductGetDetailsResultProduct() {
    }

    public GPDB_ProductGetDetailsResultProduct(
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicData basicData,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductDescriptionsDescription[] descriptions,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure[] alternativeUnitsOfMeasures,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAttributesAttribute[] attributes,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductClassificationsClassification[] classifications,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductSalesDataSalesEntry[] salesData,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductPlantDataPlantEntry[] plantData,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterial billOfMaterial,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry[] lifeCycleData,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink[] hierarchyData,
           com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributes[] WFAttributes,
           java.lang.String prodMatnr) {
           this.basicData = basicData;
           this.descriptions = descriptions;
           this.alternativeUnitsOfMeasures = alternativeUnitsOfMeasures;
           this.attributes = attributes;
           this.classifications = classifications;
           this.salesData = salesData;
           this.plantData = plantData;
           this.billOfMaterial = billOfMaterial;
           this.lifeCycleData = lifeCycleData;
           this.hierarchyData = hierarchyData;
           this.WFAttributes = WFAttributes;
           this.prodMatnr = prodMatnr;
    }


    /**
     * Gets the basicData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return basicData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicData getBasicData() {
        return basicData;
    }


    /**
     * Sets the basicData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param basicData
     */
    public void setBasicData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBasicData basicData) {
        this.basicData = basicData;
    }


    /**
     * Gets the descriptions value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return descriptions
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductDescriptionsDescription[] getDescriptions() {
        return descriptions;
    }


    /**
     * Sets the descriptions value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param descriptions
     */
    public void setDescriptions(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductDescriptionsDescription[] descriptions) {
        this.descriptions = descriptions;
    }


    /**
     * Gets the alternativeUnitsOfMeasures value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return alternativeUnitsOfMeasures
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure[] getAlternativeUnitsOfMeasures() {
        return alternativeUnitsOfMeasures;
    }


    /**
     * Sets the alternativeUnitsOfMeasures value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param alternativeUnitsOfMeasures
     */
    public void setAlternativeUnitsOfMeasures(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAlternativeUnitsOfMeasuresAlternativeUnitOfMeasure[] alternativeUnitsOfMeasures) {
        this.alternativeUnitsOfMeasures = alternativeUnitsOfMeasures;
    }


    /**
     * Gets the attributes value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return attributes
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAttributesAttribute[] getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param attributes
     */
    public void setAttributes(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductAttributesAttribute[] attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the classifications value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return classifications
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductClassificationsClassification[] getClassifications() {
        return classifications;
    }


    /**
     * Sets the classifications value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param classifications
     */
    public void setClassifications(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductClassificationsClassification[] classifications) {
        this.classifications = classifications;
    }


    /**
     * Gets the salesData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return salesData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductSalesDataSalesEntry[] getSalesData() {
        return salesData;
    }


    /**
     * Sets the salesData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param salesData
     */
    public void setSalesData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductSalesDataSalesEntry[] salesData) {
        this.salesData = salesData;
    }


    /**
     * Gets the plantData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return plantData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductPlantDataPlantEntry[] getPlantData() {
        return plantData;
    }


    /**
     * Sets the plantData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param plantData
     */
    public void setPlantData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductPlantDataPlantEntry[] plantData) {
        this.plantData = plantData;
    }


    /**
     * Gets the billOfMaterial value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return billOfMaterial
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterial getBillOfMaterial() {
        return billOfMaterial;
    }


    /**
     * Sets the billOfMaterial value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param billOfMaterial
     */
    public void setBillOfMaterial(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductBillOfMaterial billOfMaterial) {
        this.billOfMaterial = billOfMaterial;
    }


    /**
     * Gets the lifeCycleData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return lifeCycleData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry[] getLifeCycleData() {
        return lifeCycleData;
    }


    /**
     * Sets the lifeCycleData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param lifeCycleData
     */
    public void setLifeCycleData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductLifeCycleDataLifeCycleEntry[] lifeCycleData) {
        this.lifeCycleData = lifeCycleData;
    }


    /**
     * Gets the hierarchyData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return hierarchyData
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink[] getHierarchyData() {
        return hierarchyData;
    }


    /**
     * Sets the hierarchyData value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param hierarchyData
     */
    public void setHierarchyData(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductHierarchyDataHierarchyLink[] hierarchyData) {
        this.hierarchyData = hierarchyData;
    }


    /**
     * Gets the WFAttributes value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return WFAttributes
     */
    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributes[] getWFAttributes() {
        return WFAttributes;
    }


    /**
     * Sets the WFAttributes value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param WFAttributes
     */
    public void setWFAttributes(com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributes[] WFAttributes) {
        this.WFAttributes = WFAttributes;
    }

    public com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributes getWFAttributes(int i) {
        return this.WFAttributes[i];
    }

    public void setWFAttributes(int i, com.pg.xi.mdm.gpdb.GPDB_ProductGetDetailsResultProductWFAttributes _value) {
        this.WFAttributes[i] = _value;
    }


    /**
     * Gets the prodMatnr value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @return prodMatnr
     */
    public java.lang.String getProdMatnr() {
        return prodMatnr;
    }


    /**
     * Sets the prodMatnr value for this GPDB_ProductGetDetailsResultProduct.
     * 
     * @param prodMatnr
     */
    public void setProdMatnr(java.lang.String prodMatnr) {
        this.prodMatnr = prodMatnr;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProduct)) return false;
        GPDB_ProductGetDetailsResultProduct other = (GPDB_ProductGetDetailsResultProduct) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.basicData==null && other.getBasicData()==null) || 
             (this.basicData!=null &&
              this.basicData.equals(other.getBasicData()))) &&
            ((this.descriptions==null && other.getDescriptions()==null) || 
             (this.descriptions!=null &&
              java.util.Arrays.equals(this.descriptions, other.getDescriptions()))) &&
            ((this.alternativeUnitsOfMeasures==null && other.getAlternativeUnitsOfMeasures()==null) || 
             (this.alternativeUnitsOfMeasures!=null &&
              java.util.Arrays.equals(this.alternativeUnitsOfMeasures, other.getAlternativeUnitsOfMeasures()))) &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              java.util.Arrays.equals(this.attributes, other.getAttributes()))) &&
            ((this.classifications==null && other.getClassifications()==null) || 
             (this.classifications!=null &&
              java.util.Arrays.equals(this.classifications, other.getClassifications()))) &&
            ((this.salesData==null && other.getSalesData()==null) || 
             (this.salesData!=null &&
              java.util.Arrays.equals(this.salesData, other.getSalesData()))) &&
            ((this.plantData==null && other.getPlantData()==null) || 
             (this.plantData!=null &&
              java.util.Arrays.equals(this.plantData, other.getPlantData()))) &&
            ((this.billOfMaterial==null && other.getBillOfMaterial()==null) || 
             (this.billOfMaterial!=null &&
              this.billOfMaterial.equals(other.getBillOfMaterial()))) &&
            ((this.lifeCycleData==null && other.getLifeCycleData()==null) || 
             (this.lifeCycleData!=null &&
              java.util.Arrays.equals(this.lifeCycleData, other.getLifeCycleData()))) &&
            ((this.hierarchyData==null && other.getHierarchyData()==null) || 
             (this.hierarchyData!=null &&
              java.util.Arrays.equals(this.hierarchyData, other.getHierarchyData()))) &&
            ((this.WFAttributes==null && other.getWFAttributes()==null) || 
             (this.WFAttributes!=null &&
              java.util.Arrays.equals(this.WFAttributes, other.getWFAttributes()))) &&
            ((this.prodMatnr==null && other.getProdMatnr()==null) || 
             (this.prodMatnr!=null &&
              this.prodMatnr.equals(other.getProdMatnr())));
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
        if (getBasicData() != null) {
            _hashCode += getBasicData().hashCode();
        }
        if (getDescriptions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDescriptions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDescriptions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAlternativeUnitsOfMeasures() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAlternativeUnitsOfMeasures());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAlternativeUnitsOfMeasures(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getClassifications() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getClassifications());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getClassifications(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSalesData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSalesData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSalesData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPlantData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPlantData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPlantData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getBillOfMaterial() != null) {
            _hashCode += getBillOfMaterial().hashCode();
        }
        if (getLifeCycleData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLifeCycleData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLifeCycleData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getHierarchyData() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getHierarchyData());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getHierarchyData(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getWFAttributes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWFAttributes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWFAttributes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getProdMatnr() != null) {
            _hashCode += getProdMatnr().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProduct.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>GPDB_ProductGetDetailsResult>Product"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("prodMatnr");
        attrField.setXmlName(new javax.xml.namespace.QName("", "ProdMatnr"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("basicData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "BasicData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetailsResult>Product>BasicData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("descriptions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Descriptions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>Descriptions>Description"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "Description"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alternativeUnitsOfMeasures");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "AlternativeUnitsOfMeasures"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>AlternativeUnitsOfMeasures>AlternativeUnitOfMeasure"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "AlternativeUnitOfMeasure"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>Attributes>Attribute"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "Attribute"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("classifications");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "Classifications"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>Classifications>Classification"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "Classification"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("salesData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "SalesData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>SalesData>SalesEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "SalesEntry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("plantData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "PlantData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>PlantData>PlantEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "PlantEntry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billOfMaterial");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "BillOfMaterial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetailsResult>Product>BillOfMaterial"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lifeCycleData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "LifeCycleData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>LifeCycleData>LifeCycleEntry"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "LifeCycleEntry"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hierarchyData");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "HierarchyData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>HierarchyData>HierarchyLink"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "HierarchyLink"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("WFAttributes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "WFAttributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>GPDB_ProductGetDetailsResult>Product>WFAttributes"));
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
