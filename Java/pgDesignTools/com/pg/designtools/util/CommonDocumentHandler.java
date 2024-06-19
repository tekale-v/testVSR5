package com.pg.designtools.util;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.client.fcs.InputStreamSource_2;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.FileList;
import matrix.db.JPO;
import matrix.util.List;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

/**
 * Provides mechanism for handling DT specific requirements for document
 * management leveraging the core functionality of the platform class. Specific
 * file management at the OS level is performed in FileManagement class.
 * 
 * @author GQS
 *
 */

// TO DO - GQS - move doc mgmt code from pgVPDTOPStoEnovia_mxJPO checkin2 method
// type specific data handling occurs in the datamgmt layer that should wrap
// this class
// example is the StackingPatternDocument for IPMDocument
// CommonDocument majorCom holds the major obj in this jpo code
// this class should be aware of context and transaction handling as many
// methods in the platform class
// executes transaction block
// the following methods push\pop context: removeDocuments
// this class should also have non-BO specific code migrated from
// pgDTCommonDocumentExtension_mxJPO
public class CommonDocumentHandler extends CommonDocument {

	/**
	 * Allows for direct file checkin handling bypassing embedded checkin mechanism
	 * in platform class
	 */
	
	FileManagement fileCheckinHandler = new FileManagement();
	
	boolean bIsContextPushed = false;
	boolean isLocked = false;
	BusinessObject minorBo = null;

	// Reserve management
	public boolean isLocked(Context context, DomainObject domObject) throws MatrixException {
		boolean bLocked = false;
		if(domObject.isLocked(context))
		{
			bLocked=true;
			
		}
		return bLocked;
	}	
	
	public boolean isUnLocked(Context context, DomainObject domObject) throws MatrixException {
		boolean bUnlocked = false;
		if(!domObject.isLocked(context))
		{
			bUnlocked = true;
			
		}
		return bUnlocked;
	}	
	
	public boolean hasLockAccess(Context context, DomainObject domObject) throws FrameworkException
	{
		
			boolean hasLockAccess = FrameworkUtil.hasAccess(context, domObject, "lock");
			return hasLockAccess;
		
	}
	public boolean hasUnLockAccess(Context context, DomainObject domObject) throws FrameworkException
	{
		
			boolean hasUnLockAccess = FrameworkUtil.hasAccess(context, domObject, "unlock");
			return hasUnLockAccess;
		
	}
	
	public void lockDO(Context context, DomainObject domObject) throws MatrixException {
		try 
		{
			if(hasLockAccess(context,domObject)&& isUnLocked(context,domObject))
			{
				domObject.lock(context);
				isLocked=true;
			}
			else
			{
				VPLMIntegTraceUtil.trace(context, ">>> No Lock access for :: "+ domObject);
			}
		}catch (MatrixException e){
				if(isLocked)
				{
					domObject.unlock(context);
					isLocked=false;
				}
			VPLMIntegTraceUtil.trace(context, ">>> Inside Catch of lockDO method::"+e.getMessage());
		}
	}

	public void unlockDO(Context context, DomainObject domObject) throws MatrixException {
		try 
		{
				if(hasUnLockAccess(context,domObject) && isLocked(context,domObject))
				{
					domObject.unlock(context);
					isLocked=false;
				}
		}catch (MatrixException e){
			VPLMIntegTraceUtil.trace(context, ">>> Inside Catch of UnlockDO method::"+e.getMessage());
		}
	
	}

	// Context management
	public boolean isContextPushed() {

		return bIsContextPushed;
	}

	public void pushContext() throws FrameworkException {
		ContextUtil.pushContext(PRSPContext.get());
		bIsContextPushed = true;
	}

	public void popContext() throws FrameworkException {
		if (bIsContextPushed) {
			ContextUtil.popContext(PRSPContext.get());
		}
	}

