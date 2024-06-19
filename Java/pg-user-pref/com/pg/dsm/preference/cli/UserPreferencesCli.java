package com.pg.dsm.preference.cli;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.preference.Preferences;
import com.pg.dsm.preference.interfaces.ICopyDataPreferenceRepository;
import com.pg.dsm.preference.interfaces.IPlantDataPreferenceRepository;
import com.pg.dsm.preference.models.ChangeActionPreference;
import com.pg.dsm.preference.models.DefaultCreatePartTypePreference;
import com.pg.dsm.preference.models.IPSecurityPreference;
import com.pg.dsm.preference.models.IRMApprovalPreference;
import com.pg.dsm.preference.models.IRMAttributePreference;
import com.pg.dsm.preference.models.IRMProjectSpacePreference;
import com.pg.dsm.preference.models.PackagingPreference;
import com.pg.dsm.preference.models.ProductPreference;
import com.pg.dsm.preference.models.RawMaterialPreference;
import com.pg.dsm.preference.models.TechnicalSpecificationPreference;
import com.pg.dsm.preference.repository.CopyDataPreferenceRepository;
import com.pg.dsm.preference.repository.PlantDataPreferenceRepository;
import com.pg.dsm.preference.template.repository.CommonPickList;
import com.pg.dsm.preference.template.repository.PackagingPickList;
import com.pg.dsm.preference.template.repository.ProductPickList;
import com.pg.dsm.preference.template.repository.RawMaterialPickList;
import com.pg.dsm.preference.util.DSMUPT;
import com.pg.dsm.preference.util.DSMUPTUtil;
import com.pg.dsm.preference.util.IRMUPT;
import com.pg.dsm.preference.util.IRMUPTUtil;
import com.pg.dsm.preference.util.PlantDataPreferenceUtil;
import com.pg.dsm.preference.util.PlantUtil;
import com.pg.dsm.preference.util.UserPreferenceUtil;

import matrix.db.Context;
import matrix.util.MatrixException;
import matrix.util.StringList;

public class UserPreferencesCli {
    private static final Logger logger = Logger.getLogger(UserPreferencesCli.class.getName());

