package com.pg.widgets.structuredats;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import matrix.db.Context;
import matrix.util.StringList;
import matrix.util.MatrixException;
import matrix.util.Pattern;

import matrix.db.BusinessObject;
import matrix.db.Context;

import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MqlUtil;

import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletResponse;

import com.matrixone.apps.engineering.Part;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.cpn.CPNCommon;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeOrder;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import org.json.JSONObject;

public class PGStructuredATSUtil 
{
static final String STRING_OBJECT = "ObjectId";
private static final String STATUS_ERROR = "error";
static final String EXCEPTION_MESSAGE  = "Exception in PGStructuredATSUtil";
static final String TEMPLATE_NAME  = "Fast track";
static final String CHANGE_ORDER  = "CreateNew";
static final String STRING_MESSAGE = "message";
static final String STRING_OK = "OK";
static final String SATS_REVISION = "001";
private static final Logger logger= Logger.getLogger(PGStructuredATSUtil.class.getName()); 
public static final String TYPE_RAWMATERIAL = PropertyUtil.getSchemaProperty("type_RawMaterial");
public static final String REL_PG_ATSCONTEXT  = PropertyUtil.getSchemaProperty("relationship_pgATSContext");
public static final String ATTRIBUTE_PGSTRUCTUREATSACTION = PropertyUtil.getSchemaProperty("attribute_pgStructuredATSAction");
public static final String REL_AUTHORISEDTEMPORARYSPECIFICATION = PropertyUtil.getSchemaProperty("relationship_AuthorizedTemporarySpecification");
public static final String TYPE_ASSEMBLED_PRODUCT_PART = PropertyUtil.getSchemaProperty("type_pgAssembledProductPart");
public static final String TYPE_PG_STRUCTURED_ATS = PropertyUtil.getSchemaProperty("type_pgStructuredATS");
public static final String POLICY_PG_STRUCTURED_ATS = PropertyUtil.getSchemaProperty("policy_pgStructuredATS");
public static final String TYPE_PG_RAW_MATERIAL = PropertyUtil.getSchemaProperty("type_pgRawMaterial");
public static final String RELATIONSHIP_PGPDTEMPLATES_TO_PGPLISEGMENT = PropertyUtil.getSchemaProperty("relationship_pgPDTemplatestopgPLISegment");
public static final String REL_TEMPLATE = PropertyUtil.getSchemaProperty("relationship_Template");
public static final String REL_PG_PRIMARY_ORGANIZATION = PropertyUtil.getSchemaProperty("relationship_pgPrimaryOrganization");
public static final String REL_PROTECTED_ITEM = PropertyUtil.getSchemaProperty("relationship_ProtectedItem");
public static final String TYPE_PG_PLI_SEGMENT = PropertyUtil.getSchemaProperty("type_pgPLISegment");
public static final String TYPE_TEMPLATE = PropertyUtil.getSchemaProperty("type_Template");	
public static final String TYPE_PG_PLI_ORGANIZATION_CHANGE_MANAGEMENT = PropertyUtil.getSchemaProperty("type_pgPLIOrganizationChangeManagement");
public static final String TYPE_IP_CONTROL_CLASS = PropertyUtil.getSchemaProperty("type_IPControlClass");
public static final String TYPE_PG_AUTHORIZED_CONFIGURATION_STANDARD = PropertyUtil.getSchemaProperty("type_pgAuthorizedConfigurationStandard");
public static final String REL_PG_ATS_OPERATION = PropertyUtil.getSchemaProperty("relationship_pgATSOperation");
public static final String REL_EBOM = PropertyUtil.getSchemaProperty("relationship_EBOM");
public static final String REL_PG_ATS_CONTEXT = PropertyUtil.getSchemaProperty("relationship_pgATSContext");
// public static final String TYPE_CHANGE_TEMPLATE = PropertyUtil.getSchemaProperty("type_ChangeTemplate");
public static final String TYPE_PG_AUTHORIZED_TEMPORARY_SPECIFICATION = "type_pgAuthorizedTemporarySpecification";
public static final String REVISION_NUMBER_GENERATOR = "A";
private static final String DEFAULT_COID = "CreateNew";
String strAttributeMapping ="Expiration Date:attribute_ExpirationDate,Relationship Restriction:attribute_pgMaterialRestriction,Relationship Restriction Comment:attribute_pgMaterialRestrictionComment,Reason for Change:attribute_ReasonforChange,Local Description:attribute_pgLocalDescription";
//String strAttributeMapping = EnoviaResourceBundle.getProperty(context, "ExportControl","pgExportControl.IPClassification.Restricted", languageStr); // Read above from page file
public static final String REL_CHANGE_INSTANCE = PropertyUtil.getSchemaProperty("relationship_ChangeInstance");
private static final String SELECT_CHANGE_TEMPLATE_NAME = "to["+REL_CHANGE_INSTANCE+"].from.name";
private static final String SELECT_CHANGE_TEMPLATE_ID = "to["+REL_CHANGE_INSTANCE+"].from.id";
public static final String REL_REFERENCE_DOCUMENT = PropertyUtil.getSchemaProperty("relationship_ReferenceDocument");
String strLanguage = "en";
	/**This Method inception to revise the Structured ATS object and connect with required Objects.
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception  
	 */
	public String reviseStructuredAuthorizedTemporaryStandard(Context context, String strObjectId) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strRevisedSATSId = null;
		String strATSOperationRelId = null;
		String strAPPObjectId = null;
		StringList slAPPConnectedRAWMaterialRelIds = new StringList(); //APP connected to Structured ATS
		StringList slRAWMaterialConnectedObjectId = new StringList(); // RMP connected to Structured ATS
		StringList slRELIDsRAWMaterialToRevisedSATS = new StringList(); 
		try {
			strRevisedSATSId = reviseSATSObject(context,strObjectId); // clones the SATS object and returns object Id
			//createAndConnectCO (context, strRevisedSATSId, null); // create and Connect CO to the Revised SATS
			strAPPObjectId = getConnectedAPPId (context,strObjectId); // get Object Id for connected APP
			slRAWMaterialConnectedObjectId = getConnectedRAWMaterialToSATS (context,strObjectId); // get connected RAW Material to SATS
			slAPPConnectedRAWMaterialRelIds = getConnectedRawMaterialToAPPRelIds (context,strAPPObjectId); // get Rel IDs of the RAW materials connected with APP
			
			connectRAWMaterialWithRevisedSATSObject(context, strRevisedSATSId,slRAWMaterialConnectedObjectId); // connect Raw Materials connected with Original Structured ATS to Revised SATS object
			
			getATSContextToRelId(context,slRAWMaterialConnectedObjectId);
			jsonReturnObj.add(STRING_OBJECT, strRevisedSATSId);
		}catch(Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return jsonReturnObj.build().toString();
	}
	/**This Method revises the Structured ATS object and sets the Originator
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public String reviseSATSObject(Context context, String strObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		HashMap programMap = new HashMap();
		String clonedObjectId = null;
		BusinessObject boRevisedSATS = null;
		StringList slSelectables = new StringList(3);
		slSelectables.add("type");
		slSelectables.add("policy");
		try
		{
			DomainObject dObjectSATS = DomainObject.newInstance(context, strObjectId);
			Map mpSATS = dObjectSATS.getInfo(context, slSelectables);
			
			boRevisedSATS = dObjectSATS.reviseObject(context, true); // revising Structured ATS with file
			boRevisedSATS.setAttributeValue(context, DomainConstants.ATTRIBUTE_ORIGINATOR, context.getUser());
			//connectObjectsWithRevisedSATS(context,strObjectId,boRevisedSATS.getObjectId(context));
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return DomainObject.newInstance(context,boRevisedSATS).getObjectId(context);
	}
	/**This Method connects APP with revised Structured ATS
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public void connectAPPWithRevisedSATSObject(Context context, String strRevisedSATSId , String strAPPObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObjRevisedSATS	 = null;
		DomainObject dObjAPPObject	 = null;
				
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try
		{
			//connectObjects(context,strRevisedSATSId, strAPPObjectId,REL_AUTHORISEDTEMPORARYSPECIFICATION);
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
	}
	/**This Method connects APP with revised SATS
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public void connectRAWMaterialWithRevisedSATSObject(Context context, String strRevisedSATSId , StringList slRAWMaterialConnectedObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strATSOperationRElId = null;
		try{
			if (null != slRAWMaterialConnectedObjectId && slRAWMaterialConnectedObjectId.size()>0 )
			{
				for (String strRawMaterialId : slRAWMaterialConnectedObjectId)
				{
					connectObjects(context,strRevisedSATSId, strRawMaterialId,REL_PG_ATS_OPERATION);
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
	}
	
	
	/**This Method gets REL id of the Raw Material and APP connection
	 * @param context
	 * @param object Id for APP
	 * @return
	 * @throws Exception 
	 */
	public StringList getRELIdsRawMaterialToRevisedSATS(Context context, String strRevisedSATSId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObjRevisedSATS = null;
		Map mRAWMaterialData = null;
		String strRELId = null;
		StringList slRAWMaterialRELIds = new StringList();
		
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try
		{
			dObjRevisedSATS = DomainObject.newInstance(context,strRevisedSATSId);
			MapList mlRAWMaterialConnToAPP = dObjRevisedSATS.getRelatedObjects(context,REL_PG_ATS_OPERATION,TYPE_PG_RAW_MATERIAL,selectStmts,relSelectsList,
false,true,(short)1,null,"");
			if (null != mlRAWMaterialConnToAPP)
			{
				Iterator itrRawMaterial = mlRAWMaterialConnToAPP.iterator();
				while (itrRawMaterial.hasNext())
				{
					mRAWMaterialData = (Map)itrRawMaterial.next();
					if (null != mRAWMaterialData && mRAWMaterialData.size()>0)
					{
						strRELId = (String)mRAWMaterialData.get(DomainConstants.SELECT_RELATIONSHIP_ID);
						if(!slRAWMaterialRELIds.contains(strRELId))
							slRAWMaterialRELIds.add(strRELId);						
					}
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
		return slRAWMaterialRELIds;
	}
	/**This Method gets REL id of the Raw Material and APP connection
	 * @param context
	 * @param object Id for APP
	 * @return
	 * @throws Exception 
	 */
	public void connectObjectsWithRevisedSATS(Context context,String strObjectId,String strRevisedObjId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObject = null;
		String strConnectedObjId = null;
		String strConnectedObjType = null;
		String strRelationshipName = null;
		MapList mlConnectedSATSObject = null;
		Map mConnectedSATSObject = null;
		
		StringList objectSelects = new StringList(2);
		objectSelects.add(DomainConstants.SELECT_ID);				
		objectSelects.add(DomainConstants.SELECT_TYPE);	
		
		StringList relSelects = new StringList(2);
		relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);				
		relSelects.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
		try 
		{
			dObject = DomainObject.newInstance(context,strObjectId);
			mlConnectedSATSObject = dObject.getRelatedObjects(context,			//context
									DomainConstants.QUERY_WILDCARD,				//relationshipPattern
									DomainConstants.QUERY_WILDCARD,				//typePattern
									objectSelects,								//objectSelects
									relSelects ,								//relationshipSelects,
									true,										//getTo,
									true,										//getFrom,
									(short)1,									//recurseToLevel,
									DomainConstants.EMPTY_STRING,				//objectWhere,
									DomainConstants.EMPTY_STRING,				//relationshipWhere
									0);
									
			if (null != mlConnectedSATSObject && mlConnectedSATSObject.size()>0)
			{
				Iterator itrConnectedSATSObject = mlConnectedSATSObject.iterator();
				while(itrConnectedSATSObject.hasNext())
				{
					mConnectedSATSObject = (Map) itrConnectedSATSObject.next();
					strConnectedObjId = (String) mConnectedSATSObject.get(DomainConstants.SELECT_ID);
					strConnectedObjType = (String) mConnectedSATSObject.get(DomainConstants.SELECT_TYPE);
					strRelationshipName = (String) mConnectedSATSObject.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
					if ( UIUtil.isNotNullAndNotEmpty(strConnectedObjId) && UIUtil.isNotNullAndNotEmpty(strRelationshipName) && !strRelationshipName.equals(REL_PG_ATS_OPERATION ))
					{
						if ( strRelationshipName.equals(REL_REFERENCE_DOCUMENT) || strRelationshipName.equals(DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY) )
						{
							DomainRelationship.connect(context,DomainObject.newInstance(
									context,strConnectedObjId) , strRelationshipName,  DomainObject.newInstance(context,strRevisedObjId));
						}else
						{
							DomainRelationship.connect(context, DomainObject.newInstance(context,strRevisedObjId), strRelationshipName, DomainObject.newInstance(
									context,strConnectedObjId));	
						}
					}
				}
			}
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
	}
	/**This method connects the CO with revised Structured SATS
	 * @param context
	 * @param object Id for Revised SATS
	 * @return
	 * @throws Exception 
	 */
	public void createAndConnectCO (Context context, String strObjectId, Map<String,Object> mpRequestMap) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strChangeTemplate = null;
		String strChangeTemplateId = null;
		String strCOId = null;
		try {
			if (null != mpRequestMap)
			{
				strChangeTemplateId = (String) mpRequestMap.get("changeTemplate");
				if (UIUtil.isNotNullAndNotEmpty(strChangeTemplateId))
				{
					strCOId = (String) mpRequestMap.get("CO");
					if (UIUtil.isNullOrEmpty(strCOId))
						strCOId = DEFAULT_COID;
					if(strCOId.equalsIgnoreCase("CreateNew")) {
						strCOId =   (new ChangeOrder()).create(context, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, context.getUser(), strChangeTemplateId, null);
						ChangeOrder changeOrder = new ChangeOrder(strCOId);
					
						changeOrder.connectAffectedItems(context,  new StringList(strObjectId));
					}
				}
			}			
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
	}
	public String getRDOId (Context context, String strRevisedSATS) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		StringList objDetails = new StringList(1);
		objDetails.add(DomainConstants.SELECT_ORGANIZATION);
		Map mObjDetails = null;
		DomainObject doPD  = null;
		String strRDOID =null;
		String strOrgNameOfProductData =null; 
		try {
			doPD = DomainObject.newInstance(context, strRevisedSATS);
			mObjDetails = doPD.getInfo(context, objDetails);
			strOrgNameOfProductData = (String) mObjDetails.get(DomainConstants.SELECT_ORGANIZATION);
			strRDOID = CPNCommon.getIDForName(context, DomainConstants.TYPE_ORGANIZATION,strOrgNameOfProductData);
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return strRDOID;
	}
	// public String getChangeTemplateId (Context context, String strTemplateName) throws Exception
	// {
		// JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		// String strRDOID =null;
		// String strChangeTemplateId = null;
		// String strOrgNameOfProductData =null; 
		// StringList objDetails = new StringList(1);
		// objDetails.add(DomainConstants.SELECT_NAME);
		// objDetails.add(DomainConstants.SELECT_ID);
		// Map mObjDetails = null;
		// DomainObject doPD  = null;
		// try {			
			// MapList mlChangeTemplate = DomainObject.findObjects(context, TYPE_CHANGE_TEMPLATE, strTemplateName, "*", "*", "*", "",false,objDetails);
			// Map mTemplateId = (Map)mlChangeTemplate.get(0);
	        // strChangeTemplateId = (String)mTemplateId.get(DomainConstants.SELECT_ID);
		// } catch (Exception excep)
		// {
			// logger.log(Level.SEVERE, EXCEPTION_MESSAGE, excep);
			// jsonReturnObj.add(STATUS_ERROR,excep.getMessage());
		// }
		// return strChangeTemplateId;
	// }
	/**This is Util Method to connects objects
	 * @param context
	 * @param strFromObjectId
	 * @param strToObjectId
	 * @param Relationship Name
	 * @return
	 * @throws Exception 
	 */
	public DomainRelationship connectObjects(Context context, String strFromObjID, String strToObjID, String strRelationshipName) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObjFromObject	= null;
		DomainObject dObjToObject	= null;
		DomainRelationship domRelationship = null;
		try 
		{   if (UIUtil.isNotNullAndNotEmpty(strFromObjID) && UIUtil.isNotNullAndNotEmpty(strToObjID) && UIUtil.isNotNullAndNotEmpty(strRelationshipName))
			{
				dObjFromObject	= DomainObject.newInstance(context, strFromObjID);
				dObjToObject	= DomainObject.newInstance(context, strToObjID);
				domRelationship = DomainRelationship.connect(context, dObjFromObject, strRelationshipName, dObjToObject);
			}
		} catch (Exception excep)
		{
			excep.getMessage();
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
		return domRelationship;
	}
	/**This Method connects objects to SATS
	 * @param context
	 * @param strObjectId
	 * @param Relationship Name
	 * @param strToObjectId
	 * @return
	 * @throws Exception 
	 */
	public void connectObjectsToSATS(Context context,String strSATSObjectId,String strIsFrom,String strRelationshipName, String strToBeConnID) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		StringList relSelects=new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		StringList busSelects=new StringList(DomainConstants.SELECT_ID);
		Map relatedObjects = null;
		String strObjId = null;
		try 
		{   if (UIUtil.isNotNullAndNotEmpty(strSATSObjectId) && UIUtil.isNotNullAndNotEmpty(strRelationshipName))
			{
				DomainObject domobj	 = DomainObject.newInstance(context,strSATSObjectId);
				if (UIUtil.isNotNullAndNotEmpty(strIsFrom) && strIsFrom.equalsIgnoreCase("false"))
				{
					relatedObjects=domobj.getRelatedObject(context,strRelationshipName,true,busSelects,relSelects);
					if (null!=relatedObjects)
						strObjId=(String)relatedObjects.get(DomainConstants.SELECT_ID);	
				} else 
				{
					relatedObjects=domobj.getRelatedObject(context,strRelationshipName,false,busSelects,relSelects);
					if (null!=relatedObjects)
						strObjId=(String)relatedObjects.get(DomainConstants.SELECT_ID);
				}
				if(relatedObjects == null || !strObjId.equalsIgnoreCase(strToBeConnID))
				{				
					connectObjects(context,strSATSObjectId,strToBeConnID,strRelationshipName);
				}
			}
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
	}
	/**This Method gets REL ID between ATS Context and EBOM
	 * @param context
	 * @param strObjectId
	 * @return void
	 * @throws Exception 
	 */
	public void getATSContextToRelId(Context context,StringList slRAWMaterialConnectedObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObject = null;
		String strATSOperationRelId = null;
		String strRevATSOperationRelId = null;
		String strEBOMRelId = null;
		MapList mlATSContextRelIdDetails =null;
		Map mATSOperationRelIdDetails =null;
		Map mRevATSOperationRelIdDetails =null;
		Iterator itrATSContextRelIdDetails = null;
			
		StringList objectSelects = new StringList(1);
		objectSelects.add(DomainConstants.SELECT_ID);				
		
		StringList relSelects = new StringList(2);
		relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);				
		relSelects.add(DomainConstants.SELECT_RELATIONSHIP_NAME);
		
		Pattern relPattern = new Pattern(REL_PG_ATS_OPERATION);
		try 
		{
			if (null != slRAWMaterialConnectedObjectId && slRAWMaterialConnectedObjectId.size()>0 )
			{
				for (String strRawMaterialId : slRAWMaterialConnectedObjectId)
				{
					dObject = DomainObject.newInstance(context,strRawMaterialId);
					mlATSContextRelIdDetails = dObject.getRelatedObjects(context,				//context
											relPattern.getPattern(),					//relationshipPattern
											DomainConstants.QUERY_WILDCARD,				//typePattern
											objectSelects,								//objectSelects
											relSelects ,								//relationshipSelects,
											true,										//getTo,
											true,										//getFrom,
											(short)1,									//recurseToLevel,
											DomainConstants.EMPTY_STRING,				//objectWhere,
											DomainConstants.EMPTY_STRING,				//relationshipWhere
											0);						
					if (null !=mlATSContextRelIdDetails && mlATSContextRelIdDetails.size()>0)
					{
						itrATSContextRelIdDetails = mlATSContextRelIdDetails.iterator();
						while (itrATSContextRelIdDetails.hasNext())
						{
							mATSOperationRelIdDetails = (Map)itrATSContextRelIdDetails.next();
							strATSOperationRelId = (String) mATSOperationRelIdDetails.get(DomainConstants.SELECT_RELATIONSHIP_ID);
							strEBOMRelId = getEBOMRelId(context,strATSOperationRelId);
							if (UIUtil.isNullOrEmpty(strEBOMRelId)) // EBOM Id is null, it means SATS revised object is not connected
							{
								mATSOperationRelIdDetails = (Map)itrATSContextRelIdDetails.next();
								strATSOperationRelId = (String) mATSOperationRelIdDetails.get(DomainConstants.SELECT_RELATIONSHIP_ID);
								strEBOMRelId = getEBOMRelId(context,strATSOperationRelId);
							} else {
								 mATSOperationRelIdDetails = (Map)itrATSContextRelIdDetails.next();
								 strATSOperationRelId = (String) mATSOperationRelIdDetails.get(DomainConstants.SELECT_RELATIONSHIP_ID);
							}
							MqlUtil.mqlCommand(context, "add connection $1 fromrel $2 torel $3", REL_PG_ATS_CONTEXT, strATSOperationRelId,strEBOMRelId);
						}
							
					}
				}
			}
		}			
		catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
	}
	/**This Method gets EBOM Rel Id
	 * @param context
	 * @param strObjectId
	 * @return String
	 * @throws Exception 
	 */
	public String getEBOMRelId(Context context, String strATSOperationRelId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		Hashtable htEBOMReld =null;
		String strEBOMRelId =null;
		StringList slRelEBOMId = new StringList();
		DomainRelationship domATSOperationRelId = null;
		try 
		{
			domATSOperationRelId = DomainRelationship.newInstance(context, strATSOperationRelId); // getting ATS Operation Rel id
							
			htEBOMReld = (Hashtable) domATSOperationRelId.getRelationshipData(context, new StringList("frommid["+REL_PG_ATS_CONTEXT+"].torel.id"));
			slRelEBOMId = (StringList)htEBOMReld.get("frommid["+REL_PG_ATS_CONTEXT+"].torel.id");
			strEBOMRelId = (String)slRelEBOMId.get(0); // getting EBOM rel ID the 'TO' side of ATS context relationship
			
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return strEBOMRelId;
	}
	/**This Method clones the SATS object and relationships.
	 * @param context
	 * @param strObjectId
	 * @param strObjectSel
	 * @return
	 * @throws Exception 
	 * @throws Exception 
	 */
	public String copyStructuredATSWithBOM(Context context, Map mpRequestMap) throws Exception 
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		String strClonedSATSId = null;
		String strATSOperationRelId = null;
		String strAPPObjectId = null;
		StringList slAPPConnectedRAWMaterialRelIds = new StringList();
		StringList slRAWMaterialConnectedObjectId = new StringList();
		Map mpSelectableMap= new HashMap();
		try {
			String strObjectId = (String) mpRequestMap.get(PGStructuredATSConstants.STRING_OBJ);
			// Clone the Part using objectId
			strClonedSATSId = cloneSATSObject(context,strObjectId); // get the object Id for Cloned Object
			mpSelectableMap.put(DomainConstants.SELECT_ID, strClonedSATSId);
			mpSelectableMap.put(PGStructuredATSConstants.KEY_OBJECT_SELECTS, mpRequestMap.get("ObjectSelects").toString());
			Map returnMap = getATSInformationToCopy( context,  mpSelectableMap);
			jsonObjInfo=getJsonObjectFromMap(returnMap);
			jsonArrObjInfo.add(jsonObjInfo);
			jsonReturnObj.add(PGStructuredATSConstants.KEY_DATA, jsonArrObjInfo);
		}
		catch(Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return jsonReturnObj.build().toString();
	}
	/**This Method clones the SATS object
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public String cloneSATSObject(Context context, String strObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		 HashMap programMap = new HashMap();
		 String clonedObjectId = null;
		 String autoName = null;
		try
		{
			autoName = DomainObject.getAutoGeneratedName(context,
					PGStructuredATSConstants.TYPE_PG_AUTHORIZED_TEMPORARY_SPECIFICATION,
					PGStructuredATSConstants.REVISION_NUMBER_GENERATOR);
			DomainObject dObjectSATS = DomainObject.newInstance(context, strObjectId);
			clonedObjectId = dObjectSATS.cloneObject(context, autoName, PGStructuredATSConstants.SATS_REVISION, context.getVault().getName(), true).getObjectId();

			connectObjectsWithRevisedSATS(context,strObjectId,clonedObjectId);			
		} catch (FrameworkException excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return clonedObjectId;
	}
	/**This Method clones the SATS object
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public String getConnectedAPPId(Context context, String strObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strAPPObjectId = null;
		DomainObject dObjSATS = null;
		Map mAPPData = null;
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try
		{
			dObjSATS = DomainObject.newInstance(context,strObjectId);
			MapList mlAPPData = dObjSATS.getRelatedObjects(context,REL_AUTHORISEDTEMPORARYSPECIFICATION,TYPE_ASSEMBLED_PRODUCT_PART,selectStmts,relSelectsList,true,false,(short)1,null,"");
			if (null != mlAPPData)
			{
				Iterator itrAPP = mlAPPData.iterator();
				while (itrAPP.hasNext())
				{
					mAPPData = (Map)itrAPP.next();
					if (null != mAPPData && mAPPData.size()>0)
					{
						strAPPObjectId = (String)mAPPData.get (DomainConstants.SELECT_ID);	
					}
				}
			}
		} catch (FrameworkException excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}
		return strAPPObjectId;
	}
/**This Method gets the Raw Material connected to APP
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public StringList getConnectedRawMaterialToAPPRelIds(Context context, String strAPPObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObjAPP = null;
		Map mRAWMaterialData = null;
		String strRAWMaterialId = null;
		StringList slRAWMaterialIds = new StringList();
		
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try
		{
			dObjAPP = DomainObject.newInstance(context,strAPPObjectId);
			MapList mlRAWMaterialConnToAPP = dObjAPP.getRelatedObjects(context,"EBOM","*",selectStmts,relSelectsList,true,false,(short)1,null,"");
			if (null != mlRAWMaterialConnToAPP)
			{
				Iterator itrRawMaterial = mlRAWMaterialConnToAPP.iterator();
				while (itrRawMaterial.hasNext())
				{
					mRAWMaterialData = (Map)itrRawMaterial.next();
					if (null != mRAWMaterialData && mRAWMaterialData.size()>0)
					{
						strRAWMaterialId = (String)mRAWMaterialData.get(DomainConstants.SELECT_RELATIONSHIP_ID);
						if(!slRAWMaterialIds.contains(strRAWMaterialId))
							slRAWMaterialIds.add(strRAWMaterialId);						
					}
				}
			}
		} catch (FrameworkException excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
		return slRAWMaterialIds;
	}
	/**This Method connects APP with cloned SATS
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public StringList connectAPPWithClonedSATSObject(Context context, String strClonedSATSId , String strAPPObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObjCloneSATS	 = null;
		DomainObject dObjAPPObject	 = null;
		StringList slRAWMaterialIds = new StringList();
		
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try
		{
			dObjCloneSATS	= DomainObject.newInstance(context, strClonedSATSId);
			dObjAPPObject	= DomainObject.newInstance(context, strAPPObjectId);
			DomainRelationship.connect(context, dObjCloneSATS, REL_AUTHORISEDTEMPORARYSPECIFICATION, dObjAPPObject);
		} catch (FrameworkException excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
		return slRAWMaterialIds;
	}
	/**This Method gets the Raw Material connected to SATS
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public StringList getConnectedRAWMaterialToSATS(Context context, String strObjectId) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObjSATS = null;
		Map mRAWMaterialData = null;
		String strRAWMaterialId = null;
		StringList slRAWMaterialIds = new StringList();
		
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		try
		{
			dObjSATS = DomainObject.newInstance(context,strObjectId);
			MapList mlRAWMaterialConnToSATS = dObjSATS.getRelatedObjects(context,"ATS Operation",TYPE_RAWMATERIAL,selectStmts,relSelectsList,true,false,(short)1,null,"");
			if (null != mlRAWMaterialConnToSATS)
			{
				Iterator itrRawMaterial = mlRAWMaterialConnToSATS.iterator();
				while (itrRawMaterial.hasNext())
				{
					mRAWMaterialData = (Map)itrRawMaterial.next();
					if (null != mRAWMaterialData && mRAWMaterialData.size()>0)
					{
						strRAWMaterialId = (String)mRAWMaterialData.get(DomainConstants.SELECT_RELATIONSHIP_ID);
						if(!slRAWMaterialIds.contains(strRAWMaterialId))
							slRAWMaterialIds.add(strRAWMaterialId);						
					}
				}
			}
		} catch (FrameworkException excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
		return slRAWMaterialIds;
	}
	/**This Method connects Raw Material connected with Original SATS with cloned SATS
	 * @param context
	 * @param strObjectId
	 * @return
	 * @throws Exception 
	 */
	public StringList connectRAWMaterialWithClonedSATSObject(Context context, String strClonedSATSId , StringList slAPPConnectedRAWMaterialRelIds) throws Exception
	{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		DomainObject dObjClonedSATS	 = null;
		DomainObject dObjAPPObject	 = null;
		StringList slRAWMaterialIds = new StringList();
		
		StringList selectStmts = new StringList(2);
		selectStmts.addElement(DomainConstants.SELECT_ID);
		selectStmts.addElement(DomainConstants.SELECT_NAME);
		StringList relSelectsList = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
		int iSize =0 ;
		try
		{
			iSize = slAPPConnectedRAWMaterialRelIds.size();
			dObjClonedSATS	= DomainObject.newInstance(context, strClonedSATSId);
			if (null!=slAPPConnectedRAWMaterialRelIds && iSize>0)
			{
				for (int iIndex=0; iIndex<iSize;iIndex++)
				{
					dObjAPPObject	= DomainObject.newInstance(context, slAPPConnectedRAWMaterialRelIds.get(iIndex));
					DomainRelationship.connect(context, dObjClonedSATS, REL_AUTHORISEDTEMPORARYSPECIFICATION, dObjAPPObject);
				}
				
			}
		} catch (FrameworkException excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
		return slRAWMaterialIds;
	}
		
	/**
	 * Method to connect ATSOperation to target rel with ATSContext relationship
	 * @param context
	 * @param ATSID , sToRelid , Targetid ,sAction
	 * @return
	 * @throws Exception
	 */	
	public String connectATSCtxRelations(Context context,String relpgATSOperationId ,String sToRelid,String sAction,Map attributeMap) throws Exception
	{
		 	JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
			String sATSContextId = null;
			DomainRelationship dompgATSContextId = new DomainRelationship();
			try {
					ContextUtil.startTransaction(context, true);	
					if (!UIUtil.isNullOrEmpty(sToRelid) && !UIUtil.isNullOrEmpty(relpgATSOperationId) && !UIUtil.isNullOrEmpty(sAction))
					{
						//connect rel ATSContext in between BOM rel and ATS Operation rel 
						sATSContextId = MqlUtil.mqlCommand(context, "add connection "+PGStructuredATSConstants.REL_PG_ATSCONTEXT+" torel "+sToRelid+" fromrel "+relpgATSOperationId+" select id dump |", true);
						
						attributeMap.put(PGStructuredATSConstants.ATTRIBUTE_PGSTRUCTUREATSACTION, sAction); 
						
						dompgATSContextId = new DomainRelationship(sATSContextId);
						//update Rel attribute value
						dompgATSContextId.setAttributeValues(context, attributeMap);
					}
				ContextUtil.commitTransaction(context);
			}
			catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
				return jsonReturnObj.build().toString();	
			}
			return dompgATSContextId.toString();
	}
	
	/**
	 * Method to connect SATS to Targetid  with ATSOperation relationship
	 * @param context
	 * @param ATSID , sToRelid , Targetid 
	 * @return
	 * @throws Exception
	 */	public String connectATSOpsRelations(Context context,String sATSID,String sTargetid) throws Exception
	{
		 JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		 DomainRelationship relpgATSOperationId = new DomainRelationship();
			try {
				if (!UIUtil.isNullOrEmpty(sATSID) && !UIUtil.isNullOrEmpty(sTargetid))
				{
					DomainObject domFromObj =DomainObject.newInstance(context,sATSID);
					DomainObject domToObj =DomainObject.newInstance(context,sTargetid);
					//connect ATS-->RM with rel pgATSOperation
					relpgATSOperationId = DomainRelationship.connect(context, domFromObj, PGStructuredATSConstants.REL_PG_ATS_OPERATION  , domToObj);
				}
			}
			catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
				throw excep;
			}
		return relpgATSOperationId.toString();
	}

	/**
	 * Method to get SATS Information
	 * @param context
	 * @param mpRequestMap 
	 * @return
	 * @throws Exception
	 */	
	public String getATSInformation(Context context, Map mpRequestMap) throws Exception 
	{
		Map mObjectAttributeValues = new HashMap();
		Map mCAinfoMap = new HashMap();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
		JsonObjectBuilder jsonObjInfoCA = Json.createObjectBuilder();
		try {
			//ContextUtil.startTransaction(context, true);
			String strATSId = (String) mpRequestMap.get(DomainObject.SELECT_ID);
			DomainObject domATSObj = new DomainObject(strATSId);
			StringList objectSelects = new StringList();
			
			String sATSAttrbuteListArray = mpRequestMap.get("ObjectSelects").toString();
			String attribute_Name = null, attribute_value = null;
			StringList slAttributesMapping = FrameworkUtil.split(sATSAttrbuteListArray, ",");
			if (null != slAttributesMapping && slAttributesMapping.size() > 0) {
				for (int i = 0; i < slAttributesMapping.size(); i++) {
					attribute_Name = (String) slAttributesMapping.get(i);
					objectSelects.add(attribute_Name);
				}
				mObjectAttributeValues = domATSObj.getInfo(context, objectSelects);
				mObjectAttributeValues.remove(DomainConstants.SELECT_ID);
				
			}
			//GET CO CA CT information
			StringList selectStmts = new StringList();
			selectStmts.add(DomainConstants.SELECT_ID);
			selectStmts.add(DomainConstants.SELECT_NAME);
			// Get CO info
			Map sCAMap = com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil
					.getChangeObjectsInProposed(context, selectStmts, new String[] { strATSId }, 2);
			if (null != sCAMap && sCAMap.size() > 0) {
				MapList sCAMapInfo = (MapList) sCAMap.get(strATSId);
				if (null != sCAMapInfo && sCAMapInfo.size() > 0) {
					String sCAId = (String) (((Map) sCAMapInfo.get(0)).get(DomainConstants.SELECT_ID));
					String sCAName = (String) (((Map) sCAMapInfo.get(0)).get(DomainConstants.SELECT_NAME));

					mCAinfoMap.put(DomainConstants.SELECT_ID, sCAId);
					mCAinfoMap.put(DomainConstants.SELECT_NAME, sCAName);
				}
			}
			jsonObjInfo = getJsonObjectFromMap(mObjectAttributeValues);
			jsonObjInfoCA = getJsonObjectFromMap(mCAinfoMap);
			//ContextUtil.commitTransaction(context);
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}

		jsonObjInfo.add(PGStructuredATSConstants.STRING_CHANGEACTION, jsonObjInfoCA);
		return jsonObjInfo.build().toString();
	}
	/**
	 * Method to convert Map to JSON object
	 * @param mObjectAttributeValues
	 * @return
	 */
	private JsonObjectBuilder getJsonObjectFromMap(Map<?,?> mObjectAttributeValues) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		if (null != mObjectAttributeValues && !mObjectAttributeValues.isEmpty()) {
			for (Map.Entry<?, ?> entry : mObjectAttributeValues.entrySet()) {
				jsonReturnObj.add((String) entry.getKey(), (String) entry.getValue());
			}
		}
		return jsonReturnObj;
	}
	/**
	 * Method to update SATS Information
	 * @param context
	 * @param mpRequestMap 
	 * @return
	 * @throws Exception
	 */	
	public String updateATSInformation(Context context, Map mpRequestMap) throws Exception
	{
		Map mObjectAttributeValues = new HashMap();
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
	try 
	{
		ContextUtil.startTransaction(context, true);
		String strATSId = (String) mpRequestMap.get(DomainObject.SELECT_ID);
		updateStructuredATSAttributes(context,strATSId,(Map<String,String>)mpRequestMap.get("attributes")); 
		ContextUtil.commitTransaction(context);
	}
	catch (Exception excep) {
		logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
		jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
		jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		return jsonReturnObj.build().toString();
	}
		return STRING_OK;
	}
	
	  /**Updates Attributes values on new Object created
		 * @param context
		 * @param strNewObjectId
		 * @param mpAttribute
		 * @return
		 * @throws Exception
		 */
		public void updateStructuredATSAttributes(Context context,String strNewObjectId, Map<String,String> mpAttribute) throws Exception
		{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		HashMap attrMap = new HashMap();
		String strAttributeName = null;
		String strAttributeValue = null;
		String strAttributeWidgetName = null;
		DomainObject dObjSATS = null;
		try{
			if ( null!=mpAttribute && mpAttribute.size()>0)
			{
				for(Map.Entry entry:mpAttribute.entrySet())
				{
					strAttributeWidgetName = (String)entry.getKey();
					strAttributeName = strAttributeWidgetName.substring(strAttributeWidgetName.indexOf("[")+1,strAttributeWidgetName.lastIndexOf("]"));
					strAttributeValue = (String)entry.getValue();
					if ((UIUtil.isNotNullAndNotEmpty(strAttributeName) && UIUtil.isNotNullAndNotEmpty(strAttributeValue)) && !strAttributeName.equalsIgnoreCase(PropertyUtil.getSchemaProperty("attribute_ExpirationDate")))
					{
						attrMap.put(strAttributeName,strAttributeValue);			
					} else if ((UIUtil.isNotNullAndNotEmpty(strAttributeName) && UIUtil.isNotNullAndNotEmpty(strAttributeValue)) && strAttributeName.equalsIgnoreCase(PropertyUtil.getSchemaProperty("attribute_ExpirationDate"))) 
					{
						attrMap.put(strAttributeName,getExpirationDateValue(context,strAttributeValue));
					}
				}
			}	
			if (null != attrMap && attrMap.size()>0)
			{
				dObjSATS = DomainObject.newInstance(context,strNewObjectId);
				dObjSATS.setAttributeValues(context,attrMap);
			}
		}
		catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
		}
		/**converts Expiration Date into Date format to update on new Object
		 * @param context
		 * @param strAttributeValue
		 * @return
		 * @throws Exception
		 */
		public String getExpirationDateValue(Context context, String strAttributeValue) throws Exception
		{
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		String strExpirationDate = null;
		try{
			Calendar calndr = Calendar.getInstance();
			Date date = new Date(calndr.getTimeInMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat(eMatrixDateFormat.getInputDateFormat(), context.getLocale());
			strExpirationDate = dateFormat.format(date);
			strExpirationDate = strAttributeValue;
		} catch (Exception excep)
		{
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return jsonReturnObj.build().toString();
		}	
		return strExpirationDate;
		}
	
		
		/**
		 * Method to convert Maplist data with denied values To "No Access"
		 * @param context
		 * @param MapList
		 * @return
		 * @throws Exception
		 */	
		public MapList convertDeniedToNoAccess(Context context, MapList objectInfoList) throws Exception {
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();

			Map<String, String> objectMap = new HashMap<String, String>();
			try {

				Iterator itr = objectInfoList.iterator();
				while (itr.hasNext()) {
					objectMap = (Map) itr.next();
					for (String name : objectMap.keySet()) {
						String objectVal = objectMap.get(name);
						if (PGStructuredATSConstants.STRING_Denied.equalsIgnoreCase(objectVal)) {
							objectMap.put(name, PGStructuredATSConstants.STRING_NoAccess);
						}
					}

				}
			} catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			}
			return objectInfoList;
		}
		/**
		 * Method to get ATS Related Information
		 * @param context
		 * @param mpRequestMap 
		 * @return
		 * @throws Exception
		 */	
		public String getATSRelatedobjects(Context context, Map mpRequestMap) throws Exception 
		{
			MapList mObjectAttributeValues = new MapList();
			MapList mlgetRelatedSATS = new MapList();
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
			JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
			JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
			try {
				String strATSId = (String) mpRequestMap.get(DomainObject.SELECT_ID);
					if (UIUtil.isNullOrEmpty(strATSId))
					{
			      strATSId = (String) mpRequestMap.get(PGStructuredATSConstants.STRING_OBJ);
					}
					DomainObject domATSObj = DomainObject.newInstance(context ,strATSId);
					Pattern typePattern = new Pattern(PGStructuredATSConstants.TYPE_PLANT);
					Pattern relPattern = new Pattern(DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY);
					StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);
					StringList objectSelects = new StringList();
					objectSelects.add(DomainObject.SELECT_TYPE);
					objectSelects.add(DomainObject.SELECT_NAME);
					objectSelects.add(DomainObject.SELECT_REVISION);
					String sATSAttrbuteListArray = mpRequestMap.get(PGStructuredATSConstants.KEY_OBJECT_SELECTS).toString();
					String attribute_Name= null ;
					StringList slAttributesMapping = FrameworkUtil.split(sATSAttrbuteListArray,",");
					int sAttributesSize = slAttributesMapping.size();
					if ( sAttributesSize>0)
					{
					for ( int i=0; i < sAttributesSize ; i++)
					{
						attribute_Name=(String)slAttributesMapping.get(i);
						objectSelects.add(attribute_Name);
					}
					mlgetRelatedSATS =  domATSObj.getRelatedObjects(context, // the eMatrix Context object
							                                        relPattern.getPattern(), // Relationship pattern
							                                        typePattern.getPattern(), // Type Pattern
							                                        objectSelects, // Object selects
							                                        slRelSelects, // Relationship selects
							                                        true, // get From relationships
							                                        false, // get To relationships
							                                        (short) 1, // the number of levels to expand, 0 equals expand all.
							                                        null, // Object where clause
							                                        null, // Relationship where clause
							                                        0); // Limit : The max number of Objects to get in the expand.0 to return all the
																		// data available
					int sMapSize = mlgetRelatedSATS.size();
					 for ( int j=0; j < sMapSize ; j++)
					 {
						Map mpRemoveSelectedKey = (Map)mlgetRelatedSATS.get(j);
						mpRemoveSelectedKey.remove(DomainRelationship.SELECT_ID);
						mpRemoveSelectedKey.remove(PGStructuredATSConstants.KEY_RELATIONSHIP);
						mpRemoveSelectedKey.remove(PGStructuredATSConstants.KEY_LEVEL);
						jsonObjInfo = getJsonObjectFromMap(mpRemoveSelectedKey);
						jsonArrObjInfo.add(jsonObjInfo);
					 }
					}
			} catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			}
			jsonReturnObj.add(PGStructuredATSConstants.KEY_OBJECT_SELECTS, jsonArrObjInfo);
			return jsonReturnObj.build().toString();
		}
		
		/**
		 * Method to disconnected Plants connected to ATS
		 * @param context
		 * @param mpRequestMap 
		 * @return
		 * @throws Exception
		 */	
		public String removePlantsFromSATS(Context context, Map mpRequestMap) throws Exception {
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
			try {
				String strSATSId = (String) mpRequestMap.get(PGStructuredATSConstants.STRING_OBJ);
				String strPlantIds = (String) mpRequestMap.get(PGStructuredATSConstants.STRING_PLANT_ID);
				StringList slPlantIdList = StringUtil.split(strPlantIds, ",");

				if(UIUtil.isNotNullAndNotEmpty(strSATSId) && slPlantIdList != null && !slPlantIdList.isEmpty()) {
					
					StringList slObjSelects = new StringList(DomainConstants.SELECT_ID);
					StringList slRelSelects = new StringList(DomainConstants.SELECT_RELATIONSHIP_ID);
					
					DomainObject dobSATSObj = DomainObject.newInstance(context, strSATSId);
					MapList mlRelatedPlants = dobSATSObj.getRelatedObjects(context, // the eMatrix Context object
							DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, // Relationship pattern
							PGStructuredATSConstants.TYPE_PLANT, // Type pattern
							slObjSelects, // Object selects
							slRelSelects, // Relationship selects
							true, // get From relationships
							false, // get To relationships
							(short) 1, // the number of levels to expand, 0 equals expand all.
							null, // Object where clause
							null, // Relationship where clause
							0); // Limit : The max number of Objects to get in the exapnd.0 to return all the
								// data available

					if(mlRelatedPlants != null) {
						int iPlantListSize = mlRelatedPlants.size();
						for(int i=0;i<iPlantListSize;i++) {
							Map<?,?> mpPlantInfoMap = (Map<?, ?>) mlRelatedPlants.get(i);
							String strPlantId = (String) mpPlantInfoMap.get(DomainConstants.SELECT_ID);
							if(slPlantIdList.contains(strPlantId)) {
								String strRelId = (String) mpPlantInfoMap.get(DomainConstants.SELECT_RELATIONSHIP_ID);
								DomainRelationship.disconnect(context, strRelId);
							}
						}
					}
				}
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
				
			} catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
				throw excep;
			}

			return jsonReturnObj.build().toString();
		}
		
		/**
		 * Method to delete Objects
		 * @param context
		 * @param targetId 
		 * @throws Exception
		 */	
		public String deleteObject(Context context,String targetId) throws Exception 
		{
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();

			String returnStatus = PGStructuredATSConstants.VALUE_FAILED;
			try {
				if(UIUtil.isNotNullAndNotEmpty(targetId)){
					
					DomainObject.deleteObjects(context, new String[]{targetId});
					
					}
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			} catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			}

			return jsonReturnObj.build().toString();
		}
		
		/**
		 * Method to delete Relationship
		 * @param context
		 * @param targetId 
		 * @throws Exception
		 */	
		public String deleteRelationship(Context context,String targetId) throws Exception 
		{
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();

			String returnStatus = PGStructuredATSConstants.VALUE_FAILED;
			try {
				if(UIUtil.isNotNullAndNotEmpty(targetId)){
					
					DomainRelationship.disconnect(context, targetId);
					
					}
				jsonReturnObj.add(PGStructuredATSConstants.KEY_STATUS, PGStructuredATSConstants.VALUE_SUCCESS);
			} catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
				jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
				jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			}

			return jsonReturnObj.build().toString();
		}
		/**
		 * Method to get SATS Information
		 * @param context
		 * @param mpRequestMap 
		 * @return
		 * @throws Exception
		 */	
		public Map getATSInformationToCopy(Context context, Map mpRequestMap) throws Exception 
		{
			Map mObjectAttributeValues = new HashMap();
			Map mCAinfoMap = new HashMap();
			JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
			JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
			JsonObjectBuilder jsonObjInfoCA = Json.createObjectBuilder();
			try {
				String strATSId = (String) mpRequestMap.get(DomainObject.SELECT_ID);
				DomainObject domATSObj = new DomainObject(strATSId);
				StringList objectSelects = new StringList();
				
				String sATSAttrbuteListArray = mpRequestMap.get("ObjectSelects").toString();
				String attribute_Name = null, attribute_value = null;
				StringList slAttributesMapping = FrameworkUtil.split(sATSAttrbuteListArray, ",");
				if (null != slAttributesMapping && slAttributesMapping.size() > 0) {
					for (int i = 0; i < slAttributesMapping.size(); i++) {
						attribute_Name = (String) slAttributesMapping.get(i);
						objectSelects.add(attribute_Name);
					}
					mObjectAttributeValues = domATSObj.getInfo(context, objectSelects);
					String strTypeName = (String) mObjectAttributeValues.get(DomainConstants.SELECT_TYPE);
					String strDisplayName = EnoviaResourceBundle.getAdminI18NString(context,
							PGStructuredATSConstants.STR_SCHEMA_TYPE, strTypeName, strLanguage);

					if (strDisplayName == null) {
						strDisplayName = strTypeName;
					}
					mObjectAttributeValues.put(PGStructuredATSConstants.KEY_DISPLAY_TYPE, strDisplayName);
				}
			} catch (Exception excep) {
				logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE, excep);
			}
			return mObjectAttributeValues;
		}
}
