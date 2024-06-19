package com.pg.dsm.sapview.alternate;

import matrix.db.Context;

public class CATIAAlternatePartContext {
    private static final ThreadLocal<Context> contextThreadLocal = new ThreadLocal<>();

    public CATIAAlternatePartContext() {
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
}
