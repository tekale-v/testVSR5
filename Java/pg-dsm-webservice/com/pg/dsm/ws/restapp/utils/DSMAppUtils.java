package com.pg.dsm.ws.restapp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.ws.enumeration.DSMConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.BusinessObject;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;

/**
 * @author DSM(Sogeti)
 *
 */
public class DSMAppUtils {

	private Context ctx;
	private String objId;
	private String tableConfig;
	private String jsonOutput;
	private String program; 

	/**
	 * @param context
	 * @param objectId
	 */
	public DSMAppUtils(Context context, String objectId, String program) {
		this.ctx = context;
		this.objId = objectId;
		this.program = program;
	}

	/**
	 * @param tableConfig
	 * @return
	 */
	public DSMAppUtils setTableConfig(String tableConfig) {
		this.tableConfig = tableConfig;
		return this;
	}

	/**
	 * @return the _jsonOutput
	 */
	public String getJsonOutput() {
		return jsonOutput;
	}

	/**
	 * @param jsonOutput the _jsonOutput to set
	 */
	public void setJsonOutput(String jsonOutput) {
		this.jsonOutput = jsonOutput;
	}

	/**
	 * To fetch market clearance details
	 * 
	 * @throws JSONException
	 */
	public DSMAppUtils execute() throws JSONException {
		List tableConfigList = JSONUtils.jsonToList(new JSONArray(this.tableConfig));
		HashMap paramMap = new HashMap();
		paramMap.put(DSMConstants.TABLE_CONFIG.getValue(), tableConfigList);
		Object obj = new ProgramUtils(this.ctx, this.objId).addMethodParameters(JPO.packArgs(paramMap))
				.execute(this.program);
		putIntoJSON(obj);
		return this;
	}

	/**
	 * 
	 */
	private void putIntoJSON(Object obj) {
		Map map = new HashMap();
		List<?> tableConfigList = null;
		try {
			tableConfigList = JSONUtils.jsonToList(new JSONArray(this.tableConfig));
		} catch (JSONException e) {
			Logger.getLogger(DSMAppUtils.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		}
		String sActionType = DomainConstants.EMPTY_STRING;
		Map<?, ?> configMap ;
		if (tableConfigList != null && !tableConfigList.isEmpty()) {
			configMap = (Map) tableConfigList.get(0);
			if(configMap.containsKey(DSMConstants.ACTION_SERVICE.getValue())){
				sActionType = (String) configMap.get(DSMConstants.ACTION_SERVICE.getValue());
			}
		}
		if (obj != null) {
			if(UIUtil.isNotNullAndNotEmpty(sActionType) && sActionType.equalsIgnoreCase(DSMConstants.COMMAND.getValue())){
				map.put(DSMConstants.OBJECT.getValue(), obj);
			}else{
				map.put(DSMConstants.OBJECT_LIST.getValue(), obj);
				map.put(DSMConstants.LICENSE_KEY.getValue(), getAgGridLicenseKey(this.ctx));
			}
		} else {
				map.put(DSMConstants.OBJECT_LIST.getValue(), new HashMap<Object, Object>());
				map.put(DSMConstants.LICENSE_KEY.getValue(), getAgGridLicenseKey(this.ctx));
		}

		this.setJsonOutput(JSONUtils.jaxbObjectToJSON(map));
	}

	/**
	 * @param context
	 * @return
	 * @throws MatrixException
	 */
	public static String getAgGridLicenseKey(Context context) {
		String sLicenseKey = "";
		BusinessObject boConfig;
		try {
			boConfig = new BusinessObject(DSMConstants.MOS_CONFIG_OBJECT_TYPE.getValue(),
					DSMConstants.MOS_CONFIG_OBJECT_NAME.getValue(), DSMConstants.MOS_CONFIG_OBJECT_REVISION.getValue(),
					pgV3Constants.VAULT_ESERVICEPRODUCTION);

			if (boConfig.exists(context)) {
				sLicenseKey = boConfig.getAttributeValues(context, DSMConstants.AG_GRID_LINCENSE_ATTRIBUTE.getValue())
						.getValue().trim();
			}
		} catch (MatrixException e) {
			Logger.getLogger(DSMAppUtils.class.getName()).log(Level.WARNING, "Got exception: {0}", e.toString());
		}
		return sLicenseKey;
	}
}
