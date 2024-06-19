package com.pg.fis.integration.util;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pg.fis.integration.constants.PGFISWSConstants;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performs CAS authentication.
 *
 * @author Ryan Cuprak
 */
@SuppressWarnings( "deprecation" )
public class PGFISCASAuthenticator
{
	// https://{tenant}-{datacenter}-{cluster}.{servicename}.{domain}
    private static final String URL_PATTERN = "https://%s-%s-%s-%s.%s";
    private static final String URL_IAM_PATTERN = "https://%s-euw1-%s.%s.%s";

    public static final String USER_ODT_ID =  PGFISWSConstants.FIS_CLOUD_USERNAME;
    public static final String USER_ODT_PASSWORD = PGFISWSConstants.FIS_CLOUD_PASSWORD;
    
	public static final String CLM_USER = PGFISWSConstants.FIS_CLOUD_CLM_AGENT_ID;
	public static final String CLM_PWD = PGFISWSConstants.FIS_CLOUD_CLM_AGENT_PASSWORD;

	
	public static final String USER_ODT_ID_ON_PREM = PGFISWSConstants.ONPREM_PASSPORT_USERNAME;
	public static final String USER_ODT_PASSWORD_ON_PREM = PGFISWSConstants.ONPREM_PASSPORT_PASSWORD;

    private static final String URL_LOGIN_ACTION = "/login?action=get_auth_params";
    private static final String TICKET_GRANTING_COOKIE = "CASTGC";

    private static final Logger logger = Logger.getLogger( PGFISCASAuthenticator.class.getName( ) );


    /**
     * HTTP Context (basically a browser session)
     */
    private HttpContext httpContext;

    /**
     * Cookie jar
     */
    private CookieStore cookieStore;

    /**
     * CAS server URL
     */
    private String casServerUrl;

    /**
     * New CAS Authenticator Instamce
     * @throws Exception Thrown when something goes wrong with setting up the SSL paramters for client connections.
     */
    public PGFISCASAuthenticator(  ) throws Exception
    {
        this.casServerUrl = PGFISCASAuthenticator.getUrlForService( "iam" );
        try
        {
            httpContext = new BasicHttpContext( );
            cookieStore = new BasicCookieStore( );
            httpContext.setAttribute( ClientContext.COOKIE_STORE, cookieStore );
        }
        catch (Exception e)
        {
            throw new Exception( e );
        }
    }

