/**
 * GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis #axisVersion# #today# WSDL2Java emitter.
 */

package com.pg.xi.mdm.gpdb;

public class GPDB_ProductGetDetailsResultProductSalesDataSalesEntry  implements java.io.Serializable {
    /* MVKE-VERSG */
    private java.lang.Object materialStatisticsGroup;

    /* MVKE-BONUS */
    private java.lang.Object volumeRebateGroup;

    /* MVKE-SKTOF */
    private java.lang.Object cashDiscountIndicator;

    /* MVKE-VMSTA */
    private java.lang.Object DCSpecificMaterialStatus;

    /* MVKE-VMSTD */
    private java.util.Date DCSpecificMaterialStatusDate;

    /* MVKE-SCMNG */
    private java.lang.Object deliveryUnit;

    /* MVKE-MTPOS */
    private java.lang.Object itemCategoryGroup;

    /* MVKE-DWERK */
    private java.lang.Object deliveringPlant;

    /* MVKE-PRODH (GOCOA category and segment) */
    private java.lang.Object productHierarchy;

    /* MVKE-KTGRM */
    private java.lang.Object accountAssignmentGroup;

    /* MVKE-MVGR1 */
    private java.lang.Object materialGroup1;

    /* MVKE-MVGR2 */
    private java.lang.Object materialGroup2;

    /* MVKE-MVGR3 */
    private java.lang.Object materialGroup3;

    /* MVKE-ZZDOTX */
    private java.lang.Object domesticMaterialTaxCode;

    /* MVKE-ZZISCTXT */
    private java.lang.Object invoiceSplitCriteriaText;

    /* MVKE-ZZLOCFPC */
    private java.lang.Object localFPC;

    private java.lang.Object salesOrganization;  // attribute

    private java.lang.Object distributionChannel;  // attribute

    public GPDB_ProductGetDetailsResultProductSalesDataSalesEntry() {
    }

    public GPDB_ProductGetDetailsResultProductSalesDataSalesEntry(
           java.lang.Object materialStatisticsGroup,
           java.lang.Object volumeRebateGroup,
           java.lang.Object cashDiscountIndicator,
           java.lang.Object DCSpecificMaterialStatus,
           java.util.Date DCSpecificMaterialStatusDate,
           java.lang.Object deliveryUnit,
           java.lang.Object itemCategoryGroup,
           java.lang.Object deliveringPlant,
           java.lang.Object productHierarchy,
           java.lang.Object accountAssignmentGroup,
           java.lang.Object materialGroup1,
           java.lang.Object materialGroup2,
           java.lang.Object materialGroup3,
           java.lang.Object domesticMaterialTaxCode,
           java.lang.Object invoiceSplitCriteriaText,
           java.lang.Object localFPC,
           java.lang.Object salesOrganization,
           java.lang.Object distributionChannel) {
           this.materialStatisticsGroup = materialStatisticsGroup;
           this.volumeRebateGroup = volumeRebateGroup;
           this.cashDiscountIndicator = cashDiscountIndicator;
           this.DCSpecificMaterialStatus = DCSpecificMaterialStatus;
           this.DCSpecificMaterialStatusDate = DCSpecificMaterialStatusDate;
           this.deliveryUnit = deliveryUnit;
           this.itemCategoryGroup = itemCategoryGroup;
           this.deliveringPlant = deliveringPlant;
           this.productHierarchy = productHierarchy;
           this.accountAssignmentGroup = accountAssignmentGroup;
           this.materialGroup1 = materialGroup1;
           this.materialGroup2 = materialGroup2;
           this.materialGroup3 = materialGroup3;
           this.domesticMaterialTaxCode = domesticMaterialTaxCode;
           this.invoiceSplitCriteriaText = invoiceSplitCriteriaText;
           this.localFPC = localFPC;
           this.salesOrganization = salesOrganization;
           this.distributionChannel = distributionChannel;
    }


    /**
     * Gets the materialStatisticsGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return materialStatisticsGroup   * MVKE-VERSG
     */
    public java.lang.Object getMaterialStatisticsGroup() {
        return materialStatisticsGroup;
    }


