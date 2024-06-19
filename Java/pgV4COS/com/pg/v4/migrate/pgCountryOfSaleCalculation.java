package com.pg.v4.migrate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.pg.v4.util.circular.BOM;
import com.pg.v4.util.circular.CircularUtil;
import com.pg.v4.util.enumeration.CircularConstant;

import matrix.db.Attribute;
import matrix.db.AttributeItr;
import matrix.db.AttributeList;
import matrix.db.AttributeType;
import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MQLCommand;
import matrix.db.MxMessageSupport;
import matrix.db.RelationshipType;
import matrix.util.StringList;

import com.dassault_systemes.enovia.dcl.DCLServiceUtil;
import com.dassault_systemes.enovia.dcl.service.HistoryAuditTrailService;
import com.matrixone.apps.awl.util.Access;
import com.matrixone.apps.cpn.CPNCommonConstants;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.beans.pgProductData;
//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 4-01-2016 - Starts
import com.matrixone.apps.framework.ui.UIUtil;
//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 4-01-2016 - Ends
//Added by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance
import com.pg.v4.util.marketclearance.bean.MarketBean;
import com.pg.v4.util.marketclearance.utils.MarketClearanceCalculation;
import com.pg.v4.util.marketclearance.utils.ProductPart;
//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 37423) - Starts
import com.pg.v4.util.mos.ProductHelper;
import com.pg.v4.util.mos.override.MOSOverrideCalculationUtils;
import com.pg.v4.util.mos.override.MOSOverrideConstants;
import com.pg.v4.util.mos.utils.RawMaterialUtils;
//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 37423) - Ends
//Added by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance
public class pgCountryOfSaleCalculation {


	public static String CHANGED = "Changed";
	public static String NO_LONGER_CLEARED = "No longer cleared";
	public static String ADDED = "Added";  
	//Modified by V4 as per Defect 1579 Start ------
	public static String PGCONFIGCOMMON_COSISACTIVE_TRUE="True";
	public static String PGCONFIGCOMMON_COSISACTIVE_FALSE="False";
	public static final String TYPE_PGCONFIGURATIONADMIN = PropertyUtil.getSchemaProperty("type_pgConfigurationAdmin");

	public static final String messagebody="Hi,\n\nCOS calculation cron job is stuck and is not executed properly for last 2 scheduled runs. Please look into the issue.\n\nThanks.";
	public static final String messageSubject="COS calculation scheduled job problem";

	//Modified by V4 as per Defect 1579 End ------
	
	//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
	public static final String TYPE_PGDEVICEPRODUCTPART = pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART;
	public static final String TYPE_PGASSEMBLEDPRODUCTPART = pgV3Constants.TYPE_DEVICEPRODUCTPART;
	//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
	static String sMOSCircularParentTypes = "";
	public static final String REL_POAToCOUNTRY = pgV3Constants.RELATIONSHIP_MIGRATE_POAToCOUNTRY;
	
