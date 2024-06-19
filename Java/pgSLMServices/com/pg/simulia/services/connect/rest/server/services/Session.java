package com.pg.simulia.services.connect.rest.server.services;

import java.util.ArrayList;
//import com.dassault_systemes.;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import matrix.db.Context;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PersonUtil;

import org.json.JSONObject;
import com.matrixone.servlet.Framework;
import com.matrixone.servlet.FrameworkServlet;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

@SuppressWarnings("deprecation")

@Produces({ "application/xml" })
public class Session extends RestService {

    @GET
    @Path("/version")
    @Produces({ "application/ds-json", "application/xml" })
    public Response version(@javax.ws.rs.core.Context HttpServletRequest request) {

        return Response.status(200).entity("r2015x").build();

    }

    
    @POST
    @Path("/login")
    @Produces({ "application/ds-json", "application/xml" })
    public Response login(@javax.ws.rs.core.Context HttpServletRequest request,
            @javax.ws.rs.core.Context HttpServletResponse response,
            @DefaultValue("") @FormParam("username") String username,
            @DefaultValue("") @FormParam("password") String password) {

        String jsessionId = "JSESSIONID=" + request.getSession().getId();
        String version = "";
        StringBuffer errorMessage = new StringBuffer(0);

        try {
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(null).build();
            try {
                errorMessage.append("001,");
                boolean isContextRedirectResponse = false;
                HttpPost loginServletRequest = new HttpPost(Utility.getMcsUrl(
                        request, response) + "/servlet/login");
                loginServletRequest.addHeader("Cookie", jsessionId);
                ArrayList<NameValuePair> loginServletRequestParameters = new ArrayList<NameValuePair>();
                loginServletRequestParameters.add(new BasicNameValuePair(
                        "login_name", StringUtils.newStringUtf8(Base64.decodeBase64(username.getBytes()))));
                loginServletRequestParameters.add(new BasicNameValuePair(
                        "login_password", StringUtils.newStringUtf8(Base64.decodeBase64(password.getBytes()))));
                loginServletRequest.setEntity(new UrlEncodedFormEntity(
                        loginServletRequestParameters));
                HttpResponse loginServletResponse = httpClient
                        .execute(loginServletRequest);
                errorMessage.append("002,");
                Utility.consumeResponseEntity(loginServletResponse);
                if (loginServletResponse.getStatusLine().getStatusCode() == 302) {
                    isContextRedirectResponse = true;
                }
                if (!isContextRedirectResponse) {
                    errorMessage.append("003,");
                    Response.status(401)
                            .entity("Unable to login to server: "
                                    + errorMessage.toString()).build();
                }
            } catch (Exception e) {
                errorMessage.append("004 " + e.getMessage() + ",");
                throw e;
            } finally {
                httpClient.close();
            }

			Context context = Framework.getFrameContext(request.getSession());
			
            if (context == null) {
                errorMessage.append("005,");
                Response.status(401)
                        .entity("Unable to login to server: "
                                + errorMessage.toString()).build();
            } else {

                errorMessage.append("006,");
                String securityContext = PersonUtil.getDefaultSecurityContext(
                        context, StringUtils.newStringUtf8(Base64.decodeBase64(username.getBytes())));
                //System.out.println("###securityContext --> " + securityContext);
                if (securityContext == null || "".equals(securityContext)) {
                    securityContext = PersonUtil.getSecurityContext(context,
                            null, null, null);
                }
                PersonUtil.setSecurityContext(request.getSession(),
                        securityContext);
                PersonUtil.setDefaultSecurityContext(context, securityContext);
                errorMessage.append("007,");

                version = FrameworkUtil.getApplicationVersion(context,
                        "BusinessProcessServices");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity(e.getMessage() + " :: " + errorMessage.toString())
                    .build();
        }

        return Response.status(200).entity(version).build();

    }

    @GET
    @Path("/logout")
    @Produces({ "application/ds-json", "application/xml" })
    public Response logout(@javax.ws.rs.core.Context HttpServletRequest request) {

        try {
            FrameworkServlet.doLogout(request.getSession());
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }

        return Response.status(200).build();

    }
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/setSecurityContext")
    @Produces({ "application/ds-json", "application/xml" })
    public Response setSecurityContext(
            @javax.ws.rs.core.Context HttpServletRequest request,
            @DefaultValue("") @FormParam("securityContext") String securityContext) {

		Context  context = null;
		
        JSONObject securityContextJson = new JSONObject();
        try {
            String secContext = request.getHeader("SecurityContext");
            boolean hasContext = (secContext != null) && (!secContext.isEmpty());
            if (hasContext) {
                context = getAuthenticatedContext(request, hasContext);
            } else {
                context = authenticate(request);
            }
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {
            if(null != securityContext && !securityContext.equalsIgnoreCase("")){
                Vector<String> assignedSecurityContexts = PersonUtil.getSecurityContextAssignments(context);
                if(null != assignedSecurityContexts && assignedSecurityContexts.contains(securityContext)){
                    PersonUtil.setSecurityContext(request.getSession(), securityContext);
                    PersonUtil.setDefaultSecurityContext(context, securityContext);
                    securityContextJson.put("Security Context", securityContext);
                    return Response.status(200).entity(securityContextJson.toString()).build();
                }
            }
            securityContext = PersonUtil.getDefaultSecurityContext(context);
            PersonUtil.setSecurityContext(request.getSession(), securityContext);
            securityContextJson.put("Security Context", securityContext);
            return Response.status(200).entity(securityContextJson.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{" + e.getMessage() + "}").build();
        }

    }
    
    
    @SuppressWarnings("unchecked")
    @GET
    @Path("/getSecurityContexts")
    @Produces({ "application/ds-json", "application/xml" })
    public Response getSecurityContexts(
            @javax.ws.rs.core.Context HttpServletRequest request) {

		Context context = null;
		
        JSONObject securityContextJson = new JSONObject();
        try {
            String secContext = request.getHeader("SecurityContext");
            boolean hasContext = (secContext != null) && (!secContext.isEmpty());
            if (hasContext) {
                context = getAuthenticatedContext(request, hasContext);
            } else {
                context = authenticate(request);
            }
        } catch (Exception e) {
            return Response.status(401).entity("Unauthorized Access").build();
        }

        try {
            Vector<String> assignedSecurityContexts = PersonUtil.getSecurityContextAssignments(context);
            //System.out.println("###assignedSecurityContexts --> " + assignedSecurityContexts);
            if(null != assignedSecurityContexts){
                String defaultSecurityContext = PersonUtil.getDefaultSecurityContext(context);
                securityContextJson.put("Default Security Context", defaultSecurityContext);
                String securityContext;
                for(int s = 0; s < assignedSecurityContexts.size();) {
                    securityContext = assignedSecurityContexts.get(s);
                    s++;
                    securityContextJson.put("Security Context_" + s, securityContext);
                }
            }
            return Response.status(200).entity(securityContextJson.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity("{" + e.getMessage() + "}").build();
        }

    }


}