	public void checkinFile(Context context, BusinessObject majorBo, String format, String store, String filename,
			String srcFolder, byte[] content,boolean streaming) throws Exception {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Actual  checkinFile streaming "+streaming);
		if (streaming) {
			// Checkin file with streaming : the file is streamed in a byte array from the
			// client

			InputStreamSource_2 stream = new FileManagement.FCSInputStreamSource(new ByteArrayInputStream(content),
					filename, format, content.length);

			fileCheckinHandler.streamCheckin(context, majorBo, format, store, filename, stream);
		} else {
			// Checkin file without streaming : the file is located in the server
			fileCheckinHandler.directCheckin(context, majorBo, format, filename, srcFolder);
		}

	}
	
	//GQS - ALM38242 get related documents that can be called from many services including
	/**
	 * Method to get the related objects
	 * @param context
	 * @param mpInputData
	 * @return MapList
	 * @throws Throwable 
	 */
	public MapList getRelatedObjects(Context context, Map<String,Object> mpInputData) throws Throwable  {
		VPLMIntegTraceUtil.trace(context, ">>>>>START of getRelatedObjects method");
		MapList mlRelatedDocs=new MapList();		
		
		String strObjectId=(String)mpInputData.get(DomainConstants.SELECT_ID);
		String strType=(String)mpInputData.get(DomainConstants.SELECT_TYPE);
		String strExpandLevel=(String)mpInputData.get(DataConstants.CONSTANT_EXPAND_LEVEL);
		StringList slObjSelects=(StringList)mpInputData.get(DataConstants.CONSTANT_OBJ_SELECTS);
		VPLMIntegTraceUtil.trace(context, ">>>>>strObjectId::"+strObjectId);
		VPLMIntegTraceUtil.trace(context, ">>>>>strType::"+strType);
		VPLMIntegTraceUtil.trace(context, ">>>>>strExpandLevel::"+strExpandLevel);
		VPLMIntegTraceUtil.trace(context, ">>>>>slObjSelects::"+slObjSelects);
		
		DomainObject doObj=DomainObject.newInstance(context,strObjectId);
		
		//method to get self documents
		if(DataConstants.TYPE_VPMREFERENCE.equals(strType)) {
			//invoke the method to get the CATIA documents (through PATH)
			mlRelatedDocs=getCATIADocuments(context,strObjectId,slObjSelects);
		}else if(DataConstants.TYPE_SIMULATION.equals(strType)){
			mlRelatedDocs=getSimulationDocuments(context,strObjectId,strExpandLevel);
		}else {
		
			Pattern relPattern=(Pattern)mpInputData.get(DataConstants.CONSTANT_REL_PATTERN);
			Pattern typePattern=(Pattern)mpInputData.get(DataConstants.CONSTANT_TYPE_PATTERN);
			StringList slRelSelects=(StringList)mpInputData.get(DataConstants.CONSTANT_REL_SELECTS);
			String strToSide=(String)mpInputData.get(DataConstants.CONSTANT_TO);
			String strFromSide=(String)mpInputData.get(DataConstants.CONSTANT_FROM);
			String strObjWhere=(String)mpInputData.get(DataConstants.CONSTANT_OBJ_WHERE);
			String strRelWhere=(String)mpInputData.get(DataConstants.CONSTANT_REL_WHERE);
			
			VPLMIntegTraceUtil.trace(context, ">>>>>relPattern::"+relPattern.getPattern());
			VPLMIntegTraceUtil.trace(context, ">>>>>typePattern::"+typePattern.getPattern());
			VPLMIntegTraceUtil.trace(context, ">>>>>slRelSelects::"+slRelSelects);
			VPLMIntegTraceUtil.trace(context, ">>>>>strToSide::"+strToSide);
			VPLMIntegTraceUtil.trace(context, ">>>>>strFromSide::"+strFromSide);
			VPLMIntegTraceUtil.trace(context, ">>>>>strObjWhere::"+strObjWhere);
			VPLMIntegTraceUtil.trace(context, ">>>>>strRelWhere::"+strRelWhere);
			
			if(!relPattern.getPattern().isEmpty() && !typePattern.getPattern().isEmpty()) {
				MapList mlDocs =doObj.getRelatedObjects(context,
					relPattern.getPattern(),  // relationship pattern
					typePattern.getPattern(),         // type pattern
					slObjSelects,        // Object selects
					slRelSelects,               // relationship selects
					Boolean.parseBoolean(strFromSide),               // from
					Boolean.parseBoolean(strToSide),              // to
					Short.parseShort(strExpandLevel),           // expand level
					strObjWhere,               // object where
					strRelWhere,               // relationship where
					0);                 // limit
		
				if(!mlRelatedDocs.isEmpty())
					mlRelatedDocs.addAll(mlDocs);
				else
					mlRelatedDocs=mlDocs;
			}
		}
		VPLMIntegTraceUtil.trace(context, ">>>>>mlRelatedDocs::"+mlRelatedDocs);
		return mlRelatedDocs;
		
	}

