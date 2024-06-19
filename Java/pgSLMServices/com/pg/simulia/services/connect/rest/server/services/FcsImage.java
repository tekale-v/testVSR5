package com.pg.simulia.services.connect.rest.server.services;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;

import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.smaslm.matrix.server.ImportExportServices;
import com.matrixone.apps.common.CommonDocument;

@Path("/image")
@Produces({ "application/xml" })
public class FcsImage extends RestService {

    @GET
    @Path("/getImportTicket")
    @Produces({ "application/ds-json", "application/xml" })
    public Response getImportTicket(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @javax.ws.rs.core.Context HttpServletResponse response,
            @DefaultValue("") @QueryParam("oid") String oid,
            @DefaultValue("") @QueryParam("fileName") String fileName) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        
        // XTC - Get image ticket
        try {
            return Response.ok(
                    ImportExportServices.getImageImportTicket(context, oid,
                            fileName, Utility.getMcsUrl(request, response)))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GET
    @Path("/generateThumbnail")
    @Produces({ "application/ds-json", "application/xml" })
    public Response generateThumbnail(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("oid") String oid,
            @DefaultValue("") @QueryParam("fileName") String fileName) {

        // XTC - Authenticate and get context
        Context context = null;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        // XTC - Generate thumbnail and delete jpg if file is gif
        try {
            HashMap image = new HashMap();
            image.put("fcsEnabled", "");
            image.put("objectId", oid);
            image.put("fileName", fileName);
            JPO.invoke(context, "smaImageManager", new String[0],
                    "smaGenerateThumbnails", JPO.packArgs(image));
            BusinessObject imgBusObj = new BusinessObject(oid);
            CommonDocument imgObj = new CommonDocument(imgBusObj);
            if (fileName.toLowerCase().endsWith(".gif")) {
                String fileNameJpg = fileName.substring(0, fileName.length()
                        - ".gif".length())
                        + ".jpg";
                try {
                    imgObj.deleteFile(context, fileNameJpg, "mxImage");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

}
