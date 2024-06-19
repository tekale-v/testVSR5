/**
  JAVA class created for IRM 2018x.3
   Project Name: IRM(Sogeti)
   JAVA Name: PDFConstants
   Purpose: JAVA class created for constant variables.
 **/

package com.pg.irm.pdf;

import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.v3.custom.pgV3Constants;

public interface PDFConstants extends pgV3Constants {

	public String CONST_WORK_DIR="workDir";
	public String CONST_PDF_BASE_FOLDER="pdfHtmlBase";
	public String CONST_SUITE_KEY="suiteKey";
	public String CONST_MODE="mode";
	
	public String CONST_TRANSACTION_TYPE="TransactionType";

	public String TYPE_GPS_ASSESSMENT_TASK = PropertyUtil.getSchemaProperty("type_pgGPSAssessmentTask");
		
	public String ATTRIBUTE_GPS_ASSESSMENT_CATEGORY = PropertyUtil.getSchemaProperty("attribute_pgGPSAssessmentCategory");
	public String SELECT_ATTRIBUTE_GPS_ASSESSMENT_CATEGORY = "attribute[" + ATTRIBUTE_GPS_ASSESSMENT_CATEGORY + "]";

	public String ELEMENT_PROPERTIES="Properties";
	public String ELEMENT_GPSASSESSMENT="GPSAssessment";
	public String ELEMENT_INPUTS="Inputs";
	public String ELEMENT_INPUT="Input";
	public String ELEMENT_OWNERSHIPS="Ownerships";
	public String ELEMENT_OWNERSHIP="Ownership";
	public String ELEMENT_COUNTRIES="Countries";
	public String ELEMENT_COUNTRY="Country";
	public String ELEMENT_ATTRIBUTE_CATEGORY="category";
	public String CONSTANT_APP_CPN="emxCPN";
	public String CONSTANT_SRV_PATH="emxCPN.ServerPath";
	public String CONSTANT_MAPPING_JSON_FILE="Mapping.json";
	public String CONSTANT_FOLDER_GPS_ASSESSMENT="GPSAssessment";
	public String CONSTANT_FOLDER_ALLINFO="AllInfo";
	public String CONSTANT_FOLDER_LICENSE="license";
	public String CONSTANT_FOLDER_FONTS="fonts";
	public String CONSTANT_3DS_FONT="3ds-Regular.ttf";


	public String CONSTANT_REQUEST_CATEGORY = "request_category";
	public String CONSTANT_VIEW_KIND = "view_kind";
	public String VIEW_ALL_INFO = "allinfo";

	public String ALL_INFO_VIEW_DISPLAY_NAME="All Information View";
	public String GPS_ASSESSMENT_REQUEST_CATEGORY="GPS Assessment Request Category";
	public String CONSTANT_COMPANY_DISPLAY_NAME="The Procter & Gamble Company";

	public String IRM_PDF_VIEW_MAPPING_FILE="IRMPDFViewMapping";
	public String SYMBOL_COMMA=",";
	public String SYMBOL_SPACE=" ";
	public String SYMBOL_HYPHEN=" - ";
	public String SYMBOL_HYPHEN_STRICT="-";
	public String SYMBOL_OPEN_BRACKET="(";
	public String SYMBOL_CLOSE_BRACKET=")";

	public String RAW_MATERIAL_READINESS_GUIDANCE="A3A";
	public String FIRST_MEP_SEP="A3B";
	public String PACKAGING_MCP="A3C";
	public String ADDITONAL_MEP_SEP="A4";
	public String TEAM_TEST="A5";
	public String INVESTIGATIONAL_USE_CONSUMER_TEST="A6";
	public String INVESTIGATIONAL_USE_WITH_SALES="A7A";
	public String COMMERCIAL_USE_LIMITED_QUANTITY="A7B";
	public String COMMERCIAL_USE="A8";

	public String PDF_HEADER_A3A="A3A";
	public String ALL_INFO_VIEW="All Info View";
	public String PDF_OUTPUT_3DS_FONT="r"; 

	public String XSL_FILE_PATH="";
	public String PDF_PAGE_HEIGHT="";

	public String  PDF_HEADER_CONST_SECURITY_CLASSIFICATION= "Constant_Security_Classification";
	public String  PDF_HEADER_CONST_COMPANY_AND_VIEW= "Constant_Company_and_View";

	public String PDF_HEADER_NAME_COLON = "Name: ";
	public String PDF_HEADER_CONST_NAME_COLON = "Constant_Name_Colon";

	public String PDF_HEADER_REVISION_COLON = "Revision: ";
	public String PDF_HEADER_CONST_REVISION_COLON = "Constant_Revision_Colon";

