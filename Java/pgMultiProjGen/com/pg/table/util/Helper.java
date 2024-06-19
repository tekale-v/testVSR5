package com.pg.table.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.custom.pg.Artwork.ArtworkConstants;

import com.matrixone.apps.awl.util.BusinessUtil;

import com.matrixone.apps.cpd.util.JsonHelper;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;

import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;

import matrix.util.StringList;

public class Helper {
	private static final String STR_FIELD_DISPLAY_CHOICES= "field_display_choices";
	private static final String STR_FIELD_CHOICES = "field_choices";
	private static final String RELATIONSHIP_PGPLIProductHierarchy = PropertyUtil.getSchemaProperty("relationship_pgPLProductHierarchy");
	private static final String TYPE_SECTOR = PropertyUtil.getSchemaProperty("type_pgPLISector");
	private static final String TYPE_SUBSECTOR = PropertyUtil.getSchemaProperty("type_pgPLISubSector");
	private static final String STATE_ACTIVE = PropertyUtil.getSchemaProperty("Policy",  PropertyUtil.getSchemaProperty("policy_pgPicklistItem"), "state_Active");
	private static final String TYPE_ARTWORKTYPE = PropertyUtil.getSchemaProperty("type_pgPLIArtworkType");
	private static final String POLICY_ARTWORKTYPE =PropertyUtil.getSchemaProperty("policy_pgPicklistItem");
	private static final String DEFAULT_ARTWORKTYPE = "Primary";
	
	public static  String getProjectCategory(Context context,String []args) throws Exception {
		HashMap paramMap = new HashMap();
		HashMap requestMap = new HashMap();
		Map mProgramMap = new HashMap();
		mProgramMap.put("requestMap", requestMap);
		mProgramMap.put("paramMap", paramMap);
		HashMap mResultMap = (HashMap)JPO.invoke(context, "pgLIBCustomFieldValues", null, "getGPDBCategoryHTMLForCreateAP", JPO.packArgs(mProgramMap), Map.class);
		return new JsonHelper().getJsonString(mResultMap);
	}
	public static String getSector(Context context,String []args) throws Exception {
		HashMap mResultMap = new HashMap();
		
		StringList slSelectable = new StringList(DomainConstants.SELECT_ID);
		slSelectable.add(DomainConstants.SELECT_NAME);
		
		MapList mlSectorList = DomainObject.findObjects(context,												//context
														TYPE_SECTOR,											//search type
														DomainConstants.QUERY_WILDCARD,							//search name
														DomainConstants.QUERY_WILDCARD,							//search revision
														DomainConstants.QUERY_WILDCARD,							//owner pattern
														ArtworkConstants.VAULT_ESERVICE_PRODUCTION,				//vault to be searched
														DomainConstants.SELECT_CURRENT+"=='"+STATE_ACTIVE+"'",		//bus where clause
														false,													//expand type
														slSelectable);											//bus selectable
		
		if(!mlSectorList.isEmpty()) {
			mlSectorList.sort("name","ascending","String");
			Iterator itrSector = mlSectorList.iterator();
			Map mSector = null;
			StringList slOfDisplayChoices = new StringList();
			StringList slOfChoices = new StringList();
			DomainObject dobj = null;
			while(itrSector.hasNext()){
				mSector = (Map) itrSector.next();
				slOfChoices.add((String)mSector.get(DomainConstants.SELECT_ID));
				slOfDisplayChoices.add((String)mSector.get(DomainConstants.SELECT_NAME));
			}
			mResultMap.put(STR_FIELD_DISPLAY_CHOICES,slOfDisplayChoices);
			mResultMap.put(STR_FIELD_CHOICES,slOfChoices);
		}
		return new JsonHelper().getJsonString(mResultMap);
	}
	public  static String getConnectedSubSector(Context context,String sSectorId) throws Exception {
		HashMap mResultMap = new HashMap();
		
		DomainObject dobj = DomainObject.newInstance(context, sSectorId);
		StringList slSelectable = new StringList(DomainConstants.SELECT_ID);
		slSelectable.add(DomainConstants.SELECT_NAME);
		
		MapList mlSubSector = dobj.getRelatedObjects(context,								//context
													RELATIONSHIP_PGPLIProductHierarchy,		//Relationship
													TYPE_SUBSECTOR,							//type
													slSelectable,							//object selectable
													null,									//rel selectable
													false,									//get To side
													true,									//get From side
													(short)1,								//recurse to level
													DomainConstants.SELECT_CURRENT+"=='"+STATE_ACTIVE+"'", //object where clause
													DomainConstants.EMPTY_STRING);				//relationship where clause
		if(BusinessUtil.isNotNullOrEmpty(mlSubSector)){
			mlSubSector.sort("name","ascending","String");
			Iterator itrSubSector = mlSubSector.iterator();
			Map mSubSector=null;
			StringList slOfDisplayChoices = new StringList();
			StringList slOfChoices = new StringList();
			while(itrSubSector.hasNext()){
				mSubSector= (Map) itrSubSector.next();
				slOfChoices.add((String)mSubSector.get(DomainConstants.SELECT_ID));
				slOfDisplayChoices.add((String)mSubSector.get(DomainConstants.SELECT_NAME));
			}
			mResultMap.put(STR_FIELD_DISPLAY_CHOICES,slOfDisplayChoices);
			mResultMap.put(STR_FIELD_CHOICES,slOfChoices);
		}
		return new JsonHelper().getJsonString(mResultMap);
	}
	public static  String getDefaultArtworkType(Context context,String []args) throws Exception {
		String sOutput=DomainConstants.EMPTY_STRING;
		
		BusinessObject busObj = new BusinessObject(TYPE_ARTWORKTYPE,DEFAULT_ARTWORKTYPE,"-","");
        if(busObj != null && busObj.exists(context)) {
        	sOutput = busObj.getObjectId(context);
        	if(busObj.isOpen()) {
        		busObj.close(context);
			}
        	sOutput = new StringBuffer(sOutput).append("|").append(DEFAULT_ARTWORKTYPE).toString();
        }
        return sOutput;
	}
}
