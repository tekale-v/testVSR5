/*
 **   ColumnUtil.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Contains all the checks method configured in Upload Market Clearance XML Page object.
 **
 */
package com.pg.dsm.upload.market.util;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.upload.market.beans.mx.Gcas;
import com.pg.dsm.upload.market.beans.mx.Market;
import com.pg.dsm.upload.market.beans.xl.EachColumn;
import com.pg.dsm.upload.market.beans.xl.EachColumnCheck;
import com.pg.dsm.upload.market.beans.xl.EachRow;
import com.pg.dsm.upload.market.enumeration.MarketClearanceConstant;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.StringList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ColumnUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Context context;
    private final EachRow objEachRow;
    private final EachColumn objEachColumn;
    private final EachColumnCheck objEachColumnCheck;

    /**
     * Constructor
     *
     * @param context            - Context - context
     * @param objEachRow         - EachRow - bean object
     * @param objEachColumn      - EachColumn - bean object
     * @param objEachColumnCheck - EachColumnCheck - bean object
     * @since DSM 2018x.5
     */
    public ColumnUtil(Context context, EachRow objEachRow, EachColumn objEachColumn, EachColumnCheck objEachColumnCheck) {
        this.context = context;
        this.objEachRow = objEachRow;
        this.objEachColumn = objEachColumn;
        this.objEachColumnCheck = objEachColumnCheck;
    }

    /**
     * Method to check if column is required.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isRequired() {
        boolean isPassed = true;
        String columnValue = objEachColumn.getColumnValue();
        if (objEachColumn.isColumnRequired() && UIUtil.isNullOrEmpty(columnValue)) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(objEachColumnCheck.getError());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if same combination of gcas and market and not given in the excel.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isGcasMarketCombinationRepeated() {
        boolean isPassed = true;
        String sGCASMarketCombo = objEachRow.getGCAS() + pgV3Constants.SYMBOL_COMMA + objEachRow.getCOUNTRY_REQUESTED();
        if (objEachRow.isGcasMarketCombinatedRepeated()) {
            isPassed = false;
            StringBuilder sMessageBuilder = new StringBuilder();
            sMessageBuilder.append(MarketClearanceConstant.SYMBOL_OPEN_BRACKET.getValue());
            sMessageBuilder.append(sGCASMarketCombo);
            sMessageBuilder.append(MarketClearanceConstant.SYMBOL_CLOSE_BRACKET.getValue());
            sMessageBuilder.append(pgV3Constants.SYMBOL_SPACE);
            sMessageBuilder.append(objEachColumnCheck.getError());
            objEachColumnCheck.setValidationMessage(sMessageBuilder.toString());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if gps approval status entered value (range) is accepted.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean checkGPSApprovalStatusRange() {
        boolean isPassed = true;
        String sGPSApprovalStatusRange = objEachColumn.getColumnValue();
        if (UIUtil.isNotNullAndNotEmpty(sGPSApprovalStatusRange) && !StringUtil.split(objEachColumn.getColumnRange(), pgV3Constants.SYMBOL_COMMA).contains(sGPSApprovalStatusRange)) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(getMessage());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if gcas length is valid.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isGcasValidLength() {
        boolean isPassed = true;
        String sGcasName = objEachColumn.getColumnValue();
        if (sGcasName.length() > 8) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(getMessage());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if gcas exist
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isGcasExist() {
        boolean isPassed = true;
        if (!objEachRow.isGcasExist()) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(getMessage());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if gcas valid type
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isGcasValidType() {
        boolean isPassed = true;
        if (objEachRow.isGcasObjectExist()) {
            Gcas gcasObj = objEachRow.getGcasObj();
            if (gcasObj != null) {
                String sType = gcasObj.getType();
                if (!StringUtil.split(objEachRow.getAllowedTypes(), pgV3Constants.SYMBOL_COMMA).contains(sType)) {
                    isPassed = false;
                    objEachColumnCheck.setValidationMessage(getMessage());
                }
            }
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if gcas valid policy
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isGcasValidPolicy() {
        boolean isPassed = true;
        if (objEachRow.isGcasObjectExist()) {
            Gcas gcasObj = objEachRow.getGcasObj();
            if (gcasObj != null) {
                String sPolicy = gcasObj.getPolicy();
                if (!StringUtil.split(objEachRow.getAllowedPolicies(), pgV3Constants.SYMBOL_COMMA).contains(sPolicy)) {
                    isPassed = false;
                    objEachColumnCheck.setValidationMessage(getMessage());
                }
            }
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if gcas is released
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isGcasReleased() {
        boolean isPassed = true;
        if (objEachRow.isGcasObjectExist()) {
            Gcas gcasObj = objEachRow.getGcasObj();
            if (gcasObj != null) {
                String currentState = gcasObj.getCurrent();
                if (!pgV3Constants.STATE_RELEASE.equals(currentState)) {
                    isPassed = false;
                    objEachColumnCheck.setValidationMessage(getMessage());
                }
            }
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if country exist
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isMarketExist() {
        boolean isPassed = true;
        if (!objEachRow.isMarketExist()) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(getMessage());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if country is active
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isMarketActive() {
        boolean isPassed = true;
        if (objEachRow.isMarketObjectExist()) {
            Market marketObj = objEachRow.getMarketObj();
            if (!MarketClearanceConstant.POLICY_COUNTRY_STATE_ACTIVE.getState(context, marketObj.getPolicy()).equals(marketObj.getCurrent())) {
                isPassed = false;
                objEachColumnCheck.setValidationMessage(getMessage());
            }
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if country is already connected with gcas
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isMarketAlreadyConnected() {
        boolean isPassed = true;
        if (objEachRow.isMarketObjectExist()) {
            Market marketObj = objEachRow.getMarketObj();
            String marketName = marketObj.getName();
            List<String> existingCountries = objEachRow.getExistingCountries();
            if (null != existingCountries && existingCountries.contains(marketName)) {
                isPassed = false;
                objEachColumnCheck.setValidationMessage(getMessage());
            }
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if GPS Approval Status valid
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean validateGPSApprovalStatus() {
        boolean isPassed = true;
        String sGPSApprovalStatus = objEachColumn.getColumnValue();
        if (MarketClearanceConstant.NOT_APPROVED.getValue().equals(sGPSApprovalStatus)) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(objEachColumnCheck.getError());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if Clearance Number valid
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean validateClearanceNumber() {
        boolean isPassed = true;
        String clearanceNumber = objEachColumn.getColumnValue();
        if (Integer.parseInt(clearanceNumber) > 0) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(objEachColumnCheck.getError());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if entered value (range) is accepted.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean checkRegistrationStatusRange() {
        boolean isPassed = true;
        String sRegistrationStatus = objEachColumn.getColumnValue();
        if (UIUtil.isNotNullAndNotEmpty(sRegistrationStatus) && !StringUtil.split(objEachColumn.getColumnRange(), pgV3Constants.SYMBOL_COMMA).contains(sRegistrationStatus)) {
            isPassed = false;
            objEachColumnCheck.setValidationMessage(getMessage());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to get accepted date formats
     *
     * @param acceptedFormats - String
     * @return List<DateFormat> - list
     * @since DSM 2018x.5
     */
    public List<DateFormat> getRegistrationExpirationAcceptedDateFormats(String acceptedFormats) {
        StringList acceptedFormatList = StringUtil.split(acceptedFormats, pgV3Constants.SYMBOL_COMMA);
        List<DateFormat> dateFormats = new ArrayList<>();
        for (String expirationDateFormat : acceptedFormatList) {
            dateFormats.add(new SimpleDateFormat(expirationDateFormat));
        }
        return dateFormats;
    }

    /**
     * Method to find the date format format of expiration date.
     *
     * @param inputValue - String
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    public Date getRegistrationExpirationDate(String inputValue) {
        List<DateFormat> acceptedDateFormats = getRegistrationExpirationAcceptedDateFormats(objEachColumn.getColumnFormats());
        Date expirationDate = null;
        for (DateFormat acceptedDateFormat : acceptedDateFormats) {
            try {
                expirationDate = acceptedDateFormat.parse(inputValue);
                break;
            } catch (Exception e) {
                continue;
            }
        }
        return expirationDate;
    }

    /**
     * Method to find the date format format of expiration date.
     *
     * @param inputDate - Date
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    public String getRegistrationExpirationDateInRequiredFormat(Date inputDate) {
        String inputDateString = DomainConstants.EMPTY_STRING;
        DateFormat requiredDateFormat = new SimpleDateFormat(objEachColumn.getColumnFormat());
        try {
            inputDateString = requiredDateFormat.format(inputDate);
        } catch (Exception e) {
            logger.log(Level.WARNING, null, e);
        }
        return inputDateString;
    }

    /**
     * Method to check if date is expired.
     *
     * @param inputValue - String
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    public boolean isDateExpired(String inputValue) {
        boolean isPassed = false;
        try {
            DateFormat requiredDateFormat = new SimpleDateFormat(MarketClearanceConstant.DATE_FORMAT.getValue());
            if (requiredDateFormat.parse(inputValue).before(requiredDateFormat.parse(requiredDateFormat.format(new Date())))) {
                isPassed = true;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, null, e);
        }
        return isPassed;
    }

    /**
     * Method to check if Registration Expiration Date format.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean isRegistrationDateExpired() {
        boolean isPassed = true;
        String registrationExpirationDate = objEachColumn.getColumnValue();
        if (UIUtil.isNotNullAndNotEmpty(registrationExpirationDate)) {
            try {
                if (isDateExpired(registrationExpirationDate)) {
                    isPassed = false;
                    objEachColumnCheck.setValidationMessage(getMessage());
                }
            } catch (Exception e) {
                isPassed = false;
                logger.log(Level.WARNING, null, e);
            }
        }
        return isPassed;
    }

    /**
     * Method to check if Registration Expiration Date format.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean checkRegistrationExpirationDateFormat() {
        boolean isPassed = true;
        String registrationExpirationDateFormat = objEachColumn.getColumnValue();
        if (UIUtil.isNotNullAndNotEmpty(registrationExpirationDateFormat)) {
            try {
                Date inputDate = getRegistrationExpirationDate(registrationExpirationDateFormat);
                if (null != inputDate) {
                    String inputDateString = getRegistrationExpirationDateInRequiredFormat(inputDate);
                    if (UIUtil.isNotNullAndNotEmpty(inputDateString)) {
                        // update the converted date value only if it is not equal to the entered value.
                        updateRegistrationExpirationDateFormat(inputDateString, registrationExpirationDateFormat);
                    } else {
                        isPassed = false;
                    }
                } else {
                    isPassed = false;
                }
            } catch (Exception e) {
                isPassed = false;
                logger.log(Level.WARNING, null, e);
            }
        }
        if (!isPassed) {
            objEachColumnCheck.setValidationMessage(getMessage());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    public void updateRegistrationExpirationDateFormat(String inputDateString, String registrationExpirationDateFormat) {
        if (!inputDateString.equals(registrationExpirationDateFormat) && !inputDateString.contains(registrationExpirationDateFormat)) {
            objEachRow.getAttributeKeyValueMap().put(pgV3Constants.ATTRIBUTE_PGREGISTRATIONENDDATE, inputDateString);
            objEachColumn.setColumnValue(inputDateString);
        }
    }

    /**
     * Method to check if Registration Expiration Date format.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    @SuppressWarnings({"deprecation"})
    protected boolean checkRegistrationExpirationDateFormat(boolean flag) {
        boolean isPassed = true;
        String registrationExpirationDateFormat = objEachColumn.getColumnValue();
        if (UIUtil.isNotNullAndNotEmpty(registrationExpirationDateFormat)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                String inputRegistrationExpirationDateFormat = simpleDateFormat.format(new Date(registrationExpirationDateFormat));
                Date requiredRegistrationExpirationDate = simpleDateFormat.parse(registrationExpirationDateFormat.trim());
                String requiredRegistrationExpirationDateFormat = simpleDateFormat.format(requiredRegistrationExpirationDate);
                if (!requiredRegistrationExpirationDateFormat.equals(inputRegistrationExpirationDateFormat)) {
                    isPassed = false;
                }
            } catch (Exception e) {
                isPassed = false;
                logger.log(Level.WARNING, null, e);
            }
            if (!isPassed) {
                objEachColumnCheck.setValidationMessage(getMessage());
            }
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Method to check if gps approval status entered value (range) is accepted.
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected boolean checkRegistrationRenewalLeadTimeInteger() {
        boolean isPassed = true;
        String registrationRenewalLeadTime = objEachColumn.getColumnValue();
        if (UIUtil.isNotNullAndNotEmpty(registrationRenewalLeadTime)) {
            try {
                Integer.parseInt(registrationRenewalLeadTime);
            } catch (NumberFormatException e) {
                isPassed = false;
                logger.log(Level.WARNING, null, e);
            }
        }
        if (!isPassed) {
            objEachColumnCheck.setValidationMessage(getMessage());
        }
        objEachColumnCheck.setValidationStatus(isPassed);
        return isPassed;
    }

    /**
     * Utility method to build message
     *
     * @return boolean - true/false
     * @since DSM 2018x.5
     */
    protected String getMessage() {
        StringBuilder sMessageBuilder = new StringBuilder();
        sMessageBuilder.append(objEachColumn.getColumnValue());
        sMessageBuilder.append(pgV3Constants.SYMBOL_COMMA);
        sMessageBuilder.append(pgV3Constants.SYMBOL_SPACE);
        sMessageBuilder.append(objEachColumnCheck.getError());
        return sMessageBuilder.toString();
    }
}