package com.myorg.common.utils;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;


public class UtilityFunctions {

    private static final Logger log = Logger.getLogger(UtilityFunctions.class);

    private UtilityFunctions(){
    }

    /**
     * check map is null or empty
     */
    public static boolean isNullOrEmpty( final Map< ?, ? > m ) {
        return m == null || m.isEmpty();
    }

    /**
     * check collection object is null or empty
     */
    public static boolean isNullOrEmpty( final Collection< ? > c ) {
        return c == null || c.isEmpty();
    }

    /**
     * check object is null or empty
     */
    public static boolean isNullOrEmpty( final Object obj ) {
        return obj == null || "".equals(obj);
    }

    /**
     * check object is not null and not empty
     */
    public static boolean isNotNullAndNotEmpty( final Object obj ) {
        return obj != null && !"".equals(obj);
    }

    /**
     * check collection object is not null and empty
     */
    public static boolean isNotNullAndNotEmpty( final Collection< ? > c ) {
        return c != null && !c.isEmpty();
    }
}