    /**
     * Sets the materialStatisticsGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param materialStatisticsGroup   * MVKE-VERSG
     */
    public void setMaterialStatisticsGroup(java.lang.Object materialStatisticsGroup) {
        this.materialStatisticsGroup = materialStatisticsGroup;
    }


    /**
     * Gets the volumeRebateGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return volumeRebateGroup   * MVKE-BONUS
     */
    public java.lang.Object getVolumeRebateGroup() {
        return volumeRebateGroup;
    }


    /**
     * Sets the volumeRebateGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param volumeRebateGroup   * MVKE-BONUS
     */
    public void setVolumeRebateGroup(java.lang.Object volumeRebateGroup) {
        this.volumeRebateGroup = volumeRebateGroup;
    }


    /**
     * Gets the cashDiscountIndicator value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return cashDiscountIndicator   * MVKE-SKTOF
     */
    public java.lang.Object getCashDiscountIndicator() {
        return cashDiscountIndicator;
    }


    /**
     * Sets the cashDiscountIndicator value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param cashDiscountIndicator   * MVKE-SKTOF
     */
    public void setCashDiscountIndicator(java.lang.Object cashDiscountIndicator) {
        this.cashDiscountIndicator = cashDiscountIndicator;
    }


    /**
     * Gets the DCSpecificMaterialStatus value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return DCSpecificMaterialStatus   * MVKE-VMSTA
     */
    public java.lang.Object getDCSpecificMaterialStatus() {
        return DCSpecificMaterialStatus;
    }


    /**
     * Sets the DCSpecificMaterialStatus value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param DCSpecificMaterialStatus   * MVKE-VMSTA
     */
    public void setDCSpecificMaterialStatus(java.lang.Object DCSpecificMaterialStatus) {
        this.DCSpecificMaterialStatus = DCSpecificMaterialStatus;
    }


    /**
     * Gets the DCSpecificMaterialStatusDate value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return DCSpecificMaterialStatusDate   * MVKE-VMSTD
     */
    public java.util.Date getDCSpecificMaterialStatusDate() {
        return DCSpecificMaterialStatusDate;
    }


    /**
     * Sets the DCSpecificMaterialStatusDate value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param DCSpecificMaterialStatusDate   * MVKE-VMSTD
     */
    public void setDCSpecificMaterialStatusDate(java.util.Date DCSpecificMaterialStatusDate) {
        this.DCSpecificMaterialStatusDate = DCSpecificMaterialStatusDate;
    }


    /**
     * Gets the deliveryUnit value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return deliveryUnit   * MVKE-SCMNG
     */
    public java.lang.Object getDeliveryUnit() {
        return deliveryUnit;
    }


    /**
     * Sets the deliveryUnit value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param deliveryUnit   * MVKE-SCMNG
     */
    public void setDeliveryUnit(java.lang.Object deliveryUnit) {
        this.deliveryUnit = deliveryUnit;
    }


    /**
     * Gets the itemCategoryGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return itemCategoryGroup   * MVKE-MTPOS
     */
    public java.lang.Object getItemCategoryGroup() {
        return itemCategoryGroup;
    }


    /**
     * Sets the itemCategoryGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param itemCategoryGroup   * MVKE-MTPOS
     */
    public void setItemCategoryGroup(java.lang.Object itemCategoryGroup) {
        this.itemCategoryGroup = itemCategoryGroup;
    }


    /**
     * Gets the deliveringPlant value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return deliveringPlant   * MVKE-DWERK
     */
    public java.lang.Object getDeliveringPlant() {
        return deliveringPlant;
    }


    /**
     * Sets the deliveringPlant value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param deliveringPlant   * MVKE-DWERK
     */
    public void setDeliveringPlant(java.lang.Object deliveringPlant) {
        this.deliveringPlant = deliveringPlant;
    }


    /**
     * Gets the productHierarchy value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return productHierarchy   * MVKE-PRODH (GOCOA category and segment)
     */
    public java.lang.Object getProductHierarchy() {
        return productHierarchy;
    }