	public static Properties props = new Properties();	
	static
	{
		try
		{
			//reading the Constants properties file for the key values.
			File file = new File("Constants.properties");
			FileInputStream fis = new FileInputStream(file);
			props.load(fis);
			sMOSCircularParentTypes=props.getProperty("MOS_Circular_Parent_Types");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	public pgCountryOfSaleCalculation() {
	}

	public void executeCountryClearanceFromCRON(Context context) throws Exception {
		//MQLCommand mqlCommand = new MQLCommand();
		//String strTriggerOFFQuery = "trigger off;";
		try {
			System.out.println("executeCountryClearanceFromCRON is started.");
			//mqlCommand.executeCommand(context, strTriggerOFFQuery);

			resetExpiredRegistrationFPCC(context);

			System.out.println("executeCountryClearanceFromCRON is completed..");

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}finally{

			//String strTriggerONQuery = "trigger on;";
			//mqlCommand.executeCommand(context, strTriggerONQuery);
			System.exit(0);
		}
	}

	/**
	 * This method is called from CRON job run every hour. It will first reset
	 * all the Clearance status whose registration is Expired. Then it will call
	 * the COS Calculation for the IPS whose pgCOSCalculate attribute is True
	 * 
	 * @param context
	 * @param args
	 * @throws Exception
	 * @Author PG V4
	 */
	public void executeCOSCalculationFromCRON(Context context,String []args) throws Exception {
		MQLCommand mqlCommand = new MQLCommand();
		//Modified by V4 as per Defect 1579 Start ------
		BusinessObject boConfig = null;
		//Modified by V4 as per Defect 1579 End ------
		String strTriggerOFFQuery = "trigger off;";
		//Modified by V4 to process cron when input file is provided 
		if(args.length>0){

			try{

				System.out.println("executeCOSCalculationFromCRON is started.");
				processIPSForCOSCalculationClone(context,args);
				StringList objSelect = new StringList(2);
				objSelect.addElement(DomainConstants.SELECT_ID);
				StringBuffer sbWhereClause = new StringBuffer();
				sbWhereClause.append("(attribute[");
				sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSWIP);
				sbWhereClause.append("].value == ");
				sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
				sbWhereClause.append(" || attribute[");
				sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE);
				sbWhereClause.append("].value == ");
				sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
				sbWhereClause.append(") && current==");
				sbWhereClause.append(pgV3Constants.STATE_PART_RELEASE);
				//Modified by DSM-2015x.1 for COS (Req ID- 6278 5936) on 4-01-2016 - Starts
				MapList mlIPS = DomainObject.findObjects(context,pgV3Constants.TYPE_PGFINISHEDPRODUCT +","+pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY+","+pgV3Constants.TYPE_FINISHEDPRODUCTPART+","+pgV3Constants.TYPE_PACKAGINGASSEMBLYPART+","+pgV3Constants.TYPE_FABRICATEDPART,"*","*","*", pgV3Constants.VAULT_ESERVICEPRODUCTION, sbWhereClause.toString(),false,objSelect);
				//Modified by DSM-2015x.1 for COS (Req ID- 6278 5936) on 4-01-2016 - Ends

				if(mlIPS!=null&& mlIPS.size()>0){

					processIPSForCOSCalculation(context);
				}
				System.out.println("executeCOSCalculationFromCRON is completed..");
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				System.exit(0);
			}
		}else{

			try {
				System.out.println("executeCOSCalculationFromCRON is started.");
				mqlCommand.executeCommand(context, strTriggerOFFQuery);
				//Modified by V4 as per Defect 1579 Start ------
				boConfig = new BusinessObject (TYPE_PGCONFIGURATIONADMIN, "pgV4COSConfiguration", "-", "eService Production");
				String strCosIsactive="";
				String costryCount="";
				String toEmailID="";
				if (boConfig.exists(context)) {
					StringList slattribute=new StringList(2);
					slattribute.addElement(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSISACTIVE );
					slattribute.addElement(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSRETRYCOUNT);
					slattribute.addElement(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSADMINMAILID);
					AttributeList attlist = boConfig.getAttributeValues(context,slattribute);
					AttributeItr attrItr   = new AttributeItr(attlist);
					while (attrItr.next())
					{
						Attribute attribute = attrItr.obj();
						String attrName	= attribute.getName().trim();
						if(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSISACTIVE.equalsIgnoreCase(attrName))
						{
							strCosIsactive = attribute.getValue().trim();
						}else if(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSRETRYCOUNT.equalsIgnoreCase(attrName))
						{
							costryCount=attribute.getValue().trim();
						}else if(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSADMINMAILID.equalsIgnoreCase(attrName))
						{
							toEmailID=attribute.getValue().trim();

						}

					}
					if(PGCONFIGCOMMON_COSISACTIVE_TRUE.equalsIgnoreCase(strCosIsactive))
					{
						int count=Integer.parseInt(costryCount);
						count++;
						costryCount=Integer.toString(count);

						String stFormEmail = PersonUtil.getEmail(context);

						boConfig.setAttributeValue(context,pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSRETRYCOUNT,costryCount);
						if(count>2)
						{ 
							try {
								boolean emailStatus = sendEmail(context, stFormEmail, toEmailID, messagebody, messageSubject);

							} catch (Exception e) {

								e.printStackTrace();
							}
						}
						System.exit(0);

					}else{
						Date today = Calendar.getInstance().getTime();
						DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
						String strToday = formatter.format(today);

						AttributeList attributes = new AttributeList(1);
						attributes.addElement(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSISACTIVE), PGCONFIGCOMMON_COSISACTIVE_TRUE));
						attributes.addElement(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_ATTR), strToday));
						attributes.addElement(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSRETRYCOUNT), "1"));
						boConfig.setAttributeValues(context, attributes);
						String strTriggerONQuery = "trigger on;";
						mqlCommand.executeCommand(context, strTriggerONQuery);
						processIPSForCOSCalculation(context);
						//modified by V4 for defect 1843 start ----->
						StringList objSelect = new StringList(2);
						objSelect.addElement(DomainConstants.SELECT_ID);
						StringBuffer sbWhereClause = new StringBuffer();
						sbWhereClause.append("(attribute[");
						sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSWIP);
						sbWhereClause.append("].value == ");
						sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
						sbWhereClause.append(" || attribute[");
						sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE);
						sbWhereClause.append("].value == ");
						sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
						sbWhereClause.append(") && current==");
						sbWhereClause.append(pgV3Constants.STATE_PART_RELEASE);
						//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
						MapList mlIPS = DomainObject.findObjects(context,pgV3Constants.TYPE_PGFINISHEDPRODUCT +","+pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY+","+pgV3Constants.TYPE_FINISHEDPRODUCTPART+","+pgV3Constants.TYPE_PACKAGINGASSEMBLYPART+","+pgV3Constants.TYPE_FABRICATEDPART,"*","*","*", pgV3Constants.VAULT_ESERVICEPRODUCTION, sbWhereClause.toString(),false,objSelect);
						//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
						if(mlIPS != null && mlIPS.size()>0){

							processIPSForCOSCalculation(context);
						}
						// modified for defect 1843 End  ----->
					}
					System.out.println("executeCOSCalculationFromCRON is completed..");
				} else {
					System.out.println("COS Configuration Object does not exist. Please create pgV4COSConfiguration object to continue COS job....");
					System.exit(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}finally{
				mqlCommand.executeCommand(context, strTriggerOFFQuery);
				AttributeList attributes = new AttributeList(1);
				attributes.addElement(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSISACTIVE), PGCONFIGCOMMON_COSISACTIVE_FALSE));
				attributes.addElement(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCONFIGCOMMON_COSRETRYCOUNT), "0"));
				boConfig.setAttributeValues(context, attributes);

				//Modified by V4 as per Defect 1579 End ------
				String strTriggerONQuery = "trigger on;";
				mqlCommand.executeCommand(context, strTriggerONQuery);
				System.exit(0);
			}
		}
	}

	/**
	 * This method will get all Formula Card whose registration is Expired and Clearance status is still Not Cleared
	 * It will set pgCOSCalculate attribute value to  true and OverallClearStatus to "Not Cleared"
	 * @author PG V4 
	 */
	String strOutputFileName = "";


	Long duration=null;
	public void resetExpiredRegistrationFPCC(Context context)throws Exception
	{
		BufferedReader fileReader = null;
		PrintStream out = null;
		try
		{
			Date today = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			String strToday =formatter.format(today);
			SimpleDateFormat sDateFormatinsec = new SimpleDateFormat("yyyyMMdd");
			String sCurDate = sDateFormatinsec.format(today);
			
			String sInputData = null;
			
			String tempfileName = props.getProperty("V4_CRONJOB_LOG_FILEPATH")+File.separator+props.getProperty("Cronjob_INPUT_FC_FILENAME");
			File tempfileFC =new File(tempfileName);
			if(!tempfileFC.exists()){
				tempfileFC.createNewFile();
			}
			tempfileFC.setWritable(true,false);
			tempfileFC.setExecutable(true,false);
			tempfileFC.setReadable(true,false); 
			fileReader = new BufferedReader(new FileReader(tempfileFC));
			Date startTime = new Date();
			strOutputFileName =props.getProperty("V4_CRONJOB_LOG_FILEPATH")+File.separator+props.getProperty("ClearanceCronjob_OUTPUT_FILENAME")+sCurDate+".log";


			File file =new File(strOutputFileName);
			if(!file.exists()){
				file.createNewFile();
			}
			out = new PrintStream(new FileOutputStream(strOutputFileName,true));
			String strWhereCondFO = "relationship["+pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE+"]==True";	
			//Added/Modified by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance Starts
			//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 19-10-2016 - Start
			StringBuilder sbTempQuery = new StringBuilder("temp query bus \"").append(props.getProperty("Cronjob_ALLOWED_TYPES")).append("\" * * where \"").append(strWhereCondFO)
					.append("\" select id dump , output '").append(tempfileFC).append("'");
			String tempquery = sbTempQuery.toString();
			//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 19-10-2016 - End
			MqlUtil.mqlCommand(context, tempquery);
			out.append("\n\n====================== "+strToday+"===========================\n");
			out.append("processing  for Formula Card  Start time "+strToday+"\n");	
			out.append("FC Query file size:  "+tempfileFC.length()+"\n");	
			MarketClearanceCalculation marketClearanceCalculation;
			ProductPart productPart;
			String sPCType = null;
			String sPCName = null;
			String sPCRevision = null;
			String strFCId = null;
			DomainObject domObj=null;
			
			while((sInputData = fileReader.readLine()) != null){
				StringList sl = StringUtil.split(sInputData, ",");
				if(sl!=null && !sl.isEmpty()){
					sPCType = sl.get(0);
					sPCName = sl.get(1);
					sPCRevision = sl.get(2);
					strFCId= sl.get(3);
					domObj=DomainObject.newInstance(context, strFCId);
					if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(sPCType) && !isPartSetProduct(context, domObj)) {
						continue;
					}
					out.append("Processing Formula card "+sPCName+","+sPCRevision+"\n");	
					marketClearanceCalculation = new MarketClearanceCalculation();
					productPart = marketClearanceCalculation.getConnectedMarketBeans(context,domObj);
					if(null != productPart) {
						List<MarketBean> markets = productPart.getMarkets();
						if (null!=markets && !markets.isEmpty()) {
							int marketsSize = markets.size();
							MarketBean marketBean =null;
							String overallClearanceStatus;
							for (int i = 0; i < marketsSize; i++) {
								marketBean=markets.get(i);
								if(marketClearanceCalculation.isAnyRegistrationExpired(marketBean.getMarketRegistrationBeans(), today)){
									try {
										overallClearanceStatus = marketClearanceCalculation.calculateRegistrationClearanceStatus(marketBean.getMarketRegistrationBeans());
										marketClearanceCalculation.updateOverallClearanceStatus(context, marketBean.getConnectionId(), overallClearanceStatus);
										out.append("Formula card "+sPCName+","+sPCRevision+" with rel id "+marketBean.getConnectionId()+" was updated\n");
									}catch (Exception e) {
										out.append("error"+e.getMessage());
									}
								}
							}
						}
					}
				}
			}
			//Added/Modified by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance Ends.
			Date endDate = new Date();
			duration=((endDate.getTime() - startTime.getTime())/1000);
			out.append("Process for FC End: "+(duration) + "secs \n");
		
			fileReader.close();
			tempfileFC.delete();
		}
		catch(Exception  ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally {
			fileReader.close();
			out.close();
		}
	}
	/**
	 * This method will fetch  all IPS whose pgCOSCalculate attribute value is true
	 * it will calculate the COS for those IPS
	 * if we modify this method then same modification need to be done in processIPSForCOSCalculationClone.
	 * @Author PG V4
	 * 
	 */
	public void processIPSForCOSCalculation (Context context) throws Exception  
	{
		try 
		{  
			Date today = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			String strToday =formatter.format(today);
			SimpleDateFormat sDateFormatinsec = new SimpleDateFormat("yyyyMMdd");
			String sCurDate = sDateFormatinsec.format(today);

			int cntIps = 0;
			Date startTime = new Date();
			strOutputFileName =props.getProperty("V4_CRONJOB_LOG_FILEPATH")+File.separator+props.getProperty("Cronjob_OUTPUT_FILENAME")+sCurDate+".log";
			File file =new File(strOutputFileName);
			if(!file.exists()){
				file.createNewFile();
			}
			BufferedReader fileReader = null;
			String sInputData = null;
			// String tempfileName = "pgFinishedProductRecords.txt";
			String tempfileName = props.getProperty("V4_CRONJOB_LOG_FILEPATH")+File.separator+props.getProperty("Cronjob_INPUT_IPS_FILENAME");

			File tempfileIPS =new File(tempfileName);

			String fileName = tempfileIPS.getAbsolutePath();

			if(!tempfileIPS.exists()){
				tempfileIPS.createNewFile();
			}

			tempfileIPS.setWritable(true,false);
			tempfileIPS.setExecutable(true,false);
			tempfileIPS.setReadable(true,false); 

			fileReader = new BufferedReader(new FileReader(tempfileIPS));
			PrintStream out = null;
			//Added by DSM(Sogeti) 2018x.6 for Defect 38965 for MOS Circular : Starts
			boolean isMOSCircularCheckingIsActive = props.getProperty("MOS_Circular_Checking_Is_Active").equalsIgnoreCase(pgV3Constants.KEY_TRUE)?true:false;
			//Added by DSM(Sogeti) 2018x.6 for Defect 38965 for MOS Circular : Ends
			Set scountry=new HashSet();
			//strOutputFileName = props.getProperty("Cronjob_OUTPUT_FILENAME");
			//String fileName= props.getProperty("V4_CRONJOB_IPS_FILEPATH")+File.separator+strOutputFileName+".log";
			out = new PrintStream(new FileOutputStream(strOutputFileName,true));
			out.append("\n\n====================== "+strToday+"===========================\n");
			out.append("process for IPS Start "+startTime+"\n");
			StringBuffer sbWhereClause = new StringBuffer();
			sbWhereClause.append("(attribute[");
			sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSWIP);
			sbWhereClause.append("].value == ");
			sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
			sbWhereClause.append(" || attribute[");
			sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE);
			sbWhereClause.append("].value == ");
			sbWhereClause.append(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
			sbWhereClause.append(") && current==");
			sbWhereClause.append(pgV3Constants.STATE_PART_RELEASE);
			//Modified by DSM-2015x.1 for COS (Req ID- 7530,6278 5936) on 4-01-2016 - Starts
			MqlUtil.mqlCommand(context, "temp query bus '"+pgV3Constants.TYPE_PGFINISHEDPRODUCT+","+pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY+","+pgV3Constants.TYPE_PACKAGINGASSEMBLYPART+","+pgV3Constants.TYPE_FINISHEDPRODUCTPART+","+pgV3Constants.TYPE_FABRICATEDPART+"' * * orderby attribute["+pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE+"] !expand where '"+sbWhereClause.toString()+"' select id dump , output "+tempfileIPS);
			//Modified by DSM-2015x.1 for COS (Req ID- 7530,6278 5936) on 4-01-2016 - Ends
			out.append("Process done for following IPS  :\n");
			out.append("\t Type \t \t  \t   \t\t  \t  Name \t \t\t  Revision  \t    \t ID  \n");
			
			//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - Start
			StringList slattr = new StringList(2);
			slattr.add(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
			slattr.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
			
			String strCOSFPPOverriden = null;
			String strCOSFPPAssemblyType = null;
			////Modified by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
			//Modified by DSM-2018x.3 for COS (Defect ID- 31776,31747,31803) on 25-02-2020 - Start
			//String strCOSFPPOverridenValue = pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN;
			//String strCOSFPPAssemblyTypeValue = pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE;
			//Modified by DSM-2018x.3 for COS (Defect ID- 31776,31747,31803) on 25-02-2020 - End
			//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
			Map strARValueMap = null;
			
			while((sInputData = fileReader.readLine()) != null)
			{
				
				String sPCType = null;
				String sPCName = null;
				String sPCRevision = null;
				String strIPSId = null;	
				StringTokenizer st = new StringTokenizer(sInputData,",");

				if (st.hasMoreTokens())
				{
					sPCType = st.nextToken().trim();
					sPCName = st.nextToken().trim();
					sPCRevision = st.nextToken().trim();
					strIPSId = st.nextToken().trim();
				}
				Date procesDate=new Date();
				out.append(sPCType +"\t \t\t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+" \t "+procesDate+" => Starts\n");
				// fetching this attribute value dynamically as this will get set during COS Calculation .
				//Modified by DSM-2015x.1 for COS removed value on 16-02-2016 - Starts
				String strAttValue = MqlUtil.mqlCommand(context, "print bus " + strIPSId + " select attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"] dump");
				String strAttValueCOSWIP = MqlUtil.mqlCommand(context, "print bus " + strIPSId + " select attribute["+pgV3Constants.ATTRIBUTE_PGCOSWIP+"] dump");
				//Modified by DSM-2015x.1 for COS removed value on 16-02-2016 - Ends
				DomainObject domIps= DomainObject.newInstance(context,strIPSId);
				//Added by DSM-2018x.2 for COS Requirement 28458 on 16-02-2016 - Starts
				
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
				if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(sPCType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(sPCType)||pgV3Constants.TYPE_FABRICATEDPART.equals(sPCType)) {
                                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
					//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - Start
					strARValueMap = domIps.getInfo(context, slattr);
					//Modified by DSM-2018x.3 for COS (Defect ID- 31776,31747,31803) on 25-02-2020 - Start
					//strCOSFPPOverriden = (String)strARValueMap.get("attribute[strCOSFPPOverridenValue]");
					//strCOSFPPAssemblyType = (String)strARValueMap.get("attribute[strCOSFPPAssemblyTypeValue]");
					strCOSFPPOverriden = (String)strARValueMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
					strCOSFPPAssemblyType = (String)strARValueMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
					//Modified by DSM-2018x.3 for COS (Defect ID- 31776,31747,31803) on 25-02-2020 - End

					if((UIUtil.isNotNullAndNotEmpty(strCOSFPPOverriden) && "Yes".equalsIgnoreCase(strCOSFPPOverriden))|| (pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strCOSFPPAssemblyType))){
						continue;
					}
					//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
				}
				
				if(sMOSCircularParentTypes.contains(sPCType) && isMOSCircularCheckingIsActive) {
					//Modified by DSM (Sogeti) 2018x.5 December Downtime for COS Circular Check (Defect ID - 36756) - Starts
					// below code detects circular reference in EBOM Substitute and updates history if found.
					//Added by DSM(Sogeti) 2018x.6 for Defect 38965 for MOS Circular : Starts
					//Added by DSM(Sogeti) 2018x.6 for Defect 38965 for MOS Circular : Ends
						BOM bomObj=new BOM.Structure(context, strIPSId).checkCircular().getInstance();
						boolean circularExist = bomObj.isCircularExist(); 
													
						/*
						 * If circular reference is found. then do the following:
						 * 		1. set pgCOSCalculate to false.
						 * 		2. set pgCOSFlagDate to blank.
						 * 		3. set pgCOSRunate to (today's date) 
						 */
						if(circularExist) {
							// if circular found - then clear attribute values.
							Map<String, String> mpAttributes = new HashMap <> ();
							mpAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSWIP, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
							mpAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
							mpAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE, DomainConstants.EMPTY_STRING);
							// update run date as today's
							mpAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSRUNDATE, new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US).format(new Date()));
							domIps.setAttributeValues(context, mpAttributes);
							HistoryAuditTrailService auditTrailService = DCLServiceUtil.getHistoryAuditTrailService(context);
				            auditTrailService.customHistoryUpdation(context, strIPSId, "Circular Structure", bomObj.getCircularHistory(),
				            		CircularConstant.CUSTOM.getValue());							
							continue;
						} 
					//Modified by DSM (Sogeti) 2018x.5 December Downtime for COS Circular Check (Defect ID - 36756) - Ends
				}
				//Added by DSM-2018x.2 for COS Requirement 28458 on 16-02-2016 - Ends
				if(null != strAttValue && "True".equalsIgnoreCase(strAttValue) || "True".equalsIgnoreCase(strAttValueCOSWIP))
					//if(null!=strAttValue && "True".equalsIgnoreCase(strAttValue))
				  {
					cntIps++;
					//Modified by V4 as per Defect 1579 Start here-----
					
					HashMap hmAttributes = new HashMap();
					hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
					hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSWIP, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
					//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
					hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE, "");
					domIps.setAttributeValues(context, hmAttributes);
					//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends
					//Modified by V4 as per Defect 1579 Ends-----
					//Modified by V4 for defect 3046 - Starts
					if(restrictedCountries.containsKey(strIPSId))
					{
						restrictedCountries.remove(strIPSId);
					}
					//Modified by V4 for defect 3046 - Ends
					//	getCountriesForFinishedProduct(context, strIPSId); // Modified to append processing for each IPS
					//Modified by V4-2013x.4 to call separately for FP and FPP
					//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
					if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(sPCType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(sPCType)||pgV3Constants.TYPE_FABRICATEDPART.equals(sPCType))
					//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
					{
						scountry= getCountriesForFinishedProductPart(context, strIPSId);
					}
					else
					{
						scountry=	getCountriesForFinishedProduct(context, strIPSId);
					}
				}
				procesDate=new Date();
				if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sPCType)){
					// Modified to append processing for each IPS
					//out.append(sPCType +"\t \t\t \t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+"\n");	
					out.append(sPCType +"\t \t\t \t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+" \t "+(null!=scountry?procesDate:" ")+" => Ends\n");	
				}
				else if(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sPCType)){
					// Modified to append processing for each IPS
					//out.append(sPCType +"\t \t\t \t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+"\n");	
					out.append(sPCType +"\t \t\t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+" \t "+(null!=scountry?procesDate:" ")+" => Ends\n");	
				} else {
					out.append(sPCType +"\t \t\t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+" \t "+(null!=scountry?procesDate:" ")+" => Ends\n");
				}
			}
			out.append("Total IPS processed: " + cntIps);
			Date endDate = new Date();
			duration=((endDate.getTime() - startTime.getTime())/1000);
			out.append("IPS Process time  : "+((endDate.getTime() - startTime.getTime())/1000) + "secs \n");
			//out.append("Total Calculation Time : "+(duration + ((endDate.getTime()- startTime.getTime())/1000)) + "secs \n");
			out.close();
			fileReader.close();
			tempfileIPS.delete();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * This method is clone of processIPSForCOSCalculation
	 * This method will read the input file and process it one by one for Calculation 
	 * it will calculate the COS for those IPS
	 * Any changes made on method processIPSForCOSCalculation is required in this method too.
	 * @Author PG V4
	 * 
	 */
	public void processIPSForCOSCalculationClone (Context context , String [] args ) throws Exception  
	{ 
		//System.out.println("inside processIPSForCOSCalculationInFile");
		try 
		{  
			Date today = Calendar.getInstance().getTime();
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			String strToday =formatter.format(today);
			SimpleDateFormat sDateFormatinsec = new SimpleDateFormat("yyyyMMdd");
			String sCurDate = sDateFormatinsec.format(today);

			int cntIps = 0;
			Date startTime = new Date();
			strOutputFileName =props.getProperty("V4_CRONJOB_LOG_FILEPATH")+File.separator+props.getProperty("Cronjob_OUTPUT_FILENAME")+sCurDate+".log";
			File file =new File(strOutputFileName);
			if(!file.exists()){
				file.createNewFile();
			}
			BufferedReader fileReader = null;
			String sInputData = null;
			// String tempfileName = "pgFinishedProductRecords.txt";
			String tempfileName = props.getProperty("V4_CRONJOB_LOG_FILEPATH")+File.separator+props.getProperty("Cronjob_INPUT_IPS_FILENAME");
			if(args.length >0){

				tempfileName=props.getProperty("V4_CRONJOB_LOG_FILEPATH")+File.separator+args[0];
				//System.out.println("tempfileName :"+tempfileName);
			}
			File tempfileIPS =new File(tempfileName);

			String fileName = tempfileIPS.getAbsolutePath();

			if(!tempfileIPS.exists()){
				tempfileIPS.createNewFile();
			}

			tempfileIPS.setWritable(true,false);
			tempfileIPS.setExecutable(true,false);
			tempfileIPS.setReadable(true,false); 

			fileReader = new BufferedReader(new FileReader(tempfileIPS));
			PrintStream out = null;




			//strOutputFileName = props.getProperty("Cronjob_OUTPUT_FILENAME");
			//String fileName= props.getProperty("V4_CRONJOB_IPS_FILEPATH")+File.separator+strOutputFileName+".log";
			out = new PrintStream(new FileOutputStream(strOutputFileName,true));
			out.append("\n\n====================== "+strToday+"===========================\n");
			out.append("process for IPS Start "+startTime+"\n");

			String strWhereclause="attribute["+pgV3Constants.ATTRIBUTE_PGCOSWIP+"] =="+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE+"||attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"] =="+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE+" && current=="+pgV3Constants.STATE_PART_RELEASE;
			// do temp query only when input file is not provided else proceed with input file
			//MqlUtil.mqlCommand(context, "temp query bus '"+pgV3Constants.TYPE_PGFINISHEDPRODUCT+","+pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY+"' * * !expand where '"+strWhereclause+"' select id dump , output "+tempfileIPS);

			out.append("Process done for following IPS  :\n");
			out.append("\t Type \t \t  \t   \t\t  \t  Name \t \t\t  Revision  \t    \t ID  \n");
			while((sInputData = fileReader.readLine()) != null)
			{
				String sPCType = null;
				String sPCName = null;
				String sPCRevision = null;
				String strIPSId = null;				

				StringTokenizer st = new StringTokenizer(sInputData,",");

				if (st.hasMoreTokens())
				{
					sPCType = st.nextToken().trim();
					sPCName = st.nextToken().trim();
					sPCRevision = st.nextToken().trim();
					strIPSId = st.nextToken().trim();
				}
				/*	 Commented because we take input from file so no need to check attribute value dynamically.
				 * // fetching this attribute value dynamically as this will get set during COS Calculation .
				String strAttValue=MqlUtil.mqlCommand(context, "print bus " + strIPSId + " select attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"].value dump");
				String strAttValueCOSWIP=MqlUtil.mqlCommand(context, "print bus " + strIPSId + " select attribute["+pgV3Constants.ATTRIBUTE_PGCOSWIP+"].value dump");
				if(null!=strAttValue && "True".equalsIgnoreCase(strAttValue) || "True".equalsIgnoreCase(strAttValueCOSWIP))
					//if(null!=strAttValue && "True".equalsIgnoreCase(strAttValue))
				{*/
				cntIps++;
				//Modified by V4 as per Defect 1579 Start here-----
				DomainObject domIps= DomainObject.newInstance(context,strIPSId);
				HashMap hmAttributes = new HashMap();
				hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
				hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSWIP, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
				//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
				hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE, "");
				//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends

				domIps.setAttributeValues(context, hmAttributes);
				//Modified by V4 as per Defect 1579 Ends-----
				//Modified by V4 for defect 3046 - Starts
				if(restrictedCountries.containsKey(strIPSId))
				{
					restrictedCountries.remove(strIPSId);
				}
				//Modified by V4 for defect 3046 - Ends
				//					getCountriesForFinishedProduct(context, strIPSId); // Modified to append processing for each IPS
				//Modified for FPP COS implementation by DSO-II
				Set scountry = new HashSet();
				//Modified by V4-2013x.4 to call separately for FP and FPP
				if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(sPCType))
				{
					scountry= getCountriesForFinishedProductPart(context, strIPSId);
				}
				else
				{
					scountry=	getCountriesForFinishedProduct(context, strIPSId);
				}
				//}
				Date procesDate= new Date();
				if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sPCType)){
					// Modified to append processing for each IPS
					//out.append(sPCType +"\t \t\t \t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+"\n");	
					out.append(sPCType +"\t \t\t \t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+" \t "+(null!=scountry?procesDate:" ")+"\n");	
				}
				else if(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sPCType)){
					// Modified to append processing for each IPS
					//out.append(sPCType +"\t \t\t \t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+"\n");	
					out.append(sPCType +"\t \t\t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+" \t "+(null!=scountry?procesDate:" ")+"\n");	
				} else {
					out.append(sPCType +"\t \t\t "+sPCName +"\t\t   "+sPCRevision +"  \t  " +strIPSId+" \t "+(null!=scountry?procesDate:" ")+"\n");
				}
			}
			out.append("Total IPS processed: " + cntIps);
			Date endDate = new Date();
			duration=((endDate.getTime() - startTime.getTime())/1000);
			out.append("IPS Process time  : "+((endDate.getTime() - startTime.getTime())/1000) + "secs \n");
			//out.append("Total Calculation Time : "+(duration + ((endDate.getTime()- startTime.getTime())/1000)) + "secs \n");
			out.close();
			fileReader.close();
			tempfileIPS.delete();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param context
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private void setIPSAttributeValueForpgProduct(Context context, String strObjectID) throws Exception {
		try {
			{		       
				Set IPSSet = new HashSet();
				Map mpIPS= null;
				String strType="";
				String strIPSName="";
				String strIPSid = "";
				StringList busSelect = new StringList(5);
				busSelect.add(DomainConstants.SELECT_TYPE);
				busSelect.add(DomainConstants.SELECT_NAME);
				busSelect.add(DomainConstants.SELECT_ID);
				busSelect.add("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
				DomainObject dom = DomainObject.newInstance(context,strObjectID);


				//for direct EBOM rel
				//Modified by V4-2013x.4 for updating pgCOSCalculate attribute on FPP, FP and PSUB
				MapList mlIPS = new MapList();
				setParentIPSCosCalculate( context, dom);
				/*MapList mlIPS = dom.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM ,pgV3Constants.TYPE_PART, true,false, 2, busSelect, null, null, null, null, pgV3Constants.TYPE_PGFINISHEDPRODUCT +","+ pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY, null);

				if(mlIPS != null && mlIPS.size() > 0)
				{
					for(int iIndex=0;iIndex<mlIPS.size();iIndex++)
					{
						Map mapIPSData = (Map)mlIPS.get(iIndex);
						strIPSid =(String)mapIPSData.get(DomainConstants.SELECT_ID);
						String attValue =(String)mapIPSData.get("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");

						if(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE.equalsIgnoreCase(attValue))
						{
							IPSSet.add(strIPSid);// to add in IPS SET
						}

					}	
				}*/	


				//*********for **** rel pgDefinesMaterial and then EBOM rel****************************
				//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
				StringBuffer sbType = new StringBuffer(pgV3Constants.TYPE_PGRAWMATERIAL).append(",").append(pgV3Constants.TYPE_RAWMATERIALPART);
				MapList mlRawMaterial = dom.getRelatedObjects(context,pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL,sbType.toString(), busSelect, null, false, true,(short)1, null,null);
				//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
				if(mlRawMaterial != null && mlRawMaterial.size()>0)
				{
					for(int iIndex=0;iIndex<mlRawMaterial.size();iIndex++)
					{
						Map mapRawMaterial = (Map) mlRawMaterial.get(iIndex);
						strType = (String)mapRawMaterial.get(DomainConstants.SELECT_TYPE);
						String   strRMName=(String)mapRawMaterial.get(DomainConstants.SELECT_NAME);
						String	strRMid =(String)mapRawMaterial.get(DomainConstants.SELECT_ID);
						// Added by Kannan -Start
						String strSubtitutes = MqlUtil.mqlCommand(context, "print bus " + strRMid  + " select relationship[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.id dump");
						if((null!= strSubtitutes)&& (!strSubtitutes.isEmpty()) ){
							StringList slSubstitute = FrameworkUtil.split(strSubtitutes, ",");
							for(Iterator iterator= slSubstitute.iterator();iterator.hasNext();)
							{
								String strpgPhaseID = (String) iterator.next();
								DomainObject dompgPhase =DomainObject.newInstance(context, strpgPhaseID);
								 //Modified by V4-2013x.4 for updating pgCOSCalculate attribute on FPP, FP and PSUB
								 setParentIPSCosCalculate( context, dompgPhase);
								/*MapList mlips= dompgPhase.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM,pgV3Constants.TYPE_PGFINISHEDPRODUCT +","+ pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY, true,false, 1, busSelect, null, null, null, null,"" , null);
								if(mlips != null && mlips.size() > 0)
								{
									for(int kIndex=0;kIndex<mlips.size();kIndex++)
									{
										Map mapIPSData = (Map)mlips.get(kIndex);
										strIPSid =(String)mapIPSData.get(DomainConstants.SELECT_ID);
										String attValue =(String)mapIPSData.get("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
										if(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE.equalsIgnoreCase(attValue))
										{
											IPSSet.add(strIPSid);
										}								
									}	
								}*/
							}// end of iterator
						}
						// Added by Kannan -End

						DomainObject domRM= DomainObject.newInstance(context,strRMid);
						//Modified by V4-2013x.4 for updating pgCOSCalculate attribute on FPP, FP and PSUB
					setParentIPSCosCalculate( context, domRM);
						/*mlIPS = domRM.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM,pgV3Constants.TYPE_PART, true,false, 2, busSelect, null, null, null, null, pgV3Constants.TYPE_PGFINISHEDPRODUCT +","+ pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY, null);

						if(mlIPS != null && mlIPS.size() > 0)
						{
							for(int intIndex=0;intIndex<mlIPS.size();intIndex++)
							{
								Map mapIPSData = (Map)mlIPS.get(intIndex);
								strType = (String)mapIPSData.get(DomainConstants.SELECT_TYPE);
								strIPSName=(String)mapIPSData.get(DomainConstants.SELECT_NAME);
								strIPSid =(String)mapIPSData.get(DomainConstants.SELECT_ID);
								String attValue =(String)mapIPSData.get("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");

								if(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE.equalsIgnoreCase(attValue))
								{
									IPSSet.add(strIPSid);// to add in IPS SET
								}
							}	
						}*/	
					}
				} 
				//**************************************
				//for EBOMsiinstitue relation 
				String strSubtitutes = MqlUtil.mqlCommand(context, "print bus " + strObjectID + " select relationship[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].fromrel.from.id dump");

				if((null!= strSubtitutes)&& (!strSubtitutes.isEmpty()) ){
					StringList slSubstitute = FrameworkUtil.split(strSubtitutes, ",");
					for(Iterator iterator= slSubstitute.iterator();iterator.hasNext();)
					{
						String strpgPhaseID = (String) iterator.next();
						DomainObject dompgPhase =DomainObject.newInstance(context, strpgPhaseID);
						//Modified by V4-2013x.4 for updating pgCOSCalculate attribute on FPP, FP and PSUB
					setParentIPSCosCalculate( context, dompgPhase);
						/*MapList mlips= dompgPhase.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM,pgV3Constants.TYPE_PART, true,false, 1, busSelect, null, null, null, null, pgV3Constants.TYPE_PGFINISHEDPRODUCT +","+ pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY, null);
						// MapList mlips = dompgPhase.getRelatedObjects(context,pgV3Constants.RELATIONSHIP_EBOM,pgV3Constants.TYPE_PART, busSelect, null, true, false,(short)1, null,null);
						if(mlips != null && mlips.size() > 0)
						{
							for(int iIndex=0;iIndex<mlips.size();iIndex++)
							{
								Map mapIPSData = (Map)mlips.get(iIndex);
								strIPSid =(String)mapIPSData.get(DomainConstants.SELECT_ID);
								String attValue =(String)mapIPSData.get("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");

								if(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE.equalsIgnoreCase(attValue))
								{
									IPSSet.add(strIPSid);
								}								
							}	
						}*/
					}// end of iterator
				} // end of if substitute 
				//for setting pgCOSCalculate attribute vale to true;
				if ((IPSSet != null)&&(IPSSet.size()>0))
				{
					for (Iterator iterator = IPSSet.iterator(); iterator.hasNext();) {
						String strIPSId = (String) iterator.next();

						DomainObject domips =DomainObject.newInstance(context, strIPSId);
						try{
							//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
							setMultipleAttributeValues(context, domips); 
							//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends
							setParentIPSCosCalculate( context, domips);
						}catch (FrameworkException e) 
						{
							e.printStackTrace();
						}	
					}
				}// if IPSSet ends here

			} // end of if args not null

		}catch (Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
	}// end of method 

	Map<String, Set> restrictedCountries = new HashMap<String, Set>();

	public void getCountriesForFinishedProduct(Context context, String args[])throws Exception {
		String IPSId = (String) JPO.unpackArgs(args);		
		getCountriesForFinishedProduct(context, IPSId);
	}

	/* Method to update attribute when Circular reference is found.
	 * @param context - Context
	 * @param IPSId - String
	 * @throws FrameworkException - exception
	 */
	public void updateOnCircular(Context context, String IPSId) throws FrameworkException {
		HashMap hmAttributes = new HashMap();
		DomainObject domIps = DomainObject.newInstance(context, IPSId);
		//attributes.put(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
		hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSWIP, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
		//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
		hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE, "");
		//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends
		domIps.setAttributeValues(context, hmAttributes);
	}

	/**
	 * This method will be called for COS Calculation. It is also recursively called during the calculation
	 * */
	public Set getCountriesForFinishedProduct(Context context, String IPSId) throws Exception {
		//structure the EBOM map with parent and child combination. Also fetch the countries for the children other than IPS.
		// Modified by DSM(sogeti) for 2018x.5 COS Defect 33103 - Starts
		
		MapList mlBoms = getEBOMsWithSubstitute(context, IPSId);
		
		boolean isCircular = isCircular(context, IPSId, mlBoms);		
		// Modified by DSM(sogeti) for 2018x.5 COS Defect 33103 - Ends
		Set countries = new HashSet();

		// Modified by DSM(sogeti) for 2018x.5 COS Defect 33103 - Starts
		Map<String, pgProductData> ebomMap = createParentChildStructure(context, IPSId, mlBoms);
		// Modified by DSM(sogeti) for 2018x.5 COS Defect 33103 - Ends
		// iterate this structure  and calculate Countries for IPS
		countries = calculateCOS(context, IPSId, ebomMap);
		return countries;
	}
	//Added new method by V4-2013x.4 for implementing COS for Finished Product Part
	public Set getCountriesForFinishedProductPart(Context context, String IPSId) throws Exception {
		//System.out.println("Inside main method for getCountriesForFinishedProductPart for "+IPSId);
		//structure the EBOM map with parent and child combination. Also fetch the countries for the children other than IPS.
		Set countries = new HashSet();
		MapList ebomMapList = createParentChildStructureForFPP(context, IPSId);
		//System.out.println("\n ebomMapList "+ebomMapList);
		countries = calculateCOSForFPP(context, IPSId, ebomMapList);
		return countries;
	}

	//Added new method by V4-2013x.4 for implementing COS for Finished Product Part css type
	private Set<String> calculateCOSForFPP(Context context, String childId, MapList ebomMapList) throws Exception {
		//System.out.println("\n Calculating ctries for "+childId);
		Set countries = new HashSet();
		Set stCountries = new HashSet();
		Map childCountriesMap = new HashMap();
		Set<String> restrictedSet = new HashSet(); 

		//System.out.println("ebomMapList "+ebomMapList);
		boolean isNCC = true;
		//Before proceeding for calculation, check the specification subtype of FPP. If it is FPP, isArtExist and isFCExist both should be true
		updateFCOrArtExist(context,childId);
		DomainObject dObjChild = DomainObject.newInstance(context, childId);
		StringList slObjectInfo = new StringList(4);
		slObjectInfo.addElement(DomainConstants.SELECT_TYPE);
		slObjectInfo.addElement(DomainConstants.SELECT_CURRENT);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		
		Map mapAttributes = dObjChild.getInfo(context,slObjectInfo);
		String strArtExist = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		String strFCExist = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		String strIncludeInCOS = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
		String strType = (String)mapAttributes.get(DomainConstants.SELECT_TYPE);
		String strState = (String)mapAttributes.get(DomainConstants.SELECT_CURRENT);
		String strSubType = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		
		//System.out.println("strArtExist "+strArtExist+" strFCExist "+strFCExist+" type "+type);
		
		//If FPP is not released/complete and either one component is not released, do not calculate
		//Modified by DSM-2015x.1 for COS (Req ID- 6310,6311,6312) on 4-01-2016 - Starts
		if(!"Release".equalsIgnoreCase(strState) && !"Complete".equalsIgnoreCase(strState) && (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strType) ||  pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType)))
		{
		//Modified by DSM-2015x.1 for COS (Req ID- 6310,6311,6312) on 4-01-2016 - Ends
			boolean blIsPreliminaryChild = false;
			if(null != ebomMapList && !ebomMapList.isEmpty())
			{
				int iEbomMapSize=ebomMapList.size();
				for(int i=0;i<iEbomMapSize;i++)
				{
					Map mapTempEBOM = (Map)ebomMapList.get(i);
					String strCurrent = (String)mapTempEBOM.get(DomainConstants.SELECT_CURRENT);
					if(!"Release".equalsIgnoreCase(strCurrent) && !"Complete".equalsIgnoreCase(strCurrent) && !"Obsolete".equalsIgnoreCase(strCurrent))
					{
						blIsPreliminaryChild = true;
						break;
					}
				}
				if(blIsPreliminaryChild)
				{
					dObjChild.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
                                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
					updateCountries(context, childId, countries,restrictedSet, new HashSet());
                                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
					return countries;
				}
			}
		}
		
		//If Specification subtype is FPP
		//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 03-12-2018 - Start
		if(!"FWIP".equalsIgnoreCase(strSubType) && !"FWIP-Finished Work in Process".equalsIgnoreCase(strSubType) && !(pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strSubType)) && strType.equalsIgnoreCase(pgV3Constants.TYPE_FINISHEDPRODUCTPART))
		{
			//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 03-12-2018 - End
			//System.out.println("Inside not FWIP");
			if(!"true".equalsIgnoreCase(strFCExist) || !"true".equalsIgnoreCase(strArtExist))
			{
				//System.out.println("Inside either one false");
				//Connect calculated countries to FPP
				if("false".equalsIgnoreCase(strIncludeInCOS))
				{
					dObjChild.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
					setParentIPSCosCalculate(context,dObjChild);
				}
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
				updateCountries(context, childId, countries,restrictedSet, new HashSet());
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
				return countries;
			}
		}

		try {
			if(null != ebomMapList && !ebomMapList.isEmpty())
			{
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
				MOSOverrideCalculationUtils mosPOAOverride = new MOSOverrideCalculationUtils(context, childId);
				boolean isParentPartHasPOAOverride = mosPOAOverride.isPartHasPOAOverride();
				if(isParentPartHasPOAOverride) {
					mosPOAOverride.fetchPOAOverrideMarkets();
				} // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
				String strChildType = "";
				String strName = "";
				String strID = "";
				//Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - Start
				String strAssemblyType = "";
				//Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
				//Modified/Added DSM(Sogeti) 18x.6 Refactor - Starts
 				int iEbomMapSize = ebomMapList.size();
				for(int i=0;i<iEbomMapSize;i++)
				{
				//Modified/Added DSM(Sogeti) 18x.6 Refactor - Ends
					Map mapTempEBOM = (Map)ebomMapList.get(i);
					strChildType = (String)mapTempEBOM.get("type");
					strName = (String)mapTempEBOM.get("name");
					strID = (String)mapTempEBOM.get("id");
				//Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - Start
					strAssemblyType = (String)mapTempEBOM.get("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]");
			        //Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
					stCountries = new HashSet();
					//Based on type call method to get countries
					//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
					//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36270) - Starts
					if(pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(strChildType) || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(strChildType)
							|| TYPE_PGDEVICEPRODUCTPART.equalsIgnoreCase(strChildType) || TYPE_PGASSEMBLEDPRODUCTPART.equalsIgnoreCase(strChildType) || pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strChildType)){
					//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36270) - Ends
						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
						stCountries=getCountriesForFormulatedProduct( context, strID, new StringList());
						// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
                                                if(isParentPartHasPOAOverride) {
							mosPOAOverride.addProductPartIntersectionClearedMarkets(stCountries);
						} // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
						//Commented by V4-IPM/DSO-2013x.5 for Defect-3674 Starts
						/*if(null != stCountries && stCountries.size() > 0)
						{*/
						//Commented by V4-IPM/DSO-2013x.5 for Defect-3674 Ends	
							isNCC = false;
						//}

						//System.out.println("Ctries for FC "+stCountries);
						//childCountriesMap.put(strID, stCountries);
					} else if(pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(strChildType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strChildType) || pgV3Constants.TYPE_PGONLINEPRINTINGPART.equalsIgnoreCase(strChildType)){
						stCountries=getCountriesForPackingMaterial(context, strID, new StringList());
						if(null != stCountries && stCountries.size() > 0)
						{
							isNCC = false;
						}

						//System.out.println("Ctries for IPMS "+stCountries);
						//childCountriesMap.put(strID, stCountries);
					}
					//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start 
					else if(pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(strChildType) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(strChildType)){
						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
						//System.out.println("strID:" + strID);
						//System.out.println("strType:" + strType);
						stCountries=getCountriesForRawMaterial( context, strID, new StringList());
						
						//Added by DSM-2022x.5 April CW for MOS (Defect ID- 56653) - Starts
						if(Boolean.TRUE.equals(isParentPartHasPOAOverride) && stCountries != null) {
							mosPOAOverride.addProductPartIntersectionClearedMarkets(stCountries);
						}
						//Added by DSM-2022x.5 April CW for MOS (Defect ID- 56653) - Ends
						if (stCountries != null && stCountries.size() >0) {
							isNCC = false;
						}

						//System.out.println("Ctries for IRMS "+stCountries);
						//childCountriesMap.put(strID, stCountries);
					} else if((strChildType.equalsIgnoreCase(pgV3Constants.TYPE_PGFINISHEDPRODUCT) || (strChildType.equalsIgnoreCase(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY))))
					{
						stCountries = getCountriesForFinishedProduct(context, strID);
						
						//If FP or PSUB is NCC, do not add for calculation
		
							DomainObject dObjIPS = DomainObject.newInstance(context, strID);
							// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
							StringList selectTables = new StringList(2);
							selectTables.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
							selectTables.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
							
							Map mapCSSData = (Map)dObjIPS.getInfo(context, selectTables);
							
							String strIPSIncludeInCOS =(String) mapCSSData.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
							if("true".equalsIgnoreCase(strIPSIncludeInCOS))
							{
								isNCC = false;
								String sFCExist = (String)mapCSSData.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
								boolean isFCExist = (UIUtil.isNotNullAndNotEmpty(sFCExist) && pgV3Constants.KEY_YES.equalsIgnoreCase(sFCExist))?Boolean.TRUE:Boolean.FALSE;
								if(isFCExist) {
									mosPOAOverride.fetchMarketOfSaleResult(dObjIPS);
								} // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
							} 
							 else
							{
								stCountries = null;
							} 
							
						//System.out.println("strIPSIncludeInCOS"+strIPSIncludeInCOS+" and stCountries "+stCountries);
						//childCountriesMap.put(strID, stCountries);
					}
					
					//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
					//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 03-12-2019 - Start
					else if((pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strChildType) && !(pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strAssemblyType))) || (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strChildType)) || (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strChildType)))	
					//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
					//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 03-12-2019 - End
					{
						//System.out.println("Calculating for FPP/PAP");
						//If pgCOSCalculate is true then only call the method else get countries directly
						DomainObject dObjFPP = DomainObject.newInstance(context, strID);
						StringList slFPPData = new StringList(4);
						slFPPData.addElement(DomainConstants.SELECT_TYPE);
						slFPPData.addElement("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
						slFPPData.addElement("attribute[" +pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN+ "]");
						// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
                                                slFPPData.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
                                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
						Map mapFPPData = (Map)dObjFPP.getInfo(context, slFPPData);
						//String strType = (String)mapIPSData.get(DomainConstants.SELECT_TYPE);
						String strAttributeCOS = (String)mapFPPData.get("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
						String strAttributeCOSOverriden = (String)mapFPPData.get("attribute[" +pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN+ "]");
					//	System.out.println("strAttributeCOSOverriden "+strAttributeCOSOverriden);
						if(!"True".equalsIgnoreCase(strAttributeCOS) || ("Yes".equalsIgnoreCase(strAttributeCOSOverriden) && "True".equalsIgnoreCase(strAttributeCOS)))
						{
							//System.out.println("Inside pgCOSCalculate false");
							StringList objSelect = new StringList(1);
							objSelect.add(DomainConstants.SELECT_NAME);
							StringList relSelect = new StringList(2);
							relSelect.add(DomainConstants.SELECT_RELATIONSHIP_ID);
							relSelect.add("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
							// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
                                                        relSelect.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);
                                                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
							//Expand this FPP and get countries attached
							MapList mlFPPCountryData = (MapList)dObjFPP.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,
									CPNCommonConstants.TYPE_COUNTRY, objSelect, relSelect, false, true, (short) 1, "", "");
							//System.out.println("mlFPPCountryData "+mlFPPCountryData);
							if(null!=mlFPPCountryData && mlFPPCountryData.size() > 0)
							{
								for(int iCtryCount=0;iCtryCount<mlFPPCountryData.size();iCtryCount++)
								{
									Map mapCountryData = (Map)mlFPPCountryData.get(iCtryCount);
									//System.out.println("mapCountryData "+mapCountryData);
									String strCountryName = (String)mapCountryData.get(DomainConstants.SELECT_NAME);
									//System.out.println("strCountryName "+strCountryName);
									stCountries.add(strCountryName);
									//System.out.println("stCountries "+stCountries);
									String restriction=(String)mapCountryData.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
									//System.out.println("restriction "+restriction);
									if("Yes".equalsIgnoreCase(restriction)){
										restrictedSet.add(strCountryName) ;
									}
								}								
								//If there are restricted countries, add them to final restricted list

							}
						}
						else 
						{	
							stCountries = getCountriesForFinishedProductPart(context, strID);
							
						}
						DomainObject dObjIPS = DomainObject.newInstance(context, strID);
						
						//System.out.println("FPP + PAP stCountries" + stCountries);
						//System.out.println("FPP + PAP stCountries 1" + isNCC);

							//childCountriesMap.put(strID, stCountries);
							//If FPP or PAP is NCC, do not add for calculation
							// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
                                                        slFPPData = new StringList(2);
							slFPPData.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
							slFPPData.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
							mapFPPData = (Map)dObjIPS.getInfo(context, slFPPData);
							
							String strIPSIncludeInCOS =(String) mapFPPData.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
							if("true".equalsIgnoreCase(strIPSIncludeInCOS))
							{
								String sFCExist = (String)mapFPPData.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
								boolean isFCExist = (UIUtil.isNotNullAndNotEmpty(sFCExist) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(sFCExist))?Boolean.TRUE:Boolean.FALSE;
								if(isFCExist) {
									mosPOAOverride.fetchMarketOfSaleResult(dObjIPS);
								} // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
								isNCC = false;
							}
							else
							{
								stCountries = null;
							} 

					}
					//System.out.println("stCountries:" + stCountries);
					//System.out.println("strID:" + strID);
					
					//Modified For defect 1927 starts by V4-2013x.4
					//if(null != stCountries)
					//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - Start
					//Modified by DSM-2018x.3 June Release for COS (Defect ID- 3331) on 20-05-2020 - Starts
					//if(null != stCountries &&  !stCountries.isEmpty() && (!(pgV3Constants.TYPE_PGPROMOTIONALITEMPART.equals(strChildType) && !pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART.equals(strChildType))))
					if(null != stCountries && (!(pgV3Constants.TYPE_PGPROMOTIONALITEMPART.equals(strChildType) && !pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART.equals(strChildType))))
					{
						childCountriesMap.put(strID, stCountries);
						//}
					}
					//Modified by DSM-2018x.3 June Release for COS (Defect ID- 3331) on 20-05-2020 - Ends
					//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - End
						//Modified For defect 1927 starts by V4-2013x.4
					//Write all countries having restriction
					if(restrictedCountries.containsKey(strID))						
						restrictedSet.addAll(restrictedCountries.get(strID));
				}
				//System.out.println("childCountriesMap "+childCountriesMap);
				//System.out.println("restrictedSet "+restrictedSet);
				//if IPS contains restricted countries
				if(restrictedCountries.containsKey(childId))						
					restrictedSet.addAll(restrictedCountries.get(childId));

				//At this point we have all countries on all components, perform AND operation to get intersecting countries
				//System.out.println("\n Set for calculation " +childCountriesMap);
				countries= getIntersectingCountries(childCountriesMap);
				//System.out.println("countries:" + countries);
				// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
				mosPOAOverride.calculatePOAOverrideMarkets(countries);
				
				//Connect calculated countries to FPP
				updateCountries(context, childId, countries,restrictedSet, mosPOAOverride.getChildComponentLRRMarkets());
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
			}
			//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
			StringList slObjectSelect=new StringList(2);
			slObjectSelect.addElement(DomainConstants.SELECT_ID);
			slObjectSelect.addElement(DomainConstants.SELECT_CURRENT);
			MapList mpAltAPPList = dObjChild.getRelatedObjects(context, //context user
											pgV3Constants.RELATIONSHIP_ALTERNATE, //relationship pattern
											pgV3Constants.TYPE_PACKAGINGMATERIALPART, //type pattern
											slObjectSelect, //bus select
											null, //rel select
											true, //get to
											false, //get from
											(short)1,//level
											"", //bus where
											null, //rel where
											0);//limit
			//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
			//Update No Country Components attribute
			//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
			if(("FWIP".equalsIgnoreCase(strSubType) || "FWIP-Finished Work in Process".equalsIgnoreCase(strSubType) || (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strType)) || (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType))) && isNCC)
			//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
			{
				//System.out.println("Inside:" + isNCC);

				if("true".equalsIgnoreCase(strIncludeInCOS))
				{
					dObjChild.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
					setParentIPSCosCalculate(context,dObjChild);
					//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
					if(mpAltAPPList != null && !mpAltAPPList.isEmpty()) {
						Map mapTemp;
						DomainObject doAltAPPObj = null;
						int iMapSize = mpAltAPPList.size();
						for(int z=0; z<iMapSize; z++) {
							mapTemp = (Map) mpAltAPPList.get(z);
							if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase((String)mapTemp.get(DomainConstants.SELECT_CURRENT))) {
								doAltAPPObj = DomainObject.newInstance(context, (String) mapTemp.get(DomainConstants.SELECT_ID));
								setParentIPSCosCalculate(context, doAltAPPObj);
							}
						}
					}
					//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
				} 
				
				Date today = Calendar.getInstance().getTime();
				DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
				String strToday = formatter.format(today);
				AttributeList alAttributes = new AttributeList(3);
				alAttributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCOSWIP), pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE));
				alAttributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCOSRUNDATE),strToday));
				//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
				alAttributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE),""));
				//Added by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends
				dObjChild.setAttributes(context, alAttributes);
			}
			else
			{
				if("false".equalsIgnoreCase(strIncludeInCOS))
				{
					dObjChild.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
					setParentIPSCosCalculate(context,dObjChild);
					//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
					if(mpAltAPPList != null && !mpAltAPPList.isEmpty()) {
						Map mapTemp;
						DomainObject doAltAPPObj = null;
						int iMapSize = mpAltAPPList.size();
						for(int z=0; z<iMapSize; z++) {
							mapTemp = (Map) mpAltAPPList.get(z);
							if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase((String)mapTemp.get(DomainConstants.SELECT_CURRENT))) {
								doAltAPPObj = DomainObject.newInstance(context, (String) mapTemp.get(DomainConstants.SELECT_ID));
								setParentIPSCosCalculate(context, doAltAPPObj);
							}
						}
					}
					//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
				}
			}
			//System.out.println("\n final countries for "+childId+" ---> "+countries);
			//System.out.println("\n restrictedCountries "+restrictedCountries);
			//System.out.println("\n restrictedCountries "+restrictedSet);
		} catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return countries;
	}
	/**
	 * This method is a recursive method that calculates the COS for an IPS by drilling through its child IPS
	 * 
	 * Logic: 
	 * 1) if IPS then drill down and call COS
	 * 2) if Not IPS then fetch the connected countries and return
	 * */
	private Set<String> calculateCOS(Context context, String strChildId, Map<String, pgProductData> ebomMap) throws Exception {
		//System.out.println("Calculating for FP/PSUB "+strChildId);
		// Modified by V4 for No Country Component 18/07/2013 Start
		Set childType=new HashSet();// this set will contain child of PSUB.
		//Modified by V4 for No Country Component 18/07/2013 End
		Set countries = new HashSet();
		//this Map will contain countries of the IPS and its siblings. (Siblings include FC, IRMS, IPMS and Substitutes)
		Map childCountriesMap = new HashMap();
		// iterate this structure  and calculate Countries for IPS
		pgProductData productData = ebomMap.get(strChildId);

		//Modified by V4-2013x.4 for checking if PSUB, FP has at least one Art/FC on exploded structure
		updateFCOrArtExist(context,strChildId);
		DomainObject dObjChild = DomainObject.newInstance(context, strChildId);

		//Get Specification Subtype value
		Set<String> restrictedSet = new HashSet();

		
		StringList slObjectInfo = new StringList(4);
		slObjectInfo.addElement(DomainConstants.SELECT_TYPE);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
		slObjectInfo.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		
		Map mapAttributes = dObjChild.getInfo(context,slObjectInfo);
		String strArtExist = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		String strFCExist = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		String strIncludeInCOS = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
		String strObjectType = (String)mapAttributes.get(DomainConstants.SELECT_TYPE);
		String strSubType = (String)mapAttributes.get(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		
		/*Map mapAttributes = dObjChild.getAttributeMap(context);
		String strArtExist = (String)mapAttributes.get(pgV3Constants.ATTRIBUTE_ISARTEXIST);
		String strFCExist = (String)mapAttributes.get(pgV3Constants.ATTRIBUTE_ISFCEXIST);
		String strIncludeInCOS = (String)mapAttributes.get(pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS);
		String strSubType = (String)mapAttributes.get(pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE);*/
		//System.out.println("strArtExist "+strArtExist+" strFCExist "+strFCExist+" strObjectType "+strObjectType+" strSubType "+strSubType);
		//If Specification subtype is FPP
		if(!"FWIP".equalsIgnoreCase(strSubType) && !"FWIP-Finished Work in Process".equalsIgnoreCase(strSubType) && strObjectType.equalsIgnoreCase(pgV3Constants.TYPE_PGFINISHEDPRODUCT))
		{
			//System.out.println("Inside not FWIP");
			if(!"true".equalsIgnoreCase(strFCExist) || !"true".equalsIgnoreCase(strArtExist))
			{
				//System.out.println("Inside either one false");
				//Connect calculated countries to FPP
				if("false".equalsIgnoreCase(strIncludeInCOS))
				{
					dObjChild.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
					setParentIPSCosCalculate(context,dObjChild);
				}
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
				updateCountries(context, strChildId, countries,restrictedSet,new HashSet());
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
				return countries;
			}
		}
		
		String strType = productData.getType();
		// if it is IPS then drill down else fetch the connected countries and return
		//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
		if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strType) || (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strType)) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType))
		//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
		{
                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
			MOSOverrideCalculationUtils mosPOAOverride = new MOSOverrideCalculationUtils(context, strChildId);
			boolean isParentPartHasPOAOverride = mosPOAOverride.isPartHasPOAOverride();
			if(isParentPartHasPOAOverride) {
				mosPOAOverride.fetchPOAOverrideMarkets();
			} // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
			// this set contains countries that have restriction on them in a particular IPS BOM assembly
			//Set<String> restrictedSet = new HashSet(); 
			Set<String> children =  productData.getChildren();

			//if IPS contains restricted countries
			if(restrictedCountries.containsKey(strChildId))						
				restrictedSet.addAll(restrictedCountries.get(strChildId));

			//iterate each child and perform COS calculation on each of them
			for (Iterator childItr = children.iterator(); childItr.hasNext();) {
				String child = (String) childItr.next();
				// Modified by V4 for No Country Component 18/07/2013 Start
				pgProductData pgtempData =ebomMap.get(child);
				String strIncludeInCos=pgtempData.getIncludeInCos();
				String strParentId=pgtempData.getParentid();
				String sType=pgtempData.getType();
			
				//Modified for defect 2909 - If type is IPMS, check if its substitute has countries. If yes, pgIncludeInCOS attribute of parent IPS will be set to true. pgtempData.isIPMSconsider() will give value if IPMS or substitute has Country

				if(!((pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(sType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(sType) || pgV3Constants.TYPE_PGONLINEPRINTINGPART.equalsIgnoreCase(sType)) && !pgtempData.isIPMSconsider()) && !"False".equalsIgnoreCase(strIncludeInCos)){
					childType.add(sType);

				}
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
				if((pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sType)|| pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sType)) && "False".equalsIgnoreCase(strIncludeInCos) && strParentId.equalsIgnoreCase(strChildId))
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
				{
					//Modified for defect 2911 - If pgIncludeInCos is False then do not include in COS Calculation, but get countries on its substitute and put it in MAP	
					if(pgtempData.isSubstitute())
					{
						childCountriesMap.put(child,pgtempData.getCountries());
						childType.add(sType);
					}
					continue;
				}// Modified by V4 for No Country Component 18/07/2013 Ends
			
				//Modified by V4 for defect 1317
				Set<String> childCountries = new HashSet();
				StringList objSelect = new StringList(1);
				objSelect.add(DomainConstants.SELECT_NAME);
				StringList relSelect = new StringList(1);
				relSelect.add(DomainConstants.SELECT_RELATIONSHIP_ID);
				relSelect.add("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
				DomainObject dObjIPS = DomainObject.newInstance(context, child);
				StringList slIPSData = new StringList(2);
				slIPSData.addElement(DomainConstants.SELECT_TYPE);
				slIPSData.addElement("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
				Map mapIPSData = (Map)dObjIPS.getInfo(context, slIPSData);
				String strBOMType = (String)mapIPSData.get(DomainConstants.SELECT_TYPE);
				String strAttributeCOS = (String)mapIPSData.get("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
				//Modified by V4-2013x - Removed condition for substitute for performance issue
				
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
				//Modified by DSM(Sogeti) 2015x.5 for COS Defect Id.16283 on 13-Feb-2018 - START
				if((pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strBOMType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strBOMType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strBOMType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strBOMType)) && (!"True".equalsIgnoreCase(strAttributeCOS) || pgtempData.isSubstitute()))
				//Modified by DSM(Sogeti) 2015x.5 for COS Defect Id.16283 on 13-Feb-2018 - END
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
				{
					//Expand this IPS and get countries attached
					MapList mlIPSCountryData = (MapList)dObjIPS.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,
							CPNCommonConstants.TYPE_COUNTRY, objSelect, relSelect, false, true, (short) 1, "", "");
					if(null!=mlIPSCountryData && mlIPSCountryData.size() > 0)
					{
						for(int i=0;i<mlIPSCountryData.size();i++)
						{
							Map mapCountryData = (Map)mlIPSCountryData.get(i);
							String strCountryName = (String)mapCountryData.get(DomainConstants.SELECT_NAME);
							childCountries.add(strCountryName);
							String restriction=(String)mapCountryData.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
							if("Yes".equalsIgnoreCase(restriction)){
								restrictedSet.add(strCountryName) ;
							}
						}
					}
				}
				else
				{
					//System.out.println("child:" + ebomMap);

					childCountries = calculateCOS(context,child,ebomMap);
                                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
					if(pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(strBOMType) || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(strBOMType)
							|| TYPE_PGDEVICEPRODUCTPART.equalsIgnoreCase(strBOMType) || TYPE_PGASSEMBLEDPRODUCTPART.equalsIgnoreCase(strBOMType) || pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strBOMType)){
						if(isParentPartHasPOAOverride) {
							mosPOAOverride.addProductPartIntersectionClearedMarkets(childCountries);
						}					
					}
				}
				
				if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strBOMType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strBOMType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strBOMType)) {
					StringList slFPPData = new StringList(2);
					slFPPData.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
					slFPPData.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
					
					Map mapFPPData = (Map)dObjIPS.getInfo(context, slFPPData);
					String strIPSIncludeInCOS =(String) mapFPPData.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
					
					if("true".equalsIgnoreCase(strIPSIncludeInCOS)) {
						String sFCExist = (String)mapFPPData.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
						boolean isFCExist = (UIUtil.isNotNullAndNotEmpty(sFCExist) && pgV3Constants.KEY_TRUE.equalsIgnoreCase(sFCExist))?Boolean.TRUE:Boolean.FALSE;
						if(isFCExist) {
							mosPOAOverride.fetchMarketOfSaleResult(dObjIPS);
						}
					}
                                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
				}
				//Modified by V4 for defect 1317 - Ends
			//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - Start
				if(null!=childCountries){
			//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - End
					//System.out.println("child:" + childCountries);
					childCountriesMap.put(child,childCountries);
					if(restrictedCountries.containsKey(child))						
						restrictedSet.addAll(restrictedCountries.get(child));
				}
			}
			//System.out.println("childCountriesMap:" + childCountriesMap);
			//perform AND operation on countries
			countries= getIntersectingCountries(childCountriesMap);
			//System.out.println("childCountriesMap countries:" + countries);

			if(!restrictedSet.isEmpty()){
				Map<String,Set> tempRestrictionMap = new HashMap<String,Set>();
				tempRestrictionMap.put("A",restrictedSet );
				tempRestrictionMap.put("B",countries );

				// get only those restricted countries that are part of IPS BOM assembly
				Set restCountriesForIPS= getIntersectingCountries(tempRestrictionMap);
				restrictedCountries.put(strChildId, restCountriesForIPS);
			}


			//Found the countries for the IPS . Now we connect them 
			//connect the countries to the IPS
			try{
				// Modified by V4 for No Country Component 18/07/2013 Start
				DomainObject domPSUB=DomainObject.newInstance(context,strChildId);
				StringList objectselect= new StringList(3);
				objectselect.add(DomainConstants.SELECT_TYPE);
				objectselect.add("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]");
				objectselect.add("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
				Map IPSinfo=domPSUB.getInfo(context, objectselect);
				String stype=(String) IPSinfo.get(DomainConstants.SELECT_TYPE);
				String stassemblyType=(String) IPSinfo.get("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]");
				String stIncludeInCOS=(String) IPSinfo.get("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
				
				// check if No Country Component condition is satisfied. If yes set pgIncludeInCOS attribute to False. 

				if(childType.isEmpty()&&("FWIP-Finished Work in Process".equalsIgnoreCase(stassemblyType)|| "FWIP".equalsIgnoreCase(stassemblyType)||"Purchased Subassembly".equalsIgnoreCase(stassemblyType)||"Purchased and/or Produced Subassembly".equalsIgnoreCase(stassemblyType) )){
					//if(childType.isEmpty()&&countries.isEmpty()&&pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)){	
					if(!"False".equalsIgnoreCase(stIncludeInCOS))
					{
						domPSUB.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
						setParentIPSCosCalculate(context, domPSUB);
					}
				}else if("False".equalsIgnoreCase(stIncludeInCOS)) {
					domPSUB.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);

					setParentIPSCosCalculate(context, domPSUB);
				}
				// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - START
				mosPOAOverride.calculatePOAOverrideMarkets(countries);
				
				// Modified by V4 for No Country Component 18/07/2013 End
				updateCountries(context, strChildId, countries,restrictedSet, mosPOAOverride.getChildComponentLRRMarkets());
                                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244) - END
			} catch(Exception ex){
				System.out.println("ERROR: Could not connect "+ strChildId + " with Countries: " + countries);
				ex.printStackTrace();
			}

			//get the substitute for the IPS and then take intersection for the Substitute and other siblings and return the intersected Countries
			if(productData.isSubstitute()&&!countries.isEmpty()){
				childCountriesMap.put(strChildId,productData.getCountries());
			}
			//System.out.println("Here111111111111111111");
			countries= getIntersectingCountries(childCountriesMap);
			//System.out.println("Here111111111111111111:" + countries);

		}else{
			countries= productData.getCountries();
		}
		return countries;
	}

	/**
         * Updated by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244)
	 * This method will update the countries found during calculation. 
	 * It uses delta coding to find delta maps for adding countries, removing Countries and updating the restrictions 
	 * 
	 * */
	private void updateCountries(Context context, String objectId, Set newCountriesSet, Set restrictedCountriesSet,Set<String> lrrMarkets) throws Exception{
		DomainObject doIPS = DomainObject.newInstance(context, objectId);
		//this map will initially contain all the connected countries of the IPS . finally contain countries that will be disconnected, 
		Map<String, Map> existingCountries = new HashMap<String, Map>();
		Map mpCountry = null;

		StringList relSelect = new StringList(DomainObject.SELECT_RELATIONSHIP_ID);
		relSelect.add("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
		relSelect.add(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);

		StringList objSelect = new StringList(DomainObject.SELECT_NAME);
		objSelect.add(DomainConstants.SELECT_ID);
		/*Fetch Existing Countries and compare with the new countries to be added*/
		MapList mlCountries = doIPS.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,
				CPNCommonConstants.TYPE_COUNTRY, objSelect, relSelect, false, true, (short) 1, "", "");

		String cId = "";
		String cName ="";
		String cRelId = "";
		String cRest ="";
		String cLRR ="";
		for (Iterator mlCountriesIter = mlCountries.iterator(); mlCountriesIter.hasNext();) {
			mpCountry = (Map) mlCountriesIter.next();
			cId = (String) mpCountry.get(DomainObject.SELECT_ID);
			cName = (String) mpCountry.get(DomainObject.SELECT_NAME);
			cRelId = (String) mpCountry.get(DomainObject.SELECT_RELATIONSHIP_ID);
			cRest = (String) mpCountry.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
			cLRR  = (String) mpCountry.get(MOSOverrideConstants.SELECT_ATTRIBUTE_PGMOSPOAOVERRIDELRR);
			Map mpCountrySave = new HashMap<String, String>();
			mpCountrySave.put("relId", cRelId);
			mpCountrySave.put("restriction", cRest);
			mpCountrySave.put("isLRR", cLRR);
			mpCountrySave.put("id", cId);
			mpCountrySave.put("name", cName);
			existingCountries.put(cName, mpCountrySave);
		}

		Set addCountries = new HashSet();
		Set<Map> updateCountriesSet = new HashSet<Map>();

		for (Iterator countriesItr = newCountriesSet.iterator(); countriesItr.hasNext();) {
			String newCountry = (String) countriesItr.next();
			if (existingCountries.containsKey(newCountry)) {
				//System.out.println("newCountry:" +newCountry);

				// check if the restrictions are same. if yes then continue else
				// add to the changeRestriction Set
				mpCountry = (Map) existingCountries.get(newCountry);
				String restriction = (String) mpCountry.get("restriction");
				if (null != restriction && (!restriction.isEmpty())) {
					// there is restrictions on the exiting Country . Check if
					// the new country does not have restriction then change the
					// attribute value
					if (!restrictedCountriesSet.contains(newCountry)) {
						updateCountriesSet.add(mpCountry);
					}
				} else {
					// existing country is not having any restriction. Check if
					// the new country has any restrictions
					if (restrictedCountriesSet.contains(newCountry)) {
						updateCountriesSet.add(mpCountry);
					}
				}
				
				String isLRR = (String) mpCountry.get("isLRR");
				if (null != isLRR && !isLRR.isEmpty()) {
					if (!lrrMarkets.contains(newCountry)) {
						updateCountriesSet.add(mpCountry);
					}
				} else {
					if (lrrMarkets.contains(newCountry)) {
						updateCountriesSet.add(mpCountry);
					}
				}
				
				existingCountries.remove(newCountry);
			} else {
				addCountries.add(newCountry);
			}

		}

		// need to add countries in addCountries and delete countries in
		// newCountries and update restrictions in updateCountries set.

		Date today = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		String strToday = formatter.format(today);
		RelationshipType relationShipType = new RelationshipType(pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALEHISTORY);
		BusinessObject COSHistoryBus = null;

		boolean isUpdated = false;
		Map attributeValues = null;
		for (Iterator iterator = updateCountriesSet.iterator(); iterator.hasNext();) {
			isUpdated = true;
			Map countryMp = (Map) iterator.next();
			String relId = (String) countryMp.get("relId");
			String countryName = (String) countryMp.get("name");
			String restriction = "";
			if (restrictedCountriesSet.contains(countryName)){
				restriction = "Yes";
			}
			
			String isLRRMarket = lrrMarkets.contains(countryName)?"Yes":"";
			
			attributeValues = new HashMap();
			attributeValues.put(pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION,restriction);
			attributeValues.put(MOSOverrideConstants.ATTRIBUTE_PGMOSPOAOVERRIDELRR,isLRRMarket);
			
			DomainRelationship.setAttributeValues(context, relId, attributeValues);
//			DomainRelationship.setAttributeValue(context, relId, pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION, restriction);
			restriction = restriction.equalsIgnoreCase("Yes")?restriction:"No";
			// create history object when attribute is modify

			try {
				COSHistoryBus = new BusinessObject(FrameworkUtil.autoName(context,"type_pgCOSHistory", "","policy_pgCOSHistory" ,pgV3Constants.VAULT_ESERVICEPRODUCTION));
				AttributeList attributes = new AttributeList(4);
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCHANGEDETAIL),CHANGED));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCOUNTRYNAME),countryName));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGMODIFIEDDATE),strToday));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_COMMENT),restriction));
				COSHistoryBus.setAttributes(context, attributes);
				doIPS.addToObject(context,relationShipType, COSHistoryBus.getObjectId(context));
			} catch (Exception e) {

				e.printStackTrace();
			}

		}
		String sCountryId = "";
		for (Iterator iterator = addCountries.iterator(); iterator.hasNext();) {

			String countryName = (String) iterator.next();
			if(countryName==null || countryName.isEmpty()){
				continue;
			}
			isUpdated = true;
			sCountryId = MqlUtil.mqlCommand(context, "print bus " + CPNCommonConstants.TYPE_COUNTRY + " \"" + countryName + "\" 0 select id dump");

			RelationshipType relCountryOfSale = new RelationshipType(pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE);

			
			//create a new pgCOSHistoryObject

			try {
				//Moved by DSM-2018x.6_October_CW for COS (Defect ID- 44588) on 03-09-2021 - Starts
				DomainRelationship drCountryOfSale = doIPS.addToObject(context, relCountryOfSale, sCountryId);
				String restriction = "";
				if (restrictedCountriesSet.contains(countryName)){
					restriction = "Yes";
				}
				String isLRRMarket = lrrMarkets.contains(countryName)?"Yes":"";
				attributeValues = new HashMap();
				attributeValues.put(pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION,restriction);
				attributeValues.put(MOSOverrideConstants.ATTRIBUTE_PGMOSPOAOVERRIDELRR,isLRRMarket);
				drCountryOfSale.setAttributeValues(context, attributeValues);
//				drCountryOfSale.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION, restriction);
				//Moved by DSM-2018x.6_October_CW for COS (Defect ID- 44588) on 03-09-2021 - Ends
				COSHistoryBus = new BusinessObject(FrameworkUtil.autoName(context,"type_pgCOSHistory", "","policy_pgCOSHistory" ,pgV3Constants.VAULT_ESERVICEPRODUCTION));
				AttributeList attributes = new AttributeList(4);
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCHANGEDETAIL),ADDED));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCOUNTRYNAME),countryName));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGMODIFIEDDATE),strToday));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_COMMENT),restriction));
				COSHistoryBus.setAttributes(context, attributes);
				doIPS.addToObject(context,relationShipType, COSHistoryBus.getObjectId(context));
			} catch (Exception e) {

				e.printStackTrace();
			}
			// create history object when country is added end here	
		}

		Set existingCountriesSet = existingCountries.keySet();

		for (Iterator iterator = existingCountriesSet.iterator(); iterator.hasNext();) {
			isUpdated = true;
			String country = (String) iterator.next();
			Map countryMp = (Map) existingCountries.get(country);
			//System.out.println("countryMp:" + countryMp);
			String relId = (String) countryMp.get("relId");

			String pgcosRestriction = DomainRelationship.getAttributeValue(context, relId, pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION);			
			DomainRelationship.disconnect(context, relId);

			try {
				COSHistoryBus = new BusinessObject(FrameworkUtil.autoName(context,"type_pgCOSHistory", "","policy_pgCOSHistory" ,pgV3Constants.VAULT_ESERVICEPRODUCTION));
				AttributeList attributes = new AttributeList(4);
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCHANGEDETAIL),NO_LONGER_CLEARED));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGCOUNTRYNAME),country));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_PGMODIFIEDDATE),strToday));
				attributes.add(new Attribute(new AttributeType(pgV3Constants.ATTRIBUTE_COMMENT),pgcosRestriction));
				COSHistoryBus.setAttributes(context, attributes);

				doIPS.addToObject(context,relationShipType, COSHistoryBus.getObjectId(context));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// create history object when country is removed end here	
		}
		//Modified by V4 as per Defect 1579 Start here ----
		//after processing the pgFinishedProduct or pgPackingSubassembly , set the pgCOSCalculate attribute value to false
		//doIPS.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
		try {
			//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
			HashMap hmAttributes = new HashMap();
			hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSWIP, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_FALSE);
			hmAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE, "");
			doIPS.setAttributeValues(context, hmAttributes);
			//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Modified by V4 as per Defect 1579 End here ----
		if (isUpdated) {
			//System.out.println("doIPS:" +doIPS);
			//Commented as per defect 1317

			setParentIPSCosCalculate(context, doIPS);
		}

		doIPS.setAttributeValue(context, pgV3Constants.ATTRIBUTE_PGCOSRUNDATE, strToday);
	}

	/**
	 * This method structures the EBOM map with parent and child combination. css type
	 * It also fetches the countries for the children other than IPS.
	 * 
	 */
	private Map<String, pgProductData> createParentChildStructure(Context context, String IPSId, MapList mlBoms) throws Exception {
		// this Map stores all the EBOM objects in a hierarchy parent-child structure. This Map is returned finally
		Map<String, pgProductData> ebomMap = new HashMap<String, pgProductData>();

		pgProductData pdIPS = new pgProductData();
		pdIPS.setId(IPSId);
		pdIPS.setLevel(0);
		//JUST A DUMMY TYPE
		pdIPS.setType(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
		String parentId = IPSId;
		ebomMap.put(IPSId, pdIPS);

		//parent Pd to hold the parent object
		pgProductData pgPdPrevious = pdIPS.clone();
		boolean isCircular = false;

		
		String actualConnectedId = "";
		int actualConnectedLevel = 0;
		int parentLevel = 0;

		//Modified for defect 2909
		HashMap substituteIdsIPMSAll = new HashMap();
		HashMap substituteIdsFCAll = new HashMap();
		
		
		HashMap substituteIdsIRMSAll = new HashMap();
		if(mlBoms!=null && mlBoms.size()>0) {
			String id = DomainConstants.EMPTY_STRING;
			String type = DomainConstants.EMPTY_STRING;
			String sPolicy = DomainConstants.EMPTY_STRING;
			String pgSAPType = DomainConstants.EMPTY_STRING;
			String strIncludeInCos = DomainConstants.EMPTY_STRING;
			String strCOSCalculate = DomainConstants.EMPTY_STRING;
			String strParentType = DomainConstants.EMPTY_STRING;
			//Added by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - Start
			String strAssemblyType = DomainConstants.EMPTY_STRING;
			//Added by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - End
			int currLevel = 0;
			boolean substitute = false;
			boolean isArtConnectedToCountry = false;
			Set stCountries = new HashSet();
			String strCOSFPPOverriden = null;
			for (Iterator eBomItr = mlBoms.iterator(); eBomItr.hasNext();) {
				substitute = false;
				isArtConnectedToCountry = false;
				stCountries = new HashSet();

				Map mpPart = (Map) eBomItr.next();
				id = (String)mpPart.get(DomainConstants.SELECT_ID);
				type = (String)mpPart.get(DomainConstants.SELECT_TYPE);
				pgSAPType = (String)mpPart.get("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
				currLevel = Integer.parseInt((String)mpPart.get(DomainConstants.SELECT_LEVEL));
				strCOSCalculate = (String)mpPart.get("attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"]");
				//Added by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - Start
				strAssemblyType = (String)mpPart.get("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]");
				//Added by DSM-2018x.3 for COS (Req ID- 30899) on 20-01-2020 - End
				strCOSFPPOverriden = null;
				if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(type)) {
					DomainObject doChild = DomainObject.newInstance(context, id);
					strCOSFPPOverriden = doChild.getAttributeValue(context,pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
				}
				//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
				sPolicy = (String)mpPart.get(DomainConstants.SELECT_POLICY);
				//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
				//Modified by V4 for No Country Component start
				strIncludeInCos = (String)mpPart.get("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
				//Modified by V4 for No Country Component End
			
				strParentType = DomainConstants.EMPTY_STRING;
				Object substituteId = (Object) mpPart.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.id");
				StringList substituteIds = new StringList(2);

				if (null != substituteId) {
					if (substituteId instanceof StringList) {
						substituteIds = (StringList) substituteId;
					} else {
						substituteIds.add(substituteId.toString());
					}
				}

				// if pgPhase then ignore
				if (pgV3Constants.TYPE_PGPHASE.equalsIgnoreCase(type)) {
					actualConnectedId = id;
					actualConnectedLevel = currLevel;
					continue;
				}
				//	if rawMaterial with ROH SAPType then ignore
				//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
				if (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(type) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(type)) {
					//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
					//Modified by V4 for defect 3035 start
					boolean isROH=true;
					for (Iterator subItr = substituteIds.iterator(); subItr.hasNext();) {
						String subId = (String) subItr.next();
						StringList objectselect=new StringList(2);
						objectselect.add(DomainConstants.SELECT_TYPE);
						objectselect.add("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
						DomainObject domIRMS = DomainObject.newInstance(context,subId);
						Map psubMap=domIRMS.getInfo(context, objectselect);
						String stype=(String) psubMap.get(DomainConstants.SELECT_TYPE);
						String pgSAPTypesub=(String) psubMap.get("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
						if((pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(type) 
								|| pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(type)) &&!"ROH".equalsIgnoreCase(pgSAPTypesub)){
							//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
							isROH=false;
						}
					}
					if(pgSAPType.equalsIgnoreCase("ROH")&&isROH){
						continue;	
					}
					//Modified by V4 for defect 3035 end
				}

				//if type==NonGCASPart then ignore
				if ((pgV3Constants.TYPE_PGNONGCASPART.equalsIgnoreCase(type))) {
					continue;
				}

				parentLevel = pgPdPrevious.getLevel();
				//code to find out the parent: start
				if (currLevel > parentLevel) {

					if ((!parentId.equalsIgnoreCase(actualConnectedId)) && (actualConnectedLevel == parentLevel)) {
						parentId = pgPdPrevious.getParentid();
					} else {
						parentId = pgPdPrevious.getId();
					}

				} else if (currLevel == parentLevel) {
					parentId = pgPdPrevious.getParentid();
				}else{
					// curr Level < parent level 

					int intTempParentLevel = parentLevel;
					while(parentLevel>currLevel){
						//get parent of the parenPd and then check the level
						pgPdPrevious = ebomMap.get(pgPdPrevious.getParentid());
						parentLevel = pgPdPrevious.getLevel();
						if(intTempParentLevel <= parentLevel)
						{
							isCircular = true;
							break;
						}

						intTempParentLevel = parentLevel;
					}
					if(!isCircular)
						if(null!=pgPdPrevious.getParentid()){
							parentId = pgPdPrevious.getParentid();
						} else{ 
							parentId = pgPdPrevious.getId();
						}
				}
				//code to get Parent: end
				if(parentId!=null) {
					pgPdPrevious = ebomMap.get(parentId);
				}

				strParentType = pgPdPrevious.getType();

				//calculate the countries only when parent is IPS
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
				if(strParentType.isEmpty() || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strParentType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strParentType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strParentType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strParentType))
					//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
				{
					//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
					//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
					if((pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(type) || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(type)
							|| TYPE_PGDEVICEPRODUCTPART.equalsIgnoreCase(type) || TYPE_PGASSEMBLEDPRODUCTPART.equalsIgnoreCase(type))
							&& !pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(sPolicy)){
						//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
						
						//check if that object already have countries fetched if not then 
						//get the countries connected to the FC and store it in Map
						//Modified for defect 2909 - Check if same FC has different substitute
						// Modified by V4 to make substitute as flat BOM  
						/*	StringList slTempList = (StringList)substituteIdsFCAll.get(id);
						if(null != slTempList && slTempList.size() > 0)
						{
							for(int i=0;i<slTempList.size();i++) {
								substituteIds.addElement(slTempList.get(i));
							}
						}
						substituteIdsFCAll.put(id, substituteIds);*/
						stCountries=getCountriesForFormulatedProduct(context, id, new StringList());
					}
					//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
					else if(pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(type) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(type)){
						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
						StringList slTempList = (StringList)substituteIdsIRMSAll.get(id);
						// Modified by V4 to make substitute as flat BOM 
						/*if(null != slTempList && slTempList.size() > 0)
						{
							for( int i=0;i<slTempList.size();i++) {
								substituteIds.addElement(slTempList.get(i));
							}
						}
						substituteIdsIRMSAll.put(id, substituteIds);*/
						//check if that object already have countries fetched if not then 
						//get the countries connected to the FC and store it in Map
						
						stCountries = getCountriesForRawMaterial(context, id, new StringList());
					} else if(pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(type) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(type) || pgV3Constants.TYPE_PGONLINEPRINTINGPART.equalsIgnoreCase(type)){
						
						// Modified by V4 to make substitute as flat BOM 
						/*  StringList slTempList = (StringList)substituteIdsIPMSAll.get(id);
						if(null != slTempList && slTempList.size() > 0)
						{
							for(int i=0;i<slTempList.size();i++) {
								substituteIds.addElement(slTempList.get(i));
							}
						}
						substituteIdsIPMSAll.put(id, substituteIds);*/
						//check if that object already have countries fetched if not then 
						//get the countries connected to the Art and store it in Map	
						stCountries = getCountriesForPackingMaterial(context, id, new StringList());
						
						if(null!=stCountries && stCountries.size()>0){
							isArtConnectedToCountry=true;
						}
						//isArtConnectedToCountry = isIPMSconnectedToCountry(context, id, substituteIds);
						// Modified by V4 to make substitute as flat BOM
					} if((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(type) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(type)) && !"False".equalsIgnoreCase(strIncludeInCos)) {
						if("true".equalsIgnoreCase(strCOSCalculate)) {
							stCountries = getCountriesForFinishedProduct(context, id);
						} else {
							stCountries = getCountriesForIPS(context, id, DomainConstants.EMPTY_STRINGLIST);
						}
						
					} 
					//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 03-12-2018 - Start
					else if(((pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(type) && !pgV3Constants.SHIPPABLE_HALB.equalsIgnoreCase(strAssemblyType)) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(type) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(type)) && !"False".equalsIgnoreCase(strIncludeInCos)) {
						//Modified by DSM-2018x.3 for COS (Req ID- 30899) on 03-12-2018 - End	
						if("true".equalsIgnoreCase(strCOSCalculate) && !("Yes".equalsIgnoreCase(strCOSFPPOverriden) && "true".equalsIgnoreCase(strCOSCalculate))) {
							stCountries = getCountriesForFinishedProductPart(context, id);
						} else {
							stCountries = getCountriesForIPS(context, id, DomainConstants.EMPTY_STRINGLIST);
						}
						
					} 
					/*else if((!substituteIds.isEmpty()) && (pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(type) || (pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(type))))	{
					// for finishedProducts only fetch countries for its substitutes. for non substitute finishedproduct, Countries will be calculated later.
						Map<String, Set> childCountriesMap = new HashMap<String, Set>();
						Set<String> restrictedCountriesForIPS = new HashSet<String>();
						for (Iterator iterator = substituteIds.iterator(); iterator.hasNext();) {
							String subId = (String) iterator.next();
							// Modified by V4 for No Country Component Start
							StringList objectselect=new StringList(2);
							objectselect.add(DomainConstants.SELECT_TYPE);
							objectselect.add("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
							DomainObject domPSUB = DomainObject.newInstance(context,subId);
							Map psubMap=domPSUB.getInfo(context, objectselect);
							String stype=(String) psubMap.get(DomainConstants.SELECT_TYPE);
							String includeInCOS=(String) psubMap.get("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
							if((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype))&&"False".equalsIgnoreCase(includeInCOS)){
								continue;
								// if pgIncludeInCOS is false then do not include in COS Calculation
							}
							// Modified by V4 for No Country Component End
	
							// Modified by V4 for substitute other than IPS ,PSUB start .
							if(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype)){
								stCountries=getCountriesForFinishedProduct(context , subId);
							}else{
								stCountries=getCountriesForPart(context, subId, new StringList());
	
							}
	
							if(null!=stCountries){
								childCountriesMap.put(subId, stCountries);
								substitute = true; // Modified by V4 to make substitute as flat BOM
							}
							// Modified by V4 for substitute other than IPS ,PSUB end .
	
							if(restrictedCountries.containsKey(subId)){
								restrictedCountriesForIPS.addAll(restrictedCountries.get(subId));
							}
							//substitute = true;
						}
						//substitute = true;
						stCountries = getIntersectingCountries(childCountriesMap);
	
						if(!restrictedCountriesForIPS.isEmpty()){
							Map<String,Set<String>> tempRestrictionMap = new HashMap<String,Set<String>>();
							tempRestrictionMap.put("A",restrictedCountriesForIPS );
							tempRestrictionMap.put("B",stCountries );
	
							Set<String> restCountriesForIPS= getIntersectingCountries(tempRestrictionMap);
							if((null!=restCountriesForIPS) && (!restCountriesForIPS.isEmpty()) && (null!=parentId)){
								// Modified by V4 for No Country Component Start
								//if(!"False".equalsIgnoreCase(includeInCos))
								restrictedCountries.put(parentId, restCountriesForIPS);
	
								// Modified by V4 for No Country Component End
							}
						}
					}*/
					if(!substituteIds.isEmpty()){
						String strCOSSubFPPOverriden = null;
						for (Iterator iterator = substituteIds.iterator(); iterator.hasNext();) {
							String subId = (String) iterator.next();
							Set subCountries=new HashSet();
							boolean isArtConnectedToCountrysub=false;
							
							StringList objectselect=new StringList(2);
							objectselect.add(DomainConstants.SELECT_TYPE);
							objectselect.add("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
							objectselect.add("attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"]");
							DomainObject domPSUB = DomainObject.newInstance(context,subId);
							Map psubMap=domPSUB.getInfo(context, objectselect);
							String stype=(String) psubMap.get(DomainConstants.SELECT_TYPE);
							String strIncludeInCOS = (String) psubMap.get("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
							strCOSCalculate = (String) psubMap.get("attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"]");
							strCOSSubFPPOverriden = null;
							if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(stype)) {
								strCOSSubFPPOverriden = domPSUB.getAttributeValue(context,pgV3Constants.ATTRIBUTE_PGHASCOSFPPOVERRIDDEN);
							}
							//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
							if((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(stype)) && "False".equalsIgnoreCase(strIncludeInCOS))
								//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
							{
								continue;
								// if pgIncludeInCOS is false then do not include in COS Calculation
							}
							if(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype)){
								if("true".equalsIgnoreCase(strCOSCalculate)) {
									subCountries = getCountriesForFinishedProduct(context , subId);
								} else {
									subCountries = getCountriesForIPS(context, subId, DomainConstants.EMPTY_STRINGLIST);
								}
								substitute = true; //Modified for defect 5311
							} 
							//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
							else if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(stype))
							{
								//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
							
								if("true".equalsIgnoreCase(strCOSCalculate) && !("Yes".equalsIgnoreCase(strCOSSubFPPOverriden) && "true".equalsIgnoreCase(strCOSCalculate))) {
									subCountries = getCountriesForFinishedProductPart(context, subId);
								} else {
									subCountries = getCountriesForIPS(context, subId, DomainConstants.EMPTY_STRINGLIST);
								}
								substitute = true;
							} else{
								subCountries = getCountriesForPart(context, subId, new StringList());
								if(null!=subCountries&&subCountries.size()>0){
									isArtConnectedToCountrysub=true;
								}
							}
							pgProductData pgPdCurrent = new pgProductData();
							pgPdCurrent.setId(subId);
							pgPdCurrent.setSubstitute(substitute);
							pgPdCurrent.setType(stype);
							pgPdCurrent.setParentid(parentId);
							pgPdCurrent.setLevel(currLevel);
							pgPdCurrent.setIncludeInCos(strIncludeInCOS);
							pgPdCurrent.setIPMSconsider(isArtConnectedToCountrysub);
							pgPdPrevious.addChildren(subId);
							pgPdCurrent.setCountries(subCountries); 
							ebomMap.put(subId, pgPdCurrent);

						}
						substitute = false; //Modified for defect 5311
					}	
				} else {
					// if parent is not IPS then don't count that GCAS
					continue;
				}

				pgProductData pgPdCurrent = new pgProductData();
				pgPdCurrent.setId(id);
				pgPdCurrent.setSubstitute(substitute);
				pgPdCurrent.setType(type);
				pgPdCurrent.setParentid(parentId);
				pgPdCurrent.setLevel(currLevel);
				pgPdCurrent.setIncludeInCos(strIncludeInCos);
				pgPdCurrent.setIPMSconsider(isArtConnectedToCountry);
				//if there are restricted countries in this Part then get them from the global restricted Map
				Set restrictedSet = new HashSet();
				if (restrictedCountries.containsKey(id)) {
					restrictedSet = restrictedCountries.get(id);
				}
				for (Iterator substituteIter = substituteIds.iterator(); substituteIter.hasNext();) {
					String subsId = (String) substituteIter.next();
					if (restrictedCountries.containsKey(subsId))
						restrictedSet = restrictedCountries.get(subsId);
				}

				pgPdPrevious.addChildren(id);
				pgPdCurrent.setCountries(stCountries);

				ebomMap.put(id, pgPdCurrent);
				parentId = id;
				parentLevel = currLevel;
				//prev Pd to hold the current object
				pgPdPrevious = pgPdCurrent.clone();
				actualConnectedId = id;
				actualConnectedLevel = currLevel;
			}
		}
		return ebomMap;
	}

	/** Added By Sogeti(DSM) 2018x.5 COS - This method is used to check Circular Data
	 * 
	 * @param context
	 * @param IPSId
	 * @param mlBoms
	 * @return boolean
	 * @throws Exception
	 */
	 //Added by DSM(sogeti) for 2018x.5 COS Defect 33103 - Starts
	private boolean isCircular(Context context, String IPSId, MapList mlBoms) throws Exception {

		// this Map stores all the EBOM objects in a hierarchy parent-child
		// structure. This Map is returned finally
		Map<String, pgProductData> ebomMap = new HashMap<>();
		pgProductData pdIPS = new pgProductData();
		pdIPS.setId(IPSId);
		pdIPS.setLevel(0);
		// JUST A DUMMY TYPE
		pdIPS.setType(pgV3Constants.TYPE_PGFINISHEDPRODUCT);
		String parentId = IPSId;
		ebomMap.put(IPSId, pdIPS);

		// parent Pd to hold the parent object
		pgProductData pgPdPrevious = pdIPS.clone();
		boolean isCircular = false;
		String actualConnectedId = "";
		int actualConnectedLevel = 0;
		int parentLevel = 0;

		StringList slobjectselect = new StringList(2);
		slobjectselect.add(DomainConstants.SELECT_TYPE);
		slobjectselect.add("attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]");

		StringList slsubobjectselect = new StringList(2);
		slobjectselect.add(DomainConstants.SELECT_TYPE);
		slobjectselect.add("attribute[" + pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS + "]");
		try {
			if (mlBoms != null && mlBoms.size() > 0) {
				String id;
				String type;
				String pgSAPType;
				String strIncludeInCos;
				String strParentType;

				int currLevel = 0;
				boolean substitute = false;
				for (Iterator<?> eBomItr = mlBoms.iterator(); eBomItr.hasNext();) {
					substitute = false;

					Map<?, ?> mpPart = (Map) eBomItr.next();
					id = (String) mpPart.get(DomainConstants.SELECT_ID);
					type = (String) mpPart.get(DomainConstants.SELECT_TYPE);
					pgSAPType = (String) mpPart.get("attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]");
					currLevel = Integer.parseInt((String) mpPart.get(DomainConstants.SELECT_LEVEL));
					strIncludeInCos = (String) mpPart.get("attribute[" + pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS + "]");

					Object substituteId = (Object) mpPart
							.get("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.id");
					StringList substituteIds = new StringList(2);

					if (null != substituteId) {
						if (substituteId instanceof StringList) {
							substituteIds = (StringList) substituteId;
						} else {
							substituteIds.add(substituteId.toString());
						}
					}

					// if pgPhase then ignore
					if (pgV3Constants.TYPE_PGPHASE.equalsIgnoreCase(type)) {
						actualConnectedId = id;
						actualConnectedLevel = currLevel;
					}
					// if rawMaterial with ROH SAPType then ignore
					if (pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(type) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(type)) {
						boolean isROH = true;

						for (Iterator<?> subItr = substituteIds.iterator(); subItr.hasNext();) {
							String subId = (String) subItr.next();
							DomainObject domIRMS = DomainObject.newInstance(context, subId);
							Map<?, ?> psubMap = domIRMS.getInfo(context, slobjectselect);
							String pgSAPTypesub = (String) psubMap
									.get("attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]");

							if ((pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(type) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(type))
									&& !"ROH".equalsIgnoreCase(pgSAPTypesub)) {
								isROH = false;
							}
						}
						if (pgSAPType.equalsIgnoreCase("ROH") && isROH) {
							continue;
						}
					}

					// if type==NonGCASPart then ignore
					if ((pgV3Constants.TYPE_PGNONGCASPART.equalsIgnoreCase(type))) {
						continue;
					}

					parentLevel = pgPdPrevious.getLevel();
					// code to find out the parent: start
					if (currLevel > parentLevel) {

						if ((!parentId.equalsIgnoreCase(actualConnectedId)) && (actualConnectedLevel == parentLevel)) {
							parentId = pgPdPrevious.getParentid();
						} else {
							parentId = pgPdPrevious.getId();
						}

					} else if (currLevel == parentLevel) {
						parentId = pgPdPrevious.getParentid();
					} else {
						// curr Level < parent level

						int intTempParentLevel = parentLevel;
						while (parentLevel > currLevel) {
							// get parent of the parenPd and then check the
							// level
							pgPdPrevious = ebomMap.get(pgPdPrevious.getParentid());
							parentLevel = pgPdPrevious.getLevel();

							if (intTempParentLevel <= parentLevel) {
								isCircular = true;
								break;
							}

							intTempParentLevel = parentLevel;
						}
						if (!isCircular)
							if (null != pgPdPrevious.getParentid()) {
								parentId = pgPdPrevious.getParentid();
							} else {
								parentId = pgPdPrevious.getId();
							}
					}

					if (isCircular) {
						pdIPS.setStrIsCircular("Circular");
						break;
					}

					// code to get Parent: end
					if (parentId != null) {
						pgPdPrevious = ebomMap.get(parentId);
					}

					strParentType = pgPdPrevious.getType();

					// calculate the countries only when parent is IPS
					if (!strParentType.isEmpty() || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strParentType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strParentType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strParentType)
							|| pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strParentType)) {
						if (!substituteIds.isEmpty()) {
							for (Iterator<?> iterator = substituteIds.iterator(); iterator.hasNext();) {
								String subId = (String) iterator.next();
								DomainObject domPSUB = DomainObject.newInstance(context, subId);
								Map<?, ?> psubMap = domPSUB.getInfo(context, slsubobjectselect);
								String stype = (String) psubMap.get(DomainConstants.SELECT_TYPE);
								String strIncludeInCOS = (String) psubMap
										.get("attribute[" + pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS + "]");

								if ((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(stype)
										|| pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(stype))
										&& "False".equalsIgnoreCase(strIncludeInCOS)) {
									continue;
								}
								substitute = true;

								pgProductData pgPdCurrent = new pgProductData();
								pgPdCurrent.setId(subId);
								pgPdCurrent.setSubstitute(substitute);
								pgPdCurrent.setType(stype);
								pgPdCurrent.setParentid(parentId);
								pgPdCurrent.setLevel(currLevel);
								pgPdCurrent.setIncludeInCos(strIncludeInCOS);
								pgPdPrevious.addChildren(subId);
								ebomMap.put(subId, pgPdCurrent);

							}
							substitute = false;
						}
					} else {
						// if parent is not IPS then don't count that GCAS
						continue;
					}

					pgProductData pgPdCurrent = new pgProductData();
					pgPdCurrent.setId(id);
					pgPdCurrent.setSubstitute(substitute);
					pgPdCurrent.setType(type);
					pgPdCurrent.setParentid(parentId);
					pgPdCurrent.setLevel(currLevel);
					pgPdCurrent.setIncludeInCos(strIncludeInCos);
					pgPdPrevious.addChildren(id);

					ebomMap.put(id, pgPdCurrent);
					parentId = id;
					// prev Pd to hold the current object
					pgPdPrevious = pgPdCurrent.clone();
					actualConnectedId = id;
					actualConnectedLevel = currLevel;
				}
			}
			//System.out.println("ebomMap======Outside Circular=============>" + ebomMap);
		} catch (Exception ex) {
			throw ex;
		}
		return isCircular;
	}
	 // Added by DSM(sogeti) for 2018x.5 COS Defect 33103 - Ends
	
	/**
	 * fetch countries connected to the formula card.
	 * Ignore the countries which are Not Cleared
	 * */
	private Set getCountriesForFormulatedProduct(Context context, String FCId, StringList substituteId) throws Exception {

		Map cosMap = new HashMap();
		DomainObject doFC = DomainObject.newInstance(context, FCId);
		Set cosRestricted = new HashSet();
		String whereCond = "";

		// if there are substitutes then fetch the substitutes countries
		if (!substituteId.isEmpty()) {
			for (Iterator subItr = substituteId.iterator(); subItr.hasNext();) {
				String subId = (String) subItr.next();

				// Modified by V4 for No Country Component 18/07/2013 Start - If PSUB with pgIncludeInCOS False is substitute for FC, do not consider PSUB for calculation
				StringList objectselect=new StringList(2);
				objectselect.add(DomainConstants.SELECT_TYPE);
				objectselect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				DomainObject domPSUB = DomainObject.newInstance(context,subId);
				Map psubMap=domPSUB.getInfo(context, objectselect);
				String stype=(String) psubMap.get(DomainConstants.SELECT_TYPE);
				String strIncludeInCOS=(String) psubMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
				if((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype)||pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(stype)||pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(stype)) && "False".equalsIgnoreCase(strIncludeInCOS))
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
				{

					continue;
				}
				// Modified by V4 for No Country Component 18/07/2013 End
				Set countries  = getCountriesForPart(context, subId, new StringList());

				//if subsId has restricted countries then add it to the original id set
				if(restrictedCountries.containsKey(subId)){
					Set restSet = restrictedCountries.get(subId);
					cosRestricted.addAll(restSet);
				}

				if(null!=countries){
					cosMap.put(subId, countries);
				}
			}
		}

		Set cosSet = new HashSet();

		MapList mlFC = doFC.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE,
				CPNCommonConstants.TYPE_COUNTRY, new StringList(DomainObject.SELECT_NAME), 
				new StringList("attribute["+pgV3Constants.ATTRIBUTE_PGOVERALLCLEARANCESTATUS+"]"), true, true, (short) 1, "", whereCond);

		for (Iterator FCitr = mlFC.iterator(); FCitr.hasNext();) {
			Map mpFC = (Map) FCitr.next();
			String countryName = (String) mpFC.get(DomainObject.SELECT_NAME);
			String restriction = (String) mpFC.get("attribute["+pgV3Constants.ATTRIBUTE_PGOVERALLCLEARANCESTATUS+"]");
			// don't process the Not Cleared formula cards
			if((!restriction.isEmpty() ) && restriction.equalsIgnoreCase(pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED)){
				continue;
			}
			cosSet.add(countryName);

			if((!restriction.isEmpty() )&& (restriction.contains("Restriction") || pgV3Constants.CLEARANCE_STATUS_CLEARED_WITH_CONDITIONS.equalsIgnoreCase(restriction))){
				cosRestricted.add(countryName);
			}
		}

		if(cosRestricted.size()>0){
			restrictedCountries.put(FCId, cosRestricted);
		}

		cosMap.put(FCId, cosSet);
		Set intersectedCountries = getIntersectingCountries(cosMap);
		return intersectedCountries;
	}
	/**
	 * fetch countries connected to PackingSubassembly and FinishedPrduct as a Substitute of any part.
	 * @param context
	 * @param ipsID
	 * @param substituteId
	 * @return  Set of Countries Connected to IPS
	 * @throws Exception
	 */
	private Set getCountriesForIPS(Context context, String ipsID, StringList substituteId) throws Exception {

		Map cosMap = new HashMap();
		DomainObject doIPS = DomainObject.newInstance(context, ipsID);
		Set cosRestricted = new HashSet();
		String whereCond = "";

		MapList mlIPSCountry = doIPS.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE,
				CPNCommonConstants.TYPE_COUNTRY, new StringList(DomainObject.SELECT_NAME), 
				new StringList("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]"), true, true, (short) 1, "", whereCond);




		// if there are substitutes then fetch the substitutes countries
		if (!substituteId.isEmpty()) {
			for (Iterator subItr = substituteId.iterator(); subItr.hasNext();) {
				String subId = (String) subItr.next();
				Set countries  = getCountriesForPart(context, subId, new StringList());

				//if subsId has restricted countries then add it to the original id set
				if(restrictedCountries.containsKey(subId)){
					Set restSet = restrictedCountries.get(subId);
					cosRestricted.addAll(restSet);
				}
				//System.out.println("subId:" + subId);
				//System.out.println("countries:" + countries);

				if(null!=countries){
					cosMap.put(subId, countries);
				}
			}
		}
		//System.out.println("cosMap:" + cosMap);
		Set cosSet = new HashSet();

		for (Iterator IPSitr = mlIPSCountry.iterator(); IPSitr.hasNext();) {
			Map mpCountry = (Map) IPSitr.next();
			String countryName = (String) mpCountry.get(DomainObject.SELECT_NAME);
			String restriction = (String) mpCountry.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSRESTRICTION + "]");
			cosSet.add(countryName);

			if((!restriction.isEmpty() )&& (restriction.contains("Yes"))){
				cosRestricted.add(countryName);
			}
		}

		if(cosRestricted.size()>0){
			restrictedCountries.put(ipsID, cosRestricted);
		}
		//System.out.println("ipsID:" + ipsID);
		//System.out.println("cosSet:" + cosSet);

		cosMap.put(ipsID, cosSet);
		Set intersectedCountries = getIntersectingCountries(cosMap);
		//System.out.println("intersectedCountries:" + intersectedCountries);

		return intersectedCountries;
	}

	/**
	 * fetch countries connected to the IPMS-POA.
	 * In case if no countries are connected then don't fetch the Art
	 * */
	private  Set getCountriesForPackingMaterial(Context context, String IPMSId,StringList substituteId) throws Exception{


		Set cosSET = new HashSet();
		Map cosMap = new HashMap();
		DomainObject doIPMS  = DomainObject.newInstance(context, IPMSId);
		boolean foundSubstitute = false;
		String whereCond = "current=="+pgV3Constants.STATE_PART_RELEASE;  
		// also get substitutes for these objects	
		StringList slObjIPMSSelect = new StringList(2);
		slObjIPMSSelect.add(DomainObject.SELECT_ID);
		slObjIPMSSelect.add(DomainObject.SELECT_CURRENT);
		Set cosRestricted = new HashSet();
		// if there are substitutes then fetch the substitutes countries
		Map artworkMap = null;
		String artId = null;
		MapList mlIPMS = new MapList();
		MapList mlIPMSCountry  = null;
		String artCurrent = null;
		//boolean bToChkPOA = false;
		if (!substituteId.isEmpty()) {
			for (Iterator subItr = substituteId.iterator(); subItr.hasNext();) {
				String subId = (String) subItr.next();

				// Modified by V4 for No Country Component 18/07/2013 Start - If PSUB with pgIncludeInCOS False is substitute for IPMS, do not consider PSUB for calculation
				StringList objectselect=new StringList(2);
				objectselect.add(DomainConstants.SELECT_TYPE);
				objectselect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				DomainObject domPSUB = DomainObject.newInstance(context,subId);
				Map psubMap=domPSUB.getInfo(context, objectselect);
				String stype=(String) psubMap.get(DomainConstants.SELECT_TYPE);
				String strIncludeInCOS=(String) psubMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
				if((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype)||pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(stype))&&"False".equalsIgnoreCase(strIncludeInCOS))
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
				{

					continue;
				}
				// Modified by V4 for No Country Component 18/07/2013 End
				Set countries  = getCountriesForPart(context, subId, new StringList());
				if(null!=countries){
					foundSubstitute = true;
					cosMap.put(subId, countries);
					//if the substitute has restricted countries then add restriction for this IPMS
					if(restrictedCountries.containsKey(subId)){
						cosRestricted.addAll(restrictedCountries.get(subId));
					}
				}
			}
		}
		if(cosRestricted.size()>0){
			restrictedCountries.put(IPMSId, cosRestricted);
		}
		//Modified by DSM-2015x.2 for COS (Req ID- 8907) on 13-10-2016 - Start
		//Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Starts
		//MapList mlIPMS = doIPMS.getRelatedObjects(context,
				//REL_POAToCOUNTRY+","+pgV3Constants.RELATIONSHIP_PART_SPECIFICATION,
				//pgV3Constants.TYPE_POA+","+pgV3Constants.TYPE_COUNTRY, false, true,3,new StringList(DomainObject.SELECT_NAME),
				//null,"",null,"", pgV3Constants.TYPE_COUNTRY,null);
		
		MapList mlIPMSArt = doIPMS.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_PART_SPECIFICATION, pgV3Constants.TYPE_POA,
				slObjIPMSSelect, null, false, true, (short) 1, "", "",0);
		
		if(null != mlIPMSArt && !mlIPMSArt.isEmpty()) {
			artworkMap = new HashMap();
			for (Iterator itrArt = mlIPMSArt.iterator(); itrArt.hasNext();) {
				artworkMap = (Map) itrArt.next();
				artId = (String) artworkMap.get(DomainObject.SELECT_ID);
				artCurrent = (String) artworkMap.get(DomainObject.SELECT_CURRENT);
				/*if(!"Release".equalsIgnoreCase(artCurrent)) {
					bToChkPOA = true;
					//break;
				}*/
				//Modified/Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36924) - Starts
				if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase(artCurrent)) {
					mlIPMSCountry = DomainObject.newInstance(context, artId).getRelatedObjects(context,
							REL_POAToCOUNTRY, pgV3Constants.TYPE_COUNTRY, new StringList(DomainObject.SELECT_NAME), null, false, true,
						(short) 1, "", "");
					if(null!=mlIPMSCountry && !mlIPMSCountry.isEmpty()){
						mlIPMS.addAll(mlIPMSCountry);
					}

				}
				//Modified/Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36924) - Ends
			}
		}
		//Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Ends
		//Modified by DSM-2015x.2 for COS (Req ID- 8907) on 13-10-2016 - End
		if(mlIPMS!=null && !mlIPMS.isEmpty()) {
			for (Iterator IPMSitr = mlIPMS.iterator(); IPMSitr.hasNext();) {
				Map mpIPMS = (Map) IPMSitr.next();
				String countryName = (String)mpIPMS.get(DomainObject.SELECT_NAME);
				cosSET.add(countryName);
			}
		}
		//Only for the case of Artwork/POA , we need to ignore the Artwork incase Countries are not present
		if(!cosSET.isEmpty()){
			cosMap.put(IPMSId, cosSET);
		}

		Set returnCountries = getIntersectingCountries(cosMap);

		// if we have not found the substitute  and still the countries are empty
		if ((!foundSubstitute) && (returnCountries.isEmpty())){
			return null;
		}

		return returnCountries;
	}

	/**
	 * fetch countries connected to the IRMS-FC.
	 * In case if no countries are connected then set the intersection to empty
	 * */
	private  Set getCountriesForRawMaterial(Context context, String IRMSId, StringList substituteId) throws Exception{
		Set cosRestricted = new HashSet();
		Map cosMap = new HashMap();
		DomainObject doIRMS  = DomainObject.newInstance(context, IRMSId);
		StringList objSelect=new StringList();
		objSelect.add("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
		Map mpIRMS= doIRMS.getInfo(context,objSelect );
		String pgSAPtype=(String) mpIRMS.get("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
		// proceed only if SAPType is not ROH  
		if(!"ROH".equalsIgnoreCase(pgSAPtype)){
			String whereCond = "";

			StringList objIRMSSelect =  new StringList(4);
			objIRMSSelect.add(DomainObject.SELECT_NAME);
			objIRMSSelect.add(DomainObject.SELECT_REVISION);
			objIRMSSelect.add(DomainObject.SELECT_TYPE);
			// Modified as per Defect 1303-- Start
			objIRMSSelect.add(DomainObject.SELECT_ID);
			objIRMSSelect.add(DomainObject.SELECT_LAST_ID);
			String ProcessedID="";
			String strLatestReleaseID="";
			// Modified as per Defect 1303-- End
			//get all the formula cards
			//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
			StringBuffer sbType = new StringBuffer(pgV3Constants.TYPE_PGPRODUCT).append(",").append(pgV3Constants.TYPE_PRODUCTDATAPART);
			MapList mlFC = doIRMS.getRelatedObjects(context,pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL, sbType.toString(),
					objIRMSSelect, null,
					true, false, (short)1, "", "");
			//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
			if(mlFC.size()<1){
				cosMap.put(IRMSId, new HashSet());
			}
                        // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID 46042 - START
			if(mlFC!=null && !mlFC.isEmpty()) {
				List<String> lsConnectedProdPartIds = RawMaterialUtils.getObjectIdsFromMapList(mlFC);
				if(lsConnectedProdPartIds!=null && !lsConnectedProdPartIds.isEmpty()) {
					Set<String> doNotProcessIds = new HashSet<String>();
					for (Iterator iterRMS = mlFC.iterator(); iterRMS.hasNext();) {
						Map mpProdPart = (Map) iterRMS.next();
						String sProdPartId = (String)mpProdPart.get(DomainConstants.SELECT_ID);
						
						Map mpReleasedPart = RawMaterialUtils.getConnetedLatestReleasedProdPart(context, lsConnectedProdPartIds, sProdPartId, doNotProcessIds);
						
						if(mpReleasedPart!=null && !mpReleasedPart.isEmpty() && !cosMap.containsKey((String)mpReleasedPart.get(DomainConstants.SELECT_ID))) {
							sProdPartId = (String)mpReleasedPart.get(DomainConstants.SELECT_ID);
							Set<String> cosSET = new HashSet<String>();
							MapList mlCountryRMS = DomainObject.newInstance(context, sProdPartId).getRelatedObjects(context, // Context context
									pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE, // String relationship Pattern
									CPNCommonConstants.TYPE_COUNTRY,  // String type Pattern
									objIRMSSelect,   // StringList object Selects
									new StringList(pgV3Constants.SELECT_ATTRIBUTE_PGOVERALLCLEARANCESTATUS), // StringList rel Selects
									false,  // boolean to side
									true,   // boolean from side
									(short)1,  // short recurse
									"",  // String object where
									whereCond, // String rel where
									0); // int limit 

							for (Iterator iterCntFC = mlCountryRMS.iterator(); iterCntFC.hasNext();) {
								Map mpCntFC = (Map) iterCntFC.next();
								
								String restriction = (String)mpCntFC.get(pgV3Constants.SELECT_ATTRIBUTE_PGOVERALLCLEARANCESTATUS);

								// don't process the Not Cleared formula cards
								if((!restriction.isEmpty() ) && restriction.equalsIgnoreCase(pgV3Constants.CLEARANCE_STATUS_NOT_CLEARED)){
									continue;
								}
								
								String countryName = (String)mpCntFC.get(DomainConstants.SELECT_NAME);
								
								cosSET.add(countryName);

								if((null!=restriction) &&(!restriction.isEmpty()) && (restriction.contains("Restriction") || pgV3Constants.CLEARANCE_STATUS_CLEARED_WITH_CONDITIONS.equalsIgnoreCase(restriction))){
									cosRestricted.add(countryName);
								}
							}
							cosMap.put(sProdPartId, cosSET);
						}
					}
				}
			}
                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID 46042 - END

		}// End for ROH check 
		// if there are substitutes then fetch the substitutes countries
		if (!substituteId.isEmpty()) {
			for (Iterator subItr = substituteId.iterator(); subItr.hasNext();) {
				String subId = (String) subItr.next();
				// Modified by V4 for No Country Component 18/07/2013 Start - If PSUB with pgIncludeInCOS False is substitute for IRMS, do not consider PSUB for calculation
				StringList objectselect=new StringList(2);
				objectselect.add(DomainConstants.SELECT_TYPE);
				objectselect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				objectselect.add("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
				DomainObject domPSUB = DomainObject.newInstance(context,subId);
				Map psubMap=domPSUB.getInfo(context, objectselect);
				String stype=(String) psubMap.get(DomainConstants.SELECT_TYPE);
				String strIncludeInCOS=(String) psubMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				String pgSAPType=(String) psubMap.get("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
				if((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(stype)||pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(stype)) && "False".equalsIgnoreCase(strIncludeInCOS))
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
				{

					continue;
				}
				// Modified by V4 for No Country Component 18/07/2013 End
				//Modified for ROH ---if rawMaterial with ROH SAPType then ignore
				//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
				if((pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(stype) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(stype)) &&(null != pgSAPType && pgSAPType.equalsIgnoreCase("ROH"))){
					continue;
				}
				//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
				Set countries  = getCountriesForPart(context, subId, new StringList());

				if(null!=countries){
					cosMap.put(subId, countries);
					if(restrictedCountries.containsKey(subId)){
						cosRestricted.addAll(restrictedCountries.get(subId));
					}
				} 
			}
		}
		//System.out.println("cosMap RAW 111111:" + cosMap);

		Set intersectedCountries = getIntersectingCountries(cosMap);
		//System.out.println("intersectedCountries RAW:" + intersectedCountries);
		//P&G: Commented for defect 3073 - If IRMS does not have connected FC, send intersected countries. Because if null is sent, this IRMS wont be considered in calculation
		// Commented for Defect 3130 	
		//System.out.println("cosMap RAW2222:" + intersectedCountries);

		 if(cosMap.isEmpty()){
			return null;
		} 

		if(cosRestricted.size()>0){
			restrictedCountries.put(IRMSId, cosRestricted);
		}

		return intersectedCountries;
	}	

	/**
	 * This common method checks the type and calls the type wise method to fetch Countries accordingly
	 * */
	private  Set getCountriesForPart(Context context, String partId, StringList substituteId) throws Exception{

		DomainObject partDO = DomainObject.newInstance(context,partId);
		String strType  = partDO.getType(context);

		//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
		if(pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(strType) || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(strType)
				|| TYPE_PGDEVICEPRODUCTPART.equalsIgnoreCase(strType) || TYPE_PGASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType)){
			return	getCountriesForFormulatedProduct( context, partId, substituteId);
		}else if(pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(strType) || pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(strType)){
			return	getCountriesForRawMaterial( context, partId, substituteId);
		}
		//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
		else if(pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PGONLINEPRINTINGPART.equalsIgnoreCase(strType)){
			return	getCountriesForPackingMaterial(context, partId, substituteId);
		} 
		//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
		else if(pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strType)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strType)||pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType))
		//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
		{
			return	getCountriesForIPS(context, partId, substituteId);
		}

		else return null;
	}
	/**
	 * Method to fetch the intersection of a Map of Set of countries.
	 * it will take intersection of all the Sets and return a Set of countries that is contained in all the Sets 
	 * */
	private  Set getIntersectingCountries(Map cosMap){
		Set intsecCOSSet = new HashSet();
		//System.out.println("cosMap:" + cosMap);

		// iterate the Map and get the smallest Set. compare that SET to other sets to get intersecting Countries
		if(!cosMap.isEmpty()){			
			intsecCOSSet = (Set)cosMap.get((cosMap.keySet().iterator().next()));			
		}else{
			return new HashSet();
		}
		
		for (Iterator cosSetItr = cosMap.keySet().iterator(); cosSetItr.hasNext();) {

			Set cosSet = (Set) cosMap.get((String)cosSetItr.next());
			if(cosSet.size()<1){
				// if you find any set which do not have any country then return empty SET
				return  new HashSet();
			}
			//this method does the intersection between the sets
			intsecCOSSet.retainAll(cosSet);
		}

		return intsecCOSSet;
	}

	/**
	 * This method will fetch the EBOM structure along with its substitute parts
	 * It does not fetch 
	 *	 * Raw Materials that are non-intermediates (i.e. ROHs)
	 *	 * Parts that are Obsolete
	 *	 * Non-GCAS Items.
	 * 
	 * **/
	public  MapList getEBOMsWithSubstitute(Context context, String objId) throws Exception{

		DomainObject dObj = DomainObject.newInstance(context,objId);

		String relationshipPattern = pgV3Constants.RELATIONSHIP_EBOM;
		String typePattern = pgV3Constants.TYPE_PART;
		StringList objectSelects = new StringList(10);
		objectSelects.add(DomainObject.SELECT_ID);
		objectSelects.add(DomainObject.SELECT_NAME);
		objectSelects.add(DomainObject.SELECT_CURRENT);
		objectSelects.add(DomainConstants.SELECT_REVISION);
		objectSelects.add(pgV3Constants.SELECT_TYPE);
		objectSelects.add(pgV3Constants.SELECT_POLICY);
		objectSelects.add("attribute["+pgV3Constants.ATTRIBUTE_PGSAPTYPE+"]");
		objectSelects.add("attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"]");
		//Modified by V4 for No Country Component start
		objectSelects.add("attribute["+pgV3Constants.ATTRIBUTE_PGINCLUDEINCOS+"]");
		//Modified by V4 for No Country Component End
		//Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - Start
		objectSelects.add("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]");
		//Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
		StringList relationshipSelects = new StringList(4);
		relationshipSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		relationshipSelects.add("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.id");

		boolean getTo = false;
		boolean getFrom = true;
		// go till n levels
		short recurseToLevel = 0;
		String objectWhere = DomainObject.SELECT_CURRENT+" != "+pgV3Constants.STATE_PART_OBSOLETE;
		String relationshipWhere = "";

		MapList mpDOList = dObj.getRelatedObjects(context, relationshipPattern, typePattern, objectSelects,
				relationshipSelects, getTo, getFrom, recurseToLevel, objectWhere, relationshipWhere);

		//System.out.println("mpDOList:" + mpDOList);
		return mpDOList;
	}
	/** 
	 *  This method find all the parents ips and set pgCosCalcuate attribute value to True for cron job calculation
	 * @param context
	 * @param domips
	 * @throws Exception
	 */
	public void setParentIPSCosCalculate(Context context,DomainObject domips) throws Exception 
	{
		try 
		{
			//Modified By DSM-2015x.5 for COS Requirement ID: 19202 on 15-Nov-2017 START
			String strRelationshipPattern = pgV3Constants.RELATIONSHIP_EBOM+","+pgV3Constants.RELATIONSHIP_ALTERNATE;
			//Modified By DSM-2015x.5 for COS Requirement ID: 19202 on 15-Nov-2017 END
			
			//Modified by DSM-15x.5 for COS Defect ID: 15572 on 15-Jan-2017 -- START
			//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
			String strPostTypePattern = pgV3Constants.TYPE_PGFINISHEDPRODUCT+","+pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY+","+pgV3Constants.TYPE_FINISHEDPRODUCTPART+","+pgV3Constants.TYPE_PACKAGINGASSEMBLYPART+","+pgV3Constants.TYPE_FABRICATEDPART+","+pgV3Constants.TYPE_PGCUSTOMERUNITPART;
			//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
			//Modified by DSM-15x.5 for COS Defect ID: 15572 on 15-Jan-2017 -- END
			
			StringList slbbjectSelects = new StringList(4);
			slbbjectSelects.addElement(DomainObject.SELECT_ID);
			slbbjectSelects.addElement(DomainObject.SELECT_TYPE);
			slbbjectSelects.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]");
			slbbjectSelects.addElement("attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"]");
			boolean getTo = true;
			boolean getFrom = false;
			int recurseToLevel = 0;
			MapList mlIPS = domips.getRelatedObjects(context, strRelationshipPattern ,pgV3Constants.TYPE_PART, getTo,getFrom, recurseToLevel, slbbjectSelects, null, null, null, null, strPostTypePattern, null);
			//System.out.println("\n mlIPS "+mlIPS);
			String strParentIpsId = DomainConstants.EMPTY_STRING;
			String strAttval = DomainConstants.EMPTY_STRING;
			String strParentType = DomainConstants.EMPTY_STRING;
			String strAttrAssemblyType = DomainConstants.EMPTY_STRING;
			//Modification to get only latest released revision and set pgCOSCalculate to true - Starts
			String arrLatestRev[] = new String[2];
			StringList slProcessedID = new StringList();
			for(Iterator itrIPS=mlIPS.iterator();itrIPS.hasNext();)
			{
				Map mpParentIps = (Map) itrIPS.next();
				strParentIpsId=(String) mpParentIps.get(DomainConstants.SELECT_ID);
				arrLatestRev = getLatestRelease(context,strParentIpsId);
				if(null != arrLatestRev && arrLatestRev.length > 1)
				{
					strParentIpsId = arrLatestRev[0];
				}
				strAttval=(String) mpParentIps.get("attribute["+pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+"]");
				
				if(null != slProcessedID && !slProcessedID.contains(strParentIpsId))
				{
					DomainObject domParentips = DomainObject.newInstance(context, strParentIpsId);
					//Modified by DSM-15x.5 for COS Defect ID: 15572 on 15-Jan-2017 -- START
					strParentType = (String) mpParentIps.get(DomainConstants.SELECT_TYPE);
					if(!pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strParentType)) {
						//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
						setMultipleAttributeValues(context, domParentips);
						//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends
					} else {
						strAttrAssemblyType = (String) mpParentIps.get("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]");
						if(pgV3Constants.RESHIPPER_ASS_TYPE_VAL.equalsIgnoreCase(strAttrAssemblyType))
							getEBOMSubstituteIPS(context, domParentips);
					}
					//Modified by DSM-15x.5 for COS Defect ID: 15572 on 15-Jan-2017 -- END
					slProcessedID.add(strParentIpsId);
				}
				//Modification to get only latest released revision and set pgCOSCalculate to true - Ends
			}
			//check if present IPS is Substitute of any other IPS if yes than set its pgCOSCalculate value to True.
			getEBOMSubstituteIPS(context,domips);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * This Method will set pgCOSCalculate value to True for IPS having current IPS as Substitute
	 * @param context
	 * @param domips
	 */
	private void getEBOMSubstituteIPS(Context context, DomainObject domips) throws Exception{
		try {
			String busSelect = "relationship[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE +"].fromrel.from.id";
			StringList busSelectPhase = new StringList(2);
			busSelectPhase.addElement(DomainObject.SELECT_ID);
			busSelectPhase.addElement(DomainObject.SELECT_NAME);
			busSelectPhase.addElement("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
			StringList relationshipSelects = new StringList(1);

			StringList slSubstitute = domips.getInfoList(context,busSelect);
			//System.out.println("slSubstitute "+slSubstitute);
			if(null != slSubstitute)
			{
				//Modification to get only latest released revision and set pgCOSCalculate to true - Starts
				String arrLatestRev[] = new String[2];
				StringList slProcessedID = new StringList();
				String strpgPhaseType = DomainConstants.EMPTY_STRING;
				String strpgPhaseID = DomainConstants.EMPTY_STRING;
				DomainObject domIPS = null;
				for(Iterator iterator = slSubstitute.iterator();iterator.hasNext();)
				{
					strpgPhaseID = (String) iterator.next();
					DomainObject dPhaseObject = DomainObject.newInstance(context,strpgPhaseID);
					
					//Added by DSM-2015x.4 for COS Defect Id- 12549 on 07 July 2017 - START
					strpgPhaseType = dPhaseObject.getInfo(context, DomainConstants.SELECT_TYPE);
					if(pgV3Constants.TYPE_FINISHEDPRODUCTPART.equals(strpgPhaseType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equals(strpgPhaseType) || pgV3Constants.TYPE_FABRICATEDPART.equals(strpgPhaseType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equals(strpgPhaseType) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equals(strpgPhaseType)) {
						arrLatestRev = getLatestRelease(context, strpgPhaseID);
						if(null != arrLatestRev && arrLatestRev.length > 1) {
							strpgPhaseID = arrLatestRev[0];
						}
						if(null != slProcessedID && !slProcessedID.contains(strpgPhaseID)) {
							domIPS = DomainObject.newInstance(context, strpgPhaseID);
							setMultipleAttributeValues(context, domIPS);
							slProcessedID.add(strpgPhaseID);
						}
					}
					//Added by DSM-2015x.4 for COS Defect Id- 12549 on 07 July 2017 - END
					
					//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
					String strPostypePattern = pgV3Constants.TYPE_PGFINISHEDPRODUCT+","+pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY+","+pgV3Constants.TYPE_FINISHEDPRODUCTPART+","+pgV3Constants.TYPE_PACKAGINGASSEMBLYPART+","+pgV3Constants.TYPE_FABRICATEDPART;
					//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
					int recurseToLevel = 0;
					MapList mlIPSObject = dPhaseObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM ,pgV3Constants.TYPE_PART, true,false, recurseToLevel, busSelectPhase, null, null, null, null, strPostypePattern, null);
					//MapList mlIPSObject = dPhaseObject.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM,pgV3Constants.TYPE_PART, busSelectPhase, relationshipSelects, true,false, (short)0, "", "");
					if(null != mlIPSObject && mlIPSObject.size() > 0)
					{
						for(int intIPSCount=0;intIPSCount<mlIPSObject.size();intIPSCount++)
						{
							Map mapIPS = (Map)mlIPSObject.get(intIPSCount);
							String attValue = (String)mapIPS.get("attribute[" +pgV3Constants.ATTRIBUTE_PGCOSCALCULATE+ "]");
							String strFPID = (String)mapIPS.get(DomainObject.SELECT_ID);
							arrLatestRev = getLatestRelease(context,strFPID);
							if(null != arrLatestRev && arrLatestRev.length > 1)
							{
								strFPID = arrLatestRev[0];
							}
							if(null != slProcessedID && !slProcessedID.contains(strFPID))
							{
								domIPS = DomainObject.newInstance(context,strFPID);
								//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Starts
								setMultipleAttributeValues(context, domIPS);
								slProcessedID.add(strFPID);
								//Modified by DSM-2015x.1 for COS (Req ID- 7530) on 10-02-2016 - Ends
							}
							//Modification to get only latest released revision and set pgCOSCalculate to true - Ends
						}
					}
				}
			}
		}
		catch (FrameworkException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/** to get latest released revision
	 * 
	 * @param context
	 * @param strIPS
	 * @return
	 * @throws Exception
	 */
	public String [] getLatestRelease(Context context,String strIPS) throws Exception{
		StringList obselect=new StringList(3);
		obselect.add(pgV3Constants.SELECT_ID);
		obselect.add(pgV3Constants.SELECT_REVISION);
		obselect.add(pgV3Constants.SELECT_CURRENT);
		String strid="";
		String revision="";
		String current="";
		String [] latestrelase= new String[2];
		//DomainObject dom = new DomainObject(strIPS);
		DomainObject dom = DomainObject.newInstance(context, strIPS);

		MapList mlRevsioninfo=	dom.getRevisionsInfo(context, obselect,new StringList());
		int size=mlRevsioninfo.size();
		for(int i=size-1;i>=0;i--)
		{
			Map mapRevsioninfo=(Map) mlRevsioninfo.get(i);

			strid=(String) mapRevsioninfo.get(pgV3Constants.SELECT_ID);
			revision=(String) mapRevsioninfo.get(pgV3Constants.SELECT_REVISION);
			current=(String) mapRevsioninfo.get(pgV3Constants.SELECT_CURRENT);
			if(pgV3Constants.STATE_PART_RELEASE.equalsIgnoreCase(current)){
				latestrelase[0]=strid;
				latestrelase[1]=revision;
				return latestrelase;
			}
		}
		return null;

	} 
	/**
	 * This Method is called to send email to inform existing COS JOB still running after 5 re-try. 
	 * @param context
	 * @param fromUserEmailAddress
	 * @param UserEmailAddress
	 * @param message
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	public boolean sendEmail(Context context,String fromUserEmailAddress, String UserEmailAddress, String message, String subject) throws Exception
	{
		boolean debug = true;
		try {

			MxMessageSupport support = new MxMessageSupport();
			support.getSendMailInfo(context);
			Properties props = new Properties();

			props.put("mail.smtp.host", support.getSmtpHost());
			Session session = Session.getDefaultInstance(props, null);

			Message msg = new MimeMessage(session);
			InternetAddress addressFrom = new InternetAddress(fromUserEmailAddress);
			msg.setFrom(addressFrom);
			//Modified for Defect no. 3347 (to send email to multiple ids):Start
			msg.addRecipients(Message.RecipientType.TO,InternetAddress.parse(UserEmailAddress));
			/*InternetAddress addressTo = new InternetAddress(UserEmailAddress);
			msg.setRecipient(Message.RecipientType.TO, addressTo);*/
			//Modified for Defect no. 3347 :end
			msg.setSubject(subject);
			BodyPart messageBodyPart = new MimeBodyPart();
			// Fill the message
			messageBodyPart.setText(message);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// Put parts in message
			msg.setContent(multipart);
			javax.mail.Transport.send(msg);
		} catch (Exception ex) {
			debug = false;
			ex.printStackTrace();
		}
		return debug;
	}

	/**
	 * This method check if IPMS or its substitute has at least one country connected
	 * In case if no countries are connected then the IPMS and its substitutes will not be considered in determining pgIncludeInCOS value of parent IPS
	 * */
	private  boolean  isIPMSconnectedToCountry(Context context, String IPMSId,StringList substituteId) throws Exception{


		Set cosSET = new HashSet();
		Map cosMap = new HashMap();
		DomainObject doIPMS  = DomainObject.newInstance(context, IPMSId);
		boolean foundSubstitute = false;
		String whereCond = "";  

		//Modified by DSM-2015x.2 for COS (Req ID- 8907) on 13-10-2016 - Start
		MapList mlIPMS = doIPMS.getRelatedObjects(context,
				REL_POAToCOUNTRY+","+pgV3Constants.RELATIONSHIP_PART_SPECIFICATION,
				pgV3Constants.TYPE_POA+","+pgV3Constants.TYPE_COUNTRY, false, true,3,new StringList(DomainObject.SELECT_NAME),
				null,"",whereCond,"", pgV3Constants.TYPE_COUNTRY,null);
		//Modified by DSM-2015x.2 for COS (Req ID- 8907) on 13-10-2016 - End
		Set cosRestricted = new HashSet();
		// if there are substitutes then fetch the substitutes countries
		if (!substituteId.isEmpty()) {
			for (Iterator subItr = substituteId.iterator(); subItr.hasNext();) {
				String subId = (String) subItr.next();

				StringList objectselect=new StringList(2);
				objectselect.add(DomainConstants.SELECT_TYPE);
				objectselect.add(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				DomainObject domPSUB = DomainObject.newInstance(context,subId);
				Map psubMap=domPSUB.getInfo(context, objectselect);
				String stype=(String) psubMap.get(DomainConstants.SELECT_TYPE);
				String strIncludeInCOS=(String) psubMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
				if((pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(stype)||pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(stype) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(stype)) && "False".equalsIgnoreCase(strIncludeInCOS))
				//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
				{

					continue;
				}

				Set countries  = getCountriesForPart(context, subId, new StringList());
				if(null!=countries){
					foundSubstitute = true;

				}
			}
		}

		for (Iterator IPMSitr = mlIPMS.iterator(); IPMSitr.hasNext();) {
			Map mpIPMS = (Map) IPMSitr.next();
			String countryName = (String)mpIPMS.get(DomainObject.SELECT_NAME);
			cosSET.add(countryName);
		}



		// If substitute or IPMS has country, return true else return false
		if ((foundSubstitute) || (!cosSET.isEmpty())){
			return true;
		}

		return false;
	}
	//Added this method for Finished Product Part COS implementation
	

	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**Added by V4-2013x.4
	 * This method structures the EBOM map with parent and child combination. 
	 * It also fetches the countries for the children other than IPS.FPP/FAB/PAP
	 * 
	 */
	public MapList createParentChildStructureForFPP(Context context, String strIPSId) throws FrameworkException {
		MapList mlFlatBOM = new MapList();
		
		try {

			//Get first level flat BOM structure for FPP
			DomainObject dObjFPP = DomainObject.newInstance(context, strIPSId);
			StringList lsObjectSelect=new StringList(4);
			lsObjectSelect.addElement(DomainConstants.SELECT_TYPE);
			lsObjectSelect.addElement(DomainConstants.SELECT_NAME);
			lsObjectSelect.addElement(DomainConstants.SELECT_REVISION);
			lsObjectSelect.addElement(DomainConstants.SELECT_ID);
			Map mpObjectInfo=dObjFPP.getInfo(context, lsObjectSelect);
			//Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - Start
			StringList slObjectSelect = getObjectSelect();
			//Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
			StringList relationshipSelects = getRelationshipSelect();
			
			String objectWhere =  DomainConstants.SELECT_CURRENT+ " != " + DomainConstants.STATE_PART_OBSOLETE;
			
			StringList slSubIntermediate = new StringList();
			StringList slSubIntermediateName = new StringList();
			StringList slSubIntermediateRev = new StringList();
			
			MapList mlFPPChildData = dObjFPP.getRelatedObjects(context,pgV3Constants.RELATIONSHIP_EBOM,pgV3Constants.TYPE_PGCONSUMERUNITPART+","+pgV3Constants.TYPE_PGCUSTOMERUNITPART+","+pgV3Constants.TYPE_PGINNERPACKUNITPART,false,true,(short)0,slObjectSelect,relationshipSelects,objectWhere,"",(short)0,"",pgV3Constants.TYPE_PGCONSUMERUNITPART+","+pgV3Constants.TYPE_PGCUSTOMERUNITPART+","+pgV3Constants.TYPE_PGINNERPACKUNITPART,null);
			//Get first level child for CUP, COP and IP
			ProductHelper productPart = new ProductHelper();
			if(null != mlFPPChildData && !mlFPPChildData.isEmpty())
			{
				DomainObject dObjIntermediate = null;
				//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005,14006 -- START
				//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- START

				Map<String,String> mapTemp = null;
				String strPolicy;

				//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- END
				//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005,14006 -- END
			    int iSize=mlFPPChildData.size();
				//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Starts
				boolean isSetProductAvailableInCUPBOM=false;
				boolean isSetProductAvailableInCUPSubBOM=false;
				MapList mlIntermediateChildData;
				String strMaterialTypes ;
				boolean isParentIsSetPoduct=false;
                int setProductPartLevel=-1;
				//check only first COP is set product or not
                for(int i=0; i<iSize; i++)
				{
                	if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase((String)((Map)mlFPPChildData.get(i)).get(DomainConstants.SELECT_TYPE))) {
                		isSetProductAvailableInCUPBOM=isPartSetProduct(context, DomainObject.newInstance(context,(String)((Map)mlFPPChildData.get(i)).get(DomainConstants.SELECT_ID)));
                		break;
                	}
				}
                boolean isPrimaryCOPSetProdIsProcessed=false;
				//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Ends
                for(int i=0; i<iSize; i++)
				{
					String strID = (String)((Map)mlFPPChildData.get(i)).get(DomainConstants.SELECT_ID);
					String strObjectType = (String)((Map)mlFPPChildData.get(i)).get(DomainConstants.SELECT_TYPE);
					String strName = (String)((Map)mlFPPChildData.get(i)).get(DomainConstants.SELECT_NAME);
					String strRev = (String)((Map)mlFPPChildData.get(i)).get(DomainConstants.SELECT_REVISION);
					String strLevel = (String)((Map)mlFPPChildData.get(i)).get(DomainConstants.SELECT_LEVEL);
					//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Starts
					strMaterialTypes = pgV3Constants.SYMBOL_STAR;
					//Check if intermediate object has substitute itself - Start
					//Modified by DSM(Sogeti) 2015x.5.1 for Req #20912 on 25-May-2019 - Added if check to include only 1st level Substitute in this block
					
				
					if("1".equals(strLevel)) {
						isSetProductAvailableInCUPSubBOM=addFirstLevelSubstitutes(context,(Map)mlFPPChildData.get(i),slSubIntermediate,slSubIntermediateName,slSubIntermediateRev,isSetProductAvailableInCUPBOM);
						if (!isSetProductAvailableInCUPBOM && isSetProductAvailableInCUPSubBOM) {
							break;
						}
					}
					
					if (isParentIsSetPoduct && setProductPartLevel<Integer.parseInt(strLevel)) {
                        continue;
					}else {
                         isParentIsSetPoduct=false;
                         setProductPartLevel=-1;
					}
					
                    if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType) && !isPrimaryCOPSetProdIsProcessed) {
                    	if(isSetProductAvailableInCUPBOM) {
                    		Map map=mpObjectInfo;
                    		if(!"1".equalsIgnoreCase(strLevel))
                    			map=(Map)mlFPPChildData.get(i-1);
                    		addConnectedBOMs((Map)mlFPPChildData.get(i), (String)map.get(DomainConstants.SELECT_NAME), (String)map.get(DomainConstants.SELECT_REVISION), (String)map.get(DomainConstants.SELECT_ID), mlFlatBOM);
	                    	
                    		strMaterialTypes = new StringBuilder(pgV3Constants.TYPE_PGPACKINGMATERIAL).append(",").append(pgV3Constants.TYPE_PACKAGINGMATERIALPART).append(",").append(pgV3Constants.TYPE_PGONLINEPRINTINGPART).toString();
	                    	isParentIsSetPoduct=true;
	                    	setProductPartLevel=Integer.parseInt(strLevel);
                    	}
                    	isPrimaryCOPSetProdIsProcessed=true;
                    }
					//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Ends
					dObjIntermediate = DomainObject.newInstance(context, strID);					
					mlIntermediateChildData = dObjIntermediate.getRelatedObjects(context,//context user
														pgV3Constants.RELATIONSHIP_EBOM,//rel pattern
														strMaterialTypes,//types pattern
														slObjectSelect,//bus select
														relationshipSelects,//rel select
														false,//get to
														true,//get from
														(short)1,//level
														objectWhere,//bus where
														"",//rel where
														0);///limit
					if(null != mlIntermediateChildData && !mlIntermediateChildData.isEmpty())
					{
						int iIntermediateChildSize=mlIntermediateChildData.size();
						for(int iCount=0;iCount<iIntermediateChildSize;iCount++)
						{
							
							String strType = (String)((Map)mlIntermediateChildData.get(iCount)).get(DomainConstants.SELECT_TYPE);
							strPolicy = (String)((Map)mlIntermediateChildData.get(iCount)).get(DomainConstants.SELECT_POLICY);
							//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
							//if BOM is set product OR both BOM and Substitute is not set product
							if(isFlatBomAllowedType(strType,strPolicy))
							{
								//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
								addConnectedBOMs((Map)mlIntermediateChildData.get(iCount), strName, strRev, strID, mlFlatBOM);
								//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005 on 20-Mar-2017 to include Alternate APPs of Base APPs -- START
								
								//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
								if(isAllowTOAddAlternate(strType)) {
									 addBOMsAlternates(context, (Map)mlIntermediateChildData.get(iCount), strLevel,mlFlatBOM);
								}
//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
								//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005 on 20-Mar-2017 to include Alternate APPs of Base APPs -- END
							}
							//Check if there is substitute
							productPart.extractSubstitutes((Map)mlIntermediateChildData.get(iCount));
							
							if(null != productPart.getId() && !productPart.getId().isEmpty())
							{
								int iSubSize=productPart.getId().size();
								for(int iSub = 0; iSub < iSubSize; iSub++)
								{
									//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
									if(isFlatBomAllowedType(productPart.getType().get(iSub),productPart.getPolicy().get(iSub)))
									{
										//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
										mapTemp = addBOMsSubstitute(productPart, iSub, strName, strRev, strID, strLevel);
										mlFlatBOM.add(mapTemp);
										//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14006 on 20-Mar-2017 to include Alternate APPs of Substitute APPs -- START
										//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
										if(isAllowTOAddAlternate(productPart.getType().get(iSub))) {
											addBOMsSubstitutesAlternates(context, productPart, iSub, strLevel, mlFlatBOM);
										}
										//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
										//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14006 on 20-Mar-2017 to include Alternate APPs of Substitute APPs -- END
									} else if(!pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(productPart.getPolicy().get(iSub))) {
										slSubIntermediate.addElement(productPart.getId().get(iSub));
										slSubIntermediateName.addElement(productPart.getName().get(iSub));
										slSubIntermediateRev.addElement(productPart.getRevision().get(iSub));
									}
								}
							}
						}
					}
				}
			}
			//Expand stringlist for Substitute intermediate objects upto nth level and get the data.
			if(null != slSubIntermediate && !slSubIntermediate.isEmpty())
			{
				DomainObject dObjInter = null;
				DomainObject dObjIntermediate = null;
				Map<String,String> tempMap = null;
				MapList mlIntermediateChildData = null;
				MapList mlConnectedInterChildData = null;
				String strInterID;
				String strID;
				String strType;
				String strName;
				String strRev;
				String strPolicy;
				String strSubInterSubType;
				String strSubInterSubPolicy;
				StringList slSubsIdList = new StringList();
				StringList slSubsNameList = new StringList();
				StringList slSubsRevList = new StringList();
				boolean isParentIsSetPoduct = false;
				int setProductPartLevel = -1;
				String strLevel;
				String strObjectType;
				String strMaterialTypes;
				int iSubIntSize=slSubIntermediate.size();
				String strSubName;
				String strSubRev;
				for(int iSubInter=0; iSubInter < iSubIntSize; iSubInter++)
				{
					strInterID = slSubIntermediate.get(iSubInter);
					dObjInter = DomainObject.newInstance(context, strInterID);
					//Check if there is intermediate object connected, if yes, get required data
					mlIntermediateChildData = dObjInter.getRelatedObjects(context,pgV3Constants.RELATIONSHIP_EBOM,pgV3Constants.TYPE_PGCONSUMERUNITPART+","+pgV3Constants.TYPE_PGCUSTOMERUNITPART+","+pgV3Constants.TYPE_PGINNERPACKUNITPART,false,true,(short)0,slObjectSelect,relationshipSelects,objectWhere,"",(short)0,"",pgV3Constants.TYPE_PGCONSUMERUNITPART+","+pgV3Constants.TYPE_PGCUSTOMERUNITPART+","+pgV3Constants.TYPE_PGINNERPACKUNITPART,null);
					strSubName=slSubIntermediateName.get(iSubInter);
					strSubRev=slSubIntermediateRev.get(iSubInter);
					//Get first level child for CUP, COP and IP
					if(null != mlIntermediateChildData && !mlIntermediateChildData.isEmpty())
					{
						int iIntChildSize=mlIntermediateChildData.size();
						//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Starts
						boolean isPrimaryCOPSetProdIsProcessed=false;
						for(int i=0;i<iIntChildSize;i++)
						{
							strID = (String)((Map)mlIntermediateChildData.get(i)).get(DomainConstants.SELECT_ID);
							strObjectType = (String)((Map)mlIntermediateChildData.get(i)).get(DomainConstants.SELECT_TYPE);
							strName = (String)((Map)mlIntermediateChildData.get(i)).get(DomainConstants.SELECT_NAME);
							strRev = (String)((Map)mlIntermediateChildData.get(i)).get(DomainConstants.SELECT_REVISION);
							strLevel = (String)((Map)mlIntermediateChildData.get(i)).get("level");
							strMaterialTypes = pgV3Constants.SYMBOL_STAR;
							if (isParentIsSetPoduct && setProductPartLevel<Integer.parseInt(strLevel)) {
		                        continue;
							}else {
		                         isParentIsSetPoduct=false;
		                         setProductPartLevel=-1;
							}
							
							//To handle reshipper first set product COP
							if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType) && !isPrimaryCOPSetProdIsProcessed) {
		                    	if(isPartCOPAndSetProduct(context, strObjectType, strID)) {
		                    		if(!"1".equalsIgnoreCase(strLevel))
		                    			addConnectedBOMs((Map)mlIntermediateChildData.get(i), (String)((Map)mlIntermediateChildData.get(i-1)).get(DomainConstants.SELECT_NAME), (String)((Map)mlIntermediateChildData.get(i-1)).get(DomainConstants.SELECT_REVISION), (String)((Map)mlIntermediateChildData.get(i-1)).get(DomainConstants.SELECT_ID), mlFlatBOM);
		                    		else
		                    			addConnectedBOMs((Map)mlIntermediateChildData.get(i), strSubName, strSubRev,strInterID, mlFlatBOM);
			                    	strMaterialTypes = pgV3Constants.TYPE_PGPACKINGMATERIAL+","+pgV3Constants.TYPE_PACKAGINGMATERIALPART+","+pgV3Constants.TYPE_PGONLINEPRINTINGPART;
			                    	isParentIsSetPoduct=true;
			                    	setProductPartLevel=Integer.parseInt(strLevel);
		                    	}
		                    	isPrimaryCOPSetProdIsProcessed=true;
		                    }
		                    //Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Ends
		                    dObjIntermediate = DomainObject.newInstance(context, strID);
							mlConnectedInterChildData = dObjIntermediate.getRelatedObjects(context,//context user
																			pgV3Constants.RELATIONSHIP_EBOM,//rel pattern
																			strMaterialTypes,//type pattern
																			slObjectSelect,//bus select
																			relationshipSelects,//rel select
																			false,//get to
																			true,//get from
																			(short)1,//level
																			objectWhere,//bus where
																			"",//rel where
																			0);//limit
							
							if(null != mlConnectedInterChildData && !mlConnectedInterChildData.isEmpty())
							{
								int iConnectedInterChildSize=mlConnectedInterChildData.size();
								for(int iCount=0;iCount<iConnectedInterChildSize;iCount++)
								{
									strType = (String)((Map)mlConnectedInterChildData.get(iCount)).get(DomainConstants.SELECT_TYPE);
									strPolicy = (String)((Map)mlConnectedInterChildData.get(iCount)).get(DomainConstants.SELECT_POLICY);
									
									//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
									if(isFlatBomAllowedType(strType, strPolicy))
									{
										//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
										
										addConnectedBOMs((Map)mlConnectedInterChildData.get(iCount),strName,strRev,strID,mlFlatBOM);
										
										//get the Alternate APP of Base APP
										//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
										if(isAllowTOAddAlternate(strType)) {
											addBOMsAlternates(context,(Map)mlConnectedInterChildData.get(iCount), "",mlFlatBOM);
										}
										//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
									}
									
									productPart.extractSubstitutes((Map)mlConnectedInterChildData.get(iCount));
									
									if(productPart.getId() != null && !productPart.getId().isEmpty()) {

										int iSubIntrSubSize=productPart.getId().size();
									
										for(int iSubInterSub=0;iSubInterSub < iSubIntrSubSize;iSubInterSub++)
										{
											strSubInterSubType = productPart.getType().get(iSubInterSub);
											strSubInterSubPolicy = productPart.getPolicy().get(iSubInterSub);
											
											if(isFlatBomAllowedType(strSubInterSubType, strSubInterSubPolicy))
											{
												tempMap = addBOMsSubstitute(productPart, iSubInterSub, strName, strRev, strID, "");
												mlFlatBOM.add(tempMap);
												//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
												//get the Alternate APP of Substitute APP
												if(isAllowTOAddAlternate(productPart.getType().get(iSubInterSub))) {
													addBOMsSubstitutesAlternates(context, productPart, iSubInterSub, "", mlFlatBOM);
												}
												//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
											}
											//Modified by DSM(Sogeti) 2015x.5.1 for Req #20912 on 25-May-2019 -- START
											else if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strSubInterSubType)) {
												slSubsIdList.addElement(productPart.getId().get(iSubInterSub));
												slSubsNameList.addElement(productPart.getName().get(iSubInterSub));
												slSubsRevList.addElement(productPart.getRevision().get(iSubInterSub));
											}
											//Modified by DSM(Sogeti) 2015x.5.1 for Req #20912 on 25-May-2019 -- END
										}
									}
								}
							}
						}
					}
					//Modified by DSM(Sogeti) 2015x.5.1 for Req #20912 on 25-May-2019 -- START
					slSubsIdList.addElement(strInterID);
					slSubsNameList.addElement(slSubIntermediateName.get(iSubInter));
					slSubsRevList.addElement(slSubIntermediateRev.get(iSubInter));
					//Modified by DSM(Sogeti) 2015x.5.1 for Req #20912 on 25-May-2019 -- END
				}
				//Now, expand substitute intermediate object and get the connected data
				if(null != slSubsIdList && !slSubsIdList.isEmpty()) {
					DomainObject doSubObject = null;
					MapList mlSubInterFirstLevelData = null;
					String strInterName;
					String strInterRev;
					int iSubIdSize=slSubsIdList.size();
					int iSubInterFirstLvlSize;
					for(int iSub=0; iSub<iSubIdSize; iSub++) {
						doSubObject = DomainObject.newInstance(context, slSubsIdList.get(iSub));
						mlSubInterFirstLevelData = doSubObject.getRelatedObjects(context,//context user
															pgV3Constants.RELATIONSHIP_EBOM,//relationship
															"*",//type pattern
															slObjectSelect,//bus select
															relationshipSelects,//rel select
															false,//get to
															true,//get from
															(short)1,//level
															objectWhere,//object where
															"",//rel where
															0);//limit

						if(null != mlSubInterFirstLevelData && !mlSubInterFirstLevelData.isEmpty())
						{
							strInterName = slSubsNameList.get(iSub);
							strInterRev = slSubsRevList.get(iSub);
							iSubInterFirstLvlSize=mlSubInterFirstLevelData.size();
							for(int iCount=0; iCount<iSubInterFirstLvlSize; iCount++)
							{
								strType = (String)((Map)mlSubInterFirstLevelData.get(iCount)).get(DomainConstants.SELECT_TYPE);
								strPolicy = (String)((Map)mlSubInterFirstLevelData.get(iCount)).get(DomainConstants.SELECT_POLICY);
								//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
								if(isFlatBomAllowedType(strType, strPolicy))
								{
									//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END

									//Added by DSM(Sogeti) 2015x.5 for COS Defect Id.15398 on 09-Jan-2018 -- START
									addConnectedBOMs((Map)mlSubInterFirstLevelData.get(iCount), strInterName, strInterRev, slSubsIdList.get(iSub), mlFlatBOM);
									//Added by DSM(Sogeti) 2015x.5 for COS Defect Id.15398 on 09-Jan-2018 -- END
									//get the Alternate APP of Base APP
									//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
									if(isAllowTOAddAlternate(strType)) {
										 addBOMsAlternates(context,(Map)mlSubInterFirstLevelData.get(iCount), "",mlFlatBOM);
									}
									//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends

									//Added by DSM(Sogeti) 2015x.5 for COS Defect Id.15399 check for Substitute on 09-Jan-2018 -- START
									productPart.extractSubstitutes((Map)mlSubInterFirstLevelData.get(iCount));

									if(productPart.getId() != null && !productPart.getId().isEmpty()) {

										int iSubIntSubSize=productPart.getId().size();
										for(int iSubInterSub=0;iSubInterSub < iSubIntSubSize;iSubInterSub++)
										{
											strSubInterSubType = productPart.getType().get(iSubInterSub);
											strSubInterSubPolicy = productPart.getPolicy().get(iSubInterSub);
											if(isFlatBomAllowedType(strSubInterSubType, strSubInterSubPolicy))
											{
												tempMap = addBOMsSubstitute(productPart, iSubInterSub, strInterName, strInterRev, slSubsIdList.get(iSub), "");
												mlFlatBOM.add(tempMap);

												//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
												//get the Alternate APP of Substitute APP
												if(isAllowTOAddAlternate(productPart.getType().get(iSubInterSub))) {
													addBOMsSubstitutesAlternates(context, productPart, iSubInterSub, "", mlFlatBOM);
												}
												//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
											}
										}
									}
									//Added by DSM(Sogeti) 2015x.5 for COS Defect Id.15399 on 09-Jan-2018 -- END
								}
							}
						}
					}
				}
			}
			//Expand FPP and get first level parts apart from CUP, COP and IP
			MapList mlFPPFirstLevelData = dObjFPP.getRelatedObjects(context,//context user
												pgV3Constants.RELATIONSHIP_EBOM,//relationship pattern
												DomainConstants.QUERY_WILDCARD, //type pattern
												slObjectSelect, //bus select
												relationshipSelects,//rel select
												false,//get to
												true,//get from
												(short)1,//level
												objectWhere,//bus where
												DomainConstants.EMPTY_STRING,//rel where
												0);//limit
			
			if(null != mlFPPFirstLevelData && !mlFPPFirstLevelData.isEmpty())
			{
				DomainObject doAPPObj = null;
				Map<String,String> mapAltAPP = null;
				MapList mlAlternateAPPs = null;
				String strId;
				String strPolicy = DomainConstants.EMPTY_STRING;
				StringBuilder sbAltAPPWhereClause;
				StringList slCountryID;
				String strType;
				String strLevel;
				int iFPPFirstLvlSize=mlFPPFirstLevelData.size();
				for(int iCount=0; iCount<iFPPFirstLvlSize; iCount++)
				{
					strType = (String)((Map)mlFPPFirstLevelData.get(iCount)).get(DomainConstants.SELECT_TYPE);
					strPolicy = (String)((Map)mlFPPFirstLevelData.get(iCount)).get(DomainConstants.SELECT_POLICY);
					strLevel = (String)((Map)mlFPPFirstLevelData.get(iCount)).get("level");
					//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
					if(isFlatBomAllowedType(strType,strPolicy))
					{
						//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
						mlFlatBOM.add(mlFPPFirstLevelData.get(iCount));
						//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005,14006 (Defect Id 12549) -- START
					//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
						if(isAllowTOAddAlternate(strType)) {
							strId = (String)((Map)mlFPPFirstLevelData.get(iCount)).get(DomainConstants.SELECT_ID);
							doAPPObj = DomainObject.newInstance(context, strId);
							//Modified by DSM(Sogeti) 2015x.5 for COS REQ Id.19213 on 21-Dec-2017 to include only Release Alt APPs -- START
							sbAltAPPWhereClause = new StringBuilder(DomainConstants.SELECT_CURRENT).append(" == ").append(pgV3Constants.STATE_RELEASE);
							//Modified by DSM(Sogeti) 2015x.5 for COS REQ Id.19213 on 21-Dec-2017 to include only Release Alt APPs -- END
							StringList slTypes=new StringList();
							if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType)) {
								slTypes.addElement(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
							}else  {
								slTypes.addElement(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
								slTypes.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
								slTypes.addElement(pgV3Constants.TYPE_FABRICATEDPART);
							}
							mlAlternateAPPs = doAPPObj.getRelatedObjects(context, //context user
														pgV3Constants.RELATIONSHIP_ALTERNATE, //relationship pattern
														StringUtil.join(slTypes, ","), //type pattern
														slObjectSelect, //bus select
														relationshipSelects, //rel select
														false, //get to
														true, //get from
														(short)1, //limit
														sbAltAPPWhereClause.toString(), //bus where
														null, //rel where
														0);//limit
							if(mlAlternateAPPs != null && !mlAlternateAPPs.isEmpty()) {
								int iAltAPPSize=mlAlternateAPPs.size();
								for(int j=0; j<iAltAPPSize; j++) {
									mapAltAPP	= (Map) mlAlternateAPPs.get(j);
									if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType)) {
										//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- START
										slCountryID =ProductHelper.getPartDetails(mapAltAPP, "from["+pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE+"].to.id");
										if(null != slCountryID && !slCountryID.isEmpty()){
											mlFlatBOM.add(mapAltAPP);
										}
									}
									else {
										mlFlatBOM.add(mapAltAPP);
									}	//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- END
								}
							}
							//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
						}
						//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005,14006 (Defect Id 12549) -- END
					}

					//Check if there is substitute
					productPart.extractSubstitutes((Map)mlFPPFirstLevelData.get(iCount));

					if(null != productPart.getId() && !productPart.getId().isEmpty())
					{
						int iSustituteSize=productPart.getId().size();
						for(int iSub = 0; iSub < iSustituteSize; iSub++)
						{
							//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
							if(isFlatBomAllowedType(productPart.getType().get(iSub), productPart.getPolicy().get(iSub)))
							{
								//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
								Map<String,String> mapTemp = new HashMap<>();
								mapTemp.put("name", productPart.getName().get(iSub));
								mapTemp.put("id[connection]", productPart.getRelID().get(iSub));
								mapTemp.put("relationship", "EBOM Substitute");
								mapTemp.put("id", productPart.getId().get(iSub));
								mapTemp.put("type", productPart.getType().get(iSub));
								mapTemp.put("revision", productPart.getRevision().get(iSub));
								mapTemp.put("current", productPart.getState().get(iSub));
								mapTemp.put("level", strLevel);
								mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST, productPart.getIsFCExist().get(iSub));
								mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST, productPart.getIsArtExist().get(iSub));
								mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST, productPart.getIsNonReleasedArtExist().get(iSub));
								mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS, productPart.getIncludeInCOS().get(iSub));
								//Modified For defect 1790 starts by V4-2013x.4
								mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, productPart.getTitle().get(iSub));
								//Modified For defect 1790 Ends by V4-2013x.4
								//Added by DSM(Sogeti) 2015x.4.1 for COS Defect Id. 14780 on 27-Oct-2017 -- START
								mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE, productPart.getSapType().get(iSub));
								//Added by DSM(Sogeti) 2015x.4.1 for COS Defect Id. 14780 on 27-Oct-2017 -- END
								//Added by DSM(Sogeti) 2018x.3 for COS Defect Id 32460 -- START
								mapTemp.put("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]", productPart.getSubAssemblyType().get(iSub));
								//Added by DSM(Sogeti) 2018x.3 for COS Defect Id 32460 -- END
								mlFlatBOM.add(mapTemp);
								//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
								//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005,14006 (Defect Id 12549) -- START
								if(isAllowTOAddAlternate(productPart.getType().get(iSub))) {
									strId = productPart.getId().get(iSub);
									doAPPObj = DomainObject.newInstance(context, strId);
									//Modified by DSM(Sogeti) 2015x.5 for COS REQ Id.19213 on 21-Dec-2017 to include only Release Alt APPs -- START
									sbAltAPPWhereClause = new StringBuilder(DomainConstants.SELECT_CURRENT).append(" == ").append(pgV3Constants.STATE_RELEASE);
									//Modified by DSM(Sogeti) 2015x.5 for COS REQ Id.19213 on 21-Dec-2017 to include only Release Alt APPs -- END
									StringList slTypes=new StringList();
									if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(productPart.getType().get(iSub))) {
										slTypes.addElement(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
									}else  {
										slTypes.addElement(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
										slTypes.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
										slTypes.addElement(pgV3Constants.TYPE_FABRICATEDPART);
									}
									
									mlAlternateAPPs = doAPPObj.getRelatedObjects(context, //context user
															pgV3Constants.RELATIONSHIP_ALTERNATE, //rel pattern
															StringUtil.join(slTypes, ","), //rel pattern
															slObjectSelect, //bus select
															relationshipSelects, //rel select
															false, //get to
															true, //get from
															(short)1, //level
															sbAltAPPWhereClause.toString(), //bus select
															null, //rel select
															0);//limit
									if(mlAlternateAPPs != null && !mlAlternateAPPs.isEmpty()) {
										for(int j=0; j<mlAlternateAPPs.size(); j++) {
											mapAltAPP 	= (Map) mlAlternateAPPs.get(j);
											//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- START
											if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(productPart.getType().get(iSub))) {
												slCountryID =ProductHelper.getPartDetails(mapAltAPP, "from["+pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE+"].to.id");
												
												if(null != slCountryID && !slCountryID.isEmpty()){
													mlFlatBOM.add(mapAltAPP);
												}
											}
											else {
												mlFlatBOM.add(mapAltAPP);
											}
											//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- END
										}
									}
								}
								//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
								//Added by DSM(Sogeti) 2015x.4 for COS REQ Id.14005,14006 (Defect Id 12549) -- END
							}
						}
					}
				}
			}
			
			//adding PAP and FAB directly connected alternates
			//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
			
			String strParentType=(String)mpObjectInfo.get(DomainConstants.SELECT_TYPE);
			if (pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strParentType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strParentType)) {
				addBOMsAlternates(context,mpObjectInfo,"", mlFlatBOM);
			}
			//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
		} catch (FrameworkException e) {
			throw e;
		}
		return mlFlatBOM;
	}
	
	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @param mapConnectedInterChildData
	 * @param strName
	 * @param strRev
	 * @param strID
	 * @param mlFlatBOM
	 */
	private void addConnectedBOMs(Map mapConnectedInterChildData, String strName, String strRev, String strID, MapList mlFlatBOM) {
		mapConnectedInterChildData.put(pgV3Constants.KEY_INTERMEDIATE_NAME, strName+"."+strRev);
		mapConnectedInterChildData.put(pgV3Constants.KEY_INTERMEDIATE_ID, strID);
		mlFlatBOM.add(mapConnectedInterChildData);
	}

	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * To Add FPP first level BOM substitutes
	 * @param mapFPPChildData
	 * @param slSubIntermediate
	 * @param slSubIntermediateName
	 * @param slSubIntermediateRev
	 * @throws FrameworkException 
	 */
	private boolean addFirstLevelSubstitutes(Context context,Map mapFPPChildData, StringList slSubIntermediate, StringList slSubIntermediateName,
			StringList slSubIntermediateRev,boolean isSetProductAvailableInCUPBOM) throws FrameworkException {
		boolean bCheckSetProduct;
		ProductHelper productPart =new ProductHelper();
		productPart.extractSubstitutes(mapFPPChildData);
		StringList slObjectSelect=new StringList(2);
		slObjectSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME);
		slObjectSelect.add(DomainConstants.SELECT_CURRENT);
		boolean isSubstituteCUPSetProduct=false;
		
		if(null !=productPart.getId()&& !productPart.getId().isEmpty())
		{
			int iSubInterIdsSize=productPart.getId().size();
			Map mpObj;
			int iFPPChildSize;
			DomainObject dObjFPP;
			MapList mlFPPChildData;
			Map<Integer,Boolean> mpSetProductCUPStatus=new HashMap<>();
			for(int iSubInter=0;iSubInter < iSubInterIdsSize;iSubInter++)
			{
				if(pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(productPart.getType().get(iSubInter)) || pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(productPart.getType().get(iSubInter)) || pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(productPart.getType().get(iSubInter)))
				{
					bCheckSetProduct=false;
					if(pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(productPart.getType().get(iSubInter))) {	
						
						dObjFPP = DomainObject.newInstance(context, productPart.getId().get(iSubInter));
						
						mlFPPChildData = dObjFPP.getRelatedObjects(context,//context user
													pgV3Constants.RELATIONSHIP_EBOM,//relationship pattern
													DomainConstants.QUERY_WILDCARD,//type pattern
													false,//get to
													true,//get from
													(short)2,//level
													slObjectSelect,//bus select
													null,//rel select
													DomainConstants.EMPTY_STRING,//bus where
													DomainConstants.EMPTY_STRING,//rel where
													(short)0,//limit
													DomainConstants.EMPTY_STRING,//post rel pattern
													DomainConstants.EMPTY_STRING,//post type pattern
													null);//post patterns
						
						if(mlFPPChildData!=null && !mlFPPChildData.isEmpty()){
							iFPPChildSize=mlFPPChildData.size();
							for (int i = 0; i < iFPPChildSize; i++) {
								mpObj=(Map)mlFPPChildData.get(i);
								if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase((String)mpObj.get(DomainConstants.SELECT_TYPE)) && !DomainConstants.STATE_PART_OBSOLETE.equalsIgnoreCase((String)mpObj.get(DomainConstants.SELECT_CURRENT))) {
									if(pgV3Constants.KEY_YES.equalsIgnoreCase((String)mpObj.get(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
										bCheckSetProduct = true;
										isSubstituteCUPSetProduct=true;
									}
									break;
								}
							}
						}
					}
					mpSetProductCUPStatus.put(iSubInter, bCheckSetProduct);
				}
			}
			if(isSubstituteCUPSetProduct || (!isSubstituteCUPSetProduct && !isSetProductAvailableInCUPBOM)) {
				for(int iSubInter=0;iSubInter < iSubInterIdsSize; iSubInter++)
				{
					if(pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(productPart.getType().get(iSubInter)) && (Boolean.TRUE.equals(mpSetProductCUPStatus.get(iSubInter)) || (!isSetProductAvailableInCUPBOM && !isSubstituteCUPSetProduct))) {
						slSubIntermediate.addElement(productPart.getId().get(iSubInter));
						slSubIntermediateName.addElement(productPart.getName().get(iSubInter));
						slSubIntermediateRev.addElement(productPart.getRevision().get(iSubInter));
					}
				}
			}
		}
		return isSubstituteCUPSetProduct;
	}

