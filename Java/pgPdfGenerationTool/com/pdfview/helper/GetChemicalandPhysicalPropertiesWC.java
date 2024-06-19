package com.pdfview.helper;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pdfview.impl.FOP.ChemicalPhysicalProertiesWC;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class GetChemicalandPhysicalPropertiesWC {

	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetChemicalandPhysicalPropertiesWC(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	/** 
	 * This method returns result of Chemical And Physical For Warehouse Classification
	 * @param context 
	 * @param objectID
	 * @throws Exception
	 */
	public ChemicalPhysicalProertiesWC getComponent(){
		ChemicalPhysicalProertiesWC chemicalPhysicalProertiesWC = new ChemicalPhysicalProertiesWC();
		String strDoesPdtCombustion = DomainConstants.EMPTY_STRING;
		String strWeightEmulsified = DomainConstants.EMPTY_STRING; 
		String strBaseEthanolProponal=  DomainConstants.EMPTY_STRING; 
		String strWtEmulsifiedLiq=  DomainConstants.EMPTY_STRING; 
		String strPerFlamePropAerosol = DomainConstants.EMPTY_STRING;
		String strPGPCBByVWMiscibleAlcohols = DomainConstants.EMPTY_STRING;
		String strPGDoesBaseProductHaveAFirePoint = DomainConstants.EMPTY_STRING;
		String strPGFlammableOrNonFlammable = DomainConstants.EMPTY_STRING;
		try{
			Map mpFOPAttributeInfo=PDFPOCHelper.getChemicalAndPhysicalData(_context, _OID);
			if(StringHelper.validateString(_OID)){				
				if(!mpFOPAttributeInfo.isEmpty()){					
					strDoesPdtCombustion = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_BASEPRODUCTSUSTAINCOMBUSTION);
					strWeightEmulsified = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAMMABLE_GAS_PROPELLANT);
					strBaseEthanolProponal = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_PRODUCT_CONTAIN_ETHANOL);
					strWtEmulsifiedLiq  = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_PRODUCT_CONTAIN_GAS);
					strPerFlamePropAerosol = (String)mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_PERCENT_OF_WEIGHT_FLAMMABLE+".inputvalue");
					strPGPCBByVWMiscibleAlcohols = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPCBBYVWMISCIBLEALOHOLS);
					strPGDoesBaseProductHaveAFirePoint = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTHAVEAFIREPOINT);
					strPGFlammableOrNonFlammable = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGFLAMMABLEORNONFLAMMABLE);
					chemicalPhysicalProertiesWC.setIsthepropellantflammableornonflammable(strPGFlammableOrNonFlammable);
					chemicalPhysicalProertiesWC.setPercentbyweightofflammablepropellantinaerosolcontainer(strPerFlamePropAerosol);
					chemicalPhysicalProertiesWC.setDoesthebaseproductcontainwaterbyvolumeANDcontainbyvolumewatermisciblealcohols(strPGPCBByVWMiscibleAlcohols);
					chemicalPhysicalProertiesWC.setDoesthebaseproductcontainwaterANDbyweightethanolplusisopropanol(strBaseEthanolProponal);
					chemicalPhysicalProertiesWC.setDoesthebaseproductSustainCombustion(strDoesPdtCombustion);
					chemicalPhysicalProertiesWC.setDoesthebaseproducthaveaFirePoint(strPGDoesBaseProductHaveAFirePoint);
					chemicalPhysicalProertiesWC.setDoesthebaseproductcontainwaterbyweightofanemulsifiedliquefiednonflammablegaspropellant(strWeightEmulsified);
					chemicalPhysicalProertiesWC.setDoesthebaseproductcontainwaterANDbyweightofanemulsifiedliquefiedflammablegaspropellantthatremainsemulsifiedforthelifeoftheproduct(strWtEmulsifiedLiq);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return chemicalPhysicalProertiesWC;
	}
}