    /**
     * Sets the productHierarchy value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param productHierarchy   * MVKE-PRODH (GOCOA category and segment)
     */
    public void setProductHierarchy(java.lang.Object productHierarchy) {
        this.productHierarchy = productHierarchy;
    }


    /**
     * Gets the accountAssignmentGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return accountAssignmentGroup   * MVKE-KTGRM
     */
    public java.lang.Object getAccountAssignmentGroup() {
        return accountAssignmentGroup;
    }


    /**
     * Sets the accountAssignmentGroup value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param accountAssignmentGroup   * MVKE-KTGRM
     */
    public void setAccountAssignmentGroup(java.lang.Object accountAssignmentGroup) {
        this.accountAssignmentGroup = accountAssignmentGroup;
    }


    /**
     * Gets the materialGroup1 value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return materialGroup1   * MVKE-MVGR1
     */
    public java.lang.Object getMaterialGroup1() {
        return materialGroup1;
    }


    /**
     * Sets the materialGroup1 value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param materialGroup1   * MVKE-MVGR1
     */
    public void setMaterialGroup1(java.lang.Object materialGroup1) {
        this.materialGroup1 = materialGroup1;
    }


    /**
     * Gets the materialGroup2 value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return materialGroup2   * MVKE-MVGR2
     */
    public java.lang.Object getMaterialGroup2() {
        return materialGroup2;
    }


    /**
     * Sets the materialGroup2 value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param materialGroup2   * MVKE-MVGR2
     */
    public void setMaterialGroup2(java.lang.Object materialGroup2) {
        this.materialGroup2 = materialGroup2;
    }


    /**
     * Gets the materialGroup3 value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return materialGroup3   * MVKE-MVGR3
     */
    public java.lang.Object getMaterialGroup3() {
        return materialGroup3;
    }


    /**
     * Sets the materialGroup3 value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param materialGroup3   * MVKE-MVGR3
     */
    public void setMaterialGroup3(java.lang.Object materialGroup3) {
        this.materialGroup3 = materialGroup3;
    }


    /**
     * Gets the domesticMaterialTaxCode value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return domesticMaterialTaxCode   * MVKE-ZZDOTX
     */
    public java.lang.Object getDomesticMaterialTaxCode() {
        return domesticMaterialTaxCode;
    }


    /**
     * Sets the domesticMaterialTaxCode value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param domesticMaterialTaxCode   * MVKE-ZZDOTX
     */
    public void setDomesticMaterialTaxCode(java.lang.Object domesticMaterialTaxCode) {
        this.domesticMaterialTaxCode = domesticMaterialTaxCode;
    }


    /**
     * Gets the invoiceSplitCriteriaText value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return invoiceSplitCriteriaText   * MVKE-ZZISCTXT
     */
    public java.lang.Object getInvoiceSplitCriteriaText() {
        return invoiceSplitCriteriaText;
    }


    /**
     * Sets the invoiceSplitCriteriaText value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param invoiceSplitCriteriaText   * MVKE-ZZISCTXT
     */
    public void setInvoiceSplitCriteriaText(java.lang.Object invoiceSplitCriteriaText) {
        this.invoiceSplitCriteriaText = invoiceSplitCriteriaText;
    }


    /**
     * Gets the localFPC value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return localFPC   * MVKE-ZZLOCFPC
     */
    public java.lang.Object getLocalFPC() {
        return localFPC;
    }


    /**
     * Sets the localFPC value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param localFPC   * MVKE-ZZLOCFPC
     */
    public void setLocalFPC(java.lang.Object localFPC) {
        this.localFPC = localFPC;
    }


    /**
     * Gets the salesOrganization value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return salesOrganization
     */
    public java.lang.Object getSalesOrganization() {
        return salesOrganization;
    }


    /**
     * Sets the salesOrganization value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param salesOrganization
     */
    public void setSalesOrganization(java.lang.Object salesOrganization) {
        this.salesOrganization = salesOrganization;
    }


