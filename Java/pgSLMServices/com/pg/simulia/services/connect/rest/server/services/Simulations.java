package com.pg.simulia.services.connect.rest.server.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import matrix.db.Context;
import matrix.util.StringList;

import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.smaslm.common.util.W3CUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.FrameworkException;
//Added for incorporating current as Basic field in the rest API - JIRA (CAD) DSM15X4-93 STARTS
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.library.LibraryCentralConstants;
//Added for incorporating current as Basic field in the rest API - JIRA (CAD) DSM15X4-93 ENDS

//DSM(DS)2018x.0 - Fix for ALM 24069 IP Control Class Webservice - Handle IPSecurity - START
import java.util.Map;
import com.matrixone.apps.exportcontrol.ExportControlConstants;
import com.matrixone.jdom.JDOMException;
import com.matrixone.servlet.Framework;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
//DSM(DS)2018x.0 - Fix for ALM 24069 IP Control Class Webservice - Handle IPSecurity - END
import com.pg.designtools.datamanagement.DataConstants;


@Path("/simulations")
@Produces({ "application/xml" })
public class Simulations extends RestService {
	
	protected matrix.db.Context context; 

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GET
    @Path("/findAdvanced")
    @Produces({ "application/ds-json", "application/xml" })
    public Response findAdvanced(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("<SearchCriteriaList></SearchCriteriaList>") @QueryParam("criteriaXML") String criteriaXML) {

    	return Response.status(500).entity("This webservice is deprecated. Please use the POST webservice for findAdvanced").build();
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GET
    @Path("/findAll")
    @Produces({ "application/ds-json", "application/xml" })
    public Response findAll(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("<SearchCriteriaList></SearchCriteriaList>") @QueryParam("criteriaXML") String criteriaXML) {

    	return Response.status(500).entity("This webservice is deprecated. Please use the POST webservice for findAll").build();
    } 
    
//DSM(DS)2018x.0 - Fix for ALM 24069 IP Control Class Webservice - Handle IPSecurity - START
/***
 * 
 * @param request
 * @param criteriaXML
 * @return
 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GET
    @Path("/findIPClass")
    @Produces({ "application/ds-json", "application/xml" })
    public Response findIPClass(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("<SearchCriteriaList></SearchCriteriaList>") @QueryParam("criteriaXML") String criteriaXML) {
    
		return Response.status(500).entity("This webservice is deprecated. Please use the POST webservice for findIPClass").build();
    }
	
	@POST
	@Path("/findAdvanced")
	/**
	 * This Web Service is called for EBOM Sync Operation. 
	 * @param request
	 * @param incomingData
	 * @return
	 * @throws Exception
	 */
	public Response findAdvanced(@javax.ws.rs.core.Context HttpServletRequest request)
	{				
		context = Framework.getContext(request.getSession(false)); 
		VPLMIntegTraceUtil.trace(context, ">> START of findAdvanced POST webservice");
		String strResult;
		try {
	
			String strInputParam=Utility.getInputJSON(request);
		
			VPLMIntegTraceUtil.trace(context, ">>Input JSON::"+strInputParam);
			
			String searchCriteriaXML=Utility.convertJSONArrayToXML(strInputParam);
			VPLMIntegTraceUtil.trace(context, ">> xml format of json::"+searchCriteriaXML);
			
			HashMap<String, String> criteriaMap=Utility.convertsearchCriteriaXML(context, searchCriteriaXML);
			VPLMIntegTraceUtil.trace(context, ">> criteriaMap::"+criteriaMap);
			
			Document resultDoc= W3CUtil.newDocument();
			
			MapList resultsMapList=findAdvancedLogic(criteriaMap);
			
			strResult=generateResult(resultsMapList,resultDoc,"findAdvanced");
			
			VPLMIntegTraceUtil.trace(context, ">> findAdvanced POST strResult::"+strResult);
			
			if(UIUtil.isNullOrEmpty(strResult)) {
				strResult=Utility.saveXml(resultDoc, true);
			}
			VPLMIntegTraceUtil.trace(context, ">> findAdvanced POST final strResult::"+strResult);
		}catch(Exception ex)
		{
			VPLMIntegTraceUtil.trace(context, ">>> Inside catch of POST findAdvanced method::"+ex.getMessage());	
			return Response.status(500).entity(ex.getMessage()).build();	
		}
		return Response.ok(strResult).build();
	}
	
	@POST
	@Path("/findAll")
	public Response findAll(@javax.ws.rs.core.Context HttpServletRequest request)
	{				
		context = Framework.getContext(request.getSession(false)); 
		VPLMIntegTraceUtil.trace(context, ">> START of findAll POST webservice");
		String strResult;
		try {
		
			String strInputParam=Utility.getInputJSON(request);
		
			VPLMIntegTraceUtil.trace(context, ">>Input JSON::"+strInputParam);
			
			String searchCriteriaXML=Utility.convertJSONArrayToXML(strInputParam);
			VPLMIntegTraceUtil.trace(context, ">> xml format of json::"+searchCriteriaXML);
			
			HashMap<String, String> criteriaMap=Utility.convertsearchCriteriaXML(context, searchCriteriaXML);
			VPLMIntegTraceUtil.trace(context, ">> criteriaMap::"+criteriaMap);
			
			Document resultDoc= W3CUtil.newDocument();
			
			MapList resultsMapList=findAllLogic(criteriaMap);
			VPLMIntegTraceUtil.trace(context, ">> resultsMapList::"+resultsMapList);
			
			if(resultsMapList.get(0) instanceof String) {
				strResult=(String) resultsMapList.get(0);
				return Response.status(500).entity(strResult).build();	
			}
			else {	
				
				strResult=generateResult(resultsMapList,resultDoc,"findAll");
				
				VPLMIntegTraceUtil.trace(context, ">> findAll POST strResult::"+strResult);
				
				if(UIUtil.isNullOrEmpty(strResult)) {
					strResult=Utility.saveXml(resultDoc, true);
				}
			}
			VPLMIntegTraceUtil.trace(context, ">> findAll POST final strResult::"+strResult);
		
		}catch(Exception ex)
		{
			VPLMIntegTraceUtil.trace(context, ">>> Inside catch of POST findAll method::"+ex.getMessage());	
			return Response.status(500).entity(ex.getMessage()).build();	
		}
		
		return Response.ok(strResult).build();
	}

	@POST
	@Path("/findIPClass")
	public Response findIPClass(@javax.ws.rs.core.Context HttpServletRequest request)
	{				
		context = Framework.getContext(request.getSession(false)); 
		VPLMIntegTraceUtil.trace(context, ">> START of findIPClass POST webservice");
		String strResult;
		try {
			String strInputParam=Utility.getInputJSON(request);
		
			VPLMIntegTraceUtil.trace(context, ">>Input JSON::"+strInputParam);
			
			String searchCriteriaXML=Utility.convertJSONArrayToXML(strInputParam);
			VPLMIntegTraceUtil.trace(context, ">> xml format of json::"+searchCriteriaXML);
			
			HashMap<String, String> criteriaMap=Utility.convertsearchCriteriaXML(context, searchCriteriaXML);
			VPLMIntegTraceUtil.trace(context, ">> criteriaMap::"+criteriaMap);
			
			Document resultDoc= W3CUtil.newDocument();
			
			StringList slCompleteIDNameList=findIPClassLogic(criteriaMap);
			VPLMIntegTraceUtil.trace(context, ">> slCompleteIDNameList::"+slCompleteIDNameList);
			
			strResult=generateResultForFindIPClassWS(slCompleteIDNameList, resultDoc);
				
			VPLMIntegTraceUtil.trace(context, ">> findIPClass POST strResult::"+strResult);
				
			if(UIUtil.isNullOrEmpty(strResult)) {
					strResult=Utility.saveXml(resultDoc, true);
			}
			VPLMIntegTraceUtil.trace(context, ">> findIPClass POST final strResult::"+strResult);
		
		}catch(Exception ex)
		{
			VPLMIntegTraceUtil.trace(context, ">>> Inside catch of POST findIPClass method::"+ex.getMessage());	
			return Response.status(500).entity(ex.getMessage()).build();	
		}
		
		return Response.ok(strResult).build();
	}
    
    /**
     * Method added for DTCLD-742. This method would generate the maps of key value pairs sent as query parameters for the webservice
     * @param searchString
     * @return MapList
     * @throws Exception
     */
	   private MapList generateInputForCriteriaXML(String searchString) throws Exception {
			MapList mlMap=new MapList();
			
		    if(UIUtil.isNotNullAndNotEmpty(searchString)) {
		    	StringList slSearchCriterias=StringUtil.splitString(searchString, DataConstants.SEPARATOR_TILDE);
		    	
		    	String strSearchCriteria;
				StringList slCriteria;
				Map mpInputParam=new HashMap();
				StringList slSearchCriteria;
				
		    	for(int j=0;j<slSearchCriterias.size();j++) {
		    		mpInputParam=new HashMap<>();
					slSearchCriteria=StringUtil.splitString(slSearchCriterias.get(j), "_AND_");
					
					for(int i=0;i<slSearchCriteria.size();i++) {
						
						strSearchCriteria=slSearchCriteria.get(i);
						
						slCriteria=StringUtil.splitString(strSearchCriteria, DataConstants.SEPARATOR_COLON);
						
						mpInputParam.put(slCriteria.get(0), slCriteria.get(1));
					}
					mlMap.add(mpInputParam);
		    	}
		    }
		    return mlMap;
	    }

	/**
	 * Method added for DTCLD-742. This method would create the xml for the web services
	 * @param mlInputParam
	 * @return String
	 */
	private String generateCriteriaXML(MapList mlInputParam) {
			
		StringBuilder criteriaXML=new StringBuilder("<SearchCriteriaList>");
		
		if( null!=mlInputParam && !mlInputParam.isEmpty()) {
			Map mpParam;
			String sValue;
			Iterator keyIterator;
			Iterator valueIterator;
			
			for(int i=0;i<mlInputParam.size();i++) {
				criteriaXML.append("<SearchCriteria ");
				mpParam=(Map)mlInputParam.get(i);
				
				keyIterator = mpParam.keySet().iterator(); 
				valueIterator = mpParam.values().iterator(); 
				
				while (keyIterator.hasNext()) {
					criteriaXML.append(keyIterator.next());
				
					sValue=(String) valueIterator.next();
					if(! sValue.contains("\""))
						criteriaXML.append("=\"");
					else
						criteriaXML.append("=");
					
					criteriaXML.append(sValue);
					
					if(! sValue.contains("\""))
						criteriaXML.append("\" ");
					else
						criteriaXML.append(" ");
				}
				criteriaXML.append("></SearchCriteria>");
			}
		}
		criteriaXML.append("</SearchCriteriaList>");
		return criteriaXML.toString();
		}
    
    /***
     * 
     * @param context
     * @param strobjType
     * @param strPolicy
     * @return
     * @throws FrameworkException
     * @throws JDOMException
     * @throws IOException
     */
    public Map getTypeAllowedClassificationForTypePolicy(Context context, String strobjType) throws FrameworkException, JDOMException, IOException {
		String strPageIPClassContent = MqlUtil.mqlCommand(context, "print page $1 select content dump", "pgIPClassTypePolicyMapping");
		com.matrixone.jdom.input.SAXBuilder localSAXBuilder = new com.matrixone.jdom.input.SAXBuilder();
		com.matrixone.jdom.Document document = null;
		HashMap<String, Object> mapIPClass = new HashMap<>();
		if (strPageIPClassContent != null && !"".equals(strPageIPClassContent.trim())) {
			document = localSAXBuilder.build(new StringReader(strPageIPClassContent));
			com.matrixone.jdom.xpath.XPath xpath = com.matrixone.jdom.xpath.XPath.newInstance("/SECUIRTY_CLASS_MAPPING/TYPE[@name='" + strobjType + "']");
			com.matrixone.jdom.Element elemType = (com.matrixone.jdom.Element) xpath.selectSingleNode(document);
			String strIPClass = elemType.getAttribute("ipclass").getValue();
			String strDefault = elemType.getAttribute("default").getValue();
			mapIPClass.put("ipclass", StringUtil.split(strIPClass, ","));
			mapIPClass.put("default", strDefault);
		}
		return mapIPClass;
	}
    
	/**
	 * Added the code to implement search for Restricted/Highly Restricted Objects based on the Classification Selected.<br>
	 * This method caches the computed data for later retrieval. It also updates the cached timestamp which is later used to check the expiry of the
	 * cached info.
	 * 
	 * @param context
	 *            The enovia Context Object
	 * @param strObjName
	 *            String whose value can be RANGE_HIGHLYRESTRICTED or RANGE_RESTRICTED
	 * @param cacheMap
	 *            Map containing cached data and timestamp of cached data
	 * @return a Map containing the OIDs of the IP Classes of categories - Business Use OR Highly Restricted
	 * @throws Exception
	 *             when operation fails
	 */
	 //method signature modified for ALM#24069 IP Find Class Web Call not Returning all expected (DT18X2-181) by Msh7
	private Map<String, Object> reloadCacheForIPClassSearch(Context context, String strObjName) {
		StringList includeIdList = new StringList();
		StringList includeIdNameList = new StringList();
		Map<String, Object> cacheMap = new HashMap<>();
		try {
			String strLibraryObjID = null;
			String strId = null;
			String strECCStateActive = PropertyUtil.getSchemaProperty(context, "policy", ExportControlConstants.POLICY_EXC_CLASSIFICATION,
					"state_Active"); 
			String ATTRIBUTE_PGIPLIBRARYCLASSIFICATION = PropertyUtil.getSchemaProperty(context,"attribute_pgIPLibraryClassification");
			String CACHE_MAP_KEY_ID_NAME = "_idname";
			DomainObject domLibObj = null;
			String hasFromConnectAccess = null;
			String strCurrent = null;
			MapList mIPControlList ;
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_NAME);
			busSelects.add(DomainConstants.SELECT_ID);
			busSelects.add(DomainConstants.SELECT_CURRENT);
			String strWhere = "attribute[" + ATTRIBUTE_PGIPLIBRARYCLASSIFICATION + "]=='" + strObjName + "'";
			// Platform Security 2022x Upgrade : Start : Change type from "Export Control Library" to "IP Protection Library"
			MapList mObjectList = DomainObject.findObjects(context, 						//context
					ExportControlConstants.TYPE_IP_PROTECTION_LIBRARY,               //type pattern
					DataConstants.VAULT_ESERVICE_PRODUCTION,                            //vault pattern
					strWhere,                                                                                        //where clause
					busSelects);                                                                                    //object selects
			//Platform Security 2022x Upgrade : End

			boolean bCheckFromConnectAccess=validateFromConnectAccess(strObjName);
			if (bCheckFromConnectAccess) {
				busSelects.add("current.access[fromconnect]");
			}
			Iterator<?> itrList = mObjectList.iterator();
			Map<?, ?> mapObject = null;
			Map<?, ?> mapIPClass = null;
			Iterator<?> itrClassList = null;
			while (itrList.hasNext()) {
				mapObject = (Map<?, ?>) itrList.next();
				strLibraryObjID = (String) mapObject.get(DomainConstants.SELECT_ID);
				if (UIUtil.isNotNullAndNotEmpty(strLibraryObjID)) {
					domLibObj = DomainObject.newInstance(context, strLibraryObjID);
					mIPControlList = domLibObj.getRelatedObjects(context, LibraryCentralConstants.RELATIONSHIP_SUBCLASS, ExportControlConstants.TYPE_IP_CONTROL_CLASS, busSelects,
							null, false, true, (short) 0, null, null, 0);
					itrClassList = mIPControlList.iterator();
					while (itrClassList.hasNext()) {
						mapIPClass = (Map<?, ?>) itrClassList.next();
						strId = (String) mapIPClass.get(DomainConstants.SELECT_ID);
						strCurrent = (String) mapIPClass.get(DomainConstants.SELECT_CURRENT);
						if (UIUtil.isNotNullAndNotEmpty(strId) && strECCStateActive.equals(strCurrent)) {
							if (bCheckFromConnectAccess) {
								hasFromConnectAccess = (String) mapIPClass.get("current.access[fromconnect]");
								if (hasFromConnectAccess.equalsIgnoreCase("TRUE")) {
									includeIdList.add(strId);
									includeIdNameList.add(strId+"="+(String) mapIPClass.get(DomainConstants.SELECT_NAME));
								}
							} else {
								includeIdList.add(strId);
								includeIdNameList.add(strId+"="+(String) mapIPClass.get(DomainConstants.SELECT_NAME));
							}
						}
					}
				}
			}
			cacheMap.put(strObjName + CACHE_MAP_KEY_ID_NAME, includeIdNameList);

		} catch (Exception e) {
				VPLMIntegTraceUtil.trace(context, ">> Inside catch of reloadCacheForIPClassSearch::"+e.getMessage());
		}
		return cacheMap;
	}
	