//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
     * @param context
     * @param strType
     * @param domObj
     * @return
     * @throws FrameworkException
     */
     public boolean isPartCOPAndSetProduct(Context context, String strType, String strId) throws FrameworkException {
           boolean isPartCOPAndSetProduct=false;
           if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strType) && isPartSetProduct(context, DomainObject.newInstance(context,strId))) {
                isPartCOPAndSetProduct=true;
           }
           return isPartCOPAndSetProduct;
     }


	/** This method will calculate and set isFCExist and isArtExist attribute on FP, FPP or PSUB
	 */

     public Map updateFCOrArtExist(Context context, String strIPSId) throws Exception {
 		Map returnMap = new HashMap();
 		try {
 			String blArtExist = "false";
 			String blFCExist = "false";
			//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 			String blNonReleasedArtExist = "false";
			//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 			Map valueMap = null;
 			DomainObject dObjFPP = DomainObject.newInstance(context, strIPSId);
 			
 			//Get all level EBOM structure for object
 			StringList slObjectSelect =getObjectSelectForFCAndArtExist();
 			StringList relationshipSelects = getRelationshipSelectForFCAndArtExist();

 			//Modified by DSM-2015x.1 for COS (Req ID-5934) on 11-Jan-2016 - Starts
 			
 			String sParentObjectType = dObjFPP.getInfo(context,DomainConstants.SELECT_TYPE);
 			//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
 			//fetching alternate when parent type is PAP/FAB 
 			if((pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sParentObjectType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sParentObjectType)) && ("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))) {
 					valueMap = checkAlternateReleaseArtExists(context,dObjFPP,slObjectSelect);
 					if(valueMap != null && !valueMap.isEmpty()) {
 						String sIsFCExist = (String) valueMap.get("ISFCEXIST");
 						String sIsArtExist = (String) valueMap.get("ISARTEXIST");
 						String sIsNonReleaseArtExist = (String) valueMap.get("ISNONRELEASEARTEXIST");
 						if(UIUtil.isNotNullAndNotEmpty(sIsFCExist) && "true".equalsIgnoreCase(sIsFCExist))
 							blFCExist = sIsFCExist;
 						if(UIUtil.isNotNullAndNotEmpty(sIsArtExist) && "true".equalsIgnoreCase(sIsArtExist))
 							blArtExist = sIsArtExist;
 						if(UIUtil.isNotNullAndNotEmpty(sIsNonReleaseArtExist) && "true".equalsIgnoreCase(sIsNonReleaseArtExist))
 							blNonReleasedArtExist = sIsNonReleaseArtExist;
 					}
 			}
 			//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
 			MapList mlIPSObjectWithFlatBom = dObjFPP.getRelatedObjects(context,
 														pgV3Constants.RELATIONSHIP_EBOM,
 														pgV3Constants.SYMBOL_STAR,
 														slObjectSelect, 
 														relationshipSelects,
 														false,
 														true,
 														(short)0,
 														"",
 														"",
 														0);
 			//Modified by DSM-2015x.1 for COS (Req ID-5934) on 11-Jan-2016 - Ends
 			
 			DomainObject dObjChild = null;
 			StringList slArtPOA = new StringList(2);
 			slArtPOA.addElement("from["+REL_POAToCOUNTRY+"]");
 			slArtPOA.addElement(DomainConstants.SELECT_CURRENT);

 			//Added by DSM-2015x.1 for COS (Req ID-5934) on 11-Jan-2016 - Starts
 			MapList mlIPSObject = removeComponentBelowFormulaCard(context,mlIPSObjectWithFlatBom);
 			//Added by DSM-2015x.1 for COS (Req ID-5934) on 11-Jan-2016 - Ends
 			String strArtState  = null;
			//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Starts
 			boolean isBOMCOPIsSetPoduct=false;
 			boolean isSubstituteCOPIsSetPoduct=false;
 			int setProductPartLevel=-1;
 			boolean isSetProductAvailableInCUPBOM=false;
 			boolean isSubstituteCUPHasSetProductCOP = false;
 			if(null != mlIPSObject && !mlIPSObject.isEmpty())
 			{
 				String strPolicy;
 				String sIsFCExist;
 				String sIsArtExist;
 				String sIsNonReleasedArtExist;
 				int mlIPSObjectSize=mlIPSObject.size();
 				StringList slIsExistSelectable=new StringList(3);
 				slIsExistSelectable.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
 				slIsExistSelectable.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
 				slIsExistSelectable.addElement(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
 				//checking primary COP is set product or not in BOM structure
 				for(int i=0; i<mlIPSObjectSize; i++)
				{
 					if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase((String)((Map)mlIPSObject.get(i)).get(DomainConstants.SELECT_TYPE))) {
            			isSetProductAvailableInCUPBOM=isPartSetProduct(context, DomainObject.newInstance(context,(String)((Map)mlIPSObject.get(i)).get(DomainConstants.SELECT_ID)));
            			break;
                	}
				}
 				
 				boolean isPrimaryCOPIsProcessed=false;
//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Ends 				
 				for(int iIndex = 0; iIndex < mlIPSObjectSize; iIndex++)
 				{
 					if("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))
 					{
 						Map mapTemp = (Map)mlIPSObject.get(iIndex);
 						String strObjectType = (String)mapTemp.get(DomainConstants.SELECT_TYPE);
 						String strObjectID = (String)mapTemp.get(DomainConstants.SELECT_ID);
 						strPolicy = (String)mapTemp.get(DomainConstants.SELECT_POLICY);
 						String strSAPType = (String)mapTemp.get("attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]");
						//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Starts
 						String strLevel=(String)mapTemp.get(DomainConstants.SELECT_LEVEL);
 						
 						if (isBOMCOPIsSetPoduct && setProductPartLevel+1==Integer.parseInt(strLevel)) {
							if(!(pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGONLINEPRINTINGPART.equalsIgnoreCase(strObjectType))) {
								continue;
							}
						}//skip bom component based on reshipper cup cop set product
 						else if((isBOMCOPIsSetPoduct || isSubstituteCOPIsSetPoduct) && !isSetProductAvailableInCUPBOM && setProductPartLevel<Integer.parseInt(strLevel)) {
 	                        continue;
 						}else {
 	                         isBOMCOPIsSetPoduct=false;
 	                         isSubstituteCOPIsSetPoduct=false;
 	                         setProductPartLevel=-1;
 						}
 						
 						if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType) && !isPrimaryCOPIsProcessed) {
	                    	if(isSetProductAvailableInCUPBOM) {
	                    		blFCExist="true";
	                    		isBOMCOPIsSetPoduct=true;
	 	                        setProductPartLevel=Integer.parseInt(strLevel);
	                    	}
	                    	isPrimaryCOPIsProcessed=true;
 						}//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Ends
						else if(pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGONLINEPRINTINGPART.equalsIgnoreCase(strObjectType)){
 							dObjChild = DomainObject.newInstance(context, strObjectID);
 							if("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))
 							{
 								MapList mlArtObject = dObjChild.getRelatedObjects(context, //context user
 															DomainConstants.RELATIONSHIP_PART_SPECIFICATION,//relationship pattern
 															pgV3Constants.TYPE_PGARTWORK+","+pgV3Constants.TYPE_POA, //type pattern
 															slArtPOA, //bus select
 															new StringList(), //rel select
 															false,//get to
 															true, //get from
 															(short)1,//level
 															"", //bus where
 															"", //rel where
 															0);//limit
 								if(null != mlArtObject && !mlArtObject.isEmpty())
 								{
 									int iArtObjectSize = mlArtObject.size();
 									for(int iArt = 0; iArt < iArtObjectSize; iArt++)
 									{
 										Map mapArt = (Map)mlArtObject.get(iArt);
 										String strIsCtryConnected = (String)mapArt.get("from["+REL_POAToCOUNTRY+"]");
 										//Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Starts
 										strArtState = (String)mapArt.get(DomainConstants.SELECT_CURRENT);
										//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts										
 									    if("Release".equalsIgnoreCase(strArtState) && "true".equalsIgnoreCase(strIsCtryConnected))
 										{
 											blArtExist = "true";
 										}

 									   if(!"Release".equalsIgnoreCase(strArtState) && !"Obsolete".equalsIgnoreCase(strArtState)) {
 											blNonReleasedArtExist = "true";
 										}
										//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 									  //Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Ends
 									}
 								}
 							}
							//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
							//Fetch alternates
 							if(pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strObjectType) && ("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))) {
 								valueMap = checkAlternateReleaseArtExists(context,dObjChild,slObjectSelect);
 								if(valueMap != null && !valueMap.isEmpty()) {
 									sIsFCExist = (String) valueMap.get("ISFCEXIST");
 									sIsArtExist = (String) valueMap.get("ISARTEXIST");
 									sIsNonReleasedArtExist = (String) valueMap.get("ISNONRELEASEARTEXIST");
 									if(UIUtil.isNotNullAndNotEmpty(sIsFCExist) && "true".equalsIgnoreCase(sIsFCExist))
 										blFCExist = sIsFCExist;
 									if(UIUtil.isNotNullAndNotEmpty(sIsArtExist) && "true".equalsIgnoreCase(sIsArtExist))
 										blArtExist = sIsArtExist;
 									if(UIUtil.isNotNullAndNotEmpty(sIsNonReleasedArtExist) && "true".equalsIgnoreCase(sIsNonReleasedArtExist))
 										blNonReleasedArtExist = sIsNonReleasedArtExist;
 								}
 							}
							//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
 						}
 									
 						
 						//Check for FC connected even if country is not connected
 						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
 						//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
 						else if((pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(strObjectType)
 								|| TYPE_PGDEVICEPRODUCTPART.equalsIgnoreCase(strObjectType) || TYPE_PGASSEMBLEDPRODUCTPART.equalsIgnoreCase(strObjectType))
 								&& !pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(strPolicy))
 						{
 							//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
 							blFCExist = "true";
 						}
 						//Check for FC connected through IRMS
 						else if((pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(strObjectType) 
 								|| pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(strObjectType)) && !"ROH".equalsIgnoreCase(strSAPType))
 						{
 							if("false".equalsIgnoreCase(blFCExist))
 							{
 								MapList mlFCObject = getDefinesMaterial(context, strObjectID);
 								if(null != mlFCObject && !mlFCObject.isEmpty())
 								{
 									blFCExist = "true";
 								}
 							}
 						}
 						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
 						
 						//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
 						else if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strObjectType))
 						//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936) on 4-01-2016 - Ends
 						{
 							dObjChild = DomainObject.newInstance(context, strObjectID);
 							String strCalculateCOS = (String)mapTemp.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSCALCULATE + "]");
 							String strCOSWIP = (String)mapTemp.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSWIP + "]");
 							
 							//If pgCOSCalculate is false, means there is no need to calculate isFCExist and isArtExist again. Use as it is.
 							if(null != strCalculateCOS && "false".equalsIgnoreCase(strCalculateCOS) && "false".equalsIgnoreCase(strCOSWIP))
 							{
 								String strIsFCExist = (String)mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
 	 							String strIsARTExist = (String)mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
								//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 	 							String strIsNonReleaseARTExist = (String)mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
 								//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
								if(null != strIsFCExist && "true".equalsIgnoreCase(strIsFCExist))
 								{
 									blFCExist = "true";
 								}
 								if(null != strIsARTExist && "true".equalsIgnoreCase(strIsARTExist))
 								{
 									blArtExist = "true";
 								}
								//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 								if(null != strIsNonReleaseARTExist && "true".equalsIgnoreCase(strIsNonReleaseARTExist))
 								{
 									blNonReleasedArtExist = "true";
 								}
								//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 							}
 							else
 							{
 								//Calculate isFCExist and isArtExist as there is some change in connections
 								updateFCOrArtExist(context,strObjectID);
 								Map mapAttrMap = dObjChild.getInfo(context, slIsExistSelectable);
 								
 								if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST)))
 								{
 									blFCExist = "true";
 								}
 								if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST)))
 								{
 									blArtExist = "true";
 								}
								//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 								if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST)))
 								{
 									blNonReleasedArtExist = "true";
 								}
								//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 							}
 						}

 						//Check for EBOM Substitute objects. Repeat the same method for substitute objects
 						if("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))
 						{
 							
 							StringList slObjectID = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.id"));
 							
 							if(null != slObjectID && !slObjectID.isEmpty())
 							{
 								StringList slSAPType = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]"));
 								StringList slObjectType = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.type"));
 	 							StringList slCalculateCOS = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.attribute[" + pgV3Constants.ATTRIBUTE_PGCOSCALCULATE + "]"));
 	 							StringList slCOSWIP = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.attribute[" + pgV3Constants.ATTRIBUTE_PGCOSWIP + "]"));
 	 							StringList slIsFCExist = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST));
 	 							StringList slIsARTExist = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST));
 	 							StringList slIsNonReleasedARTExist = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST));
 	 							StringList slObjectPolicy = convertToStringList(mapTemp.get("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.policy"));
 	 							Map<Integer,Boolean> mpSetProductCUPStatus=new HashMap<>();
 	 							int iObjectIdSize=slObjectID.size();
 	 							if(pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase((String)mapTemp.get(DomainConstants.SELECT_TYPE))) {
	 	 							for(int iCount=0; iCount<iObjectIdSize; iCount++)
	 								{
	 	 								if(pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(slObjectType.get(iCount))) {
	 	 									dObjChild = DomainObject.newInstance(context, slObjectID.get(iCount));
	 	 									if(isCUPHasSetProduct(context,dObjChild)) {
	 	 										mpSetProductCUPStatus.put(iCount, true);
	 	 										blFCExist="true";
 	 											isSubstituteCOPIsSetPoduct=true;
 												setProductPartLevel=Integer.parseInt(strLevel);
	 	 									}else {
	 	 										mpSetProductCUPStatus.put(iCount, false);
	 	 									}
	 	 								}
	 	 							}
 	 							}
 								for(int iCount=0; iCount<iObjectIdSize; iCount++)
 								{
 									strObjectType = slObjectType.get(iCount);
 									strObjectID = slObjectID.get(iCount);
 									strSAPType = slSAPType.get(iCount);
 									strPolicy = slObjectPolicy.get(iCount);
 									//Check for Art/POA with Countries
									//Modified/Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 									if("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))
 									{
									//Modified/Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
									if(pgV3Constants.TYPE_PGPACKINGMATERIAL.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGONLINEPRINTINGPART.equalsIgnoreCase(strObjectType))
 									{
 										dObjChild = DomainObject.newInstance(context, strObjectID);
 										if("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))
 										{
 											MapList mlArtObject = dObjChild.getRelatedObjects(context,//context user
 																		DomainConstants.RELATIONSHIP_PART_SPECIFICATION,//relationship pattern
 																		pgV3Constants.TYPE_PGARTWORK+","+pgV3Constants.TYPE_POA, //type pattern
 																		slArtPOA, //bus select
 																		new StringList(), //rel select
 																		false,//get to 
 																		true, //get from
 																		(short)1,//level
 																		"", //bus where
 																		"",//rel where
 																		0);//limit

 											if(null != mlArtObject && !mlArtObject.isEmpty())
 											{
 												int iArtObjectSize=mlArtObject.size();
 												for(int iArt = 0; iArt < iArtObjectSize; iArt++)
 												{
 													Map mapArt = (Map)mlArtObject.get(iArt);
 													String strIsCtryConnected = (String)mapArt.get("from["+REL_POAToCOUNTRY+"]");
 													//Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Starts
 													strArtState = (String)mapArt.get(DomainConstants.SELECT_CURRENT);
 													if("Release".equalsIgnoreCase(strArtState) && "true".equalsIgnoreCase(strIsCtryConnected))
 													{
 														blArtExist = "true";
 													}
													//Modified/Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 													if(!"Release".equalsIgnoreCase(strArtState) && !"Obsolete".equalsIgnoreCase(strArtState)) {
 														blNonReleasedArtExist = "true";
 													}
													//Modified/Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 													//Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Ends
 												}
 											}
 										}//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
 										//check PMP alternates
 										if(pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strObjectType) && ("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blNonReleasedArtExist))) {
											valueMap = checkAlternateReleaseArtExists(context,dObjChild,slObjectSelect);
											if(valueMap != null) {
												sIsFCExist = (String) valueMap.get("ISFCEXIST");
												sIsArtExist = (String) valueMap.get("ISARTEXIST");
												sIsNonReleasedArtExist = (String) valueMap.get("ISNONRELEASEARTEXIST");
												if(UIUtil.isNotNullAndNotEmpty(sIsFCExist) && "true".equalsIgnoreCase(sIsFCExist))
													blFCExist = sIsFCExist;
												if(UIUtil.isNotNullAndNotEmpty(sIsArtExist) && "true".equalsIgnoreCase(sIsArtExist))
													blArtExist = sIsArtExist;
												if(UIUtil.isNotNullAndNotEmpty(sIsNonReleasedArtExist) && "true".equalsIgnoreCase(sIsNonReleasedArtExist))
													blNonReleasedArtExist = sIsNonReleasedArtExist;
											}
 										}
										//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends
 										
 									}

 									//Check for FC connected even if country is not connected
 									//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
 									//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- START
 									else if((pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(strObjectType)
 											|| TYPE_PGDEVICEPRODUCTPART.equalsIgnoreCase(strObjectType) || TYPE_PGASSEMBLEDPRODUCTPART.equalsIgnoreCase(strObjectType))
 											&& !pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(strPolicy))
 									{
 										//Added by DSM(Sogeti) 2015x.5 for COS REQ Id.19829 for Clone Policy on 02-Jan-2018 -- END
 										blFCExist = "true";
 									}

 									//Check for FC connected as parent to IRMS
 									else if((pgV3Constants.TYPE_PGRAWMATERIAL.equalsIgnoreCase(strObjectType) 
 											|| pgV3Constants.TYPE_RAWMATERIALPART.equalsIgnoreCase(strObjectType)) && !"ROH".equalsIgnoreCase(strSAPType))
 									{
 										if("false".equalsIgnoreCase(blFCExist))
 										{
 											MapList mlFCObject =getDefinesMaterial(context, strObjectID);
 											if(null != mlFCObject && !mlFCObject.isEmpty())
 											{
 												blFCExist = "true";
 											}
 										}
									//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Starts
 									}else if(pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strObjectType)) {
 										
 										if(("false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blNonReleasedArtExist)) && (isSubstituteCOPIsSetPoduct || (!isSubstituteCOPIsSetPoduct && !isSetProductAvailableInCUPBOM)))
 										{
											if(Boolean.TRUE.equals(mpSetProductCUPStatus.get(iCount)) || (!isSetProductAvailableInCUPBOM && !isSubstituteCOPIsSetPoduct)) {
												valueMap = updateFCOrArtExist(context,strObjectID);
 												if(valueMap != null && !valueMap.isEmpty()) {
 													sIsFCExist = (String) valueMap.get("ISFCEXIST");
 													sIsArtExist = (String) valueMap.get("ISARTEXIST");
 													sIsNonReleasedArtExist = (String) valueMap.get("ISNONRELEASEARTEXIST");
 													if(UIUtil.isNotNullAndNotEmpty(sIsFCExist) && "true".equalsIgnoreCase(sIsFCExist))
 														blFCExist = sIsFCExist;
 													if(UIUtil.isNotNullAndNotEmpty(sIsArtExist) && "true".equalsIgnoreCase(sIsArtExist))
 														blArtExist = sIsArtExist;
 													if(UIUtil.isNotNullAndNotEmpty(sIsNonReleasedArtExist) && "true".equalsIgnoreCase(sIsNonReleasedArtExist))
 														blNonReleasedArtExist = sIsNonReleasedArtExist;
 												}
 											}	
 										}
 									}
									//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Ends
 									//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
 									//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936) on 4-01-2016 - Starts
 									else if(pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strObjectType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strObjectType))
 										//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936) on 4-01-2016 - Ends
 									{
 										String strCalculateCOS = slCalculateCOS.get(iCount);
 										String strCOSWIP = slCOSWIP.get(iCount);
 										dObjChild = DomainObject.newInstance(context, strObjectID);
 										//If pgCOSCalculate is false, means there is no need to calculate isFCExist and isArtExist again. Use as it is.
 										if(null != strCalculateCOS && "false".equalsIgnoreCase(strCalculateCOS) && "false".equalsIgnoreCase(strCOSWIP))
 										{
 											String strIsFCExist = slIsFCExist.get(iCount);
 	 										String strIsARTExist = slIsARTExist.get(iCount);
											//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 	 										String strIsNonReleasedARTExist = slIsNonReleasedARTExist.get(iCount);
											//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 	 										if(null != strIsFCExist && "true".equalsIgnoreCase(strIsFCExist))
 											{
 												blFCExist = "true";
 											}
 											if(null != strIsARTExist && "true".equalsIgnoreCase(strIsARTExist))
 											{
 												blArtExist = "true";
 											}
											//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 											if(null != strIsNonReleasedARTExist && "true".equalsIgnoreCase(strIsNonReleasedARTExist))
 											{
 												blNonReleasedArtExist = "true";
 											}
											//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 										}
 										else
 										{
 											//Calculate isFCExist and isArtExist as there is some change in connections
 											valueMap = updateFCOrArtExist(context,strObjectID);
 											if(!pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strObjectType)) {
	 											Map mapAttrMap = dObjChild.getInfo(context, slIsExistSelectable);
	 											if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST)))
	 											{
	 												blFCExist = "true";
	 											}
	 											if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST)))
	 											{
	 												blArtExist = "true";
	 											}
												//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
	 											if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST)))
	 											{
	 												blNonReleasedArtExist = "true";
	 											}
												//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 											}else {
 												if(valueMap != null) {
 													sIsFCExist = (String) valueMap.get("ISFCEXIST");
 													sIsArtExist = (String) valueMap.get("ISARTEXIST");
													//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 													sIsNonReleasedArtExist = (String) valueMap.get("ISNONRELEASEARTEXIST");
													//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 													if(UIUtil.isNotNullAndNotEmpty(sIsFCExist) && "true".equalsIgnoreCase(sIsFCExist))
 														blFCExist = sIsFCExist;
 													if(UIUtil.isNotNullAndNotEmpty(sIsArtExist) && "true".equalsIgnoreCase(sIsArtExist))
 														blArtExist = sIsArtExist;
														//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 													if(UIUtil.isNotNullAndNotEmpty(sIsNonReleasedArtExist) && "true".equalsIgnoreCase(sIsNonReleasedArtExist))
 														blNonReleasedArtExist = sIsNonReleasedArtExist;
														//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 												}	
 											}
 										}
 									}
 								}
 								}
 							}
 						}
 					} else {
 						break;
 					}
 				}
 			}
 		//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Starts
 			if(pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sParentObjectType) || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sParentObjectType) || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sParentObjectType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sParentObjectType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sParentObjectType)) {
 			//Modified by DSM-2015x.1 for COS (Req ID- 6278,5936,6279,6280) on 4-01-2016 - Ends
 				Map mapAttributes = new HashMap();
 				mapAttributes.put(pgV3Constants.ATTRIBUTE_ISFCEXIST, blFCExist);
 				mapAttributes.put(pgV3Constants.ATTRIBUTE_ISARTEXIST, blArtExist);
				//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 				mapAttributes.put(pgV3Constants.ATTRIBUTE_NON_RELEASED_ART_EXIST, blNonReleasedArtExist);
				//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 				dObjFPP.setAttributeValues(context, mapAttributes);
 			}
 			if("true".equalsIgnoreCase(blFCExist))
 				returnMap.put("ISFCEXIST", blFCExist);
 			if("true".equalsIgnoreCase(blArtExist))
 				returnMap.put("ISARTEXIST", blArtExist);
				//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Starts
 			if("true".equalsIgnoreCase(blNonReleasedArtExist))
 				returnMap.put("ISNONRELEASEARTEXIST", blNonReleasedArtExist);
				//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36925) - Ends
 		} catch(Exception ex)
 		{
 			ex.printStackTrace();
 		}
 		return returnMap;
 	}
	
 	//Modified/Added DSM(Sogeti) 18x.6 Refactor
 	/**
 	 * @param context
 	 * @param strObjectID
 	 * @return
 	 * @throws FrameworkException
 	 */
 	private MapList getDefinesMaterial(Context context, String strObjectID) throws FrameworkException {
 		DomainObject dObjChild = DomainObject.newInstance(context, strObjectID);
		StringBuffer sbType = new StringBuffer(pgV3Constants.TYPE_PGFORMULATEDPRODUCT).append(",").append(pgV3Constants.TYPE_FORMULATIONPART)
				.append(",").append(TYPE_PGDEVICEPRODUCTPART).append(",").append(TYPE_PGASSEMBLEDPRODUCTPART);
		// Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID 46042 - START
                MapList mlReturnFC = new MapList();
		MapList mlFC =  dObjChild.getRelatedObjects(context, 
				pgV3Constants.RELATIONSHIP_PGDEFINESMATERIAL, //relationship
				sbType.toString(), //types
				new StringList(DomainObject.SELECT_ID), //object selectable
				new StringList(), //relationship selectable
				true, //get to
				false, //get from
				(short)1, //level
				DomainConstants.EMPTY_STRING, //object where
				DomainConstants.EMPTY_STRING, //rel where
				0);//count
		if(mlFC!=null && !mlFC.isEmpty()) {
			List<String> lsConnectedProdPartIds = RawMaterialUtils.getObjectIdsFromMapList(mlFC);
			if(lsConnectedProdPartIds!=null && !lsConnectedProdPartIds.isEmpty()) {
				Set<String> doNotProcessIds = new HashSet<String>();
				for (Iterator iterRMS = mlFC.iterator(); iterRMS.hasNext();) {
					Map mpProdPart = (Map) iterRMS.next();
					String sProdPartId = (String)mpProdPart.get(DomainConstants.SELECT_ID);
					
					Map mpReleasedPart = RawMaterialUtils.getConnetedLatestReleasedProdPart(context, lsConnectedProdPartIds, sProdPartId, doNotProcessIds);
					
					if(mpReleasedPart!=null && !mpReleasedPart.isEmpty()) {
						mlReturnFC.add(mpReleasedPart);
					}
				}
			}
		}
                // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID 46042 - END
		return mlReturnFC;
	}
	
	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
 	 * @param object
 	 * @return
 	 */
 	private StringList convertToStringList(Object object) {
 		StringList slObjectInfo=new StringList();
 		if(object!=null) {
 			if (object instanceof StringList)
			{
 				slObjectInfo=(StringList)object;
			}else {
				slObjectInfo.addElement((String)object);
			}
 		}
		return slObjectInfo;
	}

	//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Starts
	/**
 	 * @param context
 	 * @param dObjCUP
 	 * @return
 	 * @throws FrameworkException
 	 */
 	public static boolean isCUPHasSetProduct(Context context,DomainObject dObjCUP) throws FrameworkException {
 		boolean bCheckSetProduct=false;
 		StringList slObjectSelect=new StringList(3);
 		slObjectSelect.add(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME);
		slObjectSelect.add(DomainConstants.SELECT_CURRENT);
		slObjectSelect.add(DomainConstants.SELECT_TYPE);
 		
 		
 		MapList mlFPPChildData = dObjCUP.getRelatedObjects(context,//context user
 										pgV3Constants.RELATIONSHIP_EBOM,//relationship pattern
 										DomainConstants.QUERY_WILDCARD,//type pattern
 										false,//get to
 										true,//get from
 										(short)2,//level
 										slObjectSelect,//bus select
 										null,//rel select
 										DomainConstants.EMPTY_STRING,//bus where
 										DomainConstants.EMPTY_STRING,//rel where
 										(short)0,//limit
 										DomainConstants.EMPTY_STRING,//post rel pattern
 										DomainConstants.EMPTY_STRING,//post type pattern
 										null);//post patterns
		
		if(mlFPPChildData!=null && !mlFPPChildData.isEmpty()){
			int iFPPChildSize=mlFPPChildData.size(); 
			Map mpObj;
			for (int i = 0; i < iFPPChildSize; i++) {
				mpObj=(Map)mlFPPChildData.get(i);
				if(!DomainConstants.STATE_PART_OBSOLETE.equalsIgnoreCase((String)mpObj.get(DomainConstants.SELECT_CURRENT)) && pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase((String)mpObj.get(DomainConstants.SELECT_TYPE))){
					if(pgV3Constants.KEY_YES.equalsIgnoreCase((String)mpObj.get(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
						bCheckSetProduct = true;
					}
					break;
				}
			}	
		}
 		return bCheckSetProduct;
 	}
	//Added by DSM(Sogeti) 2018x.6 for COS (Req ID- 36247,37087,36549,36245,36926,36270) - Ends
	//Added by DSM-2015x.1 for COS (Req ID- 5934) on 11-01-2016 - Starts
	/**
	 * @Desc This method will accept FlatBOM and return a EBOM maplist which will not contain components connected below FC.
	 * @param context
	 * @param MapList
	 * @return MapList
	 * @throws Exception
	**/
	public MapList removeComponentBelowFormulaCard(Context context, MapList mlIPSObjectWithFlatBom) throws Exception {
		MapList mlIPSObject = new MapList();
		try {
			int intMlIPSObjectWithFlatBomCount = mlIPSObjectWithFlatBom.size();
			if(null != mlIPSObjectWithFlatBom && intMlIPSObjectWithFlatBomCount > 0) {
				int iFcLevel = 0;
				String strObjectType = null;
				Map mapTemp = new HashMap();
				int iCurrLevel = 0;
				for(int iIndex = 0; iIndex < intMlIPSObjectWithFlatBomCount; iIndex++) {
					mapTemp = (Map)mlIPSObjectWithFlatBom.get(iIndex);
					strObjectType = (String)mapTemp.get(DomainConstants.SELECT_TYPE);
					iCurrLevel = Integer.parseInt((String)mapTemp.get(DomainConstants.SELECT_LEVEL));
					if(iFcLevel != 0 && iCurrLevel == iFcLevel) {
						iFcLevel = 0;
					}
					//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - Start
					if(UIUtil.isNotNullAndNotEmpty(strObjectType) && iFcLevel == 0
							&& (pgV3Constants.TYPE_PGFORMULATEDPRODUCT.equalsIgnoreCase(strObjectType)
									|| pgV3Constants.TYPE_FORMULATIONPART.equalsIgnoreCase(strObjectType)
									|| TYPE_PGDEVICEPRODUCTPART.equalsIgnoreCase(strObjectType) 
									|| TYPE_PGASSEMBLEDPRODUCTPART.equalsIgnoreCase(strObjectType) 
									|| (sMOSCircularParentTypes.contains(strObjectType) && mapTemp.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSCALCULATE + "]").toString().equalsIgnoreCase(pgV3Constants.KEY_FALSE) && mapTemp.get(pgV3Constants.SELECT_ATTRIBUTE_PGCOSWIP).toString().equalsIgnoreCase(pgV3Constants.KEY_FALSE)))  ) {
						//Modified by DSM-2015x.2 for COS (Req ID- 10555,10556,10820,10821,10822) on 13-10-2016 - End
						iFcLevel = iCurrLevel;
					} else {
						if((iFcLevel < iCurrLevel) && iFcLevel != 0) {
							continue;
						}
					}
					mlIPSObject.add(mapTemp);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return mlIPSObject;
	}
	//Added by DSM-2015x.1 for COS (Req ID- 5934) on 11-01-2016 - Ends
	
	/**
	 * @Desc This utility method is used to set attribute value.
	 * @Added by DSM-2015x.1 for COS on 02-02-2016
	 * @param context
	 * @param DomainObject
	 * @return void
	 * @throws Exception
	*/
	public void setMultipleAttributeValues(Context context, DomainObject domParentObj)throws Exception{
		try {
			//Added by DSM-2015x.2 for Defect ID- 7372 on 04-11-2016 - Start
			StringList slSelect = new StringList(4);
			slSelect.addElement(DomainConstants.SELECT_ID);
			slSelect.addElement(DomainConstants.SELECT_POLICY);
			// Modified for Market Registration Rollup marking - Start
			slSelect.addElement(DomainConstants.SELECT_TYPE);
			slSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
			// Modified for Market Registration Rollup marking - End
			Map infoMap = domParentObj.getInfo(context, slSelect);
			String strObjectId = (String) infoMap.get(DomainConstants.SELECT_ID);
			if(UIUtil.isNotNullAndNotEmpty(strObjectId)) {
				boolean hasModifyAccess = Access.hasAccess(context, strObjectId, "modify");
				if(hasModifyAccess) {
					//Added by DSM-2015x.2 for Defect ID- 7372 on 04-11-2016 - End
					//Added by DSM(Sogeti)-2015x.1 for "Avoiding MEP SEP objects in COS" on 29-07-2016 - Starts 
					String strPolicy = (String) infoMap.get(DomainConstants.SELECT_POLICY);
					if(!(pgV3Constants.POLICY_MANUFACTURER_EQUIVALENT.equalsIgnoreCase(strPolicy) || pgV3Constants.POLICY_SUPPLIEREQUIVALENT.equalsIgnoreCase(strPolicy))){
						//Added by DSM(Sogeti)-2015x.1 for "Avoiding MEP SEP objects in COS" on 29-07-2016 - Ends 
						DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
						Date date = Calendar.getInstance().getTime();
						String strFormattedDate = dateFormatter.format(date);
						Map mpAttributes = new HashMap();
						mpAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSCALCULATE, pgV3Constants.ATTRIBUTE_PGCOSCALCULATE_TRUE);
						mpAttributes.put(pgV3Constants.ATTRIBUTE_PGCOSFLAGDATE, strFormattedDate);

						// Modified for Market Registration Rollup marking - Start
						updateRollupMarking(infoMap, mpAttributes);
                        // Modified for Market Registration Rollup marking - End
						domParentObj.setAttributeValues(context, mpAttributes);
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @Desc This method is used to build a BOM Map for flat BOM structure.
	 * @Added by DSM-2015x.4 for COS on 22-May-2017
	 * @return Map
	*/
	private Map getBOMMap(Map mapAltAPP, String strAPPId, String strAPPName, String strAPPRev, String strLevel) {
		Map mapTemp = new HashMap();
		
		mapTemp.put(DomainConstants.SELECT_NAME, (String)mapAltAPP.get(DomainConstants.SELECT_NAME));
		mapTemp.put(DomainConstants.SELECT_RELATIONSHIP_ID, (String)mapAltAPP.get(DomainRelationship.SELECT_ID));
		mapTemp.put(DomainConstants.KEY_RELATIONSHIP, pgV3Constants.RELATIONSHIP_ALTERNATE);
		mapTemp.put(DomainConstants.SELECT_ID, (String)mapAltAPP.get(DomainConstants.SELECT_ID));
		mapTemp.put(DomainConstants.SELECT_TYPE, (String)mapAltAPP.get(DomainConstants.SELECT_TYPE));
		mapTemp.put(pgV3Constants.KEY_INTERMEDIATE_NAME, strAPPName+"."+strAPPRev);
		mapTemp.put(pgV3Constants.KEY_INTERMEDIATE_ID, strAPPId);
		mapTemp.put(DomainConstants.SELECT_REVISION, (String)mapAltAPP.get(DomainConstants.SELECT_REVISION));
		mapTemp.put(DomainConstants.SELECT_CURRENT, (String)mapAltAPP.get(DomainConstants.SELECT_CURRENT));
		mapTemp.put(DomainConstants.SELECT_LEVEL, strLevel);
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST, (String)mapAltAPP.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST));
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST, (String)mapAltAPP.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST));
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS, (String)mapAltAPP.get(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS));
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, (String)mapAltAPP.get(pgV3Constants.SELECT_ATTRIBUTE_TITLE));
		
		return mapTemp;
	}

	/**
	* This product part is retrieve is cop set product or not
	* @Added by DSM-2018x.6 (Sogeti) for MOS Requirement 8802
	* @param context
	* @param copId
	* @throws FrameworkException
	*/
	public boolean isPartSetProduct(Context context, DomainObject domObj) throws FrameworkException {
		boolean bCheckSetProduct = false;
		if(pgV3Constants.KEY_YES.equalsIgnoreCase(domObj.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
			bCheckSetProduct = true;
		}
		return bCheckSetProduct;
	}
	
	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @return
	 */
	private StringList getObjectSelect() {
		StringList slObjectSelect = new StringList(15);
		// Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - End
		slObjectSelect.addElement(DomainConstants.SELECT_ID);
		slObjectSelect.addElement(DomainConstants.SELECT_NAME);
		slObjectSelect.addElement(DomainConstants.SELECT_TYPE);
		slObjectSelect.addElement(DomainConstants.SELECT_REVISION);
		slObjectSelect.addElement(DomainConstants.SELECT_CURRENT);
		slObjectSelect.addElement(DomainConstants.SELECT_POLICY);
		slObjectSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_TITLE + "]");
		slObjectSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]");
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		// Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- START
		slObjectSelect.addElement("from[" + pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE + "].to.id");
		// Added by DSM-2018x.3 for COS (Req ID- 30899) on 16-01-2020 - Start
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
		slObjectSelect.addElement(MOSOverrideConstants.SELECT_ATTRIBUTE_PGHASMOSPARTIALOVERRIDDEN); // Added/Modified by (DSM Sogeti) for Apr CW 2022 - MOS Override Requirements ID (41972,42016,42017,42018,42020,42031,42033,42034,42035,42039,42041,42042,42043,42045,42048,42049,42051,42053,42134,42135,42192,42193,42222,42241,42302,42489,42244)
		return slObjectSelect;
	}
	
	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @return
	 */
	private StringList getRelationshipSelect() {
		StringList relationshipSelects = new StringList(15);
		// Added by DSM(Sogeti) 2018x.3 for COS Defect Id 32460 -- END
		relationshipSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].id");
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.id");
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.type");
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.name");
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.revision");
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.current");
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.policy");
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to."
				+ pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to."
				+ pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to."
				+ pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS);
		// Modified For defect 1790 starts by V4-2013x.4
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to."
				+ pgV3Constants.SELECT_ATTRIBUTE_TITLE);
		// Modified For defect 1790 Ends by V4-2013x.4
		// Added by DSM(Sogeti) 2015x.4.1 for COS Defect Id. 14780 on 27-Oct-2017 --
		// START
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to."
				+ pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
		// Added by DSM(Sogeti) 2015x.4.1 for COS Defect Id. 14780 on 27-Oct-2017 -- END
		// Added by DSM(Sogeti) 2018x.3 for COS Defect Id 32460 -- START
		relationshipSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "].to.attribute["
				+ pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE + "]");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
		
		return relationshipSelects;
	}
	
	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @return
	 */
	private StringList getObjectSelectForFCAndArtExist() {
		StringList slObjectSelect = new StringList(9);
		slObjectSelect.addElement(DomainConstants.SELECT_ID);
		slObjectSelect.addElement(DomainConstants.SELECT_NAME);
		slObjectSelect.addElement(DomainConstants.SELECT_TYPE);
		slObjectSelect.addElement(DomainConstants.SELECT_CURRENT);
		slObjectSelect.addElement(DomainConstants.SELECT_POLICY);
		slObjectSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSCALCULATE + "]");
		slObjectSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSWIP + "]");
		slObjectSelect.addElement("attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]");
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		slObjectSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
		return slObjectSelect;
	}
	
	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @return
	 */
	private StringList getRelationshipSelectForFCAndArtExist() {

		StringList relationshipSelects = new StringList(9);
		relationshipSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.id");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.type");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.name");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.policy");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.attribute[" + pgV3Constants.ATTRIBUTE_PGCOSCALCULATE + "]");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.attribute[" + pgV3Constants.ATTRIBUTE_PGCOSWIP + "]");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to.attribute[" + pgV3Constants.ATTRIBUTE_PGSAPTYPE + "]");
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		relationshipSelects.addElement("frommid["+pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE+"].to."+pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
		return relationshipSelects;
	}
	
