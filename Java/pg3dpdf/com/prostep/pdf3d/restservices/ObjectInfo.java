package com.prostep.pdf3d.restservices;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.util.SelectList;

/**
 * Class for exposing REST services
 * 
 * @author lockstein
 *
 */
@Path("/details")
@Produces({ MediaType.APPLICATION_JSON })
public class ObjectInfo extends RestService {

    /**
     * get info for specific object
     * 
     * @param request
     * @param physicalid
     * @return json
     */
    @GET
    @Path("/object/{physicalid}")
    public Response getCollectionByID(@Context HttpServletRequest request, @PathParam("physicalid") String physicalid) {
    	matrix.db.Context context = getAuthenticatedContext(request, true);
    	final String ATTRIBUTE_PLMENTITY_PLM_EXTERNALID = PropertyUtil.getSchemaProperty(context, "attribute_PLMEntity.PLM_ExternalID");
    	final String ATTRIBUTE_PLMENTITY_V_NAME = PropertyUtil.getSchemaProperty(context, "attribute_PLMEntity.PLM_ExternalID");
		final String SELECT_ATTRIBUTE_PLMENTITY_PLM_EXTERNALID = "attribute[" + ATTRIBUTE_PLMENTITY_PLM_EXTERNALID + "]";
		final String SELECT_ATTRIBUTE_PLMENTITY_V_NAME = "attribute[" + ATTRIBUTE_PLMENTITY_V_NAME + "]";
        JsonObject response = Json.createObjectBuilder().build();
        JsonObjectBuilder json = Json.createObjectBuilder();
        DomainObject obj = null;
        try {
            obj = DomainObject.newInstance(context, physicalid);
           
            // basic info
            SelectList objSelectList = new SelectList(7);
            objSelectList.add(DomainConstants.SELECT_TYPE);
            objSelectList.add(DomainConstants.SELECT_NAME);
            objSelectList.add(DomainConstants.SELECT_REVISION);
            objSelectList.add(DomainConstants.SELECT_ID);
            objSelectList.add(DomainConstants.SELECT_OWNER);
            
            objSelectList.add(SELECT_ATTRIBUTE_PLMENTITY_PLM_EXTERNALID);
            objSelectList.add(SELECT_ATTRIBUTE_PLMENTITY_V_NAME);
            
            Map<?, ?> objInfoMap = obj.getInfo(context, objSelectList);
            
            json.add(DomainConstants.SELECT_TYPE, (String) objInfoMap.get(DomainConstants.SELECT_TYPE));
            json.add(DomainConstants.SELECT_NAME, (String) objInfoMap.get(DomainConstants.SELECT_NAME));
            json.add(DomainConstants.SELECT_REVISION, (String) objInfoMap.get(DomainConstants.SELECT_REVISION));
            json.add(DomainConstants.SELECT_ID, (String) objInfoMap.get(DomainConstants.SELECT_ID));
            json.add(DomainConstants.SELECT_OWNER, (String) objInfoMap.get(DomainConstants.SELECT_OWNER));
            json.add(ATTRIBUTE_PLMENTITY_PLM_EXTERNALID, (String) objInfoMap.get(SELECT_ATTRIBUTE_PLMENTITY_PLM_EXTERNALID));
            json.add(ATTRIBUTE_PLMENTITY_V_NAME, (String) objInfoMap.get(SELECT_ATTRIBUTE_PLMENTITY_V_NAME));
            
        } catch (Exception ex) {
            json = Json.createObjectBuilder().add("Error", ex.toString());
        } 

        response = json.build();

        return Response.status(Status.OK).entity(response.toString()).build();
    }

}
