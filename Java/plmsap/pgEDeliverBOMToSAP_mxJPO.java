import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.enovia.formulation.custom.enumeration.FormulationAttribute;
//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends
//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
import com.dassault_systemes.enovia.formulation.custom.virtualintermediates.model.VirtualIntermediate;
//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
import com.matrixone.apps.awl.util.BusinessUtil;
//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends
//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
//import com.matrixone.apps.cpn.util.BusinessUtil;
//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.sapview.enumeration.SAPViewConstant;
import com.pg.dsm.sapview.interfaces.ISAPBOM;
import com.pg.dsm.sapview.services.SAPBOM;
import com.pg.dsm.sapview.services.SAPReshipperBOM;
import com.pg.dsm.sapview.utils.MarkUtils;
import com.pg.util.EncryptCrypto;
import com.pg.v3.custom.pgV3Constants;
import com.sap.mw.jco.IRepository;
import com.sap.mw.jco.JCO;
import com.sap.mw.jco.JCO.Field;
import com.sap.mw.jco.JCO.ParameterList;
import com.sap.mw.jco.JCO.Structure;
import com.sap.mw.jco.JCO.Table;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.Pattern;
import matrix.util.StringList;

public class pgEDeliverBOMToSAP_mxJPO {

	public static final StringList SL_OBJECT_INFO_SELECT = getObjectSelects();
	public static final StringList SL_RELATION_INFO_SELECT_EBOM = getRelationshipSelectsEBOM();
	public static final StringList SL_RELATION_INFO_SELECT_OTHERS = getRelationshipSelectsOthers();
	public static final StringList SL_FOP_OBJECT_INFO_SELECT = getFOPObjectSelects();
	private boolean bExecuteRFC = true;
	private boolean bSitesListActive = false;
	private String strDesignatedSites = DomainConstants.EMPTY_STRING;
	private static String strSuccess = "BOM Successfully Processed";
	public static final String PATTERN_DECIMALFORMAT = "##.###";
	public static final String TYPE_INTERMEDIATEPRODUCTPART = PropertyUtil.getSchemaProperty("type_pgIntermediateProductPart");
	public static final int OPT_COMPONENT = 24;
	public static final int BOM_ARRAY_SIZE = 26;
	public static final int COMMENT = 25;
	public static final int OBJECT_ID = 22;
	public static final int DISPLAY_OBJECT_ID = 23;
	public static final String PGPHASE_EXPAND_TYPES = "pgFinishedProduct,pgFormulatedProduct,pgRawMaterial,pgPackingMaterial,pgPackingSubassembly";
	//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Starts
	public static final String ALTERNATE_ALLOWED_TYPES = pgV3Constants.TYPE_RAWMATERIALPART + "," + pgV3Constants.TYPE_PGRAWMATERIAL + "," + pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART + "," + pgV3Constants.TYPE_FABRICATEDPART + "," + pgV3Constants.TYPE_PACKAGINGMATERIALPART + "," + pgV3Constants.TYPE_PACKAGINGASSEMBLYPART;
	public static final String COP_BULK_ALTERNATES_ALLOWED_TYPES = pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART + "," + pgV3Constants.TYPE_PACKAGINGASSEMBLYPART;
	//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Ends
	public String strParentRev = "";
	public ArrayList alBOMFC = new ArrayList();
	public String _AbortReason = "";
	public String _ParentMatlNumber = "";
	private boolean bCreatingSubsRows = false;
	private String strSubsDispId = "";

	public String strFirstlevelObjId = "";
	public String strFirstlevelObjIdCopy = "";
	HashMap hmParentObjects = new HashMap();
	HashMap hmChildObjects = new HashMap();
	int iChildObjCount = 0;

	private String strParentBUOM = DomainConstants.EMPTY_STRING;
	private String strParentType = DomainConstants.EMPTY_STRING;
	// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Starts
	public static  Logger logger = Logger.getLogger("pgEDeliverBOMToSAP_mxJPO");
	// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Ends

	public pgEDeliverBOMToSAP_mxJPO() throws Exception {
	}


	public int mxMain(Context context, String args[]) throws Exception {
		return 1;
	}

	/**
	 * Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)
	 * gets the SAP Configuration details from config object
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @return String array
	 * @throws Exception
	 */

	public synchronized String[] getConfig(Context context, boolean inUse) throws Exception {

		String[] sarySAPConfig = new String[5];

		try {

			String strSAPPass = DomainConstants.EMPTY_STRING;
			String strSAPCommonAttr = DomainConstants.EMPTY_STRING;

			int iIndex = -1;

			Map mpSAPConfData = new HashMap();

			StringList slSAPConf = new StringList(4);

			slSAPConf.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSERVERHOST);
			slSAPConf.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGJMSUSERNAME);
			slSAPConf.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPASSWORD);
			slSAPConf.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONATTR);

			// DomainObject doesn't have APIs to create instance by passing TNR.
			// Using BusinessObject Instance

			DomainObject doSAPConfigObj = DomainObject.newInstance(context, new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, pgV3Constants.CONFIG_OBJECT_PGSENDPLANTSSAPCONFIG, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION));
			// BusinessObject doesn't require DB Hit as context is getting passed to DomainObject but NOT to businessobject.
			// No need to check null for DomainObject, it will throw Framework Exception if no TNR found in Enovia DB

			mpSAPConfData = doSAPConfigObj.getInfo(context, slSAPConf);

			if (null != mpSAPConfData) {

				sarySAPConfig[0] = (String) mpSAPConfData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSERVERHOST);

				sarySAPConfig[1] = (String) mpSAPConfData.get(pgV3Constants.SELECT_ATTRIBUTE_PGJMSUSERNAME);

				strSAPPass = (String) mpSAPConfData.get(pgV3Constants.SELECT_ATTRIBUTE_PGPASSWORD);

				strSAPCommonAttr = (String) mpSAPConfData.get(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGCOMMONATTR);

				sarySAPConfig[2] = EncryptCrypto.decryptString(strSAPPass);

				iIndex = strSAPCommonAttr.indexOf("|");

				if (iIndex > 0) {

					sarySAPConfig[3] = strSAPCommonAttr.substring(0, iIndex);

					sarySAPConfig[4] = strSAPCommonAttr.substring(iIndex + 1);


				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			//throw new Exception(e.getMessage());
		}

		return sarySAPConfig;
	}


	/**
	 * Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)
	 * Establishes the Enovia - SAP connection and stores data to SAP.
	 *
	 * @param String Array, String, String Array, ArrayList
	 * @throws Exception
	 */

	public synchronized String initSapConnectionandStore(String[] strArrConnectionParams, String strGCAS, String[] strArrEligiblePlants, ArrayList alPartRelatedObjects, boolean inUse) throws Exception {

		String strMessage = strSuccess;
		String[][] saBOMData = null;
		if (strArrConnectionParams != null && strArrConnectionParams.length == 5) {
			// Create a client connection to R/3 system
			try {

				String strSAPHostName = strArrConnectionParams[0]; // SAP Host Name
				String strSAPUserId = strArrConnectionParams[1]; // SAP User ID to push data
				String strSAPPassword = strArrConnectionParams[2]; // SAP encrypted password
				String strSAPClientNumber = strArrConnectionParams[3]; // SAP client number
				String strSAPSystemNumber = strArrConnectionParams[4]; // SAP system number

				if (BusinessUtil.isNotNullOrEmpty(strSAPHostName) && BusinessUtil.isNotNullOrEmpty(strSAPUserId) && BusinessUtil.isNotNullOrEmpty(strSAPPassword) && BusinessUtil.isNotNullOrEmpty(strSAPClientNumber) && BusinessUtil.isNotNullOrEmpty(strSAPSystemNumber)) {

					if (BusinessUtil.isNotNullOrEmpty(alPartRelatedObjects)) {
						// Convert data to be pushed from ArrayList to String double array in format of row and cloumn as required by SAP push APIs.

						saBOMData = convertArrayListToArray(alPartRelatedObjects, pgV3Constants.INDEX_BOM_ARRAY_SIZE);

						if (null != saBOMData && saBOMData.length > 0) {
							// Once connection is established successfully the actual data push happens through the method sendDataToSAP
							//Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)
							strMessage = sendDataToSAP(saBOMData, strArrEligiblePlants, strSAPClientNumber, strSAPUserId, strSAPPassword, pgV3Constants.SAP_LANGUAGE, strSAPHostName, strSAPSystemNumber, true);
							// The method sendDataToSAP() returns error message or success message as "BOM Successfully Processed"
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				//throw new Exception(e.getMessage());
			}
		}
		// The message from SAP side is captured and forwarded to calling method.
		return strMessage;
	}

	//Added by DSM(Sogeti) - 2015x.4 Change methods to invokable format - Starts
	public synchronized ArrayList performDataReadFromEnovia(Context context, String[] args) throws Exception {

		return performDataReadFromEnovia(context, args[0]);

	}
	//Added by DSM(Sogeti) - 2015x.4 Change methods to invokable format - Ends

	/**
	 * Gets details from Enovia DB related to the part object being passed.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String
	 * @return ArrayList
	 * @throws Exception
	 */


	public synchronized ArrayList performDataReadFromEnovia(Context context, String sPartObjectID) throws Exception {
		Map mapPartDetails = new HashMap();
		ArrayList alPartRelatedObjects = new ArrayList();

		String strSpecSubType = DomainConstants.EMPTY_STRING;
		String strFormulationType = DomainConstants.EMPTY_STRING;
		String strPartType = DomainConstants.EMPTY_STRING;

		DomainObject doPart = null;
		StringList slBusSelect = new StringList(13);
		slBusSelect.add(DomainConstants.SELECT_TYPE);
		slBusSelect.add(DomainConstants.SELECT_NAME);
		slBusSelect.add(DomainConstants.SELECT_ID);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		slBusSelect.add("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from." + pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);
		slBusSelect.add("next.id");
		/*
		 * 	1.	The Object type needs to be checked to make sure if it is one of the following types
						Formula Card
						Finished Product with blank Subassembly Type
						Finished Product with FWIP Subassembly Type
						Packing Subassembly with subassembly type other than Purchased Subassembly
						Finished Product Part (FPP)
						Packing Assembly Part (PAP)
						Formulation Part(FOP)-> it doesnt have EBOM but diff relationship
						Assembled Product (APP)
						Device Product (DPP)
				2.	BOM eDelivery shall be done only for Production Stage with Usage 3.
				3.	Check if object is connected to atleast one Plant with Authorize to
    				Produce and Plants lociation listed in one of the SAP plant sites in the Config Object.
    				(pgSAPDesignatedSites DesignatedSitesObj -)
		 */

		try {

			if (BusinessUtil.isNotNullOrEmpty(sPartObjectID)) {

				pgEDeliverBOMToSAP_mxJPO sapLoad = new pgEDeliverBOMToSAP_mxJPO();

				// The method doTypeCheckForeDelivery() expects String array as input . This method gets invoked from a jsp as well.
				doPart = DomainObject.newInstance(context, sPartObjectID);
				mapPartDetails = doPart.getInfo(context, slBusSelect);

				/*
				 * 	1.	The Object type needs to be checked to make sure if it is one of the following types
						Formula Card
						Finished Product with blank Subassembly Type
						Finished Product with FWIP Subassembly Type
						Packing Subassembly with subassembly type other than Purchased Subassembly
						Finished Product Part (FPP)
						Packing Assembly Part (PAP)
						Formulation Part(FOP)-> it doesnt have EBOM but diff relationship
						Assembled Product (APP)
						Device Product (DPP)
					2.	BOM eDelivery shall be done only for Production Stage with Usage 3.
					3.	Check if object is connected to atleast one Plant with Authorize to
						Produce and Plants lociation listed in one of the SAP plant sites in the Config Object.
						(pgSAPDesignatedSites DesignatedSitesObj -)
				 */

				if (null != mapPartDetails && mapPartDetails.size() > 0) {
					strSpecSubType = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
					//Modified by DSM(Sogeti) for 2018x.1.1 September Release SAP BOM as Fed Defect#29366  - Starts
					//strFormulationType = (String) mapPartDetails.get("to["+pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE+"].from."+pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);
					Object OFormulationType = (Object) mapPartDetails.get("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from." + pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);

					StringList slFormulationType = new StringList();

					if (null != OFormulationType) {
						if (OFormulationType instanceof StringList) {
							slFormulationType = (StringList) OFormulationType;
						} else if (OFormulationType instanceof String) {
							slFormulationType.add(OFormulationType.toString());
						}
					}

					//Modified by DSM(Sogeti) for 2018x.1.1 September Release SAP BOM as Fed Defect#29366  - Ends
					if (sapLoad.doProdUsageCheckForeDelivery(context, mapPartDetails)) {

						/*
						 *	Get the EBOM, Substitute and Alternates for the given Object.
						 *  For each EBOM components/Substitute/Alternate , check the Eligibility to process the BOM for eDelivery.
						 */
						strPartType = (String) mapPartDetails.get(DomainConstants.SELECT_TYPE);

						if (BusinessUtil.isNotNullOrEmpty(strPartType)) {

							if (pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(strPartType) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strPartType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strPartType)) {

								/*
								 *	Uses existing CSS JAR to fetch the data.
								 *
								 */
								alPartRelatedObjects = sapLoad.getPartObjectsFromEBOM(context, mapPartDetails);

							} else if (pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(strPartType) && !slFormulationType.isEmpty() && !slFormulationType.contains(pgV3Constants.THIRD_PARTY_FORMULA)) {

								alPartRelatedObjects = sapLoad.getObjectsForFOPFromFormulaIngredient(context, mapPartDetails);

							} else if (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strPartType) || (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strPartType) && !pgV3Constants.THIRD_PARTY.equals(strSpecSubType))) {

								alPartRelatedObjects = sapLoad.getPartObjectsFromIntermediateBOM(context, sPartObjectID);

							} else if (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strPartType) || TYPE_INTERMEDIATEPRODUCTPART.equalsIgnoreCase(strPartType) || ((pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(strPartType) || pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strPartType)) && !pgV3Constants.THIRD_PARTY.equals(strSpecSubType))) {

								alPartRelatedObjects = sapLoad.getPartObjectsFromBOM(context, sPartObjectID);

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw new Exception(e.getMessage());
		}
		return alPartRelatedObjects;
	}
	/*
	 * 	The Object type needs to be checked to make sure if it is one of the following types
			Formula Card
			Finished Product with blank Subassembly Type
			Finished Product with FWIP Subassembly Type
			Packing Subassembly with subassembly type other than Purchased Subassembly
			Finished Product Part (FPP)
			Packaging Assembly Part (PAP)
			Formulation Part (FMP)
			Assembled Product (APP)
			Device Product (DPP)

	 */
	// This method expects String array and same is being used by other code components.

	/**
	 * Checks the validity of the object type.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String  Array
	 * @return boolean
	 * @throws Exception
	 */

	public synchronized boolean doTypeCheckForeDelivery(Context context, String strArrPart[]) throws Exception {

		boolean isTypeAllowed = false;

		if (null != strArrPart && strArrPart.length > 0) {

			String sPartObjectID = strArrPart[0];
			String sPartObjectType = DomainConstants.EMPTY_STRING;
			String SubassemblyType = DomainConstants.EMPTY_STRING;
			String sPartPolicy = DomainConstants.EMPTY_STRING;

			StringList slPartSelect = new StringList(3);
			slPartSelect.addElement(DomainConstants.SELECT_TYPE);
			slPartSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			slPartSelect.addElement(DomainObject.SELECT_POLICY);

			DomainObject domObject = null;
			Map mpPartObjectInfo = new HashMap();

			try {

				if (BusinessUtil.isNotNullOrEmpty(sPartObjectID)) {

					domObject = DomainObject.newInstance(context, sPartObjectID);

					mpPartObjectInfo = domObject.getInfo(context, slPartSelect);

					if (null != mpPartObjectInfo && mpPartObjectInfo.size() > 0) {

						sPartObjectType = (String) mpPartObjectInfo.get(DomainConstants.SELECT_TYPE);
						SubassemblyType = (String) mpPartObjectInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
						sPartPolicy = (String) mpPartObjectInfo.get(DomainObject.SELECT_POLICY);
						if (isAllowedTypeForSAPBOM(context, sPartObjectType)) {
							isTypeAllowed = true;
							if (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sPartObjectType)) {

								// Finished Product with blank Sub assembly Type OR Finished Product with FWIP Sub assembly Type
								// Finished Product Part (FPP)

								if (BusinessUtil.isNotNullOrEmpty(SubassemblyType) && !SubassemblyType.equals(pgV3Constants.PGASSEMBLY_TYPE_FWIP)) {

									isTypeAllowed = false;
								}

								// Packing Subassembly with subassembly type other than Purchased Subassembly
							} else if (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sPartObjectType)) {

								if (BusinessUtil.isNotNullOrEmpty(SubassemblyType) && SubassemblyType.equalsIgnoreCase(pgV3Constants.SUBASSEMBLY_TYPE_PURCHASED)) {

									isTypeAllowed = false;

								}
							}
							//Added by DSM(Sogeti) for 2015x.5 SAP BOM as Fed Req 19833 (Parallel clone) - Starts
							else if ((pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sPartObjectType) || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sPartObjectType) || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(sPartObjectType)) && (pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(sPartPolicy))) {
								isTypeAllowed = false;
							}
							//Added by DSM(Sogeti) for 2015x.5 SAP BOM as Fed Req 19833 (Parallel clone) - Ends
						}
						//Modified by DSM(Sogeti) for 2015x.4 SAP BOM as Fed Req 14361 - Ends

					}

				}

			} catch (Exception e) {

				e.printStackTrace();
				isTypeAllowed = false;
			}
		}
		return isTypeAllowed;
	}

	/**
	 * This method checks view access for command pgIPMSAPBOM
	 *
	 * @param Context
	 * @param String  array contains objectid
	 * @return boolean
	 * @throws Exception
	 * @author Sogeti
	 */
	public boolean hasAccessToSAPBOMAsFED(Context context, String[] args) throws Exception {
		boolean hasAccess = false;
		//JPO emxCPNENCActionLinkAccess is deleted, moving method isEBOMIndentedTable to JPO pgDSOUtil
		Boolean isEBOMIndentedTable = (Boolean) JPO.invoke(context, "pgDSOUtil", null, "isEBOMIndentedTable", args, Boolean.class);
		if (isEBOMIndentedTable) {
			HashMap programMap = (HashMap) JPO.unpackArgs(args);
			String[] objectId = new String[1];
			objectId[0] = (String) programMap.get("objectId");
			hasAccess = doTypeCheckForeDelivery(context, objectId);
		}
		return hasAccess;

	}

	/**
	 * This method checks if the type of the Object is in Allowed Type List
	 *
	 * @param Context
	 * @param String  type to check
	 * @return boolean
	 * @throws Exception
	 * @author Sogeti
	 */
	public boolean isAllowedTypeForSAPBOM(Context context, String strType) throws Exception {
		boolean bAllowedType = false;
		String strDSAllowedTypes = UINavigatorUtil.getI18nString("emxCPN.SAPBOM.AllowedTypes", "emxCPN", context.getLocale().getLanguage());
		if (BusinessUtil.isNotNullOrEmpty(strType) && strDSAllowedTypes.contains(strType)) {
			bAllowedType = true;
		}
		return bAllowedType;
	}

	/*
	 *	BOM eDelivery shall be done only for Production Stage with Usage 3.
	 *	If the Manufacturing Status of Parent Object is PLANNING ,
		    then PLM sends BOM USAGE as 2,
		    otherwise BOM USAGE will be sent as 3.
	 *  If the Manufacturing Status of Parent Object is EXPERIMENTAL, then BOM will not be eDelivered.
	 *  Manufacturing Status logic will not be used.
		    If the stage is Production, then send the BOM Usage as 3 and
		    then check the Authorized to Produce Plants to SAP,
		    otherwise no BOM eDelivery.
	 */

	/**
	 * Checks the validity of the Production Stage.
	 * BOM eDelivery shall be done only for Production Stage with Usage 3.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param Map
	 * @return boolean
	 * @throws Exception
	 */

	public synchronized boolean doProdUsageCheckForeDelivery(Context context, Map mapPartDetails) throws Exception {

		boolean bProdStage3 = false;
		String strStage = DomainConstants.EMPTY_STRING;
		String strStatus = DomainConstants.EMPTY_STRING;
		try {

			if (null != mapPartDetails && mapPartDetails.size() > 0) {
				strStatus = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
				strStage = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);

				if (BusinessUtil.isNotNullOrEmpty(strStatus) && !"Unknown".equalsIgnoreCase(strStatus) && !pgV3Constants.RANGE_ATTRIBUTE_STATUS_PLANNING.equalsIgnoreCase(strStatus) && !pgV3Constants.RANGE_ATTRIBUTE_PGBOMBASEQUANTITY_EXPERIMENTAL.equalsIgnoreCase(strStatus)) {

					bProdStage3 = true;
				}

				if (!bProdStage3 && BusinessUtil.isNotNullOrEmpty(strStage) && !pgV3Constants.RANGE_ATTRIBUTE_STATUS_PLANNING.equalsIgnoreCase(strStage) && !pgV3Constants.RANGE_ATTRIBUTE_PGBOMBASEQUANTITY_EXPERIMENTAL.equalsIgnoreCase(strStage)) {

					bProdStage3 = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw new Exception(ex.getMessage());
		}
		return bProdStage3;
	}

	/*
	 *	Check if object is connected to atleast one Plant with Authorize to Produce and Plants
	 *	location listed in one of the SAP plant sites in the Config Object.
	 *	(pgSAPDesignatedSites DesignatedSitesObj -)
	 */

	public synchronized boolean doPlantCheckForeDelivery(Context context, String strPartId) throws Exception {
		boolean bPlantConnected = false;
		String[] strArrPlants = null;

		try {
			strArrPlants = getEligiblePlants(context, strPartId);
			if (null != strArrPlants) {
				for (int iPlantCount = 0; iPlantCount < strArrPlants.length; iPlantCount++) {
					String strPlant = strArrPlants[iPlantCount];
					//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
					//if(BusinessUtil.isNotNullOrEmpty(strPlant) && !pgV3Constants.DUMMY_PLANT_VALUE_3151.equalsIgnoreCase(strPlant)) {
					if (BusinessUtil.isNotNullOrEmpty(strPlant) && !strPlant.contains(pgV3Constants.DUMMY_PLANT_VALUE_3151)) {
						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
						bPlantConnected = true;
						break;
					}
				}


			}

		} catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		return bPlantConnected;
	}


	/*
	 *	Get the EBOM, Substitute and Alternates for the given Object.
	 *  For each EBOM components/Substitute/Alternate , check the Eligibility to process the BOM for eDelivery.
	 *
	 *	Masters components will not be included in BOM eDelivery
			Any component of SAP type as "DOC" will not be eDelivered.
	 *

	 */

	/**
	 * Get the EBOM, Substitute and Alternates for types Formula card, Finished Product and Packing Subassembly.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param Map
	 * @return ArrayList
	 * @throws Exception
	 */

	public synchronized ArrayList getPartObjectsFromEBOM(Context context, Map mapPartDetails) throws Exception {

		MapList mlPartObjects = new MapList();
		ArrayList alPartRelatedObjects = new ArrayList();

		String strType = DomainConstants.EMPTY_STRING;
		String strName = DomainConstants.EMPTY_STRING;
		String strRev = DomainConstants.EMPTY_STRING;
		String strFindNumb = DomainConstants.EMPTY_STRING;
		String strEffectivityDate = DomainConstants.EMPTY_STRING;
		String strParentEffectivityDate = DomainConstants.EMPTY_STRING;
		String sChildName = DomainConstants.EMPTY_STRING;
		String sQuantity = DomainConstants.EMPTY_STRING;
		String strMax = DomainConstants.EMPTY_STRING;
		String sTarget = DomainConstants.EMPTY_STRING;
		String strMin = DomainConstants.EMPTY_STRING;
		String strPosInd = DomainConstants.EMPTY_STRING;
		String strFILBaseQty = DomainConstants.EMPTY_STRING;
		String strSubstitute = DomainConstants.EMPTY_STRING;
		String strBOMQuantity = DomainConstants.EMPTY_STRING;
		String strSAPName = DomainConstants.EMPTY_STRING;
		String id = DomainConstants.EMPTY_STRING;
		String strObjectType = DomainConstants.EMPTY_STRING;
		String strTempSAPType = DomainConstants.EMPTY_STRING;
		String strObjID = DomainConstants.EMPTY_STRING;
		String strChildID = DomainConstants.EMPTY_STRING;
		String strpgPhaseBOMQty = DomainConstants.EMPTY_STRING;
		String strOptComponent = DomainConstants.EMPTY_STRING;
		String strComment = DomainConstants.EMPTY_STRING;
		String strParentBOMBaseQty = DomainConstants.EMPTY_STRING;

		try {


			if (null != mapPartDetails && mapPartDetails.size() > 0) {

				strType = (String) mapPartDetails.get(DomainConstants.SELECT_TYPE);
				strName = (String) mapPartDetails.get(DomainConstants.SELECT_NAME);
				strRev = (String) mapPartDetails.get(DomainConstants.SELECT_REVISION);
				strParentEffectivityDate = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
				strParentBOMBaseQty = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);

				HashMap mpPartDataMap = new HashMap();
				DomainObject dChildObj = DomainObject.newInstance(context);
				Map mAllData = new HashMap();

				String arrOutput[][] = getBOMData(context, mapPartDetails);


				StringList slPartInfo = new StringList(8);
				slPartInfo.addElement(DomainConstants.SELECT_TYPE);
				slPartInfo.addElement(DomainConstants.SELECT_DESCRIPTION);
				slPartInfo.addElement(DomainConstants.SELECT_NAME);
				slPartInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
				slPartInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
				slPartInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
				slPartInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
				slPartInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);

				if (null != arrOutput && arrOutput.length > 0) {
					for (int i = 0; i < arrOutput.length; i++) {

						mpPartDataMap = new HashMap();
						id = arrOutput[i][23];
						strSubstitute = arrOutput[i][14];
						strBOMQuantity = arrOutput[i][10];
						mpPartDataMap.put(pgV3Constants.MAP_KEY_BOM_TARGET, arrOutput[i][18]);

						strMin = arrOutput[i][19];
						strMax = arrOutput[i][17];
						strPosInd = arrOutput[i][13];
						strSAPName = arrOutput[i][9];


						mpPartDataMap.put(pgV3Constants.MAP_KEY_ID, arrOutput[i][23]);
						mpPartDataMap.put(pgV3Constants.MAP_KEY_SUBSTITUTE, strSubstitute);
						mpPartDataMap.put(pgV3Constants.MAP_KEY_BOMQTY, strBOMQuantity);
						mpPartDataMap.put(pgV3Constants.MAP_KEY_MIN, strMin);
						mpPartDataMap.put(pgV3Constants.MAP_KEY_MAX, strMax);
						mpPartDataMap.put(pgV3Constants.MAP_KEY_POSINDEX, strPosInd);
						mpPartDataMap.put(pgV3Constants.MAP_KEY_SAPNAME, strSAPName);
						mpPartDataMap.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, arrOutput[i][24]);
						mpPartDataMap.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT, arrOutput[i][25]);

						if (BusinessUtil.isNotNullOrEmpty(id)) {

							dChildObj = DomainObject.newInstance(context, id);

							mAllData = dChildObj.getInfo(context, slPartInfo);

							if (null != mAllData && mAllData.size() > 0) {


								strTempSAPType = (String) mAllData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
								strObjectType = (String) mAllData.get(DomainConstants.SELECT_TYPE);
								strEffectivityDate = (String) mAllData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
								mpPartDataMap.put(pgV3Constants.MAP_KEY_EFFECTIVITYDATE, strEffectivityDate);
								mpPartDataMap.put(DomainConstants.SELECT_TYPE, strObjectType);
								mpPartDataMap.put(DomainConstants.SELECT_DESCRIPTION, (String) mAllData.get(DomainConstants.SELECT_DESCRIPTION));
								mpPartDataMap.put(pgV3Constants.MAP_KEY_CHILD, (String) mAllData.get(DomainConstants.SELECT_NAME));
								mpPartDataMap.put(pgV3Constants.MAP_KEY_SAPUOM, (String) mAllData.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE));
								mpPartDataMap.put(pgV3Constants.MAP_KEY_SAPTYPE, (String) mAllData.get(strTempSAPType));
								mpPartDataMap.put(pgV3Constants.MAP_KEY_SSTYPE, pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
								mpPartDataMap.put(pgV3Constants.MAP_KEY_SAPDESC, (String) mAllData.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));

								mpPartDataMap.put(pgV3Constants.MAP_KEY_dISPLAYID, arrOutput[i][23]);

								mpPartDataMap.put(pgV3Constants.MAP_KEY_CHILD_ID, id);

								/*	Masters components will not be included in BOM eDelivery
								Any component of SAP type as "DOC" will not be eDelivered.
								 */

								if (!pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strTempSAPType) && !strObjectType.contains(pgV3Constants.TYPE_MASTER)) {

									mlPartObjects.add(mpPartDataMap);

								}
							}
						}
					}
				}

				if (BusinessUtil.isNotNullOrEmpty(mlPartObjects)) {

					int iSizeFlatBOM = mlPartObjects.size();
					Map mpBOM = new HashMap();

					String[] arrBOM = new String[pgV3Constants.INDEX_BOM_ARRAY_SIZE];

					for (int i = 0; iSizeFlatBOM > i; i++) {

						mpBOM = (Map) mlPartObjects.get(i);


						strEffectivityDate = (String) mpBOM.get(pgV3Constants.MAP_KEY_EFFECTIVITYDATE);
						sChildName = (String) mpBOM.get(pgV3Constants.MAP_KEY_CHILD);
						sQuantity = (String) mpBOM.get(pgV3Constants.MAP_KEY_BOMQTY);
						sTarget = (String) mpBOM.get(pgV3Constants.MAP_KEY_BOM_TARGET);
						strMin = (String) mpBOM.get(pgV3Constants.MAP_KEY_MIN);
						strMax = (String) mpBOM.get(pgV3Constants.MAP_KEY_MAX);
						strPosInd = (String) mpBOM.get(pgV3Constants.MAP_KEY_POSINDEX);
						strObjID = (String) mpBOM.get(DomainObject.SELECT_ID);
						strChildID = (String) mpBOM.get(pgV3Constants.MAP_KEY_CHILD_ID);
						strOptComponent = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
						strComment = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);

						// To get the necessary information of the child objects.

						arrBOM = buildBOMArray(strName, strFindNumb, strEffectivityDate, sChildName, sQuantity, strMax, sTarget, strMin, strParentEffectivityDate, strParentBOMBaseQty, strPosInd, strFILBaseQty, strpgPhaseBOMQty, (String) mpBOM.get(pgV3Constants.MAP_KEY_SUBSTITUTE), strObjID, strOptComponent, strComment, "", "", "", "");
						alPartRelatedObjects.add(arrBOM);
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw new Exception(ex.getMessage());
		}
		return alPartRelatedObjects;
	}

	/**
	 * getSubstituteFlag-Method is used to get the Substitute value for children
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String
	 * @return String
	 * @throws Exception
	 */

	private String getSubstituteFlag(Context context, String strRelID) throws Exception {

		StringList substituteInterIds = new StringList();
		char cAltItem1 = '9';
		char cAltItem2 = 'A';
		int iSubCount = -1;
		int iCounter = 0;
		String strSubFlag = DomainConstants.EMPTY_STRING;
		String strRelName = DomainConstants.EMPTY_STRING;

		Object substituteInterId = null;

		Map mpTemp = new HashMap();

		try {

			if (BusinessUtil.isNotNullOrEmpty(strRelID)) {

				String[] saRelId = new String[1];
				saRelId[0] = strRelID;

				StringList slSelectable = new StringList(2);
				slSelectable.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
				slSelectable.add("relationship");

				MapList mpListsubstituteInterIds = DomainRelationship.getInfo(context, saRelId, slSelectable);


				if (null != mpListsubstituteInterIds) {

					for (int j = 0; j < mpListsubstituteInterIds.size(); j++) {

						mpTemp = (Map) mpListsubstituteInterIds.get(j);

						substituteInterId = (Object) mpTemp.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
						strRelName = (String) mpTemp.get("relationship");

						if (null != substituteInterId) {

							substituteInterId = getInstanceList(substituteInterId);

							iSubCount = substituteInterIds.size();

							if (iCounter > 0) {

								if (cAltItem2 == 'Z') {

									cAltItem2 = 'A';
									cAltItem1--;
								} else {

									cAltItem2++;
								}
							}
							strSubFlag = cAltItem1 + "" + cAltItem2;
							iCounter++;
						} else {

							if (pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE.equals(strRelName)) {

								iSubCount--;
								strSubFlag = cAltItem1 + "" + cAltItem2;
							} else {

								strSubFlag = DomainConstants.EMPTY_STRING;
								continue;
							}
						}
					}
				}

			}
		} catch (Exception e) {

			e.printStackTrace();
			strSubFlag = DomainConstants.EMPTY_STRING;
		}
		return strSubFlag;
	}


	/**
	 * getSubstituteGroupValue- Method is used to get the Substitute Group value for the given object
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String
	 * @param MapList
	 * @return StringList
	 * @throws Exception
	 */

	private StringList getSubstituteGroupValue(Context context, String strObjectID, MapList mpListChildren) throws Exception {

		String strGroup = DomainConstants.EMPTY_STRING;
		float fQTY = 0;

		StringList slGroupValues = new StringList(1);

		try {

			Map mParamList = new HashMap();
			mParamList.put("objectId", strObjectID);

			Map hmArgMapParams = new HashMap();
			hmArgMapParams.put("objectList", mpListChildren);
			hmArgMapParams.put("paramList", mParamList);

			String[] strArrArgsSubInput = JPO.packArgs(hmArgMapParams);

			// Instead of re-writing the logic, calling the existing method which is being used by the table pgIPMFlatBOMViewTable
			Vector vBOMGroupData = (Vector) JPO.invoke(context, "pgIPMProductData", null, "getSubstituteFlag", strArrArgsSubInput, Vector.class);

			if (null != vBOMGroupData && vBOMGroupData.size() > 0) {

				for (int k = 0; k < vBOMGroupData.size(); k++) {

					strGroup = (String) vBOMGroupData.get(k);
					if (BusinessUtil.isNullOrEmpty(strGroup)) {
						strGroup = DomainConstants.EMPTY_STRING;
					}
					slGroupValues.add(strGroup);
				}

			}

		} catch (Exception e) {

			e.printStackTrace();
			slGroupValues = new StringList();
		}
		return slGroupValues;
	}

	/**
	 * getQuantityValue-Method is used to get the Quantity value for the given object
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String
	 * @param Maplist
	 * @return StringList
	 * @throws Exception
	 */

	private StringList getQuantityValue(Context context, String strObjectID, MapList mpListChildren) throws Exception {

		String strQTY = DomainConstants.EMPTY_STRING;
		float fQTY = 0;

		StringList strListQtyValues = new StringList(1);

		try {

			Map mParamList = new HashMap();
			mParamList.put(pgV3Constants.OBJECT_ID, strObjectID);
			Map hmArgMapParams = new HashMap();
			hmArgMapParams.put("objectList", mpListChildren);
			hmArgMapParams.put("paramList", mParamList);
			String[] argsArrQtyInput = JPO.packArgs(hmArgMapParams);

			// Instead of re-writing the logic, calling the existing method which is being used by the table pgIPMFlatBOMViewTable
			Vector BOMQuantityData = (Vector) JPO.invoke(context, "pgIPMProductData", null, "getBOMQTYData", argsArrQtyInput, Vector.class);

			if (null != BOMQuantityData && BOMQuantityData.size() > 0) {

				for (int k = 0; k < BOMQuantityData.size(); k++) {

					strQTY = (String) BOMQuantityData.get(k);
					if (BusinessUtil.isNullOrEmpty(strQTY)) {
						strQTY = DomainConstants.EMPTY_STRING;
					}
					strListQtyValues.add(strQTY);
				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		return strListQtyValues;
	}


	/**
	 * getPartObjectsFromIntermediateBOM- Method is used to get EBOM, Substitute and Alternate for Finished Product Part and Packing Assembly Part .
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param String
	 * @return ArrayList
	 * @throws Exception
	 */

	public ArrayList getPartObjectsFromIntermediateBOM(Context context, String IPSId) throws Exception {


		ArrayList alBOM = new ArrayList();

		StringList vc = new StringList();
		StringList strListQty = new StringList(1);
		StringList slSubstituteGroupValues = new StringList(1);
		StringList slSubIntermediate = new StringList();
		StringList slSubstituteInterIdReshipper = new StringList();
		StringList slChildCOPConnectedFPPQTY = new StringList(1);
		StringList slReleaseFPPQTY = new StringList(1);
		StringList slCOPSubstituteIds = new StringList();
		//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
		StringList slCOPSubstituteQuantities = new StringList();
		//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends
		Map mpAltMap = new HashMap();
		Map mpChildMap = new HashMap();
		Map mpBOM = new HashMap();
		Map tmpMap = new HashMap();
		Map mapTemp = new HashMap();
		Map tempMap = new HashMap();
		Map mpNodeDeatails = new HashMap();
		Map mpConnectedFPPToChildCOP = new HashMap();

		MapList mlIntermediateChildData = new MapList();
		MapList mlSubInterFirstLevelData = new MapList();
		MapList mlConnectedInterChildData = new MapList();
		MapList mlSubAlts = new MapList();
		MapList mlFlatBOM = new MapList();
		MapList mpTUPDATA = new MapList();
		MapList mlChildCOPConnectedFPP = new MapList();
		MapList mlReleasedFPP = new MapList();
		MapList masterList = new MapList();
		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
		MapList mlPAPCOPBulk = new MapList();
		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends
		DomainObject dObjForTopNodePart = null;
		DomainObject dObjIntermediate = null;
		DomainObject dobjAlternate = null;
		DomainObject dObjInter = null;
		DomainObject dmaster = null;
		//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Starts
		DomainObject doBase = null;
		DomainObject doAlternate = null;
		String sBOMComponentCurrent = DomainConstants.EMPTY_STRING;
		String sAuthoringApplication = DomainConstants.EMPTY_STRING;
		String strRelationship = DomainConstants.EMPTY_STRING;
		boolean sCATIAAPPDeliver = true;
		Map mpAlternateDetails = new HashMap();
		StringList slAlternateSelects = new StringList(2);
		slAlternateSelects.add(DomainConstants.SELECT_CURRENT);
		slAlternateSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Ends
		String strSubstituteInterType = DomainConstants.EMPTY_STRING;
		String strSubstituteType = DomainConstants.EMPTY_STRING;
		String strTopNodeType = DomainConstants.EMPTY_STRING;
		String strTopNodeName = DomainConstants.EMPTY_STRING;
		String strTopNodeEffeDate = DomainConstants.EMPTY_STRING;
		String strTopNodeBOMQty = DomainConstants.EMPTY_STRING;
		String strReshipperLevel = DomainConstants.EMPTY_STRING;
		String strID = DomainConstants.EMPTY_STRING;
		String strObjectType = DomainConstants.EMPTY_STRING;
		String strSAPType = DomainConstants.EMPTY_STRING;
		String strName = DomainConstants.EMPTY_STRING;
		String strRev = DomainConstants.EMPTY_STRING;
		String strLevel = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		String strInterID = DomainConstants.EMPTY_STRING;
		String strFindNumb = DomainConstants.EMPTY_STRING;
		String strEffectivityDateChild = DomainConstants.EMPTY_STRING;
		String sChildName = DomainConstants.EMPTY_STRING;
		String sQuantity = DomainConstants.EMPTY_STRING;
		String strMax = DomainConstants.EMPTY_STRING;
		String sTarget = DomainConstants.EMPTY_STRING;
		String strMin = DomainConstants.EMPTY_STRING;
		String strPosInd = DomainConstants.EMPTY_STRING;
		String strFILBaseQty = DomainConstants.EMPTY_STRING;
		String strpgPhaseBOMQty = DomainConstants.EMPTY_STRING;
		String strQtyTmp = DomainConstants.EMPTY_STRING;
		String strGroupTmp = DomainConstants.EMPTY_STRING;
		String strOptComponent = DomainConstants.EMPTY_STRING;
		String strComment = DomainConstants.EMPTY_STRING;
		String strIntermediateLevel = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921,#25922,#27206 - Starts
		String strExpandBOMonSAPBOMasFed = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921,#25922,#27206 - Ends

		//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921,#25922,#27206 - Starts
		String strNONGCASTYPE = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(), "emxCPN.SAPBOM.DSM.NONGCASTypes");
		//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921,#25922,#27206 - Ends

		String strBUOM = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Requirement #30797 #33457 - Starts
		String strTopNodeSpecSubType = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Requirement #30797 #33457 - Ends
		String strValidUntilDate = DomainConstants.EMPTY_STRING;
		String strSpecSubType = DomainConstants.EMPTY_STRING;
		String strInterSpecSubType = DomainConstants.EMPTY_STRING;
		String strChildQuantity = DomainConstants.EMPTY_STRING;
		String strInterMedLevel = DomainConstants.EMPTY_STRING;
		String strIntermediateObjectQty = DomainConstants.EMPTY_STRING;
		String strCOPToCOPQuantity = DomainConstants.EMPTY_STRING;
		String strSubstituteId = DomainConstants.EMPTY_STRING;
		String strMasterID = DomainConstants.EMPTY_STRING;
		String strExpandID = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sustitute Alternates Requirements #30849,#30850 - Starts
		String strAlternateCUPSpecSubType = DomainConstants.EMPTY_STRING;
		String strAlternateCUPSAPType = DomainConstants.EMPTY_STRING;
		String strAlternateCOPSpecSubType = DomainConstants.EMPTY_STRING;
		String strAlternateCOPSAPType = DomainConstants.EMPTY_STRING;
		String strsubsCOPQuantity = DomainConstants.EMPTY_STRING;
		String strsubsCUPQuantity = DomainConstants.EMPTY_STRING;
		String strChildCOPAltQuantity = DomainConstants.EMPTY_STRING;
		String strChildCUPAltQuantity = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sustitute Alternates Requirements #30849,#30850 - Ends
		//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Starts
		String strPriAlternateCUPSpecSubType = DomainConstants.EMPTY_STRING;
		String strPriAlternateCOPSpecSubType = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Ends

		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
		String strPAPChildSpecSubType = DomainConstants.EMPTY_STRING;
		String strPriAltSpecSubType = DomainConstants.EMPTY_STRING;
		String strSubAltSpecSubType = DomainConstants.EMPTY_STRING;
		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts

		//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Starts
		String strExpandFABSubsAltSpecSubType = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Ends

		Object substituteInterId = null;
		Object substituteInterType = null;
		Object substituteSpecificationType = null;

		//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Starts
		Object oReshipperPrimaryAlternateIds = null;
		//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Ends
		//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
		Object oCOPSubtituteQuantities = null;
		//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends
		int iAltsSize = 0;

		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
		String strComponentQuantity = DomainConstants.EMPTY_STRING;
		String strMasterComponentQuantity = DomainConstants.EMPTY_STRING;
		String strPrimaryQuantity = DomainConstants.EMPTY_STRING;
		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End

		String[] arrBOM;

		char cAltItem1 = pgV3Constants.ALT_ITEM_NINE, cAltItem2 = pgV3Constants.ALT_ITEM_A;
		String strIntermediateTypes = pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_PGINNERPACKUNITPART + "," + pgV3Constants.TYPE_PGCONSUMERUNITPART;
		String strDONOTINCLUDETYPE = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(), "emxCPN.SAPBOM.DSM.DONOTINCLUDETYPE");
		String objectWhere = DomainObject.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;

		Pattern pIntermediateObjectType = new Pattern(pgV3Constants.TYPE_PGCONSUMERUNITPART);
		pIntermediateObjectType.addPattern(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
		pIntermediateObjectType.addPattern(pgV3Constants.TYPE_PGINNERPACKUNITPART);

		boolean isReshipperSub = false;
		float fChildSAPQty = 1;
		float fIntermediateSAPQty = 1;
		BigDecimal bdIntermediateObjectQty = new BigDecimal("1");

		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
		StringList substituteReshipper = new StringList();
		int processIdLevel = 0;
		String keyIsComplexBOM = SAPViewConstant.KEY_IS_SELF_COMPLEX_BOM.getValue();
		String keyIsParentComplexBOM = SAPViewConstant.KEY_IS_PARENT_COMPLEX_BOM.getValue();
		String keyParenID = SAPViewConstant.KEY_PARENT_ID.getValue();
		String keyParentType = SAPViewConstant.KEY_PARENT_TYPE.getValue();
		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

		// Added by (DSM) Sogeti for 22x.02 REQ 46286 - Start
		StringList slCOPSubstituteTypes = new StringList();
		// Added by (DSM) Sogeti for 22x.02 REQ 46286 - End
		
		// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts	
		float fIntermediateSHALBSAPQty = 1;
		// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
		try {
			if (BusinessUtil.isNotNullOrEmpty(IPSId)) {

				dObjForTopNodePart = DomainObject.newInstance(context, IPSId);
				// Check the top level part type
				mpNodeDeatails = dObjForTopNodePart.getInfo(context, SL_OBJECT_INFO_SELECT);
				strTopNodeType = (String) mpNodeDeatails.get(DomainConstants.SELECT_TYPE);
				strTopNodeName = (String) mpNodeDeatails.get(DomainConstants.SELECT_NAME);
				strTopNodeEffeDate = (String) mpNodeDeatails.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
				strTopNodeBOMQty = (String) mpNodeDeatails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
				strBUOM = (String) mpNodeDeatails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
				//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
				strTopNodeSpecSubType = (String) mpNodeDeatails.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
				//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends

				//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
				if ((pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strTopNodeType))) {
					//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends
					MapList mlFPPChildData = dObjForTopNodePart.getRelatedObjects(context,
							pgV3Constants.RELATIONSHIP_EBOM,    // relationship pattern
							pIntermediateObjectType.getPattern(), // Type pattern
							SL_OBJECT_INFO_SELECT,                // object selects
							SL_RELATION_INFO_SELECT_EBOM,            // rel selects
							false,                                // to side
							true,                                // from side
							(short) 0,                            // recursion level
							objectWhere,                        // object where clause
							null, 0);                                // rel where clause
					//Get first level child for CUP, COP and IP
					if (null != mlFPPChildData && mlFPPChildData.size() > 0) {

						// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
						ISAPBOM iSAPBOM = new SAPBOM();
						mlFPPChildData = iSAPBOM.getBOMInfoList(context, IPSId, mlFPPChildData);
						boolean isSelfComplexBOM = false;
						// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
						int iSizeOfChildData = mlFPPChildData.size();
						Map mpPreviousLevelChildType = new HashMap();

						for (int i = 0; i < iSizeOfChildData; i++) {

							tempMap = (Map) mlFPPChildData.get(i);
							strLevel = (String) (tempMap).get(DomainConstants.SELECT_LEVEL);

							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
							isSelfComplexBOM = false;
							if (tempMap.containsKey(keyIsComplexBOM)) {
								isSelfComplexBOM = (Boolean) (tempMap).get(keyIsComplexBOM);
							}
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
							// to avoid the child intermediate parts of reshipper CUP
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
							if (processIdLevel != 0 && processIdLevel != parseValue(strLevel)) {
								continue;
							} else if (processIdLevel == parseValue(strLevel)) {
								processIdLevel = 0;
							}
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
							if ((BusinessUtil.isNotNullOrEmpty(strLevel))
									&& (BusinessUtil.isNotNullOrEmpty(strReshipperLevel))
									&& (parseValue(strLevel) > parseValue(strReshipperLevel))) {
								continue;
							} else if ((BusinessUtil.isNotNullOrEmpty(strLevel))
									&& (BusinessUtil.isNotNullOrEmpty(strReshipperLevel))
									&& (parseValue(strLevel) <= parseValue(strReshipperLevel))) {
								strReshipperLevel = DomainConstants.EMPTY_STRING;
							}
							strID = (String) (tempMap).get(DomainConstants.SELECT_ID);
							strObjectType = (String) (tempMap).get(DomainConstants.SELECT_TYPE);
							strName = (String) (tempMap).get(DomainConstants.SELECT_NAME);
							strRev = (String) (tempMap).get(DomainConstants.SELECT_REVISION);
							//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
							strSpecSubType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
							//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends

							mpPreviousLevelChildType.put(parseValue(strLevel), strObjectType);
							mpPreviousLevelChildType.put(strLevel, strID);
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
							// Modified by (DSM-Sogeti) for June CW Defect #48289 Start
							substituteReshipper = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
							// Modified by (DSM-Sogeti) for June CW Defect #48289 Ends
							//if Type is Customer Part and don't have RESHIPPER or type is Consumer part and Customer Part and Level should be greater that 1

							if (((pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strObjectType)
									&& !substituteReshipper.contains("Reshipper"))
									|| pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strObjectType)
									|| pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType))
									&& parseValue(strLevel) > 1) {
								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
								String strPreviousTypeToSecondLevelCOP = (String) mpPreviousLevelChildType.get(parseValue(strLevel) - 1);
								String strPreviousTypeToThirdLevelCOP = (String) mpPreviousLevelChildType.get(parseValue(strLevel) - 2);

								// Modified by (DSM-Sogeti) for (June 2022) Defect 48497 Start
								String sPreviousLevel = String.valueOf(Integer.parseInt(strLevel) - 1);
								String strPreviousObjectId = (String) mpPreviousLevelChildType.get(sPreviousLevel);
								// Modified by (DSM-Sogeti) for (June 2022) Defect 48497 END
								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
								// Modified by (DSM-Sogeti) for (June 2022) Defect 48497 Start
								boolean isThirdLevelCOP = pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPreviousTypeToSecondLevelCOP);
								boolean isCOPSpecialCase = isThirdLevelCOP
										&& !pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPreviousTypeToThirdLevelCOP);

								if ((isSelfComplexBOM && !isThirdLevelCOP) || isCOPSpecialCase) {
									// Modified by (DSM-Sogeti) for (June 2022) Defect 48497 END
									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
									for (int j = iSizeOfChildData - 1; j > 0; j--) {
										mpChildMap = (Map) mlFPPChildData.get(j - 1);
										strIntermediateLevel = (String) (mpChildMap).get(DomainConstants.SELECT_LEVEL);
										if (parseValue(strIntermediateLevel) < parseValue(strLevel)) {
											strIntermediateObjectQty = (String) (mpChildMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
											bdIntermediateObjectQty = bdIntermediateObjectQty.multiply(new BigDecimal(strIntermediateObjectQty));
										}
									}
									strCOPToCOPQuantity = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
									//Modified by DSM Sogeti for 2018x.5 Defect #36454 - Starts
									slCOPSubstituteIds = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
									//Modified by DSM Sogeti for 2018x.5 Defect #36454 - Ends
									//Modified by DSM Sogeti for 2018x.5 Requirement #30797 #33457 - Starts
									oCOPSubtituteQuantities = (Object) (tempMap).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
									//Modified by DSM Sogeti for 2018x.5 Defect #36454 - Starts
									slCOPSubstituteQuantities = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
									//Modified by DSM Sogeti for 2018x.5 Defect #36454 - Ends
									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start

									// Added by (DSM) Sogeti for 22x.02 REQ 46286 - Start
									slCOPSubstituteTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
									// Added by (DSM) Sogeti for 22x.02 REQ 46286 - End

									// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - Start
									// get where-used FPPs
									mpConnectedFPPToChildCOP = getFPPObjectsForChildCOPInCOP(context,
											strID, strCOPToCOPQuantity, IPSId, slCOPSubstituteIds,
											strTopNodeBOMQty,
											bdIntermediateObjectQty,
											strBUOM,
											strTopNodeSpecSubType,
											strSpecSubType,
											slCOPSubstituteQuantities, slCOPSubstituteTypes);
									// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - End
									//Modified by DSM Sogeti for 2018x.5 Requirement #30797 #33457 - Ends
									bdIntermediateObjectQty = new BigDecimal("1");
									mlChildCOPConnectedFPP = (MapList) mpConnectedFPPToChildCOP.get("mlReleasedFPP");
									slChildCOPConnectedFPPQTY = (StringList) mpConnectedFPPToChildCOP.get("strListQtyValues");
									if (!mlChildCOPConnectedFPP.isEmpty() && !slChildCOPConnectedFPPQTY.isEmpty()) {
										mlReleasedFPP.addAll(mlChildCOPConnectedFPP);
										slReleaseFPPQTY.addAll(slChildCOPConnectedFPPQTY);
									}
									// To Skip COP child GCAS Components
									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
									// if where-used is found then continue.
									if (!mlChildCOPConnectedFPP.isEmpty() && isSelfComplexBOM) {
										processIdLevel = Integer.parseInt(strLevel);
										continue;
										// Modified by (DSM-Sogeti) for (June 2022) Defect 48497 Start
									} else {
										// if where-used is not found - then continue.
										if (isSelfComplexBOM) {
											processIdLevel = Integer.parseInt(strLevel);
											continue;
										}
									}
									if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)) {
										// Modified by (DSM-Sogeti) for (June 2022) Defect 48497 End
										continue;
									}

									//Below check is to skip 3rd level COP child objects
								} else if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPreviousTypeToSecondLevelCOP)) {
									continue;
								}
							}


							strSpecSubType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
							if ((pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strObjectType)
									|| pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType))
									&& pgV3Constants.THIRD_PARTY.equals(strSpecSubType)) {
								break;
							}

							// get intermiediates substitues
							if (strIntermediateTypes.contains(strObjectType)) {
								String strIntermediateObjQty = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
								if (UIUtil.isNotNullAndNotEmpty(strBUOM)
										&& (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM)
												|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM)
												|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM)
												|| pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM)
												|| pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM))
										&& UIUtil.isNotNullAndNotEmpty(strIntermediateObjQty)) {
									//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Starts
									BigDecimal bdIntermediateSAPQty = BigDecimal.valueOf(fIntermediateSAPQty);
									//fIntermediateSAPQty = Float.valueOf(strIntermediateObjQty) * fIntermediateSAPQty;
									bdIntermediateSAPQty = new BigDecimal(strIntermediateObjQty).multiply(bdIntermediateSAPQty);
									fIntermediateSAPQty = bdIntermediateSAPQty.floatValue();
									//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Ends
								} 
								// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
								if ((strTopNodeSpecSubType.equalsIgnoreCase(pgV3Constants.SHIPPABLE_HALB)) && 
										UIUtil.isNotNullAndNotEmpty(strBUOM) && 
										UIUtil.isNotNullAndNotEmpty(strIntermediateObjQty)) {
									BigDecimal bdIntermediateSAPQty = BigDecimal.valueOf(fIntermediateSAPQty);
									bdIntermediateSAPQty = new BigDecimal(strIntermediateObjQty).multiply(bdIntermediateSAPQty);
									fIntermediateSAPQty = bdIntermediateSAPQty.floatValue();
									fIntermediateSHALBSAPQty = fIntermediateSAPQty;
								}
								// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
							}
							// get substitute parts for Intermediate parts
							//Check if intermediate object has substitute itself - Start

							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
							StringList substituteInterIds = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
							slSubstituteInterIdReshipper = substituteInterIds;
							StringList substituteInterTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE); // Intermediate parts substitute parts types
							StringList slSubstituteSpecificationTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE); // Intermediate part substitute specification Type
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
							//If types is intermediate type, add to sub stringlist
							if (null != substituteInterIds && substituteInterIds.size() > 0) {

								for (int iSubInter = 0; iSubInter < substituteInterIds.size(); iSubInter++) {
									strSubstituteInterType = (String) substituteInterTypes.get(iSubInter);
									strSpecSubType = (String) slSubstituteSpecificationTypes.get(iSubInter);
									if (((pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strSubstituteInterType))
											|| (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteInterType))
											&& !pgV3Constants.THIRD_PARTY.equals(strSpecSubType))
											|| (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strSubstituteInterType))) {
										slSubIntermediate.addElement((String) substituteInterIds.get(iSubInter));
									}
								}
							}

							//Check if intermediate object has substitute itself - End
							// Now for each Intermediate object find its child
							// avoid adding child parts for CUP which has Reshipper as a specification sub type
							if ((strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART))
									&& (slSubstituteSpecificationTypes.indexOf(pgV3Constants.RESHIPPER_ASS_TYPE_VAL) != -1)
									&& (strTopNodeType.equalsIgnoreCase(pgV3Constants.TYPE_FINISHEDPRODUCTPART))) {
								strReshipperLevel = strLevel;
								isReshipperSub = true;
								continue;
							}

							dObjIntermediate = DomainObject.newInstance(context, strID);
							mlIntermediateChildData = dObjIntermediate.getRelatedObjects(context,
									pgV3Constants.RELATIONSHIP_EBOM, // rel pattern
									//pgV3Constants.SYMBOL_STAR,       // type pattern
									DomainConstants.QUERY_WILDCARD,  // type pattern
									SL_OBJECT_INFO_SELECT,           // object select
									SL_RELATION_INFO_SELECT_EBOM,    //  rel select
									false,                           // to side
									true,                            //  from side
									(short) 1,                        // recursion level
									objectWhere,                     //  object where clause
									null, 0);                         //  rel where clause

							if (null != mlIntermediateChildData && mlIntermediateChildData.size() > 0) {

								mlIntermediateChildData.sort(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, "ascending", "integer");

								int iSizeOfIntermediateChild = mlIntermediateChildData.size();

								for (int iCount = 0; iCount < iSizeOfIntermediateChild; iCount++) {

									tmpMap = (Map) mlIntermediateChildData.get(iCount);
									strType = (String) (tmpMap).get(DomainConstants.SELECT_TYPE);

									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
									float fQtyMultiplier = 0;
									strPrimaryQuantity = DomainConstants.EMPTY_STRING;
									strComponentQuantity = (String) tmpMap.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

									//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
									boolean bExpandFAB = false;
									if (UIUtil.isNotNullAndNotEmpty(strType) && strType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART)) {
										strExpandBOMonSAPBOMasFed = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
										if ((UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && strExpandBOMonSAPBOMasFed.equalsIgnoreCase("TRUE"))) {
											bExpandFAB = true;
										}
									}
									//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
									if (strObjectType.equals(pgV3Constants.TYPE_PGCONSUMERUNITPART) && strType.equals(pgV3Constants.TYPE_PGCONSUMERUNITPART)) {
										continue;
									}
									strSpecSubType = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
									if (pgV3Constants.THIRD_PARTY.equals(strSpecSubType)) {
										continue;
									}
									if (!(strDONOTINCLUDETYPE.contains(strType))) {
										(tmpMap).put("IntermediateName", strName + "." + strRev);
										(tmpMap).put("IntermediateID", strID);
										(tmpMap).put("IntermediateType", strObjectType);
										(tmpMap).put("IntermediateLevel", strLevel);
										(tmpMap).put("IsDirectComponent", "true");

										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										strPrimaryQuantity = getReshipperQuantityValue(context, dObjIntermediate, isReshipperSub, strComponentQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start

										//Modified by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
										//if(strType.contains(pgV3Constants.TYPE_MASTER)) {
										if (strType.contains(pgV3Constants.TYPE_MASTER)
												|| (UIUtil.isNotNullAndNotEmpty(strType)
														&& strType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART)
														&& bExpandFAB)) {
											//Modified by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends

											//return (new MapList());
											strMasterID = (String) (tmpMap).get(DomainConstants.SELECT_ID);
											dmaster = DomainObject.newInstance(context, strMasterID);
											masterList = dmaster.getRelatedObjects(context,
													pgV3Constants.RELATIONSHIP_EBOM, // rel pattern
													//pgV3Constants.SYMBOL_STAR,       // type pattern
													DomainConstants.QUERY_WILDCARD,  // type pattern
													SL_OBJECT_INFO_SELECT,           // object select
													SL_RELATION_INFO_SELECT_EBOM,    //  rel select
													false,                           // to side
													true,                            //  from side
													(short) 1,                        // recursion level
													DomainConstants.EMPTY_STRING,    //  object where clause
													null, 0);

											int masterComp = masterList.size();
											for (int k = 0; k < masterComp; k++) {

												tempMap = (Map) masterList.get(k);
												strExpandID = (String) (tempMap).get(DomainConstants.SELECT_ID);
												strObjectType = (String) (tempMap).get(DomainConstants.SELECT_TYPE);
												strName = (String) (tempMap).get(DomainConstants.SELECT_NAME);
												strRev = (String) (tempMap).get(DomainConstants.SELECT_REVISION);
												//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
												String strSAPChildType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
												String strSpecSubChildType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
												strMasterComponentQuantity = (String) tmpMap.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);

												// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
												strPrimaryQuantity = DomainConstants.EMPTY_STRING;
												strPrimaryQuantity = getReshipperQuantityValue(context, dmaster, isReshipperSub, strMasterComponentQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
												// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

												if (UIUtil.isNotNullAndNotEmpty(strType) && strType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART)) {
													if (!(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSAPChildType)) && !(strNONGCASTYPE.contains(strObjectType)) && !(pgV3Constants.THIRD_PARTY).equals(strSpecSubChildType)) {
														//Modified by DSM Sogeti for 2018x.2 Defect #28591 - Starts
														(tempMap).put("IntermediateName", strName + "." + strRev);
														(tempMap).put("IntermediateType", strObjectType);
														(tempMap).put("IntermediateLevel", strLevel);
														//Modified by DSM Sogeti for 2018x.2 Defect #28591 - Ends
														(tempMap).put("IntermediateID", strID);
														(tempMap).put("IsDirectComponent", "true");
														// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Start
														// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
														if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
																UIUtil.isNotNullAndNotEmpty(strMasterComponentQuantity)) {
															(tempMap).put("Type", strType);
															(tempMap).put("FAB_ExpandBOMonSAPBOMasFed",strExpandBOMonSAPBOMasFed);
															(tempMap).put("FAB_ExpandBOMQuantity",strMasterComponentQuantity);
														}
														// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
														// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
														mlFlatBOM.add(tempMap);


														// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
														strListQty.add(strPrimaryQuantity);
														// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
													}
												} else {
													mlFlatBOM.add(tempMap);
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
													strListQty.add(strPrimaryQuantity);
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

												}
												if (UIUtil.isNotNullAndNotEmpty(strType) && strType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART)) {
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
													StringList substituteIds = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
													StringList substituteSAPTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
													StringList substituteNames = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
													StringList substituteTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
													StringList slsubstituteRev = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
													StringList slsubstituteCurrent = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
													StringList slsubstituteRelID = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
													//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Starts
													//Modified START_EFFECTIVITY to EFFECTIVITY_DATE
													//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Ends
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
													StringList slSubstituteEffectivityDate = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
													StringList slSubstituteValidUntilDate = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
													StringList slSubstituteOptComponent = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
													StringList slSubstituteComment = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
													StringList slSubSpecSubType = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
													//Include the Alternates of Component
													if (!(strDONOTINCLUDETYPE.contains(strType))) {
														// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
														StringList slAlternateIds = getStringListFromMap(tempMap, pgV3Constants.SELECT_ALTERNATE_ID);
														// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
														for (Object oAlternateId : slAlternateIds) {
															dobjAlternate = DomainObject.newInstance(context, (String) oAlternateId);
															Map mpAlternate = dobjAlternate.getInfo(context, SL_OBJECT_INFO_SELECT);
															//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Starts
															if (!pgV3Constants.THIRD_PARTY.equalsIgnoreCase((String) mpAlternate.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE))) {
																//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Ends
																(mpAlternate).put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) tempMap.get(DomainConstants.SELECT_RELATIONSHIP_ID));
																(mpAlternate).put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
																(mpAlternate).put("IntermediateName", strName + "." + strRev);
																(mpAlternate).put("IntermediateID", strID);
																(mpAlternate).put("IntermediateType", strObjectType);
																(mpAlternate).put("IntermediateLevel", strLevel);
																// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 Starts
																// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
																if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
																		UIUtil.isNotNullAndNotEmpty(strMasterComponentQuantity)) {
																	(mpAlternate).put("Type", strType);
																	(mpAlternate).put("FAB_ExpandBOMonSAPBOMasFed",strExpandBOMonSAPBOMasFed);
																	(mpAlternate).put("FAB_ExpandBOMQuantity",strMasterComponentQuantity);
																}
																// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
																// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 Ends
																mlFlatBOM.add(mpAlternate);

																// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
																strListQty.add(strPrimaryQuantity);
																// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
															}
														}
													}
													if (null != substituteIds && substituteIds.size() > 0) {
														int iSizeOfSubstitutes = substituteIds.size();
														for (int iSub = 0; iSub < iSizeOfSubstitutes; iSub++) {
															//Add Fabricated Part as SAP BOM Component
															String strSubstitutepgSAPType = (String) substituteSAPTypes.get(iSub);
															if (!(strDONOTINCLUDETYPE.contains((String) substituteTypes.get(iSub))) && !(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
																mapTemp = new HashMap();
																strSubstituteId = (String) substituteIds.get(iSub);
																strSubstituteType = (String) substituteTypes.get(iSub);
																mapTemp.put("name", (String) substituteNames.get(iSub));
																mapTemp.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
																mapTemp.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
																//mapTemp.put(DomainConstants.SELECT_ID, (String)substituteIds.get(iSub));
																mapTemp.put(DomainConstants.SELECT_ID, strSubstituteId);
																//mapTemp.put(DomainConstants.SELECT_TYPE, (String)substituteTypes.get(iSub));
																mapTemp.put(DomainConstants.SELECT_TYPE, strSubstituteType);
																mapTemp.put("IntermediateName", strName + "." + strRev);
																mapTemp.put("IntermediateID", strID);
																mapTemp.put("IntermediateType", strObjectType);
																mapTemp.put(DomainConstants.SELECT_REVISION, (String) slsubstituteRev.get(iSub));
																mapTemp.put(DomainConstants.SELECT_CURRENT, (String) slsubstituteCurrent.get(iSub));
																mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, (String) slSubstituteEffectivityDate.get(iSub));
																mapTemp.put(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE, (String) slSubstituteValidUntilDate.get(iSub));
																mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, (String) slSubstituteOptComponent.get(iSub));
																mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT, (String) slSubstituteComment.get(iSub));
																mapTemp.put("IntermediateLevel", strLevel);
																// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 Starts
																// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
																if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
																		UIUtil.isNotNullAndNotEmpty(strMasterComponentQuantity)) {
																	(mapTemp).put("Type", strType);
																	(mapTemp).put("FAB_ExpandBOMonSAPBOMasFed",strExpandBOMonSAPBOMasFed);
																	(mapTemp).put("FAB_ExpandBOMQuantity",strMasterComponentQuantity);
																}
																// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
																// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 Ends
																mlFlatBOM.add(mapTemp);
																// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
																strPrimaryQuantity = DomainConstants.EMPTY_STRING;
																String strChildSubQuantity = (String) mapTemp.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
																strPrimaryQuantity = getReshipperQuantityValue(context, dmaster, isReshipperSub, strChildSubQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
																strListQty.add(strPrimaryQuantity);
																// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
																if (UIUtil.isNotNullAndNotEmpty(strSubstituteType) && ALTERNATE_ALLOWED_TYPES.contains(strSubstituteType)) {
																	mlSubAlts = getAlternates(context, strSubstituteId);
																	iAltsSize = mlSubAlts.size();
																	for (int j = 0; j < iAltsSize; j++) {
																		mpAltMap = (Map) mlSubAlts.get(j);
																		//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Starts
																		strExpandFABSubsAltSpecSubType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
																		if (!strExpandFABSubsAltSpecSubType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY)) {
																			//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Ends
																			mpAltMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
																			mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
																			mpAltMap.put("IntermediateName", strName + "." + strRev);
																			mpAltMap.put("IntermediateID", strID);
																			mpAltMap.put("IntermediateType", strObjectType);
																			mpAltMap.put("IntermediateLevel", strLevel);
																			// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
																			// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
																			if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
																					UIUtil.isNotNullAndNotEmpty(strMasterComponentQuantity)) {
																				(mpAltMap).put("Type", strType);
																				(mpAltMap).put("FAB_ExpandBOMonSAPBOMasFed",strExpandBOMonSAPBOMasFed);
																				(mpAltMap).put("FAB_ExpandBOMQuantity",strMasterComponentQuantity);
																			}
																			// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
																			// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
																			mlFlatBOM.add(mpAltMap);
																			// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
																			strListQty.add(strPrimaryQuantity);
																			// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
																		}
																	}
																}
															}
															//Modified by DSM(Sogeti) - for 2018x.5 Requirement #33444 - Starts
															/*else {
															slSubIntermediate.addElement((String)substituteIds.get(iSub));
														}*/
															//Modified by DSM(Sogeti) - for 2018x.5 Requirement #33444 - Ends
														}
													}
												}
											}
											//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
										}

										//Modified by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
										if (UIUtil.isNotNullAndNotEmpty(strType)
												&& !(strType.contains(pgV3Constants.TYPE_MASTER))
												&& !(strType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART)
														&& bExpandFAB)) {
											// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
											mlFlatBOM.add(tmpMap);
											strListQty.add(strPrimaryQuantity);
											// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
										}

									}
									//Check if there is substitute
									//Find substitute for child object
									//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
									//Skip primary FAB and its Substitutes if "Expand BOM on SAP BOM as Fed" is set to Yes
									if (UIUtil.isNotNullAndNotEmpty(strType) && !(strType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART) && bExpandFAB)) {
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										StringList substituteIds = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
										StringList substituteSAPTypes = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
										StringList substituteNames = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
										StringList substituteTypes = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
										StringList slsubstituteRev = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
										StringList slsubstituteCurrent = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
										StringList slsubstituteRelID = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
										//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Starts

										//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Ends
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										StringList slSubstituteEffectivityDate = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
										//Added by DSM - for 2018x.1 requirement #25043 - Starts
										StringList slSubstituteValidUntilDate = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
										//Added by DSM - for 2018x.1 requirement #25043 - Ends
										StringList slSubstituteOptComponent = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
										StringList slSubstituteComment = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
										//Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										StringList slsubstituteQuantity = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
										//Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
										StringList slSubSpecSubType = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
										//Include the Alternates of Component
										if (!(strDONOTINCLUDETYPE.contains(strType))) {
											// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
											StringList slAlternateIds = getStringListFromMap(tmpMap, pgV3Constants.SELECT_ALTERNATE_ID);
											// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
											for (Object oAlternateId : slAlternateIds) {
												dobjAlternate = DomainObject.newInstance(context, (String) oAlternateId);
												Map mpAlternate = dobjAlternate.getInfo(context, SL_OBJECT_INFO_SELECT);
												if (!pgV3Constants.THIRD_PARTY.equals((String) mpAlternate.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE))) {
													(mpAlternate).put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) tmpMap.get(DomainConstants.SELECT_RELATIONSHIP_ID));
													(mpAlternate).put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
													(mpAlternate).put("IntermediateName", strName + "." + strRev);
													(mpAlternate).put("IntermediateID", strID);
													(mpAlternate).put("IntermediateType", strObjectType);
													(mpAlternate).put("IntermediateLevel", strLevel);
													mlFlatBOM.add(mpAlternate);
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
													strListQty.add(strPrimaryQuantity);
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
												}
											}
										}
										if (null != substituteIds && substituteIds.size() > 0) {
											int iSizeOfSubstitutes = substituteIds.size();
											for (int iSub = 0; iSub < iSizeOfSubstitutes; iSub++) {
												//Add Fabricated Part as SAP BOM Component
												String strSubstitutepgSAPType = (String) substituteSAPTypes.get(iSub);
												//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Starts
												if (!(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
													mapTemp = new HashMap();
													strSubstituteId = (String) substituteIds.get(iSub);
													strSubstituteType = (String) substituteTypes.get(iSub);
													mapTemp.put("name", (String) substituteNames.get(iSub));
													mapTemp.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
													mapTemp.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
													//mapTemp.put(DomainConstants.SELECT_ID, (String)substituteIds.get(iSub));
													mapTemp.put(DomainConstants.SELECT_ID, strSubstituteId);
													//mapTemp.put(DomainConstants.SELECT_TYPE, (String)substituteTypes.get(iSub));
													// Added by (DSM) Sogeti for 22x.02 REQ 46286 - Start
													if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strType) && pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strSubstituteType)) {
														continue;
													}
													// Added by (DSM) Sogeti for 22x.02 REQ 46286 - End
													mapTemp.put(DomainConstants.SELECT_TYPE, strSubstituteType);
													mapTemp.put("IntermediateName", strName + "." + strRev);
													mapTemp.put("IntermediateID", strID);
													mapTemp.put("IntermediateType", strObjectType);
													mapTemp.put(DomainConstants.SELECT_REVISION, (String) slsubstituteRev.get(iSub));
													mapTemp.put(DomainConstants.SELECT_CURRENT, (String) slsubstituteCurrent.get(iSub));
													mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, (String) slSubstituteEffectivityDate.get(iSub));
													//Added by DSM - for 2018x.1 requirement #25043 - Starts
													mapTemp.put(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE, (String) slSubstituteValidUntilDate.get(iSub));
													//Added by DSM - for 2018x.1 requirement #25043 - Ends
													mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, (String) slSubstituteOptComponent.get(iSub));
													mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT, (String) slSubstituteComment.get(iSub));
													mapTemp.put("IntermediateLevel", strLevel);
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
													mapTemp.put(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY, slsubstituteQuantity.get(iSub));
													// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
													//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Starts
													if ((pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteType)) && UIUtil.isNotNullAndNotEmpty(slSubSpecSubType.get(iSub)) && (pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(slSubSpecSubType.get(iSub)))) {
														mlFlatBOM.addAll(getFPPObjectsForCOPBulk(context, mapTemp));
													}
													//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Ends
													if (!(strDONOTINCLUDETYPE.contains((String) substituteTypes.get(iSub))) && !(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
														mlFlatBOM.add(mapTemp);
														// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
														strPrimaryQuantity = DomainConstants.EMPTY_STRING;
														String strChildSubQuantity = (String) mapTemp.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
														strPrimaryQuantity = getReshipperQuantityValue(context, dObjIntermediate, isReshipperSub, strChildSubQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
														strListQty.add(strPrimaryQuantity);
														// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
														if (UIUtil.isNotNullAndNotEmpty(strSubstituteType) && ALTERNATE_ALLOWED_TYPES.contains(strSubstituteType)) {
															mlSubAlts = getAlternates(context, strSubstituteId);
															iAltsSize = mlSubAlts.size();
															for (int j = 0; j < iAltsSize; j++) {
																mpAltMap = (Map) mlSubAlts.get(j);
																//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Starts
																String strFPPSubsAltSpecSubType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
																if (!strFPPSubsAltSpecSubType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY)) {
																	mpAltMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
																	mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
																	mpAltMap.put("IntermediateName", strName + "." + strRev);
																	mpAltMap.put("IntermediateID", strID);
																	mpAltMap.put("IntermediateType", strObjectType);
																	mpAltMap.put("IntermediateLevel", strLevel);
																	mlFlatBOM.add(mpAltMap);
																	// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
																	strListQty.add(strPrimaryQuantity);
																	// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
																}
																//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Ends
															}
														}
													}
													//Modified by DSM(Sogeti) - for 2018x.5 Requirement #33444 - Starts
													/*else {
											slSubIntermediate.addElement((String)substituteIds.get(iSub));
										} */
													//Modified by DSM(Sogeti) - for 2018x.5 Requirement #33444 - Ends
												}
											}
										}

									}//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
								}
							}
						}
					}
					//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
				}
				//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends

				// Now we have stringlist which contains substitutes of type intermediate
				//Expand strlinglist for Sub intermediate objects and get the data.
				// These are for substitute data processing when its an intermediate type of part

				if (null != slSubIntermediate && slSubIntermediate.size() > 0) {
					// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
					strPrimaryQuantity = DomainConstants.EMPTY_STRING;
					// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
					int iSizeOfSubInterm = slSubIntermediate.size();
					float fQtyMultiplier = 0;
					for (int iSubInter = 0; iSubInter < iSizeOfSubInterm; iSubInter++) {

						strInterID = (String) slSubIntermediate.get(iSubInter);
						dObjInter = DomainObject.newInstance(context, strInterID);

						//Order Of Execution Modified by DSM Sogeti for Defect #34846 of 2018x.5 - Starts

						//Now, expand substitute intermediate object and get the connected data
						//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Starts
						//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Starts
						mlSubInterFirstLevelData = dObjInter.getRelatedObjects(context,
								//mlSubInterFirstLevelData = dObjIntermediate.getRelatedObjects(context,
								//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Ends
								//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Ends
								pgV3Constants.RELATIONSHIP_EBOM,    // rel pattern
								//pgV3Constants.SYMBOL_STAR,     		// type pattern
								DomainConstants.QUERY_WILDCARD,        // type pattern
								SL_OBJECT_INFO_SELECT,                // object select
								SL_RELATION_INFO_SELECT_EBOM,        // rel select
								false,                            // to side
								true,                            // from side
								(short) 1,                        // recursion level
								objectWhere,                        // object where clause
								"", 0);                                // rel where clause

						if (null != mlSubInterFirstLevelData && mlSubInterFirstLevelData.size() > 0) {

							mlSubInterFirstLevelData.sort(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, "ascending", "integer");

							int iSizeOfSubInterFirstLevel = mlSubInterFirstLevelData.size();

							for (int iCount = 0; iCount < iSizeOfSubInterFirstLevel; iCount++) {

								tempMap = (Map) mlSubInterFirstLevelData.get(iCount);
								strType = (String) (tempMap).get(DomainConstants.SELECT_TYPE);
								strLevel = (String) (tempMap).get(DomainConstants.SELECT_LEVEL);
								strChildQuantity = (String) tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
								strInterSpecSubType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
								String strPrimarySAPType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
								(tempMap).put("IntermediateLevel", strLevel);
								(tempMap).put("IsDirectComponent", "true");
								//Add Fabricated Part as BOM Component
								strInterSpecSubType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
								//Modified by (DSM) Sogeti for 22x.02 REQ 52875 - Start
								if (!(strDONOTINCLUDETYPE.contains(strType)) && !pgV3Constants.THIRD_PARTY.equals(strInterSpecSubType) && isReshipperSub) {
								//Modified by (DSM) Sogeti for 22x.02 REQ 52875 - End
								
									mlFlatBOM.add(tempMap);
									//Modified by DSM(Sogeti) - for 2018x.2 CUP Reshipper Requirements #29029 #29030 - Starts
									//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Starts
									//if (!(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strPrimarySAPType))) {
									//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Starts
									//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Starts
									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
									strPrimaryQuantity = DomainConstants.EMPTY_STRING;
									// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

									strPrimaryQuantity = getReshipperQuantityValue(context, dObjInter, isReshipperSub, strChildQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
									//String strPrimaryQuantity =getReshipperQuantityValue(context,dObjIntermediate,isReshipperSub,strChildQuantity,strTopNodeBOMQty,strBUOM,fQtyMultiplier);
									//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Ends
									//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Ends
									strListQty.add(strPrimaryQuantity);
									//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Starts
									// CUP Reshipper Primary Alternate
									if (!strDONOTINCLUDETYPE.contains(strType)) {
										oReshipperPrimaryAlternateIds = (Object) (tempMap).get(pgV3Constants.SELECT_ALTERNATE_ID);
										StringList slAlternateIds = new StringList();
										Map mpAlternate = null;
										if (null != oReshipperPrimaryAlternateIds) {
											slAlternateIds = getInstanceList(oReshipperPrimaryAlternateIds);
										}
										for (Object oAlternateId : slAlternateIds) {
											dobjAlternate = DomainObject.newInstance(context, (String) oAlternateId);
											mpAlternate = dobjAlternate.getInfo(context, SL_OBJECT_INFO_SELECT);
											strPriAlternateCUPSpecSubType = (String) mpAlternate.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
											(mpAlternate).put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) tempMap.get(DomainConstants.SELECT_RELATIONSHIP_ID));
											(mpAlternate).put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
											(mpAlternate).put("level", strLevel);
											(mpAlternate).put("IntermediateName", strName + "." + strRev);
											(mpAlternate).put("IntermediateID", strID);
											(mpAlternate).put("IntermediateType", strObjectType);
											(mpAlternate).put("IntermediateLevel", strLevel);
											if (!(pgV3Constants.THIRD_PARTY.equals(strPriAlternateCUPSpecSubType))) {
												mlFlatBOM.add(mpAlternate);
												strListQty.add(strPrimaryQuantity);

											}
										}
										
									}
									//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Ends

									//}
									//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Ends
									/*if(isReshipperSub && !(pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM))){
										strChildQuantity	= (String)tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
										if(UIUtil.isNotNullAndNotEmpty(strTopNodeBOMQty) && UIUtil.isNotNullAndNotEmpty(strChildQuantity))
												{
										fChildSAPQty = (Float.valueOf(strTopNodeBOMQty)).floatValue() * (Float.valueOf(strChildQuantity)).floatValue();
										strListQty.add(String.valueOf(fChildSAPQty));
										}*/
									if (isReshipperSub) {
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										StringList substituteIds = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
										StringList substituteSAPTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
										StringList substituteNames = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
										StringList substituteTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
										StringList slsubstituteRev = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
										StringList slsubstituteCurrent = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
										StringList slsubstituteRelID = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
										//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Starts

										//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Ends
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										StringList slSubstituteEffectivityDate = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
										StringList slSubstituteValidUntilDate = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
										StringList slSubstituteOptComponent = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
										StringList slSubstituteComment = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
										StringList slSubSpecSubType = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
										StringList slsubstituteQuantity = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End

										if (null != substituteIds && substituteIds.size() > 0) {
											int iSizeOfSubstitutes = substituteIds.size();
											for (int iSub = 0; iSub < iSizeOfSubstitutes; iSub++) {
												String strSubstitutepgSAPType = (String) substituteSAPTypes.get(iSub);
												//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Starts
												if (!(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
													//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Ends
													mapTemp = new HashMap();
													strSubstituteId = (String) substituteIds.get(iSub);
													strSubstituteType = (String) substituteTypes.get(iSub);

													mapTemp.put("name", (String) substituteNames.get(iSub));
													mapTemp.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
													mapTemp.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
													mapTemp.put(DomainConstants.SELECT_ID, strSubstituteId);
													mapTemp.put(DomainConstants.SELECT_TYPE, strSubstituteType);
													mapTemp.put("IntermediateName", strName + "." + strRev);
													mapTemp.put("IntermediateID", strID);
													mapTemp.put("IntermediateType", strObjectType);
													mapTemp.put(DomainConstants.SELECT_REVISION, (String) slsubstituteRev.get(iSub));
													mapTemp.put(DomainConstants.SELECT_CURRENT, (String) slsubstituteCurrent.get(iSub));
													mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, (String) slSubstituteEffectivityDate.get(iSub));
													mapTemp.put(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE, (String) slSubstituteValidUntilDate.get(iSub));
													mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, (String) slSubstituteOptComponent.get(iSub));
													mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT, (String) slSubstituteComment.get(iSub));
													mapTemp.put(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY, (String) slsubstituteQuantity.get(iSub));
													mapTemp.put("IntermediateLevel", strLevel);

													//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Starts
													if ((pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteType)) && UIUtil.isNotNullAndNotEmpty(slSubSpecSubType.get(iSub)) && (pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(slSubSpecSubType.get(iSub)))) {
														MapList mlCUPComponents = getFPPObjectsForCOPBulk(context, mapTemp);
														Map mapCUPComponents = null;
														int mlCUPComponentsSize = mlCUPComponents.size();
														for (int jj = 0; jj < mlCUPComponentsSize; jj++) {
															mapCUPComponents = (Map) mlCUPComponents.get(jj);
															if (mapCUPComponents != null && !mapCUPComponents.isEmpty()) {
																String strsubsCUPPFPPQuantity = getReshipperQuantityValue(context, dObjInter, isReshipperSub, (String) mapCUPComponents.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY), strTopNodeBOMQty, strBUOM, fQtyMultiplier);
																mlFlatBOM.add(mapCUPComponents);
																strListQty.add(strsubsCUPPFPPQuantity);
															}
														}
													}
													//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Ends
													if (!(strDONOTINCLUDETYPE.contains((String) substituteTypes.get(iSub))) && !(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
														mlFlatBOM.add(mapTemp);
														//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Starts
														String strChildSubQuantity = (String) mapTemp.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
														//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Starts
														//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Starts
														strsubsCUPQuantity = getReshipperQuantityValue(context, dObjInter, isReshipperSub, strChildSubQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
														//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Ends	//strsubsCUPQuantity=getReshipperQuantityValue(context,dObjIntermediate,isReshipperSub,strChildSubQuantity,strTopNodeBOMQty,strBUOM,fQtyMultiplier);
														//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Ends
														strListQty.add(strsubsCUPQuantity);

														//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Ends
														//Added by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sustitute Alternates Requirements #30849,#30850 - Starts
														if (UIUtil.isNotNullAndNotEmpty(strSubstituteType) && ALTERNATE_ALLOWED_TYPES.contains(strSubstituteType)) {
															mlSubAlts = getAlternates(context, strSubstituteId);
															iAltsSize = mlSubAlts.size();
															if (null != mlSubAlts && iAltsSize > 0) {
																for (int j = 0; j < iAltsSize; j++) {
																	mpAltMap = (Map) mlSubAlts.get(j);
																	strAlternateCUPSpecSubType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
																	strAlternateCUPSAPType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
																	mpAltMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
																	mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
																	mpAltMap.put("IntermediateName", strName + "." + strRev);
																	mpAltMap.put("IntermediateID", strID);
																	mpAltMap.put("IntermediateType", strObjectType);
																	mpAltMap.put("IntermediateLevel", strLevel);
																	if (!strAlternateCUPSpecSubType.contains(pgV3Constants.THIRD_PARTY) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strAlternateCUPSAPType))) {
																		mlFlatBOM.add(mpAltMap);
																		strChildCUPAltQuantity = strsubsCUPQuantity;
																		strListQty.add(strChildCUPAltQuantity);
																	}
																}
															}
														}
														//Added by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sustitute Alternates Requirements #30849,#30850 - Ends
													} else {
														slSubIntermediate.addElement((String) substituteIds.get(iSub));
													}
												}

											}

										}
										//Modified by DSM(Sogeti) - for 2018x.2 CUP Reshipper Requirements #29029 #29030 - Ends
									}
								}
							}

						}
						//Order Of Execution Modified by DSM Sogeti for Defect #34846 of 2018x.5 - Ends
						mlIntermediateChildData = dObjInter.getRelatedObjects(context,
								pgV3Constants.RELATIONSHIP_EBOM,   // rel pattern
								pIntermediateObjectType.getPattern(),  // type pattern
								SL_OBJECT_INFO_SELECT,             // object selects
								SL_RELATION_INFO_SELECT_EBOM,           //  rel selects
								false,                             // to side
								true,                              // from side
								(short) 0,                          //  recursion level
								objectWhere,                       // object where clause
								null, 0);                             //  rel where clause
						fQtyMultiplier = 1;
						String strTemplevel = DomainConstants.EMPTY_STRING;

						//Get first level child for CUP, COP and IP
						if (null != mlIntermediateChildData && mlIntermediateChildData.size() > 0) {
							//mlIntermediateChildData.sort(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER,"ascending","integer");

							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
							ISAPBOM reshipperBOM = new SAPReshipperBOM();
							mlIntermediateChildData = reshipperBOM.getBOMInfoList(context, strInterID, mlIntermediateChildData);
							boolean isSelfComplexBOM;
							boolean isParentComplexBOM;
							processIdLevel = 0;
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End

							int iSizeOfInterChild = mlIntermediateChildData.size();
							Map mpPreviousLevelSubstituteType = new HashMap();

							for (int i = 0; i < iSizeOfInterChild; i++) {

								tempMap = (Map) mlIntermediateChildData.get(i);
								strID = (String) (tempMap).get(DomainConstants.SELECT_ID);
								strObjectType = (String) (tempMap).get(DomainConstants.SELECT_TYPE);
								strLevel = (String) (tempMap).get(DomainConstants.SELECT_LEVEL);
								//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
								strSpecSubType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
								//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends
								mpPreviousLevelSubstituteType.put(parseValue(strLevel), strObjectType);
								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
								isSelfComplexBOM = false;
								if (tempMap.containsKey(keyIsComplexBOM)) {
									isSelfComplexBOM = (Boolean) (tempMap).get(keyIsComplexBOM);
								}
								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End

								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
								if (processIdLevel != 0 && processIdLevel != parseValue(strLevel)) {
									continue;
								} else if (processIdLevel == parseValue(strLevel)) {
									processIdLevel = 0;
								}
								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

								if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strObjectType)
										|| (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)
												&& parseValue(strLevel) > 1)) {

									String strPreviousTypeToScndLevelCOPSubstitute = (String) mpPreviousLevelSubstituteType.get(parseValue(strLevel) - 1);
									String strPreviousTypeToThirdLevelSubsctituteCOP = (String) mpPreviousLevelSubstituteType.get(parseValue(strLevel) - 2);

									boolean isThirdLevelCOP = pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPreviousTypeToScndLevelCOPSubstitute);
									boolean isCOPSpecialCase = isThirdLevelCOP
											&& !pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPreviousTypeToThirdLevelSubsctituteCOP);
									if ((isSelfComplexBOM && !isThirdLevelCOP) || isCOPSpecialCase) {
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
										System.out.println(" ======= Inside Child COP block ========== ");

										for (int j = iSizeOfInterChild - 1; j > 0; j--) {
											mpChildMap = (Map) mlIntermediateChildData.get(j - 1);
											strIntermediateLevel = (String) (mpChildMap).get(DomainConstants.SELECT_LEVEL);
											if (parseValue(strIntermediateLevel) < parseValue(strLevel)) {
												strIntermediateObjectQty = (String) (mpChildMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
												bdIntermediateObjectQty = bdIntermediateObjectQty.multiply(new BigDecimal(strIntermediateObjectQty));
											}
										}
										strCOPToCOPQuantity = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										slCOPSubstituteIds = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
										slCOPSubstituteQuantities = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
										// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
										//Modified by DSM Sogeti for 2018x.5 Defect #36454 - Ends

										// Added by (DSM) Sogeti for 22x.02 REQ 46286 - Start
										slCOPSubstituteTypes = getStringListFromMap(tempMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
										// Added by (DSM) Sogeti for 22x.02 REQ 46286 - End

										// Modfied by (DSM) Sogeti for 22x.02 REQ 46286 - Start
										mpConnectedFPPToChildCOP = getFPPObjectsForChildCOPInCOP(context, strID, strCOPToCOPQuantity, IPSId, slCOPSubstituteIds, strTopNodeBOMQty, bdIntermediateObjectQty, strBUOM, strTopNodeSpecSubType, strSpecSubType, slCOPSubstituteQuantities, slCOPSubstituteTypes);
										// Modfied by (DSM) Sogeti for 22x.02 REQ 46286 - End
										//Modified by DSM Sogeti for 2018x.5 Requirement #30797 #33457- Ends
										bdIntermediateObjectQty = new BigDecimal("1");
										mlChildCOPConnectedFPP = (MapList) mpConnectedFPPToChildCOP.get("mlReleasedFPP");
										slChildCOPConnectedFPPQTY = (StringList) mpConnectedFPPToChildCOP.get("strListQtyValues");

										if (!mlChildCOPConnectedFPP.isEmpty() && !slChildCOPConnectedFPPQTY.isEmpty()) {
											mlReleasedFPP.addAll(mlChildCOPConnectedFPP);
											slReleaseFPPQTY.addAll(slChildCOPConnectedFPPQTY);
										}
										//Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
										//Stop to get GCAS Component
										// if Child Part has where used FPP stop Traverse to the BOM
										if (!mlChildCOPConnectedFPP.isEmpty() && isSelfComplexBOM) {
											processIdLevel = Integer.parseInt(strLevel);
											continue;
										} else {
											// if where-used is not found - then continue.
											if (isSelfComplexBOM) {
												processIdLevel = Integer.parseInt(strLevel);
												continue;
											}
										}
										if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)) {
											//Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
											continue;
										}


									} else if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPreviousTypeToScndLevelCOPSubstitute)) {
										continue;
									}
								}
								strInterSpecSubType = (String) (tempMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
								if ((pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)) && pgV3Constants.THIRD_PARTY.equals(strInterSpecSubType)) {
									break;
								}
								strName = (String) (tempMap).get(DomainConstants.SELECT_NAME);
								strRev = (String) (tempMap).get(DomainConstants.SELECT_REVISION);
								strInterMedLevel = (String) (tempMap).get(DomainConstants.SELECT_LEVEL);
								if ((!pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strObjectType) || !pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)) && strInterMedLevel.equals("1")) {
									fQtyMultiplier = 1;
								}
								String strInterQuantity = (String) tempMap.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
								if (!strTemplevel.equals(strInterMedLevel) && UIUtil.isNotNullAndNotEmpty(strInterQuantity)) {
									//fQtyMultiplier = fQtyMultiplier * (Float.valueOf(strInterQuantity)).floatValue();
									//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Starts
									BigDecimal bdQtyMultiplier = BigDecimal.valueOf(fQtyMultiplier);
									bdQtyMultiplier = bdQtyMultiplier.multiply(new BigDecimal(strInterQuantity));
									fQtyMultiplier = bdQtyMultiplier.floatValue();
									//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Ends
									strTemplevel = strInterMedLevel;
								}

								dObjIntermediate = DomainObject.newInstance(context, strID);
								//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Starts
								//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Starts
								mlConnectedInterChildData = dObjIntermediate.getRelatedObjects(context,
										//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Ends
										//mlConnectedInterChildData = dObjInter.getRelatedObjects(context,
										//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Ends
										pgV3Constants.RELATIONSHIP_EBOM,// rel pattern
										//pgV3Constants.SYMBOL_STAR,     	// type pattern
										DomainConstants.QUERY_WILDCARD, // type pattern
										SL_OBJECT_INFO_SELECT,            // object select
										SL_RELATION_INFO_SELECT_EBOM,    // rel select
										false,                        // to side
										true,                        // from side
										(short) 1,                    // recursion level
										objectWhere,                    // object where clause
										DomainConstants.EMPTY_STRING, 0);// rel where clause
								if (null != mlConnectedInterChildData && mlConnectedInterChildData.size() > 0) {

									mlConnectedInterChildData.sort(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, "ascending", "integer");
									strType = DomainConstants.EMPTY_STRING;

									for (int iCount = 0; iCount < mlConnectedInterChildData.size(); iCount++) {

										tmpMap = (Map) mlConnectedInterChildData.get(iCount);
										strType = (String) (tmpMap).get(DomainConstants.SELECT_TYPE);
										strInterSpecSubType = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
										strChildQuantity = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
										String strPrimarySAPType = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
										//Modified by (DSM) Sogeti for 22x.02 REQ 52875 - Start
										if (!(strDONOTINCLUDETYPE.contains(strType)) && !pgV3Constants.THIRD_PARTY.equals(strInterSpecSubType) && isReshipperSub) {
										//Modified by (DSM) Sogeti for 22x.02 REQ 52875 - Ends
											(tmpMap).put("IntermediateName", strName + "." + strRev);
											(tmpMap).put("IntermediateID", strID);
											(tmpMap).put("IntermediateType", strObjectType);
											(tmpMap).put("IsDirectComponent", "true");
											//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
											if (isReshipperSub && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM) || (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType))))
												//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
											{
												int iLevel = Integer.parseInt(strLevel);
												iLevel++;
												(tmpMap).put("IntermediateLevel", String.valueOf(iLevel));
											} else {
												(tmpMap).put("IntermediateLevel", strLevel);
											}
											//Modified by DSM(Sogeti) - for 2018x.2 CUP Reshipper Requirements #29029 #29030 - Starts
											//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Starts
											//if (!(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strPrimarySAPType))) {
											//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Starts
											//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Starts

											// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
											
											String strExpandBOMonSAPBOMVal = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
											String strExpandBOMQty = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
											
											if ((pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType))
													&& UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMVal)
													&& "TRUE".equalsIgnoreCase(strExpandBOMonSAPBOMVal)) {
												Map mpFABBOM = null;
												MapList mlSAPExpandBOMFABList = new MapList();
												MapList mlSAPExpandBOMSAPQty = new MapList();
												mlSAPExpandBOMFABList = getFABExpandBOMOnSAPComponents(context, tmpMap);

												int iSAPExpandBOMFABSize = mlSAPExpandBOMFABList.size();

												mlSAPExpandBOMSAPQty = getReshipperQtyForFABComponents(context,
														dObjIntermediate, strTopNodeBOMQty, strBUOM, fQtyMultiplier,
														mlSAPExpandBOMFABList);
												if (null != mlSAPExpandBOMSAPQty
														&& iSAPExpandBOMFABSize == mlSAPExpandBOMSAPQty.size()) {
													for (int f = 0; iSAPExpandBOMFABSize > f; f++) {
														mpFABBOM = (Map) mlSAPExpandBOMSAPQty.get(f);
														String strQuantity = (String) mpFABBOM.get("Qty");
														strListQty.add(strQuantity);
													}
												}
												mlFlatBOM.addAll(mlSAPExpandBOMFABList);
											// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
											}	
											// Modified by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
											 else {
											// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
											strPrimaryQuantity = DomainConstants.EMPTY_STRING;
											// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

											strPrimaryQuantity = getReshipperQuantityValue(context, dObjIntermediate, isReshipperSub, strChildQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
											//String strPrimaryQuantity = getReshipperQuantityValue(context,dObjInter,isReshipperSub,strChildQuantity,strTopNodeBOMQty,strBUOM,fQtyMultiplier);
											//Added by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Ends
											//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Ends
											strListQty.add(strPrimaryQuantity);
											//}
											//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Ends
											/*if(isReshipperSub && !(pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM))){
												strChildQuantity	= (String)tmpMap.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
												if(UIUtil.isNotNullAndNotEmpty(strTopNodeBOMQty) && UIUtil.isNotNullAndNotEmpty(strChildQuantity))
												{
												fChildSAPQty = (Float.valueOf(strTopNodeBOMQty)).floatValue() * fQtyMultiplier * (Float.valueOf(strChildQuantity)).floatValue();
												strListQty.add(String.valueOf(fChildSAPQty));
												}
											}*/
											mlFlatBOM.add(tmpMap);

											//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Starts
											// CUP Reshipper Primary Alternate
											if (!strDONOTINCLUDETYPE.contains(strType)) {
												oReshipperPrimaryAlternateIds = (Object) (tmpMap).get(pgV3Constants.SELECT_ALTERNATE_ID);
												StringList slAlternateIds = new StringList();
												Map mpAlternate = null;
												if (null != oReshipperPrimaryAlternateIds) {
													slAlternateIds = getInstanceList(oReshipperPrimaryAlternateIds);
												}
												for (Object oAlternateId : slAlternateIds) {
													dobjAlternate = DomainObject.newInstance(context, (String) oAlternateId);
													mpAlternate = dobjAlternate.getInfo(context, SL_OBJECT_INFO_SELECT);
													strPriAlternateCOPSpecSubType = (String) mpAlternate.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
													(mpAlternate).put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) tmpMap.get(DomainConstants.SELECT_RELATIONSHIP_ID));
													(mpAlternate).put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
													(mpAlternate).put("level", strLevel);
													(mpAlternate).put("IntermediateName", strName + "." + strRev);
													(mpAlternate).put("IntermediateID", strID);
													(mpAlternate).put("IntermediateType", strObjectType);
													//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
													if (isReshipperSub && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM) || (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType)))) {
														int iLevel = Integer.parseInt(strLevel);
														iLevel++;
														(mpAlternate).put("IntermediateLevel", String.valueOf(iLevel));
													} else {
														(mpAlternate).put("IntermediateLevel", strLevel);
													}
													//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
													if (!(pgV3Constants.THIRD_PARTY.equals(strPriAlternateCOPSpecSubType))) {
														mlFlatBOM.add(mpAlternate);
														strListQty.add(strPrimaryQuantity);
													}
												}
												
											}
											//Added by DSM(Sogeti) - for 2018x.3 Defect #32076 - Ends
											if (isReshipperSub) {
												// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
												StringList substituteIds = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
												StringList substituteSAPTypes = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
												StringList substituteNames = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
												StringList substituteTypes = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
												StringList slsubstituteRev = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
												StringList slsubstituteCurrent = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
												StringList slsubstituteRelID = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
												//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Starts
												//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Ends
												StringList slSubstituteEffectivityDate = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
												StringList slSubstituteValidUntilDate = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
												StringList slSubstituteOptComponent = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
												StringList slSubstituteComment = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
												StringList slSubSpecSubType = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
												StringList slsubstituteQuantity = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
												// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
												if (null != substituteIds && substituteIds.size() > 0) {
													int iSizeOfSubstitutes = substituteIds.size();
													for (int iSub = 0; iSub < iSizeOfSubstitutes; iSub++) {
														String strSubstitutepgSAPType = (String) substituteSAPTypes.get(iSub);
														//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Starts
														if (!(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
															//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Ends
															mapTemp = new HashMap();
															strSubstituteId = (String) substituteIds.get(iSub);
															strSubstituteType = (String) substituteTypes.get(iSub);
															mapTemp.put("name", (String) substituteNames.get(iSub));
															mapTemp.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
															mapTemp.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
															mapTemp.put(DomainConstants.SELECT_ID, strSubstituteId);
															mapTemp.put(DomainConstants.SELECT_TYPE, strSubstituteType);
															mapTemp.put("IntermediateName", strName + "." + strRev);
															mapTemp.put("IntermediateID", strID);
															mapTemp.put("IntermediateType", strObjectType);
															mapTemp.put(DomainConstants.SELECT_REVISION, (String) slsubstituteRev.get(iSub));
															mapTemp.put(DomainConstants.SELECT_CURRENT, (String) slsubstituteCurrent.get(iSub));
															mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, (String) slSubstituteEffectivityDate.get(iSub));
															mapTemp.put(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE, (String) slSubstituteValidUntilDate.get(iSub));
															mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, (String) slSubstituteOptComponent.get(iSub));
															mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT, (String) slSubstituteComment.get(iSub));
															mapTemp.put(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY, (String) slsubstituteQuantity.get(iSub));

															//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
															if (isReshipperSub && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM) || (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType)))) {
																int iLevel = Integer.parseInt(strLevel);
																iLevel++;
																(mapTemp).put("IntermediateLevel", String.valueOf(iLevel));
															} else {
																(mapTemp).put("IntermediateLevel", strLevel);
															}
															//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
															//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Starts
															if ((pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteType)) && UIUtil.isNotNullAndNotEmpty(slSubSpecSubType.get(iSub)) && (pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(slSubSpecSubType.get(iSub)))) {

																MapList mlCOPComponents = getFPPObjectsForCOPBulk(context, mapTemp);
																Map mapCOPComponents = null;
																String strsubsCOPFPPQuantity = DomainConstants.EMPTY_STRING;
																int mlCOPComponentsSize = mlCOPComponents.size();
																for (int jj = 0; jj < mlCOPComponentsSize; jj++) {
																	mapCOPComponents = (Map) mlCOPComponents.get(jj);
																	if (mapCOPComponents != null && !mapCOPComponents.isEmpty()) {
																		strsubsCOPFPPQuantity = getReshipperQuantityValue(context, dObjIntermediate, isReshipperSub, (String) mapCOPComponents.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY), strTopNodeBOMQty, strBUOM, fQtyMultiplier);
																		mlFlatBOM.add(mapCOPComponents);
																		strListQty.add(strsubsCOPFPPQuantity);
																	}
																}
															}
															//Modified by DSM Sogeti for Requirement #33444 2018x.5 - Ends
															if (!(strDONOTINCLUDETYPE.contains((String) substituteTypes.get(iSub))) && !(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
																mlFlatBOM.add(mapTemp);
																//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Starts
																String strChildSubQuantity = (String) mapTemp.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
																//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Starts
																//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Starts
																strsubsCOPQuantity = getReshipperQuantityValue(context, dObjIntermediate, isReshipperSub, strChildSubQuantity, strTopNodeBOMQty, strBUOM, fQtyMultiplier);
																//strsubsCOPQuantity = getReshipperQuantityValue(context,dObjInter,isReshipperSub,strChildSubQuantity,strTopNodeBOMQty,strBUOM,fQtyMultiplier);
																//Modified by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sequence Defect #32076 - Ends
																//Code reverted by DSM(Sogeti) - for Defect #33302:2018x.3 Prod Issue - Ends
																strListQty.add(strsubsCOPQuantity);
																//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30512 - Ends
																//Added by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sustitute Alternates Requirements #30849,#30850 - Starts
																if (UIUtil.isNotNullAndNotEmpty(strSubstituteType) && ALTERNATE_ALLOWED_TYPES.contains(strSubstituteType)) {
																	mlSubAlts = getAlternates(context, strSubstituteId);
																	iAltsSize = mlSubAlts.size();
																	if (null != mlSubAlts && iAltsSize > 0) {
																		for (int j = 0; j < iAltsSize; j++) {
																			mpAltMap = (Map) mlSubAlts.get(j);
																			strAlternateCOPSpecSubType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
																			strAlternateCOPSAPType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
																			mpAltMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
																			mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
																			mpAltMap.put("IntermediateName", strName + "." + strRev);
																			mpAltMap.put("IntermediateID", strID);
																			mpAltMap.put("IntermediateType", strObjectType);
																			//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
																			if (isReshipperSub && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM) || (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType)))) {
																				int iLevel = Integer.parseInt(strLevel);
																				iLevel++;
																				(mpAltMap).put("IntermediateLevel", String.valueOf(iLevel));
																			} else {
																				(mpAltMap).put("IntermediateLevel", strLevel);
																			}
																			//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
																			if (!strAlternateCOPSpecSubType.contains(pgV3Constants.THIRD_PARTY) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strAlternateCOPSAPType))) {
																				mlFlatBOM.add(mpAltMap);
																				strChildCOPAltQuantity = strsubsCOPQuantity;
																				strListQty.add(strChildCOPAltQuantity);
																			}
																		}
																	}
																}
																//Added by DSM(Sogeti) - for 2018x.3 CUP Reshipper Sustitute Alternates Requirements #30849,#30850 - Ends
															} else {
																slSubIntermediate.addElement((String) substituteIds.get(iSub));
															}
														}
													}

												}
											}
											//Modified by DSM(Sogeti) - for 2018x.2 CUP Reshipper Requirements #29029 #29030 - Ends
											}
											// Modified by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
										}
									}
								}
							}
						}
					}
				}

				//Expand FPP and get first level parts apart from CUP, COP and IP
				MapList mlFPPFirstLevelData = dObjForTopNodePart.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_EBOM,    // rel pattern
						//pgV3Constants.SYMBOL_STAR,         	// type pattern
						DomainConstants.QUERY_WILDCARD,        // type pattern
						SL_OBJECT_INFO_SELECT,                // object select
						SL_RELATION_INFO_SELECT_EBOM,        // rel select
						false,                            // to side
						true,                            // from side
						(short) 1,                        // recursion level
						objectWhere,                        // object where clause
						DomainConstants.EMPTY_STRING, 0);    // rel where clause

				if (null != mlFPPFirstLevelData && mlFPPFirstLevelData.size() > 0) {
					mlFPPFirstLevelData.sort(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, "ascending", "integer");
					int iSizeOfFirstLevelData = mlFPPFirstLevelData.size();
					for (int iCount = 0; iCount < iSizeOfFirstLevelData; iCount++) {
						tmpMap = (Map) mlFPPFirstLevelData.get(iCount);
						strType = (String) (tmpMap).get(DomainConstants.SELECT_TYPE);
						strLevel = (String) (tmpMap).get(DomainConstants.SELECT_LEVEL);
						strPAPChildSpecSubType = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
						MapList mlFABChilData = new MapList();
						String strChildId = (String) (tmpMap).get(DomainConstants.SELECT_ID);
						String strExpandBOMonSAPBOMVal = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
						String strExpandBOMQty = (String) (tmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
						
						if ((pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strTopNodeType))
								&& UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMVal)
								&& "TRUE".equalsIgnoreCase(strExpandBOMonSAPBOMVal)) {
							mlFABChilData = getFABExpandBOMOnSAPComponents(context, tmpMap);
							mlFlatBOM.addAll(mlFABChilData);
						}
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
						// Modfied by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
						else {
						//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
						//COP BULK in Direct Components of PAP
						if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strType) && pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strTopNodeType) && UIUtil.isNotNullAndNotEmpty(strPAPChildSpecSubType) && pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(strPAPChildSpecSubType)) {
							(tmpMap).put("IsDirectComponent", "true");
							mlPAPCOPBulk = getFPPObjectsForCOPBulk(context, tmpMap);

							if (!mlPAPCOPBulk.isEmpty()) {
								mlFlatBOM.addAll(mlPAPCOPBulk);
							}
						}
						//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends
						if (!(strDONOTINCLUDETYPE.contains(strType)) && !pgV3Constants.THIRD_PARTY.equals(strPAPChildSpecSubType)) {
							(tmpMap).put("IsDirectComponent", "true");

							//If BOM contain master then no object will be displayed on SAP BOM as Fed
							if (strType.contains(pgV3Constants.TYPE_MASTER)) {

								return (new MapList());
							}
							mlFlatBOM.add(tmpMap);
						}
						if ((!pgV3Constants.THIRD_PARTY.equals(strPAPChildSpecSubType) && !(strDONOTINCLUDETYPE.contains(strType))) || (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strType) && (pgV3Constants.BULK_INTERMEDIATE_UNIT.equals(strPAPChildSpecSubType)) && !mlPAPCOPBulk.isEmpty())) {
							//Check if there is substitute
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
							StringList substituteIds = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
							StringList substituteSAPTypes = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
							StringList substituteNames = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
							StringList substituteTypes = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
							StringList substituteRevs = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
							StringList slsubstituteCurrent = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
							StringList slsubstituteRelID = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
							//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Starts
							//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Ends
							StringList slSubstituteEffectivityDate = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
							//Added by DSM - for 2018x.1 requirement #25043 - Starts
							StringList slSubstituteValidUntilDate = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
							StringList slSubstituteOptComponent = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
							StringList slSubstituteComment = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
							StringList slSubSpecSubType = getStringListFromMap(tmpMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
							// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
							//Include the Alternates of Component
							if (!(strDONOTINCLUDETYPE.contains(strType))) {
								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
								StringList slAlternateIds = getStringListFromMap(tmpMap, pgV3Constants.SELECT_ALTERNATE_ID);
								// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
								for (Object oAlternateId : slAlternateIds) {
									dobjAlternate = DomainObject.newInstance(context, (String) oAlternateId);
									Map mpAlternate = dobjAlternate.getInfo(context, SL_OBJECT_INFO_SELECT);
									//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
									//COP BULK - Primary Component Alternate PAP
									strPriAltSpecSubType = (String) mpAlternate.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
									if (!strPriAltSpecSubType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY)) {
										(mpAlternate).put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) tmpMap.get(DomainConstants.SELECT_RELATIONSHIP_ID));
										(mpAlternate).put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
										(mpAlternate).put("level", strLevel);
										mlFlatBOM.add(mpAlternate);
									}
									//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends
								}
							}

							if (null != substituteIds && substituteIds.size() > 0) {
								int iSizeOfSubstitutes = substituteIds.size();
								for (int iSub = 0; iSub < iSizeOfSubstitutes; iSub++) {
									strSubstituteType = (String) substituteTypes.get(iSub);
									//Add Fabricated Part as BOM Component
									String strSubstitutepgSAPType = (String) substituteSAPTypes.get(iSub);

									//Modified by DSM Sogeti for Requirement #33443 2018x.5 - Starts
									//COP BULK - Substitute PAP
									if (!(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
										//Modified by DSM Sogeti for Requirement #33443 2018x.5 - Ends
										mapTemp = new HashMap();
										strSubstituteId = (String) substituteIds.get(iSub);

										mapTemp.put("name", (String) substituteNames.get(iSub));
										mapTemp.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
										mapTemp.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
										//mapTemp.put(DomainConstants.SELECT_ID, (String)substituteIds.get(iSub));
										mapTemp.put(DomainConstants.SELECT_ID, strSubstituteId);
										//mapTemp.put(DomainConstants.SELECT_TYPE, (String)substituteTypes.get(iSub));
										mapTemp.put(DomainConstants.SELECT_TYPE, strSubstituteType);
										mapTemp.put(DomainConstants.SELECT_REVISION, (String) substituteRevs.get(iSub));
										mapTemp.put(DomainConstants.SELECT_CURRENT, (String) slsubstituteCurrent.get(iSub));
										mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, (String) slSubstituteEffectivityDate.get(iSub));
										mapTemp.put(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE, (String) slSubstituteValidUntilDate.get(iSub));
										//Added by DSM - for 2018x.1 requirement #25043 - Ends
										mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, (String) slSubstituteOptComponent.get(iSub));
										mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT, (String) slSubstituteComment.get(iSub));
										mapTemp.put("level", strLevel);
										//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
										//COP BULK - Substitute PAP
										if ((pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteType)) && UIUtil.isNotNullAndNotEmpty(slSubSpecSubType.get(iSub)) && (pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(slSubSpecSubType.get(iSub)))) {
											mlFlatBOM.addAll(getFPPObjectsForCOPBulk(context, mapTemp));
										}
										//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends

										if (!(strDONOTINCLUDETYPE.contains(strSubstituteType)) && !(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub))) && !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubstitutepgSAPType))) {
											mlFlatBOM.add(mapTemp);

											if (UIUtil.isNotNullAndNotEmpty(strSubstituteType) && ALTERNATE_ALLOWED_TYPES.contains(strSubstituteType)) {
												mlSubAlts = getAlternates(context, strSubstituteId);
												iAltsSize = mlSubAlts.size();
												for (int i = 0; i < iAltsSize; i++) {
													mpAltMap = (Map) mlSubAlts.get(i);
													//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
													//Subtitute Alternate of PAP
													strSubAltSpecSubType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
													if (!strSubAltSpecSubType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY)) {
														mpAltMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String) slsubstituteRelID.get(iSub));
														mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
														mpAltMap.put("IntermediateName", strName + "." + strRev);
														mpAltMap.put("IntermediateID", strID);
														mpAltMap.put("IntermediateType", strObjectType);
														mpAltMap.put("IntermediateLevel", strLevel);
														mlFlatBOM.add(mpAltMap);
													}
													//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends
												}
											}
										}
									}
								}
							}
						}
					}
						// Modfied by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
					}
				}
				if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strTopNodeType)) {
					mpTUPDATA = getPartBOMFEEDData(context, IPSId, strTopNodeType);
				}

				//calculate BOM Quantitiy
				Map mParamList = new HashMap();
				mParamList.put("objectId", IPSId);
				Map hmArgMapParams = new HashMap();
				hmArgMapParams.put("objectList", mlFlatBOM);
				hmArgMapParams.put("paramList", mParamList);
				String strCalcQTY = null;


				if ((null != mlFlatBOM && mlFlatBOM.size() > 0) || (null != mlReleasedFPP && mlReleasedFPP.size() > 0)) {

					//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
					if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strTopNodeType) && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM) || (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType)))) {
						//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
						Map mpBOMQTY = getBOMQTYDataFPP(context, hmArgMapParams, isReshipperSub, slSubstituteInterIdReshipper, strBUOM);
						fIntermediateSAPQty = (float) mpBOMQTY.get("fIntermediateSAPQty");
						strListQty = (StringList) mpBOMQTY.get("strListQtyValues");
					} else if (!isReshipperSub) {
						strListQty = getQuantityValue(context, IPSId, mlFlatBOM);
					}
					// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
					if (null != mpTUPDATA && mpTUPDATA.size() > 0) {
						if((mlFlatBOM.isEmpty()) && (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType))) {
							mlFlatBOM.addAll(mpTUPDATA);
							strListQty.addAll(getTUPBOMQTYData(context, IPSId, mpTUPDATA, fIntermediateSHALBSAPQty, strBUOM));
						} else {
							mlFlatBOM.addAll(mpTUPDATA);
							strListQty.addAll(getTUPBOMQTYData(context, IPSId, mpTUPDATA, fIntermediateSAPQty, strBUOM));
						}
					}
					// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends

					if (null != mlReleasedFPP && mlReleasedFPP.size() > 0) {
						mlFlatBOM.addAll(mlReleasedFPP);
						strListQty.addAll(slReleaseFPPQTY);
					}
					slSubstituteGroupValues = getSubstituteGroupValue(context, IPSId, mlFlatBOM);
					
				} 
				// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
				else if (null != mpTUPDATA && mpTUPDATA.size() > 0) {
					if((mlFlatBOM.isEmpty()) && (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType))) {
						strListQty = getTUPBOMQTYData(context, IPSId, mpTUPDATA, fIntermediateSHALBSAPQty, strBUOM);
						slSubstituteGroupValues = getSubstituteGroupValue(context, IPSId, mpTUPDATA);
						mlFlatBOM = mpTUPDATA;
					} else {
					strListQty = getTUPBOMQTYData(context, IPSId, mpTUPDATA, fIntermediateSAPQty, strBUOM);
					//Substitute grouping is done for TUP
					slSubstituteGroupValues = getSubstituteGroupValue(context, IPSId, mpTUPDATA);
					mlFlatBOM = mpTUPDATA;
					}
				}
				// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
				if (null != mlFlatBOM) {
					int iSizeFlatBOM = mlFlatBOM.size();
					if (null != strListQty && iSizeFlatBOM == strListQty.size()) {
						for (int i = 0; iSizeFlatBOM > i; i++) {
							//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Starts
							mpAlternateDetails = new HashMap();
							sCATIAAPPDeliver = true;
							//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Ends
							mpBOM = (Map) mlFlatBOM.get(i);
							strType = (String) mpBOM.get(DomainObject.SELECT_TYPE);
							//Add Fabricated Part as BOM Component
							if (!(strDONOTINCLUDETYPE.contains(strType))) {
								strFindNumb = (String) mpBOM.get(DomainConstants.SELECT_ATTRIBUTE_FIND_NUMBER);
								strEffectivityDateChild = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
								//Added by DSM - for 2018x.1 requirement #25043 - Starts
								strValidUntilDate = (String) mpBOM.get(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE);
								//Added by DSM - for 2018x.1 requirement #25043 - Ends
								sChildName = (String) mpBOM.get(DomainConstants.SELECT_NAME);
								sQuantity = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITY);
								strMax = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGMAXQUANTITY);
								strMin = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGMINQUANTITY);
								sTarget = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
								strPosInd = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
								strFILBaseQty = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
								strID = (String) mpBOM.get(DomainObject.SELECT_ID);
								strObjectType = (String) mpBOM.get(DomainConstants.SELECT_TYPE);
								strSAPType = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
								strQtyTmp = (String) strListQty.get(i);
								strGroupTmp = (String) slSubstituteGroupValues.get(i);
								String strRelID = (String) mpBOM.get(DomainConstants.SELECT_RELATIONSHIP_ID);
								strOptComponent = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
								strComment = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
								strRelationship = (String) mpBOM.get("relationship");

								//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Starts
								if (UIUtil.isNotNullAndNotEmpty(strID) && UIUtil.isNotNullAndNotEmpty(strObjectType) && UIUtil.isNotNullAndNotEmpty(strRelationship) && pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strObjectType) && (DomainConstants.RELATIONSHIP_ALTERNATE.equalsIgnoreCase(strRelationship))) {

									doAlternate = DomainObject.newInstance(context, strID);

									mpAlternateDetails = doAlternate.getInfo(context, slAlternateSelects);
									sBOMComponentCurrent = (String) mpAlternateDetails.get(DomainConstants.SELECT_CURRENT);
									sAuthoringApplication = (String) mpAlternateDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);

									if (UIUtil.isNotNullAndNotEmpty(sAuthoringApplication) && pgV3Constants.RANGE_PGAUTHORINGAPPLICATION_LPD.equalsIgnoreCase(sAuthoringApplication) && !(pgV3Constants.STATE_RELEASE.equalsIgnoreCase(sBOMComponentCurrent))) {

										sCATIAAPPDeliver = false;
									}
								}

								if (!pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSAPType) && !strObjectType.contains(pgV3Constants.TYPE_MASTER) && sCATIAAPPDeliver) {
									//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Ends
									//Modified by DSM - for 2018x.1 requirement #25043 - Starts
									arrBOM = buildBOMArray(strTopNodeName, strFindNumb,
											strEffectivityDateChild,
											sChildName, strQtyTmp,
											strMax, sTarget, strMin,
											strTopNodeEffeDate,
											strTopNodeBOMQty, strPosInd, strFILBaseQty, strpgPhaseBOMQty, strGroupTmp, strID, strOptComponent, strComment,
											"", "", "", strValidUntilDate);
									//Modified by DSM - for 2018x.1 requirement #25043 - Ends
									arrBOM[pgV3Constants.INDEX_TPU_ID] = (String) mpBOM.get("TUP_ID");
									alBOM.add(arrBOM);
								}
							}
						}
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw new Exception(ex.getMessage());
		}
		return alBOM;
	}

	/* Group the EBOM/Substitute/Alternate using Grouping algorithm.
	 *
	 * 	Substitute/Alternate components shall be grouped with their primary component
				using a grouping identifier.
				The identifier for substitutes/alternates and their primary component shall be a two character value
				for each set of substitutes/alternates.
				The Substitute/Alternates Identifier is a two character value that is unique
				for each substitute/alternate grouping on the BOM.

	 *	The two character values adhere to the following format and sequence:
					The first Identifier used is 9A.
					The second group uses the identifier 9B.
					The sequence continues as:  9C,9D, 9E, ...,9Z,8A, ... 8Z, ...,7A,...,1Z, ..., 1Z.
	 */

	/**
	 * groupPartObjects- Group the EBOM/Substitute/Alternate using Grouping algorithm.
	 *
	 * @param context          the eMatrix <code>Context</code> object
	 * @param String,ArrayList
	 * @return MapList
	 * @throws Exception
	 */


	public synchronized MapList groupPartObjects(Context context, String sPartObjectID, ArrayList alPartObjects) throws Exception {

		MapList mlPartObjectsWithGroupAndQty = new MapList();
		int iSize;

		char cAltItem1 = pgV3Constants.ALT_ITEM_NINE;
		char cAltItem2 = pgV3Constants.ALT_ITEM_A;

		try {

			if (null != alPartObjects && BusinessUtil.isNotNullOrEmpty(sPartObjectID)) {

				iSize = alPartObjects.size();

				if (iSize > 0) {

					Map mtempMap = null;

					Object substituteInterObject = null;
					Object AlternateInterType = null;

					HashMap hmMap = null;

					StringList slsubstitute = new StringList(); // Intermediate parts substitute parts types
					StringList slAlternate = new StringList();

					String sFROMID_EBOM_SUB_TO_ID = "frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.id";

					String sFROM_ALT_TO_ID = "from[" + DomainConstants.RELATIONSHIP_ALTERNATE + "].to.id";

					for (int i = 0; i < iSize; i++) {
						mtempMap.put(i, alPartObjects.get(i));

						hmMap = new HashMap();

						substituteInterObject = (Object) (mtempMap).get(sFROMID_EBOM_SUB_TO_ID);

						if (null != substituteInterObject) {
							//Refactoring - InstanceList - 32
							slsubstitute = getInstanceList(substituteInterObject);
						}

						AlternateInterType = (Object) (mtempMap).get(sFROM_ALT_TO_ID);

						if (null != AlternateInterType) {
							//Refactoring - InstanceList - 33
							slAlternate = getInstanceList(AlternateInterType);
						}

						hmMap.put("primary", sPartObjectID);
						hmMap.put("substitute", slsubstitute);
						hmMap.put("alternate", slAlternate);
						hmMap.put("Identifier1", cAltItem1);
						hmMap.put("Identifier2", cAltItem2);

						mlPartObjectsWithGroupAndQty.add(hmMap);
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw new Exception(ex.getMessage());
		}
		return mlPartObjectsWithGroupAndQty;
	}

	/*
	Get all the Plants connected to the Object.
    Filter the above plan list by checking eligibility of Plants to process.

	 * If the Manufacting status of Parent Object is not PLANNING, then
			PLM sends the Authorized to Produce Plants to SAP.

	 * If the SAP type is doc on the individual connected to the Master BOM components
			or Master Substitute then dont add the individual for BOM eDelivery.
			Manufcating Status logic is PLANNING, then no BOM eDelivery

	 * If the Manufacting Status of Parent Object is PLANNING ,
			then PLM sends the Plant value  as 3151 to SAP.


	 */

	/**
	 * getEligiblePlants- checking eligibility of Plants to process.
	 *
	 * @param context          the eMatrix <code>Context</code> object
	 * @param String,ArrayList
	 * @return String Array
	 * @throws Exception
	 */

	public synchronized String[] getEligiblePlants(Context context, String sPartObjectID) throws Exception {
		String sStatus = "";
		String strType = "";
		String sStage = "";

		String sPlant = DomainConstants.EMPTY_STRING;
		String sAuthorized = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
		String sAuthorizedToUse = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
		String sActivated = DomainConstants.EMPTY_STRING;

		MapList mlEligiblePlants = new MapList();
		Map hmTmpMap = new HashMap();
		//String strRelWhere = "tomid["+pgV3Constants.RELATIONSHIP_ASSOCIATED_PLANTS+"]==True";
		StringList slEligiblePlants = new StringList(3);
		slEligiblePlants.addElement(DomainConstants.SELECT_TYPE);
		slEligiblePlants.addElement(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		slEligiblePlants.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);

		StringList slDesSitesSelectables = new StringList(2);
		slDesSitesSelectables.add(DomainConstants.SELECT_CURRENT);
		slDesSitesSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPDESIGNATEDSITESLIST);

		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
		StringList slEMEASitesSelectables = new StringList(2);
		slEMEASitesSelectables.add(DomainConstants.SELECT_CURRENT);
		slEMEASitesSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PG_EMEA_PLANTS_LIST);
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends


		StringList slPlantRelSelect = new StringList(3);
		slPlantRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
		slPlantRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
		slPlantRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE);
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends

		StringList slPlantSelect = new StringList(2);
		slPlantSelect.add(DomainConstants.SELECT_NAME);
		slPlantSelect.add(DomainConstants.SELECT_ID);

		StringList slPlantsData = new StringList();
		StringList slPlants = new StringList();
		StringList slAuthorized = new StringList();
		StringList slActivated = new StringList();
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
		StringList slAuthorizedToUse = new StringList();
		String strFinalPlantName = DomainConstants.EMPTY_STRING;
		String strPlantNameWithTableParam = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
		String[] saReturn = null;
		try {
			if (BusinessUtil.isNotNullOrEmpty(sPartObjectID)) {

				saReturn = new String[1];
				DomainObject doTS = DomainObject.newInstance(context, sPartObjectID);
				hmTmpMap = doTS.getInfo(context, slEligiblePlants);

				if (null != hmTmpMap && hmTmpMap.size() > 0) {

					sStatus = (String) hmTmpMap.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
					sStage = (String) hmTmpMap.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
					strType = (String) hmTmpMap.get(DomainConstants.SELECT_TYPE);
				}
				/*
				 *Check if object is connected to atleast one Plant with Authorize to Produce
				 *and Plants location listed in one of the SAP plant sites in the Config Object.
				 *	location listed in one of the SAP plant sites in the Config Object.
				 *	(pgSAPDesignatedSites DesignatedSitesObj -)
				 */

				DomainObject doSAPSiteObj = DomainObject.newInstance(context, new BusinessObject(pgV3Constants.TYPE_PGSAPDESIGNATEDSITES, pgV3Constants.CONFIG_OBJECT_DESIGNATEDSITESOBJ, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION));

				Map mpSAPSiteInfo = (Map) doSAPSiteObj.getInfo(context, slDesSitesSelectables);

				String sDesSitesCurrent = (String) mpSAPSiteInfo.get(DomainConstants.SELECT_CURRENT);
				String sSiteListInConfig = (String) mpSAPSiteInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPDESIGNATEDSITESLIST);
				//Modified by DSM(Sogeti) for Performance Issue on 07-02-2017 Starts

				//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
				DomainObject doEMEASiteObj = DomainObject.newInstance(context, new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, pgV3Constants.OBJECT_PG_EMEA_PLANTS_LIST, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION));

				Map mpEMEASiteInfo = (Map) doEMEASiteObj.getInfo(context, slEMEASitesSelectables);

				String sEMEASitesCurrent = (String) mpEMEASiteInfo.get(DomainConstants.SELECT_CURRENT);
				String sEMEASiteListInConfig = (String) mpEMEASiteInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PG_EMEA_PLANTS_LIST);
				//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends

				//mlEligiblePlants = doTS.getRelatedObjects(context,pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY, pgV3Constants.SYMBOL_STAR, SL_OBJECT_INFO_SELECT, SL_RELATION_INFO_SELECT_OTHERS, true, false,(short) 1, "", "", 0);
				mlEligiblePlants = doTS.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY, "Plant", slPlantSelect, slPlantRelSelect, true, false, (short) 1, "", "", 0);
				//Modified by DSM(Sogeti) for Performance Issue on 07-02-2017 Ends
				if (null != mlEligiblePlants && mlEligiblePlants.size() > 0) {

					int iSizeOfEligiblePlants = mlEligiblePlants.size();
					String strPlantName = "";
					for (int iCount = 0; iCount < iSizeOfEligiblePlants; iCount++) {

						hmTmpMap = (Map) mlEligiblePlants.get(iCount);

						strPlantName = (String) (hmTmpMap).get(DomainConstants.SELECT_NAME);

						/*if(BusinessUtil.isNullOrEmpty(strPlantName)){

							strPlantName =(String)(hmTmpMap).get("to["+pgV3Constants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY+"].from.name");

						}*/
						slPlants.add(strPlantName);
						slAuthorized.add((String) (hmTmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE));
						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
						slAuthorizedToUse.add((String) (hmTmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOUSE));
						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
						slActivated.add((String) (hmTmpMap).get(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED));
					}
				}

				if ((BusinessUtil.isNotNullOrEmpty(sStatus) && pgV3Constants.RANGE_ATTRIBUTE_STATUS_PLANNING.equalsIgnoreCase(sStatus)) || (BusinessUtil.isNotNullOrEmpty(sStage) && pgV3Constants.RANGE_ATTRIBUTE_STATUS_PLANNING.equalsIgnoreCase(sStage))) {
					saReturn = new String[1];
					saReturn[0] = pgV3Constants.DUMMY_PLANT_VALUE_3151;

				} else {
					//Modified by DSM(Sogeti) - for 2015x.5 BOM eDelivery Defect# 15635 -- Starts
					//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
					if (BusinessUtil.isNotNullOrEmpty(slPlants)) {
						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
						//Modified by DSM(Sogeti) - for 2015x.5 BOM eDelivery Defect# 15635 -- Ends

						Iterator itrPlants = slPlants.iterator();
						Iterator itrAuthorized = slAuthorized.iterator();
						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
						Iterator itrAuthorizedToUse = slAuthorizedToUse.iterator();
						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
						Iterator itrActivated = slActivated.iterator();

						while (itrPlants.hasNext()) {
							sPlant = (String) itrPlants.next();
							sAuthorized = (String) itrAuthorized.next();
							//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
							sAuthorizedToUse = (String) itrAuthorizedToUse.next();
							//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
							sActivated = (String) itrActivated.next();
							if (BusinessUtil.isNotNullOrEmpty(sPlant) && sPlant.indexOf(pgV3Constants.SYMBOL_TILDA) >= 0) {
								//Modified by DSM(Sogeti) - for 2015x.5 BOM eDelivery Defect# 15635 -- Starts
								strFinalPlantName = sPlant.substring(sPlant.indexOf(pgV3Constants.SYMBOL_TILDA) + 1);
								//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
								if (null != sDesSitesCurrent && !pgV3Constants.ATTRIBUTE_CURRENT_INACTIVE.equalsIgnoreCase(sDesSitesCurrent) && BusinessUtil.isNotNullOrEmpty(sSiteListInConfig)) {
									if (pgV3Constants.KEY_TRUE.equalsIgnoreCase(sAuthorized) && sSiteListInConfig.indexOf(strFinalPlantName) >= 0) {
										strPlantNameWithTableParam = strFinalPlantName.concat("|AP");
										slPlantsData.add(strPlantNameWithTableParam);
										//Modified by DSM(Sogeti) - for 2015x.5 BOM eDelivery Defect# 15635 -- Ends
									}
								}
								//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
								//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
								if (null != sEMEASitesCurrent && !pgV3Constants.ATTRIBUTE_CURRENT_INACTIVE.equalsIgnoreCase(sEMEASitesCurrent) && BusinessUtil.isNotNullOrEmpty(sEMEASiteListInConfig)) {
									if (pgV3Constants.KEY_TRUE.equalsIgnoreCase(sAuthorizedToUse) && sEMEASiteListInConfig.indexOf(strFinalPlantName) >= 0) {
										strPlantNameWithTableParam = strFinalPlantName.concat("|AU");
										slPlantsData.add(strPlantNameWithTableParam);
									}
								}
								//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
							}
						}

						if (null != slPlantsData && slPlantsData.size() > 0) {

							saReturn = new String[slPlantsData.size()];
							Iterator itrAuthPlants = slPlantsData.iterator();

							int i = 0;
							while (itrAuthPlants.hasNext()) {
								String sTemp = (String) itrAuthPlants.next();
								saReturn[i] = sTemp;
								if (itrAuthPlants.hasNext())
									i++;
							}
						}
					}
				}

				// No need to check null for DomainObject, it will throw Framework Exception if no TNR found in Enovia DB
				//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
				if ((null == sDesSitesCurrent || pgV3Constants.ATTRIBUTE_CURRENT_INACTIVE.equalsIgnoreCase(sDesSitesCurrent)) && (null == sEMEASitesCurrent || pgV3Constants.ATTRIBUTE_CURRENT_INACTIVE.equalsIgnoreCase(sEMEASiteListInConfig))) {
					//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
					saReturn = new String[1];
					saReturn[0] = pgV3Constants.DUMMY_PLANT_VALUE_3151;
				}
			}
		} catch (Exception ex) {

			ex.printStackTrace();
			//throw new Exception(ex.getMessage());
		}
		return saReturn;
	}

	/****
	 *
	 * @param String,String,String,String,String,String,String,String,String,String,String,String,String,String,String
	 *
	 * return String Array
	 */
	//Modified by DSM - for 2018x.1 requirement #25043 - Starts
	public String[] buildBOMArray(String strParentName, String strFindNumber,
			String strEffectivityDateChild,
			String sComponentName, String sQuantity,
			String strMax, String sTarget, String strMin,
			String strEffectivDateParent,
			String sBOMBaseQuantity, String strPosInd, String strFILBaseQty, String strpgPhaseBOMQty, String strSubFlag,
			String strID, String strOptComponent, String strComment, String strTargetWetWeight, String strTargetDryWeight, String strIsPrimaryComponent, String strValidUntilDate) {
		//Modified by DSM - for 2018x.1 requirement #25043 - Ends
		String[] saTemp = new String[pgV3Constants.INDEX_BOM_ARRAY_SIZE];

		try {
			//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Starts
			String sFinalQuantity = DomainConstants.EMPTY_STRING;
			//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Ends
			if (BusinessUtil.isNullOrEmpty(sQuantity))
				sQuantity = "1";
			if (BusinessUtil.isNullOrEmpty(strMax))
				strMax = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
			if (BusinessUtil.isNullOrEmpty(strMin))
				strMin = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
			if (BusinessUtil.isNullOrEmpty(sBOMBaseQuantity))
				sBOMBaseQuantity = "1";
			if (BusinessUtil.isNullOrEmpty(strFILBaseQty))
				strFILBaseQty = "1";
			if (BusinessUtil.isNullOrEmpty(strpgPhaseBOMQty))
				strpgPhaseBOMQty = "1";
			if (BusinessUtil.isNullOrEmpty(sTarget))
				sTarget = "1";

			double dFil = 1;
			double dMinQty = 1;
			double dPgPhaseBOMQty = 1;
			double dBOMBaseQty = 1;
			double dTargetQty = 1;
			double dMaxQty = 1;
			double dFilBaseQty = 1;
			double dPCTBase = 1;
			double dQuantity = 1;
			double dFormula_Card_Minimum_Quantity = parseValue(strMin);
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Starts
			if (dFormula_Card_Minimum_Quantity <= 0) {
				dFormula_Card_Minimum_Quantity = -9.99;
			}
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Ends
			double dFormula_Card_Maximum_Quantity = parseValue(strMax);
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Starts
			if (dFormula_Card_Maximum_Quantity <= 0) {
				dFormula_Card_Maximum_Quantity = -9.99;
			}
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Ends
			double dFormula_Card = 1;
			double dIPS_Quantity = 1;

			dMinQty = parseValue(strMin);
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Starts
			if (dMinQty <= 0) {
				dMinQty = -9.99;
			}
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Ends
			dPgPhaseBOMQty = parseValue(strpgPhaseBOMQty);
			dBOMBaseQty = parseValue(sBOMBaseQuantity);
			dTargetQty = parseValue(sTarget);
			dMaxQty = parseValue(strMax);
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Starts
			if (dMaxQty <= 0) {
				dMaxQty = -9.99;
			}
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Ends
			dFilBaseQty = parseValue(parseQuantityValue(strFILBaseQty));
			dPCTBase = dFilBaseQty;
			dQuantity = parseValue(parseQuantityValue(sQuantity));
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Starts
			if (dQuantity <= 0) {
				dQuantity = -9.99;
			} else {
				//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Starts
				BigDecimal bdQuantity = new BigDecimal(sQuantity);
				sFinalQuantity = bdQuantity.toPlainString();
				//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Ends
			}
			//Added by DSM(Sogeti) - for BOM eDelivery 2018x.3 Defect #30035 - Ends

			strMin = dMinQty + DomainConstants.EMPTY_STRING;
			strpgPhaseBOMQty = dPgPhaseBOMQty + DomainConstants.EMPTY_STRING;
			sBOMBaseQuantity = dBOMBaseQty + DomainConstants.EMPTY_STRING;
			sTarget = dTargetQty + DomainConstants.EMPTY_STRING;
			strMax = dMaxQty + DomainConstants.EMPTY_STRING;
			strFILBaseQty = dFilBaseQty + DomainConstants.EMPTY_STRING;
			//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Starts
			if (dQuantity <= 0) {
				sQuantity = dQuantity + DomainConstants.EMPTY_STRING;
			} else {
				sQuantity = sFinalQuantity;
			}
			//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Ends
			if (BusinessUtil.isNotNullOrEmpty(strEffectivityDateChild))
				strEffectivityDateChild = convertPLMDateToSAP(strEffectivityDateChild);

			//Added by DSM - for 2018x.1 requirement #25043 - Starts
			if (BusinessUtil.isNotNullOrEmpty(strValidUntilDate))
				strValidUntilDate = convertPLMDateToSAP(strValidUntilDate);
			//Added by DSM - for 2018x.1 requirement #25043 - Ends

			if (BusinessUtil.isNotNullOrEmpty(strEffectivDateParent))
				strEffectivDateParent = convertPLMDateToSAP(strEffectivDateParent);

			if (BusinessUtil.isNotNullOrEmpty(sTarget) && !pgV3Constants.STR_DEFAULT_QTY_IF_FAILED.equals(sTarget)) {
				dFormula_Card = dBOMBaseQty / (dFilBaseQty * dTargetQty);
				dFormula_Card = (double) Math.round(dFormula_Card * 1000) / 1000;
			} else {

				dFormula_Card = parseValue(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);
			}

			if (BusinessUtil.isNotNullOrEmpty(sQuantity) && !pgV3Constants.STR_DEFAULT_QTY_IF_FAILED.equals(sQuantity)) {
				dIPS_Quantity = dBOMBaseQty / (dPCTBase * dQuantity);
				dIPS_Quantity = (double) Math.round(dIPS_Quantity * 1000) / 1000;
			} else {
				dIPS_Quantity = parseValue(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);
			}

			if (UIUtil.isNotNullAndNotEmpty(strSubFlag)) {
				saTemp[pgV3Constants.INDEX_USAGE_PROBABILITY] = pgV3Constants.VALUE_USAGE_PROBABILITY;
			} else {
				saTemp[pgV3Constants.INDEX_USAGE_PROBABILITY] = "";
			}

			saTemp[pgV3Constants.INDEX_ALT_BOM] = pgV3Constants.VALUE_ALT_BOM;
			saTemp[pgV3Constants.INDEX_BOM_STATUS] = pgV3Constants.VALUE_BOM_STATUS;
			saTemp[pgV3Constants.INDEX_ITEM_CATEGORY] = pgV3Constants.VALUE_ITEM_CATEGORY;
			saTemp[pgV3Constants.INDEX_BOM_TEXT] = strParentName;
			saTemp[pgV3Constants.INDEX_ALT_BOM_TEXT] = pgV3Constants.VALUE_ALT_BOM_TEXT;
			saTemp[pgV3Constants.INDEX_MATERIAL_NUMBER] = strParentName;
			saTemp[pgV3Constants.INDEX_BOM_USAGE] = pgV3Constants.VALID_CODE;
			saTemp[pgV3Constants.INDEX_VALID_FROM_DATE_PARENT] = strEffectivDateParent;
			saTemp[pgV3Constants.INDEX_COMPONENT_NAME] = sComponentName;
			saTemp[pgV3Constants.INDEX_QUANTITY] = sQuantity;
			saTemp[pgV3Constants.INDEX_UPPER_LIMIT] = strMax;
			saTemp[pgV3Constants.INDEX_TARGET] = sQuantity;
			saTemp[pgV3Constants.INDEX_LOWER_LIMIT] = strMin;
			saTemp[pgV3Constants.INDEX_FIND_NUMBER] = strFindNumber;
			saTemp[pgV3Constants.INDEX_VALID_FROM_DATE_CHILD] = strEffectivityDateChild;
			//Added by DSM - for 2018x.1 requirement #25043 - Starts
			saTemp[pgV3Constants.INDEX_VALID_UNTIL_DATE] = strValidUntilDate;
			//Added by DSM - for 2018x.1 requirement #25043 - Ends
			saTemp[pgV3Constants.INDEX_PHASE_NAME] = DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_CONFIRMED_QUANTITY] = sBOMBaseQuantity;
			saTemp[pgV3Constants.INDEX_SORT_STRING] = strPosInd;
			saTemp[pgV3Constants.INDEX_BOM_ITEM_NUMBER] = DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_SUBSTITUTE_FLAG] = strSubFlag;
			saTemp[pgV3Constants.INDEX_FC_MIN_QTY] = dFormula_Card_Minimum_Quantity + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_FORMULA_CARD] = dFormula_Card + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_FC_MAX_QTY] = dFormula_Card_Maximum_Quantity + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_IPS_QTY] = dIPS_Quantity + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_OPT_COMPONENT] = strOptComponent;
			saTemp[pgV3Constants.INDEX_COMMENT] = strComment;
			saTemp[pgV3Constants.INDEX_TARGETWETWEIGHT] = strTargetWetWeight;
			saTemp[pgV3Constants.INDEX_TARGETDRYWEIGHT] = strTargetDryWeight;
			saTemp[pgV3Constants.INDEX_ISPRIMARYCOMPONENT] = strIsPrimaryComponent;
			saTemp[pgV3Constants.INDEX_OBJECT_ID] = strID;

			if (saTemp[pgV3Constants.INDEX_UPPER_LIMIT] == null || saTemp[pgV3Constants.INDEX_UPPER_LIMIT].equals("")) {
				saTemp[pgV3Constants.INDEX_RANGE_FLAG] = DomainConstants.EMPTY_STRING;
			} else
				saTemp[pgV3Constants.INDEX_RANGE_FLAG] = pgV3Constants.VALUE_RANGE_FLAG;


		} catch (Exception e) {
			e.printStackTrace();
			//Not throwing exception here as the calling method is expecting String Array.
			//In this case Blank String array would be returned which is being handled properly by calling method.
			//throw e;
		}
		return saTemp;

	}

	private double parseValue(String strValue) {

		double dValue = 1;
		try {
			dValue = Double.parseDouble(strValue);
		} catch (Exception e) {
			dValue = 1;
		}
		if (pgV3Constants.STR_DEFAULT_QTY_IF_FAILED.equalsIgnoreCase(strValue)) {
			dValue = -9.99;
		}
		dValue = (double) Math.round(dValue * 1000) / 1000;
		return dValue;

	}

	private String parseQuantityValue(String strValue) {

		double dValue = -9.99;
		try {

			if (BusinessUtil.isNotNullOrEmpty(strValue)) {
				dValue = Double.parseDouble(strValue);

				if (dValue < 0) {

					strValue = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
					dValue = -9.99;
				}

			} else {
				strValue = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
				dValue = -9.99;
			}
		} catch (Exception e) {
			dValue = -9.99;
		}
		if (pgV3Constants.STR_DEFAULT_QTY_IF_FAILED.equalsIgnoreCase(strValue)) {
			dValue = -9.99;
		}

		return dValue + DomainConstants.EMPTY_STRING;


	}

	private double parseFOPQuantityValue(String strValue) {

		double dValue = 1;
		if (pgV3Constants.STR_DEFAULT_QTY_IF_FAILED.equalsIgnoreCase(strValue)) {
			dValue = -9.99;
		} else {
			try {
				dValue = Double.parseDouble(strValue);

			} catch (Exception e) {
				dValue = 1;
			}
		}
		return dValue;
	}

	/**
	 * This Method is use for Global Object selectables
	 *
	 * @return StringList
	 * @throws Exception
	 */
	public static StringList getObjectSelects() {
		StringList slEBOMTarget = new StringList(16);
		try {


			slEBOMTarget.add(DomainConstants.SELECT_TYPE);
			slEBOMTarget.add(DomainConstants.SELECT_NAME);
			slEBOMTarget.add(DomainConstants.SELECT_REVISION);
			slEBOMTarget.add(DomainConstants.SELECT_ID);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_ID);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_TYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_NAME);
			slEBOMTarget.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]");
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
			slEBOMTarget.add("from[" + pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION + "].to.name");
			slEBOMTarget.add("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.name");
			slEBOMTarget.addElement(DomainConstants.SELECT_LEVEL);
			//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
			//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return slEBOMTarget;
	}

	/**
	 * This Method is use for Global Object selectables
	 *
	 * @return StringList
	 * @throws Exception
	 */

	public static StringList getFOPObjectSelects() {

		StringList slEBOMTarget = new StringList(25);
		try {

			slEBOMTarget.add(DomainConstants.SELECT_TYPE);
			slEBOMTarget.add(DomainConstants.SELECT_NAME);
			slEBOMTarget.add(DomainConstants.SELECT_REVISION);
			slEBOMTarget.add(DomainConstants.SELECT_ID);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_WEIGHT_DRY);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_PERCENT_DRY);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_WEIGHT_WET);
			slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_PERCENT_WET);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_ID);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_NAME);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_TYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_EFFECTIVITY_DATE);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_BASE_UOM);
			//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Starts
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_PGASSEMBLYTYPE);
			slEBOMTarget.add(pgV3Constants.SELECT_ALTERNATE_PGSAPTYPE);
			//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Ends
			slEBOMTarget.add("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from." + pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return slEBOMTarget;
	}

	/**
	 * This Method is use for Relationship selectables
	 *
	 * @return StringList
	 */


	public static StringList getRelationshipSelectsEBOM() {
		StringList slEBOMRel = new StringList(33);
		try {

			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
			slEBOMRel.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
			slEBOMRel.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]");
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PG_BOM_BASEQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MIN_QUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MAX_QUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
			//Added by DSM - for 2018x.1 requirement #25043 - Starts
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_START_EFFECTIVITY);
			slEBOMRel.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
			//Added by DSM - for 2018x.1 requirement #25043 - Ends

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return slEBOMRel;
	}


	/**
	 * This Method is use for Relationship selectables
	 *
	 * @return StringList
	 */


	public static StringList getRelationshipSelectsOthers() {
		StringList slEBOMRel = new StringList(25);
		try {
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
			slEBOMRel.add(DomainConstants.SELECT_RELATIONSHIP_ID);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINIMUMPERCENTWEIGHTBYWEIGHT);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXIMUMPERCENTWEIGHTBYWEIGHT);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGISAUTHORIZEDTOPRODUCE);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_PGISACTIVATED);
			slEBOMRel.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_TYPE);
			slEBOMRel.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_NAME);
			slEBOMRel.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_ID);
			slEBOMRel.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_REL_ID);
			slEBOMRel.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_BASE_UOM);
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
			slEBOMRel.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_EFFECTIVITY_DATE);
			slEBOMRel.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_FORMULATION_TYPE);
			//Modified by DSM Sogeti for 2018x.5 Defect #30820 - Starts
			slEBOMRel.add(pgV3Constants.SELECT_ATTRIBUTE_VIRTUALINTERMEDIATEPHYSICALID);
			//Modified by DSM Sogeti for 2018x.5 Defect #30820 - Ends
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return slEBOMRel;
	}


	/**
	 * converts ArrayList To Array
	 *
	 * @param ArrayList
	 * @param int
	 * @return String[][]
	 */
	private String[][] convertArrayListToArray(ArrayList alGeneric, int iArraySize) throws Exception {
		String[][] saReturn = new String[1][1];
		try {
			if (alGeneric != null) {
				Iterator itr = alGeneric.iterator();

				saReturn = new String[alGeneric.size()][iArraySize];
				int i = 0;
				while (itr.hasNext()) {
					String[] saTemp = (String[]) itr.next();
					saReturn[i] = saTemp;
					i++;
				}
			} else {
				saReturn = new String[1][1];
			}
		} catch (Exception e) {
			e.printStackTrace();
			saReturn = new String[1][1];
		}

		return saReturn;
	}

	/**
	 * Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)
	 * pushes data to SAP system
	 * Returns Success or failure message.
	 *
	 * @param String[][], String[],String,String,String,String,String,String
	 */

	private synchronized String sendDataToSAP(String[][] saBOMData, String[] saPlants, String sSAP_Client,
			String sSAP_Userid, String sSAP_Password, String sSAP_Language, String sSAP_HostName,
			String sSAP_SystemNumber, boolean inUse) throws Exception {

		JCO.Function function;
		IRepository repository;
		// Method taken from Bean, earlier being used for CSS to SAP push.
		JCO.Client client = null;
		String strMessage = "";
		boolean bClientOpen = false;
		String strChildEffecDate = "";
		String strParentEffecDate = "";
		//Added by DSM - for 2018x.1 requirement #25043 - Starts
		String strValidUntilDate = "";
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
		String strPlantName = DomainConstants.EMPTY_STRING;
		String strTableParameterCheck = DomainConstants.EMPTY_STRING;
		String[] arrPlantDetails = null;
		//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends

		//Added by DSM - for 2018x.1 requirement #25043 - Ends
		try {

			client = JCO.createClient(sSAP_Client, sSAP_Userid,
					sSAP_Password, sSAP_Language, sSAP_HostName,
					sSAP_SystemNumber);
			client.connect();
			strMessage = strSuccess;
			bClientOpen = true;
			System.out.println("*********** Enovia system got connected to SAP ********************");
			repository = new JCO.Repository("RDB RFC Repository", client);

			function = repository.getFunctionTemplate("ZT_RFC_BOM_CREATE_PLM").getFunction();


			if (function != null) {

				ParameterList plTemp = function.getImportParameterList();

				Table tTemp = plTemp.getTable("CT_BOM_DATA");
				//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
				Table tTempAPPlants = plTemp.getTable("CT_BOM_PLANT");
				Table tTempAUPlants = plTemp.getTable("CT_BOM_PLANT_2");
				//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
				if (saBOMData != null && saBOMData.length > 0) {

					for (int i = 0; i < saBOMData.length; i++) {

						tTemp.appendRow();

						String strSort = saBOMData[i][pgV3Constants.INDEX_SORT_STRING];

						if (strSort == null || strSort.equalsIgnoreCase("Null")) {

							strSort = DomainConstants.EMPTY_STRING;

						}

						String strUpperLimit = saBOMData[i][pgV3Constants.INDEX_UPPER_LIMIT];

						if (strUpperLimit == null || strUpperLimit.equalsIgnoreCase("Null")) {

							strUpperLimit = DomainConstants.EMPTY_STRING;

						}

						String strLowerLimit = saBOMData[i][pgV3Constants.INDEX_LOWER_LIMIT];

						if (strLowerLimit == null || strLowerLimit.equalsIgnoreCase("Null")) {

							strLowerLimit = DomainConstants.EMPTY_STRING;

						}
						tTemp.setValue("0000000000" + saBOMData[i][pgV3Constants.INDEX_MATERIAL_NUMBER], "MATNR"); //Material Number
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_BOM_USAGE], "STLAN"); // BOM Usage
						strParentEffecDate = saBOMData[i][pgV3Constants.INDEX_VALID_FROM_DATE_PARENT];
						if (BusinessUtil.isNotNullOrEmpty(strParentEffecDate)) {

							tTemp.setValue(strParentEffecDate, "DATUV"); // Parent Effective Date

						}
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_ALT_BOM], "STLAL"); // ALT BOM
						tTemp.setValue("", "WERKS"); // PLANT -- not used
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_BOM_TEXT], "STKTX"); // ALT BOM TEXT
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_ALT_BOM_TEXT], "ZTEXT"); // ALT BOM TEXT
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_CONFIRMED_QUANTITY], "BMENG"); // BOM Base Qty
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_BOM_STATUS], "STLST"); // BOM STATUS
						tTemp.setValue("0000000000" + saBOMData[i][pgV3Constants.INDEX_COMPONENT_NAME], "IDNRK"); // Comp Name

						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_ITEM_CATEGORY], "POSTP"); // Item Category
						tTemp.setValue((i + 1) * 10, "POSNR"); // Incrementing Number

						strChildEffecDate = saBOMData[i][pgV3Constants.INDEX_VALID_FROM_DATE_CHILD];

						if (BusinessUtil.isNotNullOrEmpty(strChildEffecDate)) {
							tTemp.setValue(strChildEffecDate, "DATUV_I"); // Child Effective Date
						}

						//Added by DSM - for 2018x.1 requirement #25043 - Starts
						/*strValidUntilDate = saBOMData[i][pgV3Constants.INDEX_VALID_UNTIL_DATE];

						if (BusinessUtil.isNotNullOrEmpty(strValidUntilDate)) {
						tTemp.setValue(strValidUntilDate, "XXXXX"); // Valid until Date
						}*/
						//Added by DSM - for 2018x.1 requirement #25043 - Ends

						tTemp.setValue(strSort, "SORTF"); // Position Indicator
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_SUBSTITUTE_FLAG], "ALPGR"); // Alt Item Group -- Substitute combination numbers AA-ZZ
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_USAGE_PROBABILITY], "EWAHR"); // Usage Probability
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_RANGE_FLAG], "ZRANGE_FLAG"); // Range Flag
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_TARGET], "MENGE"); // Component Quantity -- BOM Target?
						tTemp.setValue(strUpperLimit, "ZUPPER_LIMIT"); // Upper Limit -- Max?
						tTemp.setValue(saBOMData[i][pgV3Constants.INDEX_QUANTITY], "ZTARGET_LIMIT"); // Target --- Target?
						tTemp.setValue(strLowerLimit, "ZLOWER_LIMIT"); // Lower Limit -- Min?
					}

					//Make sure to use the correct material number (may be gcas, may be FPC)
					//Material number column from BOM data is the same for every row
					String sMatNumber = "0000000000" + saBOMData[0][pgV3Constants.INDEX_MATERIAL_NUMBER];
					String saTempPlant = DomainConstants.EMPTY_STRING;
					for (int i = 0; i < saPlants.length; i++) {
						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
						arrPlantDetails = saPlants[i].split(pgV3Constants.PLANTS_DELIMITER);
						if (arrPlantDetails != null) {
							strPlantName = arrPlantDetails[0];
							strTableParameterCheck = arrPlantDetails[1];

							if (UIUtil.isNotNullAndNotEmpty(strPlantName) && UIUtil.isNotNullAndNotEmpty(strTableParameterCheck)) {

								if (strTableParameterCheck.equalsIgnoreCase(pgV3Constants.TABLE_PARAM_CHECK_AU)) {

									tTempAUPlants.appendRow();
									tTempAUPlants.setValue(sMatNumber, "MATNR");
									tTempAUPlants.setValue(strPlantName, "WERKS");
								} else if (strTableParameterCheck.equalsIgnoreCase(pgV3Constants.TABLE_PARAM_CHECK_AP)) {

									tTempAPPlants.appendRow();
									tTempAPPlants.setValue(sMatNumber, "MATNR");
									tTempAPPlants.setValue(strPlantName, "WERKS");
								}
							}
						}

						//Modified by DSM Sogeti for 2018x.5 Requirement #33310 - Ends
						/*
						//Only send plants that are in the designated Sites list,
						//if the Designated Sites list is active
						saTempPlant= saPlants[i]+"|";


						if (bSitesListActive) {
							if (strDesignatedSites.length()>0) {
								if (strDesignatedSites.indexOf(saTempPlant) > -1) {

									tTempPlants.appendRow();
									tTempPlants.setValue(sMatNumber,"MATNR");
									tTempPlants.setValue(saPlants[i], "WERKS");
								}
							} else {

								tTempPlants.appendRow();
								tTempPlants.setValue(sMatNumber,"MATNR");
								tTempPlants.setValue(saPlants[i], "WERKS");
							}
						} else {
							tTempPlants.appendRow();
							tTempPlants.setValue(sMatNumber,"MATNR");
							tTempPlants.setValue(saPlants[i], "WERKS");
						} */

					}

					if (bExecuteRFC) {
						System.out.println("*********** Enovia to SAP PUSH - Starts ********************");
						client.execute(function);
						ParameterList plResponse = function.getExportParameterList();
						int i = plResponse.getFieldCount();
						Structure sReturn = plResponse.getField("EW_RETURN").getStructure();
						Field fRetMessage = sReturn.getField("MESSAGE");
						String _Response = "" + fRetMessage.getValue();
						System.out.println(" Message from SAP :" + _Response);
						System.out.println();
						System.out.println("*********** Enovia to SAP PUSH - Ends ********************");
						strMessage = _Response;

					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			strMessage = ex.getMessage();
			//throw new Exception(ex.getMessage());
		}
		//This should be done in finally using variable which suggests connect has happened
		finally {
			if (bClientOpen && null != client) {
				client.disconnect();
			}
		}
		return strMessage;
	}

	/**
	 * This method returns Vector of BOM quantity data for TUP Components
	 *
	 * @param context
	 * @param strObjectID:    Object Id
	 * @param mpListChildren: BOM Feed Data
	 * @return StringList of BOM Quantity
	 * @throws Exception
	 * @arg object details
	 */
	public StringList getTUPBOMQTYData(Context context, String strObjectID, MapList mpListChildren, Float fIntermediateSAPQty, String strBUOM) throws Exception {
		StringList slQtyValues = new StringList(1);
		try {
			//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Starts
			//float flMCUPQty = 0;
			BigDecimal bdCUPQty = BigDecimal.ZERO;
			//float flBOMBaseQty = 0;
			BigDecimal bdBOMBaseQty = BigDecimal.ZERO;
			BigDecimal bdIntermediateSAPQty = BigDecimal.valueOf(fIntermediateSAPQty);
			BigDecimal bdBOMQuantityCheck = new BigDecimal("9999999999.999");
			//float flNumber = 0;
			BigDecimal bdNumber = BigDecimal.ZERO;
			//float flBOMQty = 0;
			BigDecimal bdBOMQty = BigDecimal.ZERO;

			String strCUPQty = DomainConstants.EMPTY_STRING;
			String strBOMBaseQty = DomainConstants.EMPTY_STRING;
			String strTUPID = DomainConstants.EMPTY_STRING;
			String strRelWhere = new StringBuffer(pgV3Constants.SELECT_ATTRIBUTE_PGISPRIMARY).append("==TRUE").toString();

			DomainObject dObjFPP = null;

			String strCompQty = null;
			String strBOMQty = null;
			String strConnectionID = null;

			Map mapTemp = null;

			MapList mlPart = new MapList();
			MapList mlTUPChildData = new MapList();

			StringList relationshipSelects = new StringList(1);
			relationshipSelects.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
			if (BusinessUtil.isNotNullOrEmpty(strObjectID)) {

				dObjFPP = DomainObject.newInstance(context, strObjectID);
				strBOMBaseQty = dObjFPP.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);

				// Modified for both are marked as true
				mlPart = dObjFPP.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_PGTRANSPORTUNIT, // Rel Pattern
						pgV3Constants.TYPE_PGTRANSPORTUNITPART,     // Type Pattern
						SL_OBJECT_INFO_SELECT,                    // Bus select
						null,                                        // Rel select
						false,                                        // to side
						true,                                    // from side
						(short) 1,                                    // recursion level
						null,                                        // Object Where Clause
						strRelWhere, 0);                            // Rel Where Clause

				if (null != mlPart && mlPart.size() > 0) {
					Map mapTUPData = (Map) mlPart.get(0);
					strTUPID = (String) mapTUPData.get(DomainConstants.SELECT_ID);
				}
				if (BusinessUtil.isNotNullOrEmpty(strTUPID)) {
					DomainObject dObjTUP = DomainObject.newInstance(context, strTUPID);
					mlTUPChildData = dObjTUP.getRelatedObjects(context,
							pgV3Constants.RELATIONSHIP_EBOM,                // Rel Pattern
							pgV3Constants.TYPE_PGMASTERCUSTOMERUNITPART,    // Type Pattern
							null,                                            // Bus select
							relationshipSelects,                            // Rel select
							false,                                            // to side
							true,                                        // from side
							(short) 0,                                        // recursion level
							null,                                            // Object Where Clause
							null);                                        // Rel Where Clause
					if (null != mlTUPChildData && mlTUPChildData.size() > 0) {
						Map mapCUPData = (Map) mlTUPChildData.get(0);
						strCUPQty = (String) mapCUPData.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
					}
					//Converting Quantity to the float
					//coverting String To BigDecimal
					try {
						if (BusinessUtil.isNotNullOrEmpty(strBOMBaseQty)) {
							//flBOMBaseQty = (Float.valueOf(strBOMBaseQty)).floatValue();
							bdBOMBaseQty = new BigDecimal(strBOMBaseQty);
						}
						if (BusinessUtil.isNotNullOrEmpty(strCUPQty)) {
							//flMCUPQty = (Float.valueOf(strCUPQty)).floatValue();
							bdCUPQty = new BigDecimal(strCUPQty);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (null != mpListChildren) {
						int iObjSize = mpListChildren.size();

						for (int i = 0; i < iObjSize; i++) {
							if (pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM)) {
								strBOMQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
							} else {
								//flBOMQty = 0;
								bdBOMQty = BigDecimal.ZERO;
								strBOMQty = "";
								mapTemp = (Map) mpListChildren.get(i);
								strConnectionID = (String) mapTemp.get(DomainRelationship.SELECT_ID);
								strCompQty = DomainRelationship.getAttributeValue(context, strConnectionID, pgV3Constants.ATTRIBUTE_QUANTITY);

								if (BusinessUtil.isNotNullOrEmpty(strCompQty)) {
									//flNumber = (Float.valueOf(strCompQty)).floatValue();
									bdNumber = new BigDecimal(strCompQty);
								}
								//BOM Quantity Calculation
								//if(flMCUPQty != 0)
								if (bdCUPQty.compareTo(BigDecimal.ZERO) != 0)
									//flBOMQty = (flBOMBaseQty / flMCUPQty) * flNumber;
									//Modified by DSM-2018x.6(Sogeti) JAN CW for Defect_44956 -Starts
									bdBOMQty = bdBOMBaseQty.divide(bdCUPQty, 16, RoundingMode.HALF_UP);
								//Modified by DSM-2018x.6(Sogeti) JAN CW for Defect_44956 -Ends
								bdBOMQty = bdBOMQty.multiply(bdNumber);
								//if(fIntermediateSAPQty > 0)
								//flBOMQty = flBOMQty / fIntermediateSAPQty;
								//Modified by DSM-2018x.6(Sogeti) JAN CW for Defect_44956 -Starts
								bdBOMQty = bdBOMQty.divide(bdIntermediateSAPQty, 16, RoundingMode.HALF_UP);
								bdBOMQty = bdBOMQty.setScale(3, RoundingMode.HALF_UP);
								//Modified by DSM-2018x.6(Sogeti) JAN CW for Defect_44956 -Ends
								//Convert this float to String
								//strBOMQty = String.valueOf(flBOMQty);
								strBOMQty = String.valueOf(bdBOMQty);
								//if(flBOMQty == 0 || flBOMQty > 9999999999.999) {
								if ((bdBOMQty.compareTo(BigDecimal.ZERO) == 0) || (bdBOMQty.compareTo(bdBOMQuantityCheck) > 0)) {
									strBOMQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
								}
								//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Ends
							}
							slQtyValues.add(strBOMQty);
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		return slQtyValues;
	}

	/**
	 * This method returns Maplist of BOM Component for SAP BOM FEED
	 * Gets EBOM, Substitute and Alternates of TUP, APP and DPP.
	 *
	 * @param context
	 * @param strObjectID: Object Id
	 * @return Maplist of BOM FEED
	 * @throws Exception
	 * @arg object details
	 */
	public MapList getPartBOMFEEDData(Context context, String strObjectID, String strTopNodeType) throws Exception {

		MapList mlFlatBOM = new MapList();
		String strTUPID = DomainConstants.EMPTY_STRING;
		String strPartType = DomainConstants.EMPTY_STRING;
		String strRelWhere = DomainConstants.EMPTY_STRING;

		Map mapSub = null;
		Map mpAlternate = null;
		Map mpCompPart = null;

		MapList mlTUPPart = null;
		MapList mlPartChildData = null;

		Object substituteId = null;
		Object substituteName = null;
		Object substituteType = null;
		Object substituteRev = null;
		Object substituteState = null;
		Object substituteEffectivityDate = null;
		Object SubstituteValidUntilDate = null;
		Object substituteOptComponent = null;
		Object substituteRelID = null;
		Object substituteComment = null;
		Object substituteSpecSubType = null;
		//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Starts
		Object substituteSAPType = null;
		//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Ends
		Object substituteMinQty = null;
		Object substituteMaxQty = null;
		Object substituteQty = null;
		Object oAlternateIds = null;

		MapList mlSubAlts = null;
		int iAltsSize = 0;
		Map mpAltMap = null;
		//Added by DSM(Sogeti) - 2015x.5.1 BOM eDelivery/SAP BOM as Fed Reqs 23852,23853,23854,23855 - Ends
		//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Starts
		String strNONGCASTYPE = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(), "emxCPN.SAPBOM.DSM.NONGCASTypes");
		//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Ends
		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
		String strType = null;
		String strExpandBOMonSAPBOMasFed = null;
		String strFABExpandBOMQuantity = null;
		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
		try {
			if (BusinessUtil.isNotNullOrEmpty(strObjectID)) {
				DomainObject dObjPart = DomainObject.newInstance(context, strObjectID);
				if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strTopNodeType)) {
					strRelWhere = new StringBuffer(pgV3Constants.SELECT_ATTRIBUTE_PGISPRIMARY).append("==TRUE").toString();
					mlTUPPart = dObjPart.getRelatedObjects(context,
							pgV3Constants.RELATIONSHIP_PGTRANSPORTUNIT,  // Rel Pattern
							pgV3Constants.TYPE_PGTRANSPORTUNITPART,        // Type Pattern
							SL_OBJECT_INFO_SELECT,                    // bus select
							null,                                        // Rel select
							false,                                        // to side
							true,                                        // from side
							(short) 1,                                    // recursion level
							null,                                        // Object Where Clause
							strRelWhere,                                // Rel Where Clause
							0);
					if (null != mlTUPPart && mlTUPPart.size() > 0) {
						Map mapTUPData = (Map) mlTUPPart.get(0);
						strTUPID = (String) mapTUPData.get(DomainConstants.SELECT_ID);
					}
					//To get the child of TUP
					mlPartChildData = getTUPBOMData(context, strTUPID);
				} else if (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strTopNodeType) || pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strTopNodeType) || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(strTopNodeType) || TYPE_INTERMEDIATEPRODUCTPART.equalsIgnoreCase(strTopNodeType)) {
					//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
					mlPartChildData = getEBOMData(context, strObjectID, strTopNodeType);
					//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
					//To get the child of FAB, APP and DPP
				}
				if (null != mlPartChildData && mlPartChildData.size() > 0) {
					mlPartChildData.sort(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, "ascending", "integer");
					int isizePartData = mlPartChildData.size();
					StringList slSubstituteIds = null;
					for (int iCount = 0; iCount < isizePartData; iCount++) {
						mpCompPart = (Map) mlPartChildData.get(iCount);
						(mpCompPart).put("IsDirectComponent", "true");
						(mpCompPart).put("TUP_ID", strTUPID);
						//Adding the Primary Part
						mlFlatBOM.add(mpCompPart);
						//Include the Substitute of Component
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
						strType = (String) (mpCompPart).get("Type");
						strExpandBOMonSAPBOMasFed = (String) (mpCompPart).get("FAB_ExpandBOMonSAPBOMasFed");
						strFABExpandBOMQuantity = (String) (mpCompPart).get("FAB_ExpandBOMQuantity");
					
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
						substituteId = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
						slSubstituteIds = new StringList();
						if (null != substituteId) {
							//Refactoring - InstanceList - 34
							slSubstituteIds = getInstanceList(substituteId);
						}
						substituteName = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
						StringList slSubstituteNames = new StringList();
						if (null != substituteName) {
							//Refactoring - InstanceList - 35
							slSubstituteNames = getInstanceList(substituteName);
						}
						substituteType = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
						StringList slSubstituteTypes = new StringList();
						if (null != substituteType) {
							//Refactoring - InstanceList - 36
							slSubstituteTypes = getInstanceList(substituteType);
						}
						substituteRev = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
						StringList slSubstituteRevs = new StringList();
						if (null != substituteRev) {
							//Refactoring - InstanceList - 37
							slSubstituteRevs = getInstanceList(substituteRev);
						}
						substituteState = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
						StringList slSubstituteCurrent = new StringList();
						if (null != substituteState) {
							//Refactoring - InstanceList - 38
							slSubstituteCurrent = getInstanceList(substituteState);
						}
						//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Starts
						//Modified START_EFFECTIVITY to EFFECTIVITY_DATE
						substituteEffectivityDate = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
						//Modified by DSM(Sogeti) for 2018x.2 BOM eDelivery Defect #28078  - Ends
						StringList slSubstituteEffectivityDate = new StringList();
						if (null != substituteEffectivityDate) {
							//Refactoring - InstanceList - 39
							slSubstituteEffectivityDate = getInstanceList(substituteEffectivityDate);
						}

						//Added by DSM - for 2018x.1 requirement #25043 - Starts
						SubstituteValidUntilDate = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
						StringList slSubstituteValidUntilDate = new StringList();
						if (null != SubstituteValidUntilDate) {
							//Refactoring - InstanceList - 40
							slSubstituteValidUntilDate = getInstanceList(SubstituteValidUntilDate);
						}
						//Added by DSM - for 2018x.1 requirement #25043 - Ends

						substituteOptComponent = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
						StringList slSubstituteOptComponent = new StringList();
						if (null != substituteOptComponent) {
							//Refactoring - InstanceList - 41
							slSubstituteOptComponent = getInstanceList(substituteOptComponent);
						}
						//Added by DSM(Sogeti) - for 2015x.4 SAP BOM as Fed requirement 14871 - Ends

						substituteRelID = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
						StringList slSubstituteRelID = new StringList();
						if (null != substituteRelID) {
							//Refactoring - InstanceList - 42
							slSubstituteRelID = getInstanceList(substituteRelID);
						}
						//Added by DSM(Sogeti) - for 2015x.5 SAP BOM as Fed Defect# 14537 -- Starts
						substituteComment = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
						StringList slSubstituteComment = new StringList();
						if (null != substituteComment) {
							//Refactoring - InstanceList - 43
							slSubstituteComment = getInstanceList(substituteComment);
						} else {

							int isizeOfSubstituteComponents = slSubstituteRelID.size();
							for (int iSubstitute = 0; iSubstitute < isizeOfSubstituteComponents; iSubstitute++) {
								String strSubstituteRelId = (String) slSubstituteRelID.get(iSubstitute);
								String strComment = DomainRelationship.getAttributeValue(context, strSubstituteRelId, pgV3Constants.ATTRIBUTE_COMMENT);
								if (BusinessUtil.isNullOrEmpty(strComment)) {
									slSubstituteComment.add(DomainConstants.EMPTY_STRING);
								}
							}
						}

						substituteSpecSubType = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
						StringList slSubSpecSubTypes = new StringList();
						if (null != substituteSpecSubType) {
							//Refactoring - InstanceList - 44
							slSubSpecSubTypes = getInstanceList(substituteSpecSubType);
						}
						String strSpecSubType = DomainConstants.EMPTY_STRING;

						//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Starts
						substituteSAPType = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
						StringList slSubSAPTypes = new StringList();
						if (null != substituteSAPType) {
							//Refactoring - InstanceList - 44
							slSubSAPTypes = getInstanceList(substituteSAPType);
						}
						String strSAPType = DomainConstants.EMPTY_STRING;

						//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Ends

						substituteMinQty = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MIN_QUANTITY);
						StringList slSubstituteMinQty = new StringList();
						if (null != substituteMinQty) {
							//Refactoring - InstanceList - 45
							slSubstituteMinQty = getInstanceList(substituteMinQty);
						}
						substituteMaxQty = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MAX_QUANTITY);
						StringList slSubstituteMaxQty = new StringList();
						if (null != substituteMaxQty) {
							//Refactoring - InstanceList - 46
							slSubstituteMaxQty = getInstanceList(substituteMaxQty);
							/*
							if (substituteMaxQty instanceof StringList) {
								slSubstituteMaxQty = (StringList) substituteMaxQty;
							} else {
								slSubstituteMaxQty.add(substituteMaxQty.toString());
							} */
						}

						substituteQty = (Object) (mpCompPart).get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
						StringList slSubstituteQty = new StringList();
						if (null != substituteQty) {
							//Refactoring - InstanceList - 47
							slSubstituteQty = getInstanceList(substituteQty);
						}

						//Include the Alternates of Component
						oAlternateIds = (Object) (mpCompPart).get(pgV3Constants.SELECT_ALTERNATE_ID);
						StringList slAlternateIds = new StringList();
						if (null != oAlternateIds) {
							//Refactoring - InstanceList - 48
							slAlternateIds = getInstanceList(oAlternateIds);
						}
						for (Object oAlternateId : slAlternateIds) {
							DomainObject dobjAlternate = DomainObject.newInstance(context, (String) oAlternateId);
							mpAlternate = dobjAlternate.getInfo(context, SL_OBJECT_INFO_SELECT);
							//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Starts
							String strPriAlternateSpecSubType = (String) mpAlternate.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
							if (!(pgV3Constants.THIRD_PARTY.equalsIgnoreCase(strPriAlternateSpecSubType))) {
								//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Ends
								(mpAlternate).put(DomainRelationship.SELECT_ID, (String) mpCompPart.get(DomainConstants.SELECT_RELATIONSHIP_ID));
								(mpAlternate).put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
								(mpAlternate).put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY, (String) mpCompPart.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY));
								(mpAlternate).put("TUP_ID", strTUPID);
								// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
								// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
								if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
										UIUtil.isNotNullAndNotEmpty(strFABExpandBOMQuantity)) {
									(mpAlternate).put("Type", strType);
									(mpAlternate).put("FAB_ExpandBOMonSAPBOMasFed", strExpandBOMonSAPBOMasFed);
									(mpAlternate).put("FAB_ExpandBOMQuantity", strFABExpandBOMQuantity);
								}
								// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
								// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
								//Adding Alternate Part
								mlFlatBOM.add(mpAlternate);
							}
						}


						if (null != slSubstituteIds) {
							int isizeOfSubstitutes = slSubstituteIds.size();
							for (int iSub = 0; iSub < isizeOfSubstitutes; iSub++) {
								strSpecSubType = (String) slSubSpecSubTypes.get(iSub);
								//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Starts
								strSAPType = (String) slSubSAPTypes.get(iSub);

								String strSubstituteType = (String) slSubstituteTypes.get(iSub);
								//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
								if (!(pgV3Constants.THIRD_PARTY.equalsIgnoreCase(strSpecSubType)) && (!strSAPType.equalsIgnoreCase(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC))) {
									//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends

									//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Ends
									mapSub = new HashMap();
									String strSubstituteId = (String) slSubstituteIds.get(iSub);
									mapSub.put(DomainConstants.SELECT_NAME, (String) slSubstituteNames.get(iSub));
									mapSub.put(DomainRelationship.SELECT_ID, (String) slSubstituteRelID.get(iSub));
									//mapSub.put(DomainConstants.SELECT_ID, (String)slSubstituteIds.get(iSub));
									mapSub.put(DomainConstants.SELECT_ID, strSubstituteId);
									//mapSub.put(DomainConstants.SELECT_TYPE, (String)slSubstituteTypes.get(iSub));
									mapSub.put(DomainConstants.SELECT_TYPE, strSubstituteType);
									mapSub.put(DomainConstants.SELECT_REVISION, (String) slSubstituteRevs.get(iSub));
									mapSub.put(DomainConstants.SELECT_CURRENT, (String) slSubstituteCurrent.get(iSub));
									mapSub.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, (String) slSubstituteEffectivityDate.get(iSub));
									//Added by DSM - for 2018x.1 requirement #25043 - Starts
									mapSub.put(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE, (String) slSubstituteValidUntilDate.get(iSub));
									//Added by DSM - for 2018x.1 requirement #25043 - Ends
									mapSub.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, (String) slSubstituteOptComponent.get(iSub));
									mapSub.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT, (String) slSubstituteComment.get(iSub));
									mapSub.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
									mapSub.put("TUP_ID", strTUPID);
									//Adding the Substitute Part
									mapSub.put(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET, (String) slSubstituteMinQty.get(iSub));
									mapSub.put(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET, (String) slSubstituteMaxQty.get(iSub));
									mapSub.put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY, (String) slSubstituteQty.get(iSub));
									//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
									//COP BULK - Substitute
									if ((pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteType)) && UIUtil.isNotNullAndNotEmpty(strSpecSubType) && (pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(strSpecSubType))) {
										mlFlatBOM.addAll(getFPPObjectsForCOPBulk(context, mapSub));
									}

									if (!(pgV3Constants.THIRD_PARTY.equalsIgnoreCase(strSpecSubType)) && (!strSAPType.equalsIgnoreCase(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC)) && !(strNONGCASTYPE.contains(strSubstituteType))) {
										//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends
										// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
										// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
										if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
												UIUtil.isNotNullAndNotEmpty(strFABExpandBOMQuantity)) {
											(mapSub).put("Type", strType);
											(mapSub).put("FAB_ExpandBOMonSAPBOMasFed", strExpandBOMonSAPBOMasFed);
											(mapSub).put("FAB_ExpandBOMQuantity", strFABExpandBOMQuantity);
										}
										// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
										// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
										mlFlatBOM.add(mapSub);

										if (UIUtil.isNotNullAndNotEmpty(strSubstituteType) && ALTERNATE_ALLOWED_TYPES.contains(strSubstituteType)) {
											mlSubAlts = getAlternates(context, strSubstituteId);
											iAltsSize = mlSubAlts.size();
											for (int i = 0; i < iAltsSize; i++) {
												mpAltMap = (Map) mlSubAlts.get(i);
												//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Starts
												String strSubAltSpecSubType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
												if (!strSubAltSpecSubType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY)) {
													//Modified by DSM(Sogeti) for 2018x.3 - Defect #32605 : Ends
													mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
													mpAltMap.put(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET, (String) slSubstituteMinQty.get(iSub));
													mpAltMap.put(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET, (String) slSubstituteMaxQty.get(iSub));
													mpAltMap.put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY, (String) slSubstituteQty.get(iSub));
													mpAltMap.put("TUP_ID", strTUPID);
													// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
													// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
													if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
															UIUtil.isNotNullAndNotEmpty(strFABExpandBOMQuantity)) {
														(mpAltMap).put("Type", strType);
														(mpAltMap).put("FAB_ExpandBOMonSAPBOMasFed",strExpandBOMonSAPBOMasFed);
														(mpAltMap).put("FAB_ExpandBOMQuantity", strFABExpandBOMQuantity);
													}
													// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
													// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
													mlFlatBOM.add(mpAltMap);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		return mlFlatBOM;
	}

	/**
	 * This method returns Maplist of TUP BOM Component for SAP BOM FEED
	 *
	 * @param context
	 * @param strObjectID: Object Id
	 * @return Maplist of BOM FEED
	 * @throws Exception
	 * @arg object details
	 */
	public MapList getTUPBOMData(Context context, String strObjectID) throws Exception {
		MapList mlBOM = new MapList();
		String strObjectWhere = new StringBuffer(DomainObject.SELECT_CURRENT).append(" != ").append(pgV3Constants.STATE_PART_OBSOLETE).toString();
		DomainObject dObjTUP = null;
		Pattern pObjectType = new Pattern(pgV3Constants.TYPE_RAWMATERIALPART);
		pObjectType.addPattern(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
		pObjectType.addPattern(pgV3Constants.TYPE_PGRAWMATERIAL);
		pObjectType.addPattern(pgV3Constants.TYPE_PGPACKINGMATERIAL);
		try {
			if (BusinessUtil.isNotNullOrEmpty(strObjectID)) {
				dObjTUP = DomainObject.newInstance(context, strObjectID);
				mlBOM = dObjTUP.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_EBOM,    //relationshipPattern
						//"*", 								//typePattern
						DomainConstants.QUERY_WILDCARD,    //typePattern
						false,                                //getTo
						true,                                //getFrom
						(short) 0,                            //recurseToLevel
						SL_OBJECT_INFO_SELECT,                //objectSelects
						SL_RELATION_INFO_SELECT_EBOM,        //relationshipSelects
						strObjectWhere,                    //objectWhere
						null,                                //relationshipWhere
						null,                                //PostRelPattern
						pObjectType.getPattern(),            //PostTypePattern
						null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		return mlBOM;
	}

	/**
	 * This method returns Maplist of BOM Component for SAP BOM FEED
	 *
	 * @param context
	 * @param strObjectID: Object Id
	 * @return Maplist of BOM FEED
	 * @throws Exception
	 * @arg object details
	 */
	public MapList getEBOMData(Context context, String strObjectID, String strTopNodeType) throws Exception {
		MapList mlBOM = new MapList();
		MapList mlEBOMTemp = new MapList();
		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
		MapList mlAPPDPPCOPBulk = new MapList();
		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends

		DomainObject dObjPart = null;
		String strNONGCASTYPE = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(), "emxCPN.SAPBOM.DSM.NONGCASTypes");
		String strObjectWhere = new StringBuffer(DomainObject.SELECT_CURRENT).append(" != ").append(pgV3Constants.STATE_PART_OBSOLETE).toString();
		String strSpecSubType = DomainConstants.EMPTY_STRING;
		String strSAPType = DomainConstants.EMPTY_STRING;
		String strChildType = DomainConstants.EMPTY_STRING;
		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
		String strChildPartSpecSubType = DomainConstants.EMPTY_STRING;
		//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
		//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
		String strExpandBOMonSAPBOMasFed = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends

		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
		String strFABExpandBOMQuantity = null;
		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
		Map mpBOMData = null;

		try {
			if (BusinessUtil.isNotNullOrEmpty(strObjectID)) {
				dObjPart = DomainObject.newInstance(context, strObjectID);
				mlEBOMTemp = dObjPart.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_EBOM,    //relationshipPattern
						//pgV3Constants.SYMBOL_STAR, 			//typePattern
						DomainConstants.QUERY_WILDCARD,        //typePattern
						false,                                //getTo
						true,                                //getFrom
						(short) 1,                            //recurseToLevel
						SL_OBJECT_INFO_SELECT,                //objectSelects
						SL_RELATION_INFO_SELECT_EBOM,        //relationshipSelects
						strObjectWhere,                    //objectWhere
						null,                                //relationshipWhere
						null,                                //PostRelPattern
						null,                                //PostTypePattern
						null);
				//Code to exclude NON-GCAS Parts
				if (null != mlEBOMTemp) {
					int iMLBOMSize = mlEBOMTemp.size();
					for (int j = 0; j < iMLBOMSize; j++) {
						mpBOMData = (Map) mlEBOMTemp.get(j);
						strSAPType = (String) mpBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
						strChildType = (String) mpBOMData.get(DomainConstants.SELECT_TYPE);
						String strChildID = (String) mpBOMData.get(DomainConstants.SELECT_ID);
						//Added by DSM Sogeti for Requirement #33443 2018x.5 - Starts
						//COP BULK - BOM COmponent APP/DPP/FAB
						strChildPartSpecSubType = (String) mpBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
						if ((pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strTopNodeType) || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(strTopNodeType)) && pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strChildType) && UIUtil.isNotNullAndNotEmpty(strChildPartSpecSubType) && pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(strChildPartSpecSubType)) {

							mlAPPDPPCOPBulk = getFPPObjectsForCOPBulk(context, mpBOMData);
							if (!mlAPPDPPCOPBulk.isEmpty()) {
								mlBOM.addAll(mlAPPDPPCOPBulk);
							}
						}
						//Added by DSM Sogeti for Requirement #33443 2018x.5 - Ends
						//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
						boolean bExpandFAB = false;
						if (UIUtil.isNotNullAndNotEmpty(strChildType) && strChildType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART)) {
							strExpandBOMonSAPBOMasFed = (String) mpBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
							if (UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && strExpandBOMonSAPBOMasFed.equalsIgnoreCase("TRUE")) {
								bExpandFAB = true;
								// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
								strFABExpandBOMQuantity = (String) mpBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
								// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
							}
						}
						//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
						strSpecSubType = (String) mpBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
						if (!(strNONGCASTYPE.contains(strChildType)) && !(pgV3Constants.THIRD_PARTY).equals(strSpecSubType)) {
							//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
							if (UIUtil.isNotNullAndNotEmpty(strChildType) && !(strChildType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART) && bExpandFAB)) {
								mlBOM.add(mpBOMData);
							} else {
								dObjPart = DomainObject.newInstance(context, strChildID);
								MapList mlEBOMChildTemp = dObjPart.getRelatedObjects(context,
										pgV3Constants.RELATIONSHIP_EBOM,    //relationshipPattern
										//pgV3Constants.SYMBOL_STAR, 			//typePattern
										DomainConstants.QUERY_WILDCARD,        //typePattern
										false,                                //getTo
										true,                                //getFrom
										(short) 1,                            //recurseToLevel
										SL_OBJECT_INFO_SELECT,                //objectSelects
										SL_RELATION_INFO_SELECT_EBOM,        //relationshipSelects
										strObjectWhere,                    //objectWhere
										null,                                //relationshipWhere
										null,                                //PostRelPattern
										null,                                //PostTypePattern
										null);
								int imlEBOMChildTempSize = mlEBOMChildTemp.size();
								for (int o = 0; o < imlEBOMChildTempSize; o++) {
									Map mpBOMChildData = (Map) mlEBOMChildTemp.get(o);
									String strSAPChildType = (String) mpBOMChildData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
									String strSpecSubChildType = (String) mpBOMChildData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
									String strFABChildType = (String) mpBOMChildData.get(DomainConstants.SELECT_TYPE);
									if (!(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSAPChildType)) && !(strNONGCASTYPE.contains(strFABChildType)) && !(pgV3Constants.THIRD_PARTY).equals(strSpecSubChildType)) {
										// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
										// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
										if(UIUtil.isNotNullAndNotEmpty(strChildType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed) && 
												UIUtil.isNotNullAndNotEmpty(strFABExpandBOMQuantity)) {
											mpBOMChildData.put("Type", strChildType);
											mpBOMChildData.put("FAB_ExpandBOMonSAPBOMasFed", strExpandBOMonSAPBOMasFed);
											mpBOMChildData.put("FAB_ExpandBOMQuantity", strFABExpandBOMQuantity);
										}
										// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
										// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
										mlBOM.add(mpBOMChildData);
									}
								}

							}
							//Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return mlBOM;
	}

	/**
	 * Gets EBOM, Substitutes and Alternate of Fabricated Part, Assembled Product Part and Devise Product Part.
	 *
	 * @param context
	 * @param strObjectID: Object Id
	 * @return Maplist of BOM FEED
	 * @throws Exception
	 * @arg object details
	 */
	public ArrayList getPartObjectsFromBOM(Context context, String strObjectID) throws Exception {
		ArrayList alBOM = new ArrayList();
		MapList mlMinMaxQtyValues = new MapList();
		MapList mlFlatBOM = new MapList();
		Map mpMinMaxQty = null;
		StringList slSubstituteGroupValues = new StringList(1);

		String strFindNumb = DomainConstants.EMPTY_STRING;
		String strEffectivityDateChild = DomainConstants.EMPTY_STRING;
		//Added by DSM - for 2018x.1 requirement #25043 - Starts
		String strValidUntilDate = DomainConstants.EMPTY_STRING;
		//Added by DSM - for 2018x.1 requirement #25043 - Ends
		String sChildName = DomainConstants.EMPTY_STRING;
		String sQuantity = DomainConstants.EMPTY_STRING;
		String strMax = DomainConstants.EMPTY_STRING;
		String strMin = DomainConstants.EMPTY_STRING;
		String sTarget = DomainConstants.EMPTY_STRING;
		String strPosInd = DomainConstants.EMPTY_STRING;
		String strFILBaseQty = DomainConstants.EMPTY_STRING;
		String strID = DomainConstants.EMPTY_STRING;
		String strQtyTmp = DomainConstants.EMPTY_STRING;
		String strGroupTmp = DomainConstants.EMPTY_STRING;
		String strRelID = DomainConstants.EMPTY_STRING;
		String strTopNodeType = DomainConstants.EMPTY_STRING;
		String strTopNodeName = DomainConstants.EMPTY_STRING;
		String strTopNodeEffeDate = DomainConstants.EMPTY_STRING;
		String strTopNodeBOMQty = DomainConstants.EMPTY_STRING;
		String strpgPhaseBOMQty = DomainConstants.EMPTY_STRING;
		String strSAPType = DomainConstants.EMPTY_STRING;
		String strObjectType = DomainConstants.EMPTY_STRING;
		String strOptComponent = DomainConstants.EMPTY_STRING;
		String strComment = DomainConstants.EMPTY_STRING;
		Map mpBOM = null;
		Map mpNodeDeatails = null;
		String[] arrBOM;
		DomainObject dObjForTopNodePart = null;
		//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Starts
		DomainObject doBase = null;
		DomainObject doAlternate = null;
		String sBOMComponentCurrent = DomainConstants.EMPTY_STRING;
		String sAuthoringApplication = DomainConstants.EMPTY_STRING;
		String strRelationship = DomainConstants.EMPTY_STRING;
		boolean sCATIAAPPDeliver = true;
		Map mpAlternateDetails = new HashMap();
		StringList slAlternateSelects = new StringList(2);
		slAlternateSelects.add(DomainConstants.SELECT_CURRENT);
		slAlternateSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
		//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Ends
		try {
			if (BusinessUtil.isNotNullOrEmpty(strObjectID)) {
				dObjForTopNodePart = DomainObject.newInstance(context, strObjectID);
				//Check the top level part type
				mpNodeDeatails = dObjForTopNodePart.getInfo(context, SL_OBJECT_INFO_SELECT);
				strTopNodeType = (String) mpNodeDeatails.get(DomainConstants.SELECT_TYPE);
				strTopNodeName = (String) mpNodeDeatails.get(DomainConstants.SELECT_NAME);
				strTopNodeEffeDate = (String) mpNodeDeatails.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
				strTopNodeBOMQty = (String) mpNodeDeatails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
				//To get BOM components of the respective types
				mlFlatBOM = getPartBOMFEEDData(context, strObjectID, strTopNodeType);
				int iSizeFlatBOM = mlFlatBOM.size();
				if (null != mlFlatBOM && iSizeFlatBOM > 0) {
					mlMinMaxQtyValues = getPartBOMQTYData(context, strObjectID, mlFlatBOM);
					slSubstituteGroupValues = getSubstituteGroupValue(context, strObjectID, mlFlatBOM);

					if (null != mlMinMaxQtyValues && iSizeFlatBOM == mlMinMaxQtyValues.size()) {
						for (int i = 0; iSizeFlatBOM > i; i++) {
							//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Starts
							mpAlternateDetails = new HashMap();
							sCATIAAPPDeliver = true;
							//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Ends
							mpBOM = (Map) mlFlatBOM.get(i);
							strFindNumb = (String) mpBOM.get(DomainConstants.SELECT_ATTRIBUTE_FIND_NUMBER);
							strEffectivityDateChild = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
							//Added by DSM - for 2018x.1 requirement #25043 - Starts
							strValidUntilDate = (String) mpBOM.get(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE);
							//Added by DSM - for 2018x.1 requirement #25043 - Ends
							sChildName = (String) mpBOM.get(DomainConstants.SELECT_NAME);
							sQuantity = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITY);
							mpMinMaxQty = (Map) mlMinMaxQtyValues.get(i);
							strMax = (String) mpMinMaxQty.get("MaxQty");
							strMin = (String) mpMinMaxQty.get("MinQty");
							sTarget = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
							;
							strPosInd = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
							strFILBaseQty = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
							strID = (String) mpBOM.get(DomainObject.SELECT_ID);
							strObjectType = (String) mpBOM.get(DomainConstants.SELECT_TYPE);
							strSAPType = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
							strOptComponent = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
							strComment = (String) mpBOM.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
							strQtyTmp = (String) mpMinMaxQty.get("Qty");
							strGroupTmp = (String) slSubstituteGroupValues.get(i);
							strRelationship = (String) mpBOM.get("relationship");

							//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Starts
							if (UIUtil.isNotNullAndNotEmpty(strID) && UIUtil.isNotNullAndNotEmpty(strObjectType) && UIUtil.isNotNullAndNotEmpty(strRelationship) && pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strObjectType) && (DomainConstants.RELATIONSHIP_ALTERNATE.equalsIgnoreCase(strRelationship))) {

								doAlternate = DomainObject.newInstance(context, strID);

								mpAlternateDetails = doAlternate.getInfo(context, slAlternateSelects);
								sBOMComponentCurrent = (String) mpAlternateDetails.get(DomainConstants.SELECT_CURRENT);
								sAuthoringApplication = (String) mpAlternateDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);

								if (UIUtil.isNotNullAndNotEmpty(sAuthoringApplication) && pgV3Constants.RANGE_PGAUTHORINGAPPLICATION_LPD.equalsIgnoreCase(sAuthoringApplication) && !(pgV3Constants.STATE_RELEASE.equalsIgnoreCase(sBOMComponentCurrent))) {

									sCATIAAPPDeliver = false;
								}
							}

							if (!pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSAPType) && !strObjectType.contains(pgV3Constants.TYPE_MASTER) && sCATIAAPPDeliver) {
								//Added by DSM Sogeti for 2018x.5 Requirement #34981 - Ends
								//Modified by DSM - for 2018x.1 requirement #25043 - Starts
								arrBOM = buildBOMArray(strTopNodeName, strFindNumb,
										strEffectivityDateChild,
										sChildName, strQtyTmp,
										strMax, sTarget, strMin,
										strTopNodeEffeDate,
										strTopNodeBOMQty, strPosInd, strFILBaseQty, strpgPhaseBOMQty, strGroupTmp, strID, strOptComponent, strComment
										, "", "", "", strValidUntilDate);
								//Modified by DSM - for 2018x.1 requirement #25043 - Ends
								arrBOM[pgV3Constants.INDEX_TPU_ID] = (String) mpBOM.get("TUP_ID");
								alBOM.add(arrBOM);
							}

						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return alBOM;
	}

	/**
	 * This method returns Vector of BOM quantity data for APP/DPP/FAB Components
	 *
	 * @param context
	 * @param strObjectID:    Object Id
	 * @param mpListChildren: BOM Feed Data
	 * @return MapList of BOM Quantity,Min and Max
	 * @throws Exception
	 * @arg object details
	 */

	public MapList getPartBOMQTYData(Context context, String strObjectID, MapList mpListChildren) throws Exception {
		//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Starts
		//float flSAPBOMBaseQty = 0;
		BigDecimal bdSAPBOMBaseQty = BigDecimal.ZERO;
		//float flBOMBaseQty = 1;
		BigDecimal bdBOMBaseQty = new BigDecimal("1");
		BigDecimal bdBOMQuantityCheck = new BigDecimal("9999999999.999");
		StringList slBCF = new StringList(2);
		slBCF.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
		slBCF.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
		MapList mlMinMaxQtyValues = new MapList();
		DomainObject dObjFPP = null;
		Map mapBCF = null;
		Map mapTemp = null;

		String strSAPBOMBaseQty = null;
		String strBOMBaseQty = null;
		String strMinQty = null;
		String strMaxQty = null;
		String strMinVal = null;
		String strMaxVal = null;
		String strBOMQty = null;
		String strQuantityVal = null;
		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
		String strType = null;
		String strExpandBOMonSAPBOMasFed = null;
		String strFABExpandBOMQuantity = null;
		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends

		//float fltMinQty = 0;
		BigDecimal bdMinQty = BigDecimal.ZERO;
		//float fltMaxQty = 0;
		BigDecimal bdMaxQty = BigDecimal.ZERO;
		//float flMinQuantity = 0;
		BigDecimal bdMinQuantity = BigDecimal.ZERO;
		//float flMaxQuantity = 0;
		BigDecimal bdMaxQuantity = BigDecimal.ZERO;
		//float fltBOMQty = 0;
		BigDecimal bdBOMQty = BigDecimal.ZERO;
		//float flQuantity = 0;
		BigDecimal bdQuantity = BigDecimal.ZERO;
		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
		BigDecimal bdFABQuantity = BigDecimal.ZERO;
		// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends

		try {

			if (BusinessUtil.isNotNullOrEmpty(strObjectID)) {
				dObjFPP = DomainObject.newInstance(context, strObjectID);
				mapBCF = (Map) dObjFPP.getInfo(context, slBCF);
				strSAPBOMBaseQty = (String) mapBCF.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
				strBOMBaseQty = (String) mapBCF.get(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
				if (BusinessUtil.isNotNullOrEmpty(strSAPBOMBaseQty)) {
					//flSAPBOMBaseQty = (Float.valueOf(strSAPBOMBaseQty)).floatValue();
					bdSAPBOMBaseQty = new BigDecimal(strSAPBOMBaseQty);
				}
				if (BusinessUtil.isNotNullOrEmpty(strBOMBaseQty)) {
					//flBOMBaseQty = (Float.valueOf(strBOMBaseQty)).floatValue();
					bdBOMBaseQty = new BigDecimal(strBOMBaseQty);
				}
				if (null != mpListChildren) {

					for (int i = 0; i < mpListChildren.size(); i++) {
						mapTemp = (Map) mpListChildren.get(i);
						// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
						strExpandBOMonSAPBOMasFed = "False";
						strFABExpandBOMQuantity = "";
						// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
						strQuantityVal = (String) mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
						strMinVal = (String) mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
						strMaxVal = (String) mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
						strType = (String) mapTemp.get("Type");
						if (UIUtil.isNotNullAndNotEmpty(strType)
								&& pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType)) {
							strExpandBOMonSAPBOMasFed = (String) mapTemp.get("FAB_ExpandBOMonSAPBOMasFed");
							strFABExpandBOMQuantity = (String) mapTemp.get("FAB_ExpandBOMQuantity");
						}
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
						if (BusinessUtil.isNotNullOrEmpty(strQuantityVal)) {
							//flQuantity = (Float.valueOf(strQuantityVal)).floatValue();
							bdQuantity = new BigDecimal(strQuantityVal);
						}
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
						if (UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMasFed)
								&& "TRUE".equalsIgnoreCase(strExpandBOMonSAPBOMasFed)
								&& UIUtil.isNotNullAndNotEmpty(strFABExpandBOMQuantity)) {
							bdFABQuantity = new BigDecimal(strFABExpandBOMQuantity);
						}
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
						//BOM Quantity Calculation
						//if(flBOMBaseQty != 0) {
						if (bdBOMBaseQty.compareTo(BigDecimal.ZERO) != 0) {
							//fltBOMQty = (flSAPBOMBaseQty/flBOMBaseQty)*flQuantity;
							bdBOMQty = bdSAPBOMBaseQty.divide(bdBOMBaseQty, 5, RoundingMode.HALF_UP);
							//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Starts
							bdBOMQty = (bdBOMQty.multiply(bdQuantity)).setScale(3, RoundingMode.HALF_UP);
							//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Ends
							// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
							if (UIUtil.isNotNullAndNotEmpty(strType)
									&& strType.equalsIgnoreCase(pgV3Constants.TYPE_FABRICATEDPART)) {
								bdBOMQty = (bdFABQuantity.multiply(bdBOMQty)).setScale(3, RoundingMode.HALF_UP);
							}
							// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
						}
						//Convert this float to String
						//strBOMQty = String.valueOf(fltBOMQty);
						strBOMQty = String.valueOf(bdBOMQty);
						//if(fltBOMQty == 0 || fltBOMQty > 9999999999.999) {
						if ((bdBOMQty.compareTo(BigDecimal.ZERO) == 0) || (bdBOMQty.compareTo(bdBOMQuantityCheck) > 0)) {
							strBOMQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
						}
						if (BusinessUtil.isNotNullOrEmpty(strMinVal)) {
							//flMinQuantity = (Float.valueOf(strMinVal)).floatValue();
							bdMinQuantity = new BigDecimal(strMinVal);
						}
						//BOM Min Calculation
						//if(flBOMBaseQty != 0) {
						if (bdBOMBaseQty.compareTo(BigDecimal.ZERO) != 0) {
							//fltMinQty = (flSAPBOMBaseQty/flBOMBaseQty)*flMinQuantity;
							bdMinQty = bdSAPBOMBaseQty.divide(bdBOMBaseQty, 5, RoundingMode.HALF_UP);
							//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Starts
							bdMinQty = bdMinQty.multiply(bdMinQuantity).setScale(3, RoundingMode.HALF_UP);
							//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Ends
						}
						//Convert this float to String
						//strMinQty = String.valueOf(fltMinQty);
						strMinQty = String.valueOf(bdMinQty);
						//if(fltMinQty == 0 || fltMinQty > 9999999999.999) {
						if ((bdMinQty.compareTo(BigDecimal.ZERO) == 0) || (bdMinQty.compareTo(bdBOMQuantityCheck) > 0)) {
							strMinQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
						}
						if (BusinessUtil.isNotNullOrEmpty(strMaxVal)) {
							//flMaxQuantity = (Float.valueOf(strMaxVal)).floatValue();
							bdMaxQuantity = new BigDecimal(strMaxVal);
						}
						//BOM Max Calculation
						//if(flBOMBaseQty != 0) {
						if (bdBOMBaseQty.compareTo(BigDecimal.ZERO) != 0) {
							//fltMaxQty = (flSAPBOMBaseQty/flBOMBaseQty)*flMaxQuantity;
							bdMaxQty = bdSAPBOMBaseQty.divide(bdBOMBaseQty, 5, RoundingMode.HALF_UP);
							//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Starts
							bdMaxQty = bdMaxQty.multiply(bdMaxQuantity).setScale(3, RoundingMode.HALF_UP);
							//Modified by DSM Sogeti for 2018x.5 Defect #33192 - Ends
						}
						//Convert this float to String
						//strMaxQty = String.valueOf(fltMaxQty);
						strMaxQty = String.valueOf(bdMaxQty);
						//if(fltMaxQty == 0 || fltMaxQty > 9999999999.999) {
						if ((bdMaxQty.compareTo(BigDecimal.ZERO) == 0) || (bdMaxQty.compareTo(bdBOMQuantityCheck) > 0)) {
							strMaxQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
						}
						Map mpMinMaxQtyValue = new HashMap();
						mpMinMaxQtyValue.put("Qty", strBOMQty);
						mpMinMaxQtyValue.put("MinQty", strMinQty);
						mpMinMaxQtyValue.put("MaxQty", strMaxQty);
						mlMinMaxQtyValues.add(mpMinMaxQtyValue);
						//fltBOMQty = 0;
						bdBOMQty = BigDecimal.ZERO;
						//flQuantity = 0;
						bdQuantity = BigDecimal.ZERO;
						//fltMaxQty = 0;
						bdMaxQty = BigDecimal.ZERO;
						//flMinQuantity = 0;
						bdMinQuantity = BigDecimal.ZERO;
						//flMaxQuantity = 0;
						bdMaxQuantity = BigDecimal.ZERO;
						strBOMQty = DomainConstants.EMPTY_STRING;
						strMinQty = DomainConstants.EMPTY_STRING;
						strMaxQty = DomainConstants.EMPTY_STRING;
					}
					//Added by DSM(Sogeti) for 2018x.2 BigDecimal Requirement #25022 -Ends
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw ex;
		}
		return mlMinMaxQtyValues;
	}

	/**
	 * This method updates the object's attribute in case of failure.
	 *
	 * @param context,String,String
	 * @return Void
	 * @throws FrameworkException
	 */

	public static void updateErrorForFailureView(Context context, String strReturnMsg, String sPartObjectID) throws FrameworkException {
		HashMap hAttributeMap = new HashMap();
		String sCurrentDate = DomainConstants.EMPTY_STRING;

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat(eMatrixDateFormat.getInputDateFormat());
			Date today = new Date();
			sCurrentDate = (String) dateFormat.format(today);

			String strClassification = pgV3Constants.ATTRIBUTE_PGFAILED_CLASSIFICATION_BOMDELIVERY;


			if (BusinessUtil.isNotNullOrEmpty(sPartObjectID)) {

				if (BusinessUtil.isNullOrEmpty(strReturnMsg)) {
					strReturnMsg = DomainConstants.EMPTY_STRING;
					strClassification = DomainConstants.EMPTY_STRING;
					sCurrentDate = DomainConstants.EMPTY_STRING;
				}

				DomainObject domObj = DomainObject.newInstance(context, sPartObjectID);

				if (!strSuccess.equalsIgnoreCase(strReturnMsg)) {
					hAttributeMap.put(pgV3Constants.ATTRIBUTE_PGFAILEDREASON, strReturnMsg);
					hAttributeMap.put(pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION, strClassification);
					hAttributeMap.put(pgV3Constants.ATTRIBUTE_PGERRORDATE, sCurrentDate);
				} else {
					hAttributeMap.put(pgV3Constants.ATTRIBUTE_PGFAILEDREASON, DomainConstants.EMPTY_STRING);
					hAttributeMap.put(pgV3Constants.ATTRIBUTE_PGERRORCLASSIFICATION, DomainConstants.EMPTY_STRING);
					hAttributeMap.put(pgV3Constants.ATTRIBUTE_PGERRORDATE, DomainConstants.EMPTY_STRING);

				}
				ContextUtil.pushContext(context);
				domObj.setAttributeValues(context, hAttributeMap);
				ContextUtil.popContext(context);

			}
		} catch (Exception e) {
			// don't stop the processing further.
			// send the error log and continue.
			e.printStackTrace();
			//throw e;
		}
	}


	/**
	 * getObjectsForFOPFromFormulaIngredient- Method is used to get the Child for Formulation Part
	 * Get the PLBOM, Substitute and Alternates for Formulation part.
	 *
	 * @param context       the eMatrix <code>Context</code> object
	 * @param sPartObjectID
	 * @return ArrayList
	 * @throws Exception
	 */
	public synchronized ArrayList getObjectsForFOPFromFormulaIngredient(Context context, Map mapPartDetails) throws Exception {


		String[] saPLBOMArray = new String[pgV3Constants.INDEX_BOM_ARRAY_SIZE];

		ArrayList alPartObjects = new ArrayList();

		Map mpPLBOMData = new HashMap();
		DomainObject domPhaseObjectChild = null;
		DomainObject doAlternateObj = null;

		String strName = DomainConstants.EMPTY_STRING;

		String strID = DomainConstants.EMPTY_STRING;
		String strBOMBaseQty = DomainConstants.EMPTY_STRING;
		String strParentEffectivDate = DomainConstants.EMPTY_STRING;
		String RELATIONSHIP_PLANNEDFOR = pgV3Constants.RELATIONSHIP_PLANNEDFOR;
		String TYPE_FORMULATIONPROCESS = pgV3Constants.TYPE_FORMULATIONPROCESS;
		String TYPE_FORMULATIONPHASE = pgV3Constants.TYPE_FORMULATIONPHASE;
		char cAltItem1 = pgV3Constants.ALT_ITEM_NINE;
		char cAltItem2 = pgV3Constants.ALT_ITEM_A;

		String strChildType = DomainConstants.EMPTY_STRING;
		String strChildID = DomainConstants.EMPTY_STRING;
		String[] saChild = new String[1];

		String strFormulationChildType = DomainConstants.EMPTY_STRING;
		String strFormulationChildId = DomainConstants.EMPTY_STRING;
		String strFormPhaseChildType = DomainConstants.EMPTY_STRING;
		MapList mpListChildren = new MapList();
		String strParentName = DomainConstants.EMPTY_STRING;
		String strFindNumber = DomainConstants.EMPTY_STRING;
		String strEffectivityDateChild = DomainConstants.EMPTY_STRING;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strQuantity = DomainConstants.EMPTY_STRING;
		String strMax = DomainConstants.EMPTY_STRING;
		String strTargetQuantity = DomainConstants.EMPTY_STRING;
		String strMin = DomainConstants.EMPTY_STRING;
		String strPosInd = DomainConstants.EMPTY_STRING;
		String strFILBaseQty = DomainConstants.EMPTY_STRING;
		String strpgPhaseBOMQty = DomainConstants.EMPTY_STRING;
		String id = DomainConstants.EMPTY_STRING;
		String strSAPType = DomainConstants.EMPTY_STRING;
		String strObjectType = DomainConstants.EMPTY_STRING;
		String strAlternate = DomainConstants.EMPTY_STRING;
		String strPLBOMRelId = DomainConstants.EMPTY_STRING;
		String strSubstitue = DomainConstants.EMPTY_STRING;
		String strTSName = DomainConstants.EMPTY_STRING;
		String strTSType = DomainConstants.EMPTY_STRING;
		String strValidFromDateSub = DomainConstants.EMPTY_STRING;
		String strQty = DomainConstants.EMPTY_STRING;
		String strGroup = DomainConstants.EMPTY_STRING;
		String sChildFormulationType = DomainConstants.EMPTY_STRING;
		String strChildSubFormulationType = DomainConstants.EMPTY_STRING;
		String[][] saSubs = new String[1][1];
		StringList slQtyValues = new StringList(1);
		StringList slSubstituteGroupValues = new StringList(1);
		MapList mpListIngredients = new MapList();

		String strAlternateBaseUOM = DomainConstants.EMPTY_STRING;
		String strChildEffectivDate = DomainConstants.EMPTY_STRING;
		String strFOPName = DomainConstants.EMPTY_STRING;
		String strAltId = DomainConstants.EMPTY_STRING;
		String strAltName = DomainConstants.EMPTY_STRING;
		String strAltEffDate = DomainConstants.EMPTY_STRING;
		String strSubQuantity = DomainConstants.EMPTY_STRING;
		String strSubMaxQty = DomainConstants.EMPTY_STRING;
		String strSubMinQty = DomainConstants.EMPTY_STRING;
		String strAlternateMin = DomainConstants.EMPTY_STRING;
		String strAlternateMax = DomainConstants.EMPTY_STRING;
		String strAlternateQTY = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Starts
		String strPriAlternateAssemblyType = DomainConstants.EMPTY_STRING;
		String strPriAlternateSAPType = DomainConstants.EMPTY_STRING;
		String strSubAlternateAssemblyType = DomainConstants.EMPTY_STRING;
		String strSubAlternateSAPType = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Ends

		//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
		String strTempSpecSubType = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends

		//For Grouping Identifier
		int iCounter = 0;
		//For BaseUOM Validation
		String strBaseUOMChild = DomainConstants.EMPTY_STRING;
		String strBaseUOMParent = DomainConstants.EMPTY_STRING;
		String strBaseUOMSub = DomainConstants.EMPTY_STRING;
		String strSubRelID = DomainConstants.EMPTY_STRING;
		String strFOPNextRev = DomainConstants.EMPTY_STRING;

		Map mpTempChildData = new HashMap();
		Map phaseInfo = new HashMap();
		DomainObject domObjectChild = null;

		MapList mlPLBOMParts = new MapList();
		MapList mlPhases = new MapList();


		DomainObject domPhase = null;
		String strActualWeightDry = DomainConstants.EMPTY_STRING;
		String strActualPercentDry = DomainConstants.EMPTY_STRING;
		String strActualWeightWet = DomainConstants.EMPTY_STRING;
		String strActualPercentWet = DomainConstants.EMPTY_STRING;
		String strLoss2 = DomainConstants.EMPTY_STRING;
		String phaseId = DomainConstants.EMPTY_STRING;

		//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
		String phaseVIPercentConsumed = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends
		double dTotal_Dry_Weight = 0.0;
		double dFOP_Percent = 100;
		double dFOP_Loss = 0.0;
		double dFOP_Actual_Weight_Wet = 0.0;
		double dFOP_Loss_Percent = 0.0;

		//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
		boolean isTargetPercentAsConsumedFixed = false;
		//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends

		//Sub Selectable
		String strNONGCASTYPE = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(), "emxCPN.SAPBOM.DSM.NONGCASTypes");
		String strObjectWhere = new StringBuffer(DomainObject.SELECT_CURRENT).append(" != ").append(pgV3Constants.STATE_PART_OBSOLETE).toString();

		MapList mlSubAlts = null;
		int iAltsSize = 0;

		try {
			if (null != mapPartDetails && mapPartDetails.size() > 0) {
				strID = (String) mapPartDetails.get(DomainConstants.SELECT_ID);
				strFOPName = (String) mapPartDetails.get(DomainConstants.SELECT_NAME);
				strBOMBaseQty = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
				strParentEffectivDate = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
				strBaseUOMParent = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
				if (BusinessUtil.isNotNullOrEmpty(strID)) {
					strFOPNextRev = (String) mapPartDetails.get("next.id");
					mpListChildren = getValidFormulationProcess(context, strID, strFOPNextRev);

					if (null != mpListChildren && mpListChildren.size() > 0) {

						int iChildrenSize = mpListChildren.size();

						for (int i = 0; i < iChildrenSize; i++) {

							mpTempChildData = (Map) mpListChildren.get(i);
							strChildType = (String) mpTempChildData.get(DomainConstants.SELECT_TYPE);
							strChildID = (String) mpTempChildData.get(DomainConstants.SELECT_ID);

							if (TYPE_FORMULATIONPROCESS.equalsIgnoreCase(strChildType)) {

								strActualWeightDry = (String) mpTempChildData.get(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_WEIGHT_DRY);
								strActualPercentDry = (String) mpTempChildData.get(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_PERCENT_DRY);
								strActualWeightWet = (String) mpTempChildData.get(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_WEIGHT_WET);
								strActualPercentWet = (String) mpTempChildData.get(pgV3Constants.SELECT_ATTRIBUTE_ACTUAL_PERCENT_WET);

								if (BusinessUtil.isNotNullOrEmpty(strChildID)) {

									domObjectChild = DomainObject.newInstance(context, strChildID);
									//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
									String strSumOfDryweight = getTotalDryweight(context, domObjectChild);
									//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

									if (null != domObjectChild) {
										mlPhases = domObjectChild.getRelatedObjects(context,
												pgV3Constants.RELATIONSHIP_PLBOM,    //relationshipPattern
												//pgV3Constants.SYMBOL_STAR, 			//typePattern
												DomainConstants.QUERY_WILDCARD,        // type pattern
												false,                                //getTo
												true,                                //getFrom
												(short) 1,                            //recurseToLevel
												SL_FOP_OBJECT_INFO_SELECT,            //objectSelects
												SL_RELATION_INFO_SELECT_OTHERS,    //relationshipSelects
												strObjectWhere,                    //objectWhere
												null,                                //relationshipWhere
												null,                                //PostRelPattern
												null,            //PostTypePattern
												null);

										if (mlPhases != null && mlPhases.size() > 0) {

											Collections.sort(mlPhases, new Comparator() {
												public int compare(Object first,
														Object second) {
													Map firstMap = (Map) first;
													Map secondMap = (Map) second;
													String firstValue = (String) (firstMap.get(DomainConstants.SELECT_NAME));
													String secondValue = (String) secondMap.get(DomainConstants.SELECT_NAME);

													return firstValue.compareTo(secondValue);
												}
											});

											for (int l = 0; l < mlPhases.size(); l++) {
												phaseInfo = (Map) mlPhases.get(l);
												phaseId = (String) phaseInfo.get(DomainConstants.SELECT_ID);
												domPhase = DomainObject.newInstance(context, phaseId);
												mlPLBOMParts = domPhase.getRelatedObjects(context,
														pgV3Constants.RELATIONSHIP_PLBOM,    //relationshipPattern
														//pgV3Constants.SYMBOL_STAR, 			//typePattern
														DomainConstants.QUERY_WILDCARD,        // type pattern
														false,                                //getTo
														true,                                //getFrom
														(short) 2,                            //recurseToLevel
														SL_FOP_OBJECT_INFO_SELECT,            //objectSelects
														SL_RELATION_INFO_SELECT_OTHERS,    //relationshipSelects
														strObjectWhere,                    //objectWhere
														null,                                //relationshipWhere
														null,                                //PostRelPattern
														null,            //PostTypePattern
														null);
												// Expanding till level 3 to get all the required data for all the types at once. Confirm Level Once
												if (null != mlPLBOMParts && mlPLBOMParts.size() > 0) {

													int iPLBOMSize = mlPLBOMParts.size();

													String strMaxWet = DomainConstants.EMPTY_STRING;
													String strMinWet = DomainConstants.EMPTY_STRING;
													String strLoss = DomainConstants.EMPTY_STRING;
													String strQty2 = DomainConstants.EMPTY_STRING;
													String strFindNumber2 = DomainConstants.EMPTY_STRING;
													String strFindNumb = DomainConstants.EMPTY_STRING;
													String sChildName = DomainConstants.EMPTY_STRING;
													String strTempSAPType = DomainConstants.EMPTY_STRING;
													String sQuantity = DomainConstants.EMPTY_STRING;
													String sTarget = DomainConstants.EMPTY_STRING;
													String strQtyTmp = DomainConstants.EMPTY_STRING;
													String strGroupTmp = DomainConstants.EMPTY_STRING;
													String strFOPChildID = DomainConstants.EMPTY_STRING;
													String strFOPChildType = DomainConstants.EMPTY_STRING;
													String strTargetDryWeight = DomainConstants.EMPTY_STRING;
													String strTargetWetWeight = DomainConstants.EMPTY_STRING;
													//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
													String strVIPhysicalID = DomainConstants.EMPTY_STRING;
													//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends

													String strBOMType = "";
													String strBOMName = "";

													Object oSubstituteId = null;
													Object oSubstituteType = null;
													Object oSubstituteName = null;
													Object oSubstituteBaseUOM = null;
													Object substituteEffectivityDate = null;
													Object oSubstituteRelID = null;
													Object substituteFormulationType = null;
													Object oAlternateId = null;
													Object oAlternateType = null;
													Object oAlternateName = null;
													Object oAlternateEffDate = null;
													Object oAlternateBaseUOM = null;

													DomainObject doRMP = DomainObject.newInstance(context);
													MapList mpListBOMChildren = new MapList();

													Map mpReturned = null;
													//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
													Map mapVISUbs = new HashMap();
													//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends

													StringList slAltType = new StringList(1);
													StringList slAltName = new StringList(1);
													StringList slAltID = new StringList(1);
													for (int j = 0; j < iPLBOMSize; j++) {
														strMaxWet = DomainConstants.EMPTY_STRING;
														strMinWet = DomainConstants.EMPTY_STRING;
														strLoss = DomainConstants.EMPTY_STRING;
														strQty2 = DomainConstants.EMPTY_STRING;
														strFindNumber2 = DomainConstants.EMPTY_STRING;
														mpPLBOMData = (Map) mlPLBOMParts.get(j);
														strTempSAPType = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
														//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
														strTempSpecSubType = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
														//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends

														strFormulationChildType = (String) mpPLBOMData.get(DomainConstants.SELECT_TYPE);
														//Modified by DSM(Sogeti) for 2015x.5 SAP BOM as Fed Req 18926,18927,19163,19122 - Starts
														//Modified by DSM(Sogeti) for 2015x.5 SAP BOM as Fed Defect#15651  - Starts
														//Modified by DSM(Sogeti) for 2018x.1.1 September Release SAP BOM as Fed Defect#29366  - Starts
														//sChildFormulationType 	= (String)mpPLBOMData.get("to["+pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE+"].from."+pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);
														Object OChildFormulationType = (Object) mpPLBOMData.get("to[" + pgV3Constants.RELATIONSHIP_FORMULATIONPROPAGATE + "].from." + pgV3Constants.SELECT_ATTRIBUTE_FORMULATIONTYPE);

														StringList slChildFormulationType = new StringList();

														if (null != OChildFormulationType) {
															if (OChildFormulationType instanceof StringList) {
																slChildFormulationType = (StringList) OChildFormulationType;
															} else if (OChildFormulationType instanceof String) {
																slChildFormulationType.add(OChildFormulationType.toString());
															}
														}

														//Modified by DSM(Sogeti) for 2018x.1.1 September Release SAP BOM as Fed Defect#29366  - Ends
														//Modified by DSM(Sogeti) for 2015x.5 SAP BOM as Fed Defect#15651  - Ends
														//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
														if ((!(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strTempSAPType)) && !(strNONGCASTYPE.contains(strFormulationChildType)) && !strTempSpecSubType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY)) && (!pgV3Constants.TYPE_FORMULATIONPART.equals(strFormulationChildType) || (pgV3Constants.TYPE_FORMULATIONPART.equals(strFormulationChildType) && !slChildFormulationType.isEmpty() && !slChildFormulationType.contains(pgV3Constants.THIRD_PARTY_FORMULA)))) {
															//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends
															//Modified by DSM(Sogeti) for 2015x.5 SAP BOM as Fed Req 18926,18927,19163,19122 - Ends
															strFormulationChildId = (String) mpPLBOMData.get(DomainConstants.SELECT_ID);
															strChildEffectivDate = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
															strFindNumb = (String) mpPLBOMData.get(DomainConstants.SELECT_ATTRIBUTE_FIND_NUMBER);
															strEffectivityDateChild = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
															sChildName = (String) mpPLBOMData.get(DomainConstants.SELECT_NAME);
															sQuantity = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITY);
															strMax = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGMAXQUANTITY);
															strMin = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGMINQUANTITY);
															sTarget = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
															strPosInd = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
															strFILBaseQty = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
															strID = (String) mpPLBOMData.get(DomainObject.SELECT_ID);
															strBaseUOMChild = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
															strQtyTmp = strQty2;
															strPLBOMRelId = (String) mpPLBOMData.get(DomainRelationship.SELECT_ID);
															strBOMType = "";
															strBOMName = "";
															strTargetDryWeight = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY);
															strTargetWetWeight = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
															//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
															strVIPhysicalID = (String) mpPLBOMData.get(pgV3Constants.SELECT_ATTRIBUTE_VIRTUALINTERMEDIATEPHYSICALID);

															if (BusinessUtil.isNotNullOrEmpty(strVIPhysicalID)) {
																VirtualIntermediate virtualObj = new VirtualIntermediate(context, strVIPhysicalID);

																isTargetPercentAsConsumedFixed = virtualObj.isTargetPercentAsConsumedFixed();


																phaseVIPercentConsumed = (String) getVITargetPercentConsumed(context, isTargetPercentAsConsumedFixed, strPLBOMRelId);

															}
															//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends
															if (BusinessUtil.isNotNullOrEmpty(strFormulationChildId)) {

																//Include the Substitute of Component
																oSubstituteId = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_ID);
																StringList slSubstituteId = new StringList();
																if (null != oSubstituteId) {
																	//Refactoring - InstanceList - 49
																	slSubstituteId = getInstanceList(oSubstituteId);
																}
																oSubstituteType = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_TYPE);
																StringList slSubstituteType = new StringList();
																if (null != oSubstituteType) {
																	//Refactoring - InstanceList - 50
																	slSubstituteType = getInstanceList(oSubstituteType);
																}
																oSubstituteName = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_NAME);
																StringList slSubstituteName = new StringList();
																if (null != oSubstituteName) {
																	//Refactoring - InstanceList - 51
																	slSubstituteName = getInstanceList(oSubstituteName);
																}
																oSubstituteBaseUOM = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_BASE_UOM);
																StringList slSubstituteBaseUOM = new StringList();
																if (null != oSubstituteBaseUOM) {
																	//Refactoring - InstanceList - 52
																	slSubstituteBaseUOM = getInstanceList(oSubstituteBaseUOM);
																}

																substituteEffectivityDate = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_EFFECTIVITY_DATE);
																StringList slSubstituteEffectivityDate = new StringList();
																if (null != substituteEffectivityDate) {
																	//Refactoring - InstanceList - 53
																	slSubstituteEffectivityDate = getInstanceList(substituteEffectivityDate);
																}

																oSubstituteRelID = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_REL_ID);
																StringList slSubstituteRelID = new StringList();
																if (null != oSubstituteRelID) {
																	//Refactoring - InstanceList - 54
																	slSubstituteRelID = getInstanceList(oSubstituteRelID);
																}

																substituteFormulationType = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_FORMULATION_TYPE);
																StringList slSubstituteFormulationType = new StringList();
																if (null != substituteFormulationType) {
																	//Refactoring - InstanceList - 55
																	slSubstituteFormulationType = getInstanceList(substituteFormulationType);
																}

																//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
																if (BusinessUtil.isNotNullOrEmpty(strVIPhysicalID)) {
																	mapVISUbs = (Map) getVISubstituteDetails(context, strVIPhysicalID, strFormulationChildId);

																	if (mapVISUbs != null && !mapVISUbs.isEmpty()) {
																		slSubstituteId.addAll((StringList) mapVISUbs.get("slSubstituteId"));
																		slSubstituteType.addAll((StringList) mapVISUbs.get("slSubstituteType"));
																		slSubstituteName.addAll((StringList) mapVISUbs.get("slSubstituteName"));
																		slSubstituteBaseUOM.addAll((StringList) mapVISUbs.get("slSubstituteBaseUOM"));
																		slSubstituteEffectivityDate.addAll((StringList) mapVISUbs.get("slSubstituteEffectivityDate"));
																		slSubstituteRelID.addAll((StringList) mapVISUbs.get("slSubstituteRelID"));
																		slSubstituteFormulationType.addAll((StringList) mapVISUbs.get(slSubstituteFormulationType));
																	}

																}
																//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends
																//Include the Alternates of Component
																oAlternateId = (Object) mpPLBOMData.get(pgV3Constants.SELECT_ALTERNATE_ID);
																StringList slAlternateId = new StringList();
																if (null != oAlternateId) {
																	//Refactoring - InstanceList - 56
																	slAlternateId = getInstanceList(oAlternateId);
																}
																oAlternateType = (Object) mpPLBOMData.get(pgV3Constants.SELECT_ALTERNATE_TYPE);
																StringList slAlternateType = new StringList();
																if (null != oAlternateType) {
																	//Refactoring - InstanceList - 57
																	slAlternateType = getInstanceList(oAlternateType);
																}
																oAlternateName = (Object) mpPLBOMData.get(pgV3Constants.SELECT_ALTERNATE_NAME);
																StringList slAlternateName = new StringList();
																if (null != oAlternateName) {
																	//Refactoring - InstanceList - 58
																	slAlternateName = getInstanceList(oAlternateName);
																}
																//Added by DSM(Sogeti) - for 2015x.4 BOM eDelivery defect 12826 - Starts
																oAlternateEffDate = (Object) mpPLBOMData.get(pgV3Constants.SELECT_ALTERNATE_EFFECTIVITY_DATE);
																StringList slAlternateEffDate = new StringList();
																if (null != oAlternateEffDate) {
																	//Refactoring - InstanceList - 59
																	slAlternateEffDate = getInstanceList(oAlternateEffDate);
																}

																oAlternateBaseUOM = (Object) mpPLBOMData.get(pgV3Constants.SELECT_ALTERNATE_BASE_UOM);
																StringList slAlternateBaseUOM = new StringList();
																if (null != oAlternateBaseUOM) {
																	//Refactoring - InstanceList - 60
																	slAlternateBaseUOM = getInstanceList(oAlternateBaseUOM);
																}
																//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Starts
																Object oAlternateSpecSubType = (Object) mpPLBOMData.get(pgV3Constants.SELECT_ALTERNATE_PGASSEMBLYTYPE);
																StringList slAlternateAssemblyType = new StringList();
																if (null != oAlternateSpecSubType) {
																	slAlternateAssemblyType = getInstanceList(oAlternateSpecSubType);
																}

																Object oAlternateSAPType = (Object) mpPLBOMData.get(pgV3Constants.SELECT_ALTERNATE_PGASSEMBLYTYPE);
																StringList slAlternateSAPType = new StringList();
																if (null != oAlternateSAPType) {
																	slAlternateSAPType = getInstanceList(oAlternateSAPType);
																}
																//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Ends

																//If Part has Substitute/Alternate then set Grouping Identifier
																//Note: Include the alternate validation as well once code is written to include Alternate Part
																if ((null != slSubstituteId && slSubstituteId.size() > 0) || (null != slAlternateId && slAlternateId.size() > 0)) {
																	if (iCounter > 0) {
																		if (cAltItem2 == 'Z') {
																			cAltItem2 = 'A';
																			cAltItem1--;
																		} else {
																			cAltItem2++;
																		}
																	}
																	iCounter++;
																	strGroupTmp = cAltItem1 + "" + cAltItem2;
																} else {
																	strGroupTmp = DomainConstants.EMPTY_STRING;
																}
																//Modified by DSM(Sogeti) - for 2018x.1.1 requirement #27074 - Starts
																//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
																//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
																StringList slMinMaxQty_Primary = calculateMaxMinQtyForFOPChild(context, strPLBOMRelId, strBOMBaseQty, strBaseUOMParent, strBaseUOMChild, false, strSumOfDryweight, DomainConstants.EMPTY_STRING);
																//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends
																//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
																//Modified by DSM(Sogeti) - for 2018x.1.1 requirement #27074 - ends
																//Modified by DSM - for 2018x.1 requirement #25043 - Starts
																saPLBOMArray = buildBOMArray(strFOPName, strFindNumber, strEffectivityDateChild, sChildName, (String) slMinMaxQty_Primary.get(2), (String) slMinMaxQty_Primary.get(1), "", (String) slMinMaxQty_Primary.get(0), strParentEffectivDate, strBOMBaseQty, "", "", "", strGroupTmp, strFormulationChildId, "", "", strTargetWetWeight, strTargetDryWeight, "True", "");
																//Modified by DSM - for 2018x.1 requirement #25043 - Ends
																alPartObjects.add(saPLBOMArray);

																if (null != slAlternateId) {
																	String strAlternateType = DomainConstants.EMPTY_STRING;
																	String strAlternateName = DomainConstants.EMPTY_STRING;
																	String strAlternateID = DomainConstants.EMPTY_STRING;
																	String strAlternateEffDate = DomainConstants.EMPTY_STRING;
																	for (int kk = 0; kk < slAlternateId.size(); kk++) {
																		//Modified by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Starts
																		strPriAlternateAssemblyType = (String) slAlternateAssemblyType.get(kk);
																		strPriAlternateSAPType = (String) slAlternateSAPType.get(kk);
																		if (!strPriAlternateAssemblyType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY) && !pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strPriAlternateSAPType)) {
																			strAlternateType = (String) slAlternateType.get(kk);
																			strAlternateName = (String) slAlternateName.get(kk);
																			strAlternateID = (String) slAlternateId.get(kk);
																			strAlternateEffDate = (String) slAlternateEffDate.get(kk);

																			String[] saTemp2 = new String[pgV3Constants.INDEX_BOM_ARRAY_SIZE];
																			strAlternateBaseUOM = (String) slAlternateBaseUOM.get(kk);
																			if (BusinessUtil.isNotNullOrEmpty(strBaseUOMParent) && BusinessUtil.isNotNullOrEmpty(strAlternateBaseUOM) && ((strBaseUOMParent.equalsIgnoreCase(strAlternateBaseUOM)) || (strBaseUOMParent.equalsIgnoreCase(pgV3Constants.BUOM_EACH) && strAlternateBaseUOM.equalsIgnoreCase(pgV3Constants.BUOM_KILOGRAM)))) {
																				strAlternateMin = (String) slMinMaxQty_Primary.get(0);
																				strAlternateMax = (String) slMinMaxQty_Primary.get(1);
																				strAlternateQTY = (String) slMinMaxQty_Primary.get(2);
																			} else if (BusinessUtil.isNullOrEmpty(strBaseUOMParent) || BusinessUtil.isNullOrEmpty(strAlternateBaseUOM) || !(strBaseUOMParent.equalsIgnoreCase(strAlternateBaseUOM))) {
																				strAlternateMin = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
																				strAlternateMax = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
																				strAlternateQTY = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
																			}
																			//Modified by DSM - for 2018x.1 requirement #25043 - Starts
																			saTemp2 = buildBOMArray(strFOPName, strFindNumber, strAlternateEffDate, strAlternateName, strAlternateQTY, strAlternateMax, "", strAlternateMin, strParentEffectivDate, strBOMBaseQty, "", "", "", strGroupTmp, strAlternateID, "", "", "", "", "", "");
																			//Modified by DSM - for 2018x.1 requirement #25043 - Ends
																			alPartObjects.add(saTemp2);
																		}
																		//Modified by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Ends
																	}
																}

																if (null != slSubstituteId) {
																	int iFormulationTypeSize = slSubstituteFormulationType.size();
																	int iCounterFormulation = 0;
																	for (int pp = 0; pp < slSubstituteId.size(); pp++) {
																		strBOMType = (String) slSubstituteType.get(pp);
																		if (pgV3Constants.TYPE_FORMULATIONPART.equals(strBOMType) && (iCounterFormulation < iFormulationTypeSize)) {
																			strChildSubFormulationType = (String) slSubstituteFormulationType.get(iCounterFormulation);
																			iCounterFormulation++;
																		} else
																			strChildSubFormulationType = DomainConstants.EMPTY_STRING;

																		if (!pgV3Constants.TYPE_FORMULATIONPART.equals(strBOMType) || (pgV3Constants.TYPE_FORMULATIONPART.equals(strBOMType) && UIUtil.isNotNullAndNotEmpty(strChildSubFormulationType) && !pgV3Constants.THIRD_PARTY_FORMULA.equals(strChildSubFormulationType))) {
																			strBOMName = (String) slSubstituteName.get(pp);
																			strBaseUOMSub = (String) slSubstituteBaseUOM.get(pp);
																			strEffectivityDateChild = (String) slSubstituteEffectivityDate.get(pp);
																			strSubRelID = (String) slSubstituteRelID.get(pp);
																			String strBOMIdName = (String) slSubstituteId.get(pp);

																			String[] saTemp = new String[pgV3Constants.INDEX_BOM_ARRAY_SIZE];
																			//Modified by DSM(Sogeti) - for 2018x.1.1 requirement #27074 - Starts
																			//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
																			//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Starts
																			StringList slMinMaxQty = calculateMaxMinQtyForFOPChild(context, strSubRelID, strBOMBaseQty, strBaseUOMParent, strBaseUOMSub, true, DomainConstants.EMPTY_STRING, phaseVIPercentConsumed);
																			//Modified by DSM Sogeti for 2018x.5 Defect #30820 : Ends
																			//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
																			//Modified by DSM(Sogeti) - for 2018x.1.1 requirement #27074 - Ends
																			strSubQuantity = (String) slMinMaxQty.get(2);
																			strSubMaxQty = (String) slMinMaxQty.get(1);
																			strSubMinQty = (String) slMinMaxQty.get(0);
																			//Modified by DSM - for 2018x.1 requirement #25043 - Starts
																			saTemp = buildBOMArray(strFOPName, strFindNumber, strEffectivityDateChild, strBOMName, strSubQuantity, strSubMaxQty, "", strSubMinQty, strParentEffectivDate, strBOMBaseQty, "", "", "", strGroupTmp, strBOMIdName, "", "", "", "", "", "");
																			//Modified by DSM - for 2018x.1 requirement #25043 - Ends

																			alPartObjects.add(saTemp);

																			if (UIUtil.isNotNullAndNotEmpty(strBOMType) && ALTERNATE_ALLOWED_TYPES.contains(strBOMType)) {
																				mlSubAlts = getAlternates(context,
																						strBOMIdName);
																				iAltsSize = mlSubAlts.size();
																				for (int k = 0; k < iAltsSize; k++) {
																					Map mpAltMap = (Map) mlSubAlts.get(k);
																					//Modified by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Starts
																					strSubAlternateAssemblyType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
																					strSubAlternateSAPType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
																					if (!strSubAlternateAssemblyType.equalsIgnoreCase(pgV3Constants.THIRD_PARTY) && !pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC.equalsIgnoreCase(strSubAlternateSAPType)) {
																						strAltId = (String) mpAltMap.get(DomainConstants.SELECT_ID);
																						strAltName = (String) mpAltMap.get(DomainConstants.SELECT_NAME);
																						strAltEffDate = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
																						strAlternateBaseUOM = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);

																						if (BusinessUtil.isNotNullOrEmpty(strBaseUOMParent) && BusinessUtil.isNotNullOrEmpty(strAlternateBaseUOM) && ((strBaseUOMParent.equalsIgnoreCase(strAlternateBaseUOM)) || (strBaseUOMParent.equalsIgnoreCase(pgV3Constants.BUOM_EACH) && strAlternateBaseUOM.equalsIgnoreCase(pgV3Constants.BUOM_KILOGRAM)))) {
																							strAlternateMin = strSubMinQty;
																							strAlternateMax = strSubMaxQty;
																							strAlternateQTY = strSubQuantity;
																						} else if (BusinessUtil.isNullOrEmpty(strBaseUOMParent) || BusinessUtil.isNullOrEmpty(strAlternateBaseUOM) || !(strBaseUOMParent.equalsIgnoreCase(strAlternateBaseUOM))) {
																							strAlternateMin = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
																							strAlternateMax = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
																							strAlternateQTY = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
																						}

																						saTemp = new String[pgV3Constants.INDEX_BOM_ARRAY_SIZE];
																						//Modified by DSM - for 2018x.1 requirement #25043 - Starts
																						saTemp = buildBOMArray(strFOPName, strFindNumber, strAltEffDate, strAltName, strAlternateQTY, strAlternateMax, "", strAlternateMin, strParentEffectivDate, strBOMBaseQty, "", "", "", strGroupTmp, strAltId, "", "", "", "", "", "");
																						//Modified by DSM - for 2018x.1 requirement #25043 - Ends
																						alPartObjects.add(saTemp);
																					}
																					//Modified by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Ends
																				}
																			}
																		}
																	}
																}
															}
														}
													} // End of for loop of child objects

												}//end phase loop

											} // End of if (phase null check)
										}// End of if null check
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//throw new Exception(ex.getMessage());
		}
		return alPartObjects;
	} // end of method getObjectsForFOPFromFormulaIngredient

	/**
	 * calculateMaxMinQtyForFOPChild- Method is used to get the values for Max Min and Qty of Child for Formulation Part.
	 *
	 * @param context                     the eMatrix <code>Context</code> object
	 * @param String,String,String,String
	 * @return StringList
	 * @throws Exception
	 */
	//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
	//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Starts
	private StringList calculateMaxMinQtyForFOPChild(Context context, String strPLBOMRelId, String strSAPBOMBaseQty, String strBaseUOMParent, String strBaseUOMChild, boolean bSubstitute, String SumofDryWeight, String sTargetPercentConsumed) {
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Ends
		//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
		//Modified by DSM(Sogeti) for 2018x.1.1 Requirement 27074 - Added one argument bSubstitute
		StringList slReturn = new StringList(3);
		final String ATTRIBUTE_TOTAL = PropertyUtil.getSchemaProperty("attribute_Total");
		//Modified by DSM(Sogeti)-2018x.1.1 for Numeric Attributes Keep Format -Starts
		final String SELECT_ATTRIBUTE_TOTAL = "attribute[" + ATTRIBUTE_TOTAL + "].inputvalue";
		//Modified by DSM(Sogeti)-2018x.1.1 for Numeric Attributes Keep Format -Ends
		String strMin = DomainConstants.EMPTY_STRING;
		String strMax = DomainConstants.EMPTY_STRING;
		String strQty = DomainConstants.EMPTY_STRING;
		final String ATTRIBUTE_MAXIMUMACTUALPERCENTWEIGHT = "Maximum Actual Percent Wet";
		final String ATTRIBUTE_MINIMUMACTUALPERCENTWEIGHT = "Minimum Actual Percent Wet";

		final String SELECT_ATTRIBUTE_MINIMUMACTUALPERCENTWEIGHT = "attribute[" + ATTRIBUTE_MINIMUMACTUALPERCENTWEIGHT + "]";
		final String SELECT_ATTRIBUTE_MAXIMUMACTUALPERCENTWEIGHT = "attribute[" + ATTRIBUTE_MAXIMUMACTUALPERCENTWEIGHT + "]";
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Starts
		BigDecimal ONE_HUNDRED = new BigDecimal(100);
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Ends
		//Added by DSM(Sogeti) for 2018x.1.1 SAP BOM as Fed Defect #28126 - Starts
		boolean bProcessingLoss100Percent = false;
		//Added by DSM(Sogeti) for 2018x.1.1 SAP BOM as Fed Defect #28126 - Ends
		//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Starts
		boolean bSAPQty = false;
		//Added by DSM(Sogeti) - for 2018x.3 requirement #27166,#27207,#25923 - Ends
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Starts
		boolean isVISubs = false;
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Ends
		double dBaseQty = 100;
		double dSAPBOMBaseQty = 1000;
		double dPercentWet = 0;
		double dMinimumPercentWet = 0;
		double dMaximumPercentWet = 0;
		double dLoss = 0;
		double dDryPercent = 0;
		double dWeightWeightSAP = 0;
		double dDryWeight = 0;
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Starts
		double dTargetPercentConsumed = 0;
		double dDefaultValue = -9.99;
		double dSubsPrimaryComponentsSubstitutedWetPercentage = 0;
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Ends
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
		double dSumofDryWeight = 0;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
		//Modified by DSM(Sogeti) for 2018x.1.1 Requirement 27074 - Starts
		double dTargetWeightWet = 0;
		//Modified by DSM(Sogeti) for 2018x.1.1 Requirement 27074 - Ends
		DecimalFormat decimalformatter = new DecimalFormat(PATTERN_DECIMALFORMAT);
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Starts
		StringList slSubsPrimaryComponents = new StringList();
		StringList slSubsPrimaryComponentsSubstitutedWetPercent = new StringList();
		StringList slSubsFromType = new StringList();
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 : Ends
		StringList slBusSelect = new StringList(2);
		slBusSelect.add(DomainConstants.SELECT_ID);
		slBusSelect.add(DomainConstants.SELECT_NAME);

		StringList slRelSelect = new StringList(10);
		//Modified by DSM(Sogeti)-2018x.1.1 for Numeric Attributes Keep Format -Starts
		slRelSelect.add("attribute[" + pgV3Constants.ATTRIBUTE_QUANTITY + "].inputvalue");
		//Modified by DSM(Sogeti)-2018x.1.1 for Numeric Attributes Keep Format -Ends
		slRelSelect.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		slRelSelect.add(SELECT_ATTRIBUTE_MINIMUMACTUALPERCENTWEIGHT);
		slRelSelect.add(SELECT_ATTRIBUTE_MAXIMUMACTUALPERCENTWEIGHT);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
		slRelSelect.add(SELECT_ATTRIBUTE_TOTAL);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Starts
		slRelSelect.add(pgV3Constants.SELECT_SUBSTITUTE_PRIMARY_COMPONENTS);
		slRelSelect.add(pgV3Constants.SELECT_PRIMARY_COMPONENT_SUBSTITUTED_WET_PERCENT);
		slRelSelect.add(pgV3Constants.SELECT_SUBSTITUTE_FROM_TYPE);
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Ends
		String strTargetWetValue = DomainConstants.EMPTY_STRING;
		String strTempPhaseName = DomainConstants.EMPTY_STRING;
		String PercentWet = DomainConstants.EMPTY_STRING;
		String MinimumPercentWet = DomainConstants.EMPTY_STRING;
		String MaximumPercentWet = DomainConstants.EMPTY_STRING;
		String strTempLossValue = DomainConstants.EMPTY_STRING;
		String strDryPercentValue = DomainConstants.EMPTY_STRING;
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Starts
		Object objSubsPrimaryComponents = null;
		Object objSubsFromType = null;
		String strSubsPrimaryComponentsSubstitutedWetPercentage = DomainConstants.EMPTY_STRING;
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Ends
		Map mpTempData = null;
		MapList mlPLBOMSub = null;
		StringList slDefaultQTY = new StringList(3);
		slDefaultQTY.add(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED); //BOM Minimum QTY
		slDefaultQTY.add(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);    //BOM Maximum QTY
		slDefaultQTY.add(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);    //BOM QTY
		//Base UOM Validation
		/*if(BusinessUtil.isNullOrEmpty(strBaseUOMParent) || BusinessUtil.isNullOrEmpty(strBaseUOMChild) || !strBaseUOMChild.equalsIgnoreCase(strBaseUOMParent) ) {
			return slDefaultQTY;
		}*/

		//Modified by DSM(Sogeti) for 2018x.3 Requirements #27166,#27207,#25923 -Starts
		if (BusinessUtil.isNullOrEmpty(strBaseUOMParent) || BusinessUtil.isNullOrEmpty(strBaseUOMChild)) {

			return slDefaultQTY;
		} else if (BusinessUtil.isNotNullOrEmpty(strBaseUOMParent) && BusinessUtil.isNotNullOrEmpty(strBaseUOMChild) && ((strBaseUOMChild.equalsIgnoreCase(strBaseUOMParent)) || (pgV3Constants.BUOM_EACH.equalsIgnoreCase(strBaseUOMParent) && strBaseUOMChild.equalsIgnoreCase(pgV3Constants.BUOM_KILOGRAM)))) {

			bSAPQty = true;
		} else {
			return slDefaultQTY;
		}
		//Modified by DSM Sogeti for 2018x.3 Requirements #27166,#27207,#25923 -Ends

		if (BusinessUtil.isNotNullOrEmpty(strSAPBOMBaseQty)) {
			dSAPBOMBaseQty = parseValue(strSAPBOMBaseQty);
		}
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
		if (BusinessUtil.isNotNullOrEmpty(SumofDryWeight)) {
			dSumofDryWeight = parseValue(SumofDryWeight);
		}
		BigDecimal bdTotalDryweight = BigDecimal.valueOf(dSumofDryWeight);
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
		//Modified by DSM Sogeti for 2018x.3 Requirements #27166,#27207,#25923 -Starts
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Starts
		if (BusinessUtil.isNotNullOrEmpty(sTargetPercentConsumed)) {
			dTargetPercentConsumed = parseValue(sTargetPercentConsumed);
		}
		BigDecimal bdTargetPercentConsumed = new BigDecimal(dTargetPercentConsumed);
		//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Ends

		if (bSAPQty) {
			try {

				if (null != strPLBOMRelId) {
					String sArrRelId[] = new String[1];
					sArrRelId[0] = strPLBOMRelId;
					mlPLBOMSub = (MapList) DomainRelationship.getInfo(context, sArrRelId, slRelSelect);
					if (null != mlPLBOMSub && mlPLBOMSub.size() > 0) {
						mpTempData = (Map) mlPLBOMSub.get(0);
						//Modified by DSM(Sogeti)-2018x.1.1 for Numeric Attributes Keep Format -Starts
						PercentWet = (String) mpTempData.get("attribute[" + pgV3Constants.ATTRIBUTE_QUANTITY + "].inputvalue");
						//Modified by DSM(Sogeti)-2018x.1.1 for Numeric Attributes Keep Format -Ends
						MinimumPercentWet = (String) mpTempData.get(SELECT_ATTRIBUTE_MINIMUMACTUALPERCENTWEIGHT);
						MaximumPercentWet = (String) mpTempData.get(SELECT_ATTRIBUTE_MAXIMUMACTUALPERCENTWEIGHT);
						strTempLossValue = (String) mpTempData.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
						strDryPercentValue = (String) mpTempData.get(SELECT_ATTRIBUTE_TOTAL);
						strTargetWetValue = (String) mpTempData.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
						//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Starts
						if (bSubstitute) {
							objSubsFromType = (Object) mpTempData.get(pgV3Constants.SELECT_SUBSTITUTE_FROM_TYPE);

							if (null != objSubsFromType) {
								slSubsFromType = getInstanceList(objSubsFromType);
							}

							if (!slSubsFromType.isEmpty() && slSubsFromType.contains(pgV3Constants.TYPE_VIRTUALINTERMEDIATE)) {

								isVISubs = true;

							}
						}
						//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Ends

						if (BusinessUtil.isNotNullOrEmpty(PercentWet)) {
							dPercentWet = parseFOPQuantityValue(PercentWet);
						}
						if (BusinessUtil.isNotNullOrEmpty(MinimumPercentWet)) {
							dMinimumPercentWet = parseFOPQuantityValue(MinimumPercentWet);
						}
						if (BusinessUtil.isNotNullOrEmpty(MaximumPercentWet)) {
							dMaximumPercentWet = parseFOPQuantityValue(MaximumPercentWet);
						}
						if (BusinessUtil.isNotNullOrEmpty(strTempLossValue)) {
							dLoss = parseFOPQuantityValue(strTempLossValue);
							//Added by DSM(Sogeti) for 2018x.1.1 SAP BOM as Fed Defect #28126 - Starts
							if (dLoss == 100.0) {
								bProcessingLoss100Percent = true;
							}
							//Added by DSM(Sogeti) for 2018x.1.1 SAP BOM as Fed Defect #28126 - Starts
						}
						if (BusinessUtil.isNotNullOrEmpty(strDryPercentValue)) {
							dDryPercent = parseFOPQuantityValue(strDryPercentValue);
						}
						//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
						if (BusinessUtil.isNotNullOrEmpty(strTargetWetValue)) {
							dTargetWeightWet = parseFOPQuantityValue(strTargetWetValue);
						}
						//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
					}

					BigDecimal bdBaseQTY = BigDecimal.valueOf(dBaseQty);
					BigDecimal bdLoss = BigDecimal.valueOf((100 - dLoss));
					//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
					BigDecimal bdTargetWeightWet = BigDecimal.valueOf(dTargetWeightWet);
					//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

					if (dMinimumPercentWet > 0) {
						BigDecimal bdMinQuantityVal = BigDecimal.valueOf(dMinimumPercentWet);
						bdMinQuantityVal = bdMinQuantityVal.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));
						if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
							bdMinQuantityVal = (BigDecimal) divideBigDecimalValues(bdMinQuantityVal, bdBaseQTY);
						}
						dMinimumPercentWet = bdMinQuantityVal.doubleValue();
					}
					if (dMaximumPercentWet > 0) {
						BigDecimal bdMaxQuantityVal = BigDecimal.valueOf(dMaximumPercentWet);
						bdMaxQuantityVal = bdMaxQuantityVal.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));
						if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
							bdMaxQuantityVal = (BigDecimal) divideBigDecimalValues(bdMaxQuantityVal, bdBaseQTY);
						}
						dMaximumPercentWet = bdMaxQuantityVal.doubleValue();
					}

					if (dPercentWet > 0) {

						BigDecimal bdWeightWeightSAP = BigDecimal.valueOf(dPercentWet);
						//Modified by DSM(Sogeti) - for 2018x.2.1 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Starts
						if (isVISubs) {

							bdWeightWeightSAP = bdWeightWeightSAP.multiply(bdTargetPercentConsumed).divide(ONE_HUNDRED);

						}
						//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Ends
						if (dLoss == 100.0) {
							//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
							bdWeightWeightSAP = BigDecimal.valueOf(dSAPBOMBaseQty);
							if (bdTotalDryweight.compareTo(BigDecimal.ZERO) != 0) {
								bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdTotalDryweight);
								bdWeightWeightSAP = bdTargetWeightWet.multiply(bdWeightWeightSAP);
							} else {
								//Modified by DSM(Sogeti) for 2018x.2 Defect 24571 - Starts
								bdWeightWeightSAP = BigDecimal.valueOf(dPercentWet);
								bdWeightWeightSAP = bdWeightWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));
								if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
									bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdBaseQTY);
								}
								//Modified by DSM(Sogeti) for 2018x.2 Defect 24571 - Ends
							}
							//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
							/*
							//Modified by DSM(Sogeti) for 2018x.1.1 Requirement #25758 Starts
							bdWeightWeightSAP = BigDecimal.valueOf(parseFOPQuantityValue(PercentWet));
							//Modified by DSM(Sogeti) for 2018x.1.1 Requirement #25758 Ends
							 */
						} else if (dLoss > 0.0) {
							if (bdLoss.compareTo(BigDecimal.ZERO) != 0) {
								bdWeightWeightSAP = bdWeightWeightSAP.multiply(bdLoss);
								bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdLoss);
							}
							bdWeightWeightSAP = bdWeightWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));
							if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
								bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdBaseQTY);
							}
						} else {
							bdWeightWeightSAP = bdWeightWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));
							if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
								bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdBaseQTY);
							}
						}
						dWeightWeightSAP = bdWeightWeightSAP.doubleValue();
					}
					//Modified by DSM(Sogeti) for 2018x.1.1 SAP BOM as Fed Defect #28126 - Starts
					if (dDryPercent > 0 && !bProcessingLoss100Percent) {
						//Modified by DSM(Sogeti) for 2018x.1.1 SAP BOM as Fed Defect #28126 - Ends
						BigDecimal bdDryWeightSAP = BigDecimal.valueOf(dDryPercent);
						bdDryWeightSAP = bdDryWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));
						if (bdLoss.compareTo(BigDecimal.ZERO) != 0) {
							bdDryWeightSAP = (BigDecimal) divideBigDecimalValues(bdDryWeightSAP, bdLoss);
						}
						dWeightWeightSAP = bdDryWeightSAP.doubleValue();
					}
					//Modified by DSM(Sogeti) for 2018x.1.1 Requirement #27074 - Starts

					//Modified by DSM(Sogeti) for 2018x.2.1 Requirement #31946 - Starts
					if (bSubstitute) {
						//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Starts
						objSubsPrimaryComponents = (Object) mpTempData.get(pgV3Constants.SELECT_SUBSTITUTE_PRIMARY_COMPONENTS);
						if (null != objSubsPrimaryComponents) {
							slSubsPrimaryComponents = getInstanceList(objSubsPrimaryComponents);
						}

						if (slSubsPrimaryComponents.size() > 1) {
							dWeightWeightSAP = dDefaultValue;
							dMaximumPercentWet = dDefaultValue;
							dMinimumPercentWet = dDefaultValue;

						} else {
							strSubsPrimaryComponentsSubstitutedWetPercentage = (String) mpTempData.get(pgV3Constants.SELECT_PRIMARY_COMPONENT_SUBSTITUTED_WET_PERCENT);
							if (BusinessUtil.isNotNullOrEmpty(strSubsPrimaryComponentsSubstitutedWetPercentage)) {
								dSubsPrimaryComponentsSubstitutedWetPercentage = parseFOPQuantityValue(strSubsPrimaryComponentsSubstitutedWetPercentage);
							}
							BigDecimal bdSubsPrimaryComponentsSubstitutedWetPercentage = BigDecimal.valueOf(dSubsPrimaryComponentsSubstitutedWetPercentage);
							if (bdSubsPrimaryComponentsSubstitutedWetPercentage.compareTo(BigDecimal.ZERO) != 0) {

								dWeightWeightSAP = dDefaultValue;
								dMaximumPercentWet = dDefaultValue;
								dMinimumPercentWet = dDefaultValue;

							} else {
								//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 #33151 Requirement #34144 #34131 #34104 -Ends
								if (BusinessUtil.isNotNullOrEmpty(strTargetWetValue)) {
									//Modified by DSM(Sogeti) for 2018x.2.1 Requirement #31946 - Ends
									if (bdBaseQTY.compareTo(BigDecimal.ZERO) > 0) {
										dTargetWeightWet = parseFOPQuantityValue(strTargetWetValue);
										BigDecimal bdWeightWeightSAP = BigDecimal.valueOf(dSAPBOMBaseQty);
										bdWeightWeightSAP = bdWeightWeightSAP.divide(bdBaseQTY);
										bdWeightWeightSAP = (bdWeightWeightSAP.multiply(BigDecimal.valueOf(dTargetWeightWet))).setScale(3, RoundingMode.HALF_UP);
										dWeightWeightSAP = bdWeightWeightSAP.doubleValue();
									}
								}
							}
						}
					}
					//Modified by DSM(Sogeti) for 2018x.1.1 Requirement #27074 - Ends
					if (dMinimumPercentWet <= 0 || dMinimumPercentWet > 9999999999.999) {
						strMin = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
					} else {
						strMin = String.valueOf(decimalformatter.format(dMinimumPercentWet));
					}
					if (dMaximumPercentWet <= 0 || dMaximumPercentWet > 9999999999.999) {
						strMax = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
					} else {
						strMax = String.valueOf(decimalformatter.format(dMaximumPercentWet));
					}
					if (dWeightWeightSAP <= 0 || dWeightWeightSAP > 9999999999.999) {
						strQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
					} else {
						strQty = String.valueOf(decimalformatter.format(dWeightWeightSAP));
					}
					slReturn.add(strMin);
					slReturn.add(strMax);
					slReturn.add(strQty);
				}
				//Dry Wt for SAP BOM Base Qty = Dry % * SAP BOM Base Qty
				//Wet Weight for SAP BOM Base Qty= Dry Weight for SAP BOM Base Qty/ ( 1- % Loss)
			} catch (Exception e) {
				e.printStackTrace();
				//Sent the default QTY data
				return slDefaultQTY;
			}
			//Modified by DSM Sogeti for 2018x.3 Requirements #27166,#27207,#25923 -Ends
		}
		return slReturn;
	}


	String getFOPSubs(Context context, String strRelID, MapList mlPLBOMParts) throws FrameworkException {


		String sTest = "";

		StringList selectStmtsRel = new StringList("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.id");
		selectStmtsRel.add("from[" + DomainConstants.RELATIONSHIP_ALTERNATE + "].to.id");

		selectStmtsRel.add("relationship");


		String sArrRelId[] = new String[1];

		sArrRelId[0] = strRelID;

		MapList mlEBOMSub = (MapList) DomainRelationship.getInfo(context, sArrRelId, selectStmtsRel);

		if (null != mlEBOMSub && mlEBOMSub.size() > 0) {

			Map mpTemp = (Map) mlEBOMSub.get(0);
			String substituteInterId = (String) mpTemp.get("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.id");
			String altenateInterId = (String) mpTemp.get("from[" + DomainConstants.RELATIONSHIP_ALTERNATE + "].to.id");
			String strRelName = (String) mpTemp.get("relationship");
			String strIsDirectComp = "true";
			int iCounter = 1;
			char cAltItem1 = '9';
			char cAltItem2 = 'A';
			if ((null != substituteInterId && "true".equals(strIsDirectComp)) || (!(DomainConstants.RELATIONSHIP_ALTERNATE.equals(strRelName)) && (BusinessUtil.isNotNullOrEmpty(altenateInterId)) && "true".equals(strIsDirectComp))) {
				if (iCounter > 0) {
					if (cAltItem2 == 'Z') {
						cAltItem2 = 'A';
						cAltItem1--;
					} else {
						cAltItem2++;
					}
				}
				sTest = cAltItem1 + "" + cAltItem2;
				iCounter++;
			} else {
				if (pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE.equals(strRelName) || DomainConstants.RELATIONSHIP_ALTERNATE.equals(strRelName)) {
					sTest = cAltItem1 + "" + cAltItem2;
				} else {
					sTest = "";

				}
			}

		}
		return sTest;
	}

	/*
	/**
	 *
	 * buildFOPPLBOMArray- Method is used to build the String Array as per SAP requirement for Formulation type.
	 * @param context the eMatrix <code>Context</code> object
	 * @param String,String,String,String,double
	 * @return String[]
	 * @throws Exception
	 */

	public String[] buildFOPPLBOMArray(String strParentName, String strLoss, String strActualWeightWet, String strBOMBaseQty, double dTotal_Dry_Weight, String strSubFlag, String strQty2, String strFindNumb, String strChildEffectivDate, String sChildName, String sQuantity, String strMax, String sTarget, String strMin, String strParentEffectivDate, String sQuantity3, String strPosInd, String strFILBaseQty, String strpgPhaseBOMQty, String strGroupTmp, String strFormulationChildId) {


		String[] saTemp = new String[pgV3Constants.INDEX_BOM_ARRAY_SIZE];

		try {

			if (BusinessUtil.isNullOrEmpty(sQuantity))
				sQuantity = "1";
			if (BusinessUtil.isNullOrEmpty(strMax))
				strMax = "1";
			if (BusinessUtil.isNullOrEmpty(strMin))
				strMin = "1";
			if (BusinessUtil.isNullOrEmpty(strBOMBaseQty))
				strBOMBaseQty = "1";
			if (BusinessUtil.isNullOrEmpty(strFILBaseQty))
				strFILBaseQty = "1";
			if (BusinessUtil.isNullOrEmpty(strpgPhaseBOMQty))
				strpgPhaseBOMQty = "1";
			if (BusinessUtil.isNullOrEmpty(sTarget))
				sTarget = "1";

			double dFil = 1;
			double dMinQty = 1;
			double dPgPhaseBOMQty = 1;
			double dBOMBaseQty = 1;
			double dTargetQty = 1;
			double dMaxQty = 1;
			double dFilBaseQty = 1;
			double dPCTBase = 1;
			double dQuantity = 1;


			dMinQty = parseValue(strMin);
			dPgPhaseBOMQty = parseValue(strpgPhaseBOMQty);
			dBOMBaseQty = parseValue(strBOMBaseQty);
			dTargetQty = parseValue(sTarget);
			dMaxQty = parseValue(strMax);
			dFilBaseQty = parseValue(strFILBaseQty);
			dPCTBase = dFilBaseQty;
			dQuantity = parseValue(sQuantity);


			if (BusinessUtil.isNotNullOrEmpty(strChildEffectivDate))
				//Modified by DSM(Sogeti) - 2015x.4 Code migrated from pgIPMSAPBOM.jar - Starts
				//strChildEffectivDate = DateUtil.convertPLMDateToSAP(strChildEffectivDate);
				strChildEffectivDate = convertPLMDateToSAP(strChildEffectivDate);
			//Modified by DSM(Sogeti) - 2015x.4 Code migrated from pgIPMSAPBOM.jar - Ends

			if (BusinessUtil.isNotNullOrEmpty(strParentEffectivDate))
				//Modified by DSM(Sogeti) - 2015x.4 Code migrated from pgIPMSAPBOM.jar - Starts
				//strParentEffectivDate = DateUtil.convertPLMDateToSAP(strParentEffectivDate);
				strParentEffectivDate = convertPLMDateToSAP(strParentEffectivDate);
			//Modified by DSM(Sogeti) - 2015x.4 Code migrated from pgIPMSAPBOM.jar - Ends


			double dFormula_Card_Minimum_Quantity = dBOMBaseQty / (dFil / (dPgPhaseBOMQty * dMinQty));
			double dFormula_Card = dBOMBaseQty / (dFilBaseQty * dTargetQty);
			double dFormula_Card_Maximum_Quantity = dBOMBaseQty / (dFilBaseQty * dMaxQty);
			double dIPS_Quantity = dBOMBaseQty / (dPCTBase * dQuantity);

			//Rounding to 3 decimal
			dFormula_Card_Minimum_Quantity = (double) Math.round(dFormula_Card_Minimum_Quantity * 1000) / 1000;
			dFormula_Card = (double) Math.round(dFormula_Card * 1000) / 1000;
			dFormula_Card_Maximum_Quantity = (double) Math.round(dFormula_Card_Maximum_Quantity * 1000) / 1000;
			dIPS_Quantity = (double) Math.round(dIPS_Quantity * 1000) / 1000;

			saTemp[pgV3Constants.INDEX_ALT_BOM] = pgV3Constants.VALUE_ALT_BOM;
			saTemp[pgV3Constants.INDEX_BOM_STATUS] = pgV3Constants.VALUE_BOM_STATUS;
			saTemp[pgV3Constants.INDEX_ITEM_CATEGORY] = pgV3Constants.VALUE_ITEM_CATEGORY;
			saTemp[pgV3Constants.INDEX_BOM_TEXT] = strParentName;
			saTemp[pgV3Constants.INDEX_ALT_BOM_TEXT] = pgV3Constants.VALUE_ALT_BOM_TEXT;
			saTemp[pgV3Constants.INDEX_MATERIAL_NUMBER] = strParentName;
			saTemp[pgV3Constants.INDEX_BOM_USAGE] = pgV3Constants.VALID_CODE;
			saTemp[pgV3Constants.INDEX_VALID_FROM_DATE_PARENT] = strParentEffectivDate;
			saTemp[pgV3Constants.INDEX_COMPONENT_NAME] = sChildName;
			saTemp[pgV3Constants.INDEX_QUANTITY] = sQuantity;
			saTemp[pgV3Constants.INDEX_UPPER_LIMIT] = strMax;
			saTemp[pgV3Constants.INDEX_TARGET] = sQuantity;
			saTemp[pgV3Constants.INDEX_LOWER_LIMIT] = strMin;
			saTemp[pgV3Constants.INDEX_FIND_NUMBER] = strFindNumb;
			saTemp[pgV3Constants.INDEX_VALID_FROM_DATE_CHILD] = strChildEffectivDate;
			saTemp[pgV3Constants.INDEX_PHASE_NAME] = DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_CONFIRMED_QUANTITY] = strBOMBaseQty;
			saTemp[pgV3Constants.INDEX_SORT_STRING] = strPosInd;
			saTemp[pgV3Constants.INDEX_BOM_ITEM_NUMBER] = DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_SUBSTITUTE_FLAG] = strSubFlag;
			saTemp[pgV3Constants.INDEX_FC_MIN_QTY] = dFormula_Card_Minimum_Quantity + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_FORMULA_CARD] = dFormula_Card + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_FC_MAX_QTY] = dFormula_Card_Maximum_Quantity + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_IPS_QTY] = dIPS_Quantity + DomainConstants.EMPTY_STRING;


			saTemp[pgV3Constants.INDEX_OBJECT_ID] = strFormulationChildId;

			if (saTemp[pgV3Constants.INDEX_UPPER_LIMIT] == null || saTemp[pgV3Constants.INDEX_UPPER_LIMIT].equals("")) {
				saTemp[pgV3Constants.INDEX_RANGE_FLAG] = DomainConstants.EMPTY_STRING;
			} else
				saTemp[pgV3Constants.INDEX_RANGE_FLAG] = pgV3Constants.VALUE_RANGE_FLAG; // "X"


		} catch (Exception e) {
			e.printStackTrace();
			//Not throwing exception here as the calling method is expecting String Array.
			//In this case Blank String array would be returned which is being handled properly by calling method.
			//throw e;
		}


		try {

			double dloss = 1;
			double dActualWeightWet = 1;
			double dBOMBaseQty = 1;
			double dPrcent = 100;


			dActualWeightWet = parseValue(strActualWeightWet);
			dloss = parseValue(strLoss);


			try {
				dBOMBaseQty = parseValue(strBOMBaseQty);
			} catch (Exception e) {
				dBOMBaseQty = 1000;
			}

			// FOP Calculation Starts
			double dLossPercent = (dPrcent - dloss);

			double dDry_Weight = (dPrcent - dloss) * dActualWeightWet;
			dDry_Weight = (dDry_Weight) / dPrcent;

			double dDry_weight_Percent = (dDry_Weight / dTotal_Dry_Weight);
			dDry_weight_Percent = (dDry_weight_Percent * dPrcent);

			double dDry_Weight_SAP = (dDry_weight_Percent * dBOMBaseQty);
			dDry_Weight_SAP = (dDry_Weight_SAP) / dPrcent;

			double dDry_weight_Quantity = (dDry_Weight_SAP / dLossPercent);
			dDry_weight_Quantity = (dDry_weight_Quantity * dPrcent);

			// FOP Calculation Ends

			saTemp[pgV3Constants.INDEX_BOM_TEXT] = strParentName;
			saTemp[pgV3Constants.INDEX_QUANTITY] = dDry_weight_Quantity + DomainConstants.EMPTY_STRING;
			saTemp[pgV3Constants.INDEX_TARGET] = dDry_Weight_SAP + DomainConstants.EMPTY_STRING;

		} catch (Exception e) {
			e.printStackTrace();
			saTemp = null;
		}

		return saTemp;
	} // end of method buildFOPPLBOMArray

	/**
	 * ReadSAPDataTOJSP- Method is used to call the functionality from jsp in case of Failure View.
	 *
	 * @param context  the eMatrix <code>Context</code> object
	 * @param String[]
	 * @return Map
	 * @throws Exception
	 */


	public synchronized Map ReadSAPDataTOJSP(Context context, String[] methodArgs) throws Exception {

		Map mapFinalData = new HashMap();
		Map mapFinalData2 = new HashMap();

		methodArgs = new String[4];
		String strReturnMsg = DomainConstants.EMPTY_STRING;
		String sPartObjectType = DomainConstants.EMPTY_STRING;
		String sPartObjectName = DomainConstants.EMPTY_STRING;
		String sPartObjectRev = DomainConstants.EMPTY_STRING;
		String sPartObjectID = DomainConstants.EMPTY_STRING;

		try {
			sPartObjectID = methodArgs[0];
			sPartObjectType = methodArgs[1];
			sPartObjectName = methodArgs[2];
			sPartObjectRev = methodArgs[3];

			//if (BusinessUtil.isNotNullOrEmpty(sPartObjectID) ) {
			pgEDeliverBOMToSAP_mxJPO sapLoad = new pgEDeliverBOMToSAP_mxJPO();

			if (BusinessUtil.isNotNullOrEmpty(sPartObjectID) && sapLoad.doTypeCheckForeDelivery(context, methodArgs) && sapLoad.doPlantCheckForeDelivery(context, sPartObjectID)) {

				ArrayList alPartObjectsWithGroupAndQty = sapLoad.performDataReadFromEnovia(context, sPartObjectID);

				Iterator itr = alPartObjectsWithGroupAndQty.iterator();

				int iCounter = 0;

				while (itr.hasNext()) {

					String[] arraytest = (String[]) itr.next();

					mapFinalData = new HashMap();
					for (int i = 0; i < arraytest.length; i++) {

						if (arraytest[i] == null) {

							arraytest[i] = "";
						}
						String sDemoTemp = "";
						if (i == pgV3Constants.INDEX_MATERIAL_NUMBER) {

							mapFinalData.put("INDEX_MATERIAL_NUMBER", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_BOM_USAGE) {

							mapFinalData.put("INDEX_BOM_USAGE", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_VALID_FROM_DATE_PARENT) {

							mapFinalData.put("INDEX_VALID_FROM_DATE_PARENT", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_ALT_BOM) {
							mapFinalData.put("INDEX_ALT_BOM", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_BOM_TEXT) {
							mapFinalData.put("INDEX_BOM_TEXT", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_ALT_BOM_TEXT) {
							mapFinalData.put("INDEX_ALT_BOM_TEXT", (String) arraytest[i]);


						}
						if (i == pgV3Constants.INDEX_CONFIRMED_QUANTITY) {
							mapFinalData.put("INDEX_CONFIRMED_QUANTITY", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_BOM_STATUS) {

							mapFinalData.put("INDEX_BOM_STATUS", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_COMPONENT_NAME) {
							mapFinalData.put("INDEX_COMPONENT_NAME", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_ITEM_CATEGORY) {
							mapFinalData.put("INDEX_ITEM_CATEGORY", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_VALID_FROM_DATE_CHILD) {
							mapFinalData.put("INDEX_VALID_FROM_DATE_CHILD", (String) arraytest[i]);


						}
						if (i == pgV3Constants.INDEX_SUBSTITUTE_FLAG) {
							mapFinalData.put("INDEX_SUBSTITUTE_FLAG", (String) arraytest[i]);


						}
						if (i == pgV3Constants.INDEX_USAGE_PROBABILITY) {
							mapFinalData.put("INDEX_USAGE_PROBABILITY", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_RANGE_FLAG) {
							mapFinalData.put("INDEX_RANGE_FLAG", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_TARGET) {
							mapFinalData.put("INDEX_TARGET", (String) arraytest[i]);

						}
						if (i == pgV3Constants.INDEX_QUANTITY) {
							mapFinalData.put("INDEX_QUANTITY", (String) arraytest[i]);

						}

					}
					mapFinalData2.put(iCounter, mapFinalData);
					iCounter++;
				}

				int iRow = mapFinalData2.size();
				int iColumn = 5;
				String strArrSAPData[][] = null;
				for (int j = 0; j < iRow; j++) {
					Map mpTemp = (Map) mapFinalData2.get(j);
					iColumn = mpTemp.size();
					strArrSAPData = new String[iRow][iColumn];

					strArrSAPData[j][0] = (String) mpTemp.get("INDEX_MATERIAL_NUMBER");

					strArrSAPData[j][1] = (String) mpTemp.get("INDEX_VALID_FROM_DATE_PARENT");

					strArrSAPData[j][2] = (String) mpTemp.get("INDEX_ALT_BOM");

					strArrSAPData[j][3] = (String) mpTemp.get("INDEX_BOM_TEXT");

					strArrSAPData[j][4] = (String) mpTemp.get("INDEX_CONFIRMED_QUANTITY");

					strArrSAPData[j][5] = (String) mpTemp.get("INDEX_BOM_STATUS");

					strArrSAPData[j][6] = (String) mpTemp.get("INDEX_COMPONENT_NAME");

					strArrSAPData[j][7] = (String) mpTemp.get("INDEX_ITEM_CATEGORY");

					strArrSAPData[j][8] = (String) mpTemp.get("INDEX_VALID_FROM_DATE_CHILD");

					strArrSAPData[j][9] = (String) mpTemp.get("INDEX_SUBSTITUTE_FLAG");

					strArrSAPData[j][10] = (String) mpTemp.get("INDEX_USAGE_PROBABILITY");

					strArrSAPData[j][11] = (String) mpTemp.get("INDEX_RANGE_FLAG");

					strArrSAPData[j][12] = (String) mpTemp.get("INDEX_TARGET");

					strArrSAPData[j][13] = (String) mpTemp.get("INDEX_QUANTITY");

				}
			}

		} catch (Exception ex) {

			ex.printStackTrace();

		}
		return mapFinalData2;
	}

	/**
	 * getBOMQTYDataFPP- Method is used to get the Quantity values for FPP when BUOM is IT.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param Map
	 * @return StringList
	 * @throws Exception Added as per defect # 10187
	 */
	public Map getBOMQTYDataFPP(Context context, Map FPPmap, boolean isReshipperSub, StringList slSubstituteInterIdReshipper, String strBUOM) throws Exception {
		//Modified by DSM(Sogeti) - 2015x.5.1 INCIDENT# INC1486665
		BigDecimal bdBOMBaseQty = BigDecimal.ZERO;
		BigDecimal bdInterObjRelAttrQty = BigDecimal.ZERO;
		BigDecimal bdBOMQuantityCheck = new BigDecimal("9999999999.999");
		Map mpReturn = new HashMap();
		float fIntermediateSAPQty = 1;
		Vector vc = new Vector();
		StringList strListQtyValues = new StringList(1);
		String strCalcQTY = null;
		DomainObject doCUPObjInter = null;
		DomainObject dObjFPP = null;

		StringList slObjectSelect = new StringList(5);
		slObjectSelect.addElement(DomainConstants.SELECT_ID);
		slObjectSelect.addElement(DomainConstants.SELECT_NAME);
		slObjectSelect.addElement(DomainConstants.SELECT_TYPE);
		slObjectSelect.addElement(DomainConstants.SELECT_REVISION);
		slObjectSelect.addElement(DomainConstants.SELECT_LEVEL);

		StringList relationshipSelects = new StringList(2);
		relationshipSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		relationshipSelects.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);

		MapList mlFPPChildData = new MapList();
		boolean bFlatReshipper = false;
		String strInterCUPID = DomainConstants.EMPTY_STRING;

		try {

			MapList objectList = (MapList) FPPmap.get("objectList");
			HashMap paramList = (HashMap) FPPmap.get("paramList");
			String strFPPID = (String) paramList.get("objectId");
			dObjFPP = DomainObject.newInstance(context, strFPPID);

			if (isReshipperSub && slSubstituteInterIdReshipper.size() > 0) {

				int iSizeOfSubInterm = slSubstituteInterIdReshipper.size();
				for (int iSubInter = 0; iSubInter < iSizeOfSubInterm; iSubInter++) {
					strInterCUPID = (String) slSubstituteInterIdReshipper.get(iSubInter);
					doCUPObjInter = DomainObject.newInstance(context, strInterCUPID);
					StringList slIntermediateObjectReshipperType = doCUPObjInter.getInfoList(context, "from[EBOM].to.type");
					if (slIntermediateObjectReshipperType.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART) || slIntermediateObjectReshipperType.contains(pgV3Constants.TYPE_PGINNERPACKUNITPART)) {
						mlFPPChildData = doCUPObjInter.getRelatedObjects(context,
								pgV3Constants.RELATIONSHIP_EBOM,   // rel pattern
								pgV3Constants.TYPE_PGCONSUMERUNITPART + "," + pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_PGINNERPACKUNITPART,  // type pattern
								slObjectSelect,             // object selects
								relationshipSelects,           //  rel selects
								false,                             // to side
								true,                              // from side
								(short) 0,                          //  recursion level
								null,                       // object where clause
								null);                             //  rel where clause
					} else {
						bFlatReshipper = true;

						mlFPPChildData = dObjFPP.getRelatedObjects(context,
								pgV3Constants.RELATIONSHIP_EBOM,  // Rel Pattern

								pgV3Constants.TYPE_PGCONSUMERUNITPART + "," + pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_PGINNERPACKUNITPART,              //Type Pattern

								slObjectSelect,                  //  bus select

								relationshipSelects,             //   Rel select

								false,                             // to side

								true,                           // from side

								(short) 0,                       //  recursion level

								null,                          // Object Where Clause

								null);                       // Rel Where Clause
					}

				}

			} else {
				mlFPPChildData = dObjFPP.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_EBOM,  // Rel Pattern

						pgV3Constants.TYPE_PGCONSUMERUNITPART + "," + pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_PGINNERPACKUNITPART,              //Type Pattern

						slObjectSelect,                  //  bus select

						relationshipSelects,             //   Rel select

						false,                             // to side

						true,                           // from side

						(short) 0,                       //  recursion level

						null,                          // Object Where Clause

						null);                       // Rel Where Clause
			}
			float fltBCF = 0;
			StringList slBCF = new StringList(1);
			slBCF.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);

			Map mapBCF = (Map) dObjFPP.getInfo(context, slBCF);

			float flBOMBaseQty = 0;
			float flPCTBaseQty = 0;

			String strBOMBaseQty = (String) mapBCF.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
			bdBOMBaseQty = new BigDecimal(strBOMBaseQty);

			if (null != objectList && objectList.size() > 0) {
				BigDecimal bdBOMQty = BigDecimal.ZERO;
				String strBOMQty = "";
				String strObjectID = "";
				String strConnectionID = null;
				String strChildName = null;
				String strChildID = null;
				String strChildType = null;
				String strInterObjRelAttrQty = null;
				String strInterQTY = null;
				String strLevel = null;
				String level = null;
				String strDenominator = null;
				float flNumber = 0.0f;
				float fdenominator = 0.0f;
				BigDecimal bdNumerator = BigDecimal.ZERO;
				BigDecimal bdDenominator = BigDecimal.ZERO;
				int sizeOfChildData = mlFPPChildData.size();
				Map mapTempInter = null;
				Map mapTemp = null;
				int denoLevel = 0;
				float fTemp = 1.0f;
				float fTemp1 = 1.0f;
				int iSizeOfChildDataCOP = sizeOfChildData;
				Map mpTempInterExcludingCOPToCOP = null;
				MapList mlFPPChildDataExcludingCOPTOCOP = new MapList();
				Map mpPreviousLevelChildType = new HashMap();
				// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
				String strTypeName = null;
				String strFABExpandBOMValue = null;
				String strExpandFABQty = null;
				BigDecimal bdExpandFABBOMQuantity = BigDecimal.ZERO;
				// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
				for (int i = 0; i < iSizeOfChildDataCOP; i++) {
					mapTempInter = (Map) mlFPPChildData.get(i);
					String strRelAttrQty = (String) mapTempInter.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
					String strObjectType = (String) mapTempInter.get(DomainConstants.SELECT_TYPE);
					String strObjectId = (String) mapTempInter.get(DomainConstants.SELECT_ID);
					mpPreviousLevelChildType.put(i + 1, strObjectType);
					if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)) {
						String strPreviousLevelType = (String) mpPreviousLevelChildType.get(i);
						if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPreviousLevelType)) {
							sizeOfChildData--;
							continue;
						}
					}
					mlFPPChildDataExcludingCOPTOCOP.add(mapTempInter);
				}
				float[] floatArrQTY = null;
				float[] floatArrDenominator = null;
				String[] strArrLevel = null;
				if (isReshipperSub && !bFlatReshipper) {
					floatArrQTY = new float[sizeOfChildData + 1];
					floatArrDenominator = new float[sizeOfChildData + 1];
					strArrLevel = new String[sizeOfChildData + 1];
				} else {
					floatArrQTY = new float[sizeOfChildData];
					floatArrDenominator = new float[sizeOfChildData];
					strArrLevel = new String[sizeOfChildData];
				}
				int iFPPChildDataCOPTOCOPRelSize = mlFPPChildDataExcludingCOPTOCOP.size();
				for (int i = 0; i < iFPPChildDataCOPTOCOPRelSize; i++) {
					mpTempInterExcludingCOPToCOP = (Map) mlFPPChildDataExcludingCOPTOCOP.get(i);
					String strRelAttrQty = (String) mpTempInterExcludingCOPToCOP.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
					if (isReshipperSub && !bFlatReshipper) {
						floatArrQTY[0] = 1.0f;
						int iSizeOfChildData = sizeOfChildData + 1;
						strArrLevel[sizeOfChildData] = String.valueOf(iSizeOfChildData);
						floatArrQTY[i + 1] = (Float.valueOf(strRelAttrQty)).floatValue();
						strArrLevel[i] = (String) mpTempInterExcludingCOPToCOP.get("level");
					} else {
						floatArrQTY[i] = (Float.valueOf(strRelAttrQty)).floatValue();
						strArrLevel[i] = (String) mpTempInterExcludingCOPToCOP.get("level");
					}

				}

				//calculate denominator & prepare map for key level & corresponding denominator value
				int iCount = 0;

				if (isReshipperSub && !bFlatReshipper) {
					iCount = sizeOfChildData;
				} else {
					iCount = sizeOfChildData - 1;
				}
				Map denominatorMap = new HashMap();
				String strKey = null;
				for (int i = 0; i < floatArrQTY.length; i++) {
					fTemp = 1.0f;
					for (int j = iCount; j > i; j--) {
						fTemp1 = floatArrQTY[j] * fTemp;
						fTemp = fTemp1;
					}
					floatArrDenominator[i] = fTemp;
					strKey = "level" + strArrLevel[i];
					denominatorMap.put(strKey, floatArrDenominator[i]);
				}
				fIntermediateSAPQty = (float) denominatorMap.get("level1");
				for (int i = 0; i < objectList.size(); i++) {
					strBOMQty = "";
					//Added by DSM Sogeti for 2018x.5_March Defect #38012 - Starts
					bdBOMQty = BigDecimal.ZERO;
					//Added by DSM Sogeti for 2018x.5_March Defect #38012 - Ends
					// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
					strFABExpandBOMValue = "False";
					strExpandFABQty = "";
					// Added by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
					if (pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM)) {
						strBOMQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
					} else {
						mapTemp = (Map) objectList.get(i);
						strConnectionID = (String) mapTemp.get("id[connection]");
						strChildName = (String) mapTemp.get("name");
						strChildID = (String) mapTemp.get("id");
						strChildType = (String) mapTemp.get("type");
						strInterObjRelAttrQty = DomainRelationship.getAttributeValue(context, strConnectionID, pgV3Constants.ATTRIBUTE_QUANTITY);
						strLevel = (String) mapTemp.get("IntermediateLevel");

						bdInterObjRelAttrQty = new BigDecimal(strInterObjRelAttrQty);

						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
						strTypeName = (String) mapTemp.get("Type");
						if (UIUtil.isNotNullAndNotEmpty(strTypeName)
								&& pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strTypeName)) {
							strFABExpandBOMValue = (String) mapTemp.get("FAB_ExpandBOMonSAPBOMasFed");
							
							strExpandFABQty = (String) mapTemp.get("FAB_ExpandBOMQuantity");
						}
						// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
						if (null != strChildID && strChildID.length() > 0 && bdInterObjRelAttrQty.compareTo(BigDecimal.ZERO) != 0) {
							bdNumerator = bdBOMBaseQty.multiply(bdInterObjRelAttrQty);
							// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Starts
							if (UIUtil.isNotNullAndNotEmpty(strFABExpandBOMValue)
									&& "TRUE".equalsIgnoreCase(strFABExpandBOMValue)) {
								bdExpandFABBOMQuantity = new BigDecimal(strExpandFABQty);
								bdNumerator = bdNumerator.multiply(bdExpandFABBOMQuantity);
							}
							// Added by (DSM) Sogeti for 22x.03 Defect 52121 & 52122 - Ends
							fdenominator = (float) denominatorMap.get("level" + strLevel);

							strDenominator = String.valueOf(fdenominator);
							//calculate BOM Quantity
							bdDenominator = new BigDecimal(strDenominator);
							if (bdDenominator.compareTo(BigDecimal.ZERO) != 0) {
								bdBOMQty = bdNumerator.divide(bdDenominator, 3, RoundingMode.HALF_UP);
							}
						}

						strBOMQty = String.valueOf(bdBOMQty);
						if ((bdBOMQty.compareTo(BigDecimal.ZERO) == 0) || (bdBOMQty.compareTo(bdBOMQuantityCheck) > 0)) {
							strBOMQty = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
						}
					}
					vc.add(strBOMQty);
				}
			}

			if (null != vc && vc.size() > 0) {


				for (int k = 0; k < vc.size(); k++) {
					strCalcQTY = (String) vc.get(k);
					if (BusinessUtil.isNullOrEmpty(strCalcQTY)) {
						strCalcQTY = DomainConstants.EMPTY_STRING;
					}
					strListQtyValues.add(strCalcQTY);
				}

			}


		} catch (Exception ex) {
			ex.printStackTrace();
		}

		mpReturn.put("strListQtyValues", strListQtyValues);
		mpReturn.put("fIntermediateSAPQty", fIntermediateSAPQty);
		//return strListQtyValues;
		return mpReturn;

	}


	/**
	 * getBOMData- Method is used get BOM data of FC, FP, PSUB to be send to SAP.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param Map     with parent object details
	 * @return String[][] of BOM object details
	 * @throws Exception
	 */
	public String[][] getBOMData(Context context, Map mapPartDetails) {

		String[][] saBOM = null;
		String strValidFromDate = DomainConstants.EMPTY_STRING;
		String strBOMBaseQty = DomainConstants.EMPTY_STRING;
		String strStatus = DomainConstants.EMPTY_STRING;
		String strBOMUsage = DomainConstants.EMPTY_STRING;
		String strObjectID = DomainConstants.EMPTY_STRING;
		String strObjectName = DomainConstants.EMPTY_STRING;
		String strObjectRev = DomainConstants.EMPTY_STRING;
		String strChildType = DomainConstants.EMPTY_STRING;
		String strChildName = DomainConstants.EMPTY_STRING;
		String strChildRev = DomainConstants.EMPTY_STRING;
		String strPgFPC = DomainConstants.EMPTY_STRING;
		String strChildObjID = DomainConstants.EMPTY_STRING;
		String strFindNumber = DomainConstants.EMPTY_STRING;
		String strRelId = DomainConstants.EMPTY_STRING;
		String strQuantity = DomainConstants.EMPTY_STRING;
		String strTarget = DomainConstants.EMPTY_STRING;
		String strLowerLim = DomainConstants.EMPTY_STRING;
		String strUpperLim = DomainConstants.EMPTY_STRING;
		String strPosIndicator = DomainConstants.EMPTY_STRING;
		String strOptComponent = DomainConstants.EMPTY_STRING;
		String strComment = DomainConstants.EMPTY_STRING;
		String strValidFromDateComp = DomainConstants.EMPTY_STRING;
		String strSubstFlag = DomainConstants.EMPTY_STRING;
		String strBOMNumber = DomainConstants.EMPTY_STRING;
		String strComponentNumber = DomainConstants.EMPTY_STRING;
		String strPhaseName = DomainConstants.EMPTY_STRING;
		String strPhaseObjID = DomainConstants.EMPTY_STRING;
		DomainObject dObjPhase = null;
		MapList mlPhaseChildObjList = null;
		MapList mlFirstLevelData = null;
		Map hmPhaseMap = null;
		Map hmPhaseChildMap = null;
		String strChildBUOM = DomainConstants.EMPTY_STRING;
		DomainObject dObjForTopNodePart = null;

		try {
			strObjectName = (String) mapPartDetails.get(DomainConstants.SELECT_NAME);
			strObjectRev = (String) mapPartDetails.get(DomainConstants.SELECT_REVISION);
			strObjectID = (String) mapPartDetails.get(DomainConstants.SELECT_ID);
			dObjForTopNodePart = DomainObject.newInstance(context, strObjectID);

			strBOMBaseQty = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
			strStatus = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
			strParentBUOM = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
			strParentType = (String) mapPartDetails.get(DomainConstants.SELECT_TYPE);

			if (strStatus.equals(pgV3Constants.RANGE_ATTRIBUTE_STATUS_PLANNING))
				strBOMUsage = "2";
			else if (!strStatus.equals(pgV3Constants.RANGE_ATTRIBUTE_PGBOMBASEQUANTITY_EXPERIMENTAL))
				strBOMUsage = "3";

			strValidFromDate = (String) mapPartDetails.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
			//strValidFromDate = DateUtil.convertPLMDateToSAP(strValidFromDate);
			strValidFromDate = convertPLMDateToSAP(strValidFromDate);

			_ParentMatlNumber = strObjectName;
			//strParentGCAS = strObjectName;
			strParentRev = strObjectRev;

			StringList slPhaseBusSelect = new StringList(2);
			slPhaseBusSelect.add(DomainConstants.SELECT_NAME);
			slPhaseBusSelect.add(DomainConstants.SELECT_ID);

			mlFirstLevelData = dObjForTopNodePart.getRelatedObjects(context,
					pgV3Constants.RELATIONSHIP_EBOM,    // rel pattern
					pgV3Constants.TYPE_PGPHASE,         // type pattern
					//SL_OBJECT_INFO_SELECT,         	// object select
					slPhaseBusSelect,
					//SL_RELATION_INFO_SELECT_EBOM,   // rel select
					null,
					false,                            // to side
					true,                            // from side
					(short) 1,                        // recursion level
					DomainConstants.EMPTY_STRING,       // object where clause
					DomainConstants.EMPTY_STRING, 0);
			Iterator itr = mlFirstLevelData.iterator();
			String[] saBOMRow = null;
			// Expand from the TS to get the pgPhase object
			while (itr.hasNext()) {

				hmPhaseMap = (Hashtable) itr.next();

				strPhaseName = (String) hmPhaseMap.get(DomainConstants.SELECT_NAME);
				strPhaseObjID = (String) hmPhaseMap.get(DomainConstants.SELECT_ID);
				dObjPhase = DomainObject.newInstance(context, strPhaseObjID);

				Pattern pgPhaseExpTypePattern = new Pattern(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_PGFORMULATEDPRODUCT);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_PGRAWMATERIAL);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGMATERIAL);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_FORMULATIONPART);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_DEVICEPRODUCTPART);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
				pgPhaseExpTypePattern.addPattern(pgV3Constants.TYPE_RAWMATERIALPART);

				StringList slPhaseChildBusSelect = new StringList(6);
				slPhaseChildBusSelect.add(DomainConstants.SELECT_TYPE);
				slPhaseChildBusSelect.add(DomainConstants.SELECT_NAME);
				slPhaseChildBusSelect.add(DomainConstants.SELECT_REVISION);
				slPhaseChildBusSelect.add(DomainConstants.SELECT_ID);
				slPhaseChildBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
				slPhaseChildBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
				slPhaseChildBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);


				StringList slPhaseChildRelSelect = new StringList(7);
				slPhaseChildRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
				slPhaseChildRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
				slPhaseChildRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
				slPhaseChildRelSelect.add(DomainConstants.SELECT_RELATIONSHIP_ID);
				slPhaseChildRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
				slPhaseChildRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY);
				slPhaseChildRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY);
				slPhaseChildRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);

				mlPhaseChildObjList = dObjPhase.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_EBOM,        // rel pattern
						pgPhaseExpTypePattern.getPattern(),    // type pattern
						//SL_OBJECT_INFO_SELECT,         		// object select
						slPhaseChildBusSelect,
						//SL_RELATION_INFO_SELECT_EBOM,    		// rel select
						slPhaseChildRelSelect,
						false,                                // to side
						true,                                // from side
						(short) 1,                            // recursion level
						DomainConstants.EMPTY_STRING,           // object where clause
						DomainConstants.EMPTY_STRING, 0);

				Iterator pgPhaseItr = mlPhaseChildObjList.iterator();

				while (pgPhaseItr.hasNext()) {

					hmPhaseChildMap = (Hashtable) pgPhaseItr.next();

					strChildType = (String) hmPhaseChildMap.get(DomainConstants.SELECT_TYPE);
					strChildName = (String) hmPhaseChildMap.get(DomainConstants.SELECT_NAME);
					strChildRev = (String) hmPhaseChildMap.get(DomainConstants.SELECT_REVISION);
					strPgFPC = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
					strChildObjID = (String) hmPhaseChildMap.get(DomainConstants.SELECT_ID);
					strFindNumber = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
					strChildBUOM = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);

					strComponentNumber = strChildName;

					//For Substitutes
					strRelId = (String) hmPhaseChildMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
					strQuantity = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
					strTarget = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
					strLowerLim = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY);
					strUpperLim = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY);
					strPosIndicator = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
					strOptComponent = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
					strComment = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
					strValidFromDateComp = (String) hmPhaseChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
					strValidFromDateComp = convertPLMDateToSAP(strValidFromDateComp);

					if (!strFirstlevelObjId.equals(""))
						hmParentObjects.put(strFirstlevelObjId, iChildObjCount);
					strFirstlevelObjId = strChildObjID;
					iChildObjCount = 1;

					if (strChildType.equals(pgV3Constants.TYPE_PGBASEFORMULA)) {
						getBaseFormulaRows(context, strChildName, strChildRev,
								strFindNumber, strPhaseName, strObjectName, strValidFromDate, strBOMBaseQty,
								strBOMUsage, strChildObjID);
					} else {

						if (strChildType.startsWith(pgV3Constants.PGMASTER)) {

							//Add the indidivuals as subs
							getIndividualsFromMaster(context, strObjectName, strValidFromDate, strBOMBaseQty,
									strPhaseName, strFindNumber, strBOMNumber, strChildType,
									strChildName, strChildRev, strBOMUsage, strQuantity, strUpperLim,
									strTarget, strLowerLim, strValidFromDateComp, strPosIndicator, true, strChildObjID);
							buildFCBOMArray(context, strPhaseName, strFindNumber,
									strObjectName, strBOMUsage, strValidFromDate,
									strComponentNumber, strQuantity, strSubstFlag,
									strUpperLim, strTarget, strLowerLim,
									strValidFromDateComp, strBOMNumber, strBOMBaseQty,
									strPosIndicator, strRelId, strChildName, strChildRev, true, strChildObjID, "", "", strChildBUOM);
						} else {
							buildFCBOMArray(context, strPhaseName, strFindNumber,
									strObjectName, strBOMUsage, strValidFromDate,
									strComponentNumber, strQuantity, strSubstFlag,
									strUpperLim, strTarget, strLowerLim,
									strValidFromDateComp, strBOMNumber, strBOMBaseQty,
									strPosIndicator, strRelId, strChildName, strChildRev, false, strChildObjID, strOptComponent, strComment, strChildBUOM);
						}
					}
				}
			}
			hmParentObjects.put(strFirstlevelObjId, iChildObjCount);

			alBOMFC = getBOMAlternateSubstituteGrouping(context, alBOMFC);
			saBOM = convertArrayListToArray(alBOMFC, BOM_ARRAY_SIZE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (saBOM != null) {

			//Arrays.sort(saBOM, new BOMArrayComparator());
			Arrays.sort(saBOM, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					int result = 0;

					String[] str1 = (String[]) obj1;
					String[] str2 = (String[]) obj2;


					/* Sort on first element of each array (last name) */
					if ((result = str1[pgV3Constants.INDEX_PHASE_NAME].compareTo(str2[pgV3Constants.INDEX_PHASE_NAME])) == 0) {
						/* If same last name, sort on second element (first name) */
						Float in1 = null;
						if (UIUtil.isNotNullAndNotEmpty(str1[pgV3Constants.INDEX_FIND_NUMBER]) && !(str1[pgV3Constants.INDEX_FIND_NUMBER].contains("#DENIED!"))) {
							in1 = Float.valueOf(str1[pgV3Constants.INDEX_FIND_NUMBER]);
						}
						Float in2 = null;
						if (UIUtil.isNotNullAndNotEmpty(str2[pgV3Constants.INDEX_FIND_NUMBER]) && !(str2[pgV3Constants.INDEX_FIND_NUMBER].contains("#DENIED!"))) {
							in2 = Float.valueOf(str2[pgV3Constants.INDEX_FIND_NUMBER]);
						}
						if (null != in1 && null != in2)
							result = in1.compareTo(in2);
					}
					return result;
				}

			});
		}
		return saBOM;
	}

	/**
	 * getBaseFormulaRows- Method is used get BOM data of Base Formula to be send to SAP.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param *
	 * @return ArrayList of BOM object details
	 * @throws Exception
	 */
	public ArrayList getBaseFormulaRows(Context context, String strBSFName, String strBSFRev,
			String strFindNumber, String strPgPhaseName, String strParentName, String strParentEffectiveDate,
			String strParentBOMBaseQty, String strBOMUsage, String strTSID) {
		String[][] saReturn = null;
		String strType = DomainConstants.EMPTY_STRING;
		String strName = DomainConstants.EMPTY_STRING;
		String strRev = DomainConstants.EMPTY_STRING;
		String strRelId = DomainConstants.EMPTY_STRING;
		String strChildObjId = DomainConstants.EMPTY_STRING;
		String strRowFindNumber = DomainConstants.EMPTY_STRING;
		String strComponentNumber = DomainConstants.EMPTY_STRING;
		String strQuantity = DomainConstants.EMPTY_STRING;
		String strTarget = DomainConstants.EMPTY_STRING;
		String strLowerLim = DomainConstants.EMPTY_STRING;
		String strUpperLim = DomainConstants.EMPTY_STRING;
		String strPosIndicator = DomainConstants.EMPTY_STRING;
		String strOptComponent = DomainConstants.EMPTY_STRING;
		String strComment = DomainConstants.EMPTY_STRING;
		String strValidFromDateComp = DomainConstants.EMPTY_STRING;
		String strBOMNumber = DomainConstants.EMPTY_STRING;
		String strSubstFlag = DomainConstants.EMPTY_STRING;
		String strChildBUOM = DomainConstants.EMPTY_STRING;
		DomainObject domBSF = null;

		StringList slBusSelect = new StringList(7);
		slBusSelect.add(DomainConstants.SELECT_TYPE);
		slBusSelect.add(DomainConstants.SELECT_NAME);
		slBusSelect.add(DomainConstants.SELECT_REVISION);
		slBusSelect.add(DomainConstants.SELECT_ID);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);

		StringList slRelSelect = new StringList(8);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
		slRelSelect.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY);
		slRelSelect.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);

		MapList mlBSFChildObjList = new MapList();

		try {
			domBSF = DomainObject.newInstance(context, strTSID);

			mlBSFChildObjList = domBSF.getRelatedObjects(context,
					pgV3Constants.RELATIONSHIP_EBOM,    // rel pattern
					pgV3Constants.TYPE_PRODUCTDATA,     // type pattern
					//SL_OBJECT_INFO_SELECT,         	// object select
					slBusSelect,
					//SL_RELATION_INFO_SELECT_EBOM,   // rel select
					slRelSelect,
					false,                            // to side
					true,                            // from side
					(short) 1,                        // recursion level
					DomainConstants.EMPTY_STRING,       // object where clause
					DomainConstants.EMPTY_STRING, 0);

			Iterator bsfChildItr = mlBSFChildObjList.iterator();

			while (bsfChildItr.hasNext()) {

				Map hmChildMap = (HashMap) bsfChildItr.next();

				strType = (String) hmChildMap.get(DomainConstants.SELECT_TYPE);
				strName = (String) hmChildMap.get(DomainConstants.SELECT_NAME);
				strRev = (String) hmChildMap.get(DomainConstants.SELECT_REVISION);
				strRelId = (String) hmChildMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
				strChildObjId = (String) hmChildMap.get(DomainConstants.SELECT_ID);
				strRowFindNumber = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
				strQuantity = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
				strTarget = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
				strLowerLim = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY);
				strUpperLim = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY);
				strPosIndicator = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
				strOptComponent = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
				strComment = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
				strChildBUOM = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
				strValidFromDateComp = (String) hmChildMap.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
				strValidFromDateComp = convertPLMDateToSAP(strValidFromDateComp);

				if (strType.startsWith(pgV3Constants.PGMASTER)) {
					buildFCBOMArray(context, strPgPhaseName, strRowFindNumber,
							strParentName, strBOMUsage, strParentEffectiveDate,
							strComponentNumber, strQuantity, strSubstFlag,
							strUpperLim, strTarget, strLowerLim,
							strValidFromDateComp, strBOMNumber, strParentBOMBaseQty,
							strPosIndicator, strRelId, strName, strRev, false, strChildObjId, "", "", strChildBUOM);
					//Add the indidivuals as subs
					getIndividualsFromMaster(context, strParentName, strParentEffectiveDate, strParentBOMBaseQty,
							strPgPhaseName, strFindNumber, strBOMNumber, strType,
							strName, strRev, strBOMUsage, strQuantity, strUpperLim,
							strTarget, strLowerLim, strValidFromDateComp, strPosIndicator, true, strChildObjId);
				} else {
					buildFCBOMArray(context, strPgPhaseName, strRowFindNumber,
							strParentName, strBOMUsage, strParentEffectiveDate,
							strComponentNumber, strQuantity, strSubstFlag,
							strUpperLim, strTarget, strLowerLim,
							strValidFromDateComp, strBOMNumber, strParentBOMBaseQty,
							strPosIndicator, strRelId, strName, strRev, false, strChildObjId, strOptComponent, strComment, strChildBUOM);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return alBOMFC;
	}

	/**
	 * buildFCBOMArray- Method is used build array for FC BOM data.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param *
	 * @return
	 * @throws Exception
	 */

	public void buildFCBOMArray(Context context, String strPgPhase, String strFindNumber,
			String strName, String strBomUsage, String strValidFromDate,
			String strComponentName, String strQuantity, String strSubstituteFlag,
			String strUpperLimit, String strTarget, String strLowerLimit,
			String strValidFromDateComponent, String strBOMNumber,
			String strBOMBaseQuantity, String strPositionIndicator, String strRelId,
			String strGCAS, String strVersion, boolean bOnlySendChildren, String strTSID, String strOptComponent, String strComment, String strChildBUOM) {

		boolean bIgnoreBlankTarget = false;
		if (strTarget.equals("")) {
			_AbortReason = "Aborting BOM because target is null for " + strComponentName;
			bIgnoreBlankTarget = true;
		}

		String[] saTemp = new String[BOM_ARRAY_SIZE];
		String[][] saSubs = null;
		String strTSName = DomainConstants.EMPTY_STRING;
		String strTSType = DomainConstants.EMPTY_STRING;
		String strTSRev = DomainConstants.EMPTY_STRING;
		String strValidFromDateSub = DomainConstants.EMPTY_STRING;
		String strSubComponentNumber = DomainConstants.EMPTY_STRING;

		// Set the constant value fields
		saTemp[pgV3Constants.INDEX_ALT_BOM] = pgV3Constants.VALUE_ALT_BOM;
		saTemp[pgV3Constants.INDEX_BOM_STATUS] = pgV3Constants.VALUE_BOM_STATUS;
		saTemp[pgV3Constants.INDEX_ITEM_CATEGORY] = pgV3Constants.VALUE_ITEM_CATEGORY;
		//saTemp[pgV3Constants.INDEX_BOM_TEXT] = strParentGCAS + "." + strParentRev;
		saTemp[pgV3Constants.INDEX_BOM_TEXT] = _ParentMatlNumber + "." + strParentRev;
		saTemp[pgV3Constants.INDEX_ALT_BOM_TEXT] = pgV3Constants.VALUE_ALT_BOM_TEXT;

		// Set the variable fields
		if (!strSubstituteFlag.equals("")) {
			saTemp[pgV3Constants.INDEX_SUBSTITUTE_FLAG] = strSubstituteFlag;
			saTemp[pgV3Constants.INDEX_USAGE_PROBABILITY] = pgV3Constants.VALUE_USAGE_PROBABILITY;
		} else {
			saTemp[pgV3Constants.INDEX_SUBSTITUTE_FLAG] = "";
			saTemp[pgV3Constants.INDEX_USAGE_PROBABILITY] = "";
		}

		saTemp[pgV3Constants.INDEX_MATERIAL_NUMBER] = _ParentMatlNumber;
		saTemp[pgV3Constants.INDEX_BOM_USAGE] = strBomUsage;
		saTemp[pgV3Constants.INDEX_VALID_FROM_DATE_PARENT] = strValidFromDate;
		saTemp[pgV3Constants.INDEX_COMPONENT_NAME] = strComponentName;

		if (strParentType.equalsIgnoreCase(pgV3Constants.TYPE_PGFORMULATEDPRODUCT) && !strChildBUOM.equalsIgnoreCase(strParentBUOM)) {

			saTemp[pgV3Constants.INDEX_QUANTITY] = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
			saTemp[pgV3Constants.INDEX_UPPER_LIMIT] = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
			saTemp[pgV3Constants.INDEX_LOWER_LIMIT] = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
		} else {

			saTemp[pgV3Constants.INDEX_QUANTITY] = strQuantity;
			saTemp[pgV3Constants.INDEX_UPPER_LIMIT] = strUpperLimit;
			saTemp[pgV3Constants.INDEX_LOWER_LIMIT] = strLowerLimit;
		}

		if (bIgnoreBlankTarget || (strParentType.equalsIgnoreCase(pgV3Constants.TYPE_PGFINISHEDPRODUCT) && !strChildBUOM.equalsIgnoreCase(strParentBUOM))) {

			saTemp[pgV3Constants.INDEX_TARGET] = "-9.99";
		} else {
			saTemp[pgV3Constants.INDEX_TARGET] = strTarget;
		}
		saTemp[pgV3Constants.INDEX_FIND_NUMBER] = strFindNumber;
		saTemp[pgV3Constants.INDEX_VALID_FROM_DATE_CHILD] = strValidFromDateComponent;
		saTemp[pgV3Constants.INDEX_PHASE_NAME] = strPgPhase;
		saTemp[pgV3Constants.INDEX_CONFIRMED_QUANTITY] = strBOMBaseQuantity;
		saTemp[pgV3Constants.INDEX_SORT_STRING] = strPositionIndicator;
		saTemp[OPT_COMPONENT] = strOptComponent;
		saTemp[COMMENT] = strComment;
		saTemp[pgV3Constants.INDEX_BOM_ITEM_NUMBER] = strBOMNumber;
		saTemp[OBJECT_ID] = strTSID;
		if (bCreatingSubsRows) {
			saTemp[DISPLAY_OBJECT_ID] = strSubsDispId;
		} else {
			saTemp[DISPLAY_OBJECT_ID] = strTSID;
		}
		if (saTemp[pgV3Constants.INDEX_UPPER_LIMIT] == null || saTemp[pgV3Constants.INDEX_UPPER_LIMIT].equals("")) {
			saTemp[pgV3Constants.INDEX_RANGE_FLAG] = "";
		} else
			saTemp[pgV3Constants.INDEX_RANGE_FLAG] = pgV3Constants.VALUE_RANGE_FLAG;

		//Need to see if this Row has subs for it. If it does. set the Alt_Item_Group for this
		//Row to the same value as the subs use
		saSubs = getSubs(context, strRelId);

		if (saSubs != null) {
			saTemp[pgV3Constants.INDEX_SUBSTITUTE_FLAG] = "";
			saTemp[pgV3Constants.INDEX_USAGE_PROBABILITY] = pgV3Constants.VALUE_USAGE_PROBABILITY;
		}

		if (!bOnlySendChildren) {
			alBOMFC.add(saTemp);
			if (strFirstlevelObjIdCopy.equals(""))
				strFirstlevelObjIdCopy = strFirstlevelObjId;
			else if (!strFirstlevelObjIdCopy.equals(strFirstlevelObjId))
				strFirstlevelObjIdCopy = strFirstlevelObjId;
			else
				iChildObjCount++;

			hmChildObjects.put(saTemp[DISPLAY_OBJECT_ID], strFirstlevelObjId);
		}

		boolean bIncrementAltItem = true;
		if (saSubs != null) {
			for (int i = 0; i < saSubs.length; i++) {
				bCreatingSubsRows = true;
				strSubsDispId = saSubs[i][13];
				strTSName = saSubs[i][6];
				strTSType = saSubs[i][7];
				strTSRev = saSubs[i][11];
				//strValidFromDateSub = saSubs[i][10];
				//strValidFromDateSub = DateUtil.convertPLMDateToSAP(saSubs[i][10]);
				strValidFromDateSub = convertPLMDateToSAP(saSubs[i][10]);
				strChildBUOM = saSubs[i][17];

				if (strTSType.startsWith(pgV3Constants.PGMASTER)) {
					try {
						strTarget = saSubs[i][2];
						bCreatingSubsRows = false;
						getIndividualsFromMaster(context, strName, strValidFromDate, strBOMBaseQuantity, strPgPhase,
								strFindNumber, strBOMNumber, strTSType, strTSName, strTSRev,
								strBomUsage, strQuantity, strUpperLimit, strTarget, strLowerLimit,
								strValidFromDateComponent, strPositionIndicator, false, strTSID);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					bIncrementAltItem = false;
				} else {

					strSubComponentNumber = strTSName;
					buildFCBOMArray(context, strPgPhase, strFindNumber,
							strName, strBomUsage, strValidFromDate,
							strSubComponentNumber, saSubs[i][2], saSubs[i][5],
							saSubs[i][3], saSubs[i][2], saSubs[i][1],
							strValidFromDateSub, strBOMNumber,
							strBOMBaseQuantity, saSubs[i][0], "", strGCAS, strVersion, false, strTSID, saSubs[i][14], saSubs[i][15], strChildBUOM);
				}

				if (i == saSubs.length - 1) {
					bCreatingSubsRows = false;
					strSubsDispId = "";
				}

			}
		}
	}

	/**
	 * getSubstitutes- Method is used to get Sustitutes connected to relationship.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param *
	 * @return String[][]
	 * @throws Exception
	 */
	public String[][] getSubs(Context context, String strRelId) {
		String[][] saReturn = null;
		String strSubsCommand = DomainConstants.EMPTY_STRING;
		String strMQLSubResult = DomainConstants.EMPTY_STRING;
		String strSapType = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		String strATS = DomainConstants.EMPTY_STRING;
		String[] saParts = null;
		String strMQLStmt = DomainConstants.EMPTY_STRING;
		String strMqlRes = DomainConstants.EMPTY_STRING;
		ArrayList<String[]> alSubRows = new ArrayList<String[]>();
		String[] saIds = null;

		if (strRelId.equals(""))
			return saReturn;
		try {

			strMQLStmt = "print connection $1 select $2 dump $3";
			strMqlRes = MqlUtil.mqlCommand(context, strMQLStmt, strRelId, "frommid[EBOM Substitute].id", "|");

			if (!strMqlRes.equals("")) {
				saIds = (strMqlRes.trim().split("\\|", -1));
				if (!saIds[0].equals("")) {
					for (int i = 0; i < saIds.length; i++) {
						strSubsCommand = "print connection $1 select $2 $3 $4 $5 $6 $7 $8 $9 $10 $11 $12 $13 $14 $15 $16 $17 $18 $19 dump $20";
						strMQLSubResult = MqlUtil.mqlCommand(context, strSubsCommand, saIds[i], pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR, pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY, pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY, pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY, pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITYUNITOFMEASURE, pgV3Constants.SELECT_ATTRIBUTE_PGSUBSTITUTECOMBINATIONNUMBER, "to.name", "to.type", "to." + pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE, "to." + pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE, "to." + pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, "to.revision", "to." + pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE, "to.id", "to.from[" + pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION + "]", pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT, pgV3Constants.SELECT_ATTRIBUTE_COMMENT, "to." + pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE, "|");

						saParts = (strMQLSubResult.trim().split("\\|", -1));
						strSapType = saParts[12];
						strType = saParts[7];
						//if (saParts.length == 16 )
						strATS = saParts[14];
						if (strType.startsWith(pgV3Constants.PGMASTER) || (!strSapType.equalsIgnoreCase(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC) && (pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE.equals(strATS))))
							alSubRows.add(saParts);
					}
				}
				if (alSubRows.size() > 0) {
					saReturn = convertArrayListToArray(alSubRows, 17);
					Arrays.sort(saReturn, new Comparator() {
						public int compare(Object obj1, Object obj2) {
							int result = 0;

							String[] str1 = (String[]) obj1;
							String[] str2 = (String[]) obj2;

							/* Sort on Substitute Combination Number */
							Float in1 = Float.valueOf(str1[5]);
							Float in2 = Float.valueOf(str2[5]);
							result = in1.compareTo(in2);

							return result;
						}
					});

					for (int i = 0; i < saReturn.length; i++) {
						saReturn[i][5] = "";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return saReturn;
	}

	/**
	 * getIndividualsFromMaster- Method is used to get individuals connected to master.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param *
	 * @return
	 * @throws Exception
	 */
	public void getIndividualsFromMaster(Context context, String TSName, String strValidFromDate, String strBOMBaseQty, String strPgPhaseName, String strFindNumber, String strBOMNumber, String strMasterTSType, String strMasterTSName, String strMasterTSRev, String strBOMUsage, String strQuantity, String strUpperLim, String strTarget, String strLowerLim, String strValidFromDateComp, String strPosIndicator, boolean bIncrementAltItem, String strTSID) throws Exception {

		String strComponentNumber = DomainConstants.EMPTY_STRING;
		String strIndSubFlag = DomainConstants.EMPTY_STRING;
		String strMasterObjectId = DomainConstants.EMPTY_STRING;
		String strRelId = DomainConstants.EMPTY_STRING;
		String strIndObjectId = DomainConstants.EMPTY_STRING;
		String strIndType = DomainConstants.EMPTY_STRING;
		String strIndName = DomainConstants.EMPTY_STRING;
		String strIndRev = DomainConstants.EMPTY_STRING;
		StringList slIndNames = new StringList();
		Map hmMasterDetails = null;
		Map hmIndDetails = null;
		MapList mlIndividuals = null;
		int iIndObjCount = 0;
		MapList mlMasterObjectList = null;
		DomainObject domMaster = null;
		String strChildBUOM = DomainConstants.EMPTY_STRING;

		StringList slBusSelect = new StringList(5);
		slBusSelect.add(DomainConstants.SELECT_TYPE);
		slBusSelect.add(DomainConstants.SELECT_NAME);
		slBusSelect.add(DomainConstants.SELECT_REVISION);
		slBusSelect.add(DomainConstants.SELECT_ID);
		slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);


		try {

			String strMQLStmt = "temp query bus $1 $2 $3 select $4 dump $5";
			String strMqlRes = MqlUtil.mqlCommand(context, strMQLStmt, strMasterTSType, strMasterTSName, DomainConstants.QUERY_WILDCARD, DomainObject.SELECT_ID, "|");
			StringList strlResult = new StringList();
			StringList slMasterIds = FrameworkUtil.split(strMqlRes, "\n");
			Iterator itrMasters = slMasterIds.iterator();
			while (itrMasters.hasNext()) {
				String strMaster = (String) itrMasters.next();
				strlResult = FrameworkUtil.split(strMaster, "|");
				strMasterObjectId = (String) strlResult.get(3);

				if (BusinessUtil.isNotNullOrEmpty(strMasterObjectId)) {
					domMaster = DomainObject.newInstance(context, strMasterObjectId);

					mlIndividuals = domMaster.getRelatedObjects(context,
							pgV3Constants.RELATIONSHIP_PGMASTER,
							DomainConstants.QUERY_WILDCARD,
							true,                        //getTo
							true,                        //getFrom
							(short) 1,                    // recursionLevel
							//SL_OBJECT_INFO_SELECT, 	//objectSelects
							slBusSelect,
							new StringList(DomainObject.SELECT_RELATIONSHIP_ID),                            //relSelects
							null,                        // busWhereClause,
							null,                        // relWhereClause,
							null,                        // postRelPattern,
							null,                        //postTypePattern
							null);

					Iterator itrIndiv = mlIndividuals.iterator();
					while (itrIndiv.hasNext()) {
						hmIndDetails = (Hashtable) itrIndiv.next();
						strRelId = (String) hmIndDetails.get(DomainConstants.SELECT_RELATIONSHIP_ID);
						strIndObjectId = (String) hmIndDetails.get(DomainConstants.SELECT_ID);
						strIndType = (String) hmIndDetails.get(DomainConstants.SELECT_TYPE);
						strIndName = (String) hmIndDetails.get(DomainConstants.SELECT_NAME);
						strIndRev = (String) hmIndDetails.get(DomainConstants.SELECT_REVISION);
						strChildBUOM = (String) hmIndDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
						strComponentNumber = strIndName;
						strValidFromDateComp = convertPLMDateToSAP(getEffDateForLatestInd(context, strIndType, strIndName, strMasterTSName));

						if (BusinessUtil.isNotNullOrEmpty(strValidFromDateComp)) {
							if (!slIndNames.contains(strIndName)) {
								buildFCBOMArray(context, strPgPhaseName, strFindNumber, TSName, strBOMUsage, strValidFromDate, strComponentNumber, strQuantity, strIndSubFlag, strUpperLim, strTarget, strLowerLim, strValidFromDateComp, strBOMNumber, strBOMBaseQty, strPosIndicator, strRelId, strMasterTSName, strMasterTSRev, false, strIndObjectId, "", "", strChildBUOM);
								slIndNames.add(strIndName);
							}
						}
						iIndObjCount++;
					}

				}
				//}

			}
			if (iIndObjCount == 0) {
				_AbortReason = "Aborting because there are no individuals connected to Master " + strMasterTSName + " " + strMasterTSRev;
			}
		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	/**
	 * getEffDateForLatestInd- Method is used to get Eff Date of latest individuals.
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param *
	 * @return Effectivity Date of latest revision of individual
	 * @throws Exception
	 */
	public String getEffDateForLatestInd(Context context, String strType, String strName, String strMasterName) throws Exception {
		String strEffDate = DomainConstants.EMPTY_STRING;
		String strATS = DomainConstants.EMPTY_STRING;
		String strDocType = DomainConstants.EMPTY_STRING;
		String strMaster = DomainConstants.EMPTY_STRING;
		Map mpIndInfo = new HashMap();
		MapList mlIndObjectList = null;
		StringList slSelectables = new StringList();
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slSelectables.add("from[" + pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION + "].to.name");
		slSelectables.add("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.name");
		StringBuffer sbWhereCond = new StringBuffer();
		sbWhereCond.append("(");
		sbWhereCond.append(DomainObject.SELECT_CURRENT);
		sbWhereCond.append("==");
		sbWhereCond.append(pgV3Constants.STATE_RELEASE);
		sbWhereCond.append(" && (next.current != " + pgV3Constants.STATE_RELEASE + " || revision == last)");
		sbWhereCond.append(")");

		try {
			mlIndObjectList = DomainObject.findObjects(
					context,
					strType, //typePattern
					strName, //namePattern
					"*", //revPattern
					"*", //ownerPattern
					pgV3Constants.VAULT_ESERVICEPRODUCTION, //vaultPattern
					sbWhereCond.toString(), //whereExpression
					false, //expandType
					slSelectables); //objectSelects
			if (BusinessUtil.isNotNullOrEmpty(mlIndObjectList)) {
				mpIndInfo = (HashMap) mlIndObjectList.get(0);
				strATS = (String) mpIndInfo.get("from[" + pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION + "].to.name");
				strDocType = (String) mpIndInfo.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
				if (UIUtil.isNotNullAndNotEmpty(strDocType) && (!strDocType.equalsIgnoreCase(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC) || !strType.startsWith(pgV3Constants.PGMASTER))) {
					if (strATS == null || strATS.equals("")) {
						strMaster = (String) mpIndInfo.get("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.name");
						if (UIUtil.isNotNullAndNotEmpty(strMaster) && strMaster.contains(strMasterName))
							strEffDate = (String) mpIndInfo.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strEffDate;
	}

	/**
	 * getBOMAlternateSubstituteGrouping- Method is used to get Alternate and Substitutes grouping.
	 *
	 * @param context   the eMatrix <code>Context</code> object
	 * @param ArrayList of all BOM components
	 * @return ArrayList of all BOM components updated with proper substitute grouping.
	 * @throws Exception
	 */
	public ArrayList getBOMAlternateSubstituteGrouping(Context ctx, ArrayList alBOM) {

		ArrayList tempAllBOM = new ArrayList();
		String strParentObjId = "";
		String strObjId = "";
		int ichildObjCount = 0;
		String strTempId = "";
		boolean bIncGrouping = true;
		char _cAltItem1 = '9';
		char _cAltItem2 = 'A';
		if (alBOM != null) {
			Iterator itr = alBOM.iterator();
			while (itr.hasNext()) {
				String[] saTempBOM = (String[]) itr.next();
				strObjId = saTempBOM[DISPLAY_OBJECT_ID];
				strParentObjId = (String) hmChildObjects.get(strObjId);
				ichildObjCount = (int) hmParentObjects.get(strParentObjId);
				if (ichildObjCount > 1) {
					if (!bIncGrouping && !strTempId.equals(strParentObjId)) {
						if (_cAltItem2 == 'Z') {
							_cAltItem2 = 'A';
							_cAltItem1--;
						} else {
							_cAltItem2++;
						}
					}
					bIncGrouping = false;
					saTempBOM[pgV3Constants.INDEX_SUBSTITUTE_FLAG] = _cAltItem1 + "" + _cAltItem2;
					saTempBOM[pgV3Constants.INDEX_USAGE_PROBABILITY] = pgV3Constants.VALUE_USAGE_PROBABILITY;
				} else {
					saTempBOM[pgV3Constants.INDEX_SUBSTITUTE_FLAG] = "";
					saTempBOM[pgV3Constants.INDEX_USAGE_PROBABILITY] = "";
				}
				tempAllBOM.add(saTempBOM);
				strTempId = strParentObjId;
			}
		}
		return tempAllBOM;
	}

	public static String convertPLMDateToSAP(String strDate) {
		String strSAPDate = DomainConstants.EMPTY_STRING;
		;
		Date dMatrixDate = null;
		SimpleDateFormat formatter = null;
		SimpleDateFormat sapFormatter = null;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strDate)) {
				formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
				sapFormatter = new SimpleDateFormat(pgV3Constants.SAP_DATE_FORMAT);
				dMatrixDate = formatter.parse(strDate);
				strSAPDate = sapFormatter.format(dMatrixDate);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return (strSAPDate);
	}

	public String doBOMeDeliveryFromFailureView(Context context, String[] methodArgs) {
		String strSAPStatus = DomainConstants.EMPTY_STRING;
		int iReturn = 0;
		try {
			//pgIPMUtil_Deferred_mxJPO objpgIPMDef = new pgIPMUtil_Deferred_mxJPO(context, methodArgs);
			//iReturn = (int)objpgIPMDef.doBOMeDelivery(context, methodArgs);
			iReturn = JPO.invoke(context, "pgIPMUtil_Deferred", null, "doBOMeDelivery", methodArgs);
			if (iReturn == 0)
				strSAPStatus = "Success";
			else
				strSAPStatus = "Failure";
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return strSAPStatus;
	}

	public BigDecimal divideBigDecimalValues(BigDecimal bdNum, BigDecimal bdDen) {
		try {
			if (bdDen.compareTo(BigDecimal.ZERO) != 0) {
				bdNum = bdNum.divide(bdDen, 3, RoundingMode.HALF_UP);
			}
		} catch (ArithmeticException ae) {
			bdNum = bdNum.divide(bdDen, 3, RoundingMode.HALF_UP);
		}
		return bdNum;
	}

	/**
	 * doBOMeDeliveryOnAuthorizedToProduceModify- This method will be invoked on "Authorized to Produce"
	 * attribute modification to trigger BOM eDelivery.
	 *
	 * @param *
	 * @return
	 * @throws Exception
	 **/
	public int doBOMeDeliveryOnAuthorizedToProduceModify(Context context, String[] methodArgs) throws Exception {
		int iReturn = 0;
		String sPartObjectType = DomainConstants.EMPTY_STRING;
		String sPartObjectName = DomainConstants.EMPTY_STRING;
		String sPartObjectRev = DomainConstants.EMPTY_STRING;
		String sPartObjectID = DomainConstants.EMPTY_STRING;
		String sPlantObjectId = DomainConstants.EMPTY_STRING;
		String sNewAttrVal = DomainConstants.EMPTY_STRING;
		String strCurrent = DomainConstants.EMPTY_STRING;
		String[] strArrPlants = null;
		DomainObject doTS = null;
		try {
			if (null != methodArgs && methodArgs.length >= 6) {
				sPartObjectID = methodArgs[0];
				//methodArgs[5] = "Dummy";

				sPartObjectType = methodArgs[1];
				sPartObjectName = methodArgs[2];
				sPartObjectRev = methodArgs[3];
				sPlantObjectId = methodArgs[4];
				sNewAttrVal = methodArgs[5];
				String[] strArgs = new String[6];
				strArgs[0] = sPartObjectID;
				strArgs[1] = sPartObjectType;
				strArgs[2] = sPartObjectName;
				strArgs[3] = sPartObjectRev;
				strArgs[4] = sPlantObjectId;
				strArgs[5] = "Dummy";

				if (UIUtil.isNotNullAndNotEmpty(sNewAttrVal) && sNewAttrVal.equalsIgnoreCase("TRUE") && UIUtil.isNotNullAndNotEmpty(sPartObjectID)) {
					doTS = DomainObject.newInstance(context, sPartObjectID);
					strCurrent = doTS.getInfo(context, DomainConstants.SELECT_CURRENT);
					if (strCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE)) {
						iReturn = JPO.invoke(context, "pgIPMUtil_Deferred", null, "doBOMeDelivery", strArgs);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return iReturn;
	}

	//Added by DSM(Sogeti) for 2018x.2 Requirement #29558 - Starts
	//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
	private String calculateBOMQuantityFOPChild(Context context, String strSAPBOMBaseQty, Map mapIngredients, String strSumOfDryweight) {
		//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
		String strQty = DomainConstants.EMPTY_STRING;

		double dBaseQty = 100;
		double dSAPBOMBaseQty = 1000;
		double dPercentWet = 0;
		double dLoss = 0;
		double dDryPercent = 0;
		double dWeightWeightSAP = 0;
		double dDryWeight = 0;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
		double dTargetWeightWet = 0;
		double dSumofDryWeight = 0;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

		boolean bProcessingLoss100Percent = false;

		DecimalFormat decimalformatter = new DecimalFormat(PATTERN_DECIMALFORMAT);

		String PercentWet = DomainConstants.EMPTY_STRING;
		String strTempLossValue = DomainConstants.EMPTY_STRING;
		String strDryPercentValue = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
		String strTargetWetValue = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

		if (BusinessUtil.isNotNullOrEmpty(strSAPBOMBaseQty)) {
			dSAPBOMBaseQty = parseValue(strSAPBOMBaseQty);
		}
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
		if (BusinessUtil.isNotNullOrEmpty(strSumOfDryweight)) {
			dSumofDryWeight = parseValue(strSumOfDryweight);
		}
		BigDecimal bdTotalDryweight = BigDecimal.valueOf(dSumofDryWeight);
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
		try {

			if (null != mapIngredients && !mapIngredients.isEmpty()) {

				PercentWet = (String) mapIngredients.get("attribute[" + pgV3Constants.ATTRIBUTE_QUANTITY + "].inputvalue");
				strTempLossValue = (String) mapIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
				strDryPercentValue = (String) mapIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
				//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
				strTargetWetValue = (String) mapIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
				//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
				if (BusinessUtil.isNotNullOrEmpty(PercentWet)) {
					dPercentWet = parseFOPQuantityValue(PercentWet);
				}
				if (BusinessUtil.isNotNullOrEmpty(strTempLossValue)) {
					dLoss = parseFOPQuantityValue(strTempLossValue);
					if (dLoss == 100.0) {
						bProcessingLoss100Percent = true;
					}
				}
				if (BusinessUtil.isNotNullOrEmpty(strDryPercentValue)) {
					dDryPercent = parseFOPQuantityValue(strDryPercentValue);
				}
				//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
				if (BusinessUtil.isNotNullOrEmpty(strTargetWetValue)) {
					dTargetWeightWet = parseFOPQuantityValue(strTargetWetValue);
				}
				//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

				BigDecimal bdBaseQTY = BigDecimal.valueOf(dBaseQty);
				BigDecimal bdLoss = BigDecimal.valueOf((100 - dLoss));
				//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
				BigDecimal bdTargetWeightWet = BigDecimal.valueOf(dTargetWeightWet);
				//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
				if (dPercentWet > 0) {
					BigDecimal bdWeightWeightSAP = BigDecimal.valueOf(dPercentWet);

					if (dLoss == 100.0) {
						//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
						bdWeightWeightSAP = BigDecimal.valueOf(dSAPBOMBaseQty);
						if (bdTotalDryweight.compareTo(BigDecimal.ZERO) != 0) {
							bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdTotalDryweight);
							bdWeightWeightSAP = bdTargetWeightWet.multiply(bdWeightWeightSAP);
						} else {
							bdWeightWeightSAP = BigDecimal.valueOf(dPercentWet);
							bdWeightWeightSAP = bdWeightWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));
							if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
								bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdBaseQTY);
							}
						}
						//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
					} else if (dLoss > 0.0) {

						if (bdLoss.compareTo(BigDecimal.ZERO) != 0) {
							bdWeightWeightSAP = bdWeightWeightSAP.multiply(bdLoss);
							bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdLoss);
						}
						bdWeightWeightSAP = bdWeightWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));

						if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
							bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdBaseQTY);
						}
					} else {
						bdWeightWeightSAP = bdWeightWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));

						if (bdBaseQTY.compareTo(BigDecimal.ZERO) != 0) {
							bdWeightWeightSAP = (BigDecimal) divideBigDecimalValues(bdWeightWeightSAP, bdBaseQTY);
						}
					}
					dWeightWeightSAP = bdWeightWeightSAP.doubleValue();
				}

				if (dDryPercent > 0 && !bProcessingLoss100Percent) {
					BigDecimal bdDryWeightSAP = BigDecimal.valueOf(dDryPercent);
					bdDryWeightSAP = bdDryWeightSAP.multiply(BigDecimal.valueOf(dSAPBOMBaseQty));

					if (bdLoss.compareTo(BigDecimal.ZERO) != 0) {
						bdDryWeightSAP = (BigDecimal) divideBigDecimalValues(bdDryWeightSAP, bdLoss);
					}
					dWeightWeightSAP = bdDryWeightSAP.doubleValue();
				}
				strQty = String.valueOf(decimalformatter.format(dWeightWeightSAP));

			}
			//Dry Wt for SAP BOM Base Qty = Dry % * SAP BOM Base Qty
			//Wet Weight for SAP BOM Base Qty= Dry Weight for SAP BOM Base Qty/ ( 1- % Loss)
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strQty;
	}
	//Added by DSM(Sogeti) for 2018x.2 Requirement #29558 - Ends

	//Modified by DSM(Sogeti) for 2018x.2 Requirement #21573,#24407,#25099,#29556,#29557,#29558,#29559 - Starts

	/**
	 * checkSAPBOMQuantity- This method will check if the Sum of Ingredients quantities is equal to * * BOM Base Quantity.
	 *
	 * @param Object Id, Object Type
	 * @return 1 on failure, 0 on success
	 * @throws Exception
	 **/

	public int checkSAPBOMQuantity(Context context, String[] args) throws Exception {
		String strObjectId = args[0];
		String strObjectType = args[1];
		//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
		String strActionParameter = args[2];
		//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends

		String strRelPattern = DomainConstants.EMPTY_STRING;
		String strTypePattern = DomainConstants.EMPTY_STRING;
		String strIngredientType = DomainConstants.EMPTY_STRING;
		String strTargetWeightDry = DomainConstants.EMPTY_STRING;
		String strTargetWeightWet = DomainConstants.EMPTY_STRING;
		String strDryPercentage = DomainConstants.EMPTY_STRING;
		String strProcessingLoss = DomainConstants.EMPTY_STRING;
		String strFOPBaseUOM = DomainConstants.EMPTY_STRING;
		String strIngredientBaseUOM = DomainConstants.EMPTY_STRING;
		String strFBOMRelId = DomainConstants.EMPTY_STRING;
		String strBOMBaseQty = DomainConstants.EMPTY_STRING;
		String strFormulationProcessGCAS = DomainConstants.EMPTY_STRING;
		String strIngredientTypePL = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
		String strSumOfDryweight = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
		String strChildType = DomainConstants.EMPTY_STRING;
		String strChildID = DomainConstants.EMPTY_STRING;
		String TYPE_FORMULATIONPROCESS = pgV3Constants.TYPE_FORMULATIONPROCESS;
		String strFPObjWhere = "revision == last";

		int iReturn = 0;

		double dQuantityValue = 0.0;
		double dWetWeight = 0.0;
		double dDryWeight = 0.0;

		BigDecimal bdQuantityValue = BigDecimal.ZERO;
		BigDecimal bdDryWeight = BigDecimal.ZERO;
		BigDecimal bdWetWeight = BigDecimal.ZERO;
		BigDecimal bdSumofBOMQuantities = BigDecimal.ZERO;
		BigDecimal bdSumofWetWeight = BigDecimal.ZERO;
		BigDecimal bdSumofDryWeight = BigDecimal.ZERO;
		BigDecimal bdBOMBaseQuantity = BigDecimal.ZERO;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
		BigDecimal bdSumofDryWeights = BigDecimal.ZERO;
		//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends
		BigDecimal bdCalculatedQty = new BigDecimal("0.0");
		BigDecimal bdPercentageQty = new BigDecimal("100");
		BigDecimal bdValidationValue = new BigDecimal("0.00049");
		BigDecimal bdFinalQuantityValue = new BigDecimal("9999999999.999");

		boolean bSAPBOMQuantityValidation = false;

		DomainObject doFormulaObject = null;
		MapList mlFormulaIngredientValues = null;
		MapList mpListChildren = null;

		Map mapFormulaIngredientsProcessingLoss = null;
		Map mapFormulaIngredients = null;
		Map mpTempChildData = null;

		//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
		int iValidate = pgV3Constants.IVALIDATE_ZERO;
		String sFinalMessage = DomainConstants.EMPTY_STRING;
		//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends

		//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Starts
		String strFormulationProcessValidationMsg = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.ProductData.BOMeDelivery.BOMQuantityValidation");
		String strFormulationProcessValidationMsgContinue = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.ProductData.BOMeDelivery.BOMQuantityValidationAlert");
		String strFormulationProcessUOMValidationMsg = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.ProductData.BOMeDelivery.UnitOfMeasureValidationAlert");
		String strFormulationProcessTypeValidationMsg = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.ProductData.BOMeDelivery.TypeValidationAlert");
		String strFormulationProcessPLossValidationMsg = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.ProductData.BOMeDelivery.ProcessingLossValidation");
		//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Ends
		try {
			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Starts
			strRelPattern = pgV3Constants.RELATIONSHIP_PLBOM;
			strTypePattern = pgV3Constants.TYPE_FORMULATIONPHASE + "," + pgV3Constants.TYPE_RAWMATERIALPART + "," + pgV3Constants.TYPE_FORMULATIONPART;
			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Starts
			StringList slObjSelect = new StringList(3);
			slObjSelect.add(DomainConstants.SELECT_TYPE);
			slObjSelect.add(DomainConstants.SELECT_ID);
			slObjSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);

			StringList slRelSelect = new StringList(6);
			slRelSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY);
			slRelSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
			slRelSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_TOTAL);
			slRelSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
			slRelSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_QUANTITY + "].inputvalue");
			slRelSelect.addElement(DomainRelationship.SELECT_ID);

			StringList slFOPObjectSelects = new StringList(2);
			slFOPObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
			slFOPObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);

			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Starts
			StringList slFormulationProcessObjectSelects = new StringList(3);
			slFormulationProcessObjectSelects.addElement(DomainConstants.SELECT_TYPE);
			slFormulationProcessObjectSelects.addElement(DomainConstants.SELECT_ID);
			slFormulationProcessObjectSelects.addElement(DomainConstants.SELECT_NAME);
			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Ends

			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {

				doFormulaObject = DomainObject.newInstance(context, strObjectId);
				Map mapFOPDetails = (Map) doFormulaObject.getInfo(context, slFOPObjectSelects);
				strBOMBaseQty = (String) mapFOPDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
				bdBOMBaseQuantity = new BigDecimal(strBOMBaseQty);
				strFOPBaseUOM = (String) mapFOPDetails.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);

				//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Starts
				mpListChildren = doFormulaObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PLANNEDFOR, pgV3Constants.TYPE_FORMULATIONPROCESS, slFormulationProcessObjectSelects, null, false, true, (short) 1, strFPObjWhere, "", 0);

				if (null != mpListChildren && mpListChildren.size() > 0) {
					int iChildrenSize = mpListChildren.size();
					for (int i = 0; i < iChildrenSize; i++) {
						mpTempChildData = (Map) mpListChildren.get(i);
						strChildType = (String) mpTempChildData.get(DomainConstants.SELECT_TYPE);
						strChildID = (String) mpTempChildData.get(DomainConstants.SELECT_ID);
						strFormulationProcessGCAS = (String) mpTempChildData.get(DomainConstants.SELECT_NAME);
						//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Ends
						if (TYPE_FORMULATIONPROCESS.equalsIgnoreCase(strChildType) && BusinessUtil.isNotNullOrEmpty(strChildID)) {
							DomainObject domObjectChild = DomainObject.newInstance(context, strChildID);

							mlFormulaIngredientValues = domObjectChild.getRelatedObjects(context,
									strRelPattern, //relationship Pattern
									strTypePattern, //type Pattern
									slObjSelect, //objectSelects
									slRelSelect, //relSelects
									false,//true, //getTo
									true,//false, //getFrom
									(short) 2, //recurse
									null, //objectWhere
									null, //relWhere
									0);

							if (!mlFormulaIngredientValues.isEmpty() && mlFormulaIngredientValues != null) {
								Iterator mapItr = mlFormulaIngredientValues.iterator();
								Iterator mapItrProcessingLoss = mlFormulaIngredientValues.iterator();
								ArrayList<Boolean> listProcessingLoss = new ArrayList<Boolean>();
								//Added by DSM(Sogeti) for 2018x.2 Defect #29682 - Starts
								ArrayList<Boolean> listProcessingLoss100Percent = new ArrayList<Boolean>();
								//Added by DSM(Sogeti) for 2018x.2 Defect #29682 - Ends

								while (mapItrProcessingLoss.hasNext()) {
									boolean bProcessingLossValidation = false;
									boolean bProcessingLoss100PercentLossValidation = false;
									bdDryWeight = BigDecimal.ZERO;
									mapFormulaIngredientsProcessingLoss = (Map) mapItrProcessingLoss.next();
									strIngredientTypePL = (String) mapFormulaIngredientsProcessingLoss.get(DomainConstants.SELECT_TYPE);
									if (UIUtil.isNotNullAndNotEmpty(strIngredientTypePL) && !strIngredientTypePL.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPROCESS) && !strIngredientTypePL.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPHASE)) {
										strProcessingLoss = (String) mapFormulaIngredientsProcessingLoss.get(pgV3Constants.SELECT_ATTRIBUTE_LOSS);
										//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
										String strTargetDryWeight = (String) mapFormulaIngredientsProcessingLoss.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY);
										if (UIUtil.isNotNullAndNotEmpty(strTargetDryWeight)) {
											dDryWeight = Double.parseDouble(strTargetDryWeight);
											bdDryWeight = BigDecimal.valueOf(dDryWeight);
										}
										if (bdDryWeight.compareTo(BigDecimal.ZERO) > 0) {
											bdSumofDryWeights = bdSumofDryWeights.add(bdDryWeight);
										}
										strSumOfDryweight = String.valueOf(bdSumofDryWeights);
										//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

										if (UIUtil.isNotNullAndNotEmpty(strProcessingLoss)) {
											bProcessingLossValidation = true;
										}
										listProcessingLoss.add(bProcessingLossValidation);
										//Added by DSM(Sogeti) for 2018x.2 Defect #29682 - Starts
										if (!strProcessingLoss.equalsIgnoreCase("100")) {
											bProcessingLoss100PercentLossValidation = true;
										}
										listProcessingLoss100Percent.add(bProcessingLoss100PercentLossValidation);
										//Added by DSM(Sogeti) for 2018x.2 Defect #29682 - Ends
									}
								}

								while (mapItr.hasNext()) {
									mapFormulaIngredients = (Map) mapItr.next();
									strIngredientType = (String) mapFormulaIngredients.get(DomainConstants.SELECT_TYPE);
									if (UIUtil.isNotNullAndNotEmpty(strIngredientType) && !strIngredientType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPROCESS) && !strIngredientType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPHASE)) {
										strTargetWeightDry = (String) mapFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY);
										if (UIUtil.isNotNullAndNotEmpty(strTargetWeightDry)) {
											dDryWeight = Double.parseDouble(strTargetWeightDry);
											bdDryWeight = BigDecimal.valueOf(dDryWeight);
										}

										strTargetWeightWet = (String) mapFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_TARGETWETWEIGHT);
										if (UIUtil.isNotNullAndNotEmpty(strTargetWeightWet)) {
											dWetWeight = Double.parseDouble(strTargetWeightWet);
											bdWetWeight = BigDecimal.valueOf(dWetWeight);
										}

										strIngredientBaseUOM = (String) mapFormulaIngredients.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);

										strFBOMRelId = (String) mapFormulaIngredients.get(DomainRelationship.SELECT_ID);

										if (listProcessingLoss.contains(true)) {
											//Modified by DSM(Sogeti) for 2018x.2 Defect #29682 - Starts
											if (!listProcessingLoss100Percent.contains(false)) {
												//Modified by DSM(Sogeti) for 2018x.2 Defect #29682 - Ends
												if (UIUtil.isNotNullAndNotEmpty(strIngredientType) && !strIngredientType.equalsIgnoreCase(pgV3Constants.TYPE_ANCILLARYRAWMATERIALPART)) {

													if (UIUtil.isNotNullAndNotEmpty(strFOPBaseUOM) && UIUtil.isNotNullAndNotEmpty(strIngredientBaseUOM) && strIngredientBaseUOM.equalsIgnoreCase(strFOPBaseUOM)) {
														//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
														String strQuantity = calculateBOMQuantityFOPChild(context, strBOMBaseQty, mapFormulaIngredients, strSumOfDryweight);
														//Modified by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

														if (UIUtil.isNotNullAndNotEmpty(strQuantity)) {
															dQuantityValue = Double.parseDouble(strQuantity);
															bdQuantityValue = BigDecimal.valueOf(dQuantityValue);
														}

														if (bdFinalQuantityValue.compareTo(bdQuantityValue) > 0) {

															bSAPBOMQuantityValidation = true;
														} else {
															bSAPBOMQuantityValidation = false;
															//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Starts
															//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
															sFinalMessage = getValidationWarningMessage(strFormulationProcessValidationMsg, strFormulationProcessGCAS, strFormulationProcessValidationMsgContinue);

															//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Ends
															if (strActionParameter.equalsIgnoreCase(pgV3Constants.VALIDATE_PARAMETER)) {

																iValidate = pgV3Constants.IVALIDATE_FOUR;
															}
															//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends
															break;
														}
													} else {
														bSAPBOMQuantityValidation = false;
														//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Starts
														//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
														sFinalMessage = getValidationWarningMessage(strFormulationProcessValidationMsg, strFormulationProcessGCAS, strFormulationProcessUOMValidationMsg);
														//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Ends
														if (strActionParameter.equalsIgnoreCase(pgV3Constants.VALIDATE_PARAMETER)) {

															iValidate = pgV3Constants.IVALIDATE_THREE;
														}
														//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends
														break;
													}
												} else {
													bSAPBOMQuantityValidation = false;
													//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Starts
													//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
													sFinalMessage = getValidationWarningMessage(strFormulationProcessValidationMsg, strFormulationProcessGCAS, strFormulationProcessTypeValidationMsg);
													//Added by DSM(Sogeti) for 2018x.2 : Modifiying Warning Messages Content : 10 Oct 2019 - Ends
													if (strActionParameter.equalsIgnoreCase(pgV3Constants.VALIDATE_PARAMETER)) {
														iValidate = pgV3Constants.IVALIDATE_TWO;
													}
													//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends
													break;
												}
											} else {
												//Modified by DSM(Sogeti) for 2018x.2 Defect #29682 - Starts
												bSAPBOMQuantityValidation = false;
												//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
												sFinalMessage = getValidationWarningMessage(strFormulationProcessValidationMsg, strFormulationProcessGCAS, strFormulationProcessPLossValidationMsg);
												if (strActionParameter.equalsIgnoreCase(pgV3Constants.VALIDATE_PARAMETER)) {

													iValidate = pgV3Constants.IVALIDATE_FIVE;
												}
												//Modified by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends
												break;
											}
											//Modified by DSM(Sogeti) for 2018x.2 Defect #29682 - Ends
										} else {

											bSAPBOMQuantityValidation = false;
										}

										if (bSAPBOMQuantityValidation) {

											if (bdQuantityValue.compareTo(BigDecimal.ZERO) > 0) {
												bdSumofBOMQuantities = bdSumofBOMQuantities.add(bdQuantityValue);
											}
											if (bdWetWeight.compareTo(BigDecimal.ZERO) > 0) {
												bdSumofWetWeight = bdSumofWetWeight.add(bdWetWeight);
											}
											if (bdDryWeight.compareTo(BigDecimal.ZERO) > 0) {
												bdSumofDryWeight = bdSumofDryWeight.add(bdDryWeight);
											}
										}
									}
								}
								//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
								//22x Upgrade - Added null and empty check on notice message.
								if (!strActionParameter.equalsIgnoreCase(pgV3Constants.VALIDATE_PARAMETER) && iReturn == 0 && UIUtil.isNotNullAndNotEmpty(sFinalMessage)) {
									MqlUtil.mqlCommand(context, "notice $1", sFinalMessage);
								}
								//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends
								if (bSAPBOMQuantityValidation) {

									if (bdSumofWetWeight.compareTo(BigDecimal.ZERO) > 0 && bdSumofDryWeight.compareTo(BigDecimal.ZERO) > 0) {

										bdCalculatedQty = bdSumofWetWeight.divide(bdSumofDryWeight, MathContext.DECIMAL128);
										bdCalculatedQty = bdCalculatedQty.multiply(bdBOMBaseQuantity);
										bdCalculatedQty = bdCalculatedQty.subtract(bdSumofBOMQuantities);
										bdCalculatedQty = bdCalculatedQty.divide(bdPercentageQty, 5, RoundingMode.HALF_UP);
										bdCalculatedQty = bdCalculatedQty.abs();
										if (bdCalculatedQty.compareTo(bdValidationValue) > 0) {
											iReturn = 1;
										}
									}
								} else {
									iReturn = 0;
								}
							}
							if (iReturn == 1) {
								String error_Firsthalf = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.ProductData.BOMeDelivery.BOMQuantityWarning");
								String FormulationProcess_Name = strFormulationProcessGCAS;
								String erorr_secondHalf = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.ProductData.BOMeDelivery.BOMQuantityWarningContinue");

								//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
								if (!strActionParameter.equalsIgnoreCase(pgV3Constants.VALIDATE_PARAMETER)) {
									MqlUtil.mqlCommand(context, "notice " + getValidationWarningMessage(error_Firsthalf, FormulationProcess_Name, erorr_secondHalf));
								} else {
									iValidate = pgV3Constants.IVALIDATE_SIX;
								}
								//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
		if (strActionParameter.equalsIgnoreCase(pgV3Constants.VALIDATE_PARAMETER)) {
			iReturn = iValidate;
		}
		//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends
		return iReturn;
	}

	//Modified by DSM(Sogeti) for 2018x.2 Requirements #21573,#24407,#25099,#29556,#29557,#29558,#29559	 - Ends

	/**
	 * Method returns the alternates connected to an object.
	 *
	 * @param context
	 * @param strObjectId : Object Id of the parent.
	 * @throws Exception when operation fails
	 */
	public MapList getAlternates(Context context, String strObjectId) throws Exception {

		MapList mlAltsList = null;
		StringList slAltObjSelects = new StringList(6);
		slAltObjSelects.add(DomainConstants.SELECT_ID);
		slAltObjSelects.add(DomainConstants.SELECT_NAME);
		slAltObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slAltObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		slAltObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		//Modified by DSM Sogeti for 2018x.3 Requirements #27166,#27207,#25923 -Starts
		slAltObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		//Modified by DSM Sogeti for 2018x.3 Requirements #27166,#27207,#25923 -Ends

		StringList slAltRelSelects = new StringList(1);
		slAltRelSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);

		DomainObject doParentObj = null;

		try {
			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {

				doParentObj = DomainObject.newInstance(context, strObjectId);

				mlAltsList = doParentObj.getRelatedObjects(context,
						DomainConstants.RELATIONSHIP_ALTERNATE,    // rel pattern
						//"*",     									// type pattern
						DomainConstants.QUERY_WILDCARD,            // type pattern
						slAltObjSelects,                            // obj selects
						slAltRelSelects,                            // rel selects
						false,                                    // to side
						true,                                    // from side
						(short) 1,                                // recursion level
						DomainConstants.EMPTY_STRING,                                // object where clause
						DomainConstants.EMPTY_STRING, 0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return mlAltsList;
	}

	/**
	 * getFPPObjectsForChildCOPInCOP- This method returns Released FPP object connected to the Second level COP in FPP BOM Structure.
	 *
	 * @param *
	 * @return Map
	 * @throws Exception
	 **/
	// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - Start
	//Modified by DSM Sogeti for 2018x.5 Requirement #30797 #33457 - Starts
	public Map getFPPObjectsForChildCOPInCOP(Context context, String strID, String strCOPToCOPQuantity, String strContextFPPId, StringList slCOPSubstituteIds, String strBOMBaseQuantity, BigDecimal bdIntermediateObjectQty, String strContextFPPBUOM, String strTopNodeSpecSubType, String sChildCOPSpecSubType, StringList slCOPSubsQuantities, StringList slCOPSubstituteTypes) throws Exception
	//Modified by DSM Sogeti for 2018x.5 Requirement #30797 #33457 - Ends
	// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - End
	{
		Map mpReturn = new HashMap();
		String strCOPId = strID;
		DomainObject doObjectCOP = null;
		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
		DomainObject doObjectContextFPP = null;
		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
		MapList mlConnectedFPPs = null;

		StringList slObjectSelects = new StringList(6);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_CURRENT);
		slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
		slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends

		StringList slRelSelects = new StringList(3);
		slRelSelects.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
		slRelSelects.add(DomainRelationship.SELECT_ID);
		slRelSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);

		String strCOPCUPITQuantity = DomainConstants.EMPTY_STRING;
		Pattern pIntermediateTypesForCOPToCOP = new Pattern(pgV3Constants.TYPE_FINISHEDPRODUCTPART);
		pIntermediateTypesForCOPToCOP.addPattern(pgV3Constants.TYPE_PGCUSTOMERUNITPART);
		pIntermediateTypesForCOPToCOP.addPattern(pgV3Constants.TYPE_PGINNERPACKUNITPART);
		Map mpFPPObjects = new HashMap();
		String strFPPId = DomainConstants.EMPTY_STRING;
		String strFPPCurrent = DomainConstants.EMPTY_STRING;
		String strFPPType = DomainConstants.EMPTY_STRING;
		Map mpsubstituteFPPObjects = new HashMap();
		String strSubstituteFPPId = DomainConstants.EMPTY_STRING;
		String strSubstituteFPPCurrent = DomainConstants.EMPTY_STRING;
		String strSubstituteFPPType = DomainConstants.EMPTY_STRING;
		MapList mlReleasedFPP = new MapList();
		StringList strListQtyValues = new StringList(1);
		String strObjWhere = "current==" + pgV3Constants.STATE_RELEASE + "";
		String strReleasedFPPBUOM = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
		String strReleasedFPPSpecSubType = DomainConstants.EMPTY_STRING;
		String strReleasedSubFPPSpecSubType = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
		String strIntermediateObjQty = DomainConstants.EMPTY_STRING;
		String strConnectedFPPLevel = DomainConstants.EMPTY_STRING;
		BigDecimal bdDenominator = new BigDecimal("1");
		BigDecimal bdNumerator = new BigDecimal("1");
		BigDecimal bdBOMQty = new BigDecimal("1");
		BigDecimal bdBOMQuantityCheck = new BigDecimal("9999999999.999");

		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
		StringList primaryCUPwhereUsedFPP = new StringList();
		boolean isPrimaryCupHasWhereUsedFPP = true;
		// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End

		String strBUoMITBPMP = pgV3Constants.BASEUNITOFMEASURE_IT + "|" + pgV3Constants.BASEUNITOFMEASURE_MP + "|" + pgV3Constants.BASEUNITOFMEASURE_BP;

		// Added by (DSM) Sogeti for 22x.02 REQ 46286 - Start
		Map mpIntermediatesubstituteFPPs = new HashMap();
		// Added by (DSM) Sogeti for 22x.02 REQ 46286 - End
		
		// Added by (DSM) Sogeti for 22x.05 April CW Defect 56797 - Start
		String strFirstLevelIntermediateQty = DomainConstants.EMPTY_STRING;
		// Added by (DSM) Sogeti for 22x.05 April CW Defect 56797 - End
		
		// Added by (DSM) Sogeti for 22x.06 June CW Defect 57676 - Start
		DomainObject doIntermediateObjId = null;
		StringList slIntermediateParentCurrent  = new StringList();
		// Added by (DSM) Sogeti for 22x.06 June CW Defect 57676 - End

		try {
			doObjectCOP = DomainObject.newInstance(context, strCOPId);

			// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
			String strgetRelProcessType = doObjectCOP.getInfo(context, DomainConstants.SELECT_TYPE);
			short expandLevelVal = 0;
			String expansionTypes = DomainConstants.EMPTY_STRING;
			//if type is Customer Part expand level should be one to get immediate where used FPP
			if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strgetRelProcessType)) {
				expandLevelVal = 1;
				expansionTypes = pgV3Constants.TYPE_FINISHEDPRODUCTPART;
			}
			if (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strgetRelProcessType)) {
				expandLevelVal = 2;
				expansionTypes = pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_FINISHEDPRODUCTPART;
			}
			if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strgetRelProcessType)) {
				expandLevelVal = 0;
				expansionTypes = pgV3Constants.TYPE_FINISHEDPRODUCTPART + "," + pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_PGINNERPACKUNITPART;
			}
			// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
			//Get all Released FPPs linked to child COP
			mlConnectedFPPs = doObjectCOP.getRelatedObjects(context,
					pgV3Constants.RELATIONSHIP_EBOM,    // relationship pattern
					expansionTypes, // Type pattern
					slObjectSelects,                // object selects
					slRelSelects,            // rel selects
					true,                                // to side
					false,                                // from side
					expandLevelVal,                // recursion level
					strObjWhere,                        // object where clause
					null, 0);                                // rel where clause
			int mlConnectedFPPSize = mlConnectedFPPs.size();
			bdNumerator = new BigDecimal(strCOPToCOPQuantity);
			// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Start
			for (int i = 0; i < mlConnectedFPPSize; i++) {
				mpFPPObjects = (Map) mlConnectedFPPs.get(i);
				
				logger.log(Level.INFO,"Where Used Released FPP Map : {0}",mpFPPObjects);
				//Setting the COPinCOP key value with COP Id for substitute grouping
				//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
				mpFPPObjects.put(pgV3Constants.MAP_KEY_SUBSTITUTE_FLAG_COP_IN_COP, strCOPId);
				//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
				strFPPId = (String) mpFPPObjects.get(DomainConstants.SELECT_ID);
				strFPPCurrent = (String) mpFPPObjects.get(DomainConstants.SELECT_CURRENT);
				strFPPType = (String) mpFPPObjects.get(DomainConstants.SELECT_TYPE);
				strConnectedFPPLevel = (String) mpFPPObjects.get(DomainConstants.SELECT_LEVEL);
				
				// Added by (DSM) Sogeti for 22x.06 June CW Defect 57676 - Start
				//logic to skip Released intermediate quantity which is having obsolete state parent FPPs Starts 
				if(UIUtil.isNotNullAndNotEmpty(strFPPType) && (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strFPPType) || pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strFPPType))){
					doIntermediateObjId = DomainObject.newInstance(context, strFPPId);
					slIntermediateParentCurrent = doIntermediateObjId.getInfoList(context, "to["+ pgV3Constants.RELATIONSHIP_EBOM+"].from.current");
					//logic to skip Obsolete FPPs
					if(slIntermediateParentCurrent !=null && !slIntermediateParentCurrent.contains(pgV3Constants.STATE_RELEASE)) {
						logger.log(Level.INFO,"Where Used to skip Released FPP Map : {0}",mpFPPObjects);
						continue;
					}
				}
				//logic to skip Released intermediate quantity which is having obsolete state parent FPPs Ends
				// Added by (DSM) Sogeti for 22x.06 June CW Defect 57676 - End
				
				if (parseValue(strConnectedFPPLevel) == 1) {
					bdDenominator = new BigDecimal("1");
					logger.log(Level.INFO,"bdDenominator1 : {0}",bdDenominator);
				}
				
				// Added by (DSM) Sogeti for 22x.05 April CW Defect 56797 - Start
				//Grouping Logic for Quantity Calculation with same IP/CUP in where used FPPs Starts
				if (parseValue(strConnectedFPPLevel) == 1) {
					strFirstLevelIntermediateQty = (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
					logger.log(Level.INFO,"strFirstLevelIntermediateQty1 : {0}",strFirstLevelIntermediateQty);
				}
				
				if (!bdBOMQty.toString().equalsIgnoreCase("1") && parseValue(strConnectedFPPLevel) >= 2) {
					bdDenominator = new BigDecimal("1");
					bdDenominator = bdDenominator.multiply(new BigDecimal(strFirstLevelIntermediateQty));
					logger.log(Level.INFO,"bdDenominator2 : {0}",bdDenominator);
					bdBOMQty = new BigDecimal("1");
				} else {
					bdBOMQty = new BigDecimal("1");
				}
				
				//Grouping Logic for Quantity Calculation with same IP/CUP in where used FPPs Ends
				// Added by (DSM) Sogeti for 22x.05 April CW Defect 56797 - Ends
				
				// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
				// if type is consumer part and where used FPP at level 3 and same level if primary cup have where used fpp  ignoring primary CUP where used FPP
				if (parseValue(strConnectedFPPLevel) == 3 && (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strgetRelProcessType))) {
					doObjectContextFPP = DomainObject.newInstance(context, strContextFPPId);
					primaryCUPwhereUsedFPP = doObjectContextFPP.getInfoList(context, "from[" + DomainConstants.RELATIONSHIP_EBOM + "].to.to[" + DomainConstants.RELATIONSHIP_EBOM + "].from.to[" + DomainConstants.RELATIONSHIP_EBOM + "].from.id");
					if (null != primaryCUPwhereUsedFPP && !primaryCUPwhereUsedFPP.isEmpty()) {
						if (primaryCUPwhereUsedFPP.contains(strFPPId)) {
							isPrimaryCupHasWhereUsedFPP = false;
						} else {
							isPrimaryCupHasWhereUsedFPP = true;
						}
					}
				} // Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
				//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Starts
				// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
				// type should be FPP and level should have below or equal to 3
				if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strFPPType)
						&& isPrimaryCupHasWhereUsedFPP && parseValue(strConnectedFPPLevel) <= 3) {
					// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
					//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Ends

					strReleasedFPPBUOM = (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
					//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
					strReleasedFPPSpecSubType = (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
					//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
				}
				
				// Modified by (DSM) Sogeti for 22x.05 April CW Defect 56526 - Start
				if(parseValue(strConnectedFPPLevel) <= 2) {
					strIntermediateObjQty = (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
					logger.log(Level.INFO,"strIntermediateObjQty1 : {0}", strIntermediateObjQty);
				}
				if(parseValue(strConnectedFPPLevel) == 3 && (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strFPPType))) {
					strIntermediateObjQty = (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
					logger.log(Level.INFO,"strIntermediateObjQty2 : {0}", strIntermediateObjQty);
				}
				// Modified by (DSM) Sogeti for 22x.05 April CW Defect 56526 - End
				if (UIUtil.isNotNullAndNotEmpty(strIntermediateObjQty)) {
					bdDenominator = bdDenominator.multiply(new BigDecimal(strIntermediateObjQty));
					logger.log(Level.INFO,"bdDenominator3 : {0}", bdDenominator);
				}
				
				//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Starts
				// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
				if ((pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strFPPType)
						&& !strFPPId.equalsIgnoreCase(strContextFPPId)
						&& isPrimaryCupHasWhereUsedFPP
						&& parseValue(strConnectedFPPLevel) <= 3)) {
					// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 End
					//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Ends
					mlReleasedFPP.add(mpFPPObjects);

					//If the context FPP Base Unit of Measure is SP/SW or if the Base Unit Measure of FPP linked to child COP is SP/SW, then final quantity will be -9.99
					if (pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strContextFPPBUOM)
							|| pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strContextFPPBUOM)
							|| pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strReleasedFPPBUOM)
							|| pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strReleasedFPPBUOM)) {
						strListQtyValues.add(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);
						continue;
					}
					//If the context FPP Base Unit of Measure is anything other than IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is anything other than IT/BP/MP
					if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM)
							&& !strBUoMITBPMP.contains(strContextFPPBUOM))
							&& (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM)
									&& !strBUoMITBPMP.contains(strReleasedFPPBUOM))) {
						System.out.println("bdDenominator>>>" + bdDenominator);
						if (bdDenominator.compareTo(BigDecimal.ZERO) != 0)
							logger.log(Level.INFO,"bdDenominator : {0}", bdDenominator);
							logger.log(Level.INFO,"bdNumerator : {0}", bdNumerator);
							
							bdBOMQty = bdNumerator.divide(bdDenominator, 8, RoundingMode.HALF_UP);
						
							logger.log(Level.INFO,"bdBOMQty1 : {0}", bdBOMQty);
							logger.log(Level.INFO,"strBOMBaseQuantity : {0}", strBOMBaseQuantity);
						
							bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
						
						logger.log(Level.INFO,"bdBOMQty2 : {0}",bdBOMQty);
						logger.log(Level.INFO,"bdIntermediateObjectQty : {0}",bdIntermediateObjectQty);
						
						bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty).setScale(3, BigDecimal.ROUND_HALF_UP);
						
						logger.log(Level.INFO,"bdBOMQty3 Final : {0}",bdBOMQty);
						
						// Modified by (DSM) Sogeti for 22x.05 April CW Defect 56797 - Ends
						// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - End
						
						//If the context FPP Base Unit of Measure is anything other than IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is IT/BP/MP
					} else if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM)
							&& !strBUoMITBPMP.contains(strContextFPPBUOM))
							&& (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM)
									&& (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strReleasedFPPBUOM)
											|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strReleasedFPPBUOM)
											|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strReleasedFPPBUOM)))) {
						bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
						//bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty);

						// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
						BigDecimal interMediateQuantity = getIntermediateQuantity(context, doObjectCOP, bdIntermediateObjectQty);
						if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strgetRelProcessType)
								|| pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strgetRelProcessType)) {
							bdBOMQty = bdBOMQty.multiply(interMediateQuantity);
						} else {
							bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty);
						}
						bdBOMQty = bdBOMQty.multiply(bdNumerator).setScale(3, BigDecimal.ROUND_HALF_UP);
						if (!pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strgetRelProcessType)) {
							bdBOMQty = bdBOMQty.divide(bdDenominator, 3, RoundingMode.HALF_UP);
						}
						// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

						//If the context FPP Base Unit of Measure is IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is anything other than IT/BP/MP
					} else if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM)
							&& (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strContextFPPBUOM)
									|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strContextFPPBUOM)
									|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strContextFPPBUOM)))
							&& (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM)
									&& !strBUoMITBPMP.contains(strReleasedFPPBUOM))) {
						if (bdDenominator.compareTo(BigDecimal.ZERO) != 0)
							// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Start
							logger.log(Level.INFO,"bdDenominator  : {0}",bdDenominator);
							bdBOMQty = bdNumerator.divide(bdDenominator, 8, RoundingMode.HALF_UP);
						logger.log(Level.INFO,"bdBOMQty1 : {0}",bdBOMQty);
						bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
						logger.log(Level.INFO,"bdBOMQty2 : {0}",bdBOMQty);
						// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Ends
						//If the context FPP Base Unit of Measure is IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is IT/BP/MP
					} else if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM)
							&& (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strContextFPPBUOM)
									|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strContextFPPBUOM)
									|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strContextFPPBUOM)))
							&& (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM)
									&& (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strReleasedFPPBUOM)
											|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strReleasedFPPBUOM)
											|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strReleasedFPPBUOM)))) {
						bdBOMQty = bdNumerator.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
					}

					//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
					if ((UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType)
							&& (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType)))
							|| (UIUtil.isNotNullAndNotEmpty(strReleasedFPPSpecSubType)
									&& (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedFPPSpecSubType)))) {
						bdBOMQty = new BigDecimal("1");
						if (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType)
								&& (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType))) {
							//Context FPP-Shippable HALB  and Linked FPP-Shippable HALB or FPP- Non Shippable HALB with IT/BP/MP
							if ((UIUtil.isNotNullAndNotEmpty(strReleasedFPPSpecSubType)
									&& (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedFPPSpecSubType)))
									|| (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM)
											&& (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strReleasedFPPBUOM)
													|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strReleasedFPPBUOM)
													|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strReleasedFPPBUOM)))) {
								bdBOMQty = bdNumerator.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);

							} else {

								//Context FPP-Shippable HALB  and Linked FPP- Not Shippable HALB and BUOM other than IT
								if (bdDenominator.compareTo(BigDecimal.ZERO) != 0)
									// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Start
									logger.log(Level.INFO,"bdDenominator  : {0}",bdDenominator);
									bdBOMQty = bdNumerator.divide(bdDenominator, 8, RoundingMode.HALF_UP);
								logger.log(Level.INFO,"bdBOMQty1 : {0}",bdBOMQty);
								bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
								logger.log(Level.INFO,"bdBOMQty2 : {0}",bdBOMQty);
								// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Ends
							}

						} else {

							//Context FPP- Non Shippable HALB and BUOM is IT and Linked FPP is Shippable HALB
							if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM)
									&& (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strContextFPPBUOM)
											|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strContextFPPBUOM)
											|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strContextFPPBUOM)))) {
								if (UIUtil.isNotNullAndNotEmpty(strReleasedFPPSpecSubType)
										&& (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedFPPSpecSubType))) {
									//Context FPP- Not Shippable HALB but IT and Linked FPP-Shippable HALB
									bdBOMQty = bdNumerator.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
								}
							} else {
								//Context FPP NON IT - NON SHIPPABLE HALB / Linked FPP Shippable HALB
								if (UIUtil.isNotNullAndNotEmpty(strReleasedFPPSpecSubType)
										&& (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedFPPSpecSubType))) {
									//Context FPP- Not Shippable HALB but IT and Linked FPP-Shippable HALB
									bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
									bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty);
									bdBOMQty = bdBOMQty.multiply(bdNumerator).setScale(3, BigDecimal.ROUND_HALF_UP);
								}
							}
						}
					}
					//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends

					//If the calculated quantity is zero or if it is greater than 9999999999.999, then final quantity will be -9.99
					if (bdBOMQty.compareTo(BigDecimal.ZERO) == 0 || bdBOMQty.compareTo(bdBOMQuantityCheck) > 0) {
						bdBOMQty = new BigDecimal(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);
					}
					strListQtyValues.add(bdBOMQty.toString());
				}
			}
			int slSubstituteIdSize = slCOPSubstituteIds.size();
			//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
			Map mapCOPBulkSubstitutes = null;
			Map mapCOPBulkSubsAlternates = null;
			//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends
			if (!slCOPSubstituteIds.isEmpty()) {
				for (int iCOPSubstitute = 0; iCOPSubstitute < slSubstituteIdSize; iCOPSubstitute++) {
					String strSubstituteId = (String) slCOPSubstituteIds.get(iCOPSubstitute);
					//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
					String strSubstituteQuantity = (String) slCOPSubsQuantities.get(iCOPSubstitute);
					//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends
					DomainObject doSubstituteId = DomainObject.newInstance(context, strSubstituteId);
					// Added by (DSM) Sogeti for 22x.02 REQ 46286 - Start
					String strSubstituteType = (String) slCOPSubstituteTypes.get(iCOPSubstitute);
					// Added by (DSM) Sogeti for 22x.02 REQ 46286 - End

					// Modified by (DSM) Sogeti for 22x.02 Defect 52923 - Start
					if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strSubstituteType)) {
						expandLevelVal = 1;
					}
					if (pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strSubstituteType)) {
						expandLevelVal = 2;
					}
					if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubstituteType)) {
						expandLevelVal = 0;
					}
					
					MapList mlSubstituteConnectedFPP = doSubstituteId.getRelatedObjects(context,
							pgV3Constants.RELATIONSHIP_EBOM,    // relationship pattern
							pgV3Constants.TYPE_FINISHEDPRODUCTPART + "," + pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_PGINNERPACKUNITPART, // Type pattern
							slObjectSelects,                // object selects
							slRelSelects,            // rel selects
							true,                                // to side
							false,                                // from side
							expandLevelVal,           		 // recursion level
							strObjWhere,                        // object where clause
							null, 0);                                // rel where clause
							
					// Modified by (DSM) Sogeti for 22x.02 Defect 52923 - End
					
					int imlSubstituteConnectedFPP = mlSubstituteConnectedFPP.size();
					for (int iSubstituteConnectedFPP = 0; iSubstituteConnectedFPP < imlSubstituteConnectedFPP; iSubstituteConnectedFPP++) {
						mpsubstituteFPPObjects = (Map) mlSubstituteConnectedFPP.get(iSubstituteConnectedFPP);
						//Setting the COPinCOP key value with COP Id for substitute grouping
						//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
						mpsubstituteFPPObjects.put(pgV3Constants.MAP_KEY_SUBSTITUTE_FLAG_COP_IN_COP, strCOPId);
						//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
						strSubstituteFPPId = (String) mpsubstituteFPPObjects.get(DomainConstants.SELECT_ID);
						strSubstituteFPPCurrent = (String) mpsubstituteFPPObjects.get(DomainConstants.SELECT_CURRENT);
						strSubstituteFPPType = (String) mpsubstituteFPPObjects.get(DomainConstants.SELECT_TYPE);
						strConnectedFPPLevel = (String) mpsubstituteFPPObjects.get(DomainConstants.SELECT_LEVEL);

						//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Starts
						// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - Start
						if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strSubstituteFPPType) && (parseValue(strConnectedFPPLevel) == 1 || parseValue(strConnectedFPPLevel) == 2 || parseValue(strConnectedFPPLevel) == 3)) {
							// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - End
							//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Ends
							strReleasedFPPBUOM = (String) mpsubstituteFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
							//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
							strReleasedSubFPPSpecSubType = (String) mpsubstituteFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
							//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends
						}
						
						// Added by (DSM) Sogeti for 22x.05 April CW Defect 56797 - Start
						if (parseValue(strConnectedFPPLevel) == 1)
							bdDenominator = new BigDecimal("1");
						
						//Grouping Logic for Quantity Calculation with same IP/CUP in where used FPPs Starts
						if (parseValue(strConnectedFPPLevel) == 1) {
							strFirstLevelIntermediateQty = (String) mpsubstituteFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
						}
						
						if (!bdBOMQty.toString().equalsIgnoreCase("1") && parseValue(strConnectedFPPLevel) >= 2) {
							bdDenominator = new BigDecimal("1");
							bdDenominator = bdDenominator.multiply(new BigDecimal(strFirstLevelIntermediateQty));
							bdBOMQty = new BigDecimal("1");
						} else {
							bdBOMQty = new BigDecimal("1");
						}
						//Grouping Logic for Quantity Calculation with same IP/CUP in where used FPPs Ends
						// Added by (DSM) Sogeti for 22x.05 April CW Defect 56797 - Ends
						
						// Modified by (DSM) Sogeti for 22x.05 April CW Defect 56526,56797 - Start
						if(parseValue(strConnectedFPPLevel) <= 2) {
							strIntermediateObjQty = (String) mpsubstituteFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
						}
						if(parseValue(strConnectedFPPLevel) == 3 && (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strSubstituteFPPType))) {
							strIntermediateObjQty = (String) mpsubstituteFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
						}
						// Modified by (DSM) Sogeti for 22x.05 April CW Defect 56526,56797 - End

						if (UIUtil.isNotNullAndNotEmpty(strIntermediateObjQty)) {
							bdDenominator = bdDenominator.multiply(new BigDecimal(strIntermediateObjQty));
						}
						//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Starts
						// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - Start
						// Modified by (DSM) Sogeti for 22x.02 Defect 52923 - Start
						if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strSubstituteFPPType) && !strSubstituteFPPId.equalsIgnoreCase(strContextFPPId)
								&& (parseValue(strConnectedFPPLevel) == 1 || parseValue(strConnectedFPPLevel) == 2 || parseValue(strConnectedFPPLevel) == 3)) {
							// Modified by (DSM) Sogeti for 22x.02 Defect 52923 - End
							// Modified by (DSM) Sogeti for 22x.02 REQ 46286 - End
							//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Ends
							mlReleasedFPP.add(mpsubstituteFPPObjects);
							//Modified by DSM Sogeti for 2018x.5 Defect #36454 - Starts
							bdNumerator = new BigDecimal(strSubstituteQuantity);
							//Modified by DSM Sogeti for 2018x.5 Defect #36454 - Ends
							//If the context FPP Base Unit of Measure is SP/SW or if the Base Unit Measure of FPP linked to child COP is SP/SW, then final quantity will be -9.99
							if (pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strReleasedFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strReleasedFPPBUOM)) {
								strListQtyValues.add(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);
								continue;
							}

							//If the context FPP Base Unit of Measure is anything other than IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is anything other than IT/BP/MP
							if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM) && !strBUoMITBPMP.contains(strContextFPPBUOM)) && (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM) && !strBUoMITBPMP.contains(strReleasedFPPBUOM))) {
								if (bdDenominator.compareTo(BigDecimal.ZERO) != 0)
									// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Start
									logger.log(Level.INFO,"bdDenominator  : {0}",bdDenominator);
									bdBOMQty = bdNumerator.divide(bdDenominator, 8, RoundingMode.HALF_UP);
								logger.log(Level.INFO,"bdBOMQty1 : {0}",bdBOMQty);
								bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
								logger.log(Level.INFO,"bdBOMQty2 : {0}",bdBOMQty);
								bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty).setScale(3, BigDecimal.ROUND_HALF_UP);
								logger.log(Level.INFO,"bdBOMQty3 : {0}",bdBOMQty);
								// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Ends
								//If the context FPP Base Unit of Measure is anything other than IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is IT/BP/MP
							} else if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM) && !strBUoMITBPMP.contains(strContextFPPBUOM)) && (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM) && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strReleasedFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strReleasedFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strReleasedFPPBUOM)))) {
								bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
								bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty);
								bdBOMQty = bdBOMQty.multiply(bdNumerator).setScale(3, BigDecimal.ROUND_HALF_UP);

								//If the context FPP Base Unit of Measure is IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is anything other than IT/BP/MP
							} else if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM) && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strContextFPPBUOM))) && (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM) && !strBUoMITBPMP.contains(strReleasedFPPBUOM))) {
								if (bdDenominator.compareTo(BigDecimal.ZERO) != 0)
									// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Start
									logger.log(Level.INFO,"bdDenominator  : {0}",bdDenominator);
									bdBOMQty = bdNumerator.divide(bdDenominator, 8, RoundingMode.HALF_UP);
								logger.log(Level.INFO,"bdBOMQty1 : {0}",bdBOMQty);
								bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
								logger.log(Level.INFO,"bdBOMQty2 : {0}",bdBOMQty);
								// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Ends
								//If the context FPP Base Unit of Measure is IT/BP/MP and if the Base Unit Measure of FPP linked to child COP is IT/BP/MP
							} else if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM) && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strContextFPPBUOM))) && (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM) && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strReleasedFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strReleasedFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strReleasedFPPBUOM)))) {
								bdBOMQty = bdNumerator.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
							}

							//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Starts
							if ((UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType))) || (UIUtil.isNotNullAndNotEmpty(strReleasedSubFPPSpecSubType) && (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedSubFPPSpecSubType)))) {
								bdBOMQty = new BigDecimal("1");
								if (UIUtil.isNotNullAndNotEmpty(strTopNodeSpecSubType) && (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strTopNodeSpecSubType))) {
									//Context FPP-Shippable HALB  and Linked FPP-Shippable HALB or FPP- Non Shippable HALB with IT/BP/MP

									if ((UIUtil.isNotNullAndNotEmpty(strReleasedSubFPPSpecSubType) && (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedSubFPPSpecSubType))) || (UIUtil.isNotNullAndNotEmpty(strReleasedFPPBUOM) && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strReleasedFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strReleasedFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strReleasedFPPBUOM)))) {

										bdBOMQty = bdNumerator.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);

									} else {

										//Context FPP-Shippable HALB  and Linked FPP- Not Shippable HALB and BUOM other than IT
										if (bdDenominator.compareTo(BigDecimal.ZERO) != 0)
											// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Start
											logger.log(Level.INFO,"bdDenominator  : {0}",bdDenominator);
											bdBOMQty = bdNumerator.divide(bdDenominator, 8, RoundingMode.HALF_UP);
										logger.log(Level.INFO,"bdBOMQty1 : {0}",bdBOMQty);
										bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
										logger.log(Level.INFO,"bdBOMQty2 : {0}",bdBOMQty);
										// Modified by (DSM) Sogeti for 22x.05 April CW Defect 57118 - Start
									}

								} else {

									if ((UIUtil.isNotNullAndNotEmpty(strContextFPPBUOM) && (pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strContextFPPBUOM)))) {

										if (UIUtil.isNotNullAndNotEmpty(strReleasedSubFPPSpecSubType) && (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedSubFPPSpecSubType))) {
											//Context FPP- Not Shippable HALB but IT and Linked FPP-Shippable HALB
											bdBOMQty = bdNumerator.multiply(new BigDecimal(strBOMBaseQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
										}
									} else {
										//Context FPP- Not Shippable HALB but IT and Linked FPP-Shippable HALB
										if (UIUtil.isNotNullAndNotEmpty(strReleasedSubFPPSpecSubType) && (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strReleasedSubFPPSpecSubType))) {
											bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
											bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty);
											bdBOMQty = bdBOMQty.multiply(bdNumerator).setScale(3, BigDecimal.ROUND_HALF_UP);
										}
									}
								}
							}
							//Modified by DSM Sogeti for 2018x.5 Requirement #30797 - Ends


							//If the calculated quantity is zero or if it is greater than 9999999999.999, then final quantity will be -9.99
							if (bdBOMQty.compareTo(BigDecimal.ZERO) == 0 || bdBOMQty.compareTo(bdBOMQuantityCheck) > 0) {
								bdBOMQty = new BigDecimal(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED);
							}
							strListQtyValues.add(bdBOMQty.toString());
							//Modified by DSM(Sogeti) for 2015x.5.1 SAP BOM as Fed Defect #19320  - Ends
						}
					}
					//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
					if (pgV3Constants.BULK_INTERMEDIATE_UNIT.equalsIgnoreCase(sChildCOPSpecSubType)) {
						mapCOPBulkSubstitutes = getCOPBulkSubstitutes(context, strID, strSubstituteId, strBOMBaseQuantity, bdIntermediateObjectQty, strContextFPPBUOM, strTopNodeSpecSubType, strSubstituteQuantity);
						if (null != mapCOPBulkSubstitutes && mapCOPBulkSubstitutes.size() > 0) {
							mlReleasedFPP.add(mapCOPBulkSubstitutes);
							strListQtyValues.add((String) mapCOPBulkSubstitutes.get(pgV3Constants.MAP_KEY_COP_BULK_SUBS_QUANTITY));

							mapCOPBulkSubsAlternates = getCOPBulkSubstitutesAlternates(context, mapCOPBulkSubstitutes);
							if (null != mapCOPBulkSubsAlternates && mapCOPBulkSubsAlternates.size() > 0) {

								mlReleasedFPP.addAll((MapList) mapCOPBulkSubsAlternates.get("finalMapList"));
								strListQtyValues.addAll((StringList) mapCOPBulkSubsAlternates.get("slCOPBulkAltsQtyValues"));
							}

						}
					}
					//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends

					// Added by (DSM) Sogeti for 22x.02 REQ 46286 - Start
					if ((UIUtil.isNotNullAndNotEmpty(strSubstituteType)) && pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strSubstituteType)) {

						mpIntermediatesubstituteFPPs = doSubstituteId.getInfo(context, slObjectSelects);

						if (null != mpIntermediatesubstituteFPPs && mpIntermediatesubstituteFPPs.size() > 0) {

							mpIntermediatesubstituteFPPs.put(pgV3Constants.MAP_KEY_SUBSTITUTE_FLAG_COP_IN_COP, strCOPId);

							bdNumerator = new BigDecimal(strSubstituteQuantity);
							bdBOMQty = bdNumerator.multiply(new BigDecimal(strBOMBaseQuantity));

							mlReleasedFPP.add(mpIntermediatesubstituteFPPs);
							strListQtyValues.add(bdBOMQty.toString());
						}

					}
					// Added by (DSM) Sogeti for 22x.02 REQ 46286 - End
				}
			}
			mpReturn.put("strListQtyValues", strListQtyValues);
			mpReturn.put("mlReleasedFPP", mlReleasedFPP);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return mpReturn;
	}

	/**
	 * getValidFormulationProcess- Method is used to get valid Formualtion Process.
	 *
	 * @param context       the eMatrix <code>Context</code> object
	 * @param strID         -  parent object Id
	 * @param strFOPNextRev - Next revision object Id
	 * @return MapList - Formulation Process details.
	 * @throws Exception
	 */
	public MapList getValidFormulationProcess(Context context, String strID, String strFOPNextRev) throws Exception {
		DomainObject doTS = null;
		MapList mpListChildFP = new MapList();
		String sLastFormulationProcessID = DomainConstants.EMPTY_STRING;
		String strFPObjWhere = DomainConstants.EMPTY_STRING;
		try {
			if (BusinessUtil.isNotNullOrEmpty(strID)) {
				doTS = DomainObject.newInstance(context, strID);
				if (UIUtil.isNotNullAndNotEmpty(strFOPNextRev)) {
					strFPObjWhere = "(current==Release && (revision==last||next.current!= Release))";
				} else {
					strFPObjWhere = "revision == last";
				}
				mpListChildFP = doTS.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PLANNEDFOR, pgV3Constants.TYPE_FORMULATIONPROCESS, SL_OBJECT_INFO_SELECT, SL_RELATION_INFO_SELECT_OTHERS, false, true, (short) 1, strFPObjWhere, "", 0);

				//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
				//int mpListChildFPSize = mpListChildFP.size();

				if (mpListChildFP.size() == 0) {

					sLastFormulationProcessID = getFinalFormulationProcessDetails(context, strID);
					strFPObjWhere = (new StringBuilder()).append(DomainConstants.SELECT_ID).append("==").append(sLastFormulationProcessID).toString();
					mpListChildFP = doTS.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PLANNEDFOR, pgV3Constants.TYPE_FORMULATIONPROCESS, SL_OBJECT_INFO_SELECT, SL_RELATION_INFO_SELECT_OTHERS, false, true, (short) 1, strFPObjWhere, "", 0);

				}
				//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends
			}

		} catch (Exception ex) {
			throw ex;
		}
		return mpListChildFP;
	}


	//Added by DSM(Sogeti) for 2018x.1.1 Requirement #25101 Starts
	//Modified by DSM(Sogeti) for 2018x.2 Defect #28692 Starts
	public int checkFormulationIngredientsAttributes(Context context, String[] args) throws Exception {
		String strObjectId = args[0];
		String strObjectType = args[1];
		int iReturn = 0;
		try {
			String strRelPattern = DomainConstants.EMPTY_STRING;
			String strTypePattern = DomainConstants.EMPTY_STRING;
			String strIngredientType = DomainConstants.EMPTY_STRING;
			String strTargetWeightDry = DomainConstants.EMPTY_STRING;
			String strTargetWeightWet = DomainConstants.EMPTY_STRING;
			String strDryPercentage = DomainConstants.EMPTY_STRING;
			String strFormulationProcessGCAS = DomainConstants.EMPTY_STRING;

			String strChildType = DomainConstants.EMPTY_STRING;
			String strChildID = DomainConstants.EMPTY_STRING;
			String TYPE_FORMULATIONPROCESS = pgV3Constants.TYPE_FORMULATIONPROCESS;

			ArrayList<Boolean> listTargetWetWeights = new ArrayList<Boolean>();
			ArrayList<Boolean> listTargetDryWeights = new ArrayList<Boolean>();
			ArrayList<Boolean> listDryPercentage = new ArrayList<Boolean>();

			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Starts
			String strFPObjWhere = "revision==last";
			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Ends
			boolean bWeightsFilled = false;
			boolean bWeightsEmpty = false;
			boolean bWeightsPartiallyFilled = false;
			DomainObject doFormulaObject = null;
			MapList mlFormulaIngredientValues = null;
			MapList mpListChildren = null;
			Map mapFormulaIngredients = null;
			Map mpTempChildData = null;
			Map mapFOPDetails = null;

			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Starts
			strRelPattern = pgV3Constants.RELATIONSHIP_PLBOM;
			strTypePattern = pgV3Constants.TYPE_FORMULATIONPHASE + "," + pgV3Constants.TYPE_RAWMATERIALPART + "," + pgV3Constants.TYPE_FORMULATIONPART;
			//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Ends

			StringList slObjSelect = new StringList(2);
			slObjSelect.add(DomainConstants.SELECT_TYPE);
			slObjSelect.add(DomainConstants.SELECT_ID);

			StringList slRelSelect = new StringList(3);
			slRelSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_TARGETWEIGHTDRY + "].inputvalue");
			slRelSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_TARGETWETWEIGHT + "].inputvalue");
			slRelSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_TOTAL + "].inputvalue");

			StringList slFormulationProcessObjectSelects = new StringList(3);
			slFormulationProcessObjectSelects.addElement(DomainConstants.SELECT_TYPE);
			slFormulationProcessObjectSelects.addElement(DomainConstants.SELECT_ID);
			slFormulationProcessObjectSelects.addElement(DomainConstants.SELECT_NAME);

			if (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				doFormulaObject = DomainObject.newInstance(context, strObjectId);
				String sStrType = doFormulaObject.getType(context);
				//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Starts
				if (sStrType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPART)) {
					mpListChildren = doFormulaObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PLANNEDFOR, pgV3Constants.TYPE_FORMULATIONPROCESS, slFormulationProcessObjectSelects, null, false, true, (short) 1, strFPObjWhere, "", 0);
					if (null != mpListChildren && mpListChildren.size() > 0) {
						int iChildrenSize = mpListChildren.size();

						for (int i = 0; i < iChildrenSize; i++) {
							mpTempChildData = (Map) mpListChildren.get(i);
							strChildType = (String) mpTempChildData.get(DomainConstants.SELECT_TYPE);
							strChildID = (String) mpTempChildData.get(DomainConstants.SELECT_ID);
							strFormulationProcessGCAS = (String) mpTempChildData.get(DomainConstants.SELECT_NAME);

							if (TYPE_FORMULATIONPROCESS.equalsIgnoreCase(strChildType) && BusinessUtil.isNotNullOrEmpty(strChildID)) {
								DomainObject domObjectChild = DomainObject.newInstance(context, strChildID);
								mlFormulaIngredientValues = domObjectChild.getRelatedObjects(context,
										strRelPattern, //relationship Pattern
										strTypePattern, //type Pattern
										slObjSelect, //objectSelects
										slRelSelect, //relSelects
										false,//true, //getTo
										true,//false, //getFrom
										(short) 3, //recurse
										null, //objectWhere
										null, //relWhere
										0);
							}
						}
					}

				} else {
					strFormulationProcessGCAS = (String) doFormulaObject.getInfo(context, DomainConstants.SELECT_NAME);
					mlFormulaIngredientValues = doFormulaObject.getRelatedObjects(context,
							strRelPattern, //relationship Pattern
							strTypePattern, //type Pattern
							slObjSelect, //objectSelects
							slRelSelect, //relSelects
							false,//true, //getTo
							true,//false, //getFrom
							(short) 3, //recurse
							null, //objectWhere
							null, //relWhere
							0);
				}
				//Added by DSM(Sogeti) for 2018x.2 : Multiple Formulation Process Case : 10 Oct 2019 - Ends
				if (!mlFormulaIngredientValues.isEmpty() && mlFormulaIngredientValues != null) {
					Iterator mapItr = mlFormulaIngredientValues.iterator();
					while (mapItr.hasNext()) {
						boolean bWetWeightFilled = false;
						boolean bDryWeightFilled = false;
						boolean bDryPercentageFilled = false;

						mapFormulaIngredients = (Map) mapItr.next();
						strIngredientType = (String) mapFormulaIngredients.get(DomainConstants.SELECT_TYPE);
						strTargetWeightDry = (String) mapFormulaIngredients.get("attribute[" + pgV3Constants.ATTRIBUTE_TARGETWEIGHTDRY + "].inputvalue");
						strTargetWeightWet = (String) mapFormulaIngredients.get("attribute[" + pgV3Constants.ATTRIBUTE_TARGETWETWEIGHT + "].inputvalue");
						strDryPercentage = (String) mapFormulaIngredients.get("attribute[" + pgV3Constants.ATTRIBUTE_TOTAL + "].inputvalue");

						if (UIUtil.isNotNullAndNotEmpty(strIngredientType) && !strIngredientType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPROCESS) && !strIngredientType.equalsIgnoreCase(pgV3Constants.TYPE_FORMULATIONPHASE)) {
							if (UIUtil.isNotNullAndNotEmpty(strTargetWeightDry) && UIUtil.isNotNullAndNotEmpty(strTargetWeightWet) && UIUtil.isNotNullAndNotEmpty(strDryPercentage)) {
								bWeightsFilled = true;

							} else if (UIUtil.isNullOrEmpty(strTargetWeightDry) && UIUtil.isNullOrEmpty(strTargetWeightWet) && UIUtil.isNullOrEmpty(strDryPercentage)) {
								bWeightsEmpty = true;
							}
							if (UIUtil.isNotNullAndNotEmpty(strTargetWeightWet)) {
								bWetWeightFilled = true;
							}
							if (UIUtil.isNotNullAndNotEmpty(strTargetWeightDry)) {
								bDryWeightFilled = true;
							}

							if (UIUtil.isNotNullAndNotEmpty(strDryPercentage)) {
								bDryPercentageFilled = true;
							}

							listTargetWetWeights.add(bWetWeightFilled);
							listTargetDryWeights.add(bDryWeightFilled);
							listDryPercentage.add(bDryPercentageFilled);

						}
					}

					if (listTargetWetWeights.contains(true) && listTargetWetWeights.contains(false)) {
						bWeightsPartiallyFilled = true;

					} else if (listTargetDryWeights.contains(true) && listTargetDryWeights.contains(false)) {

						bWeightsPartiallyFilled = true;

					} else if (listDryPercentage.contains(true) && listDryPercentage.contains(false)) {

						bWeightsPartiallyFilled = true;
					}

					if ((bWeightsFilled && bWeightsEmpty) || bWeightsPartiallyFilled) {
						iReturn = 1;
						String error_Firsthalf = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.FormulationProcess.FormulaIngredientsCheck.Alert");
						String FormulationProcess_Name = strFormulationProcessGCAS;
						String erorr_secondHalf = EnoviaResourceBundle.getProperty(context, "emxCPNStringResource", context.getLocale(), "emxCPN.FormulationProcess.FormulaIngredientsCheck.AlertContinue");
						StringBuilder error_Full = new StringBuilder();
						error_Full.append(error_Firsthalf);
						error_Full.append(FormulationProcess_Name);
						error_Full.append(erorr_secondHalf);
						MqlUtil.mqlCommand(context, "notice " + error_Full.toString());
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return iReturn;
	}
	//Added by DSM(Sogeti) for 2018x.1.1 Requirement #25101 Ends
	//Modified by DSM(Sogeti) for 2018x.2 Defect #28692 Ends

	/**
	 * getInstanceList- Method is used to get dynamically string/string list values .
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param input   - object
	 * @return String List - Final Value.
	 */
	public StringList getInstanceList(Object input) {
		StringList returnList = new StringList();
		if (null != input) {

			if (input instanceof StringList) {

				returnList = (StringList) input;
			} else {

				returnList.add(input.toString());
			}
		}
		return returnList;
	}

	//Addes by DSM(Sogeti) - for 2018x.2 CUP Reshipper Requirements #29029 #29030 - Starts
	//CUP Reshipper Quantity Calculation
	private String getReshipperQuantityValue(Context context, DomainObject doObject, boolean isReshipperSub, String strChildQuantity, String strTopNodeBOMQty, String strBUOM, float fQtyMultiplier) throws Exception {

		float fChildSAPQty = 1;
		String strListQty = DomainConstants.EMPTY_STRING;
		String strType = DomainConstants.EMPTY_STRING;
		try {
			if (doObject != null) {
				strType = doObject.getType(context);
			}
			// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
			if (strType.equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART)
					|| strType.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)
					|| strType.equalsIgnoreCase(pgV3Constants.TYPE_PGINNERPACKUNITPART)) {
				fQtyMultiplier = 1;
			}
			// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END

			// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 Start
			if (!(pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM))) {
				// Modified by (DSM-Sogeti) for (June 2022) Requirement 42807 END
				if (UIUtil.isNotNullAndNotEmpty(strTopNodeBOMQty) && UIUtil.isNotNullAndNotEmpty(strChildQuantity)) {
					BigDecimal bdfChildSAPQty = BigDecimal.valueOf(fChildSAPQty);
					bdfChildSAPQty = bdfChildSAPQty.multiply(new BigDecimal(strTopNodeBOMQty)).multiply(BigDecimal.valueOf(fQtyMultiplier)).multiply(new BigDecimal(strChildQuantity)).setScale(3, BigDecimal.ROUND_HALF_UP);
					strListQty = String.valueOf(bdfChildSAPQty);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
		return strListQty;
	}
	//Added by DSM(Sogeti) - for 2018x.2 CUP Reshipper Requirements #29029 #29030 - Ends

	//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Starts
	// To get the TotalDryWeight
	private String getTotalDryweight(Context context, DomainObject doPart) throws Exception {

		double dDryWeight = 0.0;

		BigDecimal bdSumofDryWeights = BigDecimal.ZERO;
		StringList slTotalDryWeight = new StringList();
		String strSumOfDryweight = DomainConstants.EMPTY_STRING;
		String SELECT_TARGET_DRY_WEIGHT = new StringBuffer("from[").append(pgV3Constants.RELATIONSHIP_PLBOM).append("].to.from[").append(pgV3Constants.RELATIONSHIP_PLBOM).append("].").append(pgV3Constants.SELECT_ATTRIBUTE_TARGETWEIGHTDRY).toString();

		slTotalDryWeight = doPart.getInfoList(context, SELECT_TARGET_DRY_WEIGHT);

		if (!slTotalDryWeight.isEmpty()) {
			for (String strTargetDryWeight : slTotalDryWeight) {
				BigDecimal bdDryWeight = BigDecimal.ZERO;
				if (UIUtil.isNotNullAndNotEmpty(strTargetDryWeight)) {
					dDryWeight = Double.parseDouble(strTargetDryWeight);
					bdDryWeight = BigDecimal.valueOf(dDryWeight);
				}
				if (bdDryWeight.compareTo(BigDecimal.ZERO) > 0) {
					bdSumofDryWeights = bdSumofDryWeights.add(bdDryWeight);
				}
				strSumOfDryweight = String.valueOf(bdSumofDryWeights);
			}
		}
		return strSumOfDryweight;
	}
	//Added by DSM(Sogeti) - for 2018x.2.1 Requirement #31136 - Ends

	//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Starts
	//To get the ValidationWarningMessage
	private String getValidationWarningMessage(String sMsgStart, String sPartName, String sMsgEnd) throws Exception {
		StringBuilder sbValidationWarningMessage = new StringBuilder();
		sbValidationWarningMessage.append(sMsgStart);
		sbValidationWarningMessage.append(sPartName);
		sbValidationWarningMessage.append(sMsgEnd);

		String sValidationWarningMessage = sbValidationWarningMessage.toString();
		return sValidationWarningMessage;
	}
	//Added by DSM(Sogeti) for 2018x.3 Validation Button Requirement #29649 - Ends

	/**
	 * getFinalFormulationProcessDetails- Method is used to get Formulation Process Data
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param input   - String
	 * @return String - Final Value : Object ID.
	 */
	//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
	public String getFinalFormulationProcessDetails(Context context, String strID) throws Exception {

		int iRevisionIndex;
		boolean isAllvaluesProd = false;

		Map mpFormulationProcessDetails = new HashMap();

		Object oFormulationProcessID = null;
		Object oRelOriginated = null;
		Object oReleasePhaseValue = null;
		Object oFormulationProcessRevision = null;

		String strFinalFormulationProcessID = DomainConstants.EMPTY_STRING;

		StringList slFormulationProcessObjectSelects = new StringList(4);
		slFormulationProcessObjectSelects.addElement(pgV3Constants.SELECT_FORMULATION_PROCESS_ID);
		slFormulationProcessObjectSelects.addElement(pgV3Constants.SELECT_FORMULATION_PROCESS_REVISION);
		slFormulationProcessObjectSelects.addElement(pgV3Constants.SELECT_FORMULATION_PROCESS_RELEASE_PHASE);
		slFormulationProcessObjectSelects.addElement(pgV3Constants.SELECT_FORMULATION_PROCESS_ORIGINATED);
		try {

			mpFormulationProcessDetails = BusinessUtil.getInfoList(context, strID, slFormulationProcessObjectSelects);

			oFormulationProcessID = (mpFormulationProcessDetails).get(pgV3Constants.SELECT_FORMULATION_PROCESS_ID);
			StringList slFormulationProcessID = new StringList();

			if (null != oFormulationProcessID) {
				slFormulationProcessID = getInstanceList(oFormulationProcessID);
			}

			oRelOriginated = (Object) (mpFormulationProcessDetails).get(pgV3Constants.SELECT_FORMULATION_PROCESS_ORIGINATED);
			StringList slRelOriginated = new StringList();

			if (null != oRelOriginated) {
				slRelOriginated = getInstanceList(oRelOriginated);
			}

			oReleasePhaseValue = (Object) (mpFormulationProcessDetails).get(pgV3Constants.SELECT_FORMULATION_PROCESS_RELEASE_PHASE);
			StringList slReleasePhaseValues = new StringList();

			if (null != oReleasePhaseValue) {
				slReleasePhaseValues = getInstanceList(oReleasePhaseValue);
			}

			oFormulationProcessRevision = (Object) (mpFormulationProcessDetails).get(pgV3Constants.SELECT_FORMULATION_PROCESS_REVISION);

			StringList slFormulationProcessRevision = new StringList();

			if (null != oFormulationProcessRevision) {
				slFormulationProcessRevision = getInstanceList(oFormulationProcessRevision);
			}

			int iSizeOfFormulationProcess = slFormulationProcessID.size();

			if (null != slFormulationProcessID && iSizeOfFormulationProcess > 0) {

				isAllvaluesProd = Collections.frequency(slReleasePhaseValues, pgV3Constants.ATTRIBUTE_STAGE_PRODUCTION_VALUE) == slReleasePhaseValues.size();

				for (int iF = 0; iF < iSizeOfFormulationProcess; iF++) {
					strFinalFormulationProcessID = (String) slFormulationProcessID.get(iF);
				}

				if (isAllvaluesProd && iSizeOfFormulationProcess > 1) {
					iRevisionIndex = getIndexHighestRevisionConnected(slFormulationProcessRevision);
					strFinalFormulationProcessID = (String) slFormulationProcessID.get(iRevisionIndex);
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return strFinalFormulationProcessID;
	}
	//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends

	/**
	 * getIndexHighestRevisionConnected- Method is used to get connected highest Formulation Process revision
	 *
	 * @param input - StringList
	 * @return int - Final Value
	 */

	//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Starts
	public int getIndexHighestRevisionConnected(StringList slFormulationProcessRevision) throws Exception {

		int maxVal;
		int maxIdx = -1;

		try {
			ArrayList<Integer> intList = new ArrayList<Integer>();
			for (String s : slFormulationProcessRevision) {
				intList.add(Integer.valueOf(s));
			}

			if (!intList.isEmpty()) {
				maxVal = Collections.max(intList); // should return 7
				maxIdx = intList.indexOf(maxVal); // should return 2 (position of the value 7)
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return maxIdx;
	}
	//Modified by DSM Sogeti for 2018x.5 Defect #32945 - Ends

	/**
	 * getVITargetPercentConsumed- Method is used to get VI Percentage
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param input   - boolean
	 * @param input   - String
	 * @return String - Final Value
	 */

	//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 -Starts
	public String getVITargetPercentConsumed(Context context, boolean isFixed, String SRelID) throws Exception {
		String sPercent = DomainConstants.EMPTY_STRING;
		String quantityInVI = DomainConstants.EMPTY_STRING;
		String currentQuantity = DomainConstants.EMPTY_STRING;
		DomainObject domVI = null;
		try {
			DomainRelationship domEBOMRel = new DomainRelationship(SRelID);
			Map attributeMap = domEBOMRel.getAttributeMap(context, true);
			String viPhysicalId = (String) attributeMap.get(FormulationAttribute.VIRTUAL_INTERMEDIATE_PHYSICAL_ID.getAttribute(context));

			if (UIUtil.isNotNullAndNotEmpty(viPhysicalId)) {

				if (!isFixed) {

					quantityInVI = (String) attributeMap.get(FormulationAttribute.TARGET_PERCENT_WET_IN_VIRTUAL_INTERMEDIATE.getAttribute(context));
					currentQuantity = (String) attributeMap.get(FormulationAttribute.QUANTITY.getAttribute(context));
					double dQuantityInVI = Double.parseDouble(quantityInVI);
					if (dQuantityInVI > 0) {
						double targetPercent = (Double.parseDouble(currentQuantity) / dQuantityInVI) * 100.0;
						sPercent = Double.toString(targetPercent);
					}
				} else {
					domVI = DomainObject.newInstance(context, viPhysicalId);
					sPercent = domVI.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_TARGET_PERCENT_AS_CONSUMED);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sPercent;
	}

	/**
	 * getVISubstituteDetails- Method is used to get VI Substitutes
	 *
	 * @param context the eMatrix <code>Context</code> object
	 * @param input   - String
	 * @param input   - String
	 * @return Map - Final Value
	 */

	public Map getVISubstituteDetails(Context context, String sVIObjID, String strIngredientId) throws Exception {

		DomainObject domVIObj = new DomainObject(sVIObjID);
		Map mapVISubstituteDetails = new HashMap();
		Map mpTempChildData = new HashMap();
		Map mpPLBOMData = new HashMap();
		Map phaseVIInfo = new HashMap();

		DomainObject domObjectChild = null;
		DomainObject domVIPhase = null;

		MapList mlVIPLBOMParts = new MapList();
		MapList mlVIPhases = new MapList();

		String phaseVIId = DomainConstants.EMPTY_STRING;
		String strFormulationChildId = DomainConstants.EMPTY_STRING;


		String strObjectWhere = new StringBuffer(DomainObject.SELECT_CURRENT).append(" != ").append(pgV3Constants.STATE_PART_OBSOLETE).toString();

		StringList slSubstituteId = new StringList();
		StringList slSubstituteType = new StringList();
		StringList slSubstituteName = new StringList();
		StringList slSubstituteBaseUOM = new StringList();
		StringList slSubstituteEffectivityDate = new StringList();
		StringList slSubstituteRelID = new StringList();
		StringList slSubstituteFormulationType = new StringList();


		try {

			if (null != domVIObj) {

				mlVIPhases = domVIObj.getRelatedObjects(context,
						pgV3Constants.RELATIONSHIP_PLBOM,    //relationshipPattern
						//pgV3Constants.SYMBOL_STAR, 			//typePattern
						DomainConstants.QUERY_WILDCARD,        // type pattern
						false,                                //getTo
						true,                                //getFrom
						(short) 1,                            //recurseToLevel
						SL_FOP_OBJECT_INFO_SELECT,            //objectSelects
						SL_RELATION_INFO_SELECT_OTHERS,    //relationshipSelects
						strObjectWhere,                    //objectWhere
						null,                                //relationshipWhere
						null,                                //PostRelPattern
						null,            //PostTypePattern
						null);

				if (mlVIPhases != null && mlVIPhases.size() > 0) {

					Collections.sort(mlVIPhases, new Comparator() {
						public int compare(Object first, Object second) {
							Map firstMap = (Map) first;
							Map secondMap = (Map) second;
							String firstValue = (String) (firstMap.get(DomainConstants.SELECT_NAME));
							String secondValue = (String) secondMap.get(DomainConstants.SELECT_NAME);
							return firstValue.compareTo(secondValue);
						}
					});

					for (int l = 0; l < mlVIPhases.size(); l++) {

						phaseVIInfo = (Map) mlVIPhases.get(l);
						phaseVIId = (String) phaseVIInfo.get(DomainConstants.SELECT_ID);

						domVIPhase = DomainObject.newInstance(context, phaseVIId);

						mlVIPLBOMParts = domVIPhase.getRelatedObjects(context,
								pgV3Constants.RELATIONSHIP_PLBOM,    //relationshipPattern
								//pgV3Constants.SYMBOL_STAR, 			//typePattern
								DomainConstants.QUERY_WILDCARD,        // type pattern
								false,                                //getTo
								true,                                //getFrom
								(short) 2,                            //recurseToLevel
								SL_FOP_OBJECT_INFO_SELECT,            //objectSelects
								SL_RELATION_INFO_SELECT_OTHERS,    //relationshipSelects
								strObjectWhere,                    //objectWhere
								null,                                //relationshipWhere
								null,                                //PostRelPattern
								null,            //PostTypePattern
								null);

						if (null != mlVIPLBOMParts && mlVIPLBOMParts.size() > 0) {

							int iPLBOMSize = mlVIPLBOMParts.size();

							Object oSubstituteId = null;
							Object oSubstituteType = null;
							Object oSubstituteName = null;
							Object oSubstituteBaseUOM = null;
							Object substituteEffectivityDate = null;
							Object oSubstituteRelID = null;
							Object substituteFormulationType = null;


							for (int j = 0; j < iPLBOMSize; j++) {

								mpPLBOMData = (Map) mlVIPLBOMParts.get(j);
								strFormulationChildId = (String) mpPLBOMData.get(DomainConstants.SELECT_ID);

								if (BusinessUtil.isNotNullOrEmpty(strFormulationChildId) && BusinessUtil.isNotNullOrEmpty(strIngredientId) && strIngredientId.equalsIgnoreCase(strFormulationChildId)) {

									oSubstituteId = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_ID);
									slSubstituteId = new StringList();
									if (null != oSubstituteId) {
										slSubstituteId = getInstanceList(oSubstituteId);
									}

									oSubstituteType = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_TYPE);
									slSubstituteType = new StringList();
									if (null != oSubstituteType) {
										slSubstituteType = getInstanceList(oSubstituteType);
									}

									oSubstituteName = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_NAME);
									slSubstituteName = new StringList();
									if (null != oSubstituteName) {
										slSubstituteName = getInstanceList(oSubstituteName);
									}

									oSubstituteBaseUOM = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_BASE_UOM);
									slSubstituteBaseUOM = new StringList();
									if (null != oSubstituteBaseUOM) {
										slSubstituteBaseUOM = getInstanceList(oSubstituteBaseUOM);
									}

									substituteEffectivityDate = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_EFFECTIVITY_DATE);
									slSubstituteEffectivityDate = new StringList();
									if (null != substituteEffectivityDate) {
										slSubstituteEffectivityDate = getInstanceList(substituteEffectivityDate);
									}

									oSubstituteRelID = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_REL_ID);
									slSubstituteRelID = new StringList();
									if (null != oSubstituteRelID) {
										slSubstituteRelID = getInstanceList(oSubstituteRelID);
									}

									substituteFormulationType = (Object) mpPLBOMData.get(pgV3Constants.SELECT_FBOM_SUBSTITUTE_FORMULATION_TYPE);
									slSubstituteFormulationType = new StringList();
									if (null != substituteFormulationType) {
										slSubstituteFormulationType = getInstanceList(substituteFormulationType);
									}

									mapVISubstituteDetails.put("slSubstituteId", slSubstituteId);
									mapVISubstituteDetails.put("slSubstituteType", slSubstituteType);
									mapVISubstituteDetails.put("slSubstituteName", slSubstituteName);
									mapVISubstituteDetails.put("slSubstituteBaseUOM", slSubstituteBaseUOM);
									mapVISubstituteDetails.put("slSubstituteEffectivityDate", slSubstituteEffectivityDate);
									mapVISubstituteDetails.put("slSubstituteRelID", slSubstituteRelID);
									mapVISubstituteDetails.put("slSubstituteFormulationType", slSubstituteFormulationType);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapVISubstituteDetails;
	}
	//Modified by DSM(Sogeti) - for 2018x.5 Defect #30820 -Ends

	/**
	 * doBOMeDeliveryOnAuthorizedToUseModify- This method will be invoked on "Authorized to Use"
	 * attribute modification to trigger BOM eDelivery.
	 *
	 * @param *
	 * @return int
	 * @throws Exception
	 **/

	//Added by DSM Sogeti for 2018x.5 Requirement #33310 - Starts
	public int doBOMeDeliveryOnAuthorizedToUseModify(Context context, String[] methodArgs) throws Exception {
		int iReturn = 0;
		String sPartObjectType = DomainConstants.EMPTY_STRING;
		String sPartObjectName = DomainConstants.EMPTY_STRING;
		String sPartObjectRev = DomainConstants.EMPTY_STRING;
		String sPartObjectID = DomainConstants.EMPTY_STRING;
		String sPlantObjectId = DomainConstants.EMPTY_STRING;
		String sNewAttrVal = DomainConstants.EMPTY_STRING;
		String strCurrent = DomainConstants.EMPTY_STRING;
		DomainObject doTS = null;
		try {
			if (null != methodArgs && methodArgs.length >= 6) {
				sPartObjectID = methodArgs[0];
				//methodArgs[5] = "Dummy";

				sPartObjectType = methodArgs[1];
				sPartObjectName = methodArgs[2];
				sPartObjectRev = methodArgs[3];
				sPlantObjectId = methodArgs[4];
				sNewAttrVal = methodArgs[5];
				String[] strArgs = new String[6];
				strArgs[0] = sPartObjectID;
				strArgs[1] = sPartObjectType;
				strArgs[2] = sPartObjectName;
				strArgs[3] = sPartObjectRev;
				strArgs[4] = sPlantObjectId;
				strArgs[5] = pgV3Constants.DUMMY_ARG;

				if (UIUtil.isNotNullAndNotEmpty(sNewAttrVal) && sNewAttrVal.equalsIgnoreCase(pgV3Constants.KEY_TRUE) && UIUtil.isNotNullAndNotEmpty(sPartObjectID)) {
					doTS = DomainObject.newInstance(context, sPartObjectID);
					strCurrent = doTS.getInfo(context, DomainConstants.SELECT_CURRENT);
					if (strCurrent.equalsIgnoreCase(pgV3Constants.STATE_RELEASE)) {
						iReturn = JPO.invoke(context, "pgIPMUtil_Deferred", null, "doBOMeDelivery", strArgs);
					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return iReturn;
	}
	//Added by DSM Sogeti for 2018x.5 Requirement #33310 - Ends

	/**
	 * getCOPBulkSubstitutes- Method is used to get APP/DPP/PAP Details Connected as Substitutes to COP BULK
	 *
	 * @param *
	 * @return Map - Final Value.
	 * @throws Exception
	 */

	//Added by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
	public Map getCOPBulkSubstitutes(Context context, String strID, String strSubstituteId, String strBOMBaseQuantity, BigDecimal bdIntermediateObjectQty, String sContextFPPBUOM, String sTopNodeSpecSubType, String strSubstituteQuantity) throws Exception {

		String sQuantity = DomainConstants.EMPTY_STRING;
		String sCOPSubsType = DomainConstants.EMPTY_STRING;
		String sCOPSubsSpecSubType = DomainConstants.EMPTY_STRING;
		String strIntermediateObjQty = DomainConstants.EMPTY_STRING;

		BigDecimal bdSubsQuantity = new BigDecimal("1");
		Map mapDetailsCOPCub = new HashMap();


		StringList slObjectSelects = new StringList(8);
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_NAME);
		slObjectSelects.add(DomainConstants.SELECT_CURRENT);
		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
		slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		slObjectSelects.add(pgV3Constants.SELECT_COP_BULK_SUBSTITUTE_QUANTITY);
		slObjectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		try {

			DomainObject doObjectCOPSub = DomainObject.newInstance(context, strSubstituteId);
			if (doObjectCOPSub != null) {
				mapDetailsCOPCub = doObjectCOPSub.getInfo(context, slObjectSelects);
				sCOPSubsType = (String) mapDetailsCOPCub.get(DomainConstants.SELECT_TYPE);
				sCOPSubsSpecSubType = (String) mapDetailsCOPCub.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
				//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
				//strIntermediateObjQty = (String)mapDetailsCOPCub.get(pgV3Constants.SELECT_COP_BULK_SUBSTITUTE_QUANTITY);
				strIntermediateObjQty = strSubstituteQuantity;
				//Modified by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends
				if (UIUtil.isNotNullAndNotEmpty(strIntermediateObjQty)) {
					bdSubsQuantity = bdSubsQuantity.multiply(new BigDecimal(strIntermediateObjQty));
				}

				if ((pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sCOPSubsType) || pgV3Constants.TYPE_DEVICEPRODUCTPART.equalsIgnoreCase(sCOPSubsType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sCOPSubsType)) && !(pgV3Constants.THIRD_PARTY.equalsIgnoreCase(sCOPSubsSpecSubType))) {

					sQuantity = getCOPBulkSubstitutesQuantity(bdSubsQuantity, bdIntermediateObjectQty, sContextFPPBUOM, strBOMBaseQuantity, sTopNodeSpecSubType);

					mapDetailsCOPCub.put(pgV3Constants.MAP_KEY_COP_BULK_SUBS_QUANTITY, sQuantity);
					mapDetailsCOPCub.put(pgV3Constants.MAP_KEY_SUBSTITUTE_FLAG_COP_IN_COP, strID);
					mapDetailsCOPCub.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
				} else {
					mapDetailsCOPCub = new HashMap();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapDetailsCOPCub;
	}
	//Added by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends

	/**
	 * getCOPBulkSubstitutesQuantity- Method is used to get APP/DPP/PAP Quantities Connected as Substitutes to COP BULK
	 *
	 * @param *
	 * @return String - Final Value.
	 * @throws Exception
	 */

	//Added by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
	public String getCOPBulkSubstitutesQuantity(BigDecimal bdSubsQuantity, BigDecimal bdIntermediateObjectQty, String sContextFPPBUOM, String strBOMBaseQuantity, String sTopNodeSpecSubType) throws Exception {
		String strBUoMITBPMP = pgV3Constants.BASEUNITOFMEASURE_IT + "|" + pgV3Constants.BASEUNITOFMEASURE_MP + "|" + pgV3Constants.BASEUNITOFMEASURE_BP;
		String sQuantity = DomainConstants.EMPTY_STRING;
		BigDecimal bdBOMQty = new BigDecimal("1");
		BigDecimal bdBOMQuantityCheck = new BigDecimal("9999999999.999");
		try {
			if (pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(sContextFPPBUOM) || pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(sContextFPPBUOM)) {
				sQuantity = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
			} else if (strBUoMITBPMP.contains(sContextFPPBUOM) || pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(sTopNodeSpecSubType)) {
				//Formula IT/BP/MP or Shippable HALB: SAP BOM Quantity = (BOM Base Quantity * Substitute Quantity)
				bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
				bdBOMQty = bdBOMQty.multiply(bdSubsQuantity).setScale(3, BigDecimal.ROUND_HALF_UP);

			} else {
				//Formula CS& Others : SAP BOM Quantity = (BOM Base Quantity * Substitute Quantity * Intermediate Quantity)
				bdBOMQty = bdBOMQty.multiply(new BigDecimal(strBOMBaseQuantity));
				bdBOMQty = bdBOMQty.multiply(bdSubsQuantity);
				bdBOMQty = bdBOMQty.multiply(bdIntermediateObjectQty).setScale(3, BigDecimal.ROUND_HALF_UP);
			}

			if ((bdBOMQty.compareTo(BigDecimal.ZERO) == 0) || (bdBOMQty.compareTo(bdBOMQuantityCheck) > 0)) {
				sQuantity = pgV3Constants.STR_DEFAULT_QTY_IF_FAILED;
			}

			if (!sQuantity.equalsIgnoreCase(pgV3Constants.STR_DEFAULT_QTY_IF_FAILED)) {
				sQuantity = bdBOMQty.toPlainString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sQuantity;
	}
	//Added by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts

	/**
	 * getCOPBulkSubstitutesAlternates- Method is used to get APP Alternates Quantities Connected as Substitutes to COP BULK
	 *
	 * @param Context,Map
	 * @return Map - Final Value.
	 * @throws Exception
	 */

	//Added by DSM Sogeti for Requirement #33457 of 2018x.5 - Starts
	public Map getCOPBulkSubstitutesAlternates(Context context, Map mCOPBulkSubstituteMap) throws Exception {

		StringList slCOPBulkAltsQtyValues = new StringList();
		Map mpAltMap = new HashMap();
		Map mpFinalAltMap = new HashMap();
		MapList finalMapList = new MapList();
		MapList mlSubAlts = new MapList();
		String strSubAltSpecSubType = DomainConstants.EMPTY_STRING;
		try {

			String sCOPSubsType = (String) mCOPBulkSubstituteMap.get(DomainConstants.SELECT_TYPE);
			String sCOPSubsID = (String) mCOPBulkSubstituteMap.get(DomainConstants.SELECT_ID);
			String sCOPSubsQty = (String) mCOPBulkSubstituteMap.get(pgV3Constants.MAP_KEY_COP_BULK_SUBS_QUANTITY);
			String sCOPinCOPID = (String) mCOPBulkSubstituteMap.get(pgV3Constants.MAP_KEY_SUBSTITUTE_FLAG_COP_IN_COP);
			//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Starts
			if (UIUtil.isNotNullAndNotEmpty(sCOPSubsType) && COP_BULK_ALTERNATES_ALLOWED_TYPES.contains(sCOPSubsType)) {
				//Modified by DSM Sogeti for Requirement #36213, #37646 2018x.6 - Ends
				mlSubAlts = getAlternates(context, sCOPSubsID);
				for (int i = 0; i < mlSubAlts.size(); i++) {
					mpAltMap = (Map) mlSubAlts.get(i);
					strSubAltSpecSubType = (String) mpAltMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
					if (!pgV3Constants.THIRD_PARTY.equalsIgnoreCase(strSubAltSpecSubType)) {
						mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
						mpAltMap.put(pgV3Constants.MAP_KEY_COP_BULK_SUBS_QUANTITY, sCOPSubsQty);
						mpAltMap.put(pgV3Constants.MAP_KEY_SUBSTITUTE_FLAG_COP_IN_COP, sCOPinCOPID);
						finalMapList.add(mpAltMap);
						slCOPBulkAltsQtyValues.add(sCOPSubsQty);
					}
				}
				mpFinalAltMap.put("slCOPBulkAltsQtyValues", slCOPBulkAltsQtyValues);
				mpFinalAltMap.put("finalMapList", finalMapList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mpFinalAltMap;
	}
	//Added by DSM Sogeti for Requirement #33457 of 2018x.5 - Ends

	/**
	 * getFPPObjectsForCOPBulk- Method is used to get FPP Details of COP BULK
	 *
	 * @param Context,Map
	 * @return MapList - Final Value.
	 * @throws Exception
	 */

	//Added by DSM Sogeti for Requirement #33443 #33444 2018x.5 - Starts
	public MapList getFPPObjectsForCOPBulk(Context context, Map mapCOPDetails) throws Exception {
		String strCOPId = (String) mapCOPDetails.get(DomainConstants.SELECT_ID);
		DomainObject doObjectCOP = null;
		MapList mlConnectedFPPs = new MapList();
		MapList mlConnectedFPPsCOP = new MapList();

		Map mpFPPObjects = new HashMap();
		Map mpTemp = new HashMap();
		String strFPPType = DomainConstants.EMPTY_STRING;
		String strFPPSpecSubType = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Starts
		String strConnectedFPPLevel = DomainConstants.EMPTY_STRING;
		//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Ends
		String strObjWhere = "current==" + pgV3Constants.STATE_RELEASE + "";

		StringList slEBOMTarget = new StringList(16);
		slEBOMTarget.add(DomainConstants.SELECT_TYPE);
		slEBOMTarget.add(DomainConstants.SELECT_NAME);
		slEBOMTarget.add(DomainConstants.SELECT_REVISION);
		slEBOMTarget.add(DomainConstants.SELECT_ID);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
		slEBOMTarget.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);


		try {
			doObjectCOP = DomainObject.newInstance(context, strCOPId);
			//Get all Released FPPs linked to child COP
			mlConnectedFPPs = doObjectCOP.getRelatedObjects(context,
					pgV3Constants.RELATIONSHIP_EBOM,    // relationship pattern
					pgV3Constants.TYPE_FINISHEDPRODUCTPART + "," + pgV3Constants.TYPE_PGCUSTOMERUNITPART + "," + pgV3Constants.TYPE_PGINNERPACKUNITPART, // Type pattern
					slEBOMTarget,// object selects
					null,// rel selects
					true,// to side
					false,// from side
					(short) 0,// recursion level
					strObjWhere,// object where clause
					null, 0);// rel where clause

			int mlConnectedFPPsSize = mlConnectedFPPs.size();
			for (int i = 0; i < mlConnectedFPPsSize; i++) {
				mpTemp = new HashMap();
				mpFPPObjects = (Map) mlConnectedFPPs.get(i);
				strFPPType = (String) mpFPPObjects.get(DomainConstants.SELECT_TYPE);
				strFPPSpecSubType = (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
				//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Starts
				strConnectedFPPLevel = (String) mpFPPObjects.get(DomainConstants.SELECT_LEVEL);

				if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strFPPType) && (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strFPPSpecSubType)) && (parseValue(strConnectedFPPLevel) == 2 || parseValue(strConnectedFPPLevel) == 3)) {
					//Modified by DSM Sogeti for 2018x.5 December Release Defect #37548 - Ends
					mpTemp.putAll(mapCOPDetails);
					mpTemp.put(DomainConstants.SELECT_ID, (String) mpFPPObjects.get(DomainConstants.SELECT_ID));
					mpTemp.put(DomainConstants.SELECT_TYPE, (String) mpFPPObjects.get(DomainConstants.SELECT_TYPE));
					mpTemp.put(DomainConstants.SELECT_NAME, (String) mpFPPObjects.get(DomainConstants.SELECT_NAME));
					mpTemp.put(DomainConstants.SELECT_REVISION, (String) mpFPPObjects.get(DomainConstants.SELECT_REVISION));
					mpTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE, (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE));
					mpTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE, (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE));
					mpTemp.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
					mpTemp.put(pgV3Constants.SELECT_ATTRIBUTE_STATUS, (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_STATUS));
					mpTemp.put(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE, (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE));
					mpTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE, (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE));
					mpTemp.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE, (String) mpFPPObjects.get(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE));

					mlConnectedFPPsCOP.add(mpTemp);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return mlConnectedFPPsCOP;
	}
	//Added by DSM Sogeti for Requirement #33443 #33444 2018x.5 - Ends
	//Added by DSM(Sogeti) for 2018x.6 Dec CW SAP Requirement #40805 - Starts

	/**
	 * @param context
	 * @param args
	 * @return
	 * @throws FrameworkException
	 */
	public boolean markForBOMeDeliveryOnAlternateSnapDiff(Context context, String[] args) throws FrameworkException {
		boolean isPassed = Boolean.TRUE;
		boolean isPushContext = Boolean.FALSE;
		try {
			if (args.length == 0) {
				throw new IllegalArgumentException();
			}
			String sId = args[0];
			String sType = args[1];

			if (UIUtil.isNotNullAndNotEmpty(sType) && UIUtil.isNotNullAndNotEmpty(sId)
					&& MarkUtils.isAllowedTypeForSAPBOMeDelivery(context, sType, "Alternate")) {

				//Need to push the context to be able the update flag for SAP BOM eDelivery.
				ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				isPushContext = Boolean.TRUE;

				DomainObject currDomObject = DomainObject.newInstance(context, sId);
				StringList slCurrAlternates = currDomObject.getInfoList(context, pgV3Constants.SELECT_ALTERNATE_ID);

				String sPreviousId = currDomObject.getInfo(context, pgV3Constants.SELECT_PREVIOUS_ID);
				// Added by DSM (Sogeti) for 18x.6 DEC CW Defect# 45017 - Starts
				boolean isRawMaterial = pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(sType);
				boolean isHavingAlternateSnapshot = false;
				// Added by DSM (Sogeti) for 18x.6 DEC CW Defect# 45017 - Ends
				boolean isApolloAPPData = Boolean.FALSE;
				if (pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(sType)) {
					String sAuthApplication = currDomObject.getInfo(context,
							pgV3Constants.SELECT_ATTRIBUTE_PGAUTHORINGAPPLICATION);
					if (UIUtil.isNotNullAndNotEmpty(sAuthApplication)
							&& pgV3Constants.RANGE_PGAUTHORINGAPPLICATION_LPD.equals(sAuthApplication)) {
						isApolloAPPData = Boolean.TRUE;
						MarkUtils.markBOMeDeliveryParentFlag(context, currDomObject);
					}
				}

				if (!isApolloAPPData && UIUtil.isNotNullAndNotEmpty(sPreviousId)) {
					DomainObject prevDomObject = DomainObject.newInstance(context, sPreviousId);
					StringList slPrevAlternates = prevDomObject.getInfoList(context, pgV3Constants.SELECT_ALTERNATE_ID);
					isHavingAlternateSnapshot = isSnapshotDifference(slCurrAlternates, slPrevAlternates);
					if (isHavingAlternateSnapshot) {
						MarkUtils.markBOMeDeliveryParentFlag(context, currDomObject);
					}
				}
				// Added by DSM (Sogeti) for 18x.6 DEC CW Defect# 45017 - Starts
				if (isRawMaterial && isHavingAlternateSnapshot) {
					currDomObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_TRUE);
				}
				// Added by DSM (Sogeti) for 18x.6 DEC CW Defect# 45017 - Ends
			}
		} catch (Exception e) {
			Logger.getLogger(pgEDeliverBOMToSAP_mxJPO.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		} finally {
			if (isPushContext)
				ContextUtil.popContext(context);
		}
		return isPassed;
	}

	/**
	 * @param listOne
	 * @param listTwo
	 * @return
	 */
	public static boolean isSnapshotDifference(StringList listOne, StringList listTwo) {
		boolean isDifferent = Boolean.FALSE;
		final int listOneSize = listOne != null ? listOne.size() : 0;
		final int listTwoSize = listTwo != null ? listTwo.size() : 0;

		if (listOneSize != listTwoSize) {
			isDifferent = true; // meaning there is a difference.
		}
		if (listOneSize == listTwoSize) {
			for (int i = 0; i < listOneSize; i++) {
				// if list two does not contain atleast one element then break.
				if (!listTwo.contains(listOne.get(i))) {
					isDifferent = Boolean.TRUE; // meaning there is a difference. break out of loop.
					break;
				}
			}
		}
		return isDifferent;
	}
	//Added by DSM(Sogeti) for 2018x.6 Dec CW SAP Requirement #40805 - Ends


	/**
	 * Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)
	 *
	 * @param context
	 * @return String array
	 * @throws Exception
	 */
	public synchronized String[] getConfig(Context context) {
		String[] connectionParameterArr = new String[0];
		com.pg.dsm.sapview.config.SAPRFCConfig rfcConfig = new com.pg.dsm.sapview.config.SAPRFCConfig.Builder(context).build();
		if (rfcConfig.isLoaded()) {
			connectionParameterArr = rfcConfig.getConnectionParameterArray();
		} else {
			Logger.getLogger(pgEDeliverBOMToSAP_mxJPO.class.getName()).log(Level.WARNING, "Failed to load SAP Connection Configurations with error: {0}", rfcConfig.getErrorMessage());
		}
		return connectionParameterArr;
	}

	/**
	 * Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704)
	 *
	 * @param strArrConnectionParams
	 * @param strGCAS
	 * @param strArrEligiblePlants
	 * @param alPartRelatedObjects
	 * @return String
	 * @throws Exception
	 */
	public synchronized String initSapConnectionandStore(String[] strArrConnectionParams, String strGCAS, String[] strArrEligiblePlants, ArrayList alPartRelatedObjects) throws Exception {
		com.pg.dsm.sapview.services.SAPPush sapPush = new com.pg.dsm.sapview.services.SAPPush(strArrConnectionParams);
		return sapPush.initSapConnectionAndStore(strGCAS, strArrEligiblePlants, alPartRelatedObjects);
	}

	/**
	 * @param objectMap
	 * @param selectable
	 * @return
	 */
	private StringList getStringListFromMap(Map<Object, Object> objectMap, String selectable) {
		StringList retList = new StringList();
		Object resList = (objectMap).get(selectable);
		if (null != resList) {
			if (resList instanceof StringList) {
				retList = (StringList) resList;
			} else {
				retList.add(resList.toString());
			}
		}
		return retList;
	}

	/**
	 * added for (DSM-Sogeti) for (June 2022) Defect ID : 48497
	 *
	 * @param context
	 * @param strPreviousObjectId
	 * @param strPreviousTypeToSecondLevelCOP
	 * @param strObjectType
	 * @return
	 * @throws FrameworkException
	 */
	private boolean isMoreThanOnePrimaryCOP(Context context, String strPreviousObjectId, String strPreviousTypeToSecondLevelCOP, String strObjectType) throws FrameworkException {
		boolean multiplePrimaryCOP = false;
		MapList childObjConnectedFPP = new MapList();
		StringList slObjectSelects = new StringList();
		slObjectSelects.add(DomainConstants.SELECT_ID);
		slObjectSelects.add(DomainConstants.SELECT_TYPE);
		String strObjWhere = "current!=" + pgV3Constants.STATE_OBSOLETE + "";
		if (UIUtil.isNotNullAndNotEmpty(strPreviousTypeToSecondLevelCOP)
				&& (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strPreviousTypeToSecondLevelCOP)
						|| pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strPreviousTypeToSecondLevelCOP))) {
			DomainObject dobPrimaryCop = DomainObject.newInstance(context, strPreviousObjectId);
			Map mpTemp = new HashMap();
			childObjConnectedFPP = dobPrimaryCop.getRelatedObjects(context,
					pgV3Constants.RELATIONSHIP_EBOM,    // relationship pattern
					pgV3Constants.TYPE_PGCONSUMERUNITPART, // Type pattern
					slObjectSelects,                // object selects
					null,            // rel selects
					false,                                // to side
					true,                                // from side
					(short) 1,                            // recursion level
					strObjWhere,                        // object where clause
					null, 0);
			if (!childObjConnectedFPP.isEmpty() && childObjConnectedFPP.size() > 1) {
				multiplePrimaryCOP = true;
			}
		}
		return multiplePrimaryCOP;
	}

	/**
	 * added for (DSM-Sogeti) for (June 2022) Requirement 42807 End
	 *
	 * @param context
	 * @param strProcessDOB
	 * @param bdIntermediateObjectQty
	 * @return
	 * @throws FrameworkException
	 */
	private BigDecimal getIntermediateQuantity(Context context, DomainObject strProcessDOB, BigDecimal bdIntermediateObjectQty) throws FrameworkException {

		MapList childObjConnectedFPP = new MapList();
		StringList slObjectSelects = new StringList();
		StringList slRelSelects = new StringList();
		String strPartQuantity = DomainConstants.EMPTY_STRING;
		String strPartLevel = DomainConstants.EMPTY_STRING;
		String strPartType = DomainConstants.EMPTY_STRING;
		String strPartID = DomainConstants.EMPTY_STRING;
		String previousLeve = DomainConstants.EMPTY_STRING;

		slRelSelects.add(DomainConstants.SELECT_ATTRIBUTE_QUANTITY);
		BigDecimal bdBOMQty = new BigDecimal("1");
		BigDecimal toSideBOMQty = new BigDecimal("1");

		DomainObject dobTOSideObject;
		Pattern pIntermediateObjectType = new Pattern(pgV3Constants.TYPE_PGCONSUMERUNITPART);
		pIntermediateObjectType.addPattern(pgV3Constants.TYPE_PGCONSUMERUNITPART);
		pIntermediateObjectType.addPattern(pgV3Constants.TYPE_PGINNERPACKUNITPART);

		StringList slFPPchildObjList = new StringList();
		Map mpTemp = new HashMap();
		childObjConnectedFPP = strProcessDOB.getRelatedObjects(context,
				pgV3Constants.RELATIONSHIP_EBOM,    // relationship pattern
				pIntermediateObjectType.getPattern(), // Type pattern
				slObjectSelects,                // object selects
				slRelSelects,            // rel selects
				false,                                // to side
				true,                                // from side
				(short) 0,                            // recursion level
				null,                        // object where clause
				null, 0);
		if (!childObjConnectedFPP.isEmpty() && childObjConnectedFPP.size() > 0) {
			Map mpPreviousLevelChildType = new HashMap();
			for (int i = 0; i < childObjConnectedFPP.size(); i++) {
				mpTemp = (Map) childObjConnectedFPP.get(i);
				strPartLevel = (String) mpTemp.get(DomainConstants.SELECT_LEVEL);
				strPartQuantity = (String) mpTemp.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
				strPartType = (String) mpTemp.get(DomainConstants.SELECT_TYPE);
				if (UIUtil.isNotNullAndNotEmpty(strPartQuantity)) {
					toSideBOMQty = toSideBOMQty.multiply(new BigDecimal(strPartQuantity));
				}
				if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strPartType)) {
					break;
				}
			}
			bdBOMQty = bdIntermediateObjectQty.multiply(toSideBOMQty);
		} else {
			bdBOMQty = bdIntermediateObjectQty;
		}
		return bdBOMQty;
	}
	
	/**
	 * Added for (DSM-Sogeti) for 22x.03 (August 2023) Defect ID : 52121 & 52122
	 * 
	 * @param context
	 * @param mapPAPChildDetails
	 * @return
	 * @throws Exception
	 */
	public MapList getFABExpandBOMOnSAPComponents(Context context, Map mapFABDetails) throws Exception {

		String StrType = (String) mapFABDetails.get("IntermediateType");
		String strNameRevision = (String) mapFABDetails.get("IntermediateName");
		String strObjectType = (String) mapFABDetails.get("IntermediateType");
		String strIntermediateLevel = (String) mapFABDetails.get("IntermediateLevel");
		String strID = (String) mapFABDetails.get("IntermediateID");

		String strFABId = (String) mapFABDetails.get(DomainConstants.SELECT_ID);
		DomainObject dObjPart = null;
		String strObjectWhere = new StringBuffer(DomainObject.SELECT_CURRENT).append(" != ")
				.append(pgV3Constants.STATE_PART_OBSOLETE).toString();
		MapList mlEBOMChildData = null;
		DomainObject dobjAlternate = null;
		String strFABType = (String) (mapFABDetails).get(DomainConstants.SELECT_TYPE);
		String strExpandBOMonSAPBOMVal = (String) (mapFABDetails)
				.get(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
		String strExpandBOMQty = (String) (mapFABDetails).get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
		String strDONOTINCLUDETYPE = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(),
				"emxCPN.SAPBOM.DSM.DONOTINCLUDETYPE");
		MapList mlSubAlts = null;
		MapList mlFlatBOM = new MapList();
		String strIntermediateTypes = pgV3Constants.TYPE_PGCUSTOMERUNITPART + ","
				+ pgV3Constants.TYPE_PGINNERPACKUNITPART + "," + pgV3Constants.TYPE_PGCONSUMERUNITPART;

		try {
			dObjPart = DomainObject.newInstance(context, strFABId);
			
			mlEBOMChildData = dObjPart.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM, // relationshipPattern
					DomainConstants.QUERY_WILDCARD, // typePattern
					false, // getTo
					true, // getFrom
					(short) 1, // recurseToLevel
					SL_OBJECT_INFO_SELECT, // objectSelects
					SL_RELATION_INFO_SELECT_EBOM, // relationshipSelects
					strObjectWhere, // objectWhere
					null, // relationshipWhere
					null, // PostRelPattern
					null, // PostTypePattern
					null);
			
			if (null != mlEBOMChildData && mlEBOMChildData.size() > 0) {
				mlEBOMChildData.sort(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER, "ascending", "integer");
				Map mpCompPart = null;
				int isizePartData = mlEBOMChildData.size();

				for (int iCount = 0; iCount < isizePartData; iCount++) {
					mpCompPart = (Map) mlEBOMChildData.get(iCount);
					String strChildComponentId = (String) mpCompPart.get(DomainConstants.SELECT_ID);
					(mpCompPart).put("IsDirectComponent", "true");
					// Adding the Primary Part
					// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
					if(UIUtil.isNotNullAndNotEmpty(strFABType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMVal) && 
							UIUtil.isNotNullAndNotEmpty(strExpandBOMQty)) {
						(mpCompPart).put("Type", strFABType);
						(mpCompPart).put("FAB_ExpandBOMonSAPBOMasFed", strExpandBOMonSAPBOMVal);
						(mpCompPart).put("FAB_ExpandBOMQuantity", strExpandBOMQty);
					}
					// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends

					if (UIUtil.isNotNullAndNotEmpty(StrType) && strIntermediateTypes.contains(StrType)) {
						(mpCompPart).put("IntermediateName", strNameRevision);
						(mpCompPart).put("IntermediateType", strObjectType);
						(mpCompPart).put("IntermediateID", strID);
						(mpCompPart).put("IntermediateLevel", strIntermediateLevel);
					}
					mlFlatBOM.add(mpCompPart);
					// Adding the Primary Part Alternates
					if (!(strDONOTINCLUDETYPE.contains(strFABType))) {
						StringList slAlternateIds = getStringListFromMap(mpCompPart, pgV3Constants.SELECT_ALTERNATE_ID);
						for (Object oAlternateId : slAlternateIds) {
							dobjAlternate = DomainObject.newInstance(context, (String) oAlternateId);
							Map mpAlternate = dobjAlternate.getInfo(context, SL_OBJECT_INFO_SELECT);
							if (!pgV3Constants.THIRD_PARTY.equalsIgnoreCase(
									(String) mpAlternate.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE))) {
								(mpAlternate).put(DomainConstants.SELECT_RELATIONSHIP_ID,
										(String) mpCompPart.get(DomainConstants.SELECT_RELATIONSHIP_ID));
								(mpAlternate).put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
								(mpAlternate).put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY,
										(String) mpCompPart.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY));
								// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
								if(UIUtil.isNotNullAndNotEmpty(strFABType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMVal) && 
										UIUtil.isNotNullAndNotEmpty(strExpandBOMQty)) {
									(mpAlternate).put("Type", strFABType);
									(mpAlternate).put("FAB_ExpandBOMonSAPBOMasFed", strExpandBOMonSAPBOMVal);
									(mpAlternate).put("FAB_ExpandBOMQuantity", strExpandBOMQty);
								}
								// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends

								if (UIUtil.isNotNullAndNotEmpty(StrType) && strIntermediateTypes.contains(StrType)) {
									(mpAlternate).put("IntermediateName", strNameRevision);
									(mpAlternate).put("IntermediateType", strObjectType);
									(mpAlternate).put("IntermediateID", strID);
									(mpAlternate).put("IntermediateLevel", strIntermediateLevel);
								}
								mlFlatBOM.add(mpAlternate);
							}
						}
					}
					// Adding the Primary Part Substitutes
					StringList substituteIds = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
					StringList substituteSAPTypes = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
					StringList substituteNames = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
					StringList substituteTypes = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
					StringList slsubstituteRev = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
					StringList slsubstituteCurrent = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
					StringList slsubstituteRelID = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
					StringList slSubstituteEffectivityDate = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
					StringList slSubstituteValidUntilDate = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
					StringList slSubstituteOptComponent = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
					StringList slSubstituteComment = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
					StringList slSubSpecSubType = getStringListFromMap(mpCompPart,
							pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);

					if (null != substituteIds && substituteIds.size() > 0) {
						int iSizeOfSubstitutes = substituteIds.size();
						Map mapSubstitutes = null;
						for (int iSub = 0; iSub < iSizeOfSubstitutes; iSub++) {
							String strSubstitutepgSAPType = (String) substituteSAPTypes.get(iSub);
							if (!(strDONOTINCLUDETYPE.contains((String) substituteTypes.get(iSub)))
									&& !(pgV3Constants.THIRD_PARTY.equals(slSubSpecSubType.get(iSub)))
									&& !(pgV3Constants.RANGE_ATTRIBUTE_SAPTYPE_DOC
											.equalsIgnoreCase(strSubstitutepgSAPType))) {
								mapSubstitutes = new HashMap();
								String strSubstituteId = (String) substituteIds.get(iSub);
								String strSubstituteType = (String) substituteTypes.get(iSub);
								mapSubstitutes.put("name", (String) substituteNames.get(iSub));
								mapSubstitutes.put(DomainConstants.SELECT_RELATIONSHIP_ID,
										(String) slsubstituteRelID.get(iSub));
								mapSubstitutes.put("relationship", pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE);
								mapSubstitutes.put(DomainConstants.SELECT_ID, strSubstituteId);
								mapSubstitutes.put(DomainConstants.SELECT_TYPE, strSubstituteType);
								mapSubstitutes.put(DomainConstants.SELECT_REVISION, (String) slsubstituteRev.get(iSub));
								mapSubstitutes.put(DomainConstants.SELECT_CURRENT,
										(String) slsubstituteCurrent.get(iSub));
								mapSubstitutes.put(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE,
										(String) slSubstituteEffectivityDate.get(iSub));
								mapSubstitutes.put(pgV3Constants.ATTRIBUTE_PGVALIDUNTILDATE,
										(String) slSubstituteValidUntilDate.get(iSub));
								mapSubstitutes.put(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT,
										(String) slSubstituteOptComponent.get(iSub));
								mapSubstitutes.put(pgV3Constants.SELECT_ATTRIBUTE_COMMENT,
										(String) slSubstituteComment.get(iSub));
								mapSubstitutes.put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY,
										(String) mpCompPart.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY));
								// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
								if(UIUtil.isNotNullAndNotEmpty(strFABType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMVal) && 
										UIUtil.isNotNullAndNotEmpty(strExpandBOMQty)) {
									(mapSubstitutes).put("Type", strFABType);
									(mapSubstitutes).put("FAB_ExpandBOMonSAPBOMasFed", strExpandBOMonSAPBOMVal);
									(mapSubstitutes).put("FAB_ExpandBOMQuantity", strExpandBOMQty);
								}
								// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
								// (mapSubstitutes).put("IntermediateLevel", strIntermediateLevel);

								if (UIUtil.isNotNullAndNotEmpty(StrType) && strIntermediateTypes.contains(StrType)) {
									(mapSubstitutes).put("IntermediateName", strNameRevision);
									(mapSubstitutes).put("IntermediateType", strObjectType);
									(mapSubstitutes).put("IntermediateID", strID);
									(mapSubstitutes).put("IntermediateLevel", strIntermediateLevel);
								}

								mlFlatBOM.add(mapSubstitutes);

								// Adding the Substitute Parts Alternates
								if (UIUtil.isNotNullAndNotEmpty(strSubstituteType)
										&& ALTERNATE_ALLOWED_TYPES.contains(strSubstituteType)) {
									mlSubAlts = getAlternates(context, strSubstituteId);
									Map mpAltMap = null;
									int iAltsSize = mlSubAlts.size();
									for (int j = 0; j < iAltsSize; j++) {
										mpAltMap = (Map) mlSubAlts.get(j);
										String strExpandFABSubsAltSpecSubType = (String) mpAltMap
												.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
										if (!strExpandFABSubsAltSpecSubType
												.equalsIgnoreCase(pgV3Constants.THIRD_PARTY)) {
											mpAltMap.put(DomainConstants.SELECT_RELATIONSHIP_ID,
													(String) slsubstituteRelID.get(iSub));
											mpAltMap.put("relationship", DomainConstants.RELATIONSHIP_ALTERNATE);
											mpAltMap.put(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY, (String) mpCompPart
													.get(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY));
											// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Starts
											if(UIUtil.isNotNullAndNotEmpty(strFABType) && UIUtil.isNotNullAndNotEmpty(strExpandBOMonSAPBOMVal) && 
													UIUtil.isNotNullAndNotEmpty(strExpandBOMQty)) {
												(mpAltMap).put("Type", strFABType);
												(mpAltMap).put("FAB_ExpandBOMonSAPBOMasFed", strExpandBOMonSAPBOMVal);
												(mpAltMap).put("FAB_ExpandBOMQuantity", strExpandBOMQty);
											}
											// Modified by (DSM) Sogeti for 22x.04 December(CW) Defect 55633 - Ends
											if (UIUtil.isNotNullAndNotEmpty(StrType)
													&& strIntermediateTypes.contains(StrType)) {
												(mpAltMap).put("IntermediateName", strNameRevision);
												(mpAltMap).put("IntermediateType", strObjectType);
												(mpAltMap).put("IntermediateID", strID);
												(mpAltMap).put("IntermediateLevel", strIntermediateLevel);
											}

											mlFlatBOM.add(mpAltMap);
										}
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(pgEDeliverBOMToSAP_mxJPO.class.getName()).log(Level.WARNING, "Error occured: "+ex);
			throw ex;
		}

		return mlFlatBOM;

	}

	/**
	 * Added for 22x.03 (DSM-Sogeti) for (August 2023) Defect ID : 52121 & 52122
	 * 
	 * @param context
	 * @param doObject
	 * @param strTopNodeBOMQty
	 * @param strBUOM
	 * @param fQtyMultiplier
	 * @param mpListChildren
	 * @return
	 */
	public MapList getReshipperQtyForFABComponents(Context context, DomainObject doObject, String strTopNodeBOMQty,
			String strBUOM, float fQtyMultiplier, MapList mpListChildren) {

		MapList mlQtyValues = new MapList();
		float fChildSAPQty = 1;
		String strListQty = null;
		String strType = null;
		Map mapTemp = null;

		try {
			if (doObject != null) {
				strType = doObject.getType(context);
			}
			if (strType.equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART)
					|| strType.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)
					|| strType.equalsIgnoreCase(pgV3Constants.TYPE_PGINNERPACKUNITPART)) {
				fQtyMultiplier = 1;
			}

			if (null != mpListChildren) {
				for (int i = 0; i < mpListChildren.size(); i++) {

					mapTemp = (Map) mpListChildren.get(i);
					String strQuantityVal = (String) mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
					String strFABQuantity = (String) mapTemp.get("FAB_ExpandBOMQuantity");

					if (!(pgV3Constants.BASEUNITOFMEASURE_IT.equalsIgnoreCase(strBUOM)
							|| pgV3Constants.BASEUNITOFMEASURE_MP.equalsIgnoreCase(strBUOM)
							|| pgV3Constants.BASEUNITOFMEASURE_BP.equalsIgnoreCase(strBUOM)
							|| pgV3Constants.BASEUNITOFMEASURE_SW.equalsIgnoreCase(strBUOM)
							|| pgV3Constants.BASEUNITOFMEASURE_SP.equalsIgnoreCase(strBUOM))) {

						if (UIUtil.isNotNullAndNotEmpty(strTopNodeBOMQty)
								&& UIUtil.isNotNullAndNotEmpty(strQuantityVal)) {
							BigDecimal bdfChildSAPQty = BigDecimal.valueOf(fChildSAPQty);
							bdfChildSAPQty = bdfChildSAPQty.multiply(new BigDecimal(strTopNodeBOMQty))
									.multiply(BigDecimal.valueOf(fQtyMultiplier))
									.multiply(new BigDecimal(strFABQuantity).multiply(new BigDecimal(strQuantityVal)))
									.setScale(3, BigDecimal.ROUND_HALF_UP);
							strListQty = String.valueOf(bdfChildSAPQty);
							Map mpQtyValue = new HashMap();
							mpQtyValue.put("Qty", strListQty);
							mlQtyValues.add(mpQtyValue);
						}
					}
				}
			}

		} catch (Exception e) {
			Logger.getLogger(pgEDeliverBOMToSAP_mxJPO.class.getName()).log(Level.WARNING, "Error occured: "+e);
		}
		return mlQtyValues;
	}	
}
