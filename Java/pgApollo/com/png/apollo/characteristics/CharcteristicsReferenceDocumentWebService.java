/*
 * Added by APOLLO Team
 * For Characteristic Related Webservice
 */

package com.png.apollo.characteristics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import com.dassault_systemes.enovia.characteristic.interfaces.ENOCharacteristicEnum;
import com.dassault_systemes.enovia.characteristic.restapp.util.ENOCharacteristicsRestAppUtil;
import com.dassault_systemes.enovia.characteristic.util.JsonUtil;
import com.dassault_systemes.enovia.criteria.util.CriteriaUtil;
import com.dassault_systemes.platform.restServices.RestService;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.AccessUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.XSSUtil;
import com.matrixone.apps.framework.ui.UINavigatorUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloConstants;

import matrix.db.AccessList;
import matrix.db.BusinessObject;
import matrix.db.BusinessObjectList;
import matrix.db.RelationshipType;
import matrix.util.Pattern;
import matrix.util.StringList;

@Path("ReferenceDocument")
public class CharcteristicsReferenceDocumentWebService extends RestService{
	
	protected CacheControl cacheControl;
	
	public CharcteristicsReferenceDocumentWebService() {
	    this.cacheControl = new CacheControl();
	    this.cacheControl.setNoCache(true);
	    this.cacheControl.setNoStore(true);
	}

