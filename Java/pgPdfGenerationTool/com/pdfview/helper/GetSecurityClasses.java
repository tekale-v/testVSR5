package com.pdfview.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.constant.PDFConstant;
import com.pdfview.impl.FPP.SecurityClass;
import com.pdfview.impl.FPP.SecurityClasses;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class GetSecurityClasses {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetSecurityClasses(Context context, String sOID){
		_context = context;
		_OID = sOID;
	}

	public SecurityClasses getComponent() {
		
			return getSecurityClassTableData(_context, _OID);
		
	}
	
	/**
	* This method gets Security Classes table data
	* @param Context - Context user
	* @param String - strObjectId, strHeader
	* @return StringBuilder - returns list of Security Classes
	* @throws Exception if fails
	**/
	private SecurityClasses getSecurityClassTableData(Context context, String strObjectId){
		SecurityClasses securityClasses = new SecurityClasses();
		List<SecurityClass> securityClassList=securityClasses.getSecurityClass();
		StringList objectSelects = new StringList(6);
		objectSelects.add(DomainConstants.SELECT_ID);
		objectSelects.add(DomainConstants.SELECT_NAME);
		objectSelects.add(DomainConstants.SELECT_TYPE);
		objectSelects.add(DomainConstants.SELECT_DESCRIPTION);
		objectSelects.add(DomainConstants.SELECT_CURRENT);
		objectSelects.add(PDFConstant.CONST_ACESS);
		
		
		try{
			if(UIUtil.isNotNullAndNotEmpty(strObjectId))
			{
				DomainObject domObjectId = DomainObject.newInstance(context,strObjectId);
			
			boolean isAccess = domObjectId.checkAccess(context, (short)0);
			MapList mlSecurityClasses = new MapList();
			if(isAccess)
			{
				mlSecurityClasses = domObjectId.getRelatedObjects(context,   //Context
																	DomainConstants.RELATIONSHIP_PROTECTED_ITEM, //Relationship Pattern
																	pgV3Constants.TYPE_SECURITYCONTROLCLASS,//Type Pattern
																	objectSelects,  //Object select
																	null,  			//Relationship select
																	true,			//getTo
																	false,			//getFrom
																	(short)1,		//recurseToLevel
																	null,			//objectWhere
																	null);		//relationshipWhere													
			}else{
				Map mp = new HashMap();
				mp.put(DomainConstants.SELECT_NAME,"No Access");
				mp.put(pgV3Constants.SELECT_TYPE,"No Access");
				mp.put(DomainConstants.SELECT_DESCRIPTION,"No Access");
				mp.put(DomainConstants.SELECT_CURRENT,"No Access");
				mlSecurityClasses.add(mp);
			}
			
			if((mlSecurityClasses != null) && (!mlSecurityClasses.isEmpty())){
				String strLanguage = context.getSession().getLanguage();
	
				int nMapSecurityClassSize = mlSecurityClasses.size();
				String strClassName = null;
				String strClassLibrary = DomainConstants.EMPTY_STRING;
				strClassLibrary = PDFConstant.CONST_CLASS_LIBRARY_ACCESS;
				String strClassificationPath=null;
				Map mConnectedSecurityClass = null;
				DomainObject domSecurityClass = null;
				for (int i = 0; i <nMapSecurityClassSize; i++){
					SecurityClass securityClass=new SecurityClass();
					mConnectedSecurityClass = (Map)mlSecurityClasses.get(i);
					strClassName = (String)mConnectedSecurityClass.get(DomainConstants.SELECT_NAME);
					if(isAccess)
					{
					domSecurityClass = DomainObject.newInstance(context,(String)mConnectedSecurityClass.get(DomainConstants.SELECT_ID));
					strClassLibrary = domSecurityClass.getInfo(context,"to[" + pgV3Constants.RELATIONSHIP_SUBCLASS+ "].from.name");
					}
					
					securityClass.setName(StringHelper.validateString1((String)mConnectedSecurityClass.get(DomainConstants.SELECT_NAME)));
					securityClass.setType(StringHelper.validateString1((String)mConnectedSecurityClass.get(pgV3Constants.SELECT_TYPE)));
					securityClass.setDescription(StringHelper.validateString1((String)mConnectedSecurityClass.get(DomainConstants.SELECT_DESCRIPTION)));
					securityClass.setState(StringHelper.validateString1((String)mConnectedSecurityClass.get(DomainConstants.SELECT_CURRENT)));
					
					securityClass.setHasClassAccess(StringHelper.validateString1((String)mConnectedSecurityClass.get("current.access[fromdisconnect]")));
					securityClass.setLibrary(StringHelper.validateString1(strClassLibrary));
					securityClass.setClassificationpath(strClassLibrary+"->"+strClassName);
					
					securityClassList.add(securityClass);
				}
				}
		}
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return securityClasses;
	}
	/**
	 * This method returns object ID of latest released Master
	 * 
	 * @returns String
	 * @throws Exception
	 */
	public static String[] getLatestRelease(Context context, String strIPS) throws Exception {
		StringList obselect = new StringList(3);
		obselect.add(pgV3Constants.SELECT_ID);
		obselect.add(pgV3Constants.SELECT_REVISION);
		obselect.add(pgV3Constants.SELECT_CURRENT);
		String strid = DomainConstants.EMPTY_STRING;
		String revision = DomainConstants.EMPTY_STRING;
		String current = DomainConstants.EMPTY_STRING;
		String[] latestrelase = new String[2];
		if (UIUtil.isNotNullAndNotEmpty(strIPS)) {
			DomainObject dom = DomainObject.newInstance(context, strIPS);
			MapList mlRevsioninfo = dom.getRevisionsInfo(context, obselect, new StringList());
			mlRevsioninfo.addSortKey(DomainObject.SELECT_REVISION, PDFConstant.CONST_ASCENDING, PDFConstant.CONST_STRING);
			mlRevsioninfo.sort();
			int size = mlRevsioninfo.size();
			Map mapRevsioninfo =null;
			for (int i = size - 1; i >= 0; i--) {
				mapRevsioninfo = (Map) mlRevsioninfo.get(i);
				strid = (String) mapRevsioninfo.get(pgV3Constants.SELECT_ID);
				revision = (String) mapRevsioninfo.get(pgV3Constants.SELECT_REVISION);
				current = (String) mapRevsioninfo.get(pgV3Constants.SELECT_CURRENT);
				if (pgV3Constants.STATE_PART_RELEASE.equalsIgnoreCase(current)) {
					latestrelase[0] = strid;
					latestrelase[1] = revision;
					return latestrelase;
				}
			}
		}
		return null;
	}

}
