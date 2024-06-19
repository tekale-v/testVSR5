//Added by DSM (Sogeti) 2018x.6 Rollup Circular Circular Defect ID - 42216
package com.pg.dsm.rollup_event.common.circular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.dsm.rollup_event.enumeration.RollupConstants;
import com.pg.dsm.rollup_event.rollup.services.Resource;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class BOM {

    boolean circularExist;
    String circularHistory;

    /**
     * This constructor set Circular value exist or not
     *
     * @param - Structure structure
     **/
    private BOM(Structure structure) {
        this.circularExist = structure.circularExist;
        this.circularHistory = structure.getHistory();
    }

    /**
     * This constructor check Circular  exist or not
     *
     * @return - boolean
     **/
    public boolean isCircularExist() {
        return circularExist;
    }

    /**
     * @return the circularHistory
     */
    public String getCircularHistory() {
        return circularHistory;
    }

    //Inner Class
    public static class Structure {

        private static final Logger logger = Logger.getLogger(Structure.class.getName());

        Context context;
        String objectId;
        Resource resource;
        boolean circularExist;
        boolean isBOMCOPIsSetProduct;
        boolean isSubBOMCCOPIsSetProduct;
        StringBuilder sbHistory = new StringBuilder();

        /**
         * This constructor set context & object id
         *
         * @param - Context context
         * @param - String objectId
         * @return - Structure
         **/
        public Structure(Context context, String objectId, Resource resource) {
            this.context = context;
            this.objectId = objectId;
            this.resource = resource;
        }

        /**
         * @param parentList
         * @return
         */
        public static String getReferenceDetails(List<Part> parentList) {
            StringBuilder sw = new StringBuilder();
            if (parentList != null && !parentList.isEmpty()) {
                String strSubValue = "";
                for (int i = parentList.size() - 1; i >= 0; i--) {
                    strSubValue = createSubstituteReferance(parentList.get(i));
                    if (i == 0) {
                        sw.append(strSubValue);
                    } else {
                        sw.append(strSubValue + RollupConstants.Basic.FORWARD_ARROWS.getValue());
                    }
                }
            }

            return sw.toString();
        }

        /**
         * Added for update substitute details in history
         *
         * @param part
         * @return
         */
        private static String createSubstituteReferance(Part part) {
            return part.isSubstitutePart() ? part.getSubstituteForName() + "-> " + RollupConstants.Basic.KEY_SUBSTITUTE.getValue() + " ->" + part.getName() : part.getName();
        }

        /**
         * @param sHistory
         */
        public void appendHistory(String sHistory) {
            this.sbHistory.append(sHistory);
        }

        /**
         * @return
         */
        public String getHistory() {
            return sbHistory.toString();
        }

        /**
         * @return the isBOMCOPIsSetProduct
         */
        public boolean isBOMCOPIsSetProduct() {
            return isBOMCOPIsSetProduct;
        }

        /**
         * @param isBOMCOPIsSetProduct the isBOMCOPIsSetProduct to set
         */
        public void setBOMCOPIsSetProduct(boolean isBOMCOPIsSetProduct) {
            this.isBOMCOPIsSetProduct = isBOMCOPIsSetProduct;
        }

        /**
         * @return the isSubBOMCCOPIsSetProduct
         */
        public boolean isSubBOMCCOPIsSetProduct() {
            return isSubBOMCCOPIsSetProduct;
        }

        /**
         * @param isSubBOMCCOPIsSetProduct the isSubBOMCCOPIsSetProduct to set
         */
        public void setSubBOMCCOPIsSetProduct(boolean isSubBOMCCOPIsSetProduct) {
            this.isSubBOMCCOPIsSetProduct = isSubBOMCCOPIsSetProduct;
        }

        /**
         * This method is used to check Circular
         *
         * @return - Structure
         **/
        public Structure checkCircular() throws FrameworkException {
            Part parent = getPartBean(objectId);
            if (null != parent) {
                isCircular(parent);
            }
            return this;
        }

        /**
         * This method is used to check Circular
         *
         * @param parent
         * @return
         * @throws FrameworkException
         */
        public Structure checkCircular(Part parent) throws FrameworkException {
            if (null != parent) {
                isCircular(parent);
            }
            return this;
        }

        /**
         * This method returns instance
         *
         * @return - BOM
         **/
        public BOM getInstance() {
            return new BOM(this);
        }

        /**
         * This method checks circular exists
         *
         * @param - Part parent
         * @return - Part
         **/
        private void isCircular(Part parent) throws FrameworkException {
            if (!parent.isParentExist()) {
                parent.setParentExist(false);
            }

            MapList objectList = getBOMWithSubstitute();
            objectList = removeWithoutMarkStructureParts(objectList);
            List<Part> parents = new ArrayList<>();
            Map<?, ?> busMap;
            List<Part> substitutes;
            Part currentPart;
            Part currentPartParent;
            int level;
            int iCOPSetProductIndex = 0;
            boolean isCOPSetProdCheckingStart = false;
            this.setBOMCOPIsSetProduct(isBOMsFirstCOPIsSetProduct(context, objectList));

            for (Iterator<?> bomItr = objectList.iterator(); bomItr.hasNext(); ) {
                busMap = (Map<?, ?>) bomItr.next();
                currentPart = new Part(busMap);

                level = Integer.parseInt(currentPart.getLevel());
                if (!isCOPSetProdCheckingStart && this.isBOMCOPIsSetProduct()
                        && pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(currentPart.getType())) {
                    iCOPSetProductIndex = level;
                    isCOPSetProdCheckingStart = true;
                }
                if (isCOPSetProdCheckingStart && iCOPSetProductIndex + 1 == level) {
                    if (!isProductPartType(currentPart.getType())) {
                        continue;
                    }
                } else if (isCOPSetProdCheckingStart && iCOPSetProductIndex < level) {
                    continue;
                } else if (!pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(currentPart.getType())) {
                    isCOPSetProdCheckingStart = false;
                    iCOPSetProductIndex = 0;
                }

                substitutes = getSubstitutes(busMap, currentPart);

                currentPart.setSubstitutes(substitutes);
                if (!substitutes.isEmpty()) {
                    currentPart.setSubstituteExist(true);
                }
                if (level == 1) {
                    parents.clear();
                    parents.add(parent);
                    currentPart.setParent(parent);
                    currentPart.setParentExist(true);
                    parent.addChild(currentPart);
                    parent.setChildExist(true);

                } else {
                    currentPartParent = parents.get(level - 1);
                    currentPartParent.addChild(currentPart);
                    currentPartParent.setChildExist(true);
                    currentPart.setParent(currentPartParent);
                    currentPart.setParentExist(true);
                }

                if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equals(currentPart.getType())) {
                    substitutes = checkSubstituteCUPHasSetProduct(substitutes);
                    currentPart.setSubstitutes(substitutes);
                    if (this.isSubBOMCCOPIsSetProduct() && !this.isBOMCOPIsSetProduct()) {
                        isParentExistInSubstituteBOMStructure(currentPart, substitutes);
                        break;
                    }
                }
                checkChildIsInParentList(currentPart);

                if (!circularExist && currentPart.isSubstituteExist()) {
                    isParentExistInSubstituteBOMStructure(currentPart, substitutes);
                }

                if (circularExist) {
                    break;
                }
                parents.add(level, currentPart);
            }
            parents.clear();

        }

        /**
         * This method will remove child of the parent types(FPP,FP,PSUB,PAP,FAB)
         *
         * @param objectList
         * @return Modified for Defect# 44589
         */
        private MapList removeWithoutMarkStructureParts(MapList objectList) {
            Part currentPart;
            MapList mlValidObjList = new MapList();
            if (objectList != null && !objectList.isEmpty()) {
                int parentTypeLevel = 0;
                boolean parentTypeIsFound = false;
                String strMOSCircularParentTypes = getMOSCircularParentType();
                for (Iterator iterator = objectList.iterator(); iterator.hasNext(); ) {
                    Map busMap = (Map) iterator.next();
                    currentPart = new Part(busMap);
                    int level = Integer.parseInt(currentPart.getLevel());
                    if (parentTypeIsFound && parentTypeLevel < level) {
                        continue;
                    }
                    if ((strMOSCircularParentTypes.contains(currentPart.getType()) || !isRequiredTypeForCircularRefrenceChecking(currentPart.getType()))) {
                        parentTypeIsFound = true;
                        mlValidObjList.add(busMap);
                        parentTypeLevel = level;
                        continue;
                    } else if (parentTypeLevel >= level) {
                        parentTypeIsFound = false;
                        parentTypeLevel = level;
                    }
                    mlValidObjList.add(busMap);
                }
            }
            return mlValidObjList;

        }

        /**
         * This method will check CUP substitute has COP set product
         *
         * @param substitutes
         * @return
         * @throws FrameworkException
         */
        private List<Part> checkSubstituteCUPHasSetProduct(List<Part> substitutes) throws FrameworkException {
            boolean isSubstituteCUPHasSetProduct = false;
            if (substitutes != null && !substitutes.isEmpty()) {
                for (Part part : substitutes) {
                    boolean isSubstituteCUPSetProduct = checkSubstituteHasSetProduct(part.getId());
                    if (isSubstituteCUPSetProduct) {
                        isSubstituteCUPHasSetProduct = isSubstituteCUPSetProduct;
                    }
                    part.setSetProdcutPresent(isSubstituteCUPSetProduct);
                }
                substitutes = filterSubstitutesBasedOnSetProduct(substitutes, isSubstituteCUPHasSetProduct);
            }
            this.setSubBOMCCOPIsSetProduct(isSubstituteCUPHasSetProduct);
            return substitutes;
        }

        /**
         * This method will return valid data's based on set product for MOS circular checking
         *
         * @param substitutes
         * @param isSubstituteCUPSetProduct
         * @return
         */
        private List<Part> filterSubstitutesBasedOnSetProduct(List<Part> substitutes, boolean isSubstituteCUPSetProduct) {
            List<Part> substitutesClone = new ArrayList<Part>();
            if (isSubstituteCUPSetProduct || (!isSubstituteCUPSetProduct && !this.isBOMCOPIsSetProduct())) {
                for (Part part : substitutes) {
                    if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(part.getType()) && (part.isSetProdcutPresent() || (!this.isBOMCOPIsSetProduct() && !isSubstituteCUPSetProduct))) {
                        substitutesClone.add(part);
                    }
                }
            }
            return substitutesClone;
        }

        /**
         * This method fetch BOM from substitute
         *
         * @return - MapList
         **/
        private boolean checkSubstituteHasSetProduct(String strObjectId) throws FrameworkException {
            boolean isSubstituteCUPHasSetProduct = false;
            DomainObject dObj = DomainObject.newInstance(context, strObjectId);
            String objectWhere = DomainConstants.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
            MapList list = dObj.getRelatedObjects(context, DomainConstants.RELATIONSHIP_EBOM, // relpattern
                    pgV3Constants.TYPE_PGINNERPACKUNITPART + "," + pgV3Constants.TYPE_PGCONSUMERUNITPART, // typepattern
                    new StringList(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME), // bus selectable
                    null, // rel selectable
                    false, // get to
                    true, // get from
                    (short) 2, // level
                    objectWhere, // object where
                    DomainConstants.EMPTY_STRING, // rel where
                    0);// No of objects
            if (list != null && !list.isEmpty()) {
                for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                    Map object = (Map) iterator.next();
                    String sType = (String) object.get(DomainConstants.SELECT_TYPE);
                    if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(sType)) {
                        if (pgV3Constants.KEY_YES
                                .equalsIgnoreCase((String) object.get(pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
                            isSubstituteCUPHasSetProduct = true;
                            break;
                        }
                    }
                }
            }

            return isSubstituteCUPHasSetProduct;
        }

        /**
         * Checking part is parent type part or not
         *
         * @param strType
         * @return
         */
        private boolean isProductPartType(String strType) {
            boolean isProductType = false;
            String strProductPartTypes = resource.getRollupPageProperties().getProperty("MOS_Valid_Product_Part_Types");
            if (strProductPartTypes.contains(strType)) {
                isProductType = true;
            }
            return isProductType;
        }

        /**
         * This method will check primary COP is set product or not
         *
         * @param context
         * @param objectList
         * @return
         * @throws FrameworkException
         */
        private boolean isBOMsFirstCOPIsSetProduct(Context context, MapList objectList) throws FrameworkException {
            boolean isCOPSetProduct = false;
            if (objectList != null && !objectList.isEmpty()) {
                Map<?, ?> busMap;
                for (Iterator<?> bomItr = objectList.iterator(); bomItr.hasNext(); ) {
                    busMap = (Map<?, ?>) bomItr.next();
                    String sType = (String) busMap.get(DomainConstants.SELECT_TYPE);
                    if (pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(sType)) {
                        isCOPSetProduct = isPartSetProduct(context, DomainObject.newInstance(context, (String) busMap.get(DomainConstants.SELECT_ID)));
                        break;
                    }
                }
            }
            return isCOPSetProduct;
        }

        public boolean isPartSetProduct(Context context, DomainObject domObj) throws FrameworkException {
            boolean bCheckSetProduct = false;
            if (pgV3Constants.KEY_YES
                    .equalsIgnoreCase(domObj.getInfo(context, pgV3Constants.SELECT_ATTRIBUTE_PGSETPRODUCTNAME))) {
                bCheckSetProduct = true;
            }
            return bCheckSetProduct;
        }

        /**
         * This method checks parent exists in substitute BOM Structure
         *
         * @param - Part currentPart
         * @param - Part currentParent
         * @param - List<Part> substitutes
         * @return - void
         * @throws FrameworkException
         **/
        public void isParentExistInSubstituteBOMStructure(Part currentPart, List<Part> substitutes)
                throws FrameworkException {

            checkSubstituteHasCircularRefrence(currentPart, substitutes);
            if (!circularExist) {
                for (Part substitute : substitutes) {
                    substitute.setParent(currentPart.getParent());
                    currentPart.setParentExist(true);
                    substitute.setParentExist(true);
                    // Modified for Defect# 44589 - Starts
                    if (isRequiredTypeForCircularRefrenceChecking(substitute.getType()) && !isPartValidForExecution(substitute)) {
                        // Modified for Defect# 44589 - Ends
                        createParentChildStructure(substitute, removeWithoutMarkStructureParts(getBOM(substitute.getId())));
                        checkCircularInChilds(substitute.getChildren());
                    }

                    if (circularExist) {
                        break;
                    }
                }
            }
        }

        /**
         * @param childPartList
         * @throws FrameworkException
         */
        private void checkCircularInChilds(List<Part> childPartList) throws FrameworkException {
            if (!circularExist) {
                for (int i = 0; i < childPartList.size(); i++) {
                    Part childBOM = childPartList.get(i);
                    if (isRequiredTypeForCircularRefrenceChecking(childBOM.getType()) && !circularExist) {
                        checkChildIsInParentList(childBOM);
                    }
                    if (isRequiredTypeForCircularRefrenceChecking(childBOM.getType()) || isProductPartType(childBOM.getType())) {
                        if (childBOM.isSubstituteExist() && !circularExist) {
                            checkSubstituteHasCircularRefrence(childBOM, childBOM.getSubstitutes());
                        }
                        if (isRequiredTypeForCircularRefrenceChecking(childBOM.getType())) {
                            if (childBOM.isChildExist() && !circularExist) {
                                checkCircularInChilds(childBOM.getChildren());
                            }
                        }
                    }
                }
            }
        }

        /**
         * @param child
         * @throws FrameworkException
         */
        private void checkChildIsInParentList(Part child) throws FrameworkException {
            List<Part> parentList = new ArrayList<>();
            // list get filled.
            getParentsRecursively(child, parentList);
            for (Part parentPart : parentList) {
                if (child.getId().equals(parentPart.getId())
                        && (isRequiredTypeForCircularRefrenceChecking(child.getType())
                        && isRequiredTypeForCircularRefrenceChecking(parentPart.getType()))) {
                    updateCircularHistory(parentPart, child);
                    break;
                }
            }
            if (!circularExist && isPartValidForExecution(child)) {
                checkCircularRefrence(child.getType(), child.getId(), child);
            }
        }

        /**
         * @param part
         * @return
         */
        private boolean isPartValidForExecution(Part part) {
            boolean isMarked = false;
            if (part != null) {
                String strMOSCircularParentTypes = getMOSCircularParentType();
                if (strMOSCircularParentTypes.contains(part.getType())) {
                    isMarked = true;
                }
            }
            return isMarked;
        }

        /**
         * @param sType
         * @param sObjectId
         * @throws FrameworkException
         */
        private void checkCircularRefrence(String sType, String sObjectId, Part parent) throws FrameworkException {
            String strMOSCircularParentTypes = getMOSCircularParentType();
            if (strMOSCircularParentTypes.contains(sType)) {
                BOM bomObj = new Structure(context, sObjectId, resource).checkCircular(parent)
                        .getInstance();
                boolean circularExistInSubParentParts = bomObj.isCircularExist();
                if (circularExistInSubParentParts) {
                    circularExist = true;
                    this.appendHistory(bomObj.getCircularHistory());
                }
            }
        }

        /**
         * @param currentPart
         * @param substitutes
         * @return
         * @throws FrameworkException
         */
        private void checkSubstituteHasCircularRefrence(Part currentPart, List<Part> substitutes)
                throws FrameworkException {
            List<Part> parentList = new ArrayList<>();
            getParentsRecursively(currentPart, parentList);

            for (Part substitute : substitutes) {
                substitute.setParent(currentPart.getParent());
                currentPart.setParentExist(true);
                substitute.setParentExist(true);
                if (!circularExist) {
                    for (Part parentPart : parentList) {
                        if (substitute.getId().contains(parentPart.getId())
                                && (isRequiredTypeForCircularRefrenceChecking(parentPart.getType())
                                && isRequiredTypeForCircularRefrenceChecking(substitute.getType()))) {
                            updateCircularHistory(parentPart, substitute);
                            break;
                        }
                    }
                    if (!circularExist && isPartValidForExecution(substitute)) {
                        checkCircularRefrence(substitute.getType(), substitute.getId(), substitute);
                    }
                }
            }
        }

        /**
         * @param type
         * @return
         */
        private boolean isRequiredTypeForCircularRefrenceChecking(String type) {
            boolean isValidType = false;
            String strMOSCircularValidType = resource.getRollupPageProperties().getProperty("MOS_Circular_Valid_Types");
            if (strMOSCircularValidType.contains(type)) {
                isValidType = true;
            }
            return isValidType;
        }

        /**
         * Update circular history on part
         *
         * @param parentPart
         * @param currentPart
         */
        private void updateCircularHistory(Part parentPart, Part currentPart) {
            String strReference = getReferenceDetails(getParentsRecursively(currentPart, new ArrayList<Part>()));
            strReference = new StringBuilder(RollupConstants.Basic.CIRCULAR_REFERENCE_MESSAGE.getValue()).append(strReference + RollupConstants.Basic.FORWARD_ARROWS.getValue()).append(createSubstituteReferance(currentPart)).toString();
            circularExist = true;
            this.appendHistory(strReference);
        }

        /**
         * This method fetch BOM from substitute
         *
         * @return - MapList
         **/
        private MapList getBOMWithSubstitute() throws FrameworkException {
            DomainObject dObj = DomainObject.newInstance(context, objectId);
            String objectWhere = DomainConstants.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
            return dObj.getRelatedObjects(context,
                    DomainConstants.RELATIONSHIP_EBOM, // relpattern
                    DomainConstants.TYPE_PART, // typepattern
                    getBusBasicSelects(), // bus selectable
                    getRelSubstituteSelects(), // rel selectable
                    false, // get to
                    true, // get from
                    (short) 0, // level
                    objectWhere, // object where
                    DomainConstants.EMPTY_STRING, // rel where
                    0);// No of objects
        }

        /**
         * This method used to get EBOM Substitute id
         *
         * @param - Map dataMap
         * @param - String selectable
         * @return - StringList
         **/
        private StringList getEBOMSubstituteFromMap(Map dataMap, String selectable) {
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

        /**
         * This method used to get parent recursively
         *
         * @param - Part part
         * @param - List<Part> parts
         * @return - List
         **/
        private List<Part> getParentsRecursively(Part part, List<Part> parts) {
            if (part.isParentExist()) {
                Part parent = part.getParent();
                parts.add(parent);
                getParentsRecursively(parent, parts);
            }
            return parts;
        }

        /**
         * This method used to get BOM id
         *
         * @param - String objectId
         * @return - MapList
         **/
        private MapList getBOM(String objectId) {
            MapList objectList = new MapList();
            String objectWhere = DomainConstants.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
            try {
                DomainObject dObj = DomainObject.newInstance(context, objectId);
                objectList = dObj.getRelatedObjects(context, pgV3Constants.RELATIONSHIP_EBOM,
                        DomainConstants.QUERY_WILDCARD, Boolean.FALSE, Boolean.TRUE, (short) 0, getBusBasicSelects(),
                        getRelSubstituteSelects(), objectWhere, DomainConstants.EMPTY_STRING, (short) 0,
                        DomainConstants.EMPTY_STRING, null, null);
            } catch (FrameworkException e) {
                logger.log(Level.WARNING, null, e);
            }
            return objectList;
        }

        /**
         * This method used to get BOM id
         *
         * @param - Map busMap
         * @return - List
         **/
        private List<Part> getSubstitutes(Map<?, ?> busMap, Part parent) {

            List<Part> parts = new ArrayList<>();

            if (busMap.containsKey(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID)) {
                StringList substituteIDs = getEBOMSubstituteFromMap(busMap, pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
                StringList substituteNames = getEBOMSubstituteFromMap(busMap,
                        pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
                StringList substituteTypes = getEBOMSubstituteFromMap(busMap,
                        pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
                StringList substituteRevisions = getEBOMSubstituteFromMap(busMap,
                        pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
                StringList substituteStates = getEBOMSubstituteFromMap(busMap,
                        pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
                StringList substituteRelationshipIDs = getEBOMSubstituteFromMap(busMap,
                        pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
                Map<String, String> beanMap;

                for (int i = 0; i < substituteIDs.size(); i++) {
                    beanMap = new HashMap<>();
                    beanMap.put(DomainConstants.SELECT_ID, substituteIDs.get(i));
                    beanMap.put(DomainConstants.SELECT_TYPE, substituteTypes.get(i));
                    beanMap.put(DomainConstants.SELECT_NAME, substituteNames.get(i));
                    beanMap.put(DomainConstants.SELECT_REVISION, substituteRevisions.get(i));
                    beanMap.put(DomainConstants.SELECT_CURRENT, substituteStates.get(i));
                    beanMap.put(DomainConstants.SELECT_RELATIONSHIP_ID, substituteRelationshipIDs.get(i));
                    Part part = new Part(beanMap);
                    part.setSubstitutePart(true);
                    part.setSubstituteForName(parent.getName());
                    parts.add(part);
                }
            }
            return parts;
        }

        /**
         * This method used to get Part Bean
         *
         * @param - String objectId
         * @return - Part
         **/
        private Part getPartBean(String objectId) {
            Part part = null;
            try {
                DomainObject dObj = DomainObject.newInstance(context, objectId);
                Map<String, String> info = dObj.getInfo(context, getBusBasicSelects());
                info.put(DomainConstants.SELECT_LEVEL, RollupConstants.Basic.ZERO.getValue());
                info.put(DomainConstants.SELECT_RELATIONSHIP_ID, DomainConstants.EMPTY_STRING);
                part = new Part(info);
            } catch (Exception e) {
                logger.log(Level.WARNING, null, e);
            }
            return part;
        }

        /**
         * This method used to get bus select
         *
         * @return - StringList
         **/
        private StringList getBusBasicSelects() {
            StringList selectList = new StringList(7);
            selectList.add(DomainConstants.SELECT_ID);
            selectList.add(DomainConstants.SELECT_NAME);
            selectList.add(DomainConstants.SELECT_CURRENT);
            selectList.add(DomainConstants.SELECT_REVISION);
            selectList.add(DomainConstants.SELECT_TYPE);
            return selectList;
        }

        /**
         * This method used to get rel select
         *
         * @return - StringList
         **/
        private StringList getRelSubstituteSelects() {
            StringList selectList = new StringList(9);
            selectList.add(DomainConstants.SELECT_RELATIONSHIP_ID);
            selectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
            selectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
            selectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
            selectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
            selectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
            selectList.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
            return selectList;
        }

        /**
         * This method will create parent child structure for BOM and Substitute data
         *
         * @param childList
         * @return
         */
        public Part createParentChildStructure(Part productPart, MapList childList) {
            Part currentProductPart;
            Part parentProductPart;
            int level;
            List<Part> tempParentStorageList = new ArrayList<>();
            boolean isNeedToCheckCOPSetProduct = false;
            if (pgV3Constants.TYPE_PGCUSTOMERUNITPART.equalsIgnoreCase(productPart.getType()) && this.isSubBOMCCOPIsSetProduct()) {
                isNeedToCheckCOPSetProduct = true;
            }
            List<Part> substitutes;
            boolean isCOPSetProdCheckingStart = false;
            int iCOPSetProductIndex = 0;
            for (Object obj : childList) {
                currentProductPart = new Part((Map<?, ?>) obj);
                level = Integer.parseInt(currentProductPart.getLevel());
                if (!isCOPSetProdCheckingStart && isNeedToCheckCOPSetProduct
                        && pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(currentProductPart.getType())) {
                    iCOPSetProductIndex = level;
                    isCOPSetProdCheckingStart = true;
                }
                if (isCOPSetProdCheckingStart && iCOPSetProductIndex + 1 == level) {
                    if (!isProductPartType(currentProductPart.getType())) {
                        continue;
                    }
                } else if (isCOPSetProdCheckingStart && iCOPSetProductIndex < level) {
                    continue;
                } else if (!pgV3Constants.TYPE_PGCONSUMERUNITPART.equals(currentProductPart.getType())) {
                    isCOPSetProdCheckingStart = false;
                    iCOPSetProductIndex = 0;
                }
                substitutes = getSubstitutes((Map<?, ?>) obj, currentProductPart);
                if (!substitutes.isEmpty()) {
                    currentProductPart.setSubstituteExist(true);
                    currentProductPart.setSubstitutes(substitutes);
                }
                if (level == 1) {
                    tempParentStorageList.clear();
                    tempParentStorageList.add(productPart);
                    currentProductPart.setParent(productPart);
                    currentProductPart.setParentExist(Boolean.TRUE);
                    productPart.addChild(currentProductPart);
                    productPart.setChildExist(Boolean.TRUE);
                } else {
                    parentProductPart = tempParentStorageList.get(level - 1);
                    currentProductPart.setParent(parentProductPart);
                    currentProductPart.setParentExist(Boolean.TRUE);
                    parentProductPart.addChild(currentProductPart);
                    parentProductPart.setChildExist(Boolean.TRUE);
                }
                tempParentStorageList.add(level, currentProductPart);
            }
            tempParentStorageList.clear();
            return productPart;
        }

        public String getMOSCircularParentType() {
            return resource.getRollupPageProperties().getProperty("MOS_Circular_Parent_Types");
        }
    }

}
