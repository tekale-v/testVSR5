package com.pg.designtools.integrations.datahandlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.SimulationDocument;
import com.pg.designtools.util.IPManagement;
import com.pg.designtools.datamanagement.EngineeringItem;
import com.pg.designtools.datamanagement.MasterPackagingAssemblyPart;
import com.pg.designtools.datamanagement.MasterPackagingMaterialPart;
import com.pg.designtools.datamanagement.MasterProductPart;
import com.pg.designtools.datamanagement.MasterRawMaterialPart;
import com.pg.designtools.datamanagement.PKGVPMPart;
import com.pg.designtools.util.InterfaceManagement;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class DataRectificationHandler {
	
	 EngineeringItem engItem = new EngineeringItem();
	
    public DataRectificationHandler(Context context) {
		PRSPContext.set(context);
	}
  	
    /**
	 * Create the Marker object (VPLMDataMigration)
	 * @param context
	 * @param Level which would be part of the object
	 * @return Map
	 * @throws MatrixException
	 */
	public Map<String,String> createMarker(Context context,String strLevel) throws MatrixException  {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of createMarker method");
		Map<String,String> mpMarkerObject=new HashMap<>();
		
		BusinessObject boObject=new BusinessObject(DataConstants.TYPE_VPLM_DATA_MIGRATION, DataConstants.CONST_SERVICE_NAME+strLevel, "---",DataConstants.VAULT_VPLM);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> boObject exists::"+boObject.exists(context));
		
		if(!boObject.exists(context)) {
			boObject.create(context,DataConstants.POLICY_VPLM_DATA_MIGRATION);
			boObject.setAttributeValue(context,DataConstants.MX_MIGRATION_STATUS_ATTR, DataConstants.CONST_NO_STATUS);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Object "+DataConstants.TYPE_VPLM_DATA_MIGRATION+DataConstants.CONST_SERVICE_NAME+" "+strLevel+ " --- created successfully");
			
			mpMarkerObject.put(DomainConstants.SELECT_ID,boObject.getObjectId(context));
			mpMarkerObject.put(DataConstants.MX_MIGRATION_STATUS_SELECTABLE, DataConstants.CONST_NO_STATUS);
		}
		else {
			String strObjectId=boObject.getObjectId(context);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>strObjectId::"+strObjectId);
			StringList slSelects=new StringList(2);
			slSelects.add(DomainConstants.SELECT_ID);
			slSelects.add(DataConstants.MX_MIGRATION_STATUS_SELECTABLE);
			
			DomainObject doObject=DomainObject.newInstance(context,strObjectId);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>doObject::"+doObject);
			mpMarkerObject=doObject.getInfo(context, slSelects);
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>mpMarkerObject::"+mpMarkerObject);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> END of createMarker method");
		return mpMarkerObject;
	}
	
	/**
	 * Method to find the marker object
	 * @param context
	 * @param strLevel
	 * @return Map
	 * @throws FrameworkException
	 */
	public Map<String,String> findMarkerObject(Context context,String strLevel) throws FrameworkException {
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START of findMarkerObject method");
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> strLevel::"+strLevel);
			
			StringList slSelects=new StringList(2);
			slSelects.add(DomainConstants.SELECT_ID);
			slSelects.add(DataConstants.MX_MIGRATION_STATUS_SELECTABLE);
			Map<String,String> mpObject=new HashMap<>();
			
			MapList mlObject=DomainObject.findObjects(context,
					DataConstants.TYPE_VPLM_DATA_MIGRATION,									//typePattern
					DataConstants.CONST_SERVICE_NAME+strLevel,								//namePattern
					"---",																					//revPattern
					DomainConstants.QUERY_WILDCARD,							//ownerPattern
					DomainConstants.QUERY_WILDCARD,         					//vaultPattern
					null,                                   												//where expression
					false,                                   												//expandType
					slSelects);                            												 //object selectables
	
			if(!mlObject.isEmpty()) {
				mpObject=(Map<String, String>) mlObject.get(0);
			}
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> mpObject::"+mpObject);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> END findMarkerObject method");
			return mpObject;
	}
	
	/**
	 *  Update the attribute VPLMsys/MigrationStatus
	 * @param context
	 * @param Map
	 * @param String strMode
	 * @throws MatrixException
	 */
	public void setMigrationStatus(Context context,Map<String,String> mpObject,String strMode) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START setMigrationStatus method");
	
		String strObjectId=mpObject.get(DomainConstants.SELECT_ID);
		String strMigrationStatus=mpObject.get(DataConstants.MX_MIGRATION_STATUS_SELECTABLE);
		
		HashMap<String,String> hmAttr=new HashMap<>();
		if(DataConstants.CONST_SCAN.equalsIgnoreCase(strMode) && DataConstants.CONST_NO_STATUS.equalsIgnoreCase(strMigrationStatus)) {
			hmAttr.put(DataConstants.MX_MIGRATION_STATUS_ATTR, DataConstants.CONST_NOT_STARTED);
		}
		else if(DataConstants.CONST_GENERATE.equalsIgnoreCase(strMode) && DataConstants.CONST_NOT_STARTED.equalsIgnoreCase(strMigrationStatus)) {
			hmAttr.put(DataConstants.MX_MIGRATION_STATUS_ATTR, DataConstants.CONST_ON_GOING);
		}
		else if(DataConstants.CONST_FIX.equalsIgnoreCase(strMode) && DataConstants.CONST_ON_GOING.equalsIgnoreCase(strMigrationStatus)) {
			hmAttr.put(DataConstants.MX_MIGRATION_STATUS_ATTR, DataConstants.CONST_FINISHED);
		}
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> hmAttr::"+hmAttr);
		if(!hmAttr.isEmpty() && UIUtil.isNotNullAndNotEmpty(strObjectId)) {
			DomainObject doObject=DomainObject.newInstance(context,strObjectId);
			doObject.setAttributeValues(context, hmAttr);
			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Attributes set successfully on "+strObjectId);
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> END setMigrationStatus method");
	}

	/**
	 * Method to update the migration interface attributes on the VPMReference object
	 * @param context
	 * @param strObjectId
	 * @param strMode
	 * @param strLevel
	 * @throws FrameworkException
	 */
	public void updateMigrationAttributes(Context context,String strObjectId,String strMode,String strLevel) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> START updateMigrationAttributes method");
		DomainObject doObject=DomainObject.newInstance(context,strObjectId);
		
		HashMap<String,String> hmAttr=new HashMap<>();
		if(DataConstants.CONST_GENERATE.equalsIgnoreCase(strMode)) {
			hmAttr.put(DataConstants.ATTRIBUTE_V_MIG_STEP, DataConstants.CONSTANT_ONE);
			hmAttr.put(DataConstants.ATTRIBUTE_V_MIG_TYPE, DataConstants.CONST_SERVICE_NAME+strLevel);
		}
		else if(DataConstants.CONST_FIX.equalsIgnoreCase(strMode)) {
			hmAttr.put(DataConstants.ATTRIBUTE_V_MIG_STEP, DataConstants.CONSTANT_TWO);
		}
		
		hmAttr.put(DataConstants.ATTRIBUTE_PG_MIG_DATE,getCurrentDate());
		
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> hmAttr::"+hmAttr);
		doObject.setAttributeValues(context, hmAttr);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> Attributes set successfully on "+strObjectId);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>> END updateMigrationAttributes method");
	}
	
	/**
	 * Method to get the current date in matrix format
	 * @param context
	 * @return String
	 */
	private String getCurrentDate()
    {              
	    SimpleDateFormat sdf = new SimpleDateFormat( eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);			
		return sdf.format(new Date());                
    }

