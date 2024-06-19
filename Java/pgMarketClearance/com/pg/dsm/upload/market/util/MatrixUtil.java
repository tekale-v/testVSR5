/*
 **   MatrixUtil.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Contains all utility methods for matrix.
 **
 */
package com.pg.dsm.upload.market.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.DomainRelationship;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.PersonUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.market.beans.mx.Gcas;
import com.pg.dsm.upload.market.beans.mx.Market;
import com.pg.dsm.upload.market.beans.xl.EachRow;
import com.pg.dsm.upload.market.beans.xl.Excel;
import com.pg.dsm.upload.market.enumeration.MarketClearanceConstant;
import com.pg.v3.custom.pgV3Constants;
import com.pg.v4.beans.COSRequest;
import matrix.db.Context;
import matrix.db.JPO;
import matrix.db.MxMessageSupport;
import matrix.db.RelationshipType;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.commons.beanutils.BeanUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MatrixUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Context context;
    private final Excel excel;
    private final String vault;
    private final String gcasAllowedTypes;
    private final String typeCountry;
    private final String gcasSearchWhereClauseString;
    private final boolean isLoggingEnabled;
    private final String policyProductionFormulationPart;

    /**
     * Constructor
     *
     * @param context - matrix Context
     * @param excel   - Excel bean object
     * @since DSM 2018x.5
     */
    public MatrixUtil(Context context, Excel excel, boolean isLoggingEnabled) {
        this.context = context;
        this.excel = excel;
        this.isLoggingEnabled = isLoggingEnabled;
        this.typeCountry = PropertyUtil.getSchemaProperty(context, "type_Country");
        this.vault = PropertyUtil.getSchemaProperty(context, "vault_eServiceProduction");
        this.policyProductionFormulationPart = PropertyUtil.getSchemaProperty(context, "policy_IPMRestrictedPart");
        this.gcasAllowedTypes = excel.getAllowedTypes();
        this.gcasSearchWhereClauseString = getGcasWhereClause();
    }

    /**
     * Helper method to update gcas & market bean.
     *
     * @return void
     * @throws FrameworkException, InvocationTargetException, IllegalAccessException - exception
     * @since DSM 2018x.5
     */
    public void searchGcasAndMarket() throws FrameworkException, InvocationTargetException, IllegalAccessException {
        Map<String, Gcas> gcasMaps = searchGcas();
        Map<String, Market> marketMaps = searchMarket();
        List<EachRow> excelRows = excel.getRows();

        String sGCASName;
        String sMarketName;
        for (EachRow objEachRow : excelRows) {
            sGCASName = objEachRow.getGCAS();
            sMarketName = objEachRow.getCOUNTRY_REQUESTED();
            if (gcasMaps.containsKey(sGCASName)) {
                updateGcasBean(gcasMaps.get(sGCASName), objEachRow);
            }
            if (marketMaps.containsKey(sMarketName)) {
                updateMarketBean(marketMaps.get(sMarketName), objEachRow);
            }
            // update gcas connected countries.
            updateGcasMarketBean(objEachRow);
        }
    }

    /**
     * Helper method to search gcas.
     *
     * @return Map<String, Map> -
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public Map<String, Gcas> searchGcas() throws FrameworkException {
        Map<String, Gcas> gcasMaps = new HashMap<>();
        List<EachRow> excelRowList = excel.getRows();
        Set<String> gcasNameSet = new HashSet<>(excelRowList.size());
        excelRowList.forEach(row -> gcasNameSet.add(row.getGCAS()));
        MapList objectList = findObject(
                gcasAllowedTypes,
                String.join(pgV3Constants.SYMBOL_COMMA, gcasNameSet),
                gcasSearchWhereClauseString,
                getGcasBusSelects());
        Iterator<?> listIterator = objectList.iterator();
        ObjectMapper objectMapper;
        Map<?, ?> gcasInfoMap;
        while (listIterator.hasNext()) {
            gcasInfoMap = (Map<?, ?>) listIterator.next();
            if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                logger.info("Gcas Map <--:"+gcasInfoMap);
            }
            objectMapper = new ObjectMapper();
            gcasMaps.put((String) gcasInfoMap.get(DomainConstants.SELECT_NAME), objectMapper.convertValue(gcasInfoMap, Gcas.class));
        }
        return gcasMaps;
    }

    /**
     * Helper method to search market.
     *
     * @return Map<String, Map> -
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public Map<String, Market> searchMarket() throws FrameworkException {
        Map<String, Market> marketMaps = new HashMap<>();
        List<EachRow> excelRowList = excel.getRows();
        Set<String> marketNameSet = new HashSet<>(excelRowList.size());
        excelRowList.forEach(row -> marketNameSet.add(row.getCOUNTRY_REQUESTED()));
        MapList objectList = findObject(
                typeCountry,
                String.join(pgV3Constants.SYMBOL_COMMA, marketNameSet),
                DomainConstants.SELECT_CURRENT + pgV3Constants.SYMBOL_EQUALS + MarketClearanceConstant.POLICY_COUNTRY_STATE_ACTIVE.getState(context, MarketClearanceConstant.POLICY_COUNTRY.getPolicy(context)),
                getMarketBusSelects());
        Iterator<?> listIterator = objectList.iterator();
        ObjectMapper objectMapper;
        Map<?, ?> marketInfoMap;
        while (listIterator.hasNext()) {
            marketInfoMap = (Map<?, ?>) listIterator.next();
            objectMapper = new ObjectMapper();
            marketMaps.put((String) marketInfoMap.get(DomainConstants.SELECT_NAME), objectMapper.convertValue(marketInfoMap, Market.class));
        }
        return marketMaps;
    }

    /**
     * Helper method to update gcas and its country info bean.
     *
     * @param objEachRow - EachRow  - bean object
     * @return void
     * @since DSM 2018x.5
     */
    private void updateGcasMarketBean(EachRow objEachRow) {
        if (objEachRow.isGcasObjectExist()) {
            Gcas gcasObj = objEachRow.getGcasObj();
            if (gcasObj != null && objEachRow.isMarketObjectExist() && objEachRow.isGcasHasConnectedCountry()) {
                objEachRow.setCountryAlreadyConnected(objEachRow.getExistingCountries().contains(objEachRow.getCOUNTRY_REQUESTED()));
                gcasObj.setCountryAlreadyConnected(objEachRow.getExistingCountries().contains(objEachRow.getCOUNTRY_REQUESTED()));
            }
        }
    }

    /**
     * Helper method to update gcas bean if it has country connected.
     *
     * @param objGcas - Gcas  - bean object
     * @param markets - String
     * @return void
     * @since DSM 2018x.5
     */
    public void setGcasMarket(Gcas objGcas, String markets) {
        if (UIUtil.isNotNullAndNotEmpty(markets)) {
            markets = markets.replace(MarketClearanceConstant.CONTROL_CHARACTER_BEL_ESCAPE.getValue(), MarketClearanceConstant.CONTROL_CHARACTER_BEL.getValue()).replace(MarketClearanceConstant.CONTROL_CHARACTER_BEL.getValue(), pgV3Constants.SYMBOL_PIPE);
            StringList marketList = StringUtil.split(markets, pgV3Constants.SYMBOL_PIPE);
            if (marketList != null && !marketList.isEmpty()) {
                objGcas.setGcasHasConnectedCountry(true);
                objGcas.setExistingCountries(marketList.stream().distinct().collect(Collectors.toList()));
            } else {
                objGcas.setGcasHasConnectedCountry(false);
            }
        }
    }

    /**
     * Helper method to search object.
     *
     * @param sType           - String  - type
     * @param sName           - String  - name
     * @param sBusWhereClause - String  - object where clause
     * @return MapList - map list
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public MapList findObject(String sType, String sName, String sBusWhereClause, StringList objBasicSelectList) throws FrameworkException {
        return DomainObject.findObjects(
                context,
                sType, // typePattern
                sName, // name pattern
                DomainConstants.QUERY_WILDCARD, // revision pattern
                DomainConstants.QUERY_WILDCARD, // owner pattern
                vault, // vault pattern
                sBusWhereClause, // where expression
                false, // expandType
                objBasicSelectList);// objectSelects
    }

    /**
     * Helper method to update gcas bean.
     *
     * @param objGcas    - Gcas - bean object
     * @param objEachRow - EachRow  - bean object
     * @return void
     * @throws IllegalAccessException, InvocationTargetException
     * @since DSM 2018x.5
     */
    public void updateGcasBean(Gcas objGcas, EachRow objEachRow) throws IllegalAccessException, InvocationTargetException {
        setGcasMarket(objGcas, objGcas.getCountryNames());
        objGcas.setLastRev(Boolean.parseBoolean(objGcas.getLast()));
        objGcas.setGcasExist(true);
        objGcas.setGcasObjectExist(true);
        BeanUtils.copyProperties(objEachRow, objGcas);
        objEachRow.setGcasObj(objGcas);
    }

    /**
     * Helper method to update market bean.
     *
     * @param objMarket  - Market - bean object
     * @param objEachRow - EachRow  - bean object
     * @return void
     * @since DSM 2018x.5
     */
    public void updateMarketBean(Market objMarket, EachRow objEachRow) {
        objEachRow.setMarketExist(true);
        objEachRow.setMarketObj(objMarket);
        objEachRow.setMarketObjectExist(true);
    }

    /**
     * Method to market of sale calculation.
     *
     * @return void
     * @since DSM 2018x.5
     */
    public void calculateOverAllClearanceStatus() {
        List<EachRow> rows = excel.getRows();
        COSRequest objCOSRequestNew;
        Map<String, COSRequest> argMap;
        COSRequest objCOSRequest;
        for (EachRow objEachRow : rows) {
            objCOSRequest = objEachRow.getCosRequest();
            argMap = new HashMap<>();
            argMap.put(MarketClearanceConstant.CONST_COS_REQUEST.getValue(), objCOSRequest);
            try {
                objCOSRequestNew = JPO.invoke(context, MarketClearanceConstant.JPO_COUNTRY_CLEARANCE.getValue(), null, MarketClearanceConstant.METHOD_CALCULATE_COUNTRY_CLEARANCE.getValue(), JPO.packArgs(argMap), COSRequest.class);
                String overallClearance = objCOSRequestNew.getOVERALL_CLEARANCE();
                if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                    logger.info("Gcas|" + objEachRow.getGCAS() + "|Market|" + objEachRow.getCOUNTRY_REQUESTED() + "|Overall Clearance Status|" + overallClearance);
                }
                BeanUtils.copyProperties(objCOSRequest, objCOSRequestNew);
                objEachRow.setGCASOverAllClearanceRequestPassed(true);
                objEachRow.setOVERALL_CLEARANCE(objCOSRequest.getOVERALL_CLEARANCE());
                Map<String, String> attributeKeyValueMap = objEachRow.getAttributeKeyValueMap();
                attributeKeyValueMap.put(pgV3Constants.ATTRIBUTE_PGOVERALLCLEARANCESTATUS, objEachRow.getOVERALL_CLEARANCE());
            } catch (IllegalAccessException | InvocationTargetException | MatrixException e) {
                objEachRow.setGCASOverAllClearanceRequestPassed(false);
                excel.setPassed(false);
                excel.setPassedClearanceStatusCalculation(false);
                objEachRow.setCalculateClearanceStatusErrorMessage(e.getLocalizedMessage());
                logger.log(Level.WARNING, null, e);
                continue;
            }
        }
    }

    /**
     * Method to connect gcas and country and update rel attributes
     *
     * @param context    - Context - context
     * @param objEachRow - EachRow  - bean object
     * @param gcasObj    - DomainObject  - domain object
     * @param marketId   - String  - object id
     * @return void
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public void connectMarketWithGCASUsingPushContext(Context context, EachRow objEachRow, DomainObject gcasObj, String marketId, String marketName) throws FrameworkException {
        boolean isContextPushed = false;
        DomainRelationship objRelationship;
        try {
            if (policyProductionFormulationPart.equals(objEachRow.getPolicy())) {
                ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, null, context.getVault().getName());
                isContextPushed = true;
            }
            objRelationship = gcasObj.addToObject(context, new RelationshipType(pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE), marketId);
            objRelationship.setAttributeValues(context, objEachRow.getAttributeKeyValueMap());
            if (isContextPushed) {
                ContextUtil.popContext(context);
                isContextPushed = false;
            }
            objEachRow.setRecordProcessed(true);
            if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
                logger.info("Connection established with - Gcas|" + objEachRow.getGCAS() + "|and Market|" + marketName);
            }
        } catch (FrameworkException e) {
            e.printStackTrace();
        } finally {
            if (isContextPushed) {
                ContextUtil.popContext(context);
            }
        }
    }

    /**
     * Method to connect gcas and country and update rel attributes
     *
     * @return void
     * @throws FrameworkException - exception
     * @since DSM 2018x.5
     */
    public void connectMarketWithGCAS() throws FrameworkException {
        boolean isPassed = excel.isPassed();
        if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Proceed to connect GCAS & Market|" + isPassed);
        }
        List<EachRow> rows = excel.getRows();
        DomainObject gcasObj = DomainObject.newInstance(context);
        Market marketObj;

        String gcasId;
        String marketId;
        String marketName;
        for (EachRow objEachRow : rows) {
            gcasId = objEachRow.getId();
            marketObj = objEachRow.getMarketObj();
            marketId = marketObj.getId();
            marketName = marketObj.getName();

            if (!objEachRow.isCountryAlreadyConnected()) {
                try {
                    gcasObj.setId(gcasId);
                    if (!isMarketConnected(gcasObj, marketId)) {
                        connectMarketWithGCASUsingPushContext(context, objEachRow, gcasObj, marketId, marketName);
                        recordHistory(gcasId, marketName);
                        excel.setHistoryRecorded(true);
                    } else {
                        objEachRow.setCountryAlreadyConnected(true);
                        objEachRow.setRecordProcessed(false);
                        objEachRow.setRecordFailureMessage(marketName + pgV3Constants.SYMBOL_COMMA + MarketClearanceConstant.MARKET_IS_ALREADY_CONNECTED.getValue());
                    }
                } catch (FrameworkException e) {
                    excel.setHistoryRecorded(false);
                    objEachRow.setRecordProcessed(false);
                    objEachRow.setRecordFailureMessage(e.getMessage());
                    logger.log(Level.WARNING, null, e);
                    continue;
                }
            }
        }
    }

    /**
     * Method to check calulate execution time
     *
     * @return String - string
     * @since DSM 2018x.5
     */
    public String getExecutionTime() {
        Instant startTime = excel.getExecStartTime();
        Duration duration = Duration.between(startTime, Instant.now());
        long seconds = duration.getSeconds();
        StringBuilder sMessageBuilder = new StringBuilder();
        sMessageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sMessageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sMessageBuilder.append(MarketClearanceConstant.EXECUTION_TIME.getValue());
        sMessageBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sMessageBuilder.append(seconds);
        sMessageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        sMessageBuilder.append(MarketClearanceConstant.SECONDS.getValue());
        sMessageBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sMessageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        return sMessageBuilder.toString();
    }

    /**
     * Method to format email message
     *
     * @param message - String
     * @return String - string
     * @since DSM 2018x.5
     */
    public String getEmailMessage(String message) {
        StringBuilder sMessageBuilder = new StringBuilder();
        sMessageBuilder.append(message);
        sMessageBuilder.append(pgV3Constants.SYMBOL_NEXT_LINE);
        sMessageBuilder.append(excel.getExcelProcessMessage());
        sMessageBuilder.append(getExecutionTime());
        return sMessageBuilder.toString();
    }

    /**
     * Method to send email.
     *
     * @param filePath - String file path
     * @param subject  - String subject string
     * @param message  - String body string
     * @since DSM 2018x.5
     */
    public void sendMail(String workspace, String filePath, String subject, String message, String adminEmail) {
        try {
            String[] arguments = new String[7];
            arguments[0] = PropertyUtil.getEnvironmentProperty(context, MarketClearanceConstant.SMTP_HOST.getValue()); //host
            arguments[1] = PersonUtil.getEmail(context, pgV3Constants.PERSON_USER_AGENT); //FROM user
            arguments[2] = adminEmail; //TO user
            arguments[3] = filePath; //fileAttachment
            arguments[4] = subject; // subject
            arguments[5] = getEmailMessage(message); // message
            arguments[6] = PersonUtil.getFullName(context, pgV3Constants.PERSON_USER_AGENT); //system properties
            if (sendMail(arguments)) {
                cleanupWorkspace(workspace);
                logger.info("Market Clearance Email sent to support team");
            } else {
                logger.info("Unable to send Market Clearance Email to support team");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, null, e);
        }
    }

    /**
     * Method to cleanup workspace folder.
     *
     * @param workspace - String
     * @return void -
     * @throws IOException - exception
     * @since DSM 2018x.5
     */
    public void cleanupWorkspace(String workspace) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(new File(workspace));
        if (isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Upload Market Clearance Workspace cleaned");
        }
    }

    /**
     * Method to send email.
     *
     * @param args - string array of arguments
     * @return boolean - true/false
     * @throws Exception - exception
     * @since DSM 2018x.5
     */
    public boolean sendMail(String[] args) throws Exception {
        String sFromUser = args[1]; // from user
        String sToUser = args[2]; // to user
        String sAttachment = args[3]; // attachment
        String sSubject = args[4]; // subject
        String sMessage = args[5]; // message
        String sUserFullName = args[6]; // properties
        if(isLoggingEnabled && logger.isLoggable(Level.INFO)) {
            logger.info("Upload Market Clearance Notification email address|" +sToUser);
            logger.info("Upload Market Clearance Excel File Path|" +sAttachment);
            logger.info(sMessage);
        }
        MxMessageSupport msgSupport = new MxMessageSupport();
        msgSupport.getSendMailInfo(context);
        Properties localProperties = new Properties();
        localProperties.put(MarketClearanceConstant.MAIL_SMTP_HOST.getValue(), msgSupport.getSmtpHost());
        Session localSession = Session.getInstance(localProperties, null);
        MimeMessage localMimeMessage = new MimeMessage(localSession);
        localMimeMessage.setFrom(new InternetAddress(sFromUser, sUserFullName));
        if (sToUser != null && sToUser.contains(","))
            localMimeMessage.addRecipients(RecipientType.TO, InternetAddress.parse(sToUser));
        else {
            localMimeMessage.addRecipient(RecipientType.TO, new InternetAddress(Objects.requireNonNull(sToUser)));
        }
        localMimeMessage.setSubject(sSubject);
        MimeBodyPart localMimeBodyPart = new MimeBodyPart();
        localMimeBodyPart.setText(sMessage);
        MimeMultipart localMimeMultipart = new MimeMultipart();
        localMimeMultipart.addBodyPart(localMimeBodyPart);
        if (sAttachment != null && !"".equals(sAttachment)) {
            localMimeBodyPart = new MimeBodyPart();
            FileDataSource localFileDataSource = new FileDataSource(sAttachment);
            localMimeBodyPart.setDataHandler(new DataHandler(localFileDataSource));
            String str8 = "";
            if (sAttachment.contains("/")) {
                str8 = sAttachment.substring(sAttachment.lastIndexOf('/') + 1);
            }
            localMimeBodyPart.setFileName(str8);
            localMimeMultipart.addBodyPart(localMimeBodyPart);
        }
        localMimeMessage.setContent(localMimeMultipart);
        boolean blnSent;
        try {
            Transport.send(localMimeMessage);
            blnSent = true;
        } catch (Exception e) {
            blnSent = false;
        }
        return blnSent;
    }

    public StringList getGcasBusSelects() {
        return StringList.create(
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_TYPE,
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_REVISION,
                DomainConstants.SELECT_MODIFIED,
                DomainConstants.SELECT_OWNER,
                DomainConstants.SELECT_CURRENT,
                DomainConstants.SELECT_POLICY,
                DomainConstants.SELECT_VAULT,
                MarketClearanceConstant.SELECT_LAST.getValue(),
                DomainConstants.SELECT_LAST_ID,
                pgV3Constants.SELECT_LAST_CURRENT,
                MarketClearanceConstant.SELECT_LAST_REVISION.getValue(),
                DomainConstants.SELECT_IS_LAST,
                MarketClearanceConstant.SELECT_RELATIONSHIP_PRODUCT_CLEARANCE_OBJECT_NAME.getValue());
    }

    public StringList getMarketBusSelects() {
        return StringList.create(
                DomainConstants.SELECT_ID,
                DomainConstants.SELECT_TYPE,
                DomainConstants.SELECT_NAME,
                DomainConstants.SELECT_REVISION,
                DomainConstants.SELECT_MODIFIED,
                DomainConstants.SELECT_OWNER,
                DomainConstants.SELECT_CURRENT,
                DomainConstants.SELECT_POLICY,
                DomainConstants.SELECT_VAULT,
                MarketClearanceConstant.SELECT_LAST.getValue(),
                DomainConstants.SELECT_LAST_ID,
                pgV3Constants.SELECT_LAST_CURRENT,
                MarketClearanceConstant.SELECT_LAST_REVISION.getValue(),
                DomainConstants.SELECT_IS_LAST);
    }

    /**
     * Method build where clause for gcas search
     *
     * @return void
     * @since DSM 2018x.5
     */
    public String getGcasWhereClause() {
        StringBuilder sWhereClauseBuilder = new StringBuilder();
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sWhereClauseBuilder.append(DomainConstants.SELECT_CURRENT);
        sWhereClauseBuilder.append(pgV3Constants.SYMBOL_EQUALS);
        sWhereClauseBuilder.append(pgV3Constants.STATE_RELEASE);
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sWhereClauseBuilder.append(pgV3Constants.SYMBOL_AND);
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sWhereClauseBuilder.append(DomainConstants.SELECT_REVISION);
        sWhereClauseBuilder.append(pgV3Constants.SYMBOL_EQUALS);
        sWhereClauseBuilder.append(MarketClearanceConstant.SELECT_LAST.getValue());
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sWhereClauseBuilder.append(pgV3Constants.SYMBOL_OR);
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
        sWhereClauseBuilder.append(MarketClearanceConstant.SELECT_NEXT_CURRENT.getValue());
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_NOT_EQUALS.getValue());
        sWhereClauseBuilder.append(pgV3Constants.STATE_RELEASE);
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        sWhereClauseBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
        return sWhereClauseBuilder.toString();
    }

    /**
     * Helper method to record custom history when the GCAS is connected with Country.
     *
     * @param gcasId     - GCAS Id
     * @param marketName - Market Name
     * @return void
     * @throws FrameworkException - exception
     */
    public void recordHistory(String gcasId, String marketName) throws FrameworkException {
        MqlUtil.mqlCommand(context, "modify bus $1 add history $2 comment $3", gcasId, MarketClearanceConstant.CONST_CUSTOM.getValue(), buildCustomHistoryComment(marketName).concat(context.getUser()));
    }

    /**
     * Method to format the Comments for History
     *
     * @param marketName - String Market Name
     * @return String
     */
    private String buildCustomHistoryComment(String marketName) {
        return FrameworkUtil.findAndReplace(MarketClearanceConstant.UPLOAD_MARKET_CLEARANCE_COMMENT.getValue(), MarketClearanceConstant.CONST_MARKET_NAME.getValue(), marketName);
    }

    /**
     * Method to check if gcas and country are already connected
     *
     * @param gcasObj  - DomainObject
     * @param marketId - String Market id
     * @return boolean - true/false
     * @throws FrameworkException - exception
     */
    private boolean isMarketConnected(DomainObject gcasObj, String marketId) throws FrameworkException {
        boolean isConnected = false;
        StringList marketList = gcasObj.getInfoList(context, "from[" + pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE + "].to.id");
        if (marketList != null && !marketList.isEmpty()) {
            isConnected = marketList.contains(marketId);
        }
        return isConnected;
    }
}
