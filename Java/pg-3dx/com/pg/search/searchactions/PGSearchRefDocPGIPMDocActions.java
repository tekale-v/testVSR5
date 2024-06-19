package com.pg.search.searchactions;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.search_navigate.delegation.ActionAlreadyAddedException;
import com.dassault_systemes.search_navigate.delegation.ISearchActionsDelegation;
import com.dassault_systemes.search_navigate.delegation.InvalidActionException;
import com.dassault_systemes.search_navigate.delegation.SearchActionDelegationAdp;
import com.dassault_systemes.search_navigate.delegation.utils.ActionsHelper;
import com.dassault_systemes.search_navigate.delegation.utils.ActionsPerObject;
import com.dassault_systemes.search_navigate.delegation.utils.ObjectInfo;
import com.dassault_systemes.search_navigate.delegation.utils.SearchAction;
import com.matrixone.apps.common.CommonDocument;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.json.JSONObject;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

@SuppressWarnings("deprecation")
public class PGSearchRefDocPGIPMDocActions extends SearchActionDelegationAdp implements ISearchActionsDelegation {
	private final Logger _logger = Logger.getLogger("com.pg.search.searchactions.PGSearchTechSpecActions");
	private final String TYPE_PGIPMDOCUMENT = PropertyUtil.getSchemaProperty(null, "type_pgIPMDocument");
	private final String ID_PG_REFDOC_PGIPMDOC_SEARCH_DOWNOAD = "pgRefDocPgIPMDocAction_Download";
	private final String STRING_RESOURCE_KEY_DOWNLOAD = "emxFramework.Common.pgDownloadRefDocIPMDoc";
	private final String STRING_RESOURCE_FRAMEWORK = "emxFrameworkStringResource";
	private final String SOURCE_JS_PG_REFDOC_PGIPMDOC_SEARCH_ACTIONS = "DS/PGCommonSearchLocalActions/PGSearchLocalActionsForRefDocPGIPMDoc";
	private final String ICON_DOWNLOAD = "download";
	private final static String APPLICATION_3DSPACE = "3DSpace";
	private final static String APPLICATION_ENOVIA = "enovia";

	public void computeActions(ActionsHelper helper) {
		_logger.entering(this.getClass().getName(), "computeActions");
		try {
			Context context = helper.getContext();
			List<ObjectInfo> objects = helper.getObjects();
			JSONObject obj = helper.getRequestData();
			JSONObject joApplicativeInfo = obj.getJSONObject("applicativeInfo");
			JSONObject joClientAppInfo = joApplicativeInfo.getJSONObject("clientAppInfo");
			String strIdentifier = joClientAppInfo.getString("identifier");
			
			if (!APPLICATION_3DSPACE.equalsIgnoreCase(strIdentifier) && !APPLICATION_ENOVIA.equalsIgnoreCase(strIdentifier)) {
				_logger.log(Level.INFO, "No custom right click actions because application is: {0}", strIdentifier);
				return;
			}
			
			StringList slobjectSelects = new StringList(DomainConstants.SELECT_ID);
			String strI18nDownloadCommandName = EnoviaResourceBundle.getProperty(context, STRING_RESOURCE_FRAMEWORK, (Locale) context.getLocale(), STRING_RESOURCE_KEY_DOWNLOAD);
			
			ActionsPerObject actions = null;
			String ObjectId = null;
			DomainObject domObj = null;
			MapList pgIPMDocList = null;
			Map<?, ?> tempMap = null;
			Map<?, ?> contentObjectMap = null;
			SearchAction downloadAction = null;
			for (int objCount = 0; objCount < objects.size(); objCount++) {
				actions = objects.get(objCount).getActionObject();
				try {
					try {
						ObjectId = objects.get(objCount).get_id();
						// domObj = DomainObject.newInstance(context, ObjectId);
						// pgIPMDocList = domObj.getRelatedObjects(context, // Context
						// DomainConstants.RELATIONSHIP_REFERENCE_DOCUMENT, // relationship pattern
						// TYPE_PGIPMDOCUMENT, // Type pattern
						// slobjectSelects, // object selects
						// null, // relationship selects
						// false, // to direction
						// true, // from direction
						// (short) 1, // recursion level
						// null, // object where clause
						// null, // relationship where clause
						// 1); // limit
						// if (pgIPMDocList != null && pgIPMDocList.size() > 0) {
						// tempMap = (Map<?, ?>) pgIPMDocList.get(0);
						// ObjectId = (String) tempMap.get(DomainConstants.SELECT_ID);
						// contentObjectMap = CommonDocument.createObjMap(context, ObjectId);
						// if (CommonDocument.canDownload(context, contentObjectMap) && incontextMode.equalsIgnoreCase("false")) {
						// String sDownloadTitle = EnoviaResourceBundle.getProperty(context,"emxComponentsStringResource", new
						// Locale(languageStr),"emxComponents.FileDownload.Download");
						downloadAction = new SearchAction(ID_PG_REFDOC_PGIPMDOC_SEARCH_DOWNOAD, ICON_DOWNLOAD, strI18nDownloadCommandName,
								SOURCE_JS_PG_REFDOC_PGIPMDOC_SEARCH_ACTIONS);
						downloadAction.set_multisel(false);
						actions.addAction(downloadAction);
						// }
						// }
					} catch (ActionAlreadyAddedException e) {
						_logger.log(Level.WARNING, "Encountered ActionAlreadyAddedException for the object ", e);
					}
				} catch (InvalidActionException e) {
					_logger.log(Level.SEVERE, "Cannot add the actions to the object ", e);
				} catch (Exception ex) {
					_logger.log(Level.SEVERE, "Cannot add the actions to the object ", ex);
				}
			}
		} catch (MatrixException e) {
		}
		_logger.exiting(this.getClass().getName(), "computeActions");
	}
}