	public String PDF_HEADER_STATE_COLON = "State: ";
	public String PDF_HEADER_CONST_STATE_COLON = "Constant_State_Colon";

	public String PDF_HEADER_HAS_ATS_COLON = "Has ATS: ";
	public String PDF_HEADER_CONST_HAS_ATS_COLON = "Constant_HasATS_Colon";

	public String CONSTANT_TYPE_KIND = " type_kind";

	public String PDF_HEADER_CONST_ATTRIBUTE_GPS_DISPLAY_NAME = "Constant_Attribute_GPS";


	public String CONSTANT_BUSINESS_USE_CASE_VIEW = "Business Use";
	public String CONSTANT_RESTRICTED_VIEW = "Restricted";

	public String CONSTANT_NO = "No";

	public String CONST_PAGE1_HEADER1_COL1 = "page1.header1.col1";
	public String CONST_PAGE1_HEADER1_CENTER = "page1.header1.center";
	public String CONST_PAGE1_HEADER2_CENTER = "page1.header2.center";
	public String CONST_PAGE1_HEADER3_COL1 = "page1.header3.col1";
	public String CONST_PAGE1_HEADER3_COL2 = "page1.header3.col2";
	public String CONST_PAGE1_HEADER3_COL3 = "page1.header3.col3";
	public String CONST_PAGE1_HEADER3_COL4 = "page1.header3.col4";
	public String CONST_PAGE1_HEADER4_COL4 = "page1.header4.col4";
	public String CONST_PAGE2_HEADER2_CENTER = "page2.header2.center";
	public String CONST_PAGE2_HEADER3_COL3 = "page2.header3.col3";
	public String CONST_PAGE2_HEADER3_COL4 = "page2.header3.col4";

	public String CONSTANT_COLUMN_MAP = "columnMap";
	public String CONSTANT_CATEGORY_MAP = "categoryMap";
	public String CONSTANT_HTML_DATA = "HTML_DATA";
	public String CONSTANT_PDF_FILE_NAME = "PDF_FILE_NAME";
	public String CONSTANT_PDF_PAY_LOAD = "PDF_PAY_LOAD";
	public String CONSTANT_PDF_FILE_PREFIX = "IRM_GPS_";
	public String CONSTANT_PDF_FILE_EXTENSION = ".pdf";
	public String CONSTANT_GPS_MAIN_XSL = "Main.xsl";
	public String CONSTANT_PDF_MERGE_FILE_NAME = "Merge.pdf";

	public String CONSTANT_JAVA_IO_TMP_DIR = "java.io.tmpdir";
	public String CONSTANT_UTF_8 = "UTF-8";
	public String CONSTANT_TEMP_PREFIX = "temp_";
	public String CONSTANT_PAGE = " Page ";
	public String CONSTANT_OF = " of ";
	public String CONSTANT_DENIED ="DENIED";
	public String CONSTANT_NULL ="null";
	public String CONSTANT_FILE_SEPARATOR ="file.separator";

	public String CONSTANT_GPS_TASK_XML_PAGE = "pgIRMGPSTaskXML";
	public String CONSTANT_CATEGORY ="category";
	public String CONSTANT_HIDE ="hide";
	public String CONSTANT_TRUE ="true";
	public String CONSTANT_FALSE ="false";
	public String CONSTANT_COLUMNS ="columns";
	public String CONSTANT_FIELDS ="fields";
	public String CONSTANT_NAME ="name";
	public String CONSTANT_DISPLAY ="display";
	public String CONSTANT_PROGRAM ="program";
	public String CONSTANT_TABLES ="tables";
	public String CONSTANT_CATEGORIES ="categories";
	public String CONSTANT_FORMS ="forms";
	public String CONSTANT_SECTIONS ="sections";

	public String CONSTANT_TABLE ="table";
	public String CONSTANT_UPDATE ="update";
	public String CONSTANT_MAJOR ="major";
	public String CONSTANT_MINOR ="minor";
	public String CONSTANT_FORM ="form";
	public String CONSTANT_OBJECT_ID ="objectId";
	public String CONSTANT_TIMESTAMP="timeStamp";
	public String CONSTANT_ACCEPT_LANGUAGE="Accept-Language";
	public String CONSTANT_MAX_COLS ="max cols";
	public String CONSTANT_HAS_ACCESS ="hasAccess";
	public String SECTION_ESTIMATED_CONTEXT_OF_USE ="EstimatedContextOfUseSection";
	
	public String CONSTANT_GPS_TASK_XSL_PAGE ="pgIRMGPSTaskXSL";
	
	public String CONST_ADMIN_TYPE ="Admin Type";
	public String CONST_TYPE ="type";
	public String CONST_ATTRIBUTE ="attribute";
	public String CONST_ROLE ="Role";
	