	/**
	 * Method to get the related objects of VPMReference and then get the info of those documents
	 * @param context
	 * @param String objectId of VPMReference
	 * @return MapList
	 * @throws MatrixException
	 */
	public MapList getCATIADocuments(Context context, String strObjectId,StringList slObjSelects) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>>>START of getCATIADocuments method");
		MapList mlRelatedDocs=new MapList();
		
		HashMap<String,String> hmParam=new HashMap<>();
		hmParam.put(DataConstants.KEY_OBJECT_ID, strObjectId);
		//DTCLD-192: Added the parentRelName parameter in the map
		hmParam.put(DataConstants.KEY_PARENT_REL_NAME, DataConstants.CONST_PLM_DOC_CONNECTION);
		
		MapList mlRelatedDocIds = JPO.invoke(context,"VPLMDocument", null, "getDocuments", JPO.packArgs(hmParam), MapList.class);
		VPLMIntegTraceUtil.trace(context, ">>>>>mlRelatedDocIds::"+mlRelatedDocIds);
		
		if(!mlRelatedDocIds.isEmpty()) {
			Map<String,String>mpObj;
			DomainObject doObj;
			
			for(int i=0;i<mlRelatedDocIds.size();i++) {
				mpObj=(Map)mlRelatedDocIds.get(i);
				doObj=DomainObject.newInstance(context,mpObj.get(DomainConstants.SELECT_ID));
				mlRelatedDocs.add(doObj.getInfo(context, slObjSelects));
			}
		}
		VPLMIntegTraceUtil.trace(context, ">>>> Final Output::"+mlRelatedDocs);
		return mlRelatedDocs;
	}
	
	/**
	 * Method to get the related objects of Simulation and then format the info of the documents
	 * @param context
	 * @param String objectId of Simulation object
	 * @return MapList
	 * @throws Throwable
	 */
	public MapList getSimulationDocuments(Context context, String strObjectId,String strExpandLevel) throws Throwable {
		VPLMIntegTraceUtil.trace(context, ">>>>> START of getSimulationDocuments method");
		MapList mlRelatedDocs=new MapList();
		HashMap hmParam=new HashMap();
		hmParam.put(DataConstants.KEY_OBJECT_ID,strObjectId);
		hmParam.put("basics", "basics");
		hmParam.put(DataConstants.CONSTANT_EXPAND_LEVEL, strExpandLevel);
		MapList mlRelatedList = JPO.invoke(context,"jpo.simulation.SimulationContent", null, "getFilterAll", JPO.packArgs(hmParam), MapList.class);
		VPLMIntegTraceUtil.trace(context, ">>>>> mlRelatedList::"+mlRelatedList);
		
		if(!mlRelatedList.isEmpty()) {
			Map<String,String>mpObj;
			Map<String,String>mpTemp=new HashMap();
			DomainObject doObject=null;
			for(int i=0;i<mlRelatedList.size();i++) {
				mpObj=(Map)mlRelatedList.get(i);

				if(DataConstants.CONSTANT_TRUE.equalsIgnoreCase(mpObj.get(DataConstants.KEY_IS_DOCUMENT)) || mpObj.containsKey(DomainConstants.SELECT_FILE_NAME)){
					if(!mpObj.containsKey(DomainConstants.SELECT_FILE_NAME)) {
						mpTemp=new HashMap();
						mpTemp.put(DomainConstants.SELECT_NAME, mpObj.get(DomainConstants.SELECT_NAME));
						mpTemp.put(DomainConstants.SELECT_TYPE, mpObj.get(DomainConstants.SELECT_TYPE));
						mpTemp.put(DomainConstants.SELECT_REVISION, mpObj.get(DomainConstants.SELECT_REVISION));
						mpTemp.put(DataConstants.SELECT_PHYSICALID, mpObj.get(DataConstants.SELECT_PHYSICALID));
						//Logic to fetch owner, since OOTB method doesnt fetch owner
						doObject=DomainObject.newInstance(context,mpObj.get(DomainConstants.SELECT_ID));
						mpTemp.put(DomainConstants.SELECT_OWNER, doObject.getInfo(context, DomainConstants.SELECT_OWNER));
					}else {
						mpTemp.put(DataConstants.KEY_FILE_NAME, mpObj.get(DomainConstants.SELECT_FILE_NAME));
						mlRelatedDocs.add(mpTemp);
					}
				}
			}
		}
		return mlRelatedDocs;
}
	
	/**
	 * Method added for DT18X6-321
	 * Method to validate the file name for invalid characters
	 * @param context
	 * @param String File Name
	 * @return Map
	 */
	public Map checkForInvalidCharsInFileName(Context context,String strFileName) {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  checkForInvalidCharsInFileName Method START");
		Map mpResult=new HashMap();
		StringBuilder sbBadCharsDecoded=new StringBuilder();
		StringBuilder sbErrorMessage=new StringBuilder();
		boolean isValidFileName=true;
		String strBadChar ="";
		StringList slDecodedList=new StringList();
				 
		//Logic to decode and put char again in the array. Also form a string to be displayed in the exception
		for(DataConstants.invalidChar_FileName strChar : DataConstants.invalidChar_FileName.values()) { 
		    strBadChar=strChar.getChar();
		    strBadChar = StringEscapeUtils.unescapeJava(strBadChar);
					 
			slDecodedList.add(strBadChar);
			if(sbBadCharsDecoded.length()==0) {
			    sbBadCharsDecoded.append(strBadChar);
			} else {
				sbBadCharsDecoded.append(' ');
				sbBadCharsDecoded.append(strBadChar);
			}
		}
				 
		slDecodedList=StringUtil.split(sbBadCharsDecoded.toString()," ");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> slDecodedList::"+slDecodedList);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> strFileName::"+strFileName);
		for(int i=0;i<slDecodedList.size();i++) {
		    strBadChar=slDecodedList.get(i);
	
			if(strFileName.contains(strBadChar))
			{
				isValidFileName=false;
				sbErrorMessage.append(EnoviaResourceBundle.getProperty(context, "emxComponentsStringResource",context.getLocale(), "emxComponents.FileName.SpecialChar.Alert"));
				sbErrorMessage.append(" ");
				sbErrorMessage.append(sbBadCharsDecoded.toString());
				break;
			}
		} 
		mpResult.put("bVaildFileName",isValidFileName);
		mpResult.put("alertMessage",sbErrorMessage.toString());
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>> mpResult::"+mpResult);
		return mpResult;
	}
	
	/**
	 * Method to checkout the files from particular object
	 * @param context
	 * @param doObject
	 * @return Map
	 * @throws MatrixException
	 */
	public Map checkoutFiles(Context context,DomainObject doObject) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  START of CommonDocumentHandler checkoutFiles method");
		   Map mpFileData=new HashMap();
		MapList filesList = doObject.getAllFormatFiles(context);
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  checkoutFiles filesList "+filesList);

		Iterator<?> fileItr1 = filesList.iterator();
		Map<?, ?> filemapObj1 ;
			
		String filename;
		String fileformat;
		FileList files;
		List filelist;
		java.io.File fileRoot=new java.io.File(context.createWorkspace());
		int i=1;
		while (fileItr1.hasNext())
		{
				filelist = new List();	
				files = new FileList();
				filemapObj1 = (Map<?, ?>)fileItr1.next();
				filename       = (String)filemapObj1.get("filename");
				fileformat       = (String)filemapObj1.get("format");
				
				mpFileData.put("filename_"+i, filename);
				mpFileData.put("fileformat_"+i, fileformat);
				VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  checkoutFiles filename "+filename+" format::"+fileformat);
				
				matrix.db.File file = new matrix.db.File(filename, fileformat);
				filelist.add(file);
				 files.addAll(filelist);
			        
				doObject.checkoutFiles(context, false, fileformat, files, fileRoot.getAbsolutePath() + java.io.File.separator);
				i++;
		}
		mpFileData.put("noOfFiles", filesList.size());
		mpFileData.put("directory",fileRoot.getAbsolutePath());
		VPLMIntegTraceUtil.trace(context, "<<<<<<<<<<<<<<<  END of CommonDocumentHandler checkoutFiles method");
		return mpFileData;
	}
	
	/**
	 * Method to checkin the files to given object
	 * @param context
	 * @param doObject
	 * @param mpFileData
	 * @throws MatrixException
	 */
	public void checkinFiles(Context context,DomainObject doObject,Map mpFileData) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  START of CommonDocumentHandler checkinFiles method");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>  mpFileData "+mpFileData);
		
		BusinessObject bo=new BusinessObject(doObject);
		
		String srcFolder=(String)mpFileData.get("directory");
		int iFiles=(int)mpFileData.get("noOfFiles");
		VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>   iFiles "+iFiles);
		
		for(int i=1;i<=iFiles;i++) {
			String format=(String)mpFileData.get("fileformat_"+i);
			String srcFilename=(String)mpFileData.get("filename_"+i);

			VPLMIntegTraceUtil.trace(context, ">>>>>>>>>>>>>>   format "+format+" srcFilename::"+srcFilename);
			
			bo.checkinFile(context, true, true, null, format, srcFilename, srcFolder);
		}
		
		VPLMIntegTraceUtil.trace(context, "<<<<<<<<<<<<<<  END of CommonDocumentHandler checkinFiles method");
	}

