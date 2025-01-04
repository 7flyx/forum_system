package com.fly.demo.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密的工具类
 */
public class MD5Util {
    /**
     * // 对单个字符串进行加密
     * @param str 明文
     * @return 密文
     */
    public static String md5(String str) {
        // 对str，通过导入的jar包 进行MD5加密
        return DigestUtils.md2Hex(str);
    }


    /**
     * 1、原密码
     * 2、生成扰动字符串
     * 3、原密码进行MD5加密 = 密文1
     * 4、密文1 + 扰动字符串 = 密文2
     * 5、对 密文2进行MD5加密
     * @param str 原密码
     * @param salt 扰动字符串，也就是盐
     * @return 密文
     */
    public static String md5Salt(String str, String salt) {
        return md5((md5(str) + salt));
    }
}
