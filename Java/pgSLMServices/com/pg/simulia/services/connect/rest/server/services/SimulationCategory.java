package com.pg.simulia.services.connect.rest.server.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import matrix.db.Context;
import com.dassault_systemes.platform.restServices.RestService;

@Path("/simulationCategory")
@Produces({ "application/xml" })
public class SimulationCategory extends RestService {

    @GET
    @Path("/addSimulationReference")
    @Produces({ "application/ds-json", "application/xml" })
    public Response addSimulationReference(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("catID") String catID,
            @DefaultValue("") @QueryParam("docID") String docID) {

        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        
        try {

            Utility.addSimulationReference(context, catID, docID);
            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }
    
    @GET
    @Path("/removeSimulationReference")
    @Produces({ "application/ds-json", "application/xml" })
    public Response removeSimulationReference(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("catID") String catID,
            @DefaultValue("") @QueryParam("docID") String docID) {

        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {

            Utility.removeSimulationReference(context, catID, docID);
            return Response.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }    

}
