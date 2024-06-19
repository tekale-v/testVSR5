package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtilIRM;

import matrix.db.Context;
import matrix.util.MatrixException;
//Added by IRM(Sogeti) 2022x.04 Dec CW Requirement 47851 
public class IRMRouteDetails {
    private static final Logger logger = Logger.getLogger(IRMRouteDetails.class.getName());
    String preferredRouteInstruction;
    String preferredRouteAction;
    String preferredRouteTaskRecipientMembers;
    String preferredRouteTaskRecipientUserGroups;
    boolean isPreferredRouteTaskRecipientUserGroups;
    boolean isPreferredRouteTaskRecipientMembers;
    List<IRMMember> preferredRouteTaskRecipientMemberList;
    List<IRMUserGroup> preferredRouteTaskRecipientUserGroupList;

    public IRMRouteDetails(Context context,IRMTemplate irmTemplateObj) throws MatrixException {
        Instant startTime = Instant.now();
        UserPreferenceUtilIRM util = new UserPreferenceUtilIRM();       
	   this.preferredRouteInstruction = util.getPreferredRouteInstruction(context,irmTemplateObj);
        this.preferredRouteAction = util.getPreferredRouteAction(context,irmTemplateObj);
        this.preferredRouteTaskRecipientMembers = util.getPreferredRouteTaskRecipientMembers(context,irmTemplateObj);
        this.preferredRouteTaskRecipientUserGroups = util.getPreferredRouteTaskRecipientUserGroups(context,irmTemplateObj);
        this.isPreferredRouteTaskRecipientMembers = util.isPreferredRouteTaskRecipientMembers(context,irmTemplateObj);
        this.isPreferredRouteTaskRecipientUserGroups = util.isPreferredRouteTaskRecipientUserGroup(context,irmTemplateObj);

        this.preferredRouteTaskRecipientMemberList = util.getPreferredRouteTaskRecipientMemberList(context,irmTemplateObj);
        this.preferredRouteTaskRecipientUserGroupList = util.getPreferredRouteTaskRecipientUserGroupList(context,irmTemplateObj);
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

    public List<IRMMember> getPreferredRouteTaskRecipientMemberList() {
        return preferredRouteTaskRecipientMemberList;
    }

    public List<IRMUserGroup> getPreferredRouteTaskRecipientUserGroupList() {
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
