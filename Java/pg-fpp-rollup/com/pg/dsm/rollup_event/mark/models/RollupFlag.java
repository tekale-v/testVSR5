package com.pg.dsm.rollup_event.mark.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.util.RollupCommonUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.mark.util.MarkAction;
import com.pg.dsm.rollup_event.mark.util.MarkUtil;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class RollupFlag {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    ProductPart markedProductPart;
    Resource resource;
    Properties rollupPageProperties;
    Context context;
    MarkAction markAction;

    public RollupFlag(ProductPart markedProductPart, Resource resource, MarkAction markAction) {
        this.markedProductPart = markedProductPart;
        this.resource = resource;
        this.rollupPageProperties = resource.getRollupPageProperties();
        this.context = resource.getContext();
        this.markAction = markAction;
    }

    /**
     * This method generates where used FPP's map for marked intermediate objects
     *
     * @return FPP eventAttributeMap
     */
    public Map<String, Map<String, String>> getRollupEventAttributeMap() {

        Map<String, Map<String, String>> eventAttributeMap = new HashMap<>();

        try {
            String rollupType = pgV3Constants.INTERMEDIATE;
            Rollup rollupConfig = RollupUtil.getRollup(rollupType, resource.getRollupRuleConfiguration());


            if (null != markedProductPart) {

                String markedProductPartType = markedProductPart.getType();
                String markedProductPartId = markedProductPart.getId();
                String allowedSubstituteProductPartTypes = resource.getRollupRuleConfiguration().getAllowedSubstituteTypes();
                StringList substituteProductPartOidList = new StringList();
                substituteProductPartOidList.addElement(markedProductPartId);
                StringList productOidList = new StringList();
                productOidList.addElement(markedProductPartId);
                if (allowedSubstituteProductPartTypes.contains(markedProductPartType)) {
                    StringList intermediatesFromSubstitutesOidList = markedProductPart.geteBOMSubstituteFromID();
                    StringList intermediatesFromSubstitutesTypeList = markedProductPart.geteBOMSubstituteFromType();
                    StringList alternatesIdsList = markedProductPart.getAlternateFromID();
                    int size = intermediatesFromSubstitutesTypeList.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            if (allowedSubstituteProductPartTypes.contains(intermediatesFromSubstitutesTypeList.get(i))) {
                                substituteProductPartOidList.addElement(intermediatesFromSubstitutesOidList.get(i));
                            }
                        }
                    }
                    // Gets where used primary parts from parent alternate's
                    StringList productOidListFromAlternate = markAction.getPrimaryIntermediateFromAlternate(rollupConfig, new StringList(markedProductPartId));

                    substituteProductPartOidList.addAll(alternatesIdsList);
                    substituteProductPartOidList.addAll(productOidListFromAlternate);

                    productOidList.addAll(productOidListFromAlternate);
                    productOidList.addAll(alternatesIdsList);
                    productOidListFromAlternate.addAll(alternatesIdsList);
                    productOidList.addAll(MarkUtil.filterSubstitute(MarkUtil.getSubstitutesInfo(context, productOidListFromAlternate)));

                }

                // Gets where used parent FPP's from parent Substitute's
                eventAttributeMap.putAll(markAction.getFinishedProductPartRollupEventAttributeMapFromSubstitute(rollupConfig, RollupCommonUtil.removeDuplicates(productOidList)));

                // Gets where used parent FPP's from children intermediate's
                eventAttributeMap.putAll(markAction.getFinishedProductPartRollupEventAttributeMap(rollupConfig, RollupCommonUtil.removeDuplicates(substituteProductPartOidList)));

            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
        return eventAttributeMap;
    }
}
