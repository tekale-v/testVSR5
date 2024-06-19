package com.pdfview.helper;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FOP.ChemicalPhysicalProertiesGHS;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class GetChemicalandPhysicalPropertiesGHS {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetChemicalandPhysicalPropertiesGHS(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/** 
	 * This method returns result of Chemical And Physical For GHS And DGC Classification.
	 * @param context 
	 * @param objectID
	 * @throws Exception
	 */
	public ChemicalPhysicalProertiesGHS getComponent(){
		ChemicalPhysicalProertiesGHS chemicalPhysicalProertiesGHS = new ChemicalPhysicalProertiesGHS();	
		String strGaugePressure = DomainConstants.EMPTY_STRING;
		String strIgnitionDistance = DomainConstants.EMPTY_STRING;			
		String strEnclosedSpaceIgnition = DomainConstants.EMPTY_STRING;
		String strContentConductivity = DomainConstants.EMPTY_STRING;
		String strClosedCupFlashpoint = DomainConstants.EMPTY_STRING;			
		String strSustainCombustion = DomainConstants.EMPTY_STRING;
		String strOxidizer = DomainConstants.EMPTY_STRING;
		String strPGByVolume = DomainConstants.EMPTY_STRING;
		String strPGByWeight = DomainConstants.EMPTY_STRING;
		String strKinematicViscosity = DomainConstants.EMPTY_STRING;			
		String strVaporPressure = DomainConstants.EMPTY_STRING;
		String strRelativeDensityOrSpecificGravity = DomainConstants.EMPTY_STRING;
		String strEvpRate = DomainConstants.EMPTY_STRING;
		String strResAcdity = DomainConstants.EMPTY_STRING;
		String strResAlknity = DomainConstants.EMPTY_STRING;
		String strBurnRate = DomainConstants.EMPTY_STRING;
		String strHeatOfDecomposition =DomainConstants.EMPTY_STRING;
		String strSelfAccelDecompTemp = DomainConstants.EMPTY_STRING;
		String strColorIntensity = DomainConstants.EMPTY_STRING;
		String strOdour = DomainConstants.EMPTY_STRING;
		String strCorrosiveMetal = DomainConstants.EMPTY_STRING;
		String strClosedFlashpointValue = DomainConstants.EMPTY_STRING;
		String strBolingPointValue = DomainConstants.EMPTY_STRING;
		String strDoesPdtCombustion = DomainConstants.EMPTY_STRING;
		String strOxidizerNaPerCa = DomainConstants.EMPTY_STRING;  
		String strHydrogenPeroxide = DomainConstants.EMPTY_STRING;  
		String strOrganicPeroxide = DomainConstants.EMPTY_STRING; 
		String strAvailableOxyContent = DomainConstants.EMPTY_STRING; 
		String strDilution = DomainConstants.EMPTY_STRING;
		String strLiquidCorrMetal =  DomainConstants.EMPTY_STRING;
		String strTechCorrMetal  = DomainConstants.EMPTY_STRING; 
		String strFlammabelLiq = DomainConstants.EMPTY_STRING;   
		String strConductivityoftheLiq = DomainConstants.EMPTY_STRING;  
		String strProdReactiveProp =  DomainConstants.EMPTY_STRING; 
		String strPHAvalibility =  DomainConstants.EMPTY_STRING; 
		String strHeatOfCombustion = DomainConstants.EMPTY_STRING;
		String strCanConst = DomainConstants.EMPTY_STRING;
		String strAerosolType = DomainConstants.EMPTY_STRING;
		String strAerosolTestData = DomainConstants.EMPTY_STRING;
		String strFlameHt = DomainConstants.EMPTY_STRING;
		String strFlameDuration = DomainConstants.EMPTY_STRING;
		String strCondAerosol = DomainConstants.EMPTY_STRING;
		String strVaporDensity = DomainConstants.EMPTY_STRING;
		String strBoiling_Point = DomainConstants.EMPTY_STRING;
		String str_PH = DomainConstants.EMPTY_STRING; 
		String strPGProductOxidizer = DomainConstants.EMPTY_STRING;
		String strPGAerosolCanCorrosiveToMetals = DomainConstants.EMPTY_STRING;
		String strColor = DomainConstants.EMPTY_STRING;
		String strProductPotentialToIncreaseBurningRate = DomainConstants.EMPTY_STRING;  
		Map mpFOPAttributeInfo=null;
		try{
			mpFOPAttributeInfo=PDFPOCHelper.getChemicalAndPhysicalData(_context, _OID);
			if(StringHelper.validateString(_OID)){
				if(!mpFOPAttributeInfo.isEmpty()){					
					strGaugePressure = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGGAUGEPRESSURE+".inputvalue");
					strColor =(String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCOLOR);
					strColorIntensity =(String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_COLOR_INTENSITY);
					strOdour = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_ODOUR);
					if(UIUtil.isNotNullAndNotEmpty(strOdour)) {
						strOdour = StringHelper.filterLessAndGreaterThanSign(strOdour);
					}
					strCorrosiveMetal = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCORROSIVETOMETALS);
					strClosedFlashpointValue = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_CLOSEDCUPFLASHPOINTVALUE+".inputvalue");
					strBolingPointValue = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_BOILINGPOINTVALUE+".inputvalue");
					strDoesPdtCombustion = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_BASEPRODUCTSUSTAINCOMBUSTION);
					strOxidizerNaPerCa = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_OXIDIZERSODIUMPERCARBONATE);
					strHydrogenPeroxide = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_OXIDIZERHYDROGENPEROXIDE);
					strOrganicPeroxide = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_ORGANIC_PEROXIDE);
					strAvailableOxyContent = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_AVAILABLE_OXYGEN_CONTENT+".inputvalue");
					strDilution = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_PH_DILUTION);
					strLiquidCorrMetal = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_LIQUID_CORROSIVE_TO_METAL);
					strTechCorrMetal = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_TECHNICAL_CTM);
					if(UIUtil.isNotNullAndNotEmpty(strTechCorrMetal)) {
						strTechCorrMetal = StringHelper.filterLessAndGreaterThanSign(strTechCorrMetal);
					}
					strFlammabelLiq = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAMMABLE_LIQUID);
					strConductivityoftheLiq=(String)mpFOPAttributeInfo.get(PDFConstant.SELECT_ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID+".inputvalue");
					strProdReactiveProp = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_SELF_REACTIVE_PROPERTIES);
					strPHAvalibility = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_PH_AVAILABILITY);
					strHeatOfCombustion  = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_HEAT_OF_COMBUSTION+".inputvalue");
					strCanConst  = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_CAN_CONSTRUCTION);
					strAerosolType = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_AEROSOLTYPE);
					strAerosolTestData = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_IS_AEROSOLTYPE_TEST);
					if(UIUtil.isNotNullAndNotEmpty(strAerosolTestData)) {
						strAerosolTestData = StringHelper.filterLessAndGreaterThanSign(strAerosolTestData);
					}
					strFlameHt = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAMEHEIGHT+".inputvalue");
					strFlameDuration = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAME_DURATION+".inputvalue");
					strCondAerosol = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_AEROSOL_CONDUCTIVITY+".inputvalue");
					strBoiling_Point = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOILINGPOINT+".inputvalue");
					str_PH = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_PH+".inputvalue");
					strVaporDensity = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_VAPOUR_DENSITY+".inputvalue"); 
					strIgnitionDistance = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGIGNITIONDISTANCE+".inputvalue");
					strEnclosedSpaceIgnition = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGENCLOSEDSPACEIGNITION+".inputvalue");
					strContentConductivity = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONTENTCONDUCTIVITY+".inputvalue");
					strClosedCupFlashpoint = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGCLOSEDCUPFLASHPOINT);
					strSustainCombustion = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSUSTAINCOMBUSTION);
					strOxidizer = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGOXIDIZER);
					strPGProductOxidizer = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTOXIDIZER);
					strPGByVolume = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGBYVOLUME+".inputvalue");
					strPGByWeight = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGBYWEIGHT+".inputvalue");
					strKinematicViscosity = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGKINEMATICVISCOSITY+".inputvalue");
					String strObjType = (String) mpFOPAttributeInfo.get(DomainConstants.SELECT_TYPE); 
					if(strObjType.equalsIgnoreCase(pgV3Constants.TYPE_RAWMATERIALPART) || strObjType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPART)){ 
						strVaporPressure = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_VAPORPRESSURE+".inputvalue");
					} else {
						strVaporPressure = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_VAPORPRESSURE);
					}
					strRelativeDensityOrSpecificGravity = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGRELATIVEDENSITYORSPECIFICGRAVITY+".inputvalue");
					strEvpRate = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVAPORATIONRATE+".inputvalue");
					strResAcdity = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVEACIDITY+".inputvalue");
					strResAlknity = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVERALKALINITY+".inputvalue");
					strBurnRate = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGBURNRATE+".inputvalue");
					strHeatOfDecomposition = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGHEATOFDECOMPOSITION+".inputvalue");
					strSelfAccelDecompTemp = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSELFACCELDECOMTEMP+".inputvalue");
					strPGAerosolCanCorrosiveToMetals = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGAEROSOLCANCORROSIVETOMETALS);
					strProductPotentialToIncreaseBurningRate = (String) mpFOPAttributeInfo.get(PDFConstant.SELECT_ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE);

					chemicalPhysicalProertiesGHS.setColor(strColor);
					chemicalPhysicalProertiesGHS.setColorIntensity(strColorIntensity);
					chemicalPhysicalProertiesGHS.setOdour(strOdour);
					chemicalPhysicalProertiesGHS.setHeatofCombustion(strHeatOfCombustion);
					chemicalPhysicalProertiesGHS.setCanConstruction(strCanConst);
					chemicalPhysicalProertiesGHS.setGaugePressure(strGaugePressure);
					chemicalPhysicalProertiesGHS.setAerosolType(strAerosolType);
					chemicalPhysicalProertiesGHS.setIsaerosoltestdataneeded(strAerosolTestData);
					chemicalPhysicalProertiesGHS.setIgnitionDistance(strIgnitionDistance);
					chemicalPhysicalProertiesGHS.setEnclosedSpaceIgnition(strEnclosedSpaceIgnition);
					chemicalPhysicalProertiesGHS.setFoamFlammabilityTestFlameHeight(strFlameHt);
					chemicalPhysicalProertiesGHS.setFoamFlammabilityTestFlameDuration(strFlameDuration);
					chemicalPhysicalProertiesGHS.setVaporPressure(strVaporPressure);
					chemicalPhysicalProertiesGHS.setVaporDensity(strVaporDensity);
					chemicalPhysicalProertiesGHS.setRelativeDensity(strRelativeDensityOrSpecificGravity);
					chemicalPhysicalProertiesGHS.setpH(str_PH);
					chemicalPhysicalProertiesGHS.setpHDilution(strDilution);
					chemicalPhysicalProertiesGHS.setAretheContentsoftheaerosolcanCorrosivetoMetals(strPGAerosolCanCorrosiveToMetals);
					chemicalPhysicalProertiesGHS.setTechnicalBasisfortheCorrosivetoMetalsDeterminationProvided(strTechCorrMetal);
					chemicalPhysicalProertiesGHS.setConductivityofthecontentsintheaerosolcan(strCondAerosol);
					chemicalPhysicalProertiesGHS.setClosedCupFlashpoint(strClosedCupFlashpoint);
					chemicalPhysicalProertiesGHS.setClosedCupFlashpointValue(strClosedFlashpointValue);
					chemicalPhysicalProertiesGHS.setBoilingPoint(strBoiling_Point);
					chemicalPhysicalProertiesGHS.setBoilingPointValue(strBolingPointValue);
					chemicalPhysicalProertiesGHS.setDoestheProductSustainCombustion(strDoesPdtCombustion);
					chemicalPhysicalProertiesGHS.setDoestheProductContainanOxidizerasaRawMaterial(strPGProductOxidizer);
					chemicalPhysicalProertiesGHS.setIstheOxidizerSodiumPercarbonate(strOxidizerNaPerCa);
					chemicalPhysicalProertiesGHS.setIstheOxidizerHydrogenPeroxide(strHydrogenPeroxide);
					chemicalPhysicalProertiesGHS.setDoestheProducthavethePotentialtoIncreasetheBurningRateorIntensityofaCombustibleSubstance(strProductPotentialToIncreaseBurningRate);
					chemicalPhysicalProertiesGHS.setDoesproductcontainanOrganicPeroxideasarawmaterial(strOrganicPeroxide);
					chemicalPhysicalProertiesGHS.setAvailableOxygenContent(strAvailableOxyContent);
					chemicalPhysicalProertiesGHS.setKinematicViscosity(strKinematicViscosity);
					chemicalPhysicalProertiesGHS.setpHavailability(strPHAvalibility);
					chemicalPhysicalProertiesGHS.setReserveAlkalinity(strResAlknity);
					chemicalPhysicalProertiesGHS.setReserveAcidity(strResAcdity);
					chemicalPhysicalProertiesGHS.setIstheLiquidCorrosivetoMetal(strLiquidCorrMetal);;
					chemicalPhysicalProertiesGHS.setConductivityoftheliquid(strConductivityoftheLiq);
					chemicalPhysicalProertiesGHS.setIsaFlammableLiquidabsorbedorContainedwithinthesolid(strFlammabelLiq);
					chemicalPhysicalProertiesGHS.setBurnRate(strBurnRate);
					chemicalPhysicalProertiesGHS.setEvaporationRate(strEvpRate);
					chemicalPhysicalProertiesGHS.setDoestheproducthaveanyselfreactivepropertiesorisitthermallyunstable(strProdReactiveProp);
					chemicalPhysicalProertiesGHS.setHeatofDecomposition(strHeatOfDecomposition);
					chemicalPhysicalProertiesGHS.setSelfAcceleratingDecompositionTemperature(strSelfAccelDecompTemp);
					chemicalPhysicalProertiesGHS.setContentConductivity(strContentConductivity);
					chemicalPhysicalProertiesGHS.setCorrosivetoMetals(strCorrosiveMetal);
					chemicalPhysicalProertiesGHS.setSustainCombustion(strSustainCombustion);
					chemicalPhysicalProertiesGHS.setOxidizer(strOxidizer);
					chemicalPhysicalProertiesGHS.setByVolumeethanolandorpropanol(strPGByVolume);
					chemicalPhysicalProertiesGHS.setByWeightemulsifiedLiquifiedflammablegaspropellant(strPGByWeight);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return chemicalPhysicalProertiesGHS;
	}

}
