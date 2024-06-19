import java.util.HashMap;
import java.util.List;
import java.util.Map;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

//JPO Compilation Fixing 2015XUPGRADE START
//com.matrixone.apps.cpn.customcpn.* available in cpnaccelerator.jar changed to com.matrixone.apps.cpn.*
//import com.matrixone.apps.cpn.customcpn.CPNCommonConstants;
import com.matrixone.apps.cpn.CPNCommonConstants;
//JPO Compilation Fixing 2015XUPGRADE END
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainRelationship;
import com.pg.v3.custom.pgV3Constants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;

/**
 *
 * @author bdukare
 *
 */
public class pgIPMPDFViewDataSelectable_mxJPO implements CPNCommonConstants
{
	//DSM(DS) Upgrade -JIRA:JPO-2: CA Signature ignored in In Approval state: Start
	public static final String STR_RELEASE_PHASE = PropertyUtil.getSchemaProperty("attribute_ReleasePhase");
	public static final String SELECT_ATTRIBUTE_RELEASE_PHASE  = (new StringBuilder()).append("attribute[").append(STR_RELEASE_PHASE).append("]").toString();
	//Added for 2018x Upgrade Ends  - Stage attribute is replaced by Relese Phase. Hence added this line to select the Release phase value.
	
	/**
	 *
	 * @param context
	 * @return
	 */
	public static StringList getBasicInfoBusSelectable(Context context)
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(12);
		busSelect.addElement(DomainConstants.SELECT_ID);
		
		busSelect.addElement(DomainConstants.SELECT_TYPE);
		busSelect.addElement(DomainConstants.SELECT_NAME);
		busSelect.addElement(DomainConstants.SELECT_REVISION);
		busSelect.addElement(DomainConstants.SELECT_DESCRIPTION);
		busSelect.addElement(DomainConstants.SELECT_OWNER);
		busSelect.addElement(DomainConstants.SELECT_CURRENT);
		busSelect.addElement(DomainConstants.SELECT_POLICY);
		busSelect.addElement(DomainConstants.SELECT_MODIFIED);
		busSelect.addElement(DomainConstants.SELECT_ORIGINATOR);
		//Modified by V4-2013x.4 for PDF Views - Starts
		busSelect.addElement(DomainConstants.SELECT_ORIGINATED);
		busSelect.addElement(DomainConstants.SELECT_VAULT);
		//Modified by V4-2013x.4 for PDF Views - Ends
		
		return busSelect;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static StringList getBasicInfoRelationshipSelectable(Context context)
	{
		StringList relSelect = new StringList(5);
		relSelect.addElement(DomainRelationship.SELECT_ID);
		relSelect.addElement(DomainRelationship.SELECT_TYPE);
		relSelect.addElement(DomainRelationship.SELECT_NAME);
		relSelect.addElement(DomainConstants.SELECT_DESCRIPTION);
		relSelect.addElement(DomainConstants.SELECT_MODIFIED);
		return relSelect;
	}
	
	public static StringList getKeyList() {
		StringList slCommonList = new StringList(30);
		slCommonList.addElement(pgPDFViewConstants.PARTSPEC);
		slCommonList.addElement(pgPDFViewConstants.PARTSPECSPS);
		slCommonList.addElement(pgPDFViewConstants.PRINTINGPROSS);
		slCommonList.addElement(pgPDFViewConstants.REGIONOWNS);
		slCommonList.addElement(pgPDFViewConstants.COOWNED);
		slCommonList.addElement(pgPDFViewConstants.CLASSIFIED);
		slCommonList.addElement(pgPDFViewConstants.PGBATTERYTYPE);
		slCommonList.addElement(pgPDFViewConstants.PGLIFECYCLE);
		slCommonList.addElement(pgPDFViewConstants.ORIGINATINGTEMPLATE);
		slCommonList.addElement(pgPDFViewConstants.AFFECTEDITEM);	
		slCommonList.addElement(pgPDFViewConstants.CHANGEAFFECTEDITEM);
		slCommonList.addElement(pgPDFViewConstants.CHANGEORDER);
		slCommonList.addElement(pgPDFViewConstants.PGBRAND);
		slCommonList.addElement(pgPDFViewConstants.PGCOUNTRY);
		slCommonList.addElement(pgPDFViewConstants.MATERIALGROUP);
		slCommonList.addElement(pgPDFViewConstants.BOMBASEQTY);
		slCommonList.addElement(pgPDFViewConstants.PGPDTTOPGPLIREFUN);
		slCommonList.addElement(pgPDFViewConstants.PGPDTTOPGPLICLASS);
		slCommonList.addElement(pgPDFViewConstants.PGPLIUS);
		slCommonList.addElement(pgPDFViewConstants.SHIPPINGHC);
		slCommonList.addElement(pgPDFViewConstants.MATERIALCERTIFICATION);
		slCommonList.addElement(pgPDFViewConstants.INTENDEDMARKET);
		slCommonList.addElement(pgPDFViewConstants.SEGMENT);
		slCommonList.addElement(pgPDFViewConstants.PRODUCTCATEGORYPLATFORM);
		slCommonList.addElement(pgPDFViewConstants.PGBUSINESSAREA);
		slCommonList.addElement(pgPDFViewConstants.FRANCHISE);
		slCommonList.addElement(pgPDFViewConstants.TECHNOLOGYPLATFORM);
		slCommonList.addElement(pgPDFViewConstants.TECHNOLOGYCHASSIS);
		slCommonList.addElement(pgPDFViewConstants.PRIMARYPACKAGINGTYPE);
		
		return slCommonList;
	}
	
	public static StringList getKeySelectList() {
		StringList slFirstList = new StringList(5);
		slFirstList.addElement(pgPDFViewConstants.PGPACKAGINGTECHNOLOGY);
		slFirstList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGHAZARDCLASSIFICATION);
		slFirstList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINTENDEDMARKETS);
		slFirstList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDANGERCATEGORY);
		slFirstList.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSAFETYSYMBOL);
		
		return slFirstList;
	}
	
	/**
	 *
	 * @param context
	 * @return
	 */
	//Added by DSM-2018x.6 for PDF Views Req#36434,36465,38203,36346,38412 - Starts
	public static StringList getTypeDataBusSelectable(Context context, String type) {
		StringList busSelect= new StringList();
		
		switch(type.trim()) {
			case pgPDFViewConstants.TYPE_PGCUSTOMERUNITPART:
				busSelect.addAll(getCustomerUnitPartSelectable());
				break;
			case pgPDFViewConstants.TYPE_PGMASTERPACKAGINGMATERIALPART:
				busSelect.addAll(getMasterPackagingMaterialPartSelectable());
				break;
			case pgPDFViewConstants.TYPE_PGCONSUMERUNITPART:
				busSelect.addAll(getConsumerUnitPartSelectable());
				break;
			case pgPDFViewConstants.TYPE_DEVICEPRODUCTPART:
				busSelect.addAll(getDeviceProductPartSelectable());
				break;
			case pgPDFViewConstants.TYPE_PGASSEMBLEDPRODUCTPART:
				busSelect.addAll(getAssembledProductPartSelectable());
				break;
			//Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - Start
			case pgPDFViewConstants.TYPE_PGFORMULATIONPART:
				busSelect.addAll(getFormulationPartSelectable());
				break;
			//Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - End
			default:
				break;
		}
		

		return busSelect;
	}
	/**
	 * 
	 * @return
	 */
	 //Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - Start
	 private static StringList getFormulationPartSelectable() {
		StringList busSelect= new StringList();
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		return busSelect;
	}
	//Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - End
	/**
	 * 
	 * @return
	 */
	private static StringList getAssembledProductPartSelectable() {
		StringList busSelect= new StringList();
		busSelect.add(pgPDFViewConstants.SELECT_CONN_PNGIASSEMBLED_PNGLPDMODELTYPE);
		busSelect.add(pgPDFViewConstants.SELECT_CONN_PNGIASSEMBLED_PNGDEFINITION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		return busSelect;
	}
	/**
	 * 
	 * @return
	 */
	private static StringList getDeviceProductPartSelectable() {
		StringList busSelect= new StringList();
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGNUMBEROFBATTERIESREQUIRED);
		//Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - Start
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		//Added by DSM Sogeti for 2022x.06 CW for PDF Views (Req -49517) - End
		return busSelect;
	}
	/**
	 * 
	 * @return
	 */
	private static StringList getConsumerUnitPartSelectable() {
		StringList busSelect= new StringList();
			busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME);
		return busSelect;
	}
	/**
	 * 
	 * @return
	 */
	private static StringList getMasterPackagingMaterialPartSelectable() {
		StringList busSelect= new StringList();
			busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPOAIMPACTCONFIRMATION);
		return busSelect;
	}
	/**
	 * 
	 * @return
	 */
	private static StringList getCustomerUnitPartSelectable() {
		StringList busSelect= new StringList();
			busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPACKAGINGTYPE);
		return busSelect;
	}
