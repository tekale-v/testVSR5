package com.pg.fis.integration.formula.loader.actions;

import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISCASAuthenticator;
import com.pg.fis.integration.util.HttpCallResponse;
import com.google.gson.JsonObject;
//import ims_usa.FWS_II.Utility.Profiler;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PGFISRecipeCreateAction extends PGFISDataAction {

	private static final Logger logger = Logger.getLogger( "PGFISRecipeCreateAction" ) ;
    


    @Override
    public HttpCallResponse doDataAction(PGFISCASAuthenticator httpConnection, JsonObject data) throws Exception {


        String url = PGFISCASAuthenticator.getUrlForService( "bioexperiment" ) + PGFISWSConstants.URL_RECIPE_PRIVATE;
        List<Header> params = new ArrayList< >();
        params.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
        if( !requireCAA() ) {
            params.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
        } else {
            params.add(new BasicHeader(HttpHeaders.ACCEPT, "*"));
            params.add( getClmHeader() );
        }
        logger.info("params - " + params);
        HttpCallResponse response = httpConnection.doPostRequest(url, params, data.toString());
        
        if( response.getStatus() > 200 ) {
           // Profiler.setProfilerTime(getClass().getName(), System.currentTimeMillis() - startTime);
        }

        //
        //  Weird behavior - first call seems to return 200, subsequent calls allow creation and return 201
        //
        if( response.getStatus() == 200 ) {
            response = httpConnection.doPostRequest( url, params, data.toString()  );
        }


        return response;
    }
}
