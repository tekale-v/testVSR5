/*
 **   VeevaVault.java
 **   Description - Introduced as part of Veeva integration.      
 **   Entry point to fetch data from for Veeva. 
 **
 */
package com.pg.dsm.veeva;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MessageUtil;
import com.pg.dsm.veeva.config.Configurator;
import com.pg.dsm.veeva.config.VeevaConfig;
import com.pg.dsm.veeva.config.VeevaVQL;
import com.pg.dsm.veeva.helper.enovia.ArtworkErrorMessage;
import com.pg.dsm.veeva.io.FileUtil;
import com.pg.dsm.veeva.util.Utility;
import com.pg.dsm.veeva.util.Veeva;
import com.pg.dsm.veeva.vql.VQL;
import com.pg.dsm.veeva.vql.VQLUtil;
import com.pg.dsm.veeva.vql.json.AuthenticationResponse;
import com.pg.dsm.veeva.vql.json.DocumentDatasetResponse;
import com.pg.dsm.veeva.vql.json.DocumentPropertyResponse;
import com.pg.dsm.veeva.vql.json.DocumentResponse;
import com.pg.dsm.veeva.vql.json.RenditionResponse;
import com.pg.dsm.veeva.vql.json.Response;
import com.pg.dsm.veeva.vql.json.UsersResponse;
import com.pg.dsm.veeva.vql.json.binder.authentication.AuthenticationResponseMapper;
import com.pg.dsm.veeva.vql.json.binder.document_dataset.DocumentDatasetMapper;
import com.pg.dsm.veeva.vql.json.binder.document_dataset.Renditions;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentProperty;
import com.pg.dsm.veeva.vql.json.binder.document_property.DocumentPropertyMapper;
import com.pg.dsm.veeva.vql.json.binder.documents_query.Document;
import com.pg.dsm.veeva.vql.json.binder.documents_query.DocumentResponseMapper;
import com.pg.dsm.veeva.vql.json.binder.rendition.RenditionResponseMapper;
import com.pg.dsm.veeva.vql.request.failure.HTTPRequestFailure;
import com.pg.dsm.veeva.vql.request.failure.factory.AuthenticationRequestFailureFactory;
import com.pg.dsm.veeva.vql.request.failure.factory.HTTPRequestFailureFactory;
import com.pg.dsm.veeva.vql.request.failure.factory.QueryDocumentsRequestFailureFactory;
import com.pg.dsm.veeva.vql.xml.binder.Rendition;

public class VeevaVault {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	Configurator configurator;
	VeevaConfig veevaConfig;
	String inworkFolder;
	Properties properties = new Properties();

	VQL authenticationVQL; 
	VQL documentsVQL;
	VQL documentDataSetVQL;
	VQL usersEmailVQL;
	VQL documentDataPropertyVQL;
	VQL renditionVQL;

	VeevaVaultError veevaVaultError;
	
	/** 
	 * @about - Constructor
	 * @param - Configurator - configurator object
	 * @param - VeevaVQL - veevaVQL object
	 * @throws Exception
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public VeevaVault(Configurator configurator, VeevaVQL veevaVQL) throws Exception {
		this.veevaVaultError = new VeevaVaultError(configurator);

		this.properties = configurator.getProperties();

		this.configurator = configurator;
		logger.info("Configurator is initialized");
		this.inworkFolder = configurator.getExtractionRequiredFolders().getInwork();
		logger.info("inworkFolder is initialized");
		this.authenticationVQL = veevaVQL.getAuthenticationVQL();
		logger.info("VQL Authentication query is initialized");

		this.documentsVQL = veevaVQL.getDocumentsVQL();
		logger.info("VQL Documents query is initialized");
		this.documentDataSetVQL= veevaVQL.getDocumentDataSetVQL();
		logger.info("VQL Document Data set query is initialized");
		this.usersEmailVQL = veevaVQL.getUsersEmailVQL();
		logger.info("VQL Users Emails query is initialized");
		this.documentDataPropertyVQL = veevaVQL.getDocumentDataPropertyVQL();
		logger.info("VQL Document Property query is initialized");
		this.renditionVQL= veevaVQL.getRenditionVQL();
		logger.info("VQL Rendition query is initialized");

	}

	/** 
	 * @about - Getter method - to get VeevaVaultError object
	 * @return - VeevaVaultError - veevaVaultError object
	 * @since DSM 2018x.3
	 */
	public VeevaVaultError getVeevaVaultError() {
		return veevaVaultError;
	}
	/** 
	 * @about - Setter method - to set VeevaVaultError object 
	 * @param - VeevaVaultError - veevaVaultError object
	 * @return - void
	 * @since DSM 2018x.3
	 */
	public void setVeevaVaultError(VeevaVaultError veevaVaultError) {
		this.veevaVaultError = veevaVaultError;
	}

