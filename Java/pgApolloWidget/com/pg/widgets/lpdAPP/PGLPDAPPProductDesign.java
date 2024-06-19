package com.pg.widgets.lpdAPP;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.png.apollo.pgApolloCommonUtil;
import com.png.apollo.pgApolloConstants;
import com.png.apollo.dashboard.util.ApolloWidgetServiceUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.Pattern;
import matrix.util.StringList;

public class PGLPDAPPProductDesign extends pgApolloConstants{
	

	
	public static JsonObject getAPPProductDesign(matrix.db.Context context , String strAPPId)throws Exception{


		JsonObjectBuilder output = Json.createObjectBuilder();
		JsonArray jsonArray = null;

		//Check is LPD APP
		Map programMap = new HashMap();
		programMap.put("objectId", strAPPId);
		programMap.put("objectType", TYPE_ASSEMBLED_PRODUCT_PART);
		String []args = JPO.packArgs(programMap);
		boolean isLPDAPP = (boolean)JPO.invoke(context, "pgDSMLayeredProductUtil", null, "isLayeredProductPart", args, Object.class);

		if(!isLPDAPP) {
			throw new MatrixException("Only LPD App is supported");
		}
		
		//Get Product Design

		String strselectables = pgApolloCommonUtil.getPageProperty(context, STR_APOLLO_CONFIG_PAGE_FILENAME, "pgApolloDashBoard.ProductDesign.AttributeList");
		StringList strSelectable = new StringList();
		if(null != strselectables && !strselectables.isEmpty())
		{
			strSelectable = StringUtil.split(strselectables, CONSTANT_STRING_PIPE);   
		}
		MapList productDesignMaplist = getProductDesignWithSelectables(context, strAPPId, strSelectable);
		jsonArray =ApolloWidgetServiceUtil.convertMapListToJsonArray(productDesignMaplist);
		output.add("APPProductdesign", jsonArray);
		return output.build();

	}
	public static final String STR_SELECT_FROM_PHYSICALID = "from.physicalid";
	public static final String VNAME = PropertyUtil.getSchemaProperty("attribute_PLMEntity.V_Name");
	public static final String STR_SELECT_FROM_VNAME = "from.attribute["+VNAME+"]";
	public static final String STR_SELECT_VNAME ="attribute["+VNAME+"]";
	
	public static MapList getProductDesignWithSelectables(Context context, String sObjectId, StringList strSelectable) throws MatrixException {
		String strLanguage = context.getSession().getLanguage();
		MapList mlPhysicalProduct = new MapList();
		if (UIUtil.isNotNullAndNotEmpty(sObjectId)) {
			strSelectable.add(SELECT_ID);
			strSelectable.add(SELECT_POLICY);
			strSelectable.add(SELECT_PHYSICAL_ID);
			strSelectable.add("current.access[read]");
			
			strSelectable.add(STR_SELECT_VNAME);
			StringList relSelects = new StringList();
			relSelects.add(STR_MAP_RESULT_KEY_ID_CONNECTION);
			relSelects.add(STR_SELECT_FROM_PHYSICALID);
			relSelects.add(STR_SELECT_FROM_VNAME);
			Pattern relPattern = new Pattern(pgApolloConstants.RELATIONSHIP_VPMINSTANCE);
			relPattern.addPattern(pgApolloConstants.RELATIONSHIP_VPMRepInstance);
			relPattern.addPattern(pgApolloConstants.RELATIONSHIP_PART_SPECIFICATION);
			Pattern typePattern = new Pattern(pgApolloConstants.TYPE_VPMREFERENCE);
			typePattern.addPattern(pgApolloConstants.TYPE_DRAWING);
			typePattern.addPattern(pgApolloConstants.TYPE_3DSHAPE);
			DomainObject domVPMRef = DomainObject.newInstance(context, sObjectId);
			mlPhysicalProduct = domVPMRef.getRelatedObjects(context, relPattern.getPattern(), typePattern.getPattern(),
					strSelectable, relSelects, false, true, (short) 0, "", "", 0);
		}
		
		Iterator mlPhysicalProductItr =mlPhysicalProduct.iterator();
		Map tempMap;
		String strtempType;
		String strtempCurrent;
		StringList hierarchyList;
		String strtoPhysicalId;
		while(mlPhysicalProductItr.hasNext()) {
			tempMap =(Map)mlPhysicalProductItr.next();
			strtempType =(String)tempMap.get(SELECT_TYPE);
			strtempCurrent =(String)tempMap.get(SELECT_CURRENT);
			tempMap.put(SELECT_CURRENT, EnoviaResourceBundle.getStateI18NString(context, (String)tempMap.get(SELECT_POLICY), strtempCurrent, strLanguage));
			tempMap.put("displayType",EnoviaResourceBundle.getTypeI18NString(context, strtempType, strLanguage));
			hierarchyList = new StringList();
			strtoPhysicalId =(String)tempMap.get(STR_SELECT_FROM_PHYSICALID);
			if(UIUtil.isNotNullAndNotEmpty(strtoPhysicalId)&& !sObjectId.equals(strtoPhysicalId)) {
				hierarchyList.add((String)tempMap.get(STR_SELECT_FROM_VNAME));
			}
			hierarchyList.add((String)tempMap.get(STR_SELECT_VNAME));
			tempMap.put("hierarchy",hierarchyList.toString());
		}
		
		return mlPhysicalProduct;
	}

}
