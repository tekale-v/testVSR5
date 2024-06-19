/*
 **   PhysChemContext.java
 **   Description - Introduced as part of update Physical Chemical Properties (physChem) - 18x.5.
 **   About - Hold required resources in a thread.
 **
 */
package com.pg.dsm.upload.fop.phys_chem.models;

import matrix.db.Context;

public class PhysChemContext {
    private static final ThreadLocal<Context> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<String> timeStampThreadLocal = new ThreadLocal<>();

    /**
     * Constructor
     *
     * @since DSM 2018x.5
     */
    private PhysChemContext() {
    }

    /**
     * Method to get context from local thread
     *
     * @return Context
     * @since DSM 2018x.5
     */
    public static Context getContext() {
        return contextThreadLocal.get();
    }

    /**
     * Method to set context in local thread
     *
     * @param context - Context
     * @since DSM 2018x.5
     */
    public static void setContext(Context context) {
        contextThreadLocal.set(context);
    }

    /**
     * Method to remove context in local thread
     *
     * @since DSM 2018x.5
     */
    public static void removeContext() {
        contextThreadLocal.remove();
    }

    /**
     * Method to check if context is set in local thread
     *
     * @since DSM 2018x.5
     */
    public static boolean isContextSet() {
        boolean isSet = false;
        if (contextThreadLocal.get() != null) {
            isSet = true;
        }
        return isSet;
    }

    /**
     * Method to get timestamp from local thread
     *
     * @return String
     * @since DSM 2018x.5
     */
    public static String getTimeStamp() {
        return timeStampThreadLocal.get();
    }

    /**
     * Method to set timestamp in local thread
     *
     * @param timeStamp - String
     * @since DSM 2018x.5
     */
    public static void setTimeStamp(String timeStamp) {
        timeStampThreadLocal.set(timeStamp);
    }

    /**
     * Method to remove timestamp in local thread
     *
     * @since DSM 2018x.5
     */
    public static void removeTimeStamp() {
        timeStampThreadLocal.remove();
    }

    /**
     * Method to check if timestamp is set in local thread
     *
     * @since DSM 2018x.5
     */
    public static boolean isTimeStampSet() {
        boolean isSet = false;
        if (timeStampThreadLocal.get() != null) {
            isSet = true;
        }
        return isSet;
    }

    /**
     * Method to remove the context and time stamp
     *
     * @param isContextSet   - boolean
     * @param isTimeStampSet - boolean
     * @since DSM 2018x.5
     */
    public static void endContext(boolean isContextSet, boolean isTimeStampSet) {
        if (isContextSet) {
            removeContext();
        }
        if (isTimeStampSet) {
            removeTimeStamp();
        }
    }
}
