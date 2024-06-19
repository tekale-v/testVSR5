package com.pg.dsm.rollup.packaging_certification.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.matrixone.apps.framework.ui.UIUtil;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.Pattern;
import matrix.util.StringList;

public class CertificationServiceUI implements pgV3Constants {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    Context context;

    public CertificationServiceUI(Context context) {
        this.context = context;
    }

    /**
     * @param objectOid
     * @return
     */
    public MapList getCertifications(String objectOid) throws FrameworkException {
        MapList objectList = new MapList();
        try {
            objectList = getRolledUpCertifications(objectOid);
            appendInfo(objectList, objectOid);
        } catch (FrameworkException e) {
            logger.log(Level.WARNING, "Exception -", e);
            throw e;
        }
        return objectList;
    }

    /**
     * @param partID
     * @return
     * @throws FrameworkException
     */
    public MapList getRolledUpCertifications(String partID) throws FrameworkException {
        boolean isCtxPushed = false;
        MapList objectList = new MapList();
        try {
            Pattern relPattern = new Pattern(RELATIONSHIP_ROLLED_UP_PACKAGING_MATERIAL_CERTIFICATIONS);
            Pattern typePattern = new Pattern(TYPE_PG_PLI_PACKAGING_MATERIAL_CERTIFICATION);

            StringList busSelects = new StringList(3);
            busSelects.addElement(DomainConstants.SELECT_ID);
            busSelects.addElement(DomainConstants.SELECT_NAME);
            busSelects.addElement(DomainConstants.SELECT_TYPE);
            busSelects.addElement(SELECT_READ_SHOW_ACCESS);

            StringList relSelects = new StringList(10);
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_ID); // Relationship Selects
            relSelects.addElement(DomainConstants.SELECT_RELATIONSHIP_NAME);

            relSelects.addElement(SELECT_ATTRIBUTE_COMMENTS);
            relSelects.addElement(SELECT_ATTRIBUTE_EXPIRATION_DATE);
            relSelects.add(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);
            relSelects.add(SELECT_ATTRIBUTE_ROLLUP_SOURCE_IDENTIFIER);
            relSelects.add(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE);
            relSelects.add(SELECT_ROLLED_UP_CERTIFICATION_DOCUMENT_ID);

            // Pushing context to "User Agent" to avoid issues in accessing rolled-up data.
            ContextUtil.pushContext(context, pgV3Constants.PERSON_USER_AGENT, DomainConstants.EMPTY_STRING, context.getVault().getName());
            isCtxPushed = true;

            DomainObject domainObject = DomainObject.newInstance(context, partID);
            objectList = domainObject.getRelatedObjects(context,
                    relPattern.getPattern(),  //relationship pattern
                    typePattern.getPattern(),  // object pattern
                    busSelects,                 // object selects
                    relSelects,              // relationship selects
                    false,                        // to direction
                    true,                       // from direction
                    (short) 1,                    // recursion level
                    null,                        // object where clause
                    null,
                    0);
        } catch (FrameworkException e) {
            throw e;
        } finally {
            if (isCtxPushed) {
                ContextUtil.popContext(context);
            }
        }
        return objectList;
    }

    /**
     * @param objectList
     * @throws FrameworkException
     */
    public void appendInfo(MapList objectList, String contextObjectId) throws FrameworkException {
        if (null != objectList && !objectList.isEmpty()) {
            List<String> sourceIDs = new ArrayList<>();
            List<String> documentIDs = new ArrayList<>();

            String contextObjectName = getSource(contextObjectId);
            final DomainObject domainObject = DomainObject.newInstance(context, contextObjectId);
            final String ctxObjectNameWithoutLink = domainObject.getInfo(context, DomainConstants.SELECT_NAME);

            String sourceID;
            String docID;
            String certID;
            String certName;
            String certLink;
            boolean hasAccess;
            StringList docIDList;

            Map<Object, Object> objectMap;
            Map<Object, Object> sourceMap;
            Map<Object, Object> docMap;

            for (int i = 0; i < objectList.size(); i++) {
                objectMap = (Map<Object, Object>) objectList.get(i);
                sourceID = (String) objectMap.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);
                if (UIUtil.isNotNullAndNotEmpty(sourceID)) {
                    sourceIDs.add(sourceID);
                }
            }
            for (int i = 0; i < objectList.size(); i++) {
                objectMap = (Map<Object, Object>) objectList.get(i);
                documentIDs.addAll(getStringListFromMap(objectMap, SELECT_ROLLED_UP_CERTIFICATION_DOCUMENT_ID));
            }
            Map<String, Map<Object, Object>> sourceInfo = getSourcePartInfo(sourceIDs, objectList);
            Map<String, Map<Object, Object>> certDocumentInfo = getCertDocumentInfo(documentIDs);

            String link = "javascript:showNonModalDialog('../common/emxTree.jsp?objectId=<OID>')";
            String anchorLink = "<a href=\"" + link + "\">NAME</a>";

            for (int i = 0; i < objectList.size(); i++) {
                objectMap = (Map<Object, Object>) objectList.get(i);
                certID = (String) objectMap.get(DomainConstants.SELECT_ID);
                certName = (String) objectMap.get(DomainConstants.SELECT_NAME);
                sourceID = (String) objectMap.get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID);
                certLink = anchorLink.replaceAll("<OID>", certID);
                certLink = certLink.replaceAll("NAME", certName);

                hasAccess = Boolean.parseBoolean((String) objectMap.get(SELECT_READ_SHOW_ACCESS));
                objectMap.put(pgV3Constants.KEY_CERT_ID, certID);
                objectMap.put(pgV3Constants.KEY_CERT_NAME_WITHOUT_LINK, certName);
                if (hasAccess) {
                    objectMap.put(pgV3Constants.KEY_CERT_NAME, certLink);
                    objectMap.put(pgV3Constants.KEY_CERT_EXPIRATION_DATE, (String) objectMap.get(SELECT_ATTRIBUTE_EXPIRATION_DATE));
                    objectMap.put(pgV3Constants.KEY_CERT_COMMENTS, (String) objectMap.get(SELECT_ATTRIBUTE_COMMENTS));
                } else {
                    objectMap.put(pgV3Constants.KEY_CERT_NAME, (String) objectMap.get(DomainConstants.SELECT_NAME));
                    objectMap.put(pgV3Constants.KEY_CERT_EXPIRATION_DATE, DomainConstants.EMPTY_STRING);
                    objectMap.put(pgV3Constants.KEY_CERT_COMMENTS, DomainConstants.EMPTY_STRING);
                }
                if (sourceInfo.containsKey(sourceID)) {
                    sourceMap = sourceInfo.get(sourceID);
                    objectMap.put(pgV3Constants.KEY_SOURCE_NAME, sourceMap.get(pgV3Constants.KEY_SOURCE_NAME));
                    objectMap.put(pgV3Constants.KEY_SOURCE_NAME_WITHOUT_LINK, sourceMap.get(pgV3Constants.KEY_SOURCE_NAME_WITHOUT_LINK));
                    objectMap.put(pgV3Constants.KEY_SOURCE_ID, sourceMap.get(pgV3Constants.KEY_SOURCE_ID));
                    objectMap.put(pgV3Constants.KEY_SOURCE_TYPE, sourceMap.get(pgV3Constants.KEY_SOURCE_TYPE));
                    objectMap.put(pgV3Constants.KEY_SOURCE_TITLE, sourceMap.get(pgV3Constants.KEY_SOURCE_TITLE));
                    objectMap.put(pgV3Constants.KEY_SOURCE_REL_TYPE, sourceMap.get(pgV3Constants.KEY_SOURCE_REL_TYPE));
                } else {
                    objectMap.put(pgV3Constants.KEY_SOURCE_NAME, DomainConstants.EMPTY_STRING);
                    objectMap.put(pgV3Constants.KEY_SOURCE_NAME_WITHOUT_LINK, DomainConstants.EMPTY_STRING);
                    objectMap.put(pgV3Constants.KEY_SOURCE_ID, DomainConstants.EMPTY_STRING);
                    objectMap.put(pgV3Constants.KEY_SOURCE_TYPE, DomainConstants.EMPTY_STRING);
                    objectMap.put(pgV3Constants.KEY_SOURCE_TITLE, DomainConstants.EMPTY_STRING);
                    objectMap.put(pgV3Constants.KEY_SOURCE_REL_TYPE, DomainConstants.EMPTY_STRING);
                }
                objectMap.putAll(getDocInfo(objectMap, certDocumentInfo));
                objectMap.put(pgV3Constants.KEY_SOURCE, contextObjectName);            // to show context context name (in first column)
                objectMap.put(pgV3Constants.KEY_SOURCE_WITHOUT_LINK, ctxObjectNameWithoutLink);  // for pdf/any other system - show name without link.
                objectMap.put(pgV3Constants.KEY_DISABLE_SELECTION, pgV3Constants.TRUE);// to disable table-row selection.
            }
        }
    }

    /**
     * @param objectMap
     * @param certDocumentInfo
     * @return
     */
    private Map<Object, Object> getDocInfo(Map<Object, Object> objectMap, Map<String, Map<Object, Object>> certDocumentInfo) {
        Map<Object, Object> retMap = new HashMap<>();
        Map<Object, Object> docMap;
        if (objectMap.containsKey(SELECT_ROLLED_UP_CERTIFICATION_DOCUMENT_ID)) {
            StringList docIDs = getStringListFromMap(objectMap, SELECT_ROLLED_UP_CERTIFICATION_DOCUMENT_ID);
            List<String> docNames = new ArrayList<>();
            List<String> docNamesNoHyperLink = new ArrayList<>();
            List<String> docObIDs = new ArrayList<>();
            List<String> docTypes = new ArrayList<>();
            if (null != docIDs) {
                for (String docID : docIDs) {
                    retMap.put(pgV3Constants.KEY_HAS_DOC, Boolean.TRUE);
                    if (certDocumentInfo.containsKey(docID)) {
                        docMap = certDocumentInfo.get(docID);
                        docNames.add((String) docMap.get(pgV3Constants.KEY_DOC_NAME));
                        docNamesNoHyperLink.add((String) docMap.get(pgV3Constants.KEY_DOC_NAME_WITHOUT_LINK));
                        docObIDs.add((String) docMap.get(pgV3Constants.KEY_DOC_ID));
                        docTypes.add((String) docMap.get(pgV3Constants.KEY_DOC_TYPE));
                    } else {
                        logger.log(Level.WARNING, "Error fetching cert doc info");
                    }
                }
                retMap.put(pgV3Constants.KEY_DOC_NAME, String.join(pgV3Constants.SYMBOL_COMMA_AND_SPACE, docNames));
                retMap.put(pgV3Constants.KEY_DOC_NAME_WITHOUT_LINK, String.join(pgV3Constants.SYMBOL_COMMA_AND_SPACE, docNamesNoHyperLink));
                retMap.put(pgV3Constants.KEY_DOC_ID, String.join(pgV3Constants.SYMBOL_COMMA_AND_SPACE, docObIDs));
                retMap.put(pgV3Constants.KEY_DOC_TYPE, String.join(pgV3Constants.SYMBOL_COMMA_AND_SPACE, docTypes));
                if (docObIDs.isEmpty()) {
                    retMap.put(pgV3Constants.KEY_HAS_DOC, Boolean.FALSE);
                }
            }
        } else {
            retMap.put(pgV3Constants.KEY_HAS_DOC, Boolean.FALSE);
            retMap.put(pgV3Constants.KEY_DOC_NAME, DomainConstants.EMPTY_STRING);
            retMap.put(pgV3Constants.KEY_DOC_NAME_WITHOUT_LINK, DomainConstants.EMPTY_STRING);
            retMap.put(pgV3Constants.KEY_DOC_ID, DomainConstants.EMPTY_STRING);
            retMap.put(pgV3Constants.KEY_DOC_TYPE, DomainConstants.EMPTY_STRING);
        }
        return retMap;
    }

    /**
     * This method for display context object in PAP, FAB Certifications table view.
     *
     * @param objectId
     * @return
     * @throws FrameworkException
     */
    String getSource(String objectId) throws FrameworkException {
        String retString = DomainConstants.EMPTY_STRING;
        StringList busSelects = new StringList(3);
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(SELECT_READ_SHOW_ACCESS);
        final DomainObject domainObject = DomainObject.newInstance(context, objectId);

        final Map objectInfo = domainObject.getInfo(context, busSelects);
        String objectName = (String) objectInfo.get(DomainConstants.SELECT_NAME);
        final boolean hasAccess = Boolean.parseBoolean((String) objectInfo.get(SELECT_READ_SHOW_ACCESS));
        if (hasAccess) {
            StringBuilder linkBuilder = new StringBuilder();
            linkBuilder.append("<a href=\"javascript:;\" onclick=\"javascript:showModalDialog('../common/emxTree.jsp?objectId=");
            linkBuilder.append(objectId);
            linkBuilder.append("','true');\">");
            linkBuilder.append(objectName);
            linkBuilder.append("</a>");
            retString = linkBuilder.toString();
        } else {
            retString = objectName;
        }
        return retString;
    }


    /**
     * @param partIDs
     * @param objectList
     * @return
     * @throws FrameworkException
     */
    public Map<String, Map<Object, Object>> getSourcePartInfo(List<String> partIDs, MapList objectList) throws FrameworkException {
        Map<String, Map<Object, Object>> sourceMap = new HashMap<>();

        StringList busSelects = new StringList(3);
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(SELECT_ATTRIBUTE_TITLE);
        busSelects.addElement(SELECT_READ_SHOW_ACCESS);

        MapList infoList = DomainObject.getInfo(context, partIDs.toArray(new String[partIDs.size()]), busSelects);
        if (null != infoList && !infoList.isEmpty()) {
            Map<Object, Object> objectMap;

            String sourceID;
            String sourceName;
            String sourceLink;
            boolean hasAccess;

            String link = "javascript:showNonModalDialog('../common/emxTree.jsp?objectId=<OID>')";
            String anchorLink = "<a href=\"" + link + "\">NAME</a>";

            for (int i = 0; i < infoList.size(); i++) {
                objectMap = (Map<Object, Object>) infoList.get(i);
                sourceID = (String) objectMap.get(DomainConstants.SELECT_ID);
                sourceName = (String) objectMap.get(DomainConstants.SELECT_NAME);
                sourceLink = anchorLink.replaceAll("<OID>", sourceID);
                sourceLink = sourceLink.replaceAll("NAME", sourceName);

                hasAccess = Boolean.parseBoolean((String) objectMap.get(SELECT_READ_SHOW_ACCESS));
                if (hasAccess) {
                    objectMap.put(pgV3Constants.KEY_SOURCE_NAME, sourceLink);
                    objectMap.put(pgV3Constants.KEY_SOURCE_TITLE, (String) objectMap.get(SELECT_ATTRIBUTE_TITLE));
                } else {
                    objectMap.put(pgV3Constants.KEY_SOURCE_NAME, (String) objectMap.get(DomainConstants.SELECT_NAME));
                    objectMap.put(pgV3Constants.KEY_SOURCE_TITLE, DomainConstants.EMPTY_STRING);
                }
                objectMap.put(pgV3Constants.KEY_SOURCE_NAME_WITHOUT_LINK, sourceName);
                objectMap.put(pgV3Constants.KEY_SOURCE_TYPE, (String) objectMap.get(DomainConstants.SELECT_TYPE));
                objectMap.put(pgV3Constants.KEY_SOURCE_ID, (String) sourceID);

                sourceMap.put((String) objectMap.get(DomainConstants.SELECT_ID), objectMap);
            }
        }

        Set<Map.Entry<String, Map<Object, Object>>> entrySet = sourceMap.entrySet();
        for (Map.Entry<String, Map<Object, Object>> entry : entrySet) {
            String keyID = entry.getKey();
            final Map<Object, Object> keyPairMap = entry.getValue();
            final Set<String> relTypeList = (Set<String>) objectList.stream()
                    .filter(map -> keyID.equals((String) ((Map<Object, Object>) map).get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_ID)))
                    .map(map -> (String) ((Map<Object, Object>) map).get(SELECT_ATTRIBUTE_ROLLUP_SOURCE_REL_TYPE))
                    .collect(Collectors.toSet());
            keyPairMap.put(pgV3Constants.KEY_SOURCE_REL_TYPE, String.join(pgV3Constants.SYMBOL_COMMA, relTypeList));
        }
        return sourceMap;
    }


    /**
     * @param documentIDs
     * @return
     * @throws FrameworkException
     */
    public Map<String, Map<Object, Object>> getCertDocumentInfo(List<String> documentIDs) throws FrameworkException {
        Map<String, Map<Object, Object>> certMap = new HashMap<>();

        StringList busSelects = new StringList(3);
        busSelects.addElement(DomainConstants.SELECT_ID);
        busSelects.addElement(DomainConstants.SELECT_NAME);
        busSelects.addElement(DomainConstants.SELECT_TYPE);
        busSelects.addElement(SELECT_READ_SHOW_ACCESS);

        MapList infoList = DomainObject.getInfo(context, documentIDs.toArray(new String[documentIDs.size()]), busSelects);
        if (null != infoList && !infoList.isEmpty()) {
            Map<Object, Object> objectMap;
            String docID;
            String docName;
            String docLink;
            boolean hasAccess;

            String link = "javascript:showNonModalDialog('../common/emxTree.jsp?objectId=<OID>')";
            String anchorLink = "<a href=\"" + link + "\">NAME</a>";

            for (int i = 0; i < infoList.size(); i++) {
                objectMap = (Map<Object, Object>) infoList.get(i);
                docID = (String) objectMap.get(DomainConstants.SELECT_ID);
                docName = (String) objectMap.get(DomainConstants.SELECT_NAME);
                docLink = anchorLink.replaceAll("<OID>", docID);
                docLink = docLink.replaceAll("NAME", docName);

                hasAccess = Boolean.parseBoolean((String) objectMap.get(SELECT_READ_SHOW_ACCESS));
                if (hasAccess) {
                    objectMap.put(pgV3Constants.KEY_DOC_NAME, docLink);
                } else {
                    objectMap.put(pgV3Constants.KEY_DOC_NAME, docName);
                }
                objectMap.put(pgV3Constants.KEY_DOC_NAME_WITHOUT_LINK, docName);
                objectMap.put(pgV3Constants.KEY_DOC_TYPE, (String) objectMap.get(DomainConstants.SELECT_TYPE));
                objectMap.put(pgV3Constants.KEY_DOC_ID, (String) docID);
                certMap.put((String) objectMap.get(DomainConstants.SELECT_ID), objectMap);
            }
        }
        return certMap;
    }

    /**
     * @param dataMap
     * @param selectable
     * @return
     */
    private StringList getStringListFromMap(Map<Object, Object> dataMap, String selectable) {
        Object resList = (dataMap).get(selectable);
        StringList retList = new StringList();
        if (null != resList) {
            if (resList instanceof StringList) {
                retList = (StringList) resList;
            } else {
                retList.add(resList.toString());
            }
        }
        return retList;
    }
}

