package com.pg.dsm.rollup_event.common.ebom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class ChildExpansion {
    boolean loaded;
    ProductPart productPart;

    private ChildExpansion(Builder builder) {
        this.loaded = builder.loaded;
        this.productPart = builder.productPart;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public ProductPart getProductPart() {
        return productPart;
    }

    public static class Builder {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        Context context;
        String productPartOID;
        ProductPart productPart;
        ProductPart markedProductPart;
        String assemblyType;
        String expansionType;
        String expansionRelationship;
        short expandLevel;
        StringList objectSelectList;
        StringList relationshipSelectList;
        String objectWhereClause;
        String relationshipWhereClause;
        boolean loaded;

        public Builder(Context context, String productPartOID, String assemblyType) {
            this.context = context;
            this.productPartOID = productPartOID;
            this.assemblyType = assemblyType;
        }

        public ChildExpansion build() {
            try {
                DomainObject domainObject = DomainObject.newInstance(context, productPartOID);
                if (null == markedProductPart) {
                    productPart = new ProductPart(context, domainObject.getInfo(context, getBusinessObjectSelects()), this.assemblyType);
                } else {
                    productPart = markedProductPart.copy();
                }

                // expand with EBOM to get children recursively.
                MapList childList = expand(domainObject);

                // below are temp variables
                ProductPart currentProductPart;
                ProductPart parentProductPart;
                int level;
                List<ProductPart> tempParentStorageList = new ArrayList<>();

                List<Substitute> substitutes;
                for (Object obj : childList) {
                    currentProductPart = new ProductPart(context, (Map<?, ?>) obj, this.assemblyType);
                    level = Integer.parseInt(currentProductPart.getLevel());
                    substitutes = getSubstitutes((Map<?, ?>) obj);
                    if (!substitutes.isEmpty()) {
                        currentProductPart.setSubstituteExist(true);
                        currentProductPart.setSubstitutes(substitutes);
                    }
                    if (level == 1) {
                        tempParentStorageList.clear();
                        tempParentStorageList.add(productPart);
                        currentProductPart.setParent(productPart);
                        currentProductPart.setParentExist(Boolean.TRUE);
                        productPart.addChild(currentProductPart);
                        productPart.setChildExist(Boolean.TRUE);
                    } else {
                        //For analyzing one of the critical data added try/catch block
                        try {
                            parentProductPart = tempParentStorageList.get(level - 1);
                            currentProductPart.setParent(parentProductPart);
                            currentProductPart.setParentExist(Boolean.TRUE);
                            parentProductPart.addChild(currentProductPart);
                            parentProductPart.setChildExist(Boolean.TRUE);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
                            logger.log(Level.INFO, "Level at execption occured: {0}", level);
                            logger.log(Level.INFO, "Object Name at execption occured: {0}", currentProductPart.getName());
                            logger.log(Level.INFO, "Object id at execption occured: {0}", currentProductPart.getId());
                            logger.log(Level.INFO, "children data: {0}", childList);
                        }
                    }
                    tempParentStorageList.add(level, currentProductPart);
                }
                tempParentStorageList.clear();
            } catch (Exception e) {
                this.loaded = false;
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);

            }
            this.loaded = true;
            return new ChildExpansion(this);
        }

        public Builder setExpansionType(String expansionType) {
            this.expansionType = expansionType;
            return this;
        }

        public Builder setExpansionRelationship(String expansionRelationship) {
            this.expansionRelationship = expansionRelationship;
            return this;
        }

        public Builder setExpandLevel(short expandLevel) {
            this.expandLevel = expandLevel;
            return this;
        }

        public StringList getObjectSelectList() {
            return objectSelectList;
        }

        public Builder setObjectSelectList(StringList objectSelectList) {
            this.objectSelectList = objectSelectList;
            return this;
        }

        public StringList getRelationshipSelectList() {
            return relationshipSelectList;
        }

        public Builder setRelationshipSelectList(StringList relationshipSelectList) {
            this.relationshipSelectList = relationshipSelectList;
            return this;
        }

        public Builder setRelationshipWhereClause(String relationshipWhereClause) {
            this.relationshipWhereClause = relationshipWhereClause;
            return this;
        }

        public Builder setMarkedProductPart(ProductPart markedProductPart) {
            this.markedProductPart = markedProductPart;
            return this;
        }

        private StringList getBusinessObjectSelects() {
            StringList businessObjectSelects = new StringList(18);
            businessObjectSelects.addElement(DomainConstants.SELECT_ID);
            businessObjectSelects.addElement(pgV3Constants.PHYSICALID);
            businessObjectSelects.addElement(DomainConstants.SELECT_TYPE);
            businessObjectSelects.addElement(DomainConstants.SELECT_NAME);
            businessObjectSelects.addElement(DomainConstants.SELECT_REVISION);
            businessObjectSelects.addElement(DomainConstants.SELECT_CURRENT);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTPARTPHYSICALID);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGISTHEPRODUCTABATTERY);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGPOWERSOURCE);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDINSIDEDEVICE);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBATTERIESSHIPPEDOUTSIDEDEVICE);
            businessObjectSelects.addElement(RollupConstants.Attribute.NUMBER_OF_BATTERIES_REQUIRED.getSelect(context));
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDOESDEVICECONTAINFLAMMABLELIQUID);
            businessObjectSelects.addElement(RollupConstants.Attribute.SET_PRODUCT_NAME.getSelect(context));
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
            //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Starts
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
            //Added by DSM(Sogeti) for 2018x.6 Defect #40528 - Ends
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMARKFORROLLUP);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGEVENTFORROLLUP);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGSMARTLABELREADY);
            businessObjectSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGDANGEROUSGOODSREADY);
            //Modified for Defect # 44589 - Starts
            businessObjectSelects.addElement(RollupConstants.Attribute.CALCULATE_FOR_ROLLUP.getSelect(context));
            businessObjectSelects.addElement(RollupConstants.Attribute.MARKET_REGISTRATION_ROLLUP_FLAG.getSelect(context));
            //Modified for Defect # 44589 - Ends
            return businessObjectSelects;
        }

        private StringList getRelationshipSelects() {
            StringList relationshipSelects = new StringList();
            relationshipSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
            relationshipSelects.addElement(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);

            relationshipSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
            relationshipSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
            relationshipSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
            relationshipSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PHYSICALID);
            relationshipSelects.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_ID);
            relationshipSelects.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_PHYSICALID);
            relationshipSelects.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_TYPE);
            relationshipSelects.add(pgV3Constants.SELECT_FBOM_SUBSTITUTE_REL_ID);
            return relationshipSelects;
        }

        private MapList expand(DomainObject domainObject) throws FrameworkException {

            return domainObject.getRelatedObjects(context,
                    expansionRelationship, // relationship pattern
                    expansionType, // Type pattern
                    false, // to side
                    true, // from side
                    expandLevel, // recursion level
                    (null != objectSelectList) ? objectSelectList : getBusinessObjectSelects(), // object selects
                    (null != relationshipSelectList) ? relationshipSelectList : getRelationshipSelects(), // rel selects
                    (null != objectWhereClause) ? objectWhereClause : getObjectWhereClause(), // object where clause
                    DomainConstants.EMPTY_STRING, // relWhereClause
                    0, //limit
                    null, // postRelPattern,
                    null, // PostPattern
                    null);// Map Post Pattern

        }

        private String getObjectWhereClause() {
            return new StringBuilder().append(DomainConstants.SELECT_CURRENT).
                    append(RollupConstants.Basic.SYMBOL_NOT_EQUALS.getValue()).append(pgV3Constants.STATE_OBSOLETE).toString();
        }

        public Builder setObjectWhereClause(String objectWhereClause) {
            this.objectWhereClause = objectWhereClause;
            return this;
        }

        private List<Substitute> getSubstitutes(Map<?, ?> busMap) {
            List<Substitute> parts = new ArrayList<>();
            if (busMap.containsKey(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID) || busMap.containsKey(pgV3Constants.SELECT_FBOM_SUBSTITUTE_REL_ID)) {
                StringList substituteIDs = getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
                StringList substituteTypes = getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
                StringList substituteRelationshipIDs = getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
                StringList substitutePhysicalIDs = getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_PHYSICALID);
                substituteIDs.addAll(getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_FBOM_SUBSTITUTE_ID));
                substituteTypes.addAll(getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_FBOM_SUBSTITUTE_TYPE));
                substituteRelationshipIDs.addAll(getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_FBOM_SUBSTITUTE_REL_ID));
                substitutePhysicalIDs.addAll(getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_FBOM_SUBSTITUTE_PHYSICALID));
                Map<String, String> beanMap;
                for (int i = 0; i < substituteIDs.size(); i++) {
                    beanMap = new HashMap<>();
                    beanMap.put(DomainConstants.SELECT_ID, substituteIDs.get(i));
                    beanMap.put(DomainConstants.SELECT_TYPE, substituteTypes.get(i));
                    beanMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, substituteRelationshipIDs.get(i));
                    beanMap.put(RollupConstants.Basic.PHYSICAL_ID.getValue(), substitutePhysicalIDs.get(i));
                    parts.add(new Substitute(beanMap));
                }
            }
            return parts;
        }

        /**
         * This method used to get EBOM Substitute id
         *
         * @param - Map dataMap
         * @param - String selectable
         * @return - StringList
         **/
        private StringList getEBOMSubstituteFromMap(Map<?, ?> dataMap, String selectable) {
            Object substituteId = (dataMap).get(selectable);
            StringList stringList = new StringList();
            if (null != substituteId) {
                if (substituteId instanceof StringList) {
                    stringList = (StringList) substituteId;
                } else {
                    stringList.add(substituteId.toString());
                }
            }
            return stringList;
        }
    }
}
