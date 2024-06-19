package com.pg.designtools.datamanagement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.dassault_systemes.enovia.template.enumeration.TemplateAttribute;
import com.dassault_systemes.enovia.template.enumeration.TemplateRelationship;
import com.dassault_systemes.enovia.template.enumeration.TemplateType;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.Job;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.lifecycle.LifeCyclePolicyDetails;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.integrations.exception.DesignToolsIntegrationException;
import com.pg.designtools.util.IPManagement;
import com.pg.designtools.util.PreferenceManagement;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.Signature;
import matrix.db.SignatureList;
import matrix.db.State;
import matrix.db.StateList;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CommonUtility {

	public CommonUtility() {
		/*
		 * Currently Nothing to be done
		 */
	}
	
	public CommonUtility(Context context) {
		PRSPContext.set(context);
	}

	static DataConstants.customTOPSExceptions errorNoDefaultIP = DataConstants.customTOPSExceptions.ERROR_400_NO_DEFAULT_IP_CONTROL;

	boolean bIsContextPushed = false;
	/*
	 * This method will Search the template in Released state for the given Domain
	 * Object and connect to it
	 */
	public static void connectProductDataTemplate(DomainObject domObject) throws FrameworkException {
		Context context = PRSPContext.get();
		
		StringList slSelects=new StringList(2);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(DomainConstants.SELECT_POLICY);
		
		Map mpObjectInfo=domObject.getInfo(context,slSelects);
		String strType = (String)mpObjectInfo.get(DomainConstants.SELECT_TYPE);
		String strPolicy = (String)mpObjectInfo.get(DomainConstants.SELECT_POLICY);
			
		String strTemplateId=getTemplateObjectId(context, strType, strPolicy);
		
			if (UIUtil.isNotNullAndNotEmpty(strTemplateId)) {
				DomainRelationship.connect(context, domObject, TemplateRelationship.TEMPLATE.get(context),
						DomainObject.newInstance(context, strTemplateId));
			}
	}

	/**
	 * Method to get the Template object id as per the type and policy of the EP object.
	 * @param context
	 * @param String type of object
	 * @param String policy of object
	 * @return String templateId
	 * @throws FrameworkException
	 */
public static String getTemplateObjectId(Context context,String strObjectType,String strObjectPolicy) throws FrameworkException {
	
	VPLMIntegTraceUtil.trace(context, ">>>START getTemplateObjectId method");
	VPLMIntegTraceUtil.trace(context, ">>>Object type::"+strObjectType+" policy::"+strObjectPolicy);
	
	String strTemplateId="";
	StringList slSelects=new StringList(1);
	slSelects.add(DomainConstants.SELECT_ID);
	
	strObjectPolicy=FrameworkUtil.getAliasForAdmin(context, "policy",strObjectPolicy, true);
	strObjectType=FrameworkUtil.getAliasForAdmin(context, "type",strObjectType, true);
	
	VPLMIntegTraceUtil.trace(context, ">>>Symbolic type::"+strObjectType+" policy::"+strObjectPolicy);
	
	StringBuilder sbWhere=new StringBuilder();
	sbWhere.append(DomainConstants.SELECT_CURRENT).append(" == ").append(DataConstants.STATE_RELEASE);
	sbWhere.append(" && ");
	sbWhere.append(TemplateAttribute.OBJECT_TYPE.getSelect(context)).append(" == \"").append(strObjectType).append("\"");
	sbWhere.append(" && ");
	sbWhere.append(TemplateAttribute.OBJECT_POLICIES.getSelect(context)).append(" == \"").append(strObjectPolicy).append("\"");
	
	VPLMIntegTraceUtil.trace(context, ">>>where clause::"+sbWhere.toString());
	
	MapList mlTemplateList = DomainObject.findObjects(context, 
			TemplateType.TEMPLATE.get(context),			//typePattern
			context.getVault().getName(), 							//vaultPattern
			sbWhere.toString(), 											//where expression
			slSelects);															//object selects
	
	VPLMIntegTraceUtil.trace(context, ">>>mlTemplateList::"+mlTemplateList);
	
	if(null!=mlTemplateList && !mlTemplateList.isEmpty()) {
		Map mpTemplate=(Map)mlTemplateList.get(0);
		strTemplateId=(String)mpTemplate.get(DomainConstants.SELECT_ID);
	}
	VPLMIntegTraceUtil.trace(context, ">>>strTemplateId::"+strTemplateId);
	return strTemplateId;
}
	
	/*
	 * This method will accept first argument as the object to whom we want to
	 * connect the relationships second argument a Map with key = DomainObject to be
	 * connected Value = List -> list[0] = RelationshipName , list[1] = To/From Here
	 * To/From will indicate the side of the context Object on which this connect
	 * method is invoked.
	 */
	public static void connect(DomainObject contextObject, Map<DomainObject, ArrayList<String>> mpConnectionDetails)
			throws FrameworkException {
		Context context = PRSPContext.get();
		String relName;
		DomainObject domObjForConnection = null;
		ArrayList<String> relDetails;

		String strSPSObjSide;
		DomainObject domFromObject = contextObject;
		DomainObject domToObject = domObjForConnection;

		for (Map.Entry<DomainObject, ArrayList<String>> relEntry : mpConnectionDetails.entrySet()) {
			domObjForConnection = relEntry.getKey();
			relDetails = relEntry.getValue();
			relName = relDetails.get(0);
			strSPSObjSide = relDetails.get(1);

			if (DataConstants.CONSTANT_TO.equalsIgnoreCase(strSPSObjSide)) {
				domFromObject = domObjForConnection;
				domToObject = contextObject;
			}

			DomainRelationship.connect(context, domFromObject, relName, domToObject);
		}
	}

	public static void setIPClassificationClassOnECPart(String strType, String strObjectId) throws MatrixException{
		Context context = PRSPContext.get();
		IPManagement ipMgmt=new IPManagement(PRSPContext.get());
		Map<String,String> mpPreference=ipMgmt.getPreferenceFromType(context, strType);
		String sFinalIPClassList=ipMgmt.getUserPreferenceValue(context, mpPreference); 
		
		if(UIUtil.isNullOrEmpty(sFinalIPClassList)) {
			throw new DesignToolsIntegrationException(errorNoDefaultIP.getExceptionCode(),errorNoDefaultIP.getExceptionMessage());
		}
		String[] argument = new String[] { strType, strObjectId };
		JPO.invoke(context, "pgIPSecurityDefaultClassConnect", null, "setDefaultIPClassesOnCreation", argument,void.class);
	}

	public static String getCurrentState(DomainObject domObj) throws FrameworkException {
		return domObj.getInfo(PRSPContext.get(), DomainConstants.SELECT_CURRENT);
	}

	public static String getOwner(DomainObject domObj) throws FrameworkException {
		return domObj.getInfo(PRSPContext.get(), DomainConstants.SELECT_OWNER);
	}

	public static DomainObject getSPSFromTNR(String strXMLExtType, String strXMLExtName, String strXMLExtRev)
			throws MatrixException {
		Context context = PRSPContext.get();
		DomainObject domSPSObj = null;
		BusinessObject busObj = new BusinessObject(strXMLExtType, strXMLExtName, strXMLExtRev,
				DataConstants.VAULT_ESERVICE_PRODUCTION);
		if(busObj.exists(context))
		{
			if (strXMLExtType.equalsIgnoreCase(DataConstants.TYPE_PG_STACKINGPATTERN)) {
					domSPSObj = DomainObject.newInstance(context, busObj.getObjectId(context));
	
			} else if (strXMLExtType.equalsIgnoreCase(DataConstants.TYPE_PGIPMDOCUMENT)) {
					DomainObject domIPM = DomainObject.newInstance(context, busObj.getObjectId(context));
				StackingPattern objSPS = new StackingPattern();
				domSPSObj = objSPS.getRelatedSPS(domIPM);
			}
		}
		return domSPSObj;
	}
	
	/**
	 * Method which would be invoked from all applicable scenarios to connect Primary and Secondary Org to object
	 * @param context
	 * @param doObject
	 * @throws FrameworkException
	 */
   public static void connectOrganizations(Context context, DomainObject doObject) throws FrameworkException {
	   PreferenceManagement prefMgmt=new PreferenceManagement(context);
	   prefMgmt.connectOrganizations(context, doObject);
   }
	
	/* Context Push-Pop utility for future use to allow for centralized set of functionality to be used in DT Framework
	 * For usage, follow framework guidelines but in general
	 * a) only use the methods pushContext and popContext with a trycatch block
	 * b) popContext should be used in the finally block of the try-catch
	 */
	/* blocking until Platform defines better mechanism for annotating usage of common functionality
	
		public boolean isContextPushed() {
			return bIsContextPushed;
		}
		
		public void pushContext(String strUser) throws FrameworkException {
			ContextUtil.pushContext(PRSPContext.get(), strUser, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bIsContextPushed = true;
		}

		public void popContext() throws FrameworkException {
			if (bIsContextPushed) {
				ContextUtil.popContext(PRSPContext.get());
			}
		}*/
		
	/**
	 * Method to execute the mql commands from the file
	 * @param context
	 * @param sMQLCommand
	 * @throws FrameworkException
	 */
	public String executeMQLCommands(Context context, String sMQLCommand) throws FrameworkException {
		String strResult="";
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START executeMQLCommands method");
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>context user::"+context.getUser());
		if(UIUtil.isNotNullAndNotEmpty(sMQLCommand)) {
			try {
				ContextUtil.startTransaction(context, true);
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Transaction started");
				
				//This mql statement is executed for dynamically generated script file. 
				strResult=MqlUtil.mqlCommand(context,
							 sMQLCommand,			             //command
							 true,										//run as super user
							 false);                                     //allow multiple commands
				
			}catch(Exception ex) {
				if(ContextUtil.isTransactionActive(context)) {
					ContextUtil.abortTransaction(context);	
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Transaction aborted");
					VPLMIntegTraceUtil.trace(context, ex.getMessage());
					throw new FrameworkException(ex.getMessage());
					
				}
			}finally {
				if(ContextUtil.isTransactionActive(context)) {
					ContextUtil.commitTransaction(context);
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Transaction committed");
				}
			}
		}
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END executeMQLCommands method");
		return strResult;
	}

	
	/**
	 * Method to check Running Background Job connected
	 * @param context
	 * @param strPartObjectId
	 * @param strJobTitle
	 * @return
	 * @throws FrameworkException
	 */
	public String checkRunningBackGroundJobConnected(Context context , String strPartObjectId, String strJobTitle) throws FrameworkException
	{
		String strIsRunningBackgroundJobConnected = DataConstants.CONSTANT_FALSE;
		StringBuilder sbReturnMessage = new StringBuilder();
		try
		{		
			if(UIUtil.isNotNullAndNotEmpty(strPartObjectId))
			{
				DomainObject domECPart = DomainObject.newInstance(context, strPartObjectId);

				StringList slBusSelects = new StringList(3);
				slBusSelects.addElement(DomainConstants.SELECT_NAME);
				slBusSelects.addElement(DomainConstants.SELECT_CURRENT);
				slBusSelects.addElement(DomainConstants.SELECT_ATTRIBUTE_TITLE);
				

				StringBuilder	whereExp	= new StringBuilder();
				whereExp.append(DomainConstants.SELECT_ATTRIBUTE_TITLE).append(DataConstants.CONST_DOUBLE_EQUAL).append("'").append(strJobTitle).append("'");
				whereExp.append(DataConstants.CONSTANT_STRING_SPACE);
				whereExp.append(DataConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
				whereExp.append(DataConstants.CONSTANT_STRING_SPACE);
				whereExp.append(DomainConstants.SELECT_CURRENT).append(DataConstants.CONST_DOUBLE_EQUAL).append(Job.STATE_JOB_RUNNING);

				String strJobName;
				String strJobList;				
				StringList slExistingRunningJobs = new StringList();
				Map mpJobDetails = null;
				
				MapList mlAPPJobDetails = domECPart.getRelatedObjects(context,//context
						DataConstants.RELATIONSHIP_PENDING_JOB, // relationship pattern
						DataConstants.TYPE_JOB, // type pattern
						slBusSelects, // object selects
						null, // relationship selects
						false, // to direction
						true, // from direction
						(short)1,// recursion level
						whereExp.toString(), // object where clause
						null, // relationship where clause
						0);// objects Limit
				
				VPLMIntegTraceUtil.trace(context, "MassCollab: mlAPPJobDetails >>> " + mlAPPJobDetails);
				
				if(null !=mlAPPJobDetails && !mlAPPJobDetails.isEmpty())
				{
					for(int count =0 ; count < mlAPPJobDetails.size() ; count++)
					{
						mpJobDetails = (Map)mlAPPJobDetails.get(count);
						strJobName = (String)mpJobDetails.get(DomainConstants.SELECT_NAME);
						slExistingRunningJobs.addElement(strJobName);						
					}	
					
					if(!slExistingRunningJobs.isEmpty())
					{
						strJobList = StringUtil.join(slExistingRunningJobs, DataConstants.SEPARATOR_COMMA);
						sbReturnMessage.append(DataConstants.STR_ERROR_BACKGROUND_JOB_ISRUNNING).append(strJobList).append(DataConstants.CONSTANT_DOT);
					}
				}
			}
			if(sbReturnMessage.toString().isEmpty())
			{
				sbReturnMessage.append(strIsRunningBackgroundJobConnected);
			}
		}
		catch (FrameworkException e)
		{
			sbReturnMessage = new StringBuilder();
			sbReturnMessage.append(DataConstants.STR_ERROR).append(DataConstants.SEPARATOR_COLON).append(e.getLocalizedMessage());		
		}		
		return sbReturnMessage.toString();
	}

	/**
	 * This method would find the object depending upon where clause passed
	 * @param context
	 * @param String type of object
	 * @param String name of object
	 * @param String rev of object
	 * @param String where clause
	 * @param StringList selectables for object
	 * @return MapList
	 * @throws FrameworkException 
	 */
	public MapList findObjectWithWhereClause(Context context, String strType, String strName, String strRev,String strWhereClause, StringList slSelects) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of findObjectWithWhereClause method");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> sType::"+strType+" sName::"+strName+" sRev::"+strRev);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> strWhereClause::"+strWhereClause);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> slSelects::"+slSelects);

		MapList mlObject=DomainObject.findObjects(context,
					strType,																					//typePattern
					strName,																					//namePattern
					strRev,																						//revPattern
					DomainConstants.QUERY_WILDCARD,									//ownerPattern
					DomainConstants.QUERY_WILDCARD,         							//vaultPattern
					strWhereClause,                                   									//where expression
					false,                                   														//expandType
					slSelects);                             													//object selectables

		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> mlObject::"+mlObject);
		return mlObject;
	}
	
	/**
	 * Method added to validate whether string is numeric or not
	 * @param String
	 * @return boolean
	 */
	public boolean isNumeric(String strValue) {
	    if (UIUtil.isNullOrEmpty(strValue)) {
	        return false;
	    }

	    for (char c : strValue.toCharArray()) {
	        if (c!='.' && !Character.isDigit(c)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	/**
	 * Method to verify whether Person is active or not
	 * @param context
	 * @param strPersonName
	 * @return boolean
	 * @throws FrameworkException
	 */
	public boolean verifyIfPersonIsActive(Context context, String strPersonName) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>START of verifyIfPersonIsActive method");
		boolean bIsPersonActive=false;
		
		String strCurrent="";
		StringList slSelects=new StringList(1);
		slSelects.add(DomainConstants.SELECT_CURRENT);
		
		IPManagement ipMgmt=new IPManagement(context);
		MapList mlOwnerInfo=ipMgmt.findObject(context, DomainConstants.TYPE_PERSON, strPersonName, DomainConstants.QUERY_WILDCARD, slSelects);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>mlOwnerInfo::"+mlOwnerInfo);
		
		if(!mlOwnerInfo.isEmpty()) {
			Map mpOwner=(Map)mlOwnerInfo.get(0);
			strCurrent=(String)mpOwner.get(DomainConstants.SELECT_CURRENT);
			if(DomainConstants.STATE_PERSON_ACTIVE.equalsIgnoreCase(strCurrent))
				bIsPersonActive=true;
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>bIsPersonActive::"+bIsPersonActive);
		return bIsPersonActive;
	}

	/**
	 * This methods fetches all the states of the policy
	 * @param context
	 * @param doCADObj
	 * @param strObjPolicy
	 * @return slStateList
	 * @throws MatrixException
	 */
	public StringList getStateList(Context context, DomainObject doCADObj, String strObjPolicy) throws MatrixException {
		StateList statesList  = LifeCyclePolicyDetails.getStateList(context, doCADObj, strObjPolicy);
		State state = null;
		StringList slStateList = new StringList();
		for(Iterator<State> itr =  statesList.iterator();itr.hasNext();)
		{
			state = itr.next();
			slStateList.add(state.getName());					
		}
		return slStateList;
	}
	
	
	/**
	 * This method fetched previous state of a object
	 * @param strCurrentState
	 * @param slStateList
	 * @return strPreviousState
	 */
	public String getPreviousState(String strCurrentState, StringList slStateList) {
		String strPreviousState = "";		
		int indexCurrentState = 0;
		try {
			if(null!=slStateList && !slStateList.isEmpty())
			{		
				indexCurrentState = slStateList.indexOf(strCurrentState);
				if(indexCurrentState > 0)
				{
					strPreviousState = slStateList.get(indexCurrentState-1);
				}	
			}
		} catch (Exception e) {
			throw e;
		}
		return strPreviousState;
	}
	
	/**
	 * This method rejects the already approved previous state signatures
	 * @param context
	 * @param domObjRef
	 * @param strCurrentState
	 * @param strPreviousState
	 * @throws MatrixException
	 */
	public void rejectPreviousSignatures(Context context, DomainObject domObjRef,String strCurrentState, String strPreviousState) throws Exception  {
		SignatureList slSignature = null;
		Signature objSignature = null;
		VPLMIntegTraceUtil.trace(context, "rejectPreviousSignatures Start ");
		VPLMIntegTraceUtil.trace(context, "rejectPreviousSignatures strCurrentState "+strCurrentState);
		VPLMIntegTraceUtil.trace(context, "rejectPreviousSignatures strPreviousState "+strPreviousState);
		try {
			if(UIUtil.isNotNullAndNotEmpty(strPreviousState)) {
				slSignature = domObjRef.getSignatures(context,strCurrentState,strPreviousState);													
				for(Iterator<Signature> itr =  slSignature.iterator();itr.hasNext();)
				{
					objSignature = itr.next();
					if(objSignature.isApproved())
					{
						VPLMIntegTraceUtil.trace(context, "rejectPreviousSignatures Inside rejectSignature ");
						domObjRef.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}	
	}
	
	/**
	 * Method to populate the Part Type preference, in case its empty and Sync to Server operation is performed
	 * @param strDesignDomain
	 * @return strECPartType
	 */
	public String getECPartTypeForDesignDomain(String strDesignDomain) {
		String strECPartType="";
		
		if(strDesignDomain.equals(DataConstants.CONSTANT_DESIGN_FOR_PACKAGING)) {
			strECPartType=DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART;
		}
		else if(strDesignDomain.equals(DataConstants.CONSTANT_DESIGN_FOR_PRODUCT)) {
			strECPartType=DataConstants.TYPE_PG_MASTER_PRODUCT_PART;
		}
		else if(strDesignDomain.equals(DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION)){
			strECPartType=DataConstants.TYPE_SHAPE_PART;
		}
		else if(strDesignDomain.equals(DataConstants.CONSTANT_DESIGN_FOR_ASSEMBLED)){
			strECPartType=DataConstants.TYPE_ASSEMBLED_PRODUCT_PART;
		}
		return strECPartType;
	}
	
	/**
	 * Method to delete object
	 * @param context
	 * @param strObjectId
	 * @throws Exception
	 */
	public void deleteObject(Context context,String strObjectId) throws Exception {
		VPLMIntegTraceUtil.trace(context, ">>> START of deleteObject method");
		if(UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			DomainObject doObject=DomainObject.newInstance(context,strObjectId);
			VPLMIntegTraceUtil.trace(context,">>> object exists:::"+doObject.exists(context));
			if(doObject.exists(context)){
					doObject.deleteObject(context);
					VPLMIntegTraceUtil.trace(context,">>> Deleted the object");
			}
		}
		VPLMIntegTraceUtil.trace(context, "<<< END of deleteObject method");
	}
	
	/**
	 * Method to fetch the IP Control classes connected with Protected Item relationship to the given object
	 * @param context
	 * @param String objectId
	 * @throws FrameworkException 
	 */
	public MapList getIPControlClassesOfObject(Context context,String strObjectId) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> START of getIPControlClassesOfObject method");
		
		MapList mlIPControlClasses=new MapList();
		StringList slSelects=new StringList(2);
		slSelects.add(DomainConstants.SELECT_ID);
		slSelects.add(DomainConstants.SELECT_NAME);
		
		DomainObject doObject=DomainObject.newInstance(context,strObjectId);
		
		mlIPControlClasses=doObject.getRelatedObjects(context,//context
				DomainConstants.RELATIONSHIP_PROTECTED_ITEM, // relationship pattern
				DataConstants.TYPE_IP_CONTROL_CLASS, // type pattern
				slSelects, // object selects
				null, // relationship selects
				true, // to direction
				false, // from direction
				(short)1,// recursion level
				null, // object where clause
				null, // relationship where clause
				0);// objects Limit
		
		VPLMIntegTraceUtil.trace(context, ">>> mlIPControlClasses::"+mlIPControlClasses);
		VPLMIntegTraceUtil.trace(context, "<<< END of getIPControlClassesOfObject method");
		return mlIPControlClasses;
	}
	
}
