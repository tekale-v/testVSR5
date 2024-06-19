package com.pg.dsm.custom;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import matrix.db.MxMessageSupport;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.dassault_systemes.enovia.apps.materialcomposition.enumeration.MATCSchema;
import com.dassault_systemes.enovia.apps.materialcomposition.util.MATCUtil;
import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationRelationship;
import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationType;
import com.dassault_systemes.enovia.formulation.enumeration.FormulationAttribute;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.library.LibraryCentralConstants;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.beans.pgCOSDetailBean;
import com.pg.v4.util.mos.ui.MosComponentAppUtils;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.Pattern;
import matrix.util.StringList;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PersonUtil;

public class pgPartSpecReport implements DomainConstants {
	private PrintWriter outLog = null;
	private static final String STRFINALLEVEL = "level";
	public static final String RELATIONSHIP_EBOM_SUBSTITUTE = PropertyUtil.getSchemaProperty(null,"relationship_EBOMSubstitute");
	private static final String ATTR_PG_ASSEMBLY_TYPE = PropertyUtil.getSchemaProperty("attribute_pgAssemblyType");
	public static final String SELECT_ATTR_PG_ASSEMBLY_TYPE = "attribute[" + ATTR_PG_ASSEMBLY_TYPE  + "]";
	public static final String ATTRIBUTE_BASEUNITOFMEASURE = PropertyUtil.getSchemaProperty(null,"attribute_UnitofMeasure");
	public static final String SELECT_ATTRIBUTE_BASEUNITOFMEASURE = "attribute[" + ATTRIBUTE_BASEUNITOFMEASURE  + "]";
	public static final String ATTRIBUTE_PG_VALID_UNTIL_DATE = PropertyUtil.getSchemaProperty(null,"attribute_pgValidUntilDate");
	public static final String SELECT_ATTRIBUTE_PG_VALID_UNTIL_DATE = "attribute[" + ATTRIBUTE_PG_VALID_UNTIL_DATE  + "]";
	public static final String ATTRIBUTE_PG_OPTIONAL_COMPONENT = PropertyUtil.getSchemaProperty(null,"attribute_pgOptionalComponent");
	public static final String SELECT_ATTRIBUTE_PG_OPTIONAL_COMPONENT = "attribute[" + ATTRIBUTE_PG_OPTIONAL_COMPONENT  + "]";
	private static final String COMMONCOLUMNS= "CommonColumns";
	private static final String EMXCPNSTRINGRESOURCE= "emxCPNStringResource";
	private static final String EMXCPN = "emxCPN";
	private static final String STRSUBSTITUTEPARTSINCOLUMN = "emxCPN.FamilyCareReport.Worksheet.SubstitutePartsIn.ColumnTypes";
	private static final String CAREALIZEDNAME= "CARealizedName";
	private static final String HYPERLINK = "Hyperlink:|";
	private static final String HYPERLINK_PIPE = "|";
	private static final String HYPERLINK_COMPARE = "Hyperlink:";
	private static final String CAPROPOSEDNAME = "CAProposedName";
	private static final String SCAID = "sCAId";
	private static final String CONST_DENIED = "#DENIED!";
	private static final String CONST_NO_ACCESS = "No Access";
	//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Starts 
	private static final String TYPE_PG_CUSTOMERUNIT = PropertyUtil.getSchemaProperty("type_pgCustomerUnitPart");
	private static final String TYPE_PG_INNERPACK = PropertyUtil.getSchemaProperty("type_pgInnerPackUnitPart");
	private static final String TYPE_PG_CONSUMERUNIT = PropertyUtil.getSchemaProperty("type_pgConsumerUnitPart");
	//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Ends 
	//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55935  - Start
	private static final String PG_PMP_CERTIFICATION_NAME = "to.from["+PropertyUtil.getSchemaProperty(null,"relationship_pgPLIPackagingCertifications")+"].to.name";
	//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55935  - End
	
	
	
	
	private static final String VENDOR_NAME = "to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.name";
	private static final String VENDOR_CODE = "to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from."+pgV3Constants.SELECT_ATTRIBUTE_PGSHORTCODE;
	private static final String ALNAME = "alName";
	private static final String ALREV = "alRev";
	private static final String ALSTATE = "alState";
	private static final String TITLE = "Title";
	private static final String SUBTYPE = "SubType";
	private static final String RELEASEDATE = "ReleaseDate";
	private static final String EXPRDATE = "ExpirationDate";
	private static final String ALID = "alId";
	private static final String ORGANIZATION = "from["+pgV3Constants.RELATIONSHIP_PGPRIMARYORGANIZATION+"].to.name";
	private static final String INPUTMEPSEP = "InputMEPSEP";
	private static final String ALMEPSEP= "AlternateMEPSEP";
	private static final String VENDORNAME = "VendorName";
	private static final String VENDORCODE = "VendorCode";
	//Added code for Req Ids : 34946, 34306, 34307, 34308, 34309, 34311, 34312, 34313, 34314 New Tab: Alternates--Ends
	//Added code for Requirement Id:33635 -The report should only show tabs which user selected as the input--Starts
	//Added the code for 22x.01 Feb CW Defect 49642 - Starts
	private static final String SEP_VENDOR_NAME = "to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.name";
	private static final String SEP_VENDOR_CODE = "to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from."+pgV3Constants.SELECT_ATTRIBUTE_PGSHORTCODE;
	//Added the code for 22x.01 Feb CW Defect 49642 - Ends
	//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
	public static final String ATTRIBUTE_START_EFFECTIVITY = PropertyUtil.getSchemaProperty(null,"attribute_StartEffectivity");
	public static final String SELECT_ATTRIBUTE_START_EFFECTIVITY = "attribute[" + ATTRIBUTE_START_EFFECTIVITY  + "]";
	//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
	
	//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Starts 
	private static final String REL_PARTFAMILYREFERENCE = PropertyUtil.getSchemaProperty("relationship_PartFamilyReference");
	private static final String REL_CLASSIFIEDITEM = PropertyUtil.getSchemaProperty("relationship_ClassifiedItem");
	private static final String REL_PARTSPECIFICATION = PropertyUtil.getSchemaProperty("relationship_PartSpecification");
	//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Starts 
	
	//Added code for Requirement id 46223 - IP & Export Control Tab in Part & Spec Reports - Starts	
	private static final String REL_PROTECTEDITEM = PropertyUtil.getSchemaProperty("relationship_ProtectedItem");

	private static final String DESCENDING = "descending";
	private static final String EMXSORTNUMERICALPHASMALLERBASE = "emxSortNumericAlphaSmallerBase";
	private static final String CLASSIFICATION = "Classification";
							
	//Added code for Requirement id 46223 - IP & Export Control Tab in Part & Spec Reports - Ends
	
	//Added the code for 22x.02 May CW Defect 52204 - Starts 
	private static final String STR_MASTERSPECIFICATIONNAME = "MasterSpecificationName";
	private static final String STR_MASTERSPECIFICATIONID = "MasterSpecificationId";
	private static final String STR_MASTERSPECIFICATIONTYPE = "MasterSpecificationType";
	private static final String STR_MASTERSPECIFICATIONSTATE = "MasterSpecificationState";
	private static final String STR_MASTERSPECIFICATIONTITLE = "MasterSpecificationTitle";
	private static final String STR_MASTERSPECIFICATIONSPECIFICATIONSUBTYPE = "MasterSpecificationSpecificationSubType";
	private static final String STR_MASTERPARTNAME= "MasterPartName";
	//Added the code for 22x.02 May CW Defect 52204 - Ends
	
	//Moved the code to handle Sonar Qube memory error - Starts
	private static final String STRCONSUMERCYCLATE = "ConsumerRecyclate";
	private static final String STRINDUSTRIALCYCLATE = "IndustrialRecyclate";
	private static final String STRFINALENVCLASS = "EnvClass";
	private static final String STRFINALPARTFAMILYNAME = "PartFamilyName";
	private static final String STRFINALCOMPONENTTITLE = "ComponentTitle";
	private static final String ISPARENT = "isParent";
	private static final String STRFINALPARENTNAME = "ParentName";
	private static final String STRFINALSEQUENCEVALUE = "Sequence value";
	private static final String STRFINALMATERIALLAYER = "MaterialLayer";
	private static final String STRFINALCHILDNAME = "ChildName";
	private static final String STRFINALCHILDREVISION = "ChildRevision"; //Added by DSM Reports (Sogeti) for 2022x.6 --- Defect#57760
	private static final String STRFINALCOMMONCOLUMNS = "CommonColumns";
	private static final String ATTRIBUTE_PRESERVATIVEFLAG = "attribute[Preservative Flag]";
	private static final String ATTRIBUTE_ACTIVEINGREDIENTFLAG = "attribute[Active Ingredient Flag]";
	private static final String ATTRIBUTE_ISCOLORANT = "attribute[Is Colorant]";
	private static final String ATTRFILL = "attribute[Fill]";
	private static final String ATTR_PGNSPCG = "attribute["+PropertyUtil.getSchemaProperty(null,"attribute_pgNSPCG")+"]";
	private static final String STRFINALCHILDTITLE = "ChildTitle";
	private static final String STRFINALCOMMENT = "Comment";
	private static final String STRFINALMINIMUMWEIGHT = "Minimum Weight";
	private static final String STRFINALMAXIMUMWEIGHT = "Maximum Weight";
	private static final String STRFINALQUANTITY = "Quantity";
	private static final String STRFINALQUANTITYUNITOFMEASURE = "QuantityUnitOfMeasure";
	private static final String STRFINALCHILDTYPE = "ChildType";
	private static final String STRINPUTVALUE = ".inputvalue";
	private static final String STRFROMSTART ="from[";
	private static final String STRTONAME ="].to.name";
	private static final String SPECREADER = "SpecReader";
	private static final String TAB_PERFORMANCECHAR = "~Performance Characteristics";
	//Modified the code for 2022x.02 May CW Defect 52204 - Starts
	private static final String STRMINIMUMPERCENTAGEWEIGHTBYWEIGHT = "Minimum Percentage Weight By Weight";
	private static final String STRMAXIMUMPERCENTAGEWEIGHTBYWEIGHT = "Maximum Percentage Weight By Weight";
	//Modified the code for 2022x.02 May CW Defect 52204 - Ends
	//Moved the code to handle Sonar Qube memory error - Ends
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53088 - Start
	private static final String STR_RELATIONSHIP_PGPRODUCTPLATFORMFOP = PropertyUtil.getSchemaProperty(null, "relationship_pgProductPlatformFOP");
	private static final String ATTRIBUTE_PG_INHERITED_FROM_PLATFORM = PropertyUtil.getSchemaProperty("attribute_pgInheritedFromPlatform");
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53088 - Ends
	
	// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
	private static final String ATTRIBUTE_PGTARGETPERCENTWEIGHTBYWEIGHT = "attribute[pgTargetPercentWeightbyWeight]";  
	private static final String STRTARGETPERCENTAGEWEIGHTBYWEIGHT = "Target Percent Weight By Weight";
	private static final String ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE = "attribute[Percent Post Industrial Recyclate]";
	// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - End
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53205 - Start
	private static final String STR_ORIGINATING_SOURCE_CSS = "CSS";
	private static final String ATTRIBUTE_PGTNUMBER = PropertyUtil.getSchemaProperty(null, "attribute_pgTNumber");
	private static final String SELECT_ATTRIBUTE_PGTNUMBER = "attribute[" + ATTRIBUTE_PGTNUMBER + "]";
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53205 - End
	
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
	private static final String STRFROMEMAILID = "strFromEmailId";
	private static final String STRTOEMAILID = "strToEmailId";
	private static final String STRSUBJECT = "strSubject";
	private static final String STRMESSAGEBODY = "strMessageBody";
	private static final String STR_PERSON_PLM_ADMIN = "PLM Admin";
	private static final String STR_ENOVIA = "Enovia";
	private static final String STR_SPECREADER ="SpecReader";
	private static final String STR_COMMON="common";
	private static final String STR_SYMBOL_BRACKETS="{0}";
	private static final String STR_TEXT_HTML="text/html";
	private static final String PATH_SEPARATOR="/";
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Ends
	//Added by DSM for 22x CW-05 for Requirement 49721  -changes for DSM Reports MOS Components - START
	private static final String STR_COUNTRYRESTRICTIONINFO = "pgCountriesOfSale:getCountryAndRestrictionInfo";
	private static final String STR_COUNTRY = "country";
	private static final String ATTRIBUTE_MOSPOAOVERRIDELRR = PropertyUtil.getSchemaProperty("attribute_pgMOSPOAOverrideLRR");
	private static final String SELECT_ATTRIBUTE_PGMOSFPPOVERRIDDEN = "attribute[" + ATTRIBUTE_MOSPOAOVERRIDELRR + "]"; 
	private static final String ATTRIBUTE_PGCOSRESTRICTION = PropertyUtil.getSchemaProperty("attribute_pgCOSRestriction");
	private static final String SELECT_ATTRIBUTE_PGCOSRESTRICTION = "attribute[" + ATTRIBUTE_PGCOSRESTRICTION + "]"; 
	//Added by DSM for 22x CW-05 for Requirement 49721  -changes for DSM Reports MOS Components - END
	//Added for Defect 50780:22x01 -- Starts
	/**
	 * @param context
	 * @param dobjPart
	 * @return
	 */
	 //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START
	public String getPartFamilyName(Context context, DomainObject dobjPart, PrintWriter outLog, String strOriginatingSource) throws FrameworkException{
		String strPartFamilyName = DomainConstants.EMPTY_STRING;
		StringList slSelect = new StringList(2);
		MapList mpPartFamilyName = null;
		boolean isContextPushed = false;
		try {
			if(SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
				//Pushing User Agent Context to get the Data if the Spec Reader User have access
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
			}
			
			slSelect.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			slSelect.add(DomainConstants.SELECT_NAME);
			mpPartFamilyName = dobjPart.getRelatedObjects(context,pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM ,pgV3Constants.TYPE_PARTFAMILY, slSelect, null, true,true, (short)1, null, null, 0);
			if(null != mpPartFamilyName) {
			for(int i=0;i<mpPartFamilyName.size();i++) {
				Map mpPartFamily = (Map) mpPartFamilyName.get(i);
				if (mpPartFamily.get(DomainConstants.SELECT_NAME) instanceof StringList){
					strPartFamilyName = mpPartFamily.get(DomainConstants.SELECT_ATTRIBUTE_TITLE).toString();
					strPartFamilyName = strPartFamilyName.replace("[", "").replace("]", "");
				} else {
					strPartFamilyName = (String) mpPartFamily.get(DomainConstants.SELECT_ATTRIBUTE_TITLE).toString();
				}
			}
			}
		} catch (Exception e) {
			outLog.print("Exception in getPartFamilyName method "+e+"\n");
		} finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		return strPartFamilyName;
	}
	//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- END
	//Added for Defect 50780:22x01 -- Ends
	//Added the code for 22x Feb CW Requirement 45443 - Starts
	/**
	 * @param context
	 * @param dobjPart
	 * @param mpCommonColumnsDetail
	 * @param strUserName
	 * @return
	 * @throws FrameworkException 
	 */
	public MapList getSubstitutePartsInDetails(Context context, DomainObject dobjPart, Map<String,Object> mpCommonColumnsDetail, String strUserName,PrintWriter outLog) throws FrameworkException {
		StringList slSelects = new StringList();
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_ID);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(DomainConstants.SELECT_REVISION);
		slSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		slSelects.add(pgV3Constants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE);
		slSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);


		Map mpSubstitutePartRow = null;

		StringList sRelSelects = new StringList();
		sRelSelects.add(DomainRelationship.SELECT_ID);
		sRelSelects.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
		sRelSelects.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
		sRelSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCHANGE);
		sRelSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSUBSTITUTECOMBINATIONNUMBER);
		sRelSelects.add(STRFINALLEVEL);
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.name");
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.type");
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.id");
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.revision");
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.policy");
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"]");
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].id");
		//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+DomainConstants.SELECT_ATTRIBUTE_TITLE);
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		sRelSelects.add("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+SELECT_ATTR_PG_ASSEMBLY_TYPE);
		//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
		String sChildPartSpecSubType = "";
		Map mpEBOMRelAttr = new HashMap();
		DomainRelationship domSubRel = null;
		DomainObject domPart = null;
		Map mpObjAttr = new HashMap();
		String strCAConnected = "";
		String strQuantity = "";
		String strRefDesignator = "";
		String strBUOM = "";
		String strComment = "";
		String strValidStartDate = "";
		String strValidEndDate = "";
		String strSubCombinationNumber = "";
		String strChange = "";
		String strSubPartName = "";
		String strSubPartType ="";
		String strSubPartPolicy ="";
		String strSubPartTitle ="";
		String strSubPartRev = "";
		String strSubPartSpecSubType ="";
		String strSubsRelId ="";
		//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
		String strSubAttrTitle="";
		String strSubAttrBaseUnitofMeasure="";
		String strSubAttrAssemblyType="";
		//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
		StringList slObjSelects = new StringList();
		slObjSelects.add(SELECT_TYPE);
		slObjSelects.add(SELECT_NAME);
		slObjSelects.add(SELECT_REVISION);
		slObjSelects.add(SELECT_CURRENT);
		slObjSelects.add(SELECT_POLICY);
		slObjSelects.add(SELECT_ATTRIBUTE_TITLE);
		slObjSelects.add(SELECT_ATTR_PG_ASSEMBLY_TYPE);
		//Added for Defect 51534:22x 01 CW -- Starts
		slObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
	    //Added for Defect 51534:22x 01 CW -- Ends
		MapList mlSubstitutePartsInAll=new MapList();
		//Modified for Defect 51534:22x 01 CW -- Starts
		String strContextUser = "";
		boolean bContextPushed = false;
		String strSubPolicy ="";
		boolean bSubNoAccess = false;
		String strSubID = "";
		//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - Start
		String strSubChildID = DomainConstants.EMPTY_STRING;
		boolean bSubChildAccess = false;
		//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - End
		DomainObject domSubPart = null;
		//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
		//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
		try {
			strContextUser = context.getUser();
			//We are pushing the User Agent context to fetch the Substitute Part info - if we are doing the same with CM/Supplier/any user who does not have access on Substitute Part, ml will be empty but data is shown in the UI and some columns are updated with No Access so we are making this change to match the report with UI
			if(!bContextPushed){
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,pgV3Constants.PERSON_USER_AGENT),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				bContextPushed = true;
			}
			MapList mlReturnList =  dobjPart.getRelatedObjects(context, // Context
					pgV3Constants.RELATIONSHIP_EBOM, // Relationship
					pgV3Constants.TYPE_PART, // Type
					slSelects, // Object Select
					sRelSelects, // Rel Select
					false, // get To
					true, // get From
					(short) 1, // recurse level
					null, // object where clause
					null, // relationship where clause
					0); // limit
			int mlSubstitutePartsInDetailsSize = mlReturnList.size();
			if(mlSubstitutePartsInDetailsSize > 0) {
				for(int i=0;i<mlSubstitutePartsInDetailsSize;i++) {
					mpSubstitutePartRow = (Map)mlReturnList.get(i);
					sChildPartSpecSubType = (String)mpSubstitutePartRow.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
					if(pgV3Constants.CAPS_TRUE.equalsIgnoreCase((String)(mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"]")))) {
						Object objSubstitutePart = (mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].id"));
						if(objSubstitutePart instanceof StringList) {
							StringList slSubstitutePartType = (StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.type");
							StringList slSubstitutePartName = (StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.name");
							StringList slSubstitutePartId = (StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.id");
							StringList slSubstitutePartRevision = (StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.revision");
							StringList slSubstitutePartPolicy = (StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.policy");
							StringList slSubsRelId = (StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].id");
							//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
							StringList slSubstituteAttrTitle = (StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+DomainConstants.SELECT_ATTRIBUTE_TITLE);
							StringList slSubstituteAttrBaseUnitofMeasure=(StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
							StringList slSubstituteAttrAssemblyType=(StringList)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+SELECT_ATTR_PG_ASSEMBLY_TYPE);
							//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - Start
							strSubID = (String)mpSubstitutePartRow.get(DomainConstants.SELECT_ID);
							pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
							bSubNoAccess = pgFamilyCare.accessCheck(context, strUserName, strSubID);
							//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - End
							 
							for(int k=0;k<slSubstitutePartId.size();k++) {
								Map mpSubstitutePartNew = new HashMap();
			
								//Modified by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - Start
								strSubChildID  = slSubstitutePartId.get(k);
								//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
								bSubChildAccess = pgFamilyCare.accessCheck(context, strUserName, strSubChildID);
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
								//Modified by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - End
								//domSubPart = DomainObject.newInstance(context, strSubID);
								//strSubPolicy = domSubPart.getInfo(context,SELECT_POLICY);
//								if(CONST_DENIED.equalsIgnoreCase(strSubPolicy)){
//									bSubNoAccess = true;
//								}
								//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
								
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
								mpSubstitutePartNew.put("EBOMSubstituteType", (String)slSubstitutePartType.get(k));
								mpSubstitutePartNew.put("EBOMSubstituteName", (String)slSubstitutePartName.get(k));
								mpSubstitutePartNew.put("EBOMSubstituteId", (String)slSubstitutePartId.get(k));
								mpSubstitutePartNew.put("EBOMSubstituteRevision", (String)slSubstitutePartRevision.get(k));
								mpSubstitutePartNew.put("EBOMSubstitutePolicy", (String)slSubstitutePartPolicy.get(k));
								mpSubstitutePartNew.put("EBOMPartType", mpSubstitutePartRow.get(DomainConstants.SELECT_TYPE));
								mpSubstitutePartNew.put("EBOMPartName",mpSubstitutePartRow.get(DomainConstants.SELECT_NAME));
								mpSubstitutePartNew.put("EBOMPartId",mpSubstitutePartRow.get(DomainConstants.SELECT_ID));
								mpSubstitutePartNew.put("EBOMPartRevision",mpSubstitutePartRow.get(DomainConstants.SELECT_REVISION));
								//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
								mpSubstitutePartNew.put("EBOMPartTitle",mpSubstitutePartRow.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
								//Added for Defect 51534:22x 01 CW -- Starts
								mpSubstitutePartNew.put("EBOMPartSpecSubType",mpSubstitutePartRow.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE));
								//Added for Defect 51534:22x 01 CW -- Ends
								mpSubstitutePartNew.put("EBOMLevel",mpSubstitutePartRow.get(STRFINALLEVEL));
								//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
								mpSubstitutePartNew.put("SubPartTitle",(String)slSubstituteAttrTitle.get(k));
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
//								domPart = DomainObject.newInstance(context, (String)slSubstitutePartId.get(k));
//								mpObjAttr = domPart.getInfo(context, slObjSelects);
//								mpSubstitutePartNew.put("SubPartType",mpObjAttr.get(SELECT_TYPE));
//								mpSubstitutePartNew.put("SubPartName",mpObjAttr.get(SELECT_NAME));
//								mpSubstitutePartNew.put("SubPartRev",mpObjAttr.get(SELECT_REVISION));
								//mpSubstitutePartNew.put("SubPartTitle",mpObjAttr.get(SELECT_ATTRIBUTE_TITLE));
								//Added for Defect 51534:22x 01 CW -- Starts
								//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
								if(!bSubNoAccess){
									mpSubstitutePartNew.put("EBOMSubstitutePolicy",CONST_DENIED);
									//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
								} else {
									//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
									mpSubstitutePartNew.put("EBOMSubstitutePolicy",slSubstitutePartPolicy.get(k));
									//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
								}
								if(!bSubChildAccess){
									mpSubstitutePartNew.put("SubPartUnitOfMeasure",pgV3Constants.NO_ACCESS);
								} else {
									mpSubstitutePartNew.put("SubPartUnitOfMeasure",slSubstituteAttrBaseUnitofMeasure.get(k));
								}
								//Modified by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - End
								//Added for Defect 51534:22x 01 CW -- Ends
								//if(mpObjAttr.containsKey(SELECT_ATTR_PG_ASSEMBLY_TYPE)){
									//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
									mpSubstitutePartNew.put("SubPartSpecSubType",(String)slSubstituteAttrAssemblyType.get(k));
									//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
								//}else{
									//mpSubstitutePartNew.put("SubPartSpecSubType",DomainConstants.EMPTY_STRING);
								//}
								//Added for Defect 51534:22x 01 CW -- Ends
								domSubRel = DomainRelationship.newInstance(context, (String)slSubsRelId.get(k));
								mpEBOMRelAttr = domSubRel.getAttributeMap(context);
								mpSubstitutePartNew.put("SubRelChange",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_PGCHANGE));
								mpSubstitutePartNew.put("SubRelCombNumber",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_PGSUBSTITUTECOMBINATIONNUMBER));
								mpSubstitutePartNew.put("SubRelQuantity",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_QUANTITY));
								//Added for Defect 51534:22x 01 CW -- Starts
								mpSubstitutePartNew.put("SubRelBaseUOM",mpEBOMRelAttr.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE));
								//Added for Defect 51534:22x 01 CW -- Ends
								mpSubstitutePartNew.put("SubRelValidStartDate",mpEBOMRelAttr.get(ATTRIBUTE_START_EFFECTIVITY));
								mpSubstitutePartNew.put("SubRelValidEndDate",mpEBOMRelAttr.get(ATTRIBUTE_PG_VALID_UNTIL_DATE));
								mpSubstitutePartNew.put("SubRelComment",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_COMMENT));
								mpSubstitutePartNew.put("SubRelRefDesignator",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_REFERENCEDESIGNATOR));
								mpSubstitutePartNew.put("SubRelOptionalComp",mpEBOMRelAttr.get(ATTRIBUTE_PG_OPTIONAL_COMPONENT));

								mpSubstitutePartNew.put(COMMONCOLUMNS, mpCommonColumnsDetail);
								mlSubstitutePartsInAll.add(mpSubstitutePartNew);
							}
						}else if(objSubstitutePart instanceof String) {
							Map mpSubstitutePartNew = new HashMap();
							//Modified the code for 2022x.02 May CW Defect 52430 - Starts
							mpSubstitutePartNew.put("EBOMSubstituteType", mpSubstitutePartRow.get("frommid[EBOM Substitute].to.type"));
							//Modified the code for 2022x.02 May CW Defect 52430 - Ends
							mpSubstitutePartNew.put("EBOMSubstituteName", mpSubstitutePartRow.get("frommid[EBOM Substitute].to.name"));
							mpSubstitutePartNew.put("EBOMSubstituteId", mpSubstitutePartRow.get("frommid[EBOM Substitute].to.id"));
							mpSubstitutePartNew.put("EBOMSubstituteRevision",mpSubstitutePartRow.get("frommid[EBOM Substitute].to.revision"));
							mpSubstitutePartNew.put("EBOMSubstitutePolicy",mpSubstitutePartRow.get("frommid[EBOM Substitute].to.policy"));
							mpSubstitutePartNew.put("EBOMPartType", mpSubstitutePartRow.get(DomainConstants.SELECT_TYPE));
							mpSubstitutePartNew.put("EBOMPartName",mpSubstitutePartRow.get(DomainConstants.SELECT_NAME));
							//Added for Defect 51534:22x 01 CW -- Starts
							mpSubstitutePartNew.put("EBOMPartSpecSubType",mpSubstitutePartRow.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE));
							//Added for Defect 51534:22x 01 CW -- Ends 
							mpSubstitutePartNew.put("EBOMPartId",mpSubstitutePartRow.get(DomainConstants.SELECT_ID));
							mpSubstitutePartNew.put("EBOMPartRevision",mpSubstitutePartRow.get(DomainConstants.SELECT_REVISION));
							mpSubstitutePartNew.put("EBOMLevel",mpSubstitutePartRow.get(STRFINALLEVEL));
							//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
							mpSubstitutePartNew.put("EBOMPartTitle",mpSubstitutePartRow.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
							mpSubstitutePartNew.put("SubPartTitle",mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+DomainConstants.SELECT_ATTRIBUTE_TITLE));
							//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							domPart = DomainObject.newInstance(context, (String)mpSubstitutePartRow.get(DomainConstants.SELECT_ID));
							
							
							//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
							
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							
							strSubID = (String)mpSubstitutePartRow.get(DomainConstants.SELECT_ID);
							strSubChildID = (String)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to.id");
							//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
							pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
							bSubNoAccess = pgFamilyCare.accessCheck(context, strUserName, strSubID);
							bSubChildAccess = pgFamilyCare.accessCheck(context, strUserName, strSubChildID);
							//Modified by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - End
							
							//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							
							//domSubPart = DomainObject.newInstance(context, strSubID);
							//strSubPolicy = domSubPart.getInfo(context,SELECT_POLICY);
//							if(CONST_DENIED.equalsIgnoreCase(strSubPolicy)){
//								bSubNoAccess = true;
//							}
							//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
						
							//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							
//							mpObjAttr = domPart.getInfo(context, slObjSelects);
//							mpSubstitutePartNew.put("SubPartType",mpObjAttr.get(SELECT_TYPE));
//							mpSubstitutePartNew.put("SubPartName",mpObjAttr.get(SELECT_NAME));
//							mpSubstitutePartNew.put("SubPartRev",mpObjAttr.get(SELECT_REVISION));
//							mpSubstitutePartNew.put("SubPartTitle",mpObjAttr.get(SELECT_ATTRIBUTE_TITLE));
							
							//Added for Defect 51534:22x 01 CW -- Starts
							//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
							if(!bSubNoAccess){
								mpSubstitutePartNew.put("EBOMSubstitutePolicy",CONST_DENIED);
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							} else {
								//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
								mpSubstitutePartNew.put("EBOMSubstitutePolicy",mpSubstitutePartRow.get("frommid[EBOM Substitute].to.policy"));
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							}
							if(!bSubChildAccess){
								mpSubstitutePartNew.put("SubPartUnitOfMeasure",pgV3Constants.NO_ACCESS);
							} else {
								mpSubstitutePartNew.put("SubPartUnitOfMeasure",mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE));
							//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							}
							//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 56076 - End
							
							//Added for Defect 51534:22x 01 CW -- Ends
							
							//if(mpObjAttr.containsKey(SELECT_ATTR_PG_ASSEMBLY_TYPE)){
								//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
								mpSubstitutePartNew.put("SubPartSpecSubType",mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].to."+SELECT_ATTR_PG_ASSEMBLY_TYPE));
								//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
							//}else{
								//mpSubstitutePartNew.put("SubPartSpecSubType",DomainConstants.EMPTY_STRING);
							//}
							strSubsRelId = (String)mpSubstitutePartRow.get("frommid["+RELATIONSHIP_EBOM_SUBSTITUTE+"].id");
							domSubRel = DomainRelationship.newInstance(context, strSubsRelId);
							mpEBOMRelAttr = domSubRel.getAttributeMap(context);
							mpSubstitutePartNew.put("SubRelChange",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_PGCHANGE));
							mpSubstitutePartNew.put("SubRelCombNumber",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_PGSUBSTITUTECOMBINATIONNUMBER));
							mpSubstitutePartNew.put("SubRelQuantity",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_QUANTITY));
							mpSubstitutePartNew.put("SubRelBaseUOM",mpEBOMRelAttr.get(ATTRIBUTE_BASEUNITOFMEASURE));
							mpSubstitutePartNew.put("SubRelValidStartDate",mpEBOMRelAttr.get(ATTRIBUTE_START_EFFECTIVITY));
							mpSubstitutePartNew.put("SubRelValidEndDate",mpEBOMRelAttr.get(ATTRIBUTE_PG_VALID_UNTIL_DATE));
							mpSubstitutePartNew.put("SubRelComment",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_COMMENT));
							mpSubstitutePartNew.put("SubRelRefDesignator",mpEBOMRelAttr.get(pgV3Constants.ATTRIBUTE_REFERENCEDESIGNATOR));
							mpSubstitutePartNew.put("SubRelOptionalComp",mpEBOMRelAttr.get(ATTRIBUTE_PG_OPTIONAL_COMPONENT));
							mpSubstitutePartNew.put(COMMONCOLUMNS, mpCommonColumnsDetail);
							mlSubstitutePartsInAll.add(mpSubstitutePartNew);
						}
					}
				}
			}
		}catch(Exception e) {
			outLog.print("Exception in getSubstitutePartsInDetails Method "+e+"\n");
			outLog.flush();
		} finally{
			if(bContextPushed){
				ContextUtil.popContext(context);
				bContextPushed = false;
			}
		}
		return mlSubstitutePartsInAll;
	}
	//Added the code for 22x Feb CW Requirement 45443 - Ends
	
	//Added by DSM for 22x CW-05 for Requirement 49721  -changes for DSM Reports MOS Components - START
	// Writing MOS Component Details in Excel file 
	/**
	 * @param context
	 * @param workbook
	 * @param rowHeaderMOSCompDetails
	 * @param cellStyleMarketOfSale
	 * @param mlMOSCompDetails
	 * @param sheetMOSCompDetails
	 * @param strHyperlink
	 * @param styleGlobal
	 */
	public void updateWorksheetMOSCompDetails(Context context, XSSFWorkbook workbook,XSSFRow rowHeaderMOSCompDetails,XSSFCellStyle cellStyleMarketOfSale,MapList mlMOSCompDetails,XSSFSheet sheetMOSCompDetails,String strHyperlink, PrintWriter outLog, XSSFCellStyle styleGlobal) {
		try {
			

			pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.PartAndSpecReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll=0;

			String strColumnNames = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.MOSCompDetails.ColumnTypes");
			
			String strColumnName;
			String strColumnValue;
			StringList slIndividualColumnNames = FrameworkUtil.split(strColumnNames, ",");
			String columnName;
			int slIndividualColumnNamesSize = slIndividualColumnNames.size();
			for (int i = 0;i<slIndividualColumnNamesSize;i++) {
				columnName = slIndividualColumnNames.get(i);
				strColumnName = String.valueOf(columnName).trim();
				strColumnValue = strColumnName;
				Cell cell = rowHeaderMOSCompDetails.createCell(i);
				cell.setCellStyle(cellStyleMarketOfSale);
				cell.setCellValue(strColumnValue);	
			}

			HashMap<Integer,Object> hm= new HashMap<>();
			int rowCount1 = 0;
			String strTypeName;
			String strCurrentState;
			Map<String,Object> mp;
			Hashtable<String,Object> htCommonCol;
			String strTypeDisplayName=null;
			String strPartTypeDisplayName=null;
			String strValue;
			String strParentId;
			String strHyperlinkId;
			String strCellValue;

			int hmSize = 0;
			int columnCount1 = 0;
			int mlMOSSize = mlMOSCompDetails.size();
			String[] strSplittedValue;
			for (int i=0;i<mlMOSSize;i++){
				
				String pgIsFCExist = DomainConstants.EMPTY_STRING;
				String pgIsArtExist = DomainConstants.EMPTY_STRING;
				String pgNonReleasedArtExist = DomainConstants.EMPTY_STRING;
				String pgCountriesOfSale = DomainConstants.EMPTY_STRING;
				String pgMOSPOAOverrideLRR = DomainConstants.EMPTY_STRING;
				String partType = DomainConstants.EMPTY_STRING;
				
				iRowCountAll = iRowCountAll + 1;
				++rowCount1;
				XSSFRow row = sheetMOSCompDetails.createRow(rowCount1);
				columnCount1 = 0;
				mp = (Map)mlMOSCompDetails.get(i);

				htCommonCol = (Hashtable) mp.get(COMMONCOLUMNS);
				strParentId = (String)htCommonCol.get(DomainConstants.SELECT_ID);
				strCurrentState = (String)htCommonCol.get(DomainConstants.SELECT_CURRENT);
				String strPartId = (String) mp.get(DomainConstants.SELECT_ID);
				strTypeName = (String) htCommonCol.get(DomainConstants.SELECT_TYPE);
				strTypeDisplayName= pgFamilyCare.getTypeDisplayName(context,strTypeName);
				if(mp.containsKey(DomainConstants.SELECT_TYPE)) {
					partType = (String) mp.get(DomainConstants.SELECT_TYPE);
				}
				strPartTypeDisplayName= pgFamilyCare.getTypeDisplayName(context,partType);
				
				if(mp.containsKey(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST)) {
					pgIsFCExist = (String) mp.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
					pgIsFCExist = updateValueToYesNo(pgIsFCExist);
				}
				
				if(mp.containsKey(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST)) {
					pgIsArtExist = (String) mp.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
					pgIsArtExist = updateValueToYesNo(pgIsArtExist);
				}
				
				if(mp.containsKey(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST)) {
					pgNonReleasedArtExist = (String) mp.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
					pgNonReleasedArtExist = updateValueToYesNo(pgNonReleasedArtExist);
				}
				
				if(mp.containsKey(STR_COUNTRY)) {
					pgCountriesOfSale = (String) mp.get(STR_COUNTRY);
				}
				
				if(mp.containsKey(SELECT_ATTRIBUTE_PGMOSFPPOVERRIDDEN)) {
					pgMOSPOAOverrideLRR = (String) mp.get(SELECT_ATTRIBUTE_PGMOSFPPOVERRIDDEN);
				}
				
				hm.put(0, HYPERLINK+htCommonCol.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+strParentId);
				hm.put(1, htCommonCol.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				hm.put(2, strTypeDisplayName);
				hm.put(3, htCommonCol.get(DomainConstants.SELECT_REVISION));
				hm.put(4, HYPERLINK+mp.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+strPartId);
				hm.put(5, mp.get(DomainConstants.SELECT_REVISION));
				hm.put(6, strPartTypeDisplayName);
				hm.put(7, mp.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));

				hm.put(8, pgCountriesOfSale);
				hm.put(9, pgMOSPOAOverrideLRR);
				hm.put(10, pgIsFCExist);
				hm.put(11, pgIsArtExist);
				hm.put(12, pgNonReleasedArtExist);
				
				hmSize = hm.size();
				XSSFCellStyle style = workbook.createCellStyle();
				style = pgFamilyCare.setForeGroundColor(strCurrentState, style);
				XSSFCellStyle style1 = workbook.createCellStyle();

				for(int j=0;j<hmSize;j++){	
					Cell cell = row.createCell(columnCount1);
					columnCount1++;
					if (j == 0) {
						style = pgFamilyCare.makeColumnNumeric(workbook, style);
					}
					cell.setCellStyle(style);
					strCellValue = (String)hm.get(j);

					if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
						strSplittedValue = strCellValue.split("\\|",-1); 
						strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
						strValue = strSplittedValue[(strSplittedValue.length)-2];
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
							pgFamilyCare.getHyperlink(context,cell,workbook,strValue,strHyperlinkId,strCurrentState);
						
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)) {					
							cell.setCellValue(strValue);
						}
					} else {
						cell.setCellValue((String)hm.get(j));
						if(j == 0) {
							style1.cloneStyleFrom(style);
							style1.setWrapText(true);
							cell.setCellStyle(style1);
						}
					}
				}
			}
			pgFamilyCare.sheetFormatter(sheetMOSCompDetails); 	

		} catch (Exception e) {
			outLog.print("Exception in updateWorksheetMOSCompDetails "+e+"\n");
			outLog.flush();
		}
	}
	//Added by DSM for 22x CW-05 for Requirement 49721  -changes for DSM Reports MOS Components - END

	
	public void updateWorksheetSubstitutePartsIn(Context context, XSSFWorkbook workbook, XSSFRow rowHeaderSubstitutePartsIn,
	XSSFCellStyle cellStyleSubstitutePartsIn, MapList mlSubstitutePartsIn, XSSFSheet sheetSubstitutePartsIn, String strHyperlink, PrintWriter outLog, XSSFCellStyle styleGlobal) {
		try {
			
			
			pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.PartAndSpecReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll=0;
			String strColumnNames = EnoviaResourceBundle.getProperty(context, EMXCPN, context.getLocale(),"emxCPN.FamilyCareReport.Worksheet.SubstitutePartsIn.ColumnTypes");
			pgFamilyCareReport.createCellForWorksheet(context,strColumnNames,rowHeaderSubstitutePartsIn,cellStyleSubstitutePartsIn,STRSUBSTITUTEPARTSINCOLUMN);

			int rowCount = 0;
			String strCellValue = "";
			Hashtable<String,Object> htCommonCol = new Hashtable<>();
			Map<String,Object> mp=new HashMap<>();
			HashMap<Integer,Object> hm= new HashMap<>();
			String strId = "";
			String strValue = "";
			String strHyperlinkId = "";
			String[] strSplittedValue;
			String strCurrentState = "";
			String strTypeDisplay = "";
			String subsType ="";
			String subType="";
			String sLanguage = context.getSession().getLanguage();
			int columnCount1 = 0;
			int mlSubstitutePartsInSize = mlSubstitutePartsIn.size();
			for(int i=0;i<mlSubstitutePartsInSize;i++) {
				++rowCount;
				iRowCountAll = iRowCountAll + 1;
				XSSFRow row = sheetSubstitutePartsIn.createRow(rowCount);
				columnCount1 = 0;
				mp = (Map)mlSubstitutePartsIn.get(i);
				htCommonCol =  (Hashtable<String, Object>) mp.get(COMMONCOLUMNS);
				strId = (String) htCommonCol.get(DomainConstants.SELECT_ID);
				strCurrentState = (String) htCommonCol.get(DomainConstants.SELECT_CURRENT);
				strTypeDisplay = (String) htCommonCol.get(DomainConstants.SELECT_TYPE);
				strTypeDisplay = i18nNow.getTypeI18NString(strTypeDisplay, sLanguage);
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				String sCAID = (String) htCommonCol.get(SCAID);
				if(UIUtil.isNotNullAndNotEmpty(sCAID) && !sCAID.contains(HYPERLINK_PIPE)) {
					if(htCommonCol.get(CAREALIZEDNAME) != null && htCommonCol.get(CAPROPOSEDNAME) != null) {
						hm.put(0, HYPERLINK+htCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
					} else if(htCommonCol.get(CAREALIZEDNAME) != null){
						hm.put(0, HYPERLINK+htCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
					} else if(htCommonCol.get(CAPROPOSEDNAME) != null){
						hm.put(0, HYPERLINK+htCommonCol.get(CAPROPOSEDNAME)+HYPERLINK_PIPE+sCAID);
					} else {
						hm.put(0, DomainConstants.EMPTY_STRING);
					}
				}else{
					hm.put(0,htCommonCol.get(CAPROPOSEDNAME));
				}
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
						

				hm.put(1, htCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_REASON_FOR_CHANGE));
				hm.put(2, HYPERLINK+htCommonCol.get(DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+strId);
				hm.put(3, htCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
				hm.put(4, strTypeDisplay);
				hm.put(5, htCommonCol.get(DomainConstants.SELECT_REVISION));
				hm.put(6, htCommonCol.get(DomainConstants.SELECT_CURRENT));
				hm.put(7, mp.get("EBOMLevel"));//Level
				//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
				hm.put(8,  HYPERLINK+mp.get("EBOMSubstituteName")+HYPERLINK_PIPE+mp.get("EBOMSubstituteId"));//Sub Part Name
				//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
				hm.put(9, mp.get("SubRelChange"));//Chg
				hm.put(10, mp.get("SubRelCombNumber"));//Subs Comb Number
				hm.put(11, mp.get("SubPartTitle"));//Sub Part Title
				hm.put(12, mp.get("EBOMSubstituteRevision"));//Sub Part Rev
				//Added for Defect 51534:22x 01 CW -- Starts
				hm.put(13, pgFamilyCare.getTypeDisplayName(context,mp.get("EBOMSubstituteType").toString()));//Sub Part Type
				hm.put(14, mp.get("SubPartSpecSubType"));//Sub Part Spec Sub Type
				hm.put(15, mp.get("EBOMSubstitutePolicy"));//Sub Part Policy
				hm.put(16, mp.get("SubRelQuantity"));//Quantity
				hm.put(17, mp.get("SubPartUnitOfMeasure"));//BUOM
				hm.put(18, mp.get("SubRelValidStartDate"));//Valid Start Date
				hm.put(19, mp.get("SubRelValidEndDate"));//Valid End date
				hm.put(20, mp.get("SubRelRefDesignator"));//Ref Designator
				hm.put(21, mp.get("SubRelComment"));//Comment
				//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
				hm.put(22, HYPERLINK+mp.get("EBOMPartName")+HYPERLINK_PIPE+mp.get("EBOMPartId"));//EBOM Part Name
				hm.put(23, mp.get("EBOMPartTitle"));//EBOM Part Title
				//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
				hm.put(24, mp.get("EBOMPartRevision"));//EBOM Part Rev
				hm.put(25, pgFamilyCare.getTypeDisplayName(context,mp.get("EBOMPartType").toString()));//EBOM Part Type
				hm.put(26, mp.get("EBOMPartSpecSubType"));//EBOM Part Spec Sub Type
				hm.put(27, mp.get("SubRelOptionalComp"));//Optional Comp
				//Added for Defect 51534:22x 01 CW -- Ends
				//Modified the code for 22x.01 Feb CW Defect 51534 - Starts
				XSSFCellStyle style = workbook.createCellStyle();
				style = pgFamilyCare.setForeGroundColor(strCurrentState, style);
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				XSSFCellStyle style1 = workbook.createCellStyle();
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				
				for(int j=0;j<hm.size();j++){								
					//Modified the code for _2022x_Aug CW Req-47097 - Start
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - Start
					Cell cell = row.createCell(columnCount1++);
					if (j == 2) {
						style = pgFamilyCare.makeColumnNumeric(workbook, style);
					}
					cell.setCellStyle(style);
					
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - End
					//Modified the code for _2022x_Aug CW Req-47097 - End
					if(hm.get(j) instanceof StringList) {
						strCellValue = hm.get(j).toString().replace("[", "").replace("]", "").replace(pgV3Constants.SYMBOL_COMMA,pgV3Constants.SYMBOL_PIPE);
					}
					else {
						strCellValue = (String)hm.get(j);
					}
					if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
						strSplittedValue = strCellValue.split("\\|",-1); 
						strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
						strValue = strSplittedValue[(strSplittedValue.length)-2];
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
							pgFamilyCare.getHyperlink(context,cell,workbook,strValue,strHyperlinkId,strCurrentState);
						
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)) {					
							cell.setCellValue(strValue);
						}
					}else {
						cell.setCellValue(strCellValue);
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
						if(j == 0) {
							style1.cloneStyleFrom(style);
							style1.setWrapText(true);
							cell.setCellStyle(style1);
						}
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
					}
					
				}
				//Modified the code for 22x.01 Feb CW Defect 51534 - Ends
			}
			pgFamilyCare.sheetFormatter(sheetSubstitutePartsIn); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
		}catch(Exception e) {
			outLog.print("Exception in updateWorksheetSubstitutePartsIn "+e+"\n");
			outLog.flush();
		}
	}

	//Added the code for 22x Feb CW Requirement 45443 - Ends
	
	/**
	 * @param mpCommonColumnsDetail
	 * @param isCalculationdoneforAlternatePart
	 * @param mpAlternateRow
	 * @param mlAlternateAll
	 * @param mpReturnList
	 * @param mpReturnListSize
	 */
	public MapList updateMEPSEPDetails(Map<String, Object> mpCommonColumnsDetail, boolean isCalculationdoneforAlternatePart,
			Map<String, Object> mpAlternateRow, MapList mlAlternateAll, MapList mpReturnList, int mpReturnListSize) {
		Map<String, Object> mpMEPSEP;
		String strPolicy;
		Map<String, Object> mpNewMEPSEP;
		Map<String, Object> mpFinal;
		String strVendorName = DomainConstants.EMPTY_STRING;
		String strVendorCode = DomainConstants.EMPTY_STRING;
		for(int i=0;i<mpReturnListSize;i++){
			mpMEPSEP = (Map)mpReturnList.get(i);
		
			mpNewMEPSEP = new HashMap<>();
			mpFinal = new HashMap<>();
			strPolicy = (String)mpMEPSEP.get(DomainConstants.SELECT_POLICY);
		
			mpNewMEPSEP.put("name", mpMEPSEP.get(DomainConstants.SELECT_NAME));
			mpNewMEPSEP.put("id", mpMEPSEP.get(DomainConstants.SELECT_ID));
			//Modified the code for 22xFeb CW for Defect 49642 - Starts
			if(mpMEPSEP.get(VENDOR_NAME) instanceof StringList || mpMEPSEP.get(SEP_VENDOR_NAME) instanceof StringList){
				if(pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					strVendorName = mpMEPSEP.get(VENDOR_NAME).toString();
					strVendorName = strVendorName.replace("[", "").replace("]", "");
					strVendorCode = mpMEPSEP.get(VENDOR_CODE).toString();
					strVendorCode = strVendorCode.replace("[", "").replace("]", "");

				}else if(pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					strVendorName = mpMEPSEP.get(SEP_VENDOR_NAME).toString();
					strVendorName = strVendorName.replace("[", "").replace("]", "");
					strVendorCode = mpMEPSEP.get(SEP_VENDOR_CODE).toString();
					strVendorCode = strVendorCode.replace("[", "").replace("]", "");
					//mpNewMEPSEP.put(VENDORNAME, strVendorName);
					//mpNewMEPSEP.put(VENDORCODE, strVendorCode);
				}
				mpNewMEPSEP.put(VENDORNAME, strVendorName);
				mpNewMEPSEP.put(VENDORCODE, strVendorCode);
			}
			else{
				if(pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					mpNewMEPSEP.put(VENDORNAME, mpMEPSEP.get(VENDOR_NAME));
					mpNewMEPSEP.put(VENDORCODE, mpMEPSEP.get(VENDOR_CODE));
				}else if(pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					mpNewMEPSEP.put(VENDORNAME, mpMEPSEP.get(SEP_VENDOR_NAME));
					mpNewMEPSEP.put(VENDORCODE, mpMEPSEP.get(SEP_VENDOR_CODE));
				}

			}
			
			if(isCalculationdoneforAlternatePart){
				mpNewMEPSEP.put(ALNAME, mpAlternateRow.get(DomainConstants.SELECT_NAME));
				mpNewMEPSEP.put(ALID, mpAlternateRow.get(DomainConstants.SELECT_ID));
				mpNewMEPSEP.put(ALREV, mpAlternateRow.get(DomainConstants.SELECT_REVISION));
				mpNewMEPSEP.put(ALSTATE, mpAlternateRow.get(DomainConstants.SELECT_CURRENT));
				mpNewMEPSEP.put(TITLE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
				mpNewMEPSEP.put(SUBTYPE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE));
				mpNewMEPSEP.put(RELEASEDATE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
				mpNewMEPSEP.put(EXPRDATE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE));
				mpNewMEPSEP.put(ORGANIZATION, mpAlternateRow.get(ORGANIZATION));
				mpFinal.put(ALMEPSEP, mpNewMEPSEP);
				mpFinal.put(COMMONCOLUMNS, mpCommonColumnsDetail);
				mlAlternateAll.add(mpFinal);
			} 
			else{
				mpNewMEPSEP.putAll(mpMEPSEP);
				mpFinal.put(INPUTMEPSEP, mpNewMEPSEP);
				mpFinal.put(COMMONCOLUMNS, mpCommonColumnsDetail);
				mlAlternateAll.add(mpFinal);
			}
		}

		return mlAlternateAll;
	}
	
	/**
	 * @param mpCommonColumnsDetail
	 * @param isCalculationdoneforAlternatePart
	 * @param mpAlternateRow
	 * @param mlAlternateAll
	 * @param mpReturnList
	 * @param mpReturnListSize
	 */
	public MapList updateNoAccess(Map<String, Object> mpCommonColumnsDetail, boolean isCalculationdoneforAlternatePart,
			Map<String, Object> mpAlternateRow, MapList mlAlternateAll, MapList mpReturnList, int mpReturnListSize) {
		Map<String, Object> mpMEPSEP;
		String strPolicy;
		Map<String, Object> mpNewMEPSEP;
		Map<String, Object> mpFinal;
		String strVendorName = DomainConstants.EMPTY_STRING;
		String strVendorCode = DomainConstants.EMPTY_STRING;
		for(int i=0;i<mpReturnListSize;i++){
			mpMEPSEP = (Map)mpReturnList.get(i);
			
			mpNewMEPSEP = new HashMap<>();
			mpFinal = new HashMap<>();
			strPolicy = (String)mpMEPSEP.get(DomainConstants.SELECT_POLICY);
			

			mpNewMEPSEP.put("name", "No Access");
			mpNewMEPSEP.put("id", "No Access");
			//Modified the code for 22xFeb CW for Defect 49642 - Starts
			if(mpMEPSEP.get(VENDOR_NAME) instanceof StringList || mpMEPSEP.get(SEP_VENDOR_NAME) instanceof StringList){
				if(pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					strVendorName = mpMEPSEP.get(VENDOR_NAME).toString();
					strVendorName = strVendorName.replace("[", "").replace("]", "");
					strVendorCode = mpMEPSEP.get(VENDOR_CODE).toString();
					strVendorCode = strVendorCode.replace("[", "").replace("]", "");

				}else if(pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					strVendorName = mpMEPSEP.get(SEP_VENDOR_NAME).toString();
					strVendorName = strVendorName.replace("[", "").replace("]", "");
					strVendorCode = mpMEPSEP.get(SEP_VENDOR_CODE).toString();
					strVendorCode = strVendorCode.replace("[", "").replace("]", "");
					//mpNewMEPSEP.put(VENDORNAME, strVendorName);
					//mpNewMEPSEP.put(VENDORCODE, strVendorCode);
				}
				mpNewMEPSEP.put(VENDORNAME, "No Access");
				mpNewMEPSEP.put(VENDORCODE, "No Access");
			}
			else{
				if(pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					mpNewMEPSEP.put(VENDORNAME, "No Access");
					mpNewMEPSEP.put(VENDORCODE, "No Access");
				}else if(pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase((String)mpMEPSEP.get(DomainConstants.SELECT_POLICY))) {
					mpNewMEPSEP.put(VENDORNAME, "No Access");
					mpNewMEPSEP.put(VENDORCODE, "No Access");
				}

			}
			if(isCalculationdoneforAlternatePart){
				mpNewMEPSEP.put(ALNAME, mpAlternateRow.get(DomainConstants.SELECT_NAME));
				mpNewMEPSEP.put(ALID, mpAlternateRow.get(DomainConstants.SELECT_ID));
				mpNewMEPSEP.put(ALREV, mpAlternateRow.get(DomainConstants.SELECT_REVISION));
				mpNewMEPSEP.put(ALSTATE, mpAlternateRow.get(DomainConstants.SELECT_CURRENT));
				mpNewMEPSEP.put(TITLE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
				mpNewMEPSEP.put(SUBTYPE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE));
				mpNewMEPSEP.put(RELEASEDATE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
				mpNewMEPSEP.put(EXPRDATE, mpAlternateRow.get(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE));
				mpNewMEPSEP.put(ORGANIZATION, mpAlternateRow.get(ORGANIZATION));
				mpFinal.put(ALMEPSEP, mpNewMEPSEP);
				mpFinal.put(COMMONCOLUMNS, mpCommonColumnsDetail);
				mlAlternateAll.add(mpFinal);
			} 
			else{
				mpNewMEPSEP.putAll(mpMEPSEP);
				mpFinal.put(INPUTMEPSEP, mpNewMEPSEP);
				mpFinal.put(COMMONCOLUMNS, mpCommonColumnsDetail);
				mlAlternateAll.add(mpFinal);
			}
		}

		return mlAlternateAll;
	}
	
	//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Starts 
	
	/**
	 * @param context
	 * @param strUserName
	 * @param doObj
	 * @param mpCommonColumnsDetail
	 * @return
	 * @throws FrameworkException
	 */
	//Modified the code for 22x.02 May CW Defect 52204 - Starts 
	public MapList getRelatedMasterSpecDoc(Context context, 
			DomainObject doObj,Map mpCommonColumnsDetail, String strUserName) throws FrameworkException {
		boolean isContextPushed = false;
		//Modified the code for 22x.02 May CW Defect 52204 - Ends
		MapList mlMasterSpecInfoAll = new MapList();
		
		//Modified the code for 22x.02 May CW Defect 52204 - Starts 
		String strMasterName="";
		String strMasterId="";
		String strMasterRevision="";
		String strMasterSpecId;
		String strMasterSpecName;
		StringList slMasterSpecIds;
		StringList slMasterSpecName;
		String strMasterCurrent="";
		String strMasterType="";
		String strPartFamilyId="";
		StringList slConnectedPartFamilyList;
		Map mpConnectedMasterSpecData = new HashMap();
		
		
		
		//Modified the code for 22x.02 May CW Defect 52204 - Ends
		StringList slSelects = new StringList();
		//Need Exception - not Assigned to null to avoid functionality breakage because of null value
		//Need Exception - not Assigned to null to avoid functionality breakage because of null value
		slSelects.add(DomainConstants.SELECT_ID);
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		StringList strlObjectSelectable = new StringList(5);
		strlObjectSelectable.add(DomainConstants.SELECT_ID);
		strlObjectSelectable.add(DomainConstants.SELECT_TYPE);
		strlObjectSelectable.add(DomainConstants.SELECT_NAME);
		strlObjectSelectable.add(DomainObject.getAttributeSelect(DomainConstants.ATTRIBUTE_TITLE));
		strlObjectSelectable.add(DomainConstants.SELECT_REVISION);
		StringList strlRelSelectable = new StringList(3);
		strlRelSelectable.add(DomainRelationship.SELECT_FROM_TYPE);
		strlRelSelectable.add(DomainRelationship.SELECT_TO_TYPE);
		strlRelSelectable.add(DomainRelationship.SELECT_FROM_ID);
		StringList slMasterSelects = new StringList();
		slMasterSelects.add(DomainConstants.SELECT_NAME);
		slMasterSelects.add(DomainConstants.SELECT_CURRENT);
		//Added the code for 22x.02 May CW Defect 52204 - Starts 
		StringList slObjSelects = new StringList();
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.id");
		slObjSelects.add("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.name");
		slObjSelects.add("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.type");
		slObjSelects.add("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.revision");
		slObjSelects.add("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.current");
		slObjSelects.add("to[" +DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from[Part Family].id");
		slObjSelects.add("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.from["+REL_PARTSPECIFICATION+"].to.id");
		slObjSelects.add("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.from["+REL_PARTSPECIFICATION+"].to.name");
		Map mpInputObj = null;
		try {
			
			//Pushing the User Agent Context to pull the Data irrespective of User Access
		ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
		isContextPushed = true;
		//Added the code for 22x.02 May CW Defect 52204 - Ends
		if(doObj.getTypeName().equalsIgnoreCase(pgV3Constants.TYPE_FINISHEDPRODUCTPART)){
			//Need Exception - not Assigned to null to avoid functionality breakage because of null value
			MapList ebomPartList = doObj.getRelatedObjects(context, //context
					DomainObject.RELATIONSHIP_EBOM, //rel pattern
					TYPE_PG_CUSTOMERUNIT + "," + TYPE_PG_INNERPACK + "," + TYPE_PG_CONSUMERUNIT, //type pattern
					strlObjectSelectable, // object select
					strlRelSelectable, // rel select
					false, //get To
					true, //get From
					(short)0, //recurse level
					"", // obj where clause
					null, //rel where clause
					0);//limit
			
			//Need Exception - not Assigned to null to avoid functionality breakage because of null value
			Map mpIndividualChild = new HashMap<>();
			DomainObject doObjChild = null;
			for(int i=0;i<ebomPartList.size();i++) {
				mpIndividualChild = (Map) ebomPartList.get(i);
				doObjChild = DomainObject.newInstance(context, mpIndividualChild.get(DomainConstants.SELECT_ID).toString());
				/*Needs Exception for this get Related as there are multiple Objects which has multiple Specification Doc Connected*/
				mpInputObj = doObjChild.getInfo(context, slObjSelects);
				 if(null != mpInputObj && !mpInputObj.isEmpty()) {
					 strMasterId = (String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.id");
					 strMasterRevision=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.revision");
					 strMasterType=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.type");
					 strMasterName=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.name");
					 strMasterCurrent=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.current");
					 strPartFamilyId=(String)mpInputObj.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from[Part Family].id");
					 
					 if(UIUtil.isNotNullAndNotEmpty(strPartFamilyId) && UIUtil.isNotNullAndNotEmpty(strMasterId) && strMasterCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE)) {
						 slConnectedPartFamilyList = getPartFamilyListFromHeirarchy(context,strPartFamilyId);
						 mpConnectedMasterSpecData = getConnectedMasterSpec(context,strMasterId, slConnectedPartFamilyList);
						 DomainObject dobjMaster = DomainObject.newInstance(context,strMasterId);
						//Need Exception, calling the api to get the Specifications connected
						 slMasterSpecIds = dobjMaster.getInfoList(context, "from["+REL_PARTSPECIFICATION+"].to.id");
						
							for(int j=0;j<slMasterSpecIds.size();j++) {
								setMasterSpecificationData(context, slMasterSpecIds.get(j), mpConnectedMasterSpecData, strUserName, mpCommonColumnsDetail,mlMasterSpecInfoAll);
							}
						
					 }
				 
				 }
			}
		}else{ 
			
			 mpInputObj = doObj.getInfo(context, slObjSelects);
			 if(null != mpInputObj && !mpInputObj.isEmpty()) {
				 strMasterId = (String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.id");
				 strMasterRevision=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.revision");
				 strMasterType=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.type");
				 strMasterName=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.name");
				 strMasterCurrent=(String)mpInputObj.get("to["+REL_CLASSIFIEDITEM+"].frommid["+REL_PARTFAMILYREFERENCE+"].torel.to.current");
				 strPartFamilyId=(String)mpInputObj.get("to[" + DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM + "].from[Part Family].id");
				 if(UIUtil.isNotNullAndNotEmpty(strPartFamilyId) && UIUtil.isNotNullAndNotEmpty(strMasterId) && strMasterCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE)) {
					 slConnectedPartFamilyList = getPartFamilyListFromHeirarchy(context,strPartFamilyId);
					 mpConnectedMasterSpecData = getConnectedMasterSpec(context,strMasterId, slConnectedPartFamilyList);
					 DomainObject dobjMaster = DomainObject.newInstance(context,strMasterId);
					//Need Exception, calling the api to get the Specifications connected to satisfy business Logic
					 slMasterSpecIds = dobjMaster.getInfoList(context, "from["+REL_PARTSPECIFICATION+"].to.id");
					
						for(int i=0;i<slMasterSpecIds.size();i++) {
							setMasterSpecificationData(context, slMasterSpecIds.get(i), mpConnectedMasterSpecData, strUserName, mpCommonColumnsDetail,mlMasterSpecInfoAll);
							
						}	
					
					 
				 }
				
				 
			 }
		}
		//Modified the code for 2022x.02 May CW Defect 52204 - starts
		if(!mlMasterSpecInfoAll.isEmpty()) {				
		    Comparator<Map<String, String>> mapComparator = (final Map<String, String> o1, final Map<String, String> o2)->o1.get(DomainConstants.SELECT_NAME).compareTo(o2.get(DomainConstants.SELECT_NAME));
			Collections.sort(mlMasterSpecInfoAll, mapComparator);
			
			
		}
		//Modified the code for 2022x.02 May CW Defect 52204 - Ends
		//Added the code for 22x.02 May CW Defect 52204 - Starts
		}catch(Exception e) {
			outLog.print("Exception in getRelatedMasterSpecDoc method"+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}
		}
		//Added the code for 22x.02 May CW Defect 52204 - Ends
		return mlMasterSpecInfoAll;
	}

	/**
	 * @param context
	 * @param rowHeader
	 * @param cellStyleMasterSpecification
	 * @param mlMasterSpecAll
	 * @param rowCount1
	 * @param sheetSpecsDocs
	 * @param workbook
	 * @param strHyperlink
	 * @param style
	 * @return
	 */
	public int updateExcelMasterSpec(List<Object> lParamList) {
		Context context = (Context)lParamList.get(0);
		XSSFRow rowHeader = (XSSFRow) lParamList.get(1);
		XSSFCellStyle cellStyleMasterSpecification = (XSSFCellStyle) lParamList.get(2);
		MapList mlMasterSpecAll = (MapList) lParamList.get(3);
		int rowCount1 = (int) lParamList.get(4);
		XSSFSheet sheetSpecsDocs = (XSSFSheet) lParamList.get(5);
		XSSFWorkbook workbook = (XSSFWorkbook) lParamList.get(6);
		String strHyperlink = (String) lParamList.get(7);
		XSSFCellStyle style = (XSSFCellStyle) lParamList.get(8);
		try {
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.PartAndSpecReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			String sStringResourceFile=EMXCPNSTRINGRESOURCE; 
			String sLanguage = context.getSession().getLanguage();
			String strSpecsDocsMasterSpecificationName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.MasterSpecification.Name",sStringResourceFile, sLanguage);
			String strSpecsDocsMasterSpecificationTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.MasterSpecification.Title",sStringResourceFile, sLanguage);
			String strSpecsDocsMasterSpecificationType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.MasterSpecification.Type",sStringResourceFile, sLanguage);
			String strSpecsDocsMasterSpecificationSpecificationSubType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.MasterSpecification.SpecificationSubType",sStringResourceFile, sLanguage);
			String strSpecsDocsMasterSpecificationState=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.MasterSpecification.State",sStringResourceFile, sLanguage);
			String strSpecsDocsMasterSpecificationMasterPartName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.MasterSpecification.MasterPartName",sStringResourceFile, sLanguage);

			// Modified by IRM (Sogeti) for 2022x.03 August CW Requirement 47172 - starts
			// 17 Specification Name
			Cell cellMasterSpecificationName = rowHeader.createCell(16);
			cellMasterSpecificationName.setCellStyle(cellStyleMasterSpecification);
			cellMasterSpecificationName.setCellValue(strSpecsDocsMasterSpecificationName);

			// 18 Specification Title
			Cell cellMasterSpecificationTitle = rowHeader.createCell(17);
			cellMasterSpecificationTitle.setCellStyle(cellStyleMasterSpecification);
			cellMasterSpecificationTitle.setCellValue(strSpecsDocsMasterSpecificationTitle);

			// 19 Specification Type
			Cell cellMasterSpecificationType = rowHeader.createCell(18);
			cellMasterSpecificationType.setCellStyle(cellStyleMasterSpecification);
			cellMasterSpecificationType.setCellValue(strSpecsDocsMasterSpecificationType);

			// 20 Specification Sub Type
			Cell cellMasterSpecificationSpecificationSubType = rowHeader.createCell(19);
			cellMasterSpecificationSpecificationSubType.setCellStyle(cellStyleMasterSpecification);
			cellMasterSpecificationSpecificationSubType.setCellValue(strSpecsDocsMasterSpecificationSpecificationSubType);

			// 21 Specification State
			Cell cellMasterSpecificationState = rowHeader.createCell(20);
			cellMasterSpecificationState.setCellStyle(cellStyleMasterSpecification);
			cellMasterSpecificationState.setCellValue(strSpecsDocsMasterSpecificationState);	

			// 22 Master Part Name
			Cell cellMasterSpecificationMasterPart = rowHeader.createCell(21);
			cellMasterSpecificationMasterPart.setCellStyle(cellStyleMasterSpecification);
			cellMasterSpecificationMasterPart.setCellValue(strSpecsDocsMasterSpecificationMasterPartName);
			// Modified by IRM (Sogeti) for 2022x.03 August CW Requirement 47172 - Ends

			int iListSize=mlMasterSpecAll.size();
			pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
			//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
			XSSFCellStyle style1 = workbook.createCellStyle();
			//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
			for (int i=0;i<iListSize;i++){
				rowCount1 = rowCount1+1;
				XSSFRow row = sheetSpecsDocs.createRow(rowCount1);
				int columnCount1 = 0;
				Map mpInnerData = (Map)mlMasterSpecAll.get(i);
				HashMap hm= new HashMap<>();
				//Need Exception - not Assigned to null to avoid functionality breakage because of null value
				Map hmCommonCol = new HashMap<>();
				hmCommonCol= (Map)mpInnerData.get(COMMONCOLUMNS);
				//Need Exception - not Assigned to null to avoid functionality breakage because of null value
				Map hmMasterCol = new HashMap<>();
				
				String strTypeDisplayName=null;
				String strTypeName = (String)hmCommonCol.get("type");
				strTypeDisplayName= pgFamilyCare.getTypeDisplayName(context,strTypeName);
				String strCurrent = (String)hmCommonCol.get("current");
				
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				String sCAID = (String) hmCommonCol.get(SCAID);
				if(UIUtil.isNotNullAndNotEmpty(sCAID) && !sCAID.contains(HYPERLINK_PIPE)) {
					if(hmCommonCol.get("CARealizedName") != null && hmCommonCol.get("CAProposedName") != null) {
						hm.put(0, HYPERLINK+hmCommonCol.get("CARealizedName")+HYPERLINK_PIPE+hmCommonCol.get("sCAId"));
					} else if(hmCommonCol.get("CARealizedName") != null){
						hm.put(0, HYPERLINK+hmCommonCol.get("CARealizedName")+HYPERLINK_PIPE+hmCommonCol.get("sCAId"));
					} else if(hmCommonCol.get("CAProposedName") != null){
						hm.put(0, HYPERLINK+hmCommonCol.get("CAProposedName")+HYPERLINK_PIPE+hmCommonCol.get("sCAId"));
					} else {
						hm.put(0, DomainConstants.EMPTY_STRING);
					}	
				}else {
					hm.put(0,hmCommonCol.get(CAPROPOSEDNAME));
				}
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				hm.put(1, hmCommonCol.get("attribute[Reason for Change]"));
				hm.put(2, HYPERLINK+hmCommonCol.get("name")+HYPERLINK_PIPE+hmCommonCol.get("id"));
				hm.put(3, hmCommonCol.get("attribute[Title]"));			
				hm.put(4, strTypeDisplayName);
				hm.put(5, hmCommonCol.get("revision"));
				hm.put(6, hmCommonCol.get("current"));
				hm.put(7,  DomainConstants.EMPTY_STRING);			
				hm.put(8,  DomainConstants.EMPTY_STRING);
				hm.put(9,  DomainConstants.EMPTY_STRING);
				hm.put(10, DomainConstants.EMPTY_STRING);
				hm.put(11, DomainConstants.EMPTY_STRING);
				hm.put(12, DomainConstants.EMPTY_STRING);
				hm.put(13, DomainConstants.EMPTY_STRING);
				hm.put(14, DomainConstants.EMPTY_STRING);
				hm.put(15, DomainConstants.EMPTY_STRING);
				//Added the code for 22x.02 May CW Defect 52204 - Starts
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - Start
				hm.put(16, HYPERLINK+mpInnerData.get(STR_MASTERSPECIFICATIONNAME)+HYPERLINK_PIPE+mpInnerData.get(STR_MASTERSPECIFICATIONID));
				hm.put(17, mpInnerData.get(STR_MASTERSPECIFICATIONTITLE));
				hm.put(18, pgFamilyCare.getTypeDisplayName(context,mpInnerData.get(STR_MASTERSPECIFICATIONTYPE).toString()));
				hm.put(19, mpInnerData.get(STR_MASTERSPECIFICATIONSPECIFICATIONSUBTYPE));
				hm.put(20, mpInnerData.get(STR_MASTERSPECIFICATIONSTATE));
				hm.put(21, mpInnerData.get(STR_MASTERPARTNAME));
				//Added the code for 22x.02 May CW Defect 52204 - Ends
				hm.put(22, DomainConstants.EMPTY_STRING);
				hm.put(23, DomainConstants.EMPTY_STRING);
				hm.put(24, DomainConstants.EMPTY_STRING);
				hm.put(25, DomainConstants.EMPTY_STRING);
				hm.put(26, DomainConstants.EMPTY_STRING);
				hm.put(27, DomainConstants.EMPTY_STRING);
				hm.put(28, DomainConstants.EMPTY_STRING);
				hm.put(29, DomainConstants.EMPTY_STRING);
				hm.put(30, DomainConstants.EMPTY_STRING);
				hm.put(31, DomainConstants.EMPTY_STRING);
				hm.put(32, DomainConstants.EMPTY_STRING);
				hm.put(33, DomainConstants.EMPTY_STRING);
				hm.put(34, DomainConstants.EMPTY_STRING);
				hm.put(35, DomainConstants.EMPTY_STRING);
				hm.put(36, DomainConstants.EMPTY_STRING);
				hm.put(37, DomainConstants.EMPTY_STRING);
				hm.put(38, DomainConstants.EMPTY_STRING);
				hm.put(39, DomainConstants.EMPTY_STRING);
				hm.put(40, DomainConstants.EMPTY_STRING);
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - End
				for(int j=0;j<hm.size();j++){
					Cell cell = row.createCell(columnCount1);
					columnCount1 = columnCount1+1;
					//Added code for Req Id : 33634 - Hyperlinks--Starts
					String strCellValue = (String)hm.get(j);
					if(!strCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE) && !strCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASED)){
						cell.setCellStyle(style);
					}
					if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
						String[] strSplittedValue = strCellValue.split("\\|", -1); 
						String strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
						String strValue = strSplittedValue[(strSplittedValue.length)-2];
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Starts
						//Added the code for 22x.02 May CW Defect 52204 - Starts
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && rowCount1 < iHyperLinkLimit && !CONST_NO_ACCESS.equals(strHyperlinkId)){
							//Added the code for 22x.02 May CW Defect 52204 - Ends
							pgFamilyCare.getHyperlink(context,cell,workbook,strValue,strHyperlinkId,strCurrent);

						} else if(UIUtil.isNotNullAndNotEmpty(strValue)){					
							cell.setCellValue(strValue);
						}
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Ends
					}else {
						cell.setCellValue((String)hm.get(j));
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
						if(j == 0) {
							style1.cloneStyleFrom(style);
							style1.setWrapText(true);
							cell.setCellStyle(style1);
						}
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
					}
					//Added code for Req Id : 33634 - Hyperlinks--Ends
				}
			}
		} catch (Exception e) {
			outLog.print("Exception in  updateExcelMasterSpec: "+e+"\n");
			outLog.flush();
		}
		return rowCount1;
	}
	//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Ends 
	
	
	//Added code for Requirement id 46223 - IP & Export Control Tab in Part & Spec Reports - Starts	
	
	/**
	 * @param context
	 * @param strUserName
	 * @param doObj
	 * @param mpCommonColumnsDetail
	 * @return
	 * @throws FrameworkException
	 */
	public MapList getRelatedIpAndExportControl(Context context, 
			DomainObject doObj,Map mpCommonColumnsDetail) {
		MapList mlIpClassAll = new MapList();
		Map mpIpClass;
		MapList parentClasses = null;
		StringList slSelects = new StringList();
		Iterator<?> itr2 = null;
		String parentclassName = null;
		Map<?, ?> parentClassMap = null;
		StringList elemHtmlList = null;
		String parentClassTitle=null;
		slSelects.add(DomainConstants.SELECT_ID);
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(DomainConstants.SELECT_DESCRIPTION);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		DomainObject doIpClass = null; 
		StringList selectStmts = new StringList(3);
        selectStmts.add(DomainConstants.SELECT_ID);
        selectStmts.add(DomainConstants.SELECT_NAME);
		try {
			StringList slIpClassId = doObj.getInfoList(context, "to["+REL_PROTECTEDITEM+"].from.id");
			int iListSize=slIpClassId.size();
			for(int i=0; i<iListSize; i++) {
				//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 54465 - Start
				if(!CONST_DENIED.equalsIgnoreCase(slIpClassId.get(i))){
					//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 54465 - End
					doIpClass=DomainObject.newInstance(context,slIpClassId.get(i));
					elemHtmlList = new StringList();
					mpIpClass = doIpClass.getInfo(context, slSelects);
					mpIpClass.put(COMMONCOLUMNS, mpCommonColumnsDetail);
					/*Needs Exception for this get Related as there are multiple Objects which has multiple Sub Class Connected*/
					parentClasses = doIpClass.getRelatedObjects(context, //context
							LibraryCentralConstants.RELATIONSHIP_SUBCLASS, //rel pattern
							DomainConstants.QUERY_WILDCARD, //type pattern
							selectStmts, // object select
							(StringList)null, // rel select
							true, //get To
							false, //get From
							(short)0, //recurse level
							(String)null, // obj where clause
							(String)null, //rel where clause
							0);//limit
					parentClasses = parentClasses.sortStructure(context, STRFINALLEVEL, DESCENDING, EMXSORTNUMERICALPHASMALLERBASE);
	                itr2 = parentClasses.iterator();
	                while(itr2.hasNext()) {
	                    parentClassMap = (Map)itr2.next();
	                    parentclassName = (String)parentClassMap.get(DomainConstants.SELECT_NAME);
	                    elemHtmlList.add(parentclassName);
	                }
	                parentClassTitle = (String)mpIpClass.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
	                elemHtmlList.add(parentClassTitle);
					
					mpIpClass.put(CLASSIFICATION, com.matrixone.apps.domain.util.StringUtil.join(elemHtmlList, "->"));
					
					mlIpClassAll.add(mpIpClass);
			}
				
			}
			if(!mlIpClassAll.isEmpty()) {				
			    Comparator<Map<String, String>> mapComparator = (final Map<String, String> o1, final Map<String, String> o2)->o1.get(DomainConstants.SELECT_ATTRIBUTE_TITLE).compareTo(o2.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
				Collections.sort(mlIpClassAll, mapComparator);
				
				
			}
		} catch (Exception e) {
			outLog.print("Exception in  getRelatedIpAndExportControl: "+e+"\n");
			outLog.flush();
		}
		return mlIpClassAll;
		
	}
	
	
	
	/**
	 * @param context
	 * @param workbook
	 * @param rowHeader
	 * @param cellStyle
	 * @param sheetTablesIPAndExport
	 * @param strHyperlink
	 */
	public void updateWorksheetTableIPAndExport(List<Object> lParamlist) {
		
		Context context = (Context) lParamlist.get(0);
		XSSFWorkbook workbook = (XSSFWorkbook) lParamlist.get(1);
		XSSFRow rowHeader = (XSSFRow) lParamlist.get(2);
		XSSFCellStyle cellStyle=(XSSFCellStyle) lParamlist.get(3);
		XSSFCellStyle cellStyleTablesIPAndExport = (XSSFCellStyle) lParamlist.get(4);
		MapList mlIpClassAll = (MapList) lParamlist.get(5);
		XSSFSheet sheetTablesIPAndExport = (XSSFSheet) lParamlist.get(6);
		String strHyperlink = (String) lParamlist.get(7);
		// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - Start
        XSSFCellStyle styleGlobal = (XSSFCellStyle) lParamlist.get(8);
        // Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - End
        
		try {
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.PartAndSpecReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			String sStringResourceFile=EMXCPNSTRINGRESOURCE; 
			int rowCount1 = 0;
			
			String sLanguage = context.getSession().getLanguage();
			String strTablesIPAndExportChangeAction= i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.ChangeAction",sStringResourceFile, sLanguage);
			String strTablesIPAndExportChangeActionReasonForChange=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.ChangeActionReasonForChange",sStringResourceFile, sLanguage);
			String strTablesIPAndExportNameOrNumber=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.NameOrNumber",sStringResourceFile, sLanguage);
			String strTablesIPAndExportTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.Title",sStringResourceFile, sLanguage);
			String strTablesIPAndExportType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.Type",sStringResourceFile, sLanguage);
			String strTablesIPAndExportRevision	= i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.Revision",sStringResourceFile, sLanguage);
			String strTablesIPAndExportState = i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.State",sStringResourceFile, sLanguage);
			
			
			String strTablesIPAndExportChildTitle = i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.IPAndExportControl.Column.Title",sStringResourceFile, sLanguage);
			String strTablesIPAndExportChildName = i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.IPAndExportControl.Column.Name",sStringResourceFile, sLanguage);
			String strTablesIPAndExportChildType = i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.IPAndExportControl.Column.Type",sStringResourceFile, sLanguage);
			String strTablesIPAndExportChildDescription = i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.IPAndExportControl.Column.Description",sStringResourceFile, sLanguage);
			String strTablesIPAndExportChildState = i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.IPAndExportControl.Column.State",sStringResourceFile, sLanguage);
			String strTablesIPAndExportChildClassificationPath = i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.IPAndExportControl.Column.ClassificationPath",sStringResourceFile, sLanguage);


			//Creating Header ---Begin
			//1 Change Action
			Cell cellChangeAction = rowHeader.createCell(0);
			cellChangeAction.setCellStyle(cellStyle);
			cellChangeAction.setCellValue(strTablesIPAndExportChangeAction);

			//2 Change Action 'Reason for Change'
			Cell cellChangeActionReasonForChange = rowHeader.createCell(1);
			cellChangeActionReasonForChange.setCellStyle(cellStyle);
			cellChangeActionReasonForChange.setCellValue(strTablesIPAndExportChangeActionReasonForChange);

			//3 Name/Number
			Cell cellNameOrNumber = rowHeader.createCell(2);
			cellNameOrNumber.setCellStyle(cellStyle);
			cellNameOrNumber.setCellValue(strTablesIPAndExportNameOrNumber);

			//4 Title
			Cell cellTitle = rowHeader.createCell(3);
			cellTitle.setCellStyle(cellStyle);
			cellTitle.setCellValue(strTablesIPAndExportTitle);			

			//5 Type
			Cell cellType = rowHeader.createCell(4);
			cellType.setCellStyle(cellStyle);
			cellType.setCellValue(strTablesIPAndExportType);	

			//6 Revision
			Cell cellRevision = rowHeader.createCell(5);
			cellRevision.setCellStyle(cellStyle);
			cellRevision.setCellValue(strTablesIPAndExportRevision);

			//7 State
			Cell cellState = rowHeader.createCell(6);
			cellState.setCellStyle(cellStyle);
			cellState.setCellValue(strTablesIPAndExportState);
			
			//8 ChildTitle
			Cell cellChildTitle = rowHeader.createCell(7);
			cellChildTitle.setCellStyle(cellStyleTablesIPAndExport);
			cellChildTitle.setCellValue(strTablesIPAndExportChildTitle);
			
			//9 ChildName
			Cell cellChildName = rowHeader.createCell(8);
			cellChildName.setCellStyle(cellStyleTablesIPAndExport);
			cellChildName.setCellValue(strTablesIPAndExportChildName);
			
			//10 ChildType
			Cell cellChildType = rowHeader.createCell(9);
			cellChildType.setCellStyle(cellStyleTablesIPAndExport);
			cellChildType.setCellValue(strTablesIPAndExportChildType);
			
			//11 ChildDescription
			Cell cellChildDescription = rowHeader.createCell(10);
			cellChildDescription.setCellStyle(cellStyleTablesIPAndExport);
			cellChildDescription.setCellValue(strTablesIPAndExportChildDescription);
			
			//12 ChildState
			Cell cellChildState = rowHeader.createCell(11);
			cellChildState.setCellStyle(cellStyleTablesIPAndExport);
			cellChildState.setCellValue(strTablesIPAndExportChildState);
			
			//13 ChildClassificationPath
			Cell cellChildClassificationPath = rowHeader.createCell(12);
			cellChildClassificationPath.setCellStyle(cellStyleTablesIPAndExport);
			cellChildClassificationPath.setCellValue(strTablesIPAndExportChildClassificationPath);
			
			int iListSize=mlIpClassAll.size();
			pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
			for (int i=0;i<iListSize;i++){
				rowCount1 = rowCount1+1;
				XSSFRow row = sheetTablesIPAndExport.createRow(rowCount1);
				int columnCount1 = 0;
				Map mpInnerData = (Map)mlIpClassAll.get(i);
				HashMap hm= new HashMap<>();
				//Need Exception - not Assigned to null to avoid functionality breakage because of null value
				Map hmCommonCol = new HashMap<>();
				hmCommonCol= (Map)mpInnerData.get(COMMONCOLUMNS);
				String strTypeDisplayName=null;
				String strTypeName = (String)hmCommonCol.get("type");
				strTypeDisplayName= pgFamilyCare.getTypeDisplayName(context,strTypeName);
				String strCurrent = (String)hmCommonCol.get("current");
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				String sCAID = (String) hmCommonCol.get(SCAID);
				if(UIUtil.isNotNullAndNotEmpty(sCAID) && !sCAID.contains(HYPERLINK_PIPE)) {
					if(hmCommonCol.get(CAREALIZEDNAME) != null && hmCommonCol.get(CAPROPOSEDNAME) != null) {
						hm.put(0, HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
					} else if(hmCommonCol.get(CAREALIZEDNAME) != null){
						hm.put(0, HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
					} else if(hmCommonCol.get(CAPROPOSEDNAME) != null){
						hm.put(0, HYPERLINK+hmCommonCol.get(CAPROPOSEDNAME)+HYPERLINK_PIPE+sCAID);
					} else {
						hm.put(0, DomainConstants.EMPTY_STRING);
					}	
				}else{
					hm.put(0,hmCommonCol.get(CAPROPOSEDNAME));
				}
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				hm.put(1, hmCommonCol.get("attribute[Reason for Change]"));
				hm.put(2, HYPERLINK+hmCommonCol.get("name")+HYPERLINK_PIPE+hmCommonCol.get("id"));
				hm.put(3, hmCommonCol.get("attribute[Title]"));			
				hm.put(4, strTypeDisplayName);
				hm.put(5, hmCommonCol.get("revision"));
				hm.put(6, hmCommonCol.get("current"));
				hm.put(7,  mpInnerData.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));			
				hm.put(8,  mpInnerData.get(DomainConstants.SELECT_NAME));
				hm.put(9,  mpInnerData.get(DomainConstants.SELECT_TYPE));
				hm.put(10, mpInnerData.get(DomainConstants.SELECT_DESCRIPTION));
				hm.put(11, mpInnerData.get(DomainConstants.SELECT_CURRENT));
				hm.put(12, mpInnerData.get(CLASSIFICATION));
				XSSFCellStyle style = workbook.createCellStyle();
				style = pgFamilyCare.setForeGroundColor(strCurrent, style);
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				XSSFCellStyle style1 = workbook.createCellStyle();
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				for(int j=0;j<hm.size();j++){
					//Modified the code for _2022x_Aug CW Req-47097 - Start
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - Start
					Cell cell = row.createCell(columnCount1++);
					if (j == 2) {
						style = pgFamilyCare.makeColumnNumeric(workbook, style);
					}
					cell.setCellStyle(style);
					
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - End
					//Modified the code for _2022x_Aug CW Req-47097 - End
					String strCellValue = (String)hm.get(j);
					if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
						String[] strSplittedValue = strCellValue.split("\\|", -1); 
						String strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
						String strValue = strSplittedValue[(strSplittedValue.length)-2];
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && rowCount1 < iHyperLinkLimit){
							pgFamilyCare.getHyperlink(context,cell,workbook,strValue,strHyperlinkId,strCurrent);
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)){					
							cell.setCellValue(strValue);
						}
					}else {
						cell.setCellValue((String)hm.get(j));
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
						if(j == 0) {
							style1.cloneStyleFrom(style);
							style1.setWrapText(true);
							cell.setCellStyle(style1);
						}
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
					}
				}
			}
			pgFamilyCare.sheetFormatter(sheetTablesIPAndExport); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
		}catch (Exception e) {			
			outLog.print("Exception in  updateWorksheetTableIPAndExport: "+e+"\n");
			outLog.flush();
		}
		
		
	}
	//Added code for Requirement id 46223 - IP & Export Control Tab in Part & Spec Reports - Ends	
	
	/** Added the code for May CW 2022x.02 Defect 51675 - Starts
	 * @param context
	 * @param dobjPart
	 * @return
	 */
	public boolean checkAccessforMATCOMEBPUser(Context context, DomainObject dobjPart) {
		boolean hasAccess = false;
		StringList slHiddenTypeslist =null;
		String strSymbolicTypeName="";
		 StringList slPlantList = new StringList();
		 boolean isCtxtPushed = false;
		 
		 StringList slSelectList = new StringList();
		 slSelectList.add(DomainConstants.SELECT_NAME);
	      slSelectList.add(DomainConstants.SELECT_ID);
	      slSelectList.add(DomainConstants.SELECT_OWNER);
	      
	      
		
		try {
		Map mpObjectData =	dobjPart.getInfo(context, slSelectList);
		String strObjectId=(String)mpObjectData.get(DomainConstants.SELECT_ID);
		String strObjectType =(String)mpObjectData.get(DomainConstants.SELECT_TYPE);
		String strOwner = (String)mpObjectData.get(DomainConstants.SELECT_OWNER);
		String strHiddenTypes  = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(), "emxCPN.Command.attribAuthorizedToUse.AllowedTypeList");
		slHiddenTypeslist = com.matrixone.apps.domain.util.StringUtil.split(strHiddenTypes, pgV3Constants.SYMBOL_COMMA);
	    strSymbolicTypeName = FrameworkUtil.getAliasForAdmin(context, "type", strObjectType, true);
	    if(UIUtil.isNotNullAndNotEmpty(strObjectId)) {
		 if (PersonUtil.hasAssignment(context, pgV3Constants.ROLE_PGCONTRACTMANUFACTURER) && slHiddenTypeslist.contains(strSymbolicTypeName)) {
	          DomainObject domCtxtPersonObj = PersonUtil.getPersonObject(context);
	          MapList mlPlant = domCtxtPersonObj.getRelatedObjects(
	        		  context, //Context
	        		  pgV3Constants.RELATIONSHIP_MEMBER, //Relationship
	        		  pgV3Constants.TYPE_PLANT, //type
	        		  slSelectList, //object selects
	        		  null, //rel selects 
	        		  true, //to type
	        		  false, // from type
	        		  (short)1, //expand level
	        		  null, //object where
	        		  null, //relationship where
	        		  0);//limit
	          
	          for (Object obj : mlPlant) {
	              Map map = (Map)obj;
	              slPlantList.add((String)map.get(DomainConstants.SELECT_ID));
	            }    
	          
	          if (pgV3Constants.TYPE_INTERNAL_MATERIAL.equals(strObjectType)) {
	        	  MapList mlConnectedTypeList = dobjPart.getRelatedObjects(context, pgV3Constants.REL_COMPONENT_MATERIAL, pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART +pgV3Constants.SYMBOL_COMMA+  pgV3Constants.TYPE_DEVICEPRODUCTPART + pgV3Constants.SYMBOL_COMMA+ pgV3Constants.TYPE_FORMULATIONPART, slSelectList, null, true, false, (short)1, null, null,0);
	        	  if (!mlConnectedTypeList.isEmpty()) {
	                StringBuilder sbValuelist = new StringBuilder();
	                Iterator<Map> mapItr = mlConnectedTypeList.iterator();
	                try {
	                	//Pushing the User Agent to check the Authorized to Produce plants 
	                  ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"), "", "");
	                  isCtxtPushed = true;
	                  while (mapItr.hasNext()) {
	                    Map map = mapItr.next();
	                    dobjPart.setId((String)map.get(DomainConstants.SELECT_ID));
	                    sbValuelist.append(hasOnlyAuthorisedTOProducePlants(context, dobjPart, slSelectList, slPlantList, strObjectType));
	                    sbValuelist.append(pgV3Constants.SYMBOL_COMMA);
	                  } 
	                } finally {
	                  if (isCtxtPushed) {
	                	  ContextUtil.popContext(context); 
	                  }
	                    
	                } 
	                if (!sbValuelist.toString().contains(pgV3Constants.FALSE)) {
	                	hasAccess = true;
	                }
	                	 
	              } else {
	            	  hasAccess = true;
	              } 
	          } else {
	        	  hasAccess = true;
	              String strCtxtUser = context.getUser();
	              boolean isOwner = strCtxtUser.equalsIgnoreCase(strOwner);
	              if (!isOwner) {
	            	  hasAccess = hasOnlyAuthorisedTOProducePlants(context, dobjPart, slSelectList, slPlantList, strObjectType);  
	              }
	            	  
	          }
	          if (!hasAccess && (
	                  pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equalsIgnoreCase(strObjectType))) {
	                  String strPersonObjectId = PersonUtil.getPersonObjectID(context, context.getUser());
	                  hasAccess = isUserHiRCompliant(context, strPersonObjectId);
	                } 
	              } else {
	            	  hasAccess = true;
	              } 
			}
			
		
		}catch(Exception e) {
			outLog.print("Exception in checkAccessforMATCOMEBPUser method"+e+"\n");
			outLog.flush();
		}
		return hasAccess;
		
	}
	
	/**
	 * @param context
	 * @param dobjPart
	 * @param slSelectList
	 * @param slPlantList
	 * @param strObjectType
	 * @return
	 */
	public boolean hasOnlyAuthorisedTOProducePlants(Context context, DomainObject dobjPart, StringList slSelectList, StringList slPlantList, String strObjectType) {
		boolean hasAccess = false;
	    String strAuthorizedToProducePlants = "";
	    String strPlantId = "";
	    StringList slRelSelect = new StringList();
	    slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
	    try {
	      if (pgV3Constants.TYPE_FORMULATIONPROCESS.equals(strObjectType)) {
	        String strFormulationPart = dobjPart.getInfo(context, "to[" + pgV3Constants.RELATIONSHIP_PLANNEDFOR + "].from.id");
	        if (UIUtil.isNotNullAndNotEmpty(strFormulationPart)) {
	        	dobjPart.setId(strFormulationPart); 
	        }
	        	
	      } 
	      MapList mlPlantsList = dobjPart.getRelatedObjects(
	    		  context, //context
	    		  RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, //relationship
	    		  pgV3Constants.TYPE_PLANT,//types
	    		  slSelectList, //object selects
	    		  slRelSelect, //rel selects
	    		  true, //to type
	    		  false, //from type
	    		  (short)2, //expansion level
	    		  null, //object where
	    		  null,//relationship where
	    		  0);//limit
	      if (!mlPlantsList.isEmpty()) {
	        Iterator<Map> mapItr = mlPlantsList.iterator();
	        while (mapItr.hasNext()) {
	          Map map1 = mapItr.next();
	          strPlantId = (String)map1.get(DomainConstants.SELECT_ID);
	          strAuthorizedToProducePlants = (String)map1.get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
	          if (slPlantList.contains(strPlantId) && "TRUE".equals(strAuthorizedToProducePlants)) {
	        	  hasAccess = true;
	            break;
	          } 
	        } 
	      } 
	    } catch (Exception e) {
	      outLog.print("Exception in hasOnlyAuthorisedTOProducePlants method"+e+"\n");
	      outLog.flush();
	    } 
	    return hasAccess;
	}
	
	/**
	 * @param context
	 * @param strPersonObjectId
	 * @return
	 */
	public boolean isUserHiRCompliant(Context context, String strPersonObjectId) {
		boolean hasAccess = false;
		
		try {
		      DomainObject doPersonObject = DomainObject.newInstance(context, strPersonObjectId);
		      StringList slObjSelects = new StringList(1);
		      slObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGHIRCOMPLAINT);
		      MapList mlCompanies = doPersonObject.getRelatedObjects(
		    		  context, //context
		    		  pgV3Constants.RELATIONSHIP_MEMBER, //relationship
		    		  DomainObject.TYPE_COMPANY, //type
		    		  slObjSelects, //object selects
		    		  null, //rel selects
		    		  true, //to type
		    		  false, //from type
		    		  (short)1, //expand level
		    		  null, //object where
		    		  null, //relationship where
		    		  0);//limit
		      
		      String strHiRCompany = "";
		      Map<Object, Object> mpCompany = null;
		      if (mlCompanies != null && !mlCompanies.isEmpty()) {
		        for (int i = 0; i < mlCompanies.size(); i++) {
		          mpCompany = (Map<Object, Object>)mlCompanies.get(i);
		          strHiRCompany = (String)mpCompany.get(pgV3Constants.SELECT_ATTRIBUTE_PGHIRCOMPLAINT);
		          if (UIUtil.isNotNullAndNotEmpty(strHiRCompany) && pgV3Constants.TRUE.equalsIgnoreCase(strHiRCompany)) {
		        	  return true;  
		          }
		           
		        }  
		      }
		    } catch (Exception ex) {
		    	outLog.print("Exception in isUserHiRCompliant method"+ex+"\n");
			      outLog.flush();
		    } 
		return hasAccess;
	}
	
	//Added the code for May CW 2022x.02 Defect 51675 - Ends
	
	
	
	/** Added the code for 22x.02 May CW Defect 52204 - Starts
	 * @param context
	 * @param strPartFamilyId
	 * @return
	 * @throws Exception
	 */
	public StringList getPartFamilyListFromHeirarchy(Context context, String strPartFamilyId){
			StringList partFamilyisList = new StringList();
			Map partFamilyMap = null;
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_NAME);
			objectSelects.add(DomainConstants.SELECT_ID);
			try {
			if (UIUtil.isNotNullAndNotEmpty(strPartFamilyId)) {
				DomainObject doPartFamily = DomainObject.newInstance(context, strPartFamilyId);
				MapList partFamilyMapList = doPartFamily.getRelatedObjects(
							context, 
							pgV3Constants.RELATIONSHIP_SUBCLASS, 
							pgV3Constants.TYPE_PARTFAMILY, 
							objectSelects, 
							null, 
							true,
							false, 
							(short)0, 
							null,
							null,
							0);
					if (!partFamilyMapList.isEmpty()) {
						Iterator<Map> itr = partFamilyMapList.iterator();
						while (itr.hasNext()) {
							partFamilyMap = itr.next();
							partFamilyisList.add((String) partFamilyMap.get("id"));
						}
					}
				} 
			}catch (Exception e) {
				outLog.print("Exception in getPartFamilyListFromHeirarchy Method"+e+"\n");
				outLog.flush();
				}
			return partFamilyisList;
		}
	
	/**
	 * @param context
	 * @param strConnectedMasterPartId
	 * @param slPartFamilyList
	 * @return
	 */
	public Map getConnectedMasterSpec(Context context, String strConnectedMasterPartId, StringList slPartFamilyList) {
		Map mpMasterSpecData = new HashMap<String,String>();
		try {
			
			String strMastSpecIds = "";
		if(!slPartFamilyList.isEmpty()) {
			
			StringList slobjSelect = new StringList();
			slobjSelect.add(DomainConstants.SELECT_TYPE);
			slobjSelect.add(DomainConstants.SELECT_NAME);
			slobjSelect.add(DomainConstants.SELECT_ID);
			String strPartSpecExpr = "from["+pgV3Constants.RELATIONSHIP_PARTSPECIFICATION+"].to.id";
			slobjSelect.add(strPartSpecExpr);
			
			String strWhere = "attribute["+pgV3Constants.ATTR_REFERENCE_TYPE+"] == M";
			
			for(int i=0;i<slPartFamilyList.size();i++) {
				DomainObject dobj = DomainObject.newInstance(context, slPartFamilyList.get(i));
				
				//Need Exception, we need to pull the Master parts connected to Part Family Hierarchy
				MapList mlMasterParts = dobj.getRelatedObjects(
						context, //Context
						pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM, //Relationship
						pgV3Constants.TYPE_PART,//Type
						slobjSelect,//Object Selects
						null,//rel selects
						false,//to connects
						true,//from connects
						(short)0,//recurse to level
						strWhere,//object Where
						null,//rel Where
						0);//limit
				
				for(int j=0;j<mlMasterParts.size();j++){
					Map mp = (Map)mlMasterParts.get(j);
					if(!strConnectedMasterPartId.equals(mp.get(DomainConstants.SELECT_ID)) && null != mp.get(strPartSpecExpr)) {
					
					
						if(mp.get(strPartSpecExpr) instanceof StringList) {
							strMastSpecIds = mp.get(strPartSpecExpr).toString().replace("[", "").replace("]", "");
						} else {
							strMastSpecIds = mp.get(strPartSpecExpr).toString();
						}
					mpMasterSpecData.put(mp.get(DomainConstants.SELECT_NAME)+"|"+mp.get(DomainConstants.SELECT_ID),strMastSpecIds);
						
					
					}			
					
				
				}
				
			}
	}
		
		
		}catch(Exception e) {
			outLog.print("Exception in getConnectedMasterSpec Method"+e+"\n");
				outLog.flush();
		}
		return mpMasterSpecData;
		
	}
	
	/**
	 * @param context
	 * @param strSpecId
	 * @param mpp
	 * @return
	 */
	public String getParentMasterfromSpec(String strSpecId, Map<String, String> mpp) {
		StringBuilder sb = new StringBuilder();
		try {
		for(Map.Entry entry: mpp.entrySet()){
			if((entry.getValue()).toString().contains(strSpecId)) {
				String[] strMasterobjDatta = (entry.getKey()).toString().split("\\|");
				if(sb.length()>0) {
					sb.append(pgV3Constants.SYMBOL_PIPE);
					sb.append(strMasterobjDatta[0]);
				}else {
					sb.append(strMasterobjDatta[0]);
				}
			}
			
		}
		}catch(Exception e) {
			outLog.print("Exception in getParentMasterfromSpec Method"+e+"\n");
				outLog.flush();
			}
		return sb.toString();
	}
	
	
	/**
	 * @param context
	 * @param strMasterSpecId
	 * @param mpConnectedMasterSpecData
	 * @param strUserName
	 * @param mpCommonColumnsDetail
	 */
	public void setMasterSpecificationData(Context context, String strMasterSpecId, Map mpConnectedMasterSpecData, String strUserName, Map mpCommonColumnsDetail, MapList mlMasterSpecInfoAll) {
		StringList slSelects = new StringList();
		slSelects.add(DomainConstants.SELECT_ID);
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		DomainObject doMasterSpecDoObj = null;
		Map mlMasterSpecInfo = new HashMap<>();
		boolean hasAccess = false;
		pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
		
		try {
			doMasterSpecDoObj = DomainObject.newInstance(context,strMasterSpecId);
			mlMasterSpecInfo=doMasterSpecDoObj.getInfo(context, slSelects);
			String strParentMastername = getParentMasterfromSpec(strMasterSpecId, mpConnectedMasterSpecData);
				hasAccess = pgFamilyCare.accessCheck(context,strUserName,strMasterSpecId);
				if(hasAccess) {
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONNAME, mlMasterSpecInfo.get(DomainConstants.SELECT_NAME));
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONID, strMasterSpecId);
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONTYPE, mlMasterSpecInfo.get(DomainConstants.SELECT_TYPE));
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONSTATE, mlMasterSpecInfo.get(DomainConstants.SELECT_CURRENT));
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONTITLE, mlMasterSpecInfo.get(DomainConstants.SELECT_ATTRIBUTE_TITLE));
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONSPECIFICATIONSUBTYPE, mlMasterSpecInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE));
					mlMasterSpecInfo.put(STR_MASTERPARTNAME, strParentMastername);
					
					
				}else {
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONNAME, mlMasterSpecInfo.get(DomainConstants.SELECT_NAME));
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONID, CONST_NO_ACCESS);
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONTYPE, mlMasterSpecInfo.get(DomainConstants.SELECT_TYPE));
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONSTATE, CONST_NO_ACCESS);
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONTITLE, CONST_NO_ACCESS);
					mlMasterSpecInfo.put(STR_MASTERSPECIFICATIONSPECIFICATIONSUBTYPE, CONST_NO_ACCESS);
					mlMasterSpecInfo.put(STR_MASTERPARTNAME, CONST_NO_ACCESS);
					
					
				}
				//Added the code for 22x.02 May CW Defect 52204 - Ends
				mlMasterSpecInfo.put(COMMONCOLUMNS, mpCommonColumnsDetail);
				mlMasterSpecInfoAll.add(mlMasterSpecInfo);
			
			
		}catch(Exception e) {
			outLog.print("Exception in setMasterSpecificationData method"+e+"\n");
			outLog.flush();
		}
	}
	//Added the code for 22x.02 May CW Defect 52204 - Ends
	
	public void updateWorksheetSpecsDocs(Context context, XSSFWorkbook workbook,XSSFRow rowHeader,XSSFCellStyle cellStyle, MapList mlSpecsDocsAll,XSSFSheet sheetSpecsDocs,MapList mlSpecsRefDocsAll,MapList mlPerformanceCharacteristicsAll,MapList mlMasterSpecAll,XSSFCellStyle cellStyleRelatedSpecification,XSSFCellStyle cellStyleReferenceDocument,XSSFCellStyle cellStyleCharReferenceDocument,XSSFCellStyle cellStyleMasterSpecification, String strHyperlink, XSSFCellStyle styleGlobal, String strGlobalOriginatingSource) throws FrameworkException {
		//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 54222 - Starts
		boolean isContextPushed = false;
		//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 54222 - Ends
		try {
			//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Starts
			String strHyperlinkLimit = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.PartAndSpecReport.HyperlinkLimit");
			int iHyperLinkLimit = Integer.parseInt(strHyperlinkLimit);
			int iRowCountAll=0;
			pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
			StringList SL_OBJECT_TM_SELECT=pgFamilyCare.getObjectSelectsTM();
			//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Ends
			String sStringResourceFile="emxCPNStringResource"; 
			String sLanguage = context.getSession().getLanguage();
			String strSpecsDocsChangeAction= i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.ChangeAction",sStringResourceFile, sLanguage);
			String strSpecsDocsReasonForChange=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.ChangeActionReasonForChange",sStringResourceFile, sLanguage);
			String strSpecsDocsNameOrNumber=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.NameOrNumber",sStringResourceFile, sLanguage);
			String strSpecsDocsTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.Title",sStringResourceFile, sLanguage);
			String strSpecsDocsType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.Type",sStringResourceFile, sLanguage);
			//Code for requirement to addition of revision and state on all tabs except attribute - Starts
			String strSpecsDocsCellRevision	= i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.Revision",sStringResourceFile, sLanguage);
			String strSpecsDocsCellState	= i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.Column.State",sStringResourceFile, sLanguage);
			//Code for requirement to addition of revision and state on all tabs except attribute - Ends
			String strSpecsDocsName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.Name",sStringResourceFile, sLanguage);
			String strSpecsDocsSource=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.Source",sStringResourceFile, sLanguage);
			String strSpecsDocsSpecificationSubType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.SpecificationSubType",sStringResourceFile, sLanguage);
			String strSpecsDocsState=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.State",sStringResourceFile, sLanguage);
			String strSpecsDocsOriginator=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.Originator",sStringResourceFile, sLanguage);
			String strSpecsDocsRevision=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.Revision",sStringResourceFile, sLanguage);
			String strSpecsDocsDescription=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.Description",sStringResourceFile, sLanguage);
			String strSpecsDocsLanguage=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.Language",sStringResourceFile, sLanguage);
			String strSpecsDocsRelatedSpecs=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecsHeader",sStringResourceFile, sLanguage);
			String strSpecsDocsReferenceDoc=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDocHeader",sStringResourceFile, sLanguage);
			String strSpecsDocsVersion=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.Version",sStringResourceFile, sLanguage);
			String strRelatedSpecsName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecs.Name",sStringResourceFile, sLanguage);
			String strReferenceDocsName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDocName.Name",sStringResourceFile, sLanguage);
			String strCharacteristicRefDocName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.CharacteristicReferenceDocs.Name",sStringResourceFile, sLanguage);
			String strSpecsDocsCharacteristicsRefHeader=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.CharacteristicsReferenceHeader",sStringResourceFile, sLanguage);
			String strRelatedSpecsTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecs.Title",sStringResourceFile, sLanguage);
			String strRelatedSpecsSource=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecs.Source",sStringResourceFile, sLanguage);
			String strRelatedSpecsType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecs.Type",sStringResourceFile, sLanguage);
			String strRelatedSpecsSubType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecs.SpecificationSubType",sStringResourceFile, sLanguage);
			String strRelatedSpecsState=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecs.State",sStringResourceFile, sLanguage);
			String strRelatedSpecsOriginator=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.RelatedSpecs.Originator",sStringResourceFile, sLanguage);
			String strRelatedSpecsArtworkPrimary="Related Specification - Artwork Primary";
			String strReferenceDocLanguage=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.Language",sStringResourceFile, sLanguage);
			String strReferenceDocSource=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.Source",sStringResourceFile, sLanguage);
			String strReferenceDocTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.Title",sStringResourceFile, sLanguage);
			String strReferenceDocRevision=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.Revision",sStringResourceFile, sLanguage);
			String strReferenceDocVersion=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.Version",sStringResourceFile, sLanguage);
			String strReferenceDocType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.Type",sStringResourceFile, sLanguage);
			String strReferenceDocDescription=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.Description",sStringResourceFile, sLanguage);
			String strReferenceDocState=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReferenceDoc.State",sStringResourceFile, sLanguage);
			String strCharReferenceDocTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.CharacteristicReferenceDoc.Title",sStringResourceFile, sLanguage);
			String strCharReferenceDocRevision=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.CharacteristicReferenceDoc.Revision",sStringResourceFile, sLanguage);
			String strCharReferenceDocType=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.CharacteristicReferenceDoc.Type",sStringResourceFile, sLanguage);
			String strCharReferenceDocDescription=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.CharacteristicReferenceDoc.Description",sStringResourceFile, sLanguage);
			String strCharReferenceDocState=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.CharacteristicReferenceDoc.State",sStringResourceFile, sLanguage);
			String strSpecsDocsDerivedFromName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.DerivedFromName",sStringResourceFile, sLanguage);
			String strSpecsDocsDerivedFromTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.DerivedFromTitle",sStringResourceFile, sLanguage);
			String strSpecsDocsPartFamilyName=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.PartFamilyName",sStringResourceFile, sLanguage);
			//Start Modified for 22x Changes for auto name of Part Library & Faimly
			String strSpecsDocsPartFamilyTitle=i18nNow.getI18nString("emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.PartFamilyTitle",sStringResourceFile, sLanguage);
			//End Modified for 22x Changes for auto name of Part Library & Faimly
			
			//Added in 30532 2018x.2 December since different types have File Name of Object name columns
			//Added code for 2018x.6 Requirement 36694 Add attribute Release Date in Part and Spec Reports Starts
			String strRelatedSpecReleaseDate = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(), "emxCPN.FamilyCareReport.Worksheet.SpecsDocs.Column.ReleatedSpecReleaseDate");
			//Added code for 2018x.6 Requirement 36694 Add attribute Release Date in Part and Spec Reports Ends
			String strReferenceDocFileName="Reference Document-FileName";
			String strIndividualTMName=null;
			//Header Logic Begin----------------------------------------
			//1 Change Action
			Cell cellChangeAction = rowHeader.createCell(0);
			cellChangeAction.setCellStyle(cellStyle);
			cellChangeAction.setCellValue(strSpecsDocsChangeAction);
			
			//2 Change Action 'Reason for Change'
			Cell cellChangeActionReasonForChange = rowHeader.createCell(1);
			cellChangeActionReasonForChange.setCellStyle(cellStyle);
			cellChangeActionReasonForChange.setCellValue(strSpecsDocsReasonForChange);
			
			//3 Name/Number
			Cell cellNameOrNumber = rowHeader.createCell(2);
			cellNameOrNumber.setCellStyle(cellStyle);
			cellNameOrNumber.setCellValue(strSpecsDocsNameOrNumber);
			
			//4 Title
			Cell cellTitle = rowHeader.createCell(3);
			cellTitle.setCellStyle(cellStyle);
			cellTitle.setCellValue(strSpecsDocsTitle);
			
			//5 Type
			Cell cellType = rowHeader.createCell(4);
			cellType.setCellStyle(cellStyle);
			cellType.setCellValue(strSpecsDocsType);
			
			//6 Revision
			//Code for requirement to addition of revision and state on all tabs except attribute -- Starts
			Cell cellRevision = rowHeader.createCell(5);
			cellRevision.setCellStyle(cellStyle);
			cellRevision.setCellValue(strSpecsDocsCellRevision);
			
			//7 State
			Cell cellState = rowHeader.createCell(6);
			cellState.setCellStyle(cellStyle);
			cellState.setCellValue(strSpecsDocsCellState);
			//Code for requirement to addition of revision and state on all tabs except attribute -- Ends
			
			//8 Doc Name
			Cell cellSpecsDocsName = rowHeader.createCell(7);
			cellSpecsDocsName.setCellStyle(cellStyleRelatedSpecification);
			cellSpecsDocsName.setCellValue(strRelatedSpecsName);
			
			//9 Doc Title
			Cell cellTitle1 = rowHeader.createCell(8);
			cellTitle1.setCellStyle(cellStyleRelatedSpecification);
			cellTitle1.setCellValue(strRelatedSpecsTitle);
			
			//10 Doc Source			
			Cell cellSpecsDocsSource = rowHeader.createCell(9);
			cellSpecsDocsSource.setCellStyle(cellStyleRelatedSpecification);
			cellSpecsDocsSource.setCellValue(strRelatedSpecsSource);
			
			//11 Doc Type
			Cell cellType1 = rowHeader.createCell(10);
			cellType1.setCellStyle(cellStyleRelatedSpecification);
			cellType1.setCellValue(strRelatedSpecsType);
			
			//12 Doc Sub Type
			Cell cellSpecsDocsSpecificationSubType = rowHeader.createCell(11);
			cellSpecsDocsSpecificationSubType.setCellStyle(cellStyleRelatedSpecification);
			cellSpecsDocsSpecificationSubType.setCellValue(strRelatedSpecsSubType);
			
			//13 Doc State
			Cell cellSpecsDocsState= rowHeader.createCell(12);
			cellSpecsDocsState.setCellStyle(cellStyleRelatedSpecification);
			cellSpecsDocsState.setCellValue(strRelatedSpecsState);
			
			//14 Doc Release Date
			Cell cellSpecsDocsReleaseDate = rowHeader.createCell(13);
			cellSpecsDocsReleaseDate.setCellStyle(cellStyleRelatedSpecification);
			cellSpecsDocsReleaseDate.setCellValue(strRelatedSpecReleaseDate);	
			
			//15 Doc Originator
			Cell cellSpecsDocsOriginator = rowHeader.createCell(14);
			cellSpecsDocsOriginator.setCellStyle(cellStyleRelatedSpecification);
			cellSpecsDocsOriginator.setCellValue(strRelatedSpecsOriginator);	
			
			//16 Doc Artwork Primary
			Cell cellSpecsDocsArtworkPrimary = rowHeader.createCell(15);
			cellSpecsDocsArtworkPrimary.setCellStyle(cellStyleRelatedSpecification);
			cellSpecsDocsArtworkPrimary.setCellValue(strRelatedSpecsArtworkPrimary);

			// Modified by IRM (Sogeti) for 2022x.03 August CW Requirement 47172 - Starts
			//23 Doc Name
			Cell cellRefDocsName = rowHeader.createCell(22);
			cellRefDocsName.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsName.setCellValue(strReferenceDocsName);
			
			//24 Doc Language
			Cell cellRefDocsFileName = rowHeader.createCell(23);
			cellRefDocsFileName.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsFileName.setCellValue(strReferenceDocFileName);
			
			//25 Doc Language
			Cell cellRefDocsLanguage = rowHeader.createCell(24);
			cellRefDocsLanguage.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsLanguage.setCellValue(strReferenceDocLanguage);

			//26 Doc Source
			Cell cellRefDocsSource= rowHeader.createCell(25);
			cellRefDocsSource.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsSource.setCellValue(strReferenceDocSource);		

			//27 Doc Title
			Cell cellRefDocsTitle = rowHeader.createCell(26);
			cellRefDocsTitle.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsTitle.setCellValue(strReferenceDocTitle);

			//28 Doc Revision
			Cell cellRefDocsRevision = rowHeader.createCell(27);
			cellRefDocsRevision.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsRevision.setCellValue(strReferenceDocRevision);
					
			//29 Doc Version
			Cell cellRefDocsVersion = rowHeader.createCell(28);
			cellRefDocsVersion.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsVersion.setCellValue(strReferenceDocVersion);
				
			//30 Doc Type
			Cell cellRefDocsType = rowHeader.createCell(29);
			cellRefDocsType.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsType.setCellValue(strReferenceDocType);

			//31 Doc Description
			Cell cellRefDocsDescription = rowHeader.createCell(30);
			cellRefDocsDescription.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsDescription.setCellValue(strReferenceDocDescription);
					
			//32 Doc State
			Cell cellRefDocsState = rowHeader.createCell(31);
			cellRefDocsState.setCellStyle(cellStyleReferenceDocument);
			cellRefDocsState.setCellValue(strReferenceDocState);

			//33 Doc Name
			Cell cellCharDocsName = rowHeader.createCell(32);
			cellCharDocsName.setCellStyle(cellStyleCharReferenceDocument);
			cellCharDocsName.setCellValue(strCharacteristicRefDocName);

			//34 Doc Title
			Cell cellCharDocsTitle = rowHeader.createCell(33);
			cellCharDocsTitle.setCellStyle(cellStyleCharReferenceDocument);
			cellCharDocsTitle.setCellValue(strCharReferenceDocTitle);

			// 35 Doc Revision
			Cell cellCharDocsRevision = rowHeader.createCell(34);
			cellCharDocsRevision.setCellStyle(cellStyleCharReferenceDocument);
			cellCharDocsRevision.setCellValue(strCharReferenceDocRevision);
					
			// 36 Doc Type
			Cell cellCharDocsType = rowHeader.createCell(35);
			cellCharDocsType.setCellStyle(cellStyleCharReferenceDocument);
			cellCharDocsType.setCellValue(strCharReferenceDocType);
				
			// 37 Doc Description
			Cell cellCharDocsDescription = rowHeader.createCell(36);
			cellCharDocsDescription.setCellStyle(cellStyleCharReferenceDocument);
			cellCharDocsDescription.setCellValue(strCharReferenceDocDescription);
				
			// 38 Doc State
			Cell cellCharDocsState = rowHeader.createCell(37);
			cellCharDocsState.setCellStyle(cellStyleCharReferenceDocument);
			cellCharDocsState.setCellValue(strCharReferenceDocState);
					
			// 39 Derived From Name
			Cell cellDerivedFromName = rowHeader.createCell(38);
			cellDerivedFromName.setCellStyle(cellStyleCharReferenceDocument);
			cellDerivedFromName.setCellValue(strSpecsDocsDerivedFromName);
				
			// 40 Derived from Title
			Cell cellDerivedFromTitle = rowHeader.createCell(39);
			cellDerivedFromTitle.setCellStyle(cellStyleCharReferenceDocument);
			cellDerivedFromTitle.setCellValue(strSpecsDocsDerivedFromTitle);
					
			// 41 Part Family Name
			Cell cellPartFamilyName = rowHeader.createCell(40);
			cellPartFamilyName.setCellStyle(cellStyleCharReferenceDocument);
			//Start Modified for 22x Changes for auto name of Part Library & Family
			cellPartFamilyName.setCellValue(strSpecsDocsPartFamilyTitle);
			//End Modified for 22x Changes for auto name of Part Library & Family
			// Modified by IRM (Sogeti) for 2022x.03 August CW Requirement 47172 - Ends
			int rowCount1 = 0;
			int iListSize=mlSpecsDocsAll.size();
			String strChangeAction=null;
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());	
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			//Added code for defect 2018x6 Sept 42777 Starts
			String strRefType = DomainConstants.EMPTY_STRING;
			//Added code for defect 2018x6 Sept 42777 Ends
			//Added code for defect 2018x6 Sept 42743, 42740 Starts
			MapList mlCSSData = new MapList();
			Map<String, Object> hmArgs = new HashMap<>();
			String strOriginatingSource =  DomainConstants.EMPTY_STRING;
			Map<String,Object> mpData = new HashMap<>();
			Vector<Object> vcSpecSubType = new Vector<>();
			Vector<Object> vcType = new Vector<>();
			String strSpecSubType = DomainConstants.EMPTY_STRING;
			String strDisplayNameChild = DomainConstants.EMPTY_STRING;
			//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Starts 
			List lParamList = new ArrayList<>();
			lParamList.add(context);
			lParamList.add(rowHeader);
			lParamList.add(cellStyleMasterSpecification);
			lParamList.add(mlMasterSpecAll);
			lParamList.add(rowCount1);
			lParamList.add(sheetSpecsDocs);
			lParamList.add(workbook);
			lParamList.add(strHyperlink);
			lParamList.add(style);

			rowCount1 = updateExcelMasterSpec(lParamList);
			
			//Added code for Requirement Id:46224 - Master Specification info addition to Part & Spec report - Ends 
			//Added code for defect 2018x6 Sept 42743, 42740 Ends
			for (int i=0;i<iListSize;i++){
				//Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 49288 - Starts
				boolean hasAccess = false;
				//Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 49288 - Ends
				//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Starts
				iRowCountAll = iRowCountAll + 1;
				//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Ends
				XSSFRow row = sheetSpecsDocs.createRow(++rowCount1);
				int columnCount1 = 0;
				Map mpInnerData = (Map)mlSpecsDocsAll.get(i);
				HashMap hm= new HashMap<>();
				// CommonColumns Begin		
				Map hmCommonCol = new HashMap<>();
				hmCommonCol= (Map)mpInnerData.get("CommonColumns");
				// CommonColumns End	
				//Fetching the Types Display name --Begin
				String strTypeDisplayName=null;
				String strTypeName = (String)hmCommonCol.get("type");
				strTypeDisplayName= pgFamilyCare.getTypeDisplayName(context,strTypeName);
				//Fetching the Types Display name --End
				String strCurrent = (String)hmCommonCol.get("current");
				String strId = (String)hmCommonCol.get(DomainConstants.SELECT_ID);
						
				//if(strCurrent.equalsIgnoreCase("Release") || strCurrent.equalsIgnoreCase("Released")){
				//Added the code for the Defect ID : 25104 - CA Tab is Empty : Starts
				//Code Upgrade for 2018x Data Model - Starts
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				String sCAID = (String) hmCommonCol.get(SCAID);
				if(UIUtil.isNotNullAndNotEmpty(sCAID) && !sCAID.contains(HYPERLINK_PIPE)) {
					if(hmCommonCol.get(CAREALIZEDNAME) != null && hmCommonCol.get(CAPROPOSEDNAME) != null) {
						//Updated code for Req Id : 33634 - Hyperlinks--Starts
						hm.put(0, HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
						//Updated code for Req Id : 33634 - Hyperlinks--Ends
					} else if(hmCommonCol.get(CAREALIZEDNAME) != null){
						//Updated code for Req Id : 33634 - Hyperlinks--Starts
						hm.put(0, HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
						//Updated code for Req Id : 33634 - Hyperlinks--Ends
					} else if(hmCommonCol.get(CAPROPOSEDNAME) != null){
						//Updated code for Req Id : 33634 - Hyperlinks--Starts
						hm.put(0, HYPERLINK+hmCommonCol.get(CAPROPOSEDNAME)+HYPERLINK_PIPE+sCAID);
						//Updated code for Req Id : 33634 - Hyperlinks--Ends
					} else {
						hm.put(0, DomainConstants.EMPTY_STRING);
					}	
				}else {
					hm.put(0,hmCommonCol.get(CAPROPOSEDNAME));
				}
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				
				String strRefDocArtworkPrimary=	DomainConstants.EMPTY_STRING;
				strRefDocArtworkPrimary = (String)mpInnerData.get("attribute[pgArtworkPrimary]");
				if(UIUtil.isNotNullAndNotEmpty(strRefDocArtworkPrimary)){
					if(strRefDocArtworkPrimary.equalsIgnoreCase("TRUE")){
						strRefDocArtworkPrimary = "Yes";
					}
					else if(strRefDocArtworkPrimary.equalsIgnoreCase("FALSE")){
						strRefDocArtworkPrimary = "No";
					}
				}
				//Code Upgrade for 2018x Data Model - Ends	
				//Added the code for the Defect ID : 25104 - CA Tab is Empty : Ends
				hm.put(1, hmCommonCol.get("attribute[Reason for Change]"));
				//Updated code for Req Id : 33634 - Hyperlinks--Starts
				hm.put(2, HYPERLINK+hmCommonCol.get("name")+HYPERLINK_PIPE+hmCommonCol.get("id"));
				//Updated code for Req Id : 33634 - Hyperlinks--Ends
				hm.put(3, hmCommonCol.get("attribute[Title]"));
				hm.put(4, strTypeDisplayName);
				//Code for requirement to addition of revision and state on all tabs except attribute -- Starts
				hm.put(5, hmCommonCol.get("revision"));
				hm.put(6, hmCommonCol.get("current"));
				//Code for requirement to addition of revision and state on all tabs except attribute -- Ends
				//Updated code for Req Id : 33634 - Hyperlinks--Starts
				hm.put(7, HYPERLINK+mpInnerData.get("name")+HYPERLINK_PIPE+mpInnerData.get("id"));
				//Updated code for Req Id : 33634 - Hyperlinks--Ends
				// path needs to be added.
				//Updated code for defect 2018x6 Sept 42743, 42740 Starts
				//strTypeDisplayName=getTypeDisplayName(context,(String)mpInnerData.get("type"));
				strOriginatingSource =  (String) hmCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
				if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && strOriginatingSource.contains("CSS")) {
					mpData.put(DomainConstants.SELECT_TYPE,(String)mpInnerData.get(DomainConstants.SELECT_TYPE));
					mpData.put(DomainConstants.SELECT_ID,(String)mpInnerData.get(DomainConstants.SELECT_ID));
					mlCSSData.add(mpData);
					hmArgs.put("objectList",mlCSSData);
					vcType = JPO.invoke(context,"pgIPMTablesJPO",null,"getTypeNameColumnData",JPO.packArgs(hmArgs),Vector.class);
					strDisplayNameChild = (String) vcType.get(0);
					vcSpecSubType = JPO.invoke(context,"pgIPMTablesJPO",null,"getSpecificationSubtype",JPO.packArgs(hmArgs),Vector.class);
					strSpecSubType = (String) vcSpecSubType.get(0);
				} else {
					strDisplayNameChild = i18nNow.getTypeI18NString((String)mpInnerData.get(DomainConstants.SELECT_TYPE), sLanguage);
					strSpecSubType = (String) mpInnerData.get("SubType");
				}
				hm.put(10, strDisplayNameChild);
				
				hasAccess = (boolean)mpInnerData.get("access");
				if (hasAccess) {
					//Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 49288 - Ends
					hm.put(8, mpInnerData.get("attribute[Title]"));
					hm.put(9, mpInnerData.get("Source"));
					//strTypeDisplayName=getTypeDisplayName(context,(String)mpInnerData.get("type"));
					//hm.put(10, strTypeDisplayName);
					//Updated code for defect 2018x6 Sept 42743, 42740 Starts
					hm.put(11, strSpecSubType);
					//Updated code for defect 2018x6 Sept 42743, 42740 Ends
					hm.put(12, mpInnerData.get("current"));
					hm.put(13, mpInnerData.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE));
					hm.put(14, mpInnerData.get(DomainConstants.SELECT_ORIGINATOR));
					hm.put(15, strRefDocArtworkPrimary);
				} else {
					hm.put(8, pgV3Constants.NO_ACCESS);
					hm.put(9, pgV3Constants.NO_ACCESS);
					//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 49288 - Start
					
					if ("pgArtwork".equalsIgnoreCase(strTypeName)) {
						hm.put(10, pgV3Constants.NO_ACCESS);
					}
					//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 49288 - End
					hm.put(11, pgV3Constants.NO_ACCESS);
					hm.put(12, pgV3Constants.NO_ACCESS);
					hm.put(13, pgV3Constants.NO_ACCESS);
					hm.put(14, pgV3Constants.NO_ACCESS);
					hm.put(15, pgV3Constants.NO_ACCESS);
				}
				//Modified the code for 22x.02 May CW 52204 - Starts
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - Start
				hm.put(16, DomainConstants.EMPTY_STRING);
				hm.put(17, DomainConstants.EMPTY_STRING);
				hm.put(18, DomainConstants.EMPTY_STRING);
				hm.put(19, DomainConstants.EMPTY_STRING);
				hm.put(20, DomainConstants.EMPTY_STRING);
				hm.put(21, DomainConstants.EMPTY_STRING);
				//Modified the code for 22x.02 May CW 52204 - Ends
				hm.put(22, DomainConstants.EMPTY_STRING);
				hm.put(23, DomainConstants.EMPTY_STRING);
				hm.put(24, DomainConstants.EMPTY_STRING);
				hm.put(25, DomainConstants.EMPTY_STRING);
				hm.put(26, DomainConstants.EMPTY_STRING);
				hm.put(27, DomainConstants.EMPTY_STRING);
				hm.put(28, DomainConstants.EMPTY_STRING);
				hm.put(29, DomainConstants.EMPTY_STRING);
				hm.put(30, DomainConstants.EMPTY_STRING);
				hm.put(31, DomainConstants.EMPTY_STRING);
				hm.put(32, DomainConstants.EMPTY_STRING);
				hm.put(33, DomainConstants.EMPTY_STRING);
				hm.put(34, DomainConstants.EMPTY_STRING);
				hm.put(35, DomainConstants.EMPTY_STRING);
				hm.put(36, DomainConstants.EMPTY_STRING);
				hm.put(37, DomainConstants.EMPTY_STRING);
				//Updated code for Req Id : 33634 - Hyperlinks--Starts
				//Updated for Code Review Comments
				hm.put(38, HYPERLINK+hmCommonCol.get("to["+pgV3Constants.RELATIONSHIP_DERIVED+"].from."+DomainConstants.SELECT_NAME)+HYPERLINK_PIPE+hmCommonCol.get("to["+pgV3Constants.RELATIONSHIP_DERIVED+"].from."+DomainConstants.SELECT_ID));
				//Updated code for Req Id : 33634 - Hyperlinks--Ends
				hm.put(39, hmCommonCol.get("to["+pgV3Constants.RELATIONSHIP_DERIVED+"].from."+pgV3Constants.SELECT_ATTRIBUTE_TITLE));
				hm.put(40, mpInnerData.get("PartFamilyName"));
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - End
				
				//For Cells creation in a Row	
				style = workbook.createCellStyle();
				style = pgFamilyCare.setForeGroundColor(strCurrent, style);
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				XSSFCellStyle style1 = workbook.createCellStyle();
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				for(int j=0;j<hm.size();j++){
					//Modified the code for _2022x_Aug CW Req-47097 - Start
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - Start
					Cell cell = row.createCell(columnCount1++);
					if (j == 2) {
						style = pgFamilyCare.makeColumnNumeric(workbook, style);
					}
					cell.setCellStyle(style);
					
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - End
					//Modified the code for _2022x_Aug CW Req-47097 - End
					//Added code for Req Id : 33634 - Hyperlinks--Starts
					String strCellValue = (String)hm.get(j);
					if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
						String[] strSplittedValue = strCellValue.split("\\|", -1); 
						String strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
						String strValue = strSplittedValue[(strSplittedValue.length)-2];
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Starts
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
							pgFamilyCare.getHyperlink(context,cell,workbook,strValue,strHyperlinkId,strCurrent);
						
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)){					
							cell.setCellValue(strValue);
						}
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Ends
					}else {
						cell.setCellValue((String)hm.get(j));
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
						if(j == 0) {
							style1.cloneStyleFrom(style);
							style1.setWrapText(true);
							cell.setCellStyle(style1);
						}
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
					}
					//Added code for Req Id : 33634 - Hyperlinks--Ends
				}
			}
			pgFamilyCare.sheetFormatter(sheetSpecsDocs); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
			// Logic to update sheet for Reference Document
			iListSize=mlSpecsRefDocsAll.size();	
			for (int i=0;i<iListSize;i++){
				XSSFRow row = sheetSpecsDocs.createRow(++rowCount1);
				int columnCount1 = 0;
				Map mpInnerData = (Map)mlSpecsRefDocsAll.get(i);
				HashMap hm= new HashMap<>();
				// CommonColumns Begin		
				Map hmCommonCol = new HashMap<>();
				hmCommonCol= (Map)mpInnerData.get("CommonColumns");
				// CommonColumns End	
				//Fetching the Types Display name --Begin
				String strTypeDisplayName=null;
				//Added the code for defect Id :30015 - data displayed in family care report Specs and Docs sheet doesn't match the value in UI - Starts
				String strRefDocType=DomainConstants.EMPTY_STRING;
				//Added the code for defect Id :30015 - data displayed in family care report Specs and Docs sheet doesn't match the value in UI - Ends
				String strTypeName = (String)hmCommonCol.get("type");
				strTypeDisplayName= pgFamilyCare.getTypeDisplayName(context,strTypeName);
				//Fetching the Types Display name --End
				String strCurrent = (String)hmCommonCol.get("current");
				//if(!strCurrent.equalsIgnoreCase("Release")){
				//Added the code for the Defect ID : 25104 - CA Tab is Empty : Starts
				//Code Upgrade for 2018x Data Model - Starts
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				String sCAID = (String) hmCommonCol.get(SCAID);
				if(UIUtil.isNotNullAndNotEmpty(sCAID) && !sCAID.contains(HYPERLINK_PIPE)) {
					if(hmCommonCol.get(CAREALIZEDNAME) != null && hmCommonCol.get(CAPROPOSEDNAME) != null) {
						//Updated code for Req Id : 33634 - Hyperlinks--Starts
						hm.put(0, HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
						//Updated code for Req Id : 33634 - Hyperlinks--Ends
					} else if(hmCommonCol.get(CAREALIZEDNAME) != null){
						//Updated code for Req Id : 33634 - Hyperlinks--Starts
						hm.put(0, HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
						//Updated code for Req Id : 33634 - Hyperlinks--Ends
					} else if(hmCommonCol.get(CAPROPOSEDNAME) != null){
						//Updated code for Req Id : 33634 - Hyperlinks--Starts
						hm.put(0, HYPERLINK+hmCommonCol.get(CAPROPOSEDNAME)+HYPERLINK_PIPE+sCAID);
						//Updated code for Req Id : 33634 - Hyperlinks--Ends
					} else {
						hm.put(0, DomainConstants.EMPTY_STRING);
					}	
				}else{
					hm.put(0,hmCommonCol.get(CAPROPOSEDNAME));
				}
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
						
				//Code Upgrade for 2018x Data Model - Ends
				//Added the code for the Defect ID : 25104 - CA Tab is Empty : Starts
				hm.put(1, hmCommonCol.get("attribute[Reason for Change]"));
				//Updated code for Req Id : 33634 - Hyperlinks--Starts
				hm.put(2, HYPERLINK+hmCommonCol.get("name")+HYPERLINK_PIPE+hmCommonCol.get("id"));
				//Updated code for Req Id : 33634 - Hyperlinks--Ends
				hm.put(3, hmCommonCol.get("attribute[Title]"));			
				hm.put(4, strTypeDisplayName);
				//Code for requirement to addition of revision and state on all tabs except attribute -- Starts
				hm.put(5, hmCommonCol.get("revision"));
				hm.put(6, hmCommonCol.get("current"));
				//Code for requirement to addition of revision and state on all tabs except attribute -- Ends
				hm.put(7,DomainConstants.EMPTY_STRING);			
				// path needs to be added.
				hm.put(8, DomainConstants.EMPTY_STRING);
				hm.put(9, DomainConstants.EMPTY_STRING);
				hm.put(10,DomainConstants.EMPTY_STRING);
				hm.put(11,DomainConstants.EMPTY_STRING);
				hm.put(12,DomainConstants.EMPTY_STRING);
				hm.put(13,DomainConstants.EMPTY_STRING);
				hm.put(14,DomainConstants.EMPTY_STRING);
				hm.put(15,DomainConstants.EMPTY_STRING);
				//Added the code for defect Id :30015 - data displayed in family care report Specs and Docs sheet doesn't match the value in UI - Starts
				//Defect # 35022 : format.file.name failes when there are more than one checked in file.
				//Modified the code for 22x.02 May CW 52204 - Starts
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - Start
				hm.put(16, DomainConstants.EMPTY_STRING);
				hm.put(17, DomainConstants.EMPTY_STRING);
				hm.put(18, DomainConstants.EMPTY_STRING);
				hm.put(19, DomainConstants.EMPTY_STRING);
				hm.put(20, DomainConstants.EMPTY_STRING);
				hm.put(21, DomainConstants.EMPTY_STRING);
				//Modified the code for 22x.02 May CW 52204 - Ends
				//Updated code for defect id 35419 starts
				hm.put(22,HYPERLINK+mpInnerData.get("name")+HYPERLINK_PIPE+mpInnerData.get(DomainConstants.SELECT_ID));
				//Updated code for defect id 35419 starts
				hm.put(23,mpInnerData.get("FileName"));
				//hm.put(15,mpInnerData.get("format.file.name"));
				hm.put(24,mpInnerData.get("Language"));
				hm.put(25,mpInnerData.get("Source"));
				hm.put(26,mpInnerData.get("attribute[Title]"));
				String strInnerRev = (String) mpInnerData.get ("revision");
				if (!strInnerRev.equalsIgnoreCase("Rendition")) {
					hm.put(27,mpInnerData.get("revision"));
				} else {
					hm.put(27,hmCommonCol.get(DomainConstants.SELECT_REVISION));					
				}				
				hm.put(28,mpInnerData.get("revision"));
				//Updated code for defect 2018x6 Sept 42777 Starts
				strRefType = (String) mpInnerData.get(DomainConstants.SELECT_TYPE);
				strOriginatingSource =  (String) hmCommonCol.get(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
				if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && strOriginatingSource.contains("CSS")) {
					strRefDocType = pgFamilyCare.getRefDocTypeForCSS(mpInnerData);
				
				} else { 
					strRefDocType = i18nNow.getTypeI18NString(strRefType, sLanguage);
				}
				//strRefDocType=getRefDocTypeInfo(context,(String)mpInnerData.get("attribute["+ATTR_PG_CSS_TYPE+"]"));
				hm.put(29,strRefDocType);
				//Updated code for defect 2018x6 Sept 42777 Ends
				//Added the code for defect Id :30015 - data displayed in family care report Specs and Docs sheet doesn't match the value in UI - Ends
				hm.put(30,mpInnerData.get(DomainConstants.SELECT_DESCRIPTION));
				hm.put(31,mpInnerData.get("current"));				
				hm.put(32,DomainConstants.EMPTY_STRING);
				hm.put(33,DomainConstants.EMPTY_STRING);
				hm.put(34,DomainConstants.EMPTY_STRING);
				hm.put(35,DomainConstants.EMPTY_STRING);
				hm.put(36,DomainConstants.EMPTY_STRING);
				hm.put(37,DomainConstants.EMPTY_STRING);
				//Updatee code for Req Id : 33634 - Hyperlinks--Starts
				hm.put(38,HYPERLINK+hmCommonCol.get("to[Derived].from.name")+HYPERLINK_PIPE+hmCommonCol.get("to[Derived].from.id"));
				//Updated code for Req Id : 33634 - Hyperlinks--Ends
				hm.put(39,hmCommonCol.get("to[Derived].from.attribute[Title]"));
				hm.put(40,mpInnerData.get("PartFamilyName"));
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - End
				
				//For Cells creation in a Row
				style = workbook.createCellStyle();
				style = pgFamilyCare.setForeGroundColor(strCurrent, style);
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				XSSFCellStyle style1 = workbook.createCellStyle();
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				for(int j=0;j<hm.size();j++){
					//Modified the code for _2022x_Aug CW Req-47097 - Start
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - Start
					Cell cell = row.createCell(columnCount1++);
					if (j == 2) {
						style =  pgFamilyCare.makeColumnNumeric(workbook, style);
					}
					cell.setCellStyle(style);
					
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - End
					//Modified the code for _2022x_Aug CW Req-47097 - End
					//Added code for Req Id : 33634 - Hyperlinks--Starts
					String strCellValue = (String)hm.get(j);
					if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
						String[] strSplittedValue = strCellValue.split("\\|", -1); 
						String strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
						String strValue = strSplittedValue[(strSplittedValue.length)-2];
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Starts
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
							pgFamilyCare.getHyperlink(context,cell,workbook,strValue,strHyperlinkId,strCurrent);
						
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)){					
							cell.setCellValue(strValue);
						}
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Ends
					}else {
						cell.setCellValue((String)hm.get(j));
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
						if(j == 0) {
							style1.cloneStyleFrom(style);
							style1.setWrapText(true);
							cell.setCellStyle(style1);
						}
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
					}
					//Added code for Req Id : 33634 - Hyperlinks--Ends
				}
			}
			pgFamilyCare.sheetFormatter(sheetSpecsDocs);	 //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
			// Logic to update sheet for Reference Document
			
			// Logic to update the sheet for the Characteristics Reference Document
			MapList mlTMListAll=new MapList();
			iListSize=mlPerformanceCharacteristicsAll.size();
			for (int iIndex2=0;iIndex2<iListSize;iIndex2++){			
				Map mpInnerData=(Map)mlPerformanceCharacteristicsAll.get(iIndex2);
				Map hmCommonCol = new HashMap<>();
				hmCommonCol= (Map)mpInnerData.get("CommonColumns");
				// CommonColumns End
				String strTMNameIds= (String)mpInnerData.get("TestMethodIds");
				String strPartFamilyName= (String)mpInnerData.get("PartFamilyName");
				String strDerivedFromTitle= (String)hmCommonCol.get("to[Derived].from.attribute[Title]");
				String strDerivedFromName= (String)hmCommonCol.get("to[Derived].from.name"); 
				//Added code for Req Id : 33634 - Hyperlinks--Starts
				String strDerivedFromId= (String)hmCommonCol.get("to[Derived].from.id");
				//Added code for Req Id : 33634 - Hyperlinks--Ends
				String bHasTMRDAccess = (String)mpInnerData.get("TMRDAccess");
				//Added the code for the Defect ID 34908 Starts
				if(UIUtil.isNotNullAndNotEmpty(bHasTMRDAccess)){
				//Added the code for the Defect ID 34908 Ends
				if (bHasTMRDAccess.equalsIgnoreCase("TRUE")) {
					if(UIUtil.isNotNullAndNotEmpty(strTMNameIds))
					{
						StringTokenizer st = new StringTokenizer(strTMNameIds,",");  
						while (st.hasMoreTokens()) {
							Map hmTemp= new HashMap();
							//Added the code for the Defect ID : 25104 - CA Tab is Empty : Starts
							//Code Upgrade for 2018x Data Model - Starts
							//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
							String sCAID = (String) hmCommonCol.get(SCAID);
							if(UIUtil.isNotNullAndNotEmpty(sCAID) && !sCAID.contains(HYPERLINK_PIPE)) {
								if(hmCommonCol.get(CAREALIZEDNAME) != null && hmCommonCol.get(CAPROPOSEDNAME) != null) {
									//Updated code for Req Id : 33634 - Hyperlinks--Starts
									hmTemp.put("CAName", HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
									//Updated code for Req Id : 33634 - Hyperlinks--Ends
								} else if(hmCommonCol.get(CAREALIZEDNAME) != null){
									//Updated code for Req Id : 33634 - Hyperlinks--Starts
									hmTemp.put("CAName", HYPERLINK+hmCommonCol.get(CAREALIZEDNAME)+HYPERLINK_PIPE+sCAID);
									//Updated code for Req Id : 33634 - Hyperlinks--Ends
								} else if(hmCommonCol.get(CAPROPOSEDNAME) != null){
									//Updated code for Req Id : 33634 - Hyperlinks--Starts
									hmTemp.put("CAName", HYPERLINK+hmCommonCol.get(CAPROPOSEDNAME)+HYPERLINK_PIPE+sCAID);
									//Updated code for Req Id : 33634 - Hyperlinks--Ends
								} else {
									hmTemp.put("CAName", DomainConstants.EMPTY_STRING);
								}
							}else{
								hmTemp.put("CAName",hmCommonCol.get(CAPROPOSEDNAME));
							}
							//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
							//Code Upgrade for 2018x Data Model - Ends
							//Added the code for the Defect ID : 25104 - CA Tab is Empty : Ends
							hmTemp.put("ReasonForChange", hmCommonCol.get("attribute[Reason for Change]"));
							//Updated code for Req Id : 33634 - Hyperlinks--Starts
							hmTemp.put("PartName", HYPERLINK+hmCommonCol.get("name")+HYPERLINK_PIPE+hmCommonCol.get("id"));
							//Updated code for Req Id : 33634 - Hyperlinks--Ends
							hmTemp.put("PartTitle", hmCommonCol.get("attribute[Title]"));
							hmTemp.put("PartType", hmCommonCol.get("type"));
							hmTemp.put("PartCurrent", hmCommonCol.get("current"));
							//Code-Fix for Defect 28680- DSM report wrong Revision of part --Starts
							hmTemp.put("PartRevision", hmCommonCol.get("revision"));
							//Code-Fix for Defect 28680- DSM report wrong Revision of part --Ends
							strIndividualTMName=st.nextToken();
							if(UIUtil.isNotNullAndNotEmpty(strIndividualTMName))
							{
								String	strTempTMId=strIndividualTMName;
								//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 54222 - Starts
								if(SPECREADER.equalsIgnoreCase(strGlobalOriginatingSource)) {
									//Pushing User Agent Context to get the Data if the Spec Reader User have access
									ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
									isContextPushed = true;
								}
								//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 54222 - End
								DomainObject doObjTM=DomainObject.newInstance(context,strTempTMId);
								Map mpTempTMDetails=doObjTM.getInfo(context, SL_OBJECT_TM_SELECT);
								hmTemp.put("TMDetails",mpTempTMDetails);
								//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 54222 - Starts
								if(isContextPushed) {
									ContextUtil.popContext(context);
									isContextPushed = false;
								}
								//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 54222 - End
								//Added the code for May CW 2022x.02 May CW Defect 52204 - Starts
								hmTemp.put("PartFamilyName",(String)mpInnerData.get("PartFamilyName"));
								hmTemp.put("DerivedFromName",(String)hmCommonCol.get("to[Derived].from.name"));
								hmTemp.put("DerivedFromTitle",(String)hmCommonCol.get("to[Derived].from.attribute[Title]"));
								//Added the code for May CW 2022x.02 May CW Defect 52204 - Ends
								mlTMListAll.add(hmTemp);
							}					
						}				
					}			
				}
				//Added the code for the Defect ID 34908 Starts
			}
				//Added the code for the Defect ID 34908 Ends
			}
			// Code for updating the sheet for Charcteristic Ref Doc
			iListSize=mlTMListAll.size();
			for (int i=0;i<iListSize;i++){
				XSSFRow row = sheetSpecsDocs.createRow(++rowCount1);
				int columnCount1 = 0;
				Map mpInnerData = (Map)mlTMListAll.get(i);
				//Fetching the Types Display name --Begin
				String strTypeDisplayName=null;
				String strTypeName = (String)mpInnerData.get("PartType");			
				strTypeDisplayName= pgFamilyCare.getTypeDisplayName(context,strTypeName);
				String bHasTMRDAccess=(String)mpInnerData.get("TMRDAccess");
				//Fetching the Types Display name --End
				HashMap hm= new HashMap<>();
				// CommonColumns Begin		
				Map hmCommonCol = new HashMap<>();
				hmCommonCol= (Map)mpInnerData.get("TMDetails");			
				// CommonColumns End	
				String strCurrent = (String)mpInnerData.get("PartCurrent");
				//if(!strCurrent.equalsIgnoreCase("Release")){
				hm.put(0,mpInnerData.get("CAName"));
				hm.put(1,mpInnerData.get("ReasonForChange"));
				hm.put(2,mpInnerData.get("PartName"));
				hm.put(3,mpInnerData.get("PartTitle"));
				hm.put(4,strTypeDisplayName);
				//Code for requirement to addition of revision and state on all tabs except attribute -- Starts
				//Code-Fix for Defect 28680- DSM report wrong Revision of part --Starts
				hm.put(5, mpInnerData.get("PartRevision"));
				hm.put(6, mpInnerData.get("PartCurrent"));
				//Code-Fix for Defect 28680- DSM report wrong Revision of part --Ends
				//Code for requirement to addition of revision and state on all tabs except attribute -- Ends
				hm.put(7,DomainConstants.EMPTY_STRING);			
				// path needs to be added.
				hm.put(8,  DomainConstants.EMPTY_STRING);
				hm.put(9,  DomainConstants.EMPTY_STRING);
				hm.put(10, DomainConstants.EMPTY_STRING);
				hm.put(11, DomainConstants.EMPTY_STRING);
				hm.put(12, DomainConstants.EMPTY_STRING);
				hm.put(13, DomainConstants.EMPTY_STRING);
				hm.put(14, DomainConstants.EMPTY_STRING);
				hm.put(15, DomainConstants.EMPTY_STRING);
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - Starts
				//Modified the code for 22x.02 May CW 52204 - Starts
				hm.put(16, DomainConstants.EMPTY_STRING);
				hm.put(17, DomainConstants.EMPTY_STRING);
				hm.put(18, DomainConstants.EMPTY_STRING);
				hm.put(19, DomainConstants.EMPTY_STRING);
				hm.put(20, DomainConstants.EMPTY_STRING);
				hm.put(21, DomainConstants.EMPTY_STRING);
				//Modified the code for 22x.02 May CW 52204 - Ends
				hm.put(22, DomainConstants.EMPTY_STRING);
				hm.put(23, DomainConstants.EMPTY_STRING);
				hm.put(24, DomainConstants.EMPTY_STRING);
				hm.put(25, DomainConstants.EMPTY_STRING);
				hm.put(26, DomainConstants.EMPTY_STRING);
				hm.put(27, DomainConstants.EMPTY_STRING);
				hm.put(28, DomainConstants.EMPTY_STRING);
				hm.put(29, DomainConstants.EMPTY_STRING);
				hm.put(30, DomainConstants.EMPTY_STRING);
				hm.put(31, DomainConstants.EMPTY_STRING);
				//Updated code for defect id 35419 starts
				hm.put(32, HYPERLINK+hmCommonCol.get("name")+HYPERLINK_PIPE+hmCommonCol.get(DomainConstants.SELECT_ID));
				//Updated code for defect id 35419 ends
				hm.put(33, hmCommonCol.get("attribute[Title]"));
				hm.put(34, hmCommonCol.get(DomainConstants.SELECT_REVISION));
				strTypeDisplayName=pgFamilyCare.getTypeDisplayName(context,(String)hmCommonCol.get(DomainConstants.SELECT_TYPE));
				hm.put(35, strTypeDisplayName);
				hm.put(36, hmCommonCol.get(DomainConstants.SELECT_DESCRIPTION));
				hm.put(37, hmCommonCol.get(DomainConstants.SELECT_CURRENT));		
				hm.put(38, mpInnerData.get("DerivedFromName"));
				hm.put(39, mpInnerData.get("DerivedFromTitle"));
				hm.put(40, mpInnerData.get("PartFamilyName"));
				//Modified by IRM (Sogeti) for 22x.03 August CW Requirement 47172 - Ends
				//For Cells creation in a Row
				style = workbook.createCellStyle();
				style = pgFamilyCare.setForeGroundColor(strCurrent, style);
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
				XSSFCellStyle style1 = workbook.createCellStyle();
				//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
				for(int j=0;j<hm.size();j++){
					//Modified the code for _2022x_Aug CW Req-47097 - Start
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - Start
					Cell cell = row.createCell(columnCount1++);
					if (j == 2) {
						style = pgFamilyCare.makeColumnNumeric(workbook, style);
					}
					cell.setCellStyle(style);
					
					// Modified by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 54091 - End
					//Modified the code for _2022x_Aug CW Req-47097 - End
					//Added code for Req Id : 33634 - Hyperlinks--Starts
					String strCellValue = (String)hm.get(j);
					if(!strCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE) && !strCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASED)){
						cell.setCellStyle(style);
					}
					if(UIUtil.isNotNullAndNotEmpty(strCellValue) && strCellValue.startsWith(HYPERLINK_COMPARE)){
						String[] strSplittedValue = strCellValue.split("\\|", -1); 
						String strHyperlinkId = strSplittedValue[(strSplittedValue.length)-1];
						String strValue = strSplittedValue[(strSplittedValue.length)-2];
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Starts
						if(pgV3Constants.TRUE.equalsIgnoreCase(strHyperlink) && iRowCountAll < iHyperLinkLimit){
							pgFamilyCare.getHyperlink(context,cell,workbook,strValue,strHyperlinkId,strCurrent);
						
						} else if(UIUtil.isNotNullAndNotEmpty(strValue)){					
							cell.setCellValue(strValue);
						}
						//Added code for 2018x.6 Requirement id 36700 Ability to generate Part and Spec report with without hyperlink Ends
					}else {
						cell.setCellValue((String)hm.get(j));
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - Start
						if(j == 0) {
							style1.cloneStyleFrom(style);
							style1.setWrapText(true);
							cell.setCellStyle(style1);
						}
						//Added by DSM Reports (Sogeti) for 2022x.5 (APR CW 2024) Defect 55875 - End
					}
					//Added code for Req Id : 33634 - Hyperlinks--Ends
				}
			}
			pgFamilyCare.sheetFormatter(sheetSpecsDocs); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
		} catch (Exception e) {
			 outLog.print("Exception in  updateWorksheetSpecsDocs: "+e+"\n");
			 outLog.flush();
		}
		// Code for updating the sheet for Characteristic Doc
		// Logic to update the sheet for the Characteristic Reference Document
		}
		// Method for Related Specs Docs --End
	
	/**
	 * @param context
	 * @param strUserName
	 * @param dobjPart
	 * @param mlSubstanceAndMaterials
	 * @param mpCommonColumnsDetail
	 * @param bHasTabAccess
	 * @return
	 * @throws Exception
	 */
	//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START
	public MapList getSubstancesAndMaterials(Context context, String strUserName, DomainObject dobjPart, MapList mlSubstanceAndMaterials, Map mpCommonColumnsDetail, boolean bHasTabAccess, String strOriginatingSource, String strPPartFamilyName) throws Exception { 
	final String TYPE_INTERNAL_MATERIAL = PropertyUtil.getSchemaProperty("type_InternalMaterial"); //Added the code for 22x.06 Defect 57760 - Starts/Ends
	//Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- END
	//Added for Defect : 50138 Start
	String strParentType = dobjPart.getTypeName();
	//Modified by DSM Reports for 2022x.5 (APR CW 2024) Defect 57310 - Start
	pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null, strOriginatingSource);
	//Modified by DSM Reports for 2022x.5 (APR CW 2024) Defect 57310 - Start
	//Added for Defect : 50138 End
		/// MOVE CODE TO a Seperate Method to get the data	
		//Added the code for Defect 49698 - Spec Reader Access Issue - Starts
		boolean hasAccess = false;
		boolean isContextPushed = false;
		//Modified the code for 2022x.02 May Cw Defect 51675 - Starts
		String strParentEnvClass ="";
		// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
		String strConsumerRolledUpValue = "";
		String strIndustrialRolledUpValue = "";
		// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - End
		//Modified the code for 2022x.02 May Cw Defect 51675 - Ends
		////Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 54465 - Ends
		String strPartPolicy = "";
		//Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 54465 - Ends
		
	try {
		if(SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
			////Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 54465 - Ends
			strPartPolicy = (String)mpCommonColumnsDetail.get(DomainConstants.SELECT_POLICY);
			if(pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strPartPolicy) || 
					pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equals(strPartPolicy)) {
				hasAccess = true;
			}else {
				hasAccess = pgFamilyCare.accessCheck(context, strUserName, (String) dobjPart.getInfo(context, DomainConstants.SELECT_ID));
			}
			//Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 54465 - Ends
			if(hasAccess) {
				//Pushing User Agent Context to get the Data if the Spec Reader User have access
				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
				
			}
		}
		//Added the code for Defect 49698 - Spec Reader Access Issue - Ends
	Map<String,Object> mpCompMat=pgFamilyCare.getSubsMaterials(context,strUserName,dobjPart,mpCommonColumnsDetail);
	//Added the code for 2022x.02 May CW Defect 51675 - Starts
	strParentEnvClass = (String)mpCompMat.get(STRFROMSTART+pgV3Constants.RELATIONSHIP_PGMATERIALTOPGPLIENVIRONMENTALCLASS+STRTONAME);
	//Added the code for 2022x.02 May CW defect 52760 - Starts
	if(null == strParentEnvClass) {
		strParentEnvClass = DomainConstants.EMPTY_STRING;
	}
	// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
	strConsumerRolledUpValue = (String)mpCompMat.get(pgV3Constants.SELECT_ATTRIBUTE_PGPOSTCONSUMERRECYCLEDCONTENT+STRINPUTVALUE);
	strIndustrialRolledUpValue = (String)mpCompMat.get(ATTRIBUTE_PERCENTPOSTINDUSTRIALRECYCLATE+STRINPUTVALUE);
	if(null == strConsumerRolledUpValue) {
		strConsumerRolledUpValue = DomainConstants.EMPTY_STRING;
	}
	if(null == strIndustrialRolledUpValue) {
		strIndustrialRolledUpValue = DomainConstants.EMPTY_STRING;
	}
	// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - End
	//Added the code for 2022x.02 May CW defect 52760 - Ends
	//Added the code for 2022x.02 May CW Defect 51675 - Ends
	//Modified by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55875 - Start
	Map<String,String> mpCommonColumnsDetal = pgFamilyCare.getCommonColumnsDetail(context,strUserName, dobjPart);
	//Modified by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55875 - End
	MapList mlMCPCertificationData=new MapList();
	MapList mlCompMatChild=new MapList();
	StringList slRelSlct = new StringList(8);
	slRelSlct.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSEQUENCE);
	slRelSlct.addElement(pgV3Constants.SELECT_ATTRIBUTE_QUANTITYUNITOFMEASURE);
	slRelSlct.addElement(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY+STRINPUTVALUE);
	slRelSlct.addElement(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
	slRelSlct.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMINIMUMPERCENTWEIGHTBYWEIGHT+STRINPUTVALUE);
	slRelSlct.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMAXIMUMPERCENTWEIGHTBYWEIGHT+STRINPUTVALUE);
	slRelSlct.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALLAYER);
	// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
	slRelSlct.addElement(ATTRIBUTE_PGTARGETPERCENTWEIGHTBYWEIGHT);
	// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
	//Added for Apr22 CW requirement 41657 Starts
	slRelSlct.addElement(ATTR_PGNSPCG);
	//Added for Apr22 CW requirement 41657 Ends

	//Added the code for the Defect 51675 - Starts
	slRelSlct.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
		
	slRelSlct.add(pgV3Constants.SELECT_ATTRIBUTE_MAXIMUMWEIGHT+STRINPUTVALUE);	
	slRelSlct.add(pgV3Constants.SELECT_ATTRIBUTE_MINIMUMWEIGHT+STRINPUTVALUE);	
	slRelSlct.add(FormulationAttribute.IS_CONTAMINANT.getAttributeSelect(context));
	slRelSlct.add(FormulationAttribute.FILL.getAttributeSelect(context));
	slRelSlct.add(FormulationAttribute.IS_TARGET_MATERIAL.getAttributeSelect(context));
	slRelSlct.add(FormulationAttribute.IS_COLORANT.getAttributeSelect(context));
	slRelSlct.add(FormulationAttribute.ACTIVE_INGREDIENT_FLAG.getAttributeSelect(context));
	slRelSlct.add(FormulationAttribute.PRESERVATIVE_FLAG.getAttributeSelect(context));
	slRelSlct.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITYUNITOFMEASURE);
	slRelSlct.add(pgV3Constants.SELECT_ATTRIBUTE_QSTOCOMPOSITE);

	slRelSlct.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
	slRelSlct.add(pgV3Constants.SELECT_ATTRIBUTE_APPLICATION);
	//Added the code for the Defect 51675 - Ends


	StringList slSelect = new StringList(8);			
	//Modified the code to implement Code Review Comments - Starts
	slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);	
	//Modified the code to implement Code Review Comments - Ends
	slSelect.add("last.id");

	//Added the code for the Defect 51675 - Starts
	slSelect.add(DomainConstants.SELECT_NAME);
	slSelect.add(DomainConstants.SELECT_TYPE);
	slSelect.add(DomainConstants.SELECT_DESCRIPTION);
	slSelect.add(DomainConstants.SELECT_CURRENT);
	slSelect.add(DomainConstants.SELECT_POLICY);
	slSelect.add(DomainConstants.SELECT_REVISION);
	slSelect.add(DomainConstants.SELECT_ORGANIZATION);
	slSelect.add(DomainConstants.SELECT_ID);
	slSelect.add(FormulationAttribute.TOTAL_QUANTITY_OF_RESTRICTED_SUBSTANCE.getAttributeSelect(context));
	slSelect.add(FormulationAttribute.CONTAIN_RESTRICTED_SUBSTANCES.getAttributeSelect(context));
	slSelect.add(FormulationAttribute.EXTERNAL_REVISION_LEVEL.getAttributeSelect(context));
	slSelect.add(FormulationAttribute.MANUFACTURER.getAttributeSelect(context));
	slSelect.add(FormulationAttribute.TRADE_NAME.getAttributeSelect(context));
	slSelect.add("type.kindof["+FormulationType.MATERIAL.getType(context)+"]");
	slSelect.add("type.kindof["+FormulationType.SUBSTANCE.getType(context)+"]");
	slSelect.add(FormulationAttribute.EC_NUMBER.getAttributeSelect(context));
	slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_SUBSTANCENAME);
	slSelect.add(FormulationAttribute.TITLE.getAttributeSelect(context));
	slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_CASNUMBER);
	slSelect.add("attribute["+pgV3Constants.ATTRIBUTE_INTERNAL_MATERIAL_FOR+"]");
	//Added the code for the Defect 51675 - Ends



	String strCmpMatChildId = DomainConstants.EMPTY_STRING;
	String strLevel = DomainConstants.EMPTY_STRING;
	String strSeqValue = DomainConstants.EMPTY_STRING;
	String strQuantity = DomainConstants.EMPTY_STRING;
	String strQuantityUOM = DomainConstants.EMPTY_STRING;
	String strMinWeight = DomainConstants.EMPTY_STRING;
	String strMaxWeight = DomainConstants.EMPTY_STRING;
	String strComment = DomainConstants.EMPTY_STRING;
	String strMaterialLayer = DomainConstants.EMPTY_STRING;
	String strComponentTitle = DomainConstants.EMPTY_STRING;
	String strParentPartName = (String)mpCommonColumnsDetail.get(DomainConstants.SELECT_NAME);

	String strRelName = DomainConstants.EMPTY_STRING;
	StringList objectSelects1=new StringList();
	objectSelects1.add(DomainConstants.SELECT_ID);
	//Added for Apr22 CW requirement 42010 Starts






	String strChildComponentTitle = DomainConstants.EMPTY_STRING;
	String strParentPartId = mpCommonColumnsDetal.get(DomainConstants.SELECT_ID);
	Pattern objType = new Pattern(FormulationType.MATERIAL.getType(context));
	Pattern relType = new Pattern(FormulationRelationship.COMPONENT_MATERIAL.getRelationship(context));
	boolean isSubstance = MATCUtil.isKindOfParentType(context, strParentPartId, MATCSchema.Type.SUBSTANCE.get(context));
	boolean isPart = MATCUtil.isKindOfParentType(context, strParentPartId, MATCSchema.Type.PART.get(context)) ;
	//Modified the code to implement Code Review Comments - Starts
	DomainObject doobj = DomainObject.newInstance(context,strParentPartId);
	//Modified the code to implement Code Review Comments - Ends
	//Added the code for the Defect 51675 - Starts
	String strInputType = mpCommonColumnsDetal.get(DomainConstants.SELECT_TYPE);
	boolean isObjectOfMaterialType = Boolean.parseBoolean(doobj.getInfo(context,"type.kindof[" + FormulationType.MATERIAL.getType(context) + "]"));
	String strSelectAttrInternalMaterialFor = doobj.getInfo(context, "attribute["+pgV3Constants.ATTRIBUTE_INTERNAL_MATERIAL_FOR+"]");	

		  objType.addPattern(FormulationType.SUBSTANCE.getType(context));
	      relType.addPattern(FormulationRelationship.COMPONENT_SUBSTANCE.getRelationship(context));
	      relType.addPattern(FormulationRelationship.SECURE_COMPONENT_SUBSTANCE.getRelationship(context));

	if(isSubstance){
			strComponentTitle =  doobj.getInfo(context,MATCSchema.Attribute.SUBSTANCE_NAME.getAttributeSelect(context));
		}else if(isPart) {
			strComponentTitle = doobj.getInfo(context,MATCSchema.Attribute.V_NAME.getAttributeSelect(context));
		}else{
			strComponentTitle = doobj.getInfo(context,MATCSchema.Attribute.TITLE.getAttributeSelect(context));
		}
		//The expansion for classified Item relationship is on both from(true)/to(true) as Part Family can be connected on both sides of the Part
		
		MapList	mlMaterial=DomainObject.findObjects(context, //context
					pgV3Constants.TYPE_MATERIAL, //type
					strParentPartName,//name
					null, //revision
					null, //owner
					pgV3Constants.VAULT_ESERVICEPRODUCTION, //vault
					null, //where clause
					false, //expand type
					objectSelects1);//object select
	//Updated for Defect : 31814 Starts
		// Added by DSM Reports (Sogeti) for 22x.05 Defect 57406 - START
		if(!(isContextPushed || SPECREADER.equalsIgnoreCase(strOriginatingSource))) {
			ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			isContextPushed = true;
		}
		//Added by DSM Reports (Sogeti) for 22x.05 Defect 57406 - END
		MapList mlConnectedMaterial= dobjPart.getRelatedObjects(context, //context
							relType.getPattern(), //rel pattern
							objType.getPattern(), //type pattern
							slSelect, //obj select
							null, //rel select
							false, //get To
							true, //get From
							(short)1, //recurse level
							null, //obj where clause
							null, //rel where clause
							0); //limit
			//Updated for Defect : 31814 Ends
			if(!mlMaterial.isEmpty() && !mlConnectedMaterial.isEmpty()){
				HashMap<String,String> materialMap = (HashMap)mlMaterial.get(0);
				String strMaterialId=materialMap.get(DomainConstants.SELECT_ID);	
				DomainObject dobjMaterial = DomainObject.newInstance(context, strMaterialId);
				//Updated for Defect : 31814 Starts
				mlCompMatChild= dobjMaterial.getRelatedObjects(context, //context
								relType.getPattern(), //rel pattern
								objType.getPattern(), //type pattern
								slSelect, //obj select
								slRelSlct, //rel select
								false, //get To
								true, //get From
								(short)0, //recurse level
								null, //obj where clause
								null, //rel where clause
								0); //limit
				//Updated for Defect : 31814 Ends
				if(!mlCompMatChild.isEmpty()){
				boolean bIsParent = true;
				String strParentFieldName = mpCommonColumnsDetal.get(DomainConstants.SELECT_NAME);
				Map<String,Object>	mParentDetails = new HashMap<>();
				mParentDetails.put(STRFINALCOMMONCOLUMNS, mpCommonColumnsDetal);
				mParentDetails.put(STRCONSUMERCYCLATE, strConsumerRolledUpValue);
				mParentDetails.put(STRINDUSTRIALCYCLATE, strIndustrialRolledUpValue);
				mParentDetails.put(ISPARENT, bIsParent);
				mParentDetails.put(STRFINALPARTFAMILYNAME,strPPartFamilyName); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
				mParentDetails.put(STRFINALPARENTNAME,strParentFieldName);
				//Modified the code for 2022x.02 May CW Defect 51675 - Starts
				mParentDetails.put(STRFINALENVCLASS, strParentEnvClass);
				//Modified the code for 2022x.02 May CW Defect 51675 - Ends
				if(bHasTabAccess){
					mlSubstanceAndMaterials.add(mParentDetails);
				}
				for(int i=0;i<mlCompMatChild.size();i++){
					Map<String,String> mpCompMatChild=(Map)mlCompMatChild.get(i);
					strCmpMatChildId=mpCompMatChild.get(DomainConstants.SELECT_ID);	
					strLevel =mpCompMatChild.get(STRFINALLEVEL);
					strSeqValue = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGSEQUENCE);
					//Updated for Defect : 31814 Starts
					strQuantity =mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY+STRINPUTVALUE);
					//Updated for Defect : 31814 Ends
					strQuantityUOM = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITYUNITOFMEASURE);
					//Updated for Defect : 31814 Starts
					//Added the code for 2022x.02 May CW Defect 51675 - Starts
					strMinWeight = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_MINIMUMWEIGHT+STRINPUTVALUE);
					strMaxWeight = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_MAXIMUMWEIGHT+STRINPUTVALUE);
					
					//Added the code for 2022x.02 May CW Defect 51675 - Ends
					//Updated for Defect : 31814 Ends
					strComment = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
					if (UIUtil.isNullOrEmpty(strComment)){
						strComment=DomainConstants.EMPTY_STRING;
					}
					strMaterialLayer = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALLAYER);
					//Added for Defect : 31814 Starts
					boolean isSubstanceChild = MATCUtil.isKindOfParentType(context, strCmpMatChildId, MATCSchema.Type.SUBSTANCE.get(context)) ;
					boolean isPartChild = MATCUtil.isKindOfParentType(context, strCmpMatChildId, MATCSchema.Type.PART.get(context)) ;
					//Modified the code to implement Code Review Comments - Starts
					DomainObject doObjChild = DomainObject.newInstance(context,strCmpMatChildId);
					//Modified the code to implement Code Review Comments - Ends
					if(isSubstanceChild){
						strChildComponentTitle = doObjChild.getInfo(context,MATCSchema.Attribute.SUBSTANCE_NAME.getAttributeSelect(context));
					}else if(isPartChild) {
						strChildComponentTitle = doObjChild.getInfo(context,MATCSchema.Attribute.V_NAME.getAttributeSelect(context));
					}else{
						strChildComponentTitle = doObjChild.getInfo(context,MATCSchema.Attribute.TITLE.getAttributeSelect(context));
					}
					//Added for Defect : 31814 Ends
					DomainObject dobjCompChild = DomainObject.newInstance(context, strCmpMatChildId);
					Map<String,Object> mpCompMatChild1=pgFamilyCare.getSubsMaterials(context,strUserName,dobjCompChild,mpCommonColumnsDetail);
					StringList slEnvClass = dobjCompChild.getInfoList(context,STRFROMSTART+pgV3Constants.RELATIONSHIP_PGMATERIALTOPGPLIENVIRONMENTALCLASS+STRTONAME);
					String strEnvClass = slEnvClass.toString().replace("[", "").replace("]", "");
					mpCompMatChild1.put(STRFINALCOMMONCOLUMNS, mpCommonColumnsDetal);
					mpCompMatChild1.put(STRFINALCOMPONENTTITLE, strComponentTitle);
					mpCompMatChild1.put(STRFINALCHILDNAME, mpCompMatChild1.get(DomainConstants.SELECT_NAME)); 
					mpCompMatChild1.put(STRFINALCHILDREVISION, mpCompMatChild1.get(DomainConstants.SELECT_REVISION)); //Added by DSM Reports (Sogeti) for 2022x.6 --- Defect#57760
					mpCompMatChild1.put(STRFINALCHILDTYPE, mpCompMatChild1.get(DomainConstants.SELECT_TYPE));
					mpCompMatChild1.put(STRFINALLEVEL, strLevel);
					mpCompMatChild1.put(STRFINALENVCLASS, strEnvClass);
					mpCompMatChild1.put(STRFINALPARENTNAME, mpCommonColumnsDetal.get(DomainConstants.SELECT_NAME));
					mpCompMatChild1.put(STRFINALSEQUENCEVALUE, strSeqValue);
					mpCompMatChild1.put(STRFINALQUANTITY, strQuantity);
					mpCompMatChild1.put(STRFINALQUANTITYUNITOFMEASURE, strQuantityUOM);
					//Updated for Defect : 31814 Starts
					mpCompMatChild1.put(STRFINALMINIMUMWEIGHT, strMinWeight);
					mpCompMatChild1.put(STRFINALMAXIMUMWEIGHT, strMaxWeight);
					//Updated for Defect : 31814 Ends
					mpCompMatChild1.put(STRFINALCOMMENT, strComment);
					mpCompMatChild1.put(STRFINALMATERIALLAYER, strMaterialLayer);
					//Updated for Defect : 31814 Starts
					mpCompMatChild1.put(STRFINALCHILDTITLE,strChildComponentTitle);
					//Updated for Defect : 31814 Ends
					mpCompMatChild1.put(STRFINALPARTFAMILYNAME,strPPartFamilyName); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
					//Added for Apr22 CW requirement 41657 Starts
					mpCompMatChild1.put(ATTR_PGNSPCG,mpCompMatChild.get(ATTR_PGNSPCG));
					//Added for Apr22 CW requirement 41657 Ends
					//Added the code for the Defect 51675 - Starts
					mpCompMatChild1.put(ATTRFILL,mpCompMatChild.get(FormulationAttribute.FILL.getAttributeSelect(context)));
					mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_ISTARGETMATERIAL,(String)mpCompMatChild.get(FormulationAttribute.IS_TARGET_MATERIAL.getAttributeSelect(context)));
					mpCompMatChild1.put(ATTRIBUTE_ISCOLORANT, (String)mpCompMatChild.get(FormulationAttribute.IS_COLORANT.getAttributeSelect(context)));
					mpCompMatChild1.put(ATTRIBUTE_ACTIVEINGREDIENTFLAG, (String)mpCompMatChild.get(FormulationAttribute.ACTIVE_INGREDIENT_FLAG.getAttributeSelect(context)));
					mpCompMatChild1.put(ATTRIBUTE_PRESERVATIVEFLAG, (String)mpCompMatChild.get(FormulationAttribute.PRESERVATIVE_FLAG.getAttributeSelect(context)));
					
					mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_APPLICATION, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_APPLICATION));
					//Added for Defect : 50138 Start
					if(strParentType.equals(pgV3Constants.TYPE_DEVICEPRODUCTPART)) {
						mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_ISCONTAMINANT,DomainConstants.EMPTY_STRING);
					}else {
						mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_ISCONTAMINANT, (String)mpCompMatChild.get(FormulationAttribute.IS_CONTAMINANT.getAttributeSelect(context)));
					}
					//Added for Defect : 50138 End
					mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_CASNUMBER, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_CASNUMBER));
					//Modified the code for 2022x.02 May CW Defect 52204 - Starts
					mpCompMatChild1.put(STRMINIMUMPERCENTAGEWEIGHTBYWEIGHT, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGMINIMUMPERCENTWEIGHTBYWEIGHT+STRINPUTVALUE));
					mpCompMatChild1.put(STRMAXIMUMPERCENTAGEWEIGHTBYWEIGHT, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGMAXIMUMPERCENTWEIGHTBYWEIGHT+STRINPUTVALUE));
					// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
					mpCompMatChild1.put(STRTARGETPERCENTAGEWEIGHTBYWEIGHT, mpCompMatChild.get(ATTRIBUTE_PGTARGETPERCENTWEIGHTBYWEIGHT));
					// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
					//Modified the code for 2022x.02 May CW Defect 52204 - Ends
					//Added the code for the Defect 51675 - Ends
					//Update for req 42010
					//Add columns for Certifications	
					
					mlMCPCertificationData=pgFamilyCare.getPackagingCertification(context,strCmpMatChildId);		 	
				 	if (null != mlMCPCertificationData && !mlMCPCertificationData.isEmpty()) {
						for (int k = 0; k < mlMCPCertificationData.size(); k++) {
							Map mpCertification = new HashMap();
							Map<String,String> mpEachCert = (Map) mlMCPCertificationData.get(k);
							mpCertification.put("Certification_Name", (String) mpEachCert.get("Certification_Name"));
							
							if(mpEachCert.containsKey("Certification_Status")) {
								mpCertification.put("Certification_Status", (String) mpEachCert.get("Certification_Status"));
							}else {
								mpCertification.put("Certification_Status", "");
							}
							mpCertification.put("Certification_ExpDate", (String) mpEachCert.get("Certification_ExpDate"));
							if(mpEachCert.containsKey("Certification_SupDoc")){
							mpCertification.put("Certification_SupDoc", (String) mpEachCert.get("Certification_SupDoc"));
							}else {
							mpCertification.put("Certification_SupDoc", "");
							}
							//Modified the code for 2018x.6 APR CW Defect 46629 - starts
							if(mpEachCert.containsKey("Certification_IndendedCerti")){
								mpCertification.put("Certification_IntendedCerti", (String) mpEachCert.get("Certification_IndendedCerti"));
								//Modified the code for 2018x.6 APR CW Defect 46629 - Ends
							}else {
								mpCertification.put("Certification_IntendedCerti", "");
							}
							for (Map.Entry<String,Object> entry : mpCompMatChild1.entrySet()) {
								mpCertification.put(entry.getKey(),entry.getValue());
								}
							if(bHasTabAccess){
								
								mlSubstanceAndMaterials.add(mpCertification);
							}
						}
				 	} else {
				 		Map mpCertification = new HashMap();
				 		for (Map.Entry<String,Object> entry : mpCompMatChild1.entrySet()) {
							mpCertification.put(entry.getKey(),entry.getValue());
							}
						
				 		mpCertification.put("Certification_Name", "");
				 		mpCertification.put("Certification_Status", "");
				 		mpCertification.put("Certification_ExpDate", "");
				 		mpCertification.put("Certification_SupDoc", "");
				 		mpCertification.put("Certification_IntendedCerti", "");
						if(bHasTabAccess){
							mlSubstanceAndMaterials.add(mpCertification);
						}
				 	}
				}
				}
			} else {
			//Updated for Defect : 31814 Starts
			mlCompMatChild = dobjPart.getRelatedObjects(context, //context
									relType.getPattern(), //rel pattern
									objType.getPattern(), //type pattern
									slSelect, //obj select
									slRelSlct, //rel select
									false, //get To
									true, //get From
									(short)0, //recurse level
									null, //obj where clause
									null, //rel where clause
									0); //limit
			//Updated for Defect : 31814 Ends
			if(!mlCompMatChild.isEmpty()){
				boolean bIsParent = true;
				String strParentFieldName = mpCommonColumnsDetal.get(DomainConstants.SELECT_NAME);
				Map<String,Object> mParentDetails = new HashMap<>();
				mParentDetails.put(STRFINALCOMMONCOLUMNS, mpCommonColumnsDetal);
				mParentDetails.put(STRCONSUMERCYCLATE, strConsumerRolledUpValue);
				mParentDetails.put(STRINDUSTRIALCYCLATE, strIndustrialRolledUpValue);
				mParentDetails.put(ISPARENT, bIsParent);
				mParentDetails.put(STRFINALPARTFAMILYNAME,strPPartFamilyName); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
				mParentDetails.put(STRFINALPARENTNAME,strParentFieldName);
				//Modified the code for 2022x.02 May CW Defect 51675 - Starts
				mParentDetails.put(STRFINALENVCLASS, strParentEnvClass);
				//Modified the code for 2022x.02 May CW Defect 51675 - Ends
				if(bHasTabAccess){
					mlSubstanceAndMaterials.add(mParentDetails);
				}
				for(int i=0;i<mlCompMatChild.size();i++){
					Map<String,String> mpCompMatChild=(Map)mlCompMatChild.get(i);
					strCmpMatChildId=mpCompMatChild.get(DomainConstants.SELECT_ID);	
					strLevel =mpCompMatChild.get(STRFINALLEVEL);
					strSeqValue = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGSEQUENCE);
					//Updated for Defect : 31814 Starts
					
					//Updated for Defect : 31814 Ends
					strQuantityUOM = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITYUNITOFMEASURE);
					//Updated for Defect : 31814 Starts
					//Added the code for 2022x.02 May CW Defect 51675 - Starts
					strMinWeight =mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_MINIMUMWEIGHT+STRINPUTVALUE);
					strMaxWeight = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_MAXIMUMWEIGHT+STRINPUTVALUE);
					//Added the code for 2022x.02 May CW Defect 51675 - Starts
					//Updated for Defect : 31814 Ends
					strComment = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
					if (UIUtil.isNullOrEmpty(strComment)){
						strComment=DomainConstants.EMPTY_STRING;
					}													
					strMaterialLayer = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALLAYER);
					//Added for Defect : 31814 Starts
					boolean isSubstanceChild = MATCUtil.isKindOfParentType(context, strCmpMatChildId, MATCSchema.Type.SUBSTANCE.get(context)) ;
					boolean isPartChild = MATCUtil.isKindOfParentType(context, strCmpMatChildId, MATCSchema.Type.PART.get(context)) ;
					//Modified the code to implement Code Review Comments - Starts
					DomainObject doObjChild = DomainObject.newInstance(context,strCmpMatChildId);
					//Modified the code to implement Code Review Comments - Ends
					if(isSubstanceChild){
						strChildComponentTitle =  doObjChild.getInfo(context,MATCSchema.Attribute.SUBSTANCE_NAME.getAttributeSelect(context));
					}else if(isPartChild) {
						strChildComponentTitle =  doObjChild.getInfo(context,MATCSchema.Attribute.V_NAME.getAttributeSelect(context));
					}else{
						strChildComponentTitle =  doObjChild.getInfo(context,MATCSchema.Attribute.TITLE.getAttributeSelect(context));
					}
					//Added for Defect : 31814 Ends
					DomainObject dobjCompChild = DomainObject.newInstance(context, strCmpMatChildId);
					//Added the code for 22x.06 Defect 57760 - Starts
					if(dobjCompChild.isKindOf(context, TYPE_INTERNAL_MATERIAL)) {
						strQuantity = getDryPercentrageForInternalMaterial(context,dobjCompChild);
					} else {
						strQuantity = mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY+STRINPUTVALUE);
					}
					//Added the code for 22x.06 Defect 57760 - Ends
					Map<String,Object> mpCompMatChild1=pgFamilyCare.getSubsMaterials(context,strUserName,dobjCompChild,mpCommonColumnsDetail);
					StringList slEnvClass = dobjCompChild.getInfoList(context,STRFROMSTART+pgV3Constants.RELATIONSHIP_PGMATERIALTOPGPLIENVIRONMENTALCLASS+STRTONAME);
					String strEnvClass = slEnvClass.toString().replace("[", "").replace("]", "");
					mpCompMatChild1.put(STRFINALCOMMONCOLUMNS, mpCommonColumnsDetal);
					mpCompMatChild1.put(STRFINALCOMPONENTTITLE, strComponentTitle);
					mpCompMatChild1.put(STRFINALCHILDNAME, mpCompMatChild1.get(DomainConstants.SELECT_NAME));
					mpCompMatChild1.put(STRFINALCHILDREVISION, mpCompMatChild1.get(DomainConstants.SELECT_REVISION)); //Added by DSM Reports (Sogeti) for 2022x.6 --- Defect#57760
					mpCompMatChild1.put(STRFINALCHILDTYPE, mpCompMatChild1.get(DomainConstants.SELECT_TYPE));
					mpCompMatChild1.put(STRFINALLEVEL, strLevel);
					mpCompMatChild1.put(STRFINALENVCLASS, strEnvClass);
					mpCompMatChild1.put(STRFINALPARENTNAME, mpCommonColumnsDetal.get(DomainConstants.SELECT_NAME));
					mpCompMatChild1.put(STRFINALSEQUENCEVALUE, strSeqValue);
					mpCompMatChild1.put(STRFINALQUANTITY, strQuantity);
					mpCompMatChild1.put(STRFINALQUANTITYUNITOFMEASURE, strQuantityUOM);
					//Updated for Defect : 31814 Starts
					mpCompMatChild1.put(STRFINALMINIMUMWEIGHT, strMinWeight);
					mpCompMatChild1.put(STRFINALMAXIMUMWEIGHT, strMaxWeight);
					//Updated for Defect : 31814 Ends
					mpCompMatChild1.put(STRFINALCOMMENT, strComment);
					mpCompMatChild1.put(STRFINALMATERIALLAYER, strMaterialLayer);
					//Updated for Defect : 31814 Starts
					mpCompMatChild1.put(STRFINALCHILDTITLE,strChildComponentTitle);
					//Updated for Defect : 31814 Ends
					mpCompMatChild1.put(STRFINALPARTFAMILYNAME,strPPartFamilyName); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
					//Update for req 42010
					//Added for 18x6 Apr CW Defect 46629 - starts
					mpCompMatChild1.put(ATTR_PGNSPCG,mpCompMatChild.get(ATTR_PGNSPCG));
					//Added for 18x6 Apr CW Defect 46629 - Ends
					//Added the code for the Defect 51675 - Starts
					mpCompMatChild1.put(ATTRFILL,(String)mpCompMatChild.get(FormulationAttribute.FILL.getAttributeSelect(context)));
					mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_ISTARGETMATERIAL,(String)mpCompMatChild.get(FormulationAttribute.IS_TARGET_MATERIAL.getAttributeSelect(context)));
					mpCompMatChild1.put(ATTRIBUTE_ISCOLORANT, (String)mpCompMatChild.get(FormulationAttribute.IS_COLORANT.getAttributeSelect(context)));
					mpCompMatChild1.put(ATTRIBUTE_ACTIVEINGREDIENTFLAG, (String)mpCompMatChild.get(FormulationAttribute.ACTIVE_INGREDIENT_FLAG.getAttributeSelect(context)));
					mpCompMatChild1.put(ATTRIBUTE_PRESERVATIVEFLAG, (String)mpCompMatChild.get(FormulationAttribute.PRESERVATIVE_FLAG.getAttributeSelect(context)));
					mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_APPLICATION, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_APPLICATION));
					//Added for Defect : 50138 Start
					if(strParentType.equals(pgV3Constants.TYPE_DEVICEPRODUCTPART)) {
						mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_ISCONTAMINANT,DomainConstants.EMPTY_STRING);
					}else {
						mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_ISCONTAMINANT, (String)mpCompMatChild.get(FormulationAttribute.IS_CONTAMINANT.getAttributeSelect(context)));
					}
					//Added for Defect : 50138 End
					mpCompMatChild1.put(pgV3Constants.SELECT_ATTRIBUTE_CASNUMBER, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_CASNUMBER));
					//Added the code for the Defect 51675 - Ends
					//Modified the code for 2022x.02 May CW Defect 52204 - Starts
					mpCompMatChild1.put(STRMINIMUMPERCENTAGEWEIGHTBYWEIGHT, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGMINIMUMPERCENTWEIGHTBYWEIGHT+STRINPUTVALUE));
					mpCompMatChild1.put(STRMAXIMUMPERCENTAGEWEIGHTBYWEIGHT, (String)mpCompMatChild.get(pgV3Constants.SELECT_ATTRIBUTE_PGMAXIMUMPERCENTWEIGHTBYWEIGHT+STRINPUTVALUE));
					// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
					mpCompMatChild1.put(STRTARGETPERCENTAGEWEIGHTBYWEIGHT, mpCompMatChild.get(ATTRIBUTE_PGTARGETPERCENTWEIGHTBYWEIGHT));
					// Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53203 - Start
					//Modified the code for 2022x.02 May CW Defect 52204 - Ends
					//Add columns for Certifications			
					mlMCPCertificationData=pgFamilyCare.getPackagingCertification(context,strCmpMatChildId);		
					if (null != mlMCPCertificationData && !mlMCPCertificationData.isEmpty()) {
						for (int k = 0; k < mlMCPCertificationData.size(); k++) {
							Map mpCertification = new HashMap();
							Map<String,String> mpEachCert = (Map) mlMCPCertificationData.get(k);
							mpCertification.put("Certification_Name", (String) mpEachCert.get("Certification_Name"));
							
							if(mpEachCert.containsKey("Certification_Status")) {
								mpCertification.put("Certification_Status", (String) mpEachCert.get("Certification_Status"));
							}else {
								mpCertification.put("Certification_Status", "");
							}
							mpCertification.put("Certification_ExpDate", (String) mpEachCert.get("Certification_ExpDate"));
							if(mpEachCert.containsKey("Certification_SupDoc")){
							mpCertification.put("Certification_SupDoc", (String) mpEachCert.get("Certification_SupDoc"));
							}else {
							mpCertification.put("Certification_SupDoc", "");
							}
							//Modified the code for 2018x.6 APR CW Defect 46629 - Starts
							if(mpEachCert.containsKey("Certification_IndendedCerti")){
								mpCertification.put("Certification_IntendedCerti", (String) mpEachCert.get("Certification_IndendedCerti"));
								//Modified the code for 2018x.6 APR CW Defect 46629 - Ends
							}else {
								mpCertification.put("Certification_IntendedCerti", "");
							}
							for (Map.Entry<String,Object> entry : mpCompMatChild1.entrySet()) {
								mpCertification.put(entry.getKey(),entry.getValue());
								}
							
							if(bHasTabAccess){
								mlSubstanceAndMaterials.add(mpCertification);
							}
						}
				 	} else {
				 		
					Map mpCertification = new HashMap();
				 		for (Map.Entry<String,Object> entry : mpCompMatChild1.entrySet()) {
							mpCertification.put(entry.getKey(),entry.getValue());
							}
				 		mpCertification.put("Certification_Name", "");
				 		mpCertification.put("Certification_Status", "");
				 		mpCertification.put("Certification_ExpDate", "");
				 		mpCertification.put("Certification_SupDoc", "");
				 		mpCertification.put("Certification_IntendedCerti", "");
						if(bHasTabAccess){
							
							mlSubstanceAndMaterials.add(mpCertification);
						}
				 	}
				}
			} else {
				mpCompMat.put(STRFINALCOMMONCOLUMNS, mpCommonColumnsDetal);
				mpCompMat.put(STRFINALPARENTNAME, mpCommonColumnsDetal.get(DomainConstants.SELECT_NAME));
				//Modified for 2018x.6 APR CW Defect 46629 - starts
				mpCompMat.put(ISPARENT, true);
				mpCompMat.put(STRFINALCOMPONENTTITLE,  mpCommonColumnsDetal.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
				//Modified for 2018x.6 APR CW Defect 46629 - Ends
				mpCompMat.put(STRFINALPARTFAMILYNAME,strPPartFamilyName); //Added by DSM Reports (Sogeti) for 22x.04 (Dec CW 2023) Defect 56156 -- START/END
				//Modified the code for 2022x.02 May CW Defect 51675 - Starts
				mpCompMat.put(STRFINALENVCLASS, strParentEnvClass);
				//Modified the code for 2022x.02 May CW Defect 51675 - Ends
				mpCompMat.put("Certification_Name", "");
				mpCompMat.put("Certification_Status", "");
				mpCompMat.put("Certification_ExpDate", "");
				mpCompMat.put("Certification_SupDoc", "");
				mpCompMat.put("Certification_IntendedCerti", "");
				//Added for 2018x.6 MAY CW Defect 46716 - Starts
				
				mpCompMat.put(STRCONSUMERCYCLATE, mpCompMat.get("attribute[pgPostConsumerRecycledContent].inputvalue"));
				mpCompMat.put(STRINDUSTRIALCYCLATE,mpCompMat.get("attribute[Percent Post Industrial Recyclate].inputvalue"));
				
				//Added for 2018x.6 MAY CW Defect 46716 - Ends
				if(bHasTabAccess){
					mlSubstanceAndMaterials.add(mpCompMat);
				}
			}
		}
		//Added for 22x_05 Defect 56387 ---- START		
		final String STRING_OBJECTID = "objectId";
		HashMap programMap= new HashMap();
		programMap.put(STRING_OBJECTID, strParentPartId);
		programMap.put(DomainConstants.SELECT_TYPE, strParentType);
		Boolean breturn = JPO.invoke(context, "pgDSOMaterialComposition", null, "isInternalMaterialForProduct", JPO.packArgs(programMap), Boolean.class);
		if(breturn) {
			getRollupPostConsumerValue(context, mlSubstanceAndMaterials); //Added for 22x_05 Defect 56387 ---- START/END
		}
		//Added for 22x_05 Defect 56387 ---- END

	} catch (Exception e) {
		outLog.print("Exception in getSubstancesAndMaterials method"+e+"\n");
		outLog.flush();
	}

	//Added the code for Defect 49698 - Spec Reader Access Issue - Starts
	finally {
		
		if(isContextPushed) {
			ContextUtil.popContext(context);
		}
		
	}
	//Added the code for Defect 49698 - Spec Reader Access Issue - Ends
	return mlSubstanceAndMaterials;
	}
	
	//Added the code for 2022x.02 May CW Defect 52204 - Starts
	/**
	 * @param context
	 * @param strUserName
	 * @param dobjPart
	 * @param mpCommonColumnsDetail
	 * @param mlPerformanceCharacteristicsAll
	 */
	public void getPerformanceCharcteristicsforSpecsandDocs(Context context,String strUserName, DomainObject dobjPart, Map mpCommonColumnsDetail, MapList mlPerformanceCharacteristicsAll,String strSelectedtabs){
		MapList mlPerformanceCharacteristics = new MapList();
		int iTempSize=0;
		try {
			pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
			//Modified the code for 2022x.02 May CW Defect 52204 - Starts
		if(!strSelectedtabs.contains(TAB_PERFORMANCECHAR)) {
			//Modified the code for 2022x.02 May CW Defect 52204 - Ends
			mlPerformanceCharacteristics=pgFamilyCare.getPerformanceCharacteristics(context,strUserName,dobjPart,mpCommonColumnsDetail);
			
			iTempSize=mlPerformanceCharacteristics.size();
			if(iTempSize>0){
				for(int iIndex=0;iIndex<iTempSize;iIndex++){
					Map mpTemp=(Map)mlPerformanceCharacteristics.get(iIndex);
					
						mlPerformanceCharacteristicsAll.add(mpTemp);
					
				}
			}
			mlPerformanceCharacteristics.clear();
			
		}
		
		}catch (Exception e) {
			outLog.print("Exception in getPerformanceCharcteristicsforSpecsandDocs Method "+e+"\n");
			outLog.flush();
		}
	}
	//Added the code for 2022x.02 May CW Defect 52204 - Ends
	
	
	/**
	 * Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53088
	 * @param context
	 * @param strPath
	 * @param strParentId
	 * @param charId
	 * @param strRelationship
	 * @return
	 */
	public String getPathName(Context context, String strPath, String strParentId, String charId, String strRelationship) {
		boolean isContextPushed = false;
		
		try {
			//Pushing the context to get Path information for FOP to match the UI Logic
			if( !pgV3Constants.PERSON_USER_AGENT.equals(context.getUser())) {

				ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				isContextPushed = true;
			}

			DomainObject CharObj = DomainObject.newInstance(context, charId);
			String sInheritedFromPlatform = (String)CharObj.getInfo(context, "to["+strRelationship+"].attribute["+ ATTRIBUTE_PG_INHERITED_FROM_PLATFORM + "]");
			if("TRUE".equalsIgnoreCase(sInheritedFromPlatform))
			{
				DomainObject parentObj = DomainObject.newInstance(context, strParentId);
				strPath= parentObj.getInfo(context, "to["+STR_RELATIONSHIP_PGPRODUCTPLATFORMFOP+"].from.name");
			}

		}catch(Exception e) {
			outLog.print("Exception in getPathName method "+e+"\n");
			outLog.flush();
		}finally {
			if(isContextPushed) {

				try {
					ContextUtil.popContext(context);
				}catch(Exception ex) {
					outLog.print("Exception while popping the context in getPathName method"+ex+"\n");
					outLog.flush();
				}
			}
		}
		return strPath;
	}

	
	/** 
     * Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53205
     * 
     * This method is to get first name of the input user if the request is coming from CSS integration.
     * @param context.
     * @param strOriginatingSource : Request Originating Source.
     * @param strUserName : User Name.
     * @return sFirstName : First name of the input user.
     * @throws Exception.
     */
    public String getUserName(Context context, String strOriginatingSource, String strUserName) {
    	String strUserDisplayName = DomainConstants.EMPTY_STRING;
    	try {
    		//If Originating Source is not CSS then return strUserName as originator name.
    		if((UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && !strOriginatingSource.contains(STR_ORIGINATING_SOURCE_CSS))){
    			return strUserName;
    		} else {
    			//Check user name is not empty and not contains multiple names separated by comma.
    			if (UIUtil.isNotNullAndNotEmpty(strUserName) && !strUserName.contains(",")) {
    				//Add Selectables.
    				StringList slObjSelects = new StringList(5);
    				slObjSelects.add(DomainConstants.SELECT_ID);
    				slObjSelects.add(DomainConstants.SELECT_NAME);
    				slObjSelects.add(DomainConstants.SELECT_ATTRIBUTE_FIRSTNAME);
    				slObjSelects.add(DomainConstants.SELECT_ATTRIBUTE_LASTNAME);
    				slObjSelects.add(SELECT_ATTRIBUTE_PGTNUMBER);
    				//Add object Where clause.
    				String strWhere = SELECT_ATTRIBUTE_PGTNUMBER + "==\"" + strUserName + "\" && revision==last";
    				//Find user info.
    				MapList mlDataList = DomainObject.findObjects(context, //context 
    						DomainConstants.TYPE_PERSON, 				   // type 
    						DomainConstants.QUERY_WILDCARD, 			   // name 
    						DomainConstants.QUERY_WILDCARD,                // revision 
    						DomainConstants.QUERY_WILDCARD,                // owner
    						pgV3Constants.VAULT_ESERVICEPRODUCTION,        // vault
    						strWhere,                                      // where
    						false,                                         // hidden
    						slObjSelects);                                 // object select

    				//Get user first name.
    				if (null != mlDataList && !mlDataList.isEmpty()) {
    					Map<String, String> dataMap = (Map)mlDataList.get(0);
    					strUserDisplayName = dataMap.get(DomainConstants.SELECT_ATTRIBUTE_FIRSTNAME) + " " + (String)dataMap.get(DomainConstants.SELECT_ATTRIBUTE_LASTNAME);
    				} else {
    					strUserDisplayName = strUserName;
    				} 
    			} else {
    				//Split user name by comma and return first value as first name.
    				StringList slValues = com.matrixone.apps.domain.util.StringUtil.split(strUserName, ",");
    				if (null != slValues && !slValues.isEmpty())
    					strUserDisplayName = (String)slValues.get(0); 
    			} 
    		}
    	} catch (Exception e) {
    		outLog.print("Exception in method getUserName()"+e+"\n");
    		outLog.flush();
    	} 
    	return strUserDisplayName;
    }
    
    
  //Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Requirement 47102 - Starts
    
