package com.pg.designtools.datamanagement;

import java.util.Vector;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.util.PropertyUtil;

/**
 * Class of constants for the Design Tool space that should be continually
 * evolving to use pgDSOConstants as the common root pgDSOConstants already
 * implements implements DomainConstants, CPNCommonConstants
 * 
 * @author GQS
 *
 */

public class DataConstants {

	public enum customTOPSExceptions {
		
		ERROR_400(401,"SPS encountered error from processing or business checks"),
		ERROR_500(500,"Unknown system error"),
		ERROR_400_INVALID_XML_FORMAT(402,"Error in the process of the XML document format"),
		ERROR_400_INVALID_XML_CONTENT(403,"Error in the content processing of the XML document format"),
		ERROR_400_PRODUCT_DATA_ACCESS(404,"Error due to lack of access in Product Data"),
		ERROR_400_INVALID_SPS_ORIGINATION(405,"Error due to SPS having origination value = Manual"),
		ERROR_400_INVALID_SPS_NAME(406,"XML does not have a valid SPS Name"),
		ERROR_400_INVALID_SPS_TUP_CURRENT_STATE(407,"SPS current state is Released or connected TUP current state is not preliminary"),
		ERROR_400_MAJOR_OBJECT_LOCKED(408,"Major version locked by another user"),
		ERROR_400_MINOR_OBJECT_LOCKED(409,"Minor version locked by another user"),
		ERROR_400_UNSUPPORTED_UOM(410,"Unsupported unit of measure"),
		ERROR_400_ANALYSIS0_TAG_NOT_PRESENT(411,"analysis0 tag not present in XML"),
		ERROR_400_INVALID_STRUCTURE_TYPE(412,"Structure type of XML is not Analysis"),
		ERROR_400_NO_DEFAULT_IP_CONTROL(413," No Default IP Control Classes defined for the logged in User"),
		ERROR_400_FILE_FORMAT_NOT_VALID(414," File format can be either xml or pdf"),
		ERROR_400_SRCFOLDER_NOT_PRESENT(415," srcFolder path is not present in input parameters from TOPS"),
		ERROR_400_TUP_NOT_PRESENT(416," TUP not present for SPS"),
		ERROR_400_NO_REVISION_INSIDE_NAME(417," TOPS Client did not pass SPS Name correctly- correct format is SPS-00000834_001 or SPS-00000834.001"),
		ERROR_400_NO_ROOT_TAG_IN_XML(418," root tag not present in xml"),
		
		ERROR_400_NO_LOCK_ACCESS_ON_IPM(419," No Lock Access on IPM Document"),
		ERROR_400_NO_MODIFY_ACCESS_ON_SPS(420," No Modify Access on SPS"),
		ERROR_400_SPS_LOCKED_BY_ANOTHER_USER(421," SPS is locked by User "),
		ERROR_400_TUP_LOCKED_BY_ANOTHER_USER(422," TUP is locked by User"),
		ERROR_400_TUP_HAVE_MORE_THAN_ONE_SPS(423," TUP has more than one SPS connected"),
    	ERROR_400_ATTR_VALUE_DIFFERENCE_IN_DB_XML_UNITTYPE(424,"  cannot be updated on a Release part: "),
		ERROR_400_MANUAL_SPS_CONNECTED_TO_TUP(425,"Save TOPS XML cannot be performed, as SPS with Manual origination is connected to TUP"),
		ERROR_400_NO_CONTENT(426,"No file content present for the document ");
			
		private String exceptionMessage;
		private int exceptionCode;
		
		customTOPSExceptions(int code, String message) {
			this.exceptionCode = code;
	        this.exceptionMessage = message;
	    }
	 
	    public String getExceptionMessage() {
	        return exceptionMessage;
	    }
	    
	    public int getExceptionCode() {
	        return exceptionCode;
	    }

	}

	public enum customCATIAExceptions{
		ERROR_400_INVALID_EVENT(401,"Job Event is invalid"),
		ERROR_400_JOB_INTERFACE_NOT_PRESENT(402,"DTTransientJobExtension interface is not present on VPMReference object"),
		ERROR_400_MULTIPLE_VPMREFERENCE_CONNECTED(403,"Multiple VPMReference objects are connected to the EC Part");	
		
		private String exceptionMessage;
		private int exceptionCode;
		
		customCATIAExceptions(int code, String message) {
			this.exceptionCode = code;
	        this.exceptionMessage = message;
	    }
	 
	    public String getExceptionMessage() {
	        return exceptionMessage;
	    }
	    
	    public int getExceptionCode() {
	        return exceptionCode;
	    }
	}
	
	public enum customWorkProcessD2SExceptions{		//(Design to Spec)
		ERROR_500(500,"Unknown system error"),
		ERROR_400_MANDATORY_ATTRIBUTES_NOT_ADDED(401,"mandatory attributes"),
		ERROR_400_NO_ECPART_CONNECTED(402,NO_EC_PART_CONNECTED),
		ERROR_400_INPUT_FILE_PATH_MISSING(403,"Pass the Input file full path with file name"),
		ERROR_400_EXECUTION_MODE_MISSING(404,"Please specify the mode of execution: scan or make"),
		ERROR_400_CSVREADER_MANDATORY_FIELDS_MISSING(405,"Mandatory values are not populated for line "),
		ERROR_400_VPMREF_NAME_MISSING(406,"Name of the VPMReference object is not present in the input file"),
		ERROR_400_ECPART_NAME_MISSING(407,"Name of the source Enterprise type object is not present in the input file"),
		ERROR_400_OWNER_NAME_MISSING(408,"Owner is not mentioned in the input file"),
		ERROR_400_ACCESS_MISSING(409,"Mentioned Owner does not have relevant access"),
		ERROR_400_NOT_LATEST_REVISION_VPMREF(410,"The revision mentioned in the input file for the VPMReference object is not the latest revision"),
		ERROR_400_NOT_LATEST_REVISION_ECPART(411,"The revision mentioned in the input file for the source Enterprise type object is not the latest revision"),
		ERROR_400_NOT_RELEASED_ECPART(412,"The source Enterprise type object is neither in In Work state nor released"),
		ERROR_400_NOT_STANDALONE_ECPART(413,"The source Enterprise type object is not standalone. It has connected VPMReference object"),
		ERROR_400_VPMREFERENCE_NOT_VALID(414,"The VPMReference object is not valid object to proceed further"),
		ERROR_400_ECPART_NOT_VALID(415,"The source Enterprise type object is not valid object to proceed further"),
		ERROR_400_TRANSACTION_ABORTED(501,"The transaction is aborted due to some error while creating new objects. All the newly created objects are deleted"),
		MESSAGE_200_VPMREFERENCE_VALID(201,"The VPMReference object is valid object to proceed further"),
		MESSAGE_200_ECPART_VALID(202,"The source Enterprise type object is valid object to proceed further"),
		MESSAGE_200_NEW_ECPART_REVISION(203,"New revision of source Enterprise type object is created successfully"),
		MESSAGE_200_NEW_VPMREF_EVOLUTION(204,"Evolution of VPMReference is created successfully"),
		MESSAGE_200_NEW_VPMREF_EVOLUTION_AND_REVISION(205,"Evolution of VPMReference and its revision are created successfully");
		
		private String exceptionMessage;
		private int exceptionCode;
		
		customWorkProcessD2SExceptions(int code, String message) {
			this.exceptionCode = code;
	        this.exceptionMessage = message;
	    }
	 
	    public String getExceptionMessage() {
	        return exceptionMessage;
	    }
	    
	    public int getExceptionCode() {
	        return exceptionCode;
	    }
	}

	public enum invalidChar_FileName {
		SPECIAL_CHAR_HASH("#"),SPECIAL_CHAR_DOLLAR("$"),SPECIAL_CHAR_AT_SIGN("@"), SPECIAL_CHAR_PERCENT("%"), SPECIAL_CHAR_AMPERSAND("&"), SPECIAL_CHAR_EURO("\u20AC"),
		SPECIAL_CHAR_POUND("\u00A3"), SPECIAL_CHAR_HYPEN1("\u2015"), SPECIAL_CHAR_HYPEN2("\u2014"), SPECIAL_CHAR_HYPEN3("\u2013"), SPECIAL_CHAR_HYPEN4("\u2012"),
		SPECIAL_CHAR_SQUARE_OPEN_BRACKET("["), SPECIAL_CHAR_SQUARE_CLOSE_BRACKET("]"), SPECIAL_CHAR_CURLY_CLOSE_BRACKET("}"), SPECIAL_CHAR_CURLY_OPEN_BRACKET("{"),
		SPECIAL_CHAR_PIPE("|"),SPECIAL_CHAR_BACKSLASH("\\\\"),SPECIAL_CHAR_APOSTROPHE("\\'"), SPECIAL_CHAR_DOUBLE_APOSTROPHE("\""), SPECIAL_CHAR_DOUBLE_COMMA(",,"), 
		SPECIAL_CHAR_DOUBLE_HASH("##"), SPECIAL_CHAR_EUROPEAN_1("\u00C0"),	SPECIAL_CHAR_EUROPEAN_2("\u00C1"), SPECIAL_CHAR_EUROPEAN_3("\u00C2"), SPECIAL_CHAR_EUROPEAN_4("\u00C3"), 
		SPECIAL_CHAR_EUROPEAN_5("\u00C4"), SPECIAL_CHAR_EUROPEAN_6("\u00C5"), SPECIAL_CHAR_EUROPEAN_7("\u00C6"),	SPECIAL_CHAR_EUROPEAN_8("\u00C7"), SPECIAL_CHAR_EUROPEAN_9("\u00C8"), 
		SPECIAL_CHAR_EUROPEAN_10("\u00C9"), SPECIAL_CHAR_EUROPEAN_11("\u00CA"), SPECIAL_CHAR_EUROPEAN_12("\u00CB"),	SPECIAL_CHAR_EUROPEAN_13("\u00CC"), SPECIAL_CHAR_EUROPEAN_14("\u00CD"),
		SPECIAL_CHAR_EUROPEAN_15("\u00CE"), SPECIAL_CHAR_EUROPEAN_16("\u00CF"), SPECIAL_CHAR_EUROPEAN_17("\u00D0"),SPECIAL_CHAR_EUROPEAN_18("\u00D1"), SPECIAL_CHAR_EUROPEAN_19("\u00D2"),
		SPECIAL_CHAR_EUROPEAN_20("\u00D3"), SPECIAL_CHAR_EUROPEAN_21("\u00D4"), SPECIAL_CHAR_EUROPEAN_22("\u00D5"),SPECIAL_CHAR_EUROPEAN_23("\u00D6"), SPECIAL_CHAR_EUROPEAN_24("\u00D8"),
		SPECIAL_CHAR_EUROPEAN_25("\u00D9"), SPECIAL_CHAR_EUROPEAN_26("\u00DA"), SPECIAL_CHAR_EUROPEAN_27("\u00DB"),SPECIAL_CHAR_EUROPEAN_28("\u00DC"), SPECIAL_CHAR_EUROPEAN_29("\u00DD"),
		SPECIAL_CHAR_EUROPEAN_30("\u00DE"), SPECIAL_CHAR_EUROPEAN_31("\u00DF"), SPECIAL_CHAR_EUROPEAN_32("\u00E0"),SPECIAL_CHAR_EUROPEAN_33("\u00E1"), SPECIAL_CHAR_EUROPEAN_34("\u00E2"), 
		SPECIAL_CHAR_EUROPEAN_35("\u00E3"), SPECIAL_CHAR_EUROPEAN_36("\u00E4"), SPECIAL_CHAR_EUROPEAN_37("\u00E5"),	SPECIAL_CHAR_EUROPEAN_38("\u00E6"), SPECIAL_CHAR_EUROPEAN_39("\u00E7"),
		SPECIAL_CHAR_EUROPEAN_40("\u00E8"), SPECIAL_CHAR_EUROPEAN_41("\u00E9"), SPECIAL_CHAR_EUROPEAN_42("\u00EA"),	SPECIAL_CHAR_EUROPEAN_43("\u00EB"),SPECIAL_CHAR_EUROPEAN_44("\u00EC"),
		SPECIAL_CHAR_EUROPEAN_45("\u00ED"), SPECIAL_CHAR_EUROPEAN_46("\u00EE"),SPECIAL_CHAR_EUROPEAN_47("\u00EF"),	SPECIAL_CHAR_EUROPEAN_48("\u00F0"),SPECIAL_CHAR_EUROPEAN_49("\u00F1"),
		SPECIAL_CHAR_EUROPEAN_50("\u00F2"),SPECIAL_CHAR_EUROPEAN_51("\u00F3"),SPECIAL_CHAR_EUROPEAN_52("\u00F4"),SPECIAL_CHAR_EUROPEAN_53("\u00F5"), SPECIAL_CHAR_EUROPEAN_54("\u00F6"), 
		SPECIAL_CHAR_EUROPEAN_55("\u00F8"),SPECIAL_CHAR_EUROPEAN_56("\u00F9"), SPECIAL_CHAR_EUROPEAN_57("\u00FA"),	SPECIAL_CHAR_EUROPEAN_58("\u00FB"),SPECIAL_CHAR_EUROPEAN_59("\u00FC"), 
		SPECIAL_CHAR_EUROPEAN_60("\u00FD"),SPECIAL_CHAR_EUROPEAN_61("\u00FE"), SPECIAL_CHAR_EUROPEAN_62("\u00FF"); 
		 
