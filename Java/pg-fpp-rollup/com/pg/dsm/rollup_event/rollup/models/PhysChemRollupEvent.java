package com.pg.dsm.rollup_event.rollup.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.common.config.phys_chem.PhysChemAttribute;
import com.pg.dsm.rollup_event.common.config.rule.Rollup;
import com.pg.dsm.rollup_event.common.ebom.ProductPart;
import com.pg.dsm.rollup_event.common.interfaces.RollupEvent;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.dsm.rollup_event.rollup.services.RollupParameter;
import com.pg.dsm.rollup_event.rollup.util.RollupAction;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class PhysChemRollupEvent implements RollupEvent {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;
    ProductPart masterPart;
    Resource resource;
    Rollup rollupConfig;
    RollupAction rollupAction;
    RollupParameter rollupParameter;
    List<PhysChemAttribute> physChemAttributes;

    public PhysChemRollupEvent(ProductPart productPart, Rollup rollupConfig, Resource resource) {
        this.masterPart = productPart;
        this.rollupConfig = rollupConfig;
        this.resource = resource;
        rollupParameter = resource.getRollupParameter();
        this.context = resource.getContext();
        this.physChemAttributes = resource.getPhysChemConfig().getPhysChemAttributes();
        rollupAction = new RollupAction(this.resource);
    }

    @Override
    public void execute() {

        String owningProductLineRelationshipName = RollupConstants.Relationship.OWNING_PRODUCT_LINE.getName(context);
        String physicalChemicalRollUpFlagAttributeSelect = RollupConstants.Attribute.PHYSICAL_CHEMICAL_ROLL_UP_FLAG.getSelect(context);
        StringList objSelects = new StringList(3);
        objSelects.addElement(physicalChemicalRollUpFlagAttributeSelect);
        objSelects.add("to[" + owningProductLineRelationshipName + "].from.id");
        objSelects.add("to[" + owningProductLineRelationshipName + "].id");

        Map<?, ?> dataMap;
        Map<String, String> appProductFormCustomMap = new HashMap<>();
        Map<String, String> fopProductFormCustomMap = new HashMap<>();
        String appPhysChemRollupFlagValue = DomainConstants.EMPTY_STRING;
        try {
            DomainObject appObj = DomainObject.newInstance(context);
            if (UIUtil.isNotNullAndNotEmpty(masterPart.getId())) {

                appObj.setId(masterPart.getId());
                dataMap = appObj.getInfo(context, objSelects);
                appProductFormCustomMap = rollupAction.getFormulationPartProductFormCustomMap(dataMap, owningProductLineRelationshipName);
                appPhysChemRollupFlagValue = (String) dataMap.get(physicalChemicalRollUpFlagAttributeSelect);
            }
            /*
             * In case of manual rollup, Rollup initiator may not having access to modify all the Phy-Chem attributes.
             * Hence pushing the context to User Agent avoid issue in attribute update.
             */
            if (masterPart.isChildExist()) {
                List<ProductPart> children = masterPart.getChildren();
                int size = children.size();

                if (size > 1) {
                    if (pgV3Constants.KEY_TRUE.equalsIgnoreCase(appPhysChemRollupFlagValue)) {
                        // when multiple formulation parts are connected to APP.
                        doWhenMultiFormulationPartConnected(appObj, appPhysChemRollupFlagValue);
                        // disconnect ProductForms on APP
                        boolean bIsProductFormDisconnected = rollupAction.disconnectProductForms(appProductFormCustomMap, fopProductFormCustomMap);
                    }
                } else {
                    if (size > 0) {

                        String fopOID = children.get(0).getId();
                        String fopState = children.get(0).getCurrentState();
                        //Added Locked State check for Req #39800 in 2018x.6 July CW
                        if (UIUtil.isNotNullAndNotEmpty(fopOID) && UIUtil.isNotNullAndNotEmpty(fopState)
                                && (pgV3Constants.STATE_RELEASE.equalsIgnoreCase(fopState) || pgV3Constants.STATE_LOCKED.equalsIgnoreCase(fopState))) {
                            // when only one formulation part is connected to APP.
                            doWhenOneFormulationPartConnected(
                                    appObj, // app domain object
                                    fopOID, // fop object id.
                                    owningProductLineRelationshipName, // owning product rel name
                                    objSelects, // object selects.
                                    appProductFormCustomMap // custom map
                            );
                        } else {
                            if (pgV3Constants.KEY_TRUE.equalsIgnoreCase(appPhysChemRollupFlagValue)) {
                                doWhenMultiFormulationPartConnected(appObj, appPhysChemRollupFlagValue);
                                // disconnect product forms from APP which are not connected to FOP.
                                boolean bIsProductFormDisconnected = rollupAction.disconnectProductForms(appProductFormCustomMap, fopProductFormCustomMap);
                            }
                        }
                    }
                }
            } else {
                if (pgV3Constants.KEY_TRUE.equalsIgnoreCase(appPhysChemRollupFlagValue)) {
                    doWhenMultiFormulationPartConnected(appObj, appPhysChemRollupFlagValue);
                    // disconnect product forms from APP which are not connected to FOP.
                    boolean bIsProductFormDisconnected = rollupAction.disconnectProductForms(appProductFormCustomMap, fopProductFormCustomMap);
                }
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }
    }

    private void doWhenMultiFormulationPartConnected(DomainObject appObj, String appPhysChemRollupFlagValue) {

        // if the phys-chem roll-up flag is true.
        if (Boolean.parseBoolean(appPhysChemRollupFlagValue)) {

            // reset phys-chem attribute to default.
            boolean bIsPhysChemDefaultValuesReset = rollupAction.resetDefaultPhysChemValues(appObj, physChemAttributes);

            // if reset phys-chem reset was successful.
            if (bIsPhysChemDefaultValuesReset) {

                // change roll-up flag on APP to false.
                rollupAction.resetPhysChemRollupFlag(appObj, pgV3Constants.KEY_FALSE);

            }
        }

    }

    private void doWhenOneFormulationPartConnected(DomainObject appObj, String fopOID, String owningProductLineRelationshipName, StringList objSelects, Map<String, String> appProductFormCustomMap) {

        try {

            DomainObject fopObj = DomainObject.newInstance(context);
            fopObj.setId(fopOID);

            // get formulation part - phys-chem attribute values from database.
            Map<?, ?> formulationPartInfoMap = rollupAction.getFormulationPartPhysChemInfo(fopObj, physChemAttributes, objSelects);

            // build a custom map storing product form object id and connection rel id.
            Map<String, String> fopProductFormCustomMap = rollupAction.getFormulationPartProductFormCustomMap(formulationPartInfoMap, owningProductLineRelationshipName);

            // if formulation part has connected product form(s).
            if (!fopProductFormCustomMap.isEmpty()) {

                // check if formulation part has manually entered phys-chem data.
                boolean bIsFormulationPartHasManualPhysChemData = rollupAction.isFormulationPartPhysChemDataValid(formulationPartInfoMap, physChemAttributes);

                // if formulation part has manual phys-chem data.
                if (bIsFormulationPartHasManualPhysChemData) {

                    // roll-up phys-chem attribute data to APP level.
                    boolean bIsPhysChemAttributeDataRolledUp = rollupAction.rollupFormulationPartPhysChemData(appObj, formulationPartInfoMap, physChemAttributes);


                    // if phys-chem attribute data is successfully rolled-up.
                    if (bIsPhysChemAttributeDataRolledUp) {

                        // disconnect product forms from APP which are not connected to FOP.
                        boolean bIsProductFormDisconnected = rollupAction.disconnectProductForms(appProductFormCustomMap, fopProductFormCustomMap);

                        // connect FOP's product forms to APP.
                        boolean bIsProductFormConnected = rollupAction.connectProductForms(appObj, owningProductLineRelationshipName, appProductFormCustomMap, fopProductFormCustomMap);

                        // if connect/disconnect product forms are successful.
                        if (bIsProductFormDisconnected && bIsProductFormConnected) {

                            // change roll-up flag on APP to true.
                            rollupAction.resetPhysChemRollupFlag(appObj, pgV3Constants.KEY_TRUE);

                        }
                    }

                }
            }

        } catch (FrameworkException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        }

    }

    @Override
    public void processProductParts(ProductPart masterPart) {
    }

    @Override
    public void performRollupConnections(ProductPart masterPart, Map<String, String> mpProductMap) throws FrameworkException {
    }
}

