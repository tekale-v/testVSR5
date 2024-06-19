package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.config.rule.Rule;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.common.interfaces.RollupRule;
import com.pg.dsm.rollup_event.common.interfaces.RollupRuleFactory;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.util.RollupAction;
import com.pg.dsm.rollup_event.rollup.util.RollupUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.RelationshipType;
import matrix.util.StringList;


public class MaterialCertificationRollupEvent implements RollupEvent {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    Context context;
    ProductPart masterPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;
    Properties rollupPageProperties;

    public MaterialCertificationRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
        this.masterPart = productPart;
        this.rollupConfig = rollupConfig;
        this.resource = resource;
        this.context = resource.getContext();
        rollupAction = new RollupAction(this.resource);
        rollupPageProperties = resource.getRollupPageProperties();
    }

    public static StringList getMaterialCertificationRelSelects() {

        StringList slRelationshipSelects = new StringList(5);
        slRelationshipSelects.add(pgV3Constants.SELECT_CERTIFICATION_COUNTRYID);
        slRelationshipSelects.add(pgV3Constants.SELECT_CERTIFICATION_AREAID);
        slRelationshipSelects.add(pgV3Constants.SELECT_CERTIFICATION_REGIONID);
        slRelationshipSelects.add(pgV3Constants.SELECT_CERTIFICATION_GROUPID);
        slRelationshipSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);

        return slRelationshipSelects;
    }

    @Override
    public void execute() {
        if (masterPart != null) {
            try {
                boolean isModified = rollupAction.disconnectExistingRollupData(masterPart, rollupConfig);
                processProductParts(masterPart);
                if (isModified) {
                    resource.getDynamicSubscription().add(rollupConfig);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
        }
    }

    @Override
    public void processProductParts(ProductPart productPart) {
        try {
            if (productPart.isChildExist()) {
                List<ProductPart> children = productPart.getChildren();
                Iterator<ProductPart> childrenItr = children.iterator();
                ProductPart childrenProduct;
                boolean bChildValidation;
                boolean bMaterialCertificationPartCheck;
                Map mpProductMap = new HashMap();
                while (childrenItr.hasNext()) {
                    childrenProduct = childrenItr.next();

                    RollupRule rollupRule = RollupRuleFactory.getRollupRule(this.context, childrenProduct, null, rollupConfig); // Modified by DSM (Sogeti) for 22x.02 (May CW) - REQ 46276
                    bChildValidation = rollupRule.isChildrenAllowed();

                    if (bChildValidation) {
                        bMaterialCertificationPartCheck = performMaterialCertificationPartCheck(childrenProduct);

                        rollupAction.processAlternates(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processProducedBy(masterPart, childrenProduct, rollupConfig);
                        rollupAction.processSubstitutes(masterPart, childrenProduct, rollupConfig);

                        if (bMaterialCertificationPartCheck) {
                            mpProductMap.put(pgV3Constants.PHYSICALID, childrenProduct.getPhysicalId());
                            performRollupConnections(masterPart, mpProductMap);
                        }
                        processProductParts(childrenProduct);
                    }
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    public boolean performMaterialCertificationPartCheck(ProductPart productpart) {
        boolean iValidation = false;
        try {
            String sProductType = productpart.getType();
            Rule rollupRuleChildProductParts = RollupUtil.getRollupRule(rollupConfig, RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());
            String sProductParts = rollupRuleChildProductParts.getInclusionType();
            iValidation = (UIUtil.isNotNullAndNotEmpty(sProductParts) && sProductParts.contains(sProductType));
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

        return iValidation;
    }

    @Override
    public void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap)
            throws FrameworkException {
        try {
            if (masterPart != null && !mpProductMap.isEmpty()) {
                DomainObject domMaster = DomainObject.newInstance(context, masterPart.getId());
                String sPhysicalId = mpProductMap.get(pgV3Constants.PHYSICALID);

                if (!resource.getSlProcessedObjectsList().contains(sPhysicalId)) {

                    Map<String, StringList> mpCertificationMap;
                    DomainObject domProductPart = DomainObject.newInstance(context, sPhysicalId);

                    Rule childProductPartRule = RollupUtil.getRollupRule(rollupConfig,
                            RollupConstants.Basic.IDENTIFIER_PRODUCTPART_RULE.getValue());

                    MapList mlMaterialCertificationsData = rollupAction.getConnectedRollUpData(domProductPart, childProductPartRule.getToType(), childProductPartRule.getRelationshipName(), null, RollupUtil.getObjectSelects(), getMaterialCertificationRelSelects(), resource.getExecutionType());

                    if (mlMaterialCertificationsData != null && !mlMaterialCertificationsData.isEmpty()) {

                        /*
                         * In case of manual rollup, Rollup initiator may not have access to all the
                         * data which needs to be rolledup. So pushing context to User Agent to avoid
                         * data missing in rollup
                         */
                        Rule rollupRelationshipRule = RollupUtil.getRollupRule(rollupConfig,
                                RollupConstants.Basic.IDENTIFIER_FINISHED_PRODUCTPART_RULE.getValue());
                        RelationshipType rFPPtoRollUpRel = new RelationshipType(rollupRelationshipRule.getRelationshipName());
                        Map mapMaterialCertifiationsData;
                        String strMaterialCertificationId;
                        StringList slCountriesList;
                        StringList slAreasList;
                        StringList slRegionsList;
                        StringList slGroupsList;
                        Map mRelConnectedProduct;
                        String sConnectionId;
                        Iterator itr;
                        Map.Entry mapRelId;
                        for (int i = 0; i < mlMaterialCertificationsData.size(); i++) {
                            mpCertificationMap = new HashMap<>();
                            mapMaterialCertifiationsData = (Map) mlMaterialCertificationsData.get(i);
                            strMaterialCertificationId = (String) mapMaterialCertifiationsData.get(DomainConstants.SELECT_ID);
                            slCountriesList = rollupAction.getStringListFromMap(mapMaterialCertifiationsData, pgV3Constants.SELECT_CERTIFICATION_COUNTRYID);
                            slAreasList = rollupAction.getStringListFromMap(mapMaterialCertifiationsData, pgV3Constants.SELECT_CERTIFICATION_AREAID);
                            slRegionsList = rollupAction.getStringListFromMap(mapMaterialCertifiationsData, pgV3Constants.SELECT_CERTIFICATION_REGIONID);
                            slGroupsList = rollupAction.getStringListFromMap(mapMaterialCertifiationsData, pgV3Constants.SELECT_CERTIFICATION_GROUPID);

                            mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGCOUNTRIESCERTIED, slCountriesList);
                            mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGPLIAREACERTIFIED, slAreasList);
                            mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGPLIREGIONCERTIFIED, slRegionsList);
                            mpCertificationMap.put(pgV3Constants.RELATIONSHIP_PGPLIGROUPCERTIFIED, slGroupsList);

                            mRelConnectedProduct = DomainRelationship.connect(context, domMaster, rFPPtoRollUpRel, true,
                                    new String[]{strMaterialCertificationId}, true);

                            resource.getDynamicSubscription().add(rollupConfig);

                            itr = mRelConnectedProduct.entrySet().iterator();
                            while (itr.hasNext()) {
                                mapRelId = (Map.Entry) itr.next();
                                sConnectionId = mapRelId.getValue().toString();
                                DomainRelationship.setAttributeValue(context, sConnectionId, pgV3Constants.ATTRIBUTE_PGPRODUCTPARTPHYSICALID, sPhysicalId);
                                processMaterialCertificationConnections(mpCertificationMap, sConnectionId);
                            }
                        }
                    }
                    resource.getSlProcessedObjectsList().add(sPhysicalId);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    public void processMaterialCertificationConnections(Map<String, StringList> mCertificationConnections, String strConnectionId) throws FrameworkException {

        try {
            if (!mCertificationConnections.isEmpty()) {
                StringList slConnectionsList;
                String sRelName;
                Iterator itrConnections;
                String sObjectId;
                DomainObject domObject = DomainObject.newInstance(context);

                String sCommandStatement = "add connection \"$1\" from \"$2\" torel \"$3\"";

                for (Map.Entry<String, StringList> entry : mCertificationConnections.entrySet()) {
                    sRelName = entry.getKey();
                    slConnectionsList = entry.getValue();
                    itrConnections = slConnectionsList.iterator();
                    while (itrConnections.hasNext()) {
                        sObjectId = (String) itrConnections.next();
                        domObject.setId(sObjectId);

                        // below code established mid-rel connection. using MQL command as there is no corresponding API.
                        if (UIUtil.isNotNullAndNotEmpty(sObjectId) && UIUtil.isNotNullAndNotEmpty(strConnectionId)) {
                            MqlUtil.mqlCommand(context, sCommandStatement, sRelName, sObjectId, strConnectionId);
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }
}
