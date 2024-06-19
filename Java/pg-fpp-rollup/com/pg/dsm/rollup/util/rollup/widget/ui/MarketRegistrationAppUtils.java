package com.pg.dsm.rollup.util.rollup.widget.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.ws.enumeration.DSMConstants;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.db.JPO;
import matrix.util.StringList;

public class MarketRegistrationAppUtils {
    private transient final Logger logger = Logger.getLogger(this.getClass().getName());
    private Context context;
    private String objectId;

    /**
     * @param context
     * @param objectId
     */
    public MarketRegistrationAppUtils(Context context, String objectId) {
        this.context = context;
        this.objectId = objectId;
    }

    /**
     * To fetch market registration details Connected to FPP
     */
    public HashMap getMarketRegistrationDetails(String[] args) {
        HashMap returnMap = new HashMap();
        try {
            HashMap<?, ?> paramMap = JPO.unpackArgs(args);

            StringList slObjList = new StringList();
            slObjList.add(DomainConstants.SELECT_TYPE);
            slObjList.add(DomainConstants.SELECT_CURRENT);
            slObjList.add(DomainConstants.SELECT_NAME);

            List<?> tableConfigList = (List<?>) paramMap.get(RollupConstants.Basic.TABLE_CONFIG.getValue());

            DomainObject dobj = DomainObject.newInstance(this.context, this.objectId);
            Map<?, ?> mProductData = dobj.getInfo(this.context, slObjList);

            if (tableConfigList != null && !tableConfigList.isEmpty()) {
                Map<?, ?> confMap = (Map<?, ?>) tableConfigList.get(0);
                MapList dataList = processExpand(confMap);
                if (!dataList.isEmpty()) {
                    returnMap.put(DSMConstants.OBJECT_LIST.getValue(), dataList);
                } else {
                    returnMap.put(DSMConstants.OBJECT_LIST.getValue(), new MapList());
                }
                returnMap.put(RollupConstants.Basic.PRODUCT_DATA.getValue(), mProductData);
            } else {
                logger.log(Level.WARNING, null, "Config table details is empty or null..");
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, null, e);
        }
        return returnMap;
    }


