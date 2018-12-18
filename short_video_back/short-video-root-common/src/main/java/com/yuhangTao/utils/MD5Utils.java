package com.yuhangTao.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class MD5Utils {

    /**
     * @Description: 对字符串进行md5加密
     */
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5=MessageDigest.getInstance("MD5");
        String newstr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
        return newstr;
    }

}