		 String charValue;
		invalidChar_FileName(String sValue) {
		      charValue = sValue;
		   }
		  public  String getChar() {
		      return charValue;
		   } 
		}
	
	public enum customCATIAHomePage{	
		ERROR_400_DESIGN_FOR_NOT_SET(401,"Design->Design For"),
		ERROR_400_DEFAULT_CLASSIFICATION_NOT_SET(402,"Design->Default Classification"),
		ERROR_400_LPD_IP_CLASS_NOT_SET(403,"Security->Business Use IP Class and Security->Highly Restricted IP Class"),
		ERROR_400_PKG_IP_CLASS_NOT_SET(404,"Security->Business Use IP Class"),
		ERROR_400_IP_CLASS_NOT_SET(404,"Security->Business Use IP Class or Security->Highly Restricted IP Class"),
		ERROR_400_PREFERENCES_NOT_SET(405,"Preferences required to be set to continue:"),
		MESSAGE_200_PREFERENCES_SET(200,"Minimum required User Preferences are set");
			
		private String message;
		private int code;
		
		customCATIAHomePage(int code, String message) {
			this.code = code;
	        this.message = message;
	    }
	 
	    public String getMessage() {
	        return message;
	    }
	    
	    public int getCode() {
	        return code;
	    }
	}
	
	public static final String VAULT_ESERVICE_PRODUCTION = "eService Production";
	//START: DT18X7-64
	public static final String VAULT_VPLM;
	//END: DT18X7-64
	public static final String INTERFACE_IPSEC_CLASS;
	public static final String INTERFACE_DT_TRANSIENT_JOB_EXTENSION;

	public static final String INTERFACE_PG_TOPS_DRUM;
	public static final String INTERFACE_PG_TOPS_BUCKET_ROUND;
	public static final String INTERFACE_PG_TOPS_BUCKET_RECTANGULAR;
	public static final String INTERFACE_PG_TOPS_BOTTLE_ROUND;
	public static final String INTERFACE_PG_TOPS_BOTTLE_RECTANGULAR;
	public static final String INTERFACE_PG_TOPS_BOTTLE_OVAL;
	public static final String INTERFACE_PG_TOPS_FRA_DIMENSIONS;
	public static final String INTERFACE_PG_TOPS_CAN;
	public static final String INTERFACE_PG_TOPS_TUB_ROUND;
	public static final String INTERFACE_PG_TOPS_TUB_RECTANGULAR;
	public static final String INTERFACE_PG_TOPS_BLISTER_PACK;	
	public static final String INTERFACE_PNG_PACKAGING;
	public static final String INTERFACE_PNG_PRODUCT;
	public static final String INTERFACE_PNG_EXPLORATION;
	public static final String INTERFACE_PNG_ASSEMBLED;
	public static final String INTERFACE_PART_FAMILY_REFERENCE;
	public static final String INTERFACE_PNG_DESIGNPART;
	public static final String INTERFACE_XCADITEMEXTENSION;
	public static final  String INTERFACE_AUTOMATION_USAGE_EXTENSION;

	public static final String TYPE_PG_TRANSPORTUNIT;
	public static final String TYPE_PG_STACKINGPATTERN;
	public static final String TYPE_PG_MASTERCUSTOMERUNIT;
	public static final String TYPE_PG_MASTERCONSUMERUNIT;
	public static final String TYPE_PG_MASTERINNERPACKUNIT;
	
	public static final String TYPE_VPMREFERENCE;
	public static final String TYPE_SHAPE_PART;
	public static final String TYPE_DRAWING;
	public static final String TYPE_FEM;
	public static final String TYPE_SIM_SHAPE;
	public static final String TYPE_DSC_MATREF_REF_CORE;
	public static final String TYPE_MACRO_LIBRARY_VBA;
	public static final String TYPE_XCAD_MODEL_REP_REFERENCE;
	public static final String TYPE_SIMULATION_DOC_VERSIONED;
	public static final String TYPE_SIMULATION_DOC_NONVERSIONED;
	public static final String TYPE_REQUIREMENT;
	public static final String TYPE_REQUIREMENT_SPECIFICATION;
	public static final String TYPE_DESIGN_SIGHT;
	public static final String TYPE_ARTIOSCAD_COMPONENT;
	public static final String TYPE_IRM_DOCUMENT;
	public static final String TYPE_SIMULATION;
	public static final String TYPE_PACKING_INSTRUCTION;
	public static final String TYPE_DOCUMENT;
	public static final String TYPE_DOCUMENTS;
	public static final String TYPE_ORG_CHANGE_MGMT;
	public static final String TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART;
	public static final String TYPE_PG_MASTER_PACKAGING_MATERIAL_PART;
	public static final String TYPE_PG_MASTER_PRODUCT_PART;
	public static final String TYPE_PG_MASTER_RAW_MATERIAL_PART;
	public static final String TYPE_ASSEMBLED_PRODUCT_PART;
	public static final String TYPE_3DSHAPE;
	public static final String TYPE_IP_CONTROL_CLASS;
	public static final String TYPE_PLM_EXCHANGE_STATUS_DS;
	public static final String TYPE_TEMPLATE;
	public static final String TYPE_PLMDMTDOCUMENT;
	
	public static final String SCHEMA_TYPE_PG_TRANSPORTUNIT = "type_pgTransportUnitPart";
	public static final String SCHEMA_TYPE_PG_STACKINGPATTERN = "type_pgStackingPattern";
	public static final String SCHEMA_TYPE_PG_MASTERCUSTOMERUNIT = "type_pgMasterCustomerUnitPart";
	public static final String SCHEMA_TYPE_PG_MASTERCONSUMERUNIT = "type_pgMasterConsumerUnitPart";
	public static final String SCHEMA_TYPE_PG_MASTERINNERPACKUNIT = "type_pgMasterInnerPackUnitPart";
	public static final String SCHEMA_TYPE_PG_MASTERPACKAGINGMATERIALPART="type_pgMasterPackagingMaterialPart";
	
	public static final String TYPE_PGIPMDOCUMENT;

	public static final String SCHEMA_POLICY_EC_PART = "policy_ECPart";
	public static final String SCHEMA_POLICY_IPM_SPECIFICATION = "policy_IPMSpecification";
	public static final String TYPE_JOB;
	public static final String TYPE_DSC_MAT_CNX_COVERING_DESIGN;
	public static final String TYPE_DSC_MAT_CNX_CORE_DESIGN;
	public static final String TYPE_PROXY_OBJECT;
	public static final String TYPE_PRODUCT_DATA_PART;
	public static final String TYPE_PG_PLI_PALLET_TYPE;

	public static final String POLICY_IPM_SPECIFICATION;
	public static final String POLICY_IPM_DOCUMENT;
	public static final String POLICY_EC_PART;
	public static final String POLICY_PRODUCT_DATA_SPECIFICATION;
	public static final String POLICY_SIMULATION_DOCUMENT_LEGACY;
	public static final String POLICY_SIMULATION_DOCUMENT_OWNED;
	public static final String POLICY_SIMULATION_DOCUMENT;
	public static final String POLICY_PG_PKGWIPPART;

	public static final String REL_PART_SPECIFICATION;
	public static final String REL_REFERENCE_DOCUMENT;
	public static final String REL_VPM_REPINSTANCE;
	public static final String REL_XCADITEM;
	public static final String REL_EBOM;
	public static final String REL_SIMULATION_CONTENT_REFERENCED;
	public static final String REL_SIMULATION_CONTENT_OWNED;
	public static final String REL_PRIMARY_ORGANIZATION;
	public static final String REL_SECONDARY_ORGANIZATION;
    public static final String REL_VPM_INSTANCE;
	public static final String REL_PART_FAMILY_REFERENCE;
	public static final String REL_INHERITED_CAD_SPECIFICATION;
	public static final String REL_CLASSIFIED_ITEM;
	
	public static final String RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT;
	public static final String RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION;
	public static final String REL_PG_PRIMARY_ORGANIZATION;
	public static final String REL_PG_SECONDARY_ORGANIZATION;
	public static final String RELATIONSHIP_PENDING_JOB;
	public static final String RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER;
	public static final String RELATIONSHIP_TEMPLATE;
	public static final String RELATIONSHIP_DERIVED;
	public static final String ATTR_PG_ORIGINATINGSOURCE;
	public static final String ATTR_EXPIRATION_DATE;
	public static final String ATTRIBUTE_IS_TEMPLATE_APPLIED;
	public static final String ATTRIBUTE_REASONFORCHANGE;
		
	public static final String ATTR_RELEASE_PHASE;
	public static final String ATTR_PG_IP_CLASSIFICATION;
	public static final String ATTR_V_NAME;
	public static final String ATTRIBUTE_ISVPMVISIBLE;
	public static final String SELECT_ATTRIBUTE_ISVPMVISIBLE;
	public static final String ATTR_PG_SPS_ORIGINATION;
	public static final String SELECT_ATTR_PG_SPS_ORIGINATION;

	public static final String ATTR_OUTER_DIMENSION_LENGTH;
	public static final String ATTR_OUTER_DIMENSION_WIDTH;
	public static final String ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT;

	public static final String ATTRIBUTE_PG_DIAMETER;
	public static final String ATTRIBUTE_PG_TOPDIAMETER;
	public static final String ATTRIBUTE_PG_BOTTOMDIAMETER;
	public static final String ATTRIBUTE_PG_PITCH;
	public static final String ATTRIBUTE_PG_TOPDEPTH;
	public static final String ATTRIBUTE_PG_TOPWIDTH;
	public static final String ATTRIBUTE_PG_BOTTOMWIDTH;

	public static final String ATTRIBUTE_PG_BODYDEPTH;
	public static final String ATTRIBUTE_PG_BODYWIDTH;
	public static final String ATTRIBUTE_PG_NECKDIAMETER;
	public static final String ATTRIBUTE_PG_SHOULDERHEIGHT;
	public static final String ATTRIBUTE_PG_BODYDIAMETER;
	public static final String ATTRIBUTE_PG_NECKHEIGHT;
	public static final String ATTRIBUTE_PG_DIMENSTION_UOM;
	public static final String ATTRIBUTE_PG_TOP_INDENT;
	public static final String ATTRIBUTE_PG_BOTTOM_INDENT;
	public static final String ATTRIBUTE_PG_SIDE_INDENT;

