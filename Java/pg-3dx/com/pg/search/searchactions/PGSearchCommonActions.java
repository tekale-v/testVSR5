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
import com.dassault_systemes.search_navigate.delegation.doubleclick.DoubleClickAction;
import com.dassault_systemes.search_navigate.delegation.doubleclick.DoubleClickActionHelper;
import com.dassault_systemes.search_navigate.delegation.doubleclick.ISearchDoubleClickActionDelgation;
import com.dassault_systemes.search_navigate.delegation.utils.ActionsHelper;
import com.dassault_systemes.search_navigate.delegation.utils.ActionsPerObject;
import com.dassault_systemes.search_navigate.delegation.utils.ObjectInfo;
import com.dassault_systemes.search_navigate.delegation.utils.SearchAction;
import com.dassault_systemes.search_navigate.exceptions.DoubleClickActionException;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.json.JSONObject;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.SelectList;

/**
 * Class to get right-click-actions on 3DSearch pages
 * @author K6V
 *
 */
@SuppressWarnings("deprecation")
public class PGSearchCommonActions extends SearchActionDelegationAdp implements ISearchActionsDelegation,ISearchDoubleClickActionDelgation {
	private final Logger _logger = Logger.getLogger("com.pg.search.searchactions.PGSearchCommonActions");
	private final String ID_PGCOMMON_SEARCH_DELETE = "pgSearchDelete";
	private final String ID_PGCOMMON_SEARCH_LAUNCH = "pgSearchLaunch";
	private final String STRING_RESOURCE_KEY_DELETE = "emxFramework.Command.Delete";
	private final String STRING_RESOURCE_KEY_LAUNCH = "emxFramework.Toolbar.Launch";
	private final String STRING_RESOURCE_FRAMEWORK = "emxFrameworkStringResource";
	private final String SOURCE_JS_PGCOMMON_SEARCH_ACTIONS = "DS/PGCommonSearchLocalActions/PGSearchLocalActions";
	private final String ICON_TRASH = "trash";
	private final String ICON_LAUNCH = "link";
	private final Logger _log;
	private static final String APPLICATION_3DSPACE = "3DSpace";
	private static final String APPLICATION_ENOVIA = "enovia";

	/**
	 * Default Constructor
	 */
	public PGSearchCommonActions() {
		this._log = Logger.getLogger("com.pg.search.searchactions");
	}
	
