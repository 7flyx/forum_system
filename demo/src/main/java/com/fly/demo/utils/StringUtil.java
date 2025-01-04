package com.fly.demo.utils;

public class StringUtil {
    /**
     * 判断字符串是否为空
     * @param value null 或者空串
     * @return true or false
     */
    public static boolean iSEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