//Added by DSM-2018x.6 for PDF Views Req#36434,36465,38203,36346 - Ends	
	//Added by DSM-2018x.6 for PDF Views Req#36434: Ends
	/**
	 *
	 * @param context
	 * @return
	 */
	public static List getCommonDataBusSelectable(Context context)
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		List busSelect= new StringList(126);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		busSelect.add(DomainConstants.SELECT_ORIGINATOR);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		//Added by DSM(Sogeti)-2015x.2  for Defect ID- 9372 - Starts
		busSelect.add("to["+ pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE +"].from.attribute["+ pgV3Constants.ATTRIBUTE_FORMULATIONTYPE +"]");
		//Added by DSM(Sogeti)-2015x.4  for Req -11515- Starts
		busSelect.add("to["+ pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE +"].from.name");
		//Added by DSM(Sogeti)-2015x.4  for Req -11515- Ends
		//Added by DSM(Sogeti)-2015x.2  for Defect ID- 9372 - Ends 		
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVECOMMENT);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_REASONFORCHANGE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEREASON);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISTEMPLATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGLASTUPDATEUSER);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGLOCALDESCRIPTION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERNAMES);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCOCREATORS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERDISTRIBUTIONLIST);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGELECTRONICDISTRIBUTION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGUNARCHIVEREASON);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGUNARCHIVEDATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSEGMENT);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCATEGORY);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGGLOBALFORM);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGREGION);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGAREA);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSECTOR);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSUBSECTOR);
		busSelect.add("to["+pgV3Constants.RELATIONSHIP_AFFECTEDITEM+"].from.attribute["+pgV3Constants.ATTRIBUTE_PGCHANGEAPPROVER+"]");
		busSelect.add("to["+pgV3Constants.RELATIONSHIP_DESIGNRESPONSIBILITY+"].from.name");
		busSelect.add("to["+pgV3Constants.RELATIONSHIP_DESIGNRESPONSIBILITY+"].from.id");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
		//busSelect.add("attribute[pgProjectSecurityGroup]");
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSECURITYSTATUS);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCLASSIFICATION);
		//Added by DSM(Sogeti)-2015x.2  for Defect ID- 11058 starts
		busSelect.add("from["+pgV3Constants.RELATIONSHIP_PGTOSHIPPINGHAZARDCLASSSIFICATION+"].to.name");
		//Added by DSM(Sogeti)-2015x.2  for Defect ID- 11058 - Ends
		//Added by DSM(Sogeti)-2018x.2  for Req Id - 11471 - Starts.
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGDOESDEVICECONTAINFLAMMABLELIQUID);
		//Added by DSM(Sogeti)-2018x.2  for Req Id - 11471 - Ends.
		//Added by DSM(Sogeti)-2018x.2 for Req Id - 11470 - Starts
		busSelect.add(DomainConstants.ATTRIBUTE_DESCRIPTION);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PQUNNUMBER);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPROPERSHIPPINGNAME);
		busSelect.add("from[" + pgPDFViewConstants.RELATIONSHIP_PQHAZARDCLASS + "].to.name");
		busSelect.add("from[" + pgPDFViewConstants.RELATIONSHIP_PQPACKINGGROUP + "].to.name");
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PQSHIPMENTLIMITEDQUANTITY);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PQCONSUMERWEIGHTVOLUME);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PQCUSTOMERWEIGHTVOLUME);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PQDGLABELREQUIREMENTS);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PQDGCOPLABEL_REQUIRED);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PQDGCUPLABEL_REQUIRED);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
		//Added by DSM(Sogeti)-2018x.2 for Req Id - 11470 - Ends
		//Added by DSM(Sogeti)-2018x.3 for Req (5691, 5759, 13691) - Starts
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGREPLACEDPRODUCTNAME);
		//Added by DSM(Sogeti)-2018x.3 for Req (5691, 5759, 13691) - Ends
		//Added by DSM(Sogeti)-2018x.3 for Req (5696, 5699, 7554 , 5698) - Starts
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGMATERIALTOPGPLIENVIRONMENTALCLASS + "].to.name");
		//Added by DSM(Sogeti)-2018x.3 for Req (5696, 5699, 7554 , 5698) - Ends
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		//busSelect.add(pgPDFViewConstants.ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		busSelect.add(pgPDFViewConstants.FRANCHISE);
		busSelect.add(pgPDFViewConstants.PRODUCTCATEGORYPLATFORM);
		busSelect.add(pgPDFViewConstants.BUSINESSAREA);
		busSelect.add(pgPDFViewConstants.TECHNOLOGYPLATFORM);
		busSelect.add(pgPDFViewConstants.TECHNOLOGYCHASSIS);
		
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGHASART);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGHASMULTIPLEGTINS);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPREFERREDMATERIAL);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGLEGACYWEIGHTFACTOR);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGLEGACYWEIGHTFACTORUOM);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED);
		//Added by DSM for PDF Views (Req id #47506) - Start
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSTRUCTUREDATSWHYWHYANALYSIS);
		//Added by DSM for PDF Views (Req id #47506) - End
		//Added by DSM for PDF Views (Req id #49326) - Start
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSTRUCTUREDPERFORMANCECHARACTERISTICSREQUIRED);
		//Added by DSM for PDF Views (Req id #49326) - End
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTANDARDCOST);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZE);
		busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM);
		
		
		
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGOVERHANGACTUALLENGTH);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGOVERHANGACTUALWIDTH);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGUNDERHANGACTUALLENGTH);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGUNDERHANGACTUALWIDTH);

		//Added by DSM 2018x.5 Requirement 32405 - Start
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPROJECTMILESTONE);
		//Added by DSM 2018x.5 Requirement 32405 - End
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #34470 : Starts
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGMATERIALRESTRICTION);
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGMATERIALRESTRICTIONCOMMENT);
		//Added by DSM(Sogeti)-2018x.5 for PDF Views Defect #34470 : Ends
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		busSelect.add("to[" + pgPDFViewConstants.RELATIONSHIP_CLASSIFIEDITEM + "].from.name");	
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		//Added by DSM-2018x.6_Oct CW for PDF Views Req #40760: Start
		busSelect.add(pgPDFViewConstants.SELECT_ATTRIBUTE_PGTRANSPORTATIONTEMPCONTROL);
		//Added by DSM-2018x.6_Oct CW for PDF Views Req #40760: Start
		//Added by DSM Sogeti for 2022x.03 Aug CW for PDF Views (Req -47318) - Starts
		busSelect.add("from["+ pgV3Constants.RELATIONSHIP_PLANNEDFOR +"].to.attribute["+ pgPDFViewConstants.ATTRIBUTE_REFERENCE_IDENTIFIER +"]");
		//Added by DSM Sogeti for 2022x.03 Aug CW for PDF Views (Req -47318) - Ends
		return busSelect;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static List getRelationshippgSupersedesDataSelectable(Context context)
	{
		List busSelect = new StringList(13);
		busSelect.add("to[pgSupersedes].attribute[pgSupersedesOnDate]");
		
		busSelect.add("to[pgSupersedes].from.attribute[Title]");
		busSelect.add("to[pgSupersedes].from.name");
		busSelect.add("to[pgSupersedes].from.id");
		busSelect.add("to[pgSupersedes].from.revision");
		busSelect.add("to[pgSupersedes].from.type");
		busSelect.add("to[pgSupersedes].from.current");
		busSelect.add("from[pgSupersedes].attribute[pgSupersedesOnDate]");
		busSelect.add("from[pgSupersedes].to.attribute[Title]");
		busSelect.add("from[pgSupersedes].to.name");
		busSelect.add("from[pgSupersedes].to.id");
		busSelect.add("from[pgSupersedes].to.current");
		busSelect.add("from[pgSupersedes].to.revision");
		busSelect.add("from[pgSupersedes].to.type");
		return busSelect;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static List getRelationshipAuthorizedTemporarySpecificationDataSelectable(Context context)
	{
		List busSelect = new StringList(12);
		busSelect.add("from[Authorized Temporary Specification].to.type");
		busSelect.add("from[Authorized Temporary Specification].to.name");
		busSelect.add("from[Authorized Temporary Specification].to.id");
		busSelect.add("from[Authorized Temporary Specification].to.current");
		busSelect.add("from[Authorized Temporary Specification].to.revision");
		busSelect.add("from[Authorized Temporary Specification].to.attribute[Title]");
		busSelect.add("to[Authorized Temporary Specification].from.type");
		busSelect.add("to[Authorized Temporary Specification].from.current");
		busSelect.add("to[Authorized Temporary Specification].from.name");
		busSelect.add("to[Authorized Temporary Specification].from.id");
		busSelect.add("to[Authorized Temporary Specification].from.revision");
		busSelect.add("to[Authorized Temporary Specification].from.attribute[Title]");
		return busSelect;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static List getRelationshippgApprovedSupplierListDataSelectable(Context context)
	{
		List busSelect = new StringList(5);
		busSelect.add("from[pgApprovedSupplierList].to.type");
		busSelect.add("from[pgApprovedSupplierList].to.name");
		busSelect.add("from[pgApprovedSupplierList].to.id");
		busSelect.add("from[pgApprovedSupplierList].to.revision");
		busSelect.add("from[pgApprovedSupplierList].to.attribute[Title]");
		return busSelect;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static List getRelationshippgMasterDataSelectable(Context context)
	{
		List busSelect = new StringList(100);
		busSelect.add("from[pgMaster].to.last.id");
		busSelect.add("from[pgMaster].to.last.name");
		busSelect.add("from[pgMaster].to.last.current");
		busSelect.add("from[pgMaster].to.last.revision");
		busSelect.add("from[pgMaster].to.last.description");
		busSelect.add("from[pgMaster].to.last.attribute[Title]");
		busSelect.add("from[pgMaster].to.last.attribute[Release Date]");
		busSelect.add("from[pgMaster].to.last.attribute[Effectivity Date]");
		busSelect.add("from[pgMaster].to.last.attribute[Expiration Date]");
		busSelect.add("from[pgMaster].to.last.attribute[Status]");
		busSelect.add("from[pgMaster].to.last.attribute[pgLocalDescription]");
		busSelect.add("from[pgMaster].to.last.attribute[pgOtherNames]");
		busSelect.add("from[pgMaster].to.last.attribute[pgSecurityClassification]");
		busSelect.add("from[pgMaster].to.last.attribute[Reason for Change]");
		busSelect.add("from[pgMaster].to.last.attribute[pgBrandInformation]");
		busSelect.add("from[pgMaster].to.last.attribute[pgProductForm]");
		busSelect.add("from[pgMaster].to.last.attribute[pgMarketingSize]");
		busSelect.add("from[pgMaster].to.last.attribute[pgPackagingTechnology]");
		busSelect.add("from[pgMaster].to.last.attribute[pgShippingInstructions]");
		busSelect.add("from[pgMaster].to.last.attribute[pgLabelingInformation]");
		busSelect.add("from[pgMaster].to.last.attribute[pgStorageInformation]");
		busSelect.add("from[pgMaster].to.last.attribute[pgWarehousingClassification]");
		busSelect.add("from[pgMaster].to.last.attribute[pgStorageTemperatureLimits]");
		busSelect.add("from[pgMaster].to.last.attribute[pgStorageHumidityLimits]");
		busSelect.add("from[pgMaster].to.last.attribute[Comment]");
		busSelect.add("from[pgMaster].to.last.attribute[pgMaterialGroup]");
		busSelect.add("from[pgMaster].to.last.attribute[pgPreferredMaterial]");
		busSelect.add("from[pgMaster].to.last.attribute[pgIsTemplate]");
		busSelect.add("from[pgMaster].to.last.attribute[pgASLAllocation]");
		busSelect.add("from[pgMaster].to.last.attribute[pgArchiveDate]");
		busSelect.add("from[pgMaster].to.last.attribute[pgArchiveReason]");
		busSelect.add("from[pgMaster].to.last.attribute[pgArchiveComment]");
		busSelect.add("from[pgMaster].to.last.attribute[pgUnarchiveDate]");
		busSelect.add("from[pgMaster].to.last.attribute[pgUnarchiveReason]");
		busSelect.add("from[pgMaster].to.last.attribute[pgQuantityForProductIs]");
		busSelect.add("from[pgMaster].to.last.attribute[pgBOMBaseQuantity]");
		busSelect.add("from[pgMaster].to.last.attribute[pgEntryBaseQuantity]");
		busSelect.add("from[pgMaster].to.last.attribute[pgBaseUnitOfMeasure]");
		busSelect.add("from[pgMaster].to.last.attribute[pgPackagingLevel]");
		busSelect.add("from[pgMaster].to.last.attribute[pgMaterialOfConstruction]");
		
		busSelect.add("from[pgMaster].to.last.to[Authorized Temporary Specification]");
		busSelect.add("from[pgMaster].to.last.to[Authorized Temporary Specification].from.id");
		busSelect.add("from[pgMaster].to.last.to[Authorized Temporary Specification].from.name");
		busSelect.add("from[pgMaster].to.last.to[Authorized Temporary Specification].from.revision");
		busSelect.add("from[pgMaster].to.last.to[Authorized Temporary Specification].from.attribute[Title]");
		busSelect.add("from[pgMaster].to.last.to[Authorized Temporary Specification].from.current");
		
		busSelect.add("from[pgMaster].to.last.from[Authorized Temporary Specification]");
		busSelect.add("from[pgMaster].to.last.from[Authorized Temporary Specification].to.name");
		busSelect.add("from[pgMaster].to.last.from[Authorized Temporary Specification].to.revision");
		busSelect.add("from[pgMaster].to.last.from[Authorized Temporary Specification].to.id");
		busSelect.add("from[pgMaster].to.last.from[Authorized Temporary Specification].to.attribute[Title]");
		busSelect.add("from[pgMaster].to.last.from[Authorized Temporary Specification].to.current");
		
		busSelect.add("from[pgMaster].to.last.to[pgSupersedes].from.name");
		busSelect.add("from[pgMaster].to.last.to[pgSupersedes].from.id");
		busSelect.add("from[pgMaster].to.last.to[pgSupersedes].from.revision");
		busSelect.add("from[pgMaster].to.last.to[pgSupersedes].from.current");
		busSelect.add("from[pgMaster].to.last.to[pgSupersedes].from.attribute[Title]");
		busSelect.add("from[pgMaster].to.last.to[pgSupersedes].attribute[pgSupersedesOnDate]");
		
		busSelect.add("from[pgMaster].to.last.from[pgSupersedes].to.name");
		busSelect.add("from[pgMaster].to.last.from[pgSupersedes].to.id");
		busSelect.add("from[pgMaster].to.last.from[pgSupersedes].to.revision");
		busSelect.add("from[pgMaster].to.last.from[pgSupersedes].to.attribute[Title]");
		busSelect.add("from[pgMaster].to.last.from[pgSupersedes].to.current");
		busSelect.add("from[pgMaster].to.last.from[pgSupersedes].attribute[pgSupersedesOnDate]");
			busSelect.add("from[pgMaster].to.last.attribute[pgWDStatius]");
		busSelect.add("from[pgMaster].to.last.attribute[pgUnitOfMeasureSystem]");
		
		busSelect.add("from[pgMaster].to.last.to[pgDefinesMaterial].from.name");
		busSelect.add("from[pgMaster].to.last.to[pgDefinesMaterial].from.id");
		busSelect.add("from[pgMaster].to.last.to[pgDefinesMaterial].from.revision");
		busSelect.add("from[pgMaster].to.last.to[pgDefinesMaterial].from.current");
		busSelect.add("from[pgMaster].to.last.to[pgDefinesMaterial].from.attribute[Title]");
		busSelect.add("from[pgMaster].to.last.owner");
		busSelect.add("from[pgMaster].to.last.modified");
		busSelect.add("from[pgMaster].to.last.attribute[pgCoCreators]");
		busSelect.add("from[pgMaster].to.last.attribute[Originator]");
		busSelect.add("from[pgMaster].to.last.to[Design Responsibility].from.name");
		busSelect.add("from[pgMaster].to.last.to[Design Responsibility].from.id");
		busSelect.add("from[pgMaster].to.last.attribute[pgLastUpdateUser]");
		
		busSelect.add("from[pgMaster].to.last.attribute[pgElectronicDistribution]");
		busSelect.add("from[pgMaster].to.last.attribute[pgOtherDistributionList]");
		
		busSelect.add("from[pgMaster].to.last.attribute[pgRegion]");
		busSelect.add("from[pgMaster].to.last.attribute[pgArea]");
		busSelect.add("from[pgMaster].to.last.attribute[pgSector]");
		busSelect.add("from[pgMaster].to.last.attribute[pgSubSector]");
		busSelect.add("from[pgMaster].to.last.attribute[pgSegment]");
		
		busSelect.add("from[pgMaster].to.last.attribute[Security Classification]");
		
			busSelect.add("from[pgMaster].to.last.attribute[pgColorIndexNumber]");
		busSelect.add("from[pgMaster].to.last.attribute[pgValence]");
		busSelect.add("from[pgMaster].to.last.attribute[pgChemicalReactivity]");
		busSelect.add("from[pgMaster].to.last.attribute[pgCASNumber]");
		busSelect.add("from[pgMaster].to.last.attribute[pgEinecsElincsNumber]");
		busSelect.add("from[pgMaster].to.last.attribute[pgMaterialOrigin]");
		busSelect.add("from[pgMaster].to.last.attribute[pgMaterialFunction]");
		busSelect.add("from[pgMaster].to.last.attribute[pgChemicalGroup]");
		busSelect.add("from[pgMaster].to.last.attribute[pgMolecularFormula]");
		busSelect.add("from[pgMaster].to.last.attribute[pgIngredientClass]");
		busSelect.add("from[pgMaster].to.last.attribute[pgRPhrase]");
		busSelect.add("from[pgMaster].to.last.attribute[pgSPhrase]");
		busSelect.add("from[pgMaster].to.last.attribute[pgSafetySymbol]");
		busSelect.add("from[pgMaster].to.last.attribute[pgDangerCategory]");
		
		busSelect.add("from[pgMaster].to.last.attribute[pgPackagingComponentType]");
		busSelect.add("from[pgMaster].to.last.attribute[pgPackagingMaterialType]");
		busSelect.add("from[pgMaster].to.last.attribute[pgPackagingUnit]");
		busSelect.add("from[pgMaster].to.last.attribute[pgDecorationDetails]");
		busSelect.add("from[pgMaster].to.last.attribute[pgPackagingSize]");
		
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[Comment]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgEnvironmentalClass]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgPackingLevel]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgPostConsumerRecycleContent]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgTotalRecycleContent]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgVolumeArea]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgVolumeAreaUnitOfMeasure]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgWeight]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgComponent]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgQuantity]");
		busSelect.add("from[pgMaster].to.last.from[Characteristic].to.attribute[pgQuantityUnitOfMeasure]");
		busSelect.add("from[pgMaster].to.last.to[Region Owns].from.name");
		
		return busSelect;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static List getRelationshippgDefinesMaterialDataSelectable(Context context)
	{
		List busSelect = new StringList(12);
		busSelect.add("to[pgDefinesMaterial].from.type");
		busSelect.add("to[pgDefinesMaterial].from.name");
		busSelect.add("to[pgDefinesMaterial].from.id");
		busSelect.add("to[pgDefinesMaterial].from.revision");
		busSelect.add("to[pgDefinesMaterial].from.current");
		busSelect.add("to[pgDefinesMaterial].from.attribute[Title]");
		busSelect.add("from[pgDefinesMaterial].to.type");
		busSelect.add("from[pgDefinesMaterial].to.name");
		busSelect.add("from[pgDefinesMaterial].to.id");
		busSelect.add("from[pgDefinesMaterial].to.revision");
		busSelect.add("from[pgDefinesMaterial].to.current");
		busSelect.add("from[pgDefinesMaterial].to.attribute[Title]");
		
		return busSelect;
	}
		/**
	 *
	 * @param context
	 * @return
	 */
	public static List getRelationshippgCountryOfSalesDataSelect(Context context)
	{
		List busSelect = new StringList(6);
		busSelect.add("to[Country Of Sales].attribute[Production Restriction]");
		busSelect.add("to[Country Of Sales].from.description");
		busSelect.add("to[Country Of Sales].from.name");
		busSelect.add("to[Country Of Sales].from.id");
		busSelect.add("to[Country Of Sales].from.attribute[pgISOCode]");
		busSelect.add("to[Country Of Sales].from.attribute[pgTerminologyCode]");
		return busSelect;
	}
		
	public static List getCharacteristicTypeAttributeSelectable(Context context)
	{
		List busSelect = new StringList(12);
			busSelect.add("description");
		busSelect.add("attribute[Comment]");
		busSelect.add("attribute[pgEnvironmentalClass]");
		busSelect.add("attribute[pgPackingLevel]");
		busSelect.add("attribute[pgPostConsumerRecycleContent]");
		busSelect.add("attribute[pgTotalRecycleContent]");
		busSelect.add("attribute[pgVolumeArea]");
		busSelect.add("attribute[pgVolumeAreaUnitOfMeasure]");
		busSelect.add("attribute[pgWeight]");
		busSelect.add("attribute[pgComponent]");
		busSelect.add("attribute[pgQuantity]");
		busSelect.add("attribute[pgQuantityUnitOfMeasure]");
		return busSelect;
		}
		/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getRawMaterialSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(332);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		
		busSelect.addAll(getCommonDataBusSelectable(context));
		
		busSelect.add("attribute[pgLocalDescription]");
		busSelect.add("attribute[pgOtherNames]");
		busSelect.add("attribute[pgCategory]");
		//busSelect.add("attribute[pgProjectSecurityGroup]");
		busSelect.add("attribute[pgDangerCategory]");
		busSelect.add("attribute[pgSafetySymbol]");
		busSelect.add("attribute[pgRPhrase]");
		busSelect.add("attribute[pgSPhrase]");
		busSelect.add("attribute[pgIngredientClass]");
		busSelect.add("attribute[pgMolecularFormula]");
		busSelect.add("attribute[Authorized Temporary Specification]");
		busSelect.add("attribute[pgIsTemplate]");
		busSelect.add("attribute[Reason for Change]");
		busSelect.add("attribute[pgBaseUnitOfMeasure]");
		busSelect.add("attribute[pgIntermediateFlag]");
		busSelect.add("attribute[pgShelfLife]");
		busSelect.add("attribute[pgShippingHazardClassification]");
		busSelect.add("attribute[pgShippingInstructions]");
		busSelect.add("attribute[Comment]");
		busSelect.add("attribute[Status]");
		busSelect.add("attribute[pgValence]");
		busSelect.add("attribute[pgIsBattery]");
		busSelect.add("attribute[pgBatteryType]");
		busSelect.add("attribute[pgContainsBattery]");
		busSelect.add("attribute[pgPreferredMaterial]");
		busSelect.add("attribute[pgMaterialGroup]");
		busSelect.add("attribute[pgElectronicDistribution]");
		busSelect.add("attribute[pgOtherDistributionList]");
		busSelect.add("attribute[pgColorIndexNumber]");
		busSelect.add("attribute[pgChemicalGroup]");
		busSelect.add("attribute[pgChemicalReactivity]");
		busSelect.add("attribute[pgColorIndexNumber]");
		busSelect.add("attribute[pgEinecsElincsNumber]");
		busSelect.add("attribute[pgMaterialFunction]");
		busSelect.add("attribute[pgMaterialOrigin]");
		busSelect.add("attribute[pgArchiveDate]");
		busSelect.add("attribute[pgArchiveReason]");
		busSelect.add("attribute[pgCoCreators]");
		busSelect.add("attribute[pgUnarchiveDate]");
		busSelect.add("attribute[pgUnarchiveReason]");
		busSelect.add("attribute[pgASLAllocation]");
		busSelect.add("attribute[pgLastUpdateUser]");
		busSelect.add("attribute[pgSecurityStatus]");
		busSelect.add("attribute[pgLastUpdateUser]");
		busSelect.add("attribute[pgSecurityStatus]");
		busSelect.add("attribute[pgCASNumber]");
		busSelect.add("attribute[Title]");
		busSelect.add("attribute[pgBrandInformation]");
		busSelect.add("attribute[pgProductExtraVariant]");
		busSelect.add("attribute[Security Classification]");
		busSelect.add("to[Design Responsibility].from.name");
		busSelect.add("to[Design Responsibility].from.id");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		busSelect.addAll(getRelationshippgApprovedSupplierListDataSelectable(context));
		busSelect.addAll(getRelationshippgDefinesMaterialDataSelectable(context));
		busSelect.add("to["+pgV3Constants.RELATIONSHIP_REGIONOWNS+"].from.name");
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getRawMaterialRelatedSelectableMap(Context context,boolean isCombinedWithMaster) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(358);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addElement("attribute[pgPlantLineSpecific]");
		busSelect.addElement("attribute[pgPlantTestingText]");
		
		if(isCombinedWithMaster)
		{
			busSelect.addAll((List)getRawMaterialSelectableMap(context).get("busSelect"));
		
		}
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getFormulatedProductSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(217);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		//pgFormulatedProduct selectable
		busSelect.addElement("attribute[Reason for Change]");
		busSelect.addElement("attribute[pgCategory]");
		busSelect.addElement("attribute[pgFailedReason]");
		busSelect.addElement("attribute[pgGBU]");
		busSelect.addElement("attribute[pgLockedBy]");
		busSelect.addElement("attribute[pgLockedDate]");
		busSelect.addElement("attribute[pgModifiedDate]");
		busSelect.addElement("attribute[pgNeedsFile]");
		busSelect.addElement("attribute[pgOriginatingSource]");
		busSelect.addElement("attribute[pgRegion]");
		busSelect.addElement("attribute[pgSubType]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBatteryType]");
		busSelect.addElement("attribute[pgBrandInformation]");
		busSelect.addElement("attribute[pgIsBattery]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgProductExtraVariant]");
		busSelect.addElement("attribute[pgSecurityStatus]");
		busSelect.addElement("attribute[pgBOMBaseQuantity]");
		busSelect.addElement("attribute[pgClearanceNeeded]");
		busSelect.addElement("attribute[pgContainsBattery]");
		busSelect.addElement("attribute[pgDangerCategory]");
		busSelect.addElement("attribute[pgEntryBaseQuantity]");
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE+"].to.name");
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Ends
		busSelect.addElement("attribute[pgIntendedMarkets]");
		busSelect.addElement("attribute[pgShippingHazardClassification]");
		busSelect.addElement("attribute[pgTechnology]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("from[EBOM].to.name");
		busSelect.addElement("from[EBOM].to.id");
		
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		busSelect.addAll(getRelationshippgApprovedSupplierListDataSelectable(context));
		busSelect.addAll(getRelationshippgDefinesMaterialDataSelectable(context));
		busSelect.addAll(getRelationshippgCountryOfSalesDataSelect(context));
		
		mapSelecables.put("busSelect", busSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static Map getFormulatedProductRelatedSelectableMap(Context context)
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList relSelect = new StringList(6);
		StringList busSelect = new StringList(32);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		
		busSelect.addAll(getBasicInfoBusSelectable(context));
		
		relSelect.addAll(getBasicInfoRelationshipSelectable(context));
		
		busSelect.addElement("attribute[pgLocalDescription]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBatchUnitSize]");
		busSelect.addElement("attribute[pgIngredientLoss]");
		busSelect.addElement("attribute[pgMaxQuantity]");
		busSelect.addElement("attribute[pgMinQuantity]");
		busSelect.addElement("attribute[pgSubTotal]");
		
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		relSelect.addElement("to.name");
		relSelect.addElement("from.name");
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static Map getCDBSelectableMap(Context context)
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(156);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addElement("attribute[pgProductFormDetail]");
		//Modified by DSM(Sogeti)-2015x.2 for PDF views For defect 11300 - Starts
		busSelect.addElement("attribute[pgPHBrand]");
		busSelect.addElement("attribute[pgProductForm]");
		busSelect.addElement("attribute[pgSubBrand]");
		busSelect.addElement("attribute[pgNodeId]");
		//Modified by DSM(Sogeti)-2015x.2 for PDF views For defect 11300 - Starts
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static Map getCDBRelatedSelectableMap(Context context)
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(35);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		
		busSelect.addElement("attribute[Level]");
		busSelect.addElement("attribute[pgChange]");
		busSelect.addElement("attribute[pgArea]");
		busSelect.addElement("attribute[pgCharacteristic]");
		busSelect.addElement("attribute[pgCharacteristicSpecifics]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[pgConsumerCommentLanguage]");
		busSelect.addElement("attribute[pgConsumerNeed]");
		busSelect.addElement("attribute[pgDesignImpact]");
		busSelect.addElement("attribute[pgParameterType]");
		busSelect.addElement("attribute[Title]");
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getPackingMaterialSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(297);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		busSelect.addAll(getBasicInfoBusSelectable(context));
		Map mapSelecables = new HashMap();
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBrandInformation]");
		busSelect.addElement("attribute[pgProductExtraVariant]");
		busSelect.addElement("attribute[pgShippingInstructions]");
		busSelect.addElement("attribute[pgStorageInformation]");
		busSelect.addElement("attribute[pgTechnology]");
		busSelect.addElement("attribute[pgCPIC]");
		busSelect.addElement("attribute[pgDecorationDetails]");
		busSelect.addElement("attribute[pgLabelingInformation]");
		busSelect.addElement("attribute[pgMarketingSize]");
		busSelect.addElement("attribute[pgMaterialClassification]");
		busSelect.addElement("attribute[pgMaterialOfConstruction]");
		busSelect.addElement("attribute[pgPackagingComponentType]");
		busSelect.addElement("attribute[pgPackagingMaterialType]");
		busSelect.addElement("attribute[pgPackagingSize]");
		busSelect.addElement("attribute[pgPackagingTechnology]");
		busSelect.addElement("attribute[pgPartColor]");
		busSelect.addElement("attribute[pgPreferredMaterial]");
		busSelect.addElement("attribute[pgPackagingLevel]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[pgArtworkRequired]");
		busSelect.addElement("attribute[pgArtworkApprovedCountries]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgPackagingUnit]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("to[Design Responsibility].from.name");
		busSelect.addElement("to[Design Responsibility].from.id");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		busSelect.addAll(getRelationshippgApprovedSupplierListDataSelectable(context));
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_REGIONOWNS+"].from.name");
		busSelect.addAll(getCommonDataBusSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		return mapSelecables;
	}
		public static Map getMasterPackingMaterialSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(296);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		busSelect.addAll(getBasicInfoBusSelectable(context));
		Map mapSelecables = new HashMap();
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBrandInformation]");
		busSelect.addElement("attribute[pgProductExtraVariant]");
		busSelect.addElement("attribute[pgShippingInstructions]");
		busSelect.addElement("attribute[pgStorageInformation]");
		busSelect.addElement("attribute[pgTechnology]");
		busSelect.addElement("attribute[pgCPIC]");
		busSelect.addElement("attribute[pgDecorationDetails]");
		busSelect.addElement("attribute[pgLabelingInformation]");
		busSelect.addElement("attribute[pgMarketingSize]");
		busSelect.addElement("attribute[pgMaterialClassification]");
		busSelect.addElement("attribute[pgMaterialOfConstruction]");
		busSelect.addElement("attribute[pgPackagingComponentType]");
		busSelect.addElement("attribute[pgPackagingMaterialType]");
		busSelect.addElement("attribute[pgPackagingSize]");
		busSelect.addElement("attribute[pgPackagingTechnology]");
		busSelect.addElement("attribute[pgPartColor]");
		busSelect.addElement("attribute[pgPreferredMaterial]");
		busSelect.addElement("attribute[pgPackagingLevel]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[pgArtworkRequired]");
		busSelect.addElement("attribute[pgArtworkApprovedCountries]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgPackagingUnit]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("to[Design Responsibility].from.name");
		busSelect.addElement("to[Design Responsibility].from.id");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		
		busSelect.addAll(getRelationshippgApprovedSupplierListDataSelectable(context));
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		
		busSelect.addAll(getCommonDataBusSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getPackingMaterialRelatedSelectableMap(Context context,boolean isCombinedWithMaster) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(336);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		busSelect.addAll(getBasicInfoBusSelectable(context));
		Map mapSelecables = new HashMap();
		busSelect.addElement("attribute[pgPlantLineSpecific]");
		busSelect.addElement("attribute[pgPlantTestingText]");
		busSelect.addElement("attribute[pgPlantTesting]");
		busSelect.addElement("attribute[pgChange]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[pgEnvironmentalClass]");
		busSelect.addElement("attribute[pgPackingLevel]");
		busSelect.addElement("attribute[pgPostConsumerRecycleContent]");
		busSelect.addElement("attribute[pgTotalRecycleContent]");
		busSelect.addElement("attribute[pgVolumeArea]");
		busSelect.addElement("attribute[pgVolumeAreaUnitOfMeasure]");
		busSelect.addElement("attribute[pgWeight]");
		busSelect.addElement("attribute[pgComponent]");
		busSelect.addElement("attribute[pgQuantity]");
		busSelect.addElement("attribute[pgQuantityUnitOfMeasure]");
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		
		if(isCombinedWithMaster)
		{
			busSelect.addAll((List)getPackingMaterialSelectableMap(context).get("busSelect"));
		}
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
		public static Map getMasterPackingMaterialRelatedSelectableMap(Context context,boolean isCombinedWithMaster) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(314);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		busSelect.addAll(getBasicInfoBusSelectable(context));
		Map mapSelecables = new HashMap();
		busSelect.addElement("attribute[pgPlantLineSpecific]");
		busSelect.addElement("attribute[pgPlantTestingText]");
		
		if(isCombinedWithMaster)
		{
			busSelect.addAll((List)getMasterPackingMaterialSelectableMap(context).get("busSelect"));
		}
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getApprovedSupplierListSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(226);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement("attribute[Product Data View Form Name]");
		busSelect.addElement("attribute[Originator]");
		busSelect.addElement("attribute[ReviewInterval]");
		busSelect.addElement("attribute[Release Date]");
		busSelect.addElement("attribute[pgCSSOID]");
		busSelect.addElement("attribute[pgCSSType]");
		busSelect.addElement("attribute[pgCSSSubType]");
		busSelect.addElement("attribute[pgCSSOriginator]");
		busSelect.addElement("attribute[InternalPDF Sections]");
		busSelect.addElement("attribute[ExternalPDF Sections]");
		busSelect.addElement("attribute[Reason for Change]");
		busSelect.addElement("attribute[Status]");
		busSelect.addElement("attribute[pgArchiveComment]");
		busSelect.addElement("attribute[pgArchiveDate]");
		busSelect.addElement("attribute[pgArchiveReason]");
		busSelect.addElement("attribute[pgCategory]");
		busSelect.addElement("attribute[pgFailedReason]");
		busSelect.addElement("attribute[pgGBU]");
		busSelect.addElement("attribute[pgIsTemplate]");
		busSelect.addElement("attribute[pgLastUpdateUser]");
		busSelect.addElement("attribute[pgLocalDescription]");
		busSelect.addElement("attribute[pgLockedBy]");
		busSelect.addElement("attribute[pgLockedDate]");
		busSelect.addElement("attribute[pgModifiedDate]");
		busSelect.addElement("attribute[pgNeedsFile]");
		busSelect.addElement("attribute[pgOriginatingSource]");
		busSelect.addElement("attribute[pgOtherDistributionList]");
		busSelect.addElement("attribute[pgOtherNames]");
		busSelect.addElement("attribute[pgRegion]");
		busSelect.addElement("attribute[pgSameAsIssueDate]");
		busSelect.addElement("attribute[pgSAPType]");
		busSelect.addElement("attribute[pgSegment]");
		busSelect.addElement("attribute[pgShortDescription]");
		busSelect.addElement("attribute[pgSubType]");
		busSelect.addElement("attribute[pgUnarchiveDate]");
		busSelect.addElement("attribute[pgUnarchiveReason]");
		busSelect.addElement("attribute[pgViewSortOrder]");
		busSelect.addElement("attribute[pgCoCreators]");
		busSelect.addElement("attribute[pgElectronicDistribution]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[Authorized Temporary Specification]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[Specification Category]");
		busSelect.addElement("attribute[Template Level]");
		busSelect.addElement("attribute[Effectivity Date]");
		busSelect.addElement("attribute[Expiration Date]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[Title]");
		busSelect.addElement("attribute[Unit of Measure]");
		busSelect.addElement("attribute[Distribution List]");
		busSelect.addElement("attribute[Render Success Flag]");
		busSelect.addElement("attribute[Product Code]");
		busSelect.addElement("attribute[Render Error Message]");
		busSelect.addElement("attribute[Reference Number]");
		busSelect.addElement("attribute[Rendered File Name]");
		busSelect.addElement("attribute[Section Info]");
		busSelect.addElement("attribute[Global attribute List]");
		busSelect.addElement("attribute[Section Name]");
		busSelect.addElement("attribute[Regional attribute List]");
		busSelect.addElement("attribute[Local attribute List]");
		busSelect.addElement("attribute[Hidden attribute List]");
		busSelect.addElement("attribute[Legacy]");
		busSelect.addElement("attribute[Specification View Form Name]");
		busSelect.addElement("attribute[Claimed attribute List]");
		busSelect.addElement("attribute[Properties Form Name]");
		busSelect.addElement("attribute[Properties Template Form Name]");
		busSelect.addElement("attribute[Is Template]");
		busSelect.addElement("attribute[Tolerance]");
		busSelect.addElement("attribute[Upper Limit]");
		busSelect.addElement("attribute[Lower Limit]");
		busSelect.addElement("attribute[Issued Date]");
		busSelect.addElement("attribute[Approved Date]");
		busSelect.addElement("attribute[Brand]");
		busSelect.addElement("attribute[Location]");
		busSelect.addElement("attribute[Franchise]");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
 *
 * @param context
 * @return
 */
	public static Map getApprovedSupplierListRelatedSelectableMap(Context context)
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(121);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addElement("attribute[Product Data View Form Name]");
		busSelect.addElement("attribute[Originator]");
		busSelect.addElement("attribute[ReviewInterval]");
		busSelect.addElement("attribute[Release Date]");
		busSelect.addElement("attribute[pgCSSOID]");
		busSelect.addElement("attribute[pgCSSType]");
		busSelect.addElement("attribute[pgCSSSubType]");
		busSelect.addElement("attribute[pgCSSOriginator]");
		busSelect.addElement("attribute[InternalPDF Sections]");
		busSelect.addElement("attribute[ExternalPDF Sections]");
		busSelect.addElement("attribute[Reason for Change]");
		busSelect.addElement("attribute[Status]");
		busSelect.addElement("attribute[pgArchiveComment]");
		busSelect.addElement("attribute[pgArchiveDate]");
		busSelect.addElement("attribute[pgArchiveReason]");
		busSelect.addElement("attribute[pgCategory]");
		busSelect.addElement("attribute[pgFailedReason]");
		busSelect.addElement("attribute[pgGBU]");
		busSelect.addElement("attribute[pgIsTemplate]");
		busSelect.addElement("attribute[pgLastUpdateUser]");
		busSelect.addElement("attribute[pgLocalDescription]");
		busSelect.addElement("attribute[pgLockedBy]");
		busSelect.addElement("attribute[pgLockedDate]");
		busSelect.addElement("attribute[pgModifiedDate]");
		busSelect.addElement("attribute[pgNeedsFile]");
		busSelect.addElement("attribute[pgOriginatingSource]");
		busSelect.addElement("attribute[pgOtherDistributionList]");
		busSelect.addElement("attribute[pgOtherNames]");
		busSelect.addElement("attribute[pgRegion]");
		busSelect.addElement("attribute[pgSameAsIssueDate]");
		busSelect.addElement("attribute[pgSAPType]");
		busSelect.addElement("attribute[pgSegment]");
		busSelect.addElement("attribute[pgShortDescription]");
		busSelect.addElement("attribute[pgSubType]");
		busSelect.addElement("attribute[pgUnarchiveDate]");
		busSelect.addElement("attribute[pgUnarchiveReason]");
		busSelect.addElement("attribute[pgViewSortOrder]");
		busSelect.addElement("attribute[pgCoCreators]");
		busSelect.addElement("attribute[pgElectronicDistribution]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[Part Classification]");
		busSelect.addElement("attribute[Estimated Cost]");
		busSelect.addElement("attribute[Effectivity Date]");
		busSelect.addElement("attribute[Lead Time]");
		busSelect.addElement("attribute[Production Make Buy Code]");
		busSelect.addElement("attribute[Unit of Measure]");
		busSelect.addElement("attribute[Target Cost]");
		busSelect.addElement("attribute[Service Make Buy Code]");
		busSelect.addElement("attribute[Weight]");
		busSelect.addElement("attribute[Material Category]");
		busSelect.addElement("attribute[Spare Part]");
		busSelect.addElement("attribute[Is Version]");
		busSelect.addElement("attribute[Current Version]");
		busSelect.addElement("attribute[Design Purchase]");
		busSelect.addElement("attribute[End Item]");
		busSelect.addElement("attribute[End Item Override Enabled]");
		busSelect.addElement("attribute[All Level MBOM Generated]");
		busSelect.addElement("attribute[VPLM Image]");
		busSelect.addElement("attribute[isVPMVisible]");
		busSelect.addElement("attribute[V_Name]");
		busSelect.addElement("attribute[Title]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[Distribution List]");
		busSelect.addElement("attribute[Expiration Date]");
		busSelect.addElement("attribute[Section Info]");
		busSelect.addElement("attribute[Section Name]");
		busSelect.addElement("attribute[Specification Category]");
		busSelect.addElement("attribute[Authorized Temporary Specification]");
		busSelect.addElement("attribute[Claimed Attribute List]");
		busSelect.addElement("attribute[Global Attribute List]");
		busSelect.addElement("attribute[Hidden Attribute List]");
		busSelect.addElement("attribute[Local Attribute List]");
		busSelect.addElement("attribute[Regional Attribute List]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBatteryType]");
		busSelect.addElement("attribute[pgBrandInformation]");
		busSelect.addElement("attribute[pgIsBattery]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgProductExtraVariant]");
		busSelect.addElement("attribute[pgSecurityStatus]");
		busSelect.addElement("attribute[pgChange]");
		busSelect.addElement("attribute[pgClassification]");
		busSelect.addElement("attribute[pgPlantLineSpecific]");
		busSelect.addElement("attribute[pgPSRAInformation]");
		busSelect.addElement("attribute[pgQualificationStatus]");
		busSelect.addElement("attribute[pgSupplierTradeName]");
		busSelect.addElement("attribute[pgSequence]");
		busSelect.addElement("attribute[pgCSSManufacturer]");
		busSelect.addElement("attribute[pgCSSTraderDistributor]");
		busSelect.addElement("attribute[pgBrand]");
		busSelect.addElement("attribute[pgProduct]");
		busSelect.addElement("from[Manufacturer Equivalent].to.name");
		busSelect.addElement("from[Manufacturing Responsibility].to.name");
		busSelect.addElement("from[Part Specification].to.name");
		busSelect.addElement("from[Manufacturer Equivalent].to.id");
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
/**
 *
 * @param context
 * @return
 * @throws MatrixException
 */
	public static Map getBaseFormulaSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(148);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addElement("attribute[Product Data View Form Name]");
		busSelect.addElement("attribute[Originator]");
		busSelect.addElement("attribute[ReviewInterval]");
		busSelect.addElement("attribute[Release Date]");
		busSelect.addElement("attribute[pgCSSOID]");
		busSelect.addElement("attribute[pgCSSType]");
		busSelect.addElement("attribute[pgCSSSubType]");
		busSelect.addElement("attribute[pgCSSOriginator]");
		busSelect.addElement("attribute[InternalPDF Sections]");
		busSelect.addElement("attribute[ExternalPDF Sections]");
		busSelect.addElement("attribute[Reason for Change]");
		busSelect.addElement("attribute[Status]");
		busSelect.addElement("attribute[pgArchiveComment]");
		busSelect.addElement("attribute[pgArchiveDate]");
		busSelect.addElement("attribute[pgArchiveReason]");
		busSelect.addElement("attribute[pgCategory]");
		busSelect.addElement("attribute[pgFailedReason]");
		busSelect.addElement("attribute[pgGBU]");
		busSelect.addElement("attribute[pgIsTemplate]");
		busSelect.addElement("attribute[pgLastUpdateUser]");
		busSelect.addElement("attribute[pgLocalDescription]");
		busSelect.addElement("attribute[pgLockedBy]");
		busSelect.addElement("attribute[pgLockedDate]");
		busSelect.addElement("attribute[pgModifiedDate]");
		busSelect.addElement("attribute[pgNeedsFile]");
		busSelect.addElement("attribute[pgOriginatingSource]");
		busSelect.addElement("attribute[pgOtherDistributionList]");
		busSelect.addElement("attribute[pgOtherNames]");
		busSelect.addElement("attribute[pgRegion]");
		busSelect.addElement("attribute[pgSameAsIssueDate]");
		busSelect.addElement("attribute[pgSAPType]");
		busSelect.addElement("attribute[pgSegment]");
		busSelect.addElement("attribute[pgShortDescription]");
		busSelect.addElement("attribute[pgSubType]");
		busSelect.addElement("attribute[pgUnarchiveDate]");
		busSelect.addElement("attribute[pgUnarchiveReason]");
		busSelect.addElement("attribute[pgViewSortOrder]");
		busSelect.addElement("attribute[pgCoCreators]");
		busSelect.addElement("attribute[pgElectronicDistribution]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[Part Classification]");
		busSelect.addElement("attribute[Estimated Cost]");
		busSelect.addElement("attribute[Effectivity Date]");
		busSelect.addElement("attribute[Lead Time]");
		busSelect.addElement("attribute[Production Make Buy Code]");
		busSelect.addElement("attribute[Unit of Measure]");
		busSelect.addElement("attribute[Target Cost]");
		busSelect.addElement("attribute[Service Make Buy Code]");
		busSelect.addElement("attribute[Weight]");
		busSelect.addElement("attribute[Material Category]");
		busSelect.addElement("attribute[Spare Part]");
		busSelect.addElement("attribute[Is Version]");
		busSelect.addElement("attribute[Current Version]");
		busSelect.addElement("attribute[Design Purchase]");
		busSelect.addElement("attribute[End Item]");
		busSelect.addElement("attribute[End Item Override Enabled]");
		busSelect.addElement("attribute[All Level MBOM Generated]");
		busSelect.addElement("attribute[VPLM Image]");
		busSelect.addElement("attribute[isVPMVisible]");
		busSelect.addElement("attribute[V_Name]");
		busSelect.addElement("attribute[Title]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[Distribution List]");
		busSelect.addElement("attribute[Expiration Date]");
		busSelect.addElement("attribute[Section Info]");
		busSelect.addElement("attribute[Section Name]");
		busSelect.addElement("attribute[Specification Category]");
		busSelect.addElement("attribute[Authorized Temporary Specification]");
		busSelect.addElement("attribute[Claimed Attribute List]");
		busSelect.addElement("attribute[Global Attribute List]");
		busSelect.addElement("attribute[Hidden Attribute List]");
		busSelect.addElement("attribute[Local Attribute List]");
		busSelect.addElement("attribute[Regional Attribute List]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBatteryType]");
		busSelect.addElement("attribute[pgBrandInformation]");
		busSelect.addElement("attribute[pgIsBattery]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgProductExtraVariant]");
		busSelect.addElement("attribute[pgSecurityStatus]");
		busSelect.addElement("attribute[pgBOMBaseQuantity]");
		busSelect.addElement("attribute[pgClearanceNeeded]");
		busSelect.addElement("attribute[pgContainsBattery]");
		busSelect.addElement("attribute[pgDangerCategory]");
		busSelect.addElement("attribute[pgEntryBaseQuantity]");
		busSelect.addElement("attribute[pgIntendedMarkets]");
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE+"].to.name");
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Ends
		busSelect.addElement("attribute[pgShippingHazardClassification]");
		busSelect.addElement("attribute[pgTechnology]");
		busSelect.addElement("attribute[pgKosher]");
		busSelect.addElement("attribute[pgGelatinSource]");
		busSelect.addElement("attribute[pgPrintPattern]");
		busSelect.addElement("attribute[pgIngredientLoss]");
		busSelect.addElement("attribute[pgIngredientLossComments]");
		busSelect.addElement("attribute[pgSubTotal]");
		busSelect.addElement("attribute[pgTotal]");
		busSelect.addElement("attribute[pgSecurityClassification]");
		busSelect.addElement("to[Design Responsibility].from.name");
		busSelect.addElement("to[Design Responsibility].from.id");
		
		busSelect.addElement("to[Design Responsibility].from.name");
			busSelect.addElement("to[EBOM Substitute].fromrel[EBOM].to.name");
		busSelect.addElement("to[EBOM Substitute].fromrel[EBOM].to.id");
		busSelect.addElement("to[EBOM Substitute].fromrel[EBOM].to.attribute[Title]");
		busSelect.addElement("to[EBOM Substitute].fromrel[EBOM].from.name");
		busSelect.addElement("to[EBOM Substitute].fromrel[EBOM].from.attribute[Title]");
		
		busSelect.addElement("from[EBOM].attribute[Min]");
		busSelect.addElement("from[EBOM].attribute[Max]");
		busSelect.addElement("from[EBOM].attribute[pgMaterialFunction]");
		busSelect.addElement("from[EBOM].attribute[pgPositionIndicator]");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
/**
 *
 * @param context
 * @return
 */
	public static Map getBaseFormulaRelatedSelectableMap(Context context)
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(117);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addElement("attribute[Product Data View Form Name]");
		busSelect.addElement("attribute[ReviewInterval]");
		busSelect.addElement("attribute[Release Date]");
		busSelect.addElement("attribute[pgCSSOID]");
		busSelect.addElement("attribute[pgCSSType]");
		busSelect.addElement("attribute[pgCSSSubType]");
		busSelect.addElement("attribute[pgCSSOriginator]");
		busSelect.addElement("attribute[InternalPDF Sections]");
		busSelect.addElement("attribute[ExternalPDF Sections]");
		busSelect.addElement("attribute[Reason for Change]");
		busSelect.addElement("attribute[Status]");
		busSelect.addElement("attribute[pgArchiveComment]");
		busSelect.addElement("attribute[pgArchiveDate]");
		busSelect.addElement("attribute[pgArchiveReason]");
		busSelect.addElement("attribute[pgCategory]");
		busSelect.addElement("attribute[pgFailedReason]");
		busSelect.addElement("attribute[pgGBU]");
		busSelect.addElement("attribute[pgIsTemplate]");
		busSelect.addElement("attribute[pgLastUpdateUser]");
		busSelect.addElement("attribute[pgLocalDescription]");
		busSelect.addElement("attribute[pgLockedBy]");
		busSelect.addElement("attribute[pgLockedDate]");
		busSelect.addElement("attribute[pgModifiedDate]");
		busSelect.addElement("attribute[pgNeedsFile]");
		busSelect.addElement("attribute[pgOriginatingSource]");
		busSelect.addElement("attribute[pgOtherDistributionList]");
		busSelect.addElement("attribute[pgOtherNames]");
		//busSelect.addElement("attribute[pgProjectSecurityGroup]");
		busSelect.addElement("attribute[pgRegion]");
		busSelect.addElement("attribute[pgSameAsIssueDate]");
		busSelect.addElement("attribute[pgSAPType]");
		busSelect.addElement("attribute[pgSegment]");
		busSelect.addElement("attribute[pgShortDescription]");
		busSelect.addElement("attribute[pgSubType]");
		busSelect.addElement("attribute[pgUnarchiveDate]");
		busSelect.addElement("attribute[pgUnarchiveReason]");
		busSelect.addElement("attribute[pgViewSortOrder]");
		busSelect.addElement("attribute[pgCoCreators]");
		busSelect.addElement("attribute[pgElectronicDistribution]");
		busSelect.addElement("attribute[Comment]");
		busSelect.addElement("attribute[Part Classification]");
		busSelect.addElement("attribute[Estimated Cost]");
		busSelect.addElement("attribute[Effectivity Date]");
		busSelect.addElement("attribute[Lead Time]");
		busSelect.addElement("attribute[Production Make Buy Code]");
		busSelect.addElement("attribute[Unit of Measure]");
		busSelect.addElement("attribute[Target Cost]");
		busSelect.addElement("attribute[Service Make Buy Code]");
		busSelect.addElement("attribute[Weight]");
		busSelect.addElement("attribute[Material Category]");
		busSelect.addElement("attribute[Spare Part]");
		busSelect.addElement("attribute[Is Version]");
		busSelect.addElement("attribute[Current Version]");
		busSelect.addElement("attribute[Design Purchase]");
		busSelect.addElement("attribute[End Item]");
		busSelect.addElement("attribute[End Item Override Enabled]");
		busSelect.addElement("attribute[All Level MBOM Generated]");
		busSelect.addElement("attribute[VPLM Image]");
		busSelect.addElement("attribute[isVPMVisible]");
		busSelect.addElement("attribute[V_Name]");
		busSelect.addElement("attribute[Title]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[Distribution List]");
		busSelect.addElement("attribute[Expiration Date]");
		busSelect.addElement("attribute[Section Info]");
		busSelect.addElement("attribute[Section Name]");
		busSelect.addElement("attribute[Specification Category]");
		busSelect.addElement("attribute[Authorized Temporary Specification]");
		busSelect.addElement("attribute[Claimed attribute List]");
		busSelect.addElement("attribute[Globa attribute List]");
		busSelect.addElement("attribute[Hidden attribute List]");
		busSelect.addElement("attribute[Local attribute List]");
		busSelect.addElement("attribute[Regional attribute List]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBatteryType]");
		busSelect.addElement("attribute[pgBrandInformation]");
		busSelect.addElement("attribute[pgIsBattery]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgProductExtraVariant]");
		busSelect.addElement("attribute[pgSecurityStatus]");
		busSelect.addElement("attribute[pgBatchUnitSize]");
		busSelect.addElement("attribute[pgBatchUnitSizeUnitOfMeasure]");
		busSelect.addElement("attribute[pgIngredientLoss]");
		busSelect.addElement("attribute[pgIngredientLossComment]");
		busSelect.addElement("attribute[pgIngredientLossUnitOfMeasure]");
		busSelect.addElement("attribute[pgMaxQuantity]");
		busSelect.addElement("attribute[pgMinQuantity]");
		busSelect.addElement("attribute[pgPackingLevel]");
		busSelect.addElement("attribute[pgQuantity]");
		busSelect.addElement("attribute[pgQuantityUnitOfMeasure]");
		busSelect.addElement("attribute[pgSubTotal]");
		busSelect.addElement("attribute[pgSubTotalUnitOfMeasure]");
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
/**
 *
 * @param context
 * @return
 * @throws MatrixException
 */
	public static Map getCommonPerformanceSpecificationSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(168);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement("attribute[Release Date]");
		busSelect.addElement("attribute[pgCSSOriginator]");
		busSelect.addElement("attribute[Reason for Change]");
		busSelect.addElement("attribute[pgCategory]");
		busSelect.addElement("attribute[pgGBU]");
		busSelect.addElement("attribute[pgModifiedDate]");
		busSelect.addElement("attribute[Comments]");
		busSelect.addElement("attribute[pgRegion]");
		busSelect.addElement("attribute[pgSegment]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[Effectivity Date]");
		busSelect.addElement("attribute[Expiration Date]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[Is Template]");
		busSelect.addElement("attribute[Approved Date]");
		busSelect.addElement("to[Design Responsibility].from.name");
		busSelect.addElement("to[Design Responsibility].from.id");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	public static Map getCommonPerformanceSpecificationRelatedSelectableMap(Context context)
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(24);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
/**
 *
 * @param context
 * @return
 * @throws MatrixException
 */
	public static Map getpgMasterRawMaterialSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(197);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement("attribute[Release Date]");
		busSelect.addElement("attribute[Reason for Change]");
		busSelect.addElement("attribute[pgCategory]");
		busSelect.addElement("attribute[pgGBU]");
		busSelect.addElement("attribute[pgModifiedDate]");
		busSelect.addElement("attribute[pgRegion]");
		busSelect.addElement("attribute[pgSegment]");
		busSelect.addElement("attribute[Effectivity Date]");
		busSelect.addElement("attribute[Unit of Measure]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[Expiration Date]");
		busSelect.addElement("attribute[pgCASNumber]");
		busSelect.addElement("attribute[pgChemicalGroup]");
		busSelect.addElement("attribute[pgChemicalReactivity]");
		busSelect.addElement("attribute[pgColorIndexNumber]");
		busSelect.addElement("attribute[pgDangerCategory]");
		busSelect.addElement("attribute[pgEinecsElincsNumber]");
		busSelect.addElement("attribute[pgIngredientClass]");
		busSelect.addElement("attribute[pgMaterialFunction]");
		busSelect.addElement("attribute[pgMaterialOrigin]");
		busSelect.addElement("attribute[pgMolecularFormula]");
		busSelect.addElement("attribute[pgRPhrase]");
		busSelect.addElement("attribute[pgSafetySymbol]");
		busSelect.addElement("attribute[pgSPhrase]");
		busSelect.addElement("attribute[pgValence]");
		busSelect.addElement("attribute[pgShippingHazardClassification]");
		busSelect.addElement("attribute[pgShippingInstructions]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("attribute[pgSecurityStatus]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgPreferredMaterial]");
		busSelect.addElement("to[Design Responsibility].from.name");
		busSelect.addElement("to[Design Responsibility].from.id");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
/**
 *
 * @param context
 * @return
 */
	public static Map getpgMasterRawMaterialRelatedSelectableMap(Context context)
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(24);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	public static Map getpgMasterFinishedProductSelectableMap(Context context)
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(286);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addAll(getBasicInfoBusSelectable(context));
		
		busSelect.addElement("attribute[pgCSSType]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[Expiration Date]");
		busSelect.addElement("attribute[pgLabelingInformation]");
		busSelect.addElement("attribute[pgMarketingSize]");
		busSelect.addElement("attribute[pgPackagingTechnology]");
		busSelect.addElement("attribute[pgProductForm]");
		busSelect.addElement("attribute[pgShippingInstructions]");
		busSelect.addElement("attribute[pgStorageHumidityLimits]");
		busSelect.addElement("attribute[pgStorageInformation]");
		busSelect.addElement("attribute[pgStorageTemperatureLimits]");
		busSelect.addElement("attribute[pgWarehousingClassification]");
		busSelect.addElement("attribute[pgWDStatius]");
		busSelect.addElement("attribute[pgUnitOfMeasureSystem]");
		busSelect.addElement("attribute[pgQuantityForProductIs]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("attribute[pgBOMBaseQuantity]");
		busSelect.addElement("attribute[pgEntryBaseQuantity]");
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgSecurityStatus]");
		busSelect.addElement("attribute[Security Classification]");
		
		busSelect.addElement("to[Design Responsibility].from.name");
		busSelect.addElement("to[Design Responsibility].from.id");
		
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static Map getpgMasterFinishedProductRelatedSelectableMap(Context context)
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(28);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
			busSelect.addAll(getBasicInfoBusSelectable(context));
		//busSelect.addAll(getpgPackingUnitCharacteristicDataSelect(context));
		//busSelect.addAll(getpgTransportUnitCharacteristicDataSelect(context));
		
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		relSelect.addAll(getBasicInfoRelationshipSelectable(context));
		
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	/*
	public static List getpgPackingUnitCharacteristicDataSelect(Context context)
	{
		List busSelect = new StringList();
		busSelect.add("attribute[pgConsumerUnitsPerPackingUnit]");
		busSelect.add("attribute[pgCubeEfficiency]");
		busSelect.add("attribute[pgDepth]");
		busSelect.add("attribute[pgDimensionUnitOfMeasure]");
		busSelect.add("attribute[pgGrossWeight]");
		busSelect.add("attribute[pgGrossWeightUnitOfMeasure]");
		busSelect.add("attribute[pgGTIN]");
		busSelect.add("attribute[pgHeight]");
		busSelect.add("attribute[pgNetWeightOfProductInConsumerUnit]");
		busSelect.add("attribute[pgNetWeightUnitOfMeasure]");
		busSelect.add("attribute[pgUnitOfMeasureSystem]");
		busSelect.add("attribute[pgWidth]");
		busSelect.add("attribute[pgCSSType]");
		return busSelect;
	}
	*/
	/**
	 *
	 * @param context
	 * @return
	 */
	/*
	public static List getpgTransportUnitCharacteristicDataSelect(Context context)
	{
		List busSelect = new StringList();
		busSelect.add("attribute[pgCubeEfficiency]");
		busSelect.add("attribute[pgCustomerUnitsPerLayer]");
		busSelect.add("attribute[pgCustomerUnitsPerTransportUnit]");
		busSelect.add("attribute[pgDepth]");
		busSelect.add("attribute[pgDimensionUnitOfMeasure]");
		busSelect.add("attribute[pgGrossWeightUnitOfMeasure]");
		busSelect.add("attribute[pgGrossWeightWithoutPallet]");
		busSelect.add("attribute[pgGrossWeightWithPallet]");
		busSelect.add("attribute[pgGTIN]");
		busSelect.add("attribute[pgHeight]");
		busSelect.add("attribute[pgLayersPerTransportUnit]");
		busSelect.add("attribute[pgMaxCaseStackHeight]");
		busSelect.add("attribute[pgMaxPalletStackHeight]");
		busSelect.add("attribute[pgMaxTruckPalletStackHeight]");
		busSelect.add("attribute[pgPalletType]");
		busSelect.add("attribute[pgUnitOfMeasureSystem]");
		busSelect.add("attribute[pgVolume]");
		busSelect.add("attribute[pgVolumeUnitOfMeasure]");
		busSelect.add("attribute[pgWidth]");
		busSelect.add("attribute[pgCSSType]");
		busSelect.add("to[Reference Document].from.name");
		return busSelect;
	}
	*/
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getpgFinishedProductSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(300);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgBatteryType]");
		busSelect.addElement("attribute[pgBrandInformation]");
		busSelect.addElement("attribute[pgIsBattery]");
		busSelect.addElement("attribute[pgMaterialGroup]");
		busSelect.addElement("attribute[pgProductExtraVariant]");
		busSelect.addElement("attribute[pgContainsBattery]");
		busSelect.addElement("attribute[pgCustomizationType]");
		busSelect.addElement("attribute[pgFinishedProductCode]");
		busSelect.addElement("attribute[pgLabelingInformation]");
		busSelect.addElement("attribute[pgMarketingSize]");
		busSelect.addElement("attribute[pgPackagingTechnology]");
		busSelect.addElement("attribute[pgProductForm]");
		busSelect.addElement("attribute[pgFinishedProductCode]");
		busSelect.addElement("attribute[pgShippingInstructions]");
		busSelect.addElement("attribute[pgStorageHumidityLimits]");
		busSelect.addElement("attribute[pgStorageInformation]");
		busSelect.addElement("attribute[pgStorageTemperatureLimits]");
		busSelect.addElement("attribute[pgWarehousingClassification]");
		busSelect.addElement("attribute[pgWDStatius]");
		busSelect.addElement("attribute[Render Language]");
		busSelect.addElement("attribute[pgBrand]");
		busSelect.addElement("attribute[pgSubBrand]");
		busSelect.addElement("attribute[pgGlobalForm]");
		busSelect.addElement("attribute[pgProductForm]");
		busSelect.addElement("attribute[pgPHCategory]");
		busSelect.addElement("attribute[pgUnitOfMeasureSystem]");
		busSelect.addElement("attribute[pgASLAllocation]");
		busSelect.addElement("attribute[Security Classification]");
		busSelect.addElement("attribute[pgQuantityForProductIs]");
		busSelect.addElement("attribute[pgBOMBaseQuantity]");
		busSelect.addElement("attribute[pgEntryBaseQuantity]");
		busSelect.addElement("attribute[pgBaseUnitOfMeasure]");
		busSelect.addElement("attribute[pgProductFormDetail]");
		
		busSelect.addElement("from[pgMaster].to.last.attribute[Title]");
		
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_REGIONOWNS+"].from.name");
		
		//Added by DSM 2018x.5 Requirement - Start
		busSelect.addElement("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM + "].to.name");
		//Added by DSM 2018x.5 Requirement - End		
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static Map getpgFinishedProductRelatedSelectableMap(Context context,boolean isCombinedWithMaster) throws MatrixException
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(324);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		//busSelect.addAll(getpgPackingUnitCharacteristicDataSelect(context));
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		//busSelect.addAll(getpgTransportUnitCharacteristicDataSelect(context));
		if(isCombinedWithMaster)
		{
			Map mapRaw=getpgFinishedProductSelectableMap(context);
			List liSelect=(List)mapRaw.get("busSelect");
			busSelect.addAll(liSelect);
		}
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}	
	//P&G: Addition for V4 PSUB PDF views - Starts
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getpgPackingSubassemblySelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(299);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGBASEUNITOFMEASURE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGBATTERYTYPE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGBRANDINFORMATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGISBATTERY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGMATERIALGROUP+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPRODUCTEXTRAVARIANT+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGCONTAINSBATTERY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGCUSTOMIZATIONTYPE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGFINISHEDPRODUCTCODE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGLABELINGINFORMATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGMARKETINGSIZE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPACKAGINGTECHNOLOGY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPRODUCTFORM+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGFINISHEDPRODUCTCODE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSHIPPINGINSTRUCTIONS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSTORAGEINFORMATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSTORAGETEMPERATURELIMITS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGWAREHOUSECLASIFICATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGWDSTATIUS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_RENDERLANGUAGE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGBRAND+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSUBBRAND+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGGLOBALFORM+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPRODUCTFORM+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPHCATEGORY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGUNITOFMEASURESYSTEM+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGASLALLOCATION+"]");
		//busSelect.addElement("attribute[pgProjectSecurityGroup]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_SECURITYCLASIFICATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGQUANTITYOFPRODUCTIS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGBOMBASEQUANTITY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGENTRYBASEQUANTITY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGBASEUNITOFMEASURE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPRODUCTFORMDETAIL+"]");		
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGMASTER+"].to.last.attribute["+pgV3Constants.ATTRIBUTE_TITLE+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_REGIONOWNS+"].from.name");
		busSelect.addAll(getRelationshippgSupersedesDataSelectable(context));
		busSelect.addAll(getRelationshipAuthorizedTemporarySpecificationDataSelectable(context));
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return
	 */
	public static Map getpgPackingSubassemblyRelatedSelectableMap(Context context,boolean isCombinedWithMaster) throws MatrixException
	{
		StringList relSelect = new StringList(1);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(324);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		//busSelect.addAll(getpgPackingUnitCharacteristicDataSelect(context));
		busSelect.addAll(getCharacteristicTypeAttributeSelectable(context));
		//busSelect.addAll(getpgTransportUnitCharacteristicDataSelect(context));
		if(isCombinedWithMaster)
		{
			Map mapRaw=getpgFinishedProductSelectableMap(context);
			List liSelect=(List)mapRaw.get("busSelect");
			busSelect.addAll(liSelect);
		}
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	//P&G: Addition for V4 PSUB PDF views - Ends	
		
	 //Added by V4-2013x.4 for PDF Views -starts

	public static Map getFinishedProductPartSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(296);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGBATTERYTYPE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGISBATTERY+"]");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALGROUP);
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGCONTAINSBATTERY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGCUSTOMIZATIONTYPE+"]");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERNAMES);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDIMENSIONUOM);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPREFERREDMATERIAL);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDECORATIONDETAILS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGUNIT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPARTCOLOR);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTREAL);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTOFPRODUCTINCONSUMERUNITREAL);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGALTERNATEUNITOFMEASURE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONHEIGHT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONLENGTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONWIDTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGVOLUMEREAL);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOVERHANGMAXWIDTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOVERHANGMAXLENGTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PPGUNDERHANGMAXWIDTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGUNDERHANGMAXLENGTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGWHSEPALLETSTACKMAXHEIGHT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGWHSECASEMAXHEIGHT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGTRUCKPALLETSTACKMAXHEIGHT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCUBEEFFECIENCY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCRUSHINDEX);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCUSTOMERUNITSPERLAYERINTEGER);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLAYERSPERTRANSPORTUNITINTEGER);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGAREAEFFECIENCY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGWHSELOGISTICS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMARKETINGSIZEREAL);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONWIDTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONLENGTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONHEIGHT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGGROSSWEIGHTUNITOFMEASURE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNETWEIGHTOFPRODUCTINCONSUMERUNITUOM);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGMATERIALTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPALLETTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSUBCLASS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGVOLUMEUNITOFMEASURE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLANGUAGE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCLASS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGWEIGHTUNITOFMEASURE);
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGWDSTATIUS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGLABELINGINFORMATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGMARKETINGSIZE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPACKAGINGTECHNOLOGY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPRODUCTFORM+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSHIPPINGINSTRUCTIONS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSTORAGEINFORMATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGSTORAGETEMPERATURELIMITS+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGWAREHOUSECLASIFICATION+"]");		
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGCSU+"]");
		//Modified for 2018x Upgrade - Stage attribute is replaced by Release Phase attribute in 18x OOTB - START. 
		//busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_STAGE+"]");
		busSelect.addElement("attribute["+pgV3Constants.STR_RELEASE_PHASE+"]");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		//Modified for 2018x Upgrade - Stage attribute is replaced by Release Phase attribute in 18x OOTB - END. 
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPHCATEGORY+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGUNITOFMEASURESYSTEM+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_SECURITYCLASIFICATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPREVIOUSREVISIONOBSOLETEDATE+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_WEIGHT+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS+"]");
		//Added by DSM(Sogeti)-2015x.2 for PDF Views  (Req Id-11470,11471,11998,11475,11474,11999,11472,11473) - Starts
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE+"].to.name");
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Ends
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINTENDEDMARKETS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGHAZARDCLASSIFICATION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY);
		//Added by DSM(Sogeti)-2015x.2 for PDF Views  (Req Id-11470,11471,11998,11475,11474,11999,11472,11473) - Ends
		//Modified by DSM(Sogeti)-2015x.1 for PDF views For defect 3070 on 12-May-2016 - Starts	
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM+"].from["+DomainConstants.TYPE_PART_FAMILY+"].name");
		//Modified by DSM(Sogeti)-2015x.1 for PDF views For defect 3070 on 12-May-2016 - Ends
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLUNITSIZE+"].to.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_ORIGINATINGTEMPLATE+"].from.name");
		//Added by DSM 2015x.2(Sogeti) for defect #7417 - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_TEMPLATE+"].to.name");
		//Added by DSM 2015x.2(Sogeti) for defect #7417 - Ends
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_AFFECTEDITEM+"].from.name");
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 1827 on 1-April-2016 - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_IMPLEMENTEDITEM+"].from.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM+"].from.name");
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 4428 on 7-June-2016 - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM+"].from.to["+pgV3Constants.RELATIONSHIP_CHANGEACTION+"].from["+pgV3Constants.TYPE_CHANGEORDER+"].name");
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 4428 on 7-June-2016 - Ends
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 1827 on 1-April-2016 - Ends
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRODUCTFORM+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIBOMBASEQUANTITY+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGBRAND+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION+"].to.name");
		//Added by V4-2013x.4 for defect 1481 - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PARTSPECIFICATION+"].to.name");
		//Added by V4-2013x.4 for defect 1481 - Ends
		//Added by DSM(Sogeti)-2015x.1 for PDF Views (Defect ID-4909) - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PARTSPECIFICATION+"].to["+pgV3Constants.TYPE_PGSTACKINGPATTERN+"].name");
		//Added by DSM(Sogeti)-2015x.1 for PDF Views (Defect ID-4909) - Ends
	
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_COOWNED+"].from.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_REGIONOWNS+"].from.name");
		//Added by V4-2013x.4 for defect 3191 - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS+"].to.name");
		//Added by V4-2013x.4 for defect 3191 - Ends
		//Added by DSM-2015x.1 for PDF Views (Req ID-5691,5692,5693,5694,5696,5697,5698,5699,5700,5701,5702,5703,5704)on 22-03-2016 - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT+"].to.name");
		//Added by DSM-2015x.1 for PDF Views (Req ID-5691,5692,5693,5694,5696,5697,5698,5699,5700,5701,5702,5703,5704)on 22-03-2016 - Ends
		//Added by DSM-2015x.1 for PDF Views (Req 11473,11998) - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSHELFLIFE);
		//Added by DSM-2015x.1 for PDF Views (Req 11473,11998) - Ends
		//Added by DSM(Sogeti)-2015x.2  for Defect ID- 10647 on 20/02/2017 - Starts
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGDENSITYUOM+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGONSHELFPRODUCTDENSITY+"].inputvalue");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGENTRYBASEQUANTITY+"]");
		//Added by DSM(Sogeti)-2015x.2  for Defect ID- 10647 on 20/02/2017- Ends
		//Added by DSM-2015x.4 for PDF Views (Req 11470,11998) - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXTRAVARIANT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_INCINAME);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_CHEMICALNAME);
		//Added by DSM-2015x.4 for PDF Views (Req 11470,11998) - Ends
		//Added by DSM-2015x.4 for PDF Views (Defect 13296) - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ORGANIZATIONID+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_PGHOUSENUMBER+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ADDRESS+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_CITY+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_STATEREGION+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_COUNTRY+"]");
		//Added by DSM(Sogeti)-2018x.2 for PDF Views Defect #20091 - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgPDFViewConstants.ATTRIBUTE_POSTALCODE+"]");
		//Added by DSM(Sogeti)-2018x.2 for PDF Views Defect #20091 - Ends
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ORGANIZATIONID+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_PGHOUSENUMBER+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ADDRESS+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_CITY+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_STATEREGION+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_COUNTRY+"]");
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Req ID-12001,5716,5701,11477,11998,19145 ) - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISTHEDESIGNCHILDSAFE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMATERIALCERTIFICATIONS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISPRODUCTCERTIFICATIONORLOCALSTANDARDSCOMPLIANCESTATEMENTREQUIRED); 
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEXPECTEDREGULATORYPRODUCTCLASSIFICATION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTDOSEPERUSE); 
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTDOSEPERUSEUOM);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPRODUCTDOSEPERUSENUMERIC);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEXPECTEDFREQUENCYOFUSE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEXPECTEDFREQUENCYOFUSEUOM); 
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMODEOFPRODUCTDISPOSAL); 
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTMARKETEDASCHILDRENPRODUCT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDOESTHEPRODUCTREQUIRECHILDSAFEDESIGN);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGINTENDEDMARKETS+"].to.name");
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Req ID-12001,5716,5701,11477,11998,19145) - Ends
		
		
		//Added by DSM-2015x.4 for PDF Views (Defect 13296) - Ends
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Defect 15716) - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXPOSEDTOCHILDREN);
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Defect 15716) - Ends
		//Added by DSM(Sogeti)-2015x.5.1 for PDF Views - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_OWNINGPRODUCTLINE+"].from["+pgV3Constants.TYPE_PG_GLOBALFORM+"].attribute["+pgV3Constants.ATTRIBUTE_MARKETING_NAME+"]");
				//Added by DSM(Sogeti)-2015x.5.1 for PDF Views - Ends
		//Modified by DSM-2015x.5.1 for PDF Views (Defect ID-18911) - Starts	
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS+"].to.name");
		//Modified by DSM-2015x.5.1 for PDF Views (Defect ID-18911) - End		
		//Added by DSM(Sogeti)-2018x.1.1 for PDF Views Requirements - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRIMARYPACKAGINGTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSECONDARYPACKAGINGTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGUNIQUEFORUMULAIDENTIFIER);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGREPLACEDBYPRODUCTID);
		//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements #12002 & #11478 - Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_pgREACHExempt);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_pgREACHStatus);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_pgExpirationDate);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PG_DSM_PRODUCTSIZE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS);
		//Added by DSM(Sogeti)-2018x.2 for PDF Views Requirements #12002 & #11478 - Ends
		//busSelect.addElement("attribute[pgReplacedByProductName]");
		//Added by DSM(Sogeti)-2018x.1.1 for PDF Views Requirements - Ends
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_IS_PRODUCT_DILUTED_FOR_USE);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PERCENT_CONCENTRATION);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PERCENT_WATER);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPAKAGINGTYPE);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTR_LEGACY_PRODUCT_WEIGHT);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTR_PG_OVER_HANG_ACTUAL_WIDTH);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTR_PG_OVER_HANG_ACTUAL_LENGTH);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTR_PG_UNDER_HANG_ACTUAL_LENGTH);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTR_PG_UNDER_HANG_ACTUAL_WIDTH);
		busSelect.addElement("to["+pgPDFViewConstants.RELATIONSHIP_PGPRODUCTPLATFORMFOP+"].from.name");
		busSelect.addElement(pgPDFViewConstants.PRIMARYPACKAGINGTYPE);
		//Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42994 Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGLPDMODELTYPE);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGLPDDEFINITION);
		//Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42994 Ends
		//Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42962,42954 Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGLPDREGION);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGLPDSUBREGION);
                busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGREASONFORCHANGEMANFSTATUS);
		//Added by (DSM Sogeti) for (2018x.6 May CW 2022) - PDF Views Req #42962,42954 Ends
	    //Added by (DSM Sogeti) for (2018x.6 JUNE CW 2022) - PDF Views Req #43479 Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGREASONFORCHANGEGENERICMODEL);
		 //Added by (DSM Sogeti) for (2018x.6 JUNE CW 2022) - PDF Views Req #43479 Ends
		//Added by (DSM Sogeti) for (2022x.01 Feb CW 2022) - PDF Views Req #45516,45503 and 45510 Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGWNDEXCPTCMT);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGWNDEXCPSUPPORTDOC);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGWNDVALEXCP);
		//Added by (DSM Sogeti) for (2022x.01 Feb CW 2022) - PDF Views Req #45516,45503 and 45510 Ends
		//Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46231 Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL1);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL2);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGCOMPONENTPACKFAMILYLEVEL3);
		//Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46231 Ends
		//Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46143 Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGCONSUMERPACKFAMILY);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGCONSUMERPRIMARYPACKAGINGTYPE);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGCONSUMERSECONDARYPACKAGINGTYPE);
		//Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46143 Ends
		//Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46172 Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGRELATIONSHIPRESTRICTION);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGRELATIONSHIPRESTRICTIONCOMMENTS);
		//Added by (DSM Sogeti) for (2022x.02 May CW 2022) - PDF Views Req #46172 Ends
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	//Added by DSM-2015x.1 for PDF Views (Req ID-8196,8198) on 15-Jan-2016 - Starts
	public static Map getMEPRelatedSelectableMap(Context context,boolean isCombinedWithMaster) throws MatrixException
	{
		StringList relSelect = new StringList(1);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISRESTRICTED);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(12);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		busSelect.addAll(getBasicInfoBusSelectable(context));
		Map mapSelecables = new HashMap();
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}

	public static Map getMEPPartSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(287);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERNAMES);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		//Modified for 2018x Upgrade - Stage attribute is replaced by Release Phase attribute in 18x OOTB - START. 
		//busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_STAGE+"]");
		busSelect.addElement("attribute["+pgV3Constants.STR_RELEASE_PHASE+"]");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		//Modified for 2018x Upgrade - Stage attribute is replaced by Release Phase attribute in 18x OOTB - END. 
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_SECURITYCLASIFICATION+"]");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGLIFECYCLESTATUS+"]");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONWIDTH);
		//Modified by DSM(Sogeti)-2015x.1 for PDF views For defect 3070 on 12-May-2016 - Starts		
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM+"].from["+DomainConstants.TYPE_PART_FAMILY+"].name");
		//Modified by DSM(Sogeti)-2015x.1 for PDF views For defect 3070 on 12-May-2016 - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLUNITSIZE+"].to.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_ORIGINATINGTEMPLATE+"].from.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_AFFECTEDITEM+"].from.name");
		//Modified by DSM(Sogeti)-2015x.1 for PDF views For defect 4004 on 26-May-2016 - Starts	
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 1827 on 1-April-2016 - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_IMPLEMENTEDITEM+"].from.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM+"].from.name");
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 1827 on 1-April-2016 - Ends
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 4428 on 7-June-2016 - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM+"].from.to["+pgV3Constants.RELATIONSHIP_CHANGEACTION+"].from["+pgV3Constants.TYPE_CHANGEORDER+"].name");
		//Added by DSM(Sogeti)-2015x.1 for PDF views For defect 4428 on 7-June-2016 - Ends
		//Modified by DSM(Sogeti)-2015x.1 for PDF views For defect 4004 on 26-May-2016 - Starts	
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRODUCTFORM+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIBOMBASEQUANTITY+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGBRAND+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PARTSPECIFICATION+"].to.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.name");
		busSelect.addElement(pgV3Constants.ATTRIBUTE_PGMANUFACTURERVENDORNUMBER);
		busSelect.addElement("altowner1");
		busSelect.addElement("altowner2");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_COOWNED+"].from.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_REGIONOWNS+"].from.name");
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_COMMENT+"]");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS+"].to.name");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISSUEDDATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMANUFACTURERVENDORNUMBER);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_COMMENTS); 
		//Added by DSM-2015x.1 for PDF Views Req Id(8196,8197)on 22-03-2016 - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT+"].to.name");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.name");
		//Added by DSM-2015x.1 for PDF Views Req Id(8196,8197)on 22-03-2016 - Ends
		//Added by DSM(Sogeti)-2015x.4 for PDF Views (Req - 8196) - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ORGANIZATIONID+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_PGHOUSENUMBER+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ADDRESS+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_CITY+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_STATEREGION+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_COUNTRY+"]");
		//Added by DSM(Sogeti)-2018x.2 for PDF Views (Req - 20091) - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.attribute["+pgPDFViewConstants.ATTRIBUTE_POSTALCODE+"]");
		//Added by DSM(Sogeti)-2018x.2 for PDF Views (Req - 20091) - Ends
		//Added by DSM(Sogeti)-2015x.4 for PDF Views (Req - 8196) - Ends
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Req - 8197) - Starts
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ORGANIZATIONID+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_PGHOUSENUMBER+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_ADDRESS+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_CITY+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_STATEREGION+"]");
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_SUPPLYRESPONSIBILITY+"].from.attribute["+pgV3Constants.ATTRIBUTE_COUNTRY+"]");
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Req - 8197) - Ends
		busSelect.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGPACKAGINGMATERIALTYPE+"]");
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Defect# - 15990) - Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSAMEASISSUEDATE);
		//Added by DSM(Sogeti)-2015x.5 for PDF Views (Defect# - 15990) - Ends
		//Added by DSM(Sogeti)-2015x.5.1 for PDF Views (Defect# - 21280) - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PG_PLI_MATERIAL_CERTIFICATIONS+"].to.name");
		//Added by DSM(Sogeti)-2015x.5.1 for PDF Views (Defect# - 21280) - Ends
		busSelect.addAll(getRelationshippgMasterDataSelectable(context));
		mapSelecables.put("busSelect", busSelect);
		mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	//Added by DSM-2015x.1 for PDF Views (Req ID-8196,8198) on 15-Jan-2016 - Ends
	//Modified by DSM(Sogeti)-2015x.4 for PDF Views for Master Attributes  - Starts
	public static Map getMasterSelectableMap(Context context,String strMasterID) throws MatrixException
	{
		Map mapInfo = new HashMap();
		StringList busSelect = new StringList(70);
		StringList relSelect = new StringList(1);
		busSelect.addElement(DomainConstants.SELECT_DESCRIPTION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		busSelect.addElement(DomainConstants.SELECT_TYPE);
		busSelect.addElement(DomainConstants.SELECT_ORIGINATOR);
		busSelect.addElement(DomainConstants.SELECT_REVISION);
		busSelect.addElement(DomainConstants.SELECT_ORIGINATED);
		busSelect.addElement(DomainConstants.SELECT_OWNER);
		//Modified for 2018x Upgrade - Stage attribute is replaced by Release Phase attribute in 18x OOTB - START.
		busSelect.addElement("attribute["+pgV3Constants.STR_RELEASE_PHASE+"]");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		//Modified for 2018x Upgrade - Stage attribute is replaced by Release Phase attribute in 18x OOTB - END. 
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT+"].to.name");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_EXPIRATION_DATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_DATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPREVIOUSREVISIONOBSOLETEDATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLOCALDESCRIPTION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_REASONFORCHANGE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLIFECYCLESTATUS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOTHERNAMES);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_COMMENT); 
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVEDATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGARCHIVECOMMENT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISBATTERY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCONTAINSBATTERY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERYTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEINFORMATION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGBRAND+"].to.name");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGINSTRUCTIONS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSHIPPINGHAZARDCLASSIFICATION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGTECHNOLOGY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDENSITYUOM);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGONSHELFPRODUCTDENSITY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINTENDEDMARKETS);
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Starts
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE+"].to.name");
		//Modified by DSM-2015x.4 for PDF Views (Defect ID-11470) - Ends
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTEXTRAVARIANT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLIFECYCLESTATUS);
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CLASSIFIEDITEM+"].from[Part Family].name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIREPORTEDFUNCTION+"].to.name");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCLASS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSUBCLASS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONHEIGHT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONLENGTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGOUTERDIMENSIONWIDTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONWIDTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONLENGTH);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINNERDIMENSIONHEIGHT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDIMENSIONUOM);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGMATERIALTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGCOMPONENTTYPE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGSIZEUOM);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPACKAGINGTECHNOLOGY);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLABELINGINFORMATION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGIPCLASSIFICATION);
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS+"].to.name");
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDECORATIONDETAILS);
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM+"].from.name");
		busSelect.addElement("to["+ pgV3Constants.RELATIONSHIP_CHANGEAFFECTEDITEM+ "].from.to["+ pgV3Constants.RELATIONSHIP_CHANGEACTION +"].from.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_TEMPLATE+"].to.name");
		
		//Added for DEFECT 32058 for PDF View -- Starts
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_STORAGECONDITIONS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSEGMENT);
		
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGPROJECTMILESTONE);
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSTRUCTUREDRELEASECRITERIAREQUIRED);
		//Added by DSM for PDF Views (Req id #47506) - Start
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PGSTRUCTUREDATSWHYWHYANALYSIS);
		//Added by DSM for PDF Views (Req id #47506) - End
		//Added by DSM 2018x.5 Requirement - Start
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGETEMPERATURELIMITS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		//Added by DSM 2018x.5 Requirement - End
		//Added for DEFECT 32058 for PDF View -- Ends
		//	busSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSTORAGEHUMIDITYLIMITS);
		
		
		
		
		
		DomainObject partSpecObj1 = DomainObject.newInstance(context, strMasterID);
		mapInfo = (Map)partSpecObj1.getInfo(context, busSelect);
		return mapInfo;
	}
