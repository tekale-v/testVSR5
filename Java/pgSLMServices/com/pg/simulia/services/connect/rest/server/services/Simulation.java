package com.pg.simulia.services.connect.rest.server.services;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import matrix.db.Context;
import matrix.util.StringList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.smaslm.common.util.W3CUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.dassault_systemes.smaslm.matrix.server.SIMULATIONS;
import java.util.List;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.dassault_systemes.smaslm.matrix.common.SimulationUtil;
import com.dassault_systemes.smaslm.matrix.server.ExpressionUtil;
import com.dassault_systemes.smaslm.matrix.server.IsightWUtil;

//Added for ALMREQ#30755 2DARY PACK CONFIG attribute will be accesible via Webservices. (DT18X3-270) by MSH7 --- Start
import com.matrixone.apps.domain.util.AccessUtil;
import matrix.db.Access;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.util.IPManagement;

import javax.ws.rs.core.MediaType;
//Added for ALMREQ#30755 2DARY PACK CONFIG attribute will be accesible via Webservices. (DT18X3-270) by MSH7 --- End


@Path("/simulation")
@Produces({ "application/xml" })
public class Simulation extends RestService {

	public static final String MESSAGE_NO_OBJECTID="Object Id is missing";
	public static final String MESSAGE_NO_MODE="Mode parameter is empty. Mode value can be either Read or Write";
	public static final String MESSAGE_NO_READ_ACCESS="User does not have read access to the object";
	public static final String MESSAGE_NO_MODIFY_ACCESS="User does not have modify access to the object";
	public static final String MESSAGE_SUCCESSFUL_MODIFICATION="Attribute value is successfully modified";
	public static final String MESSAGE_ATTR_VALUE="Attribute value is ";
	public static final String READ_MODE="Read";
	public static final String WRITE_MODE="Write";
			
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @GET
    @Path("/getInfo")
    @Produces({ "application/ds-json", "application/xml" })
    public Response getInfo(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("oid") String oid) {

        Document resultDoc;
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {

            resultDoc = W3CUtil.newDocument();
            Element simEl = resultDoc.createElement("Simulation");
            DomainObject simObj = DomainObject.newInstance(context, oid);
            SIMULATIONS sim = new SIMULATIONS(oid);
            List parameters = sim.getParameterList(context);
            //System.out.println("###parameters --> " + parameters);
            simEl.setAttribute("objectId", oid);
            resultDoc.appendChild(simEl);
            Element paramsEl = W3CUtil.newElement(simEl, "Parameters");
            if(null != parameters && !parameters.isEmpty()){
                for(int k = 0; k < parameters.size(); k++){
                    Element paramEl = W3CUtil.newElement(paramsEl, "Parameter");
                    Map paramMap = (Map)parameters.get(k);
                    if(null != paramMap){
                        String paramName = (String)paramMap.get("name");
                        paramEl.setAttribute("name", paramName);
                        String paramVal = ExpressionUtil.evaluate(context, oid, (String)paramMap.get("Expression"));
                        if(null != paramVal && !paramVal.equalsIgnoreCase("")){
                            //Do Nothing
                        }
                        else{
                            paramVal = (String)paramMap.get("Value");
                        }
                        paramEl.setAttribute("value", paramVal);
                    }
                }
            }
            StringList objectSelects = new StringList(3);
            objectSelects.add(DomainConstants.SELECT_ID.toString());
            objectSelects.add(DomainConstants.SELECT_NAME.toString());
            objectSelects.add(DomainConstants.SELECT_DESCRIPTION.toString());
            
            
            boolean getTo = false;
            boolean getFrom = true;
            short recurseToLevel = 1;            

            MapList activitiesList = simObj.getRelatedObjects(context,
                    "Simulation Activity",
                    "Simulation Activity",
                    objectSelects,
                    null,
                    getTo,
                    getFrom,
                    recurseToLevel,
                    "",
                    "");
            
            //System.out.println("activitiesList --> " + activitiesList);
            
            if(null != activitiesList && !activitiesList.isEmpty()){
                
                Iterator activitiesItr = activitiesList.iterator();
                Hashtable<String, String> activityMap = new Hashtable<String, String>();
                Element actsEl = W3CUtil.newElement(simEl, "Activities");
                while(activitiesItr.hasNext()){
                    activityMap = (Hashtable<String, String>)activitiesItr.next();
                    String activityId = (String)activityMap.get(DomainConstants.SELECT_ID);
                    String activityName = (String)activityMap.get(DomainConstants.SELECT_NAME);
                    Element actEl = W3CUtil.newElement(actsEl, "Activity");
                    actEl.setAttribute("objectId", activityId);
                    actEl.setAttribute("name", activityName);
                    DomainObject actObj = DomainObject.newInstance(context, activityId);
                    
                    MapList actCategoriesList = actObj.getRelatedObjects(context,
                            "Simulation Category",
                            "Simulation Category",
                            objectSelects,
                            null,
                            getTo,
                            getFrom,
                            recurseToLevel,
                            "",
                            "");    

                    //System.out.println("categoriesList --> " + actCategoriesList);
                    
                    if(null != actCategoriesList && !actCategoriesList.isEmpty()){
                        
                        Iterator catCatItr = actCategoriesList.iterator();
                        Hashtable<String, String> actCatMap = new Hashtable<String, String>();
                        Element catsEl = W3CUtil.newElement(actEl, "Categories");
                        while(catCatItr.hasNext()){
                            actCatMap = (Hashtable<String, String>)catCatItr.next();
                            String catId = (String)actCatMap.get(DomainConstants.SELECT_ID);
                            String catName = (String)actCatMap.get(DomainConstants.SELECT_NAME);
                            Element catEl = W3CUtil.newElement(catsEl, "Category");
                            catEl.setAttribute("objectId", catId);
                            catEl.setAttribute("name", catName);
                         
                            DomainObject catObj = DomainObject.newInstance(context, catId);
                            
                            MapList docList = catObj.getRelatedObjects(context,
                                    "Simulation Content - *",
                                    "Simulation Document",
                                    objectSelects,
                                    new StringList(DomainConstants.SELECT_RELATIONSHIP_NAME),
                                    getTo,
                                    getFrom,
                                    recurseToLevel,
                                    "",
                                    "");
                            
                            if(null != docList && !docList.isEmpty()){
                                
                                Iterator docItr = docList.iterator();
                                Hashtable<String, String> docMap = new Hashtable<String, String>();
                                Element docsEl = W3CUtil.newElement(catEl, "Documents");
                                while(docItr.hasNext()){
                                    docMap = (Hashtable<String, String>)docItr.next();
                                    String docId = (String)docMap.get(DomainConstants.SELECT_ID);
                                    String docName = (String)docMap.get(DomainConstants.SELECT_NAME);
                                    String relName = (String)docMap.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
                                    Element docEl = W3CUtil.newElement(docsEl, "Document");
                                    docEl.setAttribute("objectId", docId);
                                    docEl.setAttribute("name", docName);
                                    docEl.setAttribute("relationship", relName);
                                }            
                            }
                        }
                    }
            
                }
            }
            MapList categoriesList = simObj.getRelatedObjects(context,
                    "Simulation Category",
                    "Simulation Category",
                    objectSelects,
                    null,
                    getTo,
                    getFrom,
                    recurseToLevel,
                    "",
                    "");
            
            //System.out.println("categoriesList --> " + categoriesList);
            
            if(null != categoriesList && !categoriesList.isEmpty()){
                
                Iterator categoriesItr = categoriesList.iterator();
                Hashtable<String, String> categoryMap = new Hashtable<String, String>();
                Element catsEl = W3CUtil.newElement(simEl, "Categories");
                while(categoriesItr.hasNext()){
                    categoryMap = (Hashtable<String, String>)categoriesItr.next();
                    String catId = (String)categoryMap.get(DomainConstants.SELECT_ID);
                    String catName = (String)categoryMap.get(DomainConstants.SELECT_NAME);
                    Element catEl = W3CUtil.newElement(catsEl, "Category");
                    catEl.setAttribute("objectId", catId);
                    catEl.setAttribute("name", catName);
                    
                    DomainObject catObj = DomainObject.newInstance(context, catId);
                    
                    MapList docList = catObj.getRelatedObjects(context,
                            "Simulation Content - *",
                            "Simulation Document",
                            objectSelects,
                            new StringList(DomainConstants.SELECT_RELATIONSHIP_NAME),
                            getTo,
                            getFrom,
                            recurseToLevel,
                            "",
                            "");
                    
                    if(null != docList && !docList.isEmpty()){
                        
                        Iterator docItr = docList.iterator();
                        Hashtable<String, String> docMap = new Hashtable<String, String>();
                        Element docsEl = W3CUtil.newElement(catEl, "Documents");
                        while(docItr.hasNext()){
                            docMap = (Hashtable<String, String>)docItr.next();
                            String docId = (String)docMap.get(DomainConstants.SELECT_ID);
                            String docName = (String)docMap.get(DomainConstants.SELECT_NAME);
                            String relName = (String)docMap.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
                            Element docEl = W3CUtil.newElement(docsEl, "Document");
                            docEl.setAttribute("objectId", docId);
                            docEl.setAttribute("name", docName);
                            docEl.setAttribute("relationship", relName);
                        }       
                    }
                }            
            }
            //System.out.println("XMl --> " + W3CUtil.saveXml(resultDoc, true));
            //return Response.ok(W3CUtil.saveXml(resultDoc, true)).build();
            return Response.ok(Utility.saveXml(resultDoc, true)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @GET
    @Path("/getSummary")
    @Produces({ "application/ds-json", "application/xml" })
    public Response getSummary(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("oid") String oid) {

        Document resultDoc;
        HashMap simulationMap = new HashMap();
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {

            resultDoc = W3CUtil.newDocument();
            Element simEl = resultDoc.createElement("Simulation");
            DomainObject simObj = DomainObject.newInstance(context, oid);
            SIMULATIONS sim = new SIMULATIONS(oid);
            List parameters = sim.getParameterList(context);
            //System.out.println("###parameters --> " + parameters);
            simEl.setAttribute("objectId", oid);
            resultDoc.appendChild(simEl);
            Element paramsEl = W3CUtil.newElement(simEl, "Parameters");
            if(null != parameters && !parameters.isEmpty()){
                for(int k = 0; k < parameters.size(); k++){
                    Element paramEl = W3CUtil.newElement(paramsEl, "Parameter");
                    Map paramMap = (Map)parameters.get(k);
                    if(null != paramMap){
                        String paramName = (String)paramMap.get("name");
                        paramEl.setAttribute("name", paramName);
                        String paramVal = ExpressionUtil.evaluate(context, oid, (String)paramMap.get("Expression"));
                        if(null != paramVal && !paramVal.equalsIgnoreCase("")){
                            //Do Nothing
                        }
                        else{
                            paramVal = (String)paramMap.get("Value");
                        }
                        paramEl.setAttribute("value", paramVal);
                    }
                }
            }
            StringList objectSelects = new StringList(3);
            objectSelects.add(DomainConstants.SELECT_ID);
            objectSelects.add(DomainConstants.SELECT_NAME);
            objectSelects.add(DomainConstants.SELECT_DESCRIPTION);
            
            boolean getTo = false;
            boolean getFrom = true;
            short recurseToLevel = 1;            

            MapList categoriesList = simObj.getRelatedObjects(context,
                    "Simulation Category",
                    "Simulation Category",
                    objectSelects,
                    null,
                    getTo,
                    getFrom,
                    recurseToLevel,
                    "",
                    "");
            
            //System.out.println("categoriesList --> " + categoriesList);
            
            if(null != categoriesList && !categoriesList.isEmpty()){
                
                Iterator categoriesItr = categoriesList.iterator();
                Hashtable<String, String> categoryMap = new Hashtable<String, String>();
                Element catsEl = W3CUtil.newElement(simEl, "Categories");
                while(categoriesItr.hasNext()){
                    categoryMap = (Hashtable<String, String>)categoriesItr.next();
                    String catId = (String)categoryMap.get(DomainConstants.SELECT_ID);
                    String catName = (String)categoryMap.get(DomainConstants.SELECT_NAME);
                    Element catEl = W3CUtil.newElement(catsEl, "Category");
                    catEl.setAttribute("objectId", catId);
                    catEl.setAttribute("name", catName);
                    
                    DomainObject catObj = DomainObject.newInstance(context, catId);
                    
                    MapList docList = catObj.getRelatedObjects(context,
                            "Simulation Content - *",
                            "Simulation Document",
                            objectSelects,
                            new StringList(DomainConstants.SELECT_RELATIONSHIP_NAME),
                            getTo,
                            getFrom,
                            recurseToLevel,
                            "",
                            "");
                    
                    if(null != docList && !docList.isEmpty()){
                        
                        Iterator docItr = docList.iterator();
                        Hashtable<String, String> docMap = new Hashtable<String, String>();
                        Element docsEl = W3CUtil.newElement(catEl, "Documents");
                        while(docItr.hasNext()){
                            docMap = (Hashtable<String, String>)docItr.next();
                            String docId = (String)docMap.get(DomainConstants.SELECT_ID);
                            String docName = (String)docMap.get(DomainConstants.SELECT_NAME);
                            String relName = (String)docMap.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
                            Element docEl = W3CUtil.newElement(docsEl, "Document");
                            docEl.setAttribute("objectId", docId);
                            docEl.setAttribute("name", docName);
                            docEl.setAttribute("relationship", relName);
                        }       
                    }
                }            
            }
            //System.out.println("XMl --> " + W3CUtil.saveXml(resultDoc, true));
            //return Response.ok(W3CUtil.saveXml(resultDoc, true)).build();
            return Response.ok(Utility.saveXml(resultDoc, true)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
    
    
    @POST
    @Path("/executeOLD")
    @Produces({ "application/ds-json", "application/xml" })
    public Response executeOld(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @javax.ws.rs.core.Context HttpServletResponse response,
            @DefaultValue("") @QueryParam("pid") String pid) {
            //@DefaultValue("") @FormParam("executionOptionsData") String executionOptionsData) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        // XTC - Set options
        /*try {
            if (executionOptionsData != null
                    && !executionOptionsData.trim().equals("")) {
                Simulation.setExecutionOptions(context, pid, new JSONObject(
                        executionOptionsData));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }*/

        // XTC - Submit job
        String jsessionId = "JSESSIONID=" + request.getSession().getId();
        System.out.println("###jsessionId --> " + jsessionId);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(null).build();
        //CloseableHttpClient httpClient = HttpClients.custom().setConnectionManagerShared(true).build();
        try {

            String createJobXmlText = null;
            String eedTicket = null;
            String eedUrl = null;
            String publicKey = null;
            String encryptedCredentials = null;
            // String startJobXmlText = null;

            String mcsUrl = Utility.getMcsUrl(request, response);

            // XTC - Create new job
            HttpPost createJobRequest = new HttpPost(mcsUrl
                    + "/resources/slmservices/jobs");
            createJobRequest.addHeader("Cookie", jsessionId);
            createJobRequest.setHeader("simpid", pid);
            createJobRequest.setHeader("MCSURL", mcsUrl);
            createJobRequest.setHeader("stepID", "");
            createJobRequest.setHeader("multibuild", "undefined");
            createJobRequest.setHeader("Content-Type", "application/ds-json");
            HttpResponse createJobResponse = httpClient
                    .execute(createJobRequest);
            String responseEntity = Utility
                    .consumeResponseEntity(createJobResponse);
            if (createJobResponse.getStatusLine().getStatusCode() != 200) {
                System.err.println(createJobResponse.toString());
                throw new Exception(
                        "Unable to access createJob service: Status Code "
                                + createJobResponse.getStatusLine()
                                        .getStatusCode());
            }
            createJobXmlText = responseEntity;
            // System.out.println("1---------------------------");
            // System.out.println(createJobXmlText);
            // System.out.println("2---------------------------");

            DocumentBuilder createJobDocumentBuilder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            InputSource createJobInputSource = new InputSource(
                    new StringReader(createJobXmlText));
            Document createJobDocument = createJobDocumentBuilder
                    .parse(createJobInputSource);
            NodeList eedTicketNodes = createJobDocument
                    .getElementsByTagName("EEDTicket");
            for (int i = 0; i < eedTicketNodes.getLength(); i++) {
                eedTicket = ((Element) eedTicketNodes.item(i)).getTextContent();
            }
            NodeList eedInfoNodes = createJobDocument
                    .getElementsByTagName("EEDInfo");
            for (int i = 0; i < eedInfoNodes.getLength(); i++) {
                eedUrl = ((Element) eedInfoNodes.item(i))
                        .getAttribute("eedWsURL");
            }

            // XTC - Check eed connection
            HttpOptions checkEedRequest = new HttpOptions(eedUrl
                    + "/execution/pubkey");
            HttpResponse checkEedResponse = httpClient.execute(checkEedRequest);
            responseEntity = Utility.consumeResponseEntity(checkEedResponse);
            if (checkEedResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to connect to EED server: Status Code "
                                + checkEedResponse.getStatusLine()
                                        .getStatusCode());
            }

            // XTC - Get public key from eed
            HttpGet getPublicKeyRequest = new HttpGet(eedUrl
                    + "/execution/pubkey");
            getPublicKeyRequest.setHeader("EEDTicket", eedTicket);
            // getPublicKeyRequest.setHeader("Host", "");
            // getPublicKeyRequest.setHeader("Origin", "");
            HttpResponse getPublicKeyResponse = httpClient
                    .execute(getPublicKeyRequest);
            responseEntity = Utility
                    .consumeResponseEntity(getPublicKeyResponse);
            if (getPublicKeyResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to access getPublicKey service: Status Code "
                                + getPublicKeyResponse.getStatusLine()
                                        .getStatusCode());
            }
            publicKey = responseEntity;

            // XTC - Get encrypted credentials from mcs
            HttpPost getCredentialsRequest = new HttpPost(mcsUrl
                    + "/resources/slmservices/data/getEncryptedCreds");
            getCredentialsRequest.setHeader("Cookie", jsessionId);
            getCredentialsRequest.setHeader("pubKey", publicKey);
            // getCredentialsRequest.setHeader("Host", "");
            // getCredentialsRequest.setHeader("Origin", "");
            getCredentialsRequest.setEntity(new ByteArrayEntity(publicKey
                    .toString().getBytes("UTF8")));
            HttpResponse getCredentialsResponse = httpClient
                    .execute(getCredentialsRequest);
            responseEntity = Utility
                    .consumeResponseEntity(getCredentialsResponse);
            if (getCredentialsResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to access getEncryptedCredentials service: Status Code "
                                + getPublicKeyResponse.getStatusLine()
                                        .getStatusCode());
            }
            encryptedCredentials = responseEntity;

            // XTC - Start job
            HttpPost startJobRequest = new HttpPost(eedUrl
                    + "/execution/run/workflow");
            startJobRequest.setHeader("ApplicationData", createJobXmlText);
            startJobRequest.setHeader("EEDTicket", eedTicket);
            startJobRequest.setHeader("ResourceCredentials",
                    encryptedCredentials);
            startJobRequest.setHeader("RunInfo",
                    "<RunInfo logLevel='Debug' submissionHost=''></RunInfo>");
            // startJobRequest.setHeader("Host", "");
            // startJobRequest.setHeader("Origin", "");
            HttpResponse startJobResponse = httpClient.execute(startJobRequest);
            responseEntity = Utility.consumeResponseEntity(startJobResponse);
            if (startJobResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to access startJob service: Status Code "
                                + getPublicKeyResponse.getStatusLine()
                                        .getStatusCode());
            }

            // XTC - Return the job details
            Map<String, String> job = new HashMap<String, String>(0);
            if (responseEntity.indexOf("<ResourceId>") > 0) {
                String jobPid = responseEntity.substring(
                        responseEntity.indexOf("<ResourceId>")
                                + "<ResourceId>".length(),
                        responseEntity.indexOf("</ResourceId>")).trim();
                List<String> selects = new ArrayList<String>(0);
                selects.add(SimulationUtil.getSelectAttributeTitleString());
                selects.addAll(Utility.BUSINESS_OBJECT_ATTRIBUTE_SELECTS);
                job.putAll(Utility.getMqlObjectAttributes(context, jobPid,
                        selects));
            } else {
                throw new Exception("Unable to obtain Job PID");
            }
            return Response.ok(Utility.toObjectDetailsJson(job).toString())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
    
    
    @POST
    @Path("/execute")
    @Produces({ "application/ds-json", "application/xml" })
    public Response execute(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @javax.ws.rs.core.Context HttpServletResponse response,
            @DefaultValue("") @QueryParam("pid") String pid) {
            //@DefaultValue("") @FormParam("executionOptionsData") String executionOptionsData) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        // XTC - Set options
        /*try {
            if (executionOptionsData != null
                    && !executionOptionsData.trim().equals("")) {
                Simulation.setExecutionOptions(context, pid, new JSONObject(
                        executionOptionsData));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }*/

        // XTC - Submit job
        String jsessionId = "JSESSIONID=" + request.getSession().getId();
        System.out.println("###jsessionId --> " + jsessionId);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(null).build();
        //CloseableHttpClient httpClient = HttpClients.custom().setConnectionManagerShared(true).build();
        try {

            String createJobXmlText = null;
            String eedTicket = null;
            String eedUrl = null;
            String publicKey = null;
            String encryptedCredentials = null;
            // String startJobXmlText = null;

            String mcsUrl = Utility.getMcsUrl(request, response);

            // XTC - Create new job
            /*HttpPost createJobRequest = new HttpPost(mcsUrl
                    + "/resources/slmservices/jobs");
            createJobRequest.addHeader("Cookie", jsessionId);
            createJobRequest.setHeader("simpid", pid);
            createJobRequest.setHeader("MCSURL", mcsUrl);
            createJobRequest.setHeader("stepID", "");
            createJobRequest.setHeader("multibuild", "undefined");
            createJobRequest.setHeader("Content-Type", "application/ds-json");
            HttpResponse createJobResponse = httpClient
                    .execute(createJobRequest);
            String responseEntity = Utility
                    .consumeResponseEntity(createJobResponse);
            if (createJobResponse.getStatusLine().getStatusCode() != 200) {
                System.err.println(createJobResponse.toString());
                throw new Exception(
                        "Unable to access createJob service: Status Code "
                                + createJobResponse.getStatusLine()
                                        .getStatusCode());
            }
            createJobXmlText = responseEntity;*/
            createJobXmlText = IsightWUtil.buildExeJob(context, pid, mcsUrl, "", true, null, "");
            System.out.println("###createJobXmlText --> " + createJobXmlText);
            // System.out.println("1---------------------------");
            // System.out.println(createJobXmlText);
            // System.out.println("2---------------------------");

            DocumentBuilder createJobDocumentBuilder = DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
            InputSource createJobInputSource = new InputSource(
                    new StringReader(createJobXmlText));
            Document createJobDocument = createJobDocumentBuilder
                    .parse(createJobInputSource);
            NodeList eedTicketNodes = createJobDocument
                    .getElementsByTagName("EEDTicket");
            for (int i = 0; i < eedTicketNodes.getLength(); i++) {
                eedTicket = ((Element) eedTicketNodes.item(i)).getTextContent();
            }
            NodeList eedInfoNodes = createJobDocument
                    .getElementsByTagName("EEDInfo");
            for (int i = 0; i < eedInfoNodes.getLength(); i++) {
                eedUrl = ((Element) eedInfoNodes.item(i))
                        .getAttribute("eedWsURL");
            }

            // XTC - Check eed connection
            HttpOptions checkEedRequest = new HttpOptions(eedUrl
                    + "/execution/pubkey");
            HttpResponse checkEedResponse = httpClient.execute(checkEedRequest);
            String responseEntity = Utility.consumeResponseEntity(checkEedResponse);
            if (checkEedResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to connect to EED server: Status Code "
                                + checkEedResponse.getStatusLine()
                                        .getStatusCode());
            }

            // XTC - Get public key from eed
            HttpGet getPublicKeyRequest = new HttpGet(eedUrl
                    + "/execution/pubkey");
            getPublicKeyRequest.setHeader("EEDTicket", eedTicket);
            // getPublicKeyRequest.setHeader("Host", "");
            // getPublicKeyRequest.setHeader("Origin", "");
            HttpResponse getPublicKeyResponse = httpClient
                    .execute(getPublicKeyRequest);
            responseEntity = Utility
                    .consumeResponseEntity(getPublicKeyResponse);
            if (getPublicKeyResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to access getPublicKey service: Status Code "
                                + getPublicKeyResponse.getStatusLine()
                                        .getStatusCode());
            }
            publicKey = responseEntity;
            System.out.println("###publicKey --> " + publicKey);

            // XTC - Get encrypted credentials from mcs
            /*HttpPost getCredentialsRequest = new HttpPost(mcsUrl
                    + "/resources/slmservices/data/getEncryptedCreds");
            getCredentialsRequest.setHeader("Cookie", jsessionId);
            getCredentialsRequest.setHeader("pubKey", publicKey);
            // getCredentialsRequest.setHeader("Host", "");
            // getCredentialsRequest.setHeader("Origin", "");
            getCredentialsRequest.setEntity(new ByteArrayEntity(publicKey
                    .toString().getBytes("UTF8")));
            HttpResponse getCredentialsResponse = httpClient
                    .execute(getCredentialsRequest);
            responseEntity = Utility
                    .consumeResponseEntity(getCredentialsResponse);
            if (getCredentialsResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to access getEncryptedCredentials service: Status Code "
                                + getPublicKeyResponse.getStatusLine()
                                        .getStatusCode());
            }
            encryptedCredentials = responseEntity;*/
            InputStream is = new ByteArrayInputStream(Charset.forName("UTF8").encode(publicKey).array());
            InputStream kt = new ByteArrayInputStream(publicKey.toString().getBytes("UTF8"));
            encryptedCredentials = IsightWUtil.getEncryptedCreds(context, kt);
            //System.out.println("###encryptedCredentials --> " + encryptedCredentials);

            String jobXML = createJobXmlText.replaceAll("\n", "").replaceAll("\r", "");
            //System.out.println("###createJobXmlText after removing line breaks--> " + jobXML);
            
            // XTC - Start job
            HttpPost startJobRequest = new HttpPost(eedUrl
                    + "/execution/run/workflow");
            startJobRequest.setHeader("ApplicationData", jobXML);
            startJobRequest.setHeader("EEDTicket", eedTicket);
            startJobRequest.setHeader("ResourceCredentials",
                    encryptedCredentials);
            startJobRequest.setHeader("RunInfo",
                    "<RunInfo logLevel='Debug' submissionHost=''></RunInfo>");
            // startJobRequest.setHeader("Host", "");
            // startJobRequest.setHeader("Origin", "");
            HttpResponse startJobResponse = httpClient.execute(startJobRequest);
            responseEntity = Utility.consumeResponseEntity(startJobResponse);
            if (startJobResponse.getStatusLine().getStatusCode() != 200) {
                throw new Exception(
                        "Unable to access startJob service: Status Code "
                                + startJobResponse.getStatusLine()
                                        .getStatusCode());
            }

            // XTC - Return the job details
            Map<String, String> job = new HashMap<String, String>(0);
            if (responseEntity.indexOf("<ResourceId>") > 0) {
                String jobPid = responseEntity.substring(
                        responseEntity.indexOf("<ResourceId>")
                                + "<ResourceId>".length(),
                        responseEntity.indexOf("</ResourceId>")).trim();
                List<String> selects = new ArrayList<String>(0);
                selects.add(SimulationUtil.getSelectAttributeTitleString());
                selects.addAll(Utility.BUSINESS_OBJECT_ATTRIBUTE_SELECTS);
                job.putAll(Utility.getMqlObjectAttributes(context, jobPid,
                        selects));
            } else {
                throw new Exception("Unable to obtain Job PID");
            }
            //return Response.ok(Utility.toObjectDetailsJson(job).toString()).build();
            return Response.ok(responseEntity).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }    
    
    @SuppressWarnings({ "rawtypes", "deprecation" })
    @GET
    @Path("/getDetails")
    @Produces({ "application/ds-json", "application/xml" })
    public Response get(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("oid") String oid) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {

            return Response.ok(Utility.getSimulationDetails(context, oid).toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    } 
    
    @POST
    @Path("/setSimulationAttributeValue")
    @Produces({ "application/ds-json", "application/xml" })
    public Response setSimulationAttributeValue(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @javax.ws.rs.core.Context HttpServletResponse response,
            @DefaultValue("") @QueryParam("id") String id,
            @DefaultValue("") @QueryParam("name") String name,
            @DefaultValue("") @FormParam("value") String value) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {

            Utility.setSimulationAttributeValue(context, id, name, value);
            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
    
    @POST
    @Path("/setSimulationParameterValue")
    @Produces({ "application/ds-json", "application/xml" })
    public Response setSimulationParameterValue(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @javax.ws.rs.core.Context HttpServletResponse response,
            @DefaultValue("") @QueryParam("id") String id,
            @DefaultValue("") @QueryParam("name") String name,
            @DefaultValue("") @FormParam("value") String value) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {

            Utility.setSimulationParameterValue(context, id, name, value);
            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }  

	//Added for ALMREQ#30755 2DARY PACK CONFIG attribute will be accesible via Webservices. (DT18X3-270) by MSH7 --- Start
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@GET
    @Path("/attributeValue")
	@Produces(MediaType.TEXT_PLAIN) 
    public Response setSecondaryPackConfigAttrValue(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @javax.ws.rs.core.Context HttpServletResponse response,
            @DefaultValue("") @QueryParam("Oid") String id,
            @DefaultValue("") @QueryParam("name") String name,
            @DefaultValue("") @QueryParam("value") String value,
            @DefaultValue("") @QueryParam("Mode") String mode) {
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {
        	
			String sReturnMsg = "";
			sReturnMsg=getAttributeValue(context, id, name, value, mode);

			return Response.status(200).entity(sReturnMsg).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
	
	//Added for DT18X15-346 ALM40553 Attribute ReadWrite WebService should accept TNR as an alternative to OID to ease adoption
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@GET
    @Path("/attributeValueByTNR")
	@Produces(MediaType.TEXT_PLAIN) 
	 public Response setSecondaryPackConfigAttrValue(
	            @javax.ws.rs.core.Context HttpServletRequest request,
	            @javax.ws.rs.core.Context HttpServletResponse response,
	            @DefaultValue("") @QueryParam("Type") String type,
	            @DefaultValue("") @QueryParam("Name") String name,
	            @DefaultValue("") @QueryParam("Rev") String rev,
	            @DefaultValue("") @QueryParam("AttrName") String attrName,
	            @DefaultValue("") @QueryParam("value") String value,
	            @DefaultValue("") @QueryParam("Mode") String mode) {
		 Context context = null;
	        try {
	            context = authenticate(request);
	        } catch (Exception e) {
	            return Response.status(401).entity("Unauthorized Access").build();
	        }
	        
	        try {
	        	String sReturnMsg = "";
	        	IPManagement ipMgmt=new IPManagement(context);
	        	MapList mlObjectInfo=ipMgmt.findObject(context, type, name, rev, new StringList(DomainConstants.SELECT_ID));
	        	if(!mlObjectInfo.isEmpty()) {
	        		Map<String,String> mpObjInfo=(Map<String, String>) mlObjectInfo.get(0);
	        		String strObjId=mpObjInfo.get(DomainConstants.SELECT_ID);
        			sReturnMsg=getAttributeValue(context, strObjId, attrName, value, mode);
	        	}
	        	return Response.status(200).entity(sReturnMsg).build();
	        }catch (Exception e) {
	            return Response.status(500).entity(e.getMessage()).build();
	        }
	 }
	
	/**
	 * Actual processing logic for get/set attribute value
	 * @param context
	 * @param strObjId
	 * @param strAttrName
	 * @param strValue
	 * @param strMode
	 * @return String
	 * @throws Exception
	 */
	private String getAttributeValue(Context context,String strObjId,String strAttrName, String strValue, String strMode) throws Exception {
		String strReturnMsg = "";
		boolean bHasReadAccess = false;
		boolean bHasModAccess = false;
		if(UIUtil.isNotNullAndNotEmpty(strObjId)){
			DomainObject doObj=DomainObject.newInstance(context,strObjId);
			Access access       = doObj.getAccessMask(context);
			bHasReadAccess = AccessUtil.hasReadAccess(access);
			bHasModAccess = AccessUtil.hasReadWriteAccess(access);
			
			
			//START: DT18X15-345 ALM40552 : Modified the sequence of check and added mode check with access one, to avoid incorrect message displayed to user
			if(UIUtil.isNullOrEmpty(strMode)){
				strReturnMsg =MESSAGE_NO_MODE ;
			}else {	
				if(READ_MODE.equalsIgnoreCase(strMode) && !bHasReadAccess){
				strReturnMsg = MESSAGE_NO_READ_ACCESS;
				}else if(WRITE_MODE.equalsIgnoreCase(strMode) && !bHasModAccess){
				strReturnMsg = MESSAGE_NO_MODIFY_ACCESS;
				}else {
					strReturnMsg = Utility.setOrGetAttributeValueMethod(context, doObj, strAttrName, strValue, strMode);
					if(READ_MODE.equalsIgnoreCase(strMode))
						strReturnMsg =MESSAGE_ATTR_VALUE+strReturnMsg;
					else 
						strReturnMsg = MESSAGE_SUCCESSFUL_MODIFICATION;
				} 
			}
			//END: DT18X15-345 ALM40552 : Modified the sequence of check and added mode check with access one, to avoid incorrect message displayed to user
		} else {
			strReturnMsg = MESSAGE_NO_OBJECTID;
		}
		return strReturnMsg;
	}
	

    //Added for ALMREQ#30755 2DARY PACK CONFIG attribute will be accesible via Webservices. (DT18X3-270) by MSH7 --- End	
    
}
    