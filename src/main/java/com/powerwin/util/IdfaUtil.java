package com.powerwin.util;

/**
 * @title IDFA转换工具类
 * @author wanggang
 * @date 2016年6月16日 下午3:48:39
 * @email wanggang@vfou.com
 * @descripe 将IDFA转换成小写和大写格式
 */
public class IdfaUtil {

    /**
     * IDFA小写转换成大写
     * @param idfa
     * @return
     */
    public static String toUpper(String idfa) {
        String udid = idfa.toUpperCase();
        if (udid.contains("-")) {
            return udid;
        } else {
            udid = udid.substring(0, 8) + "-" + udid.substring(8, 12) + "-" + udid.substring(12, 16) + "-"
                    + udid.substring(16, 20) + "-" + udid.substring(20);
            return udid;
        }
    }

    /**
     * IDFA大写转换成小写
     * @param idfa
     * @return
     */
    public static String toLower(String idfa) {
        return idfa.toLowerCase().replace("-", "");
    }

}
