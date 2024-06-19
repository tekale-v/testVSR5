/******************************************************************************
#############################################################
#
#   Copyright (c) 2015 Procter & Gamble, Inc.  All Rights Reserved.
#   This program contains proprietary and trade secret information of
#   Procter & Gamble, Inc.  Copyright notice is precautionary only and does not
#   evidence any actual or intended publication of such program.
#
#############################################################
******************************************************************************/
/******************************************************************************
Program:        GCASCodePuller.java
Description:    Reserves Material Numbers in SAP for use in PLM as GCAS Codes 
Assumptions:    None   
Created:        Jan 5, 2015 Andrew Fritz
Modified:
******************************************************************************/

package com.pg.plm.integ;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process reads from a properties file on Linux server - Start
import com.matrixone.apps.domain.util.FrameworkUtil;
import matrix.util.StringList;
//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process reads from a properties file on Linux server - End
import java.util.Properties;

import com.sap.mw.jco.*;


/**
 * @author AFZ
 *
 */
public class GCASCodePuller {
	
    private static JCO.Client client;
    private static final String GCAS_FUNCTION = "ZT_RFC_GMDB_GCAS_RESERVEWR";
    private static String sGCASMagLogDir;
    public static StringBuffer Log = new StringBuffer();  
	

    /**
     * Modified by DSM(DS) 2015x.4 - ALM-10943 
	 * Static Method pullGCASList will use connect parameters from ConfigurationAdmin Object to connect to SAP and pull iTotalNumber of GCAS Codes
	 * Configuration Admin object used to store SAP Connection parameters - pgConfigurationAdmin	pgGCASSAPPropertiesConfig	-
     * @param iTotalNumber
     * @param strPropertiesFilePath
     * @return Collection (List of String values)
     * @throws Exception
     */
    public static Collection<String> pullGCASList(int iTotalNumber, String strSAPProperties) throws Exception 
	{
		Collection<String> cResult = null;

		/******************************************************************************************************\
		 *  Initialize parameters with default values                                                          *
	     \******************************************************************************************************/
		String sSAP_Client = "104";
		String sSAP_Userid = "MMPLM1PLM";
		String sSAP_Password = "ATENAS09";
		String sSAP_Language = "EN";
		String sSAP_SystemNumber = "06";
		String sSAP_HostName = "g13.na.pg.com";

		// Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): START
		String[] rfcConfigParameters = new String[]{};
		boolean useDefaultConnection = false;
		boolean isConnected = false;
		// Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): END

		//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - Start		
		//String sPropertiesFile = "/var/opt/gplm/config/plmI/sapconnection.properties";        
		//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - End
		int iGCASNumber = 1;
		if (iTotalNumber > 0)
			iGCASNumber = iTotalNumber;
		//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - Start 
		if (null != strSAPProperties && !"null".equals(strSAPProperties) && !"".equals(strSAPProperties)) 
		{	
			/*
			//checking whether it is passed as SAP properties or file location
			sPropertiesFile = strSAPProperties;

			Log.append("---------------------------------------------------------------\n");
			Log.append("GCAS Magazine Loader\n");
			Log.append("Properties File:" + sPropertiesFile + "\n");                        
			Log.append("---------------------------------------------------------------\n");

			// read the properties from file
			java.io.File from_File = new java.io.File(sPropertiesFile);
			FileInputStream fis = new FileInputStream(from_File);      
			Properties prop = new Properties();      
			prop.load(fis);      


			sSAP_Client  = prop.getProperty("SAP_client");
			sSAP_Userid  = prop.getProperty("gcas_userid");
			sSAP_Password  = prop.getProperty("gcas_password");
			sSAP_Language  = prop.getProperty("language");
			sSAP_HostName  = prop.getProperty("host_name");
			sSAP_SystemNumber  = prop.getProperty("system_number"); 
			sGCASMagLogDir = prop.getProperty("log_dir"); 
			 */
			//Retrieved from the config object

			Log.append("---------------------------------------------------------------\n");
			Log.append("GCAS Magazine Loader\n");
			//Log.append("Properties File:" + sPropertiesFile + "\n"); 
			Log.append("Reading SAP Connection details from Config Admin object...\n");
			Log.append("---------------------------------------------------------------\n");

			/*
			// read the properties from file
			java.io.File from_File = new java.io.File(sPropertiesFile);
			FileInputStream fis = new FileInputStream(from_File);      
			Properties prop = new Properties();      
			prop.load(fis);      
			 */

			StringList sapPropList = FrameworkUtil.split(strSAPProperties, "|");
			// Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): START
			sSAP_HostName  = sapPropList.get(0);        // SAP Host Name
			sSAP_Client  = sapPropList.get(1);          // SAP Client Number 
			sSAP_SystemNumber  = sapPropList.get(2);    // SAP System Number 
			sGCASMagLogDir = sapPropList.get(8);        // Log Dir
			rfcConfigParameters = sapPropList.toArray(new String[sapPropList.size()]);
			// Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): END
		} else {
			Log.append("---------------------------------------------------------------\n");
			Log.append("GCAS Magazine Loader\n");
			Log.append("Using Default SAP Connection details... \n");
			Log.append("---------------------------------------------------------------\n");

			//Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): START
			useDefaultConnection = true;
			//Added by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): END
		}
		//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - End

		if (null == sGCASMagLogDir || sGCASMagLogDir.isEmpty())
			sGCASMagLogDir = "~/";

		// Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): START
		if (null != rfcConfigParameters && rfcConfigParameters.length > 0) {
			com.pg.dsm.sapview.services.SAPRFCClient rfcClient = new com.pg.dsm.sapview.services.SAPRFCClient.Connector(rfcConfigParameters).connect();
			if (rfcClient.isConnected()) {
				client = rfcClient.getClient();
				if(client.isAlive()) {
					isConnected = true;
					Log.append("\nConnected to host (using RFC):" + sSAP_HostName + ";  SystemNumber: " + sSAP_SystemNumber + " \n");
				} else {
					Log.append("\n SAP Connection is not alive \n");
				}
			} else {
				Log.append("\nFailed to connect to SAP RFC Client - error: "+rfcClient.getErrorMessage()+" - Make default connection");
				useDefaultConnection = true;
			}
		} else {
			Log.append("\nSAP RFC Connection Parameters are incorrect on config object (pgConfigurationAdmin pgGCASSAPPropertiesConfig -) - Make default connection");
			useDefaultConnection = true;
		}

		if(useDefaultConnection) {
			// Create a client connection to R/3 system
			client = JCO.createClient(sSAP_Client, sSAP_Userid, sSAP_Password, sSAP_Language, sSAP_HostName, sSAP_SystemNumber);
			client.connect();
			if(client.isAlive()) {
				isConnected = true;
				Log.append("\nConnected to host:" + sSAP_HostName + "; User:" + sSAP_Userid + ";  SystemNumber: " + sSAP_SystemNumber + " \n");
			} else {
				Log.append("\n SAP Connection is not alive \n");
			}
		} 

		if(isConnected) {
			// Print the version of the underlying JCO library
			Log.append("\nVersion of the JCO-library:" + JCO.getMiddlewareVersion());
			try {
				cResult = makeGCASListWR(iGCASNumber, client);
				if (null != cResult && cResult.isEmpty())
					Log.append("\nEmpty List returned from makeGCASList");
			} catch (Exception e) {
				Log.append("\n" + e.getLocalizedMessage());
				throw e;
			} finally {
				WriteFile(sGCASMagLogDir + "GCASMagazine.log", Log);
			}
		}
		// Modified by (DSM Sogeti) for (2018x.6 Apr CW 2022) SNC-RFC Requirements ID (42702,42703,42704): END
		return cResult;

	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        // Initialize the parameters to connect to SAP R/3
		sGCASMagLogDir = "~/";
		//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - Start 
        //String sPropertiesFile = "/var/opt/gplm/config/plmI/sapconnection.properties";
		//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - End
        int iGCASNumber =  1;

        try {
        	if (null != args && args.length > 0) 
        	{
				//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - Start 
        		/*
				sPropertiesFile = args[0];
        		if (args.length > 1)
        			try {
        				iGCASNumber = Integer.parseInt(args[1]);
        			} catch (Throwable ex) {
        				
        			}
				*/
				iGCASNumber = Integer.parseInt(args[0]);
				//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process - Moving SAP connection details from a properties file to Configuration Admin object - End
        	}
        	//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process reads from a properties file on Linux server - Start
			//Collection<String> GCASList = pullGCASList(iGCASNumber,sPropertiesFile);
        	Collection<String> GCASList = pullGCASList(iGCASNumber,null);
        	//DSM(DS) 2015x.4 ALM-10943: Automatic GCAS Load process reads from a properties file on Linux server - End
			if (null != GCASList && GCASList.isEmpty())
				Log.append("\nEmpty List returned from makeGCASList");
			else if (null != GCASList) {
				Iterator<String> listItr = GCASList.iterator();
				Log.append("\nCODES: ");				
				while (listItr.hasNext()) {
					Log.append("\t " + (String)listItr.next());
				}				
			}
		} catch (Exception e) {
			Log.append("\n" + e.getLocalizedMessage());
		} finally {
	        WriteFile(sGCASMagLogDir + "TestGCASMagazine.log", Log);
		}
	}
	