/**
 * @param context
 * @param strReportName
 * @param strUserName
 * @param strOriginatingSource
 */
public void sendEmail(Context context, String strReportName, String strUserName, String strOriginatingSource) {
		
	Map mpMailData = null;
	String strFromEmailId="";
	String strToEmailIds ="";
	String strMessageBody="";
	String strSubject ="";
	
	try {
			mpMailData = getMailData(context, strReportName, strUserName,strOriginatingSource);
			MxMessageSupport support = new MxMessageSupport();
			support.getSendMailInfo(context);
			Properties props = new Properties();
			props.put("mail.smtp.host", support.getSmtpHost());
			Session session = Session.getDefaultInstance(props, null);
			Message msg = new MimeMessage(session);
			 strFromEmailId = (String)mpMailData.get(STRFROMEMAILID);
			InternetAddress addressFrom = new InternetAddress(strFromEmailId);
			msg.setFrom(addressFrom);
			strToEmailIds = (String)mpMailData.get(STRTOEMAILID);
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(strToEmailIds));
			strSubject = (String)mpMailData.get(STRSUBJECT);
			msg.setSubject(strSubject);
			BodyPart messageBodyPart = new MimeBodyPart();
			strMessageBody =  (String)mpMailData.get(STRMESSAGEBODY);
			messageBodyPart.setContent(strMessageBody, STR_TEXT_HTML);
			if(UIUtil.isNotNullAndNotEmpty(strFromEmailId) && UIUtil.isNotNullAndNotEmpty(strToEmailIds)) {
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			msg.setContent(multipart);
			Transport.send(msg);
		}else {
			outLog.print("please check the emailIds Configured for the User \n");
			outLog.flush();
		}
		
	} catch (Exception e) {
		outLog.print("Exception in sendEmail Method"+e+"\n");
		outLog.flush();
		
	}

	}

	