	public static final String ATTR_AREA_EFFICIENCY;
	public static final String ATTR_PG_STACKING_PATTERN_TYPE;
	public static final String ATTR_PG_CUBE_EFFECIENCY;
	public static final String ATTRIBUTE_LAYERS_PERTRANSPORTUNIT;
	public static final String ATTRIBUTE_CUSTOMER_UNITS_PRELAYER;

	public static final String ATTR_UNDER_HANG_ACTUAL_LENGTH;
	public static final String ATTR_UNDER_HANG_ACTUAL_WIDTH;
	public static final String ATTR_OVER_HANG_ACTUAL_LENGTH;
	public static final String ATTR_OVER_HANG_ACTUAL_WIDTH;
	public static final String ATTR_TITLE;
	public static final String ATTR_PG_PALLETTYPE;
	public static final String SELECT_ATTR_PG_PALLETTYPE;
	public static final String ATTRIBUTE_PGENTERPRISETYPE;
	public static final String SELECT_ATTRIBUTE_PGENTERPRISETYPE;
	public static final String ATTRIBUTE_PG_JOB_CONFIG;
	public static final String ATTRIBUTE_ISVPLMCONTROLLED;
	public static final String SELECT_ATTRIBUTE_ISVPLMCONTROLLED;
	public static final String PERSON_USER_AGENT;
	public static final String ATTR_PG_SECONDARY_PACK_INFO;
	public static final String SELECT_ATTR_PG_SECONDARY_PACK_INFO ;
	public static final String ATTRIBUTE_LIFECYCLE_STATUS;
	public static final String ATTRIBUTE_V_DESCRIPTION;
	public static final String ATTRIBUTE_FIND_NUMBER;
	public static final String ATTRIBUTE_GEOMETRY_SHAPE;
	public static final String SELECT_ATTRIBUTE_GEOMETRY_SHAPE;
	public static final String ATTRIBUTE_QUANTITY;
	public static final String SELECT_ATTRIBUTE_QUANTITY;
	public static final String ATTR_PG_BODY_SHAPE; 
	
	public static final String ATTRIBUTE_XCAD_ENTERPRISE_TYPE;
	public static final String ATTRIBUTE_XCAD_MFG_MATURITY_STATUS;	
	public static final String ATTRIBUTE_XCAD_DESIGN_DOMAIN;
	public static final String ATTRIBUTE_XCAD_DEFAULTEXPLORATION;
	public static final String SELECT_CONNECTED_MASTER_PART_TYPE;
	public static final String SELECT_CONNECTED_MASTER_PART_ID;
	public static final String SELECT_MASTER_TYPE_INHERITED_CADOBJ_TYPE;
	public static final String SELECT_MASTER_TYPE_INHERITED_CADOBJ_ID;
	public static final String SELECT_MASTER_TYPE_INHERITED_CADOBJ_NAME;
	public static final String SELECT_MASTER_TYPE_INHERITED_CADOBJ_PHYSICALID;
	public static final String SELECT_MASTER_TYPE_INHERITED_CADOBJ_RELID;

 	public static final String ATTRIBUTE_PNG_CLONE_DERIVED_FROM;
	public static final String ATTRIBUTE_V_DERIVED_FROM;
	public static final String ATTRIBUTE_CAD_DESIGN_ORIGINATION;
	public static final String ATTRIBUTE_MFG_MATURITY_STATUS;
	public static final String ATTRIBUTE_DESIGN_DOMAIN;
	public static final String ATTRIBUTE_V_USAGE;
	public static final String SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM;
	public static final String SELECT_ATTRIBUTE_V_DERIVED_FROM;
	public static final String SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION;
	public static final String SELECT_ATTRIBUTE_V_DESCRIPTION;
	public static final String SELECT_ATTRIBUTE_MFG_MATURITY_STATUS;
	public static final String SELECT_ATTRIBUTE_DESIGN_DOMAIN;
	public static final String SELECT_ATTRIBUTE_V_USAGE;
	public static final String ATTRIBUTE_PG_MIG_DATE;
	public static final String ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION;
	public static final String ATTRIBUTE_PACKAGING_SEGMENT;
	public static final String SELECT_ATTRIBUTE_PACKAGING_SEGMENT;
	public static final String ATTRIBUTE_PACKAGING_REPORTED_FUNCTION;
	public static final String SELECT_ATTRIBUTE_PACKAGING_REPORTED_FUNCTION;
	public static final String ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION;
	public static final String SELECT_ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION;
	public static final String ATTRIBUTE_PACKAGING_RELEASE_CRITERIA;
	public static final String SELECT_ATTRIBUTE_PACKAGING_RELEASE_CRITERIA;
	public static final String ATTRIBUTE_PACKAGING_MATERIAL_TYPE;
	public static final String SELECT_ATTRIBUTE_PACKAGING_MATERIAL_TYPE;
	public static final String ATTRIBUTE_PACKAGING_CLASS;
	public static final String SELECT_ATTRIBUTE_PACKAGING_CLASS;
	public static final String ATTRIBUTE_PACKAGING_MFG_STATUS;
	public static final String SELECT_ATTRIBUTE_PACKAGING_MFG_STATUS;
	
	public static final String ATTRIBUTE_PRODUCT_SEGMENT;
	public static final String SELECT_ATTRIBUTE_PRODUCT_SEGMENT;
	public static final String ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION;
	public static final String SELECT_ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION;
	public static final String ATTRIBUTE_PRODUCT_RELEASE_CRITERIA;
	public static final String SELECT_ATTRIBUTE_PRODUCT_RELEASE_CRITERIA;
	public static final String ATTRIBUTE_PRODUCT_MFG_STATUS;
	public static final String SELECT_ATTRIBUTE_PRODUCT_MFG_STATUS;
	
	public static final String ATTRIBUTE_CLASS;
	public static final String ATTRIBUTE_SEGMENT;
	public static final String ATTRIBUTE_RELEASE_CRITERIA;
	public static final String ATTRIBUTE_MATERIAL_TYPE;
	
	public static final String ATTRIBUTE_PNG_UNDER_EBOM_CM_CONTROL;
	public static final String ATTRIBUTE_REFERENCE_TYPE;

	public static final String SELECT_ATTRIBUTE_LIFECYCLE_STATUS ;
	public static final String SELECT_ATTRIBUTE_CLASS;
	public static final String SELECT_ATTRIBUTE_SEGMENT;
	public static final String SELECT_ATTRIBUTE_RELEASE_CRITERIA;
	public static final String SELECT_ATTRIBUTE_MATERIAL_TYPE;
	public static final String ATTRIBUTE_REPORTED_FUNCTION;
	public static final String SELECT_ATTRIBUTE_REPORTED_FUNCTION;
	public static final String ATTRIBUTE_PLMINSTANCE_PLM_EXTERNAL_ID;
	public static final String ATTRIBUTE_EXPLORATION_REMOVAL_LOCK;
	public static final String ATTRIBUTE_PACKAGING_REMOVAL_LOCK;
	public static final String ATTRIBUTE_PRODUCT_REMOVAL_LOCK;
	public static final String ATTRIBUTE_ASSEMBLED_REMOVAL_LOCK;
	public static final String SYMBOLIC_NAME_MPAP;
	public static final String SYMBOLIC_NAME_MPMP;
	public static final String SYMBOLIC_NAME_MPP;
	public static final String SYMBOLIC_NAME_MRMP;
	public static final String SYMBOLIC_NAME_SHAPE_PART;
	public static final String ATTRIBUTE_PNG_HEIGHT;
	public static final String SELECT_ATTRIBUTE_PNG_HEIGHT;
	public static final String ATTRIBUTE_PNG_WIDTH;
	public static final String SELECT_ATTRIBUTE_PNG_WIDTH;
	public static final String ATTRIBUTE_PNG_LENGTH;
	public static final String SELECT_ATTRIBUTE_PNG_LENGTH;	
	public static final String ATTRIBUTE_OUTER_DIMENSION_HEIGHT_ORIGINAL;
	public static final String ATTRIBUTE_OUTER_DIMENSION_WIDTH_ORIGINAL;
	public static final String ATTRIBUTE_OUTER_DIMENSION_LENGTH_ORIGINAL;
	public static final String ATTRIBUTE_OUTER_DIMENSION_HEIGHT;
	public static final String ATTRIBUTE_OUTER_DIMENSION_WIDTH;
	public static final String ATTRIBUTE_OUTER_DIMENSION_LENGTH;
	public static final String ATTRIBUTE_PLM_EXCHANGE_STATUS_DS_VCUSTO;
	public static final String SELECT_ATTRIBUTE_PLM_EXCHANGE_STATUS_DS_VCUSTO;
	public static final String ATTRIBUTE_PG_ENTERPRISE_NUMBER;
	public static final String ATTRIBUTE_PG_SAPTYPE;
	public static final String ATTRIBUTE_ISONCE_INSTANTIABLE;
	public static final String SELECT_ATTRIBUTE_ISONCE_INSTANTIABLE;
	public static final String ATTRIBUTE_V_VERSION_COMMENT;
	public static final String SELECT_ATTRIBUTE_V_VERSION_COMMENT;
	public static final String ATTRIBUTE_PLMENTITY_V_NAME;
	public static final String SELECT_ATTRIBUTE_PLMENTITY_V_NAME;
	public static final String ATTRIBUTE_PLMENTITY_PLM_EXTERNALID;
	public static final String ATTRIBUTE_IS_LAST_VERSION;
	public static final String ATTRIBUTE_PG_PALLET_LENGTH;
	public static final String SELECT_ATTRIBUTE_PG_PALLET_LENGTH; 
	public static final String ATTRIBUTE_PALLET_WIDTH;
	public static final String SELECT_ATTRIBUTE_PALLET_WIDTH; 
	public static final String ATTRIBUTE_PALLET_HEIGHT;
	public static final String SELECT_ATTRIBUTE_PALLET_HEIGHT; 