	/**
	 * 
	 * @param iTotalNumber
	 * @param client
	 * @return Collection
	 * @throws Exception
	 */
    protected static Collection<String> makeGCASListWR(int iTotalNumber, JCO.Client client) throws Exception {

        // method to create a List of GCAS numbers 
        List<String> GCASNumbers = new LinkedList<String>();
        try
        {
        	JCO.Repository mRepository;
        	mRepository = new JCO.Repository("PnGCASsapClient", client);
        	
        	JCO.Function func;
    		
    		IFunctionTemplate ft =
    		mRepository.getFunctionTemplate(GCAS_FUNCTION);
    		
    		func = ft.getFunction();
    		    		
        	/************************************************************************************************\
        	 *  Provide the Input parameter IW_NUM, setting it to the given value                           *
        	\************************************************************************************************/
    		func.getImportParameterList().setValue(iTotalNumber, "IW_NUM");
    		
    		client.execute(func);    		
    		
    		JCO.Table codes = null;
    		
        	/************************************************************************************************\
        	 *  Retrieve the Output parameter table ET_GCAS                                                 *
        	\************************************************************************************************/
    		try {
    			codes =	func.getExportParameterList().getTable("ET_GCAS");
    		} catch (Throwable tEx) {
    			Log.append("\ngetTable Exception: " + tEx.getLocalizedMessage());
    		}
    		
    		String sGCAS_From_SAP = "";
    		String sReturnCode = "";
    		String sMessage = "";
             
        	/************************************************************************************************\
        	 *  Loop over the rows in the table, building the collection to be returned                     *
        	\************************************************************************************************/
    		for (int i = 0; i < codes.getNumRows(); i++, codes.nextRow()) {
                sGCAS_From_SAP = removeLeadingZeros(codes.getString("ZMATNR"));
                sReturnCode = codes.getString("ZRETCODE");
                sMessage = codes.getString("ZMESSAGE");

               if (sGCAS_From_SAP.length() != 8)
                {
                         Log.append("\nError: GCAS has more than 8 char in length...");
                         Log.append(sGCAS_From_SAP).append("\n");
                }
                if (!sReturnCode.equals("I0000"))
                {
                        Log.append("\nError: ").append(sReturnCode).append(" ");
                        Log.append(sMessage).append("\n");
                } else {
                	GCASNumbers.add(sGCAS_From_SAP);
                }
    		}
 
    		Collections.sort(GCASNumbers);
            return GCASNumbers;
        } catch (Exception e) 
        {
                Log.append("\nmakeGCASList: " + e.toString() + "\n");
        }
        return GCASNumbers;
}