/**
 * @param context
 * @param strReportName
 * @param strUserName
 * @param strOriginatingSource
 * @return
 */
private Map getMailData(Context context, String strReportName, String strUserName, String strOriginatingSource) {
	Map<String,String>mpMailData = new HashMap<String, String>();
	Map mpContextUserData = null;
	Map mpPLMAdminData = null;
	String[] strSubjectData = null;
	
	try {
		mpPLMAdminData = getPersonData(context, STR_PERSON_PLM_ADMIN);
		if(null != mpPLMAdminData && !mpPLMAdminData.isEmpty()) {
			mpMailData.put(STRFROMEMAILID, (String)mpPLMAdminData.get(pgV3Constants.SELECT_ATTRIBUTE_EMAILADDRESS));
		}
		 mpContextUserData = getPersonData(context, strUserName);
		if(null != mpContextUserData && !mpContextUserData.isEmpty()) {
			mpMailData.put(STRTOEMAILID, (String)mpContextUserData.get(pgV3Constants.SELECT_ATTRIBUTE_EMAILADDRESS));
		}
		String strSubLine = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.SubjectLine");
		
		strSubLine = strSubLine.replace(STR_SYMBOL_BRACKETS, strReportName);
		
		mpMailData.put(STRSUBJECT,strSubLine.trim());
		mpMailData.put(STRMESSAGEBODY,getMessageBody(context, strReportName,strOriginatingSource));
	
		}catch(Exception ex) {
		outLog.print("Exception in getMailData method"+ex+"\n");
		outLog.flush();
			
	}
	return mpMailData;
	
}


