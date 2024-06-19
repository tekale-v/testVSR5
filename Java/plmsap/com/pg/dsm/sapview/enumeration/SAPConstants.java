package com.pg.dsm.sapview.enumeration;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.FrameworkException;
import com.matrixone.apps.domain.util.FrameworkUtil;
import com.pg.v3.custom.pgV3Constants;
import matrix.db.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SAPConstants {
    private static final Logger logger = Logger.getLogger(SAPConstants.class.getName());

    public enum IntermediateParts {
        TYPE_PGCUSTOMERUNITPART {
            @Override
            public boolean isComplexBOM(Map<Object, Object> objectMap) {
                boolean isComplex = Boolean.FALSE;
                // get component info starts
                boolean hasComponent = (objectMap.containsKey(SAPViewConstant.KEY_HAS_COMPONENT.getValue())) ? (Boolean) objectMap.get(SAPViewConstant.KEY_HAS_COMPONENT.getValue()) : Boolean.FALSE;
                List<String> componentTypes = new ArrayList<>();
                if (hasComponent) { // if has component. initialize components types list.
                    componentTypes = (objectMap.containsKey(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue())) ? (List<String>) objectMap.get(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue()) : componentTypes;
                    if (componentTypes.isEmpty()) {
                        logger.log(Level.WARNING, "Error - (CUP) Component Type List is empty");
                    }
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
                } // get component info ends
                return isComplex;
            }
        },
        TYPE_PGINNERPACKUNITPART {
            @Override
            public boolean isComplexBOM(Map<Object, Object> objectMap) {
                boolean isComplex = Boolean.FALSE;
                // get component info starts
                boolean hasComponent = (objectMap.containsKey(SAPViewConstant.KEY_HAS_COMPONENT.getValue())) ? (Boolean) objectMap.get(SAPViewConstant.KEY_HAS_COMPONENT.getValue()) : Boolean.FALSE;
                List<String> componentTypes = new ArrayList<>();
                if (hasComponent) { // if has component. initialize components types list.
                    componentTypes = (objectMap.containsKey(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue())) ? (List<String>) objectMap.get(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue()) : componentTypes;
                    if (componentTypes.isEmpty()) {
                        logger.log(Level.WARNING, "Error - (IP) Component Type List is empty");
                    }
                    if (componentTypes.contains(pgV3Constants.TYPE_PGINNERPACKUNITPART)) { // if IP has atleast one IP
                        isComplex = Boolean.TRUE;
                    } else if (componentTypes.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)) { // if IP has more than one COP
                        if (componentTypes.stream().filter(t -> t.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)).count() > 1) {
                            isComplex = Boolean.TRUE;
                        }
                    }
                }
                return isComplex;
            }
        },
        TYPE_PGCONSUMERUNITPART {
            @Override
            public boolean isComplexBOM(Map<Object, Object> objectMap) {
                boolean isComplex = Boolean.FALSE;
                // get component info starts
                boolean hasComponent = (objectMap.containsKey(SAPViewConstant.KEY_HAS_COMPONENT.getValue())) ? (Boolean) objectMap.get(SAPViewConstant.KEY_HAS_COMPONENT.getValue()) : Boolean.FALSE;
                List<String> componentTypes = new ArrayList<>();
                if (hasComponent) { // if has component. initialize components types list.
                    componentTypes = (objectMap.containsKey(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue())) ? (List<String>) objectMap.get(SAPViewConstant.KEY_COMPONENT_TYPES_LIST.getValue()) : componentTypes;
                    if (componentTypes.isEmpty()) {
                        logger.log(Level.WARNING, "Error - (COP) Component Type List is empty");
                    }
                    if (componentTypes.contains(pgV3Constants.TYPE_PGCONSUMERUNITPART)) { // if COP has more than one COP
                        if (componentTypes.stream().filter(t -> t.equalsIgnoreCase(pgV3Constants.TYPE_PGCONSUMERUNITPART)).count() > 1) {
                            isComplex = Boolean.TRUE;
                        }
                    }
                }
                return isComplex;
            }
        };

        /**
         * @param objectMap
         * @return
         */
        public abstract boolean isComplexBOM(Map<Object, Object> objectMap);

        /**
         * @param context
         * @param objectMap
         * @return
         * @throws com.matrixone.apps.domain.util.FrameworkException
         */
        public static boolean isComplexBOM(Context context, Map<Object, Object> objectMap) throws FrameworkException {
            boolean isComplex = false;
            String type = (String) (objectMap).get(DomainConstants.SELECT_TYPE);
            String typeSymbolicName = FrameworkUtil.getAliasForAdmin(context, DomainConstants.SELECT_TYPE, type, false);
            IntermediateParts intermediateParts = IntermediateParts.valueOf(typeSymbolicName.toUpperCase());
            if (null != intermediateParts) {
                isComplex = intermediateParts.isComplexBOM(objectMap);
            }
            return isComplex;
        }
    }
}
