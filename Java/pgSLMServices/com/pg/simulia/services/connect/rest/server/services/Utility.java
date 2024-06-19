package com.pg.simulia.services.connect.rest.server.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import matrix.db.Context;
import matrix.db.MQLCommand;
import matrix.db.RelationshipType;

//Added for Get Security Classification API call - JIRA (CAD) DSM15X4-94 STARTS
import matrix.db.JPO;
import matrix.util.StringList;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
//Added for Get Security Classification API call - JIRA (CAD) DSM15X4-94 ENDS
import com.matrixone.apps.domain.util.MapList;

import org.apache.http.HttpResponse;
import com.dassault_systemes.smaslm.common.util.W3CUtil;
import com.dassault_systemes.smaslm.matrix.common.SimulationConstants;
import com.dassault_systemes.smaslm.matrix.common.SimulationUtil;
import com.dassault_systemes.smaslm.matrix.server.ExpressionUtil;
import com.dassault_systemes.smaslm.matrix.server.SIMULATIONS;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.servlet.Framework;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//Added for ALM#28761 Web Service to set IP Security (DT18X2-192) by MSH7 --- Start
import com.matrixone.apps.library.LibraryCentralConstants;
import com.matrixone.apps.domain.util.PropertyUtil;
//Added for ALM#28761 Web Service to set IP Security (DT18X2-192) by MSH7 --- End
import com.matrixone.apps.exportcontrol.ExportControlConstants;
//Added for ALM#28761 Web Service to set IP Security (DT18X2-199) by MSH7 --- Start
import com.matrixone.apps.domain.util.CacheUtil;

@SuppressWarnings("deprecation")
public class Utility {
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<String> BUSINESS_OBJECT_ATTRIBUTE_SELECTS = new ArrayList(
            0);

