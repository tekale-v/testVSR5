/*
 * New rest service class written for Automation Engine
 * DSM15X4-94
 * This class is built to retrieve IP Security Classification information from ENOVIA
 * 
 * Methods: 
 * -getIPSecurityForType()
 *  Usage: $baseURI/securityclass/getIPSecurityForType
 *  queryParams: type (object type in ENOVIA)
 *  returns: IP security classes based on type and user access 
 */

package com.pg.simulia.services.connect.rest.server.services;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dassault_systemes.platform.restServices.RestService;
import com.dassault_systemes.smaslm.common.util.W3CUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;
import matrix.util.StringList;

//Added for ALM#28761 Web Service to set IP Security (DT18X2-192) by MSH7 --- Start
import com.matrixone.apps.domain.util.FrameworkUtil;
import java.util.HashMap;
//Added for ALM#28761 Web Service to set IP Security (DT18X2-192) by MSH7 --- End

@Path("/securityclass")
@Produces({ "application/xml" })
public class SecurityClass extends RestService {
        
        @GET
    @Path("/getIPSecurityForType")
    @Produces({ "application/ds-json", "application/xml" })
    public Response getIPSecurityForType(@javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @QueryParam("type") String type) {
        Context context = null;
        Document resultDoc;
        try {
            context = authenticate(request);
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {
            resultDoc = W3CUtil.newDocument();
            Element simListEl = resultDoc.createElement("SearchResults");
            
            StringList resultsMapList = Utility.getIPSecurityClasses(context,type);
            
            if(null != resultsMapList && resultsMapList.isEmpty()){
                return Response.status(200).entity("No results found").build();
            }
            resultDoc.appendChild(simListEl);
                
            if(null != resultsMapList && !resultsMapList.isEmpty()){
                Iterator<?> resultsItr = resultsMapList.iterator();
                String result = DomainConstants.EMPTY_STRING;
                String strName = DomainConstants.EMPTY_STRING;
                String strType = DomainConstants.EMPTY_STRING;
                DomainObject dObj = null;
                Element simEl = null;
                Map<?, ?> mpObjInfo = null;
                StringList slObjInfo = new StringList(2);
                slObjInfo.add(DomainConstants.SELECT_NAME);
                slObjInfo.add(DomainConstants.SELECT_TYPE);
                while(resultsItr.hasNext()){
                        result = (String)resultsItr.next();
                        if(UIUtil.isNotNullAndNotEmpty(result)) {
                                dObj = DomainObject.newInstance(context,result);
                                mpObjInfo = dObj.getInfo(context,slObjInfo);
                                strName = (String) mpObjInfo.get(DomainConstants.SELECT_NAME);
                                strType = (String) mpObjInfo.get(DomainConstants.SELECT_TYPE);
                            simEl = W3CUtil.newElement(simListEl, "Object");
                            simEl.setAttribute("objectId", result);
                            simEl.setAttribute("name", strName);
                            simEl.setAttribute("type", strType);
                        }
                }
            }
            
            
            //return Response.ok(W3CUtil.saveXml(resultDoc, true)).build();
            return Response.ok(Utility.saveXml(resultDoc, true)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
        }

	//Added for ALM#28761 Web Service to set IP Security (DT18X2-192, DT18X2-199) by MSH7 --- Start
	@GET
	@Path("/addIPClassList")
	@Produces({ "application/ds-json", "application/xml" })
	public Response addIPClassList(
			@javax.ws.rs.core.Context HttpServletRequest request,
			@DefaultValue("") @QueryParam("OID") String targetID,
			@DefaultValue("") @QueryParam("IpClasslist") String ipClassSlist,
			@DefaultValue("") @QueryParam("Classification") String classification) {
		Context context = null;
		try {
			context = authenticate(request);
		} catch (Exception e) {
			return Response.status(401).entity("Unauthorized Access").build();
		}
		
		try {
			Document resultDoc = W3CUtil.newDocument();
            Element ListEl = resultDoc.createElement("Response:");
			Element El = W3CUtil.newElement(ListEl, "Message");
			resultDoc.appendChild(El);
			
			StringList slIpClassList = FrameworkUtil.split(ipClassSlist,",");
			
			//check IPClass existence
			String sReturn = Utility.checkIPClassExistence(context, slIpClassList);
			
			//Added for ALM#28761 Web Service to set IP Security (DT18X2-199) by MSH7 --- Start
			boolean bMixedClass = Utility.checkMixedClassPresent(context, slIpClassList, classification);
			boolean bNoChangeDetected = Utility.checkIPClassChange(context, targetID, slIpClassList);
			//Added for ALM#28761 Web Service to set IP Security (DT18X2-199) by MSH7 --- End
			
			if(bMixedClass){
				El.setAttribute("Error", "IP Classification provided in classification parameter is conflicting with IP class(es) provided in IpClasslist.");
			} else if(bNoChangeDetected){
				El.setAttribute("Error", "IP class(es) are already connected.");
			} else if("Classification Allowed".equalsIgnoreCase(sReturn)){
				//adding IpClass to Bus Obj
				boolean bHasFromConnectUser = Utility.addIPClasses(context, targetID, ipClassSlist, classification);
				if(bHasFromConnectUser)
					El.setAttribute("Success", "IP class(es) successfully connected/updated.");
				else
					El.setAttribute("Error", "User does not have necessary access for connecting the object.");
				
			} else if("Classification Not Allowed".equalsIgnoreCase(sReturn)){
				El.setAttribute("Error", "IP class(es) is not available for classification.");
				
			} else {
				El.setAttribute("Error", "IP class(es) does not exists.");
			}	
			return Response.ok(Utility.saveXml(resultDoc, true)).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}
	}
	//Added for ALM#28761 Web Service to set IP Security (DT18X2-192, DT18X2-199) by MSH7 --- End
}