    /**
     * @param confMap
     * @return
     * @throws FrameworkException
     */
    private MapList processExpand(Map confMap) throws FrameworkException {
        MapList returnList = new MapList();
        String sRelationship = (String) confMap.get(RollupConstants.Basic.RELATIONSHIP.getValue());
        String sTypes = (String) confMap.get(RollupConstants.Basic.TYPES.getValue());
        boolean sFrom = (boolean) confMap.get(RollupConstants.Basic.FROM.getValue());
        boolean sTo = (boolean) confMap.get(RollupConstants.Basic.TO.getValue());
        Integer iLevel = ((int) confMap.get(pgV3Constants.LEVEL));
        short sLevel = iLevel.shortValue();
        String sObjectwhere = (String) confMap.get(RollupConstants.Basic.OBJECT_WHERE.getValue());
        String sRelwhere = (String) confMap.get(RollupConstants.Basic.REL_WHERE.getValue());
        int sLimit = (int) confMap.get(RollupConstants.Basic.LIMIT.getValue());
        boolean isContextPushed = false;
        try {

            Part part = buildSelecTablesAndSortBy(confMap);

            StringList slRelSelect = new StringList(1);
            slRelSelect.addElement(pgV3Constants.SELECT_ATTRIBUTE_PGMOSROLLEDUPASSEMBLETYPE);

            DomainObject dobj = DomainObject.newInstance(this.context, this.objectId);
            //Pushing context to User Agent to avoid access issues for fetching data
            ContextUtil.pushContext(this.context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
            isContextPushed = true;

            MapList objectList = dobj.getRelatedObjects(this.context, sRelationship, // Rel Pattern
                    sTypes, // Type Pattern
                    part.getBusinessSelecTables(), // Object Selects
                    slRelSelect, // Rel Selects
                    sTo, // get To
                    sFrom, // Get From
                    sLevel, // level
                    sObjectwhere, // Object Where
                    sRelwhere, // Rel Where`
                    sLimit); // Count
            if (isContextPushed) {
                ContextUtil.popContext(this.context);
                isContextPushed = false;
            }

            if (objectList != null && !objectList.isEmpty()) {
                objectList = getConnectedMarkets(confMap, objectList);
                returnList = processMultiValuedAttributes(part, objectList);
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, null, e);
        } finally {
            if (isContextPushed) {
                ContextUtil.popContext(this.context);
            }
        }
        return returnList;
    }

    /**
     * This method returns MapList of Market details connected to Product Part
     *
     * @param confMap
     * @param args    mlProductData	: list of Product Part Objects
     * @throws FrameworkException
     * @returns MapList
     */
    public MapList getConnectedMarkets(Map confMap, MapList mlProductData) throws FrameworkException {
        MapList mlCountries = new MapList();
        boolean isContextPushed = false;
        try {
            if (null != mlProductData) {
                int iSize = mlProductData.size();
                Map objMap;
                String sObjId;
                String strMOSAssembleType;
                // Modified by (DSM Sogeti) for Defect # 40050 - Starts
                MapList mlRollupData = new MapList();
                // Modified by (DSM Sogeti) for Defect # 40050 - Ends
                MapList mlConnectedCountries;

                boolean hasAccess = false;

                String strRelPattern = pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE;
                String strTypePattern = pgV3Constants.TYPE_COUNTRY;
                StringList slObjectSelect = new StringList(1);
                slObjectSelect.addElement(DomainConstants.SELECT_NAME);

                Part part = buildSelecTablesAndSortBy(confMap);

                DomainObject domObj = DomainObject.newInstance(context);

                for (int i = 0; i < iSize; i++) {
                    objMap = (Map) mlProductData.get(i);
                    sObjId = (String) objMap.get(DomainConstants.SELECT_ID);
                    strMOSAssembleType = (String) objMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMOSROLLEDUPASSEMBLETYPE);
                    if (UIUtil.isNotNullAndNotEmpty(sObjId)) {
                        domObj.setId(sObjId);
                        hasAccess = FrameworkUtil.hasAccess(this.context, domObj, pgV3Constants.READSHOW_ACCESS);
                        //Pushing context to User Agent to avoid access issues for fetching data
                        if (!hasAccess) {
                            ContextUtil.pushContext(this.context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                            isContextPushed = true;
                        }
                        // getRelatedObjects in for Loop is required to fetch country details for each object
                        mlConnectedCountries = domObj.getRelatedObjects(this.context,
                                strRelPattern,  // Rel Pattern
                                strTypePattern, //type Pattern
                                slObjectSelect, //Object selects
                                part.getRelSelecTables(),  //Rel selects
                                false,  //to Side
                                true,   //from side
                                (short) 1,  // recurse to
                                DomainConstants.EMPTY_STRING,  // object where
                                null,     // rel where
                                0            //limit
                        );

                        if (isContextPushed) {
                            ContextUtil.popContext(this.context);
                            isContextPushed = false;
                        }

                        if (null != mlConnectedCountries) {
                            if (!mlConnectedCountries.isEmpty() && mlConnectedCountries.size() > 0) {

                                mlConnectedCountries.sort(DomainConstants.SELECT_NAME, "ascending", "String");
                                objMap.put(pgV3Constants.COUNTRIESLIST, mlConnectedCountries);

                            } else {

                                objMap.put(pgV3Constants.COUNTRIESLIST, new MapList());

                            }

                            objMap.put(pgV3Constants.HAS_ACCESS, hasAccess);

                            if (!hasAccess) {
                                objMap.put(pgV3Constants.SELECT_ATTRIBUTE_TITLE, pgV3Constants.NO_ACCESS);
                            }
                        }
                        // Modified by (DSM Sogeti) for Defect # 40050 - Starts
                        if (UIUtil.isNotNullAndNotEmpty(strMOSAssembleType) && pgV3Constants.KEY_EBOMCHILDREN.equalsIgnoreCase(strMOSAssembleType) || (pgV3Constants.KEY_ALTERNATE.equalsIgnoreCase(strMOSAssembleType)
                                || pgV3Constants.KEY_EBOMCHILDREN_ALTERNATE.equalsIgnoreCase(strMOSAssembleType)) || pgV3Constants.KEY_SUBSTITUTE.equalsIgnoreCase(strMOSAssembleType) || pgV3Constants.KEY_SUBSTITUTE_ALTERNATE.equalsIgnoreCase(strMOSAssembleType)) {
                            mlRollupData.add(objMap);
                        }
                        // Modified by (DSM Sogeti) for Defect # 40050 - Ends
                    }
                }
                // Modified by (DSM Sogeti) for Defect # 40050 - Starts
                mlCountries.addAll(mlRollupData);
                // Modified by (DSM Sogeti) for Defect # 40050 - Ends
            }
        } catch (FrameworkException ex) {
            logger.log(Level.WARNING, null, ex);
        } finally {
            if (isContextPushed) {
                ContextUtil.popContext(this.context);
            }
        }
        return mlCountries;
    }

    /**
     * @param confMap
     * @param objectList
     * @throws FrameworkException
     */
    private MapList processMultiValuedAttributes(Part part, MapList mlCountriesList) {
        MapList returnList = new MapList();
        Map<?, ?> mCountyMap;
        MapList mlCountryList;
        for (int i = 0; i < mlCountriesList.size(); i++) {
            mCountyMap = (Map<?, ?>) mlCountriesList.get(i);
            mlCountryList = processRegisteredCountries(part, mCountyMap);
            returnList.addAll(mlCountryList);
        }
        return returnList;
    }

    /**
     * To process multivalues and single values attributes and returns a MapList
     */
    private MapList processRegisteredCountries(Part part, Map mCountyMap) {
        MapList mlCountriesDataList = (MapList) mCountyMap.get(pgV3Constants.COUNTRIESLIST);
        MapList finalCountriesList = new MapList();
        Map mProductMap = new HashMap<>(mCountyMap);
        mProductMap.remove(pgV3Constants.COUNTRIESLIST);
        Map countryMap;

        for (int i = 0; i < mlCountriesDataList.size(); i++) {
            countryMap = (Map) mlCountriesDataList.get(i);
            finalCountriesList.addAll(processAttributes(part, countryMap, mProductMap));
        }
        return finalCountriesList;
    }

    /**
     * To process multivalues and single values attributes and returns a MapList
     */
    private MapList processAttributes(Part part, Map<Object, Object> countryMap, Map mProductMap) {
        MapList returnList = new MapList();
        StringList multiSelecTableList = part.getMultivaluedSelecTables();
        String sCountryName = (String) countryMap.get(DomainConstants.SELECT_NAME);
        countryMap.remove(DomainConstants.SELECT_NAME);
        countryMap.putAll(mProductMap);
        String selecTable;
        StringList slValuesList;
        String sValue;

        for (int i = 0; i < multiSelecTableList.size(); i++) {
            selecTable = multiSelecTableList.get(i);
            slValuesList = convertObjectToStringList(countryMap.get(selecTable));

            for (int j = 0; j < slValuesList.size(); j++) {
                sValue = slValuesList.get(j);
                sValue = filterValueForAttributes(selecTable, sValue);
                if (selecTable.equalsIgnoreCase(pgV3Constants.SELECT_ATTRIBUTE_PGPACKSIZE_INPUTVALUE)) {
                    selecTable = pgV3Constants.SELECT_ATTRIBUTE_PGPACKSIZE;
                }
                if (returnList.size() - 1 >= j) {
                    Map<String, String> map = (Map<String, String>) returnList.get(j);
                    map.put(selecTable, sValue);
                } else {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(selecTable, sValue);
                    returnList.add(map);
                }
            }
        }

        StringList businessSelecTables = part.getSinglevaluedSelecTables();
        for (int i = 0; i < businessSelecTables.size(); i++) {
            selecTable = businessSelecTables.get(i);
            sValue = (String) countryMap.get(selecTable);
            sValue = filterValueForAttributes(selecTable, sValue);
            for (int j = 0; j < returnList.size(); j++) {
                if (selecTable.equalsIgnoreCase(pgV3Constants.COUNTRY_COLUMN)) {
                    ((Map<String, String>) returnList.get(j)).put(selecTable, sCountryName);
                    ((Map<String, Object>) returnList.get(j)).put(pgV3Constants.HAS_ACCESS, countryMap.get("hasAccess"));
                } else {
                    ((Map<String, String>) returnList.get(j)).put(selecTable, sValue);
                }
            }
        }
        return returnList;
    }

    /**
     * This method filter special attribute values based on formats
     *
     * @param strValue
     * @returns String
     * Modified by DSM in 2018x.6 Jan CW for Defect #45527
     */
    private String filterValueForAttributes(String selecTable, String sValue) {
        sValue = checkNullOrEmpty(sValue);
        if (pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONENDDATE.equalsIgnoreCase(selecTable)) {
            sValue = formatDate(sValue);
        } else if (pgV3Constants.SELECT_ATTRIBUTE_PGMOSROLLEDUPASSEMBLETYPE.equalsIgnoreCase(selecTable)) {
            if (UIUtil.isNotNullAndNotEmpty(sValue) && sValue.contains(pgV3Constants.KEY_COLON)) {
                sValue = sValue.substring(sValue.indexOf(pgV3Constants.KEY_COLON) + 1, sValue.length());
            }
            if (UIUtil.isNotNullAndNotEmpty(sValue) && pgV3Constants.KEY_EBOMCHILDREN.equalsIgnoreCase(sValue)) {
                sValue = pgV3Constants.KEY_PRIMARY;
            } else if (UIUtil.isNotNullAndNotEmpty(sValue) && pgV3Constants.KEY_SUBSTITUTE.equalsIgnoreCase(sValue)) {
                sValue = pgV3Constants.KEY_SUBSTITUTE;
            } else if (UIUtil.isNotNullAndNotEmpty(sValue) && pgV3Constants.KEY_ALTERNATE.equalsIgnoreCase(sValue)) {
                sValue = pgV3Constants.KEY_ALTERNATE;
            }
        } else if (pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGSITE.equalsIgnoreCase(selecTable) || pgV3Constants.SELECT_ATTRIBUTE_PGMANUFACTURINGSITE.equalsIgnoreCase(selecTable)) {
            sValue = sValue.replaceAll("\\" + pgV3Constants.SYMBOL_CAP, pgV3Constants.SYMBOL_COMMA);
        }
        sValue = replaceHTMLspecialcharacters(sValue);
        return sValue;
    }

    /**
     * This method replace the &,<,> symbol to unicode
     *
     * @param strValue
     * @returns String replaced &,<,> symbol to unicode
     * Modified by DSM in 2018x.6 Jan CW for Defect #45527
     */
    public String replaceHTMLspecialcharacters(String strValue) {
        if (UIUtil.isNotNullAndNotEmpty(strValue) && strValue.contains("&amp;")) {
            strValue = strValue.replaceAll("&amp;", "&");
        }
        if (UIUtil.isNotNullAndNotEmpty(strValue) && strValue.contains("&gt;")) {
            strValue = strValue.replaceAll("&gt;", ">");
        }
        if (UIUtil.isNotNullAndNotEmpty(strValue) && strValue.contains("&lt;")) {
            strValue = strValue.replaceAll("&lt;", "<");
        }
        return strValue;
    }

    /**
     * This method returns string after checking Null
     *
     * @param args strValue - value to check perform null check
     * @returns String
     */
    public String checkNullOrEmpty(String strValue) {
        return UIUtil.isNullOrEmpty(strValue) ? DomainConstants.EMPTY_STRING : strValue;
    }

    /**
     * This method returns string after formating Expiration date
     *
     * @param args
     * @returns String
     */
    public String formatDate(String strExpirationDate) {
        if (UIUtil.isNotNullAndNotEmpty(strExpirationDate) && strExpirationDate.length() > 0) {
            StringList slDate = StringUtil.split(strExpirationDate, pgV3Constants.SYMBOL_SPACE);
            if (!slDate.isEmpty()) {
                strExpirationDate = slDate.get(0);
            }
        }
        return checkNullOrEmpty(strExpirationDate);
    }

    /**
     * To Convert obj to StringList
     */
    public StringList convertObjectToStringList(Object obj) {
        StringList convertedStringList = new StringList();
        String sValue;
        if (obj != null) {

            if (obj instanceof StringList) {
                convertedStringList = (StringList) obj;
            } else if (obj instanceof String) {
                sValue = (String) obj;
                convertedStringList.add(sValue);
            }

        }

        return convertedStringList;
    }

    /**
     * @param confMap
     */
    private Part buildSelecTablesAndSortBy(Map confMap) {
        Part part = new Part();
        StringList objectSelectables = new StringList();
        StringList relSelectables = new StringList();
        StringList multivaluedSelectables = new StringList();
        StringList singlevaluedSelectables = new StringList();
        String selectType;
        String selectable;
        Map columnMap;
        boolean isMultivalued = false;
        boolean isSort = false;
        List configList = (List) confMap.get(RollupConstants.Basic.COLUMN_CONFIG.getValue());

        for (int i = 0; i < configList.size(); i++) {
            columnMap = (Map) configList.get(i);
            selectType = (String) columnMap.get(RollupConstants.Basic.SELECT_TYPE.getValue());
            selectable = (String) columnMap.get(RollupConstants.Basic.SELECTABLE.getValue());
            isMultivalued = (boolean) columnMap.get("multivalued");
            isSort = columnMap.containsKey("sort") ? (boolean) columnMap.get("sort") : Boolean.FALSE;
            if (isSort) {
                part.setSortBy(selectable);
            }

            if (isMultivalued) {
                if (selectable.equalsIgnoreCase(pgV3Constants.SELECT_ATTRIBUTE_PGPACKSIZE)) {
                    selectable = pgV3Constants.SELECT_ATTRIBUTE_PGPACKSIZE_INPUTVALUE;
                }
                multivaluedSelectables.add(selectable);
                part.setMultiValued(isMultivalued);
            } else {
                singlevaluedSelectables.add(selectable);
            }
            switch (selectType) {
                case "businessobject":
                    objectSelectables.add(selectable);
                    break;
                case "relationship":
                    relSelectables.add(selectable);
                    break;
                default:
                    break;
            }

        }
        part.setBusinessSelecTables(objectSelectables);
        part.setMultivaluedSelecTables(multivaluedSelectables);
        part.setRelSelecTables(relSelectables);
        part.setSinglevaluedSelecTables(singlevaluedSelectables);
        return part;
    }

    /**
     * @author DSM(Sogeti)
     */
    protected class Part {
        String sortBy;
        boolean isMultiValued;
        StringList businessSelecTables;
        StringList multivaluedSelecTables;
        StringList relSelecTables;
        StringList singlevaluedSelecTables;

        /**
         * @return the sortBy
         */
        public String getSortBy() {
            return sortBy;
        }

        /**
         * @param sortBy the sortBy to set
         */
        public void setSortBy(String sortBy) {
            this.sortBy = sortBy;
        }

        /**
         * @return the isMultiValued
         */
        public boolean isMultiValued() {
            return isMultiValued;
        }

        /**
         * @param isMultiValued the isMultiValued to set
         */
        public void setMultiValued(boolean isMultiValued) {
            this.isMultiValued = isMultiValued;
        }

        /**
         * @return the businessSelecTables
         */
        public StringList getBusinessSelecTables() {
            return businessSelecTables;
        }

        /**
         * @param businessSelecTables the businessSelecTables to set
         */
        public void setBusinessSelecTables(StringList businessSelecTables) {
            this.businessSelecTables = businessSelecTables;
        }

        /**
         * @return the multivaluedSelecTables
         */
        public StringList getMultivaluedSelecTables() {
            return multivaluedSelecTables;
        }

        /**
         * @param multivaluedSelecTables the multivaluedSelecTables to set
         */
        public void setMultivaluedSelecTables(StringList multivaluedSelecTables) {
            this.multivaluedSelecTables = multivaluedSelecTables;
        }

        /**
         * @return the relSelecTables
         */
        public StringList getRelSelecTables() {
            return relSelecTables;
        }

        /**
         * @param relSelecTables the relSelecTables to set
         */
        public void setRelSelecTables(StringList relSelecTables) {
            this.relSelecTables = relSelecTables;
        }

        /**
         * @return the singlevaluedSelecTables
         */
        public StringList getSinglevaluedSelecTables() {
            return singlevaluedSelecTables;
        }

        /**
         * @param singlevaluedSelecTables the singlevaluedSelecTables to set
         */
        public void setSinglevaluedSelecTables(StringList singlevaluedSelecTables) {
            this.singlevaluedSelecTables = singlevaluedSelecTables;
        }

    }
}