    public static String removeLeadingZeros(String str)
 {
         if (str == null)
         {
                 return null;
         }
        
         char[] chars = str.toCharArray();
         int index = 0;
         for (; index < str.length(); index++)
         {
                 if (chars[index] != '0')
                 {
                        break;
                 }
         }
         return (index == 0) ? str : str.substring(index);
 }


public static boolean WriteFile(String fileNameWithPath, StringBuffer sFileContents)
{  
	try
	{
                java.io.File f = new java.io.File(fileNameWithPath);
                String newFile = "";
                if(f.exists())
                {
                        // Destination directory
                        newFile = fileNameWithPath + "." + GetDate("dd-MMM-yy-hh-mm-ss");

                        java.io.File nf = new java.io.File(newFile);
                        // Move file to new directory
                        boolean success = f.renameTo(nf);
                        if(success) 
                                Log.append("\nMoved old log file to new destination:" + newFile + "\n");
                        else                                         
                                Log.append("\nError occured in moving file " + fileNameWithPath + " to " + newFile + "\n");
                }
                Log.append("\nWriting new log file: " + fileNameWithPath);
                PrintWriter fileOut;
                FileOutputStream fos = new FileOutputStream(fileNameWithPath, false);
                FileWriter os = new FileWriter(fos.getFD());
                fileOut = new PrintWriter(os);
                fileOut.println(sFileContents.toString());
                fileOut.flush(); 
                fileOut.close();
                
                fos.close();
                
   		return true;
   	}catch (IOException e){
   		System.out.println(e);
   	}
	return false;
}


public static String GetDate(String dateformat)
{  

        String sCurTime ="";
	try
	{
                if(dateformat.equals("")) dateformat="EEE MMM d, yyyy hh:mm:ss aa zzz"; 
                GregorianCalendar gcal = new GregorianCalendar();
                Date dNow = gcal.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat(dateformat);
                return (formatter.format(dNow));
                
   	}catch (Exception e){
   		System.out.println(e);
   	}
        return sCurTime;
}


}
