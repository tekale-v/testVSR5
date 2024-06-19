package com.pg.fis.integration.formula.loader;

import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.fis.integration.util.PGFISCASAuthenticator;
import com.pg.fis.integration.util.PGFISCommonUtil;
import com.pg.util.EncryptCrypto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Base class for a series of data load utilities
 */
public abstract class PGFISDataCreator {

    private static final Logger logger = Logger.getLogger( "DataCreator" );


    /**
     * Total number of iterations of the data loader to execute
     */
    private int iterations = Integer.getInteger( "iterations", 1 );


    /**
     *
     */
    private int concurrentThreads = Integer.getInteger( "concurrentThreads", 4 );


    /**
     * File location for data input files
     */
    private File root = new File( System.getProperty("user.dir")+File.separator+"Data"+File.separator);


    private PGFISCASAuthenticator casAuthenticator;

    private PGFISCASAuthenticator casPassportAuthenticator;

    /**
     * List to capture errors during import
     */
    private List< String > errors = new ArrayList<>();


    /**
     * This method triggers the migration code of a specific migrator
     *
     * @throws Exception
     */
    public abstract void migrateData( ) throws Exception;


    /**
     * Get the File handle to the data folder where all input data exists
     * @return
     */
    protected File getDataRootFolder( ) throws FileNotFoundException {

        if( !root.exists() ) {
            throw new FileNotFoundException( "data folder does not exist" );
        }

        return root;
    }

    protected synchronized PGFISCASAuthenticator getCasAuthenticator( ) throws Exception {
        if( casAuthenticator == null ) {
            casAuthenticator = new PGFISCASAuthenticator( );

            // Dont even try to authenticate if we are forcing CAA and we will use CLM Later
            if( !Boolean.parseBoolean(PGFISWSConstants.FIS_CLOUD_REQUIRECAA) )
                casAuthenticator.authenticate(PGFISCASAuthenticator.USER_ODT_ID, EncryptCrypto.decryptString( PGFISCASAuthenticator.USER_ODT_PASSWORD) );
        }


        return casAuthenticator;
    }

    protected synchronized PGFISCASAuthenticator getCasAuthenticator(boolean passportLogin ) throws Exception {
        if( casPassportAuthenticator == null ) {
        	casPassportAuthenticator = new PGFISCASAuthenticator( );

            if(passportLogin || !Boolean.parseBoolean(PGFISWSConstants.FIS_CLOUD_REQUIRECAA)) {
            	casPassportAuthenticator.authenticate(PGFISCASAuthenticator.USER_ODT_ID, EncryptCrypto.decryptString( PGFISCASAuthenticator.USER_ODT_PASSWORD) );
            }
        }


        return casPassportAuthenticator;
    }

    protected int getConcurrentThreads( ) {
        return concurrentThreads;
    }


    protected int getIterations( ) {
        return iterations;
    }

    /**
     * Add an error that occurred during migration.   Error should be fully descriptive including ID and error reason
     *
     * @param error
     */
    protected void addError( String error ) {
        errors.add( error );
    }


    protected void reportErrors( Logger logger ) {

        logger.info("*************************************************");
        if( errors.size() <= 0 ) {
            logger.info( "No errors occurred");

        } else {

            logger.info("Errors encountered: " + errors.size());
            for (String s : errors) {
                logger.info("\t- " + s);
            }
        }
        logger.info("*************************************************");
    }



    protected Map< String, String > loadMappingsJson(File inputFile ) throws Exception {

        if( !inputFile.exists() ) {
            //throw new FileNotFoundException( inputFile.getAbsolutePath() );
        	return new HashMap();
        }


        Map< String, String > idMap = new HashMap<>();

        Reader reader = Files.newBufferedReader(Paths.get( inputFile.toURI() ) );
        Gson gson = new Gson( );
        JsonArray map = gson.fromJson(reader, JsonArray.class);
        for(JsonElement entry : map ) {
            logger.finest( entry.toString() );
            idMap.put( entry.getAsJsonObject().get( "originalId" ).getAsString(), entry.getAsJsonObject().get( "newId" ).getAsString());
        }

        logger.info( "Loaded " + idMap.keySet().size() + " records from " + inputFile.getAbsolutePath() );

        return idMap;

    }


    class IdMapping {
        String originalId, newId;

        public IdMapping( String originalId, String newId) {
            this.originalId = originalId;
            this.newId = newId;
        }
    }

}
