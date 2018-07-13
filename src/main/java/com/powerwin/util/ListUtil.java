package com.powerwin.util;

import java.util.List;

/**
 * 取出集合中角标位置上的value
 */
public class ListUtil {



    /**
     * 取出集合中角标位置上的value
     * @param vals
     * @param idx
     * @return
     */
    public static int getInt(List<?> vals, int idx) {
        Object obj = vals.get(idx);
        if(obj == null || obj.toString().length() == 0){

            return 0;
        }

        return Integer.parseInt(obj.toString());
    }



    /**
     * 取出集合中角标位置上的value
     * @param vals
     * @param idx
     * @return
     */
    public static long getLong(List<?> vals, int idx) {
        Object obj = vals.get(idx);
        if(obj == null || obj.toString().length() == 0) {

            return 0;
        }

        return Long.parseLong(obj.toString());
    }

    /**
     * 取出集合中角标位置上的value
     * @param vals
     * @param idx
     * @return
     */
    public static String getString(List<?> vals, int idx) {
        Object obj = vals.get(idx);
        if(obj == null) {

            return "";
        }

        return (String)obj;
    }

    /**
     * 取出集合中角标位置上的value
     * @param vals
     * @param idx
     * @return
     */
    public static float getFloat(List<Object> vals, int idx) {
        Object obj = vals.get(idx);
        if(obj == null) {

            return 0.0f;
        }

        return (Float)obj;
    }
}