    public static Parameters getParameters(String xmlContent) throws JAXBException {
        Parameters parameters = null;
        try {
            if (UIUtil.isNotNullAndNotEmpty(xmlContent)) {
                JAXBContext jaxbContext = JAXBContext.newInstance(Parameters.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                parameters = (Parameters) unmarshaller.unmarshal(new StringReader(xmlContent));
                logger.log(Level.INFO, "Parameters JAXB loaded");
            } else {
                logger.log(Level.INFO, "XML content empty during JAXB conversion");
            }
        } catch (JAXBException e) {
            throw e;
        }
        return parameters;
    }

    public static Map<String, String[]> getParameterMap(String xmlContent) {
        Map<String, String[]> paramMap = new HashMap<>();
        try {
            UserPreferencesCli cli = new UserPreferencesCli();
            Parameters parameters = cli.getParameters(xmlContent);
            if (null != parameters) {
                List<Parameter> parameterList = parameters.getParameter();
                for (Parameter parameter : parameterList) {
                    paramMap.put(parameter.getKey(), new String[]{parameter.getValue()});
                }
            }
        } catch (Exception e) {
            logger.warning("Exception occurred during insert: " + e);
        }
        return paramMap;
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void retrieveUserPreferences(Context context, String... args) throws Exception {
        getUserIPSecurityPreference(context, args);
        getUserChangeActionPreference(context, args);
        getUserDefaultCreatePreference(context, args);
        getUserPackagingPreference(context, args);
        getUserProductPreference(context, args);
        getUserRawMaterialPreference(context, args);
        getUserIRMAttributePreference(context, args);
        getUserIRMApprovalPreference(context, args);
        getUserProjectSpacePreference(context, args);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void retrieveUserCopyDataPreferences(Context context, String... args) throws Exception {
        retrieveUserCopyDataPackagingFilter(context, args);
        retrieveUserCopyDataProductFilter(context, args);
        retrieveUserCopyDataRawMaterialFilter(context, args);
        retrieveUserCopyDataTechnicalSpecificationFilter(context, args);
        retrieveUserCopyDataAllFilter(context, args);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void retrieveUserPlantDataPreferences(Context context, String... args) throws Exception {
        retrieveUserPlantDataPackagingFilter(context, args);
        retrieveUserPlantDataProductFilter(context, args);
        retrieveUserPlantDataRawMaterialFilter(context, args);
        retrieveUserPlantDataTechnicalSpecificationFilter(context, args);
        retrieveUserPlantDataAllFilter(context, args);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserPackagingPreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve Packaging Preference for user: " + user);
        PackagingPreference packagingPreference = new PackagingPreference(context);
        logger.log(Level.INFO, "Packaging Preference: " + packagingPreference);
        Preferences.PackagingPreference[] packagingPreferences = Preferences.PackagingPreference.values();
        for (Preferences.PackagingPreference packaging : packagingPreferences) {
            logger.log(Level.INFO, "|" + packaging.getPreferencePropertyKey() + "|" + packaging.getName(context) + "|" + packaging.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserProductPreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve Product Preference for user: " + user);
        ProductPreference productPreference = new ProductPreference(context);
        logger.log(Level.INFO, "Product Preference: " + productPreference);
        Preferences.ProductPreference[] productPreferences = Preferences.ProductPreference.values();
        for (Preferences.ProductPreference product : productPreferences) {
            logger.log(Level.INFO, "|" + product.getPreferencePropertyKey() + "|" + product.getName(context) + "|" + product.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserRawMaterialPreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve Raw Material Preference for user: " + user);
        RawMaterialPreference rawMaterialPreference = new RawMaterialPreference(context);
        logger.log(Level.INFO, "Raw Material Preference: " + rawMaterialPreference);
        Preferences.RawMaterialPreference[] rawMaterialPreferences = Preferences.RawMaterialPreference.values();
        for (Preferences.RawMaterialPreference rawMaterial : rawMaterialPreferences) {
            logger.log(Level.INFO, "|" + rawMaterial.getPreferencePropertyKey() + "|" + rawMaterial.getName(context) + "|" + rawMaterial.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserChangeActionPreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve Change Action Preference for user: " + user);
        ChangeActionPreference changeActionPreference = new ChangeActionPreference(context);
        logger.log(Level.INFO, "Change Action Preference: " + changeActionPreference);
        Preferences.ChangeActionPreference[] changeActionPreferences = Preferences.ChangeActionPreference.values();
        for (Preferences.ChangeActionPreference changePreference : changeActionPreferences) {
            logger.log(Level.INFO, "|" + changePreference.getPreferencePropertyKey() + "|" + changePreference.getName(context) + "|" + changePreference.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserIPSecurityPreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve IP Security Preference for user: " + user);
        IPSecurityPreference ipSecurityPreference = new IPSecurityPreference(context);
        logger.log(Level.INFO, "IP Security Preference: " + ipSecurityPreference);
        Preferences.IPSecurityPreference[] ipSecurityPreferences = Preferences.IPSecurityPreference.values();
        for (Preferences.IPSecurityPreference ipSecurityPref : ipSecurityPreferences) {
            logger.log(Level.INFO, "|" + ipSecurityPref.getPreferencePropertyKey() + "|" + ipSecurityPref.getName(context) + "|" + ipSecurityPref.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserIRMAttributePreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve IRM Attribute Preference for user: " + user);
        IRMAttributePreference irmAttributePreference = new IRMAttributePreference(context);
        logger.log(Level.INFO, "IRM Attribute Preference: " + irmAttributePreference);
        Preferences.IRMAttributePreference[] irmAttributePreferences = Preferences.IRMAttributePreference.values();
        for (Preferences.IRMAttributePreference attributePreference : irmAttributePreferences) {
            logger.log(Level.INFO, "|" + attributePreference.getPreferencePropertyKey() + "|" + attributePreference.getName(context) + "|" + attributePreference.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserIRMApprovalPreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve IRM Approval Preference for user: " + user);
        IRMApprovalPreference irmApprovalPreference = new IRMApprovalPreference(context);
        logger.log(Level.INFO, "IRM Approval Preference: " + irmApprovalPreference);
        Preferences.IRMApprovalPreference[] irmApprovalPreferences = Preferences.IRMApprovalPreference.values();
        for (Preferences.IRMApprovalPreference approvalPreference : irmApprovalPreferences) {
            logger.log(Level.INFO, "|" + approvalPreference.getPreferencePropertyKey() + "|" + approvalPreference.getName(context) + "|" + approvalPreference.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserDefaultCreatePreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve Default Create Preference for user: " + user);
        DefaultCreatePartTypePreference defaultCreatePartTypePreference = new DefaultCreatePartTypePreference(context);
        logger.log(Level.INFO, "Default Create Preference: " + defaultCreatePartTypePreference);
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws Exception
     */
    public void getUserProjectSpacePreference(Context context, String[] args) throws Exception {
        if (args == null || args.length < 1) {
            logger.warning("Exiting. Provide argument 1 (username)");
            return;
        }
        String user = args[0];
        logger.log(Level.INFO, "Connected user: " + context.getUser());
        ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
        logger.log(Level.INFO, "Retrieve Project Space Preference for user: " + user);
        IRMProjectSpacePreference irmProjectSpacePreference = new IRMProjectSpacePreference(context);
        logger.log(Level.INFO, "Project Space Preference: " + irmProjectSpacePreference);
        Preferences.IRMProjectSpacePreference[] irmProjectSpacePreferences = Preferences.IRMProjectSpacePreference.values();
        for (Preferences.IRMProjectSpacePreference projectSpacePreference : irmProjectSpacePreferences) {
            logger.log(Level.INFO, "|" + projectSpacePreference.getPreferencePropertyKey() + "|" + projectSpacePreference.getName(context) + "|" + projectSpacePreference.getID(context));
        }
        ContextUtil.popContext(context);
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void insertUserPlantDataPreference(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 4) {
                logger.warning("Exiting. Provide arguments (username) (objectId) (objectType) (identifier [ex:PRD or RM or PKG or TS])");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                String objectId = args[1];
                String categoryType = args[2];
                String category = args[3];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Insert Plant Data Preference for sample id: " + objectId);
                logger.log(Level.INFO, "Insert Plant Data Preference for user: " + user);
                PlantDataPreferenceUtil dataPreferenceUtil = new PlantDataPreferenceUtil();
                dataPreferenceUtil.createPlantEntries(context, new String[]{objectId, categoryType, category});
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during insert: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserPlantDataAllFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Plant Data All filter for user: " + user);
                IPlantDataPreferenceRepository repository = new PlantDataPreferenceRepository();
                MapList objectList = repository.getAllPlantData(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserPlantDataPackagingFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Plant Data Packaging filter for user: " + user);
                IPlantDataPreferenceRepository repository = new PlantDataPreferenceRepository();
                MapList objectList = repository.getPackagingPlantData(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserPlantDataProductFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Plant Data Product filter for user: " + user);
                IPlantDataPreferenceRepository repository = new PlantDataPreferenceRepository();
                MapList objectList = repository.getProductPlantData(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserPlantDataRawMaterialFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Plant Data Raw Material filter for user: " + user);
                IPlantDataPreferenceRepository repository = new PlantDataPreferenceRepository();
                MapList objectList = repository.getRawMaterialPlantData(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserPlantDataTechnicalSpecificationFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Plant Data Technical Specification filter for user: " + user);
                IPlantDataPreferenceRepository repository = new PlantDataPreferenceRepository();
                MapList objectList = repository.getTechnicalSpecificationPlantData(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserCopyDataTechnicalSpecificationFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Copy Data Technical Specification filter for user: " + user);
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                MapList objectList = repository.getTechnicalSpecificationFilterDataList(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserCopyDataPackagingFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Copy Data Packaging filter for user: " + user);
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                MapList objectList = repository.getPackagingFilterDataList(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserCopyDataProductFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Copy Data Product filter for user: " + user);
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                MapList objectList = repository.getProductFilterDataList(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserCopyDataRawMaterialFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Copy Data Raw Material filter for user: " + user);
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                MapList objectList = repository.getRawMaterialFilterDataList(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @param args
     * @throws MatrixException
     */
    public void retrieveUserCopyDataAllFilter(Context context, String[] args) throws MatrixException {
        try {
            if (args == null || args.length < 1) {
                logger.warning("Exiting. Provide argument 1 (username)");
                return;
            }
            if (context.isContextSet()) {
                String user = args[0];
                logger.log(Level.INFO, "Connected user: " + context.getUser());
                ContextUtil.pushContext(context, user, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                logger.log(Level.INFO, "Retrieve User Copy Data Raw Material filter for user: " + user);
                ICopyDataPreferenceRepository repository = new CopyDataPreferenceRepository();
                MapList objectList = repository.getAllFilterDataList(context);
                logger.log(Level.INFO, "Retrieved result: " + objectList);
                ContextUtil.popContext(context);

            }
        } catch (MatrixException e) {
            logger.warning("Exception occurred during retrieve: " + e);
            throw e;
        }
    }

    /**
     * @param context
     * @throws FrameworkException
     */
    public void retrieveActivePlantsJSON(Context context) throws FrameworkException {
        PlantUtil plantUtil = new PlantUtil();
        logger.log(Level.INFO, "Retrieved Active Plant(s) JSON: " + plantUtil.getActivePlantsJSON(context));
    }

    public void retrieveTechnicalSpecificationPreferences(Context context) throws Exception {
        TechnicalSpecificationPreference preference = new TechnicalSpecificationPreference(context);
        preference.getPartTypeJson();
    }

    public void retrieveDataForCreateUserPreferencePage(Context context, String[] args) throws Exception {
        Instant startTime = Instant.now();
        try {
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            PackagingPickList packagingPickList = new PackagingPickList(context);
            ProductPickList productPickList = new ProductPickList(context);
            RawMaterialPickList rawMaterialPickList = new RawMaterialPickList(context);
            CommonPickList commonPickList = new CommonPickList(context);

            JsonArray jsonPackagingTypes = packagingPickList.getPartTypes();
            JsonArray jsonMaterialTypes = packagingPickList.getMaterialTypes();
            JsonArray jsonComponentTypes = packagingPickList.getComponentTypes();
            JsonArray jsonUnitOfMeasures = packagingPickList.getUnitOfMeasures();
            JsonArray jsonPackagingReportedFunction = packagingPickList.getReportedFunction();

            JsonArray jsonProductTypes = productPickList.getPartTypes();
            JsonArray jsonComplianceRequired = productPickList.getComplianceRequired();

            JsonArray jsonRawMaterialTypes = rawMaterialPickList.getPartTypes();
            JsonArray jsonRawMaterialReportedFunction = rawMaterialPickList.getReportedFunction();
            JsonArray jsonMaterialFunction = rawMaterialPickList.getMaterialFunction();

            JsonArray jsonReleaseCriteria = commonPickList.getReleaseCriteria();
            JsonArray jsonClasses = commonPickList.getClassTypes();
            JsonArray jsonSegment = commonPickList.getSegment();
            JsonArray jsonBusinessArea = commonPickList.getBusinessArea();

            StringList partCategoryList = FrameworkUtil.split(" " + userPreferenceUtil.getPartCategory(context), ",");
            StringList partCategoryDisplayList = FrameworkUtil.split(" " + userPreferenceUtil.getPartCategoryDisplay(context), ",");

            //get context User
            String contextUserName = context.getUser();
            //String contextUserId = PersonUtil.getPersonObjectID(context);

            //String plantEnableTechnicalSpecificationType = userPreferenceUtil.getPlantEnableTechnicalSpecificationType(context);
        } catch (Exception e) {
            logger.warning("Exception occurred during insert: " + e);
        }
        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "Create User Preference Page - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
    }

    public void retrievePackagingPickLists(Context context, String... args) throws MatrixException {
        UserPreferenceUtil util = new UserPreferenceUtil();
        String packagingJson = util.getPackagingJson(context);
        logger.log(Level.INFO, String.format("Packaging -> %s", packagingJson));
    }

    public void retrieveProductPickLists(Context context, String... args) throws MatrixException {
        UserPreferenceUtil util = new UserPreferenceUtil();
        String productJson = util.getProductJson(context);
        logger.log(Level.INFO, String.format("Product -> %s", productJson));
    }

    public void retrieveRawMaterialPickLists(Context context, String... args) throws MatrixException {
        UserPreferenceUtil util = new UserPreferenceUtil();
        String rawMaterialJson = util.getRawMaterialJson(context);
        logger.log(Level.INFO, String.format("Raw Material -> %s", rawMaterialJson));
    }

    public void retrieveDSMUPTAttributeAsJson(Context context, String... args) throws Exception {
        if (null != args && args.length > 1) {
            String objectOid = args[0];
            String selects = args[1];
            StringList selectList = StringUtil.split(selects, ",");
            DSMUPTUtil util = new DSMUPTUtil();
            util.getAttributeInfoAsJsonString(context, objectOid, Arrays.asList(selectList.toStringArray()));
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument followed by command separated selectable as second argument");
        }
    }

    public void retrieveDSMUPTAttributeAsMap(Context context, String... args) throws Exception {
        if (null != args && args.length > 1) {
            String objectOid = args[0];
            String selects = args[1];
            StringList selectList = StringUtil.split(selects, ",");
            DSMUPTUtil util = new DSMUPTUtil();
            util.getAttributeInfoAsMap(context, objectOid, Arrays.asList(selectList.toStringArray()));
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument followed by command separated selectable as second argument");
        }
    }

    public void retrieveIRMUPTInfoJsonBySelects(Context context, String... args) throws Exception {
        if (null != args && args.length > 1) {
            String objectOid = args[0];
            String selects = args[1];
            StringList selectList = StringUtil.split(selects, ",");
            IRMUPTUtil util = new IRMUPTUtil();
            util.getAttributeInfoAsJsonString(context, objectOid, Arrays.asList(selectList.toStringArray()));
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument followed by command separated selectable as second argument");
        }
    }

    public void retrieveIRMUPTInfoMapBySelects(Context context, String... args) throws Exception {
        if (null != args && args.length > 1) {
            String objectOid = args[0];
            String selects = args[1];
            StringList selectList = StringUtil.split(selects, ",");
            IRMUPTUtil util = new IRMUPTUtil();
            util.getAttributeInfoAsMap(context, objectOid, Arrays.asList(selectList.toStringArray()));
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument followed by command separated selectable as second argument");
        }
    }

    public void retrieveIRMUPTInfoMap(Context context, String... args) throws Exception {
        if (null != args && args.length > 0) {
            String objectOid = args[0];
            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                IRMUPT irmupt = new IRMUPT(context, objectOid);
                Map<Object, Object> attributeMap = irmupt.getAttributeMap();
                logger.log(Level.INFO, attributeMap.toString());
            }

        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument as object ID");
        }
    }

    public void retrieveIRMUPTInfoJson(Context context, String... args) throws Exception {
        if (null != args && args.length > 0) {
            String objectOid = args[0];
            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                IRMUPT irmupt = new IRMUPT(context, objectOid);
                String attributeJson = irmupt.getAttributeJson();
                logger.log(Level.INFO, attributeJson);
            }
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument as object ID");
        }
    }

    public void retrieveDSMUPTInfoMap(Context context, String... args) throws Exception {
        if (null != args && args.length > 0) {
            String objectOid = args[0];
            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                DSMUPT dsmupt = new DSMUPT(context, objectOid);
                Map<Object, Object> attributeMap = dsmupt.getAttributeMap();
                logger.log(Level.INFO, attributeMap.toString());
            }

        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument as object ID");
        }
    }

    public void retrieveDSMUPTInfoJson(Context context, String... args) throws Exception {
        if (null != args && args.length > 0) {
            String objectOid = args[0];
            if (UIUtil.isNotNullAndNotEmpty(objectOid)) {
                DSMUPT dsmupt = new DSMUPT(context, objectOid);
                String attributeJson = dsmupt.getAttributeJson();
                logger.log(Level.INFO, attributeJson);
            }
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument as object ID");
        }
    }

    public void retrieveDSMUPTInfoJsonBySelects(Context context, String... args) throws Exception {
        if (null != args && args.length > 1) {
            String objectOid = args[0];
            String selects = args[1];
            StringList selectList = StringUtil.split(selects, ",");
            DSMUPTUtil util = new DSMUPTUtil();
            util.getAttributeInfoAsJsonString(context, objectOid, Arrays.asList(selectList.toStringArray()));
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument followed by command separated selectable as second argument");
        }
    }

    public void retrieveDSMUPTInfoMapBySelects(Context context, String... args) throws Exception {
        if (null != args && args.length > 1) {
            String objectOid = args[0];
            String selects = args[1];
            StringList selectList = StringUtil.split(selects, ",");
            DSMUPTUtil util = new DSMUPTUtil();
            util.getAttributeInfoAsMap(context, objectOid, Arrays.asList(selectList.toStringArray()));
        } else {
            logger.log(Level.WARNING, "Provide objectId as first argument followed by command separated selectable as second argument");
        }
    }

    public void propagateUPTAttributes(Context context, String... args) throws Exception {
        if (null != args && args.length > 0) {
            String partID = args[0];
            DomainObject partObject = DomainObject.newInstance(context, partID);
            String partType = partObject.getInfo(context, DomainConstants.SELECT_TYPE);
            UserPreferenceUtil userPreferenceUtil = new UserPreferenceUtil();
            userPreferenceUtil.propateUPTAttributesOnCreationPart(context, new String[]{partType, partID});
        }
    }

}