/**
 * @param context
 * @param strUserName
 * @return
 */
private Map getPersonData(Context context, String strUserName) {
		
	Map mpUserData = null;
	MapList mlUserInfo = null;
	StringList slSelect =new StringList();
	slSelect.add(DomainConstants.SELECT_ID);
	slSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EMAILADDRESS);
	try {

		 mlUserInfo = DomainObject.findObjects(context,//context
				DomainConstants.TYPE_PERSON,//type
				strUserName,//name
				pgV3Constants.SYMBOL_HYPHEN,//revision
				DomainConstants.QUERY_WILDCARD,//owner
				pgV3Constants.VAULT_ESERVICEPRODUCTION,//vault
				DomainConstants.EMPTY_STRING,//whereExpression
				false,//expandType
				slSelect);//objectSelects
		
		int mlUserInfosize = mlUserInfo.size();
		for(int i=0;i<mlUserInfosize;i++) {
			mpUserData = (Map)mlUserInfo.get(i);
		}
		
	}catch(Exception ex){
	outLog.print("Exception in getPersonData method"+ex+"\n");
	outLog.flush();
	}

	return mpUserData;
	}


	
/**
 * @param context
 * @param strReportName
 * @param strOriginatingSource
 * @return
 */
private String getMessageBody(Context context, String strReportName,String strOriginatingSource) {
	
	String strMessageBody = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.BodyMessage");
	strMessageBody = strMessageBody.replace(STR_SYMBOL_BRACKETS, strReportName);
	StringBuilder sb = new StringBuilder();
	//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53492 - Starts
	sb.append(" <!DOCTYPE><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">" +
			"<style TYPE='text/css'>" +
			"body,table,tr,td {font-family: Verdana, Arial, Helvetica;font-size: 9pt;line-height: 12pt;text-align: left;}" +
			"td.display{font-weight: bold;color: white;background-color: #088CD8;}" +
			"td.hidden{background-color: transparent;border-width: 0em; line-height: 12pt;}" +
			"tr.hidden{background-color: transparent;border-width: 0em; line-height: 16px;height: 16px;}" +
			"</style></head><body><p style=\"text-align:center\"></p><table style=\"width:100%\" >" +
			"<tr text-align=\"center\">" +
			"<th  colspan=\"6\">"+strMessageBody+"</th>"
			+"</tr><tr><td><p>URL:");
			sb.append("<a href="+getUrl(context,strOriginatingSource)+">DSM Report Page URL</a>"
			+"</p></td></tr></table><br></body></html>");
			//Added by DSM Reports (Sogeti) for 22x.03 (August CW 2023) Defect 53492 - Ends
	return sb.toString();
	
	
}


	
/**
 * @param context
 * @param strOriginatingSource
 * @return
 */