	/**
	 * Method to validate whether from connect access is present for IP Control classes
	 * @param strObjName
	 * @return boolean
	 * @throws FrameworkException
	 */
	private boolean validateFromConnectAccess(String strObjName) throws FrameworkException {
		boolean bCheckFromConnectAccess=false;
		String ATTRIBUTE_PGSECURITYEMPLOYEETYPE= PropertyUtil.getSchemaProperty(context,"attribute_pgSecurityEmployeeType");
		String SELECT_ATTRIBUTE_EMPLOYEETYPE = "attribute[" + ATTRIBUTE_PGSECURITYEMPLOYEETYPE + "]";
		String ATTRIBUTE_PGEMPLOYEE_NONEMPLOYEE = "Non-Emp";
		
		StringList slBusSelects = new StringList(1);
		slBusSelects.add(SELECT_ATTRIBUTE_EMPLOYEETYPE);
		
		if (strObjName.equals(DataConstants.RANGE_RESTRICTED)) {
			String strLoggedInUser = context.getUser();
			String personOID = PersonUtil.getPersonObjectID(context, strLoggedInUser);
			
			if (UIUtil.isNotNullAndNotEmpty(personOID)) {
				DomainObject dobPerson = DomainObject.newInstance(context, personOID);
				
				Map<?, ?> personInfoMap = dobPerson.getInfo(context, slBusSelects);
				String strEmployeeType = (String) personInfoMap.get(SELECT_ATTRIBUTE_EMPLOYEETYPE);
				
				if (UIUtil.isNotNullAndNotEmpty(strEmployeeType)	&& ATTRIBUTE_PGEMPLOYEE_NONEMPLOYEE.equals(strEmployeeType)) {
					bCheckFromConnectAccess = true;
				}
			}
		} else {
			bCheckFromConnectAccess = true;
		}
		return bCheckFromConnectAccess;
	}

