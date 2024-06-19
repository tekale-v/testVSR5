package com.pg.dsm.rollup_event.common.ebom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.util.StringList;

public class ProductPart {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    String id;
    String type;
    String name;
    String revision;
    String currentState;

    String relationship;
    String level;

    String physicalId;
    String productPartPhysicalId;
    String powerSource;
    String isTheProductABattery;
    String batteriesShippedInsideDevice;
    String batteriesShippedOutsideDevice;
    String batteriesRequired;
    String assemblyType;
    String releasePhase;

    String productPartPhysicalIdDb;
    String releasePhaseDb;
    String smartLabelReadyFlag;
    String dgcReadyFlag;

    String smartLabelReadyFlagDb;
    String dgcReadyFlagDb;

    // roll up flags. (all are manipulated)
    String dGCRollupFlag;
    String gHSRollupFlag;
    String stabilityResultsRollupFlag;
    String smartLabelRollupFlag;
    String certificationsRollupFlag;
    String warehouseRollupFlag;
    String markForRollupFlag;
    String marketRegistrationRollupFlag;
    String ingredientStatementRollupFlag;
    String batteryRollupFlag;

    String dGCRollupFlagDb;
    String gHSRollupFlagDb;
    String stabilityResultsRollupFlagDb;
    String smartLabelRollupFlagDb;
    String certificationsRollupFlagDb;
    String warehouseRollupFlagDb;
    String markForRollupFlagDb;

    String powerSourceDb;
    String isTheProductABatteryDb;
    String assemblyTypeDb;
    String wHCDoesDeviceContainsFlammableLiquidDb;
    double quantityDb;
    String batteriesShippedInsideDeviceDb;
    String batteriesShippedOutsideDeviceDb;
    String batteriesRequiredDb;
    String marketRegistrationRollupFlagDb;
    String ingredientStatementRollupFlagDb;
    String batteryRollupFlagDb;
    String eventForRollup;

    int numberOfBatteryShippedInsideDeviceDb;
    int numberOfBatteryShippedOutsideDeviceDb;
    int numberOfBatteriesRequiredDb;

    String setProductNameDb;

    StringList eBOMSubstituteFromID;
    StringList eBOMSubstituteFromType;
    StringList definesMaterialToID;
    StringList productPartIDFromMaster;
    StringList productPartTypeFromMaster;
    StringList alternateFromID;
    StringList pLBOMSubstituteFromID;

    double quantity;
    int numberOfBatteryShippedInsideDevice;
    int numberOfBatteryShippedOutsideDevice;
    int numberOfBatteriesRequired;

    String wHCDoesDeviceContainsFlammableLiquid;
    String setProductName;
    String bomType;

    ProductPart parent;
    List<ProductPart> parents;
    List<ProductPart> children;
    List<Substitute> substitutes;

    boolean childExist;
    boolean parentExist;
    boolean substituteExist;
    boolean circularExist;

    StringList markedEventList;
    StringList markedEventAttributeNameList;
    String baseUnitOfMeasure;

    //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts
    String bomBaseQuantity;
    String entryBaseQuantity;
    //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends
    //Modified for Defect # 44589 - Starts
    boolean calculateForRollup;
    //Modified for Defect # 44589 - Ends

    //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Starts
    StringList childrenList;
    boolean publishDGCToSAP;
    // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - Start
    boolean publishITToSAP;
    // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - End
    //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Ends
    public ProductPart() {
    }

