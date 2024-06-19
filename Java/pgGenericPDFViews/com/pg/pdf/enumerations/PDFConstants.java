/**
  JAVA class created for IRM 2018x.6
   Project Name: IRM(Sogeti)
   JAVA Name: PDFConstants
   Purpose: JAVA class created for constant variables.
 **/

package com.pg.pdf.enumerations;
import com.pg.v3.custom.pgV3Constants;

public class PDFConstants implements pgV3Constants {
	 protected PDFConstants() {
	 }
	public static final String CONSTANT_STUDY_PROTOCOL_XML_PAGE = "pgStudyProtocolPDFXML";
	public static final String CONSTANT_STUDY_PROTOCOL_XSL_PAGE = "pgStudyProtocolPDFXSL";
	public static final String CONSTANT_PLACEMENTAGENCY_VIEW = "pgStudyProtocolPlacementAgency";
	public static final String CONSTANT_ALL_INFO_VIEW = "pgStudyProtocolAllInfo";
	public static final String CONST_VIEW="view";
	public static final String CONSTANT_FORM ="form";
	public static final String CONSTANT_TABLE ="table";
	public static final String ELEMENT_SP ="StudyProtocol";
	public static final String CONSTANT_SP ="SP";
	public static final String STUDY_PROTOCOL="Study Protocol";
	public static final String CONSTANT_REV="Rev";
	public static final String CONSTANT_OBJECT_ID ="objectId";
	public static final String CONSTANT_S ="s";
	public static final String CONSTANT_COLON =":";
	public static final String TABLE_INTENDED ="intented";
	public static final String CONSTANT_LEVEL ="level";
	public static final String INTEGER_ONE ="1";
	public static final String CONSTANT_EQUAL ="=";
	public static final String CONSTANT_PROGRAM ="program";
	public static final String CONSTANT_OBJLIST ="objectList";
	public static final String CONSTANT_LANGUAGE ="languageStr";
	public static final String CONSTANT_PARAMLIST="paramList";
	public static final String CONST_PROGRAM_HTML="programHTML";
	public static final String CONST_REQUESTMAP="requestMap";
	public static final String CONST_PARAMMAP="paramMap";
	public static final String CONST_WORK_DIR="workDir";
	public static final String CONSTANT_FOLDER_FONTS="fonts";
	public static final String CONSTANT_3DS_FONT="3ds-Regular.ttf";
	public static final String CONSTANT_VIEW_KIND = "view_kind";
	public static final String ALL_INFO_VIEW_DISPLAY_NAME="All Information View";
	//Modified by IRM pdf views 2018x.6 Dec_CW for Requirements 40810--Starts
	public static final String PLACEMENT_AGENCY_VIEW_DISPLAY_NAME="Placement Agency View";
	//Modified by IRM pdf views 2018x.6 Dec_CW for Requirements 40810--Ends
	public static final String CONSTANT_COMPANY_DISPLAY_NAME="The Procter & Gamble Company";
	public static final String SYMBOL_HYPHEN_STRICT="-";
	public static final String SYMBOL_OPEN_BRACKET="(";
	public static final String SYMBOL_CLOSE_BRACKET=")";
	public static final String  PDF_HEADER_CONST_SECURITY_CLASSIFICATION= "Constant_Security_Classification";
	public static final String  PDF_HEADER_CONST_COMPANY_AND_VIEW= "Constant_Company_and_View";
	public static final String PDF_HEADER_CONST_ATTRIBUTE_GPS_DISPLAY_NAME = "Constant_Attribute_GPS";
	public static final String PDF_HEADER_NAME_COLON = "Name: ";
	public static final String PDF_HEADER_CONST_NAME_COLON = "Constant_Name_Colon";
	public static final String PDF_HEADER_REVISION_COLON = "Revision: ";
	public static final String PDF_HEADER_CONST_REVISION_COLON = "Constant_Revision_Colon";
	public static final String PDF_HEADER_STATE_COLON = "State: ";
	public static final String PDF_HEADER_CONST_STATE_COLON = "Constant_State_Colon";
	public static final String PDF_HEADER_HAS_ATS_COLON = "Has ATS: ";
	public static final String PDF_HEADER_CONST_HAS_ATS_COLON = "Constant_HasATS_Colon";
	public static final String CONSTANT_TYPE_KIND = " type_kind";
	public static final String CONSTANT_BUSINESS_USE_CASE_VIEW = "Business Use";
	public static final String CONSTANT_CLASSIFICATION = "Classification";
	public static final String CONSTANT_NO = "No";
	public static final String CONST_PAGE1_HEADER1_COL1 = "page1.header1.col1";
	public static final String CONST_PAGE1_HEADER1_CENTER = "page1.header1.center";
	public static final String CONST_PAGE1_HEADER2_CENTER = "page1.header2.center";
	public static final String CONST_PAGE1_HEADER3_COL1 = "page1.header3.col1";
	public static final String CONST_PAGE1_HEADER3_COL2 = "page1.header3.col2";
	public static final String CONST_PAGE1_HEADER3_COL3 = "page1.header3.col3";
	public static final String CONST_PAGE1_HEADER3_COL4 = "page1.header3.col4";
	public static final String CONST_PAGE1_HEADER4_COL4 = "page1.header4.col4";
	public static final String CONST_PAGE2_HEADER2_CENTER = "page2.header2.center";
	public static final String CONST_PAGE2_HEADER3_COL3 = "page2.header3.col3";
	public static final String CONST_PAGE2_HEADER3_COL4 = "page2.header3.col4";
	public static final String CONSTANT_HTML_DATA = "HTML_DATA";
	public static final String CONSTANT_PDF_FILE_NAME = "PDF_FILE_NAME";
	public static final String CONSTANT_PDF_PAY_LOAD = "PDF_PAY_LOAD";
	public static final String CONSTANT_PDF_FILE_EXTENSION = ".pdf";
	public static final String CONSTANT_PDF_MERGE_FILE_NAME = "Merge.pdf";
	public static final String CONSTANT_JAVA_IO_TMP_DIR = "java.io.tmpdir";
	public static final String CONSTANT_UTF_8 = "UTF-8";
	public static final String CONSTANT_TEMP_PREFIX = "temp_";
	public static final String CONSTANT_PAGE = " Page ";
	public static final String CONSTANT_OF = " of ";
	public static final String CONSTANT_DENIED ="DENIED";
	public static final String CONSTANT_NULL ="null";
	public static final String CONSTANT_FILE_SEPARATOR ="file.separator";
	public static final String CONST_PG_MARKINGS="P&G";
	public static final String CONST_PRINTED="Printed";
	public static final String SYMBOL_PIPE ="|";
	public static final String SYMBOL_OPEN_FLOWER_BRACKET ="{";
	public static final String SYMBOL_CLOSED_FLOWER_BRACKET ="}";
	public static final String CONSTANT_TYPE_PGPKGSTUDYPROTOCOL="pgPKGStudyProtocol";
	public static final String CONSTANT_POLICY_PGPKGSIGNATUREREFERENCEDOC="pgPKGSignatureReferenceDoc";
	public static final String CONSTANT_POLICY_PGPKGSIGNATUREREFERENCEDOC_DISPLAY_NAME="Signature Reference";
	public static final String CONSTANT_POLICY_PGSELFAPPROVAL ="pgSelfApproval";
	public static final String CONSTANT_POLICY_PGSELFAPPROVAL_DISPLAY_NAME ="Self Approval";
	public static final String CONSTANT_TYPE ="Type";
	//22x Upgrade Modification Start
	public static final String CONST_PDF_BASE_FOLDER="pdfHtmlBase";
	public static final String CONSTANT_APP_CPN="emxCPN";
	public static final String CONSTANT_SRV_PATH="emxCPN.ServerPath";
	//22x Upgrade Modification Start
}
