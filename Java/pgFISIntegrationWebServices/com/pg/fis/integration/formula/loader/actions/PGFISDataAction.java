package com.pg.fis.integration.formula.loader.actions;

import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISCASAuthenticator;
import com.pg.fis.integration.util.HttpCallResponse;
import com.google.gson.JsonObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;

import java.nio.charset.StandardCharsets;


/**
 *  Base class for a sset of
 */
public abstract class PGFISDataAction {

    protected PGFISCASAuthenticator httpConnection;
    protected boolean requireCAA( ) { 
        return Boolean.parseBoolean(PGFISWSConstants.FIS_CLOUD_REQUIRECAA);
    }


    protected Header getClmHeader( ) throws Exception {

        if( PGFISWSConstants.FIS_CLOUD_CLM_AGENT_ID == null || PGFISWSConstants.FIS_CLOUD_CLM_AGENT_PASSWORD == null ) {
            throw new Exception( "System Property clmAgent and clmPassword must be set to call updateFunction" );
        }

        //
        //  Set up CLM Authentication headers
        //
        String auth = PGFISWSConstants.FIS_CLOUD_CLM_AGENT_ID + ":" + PGFISWSConstants.FIS_CLOUD_CLM_AGENT_PASSWORD;
        byte[] encodedAuth = Base64.encodeBase64( auth.getBytes(StandardCharsets.ISO_8859_1));
        return new BasicHeader( HttpHeaders.AUTHORIZATION, "Basic " + new String( encodedAuth ) );
    }

    public abstract HttpCallResponse doDataAction(PGFISCASAuthenticator httpConnection, JsonObject data ) throws Exception;
}