	/**
	 * New CAS Authenticator Instamce
	 * 
	 * @throws Exception Thrown when something goes wrong with setting up the SSL
	 *                   paramters for client connections.
	 */
	public PGFISCASAuthenticator(String serviceName) throws Exception {
		if("on_Prem_3dPassport".equals(serviceName)) {
			this.casServerUrl = PGFISWSConstants.ONPREM_PASSPORT_URL;
		} else {
			this.casServerUrl = PGFISCASAuthenticator.getUrlForService(serviceName);
		}
		try {
			httpContext = new BasicHttpContext();
			cookieStore = new BasicCookieStore();
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

    /**
     * Generate a new {@link HttpClient} to be used in each HTTP request.   This will share
     * @return
     * @throws Exception
     */
    private HttpClient getHttpClient( ) throws Exception {
        SSLContextBuilder builder = new SSLContextBuilder( );
        builder.loadTrustMaterial( null, new TrustSelfSignedStrategy( ) );
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( builder.build( ) );

        // Set a 2 minute timeout
        RequestConfig config = RequestConfig.custom().setSocketTimeout( 120 * 1000 ).build();

        HttpClient myHttpClient = HttpClientBuilder.create().setDefaultRequestConfig( config ).setRedirectStrategy( new LaxRedirectStrategy()).build(); //HttpClients.custom( ).setSSLSocketFactory( sslsf ).build( );

        return myHttpClient;
    }


    /**
     * Performs the authentication for the given credientials, against the CAS url
     * given when the {@link PGFISCASAuthenticator} was created.
     *
     * @param username The User name, or course.
     * @param password  Password
     * @return The user name to use when creating an ENOVIA context.
     *
     * @throws Exception - thrown if there is an error
     */
    public String authenticate( String username, String password ) throws Exception
    {
        String authorizedUserName = null;

        // authenticate
        doAuthenticate( username, password );

        if ( logger.isLoggable(Level.FINEST ) )
        {
            logger.log( Level.FINEST, "**** Successfully authenticated user " + username );
        }
        return authorizedUserName;
    }

    // https://{tenant}-{datacenter}-{cluster}.{servicename}.{domain}
    public static String getUrlForService( String serviceName ) {


        StringBuilder builder = new StringBuilder( "https://" );
        builder.append( PGFISWSConstants.FIS_CLOUD_TENANT );
        builder.append( "-" );

       // if( "iam".equalsIgnoreCase( serviceName ) ) {
       //     builder.append( "euw1");
       //} else {
            builder.append( PGFISWSConstants.FIS_CLOUD_DATACENTER );
        //}

        if( PGFISWSConstants.FIS_CLOUD_CLUSTER != null && PGFISWSConstants.FIS_CLOUD_CLUSTER.length() > 0 ) {
            builder.append("-");
            builder.append(PGFISWSConstants.FIS_CLOUD_CLUSTER);
        }

        if( "iam".equalsIgnoreCase( serviceName ) ) {
            builder.append( ".iam");
        } else {
            builder.append("-");
            builder.append( serviceName );
        }

        builder.append(".");
        builder.append( PGFISWSConstants.FIS_CLOUD_DOMAIN );



        logger.warning( "Generated URL: " + builder.toString() );

        return builder.toString();
    }

    	
    public static String getUrlForService( String serviceName, boolean bOnPremInstance ) {
      
    	if("on_Prem_3dPassport".equals(serviceName)) {
    		return PGFISWSConstants.ONPREM_PASSPORT_URL;
    	} else {
    		return PGFISWSConstants.ONPREM_SPACE_URL;
    	}
       
    }
    

    /**
     * Performs the authentication
     * @param username - username
     * @param password - password
     * @return The ticket granting ticket.
     * @throws Exception
     * @throws Exception - thrown if there is an error
     */
    protected String doAuthenticate( String username, String password ) throws Exception
    {
        // get login ticket
        final String loginTicketURL = casServerUrl + URL_LOGIN_ACTION;
        String getResponse          = doGetRequest( loginTicketURL, false );
        String loginTicket          = null;
        if ( getResponse != null )
        {
            try {
                JsonObject jsonObject = new JsonParser().parse(getResponse).getAsJsonObject();
                loginTicket = jsonObject.get( "lt" ).getAsString();
            } catch ( Exception mEx) {
                throw new Exception("Failed to get login ticket" +mEx.toString());
            }
        }
        else
        {
            throw new Exception( "Failed to get login ticket" );
        }
        // authenticate
        final String loginURL = casServerUrl + "/login";
        List< NameValuePair > params = new ArrayList< NameValuePair >( );
        params.add( new BasicNameValuePair( "Accept", "application/json" ) );
        params.add( new BasicNameValuePair( "username", username ) );
        params.add( new BasicNameValuePair( "password", password ) );
        params.add( new BasicNameValuePair( "lt", loginTicket ) );
        params.add( new BasicNameValuePair( "_eventId", "submit" ) );
        doPostRequest( loginURL, params );

        // get CASTGC cookie from the response to the previous authentication request
        List< Cookie > cookieList = cookieStore.getCookies( );
        String casTGC = null;
        for ( Cookie cookie : cookieList )
        {
            if ( cookie.getName( ).equals( TICKET_GRANTING_COOKIE ) )
            {
                casTGC = cookie.getValue( );
                if ( logger.isLoggable(Level.FINEST ) )
                {
                    logger.log( Level.FINEST, String.format( "%s = %s", TICKET_GRANTING_COOKIE, casTGC ) );
                }
            }
        }
        if ( casTGC == null )
        {
            throw new Exception( "Failed to authenticate user" );
        }

        return loginTicket;
    }




    public HttpCallResponse doPatchRequest( String url, List< Header > headers, String body ) throws Exception {

        if ( logger.isLoggable(Level.FINEST ) )
        {
            logger.log( Level.FINEST, "######################" );
            logger.log( Level.FINEST, "PATCH request URL = [" + url + "], parameters = [" + headers.toString( ) + "]" );
        }


        HttpPatch httppatch = new HttpPatch( url );
        if( body != null && body.length() > 0 ) {
            StringEntity inputBody = new StringEntity(body, "UTF-8");
            httppatch.setEntity(inputBody);
        }
        headers.add( new BasicHeader( "X-Requested-With", "script" ) );
        for( Header header: headers ){
            httppatch.setHeader( header );
        }

        HttpResponse response = getHttpClient().execute( httppatch, httpContext );
        StatusLine status = response.getStatusLine( );
        HttpEntity entity = response.getEntity( );


        HttpCallResponse myResponse = new HttpCallResponse();
        myResponse.setStatus( status.getStatusCode() );
        myResponse.setReponse( EntityUtils.toString( entity ).trim( ) );

        logger.warning( "Result Code: " + status.getStatusCode() + " Response: " + myResponse.getReponse() );

        return myResponse;

    }
    
    public HttpCallResponse doPutRequest( String url, List< Header > headers, String body ) throws Exception {

        if ( logger.isLoggable(Level.FINEST ) )
        {
            logger.log( Level.FINEST, "######################" );
            logger.log( Level.FINEST, "PATCH request URL = [" + url + "], parameters = [" + headers.toString( ) + "]" );
        }


        HttpPut httpput = new HttpPut( url );
        if( body != null && body.length() > 0 ) {
            StringEntity inputBody = new StringEntity(body, "UTF-8");
            httpput.setEntity(inputBody);
        }
        headers.add( new BasicHeader( "X-Requested-With", "script" ) );
        for( Header header: headers ){
        	httpput.setHeader( header );
        }

        HttpResponse response = getHttpClient().execute( httpput, httpContext );
        StatusLine status = response.getStatusLine( );
        HttpEntity entity = response.getEntity( );


        HttpCallResponse myResponse = new HttpCallResponse();
        myResponse.setStatus( status.getStatusCode() );
        myResponse.setReponse( EntityUtils.toString( entity ).trim( ) );

        logger.warning( "Result Code: " + status.getStatusCode() + " Response: " + myResponse.getReponse() );

        return myResponse;

    }



    public HttpCallResponse doPostRequest(String url, List<Header> headers, String body ) throws Exception {

        if ( logger.isLoggable(Level.FINEST ) )
        {
            logger.log( Level.FINEST, "######################" );
            logger.log( Level.FINEST, "POST request URL = [" + url + "], parameters = [" + headers.toString( ) + "]" );
        }


        HttpPost httppost = new HttpPost( url );

        if( body != null && body.length() > 0 ) {
            StringEntity inputBody = new StringEntity(body, "UTF-8");
            inputBody.setContentType( "application/json" );
            httppost.setEntity(inputBody);
        }
        headers.add( new BasicHeader( "X-Requested-With", "script" ) );
        for( Header header: headers ){
            httppost.setHeader( header );
        }



        long startTime = System.currentTimeMillis( );
        HttpResponse response = getHttpClient().execute( httppost, httpContext );
//        response = httpClient.execute( httppost, httpContext );
        StatusLine status = response.getStatusLine( );
        HttpEntity entity = response.getEntity( );

        long totalTime = System.currentTimeMillis() - startTime;


        HttpCallResponse myResponse = new HttpCallResponse();
        myResponse.setStatus( status.getStatusCode() );
        myResponse.setReponse( EntityUtils.toString( entity ).trim( ) );

        logger.warning( "Result Code: " + status.getStatusCode() + " Response: " + myResponse.getReponse() );

        return myResponse;
    }


    /**
     * perform POST request for given URL and given parameters.
     * @param url The URL that receives the POST request.
     * @param params The list of {@link NameValuePair} entries that make up the form being submitted.
     * @return The result entity of the POST request.
     * @throws Exception
     */
    public String doPostRequest( final String url, final List< NameValuePair > params ) throws Exception
    {

        try
        {
            if ( logger.isLoggable(Level.FINEST ) )
            {
                logger.log( Level.FINEST, "######################" );
                logger.log( Level.FINEST, "POST request URL = [" + url + "], parameters = [" + params.toString( ) + "]" );
            }

            HttpEntity postEntity = new UrlEncodedFormEntity( params, "UTF-8" );
            HttpPost httppost = new HttpPost( url );
            httppost.setEntity( postEntity );
            if ( logger.isLoggable(Level.FINEST ) )
            {
                logger.log( Level.FINEST, httppost.toString( ) );
                for ( Header hdr : httppost.getAllHeaders( ) )
                {
                    logger.log( Level.FINEST, String.format( "  %s=%s", hdr.getName( ), hdr.getValue( ) ) );
                }
            }

            long startTime = System.nanoTime( );
            HttpResponse response = getHttpClient().execute( httppost, httpContext );
            StatusLine status = response.getStatusLine( );
            HttpEntity entity = response.getEntity( );
            if ( entity != null )
            {
                String postResponse = EntityUtils.toString( entity ).trim( );
                if ( logger.isLoggable(Level.FINEST ) )
                {
                    logger.log( Level.FINEST, "Response POST= " + postResponse );
                    logger.log( Level.FINEST, "Status POST= " + status.getStatusCode( ) );
                    for ( Header hdr : response.getAllHeaders( ) )
                    {
                        logger.log( Level.FINEST, String.format( "  %s=%s", hdr.getName( ), hdr.getValue( ) ) );
                    }
                }
                long stopTime = System.nanoTime( );
                double elapsed = ( double ) ( stopTime - startTime ) / 1000000000.0;
                if ( logger.isLoggable(Level.FINEST ) )
                {
                    logger.log( Level.FINEST, "Exec time POST= " + elapsed );
                    logger.log( Level.FINEST, "Exec time GET= " + elapsed );
                    logger.log( Level.FINEST, "######################" );
                    logger.log( Level.FINEST, "" );
                }
                return postResponse;
            }
        }
        catch (Exception e)
        {
           throw new Exception( e );
        }
        return null;
    }

    /**
     * To perform POST request for given URL and given parameters.
     * @param url The URL that receives the POST request.
     * @param params The list of {@link NameValuePair} entries that make up the form being submitted.
     * @param headers The list of {Headers} entries.
     * @return The result entity of the POST request.
     * @throws Exception
     */
     public String doPostRequest( final String url, final List< NameValuePair > params ,List<Header> headers) throws Exception
    {
        try
        {
            if ( logger.isLoggable(Level.FINEST ) )
            {
                logger.log( Level.FINEST, "######################" );
                logger.log( Level.FINEST, "POST request URL = [" + url + "], parameters = [" + params.toString( ) + "]" );
            }

            HttpEntity postEntity = new UrlEncodedFormEntity( params, "UTF-8" );
            HttpPost httppost = new HttpPost( url ); 
            //headers.add( new BasicHeader( "X-Requested-With", "script" ) );
            for( Header header: headers ){
                httppost.setHeader( header );
            }
            
            httppost.setEntity( postEntity );            
            if ( logger.isLoggable(Level.FINEST ) )
            {
                logger.log( Level.FINEST, httppost.toString( ) );
                for ( Header hdr : httppost.getAllHeaders( ) )
                {
                    logger.log( Level.FINEST, String.format( "  %s=%s", hdr.getName( ), hdr.getValue( ) ) );
                }
            }
            long startTime = System.nanoTime( );
            HttpResponse response = getHttpClient().execute( httppost, httpContext );
            StatusLine status = response.getStatusLine( );
            HttpEntity entity = response.getEntity( );
            if ( entity != null )
            {
                String postResponse = EntityUtils.toString( entity ).trim( );
                if ( logger.isLoggable(Level.FINEST ) )
                {
                    logger.log( Level.FINEST, "Response POST= " + postResponse );
                    logger.log( Level.FINEST, "Status POST= " + status.getStatusCode( ) );
                    for ( Header hdr : response.getAllHeaders( ) )
                    {
                        logger.log( Level.FINEST, String.format( "  %s=%s", hdr.getName( ), hdr.getValue( ) ) );
                    }
                }
                long stopTime = System.nanoTime( );
                double elapsed = ( double ) ( stopTime - startTime ) / 1000000000.0;
                if ( logger.isLoggable(Level.FINEST ) )
                {
                    logger.log( Level.FINEST, "Exec time POST= " + elapsed );
                    logger.log( Level.FINEST, "Exec time GET= " + elapsed );
                    logger.log( Level.FINEST, "######################" );
                    logger.log( Level.FINEST, "" );
                }
                return postResponse;
            }
        }
        catch (Exception e)
        {
            throw new Exception( e );
        }
        return null;
    }
     
    /**
     * perform GET request for given URL. When getLocation is true
     * it'll extract that from the return request.
     * @param url The URL that receives the GET request.
     * @param getLocation boolean to indicate if the result should be the location information
     * @return The results of the request.
     * @throws Exception
     */
    public String doGetRequest( final String url, final boolean getLocation ) throws Exception{
        return doGetRequest(url, getLocation, null).getReponse();
    }


    public HttpCallResponse doGetRequest( final String url, final boolean getLocation, Cookie cookie ) throws Exception {
        return doGetRequest( url, getLocation, cookie, null );
    }


    public HttpCallResponse doGetRequest( final String url, final boolean getLocation, Cookie cookie, List< Header > headers ) throws Exception
    {

        if ( logger.isLoggable(Level.FINEST  ) )
        {
            logger.log( Level.FINEST, "######################" );
            logger.log( Level.FINEST, "GET request URL = [" + url + "]" );
        }

        try
        {
            HttpGet httpget = new HttpGet( url );

            if( headers != null ) {
                for( Header header : headers ) {
                    httpget.addHeader( header );
                }
            } else {
                httpget.setHeader("Accept", "application/json");
            }
            HttpParams params = new BasicHttpParams();
            params.setParameter(ClientPNames.HANDLE_REDIRECTS, true);
            // HttpClientParams.setRedirecting(params, false); // alternative
            httpget.setParams(params);




            if(cookie != null) {
                String cookiString = cookie.getName() + "=" + cookie.getValue();
                httpget.setHeader("Cookie", cookiString);
            }
            if ( logger.isLoggable(Level.FINEST  ) )
            {
                logger.log( Level.FINEST, httpget.toString( ) );
                for ( Header hdr : httpget.getAllHeaders( ) )
                {
                    logger.log( Level.FINEST, String.format( "  %s=%s", hdr.getName( ), hdr.getValue( ) ) );
                }
            }
            long startTime = System.nanoTime( );
            HttpResponse response = getHttpClient().execute( httpget, httpContext );


            HttpEntity entity = response.getEntity( );
            StatusLine status = response.getStatusLine( );

            HttpCallResponse callResponse = new HttpCallResponse();
            callResponse.setStatus( status.getStatusCode() );

            if ( entity != null )
            {
                String getResponse = EntityUtils.toString( entity ).trim( );
                callResponse.setReponse( getResponse );

                if ( logger.isLoggable(Level.FINEST  ) ) {
                    logger.log( Level.FINEST,"Response GET= " + getResponse);
                    logger.log( Level.FINEST,"Status GET= " + status.getStatusCode());
                }

                for ( Header hdr : response.getAllHeaders( ) )
                {
                    if ( logger.isLoggable(Level.FINEST  ) ) {

                        logger.log( Level.FINEST,String.format("  %s=%s", hdr.getName(), hdr.getValue()));
                        System.out.println("hdr.getName().." + hdr.getName() + "..." + hdr.getValue());
                    }
                }

                long stopTime = System.nanoTime( );
                double elapsed = ( double ) ( stopTime - startTime ) / 1000000000.0;
                if ( logger.isLoggable(Level.FINEST  ) )
                {
                    logger.log( Level.FINEST, "Exec time GET= " + elapsed );
                    logger.log( Level.FINEST, "######################" );
                }

                return callResponse;
            }
            return null;
        }
        catch (Exception e)
        {
        	throw new Exception( e );
        }
    }

}