	/**
	 * Method would generate the result for the findAdvanced and findAll webservices
	 * @param resultsMapList
	 * @param resultDoc
	 * @param strWebServiceName
	 * @return result
	 */
	private String generateResult(MapList resultsMapList, Document resultDoc, String strWebServiceName) {
		String strResult="";
		Element simListEl = resultDoc.createElement("SearchResults");
		
		if(null != resultsMapList && resultsMapList.isEmpty()){
			strResult= "No results found";
        }
        if(null != resultsMapList && resultsMapList.size() >= 500){
                simListEl.setAttribute("limit", "500");
                simListEl.setAttribute("limitReached", "true");
                resultDoc.appendChild(simListEl);
        }
        else{
                simListEl.setAttribute("limit", "500");
                simListEl.setAttribute("limitReached", "false");
                resultDoc.appendChild(simListEl);                
        }
            
        if(null != resultsMapList && !resultsMapList.isEmpty()){
            Iterator resultsItr = resultsMapList.iterator();
            HashMap<String, String> resultsMap;
            String simOID;
            while(resultsItr.hasNext()){
                resultsMap = (HashMap<String, String>)resultsItr.next();
                simOID =resultsMap.get(DomainConstants.SELECT_ID);
               
                if("findAdvanced".equals(strWebServiceName)) {
                	Element simEl = W3CUtil.newElement(simListEl, "Simulation");
                	simEl.setAttribute("objectId", simOID);
                    simEl.setAttribute("title", resultsMap.get("attribute[" + DataConstants.ATTR_TITLE + "]"));
                    simEl.setAttribute("name", resultsMap.get(DomainConstants.SELECT_NAME));
                    simEl.setAttribute("description", resultsMap.get(DomainConstants.SELECT_DESCRIPTION));
                    simEl.setAttribute("owner", resultsMap.get(DomainConstants.SELECT_OWNER));
                    simEl.setAttribute("originated", resultsMap.get(DomainConstants.SELECT_ORIGINATED));
                    simEl.setAttribute("modified", resultsMap.get(DomainConstants.SELECT_MODIFIED));
                    
                }else if("findAll".equals(strWebServiceName)) {
                	Element simEl = W3CUtil.newElement(simListEl, "Object");
                	simEl.setAttribute("objectId", simOID);
                    simEl.setAttribute("name", resultsMap.get(DomainConstants.SELECT_NAME));
                    simEl.setAttribute("type",resultsMap.get(DomainConstants.SELECT_TYPE));
                    simEl.setAttribute("description", resultsMap.get(DomainConstants.SELECT_DESCRIPTION));
                    simEl.setAttribute("owner",resultsMap.get(DomainConstants.SELECT_OWNER));
                    simEl.setAttribute("originated", resultsMap.get(DomainConstants.SELECT_ORIGINATED));
                    simEl.setAttribute("modified", resultsMap.get(DomainConstants.SELECT_MODIFIED));
                }
            }
        }
		return strResult;
	}
	
