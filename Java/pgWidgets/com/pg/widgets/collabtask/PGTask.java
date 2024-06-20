package com.pg.widgets.collabtask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.dassault_systemes.enovia.e6wv2.foundation.FoundationException;
import com.dassault_systemes.enovia.e6wv2.foundation.ServiceConstants;
import com.dassault_systemes.enovia.e6wv2.foundation.db.ObjectUtil;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Datacollection;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Dataobject;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.ExpandData;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.ExpressionType;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.QueryData;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxb.Selectable;
import com.dassault_systemes.enovia.e6wv2.foundation.jaxbext.Select;
import com.dassault_systemes.enovia.tskv2.Task;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.program.ProgramCentralConstants;

import matrix.db.Context;

public class PGTask extends Task implements ServiceConstants {
	static final String ERROR_EMPTYPARAMLIST = "You must provide some selectables to Task.getUserTasks";
	static final String OWNED = "owned";
	static final String ASSIGNED = "assigned";
	static final String SELECTABLE_SUBTASK = new StringBuilder("from[").append(ProgramCentralConstants.RELATIONSHIP_SUBTASK).append("] ~~ False && (to[")
			.append(ProgramCentralConstants.RELATIONSHIP_PROJECT_ACCESS_KEY).append("] ~~ False").toString();
	static final String SELECTABLE_PROJECT_ACCESS_TYPE = new StringBuilder("|| !(to[").append(ProgramCentralConstants.RELATIONSHIP_PROJECT_ACCESS_KEY).append("].from.from[")
			.append(ProgramCentralConstants.RELATIONSHIP_PROJECT_ACCESS_LIST).append("].to.type matchlist '").append(ProgramCentralConstants.TYPE_PROJECT_TEMPLATE).append(",")
			.append(ProgramCentralConstants.TYPE_PROJECT_BASELINE).append(",").append(ProgramCentralConstants.TYPE_PROJECT_SNAPSHOT).append(",")
			.append(ProgramCentralConstants.TYPE_EXPERIMENT).append("', ',' ||").toString();
	static final String SELECTABLE_PROJECT_ACCESS_CURRENT = new StringBuilder(" to[").append(ProgramCentralConstants.RELATIONSHIP_PROJECT_ACCESS_KEY).append("].from.from[")
			.append(ProgramCentralConstants.RELATIONSHIP_PROJECT_ACCESS_LIST).append("].to.current matchlist 'Hold,Cancel', ','))").toString();
	static final String SELECTABLE_TASKACTUAL_DATE = new StringBuilder(ProgramCentralConstants.SELECT_ATTRIBUTE_TASK_ACTUAL_FINISH_DATE).append("=='' || ")
			.append(ProgramCentralConstants.SELECT_ATTRIBUTE_TASK_ACTUAL_FINISH_DATE).toString();
	static final String PHYSICALID = "physicalid";
	static final String CONTEXT_OWNER = " && owner == context.user";
	static final String DATE_FORMAT = "MMM dd, yyyy";
	static final String SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE = "attribute[" + PropertyUtil.getSchemaProperty(null, "attribute_ActualCompletionDate") + "]";
	static final String SELECTABLE_ACTUAL_COMPLETION_DATE = SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE + "=='' || " + SELECT_ATTRIBUTE_ACTUAL_COMPLETION_DATE;
	private static final Logger logger = Logger.getLogger(PGTask.class.getName());
	static final String ERROR_GETTASKS_GETCOMPLETIONDATE = "Exception in PGTask : getFromCompletionDate";

	/**
	 * @param paramContext the enovia context object
	 * @param paramList List of object Selectables
	 * @param paramString owned - to get owned tasks; assigned to get assigned tasks 
	 * @param showProjectTasks boolean true to fetch Project tasks
	 * @param strType Type of tasks to return
	 * @param strCompleteDays duration in days
	 * @return the collection of all the tasks for which the context user is the owner or is assigned to the task the method is used to get the Tasks
	 *         for the context user based upon the selectable list
	 * @throws FoundationException when operation fails
	 */
	public static Datacollection getUserTasks(Context paramContext, List<Selectable> paramList, String paramString, boolean showProjectTasks, String strType,
			String strCompleteDays) throws FoundationException {
		if ((paramList == null) || (paramList.isEmpty())) {
			throw new FoundationException(ERROR_EMPTYPARAMLIST);
		}
		boolean bAssigned = false;
		boolean bOwned = false;
		if (OWNED.equals(paramString)) {
			bOwned = true;
		} else if (ASSIGNED.equals(paramString)) {
			bAssigned = true;
		}
		String strTaskSelectable = SELECTABLE_SUBTASK;
		strTaskSelectable = strTaskSelectable + SELECTABLE_PROJECT_ACCESS_TYPE;
		strTaskSelectable = strTaskSelectable + SELECTABLE_PROJECT_ACCESS_CURRENT;
		// filter the completed tasks based on the setting
		String strFromCompletionDate = getFromCompletionDate(paramContext, strCompleteDays);
		if (strFromCompletionDate != null && !strFromCompletionDate.isEmpty()) {
			strTaskSelectable = strTaskSelectable + " && (" + SELECTABLE_TASKACTUAL_DATE + " >=' " + strFromCompletionDate + "')";
		}
		Datacollection datacollUserOwnedTasks = null;
		Datacollection dataCollUserAssignedTasks = null;
		if (bOwned) {
			QueryData queryDataOwnerTasks = new QueryData();
			(queryDataOwnerTasks).setTypePattern(strType);
			(queryDataOwnerTasks).setOwnerPattern(paramContext.getUser());
			strTaskSelectable = strTaskSelectable + CONTEXT_OWNER;
			(queryDataOwnerTasks).setWhereExpression(strTaskSelectable);
			datacollUserOwnedTasks = ObjectUtil.query(paramContext, (QueryData) queryDataOwnerTasks, paramList);
		}
		if (bAssigned) {
			ArrayList localObject2 = new ArrayList(1);
			Select localSelect = new Select(PHYSICALID, PHYSICALID, ExpressionType.BUS, null, false);
			(localObject2).add(localSelect);
			Datacollection datacollContextUserDetails = getPersonInfo(paramContext, paramContext.getUser(), paramList, 1);
			Dataobject dataobjectUserData = datacollContextUserDetails.getDataobjects().get(0);
			ExpandData expandDataUserAssignedTasks = new ExpandData();
			expandDataUserAssignedTasks.setRelationshipPattern(DomainConstants.RELATIONSHIP_ASSIGNED_TASKS);
			expandDataUserAssignedTasks.setTypePattern(strType);
			expandDataUserAssignedTasks.setGetFrom(Boolean.valueOf(true));
			expandDataUserAssignedTasks.setObjectWhere(strTaskSelectable);
			dataCollUserAssignedTasks = ObjectUtil.expand(paramContext, dataobjectUserData, expandDataUserAssignedTasks, paramList);
		}
		Object dataCollFinalUserTasks = new Datacollection();
		if (datacollUserOwnedTasks != null) {
			((Datacollection) dataCollFinalUserTasks).getDataobjects().addAll(datacollUserOwnedTasks.getDataobjects());
		}
		if (dataCollUserAssignedTasks != null) {
			((Datacollection) dataCollFinalUserTasks).getDataobjects().addAll((dataCollUserAssignedTasks).getDataobjects());
		}
		return (Datacollection) dataCollFinalUserTasks;
	}