//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
	/**
	 * @param context
	 * @param mapInterChild
	 * @param strLevel
	 * @param slObjectSelect
	 * @return
	 * @throws FrameworkException
	 */
	private void addBOMsAlternates(Context context, Map mapInterChild, String strLevel,MapList mlFlatBOM) throws FrameworkException {
		String strId = (String)mapInterChild.get(DomainConstants.SELECT_ID);
		String strName = (String)mapInterChild.get(DomainConstants.SELECT_NAME);
		String strRev = (String)mapInterChild.get(DomainConstants.SELECT_REVISION);
		String strType = (String)mapInterChild.get(DomainConstants.SELECT_TYPE);
		addAlternates(context, strId,strType,strName, strRev, strLevel,mlFlatBOM);
	}
	//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends

//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
	/**
	 * @param context
	 * @param productPart
	 * @param iSub
	 * @param strLevel
	 * @param mlFlatBOM
	 * @throws FrameworkException
	 */
	private void addBOMsSubstitutesAlternates(Context context, ProductHelper productPart,int iSub, String strLevel,MapList mlFlatBOM) throws FrameworkException {
		addAlternates(context, productPart.getId().get(iSub), productPart.getType().get(iSub), productPart.getName().get(iSub), productPart.getRevision().get(iSub), strLevel, mlFlatBOM);
	}
	/**
	 * @param context
	 * @param strId
	 * @param strName
	 * @param strRev
	 * @param strLevel
	 * @param slObjectSelect
	 * @return
	 * @throws FrameworkException
	 */
	private void addAlternates(Context context, String strId,String strType,String strName,String strRev, String strLevel,MapList mlFlatBOM) throws FrameworkException {
		
		DomainObject doObj = DomainObject.newInstance(context, strId);
		StringList slTypes=new StringList();
		if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType)) {
			slTypes.addElement(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART);
		}else  {
			slTypes.addElement(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
			slTypes.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
			slTypes.addElement(pgV3Constants.TYPE_FABRICATEDPART);
		}
		
		MapList mlAlternates = doObj.getRelatedObjects(context,//context user
									pgV3Constants.RELATIONSHIP_ALTERNATE, //relationship
									StringUtil.join(slTypes, ","), //types
									getObjectSelect(), //object select
									null, //rel select
									false, //get to
									true, //get from
									(short)1, //level
									"", //object where
									null, //rel where
									0);//count
		if(mlAlternates != null && !mlAlternates.isEmpty()) {
			int iAlternateSize=mlAlternates.size();
			Map mapTemp;
			Map mapAlt;
			StringList slCountryID;
			for(int j=0; j<iAlternateSize; j++) {
				mapAlt 	= (Map) mlAlternates.get(j);
				//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- START
				if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase((String)mapAlt.get(DomainConstants.SELECT_CURRENT))){
					if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType)) {
						slCountryID =ProductHelper.getPartDetails(mapAlt,"from["+pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE+"].to.id");
						if(slCountryID!=null && !slCountryID.isEmpty()){
							mapTemp = getBOMMap(mapAlt, strId, strName, strRev, strLevel);
							mlFlatBOM.add(mapTemp);
						}
					} else {
						mapTemp = getBOMMap(mapAlt, strId, strName, strRev, strLevel);
						mlFlatBOM.add(mapTemp);
					}
				}
				//Added by DSM(Sogeti) 2015x.5 for COS Req Id 19208 on 08-Nov-2017 -- END
			}
		}
	}
	//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends

	//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @param strType
	 * @param strPolicy
	 * @return
	 */
	public boolean isFlatBomAllowedType(String strType, String strPolicy) {
		boolean isPartAllowed=false;
		if(!pgV3Constants.TYPE_PGPHASE.equalsIgnoreCase(strType) 
				&& !pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(strType) 
				&& !pgV3Constants.TYPE_PGINNERPACKUNITPART.equalsIgnoreCase(strType) 
				&& !pgV3Constants.TYPE_PGCONSUMERUNITPART.equalsIgnoreCase(strType) 
				&& !pgV3Constants.TYPE_PGANCILLARYPACKAGINGMATERIALPART.equalsIgnoreCase(strType)
				&& !pgV3Constants.TYPE_INTERMEDIATE_PRODUCT_PART.equalsIgnoreCase(strType)
				&& !pgV3Constants.POLICY_PGPARALLELCLONEPRODUCTDATAPART.equalsIgnoreCase(strPolicy))
		{
			isPartAllowed=true;
		}
		return isPartAllowed;
	}
	
