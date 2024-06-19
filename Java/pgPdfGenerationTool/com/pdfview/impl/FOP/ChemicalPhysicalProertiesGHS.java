package com.pdfview.impl.FOP;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"color",
		"colorIntensity",
		"odour",
		"heatofCombustion",
		"canConstruction",
		"gaugePressure",
		"aerosolType",
		"isaerosoltestdataneeded",
		"ignitionDistance",
		"enclosedSpaceIgnition",
		"foamFlammabilityTestFlameHeight",
		"foamFlammabilityTestFlameDuration",
		"vaporPressure",
		"vaporDensity",
		"relativeDensity",
		"pH",
		"pHDilution",
		"aretheContentsoftheaerosolcanCorrosivetoMetals",
		"technicalBasisfortheCorrosivetoMetalsDeterminationProvided",
		"conductivityofthecontentsintheaerosolcan",
		"closedCupFlashpoint",
		"closedCupFlashpointValue",
		"boilingPoint",
		"boilingPointValue",
		"doestheProductSustainCombustion",
		"doestheProductContainanOxidizerasaRawMaterial",
		"istheOxidizerSodiumPercarbonate",
		"istheOxidizerHydrogenPeroxide",
		"doestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance",
		"doesproductcontainanOrganicPeroxideasarawmaterial",
		"availableOxygenContent",
		"kinematicViscosity",
		"pHavailability",
		"reserveAlkalinity",
		"reserveAcidity",
		"istheLiquidCorrosivetoMetal",
		"conductivityoftheliquid",
		"isaFlammableLiquidabsorbedorContainedwithinthesolid",
		"burnRate",
		"evaporationRate",
		"doestheproducthaveanyselfreactivepropertiesorisitthermallyunstable",
		"heatofDecomposition",
		"selfAcceleratingDecompositionTemperature",
		"contentConductivity",
		"corrosivetoMetals",
		"sustainCombustion",
		"oxidizer",
		"byVolumeethanolandorpropanol",
		"byWeightemulsifiedLiquifiedflammablegaspropellant"

})
public class ChemicalPhysicalProertiesGHS {

