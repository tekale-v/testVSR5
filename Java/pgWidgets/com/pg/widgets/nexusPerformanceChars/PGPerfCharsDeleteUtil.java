package com.pg.widgets.nexusPerformanceChars;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.widgets.structuredats.PGStructuredATSUtil;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;

import matrix.db.Context;
import matrix.util.StringList;

public class PGPerfCharsDeleteUtil {

	private static final Logger logger = Logger.getLogger(PGPerfCharsDeleteUtil.class.getName());

	/**
	 * This method is used to delete connected Performance Characteristics from APP
	 * 
	 * @param context
	 * @param mpRequestMap
	 * @return String
	 * @throws Exception
	 */
	public String deletePCFromAPP(Context context, Map mpRequestMap) throws Exception {
		JsonObjectBuilder output = Json.createObjectBuilder();
		String strPCId = (String) mpRequestMap.get(PGPerfCharsConstants.KEY_LIST_OF_PC_IDS);
		String strReturnVal = null;
		String getSelectedKey = null;
		try {
			if (UIUtil.isNotNullAndNotEmpty(strPCId)) 
			{
				StringList slList = FrameworkUtil.split(strPCId, PGPerfCharsConstants.CONSTANT_STRING_PIPE);
				for (String strPerfChar : slList) {
					getSelectedKey = strPerfChar;
					if (UIUtil.isNotNullAndNotEmpty(getSelectedKey)) {
						DomainObject doObj = DomainObject.newInstance(context,getSelectedKey);
						String strPFInheritanceType = doObj.getInfo(context,"to["+PGPerfCharsConstants.RELATIONSHIP_CHARACTERISTIC+"].attribute[" + PGPerfCharsConstants.ATTR_PG_INHERITANCE_TYPE + "]");
						if(PGPerfCharsConstants.FILTER_LOCAL.equals(strPFInheritanceType))
						{
							PGStructuredATSUtil pgStructuredATSUtil = new PGStructuredATSUtil();
							strReturnVal = pgStructuredATSUtil.deleteObject(context, getSelectedKey);
						}else
						{
							output.add(PGPerfCharsConstants.KEY_STATUS, PGPerfCharsConstants.MESSAGE_PC_LOCAL_DEL );
							strReturnVal = output.build().toString();
						}
						
					} else {
						output.add(PGPerfCharsConstants.KEY_STATUS, PGPerfCharsConstants.STRING_ERR_DELETE);
						strReturnVal = output.build().toString();
					}
				}
			}
		} catch (Exception excep) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_DELETE_UTIL, excep);
			output.add(PGWidgetConstants.KEY_ERROR, excep.getMessage());
			output.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(excep));
			return output.build().toString();
		}
		return strReturnVal;
	}
}