//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @param strType
	 * @return
	 */
	private boolean isAllowTOAddAlternate(String strType) {
		boolean isAllowToAddAlternate=false;
		if(pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART.equalsIgnoreCase(strType)|| pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strType)) {
			 isAllowToAddAlternate=true;
		}
		return isAllowToAddAlternate;
	}
	
//Modified/Added DSM(Sogeti) 18x.6 Refactor
	/**
	 * @param productPart
	 * @param iSub
	 * @param strName
	 * @param strRev
	 * @param strID
	 * @param strLevel
	 * @return
	 */
	private HashMap addBOMsSubstitute(ProductHelper productPart,int iSub,String strName, String strRev, String strID, String strLevel) {
		HashMap mapTemp = new HashMap<>();
		mapTemp.put("name", productPart.getName().get(iSub));
		mapTemp.put("id[connection]", productPart.getRelID().get(iSub));
		mapTemp.put("relationship", "EBOM Substitute");
		mapTemp.put("id", productPart.getId().get(iSub));
		mapTemp.put("type", productPart.getType().get(iSub));
		mapTemp.put("IntermediateName", strName+"."+strRev);
		mapTemp.put("IntermediateID", strID);
		mapTemp.put("revision", productPart.getRevision().get(iSub));
		mapTemp.put("current", productPart.getState().get(iSub));
		mapTemp.put("level", strLevel);
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST, productPart.getIsFCExist().get(iSub));
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST, productPart.getIsArtExist().get(iSub));
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST, productPart.getIsNonReleasedArtExist().get(iSub));
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGINCLUDEINCOS, productPart.getIncludeInCOS().get(iSub));
		//Modified For defect 1790 starts by V4-2013x.4
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, productPart.getTitle().get(iSub));
		//Modified For defect 1790 ends by V4-2013x.4
		//Added by DSM(Sogeti) 2015x.4.1 for COS Defect Id. 14780 on 27-Oct-2017 -- START
		mapTemp.put(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE,productPart.getSapType().get(iSub));
		//Added by DSM(Sogeti) 2015x.4.1 for COS Defect Id. 14780 on 27-Oct-2017 -- END
		//Added by DSM(Sogeti) 2018x.3 for COS Defect Id 32460 -- START
		mapTemp.put("attribute["+pgV3Constants.ATTRIBUTE_PGASSEMBLYTYPE+"]",productPart.getSubAssemblyType().get(iSub));
		//Added by DSM(Sogeti) 2018x.3 for COS Defect Id 32460 -- END
		return mapTemp;
	}