	/**
	 * Method to generate result for findIPClass web service
	 * @param slCompleteIDNameList
	 * @param resultDoc
	 * @return strResult
	 */
	private String generateResultForFindIPClassWS(StringList slCompleteIDNameList,Document resultDoc) {
		String strResult="";
		Element simListEl = resultDoc.createElement("SearchResults");
		
		if(null != slCompleteIDNameList && slCompleteIDNameList.isEmpty()){
			strResult="No results found";
			}
			
			if(null != slCompleteIDNameList && slCompleteIDNameList.size() >= 500){
				simListEl.setAttribute("limit", "500");
				simListEl.setAttribute("limitReached", "true");
				resultDoc.appendChild(simListEl);
			}
			else{
				simListEl.setAttribute("limit", "500");
				simListEl.setAttribute("limitReached", "false");
				resultDoc.appendChild(simListEl);                
			}
			
			if (null != slCompleteIDNameList && !slCompleteIDNameList.isEmpty()) {
				String strIPClassIDName = "";
				String strIPClassName="";
				
				for (int i = 0; i < slCompleteIDNameList.size(); i++) {
					strIPClassIDName =  slCompleteIDNameList.get(i);
					String[] arrIPClassName = strIPClassIDName.split("=");
					strIPClassName = arrIPClassName[1];
					Element simEl = W3CUtil.newElement(simListEl, "Simulation");
					simEl.setAttribute("IPClass"+i, strIPClassName);
				}
			}
			return strResult;
	}