	public static final String INTERFACE_SUCCESS_MESSAGE="Interface is added successfully";
	public static final String INTERFACE_REMOVE_SUCCESS_MESSAGE="Interface is removed successfully";
	public static final String INTERFACE_ALREADY_ADDED_MESSAGE="Interface is already added on the object";
	public static final String INTERFACE_NOT_PRESNT_ON_OBJECT="Interface cannot be removed, as it is not present on the object.";
	public static final String INTERFACE_DOES_NOT_EXIST="Interface does not exist in database";
	public static final String OBJECT_DOES_NOT_EXIST="The object whose physicalId is passed, does not exist in database";
	public static final String PROMOTE_SUCCESS_MESSAGE="Object is promoted to FROZEN state successfully";
	public static final String DEMOTE_SUCCESS_MESSAGE="Object is demoted to IN_WORK state successfully";
	public static final String ENTER_CORRECT_APPLICATION_NAME="Please pass correct application name";
	public static final String PREFERENCE_SET_SUCCESS_MESSAGE="Preference set successfully on user";
	public static final String PREFERENCE_REMOVED_SUCCESS_MESSAGE="Preference removed successfully";
	public static final String PREFERENCE_NOT_SET_MESSAGE="Preference is not set on the user";
	public static final String DEFAULT_USER_PREFERENCE_NOT_SET="Default User Preferences not set";
	public static final String RELEASE_PHASE_DEVELOPMENT="Development";
	public static final String RELEASE_PHASE_PILOT="Pilot";
	public static final String RELEASE_PHASE_PRODUCTION="Production";
	public static final String LIFECYCLE_STATUS_PRODUCTION="PRODUCTION";
	public static final String CONSTANT_PROMOTE="PROMOTE";
	public static final String CONSTANT_DEMOTE="DEMOTE";
	public static final String CONSTANT_TRUE="true";
	public static final String CONSTANT_FALSE="false";
	public static final String CONSTANT_TO = "To";
	public static final String CONSTANT_FROM = "From";
	public static final String CONSTANT_FIRST_REVISION = "001";
	public static final String CONSTANT_AUTONAME_SERIES_A = "A";
	public static final String RANGE_VALUE_SYSTEM_GENERATED = "System Generated";
	public static final String RANGE_VALUE_MANUAL = "Manual";	
	public static final String RANGE_VALUE_SYSTEM = "System";	
	public static final String RANGE_VALUE_AUTOMATION = "Automation";	
	public static final String STATE_RELEASE = "Release";
	public static final String STATE_PRELIMINARY = "Preliminary";
	public static final String STATE_FROZEN = "FROZEN";
	public static final String STATE_RELEASED	= "RELEASED";
	public static final String STATE_IN_WORK = "IN_WORK";
	public static final String STATE_PRIVATE = "PRIVATE";
	public static final String STATE_OBSOLETE="Obsolete";
	public static final String FILE_FORMAT_XML = "xml";
	public static final String FILE_FORMAT_PDF = "pdf";
	public static final String FILE_TRANSPORTVIEW_PDF = "TransportView";
	public static final String FILE_TECHNICALSTANDARD_PDF = "TechnicalStandard";
	public static final String STORE_PLMX = "plmx";
	public static final String STORE_STORE = "STORE";
	public static final String FORMAT_GENERIC = "generic";
	public static final String CONSTANT_PALLET_U1 = "U1";
	public static final String FORWARD_SLASH = "/";
	public static final String BACKWARD_SLASH = "\\";
	public static final String ANALYSIS_XML_ROOT = "TopsData";
	public static final String SIMPLE_XML_ROOT = "root";
	public static final String UOM_INCHES = "Inches";
	public static final String UOM_INCH = "Inch";
	public static final String UOM_IN = "in";
	public static final String UOM_MILLIMETER = "Millimeter";
	public static final String UOM_MM = "mm";
	public static final String UOM_MILLIMETERS = "Millimeters";
	public static final String UOM_CENTIMETER = "Centimeter";
	public static final String UOM_CM = "cm";
	public static final String UOM_METER = "Meter";
	public static final String UOM_M = "m";
	public static final String UOM_FEET = "Feet";
	public static final String UOM_FT = "ft";
	public static final String SELECT_PHYSICALID = "physicalid";
	public static final String RANGE_HIGHLYRESTRICTED = "Highly Restricted";
	public static final String RANGE_RESTRICTED = "Restricted";
	//START: Added for DT18X6-135
	public static final String STR_ASSEMBLED_PRODUCT_PART = "Assembled Product Part";
	public static final String STR_APOLLO_CONFIG_PAGE_FILENAME = "pgApolloConfigurations";
	public static final String STR_DESIGN_CONFIG_PAGE_FILENAME = "pgDesignToolConfigurations";
	public static final String STR_EXPORT_CONTROL_PAGE_FILENAME="emxExportControl_ECL.properties";
	//END: Added for DT18X6-135
	public static final String EXPRESSION_BUS= "expression_businessobject";
	public static final String CONSTANT_STRING_ADMIN_TYPE = "Admin Type";
	public static final String CONSTANT_PERSON="Person";
	public static final String CONSTANT_CIRCULAR_OPENING_BRACE="(";
	public static final String SEPARATOR_HASH = "#";
	public static final String SEPARATOR_AT_THE_RATE= "@";
	public static final String SEPARATOR_COLON = ":";
	public static final String SEPARATOR_SEMICOLON=";";
	public static final String SEPARATOR_COMMA = ",";
	public static final String SEPARATOR_PIPE = "|";
	public static final String SEPARATOR_TILDE = "~";
	public static final String SEPARATOR_HYPHEN = "-";
	public static final String CONSTANT_DOT = ".";
	public static final String SEPARATOR_UNDERSCORE = "_";
	public static final String SEPARATOR_STAR="*";
	public static final String SEPARATOR_DOLLAR="$";
	public static final String CONSTANT_REVISION_ZERO = "0";
	public static final String SIMPLE_XML_PREFIX = "IPS_import";
	public static final String REFERENCE_XML_PREFIX = "ReferenceForSPS";
	public static final String COMBINED_XML_PREFIX = "IPS_Combined";
	public static final String CONSTANT_FIND_NUMBER_ONE = "1";
	public static final String CONSTANT_PRJ = "PRJ";
			
	public static final String TUP = "TUP";
	public static final String MCUP = "MCUP";
	public static final String MCOP = "MCOP";
	public static final String MIP = "MIP";

	public static final String CONSTANT_ZERO="0";
    public static final String CONSTANT_ONE="1";
	public static final String CONSTANT_TWO="2";
	public static final String CONSTANT_THREE="3";
	public static final Double CONSTANT_ZERO_DOT_ZERO_ZERO=0.00;
	public static final String CONSTANT_TYPE_PATTERN="typePattern";
	public static final String CONSTANT_REL_PATTERN="relPattern";
	public static final String CONSTANT_OBJ_SELECTS="objSelects";
	public static final String CONSTANT_REL_SELECTS="relSelects";
	public static final String CONSTANT_OBJ_WHERE="objWhere";
	public static final String CONSTANT_REL_WHERE="relWhere";
	public static final String CONSTANT_EXPAND_LEVEL="expandLevel";
	public static final String KEY_FILE_NAME="fileName";
	public static final String KEY_OBJECT_ID="objectId";
	public static final String KEY_IS_DOCUMENT="type.kindof[DOCUMENTS]";
	//START:DT18X7-64
	public static final String CATIADESIGNER_ROLE="ctx::CATIADesigner.PG.Internal_PG";
	//END:DT18X7-64

	public static final String CONSTANT_TUP="Transport Unit";
	public static final String CONSTANT_MCOP="Master Consumer Unit";
	public static final String CONSTANT_MIP="Master Intermediate Unit";
	public static final String CONSTANT_MCUP="Master Customer Unit";
	public static final String CONSTANT_REFERENCE_XML="Reference XML";
	public static final String CONSTANT_IPS_IMPORT_XML="IPS Import XML";
	public static final String CONSTANT_IPS_COMBINED_XML="IPS Combined XML";
	public static final String CONSTANT_P_AND_G= "PandG";
	public static final String CONSTANT_TXML = "TXML_1.0";
	public static final String CONSTANT_NEW = "New";
	public static final String CONSTANT_PGEN1 = "PGEn1";
	public static final String CONSTANT_EMPTY="EMPTY";
	public static final String CONSTANT_PACKAGE_DESIGN = "Package Design";
	public static final String CONSTANT_JPO_PGDTAUTOMATION_METRIC_TRACKING = "pgDTAutomationMetricTracking";
	public static final String CONSTANT_METHOD_ADD_USAGE_TRACKING_TODATA = "addUsageTrackingToData";
	public static final String CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE = "emxEngineeringCentralStringResource";
	public static final String CONSTANT_EMX_CPN_STRING_RESOURCE = "emxCPNStringResource";	
	public static final String CONSTANT_LOCALHOST = "localhost";
	public static final String CONSTANT_ACCESS_MODIFY = "modify";
	public static final String CONSTANT_RSC = "RSC";
	public static final String CONSTANT_LETTER_V = "V";
	public static final String CONSTANT_LETTER_F = "F";
	public static final String CONSTANT_LETTER_O = "O";
	public static final String CONSTANT_NA = "NA";
	public static final String CONSTANT_RSC_FEFCO_0201 = "RSC (FEFCO 0201)";
	public static final String CONSTANT_CARTON_CAPS = "CARTON";
	public static final String CONSTANT_CARTON = "Carton";
	public static final String CONSTANT_UTF_8 = "UTF-8";
	public static final String CONSTANT_TAG_ANALYSIS_0 ="analysis0";
	public static final String CONSTANT_TAG_INFO ="info";
	public static final String CONSTANT_TAG_EXTNAME ="extName";
	public static final String CONSTANT_REFERENCE ="Reference";
	public static final String CONSTANT_TAG_EXTADDLINFO ="extAddlInfo";
	public static final String CONSTANT_COMBINED ="Combined";
	public static final String CONSTANT_TAG_CUBESPEC ="CubeSpec";
	public static final String CONSTANT_TAG_STR_USER_1 ="strUser1";
	public static final String CONSTANT_TAG_STR_USER_4 ="strUser4";
	public static final String CONSTANT_TAG_STR_USER_5 ="strUser5";
	public static final String CONSTANT_TAG_COMMENTS ="comments";
	public static final String CONSTANT_TAG_PRIPACK ="priPack";
	public static final String CONSTANT_TAG_IPACK ="ipack";
	public static final String CONSTANT_TAG_SHIPPER ="shipper";	
	public static final String CONSTANT_TAG_INPUT="input";
	public static final String CONSTANT_TAG_UNITLOAD="unitload";
	public static final String CONSTANT_TAG_PALTYPE="palType";
	public static final String CONSTANT_TAG_NAME="name";
	public static final String CONSTANT_TAG_OUTPUT="output";
	public static final String CONSTANT_TAG_PALLETID="palletId";
	public static final String CONSTANT_TAG_DB="db";
	public static final Vector VALID_SYNC_TYPES=new Vector();
	public static final Vector VALID_DT_SYNC_TYPES=new Vector();
	public static final Vector VALID_XCAD_ATTRIBUTES=new Vector();
	public static final Vector VALID_DT_SYMBOLIC_SYNC_TYPES=new Vector();
    public static final String SELECT_HAS_READ_ACCESS = "current.access[read]";  
    
    public static final String VALID_PARENT_FOR_MPAP = "pgPKGVPMPart,pgMasterPackagingAssemblyPart";
    public static final String VALID_CHILD_FOR_MPAP = "pgMasterPackagingAssemblyPart,pgMasterPackagingMaterialPart";
    
    public static final String VALID_PARENT_FOR_MPMP = "pgPKGVPMPart,pgMasterPackagingAssemblyPart";
    
    public static final String VALID_PARENT_FOR_MPP = "pgPKGVPMPart,pgMasterProductPart";
    public static final String VALID_CHILD_FOR_MPP = "pgMasterRawMaterialPart,pgMasterProductPart";
    
    public static final String VALID_PARENT_FOR_MRMP = "pgPKGVPMPart,pgMasterProductPart";
    
    public static final String VALID_CHILD_FOR_SHAPE_PART = "pgMasterPackagingAssemblyPart,pgMasterPackagingMaterialPart,pgMasterProductPart,pgMasterRawMaterialPart";
    
    public static final String VALID_ENT_TYPE_FOR_PACKAGING = "Master Packaging Assembly Part,Master Packaging Material Part";  
    public static final String VALID_ENT_TYPE_FOR_PRODUCT = "Master Product Part,Master Raw Material Part";  
    public static final String VALID_ENT_TYPE_FOR_EXPLORATION = "Shape Part";
    
