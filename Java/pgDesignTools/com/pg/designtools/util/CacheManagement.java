package com.pg.designtools.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.CacheUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.i18nNow;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;

import matrix.db.BusinessInterfaceList;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class CacheManagement {

    public CacheManagement(Context context) {
		PRSPContext.set(context);
	}
    
	/**
	 * This method would be invoked from Dynamic attributes field on type_VPMReference webform
	 * Method to fetch attributes for LPD/VPMReference as per the interfaces on it
	 * @param context
	 * @param args
	 * @return Maplist
	 */
	public MapList getDynamicAttributesForForm(Context context,String[] args)
	{
		MapList fieldMapList = new MapList();
		boolean isLayeredProduct = false;
		try 
		{
			HashMap<String,String> programMap =JPO.unpackArgs(args);
			String strObjectId = programMap.get("objectId");	
			
			StringList slSelects = new StringList(2);
			slSelects.addElement(DomainConstants.SELECT_TYPE);
			slSelects.addElement(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
			
			if(UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				DomainObject domObj = DomainObject.newInstance(context, strObjectId);
				Map<String, String> objectInfoMap = domObj.getInfo(context,slSelects);
				
				String strType = objectInfoMap.get(DomainConstants.SELECT_TYPE);
				String strEnterpriseType = objectInfoMap.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
				
				if(DataConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strType) && DataConstants.STR_ASSEMBLED_PRODUCT_PART.equalsIgnoreCase(strEnterpriseType))
				{
					isLayeredProduct = true;
				}					

				String strAttributes = null;
				if(isLayeredProduct)
				{
					strAttributes=getValueForProperty(context,DataConstants.STR_APOLLO_CONFIG_PAGE_FILENAME,"pgApolloConfiguration.VPMReference.CustomAttributeList");
								
				}else if(DataConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strType)){
					//Check if the object is non SW VPMReference
					HashMap<String,String> hmParam=new HashMap<>();
					hmParam.put("objectId", strObjectId);
					boolean isXCADType=JPO.invoke(context, "pgDSOCATIAIntegration", null, "checkVPMReferenceObjectForXCADType", JPO.packArgs(hmParam), Boolean.class);

					if(!isXCADType) {
						strAttributes=getAttributesForVPMReference(context,domObj);
					}
				}
				if(UIUtil.isNotNullAndNotEmpty(strAttributes)) {
					StringList slAttributes = StringUtil.split(strAttributes,",");
					if(null!=slAttributes && !slAttributes.isEmpty())
					{
						fieldMapList=setFieldMapList(context,slAttributes);
					}
				}
			}
		} 
		catch (Exception e)
		{
			VPLMIntegTraceUtil.trace(context, e.getMessage());
		}	
		return fieldMapList;
	}
	
	/**
	 * Method to get the value for particular key in the page file.
	 * This method would use cached page contents if available or would cache the page contents
	 * @param context
	 * @param String page name
	 * @param String key (property name)
	 * @return String
	 * @throws IOException, FrameworkException
	 */
	public String getValueForProperty(Context context, String strPageName,String strPropertyName) throws IOException, FrameworkException {
		String strValue="";
		String strPageContent=(String) CacheUtil.getCacheObject(context, strPageName);

		Properties properties = new Properties();
		
		if(UIUtil.isNullOrEmpty(strPageContent)) {

			strPageContent=MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageName);
			CacheUtil.setCacheObject(context, strPageName, strPageContent);
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strPageContent)) {
			properties.load(new StringReader(strPageContent));
			strValue = properties.getProperty(strPropertyName);
		}
		return strValue;
	}

	/**
	 * Method to get interfaces on VPMReference and corresponding attributes
	 * @param context
	 * @param DO of VPMReference
	 * @return String
	 * @throws MatrixException, IOException
	 */
	private String getAttributesForVPMReference(Context context,DomainObject domObj) throws MatrixException, IOException {
		String strAttributes = null;
		BusinessInterfaceList busInterfaces;
		busInterfaces = domObj.getBusinessInterfaces(context);
		String strInterfaceName;
		StringBuilder sbAttributes = new StringBuilder();
		String strPropertyName="";
		
		for(int i=0;i<busInterfaces.size();i++) {
			strInterfaceName=busInterfaces.get(i).toString();

			//excluding the default interface
			if(!("pngiDesignPart".equals(strInterfaceName))) {
				
				strPropertyName=DataConstants.STR_DESIGN_CONFIG_PAGE_FILENAME+"."+strInterfaceName+".AttributeList";
				
				if(UIUtil.isNotNullAndNotEmpty(sbAttributes.toString()))
					sbAttributes.append(",");
				
				sbAttributes.append(getValueForProperty(context, DataConstants.STR_DESIGN_CONFIG_PAGE_FILENAME, strPropertyName));
			}
		}
		if(UIUtil.isNotNullAndNotEmpty(sbAttributes.toString()))
			strAttributes=sbAttributes.toString();

		return strAttributes;
	}

	/**
	 * Method to get the field settings for dynamic attributes to be displayed
	 * @param context
	 * @param StringList of attributes
	 * @return MapList
	 * @throws MatrixException
	 */
	private MapList setFieldMapList(Context context,StringList slAttrList) throws MatrixException {
		MapList fieldMapList = new MapList();
		String strAttributeActualName;
		String strAttributeSymbolicName;
		String strAttributeDisplayName;
		HashMap<String,Object> fieldMap;
		HashMap<String,String> settingsMap;
		String strLanguage =  context.getSession().getLanguage();
		
		for (int i = 0; i < slAttrList.size(); i++)
		{
			fieldMap = new HashMap<>();
			settingsMap = new HashMap<>();
			
			strAttributeSymbolicName = slAttrList.get(i);
			strAttributeActualName = PropertyUtil.getSchemaProperty(context, strAttributeSymbolicName);
			
			strAttributeDisplayName = i18nNow.getAttributeI18NString(strAttributeActualName,strLanguage);
			
			fieldMap.put(DomainConstants.SELECT_NAME,strAttributeDisplayName);
			fieldMap.put("label",strAttributeDisplayName);
			fieldMap.put(DataConstants.EXPRESSION_BUS,"attribute["+strAttributeActualName+"]");
			
			settingsMap.put(DataConstants.CONSTANT_STRING_ADMIN_TYPE, strAttributeSymbolicName);
			settingsMap.put("Editable", "false");
			
			fieldMap.put("settings",settingsMap);
			fieldMapList.add(fieldMap);
		}
		return fieldMapList;
	}
	
	/**
	 * Method to get the picklist items
	 * This method would use cached contents if available or would cache the contents
	 * @param context
	 * @param String picklist name
	 * @return Map
	 * @throws MatrixException 
	 */
	public Object getPickListItems(Context context, String strPickListName) throws MatrixException  {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of getPickListItems method");
		Map mpReturnMap;
		mpReturnMap=CacheUtil.getCacheMap(context, strPickListName);

		if(null!=mpReturnMap && !mpReturnMap.isEmpty())
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Cache result for PickList "+strPickListName+" is ::"+mpReturnMap);	
		
		if(null==mpReturnMap || mpReturnMap.isEmpty()) {
			HashMap hmArgs=new HashMap();
			HashMap fieldMap=new HashMap();
			HashMap settings=new HashMap();
			
			settings.put("pgPicklistName", strPickListName);
			settings.put("Input Type","combobox");
			fieldMap.put("settings",settings);
			hmArgs.put("fieldMap",fieldMap);
			
			//invoke the corresponding method for particular field
			if(strPickListName.equals(DataConstants.CONST_PICKLIST_SEGMENT)) {
				mpReturnMap=JPO.invoke(context, DataConstants.CONST_PICKLIST_JPO, null, DataConstants.CONST_PICKLIST_RANGE_METHOD,JPO.packArgs(hmArgs),Map.class);
			}else if(strPickListName.equals(DataConstants.CONST_PICKLIST_PACKMATERIALTYPE) || strPickListName.equals(DataConstants.CONST_PICKLIST_CLASS)
					|| strPickListName.equals(DataConstants.CONST_PICKLIST_ORGCHANGEMGMT)) {
				mpReturnMap=JPO.invoke(context, DataConstants.CONST_DSOUTIL_JPO, null, DataConstants.CONST_PICKLIST_DIRECT_ATTR_METHOD,JPO.packArgs(hmArgs),Map.class);
			}else if(strPickListName.equals(DataConstants.CONST_PICKLIST_REPORTEDFUNCTION)) {
				String strObjectId="";
				
				//find a object of type MPAP to pass the value for objectId. This would ensure to bring the list of PicklistSubset instead of all the Reported Functions
				MapList mlMPAPData=findMPAPObject(context, DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART, DataConstants.VAULT_ESERVICE_PRODUCTION, 
						new StringList(DomainConstants.SELECT_ID));
				
				if(!mlMPAPData.isEmpty()) {
					Map mpMPAP=(Map) mlMPAPData.get(0);
					strObjectId=(String) mpMPAP.get(DomainConstants.SELECT_ID);
				}
				HashMap hmParamMap=new HashMap();
				hmParamMap.put("objectId",strObjectId);
				hmArgs.put("paramMap", hmParamMap);
				settings.put("allowedTypes", "emxCPN.ReportedFunction.Range.Subset.allowedTypes");
				settings.put("IncludeBlank", "true");
				settings.put("pgPicklistSubset", "Packaging Parts Reported Function");

				mpReturnMap=JPO.invoke(context, DataConstants.CONST_PICKLIST_JPO, null, DataConstants.CONST_PICKLIST_SUBSET_PLTYPE_METHOD,JPO.packArgs(hmArgs),Map.class);
			}
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> JPO result for PickList "+strPickListName+" is ::"+mpReturnMap);	
			if(null!=mpReturnMap && !mpReturnMap.isEmpty())
				CacheUtil.setCacheMap(context,strPickListName, mpReturnMap);
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> END of getPickListItems method");
		return mpReturnMap;
	}
	
	/**
	 * Method to get the picklist items as stringlist
	 * This method would use cached contents if available or would cache the contents
	 * @param context
	 * @param String picklist name
	 * @return StringList
	 * @throws MatrixException 
	 */
	public Object getStringListPickListItems(Context context, String strPickListName) throws MatrixException  {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of getStringListPickListItems method");
		StringList slResult;
		slResult=(StringList)CacheUtil.getCacheObject(context, strPickListName);

		if(null!=slResult && !slResult.isEmpty())
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Cache result for PickList "+strPickListName+" is ::"+slResult);	
		
		if(null==slResult || slResult.isEmpty()) {
			HashMap hmArgs=new HashMap();
			HashMap fieldMap=new HashMap();
			HashMap settings=new HashMap();
			
			settings.put("pgPicklistName", strPickListName);
			settings.put("Input Type","combobox");
			fieldMap.put("settings",settings);
			hmArgs.put("fieldMap",fieldMap);
			
			//invoke the corresponding method for particular field

			if(strPickListName.equals(DataConstants.CONST_PICKLIST_LIFECYCLESTATUS_EXPERIMENTAL) || 
					strPickListName.equals(DataConstants.CONST_PICKLIST_LIFECYCLESTATUS_PILOT) || 
					strPickListName.equals(DataConstants.CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTION) || 
					strPickListName.equals(DataConstants.CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTIONAPP)) {
				settings.put("pgPicklistSubset", strPickListName);
				slResult=JPO.invoke(context, DataConstants.CONST_PICKLIST_JPO, null, DataConstants.CONST_PICKLIST_SUBSET_METHOD,JPO.packArgs(hmArgs),StringList.class);
			}
			
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> JPO result for PickList "+strPickListName+" is ::"+slResult);	
			if(null!=slResult && !slResult.isEmpty())
				CacheUtil.setCacheObject(context,strPickListName, slResult);
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> END of getStringListPickListItems method");
		return slResult;
	}
	
	/**
	 * Method to find one MPAP object from database
	 * @param context
	 * @param sType
	 * @param sVault
	 * @param slSelects
	 * @return MapList
	 * @throws FrameworkException
	 */
	private MapList findMPAPObject(Context context, String sType,String sVault,StringList slSelects) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of findMPAPObject method");
		MapList mlObject;
		mlObject=DomainObject.findObjects(context, 
				sType,    //typePattern
				sVault,    //vaultPattern
				"",          //where expression
				slSelects,  //object selects
				(short) 1,            //limit
				null);    //orderBys
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> mlObject::"+mlObject);
		return mlObject;
	}
}
