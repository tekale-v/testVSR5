/*
 * Added by (DSM-Sogeti) for 22x.02 Defect - 52113
 * 
 **/
package com.pg.dsm.ui.table.market_clearance;

public class MarketClearanceRow {
    int rowCount;
    String marketName;
    String overallClearanceStatus;
    String approvalStatus;
    String ctNumber;
    String productRegulatoryClass;
    String marketApprovalHolder;
    String businessChannel;
    String legalEntity;
    String productRegistrationNumber;
    String registrationStatus;
    String registrationEndDate;
    String registrationRenewalLeadTime;
    String registrationRenewalStatus;
    String registeredProductName;
    String plantRestriction;
    String clearanceComment;
    String packingSite;
    String manufacturingSite;
    String packSize;
    String packSizeUoM;

    public String getOverallClearanceStatus() {
        return overallClearanceStatus;
    }

    public void setOverallClearanceStatus(String overallClearanceStatus) {
        this.overallClearanceStatus = overallClearanceStatus;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getCtNumber() {
        return ctNumber;
    }

    public void setCtNumber(String ctNumber) {
        this.ctNumber = ctNumber;
    }

    public String getProductRegulatoryClass() {
        return productRegulatoryClass;
    }

    public void setProductRegulatoryClass(String productRegulatoryClass) {
        this.productRegulatoryClass = productRegulatoryClass;
    }

    public String getMarketApprovalHolder() {
        return marketApprovalHolder;
    }

    public void setMarketApprovalHolder(String marketApprovalHolder) {
        this.marketApprovalHolder = marketApprovalHolder;
    }

    public String getBusinessChannel() {
        return businessChannel;
    }

    public void setBusinessChannel(String businessChannel) {
        this.businessChannel = businessChannel;
    }

    public String getLegalEntity() {
        return legalEntity;
    }

    public void setLegalEntity(String legalEntity) {
        this.legalEntity = legalEntity;
    }

    public String getProductRegistrationNumber() {
        return productRegistrationNumber;
    }

    public void setProductRegistrationNumber(String productRegistrationNumber) {
        this.productRegistrationNumber = productRegistrationNumber;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(String registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public String getRegistrationRenewalLeadTime() {
        return registrationRenewalLeadTime;
    }

    public void setRegistrationRenewalLeadTime(String registrationRenewalLeadTime) {
        this.registrationRenewalLeadTime = registrationRenewalLeadTime;
    }

    public String getRegistrationRenewalStatus() {
        return registrationRenewalStatus;
    }

    public void setRegistrationRenewalStatus(String registrationRenewalStatus) {
        this.registrationRenewalStatus = registrationRenewalStatus;
    }

    public String getRegisteredProductName() {
        return registeredProductName;
    }

    public void setRegisteredProductName(String registeredProductName) {
        this.registeredProductName = registeredProductName;
    }

    public String getPlantRestriction() {
        return plantRestriction;
    }

    public void setPlantRestriction(String plantRestriction) {
        this.plantRestriction = plantRestriction;
    }

    public String getClearanceComment() {
        return clearanceComment;
    }

    public void setClearanceComment(String clearanceComment) {
        this.clearanceComment = clearanceComment;
    }

    public String getPackingSite() {
        return packingSite;
    }

    public void setPackingSite(String packingSite) {
        this.packingSite = packingSite;
    }

    public String getManufacturingSite() {
        return manufacturingSite;
    }

    public void setManufacturingSite(String manufacturingSite) {
        this.manufacturingSite = manufacturingSite;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public String getPackSizeUoM() {
        return packSizeUoM;
    }

    public void setPackSizeUoM(String packSizeUoM) {
        this.packSizeUoM = packSizeUoM;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public String toString() {
        return "MarketClearanceRow{" +
                "rowCount=" + rowCount +
                ", marketName='" + marketName + '\'' +
                ", overallClearanceStatus='" + overallClearanceStatus + '\'' +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", ctNumber='" + ctNumber + '\'' +
                ", productRegulatoryClass='" + productRegulatoryClass + '\'' +
                ", marketApprovalHolder='" + marketApprovalHolder + '\'' +
                ", businessChannel='" + businessChannel + '\'' +
                ", legalEntity='" + legalEntity + '\'' +
                ", productRegistrationNumber='" + productRegistrationNumber + '\'' +
                ", registrationStatus='" + registrationStatus + '\'' +
                ", registrationEndDate='" + registrationEndDate + '\'' +
                ", registrationRenewalLeadTime='" + registrationRenewalLeadTime + '\'' +
                ", registrationRenewalStatus='" + registrationRenewalStatus + '\'' +
                ", registeredProductName='" + registeredProductName + '\'' +
                ", plantRestriction='" + plantRestriction + '\'' +
                ", clearanceComment='" + clearanceComment + '\'' +
                ", packingSite='" + packingSite + '\'' +
                ", manufacturingSite='" + manufacturingSite + '\'' +
                ", packSize='" + packSize + '\'' +
                ", packSizeUoM='" + packSizeUoM + '\'' +
                '}';
    }
}