    public static final String CONSTANT_DESIGN_FOR_PACKAGING = "Packaging";
    public static final String CONSTANT_DESIGN_FOR_PRODUCT = "Product";
    public static final String CONSTANT_DESIGN_FOR_EXPLORATION = "Exploration";
    public static final String CONSTANT_DESIGN_FOR_ASSEMBLED = "Assembled";
    public static final String CONSTANT_DESIGN_FOR_AUTOMATION = "Automation";
    public static final String CONSTANT_DESIGN_DOMAIN_APP_VPMREFERENCE="Assembled Product";
	public static final String CONST_LEGACY="legacy";
	public static final String CONST_IPATTR="ipattr";
	public static final String CONST_SIMDOC_SERVICE_NAME="SimDocCleanupService_";
	public static final String SIMDOC_SCAN_MODE_ERROR="Pass the correct value for process: legacy or ipattr";
    public static final String SIMDOC_GENERATE_MODE_WRONG_INPUT="The file has invalid contents. Please add Name, Rev and ObjectId of the Sim Docs";
	public static final String CONST_MOD_BUS="mod bus ";
	public static final String CONST_SCAN="Scan";
	public static final String CONST_GENERATE="Generate";
	public static final String CONST_FIX="Fix";
	public static final String MANDATORY_ATTRIBUTES_PRESENT="All mandatory attribute values are populated";
	public static final String NO_EC_PART_CONNECTED="No EC Part connected to VPMReference";
	
	public static final String CONST_PICKLIST_SEGMENT="pgPLISegment";
	public static final String CONST_PICKLIST_PACKMATERIALTYPE="pgPLIPackMaterialType";
	public static final String CONST_PICKLIST_CLASS="pgPLIClass";
	public static final String CONST_PICKLIST_REPORTEDFUNCTION="pgPLIReportedFunction";
	public static final String CONST_PICKLIST_ORGCHANGEMGMT="pgPLIOrganizationChangeManagement";
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_EXPERIMENTAL="DSOLifeCycleStatusExperimental";
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_PILOT="DSOLifeCycleStatusPilot";
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTION="DSOLifeCycleStatusProduction";
	public static final String CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTIONAPP="DSOLifeCycleStatusProductionAPP";
	public static final String CONST_FIELD_DISPLAY_CHOICES="field_display_choices";
	public static final String CONST_FIELD_CHOICES="field_choices";
	
	public static final String CONST_PICKLIST_JPO="pgPLPicklist";
	public static final String CONST_DSOUTIL_JPO="pgDSOUtil";
	public static final String CONST_PICKLIST_RANGE_METHOD="getPicklistRangeMap";
	public static final String CONST_PICKLIST_DIRECT_ATTR_METHOD="getPicklistRangeMapForDirectAttr";
	public static final String CONST_PICKLIST_SUBSET_PLTYPE_METHOD="getSubsetPLTypeSpecificRanges";
	public static final String CONST_PICKLIST_SUBSET_METHOD="getPicklistSubsetRange";
	public static final String CONST_BLANK_YESNO_METHOD="getBlankYesNoRanges";
	public static final String CONST_CPN_STRING_RESOURCE="emxCPNStringResource";
	public static final String CONST_COMPONENTS_STRING_RESOURCE="emxComponentsStringResource";
	public static final String  CONST_DOUBLE_EQUAL = "==";
   
    public static final String RANGE_PNG_UNDER_EBOM_CM_CONTROL_REQUESTED="Requested";
    public static final String RANGE_PNG_UNDER_EBOM_CM_CONTROL_ADDED="Added";
    public static final String STR_SUCCESS = "Success";
    public static final String STR_ERROR = "Error";
   
   
   public static final String  CONSTANT_STRING_SPACE = " ";
   public static final String CONSTANT_STRING_DOUBLE_AMPERSAND = "&&";
   public static final String  CONSTANT_NEW_LINE = "<br>";
   public static final String  CONSTANT_NEW_LINE_SLASH_N = "\n";
   public static final String  CONSTANT_FREEZE_DESIGN = "FreezeDesign";
   public static final String  CONSTANT_TRANSFER_CONTROL = "TransferControl";
   public static final String  CONSTANT_CHANGE_MANAGEMENT = "ChangeManagement";
   public static final String  CONSTANT_BACKGROUND_JOB = "BackgroundJob";
   public static final String  CONSTANT_REUSE_COCA = "Reuse CA/CO";
   public static final String  STR_EMAIL="Email";
   public static final String  CONSTANT_PART_INFORMATION = "Part Information";
   public static final String  CONSTANT_COLLABORATION_STATUS = "Collaboration Status";
   public static final String  CONSTANT_NO_MODIFY_ACCESS_ON_EC_PART_CHECK_VPMCONTROL = "No Modify Access on EC Part. Please verify if Control is with Enovia";
   public static final String  CONSTANT_REVISE_NOT_POSSIBLE_WHEN_CHANGE_CONTROL ="Revision from CAD not possible when design under Change Control";

   public static final String  INCORRECT_VPMREFERENCE_PROVIDED_BY_CATIA = "VPMReference Details provided by Catia does not exist in database";
   public static final String  NO_EC_PART_LINKED_TO_VPMREFERNCE = "No EC Part is Linked to VPMReference";
   public static final String JOB_SUBMITTED_SUCCESS = "Job Submitted Successfully";
   public static final String JOB_SUBMITTED_SUCCESS_FOR_REUSE_COCA = "Job Submitted Successfully for Reuse CO/CA Process";
   public static final String CHANGE_MANAGEMENT_SUCCESS = "Change Management Completed Successfully";
   public static final String FREEZE_DESIGN_SUCCESS = "Freeze To Design Completed Successfully";
   public static final String TRANSFER_CONTROL_SUCCESS = "Transfer Control Completed Successfully";
   public static final String REVISION_SUCCESS = "Revision Completed Successfully";
   public static final String UPSTAGE_SUCCESS = "Upstage Completed Successfully";
   public static final String UNSUPPORTED_SERVICE="The service is not supported by webservice";
   public static final String CONST_REL_PDTEMPLATES_TO_PGPLISEGMENT="relationship_pgPDTemplatestopgPLISegment";
   public static final String CONST_REL_PDTEMPLATES_TO_PGPLIREPORTEDFUNCTION="relationship_pgPDTemplatestopgPLIReportedFunction";
   public static final String STR_ERROR_BACKGROUND_JOB_ISRUNNING = "Operation failed. Following Background Jobs for previous Collaborate operation are still running: ";
   public static final String STR_ERROR_EC_PART_REV_NOT_LATEST =  "EC Part Object is not of latest Revision and is of stage Production or Pilot hence it cannot be revised";
   public static final String NO_ACTION_PERFORMED="No action was performed on the objects";
   public static final String STR_OPTIONS_SEPARATOR="--------------- Options ----------------";
   public static final String STR_PART_SEPARATOR="---------------- Part ------------------";
   public static final String STR_LINE_SEPARATOR="-------------------------------------------";
   public static final String SHAPE_PART_UPSTAGE_NOT_ALLOWED="Shape Part cannot be upstaged";
   public static final String LOWER_TARGET_PHASE_NOT_ALLOWED="Object cannot be upstaged with selection of lower target phase";
   public static final String CANNOT_REVISE_EC_PART_IN_OBSOLETE="Revise not possible for Obsolete state Data";
   public static final String OBJECT_IN_FROZEN_OR_BEYOND="Object is either in Frozen or beyond Frozen state hence No action taken for Freeze to Design"; 
   public static final String IMPORT_WS_OBJECT_NOT_EXIST = "Base VPMReference Object or New VPMReference Object does not exist in Enovia";
   public static final String OBJECT_TO_BE_UPDATED_DOES_NOT_EXIST="The object to be updated does not exist in database";
   public static final String SELECT_SEMANTIC_PATH_ID = "paths[SemanticRelation].path.id";
	public static final String SELECT_ELEMENT_PHYSICAL_ID = "element.physicalid";
	public static final String SELECT_ELEMENT_KIND = "element.kind";
	public static final String SELECT_ELEMENT_RELEVANT = "element.relevant";
	public static final String SELECT_LOGICAL_ID = "logicalid";
	public static final String SELECT_MAJOR_ID = "majorid"; 
	public static final String STR_SELECT_TO_PHYSICALID = "to.physicalid";
	public static final String STR_ZEROS="00000000000000000000000000000000";
	public static final String STR_REP_INSTANCES_RELID="RepInstancesRelID";
	public static final String STR_REP_INSTANCES_MAP="RepInstancesMap";
	public static final String STR_REP_INSTANCES_OID="RepInstancesOID";
	public static final String STR_PHYSICALID_NOT_PASSED="Physical ID is not passed as parameter for the WS";
   
	public static final String  CONST_STANDALONE = "Standalone";
	public static final String  CONST_LEAF = "Leaf";
	public static final String  CONST_INTERMEDIATE = "Intermediate";
	public static final String  CONST_TOP = "Top";
	public static final String  FRAMEWORK_STRING_RESOURCE = "emxFrameworkStringResource";
	public static final String  INTERFACE_MIG_STATUS_EXTENSION_DT_DATACLEANUP = "MigStatusExtension_DTDataCleanup";
	public static final String  CONST_SERVICE_NAME = "ClonedDerivedCleanupService_";
	public static final String SCAN_MODE_ERROR="Pass the correct value for level: Standalone,Leaf,Intermediate or Top";
	public static final String GENERATE_MODE_ERROR="Pass the Input file full path with file name";
	public static final String FIX_MODE_ERROR="Pass the full path of the folder where mql files are present";
	public static final String WRONG_MODE_ERROR="Pass the correct value for mode: Scan, Generate or Fix";
    public static final String MX_MIGRATION_STATUS_ATTR = "VPLMsys/MigrationStatus";
	public static final String MX_MIGRATION_STATUS_SELECTABLE = "attribute[VPLMsys/MigrationStatus]";
	public static final String CONST_NOT_STARTED = "NOT_STARTED";
	public static final String CONST_ON_GOING = "ON_GOING";
	public static final String CONST_FINISHED = "FINISHED";
	public static final String CONST_NO_STATUS = "NO_STATUS";
	public static final String TYPE_VPLM_DATA_MIGRATION="VPLMDataMigration";
	public static final String POLICY_VPLM_DATA_MIGRATION="VPLMDataMigration_Policy";
	public static final String ATTRIBUTE_V_MIG_STEP="MigStatusExtension.V_MigStep";
	public static final String ATTRIBUTE_V_MIG_TYPE="MigStatusExtension.V_MigType";  
	public static final String CONST_PNG_COMPONENT="pngComponent";
	public static final String CONST_PLMEXCHANGESTATUSDS_SERVICE_NAME= "PLMExchangeStatusDSCleanupService_";
	public static final String CONST_THREE_HYPHEN_REVISION="---";
	public static final String CONST_RELEVANT_OBJECT="relevantObject";
	public static final String CONST_OBJECT_NOT_FOUND="Object not Found";
	public static final String CONST_PLM_DOC_CONNECTION="PLMDocConnection";
	public static final String KEY_PARENT_REL_NAME="parentRelName";
	public static final String DESIGN_DOMAIN_SCAN_MODE_ERROR="Pass the correct value for ECPart type";
	public static final String CONST_DESIGNDOMAIN_SERVICE_NAME="DesignDomainCleanupService_";
	public static final String DESIGNDOMAIN_GENERATE_MODE_WRONG_INPUT="The file has invalid contents. Please add Name and Rev of the objects";
	public static final String ENT_TYPE_SHAPE_PART="Shape Part";
	public static final String ENT_TYPE_MPAP="Master Packaging Assembly Part";
	public static final String ENT_TYPE_MPMP="Master Packaging Material Part";
	public static final String ENT_TYPE_MRMP="Master Raw Material Part";
	public static final String ENT_TYPE_MPP="Master Product Part";
   
	public static final String SELECT_IPM_DOCUMENT_FILES_FROM_SPS ;
	public static final String ALERT_NO_TRANSPORTVIEW_PDF = "TransportView PDF is not connected to the IPM Document hence you cannot promote the SPS" ;

