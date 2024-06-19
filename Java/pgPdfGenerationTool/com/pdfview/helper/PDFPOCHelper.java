package com.pdfview.helper;

import java.util.HashMap;
import java.util.Map;

import com.matrixone.apps.domain.DomainObject;
import com.pdfview.constant.PDFConstant;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class PDFPOCHelper {
	
	/**
	 * Method to execute the Main Class method
	 * @param context
	 * @param classname
	 * @param methodName
	 * @param args
	 * @return
	 */
	public static Object executeMainClassMethod(Context context, String classname, String methodName, String[] args)
			 {
		Object mlRelatedObjects = null;
		try {
		mlRelatedObjects = (Object) JPO.invoke(context, classname, null, methodName, args, Object.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mlRelatedObjects;
	}

	/**
	 * Method to execute the Intermediate Class
	 * @param context
	 * @param strIfYesselectbatterytype
	 * @param strpgPicklistTypeName
	 * @param strpgPicklistAttributeName
	 * @return
	 */
	public static Object executepgDSOCPNProductDataClassMethod(Context context, String strIfYesselectbatterytype,
			String strpgPicklistTypeName, String strpgPicklistAttributeName) {
		HashMap hm = new HashMap();
		hm.put("strIfYesselectbatterytype", strIfYesselectbatterytype);
		hm.put("strpgPicklistTypeName", strpgPicklistTypeName);
		hm.put("strpgPicklistAttributeName", strpgPicklistAttributeName);
		String[] args = JPO.packArgs(hm);
		Object mlRelatedObjects = null;
		try {
			mlRelatedObjects = executeIntermediatorClassMethod(context, "getBatteryTypeFields", args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mlRelatedObjects;
	}

	/**
	 * Method to execute the Intermediate Class
	 * @param context
	 * @param methodName
	 * @param args
	 * @return
	 */
	public static Object executeIntermediatorClassMethod(Context context, String methodName, String[] args) {

		Object mlRelatedObjects=null;
		try {
			mlRelatedObjects = (Object) JPO.invoke(context, "pgPDFViewIntermediator", null, methodName, args,
					Object.class);
		} catch (MatrixException e) {
			e.printStackTrace();
		}

		return mlRelatedObjects;
	}
	/**
	 * @param context
	 * @param args
	 * @param paramName
	 * @return
	 * @throws Exception
	 */
	public static Object executepgDSOCommonUtilsMethod(Context context, String[] args,String paramName)
			throws Exception {
		
		Map paramMap = (Map) JPO.unpackArgs(args);
		paramMap.put("paramName", paramName);
		String[] args1 =JPO.packArgs(paramMap);
		Object mlRelatedObjects =executeIntermediatorClassMethod(context, "getParam", args1);

		return mlRelatedObjects;
	}
	/**
	 * @param selects
	 * @return
	 */
	public static StringList createSelects(String... selects)
	{
		StringList selectList = new StringList();
		for(String select : selects)
		{
			selectList.add(select);
		}
		return selectList;
	}
	/** 
	 * This method is for FOP Chemical/Physical properties selectables.
	 * @param context
	 * @param ObjectId
	 * @return 
	 * @throws Exception
	 */
	public static Map  getChemicalAndPhysicalData(Context context,String strObjectId) throws Exception{
		Map mpFOPAttributeInfo=null;
		if(mpFOPAttributeInfo==null || mpFOPAttributeInfo.isEmpty()) {
			mpFOPAttributeInfo =new HashMap();
			if(StringHelper.validateString(strObjectId)){				
				StringList slObjectSelects = new StringList(79);	
				StringBuffer inputValue = new StringBuffer();
				slObjectSelects.add(pgV3Constants.SELECT_TYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXCONSUMERUNITSIZE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCUSTOMERUNITLABELING);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCONSUMERUNITLABELING);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPUNNUMBER);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHAZARDCLASS); 
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGGROUP);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGGAUGEPRESSURE+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGIGNITIONDISTANCE+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGENCLOSEDSPACEIGNITION+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGFOAMFLAMMABILITY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PHVALUE);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCONTENTCONDUCTIVITY+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCORROSIVETOMETALS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCLOSEDCUPFLASHPOINT);				
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGEVAPORATIONRATE+PDFConstant.CONST_INPUTVALUE));				
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVEACIDITY+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGRESERVERALKALINITY+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_BOILINGPOINT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSUSTAINCOMBUSTION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGOXIDIZER);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAVAILABLEOXYGEN);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBYVOLUME+PDFConstant.CONST_INPUTVALUE));				
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBYWEIGHT+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPROPELLANTEMULSIFIEDPRODUCT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPROPELLANTNONFLAMMABLE);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGKINEMATICVISCOSITY+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_VAPORPRESSURE);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_BOILINGPOINT+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_VAPORPRESSURE+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_PHVALUE +PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGWTPARAMETERIZED+PDFConstant.CONST_INPUTVALUE));				
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGRELATIVEDENSITYORSPECIFICGRAVITY+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBURNRATE+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHEATOFDECOMPOSITION+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSELFACCELDECOMTEMP+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPROPERSHIPPINGNAME);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGKSTDUSTDEFLAGRATIONINDEX+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPMAXEXPLOSIONPRESSURE+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSCLASSIFICATION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCOLOR);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_COLOR_INTENSITY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_ODOUR);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_CLOSEDCUPFLASHPOINTVALUE+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_BOILINGPOINTVALUE+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_BASEPRODUCTSUSTAINCOMBUSTION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_OXIDIZERSODIUMPERCARBONATE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_OXIDIZERHYDROGENPEROXIDE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_ORGANIC_PEROXIDE);  
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_AVAILABLE_OXYGEN_CONTENT+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAMMABLE_GAS_PROPELLANT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_PROPELLANT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_PH_DILUTION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_LIQUID_CORROSIVE_TO_METAL);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_TECHNICAL_CTM);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAMMABLE_LIQUID);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_SELF_REACTIVE_PROPERTIES);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_PH_AVAILABILITY);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_PRODUCT_CONTAIN_ETHANOL);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_PRODUCT_CONTAIN_GAS);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_HEAT_OF_COMBUSTION+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_CAN_CONSTRUCTION);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_AEROSOLTYPE);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_IS_AEROSOLTYPE_TEST);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAMEHEIGHT+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_FLAME_DURATION+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_AEROSOL_CONDUCTIVITY+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_PERCENT_OF_WEIGHT_FLAMMABLE+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_VAPOUR_DENSITY+PDFConstant.CONST_INPUTVALUE));
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PG_PH+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ODOUR);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTOXIDIZER);
				inputValue.append(slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBOILINGPOINT+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPCBBYVWMISCIBLEALOHOLS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTHAVEAFIREPOINT);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAEROSOLCANCORROSIVETOMETALS);
				slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGFLAMMABLEORNONFLAMMABLE);
				inputValue.append(slObjectSelects.add(PDFConstant.SELECT_ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID+PDFConstant.CONST_INPUTVALUE));
				slObjectSelects.add(PDFConstant.SELECT_ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE);
				DomainObject domainObject= DomainObject.newInstance(context,strObjectId);
				mpFOPAttributeInfo = domainObject.getInfo(context, slObjectSelects);
			}
		}
		return mpFOPAttributeInfo;
	}
}