private String getUrl(Context context, String strOriginatingSource) {
	
	pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
	String strUrl="";
	StringBuilder sbUrl = new StringBuilder();
	String strUrlPath = "";
	try {
			if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && STR_ENOVIA.equalsIgnoreCase(strOriginatingSource)) {
			strUrl = EnoviaResourceBundle.getProperty(context,EMXCPN, context.getLocale(), "emxCPN.BaseURL");
			strUrl = strUrl.substring(0, strUrl.indexOf(PATH_SEPARATOR+STR_COMMON));
			strUrl = String.valueOf(strUrl).trim();
			strUrlPath = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.Enovia.DSMReportLink");
			sbUrl.append(strUrl);
			sbUrl.append(strUrlPath);
		} else if(UIUtil.isNotNullAndNotEmpty(strOriginatingSource) && STR_SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
			strUrl = pgFamilyCare.getSpecReaderURL(context);
			strUrl = String.valueOf(strUrl).trim();
			 strUrlPath = EnoviaResourceBundle.getProperty(context, EMXCPNSTRINGRESOURCE, context.getLocale(),"emxCPN.DSMReport.Mail.SpecReader.DSMReportLink");
			 sbUrl.append(strUrl);
			 sbUrl.append(strUrlPath);
		}
	}catch(Exception e) {
		outLog.println("Exception in getUrl method"+e+"\n");
		outLog.flush();
		
	}
	
	return sbUrl.toString();
	
}