/**
	 * Method to write the information of the Sim Doc objects to CSV File
	 * @param context
	 * @param mlData
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writeSimDocDataInCSVFile(Context context,MapList mlData,String strLevel) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeSimDocDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_SIMDOC_SERVICE_NAME+strLevel+".csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);

		String strHeader="Type\tName\tRevision\tObjectId\tOwner\tCurrent\tOriginated\tModified\tPolicy\tProcess Access Classification\tIP Control Class";
		String strExpectedDataInInputFile="Name\tRevision\tObjectId";
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){      

			//header row
			
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");

			 Map<String,String> mpData;
			 StringBuilder sbRowData;
			 String strIPControlClass="";

			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map<String, String>) mlData.get(i);
				 sbRowData.append(mpData.get(DomainConstants.SELECT_TYPE)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_POLICY)).append("\t");
				 sbRowData.append(mpData.get("attribute["+DataConstants.ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION+"]")).append("\t");

				 strIPControlClass=mpData.get("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");

				 if(UIUtil.isNullOrEmpty(strIPControlClass))
					 strIPControlClass="";

				 sbRowData.append(strIPControlClass);

				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeSimDocDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_SIMDOC_SERVICE_NAME+strLevel);
	}
	
	/**
	 * Method which would be invoked for generate mode for Sim Docs
	 * @param context
	 * @param Map mpSimDocInfo
	 * @throws Exception 
	 */
	public void generateModeLogicForSimDocs(Context context, Map<String,StringList> mpSimDocInfo) throws Exception {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForSimDocs method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the Sim Docs already processed
		StringList slProcessedObjects=new StringList();
		
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
 
		 String strNameRev;
		 String strObjectId;
		 String strDefaultIPOfOwner;
		 String strIPClassName;
		 String strIPClassRelId;
		 Map mpSimDocObjInfo;
		 boolean bIsSimDocOwned=false;
		 boolean bIsIPClassConnected=false;
 
		 StringList slNameRevList=mpSimDocInfo.get("NameRevList");
		 StringList slObjectIdList=mpSimDocInfo.get("ObjectIdList");
		 
		 SimulationDocument simDoc=new SimulationDocument(context);
		 for(int i=0;i<slNameRevList.size();i++) {
			 
			 strNameRev=slNameRevList.get(i);
			 strObjectId=slObjectIdList.get(i);
			 VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>NameRev of Sim Doc object:::"+strNameRev+" objectId:::"+strObjectId);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strObjectId));
			 
			 if(!slProcessedObjects.contains(strObjectId)) {
							 
				 strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_SIMDOC_SERVICE_NAME+strNameRev+".mql";
				 outputFile = new File(strOutputFilePath);
			
				 try(FileWriter fileWriter =new FileWriter(outputFile)){      
		 
					 // Iterate for each Sim Doc
					 sbMqlCommands=new StringBuilder();
					 
					 //3. get all the info of particular Sim Doc
					mpSimDocObjInfo=simDoc.getInfoOfSimDoc(context,strObjectId);
					 
					 if(!mpSimDocObjInfo.isEmpty()) {
						 //4. verify whether the Sim Doc is connected with Simulation Content - Owned rel
						 bIsSimDocOwned=verifyForSimDocOwnedData(mpSimDocObjInfo);
						 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bIsSimDocOwned:::"+bIsSimDocOwned);
						 
						 //5. verify whether the Sim Doc is connected with IP Control class
						 bIsIPClassConnected=verifyForConnectedIPClassesToSimDoc(mpSimDocObjInfo);
						 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bIsIPClassConnected:::"+bIsIPClassConnected);
						 
						 //6. generate mql commands for modifying the policy
						 
						 if(!bIsSimDocOwned && !bIsIPClassConnected) {
							 //write an exception, since object is not Owned, and it has no IP Control class connected, 
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>EXCEPTION Sim Doc not Owned and no IP Classes connected to ::::"+strNameRev+" objectId::"+strObjectId);
							 
							 //get the default IP from user preference
							 strDefaultIPOfOwner=getDefaultIPFromOwner(context,mpSimDocObjInfo);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToConnectIPClassToSimDoc method");
							 commandToConnectIPClassToSimDoc(sbMqlCommands, mpSimDocObjInfo,strDefaultIPOfOwner);
						 }else if(bIsSimDocOwned) {
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToSetOwnedPolicyForSimDoc method");
							 commandToSetOwnedPolicyForSimDoc(sbMqlCommands, mpSimDocObjInfo);
						 }else {
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToSetDocPolicyForSimDoc method");
							 commandToSetDocPolicyForSimDoc(sbMqlCommands, mpSimDocObjInfo);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToUpdateStateForSimDoc method");
							 commandToUpdateStateForSimDoc(sbMqlCommands, mpSimDocObjInfo);
						 }
						 
						 if(bIsIPClassConnected) {
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Inside IP Class connected logic");
							 strIPClassName=(String)mpSimDocObjInfo.get("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
							 strIPClassRelId=(String)mpSimDocObjInfo.get("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].id");
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strIPClassName::"+strIPClassName);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strIPClassRelId::"+strIPClassRelId);
							 commandToDeleteRelationship(sbMqlCommands,strIPClassRelId);
							 commandToConnectIPClassToSimDoc(sbMqlCommands, mpSimDocObjInfo,strIPClassName);
						 }
						 
						 //add the name in the slProcessedNames list
						 slProcessedObjects.add(strObjectId);
				 
						 //add the mql commands to the output file
						 fileWriter.write(sbMqlCommands.toString());
						 System.out.println("\n Output mql File is created at:"+strOutputFilePath);
						 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
					 }
				 } //try loop
			 }
		 }//for loop
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForSimDocs method");
	}
		
	/**Method to generate mql command to delete the relationship
	 * @param sbMqlCommands
	 * @param strRelationshipId
	 */
	private void commandToDeleteRelationship(StringBuilder sbMqlCommands, String strRelationshipId) {
		if(UIUtil.isNotNullAndNotEmpty(strRelationshipId)) {
			sbMqlCommands.append("# Disconnect IP Class \n");
			sbMqlCommands.append("delete connection ");
			sbMqlCommands.append(strRelationshipId);
			sbMqlCommands.append(";");
			sbMqlCommands.append("\n");
		}
	}

	/**
	 * Method to generate mql command to connect IP class and set the Process Access Classfication attribute to TRUE
	 * @param sbMqlCommands
	 * @param mpSimDocObjInfo
	 * @param strDefaultIPOfOwner
	 */
	private void commandToConnectIPClassToSimDoc(StringBuilder sbMqlCommands, Map mpSimDocObjInfo,String strDefaultIPOfOwner) {
		sbMqlCommands.append("# Connect IP Class \n");
		
		if(UIUtil.isNullOrEmpty(strDefaultIPOfOwner))
			strDefaultIPOfOwner="Corporate Functions-R";
		
		sbMqlCommands.append("connect bus ");
		sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_TYPE)+"\" ");
		sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_NAME)+"\" ");
		sbMqlCommands.append(mpSimDocObjInfo.get(DomainConstants.SELECT_REVISION)+" ");
		sbMqlCommands.append("relationship ");
		sbMqlCommands.append("\""+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"\"");
		sbMqlCommands.append(" from ");
		sbMqlCommands.append("\""+DataConstants.TYPE_IP_CONTROL_CLASS+"\" ");
		sbMqlCommands.append("\""+strDefaultIPOfOwner+"\" -");
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
		
		sbMqlCommands.append("# Update attribute \n");
		sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
		sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_TYPE)+"\" ");
		sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_NAME)+"\" ");
		sbMqlCommands.append(mpSimDocObjInfo.get(DomainConstants.SELECT_REVISION)+" ");
		sbMqlCommands.append("\""+DataConstants.ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION+"\" ");
		sbMqlCommands.append(DataConstants.CONSTANT_TRUE.toUpperCase());
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
		
	}

	/**
	 * This method would get the default IP set 
	 * @param context
	 * @param mpSimDocObjInfo
	 * @return
	 * @throws Exception 
	 */
		 private String getDefaultIPFromOwner(Context context, Map mpSimDocObjInfo) throws Exception {
		
	    VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START getDefaultIPFromOwner method");
		String strType=(String) mpSimDocObjInfo.get(DomainConstants.SELECT_TYPE);
		String strObjectId=(String)mpSimDocObjInfo.get(DomainConstants.SELECT_ID);
		
		IPManagement ipMgmt=new IPManagement(context);
		Map mpPrefInfo=ipMgmt.getUserPreferenceFromObject(context, strType, strObjectId);
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>mpPrefInfo::"+mpPrefInfo);
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END getDefaultIPFromOwner method");
		return (String) mpPrefInfo.get("preferenceValue");
	}

		/**
			 * Method to verify whether Sim Doc is connected with Owned relationship
			 * @param mpSimDocObjInfo
			 */
			private boolean verifyForSimDocOwnedData(Map mpSimDocObjInfo) {
				boolean bIsOwnedSimDoc=false;
				
				String strPolicy=(String) mpSimDocObjInfo.get(DomainConstants.SELECT_POLICY);
				String strOwnedDoc=(String) mpSimDocObjInfo.get("to["+DataConstants.REL_SIMULATION_CONTENT_OWNED+"]");
				
					if(DataConstants.POLICY_SIMULATION_DOCUMENT_LEGACY.equalsIgnoreCase(strPolicy) && DataConstants.CONSTANT_TRUE.equalsIgnoreCase(strOwnedDoc)) {
						bIsOwnedSimDoc=true;
					}
					return bIsOwnedSimDoc;
			}
			
			/**
			 * Method to verify whether IP Class is connected to Sim Doc
			 * @param mpSimDocObjInfo
			 */
			private boolean verifyForConnectedIPClassesToSimDoc(Map mpSimDocObjInfo) {
				boolean bIsIPClassConnected=false;
				
				String strIPClassName=(String) mpSimDocObjInfo.get("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
				
				if(UIUtil.isNotNullAndNotEmpty(strIPClassName)) {
						bIsIPClassConnected=true;
				}
				return bIsIPClassConnected;
			}
			
			/**
			 * Method to generate mql command to set Simulation Document Owned policy
			 * @param sbMqlCommands
			 * @param mpSimDocObjInfo
			 */
			private void commandToSetOwnedPolicyForSimDoc(StringBuilder sbMqlCommands, Map mpSimDocObjInfo) {
				sbMqlCommands.append("# Modify policy \n");
					
				sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
				sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_TYPE)+"\" ");
				sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_NAME)+"\" ");
				sbMqlCommands.append(mpSimDocObjInfo.get(DomainConstants.SELECT_REVISION)+" ");
				sbMqlCommands.append(DomainConstants.SELECT_POLICY+" ");
				sbMqlCommands.append("\""+DataConstants.POLICY_SIMULATION_DOCUMENT_OWNED+"\" ");
				sbMqlCommands.append(";");
				sbMqlCommands.append("\n");
			}
			
			/**
			 * Method to generate mql command to set Simulation Document policy
			 * @param sbMqlCommands
			 * @param mpSimDocObjInfo
			 */
			private void commandToSetDocPolicyForSimDoc(StringBuilder sbMqlCommands, Map mpSimDocObjInfo) {
				sbMqlCommands.append("# Modify policy \n");
					
				sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
				sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_TYPE)+"\" ");
				sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_NAME)+"\" ");
				sbMqlCommands.append(mpSimDocObjInfo.get(DomainConstants.SELECT_REVISION)+" ");
				sbMqlCommands.append(DomainConstants.SELECT_POLICY+" ");
				sbMqlCommands.append("\""+DataConstants.POLICY_SIMULATION_DOCUMENT+"\"");
				sbMqlCommands.append(";");
				sbMqlCommands.append("\n");
			}
	
			/**
			 * Method to generate mql command to promote object with Simulation Document policy
			 * @param sbMqlCommands
			 * @param mpSimDocObjInfo
			 */
			private void commandToUpdateStateForSimDoc(StringBuilder sbMqlCommands, Map mpSimDocObjInfo) {
				sbMqlCommands.append("# Update state \n");
					
				sbMqlCommands.append("promote bus ");
				sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_TYPE)+"\" ");
				sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_NAME)+"\" ");
				sbMqlCommands.append(mpSimDocObjInfo.get(DomainConstants.SELECT_REVISION)+" ");
				sbMqlCommands.append(";");
				sbMqlCommands.append("\n");
			}
			
			/**
			 * Method to generate mql command to promote object with Simulation Document policy
			 * @param sbMqlCommands
			 * @param mpSimDocObjInfo
			 */
			private void commandToSetProcessAccessClassificationAttr(StringBuilder sbMqlCommands, Map mpSimDocObjInfo) {

				String strAttrValue=(String) mpSimDocObjInfo.get("attribute["+DataConstants.ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION+"]");
				
				if(UIUtil.isNotNullAndNotEmpty(strAttrValue) && DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strAttrValue)) {
					sbMqlCommands.append("# Update attribute \n");
					sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
					sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_TYPE)+"\" ");
					sbMqlCommands.append("\""+mpSimDocObjInfo.get(DomainConstants.SELECT_NAME)+"\" ");
					sbMqlCommands.append(mpSimDocObjInfo.get(DomainConstants.SELECT_REVISION)+" ");
					sbMqlCommands.append("\""+DataConstants.ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION+"\" ");
					sbMqlCommands.append(DataConstants.CONSTANT_TRUE.toUpperCase());
					sbMqlCommands.append(";");
					sbMqlCommands.append("\n");
				}
			}
	
	/**
	 * Method which would be invoked for generate mode
	 * @param context
	 * @param slNameList
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void generateModeLogic(Context context, StringList slNameList) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogic method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the VPMReference already processed
		StringList slProcessedNames=new StringList();
		
		//Create a StringBuffer which would hold all the mql commands for single revision family.
		StringBuilder sbMqlCommands;
 
		 String strName;
		 MapList mlRevisionObjects;
		 MapList mlDescription=new MapList();
		 boolean bIsHierarchyValidForAllRev=true;
		 boolean bValidDescription=true;
 
		 for(int i=0;i<slNameList.size();i++) {
			 
			 strName=slNameList.get(i);
			 VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>Name of VPMReference object:::"+strName);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is name already processed:::"+slProcessedNames.contains(strName));
			 
			 if(!slProcessedNames.contains(strName)) {
							 
				 strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_SERVICE_NAME+slNameList.get(i)+".mql";
				 outputFile = new File(strOutputFilePath);
			
				 try(FileWriter fileWriter =new FileWriter(outputFile)){      
		 
					 //3. Iterate for each VPMReference
					 sbMqlCommands=new StringBuilder();
					 mlDescription=new MapList();
					 
					 //get all the revisions of particular VPMReference
					 mlRevisionObjects=engItem.getAllRevisionsOfVPMReference(context, strName);
					 
					//set the particular Marker object status to ON_GOING
					 setMarkerObjectStatus(context,mlRevisionObjects);	
					
					 //5. Verify the hierarchy structure
					 bIsHierarchyValidForAllRev=verifyHierarchyForAllRevisions(context,mlRevisionObjects,sbMqlCommands);
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bIsHierarchyValidForAllRev:::"+bIsHierarchyValidForAllRev);
				 
					//add the migration interface on all revisions and update its attributes. 
					 addMigrationInterface(context,mlRevisionObjects);
								 
					 //6. generate mql commands for setting pngiDesignPart.pngCloneDerivedFrom to empty for all revisions
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToSetCloneDerivedFromEmpty method");
					 commandToSetCloneDerivedFromEmpty(sbMqlCommands,mlRevisionObjects);
		 
					 //7. verify whether description of all revisions is correct or not
					 bValidDescription=verifyDescription(mlRevisionObjects);
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bValidDescription:::"+bValidDescription);

					 // if the revision checks fail, then dont update the description and dont sync.
					 if(bIsHierarchyValidForAllRev) {
					 
						 if(!bValidDescription) {
							 //8. generate mql commands to update the description of all the revisions. It should be same as first revision
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToUpdateDescription method");
							 mlDescription=commandToUpdateDescription(sbMqlCommands,mlRevisionObjects);
							 
							 //9. generate mql command to invoke sync method
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking syncObject method");
						     syncObject(sbMqlCommands,mlRevisionObjects);
						 }
			
					     if(!mlDescription.isEmpty()) {
					    	 //10. Reset the description to original text as per the list
					    	 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToResetDescription method");
					    	 commandToResetDescription(sbMqlCommands,mlDescription);
					 
					    	 //11. sync back after description update
					    	 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking syncObject method");
					    	 syncObject(sbMqlCommands,mlRevisionObjects);
					     }
					 } 
					 //generate mql command to update the migration attributes
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToUpdateMigrationAttr method");
					 commandToUpdateMigrationAttr(sbMqlCommands, mlRevisionObjects);
					 
					 //add the name in the slProcessedNames list
					 slProcessedNames.add(strName);
				 
					 //add the mql commands to the output file
					 fileWriter.write(sbMqlCommands.toString());
					 System.out.println("\n Output mql File is created at:"+strOutputFilePath);
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
			 } //try loop
		
		 }//if loop
		}//for loop

		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogic method");
	}
	
	/**
	 * Method to navigate to the given directory and read the files present inside it
	 * @param context
	 * @param strInputFolderPath
	 * @throws IOException
	 * @throws FrameworkException
	 */
	public void traverseDirectoryAndReadFiles(Context context, String strInputFolderPath) throws IOException, FrameworkException {
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START traverseDirectoryAndReadFiles method");
		File directoryPath = new File(strInputFolderPath);
		CommonUtility commonUtilityObject = new CommonUtility();
		if(directoryPath.exists()) {
		      //List of all files and directories
		      File[] filesList = directoryPath.listFiles();
		      String sMQLCommand;
		      for(File file : filesList) {
		         VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>file.getAbsolutePath()::"+file.getAbsolutePath());
		         
		         if(file.getName().endsWith(".mql") || file.getName().endsWith(".MQL") ) {
		        	 sMQLCommand="run '"+file.getAbsolutePath()+"';";
		        	 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>MQL command:::"+sMQLCommand);
		        	 commonUtilityObject.executeMQLCommands(context,sMQLCommand);
		         }
		      }
		}else {
			throw new IOException("The folder "+strInputFolderPath+" does not exist");
		}
	      VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END traverseDirectoryAndReadFiles method");
	}
	
	
	
	
	
	
	
	/**
	 * Method to generate mql command to invoke the updateMigrationAttr method
	 * @param sbMqlCommands
	 * @param mlRevisionObjects
	 */
	private void commandToUpdateMigrationAttr(StringBuilder sbMqlCommands, MapList mlRevisionObjects) {
			Map<String,String>mpRevObj;
			String strToSideValue="";
			String strFromSideValue="";
			String strLevel="";
			
			sbMqlCommands.append("# Update migration attributes \n");
			for(int i=0;i<mlRevisionObjects.size();i++) {
				
				mpRevObj=(Map<String, String>) mlRevisionObjects.get(i);
				
				 strToSideValue=mpRevObj.get("to["+DataConstants.REL_VPM_INSTANCE+"]");
				 strFromSideValue=mpRevObj.get("from["+DataConstants.REL_VPM_INSTANCE+"]");
				 
				strLevel=engItem.getLevelForVPMReferenceObject(strToSideValue,strFromSideValue);
				 
				//mql command to invoke the updateMigrationAttr method
				sbMqlCommands.append("exec prog pgDTDataRectification -method updateMigrationAttr ");
				sbMqlCommands.append(mpRevObj.get(DomainConstants.SELECT_ID));
				sbMqlCommands.append(" "+DataConstants.CONST_FIX);
				sbMqlCommands.append(" "+strLevel+";");
				sbMqlCommands.append("\n");
			}
	}
	
	
	
	/**
	 * Method to verify the attribute values for DesignDomain, Enterprise type and Mfg Maturity Status
	 * @param mpRevObj
	 * @return boolean
	 */
	private boolean verifyEmptyAttributeValues(Context context,Map<String, String> mpRevObj,StringBuilder sbMqlCommands) {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START verifyEmptyAttributeValues method");
		boolean bValidValues=true;
		String strDesignDomain=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
		String strEnterpriseType=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
		String strMfgStatus=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS);
		
		if(UIUtil.isNullOrEmpty(strDesignDomain) || UIUtil.isNullOrEmpty(strMfgStatus)) {
			
			bValidValues=false;
		}
		if(UIUtil.isNullOrEmpty(strEnterpriseType)) {
			String strECPartType=mpRevObj.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strECPartType::"+strECPartType);
			if(UIUtil.isNotNullAndNotEmpty(strECPartType))
				commandToUpdateEnterpriseType(context,sbMqlCommands,mpRevObj);
			else
				bValidValues=false;
		}
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bValidValues::"+bValidValues);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END verifyEmptyAttributeValues method");
		return bValidValues;
	}
	
	/**
	 * Method to generate mql command to update the enterprise type
	 * @param context
	 * @param sbMqlCommands
	 * @param mpRevObj
	 */
	private void commandToUpdateEnterpriseType(Context context,StringBuilder sbMqlCommands, Map<String, String> mpRevObj) {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START commandToUpdateEnterpriseType method");
			String strEnterpriseTypeValue=getValueForEnterpriseTypeAttr(context,mpRevObj.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type"));
		
			sbMqlCommands.append("# Update the Enterprise Type attribute \n");
			sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
			sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_TYPE)+"\" ");
			sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_NAME)+"\" ");
			sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_REVISION)+"\" ");
			sbMqlCommands.append(DataConstants.ATTRIBUTE_PGENTERPRISETYPE+" ");
			sbMqlCommands.append("\""+strEnterpriseTypeValue+"\";");
			sbMqlCommands.append("\n");
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END commandToUpdateEnterpriseType method");
	}

	/**
	 * Method to get the value to be updated on Enterprise Type attribute
	 * @param context
	 * @param strECPartType
	 * @return String
	 */
	private String getValueForEnterpriseTypeAttr(Context context,String strECPartType) {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START getValueForEnterpriseTypeAttr method");
		String strEnterpriseTypeValue="";
		if(DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART.equals(strECPartType))
			strEnterpriseTypeValue=EnoviaResourceBundle.getProperty(context,DataConstants.FRAMEWORK_STRING_RESOURCE,context.getLocale(), "emxFramework.Type.pgMasterPackagingAssemblyPart");
		else if(DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART.equals(strECPartType))
			strEnterpriseTypeValue=EnoviaResourceBundle.getProperty(context,DataConstants.FRAMEWORK_STRING_RESOURCE,context.getLocale(), "emxFramework.Type.pgMasterPackagingMaterialPart");
		else if(DataConstants.TYPE_PG_MASTER_PRODUCT_PART.equals(strECPartType))
			strEnterpriseTypeValue=EnoviaResourceBundle.getProperty(context,DataConstants.FRAMEWORK_STRING_RESOURCE,context.getLocale(), "emxFramework.Type.pgMasterProductPart");
		else if(DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART.equals(strECPartType))
			strEnterpriseTypeValue=EnoviaResourceBundle.getProperty(context,DataConstants.FRAMEWORK_STRING_RESOURCE,context.getLocale(), "emxFramework.Type.pgMasterRawMaterialPart");
		else if(DataConstants.TYPE_SHAPE_PART.equals(strECPartType))
			strEnterpriseTypeValue=EnoviaResourceBundle.getProperty(context,DataConstants.FRAMEWORK_STRING_RESOURCE,context.getLocale(), "emxFramework.Type.pgPKGVPMPart");
		else if(DataConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strECPartType))
			strEnterpriseTypeValue=EnoviaResourceBundle.getProperty(context,DataConstants.FRAMEWORK_STRING_RESOURCE,context.getLocale(), "emxFramework.Type.pgAssembledProductPart");
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strEnterpriseTypeValue::"+strEnterpriseTypeValue);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END getValueForEnterpriseTypeAttr method");
		return strEnterpriseTypeValue;
	}

	/**
	 * Method to verify the attribute values for DesignDomain and Enterprise type
	 * @param mpRevObj
	 * @return boolean
	 * @author PTE2
	 */
	public boolean verifyAttributeValues(Map<String, String> mpRevObj) {
		boolean bValidValues=false;
		String strDesignDomain=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
		String strEnterpriseType=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
		
		if((DataConstants.CONSTANT_DESIGN_FOR_PACKAGING.equals(strDesignDomain) && DataConstants.VALID_ENT_TYPE_FOR_PACKAGING.contains(strEnterpriseType)) ||
			(DataConstants.CONSTANT_DESIGN_FOR_PRODUCT.equals(strDesignDomain) && DataConstants.VALID_ENT_TYPE_FOR_PRODUCT.contains(strEnterpriseType)) ||
			(DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION.equals(strDesignDomain) && DataConstants.VALID_ENT_TYPE_FOR_EXPLORATION.contains(strEnterpriseType))) {
				bValidValues=true;
		}
		return bValidValues;
	}

	/**
	 * Method to generate mql command to set clone derived from as empty
	 * @param sbMqlCommands
	 * @param mlRevisionObjects
	 */
	private void commandToSetCloneDerivedFromEmpty(StringBuilder sbMqlCommands, MapList mlRevisionObjects) {
		Map<String,String>mpRevObj;
		String strAttrValue;
		sbMqlCommands.append("# Update pngiDesignPart.pngCloneDerivedFrom attribute \n");
		for(int i=0;i<mlRevisionObjects.size();i++) {
			
			mpRevObj=(Map<String, String>) mlRevisionObjects.get(i);
			strAttrValue=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
			
			if(UIUtil.isNotNullAndNotEmpty(strAttrValue)) {
				sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
				sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_TYPE)+"\" ");
				sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_NAME)+"\" ");
				sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_REVISION)+"\" ");
				sbMqlCommands.append(DataConstants.ATTRIBUTE_PNG_CLONE_DERIVED_FROM+" ");
				sbMqlCommands.append("'';");
				sbMqlCommands.append("\n");
			}
		}
	}
	
	/**
	 * Method to verify whether Description of all revisions is same as the first revision
	 * @param mlRevisionObjects
	 * @return boolean
	 */
	private boolean verifyDescription(MapList mlRevisionObjects) {
		boolean bValidDescription=true;
		Map<String,String>mpRevObj;
		String strFirstRevDesc="";
		
		strFirstRevDesc=getDescriptionOfFirstRev(mlRevisionObjects);
		if(UIUtil.isNotNullAndNotEmpty(strFirstRevDesc)) {
		
			for(int i=0;i<mlRevisionObjects.size();i++) {
				mpRevObj=(Map<String, String>) mlRevisionObjects.get(i);
				
				if(!(strFirstRevDesc.equals(mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_V_DESCRIPTION)))) {
					bValidDescription=false;
					break;
				}
			}
		}
		return bValidDescription;
	}

	/**
	 * Method to generate mql commands to update description
	 * @param sbMqlCommands
	 * @param mlRevisionObjects
	 * @return MapLIst 
	 */
	private MapList commandToUpdateDescription(StringBuilder sbMqlCommands, MapList mlRevisionObjects) {
		MapList mlDescription = new MapList();
		Map<String,String>mpRevObj;
		String strFirstRevDesc = "";
		String strDesc;
		
		strFirstRevDesc=getDescriptionOfFirstRev(mlRevisionObjects);
		
		if(UIUtil.isNotNullAndNotEmpty(strFirstRevDesc)) {
			Map<String,String> mpDescription;
			sbMqlCommands.append("# Update description \n");
			
			for(int i=0;i<mlRevisionObjects.size();i++) {
				mpDescription=new HashMap();
				mpRevObj=(Map<String, String>) mlRevisionObjects.get(i);
				strDesc=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
				
				if(!strFirstRevDesc.equals(strDesc)) {
					sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
					sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_TYPE)+"\" ");
					sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_NAME)+"\" ");
					sbMqlCommands.append("\""+mpRevObj.get(DomainConstants.SELECT_REVISION)+"\" ");
					sbMqlCommands.append(DataConstants.ATTRIBUTE_V_DESCRIPTION+" \"");
					sbMqlCommands.append(strFirstRevDesc+"\";");
					sbMqlCommands.append("\n");
					
					//add the entry in the maplist
					mpDescription.put(DomainConstants.SELECT_TYPE,mpRevObj.get(DomainConstants.SELECT_TYPE));
					mpDescription.put(DomainConstants.SELECT_NAME,mpRevObj.get(DomainConstants.SELECT_NAME));
					mpDescription.put(DomainConstants.SELECT_REVISION,mpRevObj.get(DomainConstants.SELECT_REVISION));
					mpDescription.put(DataConstants.ATTRIBUTE_V_DESCRIPTION, strDesc);
					mlDescription.add(mpDescription);
				}
			}
		}
		return mlDescription;
	}
	
	/**
	 * Method to get description of first revision from maplist
	 * @param mlRevisionObjects
	 * @return String
	 */
	private String getDescriptionOfFirstRev(MapList mlRevisionObjects) {
		Map<String,String>mpRevObj;
		String strRevision;
		String strFirstRevDesc="";
		
		for(int i=0;i<mlRevisionObjects.size();i++) {
			
			mpRevObj=(Map<String, String>) mlRevisionObjects.get(i);
			strRevision=mpRevObj.get(DomainConstants.SELECT_REVISION);
			if(strRevision.equals(DataConstants.CONSTANT_FIRST_REVISION+"."+DataConstants.CONSTANT_FIRST_REVISION)) {
				//get the description of the first revision
				strFirstRevDesc=mpRevObj.get(DataConstants.SELECT_ATTRIBUTE_V_DESCRIPTION);
				break;
			}
		}
		return strFirstRevDesc;
	}

	/**
	 * Method to generate mql commands to reset description
	 * @param sbMqlCommands
	 * @param mlDescription
	 */
	private void commandToResetDescription(StringBuilder sbMqlCommands, MapList mlDescription) {
		Map<String,String>mpData;
		sbMqlCommands.append("# Reset the description back to original value \n");
		for(int i=0;i<mlDescription.size();i++) {
			mpData=(Map<String, String>) mlDescription.get(i);
					
			sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
			sbMqlCommands.append("\""+mpData.get(DomainConstants.SELECT_TYPE)+"\" ");
			sbMqlCommands.append("\""+mpData.get(DomainConstants.SELECT_NAME)+"\" ");
			sbMqlCommands.append("\""+mpData.get(DomainConstants.SELECT_REVISION)+"\" ");
			sbMqlCommands.append(DataConstants.ATTRIBUTE_V_DESCRIPTION+" \"");
			sbMqlCommands.append(mpData.get(DataConstants.ATTRIBUTE_V_DESCRIPTION)+"\";");
			sbMqlCommands.append("\n");
		}
	}
	
	/**
	 * Method to generate mql command to sync object
	 * @param sbMqlCommands
	 * @param mlRevisionObjects
	 */
	private void syncObject(StringBuilder sbMqlCommands, MapList mlRevisionObjects) {
		String strLastRev;
		String strOwner="";
		String strObjectId="";
		Map<String,String>mpRevObj;
		int iLastRev=mlRevisionObjects.size();
		if(iLastRev<10)
			strLastRev="00"+iLastRev+"."+DataConstants.CONSTANT_FIRST_REVISION;
		else
			strLastRev="0"+iLastRev+"."+DataConstants.CONSTANT_FIRST_REVISION;
		
		for(int i=0;i<mlRevisionObjects.size();i++) {
			
			mpRevObj=(Map<String, String>) mlRevisionObjects.get(i);
			if(strLastRev.equals(mpRevObj.get(DomainConstants.SELECT_REVISION))) {
				
				//get the owner and object id of the last revision
				strOwner=mpRevObj.get(DomainConstants.SELECT_OWNER);
				strObjectId=mpRevObj.get(DomainConstants.SELECT_ID);
				break;
			}
		}
		//mql command to invoke the sync method
		sbMqlCommands.append("# Sync the object \n");
		sbMqlCommands.append("exec prog pgDTDataRectification -method syncToEnterprise ");
		sbMqlCommands.append(strObjectId+" ");
		sbMqlCommands.append(strOwner+";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @param strLevel
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writeVPMReferenceDataInCSVFile(Context context,MapList mlData,String strLevel) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_SERVICE_NAME+strLevel+".csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		
		String strHeader="Name\tObjectId\tOwner\tCurrent\tCADDesignOrigination\tEnterpriseType\tDesignDomain\tMfgMaturityStatus\tCloneDerivedFrom\tVDerivedFrom\tECPartObjectId";
		String strExpectedDataInInputFile="Name";
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){      
			
			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");
			 
			 Map mpData;
			 StringBuilder sbRowData;
			 String strECPartId="";
			 
			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map)mlData.get(i);
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS)).append("\t");		
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM)).append("\t");		
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_V_DERIVED_FROM)).append("\t");		
				 
				 strECPartId=(String)mpData.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.id");
						 
				 if(UIUtil.isNullOrEmpty(strECPartId))
					 strECPartId="";
				 
				 sbRowData.append(strECPartId);
				 
				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_SERVICE_NAME+strLevel);
	}
	
	/**
	 * Method to update the particular VPLMDataMigration object attribute VPLMsys/MigrationStatus value
	 * @param context
	 * @param mlRevisionObjects
	 * @throws MatrixException
	 */
	private void setMarkerObjectStatus(Context context, MapList mlRevisionObjects) throws MatrixException {
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START setMarkerObjectStatus method");
		 Map mpRevObject;
		 String strFromSideValue="";
		 String strToSideValue="";
		 String strLevel="";
		 StringList slProcessedLevel=new StringList();
		 
		 for(int i=0;i<mlRevisionObjects.size();i++) {
			 mpRevObject=(Map) mlRevisionObjects.get(i);
			
			 strToSideValue=(String) mpRevObject.get("to["+DataConstants.REL_VPM_INSTANCE+"]");
			 strFromSideValue=(String) mpRevObject.get("from["+DataConstants.REL_VPM_INSTANCE+"]");
			 
			strLevel=engItem.getLevelForVPMReferenceObject(strToSideValue,strFromSideValue);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strLevel::"+strLevel);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strLevel already processed::"+slProcessedLevel.contains(strLevel));
			 
			if(!slProcessedLevel.contains(strLevel)) {
				Map<String,String> mpMarkerObject=findMarkerObject(context, strLevel);
				setMigrationStatus(context, mpMarkerObject, DataConstants.CONST_GENERATE);
				slProcessedLevel.add(strLevel);
			}
		 }
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END setMarkerObjectStatus method");
	}
	
	/**
	 * Method to  add Migration interface on the revisions of VPMReference object
	 * @param context
	 * @param mlRevisionObjects
	 * @throws MatrixException
	 */
	private void addMigrationInterface(Context context,MapList mlRevisionObjects) throws MatrixException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START addMigrationInterface method");
			Map<String,String>mpRevObj;
			InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
			boolean bInterfaceExists=false;
			String strObjectId;
			 String strFromSideValue="";
			 String strToSideValue="";
			 String strLevel="";
			for(int i=0;i<mlRevisionObjects.size();i++) {
				
				mpRevObj=(Map<String, String>) mlRevisionObjects.get(i);
				strObjectId=mpRevObj.get(DomainConstants.SELECT_ID);
				//check whether interface is already added on the object
				bInterfaceExists=interfaceMgmt.checkInterfaceOnObject(context, strObjectId, DataConstants.INTERFACE_MIG_STATUS_EXTENSION_DT_DATACLEANUP);
					
				if(!bInterfaceExists) {
					interfaceMgmt.addInterface(context, strObjectId, DataConstants.INTERFACE_MIG_STATUS_EXTENSION_DT_DATACLEANUP);
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Interface "+DataConstants.INTERFACE_MIG_STATUS_EXTENSION_DT_DATACLEANUP+" added successfully on "+strObjectId);
				}
				
				 strToSideValue=mpRevObj.get("to["+DataConstants.REL_VPM_INSTANCE+"]");
				 strFromSideValue=mpRevObj.get("from["+DataConstants.REL_VPM_INSTANCE+"]");
				 
				strLevel=engItem.getLevelForVPMReferenceObject(strToSideValue,strFromSideValue);
				 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strLevel::"+strLevel);
				 
				updateMigrationAttributes(context, strObjectId, DataConstants.CONST_GENERATE, strLevel);
			}
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END commandToAddMigrationInterface method");
	}
	
	/**
	 * Method to find the data to be rectified as per the level mentioned by the user
	 * @param context
	 * @param strLevel
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	public MapList findIncorrectVPMReferenceData(Context context,String strLevel) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findIncorrectLeafVPMReferenceData method");
		String strWhere=engItem.getWhereClauseForLevel(strLevel);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strWhere::"+strWhere);
		
		StringList slObjSelects=new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_V_DERIVED_FROM);
		slObjSelects.add("to["+DataConstants.REL_PART_SPECIFICATION+"].from.id");
		
		MapList mlVPMReferenceData = DomainObject.findObjects(context,
				DataConstants.TYPE_VPMREFERENCE, //typepattern
				DomainConstants.QUERY_WILDCARD,  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DataConstants.VAULT_VPLM,  // vault pattern
                strWhere, // where exp
                false,
                slObjSelects);

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found for "+strLevel+" level ::"+mlVPMReferenceData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findIncorrectLeafVPMReferenceData method");
		return mlVPMReferenceData;
		
	}
	
	/**
	 * Method to verify whether Hierarchy is correct for all revisions of VPMReference object
	 * @param mlRevisionObjects
	 * @return boolean
	 */
	private boolean verifyHierarchyForAllRevisions(Context context,MapList mlRevisionObjects,StringBuilder sbMqlCommands) throws FrameworkException {
		Map<String,String>mpRevObj;
		boolean bValidAttrValues=true;
		boolean bValidHierarchy=true;
		boolean bValidCombinationOfAttrValues=true;
		for(int i=0;i<mlRevisionObjects.size();i++) {
			
			mpRevObj=(Map) mlRevisionObjects.get(i);
			
			bValidAttrValues=verifyEmptyAttributeValues(context,mpRevObj,sbMqlCommands);
			bValidCombinationOfAttrValues=verifyAttributeValues(mpRevObj);
			if(! (bValidAttrValues || bValidCombinationOfAttrValues)) {
				bValidHierarchy=false;
				break;
			}
			bValidHierarchy = verifyParentChildHierarchy(context,mpRevObj);
			if(!bValidHierarchy){
				break;
			}
		}
		return bValidHierarchy;
	}
	
	/**
	 * Method to verify whether Hierarchy is correct for given VPMReference revision
	 * @param mpRevObj
	 * @return boolean
	 * @author PTE2
	 */
	private boolean verifyParentChildHierarchy(Context context, Map<String, String> mpRevObj) throws FrameworkException{
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START verifyParentChildHierarchy");
		boolean bValidHierarchy=true;
		
		MasterPackagingAssemblyPart mpap = new MasterPackagingAssemblyPart();
		MasterProductPart mpp = new MasterProductPart();
		PKGVPMPart shapePart = new PKGVPMPart();
		MasterRawMaterialPart mrmp = new MasterRawMaterialPart();
		MasterPackagingMaterialPart mpmp = new MasterPackagingMaterialPart();
		
		String strObjId=mpRevObj.get(DomainConstants.SELECT_ID);
		String strToSideValue=mpRevObj.get("to["+DataConstants.REL_VPM_INSTANCE+"]");
		String strFromSideValue=mpRevObj.get("from["+DataConstants.REL_VPM_INSTANCE+"]");
		String strPart=mpRevObj.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strObjId "+strObjId);
		
		Boolean fromSide = Boolean.parseBoolean(strFromSideValue);  
		Boolean toSide = Boolean.parseBoolean(strToSideValue);  
		
		DomainObject domObj = DomainObject.newInstance(context,strObjId);
		MapList relatedChild = new MapList();
		MapList relatedParent = new MapList();
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strFromVPM_Instance "+strFromSideValue);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>> strToVPM_Instance "+strToSideValue);
		
		String strLevel=engItem.getLevelForVPMReferenceObject(strToSideValue, strFromSideValue);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>> strLevel "+strLevel);
		
		//If its Standalone VPMReference then there is no hierarchy to verify 
		if(!DataConstants.CONST_STANDALONE.equalsIgnoreCase(strLevel)){
			//Intermediate
			if(DataConstants.CONST_INTERMEDIATE.equalsIgnoreCase(strLevel)){
				
				relatedChild = engItem.getRelatedVPMReference(context,domObj,fromSide,false);
				relatedParent = engItem.getRelatedVPMReference(context,domObj,false,toSide);
			}//leaf
			else if(DataConstants.CONST_LEAF.equalsIgnoreCase(strLevel)){
				
				relatedParent = engItem.getRelatedVPMReference(context,domObj,fromSide,toSide);				
			}//Top
			else if(DataConstants.CONST_TOP.equalsIgnoreCase(strLevel)){
				
				relatedChild = engItem.getRelatedVPMReference(context,domObj,fromSide,toSide);	
			}
			
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>> relatedChild "+relatedChild);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>> relatedParent "+relatedParent);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>> strPart "+strPart);
			
			if((DataConstants.TYPE_ASSEMBLED_PRODUCT_PART.equals(strPart) && !(relatedChild.isEmpty() || relatedParent.isEmpty())) || 
			(DataConstants.TYPE_SHAPE_PART.equals(strPart) && !(relatedParent.isEmpty())) ||
			((DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART.equals(strPart) || DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART.equals(strPart)) && !(relatedChild.isEmpty()))){
				
				bValidHierarchy = false;
			}
			
			if(bValidHierarchy && !relatedChild.isEmpty()){
				for(int i=0;i<relatedChild.size();i++){
					Map mpRelatedChild = (Map)relatedChild.get(i);
					String strPartType = (String)mpRelatedChild.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>relatedChild strPartType "+strPartType);
					
					if(UIUtil.isNotNullAndNotEmpty(strPartType) && 
							((DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART.equals(strPart) && !mpap.isValidChild(strPartType)) || 
							(DataConstants.TYPE_PG_MASTER_PRODUCT_PART.equals(strPart) && !mpp.isValidChild(strPartType)) || 
					(DataConstants.TYPE_SHAPE_PART.equals(strPart) && !shapePart.isValidChild(strPartType))))	{

						bValidHierarchy = false;
						break;
					}	
				}	
			}
			
			if(bValidHierarchy && !relatedParent.isEmpty()){
				for(int i=0;i<relatedParent.size();i++){
					Map mpRelatedParent = (Map)relatedParent.get(i);
					String strPartType = (String)mpRelatedParent.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>relatedParent strPartType "+strPartType);
					
					if(UIUtil.isNotNullAndNotEmpty(strPartType) && 
							((DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART.equals(strPart) && !mpap.isValidParent(strPartType)) || 
							(DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART.equals(strPart) && !mpmp.isValidParent(strPartType)) || 
							(DataConstants.TYPE_PG_MASTER_PRODUCT_PART.equals(strPart) && !mpp.isValidParent(strPartType)) || 
							(DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART.equals(strPart) && !mrmp.isValidParent(strPartType)))){
						
						bValidHierarchy = false;
						break;
					}
				}		
			}	
		}
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>:bValidHierarchy "+bValidHierarchy);
		VPLMIntegTraceUtil.trace(context," >>>>>>>>>>>>>>>>>END verifyParentChildHierarchy");
		return bValidHierarchy;	
	}
	
	/**
	 * Method to find PLMExchangeStatusDS type objects having PLMExchangeStatusDS.V_Custo attr value as pngComponent
	 * @param context
	 * @return MapList
	 * @throws MatrixException 
	 */
	public MapList findPLMExchangeStatusDSDataForCleanup(Context context) throws MatrixException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findPLMExchangeStatusDSDataForCleanup method");
		
		String strWhere=DataConstants.SELECT_ATTRIBUTE_PLM_EXCHANGE_STATUS_DS_VCUSTO+" == "+DataConstants.CONST_PNG_COMPONENT;
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strWhere:::"+strWhere);

		StringList slObjSelects=new StringList(7);
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		
		MapList mlPLMExchangeStatusDSData = DomainObject.findObjects(context,
				DataConstants.TYPE_PLM_EXCHANGE_STATUS_DS, //typepattern
				DomainConstants.QUERY_WILDCARD,  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DataConstants.VAULT_VPLM,  // vault pattern
                strWhere, // where exp
                false,
                slObjSelects);
		
		//get the relevant object TNR
		Map mpData;
		Map mpRelevantObjData;
		String strName;
		String strResult="";
		MapList mlFinalData=new MapList();
		BusinessObject boObject;
		DomainObject doObject;
		boolean bExists=false;
		StringBuilder sbData;
		
		StringList slSelectList=new StringList(3);
		slSelectList.add(DomainConstants.SELECT_TYPE);
		slSelectList.add(DomainConstants.SELECT_NAME);
		slSelectList.add(DomainConstants.SELECT_REVISION);
		
		for(int i=0;i<mlPLMExchangeStatusDSData.size();i++) {
			sbData=new StringBuilder();
			mpData=(Map)mlPLMExchangeStatusDSData.get(i);
			strName=(String)mpData.get(DomainConstants.SELECT_NAME);
			
			boObject=new BusinessObject(strName);
			bExists=boObject.exists(context);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>boObject is:::"+boObject+" bExists::"+bExists);
			
			if(bExists) {
				doObject=DomainObject.newInstance(context,strName);
				mpRelevantObjData=doObject.getInfo(context, slSelectList);
				
				sbData.append(mpRelevantObjData.get(DomainConstants.SELECT_TYPE));
				sbData.append(DataConstants.SEPARATOR_COMMA);
				sbData.append(mpRelevantObjData.get(DomainConstants.SELECT_NAME));
				sbData.append(DataConstants.SEPARATOR_COMMA);
				sbData.append(mpRelevantObjData.get(DomainConstants.SELECT_REVISION));
				
				strResult=sbData.toString();
			}else {
				strResult=DataConstants.CONST_OBJECT_NOT_FOUND;
			}
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strResult::"+strResult);
			
			mpData.put(DataConstants.CONST_RELEVANT_OBJECT, strResult);
			mlFinalData.add(mpData);
		}

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found for  PLMExchangeStatusDS::"+mlFinalData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findPLMExchangeStatusDSDataForCleanup method");
		return mlFinalData;
	}
	
	/**
	 * Method to write output of Scan mode to CSV file for PLMExchangeStatusDS type objects
	 * @param context
	 * @param mlData
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writePLMExchangeStatusDSDataInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writePLMExchangeStatusDSDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + 
				DataConstants.CONST_PLMEXCHANGESTATUSDS_SERVICE_NAME+DataConstants.CONST_SCAN+".csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);

		String strHeader="Name\tRevision\tObjectId\tOwner\tCurrent\tOriginated\tModified\tRelevant Object";
		String strExpectedDataInInputFile="Name";
		try(FileWriter fileWriter =new FileWriter(outputFile)){      

			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");

			 Map<String,String> mpData;
			 StringBuilder sbRowData;

			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map<String, String>) mlData.get(i);
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.CONST_RELEVANT_OBJECT)).append("\t");

				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writePLMExchangeStatusDSDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile,DataConstants.CONST_PLMEXCHANGESTATUSDS_SERVICE_NAME);
	}
	
	/**
	 * Method invoked for Generate mode for PLMExchangeStatusDSCleanupService
	 * @param context
	 * @param slNameList
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void generateModeLogicForPLMExchangeStatusDS(Context context, StringList slNameList) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForPLMExchangeStatusDS method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the PLMExchangeStatusDS already processed
		StringList slProcessedNames=new StringList();
		
		//Create a StringBuffer which would hold all the mql commands for single PLMExchangeStatusDS object
		StringBuilder sbMqlCommands;
		 String strName;
 
		 for(int i=0;i<slNameList.size();i++) {
			 
			 strName=slNameList.get(i);
			 VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>Name of PLMExchangeStatusDS object:::"+strName);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is name already processed:::"+slProcessedNames.contains(strName));
			 
			 if(!slProcessedNames.contains(strName)) {
							 
				 strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_PLMEXCHANGESTATUSDS_SERVICE_NAME+slNameList.get(i)+".mql";
				 outputFile = new File(strOutputFilePath);
			
				 try(FileWriter fileWriter =new FileWriter(outputFile)){      
		 
					 //3. Iterate for each PLMExchangeStatusDS
					 sbMqlCommands=new StringBuilder();
					 
					 //4. generate mql commands for setting PLMExchangeStatusDS.V_Custo attribute as VPMReference
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking commandToSetVCustoAsVPMReference method");
					 commandToSetVCustoAsVPMReference(sbMqlCommands,strName);
		 
					 //add the name in the slProcessedNames list
					 slProcessedNames.add(strName);
				 
					 //add the mql commands to the output file
					 fileWriter.write(sbMqlCommands.toString());
					 System.out.println("\n Output mql File is created at:"+strOutputFilePath);
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
			 } //try loop
		 }//if loop
		}//for loop

		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForPLMExchangeStatusDS method");
	}
	
	/**
	 * Method to generate command to modify the value of PLMExchangeStatusDS.V_Custo attribute to VPMReference
	 * @param sbMqlCommands
	 * @param strName
	 */
	private void commandToSetVCustoAsVPMReference(StringBuilder sbMqlCommands, String strName) {
		sbMqlCommands.append("# Update PLMExchangeStatusDS.V_Custo attribute \n");
			
		sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
		sbMqlCommands.append(DataConstants.TYPE_PLM_EXCHANGE_STATUS_DS+" ");
		sbMqlCommands.append(strName+" ");
		sbMqlCommands.append(DataConstants.CONST_THREE_HYPHEN_REVISION+" ");
		sbMqlCommands.append(DataConstants.ATTRIBUTE_PLM_EXCHANGE_STATUS_DS_VCUSTO+" ");
		sbMqlCommands.append(DataConstants.TYPE_VPMREFERENCE+";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method to get the incorrect data for maturity state inconsistency
	 * @param context
	 * @param strECPartType
	 * @return MapList
	 * @throws FrameworkException
	*/
	public MapList findIncorrectMaturityStateDataForVPMReference(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findIncorrectMaturityStateDataForVPMReference method");
				
		StringBuilder sbWhere=new StringBuilder();
		sbWhere.append("current!=").append(DataConstants.STATE_RELEASED);
		sbWhere.append(" && to[").append(DataConstants.REL_VPM_REPINSTANCE).append("].from.type==").append(DataConstants.TYPE_VPMREFERENCE);
		sbWhere.append(" && to[").append(DataConstants.REL_VPM_REPINSTANCE).append("].from.current==").append(DataConstants.STATE_RELEASED);
				
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>where clause::"+sbWhere.toString());
				
		StringList slObjSelects=new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_ISONCE_INSTANTIABLE);
		slObjSelects.add("to["+DataConstants.REL_VPM_REPINSTANCE+"].from.name");
		slObjSelects.add("to["+DataConstants.REL_VPM_REPINSTANCE+"].from.revision");
					
		Pattern typePattern=new Pattern(DataConstants.TYPE_3DSHAPE);
		typePattern.addPattern(DataConstants.TYPE_DRAWING);
				
		MapList mlInconsistentData = DomainObject.findObjects(context,
			typePattern.getPattern(),						 //typepattern
			DomainConstants.QUERY_WILDCARD,  // namepattern
	        DomainConstants.QUERY_WILDCARD,  // revpattern
	        DomainConstants.QUERY_WILDCARD,  // owner pattern
	        DataConstants.VAULT_VPLM,  // vault pattern
	        sbWhere.toString(), // where exp
	        false,						 //expand object
	        slObjSelects);			//object selects

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found ::"+mlInconsistentData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findIncorrectMaturityStateDataForVPMReference method");
		return mlInconsistentData;
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @param strECpartType
	 * @throws MatrixException
	 * @throws IOException
	*/
	public void writeIncorrectMaturityStateDataDataInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeIncorrectMaturityStateDataDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_INCORRECT_MATURITY_STATE_SERVICE_NAME+"Scan.csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
				
		String strHeader="Type\tName\tRevision\tObjectId\tOwner\tCurrent\tOriginated\tModified\tVPMReference Name\tVPMReference Revision";
		String strExpectedDataInInputFile="Type\tName\tRevision";
		try(FileWriter fileWriter =new FileWriter(outputFile)){      
					
			//header row
			fileWriter.write(strHeader);
			fileWriter.write("\n");
					 
			Map mpData;
			StringBuilder sbRowData;
			
			for(int i=0;i<mlData.size();i++) {
				sbRowData=new StringBuilder();
				mpData=(Map)mlData.get(i);
				//logic to verify if the Drawing is a shared Drawing or not
				if(DataConstants.TYPE_DRAWING.equals(mpData.get(DomainConstants.SELECT_TYPE))) {
					//get the value of PLMCoreRepReference.V_isOnceInstantiable attribute. If its FALSE, then the Drawing is Shared
					if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase((String) mpData.get(DataConstants.SELECT_ATTRIBUTE_ISONCE_INSTANTIABLE)))
						continue;
				}
						 
				sbRowData.append(mpData.get(DomainConstants.SELECT_TYPE)).append("\t");
				sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
				sbRowData.append(mpData.get("to["+DataConstants.REL_VPM_REPINSTANCE+"].from.name")).append("\t");
				sbRowData.append(mpData.get("to["+DataConstants.REL_VPM_REPINSTANCE+"].from.revision"));
															 
				fileWriter.write(sbRowData.toString());
				fileWriter.write("\n");
			}
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeIncorrectMaturityStateDataDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile,DataConstants.CONST_INCORRECT_MATURITY_STATE_SERVICE_NAME);
	}
	
	/**
	 * Method which would be invoked for generate mode for Incorrect Maturity State cleanup script
	 * @param context
	 * @param StringList slInconsistentDataNameList
	 * @throws MatrixException 
	 * @throws IOException 
	*/
	public void generateModeLogicForIncorrectMaturityState(Context context,Map<String,StringList> mpVPMReferenceInfo) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForIncorrectState method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the objects already processed
		StringList slProcessedObjects=new StringList();
				
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
		 
		String strTypeNameRev;
		MapList mlObjectInfo;
		Map mpObjectInfo;
		StringList slTypeNameRevList=mpVPMReferenceInfo.get("TypeNameRevList");
		StringList slTypeNameRev;
		IPManagement ipMgmt=new IPManagement(context);
				 
		StringList slSelects=new StringList(1);
		slSelects.add(DomainConstants.SELECT_CURRENT);
				 
		for(int i=0;i<slTypeNameRevList.size();i++) {
			strTypeNameRev=slTypeNameRevList.get(i);
			VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>TypeNameRev of object:::"+strTypeNameRev);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strTypeNameRev));
					 
			if(!slProcessedObjects.contains(strTypeNameRev)) {
									 
				strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_INCORRECT_MATURITY_STATE_SERVICE_NAME+strTypeNameRev+".mql";
				outputFile = new File(strOutputFilePath);
				try(FileWriter fileWriter =new FileWriter(outputFile)){      
					 
					// Iterate for each object
					sbMqlCommands=new StringBuilder();
								 
					//split the strNameRev string
					slTypeNameRev=StringUtil.split(strTypeNameRev, DataConstants.SEPARATOR_HASH);
								 
					//3. get all the info of particular VPMReference
					mlObjectInfo=ipMgmt.findObject(context,slTypeNameRev.get(0),slTypeNameRev.get(1),slTypeNameRev.get(2),slSelects);
																
					if(!mlObjectInfo.isEmpty()) {
						mpObjectInfo=(Map)mlObjectInfo.get(0);
								 
						//trig off
						sbMqlCommands.append("trigger off;\n");
										 
						//generate mql commands for promoting object to Released state
						commandToRelease3DPartDrawing(sbMqlCommands, slTypeNameRev.get(0),slTypeNameRev.get(1),slTypeNameRev.get(2),mpObjectInfo);
										 
						sbMqlCommands.append("trigger on;");
						//add the name in the slProcessedNames list
						slProcessedObjects.add(strTypeNameRev);
							 
						//add the mql commands to the output file
						fileWriter.write(sbMqlCommands.toString());
						System.out.println("\n Output mql File is created at:"+strOutputFilePath);
						VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
					}
				} //try loop
			}
		}//for loop
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForIncorrectState method");
	}
	
	/**
	 * Method to generate mql command to promote the object to Release state
	 * @param sbMqlCommands
	 * @param type of object
	 * @param name of object
	 * @param revision of object
	*/
	private void commandToRelease3DPartDrawing(StringBuilder sbMqlCommands, String strType,String strName, String strRevision,Map mpObjectInfo) {
		sbMqlCommands.append("#Release the object \n");
				
		String strState=(String)mpObjectInfo.get(DomainConstants.SELECT_CURRENT);
				
		if(DataConstants.STATE_PRIVATE.equalsIgnoreCase(strState)) {
			commandToApproveSignature(sbMqlCommands, strType, strName, strRevision, DataConstants.SIGN_SHAREWITHINPROJECT);
			commandToPromoteObject(sbMqlCommands, strType, strName, strRevision);
					
			commandToApproveSignature(sbMqlCommands, strType, strName, strRevision, DataConstants.SIGN_TOFREEZE);
			commandToPromoteObject(sbMqlCommands, strType, strName, strRevision);
					
			commandToApproveSignature(sbMqlCommands, strType, strName, strRevision, DataConstants.SIGN_TORELEASE);
			commandToPromoteObject(sbMqlCommands, strType, strName, strRevision);
		}
		else if(DataConstants.STATE_IN_WORK.equalsIgnoreCase(strState)) {
			commandToApproveSignature(sbMqlCommands, strType, strName, strRevision, DataConstants.SIGN_TOFREEZE);
			commandToPromoteObject(sbMqlCommands, strType, strName, strRevision);
					
			commandToApproveSignature(sbMqlCommands, strType, strName, strRevision, DataConstants.SIGN_TORELEASE);
			commandToPromoteObject(sbMqlCommands, strType, strName, strRevision);
		}
		else if(DataConstants.STATE_FROZEN.equalsIgnoreCase(strState)) {
			commandToApproveSignature(sbMqlCommands, strType, strName, strRevision,DataConstants.SIGN_TORELEASE);
			commandToPromoteObject(sbMqlCommands, strType, strName, strRevision);
		}
	}
	
	/**
	 * Method to generate mql command to approve the signature
	 * @param sbMqlCommands
	 * @param type of object
	 * @param name of object
	 * @param revision of object
	 * @param signature name
	*/
	private void commandToApproveSignature(StringBuilder sbMqlCommands, String strType,String strName, String strRevision,String strSignatureName) {
		sbMqlCommands.append(DataConstants.CONST_APPROVE_BUS);
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append(strRevision+" signature ");
		sbMqlCommands.append("\""+strSignatureName+"\"");
		sbMqlCommands.append(" comment \"").append(DataConstants.CLEANUP_SCRIPT_APPROVAL_COMMENT).append("\"");
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
	}
			
	/**
	 * Method to generate mql command to promote the object
	 * @param sbMqlCommands
	 * @param strType
	 * @param strName
	 * @param strRevision
	*/
	private void commandToPromoteObject(StringBuilder sbMqlCommands, String strType,String strName, String strRevision) {
		sbMqlCommands.append(DataConstants.CONST_PROMOTE_BUS);
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append(strRevision);
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method to find the EC Part data in vplm vault
	 * @param context
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	public MapList findECPartDataInVPLMVault(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findECPartDataInVPLMVault method");
		StringBuilder sbWhere=new StringBuilder();
		sbWhere.append(DomainConstants.SELECT_VAULT).append(" == ").append(DataConstants.VAULT_VPLM);
		sbWhere.append(" && (from[").append(DomainConstants.RELATIONSHIP_PART_SPECIFICATION).append("].to.type == ").append(DataConstants.TYPE_VPMREFERENCE);
		sbWhere.append(" || from[").append(DomainConstants.RELATIONSHIP_PART_SPECIFICATION).append("].to.type == '").append(DataConstants.TYPE_ARTIOSCAD_COMPONENT).append("')");
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strWhere::"+sbWhere.toString());
		
		StringList slObjSelects=new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		
		Pattern typePattern=new Pattern("");
		for(int i=0;i<DataConstants.VALID_DT_SYNC_TYPES.size();i++) {
			typePattern.addPattern((String) DataConstants.VALID_DT_SYNC_TYPES.get(i));
		}
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>types::"+typePattern.getPattern());
		
		MapList mlECPartData = DomainObject.findObjects(context,
				typePattern.getPattern(), //typepattern
				DomainConstants.QUERY_WILDCARD,  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DataConstants.VAULT_VPLM,  // vault pattern
                sbWhere.toString(), // where exp
                false,
                slObjSelects);

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found ::"+mlECPartData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findECPartDataInVPLMVault method");
		return mlECPartData;
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writeECPartInVPLMVaultDataInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeECPartInVPLMVaultDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_EC_PART_IN_VPLM_VAULT_SERVICE_NAME+"Scan.csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		
		String strHeader="Type\tName\tRevision\tObjectId\tOwner\tCurrent\tOriginated\tModified";
		String strExpectedDataInInputFile="Type\tName\tRevision";
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){      
			
			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");
			 
			 Map mpData;
			 StringBuilder sbRowData;
			 String strECPartId="";
			 
			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map)mlData.get(i);
				 sbRowData.append(mpData.get(DomainConstants.SELECT_TYPE)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
				 
				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeECPartInVPLMVaultDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_EC_PART_IN_VPLM_VAULT_SERVICE_NAME);
	}
	
	/**
	 * Method to get all VPMReference data
	 * @param context
	 * @param strECPartType
	 * @return MapList
	 * @throws FrameworkException
	*/
	public MapList findAllVPMReferenceData(Context context,StringList slObjSelects) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findAllVPMReferenceData method");
					
		MapList mlData = DomainObject.findObjects(context,
			DataConstants.TYPE_VPMREFERENCE,						 //typepattern
			DomainConstants.QUERY_WILDCARD,  // namepattern
	        DomainConstants.QUERY_WILDCARD,  // revpattern
	        DomainConstants.QUERY_WILDCARD,  // owner pattern
	        DataConstants.VAULT_VPLM,  // vault pattern
	        "", 						// where exp
	        false,						 //expand object
	        slObjSelects);			//object selects

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found ::"+mlData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findAllVPMReferenceData method");
		return mlData;
	}
	
	/**
	 * Method to get selectables for VPMReference
	 * @return StringList
	*/
	public StringList getSelectablesForVPMReference() {
		StringList slObjSelects=new StringList(10);
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
		slObjSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
		return slObjSelects;
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writeDataForNoDesignDomainInterfaceDataInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeDataForNoDesignDomainInterfaceDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_NO_DESIGN_DOMAIN_INTERFACE_SERVICE_NAME+"Scan.csv";
		String strOutputFileForInputFile=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_NO_DESIGN_DOMAIN_INTERFACE_SERVICE_NAME+"Scan_InputFileData.csv";
		
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		java.io.File outputFileForInputFile = new java.io.File(strOutputFileForInputFile);
		
		String strHeader="Name\tRevision\tObjectId\tOwner\tCurrent\tOriginated\tModified\tDesignDomain\tCADDesignOrigination\tCloneDerivedFrom\tECPartType";
		String strExpectedDataInInputFile="Name\tRevision";

		try(FileWriter fileWriter =new FileWriter(outputFile)){ 
			try(FileWriter fileWriter1 =new FileWriter(outputFileForInputFile)){ 
			
			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");
			 
			 Map mpData;
			 StringBuilder sbRowData;
			 boolean bInterfaceAdded=false;
			 String strDesignDomain;
			 String strObjectId;
			 String strInterfaceName="";
			 String strECPartType;
			 InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
			 
			 for(int i=0;i<mlData.size();i++) {
				 strInterfaceName="";
				 sbRowData=new StringBuilder();
				 mpData=(Map)mlData.get(i);
				 
				 strObjectId=(String)mpData.get(DomainConstants.SELECT_ID);
				 strDesignDomain=(String)mpData.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
				 strECPartType=(String)mpData.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
				 
				 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strObjectId::"+strObjectId+" strDesignDomain::"+strDesignDomain+" strECPartType::"+strECPartType);
				 
				 if(UIUtil.isNotNullAndNotEmpty(strDesignDomain)) {
					 if(!DataConstants.CONSTANT_DESIGN_DOMAIN_APP_VPMREFERENCE.equalsIgnoreCase(strDesignDomain))
						 strInterfaceName="pngi"+strDesignDomain;
					 else
						 strInterfaceName="pngi"+DataConstants.CONSTANT_DESIGN_FOR_ASSEMBLED;
				 }
				 else if(UIUtil.isNotNullAndNotEmpty(strECPartType) && DataConstants.VALID_SYNC_TYPES.contains(strECPartType)) {
					 strInterfaceName="pngi"+getDesignDomainForECPartType(strECPartType);
				 }
				 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strInterfaceName:::"+strInterfaceName);
				 
				 bInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strObjectId, strInterfaceName);
				 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bInterfaceAdded:::"+bInterfaceAdded);
				 
				 if(!bInterfaceAdded) {
					 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
					 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
					 sbRowData.append(strObjectId).append("\t");
					 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
					 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
					 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
					 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
					 sbRowData.append(strDesignDomain).append("\t");
					 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION)).append("\t");
					 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM)).append("\t");		
				 
					 if(UIUtil.isNullOrEmpty(strECPartType))
						 strECPartType="";
					 
					 sbRowData.append(strECPartType);
					 
					 fileWriter.write(sbRowData.toString());
					 fileWriter.write("\n");
					 
					 // we need to pass only that data to the input file generation, which either has Design Domain attr value or has EC Part connected.
					 //If both cases are not valid, then the data should be written to csv file, but not passed for input file.
					 if(UIUtil.isNotNullAndNotEmpty(strInterfaceName)){
						 fileWriter1.write(sbRowData.toString());
						 fileWriter1.write("\n");
					 }
				 }
			 }
			}
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeDataForNoDesignDomainInterfaceDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFileForInputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_NO_DESIGN_DOMAIN_INTERFACE_SERVICE_NAME);
	}
	