	public static final String CONSTANT_ALL="All";
	public static final String CONST_INCORRECT_MATURITY_STATE_SERVICE_NAME="IncorrectMaturityStateCleanupService_";
	public static final String INCORRECT_MATURITY_STATE_GENERATE_MODE_WRONG_INPUT="The file has invalid contents. Please add Type,Name and Rev of the objects";
	public static final String CONST_PROMOTE_BUS="promote bus ";
	public static final String CONST_APPROVE_BUS="approve bus ";
	public static final String SIGN_SHAREWITHINPROJECT="ShareWithinProject";
	public static final String SIGN_TOFREEZE="ToFreeze";
	public static final String SIGN_TORELEASE="ToRelease";
	public static final String CLEANUP_SCRIPT_APPROVAL_COMMENT="approved through cleanup script";
	public static final String CONST_EC_PART_IN_VPLM_VAULT_SERVICE_NAME="ECPartInVPLMVaultCleanupService_";
	public static final String CONST_NO_DESIGN_DOMAIN_INTERFACE_SERVICE_NAME="NoDesignDomainInterfaceCleanupService_";
	public static final String CONST_EC_PART_WITHOUT_TEMPLATE_SERVICE_NAME="ECPartDataWithoutTemplateCleanupService_";
	public static final String CONST_PRIVATE_ASSEMBLY_DATA_SERVICE_NAME="PrivateAssemblyDataCleanupService_";
	public static final String CONST_SHAPE_PART_INCORRECT_POLICY_DATA_SERVICE_NAME="ShapePartIncorrectPolicyDataCleanupService_";
	public static final String CONST_TO_DELETE_DATA_SERVICE_NAME="ToDeleteDataCleanupService_";
	public static final String PREF_BU="preference_BusinessUseClass";
	public static final String PREF_HIR="preference_HighlyRestrictedClass";
	public static final String PREF_DEFAULT_IP_CLASSIFICATION="preference_DefaultIPClassForExploration";
	public static final String CONSTANT_BUSINESS_USE="Business Use";
	public static final String CONSTANT_HIGHLY_RESTRICTED="Highly Restricted";
	public static final String CONSTANT_DIMENSIONS="Dimensions ";
	public static final String ALERT_TUP_LOCKED=",Please try again later";
	public static final String CONSTANT_ATTR_OUTER_DIMENSION_WIDTH = "Outer Dimension Width";
	public static final String CONSTANT_ATTR_OUTER_DIMENSION_HEIGHT = "Outer Dimension Height";
	public static final String CONSTANT_ATTR_OUTER_DIMENSION_LENGTH = "Outer Dimension Depth";
	public static final String CONSTANT_INPUT="Input";
	public static final String CONSTANT_HEADER="Header";
	public static final String CONSTANT_ECPART_TYPE_KEY="ECPartType";
	public static final String CONSTANT_ECPART_NAME_KEY="ECPartName";
	public static final String CONSTANT_ECPART_REVISION_KEY="ECPartRevision";
	public static final String CONSTANT_VPMREF_TYPE_KEY="VPMReferenceType";
	public static final String CONSTANT_VPMREF_NAME_KEY="VPMReferenceName";
	public static final String CONSTANT_VPMREF_REVISION_KEY="VPMReferenceRevision";
	public static final String CONSTANT_OWNER_KEY="Owner";
	public static final String CONSTANT_ECPART_OBJECT="ECPartObject";
	public static final String CONSTANT_VPMREFERENCE_OBJECT="VPMReferenceObject";
	public static final String CONSTANT_CATIA_APPLICATION="CATIA";
	public static final String CONSTANT_COCA_PROCESS="PWPAUTO-CreateChangeManagement";
	public static final String CONSTANT_COLLABORATE_EBOM_PROCESS="PWPAUTO-CollaborateEBOM";
	public static final String CONSTANT_REVISE_PROCESS="PWPAUTO-Revise";
	public static final String CONSTANT_UPSTAGE_PROCESS="PWPAUTO-Upstage";
	public static final String CONSTANT_CAD2SPEC_CONVERSION_PROCESS="CAD2Spec-DataConversion";
	public static final String PREFERENCE_DESIGN_FOR="preference_DIDesignFor";
	public static final String PREFERENCE_PACKAGING_PHASE="preference_PackagingPartPhase";
	public static final String PREFERENCE_PRODUCT_PHASE="preference_ProductPartPhase";
	public static final String PREFERENCE_RAW_MATERIAL_PHASE="preference_RawMaterialPartPhase";
	public static final String FORMAT_1_FILE_NAME = "format[1].file.name";
	public static final String FORMAT_2_FILE_NAME = "format[2].file.name";
	public static final String PREFERENCES_NOT_SET="Default User Preferences not set for user.Please set DI Preferences in Enovia";
	public static final String CONSTANT_HIR="HiR";
	public static final String STR_ERROR_TNR_NOT_PASSED="Type Name Revision details of the object are not passed as parameter to the web service";
	public static final String STR_ERROR_OPERATION_NOT_PASSED="Operation is not passed as parameter to the web service";
	public static final String STATE_ACTIVE="Active";
	

	static {
		INTERFACE_IPSEC_CLASS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_DTIPSecClassExtension");
		INTERFACE_DT_TRANSIENT_JOB_EXTENSION = PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_DTTransientJobExtension");
				
		INTERFACE_PG_TOPS_DRUM=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSDrum");
		INTERFACE_PG_TOPS_BUCKET_ROUND=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSBucketRound");
		INTERFACE_PG_TOPS_BUCKET_RECTANGULAR=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSBucketRectangular");
		INTERFACE_PG_TOPS_BOTTLE_ROUND=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSBottleRound");
		INTERFACE_PG_TOPS_BOTTLE_RECTANGULAR=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSBottleRectangular");
		INTERFACE_PG_TOPS_BOTTLE_OVAL=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSBottleOval");
		INTERFACE_PG_TOPS_FRA_DIMENSIONS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSFRADimensions");
		INTERFACE_PG_TOPS_CAN=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSCan");
		INTERFACE_PG_TOPS_TUB_ROUND =PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSTubRound"); 
		INTERFACE_PG_TOPS_TUB_RECTANGULAR =PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSTubRectangular"); 
		INTERFACE_PG_TOPS_BLISTER_PACK =PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgTOPSBlisterPack"); 
		INTERFACE_PNG_PACKAGING=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pngiPackaging"); 
		INTERFACE_PNG_PRODUCT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pngiProduct"); 
		INTERFACE_PNG_EXPLORATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pngiExploration"); 
		INTERFACE_PNG_ASSEMBLED=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pngiAssembled"); 
		INTERFACE_PART_FAMILY_REFERENCE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_PartFamilyReference"); 
		INTERFACE_PNG_DESIGNPART=PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pngiDesignPart"); 
		INTERFACE_XCADITEMEXTENSION = PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_XCADItemExtension");
	    INTERFACE_AUTOMATION_USAGE_EXTENSION = PropertyUtil.getSchemaProperty(PRSPContext.get(),"interface_pgAutomationUsageExtension");
	
		TYPE_PG_TRANSPORTUNIT = PropertyUtil.getSchemaProperty(PRSPContext.get(), SCHEMA_TYPE_PG_TRANSPORTUNIT);
		TYPE_PG_STACKINGPATTERN = PropertyUtil.getSchemaProperty(PRSPContext.get(), SCHEMA_TYPE_PG_STACKINGPATTERN);
		TYPE_PG_MASTERCUSTOMERUNIT = PropertyUtil.getSchemaProperty(PRSPContext.get(),SCHEMA_TYPE_PG_MASTERCUSTOMERUNIT);
		TYPE_PG_MASTERCONSUMERUNIT = PropertyUtil.getSchemaProperty(PRSPContext.get(),SCHEMA_TYPE_PG_MASTERCONSUMERUNIT);
		TYPE_PG_MASTERINNERPACKUNIT = PropertyUtil.getSchemaProperty(PRSPContext.get(),SCHEMA_TYPE_PG_MASTERINNERPACKUNIT);
		
		TYPE_PGIPMDOCUMENT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgIPMDocument");

		TYPE_VPMREFERENCE= PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_VPMReference");
		TYPE_SHAPE_PART= PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgPKGVPMPart");
		TYPE_DRAWING = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_Drawing");
		TYPE_FEM= PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_FEM");
		TYPE_SIM_SHAPE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_SIMShape");
		TYPE_DSC_MATREF_REF_CORE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_dsc_matref_ref_Core");
		TYPE_MACRO_LIBRARY_VBA=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_MacroLibraryVBA");
		TYPE_XCAD_MODEL_REP_REFERENCE =PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_XCADModelRepReference");
		TYPE_SIMULATION_DOC_VERSIONED =PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_SimulationDocument_Versioned");
		TYPE_SIMULATION_DOC_NONVERSIONED =PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_SimulationDocument_NonVersioned");
		TYPE_REQUIREMENT =PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_Requirement");
		TYPE_REQUIREMENT_SPECIFICATION =PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_SoftwareRequirementSpecification");
		TYPE_DESIGN_SIGHT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_DesignSight");
		TYPE_ARTIOSCAD_COMPONENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_ArtiosCADComponent");
		TYPE_IRM_DOCUMENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgIRMDocument");
		TYPE_SIMULATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_Simulation");
		TYPE_PACKING_INSTRUCTION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgPackingInstructions");
		TYPE_DOCUMENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_Document");
		TYPE_DOCUMENTS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_DOCUMENTS");
		TYPE_ORG_CHANGE_MGMT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgPLIOrganizationChangeManagement");
		TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgMasterPackagingAssemblyPart");
		TYPE_PG_MASTER_PACKAGING_MATERIAL_PART = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgMasterPackagingMaterialPart");
		TYPE_PG_MASTER_PRODUCT_PART = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgMasterProductPart");
		TYPE_PG_MASTER_RAW_MATERIAL_PART = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgMasterRawMaterialPart");
		TYPE_ASSEMBLED_PRODUCT_PART = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgAssembledProductPart");
		TYPE_3DSHAPE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_3DShape");
		TYPE_IP_CONTROL_CLASS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_IPControlClass");
		TYPE_JOB = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_Job");
		TYPE_DSC_MAT_CNX_COVERING_DESIGN = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_dsc_mat_cnx_Covering_Design");
		TYPE_DSC_MAT_CNX_CORE_DESIGN = PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_dsc_mat_cnx_Core_Design");
		TYPE_PLM_EXCHANGE_STATUS_DS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_PLMExchangeStatusDS");
		TYPE_TEMPLATE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_Template");
		TYPE_PROXY_OBJECT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgExportControlAccessProxy");
		TYPE_PRODUCT_DATA_PART=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_ProductDataPart");
		TYPE_PLMDMTDOCUMENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_PLMDMTDocument");
		TYPE_PG_PLI_PALLET_TYPE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"type_pgPLIPalletType");
		
		POLICY_IPM_SPECIFICATION = PropertyUtil.getSchemaProperty(PRSPContext.get(), SCHEMA_POLICY_IPM_SPECIFICATION);
		POLICY_IPM_DOCUMENT = PropertyUtil.getSchemaProperty(PRSPContext.get(), "policy_pgIPMDocument");
		POLICY_EC_PART = PropertyUtil.getSchemaProperty(PRSPContext.get(), SCHEMA_POLICY_EC_PART);
		POLICY_PRODUCT_DATA_SPECIFICATION= PropertyUtil.getSchemaProperty(PRSPContext.get(), SCHEMA_POLICY_IPM_SPECIFICATION);
		POLICY_SIMULATION_DOCUMENT_LEGACY= PropertyUtil.getSchemaProperty(PRSPContext.get(), "policy_SimulationDocumentLegacy");
		POLICY_SIMULATION_DOCUMENT_OWNED= PropertyUtil.getSchemaProperty(PRSPContext.get(), "policy_SimulationDocumentOwned");
		POLICY_SIMULATION_DOCUMENT= PropertyUtil.getSchemaProperty(PRSPContext.get(), "policy_SimulationDocument");
		POLICY_PG_PKGWIPPART=PropertyUtil.getSchemaProperty(PRSPContext.get(), "policy_pgPKGWIPPart");

