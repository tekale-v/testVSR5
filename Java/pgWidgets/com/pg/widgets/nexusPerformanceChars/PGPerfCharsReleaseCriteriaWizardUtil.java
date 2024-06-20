package com.pg.widgets.nexusPerformanceChars;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import com.dassault_systemes.evp.messaging.utils.StringUtil;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.framework.ui.UIUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.widgets.util.PGWidgetConstants;
import com.pg.widgets.util.PGWidgetUtil;
import matrix.db.Page;

import matrix.db.Context;
import matrix.util.StringList;

public class PGPerfCharsReleaseCriteriaWizardUtil {

	private static final Logger logger = Logger.getLogger(PGPerfCharsReleaseCriteriaWizardUtil.class.getName());

	/**
	 * 
	 * Method to get the data for 'Release Criteria' wizard
	 * 
	 * @param context
	 * @param strJsonInput
	 * @return
	 * @throws FrameworkException
	 */
	String fetchReleaseCriteriaWizardData(Context context, String strJsonInput) throws FrameworkException {

		JsonObjectBuilder jsonReturnObj = Json.createObjectBuilder();
		boolean bIsContextPushed = false;

		try {
			ContextUtil.pushContext(context, PGPerfCharsConstants.PERSON_AGENT, DomainConstants.EMPTY_STRING,
					DomainConstants.EMPTY_STRING);
			bIsContextPushed = true;

			JsonObject jsonInputData = PGWidgetUtil.getJsonFromJsonString(strJsonInput);
			String strParentId = jsonInputData.getString(DomainConstants.SELECT_ID);

			StringList slObjSelectList = new StringList(2);
			slObjSelectList.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED);
			slObjSelectList.add(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
			DomainObject dobPDPObj = DomainObject.newInstance(context, strParentId);
			Map<?, ?> mpPDPInfoMap = dobPDPObj.getInfo(context, slObjSelectList);

			String strReleaseCriteriaRequired = (String) mpPDPInfoMap
					.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_STRUCTUREDRELEASECRITERIAREQUIRED);
			if (strReleaseCriteriaRequired == null) {
				strReleaseCriteriaRequired = "";
			}
			String strNexusPerfCharRequired = (String) mpPDPInfoMap
					.get(PGPerfCharsConstants.SELECT_ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED);
			if (strNexusPerfCharRequired == null) {
				strNexusPerfCharRequired = "";
			}

			jsonReturnObj.add(PGPerfCharsConstants.ATTRIBUTE_PG_STRUCTURE_RELEASE_CRIT_REQUIRED,
					strReleaseCriteriaRequired);
			jsonReturnObj.add(PGPerfCharsConstants.ATTRIBUTE_NEXUS_STRUCTURED_PERFCHARSREQUIRED,
					strNexusPerfCharRequired);

			Page pgCPNPropertiesPage = new Page(PGPerfCharsConstants.KEY_CPN_PROPERTIES);
			pgCPNPropertiesPage.open(context);
			String strPageContent = pgCPNPropertiesPage.getContents(context);
			pgCPNPropertiesPage.close(context);

			JsonObjectBuilder jsonPropertiesObj = Json.createObjectBuilder();
			StringList slPropertiesInfoList = StringUtil.split(strPageContent, "\n");
			int iListSize = slPropertiesInfoList.size();
			for (int i = 0; i < iListSize; i++) {
				String strProperties = slPropertiesInfoList.get(i);
				if (strProperties.contains(PGPerfCharsConstants.PROPERTIES_KEY_RELEASE_CRITERIA)) {
					StringList slRelCriteriaPropertiesList = StringUtil.split(strProperties, "=");
					if (slRelCriteriaPropertiesList.size() > 1) {
						jsonPropertiesObj.add(slRelCriteriaPropertiesList.get(0), slRelCriteriaPropertiesList.get(1));
					}
				}
			}
			jsonReturnObj.add(PGPerfCharsConstants.KEY_PROPERTIES, jsonPropertiesObj);
			
			JsonArrayBuilder jsonGradingParamArray = getGradingParamValues(context, jsonInputData);
			jsonReturnObj.add(PGPerfCharsConstants.KEY_GRADING, jsonGradingParamArray);
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, PGPerfCharsConstants.EXCEPTION_MESSAGE_PERF_CHAR_RELEASE_CRITERIA_UTIL, e);
			jsonReturnObj.add(PGWidgetConstants.KEY_ERROR, e.getMessage());
			jsonReturnObj.add(PGWidgetConstants.KEY_TRACE, PGWidgetUtil.getExceptionTrace(e));

		} finally {
			if (bIsContextPushed) {
				ContextUtil.popContext(context);
			}
		}

		return jsonReturnObj.build().toString();

	}

	/**
	 * Method to get the drop down values for 'Grading' field
	 * 
	 * @param context
	 * @param jsonInputData
	 * @throws FrameworkException
	 */
	private JsonArrayBuilder getGradingParamValues(Context context, JsonObject jsonInputData)
			throws FrameworkException {
		JsonArrayBuilder jsonGradingParamArray = Json.createArrayBuilder();
		StringList slParamValuesList = new StringList();
		if (jsonInputData.containsKey(PGPerfCharsConstants.KEY_NEXUS_PARAM_ID)
				&& UIUtil.isNotNullAndNotEmpty(jsonInputData.getString(PGPerfCharsConstants.KEY_NEXUS_PARAM_ID))) {
			String strParameterId = jsonInputData.getString(PGPerfCharsConstants.KEY_NEXUS_PARAM_ID);
			DomainObject dobParameterObj = DomainObject.newInstance(context, strParameterId);
			slParamValuesList = dobParameterObj.getInfoList(context,
					PGPerfCharsConstants.SELECT_ATTRIBUTE_PGNEXUSPARAMETERVALUES);
		} else if (jsonInputData.containsKey(PGPerfCharsConstants.KEY_LIST_OF_PC_IDS)
				&& UIUtil.isNotNullAndNotEmpty(jsonInputData.getString(PGPerfCharsConstants.KEY_LIST_OF_PC_IDS))) {
			String strPCObjectId = jsonInputData.getString(PGPerfCharsConstants.KEY_LIST_OF_PC_IDS);
			DomainObject dobPerfCharObj = DomainObject.newInstance(context, strPCObjectId);
			String strParamId = dobPerfCharObj.getAttributeValue(context,
					PGPerfCharsConstants.ATTR_PG_NEXUS_PC_PARAMETER_ID);
			if (UIUtil.isNotNullAndNotEmpty(strParamId)) {
				DomainObject dobParameterObj = DomainObject.newInstance(context, strParamId);
				slParamValuesList = dobParameterObj.getInfoList(context,
						PGPerfCharsConstants.SELECT_ATTRIBUTE_PGNEXUSPARAMETERVALUES);
			}
		}

		int iListSize = slParamValuesList.size();
		for (int i = 0; i < iListSize; i++) {
			String strGradingValue = slParamValuesList.get(i);
			jsonGradingParamArray.add(strGradingValue);
		}

		return jsonGradingParamArray;
	}

}