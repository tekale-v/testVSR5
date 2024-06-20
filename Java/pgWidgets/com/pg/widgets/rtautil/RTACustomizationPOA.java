package com.pg.widgets.rtautil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.awl.dao.CustomizationPOA;
import com.matrixone.apps.awl.util.BusinessUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class RTACustomizationPOA {
	
	private static final Logger logger = Logger.getLogger(RTAUtil.class.getName());
	
	public static String connectCusomizationPOA(Context context, String paramString) throws Exception {
		String strOut = DomainConstants.EMPTY_STRING;
		JsonObjectBuilder output = Json.createObjectBuilder();
		try {
			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(paramString);
		 String selectedPOAIds = jsonInputData.getString(RTAUtil.KEY_SELECTED_POA_IDs);
		 //poaObjectIds are the search POA object ID
		 String strpoaObjectIds = jsonInputData.getString(RTAUtil.KEY_SELECTED_POA_LIST );
		 String[] strlpoaObjectIds = strpoaObjectIds.split(PGWidgetConstants.KEY_COMMA_SEPARATOR);
		StringList selectdObjList = BusinessUtil.toStringList(strlpoaObjectIds);	
		String strConnectComprisedPOAId = null;
		String strConnectComprisedPOAName = null;
		boolean isComprisedPOAExist = false;
		DomainObject domComprisedPOA = null;
		StringList slAlreadyConnectedComprisedPOA = new StringList();	
			
		Map paramMap = new HashMap();
		paramMap.put(RTAUtil.KEY_SELECTED_POA_IDs, selectedPOAIds);
			StringList slExcludeOIDList = (StringList) JPO.invoke(context, "AWLPOAUI", null,
					"getExcludePOAsForComprised", JPO.packArgs(paramMap), StringList.class);
			for (int i = 0; i < selectdObjList.size(); i++) {
			strConnectComprisedPOAId = (String)selectdObjList.get(i);
			if(UIUtil.isNotNullAndNotEmpty(strConnectComprisedPOAId)){					
				domComprisedPOA = DomainObject.newInstance(context, strConnectComprisedPOAId);	
				strConnectComprisedPOAName = domComprisedPOA.getName(context);				
				if(slExcludeOIDList.contains(strConnectComprisedPOAId)) {	 
					isComprisedPOAExist = true;
					slAlreadyConnectedComprisedPOA.add(strConnectComprisedPOAName);				  
				}	
			}			
		}
			if (isComprisedPOAExist) {
			String strLanguage = context.getSession().getLanguage();
				String errMsg = EnoviaResourceBundle.getProperty(context, "AWL",
						"emxAWL.Alert.SelectedPOAAlreadyConnected", strLanguage);
			errMsg += String.join(", ", slAlreadyConnectedComprisedPOA);
			strOut = errMsg;
				output.add(PGWidgetConstants.KEY_ERROR, strOut).build().toString();
		}else {	
				String warningMsg = CustomizationPOA.addComprisedPOAs(context, Arrays.asList(selectedPOAIds.split(",")),
						Arrays.asList(strlpoaObjectIds));
			strOut = warningMsg;
				output.add(PGWidgetConstants.KEY_WARNING, strOut).build().toString();
			}
			
			if (strOut.equals("")) {
				output.add(PGWidgetConstants.KEY_SUCCESS, "Selected POA connected successfully").build().toString();
		}
		}catch(Exception e) {
			logger.log(Level.SEVERE, RTAUtil.EXCEPTION_MESSAGE, e);
			output.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			throw e;
		}
		return output.build().toString();
	}

}