    public ProductPart(ProductPart productPart) {
        this.id = productPart.id;
        this.type = productPart.type;
        this.name = productPart.name;
        this.revision = productPart.revision;
        this.currentState = productPart.currentState;
        this.relationship = productPart.relationship;
        this.level = productPart.level;
        this.physicalId = productPart.physicalId;
        this.productPartPhysicalId = productPart.productPartPhysicalId;
        this.powerSource = productPart.powerSource;
        this.isTheProductABattery = productPart.isTheProductABattery;
        this.batteriesShippedInsideDevice = productPart.batteriesShippedInsideDevice;
        this.batteriesShippedOutsideDevice = productPart.batteriesShippedOutsideDevice;
        this.batteriesRequired = productPart.batteriesRequired;
        this.assemblyType = productPart.assemblyType;
        this.releasePhase = productPart.releasePhase;
        this.productPartPhysicalIdDb = productPart.productPartPhysicalIdDb;
        this.releasePhaseDb = productPart.releasePhaseDb;
        this.smartLabelReadyFlag = productPart.smartLabelReadyFlag;
        this.dgcReadyFlag = productPart.dgcReadyFlag;
        this.smartLabelReadyFlagDb = productPart.smartLabelReadyFlagDb;
        this.dgcReadyFlagDb = productPart.dgcReadyFlagDb;
        this.dGCRollupFlag = productPart.dGCRollupFlag;
        this.gHSRollupFlag = productPart.gHSRollupFlag;
        this.stabilityResultsRollupFlag = productPart.stabilityResultsRollupFlag;
        this.smartLabelRollupFlag = productPart.smartLabelRollupFlag;
        this.certificationsRollupFlag = productPart.certificationsRollupFlag;
        this.warehouseRollupFlag = productPart.warehouseRollupFlag;
        this.markForRollupFlag = productPart.markForRollupFlag;
        this.marketRegistrationRollupFlag = productPart.marketRegistrationRollupFlag;
        this.ingredientStatementRollupFlag = productPart.ingredientStatementRollupFlag;
        this.batteryRollupFlag = productPart.batteryRollupFlag;
        this.dGCRollupFlagDb = productPart.dGCRollupFlagDb;
        this.gHSRollupFlagDb = productPart.gHSRollupFlagDb;
        this.stabilityResultsRollupFlagDb = productPart.stabilityResultsRollupFlagDb;
        this.smartLabelRollupFlagDb = productPart.smartLabelRollupFlagDb;
        this.certificationsRollupFlagDb = productPart.certificationsRollupFlagDb;
        this.warehouseRollupFlagDb = productPart.warehouseRollupFlagDb;
        this.markForRollupFlagDb = productPart.markForRollupFlagDb;
        this.powerSourceDb = productPart.powerSourceDb;
        this.isTheProductABatteryDb = productPart.isTheProductABatteryDb;
        this.assemblyTypeDb = productPart.assemblyTypeDb;
        this.wHCDoesDeviceContainsFlammableLiquidDb = productPart.wHCDoesDeviceContainsFlammableLiquidDb;
        this.quantityDb = productPart.quantityDb;
        this.batteriesShippedInsideDeviceDb = productPart.batteriesShippedInsideDeviceDb;
        this.batteriesShippedOutsideDeviceDb = productPart.batteriesShippedOutsideDeviceDb;
        this.batteriesRequiredDb = productPart.batteriesRequiredDb;
        this.marketRegistrationRollupFlagDb = productPart.marketRegistrationRollupFlagDb;
        this.ingredientStatementRollupFlagDb = productPart.ingredientStatementRollupFlagDb;
        this.batteryRollupFlagDb = productPart.batteryRollupFlagDb;
        this.numberOfBatteryShippedInsideDeviceDb = productPart.numberOfBatteryShippedInsideDeviceDb;
        this.numberOfBatteryShippedOutsideDeviceDb = productPart.numberOfBatteryShippedOutsideDeviceDb;
        this.numberOfBatteriesRequiredDb = productPart.numberOfBatteriesRequiredDb;
        this.setProductNameDb = productPart.setProductNameDb;
        this.eBOMSubstituteFromID = productPart.eBOMSubstituteFromID;
        this.eBOMSubstituteFromType = productPart.eBOMSubstituteFromType;
        this.definesMaterialToID = productPart.definesMaterialToID;
        this.productPartIDFromMaster = productPart.productPartIDFromMaster;
        this.productPartTypeFromMaster = productPart.productPartTypeFromMaster;
        this.alternateFromID = productPart.alternateFromID;
        this.pLBOMSubstituteFromID = productPart.pLBOMSubstituteFromID;
        this.quantity = productPart.quantity;
        this.numberOfBatteryShippedInsideDevice = productPart.numberOfBatteryShippedInsideDevice;
        this.numberOfBatteryShippedOutsideDevice = productPart.numberOfBatteryShippedOutsideDevice;
        this.numberOfBatteriesRequired = productPart.numberOfBatteriesRequired;
        this.wHCDoesDeviceContainsFlammableLiquid = productPart.wHCDoesDeviceContainsFlammableLiquid;
        this.setProductName = productPart.setProductName;
        this.bomType = productPart.bomType;
        this.parent = productPart.parent;
        this.parents = productPart.parents;
        this.children = productPart.children;
        this.substitutes = productPart.substitutes;
        this.childExist = productPart.childExist;
        this.parentExist = productPart.parentExist;
        this.substituteExist = productPart.substituteExist;
        this.markedEventList = productPart.markedEventList;
        this.markedEventAttributeNameList = productPart.markedEventAttributeNameList;
        this.eventForRollup = productPart.eventForRollup;
        this.baseUnitOfMeasure = productPart.baseUnitOfMeasure;
        //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts
        this.bomBaseQuantity = productPart.bomBaseQuantity;
        this.entryBaseQuantity = productPart.entryBaseQuantity;
        //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends
        this.circularExist = false;
        //Modified for Defect # 44589 - Starts
        this.calculateForRollup = productPart.calculateForRollup;
        //Modified for Defect # 44589 - Ends
        //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Starts
        this.childrenList = productPart.childrenList;
        this.publishDGCToSAP = productPart.publishDGCToSAP;
        //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Ends
        // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - Start
        this.publishITToSAP = productPart.publishITToSAP;
        // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - End
    }

    public ProductPart(Context context, Map<?, ?> objectMap, String bomType) {
        this.bomType = bomType;
        this.id = (String) objectMap.get(DomainConstants.SELECT_ID);
        this.type = (objectMap.containsKey(DomainConstants.SELECT_TYPE))
                ? (String) objectMap.get(DomainConstants.SELECT_TYPE)
                : DomainConstants.EMPTY_STRING;
        this.name = (objectMap.containsKey(DomainConstants.SELECT_NAME))
                ? (String) objectMap.get(DomainConstants.SELECT_NAME)
                : DomainConstants.EMPTY_STRING;
        this.revision = (objectMap.containsKey(DomainConstants.SELECT_REVISION))
                ? (String) objectMap.get(DomainConstants.SELECT_REVISION)
                : DomainConstants.EMPTY_STRING;
        this.currentState = (objectMap.containsKey(DomainConstants.SELECT_CURRENT))
                ? (String) objectMap.get(DomainConstants.SELECT_CURRENT)
                : DomainConstants.EMPTY_STRING;
        this.physicalId = (objectMap.containsKey(pgV3Constants.PHYSICALID))
                ? (String) objectMap.get(pgV3Constants.PHYSICALID)
                : DomainConstants.EMPTY_STRING;

        this.level = (objectMap.containsKey(DomainConstants.SELECT_LEVEL))
                ? (String) objectMap.get(DomainConstants.SELECT_LEVEL)
                : RollupConstants.Basic.ZERO.getValue();
        this.relationship = (objectMap.containsKey(pgV3Constants.RELATIONSHIP))
                ? (String) objectMap.get(pgV3Constants.RELATIONSHIP)
                : DomainConstants.EMPTY_STRING;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();

        setProductPartPhysicalId(objectMap);
        setPowerSource(objectMap);
        setIsTheProductABattery(objectMap);
        setAssemblyType(objectMap);
        setwHCDoesDeviceContainsFlammableLiquid(objectMap);
        setQuantity(objectMap);
        setBatteriesShippedInsideDevice(objectMap);
        setBatteriesShippedOutsideDevice(objectMap);
        setNumberOfBatteriesRequired(context, objectMap);

        // below keys required for processing - ready flags (DGC/SL Ready)
        setDgcReadyFlag(objectMap);
        setSmartLabelReadyFlag(objectMap);

        setSetProductName(context, objectMap);
        setReleasePhase(objectMap);

        setdGCRollup(objectMap);
        setgHSRollup(objectMap);
        setSmartLabelRollup(objectMap);
        setStabilityResultsRollup(objectMap);
        setCertificationsRollup(objectMap);
        setWarehouseRollup(objectMap);
        setMarkForRollup(objectMap);
        setMarketRegistrationRollup(context, objectMap);
        setIngredientStatementRollup(context, objectMap);
        setBatteryRollup(context, objectMap);

        setpLBOMSubstituteFromID(getStringListFromMap(objectMap, pgV3Constants.SELECT_PLBOMSUBSTITUTE_FROMID));
        seteBOMSubstituteFromID(getStringListFromMap(objectMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID));
        seteBOMSubstituteFromType(getStringListFromMap(objectMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROMTYPE));
        setDefinesMaterialToID(getStringListFromMap(objectMap, pgV3Constants.SELECT_PGDEFINESMATERIAL_TOID));
        setProductPartIDFromMaster(getStringListFromMap(objectMap, pgV3Constants.SELECT_PRODUCTPARTID_FROM_MASTER));
        setProductPartTypeFromMaster(getStringListFromMap(objectMap, pgV3Constants.SELECT_PRODUCTPARTTYPE_FROM_MASTER));
        setAlternateFromID(getStringListFromMap(objectMap, pgV3Constants.SELECT_ALTERNATE_FROMID));

        setMarkedEventList(new StringList());
        setMarkedEventAttributeNameList(context, new StringList());
        this.eventForRollup = (objectMap.containsKey(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP))
                ? (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP)
                : DomainConstants.EMPTY_STRING;
        this.baseUnitOfMeasure = (objectMap.containsKey(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE))
                ? (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE)
                : DomainConstants.EMPTY_STRING;
        //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts
        this.bomBaseQuantity = (objectMap.containsKey(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY))
                ? (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY)
                : DomainConstants.EMPTY_STRING;
        this.entryBaseQuantity = (objectMap.containsKey(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY))
                ? (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY)
                : DomainConstants.EMPTY_STRING;
        //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends

        //Modified for Defect # 44589 - Starts
        setCalculateForRollup(context, objectMap);
        //Modified for Defect # 44589 - Ends

        //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Starts
        this.childrenList = new StringList();
        this.publishDGCToSAP = false;
        //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Ends
        // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - Start
        this.publishITToSAP = false;
        // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - End
    }