	/** 
	 * @about - Method to perform Veeva Authentication
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @return AuthenticationResponse - response bean
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public AuthenticationResponse authenticate(CloseableHttpClient httpclient) throws Exception {
		logger.info("VQL Authentication Query initialized");
		HttpPost httpPostAuth = new HttpPost(authenticationVQL.getBaseURI()+authenticationVQL.getApi());
		logger.info("VQL Authentication HTTP Request initialized");
		httpPostAuth.setHeaders(authenticationVQL.getHeader());
		httpPostAuth.setEntity(new ByteArrayEntity(authenticationVQL.getCustomQueryString().getBytes()));
		logger.info("VQL Authentication HTTP Request Parameters are set");
		return authenticate(httpclient, httpPostAuth);
	}
	/** 
	 * @about - Method to call Veeva Query Rest-API  
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @return DocumentResponse - response bean
	 * @param String - session id
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public DocumentResponse queryDocuments(CloseableHttpClient httpclient, String sessionID) throws Exception {
		logger.info("VQL Query to get Document IDs initialized");
		HttpGet httpGetDocuments = new HttpGet(documentsVQL.getBaseURI()+documentsVQL.getApi()+Utility.encode(documentsVQL.getCustomQueryString()));
		logger.info("VQL Documents HTTP request initialized");
		httpGetDocuments.setHeaders(documentsVQL.getHeader());
		httpGetDocuments.setHeader(HttpHeaders.AUTHORIZATION, sessionID);
		return getDocuments(httpclient, httpGetDocuments);
	}
	/** 
	 * @about - Method to call Veeva Document Dataset Rest-API  
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param String - session id
	 * @param String - document id
	 * @return DocumentDatasetResponse - response bean
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public DocumentDatasetResponse queryDocumentDataset(CloseableHttpClient httpclient, String sessionID, String docID) throws Exception {
		HttpGet documentDatasetRequest = new HttpGet(documentDataSetVQL.getBaseURI()+documentDataSetVQL.getApi()+docID);
		logger.info("VQL Document Data set request is initialized");
		documentDatasetRequest.setHeader(HttpHeaders.AUTHORIZATION, sessionID);
		return getDocumentDataset(httpclient, documentDatasetRequest);
	}
	/** 
	 * @about - Method to call Veeva Users Email Rest-API  
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param String - session id
	 * @param List - list of identifiable users name
	 * @return UsersResponse - response bean
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public UsersResponse queryUsersEmail(CloseableHttpClient httpclient, String sessionID, List<Long> users) throws Exception {
		String usersEmailQueryString = usersEmailVQL.getCustomQueryString();
		HttpGet httpGetUsersEmail = new HttpGet(usersEmailVQL.getBaseURI()+usersEmailVQL.getApi()+Utility.encode(VQLUtil.getUserQuery(usersEmailQueryString, users)));
		logger.info("VQL Users Email request is initialized");
		httpGetUsersEmail.setHeader(HttpHeaders.AUTHORIZATION, sessionID);
		return getUsersResponse(httpclient, httpGetUsersEmail);
	}
	/** 
	 * @about Method to call Veeva Document Property Rest-API  
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param String - session id
	 * @param String - document id
	 * @return DocumentPropertyResponse - response bean
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public DocumentPropertyResponse queryDocumentProperty(CloseableHttpClient httpclient, String sessionID, String docID) throws Exception {
		String documentPropertyQueryString = documentDataPropertyVQL.getCustomQueryString().replaceAll("#ID", docID);
		HttpGet httpGetDocumentDataProperty =  new HttpGet(documentDataPropertyVQL.getBaseURI()+documentDataPropertyVQL.getApi()+Utility.encode(documentPropertyQueryString));
		logger.info("VQL Document Property HTTP request is initialized");
		httpGetDocumentDataProperty.setHeader(HttpHeaders.AUTHORIZATION, sessionID);
		return getDocumentProperty(httpclient, httpGetDocumentDataProperty);
	}
	/** 
	 * @about Method to call Veeva Rendition Rest-API  
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param String - session id
	 * @param String - query string
	 * @return Document - document bean
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public List<Response> queryRenditions(CloseableHttpClient httpclient, String sessionID, String renditionQueryString, Document document) throws Exception {
		String docID = document.getId();
		String docNumber = document.getDocumentNumber();
		List<Response> failureResponseList = new ArrayList<Response>();
		HttpGet httpGetRendition = new HttpGet(renditionQueryString);
		httpGetRendition.setHeader(HttpHeaders.AUTHORIZATION, sessionID);
		RenditionResponse renditionResponse = downloadRendition(httpclient, httpGetRendition, FileUtil.getRenditionDownloadPath(configurator.getExtractionRequiredFolders().getInwork(), docID), docID);
		if(Veeva.CONST_KEYWORD_200.equals(renditionResponse.getResponseStatusCode())) {
			RenditionResponseMapper renditionResponseMapper = new ObjectMapper().readValue(new StringReader(renditionResponse.getjString()), RenditionResponseMapper.class);
			if(null != renditionResponseMapper) {
				logger.info("*******************Rendition Info for Document ID <<"+docID+">>  Document Number <<"+docNumber+">> **************");
				logger.info("Rendition HTTP query response for document ID <<"+docID+">> is >> "+renditionResponseMapper.getResponseStatusCode());
				logger.info("Document ID <<"+docID+">> Document Number <<"+docNumber+">> has Rendition file? -->"+renditionResponseMapper.hasFile());
				logger.info("Document ID <<"+docID+">> Document Number <<"+docNumber+">>  Rendition file downloaded? -->"+renditionResponseMapper.isFileDownloaded());
				logger.info("Document ID <<"+docID+">> Document Number <<"+docNumber+">> Rendition file name -->"+renditionResponseMapper.getFileName());
				logger.info("Document ID <<"+docID+">> Document Number <<"+docNumber+">> Rendition file download path -->"+renditionResponseMapper.getFileDownloadFolder());

				if(renditionResponseMapper.hasFile()) {
					if(!renditionResponseMapper.isFileDownloaded()) {
						failureResponseList.add(renditionResponse);
						logger.error("*************** FAILED Rendition Request for Document ID <<"+docID+">> - unable to download file");
					}
				}
			}
		}

		return failureResponseList;
	}
	/** 
	 * @about Method to make HTTP Post Authentication request  
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param HttpPost - HTTP post request
	 * @return AuthenticationResponse - response bean
	 * @throws JSONException,ClientProtocolException, IOException
	 * @since DSM 2018x.3
	 */
	public AuthenticationResponse authenticate(CloseableHttpClient httpclient, HttpPost httpPost) throws JSONException, ClientProtocolException, IOException {
		String jsonString = Veeva.EMPTY_STRING;
		try(CloseableHttpResponse response = httpclient.execute(httpPost);) {
			jsonString = EntityUtils.toString(response.getEntity());	
		} catch(Exception e) {
			e.printStackTrace();
			logger.error("Vault Authentication Request Failure >>"+e);
		}
		return new AuthenticationResponse(jsonString);
	}
	/** 
	 * @about Method to make HTTP Get Document request 
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param HttpGet - HTTP get request
	 * @return DocumentResponse - response bean
	 * @throws JSONException,ClientProtocolException, IOException
	 * @since DSM 2018x.3
	 */
	public DocumentResponse getDocuments(CloseableHttpClient httpclient, HttpGet httpGet) throws JSONException, ClientProtocolException, IOException {
		String jsonString = Veeva.EMPTY_STRING;
		try(CloseableHttpResponse response = httpclient.execute(httpGet);) {
			jsonString = EntityUtils.toString(response.getEntity());
			logger.info("Vault Documents Response JSON >> "+jsonString);
		} catch(Exception e) {
			logger.error("Vault Documents Failure >>"+e);
		}
		return new DocumentResponse(jsonString);
	}
	/** 
	 * @about Method to make HTTP Get Document Property request 
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param HttpGet - HTTP get request
	 * @return DocumentPropertyResponse - response bean
	 * @throws JSONException,ClientProtocolException, IOException
	 * @since DSM 2018x.3
	 */
	public DocumentPropertyResponse getDocumentProperty(CloseableHttpClient httpclient, HttpGet httpGet) throws JSONException, ClientProtocolException, IOException {
		String jsonString = Veeva.EMPTY_STRING;
		try(CloseableHttpResponse response = httpclient.execute(httpGet);) {
			jsonString = EntityUtils.toString(response.getEntity());
			logger.info("Vault Document Property Response JSON >> "+jsonString);
		} catch(Exception e) {
			logger.error("Vault Document Property Request Failure >>"+e);
		}
		return new DocumentPropertyResponse(jsonString);
	}
	/** 
	 * @about Method to make HTTP Get Users Email request 
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param HttpGet - HTTP get request
	 * @return UsersResponse - response bean
	 * @throws JSONException,ClientProtocolException, IOException
	 * @since DSM 2018x.3
	 */
	public UsersResponse getUsersResponse(CloseableHttpClient httpclient, HttpGet httpGet) throws JSONException, ClientProtocolException, IOException {
		String jsonString = Veeva.EMPTY_STRING;
		try(CloseableHttpResponse response = httpclient.execute(httpGet);) {
			jsonString = EntityUtils.toString(response.getEntity());
			logger.info("Vault Users Email Response JSON >>"+jsonString);
		} catch(Exception e) {
			logger.error("Vault Users Email Request Failure >>"+e);
		}
		return new UsersResponse(jsonString);
	}
	/** 
	 * @about Method to make HTTP Get Document Dataset request 
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param HttpGet - HTTP get request
	 * @return DocumentDatasetResponse - response bean
	 * @throws JSONException,ClientProtocolException, IOException
	 * @since DSM 2018x.3
	 */
	public DocumentDatasetResponse getDocumentDataset(CloseableHttpClient httpclient, HttpGet httpGet) throws JSONException, ClientProtocolException, IOException {
		String jsonString = Veeva.EMPTY_STRING;
		try(CloseableHttpResponse response = httpclient.execute(httpGet);) {
			jsonString = EntityUtils.toString(response.getEntity());
			logger.info("Vault Document Data Set Response JSON >>"+jsonString);
		} catch(Exception e) {
			logger.error("Vault Document Data Set Request Failure >>"+e);
		}
		return new DocumentDatasetResponse(jsonString);
	}
	/** 
	 * @about Method to make HTTP Get / download rendition file request 
	 * @param CloseableHttpClient - httpclient - HTTP client request.
	 * @param HttpGet - HTTP get request
	 * @param String - folder path
	 * @param String - document id
	 * @return RenditionResponse - response bean
	 * @throws JSONException,ClientProtocolException, IOException
	 * @since DSM 2018x.3
	 */
	public RenditionResponse downloadRendition(CloseableHttpClient httpclient, HttpGet httpGet, String folder, String docID) throws JSONException, ClientProtocolException, IOException {
		JSONObject jsonObj = new JSONObject();
		try(CloseableHttpResponse response = httpclient.execute(httpGet);) {
			jsonObj.put("responseStatusCode", response.getStatusLine().getStatusCode());
			jsonObj.put("hasFile", false);
			jsonObj.put("fileName", "");
			jsonObj.put("docID", docID);
			jsonObj.put("fileDownloadFolder", "");
			jsonObj.put("fileDownloaded", false);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String disposition = response.getFirstHeader("Content-Disposition").getValue();
				if(Utility.isNotNullEmpty(disposition)) {
					String fileName = disposition.replaceFirst("(?i)^.*filename=\"([^\"]+)\".*$", "$1");
					if(Utility.isNotNullEmpty(fileName)) {
						jsonObj.put("hasFile", true);
						jsonObj.put("fileName", fileName);
						jsonObj.put("fileDownloadFolder", folder);
						FileOutputStream fos = new FileOutputStream(folder+File.separator+docID+"_rendition_"+fileName);
						entity.writeTo(fos);
						fos.close();
						jsonObj.put("fileDownloaded", true);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Vault Rendition Request Failure >>"+e.getLocalizedMessage());
		}
		logger.info("Vault Rendition Response JSON >>"+jsonObj.toString());
		return new RenditionResponse(jsonObj.toString());
	}
	/** 
	 * @about Method to update Matrix-Veeva config object 
	 * @param VeevaConfig - veevaConfig bean
	 * @return void - nothing
	 * @throws FrameworkException
	 * @since DSM 2018x.3
	 */
	public void updateVeevaConfigObject(VeevaConfig veevaConfig) throws FrameworkException {
		veevaConfig.updateNextRunStartDate();
		logger.info("set Start Date from|"+veevaConfig.getAttrStartDate()+"|to|"+veevaConfig.getNextStartDate());
		veevaConfig.updateNextRunEndDate();
		logger.info("set End Date from|"+veevaConfig.getAttrEndDate()+"|to|"+veevaConfig.getNextEndDate());
	}
	/** 
	 * @about Method to extract data from Veeva (ctrlM)
	 * @return void - nothing
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public void extractScheduled() throws Exception {

		logger.info(">>> Enter extractScheduled method >>");
		VeevaConfig veevaConfig = configurator.getVeevaConfig();
		logger.info("Veeva config object initialized");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		logger.info("Create HTTP closeable client");

		logger.info("VeevaVaultError class initialized");
		try {
				updateVeevaConfigObject(veevaConfig);
				AuthenticationResponse authenticationResponse = authenticate(httpclient);
				if(Veeva.CONST_KEYWORD_SUCCESS.equals(authenticationResponse.getResponseStatus())) {
					logger.info("Veeva Authentication is successful** >> ");
					AuthenticationResponseMapper authenticationResponseMapper = new ObjectMapper().readValue(new StringReader(authenticationResponse.getjString()), AuthenticationResponseMapper.class);
					if(null != authenticationResponseMapper) {
						String sessionID = authenticationResponseMapper.getSessionId();
						logger.info("Which Veeva Vault?"+authenticationResponseMapper.getAuthorizedVaults().get(0).getName());
						if(Utility.isNotNullEmpty(sessionID)) {
							logger.info("Veeva Session established >> ");
							DocumentResponse documentResponse = queryDocuments(httpclient, sessionID);
							if(Veeva.CONST_KEYWORD_SUCCESS.equals(authenticationResponse.getResponseStatus())) {
								logger.info("VQL Documents HTTP response is successful >>");
								FileUtil.saveDocumentQueryResponse(inworkFolder, documentResponse.getjString());
								DocumentResponseMapper documentResponseMapper = new ObjectMapper().readValue(new StringReader(documentResponse.getjString()), DocumentResponseMapper.class);
								List<Document> documentList = documentResponseMapper.getDocuments();
								logger.info("Total number of Document IDs to process >> "+String.valueOf(documentList.size()));
								Map<String, List<Response>> failureResponseMap = new HashMap<String, List<Response>>();
								extract(httpclient, sessionID, failureResponseMap, documentList);

							} else {
								logger.error("Documents Query response failed:"+documentResponse.getjString());
								HTTPRequestFailure failure = HTTPRequestFailureFactory.getFailure(new QueryDocumentsRequestFailureFactory("Document Response Failure", null, configurator, authenticationResponse));
								failure.update(); // roll back job start date/end, update retry count					
							}

						} else {
							logger.error("Invalid Session ID is null");
							logger.info("Invalid Session ID. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - start");
							veevaVaultError.notifyOnAuthenticationInValidSessionID();
							logger.info("Invalid Session ID. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - end");
						}
					} else {
						logger.error("Authentication json bean failed is not initialized");
						logger.info("Authentication Failed. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - start");
						veevaVaultError.notifyOnAuthenticationBeanInitializationFailure();
						logger.info("Authentication Failed. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - end");
					}
				} else {
					logger.error("Authentication ERROR");
					logger.info("Authentication ERROR. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - start");
					HTTPRequestFailure failure = HTTPRequestFailureFactory.getFailure(new AuthenticationRequestFailureFactory("Authentication Failure", null, configurator, authenticationResponse));
					failure.sendEmail();
					failure.update(); // roll back job start date/end, update retry count.
					logger.info("Authentication ERROR. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - end");
				}

		} catch(Exception e) {
			logger.error("General Exception"+e.getMessage());
			logger.info("General Exception. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - start");
			veevaVaultError.updateOnException();
			logger.info("General Exception. Perform Roll back operation on Matrix Veeva configuration object and send out failure email - end");
		} finally {
			httpclient.close();
		}
		logger.info(">>>> Exit extractScheduled method >>>>");
	}

	/** 
	 * @about Common method to extract data from Veeva (through ctrlM and resync)
	 * @param CloseableHttpClient - client request
	 * @param String - session id
	 * @param Map - failure messages map
	 * @param List - list of documents
	 * @return void - nothing
	 * @throws Exception
	 * @since DSM 2018x.3
	 * DSM modified for 2018x.5 requirements 34404, 34405, 34406, 34407, 34408, 34409, 34410, 34411, 34412, 34413, 34414
	 */
	public void extract(CloseableHttpClient httpclient, String sessionID, Map<String, List<Response>> failureResponseMap, List<Document> documentList) throws Exception {
		logger.info("=========================================>VEEVA EXTRACT - START<===============================================================================");
		int count = documentList.size();
		logger.info("Number of document records to get from Veeva "+String.valueOf(count));
		if(count>0) {
			List<Rendition> configuredRenditions = renditionVQL.getConfiguredRenditions();
			List<ArtworkErrorMessage> atworkErrorList = null;
			for(int i=0; i<count; i++) {
				atworkErrorList = new ArrayList<ArtworkErrorMessage>();
				List<Response> failureResponseList = new ArrayList<Response>();
				Document document = documentList.get(i);
				document.setConfiguredRenditions(configuredRenditions);
				String docID = document.getId();
				String documentNumber = document.getDocumentNumber();
				logger.info("***********VEEVA EXTRACT - DOCUMENT ID >>"+docID+"<< DOCUMENT NUMBER >>"+documentNumber+"<< START*********************************");
				logger.info("Counter---"+String.valueOf(i)+"----Processing Document ID <<"+docID+">> Document Number <<"+documentNumber+">> started ------->>");
				extractDocumentDataSet(httpclient, sessionID, document, atworkErrorList, failureResponseList);
				extractDocumentProperty(httpclient, sessionID, document, atworkErrorList, failureResponseList);
				extractRenditions(httpclient, sessionID, document, failureResponseList);
				if(failureResponseList.size()>0) {
					logger.error("Error occureds in making subsequent Veeva API call for Document ID: "+docID+" Document Number"+document.getDocumentNumber());
					failureResponseMap.put(docID+Veeva.SYMBOL_UNDERSCORE+document.getDocumentNumber(), failureResponseList);
				}
				logger.info("Counter---"+String.valueOf(i)+"----Processing Document ID <<"+docID+">> Document Number <<"+documentNumber+">> ended ------->>");
				
				logger.info("***********VEEVA EXTRACT - DOCUMENT ID >>"+docID+"<< DOCUMENT NUMBER >>"+documentNumber+"<< END*********************************");
			}
			// send email for failed response.
			if(failureResponseMap.size()>0)
				veevaVaultError.notifyOnMultipleVQLRequestFailure(failureResponseMap);
		}
		logger.info("=========================================>VEEVA EXTRACT - END<===============================================================================");
	}
	/** 
	 * @about Common method to extract document dataset from Veeva (through ctrlM and resync)
	 * @param CloseableHttpClient - client request
	 * @param String - session id
	 * @param Document - document bean
	 * @param List - failure messages list
	 * @param List - list of failure responses
	 * @return void - nothing
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public void extractDocumentDataSet(CloseableHttpClient httpclient, String sessionID, Document document, List<ArtworkErrorMessage> atworkErrorList, List<Response> failureResponseList) throws Exception {
		String docID = document.getId();
		DocumentDatasetResponse documentDatasetResponse = queryDocumentDataset(httpclient, sessionID, docID);
		if(Veeva.CONST_KEYWORD_SUCCESS.equals(documentDatasetResponse.getResponseStatus())) {
			logger.info("VQL Documents Data set HTTP query for Document ID <<"+docID+">> is successful");
			FileUtil.saveDocumentDataset(inworkFolder, docID, documentDatasetResponse.getjString());
			logger.info("VQL Documents Data set HTTP query response json for document ID <<"+docID+">> is saved to a file");
			DocumentDatasetMapper documentDatasetMapper=null;
			try {
				documentDatasetMapper = new ObjectMapper().readValue(new StringReader(documentDatasetResponse.getjString()), DocumentDatasetMapper.class);
			} catch(Exception e) {
				logger.error("Document Datasset Error >>"+e.getMessage());
			}
			if(null != documentDatasetMapper) {
				document.setRenditions(documentDatasetMapper.getRenditions());
				List<Long> users = documentDatasetMapper.getDocument().getOwnerV().getUsers();
				if(users.size()>0) { 
					UsersResponse usersResponse = queryUsersEmail(httpclient, sessionID, users);
					if(Veeva.CONST_KEYWORD_SUCCESS.equals(usersResponse.getResponseStatus())) {
						logger.info("VQL Users Email HTTP request for document ID <<"+docID+">> is successful");
						FileUtil.saveUsersResponse(inworkFolder, docID, usersResponse.getjString());
						logger.info("VQL Users Email HTTP response json for document ID <<"+docID+">> is saved to a file");
					} else {
						failureResponseList.add(usersResponse);
						logger.error("*************** FAILED Users Email Request for Document ID <<"+docID+">>");
					}
				}
			}
		} else {
			failureResponseList.add(documentDatasetResponse);
			logger.error("*************** FAILED Document Dataset Request for Document ID <<"+docID+">>");
		}
	}
	/** 
	 * @about Common method to extract document property from Veeva (through ctrlM)
	 * @param CloseableHttpClient - client request
	 * @param String - session id
	 * @param Document - document bean
	 * @param List - failure messages list
	 * @param List - list of failure responses
	 * @return void - nothing
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public void extractDocumentProperty(CloseableHttpClient httpclient, String sessionID, Document document, List<ArtworkErrorMessage> atworkErrorList, List<Response> failureResponseList) throws Exception {

		String docID = document.getId();
		logger.info("Document ID >> "+docID);
		DocumentPropertyResponse documentPropertyResponse = queryDocumentProperty(httpclient, sessionID, docID);
		if(Veeva.CONST_KEYWORD_SUCCESS.equals(documentPropertyResponse.getResponseStatus())) {
			logger.info("VQL Document Property HTTP request for document ID <<"+docID+">> is successful");
			FileUtil.saveDocumentProperty(inworkFolder, docID, documentPropertyResponse.getjString());
			logger.info("VQL Document Property HTTP response json for document ID <<"+docID+">> is saved to a file");
		} else {
			failureResponseList.add(documentPropertyResponse);
			logger.error("*************** FAILED Document Property Request for Document ID <<"+docID+">>");
		}
	}
	/** 
	 * @about Common method to extract renditions from Veeva (through ctrlM and resync)
	 * @param CloseableHttpClient - client request
	 * @param String - session id
	 * @param Document - document bean
	 * @param List - list of failure responses
	 * @return void - nothing
	 * @throws Exception
	 * @since DSM 2018x.3
	 */
	public void extractRenditions(CloseableHttpClient httpclient, String sessionID, Document document, List<Response> failureResponseList) throws Exception {
		Map<String, Method> methodMap = new HashMap<String, Method>();
		Method[] declaredMethods = Renditions.class.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (method.isAnnotationPresent(JsonGetter.class)) {
				String annotationValue = method.getAnnotation(JsonGetter.class).value();
				methodMap.put(annotationValue, method);
			}
		}
		Renditions renditions = document.getRenditions();
		List<Rendition> configuredRenditions = document.getConfiguredRenditions();
		logger.info("Number of configured Rendtions >>"+configuredRenditions.size());

		for(int i=0; i<configuredRenditions.size(); i++) {
			Rendition rendition = configuredRenditions.get(i);
			String name = rendition.getKey();
			logger.info("Configured Rendition Name >> "+name);
			Method method = (Method) methodMap.get(name);
			String renditionQuery = (String) method.invoke(renditions);
			if(Utility.isNotNullEmpty(renditionQuery)) {
				logger.info("Has File for Configured Rendition");
				failureResponseList.addAll(queryRenditions(httpclient, sessionID, renditionQuery, document));
			}
		}
	}
}