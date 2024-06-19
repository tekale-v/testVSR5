package com.pg.designtools.migration.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;
import com.pg.designtools.datamanagement.DataConstants;
import matrix.db.Context;
import matrix.util.MatrixException;

public class ObjectFetcher {
		
	protected IInputFileReader inputFileReader;
	private static final Logger logger = LoggerFactory.getLogger("DT_MIGRATION"); 
	
	public ObjectFetcher() {
		super();
	}
	
	public ObjectFetcher(String strInputFilePath) {
		if(strInputFilePath.endsWith("csv")) {
			inputFileReader=new CSVReader();
		}
	}
	
	/**
	 * Method to get the objects from the input file
	 * @param context
	 * @param strInputFilePath
	 * @return MapList
	 * @throws IOException
	 * @throws MatrixException
	 */
	public MapList getObjects(Context context,String strInputFilePath) throws IOException, MatrixException {
		VPLMIntegTraceUtil.trace(context, ">>> START of ObjectFetcher: getObjects method");
		logger.debug(">>> START of ObjectFetcher: getObjects method");
		
		MapList mlFinalData=new MapList();
		Map mpFinalOutput;
		DomainObject doVPMReferenceObject = null;
		DomainObject doECPartObject = null;
		MapList mlObjectDetails;
		Map mpObjectInfo;
		Map mpVPMRef;
		Map mpECPart;
		String strOwner;
		String strInput;
		String strError="";
		String strECPartError="";
		String strHeader;
		ObjectParser objParser=new ObjectParser();
		
		mlObjectDetails=inputFileReader.readContents(strInputFilePath);
		VPLMIntegTraceUtil.trace(context, ">>>mlObjectDetails:::"+mlObjectDetails);
		logger.debug(">>>mlObjectDetails:::"+mlObjectDetails);
		
		for(int i=0;i<mlObjectDetails.size();i++) {
				mpFinalOutput=new HashMap();
				mpObjectInfo=(Map) mlObjectDetails.get(i);
				VPLMIntegTraceUtil.trace(context, ">>>mpObjectInfo:::"+mpObjectInfo);
				logger.debug(">>>mpObjectInfo:::"+mpObjectInfo);
				
				if(null!=mpObjectInfo && !mpObjectInfo.isEmpty()) {
					strInput=(String)mpObjectInfo.get(DataConstants.CONSTANT_INPUT);
					
					mpVPMRef=objParser.getObjectDetailsForVPMReference(context,mpObjectInfo);
					VPLMIntegTraceUtil.trace(context, ">>>mpVPMRef:::"+mpVPMRef);
					logger.debug(">>>mpVPMRef:::"+mpVPMRef);
					
					doVPMReferenceObject=(DomainObject) mpVPMRef.get("DO");
					
					strError=(String) mpVPMRef.get(DataConstants.STR_ERROR);
					VPLMIntegTraceUtil.trace(context, ">>>doVPMReferenceObject:::"+doVPMReferenceObject);
					logger.debug(">>>doVPMReferenceObject:::"+doVPMReferenceObject);
					
					mpECPart=objParser.getObjectDetailsForECPart(context,mpObjectInfo);
					VPLMIntegTraceUtil.trace(context, ">>>mpECPart:::"+mpECPart);
					logger.debug(">>>mpECPart:::"+mpECPart);
					
					doECPartObject=(DomainObject) mpECPart.get("DO");
					
					strECPartError=(String) mpECPart.get(DataConstants.STR_ERROR);
					VPLMIntegTraceUtil.trace(context, ">>>doECPartObject:::"+doECPartObject);
					logger.debug(">>>doECPartObject:::"+doECPartObject);
					
					if(UIUtil.isNotNullAndNotEmpty(strError) && UIUtil.isNotNullAndNotEmpty(strECPartError))
						strError+=DataConstants.SEPARATOR_PIPE+strECPartError;
					else
						strError+=strECPartError;
					
					strOwner=(String)mpObjectInfo.get(DataConstants.CONSTANT_OWNER_KEY);
					VPLMIntegTraceUtil.trace(context, ">>>strOwner:::"+strOwner);
					logger.debug(">>>strOwner:::"+strOwner);
					
					strHeader=(String)mpObjectInfo.get(DataConstants.CONSTANT_HEADER);
					VPLMIntegTraceUtil.trace(context, ">>>strHeader:::"+strHeader);
					logger.debug(">>>strHeader:::"+strHeader);
					
					mpFinalOutput.put(DataConstants.CONSTANT_VPMREFERENCE_OBJECT, doVPMReferenceObject);
					mpFinalOutput.put(DataConstants.CONSTANT_ECPART_OBJECT, doECPartObject);
					mpFinalOutput.put(DataConstants.CONSTANT_OWNER_KEY, strOwner);
					mpFinalOutput.put(DataConstants.CONSTANT_INPUT,strInput);
					mpFinalOutput.put(DataConstants.STR_ERROR,strError);
					mpFinalOutput.put(DataConstants.CONSTANT_HEADER,strHeader);
					mlFinalData.add(mpFinalOutput);
				
					VPLMIntegTraceUtil.trace(context, ">>>mpFinalOutput:::"+mpFinalOutput);
					logger.debug(">>>mpFinalOutput:::"+mpFinalOutput);
				}
			}
		VPLMIntegTraceUtil.trace(context, ">>>mlFinalData:::"+mlFinalData);
		VPLMIntegTraceUtil.trace(context, "<<< End of ObjectFetcher: getObjects method");
		
		logger.debug(">>>mlFinalData:::"+mlFinalData);
		logger.debug("<<< End of ObjectFetcher: getObjects method");
		return mlFinalData;
	}
}