	public String CONST_POLICY ="Policy";
	public String CONST_GROUP ="Group";
	public String CONST_VAULT ="Vault";
	public String CONST_RELATIONSHIP ="Relationship";
	public String CONST_UNDERSCORE ="_";
	public String CONST_DATA_LOADED ="DataLoaded";
	public String CONST_SORT_COLUMN_NAME ="sortColumnName";
	public String CONST_UI_TYPE="uiType";
	public String CONST_UI_TABLE ="Table";
	
	public String CONST_BUSINESS_OBJECT="Businessobject";
	public String CONST_PROGRAM="Program";
	public String CONST_FILE="File";
	public String CONST_IMAGE="Image";
	public String CONST_CHECK_BOX="Checkbox";
	public String CONST_ICONS="Icons";
	public String CONST_RMB_MENU="RMB Menu";
	public String CONST_NO_WRAP="Nowrap";
	public String CONST_COLUMN_TYPE="Column Type";
	public String CONST_FORMAT="format";
	public String CONST_ALTERNATE_OID_EXPRESSION="Alternate OID expression";
	public String CONST_UOM_INPUT_SELECT="UOM Input Select";
	public String CONST_UOM_SYSTEM_SELECT="UOM System Select";
	public String CONST_UOM_DB_SELECT="UOM Db Select";
	
	public String CONST_BO = "businessobject";
	public String CONST_REL = "relationship";
	
	public String CONST_ATTRIBUTE_DATA_TYPE = "Attribute Data Type";
	public String CONST_FIELD_TYPE = "Field Type";
	public String CONST_STATE = "State";
	
	public String CONST_ALTERNATE_POLICY_EXPRESSION ="Alternate Policy expression";
	public String CONST_PROGRAM_HTML_OUTPUT ="programHTMLOutput";
	public String CONST_ATTRIBUTE_UNDERSCORE="attribute_";
	public String CONST_BOOLEAN="boolean";
	public String CONST_ACTUAL_VALUE="ActualValue";
	public String CONST_DISPLAY_VALUE="DisplayValue";
	public String CONST_ON="On";
	public String CONST_YES="Yes";
	public String CONST_OBJECT_LIST="ObjectList";
	public String CONST_VIEW_MODE="view";
	
	public String CONST_ASSOCIATED_WITH_UOM="AssociatedWithUOM";
	public String CONST_UBOM_DISPLAY_VALUE="uom_display_value";
	
	//Added For Defect 32707
	public String CONST_PG_MARKINGS="P&G";
	public String CONST_PRINTED="Printed";

	// Added for requirement 35469
	String ATTRIBUTE_BUSINESS_AREA = PropertyUtil.getSchemaProperty("attribute_pgBusinessArea");
	String SELECT_ATTRIBUTE_BUSINESS_AREA = "attribute["+ATTRIBUTE_BUSINESS_AREA+"]";
	String XML_CONFIG_TABLE_COLUMN_NAME_TO_BE_ASSESSED_BUSINESS_AREA = "pgTobeAssessedBusinessArea";
	String XML_CONFIG_TABLE_NAME_CONTEXT_OF_USE = "pgContextofUseTable";
	
