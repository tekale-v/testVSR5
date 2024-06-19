/*Created by DSM for Requirement 47389 2022x.04 Dec CW 2023
 Purpose: Contains necessary methods for on-demand GenDoc Generation
*/
package com.pg.dsm.gendocondemand.cron;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.engineering.EngineeringConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.gendocondemand.config.GenDocOnDemandCronConfig;
import com.pg.dsm.gendocondemand.enumeration.GenDocConstant;
import com.pg.dsm.gendocondemand.config.GenDocOnDemandConfig;
import com.pg.v3.custom.pgV3Constants;
import common.GenDocJobs.pgPDFGenDocGeneration;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;
import java.util.Properties;

/**
 * @ Desc - This Class will be use to Generate the GenDoc for the Marked Part for on-demand GenDoc generation
 * Added by DSM for Requirement 47389 2022x.04 Dec CW 2023
 */
public class GenDocController {
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	public static String VALID_POLICY_LIST = "";
	public static String VALID_EC_PART_PHASE_LIST = "";	
	private GenDocOnDemandConfig config;
	private  PrintWriter out = null;
	public static Properties propConstant = new Properties();
	
	public GenDocController(GenDocOnDemandConfig conf) {
		this.config = conf;
	}
	
	public GenDocController(PrintWriter out)
	{
		this.out = out;
	}
	/**
	 * This method will call a method will get all the Marked Parts and will process for the GenDoc generation
	 * 
	 */
	public PrintWriter execute(PrintWriter out) throws Exception {
		if (this.config.isLoaded()) {

			GenDocOnDemandCronConfig cronConfig = new GenDocOnDemandCronConfig(this.config).loadConfig(out);
			out.append("Inside execute()...!!\n");			
			if (!cronConfig.isCronActive()) {
				cronConfig.updateCronAttrOnStart();

				boolean isUpdatedSuccessfully = this.executeGenDocGenerationJob(this.config.getContext(),out);
				if(isUpdatedSuccessfully){
					out.append("\n\nGenDoc generation Cron has updated Successfully\n");
					cronConfig.updateCronAttrOnEnd();					
				}
			} else {
				out.append("CTRLM is running or stuck..\n");
			}
		} else {
			out.append("Failed to load CTRLM configuration..\n");
		}
		return out;
	}

	/**
	 * @Desc This method will execute the Query to get the marked Parts and then will manipulate the data to process 
	 * @param Context context
	 * @return MapList - Part details who has marked true for the GenDoc generation 
	 * @throws Exception
	 * @author Sogeti
	*/
	private MapList getMarkedPartForGenDocGeneartion(Context context) throws FrameworkException{
		logger.info("getMarkedPartForGenDocGeneartion");
		MapList mMarkedPartDetails = null;
		String tempQuery = getPartTempQuery();
		//There is huge data and writting it in a file for CTRL-M job so using the MQLCommand
		String strTempQueryResult = MqlUtil.mqlCommand(context, tempQuery);
		mMarkedPartDetails = manipulateQueryResult(strTempQueryResult);
		return mMarkedPartDetails;
	}
	/**
	 * @Desc This method will create the temp query to get the Marked Parts details  
	 * @return String - Query result Object separate by "@" and Object details separate by "|" 
	 * @throws Exception
	 * @author Sogeti
	*/
	private String getPartTempQuery(){
		String strOnDemandGenDocAllowedTypes = this.config.getProperties().getPgOnDemandGenDocAllowedTypes();
		StringBuffer strTempQuery = new StringBuffer("temp query bus ");
		strTempQuery.append("\"").append(strOnDemandGenDocAllowedTypes).append("\"").append(" * * ");	
		String strWhereClause = " ' "+pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC+"==TRUE && current== "+pgV3Constants.STATE_PRELIMINARY+"'";
		strTempQuery.append(" where").append(strWhereClause).append(" select id  policy dump |  RECORDSEP @");
		return strTempQuery.toString();		
	}
	
