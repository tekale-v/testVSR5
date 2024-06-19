package com.pg.dsm.preference.models;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.designtools.datamanagement.DataConstants;
import com.pg.designtools.util.CacheManagement;
import com.pg.dsm.preference.util.TechnicalSpecificationPreferenceUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class TechnicalSpecificationPreference {

    private static final Logger logger = Logger.getLogger(TechnicalSpecificationPreference.class.getName());
    Context context;
    String user;
    Locale locale;
    String partTypeJson;
    StringList partTypeOptions;
    StringList partTypeDisplayOptions;
    String segmentJson;
    StringList segmentOptions;
    StringList segmentDisplayOptions;

    public TechnicalSpecificationPreference(Context context) throws Exception {
        Instant startTime = Instant.now();
        this.context = context;
        this.locale = context.getLocale();
        this.user = context.getUser();


        TechnicalSpecificationPreferenceUtil technicalSpecPreferenceUtil = new TechnicalSpecificationPreferenceUtil();
        UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
        CacheManagement cacheManagement = new CacheManagement(this.context);

        this.partTypeDisplayOptions = new StringList();
        this.partTypeOptions = new StringList();
        this.getPartType(technicalSpecPreferenceUtil, userPreferenceUtil);

        this.segmentDisplayOptions = new StringList();
        this.segmentOptions = new StringList();
        this.getSegment(cacheManagement, userPreferenceUtil);

        this.loadPartTypeJson(technicalSpecPreferenceUtil, userPreferenceUtil);
        this.loadSegmentJson(technicalSpecPreferenceUtil, cacheManagement, userPreferenceUtil);


    }

    public Context getContext() {
        return context;
    }

    public String getUser() {
        return user;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getPartTypeJson() {
        return partTypeJson;
    }

    public String getSegmentJson() {
        return segmentJson;
    }

    void loadPartTypeJson(TechnicalSpecificationPreferenceUtil technicalSpecPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        this.partTypeJson = technicalSpecPreferenceUtil.getPartTypeJson(this.context, userPreferenceUtil);
    }

    void loadSegmentJson(TechnicalSpecificationPreferenceUtil technicalSpecPreferenceUtil, CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws Exception {
        this.segmentJson = technicalSpecPreferenceUtil.getSegmentJson(this.context, cacheManagement, userPreferenceUtil);
    }


    void getPartType(TechnicalSpecificationPreferenceUtil technicalSpecPreferenceUtil, UserPreferenceUtil userPreferenceUtil) throws FrameworkException {
        Map<String, StringList> partTypeRanges = technicalSpecPreferenceUtil.getPartTypeRanges(this.context, userPreferenceUtil);
        if (null != partTypeRanges && !partTypeRanges.isEmpty()) {
            this.partTypeDisplayOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.partTypeOptions.add(pgV3Constants.SYMBOL_SPACE);
            this.partTypeDisplayOptions.addAll(partTypeRanges.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.partTypeOptions.addAll(partTypeRanges.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Type range map is empty");
        }
    }

    void getSegment(CacheManagement cacheManagement, UserPreferenceUtil userPreferenceUtil) throws MatrixException {
        Map<String, StringList> rangeMap = userPreferenceUtil.getSegment(this.context, cacheManagement);
        if (null != rangeMap && !rangeMap.isEmpty()) {
            this.segmentDisplayOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_DISPLAY_CHOICES));
            this.segmentOptions.addAll(rangeMap.get(DataConstants.CONST_FIELD_CHOICES));
        } else {
            logger.log(Level.WARNING, "Segments range is empty");
        }
    }

    public StringList getPartTypeOptions() {
        return partTypeOptions;
    }

    public StringList getPartTypeDisplayOptions() {
        return partTypeDisplayOptions;
    }

    public StringList getSegmentOptions() {
        return segmentOptions;
    }

    public StringList getSegmentDisplayOptions() {
        return segmentDisplayOptions;
    }

    @Override
    public String toString() {
        return "TechnicalSpecificationPreference{" +
                "context=" + context +
                ", user='" + user + '\'' +
                ", locale=" + locale +
                ", segmentJson='" + segmentJson + '\'' +
                ", partTypeOptions=" + partTypeOptions +
                ", partTypeDisplayOptions=" + partTypeDisplayOptions +
                ", partTypeJson='" + partTypeJson + '\'' +
                '}';
    }
}