	@Path("/connectTestMethodReferenceDocument")
	  @POST
	  public Response connectedTestMethodReferenceDocument(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, String paramString)
	    throws Exception
	  {
	    StringBuilder localStringBuilder = new StringBuilder();
	    matrix.db.Context localContext = authenticate(paramHttpServletRequest);
	    ENOCharacteristicsRestAppUtil.checkCSRFToken(paramHttpServletRequest);
	    StringList localStringList = new StringList();
	    String[] localStringArray = {};
	    JsonObject localJsonObject = JsonUtil.toJsonObject(paramString);
	    String str = localJsonObject.getString("objectId");
	    DomainObject dobj = DomainObject.newInstance(localContext, str);
	    MapList localMapList = new MapList();
	    if (localJsonObject.containsKey("testMethodReferenceDocumentIds"))
	    {
	      Object localObject = localJsonObject.getJsonArray("testMethodReferenceDocumentIds");
	      for (int i = 0; i < ((JsonArray)localObject).size(); i++) {
	        if (FrameworkUtil.isObjectId(localContext, ((JsonArray)localObject).getString(i))) {
	          localStringList.add(((JsonArray)localObject).getString(i));
	        } else {
	          localStringList.add(MqlUtil.mqlCommand(localContext, "print bus $1 select $2 dump", new String[] { ((JsonArray)localObject).getString(i), "id" }));
	        }
	      }
	      //APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Starts
	      Map mapInfo = getTMWithSubtypeTAMU(localContext, localStringList);
	      localStringList = (StringList)mapInfo.get("TAMUList");
	      //APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Ends
	      List<String> localList = localStringList.toList();
	      localStringArray = localList.toArray(new String[0]);
	      StringList objectSelects = new StringList();
	      objectSelects.addElement("type");
	      objectSelects.addElement("name");
	      objectSelects.addElement("revision");
	      objectSelects.addElement("id");
	      objectSelects.addElement("policy");
	      objectSelects.addElement("latest");
	      objectSelects.addElement("title");
	      objectSelects.addElement("description");
	      objectSelects.addElement("originated");
	      localMapList = DomainObject.getInfo(localContext, localStringArray, objectSelects);      
	      
	      Iterator iterator = localMapList.iterator();

	      Object object;
	      Map mapTMRD;

	      while (iterator.hasNext()) {
	    	  object = iterator.next();
	    	  mapTMRD = (Map) object;
	    	  String sType = (String) mapTMRD.get("type");
	    	  String sTypeDisplayName = EnoviaResourceBundle.getAdminI18NString(localContext, "Type", sType,
	    			  localContext.getLocale().getLanguage());
	    	  mapTMRD.put("type", sTypeDisplayName);
	    	  String sPolicy = (String) mapTMRD.get("policy");
	    	  String sPolicyDisplayName = EnoviaResourceBundle.getAdminI18NString(localContext, "Policy", sPolicy,
	    			  localContext.getLocale().getLanguage());
	    	  mapTMRD.put("policy", sPolicyDisplayName);
	    	  if (mapTMRD.containsKey("from[Active Version].to.attribute[Title]")) {
	    		  mapTMRD.put("documentTitle", mapTMRD.remove("from[Active Version].to.attribute[Title]"));
	    		  mapTMRD.put("documentId", mapTMRD.remove("from[Active Version].to.id"));
	    	  }
	      }
	      
	      //APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Starts
		  localMapList.add(mapInfo);
		  //APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Ends
	    }
	    Response localObject = null;
	    boolean bAccessGiven = false;
		BusinessObjectList boList = new BusinessObjectList();
		String sContextUser = DomainConstants.EMPTY_STRING;

	    try
	    {
	    	BusinessObject boToBeConnected;
	    	boolean hasToConnectAccess = false;
			matrix.db.Access access = new matrix.db.Access();
			AccessList accessList = null;
			sContextUser = localContext.getUser();

	    	for(String sToBeConnectedTMRDId : localStringArray)
			{
				boToBeConnected = new BusinessObject(sToBeConnectedTMRDId);					
				hasToConnectAccess = boToBeConnected.getAccessMask(localContext).hasToConnectAccess();
				
				if(!hasToConnectAccess)
				{
					boList.add(boToBeConnected);
				}					
			}				
			
			if(!boList.isEmpty())
			{
				access.setToConnectAccess(true);
				access.setUser(sContextUser);
				accessList = new AccessList(access);
				AccessUtil.grantAccess(localContext, boList, accessList, pgApolloConstants.PERSON_USER_AGENT);	
				bAccessGiven = true;
			}	    	
	    	
	    	DomainRelationship.connect(localContext, dobj, DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, true, localStringArray);
	    	
	    	if(!boList.isEmpty())
			{
				AccessUtil.revokeAccess(localContext, boList, new StringList(sContextUser));
				bAccessGiven = false;
			}	    	
	    	
	      //ENOCharacteristicServices.connectTestMethods(localContext, str, localStringList);
	      localStringBuilder.append(ENOCharacteristicsRestAppUtil.transformToJSON(localMapList));
	      localObject = Response.status(200).entity(localStringBuilder.toString()).cacheControl(this.cacheControl).build();
	    }
	    catch (Exception localException)
	    {
	      localObject = setErrorResponse(localException);
	    }
	    finally
	    {
	    	if(bAccessGiven)
			{
				AccessUtil.revokeAccess(localContext, boList, new StringList(sContextUser));
			}	   
	    }
	    return localObject;
	  }
	  @Path("/configuration")
	  @GET
	  @Produces({"application/json", "application/ds-json"})
	  public Response getConfiguration(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @javax.ws.rs.core.Context HttpServletResponse paramHttpServletResponse)
	  {
	    Response localResponse = null;
	    ENOCharacteristicsRestAppUtil.disableResponseCache(paramHttpServletResponse);
	    
	    matrix.db.Context localContext = null;
	    try
	    {
	      localContext = authenticate(paramHttpServletRequest);
	    }
	    catch (Exception localException1)
	    {
	      localResponse = Response.status(403).build();
	    }
	    try
	    {
	      String str1 = getCSRFToken(paramHttpServletRequest);
	      
	      JsonObjectBuilder localJsonObjectBuilder = Json.createObjectBuilder();
	      String str = "";
	      String str2 = EnoviaResourceBundle.getProperty(localContext, "Characteristic.Preferences.ValidStandardOperatingProcedureList");
	      String str5 = EnoviaResourceBundle.getProperty(localContext, "Characteristic.Preferences.ValidIllustrationList");
	      String str6 = EnoviaResourceBundle.getProperty(localContext, "Characteristic.Preferences.ValidQualitySpecificationList");
	      if (UIUtil.isNotNullAndNotEmpty(str2)) {
	    	  str.concat(str2);
	      }
	      if (UIUtil.isNotNullAndNotEmpty(str5)) {
	    	  if(UIUtil.isNotNullAndNotEmpty(str)) {
	    	  str.concat("," + str5);
	    	  } else {
	    		  str.concat(str5);
	    	  }
	      } 
	      if (UIUtil.isNotNullAndNotEmpty(str6)) {
	    	  if(UIUtil.isNotNullAndNotEmpty(str)) {
	    	  str.concat("," + str6);
	    	  } else {
	    		  str.concat(str6);
	    	  }
	      }
	      String[] arrayOfString = str.split(",");
	      String str3 = "";
	      for (int i = 0; i < arrayOfString.length; i++)
	      {
	        if (UIUtil.isNotNullAndNotEmpty(str3)) {
	          str3 = str3.concat(",");
	        }
	        str3 = str3.concat(PropertyUtil.getSchemaProperty(localContext, arrayOfString[i]));
	      }
	      localJsonObjectBuilder.add("csrf", XSSUtil.encodeForHTML(localContext, str1));
	      localJsonObjectBuilder.add("enableTestMethodReferenceDocumentName", str);
	      localJsonObjectBuilder.add("typesActualValue", str3);
	      
	      localResponse = Response.ok(localJsonObjectBuilder.build().toString(), "application/json").cacheControl(this.cacheControl).build();
	    }
	    catch (Exception localException2)
	    {
	      localResponse = Response.status(500).build();
	    }
	    return localResponse;
	  }
	  
