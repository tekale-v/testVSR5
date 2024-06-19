package com.pg.fis.integration.formula.loader.actions;

import com.pg.fis.integration.util.PGFISCASAuthenticator;
import com.pg.fis.integration.util.HttpCallResponse;
import com.google.gson.JsonObject;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PGFISRecipePingAction extends PGFISDataAction {

	private static final Logger logger = Logger.getLogger( "RecipePingAction" ) ;
    @Override
    public HttpCallResponse doDataAction(PGFISCASAuthenticator httpConnection, JsonObject data) throws Exception {
        String url = PGFISCASAuthenticator.getUrlForService( "bioexperiment" ) + "/eln/spr/api/v1/ping";
        logger.info("url="+url);
        List<Header> headers = new ArrayList<>();
        headers.add( new BasicHeader( "Accept", "*/*" ) );

        HttpCallResponse response  = httpConnection.doGetRequest( url, true, null, headers );


        return response;

    }


}
