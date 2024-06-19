/*
 **   IFormulationPart.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Formulation Part Interface.
 **
 */

package com.pg.dsm.upload.fop.phys_chem.interfaces.bo;

import com.pg.dsm.upload.fop.phys_chem.models.bo.BusinessAreaBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductCategoryPlatformBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductFormBean;
import com.pg.dsm.upload.fop.phys_chem.models.bo.ProductTechnologyPlatformBean;

import java.util.List;

public interface IFormulationPart {
    String getId();

    String getType();

    String getName();

    String getCurrentState();

    String getPolicy();

    String getVault();

    String getFormulatedPartName();

    void setFormulatedPartName(String formulatedPartName);

    String getRevision();

    void setRevision(String revision);

    String getProductForm();

    void setProductForm();

    String getColor();

    void setColor(String color);

    String getColorIntensity();

    void setColorIntensity(String colorIntensity);

    String getOdour();

    void setOdour(String odour);

    String getClosedCupFlashpoint();

    void setClosedCupFlashpoint(String closedCupFlashpoint);

    String getClosedCupFlashpointValue();

    void setClosedCupFlashpointValue(String closedCupFlashpointValue);

    String getBoilingPoint();

    void setBoilingPoint(String boilingPoint);

    String getBoilingPointValue();

    void setBoilingPointValue(String boilingPointValue);

    String getProductSustainCombustion();

    void setProductSustainCombustion(String productSustainCombustion);

    String getProductOxidizer();

    void setProductOxidizer(String productOxidizer);

    String getOxidizerSodiumPerCarbonate();

    void setOxidizerSodiumPerCarbonate(String oxidizerSodiumPerCarbonate);

    String getOxidizerHydrogenPeroxide();

    void setOxidizerHydrogenPeroxide(String oxidizerHydrogenPeroxide);

    String getProductPotentialToIncreaseBurningRate();

    void setProductPotentialToIncreaseBurningRate(String productPotentialToIncreaseBurningRate);

    String getOrganicPeroxide();

    void setOrganicPeroxide(String organicPeroxide);

    String getAvailableOxygenContent();

    void setAvailableOxygenContent(String availableOxygenContent);

    String getKinematicViscosity();

    void setKinematicViscosity(String kinematicViscosity);

    String getRelativeDensityOrSpecificGravity();

    void setRelativeDensityOrSpecificGravity(String relativeDensityOrSpecificGravity);

    String getEvaporationRate();

    void setEvaporationRate(String evaporationRate);

    String getPHDataAvailable();

    void setPHDataAvailable(String pHDataAvailable);

    String getIsTheLiquidanAqeousSolution();

    void setIsTheLiquidanAqeousSolution(String isTheLiquidanAqeousSolution);

    String getPH();

    void setPH(String pH);

    String getPHDilution();

    void setPHDilution(String pHDilution);

    String getReserveAlkalinity();

    void setReserveAlkalinity(String reserveAlkalinity);

    String getReserveAcidity();

    void setReserveAcidity(String reserveAcidity);

    String getReserveAlkalinityUoM();

    void setReserveAlkalinityUoM(String reserveAlkalinityUoM);

    String getReserveAlkalinityTitrationEndPoint();

    void setReserveAlkalinityTitrationEndPoint(String reserveAlkalinityTitrationEndPoint);

    String getLiquidCorrosiveToMetal();

    void setLiquidCorrosiveToMetal(String liquidCorrosiveToMetal);

    String getTechnicalBasisForTheCorrosiveToMetals();

    void setTechnicalBasisForTheCorrosiveToMetals(String technicalBasisForTheCorrosiveToMetals);

    String getFlammableLiquidAbsorbedOrContainedWithInTheSolid();

    void setFlammableLiquidAbsorbedOrContainedWithInTheSolid(String flammableLiquidAbsorbedOrContainedWithInTheSolid);

    String getBurnRate();

    void setBurnRate(String burnRate);

    String getProductHaveAnySelfReactiveProperties();

    void setProductHaveAnySelfReactiveProperties(String productHaveAnySelfReactiveProperties);

    String getHeatOfDecomposition();

    void setHeatOfDecomposition(String heatOfDecomposition);