    public StringList getStringListFromMap(Map<?, ?> objMap, String identifier) {
        StringList objectList = new StringList();
        try {
            Object objSubstitutes = objMap.get(identifier);
            if (null != objSubstitutes) {
                if (objSubstitutes instanceof StringList) {
                    objectList = (StringList) objSubstitutes;
                } else if (objSubstitutes.toString().contains(SelectConstants.cSelectDelimiter)) {
                    objectList = StringUtil.splitString(objSubstitutes.toString(), SelectConstants.cSelectDelimiter);
                } else {
                    objectList.add(objSubstitutes.toString());
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return objectList;
    }

    public ProductPart copy() {
        return new ProductPart(this);
    }

    public void addParent(ProductPart parentProductPart) {
        parents.add(parentProductPart);
    }

    public void addChild(ProductPart childProductPart) {
        children.add(childProductPart);
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getPhysicalId() {
        return physicalId;
    }

    public void setPhysicalId(String physicalId) {
        this.physicalId = physicalId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setQuantity(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_QUANTITY;
        boolean containKey = productMap.containsKey(key);
        String sQuantity;
        if (containKey) {
            sQuantity = (String) productMap.get(key);
            if (UIUtil.isNotNullAndNotEmpty(sQuantity)) {
                Double qtyVal = Double.parseDouble(sQuantity);
                setQuantityDb(qtyVal);
                setQuantity(qtyVal);
            }
        } else {
            key = RollupConstants.Basic.DEFAULT_QUANTITY_VALUE.getValue();
            if (productMap.containsKey(key)) {
                sQuantity = (String) productMap.get(key);
                if (UIUtil.isNotNullAndNotEmpty(sQuantity)) {
                    setQuantity(Double.parseDouble(sQuantity));
                }
            } else {
                setQuantity(Double.parseDouble(RollupConstants.Basic.DOUBLE_VALUE_ONE.getValue()));
            }
        }
    }

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }

    public void setPowerSource(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String powerSourceVal = (String) productMap.get(key);
            setPowerSourceDb(powerSourceVal);
            if (null == powerSourceVal) {
                setPowerSource(DomainConstants.EMPTY_STRING);
            } else {
                setPowerSource(powerSourceVal);
            }
        } else {
            setPowerSource(DomainConstants.EMPTY_STRING);
        }
    }

    public String getAssemblyType() {
        return assemblyType;
    }

    public void setAssemblyType(String assemblyType) {
        this.assemblyType = assemblyType;
    }

    public void setAssemblyType(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String assemblyTypeVal = (String) productMap.get(key);
            setAssemblyTypeDb(assemblyTypeVal);
            if (null == assemblyTypeVal) {
                setAssemblyType(DomainConstants.EMPTY_STRING);
            } else {
                setAssemblyType(assemblyTypeVal);
            }
        } else {
            setAssemblyType(DomainConstants.EMPTY_STRING);
        }
    }

    public String getBomType() {
        return bomType;
    }

    public void setBomType(String bomType) {
        this.bomType = bomType;
    }

    public String getSetProductName() {
        return setProductName;
    }

    public void setSetProductName(String setProductName) {
        this.setProductName = setProductName;
    }

    public ProductPart getParent() {
        return parent;
    }

    public void setParent(ProductPart parent) {
        this.parent = parent;
    }

    public List<ProductPart> getParents() {
        return parents;
    }

    public void setParents(List<ProductPart> parents) {
        this.parents = parents;
    }

    public List<ProductPart> getChildren() {
        return children;
    }

    public void setChildren(List<ProductPart> children) {
        this.children = children;
    }

    public List<Substitute> getSubstitutes() {
        return substitutes;
    }

    public void setSubstitutes(List<Substitute> substitutes) {
        this.substitutes = substitutes;
    }

    public boolean isChildExist() {
        return childExist;
    }

    public void setChildExist(boolean childExist) {
        this.childExist = childExist;
    }

    public boolean isParentExist() {
        return parentExist;
    }

    public void setParentExist(boolean parentExist) {
        this.parentExist = parentExist;
    }

    public boolean isSubstituteExist() {
        return substituteExist;
    }

    public void setSubstituteExist(boolean substituteExist) {
        this.substituteExist = substituteExist;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getBatteriesShippedInsideDevice() {
        return batteriesShippedInsideDevice;
    }

    public void setBatteriesShippedInsideDevice(String batteriesShippedInsideDevice) {
        this.batteriesShippedInsideDevice = batteriesShippedInsideDevice;
    }

    public void setBatteriesShippedInsideDevice(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDINSIDEDEVICE;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String batteryVal = (String) productMap.get(key);
            setBatteriesShippedInsideDeviceDb(batteryVal);
            if (UIUtil.isNotNullAndNotEmpty(batteryVal)) {
                setNumberOfBatteryShippedInsideDeviceDb(Integer.parseInt(batteryVal));
            }
        } else {
            key = RollupConstants.Basic.NUMBER_OF_BATTERIES_SHIPPED_INSIDE_DEVICE_DEFAULT_VALUE.getValue();
            if (productMap.containsKey(key)) {
                String batteryVal = (String) productMap.get(key);
                setBatteriesShippedInsideDevice(batteryVal);
                if (UIUtil.isNotNullAndNotEmpty(batteryVal)) {
                    setNumberOfBatteryShippedInsideDevice(Integer.parseInt(batteryVal));
                }
            }
        }
    }

    public String getBatteriesShippedOutsideDevice() {
        return batteriesShippedOutsideDevice;
    }

    public void setBatteriesShippedOutsideDevice(String batteriesShippedOutsideDevice) {
        this.batteriesShippedOutsideDevice = batteriesShippedOutsideDevice;
    }

    public void setBatteriesShippedOutsideDevice(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String batteryVal = (String) productMap.get(key);
            setBatteriesShippedOutsideDeviceDb(batteryVal);
            if (UIUtil.isNotNullAndNotEmpty(batteryVal)) {
                setNumberOfBatteryShippedOutsideDeviceDb(Integer.parseInt(batteryVal));
            }
        } else {
            key = RollupConstants.Basic.NUMBER_OF_BATTERIES_SHIPPED_OUTSIDE_DEVICE_DEFAULT_VALUE.getValue();
            if (productMap.containsKey(key)) {
                String batteryVal = (String) productMap.get(key);
                setBatteriesShippedOutsideDevice(batteryVal);
                if (UIUtil.isNotNullAndNotEmpty(batteryVal)) {
                    setNumberOfBatteryShippedOutsideDeviceDb(Integer.parseInt(batteryVal));
                }
            }
        }
    }

    public String getReleasePhase() {
        return releasePhase;
    }

    public void setReleasePhase(String releasePhase) {
        this.releasePhase = releasePhase;
    }

    public void setReleasePhase(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String phaseVal = (String) productMap.get(key);
            setReleasePhaseDb(phaseVal);
            if (null == phaseVal) {
                setReleasePhase(DomainConstants.EMPTY_STRING);
            } else {
                setReleasePhase(phaseVal);
            }
        } else {
            setReleasePhase(DomainConstants.EMPTY_STRING);
        }
    }