	@XmlElement(name = "Color", namespace = "") 
	protected String color;
	@XmlElement(name = "ColorIntensity", namespace = "") 
	protected String colorIntensity;
	@XmlElement(name = "Odour", namespace = "") 
	protected String odour;
	@XmlElement(name = "HeatofCombustion", namespace = "") 
	protected String heatofCombustion;
	@XmlElement(name = "CanConstruction", namespace = "") 
	protected String canConstruction;
	@XmlElement(name = "GaugePressure", namespace = "") 
	protected String gaugePressure;
	@XmlElement(name = "AerosolType", namespace = "") 
	protected String aerosolType;
	@XmlElement(name = "Isaerosoltestdataneeded", namespace = "") 
	protected String isaerosoltestdataneeded;
	@XmlElement(name = "IgnitionDistance", namespace = "") 
	protected String ignitionDistance;
	@XmlElement(name = "EnclosedSpaceIgnition", namespace = "") 
	protected String enclosedSpaceIgnition;
	@XmlElement(name = "FoamFlammabilityTestFlameHeight", namespace = "") 
	protected String foamFlammabilityTestFlameHeight;
	@XmlElement(name = "FoamFlammabilityTestFlameDuration", namespace = "") 
	protected String foamFlammabilityTestFlameDuration;
	@XmlElement(name = "VaporPressure", namespace = "") 
	protected String vaporPressure;
	@XmlElement(name = "VaporDensity", namespace = "") 
	protected String vaporDensity;
	@XmlElement(name = "RelativeDensity", namespace = "") 
	protected String relativeDensity;
	@XmlElement(name = "pH", namespace = "") 
	protected String pH;
	@XmlElement(name = "pHDilution", namespace = "") 
	protected String pHDilution;
	@XmlElement(name = "AretheContentsoftheaerosolcanCorrosivetoMetals", namespace = "") 
	protected String aretheContentsoftheaerosolcanCorrosivetoMetals;
	@XmlElement(name = "TechnicalBasisfortheCorrosivetoMetalsDeterminationProvided", namespace = "") 
	protected String technicalBasisfortheCorrosivetoMetalsDeterminationProvided;
	@XmlElement(name = "Conductivityofthecontentsintheaerosolcan", namespace = "") 
	protected String conductivityofthecontentsintheaerosolcan;
	@XmlElement(name = "ClosedCupFlashpoint", namespace = "") 
	protected String closedCupFlashpoint;
	@XmlElement(name = "ClosedCupFlashpointValue", namespace = "") 
	protected String closedCupFlashpointValue;
	@XmlElement(name = "BoilingPoint", namespace = "") 
	protected String boilingPoint;
	@XmlElement(name = "BoilingPointValue", namespace = "") 
	protected String boilingPointValue;
	@XmlElement(name = "DoestheProductSustainCombustion", namespace = "") 
	protected String doestheProductSustainCombustion;
	@XmlElement(name = "DoestheProductContainanOxidizerasaRawMaterial", namespace = "") 
	protected String doestheProductContainanOxidizerasaRawMaterial;
	@XmlElement(name = "IstheOxidizerSodiumPercarbonate", namespace = "") 
	protected String istheOxidizerSodiumPercarbonate;
	@XmlElement(name = "IstheOxidizerHydrogenPeroxide", namespace = "") 
	protected String istheOxidizerHydrogenPeroxide;
	@XmlElement(name = "DoestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance", namespace = "") 
	protected String doestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance;
	@XmlElement(name = "DoesproductcontainanOrganicPeroxideasarawmaterial", namespace = "") 
	protected String doesproductcontainanOrganicPeroxideasarawmaterial;
	@XmlElement(name = "AvailableOxygenContent", namespace = "") 
	protected String availableOxygenContent;
	@XmlElement(name = "KinematicViscosity", namespace = "") 
	protected String kinematicViscosity;
	@XmlElement(name = "pHavailability", namespace = "") 
	protected String pHavailability;
	@XmlElement(name = "ReserveAlkalinity", namespace = "") 
	protected String reserveAlkalinity;
	@XmlElement(name = "ReserveAcidity", namespace = "") 
	protected String reserveAcidity;
	@XmlElement(name = "IstheLiquidCorrosivetoMetal", namespace = "") 
	protected String istheLiquidCorrosivetoMetal;
	@XmlElement(name = "Conductivityoftheliquid", namespace = "") 
	protected String conductivityoftheliquid;
	@XmlElement(name = "IsaFlammableLiquidabsorbedorContainedwithinthesolid", namespace = "") 
	protected String isaFlammableLiquidabsorbedorContainedwithinthesolid;
	@XmlElement(name = "BurnRate", namespace = "") 
	protected String burnRate;
	@XmlElement(name = "EvaporationRate", namespace = "") 
	protected String evaporationRate;
	@XmlElement(name = "Doestheproducthaveanyselfreactivepropertiesorisitthermallyunstable", namespace = "") 
	protected String doestheproducthaveanyselfreactivepropertiesorisitthermallyunstable;
	@XmlElement(name = "HeatofDecomposition", namespace = "") 
	protected String heatofDecomposition;
	@XmlElement(name = "SelfAcceleratingDecompositionTemperature", namespace = "") 
	protected String selfAcceleratingDecompositionTemperature;
	@XmlElement(name = "ContentConductivity", namespace = "") 
	protected String contentConductivity;
	@XmlElement(name = "CorrosivetoMetals", namespace = "") 
	protected String corrosivetoMetals;
	@XmlElement(name = "SustainCombustion", namespace = "") 
	protected String sustainCombustion;
	@XmlElement(name = "Oxidizer", namespace = "") 
	protected String oxidizer;
	@XmlElement(name = "byVolumeethanolandorpropanol", namespace = "") 
	protected String byVolumeethanolandorpropanol;
	@XmlElement(name = "byWeightemulsifiedLiquifiedflammablegaspropellant", namespace = "") 
	protected String byWeightemulsifiedLiquifiedflammablegaspropellant;
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
	public String getHeatofCombustion() {
		return heatofCombustion;
	}
	public void setHeatofCombustion(String heatofCombustion) {
		this.heatofCombustion = heatofCombustion;
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
	public String getIsaerosoltestdataneeded() {
		return isaerosoltestdataneeded;
	}
	public void setIsaerosoltestdataneeded(String isaerosoltestdataneeded) {
		this.isaerosoltestdataneeded = isaerosoltestdataneeded;
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
	public String getFoamFlammabilityTestFlameHeight() {
		return foamFlammabilityTestFlameHeight;
	}
	public void setFoamFlammabilityTestFlameHeight(
			String foamFlammabilityTestFlameHeight) {
		this.foamFlammabilityTestFlameHeight = foamFlammabilityTestFlameHeight;
	}
	public String getFoamFlammabilityTestFlameDuration() {
		return foamFlammabilityTestFlameDuration;
	}
	public void setFoamFlammabilityTestFlameDuration(
			String foamFlammabilityTestFlameDuration) {
		this.foamFlammabilityTestFlameDuration = foamFlammabilityTestFlameDuration;
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
	public String getRelativeDensity() {
		return relativeDensity;
	}
	public void setRelativeDensity(String relativeDensity) {
		this.relativeDensity = relativeDensity;
	}
	public String getpH() {
		return pH;
	}
	public void setpH(String pH) {
		this.pH = pH;
	}
	public String getpHDilution() {
		return pHDilution;
	}
	public void setpHDilution(String pHDilution) {
		this.pHDilution = pHDilution;
	}
	public String getAretheContentsoftheaerosolcanCorrosivetoMetals() {
		return aretheContentsoftheaerosolcanCorrosivetoMetals;
	}
	public void setAretheContentsoftheaerosolcanCorrosivetoMetals(
			String aretheContentsoftheaerosolcanCorrosivetoMetals) {
		this.aretheContentsoftheaerosolcanCorrosivetoMetals = aretheContentsoftheaerosolcanCorrosivetoMetals;
	}
	public String getTechnicalBasisfortheCorrosivetoMetalsDeterminationProvided() {
		return technicalBasisfortheCorrosivetoMetalsDeterminationProvided;
	}
	public void setTechnicalBasisfortheCorrosivetoMetalsDeterminationProvided(
			String technicalBasisfortheCorrosivetoMetalsDeterminationProvided) {
		this.technicalBasisfortheCorrosivetoMetalsDeterminationProvided = technicalBasisfortheCorrosivetoMetalsDeterminationProvided;
	}
	public String getConductivityofthecontentsintheaerosolcan() {
		return conductivityofthecontentsintheaerosolcan;
	}
	public void setConductivityofthecontentsintheaerosolcan(
			String conductivityofthecontentsintheaerosolcan) {
		this.conductivityofthecontentsintheaerosolcan = conductivityofthecontentsintheaerosolcan;
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
	public String getDoestheProductSustainCombustion() {
		return doestheProductSustainCombustion;
	}
	public void setDoestheProductSustainCombustion(
			String doestheProductSustainCombustion) {
		this.doestheProductSustainCombustion = doestheProductSustainCombustion;
	}
	public String getDoestheProductContainanOxidizerasaRawMaterial() {
		return doestheProductContainanOxidizerasaRawMaterial;
	}
	public void setDoestheProductContainanOxidizerasaRawMaterial(
			String doestheProductContainanOxidizerasaRawMaterial) {
		this.doestheProductContainanOxidizerasaRawMaterial = doestheProductContainanOxidizerasaRawMaterial;
	}
	public String getIstheOxidizerSodiumPercarbonate() {
		return istheOxidizerSodiumPercarbonate;
	}
	public void setIstheOxidizerSodiumPercarbonate(
			String istheOxidizerSodiumPercarbonate) {
		this.istheOxidizerSodiumPercarbonate = istheOxidizerSodiumPercarbonate;
	}
	public String getIstheOxidizerHydrogenPeroxide() {
		return istheOxidizerHydrogenPeroxide;
	}
	public void setIstheOxidizerHydrogenPeroxide(
			String istheOxidizerHydrogenPeroxide) {
		this.istheOxidizerHydrogenPeroxide = istheOxidizerHydrogenPeroxide;
	}
	public String getDoestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance() {
		return doestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance;
	}
	public void setDoestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance(
			String doestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance) {
		this.doestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance = doestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance;
	}
	public String getDoesproductcontainanOrganicPeroxideasarawmaterial() {
		return doesproductcontainanOrganicPeroxideasarawmaterial;
	}
	public void setDoesproductcontainanOrganicPeroxideasarawmaterial(
			String doesproductcontainanOrganicPeroxideasarawmaterial) {
		this.doesproductcontainanOrganicPeroxideasarawmaterial = doesproductcontainanOrganicPeroxideasarawmaterial;
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
	public String getpHavailability() {
		return pHavailability;
	}
	public void setpHavailability(String pHavailability) {
		this.pHavailability = pHavailability;
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
	public String getIstheLiquidCorrosivetoMetal() {
		return istheLiquidCorrosivetoMetal;
	}
	public void setIstheLiquidCorrosivetoMetal(String istheLiquidCorrosivetoMetal) {
		this.istheLiquidCorrosivetoMetal = istheLiquidCorrosivetoMetal;
	}
	public String getConductivityoftheliquid() {
		return conductivityoftheliquid;
	}
	public void setConductivityoftheliquid(String conductivityoftheliquid) {
		this.conductivityoftheliquid = conductivityoftheliquid;
	}
	public String getIsaFlammableLiquidabsorbedorContainedwithinthesolid() {
		return isaFlammableLiquidabsorbedorContainedwithinthesolid;
	}
	public void setIsaFlammableLiquidabsorbedorContainedwithinthesolid(
			String isaFlammableLiquidabsorbedorContainedwithinthesolid) {
		this.isaFlammableLiquidabsorbedorContainedwithinthesolid = isaFlammableLiquidabsorbedorContainedwithinthesolid;
	}
	public String getBurnRate() {
		return burnRate;
	}
	public void setBurnRate(String burnRate) {
		this.burnRate = burnRate;
	}
	public String getEvaporationRate() {
		return evaporationRate;
	}
	public void setEvaporationRate(String evaporationRate) {
		this.evaporationRate = evaporationRate;
	}
	public String getDoestheproducthaveanyselfreactivepropertiesorisitthermallyunstable() {
		return doestheproducthaveanyselfreactivepropertiesorisitthermallyunstable;
	}
	public void setDoestheproducthaveanyselfreactivepropertiesorisitthermallyunstable(
			String doestheproducthaveanyselfreactivepropertiesorisitthermallyunstable) {
		this.doestheproducthaveanyselfreactivepropertiesorisitthermallyunstable = doestheproducthaveanyselfreactivepropertiesorisitthermallyunstable;
	}
	public String getHeatofDecomposition() {
		return heatofDecomposition;
	}
	public void setHeatofDecomposition(String heatofDecomposition) {
		this.heatofDecomposition = heatofDecomposition;
	}
	public String getSelfAcceleratingDecompositionTemperature() {
		return selfAcceleratingDecompositionTemperature;
	}
	public void setSelfAcceleratingDecompositionTemperature(
			String selfAcceleratingDecompositionTemperature) {
		this.selfAcceleratingDecompositionTemperature = selfAcceleratingDecompositionTemperature;
	}
	public String getContentConductivity() {
		return contentConductivity;
	}
	public void setContentConductivity(String contentConductivity) {
		this.contentConductivity = contentConductivity;
	}
	public String getCorrosivetoMetals() {
		return corrosivetoMetals;
	}
	public void setCorrosivetoMetals(String corrosivetoMetals) {
		this.corrosivetoMetals = corrosivetoMetals;
	}
	public String getSustainCombustion() {
		return sustainCombustion;
	}
	public void setSustainCombustion(String sustainCombustion) {
		this.sustainCombustion = sustainCombustion;
	}
	public String getOxidizer() {
		return oxidizer;
	}
	public void setOxidizer(String oxidizer) {
		this.oxidizer = oxidizer;
	}
	public String getByVolumeethanolandorpropanol() {
		return byVolumeethanolandorpropanol;
	}
	public void setByVolumeethanolandorpropanol(String byVolumeethanolandorpropanol) {
		this.byVolumeethanolandorpropanol = byVolumeethanolandorpropanol;
	}
	public String getByWeightemulsifiedLiquifiedflammablegaspropellant() {
		return byWeightemulsifiedLiquifiedflammablegaspropellant;
	}
	public void setByWeightemulsifiedLiquifiedflammablegaspropellant(
			String byWeightemulsifiedLiquifiedflammablegaspropellant) {
		this.byWeightemulsifiedLiquifiedflammablegaspropellant = byWeightemulsifiedLiquifiedflammablegaspropellant;
	}
	

}
