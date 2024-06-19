/*
 **   MaterialGroupUtil.java
 **   Description - Introduced as part June CW 2022 for Material Group Code (MGC) - Requirement (39763, 39765, 39767, 39764)
 **   About - Utility class to get material code from page config.
 **
 */
package com.pg.dsm.sap.mgc.utils;

import com.dassault_systemes.enovia.dcl.DCLServiceUtil;
import com.dassault_systemes.enovia.dcl.service.HistoryAuditTrailService;
import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.CacheUtil;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.EnoviaResourceBundle;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.matrixone.apps.domain.util.PropertyUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.sap.mgc.beans.InclusionType;
import com.pg.dsm.sap.mgc.beans.MaterialGroup;
import com.pg.dsm.sap.mgc.beans.MaterialGroups;
import com.pg.dsm.sap.mgc.beans.Part;
import com.pg.dsm.sap.mgc.enumeration.MaterialGroupCode;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;
import matrix.db.Page;
import matrix.util.MatrixException;
import matrix.util.StringList;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaterialGroupUtil {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public MaterialGroupUtil() {
    }

    /**
     * @param context
     * @param part
     * @return
     * @throws MatrixException
     */
    public String getMaterialGroupCode(Context context, Part part) throws MatrixException {
        String code = DomainConstants.EMPTY_STRING;
        final Instant startTime = Instant.now();
        String pageContent = getPageContentAsString(context);
        Object[] arrInfo = new Object[5];
        arrInfo[0] = part.getType();
        arrInfo[1] = part.getName();
        arrInfo[2] = part.getRevision();
        arrInfo[3] = part.getId();
        logger.log(Level.INFO, "Load MGC Page File for - |{0}|{1}|{2}|{3}|", arrInfo);
        if (UIUtil.isNotNullAndNotEmpty(pageContent)) {
            MaterialGroups materialGroups = loadMaterialGroupCodes(pageContent);
            if (null != materialGroups) {
                StringList symbolicTypeList = StringUtil.split(materialGroups.getApplicableTypes(), pgV3Constants.SYMBOL_COMMA);
                if (symbolicTypeList.contains(part.getSymbolicType())) {
                    logger.log(Level.INFO, "Lookup MGC in Page File for - |{0}|{1}|{2}|{3}|", arrInfo);
                    code = lookup(part, materialGroups);
                }
                arrInfo[4] = code;
            } else {
                logger.log(Level.WARNING, "Failed to Convert MGC Page into Object for - |{0}|{1}|{2}|{3}|", arrInfo);
            }
        } else {
            logger.log(Level.WARNING, "Failed to Load MGC Page File for - |{0}|{1}|{2}|{3}|", arrInfo);
        }
        logger.log(Level.INFO, "MGC Code for - |{0}|{1}|{2}|{3}| is: {4}", arrInfo);
        final Instant endTime = Instant.now();
        final Duration duration = Duration.between(startTime, endTime);
        logger.info("Get MGC Code - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return code;
    }

    /**
     * @param context
     * @param part
     * @param flag
     * @return
     * @throws MatrixException
     */
    public String getMGCCode(Context context, Part part, boolean flag) throws MatrixException {
        String code = DomainConstants.EMPTY_STRING;
        final Instant startTime = Instant.now();
        String pageContent = getPageContentAsString(context);
        if (UIUtil.isNotNullAndNotEmpty(pageContent)) {
            MaterialGroups materialGroups = (MaterialGroups) CacheUtil.getCacheObject(context, "_MaterialGroupCodePageCache");
            if (null == materialGroups) {
                if (loadMaterialGroupCodes(context, pageContent)) {
                    materialGroups = (MaterialGroups) CacheUtil.getCacheObject(context, "_MaterialGroupCodePageCache");
                }
            }
            if (null != materialGroups) {
                code = lookup(part, materialGroups);
            }
        }
        logger.log(Level.INFO, "MGC Code from Page Config: {0}", code);
        final Instant endTime = Instant.now();
        final Duration duration = Duration.between(startTime, endTime);
        logger.info("Get MGC Code - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return code;
    }

    /**
     * @param part
     * @return
     */
    public String getMGCCode(Part part) {
        final Instant startTime = Instant.now();
        final MaterialGroups materialGroups = loadMaterialGroupCodes(DomainConstants.EMPTY_STRING, false);
        String code = lookup(part, materialGroups);
        final Instant endTime = Instant.now();
        final Duration duration = Duration.between(startTime, endTime);
        logger.log(Level.INFO, "MGC Code from Page Config: {0}", code);
        logger.info("Get MGC Code - took|" + duration.toMillis() + " ms|" + duration.getSeconds() + " sec|" + duration.toMinutes() + " min");
        return code;
    }

    /**
     * @param part
     * @param materialGroups
     * @return
     */
    public String lookup(Part part, MaterialGroups materialGroups) {
        return MaterialGroupCode.ApplicableType.getMaterialGroupCodeByType(part, materialGroups);
    }

    /**
     * @param pageContent
     * @return
     */
    public MaterialGroups loadMaterialGroupCodes(String pageContent) {
        MaterialGroups materialGroups = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MaterialGroups.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            materialGroups = (MaterialGroups) unmarshaller.unmarshal(new StringReader(pageContent));
        } catch (JAXBException e) {
            logger.log(Level.WARNING, "Exception occurred - ", e);
        }
        return materialGroups;
    }

    /**
     * @param context
     * @param pageContent
     * @return
     */
    public boolean loadMaterialGroupCodes(Context context, String pageContent) {
        boolean isLoaded = false;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MaterialGroups.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            MaterialGroups materialGroups = (MaterialGroups) unmarshaller.unmarshal(new StringReader(pageContent));
            MaterialGroups materialGroupsCache = (MaterialGroups) CacheUtil.getCacheObject(context, "MaterialGroupCodePageCache");
            if (null == materialGroupsCache) {
                if (null != materialGroups) {
                    CacheUtil.setCacheObject(context, "MaterialGroupCodePageCache", materialGroups);
                    isLoaded = true;
                }
            } else {
                isLoaded = true;
            }
        } catch (JAXBException | FrameworkException e) {
            logger.log(Level.WARNING, "Exception occurred - ", e);
        }
        return isLoaded;
    }


    /**
     * @param path
     * @param flag
     * @return
     */
    public MaterialGroups loadMaterialGroupCodes(String path, boolean flag) {
        MaterialGroups materialGroups = null;
        String xmlContent = readXMLPageAsString(path);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MaterialGroups.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            materialGroups = (MaterialGroups) unmarshaller.unmarshal(new StringReader(xmlContent));
        } catch (JAXBException e) {
            logger.log(Level.WARNING, "Exception occurred - ", e);
        }
        return materialGroups;
    }

    /**
     * @return
     */
    private String readXMLPageAsString(String path) {
        String xmlContent = DomainConstants.EMPTY_STRING;
        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                xmlContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Exception occurred: ", e);
            }
        }
        return xmlContent;
    }

    /**
     * @param context
     * @return
     * @throws MatrixException
     */
    public String getPageContentAsString(Context context) throws MatrixException {
        String content = DomainConstants.EMPTY_STRING;
        boolean isPageOpen = false;
        Page page = null;
        try {
            page = new Page("pgMaterialGroupCodePage");
            page.open(context);
            content = page.getContents(context);
            isPageOpen = true;
        } catch (MatrixException e) {
            logger.log(Level.WARNING, RollupConstants.Basic.EXCEPTION_OCCURRED.getValue(), e);
        } finally {
            if (isPageOpen && null != page) {
                page.close(context);
            }
        }
        return content;
    }

    /**
     * @param part
     * @param materialGroup
     * @param inclusionType
     * @return
     */
    public static boolean evaluateCriteria(Part part, MaterialGroup materialGroup, InclusionType inclusionType) {
        boolean eval = false;
        Set<Boolean> resultSet = new HashSet<>();
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_CLASS_TYPE.evaluate(part, materialGroup, inclusionType));
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_SUB_CLASS_TYPE.evaluate(part, materialGroup, inclusionType));
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_REPORTED_FUNCTION.evaluate(part, materialGroup, inclusionType));
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_PACKAGING_COMPONENT_TYPE.evaluate(part, materialGroup, inclusionType));
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_PACKAGING_MATERIAL_TYPE.evaluate(part, materialGroup, inclusionType));
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_PACKAGING_TECHNOLOGY.evaluate(part, materialGroup, inclusionType));
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_PRIMARY_ORGANIZATION.evaluate(part, materialGroup, inclusionType)); // 22x.04
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_CHEMICAL_GROUP.evaluate(part, materialGroup, inclusionType));
        resultSet.add(MaterialGroupCode.ApplicableAttribute.ATTRIBUTE_CASE_NUMBER.evaluate(part, materialGroup, inclusionType));
        if (resultSet.size() == 2) {//true & false
            eval = false;
        } else { // when size is one.
            if (resultSet.contains(Boolean.TRUE)) {
                eval = Boolean.TRUE;
            }
        }
        return eval;
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    public Part getPartBean(Context context, String objectOid) throws FrameworkException {

        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        StringList busSelects = new StringList();
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_REVISION);
        busSelects.addElement(MaterialGroupCode.Attribute.PG_CLASS.getSelect(context));
        busSelects.addElement(MaterialGroupCode.Attribute.PG_SUB_CLASS.getSelect(context));
        busSelects.addElement(pgV3Constants.HAS_FROM_RELATIONSHIP_TEMPLATE_TO_REPORTED_FUNCTION);
        busSelects.addElement(pgV3Constants.SELECT_FROM_RELATIONSHIP_TEMPLATE_TO_REPORTED_FUNCTION_TO_NAME);
        busSelects.addElement(MaterialGroupCode.Attribute.PG_PACKAGING_COMPONENT_TYPE.getSelect(context));
        busSelects.addElement(MaterialGroupCode.Attribute.PG_PACKAGING_MATERIAL_TYPE.getSelect(context));
        busSelects.addElement(MaterialGroupCode.Attribute.PG_PACKAGING_TECHNOLOGY.getSelect(context));
        busSelects.add(MaterialGroupCode.Basic.SELECT_HAS_FROM_RELATIONSHIP_PRIMARY_ORGANIZATION.getValue());
        busSelects.add(MaterialGroupCode.Basic.SELECT_FROM_RELATIONSHIP_PRIMARY_ORGANIZATION_TO_NAME.getValue());
        busSelects.addElement(MaterialGroupCode.Attribute.PG_CHEMICAL_GROUP.getSelect(context));
        busSelects.addElement(MaterialGroupCode.Attribute.PG_CAS_NUMBER.getSelect(context));

        Map objectInfo = domainObject.getInfo(context, busSelects);

        String symType = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, (String) objectInfo.get(DomainConstants.SELECT_TYPE), false);
        objectInfo.put(DomainConstants.SELECT_ATTRIBUTE_SYMBOLIC_NAME, symType);
        return new Part(context, objectInfo);
    }

    /**
     * Method to add custom history indicating which Material Group Code was sent to SAP.
     * @param context
     * @param objectOid
     * @param code
     * @throws FrameworkException
     */
    public void addCustomHistoryWhenMaterialCodeIsSent(Context context, String objectOid, String code) throws FrameworkException {
        boolean isCtxPushed = Boolean.FALSE;
        try {
            String userAgent = PropertyUtil.getSchemaProperty(context, "person_UserAgent");
            if(!userAgent.equalsIgnoreCase(context.getUser())) { // no need to push context if context user is User-Agent.
                // need to push context as the incoming object will be release state and may not have access to add history.
                ContextUtil.pushContext(context, userAgent, DomainConstants.EMPTY_STRING, DomainConstants.EMPTY_STRING);
                isCtxPushed = Boolean.TRUE;
            }
            HistoryAuditTrailService auditTrailService = DCLServiceUtil.getHistoryAuditTrailService(context);
            auditTrailService.customHistoryUpdation(
                    context,                                                // context
                    objectOid,                                              // objectid
                    MaterialGroupCode.Basic.MATERIAL_GROUP_CODE.getValue(), // identifier
                    MaterialGroupCode.Basic.MATERIAL_GROUP_CODE.getValue() + MaterialGroupCode.Basic.SYMBOL_SPACE.getValue() + code,                                                   // value
                    MaterialGroupCode.Basic.CUSTOM.getValue());             // history event name (custom)
        } catch (FrameworkException e) {
            throw e;
        } finally {
            if (isCtxPushed) {
                // pop-context only if it was pushed in this method.
                ContextUtil.popContext(context);
            }
        }
    }
}
