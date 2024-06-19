package com.pg.designtools.datamanagement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.jdom.Document;
import com.matrixone.jdom.JDOMException;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.datahandlers.SAXHandler;
import com.pg.designtools.integrations.datahandlers.SimpleXML;
import com.pg.designtools.integrations.datahandlers.TOPSSAXBuilder;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.integrations.tops.IMappableComponent;
import com.pg.designtools.integrations.tops.Input;
import com.pg.designtools.util.ChangeManagement;
import com.pg.designtools.util.CommonDocumentHandler;
import com.pg.designtools.util.FileManagement.ObjectFile;
import com.pg.designtools.util.FileManagement.ObjectFiles;
import matrix.db.JPO;
import matrix.db.RelationshipType;
import matrix.db.Access;
import matrix.db.BusinessInterfaceList;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.SelectList;
import matrix.util.StringList;

public class StackingPattern extends CommonProductData implements IProductData {

	public StackingPattern() {
		super();
	}

	public StackingPattern(Context context) {
		super();
		PRSPContext.set(context);
		setType(DataConstants.SCHEMA_TYPE_PG_STACKINGPATTERN);
		setPolicy(DataConstants.SCHEMA_POLICY_IPM_SPECIFICATION);
		setRevision(DataConstants.CONSTANT_FIRST_REVISION);
		setAutoNameSeries(DataConstants.CONSTANT_AUTONAME_SERIES_A);
	}

	DomainObject domSPS = null;
	String strSPSName = DomainConstants.EMPTY_STRING;
	String strSPSId = DomainConstants.EMPTY_STRING;
	String strSPSRevision = DomainConstants.EMPTY_STRING;

	TransportUnitPart objTUP = new TransportUnitPart(PRSPContext.get());
	DomainObject domTUP = null;
	Map<DomainObject, ArrayList<String>> mpSPSConnectionDetails = new HashMap<>();
	ArrayList<String> lRelPartSpec = new ArrayList<>();

	DomainObject domIPM = null;
	String fileName;
	boolean bIPMDocumentCreated = false;

	DataConstants.customTOPSExceptions errorNoTUP = DataConstants.customTOPSExceptions.ERROR_400_TUP_NOT_PRESENT;
	DataConstants.customTOPSExceptions errorTUPHaveMoreThanOneSPS = DataConstants.customTOPSExceptions.ERROR_400_TUP_HAVE_MORE_THAN_ONE_SPS;

	public StackingPattern(Context context, String objName, String objRevision, String file, DomainObject domSPSObj)
			throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  StackingPattern Constructore START");

		PRSPContext.set(context);
		fileName = file;
		strSPSName = objName;
		strSPSRevision = objRevision;

		domSPS = domSPSObj;

		domIPM = getRelatedIPM();
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>  StackingPattern Constructore  domIPM" + domIPM);