	/**
	 * Method having common logic for findAdvanced webservice
	 * @param criteriaMap
	 * @return MapList
	 * @throws FrameworkException
	 */
	private MapList findAdvancedLogic(HashMap<String, String> criteriaMap) throws FrameworkException {
		 StringBuilder objectWhere = new StringBuilder();
		 short objectLimit = (short)500;
         String namePattern = "*";
         String revPattern = "*";
         String ownerPattern = "*";
         
         String name = criteriaMap.get("NAME");
         criteriaMap.remove("NAME");
         if(UIUtil.isNotNullAndNotEmpty(name)){
                 namePattern = name;
         }
         
         String owner = criteriaMap.get("OWNER");
         criteriaMap.remove("OWNER");
         if(UIUtil.isNotNullAndNotEmpty(owner)){
                 ownerPattern = owner;
         }
         
         String originated = criteriaMap.get("ORIGINATED");
         criteriaMap.remove("ORIGINATED");
         if(UIUtil.isNotNullAndNotEmpty(originated)){
             objectWhere.append("Originated >= const'");
             objectWhere.append(originated);
             objectWhere.append(" 00:00:00 AM'");
         }
         
         java.util.Set criteriaKeySet = criteriaMap.keySet();
         Iterator criteriaItr = criteriaKeySet.iterator();
         while(criteriaItr.hasNext()){
             String criteriaName = (String)criteriaItr.next();
             String criteriaVal = criteriaMap.get(criteriaName);
             if(null != criteriaVal && !criteriaVal.equals("")){
                 if(objectWhere.length() > 0){
                     objectWhere.append(" && ");
                 }
                 objectWhere.append(criteriaName).append(" ~~ '*").append(criteriaVal).append("*'");
             }
         }
         VPLMIntegTraceUtil.trace(context, ">>> findAdvanced objectWhere:::"+objectWhere.toString());
         
         return DomainObject.findObjects(context,		//context
                 DataConstants.TYPE_SIMULATION,      //typePattern
                 namePattern,											  //name Pattern
                 revPattern,                                              //rev Pattern
                 ownerPattern,                                         //owner Pattern
                 DataConstants.VAULT_ESERVICE_PRODUCTION, //vault Pattern
                 objectWhere.toString(),                          //where clause
                 null,                                                         // query name to save results
                 false,                                                       //expand Type
                 getObjectSelectables(),                        //object selectables
                 objectLimit);                                         //object limit
	}