	/**
	 * overrides com.dassault_systemes.search_navigate.delegation.SearchActionDelegationAdp.computeActions
	 */
	public void computeActions(ActionsHelper actionHelper) {
		_logger.entering(this.getClass().getName(), "computeActions");
		String strIncontextMode = "false";
		Context context = actionHelper.getContext();
		String[] strArrObjectIDs = actionHelper.getObjectIDs();
		List<ObjectInfo> lSelectedObjects = actionHelper.getObjects();
		int nNumOfSelectedObjects = lSelectedObjects.size();
		_logger.log(Level.INFO, "Number of selected Parts: {0}", nNumOfSelectedObjects);
		try {
			JSONObject joRequestData = actionHelper.getRequestData();
			JSONObject joApplicativeInfo = joRequestData.getJSONObject("applicativeInfo");
			strIncontextMode = joApplicativeInfo.getString("incontextMode");
			
			JSONObject joClientAppInfo = joApplicativeInfo.getJSONObject("clientAppInfo");
			String strIdentifier = joClientAppInfo.getString("identifier");
			
			if (!APPLICATION_3DSPACE.equalsIgnoreCase(strIdentifier) && !APPLICATION_ENOVIA.equalsIgnoreCase(strIdentifier)) {
				_logger.log(Level.INFO, "No custom right click actions because application is: {0}", strIdentifier);
				return;
			}
			
			_logger.log(Level.INFO, "Is InContext mode: {0}", strIncontextMode);
		} catch (MatrixException me) {
			_logger.log(Level.SEVERE, "Failed to retrieve Request Data from ActionHelper", me);
		}
		String strI18nDeleteCommandName = EnoviaResourceBundle.getProperty(context, STRING_RESOURCE_FRAMEWORK, (Locale) context.getLocale(), STRING_RESOURCE_KEY_DELETE);
		String strI18nLaunchCommandName = EnoviaResourceBundle.getProperty(context, STRING_RESOURCE_FRAMEWORK, (Locale) context.getLocale(), STRING_RESOURCE_KEY_LAUNCH);
		SearchAction searchActionDelete = null;
		SearchAction searchActionLaunch = null;
		ActionsPerObject pgCommonObjSearchActions = null;
		String strObjId = null;
		for (int nCounter = 0; nCounter < strArrObjectIDs.length; nCounter++) {
			strObjId = strArrObjectIDs[nCounter];
			pgCommonObjSearchActions = actionHelper.getActionObject(strObjId);
			try {
				// DELETE Object Action
				if (!"true".equalsIgnoreCase(strIncontextMode)) {
					searchActionDelete = new SearchAction(ID_PGCOMMON_SEARCH_DELETE, ICON_TRASH, strI18nDeleteCommandName, SOURCE_JS_PGCOMMON_SEARCH_ACTIONS);
					//searchActionDelete.set_multisel(true);
					try {
						pgCommonObjSearchActions.addAction(searchActionDelete);
					} catch (ActionAlreadyAddedException aaae) {
						_logger.log(Level.WARNING, "Encountered ActionAlreadyAddedException for the object ", aaae);
					}
				}
				// LAUNCH Object Action
				searchActionLaunch = new SearchAction(ID_PGCOMMON_SEARCH_LAUNCH, // Action Id
						ICON_LAUNCH, // Action Value
						strI18nLaunchCommandName, // Action Title
						SOURCE_JS_PGCOMMON_SEARCH_ACTIONS // JS File Name (AMD-define Nomenclature)
				);
				searchActionLaunch.set_multisel(false);
				try {
					pgCommonObjSearchActions.addAction(searchActionLaunch);
				} catch (ActionAlreadyAddedException aaae) {
					_logger.log(Level.WARNING, "Encountered ActionAlreadyAddedException for the object ", aaae);
				}
			} catch (InvalidActionException iae) {
				_logger.log(Level.SEVERE, "Cannot add the actions to the object ", iae);
			}
		}
		_logger.exiting(this.getClass().getName(), "computeActions");
	}
	
	/**
	 * overrides com.dassault_systemes.search_navigate.delegation.SearchActionDelegationAdp.getAllActions
	 */
	public void getAllActions(final ActionsHelper actionHelper) {
		_logger.entering(this.getClass().getName(), "getAllActions");
		String strIncontextMode = "false";
		Context context = actionHelper.getContext();
		String[] strArrObjectIDs = actionHelper.getObjectIDs();
		List<ObjectInfo> lSelectedObjects = actionHelper.getObjects();
		int nNumOfSelectedObjects = lSelectedObjects.size();
		_logger.log(Level.INFO, "Number of selected Parts: {0}", nNumOfSelectedObjects);
		try {
			JSONObject joRequestData = actionHelper.getRequestData();
			JSONObject joApplicativeInfo = joRequestData.getJSONObject("applicativeInfo");
			strIncontextMode = joApplicativeInfo.getString("incontextMode");
			
			JSONObject joClientAppInfo = joApplicativeInfo.getJSONObject("clientAppInfo");
			String strIdentifier = joClientAppInfo.getString("identifier");
			if (!APPLICATION_3DSPACE.equalsIgnoreCase(strIdentifier)) {
				_logger.log(Level.INFO, "No custom right click actions because application is: {0}", strIdentifier);
				return;
			}
			
			_logger.log(Level.INFO, "Is InContext mode: {0}", strIncontextMode);
		} catch (MatrixException me) {
			_logger.log(Level.SEVERE, "Failed to retrieve Request Data from ActionHelper", me);
		}
		String strI18nDeleteCommandName = EnoviaResourceBundle.getProperty(context, STRING_RESOURCE_FRAMEWORK, (Locale) context.getLocale(), STRING_RESOURCE_KEY_DELETE);
		SearchAction searchActionDelete = null;
		ActionsPerObject pgCommonObjSearchActions = null;
		String strObjId = null;
		for (int nCounter = 0; nCounter < strArrObjectIDs.length; nCounter++) {
			strObjId = strArrObjectIDs[nCounter];
			pgCommonObjSearchActions = actionHelper.getActionObject(strObjId);
			try {
				// DELETE Object Action
				if (!"true".equalsIgnoreCase(strIncontextMode)) {
					searchActionDelete = new SearchAction(ID_PGCOMMON_SEARCH_DELETE, ICON_TRASH, strI18nDeleteCommandName, SOURCE_JS_PGCOMMON_SEARCH_ACTIONS);
					//searchActionDelete.set_multisel(true);
					try {
						pgCommonObjSearchActions.addAction(searchActionDelete);
					} catch (ActionAlreadyAddedException aaae) {
						_logger.log(Level.WARNING, "Encountered ActionAlreadyAddedException for the object ", aaae);
					}
				}
			} catch (InvalidActionException iae) {
				_logger.log(Level.SEVERE, "Cannot add the actions to the object ", iae);
			}
		}
		_logger.exiting(this.getClass().getName(), "getAllActions");
	}
	
