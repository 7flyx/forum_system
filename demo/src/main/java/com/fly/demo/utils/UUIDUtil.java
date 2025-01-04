package com.fly.demo.utils;

import java.util.UUID;

/**
 * 用UUID 生成 salt盐，表示 扰动字符串
 */
public class UUIDUtil {
    // 返回36位的随机字符串，其中有4个 -
    public static String UUID_36() {
        return UUID.randomUUID().toString();
    }

    /**
     * 32位的UUID，就是36位UUID，去除 -
     */
    public static String UUID_32() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