//Modified by DSM(Sogeti)-2015x.4 for PDF Views for Master Attributes  - Ends
//Added by DSM Sogeti for 2015x.5 REQID#19633  - Starts
//P&G: Addition for V4 PSUB PDF views - Starts
	/**
	 *
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static Map getpgStudyProtocolSelectableMap(Context context) throws MatrixException
	{
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(193);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		StringList relSelect = new StringList(1);
		Map mapSelecables = new HashMap();
		busSelect.addAll(getBasicInfoBusSelectable(context));
		busSelect.addAll(getCommonDataBusSelectable(context));
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYLAUNCHDATE);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYIMPLEMENTSSUPPLIED);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYSUPERVISED);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYBLANKETREQUEST);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYCOMPETITIVEPRODUCTPART);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYSKINLAB);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_NOOFSTUDYPANELISTS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLOWERAGELIMIT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGUPPERRAGELIMIT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYNOOFPRODUCTSORVERSION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYCOMPETITIVEPRODUCTPART);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGRETURNADDRESS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPANELISTPARTICIPATION);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYSPECIFYNOOFVISITS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYPANELISTSHOMEWORK);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYPANELISTEMAIL);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYPANELISTMATERIALS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYADDRESS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYAGREEMENT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYCDASIGNED);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYPANELISTPURCHASEINTENT);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNOOFPRODUCTSVERSIONSPERSONEXP);
		//Added by IRM Team for Defect 16696 for 2015x.5 - STARTS
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTUDYRELATEDPROJECTS);
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGGPSTASKASSESSMENTCOUNTRY+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOASSEMBLYSTATE+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOSURPLUSDISPOSITION+"].to.name");
		//Added by IRM Team for Defect 16696 for 2015x.5 - ENDS
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLIPRINTINGPROCESS+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTORESEARCHTYOE+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOGENDER+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOPANELISTTYPE+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTORACE+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTODATACOLLECTIONMETHODS+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOSTUDYLOCATIONS+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOSTUDYTYPE+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOADDITIONALSERVICES+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTODATAMERGE+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOCONFIDENCELEVEL+"].to.name");
		busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGSTUDYPROTOCOLTOASSESSMENTREQUIRED+"].to.name");
		//busSelect.add("from["+pgV3Constants.RELATIONSHIP_PGDOCUMENTTOPLATFORM+"].to.name");
		//busSelect.add("from["+pgV3Constants.RELATIONSHIP_PGDOCUMENTTOCHASSIS+"].to.name");
		
		//Platform 2018x.3 : Platform, Chassis and Business Area will be saved as attribute value instead of connection for IRM Documents : Start
		String ATTRIBUTE_BUSINESSAREA = PropertyUtil.getSchemaProperty(context, "attribute_pgBusinessArea");
		String ATTRIBUTE_FRANCHISEPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgFranchisePlatform");
		String ATTRIBUTE_PRODUCTCATEGORYPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgProductCategoryPlatform");
		String ATTRIBUTE_PRODUCTTECHNOLOGYPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgProductTechnologyPlatform");
		String ATTRIBUTE_PRODUCTTECHNOLOGYCHASSIS = PropertyUtil.getSchemaProperty(context, "attribute_pgProductTechnologyChassis");
		String ATTRIBUTE_PRODUCTPROCESSPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgProductProcessPlatform");
		String ATTRIBUTE_PRODUCTEQUIPMENTPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgProductEquipmentPlatform");
		String ATTRIBUTE_PACKAGEPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgPackagePlatform");
		String ATTRIBUTE_PACKAGECHASSIS = PropertyUtil.getSchemaProperty(context, "attribute_pgPackageChassis");
		String ATTRIBUTE_PACKAGEPROCESSPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgPackageProcessPlatform");
		String ATTRIBUTE_MATERIALPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgMaterialPlatform");
		String ATTRIBUTE_TECHNICALBUILDINGBLOCKS = PropertyUtil.getSchemaProperty(context, "attribute_pgTechnicalBuildingBlocks");
		String ATTRIBUTE_PACKAGEEQUIPMENTPLATFORM = PropertyUtil.getSchemaProperty(context, "attribute_pgPackageEquipmentPlatform");

		String SELECT_ATTRIBUTE_FRANCHISEPLATFORM = "attribute["+ATTRIBUTE_FRANCHISEPLATFORM+"]";
		String SELECT_ATTRIBUTE_PRODUCTCATEGORYPLATFORM = "attribute["+ATTRIBUTE_PRODUCTCATEGORYPLATFORM+"]";
		String SELECT_ATTRIBUTE_PRODUCTTECHNOLOGYPLATFORM = "attribute["+ATTRIBUTE_PRODUCTTECHNOLOGYPLATFORM+"]";
		String SELECT_ATTRIBUTE_PRODUCTTECHNOLOGYCHASSIS = "attribute["+ATTRIBUTE_PRODUCTTECHNOLOGYCHASSIS+"]";
		String SELECT_ATTRIBUTE_PRODUCTPROCESSPLATFORM = "attribute["+ATTRIBUTE_PRODUCTPROCESSPLATFORM+"]";
		String SELECT_ATTRIBUTE_PRODUCTEQUIPMENTPLATFORM = "attribute["+ATTRIBUTE_PRODUCTEQUIPMENTPLATFORM+"]";
		String SELECT_ATTRIBUTE_PACKAGEPLATFORM = "attribute["+ATTRIBUTE_PACKAGEPLATFORM+"]";
		String SELECT_ATTRIBUTE_PACKAGECHASSIS = "attribute["+ATTRIBUTE_PACKAGECHASSIS+"]";
		String SELECT_ATTRIBUTE_PACKAGEPROCESSPLATFORM = "attribute["+ATTRIBUTE_PACKAGEPROCESSPLATFORM+"]";
		String SELECT_ATTRIBUTE_MATERIALPLATFORM = "attribute["+ATTRIBUTE_MATERIALPLATFORM+"]";
		String SELECT_ATTRIBUTE_TECHNICALBUILDINGBLOCKS = "attribute["+ATTRIBUTE_TECHNICALBUILDINGBLOCKS+"]";
		String SELECT_ATTRIBUTE_PACKAGEEQUIPMENTPLATFORM = "attribute["+ATTRIBUTE_PACKAGEEQUIPMENTPLATFORM+"]";
		String SELECT_ATTRIBUTE_BUSINESSAREA = "attribute["+ATTRIBUTE_BUSINESSAREA+"]";
		
		busSelect.addElement(SELECT_ATTRIBUTE_FRANCHISEPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_PRODUCTCATEGORYPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_PRODUCTTECHNOLOGYPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_PRODUCTTECHNOLOGYCHASSIS);
		busSelect.addElement(SELECT_ATTRIBUTE_PRODUCTPROCESSPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_PRODUCTEQUIPMENTPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_PACKAGEPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_PACKAGECHASSIS);
		busSelect.addElement(SELECT_ATTRIBUTE_PACKAGEPROCESSPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_MATERIALPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_TECHNICALBUILDINGBLOCKS);
		busSelect.addElement(SELECT_ATTRIBUTE_PACKAGEEQUIPMENTPLATFORM);
		busSelect.addElement(SELECT_ATTRIBUTE_BUSINESSAREA);
		//Platform 2018x.3 : Platform, Chassis and Business Area will be saved as attribute value instead of connection for IRM Documents : End
		
		//Added by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		busSelect.add(pgPDFViewConstants.SELECT_ATTR_PANELIST_EXCLUSION_DETAILS);
		busSelect.add(pgPDFViewConstants.SELECT_ATTR_PANELIST_INCLUSION_DETAILS);
		busSelect.add("from[" + pgPDFViewConstants.RELATIONSHIP_PGSTUDYPROTOCOLTOPLIPANELISTSUPERVISION + "].to.name");
		busSelect.add("from[" + pgV3Constants.RELATIONSHIP_PGDOCUMENTTOBUSINESSAREA + "].to.name");
		//Added by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		mapSelecables.put("busSelect", busSelect);
		//mapSelecables.put("relSelect", relSelect);
		return mapSelecables;
	}
	/**
	 *
	 * @param context
	 * @return StringList
	 * @throws MatrixException
	*/
	public static StringList getSelectblesForLeg(Context context) throws MatrixException
	{
		//Added By IRM Team for defect 16269--Starts
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Starts
		StringList busSelect = new StringList(7);
		//Modified by DSM(Sogeti) for Defect #37590 for 2018x.5_December PDF Views - Ends
		busSelect.addElement(DomainConstants.SELECT_NAME);
		busSelect.addElement(DomainConstants.SELECT_ID);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_COMMENTS); 
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNUMBEROFPANELISTS);
		busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPLACEMENTS);
		busSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.name");
		//Added for Leg Table for Requirement 32753 - Starts
		busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_PRODUCTCODEPANELISTS);
		//Added for Leg Table for Requirement 32753 - Ends
		//Added By IRM Team for defect 16269--Ends
		return busSelect;
	}
	/**
	 *
	 * @param context
	 * @return StringList
	 * @throws MatrixException
	*/
	public static StringList getSelectblesForLegPartDetails(Context context) throws MatrixException
	{
		//Added By IRM Team for defect 16269--Starts
		StringList relSelect = new StringList(35);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGWASHOUTTIMEBETWEENPRODUCTS);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSTABILITYSAMPLECOUNT);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGANALYTICALSAMPLECOUNT);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGRNDTEAMSAMPLECOUNT);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINVENTORYSAMPLECOUNT);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNUMBEROFUNITSTOSHIP);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTCODE);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGNETCONTENTOFPRODUCTINPACKAGE);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLOTNUMBER);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBLINDCODE);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATCH);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDOSAGE);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGFREQUENCY);
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGUSAGEPERIOD);
		relSelect.addElement("frommid["+pgV3Constants.RELATIONSHIP_PGSTUDYLEGTOFREQUENCYUOM+"].to.name");
		relSelect.addElement("frommid["+pgV3Constants.RELATIONSHIP_PGSTUDYLEGTOWASHOUTTIMEUOM+"].to.name");
		relSelect.addElement("frommid["+pgV3Constants.RELATIONSHIP_PGSTUDYLEGTOREGULATORYCLASSIFICATION+"].to.name");
		relSelect.addElement("frommid["+pgV3Constants.RELATIONSHIP_PGSTUDYLEGTODOSAGEUOM+"].to.name");
		relSelect.addElement("frommid["+pgV3Constants.RELATIONSHIP_PGSTUDYLEGTOUSAGETIMEUOM+"].to.name");
		relSelect.addElement("frommid["+pgV3Constants.RELATIONSHIP_PGSTUDYLEGTOPRODUCTSOURCE+"].to.name");
		//Added for Leg Table for Requirement 32753 - Starts
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_COMMENTS); 
		relSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPLACEMENTS);
		relSelect.addElement("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.name");
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_SITEIFOTHERSELECTED);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_FREQUENCYSAMEASCURRENTMARKETPRODUCT);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_NUMBEROFUNITSPERBAG);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_RELPACKINGSITE);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_MICROMCT);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_PANELISTCOMPLETES);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_UNITSTOLABEL);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_LABELLING);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_ISPRODUCYREPACKAGED);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_UNITWEIGHT);
		relSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_PG_EBPSTUDY);
		relSelect.addElement("tomid["+pgPDFViewConstants.RELATIONSHIP_PG_MANUFACTURINGRESPONSIBILITY_LEG+"].from.name");
		//Added for Leg Table for Requirement 32753 - Ends
		//Added By IRM Team for defect 16269--Ends
		return relSelect;
	}
	//Added by DSM Sogeti for 2015x.5 REQID#19633  - Ends
	//Start Code Refactoring
	public static StringList slAllInfoTypes = null;

	public static StringList getAllInfoTypes() {
		try {
			if (slAllInfoTypes != null && !slAllInfoTypes.isEmpty()) {
				return slAllInfoTypes;
			} else {
				slAllInfoTypes = new StringList();
				slAllInfoTypes.addElement(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGINNERPACKUNITPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGMASTERINNERPACKUNITPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGONLINEPRINTINGPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGMASTERPACKAGINGMATERIALPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGMASTERPACKAGINGASSEMBLYPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGMASTERCUSTOMERUNITPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGTRANSPORTUNITPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGCONSUMERUNITPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGPROMOTIONALITEMPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_PGMASTERCONSUMERUNITPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_FABRICATEDPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_DEVICEPRODUCTPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_RAWMATERIALPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_MASTERRAWMATERIALPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_MASTERPRODUCTPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_FORMULATIONPART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART);
				slAllInfoTypes.addElement(pgV3Constants.TYPE_SOFTWAREPART);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return slAllInfoTypes;
	}
	//End Code Refactoring
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Starts
	/**
	 * This method map of non-structure data type 
	 * @param context
	 * @param strObjectId
	 * @return
	 */
	public static Map<String,StringList> getNonStructuredPartSelectableMap(Context context){
		Map<String,StringList> mapSelecables = new HashMap<>();
		try {
			StringList busSelect = new StringList(19);
			busSelect.addAll(getBasicInfoBusSelectable(context));
			busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_REASONFORCHANGE);
			busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGLOCALDESCRIPTION);
			busSelect.addElement(pgPDFViewConstants.SELECT_ATTRIBUTE_IMPACTED_TYPE);
			busSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION+"].to.name");
			busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_REFERENCEDOCUMENT+"].to.name");
			busSelect.addElement("from["+pgV3Constants.RELATIONSHIP_PGPDTEMPLATESTOPGPLISEGMENT+"].to.name");
			mapSelecables.put("busSelect", busSelect);
		} catch(Exception e){
			e.printStackTrace();
		}
		return mapSelecables;
	}
	//Added by DSM(Sogeti)-2018x.5 for PDF Views Requirements 35306, 32960, 32964, 32965, 32966, 35338, 34926, 32866 and 32869 POA ATS Gendoc- Ends
}