	@Deprecated
	public void computeActionsOld(ActionsHelper helper) {
		try {
			Context context = helper.getContext();
			String strlanguage = context.getLocale().getLanguage();
			List<ObjectInfo> objectsList = helper.getObjects();
			String strObjectId = null;
			DomainObject domObj = null;
			Map<?, ?> objInfoMap = null;
			@SuppressWarnings("unused")
			boolean bHasReadAccess = false;
			@SuppressWarnings("unused")
			boolean bHasDeleteAccess = false;
			SelectList selObjectSelects = new SelectList();
			selObjectSelects.add("current.access[Read]");
			selObjectSelects.add("current.access[Delete]");
			ActionsPerObject actions = null;
			// LAUNCH Object Action
			SearchAction searchActionLaunch = new SearchAction("pgLaunchObject", // Action Id
					"Launch", // Action Value
					EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", new Locale(strlanguage), "emxFramework.Toolbar.Launch"), // Action
					// Title
					"DS/PGCommonSearchLocalActions/PGSearchLocalActions" // JS File Name (AMD-define Nomenclature)
			);
			searchActionLaunch.set_multisel(false);
			searchActionLaunch.set_icon("fonticon-window");
			// DELETE Object Action
			SearchAction searchActionDelete = new SearchAction("pgDeleteObject", // Action Id
					"Delete", // Action Value
					EnoviaResourceBundle.getProperty(context, "emxFrameworkStringResource", new Locale(strlanguage), "emxFramework.Command.Delete"), // Action
																																						// Title
					"DS/PGCommonSearchLocalActions/PGSearchLocalActions" // JS File Name (AMD-define Nomenclature)
			);
			searchActionDelete.set_multisel(false);
			searchActionDelete.set_icon("iconActionDelete.png");
			for (int i = 0, nObjCount = objectsList.size(); i < nObjCount; i++) {
				actions = objectsList.get(i).getActionObject();
				try {
					try {
						strObjectId = objectsList.get(i).get_id();
						domObj = DomainObject.newInstance(context, strObjectId);
						objInfoMap = domObj.getInfo(context, selObjectSelects);
						bHasReadAccess = Boolean.valueOf((String) objInfoMap.get("current.access[Read]"));
						bHasDeleteAccess = Boolean.valueOf((String) objInfoMap.get("current.access[Delete]"));
						// Custom PG Actions START
						// if (bHasReadAccess) {
						actions.addAction(searchActionLaunch);
						// }
						// if (bHasDeleteAccess) {
						actions.addAction(searchActionDelete);
						// }
						// Custom PG Actions END
					} catch (ActionAlreadyAddedException e) {
					}
				} catch (Exception ex) {
				}
			}
		} catch (InvalidActionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void getDoubleClickAction(DoubleClickActionHelper paramDoubleClickActionHelper) throws DoubleClickActionException {
		DoubleClickAction localDoubleClickAction = new DoubleClickAction("doubleClick",
				SOURCE_JS_PGCOMMON_SEARCH_ACTIONS);
		paramDoubleClickActionHelper.setDoubleClickAction(localDoubleClickAction);
	}
}