    static {

        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_ID);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add("physicalid");
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_TYPE);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_OWNER);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_NAME);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_DESCRIPTION);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_REVISION);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_LOCKED);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_ORIGINATED);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_MODIFIED);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_VAULT);
        BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(DomainConstants.SELECT_POLICY);

        // BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(CommonDocument.SELECT_TITLE);
        // BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(CommonDocument.SELECT_FILE_NAME);
        // BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(CommonDocument.SELECT_FILE_SIZE);
        // BUSINESS_OBJECT_ATTRIBUTE_SELECTS.add(CommonDocument.SELECT_FILE_MODIFIED);
        

    }    

    public static String getMcsUrl(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        return Framework.getFullClientSideURL(request, response, "");

    }

    public static String consumeResponseEntity(HttpResponse response)
            throws Exception {

        BufferedReader responseReader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuffer responseTextBuffer = new StringBuffer(0);
        String responseTextLine = "";
        while ((responseTextLine = responseReader.readLine()) != null) {
            responseTextBuffer.append(responseTextLine);
        }
        return responseTextBuffer.toString();

    }

    public static void addSimulationReference(Context context,
            String parentOid, String oid) throws Exception {

        if (oid != null && !oid.equals("")) {
            ContextUtil.startTransaction(context, true);

            RelationshipType relationshipType = new RelationshipType(
                    SimulationUtil
                            .getSchemaProperty("relationship_SimulationContent_Referenced"));
            new DomainObject(parentOid).addToObject(context, relationshipType,
                    oid);
            ContextUtil.commitTransaction(context);
        }

    }
    
    public static void removeSimulationReference(Context context,
            String parentOid, String oid) throws Exception {

        if (oid != null && !oid.equals("")) {
            ContextUtil.startTransaction(context, true);

            String disconnectCommand = "disconnect bus $1 relationship $2 to $3";
            MqlUtil.mqlCommand(context, disconnectCommand, parentOid, "Simulation Content - Referenced", oid);
            ContextUtil.commitTransaction(context);
        }

    }
    
    public static JSONObject getSimulationDetails(Context context, String simulationId) throws Exception{
        
        Map<String, String> simulationAttributes = new HashMap<String, String>(
                0);
        List<String> selects = new ArrayList<String>(0);
        selects.add(SimulationUtil.getSelectAttributeTitleString());
        selects.addAll(Utility.BUSINESS_OBJECT_ATTRIBUTE_SELECTS);
        simulationAttributes.putAll(Utility.getMqlObjectAttributes(context,
                simulationId, selects));
        JSONObject simulationJson = Utility
                .toObjectDetailsJson(simulationAttributes);
        //JSONArray childrenJson = new JSONArray();
        //simulationJson.put("children", childrenJson);
        //KT-START
        DomainObject simObj = DomainObject.newInstance(context, simulationId);
        Map attrMap = simObj.getAttributeMap(context);
        JSONObject attributeJson = new JSONObject(attrMap);
        simulationJson.put("Attributes", attributeJson);

        SIMULATIONS sim = new SIMULATIONS(simulationId);
        List parameters = sim.getParameterList(context);
        //Map parameterMap = new HashMap();
        JSONObject parameterJson = new JSONObject();
        if(null != parameters && !parameters.isEmpty()){
            for(int k = 0; k < parameters.size(); k++){
                Map paramMap = (Map)parameters.get(k);
                if(null != paramMap){
                    String paramName = (String)paramMap.get("name");
                    String paramVal = ExpressionUtil.evaluate(context, simulationId, (String)paramMap.get("Expression"));
                    if(null != paramVal && !paramVal.equalsIgnoreCase("")){
                        //Do Nothing
                    }
                    else{
                        paramVal = (String)paramMap.get("Value");
                    }
                    parameterJson.put(paramName, paramVal);
                }
            }
        }
        simulationJson.put("Parameters", parameterJson);
                 
        StringList objectSelects = new StringList(5);
        objectSelects.add(DomainConstants.SELECT_ID.toString());
        objectSelects.add(DomainConstants.SELECT_NAME.toString());
        objectSelects.add("attribute[Title].value");
        objectSelects.add("format.file.name");
        objectSelects.add("format.file.size");
        
       
        JSONObject inputMap = new JSONObject();
        JSONObject outputMap = new JSONObject();

        boolean getTo = false;
        boolean getFrom = true;
        short recurseToLevel = 1;            

        MapList documentList = simObj.getRelatedObjects(context,
                "Simulation Input,Simulation Output",
                "Simulation Document",
                objectSelects,
                new StringList(DomainRelationship.SELECT_NAME),
                getTo,
                getFrom,
                recurseToLevel,
                "",
                "");            
        
        if(null != documentList && !documentList.isEmpty()){
            Iterator documentItr = documentList.iterator();
            Hashtable docMap = new Hashtable();
            while(documentItr.hasNext()){
                docMap = (Hashtable<String, String>)documentItr.next();
                String docId = (String)docMap.get(DomainConstants.SELECT_ID);
                String docName = (String)docMap.get(DomainConstants.SELECT_NAME);
                String docTitle = (String)docMap.get("attribute[Title].value");
                Map fileMap = new HashMap();
                if(null != docMap.get("format.file.name") && docMap.get("format.file.name") instanceof String){
                   String fileName =  (String)docMap.get("format.file.name");
                   String fileSize = (String)docMap.get("format.file.size");
                   if(!fileName.equalsIgnoreCase("")){
                       fileMap.put(fileName, fileSize);
                   }
                }
                else{
                    StringList fileNameList =  (StringList)docMap.get("format.file.name");
                    StringList fileSizeList = (StringList)docMap.get("format.file.size");
                    for(int i = 0; i < fileNameList.size(); i ++ ){
                        String fileName = (String)fileNameList.get(i);
                        String fileSize = (String)fileSizeList.get(i);
                        fileMap.put(fileName, fileSize);
                    }
                }
                String relName = (String)docMap.get(DomainRelationship.SELECT_NAME);
                if(relName.equalsIgnoreCase("Simulation Input")){
                    inputMap.put("oid", docId);
                    inputMap.put("name", docName);
                    inputMap.put("title", docTitle);
                    inputMap.put("files", new JSONObject(fileMap));
                }
                else if(relName.equalsIgnoreCase("Simulation Output")){
                    outputMap.put("oid", docId);
                    outputMap.put("name", docName);
                    outputMap.put("title", docTitle);
                    outputMap.put("files", fileMap);                        
                }
            }
            simulationJson.put("Input", inputMap);
            simulationJson.put("Output", outputMap);            
        }
        
        return simulationJson;
        
    }
    

    public static void setSimulationAttributeValue(Context context, String id,
            String name, String value) throws Exception {

        DomainObject simObj = DomainObject.newInstance(context, id);
        simObj.setAttributeValue(context, name, value);

    }

    public static void setSimulationParameterValue(Context context, String id,
            String name, String value) throws Exception {

        com.dassault_systemes.smaslm.matrix.server.SIMULATIONS simulation = new com.dassault_systemes.smaslm.matrix.server.SIMULATIONS(
                id);
        simulation.setParameterValue(context, name, value);

    }    
    
    public static HashMap<String, String> convertsearchCriteriaXML(Context paramContext, String searchCriteriaXML) throws Exception
    {
		VPLMIntegTraceUtil.trace(paramContext, ">> START of convertsearchCriteriaXML method");
    	Document criteriaDoc;
            try
            {
                criteriaDoc = W3CUtil.loadXml(searchCriteriaXML);
            }
            catch (Exception localException)
            {
                    throw new Exception("Xml ParseError on SearchCriteriaList", localException);
            }
            
            HashMap<String, String> criteriaMap = new HashMap<String, String>();
            Element criteriaRoot = criteriaDoc.getDocumentElement();
            String str1 = criteriaRoot.getTagName();
        	VPLMIntegTraceUtil.trace(paramContext, ">> root tag name::"+str1);
			 if (!("SearchCriteriaList".equals(str1))){
                    throw new Exception("err.api.Xml.InvalidRoot on SearchCriteriaList");
            }
			 String criteriaName;
			 String criteriaType;
			 String criteriaValue;
			 String criteriaDefault ;
			 
            for(Element criteriaList : W3CUtil.getChildElements(criteriaRoot, "SearchCriteria")){
              
            	 VPLMIntegTraceUtil.trace(paramContext, ">> criteriaList::"+criteriaList);
            	 
                criteriaName = criteriaList.getAttribute("name");
                criteriaType = criteriaList.getAttribute("type");
                criteriaValue = criteriaList.getAttribute("value");
                
				//ALM#24069 IP Find Class Web Call not Returning all expected (DT18X2-181) by MSH7 --- Start
                criteriaDefault = criteriaList.getAttribute("default");
                VPLMIntegTraceUtil.trace(paramContext, ">>criteriaName::"+criteriaName+" criteriaType::"+criteriaType+" criteriaValue::"+criteriaValue+" criteriaDefault::"+criteriaDefault);
                
                //START: DTCLD-742 Added logic to get the text content of the child nodes of SearchCriteria. Such nodes are generated when post service json object is converted to xml
                if(UIUtil.isNullOrEmpty(criteriaName) && UIUtil.isNullOrEmpty(criteriaType) && UIUtil.isNullOrEmpty(criteriaValue)) {
               	   		NodeList nl = criteriaList.getChildNodes();
               	   		VPLMIntegTraceUtil.trace(paramContext, ">>nodeList length::"+nl.getLength());
               	   		
               	   		for (int i = 0; i < nl.getLength(); i++) {
               	   			Node node = nl.item(i);
               	   			if(node.getNodeName().equals("name")) {
               	   				criteriaName=node.getTextContent();
               	   			}else if(node.getNodeName().equals("type")) {
               	   				criteriaType=node.getTextContent();
               	   			}else if(node.getNodeName().equals("value")){
               	   				criteriaValue=node.getTextContent();
                            }else if(node.getNodeName().equals("default")){
                            	criteriaDefault=node.getTextContent();
                          }
                      }
               	   	VPLMIntegTraceUtil.trace(paramContext, ">>node criteriaName::"+criteriaName+" criteriaType::"+criteriaType+" criteriaValue::"+criteriaValue+" criteriaDefault::"+criteriaDefault);
                   }
              //END: DTCLD-742 
				if(UIUtil.isNotNullAndNotEmpty(criteriaDefault)){
					 criteriaMap.put("DEFAULT", criteriaDefault);
				} else {
					 criteriaMap.put("DEFAULT", "");
				}
				//ALM#24069 IP Find Class Web Call not Returning all expected (DT18X2-181) by MSH7 --- End      
                
                if(null != criteriaName && null != criteriaType && null != criteriaValue){
                    if(criteriaType.equalsIgnoreCase("B")){
                        if(criteriaName.equalsIgnoreCase("title")){
                            criteriaMap.put("attribute[Title].value", criteriaValue);
                        }
                        else if(criteriaName.equalsIgnoreCase("owner")){
                            criteriaMap.put("OWNER", criteriaValue);
                        }
                        else if(criteriaName.equalsIgnoreCase("name")){
                            criteriaMap.put("NAME", criteriaValue);
                        }
                        else if(criteriaName.equalsIgnoreCase("type")){
                            criteriaMap.put("TYPE", criteriaValue);
                        }                        
                        else if(criteriaName.equalsIgnoreCase("originated")){
                            criteriaMap.put("ORIGINATED", criteriaValue);
                        //Added for incorporating current as Basic field in the rest API - JIRA (CAD) DSM15X4-93 STARTS
                        } else if (criteriaName.equalsIgnoreCase("current")) {
                                criteriaMap.put("CURRENT", criteriaValue);
                        //Added for incorporating current as Basic field in the rest API - JIRA (CAD) DSM15X4-93 ENDS  
                        }                        						
                        else{
                            criteriaMap.put(criteriaName, criteriaValue);
                        }
						
                    }
                    else if(criteriaType.equalsIgnoreCase("A")){
                        criteriaMap.put("attribute[" + criteriaName + "].value", criteriaValue);
                    }
                }
            }
            return criteriaMap;
    }
    
    @SuppressWarnings("rawtypes")
    public static JSONObject toObjectDetailsJson(Map object) throws Exception {

        JSONObject objectJson = new JSONObject();
        if (object.containsKey(SimulationUtil.getSelectAttributeTitleString())) {
            objectJson.put("title",
                    object.get(SimulationUtil.getSelectAttributeTitleString())
                            .toString());
        }
        if (object.containsKey(DomainObject.SELECT_TYPE)) {
            objectJson.put("type", object.get(DomainObject.SELECT_TYPE)
                    .toString());
        }
        if (object.containsKey(DomainObject.SELECT_NAME)) {
            objectJson.put("name", object.get(DomainObject.SELECT_NAME)
                    .toString());
        }
        if (object.containsKey(DomainObject.SELECT_REVISION)) {
            objectJson.put("revision", object.get(DomainObject.SELECT_REVISION)
                    .toString());
        }
        if (object.containsKey(DomainObject.SELECT_ID)) {
            objectJson
                    .put("oid", object.get(DomainObject.SELECT_ID).toString());
        }
        if (object.containsKey("physicalid")) {
            objectJson.put("pid", object.get("physicalid").toString());
        }
        if (object.containsKey(DomainObject.SELECT_OWNER)) {
            objectJson.put("owner", object.get(DomainObject.SELECT_OWNER)
                    .toString());
        }
        if (object.containsKey(DomainObject.SELECT_DESCRIPTION)) {
            String value = object.get(DomainObject.SELECT_DESCRIPTION)
                    .toString();
            objectJson.put("description", value);
        }
        if (object.containsKey(DomainObject.SELECT_LOCKED)) {
            objectJson.put("locked", object.get(DomainObject.SELECT_LOCKED)
                    .toString());
        }
        if (object.containsKey(DomainObject.SELECT_ORIGINATED)) {
            objectJson.put("originated",
                    object.get(DomainObject.SELECT_ORIGINATED).toString());
        }
        if (object.containsKey(DomainObject.SELECT_MODIFIED)) {
            objectJson.put("modified", object.get(DomainObject.SELECT_MODIFIED)
                    .toString());
        }
        if (object.containsKey(DomainObject.SELECT_VAULT)) {
            objectJson.put("vault", object.get(DomainObject.SELECT_VAULT)
                    .toString());
        }
        if (object.containsKey(DomainObject.SELECT_POLICY)) {
            objectJson.put("policy", object.get(DomainObject.SELECT_POLICY)
                    .toString());
        }
        if (object.containsKey("size")) {
            objectJson.put("size", object.get("size").toString());
        }
        return objectJson;

    }
    
    @SuppressWarnings("deprecation")
    public static Map<String, String> getMqlObjectAttributes(Context context,
            String id, List<String> selects) throws Exception {

        Map<String, String> details = new HashMap<String, String>(0);
        MQLCommand mqlCommand = new MQLCommand();
        String mqlCommandString = "print bus '" + id + "' select";
        for (int j = 0; j < selects.size(); j++) {
            mqlCommandString += " " + selects.get(j);
        }
        if (!mqlCommand.executeCommand(context, mqlCommandString)) {
            throw new Exception(mqlCommand.getError());
        }
        String mqlResult = mqlCommand.getResult().trim();
        String[] mqlResults = mqlResult.split("\\n");
        String mqlResultLine, key, value;
        if (mqlResults.length > 1) {
            for (int j = 1; j < mqlResults.length; j++) {
                mqlResultLine = mqlResults[j].trim();
                key = mqlResultLine.substring(0, mqlResultLine.indexOf("="))
                        .trim();
                value = mqlResultLine.substring(mqlResultLine.indexOf("=") + 1)
                        .trim();
                details.put(key, value);
            }
        }
        return details;

    }
    
    //Added for Get Security Classification API call - JIRA (CAD) DSM15X4-94 STARTS
    public static Map<String, StringList> getTypeListFromPageConfig(Context context, boolean includeDefaultEntry) throws Exception {
        
                Map<String, StringList> returnTypeMap = new HashMap<String, StringList>();
                try {
                        // Fetching entries from page object for different scenarios
                        String sAlwaysHighlyRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.AlwaysHighlyRestricted"); 
                        String sAlwaysRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.AlwaysRestricted"); 
                        String sAlwaysInternalUse = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.AlwaysInternalUse"); 

                        StringList sAlwaysHighlyRestrictedList = FrameworkUtil.split(sAlwaysHighlyRestricted, "|");
                        StringList sAlwaysRestrictedList = FrameworkUtil.split(sAlwaysRestricted, "|");
                        StringList sAlwaysInternalUseList = FrameworkUtil.split(sAlwaysInternalUse, "|");
                        
                        returnTypeMap.put(STR_ALWAYS_HiR, sAlwaysHighlyRestrictedList);
                        returnTypeMap.put(STR_ALWAYS_R, sAlwaysRestrictedList);
                        returnTypeMap.put(STR_ALWAYS_IU, sAlwaysInternalUseList);
                                                
                        if(includeDefaultEntry){
                                String sDefaultInternalUse = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.DefaultInternalUse");
                                String sDefaultHighlyRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.DefaultHighlyRestricted");
                                String sDefaultRestricted = EnoviaResourceBundle.getProperty(context, "pgExportControl.IPClassification.DefaultRestricted");

                                StringList sDefaultInternalUseList = FrameworkUtil.split(sDefaultInternalUse, "|");
                                StringList sDefaultHighlyRestrictedList = FrameworkUtil.split(sDefaultHighlyRestricted, "|");
                                StringList sDefaultRestrictedList = FrameworkUtil.split(sDefaultRestricted, "|");

                                returnTypeMap.put(STR_DEFAULT_IU, sDefaultInternalUseList);
                                returnTypeMap.put(STR_DEFAULT_HiR, sDefaultHighlyRestrictedList);
                                returnTypeMap.put(STR_DEFAULT_R, sDefaultRestrictedList);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                }
                return returnTypeMap;
        }
    
    public static String getIPClassification(Context context,String strObjtype) throws Exception
    {
        String strReturn = DomainConstants.EMPTY_STRING;
        try {
                        if(UIUtil.isNotNullAndNotEmpty(strObjtype)){
                                if(!strObjtype.startsWith("type_")){
                                        try {
                                                strObjtype = FrameworkUtil.getAliasForAdmin(context,DomainConstants.SELECT_TYPE, strObjtype, true);
                                        } catch (Exception exp){
                                                throw exp;
                                        }
                                }
                                if(UIUtil.isNotNullAndNotEmpty(strObjtype)){
                                        //Fetch type entry from page object
                                        Map<?, ?> typesListMap = getTypeListFromPageConfig(context,true);
                                        
                                        StringList sAlwaysHighlyRestrictedList = (StringList)typesListMap.get(STR_ALWAYS_HiR);
                                        StringList sAlwaysRestrictedList = (StringList)typesListMap.get(STR_ALWAYS_R);
                                        StringList sAlwaysInternalUseList = (StringList)typesListMap.get(STR_ALWAYS_IU);
                                        StringList sDefaultInternalUseList = (StringList)typesListMap.get(STR_DEFAULT_HiR);
                                        StringList sDefaultHighlyRestrictedList = (StringList)typesListMap.get(STR_DEFAULT_R);
                                        StringList sDefaultRestrictedList = (StringList)typesListMap.get(STR_DEFAULT_IU);
                                        
                                        String strClassHighlyRestricted = EnoviaResourceBundle.getProperty(context, "emxExportControlStringResource", context.getLocale(), "pgExportControl.IPClassification.HighlyRestricted"); 
                                    String strClassRestricted = EnoviaResourceBundle.getProperty(context, "emxExportControlStringResource", context.getLocale(), "pgExportControl.IPClassification.Restricted"); 
                                    String strClassInternalUse = EnoviaResourceBundle.getProperty(context, "emxExportControlStringResource", context.getLocale(), "pgExportControl.IPClassification.InternalUse");
                                        
                                        if(sAlwaysHighlyRestrictedList.contains(strObjtype)) {
                                                strReturn = strClassHighlyRestricted;
                                        } else if(sAlwaysRestrictedList.contains(strObjtype)) {
                                                strReturn = strClassRestricted;
                                        } else if(sAlwaysInternalUseList.contains(strObjtype)) {
                                                strReturn = strClassInternalUse;
                                        } else if(sDefaultInternalUseList.contains(strObjtype)) {
                                                strReturn = strClassInternalUse;
                                        } else if(sDefaultHighlyRestrictedList.contains(strObjtype)) {
                                                strReturn = strClassHighlyRestricted;
                                        } else if(sDefaultRestrictedList.contains(strObjtype)) {
                                                strReturn = strClassRestricted;
                                        }
                                }
                        }
        } catch (Exception ex) {
                throw ex;
        }
        return strReturn;
    }
    
    public static StringList getIPSecurityClasses(Context context, String strObjtype) throws Exception
    {
        String[] argsJPO = new String[1];
        String strIPClassificationData = DomainConstants.EMPTY_STRING;
        StringList slIPSecClasses = new StringList(1);
        HashMap<String, Object> hmArgsMap = null;
        try {
                if(UIUtil.isNotNullAndNotEmpty(strObjtype)) {
                        strObjtype = strObjtype.replace("\"", "");
                        strIPClassificationData = getIPClassification(context,strObjtype);
                        if(UIUtil.isNotNullAndNotEmpty(strIPClassificationData)) {
                                hmArgsMap = new HashMap<String, Object>();
                                hmArgsMap.put("vSelectedValue", strIPClassificationData);
                                argsJPO = JPO.packArgs(hmArgsMap);
                                slIPSecClasses = (StringList)JPO.invoke(context, STR_PROGNAME_SECURITY_UTIL, argsJPO, STR_METHODNAME_IPCONTROLCLASS_USER, argsJPO, StringList.class);
                        }
                }
        }
        catch (Exception ex) {
                throw ex;
        }
        return slIPSecClasses;
    }
    
    public static final String STR_PROGNAME_SECURITY_UTIL = "pgIPSecurityCommonUtil";
    public static final String STR_METHODNAME_IPCONTROLCLASS_USER = "includeSecurityCategoryClassification";
    public static final String STR_ALWAYS_HiR = "AlwaysHighlyRestricted";
    public static final String STR_ALWAYS_R = "AlwaysRestricted";
    public static final String STR_ALWAYS_IU = "AlwaysInternalUse";
    public static final String STR_DEFAULT_HiR = "DefaultHighlyRestricted";
    public static final String STR_DEFAULT_R = "DefaultRestricted";
    public static final String STR_DEFAULT_IU = "DefaultInternalUse";
  //Added for Get Security Classification API call - JIRA (CAD) DSM15X4-94 ENDS    
    

    public static String saveXml(Node node, boolean omitXML) throws TransformerException
    {
        StringWriter writer = new StringWriter();
        TransformerFactory localTransformerFactory = TransformerFactory.newInstance();
        Transformer localTransformer = localTransformerFactory.newTransformer();
        localTransformer.setOutputProperty("encoding", "UTF-8");
        if (omitXML) {
            localTransformer.setOutputProperty("omit-xml-declaration", "yes");
        }
        localTransformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }       
    
	//Added for ALM#28761 Web Service to set IP Security (DT18X2-192, DT18X2-199) by MSH7 --- Start
	/**
	 * This is a utility method to add IP Class to the BusinessObject
	 * @param context
	 * @param args cntext, Object Id, IpClass List and classification
	 * @return boolean if user has from connect access to connect IP Class to bus obj
	 * @throws Exception
	 */
    public static boolean addIPClasses(Context context, String targetOid, String IpClassList, String classification) throws Exception {
		boolean bHasFromConnectUser = false;
		try {
			DomainObject targetDO = new DomainObject(targetOid);
			
			if (UIUtil.isNotNullAndNotEmpty(targetOid) && UIUtil.isNotNullAndNotEmpty(IpClassList)) {
				
				IpClassList = IpClassList.replaceAll(",","|");
				ContextUtil.startTransaction(context, true);

				HashMap<String, String> paramMapNew1 = new HashMap<String, String>();
				paramMapNew1.put("strClassNames",IpClassList);
				String[] methodargs = JPO.packArgs(paramMapNew1);
				
				Map<String, String> mapPrefclass = (Map)JPO.invoke(context, "pgIPSecurityCommonUtil", null, "getPreferredClasseMap", methodargs, Map.class);
				String strPreferredClassIds = (String) mapPrefclass.get("classIds");
				
				StringList strClassIDList = FrameworkUtil.split(strPreferredClassIds, "|");
				MapList mConnectedIPClassMapList = getConnetedIPClass(context, targetDO);
				String sOID = "";
				String sRelID = "";
				Map mIPClassMap = null;
				StringList slAlreadyConnectedIPClass = new StringList();
				StringList slConnectedIPClassList = new StringList();
				int size = mConnectedIPClassMapList.size();
				String[] strClassOIDArray = new String[size];
				if(size > 0){
					for (int i = 0; i < size ; i++) {
						mIPClassMap = (Map)mConnectedIPClassMapList.get(i);
						sOID = (String)mIPClassMap.get(DomainConstants.SELECT_ID);
						slAlreadyConnectedIPClass.add(sOID);
						sRelID = (String)mIPClassMap.get(DomainRelationship.SELECT_ID);
						if(!strClassIDList.contains(sOID)){
							slConnectedIPClassList.add(sRelID);
						}
					}
					strClassOIDArray = slConnectedIPClassList.toArray(new String[slConnectedIPClassList.size()]);
					
					//disconnecting IP Classes which are not present in IpClassList
					if(strClassOIDArray.length > 0){
						DomainRelationship.disconnect(context,strClassOIDArray,true);
						slConnectedIPClassList.clear();
					}	
				}
				
				size = strClassIDList.size();
				strClassOIDArray = new String[size];
				
				//connecting IP Classes which are not connected to the target DO
				for (int i = 0; i < size ; i++) {
					sOID = strClassIDList.get(i);
					if(!slAlreadyConnectedIPClass.contains(sOID)){
						slConnectedIPClassList.add(sOID);
					}
				}
				strClassOIDArray = slConnectedIPClassList.toArray(new String[slConnectedIPClassList.size()]);
				if(strClassOIDArray.length > 0){
					DomainRelationship.connect(context,targetDO,DomainConstants.RELATIONSHIP_PROTECTED_ITEM,false,strClassOIDArray);
				}	
				
				//adding attribute pgIPClassification on target OID via DTIPSecClassExtension interface
				addIPClassificationAttr(context, targetDO, classification);
				
				ContextUtil.commitTransaction(context);
				bHasFromConnectUser = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return bHasFromConnectUser;
    }   
	
	
	/**
	 * This is a utility method to check whether IP Class is exists or not and if IP Class exists then whether it is avialable or not for classification
	 * @param context
	 * @param args context and IpClass List 
	 * @return 
	 *		Classification Not Allowed --- If Ip class is not availablle for classification
	 *		Classification Allowed  --- If Ip class is availablle for classification
	 *		IP Class does not exists  --- If Ip class does not exists
	 * @throws Exception
	 */
    public static String checkIPClassExistence(Context context,
            StringList IpClassList) throws Exception {	
		String sReturn = "";
		try {
			String strECCStateActive = PropertyUtil.getSchemaProperty(context, "policy", ExportControlConstants.POLICY_EXC_CLASSIFICATION,
					"state_Active");
			String sIpClassName = "";
			String sMqlRet = "";
			StringList slAttrValueList = new StringList();
			int ipClassListSize = IpClassList.size();
			if(ipClassListSize > 0){
				for(int i=0;i<ipClassListSize;i++){
					sIpClassName = IpClassList.get(i);
					sMqlRet = MqlUtil.mqlCommand(context, true, "pri bus 'IP Control Class' '"+sIpClassName+"' - select current dump |", true);
					slAttrValueList.add(sMqlRet);
				}
				
				if(slAttrValueList.contains(strECCStateActive)){
					sReturn = "Classification Allowed";
				} else {
					sReturn = "Classification Not Allowed";
				}
			} else {
				sReturn = "IP Class does not exists";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return sReturn;
		}		
	}			
    //Added for ALM#28761 Web Service to set IP Security (DT18X2-192, DT18X2-199) by MSH7 --- End
	
	//Added for ALM#28761 Web Service to set IP Security (DT18X2-199) by MSH7 --- Start
	/**
	 * This is a utility method to check whether both Restricted and Highly Restricted class are present in IpClasslist
	 * @param context
	 * @param args context, IpClass List and classification value
	 * @return true if both Restricted and Highly Restricted class are present in IpClasslist
	 * @throws Exception
	 */
	 
	public static boolean checkMixedClassPresent(Context context, StringList slIpClassList,
            String classification) throws Exception {	
		boolean bReturnValue = false;
		try {
			int iIpListSize = slIpClassList.size();
			if(iIpListSize > 0){
				String TYPE_IPCONTROLCLASS  = PropertyUtil.getSchemaProperty("type_IPControlClass");
				String VAULT_ESERVICEPRODUCTION  = PropertyUtil.getSchemaProperty("vault_eServiceProduction");
				
				String whereExpression  = "name==";
				StringList objectSelects = new StringList();
				objectSelects.add("to["+LibraryCentralConstants.RELATIONSHIP_SUBCLASS+"].from.name");
				
				for(int i=0;i<iIpListSize;i++){
					whereExpression = whereExpression+"\""+slIpClassList.get(i)+"\"||name==";
				}
				whereExpression = whereExpression.substring(0,whereExpression.length()-8);
				
				MapList mlIPClassList = DomainObject.findObjects(context, TYPE_IPCONTROLCLASS,VAULT_ESERVICEPRODUCTION, whereExpression ,objectSelects);
				iIpListSize = mlIPClassList.size();
				Map mIPClassMap = null;
				String sExportControlLibrary = "";
				StringList slIPClassificationType = new StringList();
				if(iIpListSize>0){
					for(int i=0;i<iIpListSize;i++){
						mIPClassMap = (Map)mlIPClassList.get(i);
						sExportControlLibrary = (String)mIPClassMap.get("to["+LibraryCentralConstants.RELATIONSHIP_SUBCLASS+"].from.name");
						slIPClassificationType.add(sExportControlLibrary);
					}
				}
				
				if("Restricted".equalsIgnoreCase(classification) && slIPClassificationType.contains("Highly Restricted") ){
					bReturnValue = true;
				}else if("HighlyRestricted".equalsIgnoreCase(classification) && slIPClassificationType.contains("Business Use")){
					bReturnValue = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 	
		return bReturnValue;	
	}	

	/**
	 * This is a utility method to check whether Ip Class provided are already connected to the target OID and are the only IP Class connected to target OID
	 * @param context
	 * @param args context,target Oid and  IpClass List
	 * @return true if IP Class are connected to target OID
	 * @throws Exception
	 */
	 
	public static boolean checkIPClassChange(Context context, String targetOid, StringList slIpClassList) throws Exception {	
		boolean bReturnValue = false;
		try {	
			DomainObject targetDO = new DomainObject(targetOid);
			MapList mConnectedIPClassMapList = getConnetedIPClass(context, targetDO);
			Map mIPClassMap = null;
			StringList slAlreadyConnectedIPClass = new StringList();
			int size = mConnectedIPClassMapList.size();
			if(size > 0){
				for (int i = 0; i < size ; i++) {
					mIPClassMap = (Map)mConnectedIPClassMapList.get(i);
					slAlreadyConnectedIPClass.add((String)mIPClassMap.get(DomainConstants.SELECT_NAME));
				}	
			}
			
			if(slIpClassList.equals(slAlreadyConnectedIPClass))
				bReturnValue = true;
		} catch (Exception e) {
			e.printStackTrace();
		} 	
		return bReturnValue;	
	}

	/**
	 * This is a utility method to get Ip Class connected to target OID
	 * @param context
	 * @param args context and target DO
	 * @return MapList
	 * @throws Exception
	 */
	public static MapList getConnetedIPClass(Context context, DomainObject targetDO) throws Exception {	
		MapList mConnectedIPClassMapList = new MapList();
		try {
			StringList busSelects = new StringList();
			busSelects.add(DomainConstants.SELECT_ID.toString());
			busSelects.add(DomainConstants.SELECT_NAME.toString());
			mConnectedIPClassMapList = targetDO.getRelatedObjects(context, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, DomainConstants.QUERY_WILDCARD, busSelects, new StringList(DomainRelationship.SELECT_ID), true, false, (short)1, null, null);
			if(mConnectedIPClassMapList.size() > 0)
				mConnectedIPClassMapList.sort();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mConnectedIPClassMapList;
	}
	
	/**
	 * This is a utility method to add pgIPClassification attribute on target OID via DTIPSecClassExtension interface
	 * @param context
	 * @param args context and target DO
	 * @return nothing
	 * @throws Exception
	 */
	private static void addIPClassificationAttr(Context context, DomainObject targetDO, String classification) throws Exception {	
		try {
			String CACHE_MAP_KEY_ID_NAME = "_DTIPSecClassExtension";
			String sCachedInterfaceType = "";
			String sMqlRet = "";
			String INTERFACE_DTIPSECCLASSEXTENSION  = PropertyUtil.getSchemaProperty("interface_DTIPSecClassExtension");
			String ATTR_PGIPCLASSIFICATION = PropertyUtil.getSchemaProperty("attribute_pgIPClassification");
			
			if(UIUtil.isNotNullAndNotEmpty(classification)){
				if("HighlyRestricted".equalsIgnoreCase(classification))
					classification = "Highly Restricted";
			} else {
				classification = "";
			}
			
			String sAttrValue = targetDO.getAttributeValue(context,ATTR_PGIPCLASSIFICATION);
			if(null==sAttrValue || UIUtil.isNullOrEmpty(sAttrValue)){
				//fetching interface data from cache
				sCachedInterfaceType = (String)CacheUtil.getCacheObject(context, CACHE_MAP_KEY_ID_NAME + context.getUser());
				
				if(UIUtil.isNullOrEmpty(sCachedInterfaceType)){
					
					sMqlRet = MqlUtil.mqlCommand(context, true, "pri interface "+INTERFACE_DTIPSECCLASSEXTENSION+" select type dump |", true);
					sCachedInterfaceType = sMqlRet;
					//setting interface data into cache
					CacheUtil.setCacheObject(context, CACHE_MAP_KEY_ID_NAME + context.getUser(), sCachedInterfaceType);
				}
				//getting target OID type
				String sType = targetDO.getInfo(context,DomainConstants.SELECT_TYPE);
				if(!sCachedInterfaceType.contains(sType)){
					MqlUtil.mqlCommand(context, true, "mod interface "+INTERFACE_DTIPSECCLASSEXTENSION+" add type '"+sType+"'", true);
				}
				
				sMqlRet = MqlUtil.mqlCommand(context, true, "pri bus "+targetDO.getId(context)+" select interface dump |", true);
				if(UIUtil.isNullOrEmpty(sMqlRet) || !sMqlRet.contains(INTERFACE_DTIPSECCLASSEXTENSION)){
					MqlUtil.mqlCommand(context, true, "mod bus "+targetDO.getId(context) +" add interface '"+INTERFACE_DTIPSECCLASSEXTENSION+"'", true);
					targetDO.setAttributeValue(context,ATTR_PGIPCLASSIFICATION,classification);
				}
				
			} else {
				if(!classification.equalsIgnoreCase(targetDO.getAttributeValue(context,ATTR_PGIPCLASSIFICATION)))
					targetDO.setAttributeValue(context,ATTR_PGIPCLASSIFICATION, classification);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Added for ALM#28761 Web Service to set IP Security (DT18X2-199) by MSH7 --- End
	
	//Added for ALMREQ#30755 2DARY PACK CONFIG attribute will be accesible via Webservices. (DT18X3-270) by MSH7 --- Start
	/**
	 * This is a utility method to either set or get attribute value
	 * @param context
	 * @param args context, Attribute name, attribute value, mode (Read/Write)
	 * @return string as empty or attribute value
	 * @throws Exception
	 */
	public static String setOrGetAttributeValueMethod(Context context, DomainObject doObj, String name, String value, String Mode) throws Exception {
		String sRetValue = "";
		
		try {
			
			if(Mode.equalsIgnoreCase("Read"))
				sRetValue = doObj.getAttributeValue(context, name);
			else 
				doObj.setAttributeValue(context, name, value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sRetValue;
    }
	//Added for ALMREQ#30755 2DARY PACK CONFIG attribute will be accesible via Webservices. (DT18X3-270) by MSH7 --- End
	
	/**
	 * Method added for ALM 49215 DTCLD-259
	 * This method would disconnect the IP Control classes connected to the Simulation object
	 * @param context
	 * @param strFromType
	 * @param strFromObjectId
	 * @param strToType
	 * @throws FrameworkException
	 */
	public static void disconnectIPControlClassFromSimulationObject(Context context,String strFromType,String strFromObjectId,String strToType) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, "START of Utility: disconnectIPControlClassFromSimulationObject method");
		
		if(SimulationConstants.TYPE_SIMULATION.equals(strFromType) && SimulationConstants.TYPE_SIMULATION_TEMPLATE.equals(strToType)) {
			//disconnect the Protected Item relationship from Simulation object
			
			DomainObject doSimulationObj=DomainObject.newInstance(context,strFromObjectId);
			StringList slProtectedItemRelId=doSimulationObj.getInfoList(context, "to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].id");
			
			VPLMIntegTraceUtil.trace(context, "slProtectedItemRelId:::"+slProtectedItemRelId);
			
			if(!slProtectedItemRelId.isEmpty()) {
				DomainRelationship.disconnect(context, slProtectedItemRelId.toStringArray());
				VPLMIntegTraceUtil.trace(context, "Disconnected the Protected Item relationships");
			}
		}
		VPLMIntegTraceUtil.trace(context, "END of disconnectIPControlClassFromSimulationObject method");
	}
	
	/**
	 * Method added for ALM 49215 DTCLD-259
	 * This method would connect the IP Control classes to the Simulation object
	 * @param context
	 * @param strFromType
	 * @param strFromObjectId
	 * @param strToType
	 * @throws FrameworkException
	 */
	public static void connectIPControlClassToSimulationObject(Context context,String strFromType,String strFromObjectId,String strToType,String strToObjectId) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, "START of Utility: connectIPControlClassToSimulationObject method");
		
		if(SimulationConstants.TYPE_SIMULATION.equals(strFromType) && SimulationConstants.TYPE_SIMULATION_TEMPLATE.equals(strToType)) {
			DomainObject doSimulationObj=DomainObject.newInstance(context,strFromObjectId);
			StringList slProtectedItemRelId=doSimulationObj.getInfoList(context, "to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].id");
			
			VPLMIntegTraceUtil.trace(context, "slProtectedItemRelId:::"+slProtectedItemRelId);
			
			if(slProtectedItemRelId.isEmpty()) {
				
				//get the IP Control class object Id from Simulation object connected to Simulation Template via Simulation Template Content relationship
				
				DomainObject doSimTemplateObj=DomainObject.newInstance(context,strToObjectId);
				StringList slIPControlClassIds=doSimTemplateObj.getInfoList(context, "from["+SimulationConstants.RELATIONSHIP_SIMULATION_TEMPLATE_CONTENT+"].to.to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from.id");
			
				VPLMIntegTraceUtil.trace(context, "slIPControlClassIds:::"+slIPControlClassIds);
				
				if(!slIPControlClassIds.isEmpty()) {
					//connect the IP Control class objects
					
					DomainRelationship.connect(context, doSimulationObj, DomainConstants.RELATIONSHIP_PROTECTED_ITEM, false,slIPControlClassIds.toStringArray());
					VPLMIntegTraceUtil.trace(context, "Connected the Simulation and IP Control class objects via Protected Item relationship");
				}
			}
		}
		VPLMIntegTraceUtil.trace(context, "END of Utility: connectIPControlClassToSimulationObject method");
	}
	
	/**
	 * Method used to convert the json string to xml format
	 * @param jsonString
	 * @return xml string
	 */
	public static String convertJSONArrayToXML(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);  
		return XML.toString(jsonObject);
	}
	
	/**
	 * Method to get the json input for POST web service
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getInputJSON(HttpServletRequest request) throws IOException {
		String line = null;
		StringBuilder sbInputData = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			while((line = in.readLine())!=null) {
					sbInputData.append(line);
			}
		}
		return sbInputData.toString();
	}
}