//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Starts
	/**
	 * @param context
	 * @param dObjChild
	 * @param slObjectSelect
	 * @return
	 * @throws Exception
	 */
	public Map checkAlternateReleaseArtExists(Context context,DomainObject dObjChild, StringList slObjectSelect) throws Exception {
		Map returnMap = new HashMap();
		String blArtExist  = "false";
		String blFCExist   = "false";
		String blNonReleasedArtExist   = "false";
		
		StringList slIsExistSelectable=new StringList(3);
		slIsExistSelectable.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
		slIsExistSelectable.addElement(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
		slIsExistSelectable.addElement(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
		
		StringList slTypes=new StringList(3);
		slTypes.addElement(pgV3Constants.TYPE_PACKAGINGMATERIALPART);
		slTypes.addElement(pgV3Constants.TYPE_PACKAGINGASSEMBLYPART);
		slTypes.addElement(pgV3Constants.TYPE_FABRICATEDPART);
		
	
		MapList mlAlternates = getAlternateMapList(context, dObjChild, slObjectSelect, slTypes);		
		
		if(mlAlternates != null && !mlAlternates.isEmpty()) {
			int iAlternateSize=mlAlternates.size();
			StringList slArtPOA = new StringList(2);
			slArtPOA.addElement("from["+REL_POAToCOUNTRY+"]");
			slArtPOA.addElement(DomainConstants.SELECT_CURRENT);
			String strArtState;
			Map mapAlt;
			MapList mlArtObject;
			Map mapArt;
			String strCalculateCOS;
			String strCOSWIP;
			String strIsFCExist;
			String strIsARTExist;
			String strIsNonReleaseARTExist;
			Map mapAttrMap;
			int iArtObjectSize=0;
			for(int j=0; j<iAlternateSize; j++) {
				if("false".equalsIgnoreCase(blArtExist) || "false".equalsIgnoreCase(blFCExist) || "false".equalsIgnoreCase(blNonReleasedArtExist)) {
					mapAlt 	= (Map) mlAlternates.get(j);
					if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase((String)mapAlt.get(DomainConstants.SELECT_CURRENT))){
						dObjChild = DomainObject.newInstance(context, (String)mapAlt.get(DomainConstants.SELECT_ID));
						if(pgV3Constants.TYPE_PACKAGINGMATERIALPART.equalsIgnoreCase((String)mapAlt.get(DomainConstants.SELECT_TYPE))) {
							mlArtObject = dObjChild.getRelatedObjects(context, //context user
															DomainConstants.RELATIONSHIP_PART_SPECIFICATION,//relationship
															pgV3Constants.TYPE_PGARTWORK+","+pgV3Constants.TYPE_POA,//types
															slArtPOA, //object select
															new StringList(), //rel select
															false,//get to
															true, //get from
															(short)1, //level
															"", //object where
															"", //rel where
															0);//limit

							if(null != mlArtObject && !mlArtObject.isEmpty())
							{
								iArtObjectSize=mlArtObject.size();
								for(int iArt = 0; iArt < iArtObjectSize; iArt++)
								{
									mapArt = (Map)mlArtObject.get(iArt);
									//Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Starts
									strArtState = (String)mapArt.get(DomainConstants.SELECT_CURRENT);
							
									if(pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strArtState) && "true".equalsIgnoreCase((String)mapArt.get("from["+REL_POAToCOUNTRY+"]")))
									{
										blArtExist = "true";
									}
									if(!pgV3Constants.STATE_RELEASE.equalsIgnoreCase(strArtState) && !pgV3Constants.STATE_OBSOLETE.equalsIgnoreCase(strArtState)) {
										blNonReleasedArtExist = "true";
									}
								  //Modified by DSM(Sogeti) 2018x.1.1for COS Defect Id.26966 Ends
								}
							}
						} else {
							strCalculateCOS = (String)mapAlt.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSCALCULATE + "]");
							strCOSWIP = (String)mapAlt.get("attribute[" + pgV3Constants.ATTRIBUTE_PGCOSWIP + "]");
							
							//If pgCOSCalculate is false, means there is no need to calculate isFCExist and isArtExist again. Use as it is.
						
							if(null != strCalculateCOS && "false".equalsIgnoreCase(strCalculateCOS) && "false".equalsIgnoreCase(strCOSWIP))
							{
								strIsFCExist = (String)mapAlt.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST);
								strIsARTExist = (String)mapAlt.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST);
								strIsNonReleaseARTExist = (String)mapAlt.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST);
								if(null != strIsFCExist && "true".equalsIgnoreCase(strIsFCExist))
								{
									blFCExist = "true";
								}
								if(null != strIsARTExist && "true".equalsIgnoreCase(strIsARTExist))
								{
									blArtExist = "true";
								}
								if(null != strIsNonReleaseARTExist && "true".equalsIgnoreCase(strIsNonReleaseARTExist))
								{
									blNonReleasedArtExist = "true";
								}
							}
							else
							{
								//Calculate isFCExist and isArtExist as there is some change in connections
								updateFCOrArtExist(context,(String)mapAlt.get(DomainConstants.SELECT_ID));
								mapAttrMap = dObjChild.getInfo(context, slIsExistSelectable);
								if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISFCEXIST)))
								{
									blFCExist = "true";
								}
								if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_ISARTEXIST)))
								{
									blArtExist = "true";
								}
								if(null != (String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST) && "true".equalsIgnoreCase((String)mapAttrMap.get(pgV3Constants.SELECT_ATTRIBUTE_NON_RELEASED_ART_EXIST)))
								{
									blNonReleasedArtExist = "true";
								}
							}
						}
					}
				}
			}
		}
		if("true".equalsIgnoreCase(blFCExist))
			returnMap.put("ISFCEXIST", blFCExist);
		if("true".equalsIgnoreCase(blArtExist))
			returnMap.put("ISARTEXIST", blArtExist);
		if("true".equalsIgnoreCase(blNonReleasedArtExist))
			returnMap.put("ISNONRELEASEARTEXIST", blNonReleasedArtExist);
		return returnMap;
		
	}
	//Modified by DSM(Sogeti) 2018x.6 for COS (Req ID- 36360,36361,36359,36362,36363,36374,36366,36357,37203,37202,37204,36358,36358,36356,36376,36355,36211) - Ends	

	/**
	 * Method to get Alternate MapList - Added for Upgrade 2022x.00 Defect 49210 - Upgrade 2022x.0 - PU22X-693 - BOM Recursion Issue
	 * @param context
	 * @param dObjChild
	 * @param slObjectSelect
	 * @param slTypes
	 * @return
	 * @throws FrameworkException
	 */
	public MapList getAlternateMapList(Context context, DomainObject dObjChild, StringList slObjectSelect,	StringList slTypes) throws FrameworkException
	{
		MapList mlAlternates = new MapList();
		
		StringList slAlternates = dObjChild.getInfoList(context, "from["+pgV3Constants.RELATIONSHIP_ALTERNATE+"].to.id");		
		
		if(null != slAlternates && !slAlternates.isEmpty())
		{
			MapList mlLocalAlternates = DomainObject.getInfo(context, slAlternates.toArray(new String[slAlternates.size()]), slObjectSelect);	
			
			Map mapAlternate;
			String sAlternateType;
			
			for(Object objMap : mlLocalAlternates)
			{
				mapAlternate = (Map)objMap;
				sAlternateType = (String)mapAlternate.get(DomainConstants.SELECT_TYPE);			
				if(UIUtil.isNotNullAndNotEmpty(sAlternateType) && slTypes.contains(sAlternateType))
				{
					mlAlternates.add(mapAlternate);
				}			
			}
		}
		
		return mlAlternates;
	}


	//Added by DSM(Sogeti) 2018x.6 for Market Registartion Rollup Marking

	/**
	 * @param Map - Product Map
	 * @param Map - Attribute Map
	 * @return
	 */
	public void updateRollupMarking(Map mpProductPart, Map mpAttributeMap){
		String strType = (String) mpProductPart.get(DomainConstants.SELECT_TYPE);
		if(UIUtil.isNotNullAndNotEmpty(strType) && pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(strType)){
			String strFPPEvent = (String) mpProductPart.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
			StringBuffer sbFPPRollupEvent = new StringBuffer();
			if(UIUtil.isNotNullAndNotEmpty(strFPPEvent) && !strFPPEvent.contains(pgV3Constants.EVENT_MARKET_REGISTRATION)){
				sbFPPRollupEvent.append(strFPPEvent).append(pgV3Constants.SYMBOL_COMMA).append(pgV3Constants.EVENT_MARKET_REGISTRATION);
			}else if(UIUtil.isNullOrEmpty(strFPPEvent)) {
				sbFPPRollupEvent.append(pgV3Constants.EVENT_MARKET_REGISTRATION);
			}
			String strEvent = sbFPPRollupEvent.toString();
			if(UIUtil.isNotNullAndNotEmpty(strEvent)) {
				mpAttributeMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_TRUE);
				mpAttributeMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, strEvent);
			}	
		}else if(UIUtil.isNotNullAndNotEmpty(strType) && (pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(strType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(strType)
				|| pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(strType) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(strType))){
			mpAttributeMap.put(pgV3Constants.ATTRIBUTE_PGREGISTRATIONROLLUPFLAG,pgV3Constants.KEY_TRUE);
		}
	}

}