	/**
	 * @param paramContext the enovia context object
	 * @param paramList object selectable
	 * @param paramString owned to get owned tasks, assigned to get assigned tasks
	 * @param strCompleteDays duration of completion
	 * @return the collection of all the tasks for which the context user is the owner or is assigned to the task the method is used to get the Tasks
	 *         for the context user based upon the selectable list
	 * @throws FoundationException when operation fails
	 */
	public static Datacollection getUserRouteInboxTasks(Context paramContext, List<Selectable> paramList, String paramString, String strCompleteDays) throws FoundationException {
		Datacollection datacollOwnedTasks = null;
		Datacollection dataCollUserTasks = null;
		boolean bAssigned = false;
		boolean bOwned = false;
		if (OWNED.equals(paramString)) {
			bOwned = true;
		} else if (ASSIGNED.equals(paramString)) {
			bAssigned = true;
		}
		String strWhere = "";
		String strTaskCompletionDate = getFromCompletionDate(paramContext, strCompleteDays);
		if (strTaskCompletionDate != null && !strTaskCompletionDate.isEmpty()) {
			strWhere = strWhere + " (" + SELECTABLE_ACTUAL_COMPLETION_DATE + ">='" + strTaskCompletionDate + "')";
		}
		if (bAssigned) {
			ArrayList selectList = new ArrayList(1);
			Select selectable = new Select(PHYSICALID, PHYSICALID, ExpressionType.BUS, null, false);
			(selectList).add(selectable);
			Datacollection datacollPersonDetails = getPersonInfo(paramContext, paramContext.getUser(), paramList, 1);
			Dataobject dataobjectPerson = datacollPersonDetails.getDataobjects().get(0);
			ExpandData expandDataUserTask = new ExpandData();
			expandDataUserTask.setRelationshipPattern(DomainConstants.RELATIONSHIP_PROJECT_TASK);
			expandDataUserTask.setTypePattern(DomainConstants.TYPE_INBOX_TASK);
			expandDataUserTask.setGetTo(Boolean.valueOf(true));
			expandDataUserTask.setObjectWhere(strWhere);
			dataCollUserTasks = ObjectUtil.expand(paramContext, dataobjectPerson, expandDataUserTask, paramList);
		}
		if (bOwned) {
			strWhere = strWhere + CONTEXT_OWNER;
			QueryData queryDataOwnedTasks = new QueryData();
			(queryDataOwnedTasks).setTypePattern(DomainConstants.TYPE_INBOX_TASK);
			(queryDataOwnedTasks).setOwnerPattern(paramContext.getUser());
			(queryDataOwnedTasks).setWhereExpression(strWhere);
			datacollOwnedTasks = ObjectUtil.query(paramContext, (QueryData) queryDataOwnedTasks, paramList);
		}
		Datacollection datacollFinalTaskList = new Datacollection();
		if (datacollOwnedTasks != null) {
			((Datacollection) datacollFinalTaskList).getDataobjects().addAll(datacollOwnedTasks.getDataobjects());
		}
		if (dataCollUserTasks != null) {
			((Datacollection) datacollFinalTaskList).getDataobjects().addAll((dataCollUserTasks).getDataobjects());
		}
		return (Datacollection) datacollFinalTaskList;
	}

	/**
	 * @param paramContext the enovia context object
	 * @param strCompleteDays duration if complete days
	 * @return the Date in String
	 */
	private static String getFromCompletionDate(Context paramContext, String strCompleteDays) {
		String strCompletionDate = null;
		try {
			if (strCompleteDays != null && !strCompleteDays.isEmpty()) {
				int i = Integer.parseInt(strCompleteDays);
				DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
				strCompletionDate = LocalDate.now().minusDays(i).format(dateTimeFormatter);
			}
		} catch (Exception exception) {
			logger.log(Level.SEVERE, ERROR_GETTASKS_GETCOMPLETIONDATE, exception);
			throw exception;
		}
		return strCompletionDate;
	}
}
