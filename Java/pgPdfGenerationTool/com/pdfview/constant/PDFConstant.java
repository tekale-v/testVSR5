package com.pdfview.constant;

import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.v3.custom.pgV3Constants;

public class PDFConstant implements pgV3Constants{
	public static String selectedTableNames = "";
	public static String BOMMainTableNames = "Bill of Materials";
	public static String SubstituteMainTableNames = "Substitutes";
	public static String BOMCUPTableNames = "Bill of Materials Customer Unit";
	public static String SubstituteCUPTableNames = "Substitutes Customer Unit";
	public static String BOMCOPTableNames = "Bill of Materials Consumer Unit";
	public static String SubstituteCOPTableNames = "Substitutes Consumer Unit";
	public static String BOMInnerPackTableNames = "Bill of Materials Inner Pack";
	public static String SubstituteInnerPackTableNames = "Substitutes Inner Pack";
	public static String BOMTUPTableNames = "Bill of Materials Transport Unit";
	public static String SubstituteTUPTableNames = "Substitutes Transport Unit";
	public static final String CONST_STRING = "String";
	public static final String CONST_ASCENDING = "ascending";
	public static String sCommonTagNames = "GenericModel";
	public static final String ATTRIBUTE_PGPACKINGSITE = PropertyUtil.getSchemaProperty("attribute_pgPackingSite");
	public static final String ATTRIBUTE_PGMANUFACTURINGSITE = PropertyUtil
			.getSchemaProperty("attribute_pgManufacturingSite");
	public static final String ATTRIBUTE_PGREGISTRATIONRENEWALLEADTIME = PropertyUtil
			.getSchemaProperty("attribute_pgRegistrationRenewalLeadTime");
	public static final String ATTRIBUTE_PGREGISTRATIONRENEWALSTATUS = PropertyUtil
			.getSchemaProperty("attribute_pgRegistrationRenewalStatus");
	public static final String ATTRIBUTE_PGMEASUREMENTPRECISION = PropertyUtil.getSchemaProperty("attribute_MeasurementPrecision");
	public static final String SELECT_ATTRIBUTE_PGMEASUREMENTPRECISION = "attribute[" + ATTRIBUTE_PGMEASUREMENTPRECISION + "]";
	public static final String ATTRIBUTE_PGTESTMETHODREFDOCGCAS = PropertyUtil.getSchemaProperty("attribute_pgTestMethodRefDocGCAS");
	public static final String SELECT_ATTRIBUTE_PGTESTMETHODREFDOCGCAS = "attribute[" + ATTRIBUTE_PGTESTMETHODREFDOCGCAS + "]";
	public static final String ATTRIBUTE_PGPLBATTERYWEIGHTLIUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryWeightLiUOM");
	public static final String ATTRIBUTE_PGPLBATTERYENRUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryEnRUOM");
	public static final String ATTRIBUTE_PGPLBATTERYVOLUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryVolUOM");
	public static final String ATTRIBUTE_PGPLBATTERYTCUOM = PropertyUtil.getSchemaProperty("attribute_pgPLBatteryTCUOM");
	public static final String ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID = PropertyUtil.getSchemaProperty("attribute_pgConductivityoftheLiquid");
	public static final String SELECT_ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID = "attribute[" + ATTRIBUTE_PGCONDUCTIVITYOFTHELIQUID + "]";
	public static final String ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE = PropertyUtil.getSchemaProperty("attribute_pgProductPotentialToIncreaseBurningRate");
	public static final String SELECT_ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE = "attribute[" + ATTRIBUTE_PGPRODUCTTOINCREASEBURNRATE + "]";
	public static final String PATTERN_DECIMALFORMAT = "0.000000";
	public static final String strSequence1 =  (String)PropertyUtil.getSchemaProperty("attribute_pgSequence");
	public static final String ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE = PropertyUtil.getSchemaProperty("attribute_PercentPostIndustrialRecyclate");
	public static final String SELECT_ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE = "attribute[" + ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE + "]";
	public static final String SELECT_MANUFACTURER = "Manufacturer";
	public static final String SELECT_SUPPLIER = "Supplier";
	public static final String DYNAMIC_ROW_SEPERATOR = "~#~";	
	public static final String ROLE_INTERNAL_USER = "pgInternalUser";	
	public static final String ID_CONNECTION="id[connection]";
	public static final String STRING_NO_COUNTRIES_COMPONENTS = "Contains no components that impact COS";
	public static final String SELECT_ATTRIBUTE_PGCOSRESTRICTION ="attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]";
	public static final String NO_MARKET="No Market";
	public static final String SEQUENCE_NUMBER="Sequence Number";
	public static final String MASTER_SPECIFICATION="Master Specification";
	public static final String SYMBOL_COLON = ":";
	public static final String TARGET_COMPONENT ="Target Component";
	public static final String CONST_REGEXP = "<[^>]*>";
	public static final String CONST_AMP = "&amp;nbsp;";
	public static final String CONST_NBSP = "&nbsp;";
	public static final String CONST_BREAK = "<br></br>;";
	public static final String SYMBOL_HYPHEN_STRICT="-";
	public static final String CONST_BREAK_CLOSE = "<br/>";
	public static final String CONST_BREAK_CLOSE_SPACE = "<br />";
	public static final String CONST_CLOSE_BREAK= "</br>";
	public static final String CONST_LESSTHAN_SYMB = "[<]";
	public static final String CONST_GREATERTHAN_SYMB = "[>]";
	public static final String CONST_LESSTHAN = "#LESS_THAN";
	public static final String CONST_GREATERTHAN = "#GREATER_THAN";
	public static final String CONST_SPACE = "";
	public static final String CONST_DOUBLESPACE = " ";
	public static final String CONST_INPUTVALUE = ".inputvalue";
	public static final String ATTRIBUTE_MARKETING_NAME = "to["+pgV3Constants.RELATIONSHIP_OWNINGPRODUCTLINE+"].from["+pgV3Constants.TYPE_PG_GLOBALFORM+"].attribute["+pgV3Constants.ATTRIBUTE_MARKETING_NAME+"]";
	public static final String RELATIONSHIP_OWNINGPRODUCTLINE ="to["+pgV3Constants.RELATIONSHIP_OWNINGPRODUCTLINE+"].from.name";
	public static final String  CONST_NO = "No";
	public static final String  CONST_YES ="Yes";
	public static final String  CONST_ACESS = "current.access[fromdisconnect]";
	public static final String CONST_CLASS_LIBRARY_ACCESS = "No Access";
	public static final String  CONST_TRUE ="TRUE";
	public static final String  CONST_FALSE ="FALSE";
	public static final String  ATTRIBUTE_PGPDTEMPLATE = "from[pgPDTemplatestopgPLISegment].to.name";
	public static final String  BASE_TAG = "Basic_";
	public static final String  XML_AND = "_AND_";
	public static final String  SERVER_PATH = "pdfGenerationToolBase";
	public static final String  ALL_INFORMATION_VIEW="All Information View";
	
}