    String getSelfAcceleratingDecompositionTemperature();

    void setSelfAcceleratingDecompositionTemperature(String selfAcceleratingDecompositionTemperature);

    String getHeatOfCombustion();

    void setHeatOfCombustion(String heatOfCombustion);

    String getCanConstruction();

    void setCanConstruction(String canConstruction);

    String getGaugePressure();

    void setGaugePressure(String gaugePressure);

    String getAerosolType();

    void setAerosolType(String aerosolType);

    String getIsAerosolTestDataNeeded();

    void setIsAerosolTestDataNeeded(String isAerosolTestDataNeeded);

    String getIgnitionDistance();

    void setIgnitionDistance(String ignitionDistance);

    String getEnclosedSpaceIgnition();

    void setEnclosedSpaceIgnition(String enclosedSpaceIgnition);

    String getFlameHeight();

    void setFlameHeight(String flameHeight);

    String getFlameDuration();

    void setFlameDuration(String flameDuration);

    String getVaporPressure();

    void setVaporPressure(String vaporPressure);

    String getVaporDensity();

    void setVaporDensity(String vaporDensity);

    String getAerosolCanCorrosiveToMetals();

    void setAerosolCanCorrosiveToMetals(String aerosolCanCorrosiveToMetals);

    String getAerosolConductivityOfTheContents();

    void setAerosolConductivityOfTheContents(String aerosolConductivityOfTheContents);

    String getConductivityoftheLiquid();

    void setConductivityoftheLiquid(String conductivityoftheLiquid);

    String getFlammableOrNonFlammable();

    void setFlammableOrNonFlammable(String flammableOrNonFlammable);

    String getPercentByWeightOfFlammablePropellantInAerosolContainer();

    void setPercentByWeightOfFlammablePropellantInAerosolContainer(String percentByWeightOfFlammablePropellantInAerosolContainer);

    String getDoesBaseProductContainByVolumeWaterMiscibleAlcohols();

    void setDoesBaseProductContainByVolumeWaterMiscibleAlcohols(String doesBaseProductContainByVolumeWaterMiscibleAlcohols);

    String getDoesTheBaseProductContainEthanolPlusISOPropanol();

    void setDoesTheBaseProductContainEthanolPlusISOPropanol(String doesTheBaseProductContainEthanolPlusISOPropanol);

    String getDoesBaseProductSustainCombustion();

    void setDoesBaseProductSustainCombustion(String doesBaseProductSustainCombustion);

    String getDoesBaseProductHaveAFirePoint();

    void setDoesBaseProductHaveAFirePoint(String doesBaseProductHaveAFirePoint);

    String getDoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant();

    void setDoesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant(String doesBaseProductContainEmulsifiedLiquefiedNonFlammableGasPropellant);

    String getDoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant();

    void setDoesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant(String doesBaseProductContainEmulsifiedLiquefiedFlammableGasPropellant);

    String getPHMin();

    void setPHMin(String pHMin);

    String getPHMax();

    void setPHMax(String pHMax);

    String getComments();

    void setComments(String comments);

    List<ProductFormBean> getConnectedProductFormBeans();

    void setConnectedProductFormBeans(List<ProductFormBean> connectedProductFormBeans);

    List<BusinessAreaBean> getConnectedBusinessAreaBeans();

    void setConnectedBusinessAreaBeans(List<BusinessAreaBean> connectedBusinessAreaBeans);

    List<ProductTechnologyPlatformBean> getConnectedProductTechnologyPlatformBeans();

    void setConnectedProductTechnologyPlatformBeans(List<ProductTechnologyPlatformBean> connectedProductTechnologyPlatformBeans);

    List<ProductCategoryPlatformBean> getConnectedProductCategoryPlatformBeans();

    void setConnectedProductCategoryPlatformBeans(List<ProductCategoryPlatformBean> connectedProductCategoryPlatformBeans);
    
    //Modified as part of 2018x.6 - Starts
    
   	void setRollupFlag(String rollupFlag);
   
   	String getRollupFlag();
   
   	String getProductFormRelId();

   	void setProductFormRelId();
   	
	//Modified as part of 2018x.6 - Ends
}