	  public static String getCSRFToken(HttpServletRequest paramHttpServletRequest)
			    throws Exception
			  {
			    String str = "";
			    try
			    {
			      HttpSession localHttpSession = paramHttpServletRequest.getSession(true);
			      Object localObject = localHttpSession.getAttribute("ENO_CSRF_TOKEN");
			      if (localObject == null)
			      {
			        str = generateCSRFToken();
			        localHttpSession.setAttribute("ENO_CSRF_TOKEN", str);
			      }
			      else
			      {
			        str = localObject.toString();
			      }
			    }
			    catch (Exception localException) {}
			    return str;
			  }
	  
	  public static String generateCSRFToken()
			    throws Exception
			  {
			    return UINavigatorUtil.generateRandomId("SHA1PRNG", 32);
			  }
	  
	  @Path("/getassociatedtestmethodReferenceDocument")
	  @GET
	  @Produces({"application/json", "application/ds-json"})
	  public Response getConnectedTestMethodReferenceDocument(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectId") String paramString)
	    throws Exception
	  {
	    Response localResponse = null;
	    StringBuilder localStringBuilder = new StringBuilder();
	    try
	    {
	      matrix.db.Context localContext = authenticate(paramHttpServletRequest);
	      
	      MapList localMapList1 = new MapList();
	      MapList localMapList2 = new MapList();
	      DomainObject domObj = DomainObject.newInstance(localContext, paramString);
	      
	      Pattern typePattern = new Pattern("pgStandardOperatingProcedure");
	      typePattern.addPattern("pgIllustration");
	      typePattern.addPattern("pgQualitySpecification");
	      //APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Starts
	      typePattern.addPattern(pgApolloConstants.TYPE_TEST_METHOD_SPECIFICATION);
	      //APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Ends
	      
	      StringList objectSelects = StringList.create(new String[] { "id", "name", DomainConstants.ATTRIBUTE_TITLE, "revision", "owner", "description", "id", "current", "policy", "revision", "originated", "latest", CriteriaUtil.stringConcat(new Object[] {ENOCharacteristicEnum.CharacteristicAttributes.TITLE.getAttributeSelect(localContext) }),  CriteriaUtil.stringConcat(new Object[] { "from[", CommonDocument.RELATIONSHIP_ACTIVE_VERSION, "].to.", ENOCharacteristicEnum.CharacteristicAttributes.TITLE.getAttributeSelect(localContext) }), 
	      CriteriaUtil.stringConcat(new Object[] { "from[", CommonDocument.RELATIONSHIP_ACTIVE_VERSION, "].to.", "id" }) });
	      short recurseToLevel = 1;
	      
	      //localMapList1 = domObj.getRelatedObjects(localContext, "Reference Document",typePattern.getPattern(), objectSelects, null, false, true, recurseToLevel, "", "");
	      localMapList1 = domObj.getRelatedObjects(localContext,  // Context
	    		  								DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // Relationship Type Pattern
	    		  								typePattern.getPattern(),  // Type Pattern
	    		  								objectSelects, // Object Selects
	    		  								null,  // Rel pattern
	    		  								false, // Get To
	    		  								true, // Get From
	    		  								recurseToLevel, // recurse level
	    		  								"", // object where clause
	    		  								"", // rel where clause
	    		  								0); // object limit
	      Map localMap = null;
	      String str1 = DomainConstants.EMPTY_STRING;
	      String str2 = DomainConstants.EMPTY_STRING;
	      String str3 = DomainConstants.EMPTY_STRING;
	      String str4 = DomainConstants.EMPTY_STRING;
	      for (Object localObject : localMapList1)
	      {
	        localMap = (Map)localObject;
	        
	        str1 = (String)localMap.get("type");
	        str2 = EnoviaResourceBundle.getAdminI18NString(localContext, "Type", str1, localContext.getLocale().getLanguage());
	        localMap.put("type", str2);
	        
	        str3 = (String)localMap.get("policy");
	        str4 = EnoviaResourceBundle.getAdminI18NString(localContext, "Policy", str3, localContext.getLocale().getLanguage());
	        localMap.put("policy", str4);
	        if (localMap.containsKey("from[Active Version].to.attribute[Title]"))
	        {
	          localMap.put("documentTitle", localMap.remove("from[Active Version].to.attribute[Title]"));
	          localMap.put("documentId", localMap.remove("from[Active Version].to.id"));
	          localMapList2.add(localMap);
	        }
	        else
	        {
	          localMapList2.add(localMap);
	        }
	      }
	      localStringBuilder.append(ENOCharacteristicsRestAppUtil.transformToJSON(localMapList2));
	      
	      localResponse = Response.status(200).entity(localStringBuilder.toString()).cacheControl(this.cacheControl).build();
	    }
	    catch (Exception localException)
	    {
	      localResponse = setErrorResponse(localException);
	    }
	    return localResponse;
	  }
	  
	  @Path("/getassociatedtestmethodReferenceDocumentAPP")
	  @GET
	  //public Response getConnectedTestMethodReferenceDocumentAPP(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectId") String paramString)
	  public Object getConnectedTestMethodReferenceDocumentAPP(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @QueryParam("objectId") String paramString)
	    throws Exception
	  {
		    Response localResponse = null;
		    boolean bContextPushed=false;
		    matrix.db.Context localContext = authenticate(paramHttpServletRequest);
		    try
		    {
		      
		      
		      DomainObject doAPPbj = DomainObject.newInstance(localContext, paramString);
		      
		      Pattern typePattern = new Pattern(pgApolloConstants.TYPE_PG_STANDARD_OPERATING_PROCEDURE);
		      typePattern.addPattern(pgApolloConstants.TYPE_PG_ILLUSTRATION);
		      typePattern.addPattern(pgApolloConstants.TYPE_PG_QUALITY_SPECIFICATION);
		      //Apollo 2018x.5 A10-631 - Added for showing Test Method with sub-type as TAMU in export file -  Starts
		      typePattern.addPattern(pgApolloConstants.TYPE_TEST_METHOD_SPECIFICATION);
		      //Apollo 2018x.5 A10-631 - Added for showing Test Method with sub-type as TAMU in export file -  Ends
		      StringList objectSelects = new StringList(1);
		      //Apollo 2018x.5 A10-631 - Modified for showing name of TMRD -  Starts
		      objectSelects.add(DomainConstants.SELECT_NAME);
		      //Apollo 2018x.5 A10-631 - Modified for showing name of TMRD -  Ends
		      
		      short recurseToLevel = 1;
		      
			  // Get all PlmParameter objects (Characteristics) connected to APP
		      StringList objSelects = new StringList(1);
		      objSelects.add(DomainConstants.SELECT_ID);	      
			      
		      MapList mlConnectedCharList = doAPPbj.getRelatedObjects(localContext,
																			pgApolloConstants.RELATIONSHIP_PARAMETER_AGGREGATION,
																			pgApolloConstants.TYPE_PLM_PARAMETER,
																			objSelects,				    //Object Select
																			null,						//rel Select
																			false,						//get To
																			true,						//get From
																			(short)0,	   				//recurse level
																			null,						//where Clause
																			null,
																			0);
		      Iterator<?> itr = mlConnectedCharList.iterator();
			  StringBuilder strData = new StringBuilder();
			  int counter = 0;
			  Map map = null;
			  String plmParameterId = DomainConstants.EMPTY_STRING;
			  MapList docList = null;
			  DomainObject domObj = null;
			  Map docMap = null;
			  ContextUtil.pushContext(localContext, PropertyUtil.getSchemaProperty(localContext, "person_UserAgent"), null, localContext.getVault().getName());
		      bContextPushed = true;
			  while(itr.hasNext()){				
				  	  counter++;
					  map = (Map) itr.next();
					  plmParameterId = (String) map.get(DomainConstants.SELECT_ID);
					  docList = new MapList();
					  domObj = DomainObject.newInstance(localContext,plmParameterId);	
					  
					  docList = domObj.getRelatedObjects(localContext, // Context
							  							DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // Relationship Pattern
														  typePattern.getPattern(), // Type Pattern
														  objectSelects, // Object Selects
														  null, // Rel Selects
														  false, // Get To 
														  true, // Get From
														  recurseToLevel, // Recurse level
														  "", // Object where clause
														  "", // Rel where clause
														  0); // limit
					  
					  if(null != docList && docList.size() > 0) {
						  strData.append(plmParameterId).append("#");
					      for (Object docObject : docList)
					      {
					        docMap = (Map)docObject;	
					        //Apollo 2018x.5 A10-631 - Modified for showing name of TMRD -  Starts
					        strData.append((String)docMap.get(DomainConstants.SELECT_NAME)).append(pgApolloConstants.CONSTANT_STRING_PIPE);
					        //Apollo 2018x.5 A10-631 - Modified for showing name of TMRD -  Ends
					      }
					      if(counter < mlConnectedCharList.size()){
					    	  strData.append("$");
					      }
					  } else {
						  strData.append(plmParameterId).append("#");
						  strData.append("").append(pgApolloConstants.CONSTANT_STRING_PIPE);
						  if(counter < mlConnectedCharList.size()){
					    	  strData.append("$");
					      }
					  }
			  }
			  ContextUtil.popContext(localContext);
			  bContextPushed=false;
			  return Response.status(200).entity(strData.toString()).build();
		    }
		    catch (Exception localException)
		    {
		      System.out.println("Error in getConnectedTestMethodReferenceDocumentAPP, localException="+localException);
		      localResponse = setErrorResponse(localException);
		    }
		    finally {
		    	if(bContextPushed) {
		    	 ContextUtil.popContext(localContext);
				  
				  }
		    }
		    return localResponse;
		  }	  
	  
	  @Path("/disconnectTestMethodReferenceDocument")
	  @POST
	  public Response disconnectTestMethodReferenceDocument(@javax.ws.rs.core.Context HttpServletRequest paramHttpServletRequest, @FormParam("charObjId") String paramString1, @FormParam("testMethodId") String paramString2)
	    throws Exception
	  {
	    Response localResponse = Response.status(200).build();
	      boolean bAccessGiven = false;
	      matrix.db.Context localContext = null;
	      BusinessObjectList boList = new BusinessObjectList();
	      String sContextUser = DomainConstants.EMPTY_STRING;
	    try
	    {
	      localContext = authenticate(paramHttpServletRequest);
	      //ENOCharacteristicServices.disconnectTestMethod(localContext, paramString1, paramString2);
	      BusinessObject buObj1 = new BusinessObject(paramString1);
	      BusinessObject buObj2 = new BusinessObject(paramString2);
	      RelationshipType type = new RelationshipType("Reference Document");
	      
	      boolean hasToDisconnectAccess = false;
	      BusinessObject boToBeDisconnected = null;
	      AccessList accessList = null;
	      matrix.db.Access access = null;
	      sContextUser = localContext.getUser();

	      boToBeDisconnected = new BusinessObject(buObj1);			
	      hasToDisconnectAccess = boToBeDisconnected.getAccessMask(localContext).hasToDisconnectAccess();	
	      boList.clear();
	      boList = new BusinessObjectList();
	      access = new matrix.db.Access();

	      if(!hasToDisconnectAccess)
	      {						
	    	  boList.add(boToBeDisconnected);
	    	  access.setToDisconnectAccess(true);
	    	  access.setUser(sContextUser);
	    	  accessList = new AccessList(access);
	    	  AccessUtil.grantAccess(localContext, boList, accessList, pgApolloConstants.PERSON_USER_AGENT);
	    	  bAccessGiven = true;
	      }

	      buObj2.disconnect(localContext, type, false, buObj1);

	      if(!hasToDisconnectAccess)
	      {
	    	  AccessUtil.revokeAccess(localContext, boList, new StringList(sContextUser));
	    	  bAccessGiven = false;
	      }	
	      
	    }
	    catch (Exception localException)
	    {
	      localResponse = setErrorResponse(localException);
	    }
	    finally
	    {
	    	 if(bAccessGiven)
		      {
		    	  AccessUtil.revokeAccess(localContext, boList, new StringList(sContextUser));
		      }	
	    }
	    return localResponse;
	  }
	  
	  private Response setErrorResponse(Exception paramException)
	  {
	    JsonObjectBuilder localJsonObjectBuilder = Json.createObjectBuilder();
	    paramException.printStackTrace();
	    try
	    {
	      localJsonObjectBuilder.add("message", paramException.getMessage());
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(localJsonObjectBuilder.build().toString()).cacheControl(this.cacheControl).build();
	  }
	  
	//APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Starts
	/**
	 * This is method to get Test Method with sub-type as TAMU from passed list.
	 * @param context
	 * @param slTMRDList - list of TMRD Ids
	 * @return Map value of TAMUList and NonTAMUList
	 * @throws Exception
	 */
	
	public static Map getTMWithSubtypeTAMU(matrix.db.Context context,StringList slTMRDList) throws Exception
	{ 
		Map mapReturnInfo = new HashMap();
		StringList slTAMUList = new StringList();
		StringList slNonTAMUList = new StringList();
		
		boolean bContextPushed = false;		
		try 
		{
			//Push context is needed to for reading TMRD information, as User will not get access to attribute values
			ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
			bContextPushed = true;
		      
			if(null != slTMRDList && !slTMRDList.isEmpty())
			{				
				String strAttrValTAMU = pgApolloConstants.EMPTY_STRING;
				String strType = pgApolloConstants.EMPTY_STRING;
				String strName = pgApolloConstants.EMPTY_STRING;
				DomainObject doObjectId = DomainObject.newInstance(context);
				Map mapInfo = null;
				
				StringList slSelectables = new StringList(3);
				slSelectables.addElement(pgApolloConstants.SELECT_TYPE);
				slSelectables.addElement(pgApolloConstants.SELECT_NAME);
				slSelectables.addElement(pgApolloConstants.SELECT_ATTRIBUTE_PG_ASSEMBLY_TYPE);
				
				StringList slTestMethodSpecificationType = new StringList(2);
				slTestMethodSpecificationType.addElement(pgApolloConstants.TYPE_TEST_METHOD_SPECIFICATION);
				slTestMethodSpecificationType.addElement(pgApolloConstants.TYPE_PG_TEST_METHOD);
				
				for(String sTMRDId : slTMRDList)
				{
					doObjectId.setId(sTMRDId);
					mapInfo = (Map)doObjectId.getInfo(context, slSelectables);
					strType = (String)mapInfo.get(pgApolloConstants.SELECT_TYPE);
					strName = (String)mapInfo.get(pgApolloConstants.SELECT_NAME);
					strAttrValTAMU = (String)mapInfo.get(pgApolloConstants.SELECT_ATTRIBUTE_PG_ASSEMBLY_TYPE);
					if(!slTestMethodSpecificationType.contains(strType)  || (slTestMethodSpecificationType.contains(strType) && strAttrValTAMU.equalsIgnoreCase(pgApolloConstants.STR_TARGET_ACCEPTABLE_MARGINAL_UNACCEPTABLE)))
					{					
						slTAMUList.addElement(sTMRDId);
					}
					else
					{
						slNonTAMUList.addElement(strName);
					}
				}
			}
			mapReturnInfo.put("TAMUList", slTAMUList);
			mapReturnInfo.put("NonTAMUList", slNonTAMUList);
		} 
		catch (Exception e) 
		{
			throw e;
		}
		finally
		{
			if(bContextPushed)
			{
				ContextUtil.popContext(context);
			}
		}
		return mapReturnInfo;
	}
	//APOLLO 2018x.5 ALM 34995, 35097, 35133, 35134 - Ends
}
