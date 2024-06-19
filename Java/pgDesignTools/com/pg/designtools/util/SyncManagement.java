package com.pg.designtools.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SyncManagement {
	
  
    public SyncManagement(Context context) {
		PRSPContext.set(context);
	}
    
    public static final String STR_PROGNAME_CATIA_INTEGRATION="pgDSOCATIAIntegration";
    public static final String STR_METHODNAME_GET_ECPART_INFO="getECPartInfoFromCAD";
    
	/**
	 * This method would be invoked from webservice. It would return the name of the EC Part connected to the VPMReference
	 * @param context
	 * @param String type of object
	 * @param String name of object
	 * @param String rev of object
	 * @return String name of connected EC Part
	 * @throws MatrixException 
	 */
    public String getConnectedECPartName(Context context, String strType,String strName,String strRev) throws MatrixException {
    	
    	String strECPartName="";
    	
    	if(UIUtil.isNotNullAndNotEmpty(strType) && UIUtil.isNotNullAndNotEmpty(strName) && UIUtil.isNotNullAndNotEmpty(strRev)) {
    		
    		StringList slSelects=new StringList(1);
    		slSelects.add(DomainConstants.SELECT_ID);
    		
    		IPManagement ipMgmt=new IPManagement(context);
    		MapList mlObject=ipMgmt.findObject(context, strType, strName, strRev, slSelects);
    		
    		if(!mlObject.isEmpty()) {
				Map<String,String>mpObject=(Map<String, String>) mlObject.get(0);
				String strCADObjId=mpObject.get(DomainConstants.SELECT_ID);
				HashMap<String, String>hmParam=new HashMap<>();
				
				hmParam.put("objectId", strCADObjId);
				hmParam.put("objSelects", DomainConstants.SELECT_NAME);
				Map mpECPartInfo=JPO.invoke(context, STR_PROGNAME_CATIA_INTEGRATION, null, STR_METHODNAME_GET_ECPART_INFO, JPO.packArgs(hmParam), Map.class);
				strECPartName=(String) mpECPartInfo.get(DomainConstants.SELECT_NAME);
				if(UIUtil.isNullOrEmpty(strECPartName))
					strECPartName="";
    		}
    	}
    	return strECPartName;
    }
    
    /**
     * This method would return the drop down list for Manufacturing Status field on DI Preferences, as per selection of Release Phase (Mfg Maturity Status) field
     * @param context
     * @param strReleasePhase
     * @return StringList
     * @throws MatrixException
     */
    public StringList getMfgStatusValue(Context context,String strReleasePhase) throws MatrixException{
		StringList slMfgStatusValues=new StringList();
		CacheManagement cacheMgmt=new CacheManagement(context);
	
		if(DataConstants.RELEASE_PHASE_DEVELOPMENT.equals(strReleasePhase)){
			slMfgStatusValues=(StringList)cacheMgmt.getStringListPickListItems(context, DataConstants.CONST_PICKLIST_LIFECYCLESTATUS_EXPERIMENTAL);
		}else if(DataConstants.RELEASE_PHASE_PILOT.equals(strReleasePhase)){
			slMfgStatusValues=(StringList)cacheMgmt.getStringListPickListItems(context, DataConstants.CONST_PICKLIST_LIFECYCLESTATUS_PILOT);
		}else if(DataConstants.RELEASE_PHASE_PRODUCTION.equals(strReleasePhase)){
			slMfgStatusValues=(StringList)cacheMgmt.getStringListPickListItems(context, DataConstants.CONST_PICKLIST_LIFECYCLESTATUS_PRODUCTION);
		}
		slMfgStatusValues.sort();
	   return slMfgStatusValues;
	}
	
	/**
	 * Added for DTWPI-9
	 * Method would be used to generate XML for drop down values of mandatory attributes
	 * @param context
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws MatrixException 
	 * @throws TransformerException 
	 */
	public String generateXMLForMandatoryAttributes(Context context) throws IOException, ParserConfigurationException, MatrixException, TransformerException {
		VPLMIntegTraceUtil.trace(context, ">>>> START generateXMLForMandatoryAttributes method");
		String strMandatoryAttrXML="";
		String strDesignFor;
		
		PreferenceManagement prefMgmt=new PreferenceManagement(context);
		//1. fetch the picklist names for mandatory attributes
		String strMandatoryAttrPickList=getMandatoryAttributesList(context);
		
		if(UIUtil.isNotNullAndNotEmpty(strMandatoryAttrPickList)) {
			
			StringList slPreferenceNameValueList=new StringList();
			StringList slMandatoryAttributes=StringUtil.split(strMandatoryAttrPickList, DataConstants.SEPARATOR_PIPE);
			
			//2. fetch the preferences for mandatory attributes as per Design For value
			strDesignFor=prefMgmt.getPreferenceValue(context, DataConstants.PREFERENCE_DESIGN_FOR);
			if(UIUtil.isNullOrEmpty(strDesignFor))
				strDesignFor=DataConstants.CONSTANT_DESIGN_FOR_PACKAGING;
			VPLMIntegTraceUtil.trace(context, ">>>> strDesignFor:::"+strDesignFor);
			
			String strMandatoryAttrPreferences=getPreferenceNamesForMandatoryAttr(context,strDesignFor);
			
			if(UIUtil.isNotNullAndNotEmpty(strMandatoryAttrPreferences)) {
				StringList slPreferenceNames=StringUtil.split(strMandatoryAttrPreferences, DataConstants.SEPARATOR_PIPE);
				
				
				String strPreferenceNameValue=prefMgmt.getUserPreferenceValues(context, slPreferenceNames);
				slPreferenceNameValueList=StringUtil.split(strPreferenceNameValue, DataConstants.SEPARATOR_AT_THE_RATE);
				VPLMIntegTraceUtil.trace(context, ">>>> slPreferenceNameValueList::"+slPreferenceNameValueList);
			}
			
			//3. create root element of xml
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document xmlDoc = docBuilder.newDocument();
			
			Element rootElement=createRootElement(xmlDoc,"MandatoryAttributeValues");
					
			String strMandatoryAttribute;
			String strPrefValue="";
			String strPhase;
			StringList slFieldChoices;
			StringList slFieldDisplayChoices;
			Element attrElement;
			CacheManagement cacheMgmt=new CacheManagement(context);
			
			for(int i=0;i<slMandatoryAttributes.size();i++) {
				strMandatoryAttribute=slMandatoryAttributes.get(i);
				VPLMIntegTraceUtil.trace(context, ">>>> strMandatoryAttribute::"+strMandatoryAttribute);
				//4. create attribute tag
				attrElement=createAttributeElement(context,xmlDoc,strMandatoryAttribute,slPreferenceNameValueList,strDesignFor);
				rootElement.appendChild(attrElement);
				
				//5. get the drop down values for attribute
				if("LifeCycleStatus".equals(strMandatoryAttribute)) { 
					VPLMIntegTraceUtil.trace(context, ">>>> Inside lifecycle condition");
					
					strPhase=getPhasePreferenceForDesignFor(strDesignFor);
					VPLMIntegTraceUtil.trace(context, ">>>>  strPhase:::"+strPhase);
					
					strPrefValue=prefMgmt.getPreferenceValue(context,strPhase);
					VPLMIntegTraceUtil.trace(context, ">>>> strPrefValue::"+strPrefValue);
					
					slFieldChoices=getMfgStatusValue(context, strPrefValue);
					slFieldDisplayChoices=slFieldChoices;
				}else if("pgStructuredReleaseCriteriaRequired".equals(strMandatoryAttribute)) {
					slFieldChoices=JPO.invoke(context, DataConstants.CONST_DSOUTIL_JPO, null, DataConstants.CONST_BLANK_YESNO_METHOD,null,StringList.class);
					slFieldDisplayChoices=slFieldChoices;
				}else {
					Map mpReturnMap=(Map) cacheMgmt.getPickListItems(context, strMandatoryAttribute);
					slFieldChoices=(StringList) mpReturnMap.get(DataConstants.CONST_FIELD_CHOICES);
					slFieldDisplayChoices=(StringList) mpReturnMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES);
				}
				
				//create child elements for drop down values
				createChildElements(xmlDoc,attrElement,slFieldChoices,slFieldDisplayChoices);
			}
			//added logic for Lifecycle Status values for all Release Phases
			addAllMfgStatusValuesInXML(context,rootElement,xmlDoc);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
			Transformer transformer = transformerFactory.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(xmlDoc), new StreamResult(writer));

			strMandatoryAttrXML = writer.getBuffer().toString();
			VPLMIntegTraceUtil.trace(context, ">>>> strMandatoryAttrXML::"+strMandatoryAttrXML);
		}
		return strMandatoryAttrXML;
	}

	/**
	 * To get the phase preference key as per the Design For preference value
	 * @param strDesignFor
	 * @return strPhase
	 */
	private String getPhasePreferenceForDesignFor(String strDesignFor) {
		String strPhase="";
		
		if(UIUtil.isNotNullAndNotEmpty(strDesignFor)) {
			if(strDesignFor.contains(DataConstants.CONSTANT_DESIGN_FOR_PACKAGING) || strDesignFor.contains(DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION) || strDesignFor.contains(DataConstants.CONSTANT_DESIGN_FOR_AUTOMATION))
				strPhase=DataConstants.PREFERENCE_PACKAGING_PHASE;
			else if(strDesignFor.contains(DataConstants.CONSTANT_DESIGN_FOR_PRODUCT))
				strPhase=DataConstants.PREFERENCE_PRODUCT_PHASE;
			else
				strPhase=DataConstants.PREFERENCE_RAW_MATERIAL_PHASE;
		}
		return strPhase;
	}

	/**
	 * Method to add the xml tag for all the Mfg Status values, as per the Release Phase
	 * @param context
	 * @param rootElement
	 * @param xmlDoc
	 * @throws MatrixException
	 */
	private void addAllMfgStatusValuesInXML(Context context,Element rootElement,Document xmlDoc ) throws MatrixException{
		
		addMfgStatusValue(context,rootElement,xmlDoc,"MfgStatus_Development");
		addMfgStatusValue(context,rootElement,xmlDoc,"MfgStatus_Pilot");
		addMfgStatusValue(context,rootElement,xmlDoc,"MfgStatus_Production");
	}

	/**
	 * Method to add the xml tag for the Mfg Status, as per the Release Phase
	 * @param context
	 * @param rootElement
	 * @param xmlDoc
	 * @param strAttrName
	 * @throws MatrixException
	 */
	private void addMfgStatusValue(Context context,Element rootElement,Document xmlDoc,String strAttrName) throws MatrixException {
	
		String strReleasePhase="";
		if("MfgStatus_Development".equals(strAttrName))
			strReleasePhase=DataConstants.RELEASE_PHASE_DEVELOPMENT;
		else if("MfgStatus_Pilot".equals(strAttrName))
			strReleasePhase=DataConstants.RELEASE_PHASE_PILOT;
		else if("MfgStatus_Production".equals(strAttrName))
			strReleasePhase=DataConstants.RELEASE_PHASE_PRODUCTION;
		
		StringList slFieldChoices=getMfgStatusValue(context, strReleasePhase);
		
		Element attrElement=xmlDoc.createElement("attribute");
		attrElement.setAttribute(DomainConstants.SELECT_NAME,strAttrName);
		attrElement.setAttribute("savedDisplayValue",slFieldChoices.get(0));
		attrElement.setAttribute("savedValue",slFieldChoices.get(0));
		rootElement.appendChild(attrElement);
		
		createChildElements(xmlDoc,attrElement,slFieldChoices,slFieldChoices);
	}

	/**
	 * Method to create child elements for each drop down of attribute for MandatoryAttribute xml
	 * @param xmlDoc
	 * @param attrElement
	 * @param slFieldChoices
	 * @param slFieldDisplayChoices
	 */
	private void createChildElements(Document xmlDoc, Element attrElement,StringList slFieldChoices, StringList slFieldDisplayChoices) {
		Element itemElement;
		
		for(int i=0;i<slFieldChoices.size();i++) {
			itemElement=xmlDoc.createElement("item");
			
			itemElement.setAttribute("displayValue", slFieldDisplayChoices.get(i));
			itemElement.setAttribute("selectValue", slFieldChoices.get(i));
			attrElement.appendChild(itemElement);
		}
	}

	/**
	 * Method to  create xml tag for each mandatory attribute.
	 * @param context
	 * @param xmlDoc
	 * @param strMandatoryAttribute
	 * @param slPreferenceNameValueList
	 * @return Element
	 * @throws FrameworkException
	 * @throws IOException
	 */
	private Element createAttributeElement(Context context,Document xmlDoc,String strMandatoryAttribute, StringList slPreferenceNameValueList,String strDesignFor) 
			throws FrameworkException, IOException {
        
        String strPreferenceName=getPreferenceNameForAttribute(context,strMandatoryAttribute,strDesignFor);
        String strPrefValue=getValueForPreference(slPreferenceNameValueList,strPreferenceName);
        Element attrElement=xmlDoc.createElement("attribute");
        attrElement.setAttribute(DomainConstants.SELECT_NAME, strMandatoryAttribute);
		
		attrElement.setAttribute("savedValue", strPrefValue);
       
  		if((DataConstants.CONST_PICKLIST_REPORTEDFUNCTION.equals(strMandatoryAttribute) || 
  				DataConstants.CONST_PICKLIST_SEGMENT.equals(strMandatoryAttribute))&& UIUtil.isNotNullAndNotEmpty(strPrefValue)) {
  			//objectId is saved as the preference. Hence, added code to get the actual display value from the object
  				DomainObject doObj=DomainObject.newInstance(context,strPrefValue);
  				attrElement.setAttribute("savedDisplayValue", doObj.getInfo(context, DomainConstants.SELECT_NAME));
  				
  		}else {
  			attrElement.setAttribute("savedDisplayValue", strPrefValue);
  		}
		return attrElement;
	}

	/**
	 * Method to get the value for particular preference from the stringlist having name value pair for preferences saved on user
	 * @param slPreferenceNameValueList
	 * @param strPreferenceName
	 * @return String
	 */
	private String getValueForPreference(StringList slPreferenceNameValueList,String strPreferenceName) {
		String strPrefData="";
		 for(int i=0;i<slPreferenceNameValueList.size();i++) {
	    	   if(slPreferenceNameValueList.get(i).contains(strPreferenceName)) {
	    		   strPrefData=slPreferenceNameValueList.get(i);
	    		   break;
	    	   }
	       }
		 StringList slTempData=StringUtil.split(strPrefData, DataConstants.SEPARATOR_COLON);
		return slTempData.get(1);
	}

	/**
	 * Method to get the property from page. The property would return the preference name for particular attribute
	 * @param context
	 * @param strMandatoryAttribute
	 * @return String
	 * @throws FrameworkException
	 * @throws IOException
	 */
	private String getPreferenceNameForAttribute(Context context,String strMandatoryAttribute,String strDesignFor) throws FrameworkException, IOException {
		
		if(DataConstants.CONSTANT_DESIGN_FOR_PACKAGING.equals(strDesignFor) || DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION.equals(strDesignFor) || DataConstants.CONSTANT_DESIGN_FOR_AUTOMATION.equals(strDesignFor)) {
			strDesignFor=DataConstants.CONSTANT_DESIGN_FOR_PACKAGING;
		}else {
			strDesignFor=DataConstants.CONSTANT_DESIGN_FOR_PRODUCT;
		}
		StringBuilder sbKey=new StringBuilder();
		sbKey.append("pgDIPreferences.");
		sbKey.append(strMandatoryAttribute+".");
		sbKey.append(strDesignFor);
		sbKey.append(".PreferenceName");
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>Page file key::"+sbKey.toString());
		
		CacheManagement cacheMgmt=new CacheManagement(context);
		String strPreferenceName=cacheMgmt.getValueForProperty(context, DataConstants.STR_DESIGN_CONFIG_PAGE_FILENAME, sbKey.toString());
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>Preference Name::"+strPreferenceName+" for attribute::"+strMandatoryAttribute);
		return strPreferenceName;
	}

	/**
	 * Method to get the list of mandatory attributes from page property
	 * @param context
	 * @return String
	 * @throws IOException 
	 * @throws FrameworkException 
	 */
	private String getMandatoryAttributesList(Context context) throws FrameworkException, IOException {
		CacheManagement cacheMgmt=new CacheManagement(context);
		String strMandatoryAttrPickList=cacheMgmt.getValueForProperty(context, DataConstants.STR_DESIGN_CONFIG_PAGE_FILENAME, "pgDIPreferences.MandatoryAttributes.List");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>strMandatoryAttrPickList::"+strMandatoryAttrPickList);
		return strMandatoryAttrPickList;
	}
	
	/**
	 * Method to get the list of preferences for mandatory attributes from page property
	 * @param context
	 * @return String
	 * @throws IOException 
	 * @throws FrameworkException 
	 */
	private String getPreferenceNamesForMandatoryAttr(Context context,String strDesignFor) throws FrameworkException, IOException {
		String strMandatoryAttrPreferences;
		CacheManagement cacheMgmt=new CacheManagement(context);
		if(DataConstants.CONSTANT_DESIGN_FOR_PACKAGING.equals(strDesignFor) || DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION.equals(strDesignFor) || DataConstants.CONSTANT_DESIGN_FOR_AUTOMATION.equals(strDesignFor))
			strMandatoryAttrPreferences=cacheMgmt.getValueForProperty(context, DataConstants.STR_DESIGN_CONFIG_PAGE_FILENAME, "pgDIPreferences.MandatoryAttributes.Packaging.Preferences");
		else
			strMandatoryAttrPreferences=cacheMgmt.getValueForProperty(context, DataConstants.STR_DESIGN_CONFIG_PAGE_FILENAME, "pgDIPreferences.MandatoryAttributes.Product.Preferences");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>strMandatoryAttrPreferences::"+strMandatoryAttrPreferences);
		return strMandatoryAttrPreferences;
	}
	
	/**
	 * Method to create the root element
	 * @param xmlDoc
	 * @param strRootElementName
	 * @return Element
	 * @throws ParserConfigurationException
	 */
	private Element createRootElement(Document xmlDoc,String strRootElementName) {
		// root element
		Element rootElement = xmlDoc.createElement(strRootElementName);
		xmlDoc.appendChild(rootElement);
		return rootElement;
	}
	
	/**
	 * DTWPI-13
	 * Auto promotion of connected EC Part (Part Specification Relationship), as per the state of connected VPMReference object
	 * @param context
	 * @param args
	 * @throws Exception
	 */
	public void autoPromoteConnectedECPart(Context context,String strObjectId,String strType)
    {
		VPLMIntegTraceUtil.trace(context, "\n START of SyncManagement: autoPromoteConnectedECPart method");
    	String strECPartId = "";
    	String strECPartType = "";
    	String strECPartState="";
  
    	try
    	{
    		if(DataConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strType)){    			

    			StringList slSelects=new StringList(4);
    			slSelects.add(DomainConstants.SELECT_ID);
    			slSelects.add(DomainConstants.SELECT_CURRENT);
    			slSelects.add(DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
    			slSelects.add(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
    			
    			StringList slObjSelects=new StringList(3);
    			slObjSelects.add(DomainConstants.SELECT_ID);
    			slObjSelects.add(DomainConstants.SELECT_TYPE);
    			slObjSelects.add(DomainConstants.SELECT_CURRENT);
    			
    			DomainObject doVPMRefObj=DomainObject.newInstance(context,strObjectId);
    			Map mpVPMRefInfo=doVPMRefObj.getInfo(context, slSelects);
    			
    			VPLMIntegTraceUtil.trace(context, "mpVPMRefInfo::"+mpVPMRefInfo);
    				
    			String strVPMRefObjectId=(String)mpVPMRefInfo.get(DomainConstants.SELECT_ID);
    			String strVPMRefState=(String)mpVPMRefInfo.get(DomainConstants.SELECT_CURRENT);
    			String strVPMControl = (String)mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_ISVPLMCONTROLLED);
				String strCADDesignOrigination=(String)mpVPMRefInfo.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
    						
    			HashMap hmParam=new HashMap<>();
    			hmParam.put("objectId", strVPMRefObjectId);
    			hmParam.put("objSelects", slObjSelects);
    				
    			Map mpECPartInfo=JPO.invoke(context, STR_PROGNAME_CATIA_INTEGRATION, null, STR_METHODNAME_GET_ECPART_INFO, JPO.packArgs(hmParam), Map.class);
    			
    			VPLMIntegTraceUtil.trace(context, "mpECPartInfo::"+mpECPartInfo);
    				
    			if (null!=mpECPartInfo && !mpECPartInfo.isEmpty())
    			{
    					strECPartId = (String)mpECPartInfo.get(DomainConstants.SELECT_ID);
    					strECPartType=(String)mpECPartInfo.get(DomainConstants.SELECT_TYPE);
    					strECPartState=(String)mpECPartInfo.get(DomainConstants.SELECT_CURRENT);
    					
    					if(!DataConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strECPartType) && !DataConstants.STATE_RELEASE.equals(strECPartState)
    							&& !DataConstants.STATE_OBSOLETE.equals(strECPartState) && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strVPMControl) 
    							&& DataConstants.RANGE_VALUE_MANUAL.equalsIgnoreCase(strCADDesignOrigination) && DataConstants.STATE_RELEASED.equalsIgnoreCase(strVPMRefState))
    						{
	    						//promote the EC Part to Release state
	    						DomainObject doECPart=DomainObject.newInstance(context,strECPartId);
	    						doECPart.setState(context, DomainConstants.STATE_PART_RELEASE);
	    						VPLMIntegTraceUtil.trace(context, "autoPromoteConnectedECPart:: EC Part promoted to Release state");
    					}
    				}
    			}
    		}
    		catch (Exception e)	{
	    		VPLMIntegTraceUtil.trace(context, e.getMessage());
    		}
    }

	/**
	 * Method invoked from webservice. This method would return the TNR of the object whose physicalid is passed
	 * @param context
	 * @param strPhysicalId
	 * @return String
	 * @throws FrameworkException 
	 */
	public String getObjectTNRFromPhysicalId(Context context,String strPhysicalId) throws FrameworkException {
		String strResult="";
		VPLMIntegTraceUtil.trace(context, ">>>> START of SyncMgmt:getObjectTNRFromPhysicalId method");
		
		String strWhereClause="physicalid == "+strPhysicalId;
		VPLMIntegTraceUtil.trace(context, ">>>> strWhereClause::"+strWhereClause);
		
		StringList slSelects=new StringList(3);
		slSelects.add(DomainConstants.SELECT_TYPE);
		slSelects.add(DomainConstants.SELECT_NAME);
		slSelects.add(DomainConstants.SELECT_REVISION);
		
		CommonUtility commonUtility=new CommonUtility(context);
		MapList mlObjDetails=commonUtility.findObjectWithWhereClause(context, DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,
				DomainConstants.QUERY_WILDCARD,strWhereClause, slSelects);
		
		if(!mlObjDetails.isEmpty()) {
			Map mpObjDetails=(Map)mlObjDetails.get(0);
			StringBuilder sbResult=new StringBuilder();
			sbResult.append(mpObjDetails.get(DomainConstants.SELECT_TYPE));
			sbResult.append(DataConstants.SEPARATOR_PIPE);
			sbResult.append(mpObjDetails.get(DomainConstants.SELECT_NAME));
			sbResult.append(DataConstants.SEPARATOR_PIPE);
			sbResult.append(mpObjDetails.get(DomainConstants.SELECT_REVISION));
			
			strResult=sbResult.toString();
		}else {
			strResult=DataConstants.OBJECT_DOES_NOT_EXIST;
		}
		VPLMIntegTraceUtil.trace(context, ">>>> strResult::"+strResult);
		return strResult;
	}
}