	 /**
	  * Method to get the selectables of the object
	  * @return StringList
	  */
	private StringList getObjectSelectables() {
        StringList objectSelects = new StringList(9);
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(DomainConstants.SELECT_TYPE);
        objectSelects.add(DomainConstants.SELECT_NAME);
        objectSelects.add("attribute["+DataConstants.ATTR_TITLE+"]");
        objectSelects.add(DomainConstants.SELECT_OWNER);
        objectSelects.add(DomainConstants.SELECT_CURRENT);
        objectSelects.add(DomainConstants.SELECT_ORIGINATED);
        objectSelects.add(DomainConstants.SELECT_MODIFIED);
        objectSelects.add(DomainConstants.SELECT_DESCRIPTION);
        
        return objectSelects;
	}
	
	/**
	 * generic method having logic for findAll web service
	 * @param criteriaMap
	 * @return MapList
	 * @throws FrameworkException
	 */
	private MapList findAllLogic(HashMap<String,String>criteriaMap) throws FrameworkException {
		MapList mlData=new MapList();
		
		String STR_STATE_ACTIVE = FrameworkUtil.lookupStateName(context, DomainConstants.POLICY_PROJECT, "state_Active");
		 StringBuilder objectWhere = new StringBuilder();
         objectWhere.append("policy != Version");
		 
        short objectLimit = (short)500;
        String typePattern = "*";
        String namePattern = "*";
        String revPattern = "*";
        String ownerPattern = "*";
        
        String type = criteriaMap.get("TYPE");
        criteriaMap.remove("TYPE");
        if(null != type){
            type = type.replaceAll("\\*", "");
            type = type.replaceAll("\\?", "");
            if(!type.equals("")){
                typePattern = type;
            }
            else{
                mlData.add("Bad TYPE Value");
            }
        }
        else{
        	mlData.add("Bad TYPE Value");
        }            
    
        if(mlData.isEmpty()) {
	        String name = criteriaMap.get("NAME");
	        criteriaMap.remove("NAME");
	        if(UIUtil.isNotNullAndNotEmpty(name)){
	                namePattern = name;
	        }
	        
	        String owner = criteriaMap.get("OWNER");
	        criteriaMap.remove("OWNER");
	        if(UIUtil.isNotNullAndNotEmpty(owner)){
	                ownerPattern = owner;
	        }
	        //Added for incorporating current as Basic field in the rest API - JIRA (CAD) DSM15X4-93 STARTS      
	       String current = criteriaMap.get("CURRENT");
	       criteriaMap.remove("CURRENT");
	       if (UIUtil.isNotNullAndNotEmpty(current))
	       {
	             if (objectWhere.length() > 0) {
	                  objectWhere.append(" && ");
	             }
	            objectWhere.append("current == '");
	            objectWhere.append(current);
	            objectWhere.append("'");
	       } else {
	            if(UIUtil.isNotNullAndNotEmpty(type) && DomainConstants.TYPE_WORKSPACE.equalsIgnoreCase(type)) {
	                   if (objectWhere.length() > 0) {
	                          objectWhere.append(" && ");
	                   }
	                   objectWhere.append("current == '");
	                   objectWhere.append(STR_STATE_ACTIVE);
	                   objectWhere.append("'");
	         }
	     }
	     //Added for incorporating current as Basic field in the rest API - JIRA (CAD) DSM15X4-93 ENDS
	     String originated = criteriaMap.get("ORIGINATED");
	    criteriaMap.remove("ORIGINATED");
	    if(UIUtil.isNotNullAndNotEmpty(originated)){
	        if(objectWhere.length() > 0){
	            objectWhere.append(" && ");
	        }
	        objectWhere.append("Originated >= const'");
	        objectWhere.append(originated);
	        objectWhere.append(" 00:00:00 AM'");
	    }            
	    
	    java.util.Set criteriaKeySet = criteriaMap.keySet();
	    Iterator criteriaItr = criteriaKeySet.iterator();
	    while(criteriaItr.hasNext()){
	        String criteriaName = (String)criteriaItr.next();
	        String criteriaVal = criteriaMap.get(criteriaName);
	        if(null != criteriaVal && !criteriaVal.equals("")){
	            if(objectWhere.length() > 0){
	                objectWhere.append(" && ");
	            }
	            objectWhere.append(criteriaName).append(" ~~ '*").append(criteriaVal).append("*'");
	        }
	    }
	
	     mlData=DomainObject.findObjects(context, //context
	            typePattern,				//typePattern
	            namePattern,             //namePattern
	            revPattern,                 // revision pattern
	            ownerPattern,           //owner Pattern
	            DataConstants.SEPARATOR_STAR,	//vault pattern
	            objectWhere.toString(),  //where clause
	            null,									//query name to save results	
	            true,							//expandType if true, the query should find subtypes of the given types
	            getObjectSelectables(),		// object selectables
	            objectLimit); 						//object limit
        }
     
     return mlData;
	}
	
