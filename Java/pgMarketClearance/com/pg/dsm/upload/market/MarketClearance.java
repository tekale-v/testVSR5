/*
 **   MarketClearance.java
 **   Description - Introduced as part of Upload Market Clearance feature - 18x.5.
 **   Entry point for processing market clearance.
 **
 */
package com.pg.dsm.upload.market;

import com.matrixone.apps.domain.util.FrameworkException;
import com.pg.dsm.upload.market.beans.mx.MarketConfig;
import com.pg.dsm.upload.market.beans.xl.EachRow;
import com.pg.dsm.upload.market.beans.xl.Excel;
import com.pg.dsm.upload.market.beans.xml.XML;
import com.pg.dsm.upload.market.enumeration.MarketClearanceConstant;
import com.pg.dsm.upload.market.util.ExcelUtil;
import com.pg.dsm.upload.market.util.MarketClearanceUtil;
import com.pg.dsm.upload.market.util.MatrixUtil;
import matrix.db.Context;
import matrix.util.MatrixException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarketClearance {

    private static final Logger logger = Logger.getLogger(MarketClearance.class.getName());
    Context context;
    String path;
    String workspace;
    XML xml;
    Excel excel;
    ExcelUtil excelUtil;
    MarketClearanceUtil marketClearanceUtil;
    MatrixUtil matrixUtil;
    boolean isLoggingEnabled;
    MarketConfig marketConfig;
    Instant execStartTime;

    /**
     * Default Constructor
     *
     * @since DSM 2018x.5
     */
    public MarketClearance(Context context, String[] args) {
    }

    /**
     * Constructor
     *
     * @param context - Context
     * @param path    - String
     * @since DSM 2018x.5
     */
    public MarketClearance(Context context, String path) throws JAXBException, MatrixException {
        this.execStartTime = Instant.now();
        this.context = context;
        this.path = path;
        getWorkspace();
        this.marketClearanceUtil = new MarketClearanceUtil();
        getConfigPage(context);
        getConfigObject(context);
        this.isLoggingEnabled = Boolean.parseBoolean(marketConfig.getConfigCommonIsActive());
        this.excelUtil = new ExcelUtil(isLoggingEnabled);
    }

    /**
     * Helper method for background job.
     *
     * @since DSM 2018x.5
     */
    public void perform(Context context) {
        try {
            perform();
        } catch (Exception e) {
            logger.log(Level.WARNING, null, e);
        }
        logger.info(marketClearanceUtil.getExecutionTimeString(excel.getExecStartTime()));
    }

    /**
     * Method to perform market clearance.
     *
     * @since DSM 2018x.5
     */
    public void perform() {
        try {
            readExcel();
            searchGcasAndMarket();
            performExcelChecks();
            if (excel.isPassed()) {
                calculateOverAllClearanceStatus();
                if (excel.isPassed()) {
                    connectMarketWithGCAS();
                    createProcessedExcel();
                    if (excel.isProcessedFileCreated()) {
                        sendSuccessMail();
                    }
                } else {
                    createErrorExcel(workspace, true);
                    if (excel.isErrorFileCreated()) {
                        sendErrorMail();
                    }
                }
            } else {
                createErrorExcel(workspace, false);
                if (excel.isErrorFileCreated()) {
                    sendErrorMail();
                }
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | ClassNotFoundException | IOException | InvocationTargetException | FrameworkException e) {
            logger.log(Level.WARNING, null, e);
            sendExceptionMail(e.getMessage());
        }
    }

    /**
     * Method to read uploaded market clearance excel data into bean.
     *
     * @throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException -  exception
     * @since DSM 2018x.5
     */
    public void readExcel() throws IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String approxUploadTime = excelProcessStartTime();
        String allowedTypes = marketClearanceUtil.getConfiguredTypes(context, xml.getTypes());
        String allowedPolicies = marketClearanceUtil.getConfiguredPolicies(context, xml.getPolicies());
        List<EachRow> rows;
        rows = readExcel(allowedTypes, allowedPolicies);
        this.excel = new Excel.Builder(context, xml, allowedTypes, allowedPolicies, path, rows).build();
        excel.setExcelProcessStartTime(approxUploadTime);
        excel.setExecStartTime(execStartTime);
    }

    /**
     * Method to read excel into bean.
     *
     * @param sTypes    - String - types allowed
     * @param sPolicies - String - policies
     * @return List<EachRow> - list of EachRow bean objects
     * @throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException - exception
     * @since DSM 2018x.5
     */
    public List<EachRow> readExcel(String sTypes, String sPolicies) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, IOException {
        return excelUtil.readExcel(context, xml, sTypes, sPolicies, path);
    }

    /**
     * Method to perform prechecks on uploaded market clearance excel file.
     *
     * @return String
     * @since DSM 2018x.5
     */
    public String getExcelPreChecks() throws IOException {
        return excelUtil.getExcelPreChecks(context, xml, path, true);
    }

    /**
     * Method to search gcas & market and load it in bean.
     *
     * @throws IllegalAccessException, FrameworkException, InvocationTargetException -  exception
     * @since DSM 2018x.5
     */
    public void searchGcasAndMarket() throws IllegalAccessException, FrameworkException, InvocationTargetException {
        Instant start = Instant.now();
        this.matrixUtil = new MatrixUtil(context, excel, isLoggingEnabled);
        matrixUtil.searchGcasAndMarket();
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to perform all the checks configured in upload market clearance xml page.
     *
     * @throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException -  exception
     * @since DSM 2018x.5
     */
    public void performExcelChecks() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Instant start = Instant.now();
        excelUtil.performExcelChecks(context, excel);
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to create processed excel report
     *
     * @throws IOException -  exception
     * @since DSM 2018x.5
     */
    public void createProcessedExcel() throws IOException {
        Instant start = Instant.now();
        excelUtil.createProcessedExcel(context, excel, workspace);
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to perform all the checks configured in upload market clearance xml page.
     *
     * @param workspace - String
     * @param flag      - boolean
     * @throws IOException -  exception
     * @since DSM 2018x.5
     */
    public void createErrorExcel(String workspace, boolean flag) throws IOException {
        Instant start = Instant.now();
        excelUtil.createErrorExcel(context, excel, workspace, flag);
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to print validation report
     *
     * @since DSM 2018x.5
     */
    public void logValidationReport() {
        excelUtil.logValidationReport(excel);
    }

    /**
     * Method to calculate overall clearance status.
     *
     * @since DSM 2018x.5
     */
    public void calculateOverAllClearanceStatus() {
        Instant start = Instant.now();
        matrixUtil.calculateOverAllClearanceStatus();
        if (isLoggingEnabled) {
            logValidationReport();
        }
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to connect and market object.
     *
     * @since DSM 2018x.5
     */
    public void connectMarketWithGCAS() throws FrameworkException {
        Instant start = Instant.now();
        matrixUtil.connectMarketWithGCAS();
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to convert matrix page object into java bean object
     *
     * @param context - matrix Context
     * @return void
     * @since DSM 2018x.5
     */
    public void getConfigPage(Context context) throws JAXBException, MatrixException {
        Instant start = Instant.now();
        this.xml = marketClearanceUtil.getConfigPage(context);
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to convert matrix page object into java bean object
     *
     * @param context - matrix Context
     * @return void
     * @since DSM 2018x.5
     */
    public void getConfigObject(Context context) throws FrameworkException {
        Instant start = Instant.now();
        this.marketConfig = marketClearanceUtil.getConfigObject(context);
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to capture excel upload date/time.
     *
     * @return String
     * @since DSM 2018x.5
     */
    public String excelProcessStartTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(MarketClearanceConstant.LONG_DATE_FORMAT.getValue());
        return sdf.format(new Date());
    }

    /**
     * Method to get workspace directory path.
     *
     * @return String
     * @since DSM 2018x.5
     */
    public void getWorkspace() {
        File file = new File(this.path);
        this.workspace = file.getAbsoluteFile().getParent();
    }

    /**
     * Method to send error mail
     *
     * @since DSM 2018x.5
     */
    public void sendErrorMail() {
        Instant start = Instant.now();
        matrixUtil.sendMail(workspace, excel.getErrorFilePath(), excel.getValidationErrorEmailSubject(), excel.getValidationErrorEmailBody(), marketConfig.getConfigCommonAdminMailId());
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to send success mail
     *
     * @since DSM 2018x.5
     */
    public void sendSuccessMail() {
        Instant start = Instant.now();
        matrixUtil.sendMail(workspace, excel.getProcessedFilePath(), excel.getProcessedEmailSubject(), excel.getProcessedEmailBody(), marketConfig.getConfigCommonAdminMailId());
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }

    /**
     * Method to send exception mail
     *
     * @since DSM 2018x.5
     */
    public void sendExceptionMail(String exception) {
        Instant start = Instant.now();
        matrixUtil.sendMail(workspace, path, MarketClearanceConstant.GENERAL_EXCEPTION_SUBJECT.getValue(), MarketClearanceConstant.GENERAL_EXCEPTION_MESSAGE.getValue() + exception, marketConfig.getConfigCommonAdminMailId());
        logger.info(marketClearanceUtil.getExecutionTimeString(start));
    }
}