//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55205 - Start
/**
 * @param context
 * @param strUserName
 * @param keyCertification
 * @param keyCertification
 * @return
 */

	public String getFinalCertiNames(Context context, String strUserName, String keyCertification, Map<String, Object> mpMEPSEPCertiRow)   {
		
		String strObjID = (String) mpMEPSEPCertiRow.get("id");
		String strFinalCertiNames = DomainConstants.EMPTY_STRING;
		
		try {
			//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55935  - Start
			if(keyCertification.equalsIgnoreCase(PG_PMP_CERTIFICATION_NAME)) {
				if(mpMEPSEPCertiRow.get(keyCertification) instanceof StringList){
					strFinalCertiNames = mpMEPSEPCertiRow.get(keyCertification).toString().replace("[","").replace("]","");
				} else {
					strFinalCertiNames = (String) mpMEPSEPCertiRow.get(keyCertification);
				}
			} else {
				//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55935  - end
				StringBuilder  sbCertiNames = new StringBuilder();
				if(UIUtil.isNotNullAndNotEmpty(strObjID) && UIUtil.isNotNullAndNotEmpty(strUserName)) {
					pgFamilyCareReport pgFamilyCare = new pgFamilyCareReport(context, null);
					boolean bAccess = pgFamilyCare.accessCheck(context,strUserName,strObjID); 
					if(!bAccess) { 
						if((mpMEPSEPCertiRow.get(keyCertification) instanceof StringList)){
							StringList slCertiNames = (StringList) mpMEPSEPCertiRow.get(keyCertification);
							for (int i = 0; i<slCertiNames.size();i++){
								if(UIUtil.isNotNullAndNotEmpty(slCertiNames.get(i))) {
									sbCertiNames.append(pgV3Constants.NO_ACCESS);
									sbCertiNames.append(pgV3Constants.SYMBOL_PIPE);
								}
							}
							sbCertiNames.deleteCharAt(sbCertiNames.length()-1);
							strFinalCertiNames = sbCertiNames.toString();
						} else {
							strFinalCertiNames = pgV3Constants.NO_ACCESS;
						}
					} else {
						if(mpMEPSEPCertiRow.get(keyCertification) instanceof StringList){
							strFinalCertiNames = mpMEPSEPCertiRow.get(keyCertification).toString().replace("[","").replace("]","");
						} else {
							strFinalCertiNames = (String) mpMEPSEPCertiRow.get(keyCertification);
						}	
					}
				}
			}
		//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55935  - Start
		if(UIUtil.isNullOrEmpty(strFinalCertiNames)) {
			strFinalCertiNames= DomainConstants.EMPTY_STRING;
		}
		//Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55935  - end
		}catch(Exception e) {
			outLog.write("Exception in getFinalCertiNames "+e+"\n");
	    	outLog.flush();
		}
		return strFinalCertiNames;
}
    //Added by DSM Reports (Sogeti) for 2022x.4 (DEC CW 2023) Defect 55205 - End
	
	/**
 * Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 54465 - Ends
 * @param context
 * @param strType
 * @param strPartName
 * @param strOriginatingSource
 * @return
 */
