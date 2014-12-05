package org.grails.plugin.wechat.util

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by hhxiao on 9/29/14.
 */
class SignatureHelper {
    private static Log log = LogFactory.getLog(SignatureHelper.class)

    /**
     * 验证签名
     *
     * @param token
     * @param signature
     * @param timestamp
     * @param nonce
     * @return 是否验证成功
     */
    public static boolean checkSignature(String token, String signature, String timestamp, String nonce) {
        // 将token、timestamp、nonce三个参数进行字典排序
        List<String> arr = [token, timestamp, nonce]
        Collections.sort(arr)

        String tmpStr = null
        try {
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            MessageDigest md = MessageDigest.getInstance("SHA-1")
            byte[] digest = md.digest(arr.join('').getBytes())
            tmpStr = byteToStr(digest)
        } catch (NoSuchAlgorithmException e) {
            log.error(e.message, e)
        }
        // 将sha1加密后的字符串可与signature对比
        return signature.equalsIgnoreCase(tmpStr)
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        StringBuilder strDigest = new StringBuilder()
        for (int i = 0; i < byteArray.length; i++) {
            strDigest.append(byteToHexStr(byteArray[i]))
        }
        return strDigest.toString()
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] tempArr = new char[2]
        tempArr[0] = DIGITS.charAt((mByte >>> 4) & 0X0F)
        tempArr[1] = DIGITS.charAt(mByte & 0X0F)
        return new String(tempArr)
    }

    private static final String DIGITS = '0123456789ABCDEF';
}
