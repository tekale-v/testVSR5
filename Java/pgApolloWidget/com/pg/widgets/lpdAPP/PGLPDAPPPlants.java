package com.pg.widgets.lpdAPP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.dassault_systemes.enovia.gls.common.util.PRSPUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.dashboard.util.ApolloWidgetServiceUtil;

import matrix.db.JPO;
import matrix.util.MatrixException;

public class PGLPDAPPPlants extends pgApolloConstants{
	

	public static final String ATTR_PGISAUTHORIZEDTO_USE = PropertyUtil.getSchemaProperty("attribute_pgIsAuthorizedtoUse");
	public static final String ATTR_PGISAUTHORIZEDTO_PRODUCE = PropertyUtil.getSchemaProperty("attribute_pgIsAuthorizedtoProduce");
	public static final String ATTR_PGIS_ACTIVATED = PropertyUtil.getSchemaProperty("attribute_pgIsActivated");
	
	public static final String SELECT_ATTR_PGISAUTHORIZEDTO_USE = (new StringBuilder()).append("attribute[").append(ATTR_PGISAUTHORIZEDTO_USE).append("]").toString();
	public static final String SELECT_ATTR_PGISAUTHORIZEDTO_PRODUCE = (new StringBuilder()).append("attribute[").append(ATTR_PGISAUTHORIZEDTO_PRODUCE).append("]").toString();
	public static final String SELECT_ATTR_PGIS_ACTIVATED = (new StringBuilder()).append("attribute[").append(ATTR_PGIS_ACTIVATED).append("]").toString();
	public static final String INIT_CAPS_NO = "No";
	public static final String INIT_CAPS_YES = "Yes";
	public static JsonObject getAPPPlants(matrix.db.Context context , String strAPPId)throws MatrixException{

		
		
		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArray jsonArray = null;
		
		//Check is LPD APP
		Map paramMap = new HashMap();
		paramMap.put("objectId", strAPPId);
		paramMap.put("objectType", TYPE_ASSEMBLED_PRODUCT_PART);
		String[] methodargs = JPO.packArgs(paramMap);
		boolean isLPDAPP = (boolean)JPO.invoke(context, "pgDSMLayeredProductUtil", null, "isLayeredProductPart", methodargs, Object.class);
		
		if(!isLPDAPP) {
			throw new MatrixException("Only LPD APP is supported");
		}
		
		
		Map programMap = new HashMap();
		strAPPId =PRSPUtil.convertToObjectId(context, strAPPId);
		programMap.put("objectId", strAPPId);
		String []args = JPO.packArgs(programMap);
		
		
		
		
		//Check if user has access for Plant command or not
		boolean hasAccessOnPlant = (boolean)JPO.invoke(context, "pgDSOUtil", null, "hasPlantAccessInPartCategory", args, Object.class);
		
		if(!hasAccessOnPlant) {
			throw new MatrixException("You do not have access to Plant Information");
		}
		
		MapList plantList =(MapList)JPO.invoke(context, "pgIPMProductData", null, "getPlants", args, Object.class);
		MapList finalPlantList =convertDisplayValueMapList(plantList);
		jsonArray =ApolloWidgetServiceUtil.convertMapListToJsonArray(finalPlantList);
		output.add("APPPlants", jsonArray);
		output.add("Message",EMPTY_STRING);
		
		return output.build();

	}
	
	private static MapList convertDisplayValueMapList(MapList plantList) {
		MapList returnList = new MapList();
		Iterator plantListItr =plantList.iterator();
		Map plantMap;
		String strValue;
		while(plantListItr.hasNext()) {
			plantMap =(Map)plantListItr.next();
			strValue =(String)plantMap.get(SELECT_ATTR_PGISAUTHORIZEDTO_PRODUCE);
			strValue=(strValue.equalsIgnoreCase(STR_TRUE_FLAG))?INIT_CAPS_YES:INIT_CAPS_NO;
			plantMap.put(SELECT_ATTR_PGISAUTHORIZEDTO_PRODUCE, strValue);
			strValue =(String)plantMap.get(SELECT_ATTR_PGISAUTHORIZEDTO_USE);
			strValue=(strValue.equalsIgnoreCase(STR_TRUE_FLAG))?INIT_CAPS_YES:INIT_CAPS_NO;
			plantMap.put(SELECT_ATTR_PGISAUTHORIZEDTO_USE, strValue);
			strValue =(String)plantMap.get(SELECT_ATTR_PGIS_ACTIVATED);
			strValue=(strValue.equalsIgnoreCase(STR_TRUE_FLAG))?INIT_CAPS_YES:INIT_CAPS_NO;
			plantMap.put(SELECT_ATTR_PGIS_ACTIVATED, strValue);
			returnList.add(plantMap);
		}
		return returnList;
	}

}