    public String getSmartLabelReadyFlag() {
        return smartLabelReadyFlag;
    }

    public void setSmartLabelReadyFlag(String smartLabelReadyFlag) {
        this.smartLabelReadyFlag = smartLabelReadyFlag;
    }

    public void setSmartLabelReadyFlag(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGSMARTLABELREADY;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String slReadyVal = (String) productMap.get(key);
            setSmartLabelReadyFlagDb(slReadyVal);
            if (null == slReadyVal) {
                setSmartLabelReadyFlag(DomainConstants.EMPTY_STRING);
            } else {
                setSmartLabelReadyFlag(slReadyVal);
            }
        } else {
            setSmartLabelReadyFlag(DomainConstants.EMPTY_STRING);
        }
    }

    public String getDgcReadyFlag() {
        return dgcReadyFlag;
    }

    public void setDgcReadyFlag(String dgcReadyFlag) {
        this.dgcReadyFlag = dgcReadyFlag;
    }

    public void setDgcReadyFlag(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSREADY;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String dgcReadyVal = (String) productMap.get(key);
            setDgcReadyFlagDb(dgcReadyVal);
            if (null == dgcReadyVal) {
                setDgcReadyFlag(DomainConstants.EMPTY_STRING);
            } else {
                setDgcReadyFlag(dgcReadyVal);
            }
        } else {
            setDgcReadyFlag(DomainConstants.EMPTY_STRING);
        }
    }

    public String getdGCRollupFlag() {
        return dGCRollupFlag;
    }

    public void setdGCRollupFlag(String dGCRollupFlag) {
        this.dGCRollupFlag = dGCRollupFlag;
    }

    public String getgHSRollupFlag() {
        return gHSRollupFlag;
    }

    public void setgHSRollupFlag(String gHSRollupFlag) {
        this.gHSRollupFlag = gHSRollupFlag;
    }

    public String getStabilityResultsRollupFlag() {
        return stabilityResultsRollupFlag;
    }

    public void setStabilityResultsRollupFlag(String stabilityResultsRollupFlag) {
        this.stabilityResultsRollupFlag = stabilityResultsRollupFlag;
    }

    public String getSmartLabelRollupFlag() {
        return smartLabelRollupFlag;
    }

    public void setSmartLabelRollupFlag(String smartLabelRollupFlag) {
        this.smartLabelRollupFlag = smartLabelRollupFlag;
    }

    public String getCertificationsRollupFlag() {
        return certificationsRollupFlag;
    }

    public void setCertificationsRollupFlag(String certificationsRollupFlag) {
        this.certificationsRollupFlag = certificationsRollupFlag;
    }

    public String getWarehouseRollupFlag() {
        return warehouseRollupFlag;
    }

    public void setWarehouseRollupFlag(String warehouseRollupFlag) {
        this.warehouseRollupFlag = warehouseRollupFlag;
    }

    public String getMarkForRollupFlag() {
        return markForRollupFlag;
    }

    public void setMarkForRollupFlag(String markForRollupFlag) {
        this.markForRollupFlag = markForRollupFlag;
    }

    public String getdGCRollupFlagDb() {
        return dGCRollupFlagDb;
    }

    public void setdGCRollupFlagDb(String dGCRollupFlagDb) {
        this.dGCRollupFlagDb = dGCRollupFlagDb;
    }

    public String getgHSRollupFlagDb() {
        return gHSRollupFlagDb;
    }

    public void setgHSRollupFlagDb(String gHSRollupFlagDb) {
        this.gHSRollupFlagDb = gHSRollupFlagDb;
    }

    public String getSmartLabelRollupFlagDb() {
        return smartLabelRollupFlagDb;
    }

    public void setSmartLabelRollupFlagDb(String smartLabelRollupFlagDb) {
        this.smartLabelRollupFlagDb = smartLabelRollupFlagDb;
    }

    public String getCertificationsRollupFlagDb() {
        return certificationsRollupFlagDb;
    }

    public void setCertificationsRollupFlagDb(String certificationsRollupFlagDb) {
        this.certificationsRollupFlagDb = certificationsRollupFlagDb;
    }

    public String getWarehouseRollupFlagDb() {
        return warehouseRollupFlagDb;
    }

    public void setWarehouseRollupFlagDb(String warehouseRollupFlagDb) {
        this.warehouseRollupFlagDb = warehouseRollupFlagDb;
    }

    public String getMarkForRollupFlagDb() {
        return markForRollupFlagDb;
    }

    public void setMarkForRollupFlagDb(String markForRollupFlagDb) {
        this.markForRollupFlagDb = markForRollupFlagDb;
    }

    public StringList geteBOMSubstituteFromID() {
        return eBOMSubstituteFromID;
    }

    public void seteBOMSubstituteFromID(StringList eBOMSubstituteFromID) {
        this.eBOMSubstituteFromID = eBOMSubstituteFromID;
    }

    public StringList geteBOMSubstituteFromType() {
        return eBOMSubstituteFromType;
    }

    public void seteBOMSubstituteFromType(StringList eBOMSubstituteFromType) {
        this.eBOMSubstituteFromType = eBOMSubstituteFromType;
    }

    public StringList getDefinesMaterialToID() {
        return definesMaterialToID;
    }

    public void setDefinesMaterialToID(StringList definesMaterialToID) {
        this.definesMaterialToID = definesMaterialToID;
    }

    public StringList getProductPartIDFromMaster() {
        return productPartIDFromMaster;
    }

    public void setProductPartIDFromMaster(StringList productPartIDFromMaster) {
        this.productPartIDFromMaster = productPartIDFromMaster;
    }

    public StringList getProductPartTypeFromMaster() {
        return productPartTypeFromMaster;
    }

    public void setProductPartTypeFromMaster(StringList productPartTypeFromMaster) {
        this.productPartTypeFromMaster = productPartTypeFromMaster;
    }

    public StringList getAlternateFromID() {
        return alternateFromID;
    }

    public void setAlternateFromID(StringList alternateFromID) {
        this.alternateFromID = alternateFromID;
    }

    public StringList getpLBOMSubstituteFromID() {
        return pLBOMSubstituteFromID;
    }

    public void setpLBOMSubstituteFromID(StringList pLBOMSubstituteFromID) {
        this.pLBOMSubstituteFromID = pLBOMSubstituteFromID;
    }

    public int getNumberOfBatteryShippedInsideDevice() {
        return numberOfBatteryShippedInsideDevice;
    }

    public void setNumberOfBatteryShippedInsideDevice(int numberOfBatteryShippedInsideDevice) {
        this.numberOfBatteryShippedInsideDevice = numberOfBatteryShippedInsideDevice;
    }

    public int getNumberOfBatteryShippedOutsideDevice() {
        return numberOfBatteryShippedOutsideDevice;
    }

    public void setNumberOfBatteryShippedOutsideDevice(int numberOfBatteryShippedOutsideDevice) {
        this.numberOfBatteryShippedOutsideDevice = numberOfBatteryShippedOutsideDevice;
    }

    public StringList getMarkedEventList() {
        return markedEventList;
    }

    public void setMarkedEventList(StringList markedEventList) {
        if (UIUtil.isNotNullAndNotEmpty(dGCRollupFlag) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(dGCRollupFlag)) {
            markedEventList.addElement(RollupConstants.Basic.DGC_ROLLUP_EVENT_IDENTIFIER.getValue());
        }
        if (UIUtil.isNotNullAndNotEmpty(gHSRollupFlag) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(gHSRollupFlag)) {
            markedEventList.addElement(RollupConstants.Basic.GHS_ROLLUP_EVENT_IDENTIFIER.getValue());
        }
        if (UIUtil.isNotNullAndNotEmpty(stabilityResultsRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(stabilityResultsRollupFlag)) {
            markedEventList.addElement(RollupConstants.Basic.STABILITY_RESULTS_ROLLUP_EVENT_IDENTIFIER.getValue());
        }
        if (UIUtil.isNotNullAndNotEmpty(smartLabelRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(smartLabelRollupFlag)) {
            markedEventList.addElement(RollupConstants.Basic.SMART_LABEL_ROLLUP_EVENT_IDENTIFIER.getValue());
        }
        if (UIUtil.isNotNullAndNotEmpty(certificationsRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(certificationsRollupFlag)) {
            markedEventList.addElement(RollupConstants.Basic.CERTIFICATIONS_ROLLUP_EVENT_IDENTIFIER.getValue());
        }
        if (UIUtil.isNotNullAndNotEmpty(warehouseRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(warehouseRollupFlag)) {
            markedEventList.addElement(RollupConstants.Basic.WAREHOUSE_ROLLUP_EVENT_IDENTIFIER.getValue());
        }
        if (UIUtil.isNotNullAndNotEmpty(smartLabelReadyFlag)
                && pgV3Constants.KEY_YES.equalsIgnoreCase(smartLabelReadyFlag)) {
            markedEventList.addElement(RollupConstants.Basic.SMART_LABEL_READY_ROLLUP_EVENT_IDENTIFIER.getValue());
        }
        this.markedEventList = markedEventList;
    }

    public String getPowerSourceDb() {
        return powerSourceDb;
    }

    public void setPowerSourceDb(String powerSourceDb) {
        this.powerSourceDb = powerSourceDb;
    }

    public String getAssemblyTypeDb() {
        return assemblyTypeDb;
    }

    public void setAssemblyTypeDb(String assemblyTypeDb) {
        this.assemblyTypeDb = assemblyTypeDb;
    }

    public String getwHCDoesDeviceContainsFlammableLiquidDb() {
        return wHCDoesDeviceContainsFlammableLiquidDb;
    }

    public void setwHCDoesDeviceContainsFlammableLiquidDb(String wHCDoesDeviceContainsFlammableLiquidDb) {
        this.wHCDoesDeviceContainsFlammableLiquidDb = wHCDoesDeviceContainsFlammableLiquidDb;
    }

    public double getQuantityDb() {
        return quantityDb;
    }

    public void setQuantityDb(double quantityDb) {
        this.quantityDb = quantityDb;
    }

    public String getBatteriesShippedInsideDeviceDb() {
        return batteriesShippedInsideDeviceDb;
    }

    public void setBatteriesShippedInsideDeviceDb(String batteriesShippedInsideDeviceDb) {
        this.batteriesShippedInsideDeviceDb = batteriesShippedInsideDeviceDb;
    }

    public String getBatteriesShippedOutsideDeviceDb() {
        return batteriesShippedOutsideDeviceDb;
    }

    public void setBatteriesShippedOutsideDeviceDb(String batteriesShippedOutsideDeviceDb) {
        this.batteriesShippedOutsideDeviceDb = batteriesShippedOutsideDeviceDb;
    }

    public int getNumberOfBatteryShippedInsideDeviceDb() {
        return numberOfBatteryShippedInsideDeviceDb;
    }

    public void setNumberOfBatteryShippedInsideDeviceDb(int numberOfBatteryShippedInsideDeviceDb) {
        this.numberOfBatteryShippedInsideDeviceDb = numberOfBatteryShippedInsideDeviceDb;
    }

    public int getNumberOfBatteryShippedOutsideDeviceDb() {
        return numberOfBatteryShippedOutsideDeviceDb;
    }

    public void setNumberOfBatteryShippedOutsideDeviceDb(int numberOfBatteryShippedOutsideDeviceDb) {
        this.numberOfBatteryShippedOutsideDeviceDb = numberOfBatteryShippedOutsideDeviceDb;
    }

    public String getSetProductNameDb() {
        return setProductNameDb;
    }

    public void setSetProductNameDb(String setProductNameDb) {
        this.setProductNameDb = setProductNameDb;
    }

    public String getwHCDoesDeviceContainsFlammableLiquid() {
        return wHCDoesDeviceContainsFlammableLiquid;
    }

    public void setwHCDoesDeviceContainsFlammableLiquid(String wHCDoesDeviceContainsFlammableLiquid) {
        this.wHCDoesDeviceContainsFlammableLiquid = wHCDoesDeviceContainsFlammableLiquid;
    }

    public void setwHCDoesDeviceContainsFlammableLiquid(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGDOESDEVICECONTAINFLAMMABLELIQUID;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String whcVal = (String) productMap.get(key);
            setwHCDoesDeviceContainsFlammableLiquidDb(whcVal);
            if (null == whcVal) {
                setwHCDoesDeviceContainsFlammableLiquid(DomainConstants.EMPTY_STRING);
            } else {
                setwHCDoesDeviceContainsFlammableLiquid(whcVal);
            }
        } else {
            setwHCDoesDeviceContainsFlammableLiquid(DomainConstants.EMPTY_STRING);
        }
    }

    public String getStabilityResultsRollupFlagDb() {
        return stabilityResultsRollupFlagDb;
    }

    public void setStabilityResultsRollupFlagDb(String stabilityResultsRollupFlagDb) {
        this.stabilityResultsRollupFlagDb = stabilityResultsRollupFlagDb;
    }

    public void setdGCRollup(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGDGCROLLUPFLAG;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String dgcVal = (String) productMap.get(key);
            setdGCRollupFlagDb(dgcVal);
            if (null == dgcVal) {
                setdGCRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setdGCRollupFlag(dgcVal);
            }
        }
    }

    public void setgHSRollup(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGGHSROLLUPFLAG;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String ghsVal = (String) productMap.get(key);
            setgHSRollupFlagDb(ghsVal);
            if (null == ghsVal) {
                setgHSRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setgHSRollupFlag(ghsVal);
            }
        }
    }

    public void setStabilityResultsRollup(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGSRROLLUPFLAG;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String srVal = (String) productMap.get(key);
            setStabilityResultsRollupFlagDb(srVal);
            if (null == srVal) {
                setStabilityResultsRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setStabilityResultsRollupFlag(srVal);
            }
        }
    }

    public void setSmartLabelRollup(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGSLROLLUPFLAG;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String slVal = (String) productMap.get(key);
            setSmartLabelRollupFlagDb(slVal);
            if (null == slVal) {
                setSmartLabelRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setSmartLabelRollupFlag(slVal);
            }
        }
    }

    public void setCertificationsRollup(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGCERTIFICATIONSROLLUPFLAG;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String certVal = (String) productMap.get(key);
            setCertificationsRollupFlagDb(certVal);
            if (null == certVal) {
                setCertificationsRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setCertificationsRollupFlag(certVal);
            }
        }
    }

    public void setWarehouseRollup(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGWAREHOUSEROLLUPFLAG;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String whVal = (String) productMap.get(key);
            setWarehouseRollupFlagDb(whVal);
            if (null == whVal) {
                setWarehouseRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setWarehouseRollupFlag(whVal);
            }
        }
    }

    public void setMarkForRollup(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String rollVal = (String) productMap.get(key);
            setMarkForRollupFlagDb(rollVal);
            if (null == rollVal) {
                setMarkForRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setMarkForRollupFlag(rollVal);
            }
        }
    }

    public void setMarketRegistrationRollup(Context context, Map<?, ?> productMap) {
        String key = RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context);
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String rollVal = (String) productMap.get(key);
            setMarketRegistrationRollupFlagDb(rollVal);
            if (null == rollVal) {
                setMarketRegistrationRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setMarketRegistrationRollupFlag(rollVal);
            }
        }
    }

    public void setIngredientStatementRollup(Context context, Map<?, ?> productMap) {
        String key = RollupConstants.Attribute.INGREDIENT_STATEMENT_ROLLUP_FLAG.getSelect(context);
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String rollVal = (String) productMap.get(key);
            setIngredientStatementRollupFlagDb(rollVal);
            if (null == rollVal) {
                setIngredientStatementRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setIngredientStatementRollupFlag(rollVal);
            }
        }
    }

    public void setBatteryRollup(Context context, Map<?, ?> productMap) {
        String key = RollupConstants.Attribute.BATTERY_ROLLUP_FLAG.getSelect(context);
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String rollVal = (String) productMap.get(key);
            setBatteryRollupFlagDb(rollVal);
            if (null == rollVal) {
                setBatteryRollupFlag(pgV3Constants.KEY_FALSE);
            } else {
                setBatteryRollupFlag(rollVal);
            }
        }
    }

    public void setSetProductName(Context context, Map<?, ?> productMap) {
        String key = RollupConstants.Attribute.SET_PRODUCT_NAME.getSelect(context);
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String setProdVal = (String) productMap.get(key);
            setSetProductNameDb(setProdVal);
            if (null == setProdVal) {
                setSetProductName(DomainConstants.EMPTY_STRING);
            } else {
                setSetProductName(setProdVal);
            }
        } else {
            setSetProductName(DomainConstants.EMPTY_STRING);
        }
    }

    public String getSmartLabelReadyFlagDb() {
        return smartLabelReadyFlagDb;
    }

    public void setSmartLabelReadyFlagDb(String smartLabelReadyFlagDb) {
        this.smartLabelReadyFlagDb = smartLabelReadyFlagDb;
    }

    public String getDgcReadyFlagDb() {
        return dgcReadyFlagDb;
    }

    public void setDgcReadyFlagDb(String dgcReadyFlagDb) {
        this.dgcReadyFlagDb = dgcReadyFlagDb;
    }

    public String getReleasePhaseDb() {
        return releasePhaseDb;
    }

    public void setReleasePhaseDb(String releasePhaseDb) {
        this.releasePhaseDb = releasePhaseDb;
    }

    public String getProductPartPhysicalId() {
        return productPartPhysicalId;
    }

    public void setProductPartPhysicalId(String productPartPhysicalId) {
        this.productPartPhysicalId = productPartPhysicalId;
    }

    public void setProductPartPhysicalId(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String prodPhysIdVal = (String) productMap.get(key);
            setProductPartPhysicalIdDb(prodPhysIdVal);
            if (null == prodPhysIdVal) {
                setProductPartPhysicalId(DomainConstants.EMPTY_STRING);
            } else {
                setProductPartPhysicalId(prodPhysIdVal);
            }
        } else {
            setProductPartPhysicalId(DomainConstants.EMPTY_STRING);
        }
    }

    public String getProductPartPhysicalIdDb() {
        return productPartPhysicalIdDb;
    }

    public void setProductPartPhysicalIdDb(String productPartPhysicalIdDb) {
        this.productPartPhysicalIdDb = productPartPhysicalIdDb;
    }

    public StringList getMarkedEventAttributeNameList() {
        return markedEventAttributeNameList;
    }

    public void setMarkedEventAttributeNameList(Context context, StringList markedEventAttributeNameList) {
        if (UIUtil.isNotNullAndNotEmpty(dGCRollupFlag) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(dGCRollupFlag)) {
            markedEventAttributeNameList.addElement(pgV3Constants.ATTRIBUTE_PGDGCROLLUPFLAG);
        }
        if (UIUtil.isNotNullAndNotEmpty(gHSRollupFlag) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(gHSRollupFlag)) {
            markedEventAttributeNameList.addElement(pgV3Constants.ATTRIBUTE_PGGHSROLLUPFLAG);
        }
        if (UIUtil.isNotNullAndNotEmpty(stabilityResultsRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(stabilityResultsRollupFlag)) {
            markedEventAttributeNameList.addElement(pgV3Constants.ATTRIBUTE_PGSRROLLUPFLAG);
        }
        if (UIUtil.isNotNullAndNotEmpty(smartLabelRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(smartLabelRollupFlag)) {
            markedEventAttributeNameList.addElement(pgV3Constants.ATTRIBUTE_PGSLROLLUPFLAG);
        }
        if (UIUtil.isNotNullAndNotEmpty(certificationsRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(certificationsRollupFlag)) {
            markedEventAttributeNameList.addElement(pgV3Constants.ATTRIBUTE_PGCERTIFICATIONSROLLUPFLAG);
        }
        if (UIUtil.isNotNullAndNotEmpty(warehouseRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(warehouseRollupFlag)) {
            markedEventAttributeNameList.addElement(pgV3Constants.ATTRIBUTE_PGWAREHOUSEROLLUPFLAG);
        }
        if (UIUtil.isNotNullAndNotEmpty(marketRegistrationRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(marketRegistrationRollupFlag)) {
            markedEventAttributeNameList
                    .addElement(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getName(context));
        }
        if (UIUtil.isNotNullAndNotEmpty(ingredientStatementRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(ingredientStatementRollupFlag)) {
            markedEventAttributeNameList
                    .addElement(RollupConstants.Attribute.INGREDIENT_STATEMENT_ROLLUP_FLAG.getName(context));
        }
        if (UIUtil.isNotNullAndNotEmpty(batteryRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(batteryRollupFlag)) {
            markedEventAttributeNameList.addElement(RollupConstants.Attribute.BATTERY_ROLLUP_FLAG.getName(context));
        }
        if (UIUtil.isNotNullAndNotEmpty(markForRollupFlag)
                && pgV3Constants.KEY_TRUE.equalsIgnoreCase(markForRollupFlag)) {
            markedEventAttributeNameList.addElement(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP);
        }
        this.markedEventAttributeNameList = markedEventAttributeNameList;
    }

    public int getNumberOfBatteriesRequired() {
        return numberOfBatteriesRequired;
    }

    public void setNumberOfBatteriesRequired(int numberOfBatteriesRequired) {
        this.numberOfBatteriesRequired = numberOfBatteriesRequired;
    }

    public void setNumberOfBatteriesRequired(Context context, Map<?, ?> productMap) {
        String key = RollupConstants.Attribute.NUMBER_OF_BATTERIES_REQUIRED.getSelect(context);
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String sNumberOfBatteriesRequired = (String) productMap.get(key);
            setBatteriesRequiredDb(sNumberOfBatteriesRequired);
            if (UIUtil.isNotNullAndNotEmpty(sNumberOfBatteriesRequired)) {
                setNumberOfBatteriesRequiredDb(Integer.parseInt(sNumberOfBatteriesRequired));
            }
        } else {
            // Add below key default value in page
            key = RollupConstants.Basic.NUMBER_OF_BATTERIES_REQUIRED_DEFAULT_VALUE.getValue();
            if (productMap.containsKey(key)) {
                String batteryVal = (String) productMap.get(key);
                setBatteriesRequired(batteryVal);
                if (UIUtil.isNotNullAndNotEmpty(batteryVal)) {
                    setNumberOfBatteriesRequired(Integer.parseInt(batteryVal));
                }
            }
        }

    }

    public String getBatteriesRequiredDb() {
        return batteriesRequiredDb;
    }

    public void setBatteriesRequiredDb(String batteriesRequiredDb) {
        this.batteriesRequiredDb = batteriesRequiredDb;
    }

    public int getNumberOfBatteriesRequiredDb() {
        return numberOfBatteriesRequiredDb;
    }

    public void setNumberOfBatteriesRequiredDb(int numberOfBatteriesRequiredDb) {
        this.numberOfBatteriesRequiredDb = numberOfBatteriesRequiredDb;
    }

    public String getBatteriesRequired() {
        return batteriesRequired;
    }

    public void setBatteriesRequired(String batteriesRequired) {
        this.batteriesRequired = batteriesRequired;
    }

    public String getIsTheProductABattery() {
        return isTheProductABattery;
    }

    public void setIsTheProductABattery(Map<?, ?> productMap) {
        String key = pgV3Constants.SELECT_ATTRIBUTE_PGISTHEPRODUCTABATTERY;
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            String sIsTheProductABatteryVal = (String) productMap.get(key);
            setIsTheProductABatteryDb(sIsTheProductABatteryVal);
            if (null == sIsTheProductABatteryVal) {
                setIsTheProductABattery(DomainConstants.EMPTY_STRING);
            } else {
                setIsTheProductABattery(sIsTheProductABatteryVal);
            }
        } else {
            setIsTheProductABattery(DomainConstants.EMPTY_STRING);
        }
    }

    public void setIsTheProductABattery(String isTheProductABattery) {
        this.isTheProductABattery = isTheProductABattery;
    }

    public String getIsTheProductABatteryDb() {
        return isTheProductABatteryDb;
    }

    public void setIsTheProductABatteryDb(String isTheProductABatteryDb) {
        this.isTheProductABatteryDb = isTheProductABatteryDb;
    }

    public String getMarketRegistrationRollupFlag() {
        return marketRegistrationRollupFlag;
    }

    public void setMarketRegistrationRollupFlag(String marketRegistrationRollupFlag) {
        this.marketRegistrationRollupFlag = marketRegistrationRollupFlag;
    }

    public String getIngredientStatementRollupFlag() {
        return ingredientStatementRollupFlag;
    }

    public void setIngredientStatementRollupFlag(String ingredientStatementRollupFlag) {
        this.ingredientStatementRollupFlag = ingredientStatementRollupFlag;
    }

    public String getBatteryRollupFlag() {
        return batteryRollupFlag;
    }

    public void setBatteryRollupFlag(String batteryRollupFlag) {
        this.batteryRollupFlag = batteryRollupFlag;
    }

    public String getMarketRegistrationRollupFlagDb() {
        return marketRegistrationRollupFlagDb;
    }

    public void setMarketRegistrationRollupFlagDb(String marketRegistrationRollupFlagDb) {
        this.marketRegistrationRollupFlagDb = marketRegistrationRollupFlagDb;
    }

    public String getIngredientStatementRollupFlagDb() {
        return ingredientStatementRollupFlagDb;
    }

    public void setIngredientStatementRollupFlagDb(String ingredientStatementRollupFlagDb) {
        this.ingredientStatementRollupFlagDb = ingredientStatementRollupFlagDb;
    }

    public String getBatteryRollupFlagDb() {
        return batteryRollupFlagDb;
    }

    public void setBatteryRollupFlagDb(String batteryRollupFlagDb) {
        this.batteryRollupFlagDb = batteryRollupFlagDb;
    }

    public String getEventForRollup() {
        return eventForRollup;
    }

    public void setEventForRollup(String eventForRollup) {
        this.eventForRollup = eventForRollup;
    }

    public String getBaseUnitOfMeasure() {
        return baseUnitOfMeasure;
    }

    public void setBaseUnitOfMeasure(String baseUnitOfMeasure) {
        this.baseUnitOfMeasure = baseUnitOfMeasure;
    }

    //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts

    public String getBomBaseQuantity() {
        return bomBaseQuantity;
    }

    public void setBomBaseQuantity(String bomBaseQuantity) {
        this.bomBaseQuantity = bomBaseQuantity;
    }

    public String getEntryBaseQuantity() {
        return entryBaseQuantity;
    }

    public void setEntryBaseQuantity(String entryBaseQuantity) {
        this.entryBaseQuantity = entryBaseQuantity;
    }

    public boolean isCircularExist() {
        return circularExist;
    }

    public void setCircularExist(boolean circularExist) {
        this.circularExist = circularExist;
    }

    public void setCalculateForRollup(Context context, Map<?, ?> productMap) {
        String key = RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context);
        boolean containKey = productMap.containsKey(key);
        if (containKey) {
            boolean rollVal = Boolean.parseBoolean((String) productMap.get(key));
            setCalculateForRollupFlag(rollVal);
        } else {
            setCalculateForRollupFlag(false);
        }
    }

    public boolean isCalculateForRollupFlag() {
        return calculateForRollup;
    }

    public void setCalculateForRollupFlag(boolean calculateForRollup) {
        this.calculateForRollup = calculateForRollup;
    }

    //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends

    //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Starts

    public StringList getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(StringList childrenList) {
        this.childrenList = childrenList;
    }

    public boolean isPublishDGCToSAP() {
        return publishDGCToSAP;
    }

    public void setPublishDGCToSAP(boolean publishDGCToSAP) {
        this.publishDGCToSAP = publishDGCToSAP;
    }


    //Added by DSM(Sogeti) for 2018x.6 Dec CW Defect #45017 - Ends

    // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - Start
    public boolean isPublishITToSAP() {
        return publishITToSAP;
    }

    public void setPublishITToSAP(boolean publishITToSAP) {
        this.publishITToSAP = publishITToSAP;
    }
     // Modified by DSM (Sogeti) for 22x.05 - REQ 49480 - End

}