	/**
	 * @Desc The result of the marked parts query will manipulate  
	 * @param String - temp query result
	 * @return MapList - Parts details who has marked true for the GenDoc generation 
	 * @throws Exception
	 * @author Sogeti
	 *
	*/
	private MapList manipulateQueryResult(String strTempQueryResult){
		MapList mMarkedPartDetails = new MapList();
		Map<String,String> mapPartDetails = null;
		if(StringUtils.isNotBlank(strTempQueryResult)){
			StringTokenizer stLine = new StringTokenizer(strTempQueryResult, "@");
			while(stLine.hasMoreTokens()){
				String strLine  = stLine.nextToken();
				StringTokenizer stTNR = new StringTokenizer(strLine, "|");
				mapPartDetails = new HashMap<String,String>();
				mapPartDetails.put(DomainConstants.SELECT_TYPE, stTNR.nextToken());
				mapPartDetails.put(DomainConstants.SELECT_NAME, stTNR.nextToken());
				mapPartDetails.put(DomainConstants.SELECT_REVISION, stTNR.nextToken());
				mapPartDetails.put(DomainConstants.SELECT_ID, stTNR.nextToken());
				mapPartDetails.put(DomainConstants.SELECT_POLICY, stTNR.nextToken());				
				mMarkedPartDetails.add(mapPartDetails);				
			}			
				mMarkedPartDetails.addSortKey(DomainConstants.SELECT_NAME,"ascending","String");
				mMarkedPartDetails.sort();			
		}
		return mMarkedPartDetails;
	}
	/**
	 * @Desc This method will get all adlib details from the Config Object
	 * @return Map : Adlib details Key and value
	 * 
	*/
	public Map getGenDocAdlibDetails() throws Exception{
		Map<String,String> mAdlibDetails = new HashMap<>();
		try{
			pgPDFGenDocGeneration classObject = new pgPDFGenDocGeneration();
			Context context =null; 
			context = classObject.getContext();
			Map<String,String> mAdlibobjMap ;

			StringList slBusSelect = new StringList(9);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPROTOCOLGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGFTPINPUTFOLDERPATHGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGHOSTNAMEGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGRENDERPDFGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGFTPOUTPUTFOLDERPATHGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGFTPROOTFOLDERPATHGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGADLIBUSERNAMEGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGPORTGENDOC);
			slBusSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGADLIBPASSWORDGENDOC);
			String strAdlibObjId ;
			BusinessObject busObj = new BusinessObject(pgV3Constants.TYPE_PGCONFIGURATIONADMIN, pgV3Constants.ADLIB_CONFIGOBJECT, pgV3Constants.SYMBOL_HYPHEN, pgV3Constants.VAULT_ESERVICEPRODUCTION);
			
