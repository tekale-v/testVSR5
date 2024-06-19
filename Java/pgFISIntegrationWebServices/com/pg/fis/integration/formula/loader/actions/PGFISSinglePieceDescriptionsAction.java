package com.pg.fis.integration.formula.loader.actions;

import com.pg.fis.integration.util.PGFISCASAuthenticator;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.HttpCallResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.Header;

import java.util.*;
import java.util.logging.Logger;

/**
 * This DataAction will cache functions from materials service and expose methods to decode
 * a string function with a pref-label
 */
public class PGFISSinglePieceDescriptionsAction extends PGFISDataAction {

    private static final Logger logger = Logger.getLogger( "PGFISSinglePieceDescriptionsAction" ) ;
    private static final Map< String, Map.Entry< String, String > > functionMap = new HashMap< >( );


    /**
     *
     * Return a map of all functions in the Materials Management Service
     *
     * @return
     */
    public Map< String, Map.Entry< String, String > > getFunctionsMap( ) {

        if( functionMap.isEmpty() ) {
        	throw new RuntimeException( "functionMap not initialized.  Please call doDataAction first." );
        }
        return functionMap;
    }


    /**
     * Return the public endpoint used for Materials CAA API
     * @return
     */
    private String getUrl( ) {
    	String url = PGFISCASAuthenticator.getUrlForService( PGFISWSConstants.CONST_BIO_EXPERIMENT_SERVICE ) + PGFISWSConstants.CONST_SINGLE_PIECE_DESC_URI;
        if( PGFISWSConstants.FIS_CLOUD_CLM_AGENT_ID != null ) {
             url =  PGFISCASAuthenticator.getUrlForService( PGFISWSConstants.CONST_BIO_EXPERIMENT_SERVICE ) + PGFISWSConstants.CONST_SINGLE_PIECE_DESC_URI;
        }

        logger.info( "Single Piece Description Function URL: " + url );
        return url;
    }


   

    private void cacheFunctions( PGFISCASAuthenticator connection ) throws Exception {

        String url = getUrl() + "?$top=1000";
        
        List<Header> params = new ArrayList<  >( );
        if( requireCAA() ) {
            params.add(getClmHeader());
        }

        HttpCallResponse response = connection.doGetRequest( url, false, null, params );

        if( response.getStatus() == 200 ) {
            //logger.info( response.getReponse() );

            JsonObject json = JsonParser.parseString( response.getReponse() ).getAsJsonObject();

            JsonArray array = json.getAsJsonArray( "member" );
            for(JsonElement el : array ) {
                JsonObject obj = el.getAsJsonObject();

                String id = obj.get( PGFISWSConstants.JSON_TAG_ID ).getAsString();
                if( obj.has( PGFISWSConstants.JSON_TAG_PREF_LABEL ) ) {
                    String label = obj.get(PGFISWSConstants.JSON_TAG_PREF_LABEL).getAsString();
                    functionMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry<>(id, label));
                }
            }


        } else {
            throw new Exception( "Unable to get Single Piece Descriptions Functions: Error code: " + response.getStatus() + "  -  Message:" + response.getReponse());
        }

    }


    /**
     *
     * This will trigger a cache build
     *
     * @param httpConnection
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public HttpCallResponse doDataAction(PGFISCASAuthenticator httpConnection, JsonObject data) throws Exception {

        cacheFunctions( httpConnection );

        return null;
    }





    public Map.Entry< String, String> decodeFunction( String inputFunction ) {

        if( functionMap.containsKey( inputFunction.toLowerCase() ) ) {
            return functionMap.get( inputFunction.toLowerCase() );
        }


        return null;


    }

}
