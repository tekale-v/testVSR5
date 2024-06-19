/*
 **   PhysChemBean.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Physical Chemical Bean class.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.models.xml;

import com.pg.dsm.upload.fop.phys_chem.interfaces.bo.IFormulationPart;
import com.pg.dsm.upload.fop.phys_chem.models.bo.BusinessAreaBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductCategoryPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductFormBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductTechnologyPlatformBean;
import matrix.util.StringList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "physChem"
})
@XmlRootElement(name = "physChemBean")
public class PhysChemBean {
    @XmlElement(required = true)
    protected List<PhysChem> physChem;
    @XmlTransient
    protected String formulatedPartName;
    @XmlTransient
    protected String revision;
    @XmlTransient
    protected String productForm;
    @XmlTransient
    protected String color;
    @XmlTransient
    protected String colorIntensity;
    @XmlTransient
    protected String odour;
    @XmlTransient
    protected String closedCupFlashpoint;
    @XmlTransient
    protected String closedCupFlashpointValue;
    @XmlTransient
    protected String boilingPoint;
    @XmlTransient
    protected String boilingPointValue;
    @XmlTransient
    protected String productSustainCombustion;
    @XmlTransient
    protected String productOxidizer;
    @XmlTransient
    protected String oxidizerSodiumPerCarbonate;
    @XmlTransient
    protected String oxidizerHydrogenPeroxide;
    @XmlTransient
    protected String productPotentialToIncreaseBurningRate;
    @XmlTransient
    protected String organicPeroxide;
    @XmlTransient
    protected String availableOxygenContent;
    @XmlTransient
    protected String kinematicViscosity;
    @XmlTransient
    protected String relativeDensityOrSpecificGravity;
    @XmlTransient
    protected String evaporationRate;
    @XmlTransient
    protected String pHDataAvailable;
    @XmlTransient
    protected String isTheLiquidanAqeousSolution;
    @XmlTransient
    protected String pH;
    @XmlTransient
    protected String pHDilution;
    @XmlTransient
    protected String reserveAlkalinity;
    @XmlTransient
    protected String reserveAcidity;
    @XmlTransient
    protected String reserveAlkalinityUoM;
    @XmlTransient
    protected String reserveAlkalinityTitrationEndPoint;
    @XmlTransient
    protected String liquidCorrosiveToMetal;
    @XmlTransient
    protected String technicalBasisForTheCorrosiveToMetals;
    @XmlTransient
    protected String flammableLiquidAbsorbedOrContainedWithInTheSolid;
    @XmlTransient
    protected String burnRate;
    @XmlTransient
    protected String productHaveAnySelfReactiveProperties;
    @XmlTransient
    protected String heatOfDecomposition;
    @XmlTransient
    protected String selfAcceleratingDecompositionTemperature;
    @XmlTransient
    protected String heatOfCombustion;
    @XmlTransient
    protected String canConstruction;
    @XmlTransient
    protected String gaugePressure;
    @XmlTransient
    protected String aerosolType;
    @XmlTransient
    protected String isAerosolTestDataNeeded;
    @XmlTransient
    protected String ignitionDistance;
    @XmlTransient
    protected String enclosedSpaceIgnition;
    @XmlTransient
    protected String flameHeight;
    @XmlTransient
    protected String flameDuration;
    @XmlTransient
    protected String vaporPressure;
    @XmlTransient
    protected String vaporDensity;
    @XmlTransient
    protected String aerosolCanCorrosiveToMetals;
    @XmlTransient
    protected String aerosolConductivityOfTheContents;
    @XmlTransient
    protected String conductivityoftheLiquid;
    @XmlTransient
    protected String flammableOrNonFlammable;
    @XmlTransient
    protected String percentByWeightOfFlammablePropellantInAerosolContainer;
    @XmlTransient
    protected String doesBaseProductContainByVolumeWaterMiscibleAlcohols;
    @XmlTransient
    protected String doesTheBaseProductContainEthanolPlusISOPropanol;
    @XmlTransient
    protected String doesBaseProductSustainCombustion;
    @XmlTransient
    protected String doesBaseProductHaveAFirePoint;
    @XmlTransient
    protected String doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant;
    @XmlTransient
    protected String doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant;
    @XmlTransient
    protected String pHMin;
    @XmlTransient
    protected String pHMax;
    @XmlTransient
    protected String comments;

    @XmlTransient
    List<BusinessAreaBean> requiredSolidBusinessAreaBean;
    @XmlTransient
    List<BusinessAreaBean> requiredLiquidBusinessAreaBean;
    @XmlTransient
    List<BusinessAreaBean> requiredAerosolBusinessAreaBean;

    @XmlTransient
    List<ProductCategoryPlatformBean> requiredSolidProductCategoryPlatformBean;
    @XmlTransient
    List<ProductCategoryPlatformBean> requiredLiquidProductCategoryPlatformBean;
    @XmlTransient
    List<ProductCategoryPlatformBean> requiredAerosolProductCategoryPlatformBean;

    @XmlTransient
    List<ProductTechnologyPlatformBean> requiredSolidProductTechnologyPlatformBean;
    @XmlTransient
    List<ProductTechnologyPlatformBean> requiredLiquidProductTechnologyPlatformBean;
    @XmlTransient
    List<ProductTechnologyPlatformBean> requiredAerosolProductTechnologyPlatformBean;
    @XmlTransient
    boolean beanExist;
    @XmlTransient
    IFormulationPart formulationPartBean;
    @XmlTransient
    List<String> errorMessageList;
    @XmlTransient
    StringList validateAttributeList;
    @XmlTransient
    ProductFormBean productFormObject;
    //Modified as part of 2018x.6 - Starts
    @XmlTransient
    List<BusinessAreaBean> requiredLiquidBusinessAreaReserveAlkalinityBean;
	//Modified as part of 2018x.6 - Ends
    public List<PhysChem> getPhysChem() {
        if (physChem == null) {
            physChem = new ArrayList<>();
        }
        return this.physChem;
    }

    public void setPhysChem(List<PhysChem> physChem) {
        this.physChem = physChem;
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

    public String getProductForm() {
        return productForm;
    }

    public void setProductForm(String productForm) {
        this.productForm = productForm;
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

    public boolean isBeanExist() {
        return beanExist;
    }

    public void setBeanExist(boolean beanExist) {
        this.beanExist = beanExist;
    }

    public IFormulationPart getFormulationPartBean() {
        return formulationPartBean;
    }

    public void setFormulationPartBean(IFormulationPart formulationPartBean) {
        this.formulationPartBean = formulationPartBean;
    }

    public StringList getValidateAttributeList() {
        return validateAttributeList;
    }

    public void setValidateAttributeList(StringList validateAttributeList) {
        this.validateAttributeList = validateAttributeList;
    }

    public ProductFormBean getProductFormObject() {
        return productFormObject;
    }

    public void setProductFormObject(ProductFormBean productFormObject) {
        this.productFormObject = productFormObject;
    }

    public List<String> getErrorMessageList() {
        return errorMessageList;
    }

    public void setErrorMessageList(List<String> errorMessageList) {
        this.errorMessageList = errorMessageList;
    }

    public List<BusinessAreaBean> getRequiredSolidBusinessAreaBean() {
        return requiredSolidBusinessAreaBean;
    }

    public void setRequiredSolidBusinessAreaBean(List<BusinessAreaBean> requiredSolidBusinessAreaBean) {
        this.requiredSolidBusinessAreaBean = requiredSolidBusinessAreaBean;
    }

    public List<BusinessAreaBean> getRequiredLiquidBusinessAreaBean() {
        return requiredLiquidBusinessAreaBean;
    }

    public void setRequiredLiquidBusinessAreaBean(List<BusinessAreaBean> requiredLiquidBusinessAreaBean) {
        this.requiredLiquidBusinessAreaBean = requiredLiquidBusinessAreaBean;
    }

    public List<BusinessAreaBean> getRequiredAerosolBusinessAreaBean() {
        return requiredAerosolBusinessAreaBean;
    }

    public void setRequiredAerosolBusinessAreaBean(List<BusinessAreaBean> requiredAerosolBusinessAreaBean) {
        this.requiredAerosolBusinessAreaBean = requiredAerosolBusinessAreaBean;
    }

    public List<ProductCategoryPlatformBean> getRequiredSolidProductCategoryPlatformBean() {
        return requiredSolidProductCategoryPlatformBean;
    }

    public void setRequiredSolidProductCategoryPlatformBean(List<ProductCategoryPlatformBean> requiredSolidProductCategoryPlatformBean) {
        this.requiredSolidProductCategoryPlatformBean = requiredSolidProductCategoryPlatformBean;
    }

    public List<ProductCategoryPlatformBean> getRequiredLiquidProductCategoryPlatformBean() {
        return requiredLiquidProductCategoryPlatformBean;
    }

    public void setRequiredLiquidProductCategoryPlatformBean(List<ProductCategoryPlatformBean> requiredLiquidProductCategoryPlatformBean) {
        this.requiredLiquidProductCategoryPlatformBean = requiredLiquidProductCategoryPlatformBean;
    }

    public List<ProductCategoryPlatformBean> getRequiredAerosolProductCategoryPlatformBean() {
        return requiredAerosolProductCategoryPlatformBean;
    }

    public void setRequiredAerosolProductCategoryPlatformBean(List<ProductCategoryPlatformBean> requiredAerosolProductCategoryPlatformBean) {
        this.requiredAerosolProductCategoryPlatformBean = requiredAerosolProductCategoryPlatformBean;
    }

    public List<ProductTechnologyPlatformBean> getRequiredSolidProductTechnologyPlatformBean() {
        return requiredSolidProductTechnologyPlatformBean;
    }

    public void setRequiredSolidProductTechnologyPlatformBean(List<ProductTechnologyPlatformBean> requiredSolidProductTechnologyPlatformBean) {
        this.requiredSolidProductTechnologyPlatformBean = requiredSolidProductTechnologyPlatformBean;
    }

    public List<ProductTechnologyPlatformBean> getRequiredLiquidProductTechnologyPlatformBean() {
        return requiredLiquidProductTechnologyPlatformBean;
    }

    public void setRequiredLiquidProductTechnologyPlatformBean(List<ProductTechnologyPlatformBean> requiredLiquidProductTechnologyPlatformBean) {
        this.requiredLiquidProductTechnologyPlatformBean = requiredLiquidProductTechnologyPlatformBean;
    }

    public List<ProductTechnologyPlatformBean> getRequiredAerosolProductTechnologyPlatformBean() {
        return requiredAerosolProductTechnologyPlatformBean;
    }

    public void setRequiredAerosolProductTechnologyPlatformBean(List<ProductTechnologyPlatformBean> requiredAerosolProductTechnologyPlatformBean) {
        this.requiredAerosolProductTechnologyPlatformBean = requiredAerosolProductTechnologyPlatformBean;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    //Modified as part of 2018x.6 - Starts
	public List<BusinessAreaBean> getRequiredLiquidBusinessAreaReserveAlkalinityBean() {
		return requiredLiquidBusinessAreaReserveAlkalinityBean;
	}

	public void setRequiredLiquidBusinessAreaReserveAlkalinityBean(
			List<BusinessAreaBean> requiredLiquidBusinessAreaReserveAlkalinityBean) {
		this.requiredLiquidBusinessAreaReserveAlkalinityBean = requiredLiquidBusinessAreaReserveAlkalinityBean;
	}
	//Modified as part of 2018x.6 - Ends
}
