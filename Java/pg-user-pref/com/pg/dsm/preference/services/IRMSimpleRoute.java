package com.pg.dsm.preference.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.common.Person;
import com.matrixone.apps.common.Route;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.eMatrixDateFormat;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.enumeration.PreferenceConstants;
import com.pg.dsm.preference.models.DSMUserPreferenceConfig;
import com.pg.dsm.preference.models.IRMApprovalPreference;
import com.pg.dsm.preference.models.IRMMember;
import com.pg.dsm.preference.models.IRMRouteDetails;
import com.pg.dsm.preference.models.IRMUserGroup;
import com.pg.dsm.preference.models.Member;
import com.pg.dsm.preference.models.UserGroup;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.Pattern;
import matrix.util.StringList;
//Added by IRM(Sogeti) 2022x.04 Dec CW Requirement 47851 
public class IRMSimpleRoute {
    HashMap routeMap;
    boolean isCreated;
    String errorMessage;

    public IRMSimpleRoute(Creator creator) {
        this.routeMap = creator.routeMap;
        this.isCreated = creator.isCreated;
        this.errorMessage = creator.errorMessage;
    }

    public HashMap getRouteMap() {
        return routeMap;
    }

    public boolean isCreated() {
        return isCreated;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static class Creator {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        Person person;
        DomainObject personObj;
        String objectOid;

        String taskDueDate;
        HashMap routeMap;
        Route route;

        boolean isCreated;
        String errorMessage;

        IRMRouteDetails approvalPreference;

        public Creator(Context context, IRMRouteDetails approvalPreference, String objectOid) throws Exception {
            this.context = context;
            this.approvalPreference = approvalPreference;
            this.person = (Person) DomainObject.newInstance(context, DomainConstants.TYPE_PERSON);
            String personObjectID = PersonUtil.getPersonObjectID(context);
            this.personObj = DomainObject.newInstance(context, personObjectID);
            this.objectOid = objectOid;
            this.routeMap = new HashMap();
        }

        public IRMSimpleRoute create() {
            try {
                this.taskDueDate = getTaskDueDate();
                logger.log(Level.INFO, "Task Due Date: " + this.taskDueDate);
                this.createSimpleRoute();
                this.isCreated = true;
            } catch (Exception e) {
                this.isCreated = false;
                this.errorMessage = e.getMessage();
            }
            return new IRMSimpleRoute(this);
        }

        private void createSimpleRoute() throws Exception {
            logger.log(Level.INFO, "Route | Context user: " + this.context.getUser());
            HashMap stateMap = getRouteStateDetails(this.objectOid);

            Hashtable routeAttributeMap = getRouteAttributes(stateMap);
            logger.log(Level.INFO, "Route | Attribute Map: " + routeAttributeMap);
            boolean isRecipientMembers = this.approvalPreference.isPreferredRouteTaskRecipientMembers();
            logger.log(Level.INFO, "Route | isRecipientMembers: " + isRecipientMembers);
            boolean isRecipientUserGroup = this.approvalPreference.isPreferredRouteTaskRecipientUserGroups();
            logger.log(Level.INFO, "Route | isRecipientUserGroup: " + isRecipientUserGroup);
            MapList recipientDetails = null;
            if (isRecipientMembers) {
                recipientDetails = getTaskMemberDetails();
                logger.log(Level.INFO, "Route | Member MapList" + recipientDetails);
            }
            if (isRecipientUserGroup) {
                recipientDetails = getUserGroupDetails();
                logger.log(Level.INFO, "Route | User Group MapList" + recipientDetails);
            }
            if (null != recipientDetails) {
                MapList recipientAccessDetails = getMemberAccessDetails(recipientDetails);
                logger.log(Level.INFO, "Route | Member Access MapList: " + recipientAccessDetails);
                createSimpleRoute(stateMap, new String[]{}, routeAttributeMap, recipientAccessDetails, recipientDetails);
                logger.log(Level.INFO, "Route | Create Route - RouteMap: " + this.routeMap);
            }
        }

        /**
         * @param stateMap
         * @param contentIdArray
         * @param routeDetails
         * @param routeMemberMapList
         * @param taskMapDetails
         * @throws Exception
         */
        void createSimpleRoute(HashMap stateMap, String[] contentIdArray, Hashtable routeDetails, MapList routeMemberMapList, MapList taskMapDetails) throws Exception {
            boolean isRouteCreated = false;
            String oid = (String) routeDetails.get("objectId");
            this.route = (Route) DomainObject.newInstance(this.context, DomainConstants.TYPE_ROUTE);
            DomainObject currentObject = DomainObject.newInstance(this.context);
            currentObject.setId(oid);
            this.routeMap = createRoute(oid, routeDetails);
            if (this.routeMap != null) {
                String routeOid = (String) this.routeMap.get("routeId");
                if (UIUtil.isNotNullAndNotEmpty(routeOid)) {
                    this.route.setId(routeOid);
                    isRouteCreated = true;
                }
            } else {
                logger.log(Level.INFO, "routeMap is null or empty");
            }
            if (isRouteCreated) {
                addRouteContent(contentIdArray, stateMap);
                createRouteTasks(taskMapDetails);
                giveRouteMembersAccess(routeMemberMapList);

                Map<String, String> attributeMap = new HashMap<>();
                attributeMap.put(DomainConstants.ATTRIBUTE_ROUTE_BASE_PURPOSE, PreferenceConstants.Basic.APPROVAL.get());
                attributeMap.put(pgV3Constants.ATTRIBUTE_ROUTECOMPLETIONACTION, PreferenceConstants.Basic.PROMOTE_CONNECTED_OBJECT.get());
                this.route.setAttributeValues(this.context, attributeMap);
            }
        }


        /**
         * @param objectId
         * @param routeDetails
         * @return
         * @throws Exception
         */
        HashMap createRoute(String objectId, Hashtable routeDetails) throws Exception {
            HashMap routeProps = null;
            String routeRequiresESign = "False";
            String eSignConfigSetting = "None";
            try {
                eSignConfigSetting = MqlUtil.mqlCommand(this.context, "list expression $1 select $2 dump", "ENXESignRequiresESign", "value");
                if (UIUtil.isNullOrEmpty(eSignConfigSetting))
                    eSignConfigSetting = "None";
            } catch (Exception e) {
                eSignConfigSetting = "None";
            }
            String routeBasePurpose = "";
            if (routeDetails != null)
                routeBasePurpose = (String) routeDetails.get("routeBasePurpose");
            String ATTRIBUTE_ROUTE_REQUIRES_E_SIGN = PropertyUtil.getSchemaProperty(this.context, "attribute_RequiresESign");

            if (objectId != null && !objectId.equals("null") && !objectId.equals("")) {
                logger.log(Level.INFO, "createRouteWithScope: START");
                routeProps = Route.createRouteWithScope(this.context, objectId, "", "", true, routeDetails);
                logger.log(Level.INFO, "createRouteWithScope: END");
            } else {
                logger.log(Level.INFO, "Create Route using auto-name: start");
                String autoRouteId = FrameworkUtil.autoName(this.context, "type_Route", "", "policy_Route");
                logger.log(Level.INFO, "autoRouteId: " + autoRouteId);
                routeProps = new HashMap();
                routeProps.put("routeId", autoRouteId);
                DomainObject tempRoute = (Route) DomainObject.newInstance(this.context, autoRouteId);
                if (("Approval".equalsIgnoreCase(routeBasePurpose) || "Standard".equalsIgnoreCase(routeBasePurpose)) && "All".equalsIgnoreCase(eSignConfigSetting)) {
                    routeRequiresESign = "True";
                }
                tempRoute.setAttributeValue(this.context, ATTRIBUTE_ROUTE_REQUIRES_E_SIGN, routeRequiresESign);
                tempRoute.connect(this.context, new RelationshipType(DomainObject.RELATIONSHIP_PROJECT_ROUTE), true, this.personObj);
                logger.log(Level.INFO, "Create Route using auto-name: end");
            }

            return routeProps;
        }

        /**
         * @param contentIdArray
         * @param stateMap
         * @throws Exception
         */
        void addRouteContent(String[] contentIdArray, HashMap stateMap) throws Exception {
            if (this.route != null && stateMap != null && contentIdArray != null && contentIdArray.length > 0) {
                this.route.AddContent(this.context, contentIdArray, stateMap);
            }
        }

        /**
         * @param taskMapList
         * @throws Exception
         */
        void createRouteTasks(MapList taskMapList) throws Exception {
            if (this.route != null && taskMapList != null && taskMapList.size() > 0) {
                this.route.addRouteMembers(this.context, taskMapList, new HashMap());
                String RouteId = this.route.getId(this.context);
                DomainObject routeObj1 = DomainObject.newInstance(this.context, RouteId);
                String strApproverRole = PropertyUtil.getSchemaProperty(this.context, "attribute_pgApproverRole");
                String strRelationshipPattern = DomainObject.RELATIONSHIP_ROUTE_NODE;
                String strTypePerson = PropertyUtil.getSchemaProperty(this.context, "type_Person");
                String strTypeTask = PropertyUtil.getSchemaProperty(this.context, "type_RouteTaskUser");
                Pattern strTypePattern = new Pattern(strTypePerson);
                strTypePattern.addPattern(strTypeTask);
                StringList slBusSelect = new StringList(3);
                slBusSelect.add(DomainObject.SELECT_ID);
                //Added by IRM (DSM) for 2022x.02 CW-02 for Defect #56054 - START
                slBusSelect.add(DomainObject.SELECT_PHYSICAL_ID);
                //Added by IRM (DSM) for 2022x.02 CW-02 for Defect #56054 - END
                slBusSelect.add(DomainObject.SELECT_NAME);
                StringList slRelSelect = new StringList(1);
                slRelSelect.add(DomainRelationship.SELECT_ID);
                final boolean GET_TO = true;
                final boolean GET_FROM = true;
                String strObjectWhere = DomainConstants.EMPTY_STRING;
                String strRelWhere = DomainConstants.EMPTY_STRING;

                MapList mlRouteNodes = routeObj1.getRelatedObjects(this.context,
                        strRelationshipPattern,
                        strTypePattern.getPattern(),
                        slBusSelect,
                        slRelSelect,
                        !GET_TO,
                        GET_FROM,
                        (short) 1,
                        strObjectWhere,
                        strRelWhere);
                Map mapRouteNode;
                Map tempTaskMap;
                String taskPersonId;
                String taskPersonRole;
                String nodePersonId;
                String strNodeId;
                for (int i = 0; i < taskMapList.size(); i++) {
                    tempTaskMap = (Map) taskMapList.get(i);
                    taskPersonId = (String) tempTaskMap.get("PersonId");
                    taskPersonRole = (String) tempTaskMap.get("Approver Role");
                    for (int j = 0; j < mlRouteNodes.size(); j++) {
                        mapRouteNode = (Map) mlRouteNodes.get(j);
                        //Added by IRM (DSM) for 2022x.02 CW-02 for Defect #56054 - START
                        nodePersonId = (String) mapRouteNode.get(DomainObject.SELECT_PHYSICAL_ID);
                        //Added by IRM (DSM) for 2022x.02 CW-02 for Defect #56054 - END
                        strNodeId = (String) mapRouteNode.get(DomainRelationship.SELECT_ID);           
                        if (taskPersonId.equals(nodePersonId)) {
                            DomainRelationship.setAttributeValue(this.context, strNodeId, strApproverRole, taskPersonRole);
                            mlRouteNodes.remove(mapRouteNode);
                        }
                    }
                }
            }
        }


        /**
         * @param routeMemberMapList
         * @throws Exception
         */
        void giveRouteMembersAccess(MapList routeMemberMapList) throws Exception {
            if (this.route != null && routeMemberMapList != null && routeMemberMapList.size() > 0) {
                this.route.addRouteMemberAccess(this.context, routeMemberMapList);
            }
        }

        /**
         * @param routeMemberMapList
         * @throws Exception
         */
        void provideRouteUserGroupAccess(MapList routeMemberMapList) throws Exception {
            if (this.route != null && routeMemberMapList != null && routeMemberMapList.size() > 0) {
                StringList groupList = new StringList();
                Iterator iterator = routeMemberMapList.iterator();
                Map<Object, Object> objectMap;
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    logger.log(Level.INFO, "Group Map >> " + objectMap);
                    groupList.add((String) objectMap.get("GroupName"));
                }
                logger.log(Level.INFO, "Add Group As Member >> " + groupList);
                this.route.addGroupAsRouteMember(this.context, groupList);
            }
        }


        /**
         * @param objectId
         * @return
         */
        HashMap getRouteStateDetails(String objectId) {
            HashMap stateMap = new HashMap();
            stateMap.put(objectId, "Review");
            return stateMap;
        }


        /**
         * @param stateMap
         * @return
         */
        Hashtable getRouteAttributes(HashMap stateMap) {
            Hashtable props = new Hashtable();
            String routeInstruction = this.approvalPreference.getPreferredRouteInstruction();
            String parentId = this.objectOid;
            props.put("routeName", "");
            props.put("routeAutoName", "true");
            props.put("templateId", "");
            props.put("templateName", "");
            props.put("routeInitiateManner", "");
            props.put("routeStart", "start");
            props.put("visblToParent", "false");
            props.put("routeDescription", "");
            props.put("routeCompletionAction", "Promote Connected Object");
            props.put("routeBasePurpose", "Approval");
            props.put("objectId", this.objectOid);
            props.put("projectId", parentId);
            props.put("scopeId", "All");
            props.put("selscope", "");
            props.put("routeMemberFilter", "");
            props.put("routeDueDate", this.taskDueDate);
            props.put("routeAction", "Approve");
            props.put("routeInstructions", routeInstruction);
            props.put("documentID", "~".concat(this.objectOid).concat("~"));
            props.putAll(stateMap);
            return props;
        }


        /**
         * @param taskMapList
         * @return
         * @throws FrameworkException
         */
        MapList getMemberAccessDetails(MapList taskMapList) throws FrameworkException {
            MapList routeMemberMapList = new MapList();
            String sUser = this.context.getUser();

            if (taskMapList != null) {

                String sCreateRoute = PropertyUtil.getSchemaProperty(this.context, "attribute_CreateRoute");
                StringList personSelects = new StringList();
                personSelects.add("to[" + DomainObject.RELATIONSHIP_EMPLOYEE + "].from.name");
                personSelects.add("to[" + DomainObject.RELATIONSHIP_EMPLOYEE + "].from.attribute[Title]");
                personSelects.add("attribute[" + DomainObject.ATTRIBUTE_FIRST_NAME + "]");
                personSelects.add("attribute[" + DomainObject.ATTRIBUTE_LAST_NAME + "]");

                HashMap taskMap;
                HashMap memberMap;
                String personId;
                String personName;
                DomainObject domainObject;
                String userName;
                Map personMap;

                for (Iterator listItr = taskMapList.iterator(); listItr.hasNext(); ) {
                    taskMap = (HashMap) listItr.next();
                    memberMap = new HashMap();
                    personId = (String) taskMap.get("PersonId");
                    personName = (String) taskMap.get("PersonName");

                    if (personId.equals("Role") || personId.equals("Group")) {
                        memberMap.put("type", personId);
                        memberMap.put("projectLead", "");
                        memberMap.put("createRoute", "");
                        memberMap.put("OrganizationName", "");
                        memberMap.put("OrganizationTitle", "");
                        memberMap.put("access", "Read");
                        memberMap.put("LastFirstName", personName);
                        memberMap.put("name", personName);
                        memberMap.put("GroupName", (String) taskMap.get("GroupName"));
                        memberMap.put(DomainConstants.SELECT_ID, personId);
                        memberMap.put(DomainConstants.SELECT_NAME, personName);
                    } else {
                        domainObject = DomainObject.newInstance(this.context, personId);
                        personMap = domainObject.getInfo(this.context, personSelects);
                        userName = domainObject.getName();

                        memberMap.put(DomainObject.SELECT_ID, personId);
                        memberMap.put(DomainObject.SELECT_NAME, userName);
                        memberMap.put("LastFirstName", (String) personMap.get("attribute[" + DomainObject.ATTRIBUTE_LAST_NAME + "]") + ", " + (String) personMap.get("attribute[" + DomainObject.ATTRIBUTE_FIRST_NAME + "]"));
                        memberMap.put(DomainObject.SELECT_TYPE, domainObject.getInfo(this.context, DomainConstants.SELECT_TYPE));
                        memberMap.put("projectLead", "");
                        memberMap.put("createRoute", sCreateRoute);
                        memberMap.put("OrganizationName", (String) personMap.get("to[" + DomainObject.RELATIONSHIP_EMPLOYEE + "].from.name"));
                        memberMap.put("OrganizationTitle", (String) personMap.get("to[" + DomainObject.RELATIONSHIP_EMPLOYEE + "].from.attribute[Title]"));
                        if (userName.equals(sUser)) {
                            memberMap.put("access", "Add Remove");
                        } else {
                            memberMap.put("access", "Read");
                        }
                    }
                    routeMemberMapList.add(memberMap);
                }
            }
            return routeMemberMapList;
        }


        /**
         * @return
         * @throws Exception
         */
        String getTaskDueDate() throws FrameworkException {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            UserPreferenceUtil preferenceUtil = new UserPreferenceUtil();
            DSMUserPreferenceConfig preferenceConfig = preferenceUtil.getDSMUserPreferenceConfig(this.context);
            String irmRouteTaskDueDay = preferenceConfig.getIrmRouteTaskDueDay();
            int numberOfDay = UIUtil.isNotNullAndNotEmpty(irmRouteTaskDueDay) ? Integer.parseInt(irmRouteTaskDueDay.trim()) : 1;
            calendar.add(Calendar.DATE, numberOfDay);
            date = calendar.getTime();
            logger.log(Level.INFO, "Date: " + date);
            SimpleDateFormat matrixDateFormat = new SimpleDateFormat(eMatrixDateFormat.getEMatrixDateFormat(), Locale.US);
            String formattedDueDate = matrixDateFormat.format(date);
            return formattedDueDate;
        }


        /**
         * @return
         */
        MapList getTaskMemberDetails() {
            MapList taskMapList = new MapList();

            String routeSequenceStr = Person.ATTRIBUTE_ROUTE_SEQUENCE;
            String routeInstructionsStr = Person.ATTRIBUTE_ROUTE_INSTRUCTIONS;
            String routeScheduledCompletionDateStr = Person.ATTRIBUTE_SCHEDULED_COMPLETION_DATE;
            String taskNameStr = Person.ATTRIBUTE_TITLE;
            String routeAllowDelegation = Person.ATTRIBUTE_ALLOW_DELEGATION;
            String routeDueDateOffset = PropertyUtil.getSchemaProperty(this.context, "attribute_DueDateOffset");
            String taskNeedsReview = PropertyUtil.getSchemaProperty(this.context, "attribute_ReviewTask");
            String routeActionStr = PropertyUtil.getSchemaProperty(this.context, "attribute_RouteAction");

            logger.log(Level.INFO, "Get Member List");
            String strAllowDelegation = String.valueOf(Boolean.TRUE).toUpperCase();

            List<IRMMember> memberList = this.approvalPreference.getPreferredRouteTaskRecipientMemberList();
            String routeInstruction = this.approvalPreference.getPreferredRouteInstruction();

            if (null != memberList && !memberList.isEmpty()) {
                HashMap taskMap;
                String personId;
                String personName;
                String role;
                String fullName;
                for (IRMMember member : memberList) {
                    taskMap = new HashMap();
                    personId = member.getId();
                    personName = member.getName();
                    role = member.getRole();
                    fullName = member.getFullName().replaceAll(";", ",");

                    logger.log(Level.INFO, "Member ID: " + personId);
                    logger.log(Level.INFO, "Member Name: " + personName);
                    logger.log(Level.INFO, "Member Role: " + role);
                    logger.log(Level.INFO, "Member FullName: " + fullName);

                    taskMap.put("PersonId", personId);
                    taskMap.put("PersonName", fullName);
                    taskMap.put("Approver Role", role);
                    taskMap.put(routeInstructionsStr, routeInstruction);

                    taskMap.put(taskNameStr, DomainConstants.EMPTY_STRING);
                    taskMap.put(routeScheduledCompletionDateStr, this.taskDueDate);
                    taskMap.put(routeActionStr, "Approve");
                    taskMap.put(routeSequenceStr, "1");
                    taskMap.put(routeDueDateOffset, "");
                    taskMap.put("templateFlag", null);
                    taskMap.put(taskNeedsReview, "No");
                    taskMap.put(DomainConstants.SELECT_NAME, fullName);

                    taskMap.put(routeAllowDelegation, strAllowDelegation);
                    taskMapList.add(taskMap);
                }
            }
            return taskMapList;
        }


        /**
         * @return
         * @throws Exception
         */
        MapList getUserGroupDetails() throws Exception {
            MapList taskMapList = new MapList();

            String routeSequenceStr = Person.ATTRIBUTE_ROUTE_SEQUENCE;
            String routeInstructionsStr = Person.ATTRIBUTE_ROUTE_INSTRUCTIONS;
            String routeScheduledCompletionDateStr = Person.ATTRIBUTE_SCHEDULED_COMPLETION_DATE;
            String taskNameStr = Person.ATTRIBUTE_TITLE;
            String routeAllowDelegation = Person.ATTRIBUTE_ALLOW_DELEGATION;
            String routeDueDateOffset = PropertyUtil.getSchemaProperty(this.context, "attribute_DueDateOffset");
            String taskNeedsReview = PropertyUtil.getSchemaProperty(this.context, "attribute_ReviewTask");
            String routeActionStr = PropertyUtil.getSchemaProperty(this.context, "attribute_RouteAction");

            String userGroupStr = EnoviaResourceBundle.getFrameworkStringResourceProperty(this.context, "emxFramework.Type.Group", this.context.getLocale());

            String strAllowDelegation = String.valueOf(Boolean.TRUE).toUpperCase();

            List<IRMUserGroup> userGroupList = this.approvalPreference.getPreferredRouteTaskRecipientUserGroupList();
            String routeInstruction = this.approvalPreference.getPreferredRouteInstruction();

            if (null != userGroupList && !userGroupList.isEmpty()) {
                logger.log(Level.INFO, "Iterate User Groups");

                HashMap taskMap;
                String personId;
                String personName;
                for (IRMUserGroup userGroup : userGroupList) {
                    taskMap = new HashMap();

                    personId = userGroupStr;
                    personName = userGroup.getId();

                    logger.log(Level.INFO, "Person ID: " + personId);
                    logger.log(Level.INFO, "Person Name: " + personName);

                    taskMap.put("PersonId", personId);
                    taskMap.put("PersonName", personName);
                    taskMap.put("GroupName", userGroup.getName());
                    taskMap.put("Approver Role", null);
                    taskMap.put(routeInstructionsStr, routeInstruction);

                    taskMap.put(taskNameStr, DomainConstants.EMPTY_STRING);
                    taskMap.put(routeScheduledCompletionDateStr, this.taskDueDate);
                    taskMap.put(routeActionStr, "Approve");
                    taskMap.put(routeSequenceStr, "1");
                    taskMap.put(routeDueDateOffset, "");
                    taskMap.put("templateFlag", null);
                    taskMap.put(taskNeedsReview, "No");
                    taskMap.put(DomainConstants.SELECT_NAME, personName);

                    taskMap.put(routeAllowDelegation, strAllowDelegation);
                    taskMapList.add(taskMap);
                }
            }
            return taskMapList;
        }
    }
}

