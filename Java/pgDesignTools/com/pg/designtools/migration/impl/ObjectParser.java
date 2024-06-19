package com.pg.designtools.migration.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.CommonUtility;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.datamanagement.DataConstants.customWorkProcessD2SExceptions;
import com.pg.designtools.util.IPManagement;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class ObjectParser {
	private static final Logger logger = LoggerFactory.getLogger("DT_MIGRATION"); 
	
	/**
	 * Method to find the VPMReference object, with the help of details mentioned in input file.
	 * @param context
	 * @param mpObjectInfo
	 * @return DomainObject
	 * @throws MatrixException
	 */
	public Map getObjectDetailsForVPMReference(Context context, Map mpObjectInfo) throws MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of ObjectParser: getObjectDetailsForVPMReference method");
		logger.debug( ">>> Start of ObjectParser: getObjectDetailsForVPMReference method");
		
		Map mpFinalOutput=new HashMap();
		CommonUtility commonUtility=new CommonUtility(context);
		DomainObject doObject=null;
		String strObjectType;
		String strObjectName;
		String strObjectRev;
		String strError="";
		
	    strObjectType=(String)mpObjectInfo.get(DataConstants.CONSTANT_VPMREF_TYPE_KEY);
	    VPLMIntegTraceUtil.trace(context, ">>> strObjectType::"+strObjectType);
	    logger.debug(  ">>> strObjectType::"+strObjectType);
	    
		if(UIUtil.isNullOrEmpty(strObjectType)) {
			strObjectType=DataConstants.TYPE_VPMREFERENCE;
			VPLMIntegTraceUtil.trace(context, ">>> strObjectType11::"+strObjectType);
			logger.debug( ">>> strObjectType11::"+strObjectType);
		}
		 
		strObjectName=(String)mpObjectInfo.get(DataConstants.CONSTANT_VPMREF_NAME_KEY);
		VPLMIntegTraceUtil.trace(context, ">>> strObjectName::"+strObjectName);
		logger.debug( ">>> strObjectName::"+strObjectName);
		 
		if(UIUtil.isNullOrEmpty(strObjectName)) {
			DataConstants.customWorkProcessD2SExceptions errorVPMRefNameMissing=customWorkProcessD2SExceptions.ERROR_400_VPMREF_NAME_MISSING;
			
			strError=errorVPMRefNameMissing.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorVPMRefNameMissing.getExceptionMessage();
			
			VPLMIntegTraceUtil.trace(context, errorVPMRefNameMissing.getExceptionMessage());
			logger.debug( errorVPMRefNameMissing.getExceptionMessage());
			mpFinalOutput.put("DO", doObject);
			mpFinalOutput.put(DataConstants.STR_ERROR, strError);
			return mpFinalOutput;
		}
		
		strObjectRev=(String)mpObjectInfo.get(DataConstants.CONSTANT_VPMREF_REVISION_KEY);
		VPLMIntegTraceUtil.trace(context, ">>> strObjectRev::"+strObjectRev);
		logger.debug( ">>> strObjectRev::"+strObjectRev);
		
		if(UIUtil.isNullOrEmpty(strObjectRev)) {
			strObjectRev=DataConstants.SEPARATOR_STAR;
			VPLMIntegTraceUtil.trace(context, ">>> strObjectRev11::"+strObjectRev);
			logger.debug( ">>> strObjectRev11::"+strObjectRev);
		}

		StringList slSelects=new StringList(2);
		slSelects.add(DomainConstants.SELECT_ID);
		slSelects.add(DomainConstants.SELECT_REVISION);
		
		MapList mlObject=commonUtility.findObjectWithWhereClause(context, strObjectType, strObjectName, strObjectRev, "",slSelects);
		VPLMIntegTraceUtil.trace(context, ">>> mlObject::"+mlObject);
		logger.debug( ">>> mlObject::"+mlObject);
		
		if(mlObject.isEmpty()) {
			VPLMIntegTraceUtil.trace(context, "The object "+strObjectType+" "+strObjectName+" "+strObjectRev+" does not exist");
			logger.debug(  "The object "+strObjectType+" "+strObjectName+" "+strObjectRev+" does not exist");
			
				if(UIUtil.isNotNullAndNotEmpty(strError))
					strError+=DataConstants.SEPARATOR_PIPE+"The object "+strObjectType+" "+strObjectName+" "+strObjectRev+" does not exist";
				else
					strError="The object "+strObjectType+" "+strObjectName+" "+strObjectRev+" does not exist";
				
				mpFinalOutput.put("DO", doObject);
				mpFinalOutput.put(DataConstants.STR_ERROR, strError);
			return mpFinalOutput;
		}
		else if(mlObject.size()==1){
			Map mpInfo=(Map)mlObject.get(0);
			doObject=DomainObject.newInstance(context,(String)mpInfo.get(DomainConstants.SELECT_ID));	
		}
		else  if(mlObject.size()>1){
			StringList slRevList=new StringList();
			StringList slTempList=new StringList();
			Map mpInfo;
			
			for(int i=0;i<mlObject.size();i++) {
				mpInfo=(Map)mlObject.get(i);
				slRevList.add((String)mpInfo.get(DomainConstants.SELECT_REVISION));
				slTempList.add((String)mpInfo.get(DomainConstants.SELECT_REVISION));
			}
			VPLMIntegTraceUtil.trace(context, ">>> slTempList::"+slTempList);
			logger.debug(  ">>> slTempList::"+slTempList);
			
			Collections.sort(slRevList,Collections.reverseOrder());
			VPLMIntegTraceUtil.trace(context, ">>> reversed slRevList::"+slRevList);
			logger.debug(  ">>> reversed slRevList::"+slRevList);
			
			int iActualIndex=slTempList.indexOf(slRevList.get(0));
			VPLMIntegTraceUtil.trace(context, ">>> iActualIndex::"+iActualIndex);
			logger.debug( ">>> iActualIndex::"+iActualIndex);
			mpInfo=(Map) mlObject.get(iActualIndex);
			doObject=DomainObject.newInstance(context,(String)mpInfo.get(DomainConstants.SELECT_ID));	
		}
		mpFinalOutput.put("DO", doObject);
		mpFinalOutput.put(DataConstants.STR_ERROR, strError);
		
		VPLMIntegTraceUtil.trace(context, ">>> doObject:::"+doObject);
		VPLMIntegTraceUtil.trace(context, ">>> strError:::"+strError);
		VPLMIntegTraceUtil.trace(context, ">>>mpFinalOutput::"+mpFinalOutput);
		VPLMIntegTraceUtil.trace(context, "<<< End of ObjectParser: getObjectDetailsForVPMReference method");
		
		logger.debug( ">>> doObject:::"+doObject);
		logger.debug( ">>> strError:::"+strError);
		logger.debug( ">>>mpFinalOutput::"+mpFinalOutput);
		logger.debug( "<<< End of ObjectParser: getObjectDetailsForVPMReference method");
		return mpFinalOutput;
	}
	
	/**
	 * Method to find the EC Part object, with the help of details mentioned in input file.
	 * @param context
	 * @param mpObjectInfo
	 * @return DomainObject
	 * @throws FrameworkException
	 */
	public Map getObjectDetailsForECPart(Context context,Map mpObjectInfo) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context, ">>> Start of ObjectParser: getObjectDetailsForECPart method");
		logger.debug( ">>> Start of ObjectParser: getObjectDetailsForECPart method");
		
		Map mpFinalOutput=new HashMap<>();
		DomainObject doObject=null;
		String strObjectType;
		String strObjectName;
		String strObjectRev;
		String strDisplayObjectType;
		String strError="";
		
		strObjectType=(String)mpObjectInfo.get(DataConstants.CONSTANT_ECPART_TYPE_KEY);
		strDisplayObjectType=strObjectType;
		VPLMIntegTraceUtil.trace(context, ">>> strObjectType::"+strObjectType);
		logger.debug( ">>> strObjectType::"+strObjectType);
		
		if(UIUtil.isNullOrEmpty(strObjectType)) {
			Pattern typePattern=new Pattern(DataConstants.TYPE_PG_MASTER_PACKAGING_ASSEMBLY_PART);
			typePattern.addPattern(DataConstants.TYPE_PG_MASTER_PACKAGING_MATERIAL_PART);
			typePattern.addPattern(DataConstants.TYPE_PG_MASTER_PRODUCT_PART);
			typePattern.addPattern(DataConstants.TYPE_PG_MASTER_RAW_MATERIAL_PART);
			typePattern.addPattern(DataConstants.TYPE_SHAPE_PART);
			strObjectType=typePattern.getPattern();
			strDisplayObjectType=DataConstants.SEPARATOR_STAR;
			VPLMIntegTraceUtil.trace(context, ">>> strObjectType11::"+strObjectType);
			logger.debug( ">>> strObjectType11::"+strObjectType);
		}
	
		strObjectName=(String)mpObjectInfo.get(DataConstants.CONSTANT_ECPART_NAME_KEY);
		VPLMIntegTraceUtil.trace(context, ">>> strObjectName::"+strObjectName);
		logger.debug( ">>> strObjectName::"+strObjectName);
		
		if(UIUtil.isNullOrEmpty(strObjectName)) {
			DataConstants.customWorkProcessD2SExceptions errorECPartNameMissing=customWorkProcessD2SExceptions.ERROR_400_ECPART_NAME_MISSING;
				
			strError=errorECPartNameMissing.getExceptionCode()+DataConstants.SEPARATOR_COLON+errorECPartNameMissing.getExceptionMessage();

			VPLMIntegTraceUtil.trace(context, errorECPartNameMissing.getExceptionMessage());
			logger.debug( errorECPartNameMissing.getExceptionMessage());
			
			mpFinalOutput.put("DO", doObject);
			mpFinalOutput.put(DataConstants.STR_ERROR, strError);
			return mpFinalOutput;
		}
		
		strObjectRev=(String)mpObjectInfo.get(DataConstants.CONSTANT_ECPART_REVISION_KEY);
		VPLMIntegTraceUtil.trace(context, ">>> strObjectRev::"+strObjectRev);
		logger.debug( ">>> strObjectRev::"+strObjectRev);
		
		if(UIUtil.isNullOrEmpty(strObjectRev)) {
			strObjectRev=DataConstants.SEPARATOR_STAR;
			VPLMIntegTraceUtil.trace(context, ">>> strObjectRev11::"+strObjectRev);
			logger.debug( ">>> strObjectRev11::"+strObjectRev);
		}

		StringList slSelects=new StringList(2);
		slSelects.add(DomainConstants.SELECT_ID);
		slSelects.add(DomainConstants.SELECT_REVISION);

		IPManagement ipMgmt=new IPManagement(context);
		MapList mlObject=ipMgmt.findObject(context, strObjectType, strObjectName, strObjectRev, slSelects);
	
		VPLMIntegTraceUtil.trace(context, ">>> mlObject::"+mlObject);
		logger.debug( ">>> mlObject::"+mlObject);
		
		if(mlObject.isEmpty()) {
			
			if(UIUtil.isNotNullAndNotEmpty(strError))
				strError+=DataConstants.SEPARATOR_PIPE+"The object "+strDisplayObjectType+" "+strObjectName+" "+strObjectRev+" does not exist";
			else
				strError="The object "+strDisplayObjectType+" "+strObjectName+" "+strObjectRev+" does not exist";
	
			VPLMIntegTraceUtil.trace(context, ">>>The object "+strDisplayObjectType+" "+strObjectName+" "+strObjectRev+" does not exist");
			logger.debug( ">>>The object "+strDisplayObjectType+" "+strObjectName+" "+strObjectRev+" does not exist");
			
			mpFinalOutput.put("DO", doObject);
			mpFinalOutput.put(DataConstants.STR_ERROR, strError);
			return mpFinalOutput;
		}
		else if(mlObject.size()==1){
			Map mpInfo=(Map)mlObject.get(0);
			doObject=DomainObject.newInstance(context,(String)mpInfo.get(DomainConstants.SELECT_ID));	
		}
		else  if(mlObject.size()>1){
			StringList slRevList=new StringList();
			StringList slTempList=new StringList();
			Map mpInfo;
			
			for(int i=0;i<mlObject.size();i++) {
				mpInfo=(Map)mlObject.get(i);
				slRevList.add((String)mpInfo.get(DomainConstants.SELECT_REVISION));
				slTempList.add((String)mpInfo.get(DomainConstants.SELECT_REVISION));
			}
			
			VPLMIntegTraceUtil.trace(context, ">>> slTempList::"+slTempList);
			logger.debug( ">>> slTempList::"+slTempList);
			
			Collections.sort(slRevList,Collections.reverseOrder());
			VPLMIntegTraceUtil.trace(context, ">>> reversed slRevList::"+slRevList);
			logger.debug( ">>> reversed slRevList::"+slRevList);
			
			int iActualIndex=slTempList.indexOf(slRevList.get(0));
			VPLMIntegTraceUtil.trace(context, ">>> iActualIndex::"+iActualIndex);
			logger.debug( ">>> iActualIndex::"+iActualIndex);
			mpInfo=(Map) mlObject.get(iActualIndex);
			doObject=DomainObject.newInstance(context,(String)mpInfo.get(DomainConstants.SELECT_ID));	
		}
		mpFinalOutput.put("DO", doObject);
		mpFinalOutput.put(DataConstants.STR_ERROR, strError);
		
		VPLMIntegTraceUtil.trace(context, ">>> doObject::"+doObject);
		VPLMIntegTraceUtil.trace(context, ">>> strError:::"+strError);
		VPLMIntegTraceUtil.trace(context, ">>> mpFinalOutput:::"+mpFinalOutput);
		VPLMIntegTraceUtil.trace(context, "<<< End of ObjectParser: getObjectDetailsForECPart method");
		
		logger.debug(  ">>> doObject::"+doObject);
		logger.debug( ">>> strError:::"+strError);
		logger.debug( ">>> mpFinalOutput:::"+mpFinalOutput);
		logger.debug( "<<< End of ObjectParser: getObjectDetailsForECPart method");
		return mpFinalOutput;
	}

}
