package com.pg.designtools.datamanagement;

import java.util.HashMap;
import java.util.Map;

import com.dassault_systemes.enovia.gls.common.model.PRSPContext;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.vplmintegration.util.VPLMIntegTraceUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class SimulationDocument {

	public SimulationDocument(Context context) {
			PRSPContext.set(context);
	}
	   
	/**
	 * Method for getting the selectables for Sim Doc
	 * @return StringList
	 */
	private StringList getSelectablesForSimDoc() {
		StringList slObjSelects=new StringList();
		slObjSelects.add(DomainConstants.SELECT_ID);
		slObjSelects.add(DomainConstants.SELECT_TYPE);
		slObjSelects.add(DomainConstants.SELECT_NAME);
		slObjSelects.add(DomainConstants.SELECT_REVISION);
		slObjSelects.add(DomainConstants.SELECT_OWNER);
		slObjSelects.add(DomainConstants.SELECT_CURRENT);
		slObjSelects.add(DomainConstants.SELECT_POLICY);
		slObjSelects.add(DomainConstants.SELECT_ORIGINATED);
		slObjSelects.add(DomainConstants.SELECT_MODIFIED);
		slObjSelects.add("attribute["+DataConstants.ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION+"]");
		slObjSelects.add("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
		slObjSelects.add("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].id");
		slObjSelects.add("to["+DataConstants.REL_SIMULATION_CONTENT_OWNED+"]");
		return slObjSelects;
	}
	
	/**
	 * Method to find all Sim Doc data
	 * @param context
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	private MapList findAllSimDocs(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findAllSimDocs method");
		StringList slObjSelects=getSelectablesForSimDoc();
		
		Pattern typePattern=new Pattern(DataConstants.TYPE_SIMULATION_DOC_VERSIONED);
		typePattern.addPattern(DataConstants.TYPE_SIMULATION_DOC_NONVERSIONED);
		
		return findObjects(context, typePattern, "", slObjSelects);
	}
	
	/**
	 * This method would filter the data from Maplist. Data governed by "Simulation Document Legacy" policy would be returned.
	 * @param mlAllSimDocData
	 * @return MapList
	 * @throws FrameworkException 
	 */
	private MapList getLegacySimDocs(Context context) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START getLegacySimDocs method");
		StringList slObjSelects=getSelectablesForSimDoc();
		
		Pattern typePattern=new Pattern(DataConstants.TYPE_SIMULATION_DOC_VERSIONED);
		typePattern.addPattern(DataConstants.TYPE_SIMULATION_DOC_NONVERSIONED);
		
		String strWhere=DomainConstants.SELECT_POLICY+" == '"+DataConstants.POLICY_SIMULATION_DOCUMENT_LEGACY+"'";
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Where clause::"+strWhere);
		return findObjects(context, typePattern, strWhere, slObjSelects);
	}

	private MapList findObjects(Context context,Pattern typePattern,String strWhere,StringList slObjSelects) throws FrameworkException {
		return DomainObject.findObjects(context,
				typePattern.getPattern(), //typepattern
				DomainConstants.QUERY_WILDCARD,  // namepattern
                DomainConstants.QUERY_WILDCARD,  // revpattern
                DomainConstants.QUERY_WILDCARD,  // owner pattern
                DataConstants.VAULT_ESERVICE_PRODUCTION,  // vault pattern
                strWhere, // where exp
                true,
                slObjSelects);
	}

	/**
	 * This method would filter the data from Maplist. Data having IP Control class and Process Access Classification attribute value as False would be returned.
	 * @param mlAllSimDocData
	 * @return MapList
	 */
	private MapList getIPConnectedAndAttrFalseSimDocs(MapList mlAllSimDocData) {
		MapList mlSimDocData=new MapList();
		if(!mlAllSimDocData.isEmpty()) {
			int iSize=mlAllSimDocData.size();
			Map<String,String> mpData;
			String strIPClasses="";
			String strProcessAccessClassification="";
			
			for(int i=0;i<iSize;i++) {
				mpData=(Map<String, String>) mlAllSimDocData.get(i);
				strIPClasses=mpData.get("to["+DomainConstants.RELATIONSHIP_PROTECTED_ITEM+"].from["+DataConstants.TYPE_IP_CONTROL_CLASS+"].name");
				strProcessAccessClassification=mpData.get("attribute["+DataConstants.ATTRIBUTE_PROCESS_ACCESS_CLASSIFICATION+"]");
				
				if(UIUtil.isNotNullAndNotEmpty(strIPClasses) && strProcessAccessClassification.equalsIgnoreCase(DataConstants.CONSTANT_FALSE))
					mlSimDocData.add(mpData);
			}
		}
		return mlSimDocData;
	}

	/**
	 * Method to find the Sim Doc data, as per the level
	 * @param context
	 * @return MapList of data
	 * @throws FrameworkException
	 */
	public MapList findSimulationDocumentDataForCleanup(Context context,String strLevel) throws FrameworkException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START findSimulationDocumentData method");
		MapList mlSimDocData=new MapList();
		
		if(strLevel.equalsIgnoreCase(DataConstants.CONST_LEGACY)) {
			mlSimDocData=getLegacySimDocs(context);
		}else if(strLevel.equalsIgnoreCase(DataConstants.CONST_IPATTR)) {
			//There are no objects returned, if we give Where clause with like "attribute[Process Access Classification]==FALSE". Hence, getting the data for all Sim Docs and then filtering it as per requirement in next method
			MapList mlAllSimDocData=findAllSimDocs(context);
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Invoking getIPConnectedAndAttrFalseSimDocs method");
			mlSimDocData=getIPConnectedAndAttrFalseSimDocs(mlAllSimDocData);
		}

		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Total data found::"+mlSimDocData.size());
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END findSimulationDocumentData method");
		return mlSimDocData;
	}
	
	/**
	 * Method to get information of Simulation Document
	 * @param context
	 * @param strObjectId
	 * @return Map
	 * @throws MatrixException 
	 */
	public Map getInfoOfSimDoc(Context context, String strObjectId) throws MatrixException {
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>START getInfoOfSimDoc method");
		Map mpSimDocObjInfo=new HashMap<>();
		StringList slObjSelects=getSelectablesForSimDoc();
		
		BusinessObject boSimDoc=new BusinessObject(strObjectId);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>boSimDoc exists::"+boSimDoc.exists(context));
		
		if(boSimDoc.exists(context)) {
			DomainObject doSimDoc=DomainObject.newInstance(context,strObjectId);
			mpSimDocObjInfo=doSimDoc.getInfo(context, slObjSelects);
		}
		else
			VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>Object "+strObjectId+" does not exist in database");
		
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>mpSimDocObjInfo::"+mpSimDocObjInfo);
		VPLMIntegTraceUtil.trace(context,">>>>>>>>>>>>>>>>>END getInfoOfSimDoc method");
		return mpSimDocObjInfo;
	}
}
