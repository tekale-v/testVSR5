/*
* Java File Name: ProductPart.java
* Created By: PLM DSM-2018x.6 - Sogeti
* Clone From/Reference: NA
* Purpose:  This File contains utility methods to expand object details..
* Change History: Added by DSM(Sogeti)-2018x.6 (req id: 8802) for Market Clearance.
*/
package com.pg.v4.util.marketclearance.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.util.marketclearance.bean.MarketBean;
import com.pg.v4.util.marketclearance.bean.MarketRegistrationBean;

import matrix.db.Context;
import matrix.util.StringList;

public class ProductPart {

    private List<MarketBean> markets;

    /**
     * @param expand
     */
    private ProductPart(Expand expand) {
        this.markets = expand.markets;
    }

    /**
     * This method return list of connected market with registration details
     * 
     * @return List<MarketBean> -list connected market 
     */
    public List<MarketBean> getMarkets() {
        return markets;
    }

    /**
     * Inner class to expand domain object 
     * @author DSM
     *
     */
    public static class Expand {
        private Context context;
        private String typePattern;
        private String relationshipPattern;
        private StringList businessObjectSelectable;
        private StringList relationshipObjectSelectable;
        private boolean expandTo;
        private boolean expandFrom;
        private short recurseLevel;
        private String businessObjectWhere;
        private String relationshipWhere;
        private DomainObject domObj;
        private int limit;
        List<MarketBean> markets;

        /**
         * @param context - login user context
         * @param objectId - processing object id
         * @throws FrameworkException 
         */
        public Expand(Context context, String objectId) throws FrameworkException {
            this.context = context;
            this.domObj = DomainObject.newInstance(context,objectId);
        }
        
        /**
         * @param context - login user context
         * @param objectId - processing object id
         */
        public Expand(Context context, DomainObject domObj) {
            this.context = context;
            this.domObj = domObj;
        }
        
        /**
         * @param typePattern
         * @return
         */
        public Expand setTypePattern(String typePattern) {
            this.typePattern = typePattern;
            return this;
        }

        /**
         * @param relationshipPattern
         * @return
         */
        public Expand setRelationshipPattern(String relationshipPattern) {
            this.relationshipPattern = relationshipPattern;
            return this;
        }

        /**
         * @param businessObjectSelectable
         * @return
         */
        public Expand setBusinessObjectSelectable(StringList businessObjectSelectable) {
            this.businessObjectSelectable = businessObjectSelectable;
            return this;
        }

        /**
         * @param relationshipObjectSelectable
         * @return
         */
        public Expand setRelationshipObjectSelectable(StringList relationshipObjectSelectable) {
            this.relationshipObjectSelectable = relationshipObjectSelectable;
            return this;
        }
 
        /**
         * @param expandTo
         * @return
         */
        public Expand setExpandTo(boolean expandTo) {
            this.expandTo = expandTo;
            return this;
        }

        /**
         * @param expandFrom
         * @return
         */
        public Expand setExpandFrom(boolean expandFrom) {
            this.expandFrom = expandFrom;
            return this;
        }

        /**
         * @param recurseLevel
         * @return
         */
        public Expand setRecurseLevel(short recurseLevel) {
            this.recurseLevel = recurseLevel;
            return this;
        }

        /**
         * @param businessObjectWhere
         * @return
         */
        public Expand setBusinessObjectWhere(String businessObjectWhere) {
            this.businessObjectWhere = businessObjectWhere;
            return this;
        }

        /**
         * @param relationshipWhere
         * @return
         */
        public Expand setRelationshipWhere(String relationshipWhere) {
            this.relationshipWhere = relationshipWhere;
            return this;
        }

        /**
         * @param limit
         * @return
         */
        public Expand setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * This method execute expand object and return ProductPart class object 
         * @return ProductPart class object
         * @throws FrameworkException 
         * @throws Exception 
         */
        public ProductPart perform() throws FrameworkException { 
                     	
                MapList relatedObjects = this.domObj.getRelatedObjects(
                        context, //context
                        relationshipPattern, //relPattern
                        typePattern, //typePattern
                        businessObjectSelectable, //objectSelects
                        relationshipObjectSelectable, //relationshipSelects
                        expandTo, //getTo
                        expandFrom, //getFrom
                        recurseLevel, //recurseToLevel
                        businessObjectWhere, //objectWhere
                        relationshipWhere, //relationshipWhere
                        limit //limit
                );                
                if(!relatedObjects.isEmpty()) {
                	markets = new ArrayList<>();
                	MarketBean marketBean;
                    List<MarketRegistrationBean> marketRegistrationBeans;
                    for (Object relatedObject : relatedObjects) {
                        marketRegistrationBeans = getMarketRegistrations((Map<?, ?>)relatedObject,true);
                        marketBean = new MarketBean((Map<?, ?>)relatedObject, marketRegistrationBeans);
                        markets.add(marketBean);
                    }
                }
           
            return new ProductPart(this);
        }
        
