package com.pg.dsm.preference.models;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class DefaultCreatePartTypePreference {
    private static final Logger logger = Logger.getLogger(DefaultCreatePartTypePreference.class.getName());
    Context context;
    String user;
    String preferredDefaultTypeOnCreateEquivalent;
    String preferredDefaultTypeOnCreateProduct;
    String preferredDefaultTypeOnCreateSpec;
    String preferredDefaultTypeOnCreateEquivalentDisplay;
    String preferredDefaultTypeOnCreateProductDisplay;
    String preferredDefaultTypeOnCreateSpecDisplay;
    String preferredDefaultPlantName;
    String preferredDefaultPlantID;
    StringList productTypeDisplayOptions;
    StringList productTypeOptions;
    StringList specTypeDisplayOptions;
    StringList specTypeOptions;
    String preferredDefaultSharingMemberName;
    String preferredDefaultSharingMemberNameDisplay;
    String preferredDefaultSharingMemberID;

    /**
     * @param context
     * @throws MatrixException
     */
    public DefaultCreatePartTypePreference(Context context) throws MatrixException {
        Instant startTime = Instant.now();
        this.context = context;
        this.user = context.getUser();

        UserPreferenceUtil util = new UserPreferenceUtil();
        this.preferredDefaultTypeOnCreateEquivalent = util.getPreferredDefaultTypeOnEquivalent(this.context);
        this.preferredDefaultTypeOnCreateEquivalentDisplay = (UIUtil.isNotNullAndNotEmpty(this.preferredDefaultTypeOnCreateEquivalent)) ? util.getTypeActualDisplayName(this.context, this.preferredDefaultTypeOnCreateEquivalent) : DomainConstants.EMPTY_STRING;
        this.preferredDefaultTypeOnCreateProduct = util.getPreferredDefaultTypeOnProduct(this.context);
        this.preferredDefaultTypeOnCreateProductDisplay = (UIUtil.isNotNullAndNotEmpty(this.preferredDefaultTypeOnCreateProduct)) ? util.getTypeActualDisplayName(this.context, this.preferredDefaultTypeOnCreateProduct) : DomainConstants.EMPTY_STRING;
        this.preferredDefaultTypeOnCreateSpec = util.getPreferredDefaultTypeOnSpec(this.context);
        this.preferredDefaultTypeOnCreateSpecDisplay = (UIUtil.isNotNullAndNotEmpty(this.preferredDefaultTypeOnCreateSpec)) ? util.getTypeActualDisplayName(this.context, this.preferredDefaultTypeOnCreateSpec) : DomainConstants.EMPTY_STRING;
        this.preferredDefaultPlantName = util.getPreferredDefaultPlantName(this.context);
        this.preferredDefaultPlantID = util.getPreferredDefaultPlantID(this.context);
        this.preferredDefaultSharingMemberName = util.getPreferredDefaultSharingMemberName(this.context);
        this.preferredDefaultSharingMemberNameDisplay = util.getPreferredDefaultSharingMemberNameDisplay(context);
        this.preferredDefaultSharingMemberID = util.getPreferredDefaultSharingMemberID(this.context);

        this.productTypeDisplayOptions = new StringList();
        this.productTypeOptions = new StringList();
        this.getCreateProductTypes();

        this.specTypeDisplayOptions = new StringList();
        this.specTypeOptions = new StringList();
        this.getCreateSpecTypes();

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("DefaultCreatePartTypePreference instantiation - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    void getCreateProductTypes() throws MatrixException {
        HashMap paramMap = new HashMap();
        HashMap requestedMap = new HashMap();
        paramMap.put("requestMap", requestedMap);
        Map partTypeMap = (Map) JPO.invoke(context, "emxCPNProductDataPartStage", null, "getProductDataTypes", JPO.packArgs(paramMap), Map.class);
        if (null != partTypeMap && !partTypeMap.isEmpty()) {
            this.productTypeDisplayOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.productTypeOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.productTypeDisplayOptions.addAll((StringList) partTypeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.productTypeOptions.addAll((StringList) partTypeMap.get(DataConstants.CONST_FIELD_CHOICES));
        }
    }

    void getCreateSpecTypes() throws MatrixException {
        HashMap paramMap = new HashMap();
        HashMap requestedMap = new HashMap();
        paramMap.put("requestMap", requestedMap);
        Map partTypeMap = (Map) JPO.invoke(context, "enoTechnicalSpecUI", null, "getAvailableTechnicalSpecTypes", JPO.packArgs(paramMap), Map.class);
        if (null != partTypeMap && !partTypeMap.isEmpty()) {
            this.specTypeDisplayOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.specTypeOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.specTypeDisplayOptions.addAll((StringList) partTypeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.specTypeOptions.addAll((StringList) partTypeMap.get(DataConstants.CONST_FIELD_CHOICES));
        }
    }

    public Context getContext() {
        return context;
    }

    public String getUser() {
        return user;
    }

    public String getPreferredDefaultTypeOnCreateEquivalent() {
        return preferredDefaultTypeOnCreateEquivalent;
    }

    public String getPreferredDefaultTypeOnCreateProduct() {
        return preferredDefaultTypeOnCreateProduct;
    }

    public String getPreferredDefaultTypeOnCreateSpec() {
        return preferredDefaultTypeOnCreateSpec;
    }

    public String getPreferredDefaultTypeOnCreateEquivalentDisplay() {
        return preferredDefaultTypeOnCreateEquivalentDisplay;
    }

    public String getPreferredDefaultTypeOnCreateProductDisplay() {
        return preferredDefaultTypeOnCreateProductDisplay;
    }

    public String getPreferredDefaultTypeOnCreateSpecDisplay() {
        return preferredDefaultTypeOnCreateSpecDisplay;
    }

    public String getPreferredDefaultPlantName() {
        return preferredDefaultPlantName;
    }

    public String getPreferredDefaultPlantID() {
        return preferredDefaultPlantID;
    }

    public StringList getProductTypeDisplayOptions() {
        return productTypeDisplayOptions;
    }

    public StringList getProductTypeOptions() {
        return productTypeOptions;
    }

    public StringList getSpecTypeDisplayOptions() {
        return specTypeDisplayOptions;
    }

    public StringList getSpecTypeOptions() {
        return specTypeOptions;
    }

    public String getPreferredDefaultSharingMemberName() {
        return preferredDefaultSharingMemberName;
    }

    public String getPreferredDefaultSharingMemberID() {
        return preferredDefaultSharingMemberID;
    }

    public String getPreferredDefaultSharingMemberNameDisplay() {
        return preferredDefaultSharingMemberNameDisplay;
    }

    @Override
    public String toString() {
        return "DefaultCreatePartTypePreference{" +
                "context=" + context +
                ", user='" + user + '\'' +
                ", preferredDefaultTypeOnCreateEquivalent='" + preferredDefaultTypeOnCreateEquivalent + '\'' +
                ", preferredDefaultTypeOnCreateProduct='" + preferredDefaultTypeOnCreateProduct + '\'' +
                ", preferredDefaultTypeOnCreateSpec='" + preferredDefaultTypeOnCreateSpec + '\'' +
                ", preferredDefaultTypeOnCreateEquivalentDisplay='" + preferredDefaultTypeOnCreateEquivalentDisplay + '\'' +
                ", preferredDefaultTypeOnCreateProductDisplay='" + preferredDefaultTypeOnCreateProductDisplay + '\'' +
                ", preferredDefaultTypeOnCreateSpecDisplay='" + preferredDefaultTypeOnCreateSpecDisplay + '\'' +
                ", preferredDefaultPlantName='" + preferredDefaultPlantName + '\'' +
                ", preferredDefaultPlantID='" + preferredDefaultPlantID + '\'' +
                ", productTypeDisplayOptions=" + productTypeDisplayOptions +
                ", productTypeOptions=" + productTypeOptions +
                ", specTypeDisplayOptions=" + specTypeDisplayOptions +
                ", specTypeOptions=" + specTypeOptions +
                ", preferredDefaultSharingMemberName='" + preferredDefaultSharingMemberName + '\'' +
                ", preferredDefaultSharingMemberNameDisplay='" + preferredDefaultSharingMemberNameDisplay + '\'' +
                ", preferredDefaultSharingMemberID='" + preferredDefaultSharingMemberID + '\'' +
                '}';
    }
}
