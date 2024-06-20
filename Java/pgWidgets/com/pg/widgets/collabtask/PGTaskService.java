package com.pg.widgets.collabtask;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dassault_systemes.enovia.e6wv2.foundation.FoundationException;
import com.dassault_systemes.enovia.e6wv2.foundation.ServiceBase;
import com.dassault_systemes.enovia.e6wv2.foundation.ServiceConstants;
import com.dassault_systemes.enovia.e6wv2.foundation.db.JPOUtil;
import com.dassault_systemes.enovia.e6wv2.foundation.db.ObjectUtil;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Datacollection;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Dataobject;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.ExpressionType;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Selectable;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.ServiceParameters;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxbext.ArgMap;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxbext.Select;
import com.dassault_systemes.enovia.tskv2.Task;
import com.dassault_systemes.enovia.tskv2.TaskScope;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.framework.ui.UIUtil;

import matrix.db.Context;

public class PGTaskService implements ServiceConstants {
	static final String SYMBOLIC_TYPE_PROJECTTASK = "type_ProjectTask";
	static final String TYPE_PROJECTTASK = PropertyUtil.getSchemaProperty(null, SYMBOLIC_TYPE_PROJECTTASK);
	static final String SYMBOLIC_TYPE_PGGPSASSESSMENTTASK = "type_pgGPSAssessmentTask";
	static final String TYPE_PGGPSASSESSMENTTASK = PropertyUtil.getSchemaProperty(null, SYMBOLIC_TYPE_PGGPSASSESSMENTTASK);
	static final String STATUS_TRUE = "TRUE";
	static final String STATE_CLAUSE = "current != 'Complete'";
	static final String SHOW_PROJECT_TASKS = "showProjectTasks";
	static final String SCOPE_ID = "scopeId";
	static final String CURRENT_TASK_FILTER = "currentTaskFilter";
	static final String INCLUDE_ROUTE_TASKS = "includeRouteTasks";
	static final String TYPE_FILTER = "typeFilter";
	static final String SHOW_COMPLETE_DAYS = "showCompleteDays";

	/**
	 * @param paramContext
	 *            The enovia context object
	 * @param strArgs
	 *            Arguments packed in JPO format
	 * @return DataCollection of details of Task objects
	 * @throws FoundationException
	 *             when operation fails
	 */
	public static Datacollection getTasks(Context paramContext, String[] strArgs) throws FoundationException {
		ServiceParameters argParams = JPOUtil.unpackArgs(strArgs);
		Datacollection argDatacoll = argParams.getDatacollection();
		List<Selectable> argObjSelectList = argParams.getSelects();
		ArgMap argMap = argParams.getServiceArgs();
		String strShowProjTasks = argMap.get(SHOW_PROJECT_TASKS);
		String strScopeId = argMap.get(SCOPE_ID);
		boolean bShowProjTasks = Boolean.parseBoolean(strShowProjTasks);
		String strCurrTaskFilter = argMap.get(CURRENT_TASK_FILTER);
		String strInclRouteTasks = argParams.getJpoArgs().get(INCLUDE_ROUTE_TASKS);
		String strInclRouteTasksArg = argMap.get(INCLUDE_ROUTE_TASKS);
		String strType = argMap.get(TYPE_FILTER);
		String strCompleteDays = argMap.get(SHOW_COMPLETE_DAYS);
		Datacollection datacollectionProjTasks = argDatacoll;
		if ((datacollectionProjTasks != null) && (!datacollectionProjTasks.getDataobjects().isEmpty())) {
			Task.getTaskInfo(paramContext, datacollectionProjTasks, argObjSelectList);
		} else {
			Object objSelects;
			Object objScopeDetails;
			if (UIUtil.isNotNullAndNotEmpty(strScopeId)) {
				objSelects = new ArrayList<Object>();
				Select selectObjSelectables = new Select(DomainConstants.SELECT_TYPE, DomainConstants.SELECT_TYPE, ExpressionType.BUS, null, false);
				((List) objSelects).add(selectObjSelectables);
				objScopeDetails = ObjectUtil.print(paramContext, strScopeId, null, (List) objSelects);
				String strScopeObjType = ((Dataobject) objScopeDetails).getType();
				if (DomainConstants.TYPE_PROJECT_SPACE.equals(strScopeObjType)) {
					datacollectionProjTasks = TaskScope.getProjectScopeTasks(paramContext, strScopeId, argObjSelectList, strCurrTaskFilter);
				} else {
					datacollectionProjTasks = PGTaskScope.getScopeTasks(paramContext, strScopeId, argObjSelectList, new ArrayList<Selectable>(), null, STATE_CLAUSE,
							strCurrTaskFilter);
				}
			} else {
				objSelects = null;
				objScopeDetails = null;
				if (strType == null || (DomainConstants.TYPE_TASK.equals(strType)) || (TYPE_PROJECTTASK.equals(strType)) || (TYPE_PGGPSASSESSMENTTASK.equals(strType))) // added
				{
					datacollectionProjTasks = PGTask.getUserTasks(paramContext, argObjSelectList, strCurrTaskFilter, bShowProjTasks, strType, strCompleteDays);
				}
				if (((STATUS_TRUE.equalsIgnoreCase(strInclRouteTasks)) || (STATUS_TRUE.equalsIgnoreCase(strInclRouteTasksArg)))
						&& (DomainConstants.TYPE_INBOX_TASK.equals(strType))) // added
				{
					Datacollection datacollectionRouteTasks = PGTask.getUserRouteInboxTasks(paramContext, argObjSelectList, strCurrTaskFilter, strCompleteDays);
					datacollectionProjTasks.getDataobjects().addAll(datacollectionRouteTasks.getDataobjects());
				}
				ServiceBase.appendInfoMessage(datacollectionProjTasks, (String) objSelects);
				ServiceBase.appendInfoMessage(datacollectionProjTasks, (String) objScopeDetails);
			}
		}
		return datacollectionProjTasks;
	}
}
