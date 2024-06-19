package com.pg.dsm.preference;

import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.pg.ctrlm.custom.CtrlmJobContext;
import com.pg.dsm.preference.cli.UserPreferencesCli;
import com.pg.dsm.preference.models.TechnicalSpecificationPreference;

import matrix.db.Context;
import matrix.util.MatrixException;

public class UserPreferencesApp {
    private static final Logger logger = Logger.getLogger(UserPreferencesApp.class.getName());

    public static void main(String[] args) {
        try {
            Context context = CtrlmJobContext.getCtrlmContext();
            if (context.isContextSet()) {
                ContextUtil.pushContext(context, PropertyUtil.getSchemaProperty(context, "person_UserAgent"), DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
				retrieveTechnicalSpecificationPreferences(context);
                ContextUtil.popContext(context);
            }
        } catch (Exception e) {
            logger.warning("Exception occurred during retrieve: " + e);
        }
    }


    private static void retrieveUserPackagingPreference(Context context) throws Exception {
        UserPreferencesCli userPreferencesCli = new UserPreferencesCli();
        userPreferencesCli.getUserPackagingPreference(context, new String[]{"dsotest30.im"});
    }

    public static void retrieveUserPlantDataPreference(Context context) throws MatrixException {
        UserPreferencesCli cli = new UserPreferencesCli();
        cli.retrieveUserPlantDataAllFilter(context, new String[]{"dsotest30.im"});
    }

    public static void insertUserPlantDataPreference(Context context) throws MatrixException {
        UserPreferencesCli userPreferencesCli = new UserPreferencesCli();
        userPreferencesCli.insertUserPlantDataPreference(context, new String[]{"20336.41905.45824.13062", "dsotest30.im"});
    }

    private static void retrieveActivePlants(Context context) throws Exception {
        UserPreferencesCli userPreferencesCli = new UserPreferencesCli();
        userPreferencesCli.retrieveActivePlantsJSON(context);
    }

    public static void retrieveTechnicalSpecificationPreferences(Context context) throws Exception {
        TechnicalSpecificationPreference preference = new TechnicalSpecificationPreference(context);
        String partTypeJson = preference.getPartTypeJson();
    }

}
