package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.util.PreferenceManagement;
import com.pg.dsm.preference.util.IRMAttributePreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;

public class IRMAttributePreference {
    private static final Logger logger = Logger.getLogger(IRMAttributePreference.class.getName());
    Context context;
    String preferredBusinessUseClass;
    String preferredHighlyRestrictedClass;
    String preferredTitle;
    String preferredDescription;
    String preferredPolicy;
    String preferredRegionName;
    String preferredRegionOID;
    String preferredClassification;
    String preferredSecurityClassificationName;
    String preferredSecurityClassificationOID;
    boolean isBusinessUseClass;
    boolean isHighlyRestrictedClass;

    String preferredSharingMembers;
    String preferredSharingMembersOID;
    String preferredSharingMembersDisplay;

    String preferredBusinessArea;
    String preferredBusinessAreaName;
    String preferredBusinessAreaID;
    String preferredBusinessAreaDisplay;

    /**
     * @param context
     * @throws MatrixException
     */
    public IRMAttributePreference(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        this.context = context;
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        IRMAttributePreferenceUtil irmAttributePreferenceUtil = new IRMAttributePreferenceUtil();
        PreferenceManagement preferenceManagement = new PreferenceManagement(this.context);

        this.preferredBusinessUseClass = userPreferenceUtil.getPreferredBusinessUseClass(this.context);
        this.preferredHighlyRestrictedClass = userPreferenceUtil.getPreferredHighlyRestrictedClass(this.context);

        this.preferredTitle = irmAttributePreferenceUtil.getTitle(this.context, preferenceManagement, userPreferenceUtil);
        this.preferredDescription = irmAttributePreferenceUtil.getDescription(this.context, preferenceManagement, userPreferenceUtil);
        this.preferredPolicy = irmAttributePreferenceUtil.getPolicy(this.context, preferenceManagement, userPreferenceUtil);
        this.preferredRegionName = irmAttributePreferenceUtil.getRegion(this.context, preferenceManagement, userPreferenceUtil);
        this.preferredClassification = irmAttributePreferenceUtil.getClassification(this.context, preferenceManagement, userPreferenceUtil);
        this.preferredBusinessArea = irmAttributePreferenceUtil.getBusinessArea(this.context, preferenceManagement, userPreferenceUtil);
        this.preferredBusinessAreaName = irmAttributePreferenceUtil.getBusinessAreaName(this.context, userPreferenceUtil, this.preferredBusinessArea);
        this.preferredBusinessAreaID = irmAttributePreferenceUtil.getBusinessAreaID(this.context, userPreferenceUtil, this.preferredBusinessArea);
        this.preferredSharingMembers = irmAttributePreferenceUtil.getSharingMember(this.context, preferenceManagement, userPreferenceUtil);

        this.preferredRegionOID = userPreferenceUtil.getPreferredIRMRegionID(this.context);
        this.preferredSecurityClassificationName = userPreferenceUtil.getSecurityCategoryClassificationName(this.context);
        this.preferredSecurityClassificationOID = userPreferenceUtil.getSecurityCategoryClassificationOID(this.context);
        this.isBusinessUseClass = userPreferenceUtil.isBusinessUseClassificationSelected(this.context);
        this.isHighlyRestrictedClass = userPreferenceUtil.isHighlyRestrictedClassificationSelected(this.context);

        this.preferredSharingMembersOID = userPreferenceUtil.getPreferredIRMSharingMembersID(this.context);
        this.preferredSharingMembersDisplay = userPreferenceUtil.getPreferredIRMSharingMembersDisplay(this.context);

        this.preferredBusinessAreaDisplay = DomainConstants.EMPTY_STRING;
        this.loadBusinessArea(irmAttributePreferenceUtil);

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("IRMAttributePreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    void loadBusinessArea(IRMAttributePreferenceUtil irmAttributePreferenceUtil) throws FrameworkException {
        this.preferredBusinessAreaDisplay = irmAttributePreferenceUtil.getBusinessAreaJSON(this.context);
    }

    public Context getContext() {
        return context;
    }

    public String getPreferredBusinessUseClass() {
        return preferredBusinessUseClass;
    }

    public String getPreferredHighlyRestrictedClass() {
        return preferredHighlyRestrictedClass;
    }

    public String getPreferredTitle() {
        return preferredTitle;
    }

    public String getPreferredDescription() {
        return preferredDescription;
    }

    public String getPreferredPolicy() {
        return preferredPolicy;
    }

    public String getPreferredRegionName() {
        return preferredRegionName;
    }

    public String getPreferredRegionOID() {
        return preferredRegionOID;
    }

    public String getPreferredClassification() {
        return preferredClassification;
    }

    public String getPreferredSecurityClassificationName() {
        return preferredSecurityClassificationName;
    }

    public String getPreferredSecurityClassificationOID() {
        return preferredSecurityClassificationOID;
    }

    public boolean isBusinessUseClass() {
        return isBusinessUseClass;
    }

    public boolean isHighlyRestrictedClass() {
        return isHighlyRestrictedClass;
    }

    public String getPreferredSharingMembers() {
        return preferredSharingMembers;
    }

    public String getPreferredSharingMembersOID() {
        return preferredSharingMembersOID;
    }

    public String getPreferredSharingMembersDisplay() {
        return preferredSharingMembersDisplay;
    }

    public String getPreferredBusinessArea() {
        return preferredBusinessArea;
    }

    public String getPreferredBusinessAreaDisplay() {
        return preferredBusinessAreaDisplay;
    }

    public String getPreferredBusinessAreaName() {
        return preferredBusinessAreaName;
    }

    public String getPreferredBusinessAreaID() {
        return preferredBusinessAreaID;
    }

    @Override
    public String toString() {
        return "IRMAttributePreference{" +
                "context=" + context +
                ", preferredBusinessUseClass='" + preferredBusinessUseClass + '\'' +
                ", preferredHighlyRestrictedClass='" + preferredHighlyRestrictedClass + '\'' +
                ", preferredTitle='" + preferredTitle + '\'' +
                ", preferredDescription='" + preferredDescription + '\'' +
                ", preferredPolicy='" + preferredPolicy + '\'' +
                ", preferredRegionName='" + preferredRegionName + '\'' +
                ", preferredRegionOID='" + preferredRegionOID + '\'' +
                ", preferredClassification='" + preferredClassification + '\'' +
                ", preferredSecurityClassificationName='" + preferredSecurityClassificationName + '\'' +
                ", preferredSecurityClassificationOID='" + preferredSecurityClassificationOID + '\'' +
                ", isBusinessUseClass=" + isBusinessUseClass +
                ", isHighlyRestrictedClass=" + isHighlyRestrictedClass +
                ", preferredSharingMembers='" + preferredSharingMembers + '\'' +
                ", preferredSharingMembersOID='" + preferredSharingMembersOID + '\'' +
                ", preferredSharingMembersDisplay='" + preferredSharingMembersDisplay + '\'' +
                ", preferredBusinessArea='" + preferredBusinessArea + '\'' +
                ", preferredBusinessAreaName='" + preferredBusinessAreaName + '\'' +
                ", preferredBusinessAreaID='" + preferredBusinessAreaID + '\'' +
                ", preferredBusinessAreaDisplay='" + preferredBusinessAreaDisplay + '\'' +
                '}';
    }
}