/**
			 * Method to get the incorrect data for Design Domain inconsistency
			 * @param context
			 * @param strECPartType
			 * @return MapList
			 * @throws FrameworkException
			 */
			public MapList findIncorrectVPMReferenceDataForDesignDomain(Context context,String strECPartType) throws FrameworkException {
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findIncorrectVPMReferenceDataForDesignDomain method");
				String strWhere=getWhereClauseForDesignDomain(strECPartType);
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strWhere::"+strWhere);
				
				StringList slObjSelects=new StringList();
				slObjSelects.add(DomainConstants.SELECT_ID);
				slObjSelects.add(DomainConstants.SELECT_NAME);
				slObjSelects.add(DomainConstants.SELECT_OWNER);
				slObjSelects.add(DomainConstants.SELECT_REVISION);
				slObjSelects.add(DomainConstants.SELECT_CURRENT);
				slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
				slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS);
				slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
				slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
				slObjSelects.add("to["+DataConstants.REL_PART_SPECIFICATION+"].from.id");
				slObjSelects.add("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
				
				MapList mlVPMReferenceData = DomainObject.findObjects(context,
						DataConstants.TYPE_VPMREFERENCE, //typepattern
						DomainConstants.QUERY_WILDCARD,  // namepattern
		                DomainConstants.QUERY_WILDCARD,  // revpattern
		                DomainConstants.QUERY_WILDCARD,  // owner pattern
		                DataConstants.VAULT_VPLM,  // vault pattern
		                strWhere, // where exp
		                false,
		                slObjSelects);

				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found for "+strECPartType+" ::"+mlVPMReferenceData.size());
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findIncorrectVPMReferenceDataForDesignDomain method");
				return mlVPMReferenceData;
			}
			
			/**
			 * Method to generate the where clause for finding objects, based on Design Domain
			 * @param strECPartType
			 * @return where clause
			 */
			private String getWhereClauseForDesignDomain(String strECPartType) {
				StringBuilder sbWhereClause=new StringBuilder();
				
				sbWhereClause.append("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type==");
				sbWhereClause.append(strECPartType);
				sbWhereClause.append(" && ");
				sbWhereClause.append(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN+"]!=");
				sbWhereClause.append(getDesignDomainForECPartType(strECPartType));
				
				return sbWhereClause.toString();
			}
			
			/**
			 * Method to write the information of the objects to CSV File
			 * @param context
			 * @param mlData
			 * @param strECpartType
			 * @throws MatrixException
			 * @throws IOException
			 */
			public void writeDesignDomainDataInCSVFile(Context context,MapList mlData,String strECPartType) throws MatrixException, IOException {
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeDesignDomainDataInCSVFile method");
				String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_DESIGNDOMAIN_SERVICE_NAME+strECPartType+".csv";
				java.io.File outputFile = new java.io.File(strOutputFilePath);
				String strHeader="Name\tRevision\tObjectId\tOwner\tCurrent\tCADDesignOrigination\tDesignDomain\tEnterpriseType\tMfgMaturityStatus\tECPartObjectId\tECPartObjectType";
				String strExpectedDataInInputFile="Name\tRevision";
				try(FileWriter fileWriter =new FileWriter(outputFile)){      
					
					//header row
					 fileWriter.write(strHeader);
					 fileWriter.write("\n");
					 
					 Map mpData;
					 StringBuilder sbRowData;
					 String strECPartId="";
					 String strECType="";
					 
					 for(int i=0;i<mlData.size();i++) {
						 sbRowData=new StringBuilder();
						 mpData=(Map)mlData.get(i);
						 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
						 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
						 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
						 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
						 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
						 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION)).append("\t");
						 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN)).append("\t");
						 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE)).append("\t");
						 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_MFG_MATURITY_STATUS)).append("\t");		
										 
						 strECPartId=(String)mpData.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.id");
						 strECType=(String)mpData.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
								 
						 if(UIUtil.isNullOrEmpty(strECPartId))
							 strECPartId="";
						 
						 sbRowData.append(strECPartId);
						 sbRowData.append("\t");
						 
						 if(UIUtil.isNullOrEmpty(strECType))
							 strECType="";
						 
						 sbRowData.append(strECType);
						 
						 fileWriter.write(sbRowData.toString());
						 fileWriter.write("\n");
					 }
				}
				System.out.println("\n CSV File is generated at::"+strOutputFilePath);
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeDesignDomainDataInCSVFile method");
				generateInputFileForGenerateMode(context,outputFile,strHeader,strExpectedDataInInputFile,DataConstants.CONST_DESIGNDOMAIN_SERVICE_NAME+strECPartType);
			}
			
			/**
			 * Method which would be invoked for generate mode for Design Domain cleanup script
			 * @param context
			 * @param Map mpVPMReferenceInfo
			 * @throws MatrixException 
			 * @throws IOException 
			 */
			public void generateModeLogicForDesignDomain(Context context, Map<String,StringList> mpVPMReferenceInfo) throws MatrixException, IOException {
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForDesignDomain method");
				//1. Create file, which would hold the mql commands to be executed for rectification of data
				String strOutputFilePath;
				File outputFile;
				//2. Create a StringList which would hold the names of the VPMReference already processed
				StringList slProcessedObjects=new StringList();
				
				//Create a StringBuffer which would hold all the mql commands
				StringBuilder sbMqlCommands;
		 
				 String strNameRev;
				 Map mpVPMReferenceObjInfo;
				 StringList slNameRevList=mpVPMReferenceInfo.get("NameRevList");
				 StringList slNameRev;
				 String strECPartType;
				 String strObjectId;
				 String strDesignDomain;
				 String strEnterpriseType;
				 String strNewDesignDomain;
				 String strNewEnterpriseType;
				 String strIncorrectInterfaceName;
				 String strCorrectInterfaceName="";
				 boolean bIncorrectInterfaceAdded=false;
				 boolean bCorrectInterfaceAdded=false;
				 
				 StringList slSelects=new StringList(2);
				 slSelects.add(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
				 slSelects.add(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
				 slSelects.add(DomainConstants.SELECT_ID);
				 slSelects.add("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
				 
				 InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
				 
				 for(int i=0;i<slNameRevList.size();i++) {
					 strIncorrectInterfaceName="";
					 strNameRev=slNameRevList.get(i);
					 VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>NameRev of VPMReference object:::"+strNameRev);
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strNameRev));
					 
					 if(!slProcessedObjects.contains(strNameRev)) {
									 
						 strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_DESIGNDOMAIN_SERVICE_NAME+strNameRev+".mql";
						 outputFile = new File(strOutputFilePath);
						 try(FileWriter fileWriter =new FileWriter(outputFile)){      
					 
								 // Iterate for each Sim Doc
								 sbMqlCommands=new StringBuilder();
								 
								 //split the strNameRev string
								 slNameRev=StringUtil.split(strNameRev, DataConstants.SEPARATOR_HASH);
								 //3. get all the info of particular VPMReference
								mpVPMReferenceObjInfo=getInfoOfVPMReference(context,slNameRev.get(0),slNameRev.get(1),slSelects);
															
								 if(!mpVPMReferenceObjInfo.isEmpty()) {
									//trig off
									 sbMqlCommands.append("trigger off;\n");
									 
									strECPartType=(String)mpVPMReferenceObjInfo.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
									strObjectId=(String)mpVPMReferenceObjInfo.get(DomainConstants.SELECT_ID);
									strDesignDomain=(String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
									strEnterpriseType=(String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
								
									strNewDesignDomain=getDesignDomainForECPartType(strECPartType);
									strNewEnterpriseType=getEnterpriseTypeForECPartType(strECPartType);
									
									if(UIUtil.isNotNullAndNotEmpty(strDesignDomain)) {
										 if(!DataConstants.CONSTANT_DESIGN_DOMAIN_APP_VPMREFERENCE.equalsIgnoreCase(strDesignDomain))
											 strIncorrectInterfaceName="pngi"+strDesignDomain;
										 else
											 strIncorrectInterfaceName="pngi"+DataConstants.CONSTANT_DESIGN_FOR_ASSEMBLED;
									}
									
									if(UIUtil.isNotNullAndNotEmpty(strNewDesignDomain))
										strCorrectInterfaceName="pngi"+strNewDesignDomain;
								
									VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strECPartType:::"+strECPartType+" strDesignDomain::"+strDesignDomain+" strNewDesignDomain::"+strNewDesignDomain);
									VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strEnterpriseType:::"+strEnterpriseType+" strNewEnterpriseType::"+strNewEnterpriseType);
									VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strIncorrectInterfaceName:::"+strIncorrectInterfaceName+" strCorrectInterfaceName::"+strCorrectInterfaceName);
									
									//generate mql commands for modifying the Design Domain
									 commandToUpdateDesignDomainForVPMReference(sbMqlCommands, slNameRev.get(0),slNameRev.get(1),strNewDesignDomain);
									 
									 //generate mql commands for modifying the Design Domain
									 commandToUpdateEnterpriseTypeForVPMReference(sbMqlCommands, slNameRev.get(0),slNameRev.get(1),strEnterpriseType,strNewEnterpriseType);
								
									 //check for incorrect interface added on the object. remove if it is added
									 if(UIUtil.isNotNullAndNotEmpty(strIncorrectInterfaceName)) {
										 bIncorrectInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strObjectId, strIncorrectInterfaceName);
										 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bIncorrectInterfaceAdded:::"+bIncorrectInterfaceAdded);
										 if(bIncorrectInterfaceAdded) {
											 commandToRemoveInterfaceFromVPMReference(sbMqlCommands, slNameRev.get(0),slNameRev.get(1),strIncorrectInterfaceName);
										 }
									 }
									 
									 //add the correct interface on the object, if not already added
									 if(UIUtil.isNotNullAndNotEmpty(strCorrectInterfaceName)) {
										 bCorrectInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strObjectId, strCorrectInterfaceName);
										 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bCorrectInterfaceAdded:::"+bCorrectInterfaceAdded);
										 if(!bCorrectInterfaceAdded) {
											 commandToAddInterfaceOnVPMReference(sbMqlCommands, slNameRev.get(0),slNameRev.get(1),strCorrectInterfaceName);
										 }
									 }
									 
									 sbMqlCommands.append("trigger on;");
									 //add the name in the slProcessedNames list
									 slProcessedObjects.add(strNameRev);
							 
									 //add the mql commands to the output file
									 fileWriter.write(sbMqlCommands.toString());
									 System.out.println("\n Output mql File is created at:"+strOutputFilePath);
									 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
								 }
							 } //try loop
					 }
				 }//for loop
				 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForDesignDomain method");
			}

			/**
			 * Method to generate mql commands to remove the interface from VPMReference object
			 * @param sbMqlCommands
			 * @param strName
			 * @param strRevision
			 * @param strInterfaceName
			 */
			private void commandToRemoveInterfaceFromVPMReference(StringBuilder sbMqlCommands, String strName,	String strRevision, String strInterfaceName) {
				sbMqlCommands.append("# Remove Interface \n");
				
				sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
				sbMqlCommands.append("\""+DataConstants.TYPE_VPMREFERENCE+"\" ");
				sbMqlCommands.append("\""+strName+"\" ");
				sbMqlCommands.append(strRevision+" ");
				sbMqlCommands.append("remove interface ");
				sbMqlCommands.append("\""+strInterfaceName+"\" ");
				sbMqlCommands.append(";");
				sbMqlCommands.append("\n");
			}

			/**
			 * Method to generate the mql command to add interface on the VPMReference object
			 * @param sbMqlCommands
			 * @param strName
			 * @param strRevision
			 * @param strInterfaceName
			 */
			private void commandToAddInterfaceOnVPMReference(StringBuilder sbMqlCommands, String strName, String strRevision,String strInterfaceName) {
				sbMqlCommands.append("# Add Interface \n");
				
				sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
				sbMqlCommands.append("\""+DataConstants.TYPE_VPMREFERENCE+"\" ");
				sbMqlCommands.append("\""+strName+"\" ");
				sbMqlCommands.append(strRevision+" ");
				sbMqlCommands.append("add interface ");
				sbMqlCommands.append("\""+strInterfaceName+"\";");
				sbMqlCommands.append("\n");
			}

			/**
			 * Method to generate mql command to update the Enterprise type attribute
			 * @param sbMqlCommands
			 * @param strName
			 * @param strRevision
			 * @param strEnterpriseType
			 * @param strNewEnterpriseType
			 */
			private void commandToUpdateEnterpriseTypeForVPMReference(StringBuilder sbMqlCommands, String strName,
					String strRevision, String strEnterpriseType, String strNewEnterpriseType) {
				
				if(UIUtil.isNotNullAndNotEmpty(strNewEnterpriseType) && !strEnterpriseType.equalsIgnoreCase(strNewEnterpriseType)) {
					sbMqlCommands.append("# Update Enterprise Type \n");
					
					sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
					sbMqlCommands.append("\""+DataConstants.TYPE_VPMREFERENCE+"\" ");
					sbMqlCommands.append("\""+strName+"\" ");
					sbMqlCommands.append(strRevision+" ");
					sbMqlCommands.append(DataConstants.ATTRIBUTE_PGENTERPRISETYPE+" ");
					sbMqlCommands.append("\""+strNewEnterpriseType+"\";");
					sbMqlCommands.append("\n");
				}
			}

			/**
			 * Method to get the information of VPMReference object
			 * @param context
			 * @param strName
			 * @param strRev
			 * @param slSelects
			 * @return Map
			 * @throws FrameworkException
			 */
			private Map getInfoOfVPMReference(Context context, String strName, String strRev,StringList slSelects) throws FrameworkException {
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START getInfoOfVPMReference method");
				Map mpResult=new HashMap();
				IPManagement ipMgmt=new IPManagement(context);
				MapList mlObject=ipMgmt.findObject(context, DataConstants.TYPE_VPMREFERENCE, strName, strRev, slSelects);
				
				if(!mlObject.isEmpty()) {
					mpResult=(Map)mlObject.get(0);
				}else {
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Object "+strName+" "+ strRev+" does not exist in database");
				}
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>VPMReference object info::"+mpResult);
				VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END getInfoOfVPMReference method");
				return mpResult;
			}

			/**
			 * Method to generate mql command to update the Design Domain attribute
			 * @param sbMqlCommands
			 * @param name of VPMReference object
			 * @param revision of VPMReference object
			 * @param new Design Domain value
			 */
			private void commandToUpdateDesignDomainForVPMReference(StringBuilder sbMqlCommands, String strName, String strRevision, String strNewDesignDomain) {
				if(UIUtil.isNotNullAndNotEmpty(strNewDesignDomain)) {
					
					sbMqlCommands.append("# Update Design Domain \n");
					
					sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
					sbMqlCommands.append("\""+DataConstants.TYPE_VPMREFERENCE+"\" ");
					sbMqlCommands.append("\""+strName+"\" ");
					sbMqlCommands.append(strRevision+" ");
					sbMqlCommands.append(DataConstants.ATTRIBUTE_DESIGN_DOMAIN+" ");
					sbMqlCommands.append(strNewDesignDomain);
					sbMqlCommands.append(";");
					sbMqlCommands.append("\n");
				}
			}

			/**
			 * Method to get the Design Domain attribute value as per the EC Part type passed
			 * @param strECPartType
			 * @return String
			 */
			public  String getDesignDomainForECPartType(String strECPartType) {
				String strDesignDomain="";
				
				if(strECPartType.contains(DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART) || 
						strECPartType.contains(DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART)) {
					strDesignDomain=DataConstants.CONSTANT_DESIGN_FOR_PACKAGING;
				}
				else if(strECPartType.contains(DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART) || 
						strECPartType.contains(DataConstants.TYPE_PG_MASTER_PRODUCT_PART)) {
					strDesignDomain=DataConstants.CONSTANT_DESIGN_FOR_PRODUCT;
				}
				else if(strECPartType.contains(DataConstants.TYPE_SHAPE_PART)){
					strDesignDomain=DataConstants.CONSTANT_DESIGN_FOR_EXPLORATION;
				}
				else if(strECPartType.contains(DataConstants.TYPE_ASSEMBLED_PRODUCT_PART)){
					strDesignDomain=DataConstants.CONSTANT_DESIGN_FOR_ASSEMBLED;
				}
				return strDesignDomain;
			}
			
			/**
			 * Method to get the Enterprise Type attribute value as per the EC Part type passed
			 * @param strECPartType
			 * @return String
			 */
			public String getEnterpriseTypeForECPartType(String strECPartType) {
				String strEnterpriseType="";
				
				if(strECPartType.contains(DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART)) {
					strEnterpriseType=DataConstants.ENT_TYPE_MPMP;
				}
				else if(strECPartType.contains(DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART)) {
					strEnterpriseType=DataConstants.ENT_TYPE_MPAP;
				}
				else if(strECPartType.contains(DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART)) {
					strEnterpriseType=DataConstants.ENT_TYPE_MRMP;
				}
				else if(strECPartType.contains(DataConstants.TYPE_PG_MASTER_PRODUCT_PART)) {
					strEnterpriseType=DataConstants.ENT_TYPE_MPP;
				}
				else if(strECPartType.contains(DataConstants.TYPE_SHAPE_PART)){
					strEnterpriseType=DataConstants.ENT_TYPE_SHAPE_PART;
				}
				else if(strECPartType.contains(DataConstants.TYPE_ASSEMBLED_PRODUCT_PART)){
					strEnterpriseType=DataConstants.STR_ASSEMBLED_PRODUCT_PART;
				}
				return strEnterpriseType;
			}
			
			/**
			 * Method to get the EC Part type as per Enterprise Type attribute value 
			 * @param strEnterpriseType
			 * @return String
			 */
			public String getECPartTypeForEnterpriseType(String strEnterpriseType) {
				String strECPartType="";
				
				if(strEnterpriseType.equals(DataConstants.ENT_TYPE_MPMP)) {
					strECPartType=DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART;
				}
				else if(strEnterpriseType.equals(DataConstants.ENT_TYPE_MPAP)) {
					 strECPartType=DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART;
				}
				else if(strEnterpriseType.equals(DataConstants.ENT_TYPE_MRMP)) {
					strECPartType=DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART;
				}
				else if(strEnterpriseType.equals(DataConstants.ENT_TYPE_MPP)) {
					strECPartType=DataConstants.TYPE_PG_MASTER_PRODUCT_PART;
				}
				else if(strEnterpriseType.equals(DataConstants.ENT_TYPE_SHAPE_PART)){
					strECPartType=DataConstants.TYPE_SHAPE_PART;
				}
				else if(strEnterpriseType.equals(DataConstants.STR_ASSEMBLED_PRODUCT_PART)){
					strECPartType=DataConstants.TYPE_ASSEMBLED_PRODUCT_PART;
				}
				return strECPartType;
			}

	/**
	 * Method to find the EC Part data which is connected to VPMReference, but does not have a template
	 * @param context
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	public MapList findECPartDataWithoutTemplate(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findECPartDataWithoutTemplate method");
		StringBuilder sbWhere=new StringBuilder();
		sbWhere.append("from[").append(DomainRelationship.RELATIONSHIP_PART_SPECIFICATION).append("].to.type==").append(DataConstants.TYPE_VPMREFERENCE);
		sbWhere.append(" && from[").append(DataConstants.RELATIONSHIP_TEMPLATE).append("]==FALSE");
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strWhere::"+sbWhere.toString());
		
		StringList slObjSelects=new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		slObjSelects.add("from["+DomainRelationship.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id");
		slObjSelects.add("from["+DomainRelationship.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"]."+DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
		slObjSelects.add("from["+DomainRelationship.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].owner");
		
		Pattern typePattern=new Pattern("");
		String strType="";
		for(int i=0;i<DataConstants.VALID_DT_SYNC_TYPES.size();i++) {
			strType=(String) DataConstants.VALID_DT_SYNC_TYPES.get(i);
			if(!DataConstants.TYPE_SHAPE_PART.equals(strType))
				typePattern.addPattern(strType);
		}
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>types::"+typePattern.getPattern());
		
		MapList mlECPartData = DomainObject.findObjects(context,
				typePattern.getPattern(), //typepattern
				DomainConstants.QUERY_WILDCARD,  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DomainConstants.QUERY_WILDCARD,  // vault pattern
                sbWhere.toString(), // where exp
                false,
                slObjSelects);

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found ::"+mlECPartData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findECPartDataWithoutTemplate method");
		return mlECPartData;
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writeECPartDataWithoutTemplateInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeECPartDataWithoutTemplateInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_EC_PART_WITHOUT_TEMPLATE_SERVICE_NAME+"Scan.csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		
		String strHeader="Type\tName\tRevision\tObjectId\tOwner\tCurrent\tOriginated\tModified\tVPMReference ObjectId\tVPMReference Owner\tCAD Design Origination";
		String strExpectedDataInInputFile="Type\tName\tRevision";
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){      
			
			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");
			 
			 Map mpData;
			 StringBuilder sbRowData;
			 
			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map)mlData.get(i);
				 sbRowData.append(mpData.get(DomainConstants.SELECT_TYPE)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
				 sbRowData.append(mpData.get("from["+DomainRelationship.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].id")).append("\t");
				 sbRowData.append(mpData.get("from["+DomainRelationship.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"].owner")).append("\t");
				 sbRowData.append(mpData.get("from["+DomainRelationship.RELATIONSHIP_PART_SPECIFICATION+"].to["+DataConstants.TYPE_VPMREFERENCE+"]."+
				 DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION));
				 
				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeECPartDataWithoutTemplateInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_EC_PART_WITHOUT_TEMPLATE_SERVICE_NAME);
	}
	
	/**
	 * Method to generate the input file as per the requirement. This input file would be generated after csv file is created in Scan mode
	 * @param context
	 * @param csvFile
	 * @param strHeader
	 * @param strExpectedDataInInputFile
	 * @param strServiceName
	 * @throws IOException
	 * @throws MatrixException
	 */
	private void generateInputFileForGenerateMode(Context context, File csvFile, String strHeader,String strExpectedDataInInputFile,String strServiceName) throws IOException, MatrixException {
		
		VPLMIntegTraceUtil.trace(context, ">> START of generateInputFileForGenerateMode method");
		
		StringList slHeader=StringUtil.split(strHeader, "\t");
		StringList slExpectedData=StringUtil.split(strExpectedDataInInputFile, "\t");
		StringList slColumnNos=new StringList();
		
		VPLMIntegTraceUtil.trace(context, ">> slHeader::"+slHeader);
		VPLMIntegTraceUtil.trace(context, ">> slExpectedData::"+slExpectedData);
		
		for(int j=0;j<slExpectedData.size();j++) {
			for(int i=0;i<slHeader.size();i++) {
				if(slExpectedData.get(j).equals(slHeader.get(i))) {
					slColumnNos.add(String.valueOf(i));
					break;
				}
			}
		}
		
		VPLMIntegTraceUtil.trace(context, ">> slColumnNos::"+slColumnNos);
		int iSize=slColumnNos.size();
		
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + strServiceName+"InputFile.txt";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		
		VPLMIntegTraceUtil.trace(context, ">> strOutputFilePath::"+strOutputFilePath);
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){  
		
			VPLMIntegTraceUtil.trace(context, ">> Inside filewriter try block");
			
			try(FileReader fileReader =new FileReader(csvFile)){      
			
				VPLMIntegTraceUtil.trace(context, ">> Inside fileReader try block");
				
				try (BufferedReader br = new BufferedReader(fileReader)){
					    //to exclude the header row	
						br.readLine();
						VPLMIntegTraceUtil.trace(context, ">> Inside BufferedReader try block");
						String line = "";
						StringList slTemp;
						while((line = br.readLine()) != null) {
							slTemp =StringUtil.split(line,"\t");
							//write the data to output file
							for(int i=0;i<iSize;i++) {
								fileWriter.write(slTemp.get(Integer.parseInt(slColumnNos.get(i))));
								if(i!=iSize-1)
									fileWriter.write("\t");
							}
							fileWriter.write("\n");
					  }
				}
			}
		}
		System.out.println("\n Input File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Input File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context, ">> END of generateInputFileForGenerateMode method");
	}
	
	/**
	 * Method which would be invoked for generate mode for VPLM vault EC Part data
	 * @param context
	 * @param StringList slInconsistentDataNameList
	 * @throws MatrixException 
	 * @throws IOException 
	*/
	public void generateModeLogicForVPLMVaultData(Context context,Map<String,StringList> mpECPartInfo) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForVPLMVaultData method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the objects already processed
		StringList slProcessedObjects=new StringList();
				
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
		 
		String strTypeNameRev;
		Map mpObjectInfo;
		StringList slTypeNameRevList=mpECPartInfo.get("TypeNameRevList");
		StringList slTypeNameRev;
				 
		for(int i=0;i<slTypeNameRevList.size();i++) {
			strTypeNameRev=slTypeNameRevList.get(i);
			VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>TypeNameRev of object:::"+strTypeNameRev);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strTypeNameRev));
					 
			if(!slProcessedObjects.contains(strTypeNameRev)) {
									 
				strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_EC_PART_IN_VPLM_VAULT_SERVICE_NAME+strTypeNameRev+".mql";
				outputFile = new File(strOutputFilePath);
				try(FileWriter fileWriter =new FileWriter(outputFile)){      
					 
					// Iterate for each object
					sbMqlCommands=new StringBuilder();
								 
					//split the strNameRev string
					slTypeNameRev=StringUtil.split(strTypeNameRev, DataConstants.SEPARATOR_HASH);
								 
					//trig off
					sbMqlCommands.append("trigger off;\n");
										 
					//generate mql command for modifying the vault
					commandToUpdateVault(sbMqlCommands, slTypeNameRev.get(0),slTypeNameRev.get(1),slTypeNameRev.get(2));
										 
					sbMqlCommands.append("trigger on;");
					//add the name in the slProcessedNames list
					slProcessedObjects.add(strTypeNameRev);
							 
					//add the mql commands to the output file
					fileWriter.write(sbMqlCommands.toString());
					System.out.println("\n Output mql File is created at:"+strOutputFilePath);
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
				} //try loop
			}
		}//for loop
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForVPLMVaultData method");
	}

	/**
	 *  Method to generate mql command to modify the vault from vplm to eService Production
	 * @param sbMqlCommands
	 * @param type
	 * @param name
	 * @param revision
	 */
	private void commandToUpdateVault(StringBuilder sbMqlCommands, String strType, String strName, String strRevision) {
		sbMqlCommands.append("# Modify the vault \n");
		sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append("\""+strRevision+"\" vault ");
		sbMqlCommands.append("\""+DataConstants.VAULT_ESERVICE_PRODUCTION+"\"");
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
	}
	
	
	/**
	 * Method to find the private data in assemblies
	 * @param context
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	public MapList findPrivateDataInAssembly(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findPrivateDataInAssembly method");
		StringBuilder sbWhereClause=new StringBuilder();
		sbWhereClause.append(DomainConstants.SELECT_CURRENT).append("==").append(DataConstants.STATE_PRIVATE);
		sbWhereClause.append(" && ((");
		sbWhereClause.append(engItem.getWhereClauseForVPMReferenceLevel(DataConstants.CONST_LEAF));
		sbWhereClause.append(") || (");
		sbWhereClause.append(engItem.getWhereClauseForVPMReferenceLevel(DataConstants.CONST_INTERMEDIATE));
		sbWhereClause.append(") || (");
		sbWhereClause.append(engItem.getWhereClauseForVPMReferenceLevel(DataConstants.CONST_TOP));
		sbWhereClause.append("))");
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strWhere::"+sbWhereClause.toString());
		
		StringList slObjSelects=new StringList(10);
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
		slObjSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.id");
		slObjSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
		
		MapList mlVPMReferenceData = DomainObject.findObjects(context,
				DataConstants.TYPE_VPMREFERENCE, //typepattern
				DomainConstants.QUERY_WILDCARD,  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DataConstants.VAULT_VPLM,  // vault pattern
                sbWhereClause.toString(), // where exp
                false,										//expandType
                slObjSelects);						//selectables

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found ::"+mlVPMReferenceData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findPrivateDataInAssembly method");
		return mlVPMReferenceData;
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @param strLevel
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writePrivateAssemblyDataInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writePrivateAssemblyDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_PRIVATE_ASSEMBLY_DATA_SERVICE_NAME+"Scan.csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		
		String strHeader="Name\tRevision\tObjectId\tOwner\tOriginated\tModified\tCADDesignOrigination\tClone Derived From\tECPart ObjectId\tSecurity Group";
		String strExpectedDataInInputFile="Name\tRevision";
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){      
			
			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");
			 
			 Map mpData;
			 StringBuilder sbRowData;
			 DomainObject doVPMReference;
			 StringList slIPControlClass;
			 String strECPartId;
			 String strECPartType;
			 
			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map)mlData.get(i);
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM)).append("\t");
				 
				 strECPartId=(String) mpData.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.id");
				 strECPartType=(String) mpData.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
				
				 if(UIUtil.isNotNullAndNotEmpty(strECPartType) && DataConstants.VALID_DT_SYNC_TYPES.contains(strECPartType)) {
					if(UIUtil.isNullOrEmpty(strECPartId))
						strECPartId="";
				 
				 sbRowData.append(strECPartId).append("\t");
				}else
					 sbRowData.append("").append("\t");
				
				 doVPMReference=DomainObject.newInstance(context,(String)mpData.get(DomainConstants.SELECT_ID));
				 slIPControlClass=doVPMReference.getInfoList(context, "access.businessobject.to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from.name");
				
				 VPLMIntegTraceUtil.trace(context, slIPControlClass.toString());
				 if(slIPControlClass.isEmpty())
					 sbRowData.append("");
				 else 
					 sbRowData.append((StringUtil.split(slIPControlClass.toString(), "=").get(1)).replace("]", ""));
				 
				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writePrivateAssemblyDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_PRIVATE_ASSEMBLY_DATA_SERVICE_NAME);
	}
	
	/**
	 * Method which would be invoked for generate mode for Private data in assembly cleanup script
	 * @param context
	 * @param Map mpVPMReferenceInfo
	 * @throws MatrixException 
	 * @throws IOException 
	 */
	public void generateModeLogicForPrivateAssemblyData(Context context, Map<String,StringList> mpVPMReferenceInfo) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForPrivateAssemblyData method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the VPMReference already processed
		StringList slProcessedObjects=new StringList();
		
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
 
		 String strNameRev;
		 Map mpVPMReferenceObjInfo;
		 StringList slNameRevList=mpVPMReferenceInfo.get("NameRevList");
		 StringList slNameRev;
		 StringList slIPControlClass;
		 String strName; 
		 String strRevision; 
		 String strVPMReferenceId;
		 String strVPMReferenceOwner;
		 String strCADOrigination;
		 DomainObject doVPMReference;
		 boolean bIsOwnerActive;
		 boolean bInterfaceAdded;
		 CommonUtility commonUtility=new CommonUtility(context);
		 InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
		 
		 String strNewOwner=getDefaultOwner();
			 
		 StringList slSelects=new StringList(11);
		 slSelects.add(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
		 slSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.id");
		 slSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.name");
		 slSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.revision");
		 slSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
		 slSelects.add("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
		 slSelects.add(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
		 slSelects.add(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
		 slSelects.add(DataConstants.SELECT_PHYSICALID);
		 slSelects.add(DomainConstants.SELECT_ID);
		 slSelects.add(DomainConstants.SELECT_OWNER);
		 
		 for(int i=0;i<slNameRevList.size();i++) {
			 strNameRev=slNameRevList.get(i);
			 VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>NameRev of VPMReference object:::"+strNameRev);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strNameRev));
			 
			 if(! (slProcessedObjects.contains(strNameRev) || strNameRev.contains("ToDelete") || strNameRev.contains("prd"))) {
							 
				 strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_PRIVATE_ASSEMBLY_DATA_SERVICE_NAME+strNameRev+".mql";
				 outputFile = new File(strOutputFilePath);
				 
				 try(FileWriter fileWriter =new FileWriter(outputFile)){      
			 
						 // Iterate for each VPMReference object
						 sbMqlCommands=new StringBuilder();
						 
						 //split the strNameRev string
						 slNameRev=StringUtil.split(strNameRev, DataConstants.SEPARATOR_HASH);
						 strName=slNameRev.get(0);
						 strRevision=slNameRev.get(1);
						 
						 //3. get all the info of particular VPMReference
						mpVPMReferenceObjInfo=getInfoOfVPMReference(context,strName,strRevision,slSelects);
													
						 if(!mpVPMReferenceObjInfo.isEmpty()) {
								
							strCADOrigination=(String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_ATTRIBUTE_CAD_DESIGN_ORIGINATION);
							strVPMReferenceId=(String)mpVPMReferenceObjInfo.get(DomainConstants.SELECT_ID);
							strVPMReferenceOwner=(String)mpVPMReferenceObjInfo.get(DomainConstants.SELECT_OWNER);
							
							doVPMReference=DomainObject.newInstance(context,strVPMReferenceId);
							slIPControlClass=doVPMReference.getInfoList(context, "access.businessobject.to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from.name");
							
							VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strCADOrigination:::"+strCADOrigination+" slIPControlClass::"+slIPControlClass);
						
							if(!DataConstants.RANGE_VALUE_AUTOMATION.equalsIgnoreCase(strCADOrigination)) {
								 //trig off
								 sbMqlCommands.append("trigger off;\n");
								 bInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strVPMReferenceId, DataConstants.INTERFACE_PNG_DESIGNPART);
								 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>DesignPartInterfaceAdded:::"+bInterfaceAdded);
								 if(!bInterfaceAdded) {
									 commandToAddInterfaceOnVPMReference(sbMqlCommands, strName,strRevision, DataConstants.INTERFACE_PNG_DESIGNPART);
								 }
							if(UIUtil.isNullOrEmpty(strCADOrigination)) {
								//generate mql commands for modifying the CAD Design Origination
								commandToUpdateCADDesignOriginationForVPMReference(sbMqlCommands, strName, strRevision, DataConstants.RANGE_VALUE_MANUAL);
							}
								
							 if(slIPControlClass.isEmpty()) {
								 //generate mql commands for Security Group
								 checkForConnectedIPControlClass(context,mpVPMReferenceObjInfo,strName, strRevision,sbMqlCommands);
							 }
							 
							 //generate mql commands to promote the VPMReference to In Work state
							commandToApproveSignature(sbMqlCommands, DataConstants.TYPE_VPMREFERENCE, strName, strRevision, DataConstants.SIGN_SHAREWITHINPROJECT);
							commandToPromoteObject(sbMqlCommands, DataConstants.TYPE_VPMREFERENCE, strName, strRevision);
								
							//generate mql commands to promote the connected 3DShape and Drawing to In Work state
								
							getConnectedVPMRepInstanceData(context,doVPMReference,sbMqlCommands);
								//check if the owner is inactive. Change the owner to heyartz.m if original owner is inactive
								bIsOwnerActive=commonUtility.verifyIfPersonIsActive(context, strVPMReferenceOwner);
								if(!bIsOwnerActive) {
									commandToModifyOwner(sbMqlCommands,DataConstants.TYPE_VPMREFERENCE, strName, strRevision,strNewOwner);
									strVPMReferenceOwner=strNewOwner;
								}
								
							commandToSyncObject(strVPMReferenceId, strVPMReferenceOwner, sbMqlCommands);
							
							String strPGCloneDerivedFrom=(String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strPGCloneDerivedFrom:::"+strPGCloneDerivedFrom);
							 
							 if(UIUtil.isNotNullAndNotEmpty(strPGCloneDerivedFrom)) {
								 commandToInvokeRestoreDerivedFromOnSyncMethod(strVPMReferenceId,DataConstants.TYPE_VPMREFERENCE,sbMqlCommands);
							 }
							 sbMqlCommands.append("trigger on;");
							}
							 //add the name in the slProcessedNames list
							 slProcessedObjects.add(strNameRev);
					 
							 //add the mql commands to the output file
							 fileWriter.write(sbMqlCommands.toString());
							 System.out.println("\n Output mql File is created at:"+strOutputFilePath);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
						 }
					 } //try loop
			 }
		 }//for loop
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForPrivateAssemblyData method");
	}
	
	/**
	 * Method to return the name of default Owner
	 * @return String
	 */
	public String getDefaultOwner() {
		return "heyartz.m";
	}

	/**
	 * Method to generate mql commands to modify the owner
	 * @param sbMqlCommands
	 * @param typeVpmreference
	 * @param strName
	 * @param strRevision
	 */
	private void commandToModifyOwner(StringBuilder sbMqlCommands, String strType, String strName,String strRevision,String strNewOwner) {
		sbMqlCommands.append("# Modify the owner \n");
		sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append("\""+strRevision+"\" ");
		sbMqlCommands.append("owner ");
		sbMqlCommands.append("\""+strNewOwner+"\";");
		sbMqlCommands.append("\n");
	}

	/**
	 * Method to check whether EC Part/proxy object exists, and IP Control class is connected to it. If not, generate mql commands for same.
	 * @param context
	 * @param mpVPMReferenceObjInfo
	 * @param strName
	 * @param strRevision
	 * @param sbMqlCommands
	 * @throws FrameworkException
	 */
	private void checkForConnectedIPControlClass(Context context, Map mpVPMReferenceObjInfo, String strName, String strRevision,StringBuilder sbMqlCommands) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START of checkForConnectedIPControlClass method");
		//get the VPMReference Id
		String  strVPMReferenceId=(String)mpVPMReferenceObjInfo.get(DomainConstants.SELECT_ID);
		
		// get the default security group
		String  strDefaultSecurityGroup=getDefaultSecurityGroup((String) mpVPMReferenceObjInfo.get(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE));
		 
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strDefaultSecurityGroup:::"+strDefaultSecurityGroup);
		 
		String  strECPartType=(String)mpVPMReferenceObjInfo.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.type");
		 
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strECPartType:::"+strECPartType);
		 
		 if(UIUtil.isNotNullAndNotEmpty(strECPartType) && DataConstants.VALID_DT_SYNC_TYPES.contains(strECPartType)) {

			 //check if  IP Control class is connected. If not, connect the defaultSecurityGroup
			 String strECPartIPControlClass=(String)mpVPMReferenceObjInfo.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from."
			 		+ "to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
			 
			 String strECPartId=(String)mpVPMReferenceObjInfo.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.id");
			 
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strECPartIPControlClass:::"+strECPartIPControlClass+" strECPartId::"+strECPartId);
			 
			 if(UIUtil.isNullOrEmpty(strECPartIPControlClass)) {
				String strECPartName=(String)mpVPMReferenceObjInfo.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.name");
				String strECPartRevision=(String)mpVPMReferenceObjInfo.get("to["+DomainConstants.RELATIONSHIP_PART_SPECIFICATION+"].from.revision");
				 
				 commandToConnectIPControlClass(sbMqlCommands,strECPartType,strECPartName,strECPartRevision,strDefaultSecurityGroup);
			 }
			 commandToAddAccessOnObject(sbMqlCommands,DataConstants.TYPE_VPMREFERENCE, strName, strRevision,strECPartId,strECPartType);
			 
		 } else if(UIUtil.isNullOrEmpty(strECPartType)) {
			 //check for PG Clone derived form attribute. If value is there, connect security group to proxy object
			 
			 String strPGCloneDerivedFrom=(String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_ATTRIBUTE_PNG_CLONE_DERIVED_FROM);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strPGCloneDerivedFrom:::"+strPGCloneDerivedFrom);
			 
			 if(UIUtil.isNotNullAndNotEmpty(strPGCloneDerivedFrom)) {
				 //find the proxy object
				 IPManagement ipMgmt=new IPManagement(context);
			
				 StringList slProxySelects=new StringList(2);
				 slProxySelects.add(DomainConstants.SELECT_ID);
				 slProxySelects.add("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
				 
				 MapList mlProxyInfo=ipMgmt.findObject(context, DataConstants.TYPE_PROXY_OBJECT, (String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_PHYSICALID), "", slProxySelects);
				 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>mlProxyInfo:::"+mlProxyInfo);
				 
				 if(!mlProxyInfo.isEmpty()) {
					 
					 Map mpProxyInfo=(Map)mlProxyInfo.get(0);
					 
					 String strProxyId=(String)mpProxyInfo.get(DomainConstants.SELECT_ID);
					 DomainObject doProxyObject=DomainObject.newInstance(context,strProxyId);
					 StringList slProxyIPControlClass=doProxyObject.getInfoList(context,"to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
					
					 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>slProxyIPControlClass:::"+slProxyIPControlClass+" strProxyId::"+strProxyId);
					 
					 if(slProxyIPControlClass.isEmpty()) {
					 
						 commandToConnectIPControlClass(sbMqlCommands,DataConstants.TYPE_PROXY_OBJECT,
							 (String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_PHYSICALID),"",strDefaultSecurityGroup);
					 }
					 commandToAddAccessOnObject(sbMqlCommands,DataConstants.TYPE_VPMREFERENCE, strName, strRevision,strProxyId,DataConstants.TYPE_PROXY_OBJECT);
				 }
			 }
		 }
	}

	/**
	 * Method to get the connected 3DShape/Drawing for VPMReference and add commands for their promotion
	 * @param context
	 * @param doVPMReference
	 * @param sbMqlCommands
	 */
	private void getConnectedVPMRepInstanceData(Context context, DomainObject doVPMReference, StringBuilder sbMqlCommands) {
		VPLMIntegTraceUtil.trace(context, ">>> START of getConnectedVPMRepInstanceData method");
		MapList mlVPMRepInstanceData=engItem.getRelatedRepInstance(context, doVPMReference);
		
		VPLMIntegTraceUtil.trace(context, ">>>mlVPMRepInstanceData:::"+mlVPMRepInstanceData);
		
		if(!mlVPMRepInstanceData.isEmpty()) {
			Map mpData;
			String strName;
			String strRevision;
			String strType;
			String strCurrent;
			String strIsOnceInstantiable;
			boolean bAddCommands;
			
			for(int i=0;i<mlVPMRepInstanceData.size();i++) {
				bAddCommands=true;
				mpData=(Map) mlVPMRepInstanceData.get(i);
				strType=(String)mpData.get(DomainConstants.SELECT_TYPE);
				strName=(String)mpData.get(DomainConstants.SELECT_NAME);
				strRevision=(String)mpData.get(DomainConstants.SELECT_REVISION);
				strCurrent=(String)mpData.get(DomainConstants.SELECT_CURRENT);
				strIsOnceInstantiable=(String)mpData.get(DataConstants.SELECT_ATTRIBUTE_ISONCE_INSTANTIABLE);
				
				//START: DTCLD-320:Added the check not to add the commands for Shared Drawing
				if(DataConstants.TYPE_DRAWING.equals(strType) && DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strIsOnceInstantiable)) {
					bAddCommands=false;
				}
				//END: DTCLD-320:Added the check not to add the commands for Shared Drawing
				if(DataConstants.STATE_PRIVATE.equals(strCurrent) && bAddCommands){
					commandToApproveSignature(sbMqlCommands, strType, strName, strRevision, DataConstants.SIGN_SHAREWITHINPROJECT);
					commandToPromoteObject(sbMqlCommands, strType, strName, strRevision);
				}
			}
		}
		VPLMIntegTraceUtil.trace(context, ">>> END of getConnectedVPMRepInstanceData method");
	}

	/**
	 * Method to generate mql command to add access on the object.
	 * @param sbMqlCommands
	 * @param strType
	 * @param strName
	 * @param strRevision
	 * @param strObjectIdToBeAddedAsAccess
	 * @param strObjectType
	 */
	private void commandToAddAccessOnObject(StringBuilder sbMqlCommands, String strType, String strName, String strRevision, String strObjectIdToBeAddedAsAccess,String strObjectType) {
		sbMqlCommands.append("# Add access on the object \n");
		sbMqlCommands.append("modify bus ");
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append(strRevision);
		sbMqlCommands.append(" add access bus ");
		sbMqlCommands.append(strObjectIdToBeAddedAsAccess);
		sbMqlCommands.append(" as ");
		sbMqlCommands.append("\"read\", ");
		sbMqlCommands.append("\"show\"");
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
		
	}

	/**
	 * Method to generate mql command to connect the IP Control class to the object
	 * @param sbMqlCommands
	 * @param strType
	 * @param strName
	 * @param strRevision
	 * @param strIPClassName
	 */
	private void commandToConnectIPControlClass(StringBuilder sbMqlCommands, String strType, String strName, String strRevision, String strIPClassName) {
		sbMqlCommands.append("# Connect IP Control class object \n");
		sbMqlCommands.append("connect bus ");
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append("\""+strRevision+"\"");
		sbMqlCommands.append(" relationship ");
		sbMqlCommands.append("\""+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"\"");
		sbMqlCommands.append(" from ");
		sbMqlCommands.append("\""+DataConstants.TYPE_IP_CONTROL_CLASS+"\" ");
		sbMqlCommands.append("\""+strIPClassName+"\" -");
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
	}

	/**
	 * Method to get the default security group name as per EC Part type
	 * @param strEnterpriseType
	 * @return String
	 */
	public String getDefaultSecurityGroup(String strEnterpriseType) {
		String strSecurityGroup="";
		if(DataConstants.ENT_TYPE_MPP.equals(strEnterpriseType) || DataConstants.STR_ASSEMBLED_PRODUCT_PART.equals(strEnterpriseType))
			strSecurityGroup="Corporate Functions-HiR";
		else
			strSecurityGroup="Corporate Functions-R";
		return strSecurityGroup;
	}

	/**
	 * Method to generate mql command to update the CADDesign Origination attribute
	 * @param sbMqlCommands
	 * @param name of VPMReference object
	 * @param revision of VPMReference object
	 * @param new value
	 */
	private void commandToUpdateCADDesignOriginationForVPMReference(StringBuilder sbMqlCommands, String strName, String strRevision, String strNewCADDesignOrigination) {
		sbMqlCommands.append("# Update CADDesign Origination \n");
		
		sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
		sbMqlCommands.append("\""+DataConstants.TYPE_VPMREFERENCE+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append(strRevision+" ");
		sbMqlCommands.append(DataConstants.ATTRIBUTE_CAD_DESIGN_ORIGINATION+" ");
		sbMqlCommands.append(strNewCADDesignOrigination);
		sbMqlCommands.append(";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method to generate command to sync object
	 * @param strObjectId
	 * @param strOwner
	 * @param sbMqlCommands
	 */
	private void commandToSyncObject(String strObjectId, String strOwner, StringBuilder sbMqlCommands) {
		//mql command to invoke the sync method
		sbMqlCommands.append("# Sync the object \n");
		sbMqlCommands.append("exec prog pgDTDataRectification -method syncToEnterpriseWithPostOperation ");
		sbMqlCommands.append(strObjectId+" ");
		sbMqlCommands.append(strOwner+";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method to generate command to sync object
	 * @param strObjectId
	 * @param strOwner
	 * @param sbMqlCommands
	 */
	private void commandToInvokeRestoreDerivedFromOnSyncMethod(String strObjectId,String strType,StringBuilder sbMqlCommands) {
		//mql command to invoke the sync method
		sbMqlCommands.append("# Invoke method after creation of EC Part from sync \n");
		sbMqlCommands.append("exec prog pgSyncEBOMDefer -method restoreDerivedFromOnSync ");
		sbMqlCommands.append(DataConstants.CONSTANT_EMPTY+" ");
		sbMqlCommands.append(strObjectId+" ");
		sbMqlCommands.append(DataConstants.CONSTANT_EMPTY+" ");
		sbMqlCommands.append(strType+";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method to find the Shape Part data governed by pgPKGWIPPart policy
	 * @param context
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	public MapList findIncorrectShapePartData(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findIncorrectShapePartData method");
		
		String strWhereClause="policy == "+DataConstants.POLICY_PG_PKGWIPPART;
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strWhere::"+strWhereClause);
		
		StringList slObjSelects=new StringList(10);
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		slObjSelects.add(DataConstants.SELECT_ATTRIBUTE_ISVPMVISIBLE);
		
		MapList mlShapePartData = DomainObject.findObjects(context,
				DataConstants.TYPE_SHAPE_PART, //typepattern
				DomainConstants.QUERY_WILDCARD,  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DomainConstants.QUERY_WILDCARD,  // vault pattern
                strWhereClause,                                       // where exp
                false,										//expandType
                slObjSelects);						//selectables

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found ::"+mlShapePartData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findIncorrectShapePartData method");
		return mlShapePartData;
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @param strLevel
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writeIncorrectShapePartDataInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeIncorrectShapePartDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_SHAPE_PART_INCORRECT_POLICY_DATA_SERVICE_NAME+"Scan.csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		
		String strHeader="Name\tRevision\tObjectId\tOwner\tState\tOriginated\tModified\tisVPMVisible";
		String strExpectedDataInInputFile="Name\tRevision";
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){      
			
			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");
			 
			 Map mpData;
			 StringBuilder sbRowData;
			 
			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map)mlData.get(i);
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
				 sbRowData.append(mpData.get(DataConstants.SELECT_ATTRIBUTE_ISVPMVISIBLE)).append("\t");
				 
				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeIncorrectShapePartDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_SHAPE_PART_INCORRECT_POLICY_DATA_SERVICE_NAME);
	}
	
	/**
	 * Method which would be invoked for generate mode for shape part incorrect policy data
	 * @param context
	 * @param Map mpShapePartInfo
	 * @throws MatrixException 
	 * @throws IOException 
	 */
	public void generateModeLogicForShapePartIncorrectPolicy(Context context, Map<String,StringList> mpShapePartInfo) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForShapePartIncorrectPolicy method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the ShapePart already processed
		StringList slProcessedObjects=new StringList();
		
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
 
		 StringList slNameRevList=mpShapePartInfo.get("NameRevList");
		 StringList slNameRev;
		 String strNameRev;
		 String strName; 
		 String strRevision; 
		 String strIsVPMVisible;
		 MapList mlObject;
		 Map mpShapePartObjInfo=new HashMap();
		 
		 IPManagement ipMgmt=new IPManagement(context);
		 
		 StringList slSelects=new StringList(1);
		 slSelects.add(DataConstants.SELECT_ATTRIBUTE_ISVPMVISIBLE);
		 
		 for(int i=0;i<slNameRevList.size();i++) {
			 strNameRev=slNameRevList.get(i);
			 VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>NameRev of Shape Part object:::"+strNameRev);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strNameRev));
			 
			 if(!slProcessedObjects.contains(strNameRev)) {
							 
				 strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_SHAPE_PART_INCORRECT_POLICY_DATA_SERVICE_NAME+strNameRev+".mql";
				 outputFile = new File(strOutputFilePath);
				 
				 try(FileWriter fileWriter =new FileWriter(outputFile)){      
			 
						 // Iterate for each Shape Part object
						 sbMqlCommands=new StringBuilder();
						 
						 //split the strNameRev string
						 slNameRev=StringUtil.split(strNameRev, DataConstants.SEPARATOR_HASH);
						 strName=slNameRev.get(0);
						 strRevision=slNameRev.get(1);
						 
						 //3. get all the info of particular Shape Part
						mlObject=ipMgmt.findObject(context, DataConstants.TYPE_SHAPE_PART, strName, strRevision,slSelects);
							
						if(!mlObject.isEmpty()) {
								mpShapePartObjInfo=(Map)mlObject.get(0);
							}else {
								VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Object "+strName+" "+ strRevision+" does not exist in database");
							}
													
						 if(!mpShapePartObjInfo.isEmpty()) {
							//trig off
							 sbMqlCommands.append("trigger off;\n");
							 
							strIsVPMVisible=(String)mpShapePartObjInfo.get(DataConstants.SELECT_ATTRIBUTE_ISVPMVISIBLE);
							
							commandToUpdatePolicy(DataConstants.TYPE_SHAPE_PART, strName, strRevision, DataConstants.POLICY_EC_PART, sbMqlCommands);

							if(DataConstants.CONSTANT_FALSE.equalsIgnoreCase(strIsVPMVisible)) {
								//generate mql commands for modifying the isVPMVisible value to True
								commandToUpdateAttrValue(DataConstants.TYPE_SHAPE_PART, strName, strRevision, DataConstants.ATTRIBUTE_ISVPMVISIBLE,
										DataConstants.CONSTANT_TRUE.toUpperCase(), sbMqlCommands);
							}
								
							 sbMqlCommands.append("trigger on;");
							 //add the name in the slProcessedNames list
							 slProcessedObjects.add(strNameRev);
					 
							 //add the mql commands to the output file
							 fileWriter.write(sbMqlCommands.toString());
							 System.out.println("\n Output mql File is created at:"+strOutputFilePath);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
						 }
					 } //try loop
			 }
		 }//for loop
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForShapePartIncorrectPolicy method");
	}

	/**
	 * Method to generate mql command to update attribute value
	 * @param strType
	 * @param strName
	 * @param strRevision
	 * @param strNewValue
	 * @param sbMqlCommands
	 */
	private void commandToUpdateAttrValue(String strType, String strName, String strRevision, String strAttrName, String strNewValue, StringBuilder sbMqlCommands) {
		sbMqlCommands.append("# Update the attribute value  \n");
		sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append("\""+strRevision+"\" ");
		sbMqlCommands.append("\""+strAttrName+"\" ");
		sbMqlCommands.append("\""+strNewValue+"\";");
		sbMqlCommands.append("\n");
	}

	/**
	 * Method to generate mql command to update the policy
	 * @param strType
	 * @param strName
	 * @param strRevision
	 * @param strNewPolicy
	 * @param sbMqlCommands
	 */
	private void commandToUpdatePolicy(String strType, String strName, String strRevision, String strNewPolicy,	StringBuilder sbMqlCommands) {
		sbMqlCommands.append("# Update the policy \n");
		sbMqlCommands.append(DataConstants.CONST_MOD_BUS);
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append("\""+strRevision+"\" policy ");
		sbMqlCommands.append("\""+strNewPolicy+"\";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method which would be invoked for generate mode for ECPart data without template cleanup script
	 * @param context
	 * @param Map mpECPartInfo
	 * @throws MatrixException 
	 * @throws IOException 
	*/
	public void generateModeLogicForECPartWithoutTemplateData(Context context,Map<String,StringList> mpECPartInfo) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForECPartWithoutTemplateData method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the objects already processed
		StringList slProcessedObjects=new StringList();
				
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
		 
		String strTypeNameRev;
		String strTemplateId;
		String strType;
		StringList slTypeNameRevList=mpECPartInfo.get("TypeNameRevList");
		StringList slTypeNameRev;
		Map mpTemplateIdList=new HashMap();
				 
		for(int i=0;i<slTypeNameRevList.size();i++) {
			strTypeNameRev=slTypeNameRevList.get(i);
			VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>TypeNameRev of object:::"+strTypeNameRev);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strTypeNameRev));
					 
			if(!slProcessedObjects.contains(strTypeNameRev)) {
									 
				strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_EC_PART_WITHOUT_TEMPLATE_SERVICE_NAME+strTypeNameRev+".mql";
				outputFile = new File(strOutputFilePath);
				try(FileWriter fileWriter =new FileWriter(outputFile)){      
					 
					// Iterate for each object
					sbMqlCommands=new StringBuilder();
								 
					//split the strNameRev string
					slTypeNameRev=StringUtil.split(strTypeNameRev, DataConstants.SEPARATOR_HASH);
					strType=slTypeNameRev.get(0);
					
					//get the TemplateId
					if(mpTemplateIdList.containsKey(strType)) {
						strTemplateId=(String) mpTemplateIdList.get(strType);
						VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>strTemplateId from Map:::"+strTemplateId);
					}else {
						strTemplateId=CommonUtility.getTemplateObjectId(context, strType, DataConstants.POLICY_EC_PART);
						VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>strTemplateId map from database:::"+strTemplateId);
					}
					
					if(UIUtil.isNotNullAndNotEmpty(strTemplateId)) {
						
						if(!mpTemplateIdList.containsKey(strType))
							mpTemplateIdList.put(strType, strTemplateId);
						
						//trig off
						sbMqlCommands.append("trigger off;\n");
										
						//generate mql commands for connecting the Template to EC Part
						commandToConnectTemplate(sbMqlCommands, strType,slTypeNameRev.get(1),slTypeNameRev.get(2),strTemplateId);
										 
						sbMqlCommands.append("trigger on;");
					}
					
					//add the name in the slProcessedNames list
					slProcessedObjects.add(strTypeNameRev);
							 
					//add the mql commands to the output file
					fileWriter.write(sbMqlCommands.toString());
					System.out.println("\n Output mql File is created at:"+strOutputFilePath);
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
				} //try loop
			}
		}//for loop
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForECPartWithoutTemplateData method");
	}
	
	/**
	 * Method to generate mql command to connect the Templates to the object
	 * @param sbMqlCommands
	 * @param strType
	 * @param strName
	 * @param strRevision
	 * @param strIPClassName
	 */
	private void commandToConnectTemplate(StringBuilder sbMqlCommands, String strType, String strName, String strRevision, String strTemplateId) {
		sbMqlCommands.append("# Connect Template object \n");
		sbMqlCommands.append("connect bus ");
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append("\""+strRevision+"\"");
		sbMqlCommands.append(" relationship ");
		sbMqlCommands.append("\""+DataConstants.RELATIONSHIP_TEMPLATE+"\"");
		sbMqlCommands.append(" to ");
		sbMqlCommands.append("\""+strTemplateId+"\";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method to find the Shape Part data governed by pgPKGWIPPart policy
	 * @param context
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	public MapList findToDeleteData(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findToDeleteData method");
		
		StringList slObjSelects=new StringList(8);
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		
		MapList mlToDeleteData = DomainObject.findObjects(context,
				DomainConstants.QUERY_WILDCARD, //typepattern
				"*ToDelete:*",  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DomainConstants.QUERY_WILDCARD,  // vault pattern
                "",                                       // where exp
                false,										//expandType
                slObjSelects);						//selectables

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found ::"+mlToDeleteData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findToDeleteData method");
		return mlToDeleteData;
	}
	
	/**
	 * Method to write the information of the objects to CSV File
	 * @param context
	 * @param mlData
	 * @param strLevel
	 * @throws MatrixException
	 * @throws IOException
	 */
	public void writeToDeleteDataInCSVFile(Context context,MapList mlData) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START writeToDeleteDataInCSVFile method");
		String strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_TO_DELETE_DATA_SERVICE_NAME+"Scan.csv";
		java.io.File outputFile = new java.io.File(strOutputFilePath);
		
		String strHeader="Type\tName\tRevision\tObjectId\tOwner\tState\tOriginated\tModified";
		String strExpectedDataInInputFile="Type\tName\tRevision";
		
		try(FileWriter fileWriter =new FileWriter(outputFile)){      
			
			//header row
			 fileWriter.write(strHeader);
			 fileWriter.write("\n");
			 
			 Map mpData;
			 StringBuilder sbRowData;
			 String strType;
			 
			 for(int i=0;i<mlData.size();i++) {
				 sbRowData=new StringBuilder();
				 mpData=(Map)mlData.get(i);
				 
				 strType=(String)mpData.get(DomainConstants.SELECT_TYPE);
				 
				 sbRowData.append(strType).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_NAME)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_REVISION)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ID)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_OWNER)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_CURRENT)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_ORIGINATED)).append("\t");
				 sbRowData.append(mpData.get(DomainConstants.SELECT_MODIFIED)).append("\t");
					 
				 fileWriter.write(sbRowData.toString());
				 fileWriter.write("\n");
			 }
		}
		System.out.println("\n CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>CSV File is generated at::"+strOutputFilePath);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END writeToDeleteDataInCSVFile method");
		generateInputFileForGenerateMode(context, outputFile, strHeader, strExpectedDataInInputFile, DataConstants.CONST_TO_DELETE_DATA_SERVICE_NAME);
	}
	
	/**
	 * Method which would be invoked for generate mode for ToDelete data cleanup script
	 * @param context
	 * @param Map mpToDeleteDataInfo
	 * @throws MatrixException 
	 * @throws IOException 
	*/
	public void generateModeLogicForToDeleteData(Context context,Map<String,StringList> mpToDeleteDataInfo) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForToDeleteData method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the objects already processed
		StringList slProcessedObjects=new StringList();
				
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
		 
		String strTypeNameRev;
		String strFileName;;
		StringList slTypeNameRevList=mpToDeleteDataInfo.get("TypeNameRevList");
		StringList slTypeNameRev;
				 
		for(int i=0;i<slTypeNameRevList.size();i++) {
			strTypeNameRev=slTypeNameRevList.get(i);
			VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>TypeNameRev of object:::"+strTypeNameRev);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strTypeNameRev));
					 
			if(!slProcessedObjects.contains(strTypeNameRev)) {
									
				//since the name of the object contains : symbol, need to remove that before adding it in filename,
				strFileName=strTypeNameRev;
				strFileName=strFileName.replace(DataConstants.SEPARATOR_COLON, "");
				strFileName=strFileName.replace(DataConstants.BACKWARD_SLASH, "");
				strFileName=strFileName.replace(DataConstants.FORWARD_SLASH, "");
				VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>strFileName:::"+strFileName);
				strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_TO_DELETE_DATA_SERVICE_NAME+strFileName+".mql";
				outputFile = new File(strOutputFilePath);
				try(FileWriter fileWriter =new FileWriter(outputFile)){      
					 
					// Iterate for each object
					sbMqlCommands=new StringBuilder();
								 
					//split the strNameRev string
					slTypeNameRev=StringUtil.split(strTypeNameRev, DataConstants.SEPARATOR_HASH);
					
					//trig off
					sbMqlCommands.append("trigger off;\n");
										
					//generate mql commands for deleting the object
					commandToDeleteData(sbMqlCommands, slTypeNameRev.get(0),slTypeNameRev.get(1),slTypeNameRev.get(2));
										 
					sbMqlCommands.append("trigger on;");
					
					//add the name in the slProcessedNames list
					slProcessedObjects.add(strTypeNameRev);
							 
					//add the mql commands to the output file
					fileWriter.write(sbMqlCommands.toString());
					System.out.println("\n Output mql File is created at:"+strOutputFilePath);
					VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
				} //try loop
			}
		}//for loop
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForToDeleteData method");
	}

	/**
	 * Method to generate mql command to delete data
	 * @param sbMqlCommands
	 * @param strType
	 * @param strName
	 * @param strRevision
	 */
	private void commandToDeleteData(StringBuilder sbMqlCommands, String strType, String strName, String strRevision) {
		sbMqlCommands.append("# Delete data \n");
		sbMqlCommands.append("delete bus ");
		sbMqlCommands.append("\""+strType+"\" ");
		sbMqlCommands.append("\""+strName+"\" ");
		sbMqlCommands.append("\""+strRevision+"\";");
		sbMqlCommands.append("\n");
	}
	
	/**
	 * Method which would be invoked for generate mode for No Design Domain cleanup script
	 * @param context
	 * @param Map mpVPMReferenceInfo
	 * @throws MatrixException 
	 * @throws IOException 
	 */
	public void generateModeLogicForNoDesignDomainInterface(Context context, Map<String,StringList> mpVPMReferenceInfo) throws MatrixException, IOException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START generateModeLogicForNoDesignDomainInterface method");
		//1. Create file, which would hold the mql commands to be executed for rectification of data
		String strOutputFilePath;
		File outputFile;
		//2. Create a StringList which would hold the names of the VPMReference already processed
		StringList slProcessedObjects=new StringList();
		
		//Create a StringBuffer which would hold all the mql commands
		StringBuilder sbMqlCommands;
 
		 String strNameRev;
		 Map mpVPMReferenceObjInfo;
		 StringList slNameRevList=mpVPMReferenceInfo.get("NameRevList");
		 StringList slNameRev;
		 String strECPartType;
		 String strObjectId;
		 String strDesignDomain;
		 String strDesignDomainFromECPartType="";
		 String strInterfaceName;
		 boolean bInterfaceAdded=false;
		 
		 StringList slSelects=new StringList(2);
		 slSelects.add(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
		 slSelects.add(DataConstants.SELECT_ATTRIBUTE_PGENTERPRISETYPE);
		 slSelects.add(DomainConstants.SELECT_ID);
		 slSelects.add("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
		 
		 InterfaceManagement interfaceMgmt=new InterfaceManagement(context);
		 
		 for(int i=0;i<slNameRevList.size();i++) {
			 strInterfaceName="";
			 strNameRev=slNameRevList.get(i);
			 VPLMIntegTraceUtil.trace(context,"\n >>>>>>>>>>>>>>>>>NameRev of VPMReference object:::"+strNameRev);
			 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Is object already processed:::"+slProcessedObjects.contains(strNameRev));
			 
			 if(!slProcessedObjects.contains(strNameRev)) {
							 
				 strOutputFilePath=context.createWorkspace() + java.io.File.separator + DataConstants.CONST_NO_DESIGN_DOMAIN_INTERFACE_SERVICE_NAME+strNameRev+".mql";
				 outputFile = new File(strOutputFilePath);
				 try(FileWriter fileWriter =new FileWriter(outputFile)){      
			 
						 // Iterate for each object
						 sbMqlCommands=new StringBuilder();
						 
						 //split the strNameRev string
						 slNameRev=StringUtil.split(strNameRev, DataConstants.SEPARATOR_HASH);
						 //3. get all the info of particular VPMReference
						mpVPMReferenceObjInfo=getInfoOfVPMReference(context,slNameRev.get(0),slNameRev.get(1),slSelects);
													
						 if(!mpVPMReferenceObjInfo.isEmpty()) {
							//trig off
							 sbMqlCommands.append("trigger off;\n");
							 
							strECPartType=(String)mpVPMReferenceObjInfo.get("to["+DataConstants.REL_PART_SPECIFICATION+"].from.type");
							strObjectId=(String)mpVPMReferenceObjInfo.get(DomainConstants.SELECT_ID);
							strDesignDomain=(String)mpVPMReferenceObjInfo.get(DataConstants.SELECT_ATTRIBUTE_DESIGN_DOMAIN);
						
							if(UIUtil.isNotNullAndNotEmpty(strDesignDomain)) {
								 if(!DataConstants.CONSTANT_DESIGN_DOMAIN_APP_VPMREFERENCE.equalsIgnoreCase(strDesignDomain))
									 strInterfaceName="pngi"+strDesignDomain;
								 else
									 strInterfaceName="pngi"+DataConstants.CONSTANT_DESIGN_FOR_ASSEMBLED;
							 }
							else if(UIUtil.isNotNullAndNotEmpty(strECPartType) && DataConstants.VALID_SYNC_TYPES.contains(strECPartType)) {
								 strDesignDomainFromECPartType=getDesignDomainForECPartType(strECPartType);
								 strInterfaceName="pngi"+strDesignDomainFromECPartType;
							 }
								
							VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>strECPartType:::"+strECPartType+" strDesignDomain::"+strDesignDomain+" strInterfaceName::"+strInterfaceName);
							
							 bInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strObjectId, strInterfaceName);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>bInterfaceAdded:::"+bInterfaceAdded);
							 
							 if(!bInterfaceAdded) {
								 commandToAddInterfaceOnVPMReference(sbMqlCommands, slNameRev.get(0),slNameRev.get(1), strInterfaceName);
							 }
							 
							 if(UIUtil.isNullOrEmpty(strDesignDomain) && UIUtil.isNotNullAndNotEmpty(strDesignDomainFromECPartType)) {
								 bInterfaceAdded= interfaceMgmt.checkInterfaceOnObject(context, strObjectId, DataConstants.INTERFACE_PNG_DESIGNPART);
								 if(!bInterfaceAdded) {
									 commandToAddInterfaceOnVPMReference(sbMqlCommands, slNameRev.get(0),slNameRev.get(1), DataConstants.INTERFACE_PNG_DESIGNPART);
								 }
								 //generate mql commands for modifying the Design Domain
								 commandToUpdateDesignDomainForVPMReference(sbMqlCommands, slNameRev.get(0),slNameRev.get(1),strDesignDomainFromECPartType);
							 }
							 						 
							 sbMqlCommands.append("trigger on;");
							 //add the name in the slProcessedNames list
							 slProcessedObjects.add(strNameRev);
					 
							 //add the mql commands to the output file
							 fileWriter.write(sbMqlCommands.toString());
							 System.out.println("\n Output mql File is created at:"+strOutputFilePath);
							 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Output mql File is created at:"+strOutputFilePath);
						 }
					 } //try loop
			 }
		 }//for loop
		 VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END generateModeLogicForNoDesignDomainInterface method");
	}
}

