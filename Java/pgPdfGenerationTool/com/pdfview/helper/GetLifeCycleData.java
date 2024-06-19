package com.pdfview.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.dassault_systemes.enovia.enterprisechangemgt.common.ChangeAction;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pdfview.impl.FPP.LifeCycle;
import com.pdfview.impl.FPP.LifeCycleData;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class GetLifeCycleData {
	Context _context = null;
	String _OID = DomainConstants.EMPTY_STRING;
	String _vault = "eService Production";

	public GetLifeCycleData(Context context, String sOID) {
		_context = context;
		_OID = sOID;
	}

	public LifeCycle getComponent() {
		LifeCycle lifCycle = new LifeCycle();
		List<LifeCycleData> lsLifeCycle = lifCycle.getLifeCycleData();
		try {
			if (StringHelper.validateString(_OID)) {
				StringList busSelects = new StringList(2);
				busSelects.add(DomainConstants.SELECT_NAME);
				busSelects.add(DomainConstants.SELECT_ID);
				Map mData = null;
				Map Argmap = null;
				int count = 0;
				String requested = DomainConstants.EMPTY_STRING;
				MapList mlTempTasks = null;
				Set alTasks = new HashSet();
				Set alCurrentTasks = new HashSet();
				Map proposedCAData = com.dassault_systemes.enovia.enterprisechangemgt.util.ChangeUtil
						.getChangeObjectsInProposed(_context, busSelects, new String[] { _OID }, 1);// REL_PG_CHANGE_AFFECTED_ITEM
				MapList maplistObjects = (MapList) proposedCAData.get(_OID);
				String strECId = DomainConstants.EMPTY_STRING;
				MapList mlTasks = null;
				if (maplistObjects != null && (!maplistObjects.isEmpty())) {
					for (Iterator iterator = maplistObjects.iterator(); iterator.hasNext();) {
						mData = (Map) iterator.next();
						strECId = (String) mData.get(DomainConstants.SELECT_ID);
						strECId = strECId.trim();
						requested = new ChangeAction(strECId).getRequestedChangeFromChangeAction(_context, _OID,
								strECId);
						if (requested.equalsIgnoreCase(pgV3Constants.For_Release)
								|| requested.equalsIgnoreCase("For Obsolescence")) {
							Argmap = new HashMap();
							Argmap.put("objectId", strECId);
							Argmap.put("languageStr", "en");
							String[] argsSub = JPO.packArgs(Argmap);
							mlTempTasks = (MapList) PDFPOCHelper.executeMainClassMethod(_context, "emxLifecycle",
									"getAllTaskSignaturesOnObject", argsSub);
							alTasks.add(mlTempTasks);
						}
					}
				}
				int cntFPPLimit = 0;
				StringBuffer sbTempTaskName = new StringBuffer();
				boolean isExists = false;
				String[] argsTaskName = null;
				Vector mlTasksName = null;
				String strTaskName = DomainConstants.EMPTY_STRING;
				Map object = null;
				Map taskData = null;
				Map paramMap = null;
				Map taskNamemap = null;
				int mlTasksNamesize=0;
				for (Iterator cOIterator = alTasks.iterator(); cOIterator.hasNext();) {
					mlTasks = (MapList) cOIterator.next();
					if (mlTasks != null && !mlTasks.isEmpty()) {
						mlTasks.addSortKey("Name", "ascending", "String");
						mlTasks.sort();
						object = null;
						taskData = new HashMap();
						for (Iterator iterator = mlTasks.iterator(); iterator.hasNext();) {
							taskData.clear();
							object = (Map) iterator.next();
							MapList objectList = new MapList();
							taskData.put("name", (String) object.get("name"));
							taskData.put("infoType", (String) object.get("infoType"));
							taskData.put("taskId", (String) object.get("taskId"));
							taskData.put("routeNodeId", (String) object.get("routeNodeId"));
							taskData.put("routeId", (String) object.get("routeId"));
							taskData.put("assigneeType", (String) object.get("assigneeType"));
							taskData.put("routeTaskUser", (String) object.get("routeTaskUser"));
							taskData.put("assigneeName", (String) object.get("assigneeName"));
							taskData.put("signed", (String) object.get("signed"));
							taskData.put("approver", (String) object.get("approver"));
							taskData.put("title", (String) object.get("title"));
							taskData.put("routeAction", (String) object.get("routeAction"));
							taskData.put("currentState", (String) object.get("currentState"));
							taskData.put("parentObjectState", (String) object.get("parentObjectState"));
							taskData.put("routeStatus", (String) object.get("routeStatus"));
							taskData.put("approvalStatus", (String) object.get("approvalStatus"));
							taskData.put("approved", (String) object.get("approved"));
							taskData.put("rejected", (String) object.get("rejected"));
							taskData.put("ignored", (String) object.get("ignored"));
							taskData.put("completionDate", (String) object.get("completionDate"));
							taskData.put("actualDate", (String) object.get("actualDate"));
							objectList.add(taskData);
							paramMap = new HashMap();
							paramMap.put("reportFormat", "PDF");
							paramMap.put("languageStr", "en");
							paramMap.put("objectId", strECId);
							taskNamemap = new HashMap();
							taskNamemap.put("objectList", objectList);
							taskNamemap.put("paramList", paramMap);
							argsTaskName = JPO.packArgs(taskNamemap);
							mlTasksName = (Vector) PDFPOCHelper.executeMainClassMethod(_context, "emxLifecycle",
									"getTaskOrSignatureForApprovals", argsTaskName);
							strTaskName = DomainConstants.EMPTY_STRING;
							mlTasksNamesize=mlTasksName.size();
							for (int i = 0; i < mlTasksNamesize; i++) {
								strTaskName = (String) mlTasksName.get(i);
								if (!UIUtil.isNullOrEmpty(strTaskName)) {
									if (sbTempTaskName.indexOf(strTaskName) != -1
											&& !(((String) object.get("infoType")).equalsIgnoreCase("signature")))
										isExists = true;
									else
										sbTempTaskName.append(strTaskName);
								}
							}
							if (!UIUtil.isNullOrEmpty(strTaskName)) {
								if (!isExists)
									cntFPPLimit++;
								alCurrentTasks.add(mlTasks);
							}
							mlTasksName.clear();
							taskData.clear();
						}
					}
				}
				String strApproverName = DomainConstants.EMPTY_STRING;
				object = null;
				taskData = new HashMap();
				taskNamemap = null;
				String routeId = DomainConstants.EMPTY_STRING;
				MapList objectList = null;
				int mlTasksNamesSize=0;
				String strTitle = DomainConstants.EMPTY_STRING;
				String strApprovalDate = DomainConstants.EMPTY_STRING;
				String strApprovalStatus = DomainConstants.EMPTY_STRING;
				String strCommentsOrInstructions = DomainConstants.EMPTY_STRING;
				DomainObject route =null;
				
				for (Iterator cOIterator = alCurrentTasks.iterator(); cOIterator.hasNext();) {
					count++;
					mlTasks = (MapList) cOIterator.next();
					if (mlTasks != null && !mlTasks.isEmpty()) {
						mlTasks.addSortKey("Name", "ascending", "String");
						mlTasks.sort();
						object = null;
						taskData = new HashMap();
						for (Iterator iterator = mlTasks.iterator(); iterator.hasNext();) {
							taskData.clear();
							object = (Map) iterator.next();
							objectList = new MapList();
							taskData.put("name", (String) object.get("name"));
							taskData.put("infoType", (String) object.get("infoType"));
							taskData.put("taskId", (String) object.get("taskId"));
							taskData.put("routeNodeId", (String) object.get("routeNodeId"));
							taskData.put("routeId", (String) object.get("routeId"));
							routeId = (String) object.get("routeId");
							if (routeId != null) {
								route = DomainObject.newInstance(_context, routeId);
								taskData.put("owner", route.getInfo(_context, DomainObject.SELECT_OWNER));
							}
							taskData.put("assigneeType", (String) object.get("assigneeType"));
							taskData.put("routeTaskUser", (String) object.get("routeTaskUser"));
							taskData.put("assigneeName", (String) object.get("assigneeName"));
							taskData.put("signed", (String) object.get("signed"));
							taskData.put("approver", (String) object.get("approver"));
							taskData.put("title", (String) object.get("title"));
							taskData.put("routeAction", (String) object.get("routeAction"));
							taskData.put("currentState", (String) object.get("currentState"));
							taskData.put("parentObjectState", (String) object.get("parentObjectState"));
							taskData.put("routeStatus", (String) object.get("routeStatus"));
							taskData.put("routeNodeId", (String) object.get("routeNodeId"));
							taskData.put("approvalStatus", (String) object.get("approvalStatus"));
							taskData.put("approved", (String) object.get("approved"));
							taskData.put("rejected", (String) object.get("rejected"));
							taskData.put("ignored", (String) object.get("ignored"));
							taskData.put("completionDate", (String) object.get("completionDate"));
							taskData.put("actualDate", (String) object.get("actualDate"));
							objectList.add(taskData);
							paramMap = new HashMap();
							paramMap.put("reportFormat", "PDF");
							paramMap.put("languageStr", "en");
							paramMap.put("objectId", strECId);
							taskNamemap = new HashMap();
							taskNamemap.put("objectList", objectList);
							taskNamemap.put("paramList", paramMap);
							argsTaskName = JPO.packArgs(taskNamemap);
							strTaskName =executeEmxLifeCycleJPO("getTaskOrSignatureForApprovals", argsTaskName);
							if (!UIUtil.isNullOrEmpty(strTaskName)) {
								LifeCycleData lifeCycle = new LifeCycleData();
								lifeCycle.setName(StringHelper.validateString1(strTaskName));
								
								strApproverName=executeEmxLifeCycleJPO("getAssigneeForApprovals", argsTaskName);
								strApproverName = (String) PersonUtil.getFullName(_context, strApproverName);
								lifeCycle.setApprover(StringHelper.validateString1(strApproverName));
								
								strTitle =executeEmxLifeCycleJPO("getTaskTitleForApprovals", argsTaskName);
								strTitle = StringHelper.filterLessAndGreaterThanSign(strTitle);
								lifeCycle.setTitle(StringHelper.validateString1(strTitle));
								
								strApprovalStatus =executeEmxLifeCycleJPO("getActionForApprovals", argsTaskName);
								lifeCycle.setApprovalStatus(StringHelper.validateString1(strApprovalStatus));
								
								strApprovalDate=executeEmxLifeCycleJPO("getCompletedDateForApprovals", argsTaskName);
								lifeCycle.setApprovalDate(StringHelper.validateString1(strApprovalDate));
								
								strCommentsOrInstructions=executeEmxLifeCycleJPO("getCommentsOrInstructionsForTaskSignatures", argsTaskName);
								lifeCycle.setCommentsOrInstructions(StringHelper.validateString1(strCommentsOrInstructions));

//								Vector mlRelatedObjects = (Vector) PDFPOCHelper.executeMainClassMethod(_context,
//										"emxLifecycle", "getRelatedObjectColumnsForTaskSignatures", argsTaskName);
								String strRelatedObjects =DomainConstants.EMPTY_STRING;;
//								for (int i = 0; i < mlRelatedObjects.size(); i++) {
//									strRelatedObjects  = (String) mlRelatedObjects.get(i);
//								}
								lifeCycle.setRelatedObjects(StringHelper.validateString1(strRelatedObjects));
							
								lsLifeCycle.add(lifeCycle);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lifCycle;
	}
	public String executeEmxLifeCycleJPO(String methodName,String[] argsTaskName) {
		Vector mlStatus = (Vector) PDFPOCHelper.executeMainClassMethod(_context,
				"emxLifecycle", methodName, argsTaskName);
		String strStatus = DomainConstants.EMPTY_STRING;
		int mlSize=mlStatus.size();
		for (int i = 0; i < mlSize; i++) {
			strStatus = (String) mlStatus.get(i);
		}
		return strStatus;
	}
}
