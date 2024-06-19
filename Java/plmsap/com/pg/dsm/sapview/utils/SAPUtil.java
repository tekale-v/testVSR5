package com.pg.dsm.sapview.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.DomainObject;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.MapList;
import com.pg.v3.custom.pgV3Constants;

import matrix.db.Context;
import matrix.util.StringList;

public class SAPUtil {
    
    /**
     * @param type
     * @param hasComponent
     * @param componentTypes
     * @return
     */
    private boolean whenTypeIsConsumerUnitPart(String type, boolean hasComponent, List<String> componentTypes) {
        boolean isComplex = Boolean.FALSE;
        if (type.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)) { // case when type is: COP
            if (hasComponent) {
                if (componentTypes.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)) { // if COP has more than one COP
                    if (componentTypes.stream().filter(t -> t.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)).count() > 1) {
                        isComplex = Boolean.TRUE;
                    }
                }
            }
        }
        return isComplex;
    }

    /**
     * @param type
     * @param hasComponent
     * @param componentTypes
     * @return
     */
    private boolean whenTypeIsInnerUnitPart(String type, boolean hasComponent, List<String> componentTypes) {
        boolean isComplex = Boolean.FALSE;
        if (type.equalsIgnoreCase(pgV3Constants.TYPE_PGINNERPACKUNITPART)) { // case when type is: IP
            if (hasComponent) {
                if (componentTypes.contains(pgV3Constants.TYPE_PGINNERPACKUNITPART)) { // if IP has atleast one IP
                    isComplex = Boolean.TRUE;
                } else if (componentTypes.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)) { // if IP has more than one COP
                    if (componentTypes.stream().filter(t -> t.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)).count() > 1) {
                        isComplex = Boolean.TRUE;
                    }
                }
            }
        }
        return isComplex;
    }

    /**
     * @param type
     * @param hasComponent
     * @param componentTypes
     * @return
     */
    public boolean whenTypeIsCustomerUnitPart(String type, boolean hasComponent, List<String> componentTypes) {
        boolean isComplex = Boolean.FALSE;
        if (type.equalsIgnoreCase(pgV3Constants.TYPE_PGCUSTOMERUNITPART)) { // case when type is: CUP
            if (hasComponent) {
                if (componentTypes.contains(pgV3Constants.TYPE_PGCUSTOMERUNITPART)) { // if CUP has atleast one CUP
                    isComplex = Boolean.TRUE;
                } else if (componentTypes.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)) { // if CUP has more than one COP
                    if (componentTypes.stream().filter(t -> t.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)).count() > 1) {
                        isComplex = Boolean.TRUE;
                    }
                } else if (componentTypes.contains(pgV3Constants.TYPE_PGINNERPACKUNITPART)) { // if CUP has more than one IP
                    if (componentTypes.stream().filter(t -> t.equalsIgnoreCase(pgV3Constants.TYPE_PGINNERPACKUNITPART)).count() > 1) {
                        isComplex = Boolean.TRUE;
                    }
                }
            }
        }
        return isComplex;
    }
    /**
     * @param context
     * @param objectOid
     * @return
     * @throws com.matrixone.apps.domain.util.FrameworkException
     */
    public MapList getFirstLevelIntermediates(Context context, String objectOid) throws FrameworkException {

        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        final String type = domainObject.getInfo(context, DomainConstants.SELECT_TYPE);
        StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append(pgV3Constants.TYPE_PGCONSUMERUNITPART);
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGCUSTOMERUNITPART); // test it...
        typeBuilder.append(pgV3Constants.SYMBOL_COMMA);
        typeBuilder.append(pgV3Constants.TYPE_PGINNERPACKUNITPART);

        String types = typeBuilder.toString();

        StringList objectSelects = new StringList();
        objectSelects.addElement(DomainConstants.SELECT_ID);
        objectSelects.addElement(DomainConstants.SELECT_TYPE);
        objectSelects.addElement(DomainConstants.SELECT_NAME);
        objectSelects.addElement(DomainConstants.SELECT_REVISION);
        objectSelects.addElement(DomainConstants.SELECT_LEVEL);
        objectSelects.addElement(DomainConstants.SELECT_CURRENT);

        String objectWhere = DomainObject.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
        return domainObject.getRelatedObjects(
                context,                             // Context
                DomainConstants.RELATIONSHIP_EBOM,   // String
                types,                               // String
                false,                            // boolean
                true,                            // boolean
                1,                                // int
                objectSelects,                       // StringList
                DomainConstants.EMPTY_STRINGLIST,    // StringList
                objectWhere,                            // String
                DomainConstants.EMPTY_STRING,        // String
                0,                                // int
                DomainConstants.EMPTY_STRING,        // String
                types,                               // String post type pattern
                null);// Map
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    public MapList getFirstLevelCustomerUnitPart(Context context, String objectOid) throws FrameworkException {

        DomainObject domainObject = DomainObject.newInstance(context, objectOid);
        final String type = domainObject.getInfo(context, DomainConstants.SELECT_TYPE);
        StringList objectSelects = new StringList();
        objectSelects.addElement(DomainConstants.SELECT_ID);
        objectSelects.addElement(DomainConstants.SELECT_TYPE);
        objectSelects.addElement(DomainConstants.SELECT_NAME);
        objectSelects.addElement(DomainConstants.SELECT_REVISION);
        objectSelects.addElement(DomainConstants.SELECT_LEVEL);
        objectSelects.addElement(DomainConstants.SELECT_CURRENT);

        String objectWhere = DomainObject.SELECT_CURRENT + " != " + DomainConstants.STATE_PART_OBSOLETE;
        return domainObject.getRelatedObjects(
                context,                             // Context
                DomainConstants.RELATIONSHIP_EBOM,   // String
                pgV3Constants.TYPE_PGCUSTOMERUNITPART, // String
                false,                            // boolean
                true,                            // boolean
                1,                                // int
                objectSelects,                       // StringList
                DomainConstants.EMPTY_STRINGLIST,    // StringList
                objectWhere,                            // String
                DomainConstants.EMPTY_STRING,        // String
                0,                                // int
                DomainConstants.EMPTY_STRING,        // String
                pgV3Constants.TYPE_PGCUSTOMERUNITPART, // String post type pattern
                null);// Map
    }

    /**
     * @param context
     * @param objectId
     * @return
     * @throws FrameworkException
     */
    public MapList getCustomerUnitFirstLevelIntermediates(Context context, String objectId) throws FrameworkException {
        return getFirstLevelIntermediates(context, objectId);
    }

    /**
     * @param context
     * @param objectOid
     * @return
     * @throws FrameworkException
     */
    public List<String> getFirstLevelIntermediateTypes(Context context, String objectOid) throws FrameworkException {
        List<String> typeList = new ArrayList<>();
        MapList objectList = getFirstLevelIntermediates(context, objectOid);
        if (null != objectList && !objectList.isEmpty()) {
            typeList = (List<String>) objectList.stream().map(map -> ((String) ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE))).collect(Collectors.toList());
        }
        return typeList;
    }

    /**
     * @param objectList
     * @return
     * @throws FrameworkException
     */
    public List<String> getFirstLevelIntermediateTypes(MapList objectList) throws FrameworkException {
        List<String> typeList = new ArrayList<>();
        if (null != objectList && !objectList.isEmpty()) {
            typeList = (List<String>) objectList.stream().map(map -> ((String) ((Map<Object, Object>) map).get(DomainConstants.SELECT_TYPE))).collect(Collectors.toList());
        }
        return typeList;
    }
    /**
     * @return
     */
    public static StringList getBusSelects() {
        StringList objectSelects = new StringList(16);
        objectSelects.add(DomainConstants.SELECT_TYPE);
        objectSelects.add(DomainConstants.SELECT_NAME);
        objectSelects.add(DomainConstants.SELECT_REVISION);
        objectSelects.add(DomainConstants.SELECT_ID);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGSAPTYPE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCSSTYPE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_TITLE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_STATUS);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_RELEASE_PHASE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBASEUNITOFMEASURE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGBOMBASEQUANTITY);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGENTRYBASEQUANTITY);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGASSEMBLYTYPE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGFINISHEDPRODUCTCODE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_EFFECTIVITYDATE);
        objectSelects.add(pgV3Constants.SELECT_ALTERNATE_ID);
        objectSelects.add(pgV3Constants.SELECT_ALTERNATE_TYPE);
        objectSelects.add(pgV3Constants.SELECT_ALTERNATE_NAME);
        objectSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]");
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGORIGINATINGSOURCE);
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_ENGINUITYAUTHORED);
        objectSelects.add("from[" + pgV3Constants.RELATIONSHIP_AUTHORIZEDTEMPORARYSPECIFICATION + "].to.name");
        objectSelects.add("from[" + pgV3Constants.RELATIONSHIP_PGMASTER + "].to.name");
        objectSelects.addElement(DomainConstants.SELECT_LEVEL);
        //Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Starts
        objectSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGEXPANDBOMONSAPBOMASFED);
        //Added by DSM(Sogeti) - for 2018x.2 FAB requirements #25921, #25922,#27206 - Ends
        return objectSelects;
    }

    /**
     * @return
     */
    public static StringList getRelSelects() {
        StringList relSelects = new StringList(33);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMAXQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGCALCQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_FINDNUMBER);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGPOSITIONINDICATOR);
        relSelects.add(DomainConstants.SELECT_RELATIONSHIP_ID);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REL_ID);
        relSelects.add("frommid[" + pgV3Constants.RELATIONSHIP_EBOMSUBSTITUTE + "]");
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_ID);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_TYPE);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_NAME);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_REVISION);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_CURRENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PG_BOM_BASEQUANTITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGSAPTYPE);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_EFFECTIVITY_DATE);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_PGASSEMBLYTYPE);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_REFERENCEDESIGNATOR);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_OPTIONAL_COMPONENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_OPT_COMPONENT);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMINCALCQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_PGMIXCALCQUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_COMMENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_COMMENT);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MIN_QUANTITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_MAX_QUANTITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_QUANTITY);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_MINACTUAL_PERCENTWET);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_MAXACTUALPERCENTWET);
        relSelects.add(pgV3Constants.SELECT_ATTRIBUTE_QUANTITY);
        //Added by DSM - for 2018x.1 requirement #25043 - Starts
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_START_EFFECTIVITY);
        relSelects.add(pgV3Constants.SELECT_EBOM_SUBSTITUTE_VALID_UNTIL_DATE);
        //Added by DSM - for 2018x.1 requirement #25043 - Ends
        return relSelects;
    }
}