		REL_PART_SPECIFICATION = PropertyUtil.getSchemaProperty(PRSPContext.get(), "relationship_PartSpecification");
		REL_REFERENCE_DOCUMENT = PropertyUtil.getSchemaProperty(PRSPContext.get(), "relationship_ReferenceDocument");

		REL_VPM_REPINSTANCE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_VPMRepInstance");
		REL_XCADITEM= PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_XCADItem");
		REL_EBOM = PropertyUtil.getSchemaProperty(PRSPContext.get(), "relationship_EBOM");
		REL_SIMULATION_CONTENT_REFERENCED= PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_SimulationContent_Referenced");
		REL_SIMULATION_CONTENT_OWNED= PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_SimulationContent_Owned");
		REL_PRIMARY_ORGANIZATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_pgPrimaryOrganization");
		REL_SECONDARY_ORGANIZATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_pgSecondaryOrganization");

		REL_VPM_INSTANCE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_VPMInstance");
		REL_PART_FAMILY_REFERENCE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_PartFamilyReference");
		REL_INHERITED_CAD_SPECIFICATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_pgInheritedCADSpecification");
		REL_CLASSIFIED_ITEM = PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_ClassifiedItem");
		
		RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_pgPDTemplatestopgPLISegment");
		REL_PG_PRIMARY_ORGANIZATION = PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_pgPrimaryOrganization");
		REL_PG_SECONDARY_ORGANIZATION = PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_pgSecondaryOrganization");
		RELATIONSHIP_PGPDTEMPLATES_TO_PGPLIREPORTEDFUNCTION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_pgPDTemplatestopgPLIReportedFunction");
		RELATIONSHIP_PENDING_JOB=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_PendingJob");
		RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_VPLMrel%PLMConnection%V_Owner");
		RELATIONSHIP_TEMPLATE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_Template");
		RELATIONSHIP_DERIVED=PropertyUtil.getSchemaProperty(PRSPContext.get(),"relationship_Derived");
		
		ATTR_PG_ORIGINATINGSOURCE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOriginatingSource");
		ATTR_EXPIRATION_DATE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_ExpirationDate");
		ATTRIBUTE_IS_TEMPLATE_APPLIED = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_IsTemplateApplied");
		ATTRIBUTE_REASONFORCHANGE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_ReasonforChange");
    
		ATTR_RELEASE_PHASE = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_ReleasePhase");
		ATTR_PG_IP_CLASSIFICATION = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_pgIPClassification");
		ATTR_V_NAME = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_V_Name");
		ATTRIBUTE_ISVPMVISIBLE = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_isVPMVisible");
		SELECT_ATTRIBUTE_ISVPMVISIBLE="attribute["+ATTRIBUTE_ISVPMVISIBLE+"]";
		ATTR_PG_SPS_ORIGINATION = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_pgSPSOrigination");
		SELECT_ATTR_PG_SPS_ORIGINATION = "attribute["+ATTR_PG_SPS_ORIGINATION+"]";

		ATTR_OUTER_DIMENSION_LENGTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionLength");
		ATTR_OUTER_DIMENSION_WIDTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionWidth");
		ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionHeight");
		ATTRIBUTE_PG_DIAMETER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgDiameter");
		ATTRIBUTE_PG_TOPDIAMETER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgTopDiameter");
		ATTRIBUTE_PG_BOTTOMDIAMETER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgBottomDiameter");
		ATTRIBUTE_PG_PITCH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgPitch");
		ATTRIBUTE_PG_TOPDEPTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgTopDepth");
		ATTRIBUTE_PG_TOPWIDTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgTopWidth");
		ATTRIBUTE_PG_BOTTOMWIDTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgBottomWidth");
		ATTRIBUTE_PG_BODYDEPTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgBodyDepth");
		ATTRIBUTE_PG_BODYWIDTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgBodyWidth");
		ATTRIBUTE_PG_NECKDIAMETER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgNeckDiameter");
		ATTRIBUTE_PG_SHOULDERHEIGHT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgShoulderHeight");
		ATTRIBUTE_PG_BODYDIAMETER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgBodyDiameter");
		ATTRIBUTE_PG_NECKHEIGHT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgNeckHeight");
		ATTRIBUTE_PG_DIMENSTION_UOM = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgDimensionUoM");
		ATTRIBUTE_PG_TOP_INDENT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgTopIndent");
		ATTRIBUTE_PG_BOTTOM_INDENT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgBottomIndent");
		ATTRIBUTE_PG_SIDE_INDENT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgSideIndent");

		ATTR_AREA_EFFICIENCY = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgAreaEffeciency");
		ATTR_PG_STACKING_PATTERN_TYPE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgStackingPatternType");
		ATTR_PG_CUBE_EFFECIENCY = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgCubeEffeciency");
		ATTRIBUTE_LAYERS_PERTRANSPORTUNIT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgLayersPerTransportUnitInteger");
		ATTRIBUTE_CUSTOMER_UNITS_PRELAYER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgCustomerUnitsPerLayerInteger");
		ATTR_UNDER_HANG_ACTUAL_LENGTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgUnderhangActualLength");
		ATTR_UNDER_HANG_ACTUAL_WIDTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgUnderhangActualWidth");
		ATTR_OVER_HANG_ACTUAL_LENGTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOverhangActualLength");
		ATTR_OVER_HANG_ACTUAL_WIDTH = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOverhangActualWidth");
		ATTR_TITLE = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_Title");
		ATTRIBUTE_PG_JOB_CONFIG = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgJobConfig");
		ATTRIBUTE_ISVPLMCONTROLLED = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMReference.V_isVPLMControlled");
		SELECT_ATTRIBUTE_ISVPLMCONTROLLED = "attribute["+ATTRIBUTE_ISVPLMCONTROLLED+"]";
		
		ATTR_PG_PALLETTYPE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgPalletType");
		SELECT_ATTR_PG_PALLETTYPE = "attribute["+ATTR_PG_PALLETTYPE+"]";
		ATTRIBUTE_PGENTERPRISETYPE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngEnterpriseType");
		SELECT_ATTRIBUTE_PGENTERPRISETYPE = "attribute["+ATTRIBUTE_PGENTERPRISETYPE+"]";
		PERSON_USER_AGENT = PropertyUtil.getSchemaProperty(PRSPContext.get(),"person_UserAgent");
			
		ATTR_PG_SECONDARY_PACK_INFO = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgSecondaryPackConfig");
		SELECT_ATTR_PG_SECONDARY_PACK_INFO ="attribute["+ATTR_PG_SECONDARY_PACK_INFO+"]";
		ATTRIBUTE_LIFECYCLE_STATUS= PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgLifeCycleStatus");	
		
		ATTRIBUTE_FIND_NUMBER = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_FindNumber");
		ATTRIBUTE_GEOMETRY_SHAPE = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_pgGeometryShape");
		SELECT_ATTRIBUTE_GEOMETRY_SHAPE = "attribute["+ATTRIBUTE_GEOMETRY_SHAPE+"]";
		ATTRIBUTE_QUANTITY = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_Quantity");
		SELECT_ATTRIBUTE_QUANTITY = "attribute["+ATTRIBUTE_QUANTITY+"]";
		ATTR_PG_BODY_SHAPE = PropertyUtil.getSchemaProperty(PRSPContext.get(), "attribute_pgBodyShape");
		

		ATTRIBUTE_V_DESCRIPTION= PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMEntity.V_description");
		VALID_SYNC_TYPES.addElement(TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART);
		VALID_SYNC_TYPES.addElement(TYPE_PG_MASTER_PACKAGING_MATERIAL_PART);
		VALID_SYNC_TYPES.addElement(TYPE_PG_MASTER_PRODUCT_PART);
		VALID_SYNC_TYPES.addElement(TYPE_PG_MASTER_RAW_MATERIAL_PART);
		VALID_SYNC_TYPES.addElement(TYPE_SHAPE_PART);
		VALID_SYNC_TYPES.addElement(TYPE_ASSEMBLED_PRODUCT_PART);
		VALID_DT_SYNC_TYPES.addElement(TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART);
		VALID_DT_SYNC_TYPES.addElement(TYPE_PG_MASTER_PACKAGING_MATERIAL_PART);
		VALID_DT_SYNC_TYPES.addElement(TYPE_PG_MASTER_PRODUCT_PART);
		VALID_DT_SYNC_TYPES.addElement(TYPE_PG_MASTER_RAW_MATERIAL_PART);
		VALID_DT_SYNC_TYPES.addElement(TYPE_SHAPE_PART);

		ATTRIBUTE_XCAD_ENTERPRISE_TYPE = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiXCADPart.pngEnterpriseType");
		ATTRIBUTE_XCAD_MFG_MATURITY_STATUS = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiXCADPart.pngManufacturingMaturityStatus");	
		ATTRIBUTE_XCAD_DESIGN_DOMAIN = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiXCADPart.pngDesignDomain");	
		ATTRIBUTE_XCAD_DEFAULTEXPLORATION = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiXCADPart.pngDefaultExplorationSecurityClass");
			
		VALID_XCAD_ATTRIBUTES.addElement(ATTRIBUTE_XCAD_ENTERPRISE_TYPE);
		VALID_XCAD_ATTRIBUTES.addElement(ATTRIBUTE_XCAD_MFG_MATURITY_STATUS);
		VALID_XCAD_ATTRIBUTES.addElement(ATTRIBUTE_XCAD_DESIGN_DOMAIN);
		VALID_XCAD_ATTRIBUTES.addElement(ATTRIBUTE_XCAD_DEFAULTEXPLORATION);
		VALID_XCAD_ATTRIBUTES.addElement(ATTRIBUTE_V_DESCRIPTION);
		SELECT_CONNECTED_MASTER_PART_TYPE="to["+ REL_CLASSIFIED_ITEM+"].frommid[" + REL_PART_FAMILY_REFERENCE+"].torel.to.type";
		SELECT_CONNECTED_MASTER_PART_ID="to["+ REL_CLASSIFIED_ITEM+"].frommid[" + REL_PART_FAMILY_REFERENCE+"].torel.to.id";
		SELECT_MASTER_TYPE_INHERITED_CADOBJ_TYPE="to["+ REL_CLASSIFIED_ITEM+"].frommid[" + REL_PART_FAMILY_REFERENCE+"].torel.to.from["+REL_INHERITED_CAD_SPECIFICATION+"].to.type";
		SELECT_MASTER_TYPE_INHERITED_CADOBJ_ID="to["+ REL_CLASSIFIED_ITEM+"].frommid[" + REL_PART_FAMILY_REFERENCE+"].torel.to.from["+ REL_INHERITED_CAD_SPECIFICATION+"].to.id";
		SELECT_MASTER_TYPE_INHERITED_CADOBJ_NAME="to["+ REL_CLASSIFIED_ITEM+"].frommid[" + REL_PART_FAMILY_REFERENCE+"].torel.to.from["+ REL_INHERITED_CAD_SPECIFICATION+"].to.name";
		SELECT_MASTER_TYPE_INHERITED_CADOBJ_PHYSICALID="to["+ REL_CLASSIFIED_ITEM+"].frommid[" + REL_PART_FAMILY_REFERENCE+"].torel.to.from["+ REL_INHERITED_CAD_SPECIFICATION+"].to.physicalid";
		SELECT_MASTER_TYPE_INHERITED_CADOBJ_RELID="to["+ REL_CLASSIFIED_ITEM+"].frommid[" + REL_PART_FAMILY_REFERENCE+"].torel.to.from["+ REL_INHERITED_CAD_SPECIFICATION+"].id";

