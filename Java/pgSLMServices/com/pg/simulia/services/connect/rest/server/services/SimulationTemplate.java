package com.pg.simulia.services.connect.rest.server.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import matrix.db.Context;

import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.smaslm.matrix.common.SimulationUtil;
import com.dassault_systemes.smaslm.matrix.server.ProceduresServices;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkProperties;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.json.JSONArray;

@SuppressWarnings("deprecation")
@Path("/simulationTemplate")
@Produces({ "application/xml" })
public class SimulationTemplate extends RestService {

    @GET
    @Path("/getOptions")
    @Produces({ "application/ds-json", "application/xml" })
    public Response getOptions(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("oid") String oid) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        
        // XTC - Get template options
        try {

            JSONArray templateOptions = new JSONArray();
            Object[] options = ProceduresServices
                    .getTemplateInstantiationOptions(context, oid);
            for (int i = 0; i < options.length; i++) {
                templateOptions.put(options[i].toString());
            }
            return Response.ok(templateOptions.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    @POST
    @Path("/instantiate")
    @Produces({ "application/ds-json", "application/xml" })
    public Response instantiate(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("oid") String oid,
            @DefaultValue("") @FormParam("optionsXml") String optionsXml) {

        // XTC - Authenticate and get context
        Context context = null;
        //System.out.println("###optionsXml --> " + optionsXml);
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        // XTC - Instantiate template
        try {

            Map<String, String> simulation = new HashMap<String, String>(0);
            Object[] objects = ProceduresServices.instantiateTemplate(context,
                    oid, optionsXml);
            for (int i = 0; i < objects.length; i++) {
                DomainObject instantiatedObjectObject = new DomainObject();
                String simulationOid = objects[i].toString();
                //System.out.println("###simulationOid --> " + simulationOid);
                instantiatedObjectObject.setId(simulationOid);
                List<String> selects = new ArrayList<String>(0);
                selects.add(SimulationUtil.getSelectAttributeTitleString());
                selects.addAll(Utility.BUSINESS_OBJECT_ATTRIBUTE_SELECTS);
                simulation.putAll(Utility.getMqlObjectAttributes(context,
                        simulationOid, selects));
                //System.out.println("###simulation --> " + simulation);
            }
            return Response.ok(
                    Utility.toObjectDetailsJson(simulation).toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
    
    @GET
    @Path("/findAndInstantiate")
    @Produces({ "application/ds-json", "application/xml" })
    public Response findAndInstantiate(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("title") String title,
            @DefaultValue("false") @QueryParam("devMode") String devMode) {

        // XTC - Authenticate and get context
        Context context = null;
        //System.out.println("###optionsXml --> " + optionsXml);
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        // XTC - Instantiate template
        try {
            
            String attrName = FrameworkProperties.getProperty(context, "smaSimulationCentral.pgSLMServices.AttributeName");
            String objType = "Simulation Template";
            String objRev = "*";
            String objWhere = "";
            String id = "id";
            String name = "name";
            String templateId = "";
            if(null != attrName && !attrName.equalsIgnoreCase("")){
                if(devMode.equalsIgnoreCase("false")){
                    objWhere = "attribute[" + attrName + "].value == true && attribute[Title].value == '" + title + "' && current == Released && latest == true";
                }
                else{
                    objWhere = "attribute[" + attrName + "].value == true && attribute[Title].value == '" + title + "' && revision == last";
                }                
            }
            else{
                if(devMode.equalsIgnoreCase("false")){
                    objWhere = "attribute[Title].value == '" + title + "' && current == Released && latest == true";
                }
                else{
                    objWhere = "attribute[Title].value == '" + title + "' && revision == last";
                }
            }
            
            //System.out.println("objWhere --> " + objWhere);
            String templateInfo = MqlUtil.mqlCommand(context, "temp query bus $1 $2 $3 where $4 select $5 $6 dump", objType, title + "*", objRev, objWhere, id, name);
            //System.out.println("templateInfo --> " + templateInfo);
            
            if(null != templateInfo && templateInfo.indexOf(",") > 0){
                
                String oid = templateInfo.split(",")[3];

                Map<String, String> simulation = new HashMap<String, String>(0);
                JSONArray templateOptions = new JSONArray();
                Object[] options = ProceduresServices.getTemplateInstantiationOptions(context, oid);
                //System.out.println("###options --> " + options);
                for (int i = 0; i < options.length; i++) {
                    //System.out.println("###options[" + i + "].toString() --> " + options[i].toString());
                    templateOptions.put(options[i].toString());
                }
               
                
                //System.out.println("###templateOptions --> " + templateOptions);
                String templateInstantiationOptions = templateOptions.toString().substring(11, templateOptions.toString().length() -5).replace("\\", "");//.replace("\"", "\'");
                //System.out.println("###templateInstantiationOptions --> " + templateInstantiationOptions);
                          
                
                if(null != templateOptions){
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(templateInstantiationOptions));
                    Document document = builder.parse(is);
                    
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    String editedOptionsXml = writer.getBuffer().toString();

                    
                    //Object[] objects = ProceduresServices.instantiateTemplate(context, oid, templateInstantiationOptions);
                    Object[] objects = ProceduresServices.instantiateTemplate(context, oid, editedOptionsXml);
                    String simulationOid = "";
                    for (int i = 0; i < objects.length; i++) {
                        DomainObject instantiatedObjectObject = new DomainObject();
                        simulationOid = objects[i].toString();
                        //System.out.println("###simulationOid --> " + simulationOid);
                        instantiatedObjectObject.setId(simulationOid);
                        List<String> selects = new ArrayList<String>(0);
                        selects.add(SimulationUtil.getSelectAttributeTitleString());
                        selects.addAll(Utility.BUSINESS_OBJECT_ATTRIBUTE_SELECTS);
                        simulation.putAll(Utility.getMqlObjectAttributes(context,
                                simulationOid, selects));
                        //System.out.println("###simulation --> " + simulation);
                    }
                    //return Response.ok(Utility.toObjectDetailsJson(simulation).toString()).build();
                    return Response.ok(Utility.getSimulationDetails(context, simulationOid).toString()).build();
                }
                else{
                     return Response.status(500).entity("Exception in getting template options").build();
                }          
            }
            else{
                return Response.status(500).entity("No Simulation Template found").build();
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }    

}
