package com.pdfview.helper;

import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.pdfview.impl.FOP.ChemicalandPhysicalProertiesEnginuity;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;

public class GetChemicalandPhysicalPropertiesEnginuity {


	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetChemicalandPhysicalPropertiesEnginuity(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}
	/** 
	 * This method returns result of Chemical And Physical For Enginuity And Legacy Data
	 * @param context 
	 * @param objectID
	 * @throws Exception
	 */
	public  ChemicalandPhysicalProertiesEnginuity getComponent(){
		ChemicalandPhysicalProertiesEnginuity chemicalandPhysicalProertiesEnginuity = new ChemicalandPhysicalProertiesEnginuity();
		String strPropellantEmulsProd = DomainConstants.EMPTY_STRING;
		String strPropellantNonflame = DomainConstants.EMPTY_STRING;
		String strWTParameterized = DomainConstants.EMPTY_STRING;
		String strKstDustDeflagrationIndex = DomainConstants.EMPTY_STRING;			
		String strPMAXExplosionPressure = DomainConstants.EMPTY_STRING;
		try{
			Map mpFOPAttributeInfo =PDFPOCHelper.getChemicalAndPhysicalData(_context, _OID);
			if(StringHelper.validateString(_OID)){				
				if(!mpFOPAttributeInfo.isEmpty()){
					strPropellantEmulsProd = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPROPELLANTEMULSIFIEDPRODUCT);
					strPropellantNonflame = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPROPELLANTNONFLAMMABLE);
					strWTParameterized = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGWTPARAMETERIZED+".inputvalue");
					strKstDustDeflagrationIndex = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGKSTDUSTDEFLAGRATIONINDEX+".inputvalue");					 
					strPMAXExplosionPressure = (String) mpFOPAttributeInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGPMAXEXPLOSIONPRESSURE+".inputvalue");
					chemicalandPhysicalProertiesEnginuity.setPropellantemulsifiedforlifeofproduct(strPropellantEmulsProd);
					chemicalandPhysicalProertiesEnginuity.setPropellantisnonflammable(strPropellantNonflame);
					chemicalandPhysicalProertiesEnginuity.setwTParameterized(strWTParameterized);
					chemicalandPhysicalProertiesEnginuity.setKstDustDeflagrationIndex(strKstDustDeflagrationIndex);
					chemicalandPhysicalProertiesEnginuity.setPmaxmaxexplosionpressure(strPMAXExplosionPressure);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}


		return chemicalandPhysicalProertiesEnginuity;
	}

}