	/**
	 * Generic method having logic to find IP Classes
	 * @param criteriaMap
	 * @return StringList
	 * @throws IOException 
	 * @throws JDOMException 
	 * @throws FrameworkException 
	 */
	private StringList findIPClassLogic(HashMap<String,String>criteriaMap) throws FrameworkException, JDOMException, IOException {
		String CACHE_MAP_KEY_ID_NAME = "_idname";
		StringList slCompleteIDNameList = new StringList();
		
		//Getting the Security Classification based on Type 
		String strType = criteriaMap.get("TYPE");
		if (UIUtil.isNotNullAndNotEmpty(strType)) {
			
			String strTempType = strType;
			if (!strType.startsWith("type_")) {
				try {
					strType = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, strType, true);
				} catch (Exception exp) {
					strType = strTempType;
				}
			}
			
			String sDefaultValue = criteriaMap.get("DEFAULT");
			
			Map mapTypeClass = getTypeAllowedClassificationForTypePolicy(context, strType);
			
			String strDefaultClassificationForType = "";
			String strIPClassificationForType = "";
			StringList slClassificationForTypeList = new StringList();
			
			if(UIUtil.isNullOrEmpty(sDefaultValue) || DataConstants.CONSTANT_FALSE.equalsIgnoreCase(sDefaultValue)){
				slClassificationForTypeList = (StringList) mapTypeClass.get("ipclass");
			} else if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(sDefaultValue)){
				strDefaultClassificationForType = (String) mapTypeClass.get("default");
			}
			
			if(slClassificationForTypeList.isEmpty()){
				slClassificationForTypeList.add(strDefaultClassificationForType);
			}
			
			int iClassificationForTypeListSize = slClassificationForTypeList.size();
			if (iClassificationForTypeListSize>0) {
				
				Map<String, Object> personIPClassCacheMap;
				StringList slIncludeIDNameList;
				
				for(int i=0;i<iClassificationForTypeListSize;i++){
					strIPClassificationForType = slClassificationForTypeList.get(i);
					personIPClassCacheMap = reloadCacheForIPClassSearch(context, strIPClassificationForType);
					slIncludeIDNameList = (StringList) personIPClassCacheMap.get(strIPClassificationForType + CACHE_MAP_KEY_ID_NAME);
					if(!slIncludeIDNameList.isEmpty()){
						for(int j=0;j<slIncludeIDNameList.size();j++){
							slCompleteIDNameList.add(slIncludeIDNameList.get(j));
						}
					}
				}
			}
		}
		return slCompleteIDNameList;
	}
}
