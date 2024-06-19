/*
 * Added by APOLLO Team
 * For all APOLLO Constants
 */

package com.png.apollo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.dassault_systemes.enovia.criteria.util.CriteriaUtil;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeConstants;
import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeOrder;
import com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil;
import com.dassault_systemes.i3dx.appsmodel.util.EnvPropertyUtil;
import com.dassault_systemes.i3dx.command.I3DXDeleteEngine;
import com.dassault_systemes.i3dx.command.I3DXDeleteInputBuilder;
import com.dassault_systemes.i3dx.core.model.I3DXDeleteInput;
import com.dassault_systemes.i3dx.core.model.I3DXDeleteOptions;
import com.dassault_systemes.i3dx.core.model.I3DXDeleteOutputs;
import com.dassault_systemes.parameter_interfaces.ParameterInterfacesServices;
import com.dassault_systemes.platform.model.CommonWebException;
import com.dassault_systemes.platform.ven.jackson.core.type.TypeReference;
import com.dassault_systemes.platform.ven.jackson.databind.ObjectMapper;
import com.dassault_systemes.platform.ven.jackson.databind.node.ArrayNode;
import com.dassault_systemes.platform.ven.jackson.databind.node.ObjectNode;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainAccess;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.CacheUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MailUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MessageUtil;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.pl.custom.pgPLUtil;
import com.pg.v3.custom.pgV3Constants;
import com.png.apollo.designtool.getData.ExtractDataForDesignTool;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.db.Page;
import matrix.db.PathQuery;
import matrix.db.PathQueryIterator;
import matrix.db.PathWithSelect;
import matrix.db.Signature;
import matrix.db.SignatureList;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class pgApolloCommonUtil extends pgApolloConstants
{

	 private static final org.apache.log4j.Logger loggerTrace = org.apache.log4j.Logger.getLogger(pgApolloCommonUtil.class);
	 private static final ObjectMapper objectMapper = new ObjectMapper();

	/*
	 * Generic Utility method to get Object Id from simple type, name, revision
	 */
	public static String getObjectId (Context context, String strType, String strName, String strRevision) throws Exception
	{
		String strObjectId = EMPTY_STRING;
		
		StringList slObjSelect = new StringList();
		slObjSelect.addElement(DomainConstants.SELECT_ID);
		
		MapList mlTemp =  DomainObject.findObjects(context, strType,strName,strRevision,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD, null,false, slObjSelect);
		if(null != mlTemp && !mlTemp.isEmpty())
		{
			Map mpTemp = (Map) mlTemp.get(0) ;
			strObjectId = (String) mpTemp.get(DomainConstants.SELECT_ID);			
		}		
		return strObjectId;
	}
	
	/*
	 * Generic Utility method to get Object Map with Select List  from simple type, name, revision
	 */
	public static Map getObjectIdWithSelects (Context context, String strType, String strName, String strRevision, StringList objectSelectList) throws Exception
	{
		Map returnMap = null;
		
		StringList slObjSelect = new StringList();
		slObjSelect.addElement(DomainConstants.SELECT_ID);
		slObjSelect.addAll(objectSelectList);
		MapList mlTemp =  DomainObject.findObjects(context, strType,strName,strRevision,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD, null,false, slObjSelect);
		if(null != mlTemp && !mlTemp.isEmpty())
		{
			returnMap =	(Map) mlTemp.get(0) ;	
		}
		
		return returnMap;
	} 

	/*
	 * Generic Utility method to get StringList based on Object. Object could be String or StringList
	 */
	
    public static StringList getStringListFromObject(Object value) 
    {
        StringList stringList=new StringList();  
        if(null != value)
        {
	        if(value instanceof String)
	        {
	        	stringList.add((String)value);
	        }
	        else
	        {
	        	stringList=(StringList)value;
	        }
        }
        return stringList;
    }
    
	/*
	 * Generic Utility method to get StringList based on Object value in Map. Object could be String or StringList
	 */
    
    public static StringList getStringListValueFromMap(Map mp, String strKey) throws Exception
    {
   	 StringList slList = new StringList();	
   	 try
   	 {	    	     	 
	    	 if(mp.containsKey(strKey))
	    	 {    
	    		 Object obj = mp.get(strKey);
	    		 slList = getStringListFromObject(obj);
	    	 }
	     }
   	 catch(Exception ex)
   	 {
   		 throw ex;
   	 }    	 
   	 return slList;
    }
	
	
	public static Map getPlatformDetailsForCATIAAPP (Context context, String strObjectId) throws Exception
	{
		Map mpReturn = new HashMap();
		try
		{
			if(UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				DomainObject doObject = DomainObject.newInstance(context,strObjectId);
				//String strRelPattern = pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM + CONSTANT_STRING_COMMA + pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS + CONSTANT_STRING_COMMA + pgApolloConstants.RELATIONSHIP_PRIMARYORGANIZATION + CONSTANT_STRING_COMMA + pgApolloConstants.RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA;
				String strRelPattern = pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM + CONSTANT_STRING_COMMA + pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS + CONSTANT_STRING_COMMA + pgApolloConstants.RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA;
				
				StringList slObjectSelect = new StringList();
				slObjectSelect.addElement(DomainConstants.SELECT_ID);
				slObjectSelect.addElement(DomainConstants.SELECT_NAME);
				
				StringList slRelSelect = new StringList();
				slRelSelect.addElement("attribute[" + pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE + "]");
				slRelSelect.addElement("attribute[" + pgApolloConstants.ATTRIBUTE_PG_CHASSIS_TYPE + "]");
				slRelSelect.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);
												
				MapList mlConnectedObjects = doObject.getRelatedObjects(context,
						strRelPattern,
						DomainConstants.QUERY_WILDCARD,
						slObjectSelect,//Object Select
						slRelSelect,//rel Select
						false,//get To
						true,//get From
						(short)1,//recurse level
						null,//where Clause
						null,
						0);
				
				if(null != mlConnectedObjects && !mlConnectedObjects.isEmpty())
				{
					Map mpT = new HashMap();
					String strRelName = DomainConstants.EMPTY_STRING;
					String strObjName = DomainConstants.EMPTY_STRING;
					String strAttributeValue = DomainConstants.EMPTY_STRING;
					for(int i=0 ; i< mlConnectedObjects.size() ; i++ )
					{
						mpT = new HashMap();
						mpT = (Map) mlConnectedObjects.get(i);
						strRelName = (String) mpT.get(DomainConstants.SELECT_RELATIONSHIP_NAME);
						strObjName = (String) mpT.get(DomainConstants.SELECT_NAME);
						if(pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_CHASSIS.equalsIgnoreCase(strRelName))
						{
							mpReturn.put(STR_PRODUCT_TECHNOLOGY_CHASSIS, strObjName);
						}
						else if(pgApolloConstants.RELATIONSHIP_PGDOCUMENT_TO_BUSINESSAREA.equalsIgnoreCase(strRelName))
						{
							mpReturn.put(STR_BUSINESS_AREA, strObjName);
						}
						else if(pgApolloConstants.RELATIONSHIP_PG_DOCUMENT_TO_PLATFORM.equalsIgnoreCase(strRelName))
						{
							strAttributeValue = (String) mpT.get("attribute[" + pgApolloConstants.ATTRIBUTE_PG_PLATFORM_TYPE + "]");
							if(pgApolloConstants.STR_PRODUCT_CATEGORY_PLATFORM.equalsIgnoreCase(strAttributeValue))
							{
								mpReturn.put(STR_PRODUCT_CATEGORY_PLATFORM, strObjName);
							}
							if(pgApolloConstants.STR_PRODUCT_TECHNOLOGY_PLATFORM.equalsIgnoreCase(strAttributeValue))
							{
								mpReturn.put(STR_PRODUCT_TECHNOLOGY_PLATFORM, strObjName);
							}
						}						
					}
				}	
				
			}
		
		}
		catch(Exception ex)
		{
			loggerTrace.error(ex.getMessage(), ex);
			throw ex;
		}
		
		return mpReturn;
	}
	
	
	/**
	 * This method will return the ObjectID of the latest Release Revision for the given type and Name. 
	 * If the Object has only one revision which is not released yet and the parameter bInWorkFirstRev is true this method will return the first revision of the object.
	 * If the Object had only one revision which is not released yet and the parameter bInWorkFirstRev is false this method will return empty string.
	 * @param context
	 * @param strType
	 * @param strName
	 * @param bInWorkFirstRev
	 * @return
	 * @throws Exception
	 */
	public static String getLatestReleaseRevisionId(Context context , String strType ,String strName, boolean bInWorkFirstRev)throws Exception
	{
		String strObjectId = EMPTY_STRING;		
		try
		{		
			StringList slObjSelect = new StringList();
			slObjSelect.addElement(DomainConstants.SELECT_ID);
			slObjSelect.addElement(DomainConstants.SELECT_CURRENT);
			slObjSelect.addElement(DomainConstants.SELECT_REVISION);
			String strcurrent = EMPTY_STRING;
			Map rmMap = null;
			MapList mlTemp =  DomainObject.findObjects(context, strType,strName,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD,DomainConstants.QUERY_WILDCARD, null,true, slObjSelect);
			mlTemp.sort(DomainConstants.SELECT_REVISION, "descending", "string");
			if(null != mlTemp && !mlTemp.isEmpty())
			{
				if(mlTemp.size()==1)
				{					
					rmMap = (Map) mlTemp.get(0);					
					strcurrent =(String)rmMap.get(DomainConstants.SELECT_CURRENT);					
					if(strcurrent.equals(STATE_RELEASE)||strcurrent.equals(STATE_RELEASED ) || (bInWorkFirstRev && ! strcurrent.equals(STATE_OBSOLETE)))
					{
						strObjectId =(String)rmMap.get(DomainConstants.SELECT_ID);
					}
				}
				else 
				{
					Iterator mlTempItr =mlTemp.iterator();
					while(mlTempItr.hasNext()) 
					{
						rmMap =(Map)mlTempItr.next();
						strcurrent =(String)rmMap.get(DomainConstants.SELECT_CURRENT);
						if(strcurrent.equals(STATE_RELEASE) || strcurrent.equals(STATE_RELEASED)) 
						{
							strObjectId =(String)rmMap.get(DomainConstants.SELECT_ID);
							break;
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			loggerTrace.error(ex.getMessage(), ex);
			throw ex;
		}
		return strObjectId;
	}
	
	 /**
	  * This Method will Take Page file name and the key Pattern as arguments. This method will return a map of key value pair which matches the key pattern. 
	  * @param context
	  * @param strPageName
	  * @param strKeyPattern
	  * @return
	  * @throws Exception
	  */
	 public static Map geValueMapfromProperty(Context context, String strPageName , String strKeyPattern) throws Exception {
	 		Map returnMap = new HashMap();
	 		try
	 		{
	 			String isPageExists	= MqlUtil.mqlCommand(context, "list page $1", strPageName);
	 			String strProperties= UIUtil.isNotNullAndNotEmpty(isPageExists) ? MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageName) : "";
	 			if(UIUtil.isNotNullAndNotEmpty(strProperties))
	 			{
		 			Properties properties = new Properties();
		 			properties.load(new StringReader(strProperties));
		 			Set allPropertyset = properties.stringPropertyNames();
		 			Iterator allPropertysetItr =allPropertyset.iterator();
		 			String strKey = EMPTY_STRING;
		 			while(allPropertysetItr.hasNext())
		 			{
		 				strKey =(String)allPropertysetItr.next();
		 				if(strKey.contains(strKeyPattern)) {
		 					returnMap.put(strKey, properties.getProperty(strKey));
		 				}
		 			}
	 			}
	 		}catch(Exception ex)
	 		{
	 			loggerTrace.error(ex.getMessage(), ex);
	 			throw ex;
	 		}
	 		return returnMap;
	 	}
	 
		/**
		 * This is a utility method to get the value from Page file property
		 * @param context
		 * @param strPageName : Page File name
		 * @param strKey : Property Key
		 * @return : returns value of the given property key
		 * @throws Exception
		 */
		public static String getPageProperty(Context context, String strPageName, String strKey)throws Exception {
			String strValue = DomainConstants.EMPTY_STRING;
			try{
				String isPageExists	= MqlUtil.mqlCommand(context, "list page $1", strPageName);
				String strProperties= UIUtil.isNotNullAndNotEmpty(isPageExists) ? MqlUtil.mqlCommand(context, "print page $1 select content dump", strPageName) : "";
				if(UIUtil.isNotNullAndNotEmpty(strProperties) && UIUtil.isNotNullAndNotEmpty(strKey)){
					Properties properties = new Properties();
					properties.load(new StringReader(strProperties));
					strValue = properties.getProperty(strKey);
				}
			}catch(Exception ex){
				loggerTrace.error(ex.getMessage(), ex);
				throw ex;
			}
			return strValue;
		}
		
		
		/**
		 * This is a utility method to get the value from Page file property
		 * @param context
		 * @param strPageName : Page File name
		 * @param strKey : Property Key
		 * @return : returns value of the given property key
		 * @throws MatrixException 
		 * @throws IOException 
		 * @throws Exception
		 */
		public static String getPagePropertyIgnoreCase(Context context, String strPageName, String strKey) throws MatrixException, IOException {
			String strValue="";
			String strPageContent=(String) CacheUtil.getCacheObject(context, strPageName);		
			if(UIUtil.isNullOrEmpty(strPageContent)) {
				Page pageObject = new Page(strPageName);
				boolean isPageExists	= pageObject.exists(context);
				if(isPageExists)
				{
					pageObject.open(context);
					strPageContent = pageObject.getContents(context);
					pageObject.close(context);
					CacheUtil.setCacheObject(context, strPageName, strPageContent);
				}
				else
				{
					strPageContent = DomainConstants.EMPTY_STRING;
				}
				
			}			
			if(UIUtil.isNotNullAndNotEmpty(strPageContent) && UIUtil.isNotNullAndNotEmpty(strKey)) {
				Properties properties = new Properties();
				properties.load(new StringReader(strPageContent));
				strValue = getPropertyIgnoreCase(properties, strKey);
			}
			return strValue;
		}
		
		
		/**
		  * get value from {Properties}
		  * 
		  * @param props
		  * @param key
		  * @return
		  */
		 public static String getPropertyIgnoreCase(Properties properties, String key) {
		  return getPropertyIgnoreCase(properties, key, DomainConstants.EMPTY_STRING);
		 }

		 /**
		  * get value from {Properties}, if no key exist then return default value.
		  * 
		  * @param props
		  * @param key
		  * @param defaultV
		  * @return
		  */
		 public static String getPropertyIgnoreCase(Properties properties, String strKey, String strDefaultValue) {
			 String strValue = properties.getProperty(strKey);
			 if (null != strValue)
			 {
				 return strValue;
			 }
			 // Not matching with the actual key then
			 Set<Entry<Object, Object>> set = properties.entrySet();
			 Iterator<Entry<Object, Object>> it = set.iterator();
			 while (it.hasNext()) {
				 Entry<Object, Object> entry = it.next();
				 if (strKey.equalsIgnoreCase((String) entry.getKey())) {
					 return (String) entry.getValue();
				 }
			 }
			 return strDefaultValue;
		 }
		
		
		/**
		 * Method to fetch APP Object Id connected 
		 * @param context
		 * @param domVPMReference
		 * @return
		 * @throws Exception
		 */
		public static String fetchAPPObjectId(matrix.db.Context context, String strVPMRefId) throws Exception 
		{		
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);		
			String strAPPObjectId = DomainConstants.EMPTY_STRING;		
			try 
			{
				if(UIUtil.isNotNullAndNotEmpty(strVPMRefId)) 
				{
					DomainObject domVPMReference = DomainObject.newInstance(context,strVPMRefId);
					//Get APP associated with VPMReference
					MapList mlRelatedAPP = domVPMReference.getRelatedObjects(context,
							DomainConstants.RELATIONSHIP_PART_SPECIFICATION, // Relationship Pattern
							pgApolloConstants.TYPE_PGASSEMBLEDPRODUCTPART, // Type Pattern
							objectSelects,//Object Select
							null,//rel Select
							true,//get To
							false,//get From
							(short)1,//recurse level
							null,//object where Clause
							null, // Rel Where clause
							0);	// limit
					if(null != mlRelatedAPP && !mlRelatedAPP.isEmpty()) 
					{
						Map mpRelatedAPP = (Map)mlRelatedAPP.get(0);
						strAPPObjectId = (String)mpRelatedAPP.get(DomainConstants.SELECT_ID);
					}
				}
			} catch (Exception e) {
				loggerTrace.error(e.getMessage(), e);
				throw e;
			}
			return strAPPObjectId;
		}
		
		//Apollo 2018x.5 Requirement 34689- Start
		/**
		 * This method Will return RMP Object Id . If the Phase is Development or Pilot it will return the latest revision of the object. 
		 * It the Phase is Production then it will return the Latest Release revision of the RMO. 
		 * @param context
		 * @param strRMPName
		 * @return
		 * @throws Exception
		 */
		public static Map getRMPIdForSync (matrix.db.Context context , String strRMPName)throws Exception{
			
			Map mapReturn = new HashMap();
			StringBuffer sbMessage = new StringBuffer();
			String strReturn =DomainConstants.EMPTY_STRING;
			boolean bContextPushed = false;
			try
			{				
				ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
				bContextPushed = true;
				//Apollo 2018x.6 A10-771 - Starts
				String strRMPRevision = EMPTY_STRING;
				//Apollo 2018x.6 A10-771 - Ends
				StringList slObjSelect = new StringList();
				slObjSelect.addElement(DomainConstants.SELECT_ID);
				slObjSelect.addElement(DomainConstants.SELECT_CURRENT);
				slObjSelect.addElement(DomainConstants.SELECT_REVISION);
				slObjSelect.addElement(pgApolloConstants.SELECT_ATTRIBUTE_RELEASE_PHASE);
				String strCurrent = DomainConstants.EMPTY_STRING;				
				Map rmMap = null;
				String strPhase = null;
				String sRMPPhase = DomainConstants.EMPTY_STRING;
				MapList devRMML = new MapList();
				MapList nonDevRMML = new MapList();
				MapList mlRMPList =  DomainObject.findObjects(context, //Context
						TYPE_RAW_MATERIAL, //Type Pattern
						strRMPName, //Name Pattern
						DomainConstants.QUERY_WILDCARD, //Revision Pattern
						DomainConstants.QUERY_WILDCARD, //Owner Pattern
						DomainConstants.QUERY_WILDCARD, //Vault Pattern
						null, //Bus where
						true, //expand type
						slObjSelect //Bus Select
						);
				Iterator mlTempItr = mlRMPList.iterator();
				while(mlTempItr.hasNext()) {
					rmMap = (Map)mlTempItr.next();
					strPhase =(String)rmMap.get(SELECT_ATTRIBUTE_RELEASE_PHASE);
					if(UIUtil.isNotNullAndNotEmpty(strPhase) && (STR_DEVELOPMENT_PHASE.equals(strPhase)|| STR_EXPERIMENTAL_PHASE.equals(strPhase))) {
						devRMML.add(rmMap);
					}
					else {					
						nonDevRMML.add(rmMap);
					}
				}
				if(nonDevRMML.isEmpty() && !devRMML.isEmpty()) {
					devRMML.sort(DomainConstants.SELECT_REVISION, "descending", "string");
					rmMap =(Map)devRMML.get(0);
					strReturn =(String)rmMap.get(DomainConstants.SELECT_ID);
					//Apollo 2018x.6 A10-771 - Starts
					strRMPRevision = (String)rmMap.get(DomainConstants.SELECT_REVISION);
					//Apollo 2018x.6 A10-771 - Ends
					sRMPPhase = (String)rmMap.get(SELECT_ATTRIBUTE_RELEASE_PHASE);
				} else if(!nonDevRMML.isEmpty()) {
					nonDevRMML.sort(DomainConstants.SELECT_REVISION, "descending", "string");
					if(nonDevRMML.size()==1)
					{					
						rmMap = (Map) nonDevRMML.get(0);					
						strCurrent =(String)rmMap.get(DomainConstants.SELECT_CURRENT);
						//Apollo 2018x.6 A10-771 - Starts					
						strRMPRevision =(String)rmMap.get(DomainConstants.SELECT_REVISION);	
						//Apollo 2018x.6 A10-771 - Ends				
						sRMPPhase = (String)rmMap.get(SELECT_ATTRIBUTE_RELEASE_PHASE);
						if(! strCurrent.equals(pgApolloConstants.STATE_OBSOLETE))
						{
							strReturn =(String)rmMap.get(DomainConstants.SELECT_ID);
						}
						else
						{
							sbMessage = new StringBuffer();
							//Apollo 2018x.6 A10-771 - Starts
							sbMessage.append(STR_ERROR_RAWMATERIAOBSOLETE.replace("<MATERIAL_NAME>", new StringBuilder(strRMPName).append(CONSTANT_STRING_SPACE).append(strRMPRevision).toString()));
							//Apollo 2018x.6 A10-771 - Ends
							mapReturn.put(STR_ERROR, sbMessage.toString());
							mapReturn.put(STR_ERROR_CODE, STR_ERROR_CODE_E001);
						}
					}
					else 
					{
						Iterator nonDevRMMLItr =nonDevRMML.iterator();
						while(nonDevRMMLItr.hasNext()) 
						{
							rmMap =(Map)nonDevRMMLItr.next();
							strCurrent =(String)rmMap.get(DomainConstants.SELECT_CURRENT);						
							if(strCurrent.equalsIgnoreCase(pgApolloConstants.STATE_RELEASE) || strCurrent.equalsIgnoreCase(pgApolloConstants.STATE_RELEASED)) 
							{
								strReturn = (String)rmMap.get(DomainConstants.SELECT_ID);
								//Apollo 2018x.6 A10-771 - Starts
								strRMPRevision = (String)rmMap.get(DomainConstants.SELECT_REVISION);
								//Apollo 2018x.6 A10-771 - Ends
								sRMPPhase = (String)rmMap.get(SELECT_ATTRIBUTE_RELEASE_PHASE);
								break;
							}							
						}
						if(UIUtil.isNullOrEmpty(strReturn))
						{
							sbMessage = new StringBuffer();
							sbMessage.append(STR_ERROR_NORAWMATERIAFOUND_RELEASE_PRILIMINARY.replace("<MATERIAL_NAME>", strRMPName));
							mapReturn.put(STR_ERROR, sbMessage.toString());
							mapReturn.put(STR_ERROR_CODE, STR_ERROR_CODE_E002);
						}
					}					
				}
				mapReturn.put(SELECT_ID, strReturn);
				//Apollo 2018x.6 A10-771 - Starts
				mapReturn.put(SELECT_REVISION, strRMPRevision);
				//Apollo 2018x.6 A10-771 - Ends
				mapReturn.put(SELECT_ATTRIBUTE_RELEASE_PHASE, sRMPPhase);
			}
			catch(Exception ex) 
			{
				loggerTrace.error(ex.getMessage(), ex);
				throw ex;
			} 
			finally 
			{
				if(bContextPushed){
					ContextUtil.popContext(context);
					bContextPushed = false;
				}
			}
			return mapReturn;
		}
		//Apollo 2018x.5 Requirement 34689- End
		
		
	
		/**
		 * APOLLO 2018x.5 ALM Requirement-35062, 34604, 34606, 34607, 34671, 34672, 34673, 34491 - A10-549, A10-532, A10-505 - RMP Validations
		 * New Method Added for RMP restriction based on Expiration Date, Restrictions for RMP usage, Primary RMP access for originator of CATIA APP , Primary RMP access for originator of CATIA APP
		 * Method Added to check validation on RMP
		 * @param context
		 * @param strAPPObjectId
		 * @param mlStacking
		 * @param bIgnoreMtrilRestrictionWarning
		 * @return
		 * @throws Exception
		 * 
		 * Mapping for Error Codes 
		 * E001 : Raw Material <MATERIAL_NAME> is in Obsolete state
		 * E002 : Latest Released Revisions NOT found for Raw Material <MATERIAL_NAME>.
		 * E003 : Raw Material <MATERIAL_NAME> does not exist in Enovia database.
		 * E004 : Error: No Read access on <MATERIAL_NAME>.
		 * E005 : Core Material <CORE_MATERIAL> is not connected to Raw Material <MATERIAL_NAME>.
		 * E006 : Core Material <CORE_MATERIAL> is not Latest revision.
		 * E007 : Core Material <CORE_MATERIAL> is not in released state.
		 * E008 : Core Material <CORE_MATERIAL> is not created for Laminate Function <LAMINATE_FUNCTION>.
		 * E009 : Core Material <CORE_MATERIAL> is not associated to Raw Material <MATERIAL_NAME>.
		 * E010 : Core Material <CORE_MATERIAL> does not exist in Enovia database.
		 * E011 : Raw Material <MATERIAL_NAME> with Release Phase <RELEASE_PHASE> cannot be used in the structure.
		 * E012 : Error: <MATERIAL_NAME> has expired. Please update Material Characteristic RMP.		 * 
		 * E013-01 : Block for New : Error: <MATERIAL_NAME> is no longer Fit For Use. Please update Material Characteristic RMP Name with a new value.
		 * E013-02 : Block for All : Error: <MATERIAL_NAME> is no longer Fit For Use. Please update Material Characteristic RMP Name with a new value.
		 * E014 : Error: <MATERIAL_NAME> cannot be used because of missing Material Restriction Comment. Please contact material owner to update this attribute or update Material Characteristic RMP Name with a new RMP value in the model.
		 * E015 : Material Restriction Warning
		 * E016 : Material Function <MATERIAL_FUNCTION> is not valid for Raw Material <MATERIAL_NAME>
		 */
		public static MapList validateRMPDataForSync (matrix.db.Context context, String strAPPObjectId, MapList mlStacking, boolean bIgnoreMtrilRestrictionWarning, boolean bIgnoreErrorForApplyMaterial, boolean bIncludeAllErrors, boolean validateCoreMaterial) throws Exception
		{				
			Map mpMaterial = new HashMap();
			String strRMPGCAS = EMPTY_STRING;
			String strRMPId = EMPTY_STRING;
			//Apollo 2018x.6 A10-771 - Starts
			String strRMPRevision = EMPTY_STRING;
			//Apollo 2018x.6 A10-771 - Ends
			String strLaminateFunction = EMPTY_STRING;
			String strCMNameRev = EMPTY_STRING;
			
			Map mpRMPGCASToId = new HashMap();
			DomainObject doRMP = null;
			boolean bReadAccess = true;
			StringList slRMPId = new StringList();
			boolean bConsiderMFValidation = false;	
			boolean bError = false;
			MapList mlRMPDetails = new MapList();
			StringList slSelectsMF = new StringList(SELECT_PHYSICAL_ID);
			boolean bContextPushed = false;
			MapList mlEBOMPreviousRev = new MapList();
			StringBuffer sbMessage = new StringBuffer();
			StringList slProcessMFList = new StringList();
			StringList slMFPhysicalIdList = new StringList();
			StringList slConnectedRMP = new StringList();
			StringList slCM = new StringList();
			Map mpCM = new HashMap();
			String strCMRMPGCAS = EMPTY_STRING;
			String strRevision = EMPTY_STRING;
			String strLatestRevision = EMPTY_STRING;
			String strCurrent = EMPTY_STRING;
			String strCMDescription = EMPTY_STRING;	
			String strPhysicalId = EMPTY_STRING;
			Object objConnectedRMP = null;
			Map mpMF = new HashMap();
			Map mpRMP = new HashMap();
			StringList slErrorCodes = new StringList();
			StringList slAllErrors = new StringList();
			String strLayerName = EMPTY_STRING;
			String strErrorCode = EMPTY_STRING;
						
			try
			{
				if(null != mlStacking && !mlStacking.isEmpty())
				{									
					String strSelPgRelatedMaterialFromId = new StringBuilder().append(CONSTANT_STRING_SELECT_TO).append(RELATIONSHIP_PGRELATEDMATERIAL).append(CONSTANT_STRING_SELECT_FROMID).toString();
					String strSelFromMtrlFunToName = new StringBuilder().append(CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_MATERIAL_FUNCTIONALITY).append(CONSTANT_STRING_SELECT_TONAME).toString();
					
					StringList slCoreMaterialSelect = new StringList();
					slCoreMaterialSelect.addElement(SELECT_ID);
					slCoreMaterialSelect.addElement(SELECT_ATTRIBUTE_PGRMPGCAS);
					slCoreMaterialSelect.addElement(SELECT_ATTRIBUTE_V_DESCRIPTION);
					slCoreMaterialSelect.addElement(SELECT_CURRENT);
					slCoreMaterialSelect.addElement(SELECT_ISLAST);
					slCoreMaterialSelect.addElement(SELECT_REVISION);
					slCoreMaterialSelect.addElement(strSelPgRelatedMaterialFromId);
					
					StringList slObjSelect = new StringList();
					slObjSelect.addElement(SELECT_ID);
					slObjSelect.addElement(SELECT_TYPE);
					slObjSelect.addElement(SELECT_NAME);
					slObjSelect.addElement(SELECT_REVISION);
														
					Iterator  materialListItr = mlStacking.iterator();
					while(materialListItr.hasNext())
					{				
						mpMaterial = (Map)materialListItr.next();
						bError = false;
						slErrorCodes = new StringList();
						slAllErrors = new StringList();
						
						strRMPGCAS = (String) mpMaterial.get(KEY_PLYMATERIAL);
						if(mpRMPGCASToId.containsKey(strRMPGCAS))
						{
							strRMPId = (String)mpRMPGCASToId.get(strRMPGCAS);
							mpMaterial.put(SELECT_ID, strRMPId);
						}
						else
						{
							mpRMP = new HashMap();
							mpRMP = getRMPIdForSync(context,strRMPGCAS);
							strRMPId = (String)mpRMP.get(SELECT_ID);					
							if(mpRMP.containsKey(STR_ERROR))
							{
								mpMaterial.put(STR_ERROR, (String)mpRMP.get(STR_ERROR));
								if(bIncludeAllErrors)
								{
									if(mpRMP.containsKey(STR_ERROR_CODE))
									{
										slErrorCodes.add((String)mpRMP.get(STR_ERROR_CODE));
										slAllErrors.add((String)mpRMP.get(STR_ERROR));
										mpMaterial.put(STR_ERROR_CODE, slErrorCodes);
										mpMaterial.put(STR_ERROR_LIST, slAllErrors);
									}									
								}
								bError = true;
								continue;
								
							} 
							else if(UIUtil.isNotNullAndNotEmpty(strRMPId))
							{					
								//Apollo 2018x.6 A10-771 - Starts
								strRMPRevision = (String)mpRMP.get(SELECT_REVISION);
								//Apollo 2018x.6 A10-771 - Ends		
								mpRMPGCASToId.put(strRMPGCAS, strRMPId);
								mpMaterial.put(SELECT_ID, strRMPId);
								doRMP = DomainObject.newInstance(context,strRMPId);
								bReadAccess = FrameworkUtil.hasAccess(context, doRMP , "read");	
								if(!bReadAccess)
								{	
									sbMessage = new StringBuffer();
									//Apollo 2018x.6 A10-771 - Starts
									sbMessage.append(STR_ERROR_RAWMATERIAL_NOREADACCESS.replace("<MATERIAL_NAME>", new StringBuilder(strRMPGCAS).append(CONSTANT_STRING_SPACE).append(strRMPRevision).toString()));
									//Apollo 2018x.6 A10-771 - Ends									
									mpMaterial.put(STR_ERROR, sbMessage.toString());
									if(bIncludeAllErrors)
									{
										slErrorCodes.add(STR_ERROR_CODE_E004);
										slAllErrors.add(sbMessage.toString());
										mpMaterial.put(STR_ERROR_CODE, slErrorCodes);
										mpMaterial.put(STR_ERROR_LIST, slAllErrors);
									}
									else
									{
										bError = true;
										continue;	
									}
								}
							} 
							else
							{								
								sbMessage = new StringBuffer();
								sbMessage.append(STR_ERROR_NORAWMATERIAFOUND.replace("<MATERIAL_NAME>", strRMPGCAS));									
								mpMaterial.put(STR_ERROR, sbMessage.toString());
								bError = true;
								if(bIncludeAllErrors)
								{
									slErrorCodes.add(STR_ERROR_CODE_E003);
									slAllErrors.add(sbMessage.toString());
									mpMaterial.put(STR_ERROR_CODE, slErrorCodes);
									mpMaterial.put(STR_ERROR_LIST, slAllErrors);
								}	
								continue;
							}
						}						
						if(mpMaterial.containsKey(KEY_APPLICATION))
						{
							bConsiderMFValidation = true;
						}						
						if(!bError && !slRMPId.contains(strRMPId))
						{
							slRMPId.addElement(strRMPId);
						}
					}					
					try
					{
						ContextUtil.pushContext(context,pgApolloConstants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
						bContextPushed = true;
						
						//Get RMP Details for all unique RMP Ids
						if(null != slRMPId && !slRMPId.isEmpty())
						{
							StringList slSelects = new StringList();
							slSelects.addElement(SELECT_ID);
							slSelects.addElement(SELECT_TYPE);
							slSelects.addElement(SELECT_NAME);
							slSelects.addElement(SELECT_CURRENT);
							slSelects.addElement(SELECT_REVISION);
							slSelects.addElement(SELECT_ATTRIBUTE_PGBASEUOM);							
							slSelects.addElement(SELECT_ATTRIBUTE_RELEASE_PHASE);
							slSelects.addElement(SELECT_ATTRIBUTE_EXPIRATION_DATE);
							slSelects.addElement(SELECT_ATTRIBUTE_PG_MATERIAL_RESTRICTION);			
							slSelects.addElement(SELECT_ATTRIBUTE_PG_MATERIAL_RESTRICTION_COMMENT);
							slSelects.addElement(SELECT_ATTRIBUTE_PG_ORIGINATINGSOURCE);
							if(bConsiderMFValidation)
							{							
								slSelects.addElement(SELECT_ATTRIBUTE_PG_MATERIAL_FUNCTION);
								slSelects.addElement(strSelFromMtrlFunToName);
							}
							mlRMPDetails = DomainObject.getInfo(context, (String[]) slRMPId.toArray(new String []{}), slSelects);
						}
						
						DomainObject doAPP = DomainObject.newInstance(context, strAPPObjectId);
						String strParentStageValue = doAPP.getInfo(context, SELECT_ATTRIBUTE_RELEASE_PHASE);
						String strAllowedStage = DomainConstants.EMPTY_STRING;
						if(UIUtil.isNotNullAndNotEmpty(strParentStageValue))
						{
							strAllowedStage = EnoviaResourceBundle.getProperty(context, "emxCPN.CreatePDBOM."+strParentStageValue+".AllwedStages");
						}
						else
						{
							strAllowedStage = STR_ERROR;
						}
						
						BusinessObject boPreviousRevision= doAPP.getPreviousRevision(context);
						if(null!=boPreviousRevision && boPreviousRevision.exists(context))
						{		
							DomainObject domPartObject = DomainObject.newInstance(context, boPreviousRevision);
							Pattern typePattern = new Pattern(pgApolloConstants.TYPE_RAW_MATERIAL);
							typePattern.addPattern(pgApolloConstants.TYPE_PG_RAW_MATERIAL);	
							//Check whether the APP is  connected to any Raw Material
							mlEBOMPreviousRev = domPartObject.getRelatedObjects(context,
									DomainConstants.RELATIONSHIP_EBOM,
									typePattern.getPattern(),
									slObjSelect,//Object Select
									null,//rel Select
									false,//get To
									true,//get From
									(short)1,//recurse level
									null,//where Clause
									null,//rel where clause
									0);	//limit
						}
						
						SimpleDateFormat formatter = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat());
						Calendar currentDate  = Calendar.getInstance();	
						String dateNow = formatter.format(currentDate.getTime());

						Map mpArgs = new HashMap();
						mpArgs.put(KEY_PREVIOUSEBOM, mlEBOMPreviousRev);
						mpArgs.put(KEY_RELPATTERN, DomainConstants.RELATIONSHIP_EBOM);
						MapList mlRelatedInfo = new MapList();
						StringList slMFCatia = new StringList();
						String strMF = EMPTY_STRING;
						StringList slRMPMF = new StringList();	
						String strMFLegacy = EMPTY_STRING;
						String strError = EMPTY_STRING;
						Map mpValidateMaterialRestriction = new HashMap();
						String strExpirationDate = EMPTY_STRING;
						Map mpTemp = new HashMap();
						String strRMPIDFromMap = EMPTY_STRING;
						String strRMPCurrentFromMap = EMPTY_STRING;
						String strRMPRelease = EMPTY_STRING;
						String strRMPName = EMPTY_STRING;
						//Apollo 2018x.6 A10-771 - Starts
						String strRMPNameRevision = EMPTY_STRING;
						//Apollo 2018x.6 A10-771 - Ends
												
						Iterator materialListItrNew = mlStacking.iterator();
						while(materialListItrNew.hasNext())
						{				
							mpMaterial = (Map) materialListItrNew.next();
							slErrorCodes = new StringList();
							slAllErrors = new StringList();
							if(bIncludeAllErrors)
							{
								if(mpMaterial.containsKey(STR_ERROR_CODE) && mpMaterial.containsKey(STR_ERROR_LIST))
								{
									slErrorCodes = (StringList)mpMaterial.get(STR_ERROR_CODE);
									slAllErrors = (StringList)mpMaterial.get(STR_ERROR_LIST);
								}								
							}
							else if(mpMaterial.containsKey(STR_ERROR))
							{
								continue;
							}			
							bError = false;
							
							strRMPId = (String) mpMaterial.get(SELECT_ID);
							
							if(null != mlRMPDetails && !mlRMPDetails.isEmpty())
							{
								for (int x=0 ; x< mlRMPDetails.size() ; x++)
								{
									mpTemp = new HashMap();
									mpTemp = (HashMap) mlRMPDetails.get(x);
									strRMPIDFromMap = EMPTY_STRING;
									strRMPCurrentFromMap = EMPTY_STRING;
									strRMPName = EMPTY_STRING;
									strRMPIDFromMap = (String) mpTemp.get(SELECT_ID);
									strRMPCurrentFromMap = (String) mpTemp.get(SELECT_CURRENT);
									strRMPName = (String) mpTemp.get(SELECT_NAME);
									//Apollo 2018x.6 A10-771 - Starts
									strRMPNameRevision = new StringBuilder(strRMPName).append(CONSTANT_STRING_SPACE).append((String) mpTemp.get(SELECT_REVISION)).toString();
									//Apollo 2018x.6 A10-771 - Ends
									if(strRMPIDFromMap.equalsIgnoreCase(strRMPId))
									{
										mpMaterial.put(SELECT_CURRENT, strRMPCurrentFromMap);
										strRMPRelease = EMPTY_STRING;
										strRMPRelease = (String) mpTemp.get(SELECT_ATTRIBUTE_RELEASE_PHASE);
										if(UIUtil.isNotNullAndNotEmpty(strRMPRelease) && !strAllowedStage.contains(strRMPRelease))
										{
											sbMessage = new StringBuffer();
											//Apollo 2018x.6 A10-771 - Starts
											sbMessage.append(STR_ERROR_RAWMATERIAL_RELEASEPHASENOTVALID.replace("<MATERIAL_NAME>", strRMPNameRevision).replace("<RELEASE_PHASE>", strRMPRelease));
											//Apollo 2018x.6 A10-771 - Ends
											mpMaterial.put(STR_ERROR, sbMessage.toString());
											if(bIncludeAllErrors)
											{
												slErrorCodes.add(STR_ERROR_CODE_E011);
												slAllErrors.add(sbMessage.toString());
											}
											else
											{
												bError = true;
											}
										}
										if(!bError)
										{
											strExpirationDate = EMPTY_STRING;
											strExpirationDate = (String) mpTemp.get(SELECT_ATTRIBUTE_EXPIRATION_DATE);
											if(UIUtil.isNotNullAndNotEmpty(strExpirationDate) && UIUtil.isNotNullAndNotEmpty(dateNow))
											{	
												if(((eMatrixDateFormat.getJavaDate(strExpirationDate)).compareTo(eMatrixDateFormat.getJavaDate(dateNow))) <= 0)
												{
													sbMessage = new StringBuffer();
													//Apollo 2018x.6 A10-771 - Starts
													sbMessage.append(STR_ERROR_RAWMATERIAL_EXPIRED.replace("<MATERIAL_NAME>", strRMPNameRevision));
													//Apollo 2018x.6 A10-771 - Ends
													mpMaterial.put(STR_ERROR, sbMessage.toString());
													if(bIncludeAllErrors)
													{
														slErrorCodes.add(STR_ERROR_CODE_E012);
														slAllErrors.add(sbMessage.toString());
													}
													else
													{
														bError = true;
													}
												}
											}
										}										
										if(!bError)
										{
											mlRelatedInfo = new MapList();
											mpTemp.put(DomainRelationship.SELECT_ID, EMPTY_STRING);
											mlRelatedInfo.add(mpTemp);
											mpArgs.put(KEY_RMPINFO, mlRelatedInfo);
											context.printTrace(pgApolloConstants.TRACE_LPD, "\n pgApolloCommonUtil : validateRMPDataForSync  mpArgs = "+mpArgs);
											mpValidateMaterialRestriction = new HashMap();
											mpValidateMaterialRestriction = (Map) JPO.invoke(context, "pgDSMParallelCloneUtil", null, "validateForMaterialRestriction", JPO.packArgs(mpArgs), Map.class);											
											strError = EMPTY_STRING;
											if(mpValidateMaterialRestriction.containsKey(STR_ERROR))
											{
												strError = (String) mpValidateMaterialRestriction.get(STR_ERROR);
											}
											if(UIUtil.isNotNullAndNotEmpty(strError) && (STR_BLOCK_FOR_ALL.equalsIgnoreCase(strError) || STR_BLOCK_FOR_NEW.equalsIgnoreCase(strError)))
											{
												sbMessage = new StringBuffer();
												//Apollo 2018x.6 A10-771 - Starts
												sbMessage.append(STR_ERROR_RAWMATERIAL_RESTRICTED.replace("<MATERIAL_NAME>", strRMPNameRevision));
												//Apollo 2018x.6 A10-771 - Ends
												mpMaterial.put(STR_ERROR, sbMessage.toString());
												if(bIncludeAllErrors)
												{
													if(STR_BLOCK_FOR_NEW.equalsIgnoreCase(strError))
													{
														slErrorCodes.add(STR_ERROR_CODE_E013_01);
													}
													else
													{
														slErrorCodes.add(STR_ERROR_CODE_E013_02);
													}
													slAllErrors.add(sbMessage.toString());
												}
												else
												{
													bError = true;
												}
											}
											else if(UIUtil.isNotNullAndNotEmpty(strError) && (STR_BLOCK_FOR_EMPTY_WARNING.equalsIgnoreCase(strError)))
											{
												sbMessage = new StringBuffer();
												//Apollo 2018x.6 A10-771 - Starts
												sbMessage.append(STR_ERROR_RAWMATERIAL_RESTRICTED_EMPTY_WARNING.replace("<MATERIAL_NAME>", strRMPNameRevision));
												//Apollo 2018x.6 A10-771 - Ends
												mpMaterial.put(STR_ERROR, sbMessage.toString());
												if(bIncludeAllErrors)
												{
													slErrorCodes.add(STR_ERROR_CODE_E014);
													slAllErrors.add(sbMessage.toString());
												}
												else
												{
													bError = true;
												}
											}
											//Apollo 2018x.5 Fix for ALM Defect 35237 - Starts
											else if (!bIgnoreMtrilRestrictionWarning && mpValidateMaterialRestriction.containsKey(pgApolloConstants.KEY_WARNING_MESSAGE))
											//Apollo 2018x.5 Fix for ALM Defect 35237 - Ends
											{
												mpMaterial.put(STR_WARNING, (String) mpTemp.get(SELECT_ATTRIBUTE_PG_MATERIAL_RESTRICTION_COMMENT));
												if(bIncludeAllErrors)
												{
													slErrorCodes.add(STR_ERROR_CODE_E015);
													slAllErrors.add((String) mpTemp.get(SELECT_ATTRIBUTE_PG_MATERIAL_RESTRICTION_COMMENT));
												}												
											}
										}																		
										
										if(!bError && (mpMaterial.containsKey(KEY_COREMATERIAL) || mpMaterial.containsKey(KEY_APPLICATION)))
										{		
												strErrorCode = EMPTY_STRING;
												strLayerName = EMPTY_STRING;
												if(mpMaterial.containsKey(ATTRIBUTE_PLY_NAME))
												{
													strLayerName = (String)mpMaterial.get(pgApolloConstants.ATTRIBUTE_PLY_NAME);
												}
												//Get Core Material Object Ids
												if(mpMaterial.containsKey(KEY_COREMATERIAL) && validateCoreMaterial)
												{
													strLaminateFunction = EMPTY_STRING;
													strCMNameRev = (String) mpMaterial.get(KEY_COREMATERIAL);
													slCM = new StringList();
													slCM = StringUtil.split(strCMNameRev, CONSTANT_STRING_PIPE);									
													if(mpMaterial.containsKey(KEY_LAMINATEFUNCTION))
													{
														strLaminateFunction = (String) mpMaterial.get(KEY_LAMINATEFUNCTION);
													}
													
													if(null != slCM && !slCM.isEmpty() && slCM.size() == 2)
													{							
														mpCM = new HashMap();
														strRevision = slCM.get(1).trim();
														mpCM = (HashMap)getLatestRevisionMap(context, TYPE_DSC_MATREF_REF_CORE, slCM.get(0).trim(), EMPTY_STRING, slCoreMaterialSelect);
														context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - strRMPNameRevision = "+strRMPNameRevision);
														
														context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - mpCM = "+mpCM);
														if(null != mpCM && !mpCM.isEmpty()){
																strCMRMPGCAS = EMPTY_STRING;
																strLatestRevision = EMPTY_STRING;
																strCurrent = EMPTY_STRING;
																strCMDescription = EMPTY_STRING;										
																objConnectedRMP = null;
																strCMRMPGCAS = (String) mpCM.get(SELECT_ATTRIBUTE_PGRMPGCAS);
																strLatestRevision = (String) mpCM.get(SELECT_REVISION);
																strCurrent = (String) mpCM.get(SELECT_CURRENT);
																strCMDescription = (String) mpCM.get(SELECT_ATTRIBUTE_V_DESCRIPTION);											
																objConnectedRMP = mpCM.get(strSelPgRelatedMaterialFromId);
																context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - objConnectedRMP = "+objConnectedRMP);

																if (!strLatestRevision.equalsIgnoreCase(strRevision))
																{
																	if(UIUtil.isNotNullAndNotEmpty(strLayerName))
																	{
																		sbMessage = new StringBuffer();
																		sbMessage.append(STR_ERROR_COREMATERIAL_NOTLATESTREV_FOR_LAYER.replace("<CORE_MATERIAL>", strCMNameRev).replace("<LAYER_NAME>",strLayerName));																		
																	}
																	else
																	{
																		sbMessage = new StringBuffer();
																		sbMessage.append(STR_ERROR_COREMATERIAL_NOTLATESTREV.replace("<CORE_MATERIAL>", strCMNameRev));																		
																	}
																	strErrorCode = STR_ERROR_CODE_E006;
																	mpMaterial.put(STR_ERROR, sbMessage.toString());
																	if(bIncludeAllErrors)
																	{
																		slErrorCodes.add(strErrorCode);
																		slAllErrors.add(sbMessage.toString());
																	}
																	else
																	{
																		bError = true;	
																	}
																}
																else
																{												
																	if (!STATE_RELEASE.equalsIgnoreCase(strCurrent) && !STATE_RELEASED.equalsIgnoreCase(strCurrent) && !bIgnoreErrorForApplyMaterial)
																	{
																		if(UIUtil.isNotNullAndNotEmpty(strLayerName))
																		{
																			sbMessage = new StringBuffer();
																			sbMessage.append(STR_ERROR_COREMATERIAL_NOTRELEASEDREV_FOR_LAYER.replace("<CORE_MATERIAL>", strCMNameRev).replace("<LAYER_NAME>",strLayerName));
																		}
																		else
																		{
																			sbMessage = new StringBuffer();
																			sbMessage.append(STR_ERROR_COREMATERIAL_NOTRELEASEDREV.replace("<CORE_MATERIAL>", strCMNameRev));																			
																		}
																		strErrorCode = STR_ERROR_CODE_E007;
																		mpMaterial.put(STR_ERROR, sbMessage.toString());													
																		if(bIncludeAllErrors)
																		{
																			slErrorCodes.add(strErrorCode);
																			slAllErrors.add(sbMessage.toString());
																		}
																		else
																		{
																			bError = true;
																		}
																	}
																	
																	if(!bError && (UIUtil.isNullOrEmpty(strCMRMPGCAS) || !strCMRMPGCAS.equals(strRMPName)))
																	{
																		sbMessage = new StringBuffer();
																		//Apollo 2018x.6 A10-771 - Starts
																		sbMessage.append(STR_ERROR_COREMATERIAL_NOTASSOCIATEDWITHRMP.replace("<MATERIAL_NAME>", strRMPNameRevision).replace("<CORE_MATERIAL>",strCMNameRev));
																		//Apollo 2018x.6 A10-771 - Ends																									
																		mpMaterial.put(STR_ERROR, sbMessage.toString());
																		if(bIncludeAllErrors)
																		{
																			slErrorCodes.add(STR_ERROR_CODE_E009);
																			slAllErrors.add(sbMessage.toString());
																		}
																		else
																		{
																			bError = true;																			
																		}
																	}
																	
																	if (!bError && (!strCMDescription.equalsIgnoreCase(strLaminateFunction)))
																	{
																		if(UIUtil.isNotNullAndNotEmpty(strLayerName))
																		{
																			sbMessage = new StringBuffer();
																			sbMessage.append(STR_ERROR_COREMATERIAL_NOTVALIDFORLAMINATEFUNCTION_FOR_LAYER.replace("<CORE_MATERIAL>", strCMNameRev).replace("<LAMINATE_FUNCTION>", strLaminateFunction).replace("<LAYER_NAME>",strLayerName));
																		}
																		else
																		{
																			sbMessage = new StringBuffer();
																			sbMessage.append(STR_ERROR_COREMATERIAL_NOTVALIDFORLAMINATEFUNCTION.replace("<CORE_MATERIAL>", strCMNameRev).replace("<LAMINATE_FUNCTION>", strLaminateFunction));																			
																		}	
																		strErrorCode = STR_ERROR_CODE_E008;
																		mpMaterial.put(STR_ERROR, sbMessage.toString());
																		if(bIncludeAllErrors)
																		{
																			slErrorCodes.add(strErrorCode);
																			slAllErrors.add(sbMessage.toString());
																		}
																		else
																		{
																			bError = true;
																		}
																	}
																	
																	if(!bError && !bIgnoreErrorForApplyMaterial)
																	{
																		context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - objConnectedRMP 2 = "+objConnectedRMP);

																		if(null == objConnectedRMP)
																		{
																			sbMessage = new StringBuffer();
																			//Apollo 2018x.6 A10-771 - Starts
																			sbMessage.append(STR_ERROR_COREMATERIAL_NOTCONNECTEDTORMP.replace("<MATERIAL_NAME>", strRMPNameRevision).replace("<CORE_MATERIAL>",strCMNameRev));
																			//Apollo 2018x.6 A10-771 - Ends
																			mpMaterial.put(STR_ERROR, sbMessage.toString());
																			if(bIncludeAllErrors)
																			{
																				slErrorCodes.add(STR_ERROR_CODE_E005);
																				slAllErrors.add(sbMessage.toString());
																			}
																			else
																			{
																				bError = true;																					
																			}
																		}
																		else
																		{
																			slConnectedRMP = new StringList();
																			slConnectedRMP = getStringListMultiValue(objConnectedRMP);
																			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - slConnectedRMP  = "+slConnectedRMP);
																			if(null == slConnectedRMP || slConnectedRMP.isEmpty() || !slConnectedRMP.contains(strRMPId))
																			{
																				sbMessage = new StringBuffer();
																				//Apollo 2018x.6 A10-771 - Starts
																				sbMessage.append(STR_ERROR_COREMATERIAL_NOTCONNECTEDTORMP.replace("<MATERIAL_NAME>", strRMPNameRevision).replace("<CORE_MATERIAL>",strCMNameRev));
																				//Apollo 2018x.6 A10-771 - Ends
																				mpMaterial.put(STR_ERROR, sbMessage.toString());
																				if(bIncludeAllErrors)
																				{
																					slErrorCodes.add(STR_ERROR_CODE_E005);
																					slAllErrors.add(sbMessage.toString());
																				}
																				else
																				{
																					bError = true;																						
																				}
																			}
																		}
																	}
																}												
														}
														else
														{
															if(UIUtil.isNotNullAndNotEmpty(strLayerName))
															{
																sbMessage = new StringBuffer();
																sbMessage.append(STR_ERROR_COREMATERIAL_NOTFOUND_FOR_LAYER.replace("<CORE_MATERIAL>", strCMNameRev).replace("<LAYER_NAME>",strLayerName));
															}
															else
															{
																sbMessage = new StringBuffer();
																sbMessage.append(STR_ERROR_COREMATERIAL_NOTFOUND.replace("<CORE_MATERIAL>", strCMNameRev));
															}
															mpMaterial.put(STR_ERROR, sbMessage.toString());
															if(bIncludeAllErrors)
															{
																slErrorCodes.add(STR_ERROR_CODE_E010);
																slAllErrors.add(sbMessage.toString());
															}
															else
															{
																bError = true;																
															}
														}
													}
													else
													{
														if(UIUtil.isNotNullAndNotEmpty(strLayerName))
														{
															sbMessage = new StringBuffer();
															sbMessage.append(STR_ERROR_COREMATERIAL_NOTFOUND_FOR_LAYER.replace("<CORE_MATERIAL>", strCMNameRev).replace("<LAYER_NAME>",strLayerName));
														}
														else
														{
															sbMessage = new StringBuffer();
															sbMessage.append(STR_ERROR_COREMATERIAL_NOTFOUND.replace("<CORE_MATERIAL>", strCMNameRev));
														}
														mpMaterial.put(STR_ERROR, sbMessage.toString());
														if(bIncludeAllErrors)
														{
															slErrorCodes.add(STR_ERROR_CODE_E010);
															slAllErrors.add(sbMessage.toString());
														}
														else
														{
															bError = true;															
														}
													}
												}	
												
												//Get Application objects and physical ids
												if(!bError && mpMaterial.containsKey(KEY_APPLICATION))
												{		
													context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - For MF strRMPNameRevision  = "+strRMPNameRevision);
													//Get Material Function from RMP
													slRMPMF = new StringList();	
													strMFLegacy = EMPTY_STRING;
													context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - mpTemp  = "+mpTemp);
													if(mpTemp.containsKey(strSelFromMtrlFunToName))
													{
														slRMPMF = getStringListMultiValue(mpTemp.get(strSelFromMtrlFunToName) );	
													}
													context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPDataForSync - slRMPMF  = "+slRMPMF);
													if(slRMPMF.isEmpty() && !RANGE_DSO.equalsIgnoreCase((String)mpTemp.get(SELECT_ATTRIBUTE_PG_ORIGINATINGSOURCE)))
													{
														strMFLegacy = (String)mpTemp.get(SELECT_ATTRIBUTE_PG_MATERIAL_FUNCTION);
													}
													
													slProcessMFList = new StringList();
													slProcessMFList = StringUtil.split((String)mpMaterial.get(KEY_APPLICATION), CONSTANT_STRING_COMMA);
													slMFPhysicalIdList = new StringList();
													
													if(null != slProcessMFList && !slProcessMFList.isEmpty())
													{
														for(String strMFName : slProcessMFList)
														{
															mpMF = new HashMap();
															mpMF = (HashMap) getObjectIdWithSelects (context, TYPE_PLI_MATERIALFUNCTION, strMFName.trim(), CONSTANT_STRING_HYPHEN, slSelectsMF);
															if(null != mpMF && !mpMF.isEmpty())
															{
																strPhysicalId = EMPTY_STRING;
																strPhysicalId = (String) mpMF.get(SELECT_PHYSICAL_ID);									
																slMFPhysicalIdList.addElement(strPhysicalId);
															}
															else
															{
																sbMessage = new StringBuffer();
																//Apollo 2018x.6 A10-771 - Starts
																sbMessage.append(STR_ERROR_MATERIALFUNCTION_NOTFOUND.replace("<MATERIAL_FUNCTION>", strMFName).replace("<MATERIAL_NAME>", strRMPNameRevision));
																//Apollo 2018x.6 A10-771 - Ends
																mpMaterial.put(STR_ERROR, sbMessage.toString());
																if(bIncludeAllErrors)
																{
																	slErrorCodes.add(STR_ERROR_CODE_E016);
																	slAllErrors.add(sbMessage.toString());
																}
																bError = true;
																continue;
															}															
															if(!(slRMPMF.contains(strMFName) || strMFLegacy.contains(strMFName)))
															{
																sbMessage = new StringBuffer();
																//Apollo 2018x.6 A10-771 - Starts
																sbMessage.append(STR_ERROR_MATERIALFUNCTION_NOTFOUND.replace("<MATERIAL_FUNCTION>", strMFName).replace("<MATERIAL_NAME>", strRMPNameRevision));												
																//Apollo 2018x.6 A10-771 - Ends
																mpMaterial.put(STR_ERROR, sbMessage.toString());
																if(bIncludeAllErrors)
																{
																	slErrorCodes.add(STR_ERROR_CODE_E016);
																	slAllErrors.add(sbMessage.toString());
																}
																bError = true;																
															}
														}														
														if(!bError && null != slMFPhysicalIdList && !slMFPhysicalIdList.isEmpty())
														{
															mpMaterial.put(KEY_APPLICATION, StringUtil.join(slMFPhysicalIdList,CONSTANT_STRING_COMMA));
														}
													}
												}						
										}
										if(!bError)
										{											
											mpMaterial.put(ATTRIBUTE_PGBASEUOM, (String) mpTemp.get(SELECT_ATTRIBUTE_PGBASEUOM));												
										}	
										if(null!=slErrorCodes && !slErrorCodes.isEmpty())
										{
											mpMaterial.put(STR_ERROR_CODE, slErrorCodes);
											mpMaterial.put(STR_ERROR_LIST, slAllErrors);
										}
									}									
								}
							}
						}
					}
					catch(Exception ex)
					{
						loggerTrace.error(ex.getMessage(), ex);
						throw ex;
					}
					finally
					{
						if(bContextPushed)
						{
							ContextUtil.popContext(context);
							bContextPushed = false;
						}
					}
				}
			}
			catch(Exception ex)
			{
				loggerTrace.error(ex.getMessage(), ex);
				throw ex;
			}
			return mlStacking;			
		}
		
		/**
		 * This method will return latest revision object details by passing type, name and where condition
		 * @param context
		 * @param strType
		 * @param strName
		 * @param strWhereCondition
		 * @return Map - it holds details of latest revision object
		 * @throws Exception
		 */
		public static Map getLatestRevisionMap(matrix.db.Context context , String strType, String strName, String strWhereCondition, StringList slObjSelect)throws Exception
		{
			Map mapReturn = new HashMap();
			slObjSelect.addElement(DomainConstants.SELECT_REVISION);
			slObjSelect.addElement(DomainConstants.SELECT_ID);
			MapList mlTemp =  DomainObject.findObjects(context, strType, strName, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD, strWhereCondition, false, slObjSelect);
			if(null != mlTemp && !mlTemp.isEmpty())
			{
				mlTemp.sort(DomainConstants.SELECT_REVISION, STR_DESCENDING, STR_STRING); //sorting revision in descending order
				mapReturn = (Map) mlTemp.get(0) ;
			}		
			return mapReturn;
		}
		
		/**
		 * This method will return latest revision object details by passing type, name and where condition
		 * @param context
		 * @param sObjectType
		 * @param sObjectName
		 * @param sObjectRevision
		 * @param strWhereCondition
		 * @param slObjSelect
		 * @return
		 * @throws Exception
		 */
		public static Map getLatestRevisionMap(matrix.db.Context context , String sObjectType, String sObjectName, String sObjectRevision, String strWhereCondition, StringList slObjSelect)throws Exception
		{
			Map mapReturn = new HashMap();
			slObjSelect.add(DomainConstants.SELECT_REVISION);
			slObjSelect.add(DomainConstants.SELECT_ID);
			
			//Fetch object info
			MapList mlTemp	 =  DomainObject.findObjects(context, // context
												sObjectType, // type pattern
												sObjectName, // name pattern
												sObjectRevision, // revision pattern	
												DomainConstants.QUERY_WILDCARD, // owner pattern	
												pgApolloConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern	
												strWhereCondition, // where expression
												false, // expand type
												slObjSelect); // Object Selectables
			
			if(null != mlTemp && !mlTemp.isEmpty())
			{
				mlTemp.sort(DomainConstants.SELECT_ORIGINATED, STR_DESCENDING, "date"); //sorting revision in descending order
				mapReturn = (Map) mlTemp.get(0) ;
			}		
			return mapReturn;
		}
		
		/**
		 * Method to check string in list for case insensitive
		 * @param str
		 * @param list
		 * @return
		 */
		public static boolean containsInListCaseInsensitive(String str, List<String> list){
			if(null!=list && !list.isEmpty())
			{
				for (String string : list){
					if (string.equalsIgnoreCase(str)){
						return true;
					}
				}
			}
		    return false;
		  }
		
		
		
	
		/**
		 * Method to check whether given Physical Product or Enterprise Part is resolved item
		 * @param context
		 * @param strObjectId
		 * @param checkComponentFamilyAssociation
		 * @return
		 * @throws MatrixException
		 */
		public static boolean isResolvedItem(Context context, String strObjectId, boolean checkComponentFamilyAssociation) throws MatrixException
		{  
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem >> strObjectId = "+ strObjectId);
			boolean isResolvedItem = false;			
			if(UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				DomainObject domObj = DomainObject.newInstance(context, strObjectId);
				String strType = domObj.getInfo(context, DomainConstants.SELECT_TYPE);
				DomainObject domVPMRefObj = null;	
				String strVPMRefernceId;
				if(pgApolloConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strType))
				{
					domVPMRefObj = domObj;
				}
				else
				{
					strVPMRefernceId = fetchVPMReferenceObject(context, domObj, null);
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem - strVPMRefernceId = "+ strVPMRefernceId);
					if(UIUtil.isNotNullAndNotEmpty(strVPMRefernceId))
					{
						domVPMRefObj = DomainObject.newInstance(context, strVPMRefernceId);
					}
				}			
				if(null != domVPMRefObj)
				{		
					String strContextObjectPhysicalId;
					String strPathOwnerType = pgApolloConstants.TYPE_CATCOMPONENTSFAMILYPROXYTOELEMENT;	
					String strSemanticRole = pgApolloConstants.CONST_SEMANTICROLE_CFY_ITEMREFERENCE;
					StringList busSelects = new StringList();
					busSelects.add(pgApolloConstants.SELECT_INTERFACE_CATCOMPONENTSFAMILYEXTENSIONONELEMENT);				
					Map mapVPMRefObject = domVPMRefObj.getInfo(context, busSelects);
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem - mapVPMRefObject = "+ mapVPMRefObject);
					if(mapVPMRefObject.containsKey(pgApolloConstants.SELECT_INTERFACE_CATCOMPONENTSFAMILYEXTENSIONONELEMENT))
					{
						String strIsResolvedItem = (String)mapVPMRefObject.get(pgApolloConstants.SELECT_INTERFACE_CATCOMPONENTSFAMILYEXTENSIONONELEMENT);
						StringList slComponentFamilyIdList = new StringList();
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem - strIsResolvedItem = "+ strIsResolvedItem);
						if(pgApolloConstants.STR_TRUE_FLAG_CAPS.equalsIgnoreCase(strIsResolvedItem))
						{	
							if(checkComponentFamilyAssociation)
							{
								strContextObjectPhysicalId = domVPMRefObj.getInfo(context, pgApolloConstants.SELECT_PHYSICAL_ID);
								context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem - strContextObjectPhysicalId = "+ strContextObjectPhysicalId);
								if(UIUtil.isNotNullAndNotEmpty(strContextObjectPhysicalId))
								{
									StringList slObjectSelect = new StringList();
									slObjectSelect.add("owner.to["+pgApolloConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER+"].from.id");
									List <String> lList = new ArrayList<>(1);
									lList.add(strContextObjectPhysicalId); // Physical Id of VPMReferece

									PathQuery pathQuery = new PathQuery();
									pathQuery.setPathType("SemanticRelation") ;
									pathQuery.setCriterion(PathQuery.ENDS_WITH_ANY, lList);
									pathQuery.setWhereExpression("attribute[RoleSemantics]=='"+strSemanticRole+"' && owner.type=='"+strPathOwnerType+"'");
									PathQueryIterator  pathQueryIter  =  pathQuery.getIterator(context, slObjectSelect, (short)0, null, false);
									StringList slComponentFamilyIds;
									Iterator iterquerySR = pathQueryIter.iterator();
									PathWithSelect pathsSR = null;
									while(iterquerySR.hasNext())
									{
										pathsSR = (PathWithSelect) iterquerySR.next();
										slComponentFamilyIds = pathsSR.getSelectDataList("owner.to["+pgApolloConstants.RELATIONSHIP_VPLMREL_PLMCONNECTION_V_OWNER+"].from.id");
										if(null!=slComponentFamilyIds)
										{
											slComponentFamilyIdList.addAll(slComponentFamilyIds);
										}
									}
									pathQueryIter.close();
									context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem - slComponentFamilyIdList = "+ slComponentFamilyIdList);
									if(!slComponentFamilyIdList.isEmpty())
									{	
										isResolvedItem = true;
									}
								}
							}
							else
							{
								isResolvedItem = true;
							}
							context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem - isResolvedItem = "+ isResolvedItem);
						}
					}	
				}	
				
			}	
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : isResolvedItem << isResolvedItem = "+ isResolvedItem);
			return isResolvedItem;
		}
		
		
		/**
		 * Method to transfer control between VPM and Matrix
		 * @param context
		 * @param strObjectId
		 * @param strTransferControlAttributeValue
		 * @param bPushContextRequired
		 * @return
		 * @throws MatrixException
		 */
		public static void transferDesignControl(Context context, String strObjectId, String strTransferControlAttributeValue, boolean bPushContextRequired) throws MatrixException
		{  
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : transferDesignControl >> strObjectId = "+ strObjectId);
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : transferDesignControl - strTransferControlAttributeValue = "+ strTransferControlAttributeValue);
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : transferDesignControl - bPushContextRequired = "+ bPushContextRequired);

			boolean isContextPushed = false;
			try
			{
				if(UIUtil.isNotNullAndNotEmpty(strObjectId))
				{
					DomainObject domObj = DomainObject.newInstance(context, strObjectId);
					String strType = domObj.getInfo(context, DomainConstants.SELECT_TYPE);
					DomainObject domVPMRefObj = null;	
					String strVPMRefernceId;
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : transferDesignControl - strType = "+ strType);
					if(pgApolloConstants.TYPE_VPMREFERENCE.equalsIgnoreCase(strType))
					{
						domVPMRefObj = domObj;
					}
					else
					{
						strVPMRefernceId = fetchVPMReferenceObject(context, domObj, null);
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : transferDesignControl - strVPMRefernceId = "+ strVPMRefernceId);
						if(UIUtil.isNotNullAndNotEmpty(strVPMRefernceId))
						{
							domVPMRefObj = DomainObject.newInstance(context, strVPMRefernceId);
						}
					}					
					if(null != domVPMRefObj)
					{
						if(bPushContextRequired)
						{
							//Context user will not always have access to transfer control between ENOVIA and CATIA. So we need to add Push Context to provide necessary access to context user.
							ContextUtil.pushContext(context, pgApolloConstants.PERSON_USER_AGENT, null, context.getVault().getName());
							isContextPushed = true;
						}
						domVPMRefObj.setAttributeValue(context, pgApolloConstants.ATTRIBUTE_ISVPLMCONTROLLED, strTransferControlAttributeValue);
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : transferDesignControl - strTransferControlAttributeValue = "+ strTransferControlAttributeValue);
					}					
				}				
			}
			catch(MatrixException e)
			{
				VPLMIntegTraceUtil.trace(context, e.getMessage());
				throw e;
			}
			finally
			{
				if(isContextPushed)
				{
					ContextUtil.popContext(context);
					isContextPushed = false;
				}			
			}
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : transferDesignControl << ");
		}	
		
		/**
		 * 
		 * Method to fetch VPMReference Object associated with enterprise part
		 * @param context
		 * @param domEnterPrisePartObject
		 * @param whereClause
		 * @return
		 * @throws MatrixException 
		 */
		public static String fetchVPMReferenceObject(matrix.db.Context context, DomainObject domEnterPrisePartObject, String whereClause) throws MatrixException 
		{		
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : fetchVPMReferenceObject >> whereClause = "+ whereClause);
			StringList objectSelects = new StringList();
			objectSelects.add(DomainConstants.SELECT_ID);		
			String strVPMReferenceObjectId = DomainConstants.EMPTY_STRING;			
			//Get Associated VPMReference
			MapList mlRelatedPhysicalProduct = domEnterPrisePartObject.getRelatedObjects(context, // Matrix Context
					DomainConstants.RELATIONSHIP_PART_SPECIFICATION, // Relationship Pattern
					pgApolloConstants.TYPE_VPMREFERENCE, // Type Pattern
					objectSelects,//Object Select
					null,//rel Select
					false,//get To
					true,//get From
					(short)1,//recurse level
					whereClause,//where Clause
					null, // rel where clause
					0);	// Object limit
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : fetchVPMReferenceObject - mlRelatedPhysicalProduct = "+ mlRelatedPhysicalProduct);
			if(null != mlRelatedPhysicalProduct && !mlRelatedPhysicalProduct.isEmpty()) 
			{
				Map mpRelatedPhysicalProduct = (Map)mlRelatedPhysicalProduct.get(0);
				strVPMReferenceObjectId = (String)mpRelatedPhysicalProduct.get(DomainConstants.SELECT_ID);
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : fetchVPMReferenceObject - mpRelatedPhysicalProduct = "+ mpRelatedPhysicalProduct);
			}
			context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : fetchVPMReferenceObject << strVPMReferenceObjectId = "+ strVPMReferenceObjectId);
			return strVPMReferenceObjectId;
		}
		
		/**
		 * This method will be used to Skip Apollo Trigger
		 * @param context
		 * @param strMethod
		 * @return
		 * @throws Exception
		 */
		public static boolean skipTrigger(Context context , String strMethod)throws Exception{
			boolean bReturn= false;
			 String strTypepgConfigurationAdmin =TYPE_PGCONFIGURATIONADMIN;
			 String strObjectId =getObjectId(context, strTypepgConfigurationAdmin, APOLLO_TRIGGER_CONFIG_OBJ_NAME, strMethod);
			 if(UIUtil.isNotNullAndNotEmpty(strObjectId))
			 {
				 DomainObject domTriggerConfigObject = DomainObject.newInstance(context, strObjectId);
				 String sCurrent = domTriggerConfigObject.getInfo(context, DomainConstants.SELECT_CURRENT);
				 if(STATE_ACTIVE.equalsIgnoreCase(sCurrent))
				 {
					 bReturn=true;
				 }
			 }			 
			return bReturn;
		}		

		
		/**
		 * Method to extract Value from Parameter
		 * @param strInputValue
		 * @return
		 */
		public static String extractValueFromParameter(String strInputValue)
		{
	    	if(UIUtil.isNotNullAndNotEmpty(strInputValue))
	    	{
	    		int iFirstCharPosition = findFirstLetterPosition(strInputValue);
	    		if(iFirstCharPosition > 0)
	    		{
	    			strInputValue = strInputValue.substring(0, iFirstCharPosition);
	    		}
	    	}
			return strInputValue;
		}
	    
	    /**
	     * Method to find first alphabetic character in value so to find UOM
	     * @param strInputValue
	     * @return
	     */
	    public static int findFirstLetterPosition(String strInputValue) {
	    	char sChar;
	    	char charPercentage = '%';
	        for (int i = 0; i < strInputValue.length(); i++) {
	        	sChar = strInputValue.charAt(i);
	            if (Character.isAlphabetic(strInputValue.charAt(i)) || (Character.compare(charPercentage, sChar) == 0) ) {
	                return i;
	            }
	        }
	        return -1; // not found
	    }
	    
	    
	    /**
		 * Method to download Design Parameter File
		 * @param context
		 * @param domObject
		 * @param sWorkspacePath 
		 * @return
		 * @throws MatrixException
		 */
		public static String downloadDesignParameterFile(matrix.db.Context context, DomainObject domObject, String sWorkspacePath) throws MatrixException 
		{
			
		    String sAPPObjectName = domObject.getInfo(context, DomainConstants.SELECT_NAME);
			StringBuilder sbFileNameDP = new StringBuilder();
			sbFileNameDP.append(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME).append(pgApolloConstants.CONSTANT_STRING_UNDERSCORE).append(sAPPObjectName).append(pgApolloConstants.STR_XMLFILE_EXTENSION);
			String sFileNameDP = sbFileNameDP.toString();
			
			FileList files = domObject.getFiles(context, pgApolloConstants.FORMAT_NOT_RENDERABLE);
			String strFileName;
			StringBuilder sFileOutput = new StringBuilder();
			
			boolean bFileFound = false;
			boolean bFileCheckoutError = false;
			
			if(files!=null && !files.isEmpty())
			{
				for(matrix.db.File f : files)
				{
					strFileName = f.getName();
					if(UIUtil.isNotNullAndNotEmpty(strFileName) && strFileName.equals(sFileNameDP))
					{
						bFileFound = true;
						try
						{
							domObject.checkoutFile(context, false, pgApolloConstants.FORMAT_NOT_RENDERABLE, strFileName, sWorkspacePath);
						}
						catch(Exception ex)
						{
							bFileCheckoutError = true;						
							sFileOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR_FILE_CHECKOUT).append(ex.getLocalizedMessage());
						}
						break;
					}
				}			
			}
			
			if(!bFileFound)
			{
				sFileOutput.append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_DESIGN_PARAMETER_NOT_PUBLISHED);
			}
			if(bFileFound && !bFileCheckoutError)
			{
				sFileOutput.append(sFileNameDP).toString();
			}
			return sFileOutput.toString();
		}
		
		
		/**
		 * Method to upload Design Parameter File
		 * @param context
		 * @param domObject
		 * @param sWorkspacePath 
		 * @param sWorkspacePath2 
		 * @return
		 * @throws MatrixException
		 * @throws IOException 
		 */
		public static void uploadDesignParameterFile(matrix.db.Context context, DomainObject domObject, String sDesignParamFileName, String sWorkspacePath) throws MatrixException, IOException 
		{
			if(UIUtil.isNotNullAndNotEmpty(sWorkspacePath) && UIUtil.isNotNullAndNotEmpty(sDesignParamFileName) && !sDesignParamFileName.contains(pgApolloConstants.STR_ERROR))
			{
				StringBuilder sbUpdateFileName = new StringBuilder();
				sbUpdateFileName.append(sWorkspacePath).append(File.separator).append(sDesignParamFileName);
				File file = new File(sbUpdateFileName.toString());
				if (!file.exists()) 
				{
					return;
				}
				domObject.checkinFile(context, false, true, "" , pgApolloConstants.FORMAT_NOT_RENDERABLE, sDesignParamFileName, sWorkspacePath );
				// create object of Path
		        Path path = Paths.get(sbUpdateFileName.toString());
		        // delete path
				Files.delete(path);
			}
		}
		
		/**
		 * This method would be invoked from web service for Automation
		 * It is used to get the value of particular preference from user properties
		 * @param context
		 * @param String preference name
		 * @return String preference value
		 * @throws FrameworkException
		 */
		public static String getPreferenceValue(Context context, String sPreferenceName) throws FrameworkException {
			String sPreferenceValue=PropertyUtil.getAdminProperty(context, DomainConstants.TYPE_PERSON, context.getUser(), sPreferenceName);
			if(UIUtil.isNullOrEmpty(sPreferenceValue))
			{
				sPreferenceValue= DomainConstants.TYPE_PERSON;
			}
			return sPreferenceValue;
		}
		
		/**
		 * This method would be invoked from web service
		 * It is used to get the value of particular preference from user properties
		 * @param context
		 * @param String preference name
		 * @return String preference value
		 * @throws FrameworkException
		 */
		public static String getPreferenceValue(Context context, String sUserName, String sPreferenceName) throws FrameworkException {
			String sPropertySelect = new StringBuilder("property[").append(sPreferenceName).append("].").append("value").toString();
			String[] sInputArray = new String[]{sUserName, sPropertySelect, ""};
			//MQL Call is required, as OOTB API fetch property value from JVM Cache, Automation need value from Database directly
			String sPreferenceValue = MqlUtil.mqlCommand(context, "print person $1 select $2 dump $3" , sInputArray);
			if(UIUtil.isNullOrEmpty(sPreferenceValue))
			{
				sPreferenceValue= DomainConstants.EMPTY_STRING;
			}
			return sPreferenceValue;
		}
    
    	/**
		 * Method to prepare map name
		 * @param _returnMap
		 * @return
		 */
		public static Map prepareMapForName(Map<String, StringList> returnMap) 
		{
			HashMap<String,String> mpDisplayName = new HashMap();
			StringList slActual = returnMap.get("field_choices");
			StringList slDisplay = returnMap.get("field_display_choices"); 
			for(int i = 0 ; i < slActual.size(); i++ ) {
				mpDisplayName.put(slActual.get(i),slDisplay.get(i) );
			}
			return mpDisplayName;
		}
		
		
		/**
		 * This method matches 2 string values and returns true if the values are same. 
		 * @param strVal1
		 * @param strVal2
		 * @return
		 * @throws Exception
		 */
		public static boolean isMatch(String strVal1, String strVal2, boolean bFormatValuesBeforeComparison) throws Exception
		{
			boolean isMatch = false;		
			if((UIUtil.isNullOrEmpty(strVal1) && UIUtil.isNullOrEmpty(strVal2)) || (strVal1.equals(strVal2)))
			{
				isMatch = true;
			}
			else if(UIUtil.isNotNullAndNotEmpty(strVal1) && UIUtil.isNotNullAndNotEmpty(strVal2))
			{		
				if(bFormatValuesBeforeComparison)
				{
					strVal1 = getFormattedDecimalValue(strVal1, pgApolloConstants.FORMAT_DECIMAL_NINE);
					strVal2 = getFormattedDecimalValue(strVal2, pgApolloConstants.FORMAT_DECIMAL_NINE);
				}						
				if((Double.valueOf(strVal1)).equals(Double.valueOf(strVal2)))
				{
					isMatch = true;
				}
			}			
			return isMatch;		
		}
		
		/**
		 * Method to get Formatted Decimal Points
		 * @param strValue
		 * @param strDecimalFormat
		 * @return
		 */
		public static String getFormattedDecimalValue(String strValue, String strDecimalFormat)
		{  
			double dNumber;
			if(UIUtil.isNotNullAndNotEmpty(strDecimalFormat))
			{
				DecimalFormat dfnumberFormat = new DecimalFormat(strDecimalFormat);
				if(UIUtil.isNotNullAndNotEmpty(strValue) && NumberUtils.isNumber(strValue))
				{
					dNumber = Double.parseDouble(strValue);
					strValue = dfnumberFormat.format(dNumber);
				}
			}
			return strValue;
		}
		
		/**
		 * This method will return a numeric number with formatted decimal number based on scaled Index
		 * @param context
		 * @param String
		 * @return
		 * @throws Exception
		 */
		public static String getFormattedScaledDecimalValue(String strValue, String sDecimalFormatIndex)
		{  
			double dNumber;
			try{
				if(UIUtil.isNotNullAndNotEmpty(sDecimalFormatIndex)){
					int iScaleIndex = 0;
					try {
						iScaleIndex = Integer.parseInt(sDecimalFormatIndex);					
					}
					catch(Exception e){
						iScaleIndex = 9;
					}
					
					String sPattern = "#." + "#".repeat(iScaleIndex);
					DecimalFormat df = new DecimalFormat(sPattern);

					if(UIUtil.isNotNullAndNotEmpty(strValue)){

						BigDecimal number = new BigDecimal(strValue);
						// Use setScale with RoundingMode
						number = number.setScale(iScaleIndex, RoundingMode.HALF_UP);

						// Convert the BigDecimal to a plain double
						strValue = df.format(number);					

					}
				}
			}catch(Exception ex){
				loggerTrace.error(ex.getMessage(), ex);
				throw ex;
			}
			return strValue;
		}
		
		/**
		 * Method to convert String where we don't get multi-values as StringList
		 * @param object
		 * @return
		 */
		public static StringList getStringListMultiValue(Object object) 
		{
			StringList slOutput = new StringList();
			if(null != object)
			{
				if(object instanceof String)
				{
					String strEachValue;
					String strOutput = (String)object;

					if(UIUtil.isNotNullAndNotEmpty(strOutput))
					{
						String[] strOutputArray = strOutput.split("\\a");
						
						if(null != strOutputArray)
						{
							for(int i=0; i< strOutputArray.length ; i++)
							{
								strEachValue = strOutputArray[i];
								slOutput.add(strEachValue);
							}
						}
						
					}
				}
				else if(object instanceof StringList)
				{
					return (StringList)object;
				}
				
			}			
			return slOutput;
		}
		
		
		
		/**
     * Method to prepare map name
		 * @param _returnMap
		 * @return
		 * @throws Exception 
		 */
		public static Map getDimensions(Context context) throws Exception 
		{
			Map mapDimensions = new HashMap();
			String sDimensionName;			
			List<String> slDimensionList = ParameterInterfacesServices.getAllDimensions(context);			
			if(null!= slDimensionList && !slDimensionList.isEmpty())
			{
				for(String sDimension : slDimensionList)
				{
					sDimensionName = ParameterInterfacesServices.getDimensionNLS(context, sDimension);	
					mapDimensions.put(sDimension, sDimensionName);
				}
			}			
			return mapDimensions;
		}

		 /** 
		 * Method to get multivalues of field in request map
		 * @param context
		 * @param requestMap
		 * @param fieldName
		 * @return
		 * @throws Exception
		 */
		  public static StringList getMultiValueAttrFromForm(Map<?, ?> requestMap, String fieldName) throws Exception 
		  {
			   StringList slAttrValue = new StringList();
			   String sValue;
			   String sAttrList	  = (String)requestMap.get(CriteriaUtil.stringConcat(fieldName, "_mva"));
			   if(UIUtil.isNotNullAndNotEmpty(sAttrList))
			   {
				   StringList slAttrList = StringUtil.split(sAttrList, ":");//Title:Title_mva_1:Title_mva_2:Title_mva_4:
				   for(Object name : slAttrList) 
				   {
					   if(UIUtil.isNotNullAndNotEmpty((String)name)) 
					   {
						   sValue = (String)requestMap.get(name);
						   if(UIUtil.isNotNullAndNotEmpty(sValue))
						   {
							   slAttrValue.add(sValue);
						   }
					   }
				   }
			   }
			   return slAttrValue;
		   }
		  
		  
		  
		 /**
		  * Method to create Change Order for given Part Ids
		  * @param context
		  * @param slPartIdList
		  * @param sExistingCOId
		  * @return
		  * @throws Exception
		  */
		  public static String createChangeObjects(matrix.db.Context context, StringList slPartIdList, String sExistingCOId, String sChangeTemplateId) throws Exception 
		  {
			  String sReturnMessage = DomainConstants.EMPTY_STRING;
			  boolean isTransactionStarted = false;
			  try
			  {
				  if(null!=slPartIdList && !slPartIdList.isEmpty())
				  {
					  
					  StringList strNewPartIdList = new StringList();
					  int iPartListSize = slPartIdList.size();
					  String[] partIdArr = new String[iPartListSize];
					  for(int k=0 ; k < iPartListSize; k++)
					  {
						  partIdArr[k] = slPartIdList.get(k);
					  }

					  Map mpCAInfo = ChangeUtil.getChangeObjectsInProposed(context, new StringList(DomainConstants.SELECT_ID), partIdArr, 1);
					  Map mpRealizedCAInfo = ChangeUtil.getChangeObjectsInRealized(context, new StringList(DomainConstants.SELECT_ID), partIdArr, 1);

					  boolean bChangeExist = false;
					  String strPartId = null;
					  Map caMap = null;
					  MapList proposedOrRealizedchangeActionList = null;
					  Iterator proposedChangeItr = null;
					  for(int iPart=0; iPart<iPartListSize; iPart++)
					  {
						  bChangeExist = false;
						  strPartId = slPartIdList.get(iPart);
						  proposedOrRealizedchangeActionList = (MapList)mpCAInfo.get(strPartId);
						  if(proposedOrRealizedchangeActionList.isEmpty() && mpRealizedCAInfo.containsKey(strPartId))
						  {
							  proposedOrRealizedchangeActionList = (MapList)mpRealizedCAInfo.get(strPartId);	
						  }
						  if(!proposedOrRealizedchangeActionList.isEmpty())
						  {
							  proposedChangeItr = proposedOrRealizedchangeActionList.iterator();
							  while(proposedChangeItr.hasNext()){
								  caMap = (Map)proposedChangeItr.next();	
								  if(ChangeConstants.TYPE_CHANGE_ACTION.equals(caMap.get(DomainConstants.SELECT_TYPE))){	
									  bChangeExist = true;
									  break;
								  }
							  }
							  if(!bChangeExist){
								  strNewPartIdList.add(strPartId);
							  }
						  } else {
							  strNewPartIdList.add(strPartId);
						  }
					  }
					  

					  //Create new CO and CA if it does not exist
					  if(!strNewPartIdList.isEmpty())
					  {						
						  ContextUtil.startTransaction(context,true);
						  isTransactionStarted = true;
						  //Create new CO
						  String strCOId;
						  if(UIUtil.isNullOrEmpty(sExistingCOId))
						  {
							  strCOId  = (new ChangeOrder()).create(context, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING, context.getUser(), sChangeTemplateId, null); 
						  }
						  else
						  {
							  strCOId = sExistingCOId;
						  }
						  ChangeOrder changeOrderObj = new ChangeOrder(strCOId);				
						  changeOrderObj.connectAffectedItems(context,  strNewPartIdList); 
						  ContextUtil.commitTransaction(context);
						  sReturnMessage = strCOId;
						  isTransactionStarted = false;
					  }
				  }
			  }
			  catch(Exception ex)
			  {
				  if(isTransactionStarted)
				  {
					  ContextUtil.abortTransaction(context);
					  isTransactionStarted = false;
				  }
				  loggerTrace.error(ex.getMessage(), ex);
				  sReturnMessage = new StringBuilder(pgApolloConstants.STR_ERROR_CHANGEACTION_CREATION).append(pgApolloConstants.CONSTANT_STRING_COLON).append(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(ex.getLocalizedMessage()).toString();
			  }
			  return sReturnMessage;
		  }
		  
		  
		  /**
			 * This method used to send mail with multiple attachments.
			 * @param args
			 * @return void
			 * @throws Exception
			 */
			public static String sendEmailToUser(String[] args) throws Exception
			{
				String sOutput = pgApolloConstants.STR_SUCCESS;
				
				try {
					String host = args[0];
					String from = args[1];
					String to = args[2];
					String fileAttachments = args[3];
					String strSubject = args[4];
					String strBodyText = args[5];
					String strPersonal = args[6];				
					
					Properties props = System.getProperties();

					props.put("mail.smtp.host", host);

					Session session = Session.getInstance(props, null);

					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(from, strPersonal));
					
					InternetAddress[] toEmailAddresses = null;					
					if(UIUtil.isNotNullAndNotEmpty(to))
					{
						toEmailAddresses = InternetAddress.parse(to , true); // Comma Separated multiple Email Ids
						message.addRecipients(Message.RecipientType.TO, toEmailAddresses);
					}				
					InternetAddress[] ccEmailAddresses = null;					
					String strCc;
					if (args.length > 7) 
					{
					   strCc = args[7];
					   if(UIUtil.isNotNullAndNotEmpty(strCc))
						{
							ccEmailAddresses = InternetAddress.parse(strCc , true);// Comma Separated multiple Email Ids
							message.addRecipients(Message.RecipientType.CC, ccEmailAddresses);
						}
					}
					
					InternetAddress[] bccEmailAddresses = null;					
					String strBcc;
					if (args.length > 8) 
					{
						strBcc = args[8];
					    if(UIUtil.isNotNullAndNotEmpty(strBcc))
						{
							bccEmailAddresses = InternetAddress.parse(strBcc , true);// Comma Separated multiple Email Ids
						  	message.addRecipients(Message.RecipientType.BCC, bccEmailAddresses);
						}

					}
					
					message.setSubject(strSubject);

					MimeBodyPart messageBodyPart = new MimeBodyPart();

					messageBodyPart.setText(strBodyText);
					Multipart multipart = new MimeMultipart();
					multipart.addBodyPart(messageBodyPart);
					String fileSeparator = System.getProperty("file.separator");
					if(UIUtil.isNotNullAndNotEmpty(fileAttachments))
					{
						StringList slFileAttachments = StringUtil.split(fileAttachments, CONSTANT_STRING_COMMA);
						DataSource source = null;
						String fileName;
						for(String fileAttachment : slFileAttachments)
						{			    		
							messageBodyPart = new MimeBodyPart();
							source = new FileDataSource(fileAttachment);
							messageBodyPart.setDataHandler(new DataHandler(source));
							fileName = DomainConstants.EMPTY_STRING;
							if (fileAttachment.indexOf(fileSeparator) != -1) {
								fileName = fileAttachment.substring(fileAttachment.lastIndexOf(fileSeparator) + 1, fileAttachment.length());
							}
							messageBodyPart.setFileName(fileName);
							multipart.addBodyPart(messageBodyPart);
						}
					}
					message.setContent(multipart);
					
					Transport.send(message);
				} catch (Exception e) 
				{
					sOutput = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
				}
				
				return sOutput;
			}	
			
			
			/**
			 * Method to compare 
			 * @param mapCurrent
			 * @param mapPrevious
			 * @return
			 * @throws Exception 
			 */
			public static boolean compareAttributeValues(Map mapCurrent, Map mapPrevious) throws Exception
			{
				boolean bChange = false;
				
				String sPreviousAttributeValue;
				String sCurrentAttributeValue;
				String sAttributeKey;
				
				Iterator<String> itrPreviousAPP =  mapPrevious.keySet().iterator();
				
				while (itrPreviousAPP.hasNext())
				{
					sPreviousAttributeValue = DomainConstants.EMPTY_STRING;
					sCurrentAttributeValue = DomainConstants.EMPTY_STRING;
					sAttributeKey = itrPreviousAPP.next();
					
					if(mapCurrent.containsKey(sAttributeKey))
					{
						sCurrentAttributeValue = (String) mapCurrent.get(sAttributeKey);
					}

					if(mapPrevious.containsKey(sAttributeKey))
					{
						sPreviousAttributeValue = (String) mapPrevious.get(sAttributeKey);
					}							
					
					if(NumberUtils.isNumber(sCurrentAttributeValue) && NumberUtils.isNumber(sPreviousAttributeValue))
					{
						if(!pgApolloCommonUtil.isMatch(sCurrentAttributeValue, sPreviousAttributeValue, false))
						{
							bChange = true;
							return bChange;
						}
					}
					else if(!sPreviousAttributeValue.equals(sCurrentAttributeValue))
					{
						bChange = true;
						return bChange;
					}	
				}
				
				return bChange;	
			}
			
			
			/**
			 * Method to append where clause
			 * @param sbWhereClause
			 * @param fieldName
			 * @param slFieldValues
			 * @return
			 */
			public static StringBuilder appendWhereClause(StringBuilder sbWhereClause, String sFieldName, StringList slFieldValues) 
			{

				if(null!= slFieldValues && slFieldValues.contains(DomainConstants.EMPTY_STRING))
				{
					slFieldValues.remove(DomainConstants.EMPTY_STRING);
				}
				if(UIUtil.isNotNullAndNotEmpty(sFieldName) && null != slFieldValues && !slFieldValues.isEmpty())
				{
					if(!sbWhereClause.toString().isEmpty())
					{
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
						
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_AMPERSAND);
						
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
					}
						
						int iValueCount = 0;
						
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_OPEN_ROUND_BRACE);
						
						for(String sValue : slFieldValues)
						{
							iValueCount = iValueCount + 1;
							
							if(iValueCount > 1)
							{
								sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
								sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_PIPE);
								sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							}
							
							sbWhereClause.append(sFieldName);
							
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);
							
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL);
							
							sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_SPACE);

							sbWhereClause.append("'");
							
							sbWhereClause.append(sValue);
							
							sbWhereClause.append("'");
							
						}
						
						sbWhereClause.append(pgApolloConstants.CONSTANT_STRING_CLOSE_ROUND_BRACE);		
				}		
				return sbWhereClause;
			}
			
			/**
			 * Method to compare two String List and find uncommon Set
			 * @param slCurrentList
			 * @param slPreviousList
			 * @return
			 */
			public static StringList compareCriteriaApplicableParts(StringList slCurrentList, StringList slPreviousList) 
			{
				StringList slDifferenceList = new StringList();

				List<String> listCurrentWithoutPrevious = slCurrentList.stream().filter(element -> !slPreviousList.contains(element)).collect(Collectors.toList());
				List<String> listPreviousWithoutCurrent = slPreviousList.stream().filter(element -> !slCurrentList.contains(element)).collect(Collectors.toList());
				
				slDifferenceList.addAll(listCurrentWithoutPrevious);
				slDifferenceList.addAll(listPreviousWithoutCurrent);

				return slDifferenceList;
			}
			
			/**
			 * Method to compare two String List and find uncommon Set
			 * @param slCurrentList
			 * @param slPreviousList
			 * @return
			 */
			public static StringList getDifferenceBetweenLists(StringList slCurrentList, StringList slPreviousList) 
			{
				StringList slDifferenceList = new StringList();

				List<String> listCurrentWithoutPrevious = slCurrentList.stream().filter(element -> !slPreviousList.contains(element)).collect(Collectors.toList());
				List<String> listPreviousWithoutCurrent = slPreviousList.stream().filter(element -> !slCurrentList.contains(element)).collect(Collectors.toList());
				
				slDifferenceList.addAll(listCurrentWithoutPrevious);
				slDifferenceList.addAll(listPreviousWithoutCurrent);

				return slDifferenceList;
			}
			
			/**
			 * Method to get Owner list for Part
			 * @param context
			 * @param sPartId
			 * @return
			 * @throws Exception 
			 */
			public static StringList getOwnerListForPart(Context context, String sPartId) throws Exception 
			{
				StringList slOwnerList = new StringList();
				
				MapList mlAccessResults = DomainAccess.getAccessSummaryList(context, sPartId);
				
				if(null != mlAccessResults && !mlAccessResults.isEmpty())
				{
					Map mapAccess;
					String sAccess;
					String sUserName;
					
					for(Object objectMap : mlAccessResults)
					{
						mapAccess = (Map)objectMap;
						sAccess = (String)mapAccess.get(pgApolloConstants.KEY_ACCESS);				
						if(mapAccess.containsKey(pgApolloConstants.KEY_USERNAME) && pgApolloConstants.STR_ALL.equalsIgnoreCase(sAccess))
						{
							sUserName = (String)mapAccess.get(pgApolloConstants.KEY_USERNAME);
							slOwnerList.add(sUserName);
						}
					}
				}
				
				return slOwnerList;
			}
			
			/**
			 * Method to get Full Names of Persons
			 * @param context
			 * @param slOwnerList
			 * @param bIncludeUserName
			 * @return
			 * @throws FrameworkException
			 */
			public static String getOwnerFullNameList(Context context, StringList slOwnerList, boolean bIncludeUserName) throws FrameworkException 
			{
				StringBuilder sbOwner;
				String sOwnerNameList = DomainConstants.EMPTY_STRING;		
				if(!slOwnerList.isEmpty())
				{
					StringList slOwnerNameList = new StringList();
					String sOwnerFullName;
					
					for(String sOwner : slOwnerList)
					{
						sbOwner = new StringBuilder();
						sOwnerFullName = PersonUtil.getFullName(context, sOwner);
						sbOwner.append(sOwnerFullName);
						if(bIncludeUserName)
						{
							sbOwner.append(pgApolloConstants.CONSTANT_STRING_OPEN_ROUND_BRACE).append(sOwner).append(pgApolloConstants.CONSTANT_STRING_CLOSE_ROUND_BRACE);
						}						
						slOwnerNameList.add(sbOwner.toString());
					}
					
					sOwnerNameList = StringUtil.join(slOwnerNameList, pgApolloConstants.CONSTANT_STRING_COMMA);
				}		
				return sOwnerNameList;
			}

			
			/**
			 * Method to get Owner email Id list
			 * @param context
			 * @param slUsersToBeNotified
			 * @return
			 * @throws FrameworkException
			 */
			public static String getOwnerEmailIdList(Context context, StringList slUsersToBeNotified) throws FrameworkException
			{
				String sUserEmailIdList = DomainConstants.EMPTY_STRING;		
				if(!slUsersToBeNotified.isEmpty())
				{
					StringList slUserEmailIdList = new StringList();
					String sUserEmail;
					
					for(String sUser : slUsersToBeNotified)
					{
						sUserEmail = PersonUtil.getEmail(context, sUser);
						slUserEmailIdList.add(sUserEmail);
					}
					
					sUserEmailIdList = StringUtil.join(slUserEmailIdList, pgApolloConstants.CONSTANT_STRING_COMMA);
				}		
				return sUserEmailIdList;
			}	
			
					
			
			/**

			 * @param context

			 * @param sObjectId

			 * @param History Entry

			 * @throws FrameworkException

			 */

			public static void updateHistory(Context context, String sObjectId, String strHistory) throws FrameworkException
			{

				StringList slHistoryList = StringUtil.split(strHistory, "|");		

				String strMQLstmt = "modify bus $1 add history 'connect' comment '$2'";

				int iHistorySize = slHistoryList.size();

				for(int i=0; i<iHistorySize; i++)
				{
					String sHistory = slHistoryList.get(i);
					if(UIUtil.isNotNullAndNotEmpty(sHistory))
					{
						//There is no standard API to update History. So MQL command will be required to update History.
						MqlUtil.mqlCommand(context, strMQLstmt, sObjectId, sHistory); 				

					} 			

				}

			}
			
			

			/**
			 * Method to convert String json which is usually the value of attributes to
			 * JsonObject
			 * @param strJsonString : String json
			 * @return : JsonObject created from String json
			 */
			public static JsonArray getJsonArrayFromJsonString(String strJsonString) 
			{
				
				if(UIUtil.isNotNullAndNotEmpty(strJsonString))
				{
					StringReader srJsonString = new StringReader(strJsonString);				
					
					Map<String, String> configMap = new HashMap<>();
					
					configMap.put(pgApolloConstants.MAX_STRING_LENGTH, pgApolloConstants.VALUE_KILOBYTES);
					
					JsonReaderFactory factory = Json.createReaderFactory(configMap);
					try (JsonReader jsonReader = factory.createReader(srJsonString)) {
						
						return jsonReader.readArray();
					} finally {
						srJsonString.close();
					}
				}
				else
				{
					return null;
				}
				
			}	
			
			/**
			 * Method to get Map from JSON Array
			 * @param jaDetails
			 * @return
			 */
			public static Map getMapFromJSONArray(JsonArray jaDetails) 
			{
		        Map<String,Object> mapObject = new HashMap<>();
				if(null != jaDetails && !jaDetails.isEmpty())
				{
					JsonObject jsonObject;
					JsonObject jsonChildObject;

					Set<String> setObjectKeys;
					Set<String> setChildObjectKeys;
					String sObjectId;
					String sChildKeyValue;

			        Map mapAttribute;
					
					for (int i = 0 ; i < jaDetails.size(); i++)
					{
						
						jsonObject = jaDetails.getJsonObject(i);					
						setObjectKeys = jsonObject.keySet();
						
						if(null != setObjectKeys && !setObjectKeys.isEmpty())
						{
							sObjectId = setObjectKeys.iterator().next();
							
							if(UIUtil.isNotNullAndNotEmpty(sObjectId))
							{
								mapAttribute = new HashMap();
								
								jsonChildObject = jsonObject.getJsonObject(sObjectId);
								
								if(null != jsonChildObject && !jsonChildObject.isEmpty())
								{
									setChildObjectKeys = jsonChildObject.keySet();
									
									for(String sChildKey : setChildObjectKeys)
									{
										sChildKeyValue = jsonChildObject.getString(sChildKey);
										mapAttribute.put(sChildKey, sChildKeyValue);
									}
								}
								
								mapObject.put(sObjectId, mapAttribute);
							}				
							
						}
					}
					
				}		
				return mapObject;
			}
			
			
			/**
			 * Method to get Latest Active Object Info
			 * @param context
			 * @param strType
			 * @param strName
			 * @param slBusSelects
			 * @return
			 * @throws Exception
			 */
			public static Map getLatestActiveObjectInfo(Context context, String strType, String strName, StringList slBusSelects) throws Exception
			{
				Map returnMap = new HashMap();
				
				//Object Select
				StringList slObjSelect = new StringList();
				slObjSelect.add(DomainConstants.SELECT_ID);	
				slObjSelect.addAll(slBusSelects);
				slObjSelect.add(DomainConstants.SELECT_ORIGINATED);	

				//Where Condition : current == Active && revision == last 
				StringBuilder sbWhereCondition = new StringBuilder(DomainConstants.SELECT_CURRENT).append(CONSTANT_STRING_DOUBLE_EQUAL).append(STATE_ACTIVE);
				
				//Fetch object info
				MapList mlObjectDetails =  DomainObject.findObjects(context, // context
						strType, // type pattern
						strName, // name pattern
						DomainConstants.QUERY_WILDCARD, // revision pattern	
						DomainConstants.QUERY_WILDCARD, // owner pattern	
						pgApolloConstants.VAULT_ESERVICE_PRODUCTION, // vault pattern	
						sbWhereCondition.toString(), // where expression
						false, // expand type
						slObjSelect); // Object Selectables
				
				if(null != mlObjectDetails && !mlObjectDetails.isEmpty())
				{
					mlObjectDetails.addSortKey(DomainConstants.SELECT_ORIGINATED, pgApolloConstants.STR_DESCENDING, "date");
					mlObjectDetails.sort();
					
					returnMap =	(Map) mlObjectDetails.get(0) ;	
				}
				
				return returnMap;
			}
			/**
			 * Method to get Object Details
			 * @param context
			 * @param args
			 * @return
			 * @throws Exception
			 */
			public static Map getObjectDetails(Context context, String[] args) throws Exception
			{
				Map mapReturn;				
				Map programMap = (HashMap)JPO.unpackArgs(args);
				StringList slObjectIdList = (StringList)programMap.get(DomainConstants.SELECT_ID);		
				mapReturn = getObjectDetails(context, slObjectIdList);		
				return mapReturn;
			}

			
			/**
			 * Method to get Object Details
			 * @param context
			 * @param slObjectIdList
			 * @return
			 * @throws FrameworkException
			 */
			public static Map getObjectDetails(Context context, StringList slObjectIdList)	throws FrameworkException 
			{
				Map mapReturn = new HashMap();
				
				if(null != slObjectIdList && !slObjectIdList.isEmpty())
				{
					StringList slObjectSelectable = new StringList();
					slObjectSelectable.add(DomainConstants.SELECT_ID);
					slObjectSelectable.add(DomainConstants.SELECT_NAME);
					slObjectSelectable.add(DomainConstants.SELECT_REVISION);
					
					MapList mlObjectList = DomainObject.getInfo(context, slObjectIdList.toArray(new String[slObjectIdList.size()]), slObjectSelectable);
									
					Map mapObject;
					String sObjectName;
					String sObjectId;
					String sObjectRevision;
					StringBuilder sbObjectDetails;
					
					for(Object obj : mlObjectList)
					{
						mapObject = (Map)obj;
						sObjectId = (String)mapObject.get(DomainConstants.SELECT_ID);
						
						if(UIUtil.isNotNullAndNotEmpty(sObjectId))
						{
							sObjectName = (String)mapObject.get(DomainConstants.SELECT_NAME);
							sObjectRevision = (String)mapObject.get(DomainConstants.SELECT_REVISION);

							sbObjectDetails = new StringBuilder();
							sbObjectDetails.append(sObjectName);
							sbObjectDetails.append(pgApolloConstants.CONSTANT_STRING_PIPE);
							sbObjectDetails.append(sObjectRevision);
							
							mapReturn.put(sObjectId, sbObjectDetails.toString());			
						}
						
					}

				}
				
				return mapReturn;
			}
			/**
			 * Method to get Object Details with additional selectable
			 * @param context
			 * @param slObjectIdList
			 * @param slNewAdditionalSelectables
			 * @return
			 * @throws FrameworkException
			 */
			public static Map getObjectDetails(Context context, StringList slObjectIdList, StringList slNewAdditionalSelectables)	throws FrameworkException 
			{
				Map mapReturn = new HashMap();
				
				if(null != slObjectIdList && !slObjectIdList.isEmpty())
				{
					StringList slObjectSelectable = new StringList();
					slObjectSelectable.add(DomainConstants.SELECT_ID);
					slObjectSelectable.add(DomainConstants.SELECT_NAME);
					slObjectSelectable.add(DomainConstants.SELECT_REVISION);
					if(null != slNewAdditionalSelectables && !slNewAdditionalSelectables.isEmpty())
					{
						slObjectSelectable.addAll(slNewAdditionalSelectables);
					}

					MapList mlObjectList = DomainObject.getInfo(context, slObjectIdList.toArray(new String[slObjectIdList.size()]), slObjectSelectable);
									
					Map mapObject;
					String sObjectName;
					String sObjectId;
					String sObjectRevision;
					String sObjectAdditionalSelectableValue;

					StringBuilder sbObjectDetails;
					
					for(Object obj : mlObjectList)
					{
						mapObject = (Map)obj;
						sObjectId = (String)mapObject.get(DomainConstants.SELECT_ID);		
						
						
						if(UIUtil.isNotNullAndNotEmpty(sObjectId))
						{
							sObjectName = (String)mapObject.get(DomainConstants.SELECT_NAME);
							sObjectRevision = (String)mapObject.get(DomainConstants.SELECT_REVISION);

							sbObjectDetails = new StringBuilder();
							sbObjectDetails.append(sObjectName);
							sbObjectDetails.append(pgApolloConstants.CONSTANT_STRING_DOT);
							sbObjectDetails.append(sObjectRevision);
							if(null != slNewAdditionalSelectables && !slNewAdditionalSelectables.isEmpty())
							{
								for(String sAdditionalSelectable : slNewAdditionalSelectables)
								{
									sObjectAdditionalSelectableValue = (String)mapObject.get(sAdditionalSelectable);	
									sbObjectDetails.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbObjectDetails.append(pgApolloConstants.CONSTANT_STRING_PIPE);
									sbObjectDetails.append(pgApolloConstants.CONSTANT_STRING_SPACE);
									sbObjectDetails.append(sObjectAdditionalSelectableValue);
								}
								
							}
							mapReturn.put(sObjectId, sbObjectDetails.toString());			
						}
						
					}

				}
				
				return mapReturn;
			}
			
			/**
			 * This is a utility method to get the value from Page file JSON File
			 * @param context
			 * @param sPageName : Page File name
			 * @param sKey : Property Key
			 * @return : returns value of the given property key
			 * @throws MatrixException 
			 * @throws IOException 
			 * @throws Exception
			 */
			public static Map getPagePropertyMapBasedOnJson(Context context, String sPageName, String sKey) throws MatrixException
			{
				Map<String, Object> mapOutput = new HashMap();
				String sPageContent = getPageContent(context, sPageName);			
				if(UIUtil.isNotNullAndNotEmpty(sPageContent) && UIUtil.isNotNullAndNotEmpty(sKey)) {
					
					JsonObject jsonObject = getJsonFromJsonString(sPageContent);		
					mapOutput = parseJsonBasedOnValueType(jsonObject, sKey);				
				}
				return mapOutput;
			}

			/**
			 * Method to get Page Content for Page File
			 * @param context
			 * @param sPageName
			 * @return
			 * @throws MatrixException
			 */
			public static String getPageContent(Context context, String sPageName)throws MatrixException {
				String sPageContent=(String) CacheUtil.getCacheObject(context, sPageName);		
				if(UIUtil.isNullOrEmpty(sPageContent)) {
					Page pageObject = new Page(sPageName);
					boolean isPageExists	= pageObject.exists(context);
					if(isPageExists)
					{
						pageObject.open(context);
						sPageContent = pageObject.getContents(context);
						pageObject.close(context);
						CacheUtil.setCacheObject(context, sPageName, sPageContent);
					}
					else
					{
						sPageContent = DomainConstants.EMPTY_STRING;
					}
					
				}
				return sPageContent;
			}
			
			/**
			 * This is a utility method to get the value from Page file JSON File
			 * @param context
			 * @param sPageName : Page File name
			 * @param sKey : Property Key
			 * @return : returns value of the given property key
			 * @throws MatrixException 
			 * @throws IOException 
			 * @throws Exception
			 */
			public static Map getPagePropertyMapBasedOnJson(Context context, String sPageName) throws MatrixException
			{
				Map<String, Object> mapOutput = new HashMap();
				String sPageContent = getPageContent(context, sPageName);			
				if(UIUtil.isNotNullAndNotEmpty(sPageContent)) {
					
					JsonObject jsonObject = getJsonFromJsonString(sPageContent);		
					mapOutput = parseJsonBasedOnValueType(jsonObject);				
				}
				return mapOutput;
			}
			
			/**
			 * Method to parse string json to object dynamically
			 * @param sJsonString
			 * @return
			 * @throws MatrixException
			 */
			public static Map getStringMapBasedOnJson(String sJsonString) throws MatrixException
			{
				Map<String, Object> mapOutput = new HashMap<>();
				if(UIUtil.isNotNullAndNotEmpty(sJsonString)) {

					JsonObject jsonObject = getJsonFromJsonString(sJsonString);		
					mapOutput = parseJsonBasedOnValueType(jsonObject);				
				}
				return mapOutput;
			}
			
			/**
			 * Method to parse string json array to object dynamically
			 * @param sJsonString
			 * @return
			 * @throws MatrixException
			 */
			public static Map getStringMapBasedOnJsonArray(String sJsonString) throws Exception
			{
				Map<String, Object> mapOutput = new HashMap<>();
				try
				{
					if(UIUtil.isNotNullAndNotEmpty(sJsonString)) {

						JsonArray jsonArray = getJsonArrayFromJsonString(sJsonString);	
						processJsonObject(mapOutput, "data", jsonArray);		
					}
				}			
				catch(Exception ex)
				{
					
					loggerTrace.error(ex.getMessage(), ex);
					throw ex;
				}				
				return mapOutput;
			}
			
			/**
			 * Method to get Value for Each type based on key
			 * @param jsonObject
			 * @return
			 */
			public static Map parseJsonBasedOnValueType(JsonObject jsonObject, String sKey) {
				Map<String, Object> mapOutput = new HashMap();
				JsonValue jsonValue;
				Map mapLocal;
				if(jsonObject.containsKey(sKey))
				{
					jsonValue = jsonObject.get(sKey);
					
					switch(jsonValue.getValueType()){
				       case ARRAY:
				    	   processJsonObject(mapOutput, sKey, (JsonArray)jsonValue);	
				           break;
				       case OBJECT:
				    	   mapLocal = parseJsonBasedOnValueType((JsonObject)jsonValue);
				    	   mapOutput.put(sKey, mapLocal);
				           break;
				       case NUMBER:
				    	   //mapOutput.put(sKey, Integer.toString(((JsonNumber)jsonValue).intValue()));
				    	   mapOutput.put(sKey, Integer.toString(((JsonNumber)jsonValue).intValue())).toString();
				           break;
				       case STRING:
				    	   mapOutput.put(sKey, ((JsonString)jsonValue).getString());
				           break;
				       case TRUE:
				    	   mapOutput.put(sKey, true);
				           break;
				       case FALSE:
				    	   mapOutput.put(sKey, false);
				           break;
				       default:
				    	   mapOutput.put(sKey, jsonValue.toString());
				   }
				}			
				return mapOutput;
			}
			
			/**
			 * Method to get Value for Each type
			 * @param jsonObject
			 * @return
			 */
			public static Map parseJsonBasedOnValueType(JsonObject jsonObject) {
				Map<String, Object> mapOutput = new HashMap();
				String sKey;
				JsonValue jsonValue;
				Map mapLocal;

				if(jsonObject.getValueType() == JsonValue.ValueType.ARRAY)
				{
					processJsonObject(mapOutput, "data", jsonObject.asJsonArray());
				}
				else if(jsonObject.getValueType() == JsonValue.ValueType.OBJECT)
				{
					for (Map.Entry<String,JsonValue> entry : jsonObject.entrySet())
					{
						sKey = entry.getKey();				
						jsonValue = entry.getValue();

						switch(jsonValue.getValueType()){
						case ARRAY:
							processJsonObject(mapOutput, sKey, (JsonArray)jsonValue);	
							break;
						case OBJECT:
							mapLocal = parseJsonBasedOnValueType((JsonObject)jsonValue);
							mapOutput.put(sKey, mapLocal);
							break;
						case NUMBER:
							mapOutput.put(sKey, Integer.toString(((JsonNumber)jsonValue).intValue()));
							break;
						case STRING:
							mapOutput.put(sKey, ((JsonString)jsonValue).getString());
							break;
						case TRUE:
							mapOutput.put(sKey, true);
							break;
						case FALSE:
							mapOutput.put(sKey, false);
							break;
						default:
							mapOutput.put(sKey, jsonValue.toString());
						}
					}
				}		
				return mapOutput;
			}
			
			/**
			 * Method to process Object if it is type of Array
			 * @param mapOutput
			 * @param sKey
			 * @param jsonValue
			 * @return
			 */
			private static Map processJsonObject(Map mapOutput, String sKey, JsonArray jsonValue) 
			{		
				if(null != jsonValue && jsonValue.isEmpty())
				{
			        mapOutput.put(sKey, new StringList());
				}
				else if(null != jsonValue && !jsonValue.isEmpty())
				{
					Object objectValue = jsonValue.get(0);
					if(objectValue instanceof JsonString)
					{
						StringList slValueList = new StringList();			
						int len = jsonValue.size();
						for (int i=0;i<len;i++){ 
							slValueList.add(((JsonString)jsonValue.get(i)).getString());
						} 
						mapOutput.put(sKey, slValueList);

					}
					else if(objectValue instanceof JsonNumber)
					{
						List<Integer> integerValueList = new ArrayList();			
						int len = jsonValue.size();
						for (int i=0;i<len;i++){ 
							integerValueList.add(((JsonNumber)jsonValue).intValue());
						} 
						mapOutput.put(sKey, integerValueList);
					}		
					else
					{
						Map mapLocal;
						MapList mlLocalList = new MapList();
						List<JsonObject> elements = jsonValue.getValuesAs(JsonObject.class);
				        for(JsonObject element: elements) {
				        	mapLocal = parseJsonBasedOnValueType(element);
				        	mlLocalList.add(mapLocal);
				        }	        
				        mapOutput.put(sKey, mlLocalList);
					}		
				}			
				return mapOutput;			
			}
			
			/**
			 * Method to read Json from Json String
			 * @param strJsonString
			 * @return
			 */
			public static JsonObject getJsonFromJsonString(String strJsonString) {
				Map<String, String> configMap = new HashMap<>();
				configMap.put(pgApolloConstants.MAX_STRING_LENGTH, pgApolloConstants.VALUE_KILOBYTES);
				JsonReaderFactory factory = Json.createReaderFactory(configMap);
				try (
						StringReader srJsonString = new StringReader(strJsonString);
						JsonReader jsonReader = factory.createReader(srJsonString)
				) {
					return jsonReader.readObject();
				}
			}

			/**
			 * This method is used to release Core material to Released State
			 * @param context
			 * @param domCoreMaterial
			 * @param strCoreMaterialCurrent
			 * @throws Exception
			 */
			public static void releaseCoreMaterial(matrix.db.Context context, DomainObject domCoreMaterial, String strCoreMaterialCurrent, String sRMPCurrent) throws Exception {
				
				try
				{
					
					if(STATE_RELEASE.equalsIgnoreCase(sRMPCurrent) && !STATE_SHARED.equalsIgnoreCase(strCoreMaterialCurrent) && !STATE_OBSOLETE_CATIA.equalsIgnoreCase(strCoreMaterialCurrent))
					{					
						//Added for releasing Composite Domain object which is connected to Core Material - Starts
						Object objCompDomain = domCoreMaterial.getInfo(context, new StringBuilder(CONSTANT_STRING_SELECT_FROM).append(RELATIONSHIP_MATERIAL_DOMAIN_INSTANCE).append("].").append(CONSTANT_STRING_SELECT_TO).append(TYPE_DSC_MATREF_REP_COMPOSITE).append(CONSTANT_STRING_SELECT_RELID).toString());
						if(objCompDomain instanceof String && UIUtil.isNotNullAndNotEmpty((String)objCompDomain))
						{
							DomainObject domCompositeDomainObject = DomainObject.newInstance(context,(String)objCompDomain);
							String strCompositeState = domCompositeDomainObject.getInfo(context, SELECT_CURRENT);
							if(!STATE_SHARED.equalsIgnoreCase(strCompositeState) && !STATE_OBSOLETE_CATIA.equalsIgnoreCase(strCompositeState))
							{
								pgApolloCommonUtil.releaseVPLMObject(context, domCompositeDomainObject, strCompositeState);
							}
						}
						//Added for releasing Composite Domain object which is connected to Core Material - Ends
						pgApolloCommonUtil.releaseVPLMObject(context, domCoreMaterial, strCoreMaterialCurrent);	
					}
					else if(STATE_PRIVATE.equalsIgnoreCase(strCoreMaterialCurrent))
					{
						pgApolloCommonUtil.approveSignatures(context, domCoreMaterial, STATE_PRIVATE, STATE_IN_WORK);
						domCoreMaterial.promote(context);
					}
				}
				catch(Exception ex)
				{
					loggerTrace.error(ex.getMessage(), ex);
					throw ex;
				}
			}
			
			/**
			 * This method is used to release Objects Like Core Material, Composite Object
			 * @param context
			 * @param domObject
			 * @param sObjectCurrentState
			 * @throws Exception
			 */
			public static void releaseVPLMObject(matrix.db.Context context, DomainObject domObject, String sObjectCurrentState) throws Exception {
				
				try
				{
					if(STATE_PRIVATE.equalsIgnoreCase(sObjectCurrentState))
					{
						approveSignatures(context, domObject, STATE_PRIVATE, STATE_IN_WORK);
						domObject.promote(context);
						unsignPreviousSignatures(context, domObject, STATE_IN_WORK, STATE_PRIVATE);	
						approveSignatures(context, domObject, STATE_IN_WORK, STATE_WAITAPP);	
						domObject.promote(context);
					}
					if(STATE_IN_WORK.equalsIgnoreCase(sObjectCurrentState))
					{
						approveSignatures(context, domObject, STATE_IN_WORK, STATE_WAITAPP);
						unsignPreviousSignatures(context, domObject, STATE_IN_WORK, STATE_PRIVATE);	
						unsignPreviousSignatures(context, domObject, STATE_IN_WORK, STATE_SHARED);	
						domObject.promote(context);
					}						
					approveSignatures(context, domObject, STATE_WAITAPP, STATE_SHARED);
					unsignPreviousSignatures(context, domObject, STATE_WAITAPP, STATE_IN_WORK);
					domObject.promote(context);
				}
				catch(Exception ex)
				{
					loggerTrace.error(ex.getMessage(), ex);
					throw ex;
				}
			}
			/**
			 * This method is used to unsign signatures automatically during promotion of Object
			 * @param context
			 * @param domObject
			 * @param strCurrentState
			 * @param strPreviousState
			 * @throws Exception
			 */
			public static void unsignPreviousSignatures(matrix.db.Context context, DomainObject domObject,String strCurrentState, String strPreviousState) throws Exception {
				SignatureList slSignature;
				slSignature = domObject.getSignatures(context,strCurrentState,strPreviousState);													
				for( Iterator itr =  slSignature.iterator();itr.hasNext();)
				{
					Signature objSignature = (Signature)itr.next();
					if(objSignature.isSigned())
					{
						domObject.rejectSignature(context, objSignature, DomainConstants.EMPTY_STRING);    	    								
					}
				}
			}
			/**
			 * This method is used to approve Signatures during promotion of Object.
			 * @param context
			 * @param domObject
			 * @param strNextState
			 * @param strCurrentState
			 * @throws Exception
			 */
			public static void approveSignatures(matrix.db.Context context, DomainObject domObject, String strCurrentState, String strNextState) throws Exception {
				SignatureList slSignature = domObject.getSignatures(context, strCurrentState, strNextState);
				for(Iterator itr =  slSignature.iterator(); itr.hasNext();)
				{
					Signature objSignature = (Signature)itr.next();
					domObject.approveSignature(context, objSignature, DomainConstants.EMPTY_STRING);    	    								
				}		
			}
			
			/**
			 * Method to validate Applicator Parts
			 * @param context
			 * @param mlApplicators
			 * @param slRMPList
			 * @return
			 * @throws Exception
			 */
			public static String validateApplicatorParts(Context context, MapList mlApplicators, StringList slRMPList, boolean bErrorCodeMode) throws Exception
			{		
				String sReturn = DomainConstants.EMPTY_STRING;
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateApplicatorParts >> slRMPList = "+slRMPList+" mlApplicators "+mlApplicators);

				boolean isValidApplicators = false;
				StringList slCATIAApplicatorList = getCATIAApplicatorList(context, mlApplicators);		
				sReturn = validateRMPApplicatorList(context, slRMPList, slCATIAApplicatorList, bErrorCodeMode);
				return sReturn;
			}
			
			
			/**
			 * Method to get CATIA Applicator List
			 * @param context
			 * @param mlApplicators
			 * @return
			 * @throws Exception
			 */
			public static StringList getCATIAApplicatorList(Context context, MapList mlApplicators) throws Exception 
			{
				StringList slAllPhysicalProductList = new StringList();
				
				Map mapApplicator;
				String sPhysicalProductName;
				String sPhysicalProductRevision;
				StringBuilder sbPhysicalProductDetails;
				String sPhysicalProductDetails;

				for(Object objMap : mlApplicators)
				{
					mapApplicator = (Map)objMap;
					sPhysicalProductName = (String)mapApplicator.get(pgApolloConstants.KEY_NAME);
					sPhysicalProductRevision = (String)mapApplicator.get(pgApolloConstants.KEY_REV);
					sbPhysicalProductDetails = new StringBuilder(sPhysicalProductName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sPhysicalProductRevision);
					sPhysicalProductDetails = sbPhysicalProductDetails.toString();			
					slAllPhysicalProductList.add(sPhysicalProductDetails);
				}
				
				slAllPhysicalProductList.sort();
				
				return slAllPhysicalProductList;
			}
			
			/**
			 * Method to validate Applicator List w.r.t. RMP List
			 * E017 - Duplicate Applicator Parts
			 * E018 - Invalid or missing Applicator Parts
			 * E019 - Applicator Parts are not released
			 * @param context
			 * @param slRMPList
			 * @param slCATIAApplicatorList
			 * @param bErrorCodeMode
			 * @return
			 * @throws Exception
			 */
			public static String validateRMPApplicatorList(Context context, StringList slRMPList, StringList slCATIAApplicatorList, boolean bErrorCodeMode) throws Exception 
			{
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPApplicatorList >> slRMPList = "+slRMPList+" slCATIAApplicatorList "+slCATIAApplicatorList);

				String sReturn = DomainConstants.EMPTY_STRING;
				
				StringList slAllPhysicalProductList = new StringList();
				
				Map mpRMP;
				StringList slRMPId = new StringList();
				StringList slMasterIdList = new StringList();
				StringList slAllPhysicalProductIdList = new StringList();
				StringList slDuplicateMasterIdList = new StringList();

			
				for(String sRMPName : slRMPList)
				{
					mpRMP = pgApolloCommonUtil.getRMPIdForSync(context,sRMPName);
					String sPartId = (String)mpRMP.get(DomainConstants.SELECT_ID);
					
					if(UIUtil.isNotNullAndNotEmpty(sPartId)) 
					{
						slRMPId.add(sPartId);
					}
				}
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPApplicatorList  slRMPId = "+slRMPId);

				
				String sInfoSelMaster= new StringBuilder("to[").append(DomainConstants.RELATIONSHIP_CLASSIFIED_ITEM).append("].frommid[").append(pgV3Constants.RELATIONSHIP_PARTFAMILYREFERENCE).append("].torel.to.id").toString();

				StringList slPartSelects = new StringList();
				slPartSelects.add(sInfoSelMaster);
				
				if(!slRMPId.isEmpty())
				{
					
					MapList mlRMPDetails = DomainObject.getInfo(context, (String[]) slRMPId.toArray(new String []{}), slPartSelects);

					Map mapRMPInfo;
					StringList slMasterId;
					String sMasterId;
					
					for(Object objMap : mlRMPDetails)
					{
						mapRMPInfo = (Map)objMap;
						slMasterId = pgApolloCommonUtil.getStringListMultiValue(mapRMPInfo.get(sInfoSelMaster));
						
						sMasterId = DomainConstants.EMPTY_STRING;
						if(!slMasterId.isEmpty())
						{
							sMasterId = slMasterId.get(0);
						}

						if(UIUtil.isNotNullAndNotEmpty(sMasterId))
						{
							if(!slMasterIdList.contains(sMasterId))
							{
								slMasterIdList.add(sMasterId);
							}
							else
							{
								slDuplicateMasterIdList.add(sMasterId);
							}
						}
					}			
					
				}
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPApplicatorList  slMasterIdList = "+slMasterIdList+" slDuplicateMasterIdList = "+slDuplicateMasterIdList);

				
				if(!slDuplicateMasterIdList.isEmpty())
				{
					Map mapOutput = getObjectDetails(context, slDuplicateMasterIdList);					
					List listDuplicateMasterList = new ArrayList(mapOutput.values());
					if(bErrorCodeMode)
					{
						//E017 - Duplicate Applicator Parts
						sReturn = pgApolloConstants.STR_ERROR_CODE_E017;
					}
					else
					{
						sReturn = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(STR_ERROR_SAME_MASTER_APPLICATORS).append(pgApolloConstants.CONSTANT_STRING_SPACE).append(pgApolloConstants.CONSTANT_STRING_COLON).append(String.join(pgApolloConstants.CONSTANT_STRING_COMMA, listDuplicateMasterList)).toString();
					}
					return sReturn;
				}
				
				String sPhysicalProductIdSelectable = new StringBuilder().append(pgApolloConstants.CONSTANT_STRING_SELECT_FROM).append(pgApolloConstants.RELATIONSHIP_PART_SPECIFICATION).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(pgApolloConstants.CONSTANT_STRING_SELECT_TO).append(pgApolloConstants.TYPE_VPMREFERENCE).append(pgApolloConstants.CONSTANT_STRING_SELECT_CLOSE_BRACKET).append(pgApolloConstants.CONSTANT_STRING_DOT).append(DomainConstants.SELECT_ID).toString();
				
				StringList slMasterSelects = new StringList();
				slMasterSelects.add(DomainConstants.SELECT_ID);
				slMasterSelects.add(DomainConstants.SELECT_TYPE);
				slMasterSelects.add(sPhysicalProductIdSelectable);
				
				String sMasterType;
				StringList slApplicatorTypes = new StringList();
				String sApplicatorTypes = pgApolloCommonUtil.getPageProperty(context, pgApolloConstants.STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloConfiguration.ApplicatorPart.ApplicableTypes");
				if(UIUtil.isNotNullAndNotEmpty(sApplicatorTypes))
				{
					slApplicatorTypes = StringUtil.split(sApplicatorTypes, pgApolloConstants.CONSTANT_STRING_COMMA);
				}
			
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPApplicatorList  slApplicatorTypes = "+slApplicatorTypes);

				StringList slPhysicalProductIdList;
				String sPhysicalProductId;
				
				if(!slMasterIdList.isEmpty())
				{		
					MapList mlMasterProductDetails = DomainObject.getInfo(context, (String[]) slMasterIdList.toArray(new String []{}), slMasterSelects);
					
					Map mapMasterProduct;
					
					for(Object objMap : mlMasterProductDetails)
					{
						mapMasterProduct = (Map)objMap;
						sMasterType = (String)mapMasterProduct.get(DomainConstants.SELECT_TYPE);

						if(slApplicatorTypes.contains(sMasterType))
						{
							slPhysicalProductIdList = pgApolloCommonUtil.getStringListMultiValue(mapMasterProduct.get(sPhysicalProductIdSelectable));
							if(null != slPhysicalProductIdList && !slPhysicalProductIdList.isEmpty())
							{
								sPhysicalProductId = slPhysicalProductIdList.get(0);
								
								if(UIUtil.isNotNullAndNotEmpty(sPhysicalProductId))
								{
									slAllPhysicalProductIdList.add(sPhysicalProductId);							
								}
							}
						}
						
					}		

				}	
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPApplicatorList  slAllPhysicalProductIdList = "+slAllPhysicalProductIdList);

				
				StringList slPhysicalProductSelects = new StringList();
				slPhysicalProductSelects.add(DomainConstants.SELECT_ID);
				slPhysicalProductSelects.add(DomainConstants.SELECT_NAME);
				slPhysicalProductSelects.add(DomainConstants.SELECT_REVISION);
				slPhysicalProductSelects.add(DomainConstants.SELECT_CURRENT);

				Set setAllPhysicalProductIdList = new HashSet();
				setAllPhysicalProductIdList.addAll(slAllPhysicalProductIdList);
				slAllPhysicalProductIdList.clear();
				slAllPhysicalProductIdList.addAll(setAllPhysicalProductIdList);
				
				MapList mlPhysicalProductList = DomainObject.getInfo(context, (String[]) slAllPhysicalProductIdList.toArray(new String []{}), slPhysicalProductSelects);

				Map mapPhysicalProduct;
				String sPhysicalProductName;
				String sPhysicalProductRevision;
				String sPhysicalProductCurrent;
				StringBuilder sbPhysicalProductDetails;
				String sPhysicalProductDetails;
				StringList slNonReleasedPhysicalProductList = new StringList();
				
				for(Object objMap : mlPhysicalProductList)
				{
					mapPhysicalProduct = (Map)objMap;
					sPhysicalProductName = (String)mapPhysicalProduct.get(DomainConstants.SELECT_NAME);
					sPhysicalProductRevision = (String)mapPhysicalProduct.get(DomainConstants.SELECT_REVISION);
					sPhysicalProductCurrent = (String)mapPhysicalProduct.get(DomainConstants.SELECT_CURRENT);					
					sbPhysicalProductDetails = new StringBuilder(sPhysicalProductName).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sPhysicalProductRevision);
					sPhysicalProductDetails = sbPhysicalProductDetails.toString();			
					slAllPhysicalProductList.add(sPhysicalProductDetails);
					if(!pgApolloConstants.STATE_SHARED.equalsIgnoreCase(sPhysicalProductCurrent))
					{
						slNonReleasedPhysicalProductList.add(sPhysicalProductDetails);
					}
				}
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPApplicatorList  slAllPhysicalProductList = "+slAllPhysicalProductList+" slNonReleasedPhysicalProductList"+slNonReleasedPhysicalProductList);

				
				slAllPhysicalProductList.sort();
				
				if(!slCATIAApplicatorList.isEmpty() && !slCATIAApplicatorList.equals(slAllPhysicalProductList))
				{
					if(bErrorCodeMode)
					{
						//E018 - Invalid or missing Applicator Parts
						sReturn = pgApolloConstants.STR_ERROR_CODE_E018;
					}
					else
					{
						sReturn = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(STR_ERROR_MASTERAPPPLICATOR_INVALID).toString();
					}
				}
				else if(!slNonReleasedPhysicalProductList.isEmpty())
				{
					if(bErrorCodeMode)
					{
						//E019 - Applicator Parts are not released
						sReturn = pgApolloConstants.STR_ERROR_CODE_E019;
					}
					else
					{
						sReturn = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(STR_ERROR_APPLICATORS_NOTRELEASED).toString();
					}
				}
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : validateRMPApplicatorList  sReturn = "+sReturn);

				return sReturn;
			}

			
			
		/**
		 * Method to get Connected Representation Instances
		 * @param context
		 * @param domPhysicalProduct
		 * @param sTypePattern
		 * @return
		 * @throws Exception
		 */
		public static MapList getConnectedRepInstances(matrix.db.Context context, DomainObject domPhysicalProduct, String sTypePattern) throws Exception {
			MapList ml3DShapeDrawing = new MapList();
			try
			{	
				StringList busSelects = new StringList();
				busSelects.add(DomainConstants.SELECT_ID);
				busSelects.add(DomainConstants.SELECT_TYPE);
				busSelects.add(DomainConstants.SELECT_NAME);

				StringList relSelects = new StringList();
				relSelects.add(DomainRelationship.SELECT_ID);
				
				ml3DShapeDrawing = domPhysicalProduct.getRelatedObjects(context, pgApolloConstants.RELATIONSHIP_VPMRepInstance, sTypePattern, busSelects, relSelects, false, true, (short)1, null, null, 0);
				ml3DShapeDrawing.sort(DomainConstants.SELECT_TYPE, "ascending", "string");

			}
			catch(Exception ex)
			{
				loggerTrace.error(ex.getMessage(), ex);
				throw ex;
			}
			return ml3DShapeDrawing;
		}

		/**
		 * Method to compare existing and calculated value and return whether value to be updated
		 * @param sCalculatedValue
		 * @param sExistingValue
		 * @return
		 */
		public static boolean preUpdateValidate(String sCalculatedValue, String sExistingValue) 
		{
			BigDecimal bdZero = new BigDecimal("0.0");

			boolean bSkipUpdate  = false;
			boolean bExistingValuePresent = false;
			
			if(UIUtil.isNotNullAndNotEmpty(sExistingValue))
			{
				BigDecimal bdExistingQty= new BigDecimal(sExistingValue);
				if(!bdExistingQty.equals(bdZero))
				{
					bExistingValuePresent = true;
				}
			}	
			
			if(bExistingValuePresent && (UIUtil.isNullOrEmpty(sCalculatedValue) ||  (UIUtil.isNotNullAndNotEmpty(sCalculatedValue) && new BigDecimal(sCalculatedValue).equals(bdZero))))
			{
				bSkipUpdate = true;
			}
			
			return bSkipUpdate;
    }
		
		/**
		 * Method to get Related PickList based on Parent 1 and Parent 2
		 * @param context
		 * @param sPCPName
		 * @param sPTPName
		 * @return
		 * @throws Exception
		 */
		public static StringList getRelatedPickListBasedOnParentNames(matrix.db.Context context, StringList slParent1List, StringList slParent2List, String sParent1Type, String sParent2Type) throws Exception {
			
			StringList slUniqueList = new StringList();
			
			StringBuilder sbUnique;
			
			Map mapParent1List = getPickListObjectIds(context, slParent1List, sParent1Type);
			
			Map mapParent2List = getPickListObjectIds(context, slParent2List, sParent2Type);
			
			String sParent1ObjectId;
			String sParent2ObjectId;
			
			
			
			for(String sParent1Value : slParent1List)
			{
				sParent1ObjectId = (String)mapParent1List.get(sParent1Value);
				
				if(UIUtil.isNotNullAndNotEmpty(sParent1ObjectId))
				{
					for(String sParent2Value : slParent2List)
					{
						sParent2ObjectId = (String)mapParent2List.get(sParent2Value);
						
						if(UIUtil.isNotNullAndNotEmpty(sParent2ObjectId))
						{					
							sbUnique = new StringBuilder();
							sbUnique.append(sParent1ObjectId).append(pgApolloConstants.CONSTANT_STRING_PIPE).append(sParent2ObjectId);
							
							slUniqueList.add(sbUnique.toString());
							
						}
					}
				}						
				
			}
			
			Set setUniqueList = new HashSet();
			setUniqueList.addAll(slUniqueList);
			slUniqueList.clear();
			slUniqueList.addAll(setUniqueList);
			
			return slUniqueList;
		}
		
		
		/**
		 * Method to get PickList objectd list based on picklist names and type
		 * @param context
		 * @param slPicklistNames
		 * @param sPickListType
		 * @return
		 * @throws MatrixException
		 */
		public static Map getPickListObjectIds(Context context, StringList slPicklistNames, String sPickListType, String sPicklistRevision) throws MatrixException 
		{
			Map mapPickList = new HashMap();
			
			String sPickListObjectId;
			boolean bPickListObjectExists = false;
			BusinessObject boPickList;
			
			for(String sPickLitsName : slPicklistNames)
			{
				boPickList = new BusinessObject(sPickListType ,sPickLitsName,sPicklistRevision,pgApolloConstants.VAULT_ESERVICE_PRODUCTION);
				
				bPickListObjectExists = boPickList.exists(context);
				
				sPickListObjectId = DomainConstants.EMPTY_STRING;
				
				if(bPickListObjectExists)
				{
					sPickListObjectId = boPickList.getObjectId(context);
				}
				
				mapPickList.put(sPickLitsName, sPickListObjectId);
			}		

			return mapPickList;
		}
		
		/**
		 * Method to get PickList objectd list based on picklist names and type
		 * @param context
		 * @param slPicklistNames
		 * @param sPickListType
		 * @return
		 * @throws MatrixException
		 */
		public static Map getPickListObjectIds(Context context, StringList slPicklistNames, String sPickListType) throws MatrixException 
		{
			Map mapPickList = new HashMap();
			
			String sPickListObjectId;
			boolean bPickListObjectExists = false;
			BusinessObject boPickList;
			
			for(String sPickLitsName : slPicklistNames)
			{
				boPickList = new BusinessObject(sPickListType ,sPickLitsName,"-",pgApolloConstants.VAULT_ESERVICE_PRODUCTION);
				
				bPickListObjectExists = boPickList.exists(context);
				
				sPickListObjectId = DomainConstants.EMPTY_STRING;
				
				if(bPickListObjectExists)
				{
					sPickListObjectId = boPickList.getObjectId(context);
				}
				
				mapPickList.put(sPickLitsName, sPickListObjectId);
			}		

			return mapPickList;
		}

		/**
		 * Method to get Related PTC list based on PCP Name and PTP Name
		 * @param context
		 * @param slPCPName
		 * @param slPTPName
		 * @return
		 * @throws Exception
		 */
		public static StringList getRelatedPTCListBasedOnParentNames(matrix.db.Context context, StringList slPCPName, StringList slPTPName) throws Exception 
		{
			StringList slPTCNameList;
			
			MapList mlPTCList  = getRelatedPTCBasedOnParentNames(context, slPCPName, slPTPName);

			slPTCNameList = new ChangeUtil().getStringListFromMapList(mlPTCList, DomainConstants.SELECT_NAME);
			
			return slPTCNameList;

		}
		
		/**
		 * Method to get Related PTC list based on PCP Name and PTP Name
		 * @param context
		 * @param slPCPName
		 * @param slPTPName
		 * @return
		 * @throws Exception
		 */
		public static MapList getRelatedPTCBasedOnParentNames(matrix.db.Context context, StringList slPCPName, StringList slPTPName) throws Exception 
		{
			MapList mlReturnList = new MapList();
			
			MapList mlLocalList;
			
			if(null != slPCPName && !slPCPName.isEmpty() && null != slPTPName && !slPTPName.isEmpty())
			{
				StringList slUniqueList = getRelatedPickListBasedOnParentNames(context, slPCPName, slPTPName, pgApolloConstants.TYPE_PG_PLI_PRODUCTCATEGORYPLATFORM, pgApolloConstants.TYPE_PG_PLI_PRODUCTTECHNOLOGYPLATFORM);
								
				String sPCPObjectId;
				String sPTPObjectId;
				StringList slParentList;
				String sObjectId;
				Map mapLocal;
				StringList slUniqueObjectIdList = new StringList();
				
				for(String sUnique : slUniqueList)
				{
					slParentList = StringUtil.split(sUnique, pgApolloConstants.CONSTANT_STRING_PIPE);
					
					if(null != slParentList && !slParentList.isEmpty() && slParentList.size() > 1)
					{
						sPCPObjectId = slParentList.get(0);
						sPTPObjectId = slParentList.get(1);
						
						mlLocalList = pgPLUtil.getRelatedPTC(context, sPCPObjectId, sPTPObjectId);
						
						for(Object objMap : mlLocalList)
						{
							mapLocal = (Map)objMap;
							sObjectId = (String)mapLocal.get(DomainConstants.SELECT_ID);
							if(UIUtil.isNotNullAndNotEmpty(sObjectId) && !slUniqueObjectIdList.contains(sObjectId))
							{
								slUniqueObjectIdList.add(sObjectId);
								mlReturnList.add(mapLocal);
							}
						}
					}
					
				}
				
				mlReturnList.sort(DomainConstants.SELECT_NAME, "ascending", "string");

			}			
			
			return mlReturnList;
		}
		
		/**
		 * Method to get Related PTC list based on PCP Name and PTP Name
		 * @param context
		 * @param sPCPName
		 * @param sPTPName
		 * @return
		 * @throws Exception
		 */
		public static MapList getRelatedPTCBasedOnParentNames(matrix.db.Context context, String sPCPName, String sPTPName) throws Exception {
			
			MapList mlPTCList = new MapList();

			BusinessObject plPCPObject = new BusinessObject(pgApolloConstants.TYPE_PG_PLI_PRODUCTCATEGORYPLATFORM ,sPCPName,"-",pgApolloConstants.VAULT_ESERVICE_PRODUCTION);
			
			boolean bPCPObjectExists = plPCPObject.exists(context);
			
			String sPCPObjectId = DomainConstants.EMPTY_STRING;
			
			if(bPCPObjectExists)
			{
				sPCPObjectId = plPCPObject.getObjectId(context);
			}

			BusinessObject plPTPObject = new BusinessObject(pgApolloConstants.TYPE_PG_PLI_PRODUCTTECHNOLOGYPLATFORM ,sPTPName,"-",pgApolloConstants.VAULT_ESERVICE_PRODUCTION);

			boolean bPTPObjectExists = plPTPObject.exists(context);
			
			String sPTPObjectId = DomainConstants.EMPTY_STRING;
			
			if(bPTPObjectExists)
			{
				sPTPObjectId = plPTPObject.getObjectId(context);
			}

			if(UIUtil.isNotNullAndNotEmpty(sPCPObjectId) && UIUtil.isNotNullAndNotEmpty(sPTPObjectId))
			{
				mlPTCList = pgPLUtil.getRelatedPTC(context, sPCPObjectId, sPTPObjectId);
			}

			return mlPTCList;
			
		}

		/**
		 * Method to get Dash board URL based on Widget Name and Tab in it
		 * @param context
		 * @param args
		 * @return
		 * @throws Exception
		 */
		public static String getDashboardURL(Context context, String[] args) throws Exception
		{					
			Map programMap = (HashMap) JPO.unpackArgs(args);	

			String sObjectId = (String) programMap.get("objectId");
			
			String sWidgetName = (String) programMap.get("widgetName");
			
			String sTabName = (String) programMap.get("tabName");
			
			String sPhysicalId = DomainObject.newInstance(context, sObjectId).getInfo(context, DomainConstants.SELECT_PHYSICAL_ID);
			
			String sEnvDashboardURL = EnvPropertyUtil.getIFWEURL(context);
			context.printTrace(pgApolloConstants.TRACE_LPD, "\n pgApolloCommonUtil : getDashboardURL ENV sDashboardURL = "+sEnvDashboardURL);
			String sDashboardURL = getPGDashboardURL(context);
			context.printTrace(pgApolloConstants.TRACE_LPD, "\n pgApolloCommonUtil : getDashboardURL PG Specific sDashboardURL = "+sDashboardURL);

			StringBuilder sbURL = new StringBuilder(sDashboardURL).append("/#app:").append(getWidgetAppIdFromDisplayName(context,sWidgetName))
					.append("/content:contentId=").append(sPhysicalId)
					.append("&tabName="+sTabName);
		
			return sbURL.toString();
    }
		
		/**
		 * Method to revise Part
		 * @param context
		 * @param sObjectId
		 */
		public static String revisePart(matrix.db.Context context, String sObjectId) 
		{
			String sRevisedObjectId = DomainConstants.EMPTY_STRING;
			try
			{
				Map mapRevisedObjectMap = ExtractDataForDesignTool.getRevisedVPMRefId(context, sObjectId);
				if(mapRevisedObjectMap.containsKey(pgApolloConstants.STR_ERROR))
				{
					sRevisedObjectId = (String)mapRevisedObjectMap.get(pgApolloConstants.STR_ERROR);
				}
				else if(mapRevisedObjectMap.containsKey(DomainConstants.SELECT_ID))
				{
					sRevisedObjectId = (String)mapRevisedObjectMap.get(DomainConstants.SELECT_ID);
				}
			}
			catch (Exception e) 
			{
				sRevisedObjectId = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
				loggerTrace.error(e.getMessage(), e);
			}

			return sRevisedObjectId;
		}
		
		/**
		 * Method to write XLSX file
		 * @param context
		 * @param mlXLSOutput
		 * @param slHeaderList
		 * @param slSelectList
		 * @param sFileName
		 * @param sReportFile
		 * @param slOwnerCoOwnerEmailList 
		 * @throws Exception
		 */
		public static void writeXLSXFile(Context context, MapList mlXLSOutput, StringList slHeaderList, StringList slSelectList,	String sFileName, String sReportFile) throws Exception
		{
			try
			{
				XSSFWorkbook wb = new XSSFWorkbook();
				XSSFSheet sheet = wb.createSheet(sFileName);

				writeSheetForOutputSummary(context,wb, sheet, mlXLSOutput, slSelectList, slHeaderList);

				writeXLSFileStream(sReportFile, wb);

			} 
			catch (Exception e) 
			{				
				loggerTrace.error(e.getMessage(), e);
				throw e;
			}
		}

		/**
		 * Method to write XLS File stream
		 * @param sReportFile
		 * @param wb
		 */
		public static void writeXLSFileStream(String sReportFile, XSSFWorkbook wb)
		{
			try(FileOutputStream fileOut = new FileOutputStream(sReportFile);) 
			{
				wb.write(fileOut);				
			} 
			catch (IOException e) 
			{				
				loggerTrace.error(e.getMessage(), e);
			}
		}
		
		/**
		 * Method to write XLS sheet for output summary
		 * @param context
		 * @param wb
		 * @param sheet
		 * @param mlXLSOutput
		 * @throws Exception
		 */
		public static void writeSheetForOutputSummary(Context context, XSSFWorkbook wb, XSSFSheet sheet, MapList mlXLSOutput, StringList slSelectList, StringList slHeaderList) throws Exception 
		{
			try {
				// Create header CellStyle
				Font headerFont = wb.createFont();
				headerFont.setColor(IndexedColors.BLACK.index);
				CellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
				// fill foreground color ...
				headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index);
				headerCellStyle.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.index);
				// and solid fill pattern produces solid grey cell fill
				headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				headerCellStyle.setFont(headerFont);		
				

				int rowCount = 0;
				Row rowHeader = sheet.createRow(rowCount);
				rowCount = rowCount+ 1;
				int iCount = 0;
				Cell cell;

				for(String sHeader : slHeaderList)
				{
					cell = rowHeader.createCell(iCount);
					cell.setCellType(CellType.STRING);
					cell.setCellValue(sHeader);
					cell.setCellStyle(headerCellStyle);
					iCount+=1;
				}

				Map mapPart;
				Row rowData;

				CellStyle cs = wb.createCellStyle();
				cs.setWrapText(true);
				cs.setBorderRight(BorderStyle.THIN); // single line border
				cs.setBorderBottom(BorderStyle.THIN);	
						

				for(Object objMap : mlXLSOutput)
				{
					mapPart = (Map)objMap;
					rowData = sheet.createRow(rowCount);
					iCount = 0;
					for(String sSelect : slSelectList)
					{
						cell = rowData.createCell(iCount);
						cell.setCellType(CellType.STRING);
						cell.setCellValue((String) mapPart.getOrDefault(sSelect,DomainConstants.EMPTY_STRING));
						cell.setCellStyle(cs);
						iCount+=1;
					}
					rowCount+=1;
				}
			} catch (Exception e) {
				loggerTrace.error(e.getMessage(), e);
				throw e;
			}

		}
		
			/**
		    * Method to form PG Specific dashboard url
		    * @param context
		    * @param strBaseUrl
		    * @return
		    * @throws FrameworkException 
		    */
		   public static String getPGDashboardURL(Context context) throws FrameworkException {
			   String strBaseURL = MailUtil.getBaseURL(context);
			   if(UIUtil.isNullOrEmpty(strBaseURL))
			   {
				   strBaseURL = EnoviaResourceBundle.getProperty(context,"emxCPN.BaseURL");
			   }
			   StringBuilder sbDashboardURL = new StringBuilder(strBaseURL);
			   if (BusinessUtil.isNotNullOrEmpty(strBaseURL)) {
				   String[] arrURL = strBaseURL.split(".pg.com");
				   sbDashboardURL = new StringBuilder(arrURL[0]).append("-3dd.pg.com/3ddashboard");
			   }
			   return sbDashboardURL.toString();
		   }
		   
		   
		   /**
		    * Method to get Widget App Id from display name
		    * @param context
		    * @param strAppDisplayName
		    * @return
		    * @throws FrameworkException
		    */
		   public static String getWidgetAppIdFromDisplayName(Context context, String strAppDisplayName) throws FrameworkException {
				String strAppName = null;
				String strWhere = new StringBuilder("attribute[").append(pgApolloConstants.ATTRIBUTE_APP_DISPLAY_NAME).append("]=='").append(strAppDisplayName).append("'").toString();

				MapList mlList = DomainObject.findObjects(context, pgApolloConstants.TYPE_APP_DEFINITION, // type pattern
						pgApolloConstants.VAULT_ESERVICE_PRODUCTION, // vaultPattern
						strWhere, // where clause
						new StringList(DomainConstants.SELECT_NAME)); // objectSelects

				if(mlList != null && !mlList.isEmpty()) {
					Map<String, String> map = (Map) mlList.get(0);
					strAppName = (String) map.get(DomainConstants.SELECT_NAME);
				}
				return strAppName;
			}
		  /**
		   * Method to delete Files in Non-Supported CDM Objects
		   * @param context
		   * @param sObjectId
		   * @param sFileNameOrPrefix
		   * @param sFileFormat
		   * @throws Exception
		   */
		   public static void deleteNonSupportedCDMObjectFile(Context context,  String sObjectId, String sFileNameOrPrefix, String sFileFormat) throws Exception
		   {
			   deleteNonSupportedCDMObjectFile(context, sObjectId,  sFileNameOrPrefix, sFileFormat, DomainConstants.EMPTY_STRING);
		   }
		   
		  /**
		   * Method to delete Files in Non-Supported CDM Objects
		   * @param context
		   * @param sObjectId
		   * @param sFileNameOrPrefix
		   * @param sFileFormat
		   * @throws Exception
		   */
		   public static void deleteNonSupportedCDMObjectFile(Context context,  String sObjectId, String sFileNameOrPrefix, String sFileFormat, String sFileNameSuffix) throws Exception
		   {
			   try {
				   String sFileName;
				   context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : deleteNonSupportedCDMObjectFile >>");

				   if(UIUtil.isNotNullAndNotEmpty(sObjectId))
				   {
					   DomainObject domObject = DomainObject.newInstance( context, sObjectId);
					   FileList files = domObject.getFiles(context, sFileFormat);
					   
					   context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : deleteNonSupportedCDMObjectFile files ="+files);

					   if(files!=null && !files.isEmpty())
					   {
						   for(matrix.db.File f : files)
						   {
							   sFileName = f.getName();
							   if(UIUtil.isNotNullAndNotEmpty(sFileName) && sFileName.startsWith(sFileNameOrPrefix) && (UIUtil.isNullOrEmpty(sFileNameSuffix) || sFileName.endsWith(sFileNameSuffix)))
							   {
								   deleteBusinessObjectFile(context, sObjectId, sFileName, sFileFormat);
							   }
						   }			
					   }
				   }
			   } catch (Exception e) {
				   loggerTrace.error(e.getMessage(), e);
				   throw e;
			   }
		   }

		/**
		 * Method to delete Business Object File
		 * @param context
		 * @param sObjectId
		 * @param sFileFormat
		 * @param sFileName
		 * @throws MatrixException
		 */
		public static void deleteBusinessObjectFile(Context context, String sObjectId, String sFileName , String sFileFormat) throws MatrixException
		{
			if(UIUtil.isNotNullAndNotEmpty(sObjectId) && UIUtil.isNotNullAndNotEmpty(sFileName))
			{
				//There is no API for deletion of file for Non-Common Document Object, so need to use MQLCommand
				MQLCommand mqlCommand = new MQLCommand();

				boolean bSuccess = false;
				
				String sCommand = "delete businessobject $1 format $2 file $3";

				String[] args = new String[3];
				args[0]= sObjectId;
				args[1]= sFileFormat;
				args[2]= sFileName;

				context.printTrace(pgApolloConstants.TRACE_LPD, "Following File getting deleted ="+sFileName);

				//There is no API for deletion of file for Non-Common Document Object, so need to use MQLCommand
				bSuccess = mqlCommand.executeCommand(context, sCommand, args);

				context.printTrace(pgApolloConstants.TRACE_LPD, "File Deletion Status ="+bSuccess);
			}
			
		}
		   
		/**
		 * Method to Delete LPD Files from APP
		 * @param context
		 * @param sPartId
		 * @throws MatrixException
		 */
		public static void deleteLPDFiles(Context context, String sPartId)	throws  MatrixException {
			
			if(UIUtil.isNotNullAndNotEmpty(sPartId))
			{
				String sFileName;
				DomainObject domObject = DomainObject.newInstance( context, sPartId);
				FileList files = domObject.getFiles(context, pgApolloConstants.FORMAT_NOT_RENDERABLE);

				if(files!=null && !files.isEmpty())
				{
					for(matrix.db.File f : files)
					{
						sFileName = f.getName();
						
						context.printTrace(pgApolloConstants.TRACE_LPD, "File Name To Check ="+sFileName);

						if(UIUtil.isNotNullAndNotEmpty(sFileName) && (sFileName.contains(pgApolloConstants.STR_AUTOMATION_DESIGN_PARAMETER_FILE_NAME) || isMSPRFile(sFileName)))
						{
							deleteBusinessObjectFile(context, sPartId, sFileName, pgApolloConstants.FORMAT_NOT_RENDERABLE);
							
							context.printTrace(pgApolloConstants.TRACE_LPD, "File Deletion Successful for ="+sPartId+" "+sFileName);

						}
					}			
				}
			}
			
		}
		   
	   /**
		 * Method to compare to check Parent List contains child list - ignore case
		 * @param slElementsParentList
		 * @param slElementsToCheck
		 * @return
		 */
		public static boolean containAnyMatchWithIgnoreCase(StringList slElementsParentList, StringList slElementsToCheck)
		{
			boolean bAnyMatch = false;
			
			for(String sElementToCheck : slElementsToCheck)
			{
				if(pgApolloCommonUtil.containsInListCaseInsensitive(sElementToCheck, slElementsParentList))
				{
					bAnyMatch = true;
					break;
				}
			}
			
			return bAnyMatch;
		}
		
		/**
		 * Method to prepare Attribute Mapping Based On Configuration
		 * @param context
		 * @param sPageFileName
		 * @param sKeyName
		 * @return
		 * @throws Exception
		 */
		public static Map prepareAttributeMappingBasedOnConfiguration(Context context, String sPageFileName, String sKeyName, boolean bReverse) throws Exception
		{
			Map mapAttributeMapping = new HashMap();

			String sAttibuteMapping = pgApolloCommonUtil.getPageProperty(context, sPageFileName, sKeyName);
			StringList slAttributeMapping = new StringList();
			if(UIUtil.isNotNullAndNotEmpty(sAttibuteMapping))
			{
				slAttributeMapping	= StringUtil.split(sAttibuteMapping, pgApolloConstants.CONSTANT_STRING_PIPE);
			}
			
			StringList slMappingList;
			
			for(String sMapping : slAttributeMapping) {
				slMappingList =  StringUtil.split(sMapping, pgApolloConstants.CONSTANT_STRING_COLON);
				if(null != slMappingList && !slMappingList.isEmpty() && slMappingList.size() > 1)
				{
					if(bReverse)
					{
						mapAttributeMapping.put(slMappingList.get(1), slMappingList.get(0));
					}
					else
					{
						mapAttributeMapping.put(slMappingList.get(0), slMappingList.get(1));
					}
				}
			}
			
			return mapAttributeMapping;
		}
		
		

		/**
		 * Method to filter and prepare data - Input Map is converted to Map to be set by DomainObject based on attribute mapping and available keys in Input Map
		 * @param context
		 * @param mapInput
		 * @param mapAttributeMapping
		 * @return
		 */
		public static Map filterAndPrepareMapBasedOnMapping(Context context, Map mapInput, Map mapAttributeMapping, boolean bReplaceKey, boolean includeEmpty)
		{
			Map<String, Object> mapOutput = new HashMap();
			
			Set<String> setKeys = mapAttributeMapping.keySet();
			
			StringList slValueList;
			Object objectValue;
			String sNewKey;			
			String sValue;
			StringList slobjectValueList;

			for(String sKey : setKeys)
			{
				sNewKey = (String)mapAttributeMapping.get(sKey);
				objectValue = mapInput.get(sKey);

				if(UIUtil.isNotNullAndNotEmpty(sNewKey) && null != objectValue)
				{
					if(objectValue instanceof StringList)
			        {
						slValueList = (StringList)objectValue;
						
						slValueList = removeEmptyStringFromStringList(slValueList);
						
						if(null != slValueList && (slValueList.isEmpty() && includeEmpty) || !slValueList.isEmpty())
						{
							if(slValueList.isEmpty())
							{
								if(!bReplaceKey)
								{
									mapOutput.put(sKey, new StringList());
								}
								mapOutput.put(sNewKey, new StringList());	
							}
							else
							{
								if(!bReplaceKey)
								{
									slobjectValueList = (StringList)objectValue;
									slobjectValueList.sort();
									mapOutput.put(sKey, slobjectValueList);
								}
								slobjectValueList = (StringList)objectValue;
								slobjectValueList.sort();
								mapOutput.put(sNewKey, (StringList)objectValue);	
							}
							
						}	
						else
						{
							mapOutput.put(sNewKey, new StringList());	
						}
			        }
			        else
			        {
			        	sValue = (String)objectValue;
			        	if(null != sValue && (sValue.isEmpty() && includeEmpty) || !sValue.isEmpty())
						{
			        		if(sValue.isEmpty())
			        		{
			        			sValue = DomainConstants.EMPTY_STRING;
			        		}
			        		
			        		sValue = sValue.trim();
			        		
			        		if(!bReplaceKey)
							{
				        		mapOutput.put(sKey, sValue);
							}
							mapOutput.put(sNewKey, sValue);
						}
			        							
			        }
				}
				
			}
			
			mapOutput = sortbykey(mapOutput);
			
			return mapOutput;
		}
		/**
		 * Method to remove empty String from string list
		 * @param criteria1ValueList
		 * @return
		 */
		public static StringList removeEmptyStringFromStringList(StringList slValueList) 
		{
			if(null != slValueList && slValueList.contains(DomainConstants.EMPTY_STRING))
			{
				slValueList.remove(DomainConstants.EMPTY_STRING);
			}		
			return slValueList;
		}
		/**
		 * Method to get Object Map
		 * @param context
		 * @param sObjectName
		 * @param sObjectRevision
		 * @return
		 * @throws Exception
		 */
		
		public static Map getLatestRevisionMap(Context context, String sTypePattern, String sObjectName, String sObjectRevision) throws Exception 
		{
			Map mapObject;
			String sObjectPhysicalId;
			
			String sWhere = DomainConstants.EMPTY_STRING;
			
			if(DomainConstants.QUERY_WILDCARD.equalsIgnoreCase(sObjectRevision) || sObjectRevision == null)
			{
				sWhere = new StringBuilder(pgApolloConstants.SELECT_ISLAST).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(pgApolloConstants.STR_TRUE_FLAG).toString();
			}			

			StringList slObjectSelects = new StringList();
			slObjectSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
			slObjectSelects.add(DomainConstants.SELECT_TYPE);
			slObjectSelects.add(DomainConstants.SELECT_NAME);
			slObjectSelects.add(DomainConstants.SELECT_CURRENT);
			slObjectSelects.add(DomainConstants.SELECT_POLICY);
			slObjectSelects.add(pgApolloConstants.SELECT_ISLAST);

			mapObject = getLatestRevisionMap(context, sTypePattern, sObjectName, sObjectRevision, sWhere, slObjectSelects);
			
			return mapObject;
		}
			
		
		/**
		 * Method to get JSON Object from Map
		 * @param jsonMap
		 * @return
		 * @throws IOException
		 */
		public static JsonObject getJSONfromMap(Map<String,Object> jsonMap) throws IOException {
			String sKey;
			Object object;
			JsonObjectBuilder jObjBuilder = Json.createObjectBuilder();

			for (Map.Entry<String,Object> entry : jsonMap.entrySet())
			{
				JsonArrayBuilder arrBuilder = null;
				JsonObject jsonObject = null;
				sKey = entry.getKey();				
				object = entry.getValue();			
				
				switch(object.getClass().getSimpleName()){
				case "StringList":
					arrBuilder = parseObjectToJson(sKey, object);
					jObjBuilder.add(sKey, arrBuilder);
					break;
				case "MapList":
					arrBuilder = parseObjectToJson(sKey, object);
					jObjBuilder.add(sKey, arrBuilder);
					break;
				case "HashMap":
					jsonObject = getJSONfromMap((Map)object);
					jObjBuilder.add(sKey, jsonObject);
					break;
				case "TreeMap":
					jsonObject = getJSONfromMap((Map)object);
					jObjBuilder.add(sKey, jsonObject);
					break;
				case "Integer":
					jObjBuilder.add(sKey, Integer.toString((int)object));
					break;
				case "String":					
					jObjBuilder.add(sKey, (String)object);
					break;
				case "Boolean":
					jObjBuilder.add(sKey, (boolean)object);
					break;
				default:
					jObjBuilder.add(sKey, Json.createObjectBuilder().build());
				}                    
			}
			return jObjBuilder.build();
		}		


		/**
		 * Method to parse the object and convert the object accordingly
		 * @param sKey
		 * @param object
		 * @return
		 * @throws IOException
		 */
		public static JsonArrayBuilder parseObjectToJson(String sKey, Object object) throws IOException 
		{
			JsonArrayBuilder jArrBuilder = Json.createArrayBuilder();

			if(object instanceof StringList)
			{
				StringList sl = (StringList) object;


				int len = sl.size();
				for (int i=0;i<len;i++){ 
					jArrBuilder.add(sl.get(i));
				} 

			}
			else
			{
				JsonObjectBuilder job = Json.createObjectBuilder();
				MapList ml = (MapList) object;
				
				for(Object ele: ml) {
					Map m = (Map) ele;
					JsonObject jo = getJSONfromMap(m);
					jArrBuilder.add(jo);
				}
			}	
			return jArrBuilder;
		}
		
		/**
		 * Method to convert Maplist to JSON Array
		 * @param mapList
		 * @return
		 * @throws IOException
		 */
		public static JsonArray getJsonArrayFromMapList(MapList mapList) throws IOException {
			JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

			try {
				for (Object element : mapList) {
					if (element instanceof Map) {
						Map map = (Map) element;
						JsonObject jsonObject = getJSONfromMap(map);
						jsonArrayBuilder.add(jsonObject);
					}
				}
			} catch (Exception e) {
				loggerTrace.error(e.getMessage(), e);
				throw e;
			}
			return jsonArrayBuilder.build();
		}
		
		/**
		 * Method to convert database date to Widget display date
		 * @param sDate
		 * @return
		 */
		public static String parseDate(String sDatabaseDate) 
		{
			SimpleDateFormat sdFormatter = new SimpleDateFormat(eMatrixDateFormat.strEMatrixDateFormat, Locale.US);		
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			Date date;
			String sDateView = DomainConstants.EMPTY_STRING;
			try {
				if(UIUtil.isNotNullAndNotEmpty(sDatabaseDate))
				{
					Date dDateObject = sdFormatter.parse(sDatabaseDate);
					sDateView = sdf.format(dDateObject);
				}
				
			} catch (Exception e) {
				loggerTrace.error(e.getMessage(), e);
			}

			return sDateView;
		}
		/**
		 * Method to sort Map by key
		 * @param map
		 */
	    public static Map<String, Object> sortbykey(Map map)
	    {
	        TreeMap<String, Object> sortedMap = new TreeMap<>();
	        sortedMap.putAll(map); 
	        for (Map.Entry<String, Object> entry : sortedMap.entrySet());
	        return sortedMap;
	    }
		/**
		 * Method to send email to owner
		 * @param context
		 * @param sBackgroundJobName
		 * @param sReportFile
		 * @param mapBackgroundJob
		 * @param bBackgroundJobError
		 * @param sMessageKey
		 * @param sSubjectKey
		 * @return
		 * @throws Exception
		 */
		public static String sendMail(Context context, String sBackgroundJobName, String sReportFile, Map mapBackgroundJob, boolean bBackgroundJobError, String sMessageKey, String sSubjectKey) throws Exception 
		{	
			String sResponse = pgApolloConstants.STR_SUCCESS;
			try 
			{
				String sBackgroundJobStatus = pgApolloConstants.STR_SUCCESS;
				if(bBackgroundJobError)
				{
					sBackgroundJobStatus = pgApolloConstants.STR_FAILED;
				}			
				String sOwner = (String)mapBackgroundJob.get(DomainConstants.SELECT_OWNER);
				
				StringBuilder sbMailMessage = new StringBuilder(MessageUtil.getMessage(context, null, sMessageKey,new String[] {sBackgroundJobName}, null, MessageUtil.getLocale(context),pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME));
				StringBuilder sbMailSubject = new StringBuilder(MessageUtil.getMessage(context, null, sSubjectKey,new String[] {sBackgroundJobStatus, sBackgroundJobName}, null, MessageUtil.getLocale(context),pgApolloConstants.STR_CPN_STRING_RESOURCE_FILENAME));

				String[] arguments = new String[7];
				arguments[0] = PropertyUtil.getEnvironmentProperty(context,"MX_SMTP_HOST");//host
				arguments[1] = PersonUtil.getEmail(context,pgApolloConstants.PERSON_USER_AGENT);//from user
				arguments[2] = PersonUtil.getEmail(context, sOwner);//to user
				arguments[3] = sReportFile;//attached files path
				arguments[4] = sbMailSubject.toString();//mail subject
				arguments[5] = pgApolloConstants.CONSTANT_STRING_NEWLINE + sbMailMessage.toString();//mail body
				arguments[6] = pgApolloConstants.PERSON_PLMADMIN;//from user
				

				sResponse = pgApolloCommonUtil.sendEmailToUser(arguments);
				

			} 
			catch (Exception e) 
			{
				sResponse = new StringBuilder(pgApolloConstants.STR_ERROR).append(pgApolloConstants.CONSTANT_STRING_COLON).append(e.getLocalizedMessage()).toString();
				loggerTrace.error(e.getMessage(), e);
			}

			return sResponse;
		}

	/**
		 * Method to check if Value is Null or Empty or Zero
		 * @param sValue
		 * @return
		 */
		public static boolean isNullOrEmptyOrZero(String sValue)
		{
			return UIUtil.isNullOrEmpty(sValue) 
			|| (UIUtil.isNotNullAndNotEmpty(sValue) && NumberUtils.isCreatable(sValue) && Double.parseDouble(sValue) == 0.0);
		}	
		
		/**
		 * Method to fetch Value of Parameter - Added for Query with UOM
		 * @param strQuantity
		 * @return
		 * @throws Exception
		 */
		public static String getParameterValue(String strParameterValue) throws Exception {
			
			try
			{
				if(UIUtil.isNotNullAndNotEmpty(strParameterValue))
				{
					StringList strList = new StringList();
					if(strParameterValue.contains(pgApolloConstants.CONSTANT_STRING_PIPE))
					{
						strList = FrameworkUtil.split(strParameterValue, pgApolloConstants.CONSTANT_STRING_PIPE);
						if(null!=strList && !strList.isEmpty())
						{
							strParameterValue = (String)strList.get(0);
						}
					}				
					strParameterValue = getValueWithoutScientificNotations(strParameterValue);
				}
			}
			catch(Exception ex)
			{
				loggerTrace.error(ex.getMessage(), ex);
				throw ex;
			}
			return strParameterValue;
		}
		
		/**
		 * Method to convert Value of Scientific value to plain string
		 * @param strNumber1
		 * @return
		 */
		public static String getValueWithoutScientificNotations(String strNumber) {
			try
			{
				if(UIUtil.isNotNullAndNotEmpty(strNumber) && strNumber.contains("E") || strNumber.contains("e"))
				{
					BigDecimal bd = new BigDecimal(strNumber);
					strNumber = bd.toPlainString();
				}
			}
			catch(Exception ex)
			{
				loggerTrace.error(ex.getMessage(), ex);
				throw ex;
			}
			return strNumber;
		}
		
		/**
		 * Method to divide values
		 * @param dividend
		 * @param divisor
		 * @return
		 * @throws NumberFormatException
		 * @throws ArithmeticException
		 */
		public static String divideValues(String dividend, String divisor) throws NumberFormatException, ArithmeticException
		{
			if(UIUtil.isNullOrEmpty(dividend))
			{
				dividend = "0.0";
			}
			if(UIUtil.isNullOrEmpty(divisor))
			{
				divisor = "0.0";
			}
	        BigDecimal num1 = new BigDecimal(dividend);
	        BigDecimal num2 = new BigDecimal(divisor);

	        if (num2.compareTo(BigDecimal.ZERO) == 0) {
	        	return "0.0"; // Returning Zero if Divisor is zero
	        }

	        BigDecimal result = num1.divide(num2, MathContext.DECIMAL64);
	        
	        return result.toString();
	    }

		
		/**
		 * Method to update Attribute Map
		 * @param context 
		 * @param attributeMap
		 * @param sExistingValue
		 * @param sCalculatedValue
		 * @param sKey
		 * @return
		 * @throws MatrixException 
		 */
		public static Map updateAttributeMap(matrix.db.Context context, Map attributeMap, String sExistingValue, String sCalculatedValue, String sKey) throws MatrixException
		{
			context.printTrace(pgApolloConstants.TRACE_LPD,  "pgApolloCommonUtil updateAttributeMap sExistingValue = " + sExistingValue+"  sCalculatedValue = "+ sCalculatedValue +" sKey = "+sKey );

			boolean bSkipUpdateValue = pgApolloCommonUtil.preUpdateValidate(sCalculatedValue, sExistingValue);
			
			context.printTrace(pgApolloConstants.TRACE_LPD,  "pgApolloCommonUtil updateAttributeMap bUpdateValue = " + bSkipUpdateValue );

			if(!bSkipUpdateValue)
			{
				attributeMap.put(sKey, sCalculatedValue);
			}	
			
			context.printTrace(pgApolloConstants.TRACE_LPD,  "pgApolloCommonUtil updateAttributeMap attributeMap = " + attributeMap );

			return attributeMap;
		} 
		
		/**
		 * Method to validate pick list and return validation results
		 * @param context
		 * @param mapNewInputData
		 * @param sPickListType
		 * @param slPickListValues
		 * @return
		 * @throws Exception
		 */
		public static Map validateAndGetPicklistItem(Context context, Map mapNewInputData, String sPickListType, StringList slPickListValues) throws Exception
		{

			if(null != slPickListValues && !slPickListValues.isEmpty())
			{
				if(pgApolloConstants.STR_AUTOMATION_BUSINESSAREA.equals(sPickListType))
				{
					String sType = pgApolloConstants.TYPE_PG_PLI_BUSINESSAREA;
					String sAttributeName = pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA;

					MapList mlObjectList = getPickListMapList(context, slPickListValues, sType);

					StringList slObjectNameList = new ChangeUtil().getStringListFromMapList(mlObjectList, DomainConstants.SELECT_NAME);

					StringList slObjectIdList = new ChangeUtil().getStringListFromMapList(mlObjectList, DomainConstants.SELECT_ID);

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, sAttributeName);				

				}
				else if(pgApolloConstants.STR_AUTOMATION_PRODUCTCATEGORYPLATFORM.equals(sPickListType) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA))
				{
					StringList slParentIdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA+"_id");
					Map programMap = new HashMap();
					programMap.put("selectedPlatformList",slParentIdList);
					programMap.put("strgetAttrObject", pgApolloConstants.STR_PRODUCT_CATEGORY_PLATFORM);
					String[] methodargs = JPO.packArgs(programMap);
					String sType = "PlatformToBusinessArea";

					Map mapPicklistValues = getProductFormPlatform(context, slPickListValues, methodargs, sType);

					StringList slObjectNameList =  (StringList) mapPicklistValues.get("field_display_choices");

					StringList slObjectIdList =  (StringList) mapPicklistValues.get("field_choices");

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);				

				}
				else if(pgApolloConstants.STR_AUTOMATION_PRODUCTTECHNOLOGYPLATFORM.equals(sPickListType) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM))
				{
					StringList slParentIdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM+"_id");
					Map programMap = new HashMap();
					programMap.put("selectedPlatformList",slParentIdList);
					programMap.put("strgetAttrObject", pgApolloConstants.STR_PRODUCT_TECHNOLOGY_PLATFORM);
					String[] methodargs = JPO.packArgs(programMap);
					String sType = "Platform";

					Map mapPicklistValues = getProductFormPlatform(context, slPickListValues, methodargs, sType);

					StringList slObjectNameList =  (StringList) mapPicklistValues.get("field_display_choices");

					StringList slObjectIdList =  (StringList) mapPicklistValues.get("field_choices");

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM);				

				}
				else if(pgApolloConstants.STR_AUTOMATION_PRODUCTTECHNOLOGYCHASSIS.equals(sPickListType) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM) &&   mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM))
				{
					StringList slParent1IdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTCATEGORYPLATFORM);

					StringList slParent2IdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYPLATFORM);
					
					MapList mlPTCList  = pgApolloCommonUtil.getRelatedPTCBasedOnParentNames(context, slParent1IdList, slParent2IdList);

					StringList slObjectNameList = new ChangeUtil().getStringListFromMapList(mlPTCList, DomainConstants.SELECT_NAME);
					
					StringList slObjectIdList = new ChangeUtil().getStringListFromMapList(mlPTCList, DomainConstants.SELECT_ID);

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS);				


				}
				else if(pgApolloConstants.STR_AUTOMATION_SIZE.equals(sPickListType) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS))
				{
					StringList slParentIdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGPRODUCTTECHNOLOGYCHASSIS+"_id");
					Map programMap = new HashMap();
					programMap.put("selectedPlatformList",slParentIdList);
					programMap.put("strgetAttrObject", "Product Size");
					String[] methodargs = JPO.packArgs(programMap);
					String sType = pgApolloConstants.STR_PRODUCT_SIZE;

					Map mapPicklistValues = getProductFormPlatform(context, slPickListValues, methodargs, sType);

					StringList slObjectNameList =  (StringList) mapPicklistValues.get("field_display_choices");

					StringList slObjectIdList =  (StringList) mapPicklistValues.get("field_choices");

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, pgApolloConstants.ATTRIBUTE_PG_DSMPRODUCTSIZE);				

				}
				else if(pgApolloConstants.STR_AUTOMATION_FRANCHISEPLATFORM.equals(sPickListType) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA))
				{
					StringList slParentIdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA+"_id");
					Map programMap = new HashMap();
					programMap.put("selectedPlatformList",slParentIdList);
					programMap.put("strgetAttrObject", "Franchise Platform");
					String[] methodargs = JPO.packArgs(programMap);
					String sType = "PlatformToBusinessArea";

					Map mapPicklistValues = getProductFormPlatform(context, slPickListValues, methodargs, sType);

					StringList slObjectNameList =  (StringList) mapPicklistValues.get("field_display_choices");

					StringList slObjectIdList =  (StringList) mapPicklistValues.get("field_choices");

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGFRANCHISEPLATFORM);	
				}
				else if(pgApolloConstants.STR_AUTOMATION_REGION.equals(sPickListType) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA))
				{
					StringList slParentIdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA+"_id");
					Map programMap = new HashMap();
					programMap.put("selectedPlatformList",slParentIdList);
					String[] methodargs = JPO.packArgs(programMap);
					String sType = pgApolloConstants.STR_AUTOMATION_REGION;

					Map mapPicklistValues = getProductFormPlatform(context, slPickListValues, methodargs, sType);

					StringList slObjectNameList =  (StringList) mapPicklistValues.get("field_display_choices");

					StringList slObjectIdList =  (StringList) mapPicklistValues.get("field_choices");

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDREGION);	
				}
				else if(pgApolloConstants.STR_AUTOMATION_SUBREGION.equals(sPickListType) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA) && mapNewInputData.containsKey(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDREGION))
				{
					StringList slParentIdList = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGBUSINESSAREA);
					StringList slParentId2List = (StringList)mapNewInputData.get(pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDREGION);
					Map programMap = new HashMap();
					programMap.put("selectedPlatformList",slParentIdList);
					programMap.put("selectedPlatformList2",slParentId2List);				
				
					String[] methodargs = JPO.packArgs(programMap);
					String sType = pgApolloConstants.STR_AUTOMATION_SUBREGION;

					Map mapPicklistValues = getProductFormPlatform(context, slPickListValues, methodargs, sType);

					StringList slObjectNameList =  (StringList) mapPicklistValues.get("field_display_choices");

					StringList slObjectIdList =  (StringList) mapPicklistValues.get("field_choices");

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, pgApolloConstants.ATTRIBUTE_ENTERPRISEPART_PGLPDSUBREGION);

				}
				if(pgApolloConstants.TYPE_PLANT.equals(sPickListType))
				{
					String sType = pgApolloConstants.TYPE_PLANT;
					String sAttributeName = pgApolloConstants.TYPE_PLANT;

					MapList mlObjectList = getPickListMapList(context, slPickListValues, sType);

					StringList slObjectNameList = new ChangeUtil().getStringListFromMapList(mlObjectList, DomainConstants.SELECT_NAME);

					StringList slObjectIdList = new ChangeUtil().getStringListFromMapList(mlObjectList, DomainConstants.SELECT_ID);

					mapNewInputData = validatePickListData(mapNewInputData, slPickListValues, slObjectNameList, slObjectIdList, sAttributeName);				

				}
			}
			
			return mapNewInputData;
		}

		/**
		 * Method to get Pick list Map-list
		 * @param context
		 * @param slPickListValues
		 * @param sType
		 * @return
		 * @throws FrameworkException
		 */
		public static MapList getPickListMapList(Context context, StringList slPickListValues, String sType)	throws FrameworkException 
		{
			StringList slObjectSelectable = new StringList();
			slObjectSelectable.add(DomainConstants.SELECT_NAME);
			slObjectSelectable.add(DomainConstants.SELECT_ID);

			String sObjectNames = StringUtil.join(slPickListValues, pgApolloConstants.CONSTANT_STRING_COMMA);

			StringBuilder sbWhereCondition = new StringBuilder(DomainConstants.SELECT_CURRENT).append(pgApolloConstants.CONSTANT_STRING_DOUBLE_EQUAL).append(pgApolloConstants.STATE_ACTIVE);

			MapList mlObjectList = DomainObject.findObjects(context, //Context
															sType, //Type
															sObjectNames, //Object Name
															DomainConstants.QUERY_WILDCARD,//Revision pattern
															DomainConstants.QUERY_WILDCARD,//Owner Pattern
															DomainConstants.QUERY_WILDCARD,//Vault Pattern
															sbWhereCondition.toString(),//where Clause
															true, //Include Subtypes
															slObjectSelectable); //Object Selectable
			return mlObjectList;
		}


	/**
	 * Method to validate pick list data and return map
	 * @param mapNewInputData
	 * @param slPickListValues
	 * @param slObjectNameList
	 * @param slObjectIdList
	 * @param sAttributeName
	 * @return
	 */
		public static Map validatePickListData(Map mapNewInputData, StringList slPickListValues,StringList slObjectNameList, StringList slObjectIdList, String sAttributeName) {


			String sObjectId;

			StringList slInvalidPickListValues = new StringList();
			StringList slValidPickListObjectIdValues = new StringList();
			StringList slValidPickListObjectNameValues = new StringList();

			String sPickListValue;

			int iObjectIndex = 0;

			if(null != slPickListValues && !slPickListValues.isEmpty())
			{
				for(int i=0; i< slPickListValues.size(); i++)
				{				
					sPickListValue = slPickListValues.get(i);

					if(slObjectNameList.contains(sPickListValue))
					{
						iObjectIndex = slObjectNameList.indexOf(sPickListValue);

						sObjectId = slObjectIdList.get(iObjectIndex);					

						slValidPickListObjectIdValues.add(sObjectId);
						
						slValidPickListObjectNameValues.add(sPickListValue);

					}
					else
					{
						slInvalidPickListValues.add(sPickListValue);
					}

				}
			}

			if(!slInvalidPickListValues.isEmpty())
			{
				Set<String> setMissingPickList = new HashSet();

				if(mapNewInputData.containsKey(pgApolloConstants.STR_ERROR))
				{
					setMissingPickList.addAll((Set<String>)mapNewInputData.get(pgApolloConstants.STR_ERROR));
				}
				setMissingPickList.addAll(slInvalidPickListValues);			
				mapNewInputData.put(pgApolloConstants.STR_ERROR, setMissingPickList);
			}
			if(!slValidPickListObjectIdValues.isEmpty())
			{
				mapNewInputData.put(sAttributeName, slValidPickListObjectNameValues);
				mapNewInputData.put(sAttributeName+"_id", slValidPickListObjectIdValues);

			}
			

			return mapNewInputData;
		}



		/**
		 * Method to get 
		 * @param context
		 * @param slFieldValueList
		 * @param methodargs
		 * @param strType
		 * @return
		 * @throws Exception
		 */
		public static Map getProductFormPlatform(matrix.db.Context context, StringList slFieldValueList, String[] methodargs, String strType) throws Exception{

			Map mapConnectedValues = new HashMap();

			if ("Platform".equalsIgnoreCase(strType)) {
				mapConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedPlatform",methodargs,Map.class);
			} else if ("Chassis".equalsIgnoreCase(strType)) {
				mapConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedChassisToPlatform",methodargs,Map.class);
			} else if (pgApolloConstants.STR_PRODUCT_SIZE.equalsIgnoreCase(strType)) {
				mapConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedProductSizeToChassis",methodargs,Map.class);				
			} else if (pgApolloConstants.STR_AUTOMATION_REGION.equalsIgnoreCase(strType)) {
				mapConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedBusinessAreaToRegion",methodargs,Map.class);				
			} else if (pgApolloConstants.STR_AUTOMATION_SUBREGION.equalsIgnoreCase(strType)) {
				mapConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getApplicableSubRegionsBasedOnBusinessAreaAndRegion",methodargs,Map.class);				
			} else {
				mapConnectedValues = (HashMap) JPO.invoke(context, "emxCommonDocument", null, "getConnectedPlatformToBusinessArea",methodargs, Map.class);
			}


			return mapConnectedValues;
		}
		/**
		 * APOLLO - 2022x.05 Apr 24 CW - Defect 57437 - If user deletes newly created revision of LPD APP, then subsequent revision of that LPD APP fails
		 * If Representation is connected to Another VPMRef, it will not get deleted
		 * This Method is getting called from Custom Revision and Delete Trigger operation of APP
		 * Method to delete Representation List
		 * @param context
		 * @param slRepresentationIdList
		 * @throws Exception 
		 */
		public static void deleteRepresentations(Context context, StringList slRepresentationIdList) throws Exception 
		{
			if(!slRepresentationIdList.isEmpty())
			{
				
				StringList slRepIdList = new StringList();

				DomainObject domRepresenation;
				
				for(String sRepId : slRepresentationIdList)
				{
					if(UIUtil.isNotNullAndNotEmpty(sRepId))
					{
						domRepresenation = DomainObject.newInstance(context,sRepId);	
    					
    					if(domRepresenation.exists(context))
    					{
    						slRepIdList.add(sRepId);
    					}
					}
				}
				
				context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : deleteRepresentations >> slRepIdList : "+slRepIdList);

				StringList slRepIdToBeDeletedList = new StringList();
				
				if(!slRepIdList.isEmpty())
				{
					String sVPMRefSelectable = new StringBuilder("to[").append(pgApolloConstants.RELATIONSHIP_VPMRepInstance).append("].from.").append(DomainConstants.SELECT_ID).toString();
					
					StringList slObjectSelects = new StringList();
					slObjectSelects.add(DomainConstants.SELECT_ID);
					slObjectSelects.add(DomainConstants.SELECT_PHYSICAL_ID);
					slObjectSelects.add(DomainConstants.SELECT_CURRENT);
					slObjectSelects.add(sVPMRefSelectable);

					MapList mlRepresentationInfoList = DomainObject.getInfo(context, slRepIdList.toArray(new String[slRepIdList.size()]), slObjectSelects, new StringList(sVPMRefSelectable));
					
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : deleteRepresentations >> mlRepresentationInfoList : "+mlRepresentationInfoList);

					Map mapRepresentation;
					
					String sRepresentationState;
					String sRepresentationId;
					String sRepresentationPhysicalId;
					StringList slVPMRefConnected;
					
					for(Object objMap : mlRepresentationInfoList)
					{
						mapRepresentation = (Map)objMap;
						
						sRepresentationId = (String)mapRepresentation.get(DomainConstants.SELECT_ID);
						
						sRepresentationPhysicalId = (String)mapRepresentation.get(DomainConstants.SELECT_PHYSICAL_ID);

						sRepresentationState = (String)mapRepresentation.get(DomainConstants.SELECT_CURRENT);

						slVPMRefConnected = (StringList)mapRepresentation.get(sVPMRefSelectable);
						
						if(pgApolloConstants.STATE_IN_WORK.equals(sRepresentationState) && (null == slVPMRefConnected || slVPMRefConnected.isEmpty()))
						{
							slRepIdToBeDeletedList.add(sRepresentationPhysicalId);
						}

					}
					
					context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : deleteRepresentations >> slRepIdToBeDeletedList : "+slRepIdToBeDeletedList);

					
					if(!slRepIdToBeDeletedList.isEmpty())
					{		
						ArrayList slRepIdToBeDeletedArrayList = new ArrayList();
						I3DXDeleteInputBuilder i3DXDeleteInputBuilder;
						I3DXDeleteInput i3DXDeleteInput;						

						for(String sRepIdToBeDeletedId : slRepIdToBeDeletedList)
						{
							i3DXDeleteInputBuilder = new I3DXDeleteInputBuilder(sRepIdToBeDeletedId, false);
							i3DXDeleteInput = i3DXDeleteInputBuilder.build();
							slRepIdToBeDeletedArrayList.add(i3DXDeleteInput);
						}
						
						I3DXDeleteOptions i3DXDeleteOptions = new I3DXDeleteOptions();
						i3DXDeleteOptions.setForceLastVersionDeletion(false);
						
						
						I3DXDeleteOutputs i3DXDeleteOutputs = I3DXDeleteEngine.executeDelete(context, slRepIdToBeDeletedArrayList, i3DXDeleteOptions);
						
						String sObjects;
						if (null != i3DXDeleteOutputs && !i3DXDeleteOutputs.isSuccessfull()) {
							String sErrorMessage = i3DXDeleteOutputs.getErrorTag();
							I3DXDeleteInput i3DXDeleteInputAfterExecution = i3DXDeleteOutputs.getFailingInput();
							sObjects = i3DXDeleteInputAfterExecution.toString();
							context.printTrace(pgApolloConstants.TRACE_LPD, "Error: unable to delete  " + sObjects + "as " + sErrorMessage);

							throw new CommonWebException("Unable to delete object " + sObjects + ". " + sErrorMessage);
						} else {
							context.printTrace(pgApolloConstants.TRACE_LPD, "exit from pgApolloCommonUtil.deleteObjects");
						}						
						
						context.printTrace(pgApolloConstants.TRACE_LPD, "pgApolloCommonUtil : deleteRepresentations >> Deleted Representations successfully : "+slRepIdToBeDeletedList);
					}				
					
					
				}
				
				
			}
			
		}
		
		/**
		 * This method will be used to check whether given file is MSPR File
		 * @param context
		 * @param strMethod
		 * @return
	     * @throws FrameworkException 
		 * @throws Exception
		 */
		public static boolean isMSPRFile(String sFileName) {
			
			boolean bReturn = false;
			 
			 if(UIUtil.isNotNullAndNotEmpty(sFileName) && sFileName.startsWith(pgApolloConstants.STR_MSPR_REPORT_PREFIX) && sFileName.endsWith(pgApolloConstants.STR_MSPR_REPORT_EXTENSION))
			 {
					bReturn = true;
			 }
			 
			 return bReturn;
		}		

		/**
		 * Method to convert MapList to Json Array - using jackson library
		 * @param mapList
		 * @return
		 */
	    public static ArrayNode convertMapListToJsonArray(MapList mapList) {
	    	
	        ArrayNode jsonArray = objectMapper.createArrayNode();
	        
	        Map mapObject = null;
	        
	        for (Object objMap : mapList) {
	        	mapObject = (Map)objMap;
	        	if(null != mapObject)
	        	{
	        		ObjectNode jsonObject =  convertMapToJson(mapObject);
		            jsonArray.add(jsonObject);
	        	}	            
	        }

	        return jsonArray;
	    }

	    /**
	     * Method to convert Map to Json - using jackson library
	     * @param map
	     * @param jsonObject
	     */
	    public static ObjectNode convertMapToJson(Map<String, ?> map) {
    		ObjectNode jsonObject = objectMapper.createObjectNode();
    		ObjectNode nestedJsonObject;
	        for (Map.Entry<String, ?> entry : map.entrySet()) {
	            String key = entry.getKey();
	            Object value = entry.getValue();
	            if (value instanceof String) {
	                jsonObject.put(key, (String) value);
	            } else if (value instanceof StringList) {
	                jsonObject.set(key, convertStringListToJsonArray((StringList) value));
	            } else if (value instanceof Map) {
	            	nestedJsonObject = convertMapToJson((Map<String, ?>) value);
	                jsonObject.set(key, nestedJsonObject);
	            } else if (value instanceof MapList) {
	                jsonObject.set(key, convertMapListToJsonArray((MapList) value));
	            }
	        }
	        return jsonObject;
	    }

	    /**
	     * Method to convert StringList to Json Array - using jackson library
	     * @param list
	     * @return
	     */
	    public static ArrayNode convertStringListToJsonArray(StringList list) {
	        ArrayNode jsonArray = objectMapper.createArrayNode();
	        for (String str : list) {
	        	if(null != str)
	        	{
	        		jsonArray.add(str);
	        	}
	        }
	        return jsonArray;
	    }
}
