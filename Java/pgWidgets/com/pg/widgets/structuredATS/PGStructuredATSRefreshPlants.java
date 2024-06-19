package com.pg.widgets.structuredats;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.util.StringList;

public class PGStructuredATSRefreshPlants 
{

	private static final Logger logger = Logger.getLogger(PGStructuredATSRefreshPlants.class.getName());
	public String connectRefreshPlants(Context context, Map<?, ?> mpRequestMap) throws Exception
	{
		DomainObject doSATS  = null;
		MapList mlATSConnectedPlants = null;
		HashSet<String> hSetUniquePlants = new HashSet<>();
		HashSet<String> hCopySetUniquePlants = new HashSet<>();
		String strPlantId = null;
		Map mConnectedPlant = null;
		String strSATSId = null;
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		try 
		{
			StringList slBusSelects = new StringList(DomainConstants.SELECT_ID);
			String strBusWhere = DomainConstants.EMPTY_STRING;
			strSATSId = (String) mpRequestMap.get(DomainConstants.SELECT_ID);
			String sPlantAttrbuteListArray = mpRequestMap.get(PGStructuredATSConstants.KEY_OBJECT_SELECTS).toString();
			StringList slAttributesMapping = FrameworkUtil.split(sPlantAttrbuteListArray,",");
			if(UIUtil.isNotNullAndNotEmpty(strSATSId))
			{
				doSATS = DomainObject.newInstance(context, strSATSId);
				hSetUniquePlants = getConnectedUniquePlants(context, doSATS);
				hCopySetUniquePlants.addAll(hSetUniquePlants);
				mlATSConnectedPlants =  getConnectedObjects(context, doSATS, PGStructuredATSConstants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY,PGStructuredATSConstants.TYPE_PLANT, true, false , slBusSelects, new StringList(),strBusWhere, strBusWhere );
				int iSize = mlATSConnectedPlants.size();
				for(int iPlant=0; iPlant<iSize; iPlant++) 
				{
					mConnectedPlant = (Map) mlATSConnectedPlants.get(iPlant);
					strPlantId = (String)mConnectedPlant.get(DomainConstants.SELECT_ID);
					if(hSetUniquePlants.contains(strPlantId))
						hSetUniquePlants.remove(strPlantId);			
				}
			jsonArrObjInfo = connectUniquePlantsToSATS(context, doSATS,hSetUniquePlants,slAttributesMapping);
			disconnectUnRelatedPlantsToSATS(context,strSATSId ,hCopySetUniquePlants);
			
			}
		}catch (Exception excep) 
		{	
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PLANT_REFRESH, excep);
		}
		return jsonArrObjInfo.build().toString();
	}
	private JsonArrayBuilder connectUniquePlantsToSATS(Context context, DomainObject doSATS, HashSet hSetUniquePlantsFinal, StringList slAttributesMapping) throws FrameworkException 
	{
		DomainObject dObjPlantObject	= null;
		Map mObjectAttributeValues = null;
		JsonObjectBuilder jsonObjInfo = Json.createObjectBuilder();
		JsonArrayBuilder jsonArrObjInfo = Json.createArrayBuilder();
		if(hSetUniquePlantsFinal.size()>0)
		{
			for (Object obj : hSetUniquePlantsFinal) 
			{
				try 
				{
					dObjPlantObject	= DomainObject.newInstance(context, (String)obj);
					DomainRelationship.connect(context, dObjPlantObject, PGStructuredATSConstants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY , doSATS);
					mObjectAttributeValues = dObjPlantObject.getInfo(context, slAttributesMapping);		
					jsonObjInfo = getJsonObjectFromMap(mObjectAttributeValues);
				}catch (Exception excep)
				{
					logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PLANT_REFRESH, excep);
				}
			jsonArrObjInfo.add(jsonObjInfo);
			}
		}
		return jsonArrObjInfo;
	}
	public MapList getConnectedObjects(Context context, DomainObject doObj, String strRelPattern, String strTypePattern, boolean bIsTo, boolean bIsFrom,StringList slBusSelects , StringList slRelSelects, String strBusWhere, String strRelWhere)throws Exception
	{
		MapList mlConnectedObjects = null;
		try 
		{
			mlConnectedObjects=	doObj.getRelatedObjects(context,
								strRelPattern,  									// rel pattern
								strTypePattern,										// type pattern
								slBusSelects,										// bus where
								slRelSelects,										// rel where
								bIsTo,												// get To relationships
								bIsFrom,											//get From relationships
								(short)0,											// the number of levels to expand, 0 equals expand all.
								strBusWhere,   										// where clause to apply to objects, can be empty ""
								strRelWhere,										// where clause to apply to relationship, can be empty ""
								0);													//The max number of Objects to get in the expand. 0 to return all the data available.

		}catch (Exception excep) 
		{	
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PLANT_REFRESH, excep);
		}
		return mlConnectedObjects;
	}
	public HashSet<String> getConnectedUniquePlants(Context context, DomainObject doATS) throws Exception
	{
		MapList mlConnectedPlants = null;
		MapList mlConnectedPDP = null;
		String sPDPId = DomainConstants.EMPTY_STRING;
		String sPlantId = DomainConstants.EMPTY_STRING;
		String strBusWhere = DomainConstants.EMPTY_STRING;
		Map mConnectedPDP = null;
		Map mPDPMap = null;
		HashSet<String> hSetUniquePlants = new HashSet<>();
		try {
			DomainObject doPDP  = null;
			StringList slBusSelects = new StringList(DomainConstants.SELECT_ID);
			slBusSelects.add(DomainConstants.SELECT_NAME);
			StringList slRelSelects = new StringList();
			mlConnectedPDP =  getConnectedObjects(context, doATS, PGStructuredATSConstants.REL_AUTHORISEDTEMPORARYSPECIFICATION, "*", false,true,slBusSelects, slRelSelects,strBusWhere, strBusWhere);
			for(int iConnectedObj =0 ; iConnectedObj<mlConnectedPDP.size() ; iConnectedObj++) 
			{
				mConnectedPDP = (Map)mlConnectedPDP.get(iConnectedObj);
				sPDPId = (String) mConnectedPDP.get(DomainConstants.SELECT_ID);
				doPDP = DomainObject.newInstance(context, sPDPId);
				mlConnectedPlants = getConnectedObjects(context, doPDP, PGStructuredATSConstants.RELATIONSHIP_MANUFACTURINGRESPONSIBILITY,PGStructuredATSConstants.TYPE_PLANT, true, false , slBusSelects, slRelSelects,strBusWhere, strBusWhere );
				for(int iConnectedPlants =0 ; iConnectedPlants<mlConnectedPlants.size() ; iConnectedPlants++) 
				{
					mPDPMap = (Map)mlConnectedPlants.get(iConnectedPlants);
					sPlantId = (String) mPDPMap.get(DomainConstants.SELECT_ID);
					hSetUniquePlants.add(sPlantId);
				}
			}
		}catch (Exception excep) 
		{	
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PLANT_REFRESH, excep);
		}
		return hSetUniquePlants;
	}
		/**
	 * Method to convert Map to JSON object
	 * @param mObjectAttributeValues
	 * @return
	 */
	private JsonObjectBuilder getJsonObjectFromMap(Map<?,?> mObjectAttributeValues) {
		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		for (Map.Entry<?, ?> entry : mObjectAttributeValues.entrySet()) {
			jsonReturnObj.add((String) entry.getKey(), (String) entry.getValue());
		}
		return jsonReturnObj;
	}
	
	private void disconnectUnRelatedPlantsToSATS(Context context, String strSATSId, HashSet<String> hSetUniquePlantsFinal) throws FrameworkException 
	{
		JsonObjectBuilder output = Json.createObjectBuilder();
		MapList mlgetRelatedSATS = new MapList();
		String strBusWhere = DomainConstants.EMPTY_STRING;
		try {
			StringList slRelSelects = new StringList(DomainRelationship.SELECT_ID);
			StringList objectSelects = new StringList(DomainConstants.SELECT_ID);
			DomainObject dObjSATS = DomainObject.newInstance(context,strSATSId);
			mlgetRelatedSATS =  getConnectedObjects(context, dObjSATS, DomainConstants.RELATIONSHIP_MANUFACTURING_RESPONSIBILITY, PGStructuredATSConstants.TYPE_PLANT, true,false,objectSelects, slRelSelects,strBusWhere, strBusWhere);

			int sMapSize = mlgetRelatedSATS.size();
			 for ( int j=0; j < sMapSize ; j++)
			 {
				Map mpGetSelectedKey = (Map)mlgetRelatedSATS.get(j);
				String strPlantsRelId = (String) mpGetSelectedKey.get(DomainRelationship.SELECT_ID);
				String strPlantsId = (String) mpGetSelectedKey.get(DomainConstants.SELECT_ID);
				if (!hSetUniquePlantsFinal.contains(strPlantsId)) {
					PGStructuredATSUtil pgStructuredATSUtil = new PGStructuredATSUtil();
					pgStructuredATSUtil.deleteRelationship(context, strPlantsRelId);
				}
			}
		}
		catch (Exception excep) {
			logger.log(Level.SEVERE, PGStructuredATSConstants.EXCEPTION_MESSAGE_SATS_PERFORMANCE_UTIL, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
		}
	}
}
