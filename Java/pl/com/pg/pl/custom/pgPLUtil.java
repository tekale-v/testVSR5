package com.pg.pl.custom;

import com.matrixone.apps.domain.DomainObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import matrix.db.Context;
import matrix.db.JPO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//Added By Picklist Team for 2018x.6 - Starts
import matrix.util.StringList;
import java.util.Map;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import matrix.db.Page;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.matrixone.apps.domain.util.mxType;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.productline.ProductLineUtil;
//Added By Picklist Team for 2018x.6 - Ends
import java.util.logging.Logger;
public class pgPLUtil
  extends DomainObject
{
	//Added By Picklist Team for 2018x.6 - Starts
	private static boolean isPLTypeAndTitleAllowed = false;
	private static String strPLStringResource = "PLStringResource";
	private static String strError = "error";
	static final Logger logPLUtil = Logger.getLogger("pgPLUtil");
	//Added By Picklist Team for 2018x.6 - Ends
	
  public void processCustomer(Context context)
    throws Exception
  {
    try
    {
      String[] arrayOfString = null;
      JPO.invoke(context, "pgPLCustomer", null, "processCustomerData", arrayOfString);
    }
    catch (Exception e)
    {
      throw e;
    }
  }
  
  public void generateHierarchyReport(Context context, String strCurrentPickListType, String strConfigRelationship, String strConfigDirection, String strConfigConnectedType, String strConfigObjectID, String strConfigLabel, StringBuffer sRelatedObjectNames)
    throws Exception
  {
    try
    {
      String[] arrayOfString = null;
      HSSFWorkbook workbook = new HSSFWorkbook();
      HashMap paramMap = new HashMap(4);
      paramMap.put("strCurrentPickListType", strCurrentPickListType);
      paramMap.put("strConfigRelationship", strConfigRelationship);
      paramMap.put("strConfigDirection", strConfigDirection);
      paramMap.put("strConfigConnectedType", strConfigConnectedType);
      paramMap.put("strConfigObjectID", strConfigObjectID);
      paramMap.put("strConfigLabel", strConfigLabel);
      paramMap.put("sRelatedObjectNames", sRelatedObjectNames);
      
      String[] methodArgs = JPO.packArgs(paramMap);
      JPO.invoke(context, "pgPLHierarchyReports", null, "getPickListConfigDetails", methodArgs);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
  
  public static void sendEMailToUser(String[] paramArrayOfString)
    throws Exception
  {
    try
    {
	
      String strSMTPHost = paramArrayOfString[0];
      String strEmailId = paramArrayOfString[1];
      String strTaskOwner = paramArrayOfString[2];
      String strEmptyString = paramArrayOfString[3];
      String strSubject = paramArrayOfString[4];
      String strMailMessage = paramArrayOfString[5];
      String strFromUser = paramArrayOfString[6];
      Properties localProperties = System.getProperties();
      localProperties.put("mail.smtp.host", strSMTPHost);
      Session localSession = Session.getInstance(localProperties, null);
      MimeMessage localMimeMessage = new MimeMessage(localSession);
      localMimeMessage.setFrom(new InternetAddress(strEmailId, strFromUser));
      if ((strTaskOwner != null) && (strTaskOwner.indexOf(",") != -1)) {
        localMimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(strTaskOwner));
      } else {
        localMimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(strTaskOwner));
      }
      localMimeMessage.setSubject(strSubject);
      MimeBodyPart localMimeBodyPart = new MimeBodyPart();
      localMimeBodyPart.setText(strMailMessage);
      MimeMultipart localMimeMultipart = new MimeMultipart();
      localMimeMultipart.addBodyPart(localMimeBodyPart);
      if ((strEmptyString != null) && (!"".equals(strEmptyString)))
      {
        localMimeBodyPart = new MimeBodyPart();
        FileDataSource localFileDataSource = new FileDataSource(strEmptyString);
        localMimeBodyPart.setDataHandler(new DataHandler(localFileDataSource));
        String strFileName = "";
        if (strEmptyString.indexOf("/") != -1) {
          strFileName = strEmptyString.substring(strEmptyString.lastIndexOf("/") + 1, strEmptyString.length());
        }
        localMimeBodyPart.setFileName(strFileName);
        localMimeMultipart.addBodyPart(localMimeBodyPart);
      }
      localMimeMessage.setContent(localMimeMultipart);
      Transport.send(localMimeMessage);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  //Added By Picklist Team for 2018x.6 - Starts
	/**
	* @Description: This method is added to provide picklist items according to new architecture.
	* @Author: Added by PickList Team
	* @param context - context of logged in user
	* @param typePattern - Old Picklist Type
	* @param namePattern - name pattern
	* @param revPattern - revision pattern
	* @param vaultPattern - vault pattern
	* @param whereExpression - object where clause
	* @param objectSelects - object selectables
	* @param states - sate of Picklist Object
	* @return MapList
	* @throws Exception if the operation fails
	*/
	
	public static MapList findPicklistObjects(Context context, String typePattern, StringList namePattern, String revPattern, String vaultPattern, String whereExpression, StringList objectSelects, String states) throws Exception {
		String strPolicyPicklistItemAliasName = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_POLICY, pgPLConstants.TYPE_PGPICKLISTITEM, true);
		String strActiveState = PropertyUtil.getSchemaProperty(context, DomainConstants.SELECT_POLICY, PropertyUtil.getSchemaProperty(context, strPolicyPicklistItemAliasName), "state_Active"); 
		MapList mlPicklistObjects = null;
		MapList mlFilteredObjects = new MapList();
		Map<String,String> mpReturn = new HashMap<>();
		String strCurrentValue = "";
		String strAttributeTitle = "";
		String strErrorMsg = "";
		Map<String,String> mpPickListObjects = null;
		if(UIUtil.isNotNullAndNotEmpty(typePattern) && !typePattern.contains(DomainConstants.QUERY_WILDCARD) && !typePattern.equals(pgPLConstants.STR_BLANK)){
			String sNamePattern = buildNamePattern(context,namePattern,typePattern);
			isPLTypeAndTitleAllowed = true;
			objectSelects = updateSelectable(objectSelects, true);
			mlPicklistObjects = DomainObject.findObjects(context,         //context of logged in user
										pgPLConstants.TYPE_PGPICKLISTITEM,//Picklist Type
										sNamePattern,                     //name pattern
										revPattern,                       //revision pattern
										DomainConstants.QUERY_WILDCARD,   //owner pattern.
										vaultPattern,                     //vault pattern
										whereExpression,                  //object where clause
										false,                            //do not find subtypes
										objectSelects);                   //object selectables		
			if(UIUtil.isNullOrEmpty(states) || (states.equals(DomainConstants.QUERY_WILDCARD))){
				 states = strActiveState;
}
			if(mlPicklistObjects!=null && !mlPicklistObjects.isEmpty()){
				int mlPicklistObjectsSize =  mlPicklistObjects.size();
				for( int iCount = 0 ; iCount < mlPicklistObjectsSize ; iCount++){   
					mpPickListObjects = (Map)mlPicklistObjects.get(iCount);
					if(mpPickListObjects!= null && !mpPickListObjects.isEmpty()){
						strCurrentValue = mpPickListObjects.get(DomainConstants.SELECT_CURRENT);
						strAttributeTitle = mpPickListObjects.get(DomainConstants.SELECT_ATTRIBUTE_TITLE);
						mpPickListObjects.put("actualname", mpPickListObjects.get(DomainConstants.SELECT_NAME));
						mpPickListObjects.replace(DomainConstants.SELECT_NAME, strAttributeTitle);
						if(states.contains(strCurrentValue)){
							mlFilteredObjects.add(mlPicklistObjects.get(iCount));
						}
					}
				}
			}	
		}else{
			strErrorMsg = EnoviaResourceBundle.getProperty(context, strPLStringResource,context.getLocale(),"emxPL.pgPLPicklist.msg.TypePatternNotAllowed");
			mpReturn.put(strError, strErrorMsg);
			mlFilteredObjects.add(mpReturn);
		}
		return mlFilteredObjects;
	}
	
	/**
	 * Description: This method is added to prepare the name pattern as per new picklist architecture
	 * Author: Method added by picklist team
	 * @param context - context of logged in user
	 * @param slPickListName - Name of the objects
	 * @param sPicklistType - Type used for filtering the data
	 * @return String
	 * @throws Exception if the operation fails
	 */
	public static String buildNamePattern(Context context, StringList slPickListName, String sPicklistType) throws Exception {
		String strNewName = "";
		String strMappingNumber = "";
		String strEachName = "";
		String strEachType = "";
		StringBuilder sbPicklistObjectName = new StringBuilder();
		StringList slTypePatterns = StringUtil.split(sPicklistType, pgPLConstants.SYMB_COMMA);
		int slTypePatternsSize = slTypePatterns.size();
		int slPickListNameSize = 0;
		if(slPickListName == null)
			slPickListName = new StringList(DomainConstants.QUERY_WILDCARD);
		slPickListNameSize = slPickListName.size();
		
		for(int typeIndex=0; typeIndex<slTypePatternsSize; typeIndex++){					
			strEachType = slTypePatterns.get(typeIndex); 
			if(!slPickListName.isEmpty() && !slPickListName.contains(DomainConstants.QUERY_WILDCARD)){
				for(int nameIndex=0; nameIndex<slPickListNameSize; nameIndex++){
					strEachName = slPickListName.get(nameIndex);
					strNewName = getPicklistObjectNamePattern(context, strEachType, strEachName);
					//Modified By Picklist Team for 2018x.6 Defect:38614 - Starts
					sbPicklistObjectName.append(strNewName);
					//Modified By Picklist Team for 2018x.6 Defect:38614 - Ends
					if(nameIndex < slPickListNameSize -1)	
						sbPicklistObjectName.append(pgPLConstants.SYMB_COMMA);
				}
			}else{
				strMappingNumber = getPicklistTypeMappingNumber(context, strEachType);
				sbPicklistObjectName.append(strMappingNumber).append(pgPLConstants.SYMBOL_UNDER_SCORE).append(DomainConstants.QUERY_WILDCARD);
			}
			if(typeIndex < slTypePatternsSize-1)	
				sbPicklistObjectName.append(pgPLConstants.SYMB_COMMA);
		}
		strNewName = sbPicklistObjectName.toString();
		return strNewName;
	}
	 /**
	 * Description Method added to get the related object of picklist types
	 * Author Added by picklist team
	 * @param context - context of logged in user
	 * @param domainObject - DomainObject to get Picklist
	 * @param relationshipPattern - Relationship pattern
	 * @param typePattern - Object type pattern
	 * @param objectSelects - Object selectable
	 * @param relationshipSelects - Relationship selectable
	 * @param getTo: Get To relationships.
	 * @param getFrom: Get From relationships.
	 * @param objectWhere - Object where clause
	 * @param relationshipWhere - Relationship where clause
	 * @param filterPicklistTypePattern: Old Picklist Type or Non-Picklist Type for filter
	 * @return MapList
	 * @throws Exception if the operation fails
	 */
	public static MapList getRelatedPicklistObjects(Context context, DomainObject domainObject, String relationshipPattern, String typePattern, StringList objectSelects, StringList relationshipSelects, boolean getTo, boolean getFrom, String objectWhere, String relationshipWhere, String filterPicklistTypePattern) throws Exception{  
		MapList mlRelatedObjects = null;
		MapList mlFilteredObjects = new MapList();
		Map<String,String> mpReturn = new HashMap<>();
		String strType = "";
		String strTypePattern = "";
		StringBuilder sbTypePattern = new StringBuilder();
		String strErrorMsg = null;
		String strWildCard = DomainConstants.QUERY_WILDCARD;
		String strTypePicklistItem = pgPLConstants.TYPE_PGPICKLISTITEM;
		String strComma = pgPLConstants.SYMB_COMMA;
		if( null != relationshipPattern && !relationshipPattern.equals(strWildCard)){
			if(null != typePattern && typePattern.length()>1 && typePattern.contains(strWildCard)){
				strErrorMsg = EnoviaResourceBundle.getProperty(context, strPLStringResource,context.getLocale(),"emxPL.pgPLPicklist.msg.AcceptedTypePattern");
				mpReturn.put(strError, strErrorMsg);
				mlFilteredObjects.add(mpReturn);
			}else if((UIUtil.isNotNullAndNotEmpty(typePattern) && (null != typePattern && typePattern.contains(strTypePicklistItem))) && (UIUtil.isNullOrEmpty(filterPicklistTypePattern) || filterPicklistTypePattern.equals(strWildCard))){
				strErrorMsg = EnoviaResourceBundle.getProperty(context, strPLStringResource,context.getLocale(),"emxPL.pgPLPicklist.msg.FilterTypePatternNotAllowed");
		        mpReturn.put(strError, strErrorMsg);
		        mlFilteredObjects.add(mpReturn);
			}else{
			    if(null != typePattern && !typePattern.equals(strWildCard)){
					StringList slType = StringUtil.split(typePattern,strComma);
					int slTypeSize = slType.size();
					for(int iCount=0; iCount<slTypeSize; iCount++){
						strType = slType.get(iCount);
						sbTypePattern.append(strType);
						if(iCount < slTypeSize-1)	
							sbTypePattern.append(strComma);			
					}
					strTypePattern = sbTypePattern.toString();
					if(strTypePattern!=null && strTypePattern.contains(strTypePicklistItem))
						isPLTypeAndTitleAllowed = true;
				}else{
					strTypePattern = strWildCard;
				}
				
				objectSelects = updateSelectable(objectSelects, false);
				mlRelatedObjects = domainObject.getRelatedObjects(context,		//Context of logged in user
												relationshipPattern,			//Relationship pattern
												strTypePattern,					//Type pattern
												objectSelects,					//Object selectable
												relationshipSelects,			//Relationship selectable
												getTo,							//Get To relationships
												getFrom,						//Get From relationships
												(short)1,				    	//Expand level
												objectWhere,					//Object where clause
												relationshipWhere,				//Relationship where clause
												0);						    	//Object limit
												
				if((typePattern!=null && typePattern.equals(strWildCard)) || (strTypePattern!=null && !strTypePattern.contains(strTypePicklistItem))){
					mlFilteredObjects = mlRelatedObjects;
				}else{
					if(strTypePattern!=null && !mlRelatedObjects.isEmpty() && strTypePattern.contains(strTypePicklistItem))
						mlFilteredObjects = filterRequiredPicklistObjectsFromGetRelated(mlRelatedObjects, filterPicklistTypePattern);
				}
			}
		}else{
			strErrorMsg = EnoviaResourceBundle.getProperty(context, strPLStringResource,context.getLocale(),"emxPL.pgPLPicklist.msg.RelationshipPatternNotAllowed");
			mpReturn.put(strError, strErrorMsg);
			mlFilteredObjects.add(mpReturn);
       }
		return mlFilteredObjects;
	}
	

	 /**
	 * Description: This method is added to filter out the picklist objects as per new picklist new architecture
	 * Author: Method added by picklist team
	 * @param context - context of logged in user
	 * @param mlAllPicklistObject - Maplist to be filtered
	 * @param strPatternToMatch - Patterns to match with object's attribute value
	 * @return MapList
	 * @throws Exception if the operation fails
	 */
	public static MapList filterRequiredPicklistObjectsFromGetRelated(MapList mlAllPicklistObject, String strPatternToMatch) {
		MapList mlFilteredObjects = new MapList();
		String strAttributePicklistTypeValue = "";
		String strType = "";
		boolean bValueMatched = false;
		StringList slPatterns = StringUtil.split(strPatternToMatch, pgPLConstants.SYMB_COMMA);
		for( int iCount = 0 ; iCount < mlAllPicklistObject.size() ; iCount++)
		{		
			strType = (String)((Map)mlAllPicklistObject.get(iCount)).get(DomainConstants.SELECT_TYPE);
			if(strType.equals(pgPLConstants.TYPE_PGPICKLISTITEM)){
				strAttributePicklistTypeValue = (String)((Map)mlAllPicklistObject.get(iCount)).get(pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE);
				if(slPatterns.contains(strAttributePicklistTypeValue)){
					bValueMatched = true;
				}
			}else if(slPatterns.contains(strType)){
		       bValueMatched = true;
			}
			if(bValueMatched){
				mlFilteredObjects.add(mlAllPicklistObject.get(iCount));
				bValueMatched = false;
	        }
		}
		return mlFilteredObjects;
	}
	
	 /**
	 * Description Method added to get the updated selectable for maplist
	 * Author Added by picklist team
	 * @param objSelects: Object selectable
	 * @param bState: true if current is required
	 * @return StringList (objSelects: Updated selectable for the maplist)
	 * @throws Exception if the operation fails
	 */
	public static StringList updateSelectable(StringList objSelects, boolean bState){
			if(null == objSelects){
				objSelects = new StringList();
			}
			if(!objSelects.contains(pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE) && isPLTypeAndTitleAllowed){
				objSelects.addElement(pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE);
			}
			if(!objSelects.contains(DomainConstants.SELECT_ATTRIBUTE_TITLE) && isPLTypeAndTitleAllowed){
				objSelects.addElement(DomainConstants.SELECT_ATTRIBUTE_TITLE);
			}
			if(bState && !objSelects.contains(DomainConstants.SELECT_CURRENT)){
				objSelects.addElement(DomainConstants.SELECT_CURRENT);
			}
			if( !objSelects.contains(DomainConstants.SELECT_NAME)){
				objSelects.addElement(DomainConstants.SELECT_NAME);
			}
			if( !objSelects.contains(DomainConstants.SELECT_ID)){
				objSelects.addElement(DomainConstants.SELECT_ID);
			}
		return objSelects;
	}
	
	 /**
	 * This method is used to return new object name for the passed picklist subtype and name entered by the user in an method arguments.
	 * @Argument context: context of the logged in user
	 * @Argument strPicklistSubType: String Picklist Subtype
	 * @Argument strPicklistNameValue: String name entered by the user
	 * @returns String
	 * @throws Exception if the operation fails
	 */
	public static String getPicklistObjectNamePattern(Context context, String strPicklistSubType, String strPicklistNameValue) throws Exception {
		String strMappingNumber = "";
		StringBuilder sbPicklistObjectName = new StringBuilder();
		int maxNumberCharsAllowedOnName = 121;
		int intPicklistTitleLength = strPicklistNameValue.length();
		if(UIUtil.isNotNullAndNotEmpty(strPicklistSubType) && !strPicklistSubType.equals(pgPLConstants.STR_BLANK)){
			strMappingNumber = getPicklistTypeMappingNumber(context,strPicklistSubType);
			sbPicklistObjectName.append(strMappingNumber).append(pgPLConstants.SYMBOL_UNDER_SCORE);
		}else{
			sbPicklistObjectName.append(pgPLConstants.STR_PREFIX_PL).append(pgPLConstants.SYMB_WILD);	
		}
		
		if(intPicklistTitleLength > maxNumberCharsAllowedOnName){
			strPicklistNameValue = strPicklistNameValue.substring(0,maxNumberCharsAllowedOnName);
		}
		
		if(UIUtil.isNotNullAndNotEmpty(strPicklistNameValue)){
			strPicklistNameValue = strPicklistNameValue.replaceAll("[$*';,?^\\\\\"]", pgPLConstants.SYMBOL_TILDE);
			sbPicklistObjectName.append(strPicklistNameValue);
		}
		return sbPicklistObjectName.toString();
	}
			
	 /**
	 * This method is used to return correct mapping for the passed picklist subtype from the respective picklist configuration object.
	 * @Argument context: context of the logged in user
	 * @Argument strConfigObjectName: String Picklist Subtype
	 * @returns String
	 * @throws Exception if the operation fails
	 */
	public static String getPicklistTypeMappingNumber(Context context, String strConfigObjectName) throws Exception {
		String strMappingNumber = DomainConstants.EMPTY_STRING;
		StringList objectSelects = new StringList(1);
		objectSelects.add(pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE_MAPPING_NUMBER);
			MapList mlPLConf = DomainObject.findObjects(context,            //context
									 pgPLConstants.TYPE_PG_PL_CONFIGURATION,//type
									 strConfigObjectName,                    //name
									 pgPLConstants.STRING_ZERO,              //revision
									 pgPLConstants.SYMB_WILD,                //owner
									 pgPLConstants.VAULT_ESERVICEPRODUCTION, //vault
									 null,                                   // where clause
									 false,                                  //expand
									 objectSelects);                         // object Select
			if(mlPLConf!=null && !mlPLConf.isEmpty()){
				strMappingNumber = (String)((Map)mlPLConf.get(0)).get(pgPLConstants.SELECT_ATTRIBUTE_PG_PL_PICKLIST_TYPE_MAPPING_NUMBER);
			}
		return strMappingNumber;
	}
				
	/**
	 * This method is used to get schema name of a picklist type (Old/New) from page object.
	 * @Argument context:  context of the logged in user
	 * @Argument property: Picklist type property alias name
	 * @returns String
	 * @throws Exception if the operation fails
	 */
	public static String getPicklistSchemaProperty(Context context, String property) throws Exception {
		String strSubType = null;
		Page plPage = new Page(pgPLConstants.PICKLIST_SUB_TYPE_PAGE_OBJECT);
		plPage.open(context);
		String strPageContext = plPage.getContents(context);
		if(UIUtil.isNotNullAndNotEmpty(strPageContext)) {
			Properties properties = new Properties();
			properties.load(new StringReader(strPageContext));
			strSubType = properties.getProperty(property);
		}
		return strSubType;
	}
	
	/**
	 * This method is used to return boolean value as true if the respective relationship is considered for common relationship merging.
	 * @Argument context: Context of the logged in User
	 * @Argument strRelName: Relationship name
	 * @returns boolean
	 * @throws Exception if the operation fails
	 */
	public static boolean isRelationshipMerged (Context context, String strRelName) throws Exception {
		String strMergedRelations = "";
		boolean isRelMerged = false;
		StringList slMergedRelationsList = null;
		Map<String,String> objectMap = null;
		StringList objectSelects = new StringList(1);
		objectSelects.add(pgPLConstants.SELECT_ATTRIBUTE_MERGED_RELATIONSHIPS);
		
		MapList mlPLSystemConfig = DomainObject.findObjects(context, 					                // eMatrix context	
															pgPLConstants.TYPE_PG_CONFIGURATION_ADMIN,  // type pattern
															pgPLConstants.NAME_PG_PL_SYSTEM_CONFIGURATION,// name pattern
															pgPLConstants.STD_REV, 						// revision pattern	
															pgPLConstants.SYMB_WILD, 					// owner pattern
															pgPLConstants.VAULT_ESERVICEPRODUCTION,		// Vault Pattern	
															null,										// where expression
															false,										// expand type
															objectSelects);								// Object Selects
	
		if(mlPLSystemConfig!=null && !mlPLSystemConfig.isEmpty()){
			objectMap = (Map) mlPLSystemConfig.get(0);
			strMergedRelations = objectMap.get(pgPLConstants.SELECT_ATTRIBUTE_MERGED_RELATIONSHIPS);
			slMergedRelationsList = StringUtil.split(strMergedRelations,pgPLConstants.SYMB_COMMA);
			if(UIUtil.isNotNullAndNotEmpty(strRelName) && slMergedRelationsList.contains(strRelName))
				isRelMerged = true;
		}
		return isRelMerged;
	}
	
	/**
	 * This method is used to Check the Type is Picklist SubType or Not. If it is subtype returning true otherwise false.
	 * @Argument context: Context of the logged in User
	 * @Argument strName: Picklist subtype name
	 * @returns boolean
	 * @throws Exception if the operation fails
	 */
	public static boolean isPicklistConfigExists(Context context, String strName) throws Exception {
		boolean isPLType = false;
		StringList objectSelects = new StringList(1);
		objectSelects.add(DomainConstants.SELECT_ID);
		MapList mlPLConf = DomainObject.findObjects(context,            //context
								 pgPLConstants.TYPE_PG_PL_CONFIGURATION, //type
								 strName,                                  //name
								 pgPLConstants.STRING_ZERO,              //revision
								 pgPLConstants.SYMB_WILD,                //owner
								 pgPLConstants.VAULT_ESERVICEPRODUCTION, //vault
								 null,                                   // where clause
								 false,                                  //expand
								 objectSelects);                         // object Select
		
		if(mlPLConf!=null && !mlPLConf.isEmpty()){
			isPLType = true;
		}
		return isPLType;
	}
	
	
	/**
	* This method is used to Check the Type is Picklist SubType or Not. If it is subtype returning true otherwise false.
	* @Argument context: Context of the logged in User
	* @Argument strType: Picklist subtype name
	* @returns boolean
	* @throws Exception if the operation fails
	*/
	public static boolean isPicklistItemType(Context context, String strType) throws Exception {
		boolean isPicklistType = false;
		String strOOTBTypes = getPicklistSchemaProperty(context,"STR_PL_OOTB_TYPES");
		StringList slPicklistOOTBTypes = StringUtil.split(strOOTBTypes,pgPLConstants.SYMB_COMMA);
		boolean isHavingPLConfigObject= isPicklistConfigExists(context,strType);
		if((slPicklistOOTBTypes != null && UIUtil.isNotNullAndNotEmpty(strType) && !slPicklistOOTBTypes.contains(strType) && isHavingPLConfigObject) 
				&& !isDerivedFromPicklistTypes(context,strType)){
				isPicklistType = true;
		}
		return isPicklistType;
	}
	
	 /**
	 * This method is used to return boolean value as true if the respective PicklistType is considered in Retained list.
	 * @Argument context: Context of the logged in User
	 * @Argument strPicklistType: Relationship name
	 * @returns boolean
	 * @throws Exception if the operation fails
	 */
	public static boolean isDerivedFromPicklistTypes(Context context, String strPicklistType) throws Exception {
		boolean isRetainedPicklistType = false;
		if(mxType.isOfParentType(context, strPicklistType, pgPLConstants.TYPE_PGPICKLISTITEM) || mxType.isOfParentType(context, strPicklistType, pgPLConstants.TYPE_PG_PLI_CHASSIS) || mxType.isOfParentType(context, strPicklistType, pgPLConstants.TYPE_PG_PLI_PLATFORM) || mxType.isOfParentType(context, strPicklistType, pgPLConstants.TYPE_PG_PPM_PHRASE)){
			isRetainedPicklistType = true;
		}
		return isRetainedPicklistType;
	}
	
	/**
	 * This method is used to return boolean value as true if the respective type is derived from pgPicklistTypes.
	 * @Argument context: Context of the logged in User
	 * @returns StringList
	 * @throws Exception if the operation fails
	 */
	public static StringList getPicklistTypesList(Context context) throws Exception {
		StringList slResult = null;
       slResult = ProductLineUtil.getChildrenTypes(context,pgPLConstants.TYPE_PGPICKLISTITEM);
		return slResult;
	}
	//Added By Picklist Team for 2018x.6 - Ends
	
	/**
	 * This method is added in 2022x-04 Req- 47715, used to return Maplist containing the names and object IDs of the expected pgPLIProductTechnologyChassis.
	 * @Argument context: Context of the logged in User
	 * @Argument strObjectIdOfPCP: Object Id of the pgPLIProductCategoryPlatform
	 * @Argument strObjectIdOfPTP: Object Id of the pgPLIProductTechnologyPlatform
	 * @returns MapList
	 * @throws Exception if the operation fails
	 */
	public static MapList getRelatedPTC(Context context, String strObjectIdOfPCP, String strObjectIdOfPTP){
		MapList mlChassisData = new MapList();
		try{
			Map mpChassis = null;
			String strPTP ="";
			String strPTC ="";
			String strChassisName="";
			String strChassisId ="";
			//Code added for Req-49488 starts
			String strChassisType ="";
			//Code added for Req-49488 ends
			String [] arrAttrCategorizedPTPPTC = null;
			DomainObject domPCP = DomainObject.newInstance(context, strObjectIdOfPCP);
			DomainObject domPTP = DomainObject.newInstance(context, strObjectIdOfPTP);
			String strAttrCategorizedPTPPTC = domPCP.getInfo(context,pgPLConstants.SELECT_ATTRIBUTE_PGPLIPRODUCTTECHNOLOGYPLATFORMTOCHASSISMAPPING);
			String strPTPName = domPTP.getInfo(context,DomainConstants.SELECT_NAME);
			if (null !=strAttrCategorizedPTPPTC && !strAttrCategorizedPTPPTC.equals("")) {
				arrAttrCategorizedPTPPTC = strAttrCategorizedPTPPTC.split(pgPLConstants.SYMB_COMMA);
			}
			StringList chassisObjSel = new StringList();
			chassisObjSel.add(DomainConstants.SELECT_ID);
			chassisObjSel.add(DomainConstants.SELECT_NAME);
			//Code added for Req-49488 starts
			chassisObjSel.add(pgPLConstants.SELECT_ATTRIBUTE_PGCHASSISTYPE);
			//Code added for Req-49488 ends
			// DSM 2022x.5 : ALM -Defect 57063 : [UAT] On copy/revise of parts with inactive PTP/PTC/PCP values, the active PTC values dont show in the list on the new part- START
			String strObjectWhere = DomainConstants.SELECT_CURRENT +"==" + pgPLConstants.STATE_ACTIVE; 
			MapList chasisMapList=domPTP.getRelatedObjects(
					context,						//the context for this request
					pgPLConstants.RELATIONSHIP_PGPLATFORMTOCHASSIS,		//relationshipPattern
					pgPLConstants.TYPE_PGPLIPRODUCTTECHNOLOGYCHASSIS,						//typePattern
					chassisObjSel,					//objectSelects
					null,							//relationshipSelects
					false,							//get To relationships
					true,							//get From relationships
					(short)1,						//number of levels to expand
					strObjectWhere,					//objectWhere
					DomainConstants.EMPTY_STRING					//relationshipWhere
					);
			// DSM 2022x.5 : ALM -Defect 57063 : [UAT] On copy/revise of parts with inactive PTP/PTC/PCP values, the active PTC values dont show in the list on the new part- END
			Map mpChassisData =null;
			boolean isMappingPresent= false;
			if(chasisMapList.size()>0 && null!=arrAttrCategorizedPTPPTC){
				for (Iterator iterator = chasisMapList.iterator(); iterator.hasNext();) {
					mpChassis = (Map) iterator.next();
					strChassisName = (String)mpChassis.get(DomainConstants.SELECT_NAME);
					strChassisId = (String)mpChassis.get(DomainConstants.SELECT_ID);
					//Code added for Req-49488 starts
					strChassisType = (String)mpChassis.get(pgPLConstants.SELECT_ATTRIBUTE_PGCHASSISTYPE);
					//Code added for Req-49488 ends
					for (int iCount =0;iCount<arrAttrCategorizedPTPPTC.length;iCount++) {
						strPTP = arrAttrCategorizedPTPPTC[iCount].split(pgPLConstants.SYMB_COLON)[0];
						strPTC = arrAttrCategorizedPTPPTC[iCount].split(pgPLConstants.SYMB_COLON)[1];
						if (strPTP.equals(strPTPName)) {
							isMappingPresent = true;
							if (strPTC.equals(strChassisName)) {
								mpChassisData = new HashMap();
								mpChassisData.put("id", strChassisId);
								mpChassisData.put("name", strChassisName);
								//Code added for Req-49488 starts
								mpChassisData.put(pgPLConstants.SELECT_ATTRIBUTE_PGCHASSISTYPE, strChassisType);
								//Code added for Req-49488 ends
								mlChassisData.add(mpChassisData);
							}
						}
					}
				}
				if (!isMappingPresent){
					mlChassisData = getChassisData(context, chasisMapList);
				}
			} else {
				mlChassisData = getChassisData(context, chasisMapList);
			}
		} catch (Exception e)
		{
			logPLUtil.severe(e.getMessage());
		} 
		return mlChassisData;
	}

	/**
	 * This method is used to return Maplist containing the names and object IDs of the pgPLIProductTechnologyChassis connected to pgPLIProductTechnologyPlatform.
	 * @Argument context: Context of the logged in User
	 * @Argument strObjectIdOfPTP: Object Id of the pgPLIProductTechnologyPlatform
	 * @returns MapList
	 * @throws Exception if the operation fails
	 */
	public static MapList getChassisData (Context context, MapList chasisMapList) throws Exception{
		MapList mlChassisData = new MapList();
		try {
			Map mpChassis = null;
			Map mpChassisData =null;
			if (chasisMapList.size()>0) {
				for (Iterator iterator = chasisMapList.iterator(); iterator.hasNext();) {
					mpChassis = (Map) iterator.next();
					mpChassisData = new HashMap();
					mpChassisData.put("id",mpChassis.get(DomainConstants.SELECT_ID));
					mpChassisData.put("name",mpChassis.get(DomainConstants.SELECT_NAME));
					//Code added for Req-49488 starts
					mpChassisData.put(pgPLConstants.SELECT_ATTRIBUTE_PGCHASSISTYPE, mpChassis.get(pgPLConstants.SELECT_ATTRIBUTE_PGCHASSISTYPE));
					//Code added for Req-49488 ends
					mlChassisData.add(mpChassisData);
				}
			}
		} catch (Exception e){
			logPLUtil.severe(e.getMessage());

		}

		return mlChassisData;
	}
}