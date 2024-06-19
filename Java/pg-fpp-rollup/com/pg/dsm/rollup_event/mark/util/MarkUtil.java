package com.pg.dsm.rollup_event.mark.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.rollup_event.common.circular.BOM;
import com.pg.dsm.rollup_event.common.config.rule.Config;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.SelectConstants;
import matrix.util.StringList;

public class MarkUtil {
    private static final Logger logger = Logger.getLogger(MarkUtil.class.getName());

    private MarkUtil() {
    }

    public static StringList getStringListFromMap(Map<?, ?> objMap, String identifier) {
        StringList objectList = new StringList();
        try {
            Object objSubstitutes = objMap.get(identifier);
            if (null != objSubstitutes) {
                if (objSubstitutes instanceof StringList) {
                    objectList = (StringList) objSubstitutes;
                } else if (objSubstitutes.toString().contains(SelectConstants.cSelectDelimiter)) {
                    objectList = StringUtil.splitString(objSubstitutes.toString(), SelectConstants.cSelectDelimiter);
                } else {
                    objectList.add(objSubstitutes.toString());
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return objectList;
    }

    public static Map<String, String> getUnflagAttributeMap(ProductPart productPart) {
        Map<String, String> attributeMap = new HashMap<>();
        try {
            StringList markedEventAttributeNameList = productPart.getMarkedEventAttributeNameList();
            for (String attributeName : markedEventAttributeNameList) {
                attributeMap.put(attributeName, pgV3Constants.KEY_FALSE);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return attributeMap;
    }

    public static boolean isRegistrationParentType(ProductPart productPart) {
        String sProductType = productPart.getType();
        return pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sProductType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sProductType)
                || pgV3Constants.TYPE_PGPACKINGSUBASSEMBLY.equalsIgnoreCase(sProductType) || pgV3Constants.TYPE_PGFINISHEDPRODUCT.equalsIgnoreCase(sProductType);
    }

    public static MapList getSubstitutesInfo(Context context, StringList producedByOidList) {
        MapList mlSubstituteInfo = new MapList();
        try {
            mlSubstituteInfo = DomainObject.getInfo(context, producedByOidList.toArray(new String[producedByOidList.size()]), new StringList(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID));
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mlSubstituteInfo;
    }

    public static StringList filterSubstitute(MapList objectList) {
        StringList producedByOidList = new StringList();
        try {
            if (!objectList.isEmpty()) {
                for (Object obj : objectList) {
                    producedByOidList.addAll(MarkUtil.getStringListFromMap((Map<?, ?>) obj, pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return producedByOidList;
    }

    /**
     * This method returns True if object is having circular reference
     *
     * @param context  - Context
     * @param objectId - Object Id
     * @param resource - Rollup resources
     * @return Boolean - True/False
     * Added by DSM (Sogeti) for defect# 43716
     */
    public static boolean isCircularExists(Context context, String objectId, Resource resource) {
        boolean isCircularExists = false;
        try {
            Config rollupRuleConfiguration = resource.getRollupRuleConfiguration();
            DomainObject domObject = DomainObject.newInstance(context, objectId);
            boolean hasMarketConnected = false;

            if (Boolean.parseBoolean(rollupRuleConfiguration.getPerformMarketCheck())) {
                hasMarketConnected = domObject.hasRelatedObjects(context, pgV3Constants.RELATIONSHIP_PGCOUNTRYOFSALE, true);
            }
            //Modified for Defect# 44220 - Starts
            if (resource.getPerformCircularCheck() && !hasMarketConnected) {
                //Modified for Defect# 44220 - Ends
                BOM bomObj = new BOM.Structure(context, objectId, resource).checkCircular().getInstance();
                isCircularExists = bomObj.isCircularExist();
                if (isCircularExists) {
                    logger.log(Level.INFO, "Object Id {0} circularExist  >>>>>>>>>>>>: {1}", new Object[]{objectId, isCircularExists});
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return isCircularExists;
    }

    /**
     * This method resets the FPP rollup details
     *
     * @param context
     * @param objectFPP
     * @throws FrameworkException Added by DSM (Sogeti) for defect# 43716
     */
    public static void resetFPPMap(Context context, DomainObject objectFPP) throws FrameworkException {
        try {
            Map<String, String> mpFPPResetMap = new HashMap<>();
            mpFPPResetMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
            mpFPPResetMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, DomainConstants.EMPTY_STRING);
            //Modified by DSM (Sogeti) for defect# 44589 - Starts
            mpFPPResetMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_FALSE);
            //Modified by DSM (Sogeti) for defect# 44589 - Ends
            objectFPP.setAttributeValues(context, mpFPPResetMap);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    /**
     * This method returns true if product part is of DSM parent type
     *
     * @param productPart
     * @return boolean True/False
     * Added by DSM (Sogeti) for defect# 43716
     */
    public static boolean isRegistrationDSMParentType(ProductPart productPart) {
        String sProductType = productPart.getType();
        return pgV3Constants.TYPE_PACKAGINGASSEMBLYPART.equalsIgnoreCase(sProductType) || pgV3Constants.TYPE_FABRICATEDPART.equalsIgnoreCase(sProductType)
                || pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(sProductType);
    }

    /**
     * This method resets the rollup attributes
     *
     * @param context
     * @param productPart bean
     * @throws FrameworkException Added by DSM (Sogeti) for defect# 43716
     */
    public static void resetMarking(Context context, ProductPart productPart) throws FrameworkException {
        try {
            DomainObject domObj = DomainObject.newInstance(context, productPart.getId());
            Map<String, String> mpResetMap = new HashMap<>();
            if (pgV3Constants.TYPE_FINISHEDPRODUCTPART.equalsIgnoreCase(productPart.getType())) {
                mpResetMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
                mpResetMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, DomainConstants.EMPTY_STRING);
                //Modified by DSM (Sogeti) for defect# 44589 - Starts
                mpResetMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_FALSE);
                //Modified by DSM (Sogeti) for defect# 44589 - Ends
            } else {
                mpResetMap = MarkUtil.getUnflagAttributeMap(productPart);
            }
            domObj.setAttributeValues(context, mpResetMap);

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }


    /**
     * This method return FPP Marking Map
     *
     * @param context
     * @param slFPPs  holds Processed FPP's
     * @return mpFPPMarkingMap
     * Added for Rollup Defect# 44589
     */
    public static Map<String, Map<String, String>> getFPPMarkingMap(Context context, StringList slFPPs) {
        Map<String, Map<String, String>> mpFPPMarkingMap = new HashMap();
        try {

            DomainObject domFPP = DomainObject.newInstance(context);
            Map<String, String> mpFPPAttributeMap;
            Map<String, String> mpFPPMap;
            boolean bMarkForRollup;
            String sEventforRollup;
            StringList slSelectables = new StringList(2);
            slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP);
            slSelectables.add(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
            for (String sFPPId : slFPPs) {
                domFPP.setId(sFPPId);
                mpFPPAttributeMap = new HashMap();
                mpFPPMap = domFPP.getInfo(context, slSelectables);
                bMarkForRollup = mpFPPMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP) == null ? false : Boolean.parseBoolean((String) mpFPPMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP));

                sEventforRollup = mpFPPMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP) == null ? DomainConstants.EMPTY_STRING : (String) mpFPPMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);

                //Skipping the FPP marking if its already processed
                if (bMarkForRollup) {
                    mpFPPAttributeMap.put(pgV3Constants.ATTRIBUTE_PGMARKFORROLLUP, pgV3Constants.KEY_FALSE);
                    mpFPPAttributeMap.put(pgV3Constants.ATTRIBUTE_PGEVENTFORROLLUP, sEventforRollup);
                    mpFPPAttributeMap.put(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getName(context), pgV3Constants.KEY_TRUE);
                    mpFPPMarkingMap.put(sFPPId, mpFPPAttributeMap);
                }
            }

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return mpFPPMarkingMap;
    }

}
