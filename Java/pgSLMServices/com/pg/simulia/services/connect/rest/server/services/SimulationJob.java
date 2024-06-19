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
import org.json.JSONObject;

@SuppressWarnings("deprecation")
@Path("/simulationJob")
@Produces({ "application/xml" })
public class SimulationJob extends RestService {

    @GET
    @Path("/getStatus")
    @Produces({ "application/ds-json", "application/xml" })
    public Response getStatus(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("oid") String oid) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        
        // XTC - Get the status
        try {

            JSONObject jobStatus = new JSONObject();
            com.dassault_systemes.smaslm.matrix.server.SimulationJob job = new com.dassault_systemes.smaslm.matrix.server.SimulationJob(
                    oid);
            jobStatus.put("status", job.getStatus(context).toLowerCase());
            jobStatus.put("started", job.getStartTime(context));
            jobStatus.put("ended", job.getEndTime(context));
            return Response.ok(jobStatus.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

}
