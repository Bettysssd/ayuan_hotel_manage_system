package com.comfort.hotel.utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @Author: ayuan
 * @PS:
 * @Date: 2024/8/29 18:23
 */


public class StringTools {

    // 密码加密
    public static String encodeByMD5(String originString) {
        return StringTools.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    // 是否为空
    public static boolean isEmpty(String str) {

        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    // 获取文件后缀
    public static String getFileSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        String suffix = fileName.substring(index);
        return suffix;
    }

    // 只获取文件名，但不包括后缀
    public static String getFileNameNoSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

    // 随机生成字符串
    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    // 随机生成数字
    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

}
