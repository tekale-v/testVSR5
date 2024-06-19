/*
 **   Gcas.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Bean with getter/setter
 **
 */
package com.pg.dsm.upload.market.beans.mx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Gcas {
    @JsonIgnore
    boolean isLastRev;
    @JsonIgnore
    boolean isGcasExist;
    @JsonIgnore
    boolean isGcasObjectExist;
    @JsonIgnore
    boolean isGcasHasConnectedCountry;
    @JsonIgnore
    boolean isCountryAlreadyConnected;
    @JsonIgnore
    List<String> existingCountries;
    @JsonProperty("id")
    String id;
    @JsonProperty("type")
    String type;
    @JsonProperty("name")
    String name;
    @JsonProperty("revision")
    String revision;
    @JsonProperty("modified")
    String modified;
    @JsonProperty("owner")
    String owner;
    @JsonProperty("current")
    String current;
    @JsonProperty("policy")
    String policy;
    @JsonProperty("vault")
    String vault;
    @JsonProperty("last")
    String last;
    @JsonProperty("last.id")
    String lastId;
    @JsonProperty("last.current")
    String lastCurrent;
    @JsonProperty("last.revision")
    String lastRevision;
    @JsonProperty("islast")
    String isLast;
    @JsonProperty("relationship[pgProductCountryClearance].to.name")
    String countryNames;

    public boolean isLastRev() {
        return isLastRev;
    }

    public void setLastRev(boolean lastRev) {
        isLastRev = lastRev;
    }

    public boolean isGcasExist() {
        return isGcasExist;
    }

    public void setGcasExist(boolean gcasExist) {
        isGcasExist = gcasExist;
    }

    public boolean isGcasObjectExist() {
        return isGcasObjectExist;
    }

    public void setGcasObjectExist(boolean gcasObjectExist) {
        isGcasObjectExist = gcasObjectExist;
    }

    public boolean isGcasHasConnectedCountry() {
        return isGcasHasConnectedCountry;
    }

    public void setGcasHasConnectedCountry(boolean gcasHasConnectedCountry) {
        isGcasHasConnectedCountry = gcasHasConnectedCountry;
    }

    public boolean isCountryAlreadyConnected() {
        return isCountryAlreadyConnected;
    }

    public void setCountryAlreadyConnected(boolean countryAlreadyConnected) {
        isCountryAlreadyConnected = countryAlreadyConnected;
    }

    public List<String> getExistingCountries() {
        return existingCountries;
    }

    public void setExistingCountries(List<String> existingCountries) {
        this.existingCountries = existingCountries;
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

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

    public String getLastCurrent() {
        return lastCurrent;
    }

    public void setLastCurrent(String lastCurrent) {
        this.lastCurrent = lastCurrent;
    }

    public String getLastRevision() {
        return lastRevision;
    }

    public void setLastRevision(String lastRevision) {
        this.lastRevision = lastRevision;
    }

    public String getIsLast() {
        return isLast;
    }

    public void setIsLast(String isLast) {
        this.isLast = isLast;
    }

    public String getCountryNames() {
        return countryNames;
    }

    public void setCountryNames(String countryNames) {
        this.countryNames = countryNames;
    }
}