	//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Starts
	String XML_CONFIG_TABLE_NAME_IMPORT_CONTEXT_OF_USE = "pgImportCOUTable";
	String TABLE_IMPORT_COU_INDIRECT_MAJOR = "InDirectImports";
	String TABLE_IMPORT_COU_INDIRECT_MINOR = "InDirectImport";
	String IMPORT_DIRECT_COLUMN_LIST="pgIsMaterialClassifiedMixtureorSubstance,PartName,Revision,Title,MEPName,pgEEAReceivingSites,pgEstimatedAnnualVolumeUoM,pgDirectICoUComments,pgICOUManufacturer,pgReceivingOtherPlant,pgICOUVender";
	String IMPORT_INDIRECT_COLUMN_LIST="pgIsMaterialImportedIntoEEA,pgFinishedProductImportedAs,PartName,Revision,Title,MEPName,pgNonEEASupplying,pgEEABasedContractPGSites,pgEstimatedAnnualRMQuantity,pgInDirectICoUComments";
	String IMPORT_INDIRECT_CATEGORIES="A3b,A4";
	String ATTRIBUTE_PGISMATERIAL_IMPORTED_INTOEEA = PropertyUtil.getSchemaProperty(null, "attribute_pgIsMaterialImportedIntoEEA");
	String SELECT_ATTRIBUTE_PGISMATERIAL_IMPORTED_INTOEEA = "attribute["+ATTRIBUTE_PGISMATERIAL_IMPORTED_INTOEEA+"]";
	String ATTRIBUTE_PGFINISHEDPRODUCT_IMPORTED_AS = PropertyUtil.getSchemaProperty(null, "attribute_pgFinishedProductImportedAs");
	String SELECT_ATTRIBUTE_PGFINISHEDPRODUCT_IMPORTED_AS = "attribute["+ATTRIBUTE_PGFINISHEDPRODUCT_IMPORTED_AS+"]";
	String ATTRIBUTE_PGISMATERIAL_CLASSIFIEDMIXTURE_OR_SUBSTANCE = PropertyUtil.getSchemaProperty(null, "attribute_pgIsMaterialClassifiedMixtureorSubstance");
	String SELECT_ATTRIBUTE_PGISMATERIAL_CLASSIFIEDMIXTURE_OR_SUBSTANCE = "attribute["+ATTRIBUTE_PGISMATERIAL_CLASSIFIEDMIXTURE_OR_SUBSTANCE+"]";
	String XML_CONFIG_TABLE_COLUMN_NAME_PGISMATERIALIMPORTEDINTOEEA = "pgIsMaterialImportedIntoEEA";
	String XML_CONFIG_TABLE_COLUMN_NAME_PGFINISHEDPRODUCTIMPORTEDAS = "pgFinishedProductImportedAs";
	String XML_CONFIG_TABLE_COLUMN_NAME_PGISMATERIALCLASSIFIEDMIXTUREORSUBSTANCE = "pgIsMaterialClassifiedMixtureorSubstance";
	//Added by IRM pdf views 2018x.6 for Requirements 37573,37575,37577 Ends
	//Added by IRM team in 2018x.6 for the Defect #39830 Starts
	String ATTRIBUTE_PGOTHERDETAILS = PropertyUtil.getSchemaProperty(null, "attribute_pgOtherDetails");
	String SELECT_ATTRIBUTE_PGOTHERDETAILS = "attribute["+ATTRIBUTE_PGOTHERDETAILS+"]";
	//Added by IRM team in 2018x.6 for the Defect #39830 Starts
	//Added by IRM team in 2018x.6 for the Defect #43129 Starts
	public String CONST_DYNAMIC="dynamic";
	public String KEY_NO_VALUE="No";
	String ATTRIBUTE_PGPKGDEVELOPMENTTYPE = PropertyUtil.getSchemaProperty(null, "attribute_pgPKGDevelopmentType");
	String SELECT_ATTRIBUTE_PGPKGDEVELOPMENTTYPE = "attribute["+ATTRIBUTE_PGPKGDEVELOPMENTTYPE+"]";
	//Added by IRM team in 2018x.6 for the Defect #43129 Ends 
	// Added by (Sogeti) for Requirement (42813) 2018x.6 May CW 2022 Release - Start
	String ATTRIBUTE_PG_PACK_SIZE = PropertyUtil.getSchemaProperty(null, "attribute_pgPackSize");
	String ATTRIBUTE_PG_COUNT_UOM_OTHER = PropertyUtil.getSchemaProperty(null, "attribute_pgCountUoMOther");
	String ATTRIBUTE_PG_COUNT=PropertyUtil.getSchemaProperty(null, "attribute_TotalCount");
	String ATTRIBUTE_PG_PACK_SIZE_OTHER=PropertyUtil.getSchemaProperty(null, "attribute_pgOther");
	String SELECT_ATTRIBUTE_PG_PACK_SIZE = "attribute["+ATTRIBUTE_PG_PACK_SIZE+"].inputvalue";
	String SELECT_ATTRIBUTE_PG_COUNT_UOM_OTHER = "attribute["+ATTRIBUTE_PG_COUNT_UOM_OTHER+"]";
	String SELECT_ATTRIBUTE_PG_COUNT = "attribute["+ATTRIBUTE_PG_COUNT+"]";
	String SELECT_ATTRIBUTE_PG_PACK_SIZE_OTHER = "attribute["+ATTRIBUTE_PG_PACK_SIZE_OTHER+"]";
	String COUNTRIES_TABLE_RELATIONSHIP_COULMN_NAMES= "PackSize,pgCountUoMOther,TotalCount,pgPackSizeUoMOther";
	String COUNTRIES_TABLE_RELATIONSHIP_COULMN_SELECTS= SELECT_ATTRIBUTE_PG_PACK_SIZE+","+SELECT_ATTRIBUTE_PG_COUNT_UOM_OTHER+","+SELECT_ATTRIBUTE_PG_COUNT+","+SELECT_ATTRIBUTE_PG_PACK_SIZE_OTHER;
	// Added by (Sogeti) for Requirement (42813) 2018x.6 May CW 2022 Release - Ends
}