public MapList getMEPSEPforSpecReaderEBPUser(Context context, String strType, String strPartName, String strOriginatingSource) {
	
	boolean isContextPushed = false;
	MapList mlTempPartRelease = null;
	MapList mlPartRelease = null;
	StringList slSelect = new StringList();
	slSelect.add(DomainConstants.SELECT_ID);	
	slSelect.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);	
	slSelect.add(DomainConstants.SELECT_CURRENT);
	slSelect.add(DomainConstants.SELECT_NAME);	
	slSelect.add(DomainConstants.SELECT_TYPE);
	slSelect.add(DomainConstants.SELECT_REVISION);
	slSelect.add(DomainConstants.SELECT_POLICY);
	boolean isMEPSEPPart = false;
	StringBuffer sbTypePattern = new StringBuffer();
	String strPolicy = "";
	
	
	try {
	if(!pgV3Constants.PERSON_USER_AGENT.equalsIgnoreCase(context.getUser()) && SPECREADER.equalsIgnoreCase(strOriginatingSource)) {
		ContextUtil.pushContext(context,PropertyUtil.getSchemaProperty(context,"User Agent"),DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
		isContextPushed = true;
		mlTempPartRelease = DomainObject.findObjects(context,strType,strPartName,"*","*",pgV3Constants.VAULT_ESERVICEPRODUCTION,"(current==Release)",false,slSelect);
		mlTempPartRelease.sort(DomainConstants.SELECT_REVISION, "descending", "string");
		if(!mlTempPartRelease.isEmpty()) {
			for(int i=0;i<mlTempPartRelease.size();i++) {
				Map mp = (Map) mlTempPartRelease.get(i);
				strPolicy = (String) mp.get(DomainConstants.SELECT_POLICY);
				if(pgV3Constants.POLICY_MANUFACTUREREQUIVALENT.equals(strPolicy) || 
						pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equals(strPolicy)) {
					isMEPSEPPart = true;
					break;
				}
			}
		}
	}
	if(isMEPSEPPart) {
		mlPartRelease = mlTempPartRelease;
	}else {
		mlPartRelease = new MapList();
	}
	
	}catch(Exception e) {
		outLog.println("Exception in getMEPSEPforSpecReaderEBPUser method"+e+"\n");
		outLog.flush();
	}finally {
		if(isContextPushed) {
			try {
				ContextUtil.popContext(context);
				isContextPushed = false;
			}catch(Exception e) {
				outLog.println("Exception while popping context in getMEPSEPforSpecReaderEBPUser method"+e+"\n");
				outLog.flush();
			}
			
			
	}
	
}
	return mlPartRelease;
}
//Added by DSM Report (Sogeti) for 22x.04 (December CW 2023) Defect 54465 - Ends

//Added for 22x_05 Defect 56387 ---- START
protected MapList getRollupPostConsumerValue(Context context, MapList mlMaterialsAndCompositions) throws Exception {
	BigDecimal dDefaultValue = BigDecimal.valueOf(0.0);
	BigDecimal dDivideByHundred = BigDecimal.valueOf(100.0);
	BigDecimal dFinalPostRecycledContent = dDefaultValue;
	BigDecimal dFinalPostIndustrialRecycledContent = dDefaultValue;
	
	String INDUSTRIALCYCLATEATTRIBUTESELECT = "attribute_PercentPostIndustrialRecyclate";
	String postIndustrialRecyclate = PropertyUtil.getSchemaProperty(context,INDUSTRIALCYCLATEATTRIBUTESELECT);
	String ATTRIBUTE_INDUSTRIALRECYCLATE = "attribute[" + postIndustrialRecyclate  + "].inputvalue";
	
	for(int i=0; i< mlMaterialsAndCompositions.size(); i++) {
		Map mpMaterialsAndCompositions = (Map) mlMaterialsAndCompositions.get(i);
		BigDecimal dTargetPercent = dDefaultValue;
		String strTargetPercentage = DomainConstants.EMPTY_STRING;
		BigDecimal dPostRecycledContent = dDefaultValue;
		BigDecimal dPostIndustrialRecycledContent = dDefaultValue;
		String strPostRecycledContent = DomainConstants.EMPTY_STRING;
		String strPostIndustrialRecycledContent = DomainConstants.EMPTY_STRING;
		BigDecimal dTempPostRecycledContent = dDefaultValue;
		BigDecimal dTempPostIndustrialRecycledContent = dDefaultValue;
		
		if(!mpMaterialsAndCompositions.containsKey(ISPARENT) && mpMaterialsAndCompositions.containsKey(DomainConstants.SELECT_LEVEL) && Integer.parseInt((String)mpMaterialsAndCompositions.get(DomainConstants.SELECT_LEVEL))  != 1 ) {
			continue;
		}
		
		strTargetPercentage=(String)mpMaterialsAndCompositions.get(pgV3Constants.ATTRIBUTE_QUANTITY);
		
		if (UIUtil.isNotNullAndNotEmpty(strTargetPercentage))
		{
			dTargetPercent = new BigDecimal(strTargetPercentage);
			dTargetPercent = dTargetPercent.divide(dDivideByHundred);
		}
		strPostRecycledContent = (String)mpMaterialsAndCompositions.get(pgV3Constants.SELECT_ATTRIBUTE_PGPOSTCONSUMERRECYCLEDCONTENT+STRINPUTVALUE);
		strPostIndustrialRecycledContent = (String)mpMaterialsAndCompositions.get(ATTRIBUTE_INDUSTRIALRECYCLATE);
		
		if(UIUtil.isNotNullAndNotEmpty(strPostRecycledContent))
		{
			dPostRecycledContent = new BigDecimal(strPostRecycledContent);  
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strPostIndustrialRecycledContent))
		{
			dPostIndustrialRecycledContent = new BigDecimal(strPostIndustrialRecycledContent);  
		}
		
		dTempPostRecycledContent = dPostRecycledContent.multiply(dTargetPercent);
		dTempPostIndustrialRecycledContent = dPostIndustrialRecycledContent.multiply(dTargetPercent);
		
		
		dFinalPostRecycledContent = dFinalPostRecycledContent.add(dTempPostRecycledContent);
		dFinalPostIndustrialRecycledContent = dFinalPostIndustrialRecycledContent.add(dTempPostIndustrialRecycledContent);
		
	}
	
	for(int i=0; i< mlMaterialsAndCompositions.size(); i++) {
		Map mpMaterialsAndCompositions = (Map) mlMaterialsAndCompositions.get(i);
		if( mpMaterialsAndCompositions.containsKey(ISPARENT) ) {
			mpMaterialsAndCompositions.put(STRCONSUMERCYCLATE, dFinalPostRecycledContent.toString());
			mpMaterialsAndCompositions.put(STRINDUSTRIALCYCLATE, dFinalPostIndustrialRecycledContent.toString());
			break;
		}
	}
	return mlMaterialsAndCompositions;
}
//Added for 22x_05 Defect 56387 ---- END

//Added by DSM Reports (Sogeti) for 2022x.6 --- Defect#57760 -- START  
protected String getDryPercentrageForInternalMaterial(Context context, DomainObject domContextPart) {
	String strTotalQuantytites = DomainConstants.EMPTY_STRING;
	StringList slQuantities;
	int iQuantityListSize = 0;
	BigDecimal dQuantites = BigDecimal.valueOf(0.0);
	slQuantities = getNonContaminantQuantities(context,domContextPart);
	
	iQuantityListSize = slQuantities.size();
	for(int j=0; j<iQuantityListSize; j++){
		dQuantites = dQuantites.add(new BigDecimal((String)slQuantities.get(j)));   
	}
	if(dQuantites.compareTo(BigDecimal.valueOf(0.00))!=0){
		strTotalQuantytites = dQuantites.toPlainString();	
	}
	return strTotalQuantytites;
}
	
	/**
	 * 2015x.5.1 - ALM - 17451 - IMP is not summing correctly to 100% (it is including the contaminants maxes as well) 
	 * It gets the quantity values of non contaminant substance.
	 * @param context
	 * @param domContextPart
	 * @return
	 */
	private StringList getNonContaminantQuantities(Context context,
		DomainObject domContextPart) {
		final String RELATIONSHIP_COMPONENT_SUBSTANCE = PropertyUtil.getSchemaProperty("relationship_ComponentSubstance");
		final String ATTR_IS_CONTAMINANT = PropertyUtil.getSchemaProperty ("attribute_IsContaminant");
		final String ATTR_QUANTITY = PropertyUtil.getSchemaProperty ("attribute_Quantity");
		final String TYPE_SUBSTANCE = PropertyUtil.getSchemaProperty("type_Substance");
		
		StringList slQuantList = new StringList();
		try {
			StringList relSelects = new StringList(2);
			relSelects.add(DomainObject.getAttributeSelect(ATTR_IS_CONTAMINANT));
			relSelects.add(DomainObject.getAttributeSelect(ATTR_QUANTITY));
			MapList mlQuantList = domContextPart.getRelatedObjects(context,
					RELATIONSHIP_COMPONENT_SUBSTANCE,
					TYPE_SUBSTANCE,
					new StringList(DomainConstants.SELECT_ID),
					relSelects,
					false, 
					true, 
					(short)1, 
					null, 
					null, 
					0);
			if(mlQuantList!= null && mlQuantList.size()>0) {
				Map quantObjMap = null;
				String isContaminant;
				for(Object quantObj:mlQuantList) {
					quantObjMap = (Map)quantObj;					
					isContaminant = (String)quantObjMap.get(DomainObject.getAttributeSelect(ATTR_IS_CONTAMINANT));
					if(!"TRUE".equalsIgnoreCase(isContaminant)) {
						slQuantList.add((String)quantObjMap.get(DomainObject.getAttributeSelect(ATTR_QUANTITY)));
					}
				}
			}
			
		}catch(Exception e) {
			outLog.print("Exception in pgPartSpecReport :: getNonContaminantQuantities method"+e+"\n");
			outLog.flush();
		}
		return slQuantList;
	}
//Added by DSM Reports (Sogeti) for 2022x.6 --- Defect#57760 -- END

//Added by DSM for 22x CW-05 for Requirement 49721  -changes for DSM Reports MOS Components - START

public MapList getMarketOfSaleDetail(Context context, DomainObject dobjPart, Map mpCommonColumnsDetail, PrintWriter outlog) throws FrameworkException {
	MapList returnMap = new MapList();
	try {
		StringList slSelectables = new StringList();
		slSelectables.add(DomainConstants.SELECT_NAME);
		slSelectables.add(DomainConstants.SELECT_REVISION);
		slSelectables.add(DomainConstants.SELECT_ATTRIBUTE_TITLE);
		slSelectables.add(DomainConstants.SELECT_TYPE);
		slSelectables.add(STR_COUNTRYRESTRICTIONINFO);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
		
		List configList = new MapList();
		
		for(String eachSelects : slSelectables) {
			Map hm = new HashMap();
			if(eachSelects.equalsIgnoreCase(STR_COUNTRYRESTRICTIONINFO)) {
				hm.put("selecttype", "programhtml");
				hm.put("field", STR_COUNTRY);
			} else {
				hm.put("selecttype", "businessobject");
			}
			hm.put("selectable", eachSelects);
			configList.add(hm);
		}
		
		
		Map confMap = new HashMap();
		confMap.put("columnconfig",configList);
		
		List tableConfigList = new MapList();
		tableConfigList.add(confMap);
		
        HashMap<String, List> paramMap = new HashMap<>();
        paramMap.put("tableconfig", tableConfigList);
		
        
        com.pg.v4.util.mos.ui.MosComponentAppUtils mosObj = new MosComponentAppUtils(context, dobjPart.getId());
        Map returnedMap = mosObj.getMarketOfSaleDetails(JPO.packArgs(paramMap));
        
        returnMap.addAll((MapList) returnedMap.get("objectList"));
        
        MapList duplicateList = new MapList();
        Map<String, LinkedList<Map<String, Object>>> mapByNameValue = new HashMap<>();
        for(Object eachList : returnMap) {
        	Map partMap = (Map) eachList;
        	String value = (String) partMap.get(STR_COUNTRY);
                if(UIUtil.isNotNullAndNotEmpty(value)) {
                	value = value.replace("</br>", ",");
                	value = value.replace("<span id='restricted'>", "");
                	value = value.replace("</span>", "");
                }
                partMap.put(STR_COUNTRY, value);
            	partMap.put(COMMONCOLUMNS, mpCommonColumnsDetail);
            	List<String> nameValues = partMap.containsKey("nameValue") ? (List<String>) partMap.get("nameValue") : (new StringList("Parent")) ;
            	if (null != nameValues && !nameValues.isEmpty()) {
            		String firstValue = nameValues.get(0);
            		mapByNameValue.computeIfAbsent(firstValue, k -> new LinkedList<>()).add(partMap);    
            	}
        }

        returnMap.clear();
        for (List<Map<String, Object>> maps : mapByNameValue.values()) {
        	returnMap.addAll(maps);
        }
        
        Map parentMap = getParentDetails(context, mpCommonColumnsDetail, outlog);
        returnMap.add(0, parentMap);
        
        for(Object eachMap : returnMap) {
        	Map mp = (Map)eachMap;
        	if(!duplicateList.contains(mp)) {
        		duplicateList.add(mp);
        	}
        }
        returnMap.clear();
        returnMap.addAll(duplicateList);
	} catch (Exception e) {
		outLog.print("Exception in getMarketOfSaleDetail  - MOS Component Details "+e+"\n");
		outLog.flush();
	//	e.printStackTrace();
	} 
	return returnMap;
}


/**
 * @param context
 * @param objIPSProduct
 * @param mpCommonColumnsDetail
 * @return
 * @throws FrameworkException
 */
private Map getParentDetails(Context context, Map mpCommonColumnsDetail, PrintWriter outlog) throws FrameworkException {
	Map parentCountriesMp;	
	String parentId = DomainConstants.EMPTY_STRING;
	String parentCountries = DomainConstants.EMPTY_STRING;
	String pgMOSPOAOverrideLRR = DomainConstants.EMPTY_STRING;
	
	if(null != mpCommonColumnsDetail.get(DomainConstants.SELECT_ID))
		parentId = (String) mpCommonColumnsDetail.get(DomainConstants.SELECT_ID);
	
	if(UIUtil.isNotNullAndNotEmpty(parentId)) {
		DomainObject dobj = DomainObject.newInstance(context, parentId);
		parentCountriesMp = getParentCountries(context, dobj, outlog);
		
		parentCountries = (String) parentCountriesMp.get("PARENTCOUNTRIES");
		pgMOSPOAOverrideLRR = (String) parentCountriesMp.get("PGMOSFPPOVERRIDDEN");
	}
		

	HashMap parentMap = new HashMap();
	parentMap.put(COMMONCOLUMNS, mpCommonColumnsDetail);
	parentMap.put(STR_COUNTRY, parentCountries);
	parentMap.put(SELECT_ATTRIBUTE_PGMOSFPPOVERRIDDEN, pgMOSPOAOverrideLRR);
	
	return parentMap;	
}

/**
 * @param context
 * @param strIPS
 * @return
 * @throws FrameworkException
 */
private Map getParentCountries(Context context, DomainObject dobj, PrintWriter outlog) throws FrameworkException {
	Map parentCountryDetails = new HashMap();
	StringBuffer parentCountries = new StringBuffer();
	StringList objSelect = new StringList(2);
	objSelect.add(DomainConstants.SELECT_NAME);
	objSelect.add(DomainConstants.SELECT_ID);
	
	StringList relSelect = new StringList(2);
	relSelect.add(SELECT_ATTRIBUTE_PGMOSFPPOVERRIDDEN);
	relSelect.add(SELECT_ATTRIBUTE_PGCOSRESTRICTION);
	
	String eachStrPOAOverride = DomainConstants.EMPTY_STRING;
	
	try {
		MapList mlCountry = dobj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE, pgV3Constants.TYPE_COUNTRY, objSelect,
				relSelect, true, true, (short) 1, "", "");
		mlCountry.sort(DomainConstants.SELECT_NAME, "ascending", "String");
		
		for(Object eachCountryObj : mlCountry) {
			Map eachCountryMap = (Map) eachCountryObj;
			String eachStrCountry = (String) eachCountryMap.get(DomainConstants.SELECT_NAME);
			String eachStrRestriction = (String) eachCountryMap.get(SELECT_ATTRIBUTE_PGCOSRESTRICTION);
			
			if(UIUtil.isNullOrEmpty(eachStrPOAOverride)) {
				eachStrPOAOverride = (String) eachCountryMap.get(SELECT_ATTRIBUTE_PGMOSFPPOVERRIDDEN);
			}
			
			if(null != eachStrCountry && UIUtil.isNotNullAndNotEmpty(eachStrCountry)) {
				parentCountries.append((String) eachCountryMap.get(DomainConstants.SELECT_NAME));
				if(null != eachStrRestriction && UIUtil.isNotNullAndNotEmpty(eachStrRestriction)) {
					parentCountries.append("|");
					parentCountries.append(eachStrRestriction);
				}
				parentCountries.append(",");
			}
			

		}
	    if (parentCountries.length() > 0) {
	    	parentCountries.setLength(parentCountries.length() - 1);
	    }		
	    
		if(parentCountries.length() == 0) {
			parentCountries.append("No Market");
		}
		parentCountryDetails.put("PARENTCOUNTRIES", parentCountries.toString());
		parentCountryDetails.put("PGMOSFPPOVERRIDDEN", eachStrPOAOverride);
	} catch(Exception e) {
		outLog.println("Exception in getParentCountries  - MOS Component Details method"+e+"\n");
		outLog.flush();
	}

	
	return parentCountryDetails;	
}

private static String updateValueToYesNo(String value) {
	if(UIUtil.isNotNullAndNotEmpty(value))
		value = "TRUE".equalsIgnoreCase(value) ? "Yes" : "No";
	return value;
}

//Added by DSM for 22x CW-05 for Requirement 49721  -changes for DSM Reports MOS Components - END
}
