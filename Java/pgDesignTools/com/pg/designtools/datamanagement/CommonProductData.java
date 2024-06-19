package com.pg.designtools.datamanagement;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.dassault_systemes.enovia.gls.common.model.ProductSpecification;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.integrations.tops.IMappableComponent;
import com.pg.designtools.util.PreferenceManagement;

import matrix.db.Access;
import matrix.db.AccessList;
import matrix.db.Attribute;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.AttributeTypeList;
import matrix.db.BusinessInterface;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.ConnectParameters;
import matrix.db.Context;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.util.List;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class CommonProductData extends ProductSpecification {

	HashMap<String, String> mapRelationships = new HashMap<>();
	boolean isAccessGranted = false;
	Context context = PRSPContext.get();
	DataConstants.customTOPSExceptions errorUnsupportedUOM = DataConstants.customTOPSExceptions.ERROR_400_UNSUPPORTED_UOM;
	DataConstants.customTOPSExceptions errorNoLockAccessOnIPM = DataConstants.customTOPSExceptions.ERROR_400_NO_LOCK_ACCESS_ON_IPM;
	String pgBaseUnitOfMeasure = "";
	String strpgGeometryShape = "";
	String strpgBodyShape = "";
	String strType = "";
	String noOfConUnits = "";
	String noOfInterUnits = "";
	Double outerDimlen = 0.0;
	Double outerDimwid = 0.0;
	Double outerDimhei = 0.0;
	String encFileName;
	String fullFilePath;
	boolean doSave = false;
	CommonUtility classCommonUtility = new CommonUtility();
	DomainObject domObj = null;
	
	public CommonProductData() {
		super();
	}

	public CommonProductData(Context context) {
		super();
		PRSPContext.set(context);

	}
	
	public CommonProductData(Context context, String objId) throws FrameworkException {
		super();
		PRSPContext.set(context);
		domObj = DomainObject.newInstance(PRSPContext.get(), objId);
		
	}
	
	public void setOwnerShip(Context context) throws MatrixException {
		//41851 - PnO Ownership Assignment
		String [] newArgs = { this.getId() };
		JPO.invoke(context, "pgIPSecurityCommonUtil", null, "pgIPSetObjectOwnership", newArgs);
	}

	public void setRelationship(String relName, String relId) {
		// GQS set values in mapRelationships

	}

	public void applyProductDataTemplate(DomainObject dom) throws FrameworkException {
		CommonUtility.connectProductDataTemplate(dom);
	}

	public void mapAttributes(IMappableComponent parsedNode) throws MatrixException{
		/**/

	}

	// this is lifted from pgDSOUOMConversion_mxJPO
	public String convertStringDimensionToMM(String value, String uom) {
		double dblMMs = this.convertStringToDouble(value);
		return this.convertDimensionToMM(dblMMs, uom);
	}

	public String convertDimensionToMM(Double value, String uom) {
		double dblMMs = value;
		if (value != null && !DataConstants.UOM_MILLIMETER.equalsIgnoreCase(uom)
				&& !DataConstants.UOM_MILLIMETERS.equalsIgnoreCase(uom)
				&& !DataConstants.UOM_MM.equalsIgnoreCase(uom)) {
			if (DataConstants.UOM_INCHES.equalsIgnoreCase(uom) || DataConstants.UOM_INCH.equalsIgnoreCase(uom)
					|| DataConstants.UOM_IN.equalsIgnoreCase(uom)) {
				dblMMs = value * 25.4D;
			} else if (DataConstants.UOM_CENTIMETER.equalsIgnoreCase(uom)
					|| DataConstants.UOM_CM.equalsIgnoreCase(uom)) {
				dblMMs = value * 10.0D;
			} else if (DataConstants.UOM_M.equalsIgnoreCase(uom) || DataConstants.UOM_METER.equalsIgnoreCase(uom)) {
				dblMMs = value * 0.001D;
			} else if (DataConstants.UOM_FEET.equalsIgnoreCase(uom) || DataConstants.UOM_FT.equalsIgnoreCase(uom)) {
				dblMMs = value * 304.8D;
			} else {
				throw new DesignToolsIntegrationException(errorUnsupportedUOM.getExceptionCode(),
						errorUnsupportedUOM.getExceptionMessage());
			}
		}
		return Double.toString(dblMMs);
	}

	public double convertStringToDouble(String strvalue) {
		double value = 0.0D;
		if (UIUtil.isNotNullAndNotEmpty(strvalue)) {
			value = Double.parseDouble(strvalue);
		}
		return value;
	}
	
	/** Method to convert different Dimensions into Inches.
	 * @param context
	 * @param value
 	 * @param unit of measure
	 * @return
	 * @throws Exception
	 */

	public String convertDimensionToInch(String value, String uom)
	{
		double dblInchs = convertStringToDouble(value);
		if(UIUtil.isNotNullAndNotEmpty(value) && (DataConstants.UOM_INCHES.equalsIgnoreCase(uom)) || DataConstants.UOM_IN.equalsIgnoreCase(uom))
		{
			return Double.toString(dblInchs);
		}
		else if (UIUtil.isNotNullAndNotEmpty(value) && (DataConstants.UOM_MILLIMETER.equalsIgnoreCase(uom)) || DataConstants.UOM_MM.equalsIgnoreCase(uom))
         {
			double mm = convertStringToDouble(value);
			dblInchs = mm * 0.0393701;
		 }
		 else if (UIUtil.isNotNullAndNotEmpty(value) && (DataConstants.UOM_METER.equalsIgnoreCase(uom)) || DataConstants.UOM_M.equalsIgnoreCase(uom))
         {
			double meter = convertStringToDouble(value);
			dblInchs = meter * 39.3701;
		 }
		 else if (UIUtil.isNotNullAndNotEmpty(value) && (DataConstants.UOM_CENTIMETER.equalsIgnoreCase(uom)) || DataConstants.UOM_CM.equalsIgnoreCase(uom))
         {
			double centimeter = convertStringToDouble(value);
			dblInchs = centimeter * 0.3937;
		 } else {
			 throw new DesignToolsIntegrationException(errorUnsupportedUOM.getExceptionCode(),
						errorUnsupportedUOM.getExceptionMessage());
		 }
		return Double.toString(dblInchs);
	}

	public Access checkOwnershipAccess(String strOwner, DomainObject domObject) throws MatrixException {
		
		boolean hasAccess = true;
		StringList slOwnerModificationAccessList = new StringList();
	
		Access accessMask = new Access();
		slOwnerModificationAccessList.add("modify");
		slOwnerModificationAccessList.add("checkin");
		slOwnerModificationAccessList.add("changeowner");
		slOwnerModificationAccessList.add("lock");
		slOwnerModificationAccessList.add("unlock");
		
		String strUser = PRSPContext.get().getUser();
		StringList slAccessList = new StringList();

		VPLMIntegTraceUtil.trace(context, ">>checkOwnershipAccess strOwner " + strOwner);
		VPLMIntegTraceUtil.trace(context,">>checkOwnershipAccess context user() " + strUser);
		
		if (UIUtil.isNotNullAndNotEmpty(strOwner) && !strOwner.equalsIgnoreCase(strUser)) {
			
			for(String str : slOwnerModificationAccessList) {
				
			hasAccess = FrameworkUtil.hasAccess(PRSPContext.get(), domObject, str);
			VPLMIntegTraceUtil.trace(context, ">>checkOwnershipAccess has "+str+" access " + hasAccess);
				if (!hasAccess) {
					slAccessList.add(str);
				}
			}
			if(!slAccessList.isEmpty())
				accessMask = grantAccessRights(PRSPContext.get(),domObject,slAccessList);
		}else {
			VPLMIntegTraceUtil.trace(context, ">>checkOwnershipAccess strOwner and strUser are same");
		}
		VPLMIntegTraceUtil.trace(context, "<< END of checkOwnershipAccess method accessMask::"+accessMask);
		return accessMask;
	}
	
	/**
	 * Method to get the value for Release Phase attribute
	 */
	public String getReleasePhase() {
		//For DT18X6-276, we are hard coding the Release Phase value to Production
		return DataConstants.RELEASE_PHASE_PRODUCTION;
		
	}
	
	/**
	 * Method to get the value for Lifecycle Status attribute
	 */
	public String getLifecycleStatus() {
		//For DT18X6-276, we are hard coding the Lifecycle Status value to PRODUCTION
		return DataConstants.LIFECYCLE_STATUS_PRODUCTION;
		
	}
	
	/**
	 * DTCLD-777
	 * Method to get the value for Segment from user preference
	 * @throws FrameworkException 
	 */
	public String getSegmentValue() throws FrameworkException {
		PreferenceManagement prefMgmt=new PreferenceManagement(PRSPContext.get());
		String strSegment=prefMgmt.getPreferenceValue(PRSPContext.get(), "preference_PackagingSegment");
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> getSegmentValue preference value::"+strSegment);
		
		if(UIUtil.isNullOrEmpty(strSegment)) {
			strSegment=DomainConstants.EMPTY_STRING;
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> getSegmentValue strSegment::"+strSegment);
		return strSegment;
	}
	
	/**
	 * DTCLD-777
	 * Method to get the value for Primary Org from user preference
	 * @throws FrameworkException 
	 */
	public String getPrimaryOrganization() throws FrameworkException {
		PreferenceManagement prefMgmt=new PreferenceManagement(PRSPContext.get());
		String strPrimaryOrg=prefMgmt.getPreferenceValue(PRSPContext.get(), "preference_DefaultAttrPrimaryOrg");
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> getPrimaryOrganization preference value::"+strPrimaryOrg);
		
		if(UIUtil.isNullOrEmpty(strPrimaryOrg)) {
			strPrimaryOrg=DomainConstants.EMPTY_STRING;
		}
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> getPrimaryOrganization strPrimaryOrg::"+strPrimaryOrg);
		return strPrimaryOrg;
	}

	/**
	 * Method to get expand view of Physical Product along with the 3DShape, Drawing and other child physical products
	 * @param context
	 * @param sObjectId - Object ID
	 * @return MapList
	 */
	public static MapList expandPhysicalProduct(Context context, String sObjectId) throws FrameworkException 
	{
		MapList mlPhysicalProduct = new MapList();
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(DataConstants.SELECT_PHYSICALID);
			busSelects.add(DomainConstants.SELECT_NAME);
			busSelects.add(DataConstants.SELECT_HAS_READ_ACCESS);
			
			StringList relSelects = new StringList();
			relSelects.add(DomainRelationship.SELECT_ID);
			
			Pattern relPattern = new Pattern(DataConstants.REL_VPM_INSTANCE);
			relPattern.addPattern(DataConstants.REL_VPM_REPINSTANCE);
			relPattern.addPattern(DataConstants.REL_PART_SPECIFICATION);
			
			Pattern typePattern = new Pattern(DataConstants.TYPE_VPMREFERENCE);
			typePattern.addPattern(DataConstants.TYPE_DRAWING);
			typePattern.addPattern(DataConstants.TYPE_3DSHAPE);
			
			DomainObject domVPMRef = DomainObject.newInstance(context, sObjectId);
			mlPhysicalProduct = domVPMRef.getRelatedObjects(context,//context
					relPattern.getPattern(),		// relationship pattern
					typePattern.getPattern(),				// type pattern
					busSelects,					// object selects
					relSelects,					// relationship selects
					false,							// to direction
					true,							// from direction
					(short) 2,						// recursion level
					DomainConstants.EMPTY_STRING, // object where clause
					DomainConstants.EMPTY_STRING,// relationship where clause
					0);//object limit
		}
		return mlPhysicalProduct;
	}

	/**
	 * Method to fetch connected VPMReference objects
	 * @param context
	 * @param sObjectId
	 * @return MapList
	 */
	public static MapList getConnectedPhysicalProduct(Context context, String sObjectId) throws FrameworkException 
	{
		MapList mlPhysicalProduct = new MapList();
		if(UIUtil.isNotNullAndNotEmpty(sObjectId))
		{
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(DomainConstants.SELECT_TYPE);
			busSelects.add(DataConstants.SELECT_PHYSICALID);
			busSelects.add(DomainConstants.SELECT_NAME);
			
			StringList relSelects = new StringList();
			relSelects.add(DomainRelationship.SELECT_ID);
			
			Pattern relPattern=new Pattern(DataConstants.REL_PART_SPECIFICATION);
			relPattern.addPattern(DataConstants.REL_INHERITED_CAD_SPECIFICATION);
			
			DomainObject doObject = DomainObject.newInstance(context, sObjectId);
			mlPhysicalProduct = doObject.getRelatedObjects(context,//context
					relPattern.getPattern(),		// relationship pattern
					DataConstants.TYPE_VPMREFERENCE,				// type pattern
					busSelects,					// object selects
					relSelects,					// relationship selects
					false,							// to direction
					true,							// from direction
					(short) 1,						// recursion level
					DomainConstants.EMPTY_STRING, // object where clause
					DomainConstants.EMPTY_STRING,// relationship where clause
					0);								// objects Limit
			
			if(mlPhysicalProduct.isEmpty()) {
				//get the physical product connected to the Master Part of the selected object
				
				mlPhysicalProduct=getPhysicalProductFromReferencePart(context,sObjectId);
			}
		}
		return mlPhysicalProduct;
	}

	/**
	 * Method to fetch connected VPMReference objects for reference parts
	 * @param context
	 * @param sObjectId
	 * @return MapList
	 */
	public static MapList getPhysicalProductFromReferencePart(Context context, String sObjectId) throws FrameworkException {
		String strMasterId=getMasterPartId(context,sObjectId);
		MapList mlData=new MapList();
		
		if(UIUtil.isNotNullAndNotEmpty(strMasterId))
			mlData=getConnectedPhysicalProduct(context, strMasterId);
		
		return mlData;
	}

	/**
	 * Method to fetch VPMReference object details for reference parts
	 * @param context
	 * @param sObjectId
	 * @return Map
	 * @throws FrameworkException 
	 */
	public static Map<String,String> getRefPartPhysicalProductInfo(Context context, String sObjectId) throws FrameworkException {

		DomainObject doObject=DomainObject.newInstance(context,sObjectId);
		StringList slSelect=new StringList(2);
		slSelect.addElement(DataConstants.SELECT_CONNECTED_MASTER_PART_TYPE);
		slSelect.addElement(DataConstants.SELECT_MASTER_TYPE_INHERITED_CADOBJ_TYPE);
	
		return doObject.getInfo(context, slSelect);
	}
	
	/**
	 * Method to get the connected Master Part object id
	 * @param context
	 * @param sObjectId
	 * @return String
	 * @throws FrameworkException 
	 */
	private static String getMasterPartId(Context context, String sObjectId) throws FrameworkException {
		DomainObject doObject=DomainObject.newInstance(context,sObjectId);
		return doObject.getInfo(context, DataConstants.SELECT_CONNECTED_MASTER_PART_ID);
	}
  
  /**
	 * This method calls Push-Pop Context for two functions in this method:
	 * - checkOwnershipAccess 
	 * - reviseVersionAndGrantRevokeAccess
	 * 
	 * @param context
	 * @param domIPM
	 * @return accessMask
	 * @throws MatrixException
	 */
	public Access grantAccessRights(Context context, DomainObject domObj, StringList slAccessList) throws MatrixException {		
		VPLMIntegTraceUtil.trace(context, ">> START of grantAccessRights method domObj::"+domObj);
		VPLMIntegTraceUtil.trace(context, ">> grantAccessRights slAccessList:"+slAccessList);
		String strUser = context.getUser();
		
		Access accessMask = new Access();	
		
		accessMask = generateAccessMask(slAccessList);
		accessMask.setUser(strUser);
		
		String strOwner = CommonUtility.getOwner(domObj);
		BusinessObjectList busObjList = new BusinessObjectList();
		busObjList.add(domObj);
		//switch context for the IPM document as defined in the two calling locations in method header
		ContextUtil.pushContext(context);
		try {
			if(!strOwner.equalsIgnoreCase(strUser)) 
			{
				VPLMIntegTraceUtil.trace(context, ">> grantAccessRights before invoking BO.grantAccessRights");
				BusinessObject.grantAccessRights(context, busObjList, accessMask);
				isAccessGranted = true;
				VPLMIntegTraceUtil.trace(context, ">> grantAccessRights after invoking BO.grantAccessRights");
			}
			
		}finally {
			ContextUtil.popContext(context);
		}
		VPLMIntegTraceUtil.trace(context, "<< END of grantAccessRights method accessMask::"+accessMask);
		return accessMask;
	}

	/**
	* Method to generate the required access masks to be granted to logged in user
	* @param StringList of required accesses
	*/
	private Access generateAccessMask(StringList slAccessList) {
		
		Access accessMask = new Access();
		
		for(String str : slAccessList)
		{
			if("modify".equalsIgnoreCase(str))
				accessMask.setModifyAccess(true);
			if("checkin".equalsIgnoreCase(str))
				accessMask.setCheckinAccess(true);
			if("lock".equalsIgnoreCase(str))
				accessMask.setLockAccess(true);
			if("unlock".equalsIgnoreCase(str))
				accessMask.setUnlockAccess(true);
			if("changeowner".equalsIgnoreCase(str))
				accessMask.setChangeOwnerAccess(true);
		}
		
	return accessMask;
}

	/**
	 * This method Revokes the Access rights which were granted
	 * 
	 * @param context
	 * @param accessMask
	 * @throws MatrixException
	 */
	public void revokeAccessRight(Context context, Access accessMask, DomainObject domObj) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">> START of revokeAccessRight method domObj::"+domObj);
				
		AccessList aclList = new AccessList();
		aclList.add(accessMask);
		
		BusinessObjectList busObjList = new BusinessObjectList();
		busObjList.add(domObj);
		
		String strOwner = CommonUtility.getOwner(domObj);
		String strUser = PRSPContext.get().getUser();

		VPLMIntegTraceUtil.trace(context, ">> strOwner " + strOwner);
		VPLMIntegTraceUtil.trace(context,">> context user() " + PRSPContext.get().getUser());

		//remove access IPM document granted in grantAccessRights as defined in the two calling locations in method header
		ContextUtil.pushContext(context);
		try {
			if (!strOwner.equalsIgnoreCase(strUser)) 
			{
				VPLMIntegTraceUtil.trace(context, ">> revokeAccessRight before BO.revokeAccessRights ");
				BusinessObject.revokeAccessRights(context, busObjList, aclList);
				VPLMIntegTraceUtil.trace(context, ">> revokeAccessRight after BO.revokeAccessRights");
			}
			
		}finally {
				isAccessGranted = false;
				ContextUtil.popContext(context);
			}
		VPLMIntegTraceUtil.trace(context, "<< END of revokeAccessRight method ");
	}
	
	/**
	 * This method Revokes the Access rights which were granted to the object
	 * Appropriate use of method is for removing access as a transient access extension for a user
	 * independent of generalization granting of the access to the object
	 * Current utilization is:
	 * - StackingPattern.StackingPatternDocument.processFile
	 * - reviseVersionAndGrantRevokeAccess
	 * @param context
	 * @param domIPM
	 * @param strTypeOfXML
	 * @param filename
	 * @param fileRoot
	 * @param fileformat
	 * @param domConnectedIPMObject
	 * @param domTUPObj
	 * @return
	 * @throws MatrixException
	 */
	public boolean reviseVersionAndGrantRevokeAccess(Context context, DomainObject domIPM, Map mpFileRelatedArguments, DomainObject domConnectedIPMObject,
		DomainObject domTUPObj) throws MatrixException,DesignToolsIntegrationException {
		
		String filename = (String) mpFileRelatedArguments.get("filename");
		File fileRoot = (File) mpFileRelatedArguments.get("fileRoot");
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionAndGrantRevokeAccess Start ");
		String strDocId = domIPM.getObjectId();
		Access accessMask = null;
        try
        {
	    accessMask = checkOwnershipAccess(CommonUtility.getOwner(domIPM),domIPM);
	    
		// Check if the object is locked by context user
		if (!domIPM.isLocked(context)) {
			
			boolean hasLockAccess = FrameworkUtil.hasAccess(context, domIPM, "lock");
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionAndGrantRevokeAccess hasLockAccess:: "+hasLockAccess);
			if(hasLockAccess) {
				domIPM.lock(context);
			}else {
				throw new DesignToolsIntegrationException(errorNoLockAccessOnIPM.getExceptionCode(),
						errorNoLockAccessOnIPM.getExceptionMessage());
			}	
		}
		reviseVersionOfDocAndCheckinFile(context, domIPM, strDocId, mpFileRelatedArguments,
				domConnectedIPMObject, domTUPObj);
        }catch (MatrixException e)
        {
        	VPLMIntegTraceUtil.trace(context, ">>> Inside Catch of reviseVersionAndGrantRevokeAccess method::"+e.getMessage());
        }
        catch (DesignToolsIntegrationException D)
        {
        	throw new DesignToolsIntegrationException(errorNoLockAccessOnIPM.getExceptionCode(),
					errorNoLockAccessOnIPM.getExceptionMessage());
        }
        finally
        {
			if(domIPM.isLocked(context))
			{
				domIPM.unlock(context);
			}
			
			if(isAccessGranted)
				revokeAccessRight(context, accessMask, domIPM);
        }
		encFileName = filename;
		fullFilePath = fileRoot.getAbsolutePath() + java.io.File.separator + filename;
		doSave = true;
		
		return doSave;
	}
					
	/**
	 * This method does revises version on document object and then checks in the file on it
	 * @param context
	 * @param domIPM
	 * @param strDocId
	 * @param strTypeOfXML
	 * @param filename
	 * @param fileRoot
	 * @param fileformat
	 * @param domConnectedIPMObject
	 * @param domTUPObj
	 * @throws MatrixException
	 */
	@SuppressWarnings("deprecation")
	private void reviseVersionOfDocAndCheckinFile(Context context, DomainObject domIPM, String strDocId,
			Map mpFileRelatedArguments, DomainObject domConnectedIPMObject,
		DomainObject domTUPObj) throws MatrixException,DesignToolsIntegrationException {
		
		
		String strTypeOfXML = (String) mpFileRelatedArguments.get("strTypeOfXML");
		String filename = (String) mpFileRelatedArguments.get("filename");
		File fileRoot = (File) mpFileRelatedArguments.get("fileRoot");
		String fileformat = (String) mpFileRelatedArguments.get("fileformat");
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionOfDocAndCheckinFile Start strTypeOfXML "+strTypeOfXML);
		// Revising version of the Document
		String sCheckInReasonValue = EnoviaResourceBundle.getProperty(PRSPContext.get(),
				"emxEngineeringCentralStringResource", PRSPContext.get().getLocale(),
				"emxEngineeringCentral.Common.TOPSXML");
		
		Map<String, String> attribMap = new HashMap<>();
		
		boolean hasCheckinAccess = FrameworkUtil.hasAccess(context, domIPM, "checkin");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionOfDocAndCheckinFile hasCheckinAccess:: "+hasCheckinAccess);
		if (!hasCheckinAccess) {
			throw new DesignToolsIntegrationException(EnoviaResourceBundle.getProperty(context,
					DataConstants.CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE, context.getLocale(),
					"emxEngineeringCentral.Alert.ObjectLocked"));
		}
		
		
		if (DataConstants.CONSTANT_REFERENCE_XML.equals(strTypeOfXML)) {
			attribMap.put(DomainConstants.ATTRIBUTE_CHECKIN_REASON, "Updated Reference XML");
		} else if (DataConstants.CONSTANT_IPS_IMPORT_XML.equals(strTypeOfXML)) {
			attribMap.put(DomainConstants.ATTRIBUTE_CHECKIN_REASON, sCheckInReasonValue);
		} else if (DataConstants.CONSTANT_IPS_COMBINED_XML.equals(strTypeOfXML)) {
			attribMap.put(DomainConstants.ATTRIBUTE_CHECKIN_REASON, "Updated Combined XML");
		}
		
		CommonDocument object = (CommonDocument) DomainObject.newInstance(context, CommonDocument.TYPE_DOCUMENTS);
		object.setId(strDocId);
		
		object.reviseVersion(context, filename, filename, attribMap);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionOfDocAndCheckinFile after reviseversion domConnectedIPMObject "+domConnectedIPMObject);
		if (DataConstants.CONSTANT_IPS_IMPORT_XML.equals(strTypeOfXML) && domConnectedIPMObject == null) {
			DomainRelationship.connect(context, domTUPObj, DataConstants.REL_REFERENCE_DOCUMENT, object);
		}
		
		if (DataConstants.CONSTANT_REFERENCE_XML.equals(strTypeOfXML)  || DataConstants.CONSTANT_IPS_COMBINED_XML.equals(strTypeOfXML)) {
			object.checkinFile(context, true, true, null, fileformat, filename, fileRoot.getAbsolutePath());
		} else if (DataConstants.CONSTANT_IPS_IMPORT_XML.equals(strTypeOfXML)) {
			domIPM.checkinFile(context, true, true, DataConstants.CONSTANT_LOCALHOST, DomainConstants.FORMAT_GENERIC, filename,
					fileRoot.getAbsolutePath());
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionOfDocAndCheckinFile after checkinFile ");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionOfDocAndCheckinFile before checkout ");
		if (DataConstants.CONSTANT_IPS_COMBINED_XML.equals(strTypeOfXML)) {
			// Checking out the file back again for user to save it in Local system.
			FileList files = new FileList();
			matrix.db.File file = new matrix.db.File(filename, fileformat);
			
			List filelist = new List();
			filelist.add(file);
			
			files.addAll(filelist);
			object.checkoutFiles(context, false, fileformat, files,
					fileRoot.getAbsolutePath() + java.io.File.separator);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  reviseVersionOfDocAndCheckinFile after checkoutFiles ");
		}		
	}
			
	/**
	 * This method returns the productdata id as per the variable value passed to it
	 * @param mpPart
	 * @param strProductDataconst
	 * @return
	 */
	protected String getProductDataId(Map mpPart, String strProductDataconst) {
		Map tempMap = null;
		tempMap = (Map) mpPart.get(strProductDataconst);
		String strProductDataId = tempMap.get("id").toString();
		strType = tempMap.get(DomainConstants.SELECT_TYPE).toString();
		if (DataConstants.CONSTANT_MCOP.equals(strProductDataconst)) {
			noOfConUnits = tempMap.get(DataConstants.SELECT_ATTRIBUTE_QUANTITY).toString();
		} else if (DataConstants.CONSTANT_MIP.equals(strProductDataconst)) {
			noOfInterUnits = tempMap.get(DataConstants.SELECT_ATTRIBUTE_QUANTITY).toString();
		}		
		return strProductDataId;
	}
	
	/**
	 * This method gives the Interface Name as per values strpgGeometryShape and strpgBodyShape
	 * @param mapAttributeMap
	 * @return
	 */
	protected String getInterfaceName(Map mapAttributeMap) {
		String strInterFaceName = null;
		pgBaseUnitOfMeasure = (String) mapAttributeMap.get(DataConstants.ATTRIBUTE_PG_DIMENSTION_UOM);			
		strpgGeometryShape = (String) mapAttributeMap.get(DataConstants.ATTRIBUTE_GEOMETRY_SHAPE);
		strpgBodyShape = (String) mapAttributeMap.get(DataConstants.ATTR_PG_BODY_SHAPE);
		if (UIUtil.isNotNullAndNotEmpty(strpgGeometryShape) && UIUtil.isNotNullAndNotEmpty(strpgBodyShape)
				&& !DataConstants.CONSTANT_NA.equalsIgnoreCase(strpgBodyShape)) {
				
			strInterFaceName = EnoviaResourceBundle.getProperty(context, "emxCPN", context.getLocale(),
					"emxCPN.SaveTOPS." + strType + "." + strpgGeometryShape + "." + strpgBodyShape + ".Interface");
		} 
		return strInterFaceName;
	}
				
	/**
	 * This method gives OuterDimension hght, width, length for MCUP,MCOP,MIP
	 * @param mapAttributeMap
	 */
	protected void getOuterDimensionlenWidHt(Map mapAttributeMap) {
		String strOuterDimenLength = (String) mapAttributeMap.get(DataConstants.ATTR_OUTER_DIMENSION_LENGTH);
		String strOuterDimenWidth = (String) mapAttributeMap.get(DataConstants.ATTR_OUTER_DIMENSION_WIDTH);
		String strOuterDimenHeight = (String) mapAttributeMap.get(DataConstants.ATTRIBUTE_PG_OUTERDIMENSIONHEIGHT);
		if (UIUtil.isNotNullAndNotEmpty(strOuterDimenLength)) {
			outerDimlen = Double.parseDouble(strOuterDimenLength);
		}
		if (UIUtil.isNotNullAndNotEmpty(strOuterDimenWidth)) {
			outerDimwid = Double.parseDouble(strOuterDimenWidth);
		}
		if (UIUtil.isNotNullAndNotEmpty(strOuterDimenHeight)) {
			outerDimhei = Double.parseDouble(strOuterDimenHeight);
		}
	}
			
	/**
	 * This method gives us the interface attributes as per Interface Name
	 * @param strInterFaceName
	 * @return
	 * @throws MatrixException
	 */
	public StringList getInterfaceAttrList(String strInterFaceName) throws MatrixException {

		BusinessInterface busInterface = new BusinessInterface(strInterFaceName, context.getVault());

		AttributeTypeList attrList = busInterface.getAttributeTypes(context);
		Iterator attrIter = attrList.iterator();
		StringList slInterfaceAttr = new StringList();
		AttributeType attrType;
		while (attrIter.hasNext()) {
			attrType = (AttributeType) attrIter.next();
			slInterfaceAttr.add(attrType.getName());
		}
		return slInterfaceAttr;
	} 
							
	/**
	 * This method gives us a attribute map as per the Interface attribute list
	 * @param slInterfaceAttrs
	 * @param domObj
	 * @return
	 * @throws MatrixException
	 */
	public Map getAttrMapFromInterfaceAttrList(StringList slInterfaceAttrs, DomainObject domObj)
			throws MatrixException {
		HashMap hmAttributeMap = new HashMap();
		AttributeList attribList = domObj.getAttributeValues(context, slInterfaceAttrs);
		Attribute attrib;
		for (int i = 0; i < attribList.size(); i++) {
			attrib = attribList.get(i);
			hmAttributeMap.put(attrib.getName(), attrib.getValue());
		}
		return hmAttributeMap;
	}

	public String getNoOfConUnits() {
		return noOfConUnits;
	}				

	public void setNoOfConUnits(String noOfConUnits) {
		this.noOfConUnits = noOfConUnits;
	}

	public String getNoOfInterUnits() {
		return noOfInterUnits;
	}

	public void setNoOfInterUnits(String noOfInterUnits) {
		this.noOfInterUnits = noOfInterUnits;
	}
	
	/**
	 * DSM 2015x.1 : Private method to retrieve ownership stringlist for passed object
	 * This method is moved from pgDSOCPNProductData_mxJPO.java
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws MatrixException 
	 */
	public StringList getOwnership(Context context, String strObjectId, String strObjectType) throws MatrixException
	{
		StringList slObjectOwnership = new StringList();
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getOwnership strObjectType " + strObjectType);
		
		DomainObject domObj = DomainObject.newInstance(context, strObjectId);
		StringList slOwnership = domObj.getInfoList(context, "ownership");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getOwnership ownership with getinfolist" + slOwnership);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getOwnership slOwnership.size()" + slOwnership.size());
		
		int iCount = 0;
		String strOwnership =  "";
		boolean ownercheck = false;
		String strPerson = "";
		String result = "";
		String strContextUser = context.getUser();
	
		for(Object objOwnership : slOwnership)
		{
			StringBuilder sbOwnership = new StringBuilder();
			strOwnership = objOwnership.toString();
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getOwnership strOwnership " + strOwnership);
			ownercheck = false;
			if(UIUtil.isNotNullAndNotEmpty(strOwnership)) {
				if (DataConstants.TYPE_PG_STACKINGPATTERN.equals(strObjectType)) {
					strPerson = StringUtil.split(strOwnership, "|").get(1);
					/*Commenting below mql for sonar error
					strMqlCommand = "print role $1 select person dump";
					result = MqlUtil.mqlCommand(context, strMqlCommand, strPerson);*/
					
					StringList slPersonDetails = StringUtil.split(strPerson, DataConstants.SEPARATOR_UNDERSCORE+DataConstants.CONSTANT_PRJ);	
					result = slPersonDetails.get(0);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getOwnership slPersonDetails.get(0) " + result);
					//To Do - Why context user is compared to ownership person ? It should just compare result with SPS owner because if TUP has SPS owner as MOA then there is no need to copy that MOA to SPS .
					//This code comes from pgDSOCPNProductData_mxJPO.java : getOwnership
					ownercheck = strContextUser.equals(result);
					
				}
				//Check to avoid adding context user as Ownership element in order to suppress can not delete owner error from OOTB
				if (!ownercheck) {
					strOwnership = StringUtil.Replace(strOwnership, "|", ":");
					sbOwnership.append(strObjectId);
					sbOwnership.append (":");
					sbOwnership.append (strOwnership);
					sbOwnership.append ("|");
					sbOwnership.append (strObjectId);
					sbOwnership.append ("|0,");
					sbOwnership.append (iCount);
					if(!slObjectOwnership.contains(sbOwnership.toString()))
					{
						slObjectOwnership.addElement(sbOwnership.toString());
					}
					
				}
			}

			iCount++;
		}
		
		return slObjectOwnership;
	}
	
	/**
	 * This method retrieves access for passed ownership on TUP.
	 * This method is moved from pgDSOCPNProductData_mxJPO.java
	 * @param context
	 * @param strOwnership
	 * @param strTUPId
	 * @param accessNames
	 * @return
	 * @throws Exception 
	 */
	public String getAccessForOwnership(Context context, String strOwnership, String strTUPId, StringList accessNames) throws Exception
	{
		String strAccess = accessNames.get(0);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getAccessForOwnership strAccess " + strAccess);
		String strOwnershipAccessList = DomainConstants.EMPTY_STRING;
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getAccessForOwnership strOwnership " + strOwnership);	
		
		StringList slOwnership = StringUtil.split(strOwnership, ":");
		if(slOwnership.size()>2) {
			strOwnershipAccessList = DomainAccess.getObjectOwnershipAccess(context, strTUPId, slOwnership.get(1), slOwnership.get(2), DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getAccessForOwnership strOwnershipAccessList " + strOwnershipAccessList);	
		}
			
		if(UIUtil.isNotNullAndNotEmpty(strOwnershipAccessList) && strOwnershipAccessList.contains("modify"))
		{
			strAccess = accessNames.get(1);
		}
		
		return strAccess;
	}
	
	/**
	 * Thie method would verify if all mandatory attributes are set on EC Part
	 * @param context
	 * @param mpECPartInfo
	 * @return String
	 */
	public String checkMandatoryAttributes(Context context, Map<String,String>mpECPartInfo) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>START checkMandatoryAttributes method");
		String strMessage="";
		int iResult=0;
		DesignToolsIntegrationException dte=new DesignToolsIntegrationException();
		int iCode=500;
		String strECPartType=mpECPartInfo.get(DomainConstants.SELECT_TYPE);
		String strECPartId=mpECPartInfo.get(DomainConstants.SELECT_ID);
		
		String[] args=new String[2];
		args[0]=strECPartId;
		args[1]=strECPartType;
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>strECPartId::"+strECPartId+" strECPartType::"+strECPartType);
		
		try {
			iResult=JPO.invoke(context, "pgDSMChangeUtil", null, "checkMandatoryAttributes", args);
		}catch(Exception e) {
			strMessage=dte.formatExceptionMessage(e.toString());
			iCode=dte.getD2SExceptionMessageCode(strMessage);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>strMessage::"+strMessage+" iCode::"+iCode);
			throw new DesignToolsIntegrationException(iCode,	strMessage);
		}
		if(iResult==0)
			strMessage=DataConstants.MANDATORY_ATTRIBUTES_PRESENT;
		return strMessage;
	}
	
	/**
     * The method gets all the previous revisions.
     * @param context the eMatrix <code>Context</code> object
     * @param DomainObject doObj of the released revision
     * @return Vector
	 * @throws MatrixException 
     */
    public java.util.List<String> getPreviousRevisions(Context context, DomainObject doObj) throws MatrixException {
    	VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>START getPreviousRevisions method");
    	VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>doObj::"+doObj);
    	java.util.List<String> arrOldRevisions = new ArrayList<>();
        DomainObject doPrevRev;
        if (doObj != null) {
            String strObjectId = getPreviousRevision(context, doObj);
            while (UIUtil.isNotNullAndNotEmpty(strObjectId)) {
            	arrOldRevisions.add(strObjectId);
                doPrevRev=DomainObject.newInstance(context,strObjectId);
                strObjectId = getPreviousRevision(context, doPrevRev);
            }
        }
        VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>list of previous revisions id::"+arrOldRevisions);
        return arrOldRevisions;
    }
    
    /**
     * The method gets the objectId of the previous revision.
     * @param context the eMatrix <code>Context</code> object
     * @param DomainObject doObj
     * @return Object ID of the previous revision
     * @throws MatrixException 
     */
    public String getPreviousRevision(Context context, DomainObject doObj) throws MatrixException {
        String strObjectId = "";
        BusinessObject busObj = doObj.getPreviousRevision(context);
        if (null !=busObj) {
            strObjectId = busObj.getObjectId(context);
        }
        return strObjectId;
    }
    
    /*
	 * Method to fetch connected VPMReference object
	 * @param context
	 * @param strECPartId
	 * @return MapList
	 */
	public MapList getConnectedVPMReference(Context context, String strECPartId,StringList busSelects,StringList relSelects) throws FrameworkException 
	{
		MapList mlPhysicalProduct = new MapList();
		VPLMIntegTraceUtil.trace(context, "<< getConnectedVPMReference: Start strECPartId "+strECPartId);
		if(UIUtil.isNotNullAndNotEmpty(strECPartId))
		{
			Pattern relPattern=new Pattern(DataConstants.REL_PART_SPECIFICATION);
			
			DomainObject doObject = DomainObject.newInstance(context, strECPartId);
			mlPhysicalProduct = doObject.getRelatedObjects(context,//context
					relPattern.getPattern(),		// relationship pattern
					DataConstants.TYPE_VPMREFERENCE,				// type pattern
					busSelects,					// object selects
					relSelects,					// relationship selects
					false,							// to direction
					true,							// from direction
					(short) 1,						// recursion level
					DomainConstants.EMPTY_STRING, // object where clause
					DomainConstants.EMPTY_STRING,// relationship where clause
					0);								// objects Limit
			
		}
		VPLMIntegTraceUtil.trace(context, "<< getConnectedVPMReference: End mlPhysicalProduct "+mlPhysicalProduct);
		return mlPhysicalProduct;
	}
	
	/**
	 * This Method transfers control to Enterprise Part.
	 * Invoked from pg_emxCPNProductDataRevisePostProcess.jsp
	 * @param context
	 * @param domCAD
	 * @throws FrameworkException
	 */
	public void transferControlToEnterprise (Context context , DomainObject domCAD) throws FrameworkException {
		boolean isContextPushed = false ; 
		//This is going to be used as a generic method for transferring control to EP. Adding PushContext as User will not always have access for setting attribute ATTRIBUTE_ISVPLMCONTROLLED on VPMReference
		try {
			ContextUtil.pushContext(context, DataConstants.PERSON_USER_AGENT, null, context.getVault().getName());
			isContextPushed =true;
			domCAD.setAttributeValue(context, DataConstants.ATTRIBUTE_ISVPLMCONTROLLED, "FALSE");
			VPLMIntegTraceUtil.trace(context, "transferControlToEnterprise control is transferred to Enterprise");
		}catch(Exception e) {
			VPLMIntegTraceUtil.trace(context, "transferControlToEnterprise Inside Catch:::"+e.getMessage());
			throw e;
		}
		finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}	
		}	
	}
	
	/**
	 * This Method transfers control to CAD.
	 * Invoked from Trigger PGDSMPolicyECPartStateApprovedPromoteAction autoPromoteConnectedCATIAPartToShared
	 * @param context
	 * @param domCAD
	 * @throws FrameworkException
	 */
	public void transferControlToCAD (Context context , DomainObject domCAD) throws FrameworkException {
		boolean isContextPushed = false ; 
		//This is going to be used as a generic method for transferring control to CAD. Adding PushContext as User will not always have access for setting attribute ATTRIBUTE_ISVPLMCONTROLLED on VPMReference
		try {
			ContextUtil.pushContext(context, DataConstants.PERSON_USER_AGENT, null, context.getVault().getName());
			isContextPushed =true;
			domCAD.setAttributeValue(context, DataConstants.ATTRIBUTE_ISVPLMCONTROLLED, "TRUE");
		}finally {
			if(isContextPushed) {
				ContextUtil.popContext(context);
			}	
		}	
	}
	
	 /**
	 * @param context
	 * @param strECPartId
	 * @return Map which has VPMReference details
	 * ALM 50590 50591 This method is invoked from pg_emxLaunch3DPlay.jsp. Method will get Related and Inherited VPMRef connected to a EC Part
	 * @throws FrameworkException
	 */
	public Map getRelatedAndInheritedVPMReferenceFromECPart(Context context, String strECPartId) throws FrameworkException {
		 		
		StringList busSelects = new StringList();
		busSelects.add(DomainConstants.SELECT_ID);
		busSelects.add(DataConstants.SELECT_PHYSICALID);
		busSelects.add(DomainConstants.SELECT_TYPE);
				
		StringList relSelects = new StringList();
		relSelects.add(DomainRelationship.SELECT_ID);
		relSelects.add(DomainRelationship.SELECT_NAME);
		
		Pattern relPattern = new Pattern(DataConstants.REL_PART_SPECIFICATION);
		relPattern.addPattern(DataConstants.REL_INHERITED_CAD_SPECIFICATION);
		
		Pattern typePattern = new Pattern(DataConstants.TYPE_VPMREFERENCE);
		
		DomainObject domECPart = DomainObject.newInstance(context, strECPartId);
		MapList mlVPMReference = domECPart.getRelatedObjects(context,//context
				relPattern.getPattern(),		// relationship pattern
				typePattern.getPattern(),				// type pattern
				busSelects,					// object selects
				relSelects,					// relationship selects
				false,							// to direction
				true,							// from direction
				(short) 1,						// recursion level
				DomainConstants.EMPTY_STRING, // object where clause
				DomainConstants.EMPTY_STRING,// relationship where clause
				0);//object limit
		
		int nSize = mlVPMReference.size();
		Map mpVPMRef = new HashMap();
		String relName;
		boolean bIsPartSpecPresent = false;
		for(int i=0;i<nSize;i++) {
			
			mpVPMRef = (Map)mlVPMReference.get(i); 
			relName = (String) mpVPMRef.get(DomainRelationship.SELECT_NAME);
			if(DataConstants.REL_PART_SPECIFICATION.equals(relName)) {
				bIsPartSpecPresent = true;
				break;
			}	
		}
		if(nSize>0 && !bIsPartSpecPresent) {
			mpVPMRef = (Map)mlVPMReference.get(0); 
		}
		return mpVPMRef;
	 }

 /**
	 * Method to check whether VPMReference object is connected to EC Part or not
	 * @param context
	 * @param strECPartObjectId
	 * @return boolean
	 * @throws FrameworkException
	 */
	public boolean checkIfECPartHasCADObjectConnected(Context context,String strECPartObjectId) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> START of checkIfECPartHasCADObjectConnected method");
		boolean isVPMRefConnected=false;
		
		StringList slBusSelects=new StringList(2);
		slBusSelects.add(DomainConstants.SELECT_NAME);
		slBusSelects.add(DomainConstants.SELECT_REVISION);
		
		MapList mlVPMReference=getConnectedVPMReference(context,strECPartObjectId,slBusSelects,new StringList());
		if(null!=mlVPMReference && !mlVPMReference.isEmpty()) {
			if(mlVPMReference.size()==1)
				isVPMRefConnected=true;
			else
				throw new DesignToolsIntegrationException(DataConstants.customCATIAExceptions.ERROR_400_MULTIPLE_VPMREFERENCE_CONNECTED.getExceptionCode(), 
						DataConstants.customCATIAExceptions.ERROR_400_MULTIPLE_VPMREFERENCE_CONNECTED.getExceptionMessage());
		}
		VPLMIntegTraceUtil.trace(context, "<<< checkIfECPartHasCADObjectConnected isVPMRefConnected::"+isVPMRefConnected);
		return isVPMRefConnected;
	}
  
  /**
	 * Method to validate and transfer control to Enterprise. Invoked from reviseProductData method 
	 * @param context
	 * @param strECPartId
	 * @param strECPartType
	 * @throws MatrixException
	 */
	public void validateAndTransferControlToEnterprise(Context context,String strECPartId,String strECPartType) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> START of validateAndTransferControlToEnterprise method");
		VPLMIntegTraceUtil.trace(context, ">>>validateAndTransferControlToEnterprise strECPartId::"+strECPartId+" strECPartType::"+strECPartType);
		
		StringList slVPMRefSelects=new StringList(2);
		slVPMRefSelects.add(DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
		slVPMRefSelects.add(DomainConstants.SELECT_ID);
		
		MapList mlVPMRef=getConnectedVPMReference(context, strECPartId, slVPMRefSelects, new StringList());
		
		if(null!=mlVPMRef && !mlVPMRef.isEmpty()) {
			Map<String,String> mpVPMRef=(Map<String, String>) mlVPMRef.get(0);
			
			String strVPMRefObjId=mpVPMRef.get(DomainConstants.SELECT_ID);
			String strIsVPMControlled=mpVPMRef.get(DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
			
			VPLMIntegTraceUtil.trace(context,">>>> validateAndTransferControlToEnterprise strVPMRefObjId::"+strVPMRefObjId+" strIsVPMControlled::"+strIsVPMControlled);
			
			EngineeringItem enggItem=new EngineeringItem(context);
			boolean bIsXCADType=enggItem.checkVPMReferenceObjectForXCADType(context, strVPMRefObjId);
			VPLMIntegTraceUtil.trace(context,">>>> validateAndTransferControlToEnterprise bIsXCADType::"+bIsXCADType);
			VPLMIntegTraceUtil.trace(context,">>>>validateAndTransferControlToEnterprise DataConstants.VALID_DT_SYMBOLIC_SYNC_TYPES::"+DataConstants.VALID_DT_SYMBOLIC_SYNC_TYPES);
			
			if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strIsVPMControlled) && DataConstants.VALID_DT_SYMBOLIC_SYNC_TYPES.contains(strECPartType) && !bIsXCADType){
				VPLMIntegTraceUtil.trace(context,">>>> validateAndTransferControlToEnterprise invoking transferControlToEnterprise method");
				DomainObject domCAD=DomainObject.newInstance(context,strVPMRefObjId);
				transferControlToEnterprise(context,domCAD);
			}
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of validateAndTransferControlToEnterprise method");
  }
	
	/**
	 * Method to connect two objects
	 * @param DomainObject of current object
	 * @param String relationship name
	 * @param objectId of the target object
	 * @param boolean true if current object is at from side of relationship
	 * @throws MatrixException
	 */
	public void connectData(DomainObject doObject,String strRelationship,String strTargetObjectId,boolean setFrom) throws MatrixException {
		RelationshipType relType = new RelationshipType(strRelationship);
		ConnectParameters connectParams = new ConnectParameters();
		connectParams.setRelType(relType);
		connectParams.setFrom(setFrom);
		connectParams.setTarget(new BusinessObject(strTargetObjectId));
		doObject.connect(PRSPContext.get(), connectParams);
	}
}
