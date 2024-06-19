package com.pg.designtools.migration.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dassault_systemes.enovia.versioning.util.ENOVersioningException;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.DataConstants.customWorkProcessD2SExceptions;
import com.pg.designtools.migration.IDTMigration;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class ProductDTAnalyzer implements IDTMigration{

	protected String strTitleMode;
	private File messageResponseFile=null;
	protected StringBuilder sbMessageResponseData;
	protected String strValidVPMReferenceObject;
	protected String strValidECPartObject;
	protected String strVPMRefType;
	protected String strECPartType;
	protected IVersioningStrategy _ecVersioning;
	protected IVersioningStrategy _vpmRefVersioning;
	protected DomainObject doVPMReferenceObject;
	protected DomainObject doECPartObject;
	protected boolean bIsContextPushed;
	private static final Logger logger = LoggerFactory.getLogger("DT_MIGRATION"); 
	
	public  ProductDTAnalyzer(String strModeOfTitle) {
		_ecVersioning = new CheckRevision();
		_vpmRefVersioning = new CheckEvolution();
		sbMessageResponseData=new StringBuilder();
		strValidVPMReferenceObject=DataConstants.CONSTANT_FALSE;
		strValidECPartObject=DataConstants.CONSTANT_FALSE;
		doVPMReferenceObject=new DomainObject();
		doECPartObject=new DomainObject();
		bIsContextPushed=false;
		strTitleMode=strModeOfTitle;
	}
	
	@Override
	public MapList init(Context context,String strInputFilePath) throws IOException, MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTAnalyzer: init method");
		logger.debug(">>> START of ProductDTAnalyzer: init method");
		ObjectFetcher objFetcher=new ObjectFetcher(strInputFilePath);
		return objFetcher.getObjects(context, strInputFilePath);
	}
	
	/**
	 * Method to validate access on the object, for the mentioned person
	 * @param context
	 * @param doObject
	 * @return boolean
	 * @throws FrameworkException
	 */
	private Map validateOwnerAccess(Context context, DomainObject doObject) throws FrameworkException { 
		VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTAnalyzer: validateOwnerAccess method");
		logger.debug(">>> START of ProductDTAnalyzer: validateOwnerAccess method");
		Map mpOwnerAccess=new HashMap();
		String strError="";
		boolean bHasAccess=false;
		VPLMIntegTraceUtil.trace(context, ">>> doObject:::"+doObject);
		logger.debug(">>> doObject:::"+doObject);
		
		if(null!=doObject) {
			
			StringList slSelect=new StringList(2);
			slSelect.add(DomainConstants.SELECT_TYPE);
			slSelect.add(DomainConstants.SELECT_CURRENT);
			
			Map mpData=doObject.getInfo(context,slSelect);
			
			String strType=(String) mpData.get(DomainConstants.SELECT_TYPE);
			String strCurrent=(String) mpData.get(DomainConstants.SELECT_CURRENT);
			
			VPLMIntegTraceUtil.trace(context, ">>> strType:::"+strType+" strCurrent::"+strCurrent);
			logger.debug( ">>> strType:::"+strType+" strCurrent::"+strCurrent);
			
			StringList slAccessList=new StringList();
			if(strType.equals(DataConstants.TYPE_VPMREFERENCE)) {
				slAccessList.add("read");
				slAccessList.add("show");
			}else {
				slAccessList.add("revise");
			}
			VPLMIntegTraceUtil.trace(context, ">>> slAccessList:::"+slAccessList);
			logger.debug( ">>> slAccessList:::"+slAccessList);
			
			bHasAccess = FrameworkUtil.hasAccess(context, doObject, slAccessList);
			
			//if the MPMP data is in In Work state, then revise access is not present.
			if(!DataConstants.TYPE_VPMREFERENCE.equals(strType) && DataConstants.STATE_PRELIMINARY.equals(strCurrent))
				bHasAccess=true;
			
			VPLMIntegTraceUtil.trace(context, ">>> bHasAccess:::"+bHasAccess);
			logger.debug(">>> bHasAccess:::"+bHasAccess);
			
			if(!bHasAccess) {
				DataConstants.customWorkProcessD2SExceptions errorNoAccessForOwner=customWorkProcessD2SExceptions.ERROR_400_ACCESS_MISSING;
				strError=errorNoAccessForOwner.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorNoAccessForOwner.getExceptionMessage()+" on "+strType+" object";
				VPLMIntegTraceUtil.trace(context, errorNoAccessForOwner.getExceptionMessage());
			}
		}
		mpOwnerAccess.put("access", bHasAccess);
		mpOwnerAccess.put(DataConstants.STR_ERROR, strError);
		
		VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTAnalyzer: validateOwnerAccess method");
		logger.debug( "<<< End of ProductDTAnalyzer: validateOwnerAccess method");
		
		return mpOwnerAccess;
	}

	/**
	 * Method for processing the objects
	 * @param context
	 * @param doObject
	 * @param strType
	 * @throws MatrixException, ENOVersioningException
	 * @return String
	 */
	@Override
	public Map process(Context context,DomainObject doObject,String strType) throws MatrixException, ENOVersioningException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of ProductDTAnalyzer: process method");
		VPLMIntegTraceUtil.trace(context, ">>> ProductDTAnalyzer : process method _vpmRefVersioning.getClass()::"+_vpmRefVersioning.getClass());
		VPLMIntegTraceUtil.trace(context, ">>> ProductDTAnalyzer : process method _ecVersioning.getClass()::"+_ecVersioning.getClass());
		
		logger.debug(">>> Start of ProductDTAnalyzer: process method");
		logger.debug( ">>> ProductDTAnalyzer : process method _vpmRefVersioning.getClass()::"+_vpmRefVersioning.getClass());
		logger.debug(">>> ProductDTAnalyzer : process method _ecVersioning.getClass()::"+_ecVersioning.getClass());
		
		Map mpResult;
		VPLMIntegTraceUtil.trace(context, ">>>strType:::"+strType);
		logger.debug( ">>>strType:::"+strType);
			
		if(DataConstants.TYPE_VPMREFERENCE.equals(strType)) {
				mpResult=_vpmRefVersioning.execute(context, doObject);
		}else {
			mpResult=_ecVersioning.execute(context, doObject);
		}
		VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTAnalyzer: process method");
		logger.debug( "<<< End of ProductDTAnalyzer: process method");
		return mpResult;
	}

	@Override
	public void updateObjects(Context context, String strNewVPMReferenceObjectId, String strNewECPartObjectId)  throws MatrixException, ENOVersioningException, Exception{
	}

	@Override
	public void setOutputPath(String strOutputFilePath,String strInputFilePath) {
		if(UIUtil.isNullOrEmpty(strOutputFilePath)) {
			//get the path from input file path
			strOutputFilePath=strInputFilePath.substring(0, strInputFilePath.lastIndexOf(File.separator));
		}
		messageResponseFile=new File(strOutputFilePath+File.separator+"PGDTResponseMessage.csv");
	}

	@Override
	public void getOutput() throws IOException {
		// write the string to the output file
		try (FileWriter fileWriter = new FileWriter(messageResponseFile)){
			fileWriter.write(sbMessageResponseData.toString());
		}
	}
	
	/**
	 * Method to change the owner
	 * @param context
	 * @param strOwner
	 * @throws MatrixException
	 */
	private void changeOwner(Context context,String strOwner) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTAnalyzer:changeOwner method");
		logger.debug(">>> START of ProductDTAnalyzer:changeOwner method");
		
		ContextUtil.pushContext(context, strOwner, DomainConstants.EMPTY_STRING,DomainConstants.EMPTY_STRING);
		if(context.isContextSet()) {
			context.resetRole("ctx::CATIADesigner.PG.Internal_PG");
		}else{
			context.setRole("ctx::CATIADesigner.PG.Internal_PG");
		}
		VPLMIntegTraceUtil.trace(context, ">>> context is pushed");
		VPLMIntegTraceUtil.trace(context, "<<< END of ProductDTAnalyzer:changeOwner method");
		logger.debug( ">>> context is pushed to "+strOwner);
		logger.debug("<<< END of ProductDTAnalyzer:changeOwner method");
	}
	
	/**
	 * Method to generate the Input string as per the TNR of EC Part and VPMReference object
	 * @param context
	 * @param mpVPMRefObjInfo
	 * @param mpECPartObjInfo
	 * @param strOwner 
	 * @return String
	 */
	private String generateInputString(Context context,Map mpVPMRefObjInfo,Map mpECPartObjInfo,String strOwner) {
		VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTAnalyzer:generateInputString method");
		logger.debug( ">>> START of ProductDTAnalyzer:generateInputString method");
		
		StringBuilder sbInputString=new StringBuilder();
		sbInputString.append(mpECPartObjInfo.get(DomainConstants.SELECT_TYPE)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpECPartObjInfo.get(DomainConstants.SELECT_NAME)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpECPartObjInfo.get(DomainConstants.SELECT_REVISION)).append(DataConstants.SEPARATOR_COMMA);
		
		sbInputString.append(mpVPMRefObjInfo.get(DomainConstants.SELECT_TYPE)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpVPMRefObjInfo.get(DomainConstants.SELECT_NAME)).append(DataConstants.SEPARATOR_COMMA);
		sbInputString.append(mpVPMRefObjInfo.get(DomainConstants.SELECT_REVISION)).append(DataConstants.SEPARATOR_COMMA);
		
		sbInputString.append(strOwner).append(DataConstants.SEPARATOR_COMMA);
		
		VPLMIntegTraceUtil.trace(context, "<<< END of ProductDTAnalyzer:generateInputString method");
		logger.debug("<<< END of ProductDTAnalyzer:generateInputString method");
		return sbInputString.toString();
	}
	
	/**
	 * Method to process the objects mentioned by user in the input file
	 * @param context
	 * @param mpObjDetails
	 * @param int i
	 * @param strMode
	 * @throws Exception 
	 */
	@Override
	public void processObjects(Context context,Map mpObjDetails,String strMode) throws Exception  {
		VPLMIntegTraceUtil.trace(context, ">>> START of ProductDTAnalyzer:processObjects method");
		logger.debug( ">>> START of ProductDTAnalyzer:processObjects method");
		String strOwner;
		String strInput;
		String strError;
		String strHeader;
		String strErrorForValidObjects="";
		String strOwnerAccessForECPartError="";
		String strVPMRefError="";
		String strECPartError="";
		Map mpVPMRefObjInfo;
		Map mpECPartObjInfo;
		Map mpOwnerAccessOnVPMRef;
		Map mpOwnerAccessOnECPart;
		Map mpVPMRefObject;
		Map mpECPartObject;
		boolean bHasAccessonVPMRef=false;
		boolean bHasAccessonECPart=false;
		DataConstants.customWorkProcessD2SExceptions errorVPMRefNotValidObj=customWorkProcessD2SExceptions.ERROR_400_VPMREFERENCE_NOT_VALID;
		DataConstants.customWorkProcessD2SExceptions errorECPartNotValidObj=customWorkProcessD2SExceptions.ERROR_400_ECPART_NOT_VALID;
		DataConstants.customWorkProcessD2SExceptions msgVPMRefValidObj=customWorkProcessD2SExceptions.MESSAGE_200_VPMREFERENCE_VALID;
		DataConstants.customWorkProcessD2SExceptions msgECPartValidObj=customWorkProcessD2SExceptions.MESSAGE_200_ECPART_VALID;
		
		doVPMReferenceObject=(DomainObject) mpObjDetails.get(DataConstants.CONSTANT_VPMREFERENCE_OBJECT);
		doECPartObject=(DomainObject)mpObjDetails.get(DataConstants.CONSTANT_ECPART_OBJECT);
		strOwner=(String) mpObjDetails.get(DataConstants.CONSTANT_OWNER_KEY);
		strInput=(String) mpObjDetails.get(DataConstants.CONSTANT_INPUT);
		strError=(String)mpObjDetails.get(DataConstants.STR_ERROR);
		strHeader=(String)mpObjDetails.get(DataConstants.CONSTANT_HEADER);
		
		bIsContextPushed=false;
		strValidVPMReferenceObject=DataConstants.CONSTANT_FALSE;
		strValidECPartObject=DataConstants.CONSTANT_FALSE;
			
		VPLMIntegTraceUtil.trace(context, ">>> doVPMReferenceObject:::"+doVPMReferenceObject);
		VPLMIntegTraceUtil.trace(context, ">>> doECPartObject:::"+doECPartObject);
		VPLMIntegTraceUtil.trace(context, ">>> strOwner:::"+strOwner);
		VPLMIntegTraceUtil.trace(context, ">>> strInput:::"+strInput);
		VPLMIntegTraceUtil.trace(context, ">>> strError:::"+strError);
		VPLMIntegTraceUtil.trace(context, ">>> context.getUser():::"+context.getUser());
		
		logger.debug(">>> doVPMReferenceObject:::"+doVPMReferenceObject);
		logger.debug( ">>> doECPartObject:::"+doECPartObject);
		logger.debug( ">>> strOwner:::"+strOwner);
		logger.debug( ">>> strInput:::"+strInput);
		logger.debug( ">>> strError:::"+strError);
		logger.debug( ">>> context.getUser():::"+context.getUser());
			
		StringList slObjSelects=new StringList(3);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
			
		try {
			
				if(UIUtil.isNullOrEmpty(sbMessageResponseData.toString()))
					sbMessageResponseData.append(strHeader);
				
				if(UIUtil.isNotNullAndNotEmpty(strOwner)) {
					//need to pushContext as the owner mentioned in input file needs to perform all operations.
					if(!context.getUser().equals(strOwner)) {
						changeOwner(context,strOwner);
						bIsContextPushed = true;
					}
					
					if(null!=doVPMReferenceObject && null!=doECPartObject) {
						
						mpVPMRefObjInfo=doVPMReferenceObject.getInfo(context, slObjSelects);
						mpECPartObjInfo=doECPartObject.getInfo(context, slObjSelects);
						
						VPLMIntegTraceUtil.trace(context, ">>> mpVPMRefObjInfo:::"+mpVPMRefObjInfo);
						VPLMIntegTraceUtil.trace(context, ">>> mpECPartObjInfo:::"+mpECPartObjInfo);
						
						logger.debug(">>> mpVPMRefObjInfo:::"+mpVPMRefObjInfo);
						logger.debug(">>> mpECPartObjInfo:::"+mpECPartObjInfo);
						
						strVPMRefType=(String)mpVPMRefObjInfo.get(DomainConstants.SELECT_TYPE);
						strECPartType=(String)mpECPartObjInfo.get(DomainConstants.SELECT_TYPE);
						
						VPLMIntegTraceUtil.trace(context, ">>> strVPMRefType:::"+strVPMRefType+" strECPartType::"+strECPartType);
						logger.debug( ">>> strVPMRefType:::"+strVPMRefType+" strECPartType::"+strECPartType);
						
						strInput=generateInputString(context,mpVPMRefObjInfo,mpECPartObjInfo,strOwner);
						
						VPLMIntegTraceUtil.trace(context, ">>> Input string created with TNR details:::"+strInput);
						logger.debug( ">>> Input string created with TNR details:::"+strInput);
				
						if(UIUtil.isNotNullAndNotEmpty(sbMessageResponseData.toString()))
							sbMessageResponseData.append("\n");
						
						sbMessageResponseData.append(strInput);
						
						mpOwnerAccessOnVPMRef=validateOwnerAccess(context, doVPMReferenceObject);
						bHasAccessonVPMRef=(boolean) mpOwnerAccessOnVPMRef.get("access");
						strErrorForValidObjects=(String)mpOwnerAccessOnVPMRef.get(DataConstants.STR_ERROR);
						
						logger.debug(">>> ProductDTAnalyzer:processObjects bHasAccessonVPMRef:::"+bHasAccessonVPMRef);
						VPLMIntegTraceUtil.trace(context, ">>> ProductDTAnalyzer:processObjects bHasAccessonVPMRef:::"+bHasAccessonVPMRef);
						
						if(bHasAccessonVPMRef) {
							mpVPMRefObject=process(context, doVPMReferenceObject,strVPMRefType);
							strValidVPMReferenceObject=(String) mpVPMRefObject.get("validRev");
							strVPMRefError=(String)mpVPMRefObject.get(DataConstants.STR_ERROR);
							
							if(UIUtil.isNotNullAndNotEmpty(strErrorForValidObjects) && UIUtil.isNotNullAndNotEmpty(strVPMRefError)) {
								strErrorForValidObjects+=DataConstants.SEPARATOR_PIPE+strVPMRefError;
							}else {
								strErrorForValidObjects+=strVPMRefError;
							}
						}
							
						VPLMIntegTraceUtil.trace(context, ">>> ProductDTAnalyzer:processObjects strValidVPMReferenceObject:::"+strValidVPMReferenceObject);
						logger.debug(">>> ProductDTAnalyzer:processObjects strValidVPMReferenceObject:::"+strValidVPMReferenceObject);
						
						mpOwnerAccessOnECPart=validateOwnerAccess(context, doECPartObject);
						strOwnerAccessForECPartError=(String)mpOwnerAccessOnECPart.get(DataConstants.STR_ERROR);
						bHasAccessonECPart=(boolean) mpOwnerAccessOnECPart.get("access");
						
						if(UIUtil.isNotNullAndNotEmpty(strErrorForValidObjects) && UIUtil.isNotNullAndNotEmpty(strOwnerAccessForECPartError)) {
							strErrorForValidObjects+=DataConstants.SEPARATOR_PIPE+strOwnerAccessForECPartError;
						}else {
							strErrorForValidObjects+=strOwnerAccessForECPartError;
						}
						VPLMIntegTraceUtil.trace(context, ">>> ProductDTAnalyzer:processObjects bHasAccessonECPart:::"+bHasAccessonECPart);
						logger.debug(">>> ProductDTAnalyzer:processObjects bHasAccessonECPart:::"+bHasAccessonECPart);
						
						if(bHasAccessonECPart) {
							mpECPartObject=process(context, doECPartObject,strECPartType);
							strValidECPartObject=(String) mpECPartObject.get("validRev");
							strECPartError=(String)mpECPartObject.get(DataConstants.STR_ERROR);
							
							if(UIUtil.isNotNullAndNotEmpty(strErrorForValidObjects) && UIUtil.isNotNullAndNotEmpty(strECPartError)) {
								strErrorForValidObjects+=DataConstants.SEPARATOR_PIPE+strECPartError;
							}else {
								strErrorForValidObjects=strECPartError;
							}
						}

						VPLMIntegTraceUtil.trace(context, ">>> ProductDTAnalyzer:processObjects strValidECPartObject:::"+strValidECPartObject);
						logger.debug(">>> ProductDTAnalyzer:processObjects strValidECPartObject:::"+strValidECPartObject);
						
						if(UIUtil.isNullOrEmpty(strValidVPMReferenceObject))
								strValidVPMReferenceObject=DataConstants.CONSTANT_FALSE;
						
						if(UIUtil.isNullOrEmpty(strValidECPartObject))
							strValidECPartObject=DataConstants.CONSTANT_FALSE;
						
						VPLMIntegTraceUtil.trace(context, ">>> ProductDTAnalyzer:processObjects strValidVPMReferenceObject:::"+strValidVPMReferenceObject+" strValidECPartObject::"+strValidECPartObject);
						logger.debug(">>> ProductDTAnalyzer:processObjects strValidVPMReferenceObject:::"+strValidVPMReferenceObject+" strValidECPartObject::"+strValidECPartObject);
						
						if(Boolean.parseBoolean(strValidVPMReferenceObject)) {
							VPLMIntegTraceUtil.trace(context,msgVPMRefValidObj.getExceptionMessage());
						}else {
							sbMessageResponseData.append("Error"+DataConstants.SEPARATOR_COMMA);
							sbMessageResponseData.append(strErrorForValidObjects);
							VPLMIntegTraceUtil.trace(context,errorVPMRefNotValidObj.getExceptionMessage());
						}
						
						if(Boolean.parseBoolean(strValidECPartObject)) {
							VPLMIntegTraceUtil.trace(context,msgECPartValidObj.getExceptionMessage());
						}else {
							VPLMIntegTraceUtil.trace(context,errorECPartNotValidObj.getExceptionMessage());
						}
						
						if(Boolean.parseBoolean(strValidVPMReferenceObject) && !Boolean.parseBoolean(strValidECPartObject)) {
							sbMessageResponseData.append("Error"+DataConstants.SEPARATOR_COMMA);
							sbMessageResponseData.append(strErrorForValidObjects);
						}
						
						if("Scan".equalsIgnoreCase(strMode) && Boolean.parseBoolean(strValidVPMReferenceObject) && Boolean.parseBoolean(strValidECPartObject)) {
							sbMessageResponseData.append("Scanned").append(DataConstants.SEPARATOR_COMMA);
							sbMessageResponseData.append(msgVPMRefValidObj.getExceptionCode()).append(DataConstants.SEPARATOR_COLON);
							sbMessageResponseData.append(msgVPMRefValidObj.getExceptionMessage()).append(DataConstants.SEPARATOR_PIPE);
							sbMessageResponseData.append(msgECPartValidObj.getExceptionCode()).append(DataConstants.SEPARATOR_COLON);
							sbMessageResponseData.append(msgECPartValidObj.getExceptionMessage());
						}
						
					}else {
						if(UIUtil.isNotNullAndNotEmpty(sbMessageResponseData.toString()))
							sbMessageResponseData.append("\n");
						
						sbMessageResponseData.append(strInput);
						sbMessageResponseData.append(DataConstants.SEPARATOR_COMMA).append("Error").append(DataConstants.SEPARATOR_COMMA);
						sbMessageResponseData.append(strError);
					}
				}else {
					DataConstants.customWorkProcessD2SExceptions errorOwnerNameMissing=customWorkProcessD2SExceptions.ERROR_400_OWNER_NAME_MISSING;
								
					if(UIUtil.isNotNullAndNotEmpty(sbMessageResponseData.toString()))
						sbMessageResponseData.append("\n");
					
					sbMessageResponseData.append(strInput);
					sbMessageResponseData.append(DataConstants.SEPARATOR_COMMA).append("Error").append(DataConstants.SEPARATOR_COMMA);
					sbMessageResponseData.append(errorOwnerNameMissing.getExceptionCode()).append(DataConstants.SEPARATOR_COLON);
					sbMessageResponseData.append(errorOwnerNameMissing.getExceptionMessage());
				}
			}finally {
			
				if("Scan".equalsIgnoreCase(strMode) && bIsContextPushed) {
					ContextUtil.popContext(context);
					VPLMIntegTraceUtil.trace(context, ">>> context is popped");
					logger.debug(">>> context is popped");
				}
			}
			VPLMIntegTraceUtil.trace(context, "<<< End of ProductDTAnalayzer: processObjects method");
			logger.debug("<<< End of ProductDTAnalayzer: processObjects method");
	}

}
