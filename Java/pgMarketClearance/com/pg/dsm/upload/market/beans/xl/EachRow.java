/*
 **   EachRow.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.xl;

import com.pg.dsm.upload.market.beans.mx.Gcas;
import com.pg.dsm.upload.market.beans.mx.Market;
import com.pg.v4.beans.COSRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EachRow {
    private List<String> validationMessageList;
    private Set<String> failedColumnNames;
    private String allowedTypes;
    private String allowedPolicies;
    private String calculateClearanceStatusErrorMessage;
    private int rowNumber;
    private int columnCount;
    private List<EachColumn> columnList;
    private COSRequest cosRequest;
    private String id;
    private String type;
    private String name;
    private String revision;
    private String current;
    private String policy;
    private String vault;
    private String owner;
    private String modified;
    private String last;
    private String lastId;
    private String gcasName;
    private String version;
    private String market;
    private String gpsApprovalStatus;
    private String clearanceNumber;
    private String productRegulatoryClassification;
    private String marketProductRegistrationNumber;
    private String registrationStatus;
    private String registrationExpirationDate;
    private String registrationRenewalLeadTime;
    private String registrationRenewalStatus;
    private String restrictions;
    private String comments;
    private String packingSite;
    private String bulkMakingManufacturingSite;
    private String recordFailureMessage;
    private boolean isGcasMarketCombinatedRepeated;
    private boolean isRecordProcessed;
    private boolean isLast;
    private boolean isGCASOverAllClearanceRequestPassed;
    private boolean isGcasExist;
    private boolean isGcasObjectExist;
    private boolean isMarketObjectExist;
    private boolean isMarketExist;
    private boolean isGcasHasConnectedCountry;
    private boolean isCountryAlreadyConnected;
    private boolean validateGcasPassed;
    private boolean validateVersionPassed;
    private boolean validateMarketPassed;
    private boolean validateGPSApprovalStatusPassed;
    private boolean validateClearanceNumberPassed;
    private boolean validateProductRegulatoryClassificationpassed;
    private boolean validateMarketProductRegistrationNumberPassed;
    private boolean validateRegistrationStatusPassed;
    private boolean validateRegistrationExpirationDatePassed;
    private boolean validateRegistrationRenewalLeadTimePassed;
    private boolean validateRegistrationRenewalStatusPassed;
    private boolean validateRestrictionsPassed;
    private boolean validateCommentsPassed;
    private boolean validatePackingSitePassed;
    private boolean validateBulkMakingManufacturingSitePassed;
    private Market marketObj;
    private Gcas gcasObj;
    private Map<String, String> attributeKeyValueMap;
    private List<String> existingCountries;

    private String strGcas;
    private String strCtNumber;
    private String strCountryRequested;
    private String strClearanceComment;
    private String strPlantRestriction;
    private String strDateModified;
    private String strDateCreated;
    private String strProductRegulatoryClassification;
    private String strCountryProductRegistrationNumber;
    private String strModifiedBy;
    private String strOverallClearance;
    private String strRegistrationStatus;
    private String strRegistrationEndDate;
    private String strPSRAApprovalStatus;
    private String strRegistrationRenewalLeadTime;
    private String strRegistrationRenewalStatus;
    private String strPackingSite;
    private String strBulkMakingManufacturingSite;
    private String strRegisteredProuctName;

    public String getRecordFailureMessage() {
        return recordFailureMessage;
    }

    public void setRecordFailureMessage(String recordFailureMessage) {
        this.recordFailureMessage = recordFailureMessage;
    }

    public boolean isRecordProcessed() {
        return isRecordProcessed;
    }

    public void setRecordProcessed(boolean isRecordProcessed) {
        this.isRecordProcessed = isRecordProcessed;
    }

    public boolean isGCASOverAllClearanceRequestPassed() {
        return isGCASOverAllClearanceRequestPassed;
    }

    public void setGCASOverAllClearanceRequestPassed(boolean isGCASOverAllClearanceRequestPassed) {
        this.isGCASOverAllClearanceRequestPassed = isGCASOverAllClearanceRequestPassed;
    }

    public COSRequest getCosRequest() {
        return cosRequest;
    }

    public void setCosRequest(COSRequest cosRequest) {
        this.cosRequest = cosRequest;
    }

    public String getGCAS() {
        return strGcas;
    }

    public void setGCAS(String sGcas) {
        strGcas = sGcas;
    }

    public String getCT_NUMBER() {
        return strCtNumber;
    }

    public void setCT_NUMBER(String strCtNumber) {
        this.strCtNumber = strCtNumber;
    }

    public String getCOUNTRY_REQUESTED() {
        return strCountryRequested;
    }

    public void setCOUNTRY_REQUESTED(String strCountryRequested) {
        this.strCountryRequested = strCountryRequested;
    }

    public String getCLEARANCE_COMMENT() {
        return strClearanceComment;
    }

    public void setCLEARANCE_COMMENT(String strClearanceComment) {
        this.strClearanceComment = strClearanceComment;
    }

    public String getPLANT_RESTRICTION() {
        return strPlantRestriction;
    }

    public void setPLANT_RESTRICTION(String strPlantRestriction) {
        this.strPlantRestriction = strPlantRestriction;
    }

    public String getDATE_MOD() {
        return strDateModified;
    }

    public void setDATE_MOD(String strDateModified) {
        this.strDateModified = strDateModified;
    }

    public String getDATE_CREATED() {
        return strDateCreated;
    }

    public void setDATE_CREATED(String strDateCreated) {
        this.strDateCreated = strDateCreated;
    }

    public String getPRODUCT_REGULATORY_CLASSIFICATION() {
        return strProductRegulatoryClassification;
    }

    public void setPRODUCT_REGULATORY_CLASSIFICATION(String strProductRegulatoryClassification) {
        this.strProductRegulatoryClassification = strProductRegulatoryClassification;
    }

    public String getCOUNTRY_PRODUCT_REGISTRATION_NUMBER() {
        return strCountryProductRegistrationNumber;
    }

    public void setCOUNTRY_PRODUCT_REGISTRATION_NUMBER(String strCountryProductRegistrationNumber) {
        this.strCountryProductRegistrationNumber = strCountryProductRegistrationNumber;
    }

    public String getMODIFIED_BY() {
        return strModifiedBy;
    }

    public void setMODIFIED_BY(String strModifiedBy) {
        this.strModifiedBy = strModifiedBy;
    }

    public String getOVERALL_CLEARANCE() {
        return strOverallClearance;
    }

    public void setOVERALL_CLEARANCE(String strOverallClearance) {
        this.strOverallClearance = strOverallClearance;
    }

    public String getREGISTRATION_STATUS() {
        return strRegistrationStatus;
    }

    public void setREGISTRATION_STATUS(String strRegistrationStatus) {
        this.strRegistrationStatus = strRegistrationStatus;
    }

    public String getREGISTRATION_END_DATE() {
        return strRegistrationEndDate;
    }

    public void setREGISTRATION_END_DATE(String strRegistrationEndDate) {
        this.strRegistrationEndDate = strRegistrationEndDate;
    }

    public String getPSRA_APPROVAL_STATUS() {
        return strPSRAApprovalStatus;
    }

    public void setPSRA_APPROVAL_STATUS(String strPSRAApprovalStatus) {
        this.strPSRAApprovalStatus = strPSRAApprovalStatus;
    }

    public String getREGISTRATION_RENEWAL_LEAD_TIME() {
        return strRegistrationRenewalLeadTime;
    }

    public void setREGISTRATION_RENEWAL_LEAD_TIME(String strRegistrationRenewalLeadTime) {
        this.strRegistrationRenewalLeadTime = strRegistrationRenewalLeadTime;
    }

    public String getREGISTRATION_RENEWAL_STATUS() {
        return strRegistrationRenewalStatus;
    }

    public void setREGISTRATION_RENEWAL_STATUS(String strRegistrationRenewalStatus) {
        this.strRegistrationRenewalStatus = strRegistrationRenewalStatus;
    }

    public String getPACKING_SITE() {
        return strPackingSite;
    }

    public void setPACKING_SITE(String sPackingSite) {
        this.strPackingSite = sPackingSite;
    }

    public String getBULK_MAKING_MANUFACTURING_SITE() {
        return strBulkMakingManufacturingSite;
    }

    public void setBULK_MAKING_MANUFACTURING_SITE(String strBulkMakingManufacturingSite) {
        this.strBulkMakingManufacturingSite = strBulkMakingManufacturingSite;
    }

    public String getGcasName() {
        return gcasName;
    }

    public void setGcasName(String gcasName) {
        this.gcasName = gcasName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getVault() {
        return vault;
    }

    public void setVault(String vault) {
        this.vault = vault;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getLast() {
        return last;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public boolean isValidateGcasPassed() {
        return validateGcasPassed;
    }

    public void setValidateGcasPassed(boolean validateGcasPassed) {
        this.validateGcasPassed = validateGcasPassed;
    }

    public boolean isGcasObjectExist() {
        return isGcasObjectExist;
    }

    public void setGcasObjectExist(boolean isGcasObjectExist) {
        this.isGcasObjectExist = isGcasObjectExist;
    }

    public boolean isMarketObjectExist() {
        return isMarketObjectExist;
    }

    public void setMarketObjectExist(boolean isMarketObjectExist) {
        this.isMarketObjectExist = isMarketObjectExist;
    }

    public boolean isCountryAlreadyConnected() {
        return isCountryAlreadyConnected;
    }

    public void setCountryAlreadyConnected(boolean isCountryAlreadyConnected) {
        this.isCountryAlreadyConnected = isCountryAlreadyConnected;
    }

    public String getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public boolean isValidateVersionPassed() {
        return validateVersionPassed;
    }

    public void setValidateVersionPassed(boolean validateVersionPassed) {
        this.validateVersionPassed = validateVersionPassed;
    }

    public boolean isValidateMarketPassed() {
        return validateMarketPassed;
    }

    public void setValidateMarketPassed(boolean validateMarketPassed) {
        this.validateMarketPassed = validateMarketPassed;
    }

    public boolean isValidateGPSApprovalStatusPassed() {
        return validateGPSApprovalStatusPassed;
    }

    public void setValidateGPSApprovalStatusPassed(boolean validateGPSApprovalStatusPassed) {
        this.validateGPSApprovalStatusPassed = validateGPSApprovalStatusPassed;
    }

    public boolean isValidateClearanceNumberPassed() {
        return validateClearanceNumberPassed;
    }

    public void setValidateClearanceNumberPassed(boolean validateClearanceNumberPassed) {
        this.validateClearanceNumberPassed = validateClearanceNumberPassed;
    }

    public boolean isValidateProductRegulatoryClassificationpassed() {
        return validateProductRegulatoryClassificationpassed;
    }

    public void setValidateProductRegulatoryClassificationpassed(boolean validateProductRegulatoryClassificationpassed) {
        this.validateProductRegulatoryClassificationpassed = validateProductRegulatoryClassificationpassed;
    }

    public boolean isValidateMarketProductRegistrationNumberPassed() {
        return validateMarketProductRegistrationNumberPassed;
    }

    public void setValidateMarketProductRegistrationNumberPassed(boolean validateMarketProductRegistrationNumberPassed) {
        this.validateMarketProductRegistrationNumberPassed = validateMarketProductRegistrationNumberPassed;
    }

    public boolean isValidateRegistrationStatusPassed() {
        return validateRegistrationStatusPassed;
    }

    public void setValidateRegistrationStatusPassed(boolean validateRegistrationStatusPassed) {
        this.validateRegistrationStatusPassed = validateRegistrationStatusPassed;
    }

    public boolean isValidateRegistrationExpirationDatePassed() {
        return validateRegistrationExpirationDatePassed;
    }

    public void setValidateRegistrationExpirationDatePassed(boolean validateRegistrationExpirationDatePassed) {
        this.validateRegistrationExpirationDatePassed = validateRegistrationExpirationDatePassed;
    }

    public boolean isValidateRegistrationRenewalLeadTimePassed() {
        return validateRegistrationRenewalLeadTimePassed;
    }

    public void setValidateRegistrationRenewalLeadTimePassed(boolean validateRegistrationRenewalLeadTimePassed) {
        this.validateRegistrationRenewalLeadTimePassed = validateRegistrationRenewalLeadTimePassed;
    }

    public boolean isValidateRegistrationRenewalStatusPassed() {
        return validateRegistrationRenewalStatusPassed;
    }

    public void setValidateRegistrationRenewalStatusPassed(boolean validateRegistrationRenewalStatusPassed) {
        this.validateRegistrationRenewalStatusPassed = validateRegistrationRenewalStatusPassed;
    }

    public boolean isValidateRestrictionsPassed() {
        return validateRestrictionsPassed;
    }

    public void setValidateRestrictionsPassed(boolean validateRestrictionsPassed) {
        this.validateRestrictionsPassed = validateRestrictionsPassed;
    }

    public boolean isValidateCommentsPassed() {
        return validateCommentsPassed;
    }

    public void setValidateCommentsPassed(boolean validateCommentsPassed) {
        this.validateCommentsPassed = validateCommentsPassed;
    }

    public boolean isValidatePackingSitePassed() {
        return validatePackingSitePassed;
    }

    public void setValidatePackingSitePassed(boolean validatePackingSitePassed) {
        this.validatePackingSitePassed = validatePackingSitePassed;
    }

    public boolean isValidateBulkMakingManufacturingSitePassed() {
        return validateBulkMakingManufacturingSitePassed;
    }

    public void setValidateBulkMakingManufacturingSitePassed(boolean validateBulkMakingManufacturingSitePassed) {
        this.validateBulkMakingManufacturingSitePassed = validateBulkMakingManufacturingSitePassed;
    }

    public List<EachColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<EachColumn> columnList) {
        this.columnList = columnList;
    }

    public Gcas getGcasObj() {
        return gcasObj;
    }

    public void setGcasObj(Gcas gcasObj) {
        this.gcasObj = gcasObj;
    }

    public List<String> getExistingCountries() {
        return existingCountries;
    }

    public void setExistingCountries(List<String> existingCountries) {
        this.existingCountries = existingCountries;
    }

    public boolean isGcasHasConnectedCountry() {
        return isGcasHasConnectedCountry;
    }

    public void setGcasHasConnectedCountry(boolean isGcasHasConnectedCountry) {
        this.isGcasHasConnectedCountry = isGcasHasConnectedCountry;
    }

    public Market getMarketObj() {
        return marketObj;
    }

    public void setMarketObj(Market marketObj) {
        this.marketObj = marketObj;
    }

    public boolean isMarketExist() {
        return isMarketExist;
    }

    public void setMarketExist(boolean isMarketExist) {
        this.isMarketExist = isMarketExist;
    }

    public boolean isGcasExist() {
        return isGcasExist;
    }

    public void setGcasExist(boolean isGcasExist) {
        this.isGcasExist = isGcasExist;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getGpsApprovalStatus() {
        return gpsApprovalStatus;
    }

    public void setGpsApprovalStatus(String gpsApprovalStatus) {
        this.gpsApprovalStatus = gpsApprovalStatus;
    }

    public String getClearanceNumber() {
        return clearanceNumber;
    }

    public void setClearanceNumber(String clearanceNumber) {
        this.clearanceNumber = clearanceNumber;
    }

    public String getProductRegulatoryClassification() {
        return productRegulatoryClassification;
    }

    public void setProductRegulatoryClassification(String productRegulatoryClassification) {
        this.productRegulatoryClassification = productRegulatoryClassification;
    }

    public String getMarketProductRegistrationNumber() {
        return marketProductRegistrationNumber;
    }

    public void setMarketProductRegistrationNumber(String marketProductRegistrationNumber) {
        this.marketProductRegistrationNumber = marketProductRegistrationNumber;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getRegistrationExpirationDate() {
        return registrationExpirationDate;
    }

    public void setRegistrationExpirationDate(String registrationExpirationDate) {
        this.registrationExpirationDate = registrationExpirationDate;
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

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPackingSite() {
        return packingSite;
    }

    public void setPackingSite(String packingSite) {
        this.packingSite = packingSite;
    }

    public String getBulkMakingManufacturingSite() {
        return bulkMakingManufacturingSite;
    }

    public void setBulkMakingManufacturingSite(String bulkMakingManufacturingSite) {
        this.bulkMakingManufacturingSite = bulkMakingManufacturingSite;
    }

    public List<String> getValidationMessageList() {
        return validationMessageList;
    }

    public void setValidationMessageList(List<String> validationMessageList) {
        this.validationMessageList = validationMessageList;
    }

    public Set<String> getFailedColumnNames() {
        return failedColumnNames;
    }

    public void setFailedColumnNames(Set<String> failedColumnNames) {
        this.failedColumnNames = failedColumnNames;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

    public String getCalculateClearanceStatusErrorMessage() {
        return calculateClearanceStatusErrorMessage;
    }

    public void setCalculateClearanceStatusErrorMessage(String calculateClearanceStatusErrorMessage) {
        this.calculateClearanceStatusErrorMessage = calculateClearanceStatusErrorMessage;
    }

    public boolean isGcasMarketCombinatedRepeated() {
        return isGcasMarketCombinatedRepeated;
    }

    public void setGcasMarketCombinatedRepeated(boolean isGcasMarketCombinatedRepeated) {
        this.isGcasMarketCombinatedRepeated = isGcasMarketCombinatedRepeated;
    }

    public String getAllowedPolicies() {
        return allowedPolicies;
    }

    public void setAllowedPolicies(String allowedPolicies) {
        this.allowedPolicies = allowedPolicies;
    }

    public Map<String, String> getAttributeKeyValueMap() {
        return attributeKeyValueMap;
    }

    public void setAttributeKeyValueMap(Map<String, String> attributeKeyValueMap) {
        this.attributeKeyValueMap = attributeKeyValueMap;
    }

    public String getREGISTERED_PRODUCT_NAME() {
        return strRegisteredProuctName;
    }

    public void setREGISTERED_PRODUCT_NAME(String strRegisteredProuctName) {
        this.strRegisteredProuctName = strRegisteredProuctName;
    }
}
