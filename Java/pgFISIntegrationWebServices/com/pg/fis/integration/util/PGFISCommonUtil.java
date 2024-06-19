package com.pg.fis.integration.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;


import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;


import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.fis.integration.constants.PGFISWSConstants;
import com.pg.util.EncryptCrypto;

import java.io.FileInputStream;
import java.util.Properties;

import matrix.db.AttributeType;
import matrix.db.Context;
import matrix.util.StringList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PGFISCommonUtil {
	private static Logger logger = Logger.getLogger(PGFISCommonUtil.class.getName());
	//private static Properties prop = pgNGFCommonUtil.getConfiguration();
    public static String strFormulatedMatId = DomainConstants.EMPTY_STRING;
    private static PGFISCASAuthenticator casAuthenticator;
	
	
	/**
	 * This methd is used for getting object details through findObjects api
	 * @param context
	 * @param sType
	 * @param sName
	 * @param sRevision
	 * @param sWhereClause
	 * @param slObjectSelects
	 * @return MapList 
	 * @throws FrameworkException
	 */
	public static MapList getObjectDetails(Context context, String sType, String sName, String sRevision,
			String sWhereClause, StringList slObjectSelects) throws FrameworkException {
		return DomainObject.findObjects(context, // context
				sType, // Type Pattern
				sName, // Name pattern
				sRevision, // Rev pattern
				DomainConstants.QUERY_WILDCARD, // owner pattern
				PGFISWSConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern
				sWhereClause, // where expression
				false, // expand type
				slObjectSelects); // bus selects

	}

	/**
	 *  This methd is used for getting object details through findObjects api 
	 * @param context
	 * @param sType
	 * @param sName
	 * @param sRevision
	 * @param sWhereClause
	 * @param sQueryName
	 * @param slObjectSelects
	 * @param limit
	 * @return MapList
	 * @throws FrameworkException
	 */
	public static MapList getObjectDetails(Context context, String sType, String sName, String sRevision,
			String sWhereClause, String sQueryName, StringList slObjectSelects, String limit, boolean expandType) throws FrameworkException {
		return DomainObject.findObjects (context, // context
				sType, // Type Pattern
				sName, // Name pattern
				sRevision, // Rev pattern
				DomainConstants.QUERY_WILDCARD, // owner pattern
				PGFISWSConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern
				sWhereClause, // where expression
				sQueryName, //queryName
				expandType, // expand type
				slObjectSelects,
				(short) Short.parseShort(limit) ); // bus selects

	}
	
	/**
	 *  This methd is used for getting object details through findObjects api 
	 * @param context
	 * @param sType
	 * @param sName
	 * @param sRevision
	 * @param sWhereClause
	 * @param sQueryName
	 * @param slObjectSelects
	 * @param limit
	 * @return MapList
	 * @throws FrameworkException
	 */
	public static MapList getObjectDetails(Context context, String sType, String sName, String sRevision, String sWhereClause, String sQueryName, StringList slObjectSelects, String limit, boolean expandType, StringList slMultiSelect) throws FrameworkException {
		return DomainObject.findObjects (context, // context
				sType, // Type Pattern
				sName, // Name pattern
				sRevision, // Rev pattern
				DomainConstants.QUERY_WILDCARD, // owner pattern
				PGFISWSConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern
				sWhereClause, // where expression
				sQueryName, //queryName
				expandType, // expand type
				slObjectSelects, // bus selects
				(short) Short.parseShort(limit), // limit
				slMultiSelect); //Multi Select values

	}

	/**
	 * This method is used for creating selectables 
	 * @param context
	 * @return slObjectSelect
	 */
	public static StringList getObjectSelects(Context context) {
		StringList slObjectSelect = new StringList();
		slObjectSelect.add(DomainConstants.SELECT_TYPE);
		slObjectSelect.add(DomainConstants.SELECT_NAME);
		slObjectSelect.add(DomainConstants.SELECT_REVISION);
		slObjectSelect.add(DomainConstants.SELECT_ID);
		slObjectSelect.add(DomainConstants.SELECT_PHYSICAL_ID);
		slObjectSelect.add(DomainConstants.SELECT_CURRENT);

		return slObjectSelect;
	}

	
	/**
	 * This method is responsible for calling on prem api to get JSON response on the basis of respective types 
	 *
	 * @param sPhysicalID
	 * @param sType
	 * @return response
	 */
	public String callOnPremAPIToGetJSON(String sPhysicalID, String sType) throws Exception {
		PGFISCASAuthenticator casAuthenticator = getCasAuthenticatorForOnPrem();
		String response = callOnPremAPIToGetJSON(casAuthenticator,sPhysicalID,sType);
		
		return response;
	}
	public String callOnPremAPIToGetJSON(PGFISCASAuthenticator casAuthenticator, String sPhysicalID, String sType) throws Exception {
		
		
		String url = null;
		if (UIUtil.isNotNullAndNotEmpty(sType)){
			if (PGFISWSConstants.TYPE_SUBSTANCE.equalsIgnoreCase(sType)){
				url = PGFISWSConstants.ONPREM_ENGINUITY_URL+PGFISWSConstants.SUBSTANCE_GET_URL+sPhysicalID;;
			} else if (PGFISWSConstants.TYPE_RAW_MATERIAL.equalsIgnoreCase(sType)){
				url = PGFISWSConstants.ONPREM_ENGINUITY_URL+PGFISWSConstants.RAWMATERIAL_GET_URL+sPhysicalID;;
			} else if (PGFISWSConstants.TYPE_FORMULATION_PROCESS.equalsIgnoreCase(sType)){
				url = PGFISWSConstants.ONPREM_ENGINUITY_URL+PGFISWSConstants.FORMULA_GET_URL+sPhysicalID;;
			} 

		}
		
		logger.info("url - " + url);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(HttpHeaders.CONTENT_TYPE, "application/json"));
		params.add(new BasicNameValuePair(HttpHeaders.ACCEPT, "application/json"));	
		
		
		//Adding Basic Authentication
		List<Header> paramsHeader = new ArrayList< >();
		paramsHeader.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
		if(PGFISWSConstants.ONPREM_BASIC_AUTH.equalsIgnoreCase("TRUE")) {
			String auth = PGFISWSConstants.ONPREM_PASSPORT_USERNAME+":"+EncryptCrypto.decryptString(PGFISWSConstants.ONPREM_PASSPORT_PASSWORD);
	        byte[] encodedAuth = Base64.encodeBase64( auth.getBytes(StandardCharsets.ISO_8859_1));
	        paramsHeader.add( new BasicHeader( HttpHeaders.AUTHORIZATION, "Basic " + new String( encodedAuth ) ));
		} 
		paramsHeader.add(new BasicHeader("Username", PGFISWSConstants.ONPREM_ADMIN_USERNAME));
        paramsHeader.add(new BasicHeader("SecurityContext", "Enginuity Administrator.PG.Internal_PG"));
        logger.info("paramsHeader - " + paramsHeader);
		HttpCallResponse response = casAuthenticator.doGetRequest(url,false, null, paramsHeader);
		
		return response.getReponse();
	}

	/**
	 * This method is responsible for authentication 
	 * @return casAuthenticator
	 * @throws Exception
	 */
	public PGFISCASAuthenticator getCasAuthenticatorForOnPrem() throws Exception {
		PGFISCASAuthenticator casAuthenticator = new PGFISCASAuthenticator("on_Prem_3dPassport");
		casAuthenticator.authenticate(PGFISCASAuthenticator.USER_ODT_ID_ON_PREM, EncryptCrypto.decryptString( PGFISCASAuthenticator.USER_ODT_PASSWORD_ON_PREM) );

		return casAuthenticator;
	}

	public HttpCallResponse updateReferenceURIAttributes(PGFISCASAuthenticator casAuthenticator, String sType, String sPhysicalID, String referenceURI, String referenceSource, String referenceIdentifier) throws Exception {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("uri", referenceURI);
		jsonObject.addProperty("source", referenceSource);
		jsonObject.addProperty("identifier", referenceIdentifier);
		String url = "";
		
		if(PGFISWSConstants.TYPE_SUBSTANCE.equals(sType)) {
			url = PGFISWSConstants.ONPREM_ENGINUITY_URL+"/substance/"+sPhysicalID+"/updateReference";
		} else if(PGFISWSConstants.TYPE_RAW_MATERIAL.equals(sType)) {
			url = PGFISWSConstants.ONPREM_ENGINUITY_URL+"/raw-material/"+sPhysicalID+"/updateReference";
		} else if(PGFISWSConstants.TYPE_FORMULATION_PROCESS.equals(sType)) {
			jsonObject.addProperty("formulatedMaterialURI", strFormulatedMatId);
			url = PGFISWSConstants.ONPREM_ENGINUITY_URL+"/formula/"+sPhysicalID+"/updateReference";
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(HttpHeaders.CONTENT_TYPE, "application/json"));
		params.add(new BasicNameValuePair(HttpHeaders.ACCEPT, "application/json"));	
		params.add( new BasicNameValuePair( "Accept", "application/json" ) );
        params.add( new BasicNameValuePair( "Username", PGFISCASAuthenticator.USER_ODT_ID_ON_PREM ) );
        params.add( new BasicNameValuePair( "SecurityContext", "Enginuity Administrator.PG.Internal_PG" ) );
        
        
		//Adding Basic Authentication
		List<Header> paramsHeader = new ArrayList< >();
		paramsHeader.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
		if(PGFISWSConstants.ONPREM_BASIC_AUTH.equalsIgnoreCase("TRUE")) {
			String auth = PGFISCASAuthenticator.USER_ODT_ID_ON_PREM+":"+EncryptCrypto.decryptString( PGFISCASAuthenticator.USER_ODT_PASSWORD_ON_PREM );
			byte[] encodedAuth = Base64.encodeBase64( auth.getBytes(StandardCharsets.ISO_8859_1));
			paramsHeader.add( new BasicHeader( HttpHeaders.AUTHORIZATION, "Basic " + new String( encodedAuth ) ));
			params.add( new BasicNameValuePair( HttpHeaders.AUTHORIZATION, "Basic " + new String( encodedAuth ) ) );
		}
		
        paramsHeader.add(new BasicHeader("Username", PGFISCASAuthenticator.USER_ODT_ID_ON_PREM));
        paramsHeader.add(new BasicHeader("SecurityContext", "Enginuity Administrator.PG.Internal_PG"));
        
        casAuthenticator.doPostRequest(PGFISWSConstants.ONPREM_ENGINUITY_URL+"/ping", params);
        
        logger.info("Update Reference Attribute JSON - " + jsonObject.toString());
        logger.info("URL - " + url);
		HttpCallResponse response = casAuthenticator.doPutRequest(url, paramsHeader, jsonObject.toString());
		logger.info("updateReferenceURIAttributes -- response - " + response);
		return response;
		
	}
	
	

	/**
	 * Method is to authenticate CAS
	 * @return
	 * @throws Exception
	 */
	public static PGFISCASAuthenticator getCasAuthenticator( ) throws Exception {
		
        if( casAuthenticator == null ) {
            casAuthenticator = new PGFISCASAuthenticator( );
        }
        return casAuthenticator;
    }
	
	/**
	 * Method is get clm header
	 * @return
	 * @throws Exception
	 */
	public static Header getClmHeader( ) throws Exception {

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
	
	
	/**
	 * To Get Status code from Mass Synchro Status Check GET API Response
	 * @param response1
	 * @return
	 */
	public static String getStatus(HttpCallResponse response1) {
		// TODO Auto-generated method stub
		String status = null;
			JsonObject jsonObject = new JsonParser().parse(response1.getReponse()).getAsJsonObject();                
			com.google.gson.JsonArray jsonArray = jsonObject.getAsJsonArray("users");
			JsonObject jsonObj = (JsonObject) jsonArray.get(0);
			status = jsonObj.get("status").getAsString();
		
		return status;
	}
}