			if (busObj.exists(context)) {
				strAdlibObjId = busObj.getObjectId(context);
				if(UIUtil.isNotNullAndNotEmpty(strAdlibObjId)){
					DomainObject domAdlibObj = DomainObject.newInstance(context, strAdlibObjId);			
					mAdlibobjMap = domAdlibObj.getInfo(context, slBusSelect);	
					mAdlibDetails.put("pgRenderPDFGenDoc",  mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGRENDERPDFGENDOC));
					mAdlibDetails.put("pgPortGenDoc",  mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPORTGENDOC));
					mAdlibDetails.put("pgFTPRootFolderPathGenDoc", mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGFTPROOTFOLDERPATHGENDOC));
					mAdlibDetails.put("pgProtocolGenDoc",  mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPROTOCOLGENDOC));
					mAdlibDetails.put("pgHostNameGenDoc",  mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGHOSTNAMEGENDOC));
					mAdlibDetails.put("pgAdlibUserNameGenDoc",  mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGADLIBUSERNAMEGENDOC));
					mAdlibDetails.put("pgAdlibPasswordGenDoc",  mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGADLIBPASSWORDGENDOC));
					mAdlibDetails.put("pgFTPInputFolderPathGenDoc", mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGFTPINPUTFOLDERPATHGENDOC));
					mAdlibDetails.put("pgFTPOutputFolderPathGenDoc",  mAdlibobjMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGFTPOUTPUTFOLDERPATHGENDOC));
				}
			}
		
		}catch(Exception exception) {		
			out.append("Error while getGenDocAdlibDetails"+exception.getMessage()+"\n");
		}
		return mAdlibDetails;
	}	     
	        private boolean isValidPolicyForGenDocGeneration(String strPolicy, String strReleasePhase){
	      	boolean isValidPolicy = false;
			VALID_POLICY_LIST = this.config.getProperties().getGenDocPolicyName(); 
			VALID_EC_PART_PHASE_LIST = this.config.getProperties().getGenDocReleasePhase();			
			if(UIUtil.isNotNullAndNotEmpty(strPolicy) && VALID_POLICY_LIST.contains(strPolicy) && ("".equals(strReleasePhase) || (pgV3Constants.POLICY_EC_PART.equalsIgnoreCase(strPolicy) && VALID_EC_PART_PHASE_LIST.contains(strReleasePhase))))
			{
				isValidPolicy = true;				
			}
			return isValidPolicy;
		}
	/**
	 * @Desc This method will get all the Marked Parts and will process for the GenDoc generation
	 * @param Context context
	 * @return boolean : is the GenDoc generation has successfully executed
	 * @throws Exception
	 * @author Sogeti
	*/
	private boolean executeGenDocGenerationJob(Context context,PrintWriter out) throws Exception{
		boolean isUpdateSuccessfully = false;
		MapList mlMarkedPartDetails = getMarkedPartForGenDocGeneartion(context);
		Map mapPart = null;
		Map mGCAS = null;
		String strGCASID = null;
		String strGCASPolicy = null;
		String strReleasePhase = "";
		Map mAdlibDetails = getGenDocAdlibDetails();
		MapList mlFailedGenDocGenDetails = new MapList();
		MapList mlSuccefullyGenDocGenDetails = new MapList();
		Map mFailedGCAS = null;
		long startTime = System.currentTimeMillis();
		DomainObject  doObj = null;
		
		for(Object objectPart:mlMarkedPartDetails) {
			 mapPart = (Map)objectPart;
			 strGCASID  = (String)mapPart.get(DomainConstants.SELECT_ID);
			 strGCASPolicy  = (String)mapPart.get(DomainConstants.SELECT_POLICY);
			if(StringUtils.isNotBlank(strGCASID)){
				 doObj = DomainObject.newInstance(context,strGCASID);
				
				String strIsProcessGenDoc  =  doObj.getInfo(context,pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
				
				if(UIUtil.isNotNullAndNotEmpty(strGCASPolicy) && pgV3Constants.POLICY_EC_PART.equalsIgnoreCase(strGCASPolicy))
				{
				
					strReleasePhase = doObj.getInfo(context,EngineeringConstants.ATTRIBUTE_RELEASE_PHASE_VALUE);
				}
				boolean isProcessGenDoc = Boolean.parseBoolean(strIsProcessGenDoc);
				if(isProcessGenDoc){
							out.append("strReleasePhase-->"+ strReleasePhase+"\n");											
							if(isValidPolicyForGenDocGeneration(strGCASPolicy, strReleasePhase)){
							out.append("Processing for strGCASID-->"+strGCASID+"\n");
							out.flush();
							HashMap argMap = new HashMap();
						    argMap.put("objectID", strGCASID);
						    argMap.put("AdlibDetails", mAdlibDetails);
						    Map mlGCASProcessDetails = null;
						    String[] methodargs = JPO.packArgs(argMap);
						    try{
						    out.append("argMap =" + argMap+"\n");
						    mlGCASProcessDetails = (Map)JPO.invoke(context, "pgIPMPDFViewUtil", null, "startGenDocGenerationJob", methodargs, Map.class);
						    
						    }catch(Throwable exception){
						    	out.append("Error while executeGenDocGenerationJob "+exception.getMessage()+"\n");					    	
						    }
						    if(null!=mlGCASProcessDetails){
						    	 String strErrorMsg = (String)mlGCASProcessDetails.get("strErrorMsg");
								String strReturnType = (String)mlGCASProcessDetails.get("strReturnType");
								if(StringUtils.isNotBlank(strErrorMsg) || "1".equals(strReturnType)){
									mFailedGCAS = new HashMap();
									if(mGCAS !=null && (mGCAS.size()>0)){
									mFailedGCAS = mGCAS;
									}
									mFailedGCAS.put("strErrorMsg", strErrorMsg);
									mlFailedGenDocGenDetails.add(mFailedGCAS);
								}else{
									mlSuccefullyGenDocGenDetails.add(mGCAS);
								}
						    }
						 }
					  }
					}
					//once the object's GenDoc generation has completed then will unmark the Parts
					updateProcessGenDocAttribute(context,doObj,out);
					out.flush();
				}
			
		
		
		MapList mlFailedReGenerateGenDocDetails = new MapList();
		try{
			if(null!=mlFailedGenDocGenDetails && !mlFailedGenDocGenDetails.isEmpty()){
				int mlLength = mlFailedGenDocGenDetails.size();
				HashMap argMap = new HashMap();
				Map mGCASProcessDetails = null;
				for(int i=0;i<mlLength;i++){
				 	Map mFailedGenDoc =  (Map)mlFailedGenDocGenDetails.get(i);
					argMap.put("objectID", mFailedGenDoc.get(DomainObject.SELECT_ID));
				    argMap.put("AdlibDetails", mAdlibDetails);
				    String[] methodargs = JPO.packArgs(argMap);
				    mGCASProcessDetails = (Map)JPO.invoke(context, "pgIPMPDFViewUtil", null, "startGenDocGenerationJob", methodargs, Map.class);
				    if(null!=mGCASProcessDetails){
				    	String strErrorMsg = (String)mGCASProcessDetails.get("strErrorMsg");
						String strReturnType = (String)mGCASProcessDetails.get("strReturnType");
						if(StringUtils.isNotBlank(strErrorMsg) || "1".equals(strReturnType)){
							mlFailedReGenerateGenDocDetails.add(mFailedGenDoc);
						}else{
							mlSuccefullyGenDocGenDetails.add(mFailedGenDoc);
						}
				    }
				}
			}
		}catch(Exception exception){
			
			out.append("Error mlFailedReGenerateGenDocDetails: "+exception.getMessage()+"\n");
			mlFailedReGenerateGenDocDetails = mlFailedGenDocGenDetails;
		}
		long endTime = System.currentTimeMillis();
		out.append("****Total time for strCAID--->"+(endTime-startTime)+"\n");
		generateGenDocForMarkedMEPSEP(context,mAdlibDetails,out);
		sendMailForFailedGCAS(context,mlMarkedPartDetails,mlFailedReGenerateGenDocDetails,mlSuccefullyGenDocGenDetails,out);
		
		isUpdateSuccessfully = true;
		return isUpdateSuccessfully;
	}
	/**
	 * @Desc This method will log details of GCAS who has processed for GenDoc
	 * @param Context context
	 * @param MapList - Marked Parts details
	 * @param MapList - GCAS details for whom GenDoc generation has failed 
	 * @param MapList - GCAS details for whom GenDoc generation has successful
	 * @return void 
	 * @throws Exception
	 * @author Sogeti
	*/
	private  void sendMailForFailedGCAS(Context context,MapList mlMarkedPart,MapList mlFailedGenDocGenDetails,MapList mSuccefullyGenDocGenDetails,PrintWriter out){
		if(null!=mlMarkedPart && !mlMarkedPart.isEmpty()){
			out.append("Below GenDoc generation has failed\n");
			for(Object object:mlFailedGenDocGenDetails){
				
				Map map = (Map)object;
				out.append("Object Id:"+map.get(DomainConstants.SELECT_ID)+"      Failed Reason:"+map.get("strErrorMsg")+"\n");
			}
			out.append("\n-------------------------------------------------------");
			out.append("Below GenDoc generation has Successful\n");
			for(Object object:mSuccefullyGenDocGenDetails){
				Map map = (Map)object;
				out.append("Object Id:"+map.get(DomainConstants.SELECT_ID)+"\n");
			}
		}
	}
	
	/**
	 * @Desc This method will get all the Marked MEP/SEP and will process MEP/SEP one by one for the GenDoc generation
	 * @param Context context
	 * @param Map : Adlib map details
	 * @return boolean : is the GenDoc generation has successfully executed
	 * @throws Exception
	 * @author Sogeti
	*/
	private boolean generateGenDocForMarkedMEPSEP(Context context,Map mAdlibDetails,PrintWriter out) throws Exception{
		boolean isUpdateSuccessfully = true;
		MapList mlMarkedMEPSEPDetails  = getMarkedMEPSEP(context);
		if(null!=mlMarkedMEPSEPDetails && !mlMarkedMEPSEPDetails.isEmpty()){
			for(Object object:mlMarkedMEPSEPDetails){
				Map mMEPSEPDetails  = (Map)object;
				String strMEPSEPID = (String)mMEPSEPDetails.get(DomainConstants.SELECT_ID);
				DomainObject  domMEPSEP = DomainObject.newInstance(context,strMEPSEPID);
				String strIsProcessGenDoc  =  domMEPSEP.getInfo(context,pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC);
				boolean isProcessGenDoc = Boolean.parseBoolean(strIsProcessGenDoc);
				if(isProcessGenDoc){
					HashMap argMap = new HashMap();
				    argMap.put("objectID", strMEPSEPID);
				    argMap.put("AdlibDetails", mAdlibDetails);
				    String[] methodargs = JPO.packArgs(argMap);
				    Map mlGCASProcessDetails = (Map)JPO.invoke(context, "pgIPMPDFViewUtil", null, "startGenDocGenerationJob", methodargs, Map.class);
				    if(null!=mlGCASProcessDetails){
				    	String strErrorMsg = (String)mlGCASProcessDetails.get("strErrorMsg");
						String strReturnType = (String)mlGCASProcessDetails.get("strReturnType");
						if(StringUtils.isNotBlank(strErrorMsg) || "1".equals(strReturnType)){
							Map mlGCASProcessDetailsRegenrate = (Map)JPO.invoke(context, "pgIPMPDFViewUtil", null, "startGenDocGenerationJob", methodargs, Map.class);
							String strErrorMsgRegenerate = (String)mlGCASProcessDetailsRegenrate.get("strErrorMsg");
							String strReturnTypeRegenerate = (String)mlGCASProcessDetailsRegenrate.get("strReturnType");
							if(StringUtils.isNotBlank(strErrorMsgRegenerate) || "1".equals(strReturnTypeRegenerate)){
								out.append("MEP/SEP has failed to Generate GenDoc -->"+strMEPSEPID+" Error Message-->"+strErrorMsgRegenerate+"\n");
							}else{
								out.append("MEP/SEP has Generated GenDoc -->"+strMEPSEPID+"\n");
							}
						}else{
							out.append("MEP/SEP has Generated GenDoc -->"+strMEPSEPID+"\n");
						}
				    }
					updateProcessGenDocAttribute(context,domMEPSEP,out);
				}
				out.flush();
			}
		}
		return isUpdateSuccessfully;
	}

	
	/**
	 * @Desc This method will create the temp query to get the Marked MEP/SEP details  
	 * @return String - Query result Object separate by "@" and Object details separate by "|" 
	 * @throws Exception
	 * @author Sogeti
	*/
	private String getMEPSEPTempQuery(){
		StringBuffer strTempQuery = new StringBuffer("temp query bus ");
		strTempQuery.append("* * * ");
		StringBuffer sbWhereClause = new StringBuffer().append(" ' ").append(pgV3Constants.SELECT_ATTRIBUTE_ISPROCESSGENDOC).append("==TRUE && policy== ");
		sbWhereClause.append("\"").append(pgV3Constants.POLICY_MANUFACTUREREQUIVALENT).append("\"").append("||").append("policy== \"").append(pgV3Constants.POLICY_SUPPLIEREQUIVALENT).append("\"").append("&& current== "+pgV3Constants.STATE_PRELIMINARY).append("'");
		strTempQuery.append(" where").append(sbWhereClause.toString()).append(" select id policy dump |  RECORDSEP @");
		return strTempQuery.toString();		
		
	}
	
	
	/**
	 * @Desc This method will execute the Query to get the marked MEP/SEP and then will manipulate the data to process 
	 * @param Context context
	 * @return MapList - MEP/SEP details who has marked true for the GenDoc generation 
	 * @throws Exception
	 * @author Sogeti
	*/
	private MapList getMarkedMEPSEP(Context context) throws FrameworkException{
		MapList mlMarkedMEPSEPDetails = null;
		String tempQuery = getMEPSEPTempQuery();
		//There is huge data and writting it in a file for CTRL-M job so using the MQLCommand
		String strTempQueryResult = MqlUtil.mqlCommand(context, tempQuery);
		mlMarkedMEPSEPDetails = manipulateQueryResult(strTempQueryResult);
		return mlMarkedMEPSEPDetails;
	}

/**
 * @Desc This method will Part attribute to False so two cron will not process the same part at same time 
 * @param Context context
 * @param DomainObject - domainObject of the parts
 * @return boolean - update attribute result 
 * @throws Exception
 * @author Sogeti
*/
private boolean updateProcessGenDocAttribute(Context context,DomainObject domObject,PrintWriter out) throws FrameworkException{
		boolean isUpdatedSuccessfully = false;
	if(null!=domObject){
		try {
			domObject.setAttributeValue(context, pgV3Constants.ATTRIBUTE_ISPROCESSGENDOC, "FALSE");
			isUpdatedSuccessfully = true;
		} catch (FrameworkException exception) {
			out.append("Update Parts attribute has failed\n");
			throw exception;
		}
	}
	return isUpdatedSuccessfully;
}

/**
 * @Desc Logger will set in this method
 * @throws Exception
 * @author Sogeti
*/
public PrintWriter setLoggerFile() throws FileNotFoundException{
	try (InputStream inputStream = new FileInputStream(
			new File(GenDocConstant.CTRLM_PROPERTIES_FILE.getValue()))) {
		Properties gendocproperties = new Properties();
		gendocproperties.load(inputStream);
	String logFolder = gendocproperties.getProperty("GenDocPartOnDemandCron.logs.logFolder");
	String logFilePrefix = 	gendocproperties.getProperty("GenDocPartOnDemandCron.logs.LogFilePrefix");
	long currentTimeMillis = System.currentTimeMillis();
	String logFileExtension = gendocproperties.getProperty("GenDocPartOnDemandCron.logs.LogFileExtension");
	out = new PrintWriter(new FileOutputStream(logFolder+ File.separator + logFilePrefix + currentTimeMillis + "."+ logFileExtension, true));
	StringBuffer sbFile = new StringBuffer();
	sbFile.append(logFolder).append(File.separator).append(logFilePrefix).append(currentTimeMillis).append(".").append(logFileExtension);
	File file = new File(sbFile.toString());
  	if(file.exists()){
  		/* we are using APIs setExecutable/setReadable/setWritable which returns boolean, since no further 
		  action required for those boolean values, we have not used it */
		 file.setReadable(true, false);
		 file.setWritable(true, false);
		 file.setExecutable(true, false); 
	} 
}catch(Exception e)
	{
	
	out.append("Set Loggers failed"+e.getMessage()+"\n");
	}
	return out;
}

}