    /**
     * Gets the distributionChannel value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @return distributionChannel
     */
    public java.lang.Object getDistributionChannel() {
        return distributionChannel;
    }


    /**
     * Sets the distributionChannel value for this GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.
     * 
     * @param distributionChannel
     */
    public void setDistributionChannel(java.lang.Object distributionChannel) {
        this.distributionChannel = distributionChannel;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GPDB_ProductGetDetailsResultProductSalesDataSalesEntry)) return false;
        GPDB_ProductGetDetailsResultProductSalesDataSalesEntry other = (GPDB_ProductGetDetailsResultProductSalesDataSalesEntry) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.materialStatisticsGroup==null && other.getMaterialStatisticsGroup()==null) || 
             (this.materialStatisticsGroup!=null &&
              this.materialStatisticsGroup.equals(other.getMaterialStatisticsGroup()))) &&
            ((this.volumeRebateGroup==null && other.getVolumeRebateGroup()==null) || 
             (this.volumeRebateGroup!=null &&
              this.volumeRebateGroup.equals(other.getVolumeRebateGroup()))) &&
            ((this.cashDiscountIndicator==null && other.getCashDiscountIndicator()==null) || 
             (this.cashDiscountIndicator!=null &&
              this.cashDiscountIndicator.equals(other.getCashDiscountIndicator()))) &&
            ((this.DCSpecificMaterialStatus==null && other.getDCSpecificMaterialStatus()==null) || 
             (this.DCSpecificMaterialStatus!=null &&
              this.DCSpecificMaterialStatus.equals(other.getDCSpecificMaterialStatus()))) &&
            ((this.DCSpecificMaterialStatusDate==null && other.getDCSpecificMaterialStatusDate()==null) || 
             (this.DCSpecificMaterialStatusDate!=null &&
              this.DCSpecificMaterialStatusDate.equals(other.getDCSpecificMaterialStatusDate()))) &&
            ((this.deliveryUnit==null && other.getDeliveryUnit()==null) || 
             (this.deliveryUnit!=null &&
              this.deliveryUnit.equals(other.getDeliveryUnit()))) &&
            ((this.itemCategoryGroup==null && other.getItemCategoryGroup()==null) || 
             (this.itemCategoryGroup!=null &&
              this.itemCategoryGroup.equals(other.getItemCategoryGroup()))) &&
            ((this.deliveringPlant==null && other.getDeliveringPlant()==null) || 
             (this.deliveringPlant!=null &&
              this.deliveringPlant.equals(other.getDeliveringPlant()))) &&
            ((this.productHierarchy==null && other.getProductHierarchy()==null) || 
             (this.productHierarchy!=null &&
              this.productHierarchy.equals(other.getProductHierarchy()))) &&
            ((this.accountAssignmentGroup==null && other.getAccountAssignmentGroup()==null) || 
             (this.accountAssignmentGroup!=null &&
              this.accountAssignmentGroup.equals(other.getAccountAssignmentGroup()))) &&
            ((this.materialGroup1==null && other.getMaterialGroup1()==null) || 
             (this.materialGroup1!=null &&
              this.materialGroup1.equals(other.getMaterialGroup1()))) &&
            ((this.materialGroup2==null && other.getMaterialGroup2()==null) || 
             (this.materialGroup2!=null &&
              this.materialGroup2.equals(other.getMaterialGroup2()))) &&
            ((this.materialGroup3==null && other.getMaterialGroup3()==null) || 
             (this.materialGroup3!=null &&
              this.materialGroup3.equals(other.getMaterialGroup3()))) &&
            ((this.domesticMaterialTaxCode==null && other.getDomesticMaterialTaxCode()==null) || 
             (this.domesticMaterialTaxCode!=null &&
              this.domesticMaterialTaxCode.equals(other.getDomesticMaterialTaxCode()))) &&
            ((this.invoiceSplitCriteriaText==null && other.getInvoiceSplitCriteriaText()==null) || 
             (this.invoiceSplitCriteriaText!=null &&
              this.invoiceSplitCriteriaText.equals(other.getInvoiceSplitCriteriaText()))) &&
            ((this.localFPC==null && other.getLocalFPC()==null) || 
             (this.localFPC!=null &&
              this.localFPC.equals(other.getLocalFPC()))) &&
            ((this.salesOrganization==null && other.getSalesOrganization()==null) || 
             (this.salesOrganization!=null &&
              this.salesOrganization.equals(other.getSalesOrganization()))) &&
            ((this.distributionChannel==null && other.getDistributionChannel()==null) || 
             (this.distributionChannel!=null &&
              this.distributionChannel.equals(other.getDistributionChannel())));
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
        if (getMaterialStatisticsGroup() != null) {
            _hashCode += getMaterialStatisticsGroup().hashCode();
        }
        if (getVolumeRebateGroup() != null) {
            _hashCode += getVolumeRebateGroup().hashCode();
        }
        if (getCashDiscountIndicator() != null) {
            _hashCode += getCashDiscountIndicator().hashCode();
        }
        if (getDCSpecificMaterialStatus() != null) {
            _hashCode += getDCSpecificMaterialStatus().hashCode();
        }
        if (getDCSpecificMaterialStatusDate() != null) {
            _hashCode += getDCSpecificMaterialStatusDate().hashCode();
        }
        if (getDeliveryUnit() != null) {
            _hashCode += getDeliveryUnit().hashCode();
        }
        if (getItemCategoryGroup() != null) {
            _hashCode += getItemCategoryGroup().hashCode();
        }
        if (getDeliveringPlant() != null) {
            _hashCode += getDeliveringPlant().hashCode();
        }
        if (getProductHierarchy() != null) {
            _hashCode += getProductHierarchy().hashCode();
        }
        if (getAccountAssignmentGroup() != null) {
            _hashCode += getAccountAssignmentGroup().hashCode();
        }
        if (getMaterialGroup1() != null) {
            _hashCode += getMaterialGroup1().hashCode();
        }
        if (getMaterialGroup2() != null) {
            _hashCode += getMaterialGroup2().hashCode();
        }
        if (getMaterialGroup3() != null) {
            _hashCode += getMaterialGroup3().hashCode();
        }
        if (getDomesticMaterialTaxCode() != null) {
            _hashCode += getDomesticMaterialTaxCode().hashCode();
        }
        if (getInvoiceSplitCriteriaText() != null) {
            _hashCode += getInvoiceSplitCriteriaText().hashCode();
        }
        if (getLocalFPC() != null) {
            _hashCode += getLocalFPC().hashCode();
        }
        if (getSalesOrganization() != null) {
            _hashCode += getSalesOrganization().hashCode();
        }
        if (getDistributionChannel() != null) {
            _hashCode += getDistributionChannel().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GPDB_ProductGetDetailsResultProductSalesDataSalesEntry.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", ">>>>GPDB_ProductGetDetailsResult>Product>SalesData>SalesEntry"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("salesOrganization");
        attrField.setXmlName(new javax.xml.namespace.QName("", "SalesOrganization"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("distributionChannel");
        attrField.setXmlName(new javax.xml.namespace.QName("", "DistributionChannel"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialStatisticsGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "MaterialStatisticsGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("volumeRebateGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "VolumeRebateGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cashDiscountIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "CashDiscountIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DCSpecificMaterialStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DCSpecificMaterialStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DCSpecificMaterialStatusDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DCSpecificMaterialStatusDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deliveryUnit");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DeliveryUnit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("itemCategoryGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ItemCategoryGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deliveringPlant");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DeliveringPlant"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("productHierarchy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "ProductHierarchy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountAssignmentGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "AccountAssignmentGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialGroup1");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "MaterialGroup1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialGroup2");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "MaterialGroup2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("materialGroup3");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "MaterialGroup3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domesticMaterialTaxCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "DomesticMaterialTaxCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invoiceSplitCriteriaText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "InvoiceSplitCriteriaText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localFPC");
        elemField.setXmlName(new javax.xml.namespace.QName("http://pg.com/xi/mdm/gpdb", "LocalFPC"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
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