		if (domIPM == null && (fileName.endsWith(DataConstants.FILE_FORMAT_XML) || fileName.startsWith(DataConstants.FILE_TRANSPORTVIEW_PDF))) {
			StackingPatternDocument objSPD = new StackingPatternDocument();

			objSPD.createIPMDocument();
			objSPD.createIPMVersion(fileName);
			bIPMDocumentCreated = true;
		}
	}

	public StackingPattern(Context context, String objectId) throws FrameworkException {
        super();

        PRSPContext.set(context);

        // Initialize the StackingPattern with the provided object ID
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
        	
        	StringList slSelects=new StringList(2);
        	slSelects.add(DomainConstants.SELECT_NAME);
        	slSelects.add(DomainConstants.SELECT_REVISION);
        	
            domSPS = DomainObject.newInstance(context, objectId);
            Map mpSPSInfo=domSPS.getInfo(context, slSelects);
        	strSPSName = (String)mpSPSInfo.get(DomainConstants.SELECT_NAME);
    		strSPSRevision = (String)mpSPSInfo.get(DomainConstants.SELECT_REVISION);
        }

    }
	
	/*
	 * This Method will create the Object and return its Name classModeler is the
	 * object for IProductSpecification. Below Method calls the create() method from
	 * ProductSpecification.java
	 */
	@Override
	public String createProductData() throws Exception {
		VPLMIntegTraceUtil.trace(context, ">> START of SPS createProductData method");
		create();
		strSPSName = getName();
		strSPSId = getId();
		strSPSRevision = getRevision();
		VPLMIntegTraceUtil.trace(context, ">> strSPSName::"+strSPSName);
		domSPS=DomainObject.newInstance(PRSPContext.get(),strSPSId);
		
		//41851 - PnO Ownership Assignment
		setOwnerShip(PRSPContext.get());

		domSPS.setAttributeValue(PRSPContext.get(), DataConstants.ATTR_TITLE, strSPSName);
		domSPS.setDescription(PRSPContext.get(), strSPSName);
		applyProductDataTemplate(domSPS);
		setSystemGenerated(true);
		
		//37884 - Usage Tracking
		
		return strSPSName;
	}

	/*
	 * This Method will create the SPS Object and return its Name It will also
	 * create TUP according to boolean variable passed.
	 */
	public String createProductData(boolean createTUP) throws Exception {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> START of SPS createProductData with boolean method");
		String spsName = createProductData();
		//START: Added for DTCLD-794
		if(createTUP) {
			// create TUP is true if we are creating data from TOPS. At that time set the SPS attribute as StartedInTOPS
				String [] newArgs = { "TOPS", "StartedInTOPS", strSPSId };
				JPO.invoke(PRSPContext.get(), DataConstants.CONSTANT_JPO_PGDTAUTOMATION_METRIC_TRACKING, null, DataConstants.CONSTANT_METHOD_ADD_USAGE_TRACKING_TODATA, newArgs);
		}
		//END: Added for DTCLD-794
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> SPS createProductData spsName::"+spsName);
		if (getRelatedTUP(domSPS) == null && createTUP) {
			objTUP.createProductData();
			domTUP = objTUP.getDomainObject();
		}
		if(null!=domTUP)
		{
			connect();
			
			//START DTCLD-777 get the info for connected Segment and Primary Org from TUP
				
			connectSegmentandOrganizationstoSPS();
			
			//END DTCLD-777
			
			Map mpSPSAttrMap = copyTUPAttributestoSPS(PRSPContext.get(),getAttributeList());
			setAttributes(mpSPSAttrMap);
			
		}

		return spsName;
	}
	
	/*
	 * This Method will connect the Segment, Primary and Secondary Org objects to SPS
	 * The objects to be connected would be fetched from TUP.
	 * 
	 */
	public void connectSegmentandOrganizationstoSPS() throws MatrixException
	{
		Map objectMap=new HashMap();
		String connectedObjId=null;
		String relName="";
		StringList objectSelect=new StringList(1);
		objectSelect.add(DomainConstants.SELECT_ID);
		
		StringBuffer strRelBuffer=new StringBuffer();
		strRelBuffer.append(DataConstants.RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT);
		strRelBuffer.append(DataConstants.SEPARATOR_COMMA);
		strRelBuffer.append(DataConstants.REL_PG_PRIMARY_ORGANIZATION);
		strRelBuffer.append(DataConstants.SEPARATOR_COMMA);
		strRelBuffer.append(DataConstants.REL_PG_SECONDARY_ORGANIZATION);
	
		MapList relatedObjectList=domTUP.getRelatedObjects(PRSPContext.get(),
				strRelBuffer.toString(),
				"*",
				objectSelect,
				null,
				true,
				true,
				(short) 1,
				null,
				null,
				0);
		Iterator itr=relatedObjectList.iterator();
		while(itr.hasNext())
		{
			objectMap=(Map) itr.next();
			connectedObjId=(String)objectMap.get(DomainConstants.SELECT_ID);
			relName=(String)objectMap.get("relationship");
			if(null!=connectedObjId) 
			{
				connectData(domSPS, relName, connectedObjId, true);
				VPLMIntegTraceUtil.trace(PRSPContext.get(), ">> Connected data "+connectedObjId+" with SPS");
			} 
		}
	}
	
	/*
	 * This Method would copy the attributes from TUP to SPS
	 * @param context
	 * @param StringList
	 * @return Map
	 */
	public Map copyTUPAttributestoSPS(Context context, StringList SPSAttributeList) throws MatrixException
	{
		Map spsAttrMap=new HashMap();
		
		Map tuAttributeMap=domTUP.getAttributeMap(context);
		
		for (String str : SPSAttributeList)
		spsAttrMap.put(str, (String)tuAttributeMap.get(str));
		
		return spsAttrMap;
	}
	
	/*
	 * This Method would get the list of attributes
	 * @return StringList
	 */
	public StringList getAttributeList()
	{
		StringList slSPSAttrList = new StringList(7);
		slSPSAttrList.add(DataConstants.ATTRIBUTE_SEGMENT);
		slSPSAttrList.add(DomainObject.ATTRIBUTE_TITLE);
		slSPSAttrList.add(DomainObject.ATTRIBUTE_ORIGINATOR);
		slSPSAttrList.add(DataConstants.ATTR_PG_ORIGINATINGSOURCE);
		slSPSAttrList.add(DataConstants.ATTR_EXPIRATION_DATE);
		slSPSAttrList.add(DataConstants.ATTRIBUTE_IS_TEMPLATE_APPLIED);
		slSPSAttrList.add(DomainConstants.ATTRIBUTE_REASON_FOR_CHANGE);
		
		return slSPSAttrList;
	}

	/**
	 * @param domSPS
	 * @return domTUP
	 * @throws FrameworkException
	 *             This method accepts SPS and generates its related TUP Object
	 */
	public DomainObject getRelatedTUP(DomainObject domObjSPS) throws FrameworkException {

		MapList mlTUP = null;
		String strConnectedTUPId;
		StringList busSelects = new StringList();
		busSelects.add(DomainConstants.SELECT_ID);

		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>  getRelatedTUP domObjSPS " + domObjSPS);
		mlTUP = domObjSPS.getRelatedObjects(PRSPContext.get(), // Context
				DataConstants.REL_PART_SPECIFICATION, // Relationship Pattern
				DataConstants.TYPE_PG_TRANSPORTUNIT, // Type Pattern
				busSelects, // Object Selects
				null, // Relationship Selects
				true, // get TO
				false, // get From
				(short) 1, // Recurrence Level
				null, // Object Where
				null, // RelationShip Where
				0);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>  getRelatedTUP mlTUP " + mlTUP);
		if (!mlTUP.isEmpty()) {
			Map<String, String> mpTUP = (Map) mlTUP.get(0);
			strConnectedTUPId = mpTUP.get(DomainConstants.SELECT_ID);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>  getRelatedTUP strConnectedTUPId " + strConnectedTUPId);
			domTUP = DomainObject.newInstance(PRSPContext.get(), strConnectedTUPId);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>  getRelatedTUP domTUP " + domTUP);
		}
		domSPS = domObjSPS;
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>  getRelatedTUP domSPS " + domSPS);
		return domTUP;
	}

	/*
	 * This method will accept a Map as argument key = DomainObject to be connected
	 * Value = List -> list[0] = RelationshipName , list[1] = To/From Here To/From
	 * will indicate the side of the context Object on which this connect method is
	 * invoked.
	 */
	@Override
	public void connect() throws FrameworkException {

		lRelPartSpec.add(DataConstants.REL_PART_SPECIFICATION); // Rel to connect with SPS
		lRelPartSpec.add(DataConstants.CONSTANT_TO); // Which side is SPS Object

		mpSPSConnectionDetails.put(domTUP, lRelPartSpec);
		CommonUtility.connect(domSPS, mpSPSConnectionDetails);
	}

	/**
	 * @param isSystemGenerated
	 * @throws FrameworkException
	 *             This method sets the attribute value for SPSOrigination according
	 *             to the value of isSystemGenerated
	 */
	public void setSystemGenerated(boolean isSystemGenerated) throws FrameworkException {
		String strSPSOrigination;
		if (isSystemGenerated) {
			strSPSOrigination = DataConstants.RANGE_VALUE_SYSTEM_GENERATED;
		} else {
			strSPSOrigination = DataConstants.RANGE_VALUE_MANUAL;
		}
		domSPS.setAttributeValue(PRSPContext.get(), DataConstants.ATTR_PG_SPS_ORIGINATION, strSPSOrigination);
	}

	/**
	 * @return IPM Object
	 * @throws FrameworkException
	 *             This method retruns related IPM of a SPS Object
	 */
	public DomainObject getRelatedIPM() throws FrameworkException {
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>  getRelatedIPM  START domSPS" + domSPS);
		MapList mlIPM = null;
		String strConnectedIPMId;
		StringList busSelects = new StringList(1);
		busSelects.add(DomainConstants.SELECT_ID);

		//DTCLD-754 Added where clause to get the correct IPMDoc (when we release using CM, another IPMDoc is attached to SPS)		
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>  getRelatedIPM  SPS Name::" + strSPSName);
		String sWhere = DomainConstants.SELECT_NAME+" ~~ '"+strSPSName+"*'";
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>  getRelatedIPM  sWhere::" + sWhere);
		
				mlIPM = domSPS.getRelatedObjects(PRSPContext.get(), // Context
				DataConstants.REL_REFERENCE_DOCUMENT, // Relationship Pattern
				DataConstants.TYPE_PGIPMDOCUMENT, // Type Pattern
				busSelects, // Object Selects
				null, // Relationship Selects
				false, // get TO
				true, // get From
				(short) 1, // Recurrence Level
				sWhere, // Object Where
				null, // RelationShip Where
				0);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>  getRelatedIPM  mlIPM" + mlIPM);
		if (!mlIPM.isEmpty()) {
			Map<String, String> mpIPM = (Map) mlIPM.get(0);
			strConnectedIPMId = mpIPM.get(DomainConstants.SELECT_ID);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>  getRelatedIPM  strConnectedIPMId" + strConnectedIPMId);
			domIPM = DomainObject.newInstance(PRSPContext.get(), strConnectedIPMId);
		}
		return domIPM;
	}

	public DomainObject getRelatedIPM(DomainObject domStackingPatternObject) throws FrameworkException {
		domSPS = domStackingPatternObject;
		strSPSName = domStackingPatternObject.getInfo(PRSPContext.get(), DomainConstants.SELECT_NAME);
		return getRelatedIPM();
	}
	
	/**
	 * ALM 56363 DTCLD-728 Start
	 * @throws FrameworkException
	 * This method updates the owner of IPMDoc when the owner of IPMDoc and SPS is not same
	 */
	public void updateIPMOwner() throws FrameworkException {
		
	    try {
	    	Context context = PRSPContext.get();
	    	VPLMIntegTraceUtil.trace(context, ">>>>> updateIPMOwner method start, domSPS"+ domSPS);
	        // Get the owner of the StackingPattern (SPS)
	        String spsOwner = domSPS.getInfo(context, DomainConstants.SELECT_OWNER);
	        VPLMIntegTraceUtil.trace(context, ">>>>> updateIPMOwner method, SPS Owner"+ spsOwner);

	        // Get the connected IPM document
	        domIPM = getRelatedIPM();
	        VPLMIntegTraceUtil.trace(context, ">>>>> updateIPMOwner method, domIPM"+ domIPM);
	        // If IPM document is found, get its current owner
	        if (null != domIPM) {
	            String ipmCurrentOwner = domIPM.getInfo(context, DomainConstants.SELECT_OWNER);
	            VPLMIntegTraceUtil.trace(context, ">>>>> updateIPMOwner method, IPM Current Owner"+ ipmCurrentOwner);

	            // Check if the current owner of IPM is different from SPS owner
	            if (!spsOwner.equals(ipmCurrentOwner)) {
	                // Update the owner of IPM to match the owner of SPS
	                domIPM.setOwner(context, spsOwner);
	                VPLMIntegTraceUtil.trace(context, ">>>>> updateIPMOwner method, newIPMowner set");
	            } else {
	                VPLMIntegTraceUtil.trace(context, "IPM Owner is already the same as SPS Owner. No change needed.");
	            }
	        } else {
	            VPLMIntegTraceUtil.trace(context, "No connected IPM document found");
	        }
	    } catch (FrameworkException e) {
	        throw new FrameworkException("Error in updateIPMOwner: " + e.getMessage());
	    }
	}
	
	/**
	 * ALM 56363 DTCLD-728 Start
	 * @param String objectId
	 * @throws FrameworkException
	 * This method would sync the classification from SPS to IPMDoc
	 */
	public void syncClassificationToIPMDoc(String strSPSObjectId) throws FrameworkException {
		
	    try {
	    		Context context = PRSPContext.get();
	    		VPLMIntegTraceUtil.trace(context, ">>>>> syncClassificationToIPMDoc method start, domSPS::"+ domSPS);
	    		
	    		// Get the IP classes of the StackingPattern (SPS)
	    		CommonUtility commonUtility=new CommonUtility(context);
	    		MapList mlSPSIPClassInfo=commonUtility.getIPControlClassesOfObject(context, strSPSObjectId);
	    		VPLMIntegTraceUtil.trace(context, ">>>>> syncClassificationToIPMDoc method, mlSPSIPClassInfo::"+ mlSPSIPClassInfo);
	    		
	    		// Get the connected IPM document
	    		domIPM = getRelatedIPM();
	    		VPLMIntegTraceUtil.trace(context, ">>>>> syncClassificationToIPMDoc method, domIPM::"+ domIPM);
	    		// If IPM document is found, get its IP Classes and compare them with list of SPS IP Classes
	    		if (null != domIPM) {
	        	
	    			MapList mlIPMIPClassInfo=commonUtility.getIPControlClassesOfObject(context, domIPM.getInfo(context, DomainConstants.SELECT_ID));
	        		
	    			VPLMIntegTraceUtil.trace(context, ">>>>> syncClassificationToIPMDoc method, mlIPMIPClassInfo::"+ mlIPMIPClassInfo);
	        	 
	    			if(null!=mlSPSIPClassInfo && !mlSPSIPClassInfo.isEmpty()) {
	        	   
	    				StringList slSPSIPClassIdList=new StringList();
	    				Map mpSPSIPClassInfo;
	    				Map mpIPMDocIPClassInfo;
	    				String sIPMDocIPClassId;
	        	   
	    				for(int i=0;i<mlSPSIPClassInfo.size();i++) {
	    					mpSPSIPClassInfo=(Map)mlSPSIPClassInfo.get(i);
	    					slSPSIPClassIdList.add((String)mpSPSIPClassInfo.get(DomainConstants.SELECT_ID));
	    				}
	    				VPLMIntegTraceUtil.trace(context, ">>>>> syncClassificationToIPMDoc method, slSPSIPClassIdList::"+ slSPSIPClassIdList);
	        	   
	    				//remove already connected IP Control classes of IPMDoc from stringList
	    				for(int i=0;i<mlIPMIPClassInfo.size();i++) {
	    					mpIPMDocIPClassInfo=(Map)mlIPMIPClassInfo.get(i);
	    					sIPMDocIPClassId=(String)mpIPMDocIPClassInfo.get(DomainConstants.SELECT_ID);
	    					if(slSPSIPClassIdList.contains(sIPMDocIPClassId)){
	    						slSPSIPClassIdList.remove(sIPMDocIPClassId);
	    					}
	    				}
	    				VPLMIntegTraceUtil.trace(context, ">>>>> syncClassificationToIPMDoc method, final slSPSIPClassIdList::"+ slSPSIPClassIdList);
	        	   
	    				if(!slSPSIPClassIdList.isEmpty()) {
	    					DomainRelationship.connect(context, domIPM, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, false, slSPSIPClassIdList.toArray(new String[slSPSIPClassIdList.size()]));
	    					VPLMIntegTraceUtil.trace(context, ">>> Connected the IP Control classes to IPMDocument");
	    				}
	    			}
	    		} else {
	    			VPLMIntegTraceUtil.trace(context, ">>> No connected IPM document found");
	    		}
	    	} catch (FrameworkException e) {
	    		throw new FrameworkException(">>>Error in syncClassificationToIPMDoc: " + e.getMessage());
	    	}
	}

	/**
	 * @param domIPM
	 * @return SPS Object
	 * @throws FrameworkException
	 *             This method returns related SPS objects from a IPM Object
	 */
	public DomainObject getRelatedSPS(DomainObject domIPM) throws FrameworkException {

		MapList mlSPS = null;
		String strConnectedSPSId;
		StringList busSelects = new StringList(1);
		busSelects.add(DomainConstants.SELECT_ID);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>  getRelatedSPS domIPM " + domIPM);
		mlSPS = domIPM.getRelatedObjects(PRSPContext.get(), // Context
				DataConstants.REL_REFERENCE_DOCUMENT, // Relationship Pattern
				DataConstants.TYPE_PG_STACKINGPATTERN, // Type Pattern
				busSelects, // Object Selects
				null, // Relationship Selects
				true, // get TO
				false, // get From
				(short) 1, // Recurrence Level
				null, // Object Where
				null, // RelationShip Where
				0);
		VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>>>>  getRelatedSPS mlSPS " + mlSPS);
		if (!mlSPS.isEmpty()) {
			Map<String, String> mpSPS = (Map) mlSPS.get(0);
			strConnectedSPSId = mpSPS.get(DomainConstants.SELECT_ID);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>  getRelatedSPS strConnectedSPSId " + strConnectedSPSId);
			domSPS = DomainObject.newInstance(PRSPContext.get(), strConnectedSPSId);
		}
		return domSPS;
	}

	@Override
	public DomainObject getDomainObject() {
		return domSPS;
	}

	@Override
	public String getProductDataRevision() {
		return strSPSRevision;
	}

	@Override
	public void setAttributes() throws MatrixException {
		/*
		 * Currently nothing is to be set on SPS
		 */
	}

	public void setAttributes(Map<String, String> attrSPSMap) throws MatrixException {
		domSPS.setAttributeValues(PRSPContext.get(), attrSPSMap);
	}

	@Override
	public void setRelationship(String relName, String relId) {
		/**/
	}

	@Override
	public void mapAttributes(IMappableComponent parsedNode) throws MatrixException {
		/**/
	}

	public String getSPSChangeActionID(MapList mlCACOInfoList) {

		String strSPSChangeActionID = DomainConstants.EMPTY_STRING;
		if (mlCACOInfoList != null && !mlCACOInfoList.isEmpty()) {
			Map<?, ?> mpSPSCA = (Map<?, ?>) mlCACOInfoList.get(0);
			if (mpSPSCA != null) {
				strSPSChangeActionID = (String) mpSPSCA.get(DomainConstants.SELECT_ID);
			}
		}
		return strSPSChangeActionID;
	}
	
	/**
	 * Save TOPS XML- This method connects SPS to CA of TUP
	 * @param context
	 * @param strStackingPatternId
	 * @param strTUPChangeActionID
	 * @throws Exception
	 */
	public void processChangeManagement(Context context, String strStackingPatternId ,String strTUPChangeActionID) throws Exception {
		ChangeManagement changeManagement = new ChangeManagement();
		MapList mlSPSCACOInfoList = changeManagement.getCACOInfoList(context,strStackingPatternId);	
       
		String strSPSCAId = getSPSChangeActionID(mlSPSCACOInfoList);
		changeManagement.connectSPSToTUPCA(context,strStackingPatternId,strSPSCAId,strTUPChangeActionID);
		
	}

	@Override
	public void processAttributesForXMLTagsCreation(Map mpPart,Input input) throws MatrixException {
		//Dont want to implement anything right now
	}

	@Override
	public void getAttributesForDimsTag(Map hmAttributeMap) {
		//Dont want to implement anything right now
	}

	@Override
	public void createTags(Input input) {
		//Dont want to implement anything right now
	}
		
	/**
	 * @param domTUP
	 * @return SPS Id
	 * @throws FrameworkException
	 *             This method returns related System Generated SPS objects from a TUP Object
	 */
	public String getRelatedSystemGeneratedSPS(Context context,DomainObject domTUP) throws FrameworkException {

		MapList mlSPS = null;
		String strConnectedSPSId = null;
		StringList busSelects = new StringList();
		busSelects.add(DomainConstants.SELECT_ID);
		busSelects.add("attribute["+DataConstants.ATTR_PG_SPS_ORIGINATION+"]");
		
		String strWhere="attribute["+DataConstants.ATTR_PG_SPS_ORIGINATION+"]==\""+DataConstants.RANGE_VALUE_SYSTEM_GENERATED+"\"";
		
		mlSPS = domTUP.getRelatedObjects(context, // Context
				DataConstants.REL_PART_SPECIFICATION, // Relationship Pattern
				DataConstants.TYPE_PG_STACKINGPATTERN, // Type Pattern
				busSelects, // Object Selects
				null, // Relationship Selects
				false, // get TO
				true, // get From
				(short) 1, // Recurrence Level
				strWhere, // Object Where
				null, // RelationShip Where
				0);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  getRelatedSystemGeneratedSPS mlSPS " + mlSPS);
		if (!mlSPS.isEmpty()) {
			if(mlSPS.size()>1) {
				 throw new DesignToolsIntegrationException(errorTUPHaveMoreThanOneSPS.getExceptionCode(),
						 errorTUPHaveMoreThanOneSPS.getExceptionMessage());
			}
			Map<String, String> mpSPS = (Map) mlSPS.get(0);
			strConnectedSPSId = mpSPS.get(DomainConstants.SELECT_ID);			
		}
		return strConnectedSPSId;
	}
	
	/**
	 * DSM 2015x.1 : This method performs ownership sync for System Generated SPS depending upon the connected TUP
	 * This method is moved from pgDSOCPNProductData_mxJPO.java
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public void addRemoveOwnership(Context context, String strTUPId) throws Exception
	{		
		if(UIUtil.isNotNullAndNotEmpty(strTUPId))
		{
			PRSPContext.set(context);
			DomainObject doTUP = DomainObject.newInstance(context, strTUPId);
			String strSPSObjId ="";
			
			/*
			 * Context pushed to increase visibility of potential SPS which current user may not have access to because
			 * the access to the TUP has not been copied to the SPS
			 */
			ContextUtil.pushContext(context);
			try {
				strSPSObjId = getRelatedSystemGeneratedSPS(context,doTUP);
			}finally {
				ContextUtil.popContext(context);
			}
			
				
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addRemoveOwnership strSPSObjId " + strSPSObjId);
			Map paramMap = new HashMap();
			if(UIUtil.isNotNullAndNotEmpty(strSPSObjId))
			{
				StringList slSPSOwnershipList = getOwnership(context, strSPSObjId, DataConstants.TYPE_PG_STACKINGPATTERN);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addRemoveOwnership slSPSOwnershipList " + slSPSOwnershipList);
				
				if (slSPSOwnershipList != null && !slSPSOwnershipList.isEmpty()) {
					paramMap.put("busObjId", strSPSObjId);
					paramMap.put("emxTableRowIds", slSPSOwnershipList.toArray(new String[slSPSOwnershipList.size()]));
					JPO.invoke(context, "emxDomainAccess", null, "deleteAccess", JPO.packArgs(paramMap));	
					
				}
				
				StringList slTUPOwnershipList = getOwnership(context, strTUPId, DataConstants.TYPE_PG_TRANSPORTUNIT);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addRemoveOwnership slTUPOwnershipList " + slTUPOwnershipList);
				addTUPOwnershipToSPS(context, strTUPId, slTUPOwnershipList.toArray(new String[slTUPOwnershipList.size()]), strSPSObjId);
			}
		}
	}
	
	/**
	 * DSM 2015x.1 : This method adds ownership from TUP to connected System Generated SPS
	 * This method is moved from pgDSOCPNProductData_mxJPO.java
	 * @param context
	 * @param strTUPArray
	 * @param strSPSId
	 * @throws MatrixException
	 * @throws Exception
	 */
	private void addTUPOwnershipToSPS(Context context, String strTUPId, String[] strTUPArray, String strSPSId) throws Exception
	{
		StringList accessNames = DomainAccess.getLogicalNames(context, strTUPId);
		String defaultAccess = accessNames.get(0);
		String strTUPOwnershipAccess ;
		StringList idList;
		String ownerName;
		String strProject ;
		String strOrganization;
		String secContextId;
		int fIndex;
		int sIndex;
		int tIndex;
		boolean ownerCheck;
		String result ;
		boolean isPerson;
		StringList slPersonDetails;
		
		for(int i =0; i<strTUPArray.length;i++)
		{
			idList = StringUtil.split(strTUPArray[i], DataConstants.SEPARATOR_PIPE);
			if( idList.size() >2 )
			{
				strTUPOwnershipAccess = getAccessForOwnership(context, strTUPArray[i], strTUPId, accessNames);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addTUPOwnershipToSPS strTUPOwnershipAccess " + strTUPOwnershipAccess);
				if(UIUtil.isNotNullAndNotEmpty(strTUPOwnershipAccess) && !strTUPOwnershipAccess.equalsIgnoreCase(defaultAccess))
				{
					defaultAccess = strTUPOwnershipAccess;
				}
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addTUPOwnershipToSPS defaultAccess " + defaultAccess);
				ownerName = DomainObject.newInstance(context, strSPSId).getOwner(context).getName();
				strProject = "";
				strOrganization = "";
				secContextId = idList.get(0);
				sIndex = -1;
				tIndex = -1;
				fIndex =secContextId.indexOf(DataConstants.SEPARATOR_COLON);
				if (fIndex != -1)
				{
					sIndex =secContextId.indexOf(DataConstants.SEPARATOR_COLON, fIndex+1);
				}
				if (sIndex != -1)
				{
					tIndex =secContextId.indexOf(DataConstants.SEPARATOR_COLON, sIndex+1);
				}
				if (fIndex != -1 && sIndex != -1)
				{
					strOrganization = secContextId.substring(fIndex+1, sIndex);
				}
				if (sIndex != -1 && tIndex != -1)
				{
					strProject = secContextId.substring(sIndex+1, tIndex);
				}
				ownerCheck = false;
				result = DomainConstants.EMPTY_STRING;
				isPerson = false;
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addTUPOwnershipToSPS strOrganization " + strOrganization);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addTUPOwnershipToSPS strProject " + strProject);
				if (strProject.length() !=0)
				{
					isPerson = strProject.contains(DataConstants.CONSTANT_PRJ);
					if(isPerson)
					{
						ownerCheck = strProject.substring(0, strProject.indexOf(DataConstants.SEPARATOR_UNDERSCORE)).equals(ownerName);
						/*Commenting below mql for sonar error
						String cmd = "print role $1 select person dump";
						result = MqlUtil.mqlCommand(context, cmd, strProject);*/
						
						slPersonDetails = StringUtil.split(strProject, DataConstants.SEPARATOR_UNDERSCORE+DataConstants.CONSTANT_PRJ);	
						result = slPersonDetails.get(0);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addTUPOwnershipToSPS slPersonDetails.get(0) " + result);
						
						//To Do - Why context user is compared to ownership person ? It should just compare result with SPS owner because if TUP has SPS owner as MOA then there is no need to copy that MOA to SPS .
						//This code comes from pgDSOCPNProductData_mxJPO.java : addTUPOwnershipToSPS
						if(context.getUser().equals(result))
						{
							ownerCheck = true;
						}
					}
				}
				
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addTUPOwnershipToSPS isPerson " + isPerson);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>  addTUPOwnershipToSPS ownerCheck " + ownerCheck);
				if(isPerson && !ownerCheck)
				{
					String strPersonId = PersonUtil.getPersonObjectID(context,result);
					DomainAccess.createObjectOwnership(context, strSPSId, strPersonId, defaultAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
				}
				else if(!DataConstants.SEPARATOR_HYPHEN.equals(strOrganization) && UIUtil.isNotNullAndNotEmpty(strProject))
				{
					DomainAccess.createObjectOwnership(context, strSPSId, strOrganization, strProject, defaultAccess, DomainAccess.COMMENT_MULTIPLE_OWNERSHIP);
				}
			}
		}
	}
	
	/**
	 * Method to delete files from given SPSobject
	 * @param context
	 * @param ObjectId
	 * @param doObject
	 * @param fileName
	 * @throws MatrixException 
	 */
	public void deleteFilesFromSPSObject(Context context,String strObjectId,DomainObject doObject) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> START of deleteFilesFromObject method");
		
		MapList mlFileList=doObject.getAllFormatFiles(context);
		VPLMIntegTraceUtil.trace(context, ">>> deleteFilesFromObject mlFileList::"+mlFileList);
		
		
		if(null!=mlFileList && !mlFileList.isEmpty()) {
			Map mpFile;
			CommonUtility commonUtility=new CommonUtility(context);
			StringBuilder sbCommand;
			String checkedInFileName;
			
			for(int i=0;i<mlFileList.size();i++) {
				sbCommand=new StringBuilder();
				mpFile=(Map)mlFileList.get(i);
				checkedInFileName=(String)mpFile.get("filename");
				VPLMIntegTraceUtil.trace(context, ">>> deleteFilesFromObject checkedInFileName::"+checkedInFileName);
				
				if(checkedInFileName.startsWith(DataConstants.FILE_TECHNICALSTANDARD_PDF) && checkedInFileName.endsWith(DataConstants.FILE_FORMAT_PDF))
					sbCommand.append("delete bus ").append(strObjectId).append(" format ").append(mpFile.get("format")).append(" file ").append(checkedInFileName);
			
				VPLMIntegTraceUtil.trace(context, ">>> deleteFilesFromObject mql command::"+sbCommand.toString());
				
				if(sbCommand.length()>0)
					commonUtility.executeMQLCommands(context, sbCommand.toString());
			}
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of deleteFilesFromObject method");
	}
	
	/**
	 * Method to copy the IPMDocument 
	 * Added for DTCLD-754: ON SPS Copy using Copy at TUP - IPSDocument is not copied
	 * @throws FrameworkException
	 */
	public int copyObject (Context context, String strSourceSPSId, String strNewSPSId) throws FrameworkException {
		PRSPContext.set(context);
		VPLMIntegTraceUtil.trace(context, ">>> START of copyObject method");
		
		//Success parameter for the trigger
		int isuccess = 0;
		VPLMIntegTraceUtil.trace(context, ">>> strSourceSPSId:: "+strSourceSPSId);
		VPLMIntegTraceUtil.trace(context, ">>> strNewSPSId:: "+strNewSPSId);
		try {
			DomainObject sourceSPS = DomainObject.newInstance(context, strSourceSPSId);
			DomainObject clonedSPS = DomainObject.newInstance(context, strNewSPSId);
			String pgSPSOriginationValue = clonedSPS.getInfo(context, DataConstants.SELECT_ATTR_PG_SPS_ORIGINATION);
			VPLMIntegTraceUtil.trace(context, ">>> copyObject pgSPSOriginationValue:: "+pgSPSOriginationValue);
			if (pgSPSOriginationValue != null && pgSPSOriginationValue.equals(DataConstants.RANGE_VALUE_SYSTEM_GENERATED)) {
				StackingPatternDocument objSPSDoc = new StackingPatternDocument();
				objSPSDoc.copyObject(sourceSPS, clonedSPS);
			}
			
		} catch(Exception e) {
			VPLMIntegTraceUtil.trace(context, ">>> Inside catch block of copyObject "+e.getMessage());
			//Failure parameter for trigger
			isuccess = 1;
		}
		
		return isuccess;
		
	}
	

	/**
	 * Provider of file functionality to the SPS in current implementation
	 * Intent is to change this in future to use different document type since current
	 * implementation is using the DSM IPMDocument type that imposes design constraints
	 * @author GQS
	 *
	 */
	public class StackingPatternDocument extends CommonDocumentHandler {

		String sCheckInReasonValue = EnoviaResourceBundle.getProperty(PRSPContext.get(),
				DataConstants.CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE, PRSPContext.get().getLocale(),
				"emxEngineeringCentral.Common.TOPSXML");

		boolean bNeedsContextPush = false;
		BusinessObject busIPMDOC;
		String strIPMBusId;
		String strIPMDocName;
		String strIPMDocRev;

		TOPSSAXBuilder saxBuilder = new TOPSSAXBuilder();
		SimpleXML simpleXML = new SimpleXML();
		MasterConsumerUnit mcop = new MasterConsumerUnit();
		MasterCustomerUnit mcup = new MasterCustomerUnit();
		MasterInnerPackUnit mip = new MasterInnerPackUnit();
		TransportUnitPart tup = new TransportUnitPart();
		File fileRoot ;
		String file;
		String strPath;
		String docName ;
		boolean doSave = false;
		String encFileName;
		String fullFilePath;
		String selBusId = "";
		private StringList slPartType = new StringList();
		String partType ="";
		private StringList tagsNeeded = new StringList();
		String strPalletTypeValue;
		String objTag;
		StringBuilder sbObjTag = new StringBuilder();
		HashMap<String, Map<?, ?>> mpPart = new HashMap<>();
		boolean founduplicates = false;
		boolean foundObject = false;
		Context context;
		DomainObject domObjectIPM;
		DomainObject domTUPObj ;
		DomainObject domStackingPatternObj ;
		

		DataConstants.customTOPSExceptions errorInvalidSpsTupCurrentState = DataConstants.customTOPSExceptions.ERROR_400_INVALID_SPS_TUP_CURRENT_STATE;
		DataConstants.customTOPSExceptions errorMajorObjectLocked = DataConstants.customTOPSExceptions.ERROR_400_MAJOR_OBJECT_LOCKED;
		DataConstants.customTOPSExceptions errorMinorObjectLocked = DataConstants.customTOPSExceptions.ERROR_400_MINOR_OBJECT_LOCKED;
		DataConstants.customTOPSExceptions errorSPSLockedByAnotherUser = DataConstants.customTOPSExceptions.ERROR_400_SPS_LOCKED_BY_ANOTHER_USER;
		DataConstants.customTOPSExceptions errorTUPLockedByAnotherUser = DataConstants.customTOPSExceptions.ERROR_400_TUP_LOCKED_BY_ANOTHER_USER;
		DataConstants.customTOPSExceptions errorManualSPSConnected = DataConstants.customTOPSExceptions.ERROR_400_MANUAL_SPS_CONNECTED_TO_TUP;

		/**
		 * @throws MatrixException
		 *             This method created IPM Document and sets its attributes values
		 */
		public void createIPMDocument() throws MatrixException {
			context = PRSPContext.get();
			String spsDocName = strSPSName + DataConstants.SEPARATOR_UNDERSCORE + strSPSRevision;
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>  createIPMDocument Start spsDocName" + spsDocName);
			busIPMDOC = new BusinessObject(DataConstants.TYPE_PGIPMDOCUMENT, spsDocName,
					DataConstants.CONSTANT_REVISION_ZERO, DataConstants.VAULT_ESERVICE_PRODUCTION);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>  createIPMDocument busIPMDOC" + busIPMDOC);
			if (!busIPMDOC.exists(context)) {
				busIPMDOC.create(context, DataConstants.POLICY_IPM_DOCUMENT);
				strIPMBusId = busIPMDOC.getObjectId();

				DomainObject domIPMDoc = DomainObject.newInstance(context, strIPMBusId);
				domIPM = domIPMDoc;
				VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>  createIPMDocument domIPMDoc" + domIPMDoc);
				domIPMDoc.setAttributeValue(context, DataConstants.ATTR_TITLE, spsDocName);

				DomainRelationship.connect(context, domSPS, DataConstants.REL_REFERENCE_DOCUMENT, domIPMDoc);
				VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>  createIPMDocument after connect");
				StringList slIPMDoc = new StringList();
				slIPMDoc.add(DomainConstants.SELECT_NAME);
				slIPMDoc.add(DomainConstants.SELECT_REVISION);

				Map<String, String> mapIPMDoc = domIPMDoc.getInfo(context, slIPMDoc);
				strIPMDocName = mapIPMDoc.get(DomainConstants.SELECT_NAME);
				strIPMDocRev = mapIPMDoc.get(DomainConstants.SELECT_REVISION);
			}
		}

		/**
		 * @param filename
		 * @throws MatrixException
		 *             This method creates new version of IPM Document
		 */
		public void createIPMVersion(String filename) throws MatrixException {
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>>>>  createIPMVersion START");
			context = PRSPContext.get();
			setId(strIPMBusId);
			Map<String, String> attribMap = new HashMap<>(1);
			attribMap.put(DomainConstants.ATTRIBUTE_CHECKIN_REASON, sCheckInReasonValue);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>  createIPMVersion before calling createVersion");
			createVersion(context, filename, filename, attribMap);
			VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>  createIPMVersion after calling createVersion");
		}

		/**
		 * @param store
		 * @param format
		 * @param srcFolder
		 * @param content
		 * @param streaming
		 * @throws Exception
		 *             This method does checkin of a file according to some conditions .
		 *             Conditions depend on if type is SPS or IPM
		 */
		public void checkinFile(String store, String format, String srcFolder, byte[] content, boolean streaming)
				throws Exception {
			
				DomainObject minorBo = null;
				DomainObject domMajor = null;
				context = PRSPContext.get();
				Access accessMaskIPM = new Access();
				Access accessMaskSPS = new Access();
				VPLMIntegTraceUtil.trace(context, ">>> START of checkinFile method fileName:::"+fileName);
				
				if (fileName.endsWith(DataConstants.FILE_FORMAT_XML) || fileName.startsWith(DataConstants.FILE_TRANSPORTVIEW_PDF)) {

					String strSPSCurrentState = CommonUtility.getCurrentState(domSPS);
					VPLMIntegTraceUtil.trace(PRSPContext.get(), "checkinFile strSPSCurrentState " + strSPSCurrentState);
					getRelatedTUP(domSPS);
					if (domTUP == null) {
						throw new DesignToolsIntegrationException(errorNoTUP.getExceptionCode(),
								errorNoTUP.getExceptionMessage());
					}
					String strTUPCurrentState = CommonUtility.getCurrentState(domTUP);
					VPLMIntegTraceUtil.trace(PRSPContext.get(), "checkinFile strTUPCurrentState " + strTUPCurrentState);

					if (DataConstants.STATE_RELEASE.equalsIgnoreCase(strSPSCurrentState)
							|| !DataConstants.STATE_PRELIMINARY.equalsIgnoreCase(strTUPCurrentState)) {

						throw new DesignToolsIntegrationException(errorInvalidSpsTupCurrentState.getExceptionCode(),
								errorInvalidSpsTupCurrentState.getExceptionMessage());
					}

					accessMaskIPM = checkOwnershipAccess(CommonUtility.getOwner(domIPM), domIPM);
				
					/**
					 * Lock management
					 */
					StringList selectList = new StringList();
					selectList.add(DomainConstants.SELECT_LOCKER);
					selectList.add(DomainConstants.SELECT_ID);
					selectList.add(CommonDocument.SELECT_TITLE);
					String objectWhere = CommonDocument.SELECT_TITLE + "== const'" + fileName + "'";
					@SuppressWarnings("rawtypes")
					Map<String, String> majorMap = domIPM.getInfo(context, selectList);
					VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>  checkinFile majorMap " + majorMap);
					String majorLocker = majorMap.get(DomainConstants.SELECT_LOCKER);
					manageLockUnlock(domIPM, majorLocker, true);

					// Minor Bo
					@SuppressWarnings("deprecation")
					MapList mlist = domIPM.getRelatedObjects(context, // context.
							CommonDocument.RELATIONSHIP_ACTIVE_VERSION, // rel filter.
							CommonDocument.TYPE_DOCUMENTS, // type filter.
							selectList, // business selectables.
							null, // relationship selectables.
							false, // expand to direction.
							true, // expand from direction.
							(short) 1, // level
							objectWhere, // object where clause
							DomainConstants.EMPTY_STRING,
							0); // relationship where clause
					
					VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>  checkinFile mlist " + mlist);
					if (mlist != null && mlist.size() == 1) {
						@SuppressWarnings("rawtypes")
						Map<String, String> minorMap = (Map) mlist.get(0);
						String minorLocker = minorMap.get(DomainConstants.SELECT_LOCKER);
						minorBo = DomainObject.newInstance(context,minorMap.get(DomainConstants.SELECT_ID));
						VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>  checkinFile minorBo " + minorBo);
						manageLockUnlock(minorBo, minorLocker, false);
					}
					VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>>>>>>>  checkinFile before CDM versioning ");
					/**
					 * CDM versioning
					 */
					if (!bIPMDocumentCreated) {
						((CommonDocument) domIPM).reviseVersion(context, fileName, fileName,
								new HashMap<Object, Object>());
					}
					domMajor = domIPM;
					VPLMIntegTraceUtil.trace(PRSPContext.get(),
							">>>>>>>>>  checkinFile after CDM versioning domMajor " + domMajor);
				} else if (fileName.endsWith(DataConstants.FILE_FORMAT_PDF)) {
					getRelatedTUP(domSPS);
					accessMaskSPS = checkOwnershipAccess(CommonUtility.getOwner(domSPS),domSPS);
					domMajor = domSPS;
					VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>checkinFile when TNR is for SPS " + domMajor);
					String strSPSLocker = domSPS.getLocker(PRSPContext.get()).toString();
					VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>>checkinFile strSPSLocker " + strSPSLocker);
					
					if(UIUtil.isNotNullAndNotEmpty(strSPSLocker) && !context.getUser().equalsIgnoreCase(strSPSLocker)) {
						throw new DesignToolsIntegrationException(errorSPSLockedByAnotherUser.getExceptionCode(),
								errorSPSLockedByAnotherUser.getExceptionMessage()+strSPSLocker);
					}
				}
				
			
				checkinFile(context, domMajor, format, store, fileName, srcFolder, content, streaming);
				VPLMIntegTraceUtil.trace(context, ">>checkinFile after actual checkin of file");
			/**
				 * Release locks
				 */
				if ( null != minorBo ) {
					
					unlockDO(context,minorBo);
				}
				
				if (null != domMajor) {
					
					unlockDO(context,domMajor);
				}
				
				// Revoke Access rights granted in method checkOwnershipAccess
				if(isAccessGranted) {
					revokeAccessRight(context,accessMaskIPM, domIPM);
					revokeAccessRight(context,accessMaskSPS, domSPS);
				}
		}

		/**
		 * Method to verify object and disconnect the previous revision files from the current SPS and IPM Doc objects
		 * @param domMajor
		 * @throws MatrixException
		 */
		public void validateAndDisconnectPrevRevFilesFromCurrentRev(DomainObject domMajor) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>> START of validateAndDisconnectPrevRevFilesFromCurrentRev method");
			
			StringList slSelects=new StringList(2);
			slSelects.add(DomainConstants.SELECT_ID);
			slSelects.add(DomainConstants.SELECT_NAME);
			
			boolean bContextPushed=false;
			try {
				if(null!=domMajor) {
					//pushed context as user may not have access to delete files/disconnect the objects
					ContextUtil.pushContext(context);
					bContextPushed=true;
					Map mpObjInfo=domMajor.getInfo(context, slSelects);
					
					String strObjectId=(String) mpObjInfo.get(DomainConstants.SELECT_ID);
					String strObjectName=(String) mpObjInfo.get(DomainConstants.SELECT_NAME);
					
					VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev strObjectId::"+strObjectId+" strObjectName::"+strObjectName);
					
					if(domMajor.isKindOf(context, DataConstants.TYPE_PG_STACKINGPATTERN)) {
						VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev inside SPS logic");
						
						BusinessObject boPrevRev=domMajor.getPreviousRevision(context);
						VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev boPrevRev::"+boPrevRev);
						
						if(boPrevRev.exists(context)) {
							deleteFilesFromSPSObject(context,strObjectId,domMajor);
						}
					}
					else if(domMajor.isKindOf(context, DataConstants.TYPE_PGIPMDOCUMENT) && !strObjectName.endsWith("001")) {
						VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev inside IPM Doc logic");
						
						//get the SPS object connected to IPM Doc. Later fetch the prev Rev of SPS object
						
						DomainObject doTempSPS=getRelatedSPS(domMajor);
						BusinessObject boPrevRev=doTempSPS.getPreviousRevision(context);
						VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev boPrevRev of SPS connected to IPMDoc::"+boPrevRev);
						
						String tempFileName=fileName;
						String strSPSRev=strObjectName.substring(strObjectName.indexOf("_")+1, strObjectName.length());
						String strPrevRev=strSPSRev;
						
						if(boPrevRev.exists(context))
							strPrevRev=boPrevRev.getRevision();
						String strTitle=tempFileName.replace(strSPSRev, strPrevRev);
						
						//get the active version with Title having prev revision
						VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev strSPSRev::"+strSPSRev+" strPrevRev::"+strPrevRev+" strTitle::"+strTitle);
						
						MapList mlActiveVersion=getActiveOrLatestVersionAsPerTitle(context,domMajor,strTitle,true);   //true for getting Active version, false to get Latest Version
						MapList mlLatestVersion=getActiveOrLatestVersionAsPerTitle(context,domMajor,strTitle,false);   //false to get Latest Version
			
						if(null!=mlActiveVersion && !mlActiveVersion.isEmpty()) {
							Map mpActiveVersion=(Map)mlActiveVersion.get(0);
							DomainRelationship.disconnect(context, (String) mpActiveVersion.get(DomainConstants.SELECT_RELATIONSHIP_ID));
							VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev disconnected the active version object with Title "+strTitle);
						}
						
						if(null!=mlLatestVersion && !mlLatestVersion.isEmpty()) {
							Map mpLatestVersion=(Map)mlLatestVersion.get(0);
							DomainRelationship.disconnect(context, (String) mpLatestVersion.get(DomainConstants.SELECT_RELATIONSHIP_ID));
							VPLMIntegTraceUtil.trace(context, ">>> validateAndDisconnectPrevRevFilesFromCurrentRev  disconnected the latest version object with Title "+strTitle);
						}
					}
				}
			}
			catch(MatrixException e){
				VPLMIntegTraceUtil.trace(context, ">>> Inside Catch of validateAndDisconnectPrevRevFilesFromCurrentRev method::"+e.getMessage());
			}
			finally {
				if(bContextPushed)
					ContextUtil.popContext(context);
			}
			
			VPLMIntegTraceUtil.trace(context, "<<< END of validateAndDisconnectPrevRevFilesFromCurrentRev method");
		}

		/**
		 * @param domObject
		 * @param locker
		 * @param isMajorBo
		 * @throws MatrixException
		 *             This method locks and unlocks the bus object
		 */
		public void manageLockUnlock(DomainObject domObject, String locker, boolean isMajorBo)
				throws MatrixException {
			context = PRSPContext.get();
			VPLMIntegTraceUtil.trace(context, ">> START of manageLockUnlock method context user::"+context.getUser());
			VPLMIntegTraceUtil.trace(context, ">> manageLockUnlock busObject::"+domObject+" locker::"+locker+" isMajorBo::"+isMajorBo);
			if (UIUtil.isNullOrEmpty(locker)) {
				
				try {
					VPLMIntegTraceUtil.trace(context, ">> manageLockUnlock before locking the object");
					lockDO(context,domObject);
					VPLMIntegTraceUtil.trace(context, ">> manageLockUnlock after locking the object");
				}catch(Exception e) {
					//If major obj locker is blank and yet you cannot lock major object that means its minor obj is locked
					if (isMajorBo) {
						throw new DesignToolsIntegrationException(errorMinorObjectLocked.getExceptionCode(),
								errorMinorObjectLocked.getExceptionMessage());
					} 
					else {
						throw new DesignToolsIntegrationException(errorMajorObjectLocked.getExceptionCode(),
								errorMajorObjectLocked.getExceptionMessage());
					}
				}
			} else if (!locker.equalsIgnoreCase(context.getUser())) {
				try {
					
					VPLMIntegTraceUtil.trace(context, ">> manageLockUnlock before unlocking the object if locker is not same as context user");
					unlockDO(context,domObject);
					VPLMIntegTraceUtil.trace(context, ">> manageLockUnlock after unlocking the object if locker is not same as context user");
					VPLMIntegTraceUtil.trace(context, ">> manageLockUnlock before locking the object if locker is not same as context user");
					lockDO(context,domObject);
					VPLMIntegTraceUtil.trace(context, ">> manageLockUnlock after locking the object if locker is not same as context user");
					
				}catch(Exception e) {
					if (isMajorBo) {
						unlockDO(context,domObject);
						throw new DesignToolsIntegrationException(errorMajorObjectLocked.getExceptionCode(),
								errorMajorObjectLocked.getExceptionMessage());
					} 
					else {
						throw new DesignToolsIntegrationException(errorMinorObjectLocked.getExceptionCode(),
								errorMinorObjectLocked.getExceptionMessage());
					}
				}
			}			
		}

		/**
		 * @param objfiles
		 * @throws Exception
		 *             This method is called from checkin2int method. This method acts
		 *             like a wrapper when checkin is done from TOPS Client
		 * Context is pushed as user may not have access granting capability
		 * This method also creates and checksin Combined xml file 
		 */
		public void processFile(ObjectFiles objfiles) throws Exception {
			boolean bCombinedXML = false;
			ObjectFile objFile = null;
			while (objfiles.hasNext()) {
				objFile = objfiles.next();
				VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>processFile objFile " + objFile);
				VPLMIntegTraceUtil.trace(PRSPContext.get(), ">>processFile obj File Name " + objFile.getFileName());
				checkinFile(objFile.getFileStore(), objFile.getFileFormat(), objFile.getFileDir(),
						objFile.getFileContent(), objFile.isFileStreaming());
			
				try {
					//START: Added for DTCLD-718: Creation and checkin of Combined xml
					//Logic for combined xml
					VPLMIntegTraceUtil.trace(context, ">>Inside try block for generation of combined xml");
					if (fileName.startsWith(DataConstants.REFERENCE_XML_PREFIX)) {
						VPLMIntegTraceUtil.trace(context, ">>>>>>>> filename::::: "+ fileName);
						File refXmlFile = new File(new StringBuilder().append(objFile.getFileDir()).append(DataConstants.FORWARD_SLASH).append(fileName).toString());
						Document refXmlDoc = SAXHandler.loadXMLDocument(refXmlFile);
						
						fileName = fileName.replace(DataConstants.REFERENCE_XML_PREFIX, DataConstants.COMBINED_XML_PREFIX);
						boolean bCombinedXmlExists = SAXHandler.checkIfCombinedXmlExists(objFile.getFileDir(),fileName);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>> bCombinedXmlExists::::: "+ bCombinedXmlExists);

						if (bCombinedXmlExists) {

							Document combinedXmlDoc = SAXHandler.loadXMLDocument(SAXHandler.getCombinedXmlFile(objFile.getFileDir(), fileName));
								
							// Clone the content of Reference.xml into a new document
							Document clonedDoc = SAXHandler.cloneXMLDocument(refXmlDoc);
							// Prepend copied contents to the top of Combined XML document
							SAXHandler.prependToCombinedXml(combinedXmlDoc, clonedDoc);
							// Save the updated Combined XML file
							SAXHandler.writeXMLDocument(clonedDoc, SAXHandler.getCombinedXmlFile(objFile.getFileDir(), fileName));
							bCombinedXML = true;
							VPLMIntegTraceUtil.trace(context, ">>>>>>>> Combined xml already exists, copied the tags of Reference xml file");
						} else {
							// Combined XML doesn't exist, clone and set the flag to true
							Document combinedXmlDoc = SAXHandler.cloneXMLDocument(refXmlDoc);

							// Save the new Combined XML file
							SAXHandler.writeXMLDocument(combinedXmlDoc, SAXHandler.getCombinedXmlFile(objFile.getFileDir(), fileName));
							
							VPLMIntegTraceUtil.trace(context, ">>>>>>>> Combined xml didn't exist, cloned the Reference xml file");
						}
					} else if (fileName.startsWith(DataConstants.SIMPLE_XML_PREFIX)) {
						VPLMIntegTraceUtil.trace(context, ">>>>>>>> filename::::: "+ fileName);
						File simpleXmlFile =new File(new StringBuilder().append(objFile.getFileDir()).append(DataConstants.FORWARD_SLASH).append(fileName).toString());
						Document simpleXmlDoc = SAXHandler.loadXMLDocument(simpleXmlFile);
							
						fileName = fileName.replace(DataConstants.SIMPLE_XML_PREFIX, DataConstants.COMBINED_XML_PREFIX);
						boolean bCombinedXmlExists = SAXHandler.checkIfCombinedXmlExists(objFile.getFileDir(), fileName);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>> bCombinedXmlExists::::: "+ bCombinedXmlExists);
						
						if (bCombinedXmlExists) {
							Document combinedXmlDoc = SAXHandler.loadXMLDocument(SAXHandler.getCombinedXmlFile(objFile.getFileDir(), fileName));
							// Merge elements from Simple xml into Combined XML document
							SAXHandler.mergeXMLDocuments(combinedXmlDoc, simpleXmlDoc);
							// Save the new Combined XML file
							SAXHandler.writeXMLDocument(combinedXmlDoc, SAXHandler.getCombinedXmlFile(objFile.getFileDir(), fileName));
							
							bCombinedXML = true;
							VPLMIntegTraceUtil.trace(context, ">>>>>>>> Combined xml already exists, merged the tags of Simple xml file");
						} else {
							// Create new Combined XML file
							SAXHandler.writeXMLDocument(simpleXmlDoc, SAXHandler.getCombinedXmlFile(objFile.getFileDir(), fileName));
							VPLMIntegTraceUtil.trace(context, ">>>>>>>> Combined xml didn't exist, copied the Simple xml file");
						}
					}
					
					VPLMIntegTraceUtil.trace(context, ">>>>>> bCombinedXML " + bCombinedXML);
					if (bCombinedXML) {
						VPLMIntegTraceUtil.trace(context, ">>>>>>>> Before checkin of Combined xml");
						checkinFile(objFile.getFileStore(), objFile.getFileFormat(), objFile.getFileDir(),
								objFile.getFileContent(), objFile.isFileStreaming());
						VPLMIntegTraceUtil.trace(context, ">>>>>>>> After checkin of Combined xml");
						context.deleteWorkspaceFile(fileName);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>> Combined xml deleted from workspace");
					}

				} catch (Exception e) {
						VPLMIntegTraceUtil.trace(context, ">>Inside catch block for generation of combined xml");
						VPLMIntegTraceUtil.trace(context, ">>error"+e.getMessage());
				}
			}
		}
		//END: Added for DTCLD-718: Creation and checkin of Combined xml

		/**
		 * Save TOPS XML - This method maps the names to the defined Strings
		 * @param partType
		 * @return
		 */
		public String mapPartTypeToDefinedString(String partType) {
			if (DataConstants.TYPE_PG_TRANSPORTUNIT.equalsIgnoreCase(partType)) {
				partType = DataConstants.CONSTANT_TUP;
			} else if (DataConstants.TYPE_PG_MASTERCONSUMERUNIT.equalsIgnoreCase(partType)) {
				partType = DataConstants.CONSTANT_MCOP;
			} else if (DataConstants.TYPE_PG_MASTERINNERPACKUNIT.equalsIgnoreCase(partType)) {
				partType = DataConstants.CONSTANT_MIP;
			} else if (DataConstants.TYPE_PG_MASTERCUSTOMERUNIT.equalsIgnoreCase(partType)) {
				partType = DataConstants.CONSTANT_MCUP;
			}
			return partType;
		}
		
		private MapList getEBOM(Context context, DomainObject tempPartObj) throws FrameworkException {
			
			SelectList slRelSelStmts = new SelectList();
			slRelSelStmts.add(DomainRelationship.SELECT_ID);
			slRelSelStmts.add(DataConstants.SELECT_ATTRIBUTE_QUANTITY);

			StringList slbusSelStmts = new StringList();
			slbusSelStmts.add(DomainConstants.SELECT_ID);
			slbusSelStmts.add(DomainConstants.SELECT_TYPE);
			slbusSelStmts.add(DomainConstants.SELECT_NAME);
			slbusSelStmts.add(DomainConstants.SELECT_REVISION);
			slbusSelStmts.add(DomainConstants.SELECT_VAULT);
			
			Pattern typePattern = new Pattern(DataConstants.TYPE_PG_MASTERCONSUMERUNIT);
			typePattern.addPattern(DataConstants.TYPE_PG_MASTERCUSTOMERUNIT);
			typePattern.addPattern(DataConstants.TYPE_PG_MASTERINNERPACKUNIT);
			
			return tempPartObj.getRelatedObjects(context, // Context
					DataConstants.REL_EBOM, // Relationship Pattern
					typePattern.getPattern(), // Type Pattern
					slbusSelStmts, // Object Selects
					slRelSelStmts, // Relationship Selects
					false, // get TO
					true, // get From
					(short) 1, // Recurrence Level
					null, // Object Where
					null, // RelationShip Where
					0);
			
		}

		/**
		 * Save TOPS XML -  This method determines object tags needed in the xml As per TUP BOM  
		 * <extAddlInfo>Transport Unit|pgTransportUnitPart|TUP-00001614|001|eService Production|20336.41905.53198.32481~Master Customer Unit
		 * |pgMasterCustomerUnitPart|MCUP-00001195|001|eService Production</extAddlInfo>
		 * @param strTransportUnitId
		 * @throws MatrixException
		 */
		public void determineTagsNeeded(String strTransportUnitId) throws MatrixException {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  determineTagsNeeded Start ");

			// Setting Temp Object as the Object Id from Request, which can be TransportUnit
			// from the functionality perspective.
			String tempPartObjId = strTransportUnitId;

			boolean foundMasterConsumerUnit = false;

			// If Master Inner Pack unit is Selected , there can be Master Consumer Part
			// connected to it as a child,
			// we need to ignore it adding to the XML.
			if (!DataConstants.CONSTANT_MIP.equals(partType)) {
				foundMasterConsumerUnit = true;
			}

			DomainObject tempPartObj;
			String strChildPartId = "";
			String strChildPartType = "";
			String strChildPartName = "";
			String strChildPartRev = "";
			String strChildRelPartType = "";
			boolean hasAUnit;
			String strRelId = "";
			String strChildPartVault = "";
			
			while (!foundObject || !foundMasterConsumerUnit) {
				tempPartObj = DomainObject.newInstance(context, tempPartObjId);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  determineTagsNeeded tempPartObj "+tempPartObj);
				hasAUnit = false;

				// Expand the Transport Unit one level with EBOM Relationship. This will
				// recursive call.
				MapList mlEBOM = getEBOM(context ,tempPartObj);

				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  determineTagsNeeded mlEBOM "+mlEBOM);
				Iterator partItr = mlEBOM.iterator();
				//This while loop is required for the case where one TUP has more than one MCUP's under it.
				while (partItr.hasNext()) {
					Map mpChildPart = (Map) partItr.next();
					strChildPartId = (String) mpChildPart.get(DomainConstants.SELECT_ID);
					strChildPartType = (String) mpChildPart.get(DomainConstants.SELECT_TYPE);
					strChildPartName = (String) mpChildPart.get(DomainConstants.SELECT_NAME);
					strChildPartRev = (String) mpChildPart.get(DomainConstants.SELECT_REVISION);
					strChildPartVault = (String) mpChildPart.get(DomainConstants.SELECT_VAULT);
					strRelId = (String) mpChildPart.get(DomainRelationship.SELECT_ID);

					strChildRelPartType = mapPartTypeToDefinedString(strChildPartType);

					tempPartObjId = strChildPartId;
					hasAUnit = true;
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  determineTagsNeeded strChildPartName "+strChildPartName);
					// If the Type is not already present on the HashMap (tagneeded)then add it.
					// else set the boolean true and break the loop
					
					if (!tagsNeeded.contains(strChildRelPartType)) {
						tagsNeeded.add(strChildRelPartType);
					} else {
						founduplicates = true;
					}
					mpPart.put(strChildRelPartType, mpChildPart);
				}
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  determineTagsNeeded hasAUnit "+hasAUnit+" founduplicates::"+founduplicates);
				// If Duplicate is available, then break the outer loop
				if (!hasAUnit || founduplicates) {
					break;
				}
				
				// Adding Object Tag with object Details and relationship Id (EBOM)
				if (strChildPartId.equals(selBusId)) {
			
					foundObject = true;
					sbObjTag.append(DataConstants.SEPARATOR_PIPE);
					sbObjTag.append(strRelId);
					sbObjTag.append(DataConstants.SEPARATOR_TILDE);
					sbObjTag.append(strChildRelPartType);
					sbObjTag.append(DataConstants.SEPARATOR_PIPE);
					sbObjTag.append(strChildPartType);
					sbObjTag.append(DataConstants.SEPARATOR_PIPE);
					sbObjTag.append(strChildPartName);
					sbObjTag.append(DataConstants.SEPARATOR_PIPE);
					sbObjTag.append(strChildPartRev);
					sbObjTag.append(DataConstants.SEPARATOR_PIPE);
					sbObjTag.append(strChildPartVault);
					
					objTag = sbObjTag.toString();
				} else {
					if (!foundObject) {
						sbObjTag.append(DataConstants.SEPARATOR_PIPE);
						sbObjTag.append(strRelId);
						sbObjTag.append(DataConstants.SEPARATOR_TILDE);
						sbObjTag.append(strChildRelPartType);
						sbObjTag.append(DataConstants.SEPARATOR_PIPE);
						sbObjTag.append(strChildPartType);
						sbObjTag.append(DataConstants.SEPARATOR_PIPE);
						sbObjTag.append(strChildPartName);
						sbObjTag.append(DataConstants.SEPARATOR_PIPE);
						sbObjTag.append(strChildPartRev);
						sbObjTag.append(DataConstants.SEPARATOR_PIPE);
						sbObjTag.append(strChildPartVault);
						objTag = sbObjTag.toString();
					}
				}

				// If the selected part type and the type iteration are equal then, no need to
				// add the object in the XML.
				if (DataConstants.CONSTANT_MIP.equals(partType)
						&& DataConstants.CONSTANT_MCOP.equals(strChildRelPartType)) {
					foundMasterConsumerUnit = true;
				}
			}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  determineTagsNeeded END objTag "+objTag);
		}
		
		/**Save TOPS XML -  This method determines object tags needed for TUP
		 * <extAddlInfo>Transport Unit|pgTransportUnitPart|TUP-00001614|001|eService Production|
		 * @throws FrameworkException
		 */
		private void buildObjtag() throws FrameworkException {

			StringList slTUPSelectables = new StringList();
			slTUPSelectables.add(DomainConstants.SELECT_ID);
			slTUPSelectables.add(DomainConstants.SELECT_TYPE);
			slTUPSelectables.add(DomainConstants.SELECT_NAME);
			slTUPSelectables.add(DomainConstants.SELECT_REVISION);
			slTUPSelectables.add(DomainConstants.SELECT_VAULT);
			slTUPSelectables.add(DomainConstants.SELECT_VAULT);
			slTUPSelectables.add(DataConstants.SELECT_ATTR_PG_PALLETTYPE);
			
			Map mpTUPObjectDetails = domTUPObj.getInfo(context, slTUPSelectables);
			String strTUPName = (String)mpTUPObjectDetails.get(DomainConstants.SELECT_NAME);
			String strTUPType = (String)mpTUPObjectDetails.get(DomainConstants.SELECT_TYPE);
			String strTUPRev = (String)mpTUPObjectDetails.get(DomainConstants.SELECT_REVISION);
			String strTUPVault = (String)mpTUPObjectDetails.get(DomainConstants.SELECT_VAULT);
			strPalletTypeValue = (String)mpTUPObjectDetails.get(DataConstants.SELECT_ATTR_PG_PALLETTYPE);
		
			sbObjTag.append(DataConstants.CONSTANT_TUP); 
			sbObjTag.append(DataConstants.SEPARATOR_PIPE); 
			sbObjTag.append(strTUPType); 
			sbObjTag.append(DataConstants.SEPARATOR_PIPE); 
			sbObjTag.append(strTUPName); 
			sbObjTag.append(DataConstants.SEPARATOR_PIPE); 
			sbObjTag.append(strTUPRev); 
			sbObjTag.append(DataConstants.SEPARATOR_PIPE); 
			sbObjTag.append(strTUPVault); 
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  buildObjtag sbObjTag "+sbObjTag);
		}
		
		/**
		 * Save TOPS XML -  This is a wrapper which invokes logic for Reference xml creation as well as IPS_import xml creation
		 * @throws IOException 
		 * @throws JDOMException 
		 * @throws MatrixException 
		 * @throws Exception
		 */
		public void processXMLs() throws MatrixException, JDOMException, IOException {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processXMLs Start ");
			doSave = saxBuilder.processIPMDocReferenceFile(domObjectIPM,fileRoot,objTag,strPalletTypeValue);
			encFileName = saxBuilder.getfilename();
			fullFilePath = fileRoot.getAbsolutePath() +java.io.File.separator + encFileName;
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processXMLs doSave "+doSave);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processXMLs encFileName "+encFileName);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processXMLs fullFilePath "+fullFilePath);
			createIPMDocAndGenerateIPSimportXML();			
		}
				
		/**
		 * Save TOPS XML -  This method determines selected bus id from the table row
		 * @param strTableRowId
		 * @return
		 */
		public String getSelBusId(String strTableRowId) {
			// check if the selected row id is not the top parent in the tree.
			// Spliting up the Object Id and Rel ID accordingly from the Table Row Id
			if (UIUtil.isNotNullAndNotEmpty(strTableRowId)) 
			{
				StringList relBusIdList = StringUtil.split(strTableRowId,DataConstants.SEPARATOR_PIPE);
				if(relBusIdList.size()==3)
				{
					selBusId = relBusIdList.get(0);
				}else{ 
					selBusId = relBusIdList.get(1);
				}
			}
			return selBusId;
		}
		
		/**
		 * Save TOPS XML -  This method generates xml file name according to sps name and its revision 
		 * @param strStackingPatternName
		 * @param strStackingPatternRevision
		 * @throws MatrixException
		 */
		public void getXmlFileNameInfo(String strStackingPatternName,String strStackingPatternRevision) throws MatrixException {
			// XML File Name setting as Stacking pattern name and Rev
		  	docName = strStackingPatternName+DataConstants.SEPARATOR_UNDERSCORE+strStackingPatternRevision;

			// XML File name formation
			file = DataConstants.SIMPLE_XML_PREFIX+DataConstants.SEPARATOR_UNDERSCORE+docName+DataConstants.CONSTANT_DOT+DataConstants.FILE_FORMAT_XML;
			strPath = context.createWorkspace();
			fileRoot = new File(strPath);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  getXmlFileNameInfo file "+file);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  getXmlFileNameInfo fileRoot "+fileRoot);
		}
		
		/**
		 * Save TOPS XML -  This method defines which parts are allowed in Save TOPS XML structure
		 */
		private void setslPartType() {
			slPartType.add(DataConstants.CONSTANT_MCOP);
			slPartType.add(DataConstants.CONSTANT_MIP);
			slPartType.add(DataConstants.CONSTANT_MCUP);
		}
			
		/**
		 * Save TOPS XML - This method is invoked from pgVPDCallTOPS.jsp 
		 * It Creates SPS if not exists , addRemoveOwnership , and wrapper for xml creation and updation methods
		 * @param context1
		 * @param strTableRowId
		 * @param strTransportUnitId
		 * @return
		 * @throws Exception
		 */
		public Map processSaveTOPSXML(Context context1,String strTableRowId, String strTransportUnitId) throws Exception {
			PRSPContext.set(context1);
			context = context1;
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML START "+context.getUser());
			HashMap<String, Object> returnMap = new HashMap<>();
			try {
				
			domTUPObj = DomainObject.newInstance(context,strTransportUnitId);
			//ALM 45135 Start
			boolean isLockedVal = isLocked(context,domTUPObj);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML domTUPObj.isLocked(context) "+isLockedVal);			
			if(isLockedVal) {
				throw new DesignToolsIntegrationException(errorTUPLockedByAnotherUser.getExceptionCode(),
						errorTUPLockedByAnotherUser.getExceptionMessage()+domTUPObj.getLocker(context)+" "+DataConstants.ALERT_TUP_LOCKED);
			}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML Before locking TUP ");
			try {
			domTUPObj.lock(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML After locking TUP ");
			}catch(Exception e) {
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML Inside catch block of locking TUP "+e.getMessage());
				if(e.getMessage().contains("locked by")) {
					throw new DesignToolsIntegrationException(errorTUPLockedByAnotherUser.getExceptionCode(),
							errorTUPLockedByAnotherUser.getExceptionMessage()+domTUPObj.getLocker(context)+" "+DataConstants.ALERT_TUP_LOCKED);
				}
			}
			//ALM 45135 End
			ContextUtil.startTransaction(context, true);
				
			String strTUPChangeActionID = tup.processChangeManagement(context,strTransportUnitId);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML strTUPChangeActionID "+strTUPChangeActionID);
			//START: DTCLD-425: Get the Pallet Id from TUP
			strPalletTypeValue=domTUPObj.getInfo(context, DataConstants.SELECT_ATTR_PG_PALLETTYPE);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML strPalletTypeValue::"+strPalletTypeValue);
			//END: DTCLD-425
			MapList mlStackingPatternIds = tup.getRelatedSPS(context,domTUPObj);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML mlStackingPatternIds "+mlStackingPatternIds);
					
			//START: Added for DTCLD-794
			BusinessInterfaceList busTUPInterfaces =domTUPObj.getBusinessInterfaces(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML TUP interfaces:::"+busTUPInterfaces.toString());
			
			if(!busTUPInterfaces.toString().contains(DataConstants.INTERFACE_AUTOMATION_USAGE_EXTENSION)){
				String []newArgs = {"TOPS","StartedInEnovia", strTransportUnitId};
				JPO.invoke(context, DataConstants.CONSTANT_JPO_PGDTAUTOMATION_METRIC_TRACKING, null, DataConstants.CONSTANT_METHOD_ADD_USAGE_TRACKING_TODATA, newArgs);
			}
			//END: Added for DTCLD-794
			
			if(null==mlStackingPatternIds || mlStackingPatternIds.isEmpty())
			{	
				//DTCLD - 739 START
				
				domTUP = domTUPObj;
				createProductData(false);
				
				Map mpSPSInfo=new HashMap();
				
				StringList slSPSInfo=new StringList(4);
				slSPSInfo.add(SELECT_ID);
				slSPSInfo.add(SELECT_NAME);
				slSPSInfo.add(SELECT_TYPE);
				slSPSInfo.add(SELECT_REVISION);
				
				mpSPSInfo=domSPS.getInfo(context,slSPSInfo);
				mlStackingPatternIds=new MapList();
				mlStackingPatternIds.add(mpSPSInfo);
				//DTCLD - 739 END
				//START Added for DTCLD-794
				String[] newArgs = new String[] {"TOPS","StartedInEnovia", (String) mpSPSInfo.get(SELECT_ID)};
				JPO.invoke(context, DataConstants.CONSTANT_JPO_PGDTAUTOMATION_METRIC_TRACKING, null, DataConstants.CONSTANT_METHOD_ADD_USAGE_TRACKING_TODATA, newArgs);		
				//END Added for DTCLD-794
				addRemoveOwnership(context,strTransportUnitId);
			}else {
				//START: Added for DTCLD-670 --check if the connected SPS has Manual SPSOrigination. If yes, throw error
				Map mapStackingPattern = (Map)mlStackingPatternIds.get(0);
				String strSPSOrigination = (String)mapStackingPattern.get(DataConstants.SELECT_ATTR_PG_SPS_ORIGINATION);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML SPSOrigination of existing connected SPS:: "+strSPSOrigination);
				
				if(DataConstants.RANGE_VALUE_MANUAL.equals(strSPSOrigination)) {
					throw new DesignToolsIntegrationException(errorManualSPSConnected.getExceptionCode(),
							errorManualSPSConnected.getExceptionMessage());
				}
				//END: Added for DTCLD-670 --check if the connected SPS has Manual SPSOrigination. If yes, throw error
			}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML after createAndConnectSPS mlStackingPatternIds "+mlStackingPatternIds);
			if(mlStackingPatternIds != null && !mlStackingPatternIds.isEmpty())
			{
				Map mapStackingPattern = (Map)mlStackingPatternIds.get(0);
				
				String strStackingPatternId = (String)mapStackingPattern.get(DomainConstants.SELECT_ID);
				
				
				domStackingPatternObj = DomainObject.newInstance(context, strStackingPatternId);
				
				processChangeManagement(context,strStackingPatternId,strTUPChangeActionID);
				buildObjtag();
				getXmlFileNameInfo((String)mapStackingPattern.get(DomainConstants.SELECT_NAME),(String)mapStackingPattern.get(DomainConstants.SELECT_REVISION));
				setslPartType();
				
				// If Table Row bus Id not empty go inside the loop else throw error and abort.
				// This Id can be type of Master Customer or Master Consumer or Master InnerPack
				if (UIUtil.isNotNullAndNotEmpty(getSelBusId(strTableRowId)))
				{
					DomainObject domselObj = DomainObject.newInstance(context,selBusId);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML domselObj "+domselObj);
					// Mapping type names to the defined Strings
					
					partType = domselObj.getInfo(context, DomainConstants.SELECT_TYPE);	
					partType = mapPartTypeToDefinedString(partType);
					
					//XML Tag starts with TransportUnit
					tagsNeeded.add(DataConstants.CONSTANT_TUP);

					//If User selected Part is the Master customer, Master consumer or Master InnerPack parts
					if (slPartType.contains(partType)) 
					{									
						determineTagsNeeded(strTransportUnitId);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML founduplicates "+founduplicates);
						VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML foundObject "+foundObject);
						if (!founduplicates)
						{			
							// If Object is there in loop go ahead and add it in XML.
							if (foundObject)
							{							
								// Getting the IPM Document Object from Stacking Pattern Object through Reference Document Relationship.
								domObjectIPM = getRelatedIPM(domStackingPatternObj);
								processXMLs();	
			
							} else 
							{
								throw new DesignToolsIntegrationException(EnoviaResourceBundle.getProperty(context, DataConstants.CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE, context.getLocale(),"emxEngineeringCentral.Alert.MissingType"));
							}
						} else
						{							
							throw new DesignToolsIntegrationException(EnoviaResourceBundle.getProperty(context, DataConstants.CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE, context.getLocale(),"emxEngineeringCentral.Alert.DuplicateType"));
						}
					} else 
					{
						throw new DesignToolsIntegrationException(EnoviaResourceBundle.getProperty(context, DataConstants.CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE, context.getLocale(),"emxEngineeringCentral.Alert.WrongType"));
					}			
				
				} else 
				{
					throw new DesignToolsIntegrationException(EnoviaResourceBundle.getProperty(context, DataConstants.CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE, context.getLocale(),"emxEngineeringCentral.Alert.EmptySelectedobject"));			
				} 
			}else 
			{	
				throw new DesignToolsIntegrationException(EnoviaResourceBundle.getProperty(context, DataConstants.CONSTANT_EMX_ENGINEERING_CENTRAL_STRINGRESOURCE, context.getLocale(),"emxEngineeringCentral.Alert.NoStackingPattern"));
			}
			}catch (DesignToolsIntegrationException ex){
				if(ContextUtil.isTransactionActive(context))
					ContextUtil.abortTransaction(context);
				throw new DesignToolsIntegrationException(ex.getStrErrorMessage());
			}catch (Exception e){
				if(ContextUtil.isTransactionActive(context))
					ContextUtil.abortTransaction(context);	
				throw new Exception(e.getMessage());
			}	finally {
				
					if(ContextUtil.isTransactionActive(context))
					{
						ContextUtil.commitTransaction(context);
					}
					
					unlockDO(context,domTUPObj);
					
			}
			
			returnMap.put("doSave", doSave);
			returnMap.put("encFileName", encFileName);
			returnMap.put("fullFilePath", fullFilePath);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  processSaveTOPSXML returnMap "+returnMap);
		
			return returnMap;		
		}
		
		/**
		 * Save TOPS XML - This method is wrapper for creation of IPM Doc and generating the IPS import xml 
		 * @throws MatrixException 
		 * @throws IOException 
		 * @throws Exception
		 */
		private void createIPMDocAndGenerateIPSimportXML() throws MatrixException, IOException {
			// If no IPM Documents present, Generate the XML, Create a new IPM Document and checkin the XML to the same.
			boolean hasOne = saxBuilder.getHasOne();
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMDocAndGenerateIPSimportXML domObjectIPM "+domObjectIPM);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMDocAndGenerateIPSimportXML hasOne "+hasOne);
			if(domObjectIPM==null || hasOne) {
				simpleXML.createRootInfoTag(context,docName,objTag,domStackingPatternObj);
				Input input = simpleXML.getinputTag();
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMDocAndGenerateIPSimportXML tagsNeeded "+tagsNeeded);
				if(tagsNeeded.contains(DataConstants.CONSTANT_MCOP)) {
					mcop.processAttributesForXMLTagsCreation(mpPart,input);
				}
				if(tagsNeeded.contains(DataConstants.CONSTANT_MIP)) {
					mip.setNoOfConUnits(mcop.getNoOfConUnits());
					mip.processAttributesForXMLTagsCreation(mpPart,input);
				}
				if(tagsNeeded.contains(DataConstants.CONSTANT_MCUP)) {
					mcup.setNoOfConUnits(mcop.getNoOfConUnits());
					mcup.setNoOfInterUnits(mip.getNoOfInterUnits());		
					mcup.processAttributesForXMLTagsCreation(mpPart,input);
				}
				if(tagsNeeded.contains(DataConstants.CONSTANT_TUP)) {
					tup.setstrPalletTypeValue(strPalletTypeValue);
					tup.processAttributesForXMLTagsCreation(mpPart,input);
				}
				simpleXML.writeContentInXMlFile(fileRoot,file);
				createIPMAndCheckin();
			
				encFileName = simpleXML.getFileName();
				fullFilePath = simpleXML.getfullFilePath();
			}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMDocAndGenerateIPSimportXML encFileName "+encFileName);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMDocAndGenerateIPSimportXML END fullFilePath "+fullFilePath);
		}
		
		/**
		 * Save TOPS XML - This method creates IPM doc and checks in the xml on it
		 * @throws MatrixException
		 */
		public void createIPMAndCheckin() throws MatrixException {
			// Checking whether IPM Document obj exists or not with the same name as Stacking Pattern name, If not so create one.
			String docID = "";
			BusinessObject busDOC =  new BusinessObject(DataConstants.TYPE_PGIPMDOCUMENT, docName, DataConstants.CONSTANT_REVISION_ZERO, DataConstants.VAULT_ESERVICE_PRODUCTION);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMAndCheckin busDOC "+busDOC);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMAndCheckin domObjectIPM "+domObjectIPM);
			if (!busDOC.exists(context) && domObjectIPM==null) 
			{
				busDOC.create(context,DataConstants.POLICY_IPM_DOCUMENT);
				
				String busDOCId = busDOC.getObjectId();
				DomainObject newDOC = DomainObject.newInstance(context,busDOCId);
				newDOC.setAttributeValue(context,DataConstants.ATTR_TITLE,docName);
				// Connect IPM Document with Stacking Pattern Object through Reference Document Relationship.				
				DomainRelationship.connect(context, domStackingPatternObj, DataConstants.REL_REFERENCE_DOCUMENT,newDOC);
		
				// Connect IPM Document with Stacking Pattern Object through Reference Document Relationship.
				busDOC.checkinFile(context, true, true, DataConstants.CONSTANT_LOCALHOST, DomainConstants.FORMAT_GENERIC, file, fileRoot.getAbsolutePath());
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMAndCheckin after checkin ");
				CommonDocument object = (CommonDocument)DomainObject.newInstance(context, CommonDocument.TYPE_DOCUMENTS);
				object.setId(busDOCId);
				Map attribMap = new HashMap();
				attribMap.put(DomainConstants.ATTRIBUTE_CHECKIN_REASON, sCheckInReasonValue);
				object.createVersion(context, file, file, attribMap);
				doSave = true;
			}
			else 
			{
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  createIPMAndCheckin else loop ");
				docID = busDOC.getObjectId(context);
				DomainObject dOBJ = DomainObject.newInstance(context,docID);
				Map mpFileRelatedArguments = new HashMap();
				mpFileRelatedArguments.put("strTypeOfXML",DataConstants.CONSTANT_IPS_IMPORT_XML);
				mpFileRelatedArguments.put("filename",file);
				mpFileRelatedArguments.put("fileRoot",fileRoot);
				mpFileRelatedArguments.put("fileformat","");
				
				doSave  = reviseVersionAndGrantRevokeAccess(context,dOBJ,mpFileRelatedArguments ,domObjectIPM,domTUPObj);		
			}
		}
		
		/**Added for DTCLD-754: Copy IPMDocument and XML files on Copy SPS
		 * @param context
		 * @param sourceSPS
		 * @param clonedSPS
		 * @throws Exception
		 */
		public void copyObject(DomainObject sourceSPS, DomainObject clonedSPS) throws Exception {
			context=PRSPContext.get();
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> START of StackingPatternDocument copyObject ");
			DomainObject sourceIPMDoc = getRelatedIPM(sourceSPS);
			
			//Update the name of the clonedIPMDocument
			StringList slSelects = new StringList();
			slSelects.add(DomainConstants.SELECT_NAME);
			slSelects.add(DomainConstants.SELECT_REVISION);
			
			Map mpInfo = clonedSPS.getInfo(context, slSelects);
			String strCloneSPSName = (String) mpInfo.get(DomainConstants.SELECT_NAME);
			String strCloneSPSRev = (String) mpInfo.get(DomainConstants.SELECT_REVISION);
			String strClonedIPMName = new StringBuilder().append(strCloneSPSName).append(DataConstants.SEPARATOR_UNDERSCORE).append(strCloneSPSRev).toString();
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> copyObject strClonedIPMName:: "+strClonedIPMName);
			
			//Clone the IPMDocument 
			BusinessObject boClonedIPMDoc = sourceIPMDoc.cloneObject(context, //context
																strClonedIPMName, //Name of the Object
																DataConstants.CONSTANT_REVISION_ZERO, //Revision of the Object
																DataConstants.VAULT_ESERVICE_PRODUCTION, //Vault of the Object
																false); //Copy File
			
			if (boClonedIPMDoc.exists(context)) {
				DomainObject clonedIPMDoc = DomainObject.newInstance(context,boClonedIPMDoc);
				//Update the title of the IPMDoc
				clonedIPMDoc.setAttributeValue(context, DomainConstants.ATTRIBUTE_TITLE, strClonedIPMName);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> copyObject TITLE "+clonedIPMDoc.getInfo(context, DomainConstants.SELECT_ATTRIBUTE_TITLE));
				
				String sClonedIPMId = clonedIPMDoc.getInfo(context, DomainConstants.SELECT_ID);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> copyObject clonedIPMDoc id:: "+sClonedIPMId);
				
				clonedSPS.addToObject(context, 
									new RelationshipType(DataConstants.REL_REFERENCE_DOCUMENT), 
									sClonedIPMId);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> copyObject  clonedIPMDoc connected to clonedSPS ");
				
				//Checkout the files checked in on the sourceIPMDoc
				CommonDocumentHandler docObject = new CommonDocumentHandler();
				Map mpFileInfo = docObject.checkoutFiles(context, sourceIPMDoc);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  copyObject files checked out mpFileInfo:: " + mpFileInfo);
				
				//Get the TUP connected to the clonedSPS
				DomainObject connectedTUPObj = getRelatedTUP(clonedSPS);
				String strclonedTUPId = connectedTUPObj.getId(context);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  copyObject strTUPId:: " + strclonedTUPId);
				
				//Method to build string for extAddlInof tag
				domTUPObj = connectedTUPObj;
				buildObjtag();
				determineTagsNeeded(strclonedTUPId);
				
				Map mpCheckinFiles = saxBuilder.updateFileNameAndTagsOnCopy (mpFileInfo, strClonedIPMName, objTag);
				 
				//Checkin files on clonedIPM
				docObject.checkinFiles(context, clonedIPMDoc, mpCheckinFiles);
				
				//Create versions
				String sFileName = DomainConstants.EMPTY_STRING;
				Map<String, String> attribMap;
				int noOfFiles = (int) mpCheckinFiles.get("noOfFiles");
				setId(sClonedIPMId);
				for(int i=1; i<=noOfFiles; i++) {
					sFileName = (String) mpCheckinFiles.get(new StringBuilder().append("filename_").append(i).toString());
					attribMap = new HashMap<>(1);
					attribMap.put(CommonDocument.ATTRIBUTE_TITLE, sFileName);
					VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  copyObject attribMap::: "+attribMap);
					createVersion(context, sFileName, sFileName, attribMap);
				}
				
				context.deleteWorkspace();
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>  copyObject workspace deleted ");
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>> END of StackingPatternDocument  copyObject ");
			}
		}
	}


	@Override
	public StringList getValidParents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringList getValidChild() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidParent(String strPartType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidChild(String strPartType) {
		// TODO Auto-generated method stub
		return false;
	}

}
