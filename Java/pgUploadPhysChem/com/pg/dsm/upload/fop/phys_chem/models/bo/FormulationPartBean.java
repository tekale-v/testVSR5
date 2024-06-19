/*
 **   FormulationPartBean.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Formulation Part Bean class.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.models.bo;

import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.upload.fop.enumeration.FormulationAttributeConstant;
import com.pg.dsm.upload.fop.enumeration.FormulationGeneralConstant;
import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPart;
import com.pg.dsm.upload.fop.phys_chem.models.PhysChemContext;
import matrix.db.Context;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class FormulationPartBean implements IFormulationPart {

    String id;
    String type;
    String name;
    String currentState;
    String policy;
    String vault;
    String formulatedPartName;
    String revision;
    String productForm;
    String color;
    String colorIntensity;
    String odour;
    String closedCupFlashpoint;
    String closedCupFlashpointValue;
    String boilingPoint;
    String boilingPointValue;
    String productSustainCombustion;
    String productOxidizer;
    String oxidizerSodiumPerCarbonate;
    String oxidizerHydrogenPeroxide;
    String productPotentialToIncreaseBurningRate;
    String organicPeroxide;
    String availableOxygenContent;
    String kinematicViscosity;
    String relativeDensityOrSpecificGravity;
    String evaporationRate;
    String pHDataAvailable;
    String isTheLiquidanAqeousSolution;
    String pH;
    String pHMin;
    String pHMax;
    String pHDilution;
    String reserveAlkalinity;
    String reserveAcidity;
    String reserveAlkalinityUoM;
    String reserveAlkalinityTitrationEndPoint;
    String liquidCorrosiveToMetal;
    String technicalBasisForTheCorrosiveToMetals;
    String flammableLiquidAbsorbedOrContainedWithInTheSolid;
    String burnRate;
    String productHaveAnySelfReactiveProperties;
    String heatOfDecomposition;
    String selfAcceleratingDecompositionTemperature;
    String heatOfCombustion;
    String canConstruction;
    String gaugePressure;
    String aerosolType;
    String isAerosolTestDataNeeded;
    String ignitionDistance;
    String enclosedSpaceIgnition;
    String flameHeight;
    String flameDuration;
    String vaporPressure;
    String vaporDensity;
    String aerosolCanCorrosiveToMetals;
    String aerosolConductivityOfTheContents;
    String conductivityoftheLiquid;
    String flammableOrNonFlammable;
    String percentByWeightOfFlammablePropellantInAerosolContainer;
    String doesBaseProductContainByVolumeWaterMiscibleAlcohols;
    String doesTheBaseProductContainEthanolPlusISOPropanol;
    String doesBaseProductSustainCombustion;
    String doesBaseProductHaveAFirePoint;
    String doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant;
    String doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant;
    String comments;
    List<ProductFormBean> connectedProductFormBeans;
    List<BusinessAreaBean> connectedBusinessAreaBeans;
    List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans;
    List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans;
    //Modified as part of 2018x.6 - Starts
    String rollupFlag;
    String productFormRelId;
	//Modified as part of 2018x.6 - Ends
    
    public FormulationPartBean() {
    }

    public FormulationPartBean(Map<?, ?> map) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        logger.info("FormulationPartBean Instance");
        Context context = PhysChemContext.getContext();
        setId((String) map.get(DomainConstants.SELECT_ID));
        setType((String) map.get(DomainConstants.SELECT_TYPE));
        setName((String) map.get(DomainConstants.SELECT_NAME));
        setCurrentState((String) map.get(DomainConstants.SELECT_CURRENT));
        setPolicy((String) map.get(DomainConstants.SELECT_POLICY));
        setVault((String) map.get(DomainConstants.SELECT_VAULT));
        setFormulatedPartName((String) map.get(DomainConstants.SELECT_NAME));
        setRevision((String) map.get(DomainConstants.SELECT_REVISION));
        setColor((String) map.get(FormulationAttributeConstant.COLOR.getAttributeSelect(context)));
        setColorIntensity((String) map.get(FormulationAttributeConstant.COLOR_INTENSITY.getAttributeSelect(context)));
        setOdour((String) map.get(FormulationAttributeConstant.ODOUR.getAttributeSelect(context)));
        setClosedCupFlashpoint((String) map.get(FormulationAttributeConstant.CLOSED_CUP_FLASH_POINT.getAttributeSelect(context)));
        setClosedCupFlashpointValue((String) map.get(FormulationAttributeConstant.CLOSED_CUP_FLASH_POINT_VALUE.getAttributeSelect(context)));
        setBoilingPoint((String) map.get(FormulationAttributeConstant.BOILING_POINT.getAttributeSelect(context)));
        setBoilingPointValue((String) map.get(FormulationAttributeConstant.BOILING_POINT_VALUE.getAttributeSelect(context)));
        setProductSustainCombustion((String) map.get(FormulationAttributeConstant.PRODUCT_SUSTAIN_COMBUSTION.getAttributeSelect(context)));
        setProductOxidizer((String) map.get(FormulationAttributeConstant.PRODUCT_OXIDIZER.getAttributeSelect(context)));
        setOxidizerSodiumPerCarbonate((String) map.get(FormulationAttributeConstant.OXIDIZER_SODIUM_PER_CARBONATE.getAttributeSelect(context)));
        setOxidizerHydrogenPeroxide((String) map.get(FormulationAttributeConstant.OXIDIZER_HYDROGEN_PEROXIDE.getAttributeSelect(context)));
        setProductPotentialToIncreaseBurningRate((String) map.get(FormulationAttributeConstant.PRODUCT_POTENTIAL_TO_INCREASE_BURNING_RATE.getAttributeSelect(context)));
        setOrganicPeroxide((String) map.get(FormulationAttributeConstant.ORGANIC_PEROXIDE.getAttributeSelect(context)));
        setAvailableOxygenContent((String) map.get(FormulationAttributeConstant.AVAILABLE_OXYGEN_CONTENT.getAttributeSelect(context)));
        setKinematicViscosity((String) map.get(FormulationAttributeConstant.KINEMATIC_VISCOSITY.getAttributeSelect(context)));
        setRelativeDensityOrSpecificGravity((String) map.get(FormulationAttributeConstant.RELATIVE_DENSITY_OR_SPECIFIC_GRAVITY.getAttributeSelect(context)));
        setEvaporationRate((String) map.get(FormulationAttributeConstant.EVAPORATION_RATE.getAttributeSelect(context)));
        setPHDataAvailable((String) map.get(FormulationAttributeConstant.PH_DATA_AVAILABLE.getAttributeSelect(context)));
        setIsTheLiquidanAqeousSolution((String) map.get(FormulationAttributeConstant.IS_LIQUID_AN_AQEOUS_SOLUTION.getAttributeSelect(context)));
        setPH((String) map.get(FormulationAttributeConstant.PH.getAttributeSelect(context)));
        setPHMin((String) map.get(FormulationAttributeConstant.PH_MIN.getAttributeSelect(context)));
        setPHMax((String) map.get(FormulationAttributeConstant.PH_MAX.getAttributeSelect(context)));
        setPHDilution((String) map.get(FormulationAttributeConstant.PH_DILUTION.getAttributeSelect(context)));
        setReserveAlkalinity((String) map.get(FormulationAttributeConstant.RESERVE_ALKALINITY.getAttributeSelect(context)));
        setReserveAcidity((String) map.get(FormulationAttributeConstant.RESERVE_ACIDITY.getAttributeSelect(context)));
        setReserveAlkalinityUoM((String) map.get(FormulationAttributeConstant.RESERVE_ALKALINITY_ACIDITY_UOM.getAttributeSelect(context)));
        setReserveAlkalinityTitrationEndPoint((String) map.get(FormulationAttributeConstant.RESERVE_ALKALINITY_TITRATION_ENDPOINT.getAttributeSelect(context)));
        setLiquidCorrosiveToMetal((String) map.get(FormulationAttributeConstant.LIQUID_CORROSIVE_TO_METAL.getAttributeSelect(context)));
        setTechnicalBasisForTheCorrosiveToMetals((String) map.get(FormulationAttributeConstant.TECHNICAL_BASIS_FOR_THE_CORROSIVE_TO_METALS.getAttributeSelect(context)));
        setFlammableLiquidAbsorbedOrContainedWithInTheSolid((String) map.get(FormulationAttributeConstant.FLAMMABLE_LIQUID_ABSORBED_OR_CONTAINED_WITHIN_THE_SOLID.getAttributeSelect(context)));
        setBurnRate((String) map.get(FormulationAttributeConstant.BURN_RATE.getAttributeSelect(context)));
        setProductHaveAnySelfReactiveProperties((String) map.get(FormulationAttributeConstant.PRODUCT_HAVE_ANY_SELF_REACTIVE_PROPERTIES.getAttributeSelect(context)));
        setHeatOfDecomposition((String) map.get(FormulationAttributeConstant.HEAT_OF_DECOMPOSITION.getAttributeSelect(context)));
        setSelfAcceleratingDecompositionTemperature((String) map.get(FormulationAttributeConstant.SELF_ACCELERATING_DECOMPOSITION_TEMPERATURE.getAttributeSelect(context)));
        setHeatOfCombustion((String) map.get(FormulationAttributeConstant.HEAT_OF_COMBUSTION.getAttributeSelect(context)));
        setCanConstruction((String) map.get(FormulationAttributeConstant.CAN_CONSTRUCTION.getAttributeSelect(context)));
        setGaugePressure((String) map.get(FormulationAttributeConstant.GUAGE_PRESSURE.getAttributeSelect(context)));
        setAerosolType((String) map.get(FormulationAttributeConstant.AEROSOL_TYPE.getAttributeSelect(context)));
        setIsAerosolTestDataNeeded((String) map.get(FormulationAttributeConstant.IS_AEROSOL_TEST_DATA_NEEDED.getAttributeSelect(context)));
        setIgnitionDistance((String) map.get(FormulationAttributeConstant.IGNITION_DISTANCE.getAttributeSelect(context)));
        setEnclosedSpaceIgnition((String) map.get(FormulationAttributeConstant.ENCLOSED_SPACE_IGNITION.getAttributeSelect(context)));
        setFlameHeight((String) map.get(FormulationAttributeConstant.FLAME_HEIGHT.getAttributeSelect(context)));
        setFlameDuration((String) map.get(FormulationAttributeConstant.FLAME_DURATION.getAttributeSelect(context)));
        setVaporPressure((String) map.get(FormulationAttributeConstant.VAPOR_PRESSURE.getAttributeSelect(context)));
        setVaporDensity((String) map.get(FormulationAttributeConstant.VAPOR_DENSITY.getAttributeSelect(context)));
        setAerosolCanCorrosiveToMetals((String) map.get(FormulationAttributeConstant.AEROSOL_CAN_CORROSIVE_TO_METALS.getAttributeSelect(context)));
        setAerosolConductivityOfTheContents((String) map.get(FormulationAttributeConstant.AEROSOL_CONDUCTIVITY_OF_THE_CONTENTS.getAttributeSelect(context)));
        setConductivityoftheLiquid((String) map.get(FormulationAttributeConstant.CONDUCTIVITY_OF_THE_LIQUID.getAttributeSelect(context)));
        setFlammableOrNonFlammable((String) map.get(FormulationAttributeConstant.FLAMMABLE_OR_NON_FLAMMABLE.getAttributeSelect(context)));
        setPercentByWeightOfFlammablePropellantInAerosolContainer((String) map.get(FormulationAttributeConstant.PERCENTAGE_BY_WEIGHT_OF_FLAMMABLE_PROPELLANT_IN_AEROSOL_CONTAINER.getAttributeSelect(context)));
        setDoesBaseProductContainByVolumeWaterMiscibleAlcohols((String) map.get(FormulationAttributeConstant.DOES_BASE_PRODUCT_CONTAIN_BY_VOLUME_WATER_MISCIBLE_ALCOHOLS.getAttributeSelect(context)));
        setDoesTheBaseProductContainEthanolPlusISOPropanol((String) map.get(FormulationAttributeConstant.DOES_THE_BASE_PRODUCT_CONTAIN_ETHANOL_PLUS_ISO_PROPANOL.getAttributeSelect(context)));
        setDoesBaseProductSustainCombustion((String) map.get(FormulationAttributeConstant.DOES_BASE_PRODUCT_SUSTAIN_COMBUSTION.getAttributeSelect(context)));
        setDoesBaseProductHaveAFirePoint((String) map.get(FormulationAttributeConstant.DOES_BASE_PRODUCT_HAVE_A_FIRE_POINT.getAttributeSelect(context)));
        setDoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant((String) map.get(FormulationAttributeConstant.DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_NON_FLAMMABLE_GAS_PROPELLANT.getAttributeSelect(context)));
        setDoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant((String) map.get(FormulationAttributeConstant.DOES_BASE_PRODUCT_CONTAIN_EMULSIFIED_FLAMMABLE_GAS_PROPELLANT.getAttributeSelect(context)));

        setConnectedProductFormBeans((List<ProductFormBean>) map.get(FormulationGeneralConstant.CONST_CONNECTED_PRODUCT_FORMS.getValue()));
        setConnectedBusinessAreaBeans((List<BusinessAreaBean>) map.get(FormulationGeneralConstant.CONST_CONNECTED_BUSINESS_AREA.getValue()));
        setConnectedProductCategoryPlatformBeans((List<ProductCategoryPlatformBean>) map.get(FormulationGeneralConstant.CONST_CONNECTED_PRODUCT_CATEGORY_PLATFORM.getValue()));
        setConnectedProductTechnologyPlatformBeans((List<ProductTechnologyPlatformBean>) map.get(FormulationGeneralConstant.CONST_CONNECTED_PRODUCT_TECHNOLOGY_PLATFORM.getValue()));

        setComments((String) map.get(FormulationAttributeConstant.COMMENT.getAttributeSelect(context)));

        // for restore excel creation - usage as getter.
        setProductForm();
        //Modified as part of 2018x.6 - Starts
        setRollupFlag((String) map.get(FormulationAttributeConstant.ROLLUP_FLAG.getAttributeSelect(context)));
        setProductFormRelId();
		//Modified as part of 2018x.6 - Ends
    }
	public static FormulationPartBean getInstance(Map<?, ?> map) {
        return new FormulationPartBean(map);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    @Override
    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    @Override
    public String getVault() {
        return vault;
    }

    public void setVault(String vault) {
        this.vault = vault;
    }

    public String getFormulatedPartName() {
        return formulatedPartName;
    }

    public void setFormulatedPartName(String formulatedPartName) {
        this.formulatedPartName = formulatedPartName;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorIntensity() {
        return colorIntensity;
    }

    public void setColorIntensity(String colorIntensity) {
        this.colorIntensity = colorIntensity;
    }

    public String getOdour() {
        return odour;
    }

    public void setOdour(String odour) {
        this.odour = odour;
    }

    public String getClosedCupFlashpoint() {
        return closedCupFlashpoint;
    }

    public void setClosedCupFlashpoint(String closedCupFlashpoint) {
        this.closedCupFlashpoint = closedCupFlashpoint;
    }

    public String getClosedCupFlashpointValue() {
        return closedCupFlashpointValue;
    }

    public void setClosedCupFlashpointValue(String closedCupFlashpointValue) {
        this.closedCupFlashpointValue = closedCupFlashpointValue;
    }

    public String getBoilingPoint() {
        return boilingPoint;
    }

    public void setBoilingPoint(String boilingPoint) {
        this.boilingPoint = boilingPoint;
    }

    public String getBoilingPointValue() {
        return boilingPointValue;
    }

    public void setBoilingPointValue(String boilingPointValue) {
        this.boilingPointValue = boilingPointValue;
    }

    public String getProductSustainCombustion() {
        return productSustainCombustion;
    }

    public void setProductSustainCombustion(String productSustainCombustion) {
        this.productSustainCombustion = productSustainCombustion;
    }

    public String getProductOxidizer() {
        return productOxidizer;
    }

    public void setProductOxidizer(String productOxidizer) {
        this.productOxidizer = productOxidizer;
    }

    public String getOxidizerSodiumPerCarbonate() {
        return oxidizerSodiumPerCarbonate;
    }

    public void setOxidizerSodiumPerCarbonate(String oxidizerSodiumPerCarbonate) {
        this.oxidizerSodiumPerCarbonate = oxidizerSodiumPerCarbonate;
    }

    public String getOxidizerHydrogenPeroxide() {
        return oxidizerHydrogenPeroxide;
    }

    public void setOxidizerHydrogenPeroxide(String oxidizerHydrogenPeroxide) {
        this.oxidizerHydrogenPeroxide = oxidizerHydrogenPeroxide;
    }

    public String getProductPotentialToIncreaseBurningRate() {
        return productPotentialToIncreaseBurningRate;
    }

    public void setProductPotentialToIncreaseBurningRate(String productPotentialToIncreaseBurningRate) {
        this.productPotentialToIncreaseBurningRate = productPotentialToIncreaseBurningRate;
    }

    public String getOrganicPeroxide() {
        return organicPeroxide;
    }

    public void setOrganicPeroxide(String organicPeroxide) {
        this.organicPeroxide = organicPeroxide;
    }

    public String getAvailableOxygenContent() {
        return availableOxygenContent;
    }

    public void setAvailableOxygenContent(String availableOxygenContent) {
        this.availableOxygenContent = availableOxygenContent;
    }

    public String getKinematicViscosity() {
        return kinematicViscosity;
    }

    public void setKinematicViscosity(String kinematicViscosity) {
        this.kinematicViscosity = kinematicViscosity;
    }

    public String getRelativeDensityOrSpecificGravity() {
        return relativeDensityOrSpecificGravity;
    }

    public void setRelativeDensityOrSpecificGravity(String relativeDensityOrSpecificGravity) {
        this.relativeDensityOrSpecificGravity = relativeDensityOrSpecificGravity;
    }

    public String getEvaporationRate() {
        return evaporationRate;
    }

    public void setEvaporationRate(String evaporationRate) {
        this.evaporationRate = evaporationRate;
    }

    public String getPHDataAvailable() {
        return pHDataAvailable;
    }

    public void setPHDataAvailable(String pHDataAvailable) {
        this.pHDataAvailable = pHDataAvailable;
    }

    public String getIsTheLiquidanAqeousSolution() {
        return isTheLiquidanAqeousSolution;
    }

    public void setIsTheLiquidanAqeousSolution(String isTheLiquidanAqeousSolution) {
        this.isTheLiquidanAqeousSolution = isTheLiquidanAqeousSolution;
    }

    public String getPH() {
        return pH;
    }

    public void setPH(String pH) {
        this.pH = pH;
    }

    public String getPHDilution() {
        return pHDilution;
    }

    public void setPHDilution(String pHDilution) {
        this.pHDilution = pHDilution;
    }

    public String getReserveAlkalinity() {
        return reserveAlkalinity;
    }

    public void setReserveAlkalinity(String reserveAlkalinity) {
        this.reserveAlkalinity = reserveAlkalinity;
    }

    public String getReserveAcidity() {
        return reserveAcidity;
    }

    public void setReserveAcidity(String reserveAcidity) {
        this.reserveAcidity = reserveAcidity;
    }

    public String getReserveAlkalinityUoM() {
        return reserveAlkalinityUoM;
    }

    public void setReserveAlkalinityUoM(String reserveAlkalinityUoM) {
        this.reserveAlkalinityUoM = reserveAlkalinityUoM;
    }

    public String getReserveAlkalinityTitrationEndPoint() {
        return reserveAlkalinityTitrationEndPoint;
    }

    public void setReserveAlkalinityTitrationEndPoint(String reserveAlkalinityTitrationEndPoint) {
        this.reserveAlkalinityTitrationEndPoint = reserveAlkalinityTitrationEndPoint;
    }

    public String getLiquidCorrosiveToMetal() {
        return liquidCorrosiveToMetal;
    }

    public void setLiquidCorrosiveToMetal(String liquidCorrosiveToMetal) {
        this.liquidCorrosiveToMetal = liquidCorrosiveToMetal;
    }

    public String getTechnicalBasisForTheCorrosiveToMetals() {
        return technicalBasisForTheCorrosiveToMetals;
    }

    public void setTechnicalBasisForTheCorrosiveToMetals(String technicalBasisForTheCorrosiveToMetals) {
        this.technicalBasisForTheCorrosiveToMetals = technicalBasisForTheCorrosiveToMetals;
    }

    public String getFlammableLiquidAbsorbedOrContainedWithInTheSolid() {
        return flammableLiquidAbsorbedOrContainedWithInTheSolid;
    }

    public void setFlammableLiquidAbsorbedOrContainedWithInTheSolid(String flammableLiquidAbsorbedOrContainedWithInTheSolid) {
        this.flammableLiquidAbsorbedOrContainedWithInTheSolid = flammableLiquidAbsorbedOrContainedWithInTheSolid;
    }

    public String getBurnRate() {
        return burnRate;
    }

    public void setBurnRate(String burnRate) {
        this.burnRate = burnRate;
    }

    public String getProductHaveAnySelfReactiveProperties() {
        return productHaveAnySelfReactiveProperties;
    }

    public void setProductHaveAnySelfReactiveProperties(String productHaveAnySelfReactiveProperties) {
        this.productHaveAnySelfReactiveProperties = productHaveAnySelfReactiveProperties;
    }

    public String getHeatOfDecomposition() {
        return heatOfDecomposition;
    }

    public void setHeatOfDecomposition(String heatOfDecomposition) {
        this.heatOfDecomposition = heatOfDecomposition;
    }

    public String getSelfAcceleratingDecompositionTemperature() {
        return selfAcceleratingDecompositionTemperature;
    }

    public void setSelfAcceleratingDecompositionTemperature(String selfAcceleratingDecompositionTemperature) {
        this.selfAcceleratingDecompositionTemperature = selfAcceleratingDecompositionTemperature;
    }

    public String getHeatOfCombustion() {
        return heatOfCombustion;
    }

    public void setHeatOfCombustion(String heatOfCombustion) {
        this.heatOfCombustion = heatOfCombustion;
    }

    public String getCanConstruction() {
        return canConstruction;
    }

    public void setCanConstruction(String canConstruction) {
        this.canConstruction = canConstruction;
    }

    public String getGaugePressure() {
        return gaugePressure;
    }

    public void setGaugePressure(String gaugePressure) {
        this.gaugePressure = gaugePressure;
    }

    public String getAerosolType() {
        return aerosolType;
    }

    public void setAerosolType(String aerosolType) {
        this.aerosolType = aerosolType;
    }

    public String getIsAerosolTestDataNeeded() {
        return isAerosolTestDataNeeded;
    }

    public void setIsAerosolTestDataNeeded(String isAerosolTestDataNeeded) {
        this.isAerosolTestDataNeeded = isAerosolTestDataNeeded;
    }

    public String getIgnitionDistance() {
        return ignitionDistance;
    }

    public void setIgnitionDistance(String ignitionDistance) {
        this.ignitionDistance = ignitionDistance;
    }

    public String getEnclosedSpaceIgnition() {
        return enclosedSpaceIgnition;
    }

    public void setEnclosedSpaceIgnition(String enclosedSpaceIgnition) {
        this.enclosedSpaceIgnition = enclosedSpaceIgnition;
    }

    public String getFlameHeight() {
        return flameHeight;
    }

    public void setFlameHeight(String flameHeight) {
        this.flameHeight = flameHeight;
    }

    public String getFlameDuration() {
        return flameDuration;
    }

    public void setFlameDuration(String flameDuration) {
        this.flameDuration = flameDuration;
    }

    public String getVaporPressure() {
        return vaporPressure;
    }

    public void setVaporPressure(String vaporPressure) {
        this.vaporPressure = vaporPressure;
    }

    public String getVaporDensity() {
        return vaporDensity;
    }

    public void setVaporDensity(String vaporDensity) {
        this.vaporDensity = vaporDensity;
    }

    public String getAerosolCanCorrosiveToMetals() {
        return aerosolCanCorrosiveToMetals;
    }

    public void setAerosolCanCorrosiveToMetals(String aerosolCanCorrosiveToMetals) {
        this.aerosolCanCorrosiveToMetals = aerosolCanCorrosiveToMetals;
    }

    public String getAerosolConductivityOfTheContents() {
        return aerosolConductivityOfTheContents;
    }

    public void setAerosolConductivityOfTheContents(String aerosolConductivityOfTheContents) {
        this.aerosolConductivityOfTheContents = aerosolConductivityOfTheContents;
    }

    public String getConductivityoftheLiquid() {
        return conductivityoftheLiquid;
    }

    public void setConductivityoftheLiquid(String conductivityoftheLiquid) {
        this.conductivityoftheLiquid = conductivityoftheLiquid;
    }

    public String getFlammableOrNonFlammable() {
        return flammableOrNonFlammable;
    }

    public void setFlammableOrNonFlammable(String flammableOrNonFlammable) {
        this.flammableOrNonFlammable = flammableOrNonFlammable;
    }

    public String getPercentByWeightOfFlammablePropellantInAerosolContainer() {
        return percentByWeightOfFlammablePropellantInAerosolContainer;
    }

    public void setPercentByWeightOfFlammablePropellantInAerosolContainer(String percentByWeightOfFlammablePropellantInAerosolContainer) {
        this.percentByWeightOfFlammablePropellantInAerosolContainer = percentByWeightOfFlammablePropellantInAerosolContainer;
    }

    public String getDoesBaseProductContainByVolumeWaterMiscibleAlcohols() {
        return doesBaseProductContainByVolumeWaterMiscibleAlcohols;
    }

    public void setDoesBaseProductContainByVolumeWaterMiscibleAlcohols(String doesBaseProductContainByVolumeWaterMiscibleAlcohols) {
        this.doesBaseProductContainByVolumeWaterMiscibleAlcohols = doesBaseProductContainByVolumeWaterMiscibleAlcohols;
    }

    public String getDoesTheBaseProductContainEthanolPlusISOPropanol() {
        return doesTheBaseProductContainEthanolPlusISOPropanol;
    }

    public void setDoesTheBaseProductContainEthanolPlusISOPropanol(String doesTheBaseProductContainEthanolPlusISOPropanol) {
        this.doesTheBaseProductContainEthanolPlusISOPropanol = doesTheBaseProductContainEthanolPlusISOPropanol;
    }

    public String getDoesBaseProductSustainCombustion() {
        return doesBaseProductSustainCombustion;
    }

    public void setDoesBaseProductSustainCombustion(String doesBaseProductSustainCombustion) {
        this.doesBaseProductSustainCombustion = doesBaseProductSustainCombustion;
    }

    public String getDoesBaseProductHaveAFirePoint() {
        return doesBaseProductHaveAFirePoint;
    }

    public void setDoesBaseProductHaveAFirePoint(String doesBaseProductHaveAFirePoint) {
        this.doesBaseProductHaveAFirePoint = doesBaseProductHaveAFirePoint;
    }

    public String getDoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant() {
        return doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant;
    }

    public void setDoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant(String doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant) {
        this.doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant = doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant;
    }

    public String getDoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant() {
        return doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant;
    }

    public void setDoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant(String doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant) {
        this.doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant = doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant;
    }

    public String getPHMin() {
        return pHMin;
    }

    public void setPHMin(String pHMin) {
        this.pHMin = pHMin;
    }

    public String getPHMax() {
        return pHMax;
    }

    public void setPHMax(String pHMax) {
        this.pHMax = pHMax;
    }

    @Override
    public String getComments() {
        return comments;
    }

    @Override
    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<ProductFormBean> getConnectedProductFormBeans() {
        return connectedProductFormBeans;
    }

    public void setConnectedProductFormBeans(List<ProductFormBean> connectedProductFormBeans) {
        this.connectedProductFormBeans = connectedProductFormBeans;
    }

    public List<ProductCategoryPlatformBean> getConnectedProductCategoryPlatformBeans() {
        return connectedProductCategoryPlatformBeans;
    }

    public void setConnectedProductCategoryPlatformBeans(List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans) {
        this.connectedProductCategoryPlatformBeans = connectedProductCategoryPlatformBeans;
    }

    public List<ProductTechnologyPlatformBean> getConnectedProductTechnologyPlatformBeans() {
        return connectedProductTechnologyPlatformBeans;
    }

    public void setConnectedProductTechnologyPlatformBeans(List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans) {
        this.connectedProductTechnologyPlatformBeans = connectedProductTechnologyPlatformBeans;
    }

    public List<BusinessAreaBean> getConnectedBusinessAreaBeans() {
        return connectedBusinessAreaBeans;
    }

    public void setConnectedBusinessAreaBeans(List<BusinessAreaBean> connectedBusinessAreaBeans) {
        this.connectedBusinessAreaBeans = connectedBusinessAreaBeans;
    }

    public String getProductForm() {
        return productForm;
    }

    public void setProductForm() {
        if (null != this.connectedProductFormBeans && !connectedProductFormBeans.isEmpty()) {
            ProductFormBean productFormBeanObj = connectedProductFormBeans.get(0);
            this.productForm = productFormBeanObj.getName();
        } else {
            this.productForm = DomainConstants.EMPTY_STRING;
        }
    }
	//Modified as part of 2018x.6 - Starts   
    public String getProductFormRelId() {
		return productFormRelId;
	}

	public void setProductFormRelId() {
		if (null != this.connectedProductFormBeans && !connectedProductFormBeans.isEmpty()) {
            ProductFormBean productFormBeanObj = connectedProductFormBeans.get(0);
            this.productFormRelId = productFormBeanObj.getRelId();
        } else {
            this.productFormRelId = DomainConstants.EMPTY_STRING;
        }
	}

	public String getRollupFlag() {
		return rollupFlag;
	}

	public void setRollupFlag(String rollupFlag) {
		this.rollupFlag = rollupFlag;
	}
	//Modified as part of 2018x.6 - Ends
}