        /**
         * This method return list of market registration bean object
         * @param relatedObject -Map
         * @return
         */
        public static List<MarketRegistrationBean> getMarketRegistrations(Map<?,?> relatedObject,boolean isFromCron) {
            List<MarketRegistrationBean> marketRegistrationBeans = new ArrayList<>();

            if(null!=relatedObject && !relatedObject.isEmpty()) {
                StringList pgPackingSite = getSelectedListFromMap(relatedObject, isFromCron?pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGSITE:pgV3Constants.ATTRIBUTE_PGPACKINGSITE);
                StringList pgManufacturingSite = getSelectedListFromMap(relatedObject, isFromCron?pgV3Constants.SELECT_ATTRIBUTE_PGMANUFACTURINGSITE:pgV3Constants.ATTRIBUTE_PGMANUFACTURINGSITE);
                StringList pgRegistrationEndDate = getSelectedListFromMap(relatedObject, isFromCron?pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONENDDATE:pgV3Constants.ATTRIBUTE_PGREGISTRATIONENDDATE);
                StringList pgPlantRestriction = getSelectedListFromMap(relatedObject, isFromCron?pgV3Constants.SELECT_ATTRIBUTE_PGPLANTRESTRICTION:pgV3Constants.ATTRIBUTE_PGPLANTRESTRICTION);
                StringList pgRegistrationStatus = getSelectedListFromMap(relatedObject, isFromCron?pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONSTATUS:pgV3Constants.ATTRIBUTE_PGREGISTRATIONSTATUS);
                StringList pgPSRAApprovalStatus = getSelectedListFromMap(relatedObject, isFromCron?pgV3Constants.SELECT_ATTRIBUTE_PGPSRAAPPROVALSTATUS:pgV3Constants.ATTRIBUTE_PGPSRAAPPROVALSTATUS);
                
                MarketRegistrationBean marketRegistrationBean;
                int size=pgPackingSite.size();
                
                // Added/Modified by Sogeti for May CW 2022 (Defect:47476) start
                int iPackingSite = pgPackingSite.size();
                int iManufacturingSite = pgManufacturingSite.size();
                int iRegistrationEndDate = pgRegistrationEndDate.size();
                int iPlantRestriction = pgPlantRestriction.size();
                int iRegistrationStatus = pgRegistrationStatus.size();
                int iApprovalStatus = pgPSRAApprovalStatus.size();
                List<Integer> integerList = java.util.Arrays.asList(
                        iPackingSite,          // Packing Site          - 0th index
                        iManufacturingSite,    // Manufacturing Site    - 1st index
                        iRegistrationEndDate,  // Registration Exp Date - 2nd index
                        iPlantRestriction,     // Plant Restriction     - 3rd index
                        iRegistrationStatus,   // Registration Status   - 4th index
                        iApprovalStatus);      // Approval Status       - 5th index
                Integer max = java.util.Collections.max(integerList);
                for (int i = 0; i < max; i++) {
                    marketRegistrationBean = new MarketRegistrationBean();
                    marketRegistrationBean.setPackingSite((iPackingSite > i) ? pgPackingSite.get(i) : DomainConstants.EMPTY_STRING);
                    marketRegistrationBean.setBulkMakingManufacturingSite((iManufacturingSite > i) ? pgManufacturingSite.get(i) : DomainConstants.EMPTY_STRING);
                    marketRegistrationBean.setRegistrationExpirationDate((iRegistrationEndDate > i) ? pgRegistrationEndDate.get(i) : DomainConstants.EMPTY_STRING);
                    marketRegistrationBean.setRestrictions((iPlantRestriction > i) ? pgPlantRestriction.get(i) : DomainConstants.EMPTY_STRING);
                    marketRegistrationBean.setRegistrationStatus((iRegistrationStatus > i) ? pgRegistrationStatus.get(i) : DomainConstants.EMPTY_STRING);
                    marketRegistrationBean.setGpsApprovalStatus((iApprovalStatus > i) ? pgPSRAApprovalStatus.get(i) : DomainConstants.EMPTY_STRING);
                    marketRegistrationBeans.add(marketRegistrationBean);
                } // Added/Modified by Sogeti for May CW 2022 (Defect:47476) end
            }
            return marketRegistrationBeans;
        }
        
        /**
         * This method return map value in string list format
         * @param map
         * @param key
         * @return
         */
        private static StringList getSelectedListFromMap(Map<?,?> map, String key) {
            Object obj = map.get(key);
            if (obj == null)
                return new StringList(0);
            return (obj instanceof String) ? new StringList((String) obj) : (StringList) obj;
        }
    }

}
