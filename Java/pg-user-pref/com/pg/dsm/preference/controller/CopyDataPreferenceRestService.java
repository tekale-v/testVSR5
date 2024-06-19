package com.pg.dsm.preference.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.pg.dsm.preference.interfaces.ICopyDataPreferenceRepository;
import com.pg.dsm.preference.models.Item;
import com.pg.dsm.preference.repository.CopyDataPreferenceRepository;
import com.pg.dsm.preference.util.JSONUtil;

@Path("/copydatapreference")
public class CopyDataPreferenceRestService extends RestService {
    static final String TYPE_APPLICATION_FORMAT = "application/json";
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * @param request
     * @return
     */
    @GET
    @Path("/all")
    @Produces({"application/json"})
    public Response getAllFilterData(@javax.ws.rs.core.Context HttpServletRequest request) {
        Response response = null;
        try {
            String jString = DomainConstants.EMPTY_STRING;
            JsonObjectBuilder output = Json.createObjectBuilder();
            boolean isSecurityContextMandatory = false;
            matrix.db.Context context = getAuthenticatedContext(request, isSecurityContextMandatory);
            if (null != context) {
                logger.log(Level.INFO, "3DX context is set");
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                List<Item> itemList = repository.getAllFilterData(context);
                if (null != itemList && !itemList.isEmpty()) {
                    JSONUtil jsonUtil = new JSONUtil();
                    JsonArray jsonArray = jsonUtil.toJSONArray(itemList);
                    logger.log(Level.INFO, "Converted result to Json");
                    output.add("data", jsonArray);
                    jString = output.build().toString();
                    logger.log(Level.INFO, "JSON result {0}", jString);
                } else {
                    logger.log(Level.WARNING, "There are no copy data for ALL filter");
                }
                response = Response.ok(jString).type(TYPE_APPLICATION_FORMAT).build();
                logger.log(Level.INFO, "Response (OK) is set");
            } else {
                logger.log(Level.WARNING, "3DX context not set or not connected");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred {0}", e);
            response = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return response;
    }

    /**
     * @param request
     * @return
     */
    @GET
    @Path("/packaging")
    @Produces({"application/json"})
    public Response getPackagingFilterData(@javax.ws.rs.core.Context HttpServletRequest request) {
        Response response = null;
        try {
            String jString = DomainConstants.EMPTY_STRING;
            JsonObjectBuilder output = Json.createObjectBuilder();
            boolean isSecurityContextMandatory = false;
            matrix.db.Context context = getAuthenticatedContext(request, isSecurityContextMandatory);
            if (null != context) {
                logger.log(Level.INFO, "3DX context is set");
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                List<Item> itemList = repository.getPackagingFilterData(context);
                if (null != itemList && !itemList.isEmpty()) {
                    JSONUtil jsonUtil = new JSONUtil();
                    JsonArray jsonArray = jsonUtil.toJSONArray(itemList);
                    logger.log(Level.INFO, "Converted result to Json");
                    output.add("data", jsonArray);
                    jString = output.build().toString();
                    logger.log(Level.INFO, "JSON result {0}", jString);
                } else {
                    logger.log(Level.WARNING, "There are no copy data for Packaging filter");
                }
                response = Response.ok(jString).type(TYPE_APPLICATION_FORMAT).build();
                logger.log(Level.INFO, "Response (OK) is set");
            } else {
                logger.log(Level.WARNING, "3DX context not set or not connected");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred {0}", e);
            response = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return response;
    }

    /**
     * @param request
     * @return
     */
    @GET
    @Path("/product")
    @Produces({"application/json"})
    public Response getProductFilterData(@javax.ws.rs.core.Context HttpServletRequest request) {
        Response response = null;
        try {
            String jString = DomainConstants.EMPTY_STRING;
            JsonObjectBuilder output = Json.createObjectBuilder();
            boolean isSecurityContextMandatory = false;
            matrix.db.Context context = getAuthenticatedContext(request, isSecurityContextMandatory);
            if (null != context) {
                logger.log(Level.INFO, "3DX context is set");
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                List<Item> itemList = repository.getProductFilterData(context);
                if (null != itemList && !itemList.isEmpty()) {
                    JSONUtil jsonUtil = new JSONUtil();
                    JsonArray jsonArray = jsonUtil.toJSONArray(itemList);
                    logger.log(Level.INFO, "Converted result to Json");
                    output.add("data", jsonArray);
                    jString = output.build().toString();
                    logger.log(Level.INFO, "JSON result {0}", jString);
                } else {
                    logger.log(Level.WARNING, "There are no copy data for Product filter");
                }
                response = Response.ok(jString).type(TYPE_APPLICATION_FORMAT).build();
                logger.log(Level.INFO, "Response (OK) is set");
            } else {
                logger.log(Level.WARNING, "3DX context not set or not connected");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred {0}", e);
            response = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return response;
    }

    /**
     * @param request
     * @return
     */
    @GET
    @Path("/rawmaterial")
    @Produces({"application/json"})
    public Response getRawMaterialFilterData(@javax.ws.rs.core.Context HttpServletRequest request) {
        Response response = null;
        try {
            String jString = DomainConstants.EMPTY_STRING;
            JsonObjectBuilder output = Json.createObjectBuilder();
            boolean isSecurityContextMandatory = false;
            matrix.db.Context context = getAuthenticatedContext(request, isSecurityContextMandatory);
            if (null != context) {
                logger.log(Level.INFO, "3DX context is set");
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                List<Item> itemList = repository.getRawMaterialFilterData(context);
                if (null != itemList && !itemList.isEmpty()) {
                    JSONUtil jsonUtil = new JSONUtil();
                    JsonArray jsonArray = jsonUtil.toJSONArray(itemList);
                    logger.log(Level.INFO, "Converted result to Json");
                    output.add("data", jsonArray);
                    jString = output.build().toString();
                    logger.log(Level.INFO, "JSON result {0}", jString);
                } else {
                    logger.log(Level.WARNING, "There are no copy data for Raw Material filter");
                }
                response = Response.ok(jString).type(TYPE_APPLICATION_FORMAT).build();
                logger.log(Level.INFO, "Response (OK) is set");
            } else {
                logger.log(Level.WARNING, "3DX context not set or not connected");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred {0}", e);
            response = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return response;
    }

    /**
     * @param request
     * @return
     */
    @GET
    @Path("/techspec")
    @Produces({"application/json"})
    public Response getTechnicalSpecificationFilterData(@javax.ws.rs.core.Context HttpServletRequest request) {
        Response response = null;
        try {
            String jString = DomainConstants.EMPTY_STRING;
            JsonObjectBuilder output = Json.createObjectBuilder();
            boolean isSecurityContextMandatory = false;
            matrix.db.Context context = getAuthenticatedContext(request, isSecurityContextMandatory);
            if (null != context) {
                logger.log(Level.INFO, "3DX context is set");
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                List<Item> itemList = repository.getTechnicalSpecificationFilterData(context);
                if (null != itemList && !itemList.isEmpty()) {
                    JSONUtil jsonUtil = new JSONUtil();
                    JsonArray jsonArray = jsonUtil.toJSONArray(itemList);
                    logger.log(Level.INFO, "Converted result to Json");
                    output.add("data", jsonArray);
                    jString = output.build().toString();
                    logger.log(Level.INFO, "JSON result {0}", jString);
                } else {
                    logger.log(Level.WARNING, "There are no copy data for Technical Spec filter");
                }
                response = Response.ok(jString).type(TYPE_APPLICATION_FORMAT).build();
                logger.log(Level.INFO, "Response (OK) is set");
            } else {
                logger.log(Level.WARNING, "3DX context not set or not connected");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception occurred {0}", e);
            response = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return response;
    }
}