		ATTRIBUTE_PNG_CLONE_DERIVED_FROM= PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngCloneDerivedFrom");	
		ATTRIBUTE_V_DERIVED_FROM= PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMReference.V_DerivedFrom");	
		ATTRIBUTE_CAD_DESIGN_ORIGINATION= PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngCADDesignOrigination");
		ATTRIBUTE_MFG_MATURITY_STATUS = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngManufacturingMaturityStatus");
		ATTRIBUTE_DESIGN_DOMAIN = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngDesignDomain");
		ATTRIBUTE_V_USAGE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMEntity.V_usage");
		SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM = "attribute["+ATTRIBUTE_PNG_CLONE_DERIVED_FROM+"]";
		SELECT_ATTRIBUTE_V_DERIVED_FROM = "attribute["+ATTRIBUTE_V_DERIVED_FROM+"]";
		SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION = "attribute["+ATTRIBUTE_CAD_DESIGN_ORIGINATION+"]";
		SELECT_ATTRIBUTE_V_DESCRIPTION = "attribute["+ATTRIBUTE_V_DESCRIPTION+"]";
		SELECT_ATTRIBUTE_MFG_MATURITY_STATUS = "attribute["+ATTRIBUTE_MFG_MATURITY_STATUS+"]";
		SELECT_ATTRIBUTE_DESIGN_DOMAIN = "attribute["+ATTRIBUTE_DESIGN_DOMAIN+"]";
		SELECT_ATTRIBUTE_V_USAGE = "attribute["+ATTRIBUTE_V_USAGE+"]";
		VAULT_VPLM=PropertyUtil.getSchemaProperty(PRSPContext.get(),"vault_vplm");
		ATTRIBUTE_PG_MIG_DATE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_attribute_pgMigDate");
		ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_ProcessAccessClassification");

		//START:DTWPI-15
		ATTRIBUTE_PACKAGING_SEGMENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngSegment");
		SELECT_ATTRIBUTE_PACKAGING_SEGMENT= "attribute["+ATTRIBUTE_PACKAGING_SEGMENT+"]";
		ATTRIBUTE_PACKAGING_REPORTED_FUNCTION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngReportedFunction");
		SELECT_ATTRIBUTE_PACKAGING_REPORTED_FUNCTION= "attribute["+ATTRIBUTE_PACKAGING_REPORTED_FUNCTION+"]";
		ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngPrimaryOrganization");
		SELECT_ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION= "attribute["+ATTRIBUTE_PACKAGING_PRIMARY_ORGANIZATION+"]";
		ATTRIBUTE_PACKAGING_RELEASE_CRITERIA=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngStructuredRelCriteriaRequired");
		SELECT_ATTRIBUTE_PACKAGING_RELEASE_CRITERIA= "attribute["+ATTRIBUTE_PACKAGING_RELEASE_CRITERIA+"]";
		ATTRIBUTE_PACKAGING_MATERIAL_TYPE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngPackMaterialType");
		SELECT_ATTRIBUTE_PACKAGING_MATERIAL_TYPE= "attribute["+ATTRIBUTE_PACKAGING_MATERIAL_TYPE+"]";
		ATTRIBUTE_PACKAGING_CLASS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngClass");
		SELECT_ATTRIBUTE_PACKAGING_CLASS= "attribute["+ATTRIBUTE_PACKAGING_CLASS+"]";
		ATTRIBUTE_PACKAGING_MFG_STATUS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngManufacturingStatus");
		SELECT_ATTRIBUTE_PACKAGING_MFG_STATUS= "attribute["+ATTRIBUTE_PACKAGING_MFG_STATUS+"]";
		
		ATTRIBUTE_PRODUCT_SEGMENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiProduct.pngSegment");
		SELECT_ATTRIBUTE_PRODUCT_SEGMENT= "attribute["+ATTRIBUTE_PRODUCT_SEGMENT+"]";
		ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiProduct.pngPrimaryOrganization");
		SELECT_ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION= "attribute["+ATTRIBUTE_PRODUCT_PRIMARY_ORGANIZATION+"]";
		ATTRIBUTE_PRODUCT_RELEASE_CRITERIA=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiProduct.pngStructuredRelCriteriaRequired");
	    SELECT_ATTRIBUTE_PRODUCT_RELEASE_CRITERIA= "attribute["+ATTRIBUTE_PRODUCT_RELEASE_CRITERIA+"]";
		ATTRIBUTE_PRODUCT_MFG_STATUS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiProduct.pngManufacturingStatus");
		SELECT_ATTRIBUTE_PRODUCT_MFG_STATUS= "attribute["+ATTRIBUTE_PRODUCT_MFG_STATUS+"]";
		//END:DTWPI-15
		ATTRIBUTE_PNG_UNDER_EBOM_CM_CONTROL = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngUnderEBOMCMControl");
		ATTRIBUTE_CLASS=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgClass");
		ATTRIBUTE_SEGMENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgSegment");
		ATTRIBUTE_RELEASE_CRITERIA=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgStructuredReleaseCriteriaRequired");
		ATTRIBUTE_MATERIAL_TYPE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgPackagingMaterialType");
		ATTRIBUTE_REFERENCE_TYPE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_ReferenceType");
		ATTRIBUTE_REPORTED_FUNCTION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgReportedFunction");
		
		SELECT_ATTRIBUTE_LIFECYCLE_STATUS = "attribute["+ATTRIBUTE_LIFECYCLE_STATUS+"]";
		SELECT_ATTRIBUTE_CLASS="attribute["+ATTRIBUTE_CLASS+"]";
		SELECT_ATTRIBUTE_SEGMENT="attribute["+ATTRIBUTE_SEGMENT+"]";
		SELECT_ATTRIBUTE_RELEASE_CRITERIA="attribute["+ATTRIBUTE_RELEASE_CRITERIA+"]";
		SELECT_ATTRIBUTE_MATERIAL_TYPE="attribute["+ATTRIBUTE_MATERIAL_TYPE+"]";
		SELECT_ATTRIBUTE_REPORTED_FUNCTION="attribute["+ATTRIBUTE_REPORTED_FUNCTION+"]";	
		ATTRIBUTE_PLMINSTANCE_PLM_EXTERNAL_ID = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMInstance.PLM_ExternalID");
		ATTRIBUTE_EXPLORATION_REMOVAL_LOCK= PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiExploration.pngRemovalLock");
		ATTRIBUTE_PACKAGING_REMOVAL_LOCK=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiPackaging.pngRemovalLock");
		ATTRIBUTE_PRODUCT_REMOVAL_LOCK=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiProduct.pngRemovalLock");
		ATTRIBUTE_ASSEMBLED_REMOVAL_LOCK=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiAssembled.pngRemovalLock");
		
		SYMBOLIC_NAME_MPAP = "type_"+TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART;
		SYMBOLIC_NAME_MPMP = "type_"+TYPE_PG_MASTER_PACKAGING_MATERIAL_PART;
		SYMBOLIC_NAME_MPP = "type_"+TYPE_PG_MASTER_PRODUCT_PART;
		SYMBOLIC_NAME_MRMP = "type_"+TYPE_PG_MASTER_RAW_MATERIAL_PART;
		SYMBOLIC_NAME_SHAPE_PART = "type_"+TYPE_SHAPE_PART;
		
		VALID_DT_SYMBOLIC_SYNC_TYPES.addElement(SYMBOLIC_NAME_MPAP);
		VALID_DT_SYMBOLIC_SYNC_TYPES.addElement(SYMBOLIC_NAME_MPMP);
		VALID_DT_SYMBOLIC_SYNC_TYPES.addElement(SYMBOLIC_NAME_MPP);
		VALID_DT_SYMBOLIC_SYNC_TYPES.addElement(SYMBOLIC_NAME_MRMP);
		VALID_DT_SYMBOLIC_SYNC_TYPES.addElement(SYMBOLIC_NAME_SHAPE_PART);
		
		SELECT_IPM_DOCUMENT_FILES_FROM_SPS =  "from["+REL_REFERENCE_DOCUMENT+"].to["+TYPE_PGIPMDOCUMENT+"].format.file.name";
		ATTRIBUTE_PNG_HEIGHT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngHeight");
		SELECT_ATTRIBUTE_PNG_HEIGHT="attribute["+ATTRIBUTE_PNG_HEIGHT+"]";
		ATTRIBUTE_PNG_WIDTH=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngWidth");
		SELECT_ATTRIBUTE_PNG_WIDTH="attribute["+ATTRIBUTE_PNG_WIDTH+"]";
		ATTRIBUTE_PNG_LENGTH=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngLength");
		SELECT_ATTRIBUTE_PNG_LENGTH="attribute["+ATTRIBUTE_PNG_LENGTH+"]";
		ATTRIBUTE_OUTER_DIMENSION_HEIGHT_ORIGINAL=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionHeightOriginal");
		ATTRIBUTE_OUTER_DIMENSION_WIDTH_ORIGINAL=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionWidthOriginal");
		ATTRIBUTE_OUTER_DIMENSION_LENGTH_ORIGINAL=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionLengthOriginal");
		ATTRIBUTE_OUTER_DIMENSION_HEIGHT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionHeight");
		ATTRIBUTE_OUTER_DIMENSION_WIDTH=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionWidth");
		ATTRIBUTE_OUTER_DIMENSION_LENGTH=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgOuterDimensionLength");
		ATTRIBUTE_PLM_EXCHANGE_STATUS_DS_VCUSTO=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMExchangeStatusDS.V_Custo");
		SELECT_ATTRIBUTE_PLM_EXCHANGE_STATUS_DS_VCUSTO="attribute["+ATTRIBUTE_PLM_EXCHANGE_STATUS_DS_VCUSTO+"]";
		ATTRIBUTE_PG_ENTERPRISE_NUMBER=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pngiDesignPart.pngEnterprisePartNumber");
		ATTRIBUTE_PG_SAPTYPE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgSAPType");
		ATTRIBUTE_ISONCE_INSTANTIABLE=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMCoreRepReference.V_isOnceInstantiable");
		SELECT_ATTRIBUTE_ISONCE_INSTANTIABLE="attribute["+ATTRIBUTE_ISONCE_INSTANTIABLE+"]";
		ATTRIBUTE_V_VERSION_COMMENT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMReference.V_versionComment");
		SELECT_ATTRIBUTE_V_VERSION_COMMENT="attribute["+ATTRIBUTE_V_VERSION_COMMENT+"]";
		ATTRIBUTE_PLMENTITY_V_NAME = PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMEntity.V_Name");
		SELECT_ATTRIBUTE_PLMENTITY_V_NAME="attribute["+ATTRIBUTE_PLMENTITY_V_NAME+"]";
		ATTRIBUTE_PLMENTITY_PLM_EXTERNALID= PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMEntity.PLM_ExternalID");
		ATTRIBUTE_IS_LAST_VERSION=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PLMReference.V_isLastVersion");
		ATTRIBUTE_PG_PALLET_LENGTH=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_pgPLPalletLength");
		SELECT_ATTRIBUTE_PG_PALLET_LENGTH="attribute["+ATTRIBUTE_PG_PALLET_LENGTH+"]";
		ATTRIBUTE_PALLET_WIDTH=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PalletWidth");
		SELECT_ATTRIBUTE_PALLET_WIDTH="attribute["+ATTRIBUTE_PALLET_WIDTH+"]";
		ATTRIBUTE_PALLET_HEIGHT=PropertyUtil.getSchemaProperty(PRSPContext.get(),"attribute_PalletHeight");
		SELECT_ATTRIBUTE_PALLET_HEIGHT="attribute["+ATTRIBUTE_PALLET_HEIGHT+"]";
	}
}