/**
 * Method to get the info of Active/Latest Version connected to the object, as per the Title 
 * @param context
 * @param doObject
 * @param strTitle
 * @param isActive
 * @return MapList
 * @throws FrameworkException
 */
	public MapList getActiveOrLatestVersionAsPerTitle(Context context, DomainObject doObject, String strTitle,boolean isActive) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> START of getActiveOrLatestVersionAsPerTitle method");
		String objectWhere = CommonDocument.SELECT_TITLE + "== const'" + strTitle + "'";
		
		VPLMIntegTraceUtil.trace(context, ">>> getActiveOrLatestVersionAsPerTitle objectWhere::"+objectWhere);
		String strRelPattern;
		if(isActive)
			strRelPattern=CommonDocument.RELATIONSHIP_ACTIVE_VERSION;
		else
			strRelPattern=CommonDocument.RELATIONSHIP_LATEST_VERSION;
		
		VPLMIntegTraceUtil.trace(context, ">>> getActiveOrLatestVersionAsPerTitle strRelPattern::"+strRelPattern);
		
		MapList mlist = doObject.getRelatedObjects(context, // context.
				strRelPattern, // rel filter.
				CommonDocument.TYPE_DOCUMENTS, // type filter.
				new StringList(DomainConstants.SELECT_ID), // business selectables.
				new StringList(DomainConstants.SELECT_RELATIONSHIP_ID), // relationship selectables.
				false, // expand to direction.
				true, // expand from direction.
				(short) 1, // level
				objectWhere, // object where clause
				DomainConstants.EMPTY_STRING,
				0); // relationship where clause
		
		VPLMIntegTraceUtil.trace(context, "<<< END of getActiveOrLatestVersionAsPerTitle method result::"+mlist);
		return mlist;
	}
}
