package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;

public class IRMApprovalPreference {
    private static final Logger logger = Logger.getLogger(IRMApprovalPreference.class.getName());
    String preferredRouteInstruction;
    String preferredRouteAction;
    String preferredRouteTaskRecipientMembers;
    String preferredRouteTaskRecipientUserGroups;
    boolean isPreferredRouteTaskRecipientUserGroups;
    boolean isPreferredRouteTaskRecipientMembers;
    List<Member> preferredRouteTaskRecipientMemberList;
    List<UserGroup> preferredRouteTaskRecipientUserGroupList;

    public IRMApprovalPreference(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        UserPreferenceUtil util = new UserPreferenceUtil();
        this.preferredRouteInstruction = util.getPreferredRouteInstruction(context);
        this.preferredRouteAction = util.getPreferredRouteAction(context);
        this.preferredRouteTaskRecipientMembers = util.getPreferredRouteTaskRecipientMembers(context);
        this.preferredRouteTaskRecipientUserGroups = util.getPreferredRouteTaskRecipientUserGroups(context);
        this.isPreferredRouteTaskRecipientMembers = util.isPreferredRouteTaskRecipientMembers(context);
        this.isPreferredRouteTaskRecipientUserGroups = util.isPreferredRouteTaskRecipientUserGroup(context);

        this.preferredRouteTaskRecipientMemberList = util.getPreferredRouteTaskRecipientMemberList(context);
        this.preferredRouteTaskRecipientUserGroupList = util.getPreferredRouteTaskRecipientUserGroupList(context);
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("IRMApprovalPreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    public String getPreferredRouteInstruction() {
        return preferredRouteInstruction;
    }

    public String getPreferredRouteAction() {
        return preferredRouteAction;
    }

    public String getPreferredRouteTaskRecipientMembers() {
        return preferredRouteTaskRecipientMembers;
    }

    public String getPreferredRouteTaskRecipientUserGroups() {
        return preferredRouteTaskRecipientUserGroups;
    }

    public boolean isPreferredRouteTaskRecipientUserGroups() {
        return isPreferredRouteTaskRecipientUserGroups;
    }

    public boolean isPreferredRouteTaskRecipientMembers() {
        return isPreferredRouteTaskRecipientMembers;
    }

    public List<Member> getPreferredRouteTaskRecipientMemberList() {
        return preferredRouteTaskRecipientMemberList;
    }

    public List<UserGroup> getPreferredRouteTaskRecipientUserGroupList() {
        return preferredRouteTaskRecipientUserGroupList;
    }

    @Override
    public String toString() {
        return "IRMApprovalPreference{" +
                "preferredRouteInstruction='" + preferredRouteInstruction + '\'' +
                ", preferredRouteAction='" + preferredRouteAction + '\'' +
                ", preferredRouteTaskRecipientMembers='" + preferredRouteTaskRecipientMembers + '\'' +
                ", preferredRouteTaskRecipientUserGroups='" + preferredRouteTaskRecipientUserGroups + '\'' +
                ", isPreferredRouteTaskRecipientUserGroups=" + isPreferredRouteTaskRecipientUserGroups +
                ", isPreferredRouteTaskRecipientMembers=" + isPreferredRouteTaskRecipientMembers +
                ", preferredRouteTaskRecipientMemberList=" + preferredRouteTaskRecipientMemberList +
                ", preferredRouteTaskRecipientUserGroupList=" + preferredRouteTaskRecipientUserGroupList +
                '}';
    }
}
