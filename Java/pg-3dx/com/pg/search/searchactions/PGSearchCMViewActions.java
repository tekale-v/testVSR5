//Added by DSM(Sogeti) - for 2022x.04 Dec CW PDF Views Req#47389 
package com.pg.search.searchactions;

import java.util.List;
import java.util.Locale;
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
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.json.JSONObject;
import matrix.db.Context;
import matrix.util.MatrixException;

@SuppressWarnings("deprecation")
public class PGSearchCMViewActions extends SearchActionDelegationAdp implements ISearchActionsDelegation {
	private final Logger _logger = Logger.getLogger("com.pg.search.searchactions.PGSearchTechSpecActions");
	private final String ID_PG_REFDOC_PGIPMDOC_SEARCH_DOWNOAD = "pgCMView_Download";
	private final String STRING_RESOURCE_KEY_DOWNLOAD = "emxCPN.CommandLabel.ContractPackagingView";
	private final String STRING_RESOURCE_FRAMEWORK = "emxCPNStringResource";
	private final String SOURCE_JS_PG_REFDOC_PGIPMDOC_SEARCH_ACTIONS = "DS/PGCommonSearchLocalActions/PGSearchLocalActionsForCMView";
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
			
			String strI18nDownloadCommandName = EnoviaResourceBundle.getProperty(context, STRING_RESOURCE_FRAMEWORK, (Locale) context.getLocale(), STRING_RESOURCE_KEY_DOWNLOAD);
			
			ActionsPerObject actions = null;
			SearchAction downloadAction = null;
			for (int objCount = 0; objCount < objects.size(); objCount++) {
				actions = objects.get(objCount).getActionObject();
				try {
					try {
						downloadAction = new SearchAction(ID_PG_REFDOC_PGIPMDOC_SEARCH_DOWNOAD, ICON_DOWNLOAD, strI18nDownloadCommandName,
								SOURCE_JS_PG_REFDOC_PGIPMDOC_SEARCH_ACTIONS);
						downloadAction.set_multisel(false);
						actions.addAction(downloadAction);
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
