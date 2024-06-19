/*
 * Added by (DSM-Sogeti) for 22x.02 Defect - 52113
 *
 **/
package com.pg.dsm.ui.table.market_clearance;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.util.StringList;
import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MarketClearanceView {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * @param context
     * @param objectId
     * @param type
     * @return
     * @throws FrameworkException
     */
    public String getMarketClearanceHtmlView(Context context, String objectId, String type) throws FrameworkException {
        String marketClearanceHtmlView = DomainConstants.EMPTY_STRING;
        if (UIUtil.isNotNullAndNotEmpty(objectId)) {
            MapList objectList = getMarketClearanceData(context, objectId);
            if (null != objectList && !objectList.isEmpty()) {
                objectList.sort(DomainConstants.SELECT_NAME, "ascending", "String");
                Iterator iterator = objectList.iterator();
                Map<Object, Object> objectMap;
                List<MarketClearanceRow> marketClearanceRowList = new ArrayList<>();
                List<MarketClearanceRow> tempList;
                while (iterator.hasNext()) {
                    objectMap = (Map<Object, Object>) iterator.next();
                    tempList = getMarketClearanceRowList(objectMap);
                    if (null != tempList && !tempList.isEmpty()) {
                        marketClearanceRowList.addAll(tempList);
                    }
                }
                if (!marketClearanceRowList.isEmpty() && UIUtil.isNotNullAndNotEmpty(type)) {
                    String tableHeaderHtml = getMarketClearanceTableHeaderHtml(type);
                    if (UIUtil.isNotNullAndNotEmpty(tableHeaderHtml)) {
                        marketClearanceHtmlView = getMarketClearanceTableRowDataHtml(getMarketClearanceTable(marketClearanceRowList), tableHeaderHtml, type);
                    } else {
                        logger.log(Level.WARNING, "Market Clearance - Header Html is null/empty {0}", tableHeaderHtml);
                    }
                } else {
                    logger.log(Level.WARNING, "Market Clearance - List<MarketClearanceRow> is null/empty {0}", marketClearanceRowList);
                }
            } else {
                logger.log(Level.WARNING, "Market Clearance - MapList is null/empty {0}", objectList);
            }
        } else {
            logger.log(Level.WARNING, "Market Clearance - Object ID is null/empty {0}", objectId);
        }
        logger.log(Level.INFO, marketClearanceHtmlView);
        return marketClearanceHtmlView;
    }


    /**
     * @param context
     * @param args
     */
    public void unitTest(Context context, String[] args) {
        try {
            String name = DomainConstants.QUERY_WILDCARD;
            String type = pgV3Constants.TYPE_ASSEMBLEDPRODUCTPART + "," + pgV3Constants.TYPE_DEVICEPRODUCTPART;
            if (args.length > 0) {
                type = args[0];
                name = args[1];
            }
            // eval expr COUNT=TRUE on temp query bus "pgAssembledProductPart,pgDeviceProductPart" * * where "relationship==pgProductCountryClearance";
            String where = "relationship==" + pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE;
            MapList mapList = DomainObject.findObjects(
                    context,//Context context,
                    type, //String typePattern,
                    name, //String namePattern,
                    DomainConstants.QUERY_WILDCARD, //String revPattern,
                    DomainConstants.QUERY_WILDCARD, //String ownerPattern,
                    DomainConstants.QUERY_WILDCARD, //String vaultPattern,
                    where.toString(), //String whereExpression,
                    false, //boolean expandType,
                    StringList.create("id", "name", "revision", "type"));//StringList objectSelects

            if (null != mapList && !mapList.isEmpty()) {
                Map<Object, Object> objectMap;
                String id;
                String oType;
                String oName;
                String oRevision;
                for (int i = 0; i < mapList.size(); i++) {
                    objectMap = (Map<Object, Object>) mapList.get(i);
                    id = (String) objectMap.get("id");
                    oType = (String) objectMap.get("type");
                    oName = (String) objectMap.get("name");
                    oRevision = (String) objectMap.get("revision");
                    logger.log(Level.INFO, "Processing Object: {0} | {1} | {2}", new Object[]{oType, oName, oRevision});
                    getMarketClearanceHtmlView(context, id, oType);
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: " + e);
        }
    }

    /**
     * @param context
     * @param args
     */
    public void getMarketClearanceHtmlView(Context context, String[] args) {
        try {
            if (args.length > 0) {
                String type = args[0];
                String id = args[1];
                if (UIUtil.isNotNullAndNotEmpty(type) && UIUtil.isNotNullAndNotEmpty(id)) {
                    String htmlTableView = getMarketClearanceHtmlView(context, id, type);
                    logger.log(Level.INFO, "Market Clearance Table Html {0}", htmlTableView);
                }
            } else {
                logger.log(Level.INFO, "Please supply arguments (type, objectId)");
            }
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Error: " + e);
        }
    }

    /**
     * @param objectMap
     * @return
     */
    public MarketClearanceTable getMarketClearanceTable(Map<Object, Object> objectMap) {
        List<MarketClearanceRow> marketClearanceRowList = getMarketClearanceRowList(objectMap);
        MarketClearanceTable marketClearanceTable = new MarketClearanceTable(marketClearanceRowList);
        return marketClearanceTable;
    }

    public MarketClearanceTable getMarketClearanceTable(List<MarketClearanceRow> marketClearanceRowList) {
        MarketClearanceTable marketClearanceTable = new MarketClearanceTable(marketClearanceRowList);
        logger.log(Level.INFO, "Market Clearance - Bean: {0}", marketClearanceTable);
        return marketClearanceTable;
    }

    /**
     * @param objectMap
     * @return
     */
    private List<MarketClearanceRow> getMarketClearanceRowList(Map<Object, Object> objectMap) {
        List<MarketClearanceRow> marketClearanceRowList = new ArrayList<>();
        try {
            String marketName = (String) objectMap.get(DomainConstants.SELECT_NAME);
            String overallClearanceStatus = (String) objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGOVERALLCLEARANCESTATUS);
            StringList approvalStatusList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPSRAAPPROVALSTATUS));
            StringList cTNumberList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCTNUMBER));
            StringList productRegulatoryClassList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTREGULATORYCLASSIFICATION));
            StringList marketApprovalHolderList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMARKETAPPROVERHOLDER));
            StringList businessChannelList = getStringListFromObject(objectMap.get("attribute[" + pgV3Constants.ATTRIBUTE_PGBUSINESSCHANNEL + "]"));
            StringList legalEntityList = getStringListFromObject(objectMap.get("attribute[" + pgV3Constants.ATTRIBUTE_PGLEGALENTITY + "]"));
            StringList productRegistrationNumberList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCOUNTRYPRODUCTREGISTRATIONNUMBER));
            StringList registrationStatusList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONSTATUS));
            StringList registrationEndDateList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONENDDATE));
            StringList registrationRenewalLeadTimeList = getStringListFromObject(objectMap.get("attribute[" + pgV3Constants.ATTRIBUTE_REGISTRATIONRENEWALLEADTIME + "]"));
            StringList registrationRenewalStatusList = getStringListFromObject(objectMap.get("attribute[" + pgV3Constants.ATTRIBUTE_REGISTRTATIONRENEWALSTATUS + "]"));
            StringList registeredProductNameList = getStringListFromObject(objectMap.get("attribute[" + pgV3Constants.ATTRIBUTE_REGISTEREDPRODUCTNAME + "]"));
            StringList plantRestrictionList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTRESTRICTION));
            StringList clearanceCommentList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGCLEARANCECOMMENT));
            StringList packingSiteList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGSITE));
            StringList manufacturingSiteList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGMANUFACTURINGSITE));
            StringList packSizeList = getStringListFromObject(objectMap.get(pgV3Constants.SELECT_ATTRIBUTE_PGPACKSIZE_INPUTVALUE));
            StringList packSizeUoMList = getStringListFromObject(objectMap.get("attribute[" + pgV3Constants.ATTRIBUTE_PGPACKSIZEUOM + "]"));

            int iApprovalStatusSize = approvalStatusList.size();
            int iCTNumberSize = cTNumberList.size();
            int iProductRegulatoryClassificationSize = productRegulatoryClassList.size();
            int iMarketApprovalHolderSize = marketApprovalHolderList.size();
            int iBusinessChannelSize = businessChannelList.size();
            int iLegalEntitySize = legalEntityList.size();
            int iProductRegistrationNumber = productRegistrationNumberList.size();
            int iRegistrationStatusSize = registrationStatusList.size();
            int iRegistrationRenewalLeadTimeSize = registrationRenewalLeadTimeList.size();
            int iRegistrationRenewalStatusSize = registrationRenewalStatusList.size();
            int iRegisteredProductNameSize = registeredProductNameList.size();
            int iPlantRestrictionSize = plantRestrictionList.size();
            int iClearanceCommentSize = clearanceCommentList.size();
            int iPackingSiteSize = packingSiteList.size();
            int iRegistrationEndDateSize = registrationEndDateList.size();
            int iManufacturingSiteSize = manufacturingSiteList.size();
            int iPackSize = packSizeList.size();
            int iPackSizeUoMSize = packSizeUoMList.size();

            List<Integer> integerList = java.util.Arrays.asList(
                    iApprovalStatusSize,
                    iCTNumberSize,
                    iProductRegulatoryClassificationSize,
                    iMarketApprovalHolderSize,
                    iBusinessChannelSize,
                    iLegalEntitySize,
                    iProductRegistrationNumber,
                    iRegistrationStatusSize,
                    iRegistrationRenewalLeadTimeSize,
                    iRegistrationRenewalStatusSize,
                    iRegisteredProductNameSize,
                    iPlantRestrictionSize,
                    iClearanceCommentSize,
                    iPackingSiteSize,
                    iRegistrationEndDateSize,
                    iManufacturingSiteSize,
                    iPackSize,
                    iPackSizeUoMSize);

            Integer max = java.util.Collections.max(integerList);
            MarketClearanceRow marketClearanceRow;
            for (int i = 0; i < max; i++) {
                marketClearanceRow = new MarketClearanceRow();
                marketClearanceRow.setRowCount(max);
                marketClearanceRow.setMarketName(marketName);
                marketClearanceRow.setOverallClearanceStatus(overallClearanceStatus);
                marketClearanceRow.setApprovalStatus((iApprovalStatusSize > i) ? cleanData(approvalStatusList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setCtNumber((iCTNumberSize > i) ? cleanData(cTNumberList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setProductRegulatoryClass((iProductRegulatoryClassificationSize > i) ? cleanData(productRegulatoryClassList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setMarketApprovalHolder((iMarketApprovalHolderSize > i) ? cleanData(marketApprovalHolderList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setBusinessChannel((iBusinessChannelSize > i) ? cleanData(businessChannelList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setLegalEntity((iLegalEntitySize > i) ? cleanData(legalEntityList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setProductRegistrationNumber((iProductRegistrationNumber > i) ? cleanData(productRegistrationNumberList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setRegistrationStatus((iRegistrationStatusSize > i) ? cleanData(registrationStatusList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setRegistrationEndDate((iRegistrationEndDateSize > i) ? cleanData(registrationEndDateList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setRegistrationRenewalLeadTime((iRegistrationRenewalLeadTimeSize > i) ? cleanData(registrationRenewalLeadTimeList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setRegistrationRenewalStatus((iRegistrationRenewalStatusSize > i) ? cleanData(registrationRenewalStatusList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setRegisteredProductName((iRegisteredProductNameSize > i) ? cleanData(registeredProductNameList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setPlantRestriction((iPlantRestrictionSize > i) ? cleanData(plantRestrictionList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setClearanceComment((iClearanceCommentSize > i) ? cleanData(clearanceCommentList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setPackingSite((iPackingSiteSize > i) ? cleanData(packingSiteList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setManufacturingSite((iManufacturingSiteSize > i) ? cleanData(manufacturingSiteList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setPackSize((iPackSize > i) ? cleanData(packSizeList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRow.setPackSizeUoM((iPackSizeUoMSize > i) ? cleanData(packSizeUoMList.get(i)) : DomainConstants.EMPTY_STRING);
                marketClearanceRowList.add(marketClearanceRow);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error: " + e);
        }
        return marketClearanceRowList;
    }

    /**
     * @param data
     * @return
     */
    private String cleanData(String data) {
        if (UIUtil.isNotNullAndNotEmpty(data)) {
            data = data.replaceAll("<[/]?br>", ""); // replace <br> and </br> with ""
            data = data.replaceAll("<[/]?br/>", ""); // replace <br/> with ""
        }
        return data;
    }

    private StringList cleanData(Object object) {
        StringList cleanedList = new StringList();
        StringList dataList = new StringList();
        if (object != null) {
            if (object instanceof StringList) {
                dataList = (StringList) object;
                if (null != dataList && !dataList.isEmpty()) {
                    for (String data : dataList) {
                        if (UIUtil.isNotNullAndNotEmpty(data)) {
                            data = data.replaceAll("<[/]?br>", ""); // replace <br> and </br> with ""
                            data = data.replaceAll("<[/]?br/>", ""); // replace <br/> with ""
                            cleanedList.add(data);
                        }
                    }
                }
            } else if (object instanceof String) {
                String data = object.toString();
                data = data.replaceAll("<[/]?br>", ""); // replace <br> and </br> with ""
                data = data.replaceAll("<[/]?br/>", ""); // replace <br/> with ""
                dataList.add(data);
            }
        }
        return cleanedList;
    }


    /**
     * @param object
     * @return
     */
    private static StringList getStringListFromObject(Object object) {
        StringList newList = new StringList();
        if (object != null) {
            if (object instanceof StringList) {
                newList = (StringList) object;
                if (null != newList && newList.isEmpty()) {
                    newList.add(DomainConstants.EMPTY_STRING);
                }
            } else if (object instanceof String) {
                newList.add(object.toString());
            }
        } else {
            newList.add(DomainConstants.EMPTY_STRING);
        }
        return newList;
    }

    /**
     * @param type
     * @return
     */
    private String getMarketClearanceTableHeaderHtml(String type) {
        StringBuilder headerBuilder = new StringBuilder();
        // define html table
        headerBuilder.append("<TR><TD>");
        headerBuilder.append("<TABLE class=\"WordWrappdf\" cellspacing=\"0\" width=\"1000px\" id=\"pgFormulatedProductTable\">");
        headerBuilder.append("<thead>");
        // define html table header.
        headerBuilder.append("<TR><TD class=\"new\" colspan=\"14\" ><font size='4'><b>Market Clearance<br /></b></font></TD></TR>");
        headerBuilder.append("<TR align=\"top\">");
        headerBuilder.append("<TD  width=\"77\" align=\"left\"><B>Market</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Overall Clearance Status</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>GPS Approval Status (GPSAS)<BR/>Clearance Number (CN)</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Product Regulatory Classification (PRC)<BR/>Market Approval Holder (MAH)</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Legal Entity (LE)<BR/>Business Channel<BR/>(BC)</B></TD>");
        if (!pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(type)) {
            headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Pack Size (PS)<BR/>Pack Size UoM(PSU)</B></TD>");
        }
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Market Product<BR/>Registration Number</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Registration Status (Rs)<BR/>Registration Expiration Date (Re)</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Registration Renewal Lead Time(Days) (Rr)<BR/>Registration Renewal Status (Rw)</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Registered Product Name (RPN)<BR/>Restrictions(R)</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>GPS<BR/>Comments</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Packing<BR/>Site</B></TD>");
        headerBuilder.append("<TD  width=\"71\" align=\"left\"><B>Bulk Making /<BR/> Manufacturing Site</B></TD>");
        headerBuilder.append("</TR></thead>");
        return headerBuilder.toString();
    }


    /**
     * @param marketClearanceTable
     * @param tableHeaderEntry
     * @param type
     * @return
     */
    private String getMarketClearanceTableRowDataHtml(MarketClearanceTable marketClearanceTable, String tableHeaderEntry, String type) {
        StringBuilder rowDataBuilder = new StringBuilder(tableHeaderEntry);
        if (null != marketClearanceTable) {
            List<MarketClearanceRow> marketClearanceRowList = marketClearanceTable.getMarketClearanceRowList();
            if (null != marketClearanceRowList && !marketClearanceRowList.isEmpty()) {
                boolean oneTime = false;
                String marketName;
                String tempMarketName = DomainConstants.EMPTY_STRING;
                for (MarketClearanceRow marketClearanceRow : marketClearanceRowList) {
                    rowDataBuilder.append("<TR align=\"center\" class=\"pdf\">");
                    marketName = marketClearanceRow.getMarketName();
                    if (UIUtil.isNullOrEmpty(tempMarketName)) {
                        tempMarketName = marketName;
                        oneTime = false;
                    }
                    if (!tempMarketName.equals(marketName)) {
                        oneTime = false;
                    }
                    if (!oneTime) {
                        rowDataBuilder.append("<TD rowspan=\"" + marketClearanceRow.getRowCount() + "\" align=\"left\" width=\"83\">" + tempMarketName + "</TD>");
                        rowDataBuilder.append("<TD rowspan=\"" + marketClearanceRow.getRowCount() + "\" align=\"left\" width=\"83\">" + (marketClearanceRow.getOverallClearanceStatus()) + "</TD>");
                        oneTime = true;
                    }
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\"><B>GPSAS: </B>" + (marketClearanceRow.getApprovalStatus()) + "<BR></BR><B>CN: </B>" + (marketClearanceRow.getCtNumber()) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\"><B>PRC: </B>" + wrapData(marketClearanceRow.getProductRegulatoryClass(), 10) + "<BR></BR><B>MAH: </B>" + wrapData(marketClearanceRow.getMarketApprovalHolder(), 10) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\"><B>LE: </B>" + wrapData(marketClearanceRow.getLegalEntity(), 10) + "<BR></BR><B>BC: </B>" + wrapData(marketClearanceRow.getBusinessChannel(), 10) + "</TD>");
                    if (!pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(type)) {
                        rowDataBuilder.append("<TD align=\"left\" width=\"83\"><B>PS: </B>" + (marketClearanceRow.getPackSize()) + "<BR></BR><B>PSU: </B>" + (marketClearanceRow.getPackSizeUoM()) + "</TD>");
                    }
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\">" + wrapData(marketClearanceRow.getProductRegistrationNumber(), 10) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\"><B>Rs: </B>" + (marketClearanceRow.getRegistrationStatus()) + "<BR></BR><B>Re: </B>" + (marketClearanceRow.getRegistrationEndDate()) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\"><B>Rr: </B>" + (marketClearanceRow.getRegistrationRenewalLeadTime()) + "<BR></BR><B>Rw: </B>" + (marketClearanceRow.getRegistrationRenewalStatus()) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\"><B>RPN: </B>" + wrapData(marketClearanceRow.getRegisteredProductName(), 10) + "<BR></BR><B>R: </B>" + wrapData(marketClearanceRow.getPlantRestriction(), 10) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\">" + wrapData(marketClearanceRow.getClearanceComment(), 10) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\">" + wrapData(replaceCapSymbolWithComma(marketClearanceRow.getPackingSite()), 10) + "</TD>");
                    rowDataBuilder.append("<TD align=\"left\" width=\"83\">" + wrapData(replaceCapSymbolWithComma(marketClearanceRow.getManufacturingSite()), 10) + "</TD>");
                    rowDataBuilder.append("</TR>");
                }
                rowDataBuilder.append("</TABLE><TR><TD><br /></TD></TR>");
                rowDataBuilder.append("</TD></TR>");
            }
        }
        return rowDataBuilder.toString();
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    private MapList getMarketClearanceData(Context context, String objectOid) throws FrameworkException {
        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        StringList objectSelects = new StringList(1);
        objectSelects.add(DomainConstants.SELECT_NAME);

        StringList relSelects = new StringList(19);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGOVERALLCLEARANCESTATUS);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPSRAAPPROVALSTATUS);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCTNUMBER);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPRODUCTREGULATORYCLASSIFICATION);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMARKETAPPROVERHOLDER);
        relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_PGLEGALENTITY + "]");
        relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_PGBUSINESSCHANNEL + "]");
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKSIZE_INPUTVALUE);
        relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_PGPACKSIZEUOM + "]");
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCOUNTRYPRODUCTREGISTRATIONNUMBER);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONSTATUS);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGREGISTRATIONENDDATE);
        relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_REGISTRATIONRENEWALLEADTIME + "]");
        relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_REGISTRTATIONRENEWALSTATUS + "]");
        relSelects.add("attribute[" + pgV3Constants.ATTRIBUTE_REGISTEREDPRODUCTNAME + "]");
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPLANTRESTRICTION);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCLEARANCECOMMENT);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPACKINGSITE);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMANUFACTURINGSITE);

        return domainObject.getRelatedObjects(
                context, //context
                pgV3Constants.RELATIONSHIP_PGPRODUCTCOUNTRYCLEARANCE, //relPattern
                pgV3Constants.TYPE_COUNTRY, //typePattern
                objectSelects, //objectSelects
                relSelects, //relationshipSelects
                Boolean.FALSE, //getTo
                Boolean.TRUE, //getFrom
                (short) 1,                     //recurseToLevel
                DomainConstants.EMPTY_STRING,    //objectWhere
                DomainConstants.EMPTY_STRING,    //relationshipWhere
                0);//limit
    }

    public static String wrapData(String data, int wLength) {
        return WordUtils.wrap(data, wLength, "<br/>", true);
    }

    private String replaceCapSymbolWithComma(String string) {
        return string.replace('^', ',');
    }
}
