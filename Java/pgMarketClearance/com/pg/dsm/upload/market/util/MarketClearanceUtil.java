/*
 **   MarketClearanceUtil.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Contains all utility methods to load resources like XML page, config object.
 **
 */
package com.pg.dsm.upload.market.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.upload.market.beans.mx.MarketConfig;
import com.pg.dsm.upload.market.beans.xml.XML;
import com.pg.dsm.upload.market.enumeration.MarketClearanceConstant;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.StringList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class MarketClearanceUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Constructor
     *
     * @since DSM 2018x.5
     */
    public MarketClearanceUtil() {
        logger.info(DomainConstants.EMPTY_STRING);
    }

    /**
     * Method to read page from matrix.
     *
     * @param context - matrix Context
     * @return String - page out string
     * @throws MatrixException - exception
     * @since DSM 2018x.5
     */
    public String readConfigPage(Context context) throws MatrixException {
        String sPageContent;
        Page matrixPage = null;
        try {
            matrixPage = new Page(MarketClearanceConstant.EXCEL_CONFIG_PAGE.getValue());
            matrixPage.open(context);
            sPageContent = matrixPage.getContents(context);
        } finally {
            if (null != matrixPage && matrixPage.isOpen())
                matrixPage.close(context);
        }
        logger.info("Market Clearance Excel Config Page XML loaded");
        return sPageContent;
    }

    /**
     * Method to convert matrix page object into java bean object
     *
     * @param context - matrix Context
     * @return XML - bean object
     * @throws MatrixException, JAXBException - exception
     * @since DSM 2018x.5
     */
    public XML getConfigPage(Context context) throws MatrixException, JAXBException {
        return (XML) unmarshallXMLFile(new StringReader(readConfigPage(context)), XML.class);
    }

    /**
     * Method to unmarshall matrix page object into java bean object
     *
     * @param sStringReader - StringReader
     * @param objMapperClass    - Class object
     * @return Object - bean object
     * @throws JAXBException - exception
     * @since DSM 2018x.5
     */
    public Object unmarshallXMLFile(StringReader sStringReader, Class<?> objMapperClass) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(objMapperClass);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(sStringReader);
    }

    /**
     * Method to get type comma separated actual names
     *
     * @param context - matrix Context
     * @param types   -  String
     * @return String - string out
     * @since DSM 2018x.5
     */
    public String getConfiguredTypes(Context context, String types) {
        return getCommaSeparatedConfiguredActualPropertyName(context, types);
    }

    /**
     * Method to get type comma separated actual names
     *
     * @param context  - matrix Context
     * @param policies - String
     * @return String - string out
     * @since DSM 2018x.5
     */
    public String getConfiguredPolicies(Context context, String policies) {
        return getCommaSeparatedConfiguredActualPropertyName(context, policies);
    }

    /**
     * Method to unmarshall matrix page object into java bean object
     *
     * @param context - matrix Context
     * @return String - string out
     * @since DSM 2018x.5
     */
    public String getCommaSeparatedConfiguredActualPropertyName(Context context, String types) {
        StringBuilder sPropertyBuilder = new StringBuilder();
        StringList allowedTypeList = StringUtil.split(types, pgV3Constants.SYMBOL_COMMA);
        for (String allowedType : allowedTypeList) {
            allowedType = PropertyUtil.getSchemaProperty(context, allowedType);
            sPropertyBuilder.append(allowedType);
            sPropertyBuilder.append(pgV3Constants.SYMBOL_COMMA);
        }
        if (sPropertyBuilder.length() > 0) {
            sPropertyBuilder.setLength(sPropertyBuilder.length() - 1);
        }
        return sPropertyBuilder.toString();
    }

    /**
     * Method to get market clearance config object into bean
     *
     * @param context - matrix Context
     * @return MarketClearanceConfig - MarketClearanceConfig object
     * @since DSM 2018x.5
     */
    public MarketConfig getConfigObject(Context context) throws FrameworkException {
        StringList slObjSelects = new StringList();
        slObjSelects.add(MarketClearanceConstant.SELECT_ATTRIBUTE_PG_CONFIG_COMMON_ADMIN_MAIL_ID.getValue());
        slObjSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCONFIGISACTIVE);
        MapList objectList = DomainObject.findObjects(
                context, // context
                pgV3Constants.TYPE_PGCONFIGURATIONADMIN, // type pattern
                MarketClearanceConstant.CONFIG_OBJECT_NAME.getValue(), // name pattern
                pgV3Constants.SYMBOL_HYPHEN, // revision pattern
                DomainConstants.QUERY_WILDCARD, // owner pattern
                DomainConstants.QUERY_WILDCARD, // // vault pattern
                DomainConstants.EMPTY_STRING, // where expression
                false, // expandType
                slObjSelects // object selects
        );
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(objectList.get(0), MarketConfig.class);
    }

    /**
     * Method to get market clearance config object into bean
     *
     * @param startTime - Instant
     * @return Duration - Duration object
     * @since DSM 2018x.5
     */
    public Duration getDuration(Instant startTime) {
        return Duration.between(startTime, Instant.now());
    }

    /**
     * Method to get market clearance config object into bean
     *
     * @param startTime - Instant
     * @return String - string
     * @since DSM 2018x.5
     */
    public String getExecutionTimeString(Instant startTime) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(MarketClearanceConstant.EXECUTION_TOOK.getValue());
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append(getDuration(startTime).getSeconds());
        messageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        messageBuilder.append((MarketClearanceConstant.EXECUTION_SECONDS).getValue());
        return messageBuilder.toString();
    }
}
