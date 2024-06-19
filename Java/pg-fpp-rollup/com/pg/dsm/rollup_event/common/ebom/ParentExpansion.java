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

public class ParentExpansion {
    boolean loaded;
    ProductPart productPart;

    private ParentExpansion(Builder builder) {
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

        boolean loaded;
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

        public Builder(Context context, String productPartOID, String assemblyType) {
            this.context = context;
            this.productPartOID = productPartOID;
            this.assemblyType = assemblyType;
        }

        public ParentExpansion build() {

            try {
                DomainObject domainObject = DomainObject.newInstance(context, productPartOID);
                if (null == markedProductPart) {
                    productPart = new ProductPart(context, domainObject.getInfo(context, getBusinessObjectSelects()), this.assemblyType);
                } else {
                    productPart = markedProductPart.copy();
                }

                // expand with EBOM to get parents recursively.
                MapList parentList = expand(domainObject);

                // below are temp variables
                ProductPart currentProductPart;
                ProductPart childProductPart;
                int level;
                List<ProductPart> tempChildStorageList = new ArrayList<>();

                List<Substitute> substitutes;
                for (Object obj : parentList) {
                    currentProductPart = new ProductPart(context, (Map<?, ?>) obj, this.assemblyType);
                    level = Integer.parseInt(currentProductPart.getLevel());

                    substitutes = getSubstitutes((Map) obj);
                    if (!substitutes.isEmpty()) {
                        currentProductPart.setSubstituteExist(true);
                        currentProductPart.setSubstitutes(substitutes);
                    }
                    if (level == 1) {
                        tempChildStorageList.clear();
                        tempChildStorageList.add(productPart);
                        productPart.addParent(currentProductPart);
                        productPart.setParentExist(Boolean.TRUE);
                        currentProductPart.addChild(productPart);
                        currentProductPart.setChildExist(Boolean.TRUE);
                    } else {
                        childProductPart = tempChildStorageList.get(level - 1);
                        currentProductPart.addChild(childProductPart);
                        currentProductPart.setChildExist(Boolean.TRUE);
                        childProductPart.addParent(currentProductPart);
                        childProductPart.setParentExist(Boolean.TRUE);
                    }
                    tempChildStorageList.add(level, currentProductPart);
                }
                tempChildStorageList.clear();
            } catch (Exception e) {
                this.loaded = false;
                logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
            }
            this.loaded = true;
            return new ParentExpansion(this);
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

        public Builder setObjectSelectList(StringList objectSelectList) {
            this.objectSelectList = objectSelectList;
            return this;
        }

        public Builder setRelationshipSelectList(StringList relationshipSelectList) {
            this.relationshipSelectList = relationshipSelectList;
            return this;
        }

        public Builder setObjectWhereClause(String objectWhereClause) {
            this.objectWhereClause = objectWhereClause;
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
            StringList businessObjectSelects = new StringList(4);
            businessObjectSelects.addElement(DomainConstants.SELECT_ID);
            businessObjectSelects.addElement(DomainConstants.SELECT_TYPE);
            businessObjectSelects.addElement(DomainConstants.SELECT_NAME);
            businessObjectSelects.addElement(DomainConstants.SELECT_REVISION);
            return businessObjectSelects;
        }

        private StringList getRelationshipSelects() {
            StringList relationshipSelects = new StringList(6);

            relationshipSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID);
            relationshipSelects.addElement(pgV3Constants.SELECT_PLBOMSUBSTITUTE_FROMID);
            relationshipSelects.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROM_PARENTID);
            relationshipSelects.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_FROMTYPE);
            relationshipSelects.addElement(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
            return relationshipSelects;
        }

        private MapList expand(DomainObject domainObject) throws FrameworkException {
            return domainObject.getRelatedObjects(
                    context,                                //Context context,
                    (null != expansionRelationship) ? expansionRelationship : DomainConstants.RELATIONSHIP_EBOM, //String relationshipPattern,
                    expansionType,        //String typePattern,
                    true,                                //boolean getTo,
                    false,                              //boolean getFrom,
                    (short) 0,                              //short recurseToLevel,
                    (null != objectSelectList) ? objectSelectList : getBusinessObjectSelects(),             //StringList objectSelects,
                    (null != relationshipSelectList) ? relationshipSelectList : getRelationshipSelects(),               //StringList relationshipSelects,
                    (null != objectWhereClause) ? objectWhereClause : DomainConstants.EMPTY_STRING,           //String objectWhere,
                    (null != relationshipWhereClause) ? relationshipWhereClause : DomainConstants.EMPTY_STRING,           //String relationshipWhere,
                    0,
                    null, // String post rel pattern
                    DomainConstants.EMPTY_STRING, // String post type pattern
                    null // post map pattern
            );
        }

        private List<Substitute> getSubstitutes(Map<?, ?> busMap) {

            List<Substitute> parts = new ArrayList<>();

            if (busMap.containsKey(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID)) {
                StringList substituteIDs = getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
                Map<String, String> beanMap;
                for (int i = 0; i < substituteIDs.size(); i++) {
                    beanMap = new HashMap<>();
                    beanMap.put(DomainConstants.SELECT_ID, substituteIDs.get(i));
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
