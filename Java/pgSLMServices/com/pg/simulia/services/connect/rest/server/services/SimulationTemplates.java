package com.pg.simulia.services.connect.rest.server.services;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import matrix.db.Context;
import matrix.util.StringList;

import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.smaslm.matrix.common.SimulationUtil;
import com.dassault_systemes.smaslm.matrix.server.SlmUtil;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;

@Path("/simulationTemplates")
@Produces({ "application/xml" })
public class SimulationTemplates extends RestService {

    @SuppressWarnings("rawtypes")
    @GET
    @Path("/find")
    @Produces({ "application/ds-json", "application/xml" })
    public Response find(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("title") String title,
            @DefaultValue("") @QueryParam("name") String name,
            @DefaultValue("") @QueryParam("revision") String revision) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        
        // XTC - Find template
        try {
            String returnType = SimulationUtil
                    .getSchemaProperty("type_SimulationTemplate");
            StringList returnSelectList = new StringList();
            returnSelectList.addElement(DomainObject.SELECT_ID);
            returnSelectList.add("physicalid");
            returnSelectList
                    .add(SimulationUtil.getSelectAttributeTitleString());
            returnSelectList.addElement(DomainObject.SELECT_NAME);
            returnSelectList.addElement(DomainObject.SELECT_REVISION);
            MapList templateList = new MapList();
            templateList = DomainObject.findObjects(context, returnType,
                    SlmUtil.getVaults(context), "", returnSelectList);
            DomainObject templateObject = new DomainObject();
            String templateId, templateTitle, templateName, templateRevision, templateLatestRevision;
            for (int i = 0; i < templateList.size(); i++) {
                Map template = (Map) templateList.get(i);
                templateId = template.get(DomainObject.SELECT_ID).toString();
                templateTitle = template.get(
                        SimulationUtil.getSelectAttributeTitleString())
                        .toString();
                templateName = template.get(DomainObject.SELECT_NAME)
                        .toString();
                templateRevision = template.get(DomainObject.SELECT_REVISION)
                        .toString();
                if (title.equals("") && name.equals("")) {
                    return Response.ok(
                            Utility.toObjectDetailsJson(template).toString())
                            .build();
                }
                boolean templateMatch = false;
                if (!title.equals("") && name.equals("")) {
                    if (title.equals(templateTitle)) {
                        templateMatch = true;
                    }
                }
                if (title.equals("") && !name.equals("")) {
                    if (name.equals(templateName)) {
                        templateMatch = true;
                    }
                }
                if (!title.equals("") && !name.equals("")) {
                    if (name.equals(templateName)
                            && title.equals(templateTitle)) {
                        templateMatch = true;
                    }
                }
                if (templateMatch) {
                    if (!revision.equals("")) {
                        if (revision.equals(templateRevision)) {
                            return Response.ok(
                                    Utility.toObjectDetailsJson(template)
                                            .toString()).build();
                        }
                    } else {
                        templateObject.setId(templateId);
                        templateLatestRevision = templateObject
                                .getLastRevision(context).getRevision();
                        if (templateRevision.equals(templateLatestRevision)) {
                            return Response.ok(
                                    Utility.toObjectDetailsJson(template)
                                            .toString()).build();
                        }
                    }
                }
            }
            return Response
                    .status(500)
                    .entity("Unable to locate template for title=\"" + title
                            + "\" and name=\"" + name + "\" and revision=\""
                            + revision + "\"").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

}
